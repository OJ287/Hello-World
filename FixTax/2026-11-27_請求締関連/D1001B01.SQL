CREATE OR REPLACE PACKAGE BODY        D1001B01
/*******************************************************************************
 * 機能名           ：売上月次締処理
 *
 ******************************************************************************/
 AS
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
  /*****************************************************************************/
  /* ファンクション名 ：基本情報設定                                           */
  /* 引数             ：iApKihoncd           AP基本コード                      */
  /*                  ：iShopcd              ショップコード                    */
  /*                  ：iTnshacd             担当者コード                      */
  /*                  ：iIpadrse             ＩＰアドレス                      */
  /*                  ：iUserApKihoncd       担当者ＡＰ基本コード              */
  /*                  ：iUserShopcd          担当者ショップコード              */
  /*                  ：iGetsujishimedd      月次締日                          */
  /*****************************************************************************/
  PROCEDURE SetUserInfo(iApKihoncd      IN VARCHAR2,
                        iShopcd         IN VARCHAR2,
                        iTnshacd        IN VARCHAR2,
                        iIpadrse        IN VARCHAR2,
                        iUserApKihoncd  IN VARCHAR2,
                        iUserShopcd     IN VARCHAR2,
                        iGetsujishimedd IN VARCHAR2) IS

  BEGIN
    -- 共通変数削除
    vRecordS07T002T           := null;
    vRecordS07T002T.APKIHONCD := iApKihoncd; -- ＡＰ基本コード
    vRecordS07T002T.SHOPCD    := iShopcd; -- ショップコード

    --システム日付 と システム時間の検索
    SELECT TO_CHAR(SYSDATE, 'YYYYMMDD'), TO_CHAR(SYSDATE, 'hh24miss')
      INTO vRecordS07T002T.KSHNSYSTMYMD, vRecordS07T002T.KSHNSYSTMJIKAN
      FROM DUAL;
    vRecordS07T002T.KOSHAAPKIHONCD := iUserApKihoncd; -- 更新者ＡＰ基本コード
    vRecordS07T002T.KOSHASHOPCD    := iUserShopcd; -- 更新者ショップコード
    vRecordS07T002T.KOSHACD        := iTnshacd; -- 更新者コード
    vRecordS07T002T.KSHNUNYOYMD    := GetOperationdate(iApKihoncd, iShopcd); -- 更新運用日付
    vRecordS07T002T.KSHNIPADRSE    := iIpadrse; -- 更新ＩＰアドレス
    vRecordS07T002T.KSHNPGMID      := 'D1001B01'; -- 更新プログラムＩＤ

    vRecordS07T002T.SHIMEDYDM := SUBSTR(iGetsujishimedd, 1, 6); -- 締年月
    vRecordS07T002T.SHIMEDD   := SUBSTR(iGetsujishimedd, 7, 2); -- 締日

    --  締処理実施日
    vRecordS07T002T.SHIMEPRCSJISSIDD := GetOperationdate(iApKihoncd,
                                                         iShopcd);

    vRecordS07T002T.KONKAISHIMEDD := iGetsujishimedd; -- 今回締日
    vRecordS07T002T.YUKOKBN       := '1'; -- 有効区分

    vRecordS07T002T.TOSHAAPKIHONCD := vRecordS07T002T.KOSHAAPKIHONCD; -- 登録者ＡＰ基本コード
    vRecordS07T002T.TOSHASHOPCD    := vRecordS07T002T.KOSHASHOPCD; -- 登録者ショップコード
    vRecordS07T002T.TOSHACD        := vRecordS07T002T.KOSHACD; -- 登録者コード
    vRecordS07T002T.INPTSYSTMYMD   := vRecordS07T002T.KSHNSYSTMYMD; -- 登録システム日付: システム日付
    vRecordS07T002T.INPTSYSTMJIKAN := vRecordS07T002T.KSHNSYSTMJIKAN; -- 登録システム時間: システム時間
    vRecordS07T002T.INPTUNYOYMD    := vRecordS07T002T.KSHNUNYOYMD; -- 登録運用日付: システム管理マスタ.文字設定値
    vRecordS07T002T.INPTIPADRSE    := vRecordS07T002T.KSHNIPADRSE; -- 登録ＩＰアドレス: パラメータ．更新ＩＰアドレス
    vRecordS07T002T.INPTPGMID      := vRecordS07T002T.KSHNPGMID; -- 登録プログラムＩＤ

  END SetUserInfo;

  /*****************************************************************************/
  /* ファンクション名 ：売掛残高テーブル削除                                   */
  /* 引数             ：iGetsujishimedd    月次締日                            */
  /*****************************************************************************/
  PROCEDURE DeleteUZDT(iGetsujishimedd IN VARCHAR2) IS
    DYDM    VARCHAR2(6);
    DD      VARCHAR2(2);
  BEGIN
    DYDM    :=    TO_CHAR(TO_DATE(iGetsujishimedd, 'YYYYMMDD'), 'YYYYMM');
    DD      :=    TO_CHAR(TO_DATE(iGetsujishimedd, 'YYYYMMDD'), 'DD');
    -- 売掛残高テーブル削除
    DELETE S07T002T UZDT
     WHERE UZDT.APKIHONCD = vRecordS07T002T.APKIHONCD -- ＡＰ基本コード
       AND UZDT.SHOPCD = vRecordS07T002T.SHOPCD -- ショップコード
       AND UZDT.SHIMEDYDM = DYDM -- 締年月
       AND UZDT.SHIMEDD = DD; -- 締日
    nCntDelS07T002T    :=    nCntDelS07T002T    +   SQL%ROWCOUNT;
  END DeleteUZDT;

  /*****************************************************************************/
  /* ファンクション名 ：売上テーブル項目のクリア                               */
  /* 引数             ：iGetsujishimedd    月次締日                            */
  /*****************************************************************************/
  PROCEDURE ClearURGT(iGetsujishimedd IN VARCHAR2) IS
    DYDM    VARCHAR2(6);
    DD      VARCHAR2(2);
  BEGIN
    DYDM    :=    TO_CHAR(TO_DATE(iGetsujishimedd, 'YYYYMMDD'), 'YYYYMM');
    DD      :=    TO_CHAR(TO_DATE(iGetsujishimedd, 'YYYYMMDD'), 'DD');
    UPDATE S06T001T URGT -- 売上テーブル
       SET URGT.GETSUJISHIMEDYDM   = NULL, -- 月次締年月
           URGT.GETSUJISHIMEDD     = NULL, -- 月次締日
           URGT.GETSUJIPRCSJISSIDD = '00000000', -- 月次処理実施日

           URGT.KOSHAAPKIHONCD = vRecordS07T002T.KOSHAAPKIHONCD, -- 更新者ＡＰ基本コード
           URGT.KOSHASHOPCD    = vRecordS07T002T.KOSHASHOPCD, -- 更新者ショップコード
           URGT.KOSHACD        = vRecordS07T002T.KOSHACD, -- 更新者コード
           URGT.KSHNSYSTMYMD   = vRecordS07T002T.KSHNSYSTMYMD, -- 更新システム日付: システム日付
           URGT.KSHNSYSTMJIKAN = vRecordS07T002T.KSHNSYSTMJIKAN, -- 更新システム時間: システム時間
           URGT.KSHNUNYOYMD    = vRecordS07T002T.KSHNUNYOYMD, -- 更新運用日付: システム管理マスタ.文字設定値
           URGT.KSHNIPADRSE    = vRecordS07T002T.KSHNIPADRSE, -- 更新ＩＰアドレス: パラメータ．更新ＩＰアドレス
           URGT.KSHNPGMID      = vRecordS07T002T.KSHNPGMID -- 更新プログラムＩＤ
     WHERE URGT.APKIHONCD = vRecordS07T002T.APKIHONCD -- ＡＰ基本コード
       AND URGT.SHOPCD = vRecordS07T002T.SHOPCD -- ショップコード
       AND URGT.GETSUJISHIMEDYDM = DYDM -- 月次締年月
       AND URGT.GETSUJISHIMEDD = DD; -- 月次締日

  END ClearURGT;

  /*****************************************************************************/
  /* ファンクション名 ：入金明細テーブル項目のクリア                           */
  /* 引数             ：iGetsujishimedd    月次締日                            */
  /*****************************************************************************/
  PROCEDURE ClearNMST(iGetsujishimedd IN VARCHAR2) IS
    DYDM    VARCHAR2(6);
    DD      VARCHAR2(2);
  BEGIN
    DYDM    :=    TO_CHAR(TO_DATE(iGetsujishimedd, 'YYYYMMDD'), 'YYYYMM');
    DD      :=    TO_CHAR(TO_DATE(iGetsujishimedd, 'YYYYMMDD'), 'DD');
    UPDATE S10T001T NMST -- 入金明細テーブル
       SET NMST.GETSUJISHIMEDYDM   = NULL, -- 月次締年月
           NMST.GETSUJISHIMEDD     = NULL, -- 月次締日
           NMST.GETSUJIPRCSJISSIDD = '00000000', -- 月次処理実施日

           NMST.KOSHAAPKIHONCD = vRecordS07T002T.KOSHAAPKIHONCD, -- 更新者ＡＰ基本コード
           NMST.KOSHASHOPCD    = vRecordS07T002T.KOSHASHOPCD, -- 更新者ショップコード
           NMST.KOSHACD        = vRecordS07T002T.KOSHACD, -- 更新者コード
           NMST.KSHNSYSTMYMD   = vRecordS07T002T.KSHNSYSTMYMD, -- 更新システム日付: システム日付
           NMST.KSHNSYSTMJIKAN = vRecordS07T002T.KSHNSYSTMJIKAN, -- 更新システム時間: システム時間
           NMST.KSHNUNYOYMD    = vRecordS07T002T.KSHNUNYOYMD, -- 更新運用日付: システム管理マスタ.文字設定値
           NMST.KSHNIPADRSE    = vRecordS07T002T.KSHNIPADRSE, -- 更新ＩＰアドレス: パラメータ．更新ＩＰアドレス
           NMST.KSHNPGMID      = vRecordS07T002T.KSHNPGMID -- 更新プログラムＩＤ
     WHERE NMST.APKIHONCD = vRecordS07T002T.APKIHONCD -- ＡＰ基本コード
       AND NMST.SHOPCD = vRecordS07T002T.SHOPCD -- ショップコード
       AND NMST.GETSUJISHIMEDYDM = DYDM -- 月次締年月
       AND NMST.GETSUJISHIMEDD = DD; -- 月次締日

  END ClearNMST;

  /*****************************************************************************/
  /* ファンクション名 ：売上テーブル更新                                       */
  /* 引数             ：iGetsujishimedd    月次締日                            */
  /*****************************************************************************/
  PROCEDURE UpdateURGT(iGetsujishimedd IN VARCHAR2) IS
    DYDM    VARCHAR2(6);
    DD      VARCHAR2(2);
  BEGIN
    DYDM    :=    TO_CHAR(TO_DATE(iGetsujishimedd, 'YYYYMMDD'), 'YYYYMM');
    DD      :=    TO_CHAR(TO_DATE(iGetsujishimedd, 'YYYYMMDD'), 'DD');
-- ■:1 Isac)Shiba 2008-11-07
--    UPDATE S06T001T URGT -- 売上テーブル
    UPDATE /*+ INDEX(URGT S06T001T_IX1) */ S06T001T URGT -- 売上テーブル
-- ■:2 Isac)Shiba 2008-11-07
       SET URGT.GETSUJISHIMEDYDM   = DYDM, -- 月次締年月
           URGT.GETSUJISHIMEDD     = DD, -- 月次締日
           URGT.GETSUJIPRCSJISSIDD = vRecordS07T002T.SHIMEPRCSJISSIDD, -- 月次処理実施日

           URGT.KOSHAAPKIHONCD = vRecordS07T002T.KOSHAAPKIHONCD, -- 更新者ＡＰ基本コード
           URGT.KOSHASHOPCD    = vRecordS07T002T.KOSHASHOPCD, -- 更新者ショップコード
           URGT.KOSHACD        = vRecordS07T002T.KOSHACD, -- 更新者コード
           URGT.KSHNSYSTMYMD   = vRecordS07T002T.KSHNSYSTMYMD, -- 更新システム日付: システム日付
           URGT.KSHNSYSTMJIKAN = vRecordS07T002T.KSHNSYSTMJIKAN, -- 更新システム時間: システム時間
           URGT.KSHNUNYOYMD    = vRecordS07T002T.KSHNUNYOYMD, -- 更新運用日付: システム管理マスタ.文字設定値
           URGT.KSHNIPADRSE    = vRecordS07T002T.KSHNIPADRSE, -- 更新ＩＰアドレス: パラメータ．更新ＩＰアドレス
           URGT.KSHNPGMID      = vRecordS07T002T.KSHNPGMID -- 更新プログラムＩＤ
     WHERE URGT.APKIHONCD = vRecordS07T002T.APKIHONCD -- ＡＰ基本コード
       AND URGT.SHOPCD = vRecordS07T002T.SHOPCD -- ショップコード
-- ■:1 Isac)Shiba 2008-11-07
       AND URGT.TOKUISAKICD = vRecordS07T002T.TOKUISAKICD -- 得意先コード
-- ■:2 Isac)Shiba 2008-11-07
       AND URGT.URIDD <= iGetsujishimedd -- 売上日 <= 月次締日
       AND URGT.GETSUJIPRCSJISSIDD = '00000000'; -- 月次処理実施日
    nCntUpdS06T001T    :=    nCntUpdS06T001T    +   SQL%ROWCOUNT;
  END UpdateURGT;

  /*****************************************************************************/
  /* ファンクション名 ：入金明細テーブル更新                                   */
  /* 引数             ：iGetsujishimedd    月次締日                            */
  /*****************************************************************************/
  PROCEDURE UpdateNMST(iGetsujishimedd IN VARCHAR2) IS
    DYDM    VARCHAR2(6);
    DD      VARCHAR2(2);
  BEGIN
    DYDM    :=    TO_CHAR(TO_DATE(iGetsujishimedd, 'YYYYMMDD'), 'YYYYMM');
    DD      :=    TO_CHAR(TO_DATE(iGetsujishimedd, 'YYYYMMDD'), 'DD');
