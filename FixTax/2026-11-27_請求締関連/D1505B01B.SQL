CREATE OR REPLACE PACKAGE BODY       D1505B01
/*******************************************************************************
 * システム名       ：ＡＰ総合管理システム
 * 機能名           ：月次自動値引発生処理
 *
 *******************************************************************************
   Date       Author         Changes Key                                 Version
   -----------------------------------------------------------------------------
   2022-07-25 Isac           Create                                      1.0.0
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
    SELECT
        SYKM.MOJISETTEIATAI
    FROM
        S02M006T SYKM
    WHERE
        SYKM.APKIHONCD = iApKihoncd
    AND SYKM.SHOPCD    = iShopcd
    AND SYKM.SETTEINO  = '038'
    AND SYKM.RCRDKBN   = 'M'
    AND SYKM.YUKOKBN   = '1'
    ;

  BEGIN
    --システム管理マスタから運用日付の取得
    OPEN SELECT_OPERATIONDATE;
    LOOP
        FETCH SELECT_OPERATIONDATE INTO returnOperationdate;
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
  /* プロシージャ名   ：基本情報設定                                           */
  /* 引数             ：iApKihoncd           ＡＰ基本コード                    */
  /*                  ：iShopcd              ショップコード                    */
  /*                  ：iSkyshimedd          請求締日                          */
  /*                  ：iUserApKihoncd       UserAP基本コード                  */
  /*                  ：iUserShopcd          Userショップコード                */
  /*                  ：iTnshacd             担当者コード                      */
  /*                  ：iIpadrse             ＩＰアドレス                      */
  /*****************************************************************************/
  PROCEDURE SetUserInfo(iApKihoncd     IN VARCHAR2,
                        iShopcd        IN VARCHAR2,
                        iSkyshimedd    IN VARCHAR2,
                        iUserApKihoncd IN VARCHAR2,
                        iUserShopcd    IN VARCHAR2,
                        iTnshacd       IN VARCHAR2,
                        iIpadrse       IN VARCHAR2
                        ) IS

  BEGIN
    -- 共通変数削除
    vRecordS06T001T           := null;
    vRecordS06T002T           := null;
    vRecordS06T001T.APKIHONCD := iApKihoncd;    -- ＡＰ基本コード
    vRecordS06T001T.SHOPCD    := iShopcd;       -- ショップコード

    --システム日付 と システム時間の検索
    SELECT
        TO_CHAR(CURRENT_DATE, 'YYYYMMDD'), TO_CHAR(CURRENT_DATE, 'hh24miss')
    INTO
        vRecordS06T001T.KSHNSYSTMYMD, vRecordS06T001T.KSHNSYSTMJIKAN
    FROM DUAL;

    vRecordS06T001T.KOSHAAPKIHONCD := iUserApKihoncd;                           -- 更新者ＡＰ基本コード
    vRecordS06T001T.KOSHASHOPCD    := iUserShopcd;                              -- 更新者ショップコード
    vRecordS06T001T.KOSHACD        := iTnshacd;                                 -- 更新者コード
    vRecordS06T001T.KSHNUNYOYMD    := GetOperationdate(iApKihoncd, iShopcd);    -- 更新運用日付
    vRecordS06T001T.KSHNIPADRSE    := iIpadrse;                                 -- 更新ＩＰアドレス
    vRecordS06T001T.KSHNPGMID      := 'D1515B01';                               -- 更新プログラムＩＤ

    --インボイス施行フラグ設定
    SELECT
        SHPM.INVOICSHIKOYMD             --インボイス施行日
    INTO
        vRecordS02M046T.INVOICSHIKOYMD
    FROM
        S02M046T SHPM                   --ショップマスタ
    WHERE
        SHPM.APKIHONCD = iApKihoncd     --ＡＰ基本コード
    AND SHPM.SHOPCD    = iShopcd        --ショップコード
    ;

    --ショップマスタ.インボイス施行日 ＞ パラメータ.請求締日 の場合 "0"
    --ショップマスタ.インボイス施行日 ≦ パラメータ.請求締日 の場合 "1"
    IF (vRecordS02M046T.INVOICSHIKOYMD > iSkyshimedd) THEN
        vInvoiceShikoFlg := '0';
    ELSE
        vInvoiceShikoFlg := '1';
    END IF;
  END SetUserInfo;

  /*****************************************************************************/
  /* プロシージャ名   ：売上伝票番号検索                                       */
  /*****************************************************************************/
  PROCEDURE GetUridnpyno IS
    wUridnoyno1         VARCHAR2(11); -- 自動値引売上伝票番号から
    wUridnoyno2         VARCHAR2(11); -- 自動値引売上伝票番号まで

  BEGIN
    Z1804B01.getSlipNoUriage(vRecordS06T001T.Apkihoncd,
                             vRecordS06T001T.Shopcd,
                             1,
                             wUridnoyno1,
                             wUridnoyno2
                             );
    vRecordS06T001T.URIDNPYNO := wUridnoyno1;
  END GetUridnpyno;

  /*****************************************************************************/
  /* プロシージャ名   ：売上テーブル登録                                       */
  /*                                                                           */
  /* 「vRecordS06T001T」の内容をそのまま登録。各項目の値は事前に設定する。     */
  /*****************************************************************************/
  PROCEDURE InsertURGT IS

  BEGIN
    --売上テーブル
    INSERT INTO S06T001T (
        APKIHONCD,                  --ＡＰ基本コード
        SHOPCD,                     --ショップコード
        URIDNPYNO,                  --売上伝票番号
        EDANO,                      --枝番号
        ACPTDNPYNO,                 --受注伝票番号
        ACPTDNPYEDANO,              --受注伝票枝番号
        TORIHIKIKBN,                --取引区分
        TOKUISAKICD,                --得意先コード
        SIIRESAKICD,                --仕入先コード
        NYUKAMOTOCD,                --入荷元コード

        URIDD,                      --売上日
        ACPTDD,                     --受注日
        URIKIN,                     --売上金額
        SHOHITAXKIN,                --消費税金額
        SANKOUSHOHITAXKIN,          --参考消費税金額
        ACPTTNSHACD,                --受注担当者コード
        DLVRYTNSHACD,               --配達担当者コード
        DLVRYKANKATSUAPKIHONCD,     --配達管轄ＡＰ基本コード
        DLVRYKANKATSUSHOPCD,        --配達管轄ショップコード
        DLVRYKYOTENCD,              --配達拠点コード

        DLVRYKBN,                   --配達区分
        DLVRYCMPNYCD,               --配達会社コード
        SHUKKAMOTOKBN,              --出荷元区分
        EIGYOTNSHACD,               --営業担当者コード
        JISSEKITNSHACD,             --実績担当者コード
        SKYSAKICD,                  --請求先コード
        SKYSHIMEDYDM,               --請求締年月
        SKYSHIMEDD,                 --請求締日
        SKYPRCSJISSIDD,             --請求処理実施日
        GETSUJISHIMEDYDM,           --月次締年月

        GETSUJISHIMEDD,             --月次締日
        GETSUJIPRCSJISSIDD,         --月次処理実施日
        URISHIMEPRCSDD,             --売上締処理日
        NEBIKISHIMEDYDM,            --値引締年月
        NEBIKISHIMEDD,              --値引締日
        NEBIKIPRCSJISSIDD,          --値引処理実施日
        DNPYBIKO,                   --伝票備考
        DNPYNO,                     --伝票番号
        SKYSHOOTPTARINASHIKBN,      --請求書出力有無区分
        SKYPRCSJISSIKBN,            --請求処理実施区分

        DATSKSMOTOKBN,              --データ作成元区分
        TAXKEISANKBN,               --税計算区分
        SIIREDNPYNO,                --仕入伝票番号
        NYKNDNPYNO,                 --入金伝票番号
        NYKNKIN,                    --入金金額
        NYKNKIND,                   --入金種別
        YUKOKBN,                    --有効区分
        TOSHAAPKIHONCD,             --登録者ＡＰ基本コード
        TOSHASHOPCD,                --登録者ショップコード
        TOSHACD,                    --登録者コード

        INPTSYSTMYMD,               --登録システム日付
        INPTSYSTMJIKAN,             --登録システム時間
        INPTUNYOYMD,                --登録運用日付
        INPTIPADRSE,                --登録ＩＰアドレス
        INPTPGMID,                  --登録プログラムＩＤ
        KOSHAAPKIHONCD,             --更新者ＡＰ基本コード
        KOSHASHOPCD,                --更新者ショップコード
        KOSHACD,                    --更新者コード
        KSHNSYSTMYMD,               --更新システム日付
        KSHNSYSTMJIKAN,             --更新システム時間

        KSHNUNYOYMD,                --更新運用日付
        KSHNIPADRSE,                --更新ＩＰアドレス
        KSHNPGMID                   --更新プログラムＩＤ
    ) VALUES (
        vRecordS06T001T.APKIHONCD,                  --ＡＰ基本コード
        vRecordS06T001T.SHOPCD,                     --ショップコード
        vRecordS06T001T.URIDNPYNO,                  --売上伝票番号
        vRecordS06T001T.EDANO,                      --枝番号
        vRecordS06T001T.ACPTDNPYNO,                 --受注伝票番号
        vRecordS06T001T.ACPTDNPYEDANO,              --受注伝票枝番号
        vRecordS06T001T.TORIHIKIKBN,                --取引区分
        vRecordS06T001T.TOKUISAKICD,                --得意先コード
        vRecordS06T001T.SIIRESAKICD,                --仕入先コード
        vRecordS06T001T.NYUKAMOTOCD,                --入荷元コード

        vRecordS06T001T.URIDD,                      --売上日
        vRecordS06T001T.ACPTDD,                     --受注日
        vRecordS06T001T.URIKIN,                     --売上金額
        vRecordS06T001T.SHOHITAXKIN,                --消費税金額
        vRecordS06T001T.SANKOUSHOHITAXKIN,          --参考消費税金額
        vRecordS06T001T.ACPTTNSHACD,                --受注担当者コード
        vRecordS06T001T.DLVRYTNSHACD,               --配達担当者コード
        vRecordS06T001T.DLVRYKANKATSUAPKIHONCD,     --配達管轄ＡＰ基本コード
        vRecordS06T001T.DLVRYKANKATSUSHOPCD,        --配達管轄ショップコード
        vRecordS06T001T.DLVRYKYOTENCD,              --配達拠点コード

        vRecordS06T001T.DLVRYKBN,                   --配達区分
        vRecordS06T001T.DLVRYCMPNYCD,               --配達会社コード
        vRecordS06T001T.SHUKKAMOTOKBN,              --出荷元区分
        vRecordS06T001T.EIGYOTNSHACD,               --営業担当者コード
        vRecordS06T001T.JISSEKITNSHACD,             --実績担当者コード
        vRecordS06T001T.SKYSAKICD,                  --請求先コード
        vRecordS06T001T.SKYSHIMEDYDM,               --請求締年月
        vRecordS06T001T.SKYSHIMEDD,                 --請求締日
        vRecordS06T001T.SKYPRCSJISSIDD,             --請求処理実施日
        vRecordS06T001T.GETSUJISHIMEDYDM,           --月次締年月

        vRecordS06T001T.GETSUJISHIMEDD,             --月次締日
        vRecordS06T001T.GETSUJIPRCSJISSIDD,         --月次処理実施日
        vRecordS06T001T.URISHIMEPRCSDD,             --売上締処理日
        vRecordS06T001T.NEBIKISHIMEDYDM,            --値引締年月
        vRecordS06T001T.NEBIKISHIMEDD,              --値引締日
        vRecordS06T001T.NEBIKIPRCSJISSIDD,          --値引処理実施日
        vRecordS06T001T.DNPYBIKO,                   --伝票備考
        vRecordS06T001T.DNPYNO,                     --伝票番号
        vRecordS06T001T.SKYSHOOTPTARINASHIKBN,      --請求書出力有無区分
        vRecordS06T001T.SKYPRCSJISSIKBN,            --請求処理実施区分

        vRecordS06T001T.DATSKSMOTOKBN,              --データ作成元区分
        vRecordS06T001T.TAXKEISANKBN,               --税計算区分
        vRecordS06T001T.SIIREDNPYNO,                --仕入伝票番号
        vRecordS06T001T.NYKNDNPYNO,                 --入金伝票番号
        vRecordS06T001T.NYKNKIN,                    --入金金額
        vRecordS06T001T.NYKNKIND,                   --入金種別
        vRecordS06T001T.YUKOKBN,                    --有効区分
        vRecordS06T001T.KOSHAAPKIHONCD,             --登録者ＡＰ基本コード
        vRecordS06T001T.KOSHASHOPCD,                --登録者ショップコード
        vRecordS06T001T.KOSHACD,                    --登録者コード

        vRecordS06T001T.KSHNSYSTMYMD,               --登録システム日付
        vRecordS06T001T.KSHNSYSTMJIKAN,             --登録システム時間
        vRecordS06T001T.KSHNUNYOYMD,                --登録運用日付
        vRecordS06T001T.KSHNIPADRSE,                --登録ＩＰアドレス
        vRecordS06T001T.KSHNPGMID,                  --登録プログラムＩＤ
        vRecordS06T001T.KOSHAAPKIHONCD,             --更新者ＡＰ基本コード
        vRecordS06T001T.KOSHASHOPCD,                --更新者ショップコード
        vRecordS06T001T.KOSHACD,                    --更新者コード
        vRecordS06T001T.KSHNSYSTMYMD,               --更新システム日付
        vRecordS06T001T.KSHNSYSTMJIKAN,             --更新システム時間

        vRecordS06T001T.KSHNUNYOYMD,                --更新運用日付
        vRecordS06T001T.KSHNIPADRSE,                --更新ＩＰアドレス
        vRecordS06T001T.KSHNPGMID                   --更新プログラムＩＤ
        );
  END InsertURGT;

  /*****************************************************************************/
  /* プロシージャ名   ：売上明細テーブル登録                                   */
  /*****************************************************************************/
  PROCEDURE InsertUMST IS

  BEGIN
    --売上明細テーブル
    INSERT INTO S06T002T (
        APKIHONCD,              --ＡＰ基本コード
        SHOPCD,                 --ショップコード
        URIDNPYNO,              --売上伝票番号
        EDANO,                  --枝番号
        URIDNPYMEISAINO,        --売上伝票明細番号
        URIDNPYMEISAIEDANO,     --売上伝票明細枝番号
        LCTGRYCD,               --大分類コード
        MCTGRYCD,               --中分類コード
        SCTGRYCD,               --小分類コード
        SHNCD,                  --商品コード

        KEIJOKBN,               --計上区分
        SURYO,                  --数量
        HYOJUNUNT,              --標準単価
        UNT,                    --単価
        KIN,                    --金額
        UNTSHOHITAXKIN,         --単価消費税金額
        SHOHITAXKIN,            --消費税金額
        SANKOUSHOHITAXKIN,      --参考消費税金額
        TAXKBN,                 --税区分
        MEISAIKBN,              --明細区分

        NYURYOKUMEISAIFLG,      --入力明細フラグ
        TANPINMEISAIFLG,        --単品明細フラグ
        MEISAIBIKO,             --明細備考
        STSHNCD,                --セット商品コード
        KATASIKI,               --型式
        SIIRESURYO,             --仕入数量
        SIIREKIN,               --仕入金額
        SIIREUNT,               --仕入単価
        SIIRESHOHITAXKIN,       --仕入消費税金額
        ZAIKOKSHNSUMIFLG,       --在庫更新済フラグ
        TOKUISAKIKBN,           --得意先区分
        JUTAKUDLVRYCSTKBN,      --受託配達顧客区分
        ROYALTYSHIMEYMD,        --ロイヤリティ締め年月日

        YUKOKBN,                --有効区分
        TOSHAAPKIHONCD,         --登録者ＡＰ基本コード
        TOSHASHOPCD,            --登録者ショップコード
        TOSHACD,                --登録者コード
        INPTSYSTMYMD,           --登録システム日付
        INPTSYSTMJIKAN,         --登録システム時間
        INPTUNYOYMD,            --登録運用日付
        INPTIPADRSE,            --登録ＩＰアドレス
        INPTPGMID,              --登録プログラムＩＤ
        KOSHAAPKIHONCD,         --更新者ＡＰ基本コード

        KOSHASHOPCD,            --更新者ショップコード
        KOSHACD,                --更新者コード
        KSHNSYSTMYMD,           --更新システム日付
        KSHNSYSTMJIKAN,         --更新システム時間
        KSHNUNYOYMD,            --更新運用日付
        KSHNIPADRSE,            --更新ＩＰアドレス
        KSHNPGMID               --更新プログラムＩＤ
    ) VALUES (
        vRecordS06T001T.APKIHONCD,              --ＡＰ基本コード
        vRecordS06T001T.SHOPCD,                 --ショップコード
        vRecordS06T001T.URIDNPYNO,              --売上伝票番号
        vRecordS06T002T.EDANO,                  --枝番号
        vRecordS06T002T.URIDNPYMEISAINO,        --売上伝票明細番号
        vRecordS06T002T.URIDNPYMEISAIEDANO,     --売上伝票明細枝番号
        vRecordS06T002T.LCTGRYCD,               --大分類コード
        vRecordS06T002T.MCTGRYCD,               --中分類コード
        vRecordS06T002T.SCTGRYCD,               --小分類コード
        vRecordS06T002T.SHNCD,                  --商品コード

        vRecordS06T002T.KEIJOKBN,               --計上区分
        vRecordS06T002T.SURYO,                  --数量
        vRecordS06T002T.HYOJUNUNT,              --標準単価
        vRecordS06T002T.UNT,                    --単価
        vRecordS06T002T.KIN,                    --金額
        vRecordS06T002T.UNTSHOHITAXKIN,         --単価消費税金額
        vRecordS06T002T.SHOHITAXKIN,            --消費税金額
        vRecordS06T002T.SANKOUSHOHITAXKIN,      --参考消費税金額
        vRecordS06T002T.TAXKBN,                 --税区分
        vRecordS06T002T.MEISAIKBN,              --明細区分

        vRecordS06T002T.NYURYOKUMEISAIFLG,      --入力明細フラグ
        vRecordS06T002T.TANPINMEISAIFLG,        --単品明細フラグ
        vRecordS06T002T.MEISAIBIKO,             --明細備考
        vRecordS06T002T.STSHNCD,                --セット商品コード
        vRecordS06T002T.KATASIKI,               --型式
        vRecordS06T002T.SIIRESURYO,             --仕入数量
        vRecordS06T002T.SIIREKIN,               --仕入金額
        vRecordS06T002T.SIIREUNT,               --仕入単価
        vRecordS06T002T.SIIRESHOHITAXKIN,       --仕入消費税金額
        vRecordS06T002T.ZAIKOKSHNSUMIFLG,       -- 在庫更新済フラグ
        vRecordS06T002T.TOKUISAKIKBN,           --得意先区分
        vRecordS06T002T.JUTAKUDLVRYCSTKBN,      --受託配達顧客区分
        vRecordS06T002T.ROYALTYSHIMEYMD,        --ロイヤリティ締め年月日

        vRecordS06T002T.YUKOKBN,                --有効区分
        vRecordS06T001T.KOSHAAPKIHONCD,         --登録者ＡＰ基本コード
        vRecordS06T001T.KOSHASHOPCD,            --登録者ショップコード
        vRecordS06T001T.KOSHACD,                --登録者コード
        vRecordS06T001T.KSHNSYSTMYMD,           --登録システム日付
        vRecordS06T001T.KSHNSYSTMJIKAN,         --登録システム時間
        vRecordS06T001T.KSHNUNYOYMD,            --登録運用日付
        vRecordS06T001T.KSHNIPADRSE,            --登録ＩＰアドレス
        vRecordS06T001T.KSHNPGMID,              --登録プログラムＩＤ
        vRecordS06T001T.KOSHAAPKIHONCD,         --更新者ＡＰ基本コード

        vRecordS06T001T.KOSHASHOPCD,            --更新者ショップコード
        vRecordS06T001T.KOSHACD,                --更新者コード
        vRecordS06T001T.KSHNSYSTMYMD,           --更新システム日付
        vRecordS06T001T.KSHNSYSTMJIKAN,         --更新システム時間
        vRecordS06T001T.KSHNUNYOYMD,            --更新運用日付
        vRecordS06T001T.KSHNIPADRSE,            --更新ＩＰアドレス
        vRecordS06T001T.KSHNPGMID               --更新プログラムＩＤ
        );
  END InsertUMST;

  /*****************************************************************************/
  /* プロシージャ名   ：得意先実績テーブル更新                                 */
  /*****************************************************************************/
  PROCEDURE UpdateZd IS

  BEGIN
    UPDATE
        S02M050T TSJT
    SET
        TSJT.GENZAIZD       = TSJT.GENZAIZD + vRecordS06T001T.URIKIN + vRecordS06T001T.SHOHITAXKIN,     -- 現在残高
        TSJT.KOSHAAPKIHONCD = vRecordS06T001T.KOSHAAPKIHONCD,       -- 更新者ＡＰ基本コード
        TSJT.KOSHASHOPCD    = vRecordS06T001T.KOSHASHOPCD,          -- 更新者ショップコード
        TSJT.KOSHACD        = vRecordS06T001T.KOSHACD,              -- 更新者コード
        TSJT.KSHNSYSTMYMD   = vRecordS06T001T.KSHNSYSTMYMD,         -- 更新システム日付
        TSJT.KSHNSYSTMJIKAN = vRecordS06T001T.KSHNSYSTMJIKAN,       -- 更新システム時間
        TSJT.KSHNUNYOYMD    = vRecordS06T001T.KSHNUNYOYMD,          -- 更新運用日付
        TSJT.KSHNIPADRSE    = vRecordS06T001T.KSHNIPADRSE,          -- 更新ＩＰアドレス
        TSJT.KSHNPGMID      = vRecordS06T001T.KSHNPGMID             -- 更新プログラムＩＤ
    WHERE
        TSJT.APKIHONCD   = vRecordS06T001T.APKIHONCD            -- AP基本コード
    AND TSJT.SHOPCD      = vRecordS06T001T.SHOPCD               -- ショップコード
    AND TSJT.TOKUISAKICD = vRecordS06T001T.TOKUISAKICD;         -- 得意先コード
  END UpdateZd;

  /*****************************************************************************/
  /* プロシージャ名   ：自動値引データ作成の時、売上明細テーブルデータ設定     */
  /* 引数           ：iRecordS02M052T         自動値引マスタ                   */
  /*                  iTokuisakikbn           得意先区分                       */
  /*                  iJutakudlvrycstkbn      受託配達顧客区分                 */
  /*                  iNebikishrydYdmn        値引終了年月                     */
  /*****************************************************************************/
  PROCEDURE Setjidonebikikidata(iRecordS02M052T    IN S02M052T%rowtype,
                                iTokuisakikbn      IN VARCHAR2,
                                iJutakudlvrycstkbn IN VARCHAR2,
                                iNebikishrydYdmn   IN VARCHAR2
  ) IS
    iTaxsetteikbn S02V001T.TAXSETTEIKBN%TYPE;   -- 消費税計算用税設定区分
    iTaxkbn       S02V001T.TAXKBN%TYPE;         -- 消費税計算用税区分

    --商品マスタビューの検索
    CURSOR SELECT_SHMV IS
    SELECT
        SHMV.LCTGRYCD AS LCTGRYCD,          -- 大分類コード
        SHMV.MCTGRYCD AS MCTGRYCD,          -- 中分類コード
        SHMV.SCTGRYCD AS SCTGRYCD,          -- 小分類コード
        SHMV.TAXSETTEIKBN,                  -- 税設定区分
        SHMV.TAXKBN                         -- 税区分
    FROM
        S02V001T SHMV -- 商品マスタビュー
    WHERE
        SHMV.APKIHONCD = vRecordS06T001T.APKIHONCD
    AND SHMV.SHOPCD    = vRecordS06T001T.SHOPCD
    AND SHMV.SHNCD     = iRecordS02M052T.NEBIKIKEIJOSHNCD       -- 値引計上商品コード
    ;

  BEGIN
    --商品マスタビューの検索
    OPEN SELECT_SHMV;
    LOOP
        FETCH SELECT_SHMV
        INTO
            vRecordS06T002T.LCTGRYCD,       -- 売上明細テーブル.大分類コード
            vRecordS06T002T.MCTGRYCD,       -- 売上明細テーブル.中分類コード
            vRecordS06T002T.SCTGRYCD,       -- 売上明細テーブル.小分類コード
            iTaxsetteikbn,                  -- 消費税計算用税設定区分
            iTaxkbn                         -- 税区分
            ;
        EXIT WHEN SELECT_SHMV%NOTFOUND;
    END LOOP;
    CLOSE SELECT_SHMV;

    vRecordS06T002T.SHNCD     := iRecordS02M052T.NEBIKIKEIJOSHNCD;      --商品コード=値引計上商品コード
    vRecordS06T002T.HYOJUNUNT := iRecordS02M052T.NEBIKIUNT;             --標準単価
    vRecordS06T002T.UNT       := iRecordS02M052T.NEBIKIUNT;             --単価
    vRecordS06T002T.SURYO     := iRecordS02M052T.NEBIKIBTLHNS * -1;     --数量
    vRecordS06T002T.KIN       := iRecordS02M052T.NEBIKIKIN * -1;        --値引データの金額

    -- 得意先が卸対象の場合は、税計算区分を２：外税にセットする。
    IF (iTokuisakikbn IN ('2', '3', '4')) THEN
        vRecordS06T001T.TAXKEISANKBN := '2';
    END IF;

    IF iTaxkbn <> '5' THEN
        -- 税計算区分が２：外税の時は、“０”とする。
        IF vRecordS06T001T.TAXKEISANKBN = '2' THEN
            vRecordS06T002T.UNTSHOHITAXKIN := 0;
        ELSE
            -- 単価消費税金額:共通部品を使用して計算をする
            vRecordS06T002T.UNTSHOHITAXKIN :=
                Z1808B01.SyohizeiKeisan(
                    '1',                            --単体計算フラグ（する）
                    iTaxsetteikbn,                  --税設定区分
                    iTaxkbn,                        --税区分
                    '2',                            --税計算区分（外税）
                    vRecordS06T001T.URIDD,          --切り替え日：売上テーブルの売上日
                    vRecordS06T001T.APKIHONCD,      --AP基本コード
                    vRecordS06T001T.SHOPCD,         --ショップコード
                    NULL,                           --仕入先コード
                    NULL,                           --数量
                    iRecordS02M052T.NEBIKIUNT       --標準価格（自動値引マスタ.値引単価）
                    );
        END IF;

        -- 消費税金額:共通部品を使用して計算をする。
        vRecordS06T002T.SHOHITAXKIN :=
            Z1808B01.SyohizeiKeisan(
                '0',                                --単体計算フラグ（しない）
                iTaxsetteikbn,                      --税設定区分
                iTaxkbn,                            --消費税計算用税区分
                vRecordS06T001T.TAXKEISANKBN,       --税計算区分
                vRecordS06T001T.URIDD,              --切り替え日：売上テーブルの売上日
                vRecordS06T001T.APKIHONCD,          --AP基本コード
                vRecordS06T001T.SHOPCD,             --ショップコード
                NULL,                               --仕入先コード
                iRecordS02M052T.NEBIKIBTLHNS * -1,  --数量（自動値引マスタ.値引ボトル本数×-1）
                iRecordS02M052T.NEBIKIUNT           --金額（自動値引マスタ情報.値引単価）
                );
    ELSE
        Z1808B01.SetSyohizeiKeisan(
            iTaxsetteikbn,                          --税設定区分
            vRecordS06T001T.TAXKEISANKBN,           --税計算区分
            vRecordS06T001T.URIDD,                  --切り替え日
            vRecordS06T001T.APKIHONCD,              --AP基本コード
            vRecordS06T001T.SHOPCD,                 --ショップコード
            iRecordS02M052T.NEBIKIBTLHNS * -1,      --数量（自動値引マスタ.値引ボトル本数×-1）
            iRecordS02M052T.NEBIKIKEIJOSHNCD,       --商品コード（自動値引マスタ情報.値引計上商品コード）
            NULL,                                   --配達区分
            '01',                                   --計上区分
            vRecordS06T002T.UNTSHOHITAXKIN,         --戻り値：単価消費税
            vRecordS06T002T.SHOHITAXKIN             --戻り値：消費税
            );
    END IF;

    -- 売上テーブル.売上金額:売上伝票番号が同一の売上明細テーブルの金額の合計を設定する。
    vRecordS06T001T.URIKIN := NVL(vRecordS06T001T.URIKIN, 0) + NVL(vRecordS06T002T.KIN, 0);

    vRecordS06T002T.EDANO              := '0';      --枝番号
    vRecordS06T002T.URIDNPYMEISAIEDANO := '1';      --売上伝票明細枝番号
    vRecordS06T002T.KEIJOKBN           := '01';     --計上区分
    vRecordS06T002T.MEISAIKBN          := '0';      --明細区分
    vRecordS06T002T.NYURYOKUMEISAIFLG  := '1';      --入力明細フラグ
    vRecordS06T002T.TANPINMEISAIFLG    := '1';      --単品明細フラグ
    vRecordS06T002T.STSHNCD            := NULL;     --セット商品コード
    vRecordS06T002T.KATASIKI           := NULL;     --型式
    vRecordS06T002T.SIIRESURYO         := '0';      --仕入数量
    vRecordS06T002T.SIIREKIN           := '0';      --仕入金額
    vRecordS06T002T.SIIREUNT           := '0';      --仕入単価
    vRecordS06T002T.SIIRESHOHITAXKIN   := '0';      --仕入消費税金額
    vRecordS06T002T.ZAIKOKSHNSUMIFLG   := '1';      --在庫更新済フラグ
    vRecordS06T002T.YUKOKBN            := '1';      --有効区分
    vRecordS06T002T.TAXKBN             := iTaxkbn;  --税区分

    vRecordS06T002T.SURYO     := iRecordS02M052T.NEBIKIBTLHNS * -1;                             --数量（パラメータ.自動値引マスタ情報.値引ボトル本数 * -1）
    vRecordS06T002T.HYOJUNUNT := iRecordS02M052T.NEBIKIUNT;                                     --標準単価（パラメータ.自動値引マスタ情報.値引単価）
    vRecordS06T002T.UNT       := iRecordS02M052T.NEBIKIUNT;                                     --単価（パラメータ.自動値引マスタ情報.値引単価）
    vRecordS06T002T.KIN       := iRecordS02M052T.NEBIKIBTLHNS * iRecordS02M052T.NEBIKIUNT * -1; --金額（パラメータ.自動値引マスタ情報.値引ボトル本数 * パラメータ.自動値引マスタ情報.値引単価 * -1）
    
    vRecordS06T002T.TOKUISAKIKBN       := iTokuisakikbn;      -- 得意先区分
    vRecordS06T002T.JUTAKUDLVRYCSTKBN  := iJutakudlvrycstkbn; -- 受託配達顧客区分
    vRecordS06T002T.ROYALTYSHIMEYMD    := '00000000';         -- ロイヤリティ締め年月日

    IF (vInvoiceShikoFlg = '0') THEN
        --vRecordS06T002T.SHOHITAXKIN;                                          --消費税は上記で計算
        vRecordS06T002T.SANKOUSHOHITAXKIN := 0;                                 --参考消費税ゼロ
    ELSE
        vRecordS06T002T.SANKOUSHOHITAXKIN := vRecordS06T002T.SHOHITAXKIN;       --消費税を参考消費税へ設定
        vRecordS06T002T.SHOHITAXKIN       := 0;                                 --消費税ゼロ
    END IF;

    -- 値引終了年月＜「299912」の場合、末尾に終了年月を追加する
    IF iNebikishrydYdmn < '299912' THEN
        IF iRecordS02M052T.MEISAIBIKO IS NULL THEN
            vRecordS06T002T.MEISAIBIKO := LPAD(TO_CHAR(TO_DATE(iNebikishrydYdmn, 'YYYYMM'), 'YYYY/MM') || '迄適用', 40);
        ELSE
            vRecordS06T002T.MEISAIBIKO := RPAD(iRecordS02M052T.MEISAIBIKO, 27) || TO_CHAR(TO_DATE(iNebikishrydYdmn, 'YYYYMM'), 'YYYY/MM') || '迄適用';
        END IF;
    ELSIF iNebikishrydYdmn = '299912' OR iNebikishrydYdmn = '999912' THEN
        vRecordS06T002T.MEISAIBIKO := iRecordS02M052T.MEISAIBIKO;
    END IF;

    -- 売上明細テーブル登録
    InsertUMST;
  END Setjidonebikikidata;

  /*****************************************************************************/
  /* プロシージャ名   ：自動値引データ作成の時、売上明細テーブルデータ設定     */
  /* 引数           ：iShohincd               商品コード                       */
  /*                  iUrikin                 売上金額                         */
  /*****************************************************************************/
  PROCEDURE Setjidonebikikidata2(iShohincd IN VARCHAR2,
                                 iUrikin   IN VARCHAR2,
                                 iTokuisakikbn      IN VARCHAR2,
                                 -- modify 軽減税率変更対応_LI 20261127 Start
                                 iJutakudlvrycstkbn IN VARCHAR2,
                                 iCalcKirikaebi     IN VARCHAR2 DEFAULT NULL  -- 計算基準日（099904用）
                                 -- modify 軽減税率変更対応_LI 20261127 End
  ) IS
    iTaxsetteikbn S02V001T.TAXSETTEIKBN%TYPE;   -- 消費税計算用税設定区分
    iTaxkbn       S02V001T.TAXKBN%TYPE;         -- 消費税計算用税区分

    --商品マスタビューの検索
    CURSOR SELECT_SHMV IS
    SELECT
        SHMV.LCTGRYCD AS LCTGRYCD,          -- 大分類コード
        SHMV.MCTGRYCD AS MCTGRYCD,          -- 中分類コード
        SHMV.SCTGRYCD AS SCTGRYCD,          -- 小分類コード
        SHMV.TAXSETTEIKBN,                  -- 税設定区分
        SHMV.TAXKBN                         -- 税区分
    FROM
        S02V001T SHMV -- 商品マスタビュー
    WHERE
        SHMV.APKIHONCD = vRecordS06T001T.APKIHONCD
    AND SHMV.SHOPCD    = vRecordS06T001T.SHOPCD
    AND SHMV.SHNCD     = iShohincd          --商品コード
    ;

  BEGIN
    --商品マスタビューの検索
    OPEN SELECT_SHMV;
    LOOP
        FETCH SELECT_SHMV
        INTO
            vRecordS06T002T.LCTGRYCD,       -- 売上明細テーブル.大分類コード
            vRecordS06T002T.MCTGRYCD,       -- 売上明細テーブル.中分類コード
            vRecordS06T002T.SCTGRYCD,       -- 売上明細テーブル.小分類コード
            iTaxsetteikbn,                  -- 消費税計算用税設定区分
            iTaxkbn                         -- 税区分
            ;
        EXIT WHEN SELECT_SHMV%NOTFOUND;
    END LOOP;
    CLOSE SELECT_SHMV;

    -- 消費税金額:共通部品を使用して計算をする。
    vRecordS06T002T.SHOHITAXKIN :=
        Z1808B01.SyohizeiKeisan(
            '1',                                --単体計算フラグ
            iTaxsetteikbn,                      --税設定区分
            iTaxkbn,                            --消費税計算用税区分
            '2',                                --税計算区分
            -- modify 軽減税率変更対応_LI 20261127 Start
            NVL(iCalcKirikaebi, vRecordS06T001T.URIDD),  --切り替え日（指定時はその日付の税率で計算）
            -- modify 軽減税率変更対応_LI 20261127 End
            vRecordS06T001T.APKIHONCD,          --AP基本コード
            vRecordS06T001T.SHOPCD,             --ショップコード
            NULL,                               --仕入先コード
            NULL,                               --数量
            iUrikin                             --金額
            );

    vRecordS06T002T.APKIHONCD          := vRecordS06T001T.APKIHONCD;        --ＡＰ基本コード
    vRecordS06T002T.SHOPCD             := vRecordS06T001T.SHOPCD;           --ショップコード
    vRecordS06T002T.URIDNPYNO          := vRecordS06T001T.URIDNPYNO;        --売上伝票番号
    vRecordS06T002T.EDANO              := '0';                              --枝番号
