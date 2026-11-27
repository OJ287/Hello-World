CREATE OR REPLACE PACKAGE BODY "Z1808B01"
/*******************************************************************************
 * 機能名           ：消費税金額計算
 *
 ******************************************************************************/
 AS
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
  /*                  ：（そうでない場合は'000000'をセット）                   */
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
						  iTaxkbn           IN VARCHAR2,
						  iTaxKeisankbn     IN VARCHAR2,
                          iKirikaebi   		IN VARCHAR2,
                          iApKihoncd   		IN VARCHAR2,
                          iShopcd      		IN VARCHAR2,
                          iSiiresakicd 		IN VARCHAR2,
                          iSuryo	 		IN VARCHAR2,
                          iKin         		IN VARCHAR2) RETURN NUMBER IS

  	vRecordS02M021T 	  S02M021T%rowtype; --仕入先マスタ
  	vRecordS02M046T 	  S02M046T%rowtype; --ショップマスタ
  	vTaxkbn 		  	  VARCHAR2(1); --税区分
  	vTaxKeisankbn 		  VARCHAR2(1); --税計算区分
  	vTaxHasukbn 		  VARCHAR2(1); --税端数計算区分
  	vKinHasukbn 		  VARCHAR2(1); --金額端数計算区分
  	nTaxRate	 		  NUMBER(3,1); --税率
  	nTankaTax	 		  NUMBER; 	   --単価消費税
  	nExKin		 		  NUMBER; 	   --税抜金額
  	nInKin		 		  NUMBER; 	   --税込金額
    returnShohitaxkin 	  NUMBER(11);

  BEGIN
    -- 入力パラメータチェック
    IF iSimpleFg IS NULL THEN
      RAISE_APPLICATION_ERROR(-20001,
                              'パラメータ.単体計算フラグ が設定されていません。');
    END IF;
-- ■:1 Base)Arai 2014-01-16
    IF iTaxsetteikbn IS NULL THEN
      RAISE_APPLICATION_ERROR(-20001,
                              'パラメータ.税設定区分 が設定されていません。');
    END IF;
-- ■:2 Base)Arai 2014-01-16
    IF iTaxkbn IS NULL THEN
      RAISE_APPLICATION_ERROR(-20001,
                              'パラメータ.税区分 が設定されていません。');
    END IF;
    IF iKirikaebi IS NULL THEN
      RAISE_APPLICATION_ERROR(-20002,
                              'パラメータ.切り替え日  が設定されていません。');
    END IF;
    IF iApKihoncd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20002,
                              'パラメータ.AP基本コード  が設定されていません。');
    END IF;
    IF iShopcd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20002,
                              'パラメータ.ショップコード  が設定されていません。');
    END IF;
-- ■:1 Base)Arai 2014-01-16
--    -- 税設定区分が0:しないの場合は０を返送する
--    IF iTaxsetteikbn = '0' THEN
--      RETURN(0);
--    END IF;
-- ■:2 Base)Arai 2014-01-16
	--****
	--**** 計算用プロパティセット
	--****

	BEGIN
		-- ショップマスタ情報取得
		SELECT * INTO vRecordS02M046T FROM S02M046T WHERE APKIHONCD = iApKihoncd AND SHOPCD = iShopcd;

		-- 仕入先マスタ情報取得
		IF NOT iSiiresakicd IS NULL THEN
			SELECT * INTO vRecordS02M021T FROM S02M021T WHERE APKIHONCD = iApKihoncd AND SHOPCD = iShopcd AND SIIRESAKICD = iSiiresakicd;
		END IF;
	EXCEPTION
		WHEN OTHERS THEN
			vRecordS02M046T := NULL;
			vRecordS02M021T := NULL;
	END;

	-- 税計算区分設定
	-- ショップマスタから取得する
	IF iSiiresakicd IS NULL THEN

		IF iTaxKeisankbn IS NULL THEN
			IF iKirikaebi < vRecordS02M046T.TAXKEISANKBNURIKIRIKAEDD THEN
				vTaxKeisankbn := vRecordS02M046T.GENZTAXKEISANKBNURI;
			ELSE
				vTaxKeisankbn := vRecordS02M046T.SHINTAXKEISANKBNURI;
			END IF;
		ELSE
			vTaxKeisankbn := iTaxKeisankbn;
		END IF;

	-- 仕入先マスタから取得する
	ELSE
		vTaxKeisankbn := vRecordS02M021T.TAXKEISANKBN;
	END IF;

