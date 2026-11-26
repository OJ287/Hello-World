CREATE OR REPLACE PACKAGE BODY F0801B01
/*******************************************************************************
 * システム名       ：ＡＰ総合管理システム
 * 機能名           ：モバイルシステム向けデータ出力
 *
 ******************************************************************************T
   Date       Author         Changes Key                                 Version
   -----------------------------------------------------------------------------
   2022-05-25 ABI)Yokota       Create                                      1.0.0
 ******************************************************************************/
 AS
  /****************************************************************************/
  /* 共通変数宣言                                                               */
  /****************************************************************************/
  /* 登録者・更新者基本情報 */
  TYPE userinfo_rec_type IS RECORD (
                         APKIHONCD  VARCHAR2(3) -- AP基本コード
                       , SHOPCD     VARCHAR2(3) -- ショップコード
                       , TNSHACD    VARCHAR2(6) -- 担当者者コード
                       , SYSTMYMD   VARCHAR2(8) -- システム日付
                       , SYSTMJIKAN VARCHAR2(6) -- システム時間
                       , UNYOYMD    VARCHAR2(8) -- 運用日付
                       , IPADRSE    VARCHAR2(15)-- ＩＰアドレス
                       , PGMID      VARCHAR2(8) -- プログラムＩＤ
  );
  userinfo_rec userinfo_rec_type; -- 登録者・更新者基本情報

  /****************************************************************************/
  /* ファンクション名 ：文字設定値取得                                             */
  /* 引数             ：iApKihoncd           ＡＰ基本コード                      */
  /*                  ：iShopcd              ショップコード                      */
  /*                  ：iSetteino            設定番号                           */
  /*                  ：iSetteicd            設定コード                         */
  /*                  ：iOyasetteino         親設定番号                         */
  /*                  ：iOyasetteicd         親設定コード                        */
  /*                  ：iSetteinm            設定名称                           */
  /* return           ：returnMojisetteiata  文字設定値                         */
  /* error            ：-20021 設定されていません                                */
  /*                  ：-20022 複数見つかりました                                */
  /****************************************************************************/
  FUNCTION getMojisetteiatai( iApKihoncd     IN VARCHAR2
                            , iShopcd        IN VARCHAR2
                            , iSetteino      IN VARCHAR2
                            , iSetteicd      IN VARCHAR2
                            , iOyasetteino   IN VARCHAR2
                            , iOyasetteicd   IN VARCHAR2
                            , iSetteinm      IN VARCHAR2
  ) RETURN VARCHAR2 IS returnMojisetteiatai VARCHAR2(150);
    /* システム管理マスタから文字設定値の取得 */
    CURSOR SELECT_ATAI IS
      SELECT SYKM.MOJISETTEIATAI
        FROM S02M006T SYKM
       WHERE SYKM.APKIHONCD = iApKihoncd
         AND SYKM.SHOPCD    = iShopcd
         AND SYKM.SETTEINO  = iSetteino
         AND SYKM.RCRDKBN = 'M'
         AND SYKM.YUKOKBN = '1'
         AND ( iSetteicd IS NULL
             OR ( iSetteicd IS NOT NULL AND SYKM.SETTEICD = iSetteicd ) )
         AND ( iOyasetteino IS NULL
             OR ( iOyasetteino IS NOT NULL AND SYKM.OYASETTEINO = iOyasetteino ) )
         AND ( iOyasetteicd IS NULL
             OR ( iOyasetteicd IS NOT NULL AND SYKM.OYASETTEICD = iOyasetteicd ) )
    ;
  BEGIN
    returnMojisetteiatai := NULL;
    OPEN SELECT_ATAI;
    LOOP FETCH SELECT_ATAI INTO returnMojisetteiatai;
      IF SELECT_ATAI%ROWCOUNT = 0 THEN
         RAISE_APPLICATION_ERROR(-20021, iSetteinm||'が設定されていません。');
      END IF;
      EXIT WHEN SELECT_ATAI%NOTFOUND;

      -- 複数件チェック
      IF SELECT_ATAI%ROWCOUNT > 1 THEN
         RAISE_APPLICATION_ERROR(-20022, iSetteinm||'が複数見つかりました。');
      END IF;
    END LOOP;
    CLOSE SELECT_ATAI;

    RETURN(returnMojisetteiatai);
  END getMojisetteiatai;

  /****************************************************************************/
  /* プロシージャ名：基本情報設定                                                 */
  /* 引数             ：iApKihoncd           担当者ＡＰ基本コード                */
  /*                  ：iShopcd              担当者ショップコード                */
  /*                  ：iTnshacd             担当者コード                       */
  /*                  ：iIpaddress           ＩＰアドレス                       */
  /*                  ：iIpaddress           プログラムＩＤ                      */
  /****************************************************************************/
  PROCEDURE setUserInfo( iApKihoncd     IN VARCHAR2
                       , iShopcd        IN VARCHAR2
                       , iTnshacd       IN VARCHAR2
                       , iIpaddress     IN VARCHAR2
                       , iPgmid         IN VARCHAR2
  ) IS
  BEGIN
    -- システム日付 と システム時間の検索
-- 本線対応変更 start ABI
    --SELECT TO_CHAR(SYSDATE, 'YYYYMMDD')
    --     , TO_CHAR(SYSDATE, 'hh24miss')
    SELECT TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
         , TO_CHAR(CURRENT_DATE, 'hh24miss')
