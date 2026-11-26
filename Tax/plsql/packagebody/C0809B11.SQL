CREATE OR REPLACE PACKAGE BODY C0809B11
/*******************************************************************************
 * システム名       ：ＡＰ総合管理システム
 * 機能名           ：配達関連処理（配達データ作成）
 *
 *******************************************************************************
   Date       Author         Changes Key                                 Version
   -----------------------------------------------------------------------------
   2022-05-09 ABI)Yokota      Create                                      1.0.0
 ******************************************************************************/
 IS
  /*****************************************************************************/
  /* プロシージャ名   ：配達予定対象データ                                     */
  /* 引数             ：iApkihoncd            AP基本コード                     */
  /*                    iShopcd               ショップコード                   */
  /*                    iDlvrykyotencd        配達拠点コード                   */
  /*                    iDlvryytidd           配達予定日                       */
  /*                    iKoshaapkihoncd       更新者AP基本コード               */
  /*                    iKoshashopcd          更新者ショップコード             */
  /*                    ikoshacd              更新者コード                     */
  /*                    iKshnipadrse          更新IPアドレス                   */
  /*****************************************************************************/
  PROCEDURE getDeliveryPlanDate(iApkihoncd      IN VARCHAR2,    -- AP基本コード
                                iShopcd         IN VARCHAR2,    -- ショップコード
                                iDlvrykyotencd  IN VARCHAR2,    -- 配達拠点コード
                                iDlvryytidd     IN VARCHAR2,    -- 配達予定日
                                iKoshaapkihoncd IN VARCHAR2,    -- 更新者AP基本コード
                                iKoshashopcd    IN VARCHAR2,    -- 更新者ショップコード
                                ikoshacd        IN VARCHAR2,    -- 更新担当者コード
                                iKshnipadrse    IN VARCHAR2) IS -- 更新IPアドレス

    -- 変数宣言
    wUnyoYmd                  VARCHAR2(8)  := NULL;       -- 運用日付
    wDlvryytidd               VARCHAR2(8)  := NULL;       -- 配達予定日
    wMeisaiCount              NUMBER       := 0;          -- 明細件数
    wPgId                     VARCHAR2(8)  := 'C0809B00'; -- プログラムID
    wApkihoncd                VARCHAR2(3)  := NULL;       -- AP基本コード
    wShopcd                   VARCHAR2(3)  := NULL;       -- ショップコード
    wAcptdnpyno               VARCHAR2(11) := NULL;       -- 受注伝票番号
    wEdano                    VARCHAR2(2)  := NULL;       -- 枝番号
    wTokuisakicd              VARCHAR2(6)  := NULL;       -- 得意先コード
-- インボイス対応変更 strat ABI
--    wKaishuhohokbn            VARCHAR2(1)  := NULL;       -- 回収方法区分
    wSkysakicd                VARCHAR2(6)  := NULL;       -- 請求先コード
    wSingldnpyflg             VARCHAR2(1)  := NULL;       -- 単体伝票締めフラグ
    wAcptthdaibikiflg         VARCHAR2(1)  := NULL;       -- 受注時代引きフラグ
    wKairekin                 VARCHAR2(11) := NULL;       -- 過入金額
    wUrichoseikin             VARCHAR2(11) := NULL;       -- 売上調整金額
-- インボイス対応変更 end ABI
    wEdano_S05T001T           NUMBER(2,0)  := 1;          -- 枝番号
    wSysdate                  DATE         := NULL;       -- システム日付
-- ACCIS2023-6922 start ABI
    wItakuaiteacptdnpyno      VARCHAR2(11) := NULL;  -- 委託相手受注伝票番号
    wItakuaiteapkihoncd       VARCHAR2(3)  := NULL;  -- 委託相手AP基本コード
    wItakuaiteshopcd          VARCHAR2(3)  := NULL;  -- 委託相手ショップコード
    wItakuaitetokuisakicd     VARCHAR2(6)  := NULL;  -- 委託相手得意先コード
    wTokuisakikbn             VARCHAR2(1)  := NULL;  -- 得意先区分
    wJutakudlvrycstkbn        VARCHAR2(1)  := NULL;  -- 受託配達顧客区分
    isItakusaki               BOOLEAN      := FALSE; -- 委託先判定用
-- ACCIS2023-6922 end ABI

    -- 受注配達テーブル抽出SQL
    CURSOR SELECT_JCHT IS
      SELECT
        JCHT.APKIHONCD,    -- AP基本コード
        JCHT.SHOPCD,       -- ショップコード
        JCHT.ACPTDNPYNO,   -- 受注伝票番号
        JCHT.EDANO,        -- 枝番号
        JCHT.TOKUISAKICD,  -- 得意先コード
        JCHT.DLVRYYTIDD,   -- 配達予定日
-- インボイス対応変更 strat ABI
--        SKSM.KAISHUHOHOKBN -- 回収方法区分
        TKSM.SKYSAKICD,          -- 得意先マスタ.請求先コード
        JCHT.SINGLDNPYFLG,       -- 単体伝票締めフラグ
        JCHT.ACPTTHDAIBIKIFLG,   -- 受注時代引きフラグ
        SKSM.KAIREKIN,           -- 過入金額
        SKSM.URICHOSEIKIN,       -- 売上調整金額
-- インボイス対応変更 end ABI
-- ACCIS2023-6922 start ABI
        JCHT.ITAKUAITEACPTDNPYNO,  -- 受注配達テーブル.委託相手受注伝票番号
        TKSM.ITAKUAITEAPKIHONCD,   -- 得意先マスタ.委託相手AP基本コード
        TKSM.ITAKUAITESHOPCD,      -- 得意先マスタ.委託相手ショップコード
        TKSM.ITAKUAITETOKUISAKICD, -- 得意先マスタ.委託相手得意先コード
        TKSM.TOKUISAKIKBN,         -- 得意先マスタ.得意先区分
        TKSM.JUTAKUDLVRYCSTKBN     -- 得意先マスタ.受託配達顧客区分
-- ACCIS2023-6922 end ABI
      FROM
        S04T001T JCHT
        LEFT OUTER JOIN
        S02M013T TKSM
        ON (TKSM.APKIHONCD = JCHT.APKIHONCD
        AND TKSM.SHOPCD = JCHT.SHOPCD
        AND TKSM.TOKUISAKICD = JCHT.TOKUISAKICD)
        LEFT OUTER JOIN
        S02M017T SKSM
        ON (SKSM.APKIHONCD = TKSM.APKIHONCD
        AND SKSM.SHOPCD = TKSM.SHOPCD
        AND SKSM.SKYSAKICD = TKSM.SKYSAKICD)
      WHERE
        JCHT.APKIHONCD = iApkihoncd
        AND JCHT.SHOPCD = iShopcd
        AND JCHT.DLVRYKANKATSUAPKIHONCD = iApkihoncd
        AND JCHT.DLVRYKANKATSUSHOPCD = iShopcd
        AND JCHT.DLVRYKYOTENCD = iDlvrykyotencd
        AND JCHT.DLVRYYTIDD <= iDlvryytidd
        AND JCHT.YUKOKBN = '1'
        AND JCHT.DNPYSTS IN ('02','03')
      ORDER BY
        JCHT.APKIHONCD,
        JCHT.SHOPCD,
        JCHT.ACPTDNPYNO
      FOR UPDATE OF JCHT.ACPTDNPYNO;

    BEGIN
      -- 入力パラメータ.ＡＰ基本コードチェック
      IF iApkihoncd IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001,'パラメータ.ＡＰ基本コードが設定されていません。');
      END IF;

      -- 入力パラメータ.ショップコードチェック
      IF iShopcd IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001,'パラメータ.ショップコードが設定されていません。');
      END IF;

      -- 入力パラメータ.配達拠点コードチェック
      IF iDlvrykyotencd IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001,'パラメータ.配達拠点コードが設定されていません。');
      END IF;

      -- 入力パラメータ.配達予定日チェック
      IF iDlvryytidd IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001,'パラメータ.配達予定日が設定されていません。');
      END IF;

      -- 入力パラメータ.更新者AP基本コードチェック
      IF iKoshaapkihoncd IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001,'パラメータ.更新者AP基本コードが設定されていません。');
      END IF;

      -- 入力パラメータ.更新者ショップコードチェック
      IF iKoshashopcd IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001,'パラメータ.更新者ショップコードが設定されていません。');
      END IF;

      -- 入力パラメータ.更新者コードチェック
      IF ikoshacd IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001,'パラメータ.更新者コードが設定さていません。');
      END IF;

      -- 入力パラメータ.更新IPアドレスチェック
      IF iKshnipadrse IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001,'パラメータ.更新IPアドレスが設定されていません。');
      END IF;

      C0809B12.updateJotai(iApkihoncd, iShopcd, iDlvrykyotencd, '1', iKoshaapkihoncd, iKoshashopcd, ikoshacd, iKshnipadrse);

      -- 運用日付を取得
      wUnyoYmd := S01GetOperationdate(iApkihoncd, iShopcd);

      BEGIN
        -- 受注配達テーブル抽出
        OPEN SELECT_JCHT;
          LOOP
            FETCH SELECT_JCHT
              INTO
                wApkihoncd,     -- AP基本コード
                wShopcd,        -- ショップコード
                wAcptdnpyno,    -- 受注伝票番号
                wEdano,         -- 枝番号
                wTokuisakicd,   -- 得意先コード
                wDlvryytidd,    -- 配達予定日
