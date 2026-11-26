CREATE OR REPLACE PROCEDURE      G1901B01
/*******************************************************************************
 * システム名       ：ＡＰ総合管理システム
 * 機能名           ：請求前クレンジングバッチ
 * 引数             ：iApKihoncd : AP基本コード
 *                  ：iShopcd    : ショップコード
 *
 ******************************************************************************T
   Date       Author         Changes Key                                 Version
   -----------------------------------------------------------------------------
   2023-03-01 ABI)Ishii      Create                                      1.0.0
 ******************************************************************************/
( iApKihonCd IN VARCHAR2
, iShopCd    IN VARCHAR2
) IS
  -- 定数
  PGMID             VARCHAR2(8)   :=  'G1901B01';
  TNSHACD           VARCHAR2(6)   :=  'NITIJI';
  -- 変数
  sIpAddress        S02M013T.KSHNIPADRSE%type;
  sSysDate          S02M013T.KSHNSYSTMYMD%type;
  sSysTime          S02M013T.KSHNSYSTMJIKAN%type;
  sApKihonCdFrom    S02M013T.APKIHONCD%type;
  sApKihonCdTo      S02M013T.APKIHONCD%type;
  sShopCdFrom       S02M013T.SHOPCD%type;
  sShopCdTo         S02M013T.SHOPCD%type;
  sUnyoYmd          S02M013T.KSHNUNYOYMD%TYPE;
  nZandaka          NUMBER :=  0;
  nKurikosiKin      NUMBER :=  0;
  nUriKin           NUMBER :=  0;
  nNyuuKin          NUMBER :=  0;
  nCntFetch1        NUMBER        :=  0;
  nCntUpd1          NUMBER        :=  0;

  sApKihonCd        S02M013T.APKIHONCD%type;    --ＡＰ基本コード
  sShopCd           S02M013T.SHOPCD%type;       --ショップコード
  sUnyoYm           VARCHAR2(6);                --運用日付の年月
  nCntFetch2        NUMBER := 0;
  nCntUpd2          NUMBER := 0;
  nCntUpd3          NUMBER := 0;

  sSkyshimeymd      VARCHAR2(8);                --売上テーブル.売上日と請求先マスタ.請求締日から算出する請求締年月日
  sYyyymm           VARCHAR2(6);                --請求締年月日算出用ワーク変数（年月を入れる）

  -- ショップマスタデータ取得用カーソル
  CURSOR SELECT_SHOP(pApKihonCdFrom VARCHAR2, pApKihonCdTo VARCHAR2, pShopCdFrom VARCHAR2, pShopCdTo VARCHAR2) IS
    SELECT
       SPM.APKIHONCD
      ,SPM.SHOPCD
      ,SPM.INVOICSHIKOYMD
    FROM
      S02M046T SPM
    WHERE
        SPM.APKIHONCD BETWEEN pApKihonCdFrom AND pApKihonCdTo
    AND SPM.SHOPCD    BETWEEN pShopCdFrom    AND pShopCdTo
    AND SPM.YUKOKBN   = '1'
  ;

  -- 売上情報データ取得用カーソル
  CURSOR SELECT_URIAGE(pApKihonCd VARCHAR2, pShopCd VARCHAR2, pUnyoYm VARCHAR2) IS
    SELECT /*+ INDEX_SS(URA S06T001T_IX4) INDEX(SKZ S07T001T_UQ1) */
        TKM.TOKUISAKICD                                -- 得意先コード
        ,SKM.SKYSAKICD                                 -- 請求先コード
        ,URA.URIDNPYNO                                 -- 売上伝票番号
        ,URA.SHOHITAXKIN                               -- 消費税金額
        ,URA.URIDD                                     -- 売上日
        ,SKM.SKYSHIMEDD                                -- 請求先マスタ.請求締日
    FROM
        --請求先マスタ
        S02M017T SKM
    INNER JOIN
        --得意先マスタ
        S02M013T TKM
    ON  TKM.APKIHONCD  = pApKihonCd                    -- ＡＰ基本コード
    AND TKM.SHOPCD     = pShopCd                       -- ショップコード
    AND TKM.SKYSAKICD     = SKM.SKYSAKICD              -- 請求先コード
    AND TKM.YUKOKBN    = '1'                           -- 有効区分（「1:有効」）
    INNER JOIN
        --売上テーブル
        S06T001T URA
    ON  URA.APKIHONCD  = pApKihonCd                    -- ＡＰ基本コード
    AND URA.SHOPCD     = pShopCd                       -- ショップコード
    AND URA.SKYSAKICD     = SKM.SKYSAKICD              -- 請求先コード
    AND URA.TOKUISAKICD     = TKM.TOKUISAKICD          -- 得意先コード
    AND URA.YUKOKBN    = '1'                           -- 有効区分（「1:有効」）

    WHERE
        SKM.APKIHONCD         =  pApKihonCd             -- ＡＰ基本コード
    AND SKM.SHOPCD            =  pShopCd                -- ショップコード
    AND SKM.KAISHUHOHOKBN     <> '7'                    -- 回収方法区分（「7:代引」以外）
    AND SKM.YUKOKBN           =  '1'                    -- 有効区分（「1:有効」）
    AND URA.URIKIN            != 0                      -- 売上金額（0ではない）
    AND URA.SHOHITAXKIN       != 0                      -- 消費税金額（0ではない）
    AND URA.SANKOUSHOHITAXKIN =  0                      -- 参考消費税額（0であること）
    AND URA.SKYPRCSJISSIDD    =  '00000000'             -- 請求締処理実施日（未締めを対象とする）
    AND URA.URIDD             > (                       -- 売上日（インボイス施行日の一月前よりあと）
        SELECT
        TO_CHAR(ADD_MONTHS(INVOICSHIKOYMD, -1), 'YYYYMMDD')
        FROM S02M046T
        WHERE
            APKIHONCD = pApKihonCd                     -- ＡＰ基本コード
            AND SHOPCD = pShopCd                       -- ショップコード
        )
    ;