--  vRecordS06T002T.URIDNPYMEISAINO    := '';                               --売上伝票明細番号（設定済み）
    vRecordS06T002T.URIDNPYMEISAIEDANO := '1';                              --売上伝票明細枝番号
--  vRecordS06T002T.LCTGRYCD           := '';                               --大分類コード（上の検索結果）
--  vRecordS06T002T.MCTGRYCD           := '';                               --中分類コード（上の検索結果）
--  vRecordS06T002T.SCTGRYCD           := '';                               --小分類コード（上の検索結果）
    vRecordS06T002T.SHNCD              := iShohincd;                        --商品コード

    vRecordS06T002T.KEIJOKBN           := '01';                             --計上区分
    vRecordS06T002T.SURYO              := -1;                               --数量
    vRecordS06T002T.HYOJUNUNT          := 0;                                --標準単価
    vRecordS06T002T.UNT                := 0;                                --単価
    vRecordS06T002T.KIN                := 0;                                --金額
    vRecordS06T002T.UNTSHOHITAXKIN     := 0;                                --単価消費税金額
--  vRecordS06T002T.SHOHITAXKIN        := '';                               --消費税金額（上の計算結果）
    vRecordS06T002T.SANKOUSHOHITAXKIN  := 0;                                --参考消費税金額
    vRecordS06T002T.TAXKBN             := iTaxkbn;                          --税区分
    vRecordS06T002T.MEISAIKBN          := '0';                              --明細区分

    vRecordS06T002T.NYURYOKUMEISAIFLG  := '1';                              --入力明細フラグ
    vRecordS06T002T.TANPINMEISAIFLG    := '1';                              --単品明細フラグ
    vRecordS06T002T.MEISAIBIKO         := NULL;                             --明細備考
    vRecordS06T002T.STSHNCD            := NULL;                             --セット商品コード
    vRecordS06T002T.KATASIKI           := NULL;                             --型式
    vRecordS06T002T.SIIRESURYO         := '0';                              --仕入数量
    vRecordS06T002T.SIIREKIN           := '0';                              --仕入金額
    vRecordS06T002T.SIIREUNT           := '0';                              --仕入単価
    vRecordS06T002T.SIIRESHOHITAXKIN   := '0';                              --仕入消費税金額
    vRecordS06T002T.ZAIKOKSHNSUMIFLG   := '1';                              --在庫更新済フラグ
    vRecordS06T002T.TOKUISAKIKBN       := iTokuisakikbn;                    -- 得意先区分
    vRecordS06T002T.JUTAKUDLVRYCSTKBN  := iJutakudlvrycstkbn;               -- 受託配達顧客区分
    vRecordS06T002T.ROYALTYSHIMEYMD    := '00000000';                       -- ロイヤリティ締め年月日

    vRecordS06T002T.YUKOKBN            := '1';                              --有効区分
    vRecordS06T002T.TOSHAAPKIHONCD     := vRecordS06T001T.TOSHAAPKIHONCD;   --登録者ＡＰ基本コード
    vRecordS06T002T.TOSHASHOPCD        := vRecordS06T001T.TOSHASHOPCD;      --登録者ショップコード
    vRecordS06T002T.TOSHACD            := vRecordS06T001T.TOSHACD;          --登録者コード
    vRecordS06T002T.INPTSYSTMYMD       := vRecordS06T001T.INPTSYSTMYMD;     --登録システム日付
    vRecordS06T002T.INPTSYSTMJIKAN     := vRecordS06T001T.INPTSYSTMJIKAN;   --登録システム時間
    vRecordS06T002T.INPTUNYOYMD        := vRecordS06T001T.INPTUNYOYMD;      --登録運用日付
    vRecordS06T002T.INPTIPADRSE        := vRecordS06T001T.INPTIPADRSE;      --登録ＩＰアドレス
    vRecordS06T002T.INPTPGMID          := vRecordS06T001T.INPTPGMID;        --登録プログラムＩＤ
    vRecordS06T002T.KOSHAAPKIHONCD     := vRecordS06T001T.KOSHAAPKIHONCD;   --更新者ＡＰ基本コード

    vRecordS06T002T.KOSHASHOPCD        := vRecordS06T001T.KOSHASHOPCD;      --更新者ショップコード
    vRecordS06T002T.KOSHACD            := vRecordS06T001T.KOSHACD;          --更新者コード
    vRecordS06T002T.KSHNSYSTMYMD       := vRecordS06T001T.KSHNSYSTMYMD;     --更新システム日付
    vRecordS06T002T.KSHNSYSTMJIKAN     := vRecordS06T001T.KSHNSYSTMJIKAN;   --更新システム時間
    vRecordS06T002T.KSHNUNYOYMD        := vRecordS06T001T.KSHNUNYOYMD;      --更新運用日付
    vRecordS06T002T.KSHNIPADRSE        := vRecordS06T001T.KSHNIPADRSE;      --更新ＩＰアドレス
    vRecordS06T002T.KSHNPGMID          := vRecordS06T001T.KSHNPGMID;        --更新プログラムＩＤ

    -- 売上明細テーブル登録
    InsertUMST;
  END Setjidonebikikidata2;

  /*****************************************************************************/
  /* プロシージャ名   ：自動値引データ作成の時、売上テーブルデータ設定         */
  /*****************************************************************************/
  PROCEDURE SetURGT IS

  BEGIN