-- インボイス対応変更 strat ABI
--                wKaishuhohokbn; -- 回収方法区分
                wSkysakicd,        -- 請求先コード
                wSingldnpyflg,     -- 単体伝票締めフラグ
                wAcptthdaibikiflg, -- 受注時代引きフラグ
                wKairekin,         -- 過入金額
                wUrichoseikin,     -- 売上調整金額
-- インボイス対応変更 end ABI
            -- ACCIS2023-6922 start ABI
                wItakuaiteacptdnpyno,  -- 委託相手受注伝票番号
                wItakuaiteapkihoncd,   -- 委託相手AP基本コード
                wItakuaiteshopcd,      -- 委託相手ショップコード
                wItakuaitetokuisakicd, -- 委託相手得意先コード
                wTokuisakikbn,         -- 得意先区分
                wJutakudlvrycstkbn;    -- 受託配達顧客区分
            -- ACCIS2023-6922 end ABI

            IF SELECT_JCHT%ROWCOUNT = 0 THEN
              EXIT;
            ELSIF SELECT_JCHT%NOTFOUND THEN
              EXIT;
            ELSE

              -- ACCIS2023-6922 start ABI
              -- 委託先判定処理
              -- 得意先区分が「NA受託」かつ受託配達顧客区分が「NA受託」または
              -- 得意先区分が「配達受託」かつ受託配達顧客区分が「配達受託」かつ
              -- 委託相手受注伝票番号が空でない場合、委託先の受注情報
              IF (('5' = wTokuisakikbn AND '5' = wJutakudlvrycstkbn) OR ('6' = wTokuisakikbn AND '6' = wJutakudlvrycstkbn)) THEN
                IF (wItakuaiteacptdnpyno IS NOT NULL) THEN
                  isItakusaki := TRUE;
                END IF;
              END IF;
              -- ACCIS2023-6922 end ABI

              -- 受注配達、売上、受注明細テーブル更新処理STR
              setJyucyuUriageTable(wApkihoncd,      -- AP基本コード
                                   wShopcd,         -- ショップコード
                                   wAcptdnpyno,     -- 受注伝票番号
                                   wEdano,          -- 枝番号
                                   wTokuisakicd,    -- 得意先コード
                                   wDlvryytidd,     -- 配達予定日
-- インボイス対応変更 strat ABI
--                                   wKaishuhohokbn,  -- 回収方法区分
                                   wSkysakicd,        -- 請求先コード
                                   wSingldnpyflg,     -- 単体伝票締めフラグ
                                   wAcptthdaibikiflg, -- 受注時代引きフラグ
                                   wKairekin,         -- 過入金額
                                   wUrichoseikin,     -- 売上調整金額
-- インボイス対応変更 end ABI
                                   wUnyoYmd,        -- 運用日付
                                   iKoshaapkihoncd, -- 更新者AP基本コード
                                   iKoshashopcd,    -- 更新者ショップコード
                                   ikoshacd,        -- 更新担当者コード
                                   iKshnipadrse,    -- 更新IPアドレス
-- ACCIS2023-6922 start ABI
                                   wItakuaiteapkihoncd,   -- 委託相手AP基本コード
                                   wItakuaiteshopcd,      -- 委託相手ショップコード
                                   wItakuaitetokuisakicd, -- 委託相手得意先コード
                                   isItakusaki);          -- 委託先判定用
-- ACCIS2023-6922 end ABI
            END IF;

            -- ループ件数を取得
            wMeisaiCount := wMeisaiCount + 1;

            -- ACCIS2023-6922 start ABI
            -- 委託先判定初期化
            isItakusaki := FALSE;
            -- ACCIS2023-6922 end ABI

          END LOOP;
        CLOSE SELECT_JCHT;
      END;

      BEGIN
        -- システム日付
-- 本線対応変更 start ABI
        --wSysdate := SYSDATE;
        wSysdate := CURRENT_DATE;
-- 本線対応変更 end ABI
        -- 枝番号
        SELECT
          MAX(EDANO)
        INTO wEdano_S05T001T
        FROM
          S05T001T
        WHERE
          APKIHONCD = iApkihoncd
          AND SHOPCD = iShopcd
          AND DLVRYKYOTENCD = iDlvrykyotencd
          AND JIKKODD = wUnyoYmd
          AND JIKKOJIKAN = TO_CHAR(wSysdate, 'hh24miss')
        ;
        IF wEdano_S05T001T IS NOT NULL THEN
          wEdano_S05T001T := wEdano_S05T001T + 1;
        ELSE
          wEdano_S05T001T := 1;
        END IF;
        -- 配達予定データ作成処理結果テーブル更新処理STR
        INSERT INTO S05T001T
          (APKIHONCD
          ,SHOPCD
          ,DLVRYKYOTENCD
          ,JIKKODD
          ,JIKKOJIKAN
          ,EDANO
          ,DLVRYYTIDD
          ,PRCSCNT
          ,YUKOKBN
          ,TOSHAAPKIHONCD
          ,TOSHASHOPCD
          ,TOSHACD
          ,INPTSYSTMYMD
          ,INPTSYSTMJIKAN
          ,INPTUNYOYMD
          ,INPTIPADRSE
          ,INPTPGMID
          ,KOSHAAPKIHONCD
          ,KOSHASHOPCD
          ,KOSHACD
          ,KSHNSYSTMYMD
          ,KSHNSYSTMJIKAN
          ,KSHNUNYOYMD
          ,KSHNIPADRSE
          ,KSHNPGMID)
        VALUES
          (iApkihoncd
          ,iShopcd
          ,iDlvrykyotencd
          ,wUnyoYmd
          ,TO_CHAR(wSysdate, 'hh24miss')
          ,wEdano_S05T001T
          ,iDlvryytidd
          ,wMeisaiCount
          ,'1'
          ,iKoshaapkihoncd
          ,iKoshashopcd
          ,ikoshacd
-- 本線対応変更 start ABI
          --,TO_CHAR(SYSDATE, 'YYYYMMDD')
          --,TO_CHAR(SYSDATE, 'hh24miss')
          ,TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
          ,TO_CHAR(CURRENT_DATE, 'hh24miss')
-- 本線対応変更 end ABI
          ,wUnyoYmd
          ,iKshnipadrse
          ,wPgId
          ,iKoshaapkihoncd
          ,iKoshashopcd
          ,ikoshacd
-- 本線対応変更 start ABI
          --,TO_CHAR(SYSDATE, 'YYYYMMDD')
          --,TO_CHAR(SYSDATE, 'hh24miss')
          ,TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
          ,TO_CHAR(CURRENT_DATE, 'hh24miss')
-- 本線対応変更 end ABI
          ,wUnyoYmd
          ,iKshnipadrse
          ,wPgId);
      END;

      C0809B12.updateJotai(iApkihoncd, iShopcd, iDlvrykyotencd, '0', iKoshaapkihoncd, iKoshashopcd, ikoshacd, iKshnipadrse);

    EXCEPTION
      WHEN OTHERS THEN
      RAISE_APPLICATION_ERROR(-20703, SQLERRM);

  END getDeliveryPlanDate;

  /*****************************************************************************/
  /* プロシージャ名   ：配達予定対象データ（伝票番号での絞り込みあり）         */
  /* 引数             ：iApkihoncd            AP基本コード                     */
  /*                    iShopcd               ショップコード                   */
  /*                    iAcptdnpyno           伝票番号                         */
  /*                    iEdano                枝番                             */
  /*                    iKoshaapkihoncd       更新者AP基本コード               */
  /*                    iKoshashopcd          更新者ショップコード             */
  /*                    ikoshacd              更新者コード                     */
  /*                    iKshnipadrse          更新IPアドレス                   */
  /*****************************************************************************/
  FUNCTION getDeliveryPlanDateByDnpyno(iApkihoncd      IN VARCHAR2,                  -- AP基本コード
                                       iShopcd         IN VARCHAR2,                  -- ショップコード
                                       iAcptdnpyno     IN VARCHAR2,                  -- 伝票番号
                                       iEdano          IN VARCHAR2,                  -- 枝番号
                                       iKoshaapkihoncd IN VARCHAR2,                  -- 更新者AP基本コード
                                       iKoshashopcd    IN VARCHAR2,                  -- 更新者ショップコード
                                       ikoshacd        IN VARCHAR2,                  -- 更新担当者コード
                                       iKshnipadrse    IN VARCHAR2) RETURN NUMBER IS -- 更新IPアドレス

    -- 変数宣言
    vRecordS04T001T  S04T001T%ROWTYPE; -- 受注配達テーブル
    vRecordS02M017T  S02M017T%ROWTYPE; -- 請求先マスタ
