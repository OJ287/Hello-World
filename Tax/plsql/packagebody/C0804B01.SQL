create or replace 
PACKAGE BODY C0804B01 
/*******************************************************************************
 * システム名       ：ＡＰ総合管理システム
 * 機能名           ：本部受注データ連携
 *
 ******************************************************************************T
   Date       Author         Changes Key                                 Version
   -----------------------------------------------------------------------------
   2022-05-16 ABI)Yokota       Create                                      1.0.0
   2026-10-08 ABI)Yokota       作業予約機能追加対応                        1.0.1
 ******************************************************************************/
 AS 
 
  /****************************************************************************/
  /* 共通変数宣言                                                               */
  /****************************************************************************/
  vRecordS04T001T S04T001T%rowtype;          --受注配達テーブル
  vRecordS04T002T S04T002T%rowtype;          --受注配達明細テーブル
  DATSKSMOTOKBN   CONSTANT VARCHAR2(2) := '20'; -- データ作成元区分：委託
  
  -- 本数、金額、サーバー集計用変数
  vWatercd        VARCHAR2(2);               -- 商品大分類コード：製品水
-- インボイス対応追加 start ABI
  vInvoicshikoymd             VARCHAR2(8) := NULL;  -- インボイス施行日
-- インボイス対応追加 end ABI
  
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
  ) RETURN VARCHAR2 IS returnSuchisetteiatai NUMBER(20);
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
  END getSuchisetteiatai;
  
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
  /* ファンクション名：得意先休眠判定                                              */
  /* 引数             ：iApkihoncd             ＡＰ基本コード                    */
  /*                  ：iShopcd                ショップコード                    */
  /*                  ：iTokuisakicd           得意先コード                     */
  /*                  ：iAcptdnpyno            受注伝票番号                     */
  /* return           ：returnJudgeKyumin      休眠：TRUE                       */
  /*****************************************************************************/
  FUNCTION judgeKyumin( iApkihoncd   IN VARCHAR2
                      , iShopcd      IN VARCHAR2
                      , iTokuisakicd IN VARCHAR2
                      , iAcptdnpyno  IN VARCHAR2 
  ) RETURN BOOLEAN IS returnJudgeKyumin BOOLEAN; 
  /* 変数宣言 */ 
  wCount            NUMBER(2,0); -- 休眠対象得意先判定
  wUnyoYmd          S02M013T.KSHNUNYOYMD%TYPE;      -- 運用日付
  wKyuminJudgeCnt   S02M006T.SUCHISETTEIATAI%TYPE;  -- 得意先休眠判定日数
  wKyuminStDate     S02M050T.SAISHUACPTDD%type;     -- 休眠開始日
  wMaxAcptDd        S04T001T.ACPTDD%TYPE;           -- 最終受注日

  BEGIN
    returnJudgeKyumin := FALSE; -- 初期値
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
     WHERE TKSM.TOKUISAKIKBN   IN ('1', '5', '6') -- 得意先区分：顧客
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
       RETURN(returnJudgeKyumin); -- 休眠対象外
    END IF; 
    
    -- 運用日付取得
    wUnyoYmd := S01GETOPERATIONDATE(iApKihoncd, iShopcd); 
    -- システム管理マスタから休眠判定日数を取得
    wKyuminJudgeCnt := getSuchisetteiatai( '000', '000', '120', '得意先休眠判定日数' );
    IF wKyuminJudgeCnt IS NULL OR wKyuminJudgeCnt = 0 THEN 
       RETURN(FALSE); 
    END IF;
    
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
       returnJudgeKyumin := TRUE; 
    END IF; 
    
    RETURN(returnJudgeKyumin);
  END judgeKyumin;
    
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
      INTO vRecordS04T001T.KSHNSYSTMYMD
         , vRecordS04T001T.KSHNSYSTMJIKAN
      FROM DUAL;
    
    -- 運用日付
    vRecordS04T001T.KSHNUNYOYMD := S01GETOPERATIONDATE(iApKihoncd, iShopcd); 
    
    -- その他の基本情報
    vRecordS04T001T.KOSHAAPKIHONCD := iApKihoncd; -- 更新者ＡＰ基本コード
    vRecordS04T001T.KOSHASHOPCD    := iShopcd;    -- 更新者ショップコード
    vRecordS04T001T.KOSHACD        := iTnshacd;   -- 更新者コード
    vRecordS04T001T.KSHNIPADRSE    := iIpaddress; -- ＩＰアドレス
    vRecordS04T001T.KSHNPGMID      := iPgmid;     -- 更新プログラムＩＤ
    
  END setUserInfo;
  
  /****************************************************************************/
  /* プロシージャ名：受託側得意先情報設定                                           */
  /* 引数(OUT)        ：oZaikokanrikbn      在庫管理区分                         */
  /*                  ：oSaishuacptdd       最終受注日                          */
  /****************************************************************************/
  PROCEDURE setJutakuTokuisaki( oZaikokanrikbn OUT VARCHAR2 
                              , oSaishuacptdd  OUT VARCHAR2
  ) IS
  /* 変数宣言 */
  wJutakuApkihoncd      VARCHAR2(3) := NULL; -- 受託側ＡＰ基本コード
  wJutakuShopcd         VARCHAR2(3) := NULL; -- 受託側ショップコード
  wJutakuTokuisakicd    VARCHAR2(6) := NULL; -- 受託側得意先コード
  wItakuApkihoncd       VARCHAR2(3) := NULL; -- 委託側ＡＰ基本コード
  wItakuShopcd          VARCHAR2(3) := NULL; -- 委託側ショップコード
  wItakuTokuisakicd     VARCHAR2(6) := NULL; -- 委託側得意先コード
  
  BEGIN
    S99_CHECK_LOG('C0804B01.setJutakuTK', vRecordS04T001T.APKIHONCD, vRecordS04T001T.APKIHONCD, '1', 'S', '1', 'XXX', 0, NULL, NULL, NULL, NULL, 'C0804B01' , '受託側得意先情報設定[開始] ' );
    /* 委託側得意先マスタ取得 */
    BEGIN 
      SELECT TKSM_ITAKU.ITAKUAITEAPKIHONCD 
           , TKSM_ITAKU.ITAKUAITESHOPCD 
           , TKSM_ITAKU.ITAKUAITETOKUISAKICD 
        INTO wJutakuApkihoncd
           , wJutakuShopcd
           , wJutakuTokuisakicd
        FROM S02M013T TKSM_ITAKU -- 得意先マスタ（委託側）
       WHERE TKSM_ITAKU.APKIHONCD   = vRecordS04T001T.APKIHONCD
         AND TKSM_ITAKU.SHOPCD      = vRecordS04T001T.SHOPCD
         AND TKSM_ITAKU.TOKUISAKICD = vRecordS04T001T.TOKUISAKICD
         AND TKSM_ITAKU.YUKOKBN     = '1'
      ;
      /* 相手先コードチェック（委託） */
      IF wJutakuApkihoncd IS NULL OR wJutakuShopcd IS NULL OR wJutakuTokuisakicd IS NULL THEN 
         RAISE_APPLICATION_ERROR(-20102, '委託の相手先コードが設定されていません。');
      END IF;
      /* 得意先マスタ存在チェック（委託） */
      EXCEPTION
        WHEN NO_DATA_FOUND THEN 
          RAISE_APPLICATION_ERROR(-20101, '委託の得意先マスタが存在しません。'); 
    END ;
    
    /* 受託側得意先マスタ取得 */
    BEGIN 
      SELECT TKSM_JUTAKU.ITAKUAITEAPKIHONCD
           , TKSM_JUTAKU.ITAKUAITESHOPCD
           , TKSM_JUTAKU.ITAKUAITETOKUISAKICD
           , TKSM_JUTAKU.DLVRYKBN     -- 配達区分 
           , TKSM_JUTAKU.DLVRYTNSHACD -- 配達担当者コード 
           , TKSM_JUTAKU.DLVRYCMPNYCD -- 配達会社コード 
        INTO wItakuApkihoncd
           , wItakuShopcd
           , wItakuTokuisakicd
           , vRecordS04T001T.DLVRYKBN     -- 配達区分 
           , vRecordS04T001T.DLVRYTNSHACD -- 配達担当者コード 
           , vRecordS04T001T.DLVRYCMPNYCD -- 配達会社コード 
        FROM S02M013T TKSM_JUTAKU -- 得意先マスタ（受託側）
       WHERE TKSM_JUTAKU.APKIHONCD   = wJutakuApkihoncd
         AND TKSM_JUTAKU.SHOPCD      = wJutakuShopcd
         AND TKSM_JUTAKU.TOKUISAKICD = wJutakuTokuisakicd
         AND TKSM_JUTAKU.YUKOKBN     = '1'
      ;
      /* 相手先コードチェック（受託） */
      IF wItakuApkihoncd IS NULL OR wItakuShopcd IS NULL OR wItakuTokuisakicd IS NULL THEN 
         RAISE_APPLICATION_ERROR(-20104, '受託の相手先コードが設定されていません。');
      END IF; 
      /* 委受託相手先コードチェック */ 
      IF wItakuApkihoncd      <> vRecordS04T001T.APKIHONCD
         OR wItakuShopcd      <> vRecordS04T001T.SHOPCD
         OR wItakuTokuisakicd <> vRecordS04T001T.TOKUISAKICD
         THEN 
         RAISE_APPLICATION_ERROR(-20105, '委託、受託の相手先コードが異なります。');
      END IF; 
      /* 得意先マスタ存在チェック（受託） */
      EXCEPTION
        WHEN NO_DATA_FOUND THEN 
          RAISE_APPLICATION_ERROR(-20103, '受託の得意先マスタが存在しません。'); 
    END; 
    
    /* 受託側得意先情報設定 */
    vRecordS04T001T.APKIHONCD   := wJutakuApkihoncd;   -- ＡＰ基本コード
    vRecordS04T001T.SHOPCD      := wJutakuShopcd;      -- ショップコード
    vRecordS04T001T.TOKUISAKICD := wJutakuTokuisakicd; -- 得意先コード
    SELECT TJST.AZUKARIBTLN1N2RITTORUHNS -- 預かりボトル１２Ｌ本数
         , TJST.AZUKARIBTLN8RITTORUHNS   -- 預かりボトル８Ｌ本数
         -- 最終受注日
         , ( CASE WHEN vRecordS04T001T.ACPTDD > TJST.SAISHUACPTDD
             THEN vRecordS04T001T.ACPTDD -- 今回受注日
             ELSE TJST.SAISHUACPTDD END  -- 最終受注日
           ) AS SAISHUACPTDD
         , SHPM.ZAIKOKANRIKBN -- 在庫管理区分
      INTO vRecordS04T001T.AZUKARIBTLN1N2RITTORUHNS -- 預かりボトル１２Ｌ本数
         , vRecordS04T001T.AZUKARIBTLN8RITTORUHNS   -- 預かりボトル８Ｌ本数
         , oSaishuacptdd  -- 最終受注日
         , oZaikokanrikbn -- 在庫管理区分
      FROM S02M050T TJST            -- 得意先実績テーブル（受託側）
           INNER JOIN S02M046T SHPM -- ショップマスタ（受託側）
        ON ( SHPM.APKIHONCD   = TJST.APKIHONCD
             AND SHPM.SHOPCD  = TJST.SHOPCD
             AND SHPM.YUKOKBN = '1'
           )
     WHERE TJST.APKIHONCD   = wJutakuApkihoncd
       AND TJST.SHOPCD      = wJutakuShopcd
       AND TJST.TOKUISAKICD = wJutakuTokuisakicd
       AND TJST.YUKOKBN     = '1'
     FOR UPDATE OF TJST.TOKUISAKICD 
    ;
    vRecordS04T001T.TAXKEISANKBN := '2'; -- 税計算区分：得意先が受託（卸対象）の場合は外税固定
    S99_CHECK_LOG('C0804B01.setJutakuTK', wItakuApkihoncd, wItakuApkihoncd, '1', 'E', '1', 'XXX', 0, vRecordS04T001T.APKIHONCD, vRecordS04T001T.SHOPCD, NULL, NULL, 'C0804B01' , '受託側得意先情報設定[終了] ' );
  END setJutakuTokuisaki;
  
  /*****************************************************************************/
  /* プロシージャ名：明細行処理前の受注伝票設定                                     */
  /*             ：1.受託情報設定                                               */
  /*             ：2.テーブル共通項目                                            */
  /*             ：3.伝票番号                                                   */
  /*             ：4.その他明細共有項目                                          */
  /* 引数           ：iApkihoncd          ＡＰ基本コード                         */
  /*                ：iShopcd             ショップコード                         */
  /*                ：iAcptdnpyno         受注伝票番号                           */
  /*                ：iEdano              枝番号                                */
  /*                ：iMode               処理モード                            */
  /* 引数(OUT)      ：oZaikokanrikbn      在庫管理区分                           */
  /*                ：oSaishuacptdd        最終受注日                           */
  /*****************************************************************************/
  PROCEDURE setDnpyBeforeMeisai ( iApkihoncd      IN VARCHAR2 -- ＡＰ基本コード
                                , iShopcd         IN VARCHAR2 -- ショップコード
                                , iAcptdnpyno     IN VARCHAR2 -- 受注伝票番号
                                , iEdano          IN VARCHAR2 -- 枝番号
                                , iMode           IN VARCHAR2 -- 処理モード
                                , oZaikokanrikbn OUT VARCHAR2 -- 在庫管理区分
                                , oSaishuacptdd  OUT VARCHAR2 -- 最終受注日
  ) IS
  /* 変数宣言 */
  wUnyoYmd                  VARCHAR2(8)  := NULL;       -- 運用日付
  BEGIN
    S99_CHECK_LOG('C0804B01.setDnpyBfr', iApkihoncd, iShopcd, '1', 'S', '1', 'XXX', 0, NULL, NULL, NULL, NULL, 'C0804B01' , '明細行処理前の受注伝票設定[開始] ' );

    /* 1.受託情報設定 */
    -- 受託側得意先情報設定
    setJutakuTokuisaki( oZaikokanrikbn, oSaishuacptdd );
    -- 受注担当者の取得（設定番号：'212'）
    vRecordS04T001T.ACPTTNSHACD := getMojisetteiatai( vRecordS04T001T.APKIHONCD, vRecordS04T001T.SHOPCD, '212', NULL, NULL, NULL, '受注配達データ連携担当者' );
    vRecordS04T001T.ACPTTNSHAAPKIHONCD := vRecordS04T001T.APKIHONCD; -- 受注担当者ＡＰ基本コード
    vRecordS04T001T.ACPTTNSHASHOPCD := vRecordS04T001T.SHOPCD;	   -- 受注担当者ショップコード
    
    /* 2.テーブル共通項目 */
    SetUserInfo(vRecordS04T001T.APKIHONCD, vRecordS04T001T.SHOPCD, vRecordS04T001T.ACPTTNSHACD, vRecordS04T001T.KSHNIPADRSE, 'C0804B01');
    
    /* 3.伝票番号 */
    IF iMode = '0' THEN -- 新規の場合、採番する
-- 本線対応変更 start ABI
--       S01SAIBAN.getSlipNoJuchu( vRecordS04T001T.APKIHONCD, vRecordS04T001T.SHOPCD, 1, vRecordS04T001T.ACPTDNPYNO, vRecordS04T001T.ACPTDNPYNO );
       Z1804B01.getSlipNoJuchu( vRecordS04T001T.APKIHONCD, vRecordS04T001T.SHOPCD, 1, vRecordS04T001T.ACPTDNPYNO, vRecordS04T001T.ACPTDNPYNO );
