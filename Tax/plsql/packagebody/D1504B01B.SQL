CREATE OR REPLACE PACKAGE BODY       D1504B01
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
  /* プロシージャ名 ：デバッグログ出力                                          */
  /* 引数           ：iPrcsName           処理名                                */
  /*                ：iSkysakicd          最終締年月日（滞留基準日）            */
  /*                ：iMsg                メッセージ                            */
  /******************************************************************************/
  PROCEDURE DEBUGLOG(iPrcsName  IN VARCHAR2
                    ,iSkysakicd IN vRecordS02M017T.SKYSAKICD%TYPE
                    ,iMsg       IN S99CHKLOG.MSG%TYPE) IS
  BEGIN
    S99_CHECK_LOG(
      iPrcsName,                                                   -- 処理ID
      vRecordS02M017T.APKIHONCD,                                   -- 実行対象AP基本コード
      vRecordS02M017T.SHOPCD,                                      -- 実行対象ショップコード
      '1',                                                         -- 処理区分（1:業務画面指示　2:夜間自動バッチ）
      'R',                                                         -- レコード区分（S:開始 R:抽出件数 I:登録件数 U:更新件数 D:削除件数 E:終了）
      '0',                                                         -- 処理結果（0:結果レコード以外 1:正常終了 9:異常終了
      'xxx',                                                       -- 処理対象テーブル名(ID)
      0,                                                           -- 処理対象件数
      vRecordS02M017T.KOSHAAPKIHONCD,                              -- 共通領域ＡＰ基本コード
      vRecordS02M017T.KOSHASHOPCD,                                 -- 共通領域ショップコード
      vRecordS02M017T.KOSHACD,                                     -- 共通領域担当者コード
      vRecordS02M017T.KSHNIPADRSE,                                 -- 共通領域ＩＰアドレス
      vRecordS02M017T.KSHNPGMID,                                   -- 共通領域プログラムＩＤ
      ' >>> ' || iMsg || '（請求先コード : ' || iSkysakicd || '）' -- メッセージ
    );
  END DEBUGLOG;


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
    AND SYKM.YUKOKBN   = '1';

  BEGIN
    --システム管理マスタから運用日付の取得
    OPEN SELECT_OPERATIONDATE;
    LOOP
        FETCH SELECT_OPERATIONDATE
        INTO returnOperationdate;
        --運用日付チェック
        IF SELECT_OPERATIONDATE%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(-20010, '運用日付が設定されていません。');
        END IF;
        EXIT WHEN SELECT_OPERATIONDATE%NOTFOUND;
        IF SELECT_OPERATIONDATE%ROWCOUNT > 1 THEN
            RAISE_APPLICATION_ERROR(-20011, '運用日付が複数見つかりました。');
        END IF;
    END LOOP;
    CLOSE SELECT_OPERATIONDATE;
    --文字設定値（運用日付）を返す
    return(returnOperationdate);
  END GetOperationdate;

  /*****************************************************************************/
  /* プロシージャ名   ：基本情報設定                                           */
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
    /* 共通変数削除*/
    vRecordS02M017T := null;    /* 請求先マスタ*/
    vRecordS07T001T := null;    /* 請求残高テーブル*/

    /*システム日付 と システム時間の検索*/
    SELECT TO_CHAR(CURRENT_DATE, 'YYYYMMDD'), TO_CHAR(CURRENT_DATE, 'hh24miss')
    INTO vRecordS02M017T.KSHNSYSTMYMD, vRecordS02M017T.KSHNSYSTMJIKAN
    FROM DUAL;

    vRecordS02M017T.APKIHONCD           := iApKihoncd;                              --ＡＰ基本コード
    vRecordS02M017T.SHOPCD              := iShopcd;                                 --ショップコード

    vRecordS02M017T.KOSHAAPKIHONCD      := iUserApKihoncd;                          --更新者ＡＰ基本コード
    vRecordS02M017T.KOSHASHOPCD         := iUserShopcd;                             --更新者ショップコード
    vRecordS02M017T.KOSHACD             := iTnshacd;                                --更新者コード
    vRecordS02M017T.KSHNUNYOYMD         := GetOperationdate(iApKihoncd, iShopcd);   --更新運用日付
    vRecordS02M017T.KSHNIPADRSE         := iIpadrse;                                --更新ＩＰアドレス
    vRecordS02M017T.KSHNPGMID           := 'D1504B01';                              --更新プログラムＩＤ

    vRecordS02M017T.TAIRYUKBN           := '0';                                     --滞留区分
    vRecordS02M017T.TAIRYUHASSEIKEIKADM := 0;                                       --滞留発生経過月
    vRecordS02M017T.TAIRYUHASSEIKAISU   := 0;                                       --滞留発生回数
  END SetUserInfo;

  /*****************************************************************************************************/
  /* プロシージャ名 ：最終締年月日取得                                                                 */
  /* 引数           ：iSkysakicd             請求先コード                                              */
  /*                ：iLastShimeDate         最終締年月日（滞留基準日）                                */
  /*                ：oTairyuKijunSeikyuDate 今回締日（滞留基準請求日）（戻り値）                      */
  /*****************************************************************************************************/
  PROCEDURE GetLastShimeDate(iSkysakicd             IN  VARCHAR2,
                             iLastShimeDate         IN  VARCHAR2,
                             oTairyuKijunSeikyuDate OUT VARCHAR2) IS

    CURSOR SELECT_LAST_SHIME_DYDMDD IS
    SELECT
        MAX(SZDT.KONKAISHIMEDD) -- 今回締日
    FROM
        S07T001T SZDT -- 請求残高テーブル
    WHERE
        SZDT.APKIHONCD     = vRecordS02M017T.APKIHONCD      --共通変数.ＡＰ基本コード
    AND SZDT.SHOPCD        = vRecordS02M017T.SHOPCD         --共通変数.ショップコード
    AND SZDT.SKYSAKICD     = iSkysakicd                     --パラメータ.請求先コード
    AND SZDT.KONKAISHIMEDD <= iLastShimeDate                --最終締年月日
    AND SZDT.YUKOKBN       = '1';                           --有効区分

  BEGIN
    --最終締年月日取得
    OPEN SELECT_LAST_SHIME_DYDMDD;
    LOOP
        FETCH SELECT_LAST_SHIME_DYDMDD
        INTO oTairyuKijunSeikyuDate;
        EXIT WHEN SELECT_LAST_SHIME_DYDMDD%NOTFOUND;
    END LOOP;
    CLOSE SELECT_LAST_SHIME_DYDMDD;
  END GetLastShimeDate;
  
  /******************************************************************************/
  /* プロシージャ名 ：請求残高件数取得                                          */
  /* 引数           ：iSkysakicd        請求先コード                            */
  /*                ：iShimeDateFm,     請求残高取得日From                      */
  /*                ：iShimeDateTo      請求残高取得日To                        */
  /*                ：oZanKensu         請求残高件数                            */
  /******************************************************************************/
  PROCEDURE GetSZDTcount(iSkysakicd   IN VARCHAR2,
                         iShimeDateFm IN VARCHAR2,
                         iShimeDateTo IN VARCHAR2,
                         oZanKensu    OUT NUMBER) IS

    --請求残高の残件数取得
    CURSOR SELECT_SZDT IS
    SELECT
        COUNT(*)  -- 件数
    FROM (
        SELECT DISTINCT
            SKYSAKICD
          , RENBAN
        FROM
            S07T001T SZDT -- 請求残高テーブル
        WHERE
            SZDT.APKIHONCD = vRecordS02M017T.APKIHONCD  --共通変数.ＡＰ基本コード
        AND SZDT.SHOPCD    = vRecordS02M017T.SHOPCD     --共通変数.ショップコード
        AND SZDT.SKYSAKICD = iSkysakicd                 --パラメータ.請求先コード
        AND SZDT.KONKAISHIMEDD BETWEEN iShimeDateFm AND iShimeDateTo
        AND SZDT.YUKOKBN   = '1'                        --有効区分
    );

  BEGIN
    --請求残高の残件数取得
    OPEN SELECT_SZDT;
    LOOP
        FETCH SELECT_SZDT
        INTO oZanKensu;     --請求残高の件数
        EXIT WHEN SELECT_SZDT%NOTFOUND;
    END LOOP;
    CLOSE SELECT_SZDT;
  END GetSZDTcount;

  /*****************************************************************************/
  /* プロシージャ名   ：請求先マスタ更新                                       */
  /* 引数             ：iSkysakicd         請求先コード                        */
  /*                  ：iTairyuKingaku     滞留金額                            */
  /*****************************************************************************/
  PROCEDURE UpdateSKSM(iSkysakicd     IN VARCHAR2,
                       iTairyuKingaku IN NUMBER) IS

  BEGIN
    --請求先マスタ更新
    UPDATE S02M017T SKSM --請求先マスタ
    SET
        SKSM.TAIRYUKBN           = vRecordS02M017T.TAIRYUKBN, --滞留区分
        SKSM.TAIRYUKIN           = iTairyuKingaku,            --パラメータ.滞留金額
        SKSM.TAIRYUHASSEIKAISU   =
            CASE
            WHEN TO_NUMBER(vRecordS02M017T.TAIRYUHASSEIKAISU) >= 99 THEN 99
            ELSE vRecordS02M017T.TAIRYUHASSEIKAISU
            END, --滞留発生回数
        SKSM.TAIRYUHASSEIKEIKADM =
            CASE
            WHEN TO_NUMBER(vRecordS02M017T.TAIRYUHASSEIKEIKADM) >= 99 THEN '99'
            ELSE vRecordS02M017T.TAIRYUHASSEIKEIKADM
            END, --滞留発生経過月
        SKSM.KOSHAAPKIHONCD      = vRecordS02M017T.KOSHAAPKIHONCD,      --更新者ＡＰ基本コード
        SKSM.KOSHASHOPCD         = vRecordS02M017T.KOSHASHOPCD,         --更新者ショップコード
        SKSM.KOSHACD             = vRecordS02M017T.KOSHACD,             --更新者コード
        SKSM.KSHNSYSTMYMD        = vRecordS02M017T.KSHNSYSTMYMD,        --更新システム日付: システム日付
        SKSM.KSHNSYSTMJIKAN      = vRecordS02M017T.KSHNSYSTMJIKAN,      --更新システム時間: システム時間
        SKSM.KSHNUNYOYMD         = vRecordS02M017T.KSHNUNYOYMD,         --更新運用日付: システム管理マスタ.文字設定値
        SKSM.KSHNIPADRSE         = vRecordS02M017T.KSHNIPADRSE,         --更新ＩＰアドレス: パラメータ．更新ＩＰアドレス
        SKSM.KSHNPGMID           = vRecordS02M017T.KSHNPGMID            --更新プログラムＩＤ
    WHERE
        SKSM.APKIHONCD = vRecordS02M017T.APKIHONCD      --ＡＰ基本コード
    AND SKSM.SHOPCD    = vRecordS02M017T.SHOPCD         --ショップコード
    AND SKSM.SKYSAKICD = iSkysakicd;                    --パラメータ.請求先コード
  END UpdateSKSM;

  /*****************************************************************************/
  /* ファンクション名：処理締日取得                                            */
  /* 引数            ：iShimedydm         締年月                               */
  /*                 ：iShimedd           締日                                 */
  /*                 ：iSkyshimedd        請求締日                             */
  /* 戻り値          ：処理締日                                                */
  /*****************************************************************************/
  FUNCTION GetProcessFixDay(iShimedydm  IN S07T001T.SHIMEDYDM%TYPE
                           ,iShimedd    IN S07T001T.SHIMEDD%TYPE
                           ,iSkyshimedd IN S02M017T.SKYSHIMEDD%TYPE) RETURN S07T001T.SHIMEDD%TYPE IS
    ---------------------------
    -- 変数定義
    ---------------------------
    wShimedydmLastDate S07T001T.KONKAISHIMEDD%TYPE; -- 締年月末日
  BEGIN
    wShimedydmLastDate := TO_CHAR(LAST_DAY(TO_DATE(iShimedydm || '01', 'YYYYMMDD')), 'YYYYMMDD');
    RETURN CASE WHEN iShimedydm || iShimedd >= wShimedydmLastDate THEN iSkyshimedd ELSE iShimedd END;
  END GetProcessFixDay;

  /*****************************************************************************/
  /* ファンクション名：直近締日取得（月末日の場合、31を返却）                  */
  /* 引数            ：iLastFixDate       直近締年月日                         */
  /* 戻り値          ：直近締日                                                */
  /*****************************************************************************/
  FUNCTION GetLastFixDay(iLastFixDate IN S07T001T.KONKAISHIMEDD%TYPE) RETURN S07T001T.SHIMEDD%TYPE IS
    ---------------------------
    -- 変数定義
    ---------------------------
    wLastDay S07T001T.SHIMEDD%TYPE; -- 対象月末日
  BEGIN
    wLastDay := TO_CHAR(LAST_DAY(TO_DATE(iLastFixDate, 'YYYYMMDD')), 'DD');
    RETURN CASE WHEN wLastDay = SUBSTR(iLastFixDate, 7, 2) THEN '31' ELSE SUBSTR(iLastFixDate, 7, 2) END;
  END GetLastFixDay;

  /******************************************************************************/
  /* プロシージャ名 ：滞留情報設定                                              */
  /* 引数           ：iSkysakicd         請求先コード                           */
  /*                ：iShimedydm         締年月                                 */
  /*                ：iShimedd           締日                                   */
  /*                ：iSkyShimeDD        請求先締日dd                           */
  /*                ：iSkyNyknKbn        請求入金区分                           */
  /*                ：iNewTairyuKbn      新滞留区分                             */
  /******************************************************************************/
  PROCEDURE SetTairyuInfo(iSkysakicd    IN VARCHAR2,
                          iShimedydm    IN VARCHAR2,
                          iShimedd      IN VARCHAR2,
                          iSkyShimeDD   IN VARCHAR2,
                          iSkyNyknKbn   IN VARCHAR2,
                          iNewTairyuKbn IN VARCHAR2) IS

    wZankensu       NUMBER(11);     --請求残高の残件数
    wKeikaKijunDate VARCHAR2(8);    --経過月算出基準日
    wShimeDate      VARCHAR2(8);    --請求残高.今回締日
    wTairyuKeikasu  NUMBER(5);      --滞留発生経過月数
    wProcessFixDay  S07T001T.SHIMEDD%TYPE;  -- 処理締日
    wLastFixDay     S07T001T.SHIMEDD%TYPE;  -- 直近締日（月末日の場合、31を設定）

  BEGIN
    -- 処理締日取得
    wProcessFixDay := GetProcessFixDay(iShimedydm, iShimedd, iSkyShimeDD);

    IF (iSkyNyknKbn = '1') THEN
        --パラメータ.請求入金区分＝1（請求）の場合

        IF (vRecordS02M017T.TAIRYUKBN = '0') THEN
            --共通変数.滞留区分＝0（滞留なし）の場合

            --共通変数.滞留発生回数←共通変数.滞留発生回数＋1（計算結果が99を超える場合は、99）
            vRecordS02M017T.TAIRYUHASSEIKAISU :=
                CASE
                WHEN TO_NUMBER(vRecordS02M017T.TAIRYUHASSEIKAISU) >= 99 THEN 99
                ELSE NVL(vRecordS02M017T.TAIRYUHASSEIKAISU, 0) + 1
                END;
        END IF;

        --滞留発生経過月を算出するための経過月算出基準日を算出
        -----------------------------------------------------------------------
        -- 処理締日 ＝ パラメータ.請求先締日 の場合
        IF (wProcessFixDay = iSkyShimeDD) THEN
            --請求残高件数取得
            GetSZDTcount(iSkysakicd,                --パラメータ.請求先コード
                         iShimedydm || iShimedd,    --パラメータ.締年月＋パラメータ.締日
                         iShimedydm || iShimedd,    --パラメータ.締年月＋パラメータ.締日
                         wZankensu                  --請求残高の件数（戻り値）
                        );

            --残数判定
            IF (wZankensu <= 1) THEN
                --変数.請求残高件数 ≦ 1 の場合（今回締の請求残高のみ存在する）
                --変数.経過月算出基準日←パラメータ.締年月 ＋ パラメータ.締日
                wKeikaKijunDate := iShimedydm || iShimedd;
            ELSE
                --変数.請求残高件数 ＞ 1 の場合（今回締の請求残高以外も存在する）
                wKeikaKijunDate := NULL;
            END IF;

        -- 処理締日 ＞ パラメータ.請求先締日 の場合
        ELSIF (wProcessFixDay > iSkyShimeDD) THEN
            --請求残高件数取得
            GetSZDTcount(iSkysakicd,                   --パラメータ.請求先コード
                         iShimedydm || iSkyShimeDD,    --パラメータ.締年月＋パラメータ.請求先締日
                         iShimedydm || iSkyShimeDD,    --パラメータ.締年月＋パラメータ.請求先締日
                         wZankensu                     --請求残高の件数（戻り値）
                        );

            --残数判定
            IF (wZankensu > 0) THEN
                --変数.請求残高件数 ＞ 0 の場合（締年月の請求先締日の請求残高が存在する）
                wKeikaKijunDate := NULL;

            ELSIF (wZankensu = 0) THEN
                --変数.請求残高件数 ＝ 0 の場合（締年月の請求先締日に請求残高が存在しない）

                --請求残高件数取得
                GetSZDTcount(iSkysakicd,                   --パラメータ.請求先コード
                             TO_CHAR(ADD_MONTHS(TO_DATE(iShimedydm || '01', 'YYYYMMDD'), -1), 'YYYYMM') || iSkyShimeDD,    --パラメータ.締年月の前月＋パラメータ.請求先締日
                             iShimedydm || iShimedd,    --パラメータ.締年月＋パラメータ.締日
                             wZankensu                     --請求残高の件数（戻り値）
                            );

               IF (wZankensu <= 1) THEN
                    --変数.請求残高件数 ≦ 1 の場合（今回締の請求残高のみ存在する）
                    wKeikaKijunDate := iShimedydm || iSkyShimeDD;    --パラメータ.締年月＋パラメータ.請求先締日
                ELSE
                    --変数.請求残高件数 ＞ 1 の場合（今回締の請求残高以外も存在する）
                    wKeikaKijunDate := NULL;
                END IF;
            ELSE
                NULL;
            END IF;

        -- 処理締日 ＜ パラメータ.請求先締日 の場合
        ELSE
            IF (iSkyShimeDD = '31') THEN
                --請求残高件数取得
                GetSZDTcount(iSkysakicd,                   --パラメータ.請求先コード
                             TO_CHAR(LAST_DAY(ADD_MONTHS(TO_DATE(iShimedydm || '01', 'YYYYMMDD'), -1)), 'YYYYMMDD'),    --パラメータ.締年月の前月の末日
                             TO_CHAR(LAST_DAY(ADD_MONTHS(TO_DATE(iShimedydm || '01', 'YYYYMMDD'), -1)), 'YYYYMMDD'),    --パラメータ.締年月の前月の末日
                             wZankensu                     --請求残高の件数（戻り値）
                            );
            ELSE
                --請求残高件数取得
                GetSZDTcount(iSkysakicd,                   --パラメータ.請求先コード
                             TO_CHAR(ADD_MONTHS(TO_DATE(iShimedydm || '01', 'YYYYMMDD'), -1), 'YYYYMM') || iSkyShimeDD,    --パラメータ.締年月の前月＋パラメータ.請求先締日
                             TO_CHAR(ADD_MONTHS(TO_DATE(iShimedydm || '01', 'YYYYMMDD'), -1), 'YYYYMM') || iSkyShimeDD,    --パラメータ.締年月の前月＋パラメータ.請求先締日
                             wZankensu                     --請求残高の件数（戻り値）
                            );
            END IF;

            IF (wZankensu > 0) THEN
                --変数.請求残高件数 ＞ 0 の場合（締年月の前月の請求先締日の請求残高が存在する）
                wKeikaKijunDate := NULL;

            ELSIF (wZankensu = 0) THEN
                --変数.請求残高件数 ＝ 0 の場合（締年月の前月の請求先締日の請求残高が存在しない）

                IF (iSkyShimeDD = '31') THEN
                    --請求残高件数取得
                    GetSZDTcount(iSkysakicd,                   --パラメータ.請求先コード
                                 TO_CHAR(LAST_DAY(ADD_MONTHS(TO_DATE(iShimedydm || '01', 'YYYYMMDD'), -1)), 'YYYYMMDD'),    --パラメータ.締年月の前月の末日
                                 iShimedydm || iShimedd,       --パラメータ.締年月＋パラメータ.締日
                                 wZankensu                     --請求残高の件数（戻り値）
                                );
                ELSE
                    --請求残高件数取得
                    GetSZDTcount(iSkysakicd,                   --パラメータ.請求先コード
                                 TO_CHAR(ADD_MONTHS(TO_DATE(iShimedydm || '01', 'YYYYMMDD'), -1), 'YYYYMM') || iSkyShimeDD,    --パラメータ.締年月の前月＋パラメータ.請求先締日
                                 iShimedydm || iShimedd,       --パラメータ.締年月＋パラメータ.締日
                                 wZankensu                     --請求残高の件数（戻り値）
                                );
                END IF;

                IF (wZankensu <= 1) THEN
                    --変数.請求残高件数 ≦ 1 の場合（今回締の請求残高のみ存在する）
                    IF (iSkyShimeDD = '31') THEN
                        --パラメータ.締年月の前月の末日
                        wKeikaKijunDate := TO_CHAR(LAST_DAY(ADD_MONTHS(TO_DATE(iShimedydm || '01', 'YYYYMMDD'), -1)), 'YYYYMMDD');
                    ELSE
                        --パラメータ.締年月の前月＋パラメータ.請求先締日
                        wKeikaKijunDate := TO_CHAR(ADD_MONTHS(TO_DATE(iShimedydm || '01', 'YYYYMMDD'), -1), 'YYYYMM') || iSkyShimeDD;
                    END IF;
                ELSE
                    --変数.請求残高件数 ＞ 1 の場合（今回締の請求残高以外も存在する）
                    wKeikaKijunDate := NULL;
                END IF;
            ELSE
                NULL;
            END IF;
        END IF;

        --滞留発生経過月を算出
        IF (wKeikaKijunDate IS NOT NULL) THEN
            --直近の請求締日を取得
            GetLastShimeDate(iSkysakicd,        --パラメータ.請求先コード
                             TO_CHAR(ADD_MONTHS(TO_DATE(wKeikaKijunDate, 'YYYYMMDD'), -1), 'YYYYMMDD'),    --変数.経過月算出基準日 の１カ月前
                             wShimeDate        --請求残高.今回締日（戻り値）
                             );
            wLastFixDay := GetLastFixDay(wShimeDate); -- 月末日の場合、31を設定

            -- 直近締日（月末日の場合、31） ≧ パラメータ.請求先締日 の場合
            IF (wLastFixDay >= iSkyShimeDD) THEN
                --変数.滞留発生経過月数←変数.直近締日の月 から 変数.経過月算出基準日の月 までの月数
                wTairyuKeikasu := TRUNC(MONTHS_BETWEEN(TO_DATE(wKeikaKijunDate, 'YYYYMMDD'), TO_DATE(wShimeDate, 'YYYYMMDD')));

            -- 直近締日（月末日の場合、31） ＜ パラメータ.請求先締日 の場合
            ELSE
                --変数.滞留発生経過月数←変数.直近締日の１カ月前の月 から 変数.経過月算出基準日の月 までの月数
                wTairyuKeikasu := TRUNC(MONTHS_BETWEEN(TO_DATE(wKeikaKijunDate, 'YYYYMMDD'), ADD_MONTHS(TO_DATE(wShimeDate, 'YYYYMMDD'), -1)));
            END IF;

            --共通変数.滞留発生経過月←共通変数.滞留発生経過月 ＋ 変数.滞留発生経過月数（計算結果が99を超える場合は、99）
            vRecordS02M017T.TAIRYUHASSEIKEIKADM :=
                CASE
                WHEN TO_NUMBER(vRecordS02M017T.TAIRYUHASSEIKEIKADM) + wTairyuKeikasu >= 99 THEN '99'
                ELSE NVL(vRecordS02M017T.TAIRYUHASSEIKEIKADM, 0) + wTairyuKeikasu
                END;
        END IF;
    END IF;

    --共通変数.滞留区分←パラメータ.新滞留区分
    vRecordS02M017T.TAIRYUKBN := iNewTairyuKbn;
  END SetTairyuInfo;

  /******************************************************************************/
  /* プロシージャ名 ：滞留基準請求日の月の税込売上金額合計を取得                */
  /* 引数           ：iSkysakicd        請求先コード                            */
  /*                ：iUriDateFm,       税込売上集計対象日From                  */
  /*                ：iUriDateTo        税込売上集計対象日To                    */
  /*                ：oUriKingaku       税込売上金額合計                        */
  /******************************************************************************/
  PROCEDURE GetTairyuKijunUriagekin(iSkysakicd  IN VARCHAR2,
                                    iUriDateFm  IN VARCHAR2,
                                    iUriDateTo  IN VARCHAR2,
                                    oUriKingaku OUT NUMBER) IS

    --売上金額取得
    CURSOR SELECT_URIAGEKIN IS
    SELECT
        NVL(SUM(NVL(SZDT.KONKAITAXNUKIURIKIN, 0) + NVL(SZDT.KONKAISHOHITAXKIN, 0)), 0)  -- 税込売上金額合計
    FROM
        S07T001T SZDT -- 請求残高テーブル
    WHERE
        SZDT.APKIHONCD = vRecordS02M017T.APKIHONCD      --ＡＰ基本コード
    AND SZDT.SHOPCD    = vRecordS02M017T.SHOPCD         --ショップコード
    AND SZDT.SKYSAKICD = iSkysakicd                     --パラメータ.請求先コード
    AND SZDT.KONKAISHIMEDD BETWEEN iUriDateFm AND iUriDateTo
    AND SZDT.YUKOKBN   = '1';                           --有効区分

  BEGIN
    --売上金額取得
    OPEN SELECT_URIAGEKIN;
    LOOP
        FETCH SELECT_URIAGEKIN
        INTO oUriKingaku; --売上金額
        EXIT WHEN SELECT_URIAGEKIN%NOTFOUND;
    END LOOP;
    CLOSE SELECT_URIAGEKIN;
  END GetTairyuKijunUriagekin;

  /******************************************************************************/
  /* プロシージャ名 ：滞留基準請求日時点の繰越金額を取得                        */
  /* 引数           ：iSkysakicd             請求先コード                       */
  /*                ：iTairyuKijunSeikyuDate 滞留基準請求日                     */
  /*                ：oKurikoshiKingaku      繰越金額合計                       */
  /******************************************************************************/
  PROCEDURE GetTairyuKijunKurikoshi(iSkysakicd             IN VARCHAR2,
                                    iTairyuKijunSeikyuDate IN VARCHAR2,
                                    oKurikoshiKingaku      OUT NUMBER) IS

    --繰越金額取得
    CURSOR SELECT_KURIKOSHIKIN IS
    SELECT
        /*+ INDEX(SZDT S07T001T_IX2) */
        NVL(SUM(NVL(SZDT.KURIKOSHIKIN, 0)), 0)     -- 繰越金額合計
    FROM
        S07T001T SZDT -- 請求残高テーブル
    WHERE
        SZDT.APKIHONCD     = vRecordS02M017T.APKIHONCD      --ＡＰ基本コード
    AND SZDT.SHOPCD        = vRecordS02M017T.SHOPCD         --ショップコード
    AND SZDT.SKYSAKICD     = iSkysakicd                     --パラメータ.請求先コード
    AND SZDT.KONKAISHIMEDD = iTairyuKijunSeikyuDate         --パラメータ.滞留基準請求日
    AND SZDT.YUKOKBN       = '1'                            --有効区分
    AND (TOKUISAKICD, RENBAN) IN (
        SELECT
            /*+ UNNEST */
            SZDT2.TOKUISAKICD, MAX(SZDT2.RENBAN)
        FROM
            S07T001T SZDT2  --請求残高
        WHERE
            SZDT2.APKIHONCD = SZDT.APKIHONCD
        AND SZDT2.SHOPCD    = SZDT.SHOPCD
        AND SZDT2.SKYSAKICD = SZDT.SKYSAKICD
        AND SZDT2.SHIMEDYDM = SZDT.SHIMEDYDM
        AND SZDT2.SHIMEDD   = SZDT.SHIMEDD
        AND SZDT2.YUKOKBN   = '1'
        GROUP BY
        SZDT2.TOKUISAKICD
    );

  BEGIN
    --繰越金額取得
    OPEN SELECT_KURIKOSHIKIN;
    LOOP
        FETCH SELECT_KURIKOSHIKIN
        INTO oKurikoshiKingaku;     --繰越金額
        EXIT WHEN SELECT_KURIKOSHIKIN%NOTFOUND;
    END LOOP;
    CLOSE SELECT_KURIKOSHIKIN;
  END GetTairyuKijunKurikoshi;

  /******************************************************************************/
  /* プロシージャ名 ：滞留基準請求日より未来の締済み入金金額を集計              */
  /* 引数           ：iSkysakicd             請求先コード                       */
  /*                ：iTairyuKijunSeikyuDate 滞留基準請求日                     */
  /*                ：oShimeNyukinKingaku    締め済入金金額                     */
  /******************************************************************************/
  PROCEDURE GetShimeZumiNyukin(iSkysakicd             IN VARCHAR2,
                               iTairyuKijunSeikyuDate IN VARCHAR2,
                               oShimeNyukinKingaku    OUT NUMBER) IS

    --入金金額取得
    CURSOR SELECT_NYUKIN IS
    SELECT
        NVL(SUM(NVL(SZDT.KONKAINYKNKIN, 0)), 0) -- 今回入金金額合計
    FROM
        S07T001T SZDT -- 請求残高テーブル
    WHERE
        SZDT.APKIHONCD     = vRecordS02M017T.APKIHONCD      --ＡＰ基本コード
    AND SZDT.SHOPCD        = vRecordS02M017T.SHOPCD         --ショップコード
    AND SZDT.SKYSAKICD     = iSkysakicd                     --パラメータ.請求先コード
    AND SZDT.KONKAISHIMEDD > iTairyuKijunSeikyuDate         --パラメータ.滞留基準請求日
    AND SZDT.YUKOKBN       = '1';                           -- 請求残高テーブル.有効区分

  BEGIN
    --入金金額取得
    OPEN SELECT_NYUKIN;
    LOOP
        FETCH SELECT_NYUKIN
        INTO oShimeNyukinKingaku; --今回入金金額
        EXIT WHEN SELECT_NYUKIN%NOTFOUND;
    END LOOP;
    CLOSE SELECT_NYUKIN;
  END GetShimeZumiNyukin;

  /******************************************************************************/
  /* プロシージャ名 ：未締入金金額取得（入金管理テーブルより）                  */
  /* 引数           ：iSkysakicd             請求先コード                       */
  /*                ：iShimedydm             締年月                             */
  /*                ：iShimedd               締日                               */
  /*                ：oShimeMaeNyukinKingaku 入金金額（戻り値）                 */
  /******************************************************************************/
  PROCEDURE GetShimeMaeNyukin(iSkysakicd             IN VARCHAR2,
                              iShimedydm             IN VARCHAR2,
                              iShimedd               IN VARCHAR2,
                              oShimeMaeNyukinKingaku OUT NUMBER) IS

    --入金金額取得
    CURSOR SELECT_NYUKIN IS
    SELECT
        -- ACCIS2023-7108 売上調整金での入金分は請求残高（繰越金額）に含まれているため、取得しないように修正
        --入金合計（入金金額、値引金額、手数料）
        NVL(SUM(NVL(NKNR.NYKNKIN, 0) + NVL(NKNR.NEBIKIKIN, 0) + NVL(NKNR.TESURYO, 0)), 0)
    FROM
        S10T003T NKNR -- 入金管理テーブル
    WHERE
        NKNR.APKIHONCD = vRecordS02M017T.APKIHONCD      --ＡＰ基本コード
    AND NKNR.SHOPCD    = vRecordS02M017T.SHOPCD         --ショップコード
    AND NKNR.SKYSAKICD = iSkysakicd                     --パラメータ.請求先コード
    AND NKNR.YUKOKBN   = '1'                            --有効区分
    AND EXISTS (
        SELECT 1
        FROM
            S10T001T NMST -- 入金明細テーブル
        WHERE
            NMST.APKIHONCD   = NKNR.APKIHONCD
        AND NMST.SHOPCD      = NKNR.SHOPCD
        AND NMST.DNPYNO      = NKNR.NYKNDNPYNO
        -- ACCIS2023-6974 start-ABI 過入金になった締済入金で消込した際に未締入金金額が正しく取得出来ない不具合の対応
        -- →過入金移動の入金明細（消込時は未締になる）の伝票番号が旧システムと異なり元伝票（締済）と同じ伝票番号になったことの考慮漏れ
        --   過入金区分を条件に追加し、過入金移動の入金明細以外で未締の入金明細を探しその入金金額を未締入金額として取得出来るように対応
        AND NMST.KAIREKINKBN = '0'                  -- 過入金区分
        -- ACCIS2023-6974 end-ABI
        AND NMST.YUKOKBN     = '1'                  -- 有効区分
        AND (NMST.SKYPRCSJISSIDD  = '00000000'      -- 請求処理実施日
            -- ACCIS2023-7108 売上調整金での入金分は請求残高（繰越金額）に含まれているため、取得しないように修正
            --OR (NKNR.URICHOSEIKIN <> 0
            --AND NMST.SKYSHIMEDYDM = iShimedydm      --パラメータ.締年月
            --AND NMST.SKYSHIMEDD   = iShimedd)       --パラメータ.締日
            )
        );

  BEGIN
    --入金金額取得
    OPEN SELECT_NYUKIN;
    LOOP
        FETCH SELECT_NYUKIN
        INTO oShimeMaeNyukinKingaku; --入金金額＋値引金額＋手数料
        EXIT WHEN SELECT_NYUKIN%NOTFOUND;
    END LOOP;
    CLOSE SELECT_NYUKIN;
  END GetShimeMaeNyukin;

  /******************************************************************************/
  /* プロシージャ名 ：滞留判定                                                  */
  /* 引数           ：iSkysakicd              請求先コード                      */
  /*                ：iShimedydm              締め年月                          */
  /*                ：iShimedd                締め日                            */
  /*                ：iSkyshimedd             請求先締日                        */
  /*                ：iTairyuKijunDate        滞留基準日                        */
  /*                ：iTairyuKijunSeikyuDate  滞留基準請求日                    */
  /*                ：iSkyNyknKbn             請求入金区分                      */
  /******************************************************************************/
  PROCEDURE TairyuHantei(iSkysakicd             IN VARCHAR2,
                         iShimedydm             IN VARCHAR2,
                         iShimedd               IN VARCHAR2,
                         iSkyshimedd            IN VARCHAR2,
                         iTairyuKijunDate       IN VARCHAR2,
                         iTairyuKijunSeikyuDate IN VARCHAR2,
                         iSkyNyknKbn            IN VARCHAR2) IS

    wShimeMaeNyukinKingaku NUMBER(11,0);
    wShimeNyukinKingaku    NUMBER(11,0);
    wKurikoshiKingaku      NUMBER(11,0);
    wTairyuKingaku         NUMBER(11,0);
    wUriDateFm             VARCHAR2(8);
    wUriDateTo             VARCHAR2(8);
    wUriKingaku            NUMBER(11,0);

  BEGIN
    DEBUGLOG('TairyuHantei', iSkysakicd, 'パラメータ.締年月 : ' || iShimedydm);
    DEBUGLOG('TairyuHantei', iSkysakicd, 'パラメータ.締日 : ' || iShimedd);
    DEBUGLOG('TairyuHantei', iSkysakicd, 'パラメータ.請求先締日 : ' || iSkyshimedd);
    DEBUGLOG('TairyuHantei', iSkysakicd, 'パラメータ.滞留基準日 : ' || iTairyuKijunDate);
    DEBUGLOG('TairyuHantei', iSkysakicd, 'パラメータ.滞留基準請求日 : ' || iTairyuKijunSeikyuDate);
    DEBUGLOG('TairyuHantei', iSkysakicd, 'パラメータ.請求入金区分 : ' || iSkyNyknKbn);
    -- ACCIS2023-7108 売上調整金での入金分は請求残高（繰越金額）に含まれているため、取得しないように修正
    --未締めの入金金額を取得
    GetShimeMaeNyukin(iSkysakicd,       --パラメータ.請求先コード
                      iShimedydm,       --パラメータ.締年月
                      iShimedd,         --パラメータ.締日
                      wShimeMaeNyukinKingaku);   --未締め入金金額／入金管理.入金金額ほか合計（戻り値）
    DEBUGLOG('TairyuHantei', iSkysakicd, '未締めの入金金額 : ' || wShimeMaeNyukinKingaku);
    --滞留基準請求日より未来の締済み入金金額を集計
    GetShimeZumiNyukin(iSkysakicd,          --パラメータ.請求先コード
                       iTairyuKijunSeikyuDate,      --パラメータ.滞留基準請求日
                       wShimeNyukinKingaku         --締め済入金金額／請求残高.今回入金金額合計（戻り値）
                       );
    DEBUGLOG('TairyuHantei', iSkysakicd, '滞留基準請求日より未来の締済み入金金額 : ' || wShimeNyukinKingaku);

    --滞留基準請求日時点の繰越金額を取得
    GetTairyuKijunKurikoshi(iSkysakicd,      --パラメータ.請求先コード
                           iTairyuKijunSeikyuDate,  --パラメータ.滞留基準請求日
                           wKurikoshiKingaku    --滞留基準繰越金額／請求残高.繰越金額合計（戻り値）
                           );
    DEBUGLOG('TairyuHantei', iSkysakicd, '滞留基準請求日時点の繰越金額 : ' || wKurikoshiKingaku);

    --滞留金額設定
    --変数.滞留金額←変数.滞留基準繰越金額－変数.締済入金金額－変数.未締入金金額
    wTairyuKingaku := wKurikoshiKingaku - wShimeNyukinKingaku - wShimeMaeNyukinKingaku;
    DEBUGLOG('TairyuHantei', iSkysakicd, '変数.滞留金額 = ' || wTairyuKingaku);

    --パラメータ.請求入金区分 ＝ "2"（入金）かつ 変数.滞留基準繰越金額 ＝ 0 の場合
    IF (iSkyNyknKbn = '2' AND wKurikoshiKingaku = 0) THEN
        wTairyuKingaku := 0;
    END IF;

    IF (wTairyuKingaku <= 0) THEN
        --滞留金額≦0の場合

        IF (vRecordS02M017T.TAIRYUKBN != '0') THEN
            DEBUGLOG('TairyuHantei', iSkysakicd, '滞留区分＝0以外の場合、滞留解除');
            --滞留区分＝'0'以外の場合、滞留解除
            vRecordS02M017T.TAIRYUKBN           := '0';     --滞留区分
            vRecordS02M017T.TAIRYUHASSEIKEIKADM := 0;       --滞留発生経過月
            --請求先マスタ更新
            UpdateSKSM(iSkysakicd,      --パラメータ.請求先コード
                       0                --滞留金額
                       );
        END IF;
    ELSE
        --滞留金額＞0の場合、滞留発生
        DEBUGLOG('TairyuHantei', iSkysakicd, '滞留金額＞0の場合、滞留発生');

        --変数.税込売上集計対象日From←パラメータ.滞留基準日の1ヵ月前の翌日（月末を考慮すること）
        wUriDateFm := TO_CHAR(ADD_MONTHS(TO_DATE(iTairyuKijunDate, 'YYYYMMDD'), -1) + 1, 'YYYYMMDD');
        --変数.税込売上集計対象日To←パラメータ.滞留基準日
        wUriDateTo := iTairyuKijunDate;
        DEBUGLOG('TairyuHantei', iSkysakicd, '変数.税込売上集計対象日From = ' || wUriDateFm);
        DEBUGLOG('TairyuHantei', iSkysakicd, '変数.税込売上集計対象日To = ' || wUriDateTo);

        --滞留基準請求日の月の税込売上金額合計を取得
        GetTairyuKijunUriagekin(iSkysakicd,     --パラメータ.請求先コード
                                wUriDateFm,     --税込売上集計対象日From
                                wUriDateTo,     --税込売上集計対象日To
                                wUriKingaku      --税込売上金額合計
                                );
        DEBUGLOG('TairyuHantei', iSkysakicd, 'GetTairyuKijunUriagekin : ' || wUriKingaku);

        IF (wTairyuKingaku - wUriKingaku <= 0) THEN
            --滞留金額－税込売上金額合計＜＝0の場合
            --※1ヵ月滞留（滞留基準日の1ヵ月前は滞留していない）
            DEBUGLOG('TairyuHantei', iSkysakicd, '滞留金額－税込売上金額合計＜＝0の場合、1ヵ月滞留（滞留基準日の1ヵ月前は滞留していない）');
            --滞留情報設定
            SetTairyuInfo(iSkysakicd,       --パラメータ.請求先コード
                          iShimedydm,       --パラメータ.締年月
                          iShimedd,         --パラメータ.締日
                          iSkyshimedd,      --請求先締日
                          iSkyNyknKbn,      --請求入金区分
                          '1'               --新滞留区分（1ヵ月滞留）
                          );
        ELSE
            --滞留金額－税込売上金額合計＜0以外の場合

            --変数.税込売上集計対象日From←パラメータ.滞留基準日の2ヵ月前の翌日（月末を考慮すること）
            wUriDateFm := TO_CHAR(ADD_MONTHS(TO_DATE(iTairyuKijunDate, 'YYYYMMDD'), -2) + 1, 'YYYYMMDD');
            --変数.税込売上集計対象日To←パラメータ.滞留基準日
            wUriDateTo := iTairyuKijunDate;
            DEBUGLOG('TairyuHantei', iSkysakicd, '変数.税込売上集計対象日From = ' || wUriDateFm);
            DEBUGLOG('TairyuHantei', iSkysakicd, '変数.税込売上集計対象日To = ' || wUriDateTo);

            --滞留基準請求日の月の税込売上金額合計を取得
            GetTairyuKijunUriagekin(iSkysakicd,     --パラメータ.請求先コード
                                    wUriDateFm,     --税込売上集計対象日From
                                    wUriDateTo,     --税込売上集計対象日To
                                    wUriKingaku      --税込売上金額合計
                                    );
            DEBUGLOG('TairyuHantei', iSkysakicd, 'GetTairyuKijunUriagekin : ' || wUriKingaku);

            IF (wTairyuKingaku - wUriKingaku <= 0) THEN
                --滞留金額－税込売上金額合計＜＝0の場合
                --※2ヵ月滞留（滞留基準日の2ヵ月前は滞留していない）
                DEBUGLOG('TairyuHantei', iSkysakicd, '滞留金額－税込売上金額合計＜＝0の場合、2ヵ月滞留（滞留基準日の2ヵ月前は滞留していない）');
                --滞留情報設定
                SetTairyuInfo(iSkysakicd,       --パラメータ.請求先コード
                              iShimedydm,       --パラメータ.締年月
                              iShimedd,         --パラメータ.締日
                              iSkyshimedd,      --請求先締日
                              iSkyNyknKbn,      --請求入金区分
                              '2'               --新滞留区分（2ヵ月滞留）
                              );
            ELSE
                --滞留金額－税込売上金額合計＜＝0以外の場合
                --※長期滞留（滞留基準日の2ヵ月前以前から滞留している）
                DEBUGLOG('TairyuHantei', iSkysakicd, '滞留金額－税込売上金額合計＜＝0以外の場合、長期滞留（滞留基準日の2ヵ月前以前から滞留している）');

                --滞留情報設定
                SetTairyuInfo(iSkysakicd,       --パラメータ.請求先コード
                              iShimedydm,       --パラメータ.締年月
                              iShimedd,         --パラメータ.締日
                              iSkyshimedd,      --請求先締日
                              iSkyNyknKbn,      --請求入金区分
                              '3'               --新滞留区分（長期滞留）
                              );
            END IF;
        END IF;
        --請求先マスタ更新
        UpdateSKSM(iSkysakicd,      --パラメータ.請求先コード
                   wTairyuKingaku   --滞留金額
                   );
    END IF;
    <<BEGIN_END>>
    NULL;
  END TairyuHantei;

  /******************************************************************************/
  /* プロシージャ名   ：前回滞留情報設定                                        */
  /* 引数             ：iSkysakicd         請求先コード                         */
  /*                  ：iShimedydm         締め年月                             */
  /*                  ：iShimedd           締め日                               */
  /******************************************************************************/
  PROCEDURE GetZKTR(iSkysakicd IN VARCHAR2,
                    iShimedydm IN VARCHAR2,
                    iShimedd   IN VARCHAR2) IS

  BEGIN
    --前回滞留情報テーブルから滞留発生回数、滞留発生経過月を取得する
    SELECT
        NVL(ZKTR.TAIRYUHASSEIKAISU, 0),     -- 滞留発生回数
        NVL(ZKTR.TAIRYUHASSEIKEIKADM, 0)    -- 滞留発生経過月
    INTO
        vRecordS02M017T.TAIRYUHASSEIKAISU,
        vRecordS02M017T.TAIRYUHASSEIKEIKADM
    FROM
        S19T001T ZKTR -- 前回滞留情報テーブル
    WHERE
        ZKTR.APKIHONCD  = vRecordS02M017T.APKIHONCD     --共通変数.ＡＰ基本コード
    AND ZKTR.SHOPCD     = vRecordS02M017T.SHOPCD        --共通変数.ショップコード
    AND ZKTR.SKYSAKICD  = iSkysakicd                    --パラメータ.請求先コード
    AND ZKTR.SHIMEDYDM  = iShimedydm                    --パラメータ.締め年月
    AND ZKTR.SKYSHIMEDD = iShimedd;                     --パラメータ.締め日
  END GetZKTR;

  /******************************************************************************/
  /* プロシージャ名   ：請求先マスタ情報設定                                    */
  /* 引数           ：iSkysakicd         請求先コード                           */
  /*                ：oTairyuSite        滞留計算支払サイト                     */
  /*                ：oTairyuKaisu       滞留発生回数                           */
  /*                ：oTairyuKeikaTsuki  滞留発生経過月                         */
  /*                ：oTairyKbn          滞留区分                               */
  /*                ：oSkyShimeDate      請求締日（請求先締日）                 */
  /*                ：oShrdd             支払日                                 */
  /******************************************************************************/
  PROCEDURE GetSKSM(iSkysakicd        IN VARCHAR2,
                    oTairyuSite       OUT VARCHAR2,
                    oTairyuKaisu      OUT VARCHAR2,
                    oTairyuKeikaTsuki OUT VARCHAR2,
                    oTairyKbn         OUT VARCHAR2,
                    oSkyShimeDate     OUT VARCHAR2,
                    oShrdd            OUT VARCHAR2) IS

  BEGIN
    --請求先先マスタ検索
    SELECT
        NVL(SKSM.TAIRYUKEISANSHRSAITO, 0),  --滞留計算支払サイト
        NVL(SKSM.TAIRYUHASSEIKAISU, 0),     --滞留発生回数
        NVL(SKSM.TAIRYUHASSEIKEIKADM, 0),   --滞留発生経過月
        SKSM.TAIRYUKBN,                     --滞留区分
        SKSM.SKYSHIMEDD,                    --請求締日（請求先締日）
        SKSM.SHRDD                          --支払日
    INTO
        oTairyuSite,
        oTairyuKaisu,
        oTairyuKeikaTsuki,
        oTairyKbn,
        oSkyShimeDate,
        oShrdd
    FROM
        S02M017T SKSM -- 請求先マスタ
    WHERE
        SKSM.APKIHONCD = vRecordS02M017T.APKIHONCD  --共通変数.ＡＰ基本コード
    AND SKSM.SHOPCD    = vRecordS02M017T.SHOPCD     --共通変数.ショップコード
    AND SKSM.SKYSAKICD = iSkysakicd                 --パラメータ.請求先コード
    AND SKSM.YUKOKBN   = '1';                       --有効区分
  END GetSKSM;

  /******************************************************************************/
  /* プロシージャ名 ：滞留基準情報（滞留基準日、滞留基準請求日）取得            */
  /* 引数           ：iSkysakicd           請求先コード                         */
  /*                  iSkysakiShimedd      請求先の請求締日（dd）               */
  /*                  iTargetDate          処理対象日                           */
  /*                  oTairyuKijunDate     滞留基準日（戻り値）                 */
  /*                  oTairyuKijunSkyDate  滞留基準請求日（戻り値）             */
  /******************************************************************************/
  PROCEDURE GetTairyuKijunInfo(iSkysakicd          IN  VARCHAR2
                             , iSkysakiShimedd     IN  VARCHAR2
                             , iTairyuSite         IN  VARCHAR2
                             , iTargetDate         IN  VARCHAR2
                             , oTairyuKijunDate    OUT VARCHAR2
                             , oTairyuKijunSkyDate OUT VARCHAR2) IS

    wTairyuKijunDate     VARCHAR2(8);     -- 滞留基準日
    wTairyuKijunSkyDate  VARCHAR2(8);     -- 滞留基準請求日
    wLatestShrYtiSkyDate VARCHAR2(8);     -- 処理対象日以前の最大支払予定日の請求締日
    wKijunSkyDate        VARCHAR2(8);     -- 基準締日
    wKijunShimeDay       VARCHAR2(2);     -- 基準締日の日
    wMonths              NUMBER(2);       -- 滞留計算支払サイトの月数
    wYyyymm              VARCHAR2(6);     -- 計算用年月
    wDd                  VARCHAR2(2);     -- 計算用日

  BEGIN
    DEBUGLOG('GetTairyuKijunInfo', iSkysakicd, 'パラメータ.請求先の請求締日 : ' || iSkysakiShimedd);
    DEBUGLOG('GetTairyuKijunInfo', iSkysakicd, 'パラメータ.滞留計算支払サイト : ' || iTairyuSite);
    DEBUGLOG('GetTairyuKijunInfo', iSkysakicd, 'パラメータ.処理対象日 : ' || iTargetDate);

    -- 「滞留計算支払サイト」のから月数を決定する
    CASE iTairyuSite
      WHEN '1' THEN wMonths := -1;
      WHEN '2' THEN wMonths := -2;
      WHEN '3' THEN wMonths := -3;
      WHEN '4' THEN wMonths := -6;
    ELSE            wMonths := -12;
    END CASE;
    DEBUGLOG('GetTairyuKijunInfo', iSkysakicd, '変数.月数 : ' || wMonths);
    -- 処理対象日の年月を取得
    wYyyymm := SUBSTR(iTargetDate, 1, 6);
    DEBUGLOG('GetTairyuKijunInfo', iSkysakicd, '変数.処理対象年月 : ' || wYyyymm);
    -- 処理対象日の日を取得
    wDd := SUBSTR(iTargetDate, 7, 2);
    DEBUGLOG('GetTairyuKijunInfo', iSkysakicd, '変数.処理対象日の日 : ' || wDd);
    -- 遡った年月と処理対象日の日を元に、締日を算出する ※月末指定の考慮
    wKijunShimeDay := GetProcessFixDay(wYyyymm, wDd, iSkysakiShimedd);
    DEBUGLOG('GetTairyuKijunInfo', iSkysakicd, '変数.基準締日の日 : ' || wKijunShimeDay);
    -- 処理対象日の年月から滞留計算支払サイトから決定した月数遡った年月を算出する
    wYyyymm := TO_CHAR(ADD_MONTHS(TO_DATE(wYyyymm || '01', 'YYYYMMDD'), wMonths), 'YYYYMM');
    DEBUGLOG('GetTairyuKijunInfo', iSkysakicd, '変数.処理対象年月から月数分遡る : ' || wYyyymm);
    -- 基準請求日を算出
    IF (wKijunShimeDay = '31') THEN
        wKijunSkyDate := TO_CHAR(LAST_DAY(TO_DATE(wYyyymm || '01', 'YYYYMMDD')), 'YYYYMMDD');
    ELSE
        wKijunSkyDate := wYyyyMM || wDd;
    END IF;
    DEBUGLOG('GetTairyuKijunInfo', iSkysakicd, '変数.基準請求日 : ' || wKijunSkyDate);
    -- 滞留基準請求日を算出（今回締日が基準請求日以前で最新の請求残高から取得する今回締日）
    GetLastShimeDate(iSkysakicd, wKijunSkyDate, wTairyuKijunSkyDate);
    -- 滞留基準日を設定
    wTairyuKijunDate := wTairyuKijunSkyDate;
    DEBUGLOG('GetTairyuKijunInfo', iSkysakicd, '戻り値.滞留基準日 : ' || wTairyuKijunDate);
    DEBUGLOG('GetTairyuKijunInfo', iSkysakicd, '戻り値.滞留基準請求日 : ' || wTairyuKijunSkyDate);
    -- 戻り値設定
    oTairyuKijunDate := wTairyuKijunDate;
    oTairyuKijunSkyDate := wTairyuKijunSkyDate;
  END GetTairyuKijunInfo;

  /******************************************************************************/
  /* プロシージャ名 ：入金滞留更新                                              */
  /* 引数           ：iPrcsdd            処理日（請求締日or入金日）             */
  /*                ：iSkysakicd         対象請求先                             */
  /******************************************************************************/
  PROCEDURE NyknPrcsd(iPrcsdd     IN VARCHAR2,
                      iSkysakicd  IN VARCHAR2) IS

    wTairyuSite         VARCHAR2(1);     --滞留計算支払サイト
    wTairyuKijunDate    VARCHAR2(8);     --滞留基準日
    wTairyuKijunSkyDate VARCHAR2(8);     --滞留基準請求日（請求残高.今回締日）
    wSkyShimedd         VARCHAR2(8);     --請求先締日dd
    wShrdd              VARCHAR2(2);     --支払日dd
    wOperationDate      VARCHAR2(8);     --運用日付

  BEGIN
    
    DEBUGLOG('NyknPrcsd', iSkysakicd, 'パラメータ.処理日 : ' || iPrcsdd);

    --請求先先マスタ検索
    GetSKSM(iSkysakicd,                             --パラメータ.請求先コード
            wTairyuSite,                            --滞留計算支払サイト（戻り値）
            vRecordS02M017T.TAIRYUHASSEIKAISU,      --滞留発生回数（戻り値）
            vRecordS02M017T.TAIRYUHASSEIKEIKADM,    --滞留発生経過月（戻り値）
            vRecordS02M017T.TAIRYUKBN,              --滞留区分（戻り値）
            wSkyShimedd,                            --請求締日（請求先締日dd）（戻り値）
            wShrdd                                  --支払日
            );

    --滞留計算サイト判定
    IF (wTairyuSite IS NULL OR wTairyuSite = '') THEN
        wTairyuSite := '1';    --1ヶ月
    END IF;
    IF (wTairyuSite = '0' OR wTairyuSite > '5') THEN
        RAISE_APPLICATION_ERROR(-20012, '請求先：' || iSkysakicd || 'の滞留計算支払サイトが"1,2,3,4,5"以外です。');
    END IF;

    -- 運用日付を取得
    wOperationDate := GetOperationDate(vRecordS02M017T.APKIHONCD, vRecordS02M017T.SHOPCD);
    DEBUGLOG('NyknPrcsd', iSkysakicd, '変数.運用日付 : ' || wOperationDate);

    -- 運用日付を処理対象日として、滞留基準情報を取得
    GetTairyuKijunInfo(iSkysakicd, wSkyShimedd, wTairyuSite, wOperationDate, wTairyuKijunDate, wTairyuKijunSkyDate);
    
    -- 滞留判定対象となる請求残高が存在しない場合は滞留状態であれば、滞留解除する
    IF (wTairyuKijunSkyDate IS NULL) THEN
        DEBUGLOG('NyknPrcsd', iSkysakicd, '滞留判定対象となる請求残高が存在しない→滞留判定しない');
        IF (vRecordS02M017T.TAIRYUKBN != '0') THEN
            DEBUGLOG('NyknPrcsd', iSkysakicd, '滞留解除');
            vRecordS02M017T.TAIRYUKBN           := '0';     --滞留区分
            vRecordS02M017T.TAIRYUHASSEIKEIKADM := 0;       --滞留発生経過月
            --請求先マスタ更新
            UpdateSKSM(iSkysakicd,      --パラメータ.請求先コード
                       0                --滞留金額
            );
        END IF;
        -- 滞留判定は行わない
        RETURN;
    END IF;

    -- 滞留判定
    TairyuHantei(iSkysakicd,                  --パラメータ.請求先コード
                 SUBSTR(wOperationDate, 1, 6), --運用日付の年月
                 SUBSTR(wOperationDate, 7, 2), --運用日付の日
                 wSkyShimedd,                  --請求先締日
                 wTairyuKijunDate,             --滞留基準日
                 wTairyuKijunSkyDate,          --滞留基準請求日
                 '2'                           --請求入金区分（2：入金）
                 );
  END NyknPrcsd;

  /******************************************************************************/
  /* プロシージャ名 ：請求滞留更新・請求先単位処理                              */
  /* 引数           ：iSkysakicd         請求先コード                           */
  /*                ：iShimedydm         締年月                               */
  /*                ：iShimedd           締日                                 */
  /*                ：iShimeprcskbn      締処理区分                             */
  /******************************************************************************/
  PROCEDURE SkysUnitPrcsd(iSkysakicd    IN VARCHAR2,
                          iShimedydm    IN VARCHAR2,
                          iShimedd      IN VARCHAR2,
                          iShimeprcskbn IN VARCHAR2)IS

    wTairyuSite         VARCHAR2(1);     --滞留計算支払サイト
    wTairyuKijunDate    VARCHAR2(8);     --滞留基準日
    wTairyuKijunSkyDate VARCHAR2(8);     --滞留基準請求日（請求残高.今回締日）
    wSkyShimedd         VARCHAR2(2);     --請求締日
    wShrdd              VARCHAR2(2);     --支払日
    wSkyNyknKbn         VARCHAR2(1);     --請求入金区分

  BEGIN

    DEBUGLOG('SkysUnitPrcsd', iSkysakicd, 'パラメータ.締年月 : ' || iShimedydm);
    DEBUGLOG('SkysUnitPrcsd', iSkysakicd, 'パラメータ.締日 : ' || iShimedd);
    DEBUGLOG('SkysUnitPrcsd', iSkysakicd, 'パラメータ.締処理区分 : ' || iShimeprcskbn);

    -- 請求先先マスタ検索
    GetSKSM(iSkysakicd,                             --パラメータ.請求先コード
            wTairyuSite,                            --滞留計算支払サイト（戻り値）
            vRecordS02M017T.TAIRYUHASSEIKAISU,      --滞留発生回数（戻り値）
            vRecordS02M017T.TAIRYUHASSEIKEIKADM,    --滞留発生経過月（戻り値）
            vRecordS02M017T.TAIRYUKBN,              --滞留区分（戻り値）
            wSkyShimedd,                            --請求締日（請求先締日dd）（戻り値）
            wShrdd                                  --支払日
            );

    -- 滞留計算サイト判定
    IF (wTairyuSite IS NULL OR wTairyuSite = '') THEN
        wTairyuSite := '1';    --1ヶ月
    END IF;
    IF (wTairyuSite = '0' OR wTairyuSite > '5') THEN
        RAISE_APPLICATION_ERROR(-20012, '請求先：' || iSkysakicd || 'の滞留計算支払サイトが"1,2,3,4,5"以外です。');
    END IF;

    -- パラメータ.締処理区分に応じた請求入金区分の設定
    IF (iShimeprcskbn = '1') THEN
        -- パラメータ.締処理区分が新規締の場合
        wSkyNyknKbn := '1';     -- 1（請求）
    ELSIF (iShimeprcskbn = '3') THEN
        -- パラメータ.締処理区分が締解除の場合
        wSkyNyknKbn := '2';     -- 2（入金）
        -- 前回滞留情報テーブルから滞留発生回数、滞留発生経過月を取得（取得値は共通変数へ設定済）
        GetZKTR(iSkysakicd,     -- パラメータ.請求先コード
                iShimedydm,     -- パラメータ.締め年月
                iShimedd        -- パラメータ.締め日
                );
    ELSE
        -- パラメータ.締処理区分が上記以外の場合は処理を抜ける
        DEBUGLOG('SkysUnitPrcsd', iSkysakicd, 'パラメータ.締処理区分 ≠ 1（新規締）, 3（締解除）の場合、処理を抜ける');
        GOTO BEGIN_END;
    END IF;

    -- パラメータ.請求締年月＋パラメータ.請求締日を処理対象日として、滞留基準情報を取得
    GetTairyuKijunInfo(iSkysakicd, wSkyShimedd, wTairyuSite, iShimedydm || iShimedd, wTairyuKijunDate, wTairyuKijunSkyDate);

    -- 滞留判定対象となる請求残高が存在しない場合は滞留状態であれば、滞留解除する
    IF (wTairyuKijunSkyDate IS NULL) THEN
        DEBUGLOG('SkysUnitPrcsd', iSkysakicd, '滞留判定対象となる請求残高が存在しない→滞留判定しない');
        IF (vRecordS02M017T.TAIRYUKBN != '0') THEN
            DEBUGLOG('SkysUnitPrcsd', iSkysakicd, '滞留解除');
            vRecordS02M017T.TAIRYUKBN           := '0';     --滞留区分
            vRecordS02M017T.TAIRYUHASSEIKEIKADM := 0;       --滞留発生経過月
            --請求先マスタ更新
            UpdateSKSM(iSkysakicd,      --パラメータ.請求先コード
                       0                --滞留金額
            );
        END IF;
        -- 滞留判定は行わない
        RETURN;
    END IF;

    --滞留判定
    TairyuHantei(iSkysakicd,            --パラメータ.請求先コード
                 iShimedydm,            --パラメータ.締年月
                 iShimedd,              --パラメータ.締日
                 wSkyShimedd,           --請求先締日（日）
                 wTairyuKijunDate,      --滞留基準日
                 wTairyuKijunSkyDate,   --滞留基準請求日
                 wSkyNyknKbn            --請求入金区分
                 );

    <<BEGIN_END>>
    NULL;
  END SkysUnitPrcsd;

  /******************************************************************************/
  /* プロシージャ名 ：請求の時、滞留更新処理                                    */
  /* 引数           ：iIraidd            依頼日                                 */
  /*                ：iIraijikan         依頼時間                               */
  /*                ：iPrcsdd            処理日  （請求締日or入金日）           */
  /*                ：iTrgtSkysakicd     対象請求先                             */
  /******************************************************************************/
  PROCEDURE SkysPrcsd(iIraidd        IN VARCHAR2,
                      iIraijikan     IN VARCHAR2,
                      iPrcsdd        IN VARCHAR2,
                      iTrgtSkysakicd IN VARCHAR2) IS

    BULK_SIZE CONSTANT PLS_INTEGER := 100;
    TYPE TABLE_TYPE_SKYSAKICD    IS TABLE OF S07T001T.SKYSAKICD%TYPE    INDEX BY BINARY_INTEGER; --請求残高テーブル.請求先コード
    TYPE TABLE_TYPE_SHIMEDYDM    IS TABLE OF S07T001T.SHIMEDYDM%TYPE    INDEX BY BINARY_INTEGER; --請求残高テーブル.締年月
    TYPE TABLE_TYPE_SHIMEDD      IS TABLE OF S07T001T.SHIMEDD%TYPE      INDEX BY BINARY_INTEGER; --請求残高テーブル.締日
    TYPE TABLE_TYPE_SHIMEPRCSKBN IS TABLE OF S07W003T.SHIMEPRCSKBN%TYPE INDEX BY BINARY_INTEGER; --請求残高ワーク.締処理区分
    wSkysakicd_TAB    TABLE_TYPE_SKYSAKICD;     --請求先コード
    wShimedydm_TAB    TABLE_TYPE_SHIMEDYDM;     --締年月
    wShimedd_TAB      TABLE_TYPE_SHIMEDD;       --締日
    wShimeprcskbn_TAB TABLE_TYPE_SHIMEPRCSKBN;  --締処理区分

    --最新請求締め処理済の検索
    CURSOR SELECT_LASE IS
    SELECT
        SZDT.SKYSAKICD,                                 --請求先コード
        SUBSTR(SZDT.KONKAISHIMEDD, 1, 6) SHIMEDYDM,     --締年月
        SUBSTR(SZDT.KONKAISHIMEDD, 7, 2) SHIMEDD,       --締日
        SZDT.SHIMEPRCSKBN                               --締処理区分
    FROM (
        SELECT
            /*+  INDEX (DTSZ S07T001T_IX5) */
            DTSZ.SKYSAKICD,                             --請求残高.請求先コード
            MAX(DTSZ.KONKAISHIMEDD) KONKAISHIMEDD,      --請求残高.今回締日
            MAX(SSMW.SHIMEPRCSKBN) SHIMEPRCSKBN         --請求ＷＫ.締処理区分
        FROM
            S07T001T DTSZ    --請求残高テーブル
        INNER JOIN
            S07W003T SSMW    --請求締ワーク
        ON
            DTSZ.APKIHONCD = SSMW.APKIHONCD             --ＡＰ基本コード
        AND DTSZ.SHOPCD    = SSMW.SHOPCD                --ショップコード
        AND DTSZ.SKYSAKICD = SSMW.SKYSAKICD             --請求先コード
        AND SSMW.APKIHONCD = vRecordS02M017T.APKIHONCD  --共通変数.ＡＰ基本コード
        AND SSMW.SHOPCD    = vRecordS02M017T.SHOPCD     --共通変数.ショップコード
        AND SSMW.IRAIDD    = iIraidd                    --パラメータ.依頼日
        AND SSMW.IRAIJIKAN = iIraijikan                 --パラメータ.依頼時間
        AND DTSZ.YUKOKBN   = '1'                        -- 請求残高テーブル.有効区分
        GROUP BY
            DTSZ.APKIHONCD,     --ＡＰ基本コード
            DTSZ.SHOPCD,        --ショップコード
            DTSZ.SKYSAKICD      --請求先コード
    ) SZDT
    ORDER BY
        SZDT.SKYSAKICD,             -- 請求先コード
        SZDT.KONKAISHIMEDD DESC;    -- 締年月日

  BEGIN
    IF (iTrgtSkysakicd IS NULL) THEN
        --パラメータ.対象請求先コード＝NULL　の場合
        OPEN SELECT_LASE;
        LOOP
            FETCH SELECT_LASE BULK COLLECT
            INTO wSkysakicd_TAB, wShimedydm_TAB, wShimedd_TAB, wShimeprcskbn_TAB LIMIT BULK_SIZE;
            EXIT WHEN wSkysakicd_TAB.COUNT = 0;

            FOR i IN 1 .. wSkysakicd_TAB.COUNT LOOP
                --請求先単位処理
                SkysUnitPrcsd(wSkysakicd_TAB(i),        --請求先コード
                              wShimedydm_TAB(i),        --締め年月
                              wShimedd_TAB(i),          --締め日
                              wShimeprcskbn_TAB(i));    --締処理区分
            END LOOP;
        END LOOP;
        CLOSE SELECT_LASE;
    ELSE
        --パラメータ.対象請求先コード＝NULL　以外の場合
        --請求先単位処理
        SkysUnitPrcsd(iTrgtSkysakicd,          --パラメータ.請求先コード
                      SUBSTR(iPrcsdd, 1, 6),   --パラメータ.処理日の年月
                      SUBSTR(iPrcsdd, 7, 2),   --パラメータ.処理日の日
                      '1');                    --締処理区分（1 新規締）
    END IF;
  END SkysPrcsd;

  /******************************************************************************/
  /* プロシージャ名 ：滞留更新処理                                              */
  /* 引数           ：iApKihoncd         ＡＰ基本コード                         */
  /*                ：iShopcd            ショップコード                         */
  /*                ：iUserApKihoncd     担当者ＡＰ基本コード                   */
  /*                ：iUserShopcd        担当者ショップコード                   */
  /*                ：iTnshacd           担当者コード                           */
  /*                ：iIpadrse           ＩＰアドレス                           */
  /*                ：iIraidd            依頼日  （処理区分＝1の場合必須）      */
  /*                ：iIraijikan         依頼時間（処理区分＝1の場合必須）      */
  /*                ：iPrcsdd            処理日  （請求締日or入金日）           */
  /*                ：iPrcskbn           処理区分（1請求、2入金、4一括入金）※3売上入力は廃止 */
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
                         iShimedydm     IN VARCHAR2) IS

  BEGIN
    --入力パラメータチェック
    --パラメータ.ＡＰ基本コード
    IF (iApKihoncd IS NULL) THEN
        RAISE_APPLICATION_ERROR(-20001, 'パラメータ.ＡＰ基本コード が設定されていません。');
    END IF;
    --パラメータ.ショップコード
    IF (iShopcd IS NULL) THEN
        RAISE_APPLICATION_ERROR(-20002, 'パラメータ.ショップコード が設定されていません。');
    END IF;
    --パラメータ.担当者ＡＰ基本コード
    IF (iUserApKihoncd IS NULL) THEN
        RAISE_APPLICATION_ERROR(-20003, 'パラメータ.担当者ＡＰ基本コードが設定されていません。');
    END IF;
    --パラメータ.担当者ショップコード
    IF (iUserShopcd IS NULL) THEN
        RAISE_APPLICATION_ERROR(-20004, 'パラメータ.担当者ショップコードが設定されていません。');
    END IF;
    --パラメータ.担当者コード
    IF (iTnshacd IS NULL) THEN
        RAISE_APPLICATION_ERROR(-20005, 'パラメータ.担当者コードが設定されていません。');
    END IF;
    --パラメータ.ＩＰアドレス
    IF (iIpadrse IS NULL) THEN
        RAISE_APPLICATION_ERROR(-20006, 'パラメータ.ＩＰアドレスが設定されていません。');
    END IF;
    --処理区分
    IF (iPrcskbn = '1') THEN
        --処理区分：１（請求）
        IF (iIraidd IS NULL) THEN
            RAISE_APPLICATION_ERROR(-20008, 'パラメータ.依頼日 が設定されていません。');
        END IF;
        IF (iIraijikan IS NULL) THEN
            RAISE_APPLICATION_ERROR(-20009, 'パラメータ.依頼時間 が設定されていません。');
        END IF;
    ELSIF (iPrcskbn = '2' OR iPrcskbn = '4') THEN
        --処理区分：2入金、4一括入金
        IF (iTrgtSkyscd IS NULL) THEN
            RAISE_APPLICATION_ERROR(-20011, 'パラメータ.対象請求先が設定されていません。');
        END IF;
    ELSE
        --処理区分が以外の場合
        RAISE_APPLICATION_ERROR(-20007, 'パラメータ.処理区分が対象外です。');
    END IF;
    --処理日（請求締日or入金日）
    IF (iPrcsdd IS NULL) THEN
        RAISE_APPLICATION_ERROR(-20010, 'パラメータ.処理日が設定されていません。');
    END IF;

    --基本情報設定
    SetUserInfo(iApKihoncd,         --パラメータ.ＡＰ基本コード
                iShopcd,            --パラメータ.ショップコード
                iUserApKihoncd,     --パラメータ.担当者ＡＰ基本コード
                iUserShopcd,        --パラメータ.担当者ショップコード
                iTnshacd,           --パラメータ.担当者コード
                iIpadrse            --パラメータ.ＩＰアドレス
                );

    S99_CHECK_LOG(
      'D1504B01', -- 処理ID
      vRecordS02M017T.APKIHONCD, -- 実行対象AP基本コード
      vRecordS02M017T.SHOPCD, -- 実行対象ショップコード
      '1', -- 処理区分（1:業務画面指示　2:夜間自動バッチ）
      'S', -- レコード区分（S:開始 R:抽出件数 I:登録件数 U:更新件数 D:削除件数 E:終了）
      '0', -- 処理結果（0:結果レコード以外 1:正常終了 9:異常終了
      'xxx', -- 処理対象テーブル名(ID)
      0, -- 処理対象件数
      vRecordS02M017T.KOSHAAPKIHONCD, -- 共通領域ＡＰ基本コード
      vRecordS02M017T.KOSHASHOPCD, -- 共通領域ショップコード
      vRecordS02M017T.KOSHACD, -- 共通領域担当者コード
      vRecordS02M017T.KSHNIPADRSE, -- 共通領域ＩＰアドレス
      vRecordS02M017T.KSHNPGMID, -- 共通領域プログラムＩＤ
      '[START] 滞留更新（処理区分 : ' || iPrcskbn || '）' -- メッセージ
    );

    --処理区分：１（請求）
    IF (iPrcskbn = '1') THEN
        --請求滞留更新処理
        SkysPrcsd(iIraidd,          --依頼日
                  iIraijikan,       --依頼時間
                  iPrcsdd,          --処理日（請求締日or入金日）
                  iTrgtSkyscd);     --対象請求先
    ELSE
        --入金滞留更新処理
        NyknPrcsd(iPrcsdd,          --処理日（請求締日or入金日）
                  iTrgtSkyscd);     --対象請求先
    END IF;

    S99_CHECK_LOG(
      'D1504B01', -- 処理ID
      vRecordS02M017T.APKIHONCD, -- 実行対象AP基本コード
      vRecordS02M017T.SHOPCD, -- 実行対象ショップコード
      '1', -- 処理区分（1:業務画面指示　2:夜間自動バッチ）
      'E', -- レコード区分（S:開始 R:抽出件数 I:登録件数 U:更新件数 D:削除件数 E:終了）
      '0', -- 処理結果（0:結果レコード以外 1:正常終了 9:異常終了
      'xxx', -- 処理対象テーブル名(ID)
      0, -- 処理対象件数
      vRecordS02M017T.KOSHAAPKIHONCD, -- 共通領域ＡＰ基本コード
      vRecordS02M017T.KOSHASHOPCD, -- 共通領域ショップコード
      vRecordS02M017T.KOSHACD, -- 共通領域担当者コード
      vRecordS02M017T.KSHNIPADRSE, -- 共通領域ＩＰアドレス
      vRecordS02M017T.KSHNPGMID, -- 共通領域プログラムＩＤ
      '[END] 滞留更新' -- メッセージ
    );

    --共通変数削除
    vRecordS02M017T := null; --請求先マスタ
    vRecordS07T001T := null; --請求残高テーブル
  END Tairyukousin;

END D1504B01;
/
exit;
