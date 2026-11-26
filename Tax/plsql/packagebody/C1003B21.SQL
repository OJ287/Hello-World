CREATE OR REPLACE PACKAGE BODY "C1003B21"
/*******************************************************************************
 * システム名       ：ＡＰ総合管理システム
 * 機能名           ：売上情報取込
 *
 * All Rights Reserved, Copyright (c) FUJITSU SYSTEM SOLUTIONS LIMITED 2007.
 *******************************************************************************
   Date            Author        Changes Key                     Version
   -----------------------------------------------------------------------------
   2016-08-23 Base)Chokenkun     Create                          1.0.0
   2017-10-16 FJ)Nishikawa       次期ACCIS項目追加対応           1.0.1
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
    -- システム管理マスタから運用日付の検索
    CURSOR SELECT_OPERATIONDATE IS
      SELECT SYKM.MOJISETTEIATAI
      FROM S02M006T SYKM
      WHERE SYKM.APKIHONCD = iApKihoncd
        AND SYKM.SHOPCD = iShopcd
        AND SYKM.SETTEINO = '038'
        AND SYKM.RCRDKBN = 'M'
        AND SYKM.YUKOKBN = '1';
  BEGIN
    -- システム管理マスタから運用日付の取得
    OPEN SELECT_OPERATIONDATE;
    LOOP
      FETCH SELECT_OPERATIONDATE
       INTO returnOperationdate;

      -- 運用日付チェック
      IF SELECT_OPERATIONDATE%ROWCOUNT = 0 THEN RAISE_APPLICATION_ERROR(-20008, '運用日付が設定されていません。');
      END IF;

      EXIT WHEN SELECT_OPERATIONDATE%NOTFOUND;

      IF SELECT_OPERATIONDATE%ROWCOUNT > 1 THEN RAISE_APPLICATION_ERROR(-20009, '運用日付が複数見つかりました。');
      END IF;

    END LOOP;
    CLOSE SELECT_OPERATIONDATE;

    return (returnOperationdate);
  END GetOperationdate;

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
    vRecordS06W002T := null;

    --システム日付 と システム時間の検索
    SELECT TO_CHAR(SYSDATE, 'YYYYMMDD'), TO_CHAR(SYSDATE, 'hh24miss')
      INTO vRecordS06W002T.KSHNSYSTMYMD, vRecordS06W002T.KSHNSYSTMJIKAN
      FROM DUAL;

    vRecordS06W002T.KOSHAAPKIHONCD := iUserApKihoncd; -- 更新者ＡＰ基本コード
    vRecordS06W002T.KOSHASHOPCD    := iUserShopcd; -- 更新者ショップコード
    vRecordS06W002T.KOSHACD        := iTnshacd; -- 更新者コード
    vRecordS06W002T.KSHNUNYOYMD    := GetOperationdate(iApKihoncd, iShopcd); -- 更新運用日付
    vRecordS06W002T.KSHNIPADRSE    := iIpadrse; -- 更新ＩＰアドレス
    vRecordS06W002T.KSHNPGMID      := 'C1003B21'; -- 更新プログラムＩＤ

  END SetUserInfo;

  /*****************************************************************************/
  /* プロシージャ名   ：売上情報ヘッダワーク登録                               */
  /* 引数             ：なし                                                   */
  /*****************************************************************************/
  PROCEDURE InsertHeaderWRK IS
  BEGIN
    INSERT INTO S06W002T
    VALUES
      ( vRecordS06W002T.APKIHONCD          -- AP基本コード
      , vRecordS06W002T.SHOPCD             -- ショップコード
      , vRecordS06W002T.PRCSIDNO           -- 処理ID番号
      , vRecordS06W002T.URITORIKOMIKANRINO -- 売上取込管理番号
      , NULL -- 売上伝票番号
      , NULL -- 枝番号
      , vRecordS06W002T.DNPYKBN     -- 伝票区分
      , vRecordS06W002T.TOKUISAKICD -- 得意先コード
      , vRecordS06W002T.SIIRESAKICD -- 仕入先コード
      , vRecordS06W002T.NYUKAMOTOCD -- 入荷元コード
      , vRecordS06W002T.URIDD       -- 売上日
      , NULL -- 売上金額
      , NULL -- 消費税金額
      , vRecordS06W002T.DLVRYTNSHACD             -- 配達担当者コード
      , vRecordS06W002T.DLVRYKANKATSUAPKIHONCD   -- 配達管轄AP基本コード
      , vRecordS06W002T.DLVRYKANKATSUSHOPCD      -- 配達管轄ショップコード
      , vRecordS06W002T.DLVRYKBN                 -- 配達区分
      , vRecordS06W002T.EIGYOTNSHACD             -- 営業担当者コード
      , vRecordS06W002T.SHUKKASAKIJISSEKITNSHACD -- 出荷先実績担当者コード
      , vRecordS06W002T.NYUKAMOTOJISSEKITNSHACD  -- 入荷元実績担当者コード
      , vRecordS06W002T.SKYSAKICD                -- 請求先コード
      , vRecordS06W002T.DNPYBIKO                 -- 伝票備考
      , vRecordS06W002T.DATSKSMOTOKBN            -- データ作成元区分
      , NULL -- 税計算区分
      , '0' -- 売上データ作成フラグ
      , '1' -- 有効区分
      , vRecordS06W002T.KOSHAAPKIHONCD -- 登録者ＡＰ基本コード
      , vRecordS06W002T.KOSHASHOPCD    -- 登録者ショップコード
      , vRecordS06W002T.KOSHACD        -- 登録者コード
      , vRecordS06W002T.KSHNSYSTMYMD   -- 登録システム日付
      , vRecordS06W002T.KSHNSYSTMJIKAN -- 登録システム時間
      , vRecordS06W002T.KSHNUNYOYMD    -- 登録運用日付
      , vRecordS06W002T.KSHNIPADRSE    -- 登録ＩＰアドレス
      , vRecordS06W002T.KSHNPGMID      -- 登録プログラムＩＤ
      , vRecordS06W002T.KOSHAAPKIHONCD -- 更新者AP基本コード
      , vRecordS06W002T.KOSHASHOPCD    -- 更新者ショップコード
      , vRecordS06W002T.KOSHACD        -- 更新者コード
      , vRecordS06W002T.KSHNSYSTMYMD   -- 更新システム日付
      , vRecordS06W002T.KSHNSYSTMJIKAN -- 更新システム時間
      , vRecordS06W002T.KSHNUNYOYMD    -- 更新運用日付
      , vRecordS06W002T.KSHNIPADRSE    -- 更新ＩＰアドレス
      , vRecordS06W002T.KSHNPGMID      -- 更新プログラムＩＤ
      );
  END InsertHeaderWRK;

  /*****************************************************************************/
  /* プロシージャ名   ：売上情報明細ワーク登録                                 */
  /* 引数             ：なし                                                   */
  /*****************************************************************************/
  PROCEDURE InsertMeisaiWRK IS
  BEGIN
    INSERT INTO S06W003T
    VALUES
      ( vRecordS06W002T.APKIHONCD            -- ヘッダ.AP基本コード
      , vRecordS06W002T.SHOPCD               -- ヘッダ.ショップコード
      , vRecordS06W002T.PRCSIDNO             -- ヘッダ.処理ID番号
      , vRecordS06W002T.URITORIKOMIKANRINO   -- ヘッダ.売上取込管理番号
      , NULL -- 売上伝票番号
      , NULL -- 枝番号
      , vRecordS06W002T.DATSKSMOTOKBN        -- ヘッダ.データ作成元区分
      , vRecordS06W003T.URIDNPYMEISAINO    -- 売上伝票明細番号
      , vRecordS06W003T.URIDNPYMEISAIEDANO -- 売上伝票明細枝番号
      , vRecordS06W003T.SHNCD            -- 商品コード
      , vRecordS06W003T.KEIJOKBN         -- 計上区分
      , vRecordS06W003T.SURYO            -- 数量
      , vRecordS06W003T.UNT              -- 単価
      , vRecordS06W003T.KIN              -- 金額
      , NULL -- 単価消費税金額
      , NULL -- 消費税金額
      , NULL -- 税区分
      , '0'                               -- 明細区分（'0'：商品）
      , vRecordS06W003T.NYURYOKUMEISAIFLG -- 入力明細フラグ（'1'：入力行; '0'：展開行）
      , vRecordS06W003T.TANPINMEISAIFLG   -- 単品明細フラグ
      , vRecordS06W003T.MEISAIBIKO        -- 明細備考
      , vRecordS06W003T.STSHNCD           -- セット商品コード
      , vRecordS06W003T.KATASIKI          -- 型式
      , vRecordS06W003T.SIIRESURYO        -- 仕入数量
      , vRecordS06W003T.SIIREKIN          -- 仕入金額
      , vRecordS06W003T.SIIREUNT          -- 仕入単価
      , vRecordS06W003T.SIIRESHOHITAXKIN  -- 仕入消費税金額
      , '1' -- 有効区分
      , vRecordS06W002T.KOSHAAPKIHONCD -- ヘッダ.登録者ＡＰ基本コード
      , vRecordS06W002T.KOSHASHOPCD    -- ヘッダ.登録者ショップコード
      , vRecordS06W002T.KOSHACD        -- ヘッダ.登録者コード
      , vRecordS06W002T.KSHNSYSTMYMD   -- ヘッダ.登録システム日付
      , vRecordS06W002T.KSHNSYSTMJIKAN -- ヘッダ.登録システム時間
      , vRecordS06W002T.KSHNUNYOYMD    -- ヘッダ.登録運用日付
      , vRecordS06W002T.KSHNIPADRSE    -- ヘッダ.登録ＩＰアドレス
      , vRecordS06W002T.KSHNPGMID      -- ヘッダ.登録プログラムＩＤ
      , vRecordS06W002T.KOSHAAPKIHONCD -- ヘッダ.更新者AP基本コード
      , vRecordS06W002T.KOSHASHOPCD    -- ヘッダ.更新者ショップコード
      , vRecordS06W002T.KOSHACD        -- ヘッダ.更新者コード
      , vRecordS06W002T.KSHNSYSTMYMD   -- ヘッダ.更新システム日付
      , vRecordS06W002T.KSHNSYSTMJIKAN -- ヘッダ.更新システム時間
      , vRecordS06W002T.KSHNUNYOYMD    -- ヘッダ.更新運用日付
      , vRecordS06W002T.KSHNIPADRSE    -- ヘッダ.更新ＩＰアドレス
      , vRecordS06W002T.KSHNPGMID      -- ヘッダ.更新プログラムＩＤ
      );
  END InsertMeisaiWRK;


  /*****************************************************************************/
  /* プロシージャ名   ：売上消費税計算                                         */
  /* 引数             ：iTaxsetteikbn　税設定区分                              */
  /*****************************************************************************/
  PROCEDURE getUriageTax(iTaxsetteikbn IN VARCHAR2) IS
  BEGIN

    --ADD START 20190521 TJS)takio 軽減税率対応
    IF vRecordS06W003T.TAXKBN IN ('4','5') AND vRecordS06W002T.URIDD <= '20190930' THEN
      vRecordS06W003T.TAXKBN := '1';
    END IF;
    --ADD E N D 20190521 TJS)takio

    --ADD START 20190703 TJS)takio 軽減税率対応
    IF vRecordS06W003T.TAXKBN <> '5' THEN
    --ADD E N D 20190703 TJS)takio
      -- 外税（税計算区分が'2'）の場合：
      IF vRecordS06W002T.TAXKEISANKBN = '2' THEN
        -- 単価消費税金額
        vRecordS06W003T.UNTSHOHITAXKIN := 0;
        -- 消費税金額（金額　＝　単価　×　数量）
        vRecordS06W003T.SHOHITAXKIN := Z1808B01.SYOHIZEIKEISAN( '1', -- 単体計算フラグ（する）
                                                                iTaxsetteikbn, -- 税設定区分
                                                       vRecordS06W003T.TAXKBN, -- 税区分
                                                 vRecordS06W002T.TAXKEISANKBN, -- 税計算区分
                                                        vRecordS06W002T.URIDD, -- 切り替え日：売上日
                                                    vRecordS06W003T.APKIHONCD, -- AP基本コード
                                                       vRecordS06W003T.SHOPCD, -- ショップコード
                                                                         NULL, -- 仕入先コード
                                                                         NULL, -- 数量
                                                         vRecordS06W003T.KIN); -- 金額
      -- 内税の場合：
      ELSE
        -- 単価消費税金額
        vRecordS06W003T.UNTSHOHITAXKIN := Z1808B01.SyohizeiKeisan('1', --単体計算フラグ（する）
                                                                iTaxsetteikbn, -- 税設定区分
                                                       vRecordS06W003T.TAXKBN, -- 税区分
                                                 vRecordS06W002T.TAXKEISANKBN, -- 税計算区分
                                                        vRecordS06W002T.URIDD, -- 切り替え日：売上日
                                                    vRecordS06W003T.APKIHONCD, -- AP基本コード
                                                       vRecordS06W003T.SHOPCD, -- ショップコード
                                                                         NULL, -- 仕入先コード
                                                                         NULL, -- 数量
                                                         vRecordS06W003T.UNT); -- 単価
        -- 消費税金額　＝　単価消費税金額　×　数量
        vRecordS06W003T.SHOHITAXKIN := vRecordS06W003T.UNTSHOHITAXKIN * vRecordS06W003T.SURYO;
      END IF;
    --ADD START 20190703 TJS)takio 軽減税率対応
    ELSE
	      Z1808B01.SetSyohizeiKeisan(iTaxsetteikbn, --税設定区分
	                            vRecordS06W002T.TAXKEISANKBN, --税計算区分
	                                   vRecordS06W002T.URIDD, --切り替え日
	                               vRecordS06W003T.APKIHONCD, --AP基本コード
	                                  vRecordS06W003T.SHOPCD, --ショップコード
	                                   vRecordS06W003T.SURYO, --数量
	                                   vRecordS06W003T.SHNCD, --商品コード
	                                vRecordS06W002T.DLVRYKBN, --配達区分
	                                vRecordS06W003T.KEIJOKBN, --計上区分
	                          vRecordS06W003T.UNTSHOHITAXKIN, --戻り値：単価消費税
	                            vRecordS06W003T.SHOHITAXKIN); --戻り値：消費税
	END IF;
    --ADD E N D 20190703 TJS)takio
  END getUriageTax;

  /*****************************************************************************/
  /* プロシージャ名   ：売上情報明細ワーク消費税更新                           */
  /* 引数             ：なし                                                   */
  /*****************************************************************************/
  PROCEDURE UpdateTaxMeisaiWRK IS
  BEGIN
    UPDATE S06W003T UMW
       SET
           UMW.UNTSHOHITAXKIN   = vRecordS06W003T.UNTSHOHITAXKIN   -- 単価消費税金額
         , UMW.SHOHITAXKIN      = vRecordS06W003T.SHOHITAXKIN      -- 消費税金額
         , UMW.TAXKBN           = vRecordS06W003T.TAXKBN           -- 税区分
         , UMW.SIIRESHOHITAXKIN = vRecordS06W003T.SIIRESHOHITAXKIN -- 仕入消費税金額
         , UMW.KOSHAAPKIHONCD = vRecordS06W002T.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
         , UMW.KOSHASHOPCD    = vRecordS06W002T.KOSHASHOPCD    -- 更新者ショップコード
         , UMW.KOSHACD        = vRecordS06W002T.KOSHACD        -- 更新者コード
         , UMW.KSHNSYSTMYMD   = vRecordS06W002T.KSHNSYSTMYMD   -- 更新システム日付: システム日付
         , UMW.KSHNSYSTMJIKAN = vRecordS06W002T.KSHNSYSTMJIKAN -- 更新システム時間: システム時間
         , UMW.KSHNUNYOYMD    = vRecordS06W002T.KSHNUNYOYMD    -- 更新運用日付: システム管理マスタ.文字設定値
         , UMW.KSHNIPADRSE    = vRecordS06W002T.KSHNIPADRSE    -- 更新ＩＰアドレス: パラメータ．更新ＩＰアドレス
         , UMW.KSHNPGMID      = 'C1003B21'                     -- 更新プログラムＩＤ: C1003B21
     WHERE
           UMW.APKIHONCD = vRecordS06W003T.APKIHONCD
       AND UMW.SHOPCD = vRecordS06W003T.SHOPCD
       AND UMW.PRCSIDNO = vRecordS06W003T.PRCSIDNO
       AND UMW.URITORIKOMIKANRINO = vRecordS06W003T.URITORIKOMIKANRINO
       AND UMW.URIDNPYMEISAINO = vRecordS06W003T.URIDNPYMEISAINO
       AND UMW.URIDNPYMEISAIEDANO = vRecordS06W003T.URIDNPYMEISAIEDANO
       AND UMW.YUKOKBN = '1'
     ;
  END UpdateTaxMeisaiWRK;

  /*****************************************************************************/
  /* プロシージャ名   ：売上情報ヘッダワーク消費税更新                         */
  /* 引数             ：なし                                                   */
  /*****************************************************************************/
  PROCEDURE UpdateTaxHeaderWRK IS
  BEGIN
    UPDATE S06W002T UHW
       SET
           UHW.URIKIN       = vRecordS06W002T.URIKIN       -- 売上金額
         , UHW.SHOHITAXKIN  = vRecordS06W002T.SHOHITAXKIN  -- 消費税金額
         , UHW.TAXKEISANKBN = vRecordS06W002T.TAXKEISANKBN -- 税計算区分
         , UHW.KOSHAAPKIHONCD = vRecordS06W002T.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
         , UHW.KOSHASHOPCD    = vRecordS06W002T.KOSHASHOPCD    -- 更新者ショップコード
         , UHW.KOSHACD        = vRecordS06W002T.KOSHACD        -- 更新者コード
         , UHW.KSHNSYSTMYMD   = vRecordS06W002T.KSHNSYSTMYMD   -- 更新システム日付: システム日付
         , UHW.KSHNSYSTMJIKAN = vRecordS06W002T.KSHNSYSTMJIKAN -- 更新システム時間: システム時間
         , UHW.KSHNUNYOYMD    = vRecordS06W002T.KSHNUNYOYMD    -- 更新運用日付: システム管理マスタ.文字設定値
         , UHW.KSHNIPADRSE    = vRecordS06W002T.KSHNIPADRSE    -- 更新ＩＰアドレス: パラメータ．更新ＩＰアドレス
         , UHW.KSHNPGMID      = 'C1003B21'                     -- 更新プログラムＩＤ: C1003B21
     WHERE
           UHW.APKIHONCD = vRecordS06W002T.APKIHONCD
       AND UHW.SHOPCD = vRecordS06W002T.SHOPCD
       AND UHW.PRCSIDNO = vRecordS06W002T.PRCSIDNO
       AND UHW.URITORIKOMIKANRINO = vRecordS06W002T.URITORIKOMIKANRINO
       AND UHW.YUKOKBN = '1'
     ;
  END UpdateTaxHeaderWRK;

  /*****************************************************************************/
  /* プロシージャ名   ：構成商品登録                                           */
  /* 引数(OUT)        ：構成商品消費税                                         */
  /*****************************************************************************/
  PROCEDURE InsertKoseiShn (oSumTaxkin OUT NUMBER) IS
    -- 変数宣言
    wRenban                   NUMBER(2,0);  -- 連番
    wOyashnSuryo              NUMBER(9,1);  -- 親商品数量
    wOyashnTaxkbn             VARCHAR2(1);  -- 親商品税区分
    wKoseishnSuryo            NUMBER(9,1);  -- 構成商品数量
    wDlvryPrice               NUMBER(10,2); -- 配達価格
    wMochikaeriPrice          NUMBER(10,2); -- 持ち帰り価格
    wTaxsetteikbn             VARCHAR2(1);  -- 構成商品税設定区分
    --ADD START 20190521 TJS)takio 軽減税率対応
    wTaxkbn                   VARCHAR2(1);  -- 構成品税区分
    --ADD E N D 20190521 TJS)takio
    -- ■:1 PKUT)YANG 軽減税率対応 2019-08-05
    wUrikinhasukeisankbn      VARCHAR2(1)  := NULL;  -- 売上金額端数計算区分
    -- ■:1 PKUT)YANG 軽減税率対応 2019-08-05
-- ■:1 ISAC)Shiba 価格改定対応 2021-01-15
    wRknKindTokubetsuUntFlg   VARCHAR2(1);  -- 料金種別特別単価フラグ
    wStShnCd                  VARCHAR2(6);
    wTkmRenban                NUMBER(2,0);  -- 連番
-- ■:2 ISAC)Shiba 価格改定対応 2021-01-15
    -- 構成商品リストの取得
    CURSOR SELECT_KOSEISHN IS
      SELECT
          TKM.KOSEISHNCD                         -- 構成商品コード
        , TKM.SURYO                              -- 構成商品数量
        , TKM.DLVRYPRICE                         -- 配達価格
        , TKM.MOCHIKAERIPRICE                    -- 持ち帰り価格
        , SHV.TAXSETTEIKBN                       -- 税設定区分
        , (
          CASE
            WHEN SHV.TAXSETTEIKBN = '1'
              THEN SHV.TAXKBN
            ELSE '1'
            END
        ) AS TAXKBN                              -- 税区分
        , TKM.STSHNCD                            -- セット商品コード
        , TKM.RENBAN                             -- 連番
      FROM
        S02M008T TKM                             -- 展開マスタ
        INNER JOIN S02V001T SHV                  -- 商品マスタビュー
        ON
          (
            SHV.SHNAPKIHONCD    = TKM.APKIHONCD
            AND SHV.SHNCD       = TKM.STSHNCD
          )
      WHERE
        SHV.APKIHONCD           = vRecordS06W003T.APKIHONCD
        AND SHV.SHOPCD          = vRecordS06W003T.SHOPCD
        AND TKM.STSHNCD         = vRecordS06W003T.SHNCD
        AND TKM.YUKOKBN         = '1'
        AND SHV.KIHONSHNYUKOKBN = '1'
        AND SHV.SHOPYUKOKBN     = '1'
      ORDER BY
        TKM.RENBAN
      ;
  BEGIN
    -- 変数初期化
    wRenban := 0;
    wOyashnSuryo := vRecordS06W003T.SURYO;
    wOyashnTaxkbn := vRecordS06W003T.TAXKBN;
    oSumTaxkin := 0;
  -- ■:2 PKUT)YANG 軽減税率対応 2019-08-05
    -- ショップマスタ取得
    SELECT
      SHPM.URIKINHASUKEISANKBN  -- 売上金額端数計算区分
    INTO wUrikinhasukeisankbn
    FROM
      S02M046T SHPM
    WHERE
      SHPM.APKIHONCD = vRecordS06W003T.APKIHONCD
      AND SHPM.SHOPCD = vRecordS06W003T.SHOPCD
      AND SHPM.YUKOKBN = '1'
    ;
  -- ■:2 PKUT)YANG 軽減税率対応 2019-08-05