--  vRecordS06T001T.APKIHONCD                                   --ＡＰ基本コード
--  vRecordS06T001T.SHOPCD                                      --ショップコード
--  vRecordS06T001T.URIDNPYNO                                   --売上伝票番号
    vRecordS06T001T.EDANO                  := '0';              --枝番号
    vRecordS06T001T.ACPTDNPYNO             := NULL;             --受注伝票番号
    vRecordS06T001T.ACPTDNPYEDANO          := NULL;             --受注伝票枝番号
    vRecordS06T001T.TORIHIKIKBN            := '1';              --取引区分
--  vRecordS06T001T.TOKUISAKICD                                 --得意先コード
    vRecordS06T001T.SIIRESAKICD            := NULL;             --仕入先コード
    vRecordS06T001T.NYUKAMOTOCD            := NULL;             --入荷元コード

--  vRecordS06T001T.URIDD                                       --売上日
    vRecordS06T001T.ACPTDD                 := '00000000';       --受注日
    vRecordS06T001T.URIKIN                 := 0;                --売上金額
    vRecordS06T001T.SHOHITAXKIN            := 0;                --消費税金額
    vRecordS06T001T.SANKOUSHOHITAXKIN      := 0;                --参考消費税金額
--  vRecordS06T001T.ACPTTNSHACD                                 --受注担当者コード
    vRecordS06T001T.DLVRYTNSHACD           := NULL;             --配達担当者コード
    vRecordS06T001T.DLVRYKANKATSUAPKIHONCD := NULL;             --配達管轄ＡＰ基本コード
    vRecordS06T001T.DLVRYKANKATSUSHOPCD    := NULL;             --配達管轄ショップコード
    vRecordS06T001T.DLVRYKYOTENCD          := NULL;             --配達拠点コード

    vRecordS06T001T.DLVRYKBN               := NULL;             --配達区分
    vRecordS06T001T.DLVRYCMPNYCD           := NULL;             --配達会社コード
    vRecordS06T001T.SHUKKAMOTOKBN          := '2';              --出荷元区分
--  vRecordS06T001T.EIGYOTNSHACD                                --営業担当者コード
    vRecordS06T001T.JISSEKITNSHACD         := NULL;             --実績担当者コード
--  vRecordS06T001T.SKYSAKICD                                   --請求先コード
    vRecordS06T001T.SKYSHIMEDYDM           := NULL;             --請求締年月
    vRecordS06T001T.SKYSHIMEDD             := NULL;             --請求締日
    vRecordS06T001T.SKYPRCSJISSIDD         := '00000000';       --請求処理実施日
    vRecordS06T001T.GETSUJISHIMEDYDM       := NULL;             --月次締年月

    vRecordS06T001T.GETSUJISHIMEDD         := NULL;             --月次締日
    vRecordS06T001T.GETSUJIPRCSJISSIDD    := '00000000';        --月次処理実施日
    vRecordS06T001T.URISHIMEPRCSDD        := GetOperationdate(vRecordS06T001T.APKIHONCD, vRecordS06T001T.SHOPCD);   --売上締処理日
--  vRecordS06T001T.NEBIKISHIMEDYDM                             --値引締年月
--  vRecordS06T001T.NEBIKISHIMEDD                               --値引締日
    vRecordS06T001T.NEBIKIPRCSJISSIDD     := GetOperationdate(vRecordS06T001T.APKIHONCD, vRecordS06T001T.SHOPCD);   --値引締処理日
    vRecordS06T001T.DNPYBIKO              := NULL;              --伝票備考
    vRecordS06T001T.DNPYNO                := NULL;              --伝票番号
    vRecordS06T001T.SKYSHOOTPTARINASHIKBN := '0';               --請求書出力有無区分
    vRecordS06T001T.SKYPRCSJISSIKBN       := NULL;              --請求処理実施区分

--  vRecordS06T001T.DATSKSMOTOKBN                               --データ作成元区分
--  vRecordS06T001T.TAXKEISANKBN                                --税計算区分
    vRecordS06T001T.SIIREDNPYNO           := NULL;              --仕入伝票番号
    vRecordS06T001T.NYKNDNPYNO            := NULL;              --入金伝票番号
    vRecordS06T001T.NYKNKIN               := NULL;              --入金金額
    vRecordS06T001T.NYKNKIND              := NULL;              --入金種別
    vRecordS06T001T.YUKOKBN               := '1';               --有効区分
--  vRecordS06T001T.TOSHAAPKIHONCD                              --登録者ＡＰ基本コード
--  vRecordS06T001T.TOSHASHOPCD                                 --登録者ショップコード
--  vRecordS06T001T.TOSHACD                                     --登録者コード

--  vRecordS06T001T.INPTSYSTMYMD                                --登録システム日付
--  vRecordS06T001T.INPTSYSTMJIKAN                              --登録システム時間
--  vRecordS06T001T.INPTUNYOYMD                                 --登録運用日付
--  vRecordS06T001T.INPTIPADRSE                                 --登録ＩＰアドレス
--  vRecordS06T001T.INPTPGMID                                   --登録プログラムＩＤ
--  vRecordS06T001T.KOSHAAPKIHONCD                              --更新者ＡＰ基本コード
--  vRecordS06T001T.KOSHASHOPCD                                 --更新者ショップコード
--  vRecordS06T001T.KOSHACD                                     --更新者コード
--  vRecordS06T001T.KSHNSYSTMYMD                                --更新システム日付
--  vRecordS06T001T.KSHNSYSTMJIKAN                              --更新システム時間