-- ACCIS2023-6922 start ABI
    vRecordS02M013T  S02M013T%ROWTYPE; -- 得意先マスタ
-- ACCIS2023-6922 start ABI
    wUnyoYmd VARCHAR2(8)     := NULL;  -- 運用日付
-- インボイス対応追加 strat ABI
    wSingldnpyflg             VARCHAR2(1)  := NULL;       -- 単体伝票締めフラグ
    wAcptthdaibikiflg         VARCHAR2(1)  := NULL;       -- 受注時代引きフラグ
    wKairekin                 VARCHAR2(11) := NULL;       -- 過入金額
    wUrichoseikin             VARCHAR2(11) := NULL;       -- 売上調整金額
-- インボイス対応変更 end ABI
-- ACCIS2023-6922 start ABI
    isItakusaki               BOOLEAN      := FALSE;      -- 委託先判定用
-- ACCIS2023-6922 end ABI

    -- カーソル宣言
    CURSOR SELECT_JCHT IS
      SELECT
        JCHT.APKIHONCD,
        JCHT.SHOPCD,
        JCHT.ACPTDNPYNO,
        JCHT.EDANO,
        JCHT.TOKUISAKICD,
        JCHT.DLVRYYTIDD,
-- インボイス対応変更 strat ABI
--        SKSM.KAISHUHOHOKBN -- 回収方法区分
        TKSM.SKYSAKICD,          -- 得意先マスタ.請求先コード
        JCHT.SINGLDNPYFLG,       -- 単体伝票締めフラグ
        JCHT.ACPTTHDAIBIKIFLG,   -- 受注時代引きフラグ
        SKSM.KAIREKIN,           -- 過入金額
        SKSM.URICHOSEIKIN,       -- 売上調整金額
-- インボイス対応変更 end ABI
-- ACCIS2023-6922 start ABI
        JCHT.ITAKUAITEACPTDNPYNO,  -- 委託相手受注伝票番号
        TKSM.ITAKUAITEAPKIHONCD,   -- 委託相手AP基本コード
        TKSM.ITAKUAITESHOPCD,      -- 委託相手ショップコード
        TKSM.ITAKUAITETOKUISAKICD, -- 委託相手得意先コード
        TKSM.TOKUISAKIKBN,         -- 得意先区分
        TKSM.JUTAKUDLVRYCSTKBN     -- 受託配達顧客区分
-- ACCIS2023-6922 end ABI
      FROM
        S04T001T JCHT
        LEFT OUTER JOIN
        S02M013T TKSM
        ON (TKSM.APKIHONCD = JCHT.APKIHONCD
        AND TKSM.SHOPCD = JCHT.SHOPCD
        AND TKSM.TOKUISAKICD = JCHT.TOKUISAKICD)
        LEFT OUTER JOIN
        S02M017T SKSM
        ON (SKSM.APKIHONCD = TKSM.APKIHONCD
        AND SKSM.SHOPCD = TKSM.SHOPCD
        AND SKSM.SKYSAKICD = TKSM.SKYSAKICD)
      WHERE
        JCHT.APKIHONCD = iApkihoncd
        AND JCHT.SHOPCD = iShopcd
        AND JCHT.ACPTDNPYNO = iAcptdnpyno
        AND JCHT.EDANO = iEdano;

    BEGIN
      -- 入力パラメータチェック
      IF iApkihoncd IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001,'パラメータ.ＡＰ基本コードが設定されていません。');
      END IF;

      -- 入力パラメータチェック
      IF iShopcd IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001,'パラメータ.ショップコードが設定されていません。');
      END IF;

      -- 入力パラメータチェック
      IF iAcptdnpyno IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001,'パラメータ.伝票番号が設定されていません。');
      END IF;

      -- 入力パラメータチェック
      IF iEdano IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001,'パラメータ.枝番が設定されていません。');
      END IF;

      -- 入力パラメータチェック
      IF iKoshaapkihoncd IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001,'パラメータ.更新者AP基本コードが設定されていません。');
      END IF;

      -- 入力パラメータチェック
      IF iKoshashopcd IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001,'パラメータ.更新者ショップコードが設定されていません。');
      END IF;

      -- 入力パラメータチェック
      IF ikoshacd IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001,'パラメータ.更新者コードが設定さていません。');
      END IF;

      -- 入力パラメータチェック
      IF iKshnipadrse IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001,'パラメータ.更新IPアドレスが設定されていません。');
      END IF;

      -- 運用日付を取得
      wUnyoYmd := S01GetOperationdate(iApkihoncd, iShopcd);

      -- 受注配達テーブル抽出
      OPEN SELECT_JCHT;
        LOOP
          FETCH SELECT_JCHT
            INTO
              vRecordS04T001T.APKIHONCD,
              vRecordS04T001T.SHOPCD,
              vRecordS04T001T.ACPTDNPYNO,
              vRecordS04T001T.EDANO,
              vRecordS04T001T.TOKUISAKICD,
              vRecordS04T001T.DLVRYYTIDD,
-- インボイス対応変更 strat ABI
--              vRecordS02M017T.KAISHUHOHOKBN;
              vRecordS02M017T.SKYSAKICD,
              vRecordS04T001T.SINGLDNPYFLG,
              vRecordS04T001T.ACPTTHDAIBIKIFLG,
              vRecordS02M017T.KAIREKIN,
              vRecordS02M017T.URICHOSEIKIN,
-- インボイス対応変更 end ABI
-- ACCIS2023-6922 start ABI
              vRecordS04T001T.ITAKUAITEACPTDNPYNO,
              vRecordS02M013T.ITAKUAITEAPKIHONCD,
              vRecordS02M013T.ITAKUAITESHOPCD,
              vRecordS02M013T.ITAKUAITETOKUISAKICD,
              vRecordS02M013T.TOKUISAKIKBN,
              vRecordS02M013T.JUTAKUDLVRYCSTKBN;
-- ACCIS2023-6922 end ABI

          IF SELECT_JCHT%ROWCOUNT = 0 THEN
            EXIT;
          ELSIF SELECT_JCHT%NOTFOUND THEN
            EXIT;
          ELSE

            -- ACCIS2023-6922 start ABI
            -- 委託先判定処理
            -- 得意先区分が「NA受託」かつ受託配達顧客区分が「NA受託」または
            -- 得意先区分が「配達受託」かつ受託配達顧客区分が「配達受託」かつ
            -- 委託相手受注伝票番号が空でない場合、委託先の受注情報
            IF (('5' = vRecordS02M013T.TOKUISAKIKBN AND '5' = vRecordS02M013T.JUTAKUDLVRYCSTKBN) OR
                ('6' = vRecordS02M013T.TOKUISAKIKBN AND '6' = vRecordS02M013T.JUTAKUDLVRYCSTKBN)) THEN
              IF (vRecordS04T001T.ITAKUAITEACPTDNPYNO IS NOT NULL) THEN
                isItakusaki := TRUE;
              END IF;
            END IF;
            -- ACCIS2023-6922 end ABI

            -- 受注配達、売上、受注明細テーブル更新処理STR
            setJyucyuUriageTable(vRecordS04T001T.APKIHONCD,     -- AP基本コード
                                 vRecordS04T001T.SHOPCD,        -- ショップコード
                                 vRecordS04T001T.ACPTDNPYNO,    -- 受注伝票番号
                                 vRecordS04T001T.EDANO,         -- 枝番号
                                 vRecordS04T001T.TOKUISAKICD,   -- 得意先コード
                                 vRecordS04T001T.DLVRYYTIDD,    -- 配達予定日
-- インボイス対応変更 strat ABI
--                                 vRecordS02M017T.KAISHUHOHOKBN, -- 回収方法区分
                                 vRecordS02M017T.SKYSAKICD,        -- 請求先コード
                                 vRecordS04T001T.SINGLDNPYFLG,     -- 単体伝票締めフラグ
                                 vRecordS04T001T.ACPTTHDAIBIKIFLG, -- 受注時代引きフラグ
                                 vRecordS02M017T.KAIREKIN,         -- 過入金額
                                 vRecordS02M017T.URICHOSEIKIN,     -- 売上調整金額
-- インボイス対応変更 end ABI
                                 wUnyoYmd,                      -- 運用日付
                                 iKoshaapkihoncd,               -- 更新者AP基本コード
                                 iKoshashopcd,                  -- 更新者ショップコード
                                 ikoshacd,                      -- 更新担当者コード
                                 iKshnipadrse,                  -- 更新IPアドレス
-- ACCIS2023-6922 start ABI
                                 vRecordS02M013T.ITAKUAITEAPKIHONCD,    -- 委託相手AP基本コード
                                 vRecordS02M013T.ITAKUAITESHOPCD,       -- 委託相手ショップコード
                                 vRecordS02M013T.ITAKUAITETOKUISAKICD,  -- 委託相手得意先コード
                                 isItakusaki);                          -- 委託先判定用