-- ■:1 ISAC)Shiba 価格改定対応 2021-01-15
   SELECT
     TSM.RKNKINDTOKUBETSUUNTFLG -- 料金種別特別単価フラグ
   INTO
     wRknKindTokubetsuUntFlg
   FROM
     S02M013T TSM
   WHERE
     TSM.APKIHONCD = vRecordS06W002T.APKIHONCD
     AND TSM.SHOPCD = vRecordS06W002T.SHOPCD
     AND TSM.TOKUISAKICD = vRecordS06W002T.TOKUISAKICD
     AND TSM.YUKOKBN = '1';
-- ■:2 ISAC)Shiba 価格改定対応 2021-01-15
    -- 構成商品順番で登録する
    OPEN SELECT_KOSEISHN;
      LOOP
        FETCH SELECT_KOSEISHN
          INTO vRecordS06W003T.SHNCD  -- 構成商品コード
             , wKoseishnSuryo         -- 構成商品数量
             , wDlvryPrice            -- 配達価格
             , wMochikaeriPrice       -- 持ち帰り価格
             , wTaxsetteikbn          -- 構成商品税設定区分
             --UPD START 20190521 TJS)takio 軽減税率対応
             --, vRecordS06W003T.TAXKBN -- 構成商品税区分
             , wTaxkbn --構成品税区分
             --UPD E N D 20190521 TJS)takio
-- ■:1 ISAC)Shiba 価格改定対応 2021-01-15
             , wStShnCd
             , wTkmRenban
-- ■:2 ISAC)Shiba 価格改定対応 2021-01-15
        ;

        -- データない場合
        IF SELECT_KOSEISHN%ROWCOUNT = 0 THEN RAISE_APPLICATION_ERROR(-20012, '構成商品の有効データが見つかりませんでした'); EXIT;
        END IF;
        -- 終了
        EXIT WHEN SELECT_KOSEISHN%NOTFOUND;

        -- 登録情報設定
        wRenban := wRenban + 1;
        vRecordS06W003T.URIDNPYMEISAIEDANO := wRenban;          -- 売上伝票明細枝番号：１から連番設定
        vRecordS06W003T.SURYO := wKoseishnSuryo * wOyashnSuryo; -- 数量：構成商品数量　×　親商品数量;
        vRecordS06W003T.NYURYOKUMEISAIFLG := '0'; -- 入力明細フラグ('0'：展開行)
        vRecordS06W003T.TANPINMEISAIFLG := '1';   -- 単品明細フラグ
        vRecordS06W003T.KATASIKI := NULL;         -- 型式
        -- 仕入情報なし

        --ADD START 20190521 TJS)takio 軽減税率対応
        -- 親商品税区分を設定
        --親商品の税区分が「5:子明細商品」の場合
        -- 税区分を親商品と一致する
        vRecordS06W003T.TAXKBN := wOyashnTaxkbn;
        --ADD E N D 20190521 TJS)takio

        -- 無償の場合：金額が0
        IF vRecordS06W003T.KEIJOKBN <> '01' AND vRecordS06W003T.KEIJOKBN <> '02' AND vRecordS06W003T.KEIJOKBN <> '08' THEN
          vRecordS06W003T.UNT := 0;
          vRecordS06W003T.KIN := 0;
        -- 有償：
        ELSE
-- ■:1 ISAC)Shiba 価格改定対応 2021-01-15
          -- 料金種別特別単価フラグ="1"で得意先別セット商品単価展開マスタが存在する場合は特別単価を使用する
          IF wRknKindTokubetsuUntFlg = '1' THEN
            BEGIN
              SELECT
                  TKM.DLVRYPRICE
                , TKM.MOCHIKAERIPRICE
              INTO
                  wDlvryPrice
                , wMochikaeriPrice
              FROM
                  S02M067T TKM
              INNER JOIN S02M066T TSM
                ON (TSM.APKIHONCD    = TKM.APKIHONCD
                AND TSM.SHOPCD       = TKM.SHOPCD
                AND TSM.STSHNCD      = TKM.STSHNCD
                AND TSM.TOKUISAKICD  = TKM.TOKUISAKICD
                AND vRecordS06W002T.URIDD BETWEEN TSM.TEKIYOSTRTDD AND TSM.TEKIYOSHRYDD
                AND TSM.TEKIYOSTRTDD = TKM.TEKIYOSTRTDD
                AND TSM.YUKOKBN      = '1' )
              WHERE
                    TKM.APKIHONCD    = vRecordS06W002T.APKIHONCD
               AND  TKM.SHOPCD       = vRecordS06W002T.SHOPCD
               AND  TKM.STSHNCD      = wStShnCd
               AND  TKM.TOKUISAKICD  = vRecordS06W002T.TOKUISAKICD
               AND  TKM.RENBAN       = wTkmRenban
               AND  TKM.SHNCD        = vRecordS06W003T.SHNCD
               AND  TKM.YUKOKBN      = '1';
            EXCEPTION
              WHEN NO_DATA_FOUND THEN
                NULL;
            END;
          END IF;
-- ■:2 ISAC)Shiba 価格改定対応 2021-01-15
          vRecordS06W003T.UNT := wDlvryPrice;          -- 単価：構成商品配達価格
-- ■:3 PKUT)YANG 軽減税率対応 2019-08-05
--          vRecordS06W003T.KIN := vRecordS06W003T.UNT * vRecordS06W003T.SURYO; -- 金額：配達価格　×　数量
--          IF vRecordS06W002T.DLVRYKBN = '3' THEN vRecordS06W003T.KIN := wMochikaeriPrice * vRecordS06W003T.SURYO; -- 金額：持ち帰り（３）の場合
          vRecordS06W003T.KIN := Z1808B01.roundValue(vRecordS06W003T.UNT * vRecordS06W003T.SURYO, wUrikinhasukeisankbn); -- 金額：配達価格　×　数量
          IF vRecordS06W002T.DLVRYKBN = '3' THEN vRecordS06W003T.KIN := Z1808B01.roundValue(wMochikaeriPrice * vRecordS06W003T.SURYO, wUrikinhasukeisankbn); -- 金額：持ち帰り（３）の場合
-- ■:3 PKUT)YANG 軽減税率対応 2019-08-05
          END IF;
        END IF;

        -- 構成商品登録
        InsertMeisaiWRK;

        -- 消費税計算
        IF vRecordS06W002T.DLVRYKBN = '3' THEN vRecordS06W003T.UNT := wMochikaeriPrice; -- 消費税計算用単価：持ち帰り（３）の場合
        END IF;
        getUriageTax( wTaxsetteikbn); -- 売上消費税
        -- 税設定するで税区分が課税以外は0円をセット
        --UPD START 20190521 TJS)takio 軽減税率対応
        --IF wTaxsetteikbn = '1' AND vRecordS06W003T.TAXKBN <> '1' THEN
        IF wTaxsetteikbn = '1' AND vRecordS06W003T.TAXKBN NOT IN ('1','4') THEN
        --UPD E N D 20190521 TJS)takio
          vRecordS06W003T.UNTSHOHITAXKIN := 0;
          vRecordS06W003T.SHOHITAXKIN := 0;
        END IF;

        --DEL START 20190521 TJS)takio 軽減税率対応
        -- 税区分を親商品と一致する
        --vRecordS06W003T.TAXKBN := wOyashnTaxkbn;
        --DEL E N D 20190521 TJS)takio
        -- 構成商品消費税更新
        UpdateTaxMeisaiWRK;

        -- 消費税総額
        oSumTaxkin := oSumTaxkin + vRecordS06W003T.SHOHITAXKIN;

      END LOOP;
    CLOSE SELECT_KOSEISHN;

  END InsertKoseiShn;

  /*****************************************************************************/
  /* プロシージャ名   ：消費税調整商品登録                                     */
  /* 引数             ：消費税調整金額                                         */
  /*****************************************************************************/
  PROCEDURE InsertShohiTaxChosei (iTaxChosei IN NUMBER) IS
  BEGIN
    -- 消費税調整商品コードの取得
    SELECT SKM.MOJISETTEIATAI
      INTO vRecordS06W003T.SHNCD
      FROM S02M006T SKM
     WHERE SKM.APKIHONCD = '000'
       AND SKM.SHOPCD = '000'
       AND SKM.SETTEINO = '166'
       AND SKM.RCRDKBN = 'M'
       AND SKM.SETTEICD = '0'
       AND SKM.YUKOKBN = '1'
    ;
    --DEL START 20190521 TJS)takio 軽減税率対応
    ---- 税区分の取得
    --SELECT
    --       ( CASE WHEN SHV.TAXSETTEIKBN = '1' THEN SHV.TAXKBN ELSE '1' END ) AS TAXKBN -- 税区分
    --  INTO vRecordS06W003T.TAXKBN
    --  FROM S02V001T SHV        -- 商品マスタビュー
    -- WHERE SHV.APKIHONCD = vRecordS06W003T.APKIHONCD
    --   AND SHV.SHOPCD = vRecordS06W003T.SHOPCD
    --   AND SHV.SHNCD = vRecordS06W003T.SHNCD
    --   AND SHV.SHOPYUKOKBN = '1'
    --   AND SHV.KIHONSHNYUKOKBN = '1'
    --;
    --DEL E N D 20190521 TJS)takio

    -- 登録情報設定
    vRecordS06W003T.URIDNPYMEISAIEDANO := vRecordS06W003T.URIDNPYMEISAIEDANO + 1; -- 売上伝票明細枝番号：セット商品の展開行の最後連番　＋　1
    vRecordS06W003T.SURYO := 1; -- 数量：定数１
    vRecordS06W003T.UNT := 0; -- 単価：定数０
    vRecordS06W003T.KIN := 0; -- 金額：定数０
    vRecordS06W003T.UNTSHOHITAXKIN := 0; -- 単価消費税金額：定数０
    vRecordS06W003T.SHOHITAXKIN := iTaxChosei; -- 消費税金額：消費税調整金額
    vRecordS06W003T.MEISAIKBN := '3'; -- 明細区分：３（消費税調整）
    vRecordS06W003T.NYURYOKUMEISAIFLG := '0'; -- 入力明細フラグ('0'：展開行)
    vRecordS06W003T.TANPINMEISAIFLG := '1';   -- 単品明細フラグ
    vRecordS06W003T.KATASIKI := NULL;         -- 型式

    -- 消費税調整商品登録
    InsertMeisaiWRK;
    UpdateTaxMeisaiWRK;
  END InsertShohiTaxChosei;

  /*****************************************************************************/
  /* プロシージャ名   ：売上情報取込ワークから登録                             */
  /* 引数             ：なし                                                   */
  /*****************************************************************************/
  PROCEDURE InsertFromUriWRK IS
    -- 変数宣言
    wHeaderKeys                   VARCHAR2(19); -- ヘッダキー（AP-ショップ-売上取込管理番号）
    wZaikoUnt                     NUMBER(10,2); -- 在庫単価
    wTokuisakiTanka               NUMBER(10,2); -- 得意先別商品別単価
    wCampaignTanka                NUMBER(10,2); -- キャンペーン単価
    wTanka                        NUMBER(10,2); -- 単価
    wHansokuStrtDD                VARCHAR2(8);  -- 販促開始日
    wHansokuShryDD                VARCHAR2(8);  -- 販促終了日
    wHansokuDlvryPrice            NUMBER(10,2); -- 販促配達価格
    wHansokuMochikaeriPrice       NUMBER(10,2); -- 販促持ち帰り価格
    wHansokuYusenKbn              VARCHAR(1);   -- 販促優先区分
    wPriceKbn                     VARCHAR(1);   -- 価格区分
    wHyojunPriceKirikaeDD         VARCHAR(8);   -- 標準価格切替日
    wGenzHyojunDlvryPrice         NUMBER(10,2); -- 現標準配達価格
    wShinHyojunDlvryPrice         NUMBER(10,2); -- 新標準配達価格
    wGenzHyojunMochikaeriPrice    NUMBER(10,2); -- 現標準持ち帰り価格
    wShinHyojunMochikaeriPrice    NUMBER(10,2); -- 新標準持ち帰り価格
    wOpnPriceKirikaeDD            VARCHAR(8);   -- オープン価格切替日
    wGenzOpnDlvryPrice            NUMBER(10,2); -- 現オープン配達価格
    wShinOpnDlvryPrice            NUMBER(10,2); -- 新オープン配達価格
    wGenzOpnMochikaeriPrice       NUMBER(10,2); -- 現オープン持ち帰り価格
    wShinOpnMochikaeriPrice       NUMBER(10,2); -- 新オープン持ち帰り価格
-- ■:1 PKUT)YANG 軽減税率対応 2019-07-25
    wSiireKinHasukbn              VARCHAR2(1);  -- 仕入金額端数計算区分
-- ■:1 PKUT)YANG 軽減税率対応 2019-07-25
-- ■:1 PKUT)YANG 軽減税率対応 2019-08-05
    wUrikinhasukeisankbn          VARCHAR2(1)  := NULL;  -- 売上金額端数計算区分
-- ■:1 PKUT)YANG 軽減税率対応 2019-08-05
    -- 登録用情報抽出
    CURSOR SELECT_URIJOHO IS
      SELECT
           /* ヘッダ情報 */
             UTW.APKIHONCD          -- AP基本コード
           , UTW.SHOPCD             -- ショップコード
           , UTW.URITORIKOMIKANRINO -- 売上取込管理番号
           , UTW.DNPYKBN            -- 伝票区分
           , UTW.TOKUISAKICD        -- 得意先コード
           , UTW.SIIRESAKICD        -- 仕入先コード
           , UTW.SOKOCD             -- 倉庫コード（入荷元コード）
           , UTW.URIDD              -- 売上日
           , ( CASE WHEN UTW.DLVRYTNSHACD IS NULL THEN TSM.DLVRYTNSHACD ELSE UTW.DLVRYTNSHACD END ) AS DLVRYTNSHACD -- 配達担当者コード
           , TSM.DLVRYKANKATSUAPKIHONCD   -- 配達管轄AP基本コード
           , TSM.DLVRYKANKATSUSHOPCD      -- 配達管轄ショップコード
           , ( CASE WHEN UTW.DLVRYKBN IS NULL THEN TSM.DLVRYKBN ELSE UTW.DLVRYKBN END ) AS DLVRYKBN                 -- 配達区分
           , ( CASE WHEN UTW.EIGYOTNSHACD IS NULL THEN TSM.EIGYOTNSHACD ELSE UTW.EIGYOTNSHACD END ) AS EIGYOTNSHACD -- 営業担当者コード
           , ( CASE WHEN UTW.SHUKKASAKIJISSEKITNSHACD IS NULL THEN                 -- 出荷先実績担当者コード：設定なし→マスタから取得する
                         ( CASE WHEN TSM.URIKEIJOTNSHAKBN = '2' THEN TSM.EIGYOTNSHACD     -- 売上計上担当者区分が営業担当者（２）の場合はマスタ.営業担当者
                           ELSE TSM.DLVRYTNSHACD END )                                    -- 売上計上担当者区分がその他の場合はマスタ.配達担当者
               ELSE UTW.SHUKKASAKIJISSEKITNSHACD END ) AS SHUKKASAKIJISSEKITNSHACD -- 出荷先実績担当者コード：設定あり→設定した値を取得する
           , UTW.NYUKAMOTOJISSEKITNSHACD  -- 入荷元実績担当者コード
           , TSM.SKYSAKICD                -- 請求先コード
           , UTW.DNPYBIKO       -- 伝票備考
           /*  明細情報  */
           , UTW.URITORIKOMIKANRIMEISAINO -- 売上取込管理明細番号（売上伝票明細番号）
           , UTW.SHNCD      -- 商品コード
           , UTW.KEIJOKBN   -- 計上区分
           , UTW.SURYO      -- 数量
           , UTW.URIUNT     -- 売上単価
           , ( CASE WHEN SHM.STKBN = '0' THEN '1' ELSE '0' END ) AS TANPINMEISAIFLG -- 単品明細フラグ(セット区分から判定)
           , UTW.MEISAIBIKO -- 明細備考
           , ( CASE WHEN SHM.STKBN = '0' THEN NULL ELSE UTW.SHNCD END ) AS STSHNCD  -- セット商品コード(セット区分から判定)
           , UTW.KATASIKI   -- 型式
           , ( CASE WHEN UTW.DNPYKBN IN ('21', '05') AND UTW.SIIREUNT IS NULL THEN SHM.SIIREUNT -- 仕入単価：未設定の場合は商品マスタから取得
               -- ■：1 Base)chokenkun 2016-10-31 入出庫情報ＢＵＧ修正
                    WHEN UTW.DNPYKBN IN ('14', '15') THEN SHM.ZAIKOUNT -- 在庫単価：仕入単価を無視して、商品マスタの在庫単価を設定
                    WHEN UTW.DNPYKBN IN ('11', '12') THEN 0            -- ０：売上のみ(11)、経費(12)のみの場合
               -- ■：2 Base)chokenkun 2016-10-31 入出庫情報ＢＵＧ修正
               ELSE UTW.SIIREUNT END )
           /*  単価設定情報  */
           , SHM.ZAIKOUNT   -- 在庫単価
-- ■:1 ISAC)Shiba 価格改定対応 2021-01-15
--         , ( CASE WHEN UTW.DLVRYKBN = '3' THEN TKM.MOCHIKAERIPRICE
--                  WHEN UTW.DLVRYKBN IS NULL AND TSM.DLVRYKBN = '3' THEN TKM.MOCHIKAERIPRICE
--             ELSE TKM.DLVRYPRICE END) -- 得意先別商品別単価
           , ( CASE WHEN TSM.RKNKINDTOKUBETSUUNTFLG = '1' THEN
                   CASE WHEN SHM.STKBN = '1' THEN
                       CASE WHEN UTW.DLVRYKBN = '3' THEN TTM.MOCHIKAERIPRICE
                            WHEN UTW.DLVRYKBN IS NULL AND TSM.DLVRYKBN = '3' THEN TTM.MOCHIKAERIPRICE
                            ELSE TTM.DLVRYPRICE
                       END
                   ELSE
                       CASE WHEN UTW.DLVRYKBN = '3' THEN TKM.MOCHIKAERIPRICE
                            WHEN UTW.DLVRYKBN IS NULL AND TSM.DLVRYKBN = '3' THEN TKM.MOCHIKAERIPRICE
                            ELSE TKM.DLVRYPRICE
                       END
                   END
               ELSE
                   NULL
               END
             )
-- ■:2 ISAC)Shiba 価格改定対応 2021-01-15
           , SHM.HANSOKUSTRTDD          -- 販促開始日
           , SHM.HANSOKUSHRYDD          -- 販促終了日
           , SHM.HANSOKUDLVRYPRICE      -- 販促配達価格
           , SHM.HANSOKUMOCHIKAERIPRICE -- 販促持ち帰り価格
           , SHM.HANSOKUYUSENKBN        -- 販促優先区分
           , ( CASE WHEN SHM.HYOJUNPRICEKIRIKAEDD <= UTW.URIDD THEN SHM.SHINPRICEKBN -- 価格区分：新価格区分
               ELSE SHM.GENZPRICEKBN END )                                           -- 価格区分：現価格区分
           , SHM.HYOJUNPRICEKIRIKAEDD                                      -- 価格切替日：標準価格
           , SHM.OPNPRICEKIRIKAEDD                                         -- 価格切替日：オープン価格
           , SHM.GENZHYOJUNPRICE                                           -- 現配達価格：標準価格
           , SHM.GENZOPNPRICE                                              -- 現配達価格：オープン価格
           , SHM.SHINHYOJUNPRICE                                           -- 新配達価格：標準価格
           , SHM.SHINOPNPRICE                                              -- 新配達価格：オープン価格
           , SHM.GENZMOCHIKAERIPRICE                                       -- 現持ち帰り価格：標準価格
           , SHM.GENZOPNMOCHIKAERIPRICE                                    -- 現持ち帰り価格：オープン価格
           , SHM.SHINMOCHIKAERIPRICE                                       -- 新持ち帰り価格：標準価格
           , SHM.SHINOPNMOCHIKAERIPRICE                                    -- 新持ち帰り価格：オープン価格

      FROM S06W004T UTW         -- 売上情報取込ワーク
        INNER JOIN S02M013T TSM -- 得意先マスタ
          ON ( TSM.APKIHONCD = UTW.APKIHONCD AND TSM.SHOPCD = UTW.SHOPCD AND TSM.TOKUISAKICD = UTW.TOKUISAKICD )
        INNER JOIN S02V001T SHM -- 商品マスタビュー
          ON ( SHM.APKIHONCD = UTW.APKIHONCD AND SHM.SHOPCD = UTW.SHOPCD AND SHM.SHNCD = UTW.SHNCD )
        LEFT OUTER JOIN S02M009T TKM -- 得意先別商品別単価マスタ
          ON ( TKM.APKIHONCD = UTW.APKIHONCD AND TKM.SHOPCD = UTW.SHOPCD AND TKM.SHNCD = UTW.SHNCD AND TKM.TOKUISAKICD = UTW.TOKUISAKICD
               AND UTW.URIDD BETWEEN TKM.TEKIYOSTRTDD AND TKM.TEKIYOSHRYDD
               AND TKM.YUKOKBN = '1' )
-- ■:1 ISAC)Shiba 価格改定対応 2021-01-15
        LEFT OUTER JOIN S02M066T TTM -- 得意先別セット商品単価マスタ
          ON ( TTM.APKIHONCD = UTW.APKIHONCD AND TTM.SHOPCD = UTW.SHOPCD AND TTM.STSHNCD = UTW.SHNCD AND TTM.TOKUISAKICD = UTW.TOKUISAKICD
               AND UTW.URIDD BETWEEN TTM.TEKIYOSTRTDD AND TTM.TEKIYOSHRYDD
               AND TTM.YUKOKBN = '1' )