--  vRecordS06T001T.KSHNUNYOYMD                                 --更新運用日付
--  vRecordS06T001T.KSHNIPADRSE                                 --更新ＩＰアドレス
--  vRecordS06T001T.KSHNPGMID                                   --更新プログラムＩＤ
  END SetURGT;

  /*****************************************************************************/
  /* プロシージャ名   ：売上テーブルマイナス伝票作成                           */
  /* 引数             ：iTokuisakicd       得意先コード                        */
  /*                  ：iSkyshimedd        請求締日                            */
  /*                  ：iDataKbn           データ作成元区分                    */
  /*****************************************************************************/
  PROCEDURE Uriminusdnpysks(iTokuisakicd IN VARCHAR2,
                            iSkyshimedd  IN VARCHAR2,
                            iDataKbn     IN VARCHAR2
  ) IS
    wUridnpyno_TAB              t_arr_str50; -- 売上テーブルマイナス元売上伝票番号
    wEdano_TAB                  t_arr_str50; -- 売上テーブル.枝番号
    wTorihikikbn_TAB            t_arr_str50; -- 売上テーブル.取引区分
    wTokuisakicd_TAB            t_arr_str50; -- 売上テーブル.得意先コード
    wSiiresakicd_TAB            t_arr_str50; -- 売上テーブル.仕入先コード
    wNyukamotocd_TAB            t_arr_str50; -- 売上テーブル.入荷元コード
    wUridd_TAB                  t_arr_str50; -- 売上テーブル.売上日
    wAcptdd_TAB                 t_arr_str50; -- 売上テーブル.受注日
    wUrikin_TAB                 t_arr_str50; -- 売上テーブル.売上金額
    wShohitaxkin_TAB            t_arr_str50; -- 売上テーブル.消費税金額

    wSankoShohitaxkin_TAB       t_arr_str50; -- 売上テーブル.参考消費税金額
    wAcpttnshacd_TAB            t_arr_str50; -- 売上テーブル.受注担当者コード
    wDlvrytnshacd_TAB           t_arr_str50; -- 売上テーブル.配達担当者コード
    wDlvrykankatsuapkihoncd_TAB t_arr_str50; -- 売上テーブル.配達管轄ＡＰ基本コード
    wDlvrykankatsushopcd_TAB    t_arr_str50; -- 売上テーブル.配達管轄ショップコード
    wDlvrykyotencd_TAB          t_arr_str50; -- 売上テーブル.配達拠点コード
    wDlvrykbn_TAB               t_arr_str50; -- 売上テーブル.配達区分
    wDlvrycmpnycd_TAB           t_arr_str50; -- 売上テーブル.配達会社コード
    wShukkamotokbn_TAB          t_arr_str50; -- 売上テーブル.出荷元区分
    wEigyotnshacd_TAB           t_arr_str50; -- 売上テーブル.営業担当者コード

    wJissekitnshacd_TAB         t_arr_str50; -- 売上テーブル.実績担当者コード
    wSkysakicd_TAB              t_arr_str50; -- 売上テーブル.請求先コード
    wSkyshimedydm_TAB           t_arr_str50; -- 売上テーブル.請求締年月
    wSkyshimedd_TAB             t_arr_str50; -- 売上テーブル.請求締日
    wSkyprcsjissidd_TAB         t_arr_str50; -- 売上テーブル.請求処理実施日
    wGetsujishimedydm_TAB       t_arr_str50; -- 売上テーブル.月次締年月
    wGetsujishimedd_TAB         t_arr_str50; -- 売上テーブル.月次締日
    wGetsujiprcsjissidd_TAB     t_arr_str50; -- 売上テーブル.月次処理実施日
    wUrishimeprcsdd_TAB         t_arr_str50; -- 売上テーブル.売上締処理日
    wDnpybiko_TAB               t_arr_str50; -- 売上テーブル.伝票備考

    wSkyshootptarinashikbn_TAB  t_arr_str50; -- 売上テーブル.請求書出力有無区分
    wDatsksmotokbn_TAB          t_arr_str50; -- 売上テーブル.データ作成元区分
    wTaxkeisankbn_TAB           t_arr_str50; -- 売上テーブル.税計算区分
    wYukokbn_TAB                t_arr_str50; -- 売上テーブル.有効区分

    wUridnpymeisaino_TAB    t_arr_str50; -- 売上明細テーブル.売上伝票明細番号
    wUridnpymeisaiedano_TAB t_arr_str50; -- 売上明細テーブル.売上伝票明細枝番号
    wLctgrycd_TAB           t_arr_str50; -- 売上明細テーブル.大分類コード
    wMctgrycd_TAB           t_arr_str50; -- 売上明細テーブル.中分類コード
    wSctgrycd_TAB           t_arr_str50; -- 売上明細テーブル.小分類コード
    wShncd_TAB              t_arr_str50; -- 売上明細テーブル.商品コード
    wKeijokbn_TAB           t_arr_str50; -- 売上明細テーブル.計上区分
    wSuryo_TAB              t_arr_str50; -- 売上明細テーブル.数量
    wHyojununt_TAB          t_arr_str50; -- 売上明細テーブル.標準単価
    wUnt_TAB                t_arr_str50; -- 売上明細テーブル.単価

    wKin_TAB                t_arr_str50; -- 売上明細テーブル.金額
    wUntshohitaxkin_TAB     t_arr_str50; -- 売上明細テーブル.単価消費税金額
    wShohitaxkin2_TAB       t_arr_str50; -- 売上明細テーブル.消費税金額
    wSankoShohitaxkin2_TAB  t_arr_str50; -- 売上明細テーブル.参考消費税金額
    wTaxkbn_TAB             t_arr_str50; -- 売上明細テーブル.税区分
    wMeisaikbn_TAB          t_arr_str50; -- 売上明細テーブル.明細区分
    wNyuryokumeisaiflg_TAB  t_arr_str50; -- 売上明細テーブル.入力明細フラグ
    wTanpinmeisaiflg_TAB    t_arr_str50; -- 売上明細テーブル.単品明細フラグ
    wMeisaibiko_TAB         t_arr_str50; -- 売上明細テーブル.明細備考
    wStshncd_TAB            t_arr_str50; -- 売上明細テーブル.セット商品コード

    wKatasiki_TAB           t_arr_str50; -- 売上明細テーブル.型式
    wSiiresuryo_TAB         t_arr_str50; -- 売上明細テーブル.仕入数量
    wSiirekin_TAB           t_arr_str50; -- 売上明細テーブル.仕入金額
    wSiireunt_TAB           t_arr_str50; -- 売上明細テーブル.仕入単価
    wSiireshohitaxkin_TAB   t_arr_str50; -- 売上明細テーブル.仕入消費税金額
    wZaikokshnsumiflg_TAB   t_arr_str50; -- 売上明細テーブル.在庫更新済フラグ
    wTokuisakikbn_TAB       t_arr_str50; -- 売上明細テーブル.得意先区分
    wJutakudlvrycstkbn_TAB  t_arr_str50; -- 売上明細テーブル.受託配達顧客区分
    wYukokbn2_TAB           t_arr_str50; -- 売上明細テーブル.有効区分

    -- 売上マイナス対象データ抽出
    CURSOR SELECT_URGT IS
    SELECT /*+ LEADING(SSMW SKSM TKSM URGT UMST)*/
        URGT.URIDNPYNO,                 -- 売上テーブル.売上伝票番号
        URGT.EDANO,                     -- 売上テーブル.枝番号
        URGT.TORIHIKIKBN,               -- 売上テーブル.取引区分
        URGT.TOKUISAKICD,               -- 売上テーブル.得意先コード
        URGT.SIIRESAKICD,               -- 売上テーブル.仕入先コード
        URGT.NYUKAMOTOCD,               -- 売上テーブル.入荷元コード
        URGT.URIDD,                     -- 売上テーブル.売上日
        URGT.ACPTDD,                    -- 売上テーブル.受注日
        URGT.URIKIN,                    -- 売上テーブル.売上金額
        URGT.SHOHITAXKIN,               -- 売上テーブル.消費税金額

        URGT.SANKOUSHOHITAXKIN,         -- 売上テーブル.参考消費税金額
        URGT.ACPTTNSHACD,               -- 売上テーブル.受注担当者コード
        URGT.DLVRYTNSHACD,              -- 売上テーブル.配達担当者コード
        URGT.DLVRYKANKATSUAPKIHONCD,    -- 売上テーブル.配達管轄ＡＰ基本コード
        URGT.DLVRYKANKATSUSHOPCD,       -- 売上テーブル.配達管轄ショップコード
        URGT.DLVRYKYOTENCD,             -- 売上テーブル.配達拠点コード
        URGT.DLVRYKBN,                  -- 売上テーブル.配達区分
        URGT.DLVRYCMPNYCD,              -- 売上テーブル.配達会社コード
        URGT.SHUKKAMOTOKBN,             -- 売上テーブル.出荷元区分
        URGT.EIGYOTNSHACD,              -- 売上テーブル.営業担当者コード

        URGT.JISSEKITNSHACD,            -- 売上テーブル.実績担当者コード
        URGT.SKYSAKICD,                 -- 売上テーブル.請求先コード
        URGT.SKYSHIMEDYDM,              -- 売上テーブル.請求締年月
        URGT.SKYSHIMEDD,                -- 売上テーブル.請求締日
        URGT.SKYPRCSJISSIDD,            -- 売上テーブル.請求処理実施日
        URGT.GETSUJISHIMEDYDM,          -- 売上テーブル.月次締年月
        URGT.GETSUJISHIMEDD,            -- 売上テーブル.月次締日
        URGT.GETSUJIPRCSJISSIDD,        -- 売上テーブル.月次処理実施日
        URGT.URISHIMEPRCSDD,            -- 売上テーブル.売上締処理日
        URGT.DNPYBIKO,                  -- 売上テーブル.伝票備考

        URGT.SKYSHOOTPTARINASHIKBN,     -- 売上テーブル.請求書出力有無区分
        URGT.DATSKSMOTOKBN,             -- 売上テーブル.データ作成元区分
        URGT.TAXKEISANKBN,              -- 売上テーブル.税計算区分
        URGT.YUKOKBN,                   -- 売上テーブル.有効区分

        UMST.URIDNPYMEISAINO,       -- 売上明細テーブル.売上伝票明細番号
        UMST.URIDNPYMEISAIEDANO,    -- 売上明細テーブル.売上伝票明細枝番号
        UMST.LCTGRYCD,              -- 売上明細テーブル.大分類コード
        UMST.MCTGRYCD,              -- 売上明細テーブル.中分類コード
        UMST.SCTGRYCD,              -- 売上明細テーブル.小分類コード
        UMST.SHNCD,                 -- 売上明細テーブル.商品コード
        UMST.KEIJOKBN,              -- 売上明細テーブル.計上区分
        UMST.SURYO,                 -- 売上明細テーブル.数量
        UMST.HYOJUNUNT,             -- 売上明細テーブル.標準単価
        UMST.UNT,                   -- 売上明細テーブル.単価

        UMST.KIN,                   -- 売上明細テーブル.金額
        UMST.UNTSHOHITAXKIN,        -- 売上明細テーブル.単価消費税金額
        UMST.SHOHITAXKIN,           -- 売上明細テーブル.消費税金額
        UMST.SANKOUSHOHITAXKIN,     -- 売上明細テーブル.参考消費税金額
        UMST.TAXKBN,                -- 売上明細テーブル.税区分
        UMST.MEISAIKBN,             -- 売上明細テーブル.明細区分
        UMST.NYURYOKUMEISAIFLG,     -- 売上明細テーブル.入力明細フラグ
        UMST.TANPINMEISAIFLG,       -- 売上明細テーブル.単品明細フラグ
        UMST.MEISAIBIKO,            -- 売上明細テーブル.明細備考
        UMST.STSHNCD,               -- 売上明細テーブル.セット商品コード

        UMST.KATASIKI,              -- 売上明細テーブル.型式
        UMST.SIIRESURYO,            -- 売上明細テーブル.仕入数量
        UMST.SIIREKIN,              -- 売上明細テーブル.仕入金額
        UMST.SIIREUNT,              -- 売上明細テーブル.仕入単価
        UMST.SIIRESHOHITAXKIN,      -- 売上明細テーブル.仕入消費税金額
        UMST.ZAIKOKSHNSUMIFLG,      -- 売上明細テーブル.在庫更新済フラグ
        UMST.TOKUISAKIKBN,          -- 売上明細テーブル.得意先区分
        UMST.JUTAKUDLVRYCSTKBN,     -- 売上明細テーブル.受託配達顧客区分
        UMST.YUKOKBN                -- 売上明細テーブル.有効区分
    FROM
        S06T001T URGT,  -- 売上テーブル
        S06T002T UMST   -- 売上明細テーブル
    WHERE
        URGT.APKIHONCD       = vRecordS06T001T.APKIHONCD                                --ＡＰ基本コード
    AND URGT.SHOPCD          = vRecordS06T001T.SHOPCD                                   --ショップコード
    AND URGT.TOKUISAKICD     = iTokuisakicd                                             --得意先マスタ.得意先コード
    AND URGT.NEBIKISHIMEDYDM = TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'YYYYMM')      --値引締年月＝請求締日の年月
    AND URGT.NEBIKISHIMEDD   = TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'DD')          --値引締日＝請求締日の日
    AND URGT.DATSKSMOTOKBN   = iDataKbn                                                 --データ作成元区分
    AND URGT.DNPYNO          IS NULL                                                    --取消伝票番号
    AND URGT.APKIHONCD       = UMST.APKIHONCD                                           --ＡＰ基本コード
    AND URGT.SHOPCD          = UMST.SHOPCD                                              --ショップコード
    AND URGT.URIDNPYNO       = UMST.URIDNPYNO                                           --売上伝票番号
    AND URGT.EDANO           = UMST.EDANO                                               --枝番号
    AND URGT.YUKOKBN         = '1'
    AND UMST.YUKOKBN         = '1'
    -- ACCIS2023-6956 start ABI 取消対象となる売上データを取得する際のソート条件を追加
    ORDER BY
        UMST.URIDNPYMEISAINO
        ,UMST.URIDNPYMEISAIEDANO
    -- ACCIS2023-6956 end ABI
    ;

  BEGIN
    -- 売上マイナス対象データ抽出
    OPEN SELECT_URGT;
    FETCH SELECT_URGT BULK COLLECT
    INTO
        wUridnpyno_TAB,                 -- 売上テーブル.売上伝票番号
        wEdano_TAB,                     -- 売上テーブル.枝番号
        wTorihikikbn_TAB,               -- 売上テーブル.取引区分
        wTokuisakicd_TAB,               -- 売上テーブル.得意先コード
        wSiiresakicd_TAB,               -- 売上テーブル.仕入先コード
        wNyukamotocd_TAB,               -- 売上テーブル.入荷元コード
        wUridd_TAB,                     -- 売上テーブル.売上日
        wAcptdd_TAB,                    -- 売上テーブル.受注日
        wUrikin_TAB,                    -- 売上テーブル.売上金額
        wShohitaxkin_TAB,               -- 売上テーブル.消費税金額

        wSankoShohitaxkin_TAB,          -- 売上テーブル.参考消費税金額
        wAcpttnshacd_TAB,               -- 売上テーブル.受注担当者コード
        wDlvrytnshacd_TAB,              -- 売上テーブル.配達担当者コード
        wDlvrykankatsuapkihoncd_TAB,    -- 売上テーブル.配達管轄ＡＰ基本コード
        wDlvrykankatsushopcd_TAB,       -- 売上テーブル.配達管轄ショップコード
        wDlvrykyotencd_TAB,             -- 売上テーブル.配達拠点コード
        wDlvrykbn_TAB,                  -- 売上テーブル.配達区分
        wDlvrycmpnycd_TAB,              -- 売上テーブル.配達会社コード
        wShukkamotokbn_TAB,             -- 売上テーブル.出荷元区分
        wEigyotnshacd_TAB,              -- 売上テーブル.営業担当者コード

        wJissekitnshacd_TAB,            -- 売上テーブル.実績担当者コード
        wSkysakicd_TAB,                 -- 売上テーブル.請求先コード
        wSkyshimedydm_TAB,              -- 売上テーブル.請求締年月
        wSkyshimedd_TAB,                -- 売上テーブル.請求締日
        wSkyprcsjissidd_TAB,            -- 売上テーブル.請求処理実施日
        wGetsujishimedydm_TAB,          -- 売上テーブル.月次締年月
        wGetsujishimedd_TAB,            -- 売上テーブル.月次締日
        wGetsujiprcsjissidd_TAB,        -- 売上テーブル.月次処理実施日
        wUrishimeprcsdd_TAB,            -- 売上テーブル.売上締処理日
        wDnpybiko_TAB,                  -- 売上テーブル.伝票備考

        wSkyshootptarinashikbn_TAB,     -- 売上テーブル.請求書出力有無区分
        wDatsksmotokbn_TAB,             -- 売上テーブル.データ作成元区分
        wTaxkeisankbn_TAB,              -- 売上テーブル.税計算区分
        wYukokbn_TAB,                   -- 売上テーブル.有効区分

        wUridnpymeisaino_TAB,       -- 売上明細テーブル.売上伝票明細番号
        wUridnpymeisaiedano_TAB,    -- 売上明細テーブル.売上伝票明細枝番号
        wLctgrycd_TAB,              -- 売上明細テーブル.大分類コード
        wMctgrycd_TAB,              -- 売上明細テーブル.中分類コード
        wSctgrycd_TAB,              -- 売上明細テーブル.小分類コード
        wShncd_TAB,                 -- 売上明細テーブル.商品コード
        wKeijokbn_TAB,              -- 売上明細テーブル.計上区分
        wSuryo_TAB,                 -- 売上明細テーブル.数量
        wHyojununt_TAB,             -- 売上明細テーブル.標準単価
        wUnt_TAB,                   -- 売上明細テーブル.単価

        wKin_TAB,                   -- 売上明細テーブル.金額
        wUntshohitaxkin_TAB,        -- 売上明細テーブル.単価消費税金額
        wShohitaxkin2_TAB,          -- 売上明細テーブル.消費税金額
        wSankoShohitaxkin2_TAB,     -- 売上明細テーブル.参考消費税金額
        wTaxkbn_TAB,                -- 売上明細テーブル.税区分
        wMeisaikbn_TAB,             -- 売上明細テーブル.明細区分
        wNyuryokumeisaiflg_TAB,     -- 売上明細テーブル.入力明細フラグ
        wTanpinmeisaiflg_TAB,       -- 売上明細テーブル.単品明細フラグ
        wMeisaibiko_TAB,            -- 売上明細テーブル.明細備考
        wStshncd_TAB,               -- 売上明細テーブル.セット商品コード

        wKatasiki_TAB,              -- 売上明細テーブル.型式
        wSiiresuryo_TAB,            -- 売上明細テーブル.仕入数量
        wSiirekin_TAB,              -- 売上明細テーブル.仕入金額
        wSiireunt_TAB,              -- 売上明細テーブル.仕入単価
        wSiireshohitaxkin_TAB,      -- 売上明細テーブル.仕入消費税金額
        wZaikokshnsumiflg_TAB,      -- 売上明細テーブル.在庫更新済フラグ
        wTokuisakikbn_TAB,          -- 売上明細テーブル.得意先区分
        wJutakudlvrycstkbn_TAB,     -- 売上明細テーブル.受託配達顧客区分
        wYukokbn2_TAB               -- 売上明細テーブル.有効区分
        ;

    FOR i IN 1 .. wUridnpyno_TAB.COUNT LOOP
        vRecordS06T001T.EDANO                  := wEdano_TAB(i);
        vRecordS06T001T.TORIHIKIKBN            := wTorihikikbn_TAB(i);
        vRecordS06T001T.TOKUISAKICD            := wTokuisakicd_TAB(i);
        vRecordS06T001T.SIIRESAKICD            := wSiiresakicd_TAB(i);
        vRecordS06T001T.NYUKAMOTOCD            := wNyukamotocd_TAB(i);
        vRecordS06T001T.URIDD                  := wUridd_TAB(i);
        vRecordS06T001T.ACPTDD                 := wAcptdd_TAB(i);
        vRecordS06T001T.URIKIN                 := wUrikin_TAB(i);
        vRecordS06T001T.SHOHITAXKIN            := wShohitaxkin_TAB(i);
        vRecordS06T001T.SANKOUSHOHITAXKIN      := wSankoShohitaxkin_TAB(i);       --参考消費税

        vRecordS06T001T.ACPTTNSHACD            := wAcpttnshacd_TAB(i);
        vRecordS06T001T.DLVRYTNSHACD           := wDlvrytnshacd_TAB(i);
        vRecordS06T001T.DLVRYKANKATSUAPKIHONCD := wDlvrykankatsuapkihoncd_TAB(i);
        vRecordS06T001T.DLVRYKANKATSUSHOPCD    := wDlvrykankatsushopcd_TAB(i);
        vRecordS06T001T.DLVRYKYOTENCD          := wDlvrykyotencd_TAB(i);
        vRecordS06T001T.DLVRYKBN               := wDlvrykbn_TAB(i);
        vRecordS06T001T.DLVRYCMPNYCD           := wDlvrycmpnycd_TAB(i);
        vRecordS06T001T.SHUKKAMOTOKBN          := wShukkamotokbn_TAB(i);
        vRecordS06T001T.EIGYOTNSHACD           := wEigyotnshacd_TAB(i);
        vRecordS06T001T.JISSEKITNSHACD         := wJissekitnshacd_TAB(i);

        vRecordS06T001T.SKYSAKICD              := wSkysakicd_TAB(i);
        vRecordS06T001T.SKYSHIMEDYDM           := NULL;       --'000000';
        vRecordS06T001T.SKYSHIMEDD             := NULL;       --'00';
        vRecordS06T001T.SKYPRCSJISSIDD         := '00000000';
        vRecordS06T001T.GETSUJISHIMEDYDM       := NULL;       --'000000';
        vRecordS06T001T.GETSUJISHIMEDD         := NULL;       --'00';
        vRecordS06T001T.GETSUJIPRCSJISSIDD     := '00000000';
        vRecordS06T001T.URISHIMEPRCSDD         := wUrishimeprcsdd_TAB(i);
        vRecordS06T001T.DNPYBIKO               := wDnpybiko_TAB(i);
        vRecordS06T001T.SKYSHOOTPTARINASHIKBN  := wSkyshootptarinashikbn_TAB(i);

        vRecordS06T001T.DATSKSMOTOKBN          := wDatsksmotokbn_TAB(i);
        vRecordS06T001T.TAXKEISANKBN           := wTaxkeisankbn_TAB(i);
        vRecordS06T001T.YUKOKBN                := wYukokbn_TAB(i);

        vRecordS06T002T.URIDNPYMEISAINO    := wUridnpymeisaino_TAB(i);
        vRecordS06T002T.URIDNPYMEISAIEDANO := wUridnpymeisaiedano_TAB(i);
        vRecordS06T002T.LCTGRYCD           := wLctgrycd_TAB(i);
        vRecordS06T002T.MCTGRYCD           := wMctgrycd_TAB(i);
        vRecordS06T002T.SCTGRYCD           := wSctgrycd_TAB(i);
        vRecordS06T002T.SHNCD              := wShncd_TAB(i);
        vRecordS06T002T.KEIJOKBN           := wKeijokbn_TAB(i);
        vRecordS06T002T.SURYO              := wSuryo_TAB(i);
        vRecordS06T002T.HYOJUNUNT          := wHyojununt_TAB(i);
        vRecordS06T002T.UNT                := wUnt_TAB(i);

        vRecordS06T002T.KIN                := wKin_TAB(i);
        vRecordS06T002T.UNTSHOHITAXKIN     := wUntshohitaxkin_TAB(i);
        vRecordS06T002T.SHOHITAXKIN        := wShohitaxkin2_TAB(i);
        vRecordS06T002T.SANKOUSHOHITAXKIN  := wSankoShohitaxkin2_TAB(i);          --参考消費税
        vRecordS06T002T.TAXKBN             := wTaxkbn_TAB(i);
        vRecordS06T002T.MEISAIKBN          := wMeisaikbn_TAB(i);
        vRecordS06T002T.NYURYOKUMEISAIFLG  := wNyuryokumeisaiflg_TAB(i);
        vRecordS06T002T.TANPINMEISAIFLG    := wTanpinmeisaiflg_TAB(i);
        vRecordS06T002T.MEISAIBIKO         := wMeisaibiko_TAB(i);
        vRecordS06T002T.STSHNCD            := wStshncd_TAB(i);

        vRecordS06T002T.KATASIKI           := wKatasiki_TAB(i);
        vRecordS06T002T.SIIRESURYO         := wSiiresuryo_TAB(i);
        vRecordS06T002T.SIIREKIN           := wSiirekin_TAB(i);
        vRecordS06T002T.SIIREUNT           := wSiireunt_TAB(i);
        vRecordS06T002T.SIIRESHOHITAXKIN   := wSiireshohitaxkin_TAB(i);
        vRecordS06T002T.ZAIKOKSHNSUMIFLG   := wZaikokshnsumiflg_TAB(i);
        vRecordS06T002T.TOKUISAKIKBN       := wTokuisakikbn_TAB(i);
        vRecordS06T002T.JUTAKUDLVRYCSTKBN  := wJutakudlvrycstkbn_TAB(i);
        vRecordS06T002T.ROYALTYSHIMEYMD    := '00000000';
        vRecordS06T002T.YUKOKBN            := wYukokbn2_TAB(i);

        IF wUridnpymeisaino_TAB(i) = 1 THEN
            -- TYYMM999999のYYMMは年月、999999は連番,自動採番はＯＲＡＣＬＥ用採番共通部品を呼び出します。
            --戻り値を「vRecordS06T001T.URIDNPYNO」へ設定
            GetUridnpyno;

            -- 売上テーブルマイナス元伝票更新
            UPDATE S06T001T URGT -- 売上テーブル
            SET
                URGT.DNPYNO                = vRecordS06T001T.URIDNPYNO,         -- 伝票番号
                URGT.SKYSHOOTPTARINASHIKBN = '1',                               -- 請求書出力有無区分
                URGT.KOSHAAPKIHONCD        = vRecordS06T001T.KOSHAAPKIHONCD,    -- 更新者ＡＰ基本コード
                URGT.KOSHASHOPCD           = vRecordS06T001T.KOSHASHOPCD,       -- 更新者ショップコード
                URGT.KOSHACD               = vRecordS06T001T.KOSHACD,           -- 更新者コード
                URGT.KSHNSYSTMYMD          = vRecordS06T001T.KSHNSYSTMYMD,      -- 更新システム日付: システム日付
                URGT.KSHNSYSTMJIKAN        = vRecordS06T001T.KSHNSYSTMJIKAN,    -- 更新システム時間: システム時間
                URGT.KSHNUNYOYMD           = vRecordS06T001T.KSHNUNYOYMD,       -- 更新運用日付: システム管理マスタ.文字設定値
                URGT.KSHNIPADRSE           = vRecordS06T001T.KSHNIPADRSE,       -- 更新ＩＰアドレス: パラメータ．更新ＩＰアドレス
                URGT.KSHNPGMID             = vRecordS06T001T.KSHNPGMID          -- 更新プログラムＩＤ
            WHERE
                URGT.APKIHONCD = vRecordS06T001T.APKIHONCD
            AND URGT.SHOPCD    = vRecordS06T001T.SHOPCD
            AND URGT.URIDNPYNO = wUridnpyno_TAB(i)              --売上伝票番号
            --AND URGT.EDANO     = vRecordS06T001T.EDANO          --枝番号
            ;

            -- 売上テーブルマイナス伝票作成
            vRecordS06T001T.EDANO                 := 0;                                                     --枝番号
            vRecordS06T001T.URIKIN                := NVL(vRecordS06T001T.URIKIN, 0) * -1;                   --売上金額
            vRecordS06T001T.SHOHITAXKIN           := NVL(vRecordS06T001T.SHOHITAXKIN, 0) * -1;              --消費税金額
            vRecordS06T001T.SANKOUSHOHITAXKIN     := NVL(vRecordS06T001T.SANKOUSHOHITAXKIN, 0) * -1;        --参考消費税金額
            vRecordS06T001T.SKYPRCSJISSIKBN       := NULL;                                                  --請求処理実施区分
            vRecordS06T001T.DNPYNO                := wUridnpyno_TAB(i);                                     --売上テーブル.伝票番号
            vRecordS06T001T.SKYSHOOTPTARINASHIKBN := '1';                                                   --請求書出力有無区分
            vRecordS06T001T.NEBIKISHIMEDYDM       := TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'YYYYMM');   --値引締年月（請求締日の年月）
            vRecordS06T001T.NEBIKISHIMEDD         := TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'DD');       --値引締日（請求締日の日）
            vRecordS06T001T.NEBIKIPRCSJISSIDD     := vRecordS06T001T.KSHNUNYOYMD;                           --値引処理実施日（運用日付）
            -- 売上テーブル登録
            InsertURGT;

            --得意先実績テーブル更新
            UpdateZd;
        END IF;

        -- 売上明細テーブルマイナス伝票作成
        vRecordS06T002T.EDANO             := 0;                                                --枝番号
        vRecordS06T002T.SURYO             := NVL(vRecordS06T002T.SURYO, 0) * -1;               --数量
        vRecordS06T002T.KIN               := NVL(vRecordS06T002T.KIN, 0) * -1;                 --金額
        vRecordS06T002T.SHOHITAXKIN       := NVL(vRecordS06T002T.SHOHITAXKIN, 0) * -1;         --消費税金額
        vRecordS06T002T.SANKOUSHOHITAXKIN := NVL(vRecordS06T002T.SANKOUSHOHITAXKIN, 0) * -1;   --参考消費税金額
        vRecordS06T002T.SIIRESURYO        := NVL(vRecordS06T002T.SIIRESURYO, 0) * -1;          --仕入数量
        vRecordS06T002T.SIIREKIN          := NVL(vRecordS06T002T.SIIREKIN, 0) * -1;            --仕入金額
        vRecordS06T002T.SIIREUNT          := NVL(vRecordS06T002T.SIIREUNT, 0) * -1;            --仕入単価
        vRecordS06T002T.SIIRESHOHITAXKIN  := NVL(vRecordS06T002T.SIIRESHOHITAXKIN, 0) * -1;    --仕入消費税金額
        -- 売上明細テーブル登録
        InsertUMST;
    END LOOP;
    CLOSE SELECT_URGT;
  END Uriminusdnpysks;

  /*****************************************************************************/
  /* プロシージャ名   ：値引対象商品売上数量参照（値引コード１）               */
  /* 引数             ：iTokuisakicd       得意先コード                        */
  /*                  ：iSkyshimedd        請求締日                            */
  /*****************************************************************************/
  FUNCTION GetUriSuryo1(iTokuisakicd IN VARCHAR2, iSkyshimedd IN VARCHAR2)
    return VARCHAR2 is
    returnUriSuryo VARCHAR2(8);

  BEGIN
    -- 値引対象商品売上数量参照
    SELECT
        NVL(SUM(UMST.SURYO), 0)
    INTO
        returnUriSuryo
    FROM
        S02M013T TKSM -- 得意先マスタ
    INNER JOIN
        S06T001T URGT -- 売上テーブル
    ON
        URGT.APKIHONCD   = TKSM.APKIHONCD       -- ＡＰ基本コード
    AND URGT.SHOPCD      = TKSM.SHOPCD          -- ショップコード
    AND URGT.TOKUISAKICD = TKSM.TOKUISAKICD     -- 得意先マスタ.得意先コード
    INNER JOIN
        S06T002T UMST -- 売上明細テーブル
    ON
        URGT.APKIHONCD = UMST.APKIHONCD         -- ＡＰ基本コード
    AND URGT.SHOPCD    = UMST.SHOPCD            -- ショップコード
    AND URGT.URIDNPYNO = UMST.URIDNPYNO         -- 売上伝票番号
    INNER JOIN
        S02M052T JNKM -- 自動値引マスタ
    ON
        JNKM.APKIHONCD    = TKSM.APKIHONCD          -- ＡＰ基本コード
    AND JNKM.SHOPCD       = TKSM.SHOPCD             -- ショップコード
    AND JNKM.JIDONEBIKICD = TKSM.JIDONEBIKICDN1     -- 自動値引コード1
    AND JNKM.SHNCD        = UMST.SHNCD              -- 商品コード
    WHERE
        URGT.APKIHONCD   = vRecordS06T001T.APKIHONCD             -- ＡＰ基本コード
    AND URGT.SHOPCD      = vRecordS06T001T.SHOPCD                -- ショップコード
    AND URGT.TOKUISAKICD = iTokuisakicd                          -- 得意先マスタ.得意先コード
    AND URGT.NEBIKIPRCSJISSIDD != '00000000'                     -- 値引処理実施日
    AND URGT.NEBIKISHIMEDYDM || URGT.NEBIKISHIMEDD = iSkyshimedd -- 値引締年月 || 値引締日
    AND UMST.KEIJOKBN IN ('01', '02')                            -- 売上明細テーブル.計上区分
    AND URGT.DATSKSMOTOKBN NOT IN ('15', '24')                   -- データ作成元区分
    AND TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'YYYYMM') >= NVL(TKSM.NEBIKISTRTDYDMN1, '999999')
    AND NVL(TKSM.NEBIKISHRYDYDMN1, '000000') >= TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'YYYYMM')
    AND URGT.YUKOKBN     = '1'
    AND UMST.YUKOKBN     = '1'
    ;

    return(NVL(returnUriSuryo, 0));
  END GetUriSuryo1;

  /*****************************************************************************/
  /* プロシージャ名   ：値引対象商品売上数量参照（値引コード２）               */
  /* 引数             ：iTokuisakicd       得意先コード                        */
  /*                  ：iSkyshimedd        請求締日                            */
  /*****************************************************************************/
  FUNCTION GetUriSuryo2(iTokuisakicd IN VARCHAR2, iSkyshimedd IN VARCHAR2)
    return VARCHAR2 is
    returnUriSuryo VARCHAR2(8);

  BEGIN
    -- 値引対象商品売上数量参照
    SELECT
        NVL(SUM(UMST.SURYO), 0)
    INTO
        returnUriSuryo
    FROM
        S02M013T TKSM -- 得意先マスタ
    INNER JOIN
        S06T001T URGT -- 売上テーブル
    ON
        URGT.APKIHONCD   = TKSM.APKIHONCD           -- ＡＰ基本コード
    AND URGT.SHOPCD      = TKSM.SHOPCD              -- ショップコード
    AND URGT.TOKUISAKICD = TKSM.TOKUISAKICD         -- 得意先マスタ.得意先コード
    INNER JOIN
        S06T002T UMST -- 売上明細テーブル
    ON
        URGT.APKIHONCD = UMST.APKIHONCD             -- ＡＰ基本コード
    AND URGT.SHOPCD    = UMST.SHOPCD                -- ショップコード
    AND URGT.URIDNPYNO = UMST.URIDNPYNO             -- 売上伝票番号
    INNER JOIN
        S02M052T JNKM1 -- 自動値引マスタ
    ON
        JNKM1.APKIHONCD    = TKSM.APKIHONCD             -- ＡＰ基本コード
    AND JNKM1.SHOPCD       = TKSM.SHOPCD                -- ショップコード
    AND JNKM1.JIDONEBIKICD = TKSM.JIDONEBIKICDN2        -- 自動値引コード2
    AND JNKM1.SHNCD        = UMST.SHNCD                 -- 商品コード
    WHERE
        URGT.APKIHONCD   = vRecordS06T001T.APKIHONCD             -- ＡＰ基本コード
    AND URGT.SHOPCD      = vRecordS06T001T.SHOPCD                -- ショップコード
    AND URGT.TOKUISAKICD = iTokuisakicd                          -- 得意先マスタ.得意先コード
    AND URGT.NEBIKIPRCSJISSIDD != '00000000'                     -- 値引処理実施日
    AND URGT.NEBIKISHIMEDYDM || URGT.NEBIKISHIMEDD = iSkyshimedd -- 値引締年月 || 値引締日
    AND UMST.KEIJOKBN IN ('01', '02')                            -- 売上明細テーブル.計上区分
    AND URGT.DATSKSMOTOKBN NOT IN ('15', '24')                   -- データ作成元区分
    AND TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'YYYYMM') >= NVL(TKSM.NEBIKISTRTDYDMN2, '999999')
    AND NVL(TKSM.NEBIKISHRYDYDMN2, '000000') >= TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'YYYYMM')
    AND URGT.YUKOKBN     = '1'
    AND UMST.YUKOKBN     = '1'
    ;

    return(NVL(returnUriSuryo, 0));
  END GetUriSuryo2;

  /*****************************************************************************/
  /* プロシージャ名   ：値引対象商品売上数量参照（値引コード３）               */
  /* 引数             ：iTokuisakicd       得意先コード                        */
  /*                  ：iSkyshimedd        請求締日                            */
  /*****************************************************************************/
  FUNCTION GetUriSuryo3(iTokuisakicd IN VARCHAR2, iSkyshimedd IN VARCHAR2)
    return VARCHAR2 is
    returnUriSuryo VARCHAR2(8);

  BEGIN
    -- 値引対象商品売上数量参照
    SELECT
        NVL(SUM(UMST.SURYO), 0)
    INTO
        returnUriSuryo
    FROM
        S02M013T TKSM -- 得意先マスタ
    INNER JOIN
        S06T001T URGT -- 売上テーブル
    ON
        URGT.APKIHONCD   = TKSM.APKIHONCD       -- ＡＰ基本コード
    AND URGT.SHOPCD      = TKSM.SHOPCD          -- ショップコード
    AND URGT.TOKUISAKICD = TKSM.TOKUISAKICD     -- 得意先マスタ.得意先コード
    INNER JOIN
        S06T002T UMST -- 売上明細テーブル
    ON
        URGT.APKIHONCD = UMST.APKIHONCD         -- ＡＰ基本コード
    AND URGT.SHOPCD    = UMST.SHOPCD            -- ショップコード
    AND URGT.URIDNPYNO = UMST.URIDNPYNO         -- 売上伝票番号
    INNER JOIN
        S02M052T JNKM2 -- 自動値引マスタ
    ON
        JNKM2.APKIHONCD    = TKSM.APKIHONCD         -- ＡＰ基本コード
    AND JNKM2.SHOPCD       = TKSM.SHOPCD            -- ショップコード
    AND JNKM2.JIDONEBIKICD = TKSM.JIDONEBIKICDN3    -- 自動値引コード3
    AND JNKM2.SHNCD        = UMST.SHNCD             -- 商品コード
    WHERE
        URGT.APKIHONCD   = vRecordS06T001T.APKIHONCD             -- ＡＰ基本コード
    AND URGT.SHOPCD      = vRecordS06T001T.SHOPCD                -- ショップコード
    AND URGT.TOKUISAKICD = iTokuisakicd                          -- 得意先マスタ.得意先コード
    AND URGT.NEBIKIPRCSJISSIDD != '00000000'                     -- 値引処理実施日
    AND URGT.NEBIKISHIMEDYDM || URGT.NEBIKISHIMEDD = iSkyshimedd -- 値引締年月 || 値引締日
    AND UMST.KEIJOKBN IN ('01', '02')                            -- 売上明細テーブル.計上区分
    AND URGT.DATSKSMOTOKBN NOT IN ('15', '24')                   -- データ作成元区分
    AND TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'YYYYMM') >= NVL(TKSM.NEBIKISTRTDYDMN3, '999999')
    AND NVL(TKSM.NEBIKISHRYDYDMN3, '000000') >= TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'YYYYMM')
    AND URGT.YUKOKBN     = '1'
    AND UMST.YUKOKBN     = '1'
    ;

    return(NVL(returnUriSuryo, 0));
  END GetUriSuryo3;

  /*****************************************************************************/
  /* プロシージャ名   ：自動値引データ作成                                     */
  /* 引数             ：iTokuisakicd       得意先コード                        */
  /*                  ：iSkyshimedd        請求締日                            */
  /*                  ：iUriSuryo1         値引対象商品売上数量（値引コード１）*/
  /*                  ：iUriSuryo2         値引対象商品売上数量（値引コード２）*/
  /*                  ：iUriSuryo3         値引対象商品売上数量（値引コード３）*/
  /*                  ：iSyorimodo         処理モード（1:請求締、2:請求締解除、3:売上締、4:売上締解除） */
  /*****************************************************************************/
  PROCEDURE Jidonebikikidatasks(iTokuisakicd IN VARCHAR2,
                                iSkyshimedd  IN VARCHAR2,
                                iUriSuryo1   IN VARCHAR2,
                                iUriSuryo2   IN VARCHAR2,
                                iUriSuryo3   IN VARCHAR2,
                                iSyorimodo   IN VARCHAR2
  ) IS
    wRecordS02M052T  S02M052T%rowtype; -- 自動値引マスタ
    wRecordS02M052T1 S02M052T%rowtype; -- 自動値引マスタ1
    wRecordS02M052T2 S02M052T%rowtype; -- 自動値引マスタ2

    wTokuisakicd_TAB       S02M013T.TOKUISAKICD%TYPE;           -- 得意先マスタ.得意先コード
    wAcpttnshacd_TAB       S02M013T.EIGYOTNSHACD%TYPE;          -- 得意先マスタ.受注担当者コード
    wSkysakicd_TAB         S02M013T.SKYSAKICD%TYPE;             -- 得意先マスタ.請求先コード
    wTaxkeisankbn_TAB      S06T001T.TAXKEISANKBN%TYPE;          -- 売上テーブル.税計算区分