-- 本線対応変更 end ABI
      INTO userinfo_rec.SYSTMYMD
         , userinfo_rec.SYSTMJIKAN
      FROM DUAL;

    -- 運用日付
    userinfo_rec.UNYOYMD := S01GETOPERATIONDATE(iApKihoncd, iShopcd);

    -- その他の基本情報
    userinfo_rec.APKIHONCD := iApKihoncd; -- 担当者ＡＰ基本コード
    userinfo_rec.SHOPCD    := iShopcd;    -- 担当者ショップコード
    userinfo_rec.TNSHACD   := iTnshacd;   -- 担当者コード
    userinfo_rec.IPADRSE   := iIpaddress; -- ＩＰアドレス
    userinfo_rec.PGMID     := iPgmid;     -- プログラムＩＤ

  END setUserInfo;

  /****************************************************************************/
  /* プロシージャ名 ：配達予定データ出力                                           */
  /* 引数           ：iApkihoncd          ＡＰ基本コード                         */
  /*                ：iShopcd             ショップコード                         */
  /*                ：iTrmnlid            端末ID                               */
  /*                ：iDlvrykyotencd      配達拠点コード                         */
  /*                ：iDlvryytidd         配達予定日                            */
  /*                ：iDlvrytnshacd       配達担当者コード                       */
  /*                ：iOutputtrgtdd       出力対象日                            */
  /*                ：file_path           ファイルパス                           */
  /****************************************************************************/
  PROCEDURE csvDlvryyti( iApkihoncd IN VARCHAR2 -- ＡＰ基本コード
                       , iShopcd    IN VARCHAR2 -- ショップコード
                       , iTrmnlid   IN VARCHAR2 -- 端末ID
                       , iDlvrykyotencd IN VARCHAR2 -- 配達拠点コード
                       , iDlvryytidd    IN VARCHAR2 -- 配達予定日
                       , iDlvrytnshacd  IN VARCHAR2 -- 配達担当者コード
                       , iOutputtrgtdd  IN VARCHAR2 -- 出力対象日
                       , iOutputtrgtkbn IN VARCHAR2 -- 出力対象区分
                       , file_path      IN VARCHAR2 -- ファイルパス
  ) IS
  /* 変数宣言 */
  file_name       VARCHAR2(50) := NULL;
  file_handle    UTL_FILE.FILE_TYPE;
  keyAcptdnpyno  VARCHAR2(20) := NULL; -- 受注伝票キー
  ymJikaimntyti  S02M048T.JIKAIMNTYTIDYDM%type := NULL; -- （最小）次回メンテナンス年月
  /* 変数宣言：代引き残高金額計算用 */
  kinDaibikizd   VARCHAR2(15) := NULL; -- 代引き残高金額格納
  kinAcptdaibiki  NUMBER(12) := 0; -- 代引き残高金額計算用：代引き受注金額集計
  kinNohindaibiki NUMBER(12) := 0; -- 代引き残高金額計算用：代引き納品金額集計
  kinUridaibiki   NUMBER(12) := 0; -- 代引き残高金額計算用：代引き売上金額集計
  kinDaibikizd_aite   VARCHAR2(15) := NULL; -- 代引き残高金額格納（委託）
  kinAcptdaibiki_aite  NUMBER(12) := 0; -- 代引き残高金額計算用：代引き受注金額集計（委託）
  kinNohindaibiki_aite NUMBER(12) := 0; -- 代引き残高金額計算用：代引き納品金額集計（委託）
  kinUridaibiki_aite   NUMBER(12) := 0; -- 代引き残高金額計算用：代引き売上金額集計（委託）
  unyoymd_aite         VARCHAR2(8) := NULL; -- 運用日付（委託）
  /* 変数宣言：配布物取得用 */
  cdHfbApkihon1  S02M035T.APKIHONCD%type    := NULL; -- 配布物ＡＰ基本コード１
  cdHfbShop1     S02M035T.SHOPCD%type       := NULL; -- 配布物ショップコード１
  cdHaifubutsu1  S02M035T.HAIFUBUTSUCD%type := NULL; -- 配布物コード１
  nmHaifubutsu1  S02M035T.HAIFUBUTSUNM%type := NULL; -- 配布物名称１
  cdHfbApkihon2  S02M035T.APKIHONCD%type    := NULL; -- 配布物ＡＰ基本コード２
  cdHfbShop2     S02M035T.SHOPCD%type       := NULL; -- 配布物ショップコード２
  cdHaifubutsu2  S02M035T.HAIFUBUTSUCD%type := NULL; -- 配布物コード２
  nmHaifubutsu2  S02M035T.HAIFUBUTSUNM%type := NULL; -- 配布物名称２
  cdHfbApkihon3  S02M035T.APKIHONCD%type    := NULL; -- 配布物ＡＰ基本コード４
  cdHfbShop3     S02M035T.SHOPCD%type       := NULL; -- 配布物ショップコード４
  cdHaifubutsu3  S02M035T.HAIFUBUTSUCD%type := NULL; -- 配布物コード３
  nmHaifubutsu3  S02M035T.HAIFUBUTSUNM%type := NULL; -- 配布物名称３
  cdHfbApkihon4  S02M035T.APKIHONCD%type    := NULL; -- 配布物ＡＰ基本コード４
  cdHfbShop4     S02M035T.SHOPCD%type       := NULL; -- 配布物ショップコード４
  cdHaifubutsu4  S02M035T.HAIFUBUTSUCD%type := NULL; -- 配布物コード４
  nmHaifubutsu4  S02M035T.HAIFUBUTSUNM%type := NULL; -- 配布物名称４
  /* CURSOR：配達予定データ */
  CURSOR SELECT_DLVRYYTI IS
    SELECT JHTT.ACPTDNPYNO  -- 伝票番号
         , JHTT.EDANO       -- 伝票番号枝番
         , JHMT.ACPTDNPYMEISAINO    -- 受注伝票明細番号
         , JHMT.ACPTDNPYMEISAIEDANO -- 受注伝票明細枝番号
         , JHTT.APKIHONCD   -- AP基本コード
         , JHTT.SHOPCD      -- ショップコード
         , JHTT.ITAKUAITEAPKIHONCD  -- ＡＰ基本コード（委託）
         , JHTT.ITAKUAITESHOPCD     -- ショップ（委託）
         , JHTT.ITAKUAITEACPTDNPYNO -- 伝票番号（委託）
         , JHTT.ITAKUAITEEDANO      -- 伝票番号枝番（委託）
         , JHTT.TOKUISAKICD -- 得意先コード
         , TKSM.ITAKUAITETOKUISAKICD -- 得意先コード（委託）
         , JHTT.ACPTKBN    -- 受注区分
         , JHTT.ACPTKBNSHS -- 受注詳細区分
         , DPOM.DNPYOSHIRASENAIYON1 -- 伝票お知らせ内容
         , JHTT.ACPTDD  -- ご注文日
         , JHTT.DLVRYDD -- 宅配希望日
         , ( CASE WHEN JHTT.DLVRYKIBOJIKANKBN = '0' THEN ''
                  ELSE JHTT.DLVRYKIBOJIKAN || SYSM_KBJK.MOJISETTEIATAI || JHTT.DLVRYKIBOJIKANSHRY
             END ) TOKKIJIKO -- 特記事項
         , ( CASE WHEN JHTT.FZAIOKIKANOFLG  = '1' THEN '可' ELSE '' END ) FZAIOKI -- 不在置
         , TKSM.NOHINSAKIJOHON1 -- 納品先情報１
         , TKSM.NOHINSAKIJOHON2 -- 納品先情報２
         , JHTT.DLVRYTNSHACD -- 担当者コード
         , NVL(JHTT.JIKAIDLVRYYTIDD, '' ) JIKAIDLVRYYTIDD -- 次回配達予定日
         , JHMT.SHNCD    -- 商品コード
         , JHMT.KEIJOKBN -- 計上区分コード
         -- 数量１
         , ( CASE WHEN JHTT.DNPYSTS = '12' THEN JHMT.NOHINSURYO
                  ELSE JHMT.ACPTSURYO
             END ) SURYO1
         , SHMV.URITRTSKITANINM -- 単位
         -- 単価
         , ( CASE WHEN ( TKSM.KINOTPTNASHIFLG = '1'
                       OR JHMT.KEIJOKBN = '08'
                       OR JHMT.NYURYOKUMEISAIFLG = '0' ) -- 金額出力無し／経費／展開行
                  THEN ''
                  WHEN JHTT.DNPYSTS = '12'
                  THEN TO_CHAR(JHMT.NOHINUNT + JHMT.NOHINUNTSHOHITAXKIN)
                  ELSE TO_CHAR(JHMT.ACPTUNT  + JHMT.ACPTUNTSHOHITAXKIN)
             END ) UNT
         , NVL(JHMT.ACPTUNT,0) TAXNUKIUNT -- 税抜き単価
         -- 金額１
         , ( CASE WHEN ( TKSM.KINOTPTNASHIFLG = '1'
                       OR JHMT.KEIJOKBN = '08'
                       OR JHMT.NYURYOKUMEISAIFLG = '0' ) -- 金額出力無し／経費／展開行
                  THEN ''
                  WHEN JHTT.DNPYSTS = '12'
                  THEN ( CASE WHEN JHTT.TAXKEISANKBN = '1'
                              THEN TO_CHAR(JHMT.NOHINKIN + JHMT.NOHINSHOHITAXKIN) -- 内税：税込金額
                              ELSE TO_CHAR(JHMT.NOHINKIN)                         -- 外税：税抜金額
                         END )
                  ELSE ( CASE WHEN JHTT.TAXKEISANKBN = '1'
                              THEN TO_CHAR(JHMT.ACPTKIN  + JHMT.ACPTSHOHITAXKIN)  -- 内税：税込金額
                              ELSE TO_CHAR(JHMT.ACPTKIN)                          -- 外税：税抜金額
                         END )
             END ) KIN1
         -- 消費税金額
         , ( CASE WHEN ( TKSM.KINOTPTNASHIFLG = '1'
                       OR JHMT.KEIJOKBN = '08'
                       OR JHMT.NYURYOKUMEISAIFLG = '0' ) -- 金額出力無し／経費／展開行
                  THEN ''
                  WHEN JHTT.DNPYSTS = '12'
                  THEN TO_CHAR(JHMT.NOHINSHOHITAXKIN)
                  ELSE TO_CHAR(JHMT.ACPTSHOHITAXKIN)
             END ) SHOHITAXKIN
         , SHMV_AITE.URITRTSKITANINM ITAKUTANINM -- 単位（委託）
         -- 単価（委託）
         , ( CASE WHEN ( TKSM_AITE.TOKUISAKICD IS NULL
                       OR TKSM_AITE.KINOTPTNASHIFLG = '1'
                       OR JHMT.KEIJOKBN = '08'
                       OR JHMT.NYURYOKUMEISAIFLG = '0'
                       OR JHTT.DNPYSTS = '12' ) -- 金額出力無し／経費／展開行／配達売上済
                  THEN ''
                  ELSE TO_CHAR(JHMT.ITAKUCMPNYACPTUNT + JHMT.ITAKUCMPNYACPTUNTSHOHITAXKIN)
             END ) ITAKUUNT
         , NVL(JHMT.ITAKUCMPNYACPTUNT,0) TAXNUKIUNT_AITE -- 税抜き単価（委託）
         -- 金額（委託）
         , ( CASE WHEN ( TKSM_AITE.TOKUISAKICD IS NULL
                       OR TKSM_AITE.KINOTPTNASHIFLG = '1'
                       OR JHMT.KEIJOKBN = '08'
                       OR JHMT.NYURYOKUMEISAIFLG = '0'
                       OR JHTT.DNPYSTS = '12' ) -- 金額出力無し／経費／展開行／配達売上済
                  THEN ''
-- 本線対応変更 start ABI
--                  ELSE TO_CHAR( S01SyohizeiService.roundValue(((JHMT.ITAKUCMPNYACPTUNT + JHMT.ITAKUCMPNYACPTUNTSHOHITAXKIN)
                  ELSE TO_CHAR( Z1808B01.roundValue(((JHMT.ITAKUCMPNYACPTUNT + JHMT.ITAKUCMPNYACPTUNTSHOHITAXKIN)
-- 本線対応変更 end ABI
                               * JHMT.ACPTSURYO), SHPM_AITE.URIKINHASUKEISANKBN ))
             END ) ITAKUKIN
         -- 消費税金額（委託）
         , ( CASE WHEN ( TKSM_AITE.TOKUISAKICD IS NULL
                       OR TKSM_AITE.KINOTPTNASHIFLG = '1'
                       OR JHMT.KEIJOKBN = '08'
                       OR JHMT.NYURYOKUMEISAIFLG = '0'
                       OR JHTT.DNPYSTS = '12' ) -- 金額出力無し／経費／展開行／配達売上済
                  THEN ''
                  ELSE TO_CHAR(JHMT.ITAKUCMPNYACPTSHOHITAXKIN)
             END ) ITAKUSHOHITAXKIN
         , JHMT.MEISAIBIKO -- 備考
         -- メモ１
         , ( CASE WHEN SHPM.PRNTMEMON1INJIFLG = '1' THEN JHTT.PRNTMEMON1
                  ELSE ''
             END ) MEMON1
         -- メモ２
         , ( CASE WHEN SHPM.PRNTMEMON2INJIFLG = '1' THEN JHTT.PRNTMEMON2
                  ELSE ''
             END ) MEMON2
         -- メモ３
         , ( CASE WHEN SHPM.PRNTMEMON3INJIFLG = '1' THEN JHTT.PRNTMEMON3
                  ELSE ''
             END ) MEMON3
         , JHTT.DLVRYKYOTENCD -- 配達拠点コード
         , JHTT.DLVRYAREACD -- 配達エリアコード
         , JHTT.DLVRYJUN -- 配達順
         -- 代引き残高日付格納
         , ( CASE WHEN ( TKSM.KINOTPTNASHIFLG = '1'
                       OR ( SKSM.KAISHUHOHOKBN <> '7' AND JHTT.ACPTKBNSHS <> '43' )
                        ) -- 金額出力無し／回収方法代引き＆退会引上
                  THEN ''
                  ELSE JHTT.ZNKSHIMEDD
             END ) ZNKSHIMEDD
         /* 代引き残高金額格納計算用データ -- BEGIN */
         , TKSM.KINOTPTNASHIFLG -- 金額出力無しフラグ
         , TKSM_AITE.KINOTPTNASHIFLG KINOTPTNASHIFLG_AITE -- 金額出力無しフラグ（委託）
         , SKSM.KAISHUHOHOKBN -- 回収方法区分
         , SKSM_AITE.KAISHUHOHOKBN KAISHUHOHOKBN_AITE -- 回収方法区分（委託）
         , SHPM.TAIKAITHMIKAISHUKINDSPLYKBN -- 納品書未回収金額表示フラグ
         , SHPM_AITE.TAIKAITHMIKAISHUKINDSPLYKBN MIKAISHUKINDSPLYKBN_AITE -- 納品書未回収金額表示フラグ（委託）
         , JHTT.DNPYSTS -- 伝票ステータス
-- インボイス対応変更 start ABI
--         , NVL(TKJT.GENZAIZD,0) GENZAIZD -- 現在残高
--         , NVL(TKJT_AITE.GENZAIZD,0) GENZAIZD_AITE -- 現在残高（委託）
         , NVL(JHTT.ZNKKURIKOSHIKIN,0) ZNKKURIKOSHIKIN -- 前回繰越金額
-- インボイス対応変更 end ABI
         , NVL(JHTT.NOHINKIN,0) + NVL(JHTT.NOHINSHOHITAXKIN,0) NOHINZEIKOMIKIN -- 納品税込金額
         , NVL(JHTT_AITE.NOHINKIN,0) + NVL(JHTT_AITE.NOHINSHOHITAXKIN,0) NOHINZEIKOMIKIN_AITE -- 納品税込金額（委託）
         /* 代引き残高金額格納計算用データ -- END */
         , JHTT.ACPTTHDAIBIKIFLG -- 受注時代引フラグ
-- インボイス対応追加 start ABI
         , JHTT.SINGLDNPYFLG -- 単体伝票締めフラグ
-- インボイス対応追加 end ABI
         -- １２Ｌお預けボトル数
         , ( CASE WHEN SHPM.NOSHOAZYOKIKAZUDSPLYKBN = '1' THEN TO_CHAR(JHTT.AZUKARIBTLN1N2RITTORUHNS)
                  ELSE ''
             END ) AZUKARIBTLN1N2RITTORUHNS
         -- ８Ｌお預けボトル数
         , ( CASE WHEN SHPM.NOSHOAZYOKIKAZUDSPLYKBN = '1' THEN TO_CHAR(JHTT.AZUKARIBTLN8RITTORUHNS)
                  ELSE ''
             END ) AZUKARIBTLN8RITTORUHNS
         , JHTT.KYBTLN1N2RITTORUHNS -- １２Ｌ回収予定
         , JHTT.KYBTLN8RITTORUHNS   -- ８Ｌ回収予定
         , JHTT.ZNKDLVRYDD          -- 前回配達日
         -- 前回ご注文本数
         , ( CASE WHEN JHTT.DNPYSTS = '12' THEN 0
                  ELSE JHTT.ZNKDLVRYHNS
             END ) ZNKDLVRYHNS
         , JHTT.ACPTRCPTKEIROKBN -- 受注受付経路区分
         -- 取扱店名称
         , ( CASE WHEN TKSM.JUTAKUDLVRYCSTKBN = '2' THEN SHPM_TKS.SHOPNM
                  ELSE SHPM.SHOPNM
             END ) SHOPNM
         /* その他委託データ -- BEGIN */
         , SHPM_AITE.SHOPNM SHOPNM_AITE -- 委託ショップ名称
         , SHPM_AITE.CMPNYNMN1 CMPNYNMN1_AITE -- 委託会社名称１
         , SHPM_AITE.CMPNYNMN2 CMPNYNMN2_AITE -- 委託会社名称２
         , SHPM_AITE.YUBINNO YUBINNO_AITE -- 委託郵便番号
         , SHPM_AITE.ADRSN1 ADRSN1_AITE -- 委託住所１
         , SHPM_AITE.ADRSN2 ADRSN2_AITE -- 委託住所２
         , SHPM_AITE.ADRSN3 ADRSN3_AITE -- 委託住所３
         , SHPM_AITE.RENRAKUSAKINON1 RENRAKUSAKINON1_AITE -- 委託連絡先番号１
         , SHPM_AITE.RENRAKUSAKINON2 RENRAKUSAKINON2_AITE -- 委託連絡先番号２
         , SHPM_AITE.TAXHASUKEISANKBN TAXHASUKEISANKBN_AITE -- 委託税端数計算区分
         , SHPM_AITE.URIKINHASUKEISANKBN URIKINHASUKEISANKBN_AITE -- 委託売上金額端数計算区分
         , JHTT_AITE.TAXKEISANKBN TAXKEISANKBN_AITE -- 委託税計算区分
         , DPOM_AITE.DNPYOSHIRASENAIYON1 DNPYOSHIRASENAIYON1_AITE -- 委託伝票お知らせ内容
-- インボイス対応追加 start ABI
         , JHTT.ACPTIPPANNUKIKIN                                         -- 受注一般税抜金額
         , JHTT.ACPTIPPANTAXKIN                                          -- 受注一般消費税金額
         , JHTT.ACPTKEIGENNUKIKIN                                        -- 受注軽減税抜金額
         , JHTT.ACPTKEIGENTAXKIN                                         -- 受注軽減消費税金額
         , JHTT.ACPTHIKAZEIURIIKIN                                       -- 受注非課税金額
         , JHTT.ACPTFUKAZEIURIIKIN                                       -- 受注不課税売上金額
         , JHTT.NOHINIPPANNUKIKIN                                        -- 納品一般税抜金額
         , JHTT.NOHINIPPANTAXKIN                                         -- 納品一般消費税金額
         , JHTT.NOHINKEIGENNUKIKIN                                       -- 納品軽減税抜金額
         , JHTT.NOHINKEIGENTAXKIN                                        -- 納品軽減消費税金額
         , JHTT.NOHINHIKAZEIURIIKIN                                      -- 納品非課税金額
         , JHTT.NOHINFUKAZEIURIIKIN                                      -- 納品不課税売上金額
         , SHPM.INVOICEJIGYOSHAINPTNO                                    -- インボイス事業者登録番号
         , SHPM_AITE.INVOICEJIGYOSHAINPTNO AS INVOICEJIGYOSHAINPTNO_AITE -- 委託インボイス事業者登録番号
-- インボイス対応追加 end ABI
         /* その他委託データ -- END */
     FROM S04T001T JHTT
     INNER JOIN S04T002T JHMT -- 受注配達明細テーブル
             ON ( JHMT.APKIHONCD  = JHTT.APKIHONCD
                  AND JHMT.SHOPCD = JHTT.SHOPCD
                  AND JHMT.ACPTDNPYNO = JHTT.ACPTDNPYNO
                  AND JHMT.EDANO      = JHTT.EDANO
                  AND JHMT.MEISAIKBN <> '1' -- 1:配布物以外
                  AND JHMT.NYURYOKUMEISAIFLG = '1' -- 入力行のみ抽出
                )
     INNER JOIN S02M013T TKSM -- 得意先マスタ
             ON ( TKSM.APKIHONCD  = JHTT.APKIHONCD
                  AND TKSM.SHOPCD = JHTT.SHOPCD
                  AND TKSM.TOKUISAKICD = JHTT.TOKUISAKICD
                )
      LEFT OUTER JOIN S02M013T TKSM_AITE -- 得意先マスタ（委託）
              ON ( TKSM_AITE.APKIHONCD  = TKSM.ITAKUAITEAPKIHONCD
                   AND TKSM_AITE.SHOPCD = TKSM.ITAKUAITESHOPCD
                   AND TKSM_AITE.TOKUISAKICD = TKSM.ITAKUAITETOKUISAKICD
                 )
      LEFT JOIN S04T001T JHTT_AITE -- 受注配達テーブル（委託）
             ON ( JHTT_AITE.APKIHONCD  = JHTT.ITAKUAITEAPKIHONCD
                  AND JHTT_AITE.SHOPCD = JHTT.ITAKUAITESHOPCD
                  AND JHTT_AITE.ACPTDNPYNO = JHTT.ITAKUAITEACPTDNPYNO
                  AND JHTT_AITE.EDANO      = JHTT.ITAKUAITEEDANO
                )
      LEFT OUTER JOIN S02M050T TKJT -- 得意先実績テーブル
              ON ( TKJT.APKIHONCD  = TKSM.APKIHONCD
                   AND TKJT.SHOPCD = TKSM.SHOPCD
                   AND TKJT.TOKUISAKICD = TKSM.TOKUISAKICD
                 )
      LEFT OUTER JOIN S02M050T TKJT_AITE -- 得意先実績テーブル（委託）
              ON ( TKJT_AITE.APKIHONCD  = TKSM.ITAKUAITEAPKIHONCD
                   AND TKJT_AITE.SHOPCD = TKSM.ITAKUAITESHOPCD
                   AND TKJT_AITE.TOKUISAKICD = TKSM.ITAKUAITETOKUISAKICD
                 )
      LEFT OUTER JOIN S02M046T SHPM -- ショップマスタ
              ON ( SHPM.APKIHONCD  = JHTT.APKIHONCD
                   AND SHPM.SHOPCD = JHTT.SHOPCD
                 )
      LEFT OUTER JOIN S02M046T SHPM_TKS -- ショップマスタ：得意先ショップ
              ON ( SHPM_TKS.APKIHONCD  = TKSM.APKIHONCD
                   AND SHPM_TKS.SHOPCD = TKSM.TOKUISAKISHOPCD
                 )
      LEFT OUTER JOIN S02M046T SHPM_AITE -- ショップマスタ（委託）
              ON ( SHPM_AITE.APKIHONCD  = TKSM.ITAKUAITEAPKIHONCD
                   AND SHPM_AITE.SHOPCD = TKSM.ITAKUAITESHOPCD
                 )
      LEFT OUTER JOIN S02M023T DPOM -- 伝票お知らせマスタ
              ON ( DPOM.APKIHONCD  = JHTT.APKIHONCD
                   AND DPOM.SHOPCD = JHTT.SHOPCD
                   AND JHTT.DLVRYDD BETWEEN DPOM.TEKIYOSTRTDD AND DPOM.TEKIYOSHRYDD
                   AND DPOM.DNPYKIND = '1' -- 1:納品書兼受領書
                   AND DPOM.YUKOKBN  = '1'
                 )
      LEFT OUTER JOIN S02M023T DPOM_AITE -- 伝票お知らせマスタ（委託）
              ON ( DPOM_AITE.APKIHONCD  = JHTT.ITAKUAITEAPKIHONCD
                   AND DPOM_AITE.SHOPCD = JHTT.ITAKUAITESHOPCD
                   AND JHTT_AITE.DLVRYDD BETWEEN DPOM_AITE.TEKIYOSTRTDD AND DPOM_AITE.TEKIYOSHRYDD
                   AND DPOM_AITE.DNPYKIND = '1' -- 1:納品書兼受領書
                   AND DPOM_AITE.YUKOKBN  = '1'
                 )
      LEFT OUTER JOIN S02V001T SHMV -- 商品マスタビュー
              ON ( SHMV.APKIHONCD  = JHTT.APKIHONCD
                   AND SHMV.SHOPCD = JHTT.SHOPCD
                   AND SHMV.SHNCD = JHMT.SHNCD
                 )
      LEFT OUTER JOIN S02V001T SHMV_AITE -- 商品マスタビュー（委託）
              ON ( SHMV_AITE.APKIHONCD  = TKSM.ITAKUAITEAPKIHONCD
                   AND SHMV_AITE.SHOPCD = TKSM.ITAKUAITESHOPCD
                   AND SHMV_AITE.SHNCD = JHMT.SHNCD
                 )
      LEFT OUTER JOIN S02M006T SYSM_KBJK -- システム管理マスタ：配達希望時間区分
              ON ( SYSM_KBJK.APKIHONCD = '000' AND SYSM_KBJK.SHOPCD = '000'
                   AND SYSM_KBJK.SETTEINO = '012' AND SYSM_KBJK.RCRDKBN = 'M'
                   AND SYSM_KBJK.SETTEICD = JHTT.DLVRYKIBOJIKANKBN
                   AND SYSM_KBJK.YUKOKBN = '1'
                 )
      LEFT OUTER JOIN S02M017T SKSM -- 請求先マスタ
              ON ( SKSM.APKIHONCD  = TKSM.APKIHONCD
                   AND SKSM.SHOPCD = TKSM.SHOPCD
                   AND SKSM.SKYSAKICD = TKSM.SKYSAKICD
                 )
      LEFT OUTER JOIN S02M017T SKSM_AITE -- 請求先マスタ
              ON ( SKSM_AITE.APKIHONCD  = TKSM_AITE.APKIHONCD
                   AND SKSM_AITE.SHOPCD = TKSM_AITE.SHOPCD
                   AND SKSM_AITE.SKYSAKICD = TKSM_AITE.SKYSAKICD
                 )
     WHERE JHTT.APKIHONCD = iApkihoncd
       AND JHTT.SHOPCD    = iShopcd
       AND JHTT.DLVRYKANKATSUAPKIHONCD = iApkihoncd
       AND JHTT.DLVRYKANKATSUSHOPCD = iShopcd
       AND JHTT.DLVRYKYOTENCD = iDlvrykyotencd
-- 本線対応変更 start ABI
--       AND JHTT.DLVRYYTIDD    = iDlvryytidd
       AND JHTT.DLVRYYTIDD    <= iDlvryytidd
-- 本線対応変更 end ABI
       AND JHTT.DLVRYTNSHACD  = iDlvrytnshacd
       AND JHTT.DLVRYKBN IN ( '1', '2' ) -- 1：自社配達  2：他社配達
       AND JHTT.DNPYSTS = '11' -- 11：配達中
       AND JHTT.DLVRYSTS = '11' -- 11：配達中
       AND TKSM.TOKUISAKIKBN IN ('1', '5', '6') -- 1：顧客 5：NA受託 6：配達受託
       AND JHTT.YUKOKBN = '1'
     ORDER BY JHTT.APKIHONCD, JHTT.SHOPCD, JHTT.ACPTDNPYNO, JHTT.EDANO
            , JHMT.ACPTDNPYMEISAINO, JHMT.ACPTDNPYMEISAIEDANO
     FOR UPDATE OF JHTT.ACPTDNPYNO
  ;
  BEGIN
    file_name  := FID_DLVRYYTI
                 || '_' || iTrmnlid
                 || '_' || userinfo_rec.SYSTMYMD || userinfo_rec.SYSTMJIKAN
                 || '_' || NVL(iOutputtrgtdd, '00000000') || '.csv';
-- 本線対応変更 start ABI
--    file_handle := UTL_FILE.FOPEN( file_path , file_name, 'w' );
    file_handle := UTL_FILE.FOPEN( 'PL_SQL_DIR2' , file_name, 'w' );
-- 本線対応変更 end ABI

    -- 配達予定データ出力：0：配達予定のみ、または、2：全て の場合
    IF iOutputtrgtkbn NOT IN ('0', '2') THEN
       UTL_FILE.FCLOSE_ALL;
-- 本線対応追加 start ABI
       FILEUPLOADTOS3( file_path , file_name );
-- 本線対応追加 end ABI
       RETURN ;
    END IF;

    FOR dlvryyti_rec IN SELECT_DLVRYYTI LOOP
        -- 伝票別で取得：配布物１～４、代引き残高金額、代引き残高金額（委託）
        IF keyAcptdnpyno IS NULL
           OR keyAcptdnpyno <> (dlvryyti_rec.APKIHONCD||dlvryyti_rec.SHOPCD||dlvryyti_rec.ACPTDNPYNO||dlvryyti_rec.EDANO ) THEN
           keyAcptdnpyno := dlvryyti_rec.APKIHONCD||dlvryyti_rec.SHOPCD||dlvryyti_rec.ACPTDNPYNO||dlvryyti_rec.EDANO;

           -- 配布物取得
           cdHfbApkihon1 := NULL; -- 配布物ＡＰ基本コード１
           cdHfbShop1    := NULL; -- 配布物ショップコード１
           cdHaifubutsu1 := NULL; -- 配布物コード１
           nmHaifubutsu1 := NULL; -- 配布物名称１

           cdHfbApkihon2 := NULL; -- 配布物ＡＰ基本コード２
           cdHfbShop2    := NULL; -- 配布物ショップコード２
           cdHaifubutsu2 := NULL; -- 配布物コード２
           nmHaifubutsu2 := NULL; -- 配布物名称２

           cdHfbApkihon3 := NULL; -- 配布物ＡＰ基本コード３
           cdHfbShop3    := NULL; -- 配布物ショップコード３
           cdHaifubutsu3 := NULL; -- 配布物コード３
           nmHaifubutsu3 := NULL; -- 配布物名称３

           cdHfbApkihon4 := NULL; -- 配布物ＡＰ基本コード４
           cdHfbShop4    := NULL; -- 配布物ショップコード４
           cdHaifubutsu4 := NULL; -- 配布物コード４
           nmHaifubutsu4 := NULL; -- 配布物名称４
           FOR hfb_rec IN ( SELECT JHMT_HFB.HAIFUBUTSUAPKIHONCD AS APKIHONCD
                                 , JHMT_HFB.HAIFUBUTSUSHOPCD AS SHOPCD
                                 , JHMT_HFB.HAIFUBUTSUCD
                                 , HFBM.HAIFUBUTSUNM
                              FROM S04T002T JHMT_HFB
                              LEFT OUTER JOIN S02M035T HFBM -- 配布物マスタ
                                      ON ( HFBM.APKIHONCD  = JHMT_HFB.HAIFUBUTSUAPKIHONCD
                                         AND HFBM.SHOPCD = JHMT_HFB.HAIFUBUTSUSHOPCD
                                         AND HFBM.HAIFUBUTSUCD = JHMT_HFB.HAIFUBUTSUCD
                                         )
                             WHERE JHMT_HFB.APKIHONCD = dlvryyti_rec.APKIHONCD
                               AND JHMT_HFB.SHOPCD    = dlvryyti_rec.SHOPCD
                               AND JHMT_HFB.ACPTDNPYNO = dlvryyti_rec.ACPTDNPYNO
                               AND JHMT_HFB.EDANO      = dlvryyti_rec.EDANO
                               AND JHMT_HFB.MEISAIKBN = '1' -- 明細区分：配布物
                             ORDER BY JHMT_HFB.ACPTDNPYMEISAINO, JHMT_HFB.ACPTDNPYMEISAIEDANO
                          ) LOOP
              IF cdHaifubutsu1 IS NULL THEN -- 配布物コード１
                 cdHfbApkihon1 := hfb_rec.APKIHONCD; -- 配布物ＡＰ基本コード１
                 cdHfbShop1    := hfb_rec.SHOPCD;    -- 配布物ショップコード１
                 cdHaifubutsu1 := hfb_rec.HAIFUBUTSUCD; -- 配布物コード１
                 nmHaifubutsu1 := hfb_rec.HAIFUBUTSUNM; -- 配布物名称１
              ELSIF cdHaifubutsu2 IS NULL THEN -- 配布物コード２
                 cdHfbApkihon2 := hfb_rec.APKIHONCD; -- 配布物ＡＰ基本コード２
                 cdHfbShop2    := hfb_rec.SHOPCD;    -- 配布物ショップコード２
                 cdHaifubutsu2 := hfb_rec.HAIFUBUTSUCD; -- 配布物コード２
                 nmHaifubutsu2 := hfb_rec.HAIFUBUTSUNM; -- 配布物名称２
              ELSIF cdHaifubutsu3 IS NULL THEN -- 配布物コード３
                 cdHfbApkihon3 := hfb_rec.APKIHONCD; -- 配布物ＡＰ基本コード３
                 cdHfbShop3    := hfb_rec.SHOPCD;    -- 配布物ショップコード３
                 cdHaifubutsu3 := hfb_rec.HAIFUBUTSUCD; -- 配布物コード３
                 nmHaifubutsu3 := hfb_rec.HAIFUBUTSUNM; -- 配布物名称３
              ELSIF cdHaifubutsu4 IS NULL THEN -- 配布物コード４
                 cdHfbApkihon4 := hfb_rec.APKIHONCD; -- 配布物ＡＰ基本コード４
                 cdHfbShop4    := hfb_rec.SHOPCD;    -- 配布物ショップコード４
                 cdHaifubutsu4 := hfb_rec.HAIFUBUTSUCD; -- 配布物コード４
                 nmHaifubutsu4 := hfb_rec.HAIFUBUTSUNM; -- 配布物名称４
              END IF;
           END LOOP;

           -- 代引き残高金額計算
           kinDaibikizd := '';
           IF dlvryyti_rec.KINOTPTNASHIFLG = '1'
              OR ( dlvryyti_rec.KAISHUHOHOKBN <> '7' AND dlvryyti_rec.ACPTKBNSHS <> '43' )
              -- 金額出力無し／回収方法代引き＆退会引上
              THEN
              kinDaibikizd := '';
           ELSIF ( dlvryyti_rec.ACPTKBNSHS = '43' AND dlvryyti_rec.TAIKAITHMIKAISHUKINDSPLYKBN <> '1' )
              -- 退会引上＆納品書未回収金額表示しない
              THEN
              kinDaibikizd := '';
           ELSE
-- インボイス対応変更 start ABI
--              BEGIN
--                -- 代引き受注金額を集計
--                SELECT SUM( NVL(JHMT_DAIBIKI.ACPTKIN,0)
--                            + NVL(JHMT_DAIBIKI.ACPTSHOHITAXKIN,0)
--                          )
--                     , SUM( NVL(JHMT_DAIBIKI.NOHINKIN,0)
--                            + NVL(JHMT_DAIBIKI.NOHINSHOHITAXKIN,0)
--                          )
--                  INTO kinAcptdaibiki
--                     , kinNohindaibiki
--                  FROM S04T002T JHMT_DAIBIKI
--                 WHERE JHMT_DAIBIKI.APKIHONCD  = dlvryyti_rec.APKIHONCD
--                   AND JHMT_DAIBIKI.SHOPCD     = dlvryyti_rec.SHOPCD
--                   AND JHMT_DAIBIKI.ACPTDNPYNO = dlvryyti_rec.ACPTDNPYNO
--                   AND JHMT_DAIBIKI.EDANO      = dlvryyti_rec.EDANO
--                   AND JHMT_DAIBIKI.MEISAIKBN  = '2'
--                 GROUP BY JHMT_DAIBIKI.APKIHONCD, JHMT_DAIBIKI.SHOPCD
--                        , JHMT_DAIBIKI.ACPTDNPYNO, JHMT_DAIBIKI.EDANO
--                ;
--                EXCEPTION
--                  WHEN NO_DATA_FOUND THEN
--                    kinAcptdaibiki := 0;
--                    kinNohindaibiki := 0;
--              END;
--              -- 代引き売上金額を集計
--              IF dlvryyti_rec.KAISHUHOHOKBN = '7' THEN
--                 BEGIN
--                   SELECT SUM(URIT.URIKIN + URIT.SHOHITAXKIN)
--                     INTO kinUridaibiki
--                     FROM S06T001T URIT
--                    WHERE URIT.APKIHONCD = dlvryyti_rec.APKIHONCD
--                      AND URIT.SHOPCD    = dlvryyti_rec.SHOPCD
--                      AND URIT.TOKUISAKICD = dlvryyti_rec.TOKUISAKICD
--                      AND URIT.URIDD > userinfo_rec.UNYOYMD
--                      AND NOT EXISTS ( SELECT 1 FROM S04T002T JHMT_SUB
--                                       WHERE JHMT_SUB.MEISAIKBN  = '2'
--                                         AND JHMT_SUB.APKIHONCD  = dlvryyti_rec.APKIHONCD
--                                         AND JHMT_SUB.SHOPCD     = dlvryyti_rec.SHOPCD
--                                         AND JHMT_SUB.ACPTDNPYNO = URIT.URIDNPYNO
--                                         AND JHMT_SUB.EDANO      = URIT.EDANO
--                                     )
--                    GROUP BY URIT.APKIHONCD, URIT.SHOPCD, URIT.TOKUISAKICD
--                   ;
--                   EXCEPTION
--                     WHEN NO_DATA_FOUND THEN
--                       kinUridaibiki := 0;
--                 END;
--              ELSE
--                 kinUridaibiki := 0;
--              END IF;
--              IF dlvryyti_rec.DNPYSTS = '12' THEN -- 配達済
--                 kinDaibikizd := TO_CHAR( dlvryyti_rec.GENZAIZD
--                                          - dlvryyti_rec.NOHINZEIKOMIKIN
--                                          - ( CASE WHEN kinAcptdaibiki = 0 THEN kinNohindaibiki ELSE kinAcptdaibiki END)
--                                          - kinUridaibiki
--                                        );
--              ELSE
--                 kinDaibikizd := TO_CHAR( NVL(dlvryyti_rec.GENZAIZD, 0)
--                                          - ( CASE WHEN kinAcptdaibiki = 0 THEN kinNohindaibiki ELSE kinAcptdaibiki END)
--                                          - kinUridaibiki
--                                        );
--              END IF;
              -- 前回繰越金額を設定する
              kinDaibikizd := dlvryyti_rec.ZNKKURIKOSHIKIN;
-- インボイス対応変更 end ABI
           END IF;

-- インボイス対応削除 start ABI
--           -- 代引き残高金額（委託）計算
--           kinDaibikizd_aite := '';
--           IF dlvryyti_rec.ITAKUAITEACPTDNPYNO IS NULL THEN -- 委託相手伝票がない場合は除外
--              kinDaibikizd_aite := '';
--           ELSIF dlvryyti_rec.KINOTPTNASHIFLG_AITE = '1'
--              OR ( dlvryyti_rec.KAISHUHOHOKBN_AITE <> '7' AND dlvryyti_rec.ACPTKBNSHS <> '43' )
--              -- 金額出力無し／回収方法代引き＆退会引上
--              THEN
--              kinDaibikizd_aite := '';
--           ELSIF ( dlvryyti_rec.ACPTKBNSHS = '43' AND dlvryyti_rec.MIKAISHUKINDSPLYKBN_AITE <> '1' )
--              -- 退会引上＆納品書未回収金額表示しない
--              THEN
--              kinDaibikizd_aite := '';
--           ELSE
--              BEGIN
--                -- 代引き受注金額を集計
--                SELECT SUM( NVL(JHMT_DAIBIKI.ACPTKIN,0)
--                            + NVL(JHMT_DAIBIKI.ACPTSHOHITAXKIN,0)
--                          )
--                     , SUM( NVL(JHMT_DAIBIKI.NOHINKIN,0)
--                            + NVL(JHMT_DAIBIKI.NOHINSHOHITAXKIN,0)
--                          )
--                  INTO kinAcptdaibiki_aite
--                     , kinNohindaibiki_aite
--                  FROM S04T002T JHMT_DAIBIKI
--                 WHERE JHMT_DAIBIKI.APKIHONCD  = dlvryyti_rec.ITAKUAITEAPKIHONCD
--                   AND JHMT_DAIBIKI.SHOPCD     = dlvryyti_rec.ITAKUAITESHOPCD
--                   AND JHMT_DAIBIKI.ACPTDNPYNO = dlvryyti_rec.ITAKUAITEACPTDNPYNO
--                   AND JHMT_DAIBIKI.EDANO      = dlvryyti_rec.ITAKUAITEEDANO
--                   AND JHMT_DAIBIKI.MEISAIKBN  = '2'
--                 GROUP BY JHMT_DAIBIKI.APKIHONCD, JHMT_DAIBIKI.SHOPCD
--                        , JHMT_DAIBIKI.ACPTDNPYNO, JHMT_DAIBIKI.EDANO
--                ;
--                EXCEPTION
--                  WHEN NO_DATA_FOUND THEN
--                    kinAcptdaibiki_aite := 0;
--                    kinNohindaibiki_aite := 0;
--              END;
--              -- 代引き売上金額を集計
--              IF dlvryyti_rec.KAISHUHOHOKBN_AITE = '7' THEN
--                 -- 委託の運用日付
--                 unyoymd_aite := S01GETOPERATIONDATE(dlvryyti_rec.ITAKUAITEAPKIHONCD, dlvryyti_rec.ITAKUAITESHOPCD);
--                 BEGIN
--                   SELECT SUM(URIT.URIKIN + URIT.SHOHITAXKIN)
--                     INTO kinUridaibiki
--                     FROM S06T001T URIT
--                    WHERE URIT.APKIHONCD = dlvryyti_rec.ITAKUAITEAPKIHONCD
--                      AND URIT.SHOPCD    = dlvryyti_rec.ITAKUAITESHOPCD
--                      AND URIT.TOKUISAKICD = dlvryyti_rec.ITAKUAITETOKUISAKICD
--                      AND URIT.URIDD > unyoymd_aite
--                      AND NOT EXISTS ( SELECT 1 FROM S04T002T JHMT_SUB
--                                       WHERE JHMT_SUB.MEISAIKBN  = '2'
--                                         AND JHMT_SUB.APKIHONCD  = dlvryyti_rec.ITAKUAITEAPKIHONCD
--                                         AND JHMT_SUB.SHOPCD     = dlvryyti_rec.ITAKUAITESHOPCD
--                                         AND JHMT_SUB.ACPTDNPYNO = URIT.URIDNPYNO
--                                         AND JHMT_SUB.EDANO      = URIT.EDANO
--                                     )
--                    GROUP BY URIT.APKIHONCD, URIT.SHOPCD, URIT.TOKUISAKICD
--                   ;
--                   EXCEPTION
--                     WHEN NO_DATA_FOUND THEN
--                       kinUridaibiki_aite := 0;
--                 END;
--              ELSE
--                 kinUridaibiki_aite := 0;
--              END IF;
--              IF dlvryyti_rec.DNPYSTS = '12' THEN -- 配達済
--                 kinDaibikizd_aite := TO_CHAR( dlvryyti_rec.GENZAIZD_AITE
--                                          - dlvryyti_rec.NOHINZEIKOMIKIN_AITE
--                                          - ( CASE WHEN kinAcptdaibiki_aite = 0 THEN kinNohindaibiki_aite ELSE kinAcptdaibiki_aite END)
--                                          - kinUridaibiki_aite
--                                        );
--              ELSE
--                 kinDaibikizd_aite := TO_CHAR( NVL(dlvryyti_rec.GENZAIZD_AITE, 0)
--                                          - ( CASE WHEN kinAcptdaibiki_aite = 0 THEN kinNohindaibiki_aite ELSE kinAcptdaibiki_aite END)
--                                          - kinUridaibiki_aite
--                                        );
--              END IF;
--           END IF;
-- インボイス対応削除 end ABI
        END IF;

        -- 伝票明細別で取得：次回メンテナンス予定年月
        IF dlvryyti_rec.SHNCD IS NOT NULL THEN
           BEGIN
             SELECT MIN(TSSM.JIKAIMNTYTIDYDM)
               INTO ymJikaimntyti
               FROM S02M048T TSSM
              WHERE TSSM.APKIHONCD = dlvryyti_rec.APKIHONCD
                AND TSSM.SHOPCD    = dlvryyti_rec.SHOPCD
                AND TSSM.TOKUISAKICD = dlvryyti_rec.TOKUISAKICD
                AND TSSM.SHNCD = dlvryyti_rec.SHNCD
                AND TSSM.YUKOKBN = '1'
             ;
             EXCEPTION
               WHEN NO_DATA_FOUND THEN
                 ymJikaimntyti := '';
           END;
        END IF;

        -- ＣＳＶ作成
        UTL_FILE.PUT( file_handle,
          REPLACE(dlvryyti_rec.ACPTDNPYNO, ',') -- 伝票番号
         || ',' || REPLACE(dlvryyti_rec.EDANO, ',') -- 伝票番号枝番
         || ',' || REPLACE(dlvryyti_rec.APKIHONCD, ',') -- AP基本コード
         || ',' || REPLACE(dlvryyti_rec.SHOPCD, ',') -- ショップコード
         || ',' || REPLACE(dlvryyti_rec.TOKUISAKICD, ',') -- 得意先コード
         || ',' || REPLACE(dlvryyti_rec.ACPTKBN, ',') -- 受注区分
         || ',' || REPLACE(dlvryyti_rec.ACPTKBNSHS, ',') -- 受注詳細区分
         || ',' || REPLACE(dlvryyti_rec.DNPYOSHIRASENAIYON1, ',') -- 伝票お知らせ内容
         || ',' || REPLACE(dlvryyti_rec.ACPTDD, ',') -- ご注文日
         || ',' || REPLACE(dlvryyti_rec.DLVRYDD, ',') -- 宅配希望日
         || ',' || REPLACE(dlvryyti_rec.TOKKIJIKO, ',') -- 特記事項
         || ',' || REPLACE(dlvryyti_rec.FZAIOKI, ',') -- 不在置
         || ',' || REPLACE(dlvryyti_rec.NOHINSAKIJOHON1, ',') -- 納品先情報１
         || ',' || REPLACE(dlvryyti_rec.NOHINSAKIJOHON2, ',') -- 納品先情報２
         || ',' || REPLACE(dlvryyti_rec.DLVRYTNSHACD, ',') -- 担当者コード
         || ',' || REPLACE(dlvryyti_rec.JIKAIDLVRYYTIDD, ',') -- 次回配達予定日
         || ',' || REPLACE(dlvryyti_rec.ACPTDNPYMEISAINO, ',') -- 受注伝票明細番号
         || ',' || REPLACE(dlvryyti_rec.ACPTDNPYMEISAIEDANO, ',') -- 受注伝票明細枝番号
         || ',' || REPLACE(dlvryyti_rec.SHNCD, ',') -- 商品コード
         || ',' || REPLACE(dlvryyti_rec.KEIJOKBN, ',') -- 計上区分コード
         || ',' || REPLACE(dlvryyti_rec.SURYO1, ',') -- 数量１
         || ',' || REPLACE(dlvryyti_rec.URITRTSKITANINM, ',') -- 単位
         || ',' || REPLACE(dlvryyti_rec.UNT, ',') -- 単価
         || ',' || REPLACE(dlvryyti_rec.TAXNUKIUNT, ',') -- 税抜き単価
         || ',' || REPLACE(dlvryyti_rec.KIN1, ',') -- 金額１
         || ',' || REPLACE(dlvryyti_rec.SHOHITAXKIN, ',') -- 消費税金額
         || ',' || REPLACE(dlvryyti_rec.ITAKUTANINM, ',') -- 単位（委託）
         || ',' || REPLACE(dlvryyti_rec.ITAKUUNT, ',') -- 単価（委託）
         || ',' || REPLACE(dlvryyti_rec.TAXNUKIUNT_AITE, ',') -- 税抜き単価（委託）
         || ',' || REPLACE(dlvryyti_rec.ITAKUKIN, ',') -- 金額（委託）
         || ',' || REPLACE(dlvryyti_rec.ITAKUSHOHITAXKIN, ',') -- 消費税金額（委託）
         || ',' || REPLACE(dlvryyti_rec.MEISAIBIKO, ',') -- 備考
         || ',' || REPLACE(dlvryyti_rec.MEMON1, ',') -- メモ１
         || ',' || REPLACE(dlvryyti_rec.MEMON2, ',') -- メモ２
         || ',' || REPLACE(dlvryyti_rec.MEMON3, ',') -- メモ３
         || ',' || REPLACE(dlvryyti_rec.DLVRYKYOTENCD, ',') -- 配達拠点コード
         || ',' || REPLACE(dlvryyti_rec.DLVRYAREACD, ',') -- 配達エリアコード
         || ',' || REPLACE(dlvryyti_rec.DLVRYJUN, ',') -- 配達順
         || ',' || REPLACE(cdHfbApkihon1, ',') -- 配布物ＡＰ基本コード１
         || ',' || REPLACE(cdHfbShop1, ',')    -- 配布物ショップコード１
         || ',' || REPLACE(cdHaifubutsu1, ',') -- 配布物コード１
         || ',' || REPLACE(nmHaifubutsu1, ',') -- 配布物名称１
         || ',' || REPLACE(cdHfbApkihon2, ',') -- 配布物ＡＰ基本コード２
         || ',' || REPLACE(cdHfbShop2, ',')    -- 配布物ショップコード２
         || ',' || REPLACE(cdHaifubutsu2, ',') -- 配布物コード２
         || ',' || REPLACE(nmHaifubutsu2, ',') -- 配布物名称２
         || ',' || REPLACE(cdHfbApkihon3, ',') -- 配布物ＡＰ基本コード３
         || ',' || REPLACE(cdHfbShop3, ',')    -- 配布物ショップコード３
         || ',' || REPLACE(cdHaifubutsu3, ',') -- 配布物コード３
         || ',' || REPLACE(nmHaifubutsu3, ',') -- 配布物名称３
         || ',' || REPLACE(cdHfbApkihon4, ',') -- 配布物ＡＰ基本コード４
         || ',' || REPLACE(cdHfbShop4, ',')    -- 配布物ショップコード４
         || ',' || REPLACE(cdHaifubutsu4, ',') -- 配布物コード４
         || ',' || REPLACE(nmHaifubutsu4, ',') -- 配布物名称４
         || ',' || REPLACE(dlvryyti_rec.ZNKSHIMEDD, ',') -- 代引き残高日付格納
         || ',' || REPLACE(TO_CHAR(kinDaibikizd), ',')   -- 代引き残高金額格納
         || ',' || REPLACE(TO_CHAR(kinDaibikizd_aite), ',') -- 代引き残高金額格納（委託）
         || ',' || REPLACE(dlvryyti_rec.ACPTTHDAIBIKIFLG, ',') -- 受注時代引フラグ
-- インボイス対応追加 start ABI
         || ',' || REPLACE(dlvryyti_rec.SINGLDNPYFLG, ',') -- 単体伝票締めフラグ
-- インボイス対応追加 end ABI
         || ',' || REPLACE(dlvryyti_rec.AZUKARIBTLN1N2RITTORUHNS, ',') -- １２Ｌお預けボトル数
         || ',' || REPLACE(dlvryyti_rec.AZUKARIBTLN8RITTORUHNS, ',')   -- ８Ｌお預けボトル数
         || ',' || REPLACE(dlvryyti_rec.KYBTLN1N2RITTORUHNS, ',') -- １２Ｌ回収予定
         || ',' || REPLACE(dlvryyti_rec.KYBTLN8RITTORUHNS, ',')   -- ８Ｌ回収予定
         || ',' || REPLACE(dlvryyti_rec.ZNKDLVRYDD, ',')  -- 前回配達日
         || ',' || REPLACE(dlvryyti_rec.ZNKDLVRYHNS, ',') -- 前回ご注文本数
         || ',' || REPLACE(dlvryyti_rec.ACPTRCPTKEIROKBN, ',') -- 受注受付経路区分
         || ',' || REPLACE(dlvryyti_rec.SHOPNM, ',') -- 取扱店名
         || ',' || REPLACE(ymJikaimntyti, ',') -- 次回メンテナンス予定年月
         || ',' || REPLACE(dlvryyti_rec.ITAKUAITETOKUISAKICD, ',') -- 委託得意先コード
         || ',' || REPLACE(dlvryyti_rec.ITAKUAITEAPKIHONCD, ',') -- 委託ＡＰ基本コード
         || ',' || REPLACE(dlvryyti_rec.ITAKUAITESHOPCD, ',')    -- 委託ショップコード
         || ',' || REPLACE(dlvryyti_rec.SHOPNM_AITE, ',') -- 委託ショップ名称
         || ',' || REPLACE(dlvryyti_rec.CMPNYNMN1_AITE, ',') -- 委託会社名称１
         || ',' || REPLACE(dlvryyti_rec.CMPNYNMN2_AITE, ',') -- 委託会社名称２
         || ',' || REPLACE(dlvryyti_rec.YUBINNO_AITE, ',') -- 委託郵便番号
         || ',' || REPLACE(dlvryyti_rec.ADRSN1_AITE, ',') -- 委託住所１
         || ',' || REPLACE(dlvryyti_rec.ADRSN2_AITE, ',') -- 委託住所２
         || ',' || REPLACE(dlvryyti_rec.ADRSN3_AITE, ',') -- 委託住所３
         || ',' || REPLACE(dlvryyti_rec.RENRAKUSAKINON1_AITE, ',') -- 委託連絡先番号１
         || ',' || REPLACE(dlvryyti_rec.RENRAKUSAKINON2_AITE, ',') -- 委託連絡先番号２
         || ',' || REPLACE(dlvryyti_rec.TAXHASUKEISANKBN_AITE, ',') -- 委託税端数計算区分
         || ',' || REPLACE(dlvryyti_rec.URIKINHASUKEISANKBN_AITE, ',') -- 委託売上金額端数計算区分
         || ',' || REPLACE(dlvryyti_rec.TAXKEISANKBN_AITE, ',') -- 委託税計算区分
         || ',' || REPLACE(dlvryyti_rec.DNPYOSHIRASENAIYON1_AITE, ',') -- 委託伝票お知らせ内容
-- インボイス対応追加 start ABI
         || ',' || REPLACE(dlvryyti_rec.ACPTIPPANNUKIKIN, ',')           -- 受注一般税抜金額
         || ',' || REPLACE(dlvryyti_rec.ACPTIPPANTAXKIN, ',')            -- 受注一般消費税金額
         || ',' || REPLACE(dlvryyti_rec.ACPTKEIGENNUKIKIN, ',')          -- 受注軽減税抜金額
         || ',' || REPLACE(dlvryyti_rec.ACPTKEIGENTAXKIN, ',')           -- 受注軽減消費税金額
         || ',' || REPLACE(dlvryyti_rec.ACPTHIKAZEIURIIKIN, ',')         -- 受注非課税金額
         || ',' || REPLACE(dlvryyti_rec.ACPTFUKAZEIURIIKIN, ',')         -- 受注不課税売上金額
         || ',' || REPLACE(dlvryyti_rec.NOHINIPPANNUKIKIN, ',')          -- 納品一般税抜金額
         || ',' || REPLACE(dlvryyti_rec.NOHINIPPANTAXKIN, ',')           -- 納品一般消費税金額
         || ',' || REPLACE(dlvryyti_rec.NOHINKEIGENNUKIKIN, ',')         -- 納品軽減税抜金額
         || ',' || REPLACE(dlvryyti_rec.NOHINKEIGENTAXKIN, ',')          -- 納品軽減消費税金額
         || ',' || REPLACE(dlvryyti_rec.NOHINHIKAZEIURIIKIN, ',')        -- 納品非課税金額
         || ',' || REPLACE(dlvryyti_rec.NOHINFUKAZEIURIIKIN, ',')        -- 納品不課税売上金額
         || ',' || REPLACE(dlvryyti_rec.INVOICEJIGYOSHAINPTNO, ',')      -- インボイス事業者登録番号
         || ',' || REPLACE(dlvryyti_rec.INVOICEJIGYOSHAINPTNO_AITE, ',') -- 委託インボイス事業者登録番号
-- インボイス対応追加 end ABI
         || chr(13) || chr(10) -- 改行：\r\n
        );
        UTL_FILE.FFLUSH(file_handle);

        -- 印刷済に更新
        UPDATE S04T001T JHTT
           SET JHTT.NOSHOPRNTSUMIFLG = '1' -- 納品書印刷済フラグ
             , JHTT.NOSHOPRNTDD = userinfo_rec.SYSTMYMD -- 納品書印刷日
             , JHTT.NOSHOHIKAEPRNTSUMIFLG = '1' -- 納品書控印刷済フラグ
             , JHTT.NOSHOHIKAEPRNTDD  = userinfo_rec.SYSTMYMD -- 納品書控印刷日
             -- 入金票印刷済フラグ
             , JHTT.NYKNHYOPRNTSUMIFLG = ( CASE WHEN JHTT.NYKNHYOPRNTTRGTFLG = '1'
                                                THEN '1'
                                                ELSE JHTT.NYKNHYOPRNTSUMIFLG
                                           END )
             -- 入金票印刷日
             , JHTT.NYKNHYOPRNTDD = ( CASE WHEN JHTT.NYKNHYOPRNTTRGTFLG = '1'
                                                THEN userinfo_rec.SYSTMYMD
                                                ELSE JHTT.NYKNHYOPRNTDD
                                      END )
             -- サーバー指示書印刷済フラグ
             , JHTT.WSVSIJISHOPRNTSUMIFLG = ( CASE WHEN JHTT.WSVSIJISHOPRNTTRGTFLG = '1'
                                                   THEN '1'
                                                   ELSE JHTT.WSVSIJISHOPRNTSUMIFLG
                                              END )
             -- サーバー指示書印刷日
             , JHTT.WSVSIJISHOPRNTDD = ( CASE WHEN JHTT.WSVSIJISHOPRNTTRGTFLG = '1'
                                              THEN userinfo_rec.SYSTMYMD
                                              ELSE JHTT.WSVSIJISHOPRNTDD
                                         END )
             /* 更新者 */
             , JHTT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD -- 更新者ＡＰ基本コード
             , JHTT.KOSHASHOPCD    = userinfo_rec.SHOPCD    -- 更新者ショップコード
             , JHTT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
             , JHTT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
             , JHTT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
             , JHTT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
             , JHTT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
             , JHTT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
         WHERE JHTT.APKIHONCD = dlvryyti_rec.APKIHONCD -- ＡＰ基本コード
           AND JHTT.SHOPCD = dlvryyti_rec.SHOPCD -- ショップコード
           AND JHTT.ACPTDNPYNO = dlvryyti_rec.ACPTDNPYNO -- 受注伝票番号
           AND JHTT.EDANO = dlvryyti_rec.EDANO -- 枝番号
        ;
    END LOOP;
    UTL_FILE.FCLOSE(file_handle);

-- 本線対応追加 start ABI
    FILEUPLOADTOS3( file_path , file_name );
-- 本線対応追加 end ABI

    -- EXCEPTION：処理終了
    EXCEPTION
      WHEN OTHERS THEN
        IF SELECT_DLVRYYTI%ISOPEN THEN
           CLOSE SELECT_DLVRYYTI;
        END IF;
        UTL_FILE.FCLOSE_ALL;
        -- エラー
        RAISE_APPLICATION_ERROR(-20012, SQLERRM || '配達予定データＣＳＶ作成時エラーが発生しました。');

  END csvDlvryyti;

-- インボイス対応追加 start ABI
  /****************************************************************************/
  /* プロシージャ名 ：売上データ出力                                          */
  /* 引数           ：iApkihoncd          ＡＰ基本コード                      */
  /*                ：iShopcd             ショップコード                      */
  /*                ：iTrmnlid            端末ID                              */
  /*                ：iDlvrykyotencd      配達拠点コード                      */
  /*                ：iDlvryytidd         配達予定日                          */
  /*                ：iDlvrytnshacd       配達担当者コード                    */
  /*                ：iOutputtrgtdd       出力対象日                          */
  /*                ：file_path           ファイルパス                        */
  /****************************************************************************/
  PROCEDURE csvUriage( iApkihoncd       IN VARCHAR2 -- ＡＰ基本コード
                     , iShopcd          IN VARCHAR2 -- ショップコード
                     , iTrmnlid         IN VARCHAR2 -- 端末ID
                     , iDlvrykyotencd   IN VARCHAR2 -- 配達拠点コード
                     , iDlvryytidd      IN VARCHAR2 -- 配達予定日
                     , iDlvrytnshacd    IN VARCHAR2 -- 配達担当者コード
                     , iOutputtrgtdd    IN VARCHAR2 -- 出力対象日
                     , iOutputtrgtkbn   IN VARCHAR2 -- 出力対象区分
                     , file_path        IN VARCHAR2 -- ファイルパス
  ) IS
  /* 変数宣言 */
  file_name       VARCHAR2(50) := NULL;
  file_handle    UTL_FILE.FILE_TYPE;
  /* CURSOR：売上データ */
  CURSOR SELECT_URIAGE IS
    SELECT URGT.APKIHONCD             -- ＡＰ基本コード
         , URGT.SHOPCD                -- ショップコード
         , URGT.URIDNPYNO             -- 売上伝票番号
         , URGT.EDANO                 -- 枝番号
         , URGT.ACPTDNPYNO            -- 受注伝票番号
         , URGT.ACPTDNPYEDANO         -- 受注伝票枝番号
         , URGT.TORIHIKIKBN           -- 取引区分
         , URGT.TOKUISAKICD           -- 得意先コード
         , URGT.URIDD                 -- 売上日
         , URGT.ACPTDD                -- 受注日
         , URGT.URIKIN                -- 売上金額
         , URGT.SHOHITAXKIN           -- 消費税金額
         , URGT.SANKOUSHOHITAXKIN     -- 参考消費税金額
         , URGT.DNPYNO                -- 伝票番号
         , URGT.SKYSHOOTPTARINASHIKBN -- 請求書出力有無区分
         , URGT.TAXKEISANKBN          -- 税計算区分
         , URMT.URIDNPYMEISAINO       -- 売上伝票明細番号
         , URMT.URIDNPYMEISAIEDANO    -- 売上伝票明細枝番号
         , URMT.LCTGRYCD              -- 大分類コード
         , URMT.MCTGRYCD              -- 中分類コード
         , URMT.SCTGRYCD              -- 小分類コード
         , URMT.SHNCD                 -- 商品コード
         , URMT.KEIJOKBN              -- 計上区分
         , URMT.SURYO                 -- 数量
         , URMT.HYOJUNUNT             -- 標準単価
         , URMT.UNT                   -- 単価
         , URMT.KIN                   -- 金額
         , URMT.UNTSHOHITAXKIN        -- 単価消費税金額
         , URMT.SHOHITAXKIN AS SHOHITAXKINMEI                -- 消費税金額
         , URMT.SANKOUSHOHITAXKIN AS SANKOUSHOHITAXKINMEI    -- 参考消費税金額
         , URMT.TAXKBN                -- 税区分
         , URMT.MEISAIKBN             -- 明細区分
         , URMT.NYURYOKUMEISAIFLG     -- 入力明細フラグ
         , URMT.TANPINMEISAIFLG       -- 単品明細フラグ
         , URMT.MEISAIBIKO            -- 明細備考
         , URMT.STSHNCD               -- セット商品コード
         , URMT.KATASIKI              -- 型式
         , URMT.YUKOKBN               -- 有効区分
         , URMT.KSHNSYSTMYMD          -- 更新システム日付
         , URMT.KSHNSYSTMJIKAN        -- 更新システム時間
    FROM S06T001T URGT       -- 売上テーブル
    INNER JOIN S06T002T URMT -- 売上明細テーブル
            ON ( URMT.APKIHONCD     = URGT.APKIHONCD
                 AND URMT.SHOPCD    = URGT.SHOPCD
                 AND URMT.URIDNPYNO = URGT.URIDNPYNO
               )
    WHERE URGT.APKIHONCD      = iApkihoncd
      AND URGT.SHOPCD         = iShopcd
      AND URGT.SKYPRCSJISSIDD = 'DAIBIKI'
      AND URGT.ACPTDNPYNO     IN (
                                    SELECT JHTT.ACPTDNPYNO
                                    FROM S04T001T JHTT       -- 受注配達テーブル
                                    INNER JOIN S04T002T JHMT -- 受注配達明細テーブル
                                            ON ( JHMT.APKIHONCD             = JHTT.APKIHONCD
                                                 AND JHMT.SHOPCD            = JHTT.SHOPCD
                                                 AND JHMT.ACPTDNPYNO        = JHTT.ACPTDNPYNO
                                                 AND JHMT.EDANO             = JHTT.EDANO
                                                 AND JHMT.MEISAIKBN        <> '1' -- 1:配布物以外
                                                 AND JHMT.NYURYOKUMEISAIFLG = '1' -- 入力行のみ抽出
                                               )
                                    INNER JOIN S02M013T TKSM -- 得意先マスタ
                                            ON ( TKSM.APKIHONCD       = JHTT.APKIHONCD
                                                 AND TKSM.SHOPCD      = JHTT.SHOPCD
                                                 AND TKSM.TOKUISAKICD = JHTT.TOKUISAKICD
                                               )
                                    LEFT JOIN S02M013T TKSM_AITE -- 得意先マスタ（委託）
                                            ON ( TKSM_AITE.APKIHONCD       = TKSM.ITAKUAITEAPKIHONCD
                                                 AND TKSM_AITE.SHOPCD      = TKSM.ITAKUAITESHOPCD
                                                 AND TKSM_AITE.TOKUISAKICD = TKSM.ITAKUAITETOKUISAKICD
                                               )
                                    LEFT JOIN S04T001T JHTT_AITE -- 受注配達テーブル（委託）
                                           ON ( JHTT_AITE.APKIHONCD      = JHTT.ITAKUAITEAPKIHONCD
                                                AND JHTT_AITE.SHOPCD     = JHTT.ITAKUAITESHOPCD
                                                AND JHTT_AITE.ACPTDNPYNO = JHTT.ITAKUAITEACPTDNPYNO
                                                AND JHTT_AITE.EDANO      = JHTT.ITAKUAITEEDANO
                                              )
                                    LEFT JOIN S02M050T TKJT -- 得意先実績テーブル
                                            ON ( TKJT.APKIHONCD       = TKSM.APKIHONCD
                                                 AND TKJT.SHOPCD      = TKSM.SHOPCD
                                                 AND TKJT.TOKUISAKICD = TKSM.TOKUISAKICD
                                               )
                                    LEFT JOIN S02M050T TKJT_AITE -- 得意先実績テーブル（委託）
                                            ON ( TKJT_AITE.APKIHONCD       = TKSM.ITAKUAITEAPKIHONCD
                                                 AND TKJT_AITE.SHOPCD      = TKSM.ITAKUAITESHOPCD
                                                 AND TKJT_AITE.TOKUISAKICD = TKSM.ITAKUAITETOKUISAKICD
                                               )
                                    LEFT JOIN S02M046T SHPM -- ショップマスタ
                                            ON ( SHPM.APKIHONCD  = JHTT.APKIHONCD
                                                 AND SHPM.SHOPCD = JHTT.SHOPCD
                                               )
                                    LEFT JOIN S02M046T SHPM_TKS -- ショップマスタ：得意先ショップ
                                            ON ( SHPM_TKS.APKIHONCD  = TKSM.APKIHONCD
                                                 AND SHPM_TKS.SHOPCD = TKSM.TOKUISAKISHOPCD
                                               )
                                    LEFT JOIN S02M046T SHPM_AITE -- ショップマスタ（委託）
                                            ON ( SHPM_AITE.APKIHONCD  = TKSM.ITAKUAITEAPKIHONCD
                                                 AND SHPM_AITE.SHOPCD = TKSM.ITAKUAITESHOPCD
                                               )
                                    LEFT JOIN S02M023T DPOM -- 伝票お知らせマスタ
                                            ON ( DPOM.APKIHONCD    = JHTT.APKIHONCD
                                                 AND DPOM.SHOPCD   = JHTT.SHOPCD
                                                 AND JHTT.DLVRYDD BETWEEN DPOM.TEKIYOSTRTDD AND DPOM.TEKIYOSHRYDD
                                                 AND DPOM.DNPYKIND = '1' -- 1:納品書兼受領書
                                                 AND DPOM.YUKOKBN  = '1'
                                               )
                                    LEFT JOIN S02M023T DPOM_AITE -- 伝票お知らせマスタ（委託）
                                            ON ( DPOM_AITE.APKIHONCD    = JHTT.ITAKUAITEAPKIHONCD
                                                 AND DPOM_AITE.SHOPCD   = JHTT.ITAKUAITESHOPCD
                                                 AND JHTT_AITE.DLVRYDD BETWEEN DPOM_AITE.TEKIYOSTRTDD AND DPOM_AITE.TEKIYOSHRYDD
                                                 AND DPOM_AITE.DNPYKIND = '1' -- 1:納品書兼受領書
                                                 AND DPOM_AITE.YUKOKBN  = '1'
                                               )
                                    LEFT JOIN S02V001T SHMV -- 商品マスタビュー
                                            ON ( SHMV.APKIHONCD  = JHTT.APKIHONCD
                                                 AND SHMV.SHOPCD = JHTT.SHOPCD
                                                 AND SHMV.SHNCD  = JHMT.SHNCD
                                               )
                                    LEFT JOIN S02V001T SHMV_AITE -- 商品マスタビュー（委託）
                                            ON ( SHMV_AITE.APKIHONCD  = TKSM.ITAKUAITEAPKIHONCD
                                                 AND SHMV_AITE.SHOPCD = TKSM.ITAKUAITESHOPCD
                                                 AND SHMV_AITE.SHNCD  = JHMT.SHNCD
                                               )
                                    LEFT JOIN S02M006T SYSM_KBJK -- システム管理マスタ：配達希望時間区分
                                            ON ( SYSM_KBJK.APKIHONCD    = '000' AND SYSM_KBJK.SHOPCD = '000'
                                                 AND SYSM_KBJK.SETTEINO = '012' AND SYSM_KBJK.RCRDKBN = 'M'
                                                 AND SYSM_KBJK.SETTEICD = JHTT.DLVRYKIBOJIKANKBN
                                                 AND SYSM_KBJK.YUKOKBN  = '1'
                                               )
                                    LEFT JOIN S02M017T SKSM -- 請求先マスタ
                                            ON ( SKSM.APKIHONCD     = TKSM.APKIHONCD
                                                 AND SKSM.SHOPCD    = TKSM.SHOPCD
                                                 AND SKSM.SKYSAKICD = TKSM.SKYSAKICD
                                               )
                                    LEFT JOIN S02M017T SKSM_AITE -- 請求先マスタ（委託）
                                            ON ( SKSM_AITE.APKIHONCD     = TKSM_AITE.APKIHONCD
                                                 AND SKSM_AITE.SHOPCD    = TKSM_AITE.SHOPCD
                                                 AND SKSM_AITE.SKYSAKICD = TKSM_AITE.SKYSAKICD
                                               )
                                    WHERE JHTT.APKIHONCD              = iApkihoncd
                                      AND JHTT.SHOPCD                 = iShopcd
                                      AND JHTT.DLVRYKANKATSUAPKIHONCD = iApkihoncd
                                      AND JHTT.DLVRYKANKATSUSHOPCD    = iShopcd
                                      AND JHTT.DLVRYKYOTENCD          = iDlvrykyotencd
                                      AND JHTT.DLVRYYTIDD            <= iDlvryytidd
                                      AND JHTT.DLVRYTNSHACD           = iDlvrytnshacd
                                      AND JHTT.DLVRYKBN              IN ( '1', '2' )    -- 1：自社配達  2：他社配達
                                      AND JHTT.DNPYSTS                = '11'            -- 11：配達中
                                      AND JHTT.DLVRYSTS               = '11'            -- 11：配達中
                                      AND TKSM.TOKUISAKIKBN          IN ('1', '5', '6') -- 1：顧客 5：NA受託 6：配達受託
                                      AND JHTT.YUKOKBN                = '1'
                                    GROUP BY JHTT.ACPTDNPYNO
                                 )
    ORDER BY URGT.APKIHONCD, URGT.SHOPCD, URGT.URIDNPYNO
           , URMT.URIDNPYMEISAINO, URMT.URIDNPYMEISAIEDANO
  ;

  BEGIN
    file_name  := FID_URIAGE
                 || '_' || iTrmnlid
                 || '_' || userinfo_rec.SYSTMYMD || userinfo_rec.SYSTMJIKAN
                 || '_' || NVL(iOutputtrgtdd, '00000000') || '.csv';
-- 本線対応変更 start ABI
--    file_handle := UTL_FILE.FOPEN( file_path , file_name, 'w' );
    file_handle := UTL_FILE.FOPEN( 'PL_SQL_DIR2' , file_name, 'w' );
-- 本線対応変更 end ABI

    -- 出力情報区分が「1：集金」の場合、売上データ出力処理終了
    IF iOutputtrgtkbn = '1' THEN
       UTL_FILE.FCLOSE_ALL;
-- 本線対応追加 start ABI
       FILEUPLOADTOS3( file_path , file_name );
-- 本線対応追加 end ABI
       RETURN ;
    END IF;

    FOR uriage_rec IN SELECT_URIAGE LOOP

        /* 受注日の判定 */
        -- 受注日がNULLの場合、売上日をセットする。
        IF uriage_rec.ACPTDD IS NULL THEN
           uriage_rec.ACPTDD := uriage_rec.URIDD;
        END IF;

        -- ＣＳＶ作成
        UTL_FILE.PUT( file_handle,
         REPLACE(uriage_rec.APKIHONCD, ',')                       -- ＡＰ基本コード
         || ',' || REPLACE(uriage_rec.SHOPCD, ',')                -- ショップコード
         || ',' || REPLACE(uriage_rec.URIDNPYNO, ',')             -- 売上伝票番号
         || ',' || REPLACE(uriage_rec.EDANO, ',')                 -- 枝番号
         || ',' || REPLACE(uriage_rec.ACPTDNPYNO, ',')            -- 受注伝票番号
         || ',' || REPLACE(uriage_rec.ACPTDNPYEDANO, ',')         -- 受注伝票枝番号
         || ',' || REPLACE(uriage_rec.TORIHIKIKBN, ',')           -- 取引区分
         || ',' || REPLACE(uriage_rec.TOKUISAKICD, ',')           -- 得意先コード
         || ',' || REPLACE(uriage_rec.URIDD, ',')                 -- 売上日
         || ',' || REPLACE(uriage_rec.ACPTDD, ',')                -- 受注日
         || ',' || REPLACE(uriage_rec.URIKIN, ',')                -- 売上金額
         || ',' || REPLACE(uriage_rec.SHOHITAXKIN, ',')           -- 消費税金額
         || ',' || REPLACE(uriage_rec.SANKOUSHOHITAXKIN, ',')     -- 参考消費税金額
         || ',' || REPLACE(uriage_rec.DNPYNO, ',')                -- 伝票番号
         || ',' || REPLACE(uriage_rec.SKYSHOOTPTARINASHIKBN, ',') -- 請求書出力有無区分
         || ',' || REPLACE(uriage_rec.TAXKEISANKBN, ',')          -- 税計算区分
         || ',' || REPLACE(uriage_rec.URIDNPYMEISAINO, ',')       -- 売上伝票明細番号
         || ',' || REPLACE(uriage_rec.URIDNPYMEISAIEDANO, ',')    -- 売上伝票明細枝番号
         || ',' || REPLACE(uriage_rec.LCTGRYCD, ',')              -- 大分類コード
         || ',' || REPLACE(uriage_rec.MCTGRYCD, ',')              -- 中分類コード
         || ',' || REPLACE(uriage_rec.SCTGRYCD, ',')              -- 小分類コード
         || ',' || REPLACE(uriage_rec.SHNCD, ',')                 -- 商品コード
         || ',' || REPLACE(uriage_rec.KEIJOKBN, ',')              -- 計上区分
         || ',' || REPLACE(uriage_rec.SURYO, ',')                 -- 数量
         || ',' || REPLACE(uriage_rec.HYOJUNUNT, ',')             -- 標準単価
         || ',' || REPLACE(uriage_rec.UNT, ',')                   -- 単価
         || ',' || REPLACE(uriage_rec.KIN, ',')                   -- 金額
         || ',' || REPLACE(uriage_rec.UNTSHOHITAXKIN, ',')        -- 単価消費税金額
         || ',' || REPLACE(uriage_rec.SHOHITAXKINMEI, ',')        -- 消費税金額
         || ',' || REPLACE(uriage_rec.SANKOUSHOHITAXKINMEI, ',')  -- 参考消費税金額
         || ',' || REPLACE(uriage_rec.TAXKBN, ',')                -- 税区分
         || ',' || REPLACE(uriage_rec.MEISAIKBN, ',')             -- 明細区分
         || ',' || REPLACE(uriage_rec.NYURYOKUMEISAIFLG, ',')     -- 入力明細フラグ
         || ',' || REPLACE(uriage_rec.TANPINMEISAIFLG, ',')       -- 単品明細フラグ
         || ',' || REPLACE(uriage_rec.MEISAIBIKO, ',')            -- 明細備考
         || ',' || REPLACE(uriage_rec.STSHNCD, ',')               -- セット商品コード
         || ',' || REPLACE(uriage_rec.KATASIKI, ',')              -- 型式
         || ',' || REPLACE(uriage_rec.YUKOKBN, ',')               -- 有効区分
         || ',' || REPLACE(uriage_rec.KSHNSYSTMYMD, ',')          -- 更新システム日付
         || ',' || REPLACE(uriage_rec.KSHNSYSTMJIKAN, ',')        -- 更新システム時間
         || chr(13) || chr(10) -- 改行：\r\n
        );
        UTL_FILE.FFLUSH(file_handle);
    END LOOP;
    UTL_FILE.FCLOSE(file_handle);

-- 本線対応追加 start ABI
    FILEUPLOADTOS3( file_path , file_name );
-- 本線対応追加 end ABI

    -- EXCEPTION：処理終了
    EXCEPTION
      WHEN OTHERS THEN
        IF SELECT_URIAGE%ISOPEN THEN
           CLOSE SELECT_URIAGE;
        END IF;
        UTL_FILE.FCLOSE_ALL;
        -- エラー
        RAISE_APPLICATION_ERROR(-20025, SQLERRM || '売上データＣＳＶ作成時エラーが発生しました。');

  END csvUriage;
-- インボイス対応追加 end ABI

  /****************************************************************************/
  /* プロシージャ名 ：集金データ出力                                               */
  /* 引数           ：iApkihoncd          ＡＰ基本コード                         */
  /*                ：iShopcd             ショップコード                         */
  /*                ：iTrmnlid            端末ID                               */
  /*                ：iDlvryytidd         配達予定日                            */
  /*                ：iDlvrytnshacd       配達担当者コード                       */
  /*                ：iTairyukbn          滞留区分                              */
  /*                ：iOutputtrgtdd       出力対象日                            */
  /*                ：file_path           ファイルパス                           */
  /****************************************************************************/
  PROCEDURE csvSyukin( iApkihoncd IN VARCHAR2 -- ＡＰ基本コード
                     , iShopcd    IN VARCHAR2 -- ショップコード
                     , iTrmnlid   IN VARCHAR2 -- 端末ID
                     , iDlvryytidd   IN VARCHAR2 -- 配達予定日
                     , iDlvrytnshacd IN VARCHAR2 -- 配達担当者コード
                     , iTairyukbn    IN VARCHAR2 -- 滞留区分
                     , iOutputtrgtdd IN VARCHAR2 -- 出力対象日
                     , iOutputtrgtkbn   IN VARCHAR2 -- 出力対象区分
                     , file_path        IN VARCHAR2 -- ファイルパス
  ) IS
  /* 変数宣言 */
  file_name       VARCHAR2(50) := NULL;
  file_handle    UTL_FILE.FILE_TYPE;
  /* CURSOR：集金データ */
  CURSOR SELECT_SYUKIN IS
    SELECT *
      FROM (
             SELECT SKZT.APKIHONCD -- ＡＰ基本コード
                  , SKZT.SHOPCD -- ショップコード
                  , SKZT.SKYSAKICD -- 請求先コード
                  , SKZT.SHIMEDYDM -- 締年月
                  , SKZT.SHIMEDD -- 締日
                  , SKZT.KONKAISHIMEDD -- 今回締日
                  , SUM( NVL(SKZT.ZNKKURIKOSHIKIN, 0) ) ZNKKURIKOSHIKIN -- 前回繰越金額
                  , SUM( NVL(SKZT.KONKAITAXNUKIURIKIN, 0) ) KONKAITAXNUKIURIKIN -- 今回税抜売上金額
                  , SUM( NVL(SKZT.KONKAISHOHITAXKIN, 0) ) KONKAISHOHITAXKIN -- 今回消費税金額
                  , SUM( NVL(SKZT.KONKAINYKNKIN, 0) ) KONKAINYKNKIN -- 今回入金金額
                  , SUM( NVL(SKZT.KURIKOSHIKIN, 0) ) KURIKOSHIKIN -- 繰越金額
                  , SKZT.SKYSHONO -- 請求書番号
                  , SKZT.KAISHUHOHOKBN -- 回収方法区分
                  , SKZT.SHRYTIDYDMDD -- 支払予定年月日
                  , SKZT.YUKOKBN -- 有効区分
                  , ( SELECT SUM(NVL(NKMS.NYKNKIN, 0)) NYKNKIN
                        FROM S10T001T NKMS
                       WHERE ( NKMS.SKYPRCSJISSIDD = '00000000'
                          OR ( SKZT.SHIMEDYDM || SKZT.SHIMEDD ) < ( NKMS.SKYSHIMEDYDM || NKMS.SKYSHIMEDD ) )
                         AND NKMS.APKIHONCD  = SKZT.APKIHONCD
                         AND NKMS.SHOPCD = SKZT.SHOPCD
                         AND NKMS.SKYSAKICD = SKZT.SKYSAKICD
                       GROUP BY APKIHONCD
                              , SHOPCD
                              , SKYSAKICD
                     ) NYKNKIN
             FROM S07T001T SKZT -- 請求残高テーブル
            INNER JOIN S02M017T SKSM -- 請求先マスタ
             ON ( SKSM.APKIHONCD  = SKZT.APKIHONCD
                  AND SKSM.SHOPCD = SKZT.SHOPCD
                  AND SKSM.SKYSAKICD = SKZT.SKYSAKICD
                 )
            WHERE SKZT.APKIHONCD = iApkihoncd
              AND SKZT.SHOPCD    = iShopcd
              AND SKZT.SHRYTIDYDMDD <= iDlvryytidd
              AND SKZT.SHIMEDYDM = ( SELECT MAX(SKZT_YM.SHIMEDYDM)
                                FROM S07T001T SKZT_YM
                               WHERE SKZT_YM.APKIHONCD = SKZT.APKIHONCD
                                 AND SKZT_YM.SHOPCD    = SKZT.SHOPCD
                                 AND SKZT_YM.SKYSAKICD = SKZT.SKYSAKICD
                                 AND SKZT_YM.SKYPRCSJISSIKBN = '1'
                                 AND SKZT_YM.SHRYTIDYDMDD <= iDlvryytidd
                             )
              AND SKZT.SHIMEDD = ( SELECT MAX(SKZT_DD.SHIMEDD)
                              FROM S07T001T SKZT_DD
                             WHERE SKZT_DD.APKIHONCD = SKZT.APKIHONCD
                               AND SKZT_DD.SHOPCD    = SKZT.SHOPCD
                               AND SKZT_DD.SKYSAKICD = SKZT.SKYSAKICD
                               AND SKZT_DD.SHIMEDYDM = SKZT.SHIMEDYDM
                               AND SKZT_DD.SKYPRCSJISSIKBN = '1'
                               AND SKZT_DD.SHRYTIDYDMDD <= iDlvryytidd
                           )
              AND SKZT.SKYPRCSJISSIKBN = '1' -- 1：月締請求処理
              AND
              -- （得意先状態＝‘6：退会’かつ状態ステータス＝‘10：退会済’）は対象外
                  NOT EXISTS
                  ( SELECT 'X'
                      FROM  S02M013T TKSM -- 得意先マスタ
                     WHERE  TKSM.APKIHONCD  = SKZT.APKIHONCD
                     AND    TKSM.SHOPCD = SKZT.SHOPCD
                     AND    TKSM.TOKUISAKICD = SKZT.TOKUISAKICD
                     AND    TKSM.YUKOKBN = '1'
                     AND    TKSM.TOKUISAKIJOTAI = '06'
                     AND    TKSM.JOTAISTS = '10')
              AND SKSM.KAISHUTNSHACD = iDlvrytnshacd
              AND SKZT.KAISHUHOHOKBN <> '7'
              AND (
              (SKZT.KAISHUHOHOKBN <> '1'
                    AND ( ( iTairyukbn = '2' AND SKSM.TAIRYUKBN in ('1', '2', '3'))
                          OR
                          ( iTairyukbn = '3' AND SKSM.TAIRYUKBN in ('2', '3'))
                          OR
                          ( iTairyukbn = '4' AND SKSM.TAIRYUKBN = '3')
                    )
               )
               OR (SKZT.KAISHUHOHOKBN = '1' )
                  )
     GROUP BY SKZT.APKIHONCD -- ＡＰ基本コード
            , SKZT.SHOPCD -- ショップコード
            , SKZT.SKYSAKICD -- 請求先コード
            , SKZT.SHIMEDYDM -- 締年月
            , SKZT.SHIMEDD -- 締日
            , SKZT.KONKAISHIMEDD -- 今回締日
            , SKZT.SKYSHONO -- 請求書番号
            , SKZT.KAISHUHOHOKBN -- 回収方法区分
            , SKZT.SHRYTIDYDMDD -- 支払予定年月日
            , SKZT.YUKOKBN -- 有効区分
     ORDER BY SKZT.APKIHONCD -- ＡＰ基本コード
            , SKZT.SHOPCD -- ショップコード
            , SKZT.SKYSAKICD -- 請求先コード
            , SKZT.SHIMEDYDM -- 締年月
            , SKZT.SHIMEDD -- 締日
     )
     WHERE NVL(KURIKOSHIKIN,0) - NVL(NYKNKIN, 0) > 0
  ;
  BEGIN
    file_name  := FID_SYUKIN
                 || '_' || iTrmnlid
                 || '_' || userinfo_rec.SYSTMYMD || userinfo_rec.SYSTMJIKAN
                 || '_' || NVL(iOutputtrgtdd, '00000000') || '.csv';
-- 本線対応変更 start ABI
--    file_handle := UTL_FILE.FOPEN( file_path , file_name, 'w' );
    file_handle := UTL_FILE.FOPEN( 'PL_SQL_DIR2' , file_name, 'w' );
-- 本線対応変更 end ABI

    -- 集金データ出力：1：集金のみ、または、2：全て の場合
    IF iOutputtrgtkbn NOT IN ('1', '2') THEN
       UTL_FILE.FCLOSE(file_handle);
-- 本線対応追加 start ABI
       FILEUPLOADTOS3( file_path , file_name );
-- 本線対応追加 end ABI
       RETURN ;
    END IF;

    FOR syukin_rec IN SELECT_SYUKIN LOOP

        -- ＣＳＶ作成
        UTL_FILE.PUT( file_handle,
          REPLACE(syukin_rec.APKIHONCD, ',') -- ＡＰ基本コード
         || ',' || REPLACE(syukin_rec.SHOPCD, ',') -- ショップコード
         || ',' || REPLACE(syukin_rec.SKYSAKICD, ',') -- 請求先コード
         || ',' || REPLACE(syukin_rec.SHIMEDYDM, ',') -- 締年月
         || ',' || REPLACE(syukin_rec.SHIMEDD, ',') -- 締日
         || ',' || REPLACE(syukin_rec.KONKAISHIMEDD, ',') -- 今回締日
         || ',' || REPLACE(syukin_rec.ZNKKURIKOSHIKIN, ',') -- 前回繰越金額
         || ',' || REPLACE(syukin_rec.KONKAITAXNUKIURIKIN, ',') -- 今回税抜売上金額
         || ',' || REPLACE(syukin_rec.KONKAISHOHITAXKIN, ',') -- 今回消費税金額
         || ',' || REPLACE(syukin_rec.KONKAINYKNKIN, ',') -- 今回入金金額
         || ',' || REPLACE(syukin_rec.KURIKOSHIKIN, ',') -- 繰越金額
         || ',' || REPLACE(syukin_rec.SKYSHONO, ',') -- 請求書番号
         || ',' || REPLACE(NVL(syukin_rec.KURIKOSHIKIN,0) - NVL(syukin_rec.NYKNKIN,0), ',') -- 現在残高
         || ',' || REPLACE(syukin_rec.KAISHUHOHOKBN, ',') -- 回収方法区分
         || ',' || REPLACE(syukin_rec.SHRYTIDYDMDD, ',') -- 支払予定年月日
         || ',' || REPLACE(syukin_rec.YUKOKBN, ',') -- 有効区分
         || chr(13) || chr(10) -- 改行：\r\n
      );
      UTL_FILE.FFLUSH(file_handle);
    END LOOP;
    UTL_FILE.FCLOSE(file_handle);

-- 本線対応追加 start ABI
    FILEUPLOADTOS3( file_path , file_name );
-- 本線対応追加 end ABI

    -- EXCEPTION：処理終了
    EXCEPTION
      WHEN OTHERS THEN
        IF SELECT_SYUKIN%ISOPEN THEN
           CLOSE SELECT_SYUKIN;
        END IF;
        UTL_FILE.FCLOSE_ALL;
        -- エラー
        RAISE_APPLICATION_ERROR(-20025, SQLERRM || '集金データＣＳＶ作成時エラーが発生しました。');

  END csvSyukin;

  /****************************************************************************/
  /* プロシージャ名 ：商品データ出力                                               */
  /* 引数           ：iApkihoncd          ＡＰ基本コード                         */
  /*                ：iShopcd             ショップコード                         */
  /*                ：iTrmnlid            端末ID                               */
  /*                ：iOutputkbn          出力区分                              */
  /*                ：iOutputtrgtdd       出力対象日                            */
  /*                ：file_path           ファイルパス                           */
  /****************************************************************************/
  PROCEDURE csvShohin( iApkihoncd IN VARCHAR2 -- ＡＰ基本コード
                     , iShopcd    IN VARCHAR2 -- ショップコード
                     , iTrmnlid   IN VARCHAR2 -- 端末ID
                     , iOutputkbn    IN VARCHAR2 -- 出力区分
                     , iOutputtrgtdd IN VARCHAR2 -- 出力対象日
                     , file_path  IN VARCHAR2 -- ファイルパス
  ) IS
  /* 変数宣言 */
  file_name     VARCHAR2(50) := NULL;
  file_handle  UTL_FILE.FILE_TYPE;
  /* CURSOR：商品データ */
  CURSOR SELECT_SHOHIN IS
    SELECT SHMV.APKIHONCD -- ＡＰ基本コード
         , SHMV.SHOPCD -- ショップコード
         , SHMV.SHNCD -- 商品コード
         , SHMV.STKBN -- セット区分
         , SHMV.LCTGRYCD -- 大分類コード
         , SHMV.MCTGRYCD -- 中分類コード
         , SHMV.SCTGRYCD -- 小分類コード
         , SHMV.LCTGRYNM -- 大分類名称
         , SHMV.MCTGRYNM -- 中分類名称
         , SHMV.SCTGRYNM -- 小分類名称
         , SHMV.SHNNMKIRIKAEDD -- 商品名称切替日
         , SHMV.GENZSHNNM -- 現商品名称
         , SHMV.SHINSHNNM -- 新商品名称
         , SHMV.GENZSHNNMKANA -- 現商品名称カナ
         , SHMV.SHINSHNNMKANA -- 新商品名称カナ
         , SHMV.GENZSHNNMRYAKUSHO -- 現商品名称略称
         , SHMV.SHINSHNNMRYAKUSHO -- 新商品名称略称
         , SHMV.SHOPPRICEKIRIKAEDD -- ショップ価格切替日
         , SHMV.GENZSHOPPRICE -- 現ショップ価格
         , SHMV.SHINSHOPPRICE -- 新ショップ価格
         , SHMV.GENZSHOPMOCHIKAERIPRICE -- 現ショップ持ち帰り価格
         , SHMV.SHINSHOPMOCHIKAERIPRICE -- 新ショップ持ち帰り価格
         , SHMV.TAXSETTEIKBN -- 税設定区分
         , SHMV.TAXKBN -- 税区分
         , SHMV.URITRTSKITANINM -- 売上取扱単位名称
         , SHMV.HANSOKUKBN -- 販促区分
         , SHMV.HANSOKUSTRTDD -- 販促開始日
         , SHMV.HANSOKUSHRYDD -- 販促終了日
         , SHMV.HANSOKUNM -- 販促名称
         , SHMV.HANSOKUDLVRYPRICE -- 販促配達価格
         , SHMV.HANSOKUMOCHIKAERIPRICE -- 販促持ち帰り価格
         , SHMV.HANSOKUYUSENKBN -- 販促優先区分
         , SHMV.HYOJUNPRICEKIRIKAEDD -- 標準価格切替日
         , SHMV.GENZHYOJUNPRICE -- 現標準価格
         , SHMV.SHINHYOJUNPRICE -- 新標準価格
         , SHMV.GENZMOCHIKAERIPRICE -- 現持ち帰り価格
         , SHMV.SHINMOCHIKAERIPRICE -- 新持ち帰り価格
         , SHMV.GENZPRICEKBN -- 現価格区分
         , SHMV.SHINPRICEKBN -- 新価格区分
         , SHMV.SURYOHASUKANRIKBN -- 数量端数管理区分
         , SHMV.SHOPYUKOKBN -- ショップ有効区分
         , SHMV.MOBILERIYOUMU -- モバイル利用有無
         , SHMV.SHOPKSHNSYSTMYMD -- ショップ更新システム日付
         , SHMV.SHOPKSHNSYSTMJIKAN -- ショップ更新システム時間
      FROM S02V001T SHMV -- 商品マスタビュー
     WHERE SHMV.APKIHONCD = iApkihoncd
       AND SHMV.SHOPCD    = iShopcd
       AND ( ( iOutputkbn = '0' AND SHMV.MOBILERIYOUMU = '1' )
             OR
             ( iOutputkbn = '1' )
           ) -- モバイル利用有無
       AND ( ( iOutputkbn = '0' )
             OR
             ( iOutputkbn = '1'
               AND ( SHMV.KSHNSYSTMYMD >= iOutputtrgtdd -- 基本商品マスタ．更新システム日付
                     OR SHMV.SHOPKSHNSYSTMYMD >= iOutputtrgtdd -- ショップ商品マスタ．更新システム日付
                    )
              )
           )
       AND ( ( iOutputkbn = '0' AND SHMV.SHOPYUKOKBN = '1' )
             OR
             ( iOutputkbn = '1' )
           )
     ORDER BY SHMV.APKIHONCD, SHMV.SHOPCD, SHMV.SHNCD
  ;
  BEGIN
    file_name  := FID_SHOHIN
                 || '_' || iTrmnlid
                 || '_' || userinfo_rec.SYSTMYMD || userinfo_rec.SYSTMJIKAN
                 || '_' || NVL(iOutputtrgtdd, '00000000') || '.csv';
-- 本線対応変更 start ABI
--    file_handle := UTL_FILE.FOPEN( file_path , file_name, 'w' );
    file_handle := UTL_FILE.FOPEN( 'PL_SQL_DIR2' , file_name, 'w' );
-- 本線対応変更 end ABI

    -- ＣＳＶ作成
    FOR shohin_rec IN SELECT_SHOHIN LOOP
        UTL_FILE.PUT( file_handle,
          REPLACE(shohin_rec.APKIHONCD, ',') -- ＡＰ基本コード
         || ',' || REPLACE(shohin_rec.SHOPCD, ',') -- ショップコード
         || ',' || REPLACE(shohin_rec.SHNCD, ',') -- 商品コード
         || ',' || REPLACE(shohin_rec.STKBN, ',') -- セット区分
         || ',' || REPLACE(shohin_rec.LCTGRYCD, ',') -- 大分類コード
         || ',' || REPLACE(shohin_rec.MCTGRYCD, ',') -- 中分類コード
         || ',' || REPLACE(shohin_rec.SCTGRYCD, ',') -- 小分類コード
         || ',' || REPLACE(shohin_rec.LCTGRYNM, ',') -- 大分類名称
         || ',' || REPLACE(shohin_rec.MCTGRYNM, ',') -- 中分類名称
         || ',' || REPLACE(shohin_rec.SCTGRYNM, ',') -- 小分類名称
         || ',' || REPLACE(shohin_rec.SHNNMKIRIKAEDD, ',') -- 商品名称切替日
         || ',' || REPLACE(shohin_rec.GENZSHNNM, ',') -- 現商品名称
         || ',' || REPLACE(shohin_rec.SHINSHNNM, ',') -- 新商品名称
         || ',' || REPLACE(shohin_rec.GENZSHNNMKANA, ',') -- 現商品名称カナ
         || ',' || REPLACE(shohin_rec.SHINSHNNMKANA, ',') -- 新商品名称カナ
         || ',' || REPLACE(shohin_rec.GENZSHNNMRYAKUSHO, ',') -- 現商品名称略称
         || ',' || REPLACE(shohin_rec.SHINSHNNMRYAKUSHO, ',') -- 新商品名称略称
         || ',' || REPLACE(shohin_rec.SHOPPRICEKIRIKAEDD, ',') -- ショップ価格切替日
         || ',' || REPLACE(shohin_rec.GENZSHOPPRICE, ',') -- 現ショップ価格
         || ',' || REPLACE(shohin_rec.SHINSHOPPRICE, ',') -- 新ショップ価格
         || ',' || REPLACE(shohin_rec.GENZSHOPMOCHIKAERIPRICE, ',') -- 現ショップ持ち帰り価格
         || ',' || REPLACE(shohin_rec.SHINSHOPMOCHIKAERIPRICE, ',') -- 新ショップ持ち帰り価格
         || ',' || REPLACE(shohin_rec.TAXSETTEIKBN, ',') -- 税設定区分
         || ',' || REPLACE(shohin_rec.TAXKBN, ',') -- 税区分
         || ',' || REPLACE(shohin_rec.URITRTSKITANINM, ',') -- 売上取扱単位名称
         || ',' || REPLACE(shohin_rec.HANSOKUKBN, ',') -- 販促区分
         || ',' || REPLACE(shohin_rec.HANSOKUSTRTDD, ',') -- 販促開始日
         || ',' || REPLACE(shohin_rec.HANSOKUSHRYDD, ',') -- 販促終了日
         || ',' || REPLACE(shohin_rec.HANSOKUNM, ',') -- 販促名称
         || ',' || REPLACE(shohin_rec.HANSOKUDLVRYPRICE, ',') -- 販促配達価格
         || ',' || REPLACE(shohin_rec.HANSOKUMOCHIKAERIPRICE, ',') -- 販促持ち帰り価格
         || ',' || REPLACE(shohin_rec.HANSOKUYUSENKBN, ',') -- 販促優先区分
         || ',' || REPLACE(shohin_rec.HYOJUNPRICEKIRIKAEDD, ',') -- 標準価格切替日
         || ',' || REPLACE(shohin_rec.GENZHYOJUNPRICE, ',') -- 現標準価格
         || ',' || REPLACE(shohin_rec.SHINHYOJUNPRICE, ',') -- 新標準価格
         || ',' || REPLACE(shohin_rec.GENZMOCHIKAERIPRICE, ',') -- 現持ち帰り価格
         || ',' || REPLACE(shohin_rec.SHINMOCHIKAERIPRICE, ',') -- 新持ち帰り価格
         || ',' || REPLACE(shohin_rec.GENZPRICEKBN, ',') -- 現価格区分
         || ',' || REPLACE(shohin_rec.SHINPRICEKBN, ',') -- 新価格区分
         || ',' || REPLACE(shohin_rec.SURYOHASUKANRIKBN, ',') -- 数量端数管理区分
         || ',' || REPLACE(shohin_rec.SHOPYUKOKBN, ',') -- ショップ有効区分
         || ',' || REPLACE(shohin_rec.MOBILERIYOUMU, ',') -- モバイル利用有無
         || ',' || REPLACE(shohin_rec.SHOPKSHNSYSTMYMD, ',') -- ショップ更新システム日付
         || ',' || REPLACE(shohin_rec.SHOPKSHNSYSTMJIKAN, ',') -- ショップ更新システム時間
         || chr(13) || chr(10) -- 改行：\r\n
      );
      UTL_FILE.FFLUSH(file_handle);
    END LOOP;
    UTL_FILE.FCLOSE(file_handle);

-- 本線対応追加 start ABI
    FILEUPLOADTOS3( file_path , file_name );
-- 本線対応追加 end ABI

    -- EXCEPTION：処理終了
    EXCEPTION
      WHEN OTHERS THEN
        IF SELECT_SHOHIN%ISOPEN THEN
           CLOSE SELECT_SHOHIN;
        END IF;
        UTL_FILE.FCLOSE_ALL;
        -- エラー
        RAISE_APPLICATION_ERROR(-20013, SQLERRM || '商品データＣＳＶ作成時エラーが発生しました。');

  END csvShohin;

  /****************************************************************************/
  /* プロシージャ名 ：得意先データ出力                                             */
  /* 引数           ：iApkihoncd          ＡＰ基本コード                         */
  /*                ：iShopcd             ショップコード                         */
  /*                ：iTrmnlid            端末ID                               */
  /*                ：iOutputkbn          出力区分                              */
  /*                ：iOutputtrgtdd       出力対象日                            */
  /*                ：file_path           ファイルパス                           */
  /****************************************************************************/
  PROCEDURE csvTokuisaki( iApkihoncd IN VARCHAR2 -- ＡＰ基本コード
                        , iShopcd    IN VARCHAR2 -- ショップコード
                        , iTrmnlid   IN VARCHAR2 -- 端末ID
                        , iOutputkbn    IN VARCHAR2 -- 出力区分
                        , iOutputtrgtdd IN VARCHAR2 -- 出力対象日
                        , file_path  IN VARCHAR2 -- ファイルパス
  ) IS
  /* 変数宣言 */
  file_name     VARCHAR2(50) := NULL;
  file_handle  UTL_FILE.FILE_TYPE;
  /* CURSOR：得意先データ */
  CURSOR SELECT_TOKUISAKI IS
    SELECT TKSM.APKIHONCD -- ＡＰ基本コード
         , TKSM.SHOPCD -- ショップコード
         , TKSM.TOKUISAKICD -- 得意先コード
         , TKSM.TOKUISAKIJOTAI -- 得意先状態
         , TKSM.JOTAISTS -- 状態ステータス
         , TKSM.TOKUISAKIKBN -- 得意先区分
         , TKSM.JUTAKUDLVRYCSTKBN -- 受託配達顧客区分
         , TKSM.SKYSAKICD -- 請求先コード
         , TKSM.KAIINCD -- 会員ＮＯ
         , SPM1.SHOPNMRYAKUSHO -- ショップ名称略称
         , JGSM.JIGYOSHONM -- 事業所名
         , TKSM.TOKUISAKINMN1 -- 得意先名称１
         , TKSM.TOKUISAKINMN2 -- 得意先名称２
         , TKSM.TOKUISAKIKEISHOKBN -- 得意先敬称区分
         , TKSM.TOKUISAKINMKANAN1 -- 得意先名称カナ１
         , TKSM.YUBINNO -- 郵便番号
         , TKSM.ADRSN1 -- 住所１
         , TKSM.ADRSN2 -- 住所２
         , TKSM.ADRSN3 -- 住所３
         , TKSM.RENRAKUSAKINOKBNN1 -- 連絡先番号区分１
         , TKSM.RENRAKUSAKINON1 -- 連絡先番号１
         , TKSM.RENRAKUSAKIMEMON1 -- 連絡先メモ１
         , TKSM.RENRAKUSAKINOKBNN2 -- 連絡先番号区分２
         , TKSM.RENRAKUSAKINON2 -- 連絡先番号２
         , TKSM.RENRAKUSAKIMEMON2 -- 連絡先メモ２
         , TKSM.RENRAKUSAKINOKBNN3 -- 連絡先番号区分３
         , TKSM.RENRAKUSAKINON3 -- 連絡先番号３
         , TKSM.RENRAKUSAKIMEMON3 -- 連絡先メモ３
         , TKSM.RENRAKUSAKINOKBNN4 -- 連絡先番号区分４
         , TKSM.RENRAKUSAKINON4 -- 連絡先番号４
         , TKSM.RENRAKUSAKIMEMON4 -- 連絡先メモ４
         , TKSM.MOSHIKOMIRCPTDD -- 申込受付日
         , TKSM.RKNKINDTOKUBETSUUNTFLG -- 料金種別特別単価プラン
         , TKSM.PLNCD  -- プランコード
         , PLM.PLNNM -- プラン名称
         , TKSM.PLNCD2 -- プランコード２
         , PLM2.PLNNM PLNNM2 -- プラン名称２
         , TKSM.PLNCD3 -- プランコード３
         , PLM3.PLNNM PLNNM3 -- プラン名称３
         , TKSM.DLVRYKANKATSUKYOTENCD -- 配達管轄拠点コード
         , TKSM.DLVRYAREACD -- 配達エリアコード
         , TKSM.FZAIOKIKANOFLG -- 不在置き可能フラグ
         , TKSM.NOHINSAKIJOHON1 -- 納品先情報１
         , TKSM.NOHINSAKIJOHON2 -- 納品先情報２
         , TKSM.CHIZUNO -- 地図番号
         , TKSM.ACPTTEISHIFLG -- 受注停止フラグ
         , TKSM.CHUITOKUISAKIKBNAP -- 注意得意先区分ＡＰ
         , TKSM.PRNTMEMON1 -- 印刷メモ１
         , TKSM.PRNTMEMON2 -- 印刷メモ２
         , TKSM.PRNTMEMON3 -- 印刷メモ３
         , TKSM.KINOTPTNASHIFLG -- 金額出力無フラグ
         , TKSM.KEIZOKURNK -- 継続ランク
         , TKSM.SHOHIRNK -- 消費ランク
         , TKSM.APBETSUTOKUISAKIRNK -- ＡＰ別得意先ランク
         , SYSM_RNK.MOJISETTEIATAI APBTOKUISAKIRNKNM -- ＡＰ別得意先ランク名称
         , TKSM.YUKOKBN -- 有効区分
         , TKSM.KSHNSYSTMYMD -- 更新システム日付
         , TKSM.KSHNSYSTMJIKAN -- 更新システム時間
      FROM S02M013T TKSM -- 得意先マスタ
     INNER JOIN S02M046T SPM1-- ショップマスタ
             ON ( SPM1.APKIHONCD  = TKSM.APKIHONCD
                  AND SPM1.SHOPCD = TKSM.SHOPCD
                )
      LEFT JOIN S02M047T JGSM-- 事業所マスタ
             ON ( JGSM.APKIHONCD  = TKSM.APKIHONCD
                  AND JGSM.SHOPCD = TKSM.SHOPCD
                  AND JGSM.JIGYOSHOCD = TKSM.JIGYOSHOCD
                )
      LEFT OUTER JOIN S02M046T SPM2 -- ショップマスタ：委託相手ショップ
              ON ( SPM2.APKIHONCD  = TKSM.ITAKUAITEAPKIHONCD
                   AND SPM2.SHOPCD = TKSM.ITAKUAITESHOPCD
                 )
      LEFT OUTER JOIN S02M079T PLM -- プラン管理マスタ：プラン１
              ON ( PLM.PLNCD = TKSM.PLNCD
                   AND PLM.PLNZOKUSEI = '0'
                 )
      LEFT OUTER JOIN S02M079T PLM2 -- プラン管理マスタ：プラン２
              ON ( PLM2.PLNCD = TKSM.PLNCD2
                   AND PLM2.PLNZOKUSEI = '0'
                 )
      LEFT OUTER JOIN S02M079T PLM3 -- プラン管理マスタ：プラン３
              ON ( PLM3.PLNCD = TKSM.PLNCD3
                   AND PLM3.PLNZOKUSEI = '0'
                 )
      LEFT OUTER JOIN S02M006T SYSM_RNK -- システム管理マスタ：ＡＰ別得意先ランク名称
              ON ( SYSM_RNK.APKIHONCD  = TKSM.APKIHONCD
                   AND SYSM_RNK.SHOPCD = TKSM.SHOPCD
                   AND SYSM_RNK.SETTEINO = '023'
                   AND SYSM_RNK.RCRDKBN = 'M'
                   AND SYSM_RNK.SETTEICD = TKSM.APBETSUTOKUISAKIRNK
                   AND SYSM_RNK.YUKOKBN = '1'
                 )
     WHERE TKSM.APKIHONCD = iApkihoncd
       AND TKSM.SHOPCD    = iShopcd
       AND ( ( iOutputkbn = '0' AND TKSM.JOTAISTS NOT IN ( '02', '06', '10' ) ) -- 02:設置前ｷｬﾝｾﾙ; 06：退会済み: 10：退会済み
             OR
             ( iOutputkbn = '1' AND '1' = '1' )
           )
       AND ( ( iOutputkbn = '0' AND '1' = '1' )
             OR
             ( iOutputkbn = '1' AND TKSM.KSHNSYSTMYMD >= iOutputtrgtdd )
           )
       AND ( ( iOutputkbn = '0' AND TKSM.YUKOKBN = '1' )
             OR
             ( iOutputkbn = '1' AND '1' = '1' )
           )
     ORDER BY TKSM.APKIHONCD, TKSM.SHOPCD, TKSM.TOKUISAKICD
  ;
  BEGIN
    file_name  := FID_TOKUISAKI
                 || '_' || iTrmnlid
                 || '_' || userinfo_rec.SYSTMYMD || userinfo_rec.SYSTMJIKAN
                 || '_' || NVL(iOutputtrgtdd, '00000000') || '.csv';
-- 本線対応変更 start ABI
--    file_handle := UTL_FILE.FOPEN( file_path , file_name, 'w' );
    file_handle := UTL_FILE.FOPEN( 'PL_SQL_DIR2' , file_name, 'w' );
-- 本線対応変更 end ABI

    -- ＣＳＶ作成
    FOR tokuisaki_rec IN SELECT_TOKUISAKI LOOP
        UTL_FILE.PUT( file_handle,
          REPLACE(tokuisaki_rec.APKIHONCD, ',') -- ＡＰ基本コード
         || ',' || REPLACE(tokuisaki_rec.SHOPCD, ',') -- ショップコード
         || ',' || REPLACE(tokuisaki_rec.TOKUISAKICD, ',') -- 得意先コード
         || ',' || REPLACE(tokuisaki_rec.TOKUISAKIJOTAI, ',') -- 得意先状態
         || ',' || REPLACE(tokuisaki_rec.JOTAISTS, ',') -- 状態ステータス
         || ',' || REPLACE(tokuisaki_rec.TOKUISAKIKBN, ',') -- 得意先区分
         || ',' || REPLACE(tokuisaki_rec.JUTAKUDLVRYCSTKBN, ',') -- 受託配達顧客区分
         || ',' || REPLACE(tokuisaki_rec.SKYSAKICD, ',') -- 請求先コード
         || ',' || REPLACE(tokuisaki_rec.KAIINCD, ',') -- 会員ＮＯ
         || ',' || REPLACE(tokuisaki_rec.SHOPNMRYAKUSHO, ',') -- ショップ名称略称
         || ',' || REPLACE(tokuisaki_rec.JIGYOSHONM, ',') -- 事業所名
         || ',' || REPLACE(tokuisaki_rec.TOKUISAKINMN1, ',') -- 得意先名称１
         || ',' || REPLACE(tokuisaki_rec.TOKUISAKINMN2, ',') -- 得意先名称２
         || ',' || REPLACE(tokuisaki_rec.TOKUISAKIKEISHOKBN, ',') -- 得意先敬称区分
         || ',' || REPLACE(tokuisaki_rec.TOKUISAKINMKANAN1, ',') -- 得意先名称カナ１
         || ',' || REPLACE(tokuisaki_rec.YUBINNO, ',') -- 郵便番号
         || ',' || REPLACE(tokuisaki_rec.ADRSN1, ',') -- 住所１
         || ',' || REPLACE(tokuisaki_rec.ADRSN2, ',') -- 住所２
         || ',' || REPLACE(tokuisaki_rec.ADRSN3, ',') -- 住所３
         || ',' || REPLACE(tokuisaki_rec.RENRAKUSAKINOKBNN1, ',') -- 連絡先番号区分１
         || ',' || REPLACE(tokuisaki_rec.RENRAKUSAKINON1, ',') -- 連絡先番号１
         || ',' || REPLACE(tokuisaki_rec.RENRAKUSAKIMEMON1, ',') -- 連絡先メモ１
         || ',' || REPLACE(tokuisaki_rec.RENRAKUSAKINOKBNN2, ',') -- 連絡先番号区分２
         || ',' || REPLACE(tokuisaki_rec.RENRAKUSAKINON2, ',') -- 連絡先番号２
         || ',' || REPLACE(tokuisaki_rec.RENRAKUSAKIMEMON2, ',') -- 連絡先メモ２
         || ',' || REPLACE(tokuisaki_rec.RENRAKUSAKINOKBNN3, ',') -- 連絡先番号区分３
         || ',' || REPLACE(tokuisaki_rec.RENRAKUSAKINON3, ',') -- 連絡先番号３
         || ',' || REPLACE(tokuisaki_rec.RENRAKUSAKIMEMON3, ',') -- 連絡先メモ３
         || ',' || REPLACE(tokuisaki_rec.RENRAKUSAKINOKBNN4, ',') -- 連絡先番号区分４
         || ',' || REPLACE(tokuisaki_rec.RENRAKUSAKINON4, ',') -- 連絡先番号４
         || ',' || REPLACE(tokuisaki_rec.RENRAKUSAKIMEMON4, ',') -- 連絡先メモ４
         || ',' || REPLACE(tokuisaki_rec.MOSHIKOMIRCPTDD, ',') -- 申込受付日
         || ',' || REPLACE(tokuisaki_rec.RKNKINDTOKUBETSUUNTFLG, ',') -- 料金種別特別単価プラン
         || ',' || REPLACE(tokuisaki_rec.PLNCD, ',')  -- プランコード
         || ',' || REPLACE(tokuisaki_rec.PLNNM, ',')  -- プラン名称
         || ',' || REPLACE(tokuisaki_rec.PLNCD2, ',') -- プランコード２
         || ',' || REPLACE(tokuisaki_rec.PLNNM2, ',')  -- プラン名称２
         || ',' || REPLACE(tokuisaki_rec.PLNCD3, ',') -- プランコード３
         || ',' || REPLACE(tokuisaki_rec.PLNNM3, ',')  -- プラン名称３
         || ',' || REPLACE(tokuisaki_rec.DLVRYKANKATSUKYOTENCD, ',') -- 配達管轄拠点コード
         || ',' || REPLACE(tokuisaki_rec.DLVRYAREACD, ',') -- 配達エリアコード
         || ',' || REPLACE(tokuisaki_rec.FZAIOKIKANOFLG, ',') -- 不在置き可能フラグ
         || ',' || REPLACE(tokuisaki_rec.NOHINSAKIJOHON1, ',') -- 納品先情報１
         || ',' || REPLACE(tokuisaki_rec.NOHINSAKIJOHON2, ',') -- 納品先情報２
         || ',' || REPLACE(tokuisaki_rec.CHIZUNO, ',') -- 地図番号
         || ',' || REPLACE(tokuisaki_rec.ACPTTEISHIFLG, ',') -- 受注停止フラグ
         || ',' || REPLACE(tokuisaki_rec.CHUITOKUISAKIKBNAP, ',') -- 注意得意先区分ＡＰ
         || ',' || REPLACE(tokuisaki_rec.PRNTMEMON1, ',') -- 印刷メモ１
         || ',' || REPLACE(tokuisaki_rec.PRNTMEMON2, ',') -- 印刷メモ２
         || ',' || REPLACE(tokuisaki_rec.PRNTMEMON3, ',') -- 印刷メモ３
         || ',' || REPLACE(tokuisaki_rec.KINOTPTNASHIFLG, ',') -- 金額出力無フラグ
         || ',' || REPLACE(tokuisaki_rec.KEIZOKURNK, ',') -- 継続ランク
         || ',' || REPLACE(tokuisaki_rec.SHOHIRNK, ',') -- 消費ランク
         || ',' || REPLACE(tokuisaki_rec.APBETSUTOKUISAKIRNK, ',') -- ＡＰ別得意先ランク
         || ',' || REPLACE(tokuisaki_rec.APBTOKUISAKIRNKNM, ',') -- ＡＰ別得意先ランク名称
         || ',' || REPLACE(tokuisaki_rec.YUKOKBN, ',') -- 有効区分
         || ',' || REPLACE(tokuisaki_rec.KSHNSYSTMYMD, ',') -- 更新システム日付
         || ',' || REPLACE(tokuisaki_rec.KSHNSYSTMJIKAN, ',') -- 更新システム時間
         || chr(13) || chr(10) -- 改行：\r\n
      );
      UTL_FILE.FFLUSH(file_handle);
    END LOOP;
    UTL_FILE.FCLOSE(file_handle);

-- 本線対応追加 start ABI
    FILEUPLOADTOS3( file_path , file_name );
-- 本線対応追加 end ABI

    -- EXCEPTION：処理終了
    EXCEPTION
      WHEN OTHERS THEN
        IF SELECT_TOKUISAKI%ISOPEN THEN
           CLOSE SELECT_TOKUISAKI;
        END IF;
        UTL_FILE.FCLOSE_ALL;
        -- エラー
        RAISE_APPLICATION_ERROR(-20014, SQLERRM || '得意先データＣＳＶ作成時エラーが発生しました。');

  END csvTokuisaki;

  /****************************************************************************/
  /* プロシージャ名 ：請求先データ出力                                             */
  /* 引数           ：iApkihoncd          ＡＰ基本コード                         */
  /*                ：iShopcd             ショップコード                         */
  /*                ：iTrmnlid            端末ID                               */
  /*                ：iOutputkbn          出力区分                              */
  /*                ：iOutputtrgtdd       出力対象日                            */
  /*                ：file_path           ファイルパス                           */
  /****************************************************************************/
  PROCEDURE csvSkysaki( iApkihoncd IN VARCHAR2 -- ＡＰ基本コード
                      , iShopcd    IN VARCHAR2 -- ショップコード
                      , iTrmnlid   IN VARCHAR2 -- 端末ID
                      , iOutputkbn    IN VARCHAR2 -- 出力区分
                      , iOutputtrgtdd IN VARCHAR2 -- 出力対象日
                      , file_path  IN VARCHAR2 -- ファイルパス
  ) IS
  /* 変数宣言 */
  file_name     VARCHAR2(50) := NULL;
  file_handle  UTL_FILE.FILE_TYPE;
  /* CURSOR：請求先データ */
  CURSOR SELECT_SKYSAKI IS
    SELECT DISTINCT
           SKSM.APKIHONCD -- ＡＰ基本コード
         , SKSM.SHOPCD -- ショップコード
         , SKSM.SKYSAKICD -- 請求先コード
         , SKSM.SKYKBN -- 請求区分
         , SKSM.SKYSAKINMN1 -- 請求先名称１
         , SKSM.SKYSAKINMN2 -- 請求先名称２
         , SKSM.SKYSAKIKEISHOKBN -- 請求先敬称区分
         , SKSM.SKYSAKINMKANA -- 請求先名称カナ
         , SKSM.YUBINNO -- 郵便番号
         , SKSM.ADRSN1 -- 住所１
         , SKSM.ADRSN2 -- 住所２
         , SKSM.ADRSN3 -- 住所３
         , SKSM.RENRAKUSAKINOKBNN1 -- 連絡先番号区分１
         , SKSM.RENRAKUSAKINON1 -- 連絡先番号１
         , SKSM.RENRAKUSAKINOKBNN2 -- 連絡先番号区分２
         , SKSM.RENRAKUSAKINON2 -- 連絡先番号２
         , SKSM.RENRAKUSAKINOKBNN3 -- 連絡先番号区分３
         , SKSM.RENRAKUSAKINON3 -- 連絡先番号３
         , SKSM.RENRAKUSAKINOKBNN4 -- 連絡先番号区分４
         , SKSM.RENRAKUSAKINON4 -- 連絡先番号４
         , SKSM.SKYSHIMEDD -- 請求締日
         , SKSM.SHRSAITO -- 支払サイト
         , SKSM.SHRDD -- 支払日
         , SKSM.KAISHUHOHOKBN -- 回収方法区分
         , SKSM.SHUKINDD -- 集金日
         , SKSM.TAIRYUKBN -- 滞留区分
         , SKSM.TAIRYUKIN -- 滞留金額
         , SKSM.OYATOKUISAKICD -- 親得意先コード
         , SKSM.YUKOKBN -- 有効区分
         , SKSM.KSHNSYSTMYMD -- 更新システム日付
         , SKSM.KSHNSYSTMJIKAN -- 更新システム時間
      FROM S02M017T SKSM -- 請求先マスタ
     LEFT JOIN S02M013T TKSM -- 得意先マスタ
             ON ( TKSM.APKIHONCD     = SKSM.APKIHONCD
                  AND TKSM.SHOPCD    = SKSM.SHOPCD
                  AND TKSM.SKYSAKICD = SKSM.SKYSAKICD
                  AND TKSM.APKIHONCD = iApkihoncd
                  AND TKSM.SHOPCD    = iShopcd
                  AND ( ( iOutputkbn = '0' AND '1' = '1' )
                        OR
                        ( iOutputkbn = '1' AND TKSM.KSHNSYSTMYMD >= iOutputtrgtdd )
                      )
                )
     WHERE SKSM.APKIHONCD = iApkihoncd
       AND SKSM.SHOPCD    = iShopcd
       AND ( ( iOutputkbn = '0' AND '1' = '1' )
             OR
             ( iOutputkbn = '1' AND SKSM.KSHNSYSTMYMD >= iOutputtrgtdd )
           )
       AND ( ( iOutputkbn = '0' AND SKSM.YUKOKBN = '1' )
             OR
             ( iOutputkbn = '1' AND '1' = '1' )
           )
       AND ( ( iOutputkbn = '0' AND (TKSM.JOTAISTS is null OR
                                     ( TKSM.JOTAISTS NOT IN ( '02', '06', '10' ) -- 02:設置前ｷｬﾝｾﾙ; 06：退会済み: 10：退会済み
                                       AND
                                       ( ( iOutputkbn = '0' AND TKSM.YUKOKBN = '1' )
                                          OR
                                         ( iOutputkbn = '1' AND '1' = '1' )
                                       )
                                     )
                                     )
              )
             OR
             ( iOutputkbn = '1' AND '1' = '1' )
           )
     ORDER BY SKSM.APKIHONCD, SKSM.SHOPCD, SKSM.SKYSAKICD
  ;
  BEGIN
    file_name  := FID_SKYSAKI
                 || '_' || iTrmnlid
                 || '_' || userinfo_rec.SYSTMYMD || userinfo_rec.SYSTMJIKAN
                 || '_' || NVL(iOutputtrgtdd, '00000000') || '.csv';
-- 本線対応変更 start ABI
--    file_handle := UTL_FILE.FOPEN( file_path , file_name, 'w' );
    file_handle := UTL_FILE.FOPEN( 'PL_SQL_DIR2' , file_name, 'w' );
-- 本線対応変更 end ABI

    -- ＣＳＶ作成
    FOR skysaki_rec IN SELECT_SKYSAKI LOOP
        UTL_FILE.PUT( file_handle,
          REPLACE(skysaki_rec.APKIHONCD, ',') -- ＡＰ基本コード
         || ',' || REPLACE(skysaki_rec.SHOPCD, ',') -- ショップコード
         || ',' || REPLACE(skysaki_rec.SKYSAKICD, ',') -- 請求先コード
         || ',' || REPLACE(skysaki_rec.SKYKBN, ',') -- 請求区分
         || ',' || REPLACE(skysaki_rec.SKYSAKINMN1, ',') -- 請求先名称１
         || ',' || REPLACE(skysaki_rec.SKYSAKINMN2, ',') -- 請求先名称２
         || ',' || REPLACE(skysaki_rec.SKYSAKIKEISHOKBN, ',') -- 請求先敬称区分
         || ',' || REPLACE(skysaki_rec.SKYSAKINMKANA, ',') -- 請求先名称カナ
         || ',' || REPLACE(skysaki_rec.YUBINNO, ',') -- 郵便番号
         || ',' || REPLACE(skysaki_rec.ADRSN1, ',') -- 住所１
         || ',' || REPLACE(skysaki_rec.ADRSN2, ',') -- 住所２
         || ',' || REPLACE(skysaki_rec.ADRSN3, ',') -- 住所３
         || ',' || REPLACE(skysaki_rec.RENRAKUSAKINOKBNN1, ',') -- 連絡先番号区分１
         || ',' || REPLACE(skysaki_rec.RENRAKUSAKINON1, ',') -- 連絡先番号１
         || ',' || REPLACE(skysaki_rec.RENRAKUSAKINOKBNN2, ',') -- 連絡先番号区分２
         || ',' || REPLACE(skysaki_rec.RENRAKUSAKINON2, ',') -- 連絡先番号２
         || ',' || REPLACE(skysaki_rec.RENRAKUSAKINOKBNN3, ',') -- 連絡先番号区分３
         || ',' || REPLACE(skysaki_rec.RENRAKUSAKINON3, ',') -- 連絡先番号３
         || ',' || REPLACE(skysaki_rec.RENRAKUSAKINOKBNN4, ',') -- 連絡先番号区分４
         || ',' || REPLACE(skysaki_rec.RENRAKUSAKINON4, ',') -- 連絡先番号４
         || ',' || REPLACE(skysaki_rec.SKYSHIMEDD, ',') -- 請求締日
         || ',' || REPLACE(skysaki_rec.SHRSAITO, ',') -- 支払サイト
         || ',' || REPLACE(skysaki_rec.SHRDD, ',') -- 支払日
         || ',' || REPLACE(skysaki_rec.KAISHUHOHOKBN, ',') -- 回収方法区分
         || ',' || REPLACE(skysaki_rec.SHUKINDD, ',') -- 集金日
         || ',' || REPLACE(skysaki_rec.TAIRYUKBN, ',') -- 滞留区分
         || ',' || REPLACE(skysaki_rec.TAIRYUKIN, ',') -- 滞留金額
         || ',' || REPLACE(skysaki_rec.OYATOKUISAKICD, ',') -- 親得意先コード
         || ',' || REPLACE(skysaki_rec.YUKOKBN, ',') -- 有効区分
         || ',' || REPLACE(skysaki_rec.KSHNSYSTMYMD, ',') -- 更新システム日付
         || ',' || REPLACE(skysaki_rec.KSHNSYSTMJIKAN, ',') -- 更新システム時間
         || chr(13) || chr(10) -- 改行：\r\n
      );
      UTL_FILE.FFLUSH(file_handle);
    END LOOP;
    UTL_FILE.FCLOSE(file_handle);

-- 本線対応追加 start ABI
    FILEUPLOADTOS3( file_path , file_name );
-- 本線対応追加 end ABI

    -- EXCEPTION：処理終了
    EXCEPTION
      WHEN OTHERS THEN
        IF SELECT_SKYSAKI%ISOPEN THEN
           CLOSE SELECT_SKYSAKI;
        END IF;
        UTL_FILE.FCLOSE_ALL;
        -- エラー
        RAISE_APPLICATION_ERROR(-20015, SQLERRM || '請求先データＣＳＶ作成時エラーが発生しました。');

  END csvSkysaki;

  /****************************************************************************/
  /* プロシージャ名 ：得意先別商品別単価データ出力                                   */
  /* 引数           ：iApkihoncd          ＡＰ基本コード                         */
  /*                ：iShopcd             ショップコード                         */
  /*                ：iTrmnlid            端末ID                               */
  /*                ：iOutputkbn          出力区分                              */
  /*                ：iOutputtrgtdd       出力対象日                            */
  /*                ：file_path           ファイルパス                           */
  /****************************************************************************/
  PROCEDURE csvTanka( iApkihoncd IN VARCHAR2 -- ＡＰ基本コード
                    , iShopcd    IN VARCHAR2 -- ショップコード
                    , iTrmnlid   IN VARCHAR2 -- 端末ID
                    , iOutputkbn    IN VARCHAR2 -- 出力区分
                    , iOutputtrgtdd IN VARCHAR2 -- 出力対象日
                    , file_path  IN VARCHAR2 -- ファイルパス
  ) IS
  /* 変数宣言 */
  file_name     VARCHAR2(50) := NULL;
  file_handle  UTL_FILE.FILE_TYPE;
  /* CURSOR：得意先別商品別単価データ */
  CURSOR SELECT_TANKA IS
    SELECT TSTM.APKIHONCD -- ＡＰ基本コード
         , TSTM.SHOPCD -- ショップコード
         , TSTM.SHNCD -- 商品コード
         , TSTM.TOKUISAKICD -- 得意先コード
         , TSTM.TEKIYOSTRTDD -- 適用開始日
         , TSTM.TEKIYOSHRYDD -- 適用終了日
         , TSTM.DLVRYPRICE -- 配達価格
         , TSTM.MOCHIKAERIPRICE -- 持ち帰り価格
         , TSTM.YUKOKBN -- 有効区分
         , TSTM.KSHNSYSTMYMD -- 更新システム日付
         , TSTM.KSHNSYSTMJIKAN -- 更新システム時間
      FROM S02M009T TSTM -- 得意先別商品別単価マスタ
     INNER JOIN S02M043T SPSM -- ショップ商品マスタ
             ON ( SPSM.APKIHONCD  = TSTM.APKIHONCD
                  AND SPSM.SHOPCD = TSTM.SHOPCD
                  AND SPSM.SHNCD  = TSTM.SHNCD
                  AND SPSM.MOBILERIYOUMU = '1'
                )
     WHERE TSTM.APKIHONCD = iApkihoncd
       AND TSTM.SHOPCD    = iShopcd
       AND ( ( iOutputkbn = '0' AND TSTM.TEKIYOSHRYDD >= userinfo_rec.UNYOYMD )
             OR
             ( iOutputkbn = '1' AND '1' = '1' )
           )
       AND ( ( iOutputkbn = '0' AND '1' = '1' )
             OR
             ( iOutputkbn = '1' AND TSTM.KSHNSYSTMYMD >= iOutputtrgtdd )
           )
       AND ( ( iOutputkbn = '0' AND TSTM.YUKOKBN = '1' )
             OR
             ( iOutputkbn = '1' AND '1' = '1' )
           )
    UNION
    SELECT TSUM.APKIHONCD -- ＡＰ基本コード
         , TSUM.SHOPCD -- ショップコード
         , TSUM.STSHNCD AS SHNCD -- セット商品コード
         , TSUM.TOKUISAKICD -- 得意先コード
         , TSUM.TEKIYOSTRTDD -- 適用開始日
         , TSUM.TEKIYOSHRYDD -- 適用終了日
         , TSUM.DLVRYPRICE -- 配達価格
         , TSUM.MOCHIKAERIPRICE -- 持ち帰り価格
         , TSUM.YUKOKBN -- 有効区分
         , TSUM.KSHNSYSTMYMD -- 更新システム日付
         , TSUM.KSHNSYSTMJIKAN -- 更新システム時間
      FROM S02M066T TSUM -- 得意先別セット商品単価マスタ
     INNER JOIN S02M043T SPSM -- ショップ商品マスタ
             ON ( SPSM.APKIHONCD  = TSUM.APKIHONCD
                  AND SPSM.SHOPCD = TSUM.SHOPCD
                  AND SPSM.SHNCD  = TSUM.STSHNCD
                  AND SPSM.MOBILERIYOUMU = '1'
                )
     WHERE TSUM.APKIHONCD = iApkihoncd
       AND TSUM.SHOPCD    = iShopcd
       AND ( ( iOutputkbn = '0' AND TSUM.TEKIYOSHRYDD >= userinfo_rec.UNYOYMD )
             OR
             ( iOutputkbn = '1' AND '1' = '1' )
           )
       AND ( ( iOutputkbn = '0' AND '1' = '1' )
             OR
             ( iOutputkbn = '1' AND TSUM.KSHNSYSTMYMD >= iOutputtrgtdd )

           )
       AND ( ( iOutputkbn = '0' AND TSUM.YUKOKBN = '1' )
             OR
             ( iOutputkbn = '1' AND '1' = '1' )
           )
     ORDER BY APKIHONCD, SHOPCD, TOKUISAKICD, SHNCD
  ;
  BEGIN
    file_name  := FID_TANKA
                 || '_' || iTrmnlid
                 || '_' || userinfo_rec.SYSTMYMD || userinfo_rec.SYSTMJIKAN
                 || '_' || NVL(iOutputtrgtdd, '00000000') || '.csv';
-- 本線対応変更 start ABI
--    file_handle := UTL_FILE.FOPEN( file_path , file_name, 'w' );
    file_handle := UTL_FILE.FOPEN( 'PL_SQL_DIR2' , file_name, 'w' );
-- 本線対応変更 end ABI

    -- ＣＳＶ作成
    FOR tanka_rec IN SELECT_TANKA LOOP
        UTL_FILE.PUT( file_handle,
          REPLACE(tanka_rec.APKIHONCD, ',') -- ＡＰ基本コード
         || ',' || REPLACE(tanka_rec.SHOPCD, ',') -- ショップコード
         || ',' || REPLACE(tanka_rec.SHNCD, ',') -- 商品コード
         || ',' || REPLACE(tanka_rec.TOKUISAKICD, ',') -- 得意先コード
         || ',' || REPLACE(tanka_rec.TEKIYOSTRTDD, ',') -- 適用開始日
         || ',' || REPLACE(tanka_rec.TEKIYOSHRYDD, ',') -- 適用終了日
         || ',' || REPLACE(tanka_rec.DLVRYPRICE, ',') -- 配達価格
         || ',' || REPLACE(tanka_rec.MOCHIKAERIPRICE, ',') -- 持ち帰り価格
         || ',' || REPLACE(tanka_rec.YUKOKBN, ',') -- 有効区分
         || ',' || REPLACE(tanka_rec.KSHNSYSTMYMD, ',') -- 更新システム日付
         || ',' || REPLACE(tanka_rec.KSHNSYSTMJIKAN, ',') -- 更新システム時間
         || chr(13) || chr(10) -- 改行：\r\n
      );
      UTL_FILE.FFLUSH(file_handle);
    END LOOP;
    UTL_FILE.FCLOSE(file_handle);

-- 本線対応追加 start ABI
    FILEUPLOADTOS3( file_path , file_name );
-- 本線対応追加 end ABI

    -- EXCEPTION：処理終了
    EXCEPTION
      WHEN OTHERS THEN
        IF SELECT_TANKA%ISOPEN THEN
           CLOSE SELECT_TANKA;
        END IF;
        UTL_FILE.FCLOSE_ALL;
        -- エラー
        RAISE_APPLICATION_ERROR(-20016, SQLERRM || '得意先別商品別単価データＣＳＶ作成時エラーが発生しました。');

  END csvTanka;

  /****************************************************************************/
  /* プロシージャ名 ：ＡＰ／ショップデータ出力                                      */
  /* 引数           ：iApkihoncd          ＡＰ基本コード                         */
  /*                ：iShopcd             ショップコード                         */
  /*                ：iTrmnlid            端末ID                               */
  /*                ：iOutputkbn          出力区分                              */
  /*                ：iOutputtrgtdd       出力対象日                            */
  /*                ：file_path           ファイルパス                           */
  /****************************************************************************/
  PROCEDURE csvApshop( iApkihoncd IN VARCHAR2 -- ＡＰ基本コード
                     , iShopcd    IN VARCHAR2 -- ショップコード
                     , iTrmnlid   IN VARCHAR2 -- 端末ID
                     , iOutputkbn    IN VARCHAR2 -- 出力区分
                     , iOutputtrgtdd IN VARCHAR2 -- 出力対象日
                     , file_path  IN VARCHAR2 -- ファイルパス
  ) IS
  /* 変数宣言 */
  file_name     VARCHAR2(50) := NULL;
  file_handle  UTL_FILE.FILE_TYPE;
  /* CURSOR：ＡＰ／ショップデータ */
  CURSOR SELECT_APSHOP IS
    SELECT SHPM.APKIHONCD -- ＡＰ基本コード
         , SHPM.SHOPCD -- ショップコード
         , APKM.APNM -- ＡＰ名称
         , SHPM.SHOPNM -- ショップ名称
         , SHPM.SHOPNMRYAKUSHO -- ショップ名称略称
         , SHPM.CMPNYNMN1 -- 会社名称１
         , SHPM.CMPNYNMN2 -- 会社名称２
         , SHPM.YUBINNO -- 郵便番号
         , SHPM.ADRSN1 -- 住所１
         , SHPM.ADRSN2 -- 住所２
         , SHPM.ADRSN3 -- 住所３
         , SHPM.RENRAKUSAKINON1 -- 連絡先番号１
         , SHPM.RENRAKUSAKINON2 -- 連絡先番号２
         , SHPM.TAXHASUKEISANKBN -- 税端数計算区分
         , SHPM.NOSHOAZYOKIKAZUDSPLYKBN -- 納品書お預け容器数表示区分
         , SHPM.URIKINHASUKEISANKBN -- 売上金額端数計算区分
         , '' SHINSHOHITAX -- 新消費税
         , '' SHOHITAXKIRIKAEDD -- 消費税切替日
         , '' GENZSHOHITAX -- 現消費税
         , SHPM.SHINTAXKEISANKBNURI -- 新税計算区分売上
         , SHPM.TAXKEISANKBNURIKIRIKAEDD -- 税計算区分売上切替日
         , SHPM.GENZTAXKEISANKBNURI -- 現税計算区分売上
         , SHPM.STSHNMEISAIINJIFLG -- セット商品明細印字フラグ
         , SHPM.TAIKAITHMIKAISHUKINDSPLYKBN -- 退会時納品書未回収金額表示区分
-- インボイス対応追加 start ABI
         , SHPM.INVOICEJIGYOSHAINPTNO -- インボイス事業者登録番号
         , SHPM.INVOICSHIKOYMD        -- インボイス施行日
-- インボイス対応追加 end ABI
         , SHPM.YUKOKBN -- 有効区分
         , SHPM.KSHNSYSTMYMD -- 更新システム日付
         , SHPM.KSHNSYSTMJIKAN -- 更新システム時間
      FROM S02M046T SHPM -- ショップマスタ
     INNER JOIN S02M001T APKM -- ＡＰ基本マスタ
             ON ( SHPM.APKIHONCD  = APKM.APKIHONCD
                )
     WHERE SHPM.APKIHONCD = iApkihoncd
       AND SHPM.SHOPCD    = iShopcd
       AND ( ( iOutputkbn = '0' AND '1' = '1' )
             OR
             ( iOutputkbn = '1' AND SHPM.KSHNSYSTMYMD >= iOutputtrgtdd )
           )
       AND ( ( iOutputkbn = '0' AND SHPM.YUKOKBN = '1' )
             OR
             ( iOutputkbn = '1' AND '1' = '1' )
           )
     ORDER BY SHPM.APKIHONCD, SHPM.SHOPCD
  ;
  BEGIN
    file_name  := FID_APSHOP
                 || '_' || iTrmnlid
                 || '_' || userinfo_rec.SYSTMYMD || userinfo_rec.SYSTMJIKAN
                 || '_' || NVL(iOutputtrgtdd, '00000000') || '.csv';
-- 本線対応変更 start ABI
--    file_handle := UTL_FILE.FOPEN( file_path , file_name, 'w' );
    file_handle := UTL_FILE.FOPEN( 'PL_SQL_DIR2' , file_name, 'w' );
-- 本線対応変更 end ABI

    -- ＣＳＶ作成
    FOR apshop_rec IN SELECT_APSHOP LOOP
        UTL_FILE.PUT( file_handle,
          REPLACE(apshop_rec.APKIHONCD, ',') -- ＡＰ基本コード
         || ',' || REPLACE(apshop_rec.SHOPCD, ',') -- ショップコード
         || ',' || REPLACE(apshop_rec.APNM, ',') -- ＡＰ名称
         || ',' || REPLACE(apshop_rec.SHOPNM, ',') -- ショップ名称
         || ',' || REPLACE(apshop_rec.SHOPNMRYAKUSHO, ',') -- ショップ名称略称
         || ',' || REPLACE(apshop_rec.CMPNYNMN1, ',') -- 会社名称１
         || ',' || REPLACE(apshop_rec.CMPNYNMN2, ',') -- 会社名称２
         || ',' || REPLACE(apshop_rec.YUBINNO, ',') -- 郵便番号
         || ',' || REPLACE(apshop_rec.ADRSN1, ',') -- 住所１
         || ',' || REPLACE(apshop_rec.ADRSN2, ',') -- 住所２
         || ',' || REPLACE(apshop_rec.ADRSN3, ',') -- 住所３
         || ',' || REPLACE(apshop_rec.RENRAKUSAKINON1, ',') -- 連絡先番号１
         || ',' || REPLACE(apshop_rec.RENRAKUSAKINON2, ',') -- 連絡先番号２
         || ',' || REPLACE(apshop_rec.TAXHASUKEISANKBN, ',') -- 税端数計算区分
         || ',' || REPLACE(apshop_rec.NOSHOAZYOKIKAZUDSPLYKBN, ',') -- 納品書お預け容器数表示区分
         || ',' || REPLACE(apshop_rec.URIKINHASUKEISANKBN, ',') -- 売上金額端数計算区分
         || ',' || REPLACE(apshop_rec.SHINSHOHITAX, ',') -- 新消費税
         || ',' || REPLACE(apshop_rec.SHOHITAXKIRIKAEDD, ',') -- 消費税切替日
         || ',' || REPLACE(apshop_rec.GENZSHOHITAX, ',') -- 現消費税
         || ',' || REPLACE(apshop_rec.SHINTAXKEISANKBNURI, ',') -- 新税計算区分売上
         || ',' || REPLACE(apshop_rec.TAXKEISANKBNURIKIRIKAEDD, ',') -- 税計算区分売上切替日
         || ',' || REPLACE(apshop_rec.GENZTAXKEISANKBNURI, ',') -- 現税計算区分売上
         || ',' || REPLACE(apshop_rec.STSHNMEISAIINJIFLG, ',')-- セット商品明細印字フラグ
         || ',' || REPLACE(apshop_rec.TAIKAITHMIKAISHUKINDSPLYKBN, ',') -- 退会時納品書未回収金額表示区分
-- インボイス対応追加 start ABI
         || ',' || REPLACE(apshop_rec.INVOICEJIGYOSHAINPTNO, ',') -- インボイス事業者登録番号
         || ',' || REPLACE(apshop_rec.INVOICSHIKOYMD, ',') -- インボイス施行日
-- インボイス対応追加 end ABI
         || ',' || REPLACE(apshop_rec.YUKOKBN, ',') -- 有効区分
         || ',' || REPLACE(apshop_rec.KSHNSYSTMYMD, ',') -- 更新システム日付
         || ',' || REPLACE(apshop_rec.KSHNSYSTMJIKAN, ',') -- 更新システム時間
         || chr(13) || chr(10) -- 改行：\r\n
      );
      UTL_FILE.FFLUSH(file_handle);
    END LOOP;
    UTL_FILE.FCLOSE(file_handle);

-- 本線対応追加 start ABI
    FILEUPLOADTOS3( file_path , file_name );
-- 本線対応追加 end ABI

    -- EXCEPTION：処理終了
    EXCEPTION
      WHEN OTHERS THEN
        IF SELECT_APSHOP%ISOPEN THEN
           CLOSE SELECT_APSHOP;
        END IF;
        UTL_FILE.FCLOSE_ALL;
        -- エラー
        RAISE_APPLICATION_ERROR(-20017, SQLERRM || 'ＡＰ／ショップデータＣＳＶ作成時エラーが発生しました。');

  END csvApshop;

  /****************************************************************************/
  /* プロシージャ名 ：担当者データ出力                                             */
  /* 引数           ：iApkihoncd          ＡＰ基本コード                         */
  /*                ：iShopcd             ショップコード                         */
  /*                ：iTrmnlid            端末ID                               */
  /*                ：iOutputkbn          出力区分                              */
  /*                ：iOutputtrgtdd       出力対象日                            */
  /*                ：file_path           ファイルパス                           */
  /****************************************************************************/
  PROCEDURE csvTnsha( iApkihoncd IN VARCHAR2 -- ＡＰ基本コード
                    , iShopcd    IN VARCHAR2 -- ショップコード
                    , iTrmnlid   IN VARCHAR2 -- 端末ID
                    , iOutputkbn    IN VARCHAR2 -- 出力区分
                    , iOutputtrgtdd IN VARCHAR2 -- 出力対象日
                    , file_path  IN VARCHAR2 -- ファイルパス
  ) IS
  /* 変数宣言 */
  file_name     VARCHAR2(50) := NULL;
  file_handle  UTL_FILE.FILE_TYPE;
  /* CURSOR：担当者データ */
  CURSOR SELECT_TNSHA IS
    SELECT TNSM.APKIHONCD -- ＡＰ基本コード
         , TNSM.SHOPCD -- ショップコード
         , TNSM.TNSHACD -- 担当者コード
         , TNSM.TNSHAMEI -- 担当者名
         , TNSM.TNSHAMEIKANA -- 担当者名カナ
         , TNSM.SHOZOKUJIGYOSHOCD -- 所属事業所コード
         , TNSM.AKSHANIKBN -- アクセス範囲区分
         , TNSM.MOBILERIYOUMU -- モバイル利用有無
         , TNSM.TNSHASTS -- 担当者ステータス
         , TNSM.LOGINID -- ログインＩＤ
         , TNSM.PSW -- パスワード
         , TNSM.DLVRYTNSHAKBN -- 配達担当者区分
         , TNSM.YUKOKBN -- 有効区分
         , TNSM.KSHNSYSTMYMD -- 更新システム日付
         , TNSM.KSHNSYSTMJIKAN -- 更新システム時間
      FROM S02M003T TNSM -- 担当者マスタ
     WHERE TNSM.APKIHONCD = iApkihoncd
       AND TNSM.SHOPCD    = iShopcd
       AND ( ( iOutputkbn = '0' AND TNSM.MOBILERIYOUMU = '1' )
             OR
             ( iOutputkbn = '1' )
           )
       AND ( ( iOutputkbn = '0' AND '1' = '1' )
             OR
             ( iOutputkbn = '1' AND TNSM.KSHNSYSTMYMD >= iOutputtrgtdd )
           )
       AND ( ( iOutputkbn = '0' AND TNSM.YUKOKBN = '1' )
             OR
             ( iOutputkbn = '1' AND '1' = '1' )
           )
     ORDER BY TNSM.APKIHONCD, TNSM.SHOPCD, TNSM.TNSHACD
  ;
  BEGIN
    file_name  := FID_TNSHA
                 || '_' || iTrmnlid
                 || '_' || userinfo_rec.SYSTMYMD || userinfo_rec.SYSTMJIKAN
                 || '_' || NVL(iOutputtrgtdd, '00000000') || '.csv';
-- 本線対応変更 start ABI
--    file_handle := UTL_FILE.FOPEN( file_path , file_name, 'w' );
    file_handle := UTL_FILE.FOPEN( 'PL_SQL_DIR2' , file_name, 'w' );
-- 本線対応変更 end ABI

    -- ＣＳＶ作成
    FOR tnsha_rec IN SELECT_TNSHA LOOP
        UTL_FILE.PUT( file_handle,
          REPLACE(tnsha_rec.APKIHONCD, ',') -- ＡＰ基本コード
         || ',' || REPLACE(tnsha_rec.SHOPCD, ',') -- ショップコード
         || ',' || REPLACE(tnsha_rec.TNSHACD, ',') -- 担当者コード
         || ',' || REPLACE(tnsha_rec.TNSHAMEI, ',') -- 担当者名
         || ',' || REPLACE(tnsha_rec.TNSHAMEIKANA, ',') -- 担当者名カナ
         || ',' || REPLACE(tnsha_rec.SHOZOKUJIGYOSHOCD, ',') -- 所属事業所コード
         || ',' || REPLACE(tnsha_rec.AKSHANIKBN, ',') -- アクセス範囲区分
         || ',' || REPLACE(tnsha_rec.MOBILERIYOUMU, ',') -- モバイル利用有無
         || ',' || REPLACE(tnsha_rec.TNSHASTS, ',') -- 担当者ステータス
         || ',' || REPLACE(tnsha_rec.LOGINID, ',') -- ログインＩＤ
         || ',' || REPLACE(tnsha_rec.PSW, ',') -- パスワード
         || ',' || REPLACE(tnsha_rec.DLVRYTNSHAKBN, ',') -- 配達担当者区分
         || ',' || REPLACE(tnsha_rec.YUKOKBN, ',') -- 有効区分
         || ',' || REPLACE(tnsha_rec.KSHNSYSTMYMD, ',') -- 更新システム日付
         || ',' || REPLACE(tnsha_rec.KSHNSYSTMJIKAN, ',') -- 更新システム時間
         || chr(13) || chr(10) -- 改行：\r\n
      );
      UTL_FILE.FFLUSH(file_handle);
    END LOOP;
    UTL_FILE.FCLOSE(file_handle);

-- 本線対応追加 start ABI
    FILEUPLOADTOS3( file_path , file_name );
-- 本線対応追加 end ABI

    -- EXCEPTION：処理終了
    EXCEPTION
      WHEN OTHERS THEN
        IF SELECT_TNSHA%ISOPEN THEN
           CLOSE SELECT_TNSHA;
        END IF;
        UTL_FILE.FCLOSE_ALL;
        -- エラー
        RAISE_APPLICATION_ERROR(-20018, SQLERRM || '担当者データＣＳＶ作成時エラーが発生しました。');

  END csvTnsha;


  /****************************************************************************/
  /* プロシージャ名 ：展開データ出力                                             */
  /* 引数           ：iApkihoncd          ＡＰ基本コード                         */
  /*                ：iShopcd             ショップコード                         */
  /*                ：iTrmnlid            端末ID                               */
  /*                ：iOutputkbn          出力区分                              */
  /*                ：iOutputtrgtdd       出力対象日                            */
  /*                ：file_path           ファイルパス                           */
  /****************************************************************************/
  PROCEDURE csvTenkai( iApkihoncd IN VARCHAR2 -- ＡＰ基本コード
                     , iShopcd    IN VARCHAR2 -- ショップコード
                     , iTrmnlid   IN VARCHAR2 -- 端末ID
                     , iOutputkbn    IN VARCHAR2 -- 出力区分
                     , iOutputtrgtdd IN VARCHAR2 -- 出力対象日
                     , file_path  IN VARCHAR2 -- ファイルパス
  ) IS
  /* 変数宣言 */
  file_name     VARCHAR2(50) := NULL;
  file_handle  UTL_FILE.FILE_TYPE;
  /* CURSOR：展開データ */
  CURSOR SELECT_TENKAI IS
    SELECT TENK.APKIHONCD         -- ＡＰ基本コード
         , TENK.STSHNCD           -- セット商品コード
         , TENK.RENBAN            -- 連番
         , TENK.KOSEISHNAPKIHONCD -- 構成商品ＡＰ基本コード
         , TENK.KOSEISHNCD        -- 構成商品コード
         , TENK.KEIJOKBN          -- 計上区分
         , TENK.SURYO             -- 数量
         , TENK.DLVRYPRICE        -- 配達価格
         , TENK.MOCHIKAERIPRICE   -- 持ち帰り価格
         , TENK.YUKOKBN           -- 有効区分
         , TENK.KSHNSYSTMYMD      -- 更新システム日付
         , TENK.KSHNSYSTMJIKAN    -- 更新システム時間
      FROM S02M008T TENK -- 展開マスタ
     WHERE TENK.APKIHONCD = iApkihoncd
       AND ( ( iOutputkbn = '0' AND '1' = '1' )
             OR
             ( iOutputkbn = '1' AND TENK.KSHNSYSTMYMD >= iOutputtrgtdd )
           )
     ORDER BY TENK.APKIHONCD, TENK.STSHNCD, TENK.RENBAN
  ;
  BEGIN
    file_name  := FID_TENKAI
                 || '_' || iTrmnlid
                 || '_' || userinfo_rec.SYSTMYMD || userinfo_rec.SYSTMJIKAN
                 || '_' || NVL(iOutputtrgtdd, '00000000') || '.csv';
-- 本線対応変更 start ABI
--    file_handle := UTL_FILE.FOPEN( file_path , file_name, 'w' );
    file_handle := UTL_FILE.FOPEN( 'PL_SQL_DIR2' , file_name, 'w' );
-- 本線対応変更 end ABI

    -- ＣＳＶ作成
    FOR tenkai_rec IN SELECT_TENKAI LOOP
        UTL_FILE.PUT( file_handle,
          REPLACE(tenkai_rec.APKIHONCD, ',') -- ＡＰ基本コード
         || ',' || REPLACE(tenkai_rec.STSHNCD, ',') -- セット商品コード
         || ',' || REPLACE(tenkai_rec.RENBAN, ',') -- 連番
         || ',' || REPLACE(tenkai_rec.KOSEISHNAPKIHONCD, ',') -- 構成商品ＡＰ基本コード
         || ',' || REPLACE(tenkai_rec.KOSEISHNCD, ',') -- 構成商品コード
         || ',' || REPLACE(tenkai_rec.KEIJOKBN, ',') -- 計上区分
         || ',' || REPLACE(tenkai_rec.SURYO, ',') -- 数量
         || ',' || REPLACE(tenkai_rec.DLVRYPRICE, ',') -- 配達価格
         || ',' || REPLACE(tenkai_rec.MOCHIKAERIPRICE, ',') -- 持ち帰り価格
         || ',' || REPLACE(tenkai_rec.YUKOKBN, ',') -- 有効区分
         || ',' || REPLACE(tenkai_rec.KSHNSYSTMYMD, ',') -- 更新システム日付
         || ',' || REPLACE(tenkai_rec.KSHNSYSTMJIKAN, ',') -- 更新システム時間
         || chr(13) || chr(10) -- 改行：\r\n
      );
      UTL_FILE.FFLUSH(file_handle);
    END LOOP;
    UTL_FILE.FCLOSE(file_handle);

-- 本線対応追加 start ABI
    FILEUPLOADTOS3( file_path , file_name );
-- 本線対応追加 end ABI

    -- EXCEPTION：処理終了
    EXCEPTION
      WHEN OTHERS THEN
        IF SELECT_TENKAI%ISOPEN THEN
           CLOSE SELECT_TENKAI;
        END IF;
        UTL_FILE.FCLOSE_ALL;
        -- エラー
        RAISE_APPLICATION_ERROR(-20018, SQLERRM || '展開データＣＳＶ作成時エラーが発生しました。');

  END csvTenkai;


  /****************************************************************************/
  /* プロシージャ名 ：抽出ＣＳＶ圧縮＆暗号化処理                                    */
  /* 引数           ：iApkihoncd          ＡＰ基本コード                         */
  /*                ：iShopcd             ショップコード                         */
  /*                ：iTrmnlid            端末ID                               */
  /*                ：iOutputtrgtdd       出力対象日                            */
  /****************************************************************************/
  PROCEDURE createZip( iApkihoncd IN VARCHAR2 -- ＡＰ基本コード
                     , iShopcd    IN VARCHAR2 -- ショップコード
                     , iTrmnlid   IN VARCHAR2 -- 端末ID
                     , iOutputtrgtdd IN VARCHAR2 -- 出力対象日
  ) IS
  /* 変数宣言 */
-- 本線対応変更 start ABI
--  action_file     S02M006T.MOJISETTEIATAI%type := NULL; -- シェル
-- 本線対応変更 end ABI
  csv_path       S02M006T.MOJISETTEIATAI%type := NULL; -- CSV出力先フォルダ（パス）
  sys_file       S02M006T.MOJISETTEIATAI%type := NULL; -- システム管理マスタ.CSV
  zip_path       S02M006T.MOJISETTEIATAI%type := NULL; -- 圧縮フォルダ
  zip_password   S02M006T.MOJISETTEIATAI%type := NULL; -- 圧縮パスワード
  BEGIN

    /* システム管理マスタ設定値取得 */
-- 本線対応変更 start ABI
--    -- モバイル ZIP出力 SHELLファイル
--    action_file := getMojisetteiatai('000', '000', '036', '12', NULL, NULL, 'モバイルZIP出力SHELLファイル' );
-- 本線対応変更 end ABI
    -- CSV出力先フォルダ（パス）
    csv_path := getMojisetteiatai('000', '000', '036', '9', NULL, NULL, 'CSV出力先フォルダ（パス）' );
    -- システム管理マスタ.CSV
    sys_file := getMojisetteiatai('000', '000', '036', '11', NULL, NULL, 'システム管理マスタ.CSV' );
    -- 圧縮フォルダ
    zip_path := getMojisetteiatai('000', '000', '036', '10', NULL, NULL, '圧縮フォルダ' );
    -- 圧縮パスワード
    zip_password := getMojisetteiatai('000', '000', '227', '1', NULL, NULL, '圧縮パスワード' );

-- 本線対応変更 start ABI
--    -- 前回ジョプ削除
--    BEGIN
--      DBMS_SCHEDULER.drop_job( job_name => JOB_ZIP );
--      EXCEPTION
--        WHEN OTHERS THEN
--          NULL;
--    END;

--    -- ジョブ作成：
--    DBMS_SCHEDULER.create_job( job_name => JOB_ZIP
--                             , job_type => 'EXECUTABLE'
--                             , job_action => action_file
--                             , number_of_arguments => 7
--                             , auto_drop => TRUE );
--    -- パラメータ１：端末ＩＤ
--    DBMS_SCHEDULER.set_job_argument_value( job_name => JOB_ZIP
--                                         , argument_position => 1
--                                         , argument_value => iTrmnlid );
--    -- パラメータ２：処理日時
--    DBMS_SCHEDULER.set_job_argument_value( job_name => JOB_ZIP
--                                         , argument_position => 2
--                                         , argument_value => (userinfo_rec.SYSTMYMD || userinfo_rec.SYSTMJIKAN ) );
--    -- パラメータ３：出力対象日
--    DBMS_SCHEDULER.set_job_argument_value( job_name => JOB_ZIP
--                                         , argument_position => 3
--                                         , argument_value => iOutputtrgtdd );
--    -- パラメータ４：CSVファイルパス
--    DBMS_SCHEDULER.set_job_argument_value( job_name => JOB_ZIP
--                                         , argument_position => 4
--                                         , argument_value => csv_path );
--    -- パラメータ５：システムＣＳＶファイル
--    DBMS_SCHEDULER.set_job_argument_value( job_name => JOB_ZIP
--                                         , argument_position => 5
--                                         , argument_value => sys_file );
--    -- パラメータ６：ＺＩＰパス
--    DBMS_SCHEDULER.set_job_argument_value( job_name => JOB_ZIP
--                                         , argument_position => 6
--                                         , argument_value => zip_path );
--    -- パラメータ７：ＰＷ
--    DBMS_SCHEDULER.set_job_argument_value( job_name => JOB_ZIP
--                                         , argument_position => 7
--                                         , argument_value => zip_password );
--    -- ジョブ実行：
--    DBMS_SCHEDULER.run_job( job_name => JOB_ZIP );
    CREATEZIPS3(iTrmnlid                                            -- 端末ＩＤ
              , (userinfo_rec.SYSTMYMD || userinfo_rec.SYSTMJIKAN ) -- 処理日時
              , iOutputtrgtdd                                       -- 出力対象日
              , csv_path                                            -- CSVファイルパス
              , sys_file                                            -- システムＣＳＶファイル
              , zip_path                                            -- ＺＩＰパス
              , zip_password                                        -- ＰＷ
              );
-- 本線対応変更 end ABI

    -- EXCEPTION：処理終了
    EXCEPTION
      WHEN OTHERS THEN
        UTL_FILE.FCLOSE_ALL;
        -- エラー
        RAISE_APPLICATION_ERROR(-20019, SQLERRM || '抽出ＣＳＶ圧縮＆暗号化処理時エラーが発生しました。');

  END createZip;

  /****************************************************************************/
  /* プロシージャ名 ：端末別データ出力状況処理                                      */
  /* 引数           ：iApkihoncd          ＡＰ基本コード                         */
  /*                ：iShopcd             ショップコード                         */
  /*                ：iTrmnlid            端末ID                               */
  /*                ：iDlvrykyotencd      配達拠点コード                         */
  /*                ：iDlvrytnshacd       配達担当者コード                       */
  /*                ：iTnshacd            ログイン担当者コード                    */
  /****************************************************************************/
  PROCEDURE updateOutput( iApkihoncd IN VARCHAR2 -- ＡＰ基本コード
                        , iShopcd    IN VARCHAR2 -- ショップコード
                        , iTrmnlid   IN VARCHAR2 -- 端末ID
                        , iDlvrykyotencd  IN VARCHAR2 -- 配達拠点コード
                        , iDlvrytnshacd   IN VARCHAR2 -- 配達担当者コード
                        , iTnshacd        IN VARCHAR2 -- ログイン担当者コード
  ) IS
  /* 変数宣言 */
  BEGIN
    UPDATE S05T022T TOPT -- 端末別データ出力状況管理テーブル
       SET TOPT.ZNKOTPTTHDLVRYKYOTEN  = iDlvrykyotencd -- 配達拠点コード
         , TOPT.ZNKOTPTTHDLVRYTNSHA   = iDlvrytnshacd  -- 配達担当者コード
         , TOPT.SAISHUKSHNDDTH = userinfo_rec.SYSTMYMD || userinfo_rec.SYSTMJIKAN -- 最終更新日時
         , TOPT.YUKOKBN = '1' -- 有効区分
         /* 更新者 */
         , TOPT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD -- 更新者ＡＰ基本コード
         , TOPT.KOSHASHOPCD    = userinfo_rec.SHOPCD    -- 更新者ショップコード
         , TOPT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
         , TOPT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
         , TOPT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
         , TOPT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
         , TOPT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
         , TOPT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
     WHERE TOPT.APKIHONCD = iApkihoncd
       AND TOPT.SHOPCD    = iShopcd
       AND TOPT.TRMNLID   = iTrmnlid
    ;
    IF SQL%ROWCOUNT = 0 THEN
       INSERT INTO S05T022T (
              APKIHONCD -- ＡＰ基本コード
            , SHOPCD    -- ショップコード
            , TRMNLID   -- 端末ID
            , ZNKOTPTTHDLVRYKYOTEN -- 配達拠点コード
            , ZNKOTPTTHDLVRYTNSHA  -- 配達担当者コード
            , SAISHUKSHNDDTH -- 最終更新日時
            /* テーブル共通項目 */
            , YUKOKBN -- 有効区分
            , TOSHAAPKIHONCD -- 登録者ＡＰ基本コード
            , TOSHASHOPCD -- 登録者ショップコード
            , TOSHACD -- 登録者コード
            , INPTSYSTMYMD -- 登録システム日付
            , INPTSYSTMJIKAN -- 登録システム時間
            , INPTUNYOYMD -- 登録運用日付
            , INPTIPADRSE -- 登録ＩＰアドレス
            , INPTPGMID -- 登録プログラムＩＤ
            , KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
            , KOSHASHOPCD -- 更新者ショップコード
            , KOSHACD -- 更新者コード
            , KSHNSYSTMYMD -- 更新システム日付
            , KSHNSYSTMJIKAN -- 更新システム時間
            , KSHNUNYOYMD -- 更新運用日付
            , KSHNIPADRSE -- 更新ＩＰアドレス
            , KSHNPGMID -- 更新プログラムＩＤ
       )
       VALUES (
              iApkihoncd -- ＡＰ基本コード
            , iShopcd    -- ショップコード
            , iTrmnlid   -- 端末ID
            , iDlvrykyotencd  -- 配達拠点コード
            , iDlvrytnshacd   -- 配達担当者コード
            , userinfo_rec.SYSTMYMD || userinfo_rec.SYSTMJIKAN -- 最終更新日時
            /* テーブル共通項目 */
            , '1' -- 有効区分
            , userinfo_rec.APKIHONCD -- 登録者ＡＰ基本コード
            , userinfo_rec.SHOPCD    -- 登録者ショップコード
            , userinfo_rec.TNSHACD    -- 登録者コード
            , userinfo_rec.SYSTMYMD   -- 登録システム日付
            , userinfo_rec.SYSTMJIKAN -- 登録システム時間
            , userinfo_rec.UNYOYMD    -- 登録運用日付
            , userinfo_rec.IPADRSE    -- 登録ＩＰアドレス
            , userinfo_rec.PGMID      -- 登録プログラムＩＤ
            , userinfo_rec.APKIHONCD -- 更新者ＡＰ基本コード
            , userinfo_rec.SHOPCD    -- 更新者ショップコード
            , userinfo_rec.TNSHACD    -- 更新者コード
            , userinfo_rec.SYSTMYMD   -- 更新システム日付
            , userinfo_rec.SYSTMJIKAN -- 更新システム時間
            , userinfo_rec.UNYOYMD    -- 更新運用日付
            , userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
            , userinfo_rec.PGMID      -- 更新プログラムＩＤ
       );
    END IF;

    -- EXCEPTION：処理終了
    EXCEPTION
      WHEN OTHERS THEN
        -- エラー
        RAISE_APPLICATION_ERROR(-20020, SQLERRM || '担当者別出力状況更新時エラーが発生しました。');

  END updateOutput;

  /****************************************************************************/
  /* プロシージャ名 ：モバイルシステム向けデータ出力                                 */
  /* 引数           ：iApkihoncd          ＡＰ基本コード                         */
  /*                ：iShopcd             ショップコード                         */
  /*                ：iTrmnlid            端末ID                               */
  /*                ：iDlvrykyotencd      配達拠点コード                         */
  /*                ：iDlvryytidd         配達予定日                            */
  /*                ：iDlvrytnshacd       配達担当者コード                       */
  /*                ：iOutputkbn          出力区分                              */
  /*                   （0：一括：1：差分）                                      */
  /*                ：iOutputtrgtkbn      出力対象区分                          */
  /*                   （0：配達予定のみ；1：集金のみ；2：全て）                    */
  /*                ：iOutputtrgtdd       出力対象日                            */
  /*                ：iTairyukbn          滞留区分                              */
  /*                   （1：現金回収のみ；2：滞留全て；3：２ヶ月滞留以上；4：長期滞留）*/
  /*                ：iTnshacd            ログイン担当者コード                    */
  /*                ：iIpaddress          ＩＰアドレス                          */
  /****************************************************************************/
  PROCEDURE MobileDataOutput( iApkihoncd IN VARCHAR2 -- ＡＰ基本コード
                            , iShopcd    IN VARCHAR2 -- ショップコード
                            , iTrmnlid   IN VARCHAR2 -- 端末ID
                            , iDlvrykyotencd  IN VARCHAR2 -- 配達拠点コード
                            , iDlvryytidd     IN VARCHAR2 -- 配達予定日
                            , iDlvrytnshacd   IN VARCHAR2 -- 配達担当者コード
                            , iOutputkbn      IN VARCHAR2 -- 出力区分
                            , iOutputtrgtkbn  IN VARCHAR2 -- 出力対象区分
                            , iOutputtrgtdd   IN VARCHAR2 -- 出力対象日
                            , iTairyukbn      IN VARCHAR2 -- 滞留区分
                            , iTnshacd        IN VARCHAR2 -- ログイン担当者コード
                            , iIpaddress      IN VARCHAR2 -- ＩＰアドレス
  ) IS
  /* ファイル取込用変数 */
  file_path VARCHAR2(150) := NULL; -- ファイル出力先パス
  BEGIN
    -- 入力パラメータチェック
    IF iApkihoncd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20001,
                              'パラメータ.ＡＰ基本コード が設定されていません。');
    END IF;
    IF iShopcd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20002,
                              'パラメータ.ショップコード が設定されていません。');
    END IF;
    IF iTrmnlid IS NULL THEN
      RAISE_APPLICATION_ERROR(-20003,
                              'パラメータ.端末ID が設定されていません。');
    END IF;
    IF iDlvrykyotencd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20004,
                              'パラメータ.配達拠点コード  が設定されていません。');
    END IF;
    IF iDlvryytidd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20005,
                              'パラメータ.配達予定日  が設定されていません。');
    END IF;
    IF iDlvrytnshacd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20006,
                              'パラメータ.配達担当者コード　が設定されていません。');
    END IF;
    IF iOutputkbn IS NULL THEN
      RAISE_APPLICATION_ERROR(-20007,
                              'パラメータ.出力区分　が設定されていません。');
    END IF;
    IF iOutputtrgtkbn IS NULL THEN
      RAISE_APPLICATION_ERROR(-20008,
                              'パラメータ.出力対象区分 が設定されていません。');
    END IF;
    IF iOutputtrgtdd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20009,
                              'パラメータ.出力対象日 が設定されていません。');
    END IF;
    IF iTairyukbn IS NULL THEN
      RAISE_APPLICATION_ERROR(-20010,
                              'パラメータ.滞留区分 が設定されていません。');
    END IF;
    IF iTnshacd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20011,
                              'パラメータ.ログイン担当者コード　が設定されていません。');
    END IF;
    IF iIpaddress IS NULL THEN
      RAISE_APPLICATION_ERROR(-20024,
                              'パラメータ．ログイン担当者ＩＰアドレス　が設定されていません。');
    END IF;

    -- テーブル共通基本情報設定
    setUserInfo(iApkihoncd, iShopcd, iTnshacd, iIpaddress, 'F0801B01');

    -- 取込ファイルパスの取得