-- ■:1 PKUT)YANG 軽減税率対応 2019-05-20
	-- 税率設定
--	IF iKirikaebi < vRecordS02M046T.SHOHITAXKIRIKAEDD THEN
--		nTaxRate := vRecordS02M046T.GENZSHOHITAX;
--	ELSE
--		nTaxRate := vRecordS02M046T.SHINSHOHITAX;
--	END IF;
-- ■:2 PKUT)YANG 軽減税率対応 2019-05-20


	-- 売上計算時
	IF iSiiresakicd IS NULL THEN

-- ■:1 PKUT)YANG 軽減税率対応 2019-05-20
		-- 税区分設定
--		vTaxkbn := iTaxkbn;

		-- 金額端数計算区分設定
		vKinHasukbn := vRecordS02M046T.URIKINHASUKEISANKBN;
-- ■:2 PKUT)YANG 軽減税率対応 2019-05-20

		-- 税端数計算区分設定
		vTaxHasukbn := vRecordS02M046T.TAXHASUKEISANKBN;

	-- 仕入計算時
	ELSE

-- ■:1 PKUT)YANG 軽減税率対応 2019-05-20
		-- 税区分設定（仕入計算時は仕入先マスタの税区分をセットする）
--		vTaxkbn := vRecordS02M021T.TAXKBN;

		-- 金額端数計算区分設定
		vKinHasukbn := vRecordS02M021T.SIIREKINHASUKEISANKBN;
-- ■:2 PKUT)YANG 軽減税率対応 2019-05-20

		-- 税端数計算区分設定
		vTaxHasukbn := vRecordS02M021T.HASUKEISANKBN;

	END IF;

-- ■:1 PKUT)YANG 軽減税率対応 2019-05-20
	-- 金額端数計算区分設定
--	vKinHasukbn := vRecordS02M046T.URIKINHASUKEISANKBN;

	-- 税区分設定
	vTaxkbn := iTaxkbn;
-- ■:2 PKUT)YANG 軽減税率対応 2019-05-20

	--****
	--**** 消費税計算実行
	--****

-- ■:1 Base)Arai 2014-02-23
--	-- 課税時以外は０を返送する
--	IF vTaxkbn <> '1' THEN
--	-- 税設定区分が1:"する"かつ課税時以外は０を返送する
-- ■:1 PKUT)YANG 軽減税率対応 2019-05-20
--	IF iTaxsetteikbn = '1' AND vTaxkbn <> '1' THEN
	IF iSiiresakicd IS NOT NULL AND (vTaxkbn = '2' OR vTaxkbn = '3') THEN
    	RETURN(0);
	END IF;
	IF vTaxkbn <> '1' AND vTaxkbn <> '4' AND vTaxkbn <> '6' AND vTaxkbn <> '7' THEN	-- 消費税改修：新固定区分 6/7 を追加
-- ■:2 PKUT)YANG 軽減税率対応 2019-05-20
-- ■:2 Base)Arai 2014-02-23
    	RETURN(0);
	END IF;

-- ■:1 PKUT)YANG 2019-05-20
	-- 消費税率管理マスタから消費税を取得する。
	SELECT
		SHOHITAX
	INTO
		nTaxRate
	FROM
		S02M091T
	WHERE
		TAXKBN = iTaxkbn
		AND TEKIYOSTRTDD <= iKirikaebi
		AND TEKIYOSHRYDD >= iKirikaebi
		AND YUKOKBN = '1'
	;