--  wUridd_TAB             S06T001T.URIDD%TYPE;                 -- 売上テーブル.売上日
    wTokuisakikbn_TAB      S02M013T.TOKUISAKIKBN%TYPE;          -- 得意先マスタ.得意先区分
    wJutakudlvrycstkbn_TAB S02M013T.JUTAKUDLVRYCSTKBN%TYPE;     -- 得意先マスタ.受託配達顧客区分

    wNebikishrydyDmn1_TAB  S02M013T.NEBIKISHRYDYDMN1%TYPE;      -- 得意先マスタ.値引終了年月１
    wNebikishrydyDmn2_TAB  S02M013T.NEBIKISHRYDYDMN2%TYPE;      -- 得意先マスタ.値引終了年月２
    wNebikishrydyDmn3_TAB  S02M013T.NEBIKISHRYDYDMN3%TYPE;      -- 得意先マスタ.値引終了年月３

    wJidonebikicd_TAB      S02M052T.JIDONEBIKICD%TYPE;          -- 自動値引コード
    wNebikikeijoshncd_TAB  S02M052T.NEBIKIKEIJOSHNCD%TYPE;      -- 値引計上商品コード
    wNebikibtlhns_TAB      S02M052T.NEBIKIBTLHNS%TYPE;          -- 値引ボトル本数
    wNebikikin_TAB         S02M052T.NEBIKIKIN%TYPE;             -- 値引データの金額
    wNebikiunt_TAB         S02M052T.NEBIKIUNT%TYPE;             -- 値引単価
    wMeisaibiko_TAB        S02M052T.MEISAIBIKO%TYPE;            -- 明細備考

    wJidonebikicd_TAB1     S02M052T.JIDONEBIKICD%TYPE;          -- 自動値引コード
    wNebikikeijoshncd_TAB1 S02M052T.NEBIKIKEIJOSHNCD%TYPE;      -- 値引計上商品コード
    wNebikibtlhns_TAB1     S02M052T.NEBIKIBTLHNS%TYPE;          -- 値引ボトル本数
    wNebikikin_TAB1        S02M052T.NEBIKIKIN%TYPE;             -- 値引データの金額
    wNebikiunt_TAB1        S02M052T.NEBIKIUNT%TYPE;             -- 値引単価
    wMeisaibiko_TAB1       S02M052T.MEISAIBIKO%TYPE;            -- 明細備考

    wJidonebikicd_TAB2     S02M052T.JIDONEBIKICD%TYPE;          -- 自動値引コード
    wNebikikeijoshncd_TAB2 S02M052T.NEBIKIKEIJOSHNCD%TYPE;      -- 値引計上商品コード
    wNebikibtlhns_TAB2     S02M052T.NEBIKIBTLHNS%TYPE;          -- 値引ボトル本数
    wNebikikin_TAB2        S02M052T.NEBIKIKIN%TYPE;             -- 値引データの金額
    wNebikiunt_TAB2        S02M052T.NEBIKIUNT%TYPE;             -- 値引単価
    wMeisaibiko_TAB2       S02M052T.MEISAIBIKO%TYPE;            -- 明細備考

    -- add 軽減税率変更対応_LI 20261127 Start
    -- 消費税改修：税率ベース振り分け用
    vTaxRate4   S02M091T.SHOHITAX%TYPE;      -- 税区分4の締日時点税率
    vTaxRate6   S02M091T.SHOHITAX%TYPE;      -- 税区分6の締日時点税率
    vTaxRate7   S02M091T.SHOHITAX%TYPE;      -- 税区分7の締日時点税率
    vRowRate    S02M091T.SHOHITAX%TYPE;      -- 振り分け対象行の税率
    vUriKin8Old S06T002T.KIN%TYPE;           -- 軽減の別率分売上金額（099904用）
    vPrevStrtdd S02M091T.TEKIYOSTRTDD%TYPE;  -- 税区分4の直前期間の適用開始日

    -- add 軽減税率変更対応_LI 20261127 End
    -- 自動値引データ作成
    CURSOR SELECT_JNKM IS
    SELECT
        TKSM.TOKUISAKICD,               -- 得意先マスタ.得意先コード
        TKSM.EIGYOTNSHACD,              -- 得意先マスタ.営業担当者コード
        TKSM.SKYSAKICD,                 -- 得意先マスタ.請求先コード
        -- 請求締日（売上日）＜ショップマスタの税計算区分売上切替日のとき現税計算区分売上、以外のとき新税計算区分売上
        CASE
            WHEN iSkyshimedd < SHPM.TAXKEISANKBNURIKIRIKAEDD THEN SHPM.GENZTAXKEISANKBNURI      --現税計算区分売上
            ELSE SHPM.SHINTAXKEISANKBNURI                                                       --新税計算区分売上
            END AS TAXKEISANKBN,                                                                --税計算区分