BEGIN
/* 処理を書く↓ */
  -- DBサーバーのIPアドレス取得
  sIpAddress := S01GetIpAddress();

  -- 開始ログ出力
  S0199Q01(PGMID, iApKihonCd, iShopCd, '2', 'S', '0', NULL, 0, iApKihonCd, iShopCd, TNSHACD, sIpAddress, PGMID, NULL);

  -- システム日時を取得
  SELECT  to_char(CURRENT_DATE, 'yyyymmdd')
        , '235959'
    INTO  sSysDate
        , sSysTime
    FROM dual;

  -- カーソル条件範囲設定
  --ＡＰ基本コード設定
  IF iApKihonCd IS NOT NULL THEN
    sApKihonCdFrom := iApKihonCd;
    sApKihonCdTo   := iApKihonCd;
  ELSE
    sApKihonCdFrom := '000';
    sApKihonCdTo   := '999';
  END IF;
  --ショップコード設定
  IF iShopCd IS NOT NULL THEN
    sShopCdFrom := iShopCd;
    sShopCdTo   := iShopCd;
  ELSE
    sShopCdFrom := '000';
    sShopCdTo   := '999';
  END IF;

  -- ショップマスタ対象データFetch
  FOR vCur1 IN SELECT_SHOP (sApKihonCdFrom, sApKihonCdTo, sShopCdFrom, sShopCdTo) LOOP
  
    --検索件数UP
    nCntFetch1 := nCntFetch1 + 1;

    -- 運用日付取得
    sUnyoYmd := S01GetOperationdate(vCur1.APKIHONCD, vCur1.SHOPCD);
    sUnyoYm  := TO_CHAR(TO_DATE(sUnyoYmd, 'YYYYMMDD'), 'YYYYMM');

    --クレンジング対象データFetch
    FOR vCur2 IN SELECT_URIAGE (vCur1.APKIHONCD, vCur1.SHOPCD, sUnyoYm) LOOP

        --検索件数UP
        nCntFetch2 := nCntFetch2 + 1;

        -- 売上日と請求締日より売上年月の請求締年月日を算出
        sYyyymm := TO_CHAR(TO_DATE(vCur2.URIDD, 'YYYYMMDD'), 'YYYYMM');
        IF (vCur2.SKYSHIMEDD = '31') THEN
          -- 請求締日が月末を示す「31」の場合は月末を考慮して算出
          sSkyshimeymd := TO_CHAR(LAST_DAY(TO_DATE(sYyyymm || '01', 'YYYYMMDD')), 'YYYYMMDD');
        ELSE
          -- 上記以外の場合は文字列の連結
          sSkyshimeymd := sYyyymm || vCur2.SKYSHIMEDD;
        END IF;
        
        -- 売上日が請求締年月日以降の場合は翌月の請求締年月日を算出
        IF (vCur2.URIDD > sSkyshimeymd) THEN
          IF (vCur2.SKYSHIMEDD = '31') THEN
            -- 請求締日が月末を示す「31」の場合は月末を考慮して算出
            sSkyshimeymd := TO_CHAR(LAST_DAY(ADD_MONTHS(TO_DATE(sYyyymm || '01', 'YYYYMMDD'), 1)), 'YYYYMMDD');
          ELSE
            sSkyshimeymd := TO_CHAR(ADD_MONTHS(TO_DATE(sSkyshimeymd, 'YYYYMMDD'), 1), 'YYYYMMDD');
          END IF;
        END IF;

        -- 算出した請求締年月日がインボイス施行日より前の場合はクレンジング対象外としてスキップ
        IF (sSkyshimeymd < vCur1.INVOICSHIKOYMD) THEN
          CONTINUE;
        END IF;

        -- 売上テーブル更新
        UPDATE S06T001T URA
        SET
            URA.SANKOUSHOHITAXKIN   = URA.SHOHITAXKIN     --参考消費税金額
            ,URA.SHOHITAXKIN   = 0     --消費税金額
            ,URA.KOSHAAPKIHONCD       = vCur1.APKIHONCD     --更新者ＡＰ基本コード
            ,URA.KOSHASHOPCD          = vCur1.SHOPCD        --更新者ショップコード
            ,URA.KOSHACD              = TNSHACD             --更新者コード
            ,URA.KSHNSYSTMYMD         = sSysDate            --更新システム日付
            ,URA.KSHNSYSTMJIKAN       = sSysTime            --更新システム時間
            ,URA.KSHNUNYOYMD          = sUnyoYmd            --更新運用日付
            ,URA.KSHNIPADRSE          = sIpAddress          --更新ＩＰアドレス
            ,URA.KSHNPGMID            = PGMID               --更新プログラムＩＤ
        WHERE
            URA.APKIHONCD   = vCur1.APKIHONCD               --ＡＰ基本コード
        AND URA.SHOPCD      = vCur1.SHOPCD                  --ショップコード
        AND URA.URIDNPYNO = vCur2.URIDNPYNO             --売上伝票番号
        ;

        --更新件数UP
        nCntUpd1 := nCntUpd1 + SQL%ROWCOUNT;


        -- 売上明細テーブル更新
        UPDATE S06T002T URM
        SET
            URM.SANKOUSHOHITAXKIN   = URM.SHOHITAXKIN     --参考消費税金額
            ,URM.SHOHITAXKIN   = 0     --消費税金額
            ,URM.KOSHAAPKIHONCD       = vCur1.APKIHONCD     --更新者ＡＰ基本コード
            ,URM.KOSHASHOPCD          = vCur1.SHOPCD        --更新者ショップコード
            ,URM.KOSHACD              = TNSHACD             --更新者コード
            ,URM.KSHNSYSTMYMD         = sSysDate            --更新システム日付
            ,URM.KSHNSYSTMJIKAN       = sSysTime            --更新システム時間
            ,URM.KSHNUNYOYMD          = sUnyoYmd            --更新運用日付
            ,URM.KSHNIPADRSE          = sIpAddress          --更新ＩＰアドレス
            ,URM.KSHNPGMID            = PGMID               --更新プログラムＩＤ
        WHERE
            URM.APKIHONCD   = vCur1.APKIHONCD               --ＡＰ基本コード
        AND URM.SHOPCD      = vCur1.SHOPCD                  --ショップコード
        AND URM.URIDNPYNO = vCur2.URIDNPYNO             --売上伝票番号
        ;
        --更新件数UP
        nCntUpd2 := nCntUpd2 + SQL%ROWCOUNT;

        -- 売上明細テーブルの調整消費税削除
        DELETE FROM S06T002T URM
        WHERE
            URM.APKIHONCD   = vCur1.APKIHONCD               --ＡＰ基本コード
        AND URM.SHOPCD      = vCur1.SHOPCD                  --ショップコード
        AND URM.URIDNPYNO = vCur2.URIDNPYNO             --売上伝票番号
        AND URM.SHNCD = '000134'             --商品コード
        ;

        -- 売上残高テーブルの残高を取得する
        BEGIN
            SELECT NVL(S07.KURIKOSHIKIN, 0) AS KURIKOSHIKIN
            INTO nZandaka
            FROM
                 S07T002T S07
            WHERE S07.YUKOKBN      = '1'
              AND S07.APKIHONCD    = vCur1.APKIHONCD
              AND S07.SHOPCD       = vCur1.SHOPCD
              AND S07.TOKUISAKICD  = vCur2.TOKUISAKICD
              AND S07.SHIMEDYDM || S07.SHIMEDD = (select max(t2.SHIMEDYDM || t2.SHIMEDD)
                                                    from S07T002T t2
                                                   where t2.YUKOKBN = '1'
                                                     and t2.APKIHONCD   = vCur1.APKIHONCD
                                                     and t2.SHOPCD      = vCur1.SHOPCD
                                                     and t2.TOKUISAKICD = vCur2.TOKUISAKICD);
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
            nZandaka := 0;
        END;

        -- 売上テーブルの未締金額を取得する
        BEGIN
            SELECT SUM(NVL(S06.URIKIN, 0) + NVL(S06.SHOHITAXKIN, 0)) AS URIKIN
            INTO nUriKin
            FROM
                  S06T001T S06
            WHERE S06.YUKOKBN            = '1'
               AND S06.APKIHONCD          = vCur1.APKIHONCD
               AND S06.SHOPCD             = vCur1.SHOPCD
               AND S06.TOKUISAKICD        = vCur2.TOKUISAKICD
               AND S06.YUKOKBN            = '1'
               AND S06.GETSUJIPRCSJISSIDD = '00000000'
            GROUP BY  S06.APKIHONCD
                    , S06.SHOPCD
                    , S06.TOKUISAKICD;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
            nUriKin := 0;
        END;

        -- 入金明細テーブルの未締金額を取得する
        BEGIN
            SELECT SUM(NVL(S10.NYKNKIN, 0) + NVL(S10.NEBIKIKIN, 0) + NVL(S10.TESURYO, 0)) AS NYKNKIN
            INTO nNyuuKin
            FROM
              S10T001T S10
            WHERE S10.YUKOKBN            = '1'
               AND S10.APKIHONCD          = vCur1.APKIHONCD
               AND S10.SHOPCD             = vCur1.SHOPCD
               AND S10.TOKUISAKICD        = vCur2.TOKUISAKICD
               AND S10.YUKOKBN            = '1'
               AND S10.GETSUJIPRCSJISSIDD = '00000000'
            GROUP BY  S10.APKIHONCD
                    , S10.SHOPCD
                    , S10.TOKUISAKICD;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
            nNyuuKin := 0;
        END;

        -- 得意先実績テーブルの現在残高を更新する
        UPDATE S02M050T TSZ
        SET
            TSZ.GENZAIZD   = nZandaka + nUriKin - nNyuuKin  --現在残高
            ,TSZ.KOSHAAPKIHONCD       = vCur1.APKIHONCD     --更新者ＡＰ基本コード
            ,TSZ.KOSHASHOPCD          = vCur1.SHOPCD        --更新者ショップコード
            ,TSZ.KOSHACD              = TNSHACD             --更新者コード
            ,TSZ.KSHNSYSTMYMD         = sSysDate            --更新システム日付
            ,TSZ.KSHNSYSTMJIKAN       = sSysTime            --更新システム時間
            ,TSZ.KSHNUNYOYMD          = sUnyoYmd            --更新運用日付
            ,TSZ.KSHNIPADRSE          = sIpAddress          --更新ＩＰアドレス
            ,TSZ.KSHNPGMID            = PGMID               --更新プログラムＩＤ
        WHERE
            TSZ.APKIHONCD   = vCur1.APKIHONCD               --ＡＰ基本コード
        AND TSZ.SHOPCD      = vCur1.SHOPCD                  --ショップコード
        AND TSZ.TOKUISAKICD = vCur2.TOKUISAKICD             --得意先コード
        ;
        --更新件数UP
        nCntUpd3 := nCntUpd3 + SQL%ROWCOUNT;

    END LOOP;       --クレンジング対象売上伝票
  END LOOP;     --ショップ

   COMMIT;

     -- 処理終了ログ出力
  S0199Q01(PGMID, iApKihonCd, iShopCd, '2', 'E', '1', NULL, 0, iApKihonCd, iShopCd, TNSHACD, sIpAddress, PGMID, NULL);
  S0199Q01(PGMID, iApKihonCd, iShopCd, '2', 'R', '1', 'S02M046T', nCntFetch1, iApKihonCd, iShopCd, TNSHACD, sIpAddress, PGMID, NULL);
  S0199Q01(PGMID, iApKihonCd, iShopCd, '2', 'R', '1', 'S06T001T', nCntFetch2, iApKihonCd, iShopCd, TNSHACD, sIpAddress, PGMID, NULL);
  S0199Q01(PGMID, iApKihonCd, iShopCd, '2', 'U', '1', 'S06T001T', nCntUpd1, iApKihonCd, iShopCd, TNSHACD, sIpAddress, PGMID, NULL);
  S0199Q01(PGMID, iApKihonCd, iShopCd, '2', 'U', '1', 'S06T002T', nCntUpd2, iApKihonCd, iShopCd, TNSHACD, sIpAddress, PGMID, NULL);
  S0199Q01(PGMID, iApKihonCd, iShopCd, '2', 'U', '1', 'S02M050T', nCntUpd3, iApKihonCd, iShopCd, TNSHACD, sIpAddress, PGMID, NULL);