-- ■:1 Isac)Shiba 2008-11-07
--    UPDATE S10T001T NMST -- 入金明細テーブル
    UPDATE /*+ INDEX(NMST S10T001T_IX2) */ S10T001T NMST -- 入金明細テーブル
-- ■:2 Isac)Shiba 2008-11-07
       SET NMST.GETSUJISHIMEDYDM   = DYDM, -- 月次締年月
           NMST.GETSUJISHIMEDD     = DD, -- 月次締日
           NMST.GETSUJIPRCSJISSIDD = vRecordS07T002T.SHIMEPRCSJISSIDD, -- 月次処理実施日

           NMST.KOSHAAPKIHONCD = vRecordS07T002T.KOSHAAPKIHONCD, -- 更新者ＡＰ基本コード
           NMST.KOSHASHOPCD    = vRecordS07T002T.KOSHASHOPCD, -- 更新者ショップコード
           NMST.KOSHACD        = vRecordS07T002T.KOSHACD, -- 更新者コード
           NMST.KSHNSYSTMYMD   = vRecordS07T002T.KSHNSYSTMYMD, -- 更新システム日付: システム日付
           NMST.KSHNSYSTMJIKAN = vRecordS07T002T.KSHNSYSTMJIKAN, -- 更新システム時間: システム時間
           NMST.KSHNUNYOYMD    = vRecordS07T002T.KSHNUNYOYMD, -- 更新運用日付: システム管理マスタ.文字設定値
           NMST.KSHNIPADRSE    = vRecordS07T002T.KSHNIPADRSE, -- 更新ＩＰアドレス: パラメータ．更新ＩＰアドレス
           NMST.KSHNPGMID      = vRecordS07T002T.KSHNPGMID -- 更新プログラムＩＤ
     WHERE NMST.APKIHONCD = vRecordS07T002T.APKIHONCD -- ＡＰ基本コード
       AND NMST.SHOPCD = vRecordS07T002T.SHOPCD -- ショップコード
-- ■:1 Isac)Shiba 2008-11-07
       AND NMST.TOKUISAKICD = vRecordS07T002T.TOKUISAKICD -- 得意先コード
-- ■:2 Isac)Shiba 2008-11-07
       AND NMST.NYKNDD <= iGetsujishimedd -- 入金日 <= 月次締日
       AND NMST.GETSUJIPRCSJISSIDD = '00000000'; -- 月次処理実施日
    nCntUpdS10T001T    :=    nCntUpdS10T001T    +   SQL%ROWCOUNT;
  END UpdateNMST;

  /******************************************************************************/
  /* ファンクション名 ：売掛残高テーブルの前回情報取得                          */
  /******************************************************************************/
  PROCEDURE GetZhkdate IS
    -- 前回繰越金額の検索
    CURSOR SELECT_MAXDATE IS
      SELECT NVL(UZDT.SHIMEDYDM || UZDT.SHIMEDD, 0),  -- 前回締日
             NVL(UZDT.KURIKOSHIKIN, 0) -- 繰越金額
        FROM S07T002T UZDT -- 売掛残高テーブル
       WHERE UZDT.APKIHONCD = vRecordS07T002T.APKIHONCD -- ＡＰ基本コード
         AND UZDT.SHOPCD = vRecordS07T002T.SHOPCD -- ショップコード
         AND UZDT.TOKUISAKICD = vRecordS07T002T.TOKUISAKICD -- 得意先コード
       ORDER BY UZDT.APKIHONCD DESC,
                UZDT.SHOPCD DESC,
                UZDT.TOKUISAKICD DESC,
                UZDT.SHIMEDYDM DESC, -- 締年月
                UZDT.SHIMEDD DESC; -- 締日
--         AND UZDT.SHIMEDYDM || UZDT.SHIMEDD = vRecordS07T002T.ZNKSHIMEDD; -- 前回締日
  BEGIN

    vRecordS07T002T.ZNKKURIKOSHIKIN := 0; -- 前回繰越金額
    vRecordS07T002T.ZNKSHIMEDD := '00000000'; -- 前回締日
    -- 前回繰越金額日の検索
    OPEN SELECT_MAXDATE;
    LOOP
      FETCH SELECT_MAXDATE
        INTO vRecordS07T002T.ZNKSHIMEDD, -- 前回締日
             vRecordS07T002T.ZNKKURIKOSHIKIN; -- 前回繰越金額
      EXIT WHEN SELECT_MAXDATE%NOTFOUND;
      EXIT WHEN SELECT_MAXDATE%ROWCOUNT = 1;
    END LOOP;
    CLOSE SELECT_MAXDATE;
  END GetZhkdate;
  /*****************************************************************************/
  /* ファンクション名 ：売上明細テーブル                                       */
  /*****************************************************************************/
  PROCEDURE SelectUMST IS
    -- 売上明細テーブル検索
    CURSOR SELECT_UMST(pSEIHINSUI_DAIBUNRUI    S02M006T.MOJISETTEIATAI%TYPE
                      ,pSEIHINSUI_REG_SHNCD    S02M006T.MOJISETTEIATAI%TYPE
                      ,pSEIHINSUI_SLM_SHNCD    S02M006T.MOJISETTEIATAI%TYPE
                      ,pSEIHINSUI_SLM_7L_SHNCD S02M006T.MOJISETTEIATAI%TYPE
                      ,pSHN_RENTAL_DAIBUNRUI   S02M006T.MOJISETTEIATAI%TYPE
                      ,pSVR_RENTAL_DAIBUNRUI   S02M006T.MOJISETTEIATAI%TYPE
                      ,pSVR_KANREN_DAIBUNRUI   S02M006T.MOJISETTEIATAI%TYPE
-- ■:1 Base)Fujiwara 2016-06-28 通信会社連携対応
		      ,pCLB_SERVICE_DAIBUNRUI S02M006T.MOJISETTEIATAI%TYPE
                      ,pCLB_SERVICE_TYUBUNRUI S02M006T.MOJISETTEIATAI%TYPE) IS
