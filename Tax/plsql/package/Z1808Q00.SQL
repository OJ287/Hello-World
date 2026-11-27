CREATE OR REPLACE PACKAGE "Z1808B01"
/*******************************************************************************
 * 機能名           ：消費税金額計算
 *
 ******************************************************************************/
 AS

  /*****************************************************************************/
  /* ユーザー定義例外宣言                                                      */
  /*****************************************************************************/
  APP_EXCEPTION EXCEPTION; -- ユーザー定義例外の簡単な説明

  /*****************************************************************************/
  /* ファンクション名 ：消費税金額計算                                         */
  /* 使用方法         ：①税抜金額（税抜単価）より消費税額を算出する場合、     */
  /*                  ：iSimpleFgに1を、iKinに値をセットする                   */
  /*                  ：税抜単価、数量から消費税金額を算出する場合、           */
  /*                  ：iSimpleFgに0を、iKin・iSuryoに値をセットする           */
  /*                  ：②税計算区分（内税・外税）を固定で決定したい場合、     */
  /*                  ：引数の税計算区分に値をセット（そうでない場合はNULL）   */
  /*                  ：ただし、単体計算時は常に外税計算となる                 */
  /*                  ：③仕入時計算の場合はiSiiresakicdに値をセットする       */
  /*                  ：（そうでない場合は'000000'をセット）				   */
  /* 引数             ：iSimpleFg            単体計算フラグ (0:しない　1:する) */
  /*                  ：iTaxsetteikbn        税設定区分                        */
  /*                  ：iTaxkbn              税区分                            */
  /*                  ：iTaxKeisanKbn        税計算区分 (固定しない場合はNULL) */
  /*                  ：iKirikaebi           切り替え日                        */
  /*                  ：iApKihoncd           AP基本コード                      */
  /*                  ：iShopcd              ショップコード                    */
  /*                  ：iSiiresakicd         仕入先コード                      */
  /*                  ：iSuryo               数量                              */
  /*                  ：iKin                 税抜金額（税抜単価）              */
  /* return           ：returnShohitaxk      消費税額                          */
  /*****************************************************************************/
  FUNCTION SyohizeiKeisan(iSimpleFg   		IN VARCHAR2,
-- ■:1 Base)Arai 2014-01-16
						  iTaxsetteikbn     IN VARCHAR2,
-- ■:2 Base)Arai 2014-01-16
						  iTaxkbn      		IN VARCHAR2,
						  iTaxKeisankbn     IN VARCHAR2,
                          iKirikaebi   		IN VARCHAR2,
                          iApKihoncd   		IN VARCHAR2,
                          iShopcd      		IN VARCHAR2,
                          iSiiresakicd 		IN VARCHAR2,
                          iSuryo	 		IN VARCHAR2,
                          iKin         		IN VARCHAR2) RETURN NUMBER;

  /*****************************************************************************/
  /* ファンクション名 ：端数計算処理                                           */
  /* 引数             ：iValue               計算対象値						   */
  /*                  ：iHasuKeisankbn       端数計算区分                      */
  /* return           ：			         計算結果値                        */
  /*****************************************************************************/
  FUNCTION roundValue (iValue	   	   IN NUMBER,
					   iHasuKeisankbn  IN VARCHAR2) RETURN NUMBER;


/******************************************************************************/
  /* ファンクション名 ：セット商品税計算                                      */
  /* 引数                                                                     */
  /*                  ：iTaxsetteikbn        税設定区分                       */
  /*                  ：iTaxKeisanKbn        税計算区分 (固定しない場合はNULL)*/
  /*                  ：iKirikaebi           切り替え日                       */
  /*                  ：iApKihoncd           AP基本コード                     */
  /*                  ：iShopcd              ショップコード                   */
  /*                  ：iSuryo               数量                             */
  /*                  ：iSyohinCd            商品コード                       */
  /*                  ：iDlvrykbn            配達区分                         */
  /*                  ：iKeijokbn            計上区分                         */
  /* return           ：oKidUntTaxSum        セット商品単価消費税             */
  /*                  ：oKidTaxSum           セット商品消費税                 */
  /*****************************************************************************/
  PROCEDURE SetSyohizeiKeisan(
                             iTaxsetteikbn     IN VARCHAR2,
                             iTaxKeisankbn     IN VARCHAR2,
                             iKirikaebi        IN VARCHAR2,
                             iApKihoncd        IN VARCHAR2,
                             iShopcd           IN VARCHAR2,
                             iSuryo            IN VARCHAR2,
                             iSyohinCd         IN VARCHAR2,
                             iDlvrykbn         IN VARCHAR2,
                             iKeijokbn         IN VARCHAR2,
                             oKidUntTaxSum     OUT NUMBER,
                             oKidTaxSum        OUT NUMBER);

END Z1808B01;
/
exit;