-- 本線対応変更 end ABI
       vRecordS04T001T.EDANO := 0;
    ELSE -- 新規以外の場合、元伝票を取得する
       vRecordS04T001T.ACPTDNPYNO := vRecordS04T001T.ITAKUAITEACPTDNPYNO; -- 受注伝票番号
       vRecordS04T001T.EDANO      := vRecordS04T001T.ITAKUAITEEDANO;      -- 枝番号
    END IF;
    
    /* 4.その他明細共有情報設定 */
    vRecordS04T001T.DATSKSMOTOKBN := DATSKSMOTOKBN; -- データ作成元区分：委託
    -- 委託側情報を相手項目にセット
    vRecordS04T001T.ITAKUAITEAPKIHONCD  := iApkihoncd;  -- 委託相手ＡＰ基本コード
    vRecordS04T001T.ITAKUAITESHOPCD     := iShopcd;     -- 委託相手ショップコード
    vRecordS04T001T.ITAKUAITEACPTDNPYNO := iAcptdnpyno; -- 委託相手受注伝票番号
    vRecordS04T001T.ITAKUAITEEDANO      := iEdano;      -- 委託相手枝番号

    S99_CHECK_LOG('C0804B01.setDnpyBfr', iApkihoncd, iShopcd, '1', 'E', '1', 'XXX', 0, vRecordS04T001T.KOSHAAPKIHONCD, vRecordS04T001T.KOSHASHOPCD, vRecordS04T001T.KOSHACD, vRecordS04T001T.KSHNIPADRSE, 'C0804B01' , '明細行処理前の受注伝票設定[終了] ' );
    
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20017, SQLERRM || '明細行処理前の受注伝票設定時エラーが発生しました。');
  END setDnpyBeforeMeisai;
  
  /*****************************************************************************/
  /* プロシージャ名：配布物企画得意先マスタ更新                                     */
  /* 引数             ：iApkihoncd         ＡＰ基本コード                        */
  /*                  ：iShopcd            ショップコード                        */
  /*                  ：iHaifubutsucd      配布物コード                          */
  /*                  ：header_rec         受注配達伝票データ                    */
  /*                  ：iHaifutrgtsts      配布対象ステータス                    */
  /*****************************************************************************/
  PROCEDURE updateHaifubutsuTKM( iApkihoncd    IN VARCHAR2 -- ＡＰ基本コード
                               , iShopcd       IN VARCHAR2 -- ショップコード
                               , iHaifubutsucd IN VARCHAR2 -- 配布物コード
                               , header_rec    IN S04T001T%rowtype -- 受注配達伝票データ
                               , iHaifutrgtsts IN VARCHAR2 -- 配布対象ステータス
  ) IS 
  BEGIN 
    UPDATE S02M033T HTTM
       SET HTTM.HAIFUTRGTSTS = iHaifutrgtsts -- 配布対象ステータス：キャンセル（'1'：未受注）；更新（'2'：受注済）
         , HTTM.ACPTDNPYNO       = DECODE(iHaifutrgtsts, '2', header_rec.ACPTDNPYNO, '') -- 受注伝票番号
         , HTTM.EDANO            = DECODE(iHaifutrgtsts, '2', header_rec.EDANO, '')      -- 枝番号
         , HTTM.ACPTDNPYMEISAINO = DECODE(iHaifutrgtsts, '2', vRecordS04T002T.ACPTDNPYMEISAINO, '') -- 受注伝票明細番号
         , HTTM.ACPTTORIKOMIDD   = DECODE(iHaifutrgtsts, '2', header_rec.ACPTDD, '')     -- 受注取込日：受注ステータス
         /* 更新者 */
         , HTTM.KOSHAAPKIHONCD = vRecordS04T001T.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
         , HTTM.KOSHASHOPCD    = vRecordS04T001T.KOSHASHOPCD    -- 更新者ショップコード
         , HTTM.KOSHACD        = vRecordS04T001T.KOSHACD        -- 更新者コード
         , HTTM.KSHNSYSTMYMD   = vRecordS04T001T.KSHNSYSTMYMD   -- 更新システム日付
         , HTTM.KSHNSYSTMJIKAN = vRecordS04T001T.KSHNSYSTMJIKAN -- 更新システム時間
         , HTTM.KSHNUNYOYMD    = vRecordS04T001T.KSHNUNYOYMD    -- 更新運用日付
         , HTTM.KSHNIPADRSE    = vRecordS04T001T.KSHNIPADRSE    -- 更新ＩＰアドレス
         , HTTM.KSHNPGMID      = vRecordS04T001T.KSHNPGMID      -- 更新プログラムＩＤ
     WHERE HTTM.APKIHONCD    = iApkihoncd 
       AND HTTM.SHOPCD       = iShopcd 
       AND HTTM.HAIFUBUTSUCD = iHaifubutsucd
       AND HTTM.TOKUISAKICD  = header_rec.TOKUISAKICD
    ;  
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20018, SQLERRM || '配布物企画得意先マスタ更新時エラーが発生しました。');
  END updateHaifubutsuTKM;
  
  /*****************************************************************************/
  /* プロシージャ名：在庫引当テーブル更新                                           */
  /* 引数             ：header_rec         受注配達伝票データ                     */
  /*                  ：iSuryo             引当在庫変更数量                      */
  /*****************************************************************************/
  PROCEDURE updateZaiko( header_rec IN S04T001T%rowtype -- 受注配達伝票データ
                       , iSuryo     IN NUMBER 
  ) IS 
  BEGIN
    UPDATE S13T004T ZKHT
       SET ZKHT.HIKIATESUMIZAIKOKAZU = ZKHT.HIKIATESUMIZAIKOKAZU + iSuryo -- 引当済在庫数
         /* 更新者 */
         , ZKHT.KOSHAAPKIHONCD = vRecordS04T001T.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
         , ZKHT.KOSHASHOPCD    = vRecordS04T001T.KOSHASHOPCD    -- 更新者ショップコード
         , ZKHT.KOSHACD        = vRecordS04T001T.KOSHACD        -- 更新者コード
         , ZKHT.KSHNSYSTMYMD   = vRecordS04T001T.KSHNSYSTMYMD   -- 更新システム日付
         , ZKHT.KSHNSYSTMJIKAN = vRecordS04T001T.KSHNSYSTMJIKAN -- 更新システム時間
         , ZKHT.KSHNUNYOYMD    = vRecordS04T001T.KSHNUNYOYMD    -- 更新運用日付
         , ZKHT.KSHNIPADRSE    = vRecordS04T001T.KSHNIPADRSE    -- 更新ＩＰアドレス
         , ZKHT.KSHNPGMID      = vRecordS04T001T.KSHNPGMID      -- 更新プログラムＩＤ
     WHERE ZKHT.APKIHONCD     = header_rec.ACPTHIKIATESAKIAPKIHONCD
       AND ZKHT.SHOPCD        = header_rec.ACPTHIKIATESAKISHOPCD
       AND ZKHT.DLVRYKYOTENCD = header_rec.ACPTHIKIATESAKIKYOTENCD
       AND ZKHT.SHNCD         = vRecordS04T002T.SHNCD
    ;
    IF SQL%ROWCOUNT = 0 THEN 
       INSERT INTO S13T004T ( 
              APKIHONCD     -- ＡＰ基本コード
            , SHOPCD        -- ショップコード
            , DLVRYKYOTENCD -- 配達拠点コード
            , SHNCD         -- 商品コード
            , HIKIATESUMIZAIKOKAZU -- 引当済在庫数
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
              header_rec.ACPTHIKIATESAKIAPKIHONCD -- 受注引当先ＡＰ基本コード
            , header_rec.ACPTHIKIATESAKISHOPCD    -- 受注引当先ショップコード
            , header_rec.ACPTHIKIATESAKIKYOTENCD -- 受注引当先拠点コード
            , vRecordS04T002T.SHNCD         -- 商品コード
            , iSuryo -- 引当済在庫数
            /* テーブル共通項目 */
            , '1' -- 有効区分
            , vRecordS04T001T.KOSHAAPKIHONCD -- 登録者ＡＰ基本コード
            , vRecordS04T001T.KOSHASHOPCD    -- 登録者ショップコード
            , vRecordS04T001T.KOSHACD        -- 登録者コード
            , vRecordS04T001T.KSHNSYSTMYMD   -- 登録システム日付
            , vRecordS04T001T.KSHNSYSTMJIKAN -- 登録システム時間
            , vRecordS04T001T.KSHNUNYOYMD    -- 登録運用日付
            , vRecordS04T001T.KSHNIPADRSE    -- 登録ＩＰアドレス
            , vRecordS04T001T.KSHNPGMID      -- 登録プログラムＩＤ
            , vRecordS04T001T.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
            , vRecordS04T001T.KOSHASHOPCD    -- 更新者ショップコード
            , vRecordS04T001T.KOSHACD        -- 更新者コード
            , vRecordS04T001T.KSHNSYSTMYMD   -- 更新システム日付
            , vRecordS04T001T.KSHNSYSTMJIKAN -- 更新システム時間
            , vRecordS04T001T.KSHNUNYOYMD    -- 更新運用日付
            , vRecordS04T001T.KSHNIPADRSE    -- 更新ＩＰアドレス
            , vRecordS04T001T.KSHNPGMID      -- 更新プログラムＩＤ
       );
    END IF;
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20019, SQLERRM || '在庫引当テーブル更新時エラーが発生しました。');
  END updateZaiko;
  
  /*****************************************************************************/
  /* プロシージャ名：配達拠点受注アラームテーブル更新                                */
  /* 引数             ：header_rec        受注配達伝票データ                      */
  /*                  ：iAcptsuryo        受注数量                              */
  /*                  ：iUnyoymd          運用日付                              */
  /*****************************************************************************/
  PROCEDURE updateKyotenAlarm( header_rec  IN S04T001T%rowtype -- 受注配達伝票データ
                             , iAcptsuryo  IN NUMBER  -- 受注数量
                             , iUnyoymd    IN VARCHAR2  -- 運用日付
  ) IS
  /* 変数宣言 */
  wCount                 NUMBER(2);          -- アラームデータ存在チェック用
  wYmd                   S04T005T.YMD%TYPE;  -- 年月日
  wYobi                  S04T005T.YOBI%TYPE; -- 曜日
  wAramusetteiAcptsuryo  S04T005T.ARAMUSETTEIACPTSURYO%TYPE; -- アラーム設定受注数量
  BEGIN
    -- アラームデータが存在しない場合：新規登録
    SELECT COUNT(*)
      INTO wCount
      FROM S04T005T DKJA
     WHERE DKJA.APKIHONCD     = header_rec.DLVRYKANKATSUAPKIHONCD
       AND DKJA.SHOPCD        = header_rec.DLVRYKANKATSUSHOPCD
       AND DKJA.DLVRYKYOTENCD = header_rec.DLVRYKYOTENCD
       AND DKJA.SHNCD         = vRecordS04T002T.SHNCD
       AND DKJA.RENBAN        = 1
    ;
    IF wCount = 0 THEN 
       FOR wRenban IN 1..7 LOOP
           -- 年月日
           wYmd  := TO_CHAR(TO_DATE(iUnyoymd, 'YYYYMMDD') + (wRenban - 1), 'YYYYMMDD');
           -- 曜日
           wYobi := TO_NUMBER(TO_CHAR(TO_DATE(wYmd, 'YYYYMMDD'), 'd')) - 1;
           -- アラーム受注設定数量取得
           BEGIN 
             SELECT DECODE( wYobi
                          , 0, DKAM.ARAMUSETTEIATAISUND
                          , 1, DKAM.ARAMUSETTEIATAIMOND
                          , 2, DKAM.ARAMUSETTEIATAITUED
                          , 3, DKAM.ARAMUSETTEIATAIWEDD
                          , 4, DKAM.ARAMUSETTEIATAITHED
                          , 5, DKAM.ARAMUSETTEIATAIFRID
                          , 6, DKAM.ARAMUSETTEIATAISATD, 0) AS SETTEISURYO
               INTO wAramusetteiAcptsuryo
               FROM S02M039T DKAM -- 配達拠点アラーム設定マスタ
              WHERE DKAM.APKIHONCD = header_rec.DLVRYKANKATSUAPKIHONCD
                AND DKAM.SHOPCD    = header_rec.DLVRYKANKATSUSHOPCD
                AND DKAM.DLVRYKYOTENCD = header_rec.DLVRYKYOTENCD
                AND DKAM.SHNCD      =  vRecordS04T002T.SHNCD
             ;
             EXCEPTION 
               WHEN NO_DATA_FOUND THEN 
                 wAramusetteiAcptsuryo := 0; 
           END;
           INSERT INTO S04T005T (
                  APKIHONCD     -- 配達管轄ＡＰ基本コード
                , SHOPCD        -- 配達管轄ショップコード
                , DLVRYKYOTENCD -- 配達拠点コード
                , SHNCD         -- 商品コード
                , RENBAN -- 連番
                , YMD    -- 日付
                , YOBI   -- 曜日
                , ARAMUSETTEIACPTSURYO -- アラーム設定受注数量
                , ACPTKAZU             -- 受注数
                /* テーブル共通項目 */
                , YUKOKBN -- 有効区分
                , TOSHAAPKIHONCD -- 登録者ＡＰ基本コード
                , TOSHASHOPCD    -- 登録者ショップコード
                , TOSHACD        -- 登録者コード
                , INPTSYSTMYMD   -- 登録システム日付
                , INPTSYSTMJIKAN -- 登録システム時間
                , INPTUNYOYMD    -- 登録運用日付
                , INPTIPADRSE    -- 登録ＩＰアドレス
                , INPTPGMID      -- 登録プログラムＩＤ
                , KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
                , KOSHASHOPCD    -- 更新者ショップコード
                , KOSHACD        -- 更新者コード
                , KSHNSYSTMYMD   -- 更新システム日付
                , KSHNSYSTMJIKAN -- 更新システム時間
                , KSHNUNYOYMD    -- 更新運用日付
                , KSHNIPADRSE    -- 更新ＩＰアドレス
                , KSHNPGMID      -- 更新プログラムＩＤ
           )
           VALUES ( 
                  header_rec.DLVRYKANKATSUAPKIHONCD -- 配達管轄ＡＰ基本コード
                , header_rec.DLVRYKANKATSUSHOPCD    -- 配達管轄ショップコード
                , header_rec.DLVRYKYOTENCD          -- 配達拠点コード
                , vRecordS04T002T.SHNCD             -- 商品コード
                , wRenban -- 連番
                , wYmd    -- 日付
                , wYobi   -- 曜日
                , wAramusetteiAcptsuryo -- アラーム設定受注数量
                , 0 -- 受注数（初期値）
                /* テーブル共通項目 */
                , '1' -- 有効区分
                , vRecordS04T001T.KOSHAAPKIHONCD -- 登録者ＡＰ基本コード
                , vRecordS04T001T.KOSHASHOPCD    -- 登録者ショップコード
                , vRecordS04T001T.KOSHACD        -- 登録者コード
                , vRecordS04T001T.KSHNSYSTMYMD   -- 登録システム日付
                , vRecordS04T001T.KSHNSYSTMJIKAN -- 登録システム時間
                , vRecordS04T001T.KSHNUNYOYMD    -- 登録運用日付
                , vRecordS04T001T.KSHNIPADRSE    -- 登録ＩＰアドレス
                , vRecordS04T001T.KSHNPGMID      -- 登録プログラムＩＤ
                , vRecordS04T001T.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
                , vRecordS04T001T.KOSHASHOPCD    -- 更新者ショップコード
                , vRecordS04T001T.KOSHACD        -- 更新者コード
                , vRecordS04T001T.KSHNSYSTMYMD   -- 更新システム日付
                , vRecordS04T001T.KSHNSYSTMJIKAN -- 更新システム時間
                , vRecordS04T001T.KSHNUNYOYMD    -- 更新運用日付
                , vRecordS04T001T.KSHNIPADRSE    -- 更新ＩＰアドレス
                , vRecordS04T001T.KSHNPGMID      -- 更新プログラムＩＤ
           );
       END LOOP;
    END IF;
    
    UPDATE S04T005T DKJA
       SET DKJA.ACPTKAZU = DKJA.ACPTKAZU + iAcptsuryo -- 受注数
         /* 更新者 */
         , DKJA.KOSHAAPKIHONCD = vRecordS04T001T.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
         , DKJA.KOSHASHOPCD    = vRecordS04T001T.KOSHASHOPCD    -- 更新者ショップコード
         , DKJA.KOSHACD        = vRecordS04T001T.KOSHACD        -- 更新者コード
         , DKJA.KSHNSYSTMYMD   = vRecordS04T001T.KSHNSYSTMYMD   -- 更新システム日付
         , DKJA.KSHNSYSTMJIKAN = vRecordS04T001T.KSHNSYSTMJIKAN -- 更新システム時間
         , DKJA.KSHNUNYOYMD    = vRecordS04T001T.KSHNUNYOYMD    -- 更新運用日付
         , DKJA.KSHNIPADRSE    = vRecordS04T001T.KSHNIPADRSE    -- 更新ＩＰアドレス
         , DKJA.KSHNPGMID      = vRecordS04T001T.KSHNPGMID      -- 更新プログラムＩＤ
     WHERE DKJA.APKIHONCD     = header_rec.DLVRYKANKATSUAPKIHONCD
       AND DKJA.SHOPCD        = header_rec.DLVRYKANKATSUSHOPCD
       AND DKJA.DLVRYKYOTENCD = header_rec.DLVRYKYOTENCD
       AND DKJA.SHNCD         = vRecordS04T002T.SHNCD
       AND DKJA.YMD           = header_rec.DLVRYYTIDD
    ;
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20020, SQLERRM || '配達拠点受注アラームテーブル更新時エラーが発生しました。');
  END updateKyotenAlarm;
  
  /*****************************************************************************/
  /* プロシージャ名：担当者受注アラームテーブル更新                                  */
  /* 引数             ：header_rec        受注配達伝票データ                      */
  /*                  ：iAcptcnt          受注件数                              */
  /*                  ：iUnyoymd          運用日付                              */
  /*****************************************************************************/
  PROCEDURE updateTnshaAlarm( header_rec  IN S04T001T%rowtype -- 受注配達伝票データ
                            , iAcptcnt    IN NUMBER   -- 受注数量
                            , iUnyoymd    IN VARCHAR2 -- 運用日付
  ) IS
  /* 変数宣言 */
  wCount                 NUMBER(2) := 0;     -- アラームデータ存在チェック用
  wYmd                   S04T006T.YMD%TYPE;  -- 年月日
  wYobi                  S04T006T.YOBI%TYPE; -- 曜日
  wAramusetteiAcptcnt    S04T006T.ARAMUSETTEIACPTCNT%TYPE; -- アラーム設定受注件数
  BEGIN
    -- アラームデータが存在しない場合：新規登録
    SELECT COUNT(*)
      INTO wCount
      FROM S04T006T TNJA
     WHERE TNJA.APKIHONCD     = header_rec.DLVRYKANKATSUAPKIHONCD
       AND TNJA.SHOPCD        = header_rec.DLVRYKANKATSUSHOPCD
       AND TNJA.DLVRYKYOTENCD = '00'
       AND TNJA.TNSHACD       = header_rec.DLVRYTNSHACD
       AND TNJA.RENBAN        = 1
    ;
    IF wCount = 0 THEN 
       FOR wRenban IN 1..7 LOOP
           -- 年月日
           wYmd  := TO_CHAR(TO_DATE(iUnyoymd, 'YYYYMMDD') + (wRenban - 1), 'YYYYMMDD');
           -- 曜日
           wYobi := TO_NUMBER(TO_CHAR(TO_DATE(wYmd, 'YYYYMMDD'), 'd')) - 1;
           -- アラーム受注設定数量取得
           SELECT DECODE( wYobi
                          , 0, TNSM.ARAMUSETTEIATAISUND
                          , 1, TNSM.ARAMUSETTEIATAIMOND
                          , 2, TNSM.ARAMUSETTEIATAITUED
                          , 3, TNSM.ARAMUSETTEIATAIWEDD
                          , 4, TNSM.ARAMUSETTEIATAITHED
                          , 5, TNSM.ARAMUSETTEIATAIFRID
                          , 6, TNSM.ARAMUSETTEIATAISATD, 0) AS SETTEICNT
             INTO wAramusetteiAcptcnt
             FROM S02M003T TNSM -- 担当者マスタ
            WHERE TNSM.APKIHONCD = header_rec.DLVRYKANKATSUAPKIHONCD
              AND TNSM.SHOPCD    = header_rec.DLVRYKANKATSUSHOPCD
              AND TNSM.TNSHACD   = header_rec.DLVRYTNSHACD
           ;
           
           INSERT INTO S04T006T (
                  APKIHONCD     -- 配達管轄ＡＰ基本コード
                , SHOPCD        -- 配達管轄ショップコード
                , DLVRYKYOTENCD -- 配達拠点コード
                , TNSHACD       -- 配達担当者コード
                , RENBAN -- 連番
                , YMD    -- 日付
                , YOBI   -- 曜日
                , ARAMUSETTEIACPTCNT -- アラーム設定受注件数
                , ACPTCNT            -- 受注件数
                /* テーブル共通項目 */
                , YUKOKBN -- 有効区分
                , TOSHAAPKIHONCD -- 登録者ＡＰ基本コード
                , TOSHASHOPCD    -- 登録者ショップコード
                , TOSHACD        -- 登録者コード
                , INPTSYSTMYMD   -- 登録システム日付
                , INPTSYSTMJIKAN -- 登録システム時間
                , INPTUNYOYMD    -- 登録運用日付
                , INPTIPADRSE    -- 登録ＩＰアドレス
                , INPTPGMID      -- 登録プログラムＩＤ
                , KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
                , KOSHASHOPCD    -- 更新者ショップコード
                , KOSHACD        -- 更新者コード
                , KSHNSYSTMYMD   -- 更新システム日付
                , KSHNSYSTMJIKAN -- 更新システム時間
                , KSHNUNYOYMD    -- 更新運用日付
                , KSHNIPADRSE    -- 更新ＩＰアドレス
                , KSHNPGMID      -- 更新プログラムＩＤ
           )
           VALUES ( 
                  header_rec.DLVRYKANKATSUAPKIHONCD -- 配達管轄ＡＰ基本コード
                , header_rec.DLVRYKANKATSUSHOPCD    -- 配達管轄ショップコード
                , '00' -- 配達拠点コード
                , header_rec.DLVRYTNSHACD           -- 配達担当者コード
                , wRenban -- 連番
                , wYmd    -- 日付
                , wYobi   -- 曜日
                , wAramusetteiAcptcnt -- アラーム設定受注件数
                , 0 -- 受注件数（初期値）
                /* テーブル共通項目 */
                , '1' -- 有効区分
                , vRecordS04T001T.KOSHAAPKIHONCD -- 登録者ＡＰ基本コード
                , vRecordS04T001T.KOSHASHOPCD    -- 登録者ショップコード
                , vRecordS04T001T.KOSHACD        -- 登録者コード
                , vRecordS04T001T.KSHNSYSTMYMD   -- 登録システム日付
                , vRecordS04T001T.KSHNSYSTMJIKAN -- 登録システム時間
                , vRecordS04T001T.KSHNUNYOYMD    -- 登録運用日付
                , vRecordS04T001T.KSHNIPADRSE    -- 登録ＩＰアドレス
                , vRecordS04T001T.KSHNPGMID      -- 登録プログラムＩＤ
                , vRecordS04T001T.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
                , vRecordS04T001T.KOSHASHOPCD    -- 更新者ショップコード
                , vRecordS04T001T.KOSHACD        -- 更新者コード
                , vRecordS04T001T.KSHNSYSTMYMD   -- 更新システム日付
                , vRecordS04T001T.KSHNSYSTMJIKAN -- 更新システム時間
                , vRecordS04T001T.KSHNUNYOYMD    -- 更新運用日付
                , vRecordS04T001T.KSHNIPADRSE    -- 更新ＩＰアドレス
                , vRecordS04T001T.KSHNPGMID      -- 更新プログラムＩＤ
           );
       END LOOP;
    END IF;
    
    -- 担当者受注アラームテーブル更新
    UPDATE S04T006T TNJA
       SET TNJA.ACPTCNT = TNJA.ACPTCNT + iAcptcnt -- 受注件数
         /* 更新者 */
         , TNJA.KOSHAAPKIHONCD = vRecordS04T001T.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
         , TNJA.KOSHASHOPCD    = vRecordS04T001T.KOSHASHOPCD    -- 更新者ショップコード
         , TNJA.KOSHACD        = vRecordS04T001T.KOSHACD        -- 更新者コード
         , TNJA.KSHNSYSTMYMD   = vRecordS04T001T.KSHNSYSTMYMD   -- 更新システム日付
         , TNJA.KSHNSYSTMJIKAN = vRecordS04T001T.KSHNSYSTMJIKAN -- 更新システム時間
         , TNJA.KSHNUNYOYMD    = vRecordS04T001T.KSHNUNYOYMD    -- 更新運用日付
         , TNJA.KSHNIPADRSE    = vRecordS04T001T.KSHNIPADRSE    -- 更新ＩＰアドレス
         , TNJA.KSHNPGMID      = vRecordS04T001T.KSHNPGMID      -- 更新プログラムＩＤ
     WHERE TNJA.APKIHONCD     = header_rec.DLVRYKANKATSUAPKIHONCD
       AND TNJA.SHOPCD        = header_rec.DLVRYKANKATSUSHOPCD
       AND TNJA.DLVRYKYOTENCD = '00'
       AND TNJA.TNSHACD       = header_rec.DLVRYTNSHACD
       AND TNJA.YMD           = header_rec.DLVRYYTIDD
    ;

    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20023, SQLERRM || '担当者受注アラームテーブル更新時エラーが発生しました。');
  END updateTnshaAlarm;
  
  /*****************************************************************************/
  /* プロシージャ名：配達エリア受注アラームテーブル更新                               */
  /* 引数             ：header_rec        受注配達伝票データ                      */
  /*                  ：iAcptcnt          受注件数                              */
  /*                  ：iUnyoymd          運用日付                              */
  /*****************************************************************************/
  PROCEDURE updateAreaAlarm( header_rec  IN S04T001T%rowtype -- 受注配達伝票データ
                           , iAcptcnt    IN NUMBER   -- 受注数量
                           , iUnyoymd    IN VARCHAR2 -- 運用日付
  ) IS
  /* 変数宣言 */
  wCount                 NUMBER(2);          -- アラームデータ存在チェック用
  wYmd                   S04T007T.YMD%TYPE;  -- 年月日
  wYobi                  S04T007T.YOBI%TYPE; -- 曜日
  wAramusetteiAcptcnt    S04T007T.ARAMUSETTEIACPTCNT%TYPE; -- アラーム設定受注件数
  BEGIN
    -- アラームデータが存在しない場合：新規登録
    SELECT COUNT(*)
      INTO wCount
      FROM S04T007T DAJA
     WHERE DAJA.APKIHONCD     = header_rec.DLVRYKANKATSUAPKIHONCD
       AND DAJA.SHOPCD        = header_rec.DLVRYKANKATSUSHOPCD
       AND DAJA.DLVRYKYOTENCD = header_rec.DLVRYKYOTENCD
       AND DAJA.DLVRYAREACD   = header_rec.DLVRYAREACD
       AND DAJA.RENBAN        = 1
    ;
    IF wCount = 0 THEN 
       FOR wRenban IN 1..7 LOOP
           -- 年月日
           wYmd  := TO_CHAR(TO_DATE(iUnyoymd, 'YYYYMMDD') + (wRenban - 1), 'YYYYMMDD');
           -- 曜日
           wYobi := TO_NUMBER(TO_CHAR(TO_DATE(wYmd, 'YYYYMMDD'), 'd')) - 1;
           -- アラーム受注設定数量取得
           SELECT DECODE( wYobi
                          , 0, DARM.SUNDACPTLMTCNT
                          , 1, DARM.MONDACPTLMTCNT
                          , 2, DARM.TUEDACPTLMTCNT
                          , 3, DARM.WEDDACPTLMTCNT
                          , 4, DARM.THEDACPTLMTCNT
                          , 5, DARM.FRIDACPTLMTCNT
                          , 6, DARM.SATDACPTLMTCNT, 0) AS SETTEICNT
             INTO wAramusetteiAcptcnt
             FROM S02M018T DARM -- 配達エリアマスタ
            WHERE DARM.APKIHONCD = header_rec.DLVRYKANKATSUAPKIHONCD
              AND DARM.SHOPCD    = header_rec.DLVRYKANKATSUSHOPCD
              AND DARM.DLVRYKYOTENCD = header_rec.DLVRYKYOTENCD
              AND DARM.DLVRYAREACD   = header_rec.DLVRYAREACD 
              AND wYmd BETWEEN DARM.TEKIYOSTRTDD AND DARM.TEKIYOSHRYDD
           ;
           
           INSERT INTO S04T007T (
                  APKIHONCD     -- 配達管轄ＡＰ基本コード
                , SHOPCD        -- 配達管轄ショップコード
                , DLVRYKYOTENCD -- 配達拠点コード
                , DLVRYAREACD   -- 配達エリアコード
                , RENBAN -- 連番
                , YMD    -- 日付
                , YOBI   -- 曜日
                , ARAMUSETTEIACPTCNT -- アラーム設定受注件数
                , ACPTCNT            -- 受注件数
                /* テーブル共通項目 */
                , YUKOKBN -- 有効区分
                , TOSHAAPKIHONCD -- 登録者ＡＰ基本コード
                , TOSHASHOPCD    -- 登録者ショップコード
                , TOSHACD        -- 登録者コード
                , INPTSYSTMYMD   -- 登録システム日付
                , INPTSYSTMJIKAN -- 登録システム時間
                , INPTUNYOYMD    -- 登録運用日付
                , INPTIPADRSE    -- 登録ＩＰアドレス
                , INPTPGMID      -- 登録プログラムＩＤ
                , KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
                , KOSHASHOPCD    -- 更新者ショップコード
                , KOSHACD        -- 更新者コード
                , KSHNSYSTMYMD   -- 更新システム日付
                , KSHNSYSTMJIKAN -- 更新システム時間
                , KSHNUNYOYMD    -- 更新運用日付
                , KSHNIPADRSE    -- 更新ＩＰアドレス
                , KSHNPGMID      -- 更新プログラムＩＤ
           )
           VALUES ( 
                  header_rec.DLVRYKANKATSUAPKIHONCD -- 配達管轄ＡＰ基本コード
                , header_rec.DLVRYKANKATSUSHOPCD    -- 配達管轄ショップコード
                , header_rec.DLVRYKYOTENCD          -- 配達拠点コード
                , header_rec.DLVRYAREACD            -- 配達担当者コード
                , wRenban -- 連番
                , wYmd    -- 日付
                , wYobi   -- 曜日
                , wAramusetteiAcptcnt -- アラーム設定受注件数
                , 0 -- 受注件数（初期値）
                /* テーブル共通項目 */
                , '1' -- 有効区分
                , vRecordS04T001T.KOSHAAPKIHONCD -- 登録者ＡＰ基本コード
                , vRecordS04T001T.KOSHASHOPCD    -- 登録者ショップコード
                , vRecordS04T001T.KOSHACD        -- 登録者コード
                , vRecordS04T001T.KSHNSYSTMYMD   -- 登録システム日付
                , vRecordS04T001T.KSHNSYSTMJIKAN -- 登録システム時間
                , vRecordS04T001T.KSHNUNYOYMD    -- 登録運用日付
                , vRecordS04T001T.KSHNIPADRSE    -- 登録ＩＰアドレス
                , vRecordS04T001T.KSHNPGMID      -- 登録プログラムＩＤ
                , vRecordS04T001T.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
                , vRecordS04T001T.KOSHASHOPCD    -- 更新者ショップコード
                , vRecordS04T001T.KOSHACD        -- 更新者コード
                , vRecordS04T001T.KSHNSYSTMYMD   -- 更新システム日付
                , vRecordS04T001T.KSHNSYSTMJIKAN -- 更新システム時間
                , vRecordS04T001T.KSHNUNYOYMD    -- 更新運用日付
                , vRecordS04T001T.KSHNIPADRSE    -- 更新ＩＰアドレス
                , vRecordS04T001T.KSHNPGMID      -- 更新プログラムＩＤ
           );
       END LOOP;
    END IF;
    
    -- 配達エリア受注アラームテーブル更新
    UPDATE S04T007T DAJA
       SET DAJA.ACPTCNT = DAJA.ACPTCNT + iAcptcnt -- 受注件数
         /* 更新者 */
         , DAJA.KOSHAAPKIHONCD = vRecordS04T001T.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
         , DAJA.KOSHASHOPCD    = vRecordS04T001T.KOSHASHOPCD    -- 更新者ショップコード
         , DAJA.KOSHACD        = vRecordS04T001T.KOSHACD        -- 更新者コード
         , DAJA.KSHNSYSTMYMD   = vRecordS04T001T.KSHNSYSTMYMD   -- 更新システム日付
         , DAJA.KSHNSYSTMJIKAN = vRecordS04T001T.KSHNSYSTMJIKAN -- 更新システム時間
         , DAJA.KSHNUNYOYMD    = vRecordS04T001T.KSHNUNYOYMD    -- 更新運用日付
         , DAJA.KSHNIPADRSE    = vRecordS04T001T.KSHNIPADRSE    -- 更新ＩＰアドレス
         , DAJA.KSHNPGMID      = vRecordS04T001T.KSHNPGMID      -- 更新プログラムＩＤ
     WHERE DAJA.APKIHONCD     = header_rec.DLVRYKANKATSUAPKIHONCD
       AND DAJA.SHOPCD        = header_rec.DLVRYKANKATSUSHOPCD
       AND DAJA.DLVRYKYOTENCD = header_rec.DLVRYKYOTENCD
       AND DAJA.DLVRYAREACD   = header_rec.DLVRYAREACD
       AND DAJA.YMD           = header_rec.DLVRYYTIDD
    ;
    
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20024, SQLERRM || '配達エリア受注アラームテーブル更新時エラーが発生しました。');
  END updateAreaAlarm;
  
  /*****************************************************************************/
  /* プロシージャ名：得意先更新処理                                                */
  /* 引数           ：header_rec             受注配達テーブル                     */
  /*                ：iCancelFlg             キャンセルフラグ                    */
  /*                ：iMode                  処理モード                         */
  /*                ：iSaishuapctdd          最終受注日                         */
  /*****************************************************************************/
  PROCEDURE updateTokuisaki( header_rec       IN S04T001T%rowtype -- 受注配達テーブル
                           , iCancel          IN BOOLEAN  -- キャンセルフラグ
                           , iMode            IN VARCHAR2 -- 処理モード
                           , iSaishuapctdd    IN VARCHAR2 -- 最終受注日
  ) IS 
  /* 変数宣言 */
  wKyuminkbn               VARCHAR(1)  := NULL; -- 休眠区分
  wKyuminChangeFLG         VARCHAR(1)  := NULL; -- 休眠変更フラグ
  BEGIN 
    S99_CHECK_LOG('C0804B01.updateTKS', vRecordS04T001T.APKIHONCD, vRecordS04T001T.SHOPCD, '1', 'S', '1', 'XXX', 0, vRecordS04T001T.KOSHAAPKIHONCD, vRecordS04T001T.KOSHASHOPCD, vRecordS04T001T.KOSHACD, vRecordS04T001T.KSHNIPADRSE, 'C0804B01' , '得意先更新処理[開始] ' ); 
    /* 得意先実績テーブル */
    IF iCancel THEN 
       UPDATE S02M050T TKJT
