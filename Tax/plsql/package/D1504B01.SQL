CREATE OR REPLACE PACKAGE       D1504B01 
/*******************************************************************************
 * システム名       ：ＡＰ総合管理システム
 * 機能名           ：滞留更新処理
 *
 *******************************************************************************
   Date       Author         Changes Key                                 Version
   -----------------------------------------------------------------------------
   2022-08-15 Isac           Create                                      1.0.0
 ******************************************************************************/
 AS
  /******************************************************************************/
  /* 共通変数宣言                                                               */
  /******************************************************************************/
  vRecordS02M017T S02M017T%rowtype; -- 請求先マスタ
  vRecordS07T001T S07T001T%rowtype; -- 請求残高テーブル
  cPGMID VARCHAR2(9) DEFAULT 'D1504B01';    --プログラムID
  cMSG   VARCHAR2(2000);                    --内部メッセージ
  /******************************************************************************/
  /* ユーザー定義例外宣言                                                       */
  /******************************************************************************/
  APP_EXCEPTION EXCEPTION; -- APPLICATION_ERROR
  /******************************************************************************/
  /* プロシージャ名 ：滞留更新処理                                              */
  /* 引数           ：iApKihoncd         AP基本コード                           */
  /*                ：iShopcd            ショップコード                         */
  /*                ：iUserApKihoncd     担当者ＡＰ基本コード                   */
  /*                ：iUserShopcd        担当者ショップコード                   */
  /*                ：iTnshacd           担当者コード                           */
  /*                ：iIpadrse           ＩＰアドレス                           */
  /*                ：iIraidd            依頼日  （処理区分＝1の場合必須）      */
  /*                ：iIraijikan         依頼時間（処理区分＝1の場合必須）      */
  /*                ：iPrcsdd            処理日  （請求締日or入金日）           */
  /*                ：iPrcskbn           処理区分（1請求、2入金、4一括入金）    */
  /*                                     （3売上入力は廃止）                    */
  /*                ：iNyknkin           入金金額                               */
  /*                ：iTrgtSkyscd        対象請求先                             */
  /*                ：iShimedydm         処理年月                               */
  /******************************************************************************/
  PROCEDURE Tairyukousin(iApKihoncd     IN VARCHAR2,
                         iShopcd        IN VARCHAR2,
                         iUserApKihoncd IN VARCHAR2,
                         iUserShopcd    IN VARCHAR2,
                         iTnshacd       IN VARCHAR2,
                         iIpadrse       IN VARCHAR2,
                         iIraidd        IN VARCHAR2,
                         iIraijikan     IN VARCHAR2,
                         iPrcsdd        IN VARCHAR2,
                         iPrcskbn       IN VARCHAR2,
                         iNyknkin       IN VARCHAR2,
                         iTrgtSkyscd    IN VARCHAR2,
                         iShimedydm     IN VARCHAR2);
END D1504B01;
/
exit;