-- ■:2 PKUT)YANG 2019-05-20

	-- 単体計算時
    IF iSimpleFg = '1' THEN
		returnShohitaxkin := roundValue(NVL(iKIN,0) * NVL(nTaxRate,0) / 100, vTaxHasukbn);

	ELSE

		-- 内税計算
    	IF vTaxKeisankbn = '1' THEN

			-- 単価消費税算出
			nTankaTax := roundValue(NVL(iKIN,0) * NVL(nTaxRate,0) / 100, vTaxHasukbn);

			-- 税抜金額算出
			nExKin := roundValue(NVL(iKIN,0) * NVL(iSuryo,0), vKinHasukbn);

			-- 税込金額算出
			nInKin := roundValue((NVL(iKIN,0) + nTankaTax) * NVL(iSuryo,0), vKinHasukbn);

			-- 消費税額算出
			returnShohitaxkin := nInKin - nExKin;

		-- 外税計算
		ELSE

			-- 税抜金額算出
			nExKin := roundValue(NVL(iKIN,0) * NVL(iSuryo,0), vKinHasukbn);

			-- 消費税額算出
			returnShohitaxkin := roundValue(nExKin * NVL(nTaxRate,0) / 100, vTaxHasukbn);

		END IF;

    END IF;

    RETURN(returnShohitaxkin);

  END SyohizeiKeisan;

  /*****************************************************************************/
  /* ファンクション名 ：端数計算処理                                           */
  /* 引数             ：iValue               計算対象値						   */
  /*                  ：iHasuKeisankbn       端数計算区分                      */
  /* return           ：			         計算結果値                        */
  /*****************************************************************************/
  FUNCTION roundValue (iValue	   		IN NUMBER,
					   iHasuKeisankbn	IN VARCHAR2) RETURN NUMBER IS
  nWorkKin			   NUMBER;
  BEGIN
	-- 切捨
	IF iHasuKeisankbn = '2' THEN
		-- RETURN TRUNC(iValue, 0) ;
-- ■:1 Base)Arai 2010-06-04
--		RETURN FLOOR(iValue) ;
		RETURN CASE WHEN iValue < 0 THEN CEIL(iValue) ELSE FLOOR(iValue) END;
-- ■:2 Base)Arai 2010-06-04
	-- 切上
	ELSIF iHasuKeisankbn = '3' THEN
		-- nWorkKin := TRUNC(iValue, 0);
		-- RETURN CASE WHEN nWorkKin <> iValue THEN nWorkKin + (CASE WHEN nWorkKin < 0 THEN -1 ELSE 1 END) ELSE nWorkKin END;
-- ■:1 Base)Arai 2010-06-04
--		RETURN CEIL(iValue) ;
		RETURN CASE WHEN iValue < 0 THEN FLOOR(iValue) ELSE CEIL(iValue) END;