-- インボイス対応変更 start ABI
          --SET TKJT.KEIJOMAEACPTKIN = NVL(TKJT.KEIJOMAEACPTKIN - header_rec.ACPTKIN 
          --                                                    - header_rec.ACPTSHOHITAXKIN, 0) -- 計上前受注金額
          SET TKJT.KEIJOMAEACPTKIN = (CASE WHEN header_rec.ACPTTHDAIBIKIFLG = '1' OR header_rec.SINGLDNPYFLG = '1' THEN -- 代引きの場合
                                        NVL(TKJT.KEIJOMAEACPTKIN - header_rec.ACPTKIN - header_rec.ACPTSHOHITAXKIN, 0)
                                      ELSE
                                        CASE WHEN header_rec.DLVRYYTIDD < vInvoicshikoymd THEN -- インボイス施行日前の場合
                                          NVL(TKJT.KEIJOMAEACPTKIN - header_rec.ACPTKIN - header_rec.ACPTSHOHITAXKIN, 0)
                                        ELSE
                                          NVL(TKJT.KEIJOMAEACPTKIN - header_rec.ACPTKIN, 0)
                                        END
                                      END) -- 計上前受注金額
-- インボイス対応変更 end ABI
            , TKJT.SAISHUACPTDD = NVL( iSaishuapctdd, TKJT.SAISHUACPTDD ) -- 最終受注日
            /* 更新者：更新なし */
        WHERE TKJT.APKIHONCD = header_rec.APKIHONCD
          AND TKJT.SHOPCD    = header_rec.SHOPCD
          AND TKJT.TOKUISAKICD = header_rec.TOKUISAKICD
          AND TKJT.YUKOKBN = '1'
       ;
      S99_CHECK_LOG('C0804B01.updateTKS', header_rec.APKIHONCD, header_rec.SHOPCD, '1', 'U', '1', 'S02M050T', SQL%ROWCOUNT, vRecordS04T001T.KOSHAAPKIHONCD, vRecordS04T001T.KOSHASHOPCD, vRecordS04T001T.KOSHACD, vRecordS04T001T.KSHNIPADRSE, 'C0804B01' , '得意先実績テーブル更新 ' );   
    ELSE 
       UPDATE S02M050T TKJT 
-- インボイス対応変更 start ABI
          --SET TKJT.KEIJOMAEACPTKIN = NVL( TKJT.KEIJOMAEACPTKIN + header_rec.ACPTKIN 
          --                                                 + header_rec.ACPTSHOHITAXKIN, 0) -- 計上前受注金額
          SET TKJT.KEIJOMAEACPTKIN = (CASE WHEN header_rec.ACPTTHDAIBIKIFLG = '1' OR header_rec.SINGLDNPYFLG = '1' THEN -- 代引きの場合
                                        NVL( TKJT.KEIJOMAEACPTKIN + header_rec.ACPTKIN + header_rec.ACPTSHOHITAXKIN, 0)
                                      ELSE
                                        CASE WHEN header_rec.DLVRYYTIDD < vInvoicshikoymd THEN -- インボイス施行日前の場合
                                          NVL( TKJT.KEIJOMAEACPTKIN + header_rec.ACPTKIN + header_rec.ACPTSHOHITAXKIN, 0)
                                        ELSE
                                          NVL( TKJT.KEIJOMAEACPTKIN + header_rec.ACPTKIN, 0)
                                        END
                                      END) -- 計上前受注金額
-- インボイス対応変更 end ABI
            , TKJT.SAISHUACPTDD = iSaishuapctdd -- 最終受注日
            /* 更新者：更新なし */
        WHERE TKJT.APKIHONCD = header_rec.APKIHONCD
          AND TKJT.SHOPCD    = header_rec.SHOPCD
          AND TKJT.TOKUISAKICD = header_rec.TOKUISAKICD
          AND TKJT.YUKOKBN = '1'
       ;
      S99_CHECK_LOG('C0804B01.updateTKS', header_rec.APKIHONCD, header_rec.SHOPCD, '1', 'U', '1', 'S02M050T', SQL%ROWCOUNT, vRecordS04T001T.KOSHAAPKIHONCD, vRecordS04T001T.KOSHASHOPCD, vRecordS04T001T.KOSHACD, vRecordS04T001T.KSHNIPADRSE, 'C0804B01' , '得意先実績テーブル更新 ' );   
    END IF;
    /* 得意先マスタ */
    IF iMode IN ( '2', '3' ) THEN -- キャンセルの場合
       -- 休眠区分判定 
       IF judgeKyumin(header_rec.APKIHONCD, header_rec.SHOPCD, header_rec.TOKUISAKICD, header_rec.ACPTDNPYNO ) THEN 
          -- 更新
          UPDATE S02M013T TKSM
             SET TKSM.KYUMINKBN = '1' -- 休眠区分
               /* 更新者：更新なし */
           WHERE TKSM.APKIHONCD = header_rec.APKIHONCD
             AND TKSM.SHOPCD    = header_rec.SHOPCD
             AND TKSM.TOKUISAKICD = header_rec.TOKUISAKICD
             AND TKSM.YUKOKBN = '1'
          ;
          S99_CHECK_LOG('C0804B01.updateTKS', header_rec.APKIHONCD, header_rec.SHOPCD, '1', 'U', '1', 'S02M013T', SQL%ROWCOUNT, vRecordS04T001T.KOSHAAPKIHONCD, vRecordS04T001T.KOSHASHOPCD, vRecordS04T001T.KOSHACD, vRecordS04T001T.KSHNIPADRSE, 'C0804B01' , '得意先マスタ休眠区分更新 ' );   
       END IF; 
    END IF; 
    IF NOT iCancel THEN           -- 登録・更新の場合
       -- 更新
       UPDATE S02M013T TKSM
          SET TKSM.KYUMINKBN = '0' -- 休眠区分
            /* 更新者：更新なし */
        WHERE TKSM.APKIHONCD = header_rec.APKIHONCD
          AND TKSM.SHOPCD    = header_rec.SHOPCD
          AND TKSM.TOKUISAKICD = header_rec.TOKUISAKICD
          AND TKSM.KYUMINKBN = '1'
          AND TKSM.YUKOKBN = '1'
       ;
       S99_CHECK_LOG('C0804B01.updateTKS', header_rec.APKIHONCD, header_rec.SHOPCD, '1', 'U', '1', 'S02M013T', SQL%ROWCOUNT, vRecordS04T001T.KOSHAAPKIHONCD, vRecordS04T001T.KOSHASHOPCD, vRecordS04T001T.KOSHACD, vRecordS04T001T.KSHNIPADRSE, 'C0804B01' , '得意先マスタ休眠区分更新 ' );   
    END IF; 
    
    S99_CHECK_LOG('C0804B01.updateTKS', vRecordS04T001T.APKIHONCD, vRecordS04T001T.SHOPCD, '1', 'E', '1', 'XXX', 0, vRecordS04T001T.KOSHAAPKIHONCD, vRecordS04T001T.KOSHASHOPCD, vRecordS04T001T.KOSHACD, vRecordS04T001T.KSHNIPADRSE, 'C0804B01' , '得意先更新処理[終了] ' );   
  END updateTokuisaki; 
  
  /*****************************************************************************/
  /* プロシージャ名：受託配達テーブルキャンセル更新                                  */
  /* 引数             ：なし                                                    */
  /*****************************************************************************/
  PROCEDURE updateCancelDnpy IS
  /* 変数宣言 */
  BEGIN
    UPDATE S04T001T JHTT
       SET JHTT.DLVRYTRGTFLG = vRecordS04T001T.DLVRYTRGTFLG -- 配達対象フラグ
         , JHTT.DNPYSTS = vRecordS04T001T.DNPYSTS -- 伝票ステータス
         , JHTT.ACPTSTS = vRecordS04T001T.ACPTSTS -- 受注ステータス
         , JHTT.YUKOKBN = vRecordS04T001T.YUKOKBN -- 有効区分
         /* 更新者 */
         , JHTT.KOSHAAPKIHONCD = vRecordS04T001T.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
         , JHTT.KOSHASHOPCD    = vRecordS04T001T.KOSHASHOPCD    -- 更新者ショップコード
         , JHTT.KOSHACD        = vRecordS04T001T.KOSHACD        -- 更新者コード
         , JHTT.KSHNSYSTMYMD   = vRecordS04T001T.KSHNSYSTMYMD   -- 更新システム日付
         , JHTT.KSHNSYSTMJIKAN = vRecordS04T001T.KSHNSYSTMJIKAN -- 更新システム時間
         , JHTT.KSHNUNYOYMD    = vRecordS04T001T.KSHNUNYOYMD    -- 更新運用日付
         , JHTT.KSHNIPADRSE    = vRecordS04T001T.KSHNIPADRSE    -- 更新ＩＰアドレス
         , JHTT.KSHNPGMID      = vRecordS04T001T.KSHNPGMID      -- 更新プログラムＩＤ
     WHERE JHTT.APKIHONCD  = vRecordS04T001T.APKIHONCD
       AND JHTT.SHOPCD     = vRecordS04T001T.SHOPCD
       AND JHTT.ACPTDNPYNO = vRecordS04T001T.ACPTDNPYNO
       AND JHTT.EDANO      = vRecordS04T001T.EDANO
    ;
    
    S99_CHECK_LOG('C0804B01.updateCCL', vRecordS04T001T.APKIHONCD, vRecordS04T001T.SHOPCD, '1', 'U', '1', 'S04T001T', SQL%ROWCOUNT, vRecordS04T001T.KOSHAAPKIHONCD, vRecordS04T001T.KOSHASHOPCD, vRecordS04T001T.KOSHACD, vRecordS04T001T.KSHNIPADRSE, 'C0804B01' , '受託配達テーブルキャンセル更新 ' );   
  END updateCancelDnpy;
  
  /*****************************************************************************/
  /* プロシージャ名：受注キャンセル処理                                            */
  /* 引数           ：iMode                   処理モード                         */
  /*                ：iZaikokanrikbn          在庫管理区分                       */
  /*****************************************************************************/
  PROCEDURE processCancel( iMode          IN VARCHAR2 -- 処理モード
                         , iZaikokanrikbn IN VARCHAR2 -- 在庫管理区分
  ) IS
  /* 変数宣言 */
  wHaifubutsuzokusei       VARCHAR2(1) := NULL; -- 配布物属性
  wZaikokanritrgtflg       VARCHAR2(1) := NULL; -- 在庫管理対象フラグ
  wSaishuacptdd            VARCHAR2(8) := NULL; -- 最終受注日
  /* CURSOR： 受託側のヘッダ情報 */
  CURSOR SELECT_HEADER IS
    SELECT *
      FROM S04T001T JHTT
     WHERE JHTT.APKIHONCD   = vRecordS04T001T.APKIHONCD  -- ＡＰ基本コード
       AND JHTT.SHOPCD      = vRecordS04T001T.SHOPCD     -- ショップコード
       AND JHTT.ACPTDNPYNO  = vRecordS04T001T.ACPTDNPYNO -- 受注伝票番号
       AND JHTT.EDANO       = vRecordS04T001T.EDANO      -- 枝番号
       AND JHTT.YUKOKBN     = '1'         -- 有効区分
    FOR UPDATE 
    ;
  /* CURSOR： 受託側の明細情報 */
  CURSOR SELECT_MEISAI IS
    SELECT *
      FROM S04T002T JHMT
     WHERE JHMT.APKIHONCD   = vRecordS04T001T.APKIHONCD  -- ＡＰ基本コード
       AND JHMT.SHOPCD      = vRecordS04T001T.SHOPCD     -- ショップコード
       AND JHMT.ACPTDNPYNO  = vRecordS04T001T.ACPTDNPYNO -- 受注伝票番号
       AND JHMT.EDANO       = vRecordS04T001T.EDANO      -- 枝番号
       AND JHMT.YUKOKBN     = '1'         -- 有効区分
       AND JHMT.MEISAIKBN IN ('0','1')    -- 明細区分：商品・配布物
     ORDER BY JHMT.ACPTDNPYMEISAINO, JHMT.ACPTDNPYMEISAIEDANO
    FOR UPDATE 
    ;
  BEGIN
    S99_CHECK_LOG('C0804B01.processCCL', vRecordS04T001T.APKIHONCD, vRecordS04T001T.SHOPCD, '1', 'S', '1', 'XXX', 0, vRecordS04T001T.KOSHAAPKIHONCD, vRecordS04T001T.KOSHASHOPCD, vRecordS04T001T.KOSHACD, vRecordS04T001T.KSHNIPADRSE, 'C0804B01' , '受注キャンセル[開始] ' );   
    FOR cancel_rec IN SELECT_HEADER LOOP 
    OPEN SELECT_MEISAI; 
      LOOP FETCH SELECT_MEISAI INTO vRecordS04T002T;
        EXIT WHEN SELECT_MEISAI%NOTFOUND; 
        
        /* 配布物企画対象得意先マスタ */
        IF vRecordS04T002T.MEISAIKBN = '1' THEN 
           BEGIN 
             SELECT HFBM.HAIFUBUTSUZOKUSEI -- 配布物属性
               INTO wHaifubutsuzokusei 
               FROM S02M035T HFBM          -- 配布物マスタ
              WHERE HFBM.APKIHONCD    = vRecordS04T002T.HAIFUBUTSUAPKIHONCD
                AND HFBM.SHOPCD       = vRecordS04T002T.HAIFUBUTSUSHOPCD
                AND HFBM.HAIFUBUTSUCD = vRecordS04T002T.HAIFUBUTSUCD
                AND HFBM.YUKOKBN = '1'
             ; 
             IF wHaifubutsuzokusei = '0' THEN -- 配布物属性：本部配布物 
                updateHaifubutsuTKM( vRecordS04T002T.HAIFUBUTSUAPKIHONCD
                                   , vRecordS04T002T.HAIFUBUTSUSHOPCD
                                   , vRecordS04T002T.HAIFUBUTSUCD
                                   , cancel_rec
                                   , '1' );   -- 未受注で配布物企画対象得意先マスタを更新
             END IF;
             EXCEPTION 
               WHEN NO_DATA_FOUND THEN 
                 NULL;
           END; 
        END IF;
        
        -- 明細区分：商品、単品区分：単品 
        IF vRecordS04T002T.MEISAIKBN = '0' AND vRecordS04T002T.TANPINMEISAIFLG = '1' THEN 
           SELECT SHMV.ZAIKOKANRITRGTFLG -- 在庫管理対象フラグ
             INTO wZaikokanritrgtflg
             FROM S02V001T SHMV -- 商品マスタビュー
            WHERE SHMV.APKIHONCD  = vRecordS04T002T.APKIHONCD
              AND SHMV.SHOPCD     = vRecordS04T002T.SHOPCD
              AND SHMV.SHNCD      = vRecordS04T002T.SHNCD
              AND SHMV.SHOPYUKOKBN = '1'
           ;
           /* 在庫引当テーブル */
           IF iZaikokanrikbn <> '0' AND wZaikokanritrgtflg <> '0' THEN  -- 在庫管理区分・在庫管理対象フラグ：ＯＮ
              updateZaiko(cancel_rec, (-1) * vRecordS04T002T.ACPTSURYO );
           END IF;
           /* 配達拠点受注アラームテーブル */
           IF  ( isRegular(vRecordS04T002T.SHNCD) OR isSlim(vRecordS04T002T.SHNCD) ) -- レギュラー／スリム商品
               AND cancel_rec.DLVRYKYOTENCD IS NOT NULL THEN                    -- 配達拠点設定あり
              updateKyotenAlarm( cancel_rec, (-1) * vRecordS04T002T.ACPTSURYO, vRecordS04T002T.KSHNUNYOYMD );
           END IF;
        END IF;
      END LOOP;
    CLOSE SELECT_MEISAI;
    
    /* アラームテーブル */
    IF cancel_rec.DLVRYKYOTENCD IS NOT NULL AND cancel_rec.DLVRYTNSHACD IS NOT NULL THEN -- 配達拠点・配達担当者設定あり
       updateTnshaAlarm( cancel_rec, -1, vRecordS04T001T.KSHNUNYOYMD ); -- 担当者受注アラームテーブル
    END IF;
    IF cancel_rec.DLVRYKYOTENCD IS NOT NULL AND cancel_rec.DLVRYAREACD IS NOT NULL THEN -- 配達拠点・配達エリア設定あり
       updateAreaAlarm( cancel_rec, -1, vRecordS04T001T.KSHNUNYOYMD );  -- 配達エリア受注アラームテーブル
    END IF;
    
    -- キャンセル・削除の場合
    IF iMode IN ( '2', '3' ) THEN 
       /* 受注配達テーブル */
       updateCancelDnpy;
       
       -- キャンセル後最終受注日の取得
       BEGIN 
         SELECT MAX(JHTT.ACPTDD) 
           INTO wSaishuacptdd 
           FROM S04T001T JHTT -- 受注配達テーブル
          WHERE JHTT.APKIHONCD   = cancel_rec.APKIHONCD
            AND JHTT.SHOPCD      = cancel_rec.SHOPCD
            AND JHTT.TOKUISAKICD = cancel_rec.TOKUISAKICD
            AND JHTT.ACPTDNPYNO <> cancel_rec.ACPTDNPYNO 
            AND JHTT.DNPYSTS IN ('02', '03', '11', '12') 
            AND JHTT.YUKOKBN = '1' 
         ;
         EXCEPTION 
           WHEN NO_DATA_FOUND THEN 
             NULL;
       END;
    END IF;
    
    /* 得意先実績テーブル・得意先マスタ */
    updateTokuisaki( cancel_rec, TRUE, iMode, wSaishuacptdd );
    
    END LOOP;
    
    S99_CHECK_LOG('C0804B01.processCCL', vRecordS04T001T.APKIHONCD, vRecordS04T001T.SHOPCD, '1', 'E', '1', 'XXX', 0, vRecordS04T001T.KOSHAAPKIHONCD, vRecordS04T001T.KOSHASHOPCD, vRecordS04T001T.KOSHACD, vRecordS04T001T.KSHNIPADRSE, 'C0804B01' , '受注キャンセル[終了] ' );   
  END processCancel;
  
  /*****************************************************************************/
  /* プロシージャ名：消費税設定                                                   */
  /* 引数             ：iTaxsetteikbn        税設定区分                          */
  /*****************************************************************************/
  PROCEDURE setTax(iTaxsetteikbn       IN VARCHAR2) IS
  /* 変数宣言 */
  wTaxkbn                    VARCHAR2(1)  := NULL; -- 消費税計算用税区分
  BEGIN
    -- 消費税計算用税区分の取得
    IF vRecordS04T002T.KEIJOKBN = '08' THEN -- 計上区分：経費
       wTaxkbn := '2';                    -- 非課税
    ELSE 
       wTaxkbn := vRecordS04T002T.TAXKBN; -- 商品税区分
    END IF;
    
    -- 消費税計算
-- インボイス対応削除 start ABI
--    IF wTaxkbn <> '5' THEN
-- インボイス対応変削除 end ABI

    IF vRecordS04T001T.TAXKEISANKBN = '2' THEN -- 税計算区分：外税
       -- 単価消費税金額
       vRecordS04T002T.ACPTUNTSHOHITAXKIN := 0;
       -- 消費税
