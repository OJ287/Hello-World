CREATE OR REPLACE PACKAGE BODY        C1001B02
/*******************************************************************************
 * システム名       ：ＡＰ総合管理システム
 * 機能名           ：定例売上処理
 *
 *******************************************************************************
   Date       Author         Changes Key                                 Version
   -----------------------------------------------------------------------------
   2022-11-01 ABI)Nakamura       Create                                    1.0.0
 ******************************************************************************/
 AS

  /*****************************************************************************/
  /* プロシージャ名   ：売上伝票番号検索                                       */
  /*****************************************************************************/
  PROCEDURE GetUridnpyno IS
    wUridnoyno1         VARCHAR2(11); -- 定例売上伝票番号から
    wUridnoyno2         VARCHAR2(11); -- 定例売上伝票番号まで
  BEGIN
--    S01SAIBAN.getSlipNoTeirei
    Z1804B01.getSlipNoTeirei(vRecordS06T001T.Apkihoncd,
                              vRecordS06T001T.Shopcd,
                              1,
                              wUridnoyno1,
                              wUridnoyno2);
    vRecordS06T001T.URIDNPYNO := wUridnoyno1;
  END GetUridnpyno;

  /******************************************************************************/
  /* ファンクション名 ：運用日付検索                                            */
  /* 引数             ：iApKihoncd           AP基本コード                       */
  /*                  ：iShopcd              ショップコード                     */
  /* return           ：returnOperationdate  運用日付                           */
  /******************************************************************************/
  FUNCTION GetOperationdate(iApKihoncd IN VARCHAR2, iShopcd IN VARCHAR2)
    return VARCHAR2 is
    returnOperationdate VARCHAR2(8);
    --システム管理マスタから運用日付の検索
    CURSOR SELECT_OPERATIONDATE IS
      SELECT SYKM.MOJISETTEIATAI
        FROM S02M006T SYKM
       WHERE SYKM.APKIHONCD = iApKihoncd
         AND SYKM.SHOPCD = iShopcd
         AND SYKM.SETTEINO = '038'
         AND SYKM.RCRDKBN = 'M'
         AND SYKM.YUKOKBN = '1';
  BEGIN
    --システム管理マスタから運用日付の取得
    OPEN SELECT_OPERATIONDATE;
    LOOP
      FETCH SELECT_OPERATIONDATE
        INTO returnOperationdate;
      -- 運用日付チェック
      IF SELECT_OPERATIONDATE%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20010, '運用日付が設定されていません。');
      END IF;

      EXIT WHEN SELECT_OPERATIONDATE%NOTFOUND;

      IF SELECT_OPERATIONDATE%ROWCOUNT > 1 THEN
        RAISE_APPLICATION_ERROR(-20011, '運用日付が複数見つかりました。');
      END IF;
    END LOOP;
    CLOSE SELECT_OPERATIONDATE;

    return(returnOperationdate);
  END GetOperationdate;
  /******************************************************************************/
  /* ファンクション名 ：定例売上仕入備考制御検索                                */
  /* 引数             ：iApKihoncd           AP基本コード                       */
  /*                  ：iShopcd              ショップコード                     */
  /* return           ：returnSeigyoFlg      制御フラグ                         */
  /******************************************************************************/
  FUNCTION GetSeigyoFlg(iApKihoncd IN VARCHAR2, iShopcd IN VARCHAR2)
    return NUMBER is
    returnSeigyoFlg NUMBER(1);
    --システム管理マスタから定例売上仕入備考制御フラグの検索
    CURSOR SELECT_SEIGYOFLG IS
      SELECT SYKM.SUCHISETTEIATAI
        FROM S02M006T SYKM
       WHERE SYKM.APKIHONCD = iApKihoncd
         AND SYKM.SHOPCD = iShopcd
         AND SYKM.SETTEINO = '917'
         AND SYKM.RCRDKBN = 'M'
         AND SYKM.YUKOKBN = '1';
  BEGIN
    --システム管理マスタから運用日付の取得
    OPEN SELECT_SEIGYOFLG;
    LOOP
      FETCH SELECT_SEIGYOFLG
        INTO returnSeigyoFlg;
      -- 運用日付チェック
      IF SELECT_SEIGYOFLG%ROWCOUNT = 0 THEN
        returnSeigyoFlg := 0;
      END IF;

      EXIT WHEN SELECT_SEIGYOFLG%NOTFOUND;

      IF SELECT_SEIGYOFLG%ROWCOUNT > 1 THEN
        RAISE_APPLICATION_ERROR(-20011, '定例売上仕入備考制御フラグが複数見つかりました。');
      END IF;
    END LOOP;
    CLOSE SELECT_SEIGYOFLG;

    return(returnSeigyoFlg);
  END GetSeigyoFlg;