-- ACCIS2023-6922 end ABI
          END IF;
-- ACCIS2023-6922 start ABI
          -- 委託先判定初期化
          isItakusaki := FALSE;
-- ACCIS2023-6922 end ABI
        END LOOP;
      CLOSE SELECT_JCHT;
        RETURN 0;

      EXCEPTION
        WHEN OTHERS THEN
          RETURN 1;
  END getDeliveryPlanDateByDnpyno;

  /*****************************************************************************/
  /* プロシージャ名   ：配達予定データ更新処理                                 */
  /* 引数             ：wApkihoncd                 AP基本コード                */
  /*                    wShopcd                    ショップコード              */
  /*                    wAcptdnpyno                受注伝票番号                */
  /*                    wEdano                     枝番号                      */
  /*                    wTokuisakicd               得意先コード                */
  /*                    wDlvryytidd                配達予定日                  */
  /* インボイス対応変更 strat ABI                                              */
  /*                --  wKaishuhohokbn             回収方法区分                */
  /*                    wSkysakicd                 請求先コード                */
  /*                    wSingldnpyflg              単体伝票締めフラグ          */
  /*                    wAcptthdaibikiflg          受注時代引きフラグ          */
  /*                    wKairekin                  過入金額                    */
  /*                    wUrichoseikin              売上調整金額                */
  /* インボイス対応変更 end ABI                                                */
  /*                    wUnyoYmd                   運用日付                    */
  /*                    iKoshaapkihoncd            更新者AP基本コード          */
  /*                    iKoshashopcd               更新者ショップコード        */
  /*                    ikoshacd                   更新担当者コード            */
  /*                    iKshnipadrse               更新IPアドレス              */
  /* ACCIS2023-6922 start ABI                                                  */
  /*                    wItakuaiteapkihoncd        委託相手AP基本コード        */
  /*                    wItakuaiteshopcd           委託相手ショップコード      */
  /*                    wItakuaitetokuisakicd      委託相手得意先コード        */
  /*                    isItakusaki                委託先判定用                */
  /* ACCIS2023-6922 end ABI                                                    */
  /*****************************************************************************/
  PROCEDURE setJyucyuUriageTable(wApkihoncd      IN VARCHAR2,    --AP基本コード
                                 wShopcd         IN VARCHAR2,    --ショップコード
                                 wAcptdnpyno     IN VARCHAR2,    --受注伝票番号
                                 wEdano          IN VARCHAR2,    --枝番号
                                 wTokuisakicd    IN VARCHAR2,    --得意先コード
                                 wDlvryytidd     IN VARCHAR2,    --配達予定日
-- インボイス対応変更 strat ABI
--                                 wKaishuhohokbn  IN VARCHAR2,    --回収方法区分
                                 wSkysakicd        IN VARCHAR2,  -- 請求先コード
                                 wSingldnpyflg     IN VARCHAR2,  -- 単体伝票締めフラグ
                                 wAcptthdaibikiflg IN VARCHAR2,  -- 受注時代引きフラグ
                                 wKairekin         IN VARCHAR2,  -- 過入金額
                                 wUrichoseikin     IN VARCHAR2,  -- 売上調整金額
-- インボイス対応変更 end ABI
                                 wUnyoYmd        IN VARCHAR2,    --運用日付
                                 iKoshaapkihoncd IN VARCHAR2,    --更新者AP基本コード
                                 iKoshashopcd    IN VARCHAR2,    --更新者ショップコード
                                 ikoshacd        IN VARCHAR2,    --更新担当者コード
                                 iKshnipadrse    IN VARCHAR2,    --更新IPアドレス
-- ACCIS2023-6922 start ABI
                                 wItakuaiteapkihoncd   IN VARCHAR2,   -- 委託相手AP基本コード
                                 wItakuaiteshopcd      IN VARCHAR2,   -- 委託相手ショップコード
                                 wItakuaitetokuisakicd IN VARCHAR2,   -- 委託相手得意先コード
                                 isItakusaki           IN BOOLEAN) IS -- 委託先判定用
-- ACCIS2023-6922 end ABI

    -- 変数宣言
    wUridnpymeisaiedano       VARCHAR2(2)  := NULL;       -- 売上伝票明細枝番号
    wLctgrycd                 VARCHAR2(2)  := NULL;       -- 大分類コード
    wMctgrycd                 VARCHAR2(3)  := NULL;       -- 中分類コード
    wSctgrycd                 VARCHAR2(3)  := NULL;       -- 小分類コード
    wShncd                    VARCHAR2(6)  := NULL;       -- 商品コード
    wKatasiki                 VARCHAR2(15) := NULL;       -- 型式
    wKeijokbn                 VARCHAR2(2)  := NULL;       -- 計上区分
    wSuryo                    NUMBER       := 0;          -- 数量
    wHyojununt                NUMBER       := 0;          -- 標準単価
    wUnt                      NUMBER       := 0;          -- 単価
    wKin                      NUMBER       := 0;          -- 金額
    wUntshohitaxkin           NUMBER       := 0;          -- 単価消費税金額
    wShohitaxkin              NUMBER       := 0;          -- 消費税金額
    wTaxkbn                   VARCHAR2(1)  := NULL;       -- 税区分
    wNyuryokumeisaiflg        VARCHAR2(1)  := NULL;       -- 入力明細フラグ
    wTanpinmeisaiflg          VARCHAR2(1)  := NULL;       -- 単品明細フラグ
    wStshncd                  VARCHAR2(6)  := NULL;       -- セット商品コード
    wMeisaibiko               VARCHAR2(40) := NULL;       -- 明細備考
    wUriageApkihoncd          VARCHAR2(3)  := NULL;       -- 売上AP基本コード
    wUriageShopcd             VARCHAR2(3)  := NULL;       -- 売上ショップコード
    wUridnpymeisaino          VARCHAR2(2)  := NULL;       -- 売上伝票明細番号
    wUriageEdno               VARCHAR2(2)  := NULL;       -- 売上枝番号
    wMeisaiCountUp            NUMBER       := 0;          -- 明細カウントアップ
    wAzukaribtln1n2rittoruhns NUMBER       := 0;          -- 預かりボトル１２Ｌ本数
    wAzukaribtln8rittoruhns   NUMBER       := 0;          -- 預かりボトル８Ｌ本数
    wZnkdlvrydd               VARCHAR2(8)  := NULL;       -- 前回配達日
    wZnkdlvryhns              NUMBER       := 0;          -- 前回配達本数
    wGenzaizd                 NUMBER       := 0;          -- 現在残高
    wJikaiDlvryytidd          VARCHAR2(8)  := NULL;       -- 次回配達予定日
    wJikaiDlvryytidd1         VARCHAR2(8)  := NULL;       -- 次回配達予定日
    wJikaiDlvryytidd2         VARCHAR2(8)  := NULL;       -- 次回配達予定日
    wJikaiDlvryytidd3         VARCHAR2(8)  := NULL;       -- 次回配達予定日
-- インボイス対応変更 strat ABI
--    wDaibiki                  VARCHAR2(1)  := '7';        -- 代引き区分
    wDaibiki                  VARCHAR2(1)  := '1';        -- 受注時代引きフラグ（代引き）