-- ■:2 ISAC)Shiba 価格改定対応 2021-01-15
      WHERE
            UTW.URIDATSKSFLG   = '0'                            -- 売上データ未作成
        AND UTW.TOSHAAPKIHONCD = vRecordS06W002T.KOSHAAPKIHONCD -- 登録者ＡＰ基本コード
        AND UTW.TOSHASHOPCD    = vRecordS06W002T.KOSHASHOPCD    -- 登録者ショップコード
        AND UTW.TOSHACD        = vRecordS06W002T.KOSHACD        -- 登録者コード
        AND UTW.YUKOKBN         = '1' -- 有効区分(ワーク)
        AND TSM.YUKOKBN         = '1' -- 有効区分(得意先マスタ)
        AND SHM.KIHONSHNYUKOKBN = '1' -- 有効区分(商品マスタ：商品有効)
        AND SHM.SHOPYUKOKBN     = '1' -- 有効区分(商品マスタ：ショップ有効)
      ORDER BY UTW.APKIHONCD, UTW.SHOPCD, UTW.URITORIKOMIKANRINO, UTW.URITORIKOMIKANRIMEISAINO
    ;
  BEGIN
    -- 変数初期化
    wHeaderKeys := NULL;
  -- ■:2 PKUT)YANG 軽減税率対応 2019-08-05
    -- ショップマスタ取得
    SELECT
      SHPM.URIKINHASUKEISANKBN  -- 売上金額端数計算区分
    INTO wUrikinhasukeisankbn
    FROM
      S02M046T SHPM
    WHERE
      SHPM.APKIHONCD = vRecordS06W002T.KOSHAAPKIHONCD
      AND SHPM.SHOPCD = vRecordS06W002T.KOSHASHOPCD
      AND SHPM.YUKOKBN = '1'
    ;
  -- ■:2 PKUT)YANG 軽減税率対応 2019-08-05

    -- 行単位で抽出データの処理
    OPEN SELECT_URIJOHO;

      LOOP
        -- 変数初期化
        vRecordS06W003T := NULL;
        -- 抽出データの取得
        FETCH SELECT_URIJOHO
          INTO
          /* ヘッダワーク */
            vRecordS06W002T.APKIHONCD                -- 売上情報ヘッダワーク.AP基本コード
          , vRecordS06W002T.SHOPCD                   -- 売上情報ヘッダワーク.ショップコード
          , vRecordS06W002T.URITORIKOMIKANRINO       -- 売上情報ヘッダワーク.売上取込管理番号
          , vRecordS06W002T.DNPYKBN                  -- 売上情報ヘッダワーク.伝票区分
          , vRecordS06W002T.TOKUISAKICD              -- 売上情報ヘッダワーク.得意先コード
          , vRecordS06W002T.SIIRESAKICD              -- 売上情報ヘッダワーク.仕入先コード
          , vRecordS06W002T.NYUKAMOTOCD              -- 売上情報ヘッダワーク.入荷元コード
          , vRecordS06W002T.URIDD                    -- 売上情報ヘッダワーク.売上日
          , vRecordS06W002T.DLVRYTNSHACD             -- 売上情報ヘッダワーク.配達担当者コード
          , vRecordS06W002T.DLVRYKANKATSUAPKIHONCD   -- 売上情報ヘッダワーク.配達管轄AP基本コード
          , vRecordS06W002T.DLVRYKANKATSUSHOPCD      -- 売上情報ヘッダワーク.配達管轄ショップコード
          , vRecordS06W002T.DLVRYKBN                 -- 売上情報ヘッダワーク.配達区分
          , vRecordS06W002T.EIGYOTNSHACD             -- 売上情報ヘッダワーク.営業担当者コード
          , vRecordS06W002T.SHUKKASAKIJISSEKITNSHACD -- 売上情報ヘッダワーク.出荷先実績担当者コード
          , vRecordS06W002T.NYUKAMOTOJISSEKITNSHACD  -- 売上情報ヘッダワーク.入荷元実績担当者コード
          , vRecordS06W002T.SKYSAKICD                -- 売上情報ヘッダワーク.請求先コード
          , vRecordS06W002T.DNPYBIKO                 -- 売上情報ヘッダワーク.伝票備考
          /*  明細ワーク */
          , vRecordS06W003T.URIDNPYMEISAINO -- 売上情報明細ワーク.売上伝票明細番号
          , vRecordS06W003T.SHNCD           -- 売上情報明細ワーク.商品コード
          , vRecordS06W003T.KEIJOKBN        -- 売上情報明細ワーク.計上区分
          , vRecordS06W003T.SURYO           -- 売上情報明細ワーク.数量
          , vRecordS06W003T.UNT             -- 売上情報明細ワーク.単価
          , vRecordS06W003T.TANPINMEISAIFLG -- 売上情報明細ワーク.単品明細フラグ
          , vRecordS06W003T.MEISAIBIKO      -- 売上情報明細ワーク.明細備考
          , vRecordS06W003T.STSHNCD         -- 売上情報明細ワーク.セット商品コード
          , vRecordS06W003T.KATASIKI        -- 売上情報明細ワーク.型式
          , vRecordS06W003T.SIIREUNT        -- 売上情報明細ワーク.仕入単価
          /*  単価設定情報  */
          , wZaikoUnt                -- 在庫単価
          , wTokuisakiTanka          -- 得意先別商品別単価
          , wHansokuStrtDD           -- 販促開始日
          , wHansokuShryDD           -- 販促終了日
          , wHansokuDlvryPrice       -- 販促配達価格
          , wHansokuMochikaeriPrice  -- 販促持ち帰り価格
          , wHansokuYusenKbn         -- 販促優先区分
          , wPriceKbn                -- 価格区分
          , wHyojunPriceKirikaeDD       -- 標準価格切替日
          , wOpnPriceKirikaeDD          -- オープン価格切替日
          , wGenzHyojunDlvryPrice       -- 現標準配達価格
          , wGenzOpnDlvryPrice          -- 現オープン配達価格
          , wShinHyojunDlvryPrice       -- 新標準配達価格
          , wShinOpnDlvryPrice          -- 新オープン配達価格
          , wGenzHyojunMochikaeriPrice  -- 現標準持ち帰り価格
          , wGenzOpnMochikaeriPrice     -- 現オープン持ち帰り価格
          , wShinHyojunMochikaeriPrice  -- 新標準持ち帰り価格
          , wShinOpnMochikaeriPrice     -- 新オープン持ち帰り価格
        ;
        -- 登録データない場合
        IF SELECT_URIJOHO%ROWCOUNT = 0 THEN RAISE_APPLICATION_ERROR(-20010, '売上情報取込ワークの有効データが見つかりませんでした'); EXIT;
        END IF;
        -- 終了
        EXIT WHEN SELECT_URIJOHO%NOTFOUND;

        /* ヘッダのその他の項目 */
        -- データ作成元区分
        vRecordS06W002T.DATSKSMOTOKBN := '18';
        /*  明細のその他の項目 */
        -- 固定値
        vRecordS06W003T.URIDNPYMEISAIEDANO := 0;  -- 売上伝票明細枝番号
        vRecordS06W003T.NYURYOKUMEISAIFLG := '1'; -- 入力明細フラグ（'1'：入力行）
        -- 売上単価：単価未設定の場合
        IF vRecordS06W003T.UNT IS NULL THEN vRecordS06W003T.UNT := 0;                        -- 売上単価：無料計上区分の場合は０
           IF vRecordS06W003T.KEIJOKBN = '08' THEN vRecordS06W003T.UNT := wZaikoUnt; END IF; -- 売上単価：計上区分が経費(08)の場合は在庫単価
           IF vRecordS06W003T.KEIJOKBN IN ('01', '02') THEN
              -- キャンペーン単価の取得
              wCampaignTanka := NULL;
              IF wHansokuStrtDD IS NOT NULL AND wHansokuShryDD IS NOT NULL AND vRecordS06W002T.URIDD BETWEEN wHansokuStrtDD AND wHansokuShryDD THEN
                 IF vRecordS06W002T.DLVRYKBN = '3' THEN wCampaignTanka := wHansokuMochikaeriPrice;
                 ELSE wCampaignTanka := wHansokuDlvryPrice; END IF;
              END IF;
              -- 標準価格の取得
              wTanka := 0;
              IF wPriceKbn = '1' THEN
              	 IF wHyojunPriceKirikaeDD IS NULL OR vRecordS06W002T.URIDD >= wHyojunPriceKirikaeDD THEN   -- 新標準価格
                    IF vRecordS06W002T.DLVRYKBN = '3' THEN wTanka := wShinHyojunMochikaeriPrice;
                    ELSE wTanka := wShinHyojunDlvryPrice; END IF;
                 ELSE                                                                                      -- 現標準価格
                    IF vRecordS06W002T.DLVRYKBN = '3' THEN wTanka := wGenzHyojunMochikaeriPrice;
                    ELSE wTanka := wGenzHyojunDlvryPrice; END IF;
                 END IF;
              -- オープン価格の取得
              ELSE
                 IF wOpnPriceKirikaeDD IS NULL OR vRecordS06W002T.URIDD >= wOpnPriceKirikaeDD THEN         -- 新オープン価格
                    IF vRecordS06W002T.DLVRYKBN = '3' THEN wTanka := wShinOpnMochikaeriPrice;
                    ELSE wTanka := wShinOpnDlvryPrice; END IF;
                 ELSE                                                                                      -- 現オープン価格
                    IF vRecordS06W002T.DLVRYKBN = '3' THEN wTanka := wGenzOpnMochikaeriPrice;
                    ELSE wTanka := wGenzOpnDlvryPrice; END IF;
                 END IF;
              END IF;
              -- 単価の設定
              IF wTokuisakiTanka IS NOT NULL THEN vRecordS06W003T.UNT := wTokuisakiTanka; -- 売上単価：得意先別商品別単価
              ELSE IF wCampaignTanka IS NOT NULL AND wHansokuYusenKbn = '1' THEN          -- 売上単価：キャンペーン単価
                      vRecordS06W003T.UNT      := wCampaignTanka;
                      vRecordS06W003T.KEIJOKBN := '02';
                   ELSE vRecordS06W003T.UNT := wTanka;                                    -- 売上単価：商品単価
                   END IF;
              END IF;
           END IF;
        END IF;
        -- 売上金額計算
-- ■:3 PKUT)YANG 軽減税率対応 2019-08-05
--        vRecordS06W003T.KIN := (vRecordS06W003T.SURYO) * (vRecordS06W003T.UNT);
        vRecordS06W003T.KIN := Z1808B01.roundValue((vRecordS06W003T.SURYO) * (vRecordS06W003T.UNT), wUrikinhasukeisankbn);
-- ■:3 PKUT)YANG 軽減税率対応 2019-08-05
        /*  仕入情報 */
-- ■:2 PKUT)YANG 軽減税率対応 2019-07-25
        IF vRecordS06W002T.SIIRESAKICD IS NOT NULL THEN
           SELECT
               SSM.SIIREKINHASUKEISANKBN
           INTO wSiireKinHasukbn
           FROM S02M021T SSM
           WHERE
              SSM.APKIHONCD = vRecordS06W002T.APKIHONCD
              AND SSM.SHOPCD = vRecordS06W002T.SHOPCD
              AND SSM.SIIRESAKICD = vRecordS06W002T.SIIRESAKICD
              AND SSM.YUKOKBN = '1'
           ;
        END IF;
-- ■:2 PKUT)YANG 軽減税率対応 2019-07-25
        -- 伝票区分が仕入売上(21)と経費購入(05)の場合:仕入金額計算を実行する
        -- ヘッダ登録
        IF (wHeaderKeys IS NULL OR wHeaderKeys <> vRecordS06W002T.APKIHONCD || vRecordS06W002T.SHOPCD || vRecordS06W002T.URITORIKOMIKANRINO ) THEN
          InsertHeaderWRK;
          wHeaderKeys := vRecordS06W002T.APKIHONCD || vRecordS06W002T.SHOPCD || vRecordS06W002T.URITORIKOMIKANRINO;
        END IF;
        -- 明細登録
        InsertMeisaiWRK;

      END LOOP;
    CLOSE SELECT_URIJOHO;

  END InsertFromUriWRK;

  /*****************************************************************************/
  /* プロシージャ名   ：請求内訳情報ワークから登録                             */
  /* 引数             ：なし                                                   */
  /*****************************************************************************/
  PROCEDURE InsertFromSkyWRK IS
  BEGIN
    NULL;
  END InsertFromSkyWRK;

  /*****************************************************************************/
  /* プロシージャ名   ：消費税更新                                             */
  /* 引数             ：なし                                                   */
  /*****************************************************************************/
  PROCEDURE UpdateSyohizei IS
    -- 変数宣言
    wHeaderKeys              VARCHAR2(19); -- ヘッダキー（AP-ショップ-売上取込管理番号）
    wTaxsetteikbn            VARCHAR2(1);  -- 税計算区分
    wStshnTaxkin             NUMBER(11,0); -- セット商品消費税
    wKoseiSumTaxkin          NUMBER(11,0); -- 構成商品消費総額
    -- 更新用情報抽出
    CURSOR SELECT_URIMEISAI IS
      SELECT UMW.* -- 明細全体
      FROM S06W003T UMW -- 売上情報明細ワーク
      WHERE
            UMW.PRCSIDNO = vRecordS06W002T.PRCSIDNO -- 処理ID番号
        AND UMW.YUKOKBN = '1'                       -- 有効区分
      ORDER BY  UMW.APKIHONCD, UMW.SHOPCD, UMW.URITORIKOMIKANRINO, UMW.URIDNPYMEISAINO
    ;
  BEGIN
    -- 変数初期化
    wHeaderKeys := NULL;
    -- 行単位で抽出データの処理
    OPEN SELECT_URIMEISAI;
      LOOP
        FETCH SELECT_URIMEISAI
          INTO vRecordS06W003T;

        -- データない場合
        IF SELECT_URIMEISAI%ROWCOUNT = 0 THEN RAISE_APPLICATION_ERROR(-20011, '明細ワークの有効データが見つかりませんでした'); EXIT;
        END IF;

/*  ヘッダワーク  */
        -- ヘッダの消費税更新（終了／伝票変更の場合）
        IF (SELECT_URIMEISAI%NOTFOUND OR (wHeaderKeys IS NOT NULL AND wHeaderKeys <> vRecordS06W003T.APKIHONCD || vRecordS06W003T.SHOPCD || vRecordS06W003T.URITORIKOMIKANRINO )) THEN
          -- ヘッダワークの消費税更新
          UpdateTaxHeaderWRK;
        END IF;

        -- 終了
        IF SELECT_URIMEISAI%NOTFOUND THEN EXIT;
        END IF;

        -- ヘッダ情報の初期取得と設定（開始／伝票変更の場合）
        IF (wHeaderKeys IS NULL OR wHeaderKeys <> vRecordS06W003T.APKIHONCD || vRecordS06W003T.SHOPCD || vRecordS06W003T.URITORIKOMIKANRINO ) THEN

          -- 税計算区分と他のヘッダ情報の取得
          SELECT
                 UHW.APKIHONCD          -- 更新／登録用：AP基本コード
               , UHW.SHOPCD             -- 更新／登録用：ショップコード
               , UHW.URITORIKOMIKANRINO -- 更新／登録用：売上取込管理番号
               , UHW.DNPYKBN -- 仕入判定用：伝票区分
               , UHW.DATSKSMOTOKBN      -- 登録用：データ作成元区分
               , UHW.URIDD              -- 税計算用：売上日
               -- 税計算用：税計算区分
               -- ■：1 Base)chokenkun 2017-08-09 卸対象外税修正
               , ( CASE WHEN TKM.TOKUISAKIKBN IN ('2', '3', '4', '5', '6') -- 卸対象
                        THEN '2' ELSE
               -- ■：2 Base)chokenkun 2017-08-09 卸対象外税修正
                       ( CASE WHEN SPM.TAXKEISANKBNURIKIRIKAEDD IS NULL OR UHW.URIDD >= SPM.TAXKEISANKBNURIKIRIKAEDD
                              THEN SPM.SHINTAXKEISANKBNURI
                              ELSE SPM.GENZTAXKEISANKBNURI
               -- ■：1 Base)chokenkun 2017-08-09 卸対象外税修正
                         END )
               -- ■：2 Base)chokenkun 2017-08-09 卸対象外税修正
                   END ) AS TAXKEISANKBN
               , UHW.SIIRESAKICD -- 税計算用：仕入先コード
               , UHW.DLVRYKBN    -- 構成商品税計算用：配達区分
          INTO
               vRecordS06W002T.APKIHONCD
             , vRecordS06W002T.SHOPCD
             , vRecordS06W002T.URITORIKOMIKANRINO
             , vRecordS06W002T.DNPYKBN
             , vRecordS06W002T.DATSKSMOTOKBN
             , vRecordS06W002T.URIDD
             , vRecordS06W002T.TAXKEISANKBN
             , vRecordS06W002T.SIIRESAKICD
             , vRecordS06W002T.DLVRYKBN
          FROM S06W002T UHW        -- 売上情報ヘッダワーク
           INNER JOIN S02M046T SPM -- ショップマスタ
           ON (SPM.APKIHONCD = UHW.APKIHONCD AND SPM.SHOPCD = UHW.SHOPCD)
          -- ■：1 Base)chokenkun 2017-08-09 卸対象外税修正
           INNER JOIN S02M013T TKM -- 得意先マスタ
           ON ( TKM.APKIHONCD = UHW.APKIHONCD
                AND TKM.SHOPCD = UHW.SHOPCD
                AND TKM.TOKUISAKICD = UHW.TOKUISAKICD
              )
          -- ■：2 Base)chokenkun 2017-08-09 卸対象外税修正
          WHERE UHW.APKIHONCD = vRecordS06W003T.APKIHONCD
            AND UHW.SHOPCD = vRecordS06W003T.SHOPCD
            AND UHW.PRCSIDNO = vRecordS06W003T.PRCSIDNO
            AND UHW.URITORIKOMIKANRINO = vRecordS06W003T.URITORIKOMIKANRINO
            AND UHW.YUKOKBN = '1'
            AND SPM.YUKOKBN = '1';

          -- 売上総額と消費税総額初期化
          vRecordS06W002T.URIKIN := 0;
          vRecordS06W002T.SHOHITAXKIN := 0;

          -- ヘッダキー設定
          wHeaderKeys := vRecordS06W002T.APKIHONCD || vRecordS06W002T.SHOPCD || vRecordS06W002T.URITORIKOMIKANRINO;
        END IF;