-- ■：1 Base)chokenkun 2016-09-02 NTT対応STEP２
  /******************************************************************************/
  /* ファンクション名 ：アクアクララ光商品分類チェック                          */
  /* 引数             ：iApKihoncd           大分類コード                       */
  /*                  ：iShopcd              中分類コード                       */
  /* return           ：returnIshikari       チェック結果                       */
  /******************************************************************************/
  FUNCTION IsHikari(iLctgrcd IN VARCHAR2, iMctgrcd IN VARCHAR2)
    return BOOLEAN is
    returnIshikari BOOLEAN;
    wHikariLctgrcd                    VARCHAR2(2); -- アクアクララ光の大分類コード
    wHikariMctgrcd                    VARCHAR2(3); -- アクアクララ光の中分類コード
  BEGIN
    --システム管理マスタからアクアクララ光の大分類コードの検索
    SELECT SYKM.MOJISETTEIATAI
      INTO wHikariLctgrcd
      FROM S02M006T SYKM
     WHERE SYKM.APKIHONCD = '000'
       AND SYKM.SHOPCD = '000'
       AND SYKM.SETTEINO = '096'
       AND SYKM.RCRDKBN = 'M'
       AND SYKM.SETTEICD = '8'
       AND SYKM.YUKOKBN = '1';
    --システム管理マスタからアクアクララ光の中分類コードの検索
    SELECT SYKM.MOJISETTEIATAI
      INTO wHikariMctgrcd
      FROM S02M006T SYKM
     WHERE SYKM.APKIHONCD = '000'
       AND SYKM.SHOPCD = '000'
       AND SYKM.SETTEINO = '918'
       AND SYKM.RCRDKBN = 'M'
       AND SYKM.OYASETTEINO = '096'
       AND SYKM.OYASETTEICD = '8'
       AND SYKM.YUKOKBN = '1';

    -- チェック
    returnIshikari := (iLctgrcd = wHikariLctgrcd AND iMctgrcd = wHikariMctgrcd);

    return(returnIshikari);
  END IsHikari;

  /*****************************************************************************/
  /* ファンクション名 ：基本情報設定                                           */
  /* 引数             ：iApKihoncd           AP基本コード                      */
  /*                  ：iShopcd              ショップコード                    */
  /*                  ：iUserApKihoncd       担当者ＡＰ基本コード              */
  /*                  ：iUserShopcd          担当者ショップコード              */
  /*                  ：iTnshacd             担当者コード                      */
  /*                  ：iIpadrse             ＩＰアドレス                      */
  /*****************************************************************************/
  PROCEDURE SetUserInfo(iApKihoncd     IN VARCHAR2,
                        iShopcd        IN VARCHAR2,
                        iUserApKihoncd IN VARCHAR2,
                        iUserShopcd    IN VARCHAR2,
                        iTnshacd       IN VARCHAR2,
                        iIpadrse       IN VARCHAR2) IS

  BEGIN
    -- 共通変数削除
    vRecordS06T001T := null;
    vRecordS06T002T := null;

    vRecordS06T001T.APKIHONCD := iApKihoncd; -- ＡＰ基本コード
    vRecordS06T001T.SHOPCD    := iShopcd; -- ショップコード

    --システム日付 と システム時間の検索
    SELECT TO_CHAR(SYSDATE, 'YYYYMMDD'), TO_CHAR(SYSDATE, 'hh24miss')
      INTO vRecordS06T001T.KSHNSYSTMYMD, vRecordS06T001T.KSHNSYSTMJIKAN
      FROM DUAL;

    vRecordS06T001T.KOSHAAPKIHONCD := iUserApKihoncd; -- 更新者ＡＰ基本コード
    vRecordS06T001T.KOSHASHOPCD    := iUserShopcd; -- 更新者ショップコード
    vRecordS06T001T.KOSHACD        := iTnshacd; -- 更新者コード
    vRecordS06T001T.KSHNUNYOYMD    := GetOperationdate(iApKihoncd, iShopcd); -- 更新運用日付
    vRecordS06T001T.KSHNIPADRSE    := iIpadrse; -- 更新ＩＰアドレス
    vRecordS06T001T.KSHNPGMID      := 'C1001B02'; -- 更新プログラムＩＤ

  END SetUserInfo;
  /*****************************************************************************/
  /* プロシージャ名   ：売上テーブル情報設定                                   */
  /* 引数             ：iRecordS06T001T          売上テーブル                  */
  /*****************************************************************************/
  PROCEDURE SetURGT(iRecordS06T001T IN S06T001T%rowtype) IS

  BEGIN
    vRecordS06T001T.TORIHIKIKBN    := iRecordS06T001T.TORIHIKIKBN; --売上テーブル.取引区分
    vRecordS06T001T.TOKUISAKICD    := iRecordS06T001T.TOKUISAKICD; --売上テーブル.得意先コード
    vRecordS06T001T.NYUKAMOTOCD    := iRecordS06T001T.NYUKAMOTOCD; --売上テーブル.入荷元コード
    vRecordS06T001T.URIDD          := iRecordS06T001T.URIDD; --売上テーブル.売上日
    vRecordS06T001T.ACPTTNSHACD    := iRecordS06T001T.ACPTTNSHACD; -- 売上テーブル.受注担当者コード
    vRecordS06T001T.EIGYOTNSHACD   := iRecordS06T001T.EIGYOTNSHACD; -- 売上テーブル.営業担当者コード
    vRecordS06T001T.JISSEKITNSHACD := iRecordS06T001T.JISSEKITNSHACD; -- 売上テーブル.実績担当者コード
    vRecordS06T001T.SKYSAKICD      := iRecordS06T001T.SKYSAKICD; -- 売上テーブル.請求先コード
    vRecordS06T001T.TAXKEISANKBN   := iRecordS06T001T.TAXKEISANKBN; -- 売上テーブル.税計算区分

  END SetURGT;
  /*****************************************************************************/
  /* プロシージャ名   ：得意先実績テーブル更新                                 */
  /* 引数             ：iTokuisakicd         得意先コード                      */
  /*****************************************************************************/
  PROCEDURE UpdateTJST(iTokuisakicd IN VARCHAR2) IS

    -- 消費税金額の取得
    taxkin S06T001T.SHOHITAXKIN%type := CASE WHEN (vRecordS06T001T.TORIHIKIKBN = '2') THEN 0 ELSE NVL(vRecordS06T001T.SHOHITAXKIN, 0) END;

  BEGIN
    -- 得意先実績テーブル更新
    UPDATE S02M050T TJST
    -- 現在残高:売上金額と消費税金額を加算する。
       SET TJST.GENZAIZD = CASE WHEN NVL(TJST.GENZAIZD, 0) + NVL(vRecordS06T001T.URIKIN, 0) + taxkin > 99999999 THEN 99999999 ELSE NVL(TJST.GENZAIZD, 0) + NVL(vRecordS06T001T.URIKIN, 0) + taxkin END,

           TJST.KOSHAAPKIHONCD = vRecordS06T001T.KOSHAAPKIHONCD, -- 更新者ＡＰ基本コード
           TJST.KOSHASHOPCD    = vRecordS06T001T.KOSHASHOPCD, -- 更新者ショップコード
           TJST.KOSHACD        = vRecordS06T001T.KOSHACD, -- 更新者コード
           TJST.KSHNSYSTMYMD   = vRecordS06T001T.KSHNSYSTMYMD, -- 更新システム日付: システム日付
           TJST.KSHNSYSTMJIKAN = vRecordS06T001T.KSHNSYSTMJIKAN, -- 更新システム時間: システム時間
           TJST.KSHNUNYOYMD    = vRecordS06T001T.KSHNUNYOYMD, -- 更新運用日付: システム管理マスタ.文字設定値
           TJST.KSHNIPADRSE    = vRecordS06T001T.KSHNIPADRSE, -- 更新ＩＰアドレス: パラメータ．更新ＩＰアドレス
           TJST.KSHNPGMID      = 'C1001B02' -- 更新プログラムＩＤ: C1001B02
     WHERE TJST.APKIHONCD = vRecordS06T001T.APKIHONCD
       AND TJST.SHOPCD = vRecordS06T001T.SHOPCD
       AND TJST.TOKUISAKICD = iTokuisakicd
       AND TJST.YUKOKBN = '1';
  END UpdateTJST;

  -- ポイント制度対応追加 start ABI
  /******************************************************************************/
  /* ファンクション名 ：ポイントテーブルの管理番号採番                          */
  /* 引数             ：iApkihoncd         ＡＰ基本コード                       */
  /*                  ：iShopcd            ショップコード                       */
  /* return           ：retPointKanrino    管理番号                             */
  /******************************************************************************/
  FUNCTION GetPointKanrino(iApKihoncd     IN VARCHAR2,
                           iShopcd        IN VARCHAR2)
  return VARCHAR2 is
    retPointKanrino     VARCHAR2(15);

  BEGIN

    -- 管理番号採番
    -- 引数ワーク(S20W002T)登録
    INSERT INTO S20W002T VALUES (iApKihoncd                         -- ＡＰ基本コード
                                 , iShopcd                          -- ショップコード
                                 , vRecordS06T001T.KOSHAAPKIHONCD   -- 担当者ＡＰ基本コード
                                 , vRecordS06T001T.KOSHASHOPCD      -- 担当者ショップコード
                                 , vRecordS06T001T.KOSHACD          -- 担当者コード
                                 , 'C1001B02'                       -- 処理ID
                                 , vRecordS06T001T.KSHNIPADRSE      -- ＩＰアドレス
                                 , '1'                              -- 行番号
                                 , null                             -- 伝票明細番号
                                 , null                             -- 伝票明細枝番号
                                 , null                             -- 商品コード
                                 , null                             -- 計上区分
                                 , null                             -- 商品単価
                                 , null                             -- 画面からの発生ポイント
                                 , null                             -- 数量
                                 , null                             -- 明細備考
                                 , null                             -- 予定発生ポイント明細の管理番号
                                 , null                             -- 予定発生ポイント明細の連番
                                 , null                             -- 売上還元ポイントマスタの取得ポイント種別
                                 , null                             -- 売上還元ポイントマスタの取得プランコード
                                 , '1'                              -- 有効区分
                                 , vRecordS06T001T.KOSHAAPKIHONCD   -- 登録者ＡＰ基本コード
                                 , vRecordS06T001T.KOSHASHOPCD      -- 登録者ショップコード
                                 , vRecordS06T001T.KOSHACD          -- 登録者コード
                                 , vRecordS06T001T.KSHNSYSTMYMD     -- 登録システム日付
                                 , vRecordS06T001T.KSHNSYSTMJIKAN   -- 登録システム時間
                                 , vRecordS06T001T.KSHNUNYOYMD      -- 登録運用日付
                                 , vRecordS06T001T.KSHNIPADRSE      -- 登録ＩＰアドレス
                                 , vRecordS06T001T.KSHNPGMID        -- 登録プログラムＩＤ
                                 , vRecordS06T001T.KOSHAAPKIHONCD   -- 更新者ＡＰ基本コード
                                 , vRecordS06T001T.KOSHASHOPCD      -- 更新者ショップコード
                                 , vRecordS06T001T.KOSHACD          -- 更新者コード
                                 , vRecordS06T001T.KSHNSYSTMYMD     -- 更新システム日付
                                 , vRecordS06T001T.KSHNSYSTMJIKAN   -- 更新システム時間
                                 , vRecordS06T001T.KSHNUNYOYMD      -- 更新運用日付
                                 , vRecordS06T001T.KSHNIPADRSE      -- 更新ＩＰアドレス
                                 , vRecordS06T001T.KSHNPGMID);      -- 更新プログラムＩＤ

      -- 実行
      Z2401B01.PointinfbhnProc(iApKihoncd                           -- AP基本コード
                               , iShopcd                            -- ショップコード
                               , vRecordS06T001T.KOSHAAPKIHONCD     -- 担当者ＡＰ基本コード
                               , vRecordS06T001T.KOSHASHOPCD        -- 担当者ショップコード
                               , vRecordS06T001T.KOSHACD            -- 担当者コード
                               , 'C1001B02'                         -- 処理ID
                               , vRecordS06T001T.KSHNIPADRSE        -- ＩＰアドレス
                               , '04'                               -- ポイント情報部品処理種別(管理番号採番)
                               , null                               -- 得意先コード
                               , null                               -- 伝票種別
                               , null                               -- 注文日
                               , null                               -- 配達予定日
                               , null                               -- 配達希望時間区分
                               , null                               -- 配達区分
                               , null);                             -- 受注受付経路区分

      -- 処理結果ワーク(S20W003T)読取り
      SELECT SAIBANKANRINO
      INTO retPointKanrino
      FROM  S20W003T
      WHERE
        TNSHAAPKIHONCD  = vRecordS06T001T.KOSHAAPKIHONCD
        AND TNSHASHOPCD = vRecordS06T001T.KOSHASHOPCD
        AND TNSHACD     = vRecordS06T001T.KOSHACD
        AND PRCSID      = 'C1001B02';

    return retPointKanrino;

  EXCEPTION
    WHEN OTHERS THEN
      IF SQLCODE >= -20000 AND SQLCODE <= -29999 THEN
        RAISE_APPLICATION_ERROR(SQLCODE, SQLERRM);
      ELSE
        RAISE_APPLICATION_ERROR(-20719, SQLERRM);
      END IF;

  END GetPointKanrino;

  /*****************************************************************************/
  /* プロシージャ名   ：ポイントテーブルの登録                                 */
  /* 引数             ：iApkihoncd         ＡＰ基本コード                      */
  /*                  ：iShopcd            ショップコード                      */
  /*                  ：iRtlRknShukeiKbn   レンタル料金集計区分                */
  /*****************************************************************************/
  PROCEDURE InsertPoint(iApKihoncd       IN VARCHAR2,
                        iShopcd          IN VARCHAR2,
                        iRtlRknShukeiKbn IN VARCHAR2) AS
    -- 変数宣言
    -- 処理結果ワークリスト
    TYPE                S20W003TList IS TABLE OF S20W003T%rowtype INDEX BY PLS_INTEGER;
    wSaibanno           VARCHAR2(15);  -- 採番した予定発行ポイントテーブル管理番号
    wUnyodd             VARCHAR2(8);  -- 運用日付
    wS20W003TList       S20W003TList;
    i                   PLS_INTEGER := 0;
    wYukoData           NUMBER(1) := 0;
    wRenban             NUMBER(2) := 1;
    wShnUnt             NUMBER(10,2) := 0; -- 商品単価
    wSuryo              NUMBER(9,1) := 0;  -- 数量

  BEGIN
    -- 運用日付取得
    wUnyodd := GetOperationdate(iApKihoncd, iShopcd);

    -- ポイント計算用の単価・数量
    IF iRtlRknShukeiKbn = '1' THEN
      -- レンタル料金集計
      wShnUnt := vRecordS06T002T.KIN;  -- 売上金額
      wSuryo  := 1;
    ELSE
      -- 定例売上
      wShnUnt := vRecordS06T002T.UNT;   -- 単価
      wSuryo  := vRecordS06T002T.SURYO; -- 数量
    END IF;

    -- ポイント情報部品(Z2401B01)で予定発生ポイントテーブルに登録する情報取得
    -- 引数ワーク(S20W002T)登録
    INSERT INTO S20W002T VALUES (iApKihoncd                             -- ＡＰ基本コード
                                 , iShopcd                              -- ショップコード
                                 , vRecordS06T001T.KOSHAAPKIHONCD       -- 担当者ＡＰ基本コード
                                 , vRecordS06T001T.KOSHASHOPCD          -- 担当者ショップコード
                                 , vRecordS06T001T.KOSHACD              -- 担当者コード                                 
                                 , 'C1001B02'                           -- 処理ID
                                 , vRecordS06T001T.KSHNIPADRSE          -- ＩＰアドレス
                                 , '1'                                  -- 行番号
                                 , vRecordS06T002T.URIDNPYMEISAINO      -- 伝票明細番号
                                 , '0'                                  -- 伝票明細枝番号
                                 , vRecordS06T002T.SHNCD                -- 商品コード
                                 , vRecordS06T002T.KEIJOKBN             -- 計上区分
                                 , wShnUnt                              -- 商品単価
                                 , null                                 -- 画面からの発生
                                 , wSuryo                               -- 数量
                                 , vRecordS06T002T.MEISAIBIKO           -- 明細備考
                                 , null                                 -- 予定発生ポイント明細の管理番号
                                 , null                                 -- 予定発生ポイント明細の連番
                                 , null                                 -- 売上還元ポイントマスタの取得種別
                                 , null                                 -- 売上還元ポイントマスタの取得プランコード
                                 , '1'                                  -- 有効区分
                                 , vRecordS06T001T.KOSHAAPKIHONCD       -- 登録者ＡＰ基本コード
                                 , vRecordS06T001T.KOSHASHOPCD          -- 登録者ショップコード
                                 , vRecordS06T001T.KOSHACD              -- 登録者コード
                                 , vRecordS06T001T.KSHNSYSTMYMD         -- 登録システム日付
                                 , vRecordS06T001T.KSHNSYSTMJIKAN       -- 登録システム時間
                                 , vRecordS06T001T.KSHNUNYOYMD          -- 登録運用日付
                                 , vRecordS06T001T.KSHNIPADRSE          -- 登録ＩＰアドレス
                                 , vRecordS06T001T.KSHNPGMID            -- 登録プログラムＩＤ
                                 , vRecordS06T001T.KOSHAAPKIHONCD       -- 更新者ＡＰ基本コード
                                 , vRecordS06T001T.KOSHASHOPCD          -- 更新者ショップコード
                                 , vRecordS06T001T.KOSHACD              -- 更新者コード
                                 , vRecordS06T001T.KSHNSYSTMYMD         -- 更新システム日付
                                 , vRecordS06T001T.KSHNSYSTMJIKAN       -- 更新システム時間
                                 , vRecordS06T001T.KSHNUNYOYMD          -- 更新運用日付
                                 , vRecordS06T001T.KSHNIPADRSE          -- 更新ＩＰアドレス
                                 , vRecordS06T001T.KSHNPGMID);          -- 更新プログラムＩＤ

  -- 実行
    Z2401B01.PointinfbhnProc(iApKihoncd                                 -- AP基本コード
                             , iShopcd                                  -- ショップコード
                             , vRecordS06T001T.KOSHAAPKIHONCD           -- 担当者ＡＰ基本コード
                             , vRecordS06T001T.KOSHASHOPCD              -- 担当者ショップコード
                             , vRecordS06T001T.KOSHACD                  -- 担当者コード
                             , 'C1001B02'                               -- 処理ID
                             , vRecordS06T001T.KSHNIPADRSE              -- ＩＰアドレス
                             , '02'                                     -- ポイント情報部品処理種別(一括計算)
                             , vRecordS06T001T.TOKUISAKICD              -- 得意先コード
                             , '05'                                     -- 伝達種別(売上)
                             , vRecordS06T001T.URIDD                    -- 注文日:売上日
                             , null                                     -- 配達予定日
                             , null                                     -- 配達希望時間区分
                             , 1                                        -- 配達区分(配達)
                             , null);                                   -- 受注受付経路区分

    -- 処理結果ワーク(S20W003T)から実行結果読取り
    i := 0;
    FOR cS20W003T IN (SELECT * FROM S20W003T
                      WHERE TNSHAAPKIHONCD  = vRecordS06T001T.KOSHAAPKIHONCD
                        AND TNSHASHOPCD     = vRecordS06T001T.KOSHASHOPCD
                        AND TNSHACD         = vRecordS06T001T.KOSHACD
                        AND PRCSID          = 'C1001B02'
                        AND YUKOKBN         = '1'
                      ORDER BY TO_NUMBER(GYOBANGOU))
    LOOP
      wS20W003TList(i).APKIHONCD := cS20W003T.APKIHONCD;                                -- ＡＰ基本コード
      wS20W003TList(i).SHOPCD := cS20W003T.SHOPCD;                                      -- ショップコード
      wS20W003TList(i).TNSHAAPKIHONCD := cS20W003T.TNSHAAPKIHONCD;                      -- 担当者ＡＰ基本コード
      wS20W003TList(i).TNSHASHOPCD := cS20W003T.TNSHASHOPCD;                            -- 担当者ショップコード
      wS20W003TList(i).TNSHACD := cS20W003T.TNSHACD;                                    -- 担当者コード
      wS20W003TList(i).IPADRSE := cS20W003T.IPADRSE;                                    -- ＩＰアドレス
      wS20W003TList(i).GYOBANGOU := cS20W003T.GYOBANGOU;                                -- 行番号
      wS20W003TList(i).SYORIKBN := cS20W003T.SYORIKBN;                                  -- ポイント情報部品処理種別
      wS20W003TList(i).SYORIKEKKA := cS20W003T.SYORIKEKKA;                              -- 処理結果
      wS20W003TList(i).POINTGENSIFUTANAPKIHONCD := cS20W003T.POINTGENSIFUTANAPKIHONCD;  -- ポイント原資負担元AP基本コード
      wS20W003TList(i).POINTGENSIFUTANSHOPCD := cS20W003T.POINTGENSIFUTANSHOPCD;        -- ポイント原資負担元ショップコード
      wS20W003TList(i).DNPYMEISAINO := cS20W003T.DNPYMEISAINO;                          -- 伝票明細番号
      wS20W003TList(i).DNPYMEISAIEDANO := cS20W003T.DNPYMEISAIEDANO;                    -- 伝票明細枝番号
      wS20W003TList(i).TOKUISAKICD := cS20W003T.TOKUISAKICD;                            -- 得意先コード
      wS20W003TList(i).CHUMONDD := cS20W003T.CHUMONDD;                                  -- 注文日
      wS20W003TList(i).SHNCD := cS20W003T.SHNCD;                                        -- 商品コード
      wS20W003TList(i).POINTKBN := cS20W003T.POINTKBN;                                  -- ポイント区分
      wS20W003TList(i).SHNNMRYAKUSHO := cS20W003T.SHNNMRYAKUSHO;                        -- 商品名称略称
      wS20W003TList(i).POINTHASSEIAPKIHONCD := cS20W003T.POINTHASSEIAPKIHONCD;          -- ポイント発生元ＡＰ基本コード
      wS20W003TList(i).POINTHASSEISHOPCD := cS20W003T.POINTHASSEISHOPCD;                -- ポイント発生元ショップコード
      wS20W003TList(i).POINTKIND := cS20W003T.POINTKIND;                                -- ポイント種別
      wS20W003TList(i).TEKIYOSTRTDD := cS20W003T.TEKIYOSTRTDD;                          -- 適用開始日
      wS20W003TList(i).TEKIYOSHRYDD := cS20W003T.TEKIYOSHRYDD;                          -- 適用終了日
      wS20W003TList(i).POINTHASSEIKBN := cS20W003T.POINTHASSEIKBN;                -- ポイント発生区分
      wS20W003TList(i).POINTTANKA := cS20W003T.POINTTANKA;                        -- ポイント単価
      wS20W003TList(i).POINTRNKCD := cS20W003T.POINTRNKCD;                              -- ポイントランクコード
      wS20W003TList(i).POINTRNK := cS20W003T.POINTRNK;                                  -- ポイントランク
      wS20W003TList(i).POINTBAIRITU := cS20W003T.POINTBAIRITU;                          -- ポイントランク倍率
      wS20W003TList(i).SUURYOUATARIPOINT := cS20W003T.SUURYOUATARIPOINT;                -- １数量あたりのポイント
      wS20W003TList(i).SURYO := cS20W003T.SURYO;                                        -- 数量
      wS20W003TList(i).GOUKEIPOINT := cS20W003T.GOUKEIPOINT;                            -- ポイント数量合計
      wS20W003TList(i).MEISAIBIKO := cS20W003T.MEISAIBIKO;                              -- 明細備考

      i := i + 1;
    END LOOP;

    IF wS20W003TList.COUNT > 0 THEN
      -- ポイントテーブルに登録する有効結果とワーニング情報の確認
      wYukoData := 0;
      FOR k IN wS20W003TList.FIRST .. wS20W003TList.LAST LOOP
        IF wS20W003TList(k).SYORIKEKKA = 0 THEN
          -- 有効行あり
          wYukoData := 1;
        ELSIF wS20W003TList(k).SYORIKEKKA = 2 THEN
          -- ショップがポイントなし
          S99_CHECK_LOG(vRecordS06T001T.KSHNPGMID
                        , iApkihoncd
                        , iShopcd
                        , '1'
                        , 'S'
                        , '1'
                        , 'XXX'
                        , 0
                        , vRecordS06T001T.KOSHAAPKIHONCD
                        , vRecordS06T001T.KOSHASHOPCD
                        , vRecordS06T001T.KOSHACD
                        , vRecordS06T001T.KSHNIPADRSE
                        , vRecordS06T001T.KSHNPGMID
                        , utl_lms.format_message('ポイントプログラムに不参加のショップです。ショップコード:[%s]', wS20W003TList(k).SHOPCD));

        ELSIF wS20W003TList(k).SYORIKEKKA = 3 THEN
          -- 得意先がポイントなし
          S99_CHECK_LOG(vRecordS06T001T.KSHNPGMID
                        , iApkihoncd
                        , iShopcd
                        , '1'
                        , 'S'
                        , '1'
                        , 'XXX'
                        , 0
                        , vRecordS06T001T.KOSHAAPKIHONCD
                        , vRecordS06T001T.KOSHASHOPCD
                        , vRecordS06T001T.KOSHACD
                        , vRecordS06T001T.KSHNIPADRSE
                        , vRecordS06T001T.KSHNPGMID
                        , utl_lms.format_message('ポイント管理を行わない得意先です。得意先コード:[%s]', wS20W003TList(k).TOKUISAKICD));

        ELSIF wS20W003TList(k).SYORIKEKKA = 4 THEN
          -- 商品がポイントなし
          S99_CHECK_LOG(vRecordS06T001T.KSHNPGMID
                        , iApkihoncd
                        , iShopcd
                        , '1'
                        , 'S'
                        , '1'
                        , 'XXX'
                        , 0
                        , vRecordS06T001T.KOSHAAPKIHONCD
                        , vRecordS06T001T.KOSHASHOPCD
                        , vRecordS06T001T.KOSHACD
                        , vRecordS06T001T.KSHNIPADRSE
                        , vRecordS06T001T.KSHNPGMID
                        , utl_lms.format_message('ポイントが発生しない商品または計上区分です。商品コード:[%s]', wS20W003TList(k).SHNCD));
        ELSIF wS20W003TList(k).SYORIKEKKA = 5 THEN
          -- 得意先がマイアクア会員登録なし
          S99_CHECK_LOG(vRecordS06T001T.KSHNPGMID
                        , iApkihoncd
                        , iShopcd
                        , '1'
                        , 'S'
                        , '1'
                        , 'XXX'
                        , 0
                        , vRecordS06T001T.KOSHAAPKIHONCD
                        , vRecordS06T001T.KOSHASHOPCD
                        , vRecordS06T001T.KOSHACD
                        , vRecordS06T001T.KSHNIPADRSE
                        , vRecordS06T001T.KSHNPGMID
                        , utl_lms.format_message('マイアクア会員が未登録の得意先です。得意先コード:[%s]', wS20W003TList(k).TOKUISAKICD));
        ELSIF wS20W003TList(k).SYORIKEKKA = -1 THEN
          -- 取込画面側の入力CSVとマスタの不整合チェック以外の予期せしないエラー発生時
          S99_CHECK_LOG(vRecordS06T001T.KSHNPGMID
                        , iApkihoncd
                        , iShopcd
                        , '1'
                        , 'S'
                        , '1'
                        , 'XXX'
                        , 0
                        , vRecordS06T001T.KOSHAAPKIHONCD
                        , vRecordS06T001T.KOSHASHOPCD
                        , vRecordS06T001T.KOSHACD
                        , vRecordS06T001T.KSHNIPADRSE
                        , vRecordS06T001T.KSHNPGMID
                        , 'ポイント情報部品(Z2401B01.PointinfbhnProc)の処理でエラーが発生しました。ログ(S99CHKLOG)を確認してください。');
          RAISE_APPLICATION_ERROR(-20719, 'ポイント情報部品(Z2401B01.PointinfbhnProc)の処理でエラーが発生しました。ログ(S99CHKLOG)を確認してください。');
        END IF;
      END LOOP;

      IF wYukoData = 1 THEN
        -- 有効なポイント情報をポイントテーブルに登録する
        wSaibanno := GetPointKanrino(iApKihoncd, iShopcd);

        -- 予定発生ポイントテーブル(S20T001T)に登録
        INSERT INTO S20T001T VALUES (iApKihoncd                                     -- ＡＰ基本コード
                                     , iShopcd                                      -- ショップコード
                                     , wSaibanno                                    -- 管理番号
                                     , null                                         -- 受注伝票番号
                                     , null                                         -- 受注枝番号
                                     , vRecordS06T001T.URIDNPYNO                    -- 売上伝票番号
                                     , vRecordS06T001T.EDANO                        -- 売上枝番号
                                     , vRecordS06T001T.TOKUISAKICD                  -- 得意先コード
                                     , '1'                                          -- 有効区分
                                     , vRecordS06T001T.KOSHAAPKIHONCD               -- 登録者ＡＰ基本コード
                                     , vRecordS06T001T.KOSHASHOPCD                  -- 登録者ショップコード
                                     , vRecordS06T001T.KOSHACD                      -- 登録者コード
                                     , vRecordS06T001T.KSHNSYSTMYMD                 -- 登録システム日付
                                     , vRecordS06T001T.KSHNSYSTMJIKAN               -- 登録システム時間
                                     , vRecordS06T001T.KSHNUNYOYMD                  -- 登録運用日付
                                     , vRecordS06T001T.KSHNIPADRSE                  -- 登録ＩＰアドレス
                                     , vRecordS06T001T.KSHNPGMID                    -- 登録プログラムＩＤ
                                     , vRecordS06T001T.KOSHAAPKIHONCD               -- 更新者ＡＰ基本コード
                                     , vRecordS06T001T.KOSHASHOPCD                  -- 更新者ショップコード
                                     , vRecordS06T001T.KOSHACD                      -- 更新者コード
                                     , vRecordS06T001T.KSHNSYSTMYMD                 -- 更新システム日付
                                     , vRecordS06T001T.KSHNSYSTMJIKAN               -- 更新システム時間
                                     , vRecordS06T001T.KSHNUNYOYMD                  -- 更新運用日付
                                     , vRecordS06T001T.KSHNIPADRSE                  -- 更新ＩＰアドレス
                                     , vRecordS06T001T.KSHNPGMID);                  -- 更新プログラムＩＤ

        FOR j IN wS20W003TList.FIRST .. wS20W003TList.LAST LOOP
          -- ポイントありのレコードだけ書き込む
          IF wS20W003TList(j).SYORIKEKKA = 0 THEN
            -- 予定発生ポイント明細テーブル(S20T002T)に登録
            INSERT INTO S20T002T VALUES (wS20W003TList(j).APKIHONCD                   -- ＡＰ基本コード
                                         , wS20W003TList(j).SHOPCD                    -- ショップコード
                                         , wSaibanno                                  -- 管理番号
                                         , wRenban                                    -- 連番
                                         , null                                       -- 受注伝票明細番号
                                         , null                                       -- 受注伝票明細枝番号
                                         , wS20W003TList(j).DNPYMEISAINO              -- 売上伝票明細番号
                                         , wS20W003TList(j).DNPYMEISAIEDANO           -- 売上伝票明細枝番号
                                         , wS20W003TList(j).POINTGENSIFUTANAPKIHONCD  -- ポイント原資負担元ＡＰ基本コード
                                         , wS20W003TList(j).POINTGENSIFUTANSHOPCD     -- ポイント原資負担元ショップコード
                                         , wS20W003TList(j).POINTHASSEIAPKIHONCD      -- ポイント発生元ＡＰ基本コード
                                         , wS20W003TList(j).POINTHASSEISHOPCD         -- ポイント発生元ショップコード
                                         , wS20W003TList(j).POINTKIND                 -- ポイント種別
                                         , null                                       -- 特別付与プランコード
                                         , null                                       -- 特別付与開始月数
                                         , null                                       -- 特別付与終了月数
                                         , wS20W003TList(j).TEKIYOSTRTDD              -- 適用開始日
                                         , wS20W003TList(j).TEKIYOSHRYDD              -- 適用終了日
                                         , wS20W003TList(j).POINTKBN                  -- ポイント区分
                                         , wS20W003TList(j).GOUKEIPOINT               -- ポイント数
                                         , wS20W003TList(j).MEISAIBIKO                -- 明細備考
                                         , wS20W003TList(j).CHUMONDD                  -- ポイント予定日付(売上日)
                                         , null                                       -- ポイント発生日付
                                         , null                                       -- ポイントマイアクア連携日付
                                         , '1'                                        -- 有効区分
                                         , vRecordS06T001T.KOSHAAPKIHONCD             -- 登録者ＡＰ基本コード
                                         , vRecordS06T001T.KOSHASHOPCD                -- 登録者ショップコード
                                         , vRecordS06T001T.KOSHACD                    -- 登録者コード
                                         , vRecordS06T001T.KSHNSYSTMYMD               -- 登録システム日付
                                         , vRecordS06T001T.KSHNSYSTMJIKAN             -- 登録システム時間
                                         , vRecordS06T001T.KSHNUNYOYMD                -- 登録運用日付
                                         , vRecordS06T001T.KSHNIPADRSE                -- 登録ＩＰアドレス
                                         , vRecordS06T001T.KSHNPGMID                  -- 登録プログラムＩＤ
                                         , vRecordS06T001T.KOSHAAPKIHONCD             -- 更新者ＡＰ基本コード
                                         , vRecordS06T001T.KOSHASHOPCD                -- 更新者ショップコード
                                         , vRecordS06T001T.KOSHACD                    -- 更新者コード
                                         , vRecordS06T001T.KSHNSYSTMYMD               -- 更新システム日付
                                         , vRecordS06T001T.KSHNSYSTMJIKAN             -- 更新システム時間
                                         , vRecordS06T001T.KSHNUNYOYMD                -- 更新運用日付
                                         , vRecordS06T001T.KSHNIPADRSE                -- 更新ＩＰアドレス
                                         , vRecordS06T001T.KSHNPGMID);                -- 更新プログラムＩＤ

            -- 予定発生ポイント明細計算詳細テーブル(S20T004T)に登録
            INSERT INTO S20T004T VALUES (wS20W003TList(j).APKIHONCD                   -- ＡＰ基本コード
                                         , wS20W003TList(j).SHOPCD                    -- ショップコード
                                         , wSaibanno                                  -- 管理番号
                                         , wRenban                                    -- 連番
                                         , null                                       -- 受注伝票明細番号
                                         , null                                       -- 受注伝票明細枝番号
                                         , wS20W003TList(j).DNPYMEISAINO              -- 売上伝票明細番号
                                         , wS20W003TList(j).DNPYMEISAIEDANO           -- 売上伝票明細枝番号
                                         , wS20W003TList(j).POINTHASSEIKBN            -- ポイント発生区分
                                         , wS20W003TList(j).POINTTANKA                -- ポイント単価
                                         , wS20W003TList(j).SURYO                     -- 数量
                                         , wS20W003TList(j).POINTRNKCD                -- ポイントランクコード
                                         , wS20W003TList(j).POINTRNK                  -- ポイントランク
                                         , wS20W003TList(j).POINTBAIRITU              -- ポイントランク倍率
                                         , wS20W003TList(j).GOUKEIPOINT               -- 計算ポイント数
                                         , '1'                                        -- 有効区分
                                         , vRecordS06T001T.KOSHAAPKIHONCD             -- 登録者ＡＰ基本コード
                                         , vRecordS06T001T.KOSHASHOPCD                -- 登録者ショップコード
                                         , vRecordS06T001T.KOSHACD                    -- 登録者コード
                                         , vRecordS06T001T.KSHNSYSTMYMD               -- 登録システム日付
                                         , vRecordS06T001T.KSHNSYSTMJIKAN             -- 登録システム時間
                                         , vRecordS06T001T.KSHNUNYOYMD                -- 登録運用日付
                                         , vRecordS06T001T.KSHNIPADRSE                -- 登録ＩＰアドレス
                                         , vRecordS06T001T.KSHNPGMID                  -- 登録プログラムＩＤ
                                         , vRecordS06T001T.KOSHAAPKIHONCD             -- 更新者ＡＰ基本コード
                                         , vRecordS06T001T.KOSHASHOPCD                -- 更新者ショップコード
                                         , vRecordS06T001T.KOSHACD                    -- 更新者コード
                                         , vRecordS06T001T.KSHNSYSTMYMD               -- 更新システム日付
                                         , vRecordS06T001T.KSHNSYSTMJIKAN             -- 更新システム時間
                                         , vRecordS06T001T.KSHNUNYOYMD                -- 更新運用日付
                                         , vRecordS06T001T.KSHNIPADRSE                -- 更新ＩＰアドレス
                                         , vRecordS06T001T.KSHNPGMID);                -- 更新プログラムＩＤ

              wRenban := wRenban + 1;
            END IF;
          END LOOP;
      END IF;
    END IF;

    return;

  EXCEPTION
    WHEN OTHERS THEN
      IF SQLCODE >= -20000 AND SQLCODE <= -29999 THEN
        RAISE_APPLICATION_ERROR(SQLCODE, SQLERRM);
      ELSE
        RAISE_APPLICATION_ERROR(-20719, SQLERRM);
      END IF;

  END InsertPoint;
  -- ポイント制度対応追加 end ABI

  /*****************************************************************************/
  /* プロシージャ名   ：売上テーブル登録                                       */
  /* 引数             ：iKaishuhohokbn           回収方法区分                  */
  /* 引数             ：iInvoicshikoymd          インボイス施工日              */
  /*****************************************************************************/
  PROCEDURE InsertURGT(iKaishuhohokbn IN VARCHAR2, iInvoicshikoymd IN VARCHAR2) IS
  BEGIN
    --売上テーブル
    INSERT INTO S06T001T
      (APKIHONCD, --  ＡＰ基本コード
       SHOPCD, --  ショップコード
       URIDNPYNO, --  売上伝票番号
       EDANO, --  枝番号
       TORIHIKIKBN, --  取引区分
       TOKUISAKICD, --  得意先コード
       SIIRESAKICD, --  仕入先コード
       NYUKAMOTOCD, --  入荷元コード
       URIDD, --  売上日
       ACPTDD, --  受注日
       URIKIN, --  売上金額
       SHOHITAXKIN, --  消費税金額
       -- インボイス対応変更 start ABI
       SANKOUSHOHITAXKIN, --  参考消費税金額
       -- インボイス対応変更 end ABI
       ACPTTNSHACD, --  受注担当者コード
       DLVRYTNSHACD, --  配達担当者コード
       DLVRYKANKATSUAPKIHONCD, --  配達管轄ＡＰ基本コード
       DLVRYKANKATSUSHOPCD, --  配達管轄ショップコード
       DLVRYKYOTENCD, --  配達拠点コード
       DLVRYKBN, --  配達区分
       DLVRYCMPNYCD, --  配達会社コード
       SHUKKAMOTOKBN, --  出荷元区分
       EIGYOTNSHACD, --  営業担当者コード
       JISSEKITNSHACD, --  実績担当者コード
       SKYSAKICD, --  請求先コード
       SKYSHIMEDYDM, --  請求締年月
       SKYSHIMEDD, --  請求締日
       SKYPRCSJISSIDD, --  請求処理実施日
       GETSUJISHIMEDYDM, --  月次締年月
       GETSUJISHIMEDD, --  月次締日
       GETSUJIPRCSJISSIDD, --  月次処理実施日
       URISHIMEPRCSDD, --  売上締処理日
       -- インボイス対応変更 start ABI
       NEBIKISHIMEDYDM, --  値引締年月
       NEBIKISHIMEDD, --  値引締日
       NEBIKIPRCSJISSIDD, --  値引処理実施日
       -- インボイス対応変更 end ABI
       DNPYBIKO, --  伝票備考
       DNPYNO, --  伝票番号
       SKYSHOOTPTARINASHIKBN, --  請求書出力有無区分
       SKYPRCSJISSIKBN, --  請求処理実施区分
       DATSKSMOTOKBN, --  データ作成元区分
       TAXKEISANKBN, --  税計算区分
       SIIREDNPYNO, --  仕入伝票番号
       YUKOKBN, --  有効区分
       TOSHAAPKIHONCD, --  登録者ＡＰ基本コード
       TOSHASHOPCD, --  登録者ショップコード
       TOSHACD, --  登録者コード
       INPTSYSTMYMD, --  登録システム日付
       INPTSYSTMJIKAN, --  登録システム時間
       INPTUNYOYMD, --  登録運用日付
       INPTIPADRSE, --  登録ＩＰアドレス
       INPTPGMID, --  登録プログラムＩＤ
       KOSHAAPKIHONCD, --  更新者ＡＰ基本コード
       KOSHASHOPCD, --  更新者ショップコード
       KOSHACD, --  更新者コード
       KSHNSYSTMYMD, --  更新システム日付
       KSHNSYSTMJIKAN, --  更新システム時間
       KSHNUNYOYMD, --  更新運用日付
       KSHNIPADRSE, --  更新ＩＰアドレス
       KSHNPGMID) --  更新プログラムＩＤ
    VALUES
      (vRecordS06T001T.APKIHONCD, --  ＡＰ基本コード
       vRecordS06T001T.SHOPCD, --  ショップコード
       vRecordS06T001T.URIDNPYNO, --  売上伝票番号
       '0', --  枝番号
       vRecordS06T001T.TORIHIKIKBN, --  取引区分
       vRecordS06T001T.TOKUISAKICD, --  得意先コード
       vRecordS06T001T.NYUKAMOTOCD, --  仕入先コード
       NULL, --  入荷元コード
       vRecordS06T001T.URIDD, --  売上日
       NULL, --  受注日
       vRecordS06T001T.URIKIN, --  売上金額
       -- インボイス対応変更 start ABI
       CASE WHEN vRecordS06T001T.TORIHIKIKBN = '2' THEN 0 ELSE vRecordS06T001T.SHOHITAXKIN END, --  消費税金額
       CASE WHEN vRecordS06T001T.TORIHIKIKBN = '2' THEN 0 ELSE vRecordS06T001T.SANKOUSHOHITAXKIN END, --  参考消費税金額
       -- インボイス対応変更 end ABI
       NULL, --  受注担当者コード
       NULL, --  配達担当者コード
       NULL, --  配達管轄ＡＰ基本コード
       NULL, --  配達管轄ショップコード
       NULL, --  配達拠点コード
       NULL, --  配達区分
       NULL, --  配達会社コード
       NULL, --  出荷元区分
       vRecordS06T001T.EIGYOTNSHACD, --  営業担当者コード
       vRecordS06T001T.JISSEKITNSHACD, --  実績担当者コード
       vRecordS06T001T.SKYSAKICD, --  請求先コード
       NULL, --  請求締年月
       NULL, --  請求締日
       '00000000', --  請求処理実施日
       NULL, --  月次締年月
       NULL, --  月次締日
       '00000000', --  月次処理実施日
       vRecordS06T001T.URIDD, --  売上締処理日:売上日と同じ値を設定
       -- インボイス対応変更 start ABI
       NULL, --  値引締年月
       NULL, --  値引締日
       '00000000', --  値引処理実施日
       -- インボイス対応変更 end ABI
       NULL, --  伝票備考
       NULL, --  伝票番号
       '0', --  請求書出力有無区分
       '0', --  請求処理実施区分
       '06', --  データ作成元区分
       vRecordS06T001T.TAXKEISANKBN, --  税計算区分
       vRecordS06T001T.URIDNPYNO, --  売上伝票番号
       '1', --  有効区分
       vRecordS06T001T.KOSHAAPKIHONCD, -- 登録者ＡＰ基本コード
       vRecordS06T001T.KOSHASHOPCD, -- 登録者ショップコード
       vRecordS06T001T.KOSHACD, -- 登録者コード
       vRecordS06T001T.KSHNSYSTMYMD, -- 登録システム日付
       vRecordS06T001T.KSHNSYSTMJIKAN, -- 登録システム時間
       vRecordS06T001T.KSHNUNYOYMD, -- 登録運用日付
       vRecordS06T001T.KSHNIPADRSE, -- 登録ＩＰアドレス
       vRecordS06T001T.KSHNPGMID, -- 登録プログラムＩＤ
       vRecordS06T001T.KOSHAAPKIHONCD, -- 更新者ＡＰ基本コード
       vRecordS06T001T.KOSHASHOPCD, -- 更新者ショップコード
       vRecordS06T001T.KOSHACD, -- 更新者コード
       vRecordS06T001T.KSHNSYSTMYMD, -- 更新システム日付
       vRecordS06T001T.KSHNSYSTMJIKAN, -- 更新システム時間
       vRecordS06T001T.KSHNUNYOYMD, -- 更新運用日付
       vRecordS06T001T.KSHNIPADRSE, -- 更新ＩＰアドレス
       vRecordS06T001T.KSHNPGMID); -- 更新プログラムＩＤ
    -- TYYMM999999のYYMMは年月、999999は連番,自動採番はＯＲＡＣＬＥ用採番共通部品を呼び出します。
    GetUridnpyno;
    -- 得意先実績テーブル更新
    UpdateTJST(vRecordS06T001T.TOKUISAKICD);
  END InsertURGT;

  /*****************************************************************************/
  /* プロシージャ名   ：売上明細テーブル登録                                   */
  /* 引数             ：iTaxsetteikbn            税設定区分                    */
  /* 引数             ：iKaishuhohokbn           回収方法区分                  */
  /* 引数             ：iInvoicshikoymd          インボイス施工日              */
  /* 引数             ：iTokuisakikbn            得意先区分                    */
  /* 引数             ：iJutakudlvrycstkbn       受託配達顧客区分              */
  /*****************************************************************************/
  PROCEDURE InsertUMST (iTaxsetteikbn IN VARCHAR2, iKaishuhohokbn IN VARCHAR2, iInvoicshikoymd IN VARCHAR2, iTokuisakikbn IN VARCHAR2, iJutakudlvrycstkbn IN VARCHAR2) IS

  --変数宣言
  wS06T002T_SHOHITAXKIN NUMBER(11); -- S06T002T_売上明細テーブルの消費税金額
  wS06T002T_SANKOUSHOHITAXKIN NUMBER(11); -- S06T002T_売上明細テーブルの参考消費税金額

  BEGIN

    IF vRecordS06T002T.TAXKBN <> '5' THEN

      IF vRecordS06T001T.TAXKEISANKBN = '2' THEN

        -- 税計算区分が２：外税の時は、“０”とする。
        vRecordS06T002T.UNTSHOHITAXKIN := 0;

        IF vRecordS06T002T.UNT IS NULL OR vRecordS06T002T.UNT = 0 THEN
          -- 消費税金額は共通部品を使用して計算をする。  金額　×　税率
          vRecordS06T002T.SHOHITAXKIN := Z1808B01.SyohizeiKeisan('1', --単体計算フラグ（する）
                                         iTaxsetteikbn, --税設定区分
                                         vRecordS06T002T.TAXKBN, --税区分
                                         vRecordS06T001T.TAXKEISANKBN, --税計算区分
                                                                           vRecordS06T001T.URIDD, --切り替え日：売上テーブルの売上日
                                                                           vRecordS06T001T.APKIHONCD, --AP基本コード
                                                                           vRecordS06T001T.SHOPCD, --ショップコード
                                                                           NULL, --仕入先コード
                                                                           NULL, --数量
                                                                           vRecordS06T002T.KIN); -- 金額
        ELSE
          -- 外税の消費税金額は共通部品を使用して計算をする。  金額（単価　×　数量）　×　税率
          vRecordS06T002T.SHOHITAXKIN := Z1808B01.SyohizeiKeisan('1', --単体計算フラグ（する）
                                         iTaxsetteikbn, --税設定区分
                                         vRecordS06T002T.TAXKBN, --税区分
                                         vRecordS06T001T.TAXKEISANKBN, --税計算区分
                                                                           vRecordS06T001T.URIDD, --切り替え日：売上テーブルの売上日
                                                                           vRecordS06T001T.APKIHONCD, --AP基本コード
                                                                           vRecordS06T001T.SHOPCD, --ショップコード
                                                                           NULL, --仕入先コード
                                                                           NULL, --数量
                                                                           vRecordS06T002T.KIN); -- 金額
        END IF;

      ELSE
        --内税の単価消費税金額は共通部品を使用して計算をする。  単価　×　税率
        vRecordS06T002T.UNTSHOHITAXKIN := Z1808B01.SyohizeiKeisan('1', --単体計算フラグ（する）
                                          iTaxsetteikbn, --税設定区分
                                          vRecordS06T002T.TAXKBN, --税区分
                                          vRecordS06T001T.TAXKEISANKBN, --税計算区分
                                                                              vRecordS06T001T.URIDD, --切り替え日：売上テーブルの売上日
                                                                              vRecordS06T001T.APKIHONCD, --AP基本コード
                                                                              vRecordS06T001T.SHOPCD, --ショップコード
                                                                              NULL, --仕入先コード
                                                                              NULL, --数量
                                                                              vRecordS06T002T.UNT); --売上明細テーブルの単価

        IF vRecordS06T002T.UNT IS NULL OR vRecordS06T002T.UNT = 0 THEN
          -- 消費税金額は共通部品を使用して計算をする。  金額　×　税率
          vRecordS06T002T.SHOHITAXKIN := Z1808B01.SyohizeiKeisan('1', --単体計算フラグ（する）
                                         iTaxsetteikbn, --税設定区分
                                         vRecordS06T002T.TAXKBN, --税区分
                                         vRecordS06T001T.TAXKEISANKBN, --税計算区分
                                                                           vRecordS06T001T.URIDD, --切り替え日：売上テーブルの売上日
                                                                           vRecordS06T001T.APKIHONCD, --AP基本コード
                                                                           vRecordS06T001T.SHOPCD, --ショップコード
                                                                           NULL, --仕入先コード
                                                                           NULL, --数量
                                                                           vRecordS06T002T.KIN); -- 金額
        ELSE
          -- 内税の消費税金額は　単価消費税金額　×　数量 とする
          vRecordS06T002T.SHOHITAXKIN := vRecordS06T002T.UNTSHOHITAXKIN * vRecordS06T002T.SURYO;
        END IF;
      END IF;

    ELSE
	      Z1808B01.SetSyohizeiKeisan(iTaxsetteikbn, --税設定区分
	                            vRecordS06T001T.TAXKEISANKBN, --税計算区分
	                                   vRecordS06T001T.URIDD, --切り替え日
	                               vRecordS06T001T.APKIHONCD, --AP基本コード
	                                  vRecordS06T001T.SHOPCD, --ショップコード
	                                   vRecordS06T002T.SURYO, --数量
	                                   vRecordS06T002T.SHNCD, --商品コード
	                                vRecordS06T001T.DLVRYKBN, --配達区分
	                                vRecordS06T002T.KEIJOKBN, --計上区分
	                          vRecordS06T002T.UNTSHOHITAXKIN, --戻り値：単価消費税
	                            vRecordS06T002T.SHOHITAXKIN); --戻り値：消費税
	END IF;

    -- 消費税金額計算
    IF vRecordS06T001T.TORIHIKIKBN = '2' THEN
        wS06T002T_SHOHITAXKIN := 0;
        wS06T002T_SANKOUSHOHITAXKIN := 0;
    ELSIF iKaishuhohokbn = '7' THEN -- 代引きの場合
        wS06T002T_SHOHITAXKIN := 0;
        wS06T002T_SANKOUSHOHITAXKIN := NVL(vRecordS06T002T.SHOHITAXKIN, 0);
    ELSIF vRecordS06T001T.URIDD < iInvoicshikoymd THEN -- 代引き以外の場合
        wS06T002T_SHOHITAXKIN := NVL(vRecordS06T002T.SHOHITAXKIN, 0);
        wS06T002T_SANKOUSHOHITAXKIN := 0;
    ELSE -- 上記以外の場合
        wS06T002T_SHOHITAXKIN := 0;
        wS06T002T_SANKOUSHOHITAXKIN := NVL(vRecordS06T002T.SHOHITAXKIN, 0);
    END IF;

    -- 売上テーブル.消費税金額:売上伝票番号が同一の売上明細テーブルの消費税金額の合計を設定する。
    -- 売上テーブル.消費税に０以外を登録する際、こちらで算出した値を用いる
    vRecordS06T001T.SHOHITAXKIN := NVL(vRecordS06T001T.SHOHITAXKIN, 0) +
                                   NVL(wS06T002T_SHOHITAXKIN, 0);
    -- 売上テーブル.売上金額:売上伝票番号が同一の売上明細テーブルの金額の合計を設定する。
    vRecordS06T001T.URIKIN := NVL(vRecordS06T001T.URIKIN, 0) +
                              NVL(vRecordS06T002T.KIN, 0);
    -- インボイス対応変更 start ABI
    -- 売上テーブル.参考消費税金額:売上明細テーブルに登録する消費税金額を足していき、売上伝票番号が同一の売上明細テーブルの参考消費税金額の合計を設定する。
    -- 売上テーブル.参考消費税に０以外を登録する際、こちらで算出した値を用いる
    vRecordS06T001T.SANKOUSHOHITAXKIN := NVL(vRecordS06T001T.SANKOUSHOHITAXKIN, 0) +
                                   NVL(wS06T002T_SANKOUSHOHITAXKIN, 0);

    -- インボイス対応変更 end ABI
    --売上明細テーブル
    INSERT INTO S06T002T
      (APKIHONCD, --  ＡＰ基本コード
       SHOPCD, --  ショップコード
       URIDNPYNO, --  売上伝票番号
       EDANO, --  枝番号
       URIDNPYMEISAINO, --  売上伝票明細番号
       URIDNPYMEISAIEDANO, --  売上伝票明細枝番号
       LCTGRYCD, --  大分類コード
       MCTGRYCD, --  中分類コード
       SCTGRYCD, --  小分類コード
       SHNCD, --  商品コード
       KEIJOKBN, --  計上区分
       SURYO, --  数量
       HYOJUNUNT, --  標準単価
       UNT, --  単価
       KIN, --  金額
       UNTSHOHITAXKIN, --  単価消費税金額
       SHOHITAXKIN, --  消費税金額
       -- インボイス対応変更 start ABI
       SANKOUSHOHITAXKIN, --  参考消費税金額
       -- インボイス対応変更 end ABI
       TAXKBN, --  税区分
       MEISAIKBN, --  明細区分
       NYURYOKUMEISAIFLG, --  入力明細フラグ
       TANPINMEISAIFLG, --  単品明細フラグ
       MEISAIBIKO, --  明細備考
       STSHNCD, --  セット商品コード
       KATASIKI, --  型式
       SIIRESURYO, --  仕入数量
       SIIREKIN, --  仕入金額
       SIIREUNT, -- 仕入単価
       SIIRESHOHITAXKIN, -- 仕入消費税金額
       ZAIKOKSHNSUMIFLG, -- 在庫更新済フラグ
       -- インボイス対応変更 start ABI
       TOKUISAKIKBN, --  得意先区分
       JUTAKUDLVRYCSTKBN, -- 受託配達顧客区分
       ROYALTYSHIMEYMD, --  ロイヤリティ締め年月日
       -- インボイス対応変更 end ABI
       YUKOKBN, --  有効区分
       TOSHAAPKIHONCD, --  登録者ＡＰ基本コード
       TOSHASHOPCD, --  登録者ショップコード
       TOSHACD, --  登録者コード
       INPTSYSTMYMD, --  登録システム日付
       INPTSYSTMJIKAN, --  登録システム時間
       INPTUNYOYMD, --  登録運用日付
       INPTIPADRSE, --  登録ＩＰアドレス
       INPTPGMID, --  登録プログラムＩＤ
       KOSHAAPKIHONCD, --  更新者ＡＰ基本コード
       KOSHASHOPCD, --  更新者ショップコード
       KOSHACD, --  更新者コード
       KSHNSYSTMYMD, --  更新システム日付
       KSHNSYSTMJIKAN, --  更新システム時間
       KSHNUNYOYMD, --  更新運用日付
       KSHNIPADRSE, --  更新ＩＰアドレス
       KSHNPGMID) -- 更新プログラムＩＤ
    VALUES
      (vRecordS06T001T.APKIHONCD, --  ＡＰ基本コード
       vRecordS06T001T.SHOPCD, --  ショップコード
       vRecordS06T001T.URIDNPYNO, --  売上伝票番号
       '0', --  枝番号
       vRecordS06T002T.URIDNPYMEISAINO, --  売上伝票明細番号
       '0', --  売上伝票明細枝番号
       vRecordS06T002T.LCTGRYCD, --  大分類コード
       vRecordS06T002T.MCTGRYCD, --  中分類コード
       vRecordS06T002T.SCTGRYCD, --  小分類コード
       vRecordS06T002T.SHNCD, --  商品コード
       vRecordS06T002T.KEIJOKBN, --  計上区分
       vRecordS06T002T.SURYO, --  数量
       vRecordS06T002T.HYOJUNUNT, --  標準単価
       vRecordS06T002T.UNT, --  単価
       vRecordS06T002T.KIN, --  金額
       CASE WHEN vRecordS06T001T.TORIHIKIKBN = '2' THEN 0 ELSE vRecordS06T002T.UNTSHOHITAXKIN END, --  単価消費税金額
       -- インボイス対応変更 start ABI
       wS06T002T_SHOHITAXKIN, --  消費税金額
       wS06T002T_SANKOUSHOHITAXKIN, --  参考消費税金額
       -- インボイス対応変更 end ABI
       vRecordS06T002T.TAXKBN, --  税区分
       '0', --  明細区分
       '1', --  入力明細フラグ
       '1', --  単品明細フラグ
       vRecordS06T002T.MEISAIBIKO, --  明細備考
       null, --  セット商品コード
       vRecordS06T002T.KATASIKI, --  型式
       '0', --  仕入数量
       '0', --  仕入金額
       '0', -- 仕入単価
       '0', -- 仕入消費税金額
       '1', -- 在庫更新済フラグ
       -- インボイス対応変更 start ABI
       iTokuisakikbn, --  得意先区分
       iJutakudlvrycstkbn, -- 受託配達顧客区分
       '00000000', -- ロイヤリティ締め年月日
       -- インボイス対応変更 end ABI
       '1', --  有効区分
       vRecordS06T001T.KOSHAAPKIHONCD, -- 登録者ＡＰ基本コード
       vRecordS06T001T.KOSHASHOPCD, -- 登録者ショップコード
       vRecordS06T001T.KOSHACD, -- 登録者コード
       vRecordS06T001T.KSHNSYSTMYMD, -- 登録システム日付
       vRecordS06T001T.KSHNSYSTMJIKAN, -- 登録システム時間
       vRecordS06T001T.KSHNUNYOYMD, -- 登録運用日付
       vRecordS06T001T.KSHNIPADRSE, -- 登録ＩＰアドレス
       vRecordS06T001T.KSHNPGMID, -- 登録プログラムＩＤ
       vRecordS06T001T.KOSHAAPKIHONCD, -- 更新者ＡＰ基本コード
       vRecordS06T001T.KOSHASHOPCD, -- 更新者ショップコード
       vRecordS06T001T.KOSHACD, -- 更新者コード
       vRecordS06T001T.KSHNSYSTMYMD, -- 更新システム日付
       vRecordS06T001T.KSHNSYSTMJIKAN, -- 更新システム時間
       vRecordS06T001T.KSHNUNYOYMD, -- 更新運用日付
       vRecordS06T001T.KSHNIPADRSE, -- 更新ＩＰアドレス
       vRecordS06T001T.KSHNPGMID); -- 更新プログラムＩＤ
  END InsertUMST;
  -- インボイス対応変更 start ABI
  /*****************************************************************************/
  /* プロシージャ名   ：売上明細テーブル税料金登録                             */
  /*****************************************************************************/
  PROCEDURE InsertUMSTByTax(iTaxkbn IN VARCHAR2, iTaxPrice IN NUMBER, iSum IN NUMBER, iTokuisakikbn IN VARCHAR2, iJutakudlvrycstkbn IN VARCHAR2) IS
  BEGIN
  	--売上明細テーブル
    INSERT INTO S06T002T
      (APKIHONCD, --  ＡＰ基本コード
       SHOPCD, --  ショップコード
       URIDNPYNO, --  売上伝票番号
       EDANO, --  枝番号
       URIDNPYMEISAINO, --  売上伝票明細番号
       URIDNPYMEISAIEDANO, --  売上伝票明細枝番号
       LCTGRYCD, --  大分類コード
       MCTGRYCD, --  中分類コード
       SCTGRYCD, --  小分類コード
       SHNCD, --  商品コード
       KEIJOKBN, --  計上区分
       SURYO, --  数量
       HYOJUNUNT, --  標準単価
       UNT, --  単価
       KIN, --  金額
       UNTSHOHITAXKIN, --  単価消費税金額
       SHOHITAXKIN, --  消費税金額
       -- インボイス対応変更 start ABI
       SANKOUSHOHITAXKIN, --  参考消費税金額
       -- インボイス対応変更 end ABI
       TAXKBN, --  税区分
       MEISAIKBN, --  明細区分
       NYURYOKUMEISAIFLG, --  入力明細フラグ
       TANPINMEISAIFLG, --  単品明細フラグ
       MEISAIBIKO, --  明細備考
       STSHNCD, --  セット商品コード
       KATASIKI, --  型式
       SIIRESURYO, --  仕入数量
       SIIREKIN, --  仕入金額
       SIIREUNT, -- 仕入単価
       SIIRESHOHITAXKIN, -- 仕入消費税金額
       ZAIKOKSHNSUMIFLG, -- 在庫更新済フラグ
       -- インボイス対応変更 start ABI
       TOKUISAKIKBN, --  得意先区分
       JUTAKUDLVRYCSTKBN, -- 受託配達顧客区分
       ROYALTYSHIMEYMD, --  ロイヤリティ締め年月日
       -- インボイス対応変更 end ABI
       YUKOKBN, --  有効区分
       TOSHAAPKIHONCD, --  登録者ＡＰ基本コード
       TOSHASHOPCD, --  登録者ショップコード
       TOSHACD, --  登録者コード
       INPTSYSTMYMD, --  登録システム日付
       INPTSYSTMJIKAN, --  登録システム時間
       INPTUNYOYMD, --  登録運用日付
       INPTIPADRSE, --  登録ＩＰアドレス
       INPTPGMID, --  登録プログラムＩＤ
       KOSHAAPKIHONCD, --  更新者ＡＰ基本コード
       KOSHASHOPCD, --  更新者ショップコード
       KOSHACD, --  更新者コード
       KSHNSYSTMYMD, --  更新システム日付
       KSHNSYSTMJIKAN, --  更新システム時間
       KSHNUNYOYMD, --  更新運用日付
       KSHNIPADRSE, --  更新ＩＰアドレス
       KSHNPGMID) -- 更新プログラムＩＤ
    VALUES
      (vRecordS06T001T.APKIHONCD, --  ＡＰ基本コード
       vRecordS06T001T.SHOPCD, --  ショップコード
       vRecordS06T001T.URIDNPYNO, --  売上伝票番号
       '0', --  枝番号
       vRecordS06T002T.URIDNPYMEISAINO, --  売上伝票明細番号
       '0', --  売上伝票明細枝番号
       '99', --  大分類コード
       '001', --  中分類コード
       CASE WHEN iTaxkbn = '1' THEN '001' ELSE '002' END, --  小分類コード
       CASE WHEN iTaxkbn = '1' THEN '099901' ELSE '099902' END, --  商品コード
       '01', --  計上区分
       1, --  数量
       0, --  標準単価
       0, --  単価
       0, --  金額
       0, --  単価消費税金額
       iTaxPrice, --  消費税金額
       0, --  参考消費税金額
       iTaxkbn, --  税区分
       '0', --  明細区分
       '1', --  入力明細フラグ
       '1', --  単品明細フラグ
       NULL, --  明細備考
       NULL, --  セット商品コード
       NULL, --  型式
       '0', --  仕入数量
       '0', --  仕入金額
       '0', -- 仕入単価
       '0', -- 仕入消費税金額
       '1', -- 在庫更新済フラグ
       iTokuisakikbn, -- 得意先区分
       iJutakudlvrycstkbn, -- 受託配達顧客区分
       '00000000', -- ロイヤリティ締め年月日
       '1', --  有効区分
       vRecordS06T001T.KOSHAAPKIHONCD, -- 登録者ＡＰ基本コード
       vRecordS06T001T.KOSHASHOPCD, -- 登録者ショップコード
       vRecordS06T001T.KOSHACD, -- 登録者コード
       vRecordS06T001T.KSHNSYSTMYMD, -- 登録システム日付
       vRecordS06T001T.KSHNSYSTMJIKAN, -- 登録システム時間
       vRecordS06T001T.KSHNUNYOYMD, -- 登録運用日付
       vRecordS06T001T.KSHNIPADRSE, -- 登録ＩＰアドレス
       vRecordS06T001T.KSHNPGMID, -- 登録プログラムＩＤ
       vRecordS06T001T.KOSHAAPKIHONCD, -- 更新者ＡＰ基本コード
       vRecordS06T001T.KOSHASHOPCD, -- 更新者ショップコード
       vRecordS06T001T.KOSHACD, -- 更新者コード
       vRecordS06T001T.KSHNSYSTMYMD, -- 更新システム日付
       vRecordS06T001T.KSHNSYSTMJIKAN, -- 更新システム時間
       vRecordS06T001T.KSHNUNYOYMD, -- 更新運用日付
       vRecordS06T001T.KSHNIPADRSE, -- 更新ＩＰアドレス
       vRecordS06T001T.KSHNPGMID); -- 更新プログラムＩＤ
  END InsertUMSTByTax;
  -- インボイス対応変更 end ABI
  /*****************************************************************************/
  /* プロシージャ名   ：定例売上仕入マスタ更新（売上）                         */
  /* 引数             ：iKeijodd              計上日                           */
  /*****************************************************************************/
  PROCEDURE UpdateTUSM(iKeijodd IN VARCHAR2) IS

  BEGIN
    -- 定例売上仕入マスタ更新（売上）
    UPDATE (select TUSM.*
              FROM s02m016t TUSM, s02m013t TKSM -- 得意先マスタ
             WHERE TUSM.APKIHONCD = vRecordS06T001T.APKIHONCD
               AND TUSM.SHOPCD = vRecordS06T001T.SHOPCD
                -- 次回計上年月+計上日 <= 計上日
             AND (TUSM.JIKAIKEIJODYDM || CASE WHEN TO_CHAR(Last_day(TO_DATE(TUSM.JIKAIKEIJODYDM, 'YYYYMM')), 'DD') <= TUSM.KEIJODD THEN TO_CHAR(Last_day(TO_DATE(TUSM.JIKAIKEIJODYDM, 'YYYYMM')), 'DD') ELSE TUSM.KEIJODD END) <= iKeijodd
               AND TUSM.SHOKAIKEIJODYDM <=
                   to_char(to_date(iKeijodd, 'yyyymmdd'), 'yyyymm') -- 初回計上年月
               AND TUSM.SHRYDYDM >=
                   to_char(to_date(iKeijodd, 'yyyymmdd'), 'yyyymm') -- 終了年月
               AND TUSM.URISIIREKBN = '1' --  売上仕入区分
               AND TUSM.YUKOKBN = '1' --  有効区分
               AND TUSM.APKIHONCD = TKSM.APKIHONCD
               AND TUSM.SHOPCD = TKSM.SHOPCD
               AND TUSM.TOKUISAKICD = TKSM.TOKUISAKICD
               -- インボイス対応変更 start ABI
               AND ( ( TUSM.TEIREIKBN <> '4' AND TKSM.JOTAISTS = '07') -- 定例区分：アクアクララ光以外；状態ステータス：継続中
               -- インボイス対応変更 end ABI
                     OR
                     ( TUSM.TEIREIKBN = '4'                                 -- 定例区分：アクアクララ光；請求停止データなし
                       AND NOT EXISTS ( SELECT * FROM S02M061T KYSM
                                         WHERE KYSM.APKIHONCD     = TUSM.APKIHONCD
                                           AND KYSM.SHOPCD        = TUSM.SHOPCD
                                           AND KYSM.TOKUISAKICD   = TUSM.TOKUISAKICD
                                           AND KYSM.YUKOKBN       = '1'
                                           AND KYSM.SKYTEISHIKBN  = '1'
                                           AND KYSM.SKYTEISHIDYDM <= TUSM.JIKAIKEIJODYDM
                                      )
                      )
                   )
            ) TUSM
    -- 売上発生有無フラグが“有”のデータ時、前回発生日に次回計上日を設定する。
       SET TUSM.ZNKHASSEIKEIJODD = (decode(TUSM.DNPYHASSEIARINASHIFLG, 1,(TUSM.JIKAIKEIJODYDM || CASE WHEN TO_CHAR(Last_day(TO_DATE(TUSM.JIKAIKEIJODYDM, 'YYYYMM')), 'DD') <= TUSM.KEIJODD THEN TO_CHAR(Last_day(TO_DATE(TUSM.JIKAIKEIJODYDM, 'YYYYMM')), 'DD') ELSE TUSM.KEIJODD END), TUSM.ZNKHASSEIKEIJODD)),
           -- 次回計上年月(次回計上日と発生間隔より算出した日付を設定 )発生間隔計上パターンが 1:毎月
           TUSM.JIKAIKEIJODYDM = (CASE when TUSM.HASSEIKANKAKUKEIJOPTN = 1 then to_char(add_months(to_date(TUSM.JIKAIKEIJODYDM, 'yyyymm'), 1), 'yyyymm')
           -- 発生間隔計上パターンが 2:３ヶ月
            when TUSM.HASSEIKANKAKUKEIJOPTN = 2 then to_char(add_months(to_date(TUSM.JIKAIKEIJODYDM, 'yyyymm'), 3), 'yyyymm')
           -- 発生間隔計上パターンが 3:６ヶ月
            when TUSM.HASSEIKANKAKUKEIJOPTN = 3 then to_char(add_months(to_date(TUSM.JIKAIKEIJODYDM, 'yyyymm'), 6), 'yyyymm')
           -- 発生間隔計上パターンが 4:１年
            when TUSM.HASSEIKANKAKUKEIJOPTN = 4 then to_char(add_months(to_date(TUSM.JIKAIKEIJODYDM, 'yyyymm'), 12), 'yyyymm') else null end),
           TUSM.KOSHAAPKIHONCD = vRecordS06T001T.KOSHAAPKIHONCD, -- 更新者ＡＰ基本コード
           TUSM.KOSHASHOPCD    = vRecordS06T001T.KOSHASHOPCD, -- 更新者ショップコード
           TUSM.KOSHACD        = vRecordS06T001T.KOSHACD, -- 更新者コード
           TUSM.KSHNSYSTMYMD   = vRecordS06T001T.KSHNSYSTMYMD, -- 更新システム日付: システム日付
           TUSM.KSHNSYSTMJIKAN = vRecordS06T001T.KSHNSYSTMJIKAN, -- 更新システム時間: システム時間
           TUSM.KSHNUNYOYMD    = vRecordS06T001T.KSHNUNYOYMD, -- 更新運用日付: システム管理マスタ.文字設定値
           TUSM.KSHNIPADRSE    = vRecordS06T001T.KSHNIPADRSE, -- 更新ＩＰアドレス: パラメータ．更新ＩＰアドレス
           TUSM.KSHNPGMID      = 'C1001B02'; -- 更新プログラムＩＤ: C1001B02

    -- 定例売上仕入マスタ更新（売上）
    UPDATE (select TUSM.*
              FROM s02m016t TUSM, s02m013t TKSM -- 得意先マスタ
             WHERE TUSM.APKIHONCD = vRecordS06T001T.APKIHONCD
               AND TUSM.SHOPCD = vRecordS06T001T.SHOPCD
                -- 次回計上年月+計上日 <= 計上日
             AND (TUSM.JIKAIKEIJODYDM || CASE WHEN TO_CHAR(Last_day(TO_DATE(TUSM.JIKAIKEIJODYDM, 'YYYYMM')), 'DD') <= TUSM.KEIJODD THEN TO_CHAR(Last_day(TO_DATE(TUSM.JIKAIKEIJODYDM, 'YYYYMM')), 'DD') ELSE TUSM.KEIJODD END) <= iKeijodd
               AND TUSM.SHOKAIKEIJODYDM <=
                   to_char(to_date(iKeijodd, 'yyyymmdd'), 'yyyymm') -- 初回計上年月
               AND TUSM.SHRYDYDM >=
                   to_char(to_date(iKeijodd, 'yyyymmdd'), 'yyyymm') -- 終了年月
               AND TUSM.URISIIREKBN = '1' --  売上仕入区分
               AND TUSM.YUKOKBN = '1' --  有効区分
               AND TUSM.APKIHONCD = TKSM.APKIHONCD
               AND TUSM.SHOPCD = TKSM.SHOPCD
               AND TUSM.TOKUISAKICD = TKSM.TOKUISAKICD
               -- インボイス対応変更 start ABI
               AND ( ( TUSM.TEIREIKBN <>'4' AND TKSM.JOTAISTS <> '07' ) -- 定例区分：アクラクララ光以外；状態ステータス：継続中以外
               -- インボイス対応変更 end ABI
                     OR
                     ( TUSM.TEIREIKBN = '4'                                  -- 定例区分：アクラクララ光；請求停止データあり
                       AND EXISTS ( SELECT * FROM S02M061T KYSM
                                     WHERE KYSM.APKIHONCD     = TUSM.APKIHONCD
                                       AND KYSM.SHOPCD        = TUSM.SHOPCD
                                       AND KYSM.TOKUISAKICD   = TUSM.TOKUISAKICD
                                       AND KYSM.YUKOKBN       = '1'
                                       AND KYSM.SKYTEISHIKBN  = '1'
                                       AND KYSM.SKYTEISHIDYDM <= TUSM.JIKAIKEIJODYDM
                                  )
                     )
                   )
            ) TUSM
    -- 売上発生有無フラグが“有”のデータ時、前回発生日に次回計上日を設定する。
       SET -- 次回計上年月(次回計上日と発生間隔より算出した日付を設定 )発生間隔計上パターンが 1:毎月
           TUSM.JIKAIKEIJODYDM = (CASE when TUSM.HASSEIKANKAKUKEIJOPTN = 1 then to_char(add_months(to_date(TUSM.JIKAIKEIJODYDM, 'yyyymm'), 1), 'yyyymm')
           -- 発生間隔計上パターンが 2:３ヶ月
            when TUSM.HASSEIKANKAKUKEIJOPTN = 2 then to_char(add_months(to_date(TUSM.JIKAIKEIJODYDM, 'yyyymm'), 3), 'yyyymm')
           -- 発生間隔計上パターンが 3:６ヶ月
            when TUSM.HASSEIKANKAKUKEIJOPTN = 3 then to_char(add_months(to_date(TUSM.JIKAIKEIJODYDM, 'yyyymm'), 6), 'yyyymm')
           -- 発生間隔計上パターンが 4:１年
            when TUSM.HASSEIKANKAKUKEIJOPTN = 4 then to_char(add_months(to_date(TUSM.JIKAIKEIJODYDM, 'yyyymm'), 12), 'yyyymm') else null end),
           TUSM.KOSHAAPKIHONCD = vRecordS06T001T.KOSHAAPKIHONCD, -- 更新者ＡＰ基本コード
           TUSM.KOSHASHOPCD    = vRecordS06T001T.KOSHASHOPCD, -- 更新者ショップコード
           TUSM.KOSHACD        = vRecordS06T001T.KOSHACD, -- 更新者コード
           TUSM.KSHNSYSTMYMD   = vRecordS06T001T.KSHNSYSTMYMD, -- 更新システム日付: システム日付
           TUSM.KSHNSYSTMJIKAN = vRecordS06T001T.KSHNSYSTMJIKAN, -- 更新システム時間: システム時間
           TUSM.KSHNUNYOYMD    = vRecordS06T001T.KSHNUNYOYMD, -- 更新運用日付: システム管理マスタ.文字設定値
           TUSM.KSHNIPADRSE    = vRecordS06T001T.KSHNIPADRSE, -- 更新ＩＰアドレス: パラメータ．更新ＩＰアドレス
           TUSM.KSHNPGMID      = 'C1001B02'; -- 更新プログラムＩＤ: C1001B02

  END UpdateTUSM;