-- 本線対応変更 start ABI
--       vRecordS04T002T.ACPTSHOHITAXKIN := S01SyohizeiService.SyohizeiKeisan('1', --単体計算フラグ（する）
       vRecordS04T002T.ACPTSHOHITAXKIN := Z1808B01.SyohizeiKeisan('1', --単体計算フラグ（する）
-- 本線対応変更 end ABI
                                                                  iTaxsetteikbn, --税設定区分
                                                                        wTaxkbn, --税区分
                                                   vRecordS04T001T.TAXKEISANKBN, --税計算区分
                                                     vRecordS04T001T.DLVRYYTIDD, --切り替え日：配達予定日
                                                      vRecordS04T001T.APKIHONCD, --AP基本コード
                                                         vRecordS04T001T.SHOPCD, --ショップコード
                                                                           NULL, --仕入先コード
                                                                           NULL, --数量
                                                       vRecordS04T002T.ACPTKIN); -- 金額
    ELSE                                       -- 税計算区分：内税
       -- 単価消費税金額
-- 本線対応変更 start ABI
--       vRecordS04T002T.ACPTUNTSHOHITAXKIN := S01SyohizeiService.SyohizeiKeisan('1', --単体計算フラグ（する）
       vRecordS04T002T.ACPTUNTSHOHITAXKIN := Z1808B01.SyohizeiKeisan('1', --単体計算フラグ（する）
-- 本線対応変更 end ABI
                                                                     iTaxsetteikbn, --税設定区分
                                                                           wTaxkbn, --税区分
                                                      vRecordS04T001T.TAXKEISANKBN, --税計算区分
                                                        vRecordS04T001T.DLVRYYTIDD, --切り替え日：配達予定日
                                                         vRecordS04T001T.APKIHONCD, --AP基本コード
                                                            vRecordS04T001T.SHOPCD, --ショップコード
                                                                              NULL, --仕入先コード
                                                                              NULL, --数量
                                                          vRecordS04T002T.ACPTUNT); --単価
       -- 消費税金額
       IF vRecordS04T002T.ACPTUNT IS NULL OR vRecordS04T002T.ACPTUNT = 0 THEN 
-- 本線対応変更 start ABI
--          vRecordS04T002T.ACPTSHOHITAXKIN := S01SyohizeiService.SyohizeiKeisan('1', --単体計算フラグ（する）
          vRecordS04T002T.ACPTSHOHITAXKIN := Z1808B01.SyohizeiKeisan('1', --単体計算フラグ（する）
-- 本線対応変更 end ABI
                                                                     iTaxsetteikbn, --税設定区分
                                                                           wTaxkbn, --税区分
                                                      vRecordS04T001T.TAXKEISANKBN, --税計算区分
                                                        vRecordS04T001T.DLVRYYTIDD, --切り替え日：配達予定日
                                                         vRecordS04T001T.APKIHONCD, --AP基本コード
                                                            vRecordS04T001T.SHOPCD, --ショップコード
                                                                              NULL, --仕入先コード
                                                                              NULL, --数量
                                                          vRecordS04T002T.ACPTKIN); -- 金額
       ELSE 
          vRecordS04T002T.ACPTSHOHITAXKIN := vRecordS04T002T.ACPTUNTSHOHITAXKIN * vRecordS04T002T.ACPTSURYO;
       END IF;
    END IF;
-- インボイス対応削除 start ABI
--    ELSE
--          S01SyohizeiService.SetSyohizeiKeisan(iTaxsetteikbn, --税設定区分
--                                vRecordS04T001T.TAXKEISANKBN, --税計算区分
--                                  vRecordS04T001T.DLVRYYTIDD, --切り替え日：配達予定日
--                                   vRecordS04T001T.APKIHONCD, --AP基本コード
--                                      vRecordS04T001T.SHOPCD, --ショップコード
--                                   vRecordS04T002T.ACPTSURYO, --数量
--                                       vRecordS04T002T.SHNCD, --商品コード
--                                    vRecordS04T001T.DLVRYKBN, --配達区分
--                                    vRecordS04T002T.KEIJOKBN, --計上区分
--                          vRecordS04T002T.ACPTUNTSHOHITAXKIN, --戻り値：単価消費税
--                            vRecordS04T002T.ACPTSHOHITAXKIN); --戻り値：消費税
--    END IF;
-- インボイス対応削除 end ABI
  END setTax;
  
  /*****************************************************************************/
  /* プロシージャ名：構成商品情報設定                                              */
  /* 引数                  ：iOya                親明細行                       */
  /*                       ：iTaxsetteikbn       税設定区分                     */
  /*****************************************************************************/
  PROCEDURE setKoseiShn( iOya          IN S04T002T%rowtype -- 親明細行
                       , iTaxsetteikbn VARCHAR2            -- 税設定区分
  ) IS
  /* 変数宣言 */
  wRecordS02M008T        S02M008T%rowtype  := NULL; -- 展開マスタ
  vUrikinhasukeisankbn        VARCHAR2(1)  := NULL;  -- 売上金額端数計算区分
 
  /* CURSOR:展開マスタから構成商品の取得 */
  CURSOR SELECT_KOSEI IS
    SELECT TNKM_JUTAKU.*
      FROM S02M008T TNKM_JUTAKU
           INNER JOIN S02M008T TNKM_ITAKU
        ON ( TNKM_ITAKU.APKIHONCD = vRecordS04T002T.ITAKUAITEAPKIHONCD -- ＡＰ基本コード（委託側）
             AND TNKM_ITAKU.STSHNCD   = vRecordS04T002T.STSHNCD            -- セット商品コード
             AND TNKM_ITAKU.RENBAN            = TNKM_JUTAKU.RENBAN            -- 連番
             AND TNKM_ITAKU.KOSEISHNAPKIHONCD = TNKM_JUTAKU.KOSEISHNAPKIHONCD -- 構成商品ＡＰ基本コード
             AND TNKM_ITAKU.KOSEISHNCD        = TNKM_JUTAKU.KOSEISHNCD        -- 構成商品コード
             AND TNKM_ITAKU.YUKOKBN = '1' -- 有効区分
           )
     WHERE TNKM_JUTAKU.APKIHONCD  = vRecordS04T002T.APKIHONCD -- ＡＰ基本コード
       AND TNKM_JUTAKU.STSHNCD    = vRecordS04T002T.STSHNCD   -- セット商品コード
       AND TNKM_JUTAKU.KOSEISHNCD = vRecordS04T002T.SHNCD     -- 構成商品コード
       AND TNKM_JUTAKU.RENBAN = vRecordS04T002T.ACPTDNPYMEISAIEDANO     -- 連番
       AND TNKM_JUTAKU.YUKOKBN    = '1' -- 有効区分
     ORDER BY TNKM_JUTAKU.RENBAN
    ;
  BEGIN

    -- ショップマスタ取得 
    SELECT
      SHPM.URIKINHASUKEISANKBN  -- 売上金額端数計算区分
    INTO vUrikinhasukeisankbn
    FROM
      S02M046T SHPM 
    WHERE
      SHPM.APKIHONCD = vRecordS04T002T.APKIHONCD 
      AND SHPM.SHOPCD = vRecordS04T002T.SHOPCD  
      AND SHPM.YUKOKBN = '1'
    ;

    -- 構成商品取得
    OPEN SELECT_KOSEI;
      LOOP FETCH SELECT_KOSEI INTO wRecordS02M008T;
        -- 構成チェック
        IF SELECT_KOSEI%ROWCOUNT = 0 THEN
           RAISE_APPLICATION_ERROR(-20013, '委託と受託のセット商品構成が同じではありません。');
        END IF;
        -- 構成商品処理完了
        EXIT WHEN SELECT_KOSEI%NOTFOUND;
        -- 複数件チェック
        IF SELECT_KOSEI%ROWCOUNT > 1 THEN
           RAISE_APPLICATION_ERROR(-20014, '同一セット商品に同じ構成商品が複数見つかりました。');
        END IF;
        
        vRecordS04T002T.ACPTSURYO := iOya.ACPTSURYO * wRecordS02M008T.SURYO; -- 数量
        
        -- 単価、金額、消費税
        IF iOya.KEIJOKBN IN ( '01', '02' ) THEN -- 有料（親計上区分：売上／キャンペーン）
           -- 単価
           
           -- 適用期間内の得意先別セット商品単価がある場合は単価にセットする
           BEGIN
                SELECT 
                    CASE 
                        WHEN vRecordS04T001T.DLVRYKBN = '3' THEN TSTM.MOCHIKAERIPRICE -- 持ち帰り価格
                        ELSE TSTM.DLVRYPRICE                                          -- 配達価格
                    END
                INTO 
                    vRecordS04T002T.ACPTUNT
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
                        AND TSTM.SHNCD                  = wRecordS02M008T.KOSEISHNCD 
                        AND TSTM.RENBAN                 = wRecordS02M008T.RENBAN     -- 連番
                        AND TSTM.YUKOKBN                = '1'
                    )
                WHERE 
                    TSSM.APKIHONCD   = vRecordS04T002T.APKIHONCD                                   -- ＡＰ基本コード
                AND TSSM.SHOPCD      = vRecordS04T002T.SHOPCD                                      -- ショップコード
                AND TSSM.STSHNCD     = wRecordS02M008T.STSHNCD                                     -- セット商品コード
                AND TSSM.TOKUISAKICD = vRecordS04T001T.TOKUISAKICD                                 -- 得意先コード
                AND vRecordS04T001T.DLVRYYTIDD BETWEEN TSSM.TEKIYOSTRTDD AND TSSM.TEKIYOSHRYDD     -- 配送予定日：適用期間内
                AND TSSM.YUKOKBN     = '1' 
                ;
           EXCEPTION 
             WHEN NO_DATA_FOUND THEN 
                vRecordS04T002T.ACPTUNT := NULL;
             WHEN TOO_MANY_ROWS THEN
                RAISE_APPLICATION_ERROR(-20015, '得意先別セット単価が複数見つかりました。');
           END;
           
           IF vRecordS04T002T.ACPTUNT IS NULL THEN
           
           vRecordS04T002T.ACPTUNT := ( CASE vRecordS04T001T.DLVRYKBN WHEN '3' -- 配達区分：持ち帰り
                                        THEN wRecordS02M008T.MOCHIKAERIPRICE   -- 持ち帰り価格
                                        ELSE wRecordS02M008T.DLVRYPRICE END ); -- 配達価格
           
           END IF;
           
           -- 金額
-- 本線対応変更 start ABI
--           vRecordS04T002T.ACPTKIN := S01SyohizeiService.roundValue(vRecordS04T002T.ACPTUNT * vRecordS04T002T.ACPTSURYO, vUrikinhasukeisankbn);
           vRecordS04T002T.ACPTKIN := Z1808B01.roundValue(vRecordS04T002T.ACPTUNT * vRecordS04T002T.ACPTSURYO, vUrikinhasukeisankbn);
-- 本線対応変更 end ABI
           -- 消費税
-- インボイス対応変更 start ABI
           -- 配達予定日がインボイス施行日前の場合、現行仕様通り
           IF vRecordS04T001T.DLVRYYTIDD < vInvoicshikoymd THEN
-- インボイス対応変更 end ABI
             IF iTaxsetteikbn = '1' AND (iOya.TAXKBN = '2' OR iOya.TAXKBN = '3') THEN -- 非課税（親税区分：非課税、不課税、子税設定区分：する）
               vRecordS04T002T.ACPTUNTSHOHITAXKIN := 0;
               vRecordS04T002T.ACPTSHOHITAXKIN    := 0;
             ELSE setTax( iTaxsetteikbn );
             END IF;
-- インボイス対応変更 start ABI
           -- インボイス施行日後の場合、消費税金額を計算しない
           ELSE
             vRecordS04T002T.ACPTUNTSHOHITAXKIN := 0;
             vRecordS04T002T.ACPTSHOHITAXKIN    := 0;
           END IF;
-- インボイス対応変更 end ABI
           
           -- 有料の場合のみ：展開マスタの計上区分で登録
           vRecordS04T002T.KEIJOKBN  := wRecordS02M008T.KEIJOKBN; 
        END IF;
        
      END LOOP;
    CLOSE SELECT_KOSEI;
  END setKoseiShn;
  
  /*****************************************************************************/
  /* プロシージャ名：明細行登録                                                   */
  /* 引数             ：なし                                                    */
  /*****************************************************************************/
  PROCEDURE insertMeisai IS
  BEGIN
    -- 基本情報設定
    vRecordS04T002T.TOSHAAPKIHONCD := vRecordS04T001T.KOSHAAPKIHONCD; -- 登録者ＡＰ基本コード
    vRecordS04T002T.TOSHASHOPCD    := vRecordS04T001T.KOSHASHOPCD;    -- 登録者ショップコード
    vRecordS04T002T.TOSHACD        := vRecordS04T001T.KOSHACD;        -- 登録者コード
    vRecordS04T002T.INPTSYSTMYMD   := vRecordS04T001T.KSHNSYSTMYMD;   -- 登録システム日付
    vRecordS04T002T.INPTSYSTMJIKAN := vRecordS04T001T.KSHNSYSTMJIKAN; -- 登録システム時間
    vRecordS04T002T.INPTUNYOYMD    := vRecordS04T001T.KSHNUNYOYMD;    -- 登録運用日付
    vRecordS04T002T.INPTIPADRSE    := vRecordS04T001T.KSHNIPADRSE;    -- 登録ＩＰアドレス
    vRecordS04T002T.INPTPGMID      := vRecordS04T001T.KSHNPGMID;      -- 登録プログラムＩＤ
    
    vRecordS04T002T.KOSHAAPKIHONCD := vRecordS04T001T.KOSHAAPKIHONCD; -- 更新者ＡＰ基本コード
    vRecordS04T002T.KOSHASHOPCD    := vRecordS04T001T.KOSHASHOPCD;    -- 更新者ショップコード
    vRecordS04T002T.KOSHACD        := vRecordS04T001T.KOSHACD;        -- 更新者コード
    vRecordS04T002T.KSHNSYSTMYMD   := vRecordS04T001T.KSHNSYSTMYMD;   -- 更新システム日付
    vRecordS04T002T.KSHNSYSTMJIKAN := vRecordS04T001T.KSHNSYSTMJIKAN; -- 更新システム時間
    vRecordS04T002T.KSHNUNYOYMD    := vRecordS04T001T.KSHNUNYOYMD;    -- 更新運用日付
    vRecordS04T002T.KSHNIPADRSE    := vRecordS04T001T.KSHNIPADRSE;    -- 更新ＩＰアドレス
    vRecordS04T002T.KSHNPGMID      := vRecordS04T001T.KSHNPGMID;      -- 更新プログラムＩＤ
    
    INSERT INTO S04T002T JHMT VALUES vRecordS04T002T;
  END insertMeisai;
  
  /*****************************************************************************/
  /* プロシージャ名：商品分類別集計                                                */
  /* 引数             ：iKeijokbn         親計上区分                             */
  /*****************************************************************************/
  PROCEDURE addAllByCtgry( iKeijokbn        IN VARCHAR2 -- 親計上区分
  ) IS
  /* 変数宣言 */
  wNotRegularSlim             BOOLEAN := TRUE; -- レギュラースリム以外フラグ
  wIsYuryo                    BOOLEAN := FALSE; -- 有料フラグ
  wIsWater                    BOOLEAN := FALSE; -- 製品水フラグ
  BEGIN
    -- 有償：親計上区分が売上／キャンペーン
    wIsYuryo := iKeijokbn IN ( '01', '02' );
    -- 製品水：商品大分類が製品水
    wIsWater := vRecordS04T002T.LCTGRYCD = vWatercd;
    /* 本数集計 */
    IF isRegular( vRecordS04T002T.SHNCD ) AND wIsWater THEN -- レギュラー
       wNotRegularSlim := FALSE;
       IF wIsYuryo THEN 
          vRecordS04T001T.DLVRYYTIRGRAYUSYOHNS := vRecordS04T001T.DLVRYYTIRGRAYUSYOHNS + vRecordS04T002T.ACPTSURYO; -- 配達予定レギュラー有償本数
       ELSE 
          vRecordS04T001T.DLVRYYTIRGRAMUSHOHNS := vRecordS04T001T.DLVRYYTIRGRAMUSHOHNS + vRecordS04T002T.ACPTSURYO; -- 配達予定レギュラー無償本数
       END IF;
    END IF; 
    IF isSlim( vRecordS04T002T.SHNCD ) AND wIsWater THEN    -- スリム
       wNotRegularSlim := FALSE;
       IF wIsYuryo THEN 
          vRecordS04T001T.DLVRYYTISRMYUSYOHNS  := vRecordS04T001T.DLVRYYTISRMYUSYOHNS + vRecordS04T002T.ACPTSURYO; -- 配達予定スリム有償本数
       ELSE 
          vRecordS04T001T.DLVRYYTISRMMUSHOHNS  := vRecordS04T001T.DLVRYYTISRMMUSHOHNS + vRecordS04T002T.ACPTSURYO; -- 配達予定スリム無償本数
       END IF; 
    END IF;
    IF wNotRegularSlim AND wIsWater THEN -- その他製品水
       IF wIsYuryo THEN 
          vRecordS04T001T.DLVRYYTISONOTAYUSYOHNS := vRecordS04T001T.DLVRYYTISONOTAYUSYOHNS + vRecordS04T002T.ACPTSURYO; -- 配達予定その他有償本数
       ELSE 
          vRecordS04T001T.DLVRYYTISONOTAMUSHOHNS := vRecordS04T001T.DLVRYYTISONOTAMUSHOHNS + vRecordS04T002T.ACPTSURYO; -- 配達予定その他無償本数
       END IF;
    END IF;
    /* 金額集計 */
    IF wIsWater THEN 
       vRecordS04T001T.DLVRYYTISEIHINMIZUURIKIN := vRecordS04T001T.DLVRYYTISEIHINMIZUURIKIN + vRecordS04T002T.ACPTKIN; -- 配達予定製品水売上金額
    ELSE 
       vRecordS04T001T.DLVRYYTISONOTAURIKIN     := vRecordS04T001T.DLVRYYTISONOTAURIKIN     + vRecordS04T002T.ACPTKIN; -- 配達予定その他売上金額
    END IF;
  END addAllByCtgry;
  
  /*****************************************************************************/
  /* プロシージャ名：消費税調整商品登録                                            */
  /* 引数                  ：iOya                親明細行                       */
  /*                       ：iKidTaxSum          子商品消費税合計                */
  /*                       ：iKidAiteTaxSum      子商品委託相手消費税合計         */
  /*                       ：iKidLastNo          子商品最終明細枝番号             */
  /*****************************************************************************/
  PROCEDURE insertTaxChosei( iOya             IN S04T002T%rowtype -- 親明細行
                           , iKidTaxSum       IN NUMBER           -- 子商品消費税合計
                           , iKidAiteTaxSum   IN NUMBER           -- 子商品委託相手消費税合計
                           , iKidLastNo       IN NUMBER           -- 子商品最終明細枝番号
  ) IS
  /* 変数宣言 */
  wTaxChosei             S04T002T%rowtype := iOya; -- 消費税調整商品明細行
  BEGIN
    S99_CHECK_LOG('C0804B01.insertTaxCS', vRecordS04T001T.APKIHONCD, vRecordS04T001T.SHOPCD, '1', 'S', '1', 'XXX', 0, vRecordS04T001T.KOSHAAPKIHONCD, vRecordS04T001T.KOSHASHOPCD, vRecordS04T001T.KOSHACD, vRecordS04T001T.KSHNIPADRSE, 'C0804B01' , '消費税調整商品登録[開始] ' );
    wTaxChosei := iOya;
    -- 消費税調整商品コード取得
    wTaxChosei.SHNCD := getMojisetteiatai( '000', '000', '166', NULL, NULL, NULL, '消費税調整商品コード');
    IF wTaxChosei.SHNCD IS NULL THEN 
       RAISE_APPLICATION_ERROR(-20016, '消費税調整商品コード　が設定されていません。');
    END IF; 
    -- 商品マスタビューからの商品情報を取得する
    SELECT SHMV.LCTGRYCD -- 大分類コード
         , SHMV.MCTGRYCD -- 中分類コード
         , SHMV.SCTGRYCD -- 小分類コード
         -- 標準価格：配達区分、受注日より設定
         , DECODE( vRecordS04T001T.DLVRYKBN,'3', -- 配達区分：持ち帰り
                   CASE WHEN ( SHMV.HYOJUNPRICEKIRIKAEDD IS NULL OR vRecordS04T001T.ACPTDD >= SHMV.HYOJUNPRICEKIRIKAEDD)
                   THEN SHMV.SHINMOCHIKAERIPRICE      -- 新持ち帰り価格
                   ELSE SHMV.GENZMOCHIKAERIPRICE END  -- 現持ち帰り価格
                   ,                             -- 配達区分：その他
                   CASE WHEN ( SHMV.HYOJUNPRICEKIRIKAEDD IS NULL OR vRecordS04T001T.ACPTDD >= SHMV.HYOJUNPRICEKIRIKAEDD )
                   THEN SHMV.SHINHYOJUNPRICE          -- 新標準価格
                   ELSE SHMV.GENZHYOJUNPRICE END      -- 現標準価格
           ) HYOJUNUNT
         -- 税区分
      INTO wTaxChosei.LCTGRYCD -- 大分類コード
         , wTaxChosei.MCTGRYCD -- 中分類コード
         , wTaxChosei.SCTGRYCD -- 小分類コード
         , wTaxChosei.HYOJUNUNT -- 標準価格
      FROM S02V001T SHMV
     WHERE SHMV.APKIHONCD   = wTaxChosei.APKIHONCD  -- ＡＰ基本コード
       AND SHMV.SHOPCD      = wTaxChosei.SHOPCD     -- セット商品コード
       AND SHMV.SHNCD       = wTaxChosei.SHNCD -- 消費税調整商品コード
       AND SHMV.SHOPYUKOKBN = '1' -- ショップ有効区分
    ;
    -- 消費税調整明細行を作る
    wTaxChosei.ACPTDNPYMEISAIEDANO     := iKidLastNo + 1; -- 受注伝票明細枝番号
    wTaxChosei.ITAKUAITEACPTDNPYMEISAIEDANO := iKidLastNo; -- 委託相手受注伝票明細枝番号
    wTaxChosei.KATASIKI  := NULL; -- 型式
    -- 受注情報
    wTaxChosei.ACPTSURYO := 1;          -- 受注数量
    wTaxChosei.ACPTUNT   := 0;          -- 受注単価
    wTaxChosei.ACPTKIN   := 0;          -- 受注金額
    wTaxChosei.ACPTUNTSHOHITAXKIN := 0; -- 受注単価消費税金額
    wTaxChosei.ACPTSHOHITAXKIN := wTaxChosei.ACPTSHOHITAXKIN - iKidTaxSum;     -- 受注消費税金額
    -- 委託会社
    wTaxChosei.ITAKUCMPNYACPTUNT   := 0;          -- 委託会社受注単価
    wTaxChosei.ITAKUCMPNYACPTUNTSHOHITAXKIN := 0; -- 委託会社受注単価消費税金額
    wTaxChosei.ITAKUCMPNYACPTSHOHITAXKIN := wTaxChosei.ITAKUCMPNYACPTSHOHITAXKIN - iKidAiteTaxSum; -- 委託会社受注消費税金額
    -- 納品情報
    wTaxChosei.NOHINSURYO          := 0; -- 納品数量
    wTaxChosei.NOHINUNT            := 0; -- 納品単価
    wTaxChosei.NOHINKIN            := 0; -- 納品金額
    wTaxChosei.NOHINUNTSHOHITAXKIN := 0; -- 納品単価消費税金額
    wTaxChosei.NOHINSHOHITAXKIN    := 0; -- 納品消費税金額
    -- 区分値
    wTaxChosei.YUSYOKBN          := '0'; -- 有償区分：指定なし
    wTaxChosei.MEISAIKBN         := '3'; -- 明細区分：消費税調整
    wTaxChosei.NYURYOKUMEISAIFLG := '0'; -- 入力明細フラグ：展開行
    wTaxChosei.TANPINMEISAIFLG   := '1'; -- 単品明細フラグ：単品
    -- 登録者
    wTaxChosei.TOSHAAPKIHONCD := vRecordS04T001T.KOSHAAPKIHONCD; -- 登録者ＡＰ基本コード
    wTaxChosei.TOSHASHOPCD    := vRecordS04T001T.KOSHASHOPCD;    -- 登録者ショップコード
    wTaxChosei.TOSHACD        := vRecordS04T001T.KOSHACD;        -- 登録者コード
    wTaxChosei.INPTSYSTMYMD   := vRecordS04T001T.KSHNSYSTMYMD;   -- 登録システム日付
    wTaxChosei.INPTSYSTMJIKAN := vRecordS04T001T.KSHNSYSTMJIKAN; -- 登録システム時間
    wTaxChosei.INPTUNYOYMD    := vRecordS04T001T.KSHNUNYOYMD;    -- 登録運用日付
    wTaxChosei.INPTIPADRSE    := vRecordS04T001T.KSHNIPADRSE;    -- 登録ＩＰアドレス
    wTaxChosei.INPTPGMID      := vRecordS04T001T.KSHNPGMID;      -- 登録プログラムＩＤ
    -- 更新者
    wTaxChosei.KOSHAAPKIHONCD := vRecordS04T001T.KOSHAAPKIHONCD; -- 更新者ＡＰ基本コード
    wTaxChosei.KOSHASHOPCD    := vRecordS04T001T.KOSHASHOPCD;    -- 更新者ショップコード
    wTaxChosei.KOSHACD        := vRecordS04T001T.KOSHACD;        -- 更新者コード
    wTaxChosei.KSHNSYSTMYMD   := vRecordS04T001T.KSHNSYSTMYMD;   -- 更新システム日付
    wTaxChosei.KSHNSYSTMJIKAN := vRecordS04T001T.KSHNSYSTMJIKAN; -- 更新システム時間
    wTaxChosei.KSHNUNYOYMD    := vRecordS04T001T.KSHNUNYOYMD;    -- 更新運用日付
    wTaxChosei.KSHNIPADRSE    := vRecordS04T001T.KSHNIPADRSE;    -- 更新ＩＰアドレス
    wTaxChosei.KSHNPGMID      := vRecordS04T001T.KSHNPGMID;      -- 更新プログラムＩＤ
    
    INSERT INTO S04T002T JHMT VALUES wTaxChosei;
    
    S99_CHECK_LOG('C0804B01.insertTaxCS', vRecordS04T001T.APKIHONCD, vRecordS04T001T.SHOPCD, '1', 'E', '1', 'XXX', 0, vRecordS04T001T.KOSHAAPKIHONCD, vRecordS04T001T.KOSHASHOPCD, vRecordS04T001T.KOSHACD, vRecordS04T001T.KSHNIPADRSE, 'C0804B01' , '消費税調整商品登録[終了] ' );
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20025, SQLERRM || '消費税調整商品登録時エラーが発生しました。');
  END insertTaxChosei;
  
  /*****************************************************************************/
  /* プロシージャ名：商品明細登録                                                 */
  /*             ：1.商品情報設定 - 入力行・展開行                                */
  /*             ：2.明細行登録                                                 */
  /*             ：3.商品本数、金額、サーバー合計                                  */
  /* 引数（IN）        ：iOya                親明細行                            */
  /* 引数（OUT）       ：oZaikokanritrgtflg  在庫管理対象フラグ                   */
  /*****************************************************************************/
  PROCEDURE insertShohin( iOya                IN S04T002T%rowtype -- 親明細行
                        , oZaikokanritrgtflg  OUT VARCHAR2        -- 在庫管理対象フラグ
  ) IS 
  /* 変数宣言 */
  -- 単価、消費税計算用
  wZaikount                NUMBER(10,2) := 0;          -- 在庫単価
  wHansokuunt              NUMBER(10,2) := 0;          -- 販促単価
  wPricekbn                VARCHAR2(1)  := NULL;       -- 価格区分
  wOpenUnt                 NUMBER(10,2) := 0;          -- オーブン単価
  wHansokuyusenkbn         VARCHAR2(1)  := NULL;       -- 販促優先区分
  wTaxsetteikbn            VARCHAR2(1)  := NULL;       -- 税設定区分
  vUrikinhasukeisankbn     VARCHAR2(1)  := NULL;       -- 売上金額端数計算区分
  vStkbn                   VARCHAR2(1)  := NULL;
  
  BEGIN

    -- ショップマスタ取得 
    SELECT
      SHPM.URIKINHASUKEISANKBN  -- 売上金額端数計算区分
    INTO vUrikinhasukeisankbn
    FROM
      S02M046T SHPM 
    WHERE
      SHPM.APKIHONCD = vRecordS04T002T.APKIHONCD 
      AND SHPM.SHOPCD = vRecordS04T002T.SHOPCD  
      AND SHPM.YUKOKBN = '1'
    ;

    /* 1.商品情報設定 */
    SELECT SHMV.LCTGRYCD -- 大分類コード
         , SHMV.MCTGRYCD -- 中分類コード
         , SHMV.SCTGRYCD -- 小分類コード
         -- 標準価格：配達区分、受注日より設定
         , DECODE( vRecordS04T001T.DLVRYKBN,'3', -- 配達区分：持ち帰り
                   CASE WHEN ( SHMV.HYOJUNPRICEKIRIKAEDD IS NULL OR vRecordS04T001T.DLVRYYTIDD >= SHMV.HYOJUNPRICEKIRIKAEDD) -- 配送予定日より設定
                   
                   THEN SHMV.SHINMOCHIKAERIPRICE      -- 新持ち帰り価格
                   ELSE SHMV.GENZMOCHIKAERIPRICE END  -- 現持ち帰り価格
                   ,                             -- 配達区分：その他
                   CASE WHEN ( SHMV.HYOJUNPRICEKIRIKAEDD IS NULL OR vRecordS04T001T.DLVRYYTIDD >= SHMV.HYOJUNPRICEKIRIKAEDD ) -- 配送予定日より設定
                   THEN SHMV.SHINHYOJUNPRICE          -- 新標準価格
                   ELSE SHMV.GENZHYOJUNPRICE END      -- 現標準価格
            ) HYOJUNUNT
         -- 在庫単価
         , SHMV.ZAIKOUNT
         -- 販促価格：配達区分より設定
         ,( CASE WHEN SHMV.HANSOKUKBN = '1' -- 販促区分：有り
                      AND vRecordS04T001T.DLVRYYTIDD BETWEEN SHMV.HANSOKUSTRTDD AND SHMV.HANSOKUSHRYDD -- 配送予定日：販促期間内
            THEN CASE WHEN vRecordS04T001T.DLVRYKBN = '3' -- 配達区分：持ち帰り
                      THEN SHMV.HANSOKUMOCHIKAERIPRICE -- 販促持ち帰り価格
                      ELSE SHMV.HANSOKUDLVRYPRICE END  -- 販促配達価格
            ELSE NULL END 
            ) HANSOKUUNT
         -- 価格区分
         ,( CASE WHEN ( SHMV.HYOJUNPRICEKIRIKAEDD IS NULL OR vRecordS04T001T.DLVRYYTIDD >= SHMV.HYOJUNPRICEKIRIKAEDD)
                 THEN SHMV.SHINPRICEKBN -- 新価格区分
                 ELSE SHMV.GENZPRICEKBN -- 現価格区分
            END ) PRICEKBN
         -- オーブン価格：配達区分、受注日より設定
         , DECODE( vRecordS04T001T.DLVRYKBN,'3', -- 配達区分：持ち帰り
                   CASE WHEN ( SHMV.OPNPRICEKIRIKAEDD IS NULL OR vRecordS04T001T.DLVRYYTIDD >= SHMV.OPNPRICEKIRIKAEDD) -- 配送予定日より設定
                   THEN SHMV.SHINOPNMOCHIKAERIPRICE      -- 新オーブン持ち帰り価格
                   ELSE SHMV.GENZOPNMOCHIKAERIPRICE END  -- 現オーブン持ち帰り価格
                   ,                             -- 配達区分：その他
                   CASE WHEN ( SHMV.OPNPRICEKIRIKAEDD IS NULL OR vRecordS04T001T.DLVRYYTIDD >= SHMV.OPNPRICEKIRIKAEDD ) -- 配送予定日より設定
                   THEN SHMV.SHINOPNPRICE          -- 新オーブン価格
                   ELSE SHMV.GENZOPNPRICE          -- 現オーブン価格
            END ) OPENUNT
         , SHMV.HANSOKUYUSENKBN -- 販促優先区分
         , SHMV.TAXSETTEIKBN    -- 税設定区分
         , SHMV.ZAIKOKANRITRGTFLG -- 在庫管理対象フラグ
         , SHMV.STKBN                -- セット区分
      INTO vRecordS04T002T.LCTGRYCD  -- 大分類コード
         , vRecordS04T002T.MCTGRYCD  -- 中分類コード
         , vRecordS04T002T.SCTGRYCD  -- 小分類コード
         , vRecordS04T002T.HYOJUNUNT -- 標準価格
         , wZaikount                 -- 在庫単価
         , wHansokuunt               -- 販促単価
         , wPricekbn                 -- 価格区分
         , wOpenUnt                  -- オーブン単価
         , wHansokuyusenkbn          -- 販促優先区分
         , wTaxsetteikbn             -- 税設定区分
         , oZaikokanritrgtflg        -- 在庫管理対象フラグ
         , vStkbn                    -- セット区分
      FROM S02V001T SHMV
     WHERE SHMV.APKIHONCD = vRecordS04T002T.APKIHONCD -- ＡＰ基本コード
       AND SHMV.SHOPCD    = vRecordS04T002T.SHOPCD    -- ショップコード
       AND SHMV.SHNCD     = vRecordS04T002T.SHNCD     -- 商品コード
       AND SHMV.SHOPYUKOKBN = '1' -- ショップ有効区分
    ;
    
    /*   入力行：単価、金額、消費税 */
    IF vRecordS04T002T.NYURYOKUMEISAIFLG = '1' THEN 
       -- 受注単価
       BEGIN 
         vRecordS04T002T.ACPTUNT := 0; -- 初期値
         -- 計上区分：経費；在庫単価
         IF vRecordS04T002T.KEIJOKBN = '08' THEN 
            vRecordS04T002T.ACPTUNT := wZaikount;
         END IF;
         -- 計上区分：売上／キャンペーン
         IF vRecordS04T002T.KEIJOKBN IN ( '01', '02' ) THEN 
            
            -- セット商品の場合
            IF vStkbn = '1' THEN
              BEGIN
                SELECT 
                    CASE 
                        WHEN vRecordS04T001T.DLVRYKBN = '3' THEN TSSM.MOCHIKAERIPRICE -- 持ち帰り価格
                        ELSE TSSM.DLVRYPRICE                                          -- 配達価格
                    END
                INTO 
                    vRecordS04T002T.ACPTUNT
                FROM 
                    S02M066T TSSM 
                    INNER JOIN S02M013T TKSM ON ( 
                            TKSM.APKIHONCD              = TSSM.APKIHONCD 
                        AND TKSM.SHOPCD                 = TSSM.SHOPCD 
                        AND TKSM.TOKUISAKICD            = TSSM.TOKUISAKICD 
                        AND TKSM.RKNKINDTOKUBETSUUNTFLG = '1'
                    )
                WHERE 
                    TSSM.APKIHONCD   = vRecordS04T002T.APKIHONCD                                   -- ＡＰ基本コード
                AND TSSM.SHOPCD      = vRecordS04T002T.SHOPCD                                      -- ショップコード
                AND TSSM.STSHNCD     = vRecordS04T002T.SHNCD                                       -- セット商品コード
                AND TSSM.TOKUISAKICD = vRecordS04T001T.TOKUISAKICD                                 -- 得意先コード
                AND vRecordS04T001T.DLVRYYTIDD BETWEEN TSSM.TEKIYOSTRTDD AND TSSM.TEKIYOSHRYDD     -- 配送予定日：販促期間内
                AND TSSM.YUKOKBN     = '1' 
                ;
                vRecordS04T002T.KEIJOKBN := '01'; -- 計上区分：売上
              EXCEPTION 
                WHEN NO_DATA_FOUND THEN 
                  vRecordS04T002T.ACPTUNT := NULL;
                WHEN TOO_MANY_ROWS THEN
                  RAISE_APPLICATION_ERROR(-20015, '得意先別セット単価が複数見つかりました。');
              END;
              
            ELSE
            
            -- 得意先別商品別単価
            BEGIN 
              SELECT ( CASE WHEN vRecordS04T001T.DLVRYKBN = '3'
                            THEN TSTM.MOCHIKAERIPRICE -- 持ち帰り価格
                            ELSE TSTM.DLVRYPRICE END  -- 配達価格
                     ) TOKUISAKIUNT
                INTO vRecordS04T002T.ACPTUNT
                FROM S02M009T TSTM 
                INNER JOIN S02M013T TKSM 
                ON ( TKSM.APKIHONCD = TSTM.APKIHONCD AND TKSM.SHOPCD = TSTM.SHOPCD 
                    AND TKSM.TOKUISAKICD = TSTM.TOKUISAKICD 
                    AND TKSM.RKNKINDTOKUBETSUUNTFLG = '1'
                   )
               WHERE TSTM.APKIHONCD = vRecordS04T002T.APKIHONCD -- ＡＰ基本コード
                 AND TSTM.SHOPCD    = vRecordS04T002T.SHOPCD    -- ショップコード
                 AND TSTM.SHNCD     = vRecordS04T002T.SHNCD     -- 商品コード
                 AND TSTM.TOKUISAKICD = vRecordS04T001T.TOKUISAKICD   -- 得意先コード
                 AND vRecordS04T001T.DLVRYYTIDD BETWEEN TSTM.TEKIYOSTRTDD -- 適用開始日
                                            AND TSTM.TEKIYOSHRYDD     -- 適用終了日
                 AND TSTM.YUKOKBN = '1' 
              ;
              vRecordS04T002T.KEIJOKBN := '01'; -- 計上区分：売上
              EXCEPTION 
                WHEN NO_DATA_FOUND THEN 
                     vRecordS04T002T.ACPTUNT := NULL;
                WHEN TOO_MANY_ROWS THEN
                  RAISE_APPLICATION_ERROR(-20015, '得意先別単価が複数見つかりました。');
            END; 
            
            END IF;

            -- 得意先別商品別単価がない場合
            IF vRecordS04T002T.ACPTUNT IS NULL THEN 
               -- 計上区分がキャンペーンの場合：販促単価／0
               IF vRecordS04T002T.KEIJOKBN = '02' THEN
                    vRecordS04T002T.ACPTUNT := NVL(wHansokuunt, 0); -- 受注単価：販促単価
               END IF;
               -- 計上区分が売上の場合：販促単価／ショップ単価
               IF vRecordS04T002T.KEIJOKBN = '01' THEN
                  vRecordS04T002T.ACPTUNT  := ( CASE WHEN wPricekbn = '1' THEN vRecordS04T002T.HYOJUNUNT 
                                                     ELSE wOpenUnt 
                                                END ); -- 受注単価：標準／オーブン単価
                  IF wHansokuunt IS NOT NULL AND wHansokuyusenkbn = '1' THEN -- 販促優先区分
                     vRecordS04T002T.ACPTUNT  := wHansokuunt; -- 受注単価：販促単価
                     vRecordS04T002T.KEIJOKBN := '02';        -- 計上区分：キャンペーン
                  END IF;
               END IF;
            END IF;
         END IF;
       END; 
       
       vRecordS04T002T.ACPTUNT := NVL(vRecordS04T002T.ACPTUNT, 0); 
-- 本線対応変更 start ABI
--       vRecordS04T002T.ACPTKIN := S01SyohizeiService.roundValue(vRecordS04T002T.ACPTUNT * vRecordS04T002T.ACPTSURYO, vUrikinhasukeisankbn); -- 受注金額
       vRecordS04T002T.ACPTKIN := Z1808B01.roundValue(vRecordS04T002T.ACPTUNT * vRecordS04T002T.ACPTSURYO, vUrikinhasukeisankbn); -- 受注金額
-- 本線対応変更 end ABI
       setTax( wTaxsetteikbn ); -- 消費税

       /* 3.合計： 受注金額、消費税合計 */
       vRecordS04T001T.ACPTKIN         := vRecordS04T001T.ACPTKIN         + vRecordS04T002T.ACPTKIN;         -- 受注金額
       vRecordS04T001T.ACPTSHOHITAXKIN := vRecordS04T001T.ACPTSHOHITAXKIN + vRecordS04T002T.ACPTSHOHITAXKIN; -- 受注消費税金額
-- インボイス対応追加 start ABI
       -- 税区分によって、以下の項目を設定
       -- 受注一般税抜金額、受注非課税金額、受注不課税売上金額、受注軽減税抜金額
       -- ※受注一般消費税金額、受注軽減消費税金額は加算完了後に消費税を算出して設定、受注金額、受注消費税金額も上記項目を合計して設定
       -- 税区分が「1:課税」の場合、受注一般税抜金額を加算
       IF vRecordS04T002T.TAXKBN = '1' THEN
         vRecordS04T001T.ACPTIPPANNUKIKIN := NVL(vRecordS04T001T.ACPTIPPANNUKIKIN, 0)     + vRecordS04T002T.ACPTKIN;         -- 受注一般税抜金額
       -- 税区分が「2:非課税」の場合、受注非課税金額を加算
       ELSIF vRecordS04T002T.TAXKBN = '2' THEN
         vRecordS04T001T.ACPTHIKAZEIURIIKIN := NVL(vRecordS04T001T.ACPTHIKAZEIURIIKIN, 0) + vRecordS04T002T.ACPTKIN;         -- 受注非課税金額
       -- 税区分が「3:不課税」の場合、受注不課税売上金額を加算
       ELSIF vRecordS04T002T.TAXKBN = '3' THEN
         vRecordS04T001T.ACPTFUKAZEIURIIKIN := NVL(vRecordS04T001T.ACPTFUKAZEIURIIKIN, 0) + vRecordS04T002T.ACPTKIN;         -- 受注不課税売上金額
       -- 税区分が「4:課税軽減」の場合、受注軽減税抜金額を加算
       ELSIF vRecordS04T002T.TAXKBN = '4' THEN
         vRecordS04T001T.ACPTKEIGENNUKIKIN := NVL(vRecordS04T001T.ACPTKEIGENNUKIKIN, 0)   + vRecordS04T002T.ACPTKIN;         -- 受注軽減税抜金額
       END IF;
-- インボイス対応追加 end ABI
    END IF;
    /*   展開行（セット商品子商品）：単価、金額、消費税 */
    IF vRecordS04T002T.NYURYOKUMEISAIFLG = '0' THEN 
       setKoseiShn( iOya, wTaxsetteikbn );
    END IF;
    
    /* 2.明細行登録 */
    InsertMeisai;
    
    /* 3.合計：本数、金額 */
    IF vRecordS04T002T.TANPINMEISAIFLG = '1' THEN -- 単品明細フラグ：単品
       IF vRecordS04T002T.NYURYOKUMEISAIFLG = '1' THEN -- 入力明細フラグ：入力行 
          addAllByCtgry( vRecordS04T002T.KEIJOKBN );
       ELSE -- 展開行
          addAllByCtgry( iOya.KEIJOKBN );
       END IF; 
    END IF;
    
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20026, SQLERRM || '商品明細作成時エラーが発生しました。');
  END insertShohin;
  
  /*****************************************************************************/
  /* プロシージャ名：配布物明細登録                                                */
  /*             ：1.受注配達明細の配布物行                                       */
  /*             ：2.配布物企画対象得意先マスタ                                   */
  /* 引数             ：iApkihoncd          ＡＰ基本コード（委託）                */
  /*                  ：iShopcd             ショップコード（委託）                */
  /*****************************************************************************/
  PROCEDURE insertHaifubutsu( iApkihoncd     IN VARCHAR2 -- ＡＰ基本コード
                            , iShopcd        IN VARCHAR2 -- ショップコード
  ) IS 
  /* 変数宣言 */
  wZokusei                  VARCHAR(1) := NULL; -- 配布物属性
  BEGIN 
    -- 配布物マスタから配布物属性を取得する
    SELECT HFBM.HAIFUBUTSUZOKUSEI
      INTO wZokusei
      FROM S02M035T HFBM
     WHERE HFBM.APKIHONCD    = iApkihoncd -- ＡＰ基本コード（委託）
       AND HFBM.SHOPCD       = iShopcd    -- ショップコード（委託）
       AND HFBM.HAIFUBUTSUCD = vRecordS04T002T.HAIFUBUTSUCD -- 配布物コード
       AND HFBM.YUKOKBN = '1'
    ;
    IF wZokusei = '0' THEN -- 本部の場合：受託のＡＰ、ショップで登録
       vRecordS04T002T.HAIFUBUTSUAPKIHONCD := vRecordS04T002T.APKIHONCD;
       vRecordS04T002T.HAIFUBUTSUSHOPCD    := vRecordS04T002T.SHOPCD;
       -- 受注済で配布物企画対象得意先マスタを更新 
       updateHaifubutsuTKM( vRecordS04T002T.HAIFUBUTSUAPKIHONCD
                          , vRecordS04T002T.HAIFUBUTSUSHOPCD
                          , vRecordS04T002T.HAIFUBUTSUCD
                          , vRecordS04T001T
                          , '2' ); 
    ELSE 
       vRecordS04T002T.HAIFUBUTSUAPKIHONCD := iApkihoncd;
       vRecordS04T002T.HAIFUBUTSUSHOPCD    := iShopcd;
    END IF;
    -- 明細行登録
    InsertMeisai;
    
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20027, SQLERRM || '配布物明細作成時エラーが発生しました。');
  END insertHaifubutsu;
  
  /*****************************************************************************/
  /* プロシージャ名：明細処理                                                     */
  /*             ：0.旧受注明細削除                                              */
  /*             ：1.受注配達明細テーブル                                         */
  /*             ：  商品                                                      */
  /*             ：  配布物                                                     */
  /*             ：  消費税調整                                                 */
  /*             ：2.在庫引当テーブル                                            */
  /*             ：3.配達拠点受注アラームテーブル                                  */
  /*             ：4.在庫引当テーブル                                            */
  /*             ：5.本数、金額合計                                              */
  /* 引数           ：iApkihoncd          ＡＰ基本コード                         */
  /*                ：iShopcd             ショップコード                         */
  /*                ：iAcptdnpyno         受注伝票番号                           */
  /*                ：iEdano              枝番号                                */
  /*                ：iMode               処理モード                            */
  /*                ：iZaikokanrikbn      在庫管理区分                           */
  /*****************************************************************************/
  PROCEDURE processMeisai( iApkihoncd     IN VARCHAR2 -- ＡＰ基本コード
                         , iShopcd        IN VARCHAR2 -- ショップコード
                         , iAcptdnpyno    IN VARCHAR2 -- 受注伝票番号
                         , iEdano         IN VARCHAR2 -- 枝番号
                         , iMode          IN VARCHAR2 -- 処理モード
                         , iZaikokanrikbn IN VARCHAR2 -- 在庫管理区分
  ) IS 
  /* 変数宣言 */
  -- セット商品展開用
  wOya                 S04T002T%rowtype := NULL;       -- 親商品明細行
  wKidTaxSum               NUMBER(11,0) := 0;          -- 子商品消費税合計
  wKidAiteTaxSum           NUMBER(11,0) := 0;          -- 子商品委託相手消費税合計
  wKidLastNo               NUMBER(2,0)  := 0;          -- 子商品最終明細枝番号
  -- 在庫管理対象フラグ
  wZaikokanritrgtflg        VARCHAR2(1) := NULL;
  /* CURSOR:受注配達明細テーブルから委託側の委託明細情報の取得 */
  CURSOR SELECT_MEISAI IS
    SELECT *
      FROM S04T002T JHMT
     WHERE JHMT.APKIHONCD   = iApkihoncd  -- ＡＰ基本コード
       AND JHMT.SHOPCD      = iShopcd     -- ショップコード
       AND JHMT.ACPTDNPYNO  = iAcptdnpyno -- 受注伝票番号
       AND JHMT.EDANO       = iEdano      -- 枝番号
       AND JHMT.YUKOKBN     = '1'         -- 有効区分
       AND JHMT.MEISAIKBN IN ('0','1')    -- 明細区分：商品・配布物
     ORDER BY JHMT.ACPTDNPYMEISAINO, JHMT.ACPTDNPYMEISAIEDANO
     FOR UPDATE 
    ;
  
  BEGIN 
    S99_CHECK_LOG('C0804B01.processMS', vRecordS04T001T.APKIHONCD, vRecordS04T001T.SHOPCD, '1', 'S', '1', 'XXX', 0, vRecordS04T001T.KOSHAAPKIHONCD, vRecordS04T001T.KOSHASHOPCD, vRecordS04T001T.KOSHACD, vRecordS04T001T.KSHNIPADRSE, 'C0804B01' , '明細行処理[開始] ' );
    -- 更新の場合：更新前明細データを削除する
    IF iMode = '1' THEN 
      DELETE FROM S04T002T JHMT
       WHERE JHMT.APKIHONCD  = vRecordS04T001T.APKIHONCD
         AND JHMT.SHOPCD     = vRecordS04T001T.SHOPCD
         AND JHMT.ACPTDNPYNO = vRecordS04T001T.ACPTDNPYNO
         AND JHMT.EDANO      = vRecordS04T001T.EDANO
      ;
      S99_CHECK_LOG('C0804B01.processMS', vRecordS04T001T.APKIHONCD, vRecordS04T001T.SHOPCD, '1', 'D', '1', 'S04T002T', SQL%ROWCOUNT, vRecordS04T001T.KOSHAAPKIHONCD, vRecordS04T001T.KOSHASHOPCD, vRecordS04T001T.KOSHACD, vRecordS04T001T.KSHNIPADRSE, 'C0804B01' , '受注配達明細テーブル削除 ' );
    END IF;
    
    /* 合計値クリア */
    -- 配達予定本数合計
    vRecordS04T001T.DLVRYYTIRGRAYUSYOHNS := 0; -- 配達予定レギュラー有償本数
    vRecordS04T001T.DLVRYYTIRGRAMUSHOHNS := 0; -- 配達予定レギュラー無償本数
    vRecordS04T001T.DLVRYYTISRMYUSYOHNS  := 0; -- 配達予定スリム有償本数
    vRecordS04T001T.DLVRYYTISRMMUSHOHNS  := 0; -- 配達予定スリム無償本数
    vRecordS04T001T.DLVRYYTISONOTAYUSYOHNS := 0; -- 配達予定その他有償本数
    vRecordS04T001T.DLVRYYTISONOTAMUSHOHNS := 0; -- 配達予定その他無償本数
    -- 配達予定金額合計
    vRecordS04T001T.DLVRYYTISEIHINMIZUURIKIN := 0; -- 配達予定製品水売上金額
    vRecordS04T001T.DLVRYYTISONOTAURIKIN     := 0; -- 配達予定その他売上金額
    -- 金額、消費税
    vRecordS04T001T.ACPTKIN         := 0; -- 受注金額
    vRecordS04T001T.ACPTSHOHITAXKIN := 0; -- 受注消費税金額