-- インボイス対応変更 end ABI
    wStatus                   VARCHAR2(2)  := '11';       -- 伝票ステータス、配達ステータス（配達中）
    wPgId                     VARCHAR2(8)  := 'C0809B00'; -- プログラムID
    wJissiDd                  VARCHAR2(8)  := '00000000'; -- 請求実施日
    wKingaku                  NUMBER       := 0;          -- 最新請求残高の繰越金額（受注時代引フラグがONの場合）
    wMishimeNyknSum           NUMBER       := 0;          -- 未締め入金金額（受注時代引フラグがONの場合）
    wUridnpyno                VARCHAR2(11) := NULL;       -- 売上伝票番号
    wMaxJyucyudnpymeisaino    NUMBER       := 0;          -- 最大の受注伝票明細番号
    wYusyokbn                 VARCHAR2(1)  := 0;          -- 有償区分
    wUriSkyShimeDyDm          VARCHAR2(6)  := NULL;       -- 売上月次締年月
    wUriSkyShimeDd            VARCHAR2(2)  := NULL;       -- 売上月次締日
    wUriSkyPrcsJissiDd        VARCHAR2(8)  := NULL;       -- 売上月次処理実施日
    wUriKshnSystemYmd         VARCHAR2(8)  := NULL;       -- 売上更新システム日付
    wUriKshnSystemJikan       VARCHAR2(6)  := NULL;       -- 売上更新システム時間
    wUriKshnPgmId             VARCHAR2(8)  := NULL;       -- 売上更新プログラムＩＤ
-- ACCIS2023-6922 start ABI
    vApkihoncd                VARCHAR2(3)  := NULL;       -- AP基本コード（次回配達予定日検索時にのみ使用）
    vShopcd                   VARCHAR2(3)  := NULL;       -- ショップコード（次回配達予定日検索時にのみ使用）
    vTokuisakicd              VARCHAR2(6)  := NULL;       -- 得意先コード（次回配達予定日検索時にのみ使用）
-- ACCIS2023-6922 end ABI

-- インボイス対応変更 strat ABI
--    -- 売上テーブル抽出SQL
--    CURSOR SELECT_URIT IS
--      SELECT
--        URIT.APKIHONCD, -- AP基本コード
--        URIT.SHOPCD,    -- ショップコード
--        URIT.URIDNPYNO,  -- 売上伝票番号
--        URIT.SKYSHIMEDYDM,  --月次締年月
--        URIT.SKYSHIMEDD,  --月次締日
--        URIT.SKYPRCSJISSIDD,  --月次処理実施日
--        URIT.KSHNSYSTMYMD,  --更新システム日付
--        URIT.KSHNSYSTMJIKAN,  --更新システム時間
--        URIT.KSHNPGMID  --更新プログラムＩＤ
--      FROM
--        S06T001T URIT
--        LEFT OUTER JOIN
--        S02M017T SKSM
--        ON(SKSM.APKIHONCD = URIT.APKIHONCD
--        AND SKSM.SHOPCD = URIT.SHOPCD
--        AND SKSM.SKYSAKICD = URIT.SKYSAKICD)
--      WHERE
--        URIT.APKIHONCD = wApkihoncd
--        AND URIT.SHOPCD = wShopcd
--        AND URIT.TOKUISAKICD = wTokuisakicd
--        AND URIT.URIDD <= wDlvryytidd
--        AND URIT.DATSKSMOTOKBN = '06'
--        AND URIT.YUKOKBN = '1'
--        AND URIT.SKYPRCSJISSIDD = wJissiDd
--        AND URIT.DNPYNO IS NULL
--      FOR UPDATE OF URIT.URIDNPYNO;
    -- 未締売上テーブル抽出SQL
    CURSOR SELECT_URIT IS
      SELECT
        URIT.APKIHONCD, -- AP基本コード
        URIT.SHOPCD,    -- ショップコード
        URIT.URIDNPYNO,  -- 売上伝票番号
        URIT.SKYSHIMEDYDM,  --月次締年月
        URIT.SKYSHIMEDD,  --月次締日
        URIT.SKYPRCSJISSIDD,  --月次処理実施日
        URIT.KSHNSYSTMYMD,  --更新システム日付
        URIT.KSHNSYSTMJIKAN,  --更新システム時間
        URIT.KSHNPGMID  --更新プログラムＩＤ
      FROM
        S06T001T URIT
      WHERE
        URIT.APKIHONCD = wApkihoncd
        AND URIT.SHOPCD = wShopcd
        AND URIT.TOKUISAKICD = wTokuisakicd
        AND URIT.URIDD <= wDlvryytidd
        AND URIT.YUKOKBN = '1'
        AND URIT.SKYPRCSJISSIDD = wJissiDd
        AND URIT.DNPYNO IS NULL
      FOR UPDATE OF URIT.URIDNPYNO;
-- インボイス対応変更 end ABI

-- インボイス対応削除 strat ABI
--    -- 売上明細テーブル抽出SQL
--    CURSOR SELECT_UAMT IS
--      SELECT
--        UAMT.URIDNPYMEISAIEDANO,
--        UAMT.LCTGRYCD,
--        UAMT.MCTGRYCD,
--        UAMT.SCTGRYCD,
--        UAMT.SHNCD,
--        UAMT.KATASIKI,
--        UAMT.KEIJOKBN,
--        UAMT.SURYO,
--        UAMT.HYOJUNUNT,
--        UAMT.UNT,
--        UAMT.KIN,
--        UAMT.UNTSHOHITAXKIN,
--        UAMT.SHOHITAXKIN,
--        UAMT.TAXKBN,
--        UAMT.NYURYOKUMEISAIFLG,
--        UAMT.TANPINMEISAIFLG,
--        UAMT.STSHNCD,
--        UAMT.MEISAIBIKO,
--        UAMT.URIDNPYNO,
--        UAMT.EDANO,
--        UAMT.URIDNPYMEISAINO
--      FROM
--        S06T002T UAMT
--      WHERE
--        APKIHONCD = wUriageApkihoncd
--        AND SHOPCD = wUriageShopcd
--        AND UAMT.URIDNPYNO = wUridnpyno;
-- インボイス対応削除 end ABI

    -- 最新請求残高から繰越金額を取得するためのカーソル
    CURSOR SELECT_LATEST_SZDT IS
    SELECT
        NVL(MAIN.KURIKOSHIKIN, 0)                   -- 繰越金額
    FROM
      S07T001T MAIN                                 -- 請求残高テーブル
    WHERE
      MAIN.YUKOKBN = '1'                            -- 有効区分
      AND MAIN.APKIHONCD = wApkihoncd               -- ＡＰ基本コード
      AND MAIN.SHOPCD = wShopcd                     -- ショップコード
      AND MAIN.SKYSAKICD = wSkysakicd               -- 請求先コード
      AND MAIN.TOKUISAKICD = wTokuisakicd           -- 得意先コード
      AND MAIN.KONKAISHIMEDD = (
        -- 今回締日が最大
        SELECT
            MAX(SZDT.KONKAISHIMEDD)                 -- 今回締日
        FROM
          S07T001T SZDT                             -- 請求残高テーブル
        WHERE
          SZDT.APKIHONCD = MAIN.APKIHONCD           -- ＡＰ基本コード
          AND SZDT.SHOPCD = MAIN.SHOPCD             -- ショップコード
          AND SZDT.SKYSAKICD = MAIN.SKYSAKICD       -- 請求先コード
          AND SZDT.TOKUISAKICD = MAIN.TOKUISAKICD   -- 得意先コード
          AND SZDT.YUKOKBN = '1'                    -- 有効区分
      )
    ORDER BY
      MAIN.RENBAN                                   -- 連番
    ;

    BEGIN
      BEGIN
        -- 得意先実績テーブル抽出SQL
        SELECT
          NVL(TSJT.AZUKARIBTLN1N2RITTORUHNS,0),
          NVL(TSJT.AZUKARIBTLN8RITTORUHNS,0),
          TSJT.ZNKDLVRYDD,
          NVL(TSJT.ZNKDLVRYRGRAYUSYOHNS,0)
              + NVL(TSJT.ZNKDLVRYRGRAMUSHOHNS,0)
              + NVL(TSJT.ZNKDLVRYSRMYUSYOHNS,0)
              + NVL(ZNKDLVRYSRMMUSHOHNS,0)
              + NVL(TSJT.ZNKDLVRYSONOTAYUSYOHNS,0)
              + NVL(ZNKDLVRYSONOTAMUSHOHNS,0) AS ZNKDLVRYHNS
-- インボイス対応削除 strat ABI
--          NVL(TSJT.GENZAIZD,0)
-- インボイス対応削除 end ABI
        INTO
          wAzukaribtln1n2rittoruhns, -- 預かりボトル１２Ｌ本数
          wAzukaribtln8rittoruhns,   -- 預かりボトル８Ｌ本数
          wZnkdlvrydd,               -- 前回配達日
          wZnkdlvryhns               -- 前回配達本数
