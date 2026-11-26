CREATE OR REPLACE PACKAGE BODY D2401B01
/*********************************************************************************************
 * システム名       ：ＡＰ総合管理システム
 * 機能名           ：ポイント月次発生処理
 *********************************************************************************************
   Date       Author         Changes Key                                 Version
   -----------------------------------------------------------------------------
   2025-09-30 ABI)Suzuki       Create                                      1.0.0
 *********************************************************************************************/
AS

  /*******************************************************************************************/
  /* 共通変数宣言                                                                            */
  /*******************************************************************************************/
  gApKihonCdFrom                 VARCHAR2(3);    -- AP基本コードFROM
  gApKihonCdTo                   VARCHAR2(3);    -- AP基本コードTO
  gShopCdFrom                    VARCHAR2(3);    -- ショップコードFROM
  gShopCdTo                      VARCHAR2(3);    -- ショップコードTO
  gJikkouYoteiDD                 VARCHAR2(8);    -- 実行予定日
  gUnyoDate                      VARCHAR2(8);    -- 運用日付（年月日）
  gIpAddress                     VARCHAR2(15);   -- ＩＰアドレス
  gCntFetch1                     NUMBER  :=  0;  -- 予定発生ポイント明細抽出件数
  gCntUpd1                       NUMBER  :=  0;  -- 予定発生ポイント明細更新件数

  gTestFlag                      NUMBER  :=  0;  -- テスト用ログ出力フラグ(※本稼働時は0テスト時は1を設定)
