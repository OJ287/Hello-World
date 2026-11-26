CREATE OR REPLACE PACKAGE BODY       C1009B01
/*******************************************************************************
 * システム名       ：ＡＰ総合管理システム
 * 機能名           ：一括入金消込
 *******************************************************************************
   Date       Author         Changes Key                                 Version
   -----------------------------------------------------------------------------
   2022-07-27 Isac)Kanaya    Create                                      1.0.0
 *******************************************************************************/
 AS
  /*****************************************************************************/
  /* ファンクション名：運用日付取得                                            */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /* 戻り値          ：運用日付                                                */
  /*****************************************************************************/
  FUNCTION GetOperationDate(iApkihoncd IN S02M006T.APKIHONCD%TYPE
                           ,iShopcd    IN S02M006T.SHOPCD%TYPE)
    RETURN S02M006T.MOJISETTEIATAI%TYPE IS
    ---------------------------
    -- 変数定義
    ---------------------------
    wOperationDate S02M006T.MOJISETTEIATAI%TYPE;
  BEGIN
    SELECT
      MOJISETTEIATAI
    INTO
      wOperationDate
    FROM
      S02M006T
    WHERE
          APKIHONCD = iApkihoncd
      AND SHOPCD    = iShopcd
      AND SETTEINO  = '038'
      AND RCRDKBN   = 'M'
      AND YUKOKBN   = '1'
    ;

    RETURN wOperationDate;
  END GetOperationDate;

  /*****************************************************************************/
  /* プロシージャ名  ：入金取込ワークデータ取得                                */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iDatsksmotokbn        データ作成元区分                  */
  /*                 ：iDnpyno               伝票番号                          */
  /*****************************************************************************/
  FUNCTION GetS10W001T(iApkihoncd     IN S10W001T.APKIHONCD%TYPE
                      ,iShopcd        IN S10W001T.SHOPCD%TYPE
                      ,iDatsksmotokbn IN S10W001T.DATSKSMOTOKBN%TYPE
                      ,iDnpyno        IN S10W001T.DNPYNO%TYPE)
    RETURN S10W001T%ROWTYPE IS
    ---------------------------
    -- レコード定義
    ---------------------------
    TYPE typeS10W001T IS TABLE OF S10W001T%ROWTYPE INDEX BY BINARY_INTEGER;
    wS10W001TList typeS10W001T;
  BEGIN
    -- 入金取込ワークデータ取得
    SELECT
      *
    BULK COLLECT INTO
      wS10W001TList
    FROM
      S10W001T
    WHERE
          APKIHONCD     = iApkihoncd
      AND SHOPCD        = iShopcd
      AND DATSKSMOTOKBN = iDatsksmotokbn
      AND NYKNDATSKSFLG = '0'
      AND DNPYNO        = iDnpyno
    ;

    RETURN wS10W001TList(wS10W001TList.FIRST);
  END GetS10W001T;

  /*****************************************************************************/
  /* プロシージャ名  ：プラス過入金リスト取得                                  */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iSkysakicd            請求先コード                      */
  /*                 ：oPlusKairekinList     プラス過入金リスト                */
  /*****************************************************************************/
  PROCEDURE GetPlusKanyukinList(iApkihoncd        IN S10T003T.APKIHONCD%TYPE
                               ,iShopcd           IN S10T003T.SHOPCD%TYPE
                               ,iSkysakicd        IN S10T003T.SKYSAKICD%TYPE
                               ,oPlusKairekinList OUT vTypeKanyukinList) IS
  BEGIN
    -- プラス過入金リスト取得（入金管理テーブル、入金明細テーブル）
    SELECT
        NYMT.DNPYNO
      , NYMT.NYKNDNPYNOEDANO
      , NYMT.TOKUISAKICD
      , NYMT.NYKNDD
      , NYMT.NYKNKIND
      , NYMT.NYKNKIN + NYMT.NEBIKIKIN + NYMT.TESURYO - NYMT.KESHIKOMIKIN - NYMT.SOSAIKIN AS KAIREKIN
      , NYMT.DATSKSMOTOKBN
    BULK COLLECT INTO
      oPlusKairekinList
    FROM
      S10T003T NYKT
      INNER JOIN S10T001T NYMT ON (
              NYMT.APKIHONCD     = NYKT.APKIHONCD
          AND NYMT.SHOPCD        = NYKT.SHOPCD
          AND NYMT.DNPYNO        = NYKT.NYKNDNPYNO
          AND NYMT.NYKNKIN + NYMT.NEBIKIKIN + NYMT.TESURYO - NYMT.KESHIKOMIKIN - NYMT.SOSAIKIN > 0
        )
    WHERE
          NYKT.APKIHONCD         = iApkihoncd
      AND NYKT.SHOPCD            = iShopcd
      AND NYKT.SKYSAKICD         = iSkysakicd
      AND NYKT.KAIREKINKANRIFLG  = '1'
    ORDER BY
      NYMT.DNPYNO, NYMT.NYKNDNPYNOEDANO
    ;
  END GetPlusKanyukinList;

  /*****************************************************************************/
  /* プロシージャ名  ：マイナス過入金リスト取得                                */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iSkysakicd            請求先コード                      */
  /*                 ：oMinusKairekinList    マイナス過入金リスト              */
  /*****************************************************************************/
  PROCEDURE GetMinusKanyukinList(iApkihoncd         IN S10T003T.APKIHONCD%TYPE
                                ,iShopcd            IN S10T003T.SHOPCD%TYPE
                                ,iSkysakicd         IN S10T003T.SKYSAKICD%TYPE
                                ,oMinusKairekinList OUT vTypeKanyukinList) IS
  BEGIN
    -- マイナス過入金リスト取得（入金管理テーブル、入金明細テーブル）
    SELECT
        NYMT.DNPYNO
      , NYMT.NYKNDNPYNOEDANO
      , NYMT.TOKUISAKICD
      , NYMT.NYKNDD
      , NYMT.NYKNKIND
      , NYMT.NYKNKIN + NYMT.NEBIKIKIN + NYMT.TESURYO - NYMT.KESHIKOMIKIN - NYMT.SOSAIKIN AS KAIREKIN
      , NYMT.DATSKSMOTOKBN
    BULK COLLECT INTO
      oMinusKairekinList
    FROM
      S10T003T NYKT
      INNER JOIN S10T001T NYMT ON (
              NYMT.APKIHONCD     = NYKT.APKIHONCD
          AND NYMT.SHOPCD        = NYKT.SHOPCD
          AND NYMT.DNPYNO        = NYKT.NYKNDNPYNO
          AND NYMT.NYKNKIN + NYMT.NEBIKIKIN + NYMT.TESURYO - NYMT.KESHIKOMIKIN - NYMT.SOSAIKIN < 0
        )
    WHERE
          NYKT.APKIHONCD         = iApkihoncd
      AND NYKT.SHOPCD            = iShopcd
      AND NYKT.SKYSAKICD         = iSkysakicd
      AND NYKT.KAIREKINKANRIFLG  = '1'
    ORDER BY
      NYMT.DNPYNO, NYMT.NYKNDNPYNOEDANO
    ;
  END GetMinusKanyukinList;

  /*****************************************************************************/
  /* ファンクション名：過入金リスト取得                                        */
  /* 引数            ：iKairekinListRecord   過入金リストレコード              */
  /* 戻り値          ：過入金リスト                                            */
  /*****************************************************************************/
  FUNCTION GetKanyukinList(iKairekinListRecord IN vTypeKanyukinListRecord) RETURN vTypeKanyukinList IS
    ---------------------------
    -- レコード定義
    ---------------------------
    wKanyukinList vTypeKanyukinList;
  BEGIN
    SELECT
        iKairekinListRecord.DNPYNO
      , iKairekinListRecord.NYKNDNPYNOEDANO
      , iKairekinListRecord.TOKUISAKICD
      , iKairekinListRecord.NYKNDD
      , iKairekinListRecord.NYKNKIND
      , iKairekinListRecord.KAIREKIN
      , iKairekinListRecord.DATSKSMOTOKBN
    BULK COLLECT INTO
      wKanyukinList
    FROM
      DUAL
    ;

    RETURN wKanyukinList;
  END GetKanyukinList;

  /*****************************************************************************/
  /* プロシージャ名  ：請求残高テーブルデータ取得                              */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iSkyshono             請求書番号                        */
  /*                 ：iSkysakicd            請求先コード                      */
  /*                 ：iTokuisakicd          得意先コード                      */
  /* 戻り値          ：請求残高テーブルレコード                                */
  /*****************************************************************************/
  FUNCTION GetS07T001TRecord(iApkihoncd   IN S07T001T.APKIHONCD%TYPE
                            ,iShopcd      IN S07T001T.SHOPCD%TYPE
                            ,iSkyshono    IN S07T001T.SKYSHONO%TYPE
                            ,iSkysakicd   IN S07T001T.SKYSAKICD%TYPE
                            ,iTokuisakicd IN S07T001T.TOKUISAKICD%TYPE)
    RETURN S07T001T%ROWTYPE IS
    ---------------------------
    -- レコード定義
    ---------------------------
    TYPE typeS07T001T IS TABLE OF S07T001T%ROWTYPE INDEX BY BINARY_INTEGER;
    wS07T001TList typeS07T001T;
  BEGIN
    -- 請求残高テーブルデータ取得
    SELECT
      *
    BULK COLLECT INTO
      wS07T001TList
    FROM
      S07T001T
    WHERE
          APKIHONCD   = iApkihoncd
      AND SHOPCD      = iShopcd
      AND SKYSHONO    = iSkyshono
      AND (iSkysakicd IS NULL OR SKYSAKICD = iSkysakicd)
      AND TOKUISAKICD = iTokuisakicd
      AND YUKOKBN     = '1'
    ;

    IF wS07T001TList.COUNT > 0 THEN
      RETURN wS07T001TList(wS07T001TList.FIRST);
    END IF;

    RETURN NULL;
  END GetS07T001TRecord;

  /*****************************************************************************/
  /* プロシージャ名  ：請求先マスタデータ取得                                  */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iSkysakicd            請求先コード                      */
  /* 戻り値          ：請求先マスタレコード                                    */
  /*****************************************************************************/
  FUNCTION GetS02M017TRecord(iApkihoncd     IN S02M017T.APKIHONCD%TYPE
                            ,iShopcd        IN S02M017T.SHOPCD%TYPE
                            ,iSkysakicd     IN S02M017T.SKYSAKICD%TYPE)
    RETURN S02M017T%ROWTYPE IS
    ---------------------------
    -- レコード定義
    ---------------------------
    TYPE typeS02M017T IS TABLE OF S02M017T%ROWTYPE INDEX BY BINARY_INTEGER;
    wS02M017TList typeS02M017T;
  BEGIN
    -- 請求先マスタデータ取得
    SELECT
      *
    BULK COLLECT INTO
      wS02M017TList
    FROM
      S02M017T
    WHERE
          APKIHONCD = iApkihoncd
      AND SHOPCD    = iShopcd
      AND SKYSAKICD = iSkysakicd
    ;

    RETURN wS02M017TList(wS02M017TList.FIRST);
  END GetS02M017TRecord;

  /*****************************************************************************/
  /* プロシージャ名  ：入金金額取得                                            */
  /* 引数            ：iNyknkind             入金種別                          */
  /*                 ：iKingaku              金額                              */
  /* 戻り値          ：入金金額                                                */
  /*****************************************************************************/
  FUNCTION GetNyknkin(iNyknkind IN S10T001T.NYKNKIND%TYPE
                     ,iKingaku  IN S10T001T.NYKNKIN%TYPE)
    RETURN S10T001T.NYKNKIN%TYPE IS
  BEGIN
    RETURN CASE WHEN iNyknkind NOT IN ('20', '21') THEN iKingaku ELSE 0 END;
  END GetNyknkin;

  /*****************************************************************************/
  /* プロシージャ名  ：値引金額取得                                            */
  /* 引数            ：iNyknkind             入金種別                          */
  /*                 ：iKingaku              金額                              */
  /* 戻り値          ：値引金額                                                */
  /*****************************************************************************/
  FUNCTION GetNebikikin(iNyknkind IN S10T001T.NYKNKIND%TYPE
                       ,iKingaku  IN S10T001T.NEBIKIKIN%TYPE)
    RETURN S10T001T.NEBIKIKIN%TYPE IS
  BEGIN
    RETURN CASE WHEN iNyknkind = '21' THEN iKingaku ELSE 0 END;
  END GetNebikikin;

  /*****************************************************************************/
  /* プロシージャ名  ：手数料取得                                              */
  /* 引数            ：iNyknkind             入金種別                          */
  /*                 ：iKingaku              金額                              */
  /* 戻り値          ：手数料                                                  */
  /*****************************************************************************/
  FUNCTION GetTesuryo(iNyknkind IN S10T001T.NYKNKIND%TYPE
                     ,iKingaku  IN S10T001T.TESURYO%TYPE)
    RETURN S10T001T.TESURYO%TYPE IS
  BEGIN
    RETURN CASE WHEN iNyknkind = '20' THEN iKingaku ELSE 0 END;
  END GetTesuryo;

  /*****************************************************************************/
  /* ファンクション名：入金伝票番号枝番号最大値取得                            */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iDnpyno               伝票番号                          */
  /* 戻り値          ：入金伝票番号枝番号最大値                                */
  /*****************************************************************************/
  FUNCTION GetMaxNykndnpynoedano(iApkihoncd IN S10T001T.APKIHONCD%TYPE
                                ,iShopcd    IN S10T001T.SHOPCD%TYPE
                                ,iDnpyno    IN S10T001T.DNPYNO%TYPE)
    RETURN S10T001T.NYKNDNPYNOEDANO%TYPE IS
    ---------------------------
    -- 変数定義
    ---------------------------
    wNykndnpynoedano S10T001T.NYKNDNPYNOEDANO%TYPE; -- 入金伝票番号枝番号
  BEGIN
    -- 入金伝票番号枝番号最大値取得（入金明細テーブル）
    SELECT
      NVL(MAX(NYKNDNPYNOEDANO), 0)
    INTO
      wNykndnpynoedano
    FROM
      S10T001T
    WHERE
          APKIHONCD   = iApkihoncd
      AND SHOPCD      = iShopcd
      AND DNPYNO      = iDnpyno
    ;

    RETURN wNykndnpynoedano;
  END GetMaxNykndnpynoedano;

  /*****************************************************************************/
  /* ファンクション名：SEQ最大値取得                                           */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iNykndnpyno           入金伝票番号                      */
  /* 戻り値          ：SEQ最大値                                               */
  /*****************************************************************************/
  FUNCTION GetMaxSeq(iApkihoncd  IN S10T004T.APKIHONCD%TYPE
                    ,iShopcd     IN S10T004T.SHOPCD%TYPE
                    ,iNykndnpyno IN S10T004T.NYKNDNPYNO%TYPE)
    RETURN S10T004T.SEQ%TYPE IS
    ---------------------------
    -- 変数定義
    ---------------------------
    wSeq S10T004T.SEQ%TYPE; -- SEQ
  BEGIN
    -- SEQ最大値取得（入金消込履歴テーブル）
    SELECT
      NVL(MAX(SEQ), 0)
    INTO
      wSeq
    FROM
      S10T004T
    WHERE
          APKIHONCD   = iApkihoncd
      AND SHOPCD      = iShopcd
      AND NYKNDNPYNO  = iNykndnpyno
    ;

    RETURN wSeq;
  END GetMaxSeq;

  /*****************************************************************************/
  /* ファンクション名：入金明細テーブル得意先コード取得                        */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iDnpyno               伝票番号                          */
  /*                 ：iSameSkysakicdFlg     請求先コードと同一フラグ          */
  /* 戻り値          ：入金明細テーブル得意先コード                            */
  /*****************************************************************************/
  FUNCTION GetS10T001TForTokuisakicd(iApkihoncd        IN S10T001T.APKIHONCD%TYPE
                                    ,iShopcd           IN S10T001T.SHOPCD%TYPE
                                    ,iDnpyno           IN S10T001T.DNPYNO%TYPE
                                    ,iSameSkysakicdFlg IN VARCHAR2)
    RETURN S10T001T.TOKUISAKICD%TYPE IS
    ---------------------------
    -- 変数定義
    ---------------------------
    wTokuisakicd S10T001T.TOKUISAKICD%TYPE; -- 得意先コード
  BEGIN
    -- 本会員の得意先コード取得（入金明細テーブル）
    SELECT
      NVL(MIN(NYMT.TOKUISAKICD), '000000')
    INTO
      wTokuisakicd
    FROM
      S10T001T NYMT
      INNER JOIN S02M013T TKSM ON (
            TKSM.APKIHONCD      = NYMT.APKIHONCD
        AND TKSM.SHOPCD         = NYMT.SHOPCD
        AND TKSM.TOKUISAKICD    = NYMT.TOKUISAKICD
        AND TKSM.TOKUISAKIJOTAI = '5' -- （本会員）
        AND TKSM.YUKOKBN        = '1'
        )
    WHERE
          NYMT.APKIHONCD = iApkihoncd
      AND NYMT.SHOPCD    = iShopcd
      AND NYMT.DNPYNO    = iDnpyno
      AND (iSameSkysakicdFlg = '0' OR NYMT.SKYSAKICD = NYMT.TOKUISAKICD)
      AND NYMT.YUKOKBN   = '1'
    ;

    RETURN wTokuisakicd;
  END GetS10T001TForTokuisakicd;

  /*****************************************************************************/
  /* ファンクション名：得意先マスタ得意先コード取得                            */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iSkysakicd            請求先コード                      */
  /*                 ：iSameSkysakicdFlg     請求先コードと同一フラグ          */
  /* 戻り値          ：得意先マスタ.得意先コード                               */
  /*****************************************************************************/
  FUNCTION GetS02M013TForTokuisakicd(iApkihoncd        IN S02M013T.APKIHONCD%TYPE
                                    ,iShopcd           IN S02M013T.SHOPCD%TYPE
                                    ,iSkysakicd        IN S02M013T.SKYSAKICD%TYPE
                                    ,iSameSkysakicdFlg IN VARCHAR2)
    RETURN S02M013T.TOKUISAKICD%TYPE IS
    ---------------------------
    -- 変数定義
    ---------------------------
    wTokuisakicd S02M013T.TOKUISAKICD%TYPE; -- 得意先コード
  BEGIN
    -- 本会員の得意先コード取得（得意先マスタ）
    SELECT
      NVL(MIN(TOKUISAKICD), '000000')
    INTO
      wTokuisakicd
    FROM
      S02M013T
    WHERE
          APKIHONCD      = iApkihoncd
      AND SHOPCD         = iShopcd
      AND SKYSAKICD      = iSkysakicd
      AND (iSameSkysakicdFlg = '0' OR TOKUISAKICD = iSkysakicd)
      AND TOKUISAKIJOTAI = '5' -- （本会員）
      AND YUKOKBN        = '1'
    ;

    RETURN wTokuisakicd;
  END GetS02M013TForTokuisakicd;

  /*****************************************************************************/
  /* ファンクション名：消込金額合計取得                                        */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iDnpyno               伝票番号                          */
  /* 戻り値          ：消込金額合計                                            */
  /*****************************************************************************/
  FUNCTION GetKeshikomikinSum(iApkihoncd  IN S10T001T.APKIHONCD%TYPE
                             ,iShopcd     IN S10T001T.SHOPCD%TYPE
                             ,iDnpyno     IN S10T001T.DNPYNO%TYPE)
    RETURN NUMBER IS
    ---------------------------
    -- 変数定義
    ---------------------------
    wKeshikomikinSum NUMBER; -- 消込金額合計
  BEGIN
    -- 消込金額合計取得（入金明細テーブル）
    SELECT
      NVL(SUM(KESHIKOMIKIN), 0)
    INTO
      wKeshikomikinSum
    FROM
      S10T001T
    WHERE
          APKIHONCD = iApkihoncd
      AND SHOPCD    = iShopcd
      AND DNPYNO    = iDnpyno
    ;

    RETURN wKeshikomikinSum;
  END GetKeshikomikinSum;

  /*****************************************************************************/
  /* プロシージャ名  ：過入金得意先コード取得                                  */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iSkysakicd            請求先コード                      */
  /*                 ：iDnpyno               伝票番号                          */
  /*                 ：iClearingFlg          消込ありフラグ                    */
  /*                 ：oKairekinTokuisakicd  過入金得意先コード                */
  /*****************************************************************************/
  PROCEDURE GetKanyukinTokuisakicd(iApkihoncd           IN S10T001T.APKIHONCD%TYPE
                                  ,iShopcd              IN S10T001T.SHOPCD%TYPE
                                  ,iSkysakicd           IN S10T001T.SKYSAKICD%TYPE
                                  ,iDnpyno              IN S10T001T.DNPYNO%TYPE
                                  ,iClearingFlg         IN VARCHAR2
                                  ,oKairekinTokuisakicd OUT S02M013T.TOKUISAKICD%TYPE) IS
    ---------------------------
    -- 変数定義
    ---------------------------
    wKairekinTokuisakicd S02M013T.TOKUISAKICD%TYPE; -- 過入金得意先コード
  BEGIN
    -- 消込ありの場合
    IF iClearingFlg = '1' THEN
      -- （請求先コードと同一の）本会員の得意先コード取得（入金明細テーブル、得意先マスタ）
      wKairekinTokuisakicd := GetS10T001TForTokuisakicd(iApkihoncd, iShopcd, iDnpyno, '1');

      -- （請求先コードと同一の）本会員の得意先コードが"000000"の場合
      IF wKairekinTokuisakicd = '000000' THEN
        -- 本会員の得意先コード取得（入金明細テーブル、得意先マスタ）
        wKairekinTokuisakicd := GetS10T001TForTokuisakicd(iApkihoncd, iShopcd, iDnpyno, '0');
      END IF;

      -- 本会員の得意先コードが"000000"の場合
      IF wKairekinTokuisakicd = '000000' THEN
        -- （請求先コードと同一の）本会員の得意先コード取得（得意先マスタ）
        wKairekinTokuisakicd := GetS02M013TForTokuisakicd(iApkihoncd, iShopcd, iSkysakicd, '1');
      END IF;

      -- （請求先コードと同一の）本会員の得意先コードが"000000"の場合
      IF wKairekinTokuisakicd = '000000' THEN
        -- 本会員の得意先コード取得（得意先マスタ）
        wKairekinTokuisakicd := GetS02M013TForTokuisakicd(iApkihoncd, iShopcd, iSkysakicd, '0');
      END IF;
    ELSE
      -- （請求先コードと同一の）本会員の得意先コード取得（得意先マスタ）
      wKairekinTokuisakicd := GetS02M013TForTokuisakicd(iApkihoncd, iShopcd, iSkysakicd, '1');

      -- （請求先コードと同一の）本会員の得意先コードが"000000"の場合
      IF wKairekinTokuisakicd = '000000' THEN
        -- 本会員の得意先コード取得（得意先マスタ）
        wKairekinTokuisakicd := GetS02M013TForTokuisakicd(iApkihoncd, iShopcd, iSkysakicd, '0');
      END IF;
    END IF;

    -- 本会員の得意先コードが"000000"の場合
    IF wKairekinTokuisakicd = '000000' THEN
      wKairekinTokuisakicd := iSkysakicd;
    END IF;

    -- 過入金得意先コード設定
    oKairekinTokuisakicd := wKairekinTokuisakicd;

  END GetKanyukinTokuisakicd;

  /*****************************************************************************/
  /* プロシージャ名  ：基本情報設定                                            */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iDatsksmotokbn        データ作成元区分                  */
  /*                 ：iUserApkihoncd        担当者ＡＰ基本コード              */
  /*                 ：iUserShopcd           担当者ショップコード              */
  /*                 ：iTantoshacd           担当者コード                      */
  /*                 ：iIpAddress            ＩＰアドレス                      */
  /*                 ：iDenpyonofrom         入金伝票番号開始値                */
  /*                 ：iDenpyonoto           入金伝票番号終了値                */
  /*****************************************************************************/
  PROCEDURE SetBasicInfo(iApkihoncd     IN VARCHAR2
                        ,iShopcd        IN VARCHAR2
                        ,iDatsksmotokbn IN VARCHAR2
                        ,iUserApkihoncd IN VARCHAR2
                        ,iUserShopcd    IN VARCHAR2
                        ,iTantoshacd    IN VARCHAR2
                        ,iIpAddress     IN VARCHAR2
                        ,iDenpyonofrom  IN VARCHAR2
                        ,iDenpyonoto    IN VARCHAR2) IS
  BEGIN
    --入力パラメータチェック
    IF (iApkihoncd IS NULL) THEN
      RAISE_APPLICATION_ERROR(-20001, 'パラメータ.AP基本コードが設定されていません。');
    END IF;

    IF (iShopcd IS NULL) THEN
      RAISE_APPLICATION_ERROR(-20002, 'パラメータ.ショップコードが設定されていません。');
    END IF;

    IF (iDatsksmotokbn IS NULL) THEN
      RAISE_APPLICATION_ERROR(-20003, 'パラメータ.データ作成元区分が設定されていません。');
    END IF;

    IF (iUserApkihoncd IS NULL) THEN
      RAISE_APPLICATION_ERROR(-20004, 'パラメータ.ログインユーザ情報(ＡＰ基本コード)が設定されていません。');
    END IF;

    IF (iUserShopcd IS NULL) THEN
      RAISE_APPLICATION_ERROR(-20005, 'パラメータ.ログインユーザ情報(ショップコード)が設定されていません。');
    END IF;

    IF (iTantoshacd IS NULL) THEN
      RAISE_APPLICATION_ERROR(-20006, 'パラメータ.担当者コードが設定されていません。');
    END IF;

    IF (iIpAddress IS NULL) THEN
      RAISE_APPLICATION_ERROR(-20007, 'パラメータ.ＩＰアドレスが設定されていません。');
    END IF;

    IF (iDenpyonofrom IS NULL) THEN
      RAISE_APPLICATION_ERROR(-20008, 'パラメータ.採番伝票番号ＦＲＯＭが設定されていません。');
    END IF;

    IF (iDenpyonoto IS NULL) THEN
      RAISE_APPLICATION_ERROR(-20009, 'パラメータ.採番伝票番号ＴＯが設定されていません。');
    END IF;

    -- 共通変数初期化
    vOperationDate  := GetOperationDate(iApkihoncd, iShopcd);  -- 運用日付
    vSystemDate     := TO_CHAR(CURRENT_DATE, 'YYYYMMDD');      -- システム日付
    vSystemTime     := TO_CHAR(CURRENT_DATE, 'HH24MISS');      -- システム時間
    vApkihoncd      := iApkihoncd;                             -- ＡＰ基本コード
    vShopcd         := iShopcd;                                -- ショップコード
    vUserApkihoncd  := iUserApkihoncd;                         -- 担当者ＡＰ基本コード
    vUserShopcd     := iUserShopcd;                            -- 担当者ショップコード
    vTantoshacd     := iTantoshacd;                            -- 担当者コード
    vIpAddress      := iIpAddress;                             -- ＩＰアドレス
    vProgramId      := 'C1009B01';                             -- プログラムＩＤ

  END SetBasicInfo;

  /*****************************************************************************/
  /* プロシージャ名  ：得意先マスタ更新                                        */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iTokuisakicd          得意先コード                      */
  /*                 ：iJotaists             状態ステータス                    */
  /*****************************************************************************/
  PROCEDURE UpdateS02M013T(iApkihoncd   IN S02M013T.APKIHONCD%TYPE
                          ,iShopcd      IN S02M013T.SHOPCD%TYPE
                          ,iTokuisakicd IN S02M013T.TOKUISAKICD%TYPE
                          ,iJotaists    IN S02M013T.JOTAISTS%TYPE) IS
  BEGIN
    -- 得意先マスタ更新
    UPDATE S02M013T
    SET
        JOTAISTS        = iJotaists
      , KOSHAAPKIHONCD  = vUserApkihoncd
      , KOSHASHOPCD     = vUserShopcd
      , KOSHACD         = vTantoshacd
      , KSHNSYSTMYMD    = vSystemDate
      , KSHNSYSTMJIKAN  = vSystemTime
      , KSHNUNYOYMD     = vOperationDate
      , KSHNIPADRSE     = vIpAddress
      , KSHNPGMID       = vProgramId
    WHERE
          APKIHONCD     = iApkihoncd
      AND SHOPCD        = iShopcd
      AND TOKUISAKICD   = iTokuisakicd
    ;
  END UpdateS02M013T;

  /*****************************************************************************/
  /* プロシージャ名  ：請求先マスタ更新ロック                                  */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iSkysakicd            請求先コード                      */
  /*****************************************************************************/
  PROCEDURE ForUpdateS02M017T(iApkihoncd IN S02M017T.APKIHONCD%TYPE
                             ,iShopcd    IN S02M017T.SHOPCD%TYPE
                             ,iSkysakicd IN S02M017T.SKYSAKICD%TYPE) IS
    ---------------------------
    -- レコード定義
    ---------------------------
    TYPE typeS02M017T IS TABLE OF S02M017T%ROWTYPE INDEX BY BINARY_INTEGER;
    wS02M017TList typeS02M017T;
  BEGIN
    -- 請求先マスタ更新ロック
    SELECT
      *
    BULK COLLECT INTO
      wS02M017TList
    FROM
      S02M017T
    WHERE
          APKIHONCD = iApkihoncd
      AND SHOPCD    = iShopcd
      AND SKYSAKICD = iSkysakicd
      AND YUKOKBN   = '1'
      FOR UPDATE;

    IF wS02M017TList.COUNT = 0 THEN
      RAISE_APPLICATION_ERROR(-20010, '請求先コード：' || iSkysakicd || 'が見つかりません。');
    END IF;
  END ForUpdateS02M017T;

  /*****************************************************************************/
  /* プロシージャ名  ：請求先マスタ更新（過入金額）                            */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iSkysakicd            請求先コード                      */
  /*                 ：iKairekin             過入金額                          */
  /*****************************************************************************/
  PROCEDURE UpdateS02M017TForKairekin(iApkihoncd IN S02M017T.APKIHONCD%TYPE
                                     ,iShopcd    IN S02M017T.SHOPCD%TYPE
                                     ,iSkysakicd IN S02M017T.SKYSAKICD%TYPE
                                     ,iKairekin  IN S02M017T.KAIREKIN%TYPE) IS
  BEGIN
    -- 請求先マスタ更新（過入金額）
    UPDATE S02M017T
    SET
        KAIREKIN        = NVL(iKairekin, KAIREKIN)
      , KOSHAAPKIHONCD  = vUserApkihoncd
      , KOSHASHOPCD     = vUserShopcd
      , KOSHACD         = vTantoshacd
      , KSHNSYSTMYMD    = vSystemDate
      , KSHNSYSTMJIKAN  = vSystemTime
      , KSHNUNYOYMD     = vOperationDate
      , KSHNIPADRSE     = vIpAddress
      , KSHNPGMID       = vProgramId
    WHERE
          APKIHONCD     = iApkihoncd
      AND SHOPCD        = iShopcd
      AND SKYSAKICD     = iSkysakicd
    ;
  END UpdateS02M017TForKairekin;

  /*****************************************************************************/
  /* プロシージャ名  ：請求先マスタ更新（売上調整金額）                        */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iSkysakicd            請求先コード                      */
  /*                 ：iUrichoseikin         売上調整金額                      */
  /*****************************************************************************/
  PROCEDURE UpdateS02M017TForUrichoseikin(iApkihoncd    IN S02M017T.APKIHONCD%TYPE
                                         ,iShopcd       IN S02M017T.SHOPCD%TYPE
                                         ,iSkysakicd    IN S02M017T.SKYSAKICD%TYPE
                                         ,iUrichoseikin IN S02M017T.URICHOSEIKIN%TYPE) IS
  BEGIN
    -- 請求先マスタ更新（売上調整金額）
    UPDATE S02M017T
    SET
        URICHOSEIKIN    = URICHOSEIKIN + iUrichoseikin
      , KOSHAAPKIHONCD  = vUserApkihoncd
      , KOSHASHOPCD     = vUserShopcd
      , KOSHACD         = vTantoshacd
      , KSHNSYSTMYMD    = vSystemDate
      , KSHNSYSTMJIKAN  = vSystemTime
      , KSHNUNYOYMD     = vOperationDate
      , KSHNIPADRSE     = vIpAddress
      , KSHNPGMID       = vProgramId
    WHERE
          APKIHONCD     = iApkihoncd
      AND SHOPCD        = iShopcd
      AND SKYSAKICD     = iSkysakicd
    ;
  END UpdateS02M017TForUrichoseikin;

  /*****************************************************************************/
  /* プロシージャ名  ：請求先マスタ更新（過入金額、売上調整金額）              */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iSkysakicd            請求先コード                      */
  /*                 ：iKairekin             過入金額                          */
  /*                 ：iUrichoseikin         売上調整金額                      */
  /*****************************************************************************/
  PROCEDURE UpdateS02M017T(iApkihoncd    IN S02M017T.APKIHONCD%TYPE
                          ,iShopcd       IN S02M017T.SHOPCD%TYPE
                          ,iSkysakicd    IN S02M017T.SKYSAKICD%TYPE
                          ,iKairekin     IN S02M017T.KAIREKIN%TYPE
                          ,iUrichoseikin IN S02M017T.URICHOSEIKIN%TYPE) IS
  BEGIN
    -- 請求先マスタ更新（過入金額、売上調整金額）
    UPDATE S02M017T
    SET
        KAIREKIN        = KAIREKIN + iKairekin
      , URICHOSEIKIN    = URICHOSEIKIN + iUrichoseikin
      , KOSHAAPKIHONCD  = vUserApkihoncd
      , KOSHASHOPCD     = vUserShopcd
      , KOSHACD         = vTantoshacd
      , KSHNSYSTMYMD    = vSystemDate
      , KSHNSYSTMJIKAN  = vSystemTime
      , KSHNUNYOYMD     = vOperationDate
      , KSHNIPADRSE     = vIpAddress
      , KSHNPGMID       = vProgramId
    WHERE
          APKIHONCD     = iApkihoncd
      AND SHOPCD        = iShopcd
      AND SKYSAKICD     = iSkysakicd
    ;
  END UpdateS02M017T;

  /*****************************************************************************/
  /* プロシージャ名  ：請求残高テーブル更新                                    */
  /* 引数            ：iS07T001TRecord       請求残高テーブルレコード          */
  /*                 ：iKeshikomikin         消込金額                          */
  /*                 ：iKeshikomizd          消込残高                          */
  /*****************************************************************************/
  PROCEDURE UpdateS07T001T(iS07T001TRecord  IN S07T001T%ROWTYPE
                          ,iKeshikomikin    IN S07T001T.KESHIKOMIKIN%TYPE
                          ,iKeshikomizd     IN S07T001T.KESHIKOMIZD%TYPE) IS
  BEGIN
    -- 請求残高テーブル更新
    UPDATE S07T001T
    SET
        KESHIKOMIKIN      = iKeshikomikin
      , KESHIKOMIZD       = iKeshikomizd
      , KOSHAAPKIHONCD    = vUserApkihoncd
      , KOSHASHOPCD       = vUserShopcd
      , KOSHACD           = vTantoshacd
      , KSHNSYSTMYMD      = vSystemDate
      , KSHNSYSTMJIKAN    = vSystemTime
      , KSHNUNYOYMD       = vOperationDate
      , KSHNIPADRSE       = vIpAddress
      , KSHNPGMID         = vProgramId
    WHERE
          APKIHONCD       = iS07T001TRecord.APKIHONCD
      AND SHOPCD          = iS07T001TRecord.SHOPCD
      AND SKYSHONO        = iS07T001TRecord.SKYSHONO
      AND SKYSHONOEDANO   = iS07T001TRecord.SKYSHONOEDANO
    ;
  END UpdateS07T001T;

  /*****************************************************************************/
  /* プロシージャ名  ：請求残高テーブル更新（項目指定）                        */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iSkyshono             請求書番号                        */
  /*                 ：iTokuisakicd          得意先コード                      */
  /*                 ：iKeshikomikin         消込金額                          */
  /*                 ：iKeshikomizd          消込残高                          */
  /*****************************************************************************/
  PROCEDURE UpdateS07T001TByItem(iApkihoncd    IN S07T001T.APKIHONCD%TYPE
                                ,iShopcd       IN S07T001T.SHOPCD%TYPE
                                ,iSkyshono     IN S07T001T.SKYSHONO%TYPE
                                ,iSkysakicd    IN S07T001T.SKYSAKICD%TYPE
                                ,iTokuisakicd  IN S07T001T.TOKUISAKICD%TYPE
                                ,iKeshikomikin IN S07T001T.KESHIKOMIKIN%TYPE
                                ,iKeshikomizd  IN S07T001T.KESHIKOMIZD%TYPE) IS
  BEGIN
    -- 請求残高テーブル更新
    UPDATE S07T001T
    SET
        KESHIKOMIKIN      = iKeshikomikin
      , KESHIKOMIZD       = iKeshikomizd
      , KOSHAAPKIHONCD    = vUserApkihoncd
      , KOSHASHOPCD       = vUserShopcd
      , KOSHACD           = vTantoshacd
      , KSHNSYSTMYMD      = vSystemDate
      , KSHNSYSTMJIKAN    = vSystemTime
      , KSHNUNYOYMD       = vOperationDate
      , KSHNIPADRSE       = vIpAddress
      , KSHNPGMID         = vProgramId
    WHERE
          APKIHONCD       = iApkihoncd
      AND SHOPCD          = iShopcd
      AND SKYSHONO        = iSkyshono
      AND (iSkysakicd IS NULL OR SKYSAKICD = iSkysakicd)
      AND TOKUISAKICD     = iTokuisakicd
    ;
  END UpdateS07T001TByItem;

  /*****************************************************************************/
  /* プロシージャ名  ：入金明細テーブル更新（相殺金額）                        */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iDnpyno               伝票番号                          */
  /*                 ：iNykndnpynoedano      入金伝票番号枝番号                */
  /*                 ：iSosaikin             相殺金額                          */
  /*****************************************************************************/
  PROCEDURE UpdateS10T001TForSosaikin(iApkihoncd       IN S10T001T.APKIHONCD%TYPE
                                     ,iShopcd          IN S10T001T.SHOPCD%TYPE
                                     ,iDnpyno          IN S10T001T.DNPYNO%TYPE
                                     ,iNykndnpynoedano IN S10T001T.NYKNDNPYNOEDANO%TYPE
                                     ,iSosaikin        IN S10T001T.SOSAIKIN%TYPE) IS
  BEGIN
    -- 入金明細テーブル更新（相殺金額）
    UPDATE S10T001T
    SET
        SOSAIKIN          = SOSAIKIN + iSosaikin
      , KOSHAAPKIHONCD    = vUserApkihoncd
      , KOSHASHOPCD       = vUserShopcd
      , KOSHACD           = vTantoshacd
      , KSHNSYSTMYMD      = vSystemDate
      , KSHNSYSTMJIKAN    = vSystemTime
      , KSHNUNYOYMD       = vOperationDate
      , KSHNIPADRSE       = vIpAddress
      , KSHNPGMID         = vProgramId
    WHERE
          APKIHONCD       = iApkihoncd
      AND SHOPCD          = iShopcd
      AND DNPYNO          = iDnpyno
      AND NYKNDNPYNOEDANO = iNykndnpynoedano
    ;
  END UpdateS10T001TForSosaikin;

  /*****************************************************************************/
  /* プロシージャ名  ：入金明細テーブル更新（請求書出力有無区分、取消伝票番号）*/
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iDnpyno               伝票番号                          */
  /*                 ：iNykndnpynoedano      入金伝票番号枝番号                */
  /*                 ：iSkyshootptumukbn     請求書出力有無区分                */
  /*                 ：iTrksdnpyno           取消伝票番号                      */
  /*****************************************************************************/
  PROCEDURE UpdateS10T001TForTrksdnpyno(iApkihoncd        IN S10T001T.APKIHONCD%TYPE
                                       ,iShopcd           IN S10T001T.SHOPCD%TYPE
                                       ,iDnpyno           IN S10T001T.DNPYNO%TYPE
                                       ,iNykndnpynoedano  IN S10T001T.NYKNDNPYNOEDANO%TYPE
                                       ,iSkyshootptkbn    IN S10T001T.SKYSHOOTPTARINASHIKBN%TYPE
                                       ,iTrksdnpyno       IN S10T001T.TRKSDNPYNO%TYPE) IS
  BEGIN
    -- 入金明細テーブル更新（請求書出力有無区分、取消伝票番号）
    UPDATE S10T001T
    SET
        SKYSHOOTPTARINASHIKBN = iSkyshootptkbn
      , TRKSDNPYNO            = iTrksdnpyno
      , KOSHAAPKIHONCD        = vUserApkihoncd
      , KOSHASHOPCD           = vUserShopcd
      , KOSHACD               = vTantoshacd
      , KSHNSYSTMYMD          = vSystemDate
      , KSHNSYSTMJIKAN        = vSystemTime
      , KSHNUNYOYMD           = vOperationDate
      , KSHNIPADRSE           = vIpAddress
      , KSHNPGMID             = vProgramId
    WHERE
          APKIHONCD           = iApkihoncd
      AND SHOPCD              = iShopcd
      AND DNPYNO              = iDnpyno
      AND NYKNDNPYNOEDANO     = iNykndnpynoedano
    ;
  END UpdateS10T001TForTrksdnpyno;

  /*****************************************************************************/
  /* プロシージャ名  ：入金明細テーブル更新（入金金額、値引金額、手数料）      */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iDnpyno               伝票番号                          */
  /*                 ：iNykndnpynoedano      入金伝票番号枝番号                */
  /*                 ：iNyknkind             入金種別                          */
  /*                 ：iNyknkin              入金金額                          */
  /*                 ：iNebikikin            値引金額                          */
  /*                 ：iTesuryo              手数料                            */
  /*****************************************************************************/
  PROCEDURE UpdateS10T001TForKingaku(iApkihoncd       IN S10T001T.APKIHONCD%TYPE
                                    ,iShopcd          IN S10T001T.SHOPCD%TYPE
                                    ,iDnpyno          IN S10T001T.DNPYNO%TYPE
                                    ,iNykndnpynoedano IN S10T001T.NYKNDNPYNOEDANO%TYPE
                                    ,iNyknkind        IN S10T001T.NYKNKIND%TYPE
                                    ,iNyknkin         IN S10T001T.NYKNKIN%TYPE
                                    ,iNebikikin       IN S10T001T.NEBIKIKIN%TYPE
                                    ,iTesuryo         IN S10T001T.TESURYO%TYPE) IS
  BEGIN
    -- 入金明細テーブル更新（入金金額、値引金額、手数料）
    UPDATE S10T001T
    SET
        NYKNKIN           = NYKNKIN + iNyknkin
      , NEBIKIKIN         = NEBIKIKIN + iNebikikin
      , TESURYO           = TESURYO + iTesuryo
      , KOSHAAPKIHONCD    = vUserApkihoncd
      , KOSHASHOPCD       = vUserShopcd
      , KOSHACD           = vTantoshacd
      , KSHNSYSTMYMD      = vSystemDate
      , KSHNSYSTMJIKAN    = vSystemTime
      , KSHNUNYOYMD       = vOperationDate
      , KSHNIPADRSE       = vIpAddress
      , KSHNPGMID         = vProgramId
    WHERE
          APKIHONCD       = iApkihoncd
      AND SHOPCD          = iShopcd
      AND DNPYNO          = iDnpyno
      AND NYKNDNPYNOEDANO = iNykndnpynoedano
      AND NYKNKIND        = iNyknkind
    ;
  END UpdateS10T001TForKingaku;

  /*****************************************************************************/
  /* プロシージャ名  ：入金明細テーブル登録                                    */
  /* 引数            ：iS10T001TRecord       入金明細テーブルレコード          */
  /*****************************************************************************/
  PROCEDURE InsertS10T001T(iS10T001TRecord IN S10T001T%ROWTYPE) IS
  BEGIN
    -- 入金明細テーブル登録
    INSERT INTO S10T001T(
          APKIHONCD
        , SHOPCD
        , DNPYNO
        , NYKNDNPYNOEDANO
        , KAIREKINKBN
        , SKYSAKICD
        , TOKUISAKICD
        , NYKNDD
        , NYKNKIND
        , NYKNKIN
        , NEBIKIKIN
        , TESURYO
        , BIKO
        , KESHIKOMIKIN
        , SOSAIKIN
        , URISHIMEPRCSDD
        , SKYPRCSJISSIDD
        , SKYSHIMEDYDM
        , SKYSHIMEDD
        , SKYPRCSJISSIKBN
        , GETSUJIPRCSJISSIDD
        , GETSUJISHIMEDYDM
        , GETSUJISHIMEDD
        , DATSKSMOTOKBN
        , SKYSHOOTPTARINASHIKBN
        , KAISHUTNSHACD
        , TRKSDNPYNO
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
    ) VALUES (
          iS10T001TRecord.APKIHONCD
        , iS10T001TRecord.SHOPCD
        , iS10T001TRecord.DNPYNO
        , iS10T001TRecord.NYKNDNPYNOEDANO
        , iS10T001TRecord.KAIREKINKBN
        , iS10T001TRecord.SKYSAKICD
        , iS10T001TRecord.TOKUISAKICD
        , iS10T001TRecord.NYKNDD
        , iS10T001TRecord.NYKNKIND
        , iS10T001TRecord.NYKNKIN
        , iS10T001TRecord.NEBIKIKIN
        , iS10T001TRecord.TESURYO
        , iS10T001TRecord.BIKO
        , iS10T001TRecord.KESHIKOMIKIN
        , iS10T001TRecord.SOSAIKIN
        , vOperationDate
        , '00000000'
        , NULL
        , NULL
        , '0'
        , '00000000'
        , NULL
        , NULL
        , iS10T001TRecord.DATSKSMOTOKBN
        , iS10T001TRecord.SKYSHOOTPTARINASHIKBN
        , iS10T001TRecord.KAISHUTNSHACD
        , iS10T001TRecord.TRKSDNPYNO
        , '1'
        , vUserApkihoncd
        , vUserShopcd
        , vTantoshacd
        , vSystemDate
        , vSystemTime
        , vOperationDate
        , vIpAddress
        , vProgramId
        , vUserApkihoncd
        , vUserShopcd
        , vTantoshacd
        , vSystemDate
        , vSystemTime
        , vOperationDate
        , vIpAddress
        , vProgramId
    );
  END InsertS10T001T;

  /*****************************************************************************/
  /* プロシージャ名  ：入金管理テーブル更新                                    */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iDnpyno               伝票番号                          */
  /*                 ：iKairekinkanriflg     過入金管理フラグ                  */
  /*                 ：iKairekin             過入金額                          */
  /*                 ：iTrksdnpyno           取消伝票番号                      */
  /*****************************************************************************/
  PROCEDURE UpdateS10T003T(iApkihoncd        IN S10T003T.APKIHONCD%TYPE
                          ,iShopcd           IN S10T003T.SHOPCD%TYPE
                          ,iDnpyno           IN S10T003T.NYKNDNPYNO%TYPE
                          ,iKairekinkanriflg IN S10T003T.KAIREKINKANRIFLG%TYPE
                          ,iKairekin         IN S10T003T.KAIREKIN%TYPE
                          ,iTrksdnpyno       IN S10T003T.TRKSDNPYNO%TYPE) IS
  BEGIN
    -- 入金管理テーブル更新
    UPDATE S10T003T
    SET
        KAIREKIN         = iKairekin
      , TRKSDNPYNO       = iTrksdnpyno
      , KAIREKINKANRIFLG = NVL(iKairekinkanriflg, KAIREKINKANRIFLG)
      , KOSHAAPKIHONCD   = vUserApkihoncd
      , KOSHASHOPCD      = vUserShopcd
      , KOSHACD          = vTantoshacd
      , KSHNSYSTMYMD     = vSystemDate
      , KSHNSYSTMJIKAN   = vSystemTime
      , KSHNUNYOYMD      = vOperationDate
      , KSHNIPADRSE      = vIpAddress
      , KSHNPGMID        = vProgramId
    WHERE
          APKIHONCD      = iApkihoncd
      AND SHOPCD         = iShopcd
      AND NYKNDNPYNO     = iDnpyno
    ;
  END UpdateS10T003T;

  /*****************************************************************************/
  /* プロシージャ名  ：入金管理テーブル登録                                    */
  /* 引数            ：iS10T003TRecord       入金管理テーブルレコード          */
  /*****************************************************************************/
  PROCEDURE InsertS10T003T(iS10T003TRecord IN S10T003T%ROWTYPE) IS
  BEGIN
    -- 入金管理テーブル登録
    INSERT INTO S10T003T(
          APKIHONCD
        , SHOPCD
        , NYKNDNPYNO
        , SKYSAKICD
        , NYKNDD
        , NYKNKIND
        , NYKNKIN
        , NEBIKIKIN
        , TESURYO
        , KAIREKIN
        , URICHOSEIKIN
        , TRKSDNPYNO
        , KAIREKINKANRIFLG
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
    ) VALUES (
          iS10T003TRecord.APKIHONCD
        , iS10T003TRecord.SHOPCD
        , iS10T003TRecord.NYKNDNPYNO
        , iS10T003TRecord.SKYSAKICD
        , iS10T003TRecord.NYKNDD
        , iS10T003TRecord.NYKNKIND
        , iS10T003TRecord.NYKNKIN
        , iS10T003TRecord.NEBIKIKIN
        , iS10T003TRecord.TESURYO
        , iS10T003TRecord.KAIREKIN
        , iS10T003TRecord.URICHOSEIKIN
        , iS10T003TRecord.TRKSDNPYNO
        , iS10T003TRecord.KAIREKINKANRIFLG
        , '1'
        , vUserApkihoncd
        , vUserShopcd
        , vTantoshacd
        , vSystemDate
        , vSystemTime
        , vOperationDate
        , vIpAddress
        , vProgramId
        , vUserApkihoncd
        , vUserShopcd
        , vTantoshacd
        , vSystemDate
        , vSystemTime
        , vOperationDate
        , vIpAddress
        , vProgramId
    );
  END InsertS10T003T;

  /*****************************************************************************/
  /* プロシージャ名  ：入金消込履歴テーブル登録                                */
  /* 引数            ：iS10T004TRecord       入金消込履歴テーブルレコード      */
  /*****************************************************************************/
  PROCEDURE InsertS10T004T(iS10T004TRecord IN S10T004T%ROWTYPE) IS
  BEGIN
    -- 入金消込履歴テーブル登録
    INSERT INTO S10T004T(
          APKIHONCD
        , SHOPCD
        , NYKNDNPYNO
        , NYKNDNPYNOEDANO
        , SEQ
        , NYKNKSHKMPRCSKBN
        , SKYSAKICD
        , TOKUISAKICD
        , NYKNDD
        , KSHKMDD
        , NYKNKIND
        , KSHKMNYKNKIND
        , KESHIKOMIKIN
        , SHOKAISKYSHONO
        , DAIBIKISKYSHONO
        , SKYPRCSJISSIKBN
        , TRKSDNPYNO
        , DSPLYKBN
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
    ) VALUES (
          vApkihoncd
        , vShopcd
        , iS10T004TRecord.NYKNDNPYNO
        , iS10T004TRecord.NYKNDNPYNOEDANO
        , iS10T004TRecord.SEQ
        , iS10T004TRecord.NYKNKSHKMPRCSKBN
        , iS10T004TRecord.SKYSAKICD
        , iS10T004TRecord.TOKUISAKICD
        , iS10T004TRecord.NYKNDD
        , iS10T004TRecord.KSHKMDD
        , iS10T004TRecord.NYKNKIND
        , iS10T004TRecord.KSHKMNYKNKIND
        , iS10T004TRecord.KESHIKOMIKIN
        , iS10T004TRecord.SHOKAISKYSHONO
        , '000000000000'
        , iS10T004TRecord.SKYPRCSJISSIKBN
        , iS10T004TRecord.TRKSDNPYNO
        , '1'
        , '1'
        , vUserApkihoncd
        , vUserShopcd
        , vTantoshacd
        , vSystemDate
        , vSystemTime
        , vOperationDate
        , vIpAddress
        , vProgramId
        , vUserApkihoncd
        , vUserShopcd
        , vTantoshacd
        , vSystemDate
        , vSystemTime
        , vOperationDate
        , vIpAddress
        , vProgramId
    );
  END InsertS10T004T;

  /*****************************************************************************/
  /* プロシージャ名  ：入金取込ワーク更新                                      */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iDatsksmotokbn        データ作成元区分                  */
  /*                 ：iDenpyonofrom         入金伝票番号開始値                */
  /*                 ：iDenpyonoto           入金伝票番号終了値                */
  /*                 ：iNykndatsksflg        入金データ作成フラグ              */
  /*                 ：iSkysakicd            請求先コード                      */
  /*****************************************************************************/
  PROCEDURE UpdateS10W001T(iApkihoncd     IN S10W001T.APKIHONCD%TYPE
                          ,iShopcd        IN S10W001T.SHOPCD%TYPE
                          ,iDatsksmotokbn IN S10W001T.DATSKSMOTOKBN%TYPE
                          ,iDenpyonofrom  IN S10W001T.DNPYNO%TYPE
                          ,iDenpyonoto    IN S10W001T.DNPYNO%TYPE
                          ,iNykndatsksflg IN S10W001T.NYKNDATSKSFLG%TYPE
                          ,iSkysakicd     IN S10W002T.SKYSAKICD%TYPE) IS
  BEGIN
    -- 入金取込ワーク更新
    UPDATE S10W001T
    SET
        NYKNDATSKSFLG   = iNykndatsksflg
      , KOSHAAPKIHONCD  = vUserApkihoncd
      , KOSHASHOPCD     = vUserShopcd
      , KOSHACD         = vTantoshacd
      , KSHNSYSTMYMD    = vSystemDate
      , KSHNSYSTMJIKAN  = vSystemTime
      , KSHNUNYOYMD     = vOperationDate
      , KSHNIPADRSE     = vIpAddress
      , KSHNPGMID       = vProgramId
    WHERE
          APKIHONCD     = iApkihoncd
      AND SHOPCD        = iShopcd
      AND DATSKSMOTOKBN = iDatsksmotokbn
      AND NYKNDATSKSFLG = '0'
      AND KSHNUNYOYMD   <= vOperationDate
      AND DNPYNO BETWEEN iDenpyonofrom AND iDenpyonoto
      AND (iSkysakicd IS NULL OR SKYSAKICD  = iSkysakicd)
    ;
  END UpdateS10W001T;

  /*****************************************************************************/
  /* プロシージャ名  ：入金取込得意先ワーク削除                                */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iTantoshacd           担当者コード                      */
  /*                 ：iSkysakicd            請求先コード                      */
  /*****************************************************************************/
  PROCEDURE DeleteS10W002T(iApkihoncd  IN S10W002T.APKIHONCD%TYPE
                          ,iShopcd     IN S10W002T.SHOPCD%TYPE
                          ,iTantoshacd IN S10W002T.PRCSTNSHACD%TYPE
                          ,iSkysakicd  IN S10W002T.SKYSAKICD%TYPE) IS
  BEGIN
    -- 入金取込得意先ワーク削除
    DELETE FROM S10W002T
    WHERE
          APKIHONCD   = iApkihoncd
      AND SHOPCD      = iShopcd
      AND PRCSTNSHACD = iTantoshacd
      AND SKYSAKICD   = iSkysakicd
    ;
  END DeleteS10W002T;

  /*****************************************************************************/
  /* プロシージャ名  ：入金取込得意先ワーク登録・更新                          */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iSkysakicd            請求先コード                      */
  /*                 ：iTokuisakicd          得意先コード                      */
  /*                 ：iNyknkin              入金金額                          */
  /*****************************************************************************/
  PROCEDURE MergeS10W002T(iApkihoncd    IN S10W002T.APKIHONCD%TYPE
                         ,iShopcd       IN S10W002T.SHOPCD%TYPE
                         ,iSkysakicd    IN S10W002T.SKYSAKICD%TYPE
                         ,iTokuisakicd  IN S10W002T.TOKUISAKICD%TYPE
                         ,iNyknkin      IN S10W002T.NYKNKIN%TYPE) IS
  BEGIN
    -- 入金取込得意先ワーク登録・更新
    MERGE INTO S10W002T
    USING DUAL ON(
          APKIHONCD       = iApkihoncd
      AND SHOPCD          = iShopcd
      AND PRCSTNSHACD     = vTantoshacd
      AND SKYSAKICD       = iSkysakicd
      AND TOKUISAKICD     = iTokuisakicd
    )
    WHEN MATCHED THEN
      UPDATE SET
          NYKNKIN         = NYKNKIN + iNyknkin
        , GENZAIZD        = NULL
        , KOSHAAPKIHONCD  = vUserApkihoncd
        , KOSHASHOPCD     = vUserShopcd
        , KOSHACD         = vTantoshacd
        , KSHNSYSTMYMD    = vSystemDate
        , KSHNSYSTMJIKAN  = vSystemTime
        , KSHNUNYOYMD     = vOperationDate
        , KSHNIPADRSE     = vIpAddress
        , KSHNPGMID       = vProgramId
    WHEN NOT MATCHED THEN
      INSERT (
          APKIHONCD
        , SHOPCD
        , PRCSTNSHACD
        , SKYSAKICD
        , TOKUISAKICD
        , NYKNKIN
        , GENZAIZD
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
    ) VALUES (
          iApkihoncd
        , iShopcd
        , vTantoshacd
        , iSkysakicd
        , iTokuisakicd
        , iNyknkin
        , NULL
        , vUserApkihoncd
        , vUserShopcd
        , vTantoshacd
        , vSystemDate
        , vSystemTime
        , vOperationDate
        , vIpAddress
        , vProgramId
        , vUserApkihoncd
        , vUserShopcd
        , vTantoshacd
        , vSystemDate
        , vSystemTime
        , vOperationDate
        , vIpAddress
        , vProgramId
      );
  END MergeS10W002T;

  /*****************************************************************************/
  /* プロシージャ名  ：入金消込ワーク削除                                      */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iDenpyono             伝票番号                          */
  /*****************************************************************************/
  PROCEDURE DeleteS10W003T(iApkihoncd IN S10W003T.APKIHONCD%TYPE
                          ,iShopcd    IN S10W003T.SHOPCD%TYPE
                          ,iDenpyono  IN S10W003T.DENPYONO%TYPE) IS
  BEGIN
    -- 入金消込ワーク削除
    DELETE FROM S10W003T
    WHERE
          APKIHONCD   = iApkihoncd
      AND SHOPCD      = iShopcd
      AND DENPYONO    = iDenpyono
    ;
  END DeleteS10W003T;

  /*****************************************************************************/
  /* プロシージャ名  ：過入金元更新                                            */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iSkysakicd            請求先コード                      */
  /*                 ：iDnpyno               伝票番号                          */
  /*                 ：iNykndnpynoedano      入金伝票番号枝番                  */
  /*                 ：iNykndd               入金日                            */
  /*                 ：iNyknkind             入金種別                          */
  /*                 ：iDatsksmotokbn        データ作成元区分                  */
  /*                 ：iKaishutnshacd        回収担当者コード                  */
  /*                 ：iSosaikin             相殺金額                          */
  /*                 ：iKyknMotoTokuisakicd  過入金元得意先コード              */
  /*                 ：iSkyshono             請求書番号                        */
  /*                 ：iSkyprcsjissikbn      請求処理実施区分                  */
  /*                 ：iSosaiSakiTokuisakicd 相殺先得意先コード                */
  /*****************************************************************************/
  PROCEDURE UpdateKanyukinMoto(iApkihoncd            IN S10T001T.APKIHONCD%TYPE
                              ,iShopcd               IN S10T001T.SHOPCD%TYPE
                              ,iSkysakicd            IN S10T001T.SKYSAKICD%TYPE
                              ,iDnpyno               IN S10T001T.DNPYNO%TYPE
                              ,iNykndnpynoedano      IN S10T001T.NYKNDNPYNOEDANO%TYPE
                              ,iNykndd               IN S10T001T.NYKNDD%TYPE
                              ,iNyknkind             IN S10T001T.NYKNKIND%TYPE
                              ,iDatsksmotokbn        IN S10T001T.DATSKSMOTOKBN%TYPE
                              ,iKaishutnshacd        IN S10T001T.KAISHUTNSHACD%TYPE
                              ,iSosaikin             IN S10T001T.KESHIKOMIKIN%TYPE
                              ,iKyknMotoTokuisakicd  IN S10T001T.TOKUISAKICD%TYPE
                              ,iSkyshono             IN S10T004T.SHOKAISKYSHONO%TYPE
                              ,iSkyprcsjissikbn      IN S10T004T.SKYPRCSJISSIKBN%TYPE
                              ,iSosaiSakiTokuisakicd IN S10T004T.TOKUISAKICD%TYPE) IS
    ---------------------------
    -- 変数定義
    ---------------------------
    wKairekin         S10T003T.KAIREKIN%TYPE;         -- 過入金額
    wTrksdnpyno       S10T003T.TRKSDNPYNO%TYPE;       -- 取消伝票番号
    wKairekinkanriflg S10T003T.KAIREKINKANRIFLG%TYPE; -- 過入金管理フラグ
    wSosaiEdaNo       S10T001T.NYKNDNPYNOEDANO%TYPE;  -- 入金伝票番号枝番号
    wSosaiSeq         S10T004T.SEQ%TYPE;              -- SEQ
    ---------------------------
    -- レコード定義
    ---------------------------
    wS10T001TRecord   S10T001T%ROWTYPE;               -- 入金明細テーブルレコード
    wS10T004TRecord   S10T004T%ROWTYPE;               -- 入金消込履歴テーブルレコード
  BEGIN
    -- 過入金額、取消伝票番号取得（入金管理テーブル）
    SELECT
      KAIREKIN, TRKSDNPYNO
    INTO
      wKairekin, wTrksdnpyno
    FROM
      S10T003T
    WHERE
          APKIHONCD  = iApkihoncd
      AND SHOPCD     = iShopcd
      AND NYKNDNPYNO = iDnpyno
    ;
    -- 過入金管理フラグ設定
    wKairekinkanriflg := (CASE WHEN wKairekin - iSosaikin = 0 THEN '0' ELSE NULL END);
    -- 入金管理テーブル更新
    UpdateS10T003T(iApkihoncd, iShopcd, iDnpyno, wKairekinkanriflg, wKairekin - iSosaikin, wTrksdnpyno);
    -- 入金明細テーブル更新（相殺金額）
    UpdateS10T001TForSosaikin(iApkihoncd, iShopcd, iDnpyno, iNykndnpynoedano, iSosaikin);
    -- 入金伝票番号枝番号最大値取得
    wSosaiEdaNo := GetMaxNykndnpynoedano(iApkihoncd, iShopcd, iDnpyno);
    -- SEQ最大値取得
    wSosaiSeq := GetMaxSeq(iApkihoncd, iShopcd, iDnpyno);

    -- 過入元得意先コード＝相殺先得意先コードの場合
    IF iKyknMotoTokuisakicd = iSosaiSakiTokuisakicd THEN
      -- 入金明細テーブル登録
      wS10T001TRecord.APKIHONCD             := iApkihoncd;
      wS10T001TRecord.SHOPCD                := iShopcd;
      wS10T001TRecord.DNPYNO                := iDnpyno;
      wS10T001TRecord.NYKNDNPYNOEDANO       := wSosaiEdaNo + 1;
      wS10T001TRecord.KAIREKINKBN           := '1'; -- （過入金移動）
      wS10T001TRecord.SKYSAKICD             := iSkysakicd;
      wS10T001TRecord.TOKUISAKICD           := iKyknMotoTokuisakicd;
      wS10T001TRecord.NYKNDD                := iNykndd;
      wS10T001TRecord.NYKNKIND              := iNyknkind;
      wS10T001TRecord.NYKNKIN               := 0;
      wS10T001TRecord.NEBIKIKIN             := 0;
      wS10T001TRecord.TESURYO               := 0;
      wS10T001TRecord.BIKO                  := NULL;
      wS10T001TRecord.KESHIKOMIKIN          := iSosaikin;
      wS10T001TRecord.SOSAIKIN              := iSosaikin * -1;
      wS10T001TRecord.DATSKSMOTOKBN         := iDatsksmotokbn;
      wS10T001TRecord.SKYSHOOTPTARINASHIKBN := '1'; -- （出力なし）
      wS10T001TRecord.KAISHUTNSHACD         := iKaishutnshacd;
      wS10T001TRecord.TRKSDNPYNO            := wTrksdnpyno;
      InsertS10T001T(wS10T001TRecord);

      -- 入金消込履歴テーブル登録
      wS10T004TRecord.NYKNDNPYNO            := iDnpyno;
      wS10T004TRecord.NYKNDNPYNOEDANO       := wSosaiEdaNo + 1;
      wS10T004TRecord.SEQ                   := wSosaiSeq + 1;
      wS10T004TRecord.NYKNKSHKMPRCSKBN      := (CASE WHEN iDatsksmotokbn = '07' THEN '10' ELSE '20' END);
      wS10T004TRecord.SKYSAKICD             := iSkysakicd;
      wS10T004TRecord.TOKUISAKICD           := iKyknMotoTokuisakicd;
      wS10T004TRecord.NYKNDD                := iNykndd;
      wS10T004TRecord.KSHKMDD               := vOperationDate;
      wS10T004TRecord.NYKNKIND              := iNyknkind;
      wS10T004TRecord.KSHKMNYKNKIND         := '30'; -- （過入金）
      wS10T004TRecord.KESHIKOMIKIN          := iSosaikin;
      wS10T004TRecord.SHOKAISKYSHONO        := iSkyshono;
      wS10T004TRecord.TRKSDNPYNO            := wTrksdnpyno;
      wS10T004TRecord.SKYPRCSJISSIKBN       := iSkyprcsjissikbn;
      InsertS10T004T(wS10T004TRecord);

    -- 過入元得意先コード≠相殺先得意先コードの場合
    ELSE
      -- 入金明細テーブル登録（相殺移動データ①）
      wS10T001TRecord.APKIHONCD             := iApkihoncd;
      wS10T001TRecord.SHOPCD                := iShopcd;
      wS10T001TRecord.DNPYNO                := iDnpyno;
      wS10T001TRecord.NYKNDNPYNOEDANO       := wSosaiEdaNo + 1;
      wS10T001TRecord.KAIREKINKBN           := '1'; -- （過入金移動）
      wS10T001TRecord.SKYSAKICD             := iSkysakicd;
      wS10T001TRecord.TOKUISAKICD           := iKyknMotoTokuisakicd;
      wS10T001TRecord.NYKNDD                := iNykndd;
      wS10T001TRecord.NYKNKIND              := iNyknkind;
      wS10T001TRecord.NYKNKIN               := GetNyknkin(iNyknkind, iSosaikin * -1);
      wS10T001TRecord.NEBIKIKIN             := GetNebikikin(iNyknkind, iSosaikin * -1);
      wS10T001TRecord.TESURYO               := GetTesuryo(iNyknkind, iSosaikin * -1);
      wS10T001TRecord.BIKO                  := NULL;
      wS10T001TRecord.KESHIKOMIKIN          := 0;
      wS10T001TRecord.SOSAIKIN              := iSosaikin * -1;
      wS10T001TRecord.DATSKSMOTOKBN         := iDatsksmotokbn;
      wS10T001TRecord.SKYSHOOTPTARINASHIKBN := '1'; -- （出力なし）
      wS10T001TRecord.KAISHUTNSHACD         := iKaishutnshacd;
      wS10T001TRecord.TRKSDNPYNO            := wTrksdnpyno;
      InsertS10T001T(wS10T001TRecord);

      -- 入金取込得意先ワーク登録・更新①
      MergeS10W002T(iApkihoncd, iShopcd, iSkysakicd, iKyknMotoTokuisakicd, iSosaikin * -1);

      -- 入金明細テーブル登録（相殺移動データ②）
      wS10T001TRecord.APKIHONCD             := iApkihoncd;
      wS10T001TRecord.SHOPCD                := iShopcd;
      wS10T001TRecord.DNPYNO                := iDnpyno;
      wS10T001TRecord.NYKNDNPYNOEDANO       := wSosaiEdaNo + 2;
      wS10T001TRecord.KAIREKINKBN           := '1'; -- （過入金移動）
      wS10T001TRecord.SKYSAKICD             := iSkysakicd;
      wS10T001TRecord.TOKUISAKICD           := iSosaiSakiTokuisakicd;
      wS10T001TRecord.NYKNDD                := iNykndd;
      wS10T001TRecord.NYKNKIND              := iNyknkind;
      wS10T001TRecord.NYKNKIN               := GetNyknkin(iNyknkind, iSosaikin);
      wS10T001TRecord.NEBIKIKIN             := GetNebikikin(iNyknkind, iSosaikin);
      wS10T001TRecord.TESURYO               := GetTesuryo(iNyknkind, iSosaikin);
      wS10T001TRecord.BIKO                  := NULL;
      wS10T001TRecord.KESHIKOMIKIN          := iSosaikin;
      wS10T001TRecord.SOSAIKIN              := 0;
      wS10T001TRecord.DATSKSMOTOKBN         := iDatsksmotokbn;
      wS10T001TRecord.SKYSHOOTPTARINASHIKBN := '1'; -- （出力なし）
      wS10T001TRecord.KAISHUTNSHACD         := iKaishutnshacd;
      wS10T001TRecord.TRKSDNPYNO            := wTrksdnpyno;
      InsertS10T001T(wS10T001TRecord);

      -- 入金取込得意先ワーク登録・更新②
      MergeS10W002T(iApkihoncd, iShopcd, iSkysakicd, iSosaiSakiTokuisakicd, iSosaikin);

      -- 入金消込履歴テーブル登録
      wS10T004TRecord.NYKNDNPYNO            := iDnpyno;
      wS10T004TRecord.NYKNDNPYNOEDANO       := wSosaiEdaNo + 2;
      wS10T004TRecord.SEQ                   := wSosaiSeq + 1;
      wS10T004TRecord.NYKNKSHKMPRCSKBN      := (CASE WHEN iDatsksmotokbn = '07' THEN '10' ELSE '20' END);
      wS10T004TRecord.SKYSAKICD             := iSkysakicd;
      wS10T004TRecord.TOKUISAKICD           := iSosaiSakiTokuisakicd;
      wS10T004TRecord.NYKNDD                := iNykndd;
      wS10T004TRecord.KSHKMDD               := vOperationDate;
      wS10T004TRecord.NYKNKIND              := iNyknkind;
      wS10T004TRecord.KSHKMNYKNKIND         := '30'; -- （過入金）
      wS10T004TRecord.KESHIKOMIKIN          := iSosaikin;
      wS10T004TRecord.SHOKAISKYSHONO        := iSkyshono;
      wS10T004TRecord.TRKSDNPYNO            := wTrksdnpyno;
      wS10T004TRecord.SKYPRCSJISSIKBN       := iSkyprcsjissikbn;
      InsertS10T004T(wS10T004TRecord);
    END IF;
  END UpdateKanyukinMoto;

  /*****************************************************************************/
  /* プロシージャ名  ：過入金反映更新                                          */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iSkysakicd            請求先コード                      */
  /*                 ：iNyukinImpWorkRecord  入金取込ワークデータ              */
  /*                 ：iKairekin             過入金額                          */
  /*                 ：iClearingFlg          消込ありフラグ                    */
  /*                 ：iDatsksmotokbn        データ作成元区分                  */
  /*                 ：iKaishutnshacd        回収担当者コード                  */
  /*****************************************************************************/
  PROCEDURE UpdateKanyukinReflection(iApkihoncd           IN S10T001T.APKIHONCD%TYPE
                                    ,iShopcd              IN S10T001T.SHOPCD%TYPE
                                    ,iSkysakicd           IN S10T001T.SKYSAKICD%TYPE
                                    ,iNyukinImpWorkRecord IN vTypeNyukinImportWorkListRecord
                                    ,iKairekin            IN NUMBER
                                    ,iClearingFlg         IN VARCHAR2
                                    ,iDatsksmotokbn       IN S10T001T.DATSKSMOTOKBN%TYPE
                                    ,iKaishutnshacd       IN S10T001T.KAISHUTNSHACD%TYPE) IS
    ---------------------------
    -- 変数定義
    ---------------------------
    wKairekinTokuisakicd  S10T001T.TOKUISAKICD%TYPE;      -- 過入金得意先コード
    wTokuisakiEdaNo       S10T001T.NYKNDNPYNOEDANO%TYPE;  -- 入金枝番号
    wNyukinEdaNo          S10T001T.NYKNDNPYNOEDANO%TYPE;  -- 入金枝番号
    wNyknkin              S10T001T.NYKNKIN%TYPE;          -- 入金金額
    wNebikikin            S10T001T.NEBIKIKIN%TYPE;        -- 値引金額
    wTesuryo              S10T001T.TESURYO%TYPE;          -- 手数料
    ---------------------------
    -- レコード定義
    ---------------------------
    wS10T001TRecord       S10T001T%ROWTYPE;               -- 入金明細テーブルレコード
    wS10T004TRecord       S10T004T%ROWTYPE;               -- 入金消込履歴テーブルレコード
  BEGIN
    -- 過入金得意先コード取得
    GetKanyukinTokuisakicd(iApkihoncd, iShopcd, iSkysakicd,
      iNyukinImpWorkRecord.DNPYNO, iClearingFlg, wKairekinTokuisakicd);

    -- 入金伝票番号枝番号最大値取得（入金明細テーブル）
    SELECT
      MIN(CASE WHEN TOKUISAKICD = wKairekinTokuisakicd THEN NYKNDNPYNOEDANO ELSE 99999 END), NVL(MAX(NYKNDNPYNOEDANO), 0)
    INTO
      wTokuisakiEdaNo, wNyukinEdaNo
    FROM
      S10T001T
    WHERE
          APKIHONCD = iApkihoncd
      AND SHOPCD    = iShopcd
      AND DNPYNO    = iNyukinImpWorkRecord.DNPYNO
      AND NYKNKIND  = iNyukinImpWorkRecord.NYKNKIND
    ;

    -- 入金明細テーブルデータ存在なしの場合
    IF wTokuisakiEdaNo IS NULL OR wTokuisakiEdaNo = 99999 THEN
      -- 入金明細テーブル登録
      wS10T001TRecord.APKIHONCD             := iApkihoncd;
      wS10T001TRecord.SHOPCD                := iShopcd;
      wS10T001TRecord.DNPYNO                := iNyukinImpWorkRecord.DNPYNO;
      wS10T001TRecord.NYKNDNPYNOEDANO       := wNyukinEdaNo + 1;
      wS10T001TRecord.KAIREKINKBN           := '0'; -- （入金）
      wS10T001TRecord.SKYSAKICD             := iSkysakicd;
      wS10T001TRecord.TOKUISAKICD           := wKairekinTokuisakicd;
      wS10T001TRecord.NYKNDD                := iNyukinImpWorkRecord.NYKNDD;
      wS10T001TRecord.NYKNKIND              := iNyukinImpWorkRecord.NYKNKIND;
      wS10T001TRecord.NYKNKIN               := GetNyknkin(iNyukinImpWorkRecord.NYKNKIND, iKairekin);
      wS10T001TRecord.NEBIKIKIN             := GetNebikikin(iNyukinImpWorkRecord.NYKNKIND, iKairekin);
      wS10T001TRecord.TESURYO               := GetTesuryo(iNyukinImpWorkRecord.NYKNKIND, iKairekin);
      wS10T001TRecord.BIKO                  := iNyukinImpWorkRecord.BIKO;
      wS10T001TRecord.KESHIKOMIKIN          := 0;
      wS10T001TRecord.SOSAIKIN              := 0;
      wS10T001TRecord.DATSKSMOTOKBN         := iDatsksmotokbn;
      wS10T001TRecord.SKYSHOOTPTARINASHIKBN := '0'; -- （出力あり）
      wS10T001TRecord.KAISHUTNSHACD         := iKaishutnshacd;
      wS10T001TRecord.TRKSDNPYNO            := NULL;
      InsertS10T001T(wS10T001TRecord);
    ELSE
      -- 金額項目設定（入金金額、値引金額、手数料）
      wNyknkin := GetNyknkin(iNyukinImpWorkRecord.NYKNKIND, iKairekin);
      wNebikikin := GetNebikikin(iNyukinImpWorkRecord.NYKNKIND, iKairekin);
      wTesuryo := GetTesuryo(iNyukinImpWorkRecord.NYKNKIND, iKairekin);

      -- 入金明細テーブル更新（入金金額、値引金額、手数料）
      UpdateS10T001TForKingaku(iApkihoncd, iShopcd, iNyukinImpWorkRecord.DNPYNO,
        wNyukinEdaNo, iNyukinImpWorkRecord.NYKNKIND, wNyknkin, wNebikikin, wTesuryo);
    END IF;

    -- 入金管理テーブル更新
    UpdateS10T003T(iApkihoncd, iShopcd, iNyukinImpWorkRecord.DNPYNO, '1', iKairekin, NULL);

    -- 入金取込得意先ワーク登録・更新
    MergeS10W002T(iApkihoncd, iShopcd, iSkysakicd, wKairekinTokuisakicd, iKairekin);

  END UpdateKanyukinReflection;

  /*****************************************************************************/
  /* プロシージャ名  ：過入金反映更新（入金入力）                              */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iSkysakicd            請求先コード                      */
  /*                 ：iNyukinImpWorkRecord  入金取込ワークデータ              */
  /*                 ：iNyukinkind           入金種別                          */
  /*                 ：iClearingFlg          消込ありフラグ                    */
  /*                 ：iDatsksmotokbn        データ作成元区分                  */
  /*                 ：iKaishutnshacd        回収担当者コード                  */
  /*****************************************************************************/
  PROCEDURE NyukinInputUpdateKanyukinReflection(iApkihoncd           IN S10T001T.APKIHONCD%TYPE
                                               ,iShopcd              IN S10T001T.SHOPCD%TYPE
                                               ,iSkysakicd           IN S10T001T.SKYSAKICD%TYPE
                                               ,iNyukinImpWorkRecord IN vTypeNyukinImportWorkListRecord
                                               ,iNyukinkind          IN S10T001T.NYKNKIND%TYPE
                                               ,iClearingFlg         IN VARCHAR2
                                               ,iDatsksmotokbn       IN S10T001T.DATSKSMOTOKBN%TYPE
                                               ,iKaishutnshacd       IN S10T001T.KAISHUTNSHACD%TYPE) IS
    ---------------------------
    -- 変数定義
    ---------------------------
    wKairekinTokuisakicd  S10T001T.TOKUISAKICD%TYPE;      -- 過入金得意先コード
    wTokuisakiEdaNo       S10T001T.NYKNDNPYNOEDANO%TYPE;  -- 入金枝番号
    wNyukinEdaNo          S10T001T.NYKNDNPYNOEDANO%TYPE;  -- 入金枝番号
    wNyknkin              S10T001T.NYKNKIN%TYPE;          -- 入金金額
    wNebikikin            S10T001T.NEBIKIKIN%TYPE;        -- 値引金額
    wTesuryo              S10T001T.TESURYO%TYPE;          -- 手数料
    wKairekin             NUMBER;                         -- 過入金
    ---------------------------
    -- レコード定義
    ---------------------------
    wS10T001TRecord       S10T001T%ROWTYPE;               -- 入金明細テーブルレコード
  BEGIN
    -- 過入金得意先コード取得
    GetKanyukinTokuisakicd(iApkihoncd, iShopcd, iSkysakicd,
      iNyukinImpWorkRecord.DNPYNO, iClearingFlg, wKairekinTokuisakicd);

    -- 入金伝票番号枝番号最大値取得（入金明細テーブル）
    SELECT
      MIN(CASE WHEN TOKUISAKICD = wKairekinTokuisakicd THEN NYKNDNPYNOEDANO ELSE 99999 END), NVL(MAX(NYKNDNPYNOEDANO), 0)
    INTO
      wTokuisakiEdaNo, wNyukinEdaNo
    FROM
      S10T001T
    WHERE
          APKIHONCD = iApkihoncd
      AND SHOPCD    = iShopcd
      AND DNPYNO    = iNyukinImpWorkRecord.DNPYNO
      AND NYKNKIND  = iNyukinKind
    ;

    -- 入金種別に応じた金額を過入金に設定
    IF iNyukinkind = '20' THEN
      -- 手数料
      wKairekin := iNyukinImpWorkRecord.TESURYO;
    ELSIF iNyukinkind = '21' THEN
      -- 値引金額
      wKairekin := iNyukinImpWorkRecord.NEBIKIKIN;
    ELSE
      -- 入金金額
      wKairekin := iNyukinImpWorkRecord.NYKNKIN;
    END IF;

    -- 入金明細テーブルデータ存在なしの場合
    IF wTokuisakiEdaNo IS NULL OR wTokuisakiEdaNo = 99999 THEN
      wNyukinEdaNo := GetMaxNykndnpynoedano(iApkihoncd, iShopcd, iNyukinImpWorkRecord.DNPYNO);
      -- 入金明細テーブル登録
      wS10T001TRecord.APKIHONCD             := iApkihoncd;
      wS10T001TRecord.SHOPCD                := iShopcd;
      wS10T001TRecord.DNPYNO                := iNyukinImpWorkRecord.DNPYNO;
      wS10T001TRecord.NYKNDNPYNOEDANO       := wNyukinEdaNo + 1;
      wS10T001TRecord.KAIREKINKBN           := '0'; -- （入金）
      wS10T001TRecord.SKYSAKICD             := iSkysakicd;
      wS10T001TRecord.TOKUISAKICD           := wKairekinTokuisakicd;
      wS10T001TRecord.NYKNDD                := iNyukinImpWorkRecord.NYKNDD;
      wS10T001TRecord.NYKNKIND              := iNyukinkind;
      wS10T001TRecord.NYKNKIN               := GetNyknkin(iNyukinkind, wKairekin);
      wS10T001TRecord.NEBIKIKIN             := GetNebikikin(iNyukinkind, wKairekin);
      wS10T001TRecord.TESURYO               := GetTesuryo(iNyukinkind, wKairekin);
      wS10T001TRecord.BIKO                  := iNyukinImpWorkRecord.BIKO;
      wS10T001TRecord.KESHIKOMIKIN          := 0;
      wS10T001TRecord.SOSAIKIN              := 0;
      wS10T001TRecord.DATSKSMOTOKBN         := iDatsksmotokbn;
      wS10T001TRecord.SKYSHOOTPTARINASHIKBN := '0'; -- （出力あり）
      wS10T001TRecord.KAISHUTNSHACD         := iKaishutnshacd;
      wS10T001TRecord.TRKSDNPYNO            := NULL;
      InsertS10T001T(wS10T001TRecord);
    ELSE
      -- 金額項目設定（入金金額、値引金額、手数料）
      wNyknkin := GetNyknkin(iNyukinkind, wKairekin);
      wNebikikin := GetNebikikin(iNyukinkind, wKairekin);
      wTesuryo := GetTesuryo(iNyukinkind, wKairekin);

      -- 入金明細テーブル更新（入金金額、値引金額、手数料）
      UpdateS10T001TForKingaku(iApkihoncd, iShopcd, iNyukinImpWorkRecord.DNPYNO,
        wNyukinEdaNo, iNyukinkind, wNyknkin, wNebikikin, wTesuryo);
    END IF;

    -- ※入金管理テーブルへのか入金額の反映は呼び元で実施する

    -- 入金取込得意先ワーク登録・更新
    MergeS10W002T(iApkihoncd, iShopcd, iSkysakicd, wKairekinTokuisakicd, wKairekin);

  END NyukinInputUpdateKanyukinReflection;

  /*****************************************************************************/
  /* プロシージャ名  ：過入金相殺処理                                          */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iSkysakicd            請求先コード                      */
  /*                 ：iKairekinList         過入金リスト                      */
  /*                 ：iSosaikin             相殺金額                          */
  /*                 ：iDatsksmotokbn        データ作成元区分                  */
  /*                 ：iKaishutnshacd        回収担当者コード                  */
  /*                 ：iNyukinImpWorkRecord  入金取込ワークデータ              */
  /*                 ：iSosaiTokuisakicd     相殺得意先コード                  */
  /*                 ：oSumSosaikin          合計相殺金額                      */
  /*****************************************************************************/
  PROCEDURE KanyukinSosaiProcess(iApkihoncd           IN S10T001T.APKIHONCD%TYPE
                                ,iShopcd              IN S10T001T.SHOPCD%TYPE
                                ,iSkysakicd           IN S10T001T.SKYSAKICD%TYPE
                                ,iKairekinList        IN vTypeKanyukinList
                                ,iSosaikin            IN NUMBER
                                ,iDatsksmotokbn       IN S10T001T.DATSKSMOTOKBN%TYPE
                                ,iKaishutnshacd       IN S10T001T.KAISHUTNSHACD%TYPE
                                ,iNyukinImpWorkRecord IN vTypeNyukinImportWorkListRecord
                                ,iSosaiTokuisakicd    IN S10T001T.TOKUISAKICD%TYPE
                                ,oSumSosaikin         OUT NUMBER) IS
    ---------------------------
    -- 変数定義
    ---------------------------
    wKairekinzan     NUMBER := iSosaikin;           -- 過入金残
    wSosaikin        NUMBER;                        -- 相殺金額
    wSumSosaikin     NUMBER := 0;                   -- 合計相殺金額
    wNykndnpynoedano S10T001T.NYKNDNPYNOEDANO%TYPE; -- 入金伝票番号枝番号
    wSeq             S10T004T.SEQ%TYPE;             -- SEQ
    ---------------------------
    -- レコード定義
    ---------------------------
    wS10T001TRecord  S10T001T%ROWTYPE;              -- 入金明細テーブルレコード
    wS10T004TRecord  S10T004T%ROWTYPE;              -- 入金消込履歴テーブルレコード
  BEGIN
    -- 過入金リストループ
    FOR i IN iKairekinList.FIRST..iKairekinList.LAST LOOP
      IF wKairekinzan = 0 THEN
        EXIT; -- （過入金残＝0の場合ループを抜ける）
      END IF;

      -- 相殺金額設定（過入金残（絶対値）≦過入金額（絶対値）：過入金残／その他：過入金額）
      wSosaikin := (
        CASE
          WHEN ABS(wKairekinzan) <= ABS(iKairekinList(i).KAIREKIN) THEN wKairekinzan
          ELSE iKairekinList(i).KAIREKIN
        END);

      -- 過入金元更新
      UpdateKanyukinMoto(iApkihoncd, iShopcd, iSkysakicd, iKairekinList(i).DNPYNO,
        iKairekinList(i).NYKNDNPYNOEDANO, iKairekinList(i).NYKNDD, iKairekinList(i).NYKNKIND,
        iDatsksmotokbn, iKaishutnshacd, wSosaikin, iKairekinList(i).TOKUISAKICD,
        NULL, 0, NVL(iSosaiTokuisakicd, iKairekinList(i).TOKUISAKICD));

      -- 入金取込ワークデータ≠NULLの場合
      IF iNyukinImpWorkRecord.DNPYNO IS NOT NULL THEN
        -- 入金伝票番号枝番号、SEQ最大値取得
        wNykndnpynoedano := GetMaxNykndnpynoedano(iApkihoncd, iShopcd, iNyukinImpWorkRecord.DNPYNO);
        wSeq             := GetMaxSeq(iApkihoncd, iShopcd, iNyukinImpWorkRecord.DNPYNO);

        -- 入金明細テーブル登録
        wS10T001TRecord.APKIHONCD             := iApkihoncd;
        wS10T001TRecord.SHOPCD                := iShopcd;
        wS10T001TRecord.DNPYNO                := iNyukinImpWorkRecord.DNPYNO;
        wS10T001TRecord.NYKNDNPYNOEDANO       := wNykndnpynoedano + 1;
        wS10T001TRecord.KAIREKINKBN           := '0'; -- （入金）
        wS10T001TRecord.SKYSAKICD             := iSkysakicd;
        wS10T001TRecord.TOKUISAKICD           := iKairekinList(i).TOKUISAKICD;
        wS10T001TRecord.NYKNDD                := iNyukinImpWorkRecord.NYKNDD;
        wS10T001TRecord.NYKNKIND              := iNyukinImpWorkRecord.NYKNKIND;
        wS10T001TRecord.NYKNKIN               := GetNyknkin(iKairekinList(i).NYKNKIND, wSosaikin * -1);
        wS10T001TRecord.NEBIKIKIN             := GetNebikikin(iKairekinList(i).NYKNKIND, wSosaikin * -1);
        wS10T001TRecord.TESURYO               := GetTesuryo(iKairekinList(i).NYKNKIND, wSosaikin * -1);
        wS10T001TRecord.BIKO                  := iNyukinImpWorkRecord.BIKO;
        wS10T001TRecord.KESHIKOMIKIN          := wSosaikin * -1;
        wS10T001TRecord.SOSAIKIN              := 0;
        wS10T001TRecord.DATSKSMOTOKBN         := iDatsksmotokbn;
        wS10T001TRecord.SKYSHOOTPTARINASHIKBN := '0'; -- （出力あり）
        wS10T001TRecord.KAISHUTNSHACD         := iKaishutnshacd;
        wS10T001TRecord.TRKSDNPYNO            := NULL;
        InsertS10T001T(wS10T001TRecord);

        -- 入金消込履歴テーブル登録
        wS10T004TRecord.NYKNDNPYNO            := iNyukinImpWorkRecord.DNPYNO;
        wS10T004TRecord.NYKNDNPYNOEDANO       := wNykndnpynoedano + 1;
        wS10T004TRecord.SEQ                   := wSeq + 1;
        wS10T004TRecord.NYKNKSHKMPRCSKBN      := (CASE WHEN iDatsksmotokbn = '07' THEN '10' ELSE '20' END);
        wS10T004TRecord.SKYSAKICD             := iSkysakicd;
        wS10T004TRecord.TOKUISAKICD           := iKairekinList(i).TOKUISAKICD;
        wS10T004TRecord.NYKNDD                := iNyukinImpWorkRecord.NYKNDD;
        wS10T004TRecord.KSHKMDD               := vOperationDate;
        wS10T004TRecord.NYKNKIND              := iNyukinImpWorkRecord.NYKNKIND;
        wS10T004TRecord.KSHKMNYKNKIND         := '30'; -- （過入金）
        wS10T004TRecord.KESHIKOMIKIN          := wSosaikin * -1;
        wS10T004TRecord.SHOKAISKYSHONO        := NULL;
        wS10T004TRecord.TRKSDNPYNO            := NULL;
        wS10T004TRecord.SKYPRCSJISSIKBN       := 0;
        InsertS10T004T(wS10T004TRecord);
      END IF;

      -- 入金取込得意先ワーク登録・更新
      MergeS10W002T(iApkihoncd, iShopcd, iSkysakicd, iKairekinList(i).TOKUISAKICD, wSosaikin * -1);

      -- 入金管理用過入金額設定（過入金残、合計相殺金額）
      wKairekinzan := wKairekinzan - wSosaikin;
      wSumSosaikin := wSumSosaikin + wSosaikin;
    END LOOP;

    -- 合計相殺金額設定
    oSumSosaikin := wSumSosaikin;

  END KanyukinSosaiProcess;

  /*****************************************************************************/
  /* プロシージャ名  ：一括消込処理                                            */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iSkysakicd            請求先コード                      */
  /*                 ：iPlusKairekinSum      プラス過入金合計                  */
  /*                 ：iMinusKairekinSum     マイナス過入金合計                */
  /*                 ：iNyukinImpWorkRecord  入金取込ワークデータ              */
  /*                 ：iKeshikomikanoukin    消込可能額                        */
  /*                 ：iDatsksmotokbn        データ作成元区分                  */
  /*                 ：iKaishutnshacd        回収担当者コード                  */
  /*****************************************************************************/
  PROCEDURE BulkClearingProcess(iApkihoncd            IN S07T001T.APKIHONCD%TYPE
                               ,iShopcd               IN S07T001T.SHOPCD%TYPE
                               ,iSkysakicd            IN S07T001T.SKYSAKICD%TYPE
                               ,iPlusKairekinSum      IN NUMBER
                               ,iMinusKairekinSum     IN NUMBER
                               ,iNyukinImpWorkRecord  IN vTypeNyukinImportWorkListRecord
                               ,iKeshikomikanoukin    IN NUMBER
                               ,iDatsksmotokbn        IN S10T001T.DATSKSMOTOKBN%TYPE
                               ,iKaishutnshacd        IN S10T001T.KAISHUTNSHACD%TYPE) IS
    ---------------------------
    -- カーソル定義
    ---------------------------
    -- 請求残高テーブルカーソル
    CURSOR curS07T001T IS
      SELECT
        *
      FROM
        S07T001T
      WHERE
            APKIHONCD   = iApkihoncd
        AND SHOPCD      = iShopcd
        AND SKYSAKICD   = iSkysakicd
        AND KESHIKOMIZD > 0
        AND YUKOKBN     = '1'
      ORDER BY
          KONKAISHIMEDD
        , TOKUISAKICD
      ;
    ---------------------------
    -- 変数定義
    ---------------------------
    wKeshikomikin       NUMBER := 0;                    -- 消込金額
    wKeshikomizan       NUMBER := 0;                    -- 消込残額
    wNyukinDetailSize   NUMBER := 0;                    -- 入金明細件数
    wNyukinEdaNo        S10T001T.NYKNDNPYNOEDANO%TYPE;  -- 入金枝番号
    wNyukinSeq          S10T004T.SEQ%TYPE;              -- 入金SEQ
    wKairekin           S02M017T.KAIREKIN%TYPE;         -- 過入金額
    wNyknkind           S10T001T.NYKNKIND%TYPE;         -- 入金種別
    wClearingFlg        VARCHAR2(1);                    -- 消込ありフラグ
    wKeshikomikanoukin  NUMBER := iKeshikomikanoukin;   -- 消込可能額
    wKeshikomikinSum    NUMBER;                         -- 消込金額合計
    wExistsNyukinEdaNo  S10T001T.NYKNDNPYNOEDANO%TYPE;  -- 入金枝番号（存在チェック用）
    wNyknkin            S10T001T.NYKNKIN%TYPE;          -- 入金金額（入金明細テーブル更新用）
    wNebikikin          S10T001T.NEBIKIKIN%TYPE;        -- 値引金額（入金明細テーブル更新用）
    wTesuryo            S10T001T.TESURYO%TYPE;          -- 手数料（入金明細テーブル更新用）
    ---------------------------
    -- レコード定義
    ---------------------------
    wKairekinList       vTypeKanyukinList;              -- 過入金リストレコード（配列）
    wS07T001TRecord     curS07T001T%ROWTYPE;            -- 請求残高カーソルレコード
    wS10T001TRecord     S10T001T%ROWTYPE;               -- 入金明細テーブルレコード
    wS10T004TRecord     S10T004T%ROWTYPE;               -- 入金消込履歴テーブルレコード
    wS02M017TRecord     S02M017T%ROWTYPE;               -- 請求先マスタレコード
  BEGIN
    -- 入金種別設定
    wNyknkind := iNyukinImpWorkRecord.NYKNKIND;

    -- プラス過入金合計＞0の場合
    IF iPlusKairekinSum > 0 THEN
      -- プラス過入金リスト取得
      GetPlusKanyukinList(iApkihoncd, iShopcd, iSkysakicd, wKairekinList);
      -- 消込残額設定（過入金額）
      wKeshikomizan := wKairekinList(wKairekinList.FIRST).KAIREKIN;
      -- プラス過入金リストループ
      FOR i IN wKairekinList.FIRST..wKairekinList.LAST LOOP
        -- 請求残高カーソルループ
        FOR wS07T001TRecord IN curS07T001T LOOP
          IF wKeshikomizan = 0 THEN
            EXIT; -- （消込残額＝0の場合ループを抜ける）
          END IF;

          -- 消込金額設定（消込残額≧消込残高：消込残高／その他：消込残額）
          wKeshikomikin := (
            CASE
              WHEN wKeshikomizan >= wS07T001TRecord.KESHIKOMIZD THEN wS07T001TRecord.KESHIKOMIZD
              ELSE wKeshikomizan
            END);

          -- 過入金元更新
          UpdateKanyukinMoto(iApkihoncd, iShopcd, iSkysakicd, wKairekinList(i).DNPYNO,
            wKairekinList(i).NYKNDNPYNOEDANO, wKairekinList(i).NYKNDD, wNyknkind,
            iDatsksmotokbn, iKaishutnshacd, wKeshikomikin, wKairekinList(i).TOKUISAKICD,
            wS07T001TRecord.SKYSHONO, wS07T001TRecord.SKYPRCSJISSIKBN, wS07T001TRecord.TOKUISAKICD);

          -- 請求残高テーブル更新
          UpdateS07T001T(wS07T001TRecord, wS07T001TRecord.KESHIKOMIKIN + wKeshikomikin,
            wS07T001TRecord.KESHIKOMIZD - wKeshikomikin);

          -- 消込残額、消込可能額設定（－消込金額）
          wKeshikomizan := wKeshikomizan - wKeshikomikin;
          wKeshikomikanoukin := wKeshikomikanoukin - wKeshikomikin;
        END LOOP;
        IF wKeshikomizan = 0 THEN
          EXIT; -- （消込残額＝0の場合ループを抜ける）
        END IF;
      END LOOP;
    END IF;

    -- マイナス過入金合計＜0の場合
    IF iMinusKairekinSum < 0 THEN
      -- マイナス過入金リスト取得
      GetMinusKanyukinList(iApkihoncd, iShopcd, iSkysakicd, wKairekinList);
      -- 消込残額設定（過入金額）
      wKeshikomizan := wKairekinList(wKairekinList.FIRST).KAIREKIN;
      -- マイナス過入金リストループ
      FOR i IN wKairekinList.FIRST..wKairekinList.LAST LOOP
        -- 請求残高カーソルループ
        FOR wS07T001TRecord IN curS07T001T LOOP
          IF wKeshikomizan = 0 THEN
            EXIT; -- （消込残額＝0の場合ループを抜ける）
          END IF;

          -- 消込金額設定（絶対値(消込残額)≧消込金額：消込金額×-1／その他：消込残額）
          wKeshikomikin := (
            CASE
              WHEN ABS(wKeshikomizan) >= wS07T001TRecord.KESHIKOMIKIN THEN wS07T001TRecord.KESHIKOMIKIN * -1
              ELSE wKeshikomizan
            END);

          -- 過入金元更新
          UpdateKanyukinMoto(iApkihoncd, iShopcd, iSkysakicd, wKairekinList(i).DNPYNO,
            wKairekinList(i).NYKNDNPYNOEDANO, wKairekinList(i).NYKNDD, wNyknkind,
            iDatsksmotokbn, iKaishutnshacd, wKeshikomikin, wKairekinList(i).TOKUISAKICD,
            wS07T001TRecord.SKYSHONO, wS07T001TRecord.SKYPRCSJISSIKBN, wS07T001TRecord.TOKUISAKICD);

          -- 請求残高テーブル更新
          UpdateS07T001T(wS07T001TRecord, wS07T001TRecord.KESHIKOMIKIN + wKeshikomikin,
            wS07T001TRecord.KESHIKOMIZD - wKeshikomikin);

          -- 消込残額、消込可能額設定（－消込金額）
          wKeshikomizan := wKeshikomizan - wKeshikomikin;
          wKeshikomikanoukin := wKeshikomikanoukin - wKeshikomikin;
        END LOOP;
        IF wKeshikomizan = 0 THEN
          EXIT; -- （消込残額＝0の場合ループを抜ける）
        END IF;
      END LOOP;
    END IF;

    -- 消込可能額＞0の場合
    IF wKeshikomikanoukin > 0 THEN
      -- 消込残額設定（消込可能額）
      wKeshikomizan := wKeshikomikanoukin;
      -- 入金明細件数設定
      wNyukinDetailSize := 1;
      -- SEQ最大値取得
      wNyukinSeq := GetMaxSeq(iApkihoncd, iShopcd, iNyukinImpWorkRecord.DNPYNO);
      -- 請求残高カーソルループ
      FOR wS07T001TRecord IN curS07T001T LOOP
        IF wKeshikomizan = 0 THEN
          EXIT; -- （消込残額＝0の場合ループを抜ける）
        END IF;

        -- 消込金額設定（消込残額≧消込残高：消込残高／その他：消込残額）
        wKeshikomikin := (
          CASE
            WHEN wKeshikomizan >= wS07T001TRecord.KESHIKOMIZD THEN wS07T001TRecord.KESHIKOMIZD
            ELSE wKeshikomizan
          END);

        -- 請求残高テーブル更新
        UpdateS07T001T(wS07T001TRecord, wS07T001TRecord.KESHIKOMIKIN + wKeshikomikin,
          wS07T001TRecord.KESHIKOMIZD - wKeshikomikin);

        -- 同じ得意先、入金種別の明細有無の判定
        SELECT
          MAX(CASE WHEN TOKUISAKICD = wS07T001TRecord.TOKUISAKICD THEN NYKNDNPYNOEDANO ELSE -1 END)
        INTO
          wExistsNyukinEdaNo
        FROM
          S10T001T
        WHERE
              APKIHONCD   = iApkihoncd
          AND SHOPCD      = iShopcd
          AND DNPYNO      = iNyukinImpWorkRecord.DNPYNO
          AND NYKNKIND    = iNyukinImpWorkRecord.NYKNKIND
          AND KAIREKINKBN = '0' -- （入金）
        ;

        -- 条件を満たす入金明細テーブルのレコードがあれば更新、それ以外は新規登録
        IF wExistsNyukinEdaNo IS NULL OR wExistsNyukinEdaNo = -1 THEN
          -- 入金伝票番号枝番号最大値取得
          wNyukinEdaNo := GetMaxNykndnpynoedano(iApkihoncd, iShopcd, iNyukinImpWorkRecord.DNPYNO);
          -- 入金明細テーブル登録
          wS10T001TRecord.APKIHONCD             := iApkihoncd;
          wS10T001TRecord.SHOPCD                := iShopcd;
          wS10T001TRecord.DNPYNO                := iNyukinImpWorkRecord.DNPYNO;
          wS10T001TRecord.NYKNDNPYNOEDANO       := wNyukinEdaNo + 1;
          wS10T001TRecord.KAIREKINKBN           := '0'; -- （入金）
          wS10T001TRecord.SKYSAKICD             := iSkysakicd;
          wS10T001TRecord.TOKUISAKICD           := wS07T001TRecord.TOKUISAKICD;
          wS10T001TRecord.NYKNDD                := iNyukinImpWorkRecord.NYKNDD;
          wS10T001TRecord.NYKNKIND              := wNyknkind;
          wS10T001TRecord.NYKNKIN               := GetNyknkin(wNyknkind, wKeshikomikin);
          wS10T001TRecord.NEBIKIKIN             := GetNebikikin(wNyknkind, wKeshikomikin);
          wS10T001TRecord.TESURYO               := GetTesuryo(wNyknkind, wKeshikomikin);
          wS10T001TRecord.BIKO                  := iNyukinImpWorkRecord.BIKO;
          wS10T001TRecord.KESHIKOMIKIN          := wKeshikomikin;
          wS10T001TRecord.SOSAIKIN              := 0;
          wS10T001TRecord.DATSKSMOTOKBN         := iDatsksmotokbn;
          wS10T001TRecord.SKYSHOOTPTARINASHIKBN := '0'; -- （出力あり）
          wS10T001TRecord.KAISHUTNSHACD         := iKaishutnshacd;
          wS10T001TRecord.TRKSDNPYNO            := NULL;
          InsertS10T001T(wS10T001TRecord);
        ELSE
          -- 金額項目設定（入金金額、値引金額、手数料）
          wNyknkin := GetNyknkin(iNyukinImpWorkRecord.NYKNKIND, wKeshikomikin);
          wNebikikin := GetNebikikin(iNyukinImpWorkRecord.NYKNKIND, wKeshikomikin);
          wTesuryo := GetTesuryo(iNyukinImpWorkRecord.NYKNKIND, wKeshikomikin);
          -- 入金明細テーブル更新（入金金額、値引金額、手数料、消込金額）
          UPDATE S10T001T
          SET
              NYKNKIN           = NYKNKIN + wNyknkin
            , NEBIKIKIN         = NEBIKIKIN + wNebikikin
            , TESURYO           = TESURYO + wTesuryo
            , KESHIKOMIKIN      = KESHIKOMIKIN + wKeshikomikin
            , KOSHAAPKIHONCD    = vUserApkihoncd
            , KOSHASHOPCD       = vUserShopcd
            , KOSHACD           = vTantoshacd
            , KSHNSYSTMYMD      = vSystemDate
            , KSHNSYSTMJIKAN    = vSystemTime
            , KSHNUNYOYMD       = vOperationDate
            , KSHNIPADRSE       = vIpAddress
            , KSHNPGMID         = vProgramId
          WHERE
                APKIHONCD       = iApkihoncd
            AND SHOPCD          = iShopcd
            AND DNPYNO          = iNyukinImpWorkRecord.DNPYNO
            AND NYKNDNPYNOEDANO = wExistsNyukinEdaNo
            AND NYKNKIND        = iNyukinImpWorkRecord.NYKNKIND
          ;
          -- 入金消込履歴テーブル登録時に必要な入金伝票枝番号を変数に設定
          -- ※消込履歴登録時に+1するのでここで引いておく
          wNyukinEdaNo := wExistsNyukinEdaNo - 1;
        END IF;

        -- 入金消込履歴テーブル登録
        wS10T004TRecord.NYKNDNPYNO            := iNyukinImpWorkRecord.DNPYNO;
        wS10T004TRecord.NYKNDNPYNOEDANO       := wNyukinEdaNo + 1;
        wS10T004TRecord.SEQ                   := wNyukinSeq + wNyukinDetailSize;
        wS10T004TRecord.NYKNKSHKMPRCSKBN      := '20'; -- （自動消込）
        wS10T004TRecord.SKYSAKICD             := iSkysakicd;
        wS10T004TRecord.TOKUISAKICD           := wS07T001TRecord.TOKUISAKICD;
        wS10T004TRecord.NYKNDD                := iNyukinImpWorkRecord.NYKNDD;
        wS10T004TRecord.KSHKMDD               := vOperationDate;
        wS10T004TRecord.NYKNKIND              := wNyknkind;
        wS10T004TRecord.KSHKMNYKNKIND         := wNyknkind;
        wS10T004TRecord.KESHIKOMIKIN          := wKeshikomikin;
        wS10T004TRecord.SHOKAISKYSHONO        := wS07T001TRecord.SKYSHONO;
        wS10T004TRecord.TRKSDNPYNO            := NULL;
        wS10T004TRecord.SKYPRCSJISSIKBN       := wS07T001TRecord.SKYPRCSJISSIKBN;
        InsertS10T004T(wS10T004TRecord);

        -- 入金取込得意先ワーク登録・更新
        MergeS10W002T(iApkihoncd, iShopcd, iSkysakicd, wS07T001TRecord.TOKUISAKICD, wKeshikomikin);

        -- 消込残額設定（消込残額－消込金額）
        wKeshikomizan := wKeshikomizan - wKeshikomikin;
        -- 入金明細件数（登録した消込履歴の件数）設定
        wNyukinDetailSize := wNyukinDetailSize + 1;
      END LOOP;

    -- 消込可能額＜0の場合
    ELSIF wKeshikomikanoukin < 0 THEN
      -- 消込残額設定（消込可能額）
      wKeshikomizan := wKeshikomikanoukin;
      -- 入金明細件数設定
      wNyukinDetailSize := 1;
      -- SEQ最大値取得
      wNyukinSeq := GetMaxSeq(iApkihoncd, iShopcd, iNyukinImpWorkRecord.DNPYNO);
      -- 請求残高カーソルループ
      FOR wS07T001TRecord IN curS07T001T LOOP
        IF wKeshikomizan = 0 THEN
          EXIT; -- （消込残額＝0の場合ループを抜ける）
        END IF;

        -- 消込金額設定（消込残額（絶対値）≧消込金額：消込金額×-1／その他：消込残額）
        wKeshikomikin := (
          CASE
            WHEN ABS(wKeshikomizan) >= wS07T001TRecord.KESHIKOMIKIN THEN wS07T001TRecord.KESHIKOMIKIN * -1
            ELSE wKeshikomizan
          END);

        -- 請求残高テーブル更新
        UpdateS07T001T(wS07T001TRecord, wS07T001TRecord.KESHIKOMIKIN + wKeshikomikin,
          wS07T001TRecord.KESHIKOMIZD - wKeshikomikin);

        -- TODO この辺で分岐して更新するか登録するかみたいな

        -- 同じ得意先、入金種別の明細有無の判定
        SELECT
          MAX(CASE WHEN TOKUISAKICD = wS07T001TRecord.TOKUISAKICD THEN NYKNDNPYNOEDANO ELSE -1 END)
        INTO
          wExistsNyukinEdaNo
        FROM
          S10T001T
        WHERE
              APKIHONCD   = iApkihoncd
          AND SHOPCD      = iShopcd
          AND DNPYNO      = iNyukinImpWorkRecord.DNPYNO
          AND NYKNKIND    = iNyukinImpWorkRecord.NYKNKIND
          AND KAIREKINKBN = '0' -- （入金）
        ;
        
        -- 条件を満たす入金明細テーブルのレコードがあれば更新、それ以外は新規登録
        IF wExistsNyukinEdaNo IS NULL OR wExistsNyukinEdaNo = -1 THEN
          -- 入金伝票番号枝番号最大値取得
          wNyukinEdaNo := GetMaxNykndnpynoedano(iApkihoncd, iShopcd, iNyukinImpWorkRecord.DNPYNO);
          -- 入金明細テーブル登録
          wS10T001TRecord.APKIHONCD             := iApkihoncd;
          wS10T001TRecord.SHOPCD                := iShopcd;
          wS10T001TRecord.DNPYNO                := iNyukinImpWorkRecord.DNPYNO;
          wS10T001TRecord.NYKNDNPYNOEDANO       := wNyukinEdaNo + 1;
          wS10T001TRecord.KAIREKINKBN           := '0'; -- （入金）
          wS10T001TRecord.SKYSAKICD             := iSkysakicd;
          wS10T001TRecord.TOKUISAKICD           := wS07T001TRecord.TOKUISAKICD;
          wS10T001TRecord.NYKNDD                := iNyukinImpWorkRecord.NYKNDD;
          wS10T001TRecord.NYKNKIND              := wNyknkind;
          wS10T001TRecord.NYKNKIN               := GetNyknkin(wNyknkind, wKeshikomikin);
          wS10T001TRecord.NEBIKIKIN             := GetNebikikin(wNyknkind, wKeshikomikin);
          wS10T001TRecord.TESURYO               := GetTesuryo(wNyknkind, wKeshikomikin);
          wS10T001TRecord.BIKO                  := iNyukinImpWorkRecord.BIKO;
          wS10T001TRecord.KESHIKOMIKIN          := wKeshikomikin;
          wS10T001TRecord.SOSAIKIN              := 0;
          wS10T001TRecord.DATSKSMOTOKBN         := iDatsksmotokbn;
          wS10T001TRecord.SKYSHOOTPTARINASHIKBN := '0'; -- （出力あり）
          wS10T001TRecord.KAISHUTNSHACD         := iKaishutnshacd;
          wS10T001TRecord.TRKSDNPYNO            := NULL;
          InsertS10T001T(wS10T001TRecord);
        ELSE
          -- 金額項目設定（入金金額、値引金額、手数料）
          wNyknkin := GetNyknkin(iNyukinImpWorkRecord.NYKNKIND, wKeshikomikin);
          wNebikikin := GetNebikikin(iNyukinImpWorkRecord.NYKNKIND, wKeshikomikin);
          wTesuryo := GetTesuryo(iNyukinImpWorkRecord.NYKNKIND, wKeshikomikin);
          -- 入金明細テーブル更新（入金金額、値引金額、手数料、消込金額）
          UPDATE S10T001T
          SET
              NYKNKIN           = NYKNKIN + wNyknkin
            , NEBIKIKIN         = NEBIKIKIN + wNebikikin
            , TESURYO           = TESURYO + wTesuryo
            , KESHIKOMIKIN      = KESHIKOMIKIN + wKeshikomikin
            , KOSHAAPKIHONCD    = vUserApkihoncd
            , KOSHASHOPCD       = vUserShopcd
            , KOSHACD           = vTantoshacd
            , KSHNSYSTMYMD      = vSystemDate
            , KSHNSYSTMJIKAN    = vSystemTime
            , KSHNUNYOYMD       = vOperationDate
            , KSHNIPADRSE       = vIpAddress
            , KSHNPGMID         = vProgramId
          WHERE
                APKIHONCD       = iApkihoncd
            AND SHOPCD          = iShopcd
            AND DNPYNO          = iNyukinImpWorkRecord.DNPYNO
            AND NYKNDNPYNOEDANO = wExistsNyukinEdaNo
            AND NYKNKIND        = iNyukinImpWorkRecord.NYKNKIND
          ;
          -- 入金消込履歴テーブル登録時に必要な入金伝票枝番号を変数に設定
          -- ※消込履歴登録時に+1するのでここで引いておく
          wNyukinEdaNo := wExistsNyukinEdaNo - 1;
        END IF;

        -- 入金消込履歴テーブル登録
        wS10T004TRecord.NYKNDNPYNO            := iNyukinImpWorkRecord.DNPYNO;
        wS10T004TRecord.NYKNDNPYNOEDANO       := wNyukinEdaNo + 1;
        wS10T004TRecord.SEQ                   := wNyukinSeq + wNyukinDetailSize;
        wS10T004TRecord.NYKNKSHKMPRCSKBN      := '20'; -- （自動消込）
        wS10T004TRecord.SKYSAKICD             := iSkysakicd;
        wS10T004TRecord.TOKUISAKICD           := wS07T001TRecord.TOKUISAKICD;
        wS10T004TRecord.NYKNDD                := iNyukinImpWorkRecord.NYKNDD;
        wS10T004TRecord.KSHKMDD               := vOperationDate;
        wS10T004TRecord.NYKNKIND              := wNyknkind;
        wS10T004TRecord.KSHKMNYKNKIND         := wNyknkind;
        wS10T004TRecord.KESHIKOMIKIN          := wKeshikomikin;
        wS10T004TRecord.SHOKAISKYSHONO        := wS07T001TRecord.SKYSHONO;
        wS10T004TRecord.TRKSDNPYNO            := NULL;
        wS10T004TRecord.SKYPRCSJISSIKBN       := wS07T001TRecord.SKYPRCSJISSIKBN;
        InsertS10T004T(wS10T004TRecord);

        -- 入金取込得意先ワーク登録・更新
        MergeS10W002T(iApkihoncd, iShopcd, iSkysakicd, wS07T001TRecord.TOKUISAKICD, wKeshikomikin);

        -- 消込残額設定（消込残額－消込金額）
        wKeshikomizan := wKeshikomizan - wKeshikomikin;
        -- 入金明細件数加算
        wNyukinDetailSize := wNyukinDetailSize + 1;
      END LOOP;
    END IF;

    -- 消込金額合計取得
    wKeshikomikinSum := GetKeshikomikinSum(iApkihoncd, iShopcd, iNyukinImpWorkRecord.DNPYNO);

    -- 今回入金金額－消込金額合計≠0の場合
    IF iNyukinImpWorkRecord.KONKAINYKNKIN - wKeshikomikinSum != 0 THEN
      -- 消込ありフラグ設定（入金明細件数＝1："0"／その他："0"）
      wClearingFlg := (CASE WHEN wNyukinDetailSize = 1 THEN '0' ELSE '1' END);
      -- 過入金反映更新
      UpdateKanyukinReflection(iApkihoncd, iShopcd, iSkysakicd, iNyukinImpWorkRecord,
        iNyukinImpWorkRecord.KONKAINYKNKIN - wKeshikomikinSum,
        wClearingFlg, iDatsksmotokbn, iNyukinImpWorkRecord.KAISHUTNSHACD);
    END IF;

    -- 請求先マスタ（過入金額）取得
    wS02M017TRecord := GetS02M017TRecord(iApkihoncd, iShopcd, iSkysakicd);

    -- 消込残額≠請求先マスタ.過入金額の場合
    IF wKeshikomizan != wS02M017TRecord.KAIREKIN THEN
      -- 過入金額設定（消込残額）
      wKairekin := wKeshikomizan;
      -- 請求先マスタ更新（過入金額）
      UpdateS02M017TForKairekin(iApkihoncd, iShopcd, iSkysakicd, wKairekin);
    END IF;
  END BulkClearingProcess;

  /*****************************************************************************/
  /* プロシージャ名  ：消込済入金額登録処理                                    */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iSkysakicd            請求先コード                      */
  /*                 ：iDatsksmotokbn        データ作成元区分                  */
  /*                 ：iKaishutnshacd        回収担当者コード                  */
  /*                 ：iApplyAmount          消込金額                          */
  /*                 ：iS07T001TRecord       請求残高テーブルデータ            */
  /*                 ：iNyukinImpWorkRecord  入金取込ワークデータ              */
  /*                 ：oUnappliedReceipt     未消し入金金額                    */
  /*                 ：oUnappliedDiscount    未消し値引金額                    */
  /*                 ：oUnappliedFee         未消し手数料                      */
  /*****************************************************************************/
  PROCEDURE RegistAppliedReceiptAmount(iApkihoncd             IN  S10T001T.APKIHONCD%TYPE
                                      ,iShopcd                IN  S10T001T.SHOPCD%TYPE
                                      ,iSkysakicd             IN  S10T001T.SKYSAKICD%TYPE
                                      ,iDatsksmotokbn         IN  S10T001T.DATSKSMOTOKBN%TYPE
                                      ,iKaishutnshacd         IN  S10T001T.KAISHUTNSHACD%TYPE
                                      ,iApplyAmount           IN  NUMBER
                                      ,iS07T001TRecord        IN  S07T001T%ROWTYPE
                                      ,iNyukinImpWorkRecord   IN  vTypeNyukinImportWorkListRecord
                                      ,oUnappliedReceipt      OUT NUMBER
                                      ,oUnappliedDiscount     OUT NUMBER
                                      ,oUnappliedFee          OUT NUMBER) IS
    ---------------------------
    -- 変数定義
    ---------------------------
    wAppliedAmount      NUMBER := 0;                                -- 消込済金額
    wApplyAmount        NUMBER := iApplyAmount;                     -- 消込金額
    wUnappliedReceipt   NUMBER := iNyukinImpWorkRecord.NYKNKIN;     -- 未消し入金金額
    wUnappliedDiscount  NUMBER := iNyukinImpWorkRecord.NEBIKIKIN;   -- 未消し値引金額
    wUnappliedFee       NUMBER := iNyukinImpWorkRecord.TESURYO;     -- 未消し手数料
    wNyukinEdaNo        S10T001T.NYKNDNPYNOEDANO%TYPE;              -- 入金枝番号（新規登録用）
    wNyukinSeq          S10T004T.SEQ%TYPE;                          -- 入金SEQ
    wExistsNyukinEdaNo  S10T001T.NYKNDNPYNOEDANO%TYPE;              -- 入金枝番号（存在チェック用）
    wNyknkin            S10T001T.NYKNKIN%TYPE;                      -- 入金金額
    wNebikikin          S10T001T.NEBIKIKIN%TYPE;                    -- 値引金額
    wTesuryo            S10T001T.TESURYO%TYPE;                      -- 手数料

    ---------------------------
    -- レコード定義
    ---------------------------
    wS10T001TRecord     S10T001T%ROWTYPE;                           -- 入金明細テーブルレコード
    wS10T004TRecord     S10T004T%ROWTYPE;                           -- 入金消込履歴テーブルレコード
  BEGIN
    ---------------------------
    -- 処理定義
    ---------------------------

    -- 入金明細テーブル、入金消込履歴テーブル登録（入金金額）
    IF iNyukinImpWorkRecord.NYKNKIN <> 0 AND wApplyAmount <> 0 THEN
      -- 消込済金額設定
      wAppliedAmount := (
        CASE
        WHEN ABS(wApplyAmount) >= ABS(iNyukinImpWorkRecord.NYKNKIN) THEN iNyukinImpWorkRecord.NYKNKIN
        ELSE wApplyAmount
      END);

      -- 同じ得意先、入金種別の明細有無の判定
      SELECT
        MAX(CASE WHEN TOKUISAKICD = iS07T001TRecord.TOKUISAKICD THEN NYKNDNPYNOEDANO ELSE -1 END)
      INTO
        wExistsNyukinEdaNo
      FROM
        S10T001T
      WHERE
            APKIHONCD   = iApkihoncd
        AND SHOPCD      = iShopcd
        AND DNPYNO      = iNyukinImpWorkRecord.DNPYNO
        AND NYKNKIND    = iNyukinImpWorkRecord.NYKNKIND
        AND KAIREKINKBN = '0' -- （入金）
      ;
      -- 条件を満たす入金明細テーブルのレコードがあれば更新、それ以外は新規登録
      -- ※wExistsNyukinEdaNoがNULL→同じ入金種別のデータがない、
      --   wExistsNyukinEdaNoが-1→データはあるが得意先が異なる
      IF wExistsNyukinEdaNo IS NULL OR wExistsNyukinEdaNo = -1 THEN
        -- 入金伝票番号枝番号最大値を取得
        wNyukinEdaNo := GetMaxNykndnpynoedano(iApkihoncd, iShopcd, iNyukinImpWorkRecord.DNPYNO);
        -- 入金明細テーブル登録
        wS10T001TRecord.APKIHONCD             := iApkihoncd;
        wS10T001TRecord.SHOPCD                := iShopcd;
        wS10T001TRecord.DNPYNO                := iNyukinImpWorkRecord.DNPYNO;
        wS10T001TRecord.NYKNDNPYNOEDANO       := wNyukinEdaNo + 1;
        wS10T001TRecord.KAIREKINKBN           := '0'; -- （入金）
        wS10T001TRecord.SKYSAKICD             := iSkysakicd;
        wS10T001TRecord.TOKUISAKICD           := iS07T001TRecord.TOKUISAKICD;
        wS10T001TRecord.NYKNDD                := iNyukinImpWorkRecord.NYKNDD;
        wS10T001TRecord.NYKNKIND              := iNyukinImpWorkRecord.NYKNKIND;
        wS10T001TRecord.NYKNKIN               := GetNyknkin(iNyukinImpWorkRecord.NYKNKIND, wAppliedAmount);
        wS10T001TRecord.NEBIKIKIN             := GetNebikikin(iNyukinImpWorkRecord.NYKNKIND, wAppliedAmount);
        wS10T001TRecord.TESURYO               := GetTesuryo(iNyukinImpWorkRecord.NYKNKIND, wAppliedAmount);
        wS10T001TRecord.BIKO                  := iNyukinImpWorkRecord.BIKO;
        wS10T001TRecord.KESHIKOMIKIN          := wAppliedAmount;
        wS10T001TRecord.SOSAIKIN              := 0;
        wS10T001TRecord.DATSKSMOTOKBN         := iDatsksmotokbn;
        wS10T001TRecord.SKYSHOOTPTARINASHIKBN := '0'; -- （出力あり）
        wS10T001TRecord.KAISHUTNSHACD         := iKaishutnshacd;
        wS10T001TRecord.TRKSDNPYNO            := NULL;
        InsertS10T001T(wS10T001TRecord);
      ELSE
        -- 金額項目設定（入金金額、値引金額、手数料）
        wNyknkin := GetNyknkin(iNyukinImpWorkRecord.NYKNKIND, wAppliedAmount);
        wNebikikin := GetNebikikin(iNyukinImpWorkRecord.NYKNKIND, wAppliedAmount);
        wTesuryo := GetTesuryo(iNyukinImpWorkRecord.NYKNKIND, wAppliedAmount);
        -- 入金明細テーブル更新（入金金額、値引金額、手数料、消込金額）
        UPDATE S10T001T
        SET
            NYKNKIN           = NYKNKIN + wNyknkin
          , NEBIKIKIN         = NEBIKIKIN + wNebikikin
          , TESURYO           = TESURYO + wTesuryo
          , KESHIKOMIKIN      = KESHIKOMIKIN + wAppliedAmount
          , KOSHAAPKIHONCD    = vUserApkihoncd
          , KOSHASHOPCD       = vUserShopcd
          , KOSHACD           = vTantoshacd
          , KSHNSYSTMYMD      = vSystemDate
          , KSHNSYSTMJIKAN    = vSystemTime
          , KSHNUNYOYMD       = vOperationDate
          , KSHNIPADRSE       = vIpAddress
          , KSHNPGMID         = vProgramId
        WHERE
              APKIHONCD       = iApkihoncd
          AND SHOPCD          = iShopcd
          AND DNPYNO          = iNyukinImpWorkRecord.DNPYNO
          AND NYKNDNPYNOEDANO = wExistsNyukinEdaNo
          AND NYKNKIND        = iNyukinImpWorkRecord.NYKNKIND
          ;
        -- 入金消込履歴テーブル登録時に必要な入金伝票枝番号を変数に設定
        -- ※登録時にインクリメントするので、デクリメントしておく
        wNyukinEdaNo := wExistsNyukinEdaNo - 1;
      END IF;

      -- SEQ最大値取得
      wNyukinSeq := GetMaxSeq(iApkihoncd, iShopcd, iNyukinImpWorkRecord.DNPYNO);
      -- 入金消込履歴テーブル登録
      wS10T004TRecord.NYKNDNPYNO            := iNyukinImpWorkRecord.DNPYNO;
      wS10T004TRecord.NYKNDNPYNOEDANO       := wNyukinEdaNo + 1;
      wS10T004TRecord.SEQ                   := wNyukinSeq + 1;
      wS10T004TRecord.NYKNKSHKMPRCSKBN      := '10'; -- （入金入力）
      wS10T004TRecord.SKYSAKICD             := iSkysakicd;
      wS10T004TRecord.TOKUISAKICD           := iS07T001TRecord.TOKUISAKICD;
      wS10T004TRecord.NYKNDD                := iNyukinImpWorkRecord.NYKNDD;
      wS10T004TRecord.KSHKMDD               := vOperationDate;
      wS10T004TRecord.NYKNKIND              := iNyukinImpWorkRecord.NYKNKIND;
      wS10T004TRecord.KSHKMNYKNKIND         := iNyukinImpWorkRecord.NYKNKIND;
      wS10T004TRecord.KESHIKOMIKIN          := wAppliedAmount;
      wS10T004TRecord.SHOKAISKYSHONO        := iS07T001TRecord.SKYSHONO;
      wS10T004TRecord.TRKSDNPYNO            := NULL;
      wS10T004TRecord.SKYPRCSJISSIKBN       := iS07T001TRecord.SKYPRCSJISSIKBN;
      InsertS10T004T(wS10T004TRecord);

      -- 未消し入金金額、消込金額から消込済金額を差し引く
      wUnappliedReceipt := wUnappliedReceipt - wAppliedAmount;
      wApplyAmount := wApplyAmount - wAppliedAmount;
    END IF;

    -- 入金明細テーブル、入金消込履歴テーブル登録（値引金額）
    IF iNyukinImpWorkRecord.NEBIKIKIN <> 0 AND wApplyAmount <> 0 THEN
      -- 消込済金額設定
      wAppliedAmount := (
        CASE
        WHEN ABS(wApplyAmount) >= ABS(iNyukinImpWorkRecord.NEBIKIKIN) THEN iNyukinImpWorkRecord.NEBIKIKIN
        ELSE wApplyAmount
      END);

      -- 同じ得意先、入金種別の明細有無の判定
      SELECT
        MAX(CASE WHEN TOKUISAKICD = iS07T001TRecord.TOKUISAKICD THEN NYKNDNPYNOEDANO ELSE -1 END)
      INTO
        wExistsNyukinEdaNo
      FROM
        S10T001T
      WHERE
            APKIHONCD   = iApkihoncd
        AND SHOPCD      = iShopcd
        AND DNPYNO      = iNyukinImpWorkRecord.DNPYNO
        AND NYKNKIND    = '21' -- (値引金額）
        AND KAIREKINKBN = '0' -- （入金）
      ;
      -- 条件を満たす入金明細テーブルのレコードがあれば更新、それ以外は新規登録
      IF wExistsNyukinEdaNo IS NULL OR wExistsNyukinEdaNo = -1 THEN
        -- 入金伝票番号枝番号最大値を取得
        wNyukinEdaNo := GetMaxNykndnpynoedano(iApkihoncd, iShopcd, iNyukinImpWorkRecord.DNPYNO);
        -- 入金明細テーブル登録
        wS10T001TRecord.APKIHONCD             := iApkihoncd;
        wS10T001TRecord.SHOPCD                := iShopcd;
        wS10T001TRecord.DNPYNO                := iNyukinImpWorkRecord.DNPYNO;
        wS10T001TRecord.NYKNDNPYNOEDANO       := wNyukinEdaNo + 1;
        wS10T001TRecord.KAIREKINKBN           := '0'; -- （入金）
        wS10T001TRecord.SKYSAKICD             := iSkysakicd;
        wS10T001TRecord.TOKUISAKICD           := iS07T001TRecord.TOKUISAKICD;
        wS10T001TRecord.NYKNDD                := iNyukinImpWorkRecord.NYKNDD;
        wS10T001TRecord.NYKNKIND              := '21'; -- (値引金額）
        wS10T001TRecord.NYKNKIN               := 0;
        wS10T001TRecord.NEBIKIKIN             := wAppliedAmount;
        wS10T001TRecord.TESURYO               := 0;
        wS10T001TRecord.BIKO                  := iNyukinImpWorkRecord.BIKO;
        wS10T001TRecord.KESHIKOMIKIN          := wAppliedAmount;
        wS10T001TRecord.SOSAIKIN              := 0;
        wS10T001TRecord.DATSKSMOTOKBN         := iDatsksmotokbn;
        wS10T001TRecord.SKYSHOOTPTARINASHIKBN := '0'; -- （出力あり）
        wS10T001TRecord.KAISHUTNSHACD         := iKaishutnshacd;
        wS10T001TRecord.TRKSDNPYNO            := NULL;
        InsertS10T001T(wS10T001TRecord);
      ELSE
        -- 金額項目設定（値引金額以外は0円になる）
        wNyknkin := GetNyknkin('21', wAppliedAmount);
        wNebikikin := GetNebikikin('21', wAppliedAmount);
        wTesuryo := GetTesuryo('21', wAppliedAmount);
        -- 入金明細テーブル更新（入金金額、値引金額、手数料、消込金額）
        UPDATE S10T001T
        SET
            NYKNKIN           = NYKNKIN + wNyknkin
          , NEBIKIKIN         = NEBIKIKIN + wNebikikin
          , TESURYO           = TESURYO + wTesuryo
          , KESHIKOMIKIN      = KESHIKOMIKIN + wAppliedAmount
          , KOSHAAPKIHONCD    = vUserApkihoncd
          , KOSHASHOPCD       = vUserShopcd
          , KOSHACD           = vTantoshacd
          , KSHNSYSTMYMD      = vSystemDate
          , KSHNSYSTMJIKAN    = vSystemTime
          , KSHNUNYOYMD       = vOperationDate
          , KSHNIPADRSE       = vIpAddress
          , KSHNPGMID         = vProgramId
        WHERE
              APKIHONCD       = iApkihoncd
          AND SHOPCD          = iShopcd
          AND DNPYNO          = iNyukinImpWorkRecord.DNPYNO
          AND NYKNDNPYNOEDANO = wExistsNyukinEdaNo
          AND NYKNKIND        = '21' -- (値引金額）
          ;
        -- 入金消込履歴テーブル登録時に必要な入金伝票枝番号を変数に設定
        -- ※登録時にインクリメントするので、デクリメントしておく
        wNyukinEdaNo := wExistsNyukinEdaNo - 1;
      END IF;

      -- SEQ最大値取得
      wNyukinSeq := GetMaxSeq(iApkihoncd, iShopcd, iNyukinImpWorkRecord.DNPYNO);
      -- 入金消込履歴テーブル登録
      wS10T004TRecord.NYKNDNPYNO            := iNyukinImpWorkRecord.DNPYNO;
      wS10T004TRecord.NYKNDNPYNOEDANO       := wNyukinEdaNo + 1;
      wS10T004TRecord.SEQ                   := wNyukinSeq + 1;
      wS10T004TRecord.NYKNKSHKMPRCSKBN      := '10'; -- （入金入力）
      wS10T004TRecord.SKYSAKICD             := iSkysakicd;
      wS10T004TRecord.TOKUISAKICD           := iS07T001TRecord.TOKUISAKICD;
      wS10T004TRecord.NYKNDD                := iNyukinImpWorkRecord.NYKNDD;
      wS10T004TRecord.KSHKMDD               := vOperationDate;
      wS10T004TRecord.NYKNKIND              := '21'; -- (値引金額）
      wS10T004TRecord.KSHKMNYKNKIND         := '21'; -- (値引金額）
      wS10T004TRecord.KESHIKOMIKIN          := wAppliedAmount;
      wS10T004TRecord.SHOKAISKYSHONO        := iS07T001TRecord.SKYSHONO;
      wS10T004TRecord.TRKSDNPYNO            := NULL;
      wS10T004TRecord.SKYPRCSJISSIKBN       := iS07T001TRecord.SKYPRCSJISSIKBN;
      InsertS10T004T(wS10T004TRecord);
      -- 未消し値引金額、消込金額から消込済金額を差し引く
      wUnappliedDiscount := wUnappliedDiscount - wAppliedAmount;
      wApplyAmount := wApplyAmount - wAppliedAmount;
    END IF;

    -- 入金明細テーブル、入金消込履歴テーブル登録（手数料）
    IF iNyukinImpWorkRecord.TESURYO <> 0 AND wApplyAmount <> 0 THEN
      -- 消込済金額設定
      wAppliedAmount := (
        CASE
        WHEN ABS(wApplyAmount) >= ABS(iNyukinImpWorkRecord.TESURYO) THEN iNyukinImpWorkRecord.TESURYO
        ELSE wApplyAmount
      END);
      -- 同じ得意先、入金種別の明細有無の判定
      SELECT
        MAX(CASE WHEN TOKUISAKICD = iS07T001TRecord.TOKUISAKICD THEN NYKNDNPYNOEDANO ELSE -1 END)
      INTO
        wExistsNyukinEdaNo
      FROM
        S10T001T
      WHERE
            APKIHONCD   = iApkihoncd
        AND SHOPCD      = iShopcd
        AND DNPYNO      = iNyukinImpWorkRecord.DNPYNO
        AND NYKNKIND    = '20' -- (手数料）
        AND KAIREKINKBN = '0' -- （入金）
      ;
      -- 条件を満たす入金明細テーブルのレコードがあれば更新、それ以外は新規登録
      IF wExistsNyukinEdaNo IS NULL OR wExistsNyukinEdaNo = -1 THEN
        -- 入金伝票番号枝番号最大値を取得
        wNyukinEdaNo := GetMaxNykndnpynoedano(iApkihoncd, iShopcd, iNyukinImpWorkRecord.DNPYNO);
        -- 入金明細テーブル登録
        wS10T001TRecord.APKIHONCD             := iApkihoncd;
        wS10T001TRecord.SHOPCD                := iShopcd;
        wS10T001TRecord.DNPYNO                := iNyukinImpWorkRecord.DNPYNO;
        wS10T001TRecord.NYKNDNPYNOEDANO       := wNyukinEdaNo + 1;
        wS10T001TRecord.KAIREKINKBN           := '0'; -- （入金）
        wS10T001TRecord.SKYSAKICD             := iSkysakicd;
        wS10T001TRecord.TOKUISAKICD           := iS07T001TRecord.TOKUISAKICD;
        wS10T001TRecord.NYKNDD                := iNyukinImpWorkRecord.NYKNDD;
        wS10T001TRecord.NYKNKIND              := '20'; -- (手数料）
        wS10T001TRecord.NYKNKIN               := 0;
        wS10T001TRecord.NEBIKIKIN             := 0;
        wS10T001TRecord.TESURYO               := wAppliedAmount;
        wS10T001TRecord.BIKO                  := iNyukinImpWorkRecord.BIKO;
        wS10T001TRecord.KESHIKOMIKIN          := wAppliedAmount;
        wS10T001TRecord.SOSAIKIN              := 0;
        wS10T001TRecord.DATSKSMOTOKBN         := iDatsksmotokbn;
        wS10T001TRecord.SKYSHOOTPTARINASHIKBN := '0'; -- （出力あり）
        wS10T001TRecord.KAISHUTNSHACD         := iKaishutnshacd;
        wS10T001TRecord.TRKSDNPYNO            := NULL;
        InsertS10T001T(wS10T001TRecord);
      ELSE
        -- 金額項目設定（手数料以外は0円になる）
        wNyknkin := GetNyknkin('20', wAppliedAmount);
        wNebikikin := GetNebikikin('20', wAppliedAmount);
        wTesuryo := GetTesuryo('20', wAppliedAmount);
        -- 入金明細テーブル更新（入金金額、値引金額、手数料、消込金額）
        UPDATE S10T001T
        SET
            NYKNKIN           = NYKNKIN + wNyknkin
          , NEBIKIKIN         = NEBIKIKIN + wNebikikin
          , TESURYO           = TESURYO + wTesuryo
          , KESHIKOMIKIN      = KESHIKOMIKIN + wAppliedAmount
          , KOSHAAPKIHONCD    = vUserApkihoncd
          , KOSHASHOPCD       = vUserShopcd
          , KOSHACD           = vTantoshacd
          , KSHNSYSTMYMD      = vSystemDate
          , KSHNSYSTMJIKAN    = vSystemTime
          , KSHNUNYOYMD       = vOperationDate
          , KSHNIPADRSE       = vIpAddress
          , KSHNPGMID         = vProgramId
        WHERE
              APKIHONCD       = iApkihoncd
          AND SHOPCD          = iShopcd
          AND DNPYNO          = iNyukinImpWorkRecord.DNPYNO
          AND NYKNDNPYNOEDANO = wExistsNyukinEdaNo
          AND NYKNKIND        = '20' -- (手数料）
          ;
        -- 入金消込履歴テーブル登録時に必要な入金伝票枝番号を変数に設定
        -- ※登録時にインクリメントするので、デクリメントしておく
        wNyukinEdaNo := wExistsNyukinEdaNo - 1;
      END IF;

      -- SEQ最大値取得
      wNyukinSeq := GetMaxSeq(iApkihoncd, iShopcd, iNyukinImpWorkRecord.DNPYNO);
      -- 入金消込履歴テーブル登録
      wS10T004TRecord.NYKNDNPYNO            := iNyukinImpWorkRecord.DNPYNO;
      wS10T004TRecord.NYKNDNPYNOEDANO       := wNyukinEdaNo + 1;
      wS10T004TRecord.SEQ                   := wNyukinSeq + 1;
      wS10T004TRecord.NYKNKSHKMPRCSKBN      := '10'; -- （入金入力）
      wS10T004TRecord.SKYSAKICD             := iSkysakicd;
      wS10T004TRecord.TOKUISAKICD           := iS07T001TRecord.TOKUISAKICD;
      wS10T004TRecord.NYKNDD                := iNyukinImpWorkRecord.NYKNDD;
      wS10T004TRecord.KSHKMDD               := vOperationDate;
      wS10T004TRecord.NYKNKIND              := '20'; -- (手数料）
      wS10T004TRecord.KSHKMNYKNKIND         := '20'; -- (手数料）
      wS10T004TRecord.KESHIKOMIKIN          := wAppliedAmount;
      wS10T004TRecord.SHOKAISKYSHONO        := iS07T001TRecord.SKYSHONO;
      wS10T004TRecord.TRKSDNPYNO            := NULL;
      wS10T004TRecord.SKYPRCSJISSIKBN       := iS07T001TRecord.SKYPRCSJISSIKBN;
      InsertS10T004T(wS10T004TRecord);
      -- 未消し手数料、消込金額から消込済金額を差し引く
      wUnappliedFee := wUnappliedFee - wAppliedAmount;
      wApplyAmount := wApplyAmount - wAppliedAmount;
    END IF;

    -- 戻り値設定
    oUnappliedReceipt := wUnappliedReceipt;
    oUnappliedDiscount := wUnappliedDiscount;
    oUnappliedFee := wUnappliedFee;

  END RegistAppliedReceiptAmount;

  /*****************************************************************************/
  /* プロシージャ名  ：入金入力消込処理                                        */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iSkysakicd            請求先コード                      */
  /*                 ：iPlusKairekinSum      プラス過入金合計                  */
  /*                 ：iMinusKairekinSum     マイナス過入金合計                */
  /*                 ：iNyukinImpWorkRecord  入金取込ワークデータ              */
  /*                 ：iS10W003TRecord       入金消込ワークデータ              */
  /*                 ：iDatsksmotokbn        データ作成元区分                  */
  /*                 ：iKaishutnshacd        回収担当者コード                  */
  /*                 ：oKairekinKeshikomikin 過入金消込金額                    */
  /*                 ：oUnappliedReceipt     未消し入金金額                    */
  /*                 ：oUnappliedDiscount    未消し値引金額                    */
  /*                 ：oUnappliedFee         未消し手数料                      */
  /*****************************************************************************/
  PROCEDURE NyukinInputClearingProcess(iApkihoncd             IN S10T001T.APKIHONCD%TYPE
                                      ,iShopcd                IN S10T001T.SHOPCD%TYPE
                                      ,iSkysakicd             IN S10T001T.SKYSAKICD%TYPE
                                      ,iPlusKairekinSum       IN NUMBER
                                      ,iMinusKairekinSum      IN NUMBER
                                      ,iNyukinImpWorkRecord   IN vTypeNyukinImportWorkListRecord
                                      ,iS10W003TRecord        IN S10W003T%ROWTYPE
                                      ,iDatsksmotokbn         IN S10T001T.DATSKSMOTOKBN%TYPE
                                      ,iKaishutnshacd         IN S10T001T.KAISHUTNSHACD%TYPE
                                      ,oKairekinKeshikomikin  OUT NUMBER
                                      ,oUnappliedReceipt      OUT NUMBER
                                      ,oUnappliedDiscount     OUT NUMBER
                                      ,oUnappliedFee          OUT NUMBER) IS
    ---------------------------
    -- 変数定義
    ---------------------------
    wKeshikomikin         NUMBER := 0;                                -- 消込金額
    wKeshikomizan         NUMBER := iS10W003TRecord.KESHIKOMIKIN;     -- 消込残額
    wKairekinTokuisakicd  S10T001T.TOKUISAKICD%TYPE;                  -- 過入金得意先コード
    wKairekinKeshikomikin NUMBER := 0;                                -- 過入金消込金額
    wUnappliedReceipt     NUMBER := iNyukinImpWorkRecord.NYKNKIN;     -- 未消し入金金額
    wUnappliedDiscount    NUMBER := iNyukinImpWorkRecord.NEBIKIKIN;   -- 未消し値引金額
    wUnappliedFee         NUMBER := iNyukinImpWorkRecord.TESURYO;     -- 未消し手数料

    ---------------------------
    -- レコード定義
    ---------------------------
    wKairekinList         vTypeKanyukinList;                          -- 過入金リストレコード（配列）
    wS07T001TRecord       S07T001T%ROWTYPE;                           -- 請求残高テーブルレコード
    wS10T001TRecord       S10T001T%ROWTYPE;                           -- 入金明細テーブルレコード
    wS10T004TRecord       S10T004T%ROWTYPE;                           -- 入金消込履歴テーブルレコード
  BEGIN
    -- プラス過入金合計＞0且つ今回消込額＞0の場合
    IF iPlusKairekinSum > 0 AND iS10W003TRecord.KESHIKOMIKIN > 0 THEN
      -- プラス過入金リスト取得
      GetPlusKanyukinList(iApkihoncd, iShopcd, iSkysakicd, wKairekinList);
      -- プラス過入金リストループ
      FOR i IN wKairekinList.FIRST..wKairekinList.LAST LOOP
        IF wKeshikomizan = 0 THEN
          EXIT; -- （消込残額＝0の場合ループを抜ける）
        END IF;

        -- 請求残高テーブルデータ取得
        wS07T001TRecord := GetS07T001TRecord(iApkihoncd, iShopcd, iS10W003TRecord.SKYSHONO,
          iSkysakicd, iS10W003TRecord.TOKUISAKICD);

        -- 請求残高テーブルデータなしまたは消込残高＜消込残額の場合
        IF wS07T001TRecord.KESHIKOMIZD IS NULL OR wS07T001TRecord.KESHIKOMIZD < wKeshikomizan THEN
          RAISE_APPLICATION_ERROR(-20011,
            '請求残高：' || iS10W003TRecord.SKYSHONO || 'への消込が行えません。');
        END IF;

        -- 消込金額設定（過入金額≧消込残額：消込残額／その他：過入金額）
        wKeshikomikin := (
          CASE
            WHEN wKairekinList(i).KAIREKIN >= wKeshikomizan THEN wKeshikomizan
            ELSE wKairekinList(i).KAIREKIN
          END);
        -- 過入金消込金額設定（過入金消込金額＋消込金額）
        wKairekinKeshikomikin := wKairekinKeshikomikin + wKeshikomikin;

        -- 過入金元更新
        UpdateKanyukinMoto(iApkihoncd, iShopcd, iSkysakicd, wKairekinList(i).DNPYNO,
          wKairekinList(i).NYKNDNPYNOEDANO, wKairekinList(i).NYKNDD, wKairekinList(i).NYKNKIND,
          iDatsksmotokbn, iKaishutnshacd, wKeshikomikin, wKairekinList(i).TOKUISAKICD,
          wS07T001TRecord.SKYSHONO, wS07T001TRecord.SKYPRCSJISSIKBN, wS07T001TRecord.TOKUISAKICD);

        -- 請求残高テーブル更新
        UpdateS07T001T(wS07T001TRecord, wS07T001TRecord.KESHIKOMIKIN + wKeshikomikin,
          wS07T001TRecord.KESHIKOMIZD - wKeshikomikin);

        -- 消込残額設定（消込残額－消込金額）
        wKeshikomizan := wKeshikomizan - wKeshikomikin;
      END LOOP;
    END IF;

    -- マイナス過入金合計＜0且つ今回消込額＜0の場合
    IF iMinusKairekinSum < 0 AND iS10W003TRecord.KESHIKOMIKIN < 0 THEN
      -- マイナス過入金リスト取得
      GetMinusKanyukinList(iApkihoncd, iShopcd, iSkysakicd, wKairekinList);
      -- マイナス過入金リストループ
      FOR i IN wKairekinList.FIRST..wKairekinList.LAST LOOP
        IF wKeshikomizan = 0 THEN
          EXIT; -- （消込残額＝0の場合ループを抜ける）
        END IF;

        -- 請求残高テーブルデータ取得
        wS07T001TRecord := GetS07T001TRecord(iApkihoncd, iShopcd, iS10W003TRecord.SKYSHONO,
          iSkysakicd, iS10W003TRecord.TOKUISAKICD);

        -- 請求残高テーブルデータなしまたは消込金額＜消込残額（絶対値）の場合
        IF wS07T001TRecord.KESHIKOMIKIN IS NULL OR wS07T001TRecord.KESHIKOMIKIN < ABS(wKeshikomizan) THEN
          RAISE_APPLICATION_ERROR(-20012,
            '請求残高：' || iS10W003TRecord.SKYSHONO || 'への消込戻しが行えません。');
        END IF;

        -- 消込金額設定（絶対値(過入金額)≧絶対値(消込残額)：消込残額／その他：過入金額）
        wKeshikomikin := (
          CASE
            WHEN ABS(wKairekinList(i).KAIREKIN) >= ABS(wKeshikomizan) THEN wKeshikomizan
            ELSE wKairekinList(i).KAIREKIN
          END);
        -- 過入金消込金額設定（過入金消込金額＋消込金額）
        wKairekinKeshikomikin := wKairekinKeshikomikin + wKeshikomikin;

        -- 過入金元更新
        UpdateKanyukinMoto(iApkihoncd, iShopcd, iSkysakicd, wKairekinList(i).DNPYNO,
          wKairekinList(i).NYKNDNPYNOEDANO, wKairekinList(i).NYKNDD, wKairekinList(i).NYKNKIND,
          iDatsksmotokbn, iKaishutnshacd, wKeshikomikin, wKairekinList(i).TOKUISAKICD,
          wS07T001TRecord.SKYSHONO, wS07T001TRecord.SKYPRCSJISSIKBN, wS07T001TRecord.TOKUISAKICD);

        -- 請求残高テーブル更新
        UpdateS07T001T(wS07T001TRecord, wS07T001TRecord.KESHIKOMIKIN + wKeshikomikin,
          wS07T001TRecord.KESHIKOMIZD - wKeshikomikin);

        -- 消込残額設定（消込残額－消込金額）
        wKeshikomizan := wKeshikomizan - wKeshikomikin;
      END LOOP;
    END IF;

    -- 消込残額＞0の場合
    IF wKeshikomizan > 0 THEN

      -- 請求残高テーブルデータ取得
      wS07T001TRecord := GetS07T001TRecord(iApkihoncd, iShopcd, iS10W003TRecord.SKYSHONO,
        iSkysakicd, iS10W003TRecord.TOKUISAKICD);
      -- 消込金額設定（消込残額）
      wKeshikomikin := wKeshikomizan;
      -- 請求残高テーブル更新
      UpdateS07T001T(wS07T001TRecord, wS07T001TRecord.KESHIKOMIKIN + wKeshikomikin,
        wS07T001TRecord.KESHIKOMIZD - wKeshikomikin);

      -- 消込済入金額登録処理
      RegistAppliedReceiptAmount(
        iApkihoncd,              -- AP基本コード
        iShopcd,                 -- ショップコード
        iSkysakicd,              -- 請求先コード
        iDatsksmotokbn,          -- データ作成元区分
        iKaishutnshacd,          -- 回収担当者コード
        wKeshikomikin,           -- 消込金額
        wS07T001TRecord,         -- 請求残高テーブルレコード
        iNyukinImpWorkRecord,    -- 入金取込ワークレコード
        wUnappliedReceipt,       -- 未消し入金金額（戻り値）
        wUnappliedDiscount,      -- 未消し値引金額（戻り値）
        wUnappliedFee            -- 未消し手数料（戻り値）
      );

      -- 入金取込得意先ワーク登録・更新
      MergeS10W002T(iApkihoncd, iShopcd, iSkysakicd, wS07T001TRecord.TOKUISAKICD, wKeshikomikin);
    END IF;

    -- 消込残額＜0の場合
    IF wKeshikomizan < 0 THEN

      -- 請求残高テーブルデータ取得
      wS07T001TRecord := GetS07T001TRecord(iApkihoncd, iShopcd, iS10W003TRecord.SKYSHONO,
        iSkysakicd, iS10W003TRecord.TOKUISAKICD);
      -- 消込金額設定（絶対値(消込残額)≧消込金額：消込金額×-1／その他：消込残額）
      wKeshikomikin := (
        CASE
          WHEN ABS(wKeshikomizan) >= wS07T001TRecord.KESHIKOMIKIN THEN wS07T001TRecord.KESHIKOMIKIN * -1
          ELSE wKeshikomizan
        END);
      -- 請求残高テーブル更新
      UpdateS07T001T(wS07T001TRecord, wS07T001TRecord.KESHIKOMIKIN + wKeshikomikin,
        wS07T001TRecord.KESHIKOMIZD - wKeshikomikin);

      -- 消込済入金額登録処理
      RegistAppliedReceiptAmount(
        iApkihoncd,              -- AP基本コード
        iShopcd,                 -- ショップコード
        iSkysakicd,              -- 請求先コード
        iDatsksmotokbn,          -- データ作成元区分
        iKaishutnshacd,          -- 回収担当者コード
        wKeshikomikin,           -- 消込金額
        wS07T001TRecord,         -- 請求残高テーブルレコード
        iNyukinImpWorkRecord,    -- 入金取込ワークレコード
        wUnappliedReceipt,       -- 未消し入金金額（戻り値）
        wUnappliedDiscount,      -- 未消し値引金額（戻り値）
        wUnappliedFee            -- 未消し手数料（戻り値）
      );

      -- 入金取込得意先ワーク登録・更新
      MergeS10W002T(iApkihoncd, iShopcd, iSkysakicd, wS07T001TRecord.TOKUISAKICD, wKeshikomikin);
    END IF;

    -- 戻り値（過入金消込金額、未消し入金金額、未消し値引金額、未消し手数料）設定
    oKairekinKeshikomikin := wKairekinKeshikomikin;
    oUnappliedReceipt := wUnappliedReceipt;
    oUnappliedDiscount := wUnappliedDiscount;
    oUnappliedFee := wUnappliedFee;

  END NyukinInputClearingProcess;

  /******************************************************************************/
  /* プロシージャ名  ：消込処理                                                 */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                     */
  /*                 ：iShopcd               ショップコード                     */
  /*                 ：iSkysakicd            請求先コード                       */
  /*                 ：iDatsksmotokbn        データ作成元区分                   */
  /*                 ：iDenpyonofrom         入金伝票番号開始値                 */
  /*                 ：iDenpyonoto           入金伝票番号終了値                 */
  /******************************************************************************/
  PROCEDURE ClearingProcess(iApkihoncd     IN S02M017T.APKIHONCD%TYPE
                           ,iShopcd        IN S02M017T.SHOPCD%TYPE
                           ,iSkysakicd     IN S02M017T.SKYSAKICD%TYPE
                           ,iDatsksmotokbn IN S10W001T.DATSKSMOTOKBN%TYPE
                           ,iDenpyonofrom  IN S10W001T.DNPYNO%TYPE
                           ,iDenpyonoto    IN S10W001T.DNPYNO%TYPE) IS
    ---------------------------
    -- カーソル定義
    ---------------------------
    -- 入金取込ワークカーソル
    CURSOR curS10W001T(pKaishutnshacd S10W001T.KAISHUTNSHACD%TYPE) IS
      SELECT
          DNPYNO
        , NYKNDD
        , TO_CHAR(TO_NUMBER(NYKNKIND))                          AS NYKNKIND
        , NVL(NYKNKIN, 0)                                       AS NYKNKIN
        , NVL(NEBIKIKIN, 0)                                     AS NEBIKIKIN
        , NVL(TESURYO, 0)                                       AS TESURYO
        , NVL(NYKNKIN, 0) + NVL(NEBIKIKIN, 0) + NVL(TESURYO, 0) AS KONKAINYKNKIN
        , NVL(KAISHUTNSHACD, pKaishutnshacd)                    AS KAISHUTNSHACD
        , BIKO
        , DATSKSMOTOKBN
      FROM
        S10W001T
      WHERE
            APKIHONCD     = iApkihoncd
        AND SHOPCD        = iShopcd
        AND SKYSAKICD     = iSkysakicd
        AND DATSKSMOTOKBN = iDatsksmotokbn
        AND NYKNDATSKSFLG = '0'
        AND KSHNUNYOYMD   <= vOperationDate
        AND DNPYNO BETWEEN iDenpyonofrom AND iDenpyonoto
      ORDER BY
        DNPYNO
      FOR UPDATE;

    -- 入金消込ワーク（プラス入金）カーソル
    CURSOR curS10W003TByPlusNyukin(pDenpyono S10W003T.DENPYONO%TYPE) IS
      SELECT
        *
      FROM
        S10W003T
      WHERE
            APKIHONCD = iApkihoncd
        AND SHOPCD    = iShopcd
        AND DENPYONO  = pDenpyono
      ORDER BY
        SKYSHONO
      ;

    -- 入金消込ワーク（マイナス入金）カーソル
    CURSOR curS10W003TByMinusNyukin(pDenpyono S10W003T.DENPYONO%TYPE) IS
      SELECT
        NKWT.*
      FROM
        S10W003T NKWT
        INNER JOIN S07T001T SKZT ON (
              SKZT.APKIHONCD    = NKWT.APKIHONCD
          AND SKZT.SHOPCD       = NKWT.SHOPCD
          AND SKZT.SKYSHONO     = NKWT.SKYSHONO
          AND SKZT.TOKUISAKICD  = NKWT.TOKUISAKICD
          )
      WHERE
            NKWT.APKIHONCD = iApkihoncd
        AND NKWT.SHOPCD    = iShopcd
        AND NKWT.DENPYONO  = pDenpyono
      ORDER BY
          SKZT.KONKAISHIMEDD DESC
        , SKZT.TOKUISAKICD ASC
      ;
    ---------------------------
    -- 変数定義
    ---------------------------
    wPlusKairekinSum      NUMBER := 0;                  -- プラス過入金合計
    wMinusKairekinSum     NUMBER := 0;                  -- マイナス過入金合計
    wPlusSumSosaikin      NUMBER := 0;                  -- プラス合計相殺金額
    wMinusSumSosaikin     NUMBER := 0;                  -- マイナス合計相殺金額
    wKeshikomikanoukin    NUMBER := 0;                  -- 消込可能額
    wKairekin             S02M017T.KAIREKIN%TYPE;       -- 過入金額
    wKeshikomikinSum      NUMBER;                       -- 消込金額合計
    wKairekinKeshikomikin NUMBER := 0;                  -- 過入金消込金額
    wPlusIndex            NUMBER;                       -- プラス過入金リストインデックス
    wMinusIndex           NUMBER;                       -- マイナス過入金リストインデックス
    wUnappliedReceipt     NUMBER := 0;                  -- 未消し入金金額
    wUnappliedDiscount    NUMBER := 0;                  -- 未消し値引金額
    wUnappliedFee         NUMBER := 0;                  -- 未消し手数料

    ---------------------------
    -- レコード定義
    ---------------------------
    wPlusKairekinList     vTypeKanyukinList;            -- プラス過入金リストレコード（配列）
    wMinusKairekinList    vTypeKanyukinList;            -- マイナス過入金リストレコード（配列）
    wS10T003TRecord       S10T003T%ROWTYPE;             -- 入金管理テーブルレコード
    wS10W003TRecord       S10W003T%ROWTYPE;             -- 入金消込ワークレコード
    wS10W001TRecord       curS10W001T%ROWTYPE;          -- 入金取込ワークカーソルレコード
    wPlus                 vTypeKanyukinListRecord;      -- プラス過入金リストレコード
    wMinus                vTypeKanyukinListRecord;      -- マイナス過入金リストレコード
    wS02M017TRecord       S02M017T%ROWTYPE;             -- 請求先マスタレコード
  BEGIN
    -- 請求先マスタ（回収担当者コード、過入金額）取得
    wS02M017TRecord := GetS02M017TRecord(iApkihoncd, iShopcd, iSkysakicd);

    -- 入金取込ワークカーソルループ
    FOR wS10W001TRecord IN curS10W001T(wS02M017TRecord.KAISHUTNSHACD) LOOP
      -- プラス過入金合計、マイナス過入金合計取得（入金管理テーブル、入金明細テーブル）
      SELECT
          NVL(SUM(CASE
            WHEN NYMT.NYKNKIN + NYMT.NEBIKIKIN + NYMT.TESURYO - NYMT.KESHIKOMIKIN - NYMT.SOSAIKIN > 0 THEN
              NYMT.NYKNKIN + NYMT.NEBIKIKIN + NYMT.TESURYO - NYMT.KESHIKOMIKIN - NYMT.SOSAIKIN
            ELSE 0
          END), 0)
        , NVL(SUM(CASE
            WHEN NYMT.NYKNKIN + NYMT.NEBIKIKIN + NYMT.TESURYO - NYMT.KESHIKOMIKIN - NYMT.SOSAIKIN < 0 THEN
              NYMT.NYKNKIN + NYMT.NEBIKIKIN + NYMT.TESURYO - NYMT.KESHIKOMIKIN - NYMT.SOSAIKIN
            ELSE 0
          END), 0)
      INTO
        wPlusKairekinSum, wMinusKairekinSum
      FROM
        S10T003T NYKT
        INNER JOIN S10T001T NYMT ON (
                NYMT.APKIHONCD     = NYKT.APKIHONCD
            AND NYMT.SHOPCD        = NYKT.SHOPCD
            AND NYMT.DNPYNO        = NYKT.NYKNDNPYNO
          )
      WHERE
            NYKT.APKIHONCD         = iApkihoncd
        AND NYKT.SHOPCD            = iShopcd
        AND NYKT.SKYSAKICD         = iSkysakicd
        AND NYKT.KAIREKINKANRIFLG  = '1'
      ;

      -- プラス過入金合計及びマイナス過入金合計が0以外の場合
      IF wPlusKairekinSum != 0 AND wMinusKairekinSum != 0 THEN
        -- プラス過入金リスト取得
        GetPlusKanyukinList(iApkihoncd, iShopcd, iSkysakicd, wPlusKairekinList);
        -- マイナス過入金リスト取得
        GetMinusKanyukinList(iApkihoncd, iShopcd, iSkysakicd, wMinusKairekinList);

        -- プラス・マイナス過入金リストインデックスの初期値設定
        wPlusIndex := wPlusKairekinList.FIRST;
        wPlus := wPlusKairekinList(wPlusIndex);
        wMinusIndex := wMinusKairekinList.FIRST;
        wMinus := wMinusKairekinList(wMinusIndex);

        LOOP
          -- プラス・マイナス過入金リストのインデックス超過又はプラス・マイナス合計過入金額＝0の場合ループを抜ける
          IF NOT wPlusKairekinList.EXISTS(wPlusIndex) OR NOT wMinusKairekinList.EXISTS(wMinusIndex)
              OR wPlusKairekinSum = 0 OR wMinusKairekinSum = 0 THEN
            EXIT;
          END IF;

          -- 過入金相殺処理（プラス過入金相殺）
          KanyukinSosaiProcess(iApkihoncd, iShopcd, iSkysakicd, GetKanyukinList(wPlus)
            , (CASE WHEN wPlus.KAIREKIN <= ABS(wMinus.KAIREKIN) THEN wPlus.KAIREKIN ELSE wMinus.KAIREKIN * -1 END)
            , iDatsksmotokbn, wS10W001TRecord.KAISHUTNSHACD, NULL, wMinus.TOKUISAKICD, wPlusSumSosaikin);

          -- 過入金相殺処理（マイナス過入金相殺）
          KanyukinSosaiProcess(iApkihoncd, iShopcd, iSkysakicd, GetKanyukinList(wMinus)
            , (CASE WHEN wPlus.KAIREKIN <= ABS(wMinus.KAIREKIN) THEN wPlus.KAIREKIN * -1 ELSE wMinus.KAIREKIN END)
            , iDatsksmotokbn, wS10W001TRecord.KAISHUTNSHACD, NULL, wMinus.TOKUISAKICD, wMinusSumSosaikin);

          -- 合計過入金から相殺金額減算（プラス過入金合計、マイナス過入金合計）
          wPlusKairekinSum := wPlusKairekinSum - wPlusSumSosaikin;
          wMinusKairekinSum := wMinusKairekinSum - wMinusSumSosaikin;

          -- プラス・マイナス過入金リストインデックス、過入金額設定
          IF wPlus.KAIREKIN < ABS(wMinus.KAIREKIN) THEN
            wPlusIndex := wPlusIndex + 1;
            wPlus := CASE WHEN wPlusKairekinList.EXISTS(wPlusIndex) THEN wPlusKairekinList(wPlusIndex) ELSE NULL END;
            wMinus.KAIREKIN := wMinus.KAIREKIN - wMinusSumSosaikin;
          ELSIF wPlus.KAIREKIN > ABS(wMinus.KAIREKIN) THEN
            wMinusIndex := wMinusIndex + 1;
            wMinus := CASE WHEN wMinusKairekinList.EXISTS(wMinusIndex) THEN wMinusKairekinList(wMinusIndex) ELSE NULL END;
            wPlus.KAIREKIN := wPlus.KAIREKIN - wPlusSumSosaikin;
          ELSE
            wPlusIndex := wPlusIndex + 1;
            wMinusIndex := wMinusIndex + 1;
          END IF;
        END LOOP;
      END IF;

      -- 今回入金額≠0の場合
      IF wS10W001TRecord.KONKAINYKNKIN != 0 THEN
        -- 入金管理テーブル登録
        wS10T003TRecord.APKIHONCD        := iApkihoncd;
        wS10T003TRecord.SHOPCD           := iShopcd;
        wS10T003TRecord.NYKNDNPYNO       := wS10W001TRecord.DNPYNO;
        wS10T003TRecord.SKYSAKICD        := iSkysakicd;
        wS10T003TRecord.NYKNDD           := wS10W001TRecord.NYKNDD;
        wS10T003TRecord.NYKNKIND         := wS10W001TRecord.NYKNKIND;
        wS10T003TRecord.NYKNKIN          := wS10W001TRecord.NYKNKIN;
        wS10T003TRecord.NEBIKIKIN        := wS10W001TRecord.NEBIKIKIN;
        wS10T003TRecord.TESURYO          := wS10W001TRecord.TESURYO;
        wS10T003TRecord.KAIREKIN         := 0;
        wS10T003TRecord.URICHOSEIKIN     := 0;
        wS10T003TRecord.TRKSDNPYNO       := NULL;
        wS10T003TRecord.KAIREKINKANRIFLG := '0';
        InsertS10T003T(wS10T003TRecord);
      END IF;

      -- 消込可能額設定（今回入金額）
      wKeshikomikanoukin := wS10W001TRecord.KONKAINYKNKIN;

      -- プラス過入金合計＞0且つ今回入金額＜0の場合
      IF wPlusKairekinSum > 0 AND wS10W001TRecord.KONKAINYKNKIN < 0 THEN
        -- プラス過入金リスト取得
        GetPlusKanyukinList(iApkihoncd, iShopcd, iSkysakicd, wPlusKairekinList);
        -- 過入金相殺処理
        KanyukinSosaiProcess(iApkihoncd, iShopcd, iSkysakicd, wPlusKairekinList,
          wS10W001TRecord.KONKAINYKNKIN * -1, iDatsksmotokbn,
          wS10W001TRecord.KAISHUTNSHACD, wS10W001TRecord, NULL, wPlusSumSosaikin);
        -- 過入金合計から相殺した金額を減算（プラス過入金合計、消込可能額）
        wPlusKairekinSum := wPlusKairekinSum - wPlusSumSosaikin;
        wKeshikomikanoukin := wKeshikomikanoukin + wPlusSumSosaikin;
      END IF;

      -- マイナス過入金合計＜0且つ今回入金額＞0の場合
      IF wMinusKairekinSum < 0 AND wS10W001TRecord.KONKAINYKNKIN > 0 THEN
        -- マイナス過入金リスト取得
        GetMinusKanyukinList(iApkihoncd, iShopcd, iSkysakicd, wMinusKairekinList);
        -- 過入金相殺処理
        KanyukinSosaiProcess(iApkihoncd, iShopcd, iSkysakicd, wMinusKairekinList,
          wS10W001TRecord.KONKAINYKNKIN * -1, iDatsksmotokbn,
          wS10W001TRecord.KAISHUTNSHACD, wS10W001TRecord, NULL, wMinusSumSosaikin);
        -- 過入金合計から相殺した金額を減算（マイナス過入金合計、消込可能額）
        wMinusKairekinSum := wMinusKairekinSum - wMinusSumSosaikin;
        wKeshikomikanoukin := wKeshikomikanoukin + wMinusSumSosaikin;
      END IF;

      -- 消込可能額設定（消込可能額＋プラス過入金合計＋マイナス過入金合計）
      wKeshikomikanoukin := wKeshikomikanoukin + wPlusKairekinSum + wMinusKairekinSum;

      -- 一括入金消込の場合
      IF wS10W001TRecord.DATSKSMOTOKBN != '07' THEN
        -- 一括消込処理
        BulkClearingProcess(iApkihoncd, iShopcd, iSkysakicd, wPlusKairekinSum, wMinusKairekinSum,
          wS10W001TRecord, wKeshikomikanoukin, iDatsksmotokbn, wS10W001TRecord.KAISHUTNSHACD);
      ELSE
        -- 消込可能額≧0の場合
        IF wKeshikomikanoukin >= 0 THEN
          FOR wS10W003TRecord IN curS10W003TByPlusNyukin(wS10W001TRecord.DNPYNO) LOOP
            -- 入金入力消込処理
            NyukinInputClearingProcess(
              iApkihoncd,                    -- AP基本コード
              iShopcd,                       -- ショップコード
              iSkysakicd,                    -- 請求先コード
              wPlusKairekinSum,              -- プラス過入金額設定
              wMinusKairekinSum,             -- マイナス過入金額設定
              wS10W001TRecord,               -- 入金取込ワークデータ
              wS10W003TRecord,               -- 入金消込ワークデータ
              iDatsksmotokbn,                -- データ作成元区分
              wS10W001TRecord.KAISHUTNSHACD, -- 回収担当者コード
              wKairekinKeshikomikin,         -- 過入金消込金額
              wUnappliedReceipt,             -- 未消し入金金額（戻り値）
              wUnappliedDiscount,            -- 未消し値引金額（戻り値）
              wUnappliedFee                  -- 未消し手数料（戻り値）
            );
            -- 入金取込ワークデータに入金入力消込処理結果を反映
            wS10W001TRecord.NYKNKIN := wUnappliedReceipt;
            wS10W001TRecord.NEBIKIKIN := wUnappliedDiscount;
            wS10W001TRecord.TESURYO := wUnappliedFee;
            -- 消込可能額設定（消込可能額－今回消込金額）
            wKeshikomikanoukin := wKeshikomikanoukin - wS10W003TRecord.KESHIKOMIKIN;
            -- プラス過入金額設定（プラス過入金額－過入金消込金額）
            IF wPlusKairekinSum > 0 THEN
              wPlusKairekinSum := wPlusKairekinSum - wKairekinKeshikomikin;
            END IF;
            -- マイナス過入金額設定（マイナス過入金額－過入金消込金額）
            IF wMinusKairekinSum < 0 THEN
              wMinusKairekinSum := wMinusKairekinSum - wKairekinKeshikomikin;
            END IF;
          END LOOP;

        -- 消込可能額＜0の場合
        ELSE
          FOR wS10W003TRecord IN curS10W003TByMinusNyukin(wS10W001TRecord.DNPYNO) LOOP
            -- 入金入力消込処理
            NyukinInputClearingProcess(
              iApkihoncd,                    -- AP基本コード
              iShopcd,                       -- ショップコード
              iSkysakicd,                    -- 請求先コード
              wPlusKairekinSum,              -- プラス過入金額設定
              wMinusKairekinSum,             -- マイナス過入金額設定
              wS10W001TRecord,               -- 入金取込ワークデータ
              wS10W003TRecord,               -- 入金消込ワークデータ
              iDatsksmotokbn,                -- データ作成元区分
              wS10W001TRecord.KAISHUTNSHACD, -- 回収担当者コード
              wKairekinKeshikomikin,         -- 過入金消込金額
              wUnappliedReceipt,             -- 未消し入金金額（戻り値）
              wUnappliedDiscount,            -- 未消し値引金額（戻り値）
              wUnappliedFee                  -- 未消し手数料（戻り値）
            );
            -- 入金取込ワークデータに入金入力消込処理結果を反映
            wS10W001TRecord.NYKNKIN := wUnappliedReceipt;
            wS10W001TRecord.NEBIKIKIN := wUnappliedDiscount;
            wS10W001TRecord.TESURYO := wUnappliedFee;
            -- 消込可能額設定（消込可能額－今回消込金額）
            wKeshikomikanoukin := wKeshikomikanoukin - wS10W003TRecord.KESHIKOMIKIN;
            -- プラス過入金額設定（プラス過入金額－過入金消込金額）
            IF wPlusKairekinSum > 0 THEN
              wPlusKairekinSum := wPlusKairekinSum - wKairekinKeshikomikin;
            END IF;
            -- マイナス過入金額設定（マイナス過入金額－過入金消込金額）
            IF wMinusKairekinSum < 0 THEN
              wMinusKairekinSum := wMinusKairekinSum - wKairekinKeshikomikin;
            END IF;
          END LOOP;
        END IF;

        -- 消込金額合計取得
        wKeshikomikinSum := GetKeshikomikinSum(iApkihoncd, iShopcd, wS10W001TRecord.DNPYNO);

        -- 今回入金額－消込可能額≠0の場合
        IF wS10W001TRecord.KONKAINYKNKIN - wKeshikomikinSum != 0 THEN
          -- 入金金額、値引金額、手数料それぞれの過入金反映更新
          IF wS10W001TRecord.NYKNKIN <> 0 THEN
            NyukinInputUpdateKanyukinReflection(iApkihoncd, iShopcd, iSkysakicd,
              wS10W001TRecord, wS10W001TRecord.NYKNKIND, '1', iDatsksmotokbn, wS10W001TRecord.KAISHUTNSHACD);
          END IF;
          IF wS10W001TRecord.NEBIKIKIN <> 0 THEN
            NyukinInputUpdateKanyukinReflection(iApkihoncd, iShopcd, iSkysakicd,
              wS10W001TRecord, '21', '1', iDatsksmotokbn, wS10W001TRecord.KAISHUTNSHACD);
          END IF;
          IF wS10W001TRecord.TESURYO <> 0 THEN
            NyukinInputUpdateKanyukinReflection(iApkihoncd, iShopcd, iSkysakicd,
              wS10W001TRecord, '20', '1', iDatsksmotokbn, wS10W001TRecord.KAISHUTNSHACD);
          END IF;
          -- 入金管理テーブルに過入金額を反映
          UpdateS10T003T(iApkihoncd, iShopcd, wS10W001TRecord.DNPYNO, '1',
            wS10W001TRecord.KONKAINYKNKIN - wKeshikomikinSum, NULL);
        END IF;

        -- 消込可能額≠消込前過入金額の場合
        IF wKeshikomikanoukin != wS02M017TRecord.KAIREKIN THEN
          -- 過入金額設定（消込可能額）
          wKairekin := wKeshikomikanoukin;
          -- 請求先マスタ更新（過入金額）
          UpdateS02M017TForKairekin(iApkihoncd, iShopcd, iSkysakicd, wKairekin);
        END IF;
      END IF;
    END LOOP;
  END ClearingProcess;

  /******************************************************************************/
  /* プロシージャ名  ：売上調整処理                                             */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                     */
  /*                 ：iShopcd               ショップコード                     */
  /*                 ：iS10W001TRecord       入金取込ワークレコード             */
  /******************************************************************************/
  PROCEDURE SalesAdjustProcess(iApkihoncd       IN S07T001T.APKIHONCD%TYPE
                              ,iShopcd          IN S07T001T.SHOPCD%TYPE
                              ,iS10W001TRecord  IN S10W001T%ROWTYPE) IS
    ---------------------------
    -- カーソル定義
    ---------------------------
    -- 入金消込ワークカーソル
    CURSOR curS10W003T IS
      SELECT
        *
      FROM
        S10W003T
      WHERE
            APKIHONCD = iApkihoncd
        AND SHOPCD    = iShopcd
        AND DENPYONO  = iS10W001TRecord.DNPYNO
      ORDER BY
        SKYSHONO
      ;
    ---------------------------
    -- 変数定義
    ---------------------------
    wIndex                      NUMBER;                             -- 請求残高テーブルレコードインデックス
    wChoseizan                  S10W003T.KESHIKOMIKIN%TYPE;         -- 調整残高（消込先）
    wChoseimotozan              S07T001T.KESHIKOMIZD%TYPE;          -- 調整残高（消込元）
    wChoseikin                  S10T003T.URICHOSEIKIN%TYPE;         -- 調整金額
    wChoseikinTotal             S10T003T.URICHOSEIKIN%TYPE := 0;    -- 調整金額合計
    wChoseikinSum               S10T003T.URICHOSEIKIN%TYPE := 0;    -- 調整金額合計（消込先残高別）
    wChoseimotoSum              S10T003T.URICHOSEIKIN%TYPE := 0;    -- 調整金額合計（調整元残高別）
    wEdaNo                      S10T001T.NYKNDNPYNOEDANO%TYPE := 0; -- 枝番号
    wSeq                        S10T004T.SEQ%TYPE := 0;             -- SEQ
    wKeshikomikin               S07T001T.KESHIKOMIKIN%TYPE;         -- 消込金額（未使用）
    wKeshikomizd                S07T001T.KESHIKOMIZD%TYPE;          -- 消込残高（未使用）
    ---------------------------
    -- レコード定義
    ---------------------------
    TYPE typeS07T001T IS TABLE OF S07T001T%ROWTYPE INDEX BY BINARY_INTEGER;
    wS10W003TRecord             curS10W003T%ROWTYPE;                -- 入金消込ワークカーソルレコード
    wS07T001TSalesAdjustRecord  typeS07T001T;                       -- 請求残高テーブルカーソルレコード（売上調整）
    wS07T001TRecord             S07T001T%ROWTYPE;                   -- 請求残高テーブルカーソルレコード
    wS10T001TRecord             S10T001T%ROWTYPE;                   -- 入金明細テーブルレコード
    wS10T003TRecord             S10T003T%ROWTYPE;                   -- 入金管理テーブルレコード
    wS10T004TRecord             S10T004T%ROWTYPE;                   -- 入金消込履歴テーブルレコード
  BEGIN
    -- 請求残高テーブルデータ取得（売上調整）
    SELECT
      *
    BULK COLLECT INTO
      wS07T001TSalesAdjustRecord
    FROM
      S07T001T
    WHERE
          APKIHONCD   = iApkihoncd
      AND SHOPCD      = iShopcd
      AND SKYSAKICD   = iS10W001TRecord.SKYSAKICD
      AND KESHIKOMIZD < 0
      AND YUKOKBN     = '1'
    ORDER BY
        KONKAISHIMEDD
      , TOKUISAKICD
    ;
    -- 請求残高テーブルインデックス設定
    wIndex := wS07T001TSalesAdjustRecord.FIRST;
    -- 調整残高（調整元）設定（消込残高）
    wChoseimotozan := wS07T001TSalesAdjustRecord(wIndex).KESHIKOMIZD;

    -- 入金消込ワークカーソルループ
    FOR wS10W003TRecord IN curS10W003T LOOP
      -- 調整残高（消込先）設定（今回消込金額）
      wChoseizan := wS10W003TRecord.KESHIKOMIKIN;
      -- 調整金合計（消込先残高別）をリセット
      wChoseikinSum := 0;

      -- 請求残高テーブルデータ取得
      wS07T001TRecord := GetS07T001TRecord(iApkihoncd, iShopcd,
        wS10W003TRecord.SKYSHONO, NULL, wS10W003TRecord.TOKUISAKICD);

      -- 今回消込金額＞消込残高の場合
      IF wS10W003TRecord.KESHIKOMIKIN > wS07T001TRecord.KESHIKOMIZD THEN
        RAISE_APPLICATION_ERROR(-20013,
          '請求残高テーブルの消込残高が変更されています。請求書番号：' || wS10W003TRecord.SKYSHONO);
      END IF;

      -- 調整残高＝0になるまでループ
      WHILE wChoseizan != 0 LOOP
        -- 調整金額設定（今回消込金額≧消込残高（絶対値）：消込残高×-1／その他：今回消込金額）
        wChoseikin := (CASE WHEN wChoseizan >= ABS(wChoseimotozan) THEN wChoseimotozan * -1 ELSE wChoseizan END);

        -- 枝番号加算
        wEdaNo := wEdaNo + 1;

        -- 入金消込ワーク.得意先コード＝請求残高テーブル.得意先コードの場合
        IF wS10W003TRecord.TOKUISAKICD = wS07T001TSalesAdjustRecord(wIndex).TOKUISAKICD THEN
          -- 入金明細テーブル登録
          wS10T001TRecord.APKIHONCD             := iApkihoncd;
          wS10T001TRecord.SHOPCD                := iShopcd;
          wS10T001TRecord.DNPYNO                := iS10W001TRecord.DNPYNO;
          wS10T001TRecord.NYKNDNPYNOEDANO       := wEdaNo;
          wS10T001TRecord.KAIREKINKBN           := '0'; -- （入金）
          wS10T001TRecord.SKYSAKICD             := iS10W001TRecord.SKYSAKICD;
          wS10T001TRecord.TOKUISAKICD           := wS10W003TRecord.TOKUISAKICD;
          wS10T001TRecord.NYKNDD                := iS10W001TRecord.NYKNDD;
          wS10T001TRecord.NYKNKIND              := '31'; -- （売上調整金額）
          wS10T001TRecord.NYKNKIN               := 0;
          wS10T001TRecord.NEBIKIKIN             := 0;
          wS10T001TRecord.TESURYO               := 0;
          wS10T001TRecord.BIKO                  := iS10W001TRecord.BIKO;
          wS10T001TRecord.KESHIKOMIKIN          := 0;
          wS10T001TRecord.SOSAIKIN              := 0;
          wS10T001TRecord.DATSKSMOTOKBN         := iS10W001TRecord.DATSKSMOTOKBN;
          wS10T001TRecord.SKYSHOOTPTARINASHIKBN := '1'; -- （出力なし）
          wS10T001TRecord.KAISHUTNSHACD         := iS10W001TRecord.KAISHUTNSHACD;
          wS10T001TRecord.TRKSDNPYNO            := NULL;
          InsertS10T001T(wS10T001TRecord);

          -- 請求残高テーブル更新（消込先）
          UpdateS07T001TByItem(iApkihoncd, iShopcd,
            wS10W003TRecord.SKYSHONO, NULL, wS10W003TRecord.TOKUISAKICD,
            wS07T001TRecord.KESHIKOMIKIN + (wChoseikinSum + wChoseikin),
            wS07T001TRecord.KESHIKOMIZD - (wChoseikinSum + wChoseikin));

          -- SEQ加算
          wSeq := wSeq + 1;

          -- 入金消込履歴テーブル登録（消込先）
          wS10T004TRecord.NYKNDNPYNO            := iS10W001TRecord.DNPYNO;
          wS10T004TRecord.NYKNDNPYNOEDANO       := wEdaNo;
          wS10T004TRecord.SEQ                   := wSeq;
          wS10T004TRecord.NYKNKSHKMPRCSKBN      := '11'; -- （売上調整）
          wS10T004TRecord.SKYSAKICD             := iS10W001TRecord.SKYSAKICD;
          wS10T004TRecord.TOKUISAKICD           := wS10W003TRecord.TOKUISAKICD;
          wS10T004TRecord.NYKNDD                := iS10W001TRecord.NYKNDD;
          wS10T004TRecord.KSHKMDD               := vOperationDate;
          wS10T004TRecord.NYKNKIND              := iS10W001TRecord.NYKNKIND;
          wS10T004TRecord.KSHKMNYKNKIND         := iS10W001TRecord.NYKNKIND;
          wS10T004TRecord.KESHIKOMIKIN          := wChoseikin;
          wS10T004TRecord.SHOKAISKYSHONO        := wS10W003TRecord.SKYSHONO;
          wS10T004TRecord.TRKSDNPYNO            := NULL;
          wS10T004TRecord.SKYPRCSJISSIKBN       := wS07T001TRecord.SKYPRCSJISSIKBN;
          InsertS10T004T(wS10T004TRecord);

          -- 請求残高テーブル更新（調整元）
          UpdateS07T001TByItem(iApkihoncd, iShopcd,
            wS07T001TSalesAdjustRecord(wIndex).SKYSHONO, NULL,
            wS07T001TSalesAdjustRecord(wIndex).TOKUISAKICD,
            wS07T001TSalesAdjustRecord(wIndex).KESHIKOMIKIN - (wChoseimotoSum + wChoseikin),
            wS07T001TSalesAdjustRecord(wIndex).KESHIKOMIZD + (wChoseimotoSum + wChoseikin));

          -- SEQ加算
          wSeq := wSeq + 1;

          -- 入金消込履歴テーブル登録（調整元）
          wS10T004TRecord.NYKNDNPYNO            := iS10W001TRecord.DNPYNO;
          wS10T004TRecord.NYKNDNPYNOEDANO       := wEdaNo;
          wS10T004TRecord.SEQ                   := wSeq;
          wS10T004TRecord.NYKNKSHKMPRCSKBN      := '11'; -- （売上調整）
          wS10T004TRecord.SKYSAKICD             := iS10W001TRecord.SKYSAKICD;
          wS10T004TRecord.TOKUISAKICD           := wS07T001TSalesAdjustRecord(wIndex).TOKUISAKICD;
          wS10T004TRecord.NYKNDD                := iS10W001TRecord.NYKNDD;
          wS10T004TRecord.KSHKMDD               := vOperationDate;
          wS10T004TRecord.NYKNKIND              := iS10W001TRecord.NYKNKIND;
          wS10T004TRecord.KSHKMNYKNKIND         := iS10W001TRecord.NYKNKIND;
          wS10T004TRecord.KESHIKOMIKIN          := wChoseikin * -1;
          wS10T004TRecord.SHOKAISKYSHONO        := wS07T001TSalesAdjustRecord(wIndex).SKYSHONO;
          wS10T004TRecord.TRKSDNPYNO            := NULL;
          wS10T004TRecord.SKYPRCSJISSIKBN       := wS07T001TSalesAdjustRecord(wIndex).SKYPRCSJISSIKBN;
          InsertS10T004T(wS10T004TRecord);

        -- 入金消込ワーク.得意先コード≠請求残高テーブル.得意先コードの場合
        ELSE
          -- SEQ加算
          wSeq := wSeq + 1;

          -- 入金明細テーブル登録（消込先）
          wS10T001TRecord.APKIHONCD             := iApkihoncd;
          wS10T001TRecord.SHOPCD                := iShopcd;
          wS10T001TRecord.DNPYNO                := iS10W001TRecord.DNPYNO;
          wS10T001TRecord.NYKNDNPYNOEDANO       := wEdaNo;
          wS10T001TRecord.KAIREKINKBN           := '0'; -- （入金）
          wS10T001TRecord.SKYSAKICD             := iS10W001TRecord.SKYSAKICD;
          wS10T001TRecord.TOKUISAKICD           := wS10W003TRecord.TOKUISAKICD;
          wS10T001TRecord.NYKNDD                := iS10W001TRecord.NYKNDD;
          wS10T001TRecord.NYKNKIND              := '31'; -- （売上調整金額）
          wS10T001TRecord.NYKNKIN               := wChoseikin;
          wS10T001TRecord.NEBIKIKIN             := 0;
          wS10T001TRecord.TESURYO               := 0;
          wS10T001TRecord.BIKO                  := iS10W001TRecord.BIKO;
          wS10T001TRecord.KESHIKOMIKIN          := wChoseikin;
          wS10T001TRecord.SOSAIKIN              := 0;
          wS10T001TRecord.DATSKSMOTOKBN         := iS10W001TRecord.DATSKSMOTOKBN;
          wS10T001TRecord.SKYSHOOTPTARINASHIKBN := '1'; -- （出力なし）
          wS10T001TRecord.KAISHUTNSHACD         := iS10W001TRecord.KAISHUTNSHACD;
          wS10T001TRecord.TRKSDNPYNO            := NULL;
          InsertS10T001T(wS10T001TRecord);

          -- 請求残高テーブル更新（消込先）
          UpdateS07T001TByItem(iApkihoncd, iShopcd,
            wS10W003TRecord.SKYSHONO, NULL, wS10W003TRecord.TOKUISAKICD,
            wS07T001TRecord.KESHIKOMIKIN + (wChoseikinSum + wChoseikin),
            wS07T001TRecord.KESHIKOMIZD - (wChoseikinSum + wChoseikin));

          -- 入金消込履歴テーブル登録（消込先）
          wS10T004TRecord.NYKNDNPYNO            := iS10W001TRecord.DNPYNO;
          wS10T004TRecord.NYKNDNPYNOEDANO       := wEdaNo;
          wS10T004TRecord.SEQ                   := wSeq;
          wS10T004TRecord.NYKNKSHKMPRCSKBN      := '11'; -- （売上調整）
          wS10T004TRecord.SKYSAKICD             := iS10W001TRecord.SKYSAKICD;
          wS10T004TRecord.TOKUISAKICD           := wS10W003TRecord.TOKUISAKICD;
          wS10T004TRecord.NYKNDD                := iS10W001TRecord.NYKNDD;
          wS10T004TRecord.KSHKMDD               := vOperationDate;
          wS10T004TRecord.NYKNKIND              := iS10W001TRecord.NYKNKIND;
          wS10T004TRecord.KSHKMNYKNKIND         := iS10W001TRecord.NYKNKIND;
          wS10T004TRecord.KESHIKOMIKIN          := wChoseikin;
          wS10T004TRecord.SHOKAISKYSHONO        := wS10W003TRecord.SKYSHONO;
          wS10T004TRecord.TRKSDNPYNO            := NULL;
          wS10T004TRecord.SKYPRCSJISSIKBN       := wS07T001TRecord.SKYPRCSJISSIKBN;
          InsertS10T004T(wS10T004TRecord);

          -- 入金取込得意先ワーク登録・更新（消込先）
          MergeS10W002T(iApkihoncd, iShopcd, iS10W001TRecord.SKYSAKICD,
            wS10W003TRecord.TOKUISAKICD, wChoseikin);

          -- 枝番号・SEQ加算
          wEdaNo := wEdaNo + 1;
          wSeq := wSeq + 1;

          -- 入金明細テーブル登録（調整元）
          wS10T001TRecord.APKIHONCD             := iApkihoncd;
          wS10T001TRecord.SHOPCD                := iShopcd;
          wS10T001TRecord.DNPYNO                := iS10W001TRecord.DNPYNO;
          wS10T001TRecord.NYKNDNPYNOEDANO       := wEdaNo;
          wS10T001TRecord.KAIREKINKBN           := '0'; -- （入金）
          wS10T001TRecord.SKYSAKICD             := iS10W001TRecord.SKYSAKICD;
          wS10T001TRecord.TOKUISAKICD           := wS07T001TSalesAdjustRecord(wIndex).TOKUISAKICD;
          wS10T001TRecord.NYKNDD                := iS10W001TRecord.NYKNDD;
          wS10T001TRecord.NYKNKIND              := '31'; -- （売上調整金額）
          wS10T001TRecord.NYKNKIN               := wChoseikin * -1;
          wS10T001TRecord.NEBIKIKIN             := 0;
          wS10T001TRecord.TESURYO               := 0;
          wS10T001TRecord.BIKO                  := iS10W001TRecord.BIKO;
          wS10T001TRecord.KESHIKOMIKIN          := wChoseikin * -1;
          wS10T001TRecord.SOSAIKIN              := 0;
          wS10T001TRecord.DATSKSMOTOKBN         := iS10W001TRecord.DATSKSMOTOKBN;
          wS10T001TRecord.SKYSHOOTPTARINASHIKBN := '1'; -- （出力なし）
          wS10T001TRecord.KAISHUTNSHACD         := iS10W001TRecord.KAISHUTNSHACD;
          wS10T001TRecord.TRKSDNPYNO            := NULL;
          InsertS10T001T(wS10T001TRecord);

          -- 請求残高テーブル更新（調整元）
          UpdateS07T001TByItem(iApkihoncd, iShopcd,
            wS07T001TSalesAdjustRecord(wIndex).SKYSHONO, NULL,
            wS07T001TSalesAdjustRecord(wIndex).TOKUISAKICD,
            wS07T001TSalesAdjustRecord(wIndex).KESHIKOMIKIN - (wChoseimotoSum + wChoseikin),
            wS07T001TSalesAdjustRecord(wIndex).KESHIKOMIZD + (wChoseimotoSum + wChoseikin));

          -- 入金消込履歴テーブル登録（調整元）
          wS10T004TRecord.NYKNDNPYNO            := iS10W001TRecord.DNPYNO;
          wS10T004TRecord.NYKNDNPYNOEDANO       := wEdaNo;
          wS10T004TRecord.SEQ                   := wSeq;
          wS10T004TRecord.NYKNKSHKMPRCSKBN      := '11'; -- （売上調整）
          wS10T004TRecord.SKYSAKICD             := iS10W001TRecord.SKYSAKICD;
          wS10T004TRecord.TOKUISAKICD           := wS07T001TSalesAdjustRecord(wIndex).TOKUISAKICD;
          wS10T004TRecord.NYKNDD                := iS10W001TRecord.NYKNDD;
          wS10T004TRecord.KSHKMDD               := vOperationDate;
          wS10T004TRecord.NYKNKIND              := iS10W001TRecord.NYKNKIND;
          wS10T004TRecord.KSHKMNYKNKIND         := iS10W001TRecord.NYKNKIND;
          wS10T004TRecord.KESHIKOMIKIN          := wChoseikin * -1;
          wS10T004TRecord.SHOKAISKYSHONO        := wS07T001TSalesAdjustRecord(wIndex).SKYSHONO;
          wS10T004TRecord.TRKSDNPYNO            := NULL;
          wS10T004TRecord.SKYPRCSJISSIKBN       := wS07T001TSalesAdjustRecord(wIndex).SKYPRCSJISSIKBN;
          InsertS10T004T(wS10T004TRecord);

          -- 入金取込得意先ワーク登録・更新（調整元）
          MergeS10W002T(iApkihoncd, iShopcd, iS10W001TRecord.SKYSAKICD,
            wS07T001TSalesAdjustRecord(wIndex).TOKUISAKICD, wChoseikin * -1);
        END IF;

        -- 調整金額合計設定（調整金額合計＋調整金額）
        wChoseikinTotal := wChoseikinTotal + wChoseikin;
        wChoseikinSum := wChoseikinSum + wChoseikin;
        wChoseimotoSum := wChoseimotoSum + wChoseikin;
        -- 調整残高（消込先）設定（調整残高－調整金額）
        wChoseizan := wChoseizan - wChoseikin;
        -- 調整残高（調整元）設定（調整残高＋調整金額）
        wChoseimotozan := wChoseimotozan + wChoseikin;
        -- 調整残高＞0の場合
        IF wChoseizan > 0 THEN
          wIndex := wIndex + 1; -- 請求残高テーブル（売上調整）レコードインデックス加算
          -- 調整残高（調整元）に次レコードの消込残高を設定
          wChoseimotozan := wS07T001TSalesAdjustRecord(wIndex).KESHIKOMIZD;
          -- 調整金額合計（調整元残高別）をリセット
          wChoseimotoSum := 0;
        END IF;
      END LOOP;
    END LOOP;

    -- 入金管理テーブル登録
    wS10T003TRecord.APKIHONCD        := iApkihoncd;
    wS10T003TRecord.SHOPCD           := iShopcd;
    wS10T003TRecord.NYKNDNPYNO       := iS10W001TRecord.DNPYNO;
    wS10T003TRecord.SKYSAKICD        := iS10W001TRecord.SKYSAKICD;
    wS10T003TRecord.NYKNDD           := iS10W001TRecord.NYKNDD;
    wS10T003TRecord.NYKNKIND         := '31'; -- （売上調整）
    wS10T003TRecord.NYKNKIN          := 0;
    wS10T003TRecord.NEBIKIKIN        := 0;
    wS10T003TRecord.TESURYO          := 0;
    wS10T003TRecord.KAIREKIN         := 0;
    wS10T003TRecord.URICHOSEIKIN     := wChoseikinTotal;
    wS10T003TRecord.TRKSDNPYNO       := NULL;
    wS10T003TRecord.KAIREKINKANRIFLG := '0';
    InsertS10T003T(wS10T003TRecord);

    -- 請求先マスタ更新（売上調整金額）
    UpdateS02M017TForUrichoseikin(iApkihoncd, iShopcd, iS10W001TRecord.SKYSAKICD, wChoseikinTotal);

    -- 入金取込ワーク更新
    UpdateS10W001T(iS10W001TRecord.APKIHONCD, iS10W001TRecord.SHOPCD,
      iS10W001TRecord.DATSKSMOTOKBN, iS10W001TRecord.DNPYNO, iS10W001TRecord.DNPYNO, '1', NULL);

    -- 入金消込ワーク削除
    DeleteS10W003T(iApkihoncd, iShopcd, iS10W001TRecord.DNPYNO);

  END SalesAdjustProcess;

  /******************************************************************************/
  /* プロシージャ名  ：取消処理                                                 */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                     */
  /*                 ：iShopcd               ショップコード                     */
  /*                 ：iS10W001TRecord       入金取込ワークレコード             */
  /*                 ：iTrksdnpyno           取消伝票番号                       */
  /*                 ：iSkyshootptkbn        請求書出力有無区分                 */
  /******************************************************************************/
  PROCEDURE CancelProcess(iApkihoncd      IN S07T001T.APKIHONCD%TYPE
                         ,iShopcd         IN S07T001T.SHOPCD%TYPE
                         ,iS10W001TRecord IN S10W001T%ROWTYPE
                         ,iTrksdnpyno     IN S10T001T.TRKSDNPYNO%TYPE
                         ,iSkyshootptkbn  IN S10T001T.SKYSHOOTPTARINASHIKBN%TYPE) IS
    ---------------------------
    -- カーソル定義
    ---------------------------
    -- 入金明細テーブルカーソル
    CURSOR curS10T001T IS
      SELECT
        *
      FROM
        S10T001T
      WHERE
            APKIHONCD = iApkihoncd
        AND SHOPCD    = iShopcd
        AND DNPYNO    = iTrksdnpyno
      ORDER BY
        NYKNDNPYNOEDANO
      ;

    -- 入金消込履歴テーブルカーソル
    CURSOR curS10T004T(pNykndnpyno      S10T004T.NYKNDNPYNO%TYPE
                      ,pNykndnpynoedano S10T004T.NYKNDNPYNOEDANO%TYPE) IS
      SELECT
          SHOKAISKYSHONO
        , NYKNKSHKMPRCSKBN
        , KESHIKOMIKIN
        , SKYSAKICD
        , TOKUISAKICD
        , KSHKMNYKNKIND
        , SKYPRCSJISSIKBN
      FROM
        S10T004T
      WHERE
            APKIHONCD       = iApkihoncd
        AND SHOPCD          = iShopcd
        AND NYKNDNPYNO      = pNykndnpyno
        AND NYKNDNPYNOEDANO = pNykndnpynoedano
        AND YUKOKBN         = '1'
      ORDER BY
        SEQ
      ;

    -- 入金明細テーブル（入金金額、過入金額集計値）カーソル
    CURSOR curS10T001TGroup IS
      SELECT
          TOKUISAKICD
        , SUM(NYKNKIN + NEBIKIKIN + TESURYO)                            AS NYKNKIN
        , SUM(NYKNKIN + NEBIKIKIN + TESURYO - KESHIKOMIKIN - SOSAIKIN)  AS KAIREKIN
      FROM
        S10T001T
      WHERE
            APKIHONCD = iS10W001TRecord.APKIHONCD
        AND SHOPCD    = iS10W001TRecord.SHOPCD
        AND DNPYNO    = iS10W001TRecord.DNPYNO
      GROUP BY
        TOKUISAKICD
      ;
    ---------------------------
    -- 変数定義
    ---------------------------
    wKairekin             S10T003T.KAIREKIN%TYPE := 0;            -- 過入金額
    wKeshikomikin         S10T001T.KESHIKOMIKIN%TYPE;             -- 消込金額
    wKeshikomikinSum      NUMBER;                                 -- 消込金額合計
    wSeq                  S10T004T.SEQ%TYPE := 0;                 -- SEQ
    wNykndnpynoedano      S10T001T.NYKNDNPYNOEDANO%TYPE;          -- 入金伝票番号枝番号
    wNyknkin              S10T001T.NYKNKIN%TYPE;                  -- 入金金額
    wKairekinkanriflg     S10T003T.KAIREKINKANRIFLG%TYPE := '0';  -- 過入金額管理フラグ
    ---------------------------
    -- レコード定義
    ---------------------------
    TYPE typeS10T003T IS TABLE OF S10T003T%ROWTYPE INDEX BY BINARY_INTEGER;
    wS10T003TList         typeS10T003T;                           -- 入金管理テーブルレコード（配列）
    wS10T001TRecord       curS10T001T%ROWTYPE;                    -- 入金明細テーブルカーソルレコード
    wS10T004TRecord       curS10T004T%ROWTYPE;                    -- 入金消込履歴テーブルカーソルレコード
    wS10T001TGroupRecord  curS10T001TGroup%ROWTYPE;               -- 入金明細テーブル（入金金額、過入金額集計値）カーソルレコード
    wS07T001TRecord       S07T001T%ROWTYPE;                       -- 請求残高テーブルレコード
    wS10T001TNewRecord    S10T001T%ROWTYPE;                       -- 入金明細テーブルレコード（登録用）
    wS10T003TNewRecord    S10T003T%ROWTYPE;                       -- 入金管理テーブルレコード（登録用）
    wS10T004TNewRecord    S10T004T%ROWTYPE;                       -- 入金消込履歴テーブルレコード（登録用）
  BEGIN
    -- 入金管理テーブルデータ取得
    SELECT
      *
    BULK COLLECT INTO
      wS10T003TList
    FROM
      S10T003T
    WHERE
          APKIHONCD   = iApkihoncd
      AND SHOPCD      = iShopcd
      AND NYKNDNPYNO  = iTrksdnpyno
      AND YUKOKBN     = '1'
    ;

    -- 入金管理テーブル更新
    UpdateS10T003T(iApkihoncd, iShopcd, iTrksdnpyno, '0', wKairekin, iS10W001TRecord.DNPYNO);

    -- 入金伝票番号枝番号最大値取得
    wNykndnpynoedano := GetMaxNykndnpynoedano(iApkihoncd, iShopcd, iTrksdnpyno);

    -- 入金明細テーブルカーソルループ
    FOR wS10T001TRecord IN curS10T001T LOOP
      -- 入金明細テーブル更新
      UpdateS10T001TForTrksdnpyno(iApkihoncd, iShopcd, wS10T001TRecord.DNPYNO,
        wS10T001TRecord.NYKNDNPYNOEDANO, iSkyshootptkbn, iS10W001TRecord.DNPYNO);

      -- 設定値初期化（消込金額、消込金額合計、入金金額）
      wKeshikomikin := 0;
      wKeshikomikinSum := 0;
      wNyknkin := wS10T001TRecord.NYKNKIN * -1;

      -- 入金消込履歴テーブルカーソルループ
      FOR wS10T004TRecord IN curS10T004T(wS10T001TRecord.DNPYNO, wS10T001TRecord.NYKNDNPYNOEDANO) LOOP
        IF wS10T004TRecord.SHOKAISKYSHONO IS NULL THEN
          IF wS10T001TRecord.SOSAIKIN != 0 THEN
            -- 各種設定値更新（入金金額、入金伝票番号枝番号、過入金額管理フラグ）
            wNyknkin := wS10T001TRecord.SOSAIKIN * -1;
            wNykndnpynoedano := wNykndnpynoedano + 1;
            wKairekinkanriflg := '1';

            -- 入金明細テーブル登録（振替明細）
            wS10T001TNewRecord.APKIHONCD             := iS10W001TRecord.APKIHONCD;
            wS10T001TNewRecord.SHOPCD                := iS10W001TRecord.SHOPCD;
            wS10T001TNewRecord.DNPYNO                := iS10W001TRecord.DNPYNO;
            wS10T001TNewRecord.NYKNDNPYNOEDANO       := wNykndnpynoedano;
            wS10T001TNewRecord.KAIREKINKBN           := '1';
            wS10T001TNewRecord.SKYSAKICD             := iS10W001TRecord.SKYSAKICD;
            wS10T001TNewRecord.TOKUISAKICD           := wS10T001TRecord.TOKUISAKICD;
            wS10T001TNewRecord.NYKNDD                := wS10T001TRecord.NYKNDD;
            wS10T001TNewRecord.NYKNKIND              := wS10T001TRecord.NYKNKIND;
            wS10T001TNewRecord.NYKNKIN               := wS10T001TRecord.SOSAIKIN;
            wS10T001TNewRecord.NEBIKIKIN             := 0;
            wS10T001TNewRecord.TESURYO               := 0;
            wS10T001TNewRecord.BIKO                  := wS10T001TRecord.BIKO;
            wS10T001TNewRecord.KESHIKOMIKIN          := 0;
            wS10T001TNewRecord.SOSAIKIN              := 0;
            wS10T001TNewRecord.DATSKSMOTOKBN         := wS10T001TRecord.DATSKSMOTOKBN;
            wS10T001TNewRecord.SKYSHOOTPTARINASHIKBN := iSkyshootptkbn;
            wS10T001TNewRecord.KAISHUTNSHACD         := wS10T001TRecord.KAISHUTNSHACD;
            wS10T001TNewRecord.TRKSDNPYNO            := iTrksdnpyno;
            InsertS10T001T(wS10T001TNewRecord);
          ELSE
            -- 各種設定値更新（入金金額、消込合計、過入金額管理フラグ）
            wNyknkin := wS10T001TRecord.NYKNKIN * -1;
            wKeshikomikinSum := 0;
            wKairekinkanriflg := '1';
          END IF;
        ELSE
          -- 請求残高テーブルデータ取得
          wS07T001TRecord := GetS07T001TRecord(iApkihoncd, iShopcd,
            wS10T004TRecord.SHOKAISKYSHONO, wS10T004TRecord.SKYSAKICD, wS10T004TRecord.TOKUISAKICD);

          -- 消込戻しチェック
          IF wS10T001TRecord.NYKNKIND = '31' THEN
            IF ABS(wS10T004TRecord.KESHIKOMIKIN) > ABS(wS07T001TRecord.KESHIKOMIKIN) THEN
              RAISE_APPLICATION_ERROR(-20014, 'この伝票で消込を行った残高が他の伝票で戻されています。');
            END IF;
          ELSE
            IF wS10T004TRecord.KESHIKOMIKIN >= 0 THEN
              IF wS10T004TRecord.KESHIKOMIKIN > wS07T001TRecord.KESHIKOMIKIN THEN
                RAISE_APPLICATION_ERROR(-20014, 'この伝票で消込を行った残高が他の伝票で戻されています。');
              END IF;
            ELSE
              IF wS10T004TRecord.KESHIKOMIKIN * -1 > wS07T001TRecord.KESHIKOMIZD THEN
                RAISE_APPLICATION_ERROR(-20014, 'この伝票で消込を行った残高が他の伝票で戻されています。');
              END IF;
            END IF;
          END IF;

          -- 消込金額設定（入金消込履歴.消込金額）
          wKeshikomikin := wS10T004TRecord.KESHIKOMIKIN;
          -- 消込金額合計設定（消込金額合計＋消込金額）
          wKeshikomikinSum := wKeshikomikinSum + wKeshikomikin;

          -- 請求残高テーブル更新
          UpdateS07T001TByItem(iApkihoncd, iShopcd, wS10T004TRecord.SHOKAISKYSHONO,
            wS10T004TRecord.SKYSAKICD, wS10T004TRecord.TOKUISAKICD,
            wS07T001TRecord.KESHIKOMIKIN - wKeshikomikin,
            wS07T001TRecord.KESHIKOMIZD + wKeshikomikin);
        END IF;

        -- 消込金額≠0の場合
        IF wKeshikomikin != 0 THEN
          -- SEQ加算
          wSeq := wSeq + 1;

          -- 入金消込履歴テーブル登録
          wS10T004TNewRecord.NYKNDNPYNO         := iS10W001TRecord.DNPYNO;
          wS10T004TNewRecord.NYKNDNPYNOEDANO    := wS10T001TRecord.NYKNDNPYNOEDANO;
          wS10T004TNewRecord.SEQ                := wSeq;
          wS10T004TNewRecord.NYKNKSHKMPRCSKBN   := '12'; -- （入金取消）
          wS10T004TNewRecord.SKYSAKICD          := iS10W001TRecord.SKYSAKICD;
          wS10T004TNewRecord.TOKUISAKICD        := wS10T001TRecord.TOKUISAKICD;
          wS10T004TNewRecord.NYKNDD             := wS10T001TRecord.NYKNDD;
          wS10T004TNewRecord.KSHKMDD            := vOperationDate;
          wS10T004TNewRecord.NYKNKIND           := wS10T001TRecord.NYKNKIND;
          wS10T004TNewRecord.KSHKMNYKNKIND      := wS10T004TRecord.KSHKMNYKNKIND;
          wS10T004TNewRecord.KESHIKOMIKIN       := wKeshikomikin * -1;
          wS10T004TNewRecord.SHOKAISKYSHONO     := wS10T004TRecord.SHOKAISKYSHONO;
          wS10T004TNewRecord.TRKSDNPYNO         := iTrksdnpyno;
          wS10T004TNewRecord.SKYPRCSJISSIKBN    := wS10T004TRecord.SKYPRCSJISSIKBN;
          InsertS10T004T(wS10T004TNewRecord);
        END IF;
      END LOOP;

      -- 入金明細テーブル登録（新伝票）
      wS10T001TNewRecord.APKIHONCD             := iS10W001TRecord.APKIHONCD;
      wS10T001TNewRecord.SHOPCD                := iS10W001TRecord.SHOPCD;
      wS10T001TNewRecord.DNPYNO                := iS10W001TRecord.DNPYNO;
      wS10T001TNewRecord.NYKNDNPYNOEDANO       := wS10T001TRecord.NYKNDNPYNOEDANO;
      wS10T001TNewRecord.KAIREKINKBN           := wS10T001TRecord.KAIREKINKBN;
      wS10T001TNewRecord.SKYSAKICD             := iS10W001TRecord.SKYSAKICD;
      wS10T001TNewRecord.TOKUISAKICD           := wS10T001TRecord.TOKUISAKICD;
      wS10T001TNewRecord.NYKNDD                := wS10T001TRecord.NYKNDD;
      wS10T001TNewRecord.NYKNKIND              := wS10T001TRecord.NYKNKIND;
      wS10T001TNewRecord.NYKNKIN               := wNyknkin;
      wS10T001TNewRecord.NEBIKIKIN             := wS10T001TRecord.NEBIKIKIN * -1;
      wS10T001TNewRecord.TESURYO               := wS10T001TRecord.TESURYO * -1;
      wS10T001TNewRecord.BIKO                  := wS10T001TRecord.BIKO;
      wS10T001TNewRecord.KESHIKOMIKIN          := wKeshikomikinSum * -1;
      wS10T001TNewRecord.SOSAIKIN              := wS10T001TRecord.SOSAIKIN * -1;
      wS10T001TNewRecord.DATSKSMOTOKBN         := wS10T001TRecord.DATSKSMOTOKBN;
      wS10T001TNewRecord.SKYSHOOTPTARINASHIKBN := iSkyshootptkbn;
      wS10T001TNewRecord.KAISHUTNSHACD         := wS10T001TRecord.KAISHUTNSHACD;
      wS10T001TNewRecord.TRKSDNPYNO            := iTrksdnpyno;
      InsertS10T001T(wS10T001TNewRecord);
    END LOOP;

    -- 入金明細テーブル（入金金額、過入金額集計値）カーソルループ
    FOR wS10T001TGroupRecord IN curS10T001TGroup LOOP
      -- 過入金額設定（過入金額＋入金明細.過入金額）
      wKairekin := wKairekin + wS10T001TGroupRecord.KAIREKIN;
      -- 入金取込得意先ワーク登録・更新
      MergeS10W002T(iS10W001TRecord.APKIHONCD, iS10W001TRecord.SHOPCD, iS10W001TRecord.SKYSAKICD
        , wS10T001TGroupRecord.TOKUISAKICD, wS10T001TGroupRecord.NYKNKIN);
    END LOOP;

    -- 入金管理テーブル登録
    wS10T003TNewRecord.APKIHONCD        := iS10W001TRecord.APKIHONCD;
    wS10T003TNewRecord.SHOPCD           := iS10W001TRecord.SHOPCD;
    wS10T003TNewRecord.NYKNDNPYNO       := iS10W001TRecord.DNPYNO;
    wS10T003TNewRecord.SKYSAKICD        := iS10W001TRecord.SKYSAKICD;
    wS10T003TNewRecord.NYKNDD           := wS10T003TList(wS10T003TList.FIRST).NYKNDD;
    wS10T003TNewRecord.NYKNKIND         := wS10T003TList(wS10T003TList.FIRST).NYKNKIND;
    wS10T003TNewRecord.NYKNKIN          := wS10T003TList(wS10T003TList.FIRST).NYKNKIN * -1;
    wS10T003TNewRecord.NEBIKIKIN        := wS10T003TList(wS10T003TList.FIRST).NEBIKIKIN * -1;
    wS10T003TNewRecord.TESURYO          := wS10T003TList(wS10T003TList.FIRST).TESURYO * -1;
    wS10T003TNewRecord.KAIREKIN         := wKairekin;
    wS10T003TNewRecord.URICHOSEIKIN     := wS10T003TList(wS10T003TList.FIRST).URICHOSEIKIN * -1;
    wS10T003TNewRecord.TRKSDNPYNO       := iTrksdnpyno;
    wS10T003TNewRecord.KAIREKINKANRIFLG := wKairekinkanriflg;
    InsertS10T003T(wS10T003TNewRecord);

    -- 請求先マスタ更新（過入金額、売上調整金額）
    UpdateS02M017T(iApkihoncd, iShopcd, iS10W001TRecord.SKYSAKICD,
      wKairekin, wS10T003TList(wS10T003TList.FIRST).URICHOSEIKIN * -1);

    -- 入金取込ワーク更新
    UpdateS10W001T(iApkihoncd, iShopcd, iS10W001TRecord.DATSKSMOTOKBN,
      iS10W001TRecord.DNPYNO, iS10W001TRecord.DNPYNO, '1', NULL);

    -- 入金消込ワーク削除
    DeleteS10W003T(iApkihoncd, iShopcd, iS10W001TRecord.DNPYNO);

  END CancelProcess;

  /******************************************************************************/
  /* プロシージャ名  ：得意先更新処理                                           */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                     */
  /*                 ：iShopcd               ショップコード                     */
  /*                 ：iSkysakicd            請求先コード                       */
  /******************************************************************************/
  PROCEDURE UpdateTokuisaki(iApkihoncd IN S10W002T.APKIHONCD%TYPE
                           ,iShopcd    IN S10W002T.SHOPCD%TYPE
                           ,iSkysakicd IN S10W002T.SKYSAKICD%TYPE) IS
    ---------------------------
    -- カーソル定義
    ---------------------------
    -- 得意先実績テーブル更新用カーソル
    CURSOR curS02M050TUpdate IS
      SELECT
          NMST.APKIHONCD
        , NMST.SHOPCD
        , NMST.TOKUISAKICD
        , NMST.NYKNKIN
      FROM
        S10W002T NMST
        INNER JOIN S02M050T TJST ON (
              TJST.APKIHONCD    = NMST.APKIHONCD
          AND TJST.SHOPCD       = NMST.SHOPCD
          AND TJST.TOKUISAKICD  = NMST.TOKUISAKICD
          AND TJST.YUKOKBN      = '1'
        )
      WHERE
            NMST.APKIHONCD      = iApkihoncd
        AND NMST.SHOPCD         = iShopcd
        AND NMST.PRCSTNSHACD    = vTantoshacd
        AND NMST.SKYSAKICD      = iSkysakicd
      ;

    -- 得意先マスタ（現在残高0）カーソル
    CURSOR curS02M013TByGenzaizdZero IS
      SELECT
          TKSM.APKIHONCD
        , TKSM.SHOPCD
        , TKSM.TOKUISAKICD
        , TKSM.JOTAISTS
      FROM
        S02M013T TKSM
        INNER JOIN S10W002T NMST ON (
              NMST.APKIHONCD    = TKSM.APKIHONCD
          AND NMST.SHOPCD       = TKSM.SHOPCD
          AND NMST.PRCSTNSHACD  = vTantoshacd
          AND NMST.SKYSAKICD    = iSkysakicd
          AND NMST.TOKUISAKICD  = TKSM.TOKUISAKICD
          AND NMST.GENZAIZD     = 0
        )
      WHERE
            TKSM.APKIHONCD      = iApkihoncd
        AND TKSM.SHOPCD         = iShopcd
        AND TKSM.TOKUISAKIJOTAI IN ('4', '6')
        AND TKSM.JOTAISTS       IN ('05', '09')
        AND TKSM.YUKOKBN        = '1'
      ;

    -- 得意先マスタ（現在残高0以外）カーソル
    CURSOR curS02M013TByGenzaizdNotZero IS
      SELECT
          TKSM.APKIHONCD
        , TKSM.SHOPCD
        , TKSM.TOKUISAKICD
        , TKSM.JOTAISTS
      FROM
        S02M013T TKSM
        INNER JOIN S10W002T NMST ON (
              NMST.APKIHONCD    = TKSM.APKIHONCD
          AND NMST.SHOPCD       = TKSM.SHOPCD
          AND NMST.PRCSTNSHACD  = vTantoshacd
          AND NMST.SKYSAKICD    = iSkysakicd
          AND NMST.TOKUISAKICD  = TKSM.TOKUISAKICD
          AND NMST.GENZAIZD     <> 0
        )
      WHERE
            TKSM.APKIHONCD      = iApkihoncd
        AND TKSM.SHOPCD         = iShopcd
        AND TKSM.TOKUISAKIJOTAI IN ('4', '6')
        AND TKSM.JOTAISTS       IN ('06', '10')
        AND TKSM.YUKOKBN        = '1'
      ;
    ---------------------------
    -- レコード定義
    ---------------------------
    TYPE typeS02M050TTokuisakicd IS TABLE OF S02M050T.TOKUISAKICD%TYPE INDEX BY BINARY_INTEGER;
    TYPE typeS02M013TTokuisakicd IS TABLE OF S02M013T.TOKUISAKICD%TYPE INDEX BY BINARY_INTEGER;
    wS02M050TTokuisakicd    typeS02M050TTokuisakicd;              -- 得意先実績テーブル.得意先コードレコード（配列）
    wS02M013TTokuisakicd    typeS02M013TTokuisakicd;              -- 得意先マスタ.得意先コードレコード（配列）
    wS02M050TUpdateRecord   curS02M050TUpdate%ROWTYPE;            -- 得意先実績テーブルカーソルレコード
    wTKMGenzaizd0Record     curS02M013TByGenzaizdZero%ROWTYPE;    -- 得意先マスタ（現在残高0）カーソルレコード
    wTKMGenzaizdNot0Record  curS02M013TByGenzaizdNotZero%ROWTYPE; -- 得意先マスタ（現在残高0以外）カーソルレコード
  BEGIN
    -- 対象得意先実績テーブルロック（得意先実績テーブル、入金取込得意先ワーク）
    SELECT
      TJST.TOKUISAKICD
    BULK COLLECT INTO
      wS02M050TTokuisakicd
    FROM
      S02M050T TJST
      INNER JOIN S10W002T NMST ON (
            NMST.APKIHONCD    = TJST.APKIHONCD
        AND NMST.SHOPCD       = TJST.SHOPCD
        AND NMST.TOKUISAKICD  = TJST.TOKUISAKICD
        AND NMST.APKIHONCD    = iApkihoncd
        AND NMST.SHOPCD       = iShopcd
        AND NMST.SKYSAKICD    = iSkysakicd
        AND NMST.PRCSTNSHACD  = vTantoshacd
      )
      FOR UPDATE;

    -- 対象得意先マスタロック（得意先マスタ、入金取込得意先ワーク）
    SELECT
      TKSM.TOKUISAKICD
    BULK COLLECT INTO
      wS02M013TTokuisakicd
    FROM
      S02M013T TKSM
      INNER JOIN S10W002T NMST ON (
            NMST.APKIHONCD   = TKSM.APKIHONCD
        AND NMST.SHOPCD      = TKSM.SHOPCD
        AND NMST.TOKUISAKICD = TKSM.TOKUISAKICD
        AND NMST.APKIHONCD   = iApkihoncd
        AND NMST.SHOPCD      = iShopcd
        AND NMST.PRCSTNSHACD = vTantoshacd
        AND NMST.SKYSAKICD   = iSkysakicd
      )
      FOR UPDATE;

    -- 得意先実績テーブルデータ更新
    FOR wS02M050TUpdateRecord IN curS02M050TUpdate LOOP
      UPDATE S02M050T
      SET
          GENZAIZD        = GENZAIZD - wS02M050TUpdateRecord.NYKNKIN
        , KOSHAAPKIHONCD  = vUserApkihoncd
        , KOSHASHOPCD     = vUserShopcd
        , KOSHACD         = vTantoshacd
        , KSHNSYSTMYMD    = vSystemDate
        , KSHNSYSTMJIKAN  = vSystemTime
        , KSHNUNYOYMD     = vOperationDate
        , KSHNIPADRSE     = vIpAddress
        , KSHNPGMID       = vProgramId
      WHERE
            APKIHONCD     = wS02M050TUpdateRecord.APKIHONCD
        AND SHOPCD        = wS02M050TUpdateRecord.SHOPCD
        AND TOKUISAKICD   = wS02M050TUpdateRecord.TOKUISAKICD
      ;
    END LOOP;

    -- 入金取込得意先ワークデータ更新
    UPDATE S10W002T NTTW
    SET
        NTTW.GENZAIZD       = (
                                SELECT
                                  NVL(GENZAIZD, 0)
                                FROM
                                  S02M050T TKSM
                                WHERE
                                      TKSM.APKIHONCD   = NTTW.APKIHONCD
                                  AND TKSM.SHOPCD      = NTTW.SHOPCD
                                  AND TKSM.TOKUISAKICD = NTTW.TOKUISAKICD
                                  AND TKSM.YUKOKBN     = '1')
      , NTTW.KOSHAAPKIHONCD  = vUserApkihoncd
      , NTTW.KOSHASHOPCD     = vUserShopcd
      , NTTW.KOSHACD         = vTantoshacd
      , NTTW.KSHNSYSTMYMD    = vSystemDate
      , NTTW.KSHNSYSTMJIKAN  = vSystemTime
      , NTTW.KSHNUNYOYMD     = vOperationDate
      , NTTW.KSHNIPADRSE     = vIpAddress
      , NTTW.KSHNPGMID       = vProgramId
    WHERE
          NTTW.APKIHONCD     = iApkihoncd
      AND NTTW.SHOPCD        = iShopcd
      AND NTTW.SKYSAKICD     = iSkysakicd
      AND NTTW.PRCSTNSHACD   = vTantoshacd
    ;

    -- 得意先マスタ（現在残高0）データ更新
    FOR wTKMGenzaizd0Record IN curS02M013TByGenzaizdZero LOOP
      UpdateS02M013T(wTKMGenzaizd0Record.APKIHONCD,
        wTKMGenzaizd0Record.SHOPCD,
        wTKMGenzaizd0Record.TOKUISAKICD,
        (CASE WHEN wTKMGenzaizd0Record.JOTAISTS = '05' THEN '06' ELSE '10' END));
    END LOOP;

    -- 得意先マスタ（現在残高0以外）データ更新
    FOR wTKMGenzaizdNot0Record IN curS02M013TByGenzaizdNotZero LOOP
      UpdateS02M013T(wTKMGenzaizdNot0Record.APKIHONCD,
        wTKMGenzaizdNot0Record.SHOPCD,
        wTKMGenzaizdNot0Record.TOKUISAKICD,
        (CASE WHEN wTKMGenzaizdNot0Record.JOTAISTS = '06' THEN '05' ELSE '09' END));
    END LOOP;

  END UpdateTokuisaki;

  /******************************************************************************/
  /* プロシージャ名  ：入金消込処理                                             */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                     */
  /*                 ：iShopcd               ショップコード                     */
  /*                 ：iUserApkihoncd        担当者ＡＰ基本コード               */
  /*                 ：iUserShopcd           担当者ショップコード               */
  /*                 ：iTantoshacd           担当者コード                       */
  /*                 ：iIpAddress            ＩＰアドレス                       */
  /*                 ：iDatsksmotokbn        データ作成元区分                   */
  /*                 ：iDenpyonofrom         入金伝票番号開始値                 */
  /*                 ：iDenpyonoto           入金伝票番号終了値                 */
  /******************************************************************************/
  PROCEDURE NyukinKeshikomi(iApkihoncd     IN VARCHAR2
                           ,iShopcd        IN VARCHAR2
                           ,iUserApkihoncd IN VARCHAR2
                           ,iUserShopcd    IN VARCHAR2
                           ,iTantoshacd    IN VARCHAR2
                           ,iIpAddress     IN VARCHAR2
                           ,iDatsksmotokbn IN VARCHAR2
                           ,iDenpyonofrom  IN VARCHAR2
                           ,iDenpyonoto    IN VARCHAR2) IS
    ---------------------------
    -- レコード定義
    ---------------------------
    wS10W001TRecord S10W001T%ROWTYPE; -- 入金管理テーブルレコード
  BEGIN
    -- 基本情報設定
    SetBasicInfo(iApkihoncd, iShopcd, iDatsksmotokbn, iUserApkihoncd,
      iUserShopcd, iTantoshacd, iIpAddress, iDenpyonofrom, iDenpyonoto);

    -- 入金取込ワークデータ取得
    wS10W001TRecord := GetS10W001T(iApkihoncd, iShopcd, iDatsksmotokbn, iDenpyonofrom);

    -- 請求先マスタ更新ロック
    ForUpdateS02M017T(iApkihoncd, iShopcd, wS10W001TRecord.SKYSAKICD);

    -- 入金取込得意先ワーク削除
    DeleteS10W002T(iApkihoncd, iShopcd, iTantoshacd, wS10W001TRecord.SKYSAKICD);

    -- 消込処理
    ClearingProcess(iApkihoncd, iShopcd, wS10W001TRecord.SKYSAKICD,
      iDatsksmotokbn, iDenpyonofrom, iDenpyonoto);

    -- 滞留更新処理
     D1504B01.Tairyukousin(iApkihoncd, iShopcd, iUserApkihoncd, iUserShopcd,
       iTantoshacd, iIpAddress, vSystemDate, vSystemTime, wS10W001TRecord.NYKNDD,
       '2', 0, wS10W001TRecord.SKYSAKICD, NULL);

    -- 得意先更新処理
    UpdateTokuisaki(iApkihoncd, iShopcd, wS10W001TRecord.SKYSAKICD);

    -- 入金取込ワーク更新
    UpdateS10W001T(wS10W001TRecord.APKIHONCD, wS10W001TRecord.SHOPCD,
      wS10W001TRecord.DATSKSMOTOKBN, wS10W001TRecord.DNPYNO, wS10W001TRecord.DNPYNO, '1', NULL);

    -- 入金消込ワーク削除
    DeleteS10W003T(iApkihoncd, iShopcd, iDenpyonofrom);

    -- 入金取込得意先ワーク削除
    DeleteS10W002T(iApkihoncd, iShopcd, iTantoshacd, wS10W001TRecord.SKYSAKICD);

  END NyukinKeshikomi;

  /******************************************************************************/
  /* プロシージャ名  ：一括入金消込処理                                         */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                     */
  /*                 ：iShopcd               ショップコード                     */
  /*                 ：iUserApkihoncd        担当者ＡＰ基本コード               */
  /*                 ：iUserShopcd           担当者ショップコード               */
  /*                 ：iTantoshacd           担当者コード                       */
  /*                 ：iIpAddress            ＩＰアドレス                       */
  /*                 ：iDatsksmotokbn        データ作成元区分                   */
  /*                 ：iDenpyonofrom         入金伝票番号開始値                 */
  /*                 ：iDenpyonoto           入金伝票番号終了値                 */
  /******************************************************************************/
  PROCEDURE IkatsuNyukin2(iApkihoncd      IN VARCHAR2
                         ,iShopcd         IN VARCHAR2
                         ,iUserApkihoncd  IN VARCHAR2
                         ,iUserShopcd     IN VARCHAR2
                         ,iTantoshacd     IN VARCHAR2
                         ,iIpAddress      IN VARCHAR2
                         ,iDatsksmotokbn  IN VARCHAR2
                         ,iDenpyonofrom   IN VARCHAR2
                         ,iDenpyonoto     IN VARCHAR2) IS
    ---------------------------
    -- カーソル定義
    ---------------------------
    -- 入金取込ワークカーソル
    CURSOR curS10W001T IS
      SELECT
        SKYSAKICD, MAX(NYKNDD) AS NYKNDD
      FROM
        S10W001T
      WHERE
            APKIHONCD     = iApkihoncd
        AND SHOPCD        = iShopcd
        AND DATSKSMOTOKBN = iDatsksmotokbn
        AND NYKNDATSKSFLG = '0'
        AND KSHNUNYOYMD   <= vOperationDate
        AND DNPYNO BETWEEN iDenpyonofrom AND iDenpyonoto
      GROUP BY
        SKYSAKICD
      ORDER BY
        SKYSAKICD
      ;
    ---------------------------
    -- レコード定義
    ---------------------------
    wS10W001TRecord curS10W001T%ROWTYPE; -- 入金取込ワークカーソルレコード
  BEGIN
    -- 基本情報設定
    SetBasicInfo(iApkihoncd, iShopcd, iDatsksmotokbn, iUserApkihoncd,
      iUserShopcd, iTantoshacd, iIpAddress, iDenpyonofrom, iDenpyonoto);

    -- 入金取込ワークカーソルループ
    FOR wS10W001TRecord IN curS10W001T LOOP
      -- 請求先マスタ更新ロック
      ForUpdateS02M017T(iApkihoncd, iShopcd, wS10W001TRecord.SKYSAKICD);

      -- 入金取込得意先ワーク削除
      DeleteS10W002T(iApkihoncd, iShopcd, iTantoshacd, wS10W001TRecord.SKYSAKICD);

      -- 消込処理
      ClearingProcess(iApkihoncd, iShopcd, wS10W001TRecord.SKYSAKICD,
        iDatsksmotokbn, iDenpyonofrom, iDenpyonoto);

      -- 滞留更新処理
       D1504B01.Tairyukousin(iApkihoncd, iShopcd, iUserApkihoncd, iUserShopcd,
         iTantoshacd, iIpAddress, vSystemDate, vSystemTime, wS10W001TRecord.NYKNDD,
         '4', 0, wS10W001TRecord.SKYSAKICD, NULL);

      -- 得意先更新処理
      UpdateTokuisaki(iApkihoncd, iShopcd, wS10W001TRecord.SKYSAKICD);

      -- 入金取込ワーク更新
      UpdateS10W001T(iApkihoncd, iShopcd, iDatsksmotokbn,
        iDenpyonofrom, iDenpyonoto, '1', wS10W001TRecord.SKYSAKICD);

      -- 入金取込得意先ワーク削除
      DeleteS10W002T(iApkihoncd, iShopcd, iTantoshacd, wS10W001TRecord.SKYSAKICD);

      -- トランザクションコミット
      COMMIT;
    END LOOP;
  END IkatsuNyukin2;

  /******************************************************************************/
  /* プロシージャ名  ：一括入金消込処理（調整）                                 */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                     */
  /*                 ：iShopcd               ショップコード                     */
  /*                 ：iUserApkihoncd        担当者ＡＰ基本コード               */
  /*                 ：iUserShopcd           担当者ショップコード               */
  /*                 ：iTantoshacd           担当者コード                       */
  /*                 ：iIpAddress            ＩＰアドレス                       */
  /*                 ：iDatsksmotokbn        データ作成元区分                   */
  /*                 ：iDenpyonofrom         入金伝票番号開始値                 */
  /*                 ：iDenpyonoto           入金伝票番号終了値                 */
  /******************************************************************************/
  PROCEDURE NyukinUriChosei(iApkihoncd      IN VARCHAR2
                           ,iShopcd         IN VARCHAR2
                           ,iUserApkihoncd  IN VARCHAR2
                           ,iUserShopcd     IN VARCHAR2
                           ,iTantoshacd     IN VARCHAR2
                           ,iIpAddress      IN VARCHAR2
                           ,iDatsksmotokbn  IN VARCHAR2
                           ,iDenpyonofrom   IN VARCHAR2
                           ,iDenpyonoto     IN VARCHAR2) IS
    ---------------------------
    -- レコード定義
    ---------------------------
    wS10W001TRecord S10W001T%ROWTYPE; -- 入金取込ワークレコード
  BEGIN
    -- 基本情報設定
    SetBasicInfo(iApkihoncd, iShopcd, iDatsksmotokbn, iUserApkihoncd,
      iUserShopcd, iTantoshacd, iIpAddress, iDenpyonofrom, iDenpyonoto);

    -- 入金取込ワークデータ取得
    wS10W001TRecord := GetS10W001T(iApkihoncd, iShopcd, iDatsksmotokbn, iDenpyonofrom);

    -- 請求先マスタ更新ロック
    ForUpdateS02M017T(iApkihoncd, iShopcd, wS10W001TRecord.SKYSAKICD);

    -- 売上調整処理
    SalesAdjustProcess(iApkihoncd, iShopcd, wS10W001TRecord);

    -- 滞留更新処理
     D1504B01.Tairyukousin(iApkihoncd, iShopcd, iUserApkihoncd, iUserShopcd,
       iTantoshacd, iIpAddress, vSystemDate, vSystemTime, wS10W001TRecord.NYKNDD,
       '2', 0, wS10W001TRecord.SKYSAKICD, NULL);

    -- 得意先更新処理
    UpdateTokuisaki(iApkihoncd, iShopcd, wS10W001TRecord.SKYSAKICD);

    -- 入金取込得意先ワーク削除
    DeleteS10W002T(iApkihoncd, iShopcd, iTantoshacd, wS10W001TRecord.SKYSAKICD);

  END NyukinUriChosei;

  /******************************************************************************/
  /* プロシージャ名  ：一括入金消込処理（取消）                                 */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                     */
  /*                 ：iShopcd               ショップコード                     */
  /*                 ：iUserApkihoncd        担当者ＡＰ基本コード               */
  /*                 ：iUserShopcd           担当者ショップコード               */
  /*                 ：iTantoshacd           担当者コード                       */
  /*                 ：iIpAddress            ＩＰアドレス                       */
  /*                 ：iDatsksmotokbn        データ作成元区分                   */
  /*                 ：iDenpyonofrom         入金伝票番号開始値                 */
  /*                 ：iDenpyonoto           入金伝票番号終了値                 */
  /*                 ：iSkyshootptkbn        請求書出力有無区分                 */
  /******************************************************************************/
  PROCEDURE NyukinTorikeshi(iApkihoncd      IN VARCHAR2
                           ,iShopcd         IN VARCHAR2
                           ,iUserApkihoncd  IN VARCHAR2
                           ,iUserShopcd     IN VARCHAR2
                           ,iTantoshacd     IN VARCHAR2
                           ,iIpAddress      IN VARCHAR2
                           ,iDatsksmotokbn  IN VARCHAR2
                           ,iDenpyonofrom   IN VARCHAR2
                           ,iDenpyonoto     IN VARCHAR2
                           ,iSkyshootptkbn  IN VARCHAR2) IS
    ---------------------------
    -- レコード定義
    ---------------------------
    wS10W001TRecord S10W001T%ROWTYPE; -- 入金取込ワークレコード
  BEGIN
    -- 基本情報設定
    SetBasicInfo(iApkihoncd, iShopcd, iDatsksmotokbn, iUserApkihoncd,
      iUserShopcd, iTantoshacd, iIpAddress, iDenpyonofrom, iDenpyonoto);

    -- 入力パラメータ.請求書出力有無区分チェック
    IF (NVL(iSkyshootptkbn, 'X') NOT IN ('0', '1')) THEN
      RAISE_APPLICATION_ERROR(-20015, 'パラメータ.請求書出力有無区分が正しくありません。');
    END IF;

    -- 入金取込ワークデータ取得
    wS10W001TRecord := GetS10W001T(iApkihoncd, iShopcd, iDatsksmotokbn, iDenpyonofrom);

    -- 請求先マスタ更新ロック
    ForUpdateS02M017T(iApkihoncd, iShopcd, wS10W001TRecord.SKYSAKICD);

    -- 取消処理
    CancelProcess(iApkihoncd, iShopcd, wS10W001TRecord, iDenpyonoto, iSkyshootptkbn);

    -- 滞留更新処理
     D1504B01.Tairyukousin(iApkihoncd, iShopcd, iUserApkihoncd, iUserShopcd,
       iTantoshacd, iIpAddress, vSystemDate, vSystemTime, wS10W001TRecord.NYKNDD,
       '2', 0, wS10W001TRecord.SKYSAKICD, NULL);

    -- 得意先更新処理
    UpdateTokuisaki(iApkihoncd, iShopcd, wS10W001TRecord.SKYSAKICD);

    -- 入金取込得意先ワーク削除
    DeleteS10W002T(iApkihoncd, iShopcd, iTantoshacd, wS10W001TRecord.SKYSAKICD);

  END NyukinTorikeshi;

END C1009B01;
/
exit;