-- ■:2 Base)Arai 2010-06-04

	-- 四捨五入
	ELSE
		RETURN ROUND(iValue, 0) ;
	END IF;
  END roundValue;


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
                             oKidTaxSum        OUT NUMBER
                              ) IS

    c_DLVRY_MOCHIKAERI  CONSTANT VARCHAR2(1) := '3';  -- 配達区分．持ち帰り
    /* 変数宣言 */
    nSuryo                NUMBER;                -- 数量
    nHyojununt            NUMBER;                --標準単価
    nAcptunt              NUMBER;                --単価
    nTankaTax             NUMBER;                --単価消費税
    nTaxPrice             NUMBER;                --消費税金額
    nKidUntTaxSum         NUMBER(11,0) := 0;     -- 子商品単価消費税合計
    nKidTaxSum            NUMBER(11,0) := 0;     -- 子商品消費税合計
    nUrikinhasukeisankbn  VARCHAR2(1)  := NULL;  -- 売上金額端数計算区分
    TYPE childModeltype IS RECORD (
                           SURYO                 S02M008T.SURYO%TYPE DEFAULT 0
                         , DLVRYPRICE            S02M008T.DLVRYPRICE%TYPE DEFAULT 0
                         , MOCHIKAERIPRICE       S02M008T.MOCHIKAERIPRICE%TYPE DEFAULT 0
                         , TAXKBN                S02V001T.TAXKBN%TYPE DEFAULT NULL
    );
    childModel childModeltype;

    /* 構成品リスト取得 */
    CURSOR SELECT_KOSEI IS
       SELECT
                 TKM.SURYO
               , TKM.DLVRYPRICE
               , TKM.MOCHIKAERIPRICE
               , SHN.TAXKBN
      FROM S02M008T TKM
      JOIN S02V001T SHN
         ON SHN.APKIHONCD    = iApKihoncd
            AND SHN.SHOPCD       = iShopcd
            AND SHN.APKIHONCD = TKM.APKIHONCD
            AND SHN.SHNCD        = TKM.KOSEISHNCD
            AND SHN.SHOPYUKOKBN  = '1'
      WHERE TKM.STSHNCD = iSyohinCd
            AND TKM.YUKOKBN = '1'
      ORDER BY TKM.RENBAN;
    BEGIN

    -- ショップマスタ取得
    SELECT
      SHPM.URIKINHASUKEISANKBN  -- 売上金額端数計算区分
    INTO nUrikinhasukeisankbn
    FROM
      S02M046T SHPM
    WHERE
      SHPM.APKIHONCD = iApKihoncd
      AND SHPM.SHOPCD = iShopcd
      AND SHPM.YUKOKBN = '1'
    ;

    -- 構成商品取得
    OPEN SELECT_KOSEI;

    LOOP FETCH SELECT_KOSEI INTO childModel;
       EXIT WHEN SELECT_KOSEI%NOTFOUND;
       --構成品数量
       nSuryo := NVL(iSuryo,0) * childModel.SURYO;

       --親商品が有料の場合は構成商品の計上区分をセットし、金額を算出する
       IF iKeijokbn IN ('01', '02') THEN
          IF iDlvrykbn = c_DLVRY_MOCHIKAERI THEN
             nAcptunt := childModel.MOCHIKAERIPRICE;
          ELSE
             nAcptunt := childModel.DLVRYPRICE;
          END IF;

          --外税
          IF iTaxKeisanKbn = '2' THEN
             --単価消費税
             nTankaTax := 0;
             --消費税金額
             nTaxPrice := Z1808B01.SyohizeiKeisan('1', --単体計算フラグ（する）
                                                  iTaxsetteikbn, --税設定区分
                                              childModel.TAXKBN, --税区分
                                                  iTaxKeisanKbn, --税計算区分
                                                     iKirikaebi, --切り替え日：配達予定日
                                                     iApKihoncd, --AP基本コード
                                                        iShopcd, --ショップコード
                                                           NULL, --仕入先コード
                                                           NULL, --数量
         roundValue((nAcptunt * nSuryo), nUrikinhasukeisankbn)); -- 金額
            nKidUntTaxSum := 0;
            nKidTaxSum := nKidTaxSum + nTaxPrice;

          --内税
          ELSE
             --単価消費税
             nTankaTax := Z1808B01.SyohizeiKeisan('1', --単体計算フラグ（する）
                                                  iTaxsetteikbn, --税設定区分
                                              childModel.TAXKBN, --税区分
                                                  iTaxKeisanKbn, --税計算区分
                                                     iKirikaebi, --切り替え日：配達予定日
                                                     iApKihoncd, --AP基本コード
                                                        iShopcd, --ショップコード
                                                           NULL, --仕入先コード
                                                           NULL, --数量
                                                      nAcptunt); -- 単価

            -- 消費税金額
            nTaxPrice := nTankaTax * nSuryo;
            --セット単価消費税
            nKidUntTaxSum :=  nKidUntTaxSum + nTankaTax * childModel.SURYO;
            --セット費税金額
            nKidTaxSum := nKidTaxSum + nTaxPrice;
         END IF;
      END IF;
    END LOOP;

    oKidUntTaxSum := nKidUntTaxSum;
    oKidTaxSum := nKidTaxSum;
  END SetSyohizeiKeisan;
END Z1808B01;
/
exit;