-- ■:2 Base)Fujiwara 2016-06-28 通信会社連携対応
-- ■:1 Isac)Shiba 2008-11-07
--      SELECT NVL(SUM(CASE
      SELECT /*+ INDEX(URGT S06T001T_IX1) ORDERED USE_NL(UMST) */

-- ■:1 Base)Fujiwara 2016-06-28 通信会社連携対応
		     -- S_補足資料【カテゴリ集計】・[６.アクアクララ光の商品大分類・中分類判断]を参照
	    NVL(SUM(CASE
		       WHEN UMST.LCTGRYCD = pCLB_SERVICE_DAIBUNRUI AND
                            UMST.MCTGRYCD = pCLB_SERVICE_TYUBUNRUI THEN
                        NVL(UMST.KIN, 0)
                       ELSE
                        0
                     END),
                  0) KONKAISHUNODAIKOURIKIN, -- 今回収納代行売上金額
                     -- S_補足資料【カテゴリ集計】・[６.アクアクララ光の商品大分類・中分類判断]を参照
	    NVL(SUM(CASE
		       WHEN UMST.LCTGRYCD = pCLB_SERVICE_DAIBUNRUI AND
                            UMST.MCTGRYCD = pCLB_SERVICE_TYUBUNRUI THEN
                        NVL(UMST.SHOHITAXKIN, 0)
                       ELSE
                        0
                       END),
                  0) KONKAISHUNODAIKOSHOHITAXKIN, -- 今回収納代行消費税金額
-- ■:2 Base)Fujiwara 2016-06-28 通信会社連携対応
            NVL(SUM(CASE
-- ■:2 Isac)Shiba 2008-11-07
                     -- S_補足資料【カテゴリ集計】・[２.アクアクララ（製品水）レギュラーの商品コード判断]と[７.有料／無料商品の判断]を参照
                       WHEN UMST.SHNCD = pSEIHINSUI_REG_SHNCD AND
                            (KEIJOKBN = '01' OR KEIJOKBN = '02') THEN
                        UMST.SURYO
                       ELSE
                        0
                     END),
                 0) RGRAYURYO, -- レギュラー有料
             NVL(SUM(CASE
                     -- S_補足資料【カテゴリ集計】・[２.アクアクララ（製品水）レギュラーの商品コード判断]と[７.有料／無料商品の判断]を参照
                       WHEN UMST.SHNCD = pSEIHINSUI_REG_SHNCD AND KEIJOKBN <> '01' AND
                            KEIJOKBN <> '02' THEN
                        UMST.SURYO
                       ELSE
                        0
                     END),
                 0) RGRAMURYO, --  レギュラー無料

             NVL(SUM(CASE
                     -- S_補足資料【カテゴリ集計】・[３.アクアクララ（製品水）スリムの商品コード判断]と[７.有料／無料商品の判断]を参照
                       WHEN UMST.SHNCD = pSEIHINSUI_SLM_SHNCD AND
                            (KEIJOKBN = '01' OR KEIJOKBN = '02') THEN
                        UMST.SURYO
                       WHEN UMST.SHNCD = pSEIHINSUI_SLM_7L_SHNCD AND
                            (KEIJOKBN = '01' OR KEIJOKBN = '02') THEN
                        UMST.SURYO
                       ELSE
                        0
                     END),
                 0) SRMYURYO, -- スリム有料
             NVL(SUM(CASE
                     -- S_補足資料【カテゴリ集計】・[３.アクアクララ（製品水）スリムの商品コード判断]と[７.有料／無料商品の判断]を参照
                       WHEN UMST.SHNCD = pSEIHINSUI_SLM_SHNCD AND KEIJOKBN <> '01' AND
                            KEIJOKBN <> '02' THEN
                        UMST.SURYO
                       WHEN UMST.SHNCD = pSEIHINSUI_SLM_7L_SHNCD AND KEIJOKBN <> '01' AND
                            KEIJOKBN <> '02' THEN
                        UMST.SURYO
                       ELSE
                        0
                     END),
                 0) SRMMURYO, -- スリム無料

             NVL(SUM(CASE
                     -- S_補足資料【カテゴリ集計】・[１.製品水の商品大分類判断]と[２.アクアクララ（製品水）レギュラーの商品コード判断]と[３.アクアクララ（製品水）スリムの商品コード判断]と[７.有料／無料商品の判断]を参照
                       WHEN UMST.LCTGRYCD = pSEIHINSUI_DAIBUNRUI AND
                            UMST.SHNCD <> pSEIHINSUI_REG_SHNCD AND
                            UMST.SHNCD <> pSEIHINSUI_SLM_SHNCD AND
                            UMST.SHNCD <> pSEIHINSUI_SLM_7L_SHNCD AND
                            (KEIJOKBN = '01' OR KEIJOKBN = '02') THEN
                        UMST.SURYO
                       ELSE
                        0
                     END),
                 0) SONOTAYURYO, -- その他有料

             NVL(SUM(CASE
                     -- S_補足資料【カテゴリ集計】・[１.製品水の商品大分類判断]と[２.アクアクララ（製品水）レギュラーの商品コード判断]と[３.アクアクララ（製品水）スリムの商品コード判断]と[７.有料／無料商品の判断]を参照
                       WHEN UMST.LCTGRYCD = pSEIHINSUI_DAIBUNRUI AND
                            UMST.SHNCD <> pSEIHINSUI_REG_SHNCD AND
                            UMST.SHNCD <> pSEIHINSUI_SLM_SHNCD AND
                            UMST.SHNCD <> pSEIHINSUI_SLM_7L_SHNCD AND
                            KEIJOKBN <> '01' AND
                            KEIJOKBN <> '02' THEN
                        UMST.SURYO
                       ELSE
                        0
                     END),
                 0) SONOTAMURYO, -- その他無料

             NVL(SUM(CASE
                     -- S_補足資料【カテゴリ集計】・[１.製品水の商品大分類判断]を参照
                       WHEN UMST.LCTGRYCD = pSEIHINSUI_DAIBUNRUI THEN
                        NVL(UMST.KIN, 0)
                       ELSE
                        0
                     END),
                 0) SEIHINMIZUURI, -- 製品水売上

             NVL(SUM(CASE
                     -- S_補足資料【カテゴリ集計】・[６.レンタル料の商品大分類判断]を参照
                       WHEN UMST.LCTGRYCD = pSHN_RENTAL_DAIBUNRUI OR
                            UMST.LCTGRYCD = pSVR_RENTAL_DAIBUNRUI THEN
                        NVL(UMST.KIN, 0)
                       ELSE
                        0
                     END),
                 0) RTLRKN, -- レンタル料金

             NVL(SUM(CASE
                     -- S_補足資料【カテゴリ集計】・[１.製品水の商品大分類判断]と[６.レンタル料の商品大分類判断]を参照
                       WHEN UMST.LCTGRYCD <> pSEIHINSUI_DAIBUNRUI AND
                            UMST.LCTGRYCD <> pSHN_RENTAL_DAIBUNRUI AND
                            UMST.LCTGRYCD <> pSVR_RENTAL_DAIBUNRUI THEN
                        NVL(UMST.KIN, 0)
                       ELSE
                        0
                     END),
                 0) SONOTAURIKIN, -- その他売上金額

             NVL(SUM(CASE
                     -- 商品が製品水のレギュラーで、計上区分がキャンペーン（02）を対象とする。
                       WHEN UMST.SHNCD = pSEIHINSUI_REG_SHNCD AND KEIJOKBN = '02' THEN
                        UMST.SURYO
                       ELSE
                        0
                     END),
                 0) AQCRGRAKYNPNSURYO, -- アクアクララレギュラーキャンペーン数量

             NVL(SUM(CASE
                     -- 商品が製品水のレギュラーで、計上区分がサービス（03）を対象とする。
                       WHEN UMST.SHNCD = pSEIHINSUI_REG_SHNCD AND KEIJOKBN = '03' THEN
                        UMST.SURYO
                       ELSE
                        0
                     END),
                 0) AQCRGRASVSURYO, -- アクアクララレギュラーサービス数量

             NVL(SUM(CASE
                     -- 商品が製品水のレギュラーで、計上区分がプロモーション（04）を対象とする。
                       WHEN UMST.SHNCD = pSEIHINSUI_REG_SHNCD AND KEIJOKBN = '04' THEN
                        UMST.SURYO
                       ELSE
                        0
                     END),
                 0) AQCRGRAPUROMOSHONSURYO, -- アクアクララレギュラープロモーション数量

             NVL(SUM(CASE
                     -- 商品が製品水のレギュラーで、計上区分が試飲（05）を対象とする。
                       WHEN UMST.SHNCD = pSEIHINSUI_REG_SHNCD AND KEIJOKBN = '05' THEN
                        UMST.SURYO
                       ELSE
                        0
                     END),
                 0) AQCRGRASIINSURYO, -- アクアクララレギュラー試飲数量

             NVL(SUM(CASE
                     -- 商品が製品水のレギュラーで、計上区分がメンテ（06）を対象とする。
                       WHEN UMST.SHNCD = pSEIHINSUI_REG_SHNCD AND KEIJOKBN = '06' THEN
                        UMST.SURYO
                       ELSE
                        0
                     END),
                 0) AQCRGRAMNTESURYO, -- アクアクララレギュラーメンテ数量

             NVL(SUM(CASE
                     -- 商品が製品水のレギュラーで、計上区分が交換（07）を対象とする。
                       WHEN UMST.SHNCD = pSEIHINSUI_REG_SHNCD AND KEIJOKBN = '07' THEN
                        UMST.SURYO
                       ELSE
                        0
                     END),
                 0) AQCRGRAKOKANSURYO, -- アクアクララレギュラー交換数量

             NVL(SUM(CASE
                     -- 商品が製品水のレギュラーで、計上区分が売上（01）を対象とする。
                       WHEN UMST.SHNCD = pSEIHINSUI_REG_SHNCD AND KEIJOKBN = '01' THEN
                        UMST.SURYO
                       ELSE
                        0
                     END),
                 0) AQCRGRAURISURYO, -- アクアクララレギュラー売上数量
             NVL(SUM(CASE
                     -- 商品が製品水のスリムで、計上区分がキャンペーン（02）を対象とする。
                       WHEN UMST.SHNCD = pSEIHINSUI_SLM_SHNCD AND KEIJOKBN = '02' THEN
                        UMST.SURYO
                       WHEN UMST.SHNCD = pSEIHINSUI_SLM_7L_SHNCD AND KEIJOKBN = '02' THEN
                        UMST.SURYO
                       ELSE
                        0
                     END),
                 0) AQCSRMKYNPNSURYO, -- アクアクララスリムキャンペーン数量

             NVL(SUM(CASE
                     -- 商品が製品水のスリムで、計上区分がサービス（03）を対象とする。
                       WHEN UMST.SHNCD = pSEIHINSUI_SLM_SHNCD AND KEIJOKBN = '03' THEN
                        UMST.SURYO
                       WHEN UMST.SHNCD = pSEIHINSUI_SLM_7L_SHNCD AND KEIJOKBN = '03' THEN
                        UMST.SURYO
                       ELSE
                        0
                     END),
                 0) AQCSRMSVSURYO, -- アクアクララスリムサービス数量

             NVL(SUM(CASE
                     -- 商品が製品水のスリムで、計上区分がプロモーション（04）を対象とする。
                       WHEN UMST.SHNCD = pSEIHINSUI_SLM_SHNCD AND KEIJOKBN = '04' THEN
                        UMST.SURYO
                       WHEN UMST.SHNCD = pSEIHINSUI_SLM_7L_SHNCD AND KEIJOKBN = '04' THEN
                        UMST.SURYO
                       ELSE
                        0
                     END),
                 0) AQCSRMPUROMOSHONSURYO, -- アクアクララスリムプロモーション数量

             NVL(SUM(CASE
                     -- 商品が製品水のスリムで、計上区分が試飲（05）を対象とする。
                       WHEN UMST.SHNCD = pSEIHINSUI_SLM_SHNCD AND KEIJOKBN = '05' THEN
                        UMST.SURYO
                       WHEN UMST.SHNCD = pSEIHINSUI_SLM_7L_SHNCD AND KEIJOKBN = '05' THEN
                        UMST.SURYO
                       ELSE
                        0
                     END),
                 0) AQCSRMSIINSURYO, -- アクアクララスリム試飲数量

             NVL(SUM(CASE
                     -- 商品が製品水のスリムで、計上区分がメンテ（06）を対象とする。
                       WHEN UMST.SHNCD = pSEIHINSUI_SLM_SHNCD AND KEIJOKBN = '06' THEN
                        UMST.SURYO
                       WHEN UMST.SHNCD = pSEIHINSUI_SLM_7L_SHNCD AND KEIJOKBN = '06' THEN
                        UMST.SURYO
                       ELSE
                        0
                     END),
                 0) AQCSRMMNTESURYO, -- アクアクララスリムメンテ数量

             NVL(SUM(CASE
                     -- 商品が製品水のスリムで、計上区分が交換（07）を対象とする。
                       WHEN UMST.SHNCD = pSEIHINSUI_SLM_SHNCD AND KEIJOKBN = '07' THEN
                        UMST.SURYO
                       WHEN UMST.SHNCD = pSEIHINSUI_SLM_7L_SHNCD AND KEIJOKBN = '07' THEN
                        UMST.SURYO
                       ELSE
                        0
                     END),
                 0) AQCSRMKOKANSURYO, -- アクアクララスリム交換数量

             NVL(SUM(CASE
                     -- 商品が製品水のスリムで、計上区分が売上（01）を対象とする。
                       WHEN UMST.SHNCD = pSEIHINSUI_SLM_SHNCD AND KEIJOKBN = '01' THEN
                        UMST.SURYO
                       WHEN UMST.SHNCD = pSEIHINSUI_SLM_7L_SHNCD AND KEIJOKBN = '01' THEN
                        UMST.SURYO
                       ELSE
                        0
                     END),
                 0) AQCSRMURISURYO, -- アクアクララスリム売上数量
             NVL(SUM(CASE
                     -- 商品が製品水のレギュラー、スリム以外で、計上区分がキャンペーン（02）を対象とする。
-- ■:1 Base)Arai 2010-06-15
--                       WHEN UMST.SHNCD <> pSEIHINSUI_REG_SHNCD AND
--                            UMST.SHNCD <> pSEIHINSUI_SLM_SHNCD AND KEIJOKBN = '02' THEN
                       WHEN UMST.LCTGRYCD = pSEIHINSUI_DAIBUNRUI AND
                            UMST.SHNCD <> pSEIHINSUI_REG_SHNCD AND
                            UMST.SHNCD <> pSEIHINSUI_SLM_SHNCD AND
                            UMST.SHNCD <> pSEIHINSUI_SLM_7L_SHNCD AND
                            KEIJOKBN = '02' THEN
-- ■:2 Base)Arai 2010-06-15
                        UMST.SURYO
                       ELSE
                        0
                     END),
                 0) ETCSHNMZKYNPNSURYO, -- その他製品水キャンペーン数量

             NVL(SUM(CASE
                     -- 商品が製品水のレギュラー、スリム以外で、計上区分がサービス（03）を対象とする。
-- ■:1 Base)Arai 2010-06-15
--                       WHEN UMST.SHNCD <> pSEIHINSUI_REG_SHNCD AND
--                            UMST.SHNCD <> pSEIHINSUI_SLM_SHNCD AND KEIJOKBN = '03' THEN
                       WHEN UMST.LCTGRYCD = pSEIHINSUI_DAIBUNRUI AND
                            UMST.SHNCD <> pSEIHINSUI_REG_SHNCD AND
                            UMST.SHNCD <> pSEIHINSUI_SLM_SHNCD AND
                            UMST.SHNCD <> pSEIHINSUI_SLM_7L_SHNCD AND
                            KEIJOKBN = '03' THEN
-- ■:2 Base)Arai 2010-06-15
                        UMST.SURYO
                       ELSE
                        0
                     END),
                 0) ETCSHNMZSVSURYO, -- その他製品水サービス数量

             NVL(SUM(CASE
                     -- 商品が製品水のレギュラー、スリム以外で、計上区分がプロモーション（04）を対象とする。
-- ■:1 Base)Arai 2010-06-15
--                       WHEN UMST.SHNCD <> pSEIHINSUI_REG_SHNCD AND
--                            UMST.SHNCD <> pSEIHINSUI_SLM_SHNCD AND KEIJOKBN = '04' THEN
                       WHEN UMST.LCTGRYCD = pSEIHINSUI_DAIBUNRUI AND
                            UMST.SHNCD <> pSEIHINSUI_REG_SHNCD AND
                            UMST.SHNCD <> pSEIHINSUI_SLM_SHNCD AND
                            UMST.SHNCD <> pSEIHINSUI_SLM_7L_SHNCD AND
                            KEIJOKBN = '04' THEN
-- ■:2 Base)Arai 2010-06-15
                        UMST.SURYO
                       ELSE
                        0
                     END),
                 0) ETCSHNMZPUROMOSHONSURYO, -- その他製品水プロモーション数量

             NVL(SUM(CASE
                     -- 商品が製品水のレギュラー、スリム以外で、計上区分が試飲（05）を対象とする。
-- ■:1 Base)Arai 2010-06-15
--                       WHEN UMST.SHNCD <> pSEIHINSUI_REG_SHNCD AND
--                            UMST.SHNCD <> pSEIHINSUI_SLM_SHNCD AND KEIJOKBN = '05' THEN
                       WHEN UMST.LCTGRYCD = pSEIHINSUI_DAIBUNRUI AND
                            UMST.SHNCD <> pSEIHINSUI_REG_SHNCD AND
                            UMST.SHNCD <> pSEIHINSUI_SLM_SHNCD AND
                            UMST.SHNCD <> pSEIHINSUI_SLM_7L_SHNCD AND
                            KEIJOKBN = '05' THEN
-- ■:2 Base)Arai 2010-06-15
                        UMST.SURYO
                       ELSE
                        0
                     END),
                 0) ETCSHNMZSIINSURYO, -- その他製品水試飲数量

             NVL(SUM(CASE
                     -- 商品が製品水のレギュラー、スリム以外で、計上区分がメンテ（06）を対象とする。
-- ■:1 Base)Arai 2010-06-15
--                       WHEN UMST.SHNCD <> pSEIHINSUI_REG_SHNCD AND
--                            UMST.SHNCD <> pSEIHINSUI_SLM_SHNCD AND KEIJOKBN = '06' THEN
                       WHEN UMST.LCTGRYCD = pSEIHINSUI_DAIBUNRUI AND
                            UMST.SHNCD <> pSEIHINSUI_REG_SHNCD AND
                            UMST.SHNCD <> pSEIHINSUI_SLM_SHNCD AND
                            UMST.SHNCD <> pSEIHINSUI_SLM_7L_SHNCD AND
                            KEIJOKBN = '06' THEN
-- ■:2 Base)Arai 2010-06-15
                        UMST.SURYO
                       ELSE
                        0
                     END),
                 0) ETCSHNMZMNTESURYO, -- その他製品水メンテ数量

             NVL(SUM(CASE
                     -- 商品が製品水のレギュラー、スリム以外で無料分、計上区分が交換（07）を対象とする。
-- ■:1 Base)Arai 2010-06-15
--                       WHEN UMST.SHNCD <> pSEIHINSUI_REG_SHNCD AND
--                            UMST.SHNCD <> pSEIHINSUI_SLM_SHNCD AND KEIJOKBN <> '01' AND
--                            KEIJOKBN <> '02' AND KEIJOKBN = '07' THEN
                       WHEN UMST.LCTGRYCD = pSEIHINSUI_DAIBUNRUI AND
                            UMST.SHNCD <> pSEIHINSUI_REG_SHNCD AND
                            UMST.SHNCD <> pSEIHINSUI_SLM_SHNCD AND
                            UMST.SHNCD <> pSEIHINSUI_SLM_7L_SHNCD AND
                            KEIJOKBN = '07' THEN
-- ■:2 Base)Arai 2010-06-15
                        UMST.SURYO
                       ELSE
                        0
                     END),
                 0) ETCSHNMZKOKANSURYO, -- その他製品水交換数量

             NVL(SUM(CASE
                     -- 商品が製品水のレギュラー、スリム以外で、計上区分が売上（01）を対象とする。
-- ■:1 Base)Arai 2010-06-15
--                       WHEN UMST.SHNCD <> pSEIHINSUI_REG_SHNCD AND
--                            UMST.SHNCD <> pSEIHINSUI_SLM_SHNCD AND KEIJOKBN = '01' THEN
                       WHEN UMST.LCTGRYCD = pSEIHINSUI_DAIBUNRUI AND
                            UMST.SHNCD <> pSEIHINSUI_REG_SHNCD AND
                            UMST.SHNCD <> pSEIHINSUI_SLM_SHNCD AND
                            UMST.SHNCD <> pSEIHINSUI_SLM_7L_SHNCD AND
                            KEIJOKBN = '01' THEN
-- ■:2 Base)Arai 2010-06-15
                        UMST.SURYO
                       ELSE
                        0
                     END),
                 0) ETCSHNMZURISURYO, -- その他製品水売上数量

             NVL(SUM(CASE
                     -- S_補足資料【カテゴリ集計】・[２.アクアクララ（製品水）レギュラーの商品コード判断]と[７.有料／無料商品の判断]を参照
                       WHEN UMST.SHNCD = pSEIHINSUI_REG_SHNCD AND
                            (KEIJOKBN = '01' OR KEIJOKBN = '02') THEN
                        NVL(UMST.KIN, 0)
                       ELSE
                        0
                     END),
                 0) SHNMZRGRAKIN, -- 製品水レギュラー金額

             NVL(SUM(CASE
                     -- S_補足資料【カテゴリ集計】・[３.アクアクララ（製品水）スリムの商品コード判断]と[７.有料／無料商品の判断]を参照
                       WHEN UMST.SHNCD = pSEIHINSUI_SLM_SHNCD AND
                            (KEIJOKBN = '01' OR KEIJOKBN = '02') THEN
                        NVL(UMST.KIN, 0)
                       WHEN UMST.SHNCD = pSEIHINSUI_SLM_7L_SHNCD AND
                            (KEIJOKBN = '01' OR KEIJOKBN = '02') THEN
                        NVL(UMST.KIN, 0)
                       ELSE
                        0
                     END),
                 0) SHNMZSRMKIN, -- 製品水スリム金額

             NVL(SUM(CASE
                     -- 商品が製品水のレギュラー、スリム以外で有料分を対象とする。
                       WHEN UMST.LCTGRYCD = pSEIHINSUI_DAIBUNRUI AND
                            UMST.SHNCD <> pSEIHINSUI_REG_SHNCD AND
                            UMST.SHNCD <> pSEIHINSUI_SLM_SHNCD AND
                            UMST.SHNCD <> pSEIHINSUI_SLM_7L_SHNCD AND
                            (KEIJOKBN = '01' OR KEIJOKBN = '02') THEN
                        NVL(UMST.KIN, 0)
                       ELSE
                        0
                     END),
                 0) SHNMZSONOTAKIN, -- 製品水その他金額

             NVL(SUM(CASE
                     -- S_補足資料【カテゴリ集計】・ [５.サーバーレンタル料の商品大分類判断]を参照
                       WHEN UMST.LCTGRYCD = pSVR_RENTAL_DAIBUNRUI THEN
                        NVL(UMST.KIN, 0)
                       ELSE
                        0
                     END),
                 0) RNTLRWSVKIN, -- レンタル料サーバー金額
             NVL(SUM(CASE
                     -- S_補足資料【カテゴリ集計】・[４.商品レンタル料の商品大分類判断]を参照
                       WHEN UMST.LCTGRYCD = pSHN_RENTAL_DAIBUNRUI THEN
                        NVL(UMST.KIN, 0)
                       ELSE
                        0
                     END),
                 0) RNTLRHKSHNKIN, -- レンタル料他商品金額

             NVL(SUM(CASE
                     -- 商品がサーバー関連分で計上区分がキャンペーン（02）以外を対象とする。
                       WHEN UMST.LCTGRYCD = pSVR_KANREN_DAIBUNRUI AND KEIJOKBN <> '02' THEN
                        NVL(UMST.KIN, 0)
                       ELSE
                        0
                     END),
                 0) SNTSHNWSVKIN, -- その他商品サーバー金額

             NVL(SUM(CASE
                     -- 商品が他商品分（製品水、レンタル料、サーバー関連以外）で計上区分がキャンペーン（02）以外を対象とする。
                       WHEN UMST.LCTGRYCD <> pSEIHINSUI_DAIBUNRUI AND
                            UMST.LCTGRYCD <> pSHN_RENTAL_DAIBUNRUI AND
                            UMST.LCTGRYCD <> pSVR_RENTAL_DAIBUNRUI AND
                            UMST.LCTGRYCD <> pSVR_KANREN_DAIBUNRUI AND KEIJOKBN <> '02' THEN
                        NVL(UMST.KIN, 0)
                       ELSE
                        0
                     END),
                 0) SNTSHNHKSHNKIN, -- その他商品他商品金額

             NVL(SUM(CASE
                     -- 商品が他商品分（製品水、レンタル料、サーバー関連以外）で計上区分がキャンペーン（02）以外を対象とする。
                       WHEN UMST.LCTGRYCD <> pSEIHINSUI_DAIBUNRUI AND
                            UMST.LCTGRYCD <> pSHN_RENTAL_DAIBUNRUI AND
                            UMST.LCTGRYCD <> pSVR_RENTAL_DAIBUNRUI AND
                            UMST.LCTGRYCD <> pSVR_KANREN_DAIBUNRUI AND KEIJOKBN = '02' THEN
                        NVL(UMST.KIN, 0)
                       ELSE
                        0
                     END),
                 0) SNTSHNKNPHNKIN -- その他商品キャンペーン品金額
        FROM S06T001T URGT, -- 売上テーブル
             S06T002T UMST -- 売上明細テーブル
       WHERE URGT.APKIHONCD = UMST.APKIHONCD -- ＡＰ基本コード
         AND URGT.SHOPCD = UMST.SHOPCD -- ショップコード
         AND URGT.URIDNPYNO = UMST.URIDNPYNO -- 売上伝票番号
         AND URGT.EDANO = UMST.EDANO -- 枝番号
         AND UMST.TANPINMEISAIFLG = '1' -- 単品明細フラグ
         AND URGT.APKIHONCD = vRecordS07T002T.APKIHONCD -- ＡＰ基本コード
         AND URGT.SHOPCD = vRecordS07T002T.SHOPCD -- ショップコード
         AND URGT.TOKUISAKICD = vRecordS07T002T.TOKUISAKICD -- 得意先コード