--  gTestFlag                      NUMBER  :=  1;  -- テスト用ログ出力フラグ(※本稼働時は0テスト時は1を設定)

  /*******************************************************************************************/
  /* 共通定数宣言                                                                            */
  /*******************************************************************************************/
  c_PGMID             VARCHAR2(8)   :=  'D2401B01';
  c_APKIHONCD         VARCHAR2(3)   :=  '001';
  c_SHOPCD            VARCHAR2(3)   :=  '000';
  c_TNSHACD           VARCHAR2(6)   :=  'NITIJI';

  /*******************************************************************************************/
  /* 共通カーソル宣言                                                                            */
  /*******************************************************************************************/
  -- 予定発生ポイント明細取得カーソル（得意先マスタ・請求先マスタ）
  CURSOR cur_YoteiHasseiPointMeisaiList(pApKihonCdFrom VARCHAR2, pApKihonCdTo VARCHAR2, pShopCdFrom VARCHAR2, pShopCdTo VARCHAR2) IS
    SELECT S20T002T.*,
           S20T001T.TOKUISAKICD
    FROM  S20T001T
    INNER JOIN S20T002T
    ON (
      S20T001T.APKIHONCD = S20T002T.APKIHONCD
      AND S20T001T.SHOPCD = S20T002T.SHOPCD
      AND S20T001T.KANRINO = S20T002T.KANRINO
    )
    WHERE S20T002T.APKIHONCD BETWEEN pApKihonCdFrom AND pApKihonCdTo
      AND S20T002T.SHOPCD BETWEEN pShopCdFrom AND pShopCdTo
      AND S20T002T.POINTHASSEIDD IS NULL
      AND S20T002T.POINTMYAQUARENKEIDD IS NULL
      -- ACCIS2023-7566
      -- 発生対象とするポイントとして画面から付与された予定ポイントを含めるように条件を追加
      -- ※画面から付与された予定ポイントはポイント種別が31かつ売上伝票と紐づいていない（伝票番号がNULL）
      AND (
        (
            S20T002T.POINTKIND = '31'
            AND S20T001T.URIDNPYNO IS NULL
        )
        OR ( 
          S20T002T.POINTKIND IN ('1','2','10','20','23','24','25','31','90')
          AND S20T001T.URIDNPYNO IN (
            SELECT S06T001T.URIDNPYNO
            FROM S06T001T
            INNER JOIN S07T001T
            ON (
              S07T001T.APKIHONCD = S06T001T.APKIHONCD
              AND S07T001T.SHOPCD = S06T001T.SHOPCD
              AND S07T001T.SKYSAKICD = S06T001T.SKYSAKICD
              AND S07T001T.TOKUISAKICD = S06T001T.TOKUISAKICD
              AND S07T001T.SHIMEDYDM = S06T001T.SKYSHIMEDYDM
              AND S07T001T.SHIMEDD = S06T001T.SKYSHIMEDD
            )
            WHERE S06T001T.APKIHONCD = S20T002T.APKIHONCD
              AND S06T001T.SHOPCD = S20T002T.SHOPCD
              AND S07T001T.KESHIKOMIZD = '0'
              AND S06T001T.YUKOKBN = '1'
              AND S07T001T.YUKOKBN = '1'
            )
        )
      );

  -- ポイント即時発生リスト取得用カーソル
  CURSOR cur_PointSokujiHasseiList(pApKihonCdFrom VARCHAR2, pApKihonCdTo VARCHAR2, pShopCdFrom VARCHAR2, pShopCdTo VARCHAR2) IS
    SELECT S20T002T.*,
           S20T001T.TOKUISAKICD
    FROM S20T002T
    INNER JOIN S20T001T
    ON (
      S20T001T.APKIHONCD = S20T002T.APKIHONCD
      AND S20T001T.SHOPCD = S20T002T.SHOPCD
      AND S20T001T.KANRINO = S20T002T.KANRINO
    )
    WHERE S20T002T.APKIHONCD BETWEEN pApKihonCdFrom AND pApKihonCdTo
      AND S20T002T.SHOPCD BETWEEN pShopCdFrom AND pShopCdTo
      AND S20T002T.POINTHASSEIDD IS NULL
      AND S20T002T.POINTMYAQUARENKEIDD IS NULL
      AND S20T002T.POINTKIND IN ('21','22','30');

  /****************************************************************************************************/
  /* プロシージャ   ：ログ出力                                                                        */
  /* 引数           ：iMessage         メッセージ                                                     */
  /****************************************************************************************************/
  PROCEDURE OutputLog(iMessage IN S99CHKLOG.MSG%TYPE
  ) IS

  BEGIN

    S99_CHECK_LOG(
      c_PGMID
      , '001'
      , '000'
      , '2'
      , 'M'
      , '0'
      , ''
      , '0'
      , '001'
      , '000'
      , 'NITIJI'
      , gIpAddress
      , c_PGMID
      , iMessage  -- ヘッダ分を差し引いて
    );

  END OutputLog;
  /****************************************************************************************************/
  /* プロシージャ   ：ポイント発生日付更新処理                                                        */
  /* 引数           ：PointHasseiDdUpdadte  ポイント利用有無                                          */
  /*                ：iApKihoncd            AP基本コード                                              */
  /*                ：iShopcd               ショップコード                                            */
  /*                ：iKanriNo              管理番号                                                  */
  /*                ：iRenban               連番                                                      */
  /*                ：iSysDate              システム日付                                              */
  /*                ：iSysTime              システム時間                                              */
  /****************************************************************************************************/
  PROCEDURE PointHasseiDdUpdadte(iApKihoncd IN S20T002T.APKIHONCD%TYPE
                                ,iShopcd IN S20T002T.SHOPCD%TYPE
                                ,iKanriNo IN S20T002T.KANRINO%TYPE
                                ,iRenban IN S20T002T.RENBAN%TYPE
                                ,iSysDate IN S20T002T.KSHNSYSTMYMD%TYPE
                                ,iSysTime IN S20T002T.KSHNSYSTMJIKAN%TYPE
  ) IS

  BEGIN

    -- ポイント発生日付更新処理
    UPDATE S20T002T
    SET POINTHASSEIDD = gUnyoDate
      ,YUKOKBN = '1'
      ,KOSHAAPKIHONCD = c_APKIHONCD
      ,KOSHASHOPCD = c_SHOPCD
      ,KOSHACD = c_TNSHACD
      ,KSHNSYSTMYMD = iSysDate
      ,KSHNSYSTMJIKAN = iSysTime
      ,KSHNUNYOYMD = gUnyoDate
      ,KSHNIPADRSE = gIpAddress
      ,KSHNPGMID = c_PGMID
    WHERE S20T002T.APKIHONCD = iApKihoncd
      AND S20T002T.SHOPCD = iShopcd
      AND S20T002T.KANRINO = iKanriNo
      AND S20T002T.RENBAN = iRenban;

    gCntUpd1 := gCntUpd1 +1;

  END PointHasseiDdUpdadte;
  /*******************************************************************************************/
  /* プロシージャ名   ：ポイント月次発生処理                                                 */
  /* 引数             ：なし                                                                 */
  /*******************************************************************************************/
  PROCEDURE PointGetujiHasseishori(iApKihonCd IN S20T002T.APKIHONCD%type
                                  ,iShopCd    IN S20T002T.SHOPCD%type
  ) IS

  -- 変数
  sSysDate                          S02M013T.KSHNSYSTMYMD%type;                   -- システム日付
  sSysTime                          S02M013T.KSHNSYSTMJIKAN%type;                 -- システム時間
  sPointKanriUmu                    S02M203T.POINTKANRIUMU%type := 0;             -- ポイント管理有無
  sMyaquaPointRiyokyodaku           S02M203T.MYAQUAPOINTRIYOKYODAKU%type := 0;    -- ﾏｲｱｸｱﾎﾟｲﾝﾄ利用有無