-- インボイス対応追加 start ABI
    -- 受注一般税抜金額、受注非課税金額、受注不課税売上金額、受注軽減税抜金額
    vRecordS04T001T.ACPTIPPANNUKIKIN   := 0; -- 受注一般税抜金額
    vRecordS04T001T.ACPTHIKAZEIURIIKIN := 0; -- 受注非課税金額
    vRecordS04T001T.ACPTFUKAZEIURIIKIN := 0; -- 受注不課税売上金額
    vRecordS04T001T.ACPTKEIGENNUKIKIN  := 0; -- 受注軽減税抜金額
-- インボイス対応追加 end ABI
    /* 合計用変数取得 */
    vWatercd := getMojisetteiatai( '000', '000', '096', '1', NULL, NULL, '商品大分類コード：製品水' );
    
    -- 明細単位で処理を行う
    OPEN SELECT_MEISAI;
      LOOP FETCH SELECT_MEISAI INTO vRecordS04T002T; 
        IF SELECT_MEISAI%ROWCOUNT = 0 THEN
           RAISE_APPLICATION_ERROR(-20012, '明細データが設定されていません。');
        END IF; 
        
-- インボイス対応変更 start ABI
        -- 配達予定日がインボイス施行日前の場合、消費税調整実施
        IF vRecordS04T001T.DLVRYYTIDD < vInvoicshikoymd THEN