-- 本線対応変更 start ABI
    -- S3ではディレクトリオブジェクトを参照できないため、直フォルダパスを取得
    --file_path := getMojisetteiatai('000', '000', '036', '13', NULL, NULL, 'CSV出力先フォルダ' );
    file_path := getMojisetteiatai('000', '000', '036', '9', NULL, NULL, 'CSV出力先フォルダ' );
-- 本線対応変更 end ABI

    -- 集金データ出力：1：集金のみ、または、2：全て の場合
    S99_CHECK_LOG('F0801B01.SYUKIN', iApkihoncd, iShopcd, '1', 'S', '1', 'XXX', 0, iApkihoncd, iShopcd, iTnshacd, iIpaddress, 'F0801B01' , '集金データ出力[開始]' );
    csvSyukin( iApkihoncd, iShopcd, iTrmnlid, iDlvryytidd, iDlvrytnshacd, iTairyukbn, iOutputtrgtdd, iOutputtrgtkbn, file_path );
    S99_CHECK_LOG('F0801B01.SYUKIN', iApkihoncd, iShopcd, '1', 'E', '1', 'XXX', 0, iApkihoncd, iShopcd, iTnshacd, iIpaddress, 'F0801B01' , '集金データ出力[終了]' );

    -- 商品データ出力
    S99_CHECK_LOG('F0801B01.SHOHIN', iApkihoncd, iShopcd, '1', 'S', '1', 'XXX', 0, iApkihoncd, iShopcd, iTnshacd, iIpaddress, 'F0801B01' , '商品データ出力[開始]' );
    csvShohin( iApkihoncd, iShopcd, iTrmnlid, iOutputkbn, iOutputtrgtdd, file_path );
    S99_CHECK_LOG('F0801B01.SHOHIN', iApkihoncd, iShopcd, '1', 'E', '1', 'XXX', 0, iApkihoncd, iShopcd, iTnshacd, iIpaddress, 'F0801B01' , '商品データ出力[終了]' );

    -- 得意先データ出力
    S99_CHECK_LOG('F0801B01.TOKUISAKI', iApkihoncd, iShopcd, '1', 'S', '1', 'XXX', 0, iApkihoncd, iShopcd, iTnshacd, iIpaddress, 'F0801B01' , '得意先データ出力[開始]' );
    csvTokuisaki( iApkihoncd, iShopcd, iTrmnlid, iOutputkbn, iOutputtrgtdd, file_path );
    S99_CHECK_LOG('F0801B01.TOKUISAKI', iApkihoncd, iShopcd, '1', 'E', '1', 'XXX', 0, iApkihoncd, iShopcd, iTnshacd, iIpaddress, 'F0801B01' , '得意先データ出力[終了]' );

    -- 請求先データ出力
    S99_CHECK_LOG('F0801B01.SKYSAKI', iApkihoncd, iShopcd, '1', 'S', '1', 'XXX', 0, iApkihoncd, iShopcd, iTnshacd, iIpaddress, 'F0801B01' , '請求先データ出力[開始]' );
    csvSkysaki( iApkihoncd, iShopcd, iTrmnlid, iOutputkbn, iOutputtrgtdd, file_path );
    S99_CHECK_LOG('F0801B01.SKYSAKI', iApkihoncd, iShopcd, '1', 'E', '1', 'XXX', 0, iApkihoncd, iShopcd, iTnshacd, iIpaddress, 'F0801B01' , '請求先データ出力[終了]' );

    -- 得意先別商品別単価データ出力
    S99_CHECK_LOG('F0801B01.TANKA', iApkihoncd, iShopcd, '1', 'S', '1', 'XXX', 0, iApkihoncd, iShopcd, iTnshacd, iIpaddress, 'F0801B01' , '得意先別商品別単価データ出力[開始]' );
    csvTanka( iApkihoncd, iShopcd, iTrmnlid, iOutputkbn, iOutputtrgtdd, file_path );
    S99_CHECK_LOG('F0801B01.TANKA', iApkihoncd, iShopcd, '1', 'E', '1', 'XXX', 0, iApkihoncd, iShopcd, iTnshacd, iIpaddress, 'F0801B01' , '得意先別商品別単価データ出力[終了]' );

    -- ＡＰ／ショップデータ出力
    S99_CHECK_LOG('F0801B01.APSHOP', iApkihoncd, iShopcd, '1', 'S', '1', 'XXX', 0, iApkihoncd, iShopcd, iTnshacd, iIpaddress, 'F0801B01' , 'ＡＰ／ショップ出力[開始]' );
    csvApshop( iApkihoncd, iShopcd, iTrmnlid, iOutputkbn, iOutputtrgtdd, file_path );
    S99_CHECK_LOG('F0801B01.APSHOP', iApkihoncd, iShopcd, '1', 'E', '1', 'XXX', 0, iApkihoncd, iShopcd, iTnshacd, iIpaddress, 'F0801B01' , 'ＡＰ／ショップデータ出力[終了]' );

    -- 担当者データ出力
    S99_CHECK_LOG('F0801B01.TNSHA', iApkihoncd, iShopcd, '1', 'S', '1', 'XXX', 0, iApkihoncd, iShopcd, iTnshacd, iIpaddress, 'F0801B01' , '担当者データ出力[開始]' );
    csvTnsha( iApkihoncd, iShopcd, iTrmnlid, iOutputkbn, iOutputtrgtdd, file_path );
    S99_CHECK_LOG('F0801B01.TNSHA', iApkihoncd, iShopcd, '1', 'E', '1', 'XXX', 0, iApkihoncd, iShopcd, iTnshacd, iIpaddress, 'F0801B01' , '担当者データ出力[終了]' );

    -- 展開データ出力
    S99_CHECK_LOG('F0801B01.TENKAI', iApkihoncd, iShopcd, '1', 'S', '1', 'XXX', 0, iApkihoncd, iShopcd, iTnshacd, iIpaddress, 'F0801B01' , '展開データ出力[開始]' );
    csvTenkai( iApkihoncd, iShopcd, iTrmnlid, iOutputkbn, iOutputtrgtdd, file_path );
    S99_CHECK_LOG('F0801B01.TENKAI', iApkihoncd, iShopcd, '1', 'E', '1', 'XXX', 0, iApkihoncd, iShopcd, iTnshacd, iIpaddress, 'F0801B01' , '展開データ出力[終了]' );

    -- 配達予定データ出力：0：配達予定のみ、または、2：全て の場合
    S99_CHECK_LOG('F0801B01.DLVRY', iApkihoncd, iShopcd, '1', 'S', '1', 'XXX', 0, iApkihoncd, iShopcd, iTnshacd, iIpaddress, 'F0801B01' , '配達予定データ出力[開始]' );
    csvDlvryyti( iApkihoncd, iShopcd, iTrmnlid, iDlvrykyotencd, iDlvryytidd, iDlvrytnshacd, iOutputtrgtdd, iOutputtrgtkbn, file_path );
    S99_CHECK_LOG('F0801B01.DLVRY', iApkihoncd, iShopcd, '1', 'E', '1', 'XXX', 0, iApkihoncd, iShopcd, iTnshacd, iIpaddress, 'F0801B01' , '配達予定データ出力[終了]' );