-- ■:1 Isac)Shiba 2008-02-21
         AND URGT.GETSUJISHIMEDYDM   = vRecordS07T002T.SHIMEDYDM -- 月次締年月
         AND URGT.GETSUJISHIMEDD     = vRecordS07T002T.SHIMEDD -- 月次締日
-- ■:2 Isac)Shiba 2008-02-21
         AND URGT.GETSUJIPRCSJISSIDD = vRecordS07T002T.SHIMEPRCSJISSIDD -- 月次処理実施日
       GROUP BY URGT.APKIHONCD, URGT.SHOPCD, URGT.TOKUISAKICD -- 得意先コード
       ORDER BY URGT.APKIHONCD, URGT.SHOPCD, URGT.TOKUISAKICD; -- 得意先コード

    SEIHINSUI_DAIBUNRUI    S02M006T.MOJISETTEIATAI%TYPE;     -- S_補足資料【カテゴリ集計】・[１.製品水の商品大分類判断]
    SEIHINSUI_REG_SHNCD    S02M006T.MOJISETTEIATAI%TYPE;     -- S_補足資料【カテゴリ集計】・[２.アクアクララ（製品水）レギュラーの商品コード判断]
    SEIHINSUI_SLM_SHNCD    S02M006T.MOJISETTEIATAI%TYPE;     -- S_補足資料【カテゴリ集計】・[３.アクアクララ（製品水）スリムの商品コード判断]
    SEIHINSUI_SLM_7L_SHNCD S02M006T.MOJISETTEIATAI%TYPE;
    SHN_RENTAL_DAIBUNRUI   S02M006T.MOJISETTEIATAI%TYPE;     -- S_補足資料【カテゴリ集計】・[４.商品レンタル料の商品大分類判断]
    SVR_RENTAL_DAIBUNRUI   S02M006T.MOJISETTEIATAI%TYPE;     -- S_補足資料【カテゴリ集計】・[５.サーバーレンタル料の商品大分類判断]
    SVR_KANREN_DAIBUNRUI   S02M006T.MOJISETTEIATAI%TYPE;     -- S_補足資料【カテゴリ集計】・[８.サーバー関連の商品大分類判断]
-- ■:1 Base)Fujiwara 2016-06-28 通信会社連携対応
    CLB_SERVICE_DAIBUNRUI S02M006T.MOJISETTEIATAI%TYPE;     -- S_補足資料【カテゴリ集計】・[６.アクアクララ光の商品大分類判断]
    CLB_SERVICE_TYUBUNRUI S02M006T.MOJISETTEIATAI%TYPE;     -- S_補足資料【カテゴリ集計】・[６.アクアクララ光の商品中分類判断]
-- ■:2 Base)Fujiwara 2016-06-28 通信会社連携対応

  BEGIN
-- ■:1 Base)Fujiwara 2016-06-28 通信会社連携対応
    vRecordS07T002T.KONKAISHUNODAIKOURIKIN  := '0'; -- 今回収納代行売上金額
    vRecordS07T002T.KONKAISHUNODAIKOSHOHITAXKIN
					    := '0'; -- 今回収納代行消費税金額
-- ■:2 Base)Fujiwara 2016-06-28 通信会社連携対応
    vRecordS07T002T.RGRAYURYO               := '0'; -- レギュラー有料
    vRecordS07T002T.RGRAMURYO               := '0'; -- レギュラー無料
    vRecordS07T002T.SRMYURYO                := '0'; -- スリム有料
    vRecordS07T002T.SRMMURYO                := '0'; -- スリム無料
    vRecordS07T002T.SONOTAYURYO             := '0'; -- その他有料
    vRecordS07T002T.SONOTAMURYO             := '0'; -- その他無料
    vRecordS07T002T.SEIHINMIZUURI           := '0'; -- 製品水売上
    vRecordS07T002T.RTLRKN                  := '0'; -- レンタル料金
    vRecordS07T002T.SONOTAURIKIN            := '0'; -- その他売上金額
    vRecordS07T002T.AQCRGRAKYNPNSURYO       := '0'; -- アクアクララレギュラーキャンペーン数量
    vRecordS07T002T.AQCRGRASVSURYO          := '0'; -- アクアクララレギュラーサービス数量
    vRecordS07T002T.AQCRGRAPUROMOSHONSURYO  := '0'; -- アクアクララレギュラープロモーション数量
    vRecordS07T002T.AQCRGRASIINSURYO        := '0'; -- アクアクララレギュラー試飲数量
    vRecordS07T002T.AQCRGRAMNTESURYO        := '0'; -- アクアクララレギュラーメンテ数量
    vRecordS07T002T.AQCRGRAKOKANSURYO       := '0'; -- アクアクララレギュラー交換数量
    vRecordS07T002T.AQCRGRAURISURYO         := '0'; -- アクアクララレギュラー売上数量
    vRecordS07T002T.AQCSRMKYNPNSURYO        := '0'; -- アクアクララスリムキャンペーン数量
    vRecordS07T002T.AQCSRMSVSURYO           := '0'; -- アクアクララスリムサービス数量
    vRecordS07T002T.AQCSRMPUROMOSHONSURYO   := '0'; -- アクアクララスリムプロモーション数量
    vRecordS07T002T.AQCSRMSIINSURYO         := '0'; -- アクアクララスリム試飲数量
    vRecordS07T002T.AQCSRMMNTESURYO         := '0'; -- アクアクララスリムメンテ数量
    vRecordS07T002T.AQCSRMKOKANSURYO        := '0'; -- アクアクララスリム交換数量
    vRecordS07T002T.AQCSRMURISURYO          := '0'; -- アクアクララスリム売上数量
    vRecordS07T002T.ETCSHNMZKYNPNSURYO      := '0'; -- その他製品水キャンペーン数量
    vRecordS07T002T.ETCSHNMZSVSURYO         := '0'; -- その他製品水サービス数量
    vRecordS07T002T.ETCSHNMZPUROMOSHONSURYO := '0'; -- その他製品水プロモーション数量
    vRecordS07T002T.ETCSHNMZSIINSURYO       := '0'; -- その他製品水試飲数量
    vRecordS07T002T.ETCSHNMZMNTESURYO       := '0'; -- その他製品水メンテ数量
    vRecordS07T002T.ETCSHNMZKOKANSURYO      := '0'; -- その他製品水交換数量
    vRecordS07T002T.ETCSHNMZURISURYO        := '0'; -- その他製品水売上数量
    vRecordS07T002T.SHNMZRGRAKIN            := '0'; -- 製品水レギュラー金額
    vRecordS07T002T.SHNMZSRMKIN             := '0'; -- 製品水スリム金額
    vRecordS07T002T.SHNMZSONOTAKIN          := '0'; -- 製品水その他金額
    vRecordS07T002T.RNTLRWSVKIN             := '0'; -- レンタル料サーバー金額
    vRecordS07T002T.RNTLRHKSHNKIN           := '0'; -- レンタル料他商品金額
    vRecordS07T002T.SNTSHNWSVKIN            := '0'; -- その他商品サーバー金額
    vRecordS07T002T.SNTSHNHKSHNKIN          := '0'; -- その他商品他商品金額
    vRecordS07T002T.SNTSHNKNPHNKIN          := '0'; -- その他商品キャンペーン品金額

    -- システム管理マスタより各種コードを取得
    SELECT  SYKM.MOJISETTEIATAI
      INTO  SEIHINSUI_DAIBUNRUI
       FROM S02M006T SYKM
      WHERE SYKM.APKIHONCD = '000'
        AND SYKM.SHOPCD = '000'
        AND SYKM.RCRDKBN = 'M' -- レコード区分
        AND SYKM.SETTEINO = '096' -- 設定番号
        AND SYKM.SETTEICD = '1';
    SELECT  SYKM.MOJISETTEIATAI
      INTO  SEIHINSUI_REG_SHNCD
       FROM S02M006T SYKM
      WHERE SYKM.APKIHONCD = '000'
        AND SYKM.SHOPCD = '000'
        AND SYKM.RCRDKBN = 'M' -- レコード区分
        AND SYKM.SETTEINO = '064' -- 設定番号
        AND SYKM.SETTEICD = '1';
    SELECT  SYKM.MOJISETTEIATAI
      INTO  SEIHINSUI_SLM_SHNCD
       FROM S02M006T SYKM
      WHERE SYKM.APKIHONCD = '000'
        AND SYKM.SHOPCD = '000'
        AND SYKM.RCRDKBN = 'M' -- レコード区分
        AND SYKM.SETTEINO = '064' -- 設定番号
        AND SYKM.SETTEICD = '2';
    SELECT  SYKM.MOJISETTEIATAI
      INTO  SEIHINSUI_SLM_7L_SHNCD
       FROM S02M006T SYKM
      WHERE SYKM.APKIHONCD = '000'
        AND SYKM.SHOPCD = '000'
        AND SYKM.RCRDKBN = 'M' -- レコード区分
        AND SYKM.SETTEINO = '181' -- 設定番号
        AND SYKM.SETTEICD = '1';
    SELECT  SYKM.MOJISETTEIATAI
      INTO  SHN_RENTAL_DAIBUNRUI
       FROM S02M006T SYKM
      WHERE SYKM.APKIHONCD = '000'
        AND SYKM.SHOPCD = '000'
        AND SYKM.RCRDKBN = 'M' -- レコード区分
        AND SYKM.SETTEINO = '096' -- 設定番号
        AND SYKM.SETTEICD = '2';
    SELECT  SYKM.MOJISETTEIATAI
      INTO  SVR_RENTAL_DAIBUNRUI
       FROM S02M006T SYKM
      WHERE SYKM.APKIHONCD = '000'
        AND SYKM.SHOPCD = '000'
        AND SYKM.RCRDKBN = 'M' -- レコード区分
        AND SYKM.SETTEINO = '096' -- 設定番号
        AND SYKM.SETTEICD = '3';
    SELECT  SYKM.MOJISETTEIATAI
      INTO  SVR_KANREN_DAIBUNRUI
       FROM S02M006T SYKM
      WHERE SYKM.APKIHONCD = '000'
        AND SYKM.SHOPCD = '000'
        AND SYKM.RCRDKBN = 'M' -- レコード区分
        AND SYKM.SETTEINO = '096' -- 設定番号
        AND SYKM.SETTEICD = '4';