-- インボイス対応削除 strat ABI
--          wGenzaizd                  -- 現在残高
-- インボイス対応削除 end ABI
        FROM
          S02M050T TSJT
        WHERE
          TSJT.APKIHONCD = wApkihoncd
          AND TSJT.SHOPCD = wShopcd
          AND TSJT.TOKUISAKICD = wTokuisakicd;
        --異常処理
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            wAzukaribtln1n2rittoruhns := 0;
            wAzukaribtln8rittoruhns := 0;
            wZnkdlvrydd := NULL;
            wZnkdlvryhns := 0;
            wGenzaizd := 0;
         WHEN OTHERS THEN
           RAISE_APPLICATION_ERROR(-20001,'得意先実績テーブルから情報を抽出出来ませんでした。');
      END;

-- インボイス対応変更 strat ABI
--      -- 請求先マスタ.回収区分が代引きの場合の処理
--      IF wKaishuhohokbn = wDaibiki THEN
--        BEGIN
--          -- 売上明細テーブル.金額 + 消費税金額を抽出
--          SELECT
--            NVL(SUM(NVL(URMT.KIN,0) + NVL(URMT.SHOHITAXKIN,0)),0)
--          INTO
--            wKingaku
--          FROM
--            S06T001T URAT
--            LEFT OUTER JOIN
--            S06T002T URMT
--            ON(URMT.APKIHONCD = URAT.APKIHONCD
--            AND URMT.SHOPCD = URAT.SHOPCD
--            AND URMT.URIDNPYNO = URAT.URIDNPYNO)
--            LEFT OUTER JOIN
--            S02M017T SKSM
--            ON(SKSM.APKIHONCD = URAT.APKIHONCD
--            AND SKSM.SHOPCD = URAT.SHOPCD
--            AND SKSM.SKYSAKICD = URAT.SKYSAKICD)
--          WHERE
--            URAT.APKIHONCD = wApkihoncd
--            AND URAT.SHOPCD = wShopcd
--            AND URAT.TOKUISAKICD = wTokuisakicd
--            AND URAT.URIDD <= wDlvryytidd
--            AND URAT.DATSKSMOTOKBN = '06'
--            AND URAT.YUKOKBN = '1'
--            AND URAT.SKYPRCSJISSIDD = wJissiDd
--            AND URAT.DNPYNO IS NULL;
--
--          --異常処理
--          EXCEPTION
--            WHEN OTHERS THEN
--              RAISE_APPLICATION_ERROR(-20001,'売上明細テーブルから情報を抽出出来ませんでした。');
--        END;
--      END IF;
      -- 受注時代引きフラグが代引きの場合の処理（得意先に請求先が設定されている場合）
      IF wAcptthdaibikiflg = wDaibiki AND wSkysakicd IS NOT NULL THEN
        /*
        BEGIN
          -- 最新の請求残高テーブルから繰越金額を取得
          SELECT
            NVL(SUM(SKZD.KESHIKOMIZD),0)
          INTO
            wKingaku
          FROM
            S07T001T SKZD
          WHERE
            SKZD.APKIHONCD = wApkihoncd
            AND SKZD.SHOPCD = wShopcd
            AND SKZD.TOKUISAKICD = wTokuisakicd
            AND SKZD.SKYSAKICD = wSkysakicd
            AND SKZD.KESHIKOMIZD <> 0
            AND SKZD.YUKOKBN = '1';
          --異常処理
          EXCEPTION
            WHEN OTHERS THEN
              RAISE_APPLICATION_ERROR(-20001,'請求残高テーブルから情報を抽出出来ませんでした。');
        END;
        */
        BEGIN
          -- 今回締日が配達予定日以前で最新の請求残高から繰越金額を取得
          -- ※同一請求締年月日の残高が複数ある場合、連番が最大（最終レコード）の金額を取得
          OPEN SELECT_LATEST_SZDT;
          LOOP
              FETCH SELECT_LATEST_SZDT
              INTO wKingaku;
              EXIT WHEN SELECT_LATEST_SZDT%NOTFOUND;
          END LOOP;
          CLOSE SELECT_LATEST_SZDT;
          -- 入金明細テーブルから未締め入金金額（手数料、値引金額を含む）を取得
          SELECT
              NVL(SUM(NKMT.NYKNKIN), 0) + NVL(SUM(NKMT.NEBIKIKIN), 0) + NVL(SUM(NKMT.TESURYO), 0) NYKNKIN
          INTO
            wMishimeNyknSum
          FROM
            S10T001T NKMT
          WHERE
            NKMT.YUKOKBN = '1'
            AND NKMT.SKYSHIMEDYDM IS NULL
            AND NKMT.APKIHONCD = wApkihoncd
            AND NKMT.SHOPCD = wShopcd
            AND NKMT.SKYSAKICD = wSkysakicd
            AND NKMT.TOKUISAKICD = wTokuisakicd
          ;
          --異常処理
          EXCEPTION
            WHEN OTHERS THEN
              RAISE_APPLICATION_ERROR(-20001,'請求残高テーブルまたは入金明細テーブルから情報を抽出時エラーが発生しました。');
        END;
      END IF;

-- インボイス対応変更 end ABI

      -- 受注配達テーブル.配達予定日を抽出(次回配達予定日)
      BEGIN

        -- ACCIS2023-6922 start ABI
        -- 委託先かつ委託相手受注伝票番号が空でない場合、委託相手の情報でマスタ検索を行う。
        -- 定期受注マスタおよび特定定期受注マスタは委託先では登録を行わない（二重受注になる）ため。
        IF isItakusaki = TRUE THEN
            vApkihoncd := wItakuaiteapkihoncd;
            vShopcd := wItakuaiteshopcd;
            vTokuisakicd := wItakuaitetokuisakicd;
        ELSE
            vApkihoncd := wApkihoncd;
            vShopcd := wShopcd;
            vTokuisakicd := wTokuisakicd;
        END IF;
        -- ACCIS2023-6922 end ABI

        SELECT
          NVL(MIN(JCHT.DLVRYYTIDD), '29991231')
        INTO
          wJikaiDlvryytidd1  -- 配達予定日(次回配達予定日)
        FROM
          S04T001T JCHT
        WHERE
          JCHT.APKIHONCD = wApkihoncd
          AND JCHT.SHOPCD = wShopcd
          AND JCHT.TOKUISAKICD = wTokuisakicd
          AND JCHT.DLVRYYTIDD > wDlvryytidd
          AND JCHT.DNPYSTS IN ('02','03')
          AND JCHT.YUKOKBN = '1';

        -- 定期受注マスタ.次回配達予定日を取得
        SELECT
          NVL(MIN(TKJM.JIKAIYTIDD), '29991231')
        INTO
          wJikaiDlvryytidd2  -- 次回配達予定日
        FROM
          S02M024T TKJM
        WHERE
        -- ACCIS2023-6922 start ABI
          TKJM.APKIHONCD = vApkihoncd
          AND TKJM.SHOPCD = vShopcd
          AND TKJM.TOKUISAKICD = vTokuisakicd
        -- ACCIS2023-6922 end ABI
          AND TKJM.JIKAIYTIDD > wDlvryytidd
          AND TKJM.YUKOKBN = '1';

        -- 特定定期受注マスタ.次回配達予定日を取得
        SELECT
          NVL(MIN(TTJM.JIKAIYTIDD), '29991231')
        INTO
          wJikaiDlvryytidd3  -- 次回配達予定日
        FROM
          S02M055T TTJM
        WHERE
        -- ACCIS2023-6922 start ABI
          TTJM.APKIHONCD = vApkihoncd
          AND TTJM.SHOPCD = vShopcd
          AND TTJM.TOKUISAKICD = vTokuisakicd
        -- ACCIS2023-6922 end ABI
          AND TTJM.JIKAIYTIDD > wDlvryytidd
          AND TTJM.YUKOKBN = '1';

        -- 最小の次回配達予定日を選択
        IF wJikaiDlvryytidd1 < wJikaiDlvryytidd2 AND wJikaiDlvryytidd1 < wJikaiDlvryytidd3 THEN
          wJikaiDlvryytidd := wJikaiDlvryytidd1;
        ELSIF wJikaiDlvryytidd2 < wJikaiDlvryytidd1 AND wJikaiDlvryytidd2 < wJikaiDlvryytidd3 THEN
          wJikaiDlvryytidd := wJikaiDlvryytidd2;
        ELSE
          wJikaiDlvryytidd := wJikaiDlvryytidd3;
        END IF;

        IF wJikaiDlvryytidd = '29991231' THEN
          wJikaiDlvryytidd := NULL;
        END IF;

        --異常処理
        EXCEPTION
          WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20001,'配達予定日を抽出出来ませんでした');
      END;

      -- 受注配達テーブル更新
      BEGIN
        UPDATE
          S04T001T JCHT
        SET
          JCHT.AZUKARIBTLN1N2RITTORUHNS = wAzukaribtln1n2rittoruhns -- 預かりボトル１２Ｌ本数
          , JCHT.AZUKARIBTLN8RITTORUHNS = wAzukaribtln8rittoruhns     -- 預かりボトル８Ｌ本数
          , JCHT.ACPTSHIMEPRCSDD = wUnyoYmd                           -- 受注締処理日
          , JCHT.ACPTSHIMETNSHAAPKIHONCD = iKoshaapkihoncd          -- 受注締処理担当者ＡＰ基本コード
          , JCHT.ACPTSHIMETNSHASHOPCD = iKoshashopcd                -- 受注締処理担当者ショップコード
          , JCHT.ACPTSHIMEPRCSTNSHACD = ikoshacd                     -- 受注締処理担当者コード