-- インボイス対応変更 end ABI
          /* 消費税調整：前行親はセット商品の場合 */
          IF ( SELECT_MEISAI%NOTFOUND 
               OR ( SELECT_MEISAI%ROWCOUNT > 1 AND vRecordS04T002T.NYURYOKUMEISAIFLG = '1' ) -- 入力行
             ) 
             AND wOya.TANPINMEISAIFLG = '0' -- 前回親明細がセット商品
             AND wOya.ACPTSHOHITAXKIN <> wKidTaxSum THEN -- 消費税調整
             insertTaxChosei( wOya, wKidTaxSum, wKidAiteTaxSum, wKidLastNo );
          END IF;
-- インボイス対応変更 start ABI
        END IF;
-- インボイス対応変更 end ABI
        
        EXIT WHEN SELECT_MEISAI%NOTFOUND; 
        
        /* 基本情報 */
        -- 委託側情報を相手項目にセット
        vRecordS04T002T.ITAKUAITEAPKIHONCD  := vRecordS04T002T.APKIHONCD;  -- 委託相手ＡＰ基本コード
        vRecordS04T002T.ITAKUAITESHOPCD     := vRecordS04T002T.SHOPCD;     -- 委託相手ショップコード
        vRecordS04T002T.ITAKUAITEACPTDNPYNO := vRecordS04T002T.ACPTDNPYNO; -- 委託相手受注伝票番号
        vRecordS04T002T.ITAKUAITEEDANO      := vRecordS04T002T.EDANO;      -- 委託相手枝番号
        vRecordS04T002T.ITAKUAITEACPTDNPYMEISAINO    := vRecordS04T002T.ACPTDNPYMEISAINO;    -- 委託相手受注伝票明細番号
        vRecordS04T002T.ITAKUAITEACPTDNPYMEISAIEDANO := vRecordS04T002T.ACPTDNPYMEISAIEDANO; -- 委託相手受注伝票明細枝番号
        vRecordS04T002T.ITAKUCMPNYACPTUNT            := vRecordS04T002T.ACPTUNT;              -- 委託相手受注単価
        vRecordS04T002T.ITAKUCMPNYACPTUNTSHOHITAXKIN := vRecordS04T002T.ACPTUNTSHOHITAXKIN;   -- 委託相手受注単価消費税
        vRecordS04T002T.ITAKUCMPNYACPTSHOHITAXKIN    := vRecordS04T002T.ACPTSHOHITAXKIN;      -- 委託相手受注消費税金額
        -- 伝票ヘッダと共有する情報をセット
        vRecordS04T002T.APKIHONCD     := vRecordS04T001T.APKIHONCD;     -- ＡＰ基本コード
        vRecordS04T002T.SHOPCD        := vRecordS04T001T.SHOPCD;        -- ショップコード
        vRecordS04T002T.ACPTDNPYNO    := vRecordS04T001T.ACPTDNPYNO;    -- 受注伝票番号
        vRecordS04T002T.EDANO         := vRecordS04T001T.EDANO;         -- 枝番号
        vRecordS04T002T.DATSKSMOTOKBN := vRecordS04T001T.DATSKSMOTOKBN; -- データ作成元区分
        -- 設定なし：納品情報
        vRecordS04T002T.NOHINSURYO          := 0; -- 納品数量
        vRecordS04T002T.NOHINUNT            := 0; -- 納品単価
        vRecordS04T002T.NOHINKIN            := 0; -- 納品金額
        vRecordS04T002T.NOHINUNTSHOHITAXKIN := 0; -- 納品単価消費税金額
        vRecordS04T002T.NOHINSHOHITAXKIN    := 0; -- 納品消費税金額
        -- 設定なし：売上伝票情報
        vRecordS04T002T.URIDNPYNO          := NULL; -- 売上伝票番号
        vRecordS04T002T.URIDNPYEDANO       := NULL; -- 売上伝票枝番号
        vRecordS04T002T.URIDNPYMEISAINO    := NULL; -- 売上伝票明細番号
        vRecordS04T002T.URIDNPYMEISAIEDANO := NULL; -- 売上伝票明細枝番号
        
        /* 商品 */
        IF vRecordS04T002T.MEISAIKBN = '0' THEN 
           insertShohin( wOya, wZaikokanritrgtflg );
           
           /* 2.在庫引当テーブル */
           IF vRecordS04T002T.TANPINMEISAIFLG = '1' -- 単品明細フラグ：単品
              -- 在庫管理区分・在庫管理対象フラグ：ＯＮ
              AND iZaikokanrikbn <> '0' AND wZaikokanritrgtflg <> '0' THEN 
              updateZaiko( vRecordS04T001T, vRecordS04T002T.ACPTSURYO );
           END IF;
           /* 3.配達拠点受注アラームテーブル */
           IF vRecordS04T002T.TANPINMEISAIFLG = '1' AND vRecordS04T001T.DLVRYKYOTENCD IS NOT NULL -- 単品明細フラグ：単品、配達拠点設定あり
              AND ( isRegular(vRecordS04T002T.SHNCD) OR isSlim(vRecordS04T002T.SHNCD) ) THEN -- レギュラー／スリム商品
              updateKyotenAlarm( vRecordS04T001T, vRecordS04T002T.ACPTSURYO, vRecordS04T002T.KSHNUNYOYMD );
           END IF;
        END IF;
        
        /* 配布物 */
        IF vRecordS04T002T.MEISAIKBN = '1' THEN
           insertHaifubutsu( iApkihoncd, iShopcd );
        END IF;
        
-- インボイス対応変更 start ABI
        -- 配達予定日がインボイス施行日前の場合、消費税調整用項目を設定
        IF vRecordS04T001T.DLVRYYTIDD < vInvoicshikoymd THEN
-- インボイス対応変更 end ABI
          /* セット商品展開・消費税調整用 */
          IF vRecordS04T002T.NYURYOKUMEISAIFLG = '1' THEN -- 入力行：セット商品情報クリア
             wOya           := vRecordS04T002T; -- 親明細行
             wKidTaxSum     := 0;               -- 子商品消費税合計
             wKidAiteTaxSum := 0;               -- 子商品委託相手消費税合計
             wKidLastNo     := vRecordS04T002T.ACPTDNPYMEISAIEDANO;               -- 子商品最終枝番号
          ELSE -- 展開行：構成商品消費税合計
             wKidTaxSum     := wKidTaxSum     + vRecordS04T002T.ACPTSHOHITAXKIN;
             wKidAiteTaxSum := wKidAiteTaxSum + vRecordS04T002T.ITAKUCMPNYACPTSHOHITAXKIN;
             wKidLastNo     := vRecordS04T002T.ACPTDNPYMEISAIEDANO;
          END IF;
-- インボイス対応変更 start ABI
        ELSE
          /* セット商品展開・消費税調整用 */
          IF vRecordS04T002T.NYURYOKUMEISAIFLG = '1' THEN -- 入力行：セット商品情報クリア
             wOya := vRecordS04T002T; -- 親明細行
          END IF;
        END IF;
-- インボイス対応変更 end ABI
        
      END LOOP;
    CLOSE SELECT_MEISAI;

    S99_CHECK_LOG('C0804B01.processMS', iApkihoncd, iShopcd, '1', 'E', '1', 'XXX', 0, vRecordS04T001T.KOSHAAPKIHONCD, vRecordS04T001T.KOSHASHOPCD, vRecordS04T001T.KOSHACD, vRecordS04T001T.KSHNIPADRSE, 'C0804B01' , '明細行処理[終了] ' );
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20028, SQLERRM || '受注配達明細処理時エラーが発生しました。');
  END processMeisai;
  
  /*****************************************************************************/
  /* プロシージャ名：明細行処理後の受注伝票設定                                      */
  /*             ：1.引当先                                                     */
  /*             ：2.固定値                                                     */
  /*             ：3.設定なしのデフォルト値                                       */
  /* 引数           ：なし                                                      */
  /*****************************************************************************/
  PROCEDURE setDnpyAfterMeisai IS
  BEGIN 
    /* 引当先 */
    vRecordS04T001T.ACPTHIKIATESAKIAPKIHONCD := vRecordS04T001T.DLVRYKANKATSUAPKIHONCD ; --  受注引当先ＡＰ基本コード：配達管轄ＡＰ基本コード
    vRecordS04T001T.ACPTHIKIATESAKISHOPCD    := vRecordS04T001T.DLVRYKANKATSUSHOPCD    ; --  受注引当先ショップコード：配達管轄ショップコード
    vRecordS04T001T.ACPTHIKIATESAKIKYOTENCD  := vRecordS04T001T.DLVRYKYOTENCD          ; --  受注引当先拠点コード：配達拠点コード
    /* 固定値 */
    vRecordS04T001T.DLVRYTRGTFLG := '1';  -- 配達対象フラグ：配達対象
    vRecordS04T001T.DNPYKBN      := '14'; -- 伝票区分：売上出庫
    vRecordS04T001T.TORIHIKIKBN  := '1';  -- 取引区分：受注
    vRecordS04T001T.FZAIOKIJISSEKIFLG := '0'; -- 不在置き実績フラグ
    vRecordS04T001T.ACPTTHDAIBIKIFLG  := '0'; -- 受注時代引フラグ
-- インボイス対応追加 start
    vRecordS04T001T.SINGLDNPYFLG      := '0'; -- 単体伝票締めフラグ
-- インボイス対応追加 end
    /* 設定なし：NULL／0 */
    vRecordS04T001T.SKSMOTOKANRIKBN := NULL; --  作成元管理区分
    vRecordS04T001T.SKSMOTOKANRINO  := NULL; --  作成元管理番号
    vRecordS04T001T.KZBTLN1N2RITTORUHNS := 0; --  回収実績ボトル１２Ｌ本数
    vRecordS04T001T.KZBTLN8RITTORUHNS   := 0; --  回収実績ボトル８Ｌ本数
    vRecordS04T001T.DLVRYJISSEKIDD   := NULL; --  配達実績日
    /* 設定なし：受注締 */
    vRecordS04T001T.ACPTSHIMEPRCSDD   := '00000000'; --  受注締処理日
    vRecordS04T001T.ACPTSHIMETNSHAAPKIHONCD := NULL; --  受注締担当者ＡＰ基本コード
    vRecordS04T001T.ACPTSHIMETNSHASHOPCD    := NULL; --  受注締担当者ショップコード
    vRecordS04T001T.ACPTSHIMEPRCSTNSHACD    := NULL; --  受注締処理担当者コード
    /* 設定なし：配達実績 */
    vRecordS04T001T.DLVRYJISSEKIPRCSDD  := '00000000'; --  配達実績処理日
    vRecordS04T001T.DLVRYJISSEKIMONOAPKIHONCD := NULL; --  配達実績者ＡＰ基本コード
    vRecordS04T001T.DLVRYJISSEKIMONOSHOPCD    := NULL; --  配達実績者ショップコード
    vRecordS04T001T.DLVRYJISSEKIPRCSTNSHACD   := NULL; --  配達実績処理担当者コード
    /* 設定なし：売上締 */
    vRecordS04T001T.URISHIMEPRCSDD     := '00000000'; --  売上締処理日
    vRecordS04T001T.GETSUJISHIMEDYDM   := NULL      ; --  月次締年月
    vRecordS04T001T.GETSUJISHIMEDD     := NULL      ; --  月次締日
    vRecordS04T001T.GETSUJIPRCSJISSIDD := '00000000'; --  月次処理実施日
    /* 設定なし：納品書 */
    vRecordS04T001T.NOSHOPRNTTRGTFLG      := '1';  --  納品書印刷対象フラグ
    vRecordS04T001T.NOSHOPRNTSUMIFLG      := '0';  --  納品書印刷済フラグ
    vRecordS04T001T.NOSHOPRNTDD           := NULL; --  納品書印刷日
    vRecordS04T001T.NOSHOHIKAEPRNTTRGTFLG := '1';  --  納品書控印刷対象フラグ
    vRecordS04T001T.NOSHOHIKAEPRNTSUMIFLG := '0';  --  納品書控印刷済フラグ
    vRecordS04T001T.NOSHOHIKAEPRNTDD      := NULL; --  納品書控印刷日
    /* 設定なし：入金票 */
    vRecordS04T001T.NYKNHYOPRNTTRGTFLG := '0';  --  入金票印刷対象フラグ
    vRecordS04T001T.NYKNHYOPRNTSUMIFLG := '0';  --  入金票印刷済フラグ
    vRecordS04T001T.NYKNHYOPRNTDD      := NULL; --  入金票印刷日
     /* 設定なし：サーバー指示書 */
    vRecordS04T001T.WSVSIJISHOPRNTSUMIFLG := '0';  --  サーバー指示書印刷済フラグ
    vRecordS04T001T.WSVSIJISHOPRNTDD      := NULL; --  サーバー指示書印刷日
     /* 設定なし：配達実績合計 */
    vRecordS04T001T.DLVRYJISSEKIRGRAYUSYOHNS := 0; --  配達実績レギュラー有償本数
    vRecordS04T001T.DLVRYJISSEKIRGRAMUSHOHNS := 0; --  配達実績レギュラー無償本数
    vRecordS04T001T.DLVRYJISSEKISRMYUSYOHNS  := 0; --  配達実績スリム有償本数
    vRecordS04T001T.DLVRYJISSEKISRMMUSHOHNS  := 0; --  配達実績スリム無償本数
    vRecordS04T001T.DLVRYJISSEKISTYSHNS      := 0; --  配達実績その他有償本数
    vRecordS04T001T.DLVRYJISSEKISTMSHNS      := 0; --  配達実績その他無償本数
    vRecordS04T001T.DLVRYJISSEKISHSUAKIN     := 0; --  配達実績製品水売上金額
    vRecordS04T001T.DLVRYJISSEKISONOTAURIKIN := 0; --  配達実績その他売上金額
-- インボイス対応追加 start ABI
    vRecordS04T001T.ACPTIPPANNUKIKIN   := NVL(vRecordS04T001T.ACPTIPPANNUKIKIN, 0);   -- 受注一般税抜金額
    vRecordS04T001T.ACPTHIKAZEIURIIKIN := NVL(vRecordS04T001T.ACPTHIKAZEIURIIKIN, 0); -- 受注非課税金額
    vRecordS04T001T.ACPTFUKAZEIURIIKIN := NVL(vRecordS04T001T.ACPTFUKAZEIURIIKIN, 0); -- 受注不課税売上金額
    vRecordS04T001T.ACPTKEIGENNUKIKIN  := NVL(vRecordS04T001T.ACPTKEIGENNUKIKIN, 0);  -- 受注軽減税抜金額
    -- 配達予定日がインボイス施行日以降の場合、受注金額に受注一般税抜金額、受注非課税金額、受注不課税売上金額、受注軽減税抜金額の合計を設定
    IF vRecordS04T001T.DLVRYYTIDD >= vInvoicshikoymd THEN
      /* 設定あり：受注 */
      vRecordS04T001T.ACPTKIN := vRecordS04T001T.ACPTIPPANNUKIKIN
                               + vRecordS04T001T.ACPTHIKAZEIURIIKIN
                               + vRecordS04T001T.ACPTFUKAZEIURIIKIN
                               + vRecordS04T001T.ACPTKEIGENNUKIKIN;             -- 受注金額
    END IF;
    -- 受注一般消費税金額