-- インボイス対応追加 start ABI
    -- 売上データ出力
    S99_CHECK_LOG('F0801B01.Uriage', iApkihoncd, iShopcd, '1', 'S', '1', 'XXX', 0, iApkihoncd, iShopcd, iTnshacd, iIpaddress, 'F0801B01' , '売上データ出力[開始]' );
    csvUriage( iApkihoncd, iShopcd, iTrmnlid, iDlvrykyotencd, iDlvryytidd, iDlvrytnshacd, iOutputtrgtdd, iOutputtrgtkbn, file_path );
    S99_CHECK_LOG('F0801B01.Uriage', iApkihoncd, iShopcd, '1', 'E', '1', 'XXX', 0, iApkihoncd, iShopcd, iTnshacd, iIpaddress, 'F0801B01' , '売上データ出力[終了]' );
-- インボイス対応追加 end ABI

    -- 抽出ＣＳＶ圧縮＆暗号化処理
    S99_CHECK_LOG('F0801B01.ZIP', iApkihoncd, iShopcd, '1', 'S', '1', 'XXX', 0, iApkihoncd, iShopcd, iTnshacd, iIpaddress, 'F0801B01' , '抽出ＣＳＶ圧縮＆暗号化処理[開始]' );
    createZip( iApkihoncd, iShopcd, iTrmnlid, iOutputtrgtdd );
    S99_CHECK_LOG('F0801B01.ZIP', iApkihoncd, iShopcd, '1', 'E', '1', 'XXX', 0, iApkihoncd, iShopcd, iTnshacd, iIpaddress, 'F0801B01' , '抽出ＣＳＶ圧縮＆暗号化処理[終了]' );

    -- 担当者別出力状況更新処理
    S99_CHECK_LOG('F0801B01.UPDATE', iApkihoncd, iShopcd, '1', 'S', '1', 'XXX', 0, iApkihoncd, iShopcd, iTnshacd, iIpaddress, 'F0801B01' , '担当者別出力状況更新処理[開始]' );
    updateOutput( iApkihoncd, iShopcd, iTrmnlid, iDlvrykyotencd, iDlvrytnshacd, iTnshacd );
    S99_CHECK_LOG('F0801B01.UPDATE', iApkihoncd, iShopcd, '1', 'E', '1', 'XXX', 0, iApkihoncd, iShopcd, iTnshacd, iIpaddress, 'F0801B01' , '担当者別出力状況更新処理[終了]' );

    COMMIT;
    -- EXCEPTION：処理終了
    EXCEPTION
      WHEN OTHERS THEN
        ROLLBACK;
        -- カーソル オーペンであれば、クローズする
        -- 共通変数削除
        userinfo_rec := NULL;
        -- エラー
        RAISE_APPLICATION_ERROR(-20705, SQLERRM);

  END MobileDataOutput;

END F0801B01;
/
exit;