/*****************************************************************************/
/* プロシージャ名 ：定例売上処理                                             */
/* 引数           ：iApKihoncd           AP基本コード                        */
/*                ：iShopcd              ショップコード                      */
/*                ：iUserApKihoncd       担当者ＡＰ基本コード                */
/*                ：iUserShopcd          担当者ショップコード                */
/*                ：iKeijodd             計上日                              */
/*                ：iTnshacd             担当者コード                        */
/*                ：iIpadrse             ＩＰアドレス                        */
/*****************************************************************************/
PROCEDURE TeireiUriage(iApKihoncd     IN VARCHAR2,
                         iShopcd        IN VARCHAR2,
                         iUserApKihoncd IN VARCHAR2,
                         iUserShopcd    IN VARCHAR2,
                         iKeijodd       IN VARCHAR2,
                         iTnshacd       IN VARCHAR2,
                         iIpadrse       IN VARCHAR2) IS
    wUridnpymeisaino S06T002T.URIDNPYMEISAINO%type; -- 売上伝票明細番号
    wRecordS06T001T  S06T001T%rowtype; --売上テーブル
    wKeijodd         VARCHAR2(2); -- 計上日
    wKeijodd2        VARCHAR2(2); -- 計上日
    wJikaikeijodydm  VARCHAR2(6); -- 次回計上年月
    wJikaikeijodydm2 VARCHAR2(6); -- 次回計上年月
    -- インボイス対応変更 start ABI
    wKaishuhohokbn   VARCHAR2(1); -- 回収方法
    wKaishuhohokbn2  VARCHAR2(1); -- 回収方法
    wInvoicshikoymd  VARCHAR2(8);    -- インボイス施工日
    wTokuisakikbn    VARCHAR2(1);    -- 得意先区分
    wJutakudlvrycstkbn  VARCHAR2(1); -- 受託配達顧客区分
    wTokuisakikbn2   VARCHAR2(1);    -- 得意先区分
    wJutakudlvrycstkbn2 VARCHAR2(1); -- 受託配達顧客区分
    wRtlRknShukeiKbn VARCHAR2(1);    -- レンタル料金集計区分
    kinSum1          NUMBER(11);     -- 金額合計(税区分１)
    kinSum4          NUMBER(11);     -- 金額合計(税区分４)
    taxPrice         NUMBER(11);     -- 税料金合計
      -- インボイス対応変更 end ABI
    wTeireihasseino  S02M016T.TEIREIHASSEINO%type; --定例発生番号
    wTaxsetteikbn    VARCHAR2(1); -- 税設定区分
    wShokaiKeijoDYDM VARCHAR2(6); -- 初回計上年月
    wShryDYDM        VARCHAR2(6); -- 終了年月
    wHasseiKankakuKeijoPTN VARCHAR(1); -- 発生間隔計上パターン
    wHasseiKankaku   NUMBER(2);   -- 発生間隔（月）
    wSoKaisu         NUMBER(9,0); -- 総回数
    wHasseiKaisu     NUMBER(9,0); -- 発生回数
    -- 定例売上仕入マスタ抽出（売上）
    CURSOR SELECT_URIDATE IS
      SELECT TUSM.TORIHIKIKBN TORIHIKIKBN, -- 取引区分
             TUSM.TOKUISAKICD TOKUISAKICD, -- 得意先コード
             TUSM.NYUKAMOTOCD NYUKAMOTOCD, -- 入荷元コード
             (TUSM.JIKAIKEIJODYDM || CASE
               WHEN TO_CHAR(Last_day(TO_DATE(TUSM.JIKAIKEIJODYDM, 'YYYYMM')),
                            'DD') <= TUSM.KEIJODD THEN
                TO_CHAR(Last_day(TO_DATE(TUSM.JIKAIKEIJODYDM, 'YYYYMM')),
                        'DD')
               ELSE
                TUSM.KEIJODD
             END) URIDD, --  売上日(=次回計上年月+計上日)
             TKSM.EIGYOTNSHACD ACPTTNSHACD, -- 受注担当者コード
             TKSM.EIGYOTNSHACD EIGYOTNSHACD, -- 営業担当者コード
             (CASE WHEN TUSM.JISSEKITNSHACD IS NULL
       THEN
      (CASE WHEN TKSM.URIKEIJOTNSHAKBN = '0' OR TKSM.URIKEIJOTNSHAKBN = '1' THEN TKSM.DLVRYTNSHACD   -- 得意先マスタの配達担当者コード
            ELSE TKSM.EIGYOTNSHACD END)  -- 得意先マスタの営業担当者コード
       ELSE
           TUSM.JISSEKITNSHACD -- 実績担当者コード
        END) JISSEKITNSHACD, -- 実績担当者コード
             TKSM.SKYSAKICD SKYSAKICD, -- 請求先コード
             -- 売上日＜ショップマスタの税計算区分売上切替日のとき現税計算区分売上、以外のとき新税計算区分売上
             (CASE
             -- 得意先が卸対象の場合は外税とする
             WHEN TKSM.TOKUISAKIKBN = '2' OR TKSM.TOKUISAKIKBN = '3' OR TKSM.TOKUISAKIKBN = '4' OR TKSM.TOKUISAKIKBN = '5' OR TKSM.TOKUISAKIKBN = '6' THEN '2'
             WHEN (TUSM.JIKAIKEIJODYDM ||
                    CASE
                    WHEN
                     TO_CHAR(Last_day(TO_DATE(TUSM.JIKAIKEIJODYDM, 'YYYYMM')),'DD') <= TUSM.KEIJODD
                    THEN
                     TO_CHAR(Last_day(TO_DATE(TUSM.JIKAIKEIJODYDM, 'YYYYMM')),'DD')
                    ELSE TUSM.KEIJODD END) <
                  SHPM.TAXKEISANKBNURIKIRIKAEDD
             THEN
                GENZTAXKEISANKBNURI
             ELSE
                SHINTAXKEISANKBNURI
             END) TAXKEISANKBN, -- 税計算区分

             SHMV.LCTGRYCD LCTGRYCD, -- 大分類コード
             SHMV.MCTGRYCD MCTGRYCD, -- 中分類コード
             SHMV.SCTGRYCD SCTGRYCD, -- 小分類コード
             TUSM.SHNCD SHNCD, --商品コード
             TUSM.KEIJOKBN KEIJOKBN, -- 計上区分

             DECODE(TUSM.RTLRKNSHUKEIKBN,
                        0, -- 定例売上仕入マスタのレンタル料金集計区分が無のとき
                        TUSM.SURYO, -- 数量
                        1, -- 定例売上仕入マスタのレンタル料金集計区分が有の時、データの件数を集計する
                        (SELECT NVL(COUNT(*),0) -- データの件数
                           FROM S02M048T TSSM -- 得意先設置サーバー管理マスタ
                          WHERE TSSM.APKIHONCD = TUSM.APKIHONCD
                            AND TSSM.SHOPCD = TUSM.SHOPCD
                            AND TSSM.TOKUISAKICD = TUSM.TOKUISAKICD -- 得意先コード：定例売上仕入マスタの得意先コード
                            AND TSSM.YUKOKBN = '1' --  有効区分
              AND TSSM.KEIJOKBN != '01' -- 買取（売上）サーバ以外
              --レンタル開始日、引き上げは引き上げ日がNullかで判断
              -- 設置日の年月 ≦ 次回計上年月
              AND TO_CHAR(TO_DATE(TSSM.RTLSTRTDD, 'YYYYMMDD'), 'YYYYMM') <= TUSM.JIKAIKEIJODYDM
              AND (TSSM.HIKIAGEDD IS NULL)
            ))SURYO, -- 数量

            DECODE(TUSM.RTLRKNSHUKEIKBN,
                   1, -- 定例売上仕入マスタのレンタル料金集計区分が有のとき
                   0,
                   NVL(SHMV.SHINHYOJUNPRICE, 0))HYOJUNPRICE, -- 標準価格

            DECODE(TUSM.RTLRKNSHUKEIKBN,
                   0, -- 定例売上仕入マスタのレンタル料金集計区分が無のとき
                   DECODE(TUSM.SHOKAIKEIJODYDM, -- 初回計上年月
                          TUSM.JIKAIKEIJODYDM, -- 次回計上年月,
                          CASE WHEN TUSM.SHOKAIKIN = 0 OR TUSM.SHOKAIKIN IS NULL THEN
                          NVL(TUSM.HYOJUNPRICE,0) -- 標準価格
                          ELSE TUSM.SHOKAIUNT -- 初回単価
                          END,
                          NVL(TUSM.HYOJUNPRICE,0) -- 標準価格
                          ),
                   1, -- 定例売上仕入マスタのレンタル料金集計区分が有の時、０を設定する
                   0) UNT, -- 単価

             DECODE(TUSM.RTLRKNSHUKEIKBN,
                        0, -- 定例売上仕入マスタのレンタル料金集計区分が無のとき
                        DECODE(TUSM.SHOKAIKEIJODYDM, -- 初回計上年月
                               TUSM.JIKAIKEIJODYDM, -- 次回計上年月,
                               CASE WHEN TUSM.SHOKAIKIN = 0 OR TUSM.SHOKAIKIN IS NULL THEN
                               NVL(TUSM.KIN,0) -- 金額
                               ELSE TUSM.SHOKAIKIN -- 初回金額
                               END,
                               NVL(TUSM.KIN,0) -- 金額
                               ),
                        1, -- 定例売上仕入マスタのレンタル料金集計区分が有の時、レンタル料金の集計値
                        CASE WHEN TUSM.KEIJOKBN = '03' THEN 0 ELSE -- 計上区分がサービスの場合は0を設定
                        (SELECT NVL(SUM(TSSM.RTLRKN),0) -- レンタル料金
                           FROM S02M048T TSSM -- 得意先設置サーバー管理マスタ
                          WHERE TSSM.APKIHONCD = TUSM.APKIHONCD
                            AND TSSM.SHOPCD = TUSM.SHOPCD
                            AND TSSM.TOKUISAKICD = TUSM.TOKUISAKICD -- 得意先コード：定例売上仕入マスタの得意先コード
                            AND TSSM.YUKOKBN = '1' --  有効区分
              AND TSSM.KEIJOKBN != '01' -- 買取（売上）サーバ以外
              -- 設置日の年月 ≦ 次回計上年月
              AND TO_CHAR(TO_DATE(TSSM.RTLSTRTDD, 'YYYYMMDD'), 'YYYYMM') <= TUSM.JIKAIKEIJODYDM
              -- 引上日がNULL
              AND (TSSM.HIKIAGEDD IS NULL)
                        ) END
            )KIN, -- 金額
             CASE WHEN SHMV.TAXKBN IN ('4','5') AND TUSM.JIKAIKEIJODYDM <= '201909' THEN '1' ELSE SHMV.TAXKBN END,

             TUSM.MEISAIBIKO MEISAIBIKO, -- 明細備考
             TUSM.KATASIKI KATASIKI, -- 型式
             SHMV.TAXSETTEIKBN TAXSETTEIKBN,
             TUSM.TEIREIHASSEINO TEIREIHASSEINO, -- 定例発生番号
             TUSM.JIKAIKEIJODYDM, -- 次回計上年月
             TUSM.SHRYDYDM,              -- 終了年月
             TUSM.SHOKAIKEIJODYDM,       -- 初回計上年月
             TUSM.HASSEIKANKAKUKEIJOPTN, -- 発生間隔計上パターン
             TUSM.KEIJODD, -- 計上日
             -- インボイス対応変更 start ABI
             SKSM.KAISHUHOHOKBN, -- 回収方法区分
             SHPM.INVOICSHIKOYMD, -- インボイス施工日
             TKSM.TOKUISAKIKBN, --   得意先区分
             TKSM.JUTAKUDLVRYCSTKBN, -- 受託配達顧客区分
             TUSM.RTLRKNSHUKEIKBN  -- レンタル料金集計区分
        FROM s02m016t TUSM -- 定例売上仕入マスタ
        INNER JOIN s02m013t TKSM      -- 得意先マスタ
      ON TKSM.APKIHONCD = TUSM.APKIHONCD
      AND TKSM.SHOPCD = TUSM.SHOPCD
      AND TKSM.TOKUISAKICD = TUSM.TOKUISAKICD
        LEFT JOIN s02m017t SKSM -- 請求先マスタ
      ON TKSM.APKIHONCD = SKSM.APKIHONCD
      AND TKSM.SHOPCD = SKSM.SHOPCD
      AND TKSM.SKYSAKICD = SKSM.SKYSAKICD
        -- インボイス対応変更 end ABI
        LEFT JOIN s02v001t SHMV -- 商品マスタビュー
      ON TUSM.APKIHONCD = SHMV.APKIHONCD
      AND TUSM.SHOPCD = SHMV.SHOPCD
      AND TUSM.SHNCD = SHMV.SHNCD
        LEFT JOIN S02M046T SHPM -- ショップマスタ
      ON TUSM.APKIHONCD = SHPM.APKIHONCD
      AND TUSM.SHOPCD = SHPM.SHOPCD
       WHERE TUSM.APKIHONCD = iApKihoncd
         AND TUSM.SHOPCD = iShopcd
            -- 次回計上年月+計上日 <= 計上日
         AND (TUSM.JIKAIKEIJODYDM || CASE WHEN TO_CHAR(Last_day(TO_DATE(TUSM.JIKAIKEIJODYDM, 'YYYYMM')), 'DD') <= TUSM.KEIJODD THEN TO_CHAR(Last_day(TO_DATE(TUSM.JIKAIKEIJODYDM, 'YYYYMM')), 'DD') ELSE TUSM.KEIJODD END) <= iKeijodd
            -- 初回計上年月 <= 計上日の年月
         AND TUSM.SHOKAIKEIJODYDM <=
             to_char(to_date(iKeijodd, 'yyyymmdd'), 'yyyymm')
            -- 終了年月 >= 計上日の年月
         AND TUSM.SHRYDYDM >=
             to_char(to_date(iKeijodd, 'yyyymmdd'), 'yyyymm')
         AND TUSM.APKIHONCD = TKSM.APKIHONCD
         AND TUSM.SHOPCD = TKSM.SHOPCD
         AND TUSM.TOKUISAKICD = TKSM.TOKUISAKICD -- 得意先コード
         AND TUSM.URISIIREKBN = '1' --  売上仕入区分
         AND TUSM.DNPYHASSEIARINASHIFLG = '1' --  伝票発生有無フラグ
         AND TUSM.YUKOKBN = '1' --  有効区分
         -- インボイス対応変更 start ABI
         AND ( ( TUSM.TEIREIKBN <> '4' AND TKSM.JOTAISTS = '07' ) -- 定例区分：アクアクララ光以外；状態ステータス：継続中
         -- インボイス対応変更 end ABI
               OR
               ( TUSM.TEIREIKBN = '4'                                  -- 定例区分：アクアクララ光；請求停止データなし
                 AND NOT EXISTS ( SELECT * FROM S02M061T KYSM
                                   WHERE KYSM.APKIHONCD     = TUSM.APKIHONCD
                                     AND KYSM.SHOPCD        = TUSM.SHOPCD
                                     AND KYSM.TOKUISAKICD   = TUSM.TOKUISAKICD
                                     AND KYSM.YUKOKBN       = '1'
                                     AND KYSM.SKYTEISHIKBN  = '1'
                                     AND KYSM.SKYTEISHIDYDM <= TUSM.JIKAIKEIJODYDM
                                )
                )
             )

       ORDER BY TUSM.TOKUISAKICD, -- 得意先コード
                TUSM.JIKAIKEIJODYDM, --次回計上年月
                TUSM.KEIJODD, -- 計上日
                TUSM.TORIHIKIKBN, -- 取引区分
                -- インボイス対応変更 start ABI
                --TUSM.NYUKAMOTOCD, -- 入荷元コード
                SKSM.KAISHUHOHOKBN, -- 回収方法区分
                -- インボイス対応変更 end ABI
              TUSM.TEIREIHASSEINO; -- 定例発生番号

  BEGIN
    -- 入力パラメータチェック
    IF iApKihoncd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20001,
                              'パラメータ.AP基本コード が設定されていません。');
    END IF;
    IF iShopcd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20002,
                              'パラメータ.ショップコード  が設定されていません。');
    END IF;
    IF iUserApKihoncd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20001,
                              'パラメータ.担当者ＡＰ基本コード が設定されていません。');
    END IF;
    IF iUserShopcd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20002,
                              'パラメータ.担当者ショップコード  が設定されていません。');
    END IF;
    IF iKeijodd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20002,
                              'パラメータ.計上日  が設定されていません。');
    END IF;
    IF iTnshacd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20008,
                              'パラメータ.担当者コード が設定されていません。');
    END IF;
    IF iIpadrse IS NULL THEN
      RAISE_APPLICATION_ERROR(-20009,
                              'パラメータ.ＩＰアドレス が設定されていません。');
    END IF;
    -- 基本情報設定
    SetUserInfo(iApKihoncd, iShopcd,iUserApKihoncd,
                    iUserShopcd,iTnshacd, iIpadrse);

    -- 定例売上仕入マスタ抽出（売上）
    OPEN SELECT_URIDATE;
    LOOP
      FETCH SELECT_URIDATE
        INTO

      wRecordS06T001T.TORIHIKIKBN, --売上テーブル.取引区分
      wRecordS06T001T.TOKUISAKICD, --売上テーブル.得意先コード
      wRecordS06T001T.NYUKAMOTOCD, --売上テーブル.入荷元コード
      wRecordS06T001T.URIDD, --売上テーブル.売上日
      wRecordS06T001T.ACPTTNSHACD, -- 売上テーブル.受注担当者コード
      wRecordS06T001T.EIGYOTNSHACD, -- 売上テーブル.営業担当者コード
      wRecordS06T001T.JISSEKITNSHACD, -- 売上テーブル.実績担当者コード
      wRecordS06T001T.SKYSAKICD, -- 売上テーブル.請求先コード
      wRecordS06T001T.TAXKEISANKBN, -- 売上テーブル.税計算区分
      vRecordS06T002T.LCTGRYCD, -- 売上明細テーブル.大分類コード
      vRecordS06T002T.MCTGRYCD, -- 売上明細テーブル.中分類コード
      vRecordS06T002T.SCTGRYCD, -- 売上明細テーブル.小分類コード
      vRecordS06T002T.SHNCD, --売上明細テーブル.商品コード
      vRecordS06T002T.KEIJOKBN, -- 売上明細テーブル.計上区分
      vRecordS06T002T.SURYO, --売上明細テーブル.数量
      vRecordS06T002T.HYOJUNUNT, --売上明細テーブル.標準単価
      vRecordS06T002T.UNT, --売上明細テーブル.単価
      vRecordS06T002T.KIN, -- 売上明細テーブル.金額
      vRecordS06T002T.TAXKBN, -- 売上明細テーブル.税区分
      vRecordS06T002T.MEISAIBIKO, -- 売上明細テーブル.明細備考
      vRecordS06T002T.KATASIKI, -- 売上明細テーブル.型式
      wTaxsetteikbn, -- 税設定区分
      wTeireihasseino, --定例発生番号
      wJikaikeijodydm, -- 次回計上年月
      wShryDYDM,              -- 終了年月
      wShokaiKeijoDYDM,       -- 初回計上年月
      wHasseiKankakuKeijoPTN, -- 発生間隔計上パターン
      wKeijodd,               -- 計上日
      -- インボイス対応変更 start ABI
      wKaishuhohokbn,         -- 回収方法
      wInvoicshikoymd,        -- インボイス施工日
      wTokuisakikbn,          -- 得意先区分
      wJutakudlvrycstkbn,     -- 受託配達顧客区分
      wRtlRknShukeiKbn;       -- レンタル料金集計区分
      -- インボイス対応変更 end ABI
      IF SELECT_URIDATE%ROWCOUNT = 0 THEN
        EXIT;
      ELSE
        -- 終了時
        IF SELECT_URIDATE%NOTFOUND THEN
          -- インボイス対応変更 start ABI
          -- 回収方法区分が７：代引きの場合
          IF wKaishuhohokbn2 = '7' THEN
            taxPrice := Z1808B01.SyohizeiKeisan('1', --単体計算フラグ（する）
                                         '1', --税設定区分
                                         '1', --税区分：課税
                                         '2', --税計算区分：外税
                                         vRecordS06T001T.URIDD, --切り替え日：売上テーブルの売上日
                                         vRecordS06T001T.APKIHONCD, --AP基本コード
                                         vRecordS06T001T.SHOPCD, --ショップコード
                                         NULL, --仕入先コード
                                         NULL, --数量
                                         kinSum1); -- 金額合計(税区分１)
            IF taxPrice != 0 THEN
              -- 売上伝票明細番号設定
              wUridnpymeisaino                := NVL(wUridnpymeisaino, 0) + 1;
              vRecordS06T002T.URIDNPYMEISAINO := wUridnpymeisaino;
              -- 売上明細テーブル登録
              InsertUMSTByTax('1', taxPrice, kinSum1, wTokuisakikbn2, wJutakudlvrycstkbn2);
              -- 商品レコード + 消費税レコードの合計金額
              vRecordS06T001T.SHOHITAXKIN := NVL(vRecordS06T001T.SHOHITAXKIN, 0) + NVL(taxPrice, 0);
            END IF;
            taxPrice := Z1808B01.SyohizeiKeisan('1', --単体計算フラグ（する）
                                         '1', --税設定区分
                                         '4', --税区分：非課税
                                         '2', --税計算区分：外税
                                         vRecordS06T001T.URIDD, --切り替え日：売上テーブルの売上日
                                         vRecordS06T001T.APKIHONCD, --AP基本コード
                                         vRecordS06T001T.SHOPCD, --ショップコード
                                         NULL, --仕入先コード
                                         NULL, --数量
                                         kinSum4); -- 金額合計(税区分４)
            IF taxPrice != 0 THEN
              -- 売上伝票明細番号設定
              wUridnpymeisaino                := NVL(wUridnpymeisaino, 0) + 1;
              vRecordS06T002T.URIDNPYMEISAINO := wUridnpymeisaino;
              -- 売上明細テーブル登録
              InsertUMSTByTax('4', taxPrice, kinSum4, wTokuisakikbn2, wJutakudlvrycstkbn2);
              -- 商品レコード + 消費税レコードの合計金額
              vRecordS06T001T.SHOHITAXKIN := NVL(vRecordS06T001T.SHOHITAXKIN, 0) + NVL(taxPrice, 0);
            END IF;
          END IF;
          -- インボイス対応変更 end ABI
          -- 売上計上テーブル登録
          InsertURGT(wKaishuhohokbn2, wInvoicshikoymd);
          EXIT;
        ELSE
          -- 初期化の時、
          IF SELECT_URIDATE%ROWCOUNT = 1 THEN
            -- TYYMM999999のYYMMは年月、999999は連番,自動採番はＯＲＡＣＬＥ用採番共通部品を呼び出します。
            GetUridnpyno;
            -- 売上テーブル設置
            SetURGT(wRecordS06T001T);
            wJikaikeijodydm2     := wJikaikeijodydm;     -- 次回計上年月
            wKeijodd2            := wKeijodd;            -- 計上日
            -- インボイス対応変更 start ABI
            wKaishuhohokbn2      := wKaishuhohokbn;      -- 回収方法区分
            wTokuisakikbn2       := wTokuisakikbn;       -- 得意先区分
            wJutakudlvrycstkbn2  := wJutakudlvrycstkbn;  -- 受託配達顧客区分
            -- インボイス対応変更 end ABI
          END IF;
        END IF;
      END IF;

      -- 同一得意先・仕入先コードの定例売上で、仕入先コード、取引区分、次回計上年月、計上日、回収方法区分が同じ場合は、一伝票に複数明細のデータとして作成する。
      IF vRecordS06T001T.TOKUISAKICD = wRecordS06T001T.TOKUISAKICD AND -- 得意先コード
         vRecordS06T001T.TORIHIKIKBN = wRecordS06T001T.TORIHIKIKBN AND -- 取引区分
         wJikaikeijodydm2 = wJikaikeijodydm AND -- 次回計上年月
         wKeijodd2 = wKeijodd AND -- 計上日
         -- インボイス対応変更 start ABI
         --vRecordS06T001T.NYUKAMOTOCD = wRecordS06T001T.NYUKAMOTOCD -- 入荷元コード
         wKaishuhohokbn2 = wKaishuhohokbn -- 回収方法区分
         -- インボイス対応変更 end ABI
       THEN
        -- 売上伝票明細番号設定
        wUridnpymeisaino                := NVL(wUridnpymeisaino, 0) + 1;
        vRecordS06T002T.URIDNPYMEISAINO := wUridnpymeisaino;
      ELSE
        -- 売上テーブル登録
        IF SELECT_URIDATE%ROWCOUNT <> 1 THEN
          -- インボイス対応変更 start ABI
          -- 回収方法区分が７：代引きの場合
          IF wKaishuhohokbn2 = '7' THEN
            taxPrice := Z1808B01.SyohizeiKeisan('1', --単体計算フラグ（する）
                                         '1', --税設定区分
                                         '1', --税区分：課税
                                         '2', --税計算区分：外税
                                         vRecordS06T001T.URIDD, --切り替え日：売上テーブルの売上日
                                         vRecordS06T001T.APKIHONCD, --AP基本コード
                                         vRecordS06T001T.SHOPCD, --ショップコード
                                         NULL, --仕入先コード
                                         NULL, --数量
                                         kinSum1); -- 金額合計(税区分１)
            IF taxPrice != 0 THEN
              -- 売上伝票明細番号設定
              wUridnpymeisaino                := NVL(wUridnpymeisaino, 0) + 1;
              vRecordS06T002T.URIDNPYMEISAINO := wUridnpymeisaino;
              -- 売上明細テーブル登録
              InsertUMSTByTax('1', taxPrice, kinSum1, wTokuisakikbn2, wJutakudlvrycstkbn2);
              -- 商品レコード + 消費税レコードの合計金額
              vRecordS06T001T.SHOHITAXKIN := NVL(vRecordS06T001T.SHOHITAXKIN, 0) + NVL(taxPrice, 0);
            END IF;
            taxPrice := Z1808B01.SyohizeiKeisan('1', --単体計算フラグ（する）
                                         '1', --税設定区分
                                         '4', --税区分：非課税
                                         '2', --税計算区分：外税
                                         vRecordS06T001T.URIDD, --切り替え日：売上テーブルの売上日
                                         vRecordS06T001T.APKIHONCD, --AP基本コード
                                         vRecordS06T001T.SHOPCD, --ショップコード
                                         NULL, --仕入先コード
                                         NULL, --数量
                                         kinSum4); -- 金額合計(税区分４)
            IF taxPrice != 0 THEN
              -- 売上伝票明細番号設定
              wUridnpymeisaino                := NVL(wUridnpymeisaino, 0) + 1;
              vRecordS06T002T.URIDNPYMEISAINO := wUridnpymeisaino;
              -- 売上明細テーブル登録
              InsertUMSTByTax('4', taxPrice, kinSum4, wTokuisakikbn2, wJutakudlvrycstkbn2);
              -- 商品レコード + 消費税レコードの合計金額
              vRecordS06T001T.SHOHITAXKIN := NVL(vRecordS06T001T.SHOHITAXKIN, 0) + NVL(taxPrice, 0);
            END IF;
          END IF;
          -- インボイス対応変更 end ABI
          InsertURGT(wKaishuhohokbn2, wInvoicshikoymd);
        END IF;
        -- 売上テーブル設置
        SetURGT(wRecordS06T001T);
        -- インボイス対応変更 start ABI
        wJikaikeijodydm2            := wJikaikeijodydm; -- 次回計上年月
        wKeijodd2                   := wKeijodd; -- 計上日
        wKaishuhohokbn2             := wKaishuhohokbn; -- 回収方法区分
        wTokuisakikbn2              := wTokuisakikbn;       -- 得意先区分
        wJutakudlvrycstkbn2         := wJutakudlvrycstkbn;  -- 受託配達顧客区分
        vRecordS06T001T.SHOHITAXKIN := 0; -- 売上テーブル.消費税金額
        vRecordS06T001T.URIKIN      := 0; -- 売上テーブル.売上金額
        vRecordS06T001T.SANKOUSHOHITAXKIN := 0; -- 売上テーブル.参考消費税金額
        taxPrice                    := 0; -- 課税料金
        kinSum1 := 0; -- 売上明細テーブル.売上金額合計(税区分１：課税)を初期化
        kinSum4 := 0; -- 売上明細テーブル.売上金額合計(税区分４：軽減税)を初期化
        -- インボイス対応変更 end ABI
        -- 売上伝票明細番号設定
        wUridnpymeisaino                := 1;
        vRecordS06T002T.URIDNPYMEISAINO := wUridnpymeisaino;
      END IF;

    IF GetSeigyoFlg(iApKihoncd, iShopcd) = 1 THEN
        -- 明細備考編集：アクアクララ光の場合
        IF IsHikari(vRecordS06T002T.LCTGRYCD, vRecordS06T002T.MCTGRYCD) THEN
           -- 発生間隔の計算
           wHasseiKankaku := 1;
           IF wHasseiKankakuKeijoPTN = '1' THEN wHasseiKankaku := 1;  END IF; -- 毎月
           IF wHasseiKankakuKeijoPTN = '2' THEN wHasseiKankaku := 3;  END IF; -- ３ヶ月
           IF wHasseiKankakuKeijoPTN = '3' THEN wHasseiKankaku := 6;  END IF; -- ６ヶ月
           IF wHasseiKankakuKeijoPTN = '4' THEN wHasseiKankaku := 12; END IF; -- １年

           -- 総回数の計算
           wSoKaisu := FLOOR( MONTHS_BETWEEN(TO_DATE(wShryDYDM, 'YYYYMM'), TO_DATE(wShokaiKeijoDYDM, 'YYYYMM')) / wHasseiKankaku ) + 1;
           -- 発生回数計算
           wHasseiKaisu := MONTHS_BETWEEN(TO_DATE(wJikaikeijodydm, 'YYYYMM'), TO_DATE(wShokaiKeijoDYDM, 'YYYYMM')) / wHasseiKankaku + 1;

           vRecordS06T002T.MEISAIBIKO :=  wHasseiKaisu || '回／' || wSoKaisu || '回中　' || SUBSTR( vRecordS06T002T.MEISAIBIKO, 1, 16) ;
        ELSE
        vRecordS06T002T.MEISAIBIKO :=  SUBSTR(wRecordS06T001T.URIDD,1,4) || '年' || SUBSTR(wRecordS06T001T.URIDD,5,2) || '月分　'  || SUBSTRB(vRecordS06T002T.MEISAIBIKO,1,16); -- 更新運用日付
        END IF;
    END IF;

      -- 売上明細テーブル登録
      InsertUMST(wTaxsetteikbn, wKaishuhohokbn, wInvoicshikoymd, wTokuisakikbn, wJutakudlvrycstkbn);
      -- インボイス対応変更 start ABI
      -- 売上明細テーブルに登録した税区分に応じて金額合計を保持する
      -- 税区分が１：課税の場合
      IF vRecordS06T002T.TAXKBN = '1' THEN
        kinSum1 := NVL(kinSum1, 0) +
                  NVL(vRecordS06T002T.KIN, 0); --金額合計(税区分１)
      END IF;
      -- 税区分が４：軽減税の場合
      IF vRecordS06T002T.TAXKBN = '4' THEN
        kinSum4 := NVL(kinSum4, 0) +
                   NVL(vRecordS06T002T.KIN, 0); --金額合計(税区分４)
      END IF;
      -- インボイス対応変更 end ABI

      -- ポイント制度対応追加 start ABI
      -- 予定発生ポイントテーブル、予定発生ポイント明細テーブル、予定発生ポイント明細計算詳細テーブル
      InsertPoint(iApKihoncd, iShopcd, wRtlRknShukeiKbn);
      -- ポイント制度対応追加 end ABI
    END LOOP;
    CLOSE SELECT_URIDATE;
    -- 定例売上仕入マスタ更新（売上）
    UpdateTUSM(iKeijodd);
    -- 共通変数削除
    vRecordS06T001T := null;
    vRecordS06T002T := null;
    --異常処理
  EXCEPTION
    WHEN OTHERS THEN
      -- カーソル オーペンであれば、クローズする
      IF (SELECT_URIDATE%ISOPEN) THEN
        CLOSE SELECT_URIDATE;
      END IF;
      -- 共通変数削除
      vRecordS06T001T := null;
      vRecordS06T002T := null;
      RAISE_APPLICATION_ERROR(-20705, SQLERRM);
  END TeireiUriage;

END C1001B02;
/
exit;