-- ■:1 Base)Fujiwara 2016-06-28 通信会社連携対応
     SELECT SYKM.MOJISETTEIATAI
       INTO CLB_SERVICE_DAIBUNRUI
       FROM S02M006T SYKM
      WHERE SYKM.APKIHONCD = '000'
        AND SYKM.SHOPCD = '000'
        AND SYKM.RCRDKBN = 'M' -- レコード区分
        AND SYKM.SETTEINO = '096' -- 設定番号
        AND SYKM.SETTEICD = '8';
     SELECT SYKM.MOJISETTEIATAI
       INTO CLB_SERVICE_TYUBUNRUI
       FROM S02M006T SYKM
      WHERE SYKM.APKIHONCD = '000'
        AND SYKM.SHOPCD = '000'
        AND SYKM.RCRDKBN = 'M' -- レコード区分
        AND SYKM.OYASETTEINO = '096'
        AND SYKM.OYASETTEICD = '8'
        AND SYKM.SETTEINO = '918' -- 設定番号
        AND SYKM.SETTEICD = '2';

    -- 売上明細テーブル検索
--    OPEN SELECT_UMST(SEIHINSUI_DAIBUNRUI, SEIHINSUI_REG_SHNCD, SEIHINSUI_SLM_SHNCD, SHN_RENTAL_DAIBUNRUI, SVR_RENTAL_DAIBUNRUI, SVR_KANREN_DAIBUNRUI);
    OPEN SELECT_UMST(SEIHINSUI_DAIBUNRUI, SEIHINSUI_REG_SHNCD, SEIHINSUI_SLM_SHNCD, SEIHINSUI_SLM_7L_SHNCD, SHN_RENTAL_DAIBUNRUI, SVR_RENTAL_DAIBUNRUI, SVR_KANREN_DAIBUNRUI, CLB_SERVICE_DAIBUNRUI, CLB_SERVICE_TYUBUNRUI);
-- ■:2 Base)Fujiwara 2016-06-28 通信会社連携対応
    LOOP
      FETCH SELECT_UMST
      INTO
-- ■:1 Base)Fujiwara 2016-06-28 通信会社連携対応
      vRecordS07T002T.KONKAISHUNODAIKOURIKIN, -- 今回収納代行売上金額
      vRecordS07T002T.KONKAISHUNODAIKOSHOHITAXKIN, -- 今回収納代行消費税金額
-- ■:2 Base)Fujiwara 2016-06-28 通信会社連携対応
      vRecordS07T002T.RGRAYURYO, -- レギュラー有料
      vRecordS07T002T.RGRAMURYO, -- レギュラー無料
      vRecordS07T002T.SRMYURYO, -- スリム有料
      vRecordS07T002T.SRMMURYO, -- スリム無料
      vRecordS07T002T.SONOTAYURYO, -- その他有料
      vRecordS07T002T.SONOTAMURYO, -- その他無料
      vRecordS07T002T.SEIHINMIZUURI, -- 製品水売上
      vRecordS07T002T.RTLRKN, -- レンタル料金
      vRecordS07T002T.SONOTAURIKIN, -- その他売上金額
      vRecordS07T002T.AQCRGRAKYNPNSURYO, -- アクアクララレギュラーキャンペーン数量
      vRecordS07T002T.AQCRGRASVSURYO, -- アクアクララレギュラーサービス数量
      vRecordS07T002T.AQCRGRAPUROMOSHONSURYO, -- アクアクララレギュラープロモーション数量
      vRecordS07T002T.AQCRGRASIINSURYO, -- アクアクララレギュラー試飲数量
      vRecordS07T002T.AQCRGRAMNTESURYO, -- アクアクララレギュラーメンテ数量
      vRecordS07T002T.AQCRGRAKOKANSURYO, -- アクアクララレギュラー交換数量
      vRecordS07T002T.AQCRGRAURISURYO, -- アクアクララレギュラー売上数量
      vRecordS07T002T.AQCSRMKYNPNSURYO, -- アクアクララスリムキャンペーン数量
      vRecordS07T002T.AQCSRMSVSURYO, -- アクアクララスリムサービス数量
      vRecordS07T002T.AQCSRMPUROMOSHONSURYO, -- アクアクララスリムプロモーション数量
      vRecordS07T002T.AQCSRMSIINSURYO, -- アクアクララスリム試飲数量
      vRecordS07T002T.AQCSRMMNTESURYO, -- アクアクララスリムメンテ数量
      vRecordS07T002T.AQCSRMKOKANSURYO, -- アクアクララスリム交換数量
      vRecordS07T002T.AQCSRMURISURYO, -- アクアクララスリム売上数量
      vRecordS07T002T.ETCSHNMZKYNPNSURYO, -- その他製品水キャンペーン数量
      vRecordS07T002T.ETCSHNMZSVSURYO, -- その他製品水サービス数量
      vRecordS07T002T.ETCSHNMZPUROMOSHONSURYO, -- その他製品水プロモーション数量
      vRecordS07T002T.ETCSHNMZSIINSURYO, -- その他製品水試飲数量
      vRecordS07T002T.ETCSHNMZMNTESURYO, -- その他製品水メンテ数量
      vRecordS07T002T.ETCSHNMZKOKANSURYO, -- その他製品水交換数量
      vRecordS07T002T.ETCSHNMZURISURYO, -- その他製品水売上数量
      vRecordS07T002T.SHNMZRGRAKIN, -- 製品水レギュラー金額
      vRecordS07T002T.SHNMZSRMKIN, -- 製品水スリム金額
      vRecordS07T002T.SHNMZSONOTAKIN, -- 製品水その他金額
      vRecordS07T002T.RNTLRWSVKIN, -- レンタル料サーバー金額
      vRecordS07T002T.RNTLRHKSHNKIN, -- レンタル料他商品金額
      vRecordS07T002T.SNTSHNWSVKIN, -- その他商品サーバー金額
      vRecordS07T002T.SNTSHNHKSHNKIN, -- その他商品他商品金額
      vRecordS07T002T.SNTSHNKNPHNKIN; -- その他商品キャンペーン品金額
      EXIT WHEN SELECT_UMST%NOTFOUND;
    END LOOP;
    CLOSE SELECT_UMST;

  END SelectUMST;
  /*****************************************************************************/
  /* ファンクション名 ：売上テーブル抽出                                       */
  /*****************************************************************************/
  PROCEDURE SelectURGT IS
    -- 売上テーブル検索
    CURSOR SELECT_URGT IS
-- ■:1 Isac)Shiba 2008-11-07
--      SELECT NVL(SUM(URIKIN), 0), -- 売上金額:伝票合計（税抜き）
      SELECT /*+ INDEX(URGT S06T001T_IX1) */ NVL(SUM(URIKIN), 0), -- 売上金額:伝票合計（税抜き）
-- ■:2 Isac)Shiba 2008-11-07
             NVL(SUM(SHOHITAXKIN), 0) -- 消費税金額
        FROM S06T001T URGT -- 売上テーブル
       WHERE URGT.APKIHONCD = vRecordS07T002T.APKIHONCD -- ＡＰ基本コード
         AND URGT.SHOPCD = vRecordS07T002T.SHOPCD -- ショップコード
         AND URGT.TOKUISAKICD = vRecordS07T002T.TOKUISAKICD -- 得意先コード
-- ■:1 Isac)Shiba 2008-02-21
         AND URGT.GETSUJISHIMEDYDM   = vRecordS07T002T.SHIMEDYDM
         AND URGT.GETSUJISHIMEDD     = vRecordS07T002T.SHIMEDD