--      iSkyshimedd URIDD,              --売上日
        TKSM.TOKUISAKIKBN,              --得意先マスタ.得意先区分
        TKSM.JUTAKUDLVRYCSTKBN AS JUTAKUDLVRYCSTKBN,        -- 受託配達顧客区分

        TKSM.NEBIKISHRYDYDMN1,          --得意先マスタ.値引終了年月１
        TKSM.NEBIKISHRYDYDMN2,          --得意先マスタ.値引終了年月２
        TKSM.NEBIKISHRYDYDMN3,          --得意先マスタ.値引終了年月３

        --自動値引1
        JNKM.JIDONEBIKICD AS JIDONEBIKICD,                  -- 自動値引コード
        JNKM.NEBIKIKEIJOSHNCD AS NEBIKIKEIJOSHNCD,          -- 値引計上商品コード
        DECODE(NVL(JNKM.GEKKANHANBAIHNS, 0), 0,             -- 自動値引マスタ.月間販売本数が0
            0,
            (DECODE(JNKM.JIDONEBIKIKIND, 1,                 -- 自動値引種別:1:ボトル値引
                -- 求めた売上数量 ÷ 自動値引マスタ.月間販売本数 × 自動値引マスタ.値引ボトル本数
                FLOOR(iUriSuryo1 / JNKM.GEKKANHANBAIHNS) * NVL(JNKM.NEBIKIBTLHNS, 0),
                2,                                          -- 自動値引種別:2:サーバ値引
                -- 求めた売上数量 ÷ 自動値引マスタ.月間販売本数
                (CASE WHEN JNKM.GEKKANHANBAIHNS <= iUriSuryo1 THEN 1 ELSE 0 END))
            )) AS SURYO,                                    -- 値引データの数量
        DECODE(JNKM.JIDONEBIKIKIND, 1,                      -- 自動値引種別:1:ボトル値引
            NVL(JNKM.NEBIKIUNT, 0),                         -- 自動値引マスタ.値引ボトル単価
            2,                                              -- 自動値引種別:2:サーバ値引
            NVL(JNKM.NEBIKIKIN, 0)),                        -- 自動値引マスタ.値引金額
        JNKM.MEISAIBIKO AS MEISAIBIKO,                      -- 明細備考

        --自動値引2
        JNKM1.JIDONEBIKICD AS JIDONEBIKICD1,                -- 自動値引コード
        JNKM1.NEBIKIKEIJOSHNCD AS NEBIKIKEIJOSHNCD1,        -- 値引計上商品コード
        DECODE(NVL(JNKM1.GEKKANHANBAIHNS, 0), 0,            -- 自動値引マスタ.月間販売本数が0
            0,
            (DECODE(JNKM1.JIDONEBIKIKIND, 1,                -- 自動値引種別:1:ボトル値引
                -- 求めた売上数量 ÷ 自動値引マスタ.月間販売本数 × 自動値引マスタ.値引ボトル本数
                FLOOR(iUriSuryo2 / JNKM1.GEKKANHANBAIHNS) * NVL(JNKM1.NEBIKIBTLHNS, 0),
                2,                                          -- 自動値引種別:2:サーバ値引
                -- 求めた売上数量 ÷ 自動値引マスタ.月間販売本数
                (CASE WHEN JNKM1.GEKKANHANBAIHNS <= iUriSuryo2 THEN 1 ELSE 0 END))
            )) AS SURYO1,                                   -- 値引データの数量
        DECODE(JNKM1.JIDONEBIKIKIND, 1,                     -- 自動値引種別:1:ボトル値引
            NVL(JNKM1.NEBIKIUNT, 0),                        -- 自動値引マスタ.値引ボトル単価
            2,                                              -- 自動値引種別:2:サーバ値引
            NVL(JNKM1.NEBIKIKIN, 0)
            ),                                              --  自動値引マスタ.値引金額
        JNKM1.MEISAIBIKO AS MEISAIBIKO1,                    -- 明細備考

        --自動値引3
        JNKM2.JIDONEBIKICD AS JIDONEBIKICD2,                -- 自動値引コード
        JNKM2.NEBIKIKEIJOSHNCD AS NEBIKIKEIJOSHNCD2,        -- 値引計上商品コード
        DECODE(NVL(JNKM2.GEKKANHANBAIHNS, 0), 0,            -- 自動値引マスタ.月間販売本数が0
            0,
            (DECODE(JNKM2.JIDONEBIKIKIND, 1,                -- 自動値引種別:1:ボトル値引
                -- 求めた売上数量 ÷ 自動値引マスタ.月間販売本数 × 自動値引マスタ.値引ボトル本数
                FLOOR(iUriSuryo3 / JNKM2.GEKKANHANBAIHNS) * NVL(JNKM2.NEBIKIBTLHNS, 0),
                2,                                          -- 自動値引種別:2:サーバ値引
                -- 求めた売上数量 ÷ 自動値引マスタ.月間販売本数
                (CASE WHEN JNKM2.GEKKANHANBAIHNS <= iUriSuryo3 THEN 1 ELSE 0 END))
            )) AS SURYO2,                                   -- 値引データの数量
        DECODE(JNKM2.JIDONEBIKIKIND, 1,                     -- 自動値引種別:1:ボトル値引
            NVL(JNKM2.NEBIKIUNT, 0),                        -- 自動値引マスタ.値引ボトル単価
            2,                                              -- 自動値引種別:2:サーバ値引
            NVL(JNKM2.NEBIKIKIN, 0)
            ),                                              -- 自動値引マスタ.値引金額
        JNKM2.MEISAIBIKO AS MEISAIBIKO2                     -- 明細備考
        FROM 
            S02M013T TKSM -- 得意先マスタ
        LEFT JOIN
            S02M046T SHPM -- ショップマスタ
        ON
            TKSM.APKIHONCD = SHPM.APKIHONCD
        AND TKSM.SHOPCD    = SHPM.SHOPCD
        LEFT JOIN
            S02M052T JNKM -- 自動値引マスタ
        ON
            JNKM.APKIHONCD    = TKSM.APKIHONCD              --ＡＰ基本コード
        AND JNKM.SHOPCD       = TKSM.SHOPCD                 --ショップコード
        AND JNKM.JIDONEBIKICD = TKSM.JIDONEBIKICDN1         --自動値引コード
        AND iUriSuryo1        >= JNKM.GEKKANHANBAIHNS       --数量 > 月間販売本数
        LEFT JOIN
            S02M052T JNKM1 -- 自動値引マスタ
        ON
            JNKM1.APKIHONCD    = TKSM.APKIHONCD             --ＡＰ基本コード
        AND JNKM1.SHOPCD       = TKSM.SHOPCD                --ショップコード
        AND JNKM1.JIDONEBIKICD = TKSM.JIDONEBIKICDN2        --自動値引コード
        AND iUriSuryo2         >= JNKM1.GEKKANHANBAIHNS     --数量 > 月間販売本数
        LEFT JOIN
            S02M052T JNKM2 -- 自動値引マスタ
        ON
            JNKM2.APKIHONCD    = TKSM.APKIHONCD             --ＡＰ基本コード
        AND JNKM2.SHOPCD       = TKSM.SHOPCD                --ショップコード
        AND JNKM2.JIDONEBIKICD = TKSM.JIDONEBIKICDN3        --自動値引コード
        AND iUriSuryo3         >= JNKM2.GEKKANHANBAIHNS     --数量 > 月間販売本数
        WHERE
            TKSM.APKIHONCD   = vRecordS06T001T.APKIHONCD    --ＡＰ基本コード
        AND TKSM.SHOPCD      = vRecordS06T001T.SHOPCD       --ショップコード
        AND TKSM.TOKUISAKICD = iTokuisakicd                 --得意先マスタ.得意先コード
        ;

  BEGIN
    -- 自動値引データ作成
    OPEN SELECT_JNKM;
    LOOP
        FETCH SELECT_JNKM
        INTO
        wTokuisakicd_TAB,           -- 売上テーブル.得意先コード
        wAcpttnshacd_TAB,           -- 売上テーブル.受注担当者コード
        wSkysakicd_TAB,             -- 売上テーブル.請求先コード
        wTaxkeisankbn_TAB,          -- 売上テーブル.税計算区分
--      wUridd_TAB,                 -- 売上テーブル.売上日
        wTokuisakikbn_TAB,          -- 得意先マスタ.得意先区分
        wJutakudlvrycstkbn_TAB,     -- 得意先マスタ.受託配達顧客区分

        wNebikishrydyDmn1_TAB,      --値引終了年月1
        wNebikishrydyDmn2_TAB,      --値引終了年月2
        wNebikishrydyDmn3_TAB,      --値引終了年月3

        wJidonebikicd_TAB,          -- 自動値引コード
        wNebikikeijoshncd_TAB,      -- 値引計上商品コード
        wNebikibtlhns_TAB,          -- 値引ボトル本数
        wNebikiunt_TAB,             -- 値引単価
        wMeisaibiko_TAB,            -- 明細備考

        wJidonebikicd_TAB1,         -- 自動値引コード
        wNebikikeijoshncd_TAB1,     -- 値引計上商品コード
        wNebikibtlhns_TAB1,         -- 値引ボトル本数
        wNebikiunt_TAB1,            -- 値引単価
        wMeisaibiko_TAB1,           -- 明細備考

        wJidonebikicd_TAB2,         -- 自動値引コード
        wNebikikeijoshncd_TAB2,     -- 値引計上商品コード
        wNebikibtlhns_TAB2,         -- 値引ボトル本数
        wNebikiunt_TAB2,            -- 値引単価
        wMeisaibiko_TAB2            -- 明細備考
        ;
        EXIT WHEN SELECT_JNKM%NOTFOUND;
    END LOOP;
    CLOSE SELECT_JNKM;

    vRecordS06T001T.TOKUISAKICD       := wTokuisakicd_TAB;              -- 売上テーブル.得意先コード
    vRecordS06T001T.ACPTTNSHACD       := wAcpttnshacd_TAB;              -- 売上テーブル.受注担当者コード
    vRecordS06T001T.SKYSAKICD         := wSkysakicd_TAB;                -- 売上テーブル.請求先コード
    vRecordS06T001T.TAXKEISANKBN      := wTaxkeisankbn_TAB;             -- 売上テーブル.税計算区分
