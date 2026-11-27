create or replace PACKAGE BODY       C0815B11
/*******************************************************************************
 * システム名       ：ＡＰ総合管理システム
 * 機能名           ：ＡＰ受注売上データ連携
 *
 ******************************************************************************T
   Date       Author         Changes Key                                 Version
   -----------------------------------------------------------------------------
   2022-05-12 ABI)Yokota         Create                                    1.0.0
 ******************************************************************************/
 AS 
 
  /****************************************************************************/
  /* 共通変数宣言                                                               */
  /****************************************************************************/
  /* レコード定義 */
  -- 登録者・更新者基本情報：定義
  TYPE userinfo_rec_type IS RECORD (
                         APKIHONCD  VARCHAR2(3) -- 更新者AP基本コード
                       , SHOPCD     VARCHAR2(3) -- 更新者ショップコード
                       , TNSHACD    VARCHAR2(6) -- 更新者コード
                       , SYSTMYMD   VARCHAR2(8) -- システム日付
                       , SYSTMJIKAN VARCHAR2(6) -- システム時間
                       , UNYOYMD    VARCHAR2(8) -- 運用日付
                       , IPADRSE    VARCHAR2(15)-- 更新ＩＰアドレス
                       , PGMID      VARCHAR2(8) -- 更新プログラムＩＤ
  ); 
  -- 委託データ
  TYPE itaku_rec_type IS RECORD ( 
                    -- 受託配達テーブル
                      APKIHONCD   S04T001T.APKIHONCD%TYPE  -- ＡＰ基本コード
                    , SHOPCD      S04T001T.SHOPCD%TYPE     -- ショップコード
                    , ACPTDNPYNO  S04T001T.ACPTDNPYNO%TYPE -- 受注伝票番号
                    , EDANO       S04T001T.EDANO%TYPE      -- 枝番号
                    , TOKUISAKICD  S04T001T.TOKUISAKICD%TYPE  -- 得意先コード
                    , TAXKEISANKBN S04T001T.TAXKEISANKBN%TYPE -- 税計算区分
                    , DLVRYTRGTFLG S04T001T.DLVRYTRGTFLG%TYPE -- 配達対象フラグ
                    , DNPYKBN      S04T001T.DNPYKBN%TYPE      -- 伝票区分
                    , TORIHIKIKBN  S04T001T.TORIHIKIKBN%TYPE  -- 取引区分
                    , ACPTKBN      S04T001T.ACPTKBN%TYPE      -- 受注区分
                    , ACPTKBNSHS   S04T001T.ACPTKBNSHS%TYPE   -- 受注区分詳細
                    , ACPTDD       S04T001T.ACPTDD%TYPE       -- 受注日
                    , ACPTTNSHAAPKIHONCD S04T001T.ACPTTNSHAAPKIHONCD%TYPE -- 受注担当者ＡＰ基本コード
                    , ACPTTNSHASHOPCD    S04T001T.ACPTTNSHASHOPCD%TYPE    -- 受注担当者ショップコード
                    , ACPTTNSHACD        S04T001T.ACPTTNSHACD%TYPE        -- 受注担当者コード
                    , DLVRYKANKATSUAPKIHONCD S04T001T.DLVRYKANKATSUAPKIHONCD%TYPE -- 配達管轄ＡＰ基本コード
                    , DLVRYKANKATSUSHOPCD    S04T001T.DLVRYKANKATSUSHOPCD%TYPE    -- 配達管轄ショップコード
                    , DLVRYKYOTENCD          S04T001T.DLVRYKYOTENCD%TYPE          -- 配達拠点コード
                    , DLVRYAREACD              S04T001T.DLVRYAREACD%TYPE            -- 配達エリアコード
                    , DLVRYKBN             S04T001T.DLVRYKBN%TYPE                 -- 配達区分
                    , DLVRYCMPNYCD    S04T001T.DLVRYCMPNYCD%TYPE -- 配達会社コード
                    , DLVRYJUN        S04T001T.DLVRYJUN%TYPE     -- 配達順
                    , DLVRYTNSHACD    S04T001T.DLVRYTNSHACD%TYPE -- 配達担当者コード
                    , ACPTKIN         S04T001T.ACPTKIN%TYPE      -- 受注金額
                    , ACPTSHOHITAXKIN S04T001T.ACPTSHOHITAXKIN%TYPE -- 受注消費税金額
                    , PRNTMEMON1      S04T001T.PRNTMEMON1%TYPE -- 印刷メモ１
                    , PRNTMEMON2      S04T001T.PRNTMEMON2%TYPE -- 印刷メモ２
                    , PRNTMEMON3      S04T001T.PRNTMEMON3%TYPE -- 印刷メモ３
                    , DATSKSMOTOKBN   S04T001T.DATSKSMOTOKBN%TYPE  -- データ作成元区分
                    -- 得意先マスタ
                    , STARTUP          S02M013T.STARTUP%TYPE -- スタートアップ
                    , TOKUISAKIJOTAI   S02M013T.TOKUISAKIJOTAI%TYPE -- 得意先状態
                    , SHOKAIWSVSTUPDD  S02M013T.SHOKAIWSVSTUPDD%TYPE -- 初回サーバー設置日
                    , EIGYOTNSHACD     S02M013T.EIGYOTNSHACD%TYPE -- 営業担当者コード
                    , URIKEIJOTNSHAKBN S02M013T.URIKEIJOTNSHAKBN%TYPE -- 売上計上担当者区分
                    , SKYSAKICD        S02M013T.SKYSAKICD%TYPE -- 請求先コード
                    -- 請求先マスタ
                    , KAISHUHOHOKBN S02M017T.KAISHUHOHOKBN%TYPE -- 回収方法区分
                    -- 得意先実績テーブル 
                    , AZUKARIBTLN1N2RITTORUHNS S02M050T.AZUKARIBTLN1N2RITTORUHNS%TYPE -- 預かりボトル１２Ｌ本数
                    , AZUKARIBTLN8RITTORUHNS   S02M050T.AZUKARIBTLN8RITTORUHNS%TYPE -- 預かりボトル８Ｌ本数
                    , RENKEITNSHACD VARCHAR2(6) -- 受託配達データ連携担当者コード
-- インボイス対応追加 start ABI
                    , TOKUISAKIKBN      S02M013T.TOKUISAKIKBN%TYPE      -- 得意先区分
                    , JUTAKUDLVRYCSTKBN S02M013T.JUTAKUDLVRYCSTKBN%TYPE -- 受託配達顧客区分
                    , ACPTTHDAIBIKIFLG  S04T001T.ACPTTHDAIBIKIFLG%TYPE  -- 受注時代引きフラグ
                    , SINGLDNPYFLG      S04T001T.SINGLDNPYFLG%TYPE      -- 単体伝票締めフラグ
                    , DLVRYYTIDD        S04T001T.DLVRYYTIDD%TYPE        -- 配達予定日
-- インボイス対応追加 end ABI
  ); 
  -- 受託データ
  TYPE jutaku_rec_type IS RECORD ( 
                      APKIHONCD  S04T001T.APKIHONCD%TYPE  -- ＡＰ基本コード
                    , SHOPCD     S04T001T.SHOPCD%TYPE     -- ショップコード
                    , ACPTDNPYNO S04T001T.ACPTDNPYNO%TYPE -- 受注伝票番号
                    , EDANO      S04T001T.EDANO%TYPE      -- 枝番号
                    , DLVRYKBN            S04T001T.DLVRYKBN%TYPE -- 配達区分
                    , DLVRYSTS            S04T001T.DLVRYSTS%TYPE -- 配達ステータス
                    , KZBTLN1N2RITTORUHNS S04T001T.KZBTLN1N2RITTORUHNS%TYPE -- 回収実績ボトル１２Ｌ本数
                    , KZBTLN8RITTORUHNS   S04T001T.KZBTLN8RITTORUHNS%TYPE   -- 回収実績ボトル８Ｌ本数
                    , DNPYSTS             S04T001T.DNPYSTS%TYPE         -- 伝票ステータス
                    , DLVRYDD             S04T001T.DLVRYDD%TYPE         -- 配達日
                    , DLVRYYTIDD          S04T001T.DLVRYYTIDD%TYPE      -- 配達予定日
                    , DLVRYJISSEKIDD      S04T001T.DLVRYJISSEKIDD%TYPE  -- 配達実績日
                    , ACPTDD              S04T001T.ACPTDD%TYPE          -- 受注日
                    , JIKAIDLVRYYTIDD     S04T001T.JIKAIDLVRYYTIDD%TYPE -- 次回配達予定日
                    , DLVRYKIBOJIKAN      S04T001T.DLVRYKIBOJIKAN%TYPE     -- 配達希望時間
                    , DLVRYKIBOJIKANSHRY  S04T001T.DLVRYKIBOJIKANSHRY%TYPE -- 配達希望時間終了
                    , DLVRYKIBOJIKANKBN   S04T001T.DLVRYKIBOJIKANKBN%TYPE  -- 配達希望時間区分
                    , FZAIOKIKANOFLG      S04T001T.FZAIOKIKANOFLG%TYPE     -- 不在置き可能フラグ
                    , KYBTLN1N2RITTORUHNS     S04T001T.KYBTLN1N2RITTORUHNS%TYPE -- 回収予定ボトル１２Ｌ本数
                    , KYBTLN8RITTORUHNS       S04T001T.KYBTLN8RITTORUHNS%TYPE   -- 回収予定ボトル８Ｌ本数
                    , ACPTSHIMEPRCSDD         S04T001T.ACPTSHIMEPRCSDD%TYPE         -- 受注締処理日
                    , ACPTSHIMETNSHAAPKIHONCD S04T001T.ACPTSHIMETNSHAAPKIHONCD%TYPE -- 受注締担当者ＡＰ基本コード
                    , ACPTSHIMETNSHASHOPCD    S04T001T.ACPTSHIMETNSHASHOPCD%TYPE    -- 受注締担当者ショップコード
                    , ACPTSHIMEPRCSTNSHACD    S04T001T.ACPTSHIMEPRCSTNSHACD%TYPE    -- 受注締処理担当者コード
                    , NOSHOPRNTTRGTFLG        S04T001T.NOSHOPRNTTRGTFLG%TYPE -- 納品書印刷対象フラグ
                    , NOSHOPRNTSUMIFLG        S04T001T.NOSHOPRNTSUMIFLG%TYPE -- 納品書印刷済フラグ
                    , NOSHOPRNTDD             S04T001T.NOSHOPRNTDD%TYPE      -- 納品書印刷日
                    , NOSHOHIKAEPRNTTRGTFLG   S04T001T.NOSHOHIKAEPRNTTRGTFLG%TYPE -- 納品書控印刷対象フラグ
                    , NOSHOHIKAEPRNTSUMIFLG   S04T001T.NOSHOHIKAEPRNTSUMIFLG%TYPE -- 納品書控印刷済フラグ
                    , NOSHOHIKAEPRNTDD        S04T001T.NOSHOHIKAEPRNTDD%TYPE      -- 納品書控印刷日
                    , NYKNHYOPRNTTRGTFLG      S04T001T.NYKNHYOPRNTTRGTFLG%TYPE -- 入金票印刷対象フラグ
                    , NYKNHYOPRNTSUMIFLG      S04T001T.NYKNHYOPRNTSUMIFLG%TYPE -- 入金票印刷済フラグ
                    , NYKNHYOPRNTDD           S04T001T.NYKNHYOPRNTDD%TYPE      -- 入金票印刷日
                    , WSVSIJISHOPRNTTRGTFLG   S04T001T.WSVSIJISHOPRNTTRGTFLG%TYPE -- サーバー指示書印刷対象フラグ
                    , WSVSIJISHOPRNTSUMIFLG   S04T001T.WSVSIJISHOPRNTSUMIFLG%TYPE -- サーバー指示書印刷済フラグ
                    , WSVSIJISHOPRNTDD        S04T001T.WSVSIJISHOPRNTDD%TYPE      -- サーバー指示書印刷日
                    , SKSMOTOKANRIKBN S04T001T.SKSMOTOKANRIKBN%TYPE -- 作成元管理区分
                    , SKSMOTOKANRINO  S04T001T.SKSMOTOKANRINO%TYPE  -- 作成元管理番号
                    , ACPTHIKIATESAKIAPKIHONCD S04T001T.ACPTHIKIATESAKIAPKIHONCD%TYPE -- 受注引当先ＡＰ基本コード
                    , ACPTHIKIATESAKISHOPCD    S04T001T.ACPTHIKIATESAKISHOPCD%TYPE    -- 受注引当先ショップコード
                    , ACPTHIKIATESAKIKYOTENCD  S04T001T.ACPTHIKIATESAKIKYOTENCD%TYPE  -- 受注引当先拠点コード
                    , KURERURITTORUSTUP        S04T001T.KURERURITTORUSTUP%TYPE    -- クレールＬ設置
                    , KURERURITTORUHIKIAGE     S04T001T.KURERURITTORUHIKIAGE%TYPE -- クレールＬ引上
                    , KURERUESUSTUP            S04T001T.KURERUESUSTUP%TYPE        -- クレールＳ設置
                    , KURERUESUHIKIAGE         S04T001T.KURERUESUHIKIAGE%TYPE     -- クレールＳ引上
                    , SONOTASTUP               S04T001T.SONOTASTUP%TYPE           -- その他設置
                    , SONOTAHIKIAGE            S04T001T.SONOTAHIKIAGE%TYPE        -- その他引上
                    , ZNKDLVRYDD_JHTT  S04T001T.ZNKDLVRYDD%TYPE       -- 前回配達日
                    , ZNKDLVRYHNS      S04T001T.ZNKDLVRYHNS%TYPE      -- 前回配達本数
                    , ZNKSHIMEDD       S04T001T.ZNKSHIMEDD%TYPE       -- 前回締日
                    , ZNKKURIKOSHIKIN  S04T001T.ZNKKURIKOSHIKIN%TYPE  -- 前回繰越金額
                    , ACPTTHDAIBIKIFLG S04T001T.ACPTTHDAIBIKIFLG%TYPE -- 受注時代引フラグ
                    , ACPTRCPTKEIROKBN S04T001T.ACPTRCPTKEIROKBN%TYPE -- 受注受付経路区分
                    , YUKOKBN          S04T001T.YUKOKBN%TYPE -- 有効区分
  ); 
  -- 合計用データ：定義
  TYPE sum_rec_type IS RECORD ( 
                     -- 製品水
                       DLVRYRGRAYUSYOHNS S04T001T.DLVRYJISSEKIRGRAYUSYOHNS%TYPE -- 配達レギュラー有償本数
                     , DLVRYRGRAMUSHOHNS S04T001T.DLVRYJISSEKIRGRAMUSHOHNS%TYPE -- 配達レギュラー無償本数
                     , DLVRYSRMYUSYOHNS  S04T001T.DLVRYJISSEKISRMYUSYOHNS%TYPE  -- 配達スリム有償本数
                     , DLVRYSRMMUSHOHNS  S04T001T.DLVRYJISSEKISRMMUSHOHNS%TYPE  -- 配達スリム無償本数
                     , DLVRYSTYSHNS      S04T001T.DLVRYJISSEKISTYSHNS%TYPE      -- 配達その他有償本数
                     , DLVRYSTMSHNS      S04T001T.DLVRYJISSEKISTMSHNS%TYPE      -- 配達その他無償本数
                     , DLVRYSHSUAKIN     S04T001T.DLVRYJISSEKISHSUAKIN%TYPE     -- 配達製品水売上金額
                     , DLVRYSONOTAURIKIN S04T001T.DLVRYJISSEKISONOTAURIKIN%TYPE -- 配達その他売上金額
                     -- サーバー
                     , KURERURITTORUSTUP    S04T001T.KURERURITTORUSTUP%TYPE    -- クレールＬ設置
                     , KURERURITTORUHIKIAGE S04T001T.KURERURITTORUHIKIAGE%TYPE -- クレールＬ引上
                     , KURERUESUSTUP        S04T001T.KURERUESUSTUP%TYPE        -- クレールＳ設置
                     , KURERUESUHIKIAGE     S04T001T.KURERUESUHIKIAGE%TYPE     -- クレールＳ引上
                     , SONOTASTUP           S04T001T.SONOTASTUP%TYPE           -- その他設置
                     , SONOTAHIKIAGE        S04T001T.SONOTAHIKIAGE%TYPE        -- その他引上
                     -- 金額、消費税
                     , ACPTKIN          S04T001T.ACPTKIN%TYPE           -- 受注金額 
                     , ACPTSHOHITAXKIN  S04T001T.ACPTSHOHITAXKIN%TYPE   -- 受注消費税金額 
-- インボイス対応追加 start ABI
                     , ACPTIPPANNUKIKIN S04T001T.ACPTIPPANNUKIKIN%TYPE       -- 受注一般税抜金額
                     , ACPTIPPANTAXKIN S04T001T.ACPTIPPANTAXKIN%TYPE         -- 受注一般消費税金額
                     , ACPTKEIGENNUKIKIN S04T001T.ACPTKEIGENNUKIKIN%TYPE     -- 受注軽減税抜金額
                     , ACPTKEIGENTAXKIN S04T001T.ACPTKEIGENTAXKIN%TYPE       -- 受注軽減消費税金額
                     , ACPTHIKAZEIURIIKIN S04T001T.ACPTHIKAZEIURIIKIN%TYPE   -- 受注非課税金額
                     , ACPTFUKAZEIURIIKIN S04T001T.ACPTFUKAZEIURIIKIN%TYPE   -- 受注不課税売上金額
-- インボイス対応追加 end ABI
                     , NOHINKIN         S04T001T.NOHINKIN%TYPE          -- 納品金額 
                     , NOHINSHOHITAXKIN S04T001T.NOHINSHOHITAXKIN%TYPE  -- 納品消費税金額 
-- インボイス対応追加 start ABI
                     , NOHINIPPANNUKIKIN S04T001T.NOHINIPPANNUKIKIN%TYPE     -- 納品一般税抜金額
                     , NOHINIPPANTAXKIN S04T001T.NOHINIPPANTAXKIN%TYPE       -- 納品一般消費税金額
                     , NOHINKEIGENNUKIKIN S04T001T.NOHINKEIGENNUKIKIN%TYPE   -- 納品軽減税抜金額
                     , NOHINKEIGENTAXKIN S04T001T.NOHINKEIGENTAXKIN%TYPE     -- 納品軽減消費税金額
                     , NOHINHIKAZEIURIIKIN S04T001T.NOHINHIKAZEIURIIKIN%TYPE -- 納品非課税金額
                     , NOHINFUKAZEIURIIKIN S04T001T.NOHINFUKAZEIURIIKIN%TYPE -- 納品不課税売上金額
-- インボイス対応追加 end ABI
                     , RGRASURYO S04T001T.DLVRYJISSEKIRGRAYUSYOHNS%TYPE -- 配達レギュラー本数 
                     , SRMSURYO  S04T001T.DLVRYJISSEKISRMYUSYOHNS%TYPE  -- 配達スリム本数 
  );   
  /* 共通変数 */
  userinfo_rec userinfo_rec_type; -- 登録者・更新者基本情報
  sum_rec      sum_rec_type;      -- 合計用データ
  -- ポイント制度対応追加 start ABI
  cmnRegistKanrino        VARCHAR2(15);              -- 登録した予定発生ポイントの管理番号
  TYPE                    S20W003TList IS TABLE OF S20W003T%ROWTYPE INDEX BY PLS_INTEGER;
  -- ポイント制度対応追加 end ABI

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
  /* ファンクション名 ：数値設定値取得                                             */
  /* 引数             ：iApKihoncd            ＡＰ基本コード                     */
  /*                  ：iShopcd               ショップコード                     */
  /*                  ：iSetteino             設定番号                          */
  /*                  ：iSetteinm             設定名称                          */
  /* return           ：returnSuchisetteiata  数値設定値                        */
  /****************************************************************************/
  FUNCTION getSuchisetteiatai( iApKihoncd     IN VARCHAR2
                             , iShopcd        IN VARCHAR2
                             , iSetteino      IN VARCHAR2
                             , iSetteinm      IN VARCHAR2
  ) RETURN VARCHAR2 IS returnSuchisetteiatai S02M006T.SUCHISETTEIATAI%type;
    /* システム管理マスタから文字設定値の取得 */
    CURSOR SELECT_ATAI IS
      SELECT SYKM.SUCHISETTEIATAI
        FROM S02M006T SYKM
       WHERE SYKM.APKIHONCD = iApKihoncd
         AND SYKM.SHOPCD    = iShopcd
         AND SYKM.SETTEINO  = iSetteino
         AND SYKM.RCRDKBN = 'M'
         AND SYKM.YUKOKBN = '1'
    ;
  
  BEGIN 
    returnSuchisetteiatai := 0; 
    OPEN SELECT_ATAI;
    LOOP FETCH SELECT_ATAI INTO returnSuchisetteiatai;  
      -- 設定されていないチェック
      IF SELECT_ATAI%ROWCOUNT = 0 THEN
         returnSuchisetteiatai := 0; 
      END IF; 
      EXIT WHEN SELECT_ATAI%NOTFOUND;
      
      -- 複数件チェック
      IF SELECT_ATAI%ROWCOUNT > 1 THEN
         RAISE_APPLICATION_ERROR(-20022, iSetteinm||'が複数見つかりました。');
      END IF;
      
    END LOOP;
    CLOSE SELECT_ATAI;
    
    RETURN(returnSuchisetteiatai);
  END getSuchisetteiatai;
  
  /****************************************************************************/
  /* ファンクション名 ：数値設定値取得（親設定コード含む）　　                        */
  /* 引数             ：iApKihoncd            ＡＰ基本コード                     */
  /*                  ：iShopcd               ショップコード                     */
  /*                  ：iSetteino             設定番号                          */
  /*                  ：iSetteicd             設定コード                        */
  /*                  ：iOyasetteino          親設定番号                        */
  /*                  ：iOyasetteicd          親設定コード                       */
  /*                  ：iSetteinm             設定名称                          */
  /* return           ：returnSuchisetteiata  数値設定値                        */
  /****************************************************************************/
  FUNCTION getSuchisetteiataiHasOya( iApKihoncd     IN VARCHAR2
                                   , iShopcd        IN VARCHAR2
                                   , iSetteino      IN VARCHAR2
                                   , iSetteicd      IN VARCHAR2
                                   , iOyasetteino   IN VARCHAR2
                                   , iOyasetteicd   IN VARCHAR2
                                   , iSetteinm      IN VARCHAR2
  ) RETURN VARCHAR2 IS returnSuchisetteiatai NUMBER(20);
    /* システム管理マスタから文字設定値の取得 */
    CURSOR SELECT_ATAI IS
      SELECT SYKM.SUCHISETTEIATAI
        FROM S02M006T SYKM
       WHERE SYKM.APKIHONCD = iApKihoncd
         AND SYKM.SHOPCD    = iShopcd
         AND SYKM.SETTEINO  = iSetteino
         AND ( ( iSetteicd IS NOT NULL AND SYKM.SETTEICD = iSetteicd ) 
             OR ( iSetteicd IS NULL ) ) 
         AND SYKM.OYASETTEINO = iOyasetteino 
         AND ( ( iOyasetteicd IS NOT NULL AND SYKM.OYASETTEICD = iOyasetteicd ) 
             OR ( iOyasetteicd IS NULL ) ) 
         AND SYKM.RCRDKBN = 'M'
         AND SYKM.YUKOKBN = '1'
    ;
  
  BEGIN 
    returnSuchisetteiatai := 0; -- 初期値 
    OPEN SELECT_ATAI;
    LOOP FETCH SELECT_ATAI INTO returnSuchisetteiatai;
      EXIT WHEN SELECT_ATAI%NOTFOUND;
      -- 複数件チェック
      IF SELECT_ATAI%ROWCOUNT > 1 THEN
         RAISE_APPLICATION_ERROR(-20009, iSetteinm||'が複数見つかりました。');
      END IF;
      
    END LOOP;
    CLOSE SELECT_ATAI;
    
    RETURN(returnSuchisetteiatai);
  END getSuchisetteiataiHasOya; 
  
  /*****************************************************************************/
  /* ファンクション名：レギュラー商品判定                                           */
  /* 引数             ：iShncd            商品コード        　                   */
  /* return           ：returnIsRegular  レギュラー商品：TRUE                    */
  /*****************************************************************************/
  FUNCTION isRegular( iShncd      IN VARCHAR2
  ) RETURN BOOLEAN IS returnIsRegular BOOLEAN;
  /* CURSOR:基本商品マスタからレギュラー商品の取得 */
  CURSOR SELECT_RGL IS
    SELECT KSNM.SHNCD
      FROM S02M007T KSNM
     WHERE KSNM.APKIHONCD = '001' -- ＡＰ基本コード
       AND KSNM.STKBN     = '0'   -- セット区分
       AND KSNM.LCTGRYCD  = '01'  -- 大分類コード
       AND KSNM.MCTGRYCD  = '001' -- 中分類コード
       AND KSNM.SCTGRYCD  = '001' -- 小分類コード
       AND KSNM.YUKOKBN   = '1'   -- 有効区分
     ORDER BY KSNM.SHNCD
    ;
  /* 変数宣言 */
  wRegularShncd             VARCHAR2(6) := NULL; -- レギュラー商品コード
  BEGIN 
    returnIsRegular := FALSE;
    OPEN SELECT_RGL;
      LOOP FETCH SELECT_RGL INTO wRegularShncd;
        EXIT WHEN SELECT_RGL%NOTFOUND;
        
        IF iShncd = wRegularShncd THEN 
           returnIsRegular := TRUE;
        END IF;
      END LOOP;
    CLOSE SELECT_RGL;
    RETURN(returnIsRegular);
  END isRegular;
  
  /*****************************************************************************/
  /* ファンクション名：スリム商品判定                                              */
  /* 引数             ：iShncd           商品コード                              */
  /* return           ：returnIsSlim     スリム商品：TRUE                        */
  /*****************************************************************************/
  FUNCTION isSlim( iShncd      IN VARCHAR2
  ) RETURN BOOLEAN IS returnIsSlim BOOLEAN;
  /* CURSOR:基本商品マスタからレギュラー商品の取得 */
  CURSOR SELECT_SLM IS
    SELECT KSNM.SHNCD
      FROM S02M007T KSNM
     WHERE KSNM.APKIHONCD = '001' -- ＡＰ基本コード
       AND KSNM.STKBN     = '0'   -- セット区分
       AND KSNM.LCTGRYCD  = '01'  -- 大分類コード
       AND KSNM.MCTGRYCD  = '001' -- 中分類コード
       AND KSNM.SCTGRYCD  = '002' -- 小分類コード
       AND KSNM.YUKOKBN   = '1'   -- 有効区分
     ORDER BY KSNM.SHNCD
    ;
  /* 変数宣言 */
  wSlimShncd             VARCHAR2(6) := NULL; -- レギュラー商品コード
  BEGIN 
    returnIsSlim := FALSE;
    OPEN SELECT_SLM;
      LOOP FETCH SELECT_SLM INTO wSlimShncd;
        EXIT WHEN SELECT_SLM%NOTFOUND;
        
        IF iShncd = wSlimShncd THEN 
           returnIsSlim := TRUE;
        END IF;
      END LOOP;
    CLOSE SELECT_SLM;
    RETURN(returnIsSlim);
  END isSlim;
  
  /*****************************************************************************/
  /* ファンクション名：クレールＬ判定                                              */
  /* 引数             ：iShncd                 商品コード                        */
  /* return           ：returnIsClairLcds     クレールＬ：TRUE                   */
  /*****************************************************************************/
  FUNCTION isClairLcds( iShncd      IN VARCHAR2
  ) RETURN BOOLEAN IS returnIsClairLcds BOOLEAN;
  /* CURSOR:システム管理マスタからクレールＬ商品の取得 */
  CURSOR SELECT_CLS IS
    SELECT SYKM.MOJISETTEIATAI
      FROM S02M006T SYKM
     WHERE SYKM.APKIHONCD = '000'
       AND SYKM.SHOPCD    = '000'
       AND SYKM.SETTEINO  = '123'
       AND SYKM.RCRDKBN   = 'M'
       AND SYKM.YUKOKBN = '1'
    ;
  /* 変数宣言 */
  wClairLcd             VARCHAR2(6) := NULL; -- クレールＬ商品コード
  BEGIN 
    returnIsClairLcds := FALSE;
    OPEN SELECT_CLS;
      LOOP FETCH SELECT_CLS INTO wClairLcd;
        EXIT WHEN SELECT_CLS%NOTFOUND;
        
        IF iShncd = wClairLcd THEN 
           returnIsClairLcds := TRUE;
        END IF;
      END LOOP;
    CLOSE SELECT_CLS;
    RETURN(returnIsClairLcds);
  END isClairLcds;
  
  /*****************************************************************************/
  /* ファンクション名：クレールＳ判定                                              */
  /* 引数             ：iShncd                 商品コード                        */
  /* return           ：returnIsClairScds     クレールＳ：TRUE                   */
  /*****************************************************************************/
  FUNCTION isClairScds( iShncd      IN VARCHAR2
  ) RETURN BOOLEAN IS returnIsClairScds BOOLEAN;
  /* CURSOR:システム管理マスタからクレールＬ商品の取得 */
  CURSOR SELECT_CSS IS
    SELECT SYKM.MOJISETTEIATAI
      FROM S02M006T SYKM
     WHERE SYKM.APKIHONCD = '000'
       AND SYKM.SHOPCD    = '000'
       AND SYKM.SETTEINO  = '124'
       AND SYKM.RCRDKBN   = 'M'
       AND SYKM.YUKOKBN = '1'
    ;
  /* 変数宣言 */
  wClairScd             VARCHAR2(6) := NULL; -- クレールＳ商品コード
  BEGIN 
    returnIsClairScds := FALSE;
    OPEN SELECT_CSS;
      LOOP FETCH SELECT_CSS INTO wClairScd;
        EXIT WHEN SELECT_CSS%NOTFOUND;
        
        IF iShncd = wClairScd THEN 
           returnIsClairScds := TRUE;
        END IF;
      END LOOP;
    CLOSE SELECT_CSS;
    RETURN(returnIsClairScds);
  END isClairScds;
  
  /*****************************************************************************/
  /* ファンクション名：得意先休眠判定                                              */
  /* 引数             ：iApkihoncd             ＡＰ基本コード                    */
  /*                  ：iShopcd                ショップコード                    */
  /*                  ：iTokuisakicd           得意先コード                     */
  /*                  ：iAcptdnpyno            受注伝票番号                     */
  /* return           ：returnJudgekyumin         休眠：TRUE                       */
  /*****************************************************************************/
  FUNCTION judgekyumin( iApkihoncd   IN VARCHAR2
                      , iShopcd      IN VARCHAR2
                      , iTokuisakicd  IN VARCHAR2
                      , iAcptdnpyno  IN VARCHAR2 
  ) RETURN BOOLEAN IS returnJudgekyumin BOOLEAN; 
  /* 変数宣言 */ 
  wCount            NUMBER(2,0); -- 休眠対象得意先判定
  wUnyoYmd          S02M013T.KSHNUNYOYMD%TYPE;      -- 運用日付
  wKyuminJudgeCnt   S02M006T.SUCHISETTEIATAI%TYPE;  -- 得意先休眠判定日数
  wKyuminStDate     S02M050T.SAISHUACPTDD%type;     -- 休眠開始日
  wMaxAcptDd        S04T001T.ACPTDD%TYPE;           -- 最終受注日

  BEGIN
    returnJudgekyumin := FALSE; -- 初期値
    -- 休眠対象得意先判定 
    SELECT COUNT(*)
      INTO wCount
      FROM S02M013T TKSM
           INNER JOIN S02M050T TKJT
           ON ( TKJT.APKIHONCD       = TKSM.APKIHONCD
                AND TKJT.SHOPCD      = TKSM.SHOPCD
                AND TKJT.TOKUISAKICD = TKSM.TOKUISAKICD
                AND TKJT.YUKOKBN = '1'
                AND TKJT.ZNKDLVRYDNPYNO IS NOT NULL -- １回以上配達実績がある
              )
           INNER JOIN S02M017T SKSM
           ON ( SKSM.APKIHONCD     = TKSM.APKIHONCD
                AND SKSM.SHOPCD    = TKSM.SHOPCD
                AND SKSM.SKYSAKICD = TKSM.SKYSAKICD
                AND SKSM.YUKOKBN = '1'
              )
     WHERE TKSM.TOKUISAKIKBN   =  '1'        -- 得意先区分：顧客
       AND TKSM.TOKUISAKIJOTAI IN ('3', '5') -- 得意先状態：仮会員／本会員
       AND TKSM.YUKOKBN        =  '1'
       AND TKSM.APKIHONCD      =  iApkihoncd
       AND TKSM.SHOPCD         =  iShopcd
       AND TKSM.TOKUISAKICD    =  iTokuisakicd
       AND NOT EXISTS ( SELECT '1'
                          FROM S04T001T JHTT
                         WHERE JHTT.APKIHONCD   = TKSM.APKIHONCD
                           AND JHTT.SHOPCD      = TKSM.SHOPCD
                           AND JHTT.TOKUISAKICD = TKSM.TOKUISAKICD
                           AND JHTT.ACPTDNPYNO <> iAcptdnpyno -- 処理中伝票以外
                           AND JHTT.DNPYSTS    IN ('02','03','11') -- 伝票ステータス：受注／再配達／配達中
                           AND JHTT.YUKOKBN     = '1'
                      )    -- 未納受注が１件もない
    ; 
    IF wCount = 0 THEN 
       RETURN(returnJudgekyumin); -- 休眠対象外
    END IF; 
    
    -- 運用日付取得
    wUnyoYmd := S01GETOPERATIONDATE(iApKihoncd, iShopcd); 
    -- システム管理マスタから休眠判定日数を取得
    wKyuminJudgeCnt := getSuchisetteiatai( '000', '000', '120', '得意先休眠判定日数' );
    
    -- 休眠判定日付の算出
    SELECT TO_CHAR(TO_DATE(wUnyoYmd, 'YYYYMMDD') - wKyuminJudgeCnt, 'YYYYMMDD')
      INTO wKyuminStDate
      FROM dual;
    
    -- 最終受注日の取得
    SELECT NVL(MAX(JHTT.ACPTDD), '00000000')
      INTO wMaxAcptDd
      FROM S04T001T JHTT
     WHERE JHTT.APKIHONCD   = iApkihoncd 
       AND JHTT.SHOPCD      = iShopcd 
       AND JHTT.TOKUISAKICD = iTokuisakicd 
       AND JHTT.ACPTDNPYNO <> iAcptdnpyno 
       AND JHTT.DNPYSTS     = '12' -- 配達済
       AND JHTT.YUKOKBN     = '1'  -- 最終の配達済みの受注日が既定日数より過去
    ;
    
    -- 休眠判定
    IF wMaxAcptDd < wKyuminStDate THEN 
       returnJudgekyumin := TRUE; 
    END IF; 
    
    RETURN(returnJudgekyumin);
  END Judgekyumin;
  
  /*****************************************************************************/
  /* ファンクション名：最終受注日取得                                              */
  /* 引数             ：iApkihoncd             ＡＰ基本コード                    */
  /*                  ：iShopcd                ショップコード                    */
  /*                  ：iTokuisakicd           得意先コード                     */
  /*                  ：iAcptdnpyno            受注伝票番号                     */
  /* return           ：returnSaishuacptdd     最終受注日                       */
  /*****************************************************************************/
  FUNCTION getLastacptdd( iApkihoncd   IN VARCHAR2
                          , iShopcd      IN VARCHAR2
                          , iTokuisakicd IN VARCHAR2
                          , iAcptdnpyno  IN VARCHAR2 
  ) RETURN VARCHAR2 IS returnLastacptdd VARCHAR2(8); 
  BEGIN 
    returnLastacptdd := NULL; -- 初期値
    SELECT MAX(JHTT.ACPTDD) 
      INTO returnLastacptdd 
      FROM S04T001T JHTT 
     WHERE JHTT.APKIHONCD   = iApkihoncd 
       AND JHTT.SHOPCD      = iShopcd 
       AND JHTT.ACPTDNPYNO <> iAcptdnpyno 
       AND JHTT.TOKUISAKICD = iTokuisakicd 
       AND JHTT.DNPYSTS IN ('02', '03', '11', '12') 
       AND JHTT.YUKOKBN = '1' 
    ;
    RETURN(returnLastacptdd);
    
    EXCEPTION 
      WHEN NO_DATA_FOUND THEN 
        RETURN NULL;
  END getLastacptdd; 
  
  /*****************************************************************************/
  /* プロシージャ名：消費税設定                                                   */
  /* 引数             ：meisai_rec           明細行                             */
  /*                  ：iSuryo               数量                              */
  /*                  ：iUnt                 単価                              */
  /*                  ：iTaxsetteikbn        税設定区分                         */
  /*                  ：iTaxkeisankbn        税計算区分                         */
  /*                  ：iKirikaebi           切替日                             */
  /*                  ：iDlvrykbn            配達区分                             */
  /* インボイス対応追加 start ABI                                               */
  /*                  ：iInvoiceFlg          インボイス施行状況区分             */
  /* インボイス対応追加 end ABI                                                 */
  /* 引数（ＯＵＴ）     ：oUntShohitax         単価消費税                         */
  /*                  ：oShohitax            消費税                             */
  /* インボイス対応追加 start ABI                                               */
  /*                  ：oRefshohitaxkin      参考消費税                         */
  /* インボイス対応追加 end ABI                                                 */
  /*****************************************************************************/
  PROCEDURE setTax( meisai_rec        IN S04T002T%rowtype -- 明細行
                  , iSuryo            IN NUMBER   -- 数量
                  , iUnt              IN NUMBER   -- 単価
                  , iTaxsetteikbn     IN VARCHAR2 -- 税設定区分
                  , iTaxkeisankbn     IN VARCHAR2 -- 税計算区分
                  , iKirikaebi        IN VARCHAR2 -- 切替日
                  , iDlvrykbn         IN VARCHAR2 -- 配達区分
                  , oUntshohitaxkin  OUT NUMBER -- 単価消費税
                  , oShohitaxkin     OUT NUMBER -- 消費税
-- インボイス対応追加 start ABI
                  , oRefshohitaxkin  OUT NUMBER   -- 参考消費税
                  , iInvoiceFlg       IN VARCHAR2 -- インボイス施行状況区分
-- インボイス対応追加 end ABI
  ) IS
  /* 変数宣言 */
  taxkbn                    VARCHAR2(1)  := NULL; -- 消費税計算用税区分
  kin                       NUMBER(11,0) := 0;    -- 消費税計算用金額
  wUrikinhasukeisankbn      VARCHAR2(1)  := NULL;  -- 売上金額端数計算区分
  BEGIN 
    -- 初期化
    oUntshohitaxkin := 0; -- 単価消費税
    oShohitaxkin    := 0; -- 消費税
    
    -- 消費税計算用税区分の取得
    IF meisai_rec.KEIJOKBN = '08' THEN -- 計上区分：経費
       taxkbn := '2';                  -- 非課税
    ELSE 
       taxkbn := meisai_rec.TAXKBN; -- 商品税区分
    END IF;
    
    -- 消費税計算用金額
    -- ショップマスタ取得 
    SELECT
      SHPM.URIKINHASUKEISANKBN  -- 売上金額端数計算区分
    INTO wUrikinhasukeisankbn
    FROM
      S02M046T SHPM 
    WHERE
      SHPM.APKIHONCD = meisai_rec.APKIHONCD 
      AND SHPM.SHOPCD = meisai_rec.SHOPCD  
      AND SHPM.YUKOKBN = '1'
    ;
-- 本線対応変更 start ABI
--    kin := S01SyohizeiService.roundValue(iSuryo * iUnt, wUrikinhasukeisankbn); 
    kin := Z1808B01.roundValue(iSuryo * iUnt, wUrikinhasukeisankbn); 
-- 本線対応変更 end ABI
    
-- インボイス対応削除 start ABI
--    IF taxkbn <> '5' THEN
-- インボイス対応削除 end ABI
    -- 税計算区分：外税
    IF iTaxkeisankbn = '2' THEN 
       -- 単価消費税金額
       oUntshohitaxkin := 0;
       -- 消費税
-- 本線対応変更 start ABI
--       oShohitaxkin := S01SyohizeiService.SyohizeiKeisan('1', --単体計算フラグ（する）
       oShohitaxkin := Z1808B01.SyohizeiKeisan('1', --単体計算フラグ（する）
-- 本線対応変更 end ABI
                                               iTaxsetteikbn, --税設定区分
                                                      taxkbn, --税区分
                                               iTaxkeisankbn, --税計算区分
                                                  iKirikaebi, --切り替え日
                                        meisai_rec.APKIHONCD, --AP基本コード
                                           meisai_rec.SHOPCD, --ショップコード
                                                        NULL, --仕入先コード
                                                        NULL, --数量
                                                        kin); -- 金額
-- インボイス対応追加 start ABI
       IF iInvoiceFlg = '1' THEN
          oRefshohitaxkin := oShohitaxkin;
       END IF;
-- インボイス対応追加 end ABI

    -- 税計算区分：内税
    ELSE 
       -- 単価消費税金額
-- 本線対応変更 start ABI
--       oUntshohitaxkin := S01SyohizeiService.SyohizeiKeisan('1', --単体計算フラグ（する）
       oUntshohitaxkin := Z1808B01.SyohizeiKeisan('1', --単体計算フラグ（する）
-- 本線対応変更 end ABI
                                                  iTaxsetteikbn, --税設定区分
                                                         taxkbn, --税区分
                                                  iTaxkeisankbn, --税計算区分
                                                     iKirikaebi, --切り替え日
                                           meisai_rec.APKIHONCD, --AP基本コード
                                              meisai_rec.SHOPCD, --ショップコード
                                                           NULL, --仕入先コード
                                                           NULL, --数量
                                                           iUnt); --単価
       -- 消費税金額
       IF iUnt IS NULL OR iUnt = 0 THEN 
-- 本線対応変更 start ABI
--          oShohitaxkin := S01SyohizeiService.SyohizeiKeisan('1', --単体計算フラグ（する）
          oShohitaxkin := Z1808B01.SyohizeiKeisan('1', --単体計算フラグ（する）
-- 本線対応変更 end ABI
                                                  iTaxsetteikbn, --税設定区分
                                                         taxkbn, --税区分
                                                  iTaxkeisankbn, --税計算区分
                                                     iKirikaebi, --切り替え日
                                           meisai_rec.APKIHONCD, --AP基本コード
                                              meisai_rec.SHOPCD, --ショップコード
                                                           NULL, --仕入先コード
                                                           NULL, --数量
                                                           kin); -- 金額
       ELSE 
          oShohitaxkin := oUntshohitaxkin * iSuryo;
       END IF;
-- インボイス対応追加 start ABI
       IF iInvoiceFlg = '1' THEN
          -- 単価消費税金額の初期化
          oUntshohitaxkin := 0;
          -- 参考消費税に消費税金額を設定
          oRefshohitaxkin := oShohitaxkin;
          -- 消費税金額の算出
-- 本線対応変更 start ABI
--          oShohitaxkin := S01SyohizeiService.SyohizeiKeisan('1', -- 単体計算フラグ（する）
          oShohitaxkin := Z1808B01.SyohizeiKeisan('1', -- 単体計算フラグ（する）
-- 本線対応変更 end ABI
                                                  iTaxsetteikbn, -- 税設定区分
                                                         taxkbn, -- 税区分
                                                            '2', -- 税計算区分
                                                     iKirikaebi, -- 切り替え日
                                           meisai_rec.APKIHONCD, -- AP基本コード
                                              meisai_rec.SHOPCD, -- ショップコード
                                                           NULL, -- 仕入先コード
                                                           NULL, -- 数量
                                                           kin); -- 金額
       END IF;
-- インボイス対応追加 end ABI
    END IF;
-- インボイス対応削除 start ABI
--    ELSE
--          S01SyohizeiService.SetSyohizeiKeisan(iTaxsetteikbn, --税設定区分
--                                               iTaxkeisankbn, --税計算区分
--                                                  iKirikaebi, --切り替え日
--                                        meisai_rec.APKIHONCD, --AP基本コード
--                                           meisai_rec.SHOPCD, --ショップコード
--                                                      iSuryo, --数量
--                                            meisai_rec.SHNCD, --商品コード
--                                                   iDlvrykbn, --配達区分
--                                         meisai_rec.KEIJOKBN, --計上区分
--                                             oUntshohitaxkin, --戻り値：単価消費税
--                                               oShohitaxkin); --戻り値：消費税
--    END IF;
-- インボイス対応削除 end ABI
  END setTax;
  
  /*****************************************************************************/
  /* プロシージャ名：本数、金額、サーバー集計                                       */
  /* 引数             ：meisai_rec         合計対象                             */
  /*                  ：iKeijokbn          計上区分                             */
  /*                  ：iWatercd           製品水大分類                         */
  /*                  ：iServerL           サーバー大分類                       */
  /*                  ：iServerM           サーバー本体中分類                    */
  /* インボイス対応追加 start ABI                                                */
  /*                  ：iInvoiceFlg           インボイス施行状況区分             */
  /* インボイス対応追加 end ABI                                                  */
  /* 引数（IN OUT）    ：sum_rec            合計データ                           */
  /*****************************************************************************/
  PROCEDURE addAll( meisai_rec   IN S04T002T%rowtype -- 合計対象
                  , iKeijokbn    IN VARCHAR2 -- 計上区分
                  , iWatercd     IN VARCHAR2 -- 製品水大分類
                  , iServerL     IN VARCHAR2 -- サーバー大分類
                  , iServerM     IN VARCHAR2 -- サーバー本体中分類
                  , sum_rec  IN OUT sum_rec_type -- 合計データ
-- インボイス対応追加 start ABI
                  , iInvoiceFlg  IN VARCHAR2  -- インボイス施行状況区分
-- インボイス対応追加 end ABI
  ) IS
  /* 変数宣言 */
  isYuryo                    BOOLEAN := FALSE; -- 有料フラグ
  BEGIN
    isYuryo := iKeijokbn IN ( '01', '02' ); 
    IF meisai_rec.TANPINMEISAIFLG = '1' AND meisai_rec.MEISAIKBN = '0' THEN -- 単品商品 
       -- 製品水合計 
       IF meisai_rec.LCTGRYCD = iWatercd THEN -- 製品水 
          IF isRegular( meisai_rec.SHNCD ) THEN -- レギュラー 
             sum_rec.RGRASURYO := sum_rec.RGRASURYO + meisai_rec.NOHINSURYO; -- 配達レギュラー本数 
             IF isYuryo THEN 
                sum_rec.DLVRYRGRAYUSYOHNS := sum_rec.DLVRYRGRAYUSYOHNS + meisai_rec.NOHINSURYO; -- 配達レギュラー有償本数
             ELSE 
                sum_rec.DLVRYRGRAMUSHOHNS := sum_rec.DLVRYRGRAMUSHOHNS + meisai_rec.NOHINSURYO; -- 配達レギュラー無償本数
             END IF; 
          ELSIF isSlim( meisai_rec.SHNCD ) THEN -- スリム
             sum_rec.SRMSURYO := sum_rec.SRMSURYO + meisai_rec.NOHINSURYO; -- 配達スリム本数 
             IF isYuryo THEN 
                sum_rec.DLVRYSRMYUSYOHNS  := sum_rec.DLVRYSRMYUSYOHNS  + meisai_rec.NOHINSURYO; -- 配達スリム有償本数
             ELSE 
                sum_rec.DLVRYSRMMUSHOHNS  := sum_rec.DLVRYSRMMUSHOHNS  + meisai_rec.NOHINSURYO; -- 配達スリム無償本数
             END IF; 
          ELSE                                  -- その他  
             IF isYuryo THEN 
                sum_rec.DLVRYSTYSHNS      := sum_rec.DLVRYSTYSHNS      + meisai_rec.NOHINSURYO; -- 配達その他有償本数
             ELSE 
                sum_rec.DLVRYSTMSHNS      := sum_rec.DLVRYSTMSHNS      + meisai_rec.NOHINSURYO; -- 配達その他無償本数
             END IF; 
          END IF; 
          -- 配達実績製品水売上金額
          sum_rec.DLVRYSHSUAKIN     := sum_rec.DLVRYSHSUAKIN     + NVL(meisai_rec.NOHINKIN, 0); 
       ELSE 
          -- 配達実績その他売上金額
          sum_rec.DLVRYSONOTAURIKIN := sum_rec.DLVRYSONOTAURIKIN + NVL(meisai_rec.NOHINKIN, 0); 
       END IF;
       -- サーバー合計
       IF meisai_rec.LCTGRYCD = iServerL AND meisai_rec.MCTGRYCD = iServerM THEN -- サーバー本体
          IF isClairLcds( meisai_rec.SHNCD ) THEN    -- クレールＬ
             IF 0 < meisai_rec.NOHINSURYO THEN 
                sum_rec.KURERURITTORUSTUP    := sum_rec.KURERURITTORUSTUP    + meisai_rec.NOHINSURYO; -- クレールＬ設置
             ELSE 
                sum_rec.KURERURITTORUHIKIAGE := sum_rec.KURERURITTORUHIKIAGE - meisai_rec.NOHINSURYO; -- クレールＬ引上
             END IF; 
          ELSIF isclairScds( meisai_rec.SHNCD ) THEN -- クレールＳ
             IF 0 < meisai_rec.NOHINSURYO THEN 
                sum_rec.KURERUESUSTUP    := sum_rec.KURERUESUSTUP    + meisai_rec.NOHINSURYO; -- クレールＳ設置;
             ELSE 
                sum_rec.KURERUESUHIKIAGE := sum_rec.KURERUESUHIKIAGE - meisai_rec.NOHINSURYO; -- クレールＳ引上
             END IF; 
          ELSE                                            -- その他
             IF 0 < meisai_rec.NOHINSURYO THEN 
                sum_rec.SONOTASTUP    := sum_rec.SONOTASTUP    + meisai_rec.NOHINSURYO; -- その他設置
             ELSE 
                sum_rec.SONOTAHIKIAGE := sum_rec.SONOTAHIKIAGE - meisai_rec.NOHINSURYO; -- その他引上
             END IF; 
          END IF;
       END IF;
    END IF; 
    
    -- 総金額、消費税金額合計
    IF meisai_rec.MEISAIKBN = '0' AND meisai_rec.NYURYOKUMEISAIFLG = '1' THEN 
       sum_rec.ACPTKIN  := sum_rec.ACPTKIN  + NVL(meisai_rec.ACPTKIN, 0);  -- 受注金額
       sum_rec.ACPTSHOHITAXKIN  := sum_rec.ACPTSHOHITAXKIN  + NVL(meisai_rec.ACPTSHOHITAXKIN, 0);  -- 受注消費税金額
-- インボイス対応追加 start ABI
       -- 税区分が「1:課税」の場合、受注一般税抜金額に受注金額を加算
       IF meisai_rec.TAXKBN = '1' THEN
          sum_rec.ACPTIPPANNUKIKIN := NVL(sum_rec.ACPTIPPANNUKIKIN, 0) + NVL(meisai_rec.ACPTKIN, 0);       -- 受注一般税抜金額
       END IF;
       -- 税区分が「4:課税軽減」の場合、受注軽減税抜金額に受注金額を加算
       IF meisai_rec.TAXKBN = '4' THEN
          sum_rec.ACPTKEIGENNUKIKIN := NVL(sum_rec.ACPTKEIGENNUKIKIN, 0) + NVL(meisai_rec.ACPTKIN, 0);     -- 受注軽減税抜金額
       END IF;
       -- 税区分が「2:非課税」の場合、受注非課税金額に受注金額を加算
       IF meisai_rec.TAXKBN = '2' THEN
          sum_rec.ACPTHIKAZEIURIIKIN := NVL(sum_rec.ACPTHIKAZEIURIIKIN, 0) + NVL(meisai_rec.ACPTKIN, 0);   -- 受注非課税金額
       END IF;
       -- 税区分が「3:不課税」の場合、受注不課税売上金額に受注金額を加算
       IF meisai_rec.TAXKBN = '3' THEN
          sum_rec.ACPTFUKAZEIURIIKIN := NVL(sum_rec.ACPTFUKAZEIURIIKIN, 0) + NVL(meisai_rec.ACPTKIN, 0);   -- 受注不課税売上金額
       END IF;
-- インボイス対応追加 end ABI
       sum_rec.NOHINKIN := sum_rec.NOHINKIN + NVL(meisai_rec.NOHINKIN, 0); -- 納品金額
       sum_rec.NOHINSHOHITAXKIN := sum_rec.NOHINSHOHITAXKIN + NVL(meisai_rec.NOHINSHOHITAXKIN, 0); -- 納品消費税金額
-- インボイス対応追加 start ABI
       -- 税区分が「1:課税」の場合、納品一般税抜金額に納品金額を加算
       IF meisai_rec.TAXKBN = '1' THEN
          sum_rec.NOHINIPPANNUKIKIN := NVL(sum_rec.NOHINIPPANNUKIKIN, 0) + NVL(meisai_rec.NOHINKIN, 0);     -- 納品一般税抜金額
       END IF;
       -- 税区分が「4:課税軽減」の場合、納品軽減税抜金額に納品金額を加算
       IF meisai_rec.TAXKBN = '4' THEN
          sum_rec.NOHINKEIGENNUKIKIN := NVL(sum_rec.NOHINKEIGENNUKIKIN, 0) + NVL(meisai_rec.NOHINKIN, 0);   -- 納品軽減税抜金額
       END IF;
       -- 税区分が「2:非課税」の場合、納品非課税金額に納品金額を加算
       IF meisai_rec.TAXKBN = '2' THEN
          sum_rec.NOHINHIKAZEIURIIKIN := NVL(sum_rec.NOHINHIKAZEIURIIKIN, 0) + NVL(meisai_rec.NOHINKIN, 0); -- 納品非課税金額
       END IF;
       -- 税区分が「3:不課税」の場合、納品不課税売上金額に納品金額を加算
       IF meisai_rec.TAXKBN = '3' THEN
          sum_rec.NOHINFUKAZEIURIIKIN := NVL(sum_rec.NOHINFUKAZEIURIIKIN, 0) + NVL(meisai_rec.NOHINKIN, 0); -- 納品不課税売上金額
       END IF;
-- インボイス対応追加 end ABI
    END IF; 
  END addAll;
  
  /*****************************************************************************/
  /* プロシージャ名：再配達本数、金額集計                                           */
  /* 引数             ：meisai_rec         合計対象                             */
  /*                  ：iKeijokbn          計上区分                             */
  /*                  ：iWatercd           製品水大分類                         */
  /* インボイス対応追加 start ABI                                               */
  /*                  ：iInvoiceFlg           インボイス施行状況区分            */
  /* インボイス対応追加 end ABI                                                 */
  /* 引数（IN OUT）    ：sum_rec            合計データ                           */
  /*****************************************************************************/
  PROCEDURE addAllSaihaitatsu( meisai_rec   IN S04T002T%rowtype -- 合計対象
                             , iKeijokbn    IN VARCHAR2 -- 計上区分
                             , iWatercd     IN VARCHAR2 -- 製品水大分類
                             , sum_rec  IN OUT sum_rec_type -- 合計データ
-- インボイス対応追加 start ABI
                             , iInvoiceFlg  IN VARCHAR2  -- インボイス施行状況区分
-- インボイス対応追加 end ABI
  ) IS
  /* 変数宣言 */
  isYuryo                    BOOLEAN := FALSE; -- 有料フラグ
  BEGIN
    isYuryo := iKeijokbn IN ( '01', '02' ); 
    IF meisai_rec.TANPINMEISAIFLG = '1' AND meisai_rec.MEISAIKBN = '0' THEN -- 単品商品 
       -- 製品水合計 
       IF meisai_rec.LCTGRYCD = iWatercd THEN -- 製品水 
          IF isRegular( meisai_rec.SHNCD ) THEN -- レギュラー 
             sum_rec.RGRASURYO := sum_rec.RGRASURYO + meisai_rec.ACPTSURYO; -- 配達レギュラー本数 
             IF isYuryo THEN 
                sum_rec.DLVRYRGRAYUSYOHNS := sum_rec.DLVRYRGRAYUSYOHNS + meisai_rec.ACPTSURYO; -- 配達レギュラー有償本数
             ELSE 
                sum_rec.DLVRYRGRAMUSHOHNS := sum_rec.DLVRYRGRAMUSHOHNS + meisai_rec.ACPTSURYO; -- 配達レギュラー無償本数
             END IF; 
          ELSIF isSlim( meisai_rec.SHNCD ) THEN -- スリム
             sum_rec.SRMSURYO := sum_rec.SRMSURYO + meisai_rec.ACPTSURYO; -- 配達スリム本数 
             IF isYuryo THEN 
                sum_rec.DLVRYSRMYUSYOHNS  := sum_rec.DLVRYSRMYUSYOHNS  + meisai_rec.ACPTSURYO; -- 配達スリム有償本数
             ELSE 
                sum_rec.DLVRYSRMMUSHOHNS  := sum_rec.DLVRYSRMMUSHOHNS  + meisai_rec.ACPTSURYO; -- 配達スリム無償本数
             END IF; 
          ELSE                                  -- その他  
             IF isYuryo THEN 
                sum_rec.DLVRYSTYSHNS      := sum_rec.DLVRYSTYSHNS      + meisai_rec.ACPTSURYO; -- 配達その他有償本数
             ELSE 
                sum_rec.DLVRYSTMSHNS      := sum_rec.DLVRYSTMSHNS      + meisai_rec.ACPTSURYO; -- 配達その他無償本数
             END IF; 
          END IF; 
          -- 配達実績製品水売上金額
          sum_rec.DLVRYSHSUAKIN     := sum_rec.DLVRYSHSUAKIN     + NVL(meisai_rec.ACPTKIN, 0); 
       ELSE 
          -- 配達実績その他売上金額
          sum_rec.DLVRYSONOTAURIKIN := sum_rec.DLVRYSONOTAURIKIN + NVL(meisai_rec.ACPTKIN, 0); 
       END IF;
    END IF; 
    
    -- 総金額、消費税金額合計
    IF meisai_rec.MEISAIKBN = '0' AND meisai_rec.NYURYOKUMEISAIFLG = '1' THEN 
       sum_rec.ACPTKIN  := sum_rec.ACPTKIN  + NVL(meisai_rec.ACPTKIN, 0);  -- 受注金額
       sum_rec.ACPTSHOHITAXKIN  := sum_rec.ACPTSHOHITAXKIN  + NVL(meisai_rec.ACPTSHOHITAXKIN, 0);  -- 受注消費税金額
-- インボイス対応追加 start ABI
       -- 税区分が「1:課税」の場合、受注一般税抜金額に受注金額を加算
       IF meisai_rec.TAXKBN = '1' THEN
          sum_rec.ACPTIPPANNUKIKIN := NVL(sum_rec.ACPTIPPANNUKIKIN, 0) + NVL(meisai_rec.ACPTKIN, 0);       -- 受注一般税抜金額
       END IF;
       -- 税区分が「4:課税軽減」の場合、受注軽減税抜金額に受注金額を加算
       IF meisai_rec.TAXKBN = '4' THEN
          sum_rec.ACPTKEIGENNUKIKIN := NVL(sum_rec.ACPTKEIGENNUKIKIN, 0) + NVL(meisai_rec.ACPTKIN, 0);     -- 受注軽減税抜金額
       END IF;
       -- 税区分が「2:非課税」の場合、受注非課税金額に受注金額を加算
       IF meisai_rec.TAXKBN = '2' THEN
          sum_rec.ACPTHIKAZEIURIIKIN := NVL(sum_rec.ACPTHIKAZEIURIIKIN, 0) + NVL(meisai_rec.ACPTKIN, 0);   -- 受注非課税金額
       END IF;
       -- 税区分が「3:不課税」の場合、受注不課税売上金額に受注金額を加算
       IF meisai_rec.TAXKBN = '3' THEN
          sum_rec.ACPTFUKAZEIURIIKIN := NVL(sum_rec.ACPTFUKAZEIURIIKIN, 0) + NVL(meisai_rec.ACPTKIN, 0);   -- 受注不課税売上金額
       END IF;
-- インボイス対応追加 end ABI
    END IF; 
  END addAllSaihaitatsu;
  
  /****************************************************************************/
  /* プロシージャ名：基本情報設定                                                 */
  /* 引数        　    ：iApKihoncd           担当者ＡＰ基本コード                */
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
  /* プロシージャ名  ：委託・受託得意先チェック                                     */
  /* 引数           ：iItakuTokuisaki          委託得意先                        */
  /*                ：iJutakuTokuisaki        受託得意先                        */
  /****************************************************************************/
  PROCEDURE checkTokuisaki( iItakuTokuisaki    IN S02M013T%rowtype -- 委託得意先
                          , iJutakuTokuisaki   IN S02M013T%rowtype -- 受託得意先
  ) IS 
  BEGIN 
    /* 受託チェック */
    IF iJutakuTokuisaki.TOKUISAKICD IS NULL THEN 
       RAISE_APPLICATION_ERROR(-20005, '受託の得意先マスタが存在しません。');
    END IF;
    IF iJutakuTokuisaki.ITAKUAITEAPKIHONCD IS NULL 
       OR iJutakuTokuisaki.ITAKUAITESHOPCD IS NULL 
       OR iJutakuTokuisaki.ITAKUAITETOKUISAKICD IS NULL THEN 
       RAISE_APPLICATION_ERROR(-20006, '受託の相手先コードが存在しません。');
    END IF;
    IF iJutakuTokuisaki.ITAKUAITEAPKIHONCD <> iItakuTokuisaki.APKIHONCD 
       OR iJutakuTokuisaki.ITAKUAITESHOPCD <> iItakuTokuisaki.SHOPCD 
       OR iJutakuTokuisaki.ITAKUAITETOKUISAKICD <> iItakuTokuisaki.TOKUISAKICD THEN 
       RAISE_APPLICATION_ERROR(-20009, '委託、受託の相手先コードが異なります。');
    END IF;
    /* 委託チェック */
    IF iItakuTokuisaki.TOKUISAKICD IS NULL THEN 
       RAISE_APPLICATION_ERROR(-20007, '委託の得意先マスタが存在しません。');
    END IF;
    IF iItakuTokuisaki.ITAKUAITEAPKIHONCD IS NULL 
       OR iItakuTokuisaki.ITAKUAITESHOPCD IS NULL 
       OR iItakuTokuisaki.ITAKUAITETOKUISAKICD IS NULL THEN 
       RAISE_APPLICATION_ERROR(-20008, '委託の相手先コードが存在しません。');
    END IF;
    IF iItakuTokuisaki.ITAKUAITEAPKIHONCD <> iJutakuTokuisaki.APKIHONCD
       OR iItakuTokuisaki.ITAKUAITESHOPCD <> iJutakuTokuisaki.SHOPCD 
       OR iItakuTokuisaki.ITAKUAITETOKUISAKICD <> iJutakuTokuisaki.TOKUISAKICD THEN 
       RAISE_APPLICATION_ERROR(-20009, '委託、受託の相手先コードが異なります。');
    END IF;
    
  END checkTokuisaki; 
  
  /*****************************************************************************/
  /* プロシージャ名：配布物企画対象得意先マスタ更新                                  */
  /* 引数             ：itaku_rec      委託データ                                */
  /*                  ：jutaku_rec     受託データ                               */
  /*****************************************************************************/
  PROCEDURE processHaifubutsu( itaku_rec    IN itaku_rec_type  -- 委託データ
                             , jutaku_rec   IN jutaku_rec_type -- 受託データ
  ) IS 
  /* 変数宣言 */
  wItakuHaifubutsucd        VARCHAR(5) := NULL; -- 委託配布物コード
  wJutakuHaifubutsucd       VARCHAR(5) := NULL; -- 受託配布物コード
  noItaku                   BOOLEAN := FALSE; -- 委託側配布物データがない
  /* CURSOR：受託配達明細テーブルから配布物明細を取得する */
  CURSOR SELECT_HFB IS
    SELECT JHMT_ITAKU.HAIFUBUTSUCD  -- 委託配布物コード
         , JHMT_JUTAKU.HAIFUBUTSUCD -- 受託配布物コード
      FROM S04T002T JHMT_ITAKU 
           LEFT OUTER JOIN S04T002T JHMT_JUTAKU
           ON ( JHMT_JUTAKU.HAIFUBUTSUCD = JHMT_ITAKU.HAIFUBUTSUCD
                AND JHMT_JUTAKU.APKIHONCD  = jutaku_rec.APKIHONCD
                AND JHMT_JUTAKU.SHOPCD     = jutaku_rec.SHOPCD
                AND JHMT_JUTAKU.ACPTDNPYNO = jutaku_rec.ACPTDNPYNO
                AND JHMT_JUTAKU.EDANO      = jutaku_rec.EDANO
                AND JHMT_JUTAKU.YUKOKBN    = '1'
                AND JHMT_JUTAKU.MEISAIKBN  = '1' -- 明細区分：配布物
           )
     WHERE JHMT_ITAKU.APKIHONCD  = itaku_rec.APKIHONCD 
       AND JHMT_ITAKU.SHOPCD     = itaku_rec.SHOPCD 
       AND JHMT_ITAKU.ACPTDNPYNO = itaku_rec.ACPTDNPYNO 
       AND JHMT_ITAKU.EDANO      = itaku_rec.EDANO 
       AND JHMT_ITAKU.YUKOKBN    = '1'
       AND JHMT_ITAKU.MEISAIKBN  = '1' -- 明細区分：配布物
    ;
  CURSOR SELECT_JTHFB IS
    SELECT JHMT_JUTAKU.HAIFUBUTSUCD -- 受託配布物コード
      FROM S04T002T JHMT_JUTAKU 
     WHERE JHMT_JUTAKU.APKIHONCD  = jutaku_rec.APKIHONCD 
       AND JHMT_JUTAKU.SHOPCD     = jutaku_rec.SHOPCD
       AND JHMT_JUTAKU.ACPTDNPYNO = jutaku_rec.ACPTDNPYNO
       AND JHMT_JUTAKU.EDANO      = jutaku_rec.EDANO
       AND JHMT_JUTAKU.YUKOKBN    = '1'
       AND JHMT_JUTAKU.MEISAIKBN  = '1' -- 明細区分：配布物
    ;
  BEGIN 
    -- 委託側存在するデータを処理する
    OPEN SELECT_HFB; 
      LOOP 
        FETCH SELECT_HFB INTO wItakuHaifubutsucd 
                            , wJutakuHaifubutsucd 
        ;
        
        EXIT WHEN SELECT_HFB%NOTFOUND; 
        
        -- 伝票ステータス：配達売上済、受託に当配布物がある場合
        IF jutaku_rec.DNPYSTS = '12' AND 
           wJutakuHaifubutsucd IS NOT NULL THEN 
           UPDATE S02M033T HTTM
              SET HTTM.HAIFUTRGTSTS = '3' -- 配布対象ステータス（'3'：配達済）
                , HTTM.DLVRYDD      = jutaku_rec.DLVRYDD -- 配達日
                /* 更新者 */
                , HTTM.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
                , HTTM.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
                , HTTM.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
                , HTTM.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
                , HTTM.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
                , HTTM.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
                , HTTM.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
                , HTTM.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
            WHERE HTTM.APKIHONCD    = itaku_rec.APKIHONCD 
              AND HTTM.SHOPCD       = itaku_rec.SHOPCD 
              AND HTTM.HAIFUBUTSUCD = wJutakuHaifubutsucd 
              AND HTTM.TOKUISAKICD  = itaku_rec.TOKUISAKICD 
           ; 
        END IF;
        -- 伝票ステータス：配達売上済、受託に当配布物がない場合；または、配達時キャンセルの場合
        IF ( jutaku_rec.DNPYSTS = '12' AND wJutakuHaifubutsucd IS NULL ) 
           OR jutaku_rec.DNPYSTS = '19' THEN 
           UPDATE S02M033T HTTM
              SET HTTM.HAIFUTRGTSTS = '1' -- 配布対象ステータス（'1'：未納品）
                , HTTM.ACPTDNPYNO       = NULL -- 受注伝票番号
                , HTTM.EDANO            = NULL -- 枝番号
                , HTTM.ACPTDNPYMEISAINO = NULL -- 受注伝票明細番号
                , HTTM.ACPTTORIKOMIDD   = NULL -- 受注取込日：受注ステータス
                /* 更新者 */
                , HTTM.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
                , HTTM.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
                , HTTM.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
                , HTTM.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
                , HTTM.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
                , HTTM.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
                , HTTM.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
                , HTTM.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
            WHERE HTTM.APKIHONCD    = itaku_rec.APKIHONCD
              AND HTTM.SHOPCD       = itaku_rec.SHOPCD
              AND HTTM.HAIFUBUTSUCD = wItakuHaifubutsucd 
              AND HTTM.TOKUISAKICD  = itaku_rec.TOKUISAKICD
           ; 
        END IF; 
        -- 伝票ステータス： 未配達/不在未配達
        IF jutaku_rec.DNPYSTS IN ( '13', '14' ) THEN 
           UPDATE S02M033T HTTM
              SET HTTM.HAIFUTRGTSTS = '2' -- 配布対象ステータス（'2'：受注済）
                , HTTM.EDANO = itaku_rec.EDANO + 1 -- 枝番号
                /* 更新者 */
                , HTTM.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
                , HTTM.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
                , HTTM.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
                , HTTM.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
                , HTTM.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
                , HTTM.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
                , HTTM.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
                , HTTM.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
            WHERE HTTM.APKIHONCD    = itaku_rec.APKIHONCD
              AND HTTM.SHOPCD       = itaku_rec.SHOPCD
              AND HTTM.HAIFUBUTSUCD = wItakuHaifubutsucd 
              AND HTTM.TOKUISAKICD  = itaku_rec.TOKUISAKICD
           ; 
        END IF; 
      END LOOP;
    CLOSE SELECT_HFB;
    
    -- 受託側データを処理する
    OPEN SELECT_JTHFB; 
      LOOP 
        FETCH SELECT_JTHFB INTO wJutakuHaifubutsucd 
        ;
        
        EXIT WHEN SELECT_JTHFB%NOTFOUND; 
        
        -- 伝票ステータス：配達売上済の場合
        IF jutaku_rec.DNPYSTS = '12' THEN 
           UPDATE S02M033T HTTM
              SET HTTM.HAIFUTRGTSTS = '3' -- 配布対象ステータス（'3'：配達済）
                , HTTM.DLVRYDD      = jutaku_rec.DLVRYDD -- 配達日
                /* 更新者 */
                , HTTM.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
                , HTTM.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
                , HTTM.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
                , HTTM.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
                , HTTM.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
                , HTTM.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
                , HTTM.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
                , HTTM.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
            WHERE HTTM.APKIHONCD    = itaku_rec.APKIHONCD 
              AND HTTM.SHOPCD       = itaku_rec.SHOPCD 
              AND HTTM.HAIFUBUTSUCD = wJutakuHaifubutsucd 
              AND HTTM.TOKUISAKICD  = itaku_rec.TOKUISAKICD 
           ; 
        END IF;
        -- 伝票ステータス：配達時キャンセルの場合
        IF jutaku_rec.DNPYSTS = '19' THEN 
           UPDATE S02M033T HTTM
              SET HTTM.HAIFUTRGTSTS = '1' -- 配布対象ステータス（'1'：未納品）
                , HTTM.ACPTDNPYNO       = NULL -- 受注伝票番号
                , HTTM.EDANO            = NULL -- 枝番号
                , HTTM.ACPTDNPYMEISAINO = NULL -- 受注伝票明細番号
                , HTTM.ACPTTORIKOMIDD   = NULL -- 受注取込日：受注ステータス
                /* 更新者 */
                , HTTM.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
                , HTTM.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
                , HTTM.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
                , HTTM.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
                , HTTM.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
                , HTTM.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
                , HTTM.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
                , HTTM.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
            WHERE HTTM.APKIHONCD    = itaku_rec.APKIHONCD
              AND HTTM.SHOPCD       = itaku_rec.SHOPCD
              AND HTTM.HAIFUBUTSUCD = wJutakuHaifubutsucd 
              AND HTTM.TOKUISAKICD  = itaku_rec.TOKUISAKICD
           ; 
        END IF; 
        -- 伝票ステータス： 未配達/不在未配達
        IF jutaku_rec.DNPYSTS IN ( '13', '14' ) THEN 
           UPDATE S02M033T HTTM
              SET HTTM.HAIFUTRGTSTS = '2' -- 配布対象ステータス（'2'：受注済）
                , HTTM.EDANO = itaku_rec.EDANO + 1 -- 枝番号
                /* 更新者 */
                , HTTM.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
                , HTTM.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
                , HTTM.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
                , HTTM.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
                , HTTM.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
                , HTTM.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
                , HTTM.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
                , HTTM.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
            WHERE HTTM.APKIHONCD    = itaku_rec.APKIHONCD
              AND HTTM.SHOPCD       = itaku_rec.SHOPCD
              AND HTTM.HAIFUBUTSUCD = wJutakuHaifubutsucd
              AND HTTM.TOKUISAKICD  = itaku_rec.TOKUISAKICD
           ; 
        END IF; 
      END LOOP;
    CLOSE SELECT_JTHFB; 
  END processHaifubutsu;
  
  -- ポイント制度対応追加 start ABI
  /******************************************************************************/
  /* ファンクション名 ：ポイントテーブルの管理番号採番                          */
  /* 引数             ：itaku_rec          委託データ                           */
  /* return           ：retPointKanrino    管理番号                             */
  /******************************************************************************/
  FUNCTION GetPointKanrino( itaku_rec        IN itaku_rec_type
  ) RETURN VARCHAR2 IS retPointKanrino       VARCHAR2(15);
  BEGIN
    -- 管理番号採番
    -- 引数ワーク(S20W002T)登録
    INSERT INTO S20W002T VALUES (
        itaku_rec.APKIHONCD       -- ＡＰ基本コード
      , itaku_rec.SHOPCD          -- ショップコード
      , userinfo_rec.APKIHONCD    -- 担当者ＡＰ基本コード
      , userinfo_rec.SHOPCD       -- 担当者ショップコード
      , userinfo_rec.TNSHACD      -- 担当者コード
      , userinfo_rec.PGMID        -- 処理ID
      , userinfo_rec.IPADRSE      -- ＩＰアドレス
      , '1'                       -- 行番号
      , null                      -- 伝票明細番号
      , null                      -- 伝票明細枝番号
      , null                      -- 商品コード
      , null                      -- 計上区分
      , null                      -- 商品単価
      , null                      -- 画面からの発生ポイント
      , null                      -- 数量
      , null                      -- 明細備考
      , null                      -- 予定発生ポイント明細の管理番号
      , null                      -- 予定発生ポイント明細の連番
      , null                      -- 売上還元ポイントマスタの取得ポイント種別
      , null                      -- 売上還元ポイントマスタの取得プランコード
      , '1'                       -- 有効区分
      , userinfo_rec.APKIHONCD    -- 登録者ＡＰ基本コード
      , userinfo_rec.SHOPCD       -- 登録者ショップコード
      , userinfo_rec.TNSHACD      -- 登録者コード
      , userinfo_rec.SYSTMYMD     -- 登録システム日付
      , userinfo_rec.SYSTMJIKAN   -- 登録システム時間
      , userinfo_rec.UNYOYMD      -- 登録運用日付
      , userinfo_rec.IPADRSE      -- 登録ＩＰアドレス
      , userinfo_rec.PGMID        -- 登録プログラムＩＤ
      , userinfo_rec.APKIHONCD    -- 更新者AP基本コード
      , userinfo_rec.SHOPCD       -- 更新者ショップコード
      , userinfo_rec.TNSHACD      -- 更新者コード
      , userinfo_rec.SYSTMYMD     -- 更新システム日付
      , userinfo_rec.SYSTMJIKAN   -- 更新システム時間
      , userinfo_rec.UNYOYMD      -- 更新運用日付
      , userinfo_rec.IPADRSE      -- 更新ＩＰアドレス
      , userinfo_rec.PGMID);      -- 更新プログラムＩＤ

    -- 実行
    Z2401B01.PointinfbhnProc(
        itaku_rec.APKIHONCD          -- AP基本コード
      , itaku_rec.SHOPCD             -- ショップコード
      , userinfo_rec.APKIHONCD       -- 担当者ＡＰ基本コード
      , userinfo_rec.SHOPCD          -- 担当者ショップコード
      , userinfo_rec.TNSHACD         -- 担当者コード
      , userinfo_rec.PGMID           -- 処理ID
      , userinfo_rec.IPADRSE         -- ＩＰアドレス
      , '04'                         -- ポイント情報部品処理種別(管理番号採番)
      , null                         -- 得意先コード
      , null                         -- 伝票種別
      , null                         -- 注文日
      , null                         -- 配達予定日
      , null                         -- 配達希望時間区分
      , null                         -- 配達区分
      , null);                       -- 受注受付経路区分

    -- 処理結果ワーク(S20W003T)読取り
    SELECT SAIBANKANRINO
    INTO retPointKanrino
    FROM  S20W003T
    WHERE
      TNSHAAPKIHONCD  = userinfo_rec.APKIHONCD
      AND TNSHASHOPCD = userinfo_rec.SHOPCD
      AND TNSHACD     = userinfo_rec.TNSHACD
      AND PRCSID      = userinfo_rec.PGMID;

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
  /* プロシージャ名 ：予定発生ポイントテーブル登録                             */
  /* 引数           ：kanrino         管理番号                                 */
  /*                ：itaku_rec       委託データ                               */
  /*****************************************************************************/
  PROCEDURE registPointHeaderTable( kanrino        IN VARCHAR2
                                  , itaku_rec      IN itaku_rec_type
  ) IS
  BEGIN
    -- S20T001T（予定発生ポイントテーブル）登録
    INSERT INTO S20T001T VALUES (
        itaku_rec.APKIHONCD       -- ＡＰ基本コード
      , itaku_rec.SHOPCD          -- ショップコード
      , kanrino                   -- 管理番号
      , itaku_rec.ACPTDNPYNO      -- 受注伝票番号
      , itaku_rec.EDANO           -- 受注枝番号
      , null                      -- 売上伝票番号（別途更新設定）
      , null                      -- 売上枝番号（別途更新設定）
      , itaku_rec.TOKUISAKICD     -- 得意先コード
      , '1'                       -- 有効区分
      , userinfo_rec.APKIHONCD    -- 登録者ＡＰ基本コード
      , userinfo_rec.SHOPCD       -- 登録者ショップコード
      , userinfo_rec.TNSHACD      -- 登録者コード
      , userinfo_rec.SYSTMYMD     -- 登録システム日付
      , userinfo_rec.SYSTMJIKAN   -- 登録システム時間
      , userinfo_rec.UNYOYMD      -- 登録運用日付
      , userinfo_rec.IPADRSE      -- 登録ＩＰアドレス
      , userinfo_rec.PGMID        -- 登録プログラムＩＤ
      , userinfo_rec.APKIHONCD    -- 更新者ＡＰ基本コード
      , userinfo_rec.SHOPCD       -- 更新者ショップコード
      , userinfo_rec.TNSHACD      -- 更新者コード
      , userinfo_rec.SYSTMYMD     -- 更新システム日付
      , userinfo_rec.SYSTMJIKAN   -- 更新システム時間
      , userinfo_rec.UNYOYMD      -- 更新運用日付
      , userinfo_rec.IPADRSE      -- 更新ＩＰアドレス
      , userinfo_rec.PGMID);      -- 更新プログラムＩＤ

  END registPointHeaderTable;

  /*****************************************************************************/
  /* プロシージャ名 ：予定発生ポイント明細テーブル登録(計算詳細含む)           */
  /* 引数           ：itaku_rec      委託データ                                */
  /*                ：jutaku_rec     受託データ                                */
  /*                ：meisai_rec     受注配達明細テーブル                      */
  /*****************************************************************************/
  PROCEDURE registPointMeisaiTable( itaku_rec   IN itaku_rec_type
                                  , jutaku_rec  IN jutaku_rec_type
                                  , meisai_rec  IN S04T002T%rowtype
  ) IS
    wSaibanno          VARCHAR2(15);                   -- 採番した予定発行ポイントテーブル管理番号
    wAcptdd            VARCHAR2(8);                    -- 受注日
    wDlvrykibojikankbn VARCHAR2(1);                    -- 配達希望時間区分
    wDlvrykbn          VARCHAR2(1);                    -- 配達区分
    wAcptrcptkeirokbn  VARCHAR2(2);                    -- 受注受付経路区分
    i                  PLS_INTEGER := 0;               -- 処理結果ワーク取得時カウンタ
    wS20W003TList      S20W003TList;                   -- ポイント情報部品処理結果ワークリスト
    wYukoData          NUMBER(1) := 0;                 -- 有効データ有無フラグ
    wRenban            NUMBER(2) := 1;                 -- 連番

  BEGIN
    -- ポイント情報部品で引数ワークに設定する情報を取得する
    -- itaku_recでは保持していないため
    SELECT
        ACPTDD            -- 受注日
      , DLVRYKIBOJIKANKBN -- 配達希望時間区分
      , DLVRYKBN          -- 配達区分
      , ACPTRCPTKEIROKBN  -- 受注受付経路区分
    INTO
        wAcptdd
      , wDlvrykibojikankbn
      , wDlvrykbn
      , wAcptrcptkeirokbn
    FROM S04T001T
    WHERE APKIHONCD  = itaku_rec.APKIHONCD
      AND SHOPCD     = itaku_rec.SHOPCD
      AND ACPTDNPYNO = meisai_rec.ACPTDNPYNO
      AND EDANO      = meisai_rec.EDANO
      AND YUKOKBN    = '1';

    -- 明細分設定した引数ワークでポイント情報部品実行
    Z2401B01.PointinfbhnProc(
        itaku_rec.APKIHONCD          -- ＡＰ基本コード
      , itaku_rec.SHOPCD             -- ショップコード
      , userinfo_rec.APKIHONCD       -- 担当者ＡＰ基本コード
      , userinfo_rec.SHOPCD          -- 担当者ショップコード
      , userinfo_rec.TNSHACD         -- 担当者コード
      , userinfo_rec.PGMID           -- 処理ID
      , userinfo_rec.IPADRSE         -- ＩＰアドレス
      , '02'                         -- ポイント情報部品処理種別(一括計算)
      , itaku_rec.TOKUISAKICD        -- 得意先コード
      , '01'                         -- 伝達種別(受注)
      , wAcptdd                      -- 注文日:受注日
      , jutaku_rec.DLVRYJISSEKIDD    -- 配達実績日
      , wDlvrykibojikankbn           -- 配達希望時間区分
      , wDlvrykbn                    -- 配達区分
      , wAcptrcptkeirokbn);          -- 受注受付経路区分

    -- 処理結果ワーク(S20W003T)から実行結果読取り
    i := 0;
    FOR cS20W003T IN (SELECT * FROM S20W003T
                      WHERE TNSHAAPKIHONCD  = userinfo_rec.APKIHONCD
                        AND TNSHASHOPCD     = userinfo_rec.SHOPCD
                        AND TNSHACD         = userinfo_rec.TNSHACD
                        AND PRCSID          = userinfo_rec.PGMID
                        AND YUKOKBN         = '1'
                      ORDER BY TO_NUMBER(GYOBANGOU))
    LOOP
      wS20W003TList(i).APKIHONCD := cS20W003T.APKIHONCD;                                -- ＡＰ基本コード
      wS20W003TList(i).SHOPCD := cS20W003T.SHOPCD;                                      -- ショップコード
      wS20W003TList(i).TNSHAAPKIHONCD := cS20W003T.TNSHAAPKIHONCD;                      -- 担当者ＡＰ基本コード
      wS20W003TList(i).TNSHASHOPCD := cS20W003T.TNSHASHOPCD;                            -- 担当者ショップコード
      wS20W003TList(i).TNSHACD := cS20W003T.TNSHACD;                                    -- 担当者コード
      wS20W003TList(i).PRCSID := userinfo_rec.PGMID;                                    -- 処理ID
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
      wS20W003TList(i).POINTHASSEIKBN := cS20W003T.POINTHASSEIKBN;                      -- ポイント発生区分
      wS20W003TList(i).POINTTANKA := cS20W003T.POINTTANKA;                              -- ポイント単価
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
          -- 有効なポイントあり
          wYukoData := 1;
        ELSIF wS20W003TList(k).SYORIKEKKA = 2 THEN
          -- ショップがポイントなし
          S99_CHECK_LOG(userinfo_rec.PGMID, itaku_rec.APKIHONCD, itaku_rec.SHOPCD, '1', 'S', '1', 'XXX', 0, userinfo_rec.APKIHONCD,
            userinfo_rec.SHOPCD, userinfo_rec.TNSHACD, userinfo_rec.IPADRSE, userinfo_rec.PGMID , utl_lms.format_message('ポイントプログラムに不参加のショップです。ショップコード:[%s]', wS20W003TList(k).SHOPCD));
        ELSIF wS20W003TList(k).SYORIKEKKA = 3 THEN
          -- 得意先がポイントなし
          S99_CHECK_LOG(userinfo_rec.PGMID, itaku_rec.APKIHONCD, itaku_rec.SHOPCD, '1', 'S', '1', 'XXX', 0, userinfo_rec.APKIHONCD,
            userinfo_rec.SHOPCD, userinfo_rec.TNSHACD, userinfo_rec.IPADRSE, userinfo_rec.PGMID , utl_lms.format_message('ポイント管理を行わない得意先です。得意先コード:[%s]', wS20W003TList(k).TOKUISAKICD));
        ELSIF wS20W003TList(k).SYORIKEKKA = 4 THEN
          -- 商品がポイントなし
          S99_CHECK_LOG(userinfo_rec.PGMID, itaku_rec.APKIHONCD, itaku_rec.SHOPCD, '1', 'S', '1', 'XXX', 0, userinfo_rec.APKIHONCD,
            userinfo_rec.SHOPCD, userinfo_rec.TNSHACD, userinfo_rec.IPADRSE, userinfo_rec.PGMID , utl_lms.format_message('ポイントが発生しない商品です。商品コード:[%s]', wS20W003TList(k).SHNCD));
        ELSIF wS20W003TList(k).SYORIKEKKA = 5 THEN
          -- 得意先がマイアクア会員登録なし
          S99_CHECK_LOG(userinfo_rec.PGMID, itaku_rec.APKIHONCD, itaku_rec.SHOPCD, '1', 'S', '1', 'XXX', 0, userinfo_rec.APKIHONCD,
            userinfo_rec.SHOPCD, userinfo_rec.TNSHACD, userinfo_rec.IPADRSE, userinfo_rec.PGMID , utl_lms.format_message('マイアクア会員が未登録の得意先です。得意先コード:[%s]', wS20W003TList(k).TOKUISAKICD));
        ELSIF wS20W003TList(k).SYORIKEKKA = -1 THEN
          -- 取込画面側の入力CSVとマスタの不整合チェック以外の予期しないエラー発生時
          S99_CHECK_LOG(userinfo_rec.PGMID, itaku_rec.APKIHONCD, itaku_rec.SHOPCD, '1', 'S', '1', 'XXX', 0, userinfo_rec.APKIHONCD,
            userinfo_rec.SHOPCD, userinfo_rec.TNSHACD, userinfo_rec.IPADRSE, userinfo_rec.PGMID , 'ポイント情報部品(Z2401B01.PointinfbhnProc)の処理でエラーが発生しました。ログ(S99CHKLOG)を確認してください。');
          RAISE_APPLICATION_ERROR(-20719, 'ポイント情報部品(Z2401B01.PointinfbhnProc)の処理でエラーが発生しました。ログ(S99CHKLOG)を確認してください。');
        END IF;
      END LOOP;

      IF wYukoData = 1 THEN
        -- 有効なポイント情報をポイントテーブルに登録する
        wSaibanno := GetPointKanrino(itaku_rec);

        FOR j IN wS20W003TList.FIRST .. wS20W003TList.LAST LOOP
          -- ポイントありのレコードだけ書き込む
          IF wS20W003TList(j).SYORIKEKKA = 0 AND wS20W003TList(j).GOUKEIPOINT <> 0 THEN

            -- 予定発生ポイント明細テーブル(S20T002T)に登録
            INSERT INTO S20T002T VALUES (
                wS20W003TList(j).APKIHONCD                 -- ＡＰ基本コード
              , wS20W003TList(j).SHOPCD                    -- ショップコード
              , wSaibanno                                  -- 管理番号
              , wRenban                                    -- 連番
              , CASE WHEN wS20W003TList(j).DNPYMEISAINO IS NULL
                THEN 1
                ELSE wS20W003TList(j).DNPYMEISAINO
                END                                        -- 受注伝票明細番号
              , CASE WHEN wS20W003TList(j).DNPYMEISAIEDANO IS NULL
                THEN 0
                ELSE wS20W003TList(j).DNPYMEISAIEDANO
                END                                        -- 受注伝票明細枝番号
              , CASE WHEN wS20W003TList(j).DNPYMEISAINO IS NULL
                THEN 1
                ELSE wS20W003TList(j).DNPYMEISAINO
                END                                        -- 売上伝票明細番号(売上明細以外の番号も設定するためここで設定)
              , CASE WHEN wS20W003TList(j).DNPYMEISAIEDANO IS NULL
                THEN 0
                ELSE wS20W003TList(j).DNPYMEISAIEDANO
                END                                        -- 売上伝票明細枝番号(売上明細以外の番号も設定するためここで設定)
              , wS20W003TList(j).POINTGENSIFUTANAPKIHONCD  -- ポイント原資負担元ＡＰ基本コード
              , wS20W003TList(j).POINTGENSIFUTANSHOPCD     -- ポイント原資負担元ショップコード
              , wS20W003TList(j).POINTHASSEIAPKIHONCD      -- ポイント発生元ＡＰ基本コード
              , wS20W003TList(j).POINTHASSEISHOPCD         -- ポイント発生元ショップコード
              , wS20W003TList(j).POINTKIND                 -- ポイント種別
              , wS20W003TList(j).TOKUBETSUFUYOPLNCD        -- 特別付与プランコード
              , wS20W003TList(j).TOKUBETSUSTARTGESSU       -- 特別付与開始月数
              , wS20W003TList(j).TOKUBETSUENDGESSU         -- 特別付与終了月数
              , wS20W003TList(j).TEKIYOSTRTDD              -- 適用開始日
              , wS20W003TList(j).TEKIYOSHRYDD              -- 適用終了日
              , wS20W003TList(j).POINTKBN                  -- ポイント区分
              , wS20W003TList(j).GOUKEIPOINT               -- ポイント数
              , wS20W003TList(j).MEISAIBIKO                -- 明細備考
              , wS20W003TList(j).CHUMONDD                  -- ポイント予定日付(売上日)
              , null                                       -- ポイント発生日付
              , null                                       -- ポイントマイアクア連携日付
              , '1'                                        -- 有効区分
              , userinfo_rec.APKIHONCD                     -- 登録者ＡＰ基本コード
              , userinfo_rec.SHOPCD                        -- 登録者ショップコード
              , userinfo_rec.TNSHACD                       -- 登録者コード
              , userinfo_rec.SYSTMYMD                      -- 登録システム日付
              , userinfo_rec.SYSTMJIKAN                    -- 登録システム時間
              , userinfo_rec.UNYOYMD                       -- 登録運用日付
              , userinfo_rec.IPADRSE                       -- 登録ＩＰアドレス
              , userinfo_rec.PGMID                         -- 登録プログラムＩＤ
              , userinfo_rec.APKIHONCD                     -- 更新者AP基本コード
              , userinfo_rec.SHOPCD                        -- 更新者ショップコード
              , userinfo_rec.TNSHACD                       -- 更新者コード
              , userinfo_rec.SYSTMYMD                      -- 更新システム日付
              , userinfo_rec.SYSTMJIKAN                    -- 更新システム時間
              , userinfo_rec.UNYOYMD                       -- 更新運用日付
              , userinfo_rec.IPADRSE                       -- 更新ＩＰアドレス
              , userinfo_rec.PGMID);                       -- 更新プログラムＩＤ

            -- 予定発生ポイント明細計算詳細テーブル(S20T004T)に登録
            INSERT INTO S20T004T VALUES (
                wS20W003TList(j).APKIHONCD                 -- ＡＰ基本コード
              , wS20W003TList(j).SHOPCD                    -- ショップコード
              , wSaibanno                                  -- 管理番号
              , wRenban                                    -- 連番
              , CASE WHEN wS20W003TList(j).DNPYMEISAINO IS NULL
                THEN 1
                ELSE wS20W003TList(j).DNPYMEISAINO
                END                                        -- 受注伝票明細番号
              , CASE WHEN wS20W003TList(j).DNPYMEISAIEDANO IS NULL
                THEN 0
                ELSE wS20W003TList(j).DNPYMEISAIEDANO
                END                                        -- 受注伝票明細枝番号
              , CASE WHEN wS20W003TList(j).DNPYMEISAINO IS NULL
                THEN 1
                ELSE wS20W003TList(j).DNPYMEISAINO
                END                                        -- 売上伝票明細番号(売上明細以外の番号も設定するためここで設定)
              , CASE WHEN wS20W003TList(j).DNPYMEISAIEDANO IS NULL
                THEN 0
                ELSE wS20W003TList(j).DNPYMEISAIEDANO
                END                                        -- 売上伝票明細枝番号(売上明細以外の番号も設定するためここで設定)
              , wS20W003TList(j).POINTHASSEIKBN            -- ポイント発生区分
              , wS20W003TList(j).POINTTANKA                -- ポイント単価
              , wS20W003TList(j).SURYO                     -- 数量
              , wS20W003TList(j).POINTRNKCD                -- ポイントランクコード
              , wS20W003TList(j).POINTRNK                  -- ポイントランク
              , wS20W003TList(j).POINTBAIRITU              -- ポイントランク倍率
              , wS20W003TList(j).GOUKEIPOINT               -- 計算ポイント数
              , '1'                                        -- 有効区分
              , userinfo_rec.APKIHONCD                     -- 登録者ＡＰ基本コード
              , userinfo_rec.SHOPCD                        -- 登録者ショップコード
              , userinfo_rec.TNSHACD                       -- 登録者コード
              , userinfo_rec.SYSTMYMD                      -- 登録システム日付
              , userinfo_rec.SYSTMJIKAN                    -- 登録システム時間
              , userinfo_rec.UNYOYMD                       -- 登録運用日付
              , userinfo_rec.IPADRSE                       -- 登録ＩＰアドレス
              , userinfo_rec.PGMID                         -- 登録プログラムＩＤ
              , userinfo_rec.APKIHONCD                     -- 更新者AP基本コード
              , userinfo_rec.SHOPCD                        -- 更新者ショップコード
              , userinfo_rec.TNSHACD                       -- 更新者コード
              , userinfo_rec.SYSTMYMD                      -- 更新システム日付
              , userinfo_rec.SYSTMJIKAN                    -- 更新システム時間
              , userinfo_rec.UNYOYMD                       -- 更新運用日付
              , userinfo_rec.IPADRSE                       -- 更新ＩＰアドレス
              , userinfo_rec.PGMID);                       -- 更新プログラムＩＤ

            wRenban := wRenban + 1;
          END IF;
        END LOOP;

        -- 紐づけ項目を保持
        cmnRegistKanrino := wSaibanno;                     -- 管理番号
      END IF;-- 有効なポイントあり
    END IF;

    -- 紐づけテーブルに有効設定があれば(ポイント発生)予定発生ポイント登録
    IF wSaibanno IS NOT NULL THEN
      registPointHeaderTable(wSaibanno, itaku_rec);
    END IF;

  END registPointMeisaiTable;
  -- ポイント制度対応追加 end ABI

  /*****************************************************************************/
  /* プロシージャ名：受注配達明細データ作成処理                                     */
  /* 引数             ：itaku_rec      委託データ                                */
  /*                  ：jutaku_rec     受託データ                               */
  /* インボイス対応追加 start ABI                                               */
  /* 引数（IN OUT）   ：iInvoiceFlg    インボイス施行状況区分                   */
  /* インボイス対応追加 end ABI                                                 */
  /*****************************************************************************/
  PROCEDURE processJuchuMeisai( itaku_rec    IN itaku_rec_type  -- 委託データ
                              , jutaku_rec   IN jutaku_rec_type -- 受託データ
-- インボイス対応追加 start ABI
                              , iInvoiceFlg  IN OUT VARCHAR2 -- インボイス施行状況区分
-- インボイス対応追加 end ABI
  ) IS 
  /* 変数宣言 */ 
  wRecordS04T002T           S04T002T%rowtype := NULL; -- 受注配達明細テーブル
  wHaifubutsuzokusei            VARCHAR2(1)  := NULL; -- 配布物属性
  itaku_meisai              S04T002T%rowtype := NULL;  -- 変更前委託側明細データ
  -- 新規明細判定フラグ
  wIsShnki                      BOOLEAN      := FALSE; -- 新規明細判定フラグ
  -- 単価、消費税計算用
  wHansokuunt                   NUMBER(10,2) := 0;     -- 販促単価
  wPricekbn                     VARCHAR2(1)  := NULL;  -- 価格区分
  wOpenUnt                      NUMBER(10,2) := 0;     -- オーブン単価
  wTaxsetteikbn                 VARCHAR2(1)  := NULL;  -- 税設定区分
  -- セット商品展開用
  wOya                      S04T002T%rowtype := NULL;  -- 親明細行
  wKidAcptTaxSum                NUMBER(11,0) := 0;     -- 子商品受注消費税合計
  wKidNohinTaxSum               NUMBER(11,0) := 0;     -- 子商品納品消費税合計
  wKidLastNo                    NUMBER(2,0)  := 0;     -- 子商品最終明細枝番号
  wKidKeijokbn                  VARCHAR2(2)  := NULL;  -- 展開マスタ計上区分（登録用）
  -- 本数、金額、サーバー合計用
  wWatercd                      VARCHAR2(2)  := NULL;  -- 製品水大分類コード
  wIsYuryo                      BOOLEAN      := FALSE; -- 有料フラグ
  wServerL                      VARCHAR2(2)  := NULL;  -- サーバー大分類 
  wServerM                      VARCHAR2(3)  := NULL;  -- サーバー中分類 
  vUrikinhasukeisankbn          VARCHAR2(1)  := NULL;  -- 売上金額端数計算区分
-- インボイス対応追加 start ABI
  vInvoicshikoymd               VARCHAR2(8)  := NULL;  -- インボイス施行日
  wRtnsankoushohitaxkin         VARCHAR2(11) := NULL;  -- 参考消費税金額（返却用）
-- インボイス対応追加 end ABI
  wStkbn                        VARCHAR2(1)  := NULL;  -- セット区分
  tokuisakiMST                  S02M013T%rowtype := NULL; -- 得意先マスタ 
  -- ポイント制度対応追加 start ABI
  wGyoban            NUMBER(3) := 1;        -- 予定発生ポイントテーブルの行番号
  -- ポイント制度対応追加 end ABI

  /* CURSOR：受託明細データ抽出 */
  CURSOR SELECT_JUTAKUMEISAI IS 
    SELECT
        JHMT_JUTAKU.APKIHONCD
      , JHMT_JUTAKU.SHOPCD
      , JHMT_JUTAKU.ACPTDNPYNO
      , JHMT_JUTAKU.EDANO
      , JHMT_JUTAKU.ACPTDNPYMEISAINO
      , JHMT_JUTAKU.ACPTDNPYMEISAIEDANO
      , JHMT_JUTAKU.ITAKUAITEAPKIHONCD
      , JHMT_JUTAKU.ITAKUAITESHOPCD
      , JHMT_JUTAKU.ITAKUAITEACPTDNPYNO
      , JHMT_JUTAKU.ITAKUAITEEDANO
      , JHMT_JUTAKU.ITAKUAITEACPTDNPYMEISAINO
      , JHMT_JUTAKU.ITAKUAITEACPTDNPYMEISAIEDANO
      , JHMT_JUTAKU.LCTGRYCD
      , JHMT_JUTAKU.MCTGRYCD
      , JHMT_JUTAKU.SCTGRYCD
      , JHMT_JUTAKU.SHNCD
      , JHMT_JUTAKU.HAIFUBUTSUAPKIHONCD
      , JHMT_JUTAKU.HAIFUBUTSUSHOPCD
      , JHMT_JUTAKU.HAIFUBUTSUCD
      , JHMT_JUTAKU.KATASIKI
      , JHMT_JUTAKU.KEIJOKBN
      , NVL(JHMT_ITAKU.ACPTSURYO,0)
      , JHMT_JUTAKU.HYOJUNUNT
      , JHMT_JUTAKU.ACPTUNT
      , JHMT_JUTAKU.ACPTKIN
      , JHMT_JUTAKU.ACPTUNTSHOHITAXKIN
      , JHMT_JUTAKU.ACPTSHOHITAXKIN
      , JHMT_JUTAKU.ITAKUCMPNYACPTUNT
      , JHMT_JUTAKU.ITAKUCMPNYACPTUNTSHOHITAXKIN
      , JHMT_JUTAKU.ITAKUCMPNYACPTSHOHITAXKIN
      , JHMT_JUTAKU.NOHINSURYO
      , JHMT_JUTAKU.NOHINUNT
      , JHMT_JUTAKU.NOHINKIN
      , JHMT_JUTAKU.NOHINUNTSHOHITAXKIN
      , JHMT_JUTAKU.NOHINSHOHITAXKIN
      , JHMT_JUTAKU.SOUKAISU
      , JHMT_JUTAKU.ZANKAISU
      , JHMT_JUTAKU.TAXKBN
      , JHMT_JUTAKU.YUSYOKBN
      , JHMT_JUTAKU.MEISAIKBN
      , JHMT_JUTAKU.NYURYOKUMEISAIFLG
      , JHMT_JUTAKU.TANPINMEISAIFLG
      , JHMT_JUTAKU.STSHNCD
      , JHMT_JUTAKU.MEISAIBIKO
      , JHMT_JUTAKU.DATSKSMOTOKBN
      , JHMT_JUTAKU.URIDNPYNO
      , JHMT_JUTAKU.URIDNPYEDANO
      , JHMT_JUTAKU.URIDNPYMEISAINO
      , JHMT_JUTAKU.URIDNPYMEISAIEDANO
      , JHMT_JUTAKU.YUKOKBN
      , JHMT_JUTAKU.TOSHAAPKIHONCD
      , JHMT_JUTAKU.TOSHASHOPCD
      , JHMT_JUTAKU.TOSHACD
      , JHMT_JUTAKU.INPTSYSTMYMD
      , JHMT_JUTAKU.INPTSYSTMJIKAN
      , JHMT_JUTAKU.INPTUNYOYMD
      , JHMT_JUTAKU.INPTIPADRSE
      , JHMT_JUTAKU.INPTPGMID
      , JHMT_JUTAKU.KOSHAAPKIHONCD
      , JHMT_JUTAKU.KOSHASHOPCD
      , JHMT_JUTAKU.KOSHACD
      , JHMT_JUTAKU.KSHNSYSTMYMD
      , JHMT_JUTAKU.KSHNSYSTMJIKAN
      , JHMT_JUTAKU.KSHNUNYOYMD
      , JHMT_JUTAKU.KSHNIPADRSE
      , JHMT_JUTAKU.KSHNPGMID 
      FROM S04T002T JHMT_JUTAKU 
      LEFT JOIN S04T002T JHMT_ITAKU
         ON JHMT_JUTAKU.ITAKUAITEAPKIHONCD           = JHMT_ITAKU.APKIHONCD
        AND JHMT_JUTAKU.ITAKUAITESHOPCD              = JHMT_ITAKU.SHOPCD
        AND JHMT_JUTAKU.ITAKUAITEACPTDNPYNO          = JHMT_ITAKU.ACPTDNPYNO
        AND JHMT_JUTAKU.ITAKUAITEEDANO               = JHMT_ITAKU.EDANO
        AND JHMT_JUTAKU.ITAKUAITEACPTDNPYMEISAINO    = JHMT_ITAKU.ACPTDNPYMEISAINO
        AND JHMT_JUTAKU.ITAKUAITEACPTDNPYMEISAIEDANO = JHMT_ITAKU.ACPTDNPYMEISAIEDANO
     WHERE JHMT_JUTAKU.APKIHONCD  = jutaku_rec.APKIHONCD  
       AND JHMT_JUTAKU.SHOPCD     = jutaku_rec.SHOPCD 
       AND JHMT_JUTAKU.ACPTDNPYNO = jutaku_rec.ACPTDNPYNO 
       AND JHMT_JUTAKU.EDANO      = jutaku_rec.EDANO 
       AND JHMT_JUTAKU.YUKOKBN = '1'
       AND JHMT_JUTAKU.MEISAIKBN <> '3' -- 消費税調整以外の明細を対象とする
    ORDER BY JHMT_JUTAKU.APKIHONCD, JHMT_JUTAKU.SHOPCD, JHMT_JUTAKU.ACPTDNPYNO, JHMT_JUTAKU.EDANO 
           , JHMT_JUTAKU.ACPTDNPYMEISAINO, JHMT_JUTAKU.ACPTDNPYMEISAIEDANO 
    FOR UPDATE 
  ; 
  /* CURSOR：委託明細データ抽出 */
  CURSOR SELECT_ITAKUMEISAI IS 
    SELECT JHMT_ITAKU.* 
      FROM S04T002T JHMT_ITAKU 
     WHERE JHMT_ITAKU.APKIHONCD  = itaku_rec.APKIHONCD  
       AND JHMT_ITAKU.SHOPCD     = itaku_rec.SHOPCD 
       AND JHMT_ITAKU.ACPTDNPYNO = itaku_rec.ACPTDNPYNO 
       AND JHMT_ITAKU.EDANO      = itaku_rec.EDANO 
       AND JHMT_ITAKU.YUKOKBN = '1'
       AND JHMT_ITAKU.MEISAIKBN <> '3' -- 消費税調整以外の明細を対象とする
    ORDER BY JHMT_ITAKU.APKIHONCD, JHMT_ITAKU.SHOPCD, JHMT_ITAKU.ACPTDNPYNO, JHMT_ITAKU.EDANO 
           , JHMT_ITAKU.ACPTDNPYMEISAINO, JHMT_ITAKU.ACPTDNPYMEISAIEDANO 
    FOR UPDATE 
  ; 
  BEGIN 
    OPEN SELECT_JUTAKUMEISAI;
    OPEN SELECT_ITAKUMEISAI; -- 変更前委託側明細データ
    -- 委託の既存明細データを削除する
    IF jutaku_rec.DNPYSTS = '12' THEN 
       DELETE FROM S04T002T JHMT_ITAKU
       WHERE JHMT_ITAKU.APKIHONCD  = itaku_rec.APKIHONCD
         AND JHMT_ITAKU.SHOPCD     = itaku_rec.SHOPCD
         AND JHMT_ITAKU.ACPTDNPYNO = itaku_rec.ACPTDNPYNO
         AND JHMT_ITAKU.EDANO      = itaku_rec.EDANO
      ;
      S99_CHECK_LOG('C0815B11.processJHMT', itaku_rec.APKIHONCD, itaku_rec.SHOPCD, '1', 'D', '1', 'S04T002T', SQL%ROWCOUNT, userinfo_rec.APKIHONCD, userinfo_rec.SHOPCD, userinfo_rec.TNSHACD, userinfo_rec.IPADRSE, 'C0815B11' , '受注配達明細テーブル削除 ' );
    END IF; 
    
    -- 合計項目初期化 
    sum_rec.DLVRYRGRAYUSYOHNS := 0; -- 配達レギュラー有償本数
    sum_rec.DLVRYRGRAMUSHOHNS := 0; -- 配達レギュラー無償本数
    sum_rec.DLVRYSRMYUSYOHNS  := 0; -- 配達スリム有償本数
    sum_rec.DLVRYSRMMUSHOHNS  := 0; -- 配達スリム無償本数
    sum_rec.DLVRYSTYSHNS      := 0; -- 配達その他有償本数
    sum_rec.DLVRYSTMSHNS      := 0; -- 配達その他無償本数
    sum_rec.DLVRYSHSUAKIN     := 0; -- 配達製品水売上金額
    sum_rec.DLVRYSONOTAURIKIN := 0; -- 配達その他売上金額
    sum_rec.KURERURITTORUSTUP    := 0; -- クレールＬ設置
    sum_rec.KURERURITTORUHIKIAGE := 0; -- クレールＬ引上
    sum_rec.KURERUESUSTUP        := 0; -- クレールＳ設置;
    sum_rec.KURERUESUHIKIAGE     := 0; -- クレールＳ引上
    sum_rec.SONOTASTUP           := 0; -- その他設置
    sum_rec.SONOTAHIKIAGE        := 0; -- その他引上
    sum_rec.ACPTKIN          := 0; -- 受注金額
    sum_rec.ACPTSHOHITAXKIN  := 0; -- 受注消費税金額
    sum_rec.NOHINKIN         := 0; -- 納品金額
    sum_rec.NOHINSHOHITAXKIN := 0; -- 納品消費税金額
    sum_rec.RGRASURYO := 0; -- 配達レギュラー本数 
    sum_rec.SRMSURYO  := 0; -- 配達スリム本数 
    -- ACCIS2023-7026_start
    sum_rec.ACPTIPPANNUKIKIN    := 0; -- 受注一般税抜金額
    sum_rec.ACPTIPPANTAXKIN     := 0; -- 受注一般消費税金額
    sum_rec.ACPTKEIGENNUKIKIN   := 0; -- 受注軽減税抜金額
    sum_rec.ACPTKEIGENTAXKIN    := 0; -- 受注軽減消費税金額
    sum_rec.ACPTHIKAZEIURIIKIN  := 0; -- 受注非課税金額
    sum_rec.ACPTFUKAZEIURIIKIN  := 0; -- 受注不課税売上金額
    sum_rec.NOHINIPPANNUKIKIN   := 0; -- 納品一般税抜金額
    sum_rec.NOHINIPPANTAXKIN    := 0; -- 納品一般消費税金額
    sum_rec.NOHINKEIGENNUKIKIN  := 0; -- 納品軽減税抜金額
    sum_rec.NOHINKEIGENTAXKIN   := 0; -- 納品軽減消費税金額
    sum_rec.NOHINHIKAZEIURIIKIN := 0; -- 納品非課税金額
    sum_rec.NOHINFUKAZEIURIIKIN := 0; -- 納品不課税売上金額
    -- ACCIS2023-7026_end

    wWatercd := getMojisetteiatai( '000', '000', '096', '1', NULL, NULL, '商品大分類コード：製品水' );
    wServerL := getMojisetteiatai( '000', '000', '096', '4', NULL, NULL, '商品大分類コード：サーバー' ); 
    wServerM := getMojisetteiatai( '000', '000', '134', '41', NULL, NULL, '商品中分類コード：サーバー本体' );

    -- ショップマスタ取得 
    SELECT
      SHPM.URIKINHASUKEISANKBN  -- 売上金額端数計算区分
-- インボイス対応追加 start ABI
      , SHPM.INVOICSHIKOYMD     -- インボイス施行日
-- インボイス対応追加 end ABI
    INTO vUrikinhasukeisankbn
-- インボイス対応追加 start ABI
       , vInvoicshikoymd        -- インボイス施行日
-- インボイス対応追加 end ABI
    FROM
      S02M046T SHPM 
    WHERE
      SHPM.APKIHONCD = itaku_rec.APKIHONCD 
      AND SHPM.SHOPCD = itaku_rec.SHOPCD  
      AND SHPM.YUKOKBN = '1'
    ;

-- インボイス対応追加 start ABI
    -- インボイス施行状況区分の初期化
    iInvoiceFlg := '0';
    -- インボイス施行状況区分の設定（受託データ.配達日がインボイス施行日以降の場合、TRUE）
    IF vInvoicshikoymd <= jutaku_rec.DLVRYDD THEN
      iInvoiceFlg := '1';
    END IF;
-- インボイス対応追加 end ABI

    -- 得意先マスタ取得 
    SELECT TKSM.* 
      INTO tokuisakiMST 
      FROM S02M013T TKSM 
     WHERE TKSM.APKIHONCD   = itaku_rec.APKIHONCD 
       AND TKSM.SHOPCD      = itaku_rec.SHOPCD 
       AND TKSM.TOKUISAKICD = itaku_rec.TOKUISAKICD 
       AND TKSM.YUKOKBN = '1' 
    ;
    
    -- 受託明細データを元に新しい明細データを作る
      LOOP 
        FETCH SELECT_JUTAKUMEISAI INTO wRecordS04T002T; 
        IF SELECT_JUTAKUMEISAI%ROWCOUNT = 0 THEN 
           RAISE_APPLICATION_ERROR(-20015, '受託の明細データが存在しません。');
        END IF;

-- インボイス対応追加 start ABI
        -- インボイス施行日以前の場合のみ、消費税調整を実施
        IF iInvoiceFlg = '0' THEN
-- インボイス対応追加 end ABI
          -- 消費税調整
          IF ( SELECT_JUTAKUMEISAI%NOTFOUND -- 末行
               OR ( SELECT_JUTAKUMEISAI%ROWCOUNT > 1 AND wRecordS04T002T.NYURYOKUMEISAIFLG = '1' ) -- 入力行
             ) 
             AND wOya.TANPINMEISAIFLG = '0' THEN -- 前回親明細がセット商品
             IF wOya.ACPTSHOHITAXKIN <> wKidAcptTaxSum           -- 受注消費税調整
                OR wOya.NOHINSHOHITAXKIN <> wKidNohinTaxSum THEN -- 納品消費税調整
                
                wOya.ACPTDNPYMEISAIEDANO          := wKidLastNo + 1; -- 受注伝票明細枝番号
                wOya.ITAKUAITEACPTDNPYMEISAIEDANO := wKidLastNo + 1; -- 委託相手受注明細枝番号
                -- 消費税調整商品コード取得
                wOya.SHNCD := getMojisetteiatai( '000', '000', '166', NULL, NULL, NULL, '消費税調整商品コード');
                IF wOya.SHNCD IS NULL THEN 
                   RAISE_APPLICATION_ERROR(-20021, '消費税調整商品コード　が設定されていません。'); 
                END IF; 
                -- 商品マスタビューからの商品情報を取得する
                SELECT SHMV.LCTGRYCD -- 大分類コード
                     , SHMV.MCTGRYCD -- 中分類コード
                     , SHMV.SCTGRYCD -- 小分類コード
                     -- 標準価格：配達区分、受注日より設定
                     , ( CASE WHEN SHMV.HYOJUNPRICEKIRIKAEDD IS NULL OR jutaku_rec.ACPTDD >= SHMV.HYOJUNPRICEKIRIKAEDD 
                              THEN ( CASE jutaku_rec.DLVRYKBN 
                                     WHEN '3' THEN SHMV.SHINMOCHIKAERIPRICE -- 新持ち帰り価格
                                              ELSE SHMV.SHINHYOJUNPRICE     -- 新標準価格
                                     END ) 
                              ELSE ( CASE jutaku_rec.DLVRYKBN 
                                     WHEN '3' THEN SHMV.GENZMOCHIKAERIPRICE -- 現持ち帰り価格
                                              ELSE SHMV.GENZHYOJUNPRICE     -- 現標準価格
                                     END ) 
                         END ) HYOJUNUNT 
                     -- 税区分
                  INTO wOya.LCTGRYCD -- 大分類コード
                     , wOya.MCTGRYCD -- 中分類コード
                     , wOya.SCTGRYCD -- 小分類コード
                     , wOya.HYOJUNUNT -- 標準価格
                  FROM S02V001T SHMV
                 WHERE SHMV.APKIHONCD   = wOya.APKIHONCD  -- ＡＰ基本コード
                   AND SHMV.SHOPCD      = wOya.SHOPCD     -- ショップコード
                   AND SHMV.SHNCD       = wOya.SHNCD -- 消費税調整商品コード
                   AND SHMV.SHOPYUKOKBN = '1' -- ショップ有効区分
                ;
                wOya.KATASIKI := NULL; -- 型式
                -- 受注 
                wOya.ACPTSURYO := CASE WHEN wOya.ACPTSHOHITAXKIN <> wKidAcptTaxSum THEN 1 ELSE 0 END; -- 受注数量
                wOya.HYOJUNUNT := 0; -- 標準単価
                wOya.ACPTUNT   := 0; -- 受注単価
                wOya.ACPTKIN   := 0; -- 受注金額
                wOya.ACPTUNTSHOHITAXKIN := 0; -- 受注単価消費税金額
                wOya.ACPTSHOHITAXKIN := wOya.ACPTSHOHITAXKIN - wKidAcptTaxSum; -- 受注消費税金額
                -- 納品 
                wOya.NOHINSURYO := CASE WHEN wOya.NOHINSHOHITAXKIN <> wKidNohinTaxSum THEN 1 ELSE 0 END; -- 納品数量 
                wOya.NOHINUNT := 0; -- 納品単価
                wOya.NOHINKIN := 0; -- 納品金額
                wOya.NOHINUNTSHOHITAXKIN := 0; -- 納品単価消費税金額
                wOya.NOHINSHOHITAXKIN := wOya.NOHINSHOHITAXKIN - wKidNohinTaxSum; -- 納品消費税金額
                wOya.YUSYOKBN  := '0'; -- 有償区分
                wOya.MEISAIKBN := '3'; -- 明細区分：消費税調整
                wOya.NYURYOKUMEISAIFLG := '0'; -- 入力明細フラグ：展開行
                wOya.TANPINMEISAIFLG   := '1'; -- 単品明細フラグ：単品
                
                -- 登録
                INSERT INTO S04T002T JHMT VALUES wOya;
             END IF; 
          END IF; 
-- インボイス対応追加 start ABI
        END IF;
-- インボイス対応追加 end ABI

        EXIT WHEN SELECT_JUTAKUMEISAI%NOTFOUND; 
        
        -- 委託のデータに切替：新規／既存明細判定 
        wIsShnki := ( CASE WHEN wRecordS04T002T.ITAKUAITEACPTDNPYMEISAINO IS NULL THEN TRUE 
                           ELSE FALSE 
                      END ) ;
        
        IF ( NOT wIsShnki )
           AND wRecordS04T002T.MEISAIKBN = '0' -- 既存商品行の場合、元委託明細を読む
           THEN 
           
           -- 受託明細の委託相手受注伝票明細番号（＋枝番号）と委託明細の受注伝票明細番号（＋枝番号）が
           -- 一致しない委託明細はスキップする（受託側の配達実績登録時に行削除され、明細番号が変わるケースを想定）
           -- ※明細の読込み時、受注伝票明細番号（＋枝番号）順に読み込まれているため、この処理でよい
           LOOP
             FETCH SELECT_ITAKUMEISAI INTO itaku_meisai; 
               IF SELECT_ITAKUMEISAI%ROWCOUNT = 0 THEN 
                 RAISE_APPLICATION_ERROR(-20024, '委託の明細データが存在しません。');
               END IF; 
               EXIT WHEN SELECT_ITAKUMEISAI%NOTFOUND
                  OR (
                    wRecordS04T002T.ITAKUAITEACPTDNPYMEISAINO = itaku_meisai.ACPTDNPYMEISAINO
                    AND wRecordS04T002T.ITAKUAITEACPTDNPYMEISAIEDANO = itaku_meisai.ACPTDNPYMEISAIEDANO
                  ); 
           END LOOP;
           
           IF wRecordS04T002T.SHNCD <> itaku_meisai.SHNCD THEN 
              RAISE_APPLICATION_ERROR(-20024, '商品明細：'|| wRecordS04T002T.SHNCD ||'の委託対象は存在しません。');
           END IF;
        ELSIF wRecordS04T002T.MEISAIKBN = '1' -- 配布物の場合、元委託明細を読む（委受託配布物変更なし）
           THEN 
           
           LOOP -- 同一配布物まで読み続く
             FETCH SELECT_ITAKUMEISAI INTO itaku_meisai; 
             IF SELECT_ITAKUMEISAI%ROWCOUNT = 0 THEN 
                RAISE_APPLICATION_ERROR(-20024, '委託の明細データが存在しません。');
             END IF; 
             
             EXIT WHEN SELECT_ITAKUMEISAI%NOTFOUND
                  OR wRecordS04T002T.HAIFUBUTSUCD = itaku_meisai.HAIFUBUTSUCD; 
           END LOOP; 
        ELSE 
           itaku_meisai := NULL; 
        END IF; 
        
        -- 委託のデータに切替：基本情報 
        wRecordS04T002T.ITAKUAITEAPKIHONCD  := wRecordS04T002T.APKIHONCD;  -- 委託相手ＡＰ基本コード
        wRecordS04T002T.ITAKUAITESHOPCD     := wRecordS04T002T.SHOPCD;     -- 委託相手ショップコード
        wRecordS04T002T.ITAKUAITEACPTDNPYNO    := wRecordS04T002T.ACPTDNPYNO; -- 委託相手受注伝票番号
        wRecordS04T002T.ITAKUAITEEDANO      := wRecordS04T002T.EDANO;      -- 委託相手枝番号
        wRecordS04T002T.ITAKUAITEACPTDNPYMEISAINO       := wRecordS04T002T.ACPTDNPYMEISAINO;    -- 委託相手受注伝票明細番号
        wRecordS04T002T.ITAKUAITEACPTDNPYMEISAIEDANO := wRecordS04T002T.ACPTDNPYMEISAIEDANO; -- 委託相手受注伝票明細枝番号
        wRecordS04T002T.APKIHONCD  := itaku_rec.APKIHONCD;  -- ＡＰ基本コード
        wRecordS04T002T.SHOPCD     := itaku_rec.SHOPCD;     -- ショップコード
        wRecordS04T002T.ACPTDNPYNO := itaku_rec.ACPTDNPYNO; -- 受注伝票番号
        wRecordS04T002T.EDANO := itaku_rec.EDANO; -- 枝番号
        -- 委託のデータに切替：受注単価、金額、消費税
        wRecordS04T002T.ACPTUNT            := NVL(wRecordS04T002T.ITAKUCMPNYACPTUNT,0);                   -- 受注単価
-- 本線対応変更 start ABI
--        wRecordS04T002T.ACPTKIN            := S01SyohizeiService.roundValue(NVL(wRecordS04T002T.ACPTSURYO,0) * wRecordS04T002T.ACPTUNT, vUrikinhasukeisankbn); -- 受注金額
        wRecordS04T002T.ACPTKIN            := Z1808B01.roundValue(NVL(wRecordS04T002T.ACPTSURYO,0) * wRecordS04T002T.ACPTUNT, vUrikinhasukeisankbn); -- 受注金額
-- 本線対応変更 end ABI
        wRecordS04T002T.ACPTUNTSHOHITAXKIN := NVL(wRecordS04T002T.ITAKUCMPNYACPTUNTSHOHITAXKIN,0);        -- 受注単価消費税金額
        wRecordS04T002T.ACPTSHOHITAXKIN    := NVL(wRecordS04T002T.ITAKUCMPNYACPTSHOHITAXKIN,0);           -- 受注消費税金額
        wRecordS04T002T.ITAKUCMPNYACPTUNT            := 0; -- 委託会社受注単価
        wRecordS04T002T.ITAKUCMPNYACPTUNTSHOHITAXKIN := 0; -- 委託会社受注単価消費税金額
        WRecordS04T002T.ITAKUCMPNYACPTSHOHITAXKIN    := 0; -- 委託会社受注消費税金額
        -- 委託のデータに切替：売上伝票番号（定例売上用）
        wRecordS04T002T.URIDNPYNO          := NULL; 
        wRecordS04T002T.URIDNPYEDANO       := NULL; 
        wRecordS04T002T.URIDNPYMEISAINO    := NULL; 
        wRecordS04T002T.URIDNPYMEISAIEDANO := NULL; 
        -- 委託のデータに切替：データ作成元区分
        wRecordS04T002T.DATSKSMOTOKBN := NVL(itaku_meisai.DATSKSMOTOKBN, wRecordS04T002T.DATSKSMOTOKBN); 
        
        -- 商品の場合：納品単価、金額、消費税の取得
        IF wRecordS04T002T.MEISAIKBN = '0' THEN 
           -- 商品情報の取得
           SELECT SHMV.LCTGRYCD -- 大分類コード
                , SHMV.MCTGRYCD -- 中分類コード
                , SHMV.SCTGRYCD -- 小分類コード
                -- 標準価格：配達区分、受注日より設定
                , ( CASE WHEN SHMV.HYOJUNPRICEKIRIKAEDD IS NULL OR jutaku_rec.DLVRYDD >= SHMV.HYOJUNPRICEKIRIKAEDD 
                
                         THEN ( CASE jutaku_rec.DLVRYKBN 
                                WHEN '3' THEN SHMV.SHINMOCHIKAERIPRICE -- 新持ち帰り価格
                                         ELSE SHMV.SHINHYOJUNPRICE     -- 新標準価格
                                END ) 
                         ELSE ( CASE jutaku_rec.DLVRYKBN 
                                WHEN '3' THEN SHMV.GENZMOCHIKAERIPRICE -- 現持ち帰り価格
                                         ELSE SHMV.GENZHYOJUNPRICE     -- 現標準価格
                                END ) 
                    END ) HYOJUNUNT 
                -- 販促価格：配達区分より設定
                ,( CASE WHEN SHMV.HANSOKUKBN = '1' -- 販促区分：有り
                             AND jutaku_rec.DLVRYDD BETWEEN SHMV.HANSOKUSTRTDD AND SHMV.HANSOKUSHRYDD -- 配達日：販促期間内
                             
                        THEN ( CASE jutaku_rec.DLVRYKBN
                               WHEN '3' THEN SHMV.HANSOKUMOCHIKAERIPRICE -- 販促持ち帰り価格
                                        ELSE SHMV.HANSOKUDLVRYPRICE      -- 販促配達価格
                               END ) 
                        ELSE NULL 
                   END ) HANSOKUUNT
                -- 価格区分
                , ( CASE WHEN SHMV.HYOJUNPRICEKIRIKAEDD IS NULL OR jutaku_rec.DLVRYDD >= SHMV.HYOJUNPRICEKIRIKAEDD 
                
                         THEN SHMV.SHINPRICEKBN -- 新価格区分
                         ELSE SHMV.GENZPRICEKBN -- 現価格区分
                    END ) PRICEKBN 
                -- オープン価格：配達区分、受注日より設定
                , ( CASE WHEN SHMV.OPNPRICEKIRIKAEDD IS NULL OR jutaku_rec.DLVRYDD >= SHMV.OPNPRICEKIRIKAEDD 
                
                         THEN ( CASE jutaku_rec.DLVRYKBN 
                                WHEN '3' THEN SHMV.SHINOPNMOCHIKAERIPRICE -- 新オーブン持ち帰り価格
                                         ELSE SHMV.SHINOPNPRICE           -- 新オーブン価格
                                END ) 
                         ELSE ( CASE jutaku_rec.DLVRYKBN 
                                WHEN '3' THEN SHMV.GENZOPNMOCHIKAERIPRICE -- 現オーブン持ち帰り価格
                                         ELSE SHMV.GENZOPNPRICE           -- 現オーブン価格
                                END ) 
                    END ) OPNUNT  
                , SHMV.TAXKBN       -- 税区分
                , SHMV.TAXSETTEIKBN -- 税設定区分
                
                , SHMV.STKBN        -- セット区分
                
             INTO wRecordS04T002T.LCTGRYCD -- 大分類コード
                , wRecordS04T002T.MCTGRYCD -- 中分類コード
                , wRecordS04T002T.SCTGRYCD -- 小分類コード
                , wRecordS04T002T.HYOJUNUNT -- 標準価格
                , wHansokuunt -- 販促単価
                , wPricekbn   -- 価格区分
                , wOpenUnt    -- オーブン価格
                , wRecordS04T002T.TAXKBN -- 税区分
                , wTaxsetteikbn          -- 税設定区分
                
                , wStkbn
                
             FROM S02V001T SHMV 
            WHERE SHMV.APKIHONCD = itaku_rec.APKIHONCD 
              AND SHMV.SHOPCD    = itaku_rec.SHOPCD 
              AND SHMV.SHNCD     = wRecordS04T002T.SHNCD 
              AND SHMV.SHOPYUKOKBN = '1' 
           ;
           
-- インボイス対応変更 start ABI
--           IF wRecordS04T002T.TAXKBN IN ('4','5') AND jutaku_rec.DLVRYJISSEKIDD <= '20190930' THEN
           IF wRecordS04T002T.TAXKBN IN ('4') AND jutaku_rec.DLVRYJISSEKIDD <= '20190930' THEN
-- インボイス対応変更 end ABI
             wRecordS04T002T.TAXKBN := '1';
           END IF;
           -- 展開行の場合
           IF wRecordS04T002T.NYURYOKUMEISAIFLG = '0' THEN 
-- インボイス対応変更 start ABI
--             IF wOya.TAXKBN <> '5' THEN
--               -- 親商品の税区分で登録
--               wRecordS04T002T.TAXKBN := wOya.TAXKBN;
--             END IF;
             -- 親商品の税区分で登録
             wRecordS04T002T.TAXKBN := wOya.TAXKBN;
-- インボイス対応変更 end ABI
           END IF;
           
           -- 納品単価
           BEGIN 
             -- 既存明細の場合：受注単価を使う
             -- 既存明細で
             IF NOT wIsShnki AND
                -- ACCIS2023-7212 セット商品の納品単価は処理時点のマスタの内容で再設定するように条件を追加
                -- セット商品以外（セット商品は納品単価をマスタから取得して設定し直す）
                wRecordS04T002T.STSHNCD IS NULL AND
                -- 配達日が変更されていないか
                 (jutaku_rec.DLVRYYTIDD = jutaku_rec.DLVRYJISSEKIDD
                OR
                -- 配達日が変更されていて
                 (jutaku_rec.DLVRYYTIDD != jutaku_rec.DLVRYJISSEKIDD
                 -- 手入力可能な得意先か
                 AND ((tokuisakiMST.TOKUISAKIKBN = '2' OR tokuisakiMST.TOKUISAKIKBN = '3' OR tokuisakiMST.TOKUISAKIKBN = '4' OR tokuisakiMST.TOKUISAKIKBN = '9')
                 -- 価格区分がオープン価格
                  OR wPricekbn = '2')))
              THEN 
                wRecordS04T002T.NOHINUNT := wRecordS04T002T.ACPTUNT; 
--             -- 新規明細の場合：納品単価を計算する
--             --    入力明細の場合
             ELSIF wRecordS04T002T.NYURYOKUMEISAIFLG = '1' THEN 
                wRecordS04T002T.NOHINUNT := 0; -- 初期値
                -- 計上区分が売上：得意先別商品別単価／ショップ価格
                IF wRecordS04T002T.KEIJOKBN = '01' THEN 
                   
                   -- セット商品の場合
                   IF wStkbn = '1' THEN
                     BEGIN
                       SELECT 
                           CASE 
                               WHEN jutaku_rec.DLVRYKBN = '3' THEN TSSM.MOCHIKAERIPRICE       -- 持ち帰り価格
                               ELSE TSSM.DLVRYPRICE                                           -- 配達価格
                           END
                       INTO 
                           wRecordS04T002T.NOHINUNT
                       FROM 
                           S02M066T TSSM 
                           INNER JOIN S02M013T TKSM ON ( 
                                   TKSM.APKIHONCD              = TSSM.APKIHONCD 
                               AND TKSM.SHOPCD                 = TSSM.SHOPCD 
                               AND TKSM.TOKUISAKICD            = TSSM.TOKUISAKICD 
                               AND TKSM.RKNKINDTOKUBETSUUNTFLG = '1'
                           )
                       WHERE 
                           TSSM.APKIHONCD   = itaku_rec.APKIHONCD                             -- ＡＰ基本コード
                       AND TSSM.SHOPCD      = itaku_rec.SHOPCD                                -- ショップコード
                       AND TSSM.STSHNCD     = wRecordS04T002T.STSHNCD                         -- セット商品コード
                       AND TSSM.TOKUISAKICD = itaku_rec.TOKUISAKICD                           -- 得意先コード
                       AND jutaku_rec.DLVRYDD BETWEEN TSSM.TEKIYOSTRTDD AND TSSM.TEKIYOSHRYDD -- 配送予定日：販促期間内
                       AND TSSM.YUKOKBN     = '1' 
                       ;
                     EXCEPTION 
                       WHEN NO_DATA_FOUND THEN 
                         wRecordS04T002T.NOHINUNT := wRecordS04T002T.HYOJUNUNT;
                       WHEN TOO_MANY_ROWS THEN
                         RAISE_APPLICATION_ERROR(-20016, '得意先別セット単価が複数見つかりました。');
                     END;
                   ELSE
                   
                   BEGIN 
                     -- 得意先別商品別単価
                     SELECT ( CASE jutaku_rec.DLVRYKBN WHEN '3' THEN TSTM.MOCHIKAERIPRICE -- 持ち帰り価格
                                                                ELSE TSTM.DLVRYPRICE      -- 配達価格
                              END ) TOKUISAKIUNT
                       INTO wRecordS04T002T.NOHINUNT 
                       FROM S02M009T TSTM 
                       INNER JOIN S02M013T TKSM 
                       ON ( TKSM.APKIHONCD = TSTM.APKIHONCD AND TKSM.SHOPCD = TSTM.SHOPCD 
                           AND TKSM.TOKUISAKICD = TSTM.TOKUISAKICD 
                           
                           AND TKSM.RKNKINDTOKUBETSUUNTFLG = '1' -- 料金種別特別単価フラグ：1 
                           
                          )
                      WHERE TSTM.APKIHONCD   = itaku_rec.APKIHONCD -- ＡＰ基本コード
                        AND TSTM.SHOPCD      = itaku_rec.SHOPCD    -- ショップコード
                        AND TSTM.SHNCD       = wRecordS04T002T.SHNCD -- 商品コード
                        AND TSTM.TOKUISAKICD = itaku_rec.TOKUISAKICD -- 得意先コード
                        
                        AND jutaku_rec.DLVRYDD BETWEEN TSTM.TEKIYOSTRTDD AND TSTM.TEKIYOSHRYDD -- 配達日：適用期間内
                        
                        AND TSTM.YUKOKBN = '1' 
                     ;
                     -- 得意先別商品別単価がない、売上の場合：標準／オーブン単価
                     EXCEPTION 
                       WHEN NO_DATA_FOUND THEN 
                            wRecordS04T002T.NOHINUNT  := ( CASE WHEN wPricekbn = '1' THEN wRecordS04T002T.HYOJUNUNT -- 標準価格 
                                                                ELSE wOpenUnt -- オーブン価格
                                                           END ); 
                       WHEN TOO_MANY_ROWS THEN
                            RAISE_APPLICATION_ERROR(-20016, '得意先別単価が複数見つかりました。');
                   END;
                   
                   END IF;
                   
                END IF;
                -- 計上区分がキャンペーン：販促単価／0 
                IF wRecordS04T002T.KEIJOKBN = '02' THEN
                   wRecordS04T002T.NOHINUNT := NVL(wHansokuunt, 0); 
                END IF; 
             --    展開行の場合
             ELSE 
               BEGIN 
                 SELECT TNKM_ITAKU.KEIJOKBN -- 計上区分
                      , TNKM_ITAKU.SURYO * wOya.NOHINSURYO
                      -- 納品単価
                      , ( CASE WHEN wOya.KEIJOKBN IN ( '01', '02' ) 
                               THEN ( CASE jutaku_rec.DLVRYKBN 
                                      WHEN '3' THEN TNKM_ITAKU.MOCHIKAERIPRICE -- 持ち帰り価格
                                               ELSE TNKM_ITAKU.DLVRYPRICE      -- 配達価格
                                      END ) 
                               ELSE 0 
                          END ) NOHINUNT 
                   INTO wKidKeijokbn -- 計上区分
                      , wRecordS04T002T.NOHINSURYO -- 納品数量
                      , wRecordS04T002T.NOHINUNT   -- 納品単価
                   FROM S02M008T TNKM_ITAKU
                        INNER JOIN S02M008T TNKM_JUTAKU
                        ON ( TNKM_JUTAKU.APKIHONCD   = jutaku_rec.APKIHONCD    -- ＡＰ基本コード（受託側）
                             AND TNKM_JUTAKU.STSHNCD = wRecordS04T002T.STSHNCD -- セット商品コード
                             AND TNKM_JUTAKU.RENBAN            = TNKM_ITAKU.RENBAN            -- 連番
                             AND TNKM_JUTAKU.KOSEISHNAPKIHONCD = TNKM_ITAKU.KOSEISHNAPKIHONCD -- 構成商品ＡＰ基本コード
                             AND TNKM_JUTAKU.KOSEISHNCD        = TNKM_ITAKU.KOSEISHNCD        -- 構成商品コード
                             AND TNKM_JUTAKU.YUKOKBN = '1' -- 有効区分
                        )
                  WHERE TNKM_ITAKU.APKIHONCD  = itaku_rec.APKIHONCD -- ＡＰ基本コード
                    AND TNKM_ITAKU.STSHNCD    = wRecordS04T002T.STSHNCD -- セット商品コード
                    AND TNKM_ITAKU.KOSEISHNCD = wRecordS04T002T.SHNCD   -- 構成商品コード
                    AND TNKM_ITAKU.RENBAN     = wRecordS04T002T.ACPTDNPYMEISAIEDANO     -- 連番
                    AND TNKM_ITAKU.YUKOKBN    = '1' -- 有効区分
                 ;
                 wRecordS04T002T.KEIJOKBN := wOya.KEIJOKBN; -- 消費税計算用計上区分 
                 EXCEPTION 
                   -- 構成チェック
                   WHEN NO_DATA_FOUND THEN
                     RAISE_APPLICATION_ERROR(-20017, '委託と受託のセット商品構成が同じではありません。');
                   -- 複数件チェック
                   WHEN TOO_MANY_ROWS THEN
                     RAISE_APPLICATION_ERROR(-20018, '同一セット商品に同じ構成商品が複数見つかりました。');
               END; 
               
               -- 適用期間内の得意先別セット商品単価がある場合は単価にセットする
               BEGIN
                    SELECT 
                        CASE 
                            WHEN wOya.KEIJOKBN IN ('01', '02') THEN
                                CASE jutaku_rec.DLVRYKBN
                                    WHEN '3' THEN TSTM.MOCHIKAERIPRICE -- 持ち帰り価格
                            ELSE TSTM.DLVRYPRICE                                          -- 配達価格
                        END
                            ELSE 0
                        END
                    INTO 
                        wRecordS04T002T.NOHINUNT
                    FROM 
                        S02M066T TSSM 
                        INNER JOIN S02M013T TKSM ON ( 
                                TKSM.APKIHONCD              = TSSM.APKIHONCD 
                            AND TKSM.SHOPCD                 = TSSM.SHOPCD 
                            AND TKSM.TOKUISAKICD            = TSSM.TOKUISAKICD 
                            AND TKSM.RKNKINDTOKUBETSUUNTFLG = '1'
                        )
                        INNER JOIN S02M067T TSTM ON ( 
                                TSTM.APKIHONCD              = TSSM.APKIHONCD 
                            AND TSTM.SHOPCD                 = TSSM.SHOPCD 
                            AND TSTM.STSHNCD                = TSSM.STSHNCD 
                            AND TSTM.TOKUISAKICD            = TSSM.TOKUISAKICD 
                            AND TSTM.TEKIYOSTRTDD           = TSSM.TEKIYOSTRTDD 
                            AND TSTM.SHNCD                  = wRecordS04T002T.SHNCD 
                            AND TSTM.RENBAN                 = wRecordS04T002T.ACPTDNPYMEISAIEDANO     -- 連番
                            AND TSTM.YUKOKBN                = '1'
                        )
                    WHERE 
                        TSSM.APKIHONCD   = itaku_rec.APKIHONCD                            -- ＡＰ基本コード
                    AND TSSM.SHOPCD      = itaku_rec.SHOPCD                               -- ショップコード
                    AND TSSM.STSHNCD     = wRecordS04T002T.STSHNCD                        -- セット商品コード
                    AND TSSM.TOKUISAKICD = itaku_rec.TOKUISAKICD                          -- 得意先コード
                    AND jutaku_rec.DLVRYDD BETWEEN TSSM.TEKIYOSTRTDD AND TSSM.TEKIYOSHRYDD -- 配送日：適用期間内
                    AND TSSM.YUKOKBN     = '1' 
                    ;
               EXCEPTION 
                 WHEN NO_DATA_FOUND THEN
                    NULL;
                 WHEN TOO_MANY_ROWS THEN
                    RAISE_APPLICATION_ERROR(-20016, '得意先別セット単価が複数見つかりました。');
               END;
               
             END IF;
           END; 
           
           -- 納品金額
-- 本線対応変更 start ABI
--           wRecordS04T002T.NOHINKIN := S01SyohizeiService.roundValue(wRecordS04T002T.NOHINUNT * wRecordS04T002T.NOHINSURYO, vUrikinhasukeisankbn); 
           wRecordS04T002T.NOHINKIN := Z1808B01.roundValue(wRecordS04T002T.NOHINUNT * wRecordS04T002T.NOHINSURYO, vUrikinhasukeisankbn); 
-- 本線対応変更 end ABI

           -- 納品消費税
           setTax( wRecordS04T002T -- 明細行 
                 , wRecordS04T002T.NOHINSURYO -- 数量
                 , wRecordS04T002T.NOHINUNT   -- 単価
                 , wTaxsetteikbn  -- 税設定区分
                 , itaku_rec.TAXKEISANKBN -- 税計算区分
                 , jutaku_rec.DLVRYDD  -- 切替日：配達日
                 , jutaku_rec.DLVRYKBN -- 配達区分
                 , wRecordS04T002T.NOHINUNTSHOHITAXKIN -- 単価消費税
                 , wRecordS04T002T.NOHINSHOHITAXKIN    -- 消費税
-- インボイス対応追加 start ABI
                 , wRtnsankoushohitaxkin               -- 参考消費税
                 , iInvoiceFlg                         -- インボイス施行状況区分
-- インボイス対応追加 end ABI
                 );
          -- 展開行の場合
          IF wRecordS04T002T.NYURYOKUMEISAIFLG = '0' THEN 
             IF wTaxsetteikbn = '1' AND (wOya.TAXKBN = '2' OR wOya.TAXKBN = '3') THEN -- 非課税（親税区分：非課税、子税設定区分：する）
                wRecordS04T002T.NOHINUNTSHOHITAXKIN := 0;
                wRecordS04T002T.NOHINSHOHITAXKIN    := 0;
             END IF;
-- インボイス対応変更 start ABI
--             IF wOya.TAXKBN <> '5' THEN
--               -- 親商品の税区分で登録
--               wRecordS04T002T.TAXKBN := wOya.TAXKBN;
--             END IF;
             -- 親商品の税区分で登録
             wRecordS04T002T.TAXKBN := wOya.TAXKBN;
-- インボイス対応変更 end ABI
             IF wRecordS04T002T.KEIJOKBN IN ( '01', '02' ) 
                AND wIsShnki -- 新規の場合のみ子商品行作り直す
                THEN 
                -- 有料の場合のみ：展開マスタの計上区分で登録
                wRecordS04T002T.KEIJOKBN := wKidKeijokbn;
             END IF;
-- インボイス対応追加 start ABI
             -- インボイス施行後の場合、展開行の受注消費税金額、納品消費税金額に0を設定する
             IF iInvoiceFlg = '1' THEN
                wRecordS04T002T.ACPTSHOHITAXKIN  := 0;
                wRecordS04T002T.NOHINSHOHITAXKIN := 0;
             END IF;
-- インボイス対応追加 end ABI
          END IF;
        END IF; 
        
        -- 配布物の場合
        IF wRecordS04T002T.MEISAIKBN = '1' THEN 
           BEGIN 
             -- 配布物属性の取得 
             SELECT HFBM.HAIFUBUTSUZOKUSEI 
               INTO wHaifubutsuzokusei 
               FROM S02M035T HFBM 
              WHERE HFBM.APKIHONCD    = wRecordS04T002T.HAIFUBUTSUAPKIHONCD 
                AND HFBM.SHOPCD       = wRecordS04T002T.HAIFUBUTSUSHOPCD 
                AND HFBM.HAIFUBUTSUCD = wRecordS04T002T.HAIFUBUTSUCD 
                AND HFBM.YUKOKBN = '1' 
             ;
             -- 本部配布物の場合 
             IF wHaifubutsuzokusei = '0' THEN 
                wRecordS04T002T.HAIFUBUTSUAPKIHONCD := itaku_rec.APKIHONCD; 
                wRecordS04T002T.HAIFUBUTSUSHOPCD    := itaku_rec.SHOPCD; 
             END IF;
             EXCEPTION 
               WHEN NO_DATA_FOUND THEN 
                 RAISE_APPLICATION_ERROR(-20019, '配布物がマスタに存在しません。');
           END; 
        END IF; 
    
        -- 受注配達明細テーブルに登録
        wRecordS04T002T.TOSHAAPKIHONCD := userinfo_rec.APKIHONCD; -- 登録者ＡＰ基本コード
        wRecordS04T002T.TOSHASHOPCD    := userinfo_rec.SHOPCD;    -- 登録者ショップコード
        wRecordS04T002T.TOSHACD        := userinfo_rec.TNSHACD;        -- 登録者コード
        wRecordS04T002T.INPTSYSTMYMD   := userinfo_rec.SYSTMYMD;   -- 登録システム日付
        wRecordS04T002T.INPTSYSTMJIKAN := userinfo_rec.SYSTMJIKAN; -- 登録システム時間
        wRecordS04T002T.INPTUNYOYMD    := userinfo_rec.UNYOYMD;    -- 登録運用日付
        wRecordS04T002T.INPTIPADRSE    := userinfo_rec.IPADRSE;    -- 登録ＩＰアドレス
        wRecordS04T002T.INPTPGMID      := userinfo_rec.PGMID;      -- 登録プログラムＩＤ
        
        wRecordS04T002T.KOSHAAPKIHONCD := userinfo_rec.APKIHONCD; -- 更新者ＡＰ基本コード
        wRecordS04T002T.KOSHASHOPCD    := userinfo_rec.SHOPCD;    -- 更新者ショップコード
        wRecordS04T002T.KOSHACD        := userinfo_rec.TNSHACD;        -- 更新者コード
        wRecordS04T002T.KSHNSYSTMYMD   := userinfo_rec.SYSTMYMD;   -- 更新システム日付
        wRecordS04T002T.KSHNSYSTMJIKAN := userinfo_rec.SYSTMJIKAN; -- 更新システム時間
        wRecordS04T002T.KSHNUNYOYMD    := userinfo_rec.UNYOYMD;    -- 更新運用日付
        wRecordS04T002T.KSHNIPADRSE    := userinfo_rec.IPADRSE;    -- 更新ＩＰアドレス
        wRecordS04T002T.KSHNPGMID      := userinfo_rec.PGMID;      -- 更新プログラムＩＤ
        
        INSERT INTO S04T002T JHMT VALUES wRecordS04T002T;

        -- ポイント制度対応 start ABI
        -- 展開行を除外
        IF wRecordS04T002T.NYURYOKUMEISAIFLG = '1' THEN 
          -- 引数ワーク（S20T002T)登録
          INSERT INTO S20W002T VALUES (
              itaku_rec.APKIHONCD                  -- ＡＰ基本コード
            , itaku_rec.SHOPCD                     -- ショップコード
            , userinfo_rec.APKIHONCD               -- 担当者ＡＰ基本コード
            , userinfo_rec.SHOPCD                  -- 担当者ショップコード
            , userinfo_rec.TNSHACD                 -- 担当者コード
            , userinfo_rec.PGMID                   -- 処理ID
            , userinfo_rec.IPADRSE                 -- ＩＰアドレス
            , wGyoban                              -- 行番号
            , wRecordS04T002T.ACPTDNPYMEISAINO     -- 伝票明細番号
            , wRecordS04T002T.ACPTDNPYMEISAIEDANO  -- 伝票明細枝番号
            , wRecordS04T002T.SHNCD                -- 商品コード
            , wRecordS04T002T.KEIJOKBN             -- 計上区分
            , wRecordS04T002T.NOHINUNT             -- 商品単価
            , null                                 -- 画面からの発生
            , wRecordS04T002T.NOHINSURYO           -- 数量
            , wRecordS04T002T.MEISAIBIKO           -- 明細備考
            , null                                 -- 予定発生ポイント明細の管理番号
            , null                                 -- 予定発生ポイント明細の連番
            , null                                 -- 売上還元ポイントマスタの取得種別
            , null                                 -- 売上還元ポイントマスタの取得プランコード
            , '1'                                  -- 有効区分
            , userinfo_rec.APKIHONCD               -- 登録者ＡＰ基本コード
            , userinfo_rec.SHOPCD                  -- 登録者ショップコード
            , userinfo_rec.TNSHACD                 -- 登録者コード
            , userinfo_rec.SYSTMYMD                -- 登録システム日付
            , userinfo_rec.SYSTMJIKAN              -- 登録システム時間
            , userinfo_rec.UNYOYMD                 -- 登録運用日付
            , userinfo_rec.IPADRSE                 -- 登録ＩＰアドレス
            , userinfo_rec.PGMID                   -- 登録プログラムＩＤ
            , userinfo_rec.APKIHONCD               -- 更新者AP基本コード
            , userinfo_rec.SHOPCD                  -- 更新者ショップコード
            , userinfo_rec.TNSHACD                 -- 更新者コード
            , userinfo_rec.SYSTMYMD                -- 更新システム日付
            , userinfo_rec.SYSTMJIKAN              -- 更新システム時間
            , userinfo_rec.UNYOYMD                 -- 更新運用日付
            , userinfo_rec.IPADRSE                 -- 更新ＩＰアドレス
            , userinfo_rec.PGMID);                 -- 更新プログラムＩＤ

          wGyoban := wGyoban + 1;
        END IF;
        -- ポイント制度対応追加 end ABI

        -- 親商品情報更新
        IF wRecordS04T002T.NYURYOKUMEISAIFLG = '1' THEN 
           wKidAcptTaxSum  := 0; -- 子商品受注消費税合計
           wKidNohinTaxSum := 0; -- 子商品納品消費税合計
           wKidLastNo      := wRecordS04T002T.ACPTDNPYMEISAIEDANO; -- 子商品最終明細枝番号
           wOya := wRecordS04T002T; 
        ELSE 
           wKidAcptTaxSum  := wKidAcptTaxSum + wRecordS04T002T.ACPTSHOHITAXKIN;   -- 子商品受注消費税合計
           wKidNohinTaxSum := wKidNohinTaxSum + wRecordS04T002T.NOHINSHOHITAXKIN; -- 子商品納品消費税合計
           wKidLastNo      := wRecordS04T002T.ACPTDNPYMEISAIEDANO; -- 子商品最終明細枝番号
        END IF;
        
        -- 本数、金額、サーバー合計 
        addAll( wRecordS04T002T -- 合計対象
              , wOya.KEIJOKBN   -- 計上区分
              , wWatercd -- 製品水大分類
              , wServerL -- サーバー大分類
              , wServerM -- サーバー本体中分類
              , sum_rec -- 合計データ
-- インボイス対応追加 start ABI
              , iInvoiceFlg -- インボイス施行状況区分
-- インボイス対応追加 end ABI
        ); 
        
      END LOOP; 

    -- ポイント制度対応追加 start ABI
    registPointMeisaiTable(itaku_rec, jutaku_rec, wRecordS04T002T);
    -- ポイント制度対応追加 end ABI

-- インボイス対応追加 start ABI
      -- 消費税金額合計の設定
      -- 受注一般消費税金額
-- 本線対応変更 start ABI
--      sum_rec.ACPTIPPANTAXKIN := S01SyohizeiService.SyohizeiKeisan('1'                        -- 単体計算フラグ（する）
      sum_rec.ACPTIPPANTAXKIN := Z1808B01.SyohizeiKeisan(        '1'                          -- 単体計算フラグ（する）
-- 本線対応変更 end ABI
                                                                 , wTaxsetteikbn              -- 税設定区分
                                                                 , '1'                        -- 税区分（課税）
                                                                 , itaku_rec.TAXKEISANKBN     -- 税計算区分
                                                                 , jutaku_rec.DLVRYDD         -- 切り替え日
                                                                 , jutaku_rec.APKIHONCD       -- AP基本コード
                                                                 , jutaku_rec.SHOPCD          -- ショップコード
                                                                 , NULL                       -- 仕入先コード
                                                                 , NULL                       -- 数量
                                                                 , sum_rec.ACPTIPPANNUKIKIN); -- 金額
      -- 納品一般消費税金額
-- 本線対応変更 start ABI
--      sum_rec.NOHINIPPANTAXKIN := S01SyohizeiService.SyohizeiKeisan('1'                        -- 単体計算フラグ（する）
      sum_rec.NOHINIPPANTAXKIN := Z1808B01.SyohizeiKeisan(       '1'                           -- 単体計算フラグ（する）
-- 本線対応変更 end ABI
                                                                 , wTaxsetteikbn               -- 税設定区分
                                                                 , '1'                         -- 税区分（課税）
                                                                 , itaku_rec.TAXKEISANKBN      -- 税計算区分
                                                                 , jutaku_rec.DLVRYDD          -- 切り替え日
                                                                 , jutaku_rec.APKIHONCD        -- AP基本コード
                                                                 , jutaku_rec.SHOPCD           -- ショップコード
                                                                 , NULL                        -- 仕入先コード
                                                                 , NULL                        -- 数量
                                                                 , sum_rec.NOHINIPPANNUKIKIN); -- 金額
      -- 受注軽減消費税金額
-- 本線対応変更 start ABI
--      sum_rec.ACPTKEIGENTAXKIN := S01SyohizeiService.SyohizeiKeisan('1'                        -- 単体計算フラグ（する）
      sum_rec.ACPTKEIGENTAXKIN := Z1808B01.SyohizeiKeisan(       '1'                           -- 単体計算フラグ（する）
-- 本線対応変更 end ABI
                                                                 , wTaxsetteikbn               -- 税設定区分
                                                                 -- ACCIS2023-7212 軽減税率の税区分の値が間違っているため修正
                                                                 , '4'                         -- 税区分（課税）
                                                                 , itaku_rec.TAXKEISANKBN      -- 税計算区分
                                                                 , jutaku_rec.DLVRYDD          -- 切り替え日
                                                                 , jutaku_rec.APKIHONCD        -- AP基本コード
                                                                 , jutaku_rec.SHOPCD           -- ショップコード
                                                                 , NULL                        -- 仕入先コード
                                                                 , NULL                        -- 数量
                                                                 , sum_rec.ACPTKEIGENNUKIKIN); -- 金額
      -- 納品軽減消費税金額
-- 本線対応変更 start ABI
--      sum_rec.NOHINKEIGENTAXKIN := S01SyohizeiService.SyohizeiKeisan('1'                        -- 単体計算フラグ（する）
      sum_rec.NOHINKEIGENTAXKIN := Z1808B01.SyohizeiKeisan('1'                        -- 単体計算フラグ（する）
-- 本線対応変更 end ABI
                                                                 , wTaxsetteikbn                -- 税設定区分
                                                                 -- ACCIS2023-7212 軽減税率の税区分の値が間違っているため修正
                                                                 , '4'                          -- 税区分（課税）
                                                                 , itaku_rec.TAXKEISANKBN       -- 税計算区分
                                                                 , jutaku_rec.DLVRYDD           -- 切り替え日
                                                                 , jutaku_rec.APKIHONCD         -- AP基本コード
                                                                 , jutaku_rec.SHOPCD            -- ショップコード
                                                                 , NULL                         -- 仕入先コード
                                                                 , NULL                         -- 数量
                                                                 , sum_rec.NOHINKEIGENNUKIKIN); -- 金額
        -- インボイス施行日以降の場合、受注消費税金額、納品消費税金額を再設定
        IF iInvoiceFlg = '1' THEN
           sum_rec.ACPTSHOHITAXKIN   := sum_rec.ACPTIPPANTAXKIN + sum_rec.ACPTKEIGENTAXKIN;   -- 受注消費税金額
           sum_rec.NOHINSHOHITAXKIN  := sum_rec.NOHINIPPANTAXKIN + sum_rec.NOHINKEIGENTAXKIN; -- 納品消費税金額
        END IF;
-- インボイス対応追加 end ABI

    CLOSE SELECT_JUTAKUMEISAI; 
    CLOSE SELECT_ITAKUMEISAI; -- 変更前委託側明細データ
    
    EXCEPTION 
      WHEN OTHERS THEN 
        IF SELECT_JUTAKUMEISAI%ISOPEN THEN 
           CLOSE SELECT_JUTAKUMEISAI; 
        END IF; 
        IF SELECT_ITAKUMEISAI%ISOPEN THEN 
           CLOSE SELECT_ITAKUMEISAI; 
        END IF; 
        RAISE_APPLICATION_ERROR(-20020, SQLERRM || '受注配達明細データ作成時　エラーが発生しました。'); 
  END processJuchuMeisai; 
  
  /*****************************************************************************/
  /* プロシージャ名：再配達受注配達明細データ作成処理                                */
  /* 引数             ：itaku_rec      委託データ                                */
  /*                  ：jutaku_rec     受託データ                               */
  /* インボイス対応追加 start ABI                                               */
  /* 引数（IN OUT）   ：iInvoiceFlg    インボイス施行状況区分                   */
  /* インボイス対応追加 end ABI                                                 */
  /*****************************************************************************/
  PROCEDURE processSaihaitatsuMeisai( itaku_rec    IN itaku_rec_type  -- 委託データ
                                    , jutaku_rec   IN jutaku_rec_type -- 受託データ
-- インボイス対応追加 start ABI
                                    , iInvoiceFlg  IN OUT VARCHAR2 -- インボイス施行状況区分
-- インボイス対応追加 end ABI
  ) IS 
  /* 変数宣言 */ 
  wRecordS04T002T           S04T002T%rowtype := NULL; -- 受注配達明細テーブル
  wHaifubutsuzokusei            VARCHAR2(1)  := NULL; -- 配布物属性
  -- セット商品展開用
  wOya                      S04T002T%rowtype := NULL;  -- 親明細行
  wKidAcptTaxSum                NUMBER(11,0) := 0;     -- 子商品受注消費税合計
  wKidNohinTaxSum               NUMBER(11,0) := 0;     -- 子商品納品消費税合計
  wKidLastNo                    NUMBER(2,0)  := 0;     -- 子商品最終明細枝番号
  wKidKeijokbn                  VARCHAR2(2)  := NULL;  -- 展開マスタ計上区分（登録用）
  -- 本数、金額、サーバー合計用
  wWatercd                      VARCHAR2(2)  := NULL;  -- 製品水大分類コード
  wIsYuryo                      BOOLEAN      := FALSE; -- 有料フラグ
  wTaxkbn                       VARCHAR2(1)  := NULL;  --税区分
  vUrikinhasukeisankbn          VARCHAR2(1)  := NULL;  -- 売上金額端数計算区分
-- インボイス対応追加 start ABI
  vInvoicshikoymd               VARCHAR2(8)  := NULL;  -- インボイス施行日
  wRtnsankoushohitaxkin         VARCHAR2(11) := NULL;  -- 参考消費税金額（返却用）
-- インボイス対応追加 end ABI

  /* CURSOR：受託明細データ抽出 */
  CURSOR SELECT_JUTAKUMEISAI IS 
    SELECT JHMT_JUTAKU.* 
      FROM S04T002T JHMT_JUTAKU 
     WHERE JHMT_JUTAKU.APKIHONCD  = jutaku_rec.APKIHONCD  
       AND JHMT_JUTAKU.SHOPCD     = jutaku_rec.SHOPCD 
       AND JHMT_JUTAKU.ACPTDNPYNO = jutaku_rec.ACPTDNPYNO 
       AND JHMT_JUTAKU.EDANO      = jutaku_rec.EDANO 
       AND JHMT_JUTAKU.YUKOKBN = '1'
       AND JHMT_JUTAKU.MEISAIKBN <> '3' -- 消費税調整以外の明細を対象とする
    FOR UPDATE 
  ; 
  BEGIN 
    
    -- 合計項目初期化 
    sum_rec.DLVRYRGRAYUSYOHNS := 0; -- 配達レギュラー有償本数
    sum_rec.DLVRYRGRAMUSHOHNS := 0; -- 配達レギュラー無償本数
    sum_rec.DLVRYSRMYUSYOHNS  := 0; -- 配達スリム有償本数
    sum_rec.DLVRYSRMMUSHOHNS  := 0; -- 配達スリム無償本数
    sum_rec.DLVRYSTYSHNS      := 0; -- 配達その他有償本数
    sum_rec.DLVRYSTMSHNS      := 0; -- 配達その他無償本数
    sum_rec.DLVRYSHSUAKIN     := 0; -- 配達製品水売上金額
    sum_rec.DLVRYSONOTAURIKIN := 0; -- 配達その他売上金額
    sum_rec.KURERURITTORUSTUP    := 0; -- クレールＬ設置
    sum_rec.KURERURITTORUHIKIAGE := 0; -- クレールＬ引上
    sum_rec.KURERUESUSTUP        := 0; -- クレールＳ設置;
    sum_rec.KURERUESUHIKIAGE     := 0; -- クレールＳ引上
    sum_rec.SONOTASTUP           := 0; -- その他設置
    sum_rec.SONOTAHIKIAGE        := 0; -- その他引上
    sum_rec.ACPTKIN          := 0; -- 受注金額
    sum_rec.ACPTSHOHITAXKIN  := 0; -- 受注消費税金額
    sum_rec.NOHINKIN         := 0; -- 納品金額
    sum_rec.NOHINSHOHITAXKIN := 0; -- 納品消費税金額
    sum_rec.RGRASURYO := 0; -- 配達レギュラー本数 
    sum_rec.SRMSURYO  := 0; -- 配達スリム本数 

    wWatercd := getMojisetteiatai( '000', '000', '096', '1', NULL, NULL, '商品大分類コード：製品水' );

    -- ショップマスタ取得 
    SELECT
      SHPM.URIKINHASUKEISANKBN  -- 売上金額端数計算区分
-- インボイス対応追加 start ABI
    , SHPM.INVOICSHIKOYMD     -- インボイス施行日
-- インボイス対応追加 end ABI
    INTO vUrikinhasukeisankbn
-- インボイス対応追加 start ABI
       , vInvoicshikoymd        -- インボイス施行日
-- インボイス対応追加 end ABI
    FROM
      S02M046T SHPM 
    WHERE
      SHPM.APKIHONCD = itaku_rec.APKIHONCD 
      AND SHPM.SHOPCD = itaku_rec.SHOPCD  
      AND SHPM.YUKOKBN = '1'
    ;
    
-- インボイス対応追加 start ABI
    -- インボイス施行状況区分の初期化
    iInvoiceFlg := '0';
    -- インボイス施行状況区分の設定（受託データ.配達日がインボイス施行日以降の場合、TRUE）
    IF vInvoicshikoymd <= jutaku_rec.DLVRYDD THEN
      iInvoiceFlg := '1';
    END IF;
-- インボイス対応追加 end ABI

    -- 受託明細データを元に新しい明細データを作る
    OPEN SELECT_JUTAKUMEISAI;
      LOOP 
        FETCH SELECT_JUTAKUMEISAI INTO wRecordS04T002T; 
        IF SELECT_JUTAKUMEISAI%ROWCOUNT = 0 THEN 
           RAISE_APPLICATION_ERROR(-20015, '受託の明細データが存在しません。');
        END IF;
        
-- インボイス対応追加 start ABI
        -- インボイス施行日以前の場合のみ、消費税調整を実施
        IF iInvoiceFlg = '0' THEN
-- インボイス対応追加 end ABI
          -- 消費税調整
          IF ( SELECT_JUTAKUMEISAI%NOTFOUND -- 末行
               OR ( SELECT_JUTAKUMEISAI%ROWCOUNT > 1 AND wRecordS04T002T.NYURYOKUMEISAIFLG = '1' ) -- 入力行
             ) 
             AND wOya.TANPINMEISAIFLG = '0' THEN -- 前回親明細がセット商品
             IF wOya.ACPTSHOHITAXKIN <> wKidAcptTaxSum THEN -- 受注消費税調整
                
                wOya.ACPTDNPYMEISAIEDANO          := wKidLastNo + 1; -- 受注伝票明細枝番号
                wOya.ITAKUAITEACPTDNPYMEISAIEDANO := wKidLastNo + 1; -- 委託相手受注明細枝番号
                -- 消費税調整商品コード取得
                wOya.SHNCD := getMojisetteiatai( '000', '000', '166', NULL, NULL, NULL, '消費税調整商品コード');
                IF wOya.SHNCD IS NULL THEN 
                   RAISE_APPLICATION_ERROR(-20021, '消費税調整商品コード　が設定されていません。'); 
                END IF; 
                -- 商品マスタビューからの商品情報を取得する
                SELECT SHMV.LCTGRYCD -- 大分類コード
                     , SHMV.MCTGRYCD -- 中分類コード
                     , SHMV.SCTGRYCD -- 小分類コード
                     -- 標準価格：配達区分、受注日より設定
                     , ( CASE WHEN SHMV.HYOJUNPRICEKIRIKAEDD IS NULL OR jutaku_rec.ACPTDD >= SHMV.HYOJUNPRICEKIRIKAEDD 
                              THEN ( CASE jutaku_rec.DLVRYKBN 
                                     WHEN '3' THEN SHMV.SHINMOCHIKAERIPRICE -- 新持ち帰り価格
                                              ELSE SHMV.SHINHYOJUNPRICE     -- 新標準価格
                                     END ) 
                              ELSE ( CASE jutaku_rec.DLVRYKBN 
                                     WHEN '3' THEN SHMV.GENZMOCHIKAERIPRICE -- 現持ち帰り価格
                                              ELSE SHMV.GENZHYOJUNPRICE     -- 現標準価格
                                     END ) 
                         END ) HYOJUNUNT 
                     -- 税区分
                     , SHMV.TAXKBN
                  INTO wOya.LCTGRYCD -- 大分類コード
                     , wOya.MCTGRYCD -- 中分類コード
                     , wOya.SCTGRYCD -- 小分類コード
                     , wOya.HYOJUNUNT -- 標準価格
                     , wTaxkbn
                  FROM S02V001T SHMV
                 WHERE SHMV.APKIHONCD   = wOya.APKIHONCD  -- ＡＰ基本コード
                   AND SHMV.SHOPCD      = wOya.SHOPCD     -- ショップコード
                   AND SHMV.SHNCD       = wOya.SHNCD -- 消費税調整商品コード
                   AND SHMV.SHOPYUKOKBN = '1' -- ショップ有効区分
                ;
                wOya.KATASIKI := NULL; -- 型式
-- インボイス対応削除 start ABI
--                IF wOya.TAXKBN IN ('5') THEN
--                  --親商品の税区分が"5:子明細商品"の場合、商品マスタビューの税区分をセット
--                  wOya.TAXKBN := wTaxkbn;
--                END IF;
-- インボイス対応削除 end ABI
-- インボイス対応変更 start ABI
--                IF wOya.TAXKBN IN ('4','5') AND jutaku_rec.DLVRYYTIDD <= '20190930' THEN
                IF wOya.TAXKBN IN ('4') AND jutaku_rec.DLVRYYTIDD <= '20190930' THEN
-- インボイス対応変更 end ABI
                  wOya.TAXKBN := '1';
                END IF;
                -- 受注 
                wOya.ACPTSURYO := 1; -- 受注数量
                wOya.HYOJUNUNT := 0; -- 標準単価
                wOya.ACPTUNT   := 0; -- 受注単価
                wOya.ACPTKIN   := 0; -- 受注金額
                wOya.ACPTUNTSHOHITAXKIN := 0; -- 受注単価消費税金額
                wOya.ACPTSHOHITAXKIN := wOya.ACPTSHOHITAXKIN - wKidAcptTaxSum; -- 受注消費税金額
                -- 納品 
                wOya.NOHINSURYO := 0; -- 納品数量 
                wOya.NOHINUNT := 0; -- 納品単価
                wOya.NOHINKIN := 0; -- 納品金額
                wOya.NOHINUNTSHOHITAXKIN := 0; -- 納品単価消費税金額
                wOya.NOHINSHOHITAXKIN := 0; -- 納品消費税金額
                wOya.YUSYOKBN  := '0'; -- 有償区分
                wOya.MEISAIKBN := '3'; -- 明細区分：消費税調整
                wOya.NYURYOKUMEISAIFLG := '0'; -- 入力明細フラグ：展開行
                wOya.TANPINMEISAIFLG   := '1'; -- 単品明細フラグ：単品
                wOya.URIDNPYMEISAIEDANO := wKidLastNo + 1; -- 売上伝票明細枝番号
                
                -- 登録
                INSERT INTO S04T002T JHMT VALUES wOya;
             END IF; 
          END IF; 

-- インボイス対応追加 start ABI
        END IF;
-- インボイス対応追加 end ABI

        EXIT WHEN SELECT_JUTAKUMEISAI%NOTFOUND; 
        
        -- 委託のデータに切替：基本情報 
        wRecordS04T002T.ITAKUAITEAPKIHONCD  := wRecordS04T002T.APKIHONCD;  -- 委託相手ＡＰ基本コード
        wRecordS04T002T.ITAKUAITESHOPCD     := wRecordS04T002T.SHOPCD;     -- 委託相手ショップコード
        wRecordS04T002T.ITAKUAITEACPTDNPYNO    := wRecordS04T002T.ACPTDNPYNO; -- 委託相手受注伝票番号
        wRecordS04T002T.ITAKUAITEEDANO      := wRecordS04T002T.EDANO;      -- 委託相手枝番号
        wRecordS04T002T.ITAKUAITEACPTDNPYMEISAINO       := wRecordS04T002T.ACPTDNPYMEISAINO;    -- 委託相手受注伝票明細番号
        wRecordS04T002T.ITAKUAITEACPTDNPYMEISAIEDANO := wRecordS04T002T.ACPTDNPYMEISAIEDANO; -- 委託相手受注伝票明細枝番号
        wRecordS04T002T.APKIHONCD  := itaku_rec.APKIHONCD;  -- ＡＰ基本コード
        wRecordS04T002T.SHOPCD     := itaku_rec.SHOPCD;     -- ショップコード
        wRecordS04T002T.ACPTDNPYNO := itaku_rec.ACPTDNPYNO; -- 受注伝票番号
        -- 枝番号
        wRecordS04T002T.EDANO := itaku_rec.EDANO + 1;
        -- 委託のデータに切替：受注単価、金額、消費税
        wRecordS04T002T.ACPTUNT            := NVL(wRecordS04T002T.ITAKUCMPNYACPTUNT, 0);                  -- 受注単価
-- 本線対応変更 start ABI
--       wRecordS04T002T.ACPTKIN            := S01SyohizeiService.roundValue(NVL(wRecordS04T002T.ACPTSURYO,0) * wRecordS04T002T.ACPTUNT, vUrikinhasukeisankbn); -- 受注金額
        wRecordS04T002T.ACPTKIN            := Z1808B01.roundValue(NVL(wRecordS04T002T.ACPTSURYO,0) * wRecordS04T002T.ACPTUNT, vUrikinhasukeisankbn); -- 受注金額
-- 本線対応変更 end ABI
        -- 受注消費税
        setTax( wRecordS04T002T -- 明細行 
             , wRecordS04T002T.ACPTSURYO          -- 数量
             , wRecordS04T002T.ACPTUNT            -- 単価
             , wRecordS04T002T.TAXKBN             -- 税設定区分
             , itaku_rec.TAXKEISANKBN             -- 税計算区分
             , jutaku_rec.DLVRYYTIDD              -- 切替日：配達予定日
             , itaku_rec.DLVRYKBN                 -- 配達区分
             , wRecordS04T002T.ACPTUNTSHOHITAXKIN -- 受注単価消費税金額
             , wRecordS04T002T.ACPTSHOHITAXKIN    -- 受注消費税金額
-- インボイス対応追加 start ABI
             , wRtnsankoushohitaxkin              -- 参考消費税
             , iInvoiceFlg                        -- インボイス施行状況区分
-- インボイス対応追加 end ABI
             );
        wRecordS04T002T.ITAKUCMPNYACPTUNT            := 0; -- 委託会社受注単価
        wRecordS04T002T.ITAKUCMPNYACPTUNTSHOHITAXKIN := 0; -- 委託会社受注単価消費税金額
        WRecordS04T002T.ITAKUCMPNYACPTSHOHITAXKIN    := 0; -- 委託会社受注消費税金額
        -- 委託のデータに切替：売上伝票番号（定例売上用）
        wRecordS04T002T.URIDNPYNO          := NULL; 
        wRecordS04T002T.URIDNPYEDANO       := NULL; 
        wRecordS04T002T.URIDNPYMEISAINO    := NULL; 
        wRecordS04T002T.URIDNPYMEISAIEDANO := NULL; 
        
        -- 商品の場合：納品単価、金額、消費税の取得
        IF wRecordS04T002T.MEISAIKBN = '0' THEN 
           -- 商品情報の取得
           SELECT SHMV.LCTGRYCD -- 大分類コード
                , SHMV.MCTGRYCD -- 中分類コード
                , SHMV.SCTGRYCD -- 小分類コード
                -- 標準価格：配達区分、受注日より設定
                , ( CASE WHEN SHMV.HYOJUNPRICEKIRIKAEDD IS NULL OR jutaku_rec.ACPTDD >= SHMV.HYOJUNPRICEKIRIKAEDD 
                         THEN ( CASE jutaku_rec.DLVRYKBN 
                                WHEN '3' THEN SHMV.SHINMOCHIKAERIPRICE -- 新持ち帰り価格
                                         ELSE SHMV.SHINHYOJUNPRICE     -- 新標準価格
                                END ) 
                         ELSE ( CASE jutaku_rec.DLVRYKBN 
                                WHEN '3' THEN SHMV.GENZMOCHIKAERIPRICE -- 現持ち帰り価格
                                         ELSE SHMV.GENZHYOJUNPRICE     -- 現標準価格
                                END ) 
                    END ) HYOJUNUNT 
                , SHMV.TAXKBN       -- 税区分
             INTO wRecordS04T002T.LCTGRYCD -- 大分類コード
                , wRecordS04T002T.MCTGRYCD -- 中分類コード
                , wRecordS04T002T.SCTGRYCD -- 小分類コード
                , wRecordS04T002T.HYOJUNUNT -- 標準価格
                , wRecordS04T002T.TAXKBN -- 税区分
             FROM S02V001T SHMV 
            WHERE SHMV.APKIHONCD = itaku_rec.APKIHONCD 
              AND SHMV.SHOPCD    = itaku_rec.SHOPCD 
              AND SHMV.SHNCD     = wRecordS04T002T.SHNCD 
              AND SHMV.SHOPYUKOKBN = '1' 
           ;
-- インボイス対応変更 start ABI
--           IF wRecordS04T002T.TAXKBN IN ('4','5') AND jutaku_rec.DLVRYYTIDD <= '20190930' THEN
           IF wRecordS04T002T.TAXKBN IN ('4') AND jutaku_rec.DLVRYYTIDD <= '20190930' THEN
-- インボイス対応変更 end ABI
             wRecordS04T002T.TAXKBN := '1';
           END IF;
           -- 展開行の場合
           IF wRecordS04T002T.NYURYOKUMEISAIFLG = '0' THEN 
-- インボイス対応変更 start ABI
--             IF wOya.TAXKBN <> '5' THEN
--               -- 親商品の税区分で登録
--               wRecordS04T002T.TAXKBN := wOya.TAXKBN;
--             END IF;
             -- 親商品の税区分で登録
             wRecordS04T002T.TAXKBN := wOya.TAXKBN;
-- インボイス対応変更 end ABI
           END IF;
           
           -- 展開行の場合 
           BEGIN 
             IF wRecordS04T002T.NYURYOKUMEISAIFLG = '0' THEN 
               BEGIN 
                 SELECT TNKM_ITAKU.KEIJOKBN -- 計上区分
                      , TNKM_ITAKU.SURYO * wOya.ACPTSURYO 
                   INTO wRecordS04T002T.KEIJOKBN  -- 計上区分 
                      , wRecordS04T002T.ACPTSURYO -- 受注数量
                   FROM S02M008T TNKM_ITAKU
                  WHERE TNKM_ITAKU.APKIHONCD  = itaku_rec.APKIHONCD -- ＡＰ基本コード
                    AND TNKM_ITAKU.STSHNCD    = wRecordS04T002T.STSHNCD -- セット商品コード
                    AND TNKM_ITAKU.KOSEISHNCD = wRecordS04T002T.SHNCD   -- 構成商品コード
                    AND TNKM_ITAKU.RENBAN     = wRecordS04T002T.ACPTDNPYMEISAIEDANO     -- 連番
                    AND TNKM_ITAKU.YUKOKBN    = '1' -- 有効区分
                 ;
                 EXCEPTION 
                   -- 構成チェック
                   WHEN NO_DATA_FOUND THEN
                     RAISE_APPLICATION_ERROR(-20017, '委託と受託のセット商品構成が同じではありません。');
                   -- 複数件チェック
                   WHEN TOO_MANY_ROWS THEN
                     RAISE_APPLICATION_ERROR(-20018, '同一セット商品に同じ構成商品が複数見つかりました。');
               END; 
-- インボイス対応追加 start ABI
               -- インボイス施行後の場合、展開行の受注消費税金額、納品消費税金額に0を設定する
               IF iInvoiceFlg = '1' THEN
                 wRecordS04T002T.ACPTSHOHITAXKIN  := 0;
               END IF;
-- インボイス対応追加 end ABI
             END IF;
           END; 
           
           -- 納品単価、金額、消費税
           wRecordS04T002T.NOHINUNT := 0; 
           wRecordS04T002T.NOHINKIN := 0; 
           wRecordS04T002T.NOHINUNTSHOHITAXKIN := 0; 
           wRecordS04T002T.NOHINSHOHITAXKIN := 0;
           
        END IF; 
        
        -- 配布物の場合
        IF wRecordS04T002T.MEISAIKBN = '1' THEN 
           BEGIN 
             -- 配布物属性の取得 
             SELECT HFBM.HAIFUBUTSUZOKUSEI 
               INTO wHaifubutsuzokusei 
               FROM S02M035T HFBM 
              WHERE HFBM.APKIHONCD    = wRecordS04T002T.HAIFUBUTSUAPKIHONCD 
                AND HFBM.SHOPCD       = wRecordS04T002T.HAIFUBUTSUSHOPCD 
                AND HFBM.HAIFUBUTSUCD = wRecordS04T002T.HAIFUBUTSUCD 
                AND HFBM.YUKOKBN = '1' 
             ;
             -- 本部配布物の場合 
             IF wHaifubutsuzokusei = '0' THEN 
                wRecordS04T002T.HAIFUBUTSUAPKIHONCD := itaku_rec.APKIHONCD; 
                wRecordS04T002T.HAIFUBUTSUSHOPCD    := itaku_rec.SHOPCD; 
             END IF;
             EXCEPTION 
               WHEN NO_DATA_FOUND THEN 
                 RAISE_APPLICATION_ERROR(-20019, '配布物がマスタに存在しません。');
           END; 
        END IF; 
        
        -- 受注配達明細テーブルに登録
        wRecordS04T002T.TOSHAAPKIHONCD := userinfo_rec.APKIHONCD; -- 登録者ＡＰ基本コード
        wRecordS04T002T.TOSHASHOPCD    := userinfo_rec.SHOPCD;    -- 登録者ショップコード
        wRecordS04T002T.TOSHACD        := userinfo_rec.TNSHACD;        -- 登録者コード
        wRecordS04T002T.INPTSYSTMYMD   := userinfo_rec.SYSTMYMD;   -- 登録システム日付
        wRecordS04T002T.INPTSYSTMJIKAN := userinfo_rec.SYSTMJIKAN; -- 登録システム時間
        wRecordS04T002T.INPTUNYOYMD    := userinfo_rec.UNYOYMD;    -- 登録運用日付
        wRecordS04T002T.INPTIPADRSE    := userinfo_rec.IPADRSE;    -- 登録ＩＰアドレス
        wRecordS04T002T.INPTPGMID      := userinfo_rec.PGMID;      -- 登録プログラムＩＤ
        
        wRecordS04T002T.KOSHAAPKIHONCD := userinfo_rec.APKIHONCD; -- 更新者ＡＰ基本コード
        wRecordS04T002T.KOSHASHOPCD    := userinfo_rec.SHOPCD;    -- 更新者ショップコード
        wRecordS04T002T.KOSHACD        := userinfo_rec.TNSHACD;        -- 更新者コード
        wRecordS04T002T.KSHNSYSTMYMD   := userinfo_rec.SYSTMYMD;   -- 更新システム日付
        wRecordS04T002T.KSHNSYSTMJIKAN := userinfo_rec.SYSTMJIKAN; -- 更新システム時間
        wRecordS04T002T.KSHNUNYOYMD    := userinfo_rec.UNYOYMD;    -- 更新運用日付
        wRecordS04T002T.KSHNIPADRSE    := userinfo_rec.IPADRSE;    -- 更新ＩＰアドレス
        wRecordS04T002T.KSHNPGMID      := userinfo_rec.PGMID;      -- 更新プログラムＩＤ
        
        INSERT INTO S04T002T JHMT VALUES wRecordS04T002T;
        
        -- 親商品情報更新
        IF wRecordS04T002T.NYURYOKUMEISAIFLG = '1' THEN 
           wKidAcptTaxSum  := 0; -- 子商品受注消費税合計
           wKidLastNo      := wRecordS04T002T.ACPTDNPYMEISAIEDANO; -- 子商品最終明細枝番号
           wOya := wRecordS04T002T; 
        ELSE 
           wKidAcptTaxSum  := wKidAcptTaxSum + wRecordS04T002T.ACPTSHOHITAXKIN;   -- 子商品受注消費税合計
           wKidLastNo      := wRecordS04T002T.ACPTDNPYMEISAIEDANO; -- 子商品最終明細枝番号
        END IF;
        
        -- 本数、金額、サーバー合計 
        addAllSaihaitatsu( wRecordS04T002T -- 合計対象
                         , wOya.KEIJOKBN -- 計上区分
                         , wWatercd -- 製品水大分類
                         , sum_rec -- 合計データ
-- インボイス対応追加 start ABI
                         , iInvoiceFlg -- インボイス施行状況区分
-- インボイス対応追加 end ABI
        ); 
        
      END LOOP; 

-- インボイス対応追加 start ABI
      -- 消費税金額合計の設定
      -- 受注一般消費税金額
-- 本線対応変更 start ABI
--      sum_rec.ACPTIPPANTAXKIN := S01SyohizeiService.SyohizeiKeisan('1'                        -- 単体計算フラグ（する）
      sum_rec.ACPTIPPANTAXKIN := Z1808B01.SyohizeiKeisan('1'                        -- 単体計算フラグ（する）
-- 本線対応変更 end ABI
                                                                 , '0'                        -- 税設定区分
                                                                 , '1'                        -- 税区分（課税）
                                                                 , itaku_rec.TAXKEISANKBN     -- 税計算区分
                                                                 , jutaku_rec.DLVRYDD         -- 切り替え日
                                                                 , jutaku_rec.APKIHONCD       -- AP基本コード
                                                                 , jutaku_rec.SHOPCD          -- ショップコード
                                                                 , NULL                       -- 仕入先コード
                                                                 , NULL                       -- 数量
                                                                 , sum_rec.ACPTIPPANNUKIKIN); -- 金額
      -- 受注軽減消費税金額
-- 本線対応変更 start ABI
--      sum_rec.ACPTKEIGENTAXKIN := S01SyohizeiService.SyohizeiKeisan('1'                         -- 単体計算フラグ（する）
      sum_rec.ACPTKEIGENTAXKIN := Z1808B01.SyohizeiKeisan('1'                         -- 単体計算フラグ（する）
-- 本線対応変更 end ABI
                                                                  , '0'                         -- 税設定区分
                                                                  , '1'                         -- 税区分（課税）
                                                                  , itaku_rec.TAXKEISANKBN      -- 税計算区分
                                                                  , jutaku_rec.DLVRYDD          -- 切り替え日
                                                                  , jutaku_rec.APKIHONCD        -- AP基本コード
                                                                  , jutaku_rec.SHOPCD           -- ショップコード
                                                                  , NULL                        -- 仕入先コード
                                                                  , NULL                        -- 数量
                                                                  , sum_rec.ACPTKEIGENNUKIKIN); -- 金額
      -- インボイス施行日以降の場合、受注消費税金額を再設定
      IF iInvoiceFlg = '1' THEN
         sum_rec.ACPTSHOHITAXKIN   := sum_rec.ACPTIPPANTAXKIN + sum_rec.ACPTKEIGENTAXKIN;   -- 受注消費税金額
      END IF;
-- インボイス対応追加 end ABI

    CLOSE SELECT_JUTAKUMEISAI; 
    
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR(-20020, SQLERRM || '受注配達明細データ作成時　エラーが発生しました。'); 
  END processSaihaitatsuMeisai; 
  
  /*****************************************************************************/
  /* プロシージャ名：受託配達テーブル配達済更新                                     */
  /* 引数             ：itaku_rec      委託データ                                */
  /*                  ：jutaku_rec     受託データ                               */
  /* インボイス対応追加 start ABI                                               */
  /*                  ：iInvoiceFlg    インボイス施行状況区分                   */
  /* インボイス対応追加 end ABI                                                 */
  /*****************************************************************************/
  PROCEDURE updateJuchuDelivered( itaku_rec    IN itaku_rec_type  -- 委託データ
                                , jutaku_rec   IN jutaku_rec_type -- 受託データ
-- インボイス対応追加 start ABI
                                , iInvoiceFlg  IN VARCHAR2        -- インボイス施行状況区分
-- インボイス対応追加 end ABI
  ) IS 
  BEGIN 
    UPDATE S04T001T JHTT 
       SET /* 受託のデータ */
           JHTT.URIDNPYNO      = itaku_rec.ACPTDNPYNO      -- 売上伝票番号 
         , JHTT.DLVRYJISSEKIDD = jutaku_rec.DLVRYJISSEKIDD -- 配達実績日
         , JHTT.DLVRYDD        = jutaku_rec.DLVRYDD   -- 配達日
           -- 不在置き実績フラグ
         , JHTT.FZAIOKIJISSEKIFLG = ( CASE jutaku_rec.DLVRYSTS WHEN '02' THEN '1' -- 配達ステータス：不在納品
                                                               ELSE '0'           -- その他配達ステータス
                                      END )
         , JHTT.DLVRYKBN = jutaku_rec.DLVRYKBN -- 配達区分
         , JHTT.KZBTLN1N2RITTORUHNS = jutaku_rec.KZBTLN1N2RITTORUHNS -- 回収実績ボトル１２Ｌ本数
         , JHTT.KZBTLN8RITTORUHNS   = jutaku_rec.KZBTLN8RITTORUHNS   -- 回収実績ボトル８Ｌ本数 
         , JHTT.DNPYSTS  = jutaku_rec.DNPYSTS  -- 伝票ステータス 
         , JHTT.DLVRYSTS = jutaku_rec.DLVRYSTS -- 配達ステータス
           /* 委託のデータ */
         , JHTT.DLVRYTNSHACD = itaku_rec.DLVRYTNSHACD -- 配達担当者コード
           -- 配達会社コード
         , JHTT.DLVRYCMPNYCD = ( CASE jutaku_rec.DLVRYKBN WHEN '1' THEN NULL -- 配達区分：自社
                                                          WHEN '3' THEN NULL -- 配達区分：持ち帰り
                                                          ELSE itaku_rec.DLVRYCMPNYCD -- その他配達区分
                                 END )
         , JHTT.DLVRYJISSEKIPRCSDD = userinfo_rec.UNYOYMD -- 配達実績処理日
         , JHTT.DLVRYJISSEKIMONOAPKIHONCD = itaku_rec.APKIHONCD     -- 配達管轄ＡＰ基本コード
         , JHTT.DLVRYJISSEKIMONOSHOPCD    = itaku_rec.SHOPCD        -- 配達管轄ショップコード
         , JHTT.DLVRYJISSEKIPRCSTNSHACD   = itaku_rec.RENKEITNSHACD -- 配達実績処理担当者コード
         , JHTT.DLVRYJUN                  = itaku_rec.DLVRYJUN      -- 配達順
           /* 合計 */
         , JHTT.DLVRYJISSEKIRGRAYUSYOHNS = sum_rec.DLVRYRGRAYUSYOHNS -- 配達実績レギュラー有償本数
         , JHTT.DLVRYJISSEKIRGRAMUSHOHNS = sum_rec.DLVRYRGRAMUSHOHNS -- 配達実績レギュラー無償本数
         , JHTT.DLVRYJISSEKISRMYUSYOHNS  = sum_rec.DLVRYSRMYUSYOHNS  -- 配達実績スリム有償本数
         , JHTT.DLVRYJISSEKISRMMUSHOHNS  = sum_rec.DLVRYSRMMUSHOHNS  -- 配達実績スリム無償本数
         , JHTT.DLVRYJISSEKISTYSHNS      = sum_rec.DLVRYSTYSHNS      -- 配達実績その他有償本数
         , JHTT.DLVRYJISSEKISTMSHNS      = sum_rec.DLVRYSTMSHNS      -- 配達実績その他無償本数
         , JHTT.DLVRYJISSEKISHSUAKIN     = sum_rec.DLVRYSHSUAKIN     -- 配達実績製品水売上金額
         , JHTT.DLVRYJISSEKISONOTAURIKIN = sum_rec.DLVRYSONOTAURIKIN -- 配達実績その他売上金額
         , JHTT.KURERURITTORUSTUP    = sum_rec.KURERURITTORUSTUP    -- クレールＬ設置
         , JHTT.KURERURITTORUHIKIAGE = sum_rec.KURERURITTORUHIKIAGE -- クレールＬ引上
         , JHTT.KURERUESUSTUP        = sum_rec.KURERUESUSTUP        -- クレールＳ設置;
         , JHTT.KURERUESUHIKIAGE     = sum_rec.KURERUESUHIKIAGE     -- クレールＳ引上
         , JHTT.SONOTASTUP           = sum_rec.SONOTASTUP           -- その他設置
         , JHTT.SONOTAHIKIAGE        = sum_rec.SONOTAHIKIAGE        -- その他引上
         , JHTT.NOHINKIN         = sum_rec.NOHINKIN         -- 納品金額
         , JHTT.NOHINSHOHITAXKIN = sum_rec.NOHINSHOHITAXKIN -- 納品消費税金額
-- インボイス対応追加 start ABI
         , JHTT.NOHINIPPANNUKIKIN   = NVL(sum_rec.NOHINIPPANNUKIKIN,0)   -- 納品一般税抜金額
         , JHTT.NOHINIPPANTAXKIN    = NVL(sum_rec.NOHINIPPANTAXKIN,0)    -- 納品一般消費税金額
         , JHTT.NOHINKEIGENNUKIKIN  = NVL(sum_rec.NOHINKEIGENNUKIKIN,0)  -- 納品軽減税抜金額
         , JHTT.NOHINKEIGENTAXKIN   = NVL(sum_rec.NOHINKEIGENTAXKIN,0)   -- 納品軽減消費税金額
         , JHTT.NOHINHIKAZEIURIIKIN = NVL(sum_rec.NOHINHIKAZEIURIIKIN,0) -- 納品非課税金額
         , JHTT.NOHINFUKAZEIURIIKIN = NVL(sum_rec.NOHINFUKAZEIURIIKIN,0) -- 納品不課税売上金額
-- インボイス対応追加 end ABI
           /* 更新者 */
         , JHTT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
         , JHTT.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
         , JHTT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
         , JHTT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
         , JHTT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
         , JHTT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
         , JHTT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
         , JHTT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
     WHERE JHTT.APKIHONCD  = itaku_rec.APKIHONCD
       AND JHTT.SHOPCD     = itaku_rec.SHOPCD 
       AND JHTT.ACPTDNPYNO = itaku_rec.ACPTDNPYNO 
       AND JHTT.EDANO      = itaku_rec.EDANO 
       AND JHTT.YUKOKBN = '1'
    ;
  END updateJuchuDelivered;
  
  /*****************************************************************************/
  /* プロシージャ名：受託配達テーブル未配達更新                                     */
  /* 引数             ：itaku_rec      委託データ                                */
  /*                  ：jutaku_rec     受託データ                               */
  /*****************************************************************************/
  PROCEDURE updateJuchuNotDeliver( itaku_rec    IN itaku_rec_type  -- 委託データ
                                 , jutaku_rec   IN jutaku_rec_type -- 受託データ
  ) IS 
  BEGIN 
    UPDATE S04T001T JHTT 
       SET /* 受託のデータ */
           JHTT.DLVRYJISSEKIDD = jutaku_rec.DLVRYDD -- 配達実績日
         , JHTT.DLVRYDD        = jutaku_rec.DLVRYDD -- 配達日
           -- 配達実績処理日
         , JHTT.DLVRYJISSEKIPRCSDD = ( CASE WHEN jutaku_rec.DLVRYSTS IN ('13', '14') 
                                            THEN jutaku_rec.JIKAIDLVRYYTIDD  -- 次回配達予定日
                                            ELSE userinfo_rec.UNYOYMD -- 運用日付
                                       END )
         , JHTT.DNPYSTS  = jutaku_rec.DNPYSTS  -- 伝票ステータス 
         , JHTT.DLVRYSTS = jutaku_rec.DLVRYSTS -- 配達ステータス
         , JHTT.YUKOKBN  = jutaku_rec.YUKOKBN  -- 有効区分
           /* 委託のデータ */
         , JHTT.DLVRYTNSHACD = itaku_rec.DLVRYTNSHACD -- 配達担当者コード
         , JHTT.DLVRYJISSEKIMONOAPKIHONCD = itaku_rec.APKIHONCD     -- 配達管轄ＡＰ基本コード
         , JHTT.DLVRYJISSEKIMONOSHOPCD    = itaku_rec.SHOPCD        -- 配達管轄ショップコード
         , JHTT.DLVRYJISSEKIPRCSTNSHACD   = itaku_rec.RENKEITNSHACD -- 配達実績処理担当者コード
           /* 更新者 */
         , JHTT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD -- 更新者ＡＰ基本コード
         , JHTT.KOSHASHOPCD    = userinfo_rec.SHOPCD    -- 更新者ショップコード
         , JHTT.KOSHACD        = userinfo_rec.TNSHACD        -- 更新者コード
         , JHTT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
         , JHTT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
         , JHTT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
         , JHTT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
         , JHTT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
     WHERE JHTT.APKIHONCD  = itaku_rec.APKIHONCD
       AND JHTT.SHOPCD     = itaku_rec.SHOPCD 
       AND JHTT.ACPTDNPYNO = itaku_rec.ACPTDNPYNO 
       AND JHTT.EDANO      = itaku_rec.EDANO 
       AND JHTT.YUKOKBN = '1'
    ;
  END updateJuchuNotDeliver;
  
  /*****************************************************************************/
  /* プロシージャ名：受託配達テーブル再配達登録                                     */
  /* 引数             ：itaku_rec      委託データ                                */
  /*                  ：jutaku_rec     受託データ                               */
  /* インボイス対応追加 start ABI                                               */
  /*                  ：iInvoiceFlg    インボイス施行状況区分                   */
  /* インボイス対応追加 end ABI                                                 */
  /*****************************************************************************/
  PROCEDURE insertJuchuSaihaitatsu( itaku_rec    IN itaku_rec_type  -- 委託データ
                                  , jutaku_rec   IN jutaku_rec_type -- 受託データ
-- インボイス対応追加 start ABI
                                , iInvoiceFlg  IN VARCHAR2          -- インボイス施行状況区分
-- インボイス対応追加 end ABI
  ) IS 
  BEGIN 
    INSERT INTO S04T001T (
           APKIHONCD  -- ＡＰ基本コード
         , SHOPCD     -- ショップコード
         , ACPTDNPYNO -- 受注伝票番号
         , EDANO      -- 枝番号
         , ITAKUAITEAPKIHONCD  -- 委託相手ＡＰ基本コード
         , ITAKUAITESHOPCD     -- 委託相手ショップコード
         , ITAKUAITEACPTDNPYNO -- 委託相手受注伝票番号
         , ITAKUAITEEDANO      -- 委託相手枝番号
         , URIDNPYNO -- 売上伝票番号
         /* 委託データ */
         , DLVRYTRGTFLG -- 配達対象フラグ
         , DNPYKBN      -- 伝票区分
         , TORIHIKIKBN  -- 取引区分
         , ACPTKBN      -- 受注区分
         , ACPTKBNSHS   -- 受注区分詳細
         , TOKUISAKICD  -- 得意先コード
         , ACPTDD       -- 受注日
         , ACPTTNSHAAPKIHONCD -- 受注担当者ＡＰ基本コード
         , ACPTTNSHASHOPCD    -- 受注担当者ショップコード
         , ACPTTNSHACD        -- 受注担当者コード
         , DLVRYKANKATSUAPKIHONCD -- 配達管轄ＡＰ基本コード
         , DLVRYKANKATSUSHOPCD    -- 配達管轄ショップコード
         , DLVRYKYOTENCD          -- 配達拠点コード
         , DLVRYAREACD            -- 配達エリアコード
         , DLVRYTNSHACD -- 配達担当者コード
         , DLVRYCMPNYCD -- 配達会社コード
         , DLVRYJUN     -- 配達順
         , TAXKEISANKBN -- 税計算区分
         , AZUKARIBTLN1N2RITTORUHNS -- 預かりボトル１２Ｌ本数
         , AZUKARIBTLN8RITTORUHNS   -- 預かりボトル８Ｌ本数
         , PRNTMEMON1 -- 印刷メモ１
         , PRNTMEMON2 -- 印刷メモ２
         , PRNTMEMON3 -- 印刷メモ３
         , DATSKSMOTOKBN   -- データ作成元区分
         /* 受託データ */
         , DLVRYYTIDD           -- 配達予定日
         , DLVRYJISSEKIDD     -- 配達実績日
         , DLVRYDD            -- 配達日
         , DLVRYKIBOJIKAN     -- 配達希望時間
         , DLVRYKIBOJIKANSHRY -- 配達希望時間終了
         , DLVRYKIBOJIKANKBN  -- 配達希望時間区分
         , FZAIOKIKANOFLG     -- 不在置き可能フラグ
         , FZAIOKIJISSEKIFLG  -- 不在置き実績フラグ
         , DLVRYKBN           -- 配達区分
         , KYBTLN1N2RITTORUHNS      -- 回収予定ボトル１２Ｌ本数
         , KZBTLN1N2RITTORUHNS      -- 回収実績ボトル１２Ｌ本数
         , KYBTLN8RITTORUHNS        -- 回収予定ボトル８Ｌ本数
         , KZBTLN8RITTORUHNS        -- 回収実績ボトル８Ｌ本数
         , ACPTSHIMEPRCSDD         -- 受注締処理日
         , ACPTSHIMETNSHAAPKIHONCD -- 受注締担当者ＡＰ基本コード
         , ACPTSHIMETNSHASHOPCD    -- 受注締担当者ショップコード
         , ACPTSHIMEPRCSTNSHACD       -- 受注締処理担当者コード
         , NOSHOPRNTTRGTFLG      -- 納品書印刷対象フラグ
         , NOSHOPRNTSUMIFLG      -- 納品書印刷済フラグ
         , NOSHOPRNTDD           -- 納品書印刷日
         , NOSHOHIKAEPRNTTRGTFLG -- 納品書控印刷対象フラグ
         , NOSHOHIKAEPRNTSUMIFLG -- 納品書控印刷済フラグ
         , NOSHOHIKAEPRNTDD      -- 納品書控印刷日
         , NYKNHYOPRNTTRGTFLG    -- 入金票印刷対象フラグ
         , NYKNHYOPRNTSUMIFLG    -- 入金票印刷済フラグ
         , NYKNHYOPRNTDD         -- 入金票印刷日
         , WSVSIJISHOPRNTTRGTFLG -- サーバー指示書印刷対象フラグ
         , WSVSIJISHOPRNTSUMIFLG -- サーバー指示書印刷済フラグ
         , WSVSIJISHOPRNTDD      -- サーバー指示書印刷日
         , SKSMOTOKANRIKBN -- 作成元管理区分
         , SKSMOTOKANRINO  -- 作成元管理番号
         , ACPTHIKIATESAKIAPKIHONCD -- 受注引当先ＡＰ基本コード
         , ACPTHIKIATESAKISHOPCD    -- 受注引当先ショップコード
         , ACPTHIKIATESAKIKYOTENCD  -- 受注引当先拠点コード
         , KURERURITTORUSTUP    -- クレールＬ設置
         , KURERURITTORUHIKIAGE -- クレールＬ引上
         , KURERUESUSTUP        -- クレールＳ設置
         , KURERUESUHIKIAGE     -- クレールＳ引上
         , SONOTASTUP           -- その他設置
         , SONOTAHIKIAGE        -- その他引上
         , ZNKDLVRYDD       -- 前回配達日
         , ZNKDLVRYHNS      -- 前回配達本数
         , JIKAIDLVRYYTIDD  -- 次回配達予定日
         , ZNKSHIMEDD       -- 前回締日
         , ZNKKURIKOSHIKIN  -- 前回繰越金額
         , ACPTTHDAIBIKIFLG -- 受注時代引フラグ
         , ACPTRCPTKEIROKBN -- 受注受付経路区分
         , YUKOKBN -- 有効区分
         /* 固定値 */
         , DLVRYJISSEKIPRCSDD        -- 配達実績処理日
         , DLVRYJISSEKIMONOAPKIHONCD -- 配達実績者ＡＰ基本コード
         , DLVRYJISSEKIMONOSHOPCD    -- 配達実績者ショップコード
         , DLVRYJISSEKIPRCSTNSHACD   -- 配達実績処理担当者コード
         , URISHIMEPRCSDD            -- 売上締処理日
         , GETSUJISHIMEDYDM          -- 月次締年月
         , GETSUJISHIMEDD            -- 月次締日
         , GETSUJIPRCSJISSIDD        -- 月次処理実施日
-- インボイス対応追加 start ABI
         , SINGLDNPYFLG              -- 単体伝票締めフラグ
-- インボイス対応追加 end ABI
         , DNPYSTS  -- 伝票ステータス
         , ACPTSTS  -- 受注ステータス
         , DLVRYSTS -- 配達ステータス
         /* 合計値 */
         , DLVRYYTIRGRAYUSYOHNS     -- 配達予定レギュラー有償本数
         , DLVRYYTIRGRAMUSHOHNS     -- 配達予定レギュラー無償本数
         , DLVRYYTISRMYUSYOHNS      -- 配達予定スリム有償本数
         , DLVRYYTISRMMUSHOHNS      -- 配達予定スリム無償本数
         , DLVRYYTISONOTAYUSYOHNS   -- 配達予定その他有償本数
         , DLVRYYTISONOTAMUSHOHNS   -- 配達予定その他無償本数
         , DLVRYYTISEIHINMIZUURIKIN -- 配達予定製品水売上金額
         , DLVRYYTISONOTAURIKIN     -- 配達予定その他売上金額
         , DLVRYJISSEKIRGRAYUSYOHNS -- 配達実績レギュラー有償本数
         , DLVRYJISSEKIRGRAMUSHOHNS -- 配達実績レギュラー無償本数
         , DLVRYJISSEKISRMYUSYOHNS  -- 配達実績スリム有償本数
         , DLVRYJISSEKISRMMUSHOHNS  -- 配達実績スリム無償本数
         , DLVRYJISSEKISTYSHNS      -- 配達実績その他有償本数
         , DLVRYJISSEKISTMSHNS      -- 配達実績その他無償本数
         , DLVRYJISSEKISHSUAKIN     -- 配達実績製品水売上金額
         , DLVRYJISSEKISONOTAURIKIN -- 配達実績その他売上金額
         , ACPTKIN                  -- 受注金額
         , ACPTSHOHITAXKIN          -- 受注消費税金額
-- インボイス対応追加 start ABI
         , ACPTIPPANNUKIKIN         -- 受注一般税抜金額
         , ACPTIPPANTAXKIN          -- 受注一般消費税金額
         , ACPTKEIGENNUKIKIN        -- 受注軽減税抜金額
         , ACPTKEIGENTAXKIN         -- 受注軽減消費税金額
         , ACPTHIKAZEIURIIKIN       -- 受注非課税金額
         , ACPTFUKAZEIURIIKIN       -- 受注不課税売上金額
-- インボイス対応追加 end ABI
         , NOHINKIN                 -- 納品金額
         , NOHINSHOHITAXKIN         -- 納品消費税金額
-- インボイス対応追加 start ABI
         , NOHINIPPANNUKIKIN        -- 納品一般税抜金額
         , NOHINIPPANTAXKIN         -- 納品一般消費税金額
         , NOHINKEIGENNUKIKIN       -- 納品軽減税抜金額
         , NOHINKEIGENTAXKIN        -- 納品軽減消費税金額
         , NOHINHIKAZEIURIIKIN      -- 納品非課税金額
         , NOHINFUKAZEIURIIKIN      -- 納品不課税売上金額
-- インボイス対応追加 end ABI
         /* 登録者 */
         , TOSHAAPKIHONCD -- 登録者ＡＰ基本コード
         , TOSHASHOPCD    -- 登録者ショップコード
         , TOSHACD        -- 登録者コード
         , INPTSYSTMYMD   -- 登録システム日付
         , INPTSYSTMJIKAN -- 登録システム時間
         , INPTUNYOYMD    -- 登録運用日付
         , INPTIPADRSE    -- 登録ＩＰアドレス
         , INPTPGMID      -- 登録プログラムＩＤ
         /* 更新者 */
         , KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
         , KOSHASHOPCD    -- 更新者ショップコード
         , KOSHACD        -- 更新者コード
         , KSHNSYSTMYMD   -- 更新システム日付
         , KSHNSYSTMJIKAN -- 更新システム時間
         , KSHNUNYOYMD    -- 更新運用日付
         , KSHNIPADRSE    -- 更新ＩＰアドレス
         , KSHNPGMID      -- 更新プログラムＩＤ 
    ) VALUES (
             itaku_rec.APKIHONCD   -- ＡＰ基本コード
           , itaku_rec.SHOPCD      -- ショップコード
           , itaku_rec.ACPTDNPYNO  -- 受注伝票番号
           , itaku_rec.EDANO + 1   -- 枝番号
           , jutaku_rec.APKIHONCD  -- 委託相手ＡＰ基本コード
           , jutaku_rec.SHOPCD     -- 委託相手ショップコード
           , jutaku_rec.ACPTDNPYNO -- 委託相手受注伝票番号
           , jutaku_rec.EDANO      -- 委託相手枝番号
           , NULL -- 売上伝票番号
           /* 委託情報 */
           , itaku_rec.DLVRYTRGTFLG -- 配達対象フラグ
           , itaku_rec.DNPYKBN      -- 伝票区分
           , itaku_rec.TORIHIKIKBN  -- 取引区分
           , itaku_rec.ACPTKBN      -- 受注区分
           , itaku_rec.ACPTKBNSHS   -- 受注区分詳細
           , itaku_rec.TOKUISAKICD  -- 得意先コード
           , itaku_rec.ACPTDD       -- 受注日
           , itaku_rec.ACPTTNSHAAPKIHONCD -- 受注担当者ＡＰ基本コード
           , itaku_rec.ACPTTNSHASHOPCD    -- 受注担当者ショップコード
           , itaku_rec.ACPTTNSHACD        -- 受注担当者コード
           , itaku_rec.DLVRYKANKATSUAPKIHONCD -- 配達管轄ＡＰ基本コード
           , itaku_rec.DLVRYKANKATSUSHOPCD    -- 配達管轄ショップコード
           , itaku_rec.DLVRYKYOTENCD          -- 配達拠点コード
           , itaku_rec.DLVRYAREACD            -- 配達エリアコード
           , itaku_rec.DLVRYTNSHACD -- 配達担当者コード
           , itaku_rec.DLVRYCMPNYCD -- 配達会社コード
           , itaku_rec.DLVRYJUN     -- 配達順
           , itaku_rec.TAXKEISANKBN -- 税計算区分
           , itaku_rec.AZUKARIBTLN1N2RITTORUHNS -- 預かりボトル１２Ｌ本数
           , itaku_rec.AZUKARIBTLN8RITTORUHNS   -- 預かりボトル８Ｌ本数
           , itaku_rec.PRNTMEMON1 -- 印刷メモ１
           , itaku_rec.PRNTMEMON2 -- 印刷メモ２
           , itaku_rec.PRNTMEMON3 -- 印刷メモ３
           , itaku_rec.DATSKSMOTOKBN -- データ作成元区分
           /* 受託情報 */
           , jutaku_rec.DLVRYYTIDD         -- 配達予定日
           , NULL -- 配達実績日
           , jutaku_rec.DLVRYDD            -- 配達日
           , jutaku_rec.DLVRYKIBOJIKAN     -- 配達希望時間
           , jutaku_rec.DLVRYKIBOJIKANSHRY -- 配達希望時間終了
           , jutaku_rec.DLVRYKIBOJIKANKBN  -- 配達希望時間区分
           , jutaku_rec.FZAIOKIKANOFLG     -- 不在置き可能フラグ
           , '0'  -- 不在置き実績フラグ
           , jutaku_rec.DLVRYKBN -- 配達区分
           , jutaku_rec.KYBTLN1N2RITTORUHNS     -- 回収予定ボトル１２Ｌ本数
           , 0    -- 回収実績ボトル１２Ｌ本数
           , jutaku_rec.KYBTLN8RITTORUHNS       -- 回収予定ボトル８Ｌ本数
           , 0    -- 回収実績ボトル８Ｌ本数
           , jutaku_rec.ACPTSHIMEPRCSDD         -- 受注締処理日
           , jutaku_rec.ACPTSHIMETNSHAAPKIHONCD -- 受注締担当者ＡＰ基本コード
           , jutaku_rec.ACPTSHIMETNSHASHOPCD    -- 受注締担当者ショップコード
           , jutaku_rec.ACPTSHIMEPRCSTNSHACD    -- 受注締処理担当者コード
           , jutaku_rec.NOSHOPRNTTRGTFLG        -- 納品書印刷対象フラグ
           , '0'  -- 納品書印刷済フラグ
           , NULL -- 納品書印刷日
           , jutaku_rec.NOSHOHIKAEPRNTTRGTFLG -- 納品書控印刷対象フラグ
           , '0'  -- 納品書控印刷済フラグ
           , NULL -- 納品書控印刷日
           , jutaku_rec.NYKNHYOPRNTTRGTFLG    -- 入金票印刷対象フラグ
           , '0'  -- 入金票印刷済フラグ
           , NULL -- 入金票印刷日
           , jutaku_rec.WSVSIJISHOPRNTTRGTFLG -- サーバー指示書印刷対象フラグ
           , '0'  -- サーバー指示書印刷済フラグ
           , NULL -- サーバー指示書印刷日
           , jutaku_rec.SKSMOTOKANRIKBN -- 作成元管理区分
           , jutaku_rec.SKSMOTOKANRINO  -- 作成元管理番号
           , jutaku_rec.ACPTHIKIATESAKIAPKIHONCD -- 受注引当先ＡＰ基本コード
           , jutaku_rec.ACPTHIKIATESAKISHOPCD    -- 受注引当先ショップコード
           , jutaku_rec.ACPTHIKIATESAKIKYOTENCD  -- 受注引当先拠点コード
           , jutaku_rec.KURERURITTORUSTUP    -- クレールＬ設置
           , jutaku_rec.KURERURITTORUHIKIAGE -- クレールＬ引上
           , jutaku_rec.KURERUESUSTUP        -- クレールＳ設置
           , jutaku_rec.KURERUESUHIKIAGE     -- クレールＳ引上
           , jutaku_rec.SONOTASTUP           -- その他設置
           , jutaku_rec.SONOTAHIKIAGE        -- その他引上
           , jutaku_rec.ZNKDLVRYDD_JHTT  -- 前回配達日
           , jutaku_rec.ZNKDLVRYHNS      -- 前回配達本数
           , jutaku_rec.JIKAIDLVRYYTIDD  -- 次回配達予定日
           , jutaku_rec.ZNKSHIMEDD       -- 前回締日
           , jutaku_rec.ZNKKURIKOSHIKIN  -- 前回繰越金額
           , jutaku_rec.ACPTTHDAIBIKIFLG -- 受注時代引フラグ
           , jutaku_rec.ACPTRCPTKEIROKBN -- 受注受付経路区分
           , jutaku_rec.YUKOKBN -- 有効区分
           /* 固定値 */
           , '00000000' -- 配達実績処理日
           , NULL -- 配達実績者ＡＰ基本コード
           , NULL -- 配達実績者ショップコード
           , NULL -- 配達実績処理担当者コード
           , '00000000' -- 売上締処理日
           , NULL -- 月次締年月
           , NULL -- 月次締日
           , NULL -- 月次処理実施日
-- インボイス対応追加 start ABI
           , '0'  -- 単体伝票締めフラグ
-- インボイス対応追加 end ABI
           , '03' -- 伝票ステータス：再配達
           , '03' -- 受注ステータス：再配達
           , '00' -- 配達ステータス：設定なし
           /* 合計値 */
           , sum_rec.DLVRYRGRAYUSYOHNS -- 配達予定レギュラー有償本数
           , sum_rec.DLVRYRGRAMUSHOHNS -- 配達予定レギュラー無償本数
           , sum_rec.DLVRYSRMYUSYOHNS  -- 配達予定スリム有償本数
           , sum_rec.DLVRYSRMMUSHOHNS  -- 配達予定スリム無償本数
           , sum_rec.DLVRYSTYSHNS      -- 配達予定その他有償本数
           , sum_rec.DLVRYSTMSHNS      -- 配達予定その他無償本数
           , sum_rec.DLVRYSHSUAKIN     -- 配達予定製品水売上金額
           , sum_rec.DLVRYSONOTAURIKIN -- 配達予定その他売上金額
           , 0  -- 配達実績レギュラー有償本数
           , 0  -- 配達実績レギュラー無償本数
           , 0  -- 配達実績スリム有償本数
           , 0  -- 配達実績スリム無償本数
           , 0  -- 配達実績その他有償本数
           , 0  -- 配達実績その他無償本数
           , 0  -- 配達実績製品水売上金額
           , 0  -- 配達実績その他売上金額
           , sum_rec.ACPTKIN             -- 受注金額
           , sum_rec.ACPTSHOHITAXKIN     -- 受注消費税金額
-- インボイス対応追加 start ABI
           , NVL(sum_rec.ACPTIPPANNUKIKIN,0)    -- 受注一般税抜金額
           , NVL(sum_rec.ACPTIPPANTAXKIN,0)     -- 受注一般消費税金額
           , NVL(sum_rec.ACPTKEIGENNUKIKIN,0)   -- 受注軽減税抜金額
           , NVL(sum_rec.ACPTKEIGENTAXKIN,0)    -- 受注軽減消費税金額
           , NVL(sum_rec.ACPTHIKAZEIURIIKIN,0)  -- 受注非課税金額
           , NVL(sum_rec.ACPTFUKAZEIURIIKIN,0)  -- 受注不課税売上金額
-- インボイス対応追加 end ABI
           , sum_rec.NOHINKIN            -- 納品金額
           , sum_rec.NOHINSHOHITAXKIN    -- 納品消費税金額
-- インボイス対応追加 start ABI
           , NVL(sum_rec.NOHINIPPANNUKIKIN,0)   -- 納品一般税抜金額
           , NVL(sum_rec.NOHINIPPANTAXKIN,0)    -- 納品一般消費税金額
           , NVL(sum_rec.NOHINKEIGENNUKIKIN,0)  -- 納品軽減税抜金額
           , NVL(sum_rec.NOHINKEIGENTAXKIN,0)   -- 納品軽減消費税金額
           , NVL(sum_rec.NOHINHIKAZEIURIIKIN,0) -- 納品非課税金額
           , NVL(sum_rec.NOHINFUKAZEIURIIKIN,0) -- 納品不課税売上金額
-- インボイス対応追加 end ABI
           /* 登録者 */
           , userinfo_rec.APKIHONCD  -- 登録者ＡＰ基本コード
           , userinfo_rec.SHOPCD     -- 登録者ショップコード
           , userinfo_rec.TNSHACD    -- 登録者コード
           , userinfo_rec.SYSTMYMD   -- 登録システム日付
           , userinfo_rec.SYSTMJIKAN -- 登録システム時間
           , userinfo_rec.UNYOYMD    -- 登録運用日付
           , userinfo_rec.IPADRSE    -- 登録ＩＰアドレス
           , userinfo_rec.PGMID      -- 登録プログラムＩＤ        
           /* 更新者 */
           , userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
           , userinfo_rec.SHOPCD     -- 更新者ショップコード
           , userinfo_rec.TNSHACD    -- 更新者コード
           , userinfo_rec.SYSTMYMD   -- 更新システム日付
           , userinfo_rec.SYSTMJIKAN -- 更新システム時間
           , userinfo_rec.UNYOYMD    -- 更新運用日付
           , userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
           , userinfo_rec.PGMID      -- 更新プログラムＩＤ        
    );

  END insertJuchuSaihaitatsu;
  
  /*****************************************************************************/
  /* プロシージャ名：再配達処理                                                   */
  /* 引数             ：itaku_rec      委託データ                                */
  /*                  ：jutaku_rec     受託データ                               */
  /*****************************************************************************/
  PROCEDURE processSaihaitatsu( itaku_rec   IN     itaku_rec_type  -- 委託データ
                              , jutaku_rec  IN     jutaku_rec_type -- 受託データ
  ) IS 
  /* 変数宣言 */
  jutaku_saihaitatsu          jutaku_rec_type := NULL; -- 受託再配達データ
-- インボイス対応追加 start ABI
  wInvoiceFlg                 VARCHAR2(1) := '0';        -- インボイス施行状況区分
-- インボイス対応追加 end ABI
  BEGIN 
    -- 再配達受託データ取得
    SELECT JHTT_JUTAKU.APKIHONCD  -- ＡＰ基本コード
         , JHTT_JUTAKU.SHOPCD     -- ショップコード
         , JHTT_JUTAKU.ACPTDNPYNO -- 受注伝票番号
         , JHTT_JUTAKU.EDANO      -- 枝番号
         , JHTT_JUTAKU.DLVRYKBN       -- 配達区分
         , JHTT_JUTAKU.DLVRYSTS       -- 配達ステータス
         , JHTT_JUTAKU.KZBTLN1N2RITTORUHNS -- 回収実績ボトル１２Ｌ本数
         , JHTT_JUTAKU.KZBTLN8RITTORUHNS   -- 回収実績ボトル８Ｌ本数
         , JHTT_JUTAKU.DNPYSTS             -- 伝票ステータス
         , JHTT_JUTAKU.DLVRYDD        -- 配達日
         , JHTT_JUTAKU.DLVRYYTIDD     -- 配達予定日
         , JHTT_JUTAKU.DLVRYJISSEKIDD -- 配達実績日
         , JHTT_JUTAKU.ACPTDD         -- 受注日
         , JHTT_JUTAKU.JIKAIDLVRYYTIDD     -- 次回配達予定日
         , JHTT_JUTAKU.DLVRYKIBOJIKAN      -- 配達希望時間
         , JHTT_JUTAKU.DLVRYKIBOJIKANSHRY  -- 配達希望時間終了
         , JHTT_JUTAKU.DLVRYKIBOJIKANKBN   -- 配達希望時間区分
         , JHTT_JUTAKU.FZAIOKIKANOFLG      -- 不在置き可能フラグ
         , JHTT_JUTAKU.KYBTLN1N2RITTORUHNS      -- 回収予定ボトル１２Ｌ本数
         , JHTT_JUTAKU.KYBTLN8RITTORUHNS        -- 回収予定ボトル８Ｌ本数
         , JHTT_JUTAKU.ACPTSHIMEPRCSDD         -- 受注締処理日
         , JHTT_JUTAKU.ACPTSHIMETNSHAAPKIHONCD -- 受注締担当者ＡＰ基本コード
         , JHTT_JUTAKU.ACPTSHIMETNSHASHOPCD    -- 受注締担当者ショップコード
         , JHTT_JUTAKU.ACPTSHIMEPRCSTNSHACD    -- 受注締処理担当者コード
         , JHTT_JUTAKU.NOSHOPRNTTRGTFLG        -- 納品書印刷対象フラグ
         , JHTT_JUTAKU.NOSHOPRNTSUMIFLG        -- 納品書印刷済フラグ
         , JHTT_JUTAKU.NOSHOPRNTDD             -- 納品書印刷日
         , JHTT_JUTAKU.NOSHOHIKAEPRNTTRGTFLG   -- 納品書控印刷対象フラグ
         , JHTT_JUTAKU.NOSHOHIKAEPRNTSUMIFLG   -- 納品書控印刷済フラグ
         , JHTT_JUTAKU.NOSHOHIKAEPRNTDD        -- 納品書控印刷日
         , JHTT_JUTAKU.NYKNHYOPRNTTRGTFLG      -- 入金票印刷対象フラグ
         , JHTT_JUTAKU.NYKNHYOPRNTSUMIFLG      -- 入金票印刷済フラグ
         , JHTT_JUTAKU.NYKNHYOPRNTDD           -- 入金票印刷日
         , JHTT_JUTAKU.WSVSIJISHOPRNTTRGTFLG   -- サーバー指示書印刷対象フラグ
         , JHTT_JUTAKU.WSVSIJISHOPRNTSUMIFLG   -- サーバー指示書印刷済フラグ
         , JHTT_JUTAKU.WSVSIJISHOPRNTDD        -- サーバー指示書印刷日
         , JHTT_JUTAKU.SKSMOTOKANRIKBN -- 作成元管理区分
         , JHTT_JUTAKU.SKSMOTOKANRINO  -- 作成元管理番号
         , JHTT_JUTAKU.ACPTHIKIATESAKIAPKIHONCD -- 受注引当先ＡＰ基本コード
         , JHTT_JUTAKU.ACPTHIKIATESAKISHOPCD    -- 受注引当先ショップコード
         , JHTT_JUTAKU.ACPTHIKIATESAKIKYOTENCD  -- 受注引当先拠点コード
         , JHTT_JUTAKU.KURERURITTORUSTUP    -- クレールＬ設置
         , JHTT_JUTAKU.KURERURITTORUHIKIAGE -- クレールＬ引上
         , JHTT_JUTAKU.KURERUESUSTUP        -- クレールＳ設置
         , JHTT_JUTAKU.KURERUESUHIKIAGE     -- クレールＳ引上
         , JHTT_JUTAKU.SONOTASTUP           -- その他設置
         , JHTT_JUTAKU.SONOTAHIKIAGE        -- その他引上
         , JHTT_JUTAKU.ZNKDLVRYDD       -- 前回配達日
         , JHTT_JUTAKU.ZNKDLVRYHNS      -- 前回配達本数
         , JHTT_JUTAKU.ZNKSHIMEDD       -- 前回締日
         , JHTT_JUTAKU.ZNKKURIKOSHIKIN  -- 前回繰越金額
         , JHTT_JUTAKU.ACPTTHDAIBIKIFLG -- 受注時代引フラグ
         , JHTT_JUTAKU.ACPTRCPTKEIROKBN -- 受注受付経路区分
         , JHTT_JUTAKU.YUKOKBN -- 有効区分
      INTO jutaku_saihaitatsu.APKIHONCD  -- ＡＰ基本コード
         , jutaku_saihaitatsu.SHOPCD     -- ショップコード
         , jutaku_saihaitatsu.ACPTDNPYNO -- 受注伝票番号
         , jutaku_saihaitatsu.EDANO      -- 枝番号
         , jutaku_saihaitatsu.DLVRYKBN   -- 配達区分
         , jutaku_saihaitatsu.DLVRYSTS   -- 配達ステータス
         , jutaku_saihaitatsu.KZBTLN1N2RITTORUHNS -- 回収実績ボトル１２Ｌ本数
         , jutaku_saihaitatsu.KZBTLN8RITTORUHNS   -- 回収実績ボトル８Ｌ本数
         , jutaku_saihaitatsu.DNPYSTS    -- 伝票ステータス
         , jutaku_saihaitatsu.DLVRYDD        -- 配達日
         , jutaku_saihaitatsu.DLVRYYTIDD     -- 配達予定日
         , jutaku_saihaitatsu.DLVRYJISSEKIDD -- 配達実績日
         , jutaku_saihaitatsu.ACPTDD         -- 受注日
         , jutaku_saihaitatsu.JIKAIDLVRYYTIDD     -- 次回配達予定日
         , jutaku_saihaitatsu.DLVRYKIBOJIKAN      -- 配達希望時間
         , jutaku_saihaitatsu.DLVRYKIBOJIKANSHRY  -- 配達希望時間終了
         , jutaku_saihaitatsu.DLVRYKIBOJIKANKBN   -- 配達希望時間区分
         , jutaku_saihaitatsu.FZAIOKIKANOFLG      -- 不在置き可能フラグ
         , jutaku_saihaitatsu.KYBTLN1N2RITTORUHNS      -- 回収予定ボトル１２Ｌ本数
         , jutaku_saihaitatsu.KYBTLN8RITTORUHNS        -- 回収予定ボトル８Ｌ本数
         , jutaku_saihaitatsu.ACPTSHIMEPRCSDD         -- 受注締処理日
         , jutaku_saihaitatsu.ACPTSHIMETNSHAAPKIHONCD -- 受注締担当者ＡＰ基本コード
         , jutaku_saihaitatsu.ACPTSHIMETNSHASHOPCD    -- 受注締担当者ショップコード
         , jutaku_saihaitatsu.ACPTSHIMEPRCSTNSHACD    -- 受注締処理担当者コード
         , jutaku_saihaitatsu.NOSHOPRNTTRGTFLG        -- 納品書印刷対象フラグ
         , jutaku_saihaitatsu.NOSHOPRNTSUMIFLG        -- 納品書印刷済フラグ
         , jutaku_saihaitatsu.NOSHOPRNTDD             -- 納品書印刷日
         , jutaku_saihaitatsu.NOSHOHIKAEPRNTTRGTFLG   -- 納品書控印刷対象フラグ
         , jutaku_saihaitatsu.NOSHOHIKAEPRNTSUMIFLG   -- 納品書控印刷済フラグ
         , jutaku_saihaitatsu.NOSHOHIKAEPRNTDD        -- 納品書控印刷日
         , jutaku_saihaitatsu.NYKNHYOPRNTTRGTFLG      -- 入金票印刷対象フラグ
         , jutaku_saihaitatsu.NYKNHYOPRNTSUMIFLG      -- 入金票印刷済フラグ
         , jutaku_saihaitatsu.NYKNHYOPRNTDD           -- 入金票印刷日
         , jutaku_saihaitatsu.WSVSIJISHOPRNTTRGTFLG   -- サーバー指示書印刷対象フラグ
         , jutaku_saihaitatsu.WSVSIJISHOPRNTSUMIFLG   -- サーバー指示書印刷済フラグ
         , jutaku_saihaitatsu.WSVSIJISHOPRNTDD        -- サーバー指示書印刷日
         , jutaku_saihaitatsu.SKSMOTOKANRIKBN -- 作成元管理区分
         , jutaku_saihaitatsu.SKSMOTOKANRINO  -- 作成元管理番号
         , jutaku_saihaitatsu.ACPTHIKIATESAKIAPKIHONCD -- 受注引当先ＡＰ基本コード
         , jutaku_saihaitatsu.ACPTHIKIATESAKISHOPCD    -- 受注引当先ショップコード
         , jutaku_saihaitatsu.ACPTHIKIATESAKIKYOTENCD  -- 受注引当先拠点コード
         , jutaku_saihaitatsu.KURERURITTORUSTUP    -- クレールＬ設置
         , jutaku_saihaitatsu.KURERURITTORUHIKIAGE -- クレールＬ引上
         , jutaku_saihaitatsu.KURERUESUSTUP        -- クレールＳ設置
         , jutaku_saihaitatsu.KURERUESUHIKIAGE     -- クレールＳ引上
         , jutaku_saihaitatsu.SONOTASTUP           -- その他設置
         , jutaku_saihaitatsu.SONOTAHIKIAGE        -- その他引上
         , jutaku_saihaitatsu.ZNKDLVRYDD_JHTT  -- 前回配達日
         , jutaku_saihaitatsu.ZNKDLVRYHNS      -- 前回配達本数
         , jutaku_saihaitatsu.ZNKSHIMEDD       -- 前回締日
         , jutaku_saihaitatsu.ZNKKURIKOSHIKIN  -- 前回繰越金額
         , jutaku_saihaitatsu.ACPTTHDAIBIKIFLG -- 受注時代引フラグ
         , jutaku_saihaitatsu.ACPTRCPTKEIROKBN -- 受注受付経路区分
         , jutaku_saihaitatsu.YUKOKBN -- 有効区分
      FROM S04T001T JHTT_JUTAKU -- 受注配達テーブル（受託データ）
           LEFT OUTER JOIN S02M050T TKJT_JUTAKU -- 得意先実績テーブル（受託データ）
           ON ( TKJT_JUTAKU.APKIHONCD       = JHTT_JUTAKU.APKIHONCD
                AND TKJT_JUTAKU.SHOPCD      = JHTT_JUTAKU.SHOPCD
                AND TKJT_JUTAKU.TOKUISAKICD = JHTT_JUTAKU.TOKUISAKICD
                AND TKJT_JUTAKU.YUKOKBN = '1'
              )
    WHERE JHTT_JUTAKU.APKIHONCD  = jutaku_rec.APKIHONCD
      AND JHTT_JUTAKU.SHOPCD     = jutaku_rec.SHOPCD
      AND JHTT_JUTAKU.ACPTDNPYNO = jutaku_rec.ACPTDNPYNO
      AND JHTT_JUTAKU.EDANO      = ( jutaku_rec.EDANO + 1 )
      AND JHTT_JUTAKU.YUKOKBN = '1' 
    FOR UPDATE OF JHTT_JUTAKU.ACPTDNPYNO 
    ;
    -- 受注配達明細テーブル作成
-- インボイス対応変更 start ABI
--    processSaihaitatsuMeisai( itaku_rec, jutaku_saihaitatsu ); 
    processSaihaitatsuMeisai( itaku_rec, jutaku_saihaitatsu, wInvoiceFlg ); 
-- インボイス対応変更 end ABI
    -- 受注配達テーブル
-- インボイス対応変更 start ABI
--    insertJuchuSaihaitatsu( itaku_rec, jutaku_saihaitatsu ); 
    insertJuchuSaihaitatsu( itaku_rec, jutaku_saihaitatsu, wInvoiceFlg ); 
-- インボイス対応変更 end ABI
    
    EXCEPTION 
      WHEN NO_DATA_FOUND THEN 
        RAISE_APPLICATION_ERROR(-20023, '受託の再配達データが存在しません。');
        
  END processSaihaitatsu;
  
  /*****************************************************************************/
  /* プロシージャ名：売上テーブル登録                                              */
  /* 引数             ：itaku_rec      委託データ                                */
  /*                  ：iRecordS04T001T      配達済受注データ                   */
  /* インボイス対応追加 start ABI                                               */
  /*                  ：iShohitaxkin         消費税金額                         */
  /*                  ：iSankoushohitaxkin   参考消費税金額                     */
  /*                  ：iInvoiceFlg          インボイス施行状況区分             */
  /* インボイス対応追加 end ABI                                                 */
  /*****************************************************************************/
  PROCEDURE insertUriage( itaku_rec        IN itaku_rec_type  -- 委託データ
                        , iRecordS04T001T IN S04T001T%rowtype -- 配達済受注データ
-- インボイス対応追加 start ABI
                        , iShohitaxkin       IN VARCHAR2      -- 消費税金額
                        , iSankoushohitaxkin IN VARCHAR2      -- 参考消費税金額
                        , iInvoiceFlg        IN VARCHAR2      -- インボイス施行状況区分
-- インボイス対応追加 end ABI
  ) IS 
  BEGIN 
    -- 売上テーブル登録
-- インボイス対応追加 start ABI
    IF iInvoiceFlg = '1' THEN
       INSERT INTO S06T001T (
              APKIHONCD     -- ＡＰ基本コード
            , SHOPCD        -- ショップコード
            , URIDNPYNO     -- 売上伝票番号
            , EDANO         -- 枝番号
            , ACPTDNPYNO    -- 受注伝票番号
            , ACPTDNPYEDANO -- 受注伝票枝番号
            , TORIHIKIKBN -- 取引区分
            , TOKUISAKICD -- 得意先コード
            , SIIRESAKICD -- 仕入先コード
            , NYUKAMOTOCD -- 入荷元コード
            , URIDD  -- 売上日
            , ACPTDD -- 受注日
            , URIKIN -- 売上金額
            , SHOHITAXKIN       -- 消費税金額
            , SANKOUSHOHITAXKIN -- 参考消費税金額
            , ACPTTNSHACD  -- 受注担当者コード
            , DLVRYTNSHACD -- 配達担当者コード
            , DLVRYKANKATSUAPKIHONCD -- 配達管轄ＡＰ基本コード
            , DLVRYKANKATSUSHOPCD    -- 配達管轄ショップコード
            , DLVRYKYOTENCD          -- 配達拠点コード
            , DLVRYKBN               -- 配達区分
            , DLVRYCMPNYCD   -- 配達会社コード
            , SHUKKAMOTOKBN  -- 出荷元区分
            , EIGYOTNSHACD   -- 営業担当者コード
            , JISSEKITNSHACD -- 実績担当者コード
            , SKYSAKICD      -- 請求先コード
            , SKYSHIMEDYDM   -- 請求締年月
            , SKYSHIMEDD     -- 請求締日
            , SKYPRCSJISSIDD -- 請求処理実施日
            , GETSUJISHIMEDYDM -- 月次締年月
            , GETSUJISHIMEDD   -- 月次締日
            , GETSUJIPRCSJISSIDD -- 月次処理実施日
            , URISHIMEPRCSDD     -- 売上締処理日
            -- ACCIS2023-6672対応 start ABI
            , NEBIKISHIMEDYDM   -- 値引締年月
            , NEBIKISHIMEDD     -- 値引締日
            , NEBIKIPRCSJISSIDD -- 値引処理実施日
            -- ACCIS2023-6672対応 end ABI
            , DNPYBIKO -- 伝票備考
            , DNPYNO   -- 伝票番号
            , SKYSHOOTPTARINASHIKBN -- 請求書出力有無区分
            , SKYPRCSJISSIKBN       -- 請求処理実施区分
            , DATSKSMOTOKBN         -- データ作成元区分
            , TAXKEISANKBN          -- 税計算区分
            , SIIREDNPYNO           -- 仕入伝票番号
            , NYKNDNPYNO            -- 入金伝票番号
            , NYKNKIN               -- 入金金額
            , NYKNKIND              -- 入金種別
            , YUKOKBN               -- 有効区分
            /* 登録者 */
            , TOSHAAPKIHONCD -- 登録者ＡＰ基本コード
            , TOSHASHOPCD    -- 登録者ショップコード
            , TOSHACD        -- 登録者コード
            , INPTSYSTMYMD   -- 登録システム日付
            , INPTSYSTMJIKAN -- 登録システム時間
            , INPTUNYOYMD    -- 登録運用日付
            , INPTIPADRSE    -- 登録ＩＰアドレス
            , INPTPGMID      -- 登録プログラムＩＤ
            /* 更新者 */
            , KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
            , KOSHASHOPCD    -- 更新者ショップコード
            , KOSHACD        -- 更新者コード
            , KSHNSYSTMYMD   -- 更新システム日付
            , KSHNSYSTMJIKAN -- 更新システム時間
            , KSHNUNYOYMD    -- 更新運用日付
            , KSHNIPADRSE    -- 更新ＩＰアドレス
            , KSHNPGMID      -- 更新プログラムＩＤ 
       ) VALUES (
                iRecordS04T001T.APKIHONCD  -- ＡＰ基本コード
              , iRecordS04T001T.SHOPCD     -- ショップコード
              , iRecordS04T001T.ACPTDNPYNO -- 売上伝票番号
              , iRecordS04T001T.EDANO      -- 枝番号
              , iRecordS04T001T.ACPTDNPYNO -- 受注伝票番号
              , iRecordS04T001T.EDANO      -- 受注伝票枝番号
              , iRecordS04T001T.TORIHIKIKBN -- 取引区分
              , iRecordS04T001T.TOKUISAKICD -- 得意先コード
              , NULL -- 仕入先コード
              , NULL -- 入荷元コード
              , iRecordS04T001T.DLVRYDD  -- 売上日
              , iRecordS04T001T.ACPTDD   -- 受注日
              , iRecordS04T001T.NOHINKIN -- 売上金額
              , 0                        -- 消費税金額
              , iSankoushohitaxkin       -- 参考消費税金額
              , iRecordS04T001T.ACPTTNSHACD            -- 受注担当者コード
              , iRecordS04T001T.DLVRYTNSHACD           -- 配達担当者コード
              , iRecordS04T001T.DLVRYKANKATSUAPKIHONCD -- 配達管轄ＡＰ基本コード
              , iRecordS04T001T.DLVRYKANKATSUSHOPCD    -- 配達管轄ショップコード
              , iRecordS04T001T.DLVRYKYOTENCD          -- 配達拠点コード
              , iRecordS04T001T.DLVRYKBN               -- 配達区分
              , iRecordS04T001T.DLVRYCMPNYCD           -- 配達会社コード
              , NULL                                   -- 出荷元区分
              , itaku_rec.EIGYOTNSHACD                 -- 営業担当者コード
              , ( CASE itaku_rec.URIKEIJOTNSHAKBN WHEN '2' THEN itaku_rec.EIGYOTNSHACD 
                                                           ELSE iRecordS04T001T.DLVRYTNSHACD
                  END )                -- 実績担当者コード
              , itaku_rec.SKYSAKICD -- 請求先コード
              , NULL       -- 請求締年月
              , NULL       -- 請求締日
              , '00000000' -- 請求処理実施日
              , NULL       -- 月次締年月
              , NULL       -- 月次締日
              , '00000000' -- 月次処理実施日
              , iRecordS04T001T.DLVRYDD    -- 売上締処理日
              -- ACCIS2023-6672対応 start ABI
              , NULL       -- 値引締年月
              , NULL       -- 値引締日
              , '00000000' -- 値引処理実施日
              -- ACCIS2023-6672対応 start ABI
              , iRecordS04T001T.PRNTMEMON1 -- 伝票備考
              , NULL -- 伝票番号
              , '0'  -- 請求書出力有無区分
              , '0'  -- 請求処理実施区分
              , iRecordS04T001T.DATSKSMOTOKBN -- データ作成元区分
              , iRecordS04T001T.TAXKEISANKBN  -- 税計算区分
              , NULL -- 仕入伝票番号
              , NULL -- 入金伝票番号
              , NULL -- 入金金額
              , NULL -- 入金種別
              , '1' -- 有効区分
              /* 登録者 */
              , userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
              , userinfo_rec.SHOPCD     -- 更新者ショップコード
              , userinfo_rec.TNSHACD    -- 更新者コード
              , userinfo_rec.SYSTMYMD   -- 更新システム日付
              , userinfo_rec.SYSTMJIKAN -- 更新システム時間
              , userinfo_rec.UNYOYMD    -- 更新運用日付
              , userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
              , userinfo_rec.PGMID      -- 更新プログラムＩＤ
              /* 更新者 */
              , userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
              , userinfo_rec.SHOPCD     -- 更新者ショップコード
              , userinfo_rec.TNSHACD    -- 更新者コード
              , userinfo_rec.SYSTMYMD   -- 更新システム日付
              , userinfo_rec.SYSTMJIKAN -- 更新システム時間
              , userinfo_rec.UNYOYMD    -- 更新運用日付
              , userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
              , userinfo_rec.PGMID      -- 更新プログラムＩＤ
       ); 
    ELSE
-- インボイス対応追加 end ABI
       INSERT INTO S06T001T (
              APKIHONCD     -- ＡＰ基本コード
            , SHOPCD        -- ショップコード
            , URIDNPYNO     -- 売上伝票番号
            , EDANO         -- 枝番号
            , ACPTDNPYNO    -- 受注伝票番号
            , ACPTDNPYEDANO -- 受注伝票枝番号
            , TORIHIKIKBN -- 取引区分
            , TOKUISAKICD -- 得意先コード
            , SIIRESAKICD -- 仕入先コード
            , NYUKAMOTOCD -- 入荷元コード
            , URIDD -- 売上日
            , ACPTDD -- 受注日
            , URIKIN -- 売上金額
            , SHOHITAXKIN -- 消費税金額
-- インボイス対応追加 start ABI
            , SANKOUSHOHITAXKIN -- 参考消費税金額
-- インボイス対応追加 end ABI
            , ACPTTNSHACD -- 受注担当者コード
            , DLVRYTNSHACD -- 配達担当者コード
            , DLVRYKANKATSUAPKIHONCD -- 配達管轄ＡＰ基本コード
            , DLVRYKANKATSUSHOPCD -- 配達管轄ショップコード
            , DLVRYKYOTENCD -- 配達拠点コード
            , DLVRYKBN -- 配達区分
            , DLVRYCMPNYCD -- 配達会社コード
            , SHUKKAMOTOKBN -- 出荷元区分
            , EIGYOTNSHACD -- 営業担当者コード
            , JISSEKITNSHACD -- 実績担当者コード
            , SKYSAKICD -- 請求先コード
            , SKYSHIMEDYDM -- 請求締年月
            , SKYSHIMEDD -- 請求締日
            , SKYPRCSJISSIDD -- 請求処理実施日
            , GETSUJISHIMEDYDM -- 月次締年月
            , GETSUJISHIMEDD -- 月次締日
            , GETSUJIPRCSJISSIDD -- 月次処理実施日
            , URISHIMEPRCSDD -- 売上締処理日
            , DNPYBIKO -- 伝票備考
            , DNPYNO -- 伝票番号
            , SKYSHOOTPTARINASHIKBN -- 請求書出力有無区分
            , SKYPRCSJISSIKBN -- 請求処理実施区分
            , DATSKSMOTOKBN -- データ作成元区分
            , TAXKEISANKBN -- 税計算区分
            , SIIREDNPYNO -- 仕入伝票番号
            , NYKNDNPYNO -- 入金伝票番号
            , NYKNKIN -- 入金金額
            , NYKNKIND -- 入金種別
            , YUKOKBN -- 有効区分
            /* 登録者 */
            , TOSHAAPKIHONCD -- 登録者ＡＰ基本コード
            , TOSHASHOPCD    -- 登録者ショップコード
            , TOSHACD        -- 登録者コード
            , INPTSYSTMYMD   -- 登録システム日付
            , INPTSYSTMJIKAN -- 登録システム時間
            , INPTUNYOYMD    -- 登録運用日付
            , INPTIPADRSE    -- 登録ＩＰアドレス
            , INPTPGMID      -- 登録プログラムＩＤ
            /* 更新者 */
            , KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
            , KOSHASHOPCD    -- 更新者ショップコード
            , KOSHACD        -- 更新者コード
            , KSHNSYSTMYMD   -- 更新システム日付
            , KSHNSYSTMJIKAN -- 更新システム時間
            , KSHNUNYOYMD    -- 更新運用日付
            , KSHNIPADRSE    -- 更新ＩＰアドレス
            , KSHNPGMID      -- 更新プログラムＩＤ 
       ) VALUES (
                iRecordS04T001T.APKIHONCD  -- ＡＰ基本コード
              , iRecordS04T001T.SHOPCD     -- ショップコード
              , iRecordS04T001T.ACPTDNPYNO -- 売上伝票番号
              , iRecordS04T001T.EDANO      -- 枝番号
              , iRecordS04T001T.ACPTDNPYNO -- 受注伝票番号
              , iRecordS04T001T.EDANO      -- 受注伝票枝番号
              , iRecordS04T001T.TORIHIKIKBN -- 取引区分
              , iRecordS04T001T.TOKUISAKICD -- 得意先コード
              , NULL -- 仕入先コード
              , NULL -- 入荷元コード
              , iRecordS04T001T.DLVRYDD -- 売上日
              , iRecordS04T001T.ACPTDD  -- 受注日
              , iRecordS04T001T.NOHINKIN         -- 売上金額
              , iRecordS04T001T.NOHINSHOHITAXKIN -- 消費税金額
-- インボイス対応追加 start ABI
              , 0 -- 参考消費税金額
-- インボイス対応追加 end ABI
              , iRecordS04T001T.ACPTTNSHACD      -- 受注担当者コード
              , iRecordS04T001T.DLVRYTNSHACD           -- 配達担当者コード
              , iRecordS04T001T.DLVRYKANKATSUAPKIHONCD -- 配達管轄ＡＰ基本コード
              , iRecordS04T001T.DLVRYKANKATSUSHOPCD    -- 配達管轄ショップコード
              , iRecordS04T001T.DLVRYKYOTENCD          -- 配達拠点コード
              , iRecordS04T001T.DLVRYKBN               -- 配達区分
              , iRecordS04T001T.DLVRYCMPNYCD           -- 配達会社コード
              , NULL -- 出荷元区分
              , itaku_rec.EIGYOTNSHACD -- 営業担当者コード
              , ( CASE itaku_rec.URIKEIJOTNSHAKBN WHEN '2' THEN itaku_rec.EIGYOTNSHACD 
                                                           ELSE iRecordS04T001T.DLVRYTNSHACD
                  END )                -- 実績担当者コード
              , itaku_rec.SKYSAKICD -- 請求先コード
              , NULL -- 請求締年月
              , NULL -- 請求締日
              , ( CASE itaku_rec.KAISHUHOHOKBN WHEN '7' THEN 'DAIBIKI' 
                                                        ELSE '00000000'
                  END ) -- 請求処理実施日
              , NULL -- 月次締年月
              , NULL -- 月次締日
              , '00000000' -- 月次処理実施日
              , iRecordS04T001T.DLVRYDD -- 売上締処理日
              , iRecordS04T001T.PRNTMEMON1 -- 伝票備考
              , NULL -- 伝票番号
              , '0' -- 請求書出力有無区分
              , '0' -- 請求処理実施区分
              , iRecordS04T001T.DATSKSMOTOKBN -- データ作成元区分
              , iRecordS04T001T.TAXKEISANKBN -- 税計算区分
              , NULL -- 仕入伝票番号
              , NULL -- 入金伝票番号
              , NULL -- 入金金額
              , NULL -- 入金種別
              , '1' -- 有効区分
              /* 登録者 */
              , userinfo_rec.APKIHONCD -- 更新者ＡＰ基本コード
              , userinfo_rec.SHOPCD    -- 更新者ショップコード
              , userinfo_rec.TNSHACD        -- 更新者コード
              , userinfo_rec.SYSTMYMD   -- 更新システム日付
              , userinfo_rec.SYSTMJIKAN -- 更新システム時間
              , userinfo_rec.UNYOYMD    -- 更新運用日付
              , userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
              , userinfo_rec.PGMID      -- 更新プログラムＩＤ        
              /* 更新者 */
              , userinfo_rec.APKIHONCD -- 更新者ＡＰ基本コード
              , userinfo_rec.SHOPCD    -- 更新者ショップコード
              , userinfo_rec.TNSHACD        -- 更新者コード
              , userinfo_rec.SYSTMYMD   -- 更新システム日付
              , userinfo_rec.SYSTMJIKAN -- 更新システム時間
              , userinfo_rec.UNYOYMD    -- 更新運用日付
              , userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
              , userinfo_rec.PGMID      -- 更新プログラムＩＤ        
       ); 
    END IF;
    -- ポイント制度対応追加 start ABI
    -- 登録した予定発生ポイントテーブルに売上情報を追記する。
    -- 予定発生ポイントテーブルに(S20T001T)に売上伝票番号、売上伝票枝番号を登録する
    UPDATE S20T001T
    SET URIDNPYNO = iRecordS04T001T.ACPTDNPYNO
      , URIEDANO = iRecordS04T001T.EDANO
    WHERE KANRINO = cmnRegistKanrino
      AND ACPTDNPYNO = iRecordS04T001T.ACPTDNPYNO
      AND ACPTEDANO = iRecordS04T001T.EDANO;
    -- ポイント制度対応追加 end ABI
  END insertUriage; 
  
  /*****************************************************************************/
  /* プロシージャ名：売上明細テーブル登録                                           */
  /* 引数             ：itaku_rec            委託データ                          */
  /* インボイス対応追加 start ABI                                                */
  /*                  ：jutaku_rec           受託データ                          */
  /* インボイス対応追加 end ABI                                                  */
  /*                  ：iRecordS04T002T      配達済受注明細                      */
  /* インボイス対応追加 start ABI                                                */
  /*                  ：iInvoiceFlg          インボイス施行状況区分              */
  /* 引数（IN OUT）   ：oSankoushohitaxkin   参考消費税金額                      */
  /* インボイス対応追加 end ABI                                                  */
  /*****************************************************************************/
  PROCEDURE insertUriageMeisai( itaku_rec       IN itaku_rec_type   -- 委託データ
-- インボイス対応追加 start ABI
                              , jutaku_rec      IN jutaku_rec_type  -- 受託データ
-- インボイス対応追加 end ABI
                              , iRecordS04T002T IN S04T002T%rowtype -- 配達済受注明細
-- インボイス対応追加 start ABI
                              , iInvoiceFlg     IN VARCHAR2         -- インボイス施行状況区分
                              , oSankoushohitaxkin  IN OUT VARCHAR2 -- 参考消費税金額
-- インボイス対応追加 end ABI
  ) IS 
-- インボイス対応追加 start ABI
  /* 変数宣言 */
  wRtnUntshohitaxkin NUMBER := 0; -- 単価消費税（返却用）
  wRtnshohitaxkin    NUMBER := 0; -- 消費税（返却用）
-- インボイス対応追加 end ABI
  BEGIN 
-- インボイス対応追加 start ABI
    IF iInvoiceFlg = '1' THEN
       -- 参考消費税の取得
       setTax( iRecordS04T002T            -- 明細行
             , iRecordS04T002T.NOHINSURYO -- 数量
             , iRecordS04T002T.NOHINUNT   -- 単価
             , '0'                        -- 税設定区分
             , itaku_rec.TAXKEISANKBN     -- 税計算区分
             , jutaku_rec.DLVRYDD         -- 切替日：配達日
             , jutaku_rec.DLVRYKBN        -- 配達区分
             , wRtnUntshohitaxkin         -- 単価消費税
             , wRtnshohitaxkin            -- 消費税
             , oSankoushohitaxkin         -- 参考消費税
             , iInvoiceFlg                -- インボイス施行状況区分
             );
       
       -- ACCIS2023-7212 セット商品の構成商品の明細行（展開行）については参考消費税を0円とする（配達実績登録と同じ仕様）
       IF iRecordS04T002T.NYURYOKUMEISAIFLG = '0' AND iRecordS04T002T.MEISAIKBN = '0' THEN
         oSankoushohitaxkin := 0;
       END IF;

       -- 売上明細テーブル登録
       INSERT INTO S06T002T ( 
              APKIHONCD          -- ＡＰ基本コード
            , SHOPCD             -- ショップコード
            , URIDNPYNO          -- 売上伝票番号
            , EDANO              -- 枝番号
            , URIDNPYMEISAINO    -- 売上伝票明細番号
            , URIDNPYMEISAIEDANO -- 売上伝票明細枝番号
            , LCTGRYCD           -- 大分類コード
            , MCTGRYCD           -- 中分類コード
            , SCTGRYCD           -- 小分類コード
            , SHNCD              -- 商品コード
            , KEIJOKBN           -- 計上区分
            , SURYO              -- 数量
            , HYOJUNUNT          -- 標準単価
            , UNT                -- 単価
            , KIN                -- 金額
            , UNTSHOHITAXKIN     -- 単価消費税金額
            , SHOHITAXKIN        -- 消費税金額
            , SANKOUSHOHITAXKIN  -- 参考消費税金額
            , TAXKBN             -- 税区分
            , MEISAIKBN          -- 明細区分
            , NYURYOKUMEISAIFLG  -- 入力明細フラグ
            , TANPINMEISAIFLG    -- 単品明細フラグ
            , MEISAIBIKO         -- 明細備考
            , STSHNCD            -- セット商品コード
            , KATASIKI           -- 型式
            , SIIRESURYO         -- 仕入数量
            , SIIREKIN           -- 仕入金額
            , SIIREUNT           -- 仕入単価
            , SIIRESHOHITAXKIN   -- 仕入消費税金額
            , ZAIKOKSHNSUMIFLG   -- 在庫更新済フラグ
-- インボイス対応追加 start ABI
            , TOKUISAKIKBN       -- 得意先区分
            , JUTAKUDLVRYCSTKBN  -- 受託配達顧客区分
-- インボイス対応追加 end ABI
            , YUKOKBN            -- 有効区分
            /* 登録者 */
            , TOSHAAPKIHONCD     -- 登録者ＡＰ基本コード
            , TOSHASHOPCD        -- 登録者ショップコード
            , TOSHACD            -- 登録者コード
            , INPTSYSTMYMD       -- 登録システム日付
            , INPTSYSTMJIKAN     -- 登録システム時間
            , INPTUNYOYMD        -- 登録運用日付
            , INPTIPADRSE        -- 登録ＩＰアドレス
            , INPTPGMID          -- 登録プログラムＩＤ
            /* 更新者 */
            , KOSHAAPKIHONCD     -- 更新者ＡＰ基本コード
            , KOSHASHOPCD        -- 更新者ショップコード
            , KOSHACD            -- 更新者コード
            , KSHNSYSTMYMD       -- 更新システム日付
            , KSHNSYSTMJIKAN     -- 更新システム時間
            , KSHNUNYOYMD        -- 更新運用日付
            , KSHNIPADRSE        -- 更新ＩＰアドレス
            , KSHNPGMID          -- 更新プログラムＩＤ
       ) VALUES (
                iRecordS04T002T.APKIHONCD  -- ＡＰ基本コード
              , iRecordS04T002T.SHOPCD     -- ショップコード
              , iRecordS04T002T.ACPTDNPYNO -- 売上伝票番号
              , iRecordS04T002T.EDANO      -- 枝番号
              , iRecordS04T002T.ACPTDNPYMEISAINO    -- 売上伝票明細番号
              , iRecordS04T002T.ACPTDNPYMEISAIEDANO -- 売上伝票明細枝番号
              , iRecordS04T002T.LCTGRYCD -- 大分類コード
              , iRecordS04T002T.MCTGRYCD -- 中分類コード
              , iRecordS04T002T.SCTGRYCD -- 小分類コード
              , iRecordS04T002T.SHNCD      -- 商品コード
              , iRecordS04T002T.KEIJOKBN   -- 計上区分
              , iRecordS04T002T.NOHINSURYO -- 数量
              , iRecordS04T002T.HYOJUNUNT  -- 標準単価
              , iRecordS04T002T.NOHINUNT   -- 単価
              , iRecordS04T002T.NOHINKIN   -- 金額
              , iRecordS04T002T.NOHINUNTSHOHITAXKIN -- 単価消費税金額
              , 0                                   -- 消費税金額
              , oSankoushohitaxkin                  -- 参考消費税金額
              , iRecordS04T002T.TAXKBN              -- 税区分
              , iRecordS04T002T.MEISAIKBN           -- 明細区分
              , iRecordS04T002T.NYURYOKUMEISAIFLG   -- 入力明細フラグ
              , iRecordS04T002T.TANPINMEISAIFLG     -- 単品明細フラグ
              , iRecordS04T002T.MEISAIBIKO          -- 明細備考
              , iRecordS04T002T.STSHNCD             -- セット商品コード
              , iRecordS04T002T.KATASIKI            -- 型式
              , NULL -- 仕入数量
              , NULL -- 仕入金額
              , NULL -- 仕入単価
              , NULL -- 仕入消費税金額
              , '0' -- 在庫更新済フラグ
-- インボイス対応追加 start ABI
              , itaku_rec.TOKUISAKIKBN      -- 得意先区分
              , itaku_rec.JUTAKUDLVRYCSTKBN -- 受託配達顧客区分
-- インボイス対応追加 end ABI
              , '1' -- 有効区分
              /* 登録者 */
              , iRecordS04T002T.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
              , iRecordS04T002T.KOSHASHOPCD -- 更新者ショップコード
              , iRecordS04T002T.KOSHACD -- 更新者コード
              , iRecordS04T002T.KSHNSYSTMYMD -- 更新システム日付
              , iRecordS04T002T.KSHNSYSTMJIKAN -- 更新システム時間
              , iRecordS04T002T.KSHNUNYOYMD -- 更新運用日付
              , iRecordS04T002T.KSHNIPADRSE -- 更新ＩＰアドレス
              , iRecordS04T002T.KSHNPGMID -- 更新プログラムＩＤ
              /* 更新者 */
              , iRecordS04T002T.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
              , iRecordS04T002T.KOSHASHOPCD -- 更新者ショップコード
              , iRecordS04T002T.KOSHACD -- 更新者コード
              , iRecordS04T002T.KSHNSYSTMYMD -- 更新システム日付
              , iRecordS04T002T.KSHNSYSTMJIKAN -- 更新システム時間
              , iRecordS04T002T.KSHNUNYOYMD -- 更新運用日付
              , iRecordS04T002T.KSHNIPADRSE -- 更新ＩＰアドレス
              , iRecordS04T002T.KSHNPGMID -- 更新プログラムＩＤ
       );
    ELSE
-- インボイス対応追加 end ABI
       -- 売上明細テーブル登録
       INSERT INTO S06T002T ( 
              APKIHONCD -- ＡＰ基本コード
            , SHOPCD -- ショップコード
            , URIDNPYNO -- 売上伝票番号
            , EDANO -- 枝番号
            , URIDNPYMEISAINO -- 売上伝票明細番号
            , URIDNPYMEISAIEDANO -- 売上伝票明細枝番号
            , LCTGRYCD -- 大分類コード
            , MCTGRYCD -- 中分類コード
            , SCTGRYCD -- 小分類コード
            , SHNCD -- 商品コード
            , KEIJOKBN -- 計上区分
            , SURYO -- 数量
            , HYOJUNUNT -- 標準単価
            , UNT -- 単価
            , KIN -- 金額
            , UNTSHOHITAXKIN -- 単価消費税金額
            , SHOHITAXKIN -- 消費税金額
-- インボイス対応追加 start ABI
            , SANKOUSHOHITAXKIN -- 参考消費税金額
-- インボイス対応追加 end ABI
            , TAXKBN -- 税区分
            , MEISAIKBN -- 明細区分
            , NYURYOKUMEISAIFLG -- 入力明細フラグ
            , TANPINMEISAIFLG -- 単品明細フラグ
            , MEISAIBIKO -- 明細備考
            , STSHNCD -- セット商品コード
            , KATASIKI -- 型式
            , SIIRESURYO -- 仕入数量
            , SIIREKIN -- 仕入金額
            , SIIREUNT -- 仕入単価
            , SIIRESHOHITAXKIN -- 仕入消費税金額
            , ZAIKOKSHNSUMIFLG -- 在庫更新済フラグ
-- インボイス対応追加 start ABI
            , TOKUISAKIKBN      -- 得意先区分
            , JUTAKUDLVRYCSTKBN -- 受託配達顧客区分
-- インボイス対応追加 end ABI
            , YUKOKBN -- 有効区分
            /* 登録者 */
            , TOSHAAPKIHONCD -- 登録者ＡＰ基本コード
            , TOSHASHOPCD -- 登録者ショップコード
            , TOSHACD -- 登録者コード
            , INPTSYSTMYMD -- 登録システム日付
            , INPTSYSTMJIKAN -- 登録システム時間
            , INPTUNYOYMD -- 登録運用日付
            , INPTIPADRSE -- 登録ＩＰアドレス
            , INPTPGMID -- 登録プログラムＩＤ
            /* 更新者 */
            , KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
            , KOSHASHOPCD -- 更新者ショップコード
            , KOSHACD -- 更新者コード
            , KSHNSYSTMYMD -- 更新システム日付
            , KSHNSYSTMJIKAN -- 更新システム時間
            , KSHNUNYOYMD -- 更新運用日付
            , KSHNIPADRSE -- 更新ＩＰアドレス
            , KSHNPGMID -- 更新プログラムＩＤ
       ) VALUES (
                iRecordS04T002T.APKIHONCD -- ＡＰ基本コード
              , iRecordS04T002T.SHOPCD -- ショップコード
              , iRecordS04T002T.ACPTDNPYNO -- 売上伝票番号
              , iRecordS04T002T.EDANO -- 枝番号
              , iRecordS04T002T.ACPTDNPYMEISAINO -- 売上伝票明細番号
              , iRecordS04T002T.ACPTDNPYMEISAIEDANO -- 売上伝票明細枝番号
              , iRecordS04T002T.LCTGRYCD -- 大分類コード
              , iRecordS04T002T.MCTGRYCD -- 中分類コード
              , iRecordS04T002T.SCTGRYCD -- 小分類コード
              , iRecordS04T002T.SHNCD -- 商品コード
              , iRecordS04T002T.KEIJOKBN -- 計上区分
              , iRecordS04T002T.NOHINSURYO -- 数量
              , iRecordS04T002T.HYOJUNUNT -- 標準単価
              , iRecordS04T002T.NOHINUNT -- 単価
              , iRecordS04T002T.NOHINKIN -- 金額
              , iRecordS04T002T.NOHINUNTSHOHITAXKIN -- 単価消費税金額
              , iRecordS04T002T.NOHINSHOHITAXKIN -- 消費税金額
-- インボイス対応追加 start ABI
              , 0 -- 参考消費税金額
-- インボイス対応追加 end ABI
              , iRecordS04T002T.TAXKBN -- 税区分
              , iRecordS04T002T.MEISAIKBN -- 明細区分
              , iRecordS04T002T.NYURYOKUMEISAIFLG -- 入力明細フラグ
              , iRecordS04T002T.TANPINMEISAIFLG -- 単品明細フラグ
              , iRecordS04T002T.MEISAIBIKO -- 明細備考
              , iRecordS04T002T.STSHNCD -- セット商品コード
              , iRecordS04T002T.KATASIKI -- 型式
              , NULL -- 仕入数量
              , NULL -- 仕入金額
              , NULL -- 仕入単価
              , NULL -- 仕入消費税金額
              , '0' -- 在庫更新済フラグ
-- インボイス対応追加 start ABI
              , itaku_rec.TOKUISAKIKBN      -- 得意先区分
              , itaku_rec.JUTAKUDLVRYCSTKBN -- 受託配達顧客区分
-- インボイス対応追加 end ABI
              , '1' -- 有効区分
              /* 登録者 */
              , iRecordS04T002T.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
              , iRecordS04T002T.KOSHASHOPCD -- 更新者ショップコード
              , iRecordS04T002T.KOSHACD -- 更新者コード
              , iRecordS04T002T.KSHNSYSTMYMD -- 更新システム日付
              , iRecordS04T002T.KSHNSYSTMJIKAN -- 更新システム時間
              , iRecordS04T002T.KSHNUNYOYMD -- 更新運用日付
              , iRecordS04T002T.KSHNIPADRSE -- 更新ＩＰアドレス
              , iRecordS04T002T.KSHNPGMID -- 更新プログラムＩＤ
              /* 更新者 */
              , iRecordS04T002T.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
              , iRecordS04T002T.KOSHASHOPCD -- 更新者ショップコード
              , iRecordS04T002T.KOSHACD -- 更新者コード
              , iRecordS04T002T.KSHNSYSTMYMD -- 更新システム日付
              , iRecordS04T002T.KSHNSYSTMJIKAN -- 更新システム時間
              , iRecordS04T002T.KSHNUNYOYMD -- 更新運用日付
              , iRecordS04T002T.KSHNIPADRSE -- 更新ＩＰアドレス
              , iRecordS04T002T.KSHNPGMID -- 更新プログラムＩＤ
       );
    END IF;
  END insertUriageMeisai; 
  
  /*****************************************************************************/
  /* プロシージャ名：売上処理                                                     */
  /* 引数             ：itaku_rec      委託データ                                */
  /* インボイス対応追加 start ABI                                               */
  /*                  ：jutaku_rec     受託データ                               */
  /* 引数（IN OUT）   ：iInvoiceFlg    インボイス施行状況区分                   */
  /* インボイス対応追加 end ABI                                                 */
  /*****************************************************************************/
  PROCEDURE processUriage( itaku_rec       IN itaku_rec_type  -- 委託データ
-- インボイス対応追加 start ABI
                         , jutaku_rec      IN jutaku_rec_type -- 受託データ
                         , iInvoiceFlg     IN OUT VARCHAR2    -- インボイス施行状況区分
-- インボイス対応追加 end ABI
  ) IS 
  wRecordS04T001T           S04T001T%rowtype := NULL; -- 受注配達テーブル
  wRecordS04T002T           S04T002T%rowtype := NULL; -- 受注配達明細テーブル  
  allZero                  BOOLEAN := TRUE; -- 明細納品数量オールゼロ
-- インボイス対応追加 start ABI
  wIppantaxkin                  VARCHAR2(11) := 0; -- 課税消費税金額
  wKeigentaxkin                 VARCHAR2(11) := 0; -- 軽減課税消費税金額
  wSankoushohitaxkin            VARCHAR2(11) := 0; -- 参考消費税金額
  wRtnsankoushohitaxkin         VARCHAR2(11) := 0; -- 参考消費税金額（返却用）
  wShohitaxkin                  VARCHAR2(11) := 0; -- 消費税金額
-- インボイス対応追加 end ABI

  /* CURSOR：明細データ抽出 */
  CURSOR SELECT_MEISAI IS 
    SELECT JHMT_JUTAKU.* 
      FROM S04T002T JHMT_JUTAKU 
     WHERE JHMT_JUTAKU.APKIHONCD  = itaku_rec.APKIHONCD  
       AND JHMT_JUTAKU.SHOPCD     = itaku_rec.SHOPCD 
       AND JHMT_JUTAKU.ACPTDNPYNO = itaku_rec.ACPTDNPYNO 
       AND JHMT_JUTAKU.EDANO      = itaku_rec.EDANO 
       AND JHMT_JUTAKU.MEISAIKBN IN ('0', '3') -- 商品、消費税調整のみを対象とする
       AND JHMT_JUTAKU.YUKOKBN = '1'
  ; 
  BEGIN 
    -- 受注明細データを元に新しい売上明細データを作る
    OPEN SELECT_MEISAI;
      LOOP 
        FETCH SELECT_MEISAI INTO wRecordS04T002T; 
        EXIT WHEN SELECT_MEISAI%NOTFOUND;
        
        IF wRecordS04T002T.NOHINSURYO <> 0 THEN 
           allZero := FALSE; 
           -- 売上明細テーブルに登録
-- インボイス対応変更 start ABI
--           insertUriageMeisai( itaku_rec, wRecordS04T002T ); 
           insertUriageMeisai( itaku_rec, jutaku_rec, wRecordS04T002T, iInvoiceFlg, wRtnsankoushohitaxkin ); 
-- インボイス対応変更 end ABI
-- インボイス対応追加 start ABI
           IF iInvoiceFlg = '1' THEN
              -- 課税消費税金額の設定
              IF wRecordS04T002T.TAXKBN = '1' THEN
                 wIppantaxkin := wIppantaxkin + wRecordS04T002T.NOHINSHOHITAXKIN;
              END IF;
              -- 軽減課税消費税金額の設定
              IF wRecordS04T002T.TAXKBN = '4' THEN
                 wKeigentaxkin := wKeigentaxkin + wRecordS04T002T.NOHINSHOHITAXKIN;
              END IF;
              -- 参考消費税金額の設定
              wSankoushohitaxkin := wSankoushohitaxkin + wRtnsankoushohitaxkin;
           END IF;
-- インボイス対応追加 end ABI
        END IF; 
      END LOOP; 
    CLOSE SELECT_MEISAI;
    
    IF NOT allZero THEN 
       -- 更新済受注伝票取得
       SELECT JHTT.* 
         INTO wRecordS04T001T 
         FROM S04T001T JHTT 
        WHERE JHTT.APKIHONCD  = itaku_rec.APKIHONCD 
          AND JHTT.SHOPCD     = itaku_rec.SHOPCD 
          AND JHTT.ACPTDNPYNO = itaku_rec.ACPTDNPYNO 
          AND JHTT.EDANO      = itaku_rec.EDANO 
          AND JHTT.YUKOKBN = '1' 
       ; 
-- インボイス対応追加 start ABI
       IF iInvoiceFlg = '1' THEN
          wShohitaxkin := wIppantaxkin + wKeigentaxkin;
       END IF;
-- インボイス対応追加 end ABI
       -- 売上テーブルに登録 
-- インボイス対応変更 start ABI
--       insertUriage( itaku_rec, wRecordS04T001T );
       insertUriage( itaku_rec, wRecordS04T001T, wShohitaxkin, wSankoushohitaxkin, iInvoiceFlg );
-- インボイス対応変更 end ABI
    END IF; 
    
    EXCEPTION 
      WHEN NO_DATA_FOUND THEN 
        NULL;
  END processUriage;
  
  /*****************************************************************************/
  /* プロシージャ名：会員入会一連処理                                              */
  /* 引数             ：iApkihoncd      ＡＰ基本コード                           */
  /*                  ：iShopcd         ショップコード                           */
  /*                  ：iTokuisakicd    得意先コード                            */
  /*****************************************************************************/
  PROCEDURE processKaiinNyukai( iApkihoncd     IN VARCHAR2 -- ＡＰ基本コード 
                              , iShopcd        IN VARCHAR2 -- ショップコード 
                              , iTokuisakicd   IN VARCHAR2 -- 得意先コード 
  ) IS 
  /* 変数宣言 */
  tokuisakiMST       S02M013T%rowtype := NULL; -- 得意先マスタ 
  kbnShozai          VARCHAR2(1) := NULL; -- 当得意先所在管理区分 
  nRenban            NUMBER := NULL; -- 連番 
  /* CURSOR：会員旧所在 */
  CURSOR SELECT_SHOZAI IS 
    SELECT KIST.*  
      FROM S03T022T KIST 
     WHERE KIST.KAIINCD     = tokuisakiMST.KAIINCD 
       AND KIST.APKIHONCD || KIST.SHOPCD || KIST.TOKUISAKICD 
           <> iApkihoncd || iShopcd || iTokuisakicd 
       AND KIST.SZIKANRIKBN = '1' 
       AND KIST.YUKOKBN = '1' 
    FOR UPDATE 
  ; 
  BEGIN 
    -- 得意先マスタ取得 
    SELECT TKSM.* 
      INTO tokuisakiMST 
      FROM S02M013T TKSM 
     WHERE TKSM.APKIHONCD   = iApkihoncd 
       AND TKSM.SHOPCD      = iShopcd 
       AND TKSM.TOKUISAKICD = iTokuisakicd 
       AND TKSM.YUKOKBN = '1' 
    ; 
    IF tokuisakiMST.NYUKAIDD IS NOT NULL AND tokuisakiMST.KAIINCD IS NOT NULL THEN 
       FOR vShozai_old IN SELECT_SHOZAI LOOP 
           IF tokuisakiMST.NYUKAIDD >= vShozai_old.NYUKAIDD THEN 
              kbnShozai := '1'; 
              UPDATE S03T022T KIST 
                 SET KIST.SZIKANRIKBN = '0' -- 所在管理区分コード 
                  /* 更新者 */
                   , KIST.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD -- 更新者ＡＰ基本コード
                   , KIST.KOSHASHOPCD    = userinfo_rec.SHOPCD    -- 更新者ショップコード
                   , KIST.KOSHACD        = userinfo_rec.TNSHACD        -- 更新者コード
                   , KIST.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
                   , KIST.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
                   , KIST.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
                   , KIST.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
                   , KIST.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
               WHERE KIST.APKIHONCD   = vShozai_old.APKIHONCD 
                 AND KIST.SHOPCD      = vShozai_old.SHOPCD 
                 AND KIST.TOKUISAKICD = vShozai_old.TOKUISAKICD 
                 AND KIST.KAIINCD     = vShozai_old.KAIINCD 
                 AND KIST.RENBAN  = vShozai_old.RENBAN 
                 AND KIST.YUKOKBN = '1' 
              ; 
           ELSE 
              kbnShozai := '0'; 
           END IF; 
       END LOOP; 
    
       -- 会員所在管理テーブル更新 
       UPDATE S03T022T KIST 
          SET KIST.KAIINCD = tokuisakiMST.KAIINCD -- 会員コード 
            , KIST.SZIKANRIKBN = NVL(kbnShozai, '1') -- 所在管理区分 
            , KIST.NYUKAIDD = tokuisakiMST.NYUKAIDD -- 入会日 
            , KIST.TAIKAIDD = tokuisakiMST.TAIKAIDD -- 退会日 
            , KIST.RENRAKUSAKINON1 = tokuisakiMST.RENRAKUSAKINON1 -- 連絡先番号１ 
            , KIST.RENRAKUSAKINON2 = tokuisakiMST.RENRAKUSAKINON2 -- 連絡先番号２ 
            , KIST.RENRAKUSAKINON3 = tokuisakiMST.RENRAKUSAKINON3 -- 連絡先番号３ 
            , KIST.RENRAKUSAKINON4 = tokuisakiMST.RENRAKUSAKINON4 -- 連絡先番号４ 
            /* 更新者 */
            , KIST.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD -- 更新者ＡＰ基本コード
            , KIST.KOSHASHOPCD    = userinfo_rec.SHOPCD    -- 更新者ショップコード
            , KIST.KOSHACD        = userinfo_rec.TNSHACD        -- 更新者コード
            , KIST.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
            , KIST.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
            , KIST.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
            , KIST.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
            , KIST.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
        WHERE KIST.APKIHONCD   = iApkihoncd 
          AND KIST.SHOPCD      = iShopcd 
          AND KIST.TOKUISAKICD = iTokuisakicd 
          AND KIST.YUKOKBN = '1' 
       ; 
       -- 当得意先の会員所在データがない場合：登録 
       IF SQL%ROWCOUNT = 0 THEN 
          -- 連番取得 
          BEGIN 
            SELECT MAX(KIST.RENBAN) 
              INTO nRenban 
              FROM S03T022T KIST 
             WHERE KIST.KAIINCD = tokuisakiMST.KAIINCD 
            ; 
            EXCEPTION 
              WHEN OTHERS THEN 
                nRenban := 0; 
          END; 
          
          INSERT INTO S03T022T ( 
              APKIHONCD   -- ＡＰ基本コード 
            , SHOPCD      -- ショップコード 
            , TOKUISAKICD -- 得意先コード 
            , KAIINCD -- 会員コード 
            , RENBAN  -- 連番 
            , SZIKANRIKBN -- 所在管理区分 
            , NYUKAIDD -- 入会日
            , TAIKAIDD -- 退会日
            , RENRAKUSAKINON1 -- 連絡先番号１
            , RENRAKUSAKINON2 -- 連絡先番号２
            , RENRAKUSAKINON3 -- 連絡先番号３
            , RENRAKUSAKINON4 -- 連絡先番号４
            , N1N2RITTORUBTLSURYO -- １２Ｌボトル数量
            , N8RITTORUBTLSURYO   -- ８Ｌボトル数量
            , N7RITTORUBTLSURYO   -- ７Ｌボトル数量
            , VANADIUMN8RITTORUBTLSURYO -- バナジウム８Ｌボトル数量
            , SONOTASEIHINMIZUSURYO     -- その他製品水数量
            , YBI1BTLSURYO -- 予備１ボトル数量
            , YBI2BTLSURYO -- 予備２ボトル数量
            , YBI3BTLSURYO -- 予備３ボトル数量
            , SEIHINMIZUURIAGEDAKA -- 製品水売上高
            , TEIREIURIAGEDAKA     -- 定例売上高
            , SONOTAURIAGEDAKA     -- その他売上高
            , SMURIAGEDAKA         -- 合計売上高
            , TTBETSUKAIINDKIKAN -- 得意先別会員期間 
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
          ) VALUES ( 
              iApkihoncd   -- ＡＰ基本コード 
            , iShopcd      -- ショップコード 
            , iTokuisakicd -- 得意先コード 
            , tokuisakiMST.KAIINCD -- 会員コード 
            , NVL(nRenban, 0) + 1 -- 連番 
            , NVL(kbnShozai, '1') -- 所在管理区分 
            , tokuisakiMST.NYUKAIDD -- 入会日
            , tokuisakiMST.TAIKAIDD -- 退会日
            , tokuisakiMST.RENRAKUSAKINON1 -- 連絡先番号１
            , tokuisakiMST.RENRAKUSAKINON2 -- 連絡先番号２
            , tokuisakiMST.RENRAKUSAKINON3 -- 連絡先番号３
            , tokuisakiMST.RENRAKUSAKINON4 -- 連絡先番号４
            , 0 -- １２Ｌボトル数量
            , 0 -- ８Ｌボトル数量
            , 0 -- ７Ｌボトル数量
            , 0 -- バナジウム８Ｌボトル数量
            , 0 -- その他製品水数量
            , 0 -- 予備１ボトル数量
            , 0 -- 予備２ボトル数量
            , 0 -- 予備３ボトル数量
            , 0 -- 製品水売上高
            , 0 -- 定例売上高
            , 0 -- その他売上高
            , 0 -- 合計売上高
            , 0 -- 得意先別会員期間 
            /* テーブル共通項目 */
            , '1' -- 有効区分
            , userinfo_rec.APKIHONCD  -- 登録者ＡＰ基本コード
            , userinfo_rec.SHOPCD     -- 登録者ショップコード
            , userinfo_rec.TNSHACD    -- 登録者コード
            , userinfo_rec.SYSTMYMD   -- 登録システム日付
            , userinfo_rec.SYSTMJIKAN -- 登録システム時間
            , userinfo_rec.UNYOYMD    -- 登録運用日付
            , userinfo_rec.IPADRSE    -- 登録ＩＰアドレス
            , userinfo_rec.PGMID      -- 登録プログラムＩＤ
            , userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
            , userinfo_rec.SHOPCD     -- 更新者ショップコード
            , userinfo_rec.TNSHACD    -- 更新者コード
            , userinfo_rec.SYSTMYMD   -- 更新システム日付
            , userinfo_rec.SYSTMJIKAN -- 更新システム時間
            , userinfo_rec.UNYOYMD    -- 更新運用日付
            , userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
            , userinfo_rec.PGMID      -- 更新プログラムＩＤ
       );
       END IF; 
    END IF;
    
    -- 会員マスタ更新：新規会員または現所在の場合 
    IF ( kbnShozai IS NULL OR kbnShozai = '1' ) 
       AND tokuisakiMST.NYUKAIDD IS NOT NULL THEN 
       UPDATE S02M081T KINM 
          SET KINM.KAIINNM1  = tokuisakiMST.TOKUISAKINMN1 -- 会員名称１ 
            , KINM.KAIINNMN2 = tokuisakiMST.TOKUISAKINMN2 -- 会員名称２ 
            , KINM.YUBINNO = tokuisakiMST.YUBINNO -- 郵便番号 
            , KINM.CHIHOUPUBDANTICDN1 = tokuisakiMST.CHIHOUPUBDANTICDN1 -- 地方公共団体コード１ 
            , KINM.CHIHOUPUBDANTICDN2 = tokuisakiMST.CHIHOUPUBDANTICDN2 -- 地方公共団体コード２ 
            , KINM.ADRSN1 = tokuisakiMST.ADRSN1 -- 住所１ 
            , KINM.ADRSN2 = tokuisakiMST.ADRSN2 -- 住所２ 
            , KINM.ADRSN3 = tokuisakiMST.ADRSN3 -- 住所３ 
            , KINM.RENRAKUSAKINOKBNN1 = tokuisakiMST.RENRAKUSAKINOKBNN1 -- 連絡先番号区分１ 
            , KINM.RENRAKUSAKINON1    = tokuisakiMST.RENRAKUSAKINON1    -- 連絡先番号１ 
            , KINM.RENRAKUSAKIMEMON1  = tokuisakiMST.RENRAKUSAKIMEMON1  -- 連絡先メモ１ 
            , KINM.RENRAKUSAKINOKBNN2 = tokuisakiMST.RENRAKUSAKINOKBNN2    -- 連絡先番号区分２ 
            , KINM.RENRAKUSAKINON2    = tokuisakiMST.RENRAKUSAKINON2    -- 連絡先番号２ 
            , KINM.RENRAKUSAKIMEMON2  = tokuisakiMST.RENRAKUSAKIMEMON2  -- 連絡先メモ２ 
            , KINM.RENRAKUSAKINOKBNN3 = tokuisakiMST.RENRAKUSAKINOKBNN3 -- 連絡先番号区分３ 
            , KINM.RENRAKUSAKINON3    = tokuisakiMST.RENRAKUSAKINON3    -- 連絡先番号３ 
            , KINM.RENRAKUSAKIMEMON3  = tokuisakiMST.RENRAKUSAKIMEMON3  -- 連絡先メモ３
            , KINM.RENRAKUSAKINOKBNN4 = tokuisakiMST.RENRAKUSAKINOKBNN4 -- 連絡先番号区分４ 
            , KINM.RENRAKUSAKINON4    = tokuisakiMST.RENRAKUSAKINON4    -- 連絡先番号４ 
            , KINM.RENRAKUSAKIMEMON4  = tokuisakiMST.RENRAKUSAKIMEMON4  -- 連絡先メモ４ 
            , KINM.MAILADRSE = tokuisakiMST.MAILADRSE -- メールアドレス
            , KINM.SETAINUSHI = tokuisakiMST.SETAINUSHI -- 世帯主
            , KINM.KAZOKUKOSEIKAZUDANSEI = tokuisakiMST.KAZOKUKOSEIKAZUDANSEI -- 利用者数
            , KINM.KAZOKUKOSEIKAZUJOSEI  = tokuisakiMST.KAZOKUKOSEIKAZUJOSEI     -- 内子供数
            , KINM.KOJUDYKAZUKBN = tokuisakiMST.KOJUDYKAZUKBN -- 居住年数区分
            , KINM.PETJOHOKBN  = tokuisakiMST.PETJOHOKBN -- ペット情報区分
            , KINM.SETAISYUNYU = tokuisakiMST.SETAISYUNYU -- 世帯収入
            , KINM.DOITSUSEKENINZUKBN = tokuisakiMST.DOITSUSEKENINZUKBN -- 同一生計人数区分
            , KINM.GAIKOKUJINNINZU = tokuisakiMST.GAIKOKUJINNINZU -- 外国人人数
            , KINM.JUGYOINKIBKBN   = tokuisakiMST.JUGYOINKIBKBN -- 従業員規模区分
            , KINM.RIYOMOKUTEKIKBN = tokuisakiMST.RIYOMOKUTEKIKBN -- 利用目的区分
            , KINM.SEIBETSUKBN   = tokuisakiMST.SEIBETSUKBN -- 性別区分
            , KINM.NYUSHATANJODD = tokuisakiMST.NYUSHATANJODD -- 入会者誕生日
            , KINM.SYKUGYOKBN = tokuisakiMST.SYKUGYOKBN -- 職業区分
            , KINM.NYUSHADYDM = tokuisakiMST.NYUSHADYDM -- 入社年月
            , KINM.SHIHASYAZOKUKBN = tokuisakiMST.SHIHASYAZOKUKBN -- 支払者続柄区分
            , KINM.HOSHOKINKBN  = tokuisakiMST.HOSHOKINKBN -- 保証金区分
            , KINM.CDKINDKBN    = tokuisakiMST.CDKINDKBN -- コード種別区分
            , KINM.GYOSHUKBN    = tokuisakiMST.GYOSHUKBN    -- 業種区分
            , KINM.GYOSHUSHSKBN = tokuisakiMST.GYOSHUSHSKBN -- 業種詳細区分
            , KINM.FTTBNYUKAIDD = ( CASE WHEN tokuisakiMST.FTTBNYUKAIFLG = '1' THEN tokuisakiMST.NYUKAIDD 
                                         ELSE NULL 
                                    END ) -- 再入会日 
            , KINM.TAIKAIDD      = tokuisakiMST.TAIKAIDD -- 退会日 
            , KINM.TAIKAIRIYUKBN = tokuisakiMST.TAIKAIRIYUKBN -- 退会理由区分 
            , KINM.SAISHINRKNKINDKIHONFLG = tokuisakiMST.RKNKINDKIHONFLG -- 最新料金種別基本フラグ 
            , KINM.SAISHINRKNKINDTOKUBETSUUNTFLG = tokuisakiMST.RKNKINDTOKUBETSUUNTFLG -- 最新料金種別特別単価フラグ 
            , KINM.SAISHINRKNKINDTOKUBETSUNBKFLG = tokuisakiMST.RKNKINDTOKUBETSUNEBIKIFLG -- 最新料金種別特別値引フラグ 
            , KINM.SAISHINPLNCD  = tokuisakiMST.PLNCD  -- 最新プランコード 
            , KINM.SAISHINPLNCD2 = tokuisakiMST.PLNCD2 -- 最新プランコード２ 
            , KINM.SAISHINPLNCD3 = tokuisakiMST.PLNCD3 -- 最新プランコード３ 
            , KINM.KOJINSYOUMEIKINDKBN = tokuisakiMST.KOJINSYOUMEIKINDKBN -- 個人証明種別区分 
            , KINM.KOJINSYOUMEINO      = tokuisakiMST.KOJINSYOUMEINO      -- 個人証明番号 
            , KINM.SAISHINRCPTKBN      = tokuisakiMST.RCPTKBN -- 最新受付区分 
            , KINM.SAISHINKAKUTOKUKBN  = tokuisakiMST.KAKUTOKUKBN -- 最新獲得区分
            , KINM.TOKUISAKISHOSHAKAZU = tokuisakiMST.TOKUISAKISHOSHAKAZU -- 得意先紹介者数 
            , KINM.SKSAPKIHONCD = ( CASE WHEN KINM.SKSAPKIHONCD  = tokuisakiMST.APKIHONCD 
                                              AND KINM.SKSSHOPCD = tokuisakiMST.SHOPCD 
                                              AND KINM.SKYSAKICD = tokuisakiMST.SKYSAKICD 
                                         THEN KINM.SKSAPKIHONCD 
                                         ELSE tokuisakiMST.APKIHONCD 
                                    END ) -- 請求先ＡＰ基本コード 
            , KINM.SKSSHOPCD    = ( CASE WHEN KINM.SKSAPKIHONCD  = tokuisakiMST.APKIHONCD 
                                              AND KINM.SKSSHOPCD = tokuisakiMST.SHOPCD 
                                              AND KINM.SKYSAKICD = tokuisakiMST.SKYSAKICD 
                                         THEN KINM.SKSSHOPCD 
                                         ELSE tokuisakiMST.SHOPCD 
                                    END ) -- 請求先ショップコード 
            , KINM.SKYSAKICD    = ( CASE WHEN KINM.SKSAPKIHONCD  = tokuisakiMST.APKIHONCD 
                                              AND KINM.SKSSHOPCD = tokuisakiMST.SHOPCD 
                                              AND KINM.SKYSAKICD = tokuisakiMST.SKYSAKICD 
                                         THEN KINM.SKYSAKICD 
                                         ELSE tokuisakiMST.SKYSAKICD 
                                    END ) -- 請求先コード 
            , KINM.ZNKSKSAPKIHONCD = ( CASE WHEN KINM.SKSAPKIHONCD  = tokuisakiMST.APKIHONCD 
                                                 AND KINM.SKSSHOPCD = tokuisakiMST.SHOPCD 
                                                 AND KINM.SKYSAKICD = tokuisakiMST.SKYSAKICD 
                                            THEN KINM.ZNKSKSAPKIHONCD 
                                            ELSE KINM.SKSAPKIHONCD 
                                       END ) -- 前回請求先ＡＰ基本コード 
            , KINM.ZNKSKSSHOPCD    = ( CASE WHEN KINM.SKSAPKIHONCD  = tokuisakiMST.APKIHONCD 
                                                 AND KINM.SKSSHOPCD = tokuisakiMST.SHOPCD 
                                                 AND KINM.SKYSAKICD = tokuisakiMST.SKYSAKICD 
                                            THEN KINM.ZNKSKSSHOPCD 
                                            ELSE KINM.SKSSHOPCD 
                                       END ) -- 前回請求先ショップコード 
            , KINM.ZNKSKSCD        = ( CASE WHEN KINM.SKSAPKIHONCD  = tokuisakiMST.APKIHONCD 
                                                 AND KINM.SKSSHOPCD = tokuisakiMST.SHOPCD 
                                                 AND KINM.SKYSAKICD = tokuisakiMST.SKYSAKICD 
                                            THEN KINM.ZNKSKSCD 
                                            ELSE KINM.SKYSAKICD 
                                       END ) -- 前回請求先コード 
            , KINM.TTAPKIHONCD     = ( CASE WHEN KINM.TTAPKIHONCD         = tokuisakiMST.APKIHONCD 
                                                 AND KINM.TOKUISAKISHOPCD = tokuisakiMST.SHOPCD 
                                                 AND KINM.TOKUISAKICD     = tokuisakiMST.TOKUISAKICD 
                                            THEN KINM.TTAPKIHONCD 
                                            ELSE tokuisakiMST.APKIHONCD 
                                       END ) -- 得意先ＡＰ基本コード
            , KINM.TOKUISAKISHOPCD = ( CASE WHEN KINM.TTAPKIHONCD         = tokuisakiMST.APKIHONCD 
                                                 AND KINM.TOKUISAKISHOPCD = tokuisakiMST.SHOPCD 
                                                 AND KINM.TOKUISAKICD     = tokuisakiMST.TOKUISAKICD 
                                            THEN KINM.TOKUISAKISHOPCD 
                                            ELSE tokuisakiMST.SHOPCD 
                                       END ) -- 得意先ショップコード
            , KINM.TOKUISAKICD     = ( CASE WHEN KINM.TTAPKIHONCD         = tokuisakiMST.APKIHONCD 
                                                 AND KINM.TOKUISAKISHOPCD = tokuisakiMST.SHOPCD 
                                                 AND KINM.TOKUISAKICD     = tokuisakiMST.TOKUISAKICD 
                                            THEN KINM.TOKUISAKICD 
                                            ELSE tokuisakiMST.TOKUISAKICD 
                                       END ) -- 得意先コード
            , KINM.ZNKTTAPKIHONCD  = ( CASE WHEN KINM.TTAPKIHONCD         = tokuisakiMST.APKIHONCD 
                                                 AND KINM.TOKUISAKISHOPCD = tokuisakiMST.SHOPCD 
                                                 AND KINM.TOKUISAKICD     = tokuisakiMST.TOKUISAKICD 
                                            THEN KINM.ZNKTTAPKIHONCD 
                                            ELSE KINM.TTAPKIHONCD 
                                       END ) -- 前回得意先ＡＰ基本コード
            , KINM.ZNKTTSHOPCD     = ( CASE WHEN KINM.TTAPKIHONCD         = tokuisakiMST.APKIHONCD 
                                                 AND KINM.TOKUISAKISHOPCD = tokuisakiMST.SHOPCD 
                                                 AND KINM.TOKUISAKICD     = tokuisakiMST.TOKUISAKICD 
                                            THEN KINM.ZNKTTSHOPCD 
                                            ELSE KINM.TOKUISAKISHOPCD 
                                       END ) -- 前回得意先ショップコード
            , KINM.ZNKTTCD         = ( CASE WHEN KINM.TTAPKIHONCD         = tokuisakiMST.APKIHONCD 
                                                 AND KINM.TOKUISAKISHOPCD = tokuisakiMST.SHOPCD 
                                                 AND KINM.TOKUISAKICD     = tokuisakiMST.TOKUISAKICD 
                                            THEN KINM.ZNKTTCD 
                                            ELSE KINM.TOKUISAKICD 
                                       END ) -- 前回得意先コード 
            , KINM.YUKOKBN = '1' 
            /* 更新者：更新なし */
        WHERE KINM.KAIINCD   = tokuisakiMST.KAIINCD 
       ; 
       
       IF SQL%ROWCOUNT = 0 THEN 
          INSERT INTO S02M081T ( 
                 KAIINCD -- 会員コード 
               , KAIINNM1  -- 会員名称１ 
               , KAIINNMN2 -- 会員名称２ 
               , YUBINNO -- 郵便番号 
               , CHIHOUPUBDANTICDN1 -- 地方公共団体コード１ 
               , CHIHOUPUBDANTICDN2 -- 地方公共団体コード２ 
               , ADRSN1 -- 住所１ 
               , ADRSN2 -- 住所２ 
               , ADRSN3 -- 住所３ 
               , RENRAKUSAKINOKBNN1 -- 連絡先番号区分１ 
               , RENRAKUSAKINON1    -- 連絡先番号１ 
               , RENRAKUSAKIMEMON1  -- 連絡先メモ１ 
               , RENRAKUSAKINOKBNN2    -- 連絡先番号区分２ 
               , RENRAKUSAKINON2    -- 連絡先番号２ 
               , RENRAKUSAKIMEMON2  -- 連絡先メモ２ 
               , RENRAKUSAKINOKBNN3 -- 連絡先番号区分３ 
               , RENRAKUSAKINON3    -- 連絡先番号３ 
               , RENRAKUSAKIMEMON3  -- 連絡先メモ３ 
               , RENRAKUSAKINOKBNN4 -- 連絡先番号区分４ 
               , RENRAKUSAKINON4    -- 連絡先番号４ 
               , RENRAKUSAKIMEMON4  -- 連絡先メモ４ 
               , MAILADRSE -- メールアドレス
               , SETAINUSHI -- 世帯主
               , KAZOKUKOSEIKAZUDANSEI -- 利用者数
               , KAZOKUKOSEIKAZUJOSEI     -- 内子供数
               , KOJUDYKAZUKBN -- 居住年数区分
               , PETJOHOKBN -- ペット情報区分
               , SETAISYUNYU -- 世帯収入
               , DOITSUSEKENINZUKBN -- 同一生計人数区分
               , GAIKOKUJINNINZU -- 外国人人数
               , JUGYOINKIBKBN -- 従業員規模区分
               , RIYOMOKUTEKIKBN -- 利用目的区分
               , SEIBETSUKBN -- 性別区分
               , NYUSHATANJODD -- 入会者誕生日
               , SYKUGYOKBN -- 職業区分
               , NYUSHADYDM -- 入社年月
               , SHIHASYAZOKUKBN -- 支払者続柄区分
               , HOSHOKINKBN -- 保証金区分
               , CDKINDKBN -- コード種別区分
               , GYOSHUKBN -- 業種区分
               , GYOSHUSHSKBN -- 業種詳細区分 
               , MOSHIKOMIRCPTDD -- 申込受付日 
               , SHOKAIWSVSTUPDD -- 初回サーバー設置日 
               , NYUKAIDD -- 入会日 
               , FTTBNYUKAIDD -- 再入会日 
               , TAIKAIDD -- 退会日 
               , TAIKAIRIYUKBN -- 退会理由区分 
               , RKNKINDKIHONFLG -- 料金種別基本フラグ（初回） 
               , RKNKINDTOKUBETSUUNTFLG -- 料金種別特別単価フラグ（初回） 
               , RKNKINDTOKUBETSUNEBIKIFLG -- 料金種別特別値引フラグ（初回） 
               , PLNCD -- プランコード（初回） 
               , PLNCD2 -- プランコード２（初回） 
               , PLNCD3 -- プランコード３（初回） 
               , SAISHINRKNKINDKIHONFLG -- 最新料金種別基本フラグ
               , SAISHINRKNKINDTOKUBETSUUNTFLG -- 最新料金種別特別単価フラグ
               , SAISHINRKNKINDTOKUBETSUNBKFLG -- 最新料金種別特別値引フラグ
               , SAISHINPLNCD  -- 最新プランコード
               , SAISHINPLNCD2 -- 最新プランコード２
               , SAISHINPLNCD3 -- 最新プランコード３ 
               , KOJINSYOUMEIKINDKBN -- 個人証明種別区分 
               , KOJINSYOUMEINO      -- 個人証明番号 
               , KEIZOKURNK -- 継続ランク 
               , SHOHIRNK   -- 消費ランク 
               , RCPTKBN -- 受付区分 
               , KAKUTOKUKBN -- 獲得区分
               , SAISHINRCPTKBN -- 最新受付区分
               , SAISHINKAKUTOKUKBN -- 最新獲得区分
               , TOKUISAKISHOSHAKAZU -- 得意先紹介者数 
               , SKSAPKIHONCD -- 請求先ＡＰ基本コード 
               , SKSSHOPCD -- 請求先ショップコード 
               , SKYSAKICD -- 請求先コード 
               , ZNKSKSAPKIHONCD -- 前回請求先ＡＰ基本コード 
               , ZNKSKSSHOPCD -- 前回請求先ショップコード 
               , ZNKSKSCD -- 前回請求先コード 
               , TTAPKIHONCD -- 得意先ＡＰ基本コード
               , TOKUISAKISHOPCD -- 得意先ショップコード
               , TOKUISAKICD -- 得意先コード
               , ZNKTTAPKIHONCD -- 前回得意先ＡＰ基本コード
               , ZNKTTSHOPCD -- 前回得意先ショップコード
               , ZNKTTCD -- 前回得意先コード
               , N1N2RITTORUBTLSURYO -- １２Ｌボトル数量
               , N8RITTORUBTLSURYO   -- ８Ｌボトル数量
               , N7RITTORUBTLSURYO   -- ７Ｌボトル数量
               , VANADIUMN8RITTORUBTL  -- バナジウム８Ｌボトル
               , SONOTASEIHINMIZUSURYO -- その他製品水数量
               , YBI1BTL -- 予備１ボトル
               , YBI2BTL -- 予備２ボトル
               , YBI3BTL -- 予備３ボトル
               , SEIHINMIZUURIAGEDAKA -- 製品水売上高
               , TEIREIURIAGEDAKA     -- 定例売上高
               , SONOTAURIAGEDAKA     -- その他売上高
               , SMURIAGEDAKA         -- 合計売上高
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
          ) VALUES ( 
                   tokuisakiMST.KAIINCD -- 会員コード 
                 , tokuisakiMST.TOKUISAKINMN1 -- 会員名称１ 
                 , tokuisakiMST.TOKUISAKINMN2 -- 会員名称２ 
                 , tokuisakiMST.YUBINNO -- 郵便番号 
                 , tokuisakiMST.CHIHOUPUBDANTICDN1 -- 地方公共団体コード１ 
                 , tokuisakiMST.CHIHOUPUBDANTICDN2 -- 地方公共団体コード２ 
                 , tokuisakiMST.ADRSN1 -- 住所１ 
                 , tokuisakiMST.ADRSN2 -- 住所２ 
                 , tokuisakiMST.ADRSN3 -- 住所３ 
                 , tokuisakiMST.RENRAKUSAKINOKBNN1 -- 連絡先番号区分１ 
                 , tokuisakiMST.RENRAKUSAKINON1    -- 連絡先番号１ 
                 , tokuisakiMST.RENRAKUSAKIMEMON1  -- 連絡先メモ１ 
                 , tokuisakiMST.RENRAKUSAKINOKBNN2    -- 連絡先番号区分２ 
                 , tokuisakiMST.RENRAKUSAKINON2    -- 連絡先番号２ 
                 , tokuisakiMST.RENRAKUSAKIMEMON2  -- 連絡先メモ２ 
                 , tokuisakiMST.RENRAKUSAKINOKBNN3 -- 連絡先番号区分３ 
                 , tokuisakiMST.RENRAKUSAKINON3    -- 連絡先番号３ 
                 , tokuisakiMST.RENRAKUSAKIMEMON3  -- 連絡先メモ３ 
                 , tokuisakiMST.RENRAKUSAKINOKBNN4 -- 連絡先番号区分４ 
                 , tokuisakiMST.RENRAKUSAKINON4    -- 連絡先番号４ 
                 , tokuisakiMST.RENRAKUSAKIMEMON4  -- 連絡先メモ４ 
                 , tokuisakiMST.MAILADRSE -- メールアドレス
                 , tokuisakiMST.SETAINUSHI -- 世帯主
                 , tokuisakiMST.KAZOKUKOSEIKAZUDANSEI -- 利用者数
                 , tokuisakiMST.KAZOKUKOSEIKAZUJOSEI     -- 内子供数
                 , tokuisakiMST.KOJUDYKAZUKBN -- 居住年数区分
                 , tokuisakiMST.PETJOHOKBN -- ペット情報区分
                 , tokuisakiMST.SETAISYUNYU -- 世帯収入
                 , tokuisakiMST.DOITSUSEKENINZUKBN -- 同一生計人数区分
                 , tokuisakiMST.GAIKOKUJINNINZU -- 外国人人数
                 , tokuisakiMST.JUGYOINKIBKBN -- 従業員規模区分
                 , tokuisakiMST.RIYOMOKUTEKIKBN -- 利用目的区分
                 , tokuisakiMST.SEIBETSUKBN -- 性別区分
                 , tokuisakiMST.NYUSHATANJODD -- 入会者誕生日
                 , tokuisakiMST.SYKUGYOKBN -- 職業区分
                 , tokuisakiMST.NYUSHADYDM -- 入社年月
                 , tokuisakiMST.SHIHASYAZOKUKBN -- 支払者続柄区分
                 , tokuisakiMST.HOSHOKINKBN -- 保証金区分
                 , tokuisakiMST.CDKINDKBN -- コード種別区分
                 , tokuisakiMST.GYOSHUKBN -- 業種区分
                 , tokuisakiMST.GYOSHUSHSKBN -- 業種詳細区分 
                 , tokuisakiMST.MOSHIKOMIRCPTDD -- 申込受付日 
                 , tokuisakiMST.SHOKAIWSVSTUPDD -- 初回サーバー設置日 
                 , tokuisakiMST.NYUKAIDD -- 入会日 
                 , ( CASE WHEN tokuisakiMST.FTTBNYUKAIFLG = '1' THEN tokuisakiMST.NYUKAIDD 
                          ELSE NULL 
                     END ) -- 再入会日 
                 , tokuisakiMST.TAIKAIDD -- 退会日 
                 , tokuisakiMST.TAIKAIRIYUKBN -- 退会理由区分 
                 , tokuisakiMST.RKNKINDKIHONFLG -- 料金種別基本フラグ（初回） 
                 , tokuisakiMST.RKNKINDTOKUBETSUUNTFLG -- 料金種別特別単価フラグ（初回） 
                 , tokuisakiMST.RKNKINDTOKUBETSUNEBIKIFLG -- 料金種別特別値引フラグ（初回） 
                 , tokuisakiMST.PLNCD -- プランコード（初回） 
                 , tokuisakiMST.PLNCD2 -- プランコード２（初回） 
                 , tokuisakiMST.PLNCD3 -- プランコード３（初回）                  
                 , tokuisakiMST.RKNKINDKIHONFLG -- 最新料金種別基本フラグ 
                 , tokuisakiMST.RKNKINDTOKUBETSUUNTFLG -- 最新料金種別特別単価フラグ
                 , tokuisakiMST.RKNKINDTOKUBETSUNEBIKIFLG -- 最新料金種別特別値引フラグ
                 , tokuisakiMST.PLNCD  -- 最新プランコード
                 , tokuisakiMST.PLNCD2 -- 最新プランコード２
                 , tokuisakiMST.PLNCD3 -- 最新プランコード３
                 , tokuisakiMST.KOJINSYOUMEIKINDKBN -- 個人証明種別区分 
                 , tokuisakiMST.KOJINSYOUMEINO      -- 個人証明番号 
                 , 'E' -- 継続ランク 
                 , 'D' -- 消費ランク 
                 , tokuisakiMST.RCPTKBN -- 受付区分（初回） 
                 , tokuisakiMST.KAKUTOKUKBN -- 獲得区分（初回） 
                 , tokuisakiMST.RCPTKBN -- 最新受付区分
                 , tokuisakiMST.KAKUTOKUKBN -- 最新獲得区分
                 , tokuisakiMST.TOKUISAKISHOSHAKAZU -- 得意先紹介者数 
                 , tokuisakiMST.APKIHONCD -- 請求先ＡＰ基本コード 
                 , tokuisakiMST.SHOPCD    -- 請求先ショップコード 
                 , tokuisakiMST.SKYSAKICD -- 請求先コード 
                 , NULL -- 前回請求先ＡＰ基本コード 
                 , NULL -- 前回請求先ショップコード 
                 , NULL -- 前回請求先コード 
                 , tokuisakiMST.APKIHONCD   -- 得意先ＡＰ基本コード
                 , tokuisakiMST.SHOPCD      -- 得意先ショップコード
                 , tokuisakiMST.TOKUISAKICD -- 得意先コード
                 , NULL -- 前回得意先ＡＰ基本コード
                 , NULL -- 前回得意先ショップコード
                 , NULL -- 前回得意先コード
                 , 0 -- １２Ｌボトル数量
                 , 0 -- ８Ｌボトル数量
                 , 0 -- ７Ｌボトル数量
                 , 0 -- バナジウム８Ｌボトル
                 , 0 -- その他製品水数量
                 , 0 -- 予備１ボトル
                 , 0 -- 予備２ボトル
                 , 0 -- 予備３ボトル
                 , 0 -- 製品水売上高
                 , 0 -- 定例売上高
                 , 0 -- その他売上高
                 , 0 -- 合計売上高
                 /* テーブル共通項目 */ 
                 , '1' -- 有効区分
                 , userinfo_rec.APKIHONCD -- 登録者ＡＰ基本コード
                 , userinfo_rec.SHOPCD    -- 登録者ショップコード
                 , userinfo_rec.TNSHACD        -- 登録者コード
                 , userinfo_rec.SYSTMYMD   -- 登録システム日付
                 , userinfo_rec.SYSTMJIKAN -- 登録システム時間
                 , userinfo_rec.UNYOYMD    -- 登録運用日付
                 , userinfo_rec.IPADRSE    -- 登録ＩＰアドレス
                 , userinfo_rec.PGMID      -- 登録プログラムＩＤ
                 , userinfo_rec.APKIHONCD -- 更新者ＡＰ基本コード
                 , userinfo_rec.SHOPCD    -- 更新者ショップコード
                 , userinfo_rec.TNSHACD        -- 更新者コード
                 , userinfo_rec.SYSTMYMD   -- 更新システム日付
                 , userinfo_rec.SYSTMJIKAN -- 更新システム時間
                 , userinfo_rec.UNYOYMD    -- 更新運用日付
                 , userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
                 , userinfo_rec.PGMID      -- 更新プログラムＩＤ
          );
       END IF; 
    END IF; 
  END processKaiinNyukai; 
  
  /*****************************************************************************/
  /* プロシージャ名：配達済得意先一連処理                                           */
  /* 引数             ：itaku_rec      委託データ                                */
  /*****************************************************************************/
  PROCEDURE processTokuisakiDlvry( itaku_rec     IN itaku_rec_type  -- 委託データ
  ) IS 
  /* 変数宣言 */ 
  -- 更新用データ 
  shopMST            S02M046T%rowtype := NULL; -- ショップマスタ
  tokuisakiMST       S02M013T%rowtype := NULL; -- 得意先マスタ 
  delivered_rec      S04T001T%rowtype := NULL; -- 更新後受注配達データ 
  -- 更新パタン 
  isDlvryInfoChanged VARCHAR2(1) := '0'; -- 配達情報変更フラグ 
  isDlvryjunChanged  VARCHAR2(1) := '0'; -- 配達順変更フラグ
  isNyukai           VARCHAR2(1) := '0'; -- 入会フラグ 
  isShokaisvstup     VARCHAR2(1) := '0'; -- 初回サーバー設置フラグ 
  isSvhikiage        VARCHAR2(1) := '0'; -- サーバー引上フラグ 
  isIyakukin         VARCHAR2(1) := '0'; -- 違約金更新フラグ 
  nGenzaizd  NUMBER := 0; -- 現在残高
  nServermei NUMBER := 0; -- サーバー明細行数 
  -- 違約金更新用変数 
  nIyakukin  NUMBER := 0; -- 違約金  
  saiban     NUMBER := 0; -- 違約金管理番号採番 
  nIyakukinkikan NUMBER := 0; -- 違約金適用期間 
  nIyakukinkin   NUMBER := 0; -- 違約金金額 
  -- 会員一連更新用変数 
  doKaiin    VARCHAR2(1)  := '0';  -- 会員作成有無 
  cdKaiin    VARCHAR2(10) := NULL; -- 会員コード（自動採番） 
-- インボイス対応追加 start ABI
  wInvoicshikoymdflg  VARCHAR2(1)      := '0';   -- インボイス施行日フラグ（0:インボイス施行前、1:インボイス施行後）
-- インボイス対応追加 end ABI
  BEGIN 
    -- 得意先マスタ取得 
    SELECT TKSM.* 
      INTO tokuisakiMST 
      FROM S02M013T TKSM 
     WHERE TKSM.APKIHONCD   = itaku_rec.APKIHONCD 
       AND TKSM.SHOPCD      = itaku_rec.SHOPCD 
       AND TKSM.TOKUISAKICD = itaku_rec.TOKUISAKICD 
       AND TKSM.YUKOKBN = '1' 
    ;
    -- ショップマスタ取得 
    SELECT SHPM.* 
      INTO shopMST 
      FROM S02M046T SHPM 
     WHERE SHPM.APKIHONCD   = itaku_rec.APKIHONCD 
       AND SHPM.SHOPCD      = itaku_rec.SHOPCD 
       AND SHPM.YUKOKBN = '1' 
    ;
    
    -- 更新後受注配達データ取得 
    SELECT JHTT.* 
      INTO delivered_rec 
      FROM S04T001T JHTT 
     WHERE JHTT.APKIHONCD  = itaku_rec.APKIHONCD 
       AND JHTT.SHOPCD     = itaku_rec.SHOPCD 
       AND JHTT.ACPTDNPYNO = itaku_rec.ACPTDNPYNO 
       AND JHTT.EDANO      = itaku_rec.EDANO 
       AND JHTT.YUKOKBN = '1' 
    ; 
    -- 状態１：配達情報変更の場合
    IF shopMST.DLVRYTNSHAJISSEKIKSHNKBN IN ('1', '2') -- 配達担当者実績更新区分：1担当者／2担当者・配達順 
       AND delivered_rec.DLVRYKBN <> '3'            -- 持ち帰り以外 
       THEN 
       IF ( delivered_rec.DLVRYKBN IS NOT NULL AND tokuisakiMST.DLVRYKBN <> delivered_rec.DLVRYKBN ) -- 配達区分変更
          OR ( delivered_rec.DLVRYCMPNYCD IS NOT NULL AND tokuisakiMST.DLVRYCMPNYCD <> delivered_rec.DLVRYCMPNYCD ) -- 配達会社コード変更
          OR ( delivered_rec.DLVRYTNSHACD IS NOT NULL AND tokuisakiMST.DLVRYTNSHACD <> delivered_rec.DLVRYTNSHACD ) -- 配達担当者コード変更
          OR ( delivered_rec.DLVRYKBN IS NOT NULL AND tokuisakiMST.DLVRYKBN IS NULL ) 
          OR ( delivered_rec.DLVRYCMPNYCD IS NOT NULL AND tokuisakiMST.DLVRYCMPNYCD IS NULL ) 
          OR ( delivered_rec.DLVRYTNSHACD IS NOT NULL AND tokuisakiMST.DLVRYTNSHACD IS NULL) 
          THEN 
          isDlvryInfoChanged := '1'; 
       END IF; 
    END IF; 
    
    IF shopMST.DLVRYTNSHAJISSEKIKSHNKBN = '2' THEN -- 実績担当者更新区分：担当者・配達順 
       IF ( delivered_rec.DLVRYJUN IS NOT NULL AND tokuisakiMST.DLVRYJUN <> delivered_rec.DLVRYJUN ) -- 配達順変更 
          OR ( delivered_rec.DLVRYJUN IS NOT NULL AND tokuisakiMST.DLVRYJUN IS NULL ) 
          THEN 
          isDlvryjunChanged := '1'; 
       END IF; 
    END IF; 
    
    -- 状態２：入会の場合 
    IF tokuisakiMST.STARTUP = '1' AND tokuisakiMST.TOKUISAKIJOTAI = '1' 
       AND delivered_rec.ACPTKBN = '2' AND delivered_rec.ACPTKBNSHS = '22' 
       THEN 
       isNyukai   := '1'; 
    END IF; 
    
    -- 状態３：初回サーバー設置
    IF tokuisakiMST.SHOKAIWSVSTUPDD IS NULL THEN 
       -- サーバー明細存在チェック 
       SELECT COUNT(JHMT.ACPTDNPYMEISAINO) 
         INTO nServermei 
         FROM S04T002T JHMT 
        INNER JOIN S02M006T SYSM_L 
                ON ( SYSM_L.APKIHONCD = '000' AND SYSM_L.SHOPCD = '000' 
                     AND SYSM_L.SETTEINO = '096' AND SYSM_L.RCRDKBN = 'M' 
                     AND SYSM_L.SETTEICD = '4' AND SYSM_L.YUKOKBN = '1' 
                     AND SYSM_L.MOJISETTEIATAI = JHMT.LCTGRYCD 
                   ) 
        INNER JOIN S02M006T SYSM_M 
                ON ( SYSM_M.APKIHONCD = '000' AND SYSM_M.SHOPCD = '000' 
                     AND SYSM_M.SETTEINO = '134' AND SYSM_M.RCRDKBN = 'M' 
                     AND SYSM_M.SETTEICD = '41' AND SYSM_M.YUKOKBN = '1' 
                     AND SYSM_M.MOJISETTEIATAI = JHMT.MCTGRYCD 
                   ) 
        WHERE JHMT.APKIHONCD  = delivered_rec.APKIHONCD 
          AND JHMT.SHOPCD     = delivered_rec.SHOPCD 
          AND JHMT.ACPTDNPYNO = delivered_rec.ACPTDNPYNO 
          AND JHMT.EDANO = delivered_rec.EDANO 
          AND JHMT.YUKOKBN = '1' 
       ; 
       IF nServermei > 0 THEN 
          isShokaisvstup := '1'; 
       END IF; 
    END IF; 
    
    -- 状態４：サーバー引上
    IF delivered_rec.ACPTKBN = '4' 
       AND delivered_rec.ACPTKBNSHS IN ('41', '43') 
       AND tokuisakiMST.TAIKAIDD IS NOT NULL 
       THEN 
       isSvhikiage := '1'; 
    END IF; 
    
    -- 状態５：会員作成有 
    BEGIN 
      -- 存在する場合：会員一連処理を行う 
      SELECT '1' 
        INTO doKaiin 
        FROM S02M006T SYSM 
       WHERE SYSM.APKIHONCD = '000' 
         AND SYSM.SHOPCD    = '000'
         AND SYSM.SETTEINO  = '225' 
         AND SYSM.RCRDKBN   = 'M' 
         AND SYSM.YUKOKBN = '1' 
      ; 
      
      -- 得意先が顧客の場合のみ会員処理を行う
      IF tokuisakiMST.TOKUISAKIKBN = '1' THEN 
         -- 入会の場合：自動採番 
         IF isNyukai = '1' AND tokuisakiMST.KAIINCD IS NULL THEN 
            -- 会員の採番
-- 本線対応変更 start ABI
--            S01SAIBAN.getNewNo( SAIBAN_KAIINCD -- 採番種別番号：会員コード 
            Z1804B01.getNewNo( SAIBAN_KAIINCD -- 採番種別番号：会員コード 
-- 本線対応変更 end ABI
                           , 1    -- チェックデジット付与フラグ
                           , 1    -- 採番回数
                           , NULL    -- AP基本コード
                           , NULL -- ショップコード
                           , NULL -- 得意先コード
                           , cdKaiin -- 採番開始値
                           , cdKaiin -- 採番終了値
            );
         END IF; 
      ELSE 
        doKaiin := '0';
      END IF;
      
      -- 会員作成無しの場合：会員一連処理しない 
      EXCEPTION 
        WHEN OTHERS THEN 
          doKaiin := '0'; 
    END; 
-- インボイス対応追加 start ABI
    -- インボイス施行状況区分の設定
    IF shopMST.INVOICSHIKOYMD <= delivered_rec.DLVRYDD THEN
      wInvoicshikoymdflg := '1';
    END IF;
-- インボイス対応追加 end ABI
    -- 得意先マスタ更新 
    IF isDlvryInfoChanged = '1' OR isDlvryjunChanged = '1' OR isNyukai = '1' OR isShokaisvstup = '1' OR isSvhikiage = '1' THEN 
       UPDATE S02M013T TKSM
          SET TKSM.TOKUISAKIJOTAI    = ( CASE WHEN isNyukai = '1' THEN '5' 
                                             WHEN isShokaisvstup = '1' THEN '3'
                                             -- ACCIS2023-7271 本会員（引上処理中）で引上受注を実績登録する場合、得意先状態を退会に変更
                                             -- ※状態ステータスに関しては別のUPDATEにより引上時の考慮が行われているため、ここは修正不要
                                             WHEN isSvhikiage = '1' AND TKSM.TOKUISAKIJOTAI = '5' THEN '6'
                                            ELSE TKSM.TOKUISAKIJOTAI 
                                      END ) -- 得意先状態：本会員 ※２の場合 
            , TKSM.JOTAISTS       = ( CASE WHEN isNyukai = '1' THEN '07' 
                                           WHEN isShokaisvstup = '1' THEN '03'
                                           ELSE TKSM.JOTAISTS 
                                      END ) -- 状態ステータス：継続中 ※２の場合 
            , TKSM.NYUKAIDD       = ( CASE WHEN isNyukai = '1' THEN delivered_rec.DLVRYDD 
                                           ELSE TKSM.NYUKAIDD 
                                      END ) -- 入会日 ※２の場合、値を設定する
            , TKSM.SHOKAIWSVSTUPDD      = ( CASE WHEN isShokaisvstup = '1' THEN delivered_rec.DLVRYDD 
                                                 ELSE TKSM.SHOKAIWSVSTUPDD 
                                            END ) -- 初回サーバー設置日 ※３の場合、値を設定する 
            , TKSM.STUPDDTOSHAAPKIHONCD = ( CASE WHEN isShokaisvstup = '1' THEN userinfo_rec.APKIHONCD 
                                                 ELSE TKSM.STUPDDTOSHAAPKIHONCD 
                                            END ) -- 設置日登録者ＡＰ基本コード ※３の場合、ログイン情報. ログイン担当者コード
            , TKSM.STUPDDTOSHASHOPCD    = ( CASE WHEN isShokaisvstup = '1' THEN userinfo_rec.SHOPCD 
                                                 ELSE TKSM.STUPDDTOSHASHOPCD 
                                            END ) -- 設置日登録者ショップコード ※３の場合、ログイン情報. ログイン担当者ＡＰ基本コード    設置日登録者ショップコード
            , TKSM.STUPDDTOSHACD        = ( CASE WHEN isShokaisvstup = '1' THEN userinfo_rec.TNSHACD 
                                                 ELSE TKSM.STUPDDTOSHACD 
                                            END ) -- 設置日登録者コード ※３の場合、ログイン情報. ログイン担当者ショップコード
            , TKSM.WSVHIKIAGEDD            = ( CASE WHEN isSvhikiage = '1' THEN delivered_rec.DLVRYDD 
                                                    ELSE TKSM.WSVHIKIAGEDD 
                                               END ) -- サーバー引上日 ※４の場合、値を設定する
            , TKSM.HIKIAGEDDTOSHAAPKIHONCD = ( CASE WHEN isSvhikiage = '1' THEN userinfo_rec.APKIHONCD 
                                                    ELSE TKSM.HIKIAGEDDTOSHAAPKIHONCD 
                                               END ) -- 引上日登録者ＡＰ基本コード ※４の場合、ログイン情報. ログイン担当者コード
            , TKSM.HIKIAGEDDTOSHASHOPCD    = ( CASE WHEN isSvhikiage = '1' THEN userinfo_rec.SHOPCD 
                                                    ELSE TKSM.HIKIAGEDDTOSHASHOPCD 
                                               END ) -- 引上日登録者ショップコード ※４の場合、ログイン情報. ログイン担当者ＡＰ基本コード
            , TKSM.HIKIAGEDDTOSHACD        = ( CASE WHEN isSvhikiage = '1' THEN userinfo_rec.TNSHACD 
                                                    ELSE TKSM.HIKIAGEDDTOSHACD 
                                               END ) -- 引上日登録者コード ※４の場合、ログイン情報. ログイン担当者ショップコード
            , TKSM.DLVRYCMPNYCD = ( CASE WHEN isDlvryInfoChanged = '1' THEN delivered_rec.DLVRYCMPNYCD 
                                         ELSE TKSM.DLVRYCMPNYCD 
                                    END ) -- 配達会社コード
            , TKSM.DLVRYTNSHACD = ( CASE WHEN isDlvryInfoChanged = '1' THEN delivered_rec.DLVRYTNSHACD 
                                         ELSE TKSM.DLVRYTNSHACD
                                    END ) -- 配達担当者コード
            , TKSM.DLVRYJUN     = ( CASE WHEN isDlvryjunChanged = '1' THEN delivered_rec.DLVRYJUN
                                         ELSE TKSM.DLVRYJUN 
                                    END ) -- 配達順
            , TKSM.DLVRYKBN     = ( CASE WHEN isDlvryInfoChanged = '1' THEN delivered_rec.DLVRYKBN 
                                         ELSE TKSM.DLVRYKBN 
                                    END ) -- 配達区分
            , TKSM.KAIINCD      = ( CASE WHEN doKaiin = '1' AND TKSM.KAIINCD IS NULL THEN cdKaiin 
                                         ELSE TKSM.KAIINCD 
                                    END ) -- 会員コード 
            /* 更新者 */
            , TKSM.KOSHAAPKIHONCD  = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
            , TKSM.KOSHASHOPCD     = userinfo_rec.SHOPCD     -- 更新者ショップコード
            , TKSM.KOSHACD         = userinfo_rec.TNSHACD    -- 更新者コード
            , TKSM.KSHNSYSTMYMD    = userinfo_rec.SYSTMYMD   -- 更新システム日付
            , TKSM.KSHNSYSTMJIKAN  = userinfo_rec.SYSTMJIKAN -- 更新システム時間
            , TKSM.KSHNUNYOYMD     = userinfo_rec.UNYOYMD    -- 更新運用日付
            , TKSM.KSHNIPADRSE     = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
            , TKSM.KSHNPGMID       = userinfo_rec.PGMID      -- 更新プログラムＩＤ
        WHERE TKSM.APKIHONCD   = itaku_rec.APKIHONCD 
          AND TKSM.SHOPCD      = itaku_rec.SHOPCD 
          AND TKSM.TOKUISAKICD = itaku_rec.TOKUISAKICD 
          AND TKSM.YUKOKBN = '1'
       ;
    END IF; 
    
    -- 得意先実績テーブル 
    UPDATE S02M050T TKJT 
-- インボイス対応変更 start ABI
--       SET TKJT.GENZAIZD = ( NVL(TKJT.GENZAIZD, 0) + delivered_rec.NOHINKIN + delivered_rec.NOHINSHOHITAXKIN ) -- 現在残高 
--         , TKJT.KEIJOMAEACPTKIN = ( NVL(TKJT.KEIJOMAEACPTKIN, 0) - delivered_rec.ACPTKIN - delivered_rec.ACPTSHOHITAXKIN ) -- 計上前受注金額 
       SET TKJT.GENZAIZD = ( CASE WHEN delivered_rec.ACPTTHDAIBIKIFLG = '1' OR delivered_rec.SINGLDNPYFLG = '1' THEN -- 代引きの場合
                                 NVL(TKJT.GENZAIZD, 0) + delivered_rec.NOHINKIN + delivered_rec.NOHINSHOHITAXKIN
                             ELSE
                                 CASE WHEN wInvoicshikoymdflg = '0' THEN -- インボイス施行日前の場合
                                     NVL(TKJT.GENZAIZD, 0) + delivered_rec.NOHINKIN + delivered_rec.NOHINSHOHITAXKIN
                                 ELSE
                                     NVL(TKJT.GENZAIZD, 0) + delivered_rec.NOHINKIN
                                 END
                             END) -- 現在残高（代引き（単体伝票含む）以外の場合は消費税金額を加算しない）
         , TKJT.KEIJOMAEACPTKIN = ( CASE WHEN delivered_rec.ACPTTHDAIBIKIFLG = '1' OR delivered_rec.SINGLDNPYFLG = '1' THEN -- 代引きの場合
                                        NVL(TKJT.KEIJOMAEACPTKIN, 0) - delivered_rec.ACPTKIN - delivered_rec.ACPTSHOHITAXKIN
                                    ELSE
                                        CASE WHEN wInvoicshikoymdflg = '0' THEN -- インボイス施行日前の場合
                                            NVL(TKJT.KEIJOMAEACPTKIN, 0) - delivered_rec.ACPTKIN - delivered_rec.ACPTSHOHITAXKIN
                                        ELSE
                                            NVL(TKJT.KEIJOMAEACPTKIN, 0) - delivered_rec.ACPTKIN
                                        END
                                    END) -- 計上前受注金額 
-- インボイス対応変更 end ABI
         , TKJT.NYKGSHOKAIDLVRYDNPYNO = ( CASE WHEN TKJT.NYKGSHOKAIDLVRYDNPYNO IS NULL 
                                                    AND ( tokuisakiMST.NYUKAIDD IS NOT NULL OR isNyukai = '1' ) 
                                               THEN delivered_rec.ACPTDNPYNO 
                                               ELSE TKJT.NYKGSHOKAIDLVRYDNPYNO 
                                          END )-- 入会後初回配達伝票番号 
         , TKJT.NKGSKDLVRYDNPYEDANO = ( CASE WHEN TKJT.NKGSKDLVRYDNPYEDANO IS NULL 
                                                  AND ( tokuisakiMST.NYUKAIDD IS NOT NULL OR isNyukai = '1' ) 
                                             THEN delivered_rec.EDANO 
                                             ELSE TKJT.NKGSKDLVRYDNPYEDANO 
                                        END ) -- 入会後初回配達伝票枝番号
         , TKJT.NYKGSHOKAIDLVRYDD  = ( CASE WHEN TKJT.NYKGSHOKAIDLVRYDD IS NULL 
                                                 AND ( tokuisakiMST.NYUKAIDD IS NOT NULL OR isNyukai = '1' ) 
                                            THEN delivered_rec.DLVRYDD 
                                            ELSE TKJT.NYKGSHOKAIDLVRYDD 
                                       END ) -- 入会後初回配達日 
         -- 最終受注日：新規配達受注の場合更新 
         , TKJT.SAISHUACPTDD = ( CASE WHEN delivered_rec.ACPTDD > TKJT.SAISHUACPTDD THEN delivered_rec.ACPTDD 
                                      ELSE TKJT.SAISHUACPTDD 
                                 END ) 
         /* 前回配達情報 */
         , TKJT.ZNKDLVRYDNPYNO = delivered_rec.ACPTDNPYNO -- 前回配達伝票番号 
         , TKJT.ZNKDLVRYDNPYEDANO = delivered_rec.EDANO -- 前回配達伝票枝番号
         , TKJT.ZNKDLVRYACPTDD = delivered_rec.ACPTDD -- 前回配達受注日
         , TKJT.ZNKDLVRYDD = delivered_rec.DLVRYJISSEKIDD -- 前回配達日
         , TKJT.ZNKDLVRYRGRAYUSYOHNS = delivered_rec.DLVRYJISSEKIRGRAYUSYOHNS -- 前回配達レギュラー有償本数
         , TKJT.ZNKDLVRYRGRAMUSHOHNS = delivered_rec.DLVRYJISSEKIRGRAMUSHOHNS -- 前回配達レギュラー無償本数
         , TKJT.ZNKDLVRYSRMYUSYOHNS = delivered_rec.DLVRYJISSEKISRMYUSYOHNS -- 前回配達スリム有償本数
         , TKJT.ZNKDLVRYSRMMUSHOHNS = delivered_rec.DLVRYJISSEKISRMMUSHOHNS -- 前回配達スリム無償本数
         , TKJT.ZNKDLVRYSONOTAYUSYOHNS = delivered_rec.DLVRYJISSEKISTYSHNS -- 前回配達その他有償本数
         , TKJT.ZNKDLVRYSONOTAMUSHOHNS = delivered_rec.DLVRYJISSEKISTMSHNS -- 前回配達その他無償本数
         , TKJT.ZNKDLVRYSEIHINMIZUURIKIN = delivered_rec.DLVRYJISSEKISHSUAKIN -- 前回配達製品水売上金額
         , TKJT.ZNKDLVRYSONOTAURIKIN = delivered_rec.DLVRYJISSEKISONOTAURIKIN -- 前回配達その他売上金額
         /* 前々回配達情報 */
         , TKJT.ZZKHTDNPYNO = TKJT.ZNKDLVRYDNPYNO -- 前々回配達伝票番号
         , TKJT.ZZKAIDLVRYDNPYEDANO = TKJT.ZNKDLVRYDNPYEDANO -- 前々回配達伝票枝番号
         , TKJT.ZZKAIDLVRYACPTDD = TKJT.ZNKDLVRYACPTDD -- 前々回配達受注日
         , TKJT.ZZKHTDD = TKJT.ZNKDLVRYDD -- 前々回配達日
         , TKJT.ZZKHTRGRAYUSYOHNS = TKJT.ZNKDLVRYRGRAYUSYOHNS -- 前々回配達レギュラー有償本数
         , TKJT.ZZKHTRGRAMUSHOHNS    = TKJT.ZNKDLVRYRGRAMUSHOHNS -- 前々回配達レギュラー無償本数
         , TKJT.ZZKHTSRMYUSYOHNS = TKJT.ZNKDLVRYSRMYUSYOHNS -- 前々回配達スリム有償本数
         , TKJT.ZZKHTSRMMUSHOHNS = TKJT.ZNKDLVRYSRMMUSHOHNS -- 前々回配達スリム無償本数
         , TKJT.ZZKHTSONOTAYUSYOHNS = TKJT.ZNKDLVRYSONOTAYUSYOHNS -- 前々回配達その他有償本数
         , TKJT.ZZKHTSONOTAMUSHOHNS = TKJT.ZNKDLVRYSONOTAMUSHOHNS -- 前々回配達その他無償本数
         , TKJT.ZZKHTSEIHINMIZUURIKIN = TKJT.ZNKDLVRYSEIHINMIZUURIKIN -- 前々回配達製品水売上金額
         , TKJT.ZZKHTSONOTAURIKIN = TKJT.ZNKDLVRYSONOTAURIKIN -- 前々回配達その他売上金額
         /* 前々前回配達情報 */
         , TKJT.ZZZKHTDNPYNO = TKJT.ZZKHTDNPYNO -- 前々前回配達伝票番号
         , TKJT.ZZZKHTDNPYEDANO = TKJT.ZZKAIDLVRYDNPYEDANO -- 前々前回配達伝票枝番号
         , TKJT.ZZZKHTACPTDD = TKJT.ZZKAIDLVRYACPTDD -- 前々前回配達受注日
         , TKJT.ZZZKHTDD = TKJT.ZZKHTDD -- 前々前回配達日
         , TKJT.ZZZKHTRGRAYUSYOHNS = TKJT.ZZKHTRGRAYUSYOHNS -- 前々前回配達レギュラー有償本数
         , TKJT.ZZZKHTRGRAMUSHOHNS = TKJT.ZZKHTRGRAMUSHOHNS -- 前々前回配達レギュラー無償本数
         , TKJT.ZZZKHTSRMYUSYOHNS = TKJT.ZZKHTSRMYUSYOHNS -- 前々前回配達スリム有償本数
         , TKJT.ZZZKHTSRMMUSHOHNS = TKJT.ZZKHTSRMMUSHOHNS -- 前々前回配達スリム無償本数
         , TKJT.ZZZKHTSONOTAYUSYOHNS = TKJT.ZZKHTSONOTAYUSYOHNS -- 前々前回配達その他有償本数
         , TKJT.ZZZKHTSONOTAMUSHOHNS = TKJT.ZZKHTSONOTAMUSHOHNS -- 前々前回配達その他無償本数
         , TKJT.ZZZKHTSEIHINMIZUURIKIN = TKJT.ZZKHTSEIHINMIZUURIKIN -- 前々前回配達製品水売上金額
         , TKJT.ZZZKHTSONOTAURIKIN = TKJT.ZZKHTSONOTAURIKIN -- 前々前回配達その他売上金額
         , TKJT.AZUKARIBTLN1N2RITTORUHNS = ( CASE WHEN ( NVL(TKJT.AZUKARIBTLN1N2RITTORUHNS, 0) + NVL(sum_rec.RGRASURYO, 0) - NVL(delivered_rec.KZBTLN1N2RITTORUHNS, 0) ) < 0 THEN 0
                                                  ELSE NVL(TKJT.AZUKARIBTLN1N2RITTORUHNS, 0) + NVL(sum_rec.RGRASURYO, 0) - NVL(delivered_rec.KZBTLN1N2RITTORUHNS, 0)
                                             END ) -- 預かりボトル１２Ｌ本数
         , TKJT.AZUKARIBTLN8RITTORUHNS = ( CASE WHEN ( NVL(TKJT.AZUKARIBTLN8RITTORUHNS, 0) + NVL(sum_rec.SRMSURYO, 0) - NVL(delivered_rec.KZBTLN8RITTORUHNS, 0) ) < 0 THEN 0 
                                                ELSE NVL(TKJT.AZUKARIBTLN8RITTORUHNS, 0) + NVL(sum_rec.SRMSURYO, 0) - NVL(delivered_rec.KZBTLN8RITTORUHNS, 0)
                                           END ) -- 預かりボトル８Ｌ本数 
         /* 更新者：更新なし */
     WHERE TKJT.APKIHONCD   = itaku_rec.APKIHONCD 
       AND TKJT.SHOPCD      = itaku_rec.SHOPCD 
       AND TKJT.TOKUISAKICD = itaku_rec.TOKUISAKICD 
       AND TKJT.YUKOKBN = '1'
    ; 
    
    -- 現在残高より退会後のステータス更新
    IF isSvhikiage = '1' THEN 
       -- 現在残高取得
       SELECT TKJT.GENZAIZD 
         INTO nGenzaizd
         FROM S02M050T TKJT 
        WHERE TKJT.APKIHONCD   = itaku_rec.APKIHONCD 
          AND TKJT.SHOPCD      = itaku_rec.SHOPCD 
          AND TKJT.TOKUISAKICD = itaku_rec.TOKUISAKICD 
          AND TKJT.YUKOKBN = '1'
       ; 
       
       -- 更新
       UPDATE S02M013T TKSM
          SET TKSM.JOTAISTS       = ( CASE WHEN NVL(nGenzaizd, 0) <> 0 
                                                THEN ( CASE WHEN TKSM.TOKUISAKIJOTAI = '4' THEN '05' -- 仮会員退会(未払い有)
                                                            WHEN TKSM.TOKUISAKIJOTAI = '6' THEN '09' -- 退会（未払い有）
                                                            ELSE TKSM.JOTAISTS 
                                                       END )
                                                ELSE ( CASE WHEN TKSM.TOKUISAKIJOTAI = '4' THEN '06' -- 仮会員退会
                                                            WHEN TKSM.TOKUISAKIJOTAI = '6' THEN '10' -- 退会
                                                            ELSE TKSM.JOTAISTS 
                                                       END )
                                      END ) -- 状態ステータス：継続中 ※４の場合 
        WHERE TKSM.APKIHONCD   = itaku_rec.APKIHONCD 
          AND TKSM.SHOPCD      = itaku_rec.SHOPCD 
          AND TKSM.TOKUISAKICD = itaku_rec.TOKUISAKICD 
          AND TKSM.YUKOKBN = '1'
       ;
    END IF;
    
    -- 得意先違約金管理テーブル 
    IF tokuisakiMST.STARTUP = '1' AND tokuisakiMST.TOKUISAKIJOTAI = '1' 
       AND delivered_rec.ACPTKBN = '2' AND delivered_rec.ACPTKBNSHS = '22' 
       THEN 
       isIyakukin := '1'; 
    END IF; 
    IF tokuisakiMST.IYAKUKINKANRIFLG = '1' 
       AND isIyakukin = '1' 
       THEN 
       -- 違約金管理テーブルにデータ存在チェック 
       SELECT COUNT(IYKT.APKIHONCD) 
         INTO nIyakukin 
         FROM S03T003T IYKT 
        WHERE IYKT.APKIHONCD = tokuisakiMST.APKIHONCD 
          AND IYKT.SHOPCD = tokuisakiMST.SHOPCD 
          AND IYKT.TOKUISAKICD = tokuisakiMST.TOKUISAKICD 
          AND IYKT.RKNPLNCD = '01' 
          AND IYKT.YUKOKBN = '1' 
       ; 
       IF nIyakukin = 0 THEN 
          saiban := 0; 
          -- 違約金管理テーブルにデータ存在チェック 
          BEGIN 
            SELECT MAX(IYKT.IYAKUKINKANRINO) 
              INTO saiban 
              FROM S03T003T IYKT 
             WHERE IYKT.APKIHONCD = tokuisakiMST.APKIHONCD 
               AND IYKT.SHOPCD    = tokuisakiMST.SHOPCD 
               AND IYKT.TOKUISAKICD = tokuisakiMST.TOKUISAKICD 
            ; 
            EXCEPTION 
              WHEN NO_DATA_FOUND THEN 
                saiban := 0; 
          END; 
          nIyakukinkikan := getSuchisetteiataiHasOya('000', '000', '155', NULL, '153', '01', '違約金適用期間'); -- 違約金適用期間 
          nIyakukinkin   := getSuchisetteiataiHasOya('000', '000', '156', NULL, '153', '01', '違約金金額'); -- 違約金金額 
          INSERT INTO S03T003T ( 
              APKIHONCD -- ＡＰ基本コード
            , SHOPCD -- ショップコード
            , TOKUISAKICD -- 得意先コード
            , IYAKUKINKANRINO -- 違約金管理番号
            , IYAKUKINKIND -- 違約金種別
            , RKNPLNCD -- 料金プランコード
            , WSVKISHUCD -- サーバー機種コード
            , IYAKUKINTEKIYOSTRTDYDM -- 違約金適用開始年月
            , IYAKUKINTEKIYOSHRYDYDM -- 違約金適用終了年月
            , IYAKUKINTEKIYODKIKAN -- 違約金適用期間
            , IYAKUKINKIN -- 違約金金額
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
              tokuisakiMST.APKIHONCD -- ＡＰ基本コード
            , tokuisakiMST.SHOPCD -- ショップコード
            , tokuisakiMST.TOKUISAKICD -- 得意先コード
            , NVL(saiban,0) + 1 -- 違約金管理番号
            , '0'  -- 違約金種別：料金プランコード
            , '01' -- 料金プランコード：基本プラン
            , NULL -- サーバー機種コード
            -- 違約金適用開始年月 
            , SUBSTRB(( CASE WHEN isNyukai = '1' THEN delivered_rec.DLVRYDD 
                             ELSE tokuisakiMST.NYUKAIDD 
                        END ), 1, 6) 
            , TO_CHAR(ADD_MONTHS( TO_DATE(SUBSTRB(( CASE WHEN isNyukai = '1' THEN  delivered_rec.DLVRYDD 
                                            ELSE tokuisakiMST.NYUKAIDD 
                                            END ), 1, 6),'YYYYMM')
                                , TO_NUMBER(nIyakukinkikan) - 1),'YYYYMM') -- 違約金適用終了年月
            , nIyakukinkikan -- 違約金適用期間 
            , nIyakukinkin   -- 違約金金額 
            /* テーブル共通項目 */
            , '1' -- 有効区分
            , userinfo_rec.APKIHONCD  -- 登録者ＡＰ基本コード
            , userinfo_rec.SHOPCD     -- 登録者ショップコード
            , userinfo_rec.TNSHACD    -- 登録者コード
            , userinfo_rec.SYSTMYMD   -- 登録システム日付
            , userinfo_rec.SYSTMJIKAN -- 登録システム時間
            , userinfo_rec.UNYOYMD    -- 登録運用日付
            , userinfo_rec.IPADRSE    -- 登録ＩＰアドレス
            , userinfo_rec.PGMID      -- 登録プログラムＩＤ
            , userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
            , userinfo_rec.SHOPCD     -- 更新者ショップコード
            , userinfo_rec.TNSHACD    -- 更新者コード
            , userinfo_rec.SYSTMYMD   -- 更新システム日付
            , userinfo_rec.SYSTMJIKAN -- 更新システム時間
            , userinfo_rec.UNYOYMD    -- 更新運用日付
            , userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
            , userinfo_rec.PGMID      -- 更新プログラムＩＤ
          );
       END IF; 
    END IF; 
    
    -- 会員一連処理：会員作成有の場合のみ 
    IF doKaiin = '1' THEN 
       -- 入会 
       IF isNyukai = '1' THEN 
          processKaiinNyukai(tokuisakiMST.APKIHONCD, tokuisakiMST.SHOPCD, tokuisakiMST.TOKUISAKICD); 
       END IF; 
       -- 会員履歴作成 
       IF isDlvryInfoChanged = '1' OR isNyukai = '1' OR isShokaisvstup = '1' OR isSvhikiage = '1' THEN 
-- 本線対応変更 start ABI
--          S0168Q00.createKaiinRireki( tokuisakiMST.APKIHONCD   -- ＡＰ基本コード
          Z0509B01.createKaiinRireki( tokuisakiMST.APKIHONCD   -- ＡＰ基本コード
-- 本線対応変更 end ABI
                                    , tokuisakiMST.SHOPCD      -- ショップコード
                                    , tokuisakiMST.TOKUISAKICD -- 得意先コード
                                    , NULL -- 変更前紹介得意先コード
                                    , tokuisakiMST.SKYSAKICD   -- 請求先コード
                                    , userinfo_rec.APKIHONCD -- 担当者ＡＰ基本コード
                                    , userinfo_rec.SHOPCD    -- 担当者ショップコード
                                    , userinfo_rec.TNSHACD        -- 担当者コード
                                    , userinfo_rec.IPADRSE    -- ＩＰアドレス 
                                    , 'S0512W00' -- プログラムＩＤ：配達実績登録 
                                    ); 
       END IF; 
    END IF; 
  END processTokuisakiDlvry; 
  
  /*****************************************************************************/
  /* プロシージャ名：得意先配達時キャンセル処理                                     */
  /* 引数             ：itaku_rec      委託データ                                */
  /*                  ：jutaku_rec     受託データ                               */
  /*****************************************************************************/
  PROCEDURE processTokuisakiCancel( itaku_rec       IN itaku_rec_type  -- 委託データ
                                  , jutaku_rec      IN jutaku_rec_type -- 受託データ
  ) IS 
-- インボイス対応追加 start ABI
  wInvoicshikoymdflg  VARCHAR2(1)      := '0';   -- インボイス施行日フラグ（0:インボイス施行前、1:インボイス施行後）
  wS02M046T           S02M046T%rowtype := NULL;  -- ショップマスタデータ
-- インボイス対応追加 end ABI
  /* 最終受注日 */
  wLactacptdd                  VARCHAR2(8) := NULL;
  BEGIN 
    IF judgekyumin( itaku_rec.APKIHONCD, itaku_rec.SHOPCD, itaku_rec.TOKUISAKICD, itaku_rec.ACPTDNPYNO ) THEN 
       -- 得意先マスタ更新
       UPDATE S02M013T TKSM 
          SET TKSM.KYUMINKBN = '1' -- 休眠区分
            /* 更新者：更新なし */
        WHERE TKSM.APKIHONCD   = itaku_rec.APKIHONCD 
          AND TKSM.SHOPCD      = itaku_rec.SHOPCD 
          AND TKSM.TOKUISAKICD = itaku_rec.TOKUISAKICD 
          AND TKSM.YUKOKBN = '1' 
       ;
    END IF; 
    -- 最終受注日の取得
    wLactacptdd := getLastacptdd( itaku_rec.APKIHONCD, itaku_rec.SHOPCD, itaku_rec.TOKUISAKICD, itaku_rec.ACPTDNPYNO );

-- インボイス対応追加 start ABI
    -- インボイス施行状況区分の設定
    SELECT
      SHPM.*
    INTO wS02M046T
    FROM
      S02M046T SHPM 
    WHERE
      SHPM.APKIHONCD = itaku_rec.APKIHONCD 
      AND SHPM.SHOPCD = itaku_rec.SHOPCD  
      AND SHPM.YUKOKBN = '1'
    ;
    IF wS02M046T.INVOICSHIKOYMD <= itaku_rec.ACPTDD THEN
      wInvoicshikoymdflg := '1';
    END IF;
-- インボイス対応追加 end ABI

    -- 得意先実績テーブル更新
    UPDATE S02M050T TKJT 
-- インボイス対応変更 start ABI
       --SET TKJT.KEIJOMAEACPTKIN = NVL(TKJT.KEIJOMAEACPTKIN, 0) - itaku_rec.ACPTKIN 
       --                                                        - itaku_rec.ACPTSHOHITAXKIN -- 計上前受注金額
       SET TKJT.KEIJOMAEACPTKIN = (CASE WHEN itaku_rec.ACPTTHDAIBIKIFLG = '1' OR itaku_rec.SINGLDNPYFLG = '1' THEN -- 代引きの場合
                                       NVL(TKJT.KEIJOMAEACPTKIN, 0) - itaku_rec.ACPTKIN - itaku_rec.ACPTSHOHITAXKIN
                                   ELSE
                                       CASE WHEN wInvoicshikoymdflg = '0' THEN -- インボイス施行前の場合
                                           NVL(TKJT.KEIJOMAEACPTKIN, 0) - itaku_rec.ACPTKIN - itaku_rec.ACPTSHOHITAXKIN
                                       ELSE
                                           NVL(TKJT.KEIJOMAEACPTKIN, 0) - itaku_rec.ACPTKIN
                                       END
                                   END) -- 計上前受注金額
-- インボイス対応変更 end ABI
         /* 更新者：更新なし */
     WHERE TKJT.APKIHONCD   = itaku_rec.APKIHONCD
       AND TKJT.SHOPCD      = itaku_rec.SHOPCD
       AND TKJT.TOKUISAKICD = itaku_rec.TOKUISAKICD 
       AND TKJT.YUKOKBN = '1'
    ;
  END processTokuisakiCancel; 
  
  /****************************************************************************/
  /* プロシージャ名  ：処理済更新処理                                              */
  /* 引数           ：jutaku_rec          受託データ                             */
  /*                ：iKanrino            管理番号                              */
  /****************************************************************************/
  PROCEDURE updateWork( jutaku_rec    IN jutaku_rec_type -- 受託データ
                      , iKanrino      IN VARCHAR2        -- 管理番号
  ) IS 
  BEGIN 
    UPDATE S04W002T JHRW
       SET JHRW.TORIKOMISUMIKBN = '1' -- 取込済区分 
         /* 更新者 */
         , JHRW.KOSHAAPKIHONCD  = userinfo_rec.APKIHONCD -- 更新者ＡＰ基本コード
         , JHRW.KOSHASHOPCD     = userinfo_rec.SHOPCD    -- 更新者ショップコード
         , JHRW.KOSHACD         = userinfo_rec.TNSHACD        -- 更新者コード
         , JHRW.KSHNSYSTMYMD    = userinfo_rec.SYSTMYMD   -- 更新システム日付
         , JHRW.KSHNSYSTMJIKAN  = userinfo_rec.SYSTMJIKAN -- 更新システム時間
         , JHRW.KSHNUNYOYMD     = userinfo_rec.UNYOYMD    -- 更新運用日付
         , JHRW.KSHNIPADRSE     = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
         , JHRW.KSHNPGMID       = userinfo_rec.PGMID      -- 更新プログラムＩＤ
     WHERE JHRW.APKIHONCD  = jutaku_rec.APKIHONCD  -- AP基本コード
       AND JHRW.SHOPCD     = jutaku_rec.SHOPCD     -- ショップコード
       AND JHRW.ACPTDNPYNO = jutaku_rec.ACPTDNPYNO -- 受注伝票番号
       AND JHRW.EDANO      = jutaku_rec.EDANO      -- 枝番号
       AND JHRW.KANRINO = iKanrino -- 管理番号
       AND JHRW.YUKOKBN = '1'      -- 有効区分
    ;
  END updateWork;
  
  /****************************************************************************/
  /* プロシージャ名  ：ＡＰ受注売上データ連携処理                                   */
  /* 引数           ：iApkihoncd          ＡＰ基本コード                         */
  /*                ：iShopcd             ショップコード                         */
  /*                ：iKanrino            管理番号                              */
  /****************************************************************************/
  PROCEDURE ApJuchuUriageDatRenkei( iApkihoncd    IN VARCHAR2 -- ＡＰ基本コード
                                  , iShopcd       IN VARCHAR2 -- ショップコード
                                  , iKanrino      IN VARCHAR2 -- 管理番号
  ) IS 
  /* 変数宣言 */ 
  itaku_rec               itaku_rec_type   := NULL; -- 委託データ
  jutaku_rec              jutaku_rec_type  := NULL; -- 受託データ
  -- 得意先チェック用データ
  wItakuTokuisaki         S02M013T%rowtype := NULL; -- 委託得意先
  wJutakuTokuisaki        S02M013T%rowtype := NULL; -- 受託得意先
  -- 受注キャンセルチェック用データ 
  wDnpysts                VARCHAR(2) := NULL; -- 伝票ステータス
  wYukokbn                VARCHAR(1) := NULL; -- 有効区分
  -- 更新・登録用変数
  wIpaddress              VARCHAR(15):= NULL; -- ＩＰアドレス
  wErrorFlg               BOOLEAN := FALSE; 
-- インボイス対応追加 start ABI
  wInvoiceFlg             VARCHAR(1) := '0';   -- インボイス施行状況区分
-- インボイス対応追加 end ABI
  /* CURSOR：受注配達データ連携ワークから処理対象抽出 */
  -- 委託データ
  CURSOR SELECT_ITAKU( cAcptdnpyno VARCHAR2 
                     , cEdano NUMBER 
                     ) IS 
    SELECT JHTT_ITAKU.APKIHONCD   -- ＡＰ基本コード
         , JHTT_ITAKU.SHOPCD      -- ショップコード
         , JHTT_ITAKU.ACPTDNPYNO  -- 受注伝票番号
         , JHTT_ITAKU.EDANO       -- 枝番号
         , JHTT_ITAKU.TOKUISAKICD  -- 得意先コード
         , JHTT_ITAKU.TAXKEISANKBN -- 税計算区分
         , JHTT_ITAKU.DLVRYTRGTFLG -- 配達対象フラグ
         , JHTT_ITAKU.DNPYKBN      -- 伝票区分
         , JHTT_ITAKU.TORIHIKIKBN  -- 取引区分
         , JHTT_ITAKU.ACPTKBN      -- 受注区分
         , JHTT_ITAKU.ACPTKBNSHS   -- 受注区分詳細
         , JHTT_ITAKU.ACPTDD       -- 受注日
         , JHTT_ITAKU.ACPTTNSHAAPKIHONCD -- 受注担当者ＡＰ基本コード
         , JHTT_ITAKU.ACPTTNSHASHOPCD    -- 受注担当者ショップコード
         , JHTT_ITAKU.ACPTTNSHACD        -- 受注担当者コード
         , JHTT_ITAKU.DLVRYKANKATSUAPKIHONCD -- 配達管轄ＡＰ基本コード
         , JHTT_ITAKU.DLVRYKANKATSUSHOPCD    -- 配達管轄ショップコード
         , JHTT_ITAKU.DLVRYKYOTENCD          -- 配達拠点コード
         , JHTT_ITAKU.DLVRYAREACD            -- 配達エリアコード
         , JHTT_ITAKU.DLVRYKBN               -- 配達区分
         , JHTT_ITAKU.DLVRYCMPNYCD  -- 配達会社コード
         , JHTT_ITAKU.DLVRYJUN      -- 配達順
         , NVL(JHTT_ITAKU.DLVRYTNSHACD, TKSM_ITAKU.DLVRYTNSHACD) -- 配達担当者コード
         , JHTT_ITAKU.ACPTKIN         -- 受注金額
         , JHTT_ITAKU.ACPTSHOHITAXKIN -- 受注消費税金額
         , JHTT_ITAKU.PRNTMEMON1 -- 印刷メモ１
         , JHTT_ITAKU.PRNTMEMON2 -- 印刷メモ２
         , JHTT_ITAKU.PRNTMEMON3 -- 印刷メモ３
         , JHTT_ITAKU.DATSKSMOTOKBN -- データ作成元区分
         , JHTT_ITAKU.AZUKARIBTLN1N2RITTORUHNS -- 預かりボトル１２Ｌ本数
         , JHTT_ITAKU.AZUKARIBTLN8RITTORUHNS   -- 預かりボトル８Ｌ本数
         -- 得意先マスタ
         , TKSM_ITAKU.STARTUP          -- スタートアップ
         , TKSM_ITAKU.TOKUISAKIJOTAI   -- 得意先状態
         , TKSM_ITAKU.SHOKAIWSVSTUPDD  -- 初回サーバー設置日
         , TKSM_ITAKU.EIGYOTNSHACD     -- 営業担当者コード
         , TKSM_ITAKU.URIKEIJOTNSHAKBN -- 売上計上担当者区分
         , TKSM_ITAKU.SKYSAKICD        -- 請求先コード
         -- 請求先マスタ
         , SKSM_ITAKU.KAISHUHOHOKBN   -- 回収方法区分
         /* 得意先チェック用データ */
         , TKSM_ITAKU.APKIHONCD            -- ＡＰ基本コード
         , TKSM_ITAKU.SHOPCD               -- ショップコード
         , TKSM_ITAKU.TOKUISAKICD          -- 得意先コード
         , TKSM_ITAKU.ITAKUAITEAPKIHONCD   -- 委託相手ＡＰ基本コード
         , TKSM_ITAKU.ITAKUAITESHOPCD      -- 委託相手ショップコード
         , TKSM_ITAKU.ITAKUAITETOKUISAKICD -- 委託相手得意先コード
-- インボイス対応追加 start ABI
         , TKSM_ITAKU.TOKUISAKIKBN      -- 得意先区分
         , TKSM_ITAKU.JUTAKUDLVRYCSTKBN -- 受託配達顧客区分
         , JHTT_ITAKU.ACPTTHDAIBIKIFLG  -- 受注時代引きフラグ
         , JHTT_ITAKU.SINGLDNPYFLG      -- 単体伝票締めフラグ
         , JHTT_ITAKU.DLVRYYTIDD        -- 配達予定日
-- インボイス対応追加 end ABI
         /* 受注キャンセルチェック用データ */
         , JHTT_ITAKU.DNPYSTS    -- 伝票ステータス
         , JHTT_ITAKU.YUKOKBN    -- 有効区分
      FROM S04W002T JHRW 
           INNER JOIN S04T001T JHTT_ITAKU -- 受注配達テーブル（委託データ）
           ON ( JHTT_ITAKU.APKIHONCD      = JHRW.ITAKUAITEAPKIHONCD 
                AND JHTT_ITAKU.SHOPCD     = JHRW.ITAKUAITESHOPCD 
                AND JHTT_ITAKU.ACPTDNPYNO = JHRW.ITAKUAITEACPTDNPYNO 
                AND JHTT_ITAKU.EDANO      = JHRW.ITAKUAITEEDANO 
              )
           LEFT OUTER JOIN S02M013T TKSM_ITAKU -- 得意先マスタ（委託データ）
           ON ( TKSM_ITAKU.APKIHONCD       = JHTT_ITAKU.APKIHONCD    
                AND TKSM_ITAKU.SHOPCD      = JHTT_ITAKU.SHOPCD
                AND TKSM_ITAKU.TOKUISAKICD = JHTT_ITAKU.TOKUISAKICD
                AND TKSM_ITAKU.YUKOKBN = '1'
              )
           LEFT OUTER JOIN S02M017T SKSM_ITAKU -- 請求先マスタ（委託データ）
           ON ( SKSM_ITAKU.APKIHONCD     = TKSM_ITAKU.APKIHONCD 
                AND SKSM_ITAKU.SHOPCD    = TKSM_ITAKU.SHOPCD
                AND SKSM_ITAKU.SKYSAKICD = TKSM_ITAKU.SKYSAKICD
                AND SKSM_ITAKU.YUKOKBN = '1'
              ) 
           LEFT OUTER JOIN S02M050T TKJT_ITAKU -- 得意先実績テーブル（委託データ）
           ON ( TKJT_ITAKU.APKIHONCD       = JHTT_ITAKU.APKIHONCD
                AND TKJT_ITAKU.SHOPCD      = JHTT_ITAKU.SHOPCD
                AND TKJT_ITAKU.TOKUISAKICD = JHTT_ITAKU.TOKUISAKICD
                AND TKJT_ITAKU.YUKOKBN = '1'
              )
     WHERE JHRW.APKIHONCD = iApkihoncd -- ＡＰ基本コード
       AND JHRW.SHOPCD    = iShopcd    -- ショップコード
       AND JHRW.KANRINO   = iKanrino   -- 管理番号
       AND JHRW.TORIKOMISUMIKBN = '0'  -- 取込済区分
       AND JHRW.YUKOKBN   = '1'        -- 有効区分 
       AND JHRW.ACPTDNPYNO = cAcptdnpyno 
       AND JHRW.EDANO = cEdano 
  ; 
  -- 受託データ
  CURSOR SELECT_JUTAKU ( cAcptdnpyno VARCHAR2 
                       , cEdano NUMBER 
                       ) IS 
    SELECT JHTT_JUTAKU.APKIHONCD  -- ＡＰ基本コード
         , JHTT_JUTAKU.SHOPCD     -- ショップコード
         , JHTT_JUTAKU.ACPTDNPYNO -- 受注伝票番号
         , JHTT_JUTAKU.EDANO      -- 枝番号
         , JHTT_JUTAKU.DLVRYKBN   -- 配達区分
         , JHTT_JUTAKU.DLVRYSTS   -- 配達ステータス
         , JHTT_JUTAKU.KZBTLN1N2RITTORUHNS -- 回収実績ボトル１２Ｌ本数
         , JHTT_JUTAKU.KZBTLN8RITTORUHNS   -- 回収実績ボトル８Ｌ本数
         , JHTT_JUTAKU.DNPYSTS             -- 伝票ステータス
         , JHTT_JUTAKU.DLVRYDD        -- 配達日
         , JHTT_JUTAKU.DLVRYYTIDD     -- 配達予定日
         , JHTT_JUTAKU.DLVRYJISSEKIDD -- 配達実績日
         , JHTT_JUTAKU.ACPTDD         -- 受注日
         , JHTT_JUTAKU.JIKAIDLVRYYTIDD     -- 次回配達予定日
         , JHTT_JUTAKU.DLVRYKIBOJIKAN      -- 配達希望時間
         , JHTT_JUTAKU.DLVRYKIBOJIKANSHRY  -- 配達希望時間終了
         , JHTT_JUTAKU.DLVRYKIBOJIKANKBN   -- 配達希望時間区分
         , JHTT_JUTAKU.FZAIOKIKANOFLG      -- 不在置き可能フラグ
         , JHTT_JUTAKU.KYBTLN1N2RITTORUHNS      -- 回収予定ボトル１２Ｌ本数
         , JHTT_JUTAKU.KYBTLN8RITTORUHNS        -- 回収予定ボトル８Ｌ本数
         , JHTT_JUTAKU.ACPTSHIMEPRCSDD         -- 受注締処理日
         , JHTT_JUTAKU.ACPTSHIMETNSHAAPKIHONCD -- 受注締担当者ＡＰ基本コード
         , JHTT_JUTAKU.ACPTSHIMETNSHASHOPCD    -- 受注締担当者ショップコード
         , JHTT_JUTAKU.ACPTSHIMEPRCSTNSHACD    -- 受注締処理担当者コード
         , JHTT_JUTAKU.NOSHOPRNTTRGTFLG        -- 納品書印刷対象フラグ
         , JHTT_JUTAKU.NOSHOPRNTSUMIFLG        -- 納品書印刷済フラグ
         , JHTT_JUTAKU.NOSHOPRNTDD             -- 納品書印刷日
         , JHTT_JUTAKU.NOSHOHIKAEPRNTTRGTFLG   -- 納品書控印刷対象フラグ
         , JHTT_JUTAKU.NOSHOHIKAEPRNTSUMIFLG   -- 納品書控印刷済フラグ
         , JHTT_JUTAKU.NOSHOHIKAEPRNTDD        -- 納品書控印刷日
         , JHTT_JUTAKU.NYKNHYOPRNTTRGTFLG      -- 入金票印刷対象フラグ
         , JHTT_JUTAKU.NYKNHYOPRNTSUMIFLG      -- 入金票印刷済フラグ
         , JHTT_JUTAKU.NYKNHYOPRNTDD           -- 入金票印刷日
         , JHTT_JUTAKU.WSVSIJISHOPRNTTRGTFLG   -- サーバー指示書印刷対象フラグ
         , JHTT_JUTAKU.WSVSIJISHOPRNTSUMIFLG   -- サーバー指示書印刷済フラグ
         , JHTT_JUTAKU.WSVSIJISHOPRNTDD        -- サーバー指示書印刷日
         , JHTT_JUTAKU.SKSMOTOKANRIKBN -- 作成元管理区分
         , JHTT_JUTAKU.SKSMOTOKANRINO  -- 作成元管理番号
         , JHTT_JUTAKU.ACPTHIKIATESAKIAPKIHONCD -- 受注引当先ＡＰ基本コード
         , JHTT_JUTAKU.ACPTHIKIATESAKISHOPCD    -- 受注引当先ショップコード
         , JHTT_JUTAKU.ACPTHIKIATESAKIKYOTENCD  -- 受注引当先拠点コード
         , JHTT_JUTAKU.KURERURITTORUSTUP    -- クレールＬ設置
         , JHTT_JUTAKU.KURERURITTORUHIKIAGE -- クレールＬ引上
         , JHTT_JUTAKU.KURERUESUSTUP        -- クレールＳ設置
         , JHTT_JUTAKU.KURERUESUHIKIAGE     -- クレールＳ引上
         , JHTT_JUTAKU.SONOTASTUP           -- その他設置
         , JHTT_JUTAKU.SONOTAHIKIAGE        -- その他引上
         , JHTT_JUTAKU.ZNKDLVRYDD       -- 前回配達日
         , JHTT_JUTAKU.ZNKDLVRYHNS      -- 前回配達本数
         , JHTT_JUTAKU.ZNKSHIMEDD       -- 前回締日
         , JHTT_JUTAKU.ZNKKURIKOSHIKIN  -- 前回繰越金額
         , JHTT_JUTAKU.ACPTTHDAIBIKIFLG -- 受注時代引フラグ
         , JHTT_JUTAKU.ACPTRCPTKEIROKBN -- 受注受付経路区分
         , JHTT_JUTAKU.YUKOKBN -- 有効区分
         /* 得意先チェック用データ */
         , TKSM_JUTAKU.APKIHONCD            -- ＡＰ基本コード
         , TKSM_JUTAKU.SHOPCD               -- ショップコード
         , TKSM_JUTAKU.TOKUISAKICD          -- 得意先コード
         , TKSM_JUTAKU.ITAKUAITEAPKIHONCD   -- 委託相手ＡＰ基本コード
         , TKSM_JUTAKU.ITAKUAITESHOPCD      -- 委託相手ショップコード
         , TKSM_JUTAKU.ITAKUAITETOKUISAKICD -- 委託相手得意先コード
         /* 更新用データ */
         , JHTT_JUTAKU.KSHNIPADRSE -- 更新ＩＤアドレス
      FROM S04W002T JHRW 
           INNER JOIN S04T001T JHTT_JUTAKU -- 受注配達テーブル（受託データ）
           ON ( JHTT_JUTAKU.APKIHONCD      = JHRW.APKIHONCD
                AND JHTT_JUTAKU.SHOPCD     = JHRW.SHOPCD
                AND JHTT_JUTAKU.ACPTDNPYNO = JHRW.ACPTDNPYNO
                AND JHTT_JUTAKU.EDANO      = JHRW.EDANO
                AND JHTT_JUTAKU.YUKOKBN = '1'
              )
           LEFT OUTER JOIN S02M013T TKSM_JUTAKU -- 得意先マスタ（受託データ）
           ON ( TKSM_JUTAKU.APKIHONCD       = JHTT_JUTAKU.APKIHONCD
                AND TKSM_JUTAKU.SHOPCD      = JHTT_JUTAKU.SHOPCD
                AND TKSM_JUTAKU.TOKUISAKICD = JHTT_JUTAKU.TOKUISAKICD
                AND TKSM_JUTAKU.YUKOKBN = '1'
              )
     WHERE JHRW.APKIHONCD = iApkihoncd -- ＡＰ基本コード
       AND JHRW.SHOPCD    = iShopcd    -- ショップコード
       AND JHRW.KANRINO   = iKanrino   -- 管理番号
       AND JHRW.TORIKOMISUMIKBN = '0'  -- 取込済区分
       AND JHRW.YUKOKBN   = '1'        -- 有効区分
       AND JHRW.ACPTDNPYNO = cAcptdnpyno 
       AND JHRW.EDANO = cEdano 
  ; 
  BEGIN 
    -- 入力パラメータチェック
    IF iApKihoncd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20001,
                              'パラメータ.ＡＰ基本コード が設定されていません。');
    END IF;
    IF iShopcd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20002,
                              'パラメータ.ショップコード  が設定されていません。');
    END IF;
    IF iKanrino IS NULL THEN
      RAISE_APPLICATION_ERROR(-20003,
                              'パラメータ.管理番号  が設定されていません。');
    END IF;
    -- 共通変数削除
    userinfo_rec := NULL;
    sum_rec      := NULL;
    
    -- 伝票単位で実行
    FOR work_rec IN ( SELECT WRK.* 
                        FROM S04W002T WRK 
                       WHERE WRK.APKIHONCD = iApkihoncd -- ＡＰ基本コード
                         AND WRK.SHOPCD    = iShopcd -- ショップコード
                         AND WRK.KANRINO   = iKanrino -- 管理番号
                         AND WRK.TORIKOMISUMIKBN = '0' -- 取込済区分
                         AND WRK.YUKOKBN   = '1' -- 有効区分
                    ) LOOP 
    
    OPEN SELECT_ITAKU(work_rec.ACPTDNPYNO, work_rec.EDANO);
    OPEN SELECT_JUTAKU(work_rec.ACPTDNPYNO, work_rec.EDANO);
      BEGIN 
      LOOP 
        -- 処理対象抽出処理（ヘッダ部）
        FETCH SELECT_ITAKU INTO itaku_rec.APKIHONCD   -- ＡＰ基本コード
                              , itaku_rec.SHOPCD      -- ショップコード
                              , itaku_rec.ACPTDNPYNO  -- 受注伝票番号
                              , itaku_rec.EDANO       -- 枝番号
                              , itaku_rec.TOKUISAKICD  -- 得意先コード
                              , itaku_rec.TAXKEISANKBN -- 税計算区分
                              , itaku_rec.DLVRYTRGTFLG -- 配達対象フラグ
                              , itaku_rec.DNPYKBN      -- 伝票区分
                              , itaku_rec.TORIHIKIKBN  -- 取引区分
                              , itaku_rec.ACPTKBN      -- 受注区分
                              , itaku_rec.ACPTKBNSHS   -- 受注区分詳細
                              , itaku_rec.ACPTDD       -- 受注日
                              , itaku_rec.ACPTTNSHAAPKIHONCD -- 受注担当者ＡＰ基本コード
                              , itaku_rec.ACPTTNSHASHOPCD    -- 受注担当者ショップコード
                              , itaku_rec.ACPTTNSHACD        -- 受注担当者コード
                              , itaku_rec.DLVRYKANKATSUAPKIHONCD -- 配達管轄ＡＰ基本コード
                              , itaku_rec.DLVRYKANKATSUSHOPCD    -- 配達管轄ショップコード
                              , itaku_rec.DLVRYKYOTENCD          -- 配達拠点コード
                              , itaku_rec.DLVRYAREACD            -- 配達エリアコード
                              , itaku_rec.DLVRYKBN               -- 配達区分
                              , itaku_rec.DLVRYCMPNYCD -- 配達会社コード
                              , itaku_rec.DLVRYJUN     -- 配達順
                              , itaku_rec.DLVRYTNSHACD -- 配達担当者コード
                              , itaku_rec.ACPTKIN         -- 受注金額
                              , itaku_rec.ACPTSHOHITAXKIN -- 受注消費税金額
                              , itaku_rec.PRNTMEMON1 -- 印刷メモ１
                              , itaku_rec.PRNTMEMON2 -- 印刷メモ２
                              , itaku_rec.PRNTMEMON3 -- 印刷メモ３
                              , itaku_rec.DATSKSMOTOKBN -- データ作成元区分
                              , itaku_rec.AZUKARIBTLN1N2RITTORUHNS -- 預かりボトル１２Ｌ本数
                              , itaku_rec.AZUKARIBTLN8RITTORUHNS   -- 預かりボトル８Ｌ本数
                              -- 得意先マスタ
                              , itaku_rec.STARTUP          -- スタートアップ
                              , itaku_rec.TOKUISAKIJOTAI   -- 得意先状態
                              , itaku_rec.SHOKAIWSVSTUPDD  -- 初回サーバー設置日
                              , itaku_rec.EIGYOTNSHACD     -- 営業担当者コード
                              , itaku_rec.URIKEIJOTNSHAKBN -- 売上計上担当者コード
                              , itaku_rec.SKYSAKICD        -- 請求先コード
                              , itaku_rec.KAISHUHOHOKBN -- 回収方法区分
                              -- 得意先実績テーブル
                              /* 得意先チェック用データ */
                              , wItakuTokuisaki.APKIHONCD            -- ＡＰ基本コード
                              , wItakuTokuisaki.SHOPCD               -- ショップコード
                              , wItakuTokuisaki.TOKUISAKICD          -- 得意先コード
                              , wItakuTokuisaki.ITAKUAITEAPKIHONCD   -- 委託相手ＡＰ基本コード
                              , wItakuTokuisaki.ITAKUAITESHOPCD      -- 委託相手ショップコード
                              , wItakuTokuisaki.ITAKUAITETOKUISAKICD -- 委託相手得意先コード
-- インボイス対応追加 start ABI
                              , itaku_rec.TOKUISAKIKBN      -- 得意先区分
                              , itaku_rec.JUTAKUDLVRYCSTKBN -- 受託配達顧客区分
                              , itaku_rec.ACPTTHDAIBIKIFLG  -- 受注時代引きフラグ
                              , itaku_rec.SINGLDNPYFLG      -- 単体伝票締めフラグ
                              , itaku_rec.DLVRYYTIDD        -- 配達予定日
-- インボイス対応追加 end ABI
                              /* 受注キャンセルチェック用データ */
                              , wDnpysts -- 伝票ステータス
                              , wYukokbn -- 有効区分
        ; 
        FETCH SELECT_JUTAKU INTO jutaku_rec.APKIHONCD  -- ＡＰ基本コード
                               , jutaku_rec.SHOPCD     -- ショップコード
                               , jutaku_rec.ACPTDNPYNO -- 受注伝票番号
                               , jutaku_rec.EDANO      -- 枝番号
                               , jutaku_rec.DLVRYKBN   -- 配達区分
                               , jutaku_rec.DLVRYSTS   -- 配達ステータス
                               , jutaku_rec.KZBTLN1N2RITTORUHNS -- 回収実績ボトル１２Ｌ本数
                               , jutaku_rec.KZBTLN8RITTORUHNS   -- 回収実績ボトル８Ｌ本数
                               , jutaku_rec.DNPYSTS    -- 伝票ステータス
                               , jutaku_rec.DLVRYDD        -- 配達日
                               , jutaku_rec.DLVRYYTIDD     -- 配達予定日
                               , jutaku_rec.DLVRYJISSEKIDD -- 配達実績日
                               , jutaku_rec.ACPTDD         -- 受注日
                               , jutaku_rec.JIKAIDLVRYYTIDD     -- 次回配達予定日
                               , jutaku_rec.DLVRYKIBOJIKAN      -- 配達希望時間
                               , jutaku_rec.DLVRYKIBOJIKANSHRY  -- 配達希望時間終了
                               , jutaku_rec.DLVRYKIBOJIKANKBN   -- 配達希望時間区分
                               , jutaku_rec.FZAIOKIKANOFLG      -- 不在置き可能フラグ
                               , jutaku_rec.KYBTLN1N2RITTORUHNS      -- 回収予定ボトル１２Ｌ本数
                               , jutaku_rec.KYBTLN8RITTORUHNS        -- 回収予定ボトル８Ｌ本数
                               , jutaku_rec.ACPTSHIMEPRCSDD         -- 受注締処理日
                               , jutaku_rec.ACPTSHIMETNSHAAPKIHONCD -- 受注締担当者ＡＰ基本コード
                               , jutaku_rec.ACPTSHIMETNSHASHOPCD    -- 受注締担当者ショップコード
                               , jutaku_rec.ACPTSHIMEPRCSTNSHACD    -- 受注締処理担当者コード
                               , jutaku_rec.NOSHOPRNTTRGTFLG        -- 納品書印刷対象フラグ
                               , jutaku_rec.NOSHOPRNTSUMIFLG        -- 納品書印刷済フラグ
                               , jutaku_rec.NOSHOPRNTDD             -- 納品書印刷日
                               , jutaku_rec.NOSHOHIKAEPRNTTRGTFLG   -- 納品書控印刷対象フラグ
                               , jutaku_rec.NOSHOHIKAEPRNTSUMIFLG   -- 納品書控印刷済フラグ
                               , jutaku_rec.NOSHOHIKAEPRNTDD        -- 納品書控印刷日
                               , jutaku_rec.NYKNHYOPRNTTRGTFLG      -- 入金票印刷対象フラグ
                               , jutaku_rec.NYKNHYOPRNTSUMIFLG      -- 入金票印刷済フラグ
                               , jutaku_rec.NYKNHYOPRNTDD           -- 入金票印刷日
                               , jutaku_rec.WSVSIJISHOPRNTTRGTFLG   -- サーバー指示書印刷対象フラグ
                               , jutaku_rec.WSVSIJISHOPRNTSUMIFLG   -- サーバー指示書印刷済フラグ
                               , jutaku_rec.WSVSIJISHOPRNTDD        -- サーバー指示書印刷日
                               , jutaku_rec.SKSMOTOKANRIKBN -- 作成元管理区分
                               , jutaku_rec.SKSMOTOKANRINO  -- 作成元管理番号
                               , jutaku_rec.ACPTHIKIATESAKIAPKIHONCD -- 受注引当先ＡＰ基本コード
                               , jutaku_rec.ACPTHIKIATESAKISHOPCD    -- 受注引当先ショップコード
                               , jutaku_rec.ACPTHIKIATESAKIKYOTENCD  -- 受注引当先拠点コード
                               , jutaku_rec.KURERURITTORUSTUP    -- クレールＬ設置
                               , jutaku_rec.KURERURITTORUHIKIAGE -- クレールＬ引上
                               , jutaku_rec.KURERUESUSTUP        -- クレールＳ設置
                               , jutaku_rec.KURERUESUHIKIAGE     -- クレールＳ引上
                               , jutaku_rec.SONOTASTUP           -- その他設置
                               , jutaku_rec.SONOTAHIKIAGE        -- その他引上
                               , jutaku_rec.ZNKDLVRYDD_JHTT  -- 前回配達日
                               , jutaku_rec.ZNKDLVRYHNS      -- 前回配達本数
                               , jutaku_rec.ZNKSHIMEDD       -- 前回締日
                               , jutaku_rec.ZNKKURIKOSHIKIN  -- 前回繰越金額
                               , jutaku_rec.ACPTTHDAIBIKIFLG -- 受注時代引フラグ
                               , jutaku_rec.ACPTRCPTKEIROKBN -- 受注受付経路区分
                               , jutaku_rec.YUKOKBN -- 有効区分
                               /* 得意先チェック用データ */
                               , wJutakuTokuisaki.APKIHONCD            -- ＡＰ基本コード
                               , wJutakuTokuisaki.SHOPCD               -- ショップコード
                               , wJutakuTokuisaki.TOKUISAKICD          -- 得意先コード
                               , wJutakuTokuisaki.ITAKUAITEAPKIHONCD   -- 委託相手ＡＰ基本コード
                               , wJutakuTokuisaki.ITAKUAITESHOPCD      -- 委託相手ショップコード
                               , wJutakuTokuisaki.ITAKUAITETOKUISAKICD -- 委託相手得意先コード
                               , wIpaddress -- ＩＰアドレス
        ; 
        
        IF SELECT_ITAKU%ROWCOUNT = 0 OR SELECT_JUTAKU%ROWCOUNT = 0 THEN 
           RAISE_APPLICATION_ERROR(-20004, '処理対象データが存在しません。');
        END IF;
        EXIT WHEN SELECT_ITAKU%NOTFOUND;
        
        -- 受注伝票番号単位で処理を行う
        
        /* 委託・受託得意先チェック */ 
        checkTokuisaki( wItakuTokuisaki, wJutakuTokuisaki ); 
        
        /* 受注キャンセルチェック */
        IF wDnpysts = '09' THEN 
           RAISE_APPLICATION_ERROR(-20012, '委託データは受注キャンセル済です。');
        END IF;
        IF wYukokbn = '0' THEN 
           RAISE_APPLICATION_ERROR(-20013, '委託データは無効です。');
        END IF;
        
        -- 基本情報設定
        itaku_rec.RENKEITNSHACD := getMojisetteiatai( itaku_rec.APKIHONCD, itaku_rec.SHOPCD, '212', NULL, NULL, NULL, '受託配達データ連携担当者コード' );
        setUserInfo( itaku_rec.APKIHONCD, itaku_rec.SHOPCD, itaku_rec.RENKEITNSHACD, wIpaddress, 'C0815B11' );
        -- 配布物企画対象得意先マスタ更新処理
        processHaifubutsu( itaku_rec, jutaku_rec );
        
        -- 受注配達データ更新処理 
        CASE 
          WHEN jutaku_rec.DNPYSTS = '12' THEN            -- 伝票ステータス：配達売上済 
-- インボイス対応変更 start ABI
--            processJuchuMeisai( itaku_rec, jutaku_rec ); 
--            updateJuchuDelivered( itaku_rec, jutaku_rec ); 
            processJuchuMeisai( itaku_rec, jutaku_rec, wInvoiceFlg ); 
            updateJuchuDelivered( itaku_rec, jutaku_rec, wInvoiceFlg ); 
-- インボイス対応変更 end ABI
          WHEN jutaku_rec.DNPYSTS IN ( '13', '14' ) THEN -- 伝票ステータス：未配達／不在未配達
            updateJuchuNotDeliver( itaku_rec, jutaku_rec ); 
            -- 再配達 
            processSaihaitatsu( itaku_rec, jutaku_rec );
          WHEN jutaku_rec.DNPYSTS = '19' THEN            -- 伝票ステータス：配達時キャンセル
            updateJuchuNotDeliver( itaku_rec, jutaku_rec ); 
          ELSE -- その他の伝票ステータス
            RAISE_APPLICATION_ERROR(-20014, '受託データの伝票ステータスは無効です。'); 
        END CASE;
        
        -- 売上データ作成
        IF jutaku_rec.DNPYSTS = '12' THEN -- 伝票ステータス：配達売上済 
-- インボイス対応変更 start ABI
--            processUriage( itaku_rec ); 
            processUriage( itaku_rec, jutaku_rec, wInvoiceFlg ); 
-- インボイス対応変更 end ABI
        END IF; 
        
        -- 得意先マスタ会員マスタ更新処理
        IF jutaku_rec.DNPYSTS = '12' THEN -- 伝票ステータス：配達売上済 
           processTokuisakiDlvry( itaku_rec ); 
        END IF; 
        
        -- 得意先配達時キャンセル処理：休眠区分＆計上前受注金額
        IF jutaku_rec.DNPYSTS = '19' THEN -- 伝票ステータス：未配達／不在未配達
           processTokuisakiCancel( itaku_rec, jutaku_rec ); 
        END IF;
        
        -- 処理済に更新
        updateWork( jutaku_rec, iKanrino );
        
        COMMIT;
          
      END LOOP;

      EXCEPTION 
        WHEN OTHERS THEN 
          ROLLBACK; 
          -- エラー
          S99_CHECK_LOG( 'C0815B11_'||iKanrino, iApkihoncd, iShopcd
              , '1', 'E', '9', null, null 
              , userinfo_rec.APKIHONCD, userinfo_rec.SHOPCD, userinfo_rec.TNSHACD
              , userinfo_rec.IPADRSE, userinfo_rec.PGMID 
              , ' 伝票 '|| work_rec.ACPTDNPYNO || '：' || SQLERRM );
          wErrorFlg := TRUE; 
      END; 
    CLOSE SELECT_JUTAKU;
    CLOSE SELECT_ITAKU; 
    
    END LOOP; 
    
    IF wErrorFlg THEN 
       RAISE_APPLICATION_ERROR(-20099, '委託へ連携できない明細が存在します。');
    END IF;
    
    -- EXCEPTION：処理終了
    EXCEPTION 
      WHEN OTHERS THEN 
        ROLLBACK;
        -- カーソル オーペンであれば、クローズする
        IF SELECT_JUTAKU%ISOPEN THEN 
           CLOSE SELECT_JUTAKU; 
        END IF;
        IF SELECT_ITAKU%ISOPEN THEN 
           CLOSE SELECT_ITAKU; 
        END IF; 
        -- 共通変数削除
        userinfo_rec := NULL;
        sum_rec      := NULL;
        -- エラー
        RAISE_APPLICATION_ERROR(-20705, SQLERRM);
        
  END ApJuchuUriageDatRenkei;

END C0815B11;
/
exit;