-- ■:2 Isac)Shiba 2008-02-21
         AND URGT.GETSUJIPRCSJISSIDD = vRecordS07T002T.SHIMEPRCSJISSIDD -- 月次処理実施日
       GROUP BY URGT.APKIHONCD, URGT.SHOPCD, URGT.TOKUISAKICD -- 得意先コード
       ORDER BY URGT.APKIHONCD, URGT.SHOPCD, URGT.TOKUISAKICD; -- 得意先コード
  BEGIN
    vRecordS07T002T.KONKAITAXNUKIURIKIN := 0; -- 今回税抜売上金額
    vRecordS07T002T.KONKAISHOHITAXKIN   := 0; -- 今回消費税金額
    -- 売上テーブル検索
    OPEN SELECT_URGT;
    LOOP
      FETCH SELECT_URGT
        INTO vRecordS07T002T.KONKAITAXNUKIURIKIN, -- 今回税抜売上金額
      vRecordS07T002T.KONKAISHOHITAXKIN; -- 今回消費税金額
      EXIT WHEN SELECT_URGT%NOTFOUND;
    END LOOP;
    CLOSE SELECT_URGT;

  END SelectURGT;

  --ADD START 20190520 TJS)takio 軽減税率対応
  /*****************************************************************************/
  /* ファンクション名 ：売上テーブル抽出(8%)                                   */
  /*****************************************************************************/
  PROCEDURE Select8URGT IS
    -- 売上テーブル検索
    CURSOR SELECT_8URGT IS
      SELECT /*+ INDEX(URGT S06T001T_IX1) */
             NVL(SUM(URMT.KIN), 0), -- 金額
             NVL(SUM(URMT.SHOHITAXKIN), 0) -- 消費税金額
        FROM S06T001T URGT -- 売上テーブル
        JOIN S06T002T URMT -- 売上明細テーブル
          ON URGT.APKIHONCD = URMT.APKIHONCD
         AND URGT.SHOPCD = URMT.SHOPCD
         AND URGT.URIDNPYNO = URMT.URIDNPYNO
         AND URGT.EDANO = URMT.EDANO
       WHERE URGT.APKIHONCD = vRecordS07T002T.APKIHONCD -- ＡＰ基本コード
         AND URGT.SHOPCD = vRecordS07T002T.SHOPCD -- ショップコード
         AND URGT.TOKUISAKICD = vRecordS07T002T.TOKUISAKICD -- 得意先コード
         AND URGT.GETSUJISHIMEDYDM   = vRecordS07T002T.SHIMEDYDM
         AND URGT.GETSUJISHIMEDD     = vRecordS07T002T.SHIMEDD
         AND URGT.GETSUJIPRCSJISSIDD = vRecordS07T002T.SHIMEPRCSJISSIDD -- 月次処理実施日
         AND URMT.TANPINMEISAIFLG = '1'
         -- 消費税改修：軽減（税区分4/6/7）のうち、売上日時点の税率が月次締日時点の税区分4税率と一致する分（新枠）
         AND URMT.TAXKBN IN ('4','6','7')
         AND ( SELECT SHOHITAX FROM S02M091T TAX
                WHERE TAX.TAXKBN = URMT.TAXKBN
                  AND URGT.URIDD BETWEEN TAX.TEKIYOSTRTDD AND TAX.TEKIYOSHRYDD )
           = ( SELECT SHOHITAX FROM S02M091T TAX4
                WHERE TAX4.TAXKBN = '4'
                  AND vRecordS07T002T.KONKAISHIMEDD BETWEEN TAX4.TEKIYOSTRTDD AND TAX4.TEKIYOSHRYDD )
       GROUP BY URGT.APKIHONCD, URGT.SHOPCD, URGT.TOKUISAKICD -- 得意先コード
       ORDER BY URGT.APKIHONCD, URGT.SHOPCD, URGT.TOKUISAKICD; -- 得意先コード
  BEGIN
    vRecordS07T002T.N8PERZEINUKIURIKIN := 0; -- ８％税抜売上金額
    vRecordS07T002T.N8PERSHOHITAXKIN   := 0; -- ８％消費税金額
    -- 売上テーブル検索
    OPEN SELECT_8URGT;
    LOOP
      FETCH SELECT_8URGT
        INTO vRecordS07T002T.N8PERZEINUKIURIKIN, -- ８％税抜売上金額
             vRecordS07T002T.N8PERSHOHITAXKIN;   -- ８％消費税金額
      EXIT WHEN SELECT_8URGT%NOTFOUND;
    END LOOP;
    CLOSE SELECT_8URGT;

  END Select8URGT;
  /*****************************************************************************/
  /* ファンクション名 ：売上テーブル抽出(10%)                                  */
  /*****************************************************************************/
  PROCEDURE Select10URGT IS
    -- 売上テーブル検索
    CURSOR SELECT_10URGT IS
      SELECT /*+ INDEX(URGT S06T001T_IX1) */
             NVL(SUM(URMT.KIN), 0), -- 金額
             NVL(SUM(URMT.SHOHITAXKIN), 0) -- 消費税金額
        FROM S06T001T URGT -- 売上テーブル
        JOIN S06T002T URMT -- 売上明細テーブル
          ON URGT.APKIHONCD = URMT.APKIHONCD
         AND URGT.SHOPCD = URMT.SHOPCD
         AND URGT.URIDNPYNO = URMT.URIDNPYNO
         AND URGT.EDANO = URMT.EDANO
       WHERE URGT.APKIHONCD = vRecordS07T002T.APKIHONCD -- ＡＰ基本コード
         AND URGT.SHOPCD = vRecordS07T002T.SHOPCD -- ショップコード
         AND URGT.TOKUISAKICD = vRecordS07T002T.TOKUISAKICD -- 得意先コード
         AND URGT.GETSUJISHIMEDYDM   = vRecordS07T002T.SHIMEDYDM
         AND URGT.GETSUJISHIMEDD     = vRecordS07T002T.SHIMEDD
         AND URGT.GETSUJIPRCSJISSIDD = vRecordS07T002T.SHIMEPRCSJISSIDD -- 月次処理実施日
         AND URMT.TANPINMEISAIFLG = '1'
         AND 10 = ( SELECT SHOHITAX FROM S02M091T TAX
                    WHERE TAX.TAXKBN = URMT.TAXKBN
                     AND URGT.URIDD BETWEEN TAX.TEKIYOSTRTDD AND TAX.TEKIYOSHRYDD
                      AND URMT.TAXKBN IN ('1','4') )
       GROUP BY URGT.APKIHONCD, URGT.SHOPCD, URGT.TOKUISAKICD -- 得意先コード
       ORDER BY URGT.APKIHONCD, URGT.SHOPCD, URGT.TOKUISAKICD; -- 得意先コード
  BEGIN
    vRecordS07T002T.N10PERZEINUKIURIKIN := 0; -- １０％税抜売上金額
    vRecordS07T002T.N10PERSHOHITAXKIN   := 0; -- １０％消費税金額
    -- 売上テーブル検索
    OPEN SELECT_10URGT;
    LOOP
      FETCH SELECT_10URGT
        INTO vRecordS07T002T.N10PERZEINUKIURIKIN, -- １０％税抜売上金額
             vRecordS07T002T.N10PERSHOHITAXKIN;   -- １０％消費税金額
      EXIT WHEN SELECT_10URGT%NOTFOUND;
    END LOOP;
    CLOSE SELECT_10URGT;

  END Select10URGT;
  --ADD E N D 20190520 TJS)takio

  -- 消費税改修で追加
  /*****************************************************************************/
  /* ファンクション名 ：売上テーブル抽出(軽減旧枠)                              */
  /* 軽減（税区分4/6/7）のうち、売上日時点の税率が月次締日時点の税区分4税率と    */
  /* 異なる分を集計する。一般旧枠は当面0固定とする。                            */
  /*****************************************************************************/
  PROCEDURE SelectOldKeigenURGT IS
    -- 売上テーブル検索
    CURSOR SELECT_OLDKEIGENURGT IS
      SELECT /*+ INDEX(URGT S06T001T_IX1) */
             NVL(SUM(URMT.KIN), 0), -- 金額
             NVL(SUM(URMT.SHOHITAXKIN), 0) -- 消費税金額
        FROM S06T001T URGT -- 売上テーブル
        JOIN S06T002T URMT -- 売上明細テーブル
          ON URGT.APKIHONCD = URMT.APKIHONCD
         AND URGT.SHOPCD = URMT.SHOPCD
         AND URGT.URIDNPYNO = URMT.URIDNPYNO
         AND URGT.EDANO = URMT.EDANO
       WHERE URGT.APKIHONCD = vRecordS07T002T.APKIHONCD -- ＡＰ基本コード
         AND URGT.SHOPCD = vRecordS07T002T.SHOPCD -- ショップコード
         AND URGT.TOKUISAKICD = vRecordS07T002T.TOKUISAKICD -- 得意先コード
         AND URGT.GETSUJISHIMEDYDM   = vRecordS07T002T.SHIMEDYDM
         AND URGT.GETSUJISHIMEDD     = vRecordS07T002T.SHIMEDD
         AND URGT.GETSUJIPRCSJISSIDD = vRecordS07T002T.SHIMEPRCSJISSIDD -- 月次処理実施日
         AND URMT.TANPINMEISAIFLG = '1'
         AND URMT.TAXKBN IN ('4','6','7')
         AND ( SELECT SHOHITAX FROM S02M091T TAX
                WHERE TAX.TAXKBN = URMT.TAXKBN
                  AND URGT.URIDD BETWEEN TAX.TEKIYOSTRTDD AND TAX.TEKIYOSHRYDD )
          != ( SELECT SHOHITAX FROM S02M091T TAX4
                WHERE TAX4.TAXKBN = '4'
                  AND vRecordS07T002T.KONKAISHIMEDD BETWEEN TAX4.TEKIYOSTRTDD AND TAX4.TEKIYOSHRYDD )
       GROUP BY URGT.APKIHONCD, URGT.SHOPCD, URGT.TOKUISAKICD -- 得意先コード
       ORDER BY URGT.APKIHONCD, URGT.SHOPCD, URGT.TOKUISAKICD; -- 得意先コード
  BEGIN
    vRecordS07T002T.OLDKEIGENZEINUKIURIKIN := 0; -- 軽減旧枠税抜売上金額
    vRecordS07T002T.OLDKEIGENSHOHITAXKIN   := 0; -- 軽減旧枠消費税金額
    vRecordS07T002T.OLDIPPANZEINUKIURIKIN  := 0; -- 一般旧枠税抜売上金額（当面0固定）
    vRecordS07T002T.OLDIPPANSHOHITAXKIN    := 0; -- 一般旧枠消費税金額（当面0固定）
    -- 売上テーブル検索
    OPEN SELECT_OLDKEIGENURGT;
    LOOP
      FETCH SELECT_OLDKEIGENURGT
        INTO vRecordS07T002T.OLDKEIGENZEINUKIURIKIN, -- 軽減旧枠税抜売上金額
             vRecordS07T002T.OLDKEIGENSHOHITAXKIN;   -- 軽減旧枠消費税金額
      EXIT WHEN SELECT_OLDKEIGENURGT%NOTFOUND;
    END LOOP;
    CLOSE SELECT_OLDKEIGENURGT;

  END SelectOldKeigenURGT;

  /*****************************************************************************/
  /* ファンクション名 ：入金明細テーブル抽出                                   */
  /*****************************************************************************/
  PROCEDURE SelectNMST IS
    -- 入金明細テーブル検索
    CURSOR SELECT_NMST IS
-- ■:1 Isac)Shiba 2008-11-07
--      SELECT NVL(SUM(NMST.NYKNKIN), 0) + -- 入金金額
      SELECT /*+ INDEX(NMST S10T001T_IX2) */ NVL(SUM(NMST.NYKNKIN), 0) + -- 入金金額
-- ■:2 Isac)Shiba 2008-11-07
             NVL(SUM(NMST.NEBIKIKIN), 0) + -- 値引金額
             NVL(SUM(NMST.TESURYO), 0) -- 手数料
        FROM S10T001T NMST -- 入金明細テーブル
       WHERE NMST.APKIHONCD = vRecordS07T002T.APKIHONCD -- ＡＰ基本コード
         AND NMST.SHOPCD = vRecordS07T002T.SHOPCD -- ショップコード
         AND NMST.TOKUISAKICD = vRecordS07T002T.TOKUISAKICD -- 得意先コード
-- ■:1 Isac)Shiba 2008-02-21
         AND NMST.GETSUJISHIMEDYDM   = vRecordS07T002T.SHIMEDYDM
         AND NMST.GETSUJISHIMEDD     = vRecordS07T002T.SHIMEDD
-- ■:2 Isac)Shiba 2008-02-21
         AND NMST.GETSUJIPRCSJISSIDD = vRecordS07T002T.SHIMEPRCSJISSIDD -- 月次処理実施日
       GROUP BY NMST.APKIHONCD, NMST.SHOPCD, NMST.TOKUISAKICD -- 得意先コード
       ORDER BY NMST.APKIHONCD, NMST.SHOPCD, NMST.TOKUISAKICD; -- 得意先コード
  BEGIN
    vRecordS07T002T.KONKAINYKNKIN := 0;
    -- 売上テーブル検索
    OPEN SELECT_NMST;
    LOOP
      FETCH SELECT_NMST
        INTO vRecordS07T002T.KONKAINYKNKIN; -- 今回入金金額
      EXIT WHEN SELECT_NMST%NOTFOUND;
    END LOOP;
    CLOSE SELECT_NMST;
  END SelectNMST;
  /*****************************************************************************/
  /* プロシージャ名   ：売掛残高テーブル登録                                   */
  /*****************************************************************************/
  PROCEDURE InsertUZDT IS
  BEGIN
    INSERT INTO S07T002T UZDT -- 売掛残高テーブル登録
      (APKIHONCD, -- ＡＰ基本コード
       SHOPCD, -- ショップコード
       TOKUISAKICD, -- 得意先コード
       SHIMEDYDM, -- 締年月
       SHIMEDD, -- 締日
       SHIMEPRCSJISSIDD, -- 締処理実施日
       ZNKSHIMEDD, -- 前回締日
       KONKAISHIMEDD, -- 今回締日
       ZNKKURIKOSHIKIN, -- 前回繰越金額
       KONKAITAXNUKIURIKIN, -- 今回税抜売上金額
       KONKAISHOHITAXKIN, -- 今回消費税金額
       KONKAINYKNKIN, -- 今回入金金額
-- ■:1 Base)Fujiwara 2016-06-28 通信会社連携対応
       KONKAISHUNODAIKOURIKIN, -- 今回収納代行売上金額
       KONKAISHUNODAIKOSHOHITAXKIN, -- 今回収納代行消費税金額