-- 本線対応変更 start ABI
--    vRecordS04T001T.ACPTIPPANTAXKIN := S01SyohizeiService.SyohizeiKeisan('1',   --単体計算フラグ（する）
    vRecordS04T001T.ACPTIPPANTAXKIN := Z1808B01.SyohizeiKeisan('1',   --単体計算フラグ（する）
-- 本線対応変更 end ABI
                                                                         '0',   --税設定区分
                                                                         '1',   --税区分
                                                vRecordS04T001T.TAXKEISANKBN,   --税計算区分
                                                  vRecordS04T001T.DLVRYYTIDD,   --切り替え日：配達予定日
                                                   vRecordS04T001T.APKIHONCD,   --AP基本コード
                                                      vRecordS04T001T.SHOPCD,   --ショップコード
                                                                        NULL,   --仕入先コード
                                                                        NULL,   --数量
                                            vRecordS04T001T.ACPTIPPANNUKIKIN);  -- 受注一般税抜金額
    -- 受注軽減消費税金額
-- 本線対応変更 start ABI
--    vRecordS04T001T.ACPTKEIGENTAXKIN := S01SyohizeiService.SyohizeiKeisan('1',  --単体計算フラグ（する）
    vRecordS04T001T.ACPTKEIGENTAXKIN := Z1808B01.SyohizeiKeisan('1',  --単体計算フラグ（する）
-- 本線対応変更 end ABI
                                                                          '0',  --税設定区分
                                                                          '4',  --税区分
                                                 vRecordS04T001T.TAXKEISANKBN,  --税計算区分
                                                   vRecordS04T001T.DLVRYYTIDD,  --切り替え日：配達予定日
                                                    vRecordS04T001T.APKIHONCD,  --AP基本コード
                                                       vRecordS04T001T.SHOPCD,  --ショップコード
                                                                         NULL,  --仕入先コード
                                                                         NULL,  --数量
                                            vRecordS04T001T.ACPTKEIGENNUKIKIN); -- 受注軽減税抜金額
    -- 配達予定日がインボイス施行日以降の場合、受注消費税金額に受注一般消費税金額、受注軽減消費税金額の合計を設定
    IF vRecordS04T001T.DLVRYYTIDD >= vInvoicshikoymd THEN
      vRecordS04T001T.ACPTSHOHITAXKIN := vRecordS04T001T.ACPTIPPANTAXKIN
                                       + vRecordS04T001T.ACPTKEIGENTAXKIN;   -- 受注消費税金額
    END IF;
-- インボイス対応追加 end ABI
     /* 設定なし：納品 */
    vRecordS04T001T.NOHINKIN         := 0; --  納品金額
    vRecordS04T001T.NOHINSHOHITAXKIN := 0; --  納品消費税金額
     /* 設定なし：前回配達 */
    vRecordS04T001T.ZNKDLVRYDD       := NULL; --  前回配達日
    vRecordS04T001T.ZNKDLVRYHNS      := 0; --  前回配達本数
    vRecordS04T001T.JIKAIDLVRYYTIDD  := NULL; --  次回配達予定日
    vRecordS04T001T.ZNKSHIMEDD       := NULL; --  前回締日
    vRecordS04T001T.ZNKKURIKOSHIKIN  := NULL; --  前回繰越金額

  END setDnpyAfterMeisai;
  
  /*****************************************************************************/
  /* プロシージャ名：受託配達テーブル登録                                          */
  /* 引数             ：なし                                                    */
  /*****************************************************************************/
  PROCEDURE insertDnpy IS
  BEGIN
    -- 基本情報設定
    vRecordS04T001T.TOSHAAPKIHONCD := vRecordS04T001T.KOSHAAPKIHONCD; -- 登録者ＡＰ基本コード
    vRecordS04T001T.TOSHASHOPCD    := vRecordS04T001T.KOSHASHOPCD;    -- 登録者ショップコード
    vRecordS04T001T.TOSHACD        := vRecordS04T001T.KOSHACD;        -- 登録者コード
    vRecordS04T001T.INPTSYSTMYMD   := vRecordS04T001T.KSHNSYSTMYMD;   -- 登録システム日付
    vRecordS04T001T.INPTSYSTMJIKAN := vRecordS04T001T.KSHNSYSTMJIKAN; -- 登録システム時間
    vRecordS04T001T.INPTUNYOYMD    := vRecordS04T001T.KSHNUNYOYMD;    -- 登録運用日付
    vRecordS04T001T.INPTIPADRSE    := vRecordS04T001T.KSHNIPADRSE;    -- 登録ＩＰアドレス
    vRecordS04T001T.INPTPGMID      := vRecordS04T001T.KSHNPGMID;      -- 登録プログラムＩＤ
    
    INSERT INTO S04T001T JHTT VALUES vRecordS04T001T;

    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20029, SQLERRM || '受注配達テーブルに新規登録時エラーが発生しました。');
  END insertDnpy;
  
  /*****************************************************************************/
  /* プロシージャ名：受託配達テーブル更新                                          */
  /* 引数             ：なし                                                    */
  /*****************************************************************************/
  PROCEDURE updateDnpy IS
  BEGIN
    UPDATE S04T001T JHTT
       SET JHTT.DLVRYTRGTFLG = vRecordS04T001T.DLVRYTRGTFLG -- 配達対象フラグ
         , JHTT.DNPYKBN      = vRecordS04T001T.DNPYKBN      -- 伝票区分
         , JHTT.TORIHIKIKBN  = vRecordS04T001T.TORIHIKIKBN  -- 取引区分
         , JHTT.ACPTKBN    = vRecordS04T001T.ACPTKBN    -- 受注区分
         , JHTT.ACPTKBNSHS = vRecordS04T001T.ACPTKBNSHS -- 受注区分詳細
         , JHTT.ACPTDD     = vRecordS04T001T.ACPTDD     -- 受注日
         , JHTT.DLVRYYTIDD = vRecordS04T001T.DLVRYYTIDD -- 配達予定日
         , JHTT.DLVRYJISSEKIDD = vRecordS04T001T.DLVRYJISSEKIDD -- 配達実績日
         , JHTT.DLVRYDD        = vRecordS04T001T.DLVRYDD        -- 配達日
         , JHTT.DLVRYKIBOJIKAN     = vRecordS04T001T.DLVRYKIBOJIKAN     -- 配達希望時間
         , JHTT.DLVRYKIBOJIKANSHRY = vRecordS04T001T.DLVRYKIBOJIKANSHRY -- 配達希望時間終了
         , JHTT.DLVRYKIBOJIKANKBN  = vRecordS04T001T.DLVRYKIBOJIKANKBN  -- 配達希望時間区分
         , JHTT.FZAIOKIKANOFLG    = vRecordS04T001T.FZAIOKIKANOFLG    -- 不在置き可能フラグ
         , JHTT.FZAIOKIJISSEKIFLG = vRecordS04T001T.FZAIOKIJISSEKIFLG -- 不在置き実績フラグ
         , JHTT.DLVRYTNSHACD = vRecordS04T001T.DLVRYTNSHACD -- 配達担当者コード
         , JHTT.DLVRYKANKATSUAPKIHONCD = vRecordS04T001T.DLVRYKANKATSUAPKIHONCD -- 配達管轄ＡＰ基本コード
         , JHTT.DLVRYKANKATSUSHOPCD    = vRecordS04T001T.DLVRYKANKATSUSHOPCD    -- 配達管轄ショップコード
         , JHTT.DLVRYKYOTENCD          = vRecordS04T001T.DLVRYKYOTENCD          -- 配達拠点コード
         , JHTT.DLVRYAREACD            = vRecordS04T001T.DLVRYAREACD            -- 配達エリアコード
         , JHTT.DLVRYKBN     = vRecordS04T001T.DLVRYKBN -- 配達区分
         , JHTT.DLVRYCMPNYCD = vRecordS04T001T.DLVRYCMPNYCD -- 配達会社コード
         , JHTT.AZUKARIBTLN1N2RITTORUHNS = vRecordS04T001T.AZUKARIBTLN1N2RITTORUHNS -- 預かりボトル１２Ｌ本数
         , JHTT.KYBTLN1N2RITTORUHNS      = vRecordS04T001T.KYBTLN1N2RITTORUHNS -- 回収予定ボトル１２Ｌ本数
         , JHTT.KZBTLN1N2RITTORUHNS      = vRecordS04T001T.KZBTLN1N2RITTORUHNS -- 回収実績ボトル１２Ｌ本数
         , JHTT.AZUKARIBTLN8RITTORUHNS   = vRecordS04T001T.AZUKARIBTLN8RITTORUHNS -- 預かりボトル８Ｌ本数
         , JHTT.KYBTLN8RITTORUHNS        = vRecordS04T001T.KYBTLN8RITTORUHNS -- 回収予定ボトル８Ｌ本数
         , JHTT.KZBTLN8RITTORUHNS        = vRecordS04T001T.KZBTLN8RITTORUHNS -- 回収実績ボトル８Ｌ本数
         , JHTT.PRNTMEMON1 = vRecordS04T001T.PRNTMEMON1 -- 印刷メモ１
         , JHTT.PRNTMEMON2 = vRecordS04T001T.PRNTMEMON2 -- 印刷メモ２
         , JHTT.PRNTMEMON3 = vRecordS04T001T.PRNTMEMON3 -- 印刷メモ３
         , JHTT.ACPTSHIMEPRCSDD         = vRecordS04T001T.ACPTSHIMEPRCSDD         -- 受注締処理日
         , JHTT.ACPTSHIMETNSHAAPKIHONCD = vRecordS04T001T.ACPTSHIMETNSHAAPKIHONCD -- 受注締担当者ＡＰ基本コード
         , JHTT.ACPTSHIMETNSHASHOPCD    = vRecordS04T001T.ACPTSHIMETNSHASHOPCD    -- 受注締担当者ショップコード
         , JHTT.ACPTSHIMEPRCSTNSHACD    = vRecordS04T001T.ACPTSHIMEPRCSTNSHACD    -- 受注締処理担当者コード
         , JHTT.DLVRYJISSEKIPRCSDD        = vRecordS04T001T.DLVRYJISSEKIPRCSDD        -- 配達実績処理日
         , JHTT.DLVRYJISSEKIMONOAPKIHONCD = vRecordS04T001T.DLVRYJISSEKIMONOAPKIHONCD -- 配達実績者ＡＰ基本コード
         , JHTT.DLVRYJISSEKIMONOSHOPCD    = vRecordS04T001T.DLVRYJISSEKIMONOSHOPCD    -- 配達実績者ショップコード
         , JHTT.DLVRYJISSEKIPRCSTNSHACD   = vRecordS04T001T.DLVRYJISSEKIPRCSTNSHACD   -- 配達実績処理担当者コード
         , JHTT.URISHIMEPRCSDD     = vRecordS04T001T.URISHIMEPRCSDD     -- 売上締処理日
         , JHTT.GETSUJISHIMEDYDM   = vRecordS04T001T.GETSUJISHIMEDYDM   -- 月次締年月
         , JHTT.GETSUJISHIMEDD     = vRecordS04T001T.GETSUJISHIMEDD     -- 月次締日
         , JHTT.GETSUJIPRCSJISSIDD = vRecordS04T001T.GETSUJIPRCSJISSIDD -- 月次処理実施日
         , JHTT.DLVRYJUN = vRecordS04T001T.DLVRYJUN -- 配達順
         , JHTT.NOSHOPRNTTRGTFLG = vRecordS04T001T.NOSHOPRNTTRGTFLG -- 納品書印刷対象フラグ
         , JHTT.NOSHOPRNTSUMIFLG = vRecordS04T001T.NOSHOPRNTSUMIFLG -- 納品書印刷済フラグ
         , JHTT.NOSHOPRNTDD      = vRecordS04T001T.NOSHOPRNTDD      -- 納品書印刷日
         , JHTT.NOSHOHIKAEPRNTTRGTFLG = vRecordS04T001T.NOSHOHIKAEPRNTTRGTFLG -- 納品書控印刷対象フラグ
         , JHTT.NOSHOHIKAEPRNTSUMIFLG = vRecordS04T001T.NOSHOHIKAEPRNTSUMIFLG -- 納品書控印刷済フラグ
         , JHTT.NOSHOHIKAEPRNTDD      = vRecordS04T001T.NOSHOHIKAEPRNTDD      -- 納品書控印刷日
         , JHTT.NYKNHYOPRNTTRGTFLG = vRecordS04T001T.NYKNHYOPRNTTRGTFLG -- 入金票印刷対象フラグ
         , JHTT.NYKNHYOPRNTSUMIFLG = vRecordS04T001T.NYKNHYOPRNTSUMIFLG -- 入金票印刷済フラグ
         , JHTT.NYKNHYOPRNTDD      = vRecordS04T001T.NYKNHYOPRNTDD      -- 入金票印刷日
         , JHTT.WSVSIJISHOPRNTTRGTFLG = vRecordS04T001T.WSVSIJISHOPRNTTRGTFLG -- サーバー指示書印刷対象フラグ
         , JHTT.WSVSIJISHOPRNTSUMIFLG = vRecordS04T001T.WSVSIJISHOPRNTSUMIFLG -- サーバー指示書印刷済フラグ
         , JHTT.WSVSIJISHOPRNTDD      = vRecordS04T001T.WSVSIJISHOPRNTDD      -- サーバー指示書印刷日
-- インボイス対応追加 start ABI
         , JHTT.SINGLDNPYFLG      = vRecordS04T001T.SINGLDNPYFLG      -- 単体伝票締めフラグ
-- インボイス対応追加 end ABI
         , JHTT.DNPYSTS  = vRecordS04T001T.DNPYSTS  -- 伝票ステータス
         , JHTT.ACPTSTS  = vRecordS04T001T.ACPTSTS  -- 受注ステータス
         , JHTT.DLVRYSTS = vRecordS04T001T.DLVRYSTS -- 配達ステータス
         , JHTT.DATSKSMOTOKBN   = vRecordS04T001T.DATSKSMOTOKBN -- データ作成元区分
         , JHTT.SKSMOTOKANRIKBN = vRecordS04T001T.SKSMOTOKANRIKBN -- 作成元管理区分
         , JHTT.SKSMOTOKANRINO  = vRecordS04T001T.SKSMOTOKANRINO -- 作成元管理番号
         , JHTT.ACPTHIKIATESAKIAPKIHONCD = vRecordS04T001T.ACPTHIKIATESAKIAPKIHONCD -- 受注引当先ＡＰ基本コード
         , JHTT.ACPTHIKIATESAKISHOPCD    = vRecordS04T001T.ACPTHIKIATESAKISHOPCD    -- 受注引当先ショップコード
         , JHTT.ACPTHIKIATESAKIKYOTENCD  = vRecordS04T001T.ACPTHIKIATESAKIKYOTENCD  -- 受注引当先拠点コード
         , JHTT.DLVRYYTIRGRAYUSYOHNS   = vRecordS04T001T.DLVRYYTIRGRAYUSYOHNS   -- 配達予定レギュラー有償本数
         , JHTT.DLVRYYTIRGRAMUSHOHNS   = vRecordS04T001T.DLVRYYTIRGRAMUSHOHNS   -- 配達予定レギュラー無償本数
         , JHTT.DLVRYYTISRMYUSYOHNS    = vRecordS04T001T.DLVRYYTISRMYUSYOHNS    -- 配達予定スリム有償本数
         , JHTT.DLVRYYTISRMMUSHOHNS    = vRecordS04T001T.DLVRYYTISRMMUSHOHNS    -- 配達予定スリム無償本数
         , JHTT.DLVRYYTISONOTAYUSYOHNS = vRecordS04T001T.DLVRYYTISONOTAYUSYOHNS -- 配達予定その他有償本数
         , JHTT.DLVRYYTISONOTAMUSHOHNS = vRecordS04T001T.DLVRYYTISONOTAMUSHOHNS -- 配達予定その他無償本数
         , JHTT.DLVRYYTISEIHINMIZUURIKIN = vRecordS04T001T.DLVRYYTISEIHINMIZUURIKIN -- 配達予定製品水売上金額
         , JHTT.DLVRYYTISONOTAURIKIN     = vRecordS04T001T.DLVRYYTISONOTAURIKIN     -- 配達予定その他売上金額
         , JHTT.DLVRYJISSEKIRGRAYUSYOHNS = vRecordS04T001T.DLVRYJISSEKIRGRAYUSYOHNS -- 配達実績レギュラー有償本数
         , JHTT.DLVRYJISSEKIRGRAMUSHOHNS = vRecordS04T001T.DLVRYJISSEKIRGRAMUSHOHNS -- 配達実績レギュラー無償本数
         , JHTT.DLVRYJISSEKISRMYUSYOHNS  = vRecordS04T001T.DLVRYJISSEKISRMYUSYOHNS  -- 配達実績スリム有償本数
         , JHTT.DLVRYJISSEKISRMMUSHOHNS  = vRecordS04T001T.DLVRYJISSEKISRMMUSHOHNS  -- 配達実績スリム無償本数
         , JHTT.DLVRYJISSEKISTYSHNS      = vRecordS04T001T.DLVRYJISSEKISTYSHNS      -- 配達実績その他有償本数
         , JHTT.DLVRYJISSEKISTMSHNS      = vRecordS04T001T.DLVRYJISSEKISTMSHNS      -- 配達実績その他無償本数
         , JHTT.DLVRYJISSEKISHSUAKIN     = vRecordS04T001T.DLVRYJISSEKISHSUAKIN     -- 配達実績製品水売上金額
         , JHTT.DLVRYJISSEKISONOTAURIKIN = vRecordS04T001T.DLVRYJISSEKISONOTAURIKIN -- 配達実績その他売上金額
         , JHTT.ACPTKIN         = vRecordS04T001T.ACPTKIN         -- 受注金額
         , JHTT.ACPTSHOHITAXKIN = vRecordS04T001T.ACPTSHOHITAXKIN -- 受注消費税金額
-- インボイス対応追加 start ABI
         , JHTT.ACPTIPPANNUKIKIN     = NVL(vRecordS04T001T.ACPTIPPANNUKIKIN,0)     -- 受注一般税抜金額
         , JHTT.ACPTIPPANTAXKIN      = NVL(vRecordS04T001T.ACPTIPPANTAXKIN,0)      -- 受注一般消費税金額
         , JHTT.ACPTKEIGENNUKIKIN    = NVL(vRecordS04T001T.ACPTKEIGENNUKIKIN,0)    -- 受注軽減税抜金額
         , JHTT.ACPTKEIGENTAXKIN     = NVL(vRecordS04T001T.ACPTKEIGENTAXKIN,0)     -- 受注軽減消費税金額
         , JHTT.ACPTHIKAZEIURIIKIN   = NVL(vRecordS04T001T.ACPTHIKAZEIURIIKIN,0)   -- 受注非課税金額
         , JHTT.ACPTFUKAZEIURIIKIN   = NVL(vRecordS04T001T.ACPTFUKAZEIURIIKIN,0)   -- 受注不課税売上金額
-- インボイス対応追加 end ABI
         , JHTT.NOHINKIN         = vRecordS04T001T.NOHINKIN         -- 納品金額
         , JHTT.NOHINSHOHITAXKIN = vRecordS04T001T.NOHINSHOHITAXKIN -- 納品消費税金額
-- インボイス対応追加 start ABI
         , JHTT.NOHINIPPANNUKIKIN    = NVL(vRecordS04T001T.NOHINIPPANNUKIKIN,0)    -- 納品一般税抜金額
         , JHTT.NOHINIPPANTAXKIN     = NVL(vRecordS04T001T.NOHINIPPANTAXKIN,0)     -- 納品一般消費税金額
         , JHTT.NOHINKEIGENNUKIKIN   = NVL(vRecordS04T001T.NOHINKEIGENNUKIKIN,0)   -- 納品軽減税抜金額
         , JHTT.NOHINKEIGENTAXKIN    = NVL(vRecordS04T001T.NOHINKEIGENTAXKIN,0)    -- 納品軽減消費税金額
         , JHTT.NOHINHIKAZEIURIIKIN  = NVL(vRecordS04T001T.NOHINHIKAZEIURIIKIN,0)  -- 納品非課税金額
         , JHTT.NOHINFUKAZEIURIIKIN  = NVL(vRecordS04T001T.NOHINFUKAZEIURIIKIN,0)  -- 納品不課税売上金額
-- インボイス対応追加 end ABI
         , JHTT.KURERURITTORUSTUP    = vRecordS04T001T.KURERURITTORUSTUP    -- クレールＬ設置
         , JHTT.KURERURITTORUHIKIAGE = vRecordS04T001T.KURERURITTORUHIKIAGE -- クレールＬ引上
         , JHTT.KURERUESUSTUP        = vRecordS04T001T.KURERUESUSTUP        -- クレールＳ設置
         , JHTT.KURERUESUHIKIAGE     = vRecordS04T001T.KURERUESUHIKIAGE     -- クレールＳ引上
         , JHTT.SONOTASTUP           = vRecordS04T001T.SONOTASTUP           -- その他設置
         , JHTT.SONOTAHIKIAGE        = vRecordS04T001T.SONOTAHIKIAGE        -- その他引上
         , JHTT.TAXKEISANKBN = vRecordS04T001T.TAXKEISANKBN -- 税計算区分
         , JHTT.ZNKDLVRYDD      = vRecordS04T001T.ZNKDLVRYDD      -- 前回配達日
         , JHTT.ZNKDLVRYHNS     = vRecordS04T001T.ZNKDLVRYHNS     -- 前回配達本数
         , JHTT.JIKAIDLVRYYTIDD = vRecordS04T001T.JIKAIDLVRYYTIDD -- 次回配達予定日
         , JHTT.ZNKSHIMEDD      = vRecordS04T001T.ZNKSHIMEDD      -- 前回締日
         , JHTT.ZNKKURIKOSHIKIN = vRecordS04T001T.ZNKKURIKOSHIKIN -- 前回繰越金額
         , JHTT.ACPTTHDAIBIKIFLG = vRecordS04T001T.ACPTTHDAIBIKIFLG -- 受注時代引フラグ
         , JHTT.ACPTRCPTKEIROKBN = vRecordS04T001T.ACPTRCPTKEIROKBN -- 受注受付経路区分
         , JHTT.YUKOKBN = vRecordS04T001T.YUKOKBN -- 有効区分
         /* 更新者 */
         , JHTT.KOSHAAPKIHONCD = vRecordS04T001T.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
         , JHTT.KOSHASHOPCD    = vRecordS04T001T.KOSHASHOPCD    -- 更新者ショップコード
         , JHTT.KOSHACD        = vRecordS04T001T.KOSHACD        -- 更新者コード
         , JHTT.KSHNSYSTMYMD   = vRecordS04T001T.KSHNSYSTMYMD   -- 更新システム日付
         , JHTT.KSHNSYSTMJIKAN = vRecordS04T001T.KSHNSYSTMJIKAN -- 更新システム時間
         , JHTT.KSHNUNYOYMD    = vRecordS04T001T.KSHNUNYOYMD    -- 更新運用日付
         , JHTT.KSHNIPADRSE    = vRecordS04T001T.KSHNIPADRSE    -- 更新ＩＰアドレス
         , JHTT.KSHNPGMID      = vRecordS04T001T.KSHNPGMID      -- 更新プログラムＩＤ
     WHERE JHTT.APKIHONCD  = vRecordS04T001T.APKIHONCD
       AND JHTT.SHOPCD     = vRecordS04T001T.SHOPCD
       AND JHTT.ACPTDNPYNO = vRecordS04T001T.ACPTDNPYNO
       AND JHTT.EDANO      = vRecordS04T001T.EDANO
    ;
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20030, SQLERRM || '（受託）受注配達テーブルを更新時エラーが発生しました。');
  END updateDnpy;
  
  /*****************************************************************************/
  /* プロシージャ名：委託新規受注更新                                              */
  /* 引数           ：iApkihoncd          ＡＰ基本コード                         */
  /*                ：iShopcd             ショップコード                         */
  /*                ：iAcptdnpyno         受注伝票番号                           */
  /*                ：iEdano              枝番号                                */
  /*****************************************************************************/
  PROCEDURE updateItaku( iApkihoncd    IN VARCHAR2 --ＡＰ基本コード
                       , iShopcd       IN VARCHAR2 --ショップコード
                       , iAcptdnpyno   IN VARCHAR2 --受注伝票番号
                       , iEdano        IN VARCHAR2 --枝番号
  ) IS
  BEGIN 
    UPDATE S04T001T JHTT
       SET JHTT.ITAKUAITEACPTDNPYNO = vRecordS04T001T.ACPTDNPYNO -- 委託相手受託伝票番号
         , JHTT.ITAKUAITEEDANO      = vRecordS04T001T.EDANO      -- 委託相手枝番号
         , JHTT.ITAKUAITEAPKIHONCD  = vRecordS04T001T.APKIHONCD  -- 委託相手ＡＰ基本コード
         , JHTT.ITAKUAITESHOPCD     = vRecordS04T001T.SHOPCD     -- 委託相手ショップコード
         /* 更新者情報：更新しない */ 
    WHERE JHTT.APKIHONCD  = iApkihoncd
      AND JHTT.SHOPCD     = iShopcd
      AND JHTT.ACPTDNPYNO = iAcptdnpyno
      AND JHTT.EDANO      = iEdano
      AND JHTT.YUKOKBN    = '1'
    ; 
    UPDATE S04T002T JHMT
       SET JHMT.ITAKUAITEACPTDNPYNO = vRecordS04T001T.ACPTDNPYNO -- 委託相手受託伝票番号
         , JHMT.ITAKUAITEEDANO      = vRecordS04T001T.EDANO      -- 委託相手枝番号
         , JHMT.ITAKUAITEAPKIHONCD  = vRecordS04T001T.APKIHONCD  -- 委託相手ＡＰ基本コード
         , JHMT.ITAKUAITESHOPCD     = vRecordS04T001T.SHOPCD     -- 委託相手ショップコード
         /* 更新者：更新しない */
    WHERE JHMT.APKIHONCD  = iApkihoncd
      AND JHMT.SHOPCD     = iShopcd
      AND JHMT.ACPTDNPYNO = iAcptdnpyno
      AND JHMT.EDANO      = iEdano
      AND JHMT.YUKOKBN    = '1'
    ;
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20031, SQLERRM || '委託データ更新時エラーが発生しました。');
  END updateItaku;
  
  /****************************************************************************/
  /* ファンクション名 ：作業グループコード取得処理                            */
  /* 引数             ：iApKihoncd           ＡＰ基本コード                   */
  /*                  ：iShopcd              ショップコード                   */
  /*                  ：iDlvkyotencd         配達拠点コード                   */
  /*                  ：iDlvareacd           配達エリアコード                 */
  /* return           ：returnSagyogroupcd   作業グループコード               */
  /****************************************************************************/
  FUNCTION getSagyogroupcd( iApKihoncd   IN VARCHAR2
                          , iShopcd      IN VARCHAR2
                          , iDlvkyotencd IN VARCHAR2
                          , iDlvareacd   IN VARCHAR2
  ) RETURN S02M302T.SAGYOGROUPCD%TYPE IS
  returnSagyogroupcd S02M302T.SAGYOGROUPCD%TYPE;

  BEGIN
    SELECT DAGM.SAGYOGROUPCD
      INTO returnSagyogroupcd
      FROM S02M302T DAGM
     WHERE DAGM.APKIHONCD     = iApKihoncd
       AND DAGM.SHOPCD        = iShopcd
       AND DAGM.DLVRYKYOTENCD = iDlvkyotencd
       AND DAGM.DLVRYAREACD   = iDlvareacd
       AND DAGM.YUKOKBN       = '1'
    ;
    RETURN returnSagyogroupcd;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN NULL;
        WHEN TOO_MANY_ROWS THEN
            RAISE_APPLICATION_ERROR(-20022, '作業グループが複数見つかりました。');
  END getSagyogroupcd;

  /****************************************************************************/
  /* ファンクション名 ：得意先情報取得処理                                    */
  /* 引数             ：iApKihoncd           ＡＰ基本コード                   */
  /*                  ：iShopcd              ショップコード                   */
  /*                  ：iTokuisakicd         得意先コード                     */
  /* return           ：returnTokuisakidata  得意先情報                       */
  /****************************************************************************/
  FUNCTION getTokuisakidata( iApKihoncd     IN VARCHAR2
                            , iShopcd       IN VARCHAR2
                            , iTokuisakicd  IN VARCHAR2
  ) RETURN S02M013T%ROWTYPE IS
  returnTokuisakidata S02M013T%ROWTYPE;

  BEGIN
    SELECT TKSM.*
      INTO returnTokuisakidata
      FROM S02M013T TKSM
     WHERE TKSM.APKIHONCD   = iApKihoncd
       AND TKSM.SHOPCD      = iShopcd
       AND TKSM.TOKUISAKICD = iTokuisakicd
       AND TKSM.YUKOKBN     = '1'
    ;
    RETURN returnTokuisakidata;
  END getTokuisakidata;

  /****************************************************************************/
  /* ファンクション名 ：作業内容区分判定処理                                  */
  /* 引数             ：iAcptkbn             受注区分                         */
  /*                  ：iAcptkbnshs          受注区分詳細                     */
  /* return           ：returnSagyonaiyokbn  作業内容区分                     */
  /****************************************************************************/
  FUNCTION getSagyonaiyokbn( iAcptkbn IN VARCHAR2
                           , iAcptkbnshs IN VARCHAR2
  ) RETURN S03T040T.SAGYONAIYOKBN%TYPE IS
  returnSagyonaiyokbn S03T040T.SAGYONAIYOKBN%TYPE;

  BEGIN
    IF iAcptkbn = '2' AND iAcptkbnshs <> '23' THEN
       returnSagyonaiyokbn := '1';
    ELSIF iAcptkbn = '4' THEN
       returnSagyonaiyokbn := '2';
    ELSIF iAcptkbn = '5' THEN
       returnSagyonaiyokbn := '3';
    ELSIF iAcptkbn = '3' OR (iAcptkbn = '2' AND iAcptkbnshs = '23') THEN
       returnSagyonaiyokbn := '4';
    ELSE
       returnSagyonaiyokbn := '4';
    END IF;
    RETURN returnSagyonaiyokbn;
  END getSagyonaiyokbn;

  /****************************************************************************/
  /* ファンクション名 ：作業予約時間区分判定処理                              */
  /* 引数             ：iDlvrykibojikankbn         配達希望時間区分           */
  /*                  ：iDlvrykibojikan            配達希望時間               */
  /* return           ：returnSagyoyoyakujikankbn  作業予約時間区分           */
  /****************************************************************************/
  FUNCTION getSagyoyoyakujikankbn( iDlvrykibojikankbn IN VARCHAR2
                                 , iDlvrykibojikan IN VARCHAR2
  ) RETURN S03T040T.SAGYOYOYAKUJIKANKBN%TYPE IS
  returnSagyoyoyakujikankbn S03T040T.SAGYOYOYAKUJIKANKBN%TYPE;

  BEGIN
    IF iDlvrykibojikankbn = '1' THEN
       returnSagyoyoyakujikankbn := '1';
    ELSIF iDlvrykibojikankbn IN ('0','2','3','4') THEN
       returnSagyoyoyakujikankbn := '2';
    ELSIF iDlvrykibojikankbn IN ('5','6','8') AND iDlvrykibojikan < '1200' THEN
       returnSagyoyoyakujikankbn := '1';
    ELSE
       returnSagyoyoyakujikankbn := '2';
    END IF;
    RETURN returnSagyoyoyakujikankbn;
  END getSagyoyoyakujikankbn;

  /****************************************************************************/
  /* プロシージャ名  ：作業予約データの登録処理                               */
  /* 引数            ：header_rec            受注配達伝票データ               */
  /*                 ：iSagyogroupcd         作業グループコード               */
  /*                 ：iTokuisakidata        得意先情報                       */
  /*                 ：iSagyonaiyokbn        作業内容区分                     */
  /*                 ：iSagyoyoyakujikankbn  作業予約時間区分                 */
  /****************************************************************************/
  PROCEDURE insertSagyoyoyaku( header_rec           IN S04T001T%ROWTYPE                  -- 受注配達伝票データ
                             , iSagyogroupcd        IN VARCHAR2                          -- 作業グループコード
                             , iTokuisakidata       IN S02M013T%ROWTYPE                  -- 得意先情報
                             , iSagyonaiyokbn       IN S03T040T.SAGYONAIYOKBN%TYPE       -- 作業内容区分
                             , iSagyoyoyakujikankbn IN S03T040T.SAGYOYOYAKUJIKANKBN%TYPE -- 作業予約時間区分
  ) IS

  /* 変数宣言 */
  v_id  S03T040T.SAGYOYOYAKUCD%TYPE;   -- 作業予約番号採番

  BEGIN
    -- 作業予約コードを採番
    Z1804B01.getNoSagyoyoyaku(v_id);
    INSERT INTO S03T040T(SAGYOYOYAKUCD
                       , SAGYOGROUPCD
                       , MOUSHIKOMIDD
                       , MOUSHIKOMIJIKAN
                       , TNSHAAPKIHONCD
                       , TNSHASHOPCD
                       , RCPTTNSHACD
                       , SAGYONAIYOKBN
                       , SAGYOYOYAKUDD
                       , SAGYOYOYAKUJIKANKBN
                       , TOKUISAKIAPKIHONCD
                       , TOKUISAKISHOPCD
                       , TOKUISAKICD
                       , KARITOKUISAKINMN1
                       , KARITOKUISAKINMN2
                       , KARITOKUISAKIKEISHOKBN
                       , KARITOKUISAKINMKANAN1
                       , KARITOKUISAKINMKANAN2
                       , KARIYUBINNO
                       , KARIADRSN1
                       , KARIADRSN2
                       , KARIADRSN3
                       , KARIRENRAKUSAKINOKBNN1
                       , KARIRENRAKUSAKINON1
                       , KARIRENRAKUSAKINOKBNN2
                       , KARIRENRAKUSAKINON2
                       , KARIMAILADRSE
                       , ACPTDNPYNO
                       , ACPTDNPYEDANO
                       , RCPTKBN
                       , YOYAKUKBN
                       , YUKOKBN
                       , TOSHAAPKIHONCD
                       , TOSHASHOPCD
                       , TOSHACD
                       , INPTSYSTMYMD
                       , INPTSYSTMJIKAN
                       , INPTUNYOYMD
                       , INPTIPADRSE
                       , INPTPGMID
                       , KOSHAAPKIHONCD
                       , KOSHASHOPCD
                       , KOSHACD
                       , KSHNSYSTMYMD
                       , KSHNSYSTMJIKAN
                       , KSHNUNYOYMD
                       , KSHNIPADRSE
                       , KSHNPGMID
    ) VALUES (v_id
            -- 作業・申込・予約
            , iSagyogroupcd                             -- 作業グループコード
            , header_rec.INPTSYSTMYMD                   -- 申込日
            , SUBSTR(header_rec.INPTSYSTMJIKAN, 1, 2)   -- 申込時間
            , header_rec.TOSHAAPKIHONCD                 -- 担当者ＡＰ基本コード
            , header_rec.TOSHASHOPCD                    -- 担当者ショップコード
            , header_rec.TOSHACD                        -- 受付担当者コード
            , iSagyonaiyokbn                            -- 作業内容区分
            , header_rec.DLVRYYTIDD                     -- 作業予約日
            , iSagyoyoyakujikankbn                      -- 作業予約時間区分

            -- 顧客（仮）・連絡先
            , iTokuisakidata.APKIHONCD           -- 得意先ＡＰ基本コード
            , iTokuisakidata.SHOPCD              -- 得意先ショップコード
            , iTokuisakidata.TOKUISAKICD         -- 得意先コード
            , iTokuisakidata.TOKUISAKINMN1       -- （仮登録）得意先名称１
            , iTokuisakidata.TOKUISAKINMN2       -- （仮登録）得意先名称２
            , iTokuisakidata.TOKUISAKIKEISHOKBN  -- （仮登録）得意先敬称区分
            , iTokuisakidata.TOKUISAKINMKANAN1   -- （仮登録）得意先名称カナ１
            , iTokuisakidata.TOKUISAKINMKANAN2   -- （仮登録）得意先名称カナ２
            , iTokuisakidata.YUBINNO             -- （仮登録）郵便番号
            , iTokuisakidata.ADRSN1              -- （仮登録）住所１
            , iTokuisakidata.ADRSN2              -- （仮登録）住所２
            , iTokuisakidata.ADRSN3              -- （仮登録）住所３
            , iTokuisakidata.RENRAKUSAKINOKBNN1  -- （仮登録）連絡先番号区分１
            , iTokuisakidata.RENRAKUSAKINON1     -- （仮登録）連絡先番号１
            , iTokuisakidata.RENRAKUSAKINOKBNN2  -- （仮登録）連絡先番号区分２
            , iTokuisakidata.RENRAKUSAKINON2     -- （仮登録）連絡先番号２
            , iTokuisakidata.MAILADRSE           -- （仮登録）メールアドレス
            , header_rec.ACPTDNPYNO              -- 受注伝票番号
            , header_rec.EDANO                   -- 受注伝票枝番号

            -- 管理区分
            , '1'  -- 受付区分
            , '1'  -- 予約区分
            , '1'  -- 有効区分

            -- 更新者情報
            , header_rec.TOSHAAPKIHONCD -- 登録者ＡＰ基本コード
            , header_rec.TOSHASHOPCD    -- 登録者ショップコード
            , header_rec.TOSHACD        -- 登録者コード
            , header_rec.INPTSYSTMYMD   -- 登録システム日付
            , header_rec.INPTSYSTMJIKAN -- 登録システム時間
            , header_rec.INPTUNYOYMD    -- 登録運用日付
            , header_rec.INPTIPADRSE    -- 登録ＩＰアドレス
            , header_rec.INPTPGMID      -- 登録プログラムＩＤ
            , header_rec.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
            , header_rec.KOSHASHOPCD    -- 更新者ショップコード
            , header_rec.KOSHACD        -- 更新者コード
            , header_rec.KSHNSYSTMYMD   -- 更新システム日付
            , header_rec.KSHNSYSTMJIKAN -- 更新システム時間
            , header_rec.KSHNUNYOYMD    -- 更新運用日付
            , header_rec.KSHNIPADRSE    -- 更新ＩＰアドレス
            , header_rec.KSHNPGMID      -- 更新プログラムＩＤ
    );
    EXCEPTION
      WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR( -20030, SQLERRM || '設引予約テーブル登録時エラーが発生しました。');
  END insertSagyoyoyaku;

  /****************************************************************************/
  /* プロシージャ名  ：作業予約データの受注伝票番号更新処理                   */
  /* 引数（IN）      ：header_rec     受注配達伝票データ                      */
  /*                 ：iSagyogroupcd  作業グループコード                      */
  /*                 ：iSagyonaiyokbn 作業内容区分                            */
  /* 引数（OUT）     ：oUpdcnt        更新件数                                */
  /****************************************************************************/
  PROCEDURE updateSagyoyoyakuAcptdnpyno( header_rec     IN S04T001T%ROWTYPE            -- 受注配達伝票データ
                                       , iSagyogroupcd  IN VARCHAR2                    -- 作業グループコード
                                       , iSagyonaiyokbn IN S03T040T.SAGYONAIYOKBN%TYPE -- 作業内容区分
                                       , oUpdcnt        OUT NUMBER                     -- 更新件数
  ) IS

  BEGIN
    UPDATE S03T040T SGYT
       SET SGYT.ACPTDNPYNO    = header_rec.ACPTDNPYNO      -- 受注伝票番号
         , SGYT.ACPTDNPYEDANO = header_rec.EDANO           -- 受注伝票枝番号
         /* 更新者 */
         , SGYT.KOSHAAPKIHONCD = header_rec.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
         , SGYT.KOSHASHOPCD    = header_rec.KOSHASHOPCD    -- 更新者ショップコード
         , SGYT.KOSHACD        = header_rec.KOSHACD        -- 更新者コード
         , SGYT.KSHNSYSTMYMD   = header_rec.KSHNSYSTMYMD   -- 更新システム日付
         , SGYT.KSHNSYSTMJIKAN = header_rec.KSHNSYSTMJIKAN -- 更新システム時間
         , SGYT.KSHNUNYOYMD    = header_rec.KSHNUNYOYMD    -- 更新運用日付
         , SGYT.KSHNIPADRSE    = header_rec.KSHNIPADRSE    -- 更新ＩＰアドレス
         , SGYT.KSHNPGMID      = header_rec.KSHNPGMID      -- 更新プログラムＩＤ
     WHERE SGYT.TOKUISAKIAPKIHONCD                   = header_rec.APKIHONCD
       AND SGYT.TOKUISAKISHOPCD                      = header_rec.SHOPCD
       AND SGYT.TOKUISAKICD                          = header_rec.TOKUISAKICD
       AND SGYT.SAGYONAIYOKBN                        = iSagyonaiyokbn
       AND SGYT.SAGYOGROUPCD                         = iSagyogroupcd
       AND NVL(SGYT.SAGYOYOYAKUDD, SGYT.SAGYOKIBODD) = header_rec.DLVRYYTIDD
       AND SGYT.YOYAKUKBN                            = '1'
       AND SGYT.YUKOKBN                              = '1'
    ;
    oUpdcnt := SQL%ROWCOUNT;
    EXCEPTION
      WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR( -20030, SQLERRM || '設引予約テーブル更新時エラーが発生しました。');
  END updateSagyoyoyakuAcptdnpyno;

  /****************************************************************************/
  /* プロシージャ名  ：作業予約データ更新処理                                 */
  /* 引数            ：header_rec            受注配達伝票データ               */
  /*                 ：iSagyoyoyakujikankbn  作業予約時間区分                 */
  /****************************************************************************/
  PROCEDURE updateSagyoyoyaku( header_rec           IN S04T001T%ROWTYPE                  -- 受注配達伝票データ
                             , iSagyoyoyakujikankbn IN S03T040T.SAGYOYOYAKUJIKANKBN%TYPE -- 作業予約時間区分
  ) IS

  BEGIN
    UPDATE S03T040T SGYT
       SET SGYT.SAGYOYOYAKUDD       = header_rec.DLVRYYTIDD -- 作業予約日
         , SGYT.SAGYOYOYAKUJIKANKBN = iSagyoyoyakujikankbn  -- 作業予約時間区分
         /* 更新者 */
         , SGYT.KOSHAAPKIHONCD = header_rec.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
         , SGYT.KOSHASHOPCD    = header_rec.KOSHASHOPCD    -- 更新者ショップコード
         , SGYT.KOSHACD        = header_rec.KOSHACD        -- 更新者コード
         , SGYT.KSHNSYSTMYMD   = header_rec.KSHNSYSTMYMD   -- 更新システム日付
         , SGYT.KSHNSYSTMJIKAN = header_rec.KSHNSYSTMJIKAN -- 更新システム時間
         , SGYT.KSHNUNYOYMD    = header_rec.KSHNUNYOYMD    -- 更新運用日付
         , SGYT.KSHNIPADRSE    = header_rec.KSHNIPADRSE    -- 更新ＩＰアドレス
         , SGYT.KSHNPGMID      = header_rec.KSHNPGMID      -- 更新プログラムＩＤ
     WHERE SGYT.TOKUISAKIAPKIHONCD = header_rec.APKIHONCD
       AND SGYT.TOKUISAKISHOPCD    = header_rec.SHOPCD
       AND SGYT.ACPTDNPYNO         = header_rec.ACPTDNPYNO
       AND SGYT.ACPTDNPYEDANO      = header_rec.EDANO
       AND SGYT.YUKOKBN            = '1'
    ;
    EXCEPTION
      WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR( -20030, SQLERRM || '設引予約テーブル更新時エラーが発生しました。');
  END updateSagyoyoyaku;

  /****************************************************************************/
  /* プロシージャ名  ：作業予約データの受注関連情報をクリア更新処理           */
  /* 引数            ：header_rec 受注配達伝票データ                          */
  /****************************************************************************/
  PROCEDURE updateSagyoyoyakuClear( header_rec     IN S04T001T%ROWTYPE            -- 受注配達伝票データ
  ) IS

  BEGIN
    UPDATE S03T040T SGYT
       SET SGYT.SAGYOYOYAKUDD       = NULL -- 作業予約日
         , SGYT.SAGYOYOYAKUJIKANKBN = NULL -- 作業予約時間区分
         , SGYT.ACPTDNPYNO          = NULL -- 受注伝票番号
         , SGYT.ACPTDNPYEDANO       = NULL -- 受注伝票枝番号
         , SGYT.SAGYOKIBODD         = NULL -- 作業希望日
         , SGYT.SAGYOKIBOJIKANKBN   = NULL -- 作業希望時間区分
         /* 更新者 */
         , SGYT.KOSHAAPKIHONCD = header_rec.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
         , SGYT.KOSHASHOPCD    = header_rec.KOSHASHOPCD    -- 更新者ショップコード
         , SGYT.KOSHACD        = header_rec.KOSHACD        -- 更新者コード
         , SGYT.KSHNSYSTMYMD   = header_rec.KSHNSYSTMYMD   -- 更新システム日付
         , SGYT.KSHNSYSTMJIKAN = header_rec.KSHNSYSTMJIKAN -- 更新システム時間
         , SGYT.KSHNUNYOYMD    = header_rec.KSHNUNYOYMD    -- 更新運用日付
         , SGYT.KSHNIPADRSE    = header_rec.KSHNIPADRSE    -- 更新ＩＰアドレス
         , SGYT.KSHNPGMID      = header_rec.KSHNPGMID      -- 更新プログラムＩＤ
     WHERE SGYT.TOKUISAKIAPKIHONCD = header_rec.APKIHONCD
       AND SGYT.TOKUISAKISHOPCD    = header_rec.SHOPCD
       AND SGYT.ACPTDNPYNO         = header_rec.ACPTDNPYNO
       AND SGYT.ACPTDNPYEDANO      = header_rec.EDANO
       AND SGYT.SAGYONAIYOKBN      = '1' -- 新規設置
       AND SGYT.YUKOKBN            = '1'
    ;
    EXCEPTION
      WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR( -20030, SQLERRM || '設引予約テーブル更新時エラーが発生しました。');
  END updateSagyoyoyakuClear;

  /****************************************************************************/
  /* プロシージャ名  ：作業予約データの削除処理                               */
  /* 引数            ：header_rec 受注配達伝票データ                          */
  /****************************************************************************/
  PROCEDURE deleteSagyoyoyaku( header_rec  IN S04T001T%ROWTYPE -- 受注配達伝票データ
  ) IS

  BEGIN
    UPDATE S03T040T SGYT
       SET SGYT.YUKOKBN = '0' -- 有効区分
         /* 更新者 */
         , SGYT.KOSHAAPKIHONCD = header_rec.KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
         , SGYT.KOSHASHOPCD    = header_rec.KOSHASHOPCD    -- 更新者ショップコード
         , SGYT.KOSHACD        = header_rec.KOSHACD        -- 更新者コード
         , SGYT.KSHNSYSTMYMD   = header_rec.KSHNSYSTMYMD   -- 更新システム日付
         , SGYT.KSHNSYSTMJIKAN = header_rec.KSHNSYSTMJIKAN -- 更新システム時間
         , SGYT.KSHNUNYOYMD    = header_rec.KSHNUNYOYMD    -- 更新運用日付
         , SGYT.KSHNIPADRSE    = header_rec.KSHNIPADRSE    -- 更新ＩＰアドレス
         , SGYT.KSHNPGMID      = header_rec.KSHNPGMID      -- 更新プログラムＩＤ
     WHERE SGYT.TOKUISAKIAPKIHONCD = header_rec.APKIHONCD
       AND SGYT.TOKUISAKISHOPCD    = header_rec.SHOPCD
       AND SGYT.ACPTDNPYNO         = header_rec.ACPTDNPYNO
       AND SGYT.ACPTDNPYEDANO      = header_rec.EDANO
       AND SGYT.SAGYONAIYOKBN      <> '1' -- 新規設置
       AND SGYT.YUKOKBN            = '1'
    ;
    EXCEPTION
      WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR( -20030, SQLERRM || '設引予約テーブル更新時エラーが発生しました。');
  END deleteSagyoyoyaku;

  /****************************************************************************/
  /* プロシージャ名  ：作業予約データの登録更新処理                           */
  /* 引数            ：header_rec 受注配達伝票データ                          */
  /*                 ：iMode       処理モード                                 */
  /****************************************************************************/
  PROCEDURE insupdSagyoyoyaku( header_rec  IN S04T001T%ROWTYPE -- 受注配達伝票データ
                             , iMode       IN VARCHAR2         -- 処理モード
  ) IS

  /* 変数宣言 */
  wSagyogroupcd       S02M302T.SAGYOGROUPCD%TYPE := NULL;       -- 作業グループコード
  wTokuisakidata      S02M013T%ROWTYPE;                         -- 得意先情報
  wUpdcnt             NUMBER := 0;                              -- 更新件数
  wSagyoyoyakuriyokbn S02M304T.SAGYOYOYAKURIYOKBN%TYPE := NULL; -- 作業グループコード

  BEGIN
    BEGIN
      -- 作業予約ショップマスタ取得 
      SELECT
        SSPM.SAGYOYOYAKURIYOKBN -- 作業予約利用区分
      INTO wSagyoyoyakuriyokbn
      FROM
        S02M304T SSPM 
      WHERE
        SSPM.APKIHONCD = header_rec.APKIHONCD
        AND SSPM.SHOPCD = header_rec.SHOPCD
        AND SSPM.YUKOKBN = '1'
      ;
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          wSagyoyoyakuriyokbn := '0';
    END;
    -- 作業予約を利用しない場合は処理終了
    IF wSagyoyoyakuriyokbn = '0' THEN
       RETURN;
    END IF;

    -- 新規受注の場合
    IF iMode = '0' THEN
       -- 作業グループコード取得
       wSagyogroupcd := getSagyogroupcd( header_rec.DLVRYKANKATSUAPKIHONCD
                                       , header_rec.DLVRYKANKATSUSHOPCD
                                       , header_rec.DLVRYKYOTENCD
                                       , header_rec.DLVRYAREACD
                                       );
       -- 作業グループが設定されている場合のみ作業予約の受注伝票番号更新処理を実施
       IF wSagyogroupcd IS NOT NULL THEN
          -- 作業予約の受注伝票番号を更新
          updateSagyoyoyakuAcptdnpyno( header_rec
                                     , wSagyogroupcd
                                     , getSagyonaiyokbn(header_rec.ACPTKBN, header_rec.ACPTKBNSHS)
                                     , wUpdcnt
                                     );
       END IF;
       -- 作業グループが未設定、または更新対象がなければ作業予約を登録
       IF wSagyogroupcd IS NULL OR wUpdcnt = 0 THEN
          -- 得意先情報取得
          wTokuisakidata := getTokuisakidata( header_rec.APKIHONCD
                                            , header_rec.SHOPCD
                                            , header_rec.TOKUISAKICD
                                            );
          -- 作業予約を登録
          insertSagyoyoyaku( header_rec
                           , wSagyogroupcd
                           , wTokuisakidata
                           , getSagyonaiyokbn(header_rec.ACPTKBN, header_rec.ACPTKBNSHS)
                           , getSagyoyoyakujikankbn(header_rec.DLVRYKIBOJIKANKBN, header_rec.DLVRYKIBOJIKAN)
                           );
       END IF;
    -- 更新の場合
    ELSIF iMode = '1' THEN
       -- 作業予約データを更新
       updateSagyoyoyaku( header_rec
                        , getSagyoyoyakujikankbn(header_rec.DLVRYKIBOJIKANKBN, header_rec.DLVRYKIBOJIKAN)
                        );
    -- キャンセル、削除の場合
    ELSIF iMode IN ( '2', '3' ) THEN
       -- 新規設置の作業予約データは設置日調整中状態となるよう受注関連情報をクリア
       updateSagyoyoyakuClear(header_rec);
       -- 新規設置以外の作業予約データは論理削除
       deleteSagyoyoyaku(header_rec);
    END IF;
  END insupdSagyoyoyaku;

  /****************************************************************************/
  /* プロシージャ名  ：本部受注データ連携処理                                       */
  /* 引数           ：iApkihoncd          ＡＰ基本コード                         */
  /*                ：iShopcd             ショップコード                         */
  /*                ：iAcptdnpyno         受注伝票番号                           */
  /*                ：iEdano              枝番号                                */
  /*                ：iMode               処理モード                            */
  /****************************************************************************/
  PROCEDURE HonbuJuchuDatRenkei( iApkihoncd    IN VARCHAR2 --ＡＰ基本コード
                               , iShopcd       IN VARCHAR2 --ショップコード
                               , iAcptdnpyno   IN VARCHAR2 --受注伝票番号
                               , iEdano        IN NUMBER   --枝番号
                               , iMode         IN VARCHAR2 --処理モード
  ) IS
  /* 変数宣言 */
  wZaikokanrikbn              VARCHAR2(1) := NULL;  -- 在庫管理区分
  wSaishuacptdd               VARCHAR2(8) := NULL;  -- 最終受注日
  
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
    IF iAcptdnpyno IS NULL THEN
      RAISE_APPLICATION_ERROR(-20003,
                              'パラメータ.受注伝票番号  が設定されていません。');
    END IF;
    IF iEdano IS NULL THEN
      RAISE_APPLICATION_ERROR(-20004,
                              'パラメータ.枝番号  が設定されていません。');
    END IF;
    IF iMode IS NULL THEN
      RAISE_APPLICATION_ERROR(-20005,
                              'パラメータ.処理モード  が設定されていません。');
    END IF;
    IF iMode NOT IN ( '0', '1', '2', '3' ) THEN 
       RAISE_APPLICATION_ERROR(-20007,
                              'パラメータ.処理モード  が無効です。');
    END IF;
    
    -- 共通変数削除
    vRecordS04T001T := NULL;
    vRecordS04T002T := NULL;
    
    -- （委託）受注伝票取得
    SELECT * 
      INTO vRecordS04T001T
      FROM S04T001T JHTT
     WHERE JHTT.APKIHONCD  = iApkihoncd  -- ＡＰ基本コード
       AND JHTT.SHOPCD     = iShopcd     -- ショップコード
       AND JHTT.ACPTDNPYNO = iAcptdnpyno -- 受注伝票番号
       AND JHTT.EDANO      = iEdano      -- 枝番号
    FOR UPDATE 
    ;
    
    -- 明細行処理前の受注伝票設定：委受託情報、明細共有項目設定、合計値クリア
    setDnpyBeforeMeisai( iApkihoncd, iShopcd, iAcptdnpyno, iEdano, iMode, wZaikokanrikbn, wSaishuacptdd ); 
    
