CREATE OR REPLACE PACKAGE      D1502B01
/*******************************************************************************
 * システム名      ：ＡＰ総合管理システム
 * 機能名          ：請求締処理
 *******************************************************************************
   Date       Author         Changes Key                                 Version
   -----------------------------------------------------------------------------
   2022-06-16 Isac)Kanaya    Create                                      1.0.0
 *******************************************************************************/
 AS
  /*****************************************************************************/
  /* 共通変数宣言                                                              */
  /*****************************************************************************/
  vPROGRAM_ID               S07T004T.KSHNPGMID%TYPE DEFAULT 'D1502B01'; -- プログラムID
  vInvoiceExecutionFlg      VARCHAR2(1);                                -- インボイス施行フラグ
  vTaxCalcDivision          S02M046T.SHINTAXKEISANKBNURI%TYPE;          -- 税計算区分
  vNextStep                 S07T005T.PRCSID%TYPE;                       -- 次処理（処理モード）

  -- レコード
  vRecordS07T001T           S07T001T%ROWTYPE;                           -- 請求残高テーブル

  -- カテゴリーコード
  vCategorySeihinSui        S06T002T.LCTGRYCD%TYPE;                     -- 商品大分類（製品水）
  vCategoryShnRental        S06T002T.LCTGRYCD%TYPE;                     -- 商品大分類（商品レンタル料）
  vCategorySvrRental        S06T002T.LCTGRYCD%TYPE;                     -- 商品大分類（サーバーレンタル料）
  vCategorySvrKanren        S06T002T.LCTGRYCD%TYPE;                     -- 商品大分類（サーバー関連）
  
  -- 商品コード
  vShnCdRegular             S06T002T.SHNCD%TYPE;                        -- 商品コード（製品水：レギュラー）
  vShnCdSlim                S06T002T.SHNCD%TYPE;                        -- 商品コード（製品水：スリム）

  -- 処理状態
  vPRCSJOTAI_COMPLETE       VARCHAR2(1) DEFAULT '0';                    -- 「終了／待機」
  vPRCSJOTAI_START          VARCHAR2(1) DEFAULT '1';                    -- 「処理開始」
  vPRCSJOTAI_RUNNING        VARCHAR2(1) DEFAULT '2';                    -- 「実行中」
  vPRCSJOTAI_RECOV_WAIT     VARCHAR2(1) DEFAULT '6';                    -- 「リカバリ実行待」
  vPRCSJOTAI_RECOV_RUNNING  VARCHAR2(1) DEFAULT '7';                    -- 「リカバリ実行中」
  vPRCSJOTAI_ABORT          VARCHAR2(1) DEFAULT '9';                    -- 「エラー」
  
  -- 処理ID
  vPRCSID_D1502010          S07T005T.PRCSID%TYPE DEFAULT 'D1502010';    -- 「ワーククリア処理」
  vPRCSID_D1502100          S07T005T.PRCSID%TYPE DEFAULT 'D1502100';    -- 「請求締解除処理」
  vPRCSID_D1502020          S07T005T.PRCSID%TYPE DEFAULT 'D1502020';    -- 「得意先抽出処理」
  vPRCSID_D1502040          S07T005T.PRCSID%TYPE DEFAULT 'D1502040';    -- 「請求締処理」
  vPRCSID_D1502060          S07T005T.PRCSID%TYPE DEFAULT 'D1502060';    -- 「マスタ反映処理」
  vPRCSID_D1502999          S07T005T.PRCSID%TYPE DEFAULT 'D1502999';    -- 「処理終了判定」

  /*****************************************************************************/
  /* ユーザー定義例外宣言                                                      */
  /*****************************************************************************/
  APP_EXCEPTION EXCEPTION; -- APPLICATION_ERROR
  PRAGMA EXCEPTION_INIT(APP_EXCEPTION, -20999);

  /*****************************************************************************/
  /* プロシージャ名  ：請求締実行チェック＆開始                                */
  /* 引数            ：iApKihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iUserApKihoncd        担当者ＡＰ基本コード              */
  /*                 ：iUserShopcd           担当者ショップコード              */
  /*                 ：iTnshacd              担当者コード                      */
  /*                 ：iIpadrse              ＩＰアドレス                      */
  /*                 ：iIraidd               依頼日                            */
  /*                 ：iIraijikan            依頼時間                          */
  /*                 ：iSkyshimedd           請求締日                          */
  /*                 ：iSyoriMode            処理モード（1:請求締め 2:締解除） */
  /*****************************************************************************/
  PROCEDURE SeikyushimeCheckAndGo(iApKihoncd     IN VARCHAR2
                                 ,iShopcd        IN VARCHAR2
                                 ,iUserApKihoncd IN VARCHAR2
                                 ,iUserShopcd    IN VARCHAR2
                                 ,iTnshacd       IN VARCHAR2
                                 ,iIpadrse       IN VARCHAR2
                                 ,iIraidd        IN VARCHAR2
                                 ,iIraijikan     IN VARCHAR2
                                 ,iSkyshimedd    IN VARCHAR2
                                 ,iSyoriMode     IN VARCHAR2
  );

  /*****************************************************************************/
  /* プロシージャ名  ：請求締処理                                              */
  /* 引数            ：iApKihoncd            AP基本コード                      */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iUserApKihoncd        担当者AP基本コード                */
  /*                 ：iUserShopcd           担当者ショップコード              */
  /*                 ：iTnshacd              担当者コード                      */
  /*                 ：iIpadrse              IPアドレス                        */
  /*                 ：iIraidd               依頼日                            */
  /*                 ：iIraijikan            依頼時間                          */
  /*                 ：iSkyshimedd           請求締日                          */
  /*****************************************************************************/
  PROCEDURE Seikyushime(iApKihoncd     IN VARCHAR2
                       ,iShopcd        IN VARCHAR2
                       ,iUserApKihoncd IN VARCHAR2
                       ,iUserShopcd    IN VARCHAR2
                       ,iTnshacd       IN VARCHAR2
                       ,iIpadrse       IN VARCHAR2
                       ,iIraidd        IN VARCHAR2
                       ,iIraijikan     IN VARCHAR2
                       ,iSkyshimedd    IN VARCHAR2
  );

  /*****************************************************************************/
  /* プロシージャ名  ：請求締解除処理                                          */
  /* 引数            ：iApKihoncd            AP基本コード                      */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iUserApKihoncd        担当者AP基本コード                */
  /*                 ：iUserShopcd           担当者ショップコード              */
  /*                 ：iTnshacd              担当者コード                      */
  /*                 ：iIpadrse              IPアドレス                        */
  /*                 ：iIraidd               依頼日                            */
  /*                 ：iIraijikan            依頼時間                          */
  /*                 ：iSkyshimedd           請求締日                          */
  /*****************************************************************************/
  PROCEDURE Seikyukaijo(iApKihoncd     IN VARCHAR2
                       ,iShopcd        IN VARCHAR2
                       ,iUserApKihoncd IN VARCHAR2
                       ,iUserShopcd    IN VARCHAR2
                       ,iTnshacd       IN VARCHAR2
                       ,iIpadrse       IN VARCHAR2
                       ,iIraidd        IN VARCHAR2
                       ,iIraijikan     IN VARCHAR2
                       ,iSkyshimedd    IN VARCHAR2
  );

  /*****************************************************************************/
  /* プロシージャ名  ：請求締解除処理                                          */
  /* 引数            ：iApKihoncd            AP基本コード                      */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iUserApKihoncd        担当者AP基本コード                */
  /*                 ：iUserShopcd           担当者ショップコード              */
  /*                 ：iTnshacd              担当者コード                      */
  /*                 ：iIpadrse              IPアドレス                        */
  /*                 ：iIraidd               依頼日                            */
  /*                 ：iIraijikan            依頼時間                          */
  /*                 ：iSkyshimedd           請求締日                          */
  /*                 ：iSyoriMode            処理モード（1:請求締め 2:締解除） */
  /*                 ：iSkysakicd            請求先コード                      */
  /*                 ：iTokuisakicd          得意先コード                      */
  /*                 ：iUridnpyno            売上伝票番号                      */
  /*                 ：iKaishuhohokbn        回収方法区分                      */
  /*                 ：iSingldnpyflg         単体伝票締めフラグ                */
  /*****************************************************************************/
  PROCEDURE SeikyuShimeTantai(iApKihoncd     IN VARCHAR2
                             ,iShopcd        IN VARCHAR2
                             ,iUserApKihoncd IN VARCHAR2
                             ,iUserShopcd    IN VARCHAR2
                             ,iTnshacd       IN VARCHAR2
                             ,iIpadrse       IN VARCHAR2
                             ,iIraidd        IN VARCHAR2
                             ,iIraijikan     IN VARCHAR2
                             ,iSkyshimedd    IN VARCHAR2
                             ,iSyoriMode     IN VARCHAR2
                             ,iSkysakicd     IN VARCHAR2
                             ,iTokuisakicd   IN VARCHAR2
                             ,iUridnpyno     IN VARCHAR2
                             ,iKaishuhohokbn IN VARCHAR2
                             ,iSingldnpyflg  IN VARCHAR2
  );
  
END D1502B01;
/
exit;