/*  明細ワーク  */
        -- 明細商品情報：税設定区分と税区分の取得
        SELECT
               SHV.TAXSETTEIKBN -- 税設定区分
             , ( CASE WHEN SHV.TAXSETTEIKBN = '1' THEN SHV.TAXKBN ELSE '1' END ) AS TAXKBN -- 税区分
        INTO wTaxsetteikbn, vRecordS06W003T.TAXKBN
        FROM S02V001T SHV -- 商品マスタビュー
        WHERE SHV.APKIHONCD = vRecordS06W003T.APKIHONCD
          AND SHV.SHOPCD    = vRecordS06W003T.SHOPCD
          AND SHV.SHNCD     = vRecordS06W003T.SHNCD
          AND SHV.KIHONSHNYUKOKBN = '1'
          AND SHV.SHOPYUKOKBN     = '1'
        ;

        -- 消費税計算
        getUriageTax(wTaxsetteikbn);
        -- 仕入消費税計算（仕入売上(21)と経費購入(05)の場合のみ）

        -- 明細ワークの消費税更新
        UpdateTaxMeisaiWRK;

        -- ヘッダワークの集計
        vRecordS06W002T.URIKIN := vRecordS06W002T.URIKIN + vRecordS06W003T.KIN;
        vRecordS06W002T.SHOHITAXKIN := vRecordS06W002T.SHOHITAXKIN + vRecordS06W003T.SHOHITAXKIN;

        -- セット商品展開：構成商品登録
        IF vRecordS06W003T.TANPINMEISAIFLG = '0' THEN
          wKoseiSumTaxkin := 0;
          wStshnTaxkin := vRecordS06W003T.SHOHITAXKIN;
          InsertKoseiShn(wKoseiSumTaxkin);

          -- 消費税調整
          IF wKoseiSumTaxkin <> wStshnTaxkin THEN
            InsertShohiTaxChosei(wStshnTaxkin - wKoseiSumTaxkin);
          END IF;

        END IF;

      END LOOP;
    CLOSE SELECT_URIMEISAI;

  END UpdateSyohizei;

  /*****************************************************************************/
  /* プロシージャ名   ：伝票番号更新                                           */
  /* 引数             ：なし                                                   */
  /*****************************************************************************/
  PROCEDURE UpdateDnpyNo IS
    -- 変数宣言
    -- 更新用情報抽出
    CURSOR SELECT_URIHEADER IS
      SELECT UHW.APKIHONCD -- AP基本コード
           , UHW.SHOPCD -- ショップコード
           , UHW.URITORIKOMIKANRINO -- 売上取込管理番号
      FROM S06W002T UHW -- 売上情報明細ワーク
      WHERE
            UHW.PRCSIDNO = vRecordS06W002T.PRCSIDNO -- 処理ID番号
        AND UHW.YUKOKBN = '1'                       -- 有効区分
      ORDER BY  UHW.APKIHONCD, UHW.SHOPCD
    ;
  BEGIN
    OPEN SELECT_URIHEADER;
      LOOP
        FETCH SELECT_URIHEADER
          INTO vRecordS06W002T.APKIHONCD, vRecordS06W002T.SHOPCD, vRecordS06W002T.URITORIKOMIKANRINO;

        -- データない場合
        IF SELECT_URIHEADER%ROWCOUNT = 0 THEN RAISE_APPLICATION_ERROR(-20013, 'ヘッダワークの有効データが見つかりませんでした'); EXIT;
        END IF;
        -- 終了
        IF SELECT_URIHEADER%NOTFOUND THEN EXIT;
        END IF;

        -- 採番
        Z1804B01.getSlipNoUriage(vRecordS06W002T.APKIHONCD, vRecordS06W002T.SHOPCD, 1, vRecordS06W002T.URIDNPYNO, vRecordS06W002T.URIDNPYNO);

        -- 更新：ヘッダワーク
        UPDATE S06W002T UHW
           SET
               UHW.URIDNPYNO = vRecordS06W002T.URIDNPYNO       -- 売上伝票番号
             , UHW.EDANO     = 0  -- 枝番号
             , UHW.KOSHAAPKIHONCD = vRecordS06W002T.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
             , UHW.KOSHASHOPCD    = vRecordS06W002T.KOSHASHOPCD    -- 更新者ショップコード
             , UHW.KOSHACD        = vRecordS06W002T.KOSHACD        -- 更新者コード
             , UHW.KSHNSYSTMYMD   = vRecordS06W002T.KSHNSYSTMYMD   -- 更新システム日付: システム日付
             , UHW.KSHNSYSTMJIKAN = vRecordS06W002T.KSHNSYSTMJIKAN -- 更新システム時間: システム時間
             , UHW.KSHNUNYOYMD    = vRecordS06W002T.KSHNUNYOYMD    -- 更新運用日付: システム管理マスタ.文字設定値
             , UHW.KSHNIPADRSE    = vRecordS06W002T.KSHNIPADRSE    -- 更新ＩＰアドレス: パラメータ．更新ＩＰアドレス
             , UHW.KSHNPGMID      = 'C1003B21'                     -- 更新プログラムＩＤ: C1003B21
         WHERE
               UHW.APKIHONCD = vRecordS06W002T.APKIHONCD
           AND UHW.SHOPCD = vRecordS06W002T.SHOPCD
           AND UHW.PRCSIDNO = vRecordS06W002T.PRCSIDNO
           AND UHW.URITORIKOMIKANRINO = vRecordS06W002T.URITORIKOMIKANRINO
           AND UHW.YUKOKBN = '1'
        ;

        -- 更新：明細ワーク
        UPDATE S06W003T UMW
           SET
               UMW.URIDNPYNO = vRecordS06W002T.URIDNPYNO       -- 売上伝票番号
             , UMW.EDANO     = 0  -- 枝番号
             , UMW.KOSHAAPKIHONCD = vRecordS06W002T.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
             , UMW.KOSHASHOPCD    = vRecordS06W002T.KOSHASHOPCD    -- 更新者ショップコード
             , UMW.KOSHACD        = vRecordS06W002T.KOSHACD        -- 更新者コード
             , UMW.KSHNSYSTMYMD   = vRecordS06W002T.KSHNSYSTMYMD   -- 更新システム日付: システム日付
             , UMW.KSHNSYSTMJIKAN = vRecordS06W002T.KSHNSYSTMJIKAN -- 更新システム時間: システム時間
             , UMW.KSHNUNYOYMD    = vRecordS06W002T.KSHNUNYOYMD    -- 更新運用日付: システム管理マスタ.文字設定値
             , UMW.KSHNIPADRSE    = vRecordS06W002T.KSHNIPADRSE    -- 更新ＩＰアドレス: パラメータ．更新ＩＰアドレス
             , UMW.KSHNPGMID      = 'C1003B21'                     -- 更新プログラムＩＤ: C1003B21
         WHERE
               UMW.APKIHONCD = vRecordS06W002T.APKIHONCD
           AND UMW.SHOPCD = vRecordS06W002T.SHOPCD
           AND UMW.PRCSIDNO = vRecordS06W002T.PRCSIDNO
           AND UMW.URITORIKOMIKANRINO = vRecordS06W002T.URITORIKOMIKANRINO
           AND UMW.YUKOKBN = '1'
        ;
      -- 更新件数
      S99_CHECK_LOG('C1003B21.WorkTRK', vRecordS06W002T.APKIHONCD, vRecordS06W002T.SHOPCD, '1', 'U', '1', 'S06W003T', SQL%ROWCOUNT, vRecordS06W002T.KOSHAAPKIHONCD, vRecordS06W002T.KOSHASHOPCD, vRecordS06W002T.KOSHACD, vRecordS06W002T.KSHNIPADRSE, 'C1003B21.WorkTRK' , '「伝票：' || vRecordS06W002T.URIDNPYNO || '」の明細の伝票番号更新' );

      END LOOP;
    CLOSE SELECT_URIHEADER;

  END UpdateDnpyNo;

  /*****************************************************************************/
  /* プロシージャ名   ：売上情報取込ワークの取込済更新                         */
  /* 引数             ：なし                                                   */
  /*****************************************************************************/
  PROCEDURE UpdateTorikomisumiUriWRK IS
  BEGIN
    UPDATE S06W004T UTW
       SET UTW.URIDATSKSFLG = '1'
     WHERE UTW.URIDATSKSFLG   = '0'
       AND UTW.TOSHAAPKIHONCD = vRecordS06W002T.KOSHAAPKIHONCD
       AND UTW.TOSHASHOPCD    = vRecordS06W002T.KOSHASHOPCD
       AND UTW.TOSHACD        = vRecordS06W002T.KOSHACD
       AND UTW.YUKOKBN = '1'
    ;
    -- 更新件数
    S99_CHECK_LOG('C1003B21.WorkTRK', '', '', '1', 'U', '1', 'S06W004T', SQL%ROWCOUNT, vRecordS06W002T.KOSHAAPKIHONCD, vRecordS06W002T.KOSHASHOPCD, vRecordS06W002T.KOSHACD, vRecordS06W002T.KSHNIPADRSE, 'C1003B21.WorkTRK' , '売上情報取込ワークの取込済更新' );

  END UpdateTorikomisumiUriWRK;

  /*****************************************************************************/
  /* プロシージャ名   ：請求内訳情報ワークの取込済更新                         */
  /* 引数             ：なし                                                   */
  /*****************************************************************************/
  PROCEDURE UpdateTorikomisumiSkyWRK IS
  BEGIN
    NULL;
  END UpdateTorikomisumiSkyWRK;

  /*****************************************************************************/
  /* プロシージャ名   ：売上情報ワーク登録                                     */
  /* 引数             ：iApkihoncd         ＡＰ基本コード                      */
  /*                  ：iShopcd            ショップコード                      */
  /*                  ：iDatsksmotokbn     データ作成元区分                    */
  /*                  ：iPrcsidno          処理ID番号                          */
  /*****************************************************************************/
  PROCEDURE UriageJohoWorkToroku(iApKihoncd     IN VARCHAR2,
                                 iShopcd        IN VARCHAR2,
                                 iDatsksmotokbn IN VARCHAR2,
                                 iPrcsidno      IN VARCHAR2) AS
  BEGIN
    -- 処理ID番号
    vRecordS06W002T.PRCSIDNO := iPrcsidno;

    -- 登録
    IF iDatsksmotokbn = '18' THEN
       S99_CHECK_LOG('C1003B21.WorkTRK', iApkihoncd, iShopcd, '1', 'S', '1', 'XXX', 0, vRecordS06W002T.KOSHAAPKIHONCD, vRecordS06W002T.KOSHASHOPCD, vRecordS06W002T.KOSHACD, vRecordS06W002T.KSHNIPADRSE, 'C1003B21.WorkTRK' , '01-売上情報取込ワークから登録[開始] ' );
       InsertFromUriWRK ; -- アクアクララ売上情報取込
       S99_CHECK_LOG('C1003B21.WorkTRK', iApkihoncd, iShopcd, '1', 'E', '1', 'XXX', 0, vRecordS06W002T.KOSHAAPKIHONCD, vRecordS06W002T.KOSHASHOPCD, vRecordS06W002T.KOSHACD, vRecordS06W002T.KSHNIPADRSE, 'C1003B21.WorkTRK' , '01-売上情報取込ワークから登録[完了] ' );
    END IF;
    IF iDatsksmotokbn NOT IN ('18', '19') THEN -- 無効データ作成元区分
      RAISE_APPLICATION_ERROR(-20014, 'パラメータ.データ作成元区分 が無効です');
    END IF;

    -- 消費税更新
    S99_CHECK_LOG('C1003B21.WorkTRK', iApkihoncd, iShopcd, '1', 'S', '1', 'XXX', 0, vRecordS06W002T.KOSHAAPKIHONCD, vRecordS06W002T.KOSHASHOPCD, vRecordS06W002T.KOSHACD, vRecordS06W002T.KSHNIPADRSE, 'C1003B21.WorkTRK' , '02-消費税更新[開始] ' );
    UpdateSyohizei;
    S99_CHECK_LOG('C1003B21.WorkTRK', iApkihoncd, iShopcd, '1', 'E', '1', 'XXX', 0, vRecordS06W002T.KOSHAAPKIHONCD, vRecordS06W002T.KOSHASHOPCD, vRecordS06W002T.KOSHACD, vRecordS06W002T.KSHNIPADRSE, 'C1003B21.WorkTRK' , '02-消費税更新[完了] ' );

    -- 伝票番号更新
    S99_CHECK_LOG('C1003B21.WorkTRK', iApkihoncd, iShopcd, '1', 'S', '1', 'XXX', 0, vRecordS06W002T.KOSHAAPKIHONCD, vRecordS06W002T.KOSHASHOPCD, vRecordS06W002T.KOSHACD, vRecordS06W002T.KSHNIPADRSE, 'C1003B21.WorkTRK' , '03-伝票番号更新[開始] ' );
    UpdateDnpyNo;
    S99_CHECK_LOG('C1003B21.WorkTRK', iApkihoncd, iShopcd, '1', 'E', '1', 'XXX', 0, vRecordS06W002T.KOSHAAPKIHONCD, vRecordS06W002T.KOSHASHOPCD, vRecordS06W002T.KOSHACD, vRecordS06W002T.KSHNIPADRSE, 'C1003B21.WorkTRK' , '03-伝票番号更新[完了] ' );

    -- 取込ワーク取込済更新
    IF iDatsksmotokbn = '18' THEN
       S99_CHECK_LOG('C1003B21.WorkTRK', iApkihoncd, iShopcd, '1', 'S', '1', 'XXX', 0, vRecordS06W002T.KOSHAAPKIHONCD, vRecordS06W002T.KOSHASHOPCD, vRecordS06W002T.KOSHACD, vRecordS06W002T.KSHNIPADRSE, 'C1003B21.WorkTRK' , '04-取込済更新[開始] ' );
       UpdateTorikomisumiUriWRK ; -- アクアクララ売上情報取込
       S99_CHECK_LOG('C1003B21.WorkTRK', iApkihoncd, iShopcd, '1', 'E', '1', 'XXX', 0, vRecordS06W002T.KOSHAAPKIHONCD, vRecordS06W002T.KOSHASHOPCD, vRecordS06W002T.KOSHACD, vRecordS06W002T.KSHNIPADRSE, 'C1003B21.WorkTRK' , '04-取込済更新[完了] ' );
    END IF;
    IF iDatsksmotokbn NOT IN ('18', '19') THEN -- 無効データ作成元区分
      RAISE_APPLICATION_ERROR(-20014, 'パラメータ.データ作成元区分 が無効です');
    END IF;

  EXCEPTION
    WHEN OTHERS THEN
      IF SQLCODE >= -20000 AND SQLCODE <= -29999 THEN
        RAISE_APPLICATION_ERROR(SQLCODE, SQLERRM);
      ELSE
        RAISE_APPLICATION_ERROR(-20718, SQLERRM);
      END IF;

  END UriageJohoWorkToroku;

  /*****************************************************************************/
  /* プロシージャ名   ：売上テーブル(ヘッダテーブル)登録                       */
  /* 引数             ：iDnpykbn     伝票区分                                  */
  /*****************************************************************************/
  PROCEDURE InsertUriage(iDnpykbn IN VARCHAR2,
                         iInvoicshikoymd IN VARCHAR2) AS
    wKaishuHohokbn       VARCHAR2(1); -- 回収方法区分
  BEGIN
    -- 請求先の回収方法区分の取得
    SELECT SKM.KAISHUHOHOKBN INTO wKaishuHohokbn
      FROM S02M017T SKM
     WHERE SKM.APKIHONCD = vRecordS06T001T.APKIHONCD
       AND SKM.SHOPCD = vRecordS06T001T.SHOPCD
       AND SKM.SKYSAKICD = vRecordS06T001T.SKYSAKICD
       AND SKM.YUKOKBN = '1'
    ;

    -- 回収方法区分を共通変数にて保持
    vZnKaishuHohokbn := wKaishuHohokbn;

    -- 売上テーブル登録
    INSERT INTO S06T001T
    VALUES
      ( vRecordS06T001T.APKIHONCD -- AP基本コード
      , vRecordS06T001T.SHOPCD    -- ショップコード
      , vRecordS06T001T.URIDNPYNO -- 売上伝票番号
      , vRecordS06T001T.EDANO     -- 枝番号
-- ■：1 FJ)Nishikawa 2017-10-16 次期ＡＣＣＩＳ項目追加対応
      , NULL -- 受注伝票番号
      , NULL -- 受注伝票枝番号