-- ■:2 Base)Fujiwara 2016-06-28 通信会社連携対応
       --ADD START 20190520 TJS)takio 軽減税率対応
       N8PERZEINUKIURIKIN,  -- ８％税抜売上金額
       N8PERSHOHITAXKIN,    -- ８％消費税金額
       N10PERZEINUKIURIKIN, -- １０％税抜売上金額
       N10PERSHOHITAXKIN,   -- １０％消費税金額
       -- 消費税改修で追加
       OLDKEIGENZEINUKIURIKIN, -- 軽減旧枠税抜売上金額
       OLDKEIGENSHOHITAXKIN,   -- 軽減旧枠消費税金額
       OLDIPPANZEINUKIURIKIN,  -- 一般旧枠税抜売上金額
       OLDIPPANSHOHITAXKIN,    -- 一般旧枠消費税金額
       --ADD E N D 20190520 TJS)takio

       KURIKOSHIKIN, -- 繰越金額

       RGRAYURYO, -- レギュラー有料
       RGRAMURYO, -- レギュラー無料
       SRMYURYO, -- スリム有料
       SRMMURYO, -- スリム無料
       SONOTAYURYO, -- その他有料
       SONOTAMURYO, -- その他無料
       SEIHINMIZUURI, -- 製品水売上
       RTLRKN, -- レンタル料金
       SONOTAURIKIN, -- その他売上金額
       AQCRGRAKYNPNSURYO, -- アクアクララレギュラーキャンペーン数量
       AQCRGRASVSURYO, -- アクアクララレギュラーサービス数量
       AQCRGRAPUROMOSHONSURYO, -- アクアクララレギュラープロモーション数量
       AQCRGRASIINSURYO, -- アクアクララレギュラー試飲数量
       AQCRGRAMNTESURYO, -- アクアクララレギュラーメンテ数量
       AQCRGRAKOKANSURYO, -- アクアクララレギュラー交換数量
       AQCRGRAURISURYO, -- アクアクララレギュラー売上数量
       AQCSRMKYNPNSURYO, -- アクアクララスリムキャンペーン数量
       AQCSRMSVSURYO, -- アクアクララスリムサービス数量
       AQCSRMPUROMOSHONSURYO, -- アクアクララスリムプロモーション数量
       AQCSRMSIINSURYO, -- アクアクララスリム試飲数量
       AQCSRMMNTESURYO, -- アクアクララスリムメンテ数量
       AQCSRMKOKANSURYO, -- アクアクララスリム交換数量
       AQCSRMURISURYO, -- アクアクララスリム売上数量
       ETCSHNMZKYNPNSURYO, -- その他製品水キャンペーン数量
       ETCSHNMZSVSURYO, -- その他製品水サービス数量
       ETCSHNMZPUROMOSHONSURYO, -- その他製品水プロモーション数量
       ETCSHNMZSIINSURYO, -- その他製品水試飲数量
       ETCSHNMZMNTESURYO, -- その他製品水メンテ数量
       ETCSHNMZKOKANSURYO, -- その他製品水交換数量
       ETCSHNMZURISURYO, -- その他製品水売上数量
       SHNMZRGRAKIN, -- 製品水レギュラー金額
       SHNMZSRMKIN, -- 製品水スリム金額
       SHNMZSONOTAKIN, -- 製品水その他金額
       RNTLRWSVKIN, -- レンタル料サーバー金額
       RNTLRHKSHNKIN, -- レンタル料他商品金額
       SNTSHNWSVKIN, -- その他商品サーバー金額
       SNTSHNHKSHNKIN, -- その他商品他商品金額
       SNTSHNKNPHNKIN, -- その他商品キャンペーン品金額

       YUKOKBN, -- 有効区分
       TOSHAAPKIHONCD, -- 登録者ＡＰ基本コード
       TOSHASHOPCD, -- 登録者ショップコード
       TOSHACD, -- 登録者コード
       INPTSYSTMYMD, -- 登録システム日付
       INPTSYSTMJIKAN, -- 登録システム時間
       INPTUNYOYMD, -- 登録運用日付
       INPTIPADRSE, -- 登録ＩＰアドレス
       INPTPGMID, -- 登録プログラムＩＤ
       KOSHAAPKIHONCD, -- 更新者ＡＰ基本コード
       KOSHASHOPCD, -- 更新者ショップコード
       KOSHACD, -- 更新者コード
       KSHNSYSTMYMD, -- 更新システム日付
       KSHNSYSTMJIKAN, -- 更新システム時間
       KSHNUNYOYMD, -- 更新運用日付
       KSHNIPADRSE, -- 更新ＩＰアドレス
       KSHNPGMID) -- 更新プログラムＩＤ
    VALUES
      (vRecordS07T002T.APKIHONCD, -- ＡＰ基本コード
       vRecordS07T002T.SHOPCD, -- ショップコード
       vRecordS07T002T.TOKUISAKICD, -- 得意先コード
       vRecordS07T002T.SHIMEDYDM, -- 締年月
       vRecordS07T002T.SHIMEDD, -- 締日
       vRecordS07T002T.SHIMEPRCSJISSIDD, -- 締処理実施日
       vRecordS07T002T.ZNKSHIMEDD, -- 前回締日
       vRecordS07T002T.KONKAISHIMEDD, -- 今回締日
       vRecordS07T002T.ZNKKURIKOSHIKIN, -- 前回繰越金額
       vRecordS07T002T.KONKAITAXNUKIURIKIN, -- 今回税抜売上金額
       vRecordS07T002T.KONKAISHOHITAXKIN, -- 今回消費税金額
       vRecordS07T002T.KONKAINYKNKIN, -- 今回入金金額
-- ■:1 Base)Fujiwara 2016-06-28 通信会社連携対応
       vRecordS07T002T.KONKAISHUNODAIKOURIKIN, -- 今回収納代行売上金額
       vRecordS07T002T.KONKAISHUNODAIKOSHOHITAXKIN, -- 今回収納代行消費税金額
-- ■:2 Base)Fujiwara 2016-06-28 通信会社連携対応
       --ADD START 20190520 TJS)takio 軽減税率対応
       vRecordS07T002T.N8PERZEINUKIURIKIN,  -- ８％税抜売上金額
       vRecordS07T002T.N8PERSHOHITAXKIN,    -- ８％消費税金額
       vRecordS07T002T.N10PERZEINUKIURIKIN, -- １０％税抜売上金額
       vRecordS07T002T.N10PERSHOHITAXKIN,   -- １０％消費税金額
       vRecordS07T002T.OLDKEIGENZEINUKIURIKIN, -- 軽減旧枠税抜売上金額
       vRecordS07T002T.OLDKEIGENSHOHITAXKIN,   -- 軽減旧枠消費税金額
       vRecordS07T002T.OLDIPPANZEINUKIURIKIN,  -- 一般旧枠税抜売上金額
       vRecordS07T002T.OLDIPPANSHOHITAXKIN,    -- 一般旧枠消費税金額
       --ADD E N D 20190520 TJS)takio
       vRecordS07T002T.KURIKOSHIKIN, -- 繰越金額

       vRecordS07T002T.RGRAYURYO, -- レギュラー有料
       vRecordS07T002T.RGRAMURYO, -- レギュラー無料
       vRecordS07T002T.SRMYURYO, -- スリム有料
       vRecordS07T002T.SRMMURYO, -- スリム無料
       vRecordS07T002T.SONOTAYURYO, -- その他有料
       vRecordS07T002T.SONOTAMURYO, -- その他無料
       vRecordS07T002T.SEIHINMIZUURI, -- 製品水売上
       vRecordS07T002T.RTLRKN, -- レンタル料金
       vRecordS07T002T.SONOTAURIKIN, -- その他売上金額
       vRecordS07T002T.AQCRGRAKYNPNSURYO, -- アクアクララレギュラーキャンペーン数量
       vRecordS07T002T.AQCRGRASVSURYO, -- アクアクララレギュラーサービス数量
       vRecordS07T002T.AQCRGRAPUROMOSHONSURYO, -- アクアクララレギュラープロモーション数量
       vRecordS07T002T.AQCRGRASIINSURYO, -- アクアクララレギュラー試飲数量
       vRecordS07T002T.AQCRGRAMNTESURYO, -- アクアクララレギュラーメンテ数量
       vRecordS07T002T.AQCRGRAKOKANSURYO, -- アクアクララレギュラー交換数量
       vRecordS07T002T.AQCRGRAURISURYO, -- アクアクララレギュラー売上数量
       vRecordS07T002T.AQCSRMKYNPNSURYO, -- アクアクララスリムキャンペーン数量
       vRecordS07T002T.AQCSRMSVSURYO, -- アクアクララスリムサービス数量
       vRecordS07T002T.AQCSRMPUROMOSHONSURYO, -- アクアクララスリムプロモーション数量
       vRecordS07T002T.AQCSRMSIINSURYO, -- アクアクララスリム試飲数量
       vRecordS07T002T.AQCSRMMNTESURYO, -- アクアクララスリムメンテ数量
       vRecordS07T002T.AQCSRMKOKANSURYO, -- アクアクララスリム交換数量
       vRecordS07T002T.AQCSRMURISURYO, -- アクアクララスリム売上数量
       vRecordS07T002T.ETCSHNMZKYNPNSURYO, -- その他製品水キャンペーン数量
       vRecordS07T002T.ETCSHNMZSVSURYO, -- その他製品水サービス数量
       vRecordS07T002T.ETCSHNMZPUROMOSHONSURYO, -- その他製品水プロモーション数量
       vRecordS07T002T.ETCSHNMZSIINSURYO, -- その他製品水試飲数量
       vRecordS07T002T.ETCSHNMZMNTESURYO, -- その他製品水メンテ数量
       vRecordS07T002T.ETCSHNMZKOKANSURYO, -- その他製品水交換数量
       vRecordS07T002T.ETCSHNMZURISURYO, -- その他製品水売上数量
       vRecordS07T002T.SHNMZRGRAKIN, -- 製品水レギュラー金額
       vRecordS07T002T.SHNMZSRMKIN, -- 製品水スリム金額
       vRecordS07T002T.SHNMZSONOTAKIN, -- 製品水その他金額
       vRecordS07T002T.RNTLRWSVKIN, -- レンタル料サーバー金額
       vRecordS07T002T.RNTLRHKSHNKIN, -- レンタル料他商品金額
       vRecordS07T002T.SNTSHNWSVKIN, -- その他商品サーバー金額
       vRecordS07T002T.SNTSHNHKSHNKIN, -- その他商品他商品金額
       vRecordS07T002T.SNTSHNKNPHNKIN, -- その他商品キャンペーン品金額

       vRecordS07T002T.YUKOKBN, -- 有効区分
       vRecordS07T002T.TOSHAAPKIHONCD, -- 登録者ＡＰ基本コード
       vRecordS07T002T.TOSHASHOPCD, -- 登録者ショップコード
       vRecordS07T002T.TOSHACD, -- 登録者コード
       vRecordS07T002T.INPTSYSTMYMD, -- 登録システム日付
       vRecordS07T002T.INPTSYSTMJIKAN, -- 登録システム時間
       vRecordS07T002T.INPTUNYOYMD, -- 登録運用日付
       vRecordS07T002T.INPTIPADRSE, -- 登録ＩＰアドレス
       vRecordS07T002T.INPTPGMID, -- 登録プログラムＩＤ
       vRecordS07T002T.KOSHAAPKIHONCD, -- 更新者ＡＰ基本コード
       vRecordS07T002T.KOSHASHOPCD, -- 更新者ショップコード
       vRecordS07T002T.KOSHACD, -- 更新者コード
       vRecordS07T002T.KSHNSYSTMYMD, -- 更新システム日付
       vRecordS07T002T.KSHNSYSTMJIKAN, -- 更新システム時間
       vRecordS07T002T.KSHNUNYOYMD, -- 更新運用日付
       vRecordS07T002T.KSHNIPADRSE, -- 更新ＩＰアドレス
       vRecordS07T002T.KSHNPGMID); -- 更新プログラムＩＤ
    nCntInsS07T002T    :=    nCntInsS07T002T    +   SQL%ROWCOUNT;
  END InsertUZDT;

  /*****************************************************************************/
  /* ファンクション名 ：ショップマスタ更新                                     */
  /* 引数             ：iGetsujishimedd    月次締日                            */
  /*****************************************************************************/
  PROCEDURE UpdateSHPM(iGetsujishimedd IN VARCHAR2) IS
  BEGIN
    -- ショップマスタ更新
    UPDATE S02M046T SHPM
       SET SHPM.KESSANKI = (CASE WHEN TO_CHAR(ADD_MONTHS(TO_DATE(SHPM.KESSANDM, 'MM'), 1), 'MM') = -- 決算月＋１ヶ月
            TO_CHAR(TO_DATE(iGetsujishimedd, 'YYYYMMDD'), 'MM') -- 月次締日(の月)
            AND SHPM.KESSANKIKSHNSHIMEDD < iGetsujishimedd -- 決算期更新締日
            THEN LPad(TO_CHAR(NVL(SHPM.KESSANKI, 0) + 1), 2, '0') -- 決算期 = 決算期 + 1
            ELSE SHPM.KESSANKI END),
           SHPM.KESSANKIKSHNSHIMEDD = (CASE WHEN TO_CHAR(ADD_MONTHS(TO_DATE(SHPM.KESSANDM, 'MM'), 1), 'MM') = -- 決算月＋１ヶ月
            TO_CHAR(TO_DATE(iGetsujishimedd, 'YYYYMMDD'), 'MM') -- 月次締日(の月)
            AND SHPM.KESSANKIKSHNSHIMEDD < iGetsujishimedd -- 決算期更新締日
            THEN iGetsujishimedd -- 決算期更新締日
            ELSE SHPM.KESSANKIKSHNSHIMEDD END),
-- ■:1 Base)Arai 2010-05-21 月次指示を行った担当者で更新しないように対応
--           SHPM.URIGETSUJIDYDMDD = iGetsujishimedd, -- 売上月次年月日
           SHPM.URIGETSUJIDYDMDD = iGetsujishimedd -- 売上月次年月日
--           SHPM.KOSHAAPKIHONCD   = vRecordS07T002T.KOSHAAPKIHONCD, -- 更新者ＡＰ基本コード
--           SHPM.KOSHASHOPCD      = vRecordS07T002T.KOSHASHOPCD, -- 更新者ショップコード
--           SHPM.KOSHACD          = vRecordS07T002T.KOSHACD, -- 更新者コード
--           SHPM.KSHNSYSTMYMD     = vRecordS07T002T.KSHNSYSTMYMD, -- 更新システム日付: システム日付
--           SHPM.KSHNSYSTMJIKAN   = vRecordS07T002T.KSHNSYSTMJIKAN, -- 更新システム時間: システム時間
--           SHPM.KSHNUNYOYMD      = vRecordS07T002T.KSHNUNYOYMD, -- 更新運用日付: システム管理マスタ.文字設定値
--           SHPM.KSHNIPADRSE      = vRecordS07T002T.KSHNIPADRSE, -- 更新ＩＰアドレス: パラメータ．更新ＩＰアドレス
--           SHPM.KSHNPGMID        = vRecordS07T002T.KSHNPGMID -- 更新プログラムＩＤ
-- ■:2 Base)Arai 2010-05-21
     WHERE SHPM.APKIHONCD = vRecordS07T002T.APKIHONCD -- ＡＰ基本コード
       AND SHPM.SHOPCD = vRecordS07T002T.SHOPCD -- ショップコード
       AND vRecordS07T002T.YUKOKBN = '1'; -- 有効区分
    nCntUpdS02M046T    :=    nCntUpdS02M046T    +   SQL%ROWCOUNT;
  END UpdateSHPM;