-- インボイス対応変更 strat ABI ACCIS2023-5148
--          , JCHT.NYKNHYOPRNTTRGTFLG = CASE WHEN wKaishuhohokbn = wDaibiki THEN '1' ELSE '0' END -- 入金票印刷対象フラグ（代引きの場合は'1'、代引き以外は'0'）
--          , JCHT.NYKNHYOPRNTTRGTFLG = CASE WHEN wAcptthdaibikiflg = wDaibiki THEN '1' ELSE '0' END -- 入金票印刷対象フラグ（代引きの場合は'1'、代引き以外は'0'）
          , JCHT.NYKNHYOPRNTTRGTFLG = CASE WHEN (wAcptthdaibikiflg = wDaibiki OR wSingldnpyflg = wDaibiki) THEN '1' ELSE '0' END -- 入金票印刷対象フラグ（単体伝票締めまたは代引きの場合'1'、それ以外は'0'）
-- インボイス対応変更 end ABI
          , JCHT.NYKNHYOPRNTSUMIFLG = '0' -- 入金票印刷済フラグ
          , JCHT.NYKNHYOPRNTDD = NULL -- 入金票印刷日フラグ
          , JCHT.DNPYSTS = wStatus                                    -- 伝票ステータス
          , JCHT.DLVRYSTS = wStatus                                   -- 配達ステータス
          , JCHT.ZNKDLVRYDD = wZnkdlvrydd                             -- 前回配達日
          , JCHT.ZNKDLVRYHNS = wZnkdlvryhns                           -- 前回配達本数
          , JCHT.JIKAIDLVRYYTIDD = wJikaiDlvryytidd                   -- 次回配達予定日
-- インボイス対応変更 strat ABI
--          , JCHT.ZNKSHIMEDD = CASE WHEN wKaishuhohokbn = wDaibiki THEN wUnyoYmd ELSE NULL END -- 前回締日（代引きの場合運用日付、以外の場合NULL）
--          , JCHT.ZNKKURIKOSHIKIN = CASE WHEN wKaishuhohokbn  = wDaibiki THEN wGenzaizd - wKingaku ELSE NULL END -- 前回繰越金額（代引きの場合得意先実績Ｔ.現在残高 - (売上明細Ｔ.金額 - 売上明細Ｔ.消費税金額)、以外の場合NULLをセット）
          , JCHT.ZNKSHIMEDD = CASE WHEN wAcptthdaibikiflg = wDaibiki THEN wUnyoYmd ELSE NULL END -- 前回締日（代引きの場合、運用日付、以外の場合NULL）
          --, JCHT.ZNKKURIKOSHIKIN = CASE WHEN wAcptthdaibikiflg  = wDaibiki THEN wKingaku - wKairekin + wUrichoseikin ELSE NULL END -- 前回繰越金額（代引きの場合、請求残高Ｔ.消込残高合計金額 - 過入金額 + 売上調整金額、以外の場合NULLをセット）
          , JCHT.ZNKKURIKOSHIKIN = CASE WHEN wAcptthdaibikiflg  = wDaibiki THEN wKingaku - wMishimeNyknSum ELSE NULL END -- 前回繰越金額（代引きの場合、最新請求残高T.繰越金額 - 未締め入金金額のサマリ、以外の場合NULLをセット）
-- インボイス対応変更 end ABI
-- インボイス対応削除 strat ABI
--          , JCHT.ACPTTHDAIBIKIFLG = CASE WHEN wKaishuhohokbn = wDaibiki THEN '1' ELSE '0' END -- 受注時代引きフラグ(代引きの場合１、以外の場合０)
-- インボイス対応削除 end ABI
          , JCHT.KOSHAAPKIHONCD = iKoshaapkihoncd                     -- 更新者AP基本コード
          , JCHT.KOSHASHOPCD = iKoshashopcd                           -- 更新者ショップコード
          , JCHT.KOSHACD = ikoshacd                                   -- 更新担当者コード
-- 本線対応変更 start ABI
          --, JCHT.KSHNSYSTMYMD = TO_CHAR(SYSDATE, 'YYYYMMDD')          -- システム日付
          --, JCHT.KSHNSYSTMJIKAN = TO_CHAR(SYSDATE, 'hh24miss')        -- システム時間
          , JCHT.KSHNSYSTMYMD = TO_CHAR(CURRENT_DATE, 'YYYYMMDD')          -- システム日付
          , JCHT.KSHNSYSTMJIKAN = TO_CHAR(CURRENT_DATE, 'hh24miss')        -- システム時間
-- 本線対応変更 end ABI
          , JCHT.KSHNUNYOYMD = wUnyoYmd                               -- 更新運用日付
          , JCHT.KSHNIPADRSE = iKshnipadrse                           -- 更新IPアドレス
          , JCHT.KSHNPGMID = wPgId                                    -- 更新プログラムID
        WHERE
          JCHT.APKIHONCD = wApkihoncd
          AND JCHT.SHOPCD = wShopcd
          AND JCHT.ACPTDNPYNO = wAcptdnpyno
          AND JCHT.EDANO = wEdano;
        --異常処理
        EXCEPTION
          WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20001,'受注配達テーブルを更新できませんでした。');
      END;

-- インボイス対応変更 strat ABI
--      -- 代引きの場合の処理STR
--      IF wKaishuhohokbn = wDaibiki THEN
      -- 代引き、かつ単体伝票締めフラグが'0'（単体伝票出力しない）の場合の処理STR
      IF wAcptthdaibikiflg = wDaibiki AND wSingldnpyflg = '0' THEN
-- インボイス対応変更 end ABI
        -- 売上テーブル抽出
        OPEN SELECT_URIT;
          LOOP
            FETCH SELECT_URIT
              INTO
                wUriageApkihoncd,   -- AP基本コード
                wUriageShopcd,      -- ショップコード
                wUridnpyno,         -- 売上伝票番号
                wUriSkyShimeDyDm,   -- 月次締年月
                wUriSkyShimeDd,     -- 月次締日
                wUriSkyPrcsJissiDd, -- 月次処理実施日
                wUriKshnSystemYmd,  -- 更新システム日付
                wUriKshnSystemJikan,-- 更新システム時間
                wUriKshnPgmId;      -- 更新プログラムＩＤ
            IF SELECT_URIT%ROWCOUNT = 0 THEN
              EXIT;
            ELSIF SELECT_URIT%NOTFOUND THEN
              EXIT;
            ELSE
              -- 売上テーブル更新
              BEGIN
                UPDATE
                  S06T001T
                SET
                  SKYPRCSJISSIDD = 'DAIBIKI'                      -- 請求処理実施日
                  , URISHIMEPRCSDD = wJissiDd                     -- 売上締処理日
-- インボイス対応追加 start ABI
                  , ACPTDNPYNO = wAcptdnpyno                      -- 受注伝票番号
                  , ACPTDNPYEDANO = wEdano                        -- 枝番号
-- インボイス対応追加 end ABI
                  , KOSHAAPKIHONCD = iKoshaapkihoncd              -- 更新者AP基本コード
                  , KOSHASHOPCD = iKoshashopcd                    -- 更新者ショップコード
                  , KOSHACD = ikoshacd                            -- 更新担当者コード