BEGIN

  -- DBサーバーのIPアドレス取得
  gIpAddress := S01GetIpAddress();

  -- 処理開始ログ出力
  S99_CHECK_LOG(c_PGMID, c_APKIHONCD, c_SHOPCD, '2', 'S', '0', NULL, 0, c_APKIHONCD, c_SHOPCD, c_TNSHACD, gIpAddress, c_PGMID, NULL);

  -- システム日時を取得
  SELECT  to_char(CurRENT_DATE, 'yyyymmdd')
        , to_char(CurRENT_DATE, 'hh24miss')
    INTO  sSysDate
        , sSysTime
    FROM dual;

  -- 運用日付取得
    gUnyoDate := S01GetOperationdate(c_APKIHONCD, c_SHOPCD);

  -- カーソル条件範囲設定
  IF iApKihonCd IS NOT NULL THEN
    gApKihonCdFrom := iApKihonCd;
    gApKihonCdTo   := iApKihonCd;
  ELSE
    gApKihonCdFrom := '000';
    gApKihonCdTo   := '999';
  END IF;
  IF iShopCd IS NOT NULL THEN
    gShopCdFrom := iShopCd;
    gShopCdTo   := iShopCd;
  ELSE
    gShopCdFrom := '000';
    gShopCdTo   := '999';
  END IF;

---------- テスト用 STERT ----------
/*  IF gTestFlag = '1' THEN
    gApKihonCdFrom := '007';
    gApKihonCdTo   := '007';

    gShopCdFrom := '001';
    gShopCdTo   := '001';
  END IF;
*/
----------- テスト用 END -----------

  --実施日取得
  SELECT T011.TRGTDYDM || T011.TRGTDD
  INTO gJikkouYoteiDD
  FROM S18T011T T011
  WHERE T011.SETTEIKBN = '2';

--実行判定処理
  IF gJikkouYoteiDD = gUnyoDate THEN
--予定発生ポイント明細取得処理
    FOR Cur IN cur_YoteiHasseiPointMeisaiList(gApKihonCdFrom, gApKihonCdTo, gShopCdFrom, gShopCdTo) LOOP

      -- 抽出件数カウント
      gCntFetch1 := gCntFetch1 + 1;

      BEGIN

        SELECT MYAQUAPOINTRIYOKYODAKU
             ,S02M203T.POINTKANRIUMU
        INTO sMyaquaPointRiyokyodaku
             ,sPointKanriUmu
        FROM S02M203T
        WHERE S02M203T.APKIHONCD = Cur.APKIHONCD
          AND S02M203T.SHOPCD = Cur.SHOPCD
          AND S02M203T.TOKUISAKICD = Cur.TOKUISAKICD;

      EXCEPTION
      WHEN NO_DATA_FOUND THEN
        OutputLog('マイアクアポイント利用許諾及び、ポイント管理有無の取得でエラーが発生しました。 AP基本コード : ' ||
        Cur.APKIHONCD || ' ショップコード : ' || Cur.SHOPCD || ' 得意先コード : ' || Cur.TOKUISAKICD);
        RAISE;
      END;

      --ポイント月次発生処理
      IF sMyaquaPointRiyokyodaku = '1' AND sPointKanriUmu = '1' THEN
        PointHasseiDdUpdadte(Cur.APKIHONCD, Cur.SHOPCD, Cur.KANRINO, Cur.RENBAN, Cur.KSHNSYSTMYMD, Cur.KSHNSYSTMJIKAN);
      END IF;

    END LOOP;