-- インボイス対応追加 start ABI
    -- ショップマスタ取得 
    SELECT
      SHPM.INVOICSHIKOYMD     -- インボイス施行日
    INTO vInvoicshikoymd
    FROM
      S02M046T SHPM 
    WHERE
      SHPM.APKIHONCD = vRecordS04T001T.APKIHONCD
      AND SHPM.SHOPCD = vRecordS04T001T.SHOPCD
      AND SHPM.YUKOKBN = '1'
    ;
-- インボイス対応追加 end ABI

    -- 受注キャンセル処理
    IF iMode <> '0' THEN 
       processCancel( iMode, wZaikokanrikbn );
    END IF;
    
    -- 新規受注／更新の場合
    IF iMode = '0' OR iMode = '1' THEN 
       processMeisai( iApkihoncd, iShopcd, iAcptdnpyno, iEdano, iMode, wZaikokanrikbn ); -- 明細行処理
       setDnpyAfterMeisai;  -- 明細行処理後の受注伝票設定：引当先、固定値セット
       
       IF iMode = '0' THEN -- 新規受注
          insertDnpy; 
          updateItaku(iApkihoncd, iShopcd, iAcptdnpyno, iEdano);
       END IF;
       IF iMode = '1' THEN -- 更新
          updateDnpy;
          updateItaku(iApkihoncd, iShopcd, iAcptdnpyno, iEdano);
       END IF;
       
       -- アラームテーブルの更新
       IF vRecordS04T001T.DLVRYKYOTENCD IS NOT NULL AND vRecordS04T001T.DLVRYTNSHACD IS NOT NULL THEN -- 配達拠点・配達担当者設定あり
          updateTnshaAlarm( vRecordS04T001T, 1, vRecordS04T001T.KSHNUNYOYMD ); -- 担当者受注アラームテーブル
       END IF;
       IF vRecordS04T001T.DLVRYKYOTENCD IS NOT NULL AND vRecordS04T001T.DLVRYAREACD IS NOT NULL THEN -- 配達拠点・配達エリア設定あり
          updateAreaAlarm( vRecordS04T001T, 1, vRecordS04T001T.KSHNUNYOYMD );  -- 配達エリア受注アラームテーブル
       END IF;
       -- 得意先の更新
       updateTokuisaki( vRecordS04T001T, FALSE, iMode, wSaishuacptdd );
       
    END IF;
    
    -- 作業予約データを更新（受注区分：1（商品受注）の 場合はスキップ）
    IF vRecordS04T001T.ACPTKBN <> '1' THEN
       insupdSagyoyoyaku(vRecordS04T001T, iMode);
    END IF;

    COMMIT; 
    
    EXCEPTION
     WHEN OTHERS THEN 
      ROLLBACK;
      RAISE_APPLICATION_ERROR(-20705, SQLERRM);
      
  END HonbuJuchuDatRenkei;

END C0804B01;
/
exit;