-- ■:1 Base)Arai 2012-11-08
  /*****************************************************************************/
  /* ファンクション名 ：T伝票の更新前ログを出力                                */
  /* 引数             ：iApKihoncd         AP基本コード                        */
  /*                  ：iShopcd            ショップコード                      */
  /*                  ：iUserApKihoncd     担当者ＡＰ基本コード                */
  /*                  ：iUserShopcd        担当者ショップコード                */
  /*                  ：iTnshacd           担当者コード                        */
  /*                  ：iIpadrse           ＩＰアドレス                        */
  /*                  ：iGetsujishimedd    月次締日                            */
  /*****************************************************************************/
  PROCEDURE TLogURGT(iApKihoncd      IN VARCHAR2,
                     iShopcd         IN VARCHAR2,
                     iUserApKihoncd  IN VARCHAR2,
                     iUserShopcd     IN VARCHAR2,
                     iTnshacd        IN VARCHAR2,
                     iIpadrse        IN VARCHAR2,
                     iGetsujishimedd IN VARCHAR2) IS

    DYDM    VARCHAR2(6);
    DD      VARCHAR2(2);
    wMsg    VARCHAR2(512);

    CURSOR SELECT_TURGT IS
      SELECT URGT.APKIHONCD || ':' || URGT.SHOPCD || ':' ||  URGT.URIDNPYNO || ':' || URGT.SKYSHIMEDYDM|| ':' || URGT.SKYSHIMEDD || ':' || URGT.URISHIMEPRCSDD || ':' || URGT.KSHNPGMID MSG
      FROM S06T001T URGT -- 売上テーブル
       WHERE URGT.APKIHONCD = iApKihoncd -- ＡＰ基本コード
         AND URGT.SHOPCD = iShopcd -- ショップコード
         AND URGT.DATSKSMOTOKBN = '06' -- T伝票
         AND (  (URGT.GETSUJISHIMEDYDM = DYDM AND URGT.GETSUJISHIMEDD = DD) -- 月次締日
             OR (URGT.URIDD <= iGetsujishimedd AND URGT.GETSUJIPRCSJISSIDD = '00000000' AND EXISTS (
                  SELECT 1 FROM S02M013T TKSM WHERE TKSM.APKIHONCD = URGT.APKIHONCD AND TKSM.SHOPCD = URGT.SHOPCD AND TKSM.TOKUISAKICD = URGT.TOKUISAKICD AND TKSM.YUKOKBN = '1'
                )))
         AND EXISTS (
           SELECT 1 FROM S02M017T SKSM WHERE SKSM.APKIHONCD = URGT.APKIHONCD AND SKSM.SHOPCD = URGT.SHOPCD AND SKSM.SKYSAKICD = URGT.SKYSAKICD AND SKSM.KAISHUHOHOKBN = '7'
         );

  BEGIN
    DYDM    :=    TO_CHAR(TO_DATE(iGetsujishimedd, 'YYYYMMDD'), 'YYYYMM');
    DD      :=    TO_CHAR(TO_DATE(iGetsujishimedd, 'YYYYMMDD'), 'DD');

    OPEN SELECT_TURGT;
    LOOP
      FETCH SELECT_TURGT
        INTO wMsg;
      EXIT WHEN SELECT_TURGT%NOTFOUND;

      S99_CHECK_LOG('D1001B01', iApKihoncd, iShopcd, '2', 'U', '1', 'S06T001T', 1, iUserApKihoncd, iUserShopcd, iTnshacd, iIpadrse, 'D1001B01', wmsg);

    END LOOP;
    CLOSE SELECT_TURGT;


  END TLogURGT;
  /******************************************************************************/
  /* プロシージャ名 ：売上月次締処理                                            */
  /* 引数           ：iApKihoncd         AP基本コード                           */
  /*                ：iShopcd            ショップコード                         */
  /*                ：iUserApKihoncd     担当者ＡＰ基本コード                   */
  /*                ：iUserShopcd        担当者ショップコード                   */
  /*                ：iTnshacd           担当者コード                           */
  /*                ：iIpadrse           ＩＰアドレス                           */
  /*                ：iGetsujishimedd    月次締日                               */
  /******************************************************************************/
  PROCEDURE UriageGetsuji(iApKihoncd      IN VARCHAR2,
                          iShopcd         IN VARCHAR2,
                          iUserApKihoncd  IN VARCHAR2,
                          iUserShopcd     IN VARCHAR2,
                          iTnshacd        IN VARCHAR2,
                          iIpadrse        IN VARCHAR2,
                          iGetsujishimedd IN VARCHAR2) IS
  -- 定数宣言
    CST_PGMID     VARCHAR2(8) := 'D1001B01';       -- プログラムID

    iMaxDate VARCHAR2(8); -- MAX前回締日
    -- 得意先コード検索
    CURSOR SELECT_TKSM IS
      SELECT TKSM.TOKUISAKICD -- 得意先コード
        FROM S02M013T TKSM -- 得意先マスタ
       WHERE TKSM.APKIHONCD = vRecordS07T002T.APKIHONCD
         AND TKSM.SHOPCD = vRecordS07T002T.SHOPCD
         AND TKSM.YUKOKBN = '1'; -- 有効区分;
  BEGIN

    -- 処理開始ログ出力
    S0199Q01(CST_PGMID, iApKihoncd, iShopcd, '1', 'S', '0', NULL, 0, iApKihoncd, iShopcd, iTnshacd, iIpadrse, CST_PGMID, NULL);

    -- 入力パラメータチェック
    IF iApKihoncd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20001,
                              'パラメータ.AP基本コード が設定されていません。');
    END IF;
    IF iShopcd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20002,
                              'パラメータ.ショップコード  が設定されていません。');
    END IF;
    IF iTnshacd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20003,
                              'パラメータ.担当者コード が設定されていません。');
    END IF;
    IF iIpadrse IS NULL THEN
      RAISE_APPLICATION_ERROR(-20004,
                              'パラメータ.ＩＰアドレス が設定されていません。');
    END IF;

    IF iUserApKihoncd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20005,
                              'パラメータ.担当者ＡＰ基本コード が設定されていません。');
    END IF;
    IF iUserShopcd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20006,
                              'パラメータ.担当者ショップコード  が設定されていません。');
    END IF;
    IF iGetsujishimedd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20007,
                              'パラメータ.月次締日  が設定されていません。');
    END IF;
    -- 基本情報設定
    SetUserInfo(iApKihoncd,
                iShopcd,
                iTnshacd,
                iIpadrse,
                iUserApKihoncd,
                iUserShopcd,
                iGetsujishimedd);
    -- MAX前回締日の検索
    SELECT NVL(MAX(UZDT.SHIMEDYDM || UZDT.SHIMEDD), 0)
      INTO iMaxDate
      FROM S07T002T UZDT -- 売掛残高テーブル
     WHERE UZDT.APKIHONCD = vRecordS07T002T.APKIHONCD -- ＡＰ基本コード
       AND UZDT.SHOPCD = vRecordS07T002T.SHOPCD; -- ショップコード
    -- 前回締日(今回締日の最大値)＞今回締日(パラメタの月次締日)の得意先の場合、以下の処理は実施しない(終了する)
    IF iMaxDate <= iGetsujishimedd THEN
-- ■:1 Base)Arai 2012-11-08
      -- T伝票の更新前ログを出力
      TLogURGT(iApKihoncd,
               iShopcd,
               iUserApKihoncd,
               iUserShopcd,
               iTnshacd,
               iIpadrse,
               iGetsujishimedd);
-- ■:2 Base)Arai 2012-11-08
      vRecordS07T002T.ZNKSHIMEDD := iMaxDate; -- 前回締日
      -- 売掛残高テーブル削除
      DeleteUZDT(iGetsujishimedd);
      -- 売上テーブル項目のクリア
      ClearURGT(iGetsujishimedd);
      -- 入金明細テーブル項目のクリア
      ClearNMST(iGetsujishimedd);
      -- 月次自動値引発生処理（売上締解除）
      D1505B01.JIDOUNEBIKI(iApKihoncd,
               iShopcd,
               iUserApKihoncd,
               iUserShopcd,
               iTnshacd,
               iIpadrse,
               TO_CHAR(SYSDATE, 'YYYYMMDD'),
               TO_CHAR(SYSDATE, 'HH24MISS'),
               iGetsujishimedd,
               '4');

-- ■:1 Isac)Shiba 2008-11-07
--      -- 売上テーブル更新
--      UpdateURGT(iGetsujishimedd);
--      -- 入金明細テーブル更新
--      UpdateNMST(iGetsujishimedd);
-- ■:2 Isac)Shiba 2008-11-07

      -- 月次自動値引発生処理（売上締処理）
      D1505B01.JIDOUNEBIKI(iApKihoncd,
               iShopcd,
               iUserApKihoncd,
               iUserShopcd,
               iTnshacd,
               iIpadrse,
               TO_CHAR(SYSDATE, 'YYYYMMDD'),
               TO_CHAR(SYSDATE, 'HH24MISS'),
               iGetsujishimedd,
               '3');

      -- 得意先コード検索
      OPEN SELECT_TKSM;
      LOOP
        FETCH SELECT_TKSM
          INTO vRecordS07T002T.TOKUISAKICD; -- 得意先コード
        EXIT WHEN SELECT_TKSM%NOTFOUND;
        nCntFetS02M013T    :=    nCntFetS02M013T    +   1;
-- ■:1 Isac)Shiba 2008-11-07
        -- 売上テーブル更新
        UpdateURGT(iGetsujishimedd);
        -- 入金明細テーブル更新
        UpdateNMST(iGetsujishimedd);
-- ■:2 Isac)Shiba 2008-11-07
        -- 売掛残高テーブルの前回情報取得
        GetZhkdate;
        -- 売上テーブル抽出
        SelectURGT;
        --ADD START 20190520 TJS)takio 軽減税率対応
        -- 消費税集計額抽出
        Select8URGT;
        Select10URGT;
        SelectOldKeigenURGT;
        --ADD E N D 20190520 TJS)takio
        -- 売上明細テーブル抽出
        SelectUMST;
        -- 入金明細テーブル抽出
        SelectNMST;
        -- 繰越金額 = 前回繰越金額+今回税抜売上金額+今回消費税金額-今回入金金額
        vRecordS07T002T.KURIKOSHIKIN := vRecordS07T002T.ZNKKURIKOSHIKIN +
                                        vRecordS07T002T.KONKAITAXNUKIURIKIN +
                                        vRecordS07T002T.KONKAISHOHITAXKIN -
                                        vRecordS07T002T.KONKAINYKNKIN;

        -- 売掛残高テーブル登録
        InsertUZDT;
      END LOOP;

      -- ショップマスタ更新
      UpdateSHPM(iGetsujishimedd);

      CLOSE SELECT_TKSM;
    END IF;

    -- 処理終了ログ出力
    S0199Q01(CST_PGMID, iApKihoncd, iShopcd, '1', 'E', '1', NULL, 0, iApKihoncd, iShopcd, iTnshacd, iIpadrse, CST_PGMID, NULL);
    S0199Q01(CST_PGMID, iApKihoncd, iShopcd, '1', 'R', '1', 'S02M013T', nCntFetS02M013T, iApKihoncd, iShopcd, iTnshacd, iIpadrse, CST_PGMID, NULL);
    S0199Q01(CST_PGMID, iApKihoncd, iShopcd, '1', 'D', '1', 'S07T002T', nCntDelS07T002T, iApKihoncd, iShopcd, iTnshacd, iIpadrse, CST_PGMID, NULL);
    S0199Q01(CST_PGMID, iApKihoncd, iShopcd, '1', 'U', '1', 'S06T001T', nCntUpdS06T001T, iApKihoncd, iShopcd, iTnshacd, iIpadrse, CST_PGMID, NULL);
    S0199Q01(CST_PGMID, iApKihoncd, iShopcd, '1', 'U', '1', 'S10T001T', nCntUpdS10T001T, iApKihoncd, iShopcd, iTnshacd, iIpadrse, CST_PGMID, NULL);
    S0199Q01(CST_PGMID, iApKihoncd, iShopcd, '1', 'I', '1', 'S07T002T', nCntInsS07T002T, iApKihoncd, iShopcd, iTnshacd, iIpadrse, CST_PGMID, NULL);
    S0199Q01(CST_PGMID, iApKihoncd, iShopcd, '1', 'U', '1', 'S02M046T', nCntUpdS02M046T, iApKihoncd, iShopcd, iTnshacd, iIpadrse, CST_PGMID, NULL);

EXCEPTION
  WHEN OTHERS THEN
    -- 異常終了ログ出力
    S0199Q01(CST_PGMID, iApKihoncd, iShopcd, '1', 'E', '9', NULL, 0, iApKihoncd, iShopcd, iTnshacd, iIpadrse, CST_PGMID, SUBSTRB(SQLERRM, 1, 512));
    S0199Q01(CST_PGMID, iApKihoncd, iShopcd, '1', 'R', '9', 'S02M013T', nCntFetS02M013T, iApKihoncd, iShopcd, iTnshacd, iIpadrse, CST_PGMID, NULL);
    S0199Q01(CST_PGMID, iApKihoncd, iShopcd, '1', 'D', '9', 'S07T002T', nCntDelS07T002T, iApKihoncd, iShopcd, iTnshacd, iIpadrse, CST_PGMID, NULL);
    S0199Q01(CST_PGMID, iApKihoncd, iShopcd, '1', 'U', '9', 'S06T001T', nCntUpdS06T001T, iApKihoncd, iShopcd, iTnshacd, iIpadrse, CST_PGMID, NULL);
    S0199Q01(CST_PGMID, iApKihoncd, iShopcd, '1', 'U', '9', 'S10T001T', nCntUpdS10T001T, iApKihoncd, iShopcd, iTnshacd, iIpadrse, CST_PGMID, NULL);
    S0199Q01(CST_PGMID, iApKihoncd, iShopcd, '1', 'I', '9', 'S07T002T', nCntInsS07T002T, iApKihoncd, iShopcd, iTnshacd, iIpadrse, CST_PGMID, NULL);
    S0199Q01(CST_PGMID, iApKihoncd, iShopcd, '1', 'U', '9', 'S02M046T', nCntUpdS02M046T, iApKihoncd, iShopcd, iTnshacd, iIpadrse, CST_PGMID, NULL);
    -- 元トランザクションをロールバック
--    ROLLBACK;
    -- 例外伝播
    RAISE;

  END UriageGetsuji;

END D1001B01;
/
EXIT;