-- 本線対応変更 start ABI
                  --, KSHNSYSTMYMD = TO_CHAR(SYSDATE, 'YYYYMMDD')   -- システム日付
                  --, KSHNSYSTMJIKAN = TO_CHAR(SYSDATE, 'hh24miss') -- システム時間
                  , KSHNSYSTMYMD = TO_CHAR(CURRENT_DATE, 'YYYYMMDD')   -- システム日付
                  , KSHNSYSTMJIKAN = TO_CHAR(CURRENT_DATE, 'hh24miss') -- システム時間
-- 本線対応変更 end ABI
                  , KSHNUNYOYMD = wUnyoYmd                        -- 更新運用日付
                  , KSHNIPADRSE = iKshnipadrse                    -- 更新IPアドレス
                  , KSHNPGMID = wPgId                             -- 更新プログラムID
                WHERE
                  APKIHONCD = wUriageApkihoncd
                  AND SHOPCD = wUriageShopcd
                  AND URIDNPYNO = wUridnpyno;
                EXCEPTION
                  WHEN OTHERS THEN
                    RAISE_APPLICATION_ERROR(-20001,'売上テーブルを更新できませんでした。');
              END;
              -- ログ出力(更新前情報) AP基本コード:ショップコード:得意先コード:受注伝票番号:売上伝票番号:締年月日:請求処理実施日:更新システム日付:更新システム時間:更新プログラムＩＤ
              S99_CHECK_LOG(wPgId, wUriageApkihoncd, wUriageShopcd, '1', 'U', '1', 'S06T001T', SELECT_URIT%ROWCOUNT, iKoshaapkihoncd, iKoshashopcd, ikoshacd, iKshnipadrse, wPgId , 'setJyucyuUriageTable:' || wUriageApkihoncd || ':' || wUriageShopcd || ':' || wTokuisakicd || ':' || wAcptdnpyno || ':' || wUridnpyno  || ':' || wUriSkyShimeDyDm || wUriSkyShimeDd || ':' || wUriSkyPrcsJissiDd || ':' || wUriKshnSystemYmd || ':' || wUriKshnSystemJikan || ':' || wUriKshnPgmId);

-- インボイス対応削除 start ABI
--              BEGIN
--                -- 最大の受注伝票明細番号を取得
--                SELECT
--                  MAX(ACPTDNPYMEISAINO)
--                INTO
--                  wMaxJyucyudnpymeisaino
--                FROM
--                  S04T002T
--                WHERE
--                  APKIHONCD = wApkihoncd
--                  AND SHOPCD = wShopcd
--                  AND ACPTDNPYNO = wAcptdnpyno;
--
--                --異常処理
--                EXCEPTION
--                  WHEN OTHERS THEN
--                    RAISE_APPLICATION_ERROR(-20001,'受注伝票明細番号を取得出来ませんでした。');
--              END;
--
--              -- 売上明細テーブル抽出処理
--              OPEN  SELECT_UAMT;
--                LOOP
--                  FETCH SELECT_UAMT
--                    INTO
--                      wUridnpymeisaiedano, -- 売上伝票明細枝番号
--                      wLctgrycd,           -- 大分類コード
--                      wMctgrycd,           -- 中分類コード
--                      wSctgrycd,           -- 小分類コード
--                      wShncd,              -- 商品コード
--                      wKatasiki,           -- 型式
--                      wKeijokbn,           -- 計上区分
--                      wSuryo,              -- 数量
--                      wHyojununt,          -- 標準単価
--                      wUnt,                -- 単価
--                      wKin,                -- 金額
--                      wUntshohitaxkin,     -- 単価消費税金額
--                      wShohitaxkin,        -- 消費税金額
--                      wTaxkbn,             -- 税区分
--                      wNyuryokumeisaiflg,  -- 入力明細フラグ
--                      wTanpinmeisaiflg,    -- 単品明細フラグ
--                      wStshncd,            -- セット商品コード
--                      wMeisaibiko,         -- 明細備考
--                      wUridnpyno,          -- 売上伝票番号
--                      wUriageEdno,         -- 売上枝番号
--                      wUridnpymeisaino;    -- 売上伝票明細番号
--
--                  IF SELECT_UAMT%ROWCOUNT = 0 THEN
--                    EXIT;
--                  ELSIF SELECT_UAMT%NOTFOUND THEN
--                    EXIT;
--                  ELSE
--                    -- 売上明細T.計上区分が01、または02の場合有償区分に1をセット
--                    IF wKeijokbn = '01' OR wKeijokbn = '02' THEN
--                      wYusyokbn := '1';
--                    END IF;
--
--                    -- 受注伝票明細番号にセットする値の処理
--                    wMeisaiCountUp := 0;
--                    wMeisaiCountUp := SELECT_UAMT%ROWCOUNT;
--
--                    -- 受注配達明細テーブル更新
--                    BEGIN
--                      INSERT INTO S04T002T
--                        (APKIHONCD
--                        , SHOPCD
--                        , ACPTDNPYNO
--                        , EDANO
--                        , ACPTDNPYMEISAINO
--                        , ACPTDNPYMEISAIEDANO
--                        , LCTGRYCD
--                        , MCTGRYCD
--                        , SCTGRYCD
--                        , SHNCD
--                        , HAIFUBUTSUCD
--                        , KATASIKI
--                        , KEIJOKBN
--                        , ACPTSURYO
--                        , HYOJUNUNT
--                        , ACPTUNT
--                        , ACPTKIN
--                        , ACPTUNTSHOHITAXKIN
--                        , ACPTSHOHITAXKIN
--                        , NOHINSURYO
--                        , NOHINUNT
--                        , NOHINKIN
--                        , NOHINUNTSHOHITAXKIN
--                        , NOHINSHOHITAXKIN
--                        , TAXKBN
--                        , YUSYOKBN
--                        , MEISAIKBN
--                        , NYURYOKUMEISAIFLG
--                        , TANPINMEISAIFLG
--                        , STSHNCD
--                        , MEISAIBIKO
--                        , DATSKSMOTOKBN
--                        , URIDNPYNO
--                        , URIDNPYEDANO
--                        , URIDNPYMEISAINO
--                        , URIDNPYMEISAIEDANO
--                        , YUKOKBN
--                        , TOSHAAPKIHONCD
--                        , TOSHASHOPCD
--                        , TOSHACD
--                        , INPTSYSTMYMD
--                        , INPTSYSTMJIKAN
--                        , INPTUNYOYMD
--                        , INPTIPADRSE
--                        , INPTPGMID
--                        , KOSHAAPKIHONCD
--                        , KOSHASHOPCD
--                        , KOSHACD
--                        , KSHNSYSTMYMD
--                        , KSHNSYSTMJIKAN
--                        , KSHNUNYOYMD
--                        , KSHNIPADRSE
--                        , KSHNPGMID)
--                      VALUES
--                        (wApkihoncd
--                        , wShopcd
--                        , wAcptdnpyno
--                        , wEdano
--                        , wMaxJyucyudnpymeisaino + wMeisaiCountUp
--                        , wUridnpymeisaiedano
--                        , wLctgrycd
--                        , wMctgrycd
--                        , wSctgrycd
--                        , wShncd
--                        , NULL
--                        , wKatasiki
--                        , wKeijokbn
--                        , wSuryo
--                        , wHyojununt
--                        , wUnt
--                        , wKin
--                        , wUntshohitaxkin
--                        , wShohitaxkin
--                        , 0
--                        , 0
--                        , 0
--                        , 0
--                        , 0
--                        , wTaxkbn
--                        , wYusyokbn
--                        , '2'
--                        , wNyuryokumeisaiflg
--                        , wTanpinmeisaiflg
--                        , wStshncd
--                        , wMeisaibiko
--                        , '06'
--                        , wUridnpyno
--                        , wUriageEdno
--                        , wUridnpymeisaino
--                        , wUridnpymeisaiedano
--                        , '1'
--                        , iKoshaapkihoncd
--                        , iKoshashopcd
--                        , ikoshacd
--                        , TO_CHAR(SYSDATE, 'YYYYMMDD')
--                        , TO_CHAR(SYSDATE, 'hh24miss')
--                        , wUnyoYmd
--                        , iKshnipadrse
--                        , wPgId
--                        , iKoshaapkihoncd
--                        , iKoshashopcd
--                        , ikoshacd
--                        , TO_CHAR(SYSDATE, 'YYYYMMDD')
--                        , TO_CHAR(SYSDATE, 'hh24miss')
--                        , wUnyoYmd
--                        , iKshnipadrse
--                        , wPgId);
--                    END;
--                  END IF;
--                END LOOP;
--              CLOSE SELECT_UAMT;
-- インボイス対応削除 end ABI
            END IF;
          END LOOP;
        CLOSE SELECT_URIT;
      END IF;
  END setJyucyuUriageTable;
END C0809B11;
/
exit;