/* 処理を書く↑ */
  EXCEPTION
   WHEN OTHERS THEN
       -- 異常終了ログ出力
    S0199Q01(PGMID, iApKihonCd, iShopCd, '2', 'E', '1', NULL, 0, iApKihonCd, iShopCd, TNSHACD, sIpAddress, PGMID, SUBSTRB(SQLERRM, 1, 512));
    S0199Q01(PGMID, iApKihonCd, iShopCd, '2', 'R', '1', 'S02M046T', nCntFetch1, iApKihonCd, iShopCd, TNSHACD, sIpAddress, PGMID, NULL);
    S0199Q01(PGMID, iApKihonCd, iShopCd, '2', 'R', '1', 'S06T001T', nCntFetch2, iApKihonCd, iShopCd, TNSHACD, sIpAddress, PGMID, NULL);
    S0199Q01(PGMID, iApKihonCd, iShopCd, '2', 'U', '1', 'S06T001T', nCntUpd1, iApKihonCd, iShopCd, TNSHACD, sIpAddress, PGMID, NULL);
    S0199Q01(PGMID, iApKihonCd, iShopCd, '2', 'U', '1', 'S06T002T', nCntUpd2, iApKihonCd, iShopCd, TNSHACD, sIpAddress, PGMID, NULL);
    S0199Q01(PGMID, iApKihonCd, iShopCd, '2', 'U', '1', 'S02M050T', nCntUpd2, iApKihonCd, iShopCd, TNSHACD, sIpAddress, PGMID, NULL);
    ROLLBACK;
    RAISE_APPLICATION_ERROR(-20705, SQLERRM);

END G1901B01;
/
exit;