--  vRecordS06T001T.URIDD             := wUridd_TAB;                    -- 売上テーブル.売上日
    vRecordS06T001T.URIDD             := iSkyshimedd;                   -- 売上テーブル.売上日
    vRecordS06T001T.EIGYOTNSHACD      := wAcpttnshacd_TAB;              -- 売上テーブル.営業担当者コード

    wRecordS02M052T.JIDONEBIKICD      := wJidonebikicd_TAB;             -- 自動値引コード
    wRecordS02M052T.NEBIKIKEIJOSHNCD  := wNebikikeijoshncd_TAB;         -- 値引計上商品コード
    wRecordS02M052T.NEBIKIBTLHNS      := wNebikibtlhns_TAB;             -- 値引ボトル本数
    wRecordS02M052T.NEBIKIKIN         := wNebikikin_TAB;                -- 値引データの金額
    wRecordS02M052T.NEBIKIUNT         := wNebikiunt_TAB;                -- 値引単価
    wRecordS02M052T.MEISAIBIKO        := wMeisaibiko_TAB;               -- 明細備考

    wRecordS02M052T1.JIDONEBIKICD     := wJidonebikicd_TAB1;            -- 自動値引コード
    wRecordS02M052T1.NEBIKIKEIJOSHNCD := wNebikikeijoshncd_TAB1;        -- 値引計上商品コード
    wRecordS02M052T1.NEBIKIBTLHNS     := wNebikibtlhns_TAB1;            -- 値引ボトル本数
    wRecordS02M052T1.NEBIKIKIN        := wNebikikin_TAB1;               -- 値引データの金額
    wRecordS02M052T1.NEBIKIUNT        := wNebikiunt_TAB1;               -- 値引単価
    wRecordS02M052T1.MEISAIBIKO       := wMeisaibiko_TAB1;              -- 明細備考

    wRecordS02M052T2.JIDONEBIKICD     := wJidonebikicd_TAB2;            -- 自動値引コード
    wRecordS02M052T2.NEBIKIKEIJOSHNCD := wNebikikeijoshncd_TAB2;        -- 値引計上商品コード
    wRecordS02M052T2.NEBIKIBTLHNS     := wNebikibtlhns_TAB2;            -- 値引ボトル本数
    wRecordS02M052T2.NEBIKIKIN        := wNebikikin_TAB2;               -- 値引データの金額
    wRecordS02M052T2.NEBIKIUNT        := wNebikiunt_TAB2;               -- 値引単価
    wRecordS02M052T2.MEISAIBIKO       := wMeisaibiko_TAB2;              -- 明細備考

    -- TYYMM999999のYYMMは年月、999999は連番,自動採番はＯＲＡＣＬＥ用採番共通部品を呼び出します。
    GetUridnpyno;

    -- 自動値引データ作成の時、売上テーブルデータ設定
    SetURGT;

    vRecordS06T002T.URIDNPYMEISAINO   := 0;         --売上伝票明細番号

    vRecordS06T001T.SHOHITAXKIN       := 0;         --売上テーブル.消費税金額
    vRecordS06T001T.SANKOUSHOHITAXKIN := 0;         --売上テーブル.参考消費税金額
    vRecordS06T001T.URIKIN            := 0;         --売上テーブル.売上金額

    vRecordS06T001T.NEBIKISHIMEDYDM   := TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'YYYYMM');   --値引締年月
    vRecordS06T001T.NEBIKISHIMEDD     := TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'DD');       --値引締日

    --データ作成元区分
    IF (iSyorimodo IN ('1', '2')) THEN
        vRecordS06T001T.DATSKSMOTOKBN := '15';
    ELSE
        vRecordS06T001T.DATSKSMOTOKBN := '24';
    END IF;

    vUriKin8  := 0;
    vUriKin10 := 0;
    -- add 軽減税率変更対応_LI 20261127 Start
    vUriKin8Old := 0;

    -- 消費税改修：振り分け判定用に、締日時点の税率と税区分4の直前期間を取得する
    IF (iSyorimodo = '3') THEN
        SELECT SHOHITAX INTO vTaxRate4 FROM S02M091T
         WHERE TAXKBN = '4' AND TEKIYOSTRTDD <= iSkyshimedd
           AND TEKIYOSHRYDD >= iSkyshimedd AND YUKOKBN = '1';
        SELECT SHOHITAX INTO vTaxRate6 FROM S02M091T
         WHERE TAXKBN = '6' AND TEKIYOSTRTDD <= iSkyshimedd
           AND TEKIYOSHRYDD >= iSkyshimedd AND YUKOKBN = '1';
        SELECT SHOHITAX INTO vTaxRate7 FROM S02M091T
         WHERE TAXKBN = '7' AND TEKIYOSTRTDD <= iSkyshimedd
           AND TEKIYOSHRYDD >= iSkyshimedd AND YUKOKBN = '1';
        -- 直前期間が無い時点ではNULL（別率分は発生しない）
        SELECT MAX(TEKIYOSTRTDD) INTO vPrevStrtdd FROM S02M091T
         WHERE TAXKBN = '4' AND TEKIYOSHRYDD < iSkyshimedd AND YUKOKBN = '1';
    END IF;
    -- add 軽減税率変更対応_LI 20261127 End

    IF wRecordS02M052T.JIDONEBIKICD IS NOT NULL THEN
        --  売上伝票明細番号
        vRecordS06T002T.URIDNPYMEISAINO := NVL(vRecordS06T002T.URIDNPYMEISAINO, 0) + 1;
        -- 自動値引データ作成の時、売上明細テーブルデータ取得
        Setjidonebikikidata(wRecordS02M052T, wTokuisakikbn_TAB, wJutakudlvrycstkbn_TAB, wNebikishrydyDmn1_TAB);
        
        -- 売上テーブルの売上金額を加算
        vRecordS06T001T.URIKIN            := vRecordS06T001T.URIKIN + vRecordS06T002T.KIN;                          -- 売上金額
        vRecordS06T001T.SHOHITAXKIN       := vRecordS06T001T.SHOHITAXKIN + vRecordS06T002T.SHOHITAXKIN;             -- 消費税金額
        vRecordS06T001T.SANKOUSHOHITAXKIN := vRecordS06T001T.SANKOUSHOHITAXKIN + vRecordS06T002T.SANKOUSHOHITAXKIN; -- 参考消費税金額

        --パラメータ.処理モード＝'3'の場合、８％消費税額、１０％消費税額を加算する。
        IF (iSyorimodo = '3') THEN
            IF (vRecordS06T002T.TAXKBN = '1') THEN
                --売上明細テーブル用変数.税区分＝'1'（課税）の場合
                vUriKin10 := vUriKin10 + vRecordS06T002T.KIN;
            -- modify 軽減税率変更対応_LI 20261127 Start
            ELSIF (vRecordS06T002T.TAXKBN IN ('4', '6', '7')) THEN
                --消費税改修：軽減は税区分ではなく税率で振り分ける（同率→099902、別率→099904）
                vRowRate := CASE vRecordS06T002T.TAXKBN
                              WHEN '4' THEN vTaxRate4
                              WHEN '6' THEN vTaxRate6
                              ELSE vTaxRate7 END;
                IF (vRowRate = vTaxRate4) THEN
                    vUriKin8 := vUriKin8 + vRecordS06T002T.KIN;
                ELSE
                    vUriKin8Old := vUriKin8Old + vRecordS06T002T.KIN;
                END IF;
            -- modify 軽減税率変更対応_LI 20261127 End
            END IF;
        END IF;
    END IF;

    IF wRecordS02M052T1.JIDONEBIKICD IS NOT NULL THEN
        --  売上伝票明細番号
        vRecordS06T002T.URIDNPYMEISAINO := NVL(vRecordS06T002T.URIDNPYMEISAINO, 0) + 1;
        -- 自動値引データ作成の時、売上明細テーブルデータ取得
        Setjidonebikikidata(wRecordS02M052T1, wTokuisakikbn_TAB, wJutakudlvrycstkbn_TAB, wNebikishrydyDmn2_TAB);

        -- 売上テーブルの売上金額を加算
        vRecordS06T001T.URIKIN            := vRecordS06T001T.URIKIN + vRecordS06T002T.KIN;                          -- 売上金額
        vRecordS06T001T.SHOHITAXKIN       := vRecordS06T001T.SHOHITAXKIN + vRecordS06T002T.SHOHITAXKIN;             -- 消費税金額
        vRecordS06T001T.SANKOUSHOHITAXKIN := vRecordS06T001T.SANKOUSHOHITAXKIN + vRecordS06T002T.SANKOUSHOHITAXKIN; -- 参考消費税金額

        --パラメータ.処理モード＝'3'の場合、８％消費税額、１０％消費税額を加算する。
        IF (iSyorimodo = '3') THEN
            IF (vRecordS06T002T.TAXKBN = '1') THEN
                --売上明細テーブル用変数.税区分＝'1'（課税）の場合
                vUriKin10 := vUriKin10 + vRecordS06T002T.KIN;
            -- modify 軽減税率変更対応_LI 20261127 Start
            ELSIF (vRecordS06T002T.TAXKBN IN ('4', '6', '7')) THEN
                --消費税改修：軽減は税区分ではなく税率で振り分ける（同率→099902、別率→099904）
                vRowRate := CASE vRecordS06T002T.TAXKBN
                              WHEN '4' THEN vTaxRate4
                              WHEN '6' THEN vTaxRate6
                              ELSE vTaxRate7 END;
                IF (vRowRate = vTaxRate4) THEN
                    vUriKin8 := vUriKin8 + vRecordS06T002T.KIN;
                ELSE
                    vUriKin8Old := vUriKin8Old + vRecordS06T002T.KIN;
                END IF;
            -- modify 軽減税率変更対応_LI 20261127 End
            END IF;
        END IF;
    END IF;

    IF wRecordS02M052T2.JIDONEBIKICD IS NOT NULL THEN
        --  売上伝票明細番号
        vRecordS06T002T.URIDNPYMEISAINO := NVL(vRecordS06T002T.URIDNPYMEISAINO, 0) + 1;
        -- 自動値引データ作成の時、売上明細テーブルデータ設定
        Setjidonebikikidata(wRecordS02M052T2, wTokuisakikbn_TAB, wJutakudlvrycstkbn_TAB, wNebikishrydyDmn3_TAB);

        -- 売上テーブルの売上金額を加算
        vRecordS06T001T.URIKIN            := vRecordS06T001T.URIKIN + vRecordS06T002T.KIN;                          -- 売上金額
        vRecordS06T001T.SHOHITAXKIN       := vRecordS06T001T.SHOHITAXKIN + vRecordS06T002T.SHOHITAXKIN;             -- 消費税金額
        vRecordS06T001T.SANKOUSHOHITAXKIN := vRecordS06T001T.SANKOUSHOHITAXKIN + vRecordS06T002T.SANKOUSHOHITAXKIN; -- 参考消費税金額

        --パラメータ.処理モード＝'3'の場合、８％消費税額、１０％消費税額を加算する。
        IF (iSyorimodo = '3') THEN
            IF (vRecordS06T002T.TAXKBN = '1') THEN
                --売上明細テーブル用変数.税区分＝'1'（課税）の場合
                vUriKin10 := vUriKin10 + vRecordS06T002T.KIN;
            -- modify 軽減税率変更対応_LI 20261127 Start
            ELSIF (vRecordS06T002T.TAXKBN IN ('4', '6', '7')) THEN
                --消費税改修：軽減は税区分ではなく税率で振り分ける（同率→099902、別率→099904）
                vRowRate := CASE vRecordS06T002T.TAXKBN
                              WHEN '4' THEN vTaxRate4
                              WHEN '6' THEN vTaxRate6
                              ELSE vTaxRate7 END;
                IF (vRowRate = vTaxRate4) THEN
                    vUriKin8 := vUriKin8 + vRecordS06T002T.KIN;
                ELSE
                    vUriKin8Old := vUriKin8Old + vRecordS06T002T.KIN;
                END IF;
            -- modify 軽減税率変更対応_LI 20261127 End
            END IF;
        END IF;
    END IF;

    --パラメータ.処理モード＝'3' かつ 共通変数.インボイス施行フラグ＝'1' の場合、消費税明細レコードを登録する。
    IF (iSyorimodo = '3' AND vInvoiceShikoFlg = '1') THEN
        --変数.１０％売上金額≠0の場合、消費税明細レコードを登録する。
        IF (vUriKin10 != 0) THEN
            --売上伝票明細番号
            vRecordS06T002T.URIDNPYMEISAINO := NVL(vRecordS06T002T.URIDNPYMEISAINO, 0) + 1;
            --自動値引データ作成の時、売上明細テーブルデータ設定
            Setjidonebikikidata2('099901', vUriKin10, wTokuisakikbn_TAB, wJutakudlvrycstkbn_TAB);
            --売上の消費税へ加算
            vRecordS06T001T.SHOHITAXKIN := vRecordS06T001T.SHOHITAXKIN + vRecordS06T002T.SHOHITAXKIN;
        END IF;

        --変数.８％売上金額≠0の場合、消費税明細レコードを登録する。
        IF (vUriKin8 != 0) THEN
            --売上伝票明細番号
            vRecordS06T002T.URIDNPYMEISAINO := NVL(vRecordS06T002T.URIDNPYMEISAINO, 0) + 1;
            --自動値引データ作成の時、売上明細テーブルデータ設定
            Setjidonebikikidata2('099902', vUriKin8, wTokuisakikbn_TAB, wJutakudlvrycstkbn_TAB);
            -- add 軽減税率変更対応_LI 20261127 Start
            --売上の消費税へ加算
            vRecordS06T001T.SHOHITAXKIN := vRecordS06T001T.SHOHITAXKIN + vRecordS06T002T.SHOHITAXKIN;
        END IF;

        --軽減の別率分（税区分4の直前税率分）がある場合、消費税明細レコードを登録する。
        IF (vUriKin8Old != 0) THEN
            --売上伝票明細番号
            vRecordS06T002T.URIDNPYMEISAINO := NVL(vRecordS06T002T.URIDNPYMEISAINO, 0) + 1;
            --自動値引データ作成の時、売上明細テーブルデータ設定（基準日＝直前期間の日付）
            Setjidonebikikidata2('099904', vUriKin8Old, wTokuisakikbn_TAB, wJutakudlvrycstkbn_TAB, vPrevStrtdd);
            -- add 軽減税率変更対応_LI 20261127 End
            --売上の消費税へ加算
            vRecordS06T001T.SHOHITAXKIN := vRecordS06T001T.SHOHITAXKIN + vRecordS06T002T.SHOHITAXKIN;
        END IF;
    END IF;

    IF (vRecordS06T002T.URIDNPYMEISAINO != 0) THEN
        -- 売上テーブル
        InsertURGT;
    END IF;
 
    -- 得意先実績テーブルの現在残高更新
    UpdateZd;
  END Jidonebikikidatasks;

  /*****************************************************************************/
  /* プロシージャ名 ：売上テーブル値引締め済み更新（代引き以外）               */
  /* 引数           ：iIraidd            依頼日                                */
  /*                ：iIraijikan         依頼時間                              */
  /*                ：iSkyshimedd        請求締日                              */
  /*                : iShimeKaijoFlg     締解除フラグ                          */
  /*****************************************************************************/
  PROCEDURE UpdateUriageTable1(iSkyshimedd    IN VARCHAR2,
                               iIraidd        IN VARCHAR2,
                               iIraijikan     IN VARCHAR2,
                               iShimeKaijoFlg IN VARCHAR2
) IS
  BEGIN
    UPDATE S06T001T URGT -- 売上テーブル
    SET
      NEBIKISHIMEDYDM   = CASE iShimeKaijoFlg
                            WHEN '0' THEN TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'YYYYMM')
                            ELSE NULL
                          END,                                                 -- 値引締年月
      NEBIKISHIMEDD     = CASE iShimeKaijoFlg
                            WHEN '0' THEN TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'DD')
                            ELSE NULL
                          END,                                                 -- 値引締日
      NEBIKIPRCSJISSIDD = CASE iShimeKaijoFlg
                            WHEN '0' THEN vRecordS06T001T.KSHNUNYOYMD
                            ELSE '00000000'
                          END,                                                 -- 値引処理実施日
      KOSHAAPKIHONCD    = vRecordS06T001T.KOSHAAPKIHONCD,                      -- 更新者ＡＰ基本コード
      KOSHASHOPCD       = vRecordS06T001T.KOSHASHOPCD,                         -- 更新者ショップコード
      KOSHACD           = vRecordS06T001T.KOSHACD,                             -- 更新者コード
      KSHNSYSTMYMD      = vRecordS06T001T.KSHNSYSTMYMD,                        -- 更新システム日付
      KSHNSYSTMJIKAN    = vRecordS06T001T.KSHNSYSTMJIKAN,                      -- 更新システム時間
      KSHNUNYOYMD       = vRecordS06T001T.KSHNUNYOYMD,                         -- 更新運用日付
      KSHNIPADRSE       = vRecordS06T001T.KSHNIPADRSE,                         -- 更新ＩＰアドレス
      KSHNPGMID         = vRecordS06T001T.KSHNPGMID                            -- 更新プログラムＩＤ
    WHERE
          URGT.APKIHONCD         = vRecordS06T001T.APKIHONCD -- AP基本コード
      AND URGT.SHOPCD            = vRecordS06T001T.SHOPCD    -- ショップコード
      AND URGT.URIDD            <= iSkyshimedd               -- 売上日
      AND (
            (
                  iShimeKaijoFlg        = '0'
              AND (
                URGT.NEBIKIPRCSJISSIDD = '00000000'        -- 値引処理実施日
                OR (
                        URGT.NEBIKISHIMEDYDM = TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'YYYYMM')    -- 値引締年月
                    AND URGT.NEBIKISHIMEDD = TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'DD')          -- 値引締日
                )
              )
            )
            OR
            (
                  iShimeKaijoFlg        = '1'
              AND URGT.NEBIKISHIMEDYDM = TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'YYYYMM')    -- 値引締年月
              AND URGT.NEBIKISHIMEDD = TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'DD')          -- 値引締日
            )
      )
      AND URGT.DATSKSMOTOKBN    NOT IN ('15','24')           --データ作成元区分
      AND URGT.URISHIMEPRCSDD   != '00000000'                -- 売上締処理日
      AND URGT.YUKOKBN          = '1'
      AND EXISTS (
        SELECT 1
        FROM
          S07W003T SKSW -- 請求締ワーク
        INNER JOIN
          S02M017T SKSM -- 請求先マスタ
        ON
              SKSM.APKIHONCD      = SKSW.APKIHONCD -- ＡＰ基本コード
          AND SKSM.SHOPCD         = SKSW.SHOPCD    -- ショップコード
          AND SKSM.SKYSAKICD      = SKSW.SKYSAKICD -- 請求先コード
          AND SKSM.KAISHUHOHOKBN != '7'            -- 回収方法区分
        INNER JOIN
          S02M013T TKSM -- 得意先マスタ
        ON
              TKSM.APKIHONCD = SKSM.APKIHONCD      -- ＡＰ基本コード
          AND TKSM.SHOPCD    = SKSM.SHOPCD         -- ショップコード
          AND TKSM.SKYSAKICD = SKSM.SKYSAKICD      -- 請求先コード
        WHERE
            SKSW.APKIHONCD   = URGT.APKIHONCD      -- ＡＰ基本コード
        AND SKSW.SHOPCD      = URGT.SHOPCD         -- ショップコード
        AND SKSW.IRAIDD      = iIraidd             -- 依頼日
        AND SKSW.IRAIJIKAN   = iIraijikan          -- 依頼時間
        AND TKSM.TOKUISAKICD = URGT.TOKUISAKICD    -- 得意先コード
      );
  END UpdateUriageTable1;
  
  /*****************************************************************************/
  /* プロシージャ名 ：売上テーブル値引締め済み更新（代引き）                   */
  /* 引数           ：iSkyshimedd        請求締日                              */
  /*                : iShimeKaijoFlg     締解除フラグ                          */
  /*****************************************************************************/
  PROCEDURE UpdateUriageTable2(iSkyshimedd IN VARCHAR2,
                               iShimeKaijoFlg IN VARCHAR2
) IS
  BEGIN
    UPDATE S06T001T URGT -- 売上テーブル
    SET
      NEBIKISHIMEDYDM   = CASE iShimeKaijoFlg
                            WHEN '0' THEN TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'YYYYMM')
                            ELSE NULL
                          END,                                                 -- 値引締年月
      NEBIKISHIMEDD     = CASE iShimeKaijoFlg
                            WHEN '0' THEN TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'DD')
                            ELSE NULL
                          END,                                                 -- 値引締日
      NEBIKIPRCSJISSIDD = CASE iShimeKaijoFlg
                            WHEN '0' THEN vRecordS06T001T.KSHNUNYOYMD
                            ELSE '00000000'
                          END,                                                 -- 値引処理実施日
      KOSHAAPKIHONCD    = vRecordS06T001T.KOSHAAPKIHONCD,                      -- 更新者ＡＰ基本コード
      KOSHASHOPCD       = vRecordS06T001T.KOSHASHOPCD,                         -- 更新者ショップコード
      KOSHACD           = vRecordS06T001T.KOSHACD,                             -- 更新者コード
      KSHNSYSTMYMD      = vRecordS06T001T.KSHNSYSTMYMD,                        -- 更新システム日付
      KSHNSYSTMJIKAN    = vRecordS06T001T.KSHNSYSTMJIKAN,                      -- 更新システム時間
      KSHNUNYOYMD       = vRecordS06T001T.KSHNUNYOYMD,                         -- 更新運用日付
      KSHNIPADRSE       = vRecordS06T001T.KSHNIPADRSE,                         -- 更新ＩＰアドレス
      KSHNPGMID         = vRecordS06T001T.KSHNPGMID                            -- 更新プログラムＩＤ
    WHERE
          URGT.APKIHONCD         = vRecordS06T001T.APKIHONCD -- AP基本コード
      AND URGT.SHOPCD            = vRecordS06T001T.SHOPCD    -- ショップコード
      AND URGT.URIDD            <= iSkyshimedd               -- 売上日
      AND (
            (
                  iShimeKaijoFlg        = '0'
              AND URGT.NEBIKIPRCSJISSIDD = '00000000'        -- 値引処理実施日
            )
            OR
            (
                  iShimeKaijoFlg        = '1'
              AND URGT.NEBIKISHIMEDYDM = TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'YYYYMM')    -- 値引締年月
              AND URGT.NEBIKISHIMEDD = TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'DD')          -- 値引締日
            )
      )
      AND URGT.DATSKSMOTOKBN    NOT IN ('15','24')           --データ作成元区分
      AND URGT.URISHIMEPRCSDD   != '00000000'                -- 売上締処理日
      AND URGT.YUKOKBN          = '1'
      AND EXISTS (
        SELECT 1
        FROM
          S02M017T SKSM -- 請求先マスタ
        INNER JOIN
          S02M013T TKSM -- 得意先マスタ
        ON
              TKSM.APKIHONCD = SKSM.APKIHONCD      -- ＡＰ基本コード
          AND TKSM.SHOPCD    = SKSM.SHOPCD         -- ショップコード
          AND TKSM.SKYSAKICD = SKSM.SKYSAKICD      -- 請求先コード
        WHERE
            SKSM.APKIHONCD     = URGT.APKIHONCD      -- ＡＰ基本コード
        AND SKSM.SHOPCD        = URGT.SHOPCD         -- ショップコード
        AND SKSM.KAISHUHOHOKBN = '7'            -- 回収方法区分
        AND TKSM.TOKUISAKICD   = URGT.TOKUISAKICD    -- 得意先コード
      );
  END UpdateUriageTable2;

  /*****************************************************************************/
  /* プロシージャ名 ：処理対象得意先抽出                                       */
  /* 引数           ：iApKihoncd         ＡＰ基本コード                        */
  /*                ：iShopcd            ショップコード                        */
  /*                ：iUserApKihoncd     担当者ＡＰ基本コード                  */
  /*                ：iUserShopcd        担当者ＡＰ基本コード                  */
  /*                ：iTnshacd           担当者コード                          */
  /*                ：iIpadrse           ＩＰアドレス                          */
  /*                ：iIraidd            依頼日                                */
  /*                ：iIraijikan         依頼時間                              */
  /*                ：iSkyshimedd        請求締日                              */
  /*                ：iSyorimodo         処理モード（1:請求締、2:請求締解除、3:売上締、4:売上締解除） */
  /*****************************************************************************/
  PROCEDURE GetTokuisakicd(iApKihoncd     IN VARCHAR2,
                           iShopcd        IN VARCHAR2,
                           iUserApKihoncd IN VARCHAR2,
                           iUserShopcd    IN VARCHAR2,
                           iTnshacd       IN VARCHAR2,
                           iIpadrse       IN VARCHAR2,
                           iIraidd        IN VARCHAR2,
                           iIraijikan     IN VARCHAR2,
                           iSkyshimedd    IN VARCHAR2,
                           iSyorimodo     IN VARCHAR2
) IS
    TYPE TABLE_TYPE_TOKUISAKICD IS TABLE OF S02M013T.TOKUISAKICD%TYPE INDEX BY BINARY_INTEGER; -- 得意先マスタ.得意先コード
    wTokuisakicd_TAB TABLE_TYPE_TOKUISAKICD; -- 得意先コード
    wUriSuryo1       NUMBER(10, 1); -- 値引対象商品売上数
    wUriSuryo2       NUMBER(10, 1); -- 値引対象商品売上数
    wUriSuryo3       NUMBER(10, 1); -- 値引対象商品売上数

    --処理モードが'1'か'2'の時（請求締処理）
    --請求締ワーク、請求先マスタ、得意先マスタから依頼日時の締対象の得意先について、
    --請求締日が自動値引開始日終了日1～3の範囲内となるレコードかつ回収方法区分が代引以外を抽出する。
    CURSOR SELECT_TOKUISAKICD IS
    SELECT
        TKSM.TOKUISAKICD -- 得意先コード
    FROM
        S07W003T SSMW,      -- 請求締ワーク
        S02M017T SKSM,      -- 請求先マスタ
        S02M013T TKSM       -- 得意先マスタ
    WHERE
        SSMW.APKIHONCD     = vRecordS06T001T.APKIHONCD
    AND SSMW.SHOPCD        = vRecordS06T001T.SHOPCD
    AND SSMW.IRAIDD        = iIraidd                --依頼日
    AND SSMW.IRAIJIKAN     = iIraijikan             --依頼時間
    AND SKSM.APKIHONCD     = SSMW.APKIHONCD         --ＡＰ基本コード
    AND SKSM.SHOPCD        = SSMW.SHOPCD            --ショップコード
    AND SKSM.SKYSAKICD     = SSMW.SKYSAKICD         --請求先コード
    AND SKSM.KAISHUHOHOKBN != '7'                   --回収方法区分（代引以外）
    AND SKSM.APKIHONCD     = TKSM.APKIHONCD
    AND SKSM.SHOPCD        = TKSM.SHOPCD
    AND SKSM.SKYSAKICD     = TKSM.SKYSAKICD
    AND (
        (TKSM.JIDONEBIKICDN1 IS NOT NULL
        AND TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'YYYYMM') >= NVL(TKSM.NEBIKISTRTDYDMN1, '999999')
        AND NVL(TKSM.NEBIKISHRYDYDMN1, '000000') >= TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'YYYYMM'))
        OR
        (TKSM.JIDONEBIKICDN2 IS NOT NULL
        AND TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'YYYYMM') >= NVL(TKSM.NEBIKISTRTDYDMN2, '999999')
        AND NVL(TKSM.NEBIKISHRYDYDMN2, '000000') >= TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'YYYYMM'))
        OR
        (TKSM.JIDONEBIKICDN3 IS NOT NULL
        AND TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'YYYYMM') >= NVL(TKSM.NEBIKISTRTDYDMN3, '999999')
        AND NVL(TKSM.NEBIKISHRYDYDMN3, '000000') >= TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'YYYYMM'))
        )
    GROUP BY TKSM.TOKUISAKICD
    ;

    --パラメータ.処理モードが'3'か'4'の時（売上締処理）
    --請求先マスタ、得意先マスタから対象の得意先について、
    --請求締日（売上締日）が自動値引開始日終了日1～3の範囲内となるレコードかつ回収方法区分が代引を抽出する。
    CURSOR SELECT_TOKUISAKICD2 IS
    SELECT
        TKSM.TOKUISAKICD        -- 得意先コード
    FROM
        S02M017T SKSM,          -- 請求先マスタ
        S02M013T TKSM           -- 得意先マスタ
    WHERE
        SKSM.APKIHONCD     = vRecordS06T001T.APKIHONCD
    AND SKSM.SHOPCD        = vRecordS06T001T.SHOPCD
    AND SKSM.KAISHUHOHOKBN = '7'                       -- 回収方法区分（代引）
    AND SKSM.APKIHONCD     = TKSM.APKIHONCD
    AND SKSM.SHOPCD        = TKSM.SHOPCD
    AND SKSM.SKYSAKICD     = TKSM.SKYSAKICD
    AND (
        (TKSM.JIDONEBIKICDN1 IS NOT NULL
        AND TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'YYYYMM') >= NVL(TKSM.NEBIKISTRTDYDMN1, '999999')
        AND NVL(TKSM.NEBIKISHRYDYDMN1, '000000') >= TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'YYYYMM'))
        OR
        (TKSM.JIDONEBIKICDN2 IS NOT NULL
        AND TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'YYYYMM') >= NVL(TKSM.NEBIKISTRTDYDMN2, '999999')
        AND NVL(TKSM.NEBIKISHRYDYDMN2, '000000') >= TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'YYYYMM'))
        OR
        (TKSM.JIDONEBIKICDN3 IS NOT NULL
        AND TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'YYYYMM') >= NVL(TKSM.NEBIKISTRTDYDMN3, '999999')
        AND NVL(TKSM.NEBIKISHRYDYDMN3, '000000') >= TO_CHAR(TO_DATE(iSkyshimedd, 'YYYYMMDD'), 'YYYYMM'))
        )
    GROUP BY TKSM.TOKUISAKICD
    ;

  BEGIN
    IF (iSyorimodo IN ('1', '2')) THEN
        --請求締の場合
        -- 売上テーブル値引締め済更新（代引き以外）
        UpdateUriageTable1(iSkyshimedd, iIraidd, iIraijikan, CASE iSyorimodo WHEN '1' THEN '0' ELSE '1' END);
        -- 処理対象得意先抽出
        OPEN SELECT_TOKUISAKICD;
        FETCH SELECT_TOKUISAKICD BULK COLLECT INTO wTokuisakicd_TAB;
        FOR i IN 1 .. wTokuisakicd_TAB.COUNT LOOP
            -- 売上テーブルマイナス伝票作成
            Uriminusdnpysks(wTokuisakicd_TAB(i), iSkyshimedd, '15');

            --締処理の場合
            IF (iSyorimodo IN ('1', '3')) THEN
                -- 基本情報再設定
                SetUserInfo(iApKihoncd,         --ＡＰ基本コード
                            iShopcd,            --ショップコード
                            iSkyshimedd,        --請求締日
                            iUserApKihoncd,     --担当者ＡＰ基本コード
                            iUserShopcd,        --担当者ショップコード
                            iTnshacd,           --担当者コード
                            iIpadrse            --ＩＰアドレス
                            );

                -- 値引対象商品売上数量参照1
                wUriSuryo1 := GetUriSuryo1(wTokuisakicd_TAB(i), iSkyshimedd);

                -- 値引対象商品売上数量参照2
                wUriSuryo2 := GetUriSuryo2(wTokuisakicd_TAB(i), iSkyshimedd);

                -- 値引対象商品売上数量参照3
                wUriSuryo3 := GetUriSuryo3(wTokuisakicd_TAB(i), iSkyshimedd);

                -- 自動値引データ作成
                Jidonebikikidatasks(wTokuisakicd_TAB(i), iSkyshimedd, wUriSuryo1, wUriSuryo2, wUriSuryo3, iSyorimodo);
            END IF;
        END LOOP;
        CLOSE SELECT_TOKUISAKICD;
    ELSE
        --売上締の場合
        -- 売上テーブル値引締め済更新（代引き）
        UpdateUriageTable2(iSkyshimedd, CASE iSyorimodo WHEN '3' THEN '0' ELSE '1' END);
        -- 処理対象得意先抽出2
        OPEN SELECT_TOKUISAKICD2;
        FETCH SELECT_TOKUISAKICD2 BULK COLLECT INTO wTokuisakicd_TAB;
        FOR i IN 1 .. wTokuisakicd_TAB.COUNT LOOP
            -- 売上テーブルマイナス伝票作成
            Uriminusdnpysks(wTokuisakicd_TAB(i), iSkyshimedd, '24');

            --締処理の場合
            IF (iSyorimodo IN ('1', '3')) THEN
                -- 基本情報再設定
                SetUserInfo(iApKihoncd,         --ＡＰ基本コード
                            iShopcd,            --ショップコード
                            iSkyshimedd,        --請求締日
                            iUserApKihoncd,     --担当者ＡＰ基本コード
                            iUserShopcd,        --担当者ショップコード
                            iTnshacd,           --担当者コード
                            iIpadrse            --ＩＰアドレス
                            );

                -- 値引対象商品売上数量参照1
                wUriSuryo1 := GetUriSuryo1(wTokuisakicd_TAB(i), iSkyshimedd );

                -- 値引対象商品売上数量参照2
                wUriSuryo2 := GetUriSuryo2(wTokuisakicd_TAB(i), iSkyshimedd);

                -- 値引対象商品売上数量参照3
                wUriSuryo3 := GetUriSuryo3(wTokuisakicd_TAB(i), iSkyshimedd);

                -- 自動値引データ作成
                Jidonebikikidatasks(wTokuisakicd_TAB(i), iSkyshimedd, wUriSuryo1, wUriSuryo2, wUriSuryo3, iSyorimodo);
            END IF;
        END LOOP;
        CLOSE SELECT_TOKUISAKICD2;
    END IF;
  END GetTokuisakicd;

  /******************************************************************************/
  /* プロシージャ名 ：月次自動値引発生処理                                      */
  /* 引数           ：iApKihoncd         AP基本コード                           */
  /*                ：iShopcd            ショップコード                         */
  /*                ：iUserApKihoncd     担当者ＡＰ基本コード                   */
  /*                ：iUserShopcd        担当者ショップコード                   */
  /*                ：iTnshacd           担当者コード                           */
  /*                ：iIpadrse           ＩＰアドレス                           */
  /*                ：iIraidd            依頼日                                 */
  /*                ：iIraijikan         依頼時間                               */
  /*                ：iSkyshimedd        請求締日                               */
  /*                ：iSyorimodo         処理モード（1:請求締、2:請求締解除、3:売上締、4:売上締解除）*/
  /******************************************************************************/
  PROCEDURE Jidounebiki(iApKihoncd     IN VARCHAR2,
                        iShopcd        IN VARCHAR2,
                        iUserApKihoncd IN VARCHAR2,
                        iUserShopcd    IN VARCHAR2,
                        iTnshacd       IN VARCHAR2,
                        iIpadrse       IN VARCHAR2,
                        iIraidd        IN VARCHAR2,
                        iIraijikan     IN VARCHAR2,
                        iSkyshimedd    IN VARCHAR2,
                        iSyorimodo     IN VARCHAR2
  ) IS
  BEGIN
    -- 入力パラメータチェック
    IF iApKihoncd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20001, 'パラメータ.ＡＰ基本コード が設定されていません。');
    END IF;
    IF iShopcd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20002, 'パラメータ.ショップコード  が設定されていません。');
    END IF;
    IF iTnshacd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20003, 'パラメータ.担当者コード が設定されていません。');
    END IF;
    IF iIpadrse IS NULL THEN
      RAISE_APPLICATION_ERROR(-20004, 'パラメータ.ＩＰアドレス が設定されていません。');
    END IF;
    IF iIraidd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20005, 'パラメータ.依頼日  が設定されていません。');
    END IF;
    IF iIraijikan IS NULL THEN
      RAISE_APPLICATION_ERROR(-20006, 'パラメータ.依頼時間  が設定されていません。');
    END IF;
    IF iSkyshimedd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20007, 'パラメータ.請求締日  が設定されていません。');
    END IF;
    IF iUserApKihoncd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20008, 'パラメータ.UserＡＰ基本コード が設定されていません。');
    END IF;
    IF iUserShopcd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20009, 'パラメータ.Userショップコード  が設定されていません。');
    END IF;
    IF iSyorimodo IS NULL THEN
      RAISE_APPLICATION_ERROR(-20010, 'パラメータ.処理モード  が設定されていません。');
    END IF;
    IF (iSyorimodo IS NOT NULL AND (iSyorimodo NOT IN ('1','2','3','4'))) THEN
      RAISE_APPLICATION_ERROR(-20011, 'パラメータ.処理モード  が正しくありません。');
    END IF;

    BEGIN
      -- 基本情報設定
      SetUserInfo(iApKihoncd,         --ＡＰ基本コード
                  iShopcd,            --ショップコード
                  iSkyshimedd,        --請求締日
                  iUserApKihoncd,     --担当者ＡＰ基本コード
                  iUserShopcd,        --担当者ショップコード
                  iTnshacd,           --担当者コード
                  iIpadrse            --ＩＰアドレス
                  );

      -- 処理対象得意先抽出
      GetTokuisakicd(iApKihoncd,          --ＡＰ基本コード
                     iShopcd,             --ショップコード
                     iUserApKihoncd,      --担当者ＡＰ基本コード
                     iUserShopcd,         --担当者ショップコード
                     iTnshacd,            --担当者コード
                     iIpadrse,            --ＩＰアドレス
                     iIraidd,             --依頼日
                     iIraijikan,          --依頼時間
                     iSkyshimedd,         --請求締日
                     iSyorimodo           --処理モード（1締、2締解除、3:売上締、4:売上締解除）
                     );

      -- 共通変数削除
      vRecordS06T001T := null;
      vRecordS06T002T := null;

    EXCEPTION
      WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20710, SUBSTRB(SQLERRM, 1, 512));
    END;

  END Jidounebiki;

END D1505B01;
/
exit;