--ポイント即時発生リスト取得処理
    FOR Cur IN cur_PointSokujiHasseiList(gApKihonCdFrom, gApKihonCdTo, gShopCdFrom, gShopCdTo) LOOP
      gCntFetch1 := gCntFetch1 + 1;

      BEGIN

        SELECT MYAQUAPOINTRIYOKYODAKU
               ,S02M203T.POINTKANRIUMU
        INTO sMyaquaPointRiyokyodaku
             ,sPointKanriUmu
        FROM S02M203T
        WHERE S02M203T.APKIHONCD = Cur.APKIHONCD
          AND S02M203T.SHOPCD = Cur.SHOPCD
          AND S02M203T.TOKUISAKICD = Cur.TOKUISAKICD;

      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          OutputLog('マイアクアポイント利用許諾及び、ポイント管理有無の取得でエラーが発生しました。 AP基本コード : ' ||
          Cur.APKIHONCD || ' ショップコード : ' || Cur.SHOPCD || ' 得意先コード : ' || Cur.TOKUISAKICD);
          RAISE;
      END;

      --ポイント月次発生処理
      IF sMyaquaPointRiyokyodaku = '1' AND sPointKanriUmu = '1' THEN
        PointHasseiDdUpdadte(Cur.APKIHONCD, Cur.SHOPCD, Cur.KANRINO, Cur.RENBAN, Cur.KSHNSYSTMYMD, Cur.KSHNSYSTMJIKAN);
      END IF;

    END LOOP;

  ELSE
    OutputLog('実行日不一致');

  END IF;

  -- 処理終了ログ出力
  S99_CHECK_LOG(c_PGMID, c_APKIHONCD, c_SHOPCD, '2', 'E', '1', NULL, 0, c_APKIHONCD, c_SHOPCD, c_TNSHACD, gIpAddress, c_PGMID, NULL);
  S99_CHECK_LOG(c_PGMID, c_APKIHONCD, c_SHOPCD, '2', 'R', '1', 'S20T002T', gCntFetch1, c_APKIHONCD, c_SHOPCD, c_TNSHACD, gIpAddress, c_PGMID, NULL);
  S99_CHECK_LOG(c_PGMID, c_APKIHONCD, c_SHOPCD, '2', 'U', '1', 'S20T002T', gCntUpd1, c_APKIHONCD, c_SHOPCD, c_TNSHACD, gIpAddress, c_PGMID, NULL);

EXCEPTION
  WHEN OTHERS THEN
    -- 異常終了ログ出力
    S99_CHECK_LOG(c_PGMID, c_APKIHONCD, c_SHOPCD, '2', 'E', '9', NULL, 0, c_APKIHONCD, c_SHOPCD, c_TNSHACD, gIpAddress, c_PGMID, SUBSTRB(SQLERRM, 1, 512));
    S99_CHECK_LOG(c_PGMID, c_APKIHONCD, c_SHOPCD, '2', 'R', '9', 'S20T002T', gCntFetch1, c_APKIHONCD, c_SHOPCD, c_TNSHACD, gIpAddress, c_PGMID, NULL);
    S99_CHECK_LOG(c_PGMID, c_APKIHONCD, c_SHOPCD, '2', 'U', '9', 'S20T002T', gCntUpd1, c_APKIHONCD, c_SHOPCD, c_TNSHACD, gIpAddress, c_PGMID, NULL);
    -- 元トランザクションをロールバック
    ROLLBACK;
    RAISE;

  END  PointGetujiHasseishori;

END D2401B01;
/
EXIT;