-- ■：2 FJ)Nishikawa 2017-10-16 次期ＡＣＣＩＳ項目追加対応
      , ( CASE WHEN iDnpykbn IN ('11', '21', '14') THEN '1' -- 取引区分：伝票区分が売上のみ(11)、仕入売上(21)、売上出庫(14)の場合は売上(1)
               WHEN iDnpykbn IN ('12', '05', '15') THEN '2' -- 取引区分：伝票区分が経費のみ(12)、経費購入(05)、経費出庫(15)の場合は経費(2)
          ELSE NULL END )
      , vRecordS06T001T.TOKUISAKICD -- 得意先コード
      , vRecordS06T001T.SIIRESAKICD -- 仕入先コード
      , vRecordS06T001T.NYUKAMOTOCD -- 入荷元コード
      , vRecordS06T001T.URIDD                  -- 売上日
      , NULL -- 受注日
      , vRecordS06T001T.URIKIN                 -- 売上金額
      -- 回収方法区分が"7" （代引き）の場合、売上テーブル.消費税を設定
      -- 回収方法区分が"7" （代引き）以外かつ売上日がインボイス施行日より前の場合、売上テーブル.消費税を設定
      -- 上記以外の場合は0を設定
      , CASE WHEN wKaishuHohokbn = '7' THEN vRecordS06T001T.SHOHITAXKIN
        ELSE
          CASE WHEN vRecordS06T001T.URIDD < iInvoicshikoymd THEN vRecordS06T001T.SHOHITAXKIN
          ELSE 0
          END
        END -- 消費税金額 インボイス対応
      -- 回収方法区分が"7" （代引き）の場合、0を設定
      -- 回収方法区分が"7" （代引き）以外かつ売上日がインボイス施行日より前の場合、0を設定
      -- 上記以外の場合は売上テーブル.消費税金額を設定
      , CASE WHEN wKaishuHohokbn = '7' THEN 0
        ELSE
          CASE WHEN vRecordS06T001T.URIDD < iInvoicshikoymd THEN 0
          ELSE vRecordS06T001T.SHOHITAXKIN
          END
        END -- 参考消費税 インボイス対応
      , NULL -- 受注担当者コード
      , vRecordS06T001T.DLVRYTNSHACD           -- 配達担当者コード
      , vRecordS06T001T.DLVRYKANKATSUAPKIHONCD -- 配達管轄ＡＰ基本コード
      , vRecordS06T001T.DLVRYKANKATSUSHOPCD    -- 配達管轄ショップコード
      , NULL -- 配達拠点コード
      , vRecordS06T001T.DLVRYKBN               -- 配達区分
      , NULL -- 配達会社コード
      , NULL -- 出荷元区分
      , vRecordS06T001T.EIGYOTNSHACD           -- 営業担当者コード
      , vRecordS06T001T.JISSEKITNSHACD         -- 実績担当者コード
      , vRecordS06T001T.SKYSAKICD              -- 請求先コード
      , NULL -- 請求締年月
      , NULL -- 請求締日
      , '00000000'                    -- 請求処理実施日：'00000000'
      , NULL -- 月次締年月
      , NULL -- 月次締日
      , '00000000'                    -- 月次処理実施日
      , vRecordS06T001T.URIDD         -- 売上締処理日
      , NULL                          -- 値引締年月
      , NULL                          -- 値引締日
      , '00000000'                    -- 値引処理実施日
      , vRecordS06T001T.DNPYBIKO      -- 伝票備考
      , NULL -- 伝票番号
      , '0'                           -- 請求書出力有無区分
      , '0'                           -- 請求処理実施区分
      , vRecordS06T001T.DATSKSMOTOKBN -- データ作成元区分
      , vRecordS06T001T.TAXKEISANKBN  -- 税計算区分
      , vRecordS06T001T.URIDNPYNO     -- 仕入伝票番号：売上伝票番号と同じ
      , NULL -- 入金伝票番号
      , NULL -- 入金金額
      , NULL -- 入金種別
      , '1' -- 有効区分
      , vRecordS06W002T.KOSHAAPKIHONCD -- 登録者ＡＰ基本コード
      , vRecordS06W002T.KOSHASHOPCD    -- 登録者ショップコード
      , vRecordS06W002T.KOSHACD        -- 登録者コード
      , vRecordS06W002T.KSHNSYSTMYMD   -- 登録システム日付
      , vRecordS06W002T.KSHNSYSTMJIKAN -- 登録システム時間
      , vRecordS06W002T.KSHNUNYOYMD    -- 登録運用日付
      , vRecordS06W002T.KSHNIPADRSE    -- 登録ＩＰアドレス
      , vRecordS06W002T.KSHNPGMID      -- 登録プログラムＩＤ
      , vRecordS06W002T.KOSHAAPKIHONCD -- 更新者AP基本コード
      , vRecordS06W002T.KOSHASHOPCD    -- 更新者ショップコード
      , vRecordS06W002T.KOSHACD        -- 更新者コード
      , vRecordS06W002T.KSHNSYSTMYMD   -- 更新システム日付
      , vRecordS06W002T.KSHNSYSTMJIKAN -- 更新システム時間
      , vRecordS06W002T.KSHNUNYOYMD    -- 更新運用日付
      , vRecordS06W002T.KSHNIPADRSE    -- 更新ＩＰアドレス
      , vRecordS06W002T.KSHNPGMID      -- 更新プログラムＩＤ
      );

  END InsertUriage;
  /*****************************************************************************/
  /* プロシージャ名   ：売上明細テーブル登録                                   */
  /* 引数             ：なし                                                   */
  /*****************************************************************************/
  PROCEDURE InsertUriageMeisaiTax(iUriDnpyMeisaiNo IN VARCHAR2,
                                  iTaxkbn IN VARCHAR2,
                                  iTaxKin IN NUMBER,
                                  iTokuisakikbn IN VARCHAR2,
                                  iJutakudlvrycstkbn IN VARCHAR2) AS
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
       SANKOUSHOHITAXKIN, --  参考消費税金額
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
       TOKUISAKIKBN, --  得意先区分
       JUTAKUDLVRYCSTKBN, -- 受託配達顧客区分
       ROYALTYSHIMEYMD, --  ロイヤリティ締め年月日
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
      (vZnRecordS06T002T.APKIHONCD, --  ＡＰ基本コード
       vZnRecordS06T002T.SHOPCD, --  ショップコード
       vZnRecordS06T002T.URIDNPYNO, --  売上伝票番号
       '0', --  枝番号
       iUriDnpyMeisaiNo, --  売上伝票明細番号
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
       iTaxKin, --  消費税金額
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
       CASE -- 受託配達顧客区分
         WHEN iJutakudlvrycstkbn = '4' AND iTokuisakikbn = '1' THEN '1'
         ELSE iJutakudlvrycstkbn
       END,
       '00000000', -- ロイヤリティ締め年月日
       '1', --  有効区分
       vZnRecordS06T002T.KOSHAAPKIHONCD, -- 登録者ＡＰ基本コード
       vZnRecordS06T002T.KOSHASHOPCD, -- 登録者ショップコード
       vZnRecordS06T002T.KOSHACD, -- 登録者コード
       vZnRecordS06T002T.KSHNSYSTMYMD, -- 登録システム日付
       vZnRecordS06T002T.KSHNSYSTMJIKAN, -- 登録システム時間
       vZnRecordS06T002T.KSHNUNYOYMD, -- 登録運用日付
       vZnRecordS06T002T.KSHNIPADRSE, -- 登録ＩＰアドレス
       vZnRecordS06T002T.KSHNPGMID, -- 登録プログラムＩＤ
       vZnRecordS06T002T.KOSHAAPKIHONCD, -- 更新者ＡＰ基本コード
       vZnRecordS06T002T.KOSHASHOPCD, -- 更新者ショップコード
       vZnRecordS06T002T.KOSHACD, -- 更新者コード
       vZnRecordS06T002T.KSHNSYSTMYMD, -- 更新システム日付
       vZnRecordS06T002T.KSHNSYSTMJIKAN, -- 更新システム時間
       vZnRecordS06T002T.KSHNUNYOYMD, -- 更新運用日付
       vZnRecordS06T002T.KSHNIPADRSE, -- 更新ＩＰアドレス
       vZnRecordS06T002T.KSHNPGMID); -- 更新プログラムＩＤ
  END InsertUriageMeisaiTax;
  /*****************************************************************************/
  /* プロシージャ名   ：売上テーブル更新（消費税更新）                         */
  /* 引数             ：なし                                                   */
  /*****************************************************************************/
  PROCEDURE UpdateUriage(iTotalTaxKin IN NUMBER) AS
  BEGIN


    UPDATE S06T001T URT
       SET
           URT.SHOHITAXKIN = iTotalTaxKin                      -- 消費税金額
         , URT.SANKOUSHOHITAXKIN = 0                           -- 参考消費税金額
         , URT.KOSHAAPKIHONCD = vRecordS06W002T.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
         , URT.KOSHASHOPCD    = vRecordS06W002T.KOSHASHOPCD    -- 更新者ショップコード
         , URT.KOSHACD        = vRecordS06W002T.KOSHACD        -- 更新者コード
         , URT.KSHNSYSTMYMD   = vRecordS06W002T.KSHNSYSTMYMD   -- 更新システム日付: システム日付
         , URT.KSHNSYSTMJIKAN = vRecordS06W002T.KSHNSYSTMJIKAN -- 更新システム時間: システム時間
         , URT.KSHNUNYOYMD    = vRecordS06W002T.KSHNUNYOYMD    -- 更新運用日付: システム管理マスタ.文字設定値
         , URT.KSHNIPADRSE    = vRecordS06W002T.KSHNIPADRSE    -- 更新ＩＰアドレス: パラメータ．更新ＩＰアドレス
         , URT.KSHNPGMID      = 'C1003B21'                     -- 更新プログラムＩＤ: C1003B21
     WHERE
           URT.APKIHONCD = vZnRecordS06T002T.APKIHONCD
       AND URT.SHOPCD = vZnRecordS06T002T.SHOPCD
       AND URT.URIDNPYNO = vZnRecordS06T002T.URIDNPYNO
     ;

      -- 売上テーブル更新時の結果 退避
      vZnRecordS06T001T := NULL;
      SELECT * INTO vZnRecordS06T001T
        FROM S06T001T URT
        WHERE URT.APKIHONCD = vZnRecordS06T002T.APKIHONCD -- 前レコード.ＡＰ基本コード
        AND URT.SHOPCD      = vZnRecordS06T002T.SHOPCD    -- 前レコード.ショップコード
        AND URT.URIDNPYNO   = vZnRecordS06T002T.URIDNPYNO -- 前レコード.売上伝票番号
      ;

  END UpdateUriage;
  /*****************************************************************************/
  /* プロシージャ名   ：消費税情報登録                                         */
  /* 引数             ：iZnUridd           前レコードの売上日                  */
  /*                  ：iTokuisakikbn      前レコードの得意先区分              */
  /*                  ：iJutakudlvrycstkbn 前レコードの受託配達顧客区分        */
  /*                  ：iZnSkysakicd       前レコードの請求先コード            */
  /*                  ：iZnKaishuHohokbn   前レコードの回収方法区分            */
  /*                  ：iZnRecordS06T001T  前レコードの売上情報                */
  /*                  ：iZnRecordS06T002T  前レコードの売上明細情報            */
  /*                  ：※前レコード：ヘッダキーの切り替わる前のレコード情報   */
  /*****************************************************************************/
  PROCEDURE TaxToroku(iZnApKihoncd IN VARCHAR2,
                      iZnShopcd IN VARCHAR2,
                      iZnUridd IN VARCHAR2,
                      iTokuisakikbn IN VARCHAR2,
                      iJutakudlvrycstkbn IN VARCHAR2,
                      iZnSkysakicd IN VARCHAR2,
                      iZnKaishuHohokbn IN VARCHAR2,
                      iZnRecordS06T001T IN S06T001T%rowtype,
                      iZnRecordS06T002T IN S06T002T%rowtype) AS
    wKazeiKin         NUMBER; -- 課税の金額
    wKazeiKeigenKin   NUMBER; -- 課税軽減の金額
    wTotalTaxKin      NUMBER; -- 消費税合計
    wTaxkin           NUMBER; -- 課税の消費税
    wTaxkinKeigen     NUMBER; -- 課税軽減の消費税
    wUriDnpyMeisaiNo  VARCHAR(11); -- 売上伝票明細番号
    wCntFLG           BOOLEAN; -- カウントフラグ

  BEGIN

    -- 消費税情報登録
    -- 回収方法が代引(7)の場合かつ前回登録売上明細情報が存在する場合
    IF iZnKaishuHohokbn = '7' AND vZnRecordS06T002T.APKIHONCD IS NOT NULL THEN
      -- 各消費税料金の取得（課税の金額合計）
      SELECT SUM(KIN) INTO wKazeiKin
      FROM S06T002T URM
      WHERE URM.APKIHONCD = vZnRecordS06T002T.APKIHONCD
            AND URM.SHOPCD = vZnRecordS06T002T.SHOPCD
            AND URM.URIDNPYNO = vZnRecordS06T002T.URIDNPYNO
            AND URM.NYURYOKUMEISAIFLG = '1'
            AND URM.TAXKBN = '1'
      ;
      -- 各消費税料金の取得（課税軽減の金額合計）
      SELECT SUM(KIN) INTO wKazeiKeigenKin
      FROM S06T002T URM
      WHERE URM.APKIHONCD = vZnRecordS06T002T.APKIHONCD
            AND URM.SHOPCD = vZnRecordS06T002T.SHOPCD
            AND URM.URIDNPYNO = vZnRecordS06T002T.URIDNPYNO
            AND URM.NYURYOKUMEISAIFLG = '1'
            AND URM.TAXKBN = '4'
      ;

      -- 消費税計算（税計算処理呼び出し）
      wTaxkin := Z1808B01.SyohizeiKeisan('1',          --単体計算フラグ（する）
                                         '1',          --税設定区分
                                         '1',          --税区分：課税
                                         '2',          --税計算区分：外税
                                         iZnUridd,     --切り替え日：売上テーブルの売上日
                                         iZnApKihoncd, --AP基本コード
                                         iZnShopcd,    --ショップコード
                                         NULL,         --仕入先コード
                                         NULL,         --数量
                                         wKazeiKin);   --金額合計(課税の金額合計)

      -- 消費税計算（税計算処理呼び出し）
      wTaxkinKeigen := Z1808B01.SyohizeiKeisan('1',        --単体計算フラグ（する）
                                         '1',              --税設定区分
                                         '4',              --税区分：課税
                                         '2',              --税計算区分：外税
                                         iZnUridd,         --切り替え日：売上テーブルの売上日
                                         iZnApKihoncd,     --AP基本コード
                                         iZnShopcd,        --ショップコード
                                         NULL,             --仕入先コード
                                         NULL,             --数量
                                         wKazeiKeigenKin); --金額合計(課税軽減の金額合計)

      -- 課税金額と課税軽減消費税金額の合計値を算出
      wTotalTaxKin := wTaxkin + wTaxkinKeigen;

      -- 売上伝票明細番号カウント用
      wUriDnpyMeisaiNo := vZnRecordS06T002T.URIDNPYMEISAINO;

      -- 課税料金が0以外の場合、消費税率10%の消費税金額の情報を売上明細テーブルに登録する
      IF NOT(wTaxkin = 0) THEN
        wUriDnpyMeisaiNo := wUriDnpyMeisaiNo + 1;
        InsertUriageMeisaiTax(wUriDnpyMeisaiNo,'1',wTaxkin,iTokuisakikbn,iJutakudlvrycstkbn);
      END IF;

      -- 課税軽減料金が0以外の場合、消費税率8%の消費税金額の情報を売上明細テーブルに登録する
      IF NOT(wTaxkinKeigen = 0) THEN
        wUriDnpyMeisaiNo := wUriDnpyMeisaiNo + 1;
        InsertUriageMeisaiTax(wUriDnpyMeisaiNo,'4',wTaxkinKeigen,iTokuisakikbn,iJutakudlvrycstkbn);
      END IF;

      --売上テーブル更新
      UpdateUriage(wTotalTaxKin);

    END IF;

  END TaxToroku;
  /*****************************************************************************/
  /* プロシージャ名   ：売上明細テーブル登録                                   */
  /* 引数             ：なし                                                   */
  /*****************************************************************************/
  PROCEDURE InsertUriageMeisai(iInvoicshikoymd IN VARCHAR2,
                                iTokuisakikbn IN VARCHAR2,
                                iJutakudlvrycstkbn IN VARCHAR2)AS
    wHyojunPriceKirikaeDD           VARCHAR2(8); -- 標準価格切替日
    wGenzHyojunDlvryPrice           NUMBER(10,2); -- 現標準配達価格
    wShinHyojunDlvryPrice           NUMBER(10,2); -- 新標準配達価格
    wGenzHyojunMochikaeriPrice      NUMBER(10,2); -- 現標準持ち帰り価格
    wShinHyojunMochikaeriPrice      NUMBER(10,2); -- 新標準持ち帰り価格
  BEGIN
    -- 商品情報の取得
    SELECT SHV.LCTGRYCD, SHV.MCTGRYCD, SHV.SCTGRYCD
         , SHV.HYOJUNPRICEKIRIKAEDD
         , SHV.GENZHYOJUNPRICE
         , SHV.SHINHYOJUNPRICE
         , SHV.GENZMOCHIKAERIPRICE
         , SHV.SHINMOCHIKAERIPRICE
      INTO vRecordS06T002T.LCTGRYCD, vRecordS06T002T.MCTGRYCD, vRecordS06T002T.SCTGRYCD
         , wHyojunPriceKirikaeDD      -- 標準価格切替日
         , wGenzHyojunDlvryPrice      -- 現標準配達価格
         , wShinHyojunDlvryPrice      -- 新標準配達価格
         , wGenzHyojunMochikaeriPrice -- 現標準持ち帰り価格
         , wShinHyojunMochikaeriPrice -- 新標準持ち帰り価格
      FROM S02V001T SHV
     WHERE SHV.APKIHONCD = vRecordS06T001T.APKIHONCD
       AND SHV.SHOPCD = vRecordS06T001T.SHOPCD
       AND SHV.SHNCD = vRecordS06T002T.SHNCD
       AND SHV.KIHONSHNYUKOKBN = '1'
       AND SHV.SHOPYUKOKBN = '1'
    ;

    -- 在庫更新フラグの取得
    IF vRecordS06T001T.URIDD > vRecordS06W002T.KSHNUNYOYMD THEN vRecordS06T002T.ZAIKOKSHNSUMIFLG := '0'; -- 在庫更新済フラグ：売上日が運用日付より新しい場合は'0'
    ELSE vRecordS06T002T.ZAIKOKSHNSUMIFLG := '1';                                                        -- 在庫更新済フラグ：売上日が運用日付より旧い場合は'1'
    END IF;

    -- 登録
    INSERT INTO S06T002T
    VALUES
      ( vRecordS06T001T.APKIHONCD -- ヘッダ.ＡＰ基本コード
      , vRecordS06T001T.SHOPCD    -- ヘッダ.ショップコード
      , vRecordS06T001T.URIDNPYNO -- ヘッダ.売上伝票番号
      , vRecordS06T001T.EDANO     -- ヘッダ.枝番号
      , vRecordS06T002T.URIDNPYMEISAINO    -- 売上伝票明細番号
      , vRecordS06T002T.URIDNPYMEISAIEDANO -- 売上伝票明細枝番号
      , vRecordS06T002T.LCTGRYCD           -- 大分類コード
      , vRecordS06T002T.MCTGRYCD           -- 中分類コード
      , vRecordS06T002T.SCTGRYCD           -- 小分類コード
      , vRecordS06T002T.SHNCD              -- 商品コード
      , vRecordS06T002T.KEIJOKBN           -- 計上区分
      , vRecordS06T002T.SURYO              -- 数量
      , ( CASE WHEN wHyojunPriceKirikaeDD IS NULL OR vRecordS06T001T.URIDD >= wHyojunPriceKirikaeDD THEN -- 売上日が標準価格切替日より新しい場合：新単価
               ( CASE WHEN vRecordS06T001T.DLVRYKBN = '3' THEN wShinHyojunMochikaeriPrice                -- 配達区分が持ち帰り（３）の場合：新持ち帰り単価
                 ELSE wShinHyojunDlvryPrice END )                                                        -- その他の場合：新配達単価
          ELSE                                                                                           -- 売上日が標準価格切替日より古い場合：現単価
               ( CASE WHEN vRecordS06T001T.DLVRYKBN = '3' THEN wGenzHyojunMochikaeriPrice                -- 配達区分が持ち帰り（３）の場合：新持ち帰り単価
                 ELSE wGenzHyojunDlvryPrice END )                                                        -- その他の場合：新配達単価
        END )
      , vRecordS06T002T.UNT                -- 単価
      , vRecordS06T002T.KIN                -- 金額
      , vRecordS06T002T.UNTSHOHITAXKIN     -- 単価消費税金額
      -- 回収方法区分が"7" （代引き）の場合、0を設定
      -- 回収方法区分が"7" （代引き）以外かつ売上日がインボイス施行日より前の場合、売上テーブル.消費税金額を設定
      -- 上記以外の場合は0を設定
      , CASE WHEN vZnKaishuHohokbn = '7' THEN 0
        ELSE CASE WHEN vRecordS06T001T.URIDD < iInvoicshikoymd THEN vRecordS06T002T.SHOHITAXKIN
             ELSE 0
             END
        END -- 消費税金額 インボイス対応
      -- 回収方法区分が"7" （代引き）の場合、売上テーブル.消費税金額を設定
      -- 回収方法区分が"7" （代引き）以外かつ売上日がインボイス施行日より前の場合、0を設定
      -- 上記以外の場合、売上明細テーブル.入力明細フラグが'1'の場合は売上テーブル.消費税金額を設定し、それ以外は0を設定
      , CASE WHEN vZnKaishuHohokbn = '7' THEN
          CASE WHEN vRecordS06T001T.URIDD < iInvoicshikoymd THEN vRecordS06T002T.SHOHITAXKIN
          ELSE
              CASE WHEN vRecordS06T002T.NYURYOKUMEISAIFLG = '1' THEN vRecordS06T002T.SHOHITAXKIN
              ELSE 0
              END
          END
        ELSE
          CASE WHEN vRecordS06T001T.URIDD < iInvoicshikoymd THEN 0
          ELSE
            CASE WHEN vRecordS06T002T.NYURYOKUMEISAIFLG = '1' THEN vRecordS06T002T.SHOHITAXKIN
            ELSE 0
            END
          END
        END -- 参考消費税 インボイス対応
      , vRecordS06T002T.TAXKBN             -- 税区分
      , vRecordS06T002T.MEISAIKBN          -- 明細区分
      , vRecordS06T002T.NYURYOKUMEISAIFLG  -- 入力明細フラグ
      , vRecordS06T002T.TANPINMEISAIFLG    -- 単品明細フラグ
      , vRecordS06T002T.MEISAIBIKO         -- 明細備考
      , vRecordS06T002T.STSHNCD            -- セット商品コード
      , vRecordS06T002T.KATASIKI           -- 型式
      , 0                                  -- 仕入数量
      , 0                                  -- 仕入金額
      , 0                                  -- 仕入単価
      , 0                                  -- 仕入消費税金額
      , vRecordS06T002T.ZAIKOKSHNSUMIFLG   -- 在庫更新済フラグ
      , iTokuisakikbn                      -- 得意先区分
      , CASE -- 受託配達顧客区分
          WHEN iJutakudlvrycstkbn = '4' AND iTokuisakikbn = '1' THEN '1'
          ELSE iJutakudlvrycstkbn
        END
      , '00000000'                         -- ロイヤリティ締め年月日
      , '1' -- 有効区分
      , vRecordS06W002T.KOSHAAPKIHONCD -- 登録者ＡＰ基本コード
      , vRecordS06W002T.KOSHASHOPCD    -- 登録者ショップコード
      , vRecordS06W002T.KOSHACD        -- 登録者コード
      , vRecordS06W002T.KSHNSYSTMYMD   -- 登録システム日付
      , vRecordS06W002T.KSHNSYSTMJIKAN -- 登録システム時間
      , vRecordS06W002T.KSHNUNYOYMD    -- 登録運用日付
      , vRecordS06W002T.KSHNIPADRSE    -- 登録ＩＰアドレス
      , vRecordS06W002T.KSHNPGMID      -- 登録プログラムＩＤ
      , vRecordS06W002T.KOSHAAPKIHONCD -- 更新者AP基本コード
      , vRecordS06W002T.KOSHASHOPCD    -- 更新者ショップコード
      , vRecordS06W002T.KOSHACD        -- 更新者コード
      , vRecordS06W002T.KSHNSYSTMYMD   -- 更新システム日付
      , vRecordS06W002T.KSHNSYSTMJIKAN -- 更新システム時間
      , vRecordS06W002T.KSHNUNYOYMD    -- 更新運用日付
      , vRecordS06W002T.KSHNIPADRSE    -- 更新ＩＰアドレス
      , vRecordS06W002T.KSHNPGMID      -- 更新プログラムＩＤ
    );

      -- 売上明細テーブル登録時の結果 退避
      vZnRecordS06T002T := NULL;
      SELECT * INTO vZnRecordS06T002T
        FROM S06T002T URM
        WHERE URM.APKIHONCD = vRecordS06T001T.APKIHONCD -- ヘッダ.ＡＰ基本コード
        AND URM.SHOPCD = vRecordS06T001T.SHOPCD    -- ヘッダ.ショップコード
        AND URM.URIDNPYNO = vRecordS06T001T.URIDNPYNO -- ヘッダ.売上伝票番号
        AND URM.EDANO = vRecordS06T001T.EDANO     -- ヘッダ.枝番号
        AND URM.URIDNPYMEISAINO = vRecordS06T002T.URIDNPYMEISAINO    -- 売上伝票明細番号
        AND URM.URIDNPYMEISAIEDANO = vRecordS06T002T.URIDNPYMEISAIEDANO -- 売上伝票明細枝番号
      ;

  END InsertUriageMeisai;

  /*****************************************************************************/
  /* プロシージャ名   ：入出庫実績テーブル登録                                 */
  /* 引数             ：iNyukamotoJissekiTnshacd     入荷元実績担当者コード    */
  /*****************************************************************************/
  PROCEDURE InsertNyushukkoJisseki(iNyukamotoJissekiTnshacd IN VARCHAR2) AS
    wSiireKin                    NUMBER(11,0); -- 仕入金額
  BEGIN
    -- 仕入金額の合計
    SELECT SUM( NVL( UMW.SIIREKIN, 0) )
      INTO wSiireKin
      FROM S06W003T UMW
     WHERE UMW.APKIHONCD = vRecordS06T001T.APKIHONCD
       AND UMW.SHOPCD = vRecordS06T001T.SHOPCD
       AND UMW.URIDNPYNO = vRecordS06T001T.URIDNPYNO
       AND UMW.EDANO = vRecordS06T001T.EDANO
       AND UMW.YUKOKBN = '1'
    GROUP BY UMW.APKIHONCD, UMW.SHOPCD, UMW.URIDNPYNO, UMW.EDANO
    ;

    -- 登録
    INSERT INTO S11T003T
    VALUES
      ( vRecordS06T001T.APKIHONCD -- ＡＰ基本コード：売上テーブル.ＡＰ基本コード
      , vRecordS06T001T.SHOPCD    -- ショップコード：売上テーブル.ショップコード
      , vRecordS06T001T.URIDNPYNO -- 入出庫伝票番号：売上テーブル.売上伝票番号
      , '2' -- 入出庫区分：出庫(2)
      , '6' -- 取引区分：出庫(6)
      , NULL -- 入荷区分
      , vRecordS06T001T.SIIRESAKICD    -- 入庫先仕入コード：売上テーブル.仕入先コード
      , vRecordS06T001T.NYUKAMOTOCD    -- 入庫先倉庫コード：売上テーブル.入荷元コード(倉庫コード)
      , vRecordS06T001T.URIDD          -- 入出庫日：        売上テーブル.売上日
      , vRecordS06T001T.NYUKAMOTOCD    -- 出庫先倉庫コード：売上テーブル.入荷元コード(倉庫コード)
      , vRecordS06T001T.TOKUISAKICD    -- 出庫先経費コード：売上テーブル.得意先コード
      , iNyukamotoJissekiTnshacd       -- 実績担当者コード：入荷元実績担当者コード
      , wSiireKin  -- 金額
      , NULL -- 月次締年月
      , NULL -- 月次締日
      , '00000000' -- 月次処理実施日
      , vRecordS06T001T.DNPYBIKO           -- 伝票備考：売上テーブル.伝票備考
      , vRecordS06T001T.DATSKSMOTOKBN      -- データ作成元区分：売上テーブル.データ作成元区分
      , '1' -- 有効区分
      , vRecordS06W002T.KOSHAAPKIHONCD -- 登録者ＡＰ基本コード
      , vRecordS06W002T.KOSHASHOPCD    -- 登録者ショップコード
      , vRecordS06W002T.KOSHACD        -- 登録者コード
      , vRecordS06W002T.KSHNSYSTMYMD   -- 登録システム日付
      , vRecordS06W002T.KSHNSYSTMJIKAN -- 登録システム時間
      , vRecordS06W002T.KSHNUNYOYMD    -- 登録運用日付
      , vRecordS06W002T.KSHNIPADRSE    -- 登録ＩＰアドレス
      , vRecordS06W002T.KSHNPGMID      -- 登録プログラムＩＤ
      , vRecordS06W002T.KOSHAAPKIHONCD -- 更新者AP基本コード
      , vRecordS06W002T.KOSHASHOPCD    -- 更新者ショップコード
      , vRecordS06W002T.KOSHACD        -- 更新者コード
      , vRecordS06W002T.KSHNSYSTMYMD   -- 更新システム日付
      , vRecordS06W002T.KSHNSYSTMJIKAN -- 更新システム時間
      , vRecordS06W002T.KSHNUNYOYMD    -- 更新運用日付
      , vRecordS06W002T.KSHNIPADRSE    -- 更新ＩＰアドレス
      , vRecordS06W002T.KSHNPGMID      -- 更新プログラムＩＤ
    );
  END InsertNyushukkoJisseki;

  /*****************************************************************************/
  /* プロシージャ名   ：入出庫実績明細テーブル登録                             */
  /* 引数             ：なし                                                   */
  /*****************************************************************************/
  PROCEDURE InsertNyushukkoJissekiMeisai AS
  BEGIN
    INSERT INTO S11T004T
    VALUES
      ( vRecordS06T001T.APKIHONCD -- ＡＰ基本コード：売上テーブル.ＡＰ基本コード
      , vRecordS06T001T.SHOPCD    -- ショップコード：売上テーブル.ショップコード
      , vRecordS06T001T.URIDNPYNO -- 入出庫伝票番号：売上テーブル.売上伝票番号
      , vRecordS06T002T.URIDNPYMEISAINO    -- 入出庫伝票明細番号：売上明細テーブル.売上伝票明細番号
      , vRecordS06T002T.URIDNPYMEISAIEDANO -- 入出庫伝票明細枝番号：売上明細テーブル.売上伝票明細枝番号
      , '2' -- 入出庫区分：出庫(2)
      , vRecordS06T002T.LCTGRYCD -- 大分類コード：売上明細テーブル.大分類コード
      , vRecordS06T002T.MCTGRYCD -- 中分類コード：売上明細テーブル.中分類コード
      , vRecordS06T002T.SCTGRYCD -- 小分類コード：売上明細テーブル.小分類コード
      , vRecordS06T002T.SHNCD    -- 商品コード：  売上明細テーブル.商品コード
      , NVL( vRecordS06T002T.SURYO, 0 )      -- 数量：売上明細テーブル数量
      , NVL( vRecordS06T002T.SIIREUNT  , 0 ) -- 単価：売上明細テーブル仕入単価
      , NVL( vRecordS06T002T.SIIREKIN  , 0 ) -- 金額：売上明細テーブル仕入金額
      , vRecordS06T002T.MEISAIBIKO       -- 明細備考：        売上明細テーブル.明細備考
      , vRecordS06T002T.KATASIKI         -- 型式：            売上明細テーブル.型式
      , vRecordS06T002T.ZAIKOKSHNSUMIFLG -- 在庫更新済フラグ：売上明細テーブル.在庫更新済フラグ
      , '1' -- 有効区分
      , vRecordS06W002T.KOSHAAPKIHONCD -- 登録者ＡＰ基本コード
      , vRecordS06W002T.KOSHASHOPCD    -- 登録者ショップコード
      , vRecordS06W002T.KOSHACD        -- 登録者コード
      , vRecordS06W002T.KSHNSYSTMYMD   -- 登録システム日付
      , vRecordS06W002T.KSHNSYSTMJIKAN -- 登録システム時間
      , vRecordS06W002T.KSHNUNYOYMD    -- 登録運用日付
      , vRecordS06W002T.KSHNIPADRSE    -- 登録ＩＰアドレス
      , vRecordS06W002T.KSHNPGMID      -- 登録プログラムＩＤ
      , vRecordS06W002T.KOSHAAPKIHONCD -- 更新者AP基本コード
      , vRecordS06W002T.KOSHASHOPCD    -- 更新者ショップコード
      , vRecordS06W002T.KOSHACD        -- 更新者コード
      , vRecordS06W002T.KSHNSYSTMYMD   -- 更新システム日付
      , vRecordS06W002T.KSHNSYSTMJIKAN -- 更新システム時間
      , vRecordS06W002T.KSHNUNYOYMD    -- 更新運用日付
      , vRecordS06W002T.KSHNIPADRSE    -- 更新ＩＰアドレス
      , vRecordS06W002T.KSHNPGMID      -- 更新プログラムＩＤ
    );
  END InsertNyushukkoJissekiMeisai;

  /*****************************************************************************/
  /* プロシージャ名   ：在庫更新処理                                           */
  /* 引数             ：iDnpykbn     伝票区分                                  */
  /*****************************************************************************/
  PROCEDURE UpdateTableZaiko(iDnpykbn IN VARCHAR2) AS
    wZaikoKanriKbn                    VARCHAR2(1); -- 在庫管理区分（ショップ）
    wZaikoKanriTrgtflg                VARCHAR2(1); -- 在庫管理対象フラグ（商品）
    wCount                            NUMBER(2,0); -- テーブル項目数
    wZaikoPrcsDydm                    VARCHAR2(6); -- 在庫処理年月（ショップ）
    wZaikoSimeDD                      VARCHAR2(2); -- 締日
    wZaikoSimeDydm                    VARCHAR2(6); -- 締年月
  BEGIN

    -- 在庫更新用項目の取得
    SELECT SPM.ZAIKOKANRIKBN     -- 在庫管理区分（ショップ）
         , SHV.ZAIKOKANRITRGTFLG -- 在庫管理対象フラグ（商品）
         , SPM.ZAIKOPRCSDYDM -- 在庫処理年月
         , SPM.SHIMEDD       -- 締日
      INTO wZaikoKanriKbn, wZaikoKanriTrgtflg, wZaikoPrcsDydm, wZaikoSimeDD
      FROM S02V001T SHV
     INNER JOIN S02M046T SPM ON (SPM.APKIHONCD = SHV.APKIHONCD AND SPM.SHOPCD = SHV.SHOPCD)
     WHERE SHV.APKIHONCD = vRecordS06T001T.APKIHONCD
       AND SHV.SHOPCD = vRecordS06T001T.SHOPCD
       AND SHV.SHNCD = vRecordS06T002T.SHNCD
       AND SHV.KIHONSHNYUKOKBN = '1'
       AND SHV.SHOPYUKOKBN = '1'
       AND SPM.YUKOKBN = '1'
    ;

    -- 締年月の取得
    IF SUBSTR(vRecordS06T001T.URIDD, 7, 2) > wZaikoSimeDD THEN  -- 売上日＞締日の場合：締年月は売上月の次月
       wZaikoSimeDydm := TO_CHAR( ADD_MONTHS( SUBSTR(vRecordS06T001T.URIDD, 1,6), 1 ), 'YYYYMM' );
    ELSE wZaikoSimeDydm := SUBSTR(vRecordS06T001T.URIDD, 1,6); -- 売上日＜＝締日の場合：締年月は売上月当月
    END IF;
    IF wZaikoPrcsDydm >= wZaikoSimeDydm THEN wZaikoSimeDydm := wZaikoPrcsDydm; -- 在庫処理年月が新しい場合：締年月は在庫処理年月
    END IF;

    IF (wZaikoKanriKbn IS NOT NULL AND wZaikoKanriKbn <> '0') AND (wZaikoKanriTrgtflg IS NOT NULL AND wZaikoKanriTrgtflg <> '0') -- 在庫管理ある場合のみ実行する
    THEN IF vRecordS06T001T.URIDD <= vRecordS06W002T.KSHNUNYOYMD THEN  -- 売上日が運用日付より古い場合のみ実行する

            -- 現在庫テーブル更新／登録
            BEGIN
              -- 更新
              UPDATE S13T001T GZT
                 SET GZT.ZAIKOSURYO = GZT.ZAIKOSURYO + ( vRecordS06T002T.SURYO * (-1) ) -- 在庫数量：在庫数量＋数量*(-1)
                   , GZT.KOSHAAPKIHONCD = vRecordS06W002T.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
                   , GZT.KOSHASHOPCD    = vRecordS06W002T.KOSHASHOPCD    -- 更新者ショップコード
                   , GZT.KOSHACD        = vRecordS06W002T.KOSHACD        -- 更新者コード
                   , GZT.KSHNSYSTMYMD   = vRecordS06W002T.KSHNSYSTMYMD   -- 更新システム日付: システム日付
                   , GZT.KSHNSYSTMJIKAN = vRecordS06W002T.KSHNSYSTMJIKAN -- 更新システム時間: システム時間
                   , GZT.KSHNUNYOYMD    = vRecordS06W002T.KSHNUNYOYMD    -- 更新運用日付: システム管理マスタ.文字設定値
                   , GZT.KSHNIPADRSE    = vRecordS06W002T.KSHNIPADRSE    -- 更新ＩＰアドレス: パラメータ．更新ＩＰアドレス
                   , GZT.KSHNPGMID      = 'C1003B21'                     -- 更新プログラムＩＤ: C1003B21
               WHERE GZT.APKIHONCD = vRecordS06T001T.APKIHONCD
                 AND GZT.SHOPCD    = vRecordS06T001T.SHOPCD
                 AND GZT.SOKOCD    = vRecordS06T001T.NYUKAMOTOCD
                 AND GZT.SHNCD     = vRecordS06T002T.SHNCD
              ;

              -- 更新項目がないの場合：登録
              IF SQL%ROWCOUNT = 0 THEN
                 INSERT INTO S13T001T
                 VALUES
                   ( vRecordS06T001T.APKIHONCD   -- AP基本コード
                   , vRecordS06T001T.SHOPCD      -- ショップコード
                   , vRecordS06T001T.NYUKAMOTOCD -- 倉庫コード：入荷元コード
                   , vRecordS06T002T.SHNCD       -- 商品コード
                   , vRecordS06T002T.SURYO * (-1) -- 数量：売上出庫と経費出庫の場合は数量＊(-1)
                   , '1' -- 有効区分
                   , vRecordS06W002T.KOSHAAPKIHONCD -- 登録者ＡＰ基本コード
                   , vRecordS06W002T.KOSHASHOPCD    -- 登録者ショップコード
                   , vRecordS06W002T.KOSHACD        -- 登録者コード
                   , vRecordS06W002T.KSHNSYSTMYMD   -- 登録システム日付
                   , vRecordS06W002T.KSHNSYSTMJIKAN -- 登録システム時間
                   , vRecordS06W002T.KSHNUNYOYMD    -- 登録運用日付
                   , vRecordS06W002T.KSHNIPADRSE    -- 登録ＩＰアドレス
                   , vRecordS06W002T.KSHNPGMID      -- 登録プログラムＩＤ
                   , vRecordS06W002T.KOSHAAPKIHONCD -- 更新者AP基本コード
                   , vRecordS06W002T.KOSHASHOPCD    -- 更新者ショップコード
                   , vRecordS06W002T.KOSHACD        -- 更新者コード
                   , vRecordS06W002T.KSHNSYSTMYMD   -- 更新システム日付
                   , vRecordS06W002T.KSHNSYSTMJIKAN -- 更新システム時間
                   , vRecordS06W002T.KSHNUNYOYMD    -- 更新運用日付
                   , vRecordS06W002T.KSHNIPADRSE    -- 更新ＩＰアドレス
                   , vRecordS06W002T.KSHNPGMID      -- 更新プログラムＩＤ
                 );
              END IF;
            END ;

            -- 在庫テーブル更新
            BEGIN
              -- 更新
              UPDATE S13T002T ZKT
                 SET ZKT.TOTSUKIURIKAZU         = ( CASE WHEN iDnpykbn = '14' THEN ZKT.TOTSUKIURIKAZU + vRecordS06T002T.SURYO
                                                    ELSE ZKT.TOTSUKIURIKAZU + 0 END )                      -- 当月売上数(売上出庫(14)の場合)：当月売上数＋数量
                   , ZKT.TOTSUKIKEIHISHUKKOKAZU = ( CASE WHEN iDnpykbn = '15' THEN ZKT.TOTSUKIKEIHISHUKKOKAZU + vRecordS06T002T.SURYO
                                                    ELSE ZKT.TOTSUKIKEIHISHUKKOKAZU + 0 END )              -- 当月経費出庫数(経費出庫(15)の場合)：当月経費出庫数＋数量
                   , ZKT.TOTSUKIMATSUZAIKOKAZU  = TOTSUKIMATSUZAIKOKAZU + ( vRecordS06T002T.SURYO * (-1) ) -- 当月末在庫数：当月末在庫数＋数量*(-1)
                   , ZKT.KOSHAAPKIHONCD = vRecordS06W002T.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
                   , ZKT.KOSHASHOPCD    = vRecordS06W002T.KOSHASHOPCD    -- 更新者ショップコード
                   , ZKT.KOSHACD        = vRecordS06W002T.KOSHACD        -- 更新者コード
                   , ZKT.KSHNSYSTMYMD   = vRecordS06W002T.KSHNSYSTMYMD   -- 更新システム日付: システム日付
                   , ZKT.KSHNSYSTMJIKAN = vRecordS06W002T.KSHNSYSTMJIKAN -- 更新システム時間: システム時間
                   , ZKT.KSHNUNYOYMD    = vRecordS06W002T.KSHNUNYOYMD    -- 更新運用日付: システム管理マスタ.文字設定値
                   , ZKT.KSHNIPADRSE    = vRecordS06W002T.KSHNIPADRSE    -- 更新ＩＰアドレス: パラメータ．更新ＩＰアドレス
                   , ZKT.KSHNPGMID      = 'C1003B21'                     -- 更新プログラムＩＤ: C1003B21
               WHERE ZKT.APKIHONCD = vRecordS06T001T.APKIHONCD
                 AND ZKT.SHOPCD    = vRecordS06T001T.SHOPCD
                 AND ZKT.SOKOCD    = vRecordS06T001T.NYUKAMOTOCD
                 AND ZKT.SHNCD     = vRecordS06T002T.SHNCD
                 AND ZKT.SHIMEDYDM = wZaikoSimeDydm
              ;
              -- 更新項目がないの場合：登録
              IF SQL%ROWCOUNT = 0 THEN
                 INSERT INTO S13T002T
                 VALUES
                   ( vRecordS06T001T.APKIHONCD   -- AP基本コード
                   , vRecordS06T001T.SHOPCD      -- ショップコード
                   , vRecordS06T001T.NYUKAMOTOCD -- 倉庫コード：入荷元コード
                   , vRecordS06T002T.SHNCD       -- 商品コード
                   , wZaikoSimeDydm              -- 締年月
                   , 0    -- 前月末在庫数
                   , 0    -- 当月仕入数
                   , 0    -- 当月仕入金額
                   , 0    -- 当月製造数
                   , 0    -- 当月調整入庫数
                   , 0    -- 当月その他入庫数
                   , ( CASE WHEN iDnpykbn = '14' THEN vRecordS06T002T.SURYO
                       ELSE 0 END )             -- 当月売上数(売上出庫(14)の場合)：数量
                   , 0    -- 当月調整出庫数
                   , ( CASE WHEN iDnpykbn = '15' THEN vRecordS06T002T.SURYO
                       ELSE 0 END )             -- 当月経費出庫数(経費出庫(15)の場合)：当月経費出庫数＋数量
                   , 0    -- 当月その他出庫数
                   , vRecordS06T002T.SURYO*(-1) -- 当月末在庫数：数量*(-1)
                   , NULL -- 当月棚卸数量
                   , NULL -- 当月過不足数量
                   , 0    -- 在庫単価
                   , '0'  -- 締フラグ
                   , '1'  -- 有効区分
                   , vRecordS06W002T.KOSHAAPKIHONCD -- 登録者ＡＰ基本コード
                   , vRecordS06W002T.KOSHASHOPCD    -- 登録者ショップコード
                   , vRecordS06W002T.KOSHACD        -- 登録者コード
                   , vRecordS06W002T.KSHNSYSTMYMD   -- 登録システム日付
                   , vRecordS06W002T.KSHNSYSTMJIKAN -- 登録システム時間
                   , vRecordS06W002T.KSHNUNYOYMD    -- 登録運用日付
                   , vRecordS06W002T.KSHNIPADRSE    -- 登録ＩＰアドレス
                   , vRecordS06W002T.KSHNPGMID      -- 登録プログラムＩＤ
                   , vRecordS06W002T.KOSHAAPKIHONCD -- 更新者AP基本コード
                   , vRecordS06W002T.KOSHASHOPCD    -- 更新者ショップコード
                   , vRecordS06W002T.KOSHACD        -- 更新者コード
                   , vRecordS06W002T.KSHNSYSTMYMD   -- 更新システム日付
                   , vRecordS06W002T.KSHNSYSTMJIKAN -- 更新システム時間
                   , vRecordS06W002T.KSHNUNYOYMD    -- 更新運用日付
                   , vRecordS06W002T.KSHNIPADRSE    -- 更新ＩＰアドレス
                   , vRecordS06W002T.KSHNPGMID      -- 更新プログラムＩＤ
                 );
              END IF;
            END ;

            -- 在庫入出庫明細登録
            BEGIN
              -- 連番の取得
              SELECT COUNT(*)
                INTO wCount
                FROM S13T003T ZMT
               WHERE ZMT.APKIHONCD   = vRecordS06T001T.APKIHONCD
                 AND ZMT.SHOPCD      = vRecordS06T001T.SHOPCD
                 AND ZMT.SOKOCD      = vRecordS06T001T.NYUKAMOTOCD
                 AND ZMT.SHNCD       = vRecordS06T002T.SHNCD
                 AND ZMT.NYUSHUKKODD = vRecordS06T001T.URIDD
                 AND ZMT.DNPYNO      = vRecordS06T001T.DNPYNO
                 AND ZMT.DNPYGYONO   = vRecordS06T002T.URIDNPYMEISAINO
              ;
              -- 登録
              INSERT INTO S13T003T
              VALUES
                ( vRecordS06T001T.APKIHONCD   -- AP基本コード
                , vRecordS06T001T.SHOPCD      -- ショップコード
                , vRecordS06T001T.NYUKAMOTOCD -- 倉庫コード：入荷元コード
                , vRecordS06T002T.SHNCD       -- 商品コード
                , wZaikoSimeDydm              -- 締年月
                , vRecordS06T001T.URIDD       -- 入出庫日：売上日
                , vRecordS06T001T.URIDNPYNO   -- 伝票番号：売上伝票番号
                , vRecordS06T001T.EDANO       -- 枝番号
                , vRecordS06T002T.URIDNPYMEISAINO  -- 伝票行番号：売上伝票明細番号
                , wCount -- 連番
                , iDnpykbn -- 伝票区分
                , ( CASE WHEN iDnpykbn = '14' THEN '02' -- 入出庫種別：売上出庫(14)の場合は'02'
                         WHEN iDnpykbn = '15' THEN '05' -- 入出庫種別：経費出庫(15)の場合は'05'
                       ELSE '0' END )
                , '2' -- 入出庫区分
                , vRecordS06T001T.APKIHONCD   -- 入出庫先ＡＰ基本コード：AP基本コード
                , vRecordS06T001T.SHOPCD      -- 入出庫先ショップコード：ショップコード
                , vRecordS06T001T.TOKUISAKICD -- 入出庫先得意先コード：  得意先コード
                , NULL -- 入出庫先倉庫コード
                , NULL -- 入庫元プラントコード
                , NULL -- 入庫元仕入先コード
                , 0    -- 入庫数量
                , vRecordS06T002T.SURYO -- 出庫数量：数量
                , vRecordS06T001T.DATSKSMOTOKBN -- データ作成元区分
                , '1'  -- 有効区分
                , vRecordS06W002T.KOSHAAPKIHONCD -- 登録者ＡＰ基本コード
                , vRecordS06W002T.KOSHASHOPCD    -- 登録者ショップコード
                , vRecordS06W002T.KOSHACD        -- 登録者コード
                , vRecordS06W002T.KSHNSYSTMYMD   -- 登録システム日付
                , vRecordS06W002T.KSHNSYSTMJIKAN -- 登録システム時間
                , vRecordS06W002T.KSHNUNYOYMD    -- 登録運用日付
                , vRecordS06W002T.KSHNIPADRSE    -- 登録ＩＰアドレス
                , vRecordS06W002T.KSHNPGMID      -- 登録プログラムＩＤ
                , vRecordS06W002T.KOSHAAPKIHONCD -- 更新者AP基本コード
                , vRecordS06W002T.KOSHASHOPCD    -- 更新者ショップコード
                , vRecordS06W002T.KOSHACD        -- 更新者コード
                , vRecordS06W002T.KSHNSYSTMYMD   -- 更新システム日付
                , vRecordS06W002T.KSHNSYSTMJIKAN -- 更新システム時間
                , vRecordS06W002T.KSHNUNYOYMD    -- 更新運用日付
                , vRecordS06W002T.KSHNIPADRSE    -- 更新ＩＰアドレス
                , vRecordS06W002T.KSHNPGMID      -- 更新プログラムＩＤ
              );
            END ;

         END IF;
    END IF;
  END UpdateTableZaiko;

  /*****************************************************************************/
  /* プロシージャ名   ：得意先更新処理                                         */
  /* 引数             ：iPrcsidno          処理ID番号                          */
  /*****************************************************************************/
  PROCEDURE UpdateTokuisaki(iPrcsidno      IN VARCHAR2) AS
    -- 変数宣言
    wApkihoncd                    VARCHAR2(3);  -- AP基本コード
    wShopcd                       VARCHAR2(3);  -- ショップコード
    wTokuisakicd                  VARCHAR2(6);  -- 得意先コード
    wUrikin                       NUMBER(11,0); -- 売上金額（得意先別）
    wShohiTaxkin                  NUMBER(11,0); -- 消費税金額（得意先別）
    wTokuisakiJotai               VARCHAR2(1);  -- 得意先状態
    wGenzaiZd                     NUMBER(8,0);  -- 現在残高
    -- 更新用得意先別情報抽出
    CURSOR SELECT_URIJOHO IS
      SELECT UHW.APKIHONCD   -- AP基本コード
           , UHW.SHOPCD      -- ショップコード
           , UHW.TOKUISAKICD -- 得意先コード
           , SUM(URT.URIKIN)      -- 売上金額の合計
           , SUM(URT.SHOHITAXKIN) -- 消費税金額の合計
           , TSM.TOKUISAKIJOTAI   -- 得意先状態
      FROM S06W002T UHW
      INNER JOIN S02M013T TSM ON ( TSM.APKIHONCD = UHW.APKIHONCD AND TSM.SHOPCD = UHW.SHOPCD AND TSM.TOKUISAKICD = UHW.TOKUISAKICD )
      INNER JOIN S06T001T URT ON ( URT.APKIHONCD = UHW.APKIHONCD AND URT.SHOPCD = UHW.SHOPCD AND URT.URIDNPYNO = UHW.URIDNPYNO )
      WHERE UHW.PRCSIDNO = iPrcsidno -- 処理ID番号
        AND UHW.URIDATSKSFLG = '0'   -- 売上データ未作成
        AND UHW.YUKOKBN = '1'        -- 有効区分(ヘッダ)
        AND TSM.YUKOKBN = '1'        -- 有効区分(得意先)
      GROUP BY UHW.APKIHONCD, UHW.SHOPCD, UHW.TOKUISAKICD, TSM.TOKUISAKIJOTAI, URT.URIKIN, URT.SHOHITAXKIN
      ;
  BEGIN
    -- 更新用情報の取得
    OPEN SELECT_URIJOHO;
      LOOP
        FETCH SELECT_URIJOHO
         INTO wApkihoncd, wShopcd, wTokuisakicd
            , wUrikin, wShohiTaxkin
            , wTokuisakiJotai
        ;

        -- データない場合
        IF SELECT_URIJOHO%ROWCOUNT = 0 THEN RAISE_APPLICATION_ERROR(-20016, 'ヘッダワークの有効データが見つかりませんでした'); EXIT;
        END IF;
        -- 終了
        IF SELECT_URIJOHO%NOTFOUND THEN EXIT;
        END IF;

        -- 得意先実績テーブル現在残高更新
        UPDATE S02M050T TJT
           SET TJT.GENZAIZD = ( CASE WHEN TJT.GENZAIZD IS NULL THEN wUrikin + wShohiTaxkin
                                ELSE      TJT.GENZAIZD + wUrikin + wShohiTaxkin END ) -- 現在残高：現在残高＋売上金額＋消費税金額
             , TJT.KOSHAAPKIHONCD = vRecordS06W002T.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
             , TJT.KOSHASHOPCD    = vRecordS06W002T.KOSHASHOPCD    -- 更新者ショップコード
             , TJT.KOSHACD        = vRecordS06W002T.KOSHACD        -- 更新者コード
             , TJT.KSHNSYSTMYMD   = vRecordS06W002T.KSHNSYSTMYMD   -- 更新システム日付: システム日付
             , TJT.KSHNSYSTMJIKAN = vRecordS06W002T.KSHNSYSTMJIKAN -- 更新システム時間: システム時間
             , TJT.KSHNUNYOYMD    = vRecordS06W002T.KSHNUNYOYMD    -- 更新運用日付: システム管理マスタ.文字設定値
             , TJT.KSHNIPADRSE    = vRecordS06W002T.KSHNIPADRSE    -- 更新ＩＰアドレス: パラメータ．更新ＩＰアドレス
             , TJT.KSHNPGMID      = 'C1003B21'                     -- 更新プログラムＩＤ: C1003B21
         WHERE TJT.APKIHONCD   = wApkihoncd
           AND TJT.SHOPCD      = wShopcd
           AND TJT.TOKUISAKICD = wTokuisakicd
        ;

        -- 得意先の状態が仮会員解約(4)／退会済（6）の場合：得意先実績テーブルの現在残高により得意先マスタ.状態ステータスを変更する
        IF wTokuisakiJotai = '4' OR wTokuisakiJotai = '6' THEN
           -- 更新後現在残高の取得
           SELECT TJT.GENZAIZD
             INTO wGenzaiZd
             FROM S02M050T TJT
            WHERE TJT.APKIHONCD   = wApkihoncd
              AND TJT.SHOPCD      = wShopcd
              AND TJT.TOKUISAKICD = wTokuisakicd
           ;
           -- 得意先マスタ.状態ステータスの更新
           UPDATE S02M013T TSM
              SET TSM.JOTAISTS = ( CASE WHEN (wTokuisakiJotai = '4' AND wGenzaiZd <> 0 ) THEN '05' -- 状態ステータス：得意先状態が仮会員退会(4)、現在残高ありの場合は退会(未支払有)(05)
                                        WHEN (wTokuisakiJotai = '4' AND wGenzaiZd = 0 )  THEN '06' -- 状態ステータス：得意先状態が仮会員退会(4)、現在残高が０の場合は退会済み(06)
                                        WHEN (wTokuisakiJotai = '6' AND wGenzaiZd <> 0 ) THEN '09' -- 状態ステータス：得意先状態が退会(6)、現在残高ありの場合は退会(未支払有)(09)
                                        WHEN (wTokuisakiJotai = '6' AND wGenzaiZd = 0 )  THEN '10' -- 状態ステータス：得意先状態が退会(6)、現在残高が０の場合は退会済み(10)
                                   ELSE TSM.JOTAISTS END )
                , TSM.KOSHAAPKIHONCD = vRecordS06W002T.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
                , TSM.KOSHASHOPCD    = vRecordS06W002T.KOSHASHOPCD    -- 更新者ショップコード
                , TSM.KOSHACD        = vRecordS06W002T.KOSHACD        -- 更新者コード
                , TSM.KSHNSYSTMYMD   = vRecordS06W002T.KSHNSYSTMYMD   -- 更新システム日付: システム日付
                , TSM.KSHNSYSTMJIKAN = vRecordS06W002T.KSHNSYSTMJIKAN -- 更新システム時間: システム時間
                , TSM.KSHNUNYOYMD    = vRecordS06W002T.KSHNUNYOYMD    -- 更新運用日付: システム管理マスタ.文字設定値
                , TSM.KSHNIPADRSE    = vRecordS06W002T.KSHNIPADRSE    -- 更新ＩＰアドレス: パラメータ．更新ＩＰアドレス
                , TSM.KSHNPGMID      = 'C1003B21'                     -- 更新プログラムＩＤ: C1003B21
            WHERE TSM.APKIHONCD   = wApkihoncd
              AND TSM.SHOPCD      = wShopcd
              AND TSM.TOKUISAKICD = wTokuisakicd
        ;
        END IF;

      END LOOP;
    CLOSE SELECT_URIJOHO;
  END UpdateTokuisaki;

  /*****************************************************************************/
  /* プロシージャ名   ：売上情報ヘッダワークの取込済更新                       */
  /* 引数             ：iPrcsidno          処理ID番号                          */
  /*****************************************************************************/
  PROCEDURE UpdateTorikomisumi(iPrcsidno      IN VARCHAR2) AS
  BEGIN
    UPDATE S06W002T UHW
       SET UHW.URIDATSKSFLG = '1'
     WHERE UHW.PRCSIDNO = iPrcsidno -- 処理ID番号
       AND UHW.URIDATSKSFLG = '0'   -- 売上データ未作成
       AND UHW.YUKOKBN = '1'        -- 有効区分(ヘッダ)
    ;
    -- 更新件数
    S99_CHECK_LOG('C1003B21.TableTRK', '', '', '1', 'U', '1', 'S06W002T', SQL%ROWCOUNT, vRecordS06W002T.KOSHAAPKIHONCD, vRecordS06W002T.KOSHASHOPCD, vRecordS06W002T.KOSHACD, vRecordS06W002T.KSHNIPADRSE, 'C1003B21.WorkTRK' , '売上情報ヘッダワークの取込済更新' );

  END UpdateTorikomisumi;

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
                                 , vRecordS06W002T.KOSHAAPKIHONCD   -- 担当者ＡＰ基本コード
                                 , vRecordS06W002T.KOSHASHOPCD      -- 担当者ショップコード
                                 , vRecordS06W002T.KOSHACD          -- 担当者コード
                                 , 'C1003B21'                       -- 処理ID
                                 , vRecordS06W002T.KSHNIPADRSE      -- ＩＰアドレス
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
                                 , vRecordS06W002T.KOSHAAPKIHONCD   -- 登録者ＡＰ基本コード
                                 , vRecordS06W002T.KOSHASHOPCD      -- 登録者ショップコード
                                 , vRecordS06W002T.KOSHACD          -- 登録者コード
                                 , vRecordS06W002T.KSHNSYSTMYMD     -- 登録システム日付
                                 , vRecordS06W002T.KSHNSYSTMJIKAN   -- 登録システム時間
                                 , vRecordS06W002T.KSHNUNYOYMD      -- 登録運用日付
                                 , vRecordS06W002T.KSHNIPADRSE      -- 登録ＩＰアドレス
                                 , vRecordS06W002T.KSHNPGMID        -- 登録プログラムＩＤ
                                 , vRecordS06W002T.KOSHAAPKIHONCD   -- 更新者AP基本コード
                                 , vRecordS06W002T.KOSHASHOPCD      -- 更新者ショップコード
                                 , vRecordS06W002T.KOSHACD          -- 更新者コード
                                 , vRecordS06W002T.KSHNSYSTMYMD     -- 更新システム日付
                                 , vRecordS06W002T.KSHNSYSTMJIKAN   -- 更新システム時間
                                 , vRecordS06W002T.KSHNUNYOYMD      -- 更新運用日付
                                 , vRecordS06W002T.KSHNIPADRSE      -- 更新ＩＰアドレス
                                 , vRecordS06W002T.KSHNPGMID);      -- 更新プログラムＩＤ

      -- 実行
      Z2401B01.PointinfbhnProc(iApKihoncd                           -- AP基本コード
                               , iShopcd                            -- ショップコード
                               , vRecordS06W002T.KOSHAAPKIHONCD     -- 担当者ＡＰ基本コード
                               , vRecordS06W002T.KOSHASHOPCD        -- 担当者ショップコード
                               , vRecordS06W002T.KOSHACD            -- 担当者コード
                               , 'C1003B21'                         -- 処理ID
                               , vRecordS06W002T.KSHNIPADRSE        -- ＩＰアドレス
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
        TNSHAAPKIHONCD  = vRecordS06W002T.KOSHAAPKIHONCD
        AND TNSHASHOPCD = vRecordS06W002T.KOSHASHOPCD
        AND TNSHACD     = vRecordS06W002T.KOSHACD
        AND PRCSID      = 'C1003B21';

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
  /*                  ：iPrcsidno          処理ID番号                          */
  /*****************************************************************************/
  PROCEDURE InsertPoint(iApKihoncd     IN VARCHAR2,
                        iShopcd        IN VARCHAR2,
                        iPrcsidno      IN VARCHAR2) AS
    -- 変数宣言
    -- 処理結果ワークリスト
    TYPE                S20W003TList IS TABLE OF S20W003T%rowtype INDEX BY PLS_INTEGER;
    wSaibanno           VARCHAR2(15);  -- 採番した予定発行ポイントテーブル管理番号
    wUnyodd             VARCHAR2(8);  -- 運用日付
    wS20W003TList       S20W003TList;
    i                   PLS_INTEGER := 0;
    wYukoData           NUMBER(1) := 0;
    wRenban             NUMBER(2) := 1;
  BEGIN
    -- 運用日付取得
    wUnyodd := GetOperationdate(iApKihoncd, iShopcd);


    -- ポイント情報部品(Z2401B01)で予定発生ポイントテーブルに登録する情報取得
    -- 引数ワーク(S20W002T)登録
    INSERT INTO S20W002T VALUES (iApKihoncd                             -- ＡＰ基本コード
                                 , iShopcd                              -- ショップコード
                                 , vRecordS06W002T.KOSHAAPKIHONCD       -- 担当者ＡＰ基本コード
                                 , vRecordS06W002T.KOSHASHOPCD          -- 担当者ショップコード
                                 , vRecordS06W002T.KOSHACD              -- 担当者コード
                                 , 'C1003B21'                           -- 処理ID
                                 , vRecordS06W002T.KSHNIPADRSE          -- ＩＰアドレス
                                 , '1'                                  -- 行番号
                                 , vRecordS06T002T.URIDNPYMEISAINO      -- 伝票明細番号
                                 , vRecordS06T002T.URIDNPYMEISAIEDANO   -- 伝票明細枝番号
                                 , vRecordS06T002T.SHNCD                -- 商品コード
                                 , vRecordS06T002T.KEIJOKBN             -- 計上区分
                                 , vRecordS06T002T.UNT                  -- 商品単価
                                 , null                                 -- 画面からの発生
                                 , vRecordS06T002T.SURYO                -- 数量
                                 , vRecordS06T002T.MEISAIBIKO           -- 明細備考
                                 , null                                 -- 予定発生ポイント明細の管理番号
                                 , null                                 -- 予定発生ポイント明細の連番
                                 , null                                 -- 売上還元ポイントマスタの取得種別
                                 , null                                 -- 売上還元ポイントマスタの取得プランコード
                                 , '1'                                  -- 有効区分
                                 , vRecordS06W002T.KOSHAAPKIHONCD       -- 登録者ＡＰ基本コード
                                 , vRecordS06W002T.KOSHASHOPCD          -- 登録者ショップコード
                                 , vRecordS06W002T.KOSHACD              -- 登録者コード
                                 , vRecordS06W002T.KSHNSYSTMYMD         -- 登録システム日付
                                 , vRecordS06W002T.KSHNSYSTMJIKAN       -- 登録システム時間
                                 , vRecordS06W002T.KSHNUNYOYMD          -- 登録運用日付
                                 , vRecordS06W002T.KSHNIPADRSE          -- 登録ＩＰアドレス
                                 , vRecordS06W002T.KSHNPGMID            -- 登録プログラムＩＤ
                                 , vRecordS06W002T.KOSHAAPKIHONCD       -- 更新者AP基本コード
                                 , vRecordS06W002T.KOSHASHOPCD          -- 更新者ショップコード
                                 , vRecordS06W002T.KOSHACD              -- 更新者コード
                                 , vRecordS06W002T.KSHNSYSTMYMD         -- 更新システム日付
                                 , vRecordS06W002T.KSHNSYSTMJIKAN       -- 更新システム時間
                                 , vRecordS06W002T.KSHNUNYOYMD          -- 更新運用日付
                                 , vRecordS06W002T.KSHNIPADRSE          -- 更新ＩＰアドレス
                                 , vRecordS06W002T.KSHNPGMID);          -- 更新プログラムＩＤ

    -- 実行
    Z2401B01.PointinfbhnProc(iApKihoncd                                 -- AP基本コード
                             , iShopcd                                  -- ショップコード
                             , vRecordS06W002T.KOSHAAPKIHONCD           -- 担当者ＡＰ基本コード
                             , vRecordS06W002T.KOSHASHOPCD              -- 担当者ショップコード
                             , vRecordS06W002T.KOSHACD                  -- 担当者コード
                             , 'C1003B21'                               -- 処理ID
                             , vRecordS06W002T.KSHNIPADRSE              -- ＩＰアドレス
                             , '02'                                     -- ポイント情報部品処理種別(一括計算)
                             , vRecordS06T001T.TOKUISAKICD              -- 得意先コード
                             , '05'                                     -- 伝達種別(売上)
                             , vRecordS06T001T.URIDD                    -- 注文日:売上日
                             , null                                     -- 配達予定日
                             , null                                     -- 配達希望時間区分
                             , vRecordS06T001T.DLVRYKBN                 -- 配達区分
                             , null);                                   -- 受注受付経路区分

    -- 処理結果ワーク(S20W003T)から実行結果読取り
    i := 0;
    FOR cS20W003T IN (SELECT * FROM S20W003T
                      WHERE TNSHAAPKIHONCD  = vRecordS06W002T.KOSHAAPKIHONCD
                        AND TNSHASHOPCD     = vRecordS06W002T.KOSHASHOPCD
                        AND TNSHACD         = vRecordS06W002T.KOSHACD
                        AND PRCSID          = 'C1003B21'
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
          S99_CHECK_LOG('C1003B21.TableTRK'
                        , iApkihoncd
                        , iShopcd
                        , '1'
                        , 'S'
                        , '1'
                        , 'XXX'
                        , 0
                        , vRecordS06W002T.KOSHAAPKIHONCD
                        , vRecordS06W002T.KOSHASHOPCD
                        , vRecordS06W002T.KOSHACD
                        , vRecordS06W002T.KSHNIPADRSE
                        , 'C1003B21.TableTRK'
                        , utl_lms.format_message('ポイントプログラムに不参加のショップです。ショップコード:[%s]', wS20W003TList(k).SHOPCD));

        ELSIF wS20W003TList(k).SYORIKEKKA = 3 THEN
          -- 得意先がポイントなし
          S99_CHECK_LOG('C1003B21.TableTRK'
                        , iApkihoncd
                        , iShopcd
                        , '1'
                        , 'S'
                        , '1'
                        , 'XXX'
                        , 0
                        , vRecordS06W002T.KOSHAAPKIHONCD
                        , vRecordS06W002T.KOSHASHOPCD
                        , vRecordS06W002T.KOSHACD
                        , vRecordS06W002T.KSHNIPADRSE
                        , 'C1003B21.TableTRK'
                        , utl_lms.format_message('ポイント管理を行わない得意先です。得意先コード:[%s]', wS20W003TList(k).TOKUISAKICD));

        ELSIF wS20W003TList(k).SYORIKEKKA = 4 THEN
          -- 商品がポイントなし
          S99_CHECK_LOG('C1003B21.TableTRK'
                        , iApkihoncd
                        , iShopcd
                        , '1'
                        , 'S'
                        , '1'
                        , 'XXX'
                        , 0
                        , vRecordS06W002T.KOSHAAPKIHONCD
                        , vRecordS06W002T.KOSHASHOPCD
                        , vRecordS06W002T.KOSHACD
                        , vRecordS06W002T.KSHNIPADRSE
                        , 'C1003B21.TableTRK'
                        , utl_lms.format_message('ポイントが発生しない商品または計上区分です。商品コード:[%s]', wS20W003TList(k).SHNCD));
        ELSIF wS20W003TList(k).SYORIKEKKA = 5 THEN
          -- 得意先がマイアクア会員登録なし
          S99_CHECK_LOG('C1003B21.TableTRK'
                        , iApkihoncd
                        , iShopcd
                        , '1'
                        , 'S'
                        , '1'
                        , 'XXX'
                        , 0
                        , vRecordS06W002T.KOSHAAPKIHONCD
                        , vRecordS06W002T.KOSHASHOPCD
                        , vRecordS06W002T.KOSHACD
                        , vRecordS06W002T.KSHNIPADRSE
                        , 'C1003B21.TableTRK'
                        , utl_lms.format_message('マイアクア会員が未登録の得意先です。得意先コード:[%s]', wS20W003TList(k).TOKUISAKICD));
        ELSIF wS20W003TList(k).SYORIKEKKA = -1 THEN
          -- 取込画面側の入力CSVとマスタの不整合チェック以外の予期せしないエラー発生時
          S99_CHECK_LOG('C1003B21.TableTRK'
                        , iApkihoncd
                        , iShopcd
                        , '1'
                        , 'S'
                        , '1'
                        , 'XXX'
                        , 0
                        , vRecordS06W002T.KOSHAAPKIHONCD
                        , vRecordS06W002T.KOSHASHOPCD
                        , vRecordS06W002T.KOSHACD
                        , vRecordS06W002T.KSHNIPADRSE
                        , 'C1003B21.TableTRK'
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
                                     , vRecordS06W002T.KOSHAAPKIHONCD               -- 登録者ＡＰ基本コード
                                     , vRecordS06W002T.KOSHASHOPCD                  -- 登録者ショップコード
                                     , vRecordS06W002T.KOSHACD                      -- 登録者コード
                                     , vRecordS06W002T.KSHNSYSTMYMD                 -- 登録システム日付
                                     , vRecordS06W002T.KSHNSYSTMJIKAN               -- 登録システム時間
                                     , vRecordS06W002T.KSHNUNYOYMD                  -- 登録運用日付
                                     , vRecordS06W002T.KSHNIPADRSE                  -- 登録ＩＰアドレス
                                     , vRecordS06W002T.KSHNPGMID                    -- 登録プログラムＩＤ
                                     , vRecordS06W002T.KOSHAAPKIHONCD               -- 更新者AP基本コード
                                     , vRecordS06W002T.KOSHASHOPCD                  -- 更新者ショップコード
                                     , vRecordS06W002T.KOSHACD                      -- 更新者コード
                                     , vRecordS06W002T.KSHNSYSTMYMD                 -- 更新システム日付
                                     , vRecordS06W002T.KSHNSYSTMJIKAN               -- 更新システム時間
                                     , vRecordS06W002T.KSHNUNYOYMD                  -- 更新運用日付
                                     , vRecordS06W002T.KSHNIPADRSE                  -- 更新ＩＰアドレス
                                     , vRecordS06W002T.KSHNPGMID);                  -- 更新プログラムＩＤ

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
                                         , vRecordS06W002T.KOSHAAPKIHONCD             -- 登録者ＡＰ基本コード
                                         , vRecordS06W002T.KOSHASHOPCD                -- 登録者ショップコード
                                         , vRecordS06W002T.KOSHACD                    -- 登録者コード
                                         , vRecordS06W002T.KSHNSYSTMYMD               -- 登録システム日付
                                         , vRecordS06W002T.KSHNSYSTMJIKAN             -- 登録システム時間
                                         , vRecordS06W002T.KSHNUNYOYMD                -- 登録運用日付
                                         , vRecordS06W002T.KSHNIPADRSE                -- 登録ＩＰアドレス
                                         , vRecordS06W002T.KSHNPGMID                  -- 登録プログラムＩＤ
                                         , vRecordS06W002T.KOSHAAPKIHONCD             -- 更新者AP基本コード
                                         , vRecordS06W002T.KOSHASHOPCD                -- 更新者ショップコード
                                         , vRecordS06W002T.KOSHACD                    -- 更新者コード
                                         , vRecordS06W002T.KSHNSYSTMYMD               -- 更新システム日付
                                         , vRecordS06W002T.KSHNSYSTMJIKAN             -- 更新システム時間
                                         , vRecordS06W002T.KSHNUNYOYMD                -- 更新運用日付
                                         , vRecordS06W002T.KSHNIPADRSE                -- 更新ＩＰアドレス
                                         , vRecordS06W002T.KSHNPGMID);                -- 更新プログラムＩＤ

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
                                         , vRecordS06W002T.KOSHAAPKIHONCD             -- 登録者ＡＰ基本コード
                                         , vRecordS06W002T.KOSHASHOPCD                -- 登録者ショップコード
                                         , vRecordS06W002T.KOSHACD                    -- 登録者コード
                                         , vRecordS06W002T.KSHNSYSTMYMD               -- 登録システム日付
                                         , vRecordS06W002T.KSHNSYSTMJIKAN             -- 登録システム時間
                                         , vRecordS06W002T.KSHNUNYOYMD                -- 登録運用日付
                                         , vRecordS06W002T.KSHNIPADRSE                -- 登録ＩＰアドレス
                                         , vRecordS06W002T.KSHNPGMID                  -- 登録プログラムＩＤ
                                         , vRecordS06W002T.KOSHAAPKIHONCD             -- 更新者AP基本コード
                                         , vRecordS06W002T.KOSHASHOPCD                -- 更新者ショップコード
                                         , vRecordS06W002T.KOSHACD                    -- 更新者コード
                                         , vRecordS06W002T.KSHNSYSTMYMD               -- 更新システム日付
                                         , vRecordS06W002T.KSHNSYSTMJIKAN             -- 更新システム時間
                                         , vRecordS06W002T.KSHNUNYOYMD                -- 更新運用日付
                                         , vRecordS06W002T.KSHNIPADRSE                -- 更新ＩＰアドレス
                                         , vRecordS06W002T.KSHNPGMID);                -- 更新プログラムＩＤ

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

  /*****************************************************************************/
  /* プロシージャ名   ：売上テーブル登録                                       */
  /* 引数             ：iApkihoncd         ＡＰ基本コード                      */
  /*                  ：iShopcd            ショップコード                      */
  /*                  ：iPrcsidno          処理ID番号                          */
  /*****************************************************************************/
  PROCEDURE UriageTableToroku(iApKihoncd     IN VARCHAR2,
                              iShopcd        IN VARCHAR2,
                              iPrcsidno      IN VARCHAR2) AS
    -- 変数宣言
    wDnpykbn                    VARCHAR2(2);  -- 伝票区分
    wHeaderKeys                 VARCHAR2(19); -- ヘッダキー（AP-ショップ-売上伝票番号-枝番）
    wHeaderInsertFLG            BOOLEAN;      -- ヘッダテーブル登録フラグ
    wNyukamotoJissekiTnshacd    VARCHAR2(6);  -- 入荷元実績担当者コード
    wInvoicshikoymd             VARCHAR2(8);  -- インボイス施行日
    wTokuisakikbn               VARCHAR2(1);  -- 得意先区分
    wJutakudlvrycstkbn          VARCHAR2(1);  -- 受託配達顧客区分
    wHeaderRecordFLG            BOOLEAN;      -- 消費税登録フラグ
    wZnAPkihoncd                VARCHAR2(3);  -- 前レコードのAP基本コード
    wZnShopcd                   VARCHAR2(3);  -- 前レコードのショップコード
    wZnSkysakicd                VARCHAR2(6);  -- 前レコードの請求先コード
    wZnUridd                    VARCHAR2(8);  -- 前レコードの売上日
    wZnKaishuHohokbn            VARCHAR2(1);  -- 前レコードの回収方法区分
    wZnRecordS06T001T           S06T001T%rowtype; -- 前回売上テーブル
    wZnRecordS06T002T           S06T002T%rowtype; -- 前回売上明細テーブル
    -- 登録用情報抽出
    CURSOR SELECT_URIJOHO IS
      SELECT UHW.APKIHONCD -- ヘッダワーク.AP基本コード
           , UHW.SHOPCD    -- ヘッダワーク.ショップコード
           , UHW.URIDNPYNO -- ヘッダワーク.売上伝票番号
           , UHW.EDANO     -- ヘッダワーク.枝番
           , UHW.DNPYKBN   -- ヘッダワーク.伝票区分
           , UHW.TOKUISAKICD -- ヘッダワーク.得意先コード
           , UHW.SIIRESAKICD -- ヘッダワーク.仕入先コード
           , UHW.NYUKAMOTOCD -- ヘッダワーク.入荷元コード
           , UHW.URIDD                    -- ヘッダワーク.売上日
           , UHW.URIKIN                   -- ヘッダワーク.売上金額
           , UHW.SHOHITAXKIN              -- ヘッダワーク.消費税金額
           , UHW.DLVRYTNSHACD             -- ヘッダワーク.配達担当者コード
           , UHW.DLVRYKANKATSUAPKIHONCD   -- ヘッダワーク.配達管轄AP基本コード
           , UHW.DLVRYKANKATSUSHOPCD      -- ヘッダワーク.配達管轄ショップコード
           , UHW.DLVRYKBN                 -- ヘッダワーク.配達区分
           , UHW.EIGYOTNSHACD             -- ヘッダワーク.営業担当者コード
           , UHW.SHUKKASAKIJISSEKITNSHACD -- ヘッダワーク.出荷先実績担当者コード
           , UHW.NYUKAMOTOJISSEKITNSHACD  -- ヘッダワーク.入荷元実績担当者コード
           , UHW.SKYSAKICD                -- ヘッダワーク.請求先コード
           , UHW.DNPYBIKO                 -- ヘッダワーク.伝票備考
           , UHW.DATSKSMOTOKBN            -- ヘッダワーク.データ作成元区分
           , UHW.TAXKEISANKBN             -- ヘッダワーク.税計算区分
           , UMW.URIDNPYMEISAINO    -- 明細ワーク.売上伝票明細番号
           , UMW.URIDNPYMEISAIEDANO -- 明細ワーク.売上伝票明細枝番号
           , UMW.SHNCD              -- 明細ワーク.商品コード
           , UMW.KEIJOKBN -- 明細ワーク.計上区分
           , UMW.SURYO    -- 明細ワーク.数量
           , UMW.UNT      -- 明細ワーク.単価
           , UMW.KIN      -- 明細ワーク.金額
           , UMW.UNTSHOHITAXKIN -- 明細ワーク.単価消費税金額
           , UMW.SHOHITAXKIN    -- 明細ワーク.消費税金額
           , UMW.TAXKBN         -- 明細ワーク.税区分
           , UMW.MEISAIKBN      -- 明細ワーク.明細区分
           , UMW.NYURYOKUMEISAIFLG -- 明細ワーク.入力明細フラグ
           , UMW.TANPINMEISAIFLG   -- 明細ワーク.単品明細フラグ
           , UMW.MEISAIBIKO -- 明細ワーク.明細備考
           , UMW.STSHNCD    -- 明細ワーク.セット商品コード
           , UMW.KATASIKI   -- 明細ワーク.型式
           , UMW.SIIRESURYO       -- 明細ワーク.仕入数量
           , UMW.SIIREKIN         -- 明細ワーク.仕入金額
           , UMW.SIIREUNT         -- 明細ワーク.仕入単価
           , UMW.SIIRESHOHITAXKIN -- 明細ワーク.仕入消費税金額
           , SHM.INVOICSHIKOYMD    -- ショップマスタ.インボイス施行日
           , TKM.TOKUISAKIKBN      -- 得意先マスタ.得意先区分
           , TKM.JUTAKUDLVRYCSTKBN -- 得意先マスタ.受託配達顧客区分
      FROM S06W002T UHW        -- 売上情報ヘッダワーク
       INNER JOIN S06W003T UMW -- 売上情報明細ワーク
       ON ( UHW.APKIHONCD = UMW.APKIHONCD AND UHW.SHOPCD = UMW.SHOPCD AND UHW.PRCSIDNO = UMW.PRCSIDNO AND UHW.URITORIKOMIKANRINO = UMW.URITORIKOMIKANRINO )
       INNER JOIN S02M046T SHM -- ショップマスタ
       ON ( UHW.APKIHONCD = SHM.APKIHONCD AND UHW.SHOPCD = SHM.SHOPCD )
       INNER JOIN S02M013T TKM -- 得意先マスタ
       ON ( UHW.APKIHONCD = TKM.APKIHONCD AND UHW.SHOPCD = TKM.SHOPCD AND UHW.TOKUISAKICD = TKM.TOKUISAKICD )
      WHERE UHW.PRCSIDNO = iPrcsidno -- 処理ID番号
        AND UHW.URIDATSKSFLG = '0'   -- 売上データ未作成
        AND UHW.YUKOKBN = '1' -- 有効区分(ヘッダ)
        AND UMW.YUKOKBN = '1' -- 有効区分(明細)
      ORDER BY UHW.APKIHONCD, UHW.SHOPCD, UHW.PRCSIDNO, UHW.URITORIKOMIKANRINO, UMW.URIDNPYMEISAINO, UMW.URIDNPYMEISAIEDANO
    ;
  BEGIN
    -- 変数初期化
    wHeaderKeys := NULL;
    wHeaderRecordFLG := FALSE;
    S99_CHECK_LOG('C1003B21.TableTRK', iApkihoncd, iShopcd, '1', 'S', '1', 'XXX', 0, vRecordS06W002T.KOSHAAPKIHONCD, vRecordS06W002T.KOSHASHOPCD, vRecordS06W002T.KOSHACD, vRecordS06W002T.KSHNIPADRSE, 'C1003B21.TableTRK' , '01-伝票登録[開始] ' );
    -- 登録用情報の取得
    OPEN SELECT_URIJOHO;
      LOOP
        -- 変数初期化
        wDnpykbn := NULL;
        vRecordS06T001T := NULL;
        vRecordS06T002T := NULL;

        -- 情報の取得
        FETCH SELECT_URIJOHO
          INTO vRecordS06T001T.APKIHONCD -- AP基本コード
             , vRecordS06T001T.SHOPCD    -- ショップコード
             , vRecordS06T001T.URIDNPYNO -- 売上伝票番号
             , vRecordS06T001T.EDANO     -- 枝番号
             , wDnpykbn -- 伝票区分
             , vRecordS06T001T.TOKUISAKICD -- 得意先コード
             , vRecordS06T001T.SIIRESAKICD -- 仕入先コード
             , vRecordS06T001T.NYUKAMOTOCD -- 入荷元コード
             , vRecordS06T001T.URIDD                  -- 売上日
             , vRecordS06T001T.URIKIN                 -- 売上金額
             , vRecordS06T001T.SHOHITAXKIN            -- 消費税金額
             , vRecordS06T001T.DLVRYTNSHACD           -- 配達担当者コード
             , vRecordS06T001T.DLVRYKANKATSUAPKIHONCD -- 配達管轄ＡＰ基本コード
             , vRecordS06T001T.DLVRYKANKATSUSHOPCD    -- 配達管轄ショップコード
             , vRecordS06T001T.DLVRYKBN               -- 配達区分
             , vRecordS06T001T.EIGYOTNSHACD           -- 営業担当者コード
             , vRecordS06T001T.JISSEKITNSHACD         -- 実績担当者コード：出荷先実績担当者コード
             , wNyukamotoJissekiTnshacd               -- 入荷元実績担当者コード
             , vRecordS06T001T.SKYSAKICD              -- 請求先コード
             , vRecordS06T001T.DNPYBIKO               -- 伝票備考
             , vRecordS06T001T.DATSKSMOTOKBN          -- データ作成元区分
             , vRecordS06T001T.TAXKEISANKBN           -- 税計算区分
             , vRecordS06T002T.URIDNPYMEISAINO    -- 明細.売上伝票明細番号
             , vRecordS06T002T.URIDNPYMEISAIEDANO -- 明細.売上伝票明細枝番号
             , vRecordS06T002T.SHNCD              -- 明細.商品コード
             , vRecordS06T002T.KEIJOKBN -- 明細.計上区分
             , vRecordS06T002T.SURYO    -- 明細.数量
             , vRecordS06T002T.UNT      -- 明細.単価
             , vRecordS06T002T.KIN      -- 明細.金額
             , vRecordS06T002T.UNTSHOHITAXKIN -- 明細.単価消費税金額
             , vRecordS06T002T.SHOHITAXKIN    -- 明細.消費税金額
             , vRecordS06T002T.TAXKBN         -- 明細.税区分
             , vRecordS06T002T.MEISAIKBN      -- 明細.明細区分
             , vRecordS06T002T.NYURYOKUMEISAIFLG -- 明細.入力明細フラグ
             , vRecordS06T002T.TANPINMEISAIFLG   -- 明細.単品明細フラグ
             , vRecordS06T002T.MEISAIBIKO -- 明細.明細備考
             , vRecordS06T002T.STSHNCD    -- 明細.セット商品コード
             , vRecordS06T002T.KATASIKI   -- 明細.型式
             , vRecordS06T002T.SIIRESURYO       -- 明細.仕入数量
             , vRecordS06T002T.SIIREKIN         -- 明細.仕入金額
             , vRecordS06T002T.SIIREUNT         -- 明細.仕入単価
             , vRecordS06T002T.SIIRESHOHITAXKIN -- 明細.仕入消費税金額
             , wInvoicshikoymd    -- ショップマスタ.インボイス施行日
             , wTokuisakikbn      -- 得意先マスタ.得意先区分
             , wJutakudlvrycstkbn -- 得意先マスタ.受託配達顧客区分
        ;

        -- データない場合
        IF SELECT_URIJOHO%ROWCOUNT = 0 THEN RAISE_APPLICATION_ERROR(-20015, 'ヘッダワークの有効データが見つかりませんでした'); EXIT;
        END IF;
        -- 終了
        IF SELECT_URIJOHO%NOTFOUND THEN
          -- 消費税情報登録（1件目および最終行のレコード処理時）
          TaxToroku(wZnAPkihoncd, wZnShopcd, wZnUridd, wTokuisakikbn, wJutakudlvrycstkbn, wZnSkysakicd,wZnKaishuHohokbn, wZnRecordS06T001T, wZnRecordS06T002T);
          EXIT;
        END IF;

        -- ヘッダテーブル登録フラグ
        wHeaderInsertFLG := FALSE;
        IF (wHeaderKeys IS NULL OR wHeaderKeys <>
                                   vRecordS06T001T.APKIHONCD || vRecordS06T001T.SHOPCD || vRecordS06T001T.URIDNPYNO || vRecordS06T001T.EDANO ) THEN
          wHeaderInsertFLG := TRUE;
          wHeaderKeys := vRecordS06W002T.APKIHONCD || vRecordS06W002T.SHOPCD || vRecordS06T001T.URIDNPYNO || vRecordS06T001T.EDANO;
        END IF;

        -- 売上登録：売上テーブル登録、その他の売上値の設定
        IF wHeaderInsertFLG THEN
          InsertUriage(wDnpykbn, wInvoicshikoymd);

          -- 処理レコードが1件の場合、当処理内にて消費税情報登録は行わない。
          IF (wHeaderRecordFLG) THEN
            -- 消費税情報登録（2件目および最終行前のレコード処理時）
            TaxToroku(wZnAPkihoncd, wZnShopcd, wZnUridd, wTokuisakikbn, wJutakudlvrycstkbn, wZnSkysakicd,wZnKaishuHohokbn, wZnRecordS06T001T, wZnRecordS06T002T);
          END IF;
        END IF; -- ヘッダ、代引き請求先の請求残高更新
        InsertUriageMeisai(wInvoicshikoymd, wTokuisakikbn, wJutakudlvrycstkbn); -- 明細

        -- ポイント制度対応追加
        -- 入力明細フラグが'1'の場合にポイントを登録（展開行を除外）
        IF vRecordS06T002T.NYURYOKUMEISAIFLG = '1' THEN
          -- 予定発生ポイントテーブル、予定発生ポイント明細テーブル、予定発生ポイント明細計算詳細テーブル
          InsertPoint(iApKihoncd, iShopcd, iPrcsidno);
        END IF;

        -- 仕入登録：伝票区分が仕入売上(21)と経費購入(05)の場合のみ

        -- 入出庫登録：伝票区分が売上出庫(14)と経費出庫(15)の場合のみ
        IF wDnpykbn IN ('14', '15') THEN
          IF wHeaderInsertFLG THEN InsertNyushukkoJisseki(wNyukamotoJissekiTnshacd); END IF; -- ヘッダ
          InsertNyushukkoJissekiMeisai; -- 明細
          UpdateTableZaiko(wDnpykbn); -- 在庫更新処理
        END IF;

        -- 1件目の読み込みが完了した時点でフラグをTRUEに更新する。
        wHeaderRecordFLG := TRUE;
        -- 前回登録売上情報情報を保持
        wZnAPkihoncd := vRecordS06T001T.APKIHONCD;
        wZnShopcd := vRecordS06T001T.SHOPCD;
        wZnUridd := vRecordS06T001T.URIDD;
        wZnSkysakicd := vRecordS06T001T.SKYSAKICD;
        wZnRecordS06T001T := vZnRecordS06T001T;
        -- 前回登録売上情報明細情報を保持
        wZnRecordS06T002T := vZnRecordS06T002T;
        -- 前回登録その他情報情報を保持（回収方法区分）
        wZnKaishuHohokbn := vZnKaishuHohokbn;

      END LOOP;
    CLOSE SELECT_URIJOHO;
    S99_CHECK_LOG('C1003B21.TableTRK', iApkihoncd, iShopcd, '1', 'E', '1', 'XXX', 0, vRecordS06W002T.KOSHAAPKIHONCD, vRecordS06W002T.KOSHASHOPCD, vRecordS06W002T.KOSHACD, vRecordS06W002T.KSHNIPADRSE, 'C1003B21.TableTRK' , '01-伝票登録[完了] ' );

    -- 得意先実績とマスタの更新
    S99_CHECK_LOG('C1003B21.TableTRK', iApkihoncd, iShopcd, '1', 'S', '1', 'XXX', 0, vRecordS06W002T.KOSHAAPKIHONCD, vRecordS06W002T.KOSHASHOPCD, vRecordS06W002T.KOSHACD, vRecordS06W002T.KSHNIPADRSE, 'C1003B21.TableTRK' , '02-得意先更新[開始] ' );
    UpdateTokuisaki( iPrcsidno);
    S99_CHECK_LOG('C1003B21.TableTRK', iApkihoncd, iShopcd, '1', 'E', '1', 'XXX', 0, vRecordS06W002T.KOSHAAPKIHONCD, vRecordS06W002T.KOSHASHOPCD, vRecordS06W002T.KOSHACD, vRecordS06W002T.KSHNIPADRSE, 'C1003B21.TableTRK' , '02-得意先更新[完了] ' );

    -- 取込済更新
    S99_CHECK_LOG('C1003B21.TableTRK', iApkihoncd, iShopcd, '1', 'S', '1', 'XXX', 0, vRecordS06W002T.KOSHAAPKIHONCD, vRecordS06W002T.KOSHASHOPCD, vRecordS06W002T.KOSHACD, vRecordS06W002T.KSHNIPADRSE, 'C1003B21.TableTRK' , '03-取込済更新[開始] ' );
    UpdateTorikomisumi( iPrcsidno);
    S99_CHECK_LOG('C1003B21.TableTRK', iApkihoncd, iShopcd, '1', 'E', '1', 'XXX', 0, vRecordS06W002T.KOSHAAPKIHONCD, vRecordS06W002T.KOSHASHOPCD, vRecordS06W002T.KOSHACD, vRecordS06W002T.KSHNIPADRSE, 'C1003B21.TableTRK' , '03-取込済更新[完了] ' );

  EXCEPTION
    WHEN OTHERS THEN
      IF SQLCODE >= -20000 AND SQLCODE <= -29999 THEN
        RAISE_APPLICATION_ERROR(SQLCODE, SQLERRM);
      ELSE
        RAISE_APPLICATION_ERROR(-20719, SQLERRM);
      END IF;

  END UriageTableToroku;

  /******************************************************************************/
  /* プロシージャ名 ：売上情報取込処理                                          */
  /* 引数           ：iApkihoncd            ＡＰ基本コード                      */
  /*                ：iShopcd               ショップコード                      */
  /*                ：iUserApkihoncd        ログインユーザ情報(ＡＰ基本コード)  */
  /*                ：iUserShopcd           ログインユーザ情報(ショップコード)  */
  /*                ：iTnshacd              ログインユーザ情報(担当者コード)    */
  /*                ：iIpadrse              ログインユーザ情報(ＩＰアドレス)    */
  /*                ：iDatsksmotokbn        データ作成元区分                    */
  /******************************************************************************/
  PROCEDURE UriageJohoTorikomi(iApKihoncd     IN VARCHAR2,
                               iShopcd        IN VARCHAR2,
                               iUserApKihoncd IN VARCHAR2,
                               iUserShopcd    IN VARCHAR2,
                               iTnshacd       IN VARCHAR2,
                               iIpadrse       IN VARCHAR2,
                               iDatsksmotokbn IN VARCHAR2) AS

    -- 変数宣言
    wPrcsidno                    VARCHAR2(10);  -- 処理ID番号
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
      RAISE_APPLICATION_ERROR(-20003,
                              'パラメータ.担当者ＡＰ基本コード が設定されていません。');
    END IF;
    IF iUserShopcd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20004,
                              'パラメータ.担当者ショップコード  が設定されていません。');
    END IF;
    IF iTnshacd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20005,
                              'パラメータ.担当者コード が設定されていません。');
    END IF;
    IF iIpadrse IS NULL THEN
      RAISE_APPLICATION_ERROR(-20006,
                              'パラメータ.ＩＰアドレス が設定されていません。');
    END IF;
    IF iDatsksmotokbn IS NULL THEN
      RAISE_APPLICATION_ERROR(-20007,
                              'パラメータ.データ作成元区分 が設定されていません。');
    END IF;

    -- 基本情報設定
    S99_CHECK_LOG('C1003B21', iApkihoncd, iShopcd, '1', 'S', '1', 'XXX', 0, iUserApkihoncd, iUserShopcd, iTnshacd, iIpadrse, 'C1003B21' , '01-基本情報設定[開始] データ作成元区分:' || iDatsksmotokbn );
    SetUserInfo(iApKihoncd, iShopcd, iUserApKihoncd, iUserShopcd, iTnshacd, iIpadrse);
    S99_CHECK_LOG('C1003B21', iApkihoncd, iShopcd, '1', 'E', '1', 'XXX', 0, iUserApkihoncd, iUserShopcd, iTnshacd, iIpadrse, 'C1003B21' , '01-基本情報設定[完了] データ作成元区分:' || iDatsksmotokbn );

    -- 採番：処理ID番号
    S99_CHECK_LOG('C1003B21', iApkihoncd, iShopcd, '1', 'S', '1', 'XXX', 0, iUserApkihoncd, iUserShopcd, iTnshacd, iIpadrse, 'C1003B21' , '02-採番処理[開始] データ作成元区分:' || iDatsksmotokbn );
    Z1804B01.getSlipNoPrcsId( wPrcsidno);
    S99_CHECK_LOG('C1003B21', iApkihoncd, iShopcd, '1', 'E', '1', 'XXX', 0, iUserApkihoncd, iUserShopcd, iTnshacd, iIpadrse, 'C1003B21' , '02-採番処理[完了] データ作成元区分:' || iDatsksmotokbn );

    -- 売上情報ヘッダと明細ワークに登録する
    S99_CHECK_LOG('C1003B21', iApkihoncd, iShopcd, '1', 'S', '1', 'XXX', 0, iUserApkihoncd, iUserShopcd, iTnshacd, iIpadrse, 'C1003B21' , '03-売上情報ワーク登録[開始] データ作成元区分:' || iDatsksmotokbn || '処理ID番号:' || wPrcsidno || 'AP基本コード：' || iApkihoncd || 'ショップコード：' || iShopcd );
    UriageJohoWorkToroku( iApkihoncd, iShopcd, iDatsksmotokbn, wPrcsidno);
    S99_CHECK_LOG('C1003B21', iApkihoncd, iShopcd, '1', 'E', '1', 'XXX', 0, iUserApkihoncd, iUserShopcd, iTnshacd, iIpadrse, 'C1003B21' , '03-売上情報ワーク登録[完了] データ作成元区分:' || iDatsksmotokbn || '処理ID番号:' || wPrcsidno || 'AP基本コード：' || iApkihoncd || 'ショップコード：' || iShopcd );

    -- 売上テーブルと明細テーブルに登録する
    S99_CHECK_LOG('C1003B21', iApkihoncd, iShopcd, '1', 'S', '1', 'XXX', 0, iUserApkihoncd, iUserShopcd, iTnshacd, iIpadrse, 'C1003B21' , '04-売上テーブル登録[開始] データ作成元区分:' || iDatsksmotokbn || '処理ID番号:' || wPrcsidno || 'AP基本コード：' || iApkihoncd || 'ショップコード：' || iShopcd );
    UriageTableToroku( iApkihoncd, iShopcd, wPrcsidno);
    S99_CHECK_LOG('C1003B21', iApkihoncd, iShopcd, '1', 'E', '1', 'XXX', 0, iUserApkihoncd, iUserShopcd, iTnshacd, iIpadrse, 'C1003B21' , '04-売上テーブル登録[完了] データ作成元区分:' || iDatsksmotokbn || '処理ID番号:' || wPrcsidno || 'AP基本コード：' || iApkihoncd || 'ショップコード：' || iShopcd );

  END UriageJohoTorikomi;

END C1003B21;
/
exit;
