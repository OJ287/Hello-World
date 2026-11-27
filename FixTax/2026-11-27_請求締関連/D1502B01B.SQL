CREATE OR REPLACE PACKAGE BODY D1502B01
/*******************************************************************************
 * システム名       ：ＡＰ総合管理システム
 * 機能名           ：請求締処理
 *******************************************************************************
   Date       Author         Changes Key                                 Version
   -----------------------------------------------------------------------------
   2022-06-16 Isac)Kanaya    Create                                      1.0.0
 *******************************************************************************/
AS
  /*****************************************************************************/
  /* ファンクション名：運用日付取得                                            */
  /* 引数            ：iApkihoncd            AP基本コード                      */
  /*                 ：iShopcd               ショップコード                    */
  /* 戻り値          ：運用日付                                                */
  /*****************************************************************************/
  FUNCTION GetOperationDate(iApkihoncd IN VARCHAR2
                           ,iShopcd    IN VARCHAR2) RETURN VARCHAR2 IS
    ---------------------------
    -- カーソル定義
    ---------------------------
    -- 運用日付取得（システム管理マスタ）カーソル
    CURSOR SELECT_OPERATIONDATE IS
      SELECT
          SYKM.MOJISETTEIATAI
      FROM
          S02M006T SYKM
      WHERE
              SYKM.APKIHONCD = iApkihoncd
          AND SYKM.SHOPCD    = iShopcd
          AND SYKM.SETTEINO  = '038'
          AND SYKM.RCRDKBN   = 'M'
          AND SYKM.YUKOKBN   = '1';

    ---------------------------
    -- 変数定義
    ---------------------------
    wOperationDate S07T001T.KSHNSYSTMYMD%TYPE;
  BEGIN
    -- 運用日付取得カーソルオープン
    OPEN SELECT_OPERATIONDATE;

    LOOP
      -- 運用日付取得カーソルフェッチ
      FETCH SELECT_OPERATIONDATE INTO wOperationDate;

      -- 運用日付データなしの場合
      IF SELECT_OPERATIONDATE%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20006, '運用日付が設定されていません。');
      END IF;

      -- 運用日付取得カーソルのデータが存在しない場合ループを抜ける
      EXIT WHEN SELECT_OPERATIONDATE%NOTFOUND;

      -- 運用日付データ件数が複数の場合
      IF SELECT_OPERATIONDATE%ROWCOUNT > 1 THEN
        RAISE_APPLICATION_ERROR(-20007, '運用日付が複数見つかりました。');
      END IF;
    END LOOP;

    -- 運用日付取得カーソルクローズ
    CLOSE SELECT_OPERATIONDATE;

    -- 運用日付返却
    RETURN wOperationDate;
  END GetOperationDate;

  /*****************************************************************************/
  /* ファンクション名：新連番取得                                              */
  /* 戻り値          ：新連番                                                  */
  /*****************************************************************************/
  FUNCTION GetNewRenban RETURN VARCHAR2 IS
    ---------------------------
    -- 変数定義
    ---------------------------
    wNewRenban S07T001T.RENBAN%TYPE;
  BEGIN
    SELECT
        NVL(MAX(RENBAN) + 1, 0)
    INTO
        wNewRenban
    FROM
        S07T001T
    WHERE
            APKIHONCD     = vRecordS07T001T.APKIHONCD
        AND SHOPCD        = vRecordS07T001T.SHOPCD
        AND SKYSAKICD     = vRecordS07T001T.SKYSAKICD
        AND TOKUISAKICD   = vRecordS07T001T.TOKUISAKICD
        AND KONKAISHIMEDD = vRecordS07T001T.KONKAISHIMEDD
    ;

    RETURN wNewRenban;
  END GetNewRenban;

  /*****************************************************************************/
  /* ファンクション名：請求書番号枝番取得                                      */
  /* 引数            ：iApkihoncd            AP基本コード                      */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iSkyshono             請求書番号                        */
  /* 戻り値          ：請求書番号枝番                                          */
  /*****************************************************************************/
  FUNCTION GetSkyshoBranchNumber(iApkihoncd IN S07T001T.APKIHONCD%TYPE
                                ,iShopcd    IN S07T001T.SHOPCD%TYPE
                                ,iSkyshono  IN S07T001T.SKYSHONO%TYPE) RETURN S07T001T.SKYSHONOEDANO%TYPE IS
    ---------------------------
    -- 変数定義
    ---------------------------
    wBranchNumber S07T001T.SKYSHONOEDANO%TYPE;
  BEGIN
    SELECT
        NVL(MAX(SKYSHONOEDANO) + 1, 0)
    INTO
        wBranchNumber
    FROM
        S07T001T
    WHERE
            APKIHONCD     = iApkihoncd
        AND SHOPCD        = iShopcd
        AND SKYSHONO      = iSkyshono
    ;
    RETURN wBranchNumber;
  END GetSkyshoBranchNumber;

  /*****************************************************************************/
  /* ファンクション名：消費税額取得                                            */
  /* 引数            ：iApkihoncd            AP基本コード                      */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iDnpyno               伝票番号                          */
  /* 戻り値          ：消費税額                                                */
  /*****************************************************************************/
  FUNCTION GetTax(iApkihoncd  IN S07T006T.APKIHONCD%TYPE
                 ,iShopcd     IN S07T006T.SHOPCD%TYPE
                 ,iDnpyno     IN S07T006T.DNPYNO%TYPE) RETURN S07T001T.KONKAISHOHITAXKIN%TYPE IS
    ---------------------------
    -- 変数定義
    ---------------------------
    wTax S07T001T.KONKAISHOHITAXKIN%TYPE;
  BEGIN
    SELECT
        SUM(NVL(SHOHITAXKIN, 0))
    INTO
        wTax
    FROM
        S06T002T
    WHERE
            APKIHONCD = iApkihoncd
        AND SHOPCD    = iShopcd
        AND URIDNPYNO = iDnpyno
        AND YUKOKBN   = '1'
    ;

    RETURN wTax;
  END GetTax;

  /******************************************************************************/
  /* プロシージャ名 ：デバッグログ出力                                          */
  /* 引数           ：iPrcsName           処理名                                */
  /*                ：iSkysakicd          請求先コード                          */
  /*                ：iTokuisakicd        得意先コード                          */
  /*                ：iMsg                メッセージ                            */
  /******************************************************************************/
  PROCEDURE DEBUGLOG(iPrcsName    IN VARCHAR2
                    ,iSkysakicd   IN vRecordS07T001T.SKYSAKICD%TYPE
                    ,iTokuisakicd IN vRecordS07T001T.TOKUISAKICD%TYPE
                    ,iMsg         IN S99CHKLOG.MSG%TYPE) IS
  BEGIN
    S99_CHECK_LOG(
      iPrcsName,                                                                               -- 処理ID
      vRecordS07T001T.APKIHONCD,                                                               -- 実行対象AP基本コード
      vRecordS07T001T.SHOPCD,                                                                  -- 実行対象ショップコード
      '1',                                                                                     -- 処理区分（1:業務画面指示　2:夜間自動バッチ）
      'R',                                                                                     -- レコード区分（S:開始 R:抽出件数 I:登録件数 U:更新件数 D:削除件数 E:終了）
      '0',                                                                                     -- 処理結果（0:結果レコード以外 1:正常終了 9:異常終了
      'xxx',                                                                                   -- 処理対象テーブル名(ID)
      0,                                                                                       -- 処理対象件数
      vRecordS07T001T.KOSHAAPKIHONCD,                                                          -- 共通領域ＡＰ基本コード
      vRecordS07T001T.KOSHASHOPCD,                                                             -- 共通領域ショップコード
      vRecordS07T001T.KOSHACD,                                                                 -- 共通領域担当者コード
      vRecordS07T001T.KSHNIPADRSE,                                                             -- 共通領域ＩＰアドレス
      vRecordS07T001T.KSHNPGMID,                                                               -- 共通領域プログラムＩＤ
      ' >>> ' || iMsg || '（' || iSkysakicd || ' / ' || iTokuisakicd || '）' -- メッセージ
    );
  END DEBUGLOG;

  /******************************************************************************/
  /* プロシージャ名 ：開始ログ出力                                              */
  /* 引数           ：iPrcsName           処理名                                */
  /******************************************************************************/
  PROCEDURE STARTLOG(iPrcsName    IN VARCHAR2) IS
  BEGIN
    S99_CHECK_LOG(
      iPrcsName,                                                                               -- 処理ID
      vRecordS07T001T.APKIHONCD,                                                               -- 実行対象AP基本コード
      vRecordS07T001T.SHOPCD,                                                                  -- 実行対象ショップコード
      '1',                                                                                     -- 処理区分（1:業務画面指示　2:夜間自動バッチ）
      'S',                                                                                     -- レコード区分（S:開始 R:抽出件数 I:登録件数 U:更新件数 D:削除件数 E:終了）
      '0',                                                                                     -- 処理結果（0:結果レコード以外 1:正常終了 9:異常終了
      'xxx',                                                                                   -- 処理対象テーブル名(ID)
      0,                                                                                       -- 処理対象件数
      vRecordS07T001T.KOSHAAPKIHONCD,                                                          -- 共通領域ＡＰ基本コード
      vRecordS07T001T.KOSHASHOPCD,                                                             -- 共通領域ショップコード
      vRecordS07T001T.KOSHACD,                                                                 -- 共通領域担当者コード
      vRecordS07T001T.KSHNIPADRSE,                                                             -- 共通領域ＩＰアドレス
      vRecordS07T001T.KSHNPGMID,                                                               -- 共通領域プログラムＩＤ
      ' >>> [START] ' || iPrcsName                                                             -- メッセージ
    );
  END STARTLOG;

  /******************************************************************************/
  /* プロシージャ名 ：終了ログ出力                                              */
  /* 引数           ：iPrcsName           処理名                                */
  /******************************************************************************/
  PROCEDURE ENDLOG(iPrcsName    IN VARCHAR2) IS
  BEGIN
    S99_CHECK_LOG(
      iPrcsName,                                                                               -- 処理ID
      vRecordS07T001T.APKIHONCD,                                                               -- 実行対象AP基本コード
      vRecordS07T001T.SHOPCD,                                                                  -- 実行対象ショップコード
      '1',                                                                                     -- 処理区分（1:業務画面指示　2:夜間自動バッチ）
      'E',                                                                                     -- レコード区分（S:開始 R:抽出件数 I:登録件数 U:更新件数 D:削除件数 E:終了）
      '0',                                                                                     -- 処理結果（0:結果レコード以外 1:正常終了 9:異常終了
      'xxx',                                                                                   -- 処理対象テーブル名(ID)
      0,                                                                                       -- 処理対象件数
      vRecordS07T001T.KOSHAAPKIHONCD,                                                          -- 共通領域ＡＰ基本コード
      vRecordS07T001T.KOSHASHOPCD,                                                             -- 共通領域ショップコード
      vRecordS07T001T.KOSHACD,                                                                 -- 共通領域担当者コード
      vRecordS07T001T.KSHNIPADRSE,                                                             -- 共通領域ＩＰアドレス
      vRecordS07T001T.KSHNPGMID,                                                               -- 共通領域プログラムＩＤ
      ' >>> [END] ' || iPrcsName                                                             -- メッセージ
    );
  END ENDLOG;

  /*****************************************************************************/
  /* プロシージャ名  ：請求残高テーブル項目値設定                              */
  /* 引数            ：iApkihoncd            AP基本コード                      */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iTnshacd              担当者コード                      */
  /*                 ：iIpadrse              IPアドレス                        */
  /*                 ：iUserApkihoncd        担当者AP基本コード                */
  /*                 ：iUserShopcd           担当者ショップコード              */
  /*                 ：iSkyshimedd           請求締日                          */
  /*****************************************************************************/
  PROCEDURE SetS07T001TRecord(iApkihoncd     IN VARCHAR2
                             ,iShopcd        IN VARCHAR2
                             ,iTnshacd       IN VARCHAR2
                             ,iIpadrse       IN VARCHAR2
                             ,iUserApkihoncd IN VARCHAR2
                             ,iUserShopcd    IN VARCHAR2
                             ,iSkyshimedd    IN VARCHAR2) IS
  BEGIN
    -- 請求残高テーブル項目値初期化
    vRecordS07T001T                         := NULL;
    vRecordS07T001T.APKIHONCD               := iApkihoncd;                              -- ＡＰ基本コード
    vRecordS07T001T.SHOPCD                  := iShopcd;                                 -- ショップコード
    vRecordS07T001T.SKYSAKICD               := NULL;                                    -- 請求先コード
    vRecordS07T001T.TOKUISAKICD             := NULL;                                    -- 得意先コード
    vRecordS07T001T.SHIMEDYDM               := SUBSTR(iSkyshimedd, 1, 6);               -- 締年月
    vRecordS07T001T.SHIMEDD                 := SUBSTR(iSkyshimedd, 7, 2);               -- 締日
    vRecordS07T001T.RENBAN                  := 0;                                       -- 連番
    vRecordS07T001T.SHIMEPRCSJISSIDD        := GetOperationDate(iApkihoncd, iShopcd);   -- 締処理実施日
    vRecordS07T001T.ZNKSHIMEDD              := NULL;                                    -- 前回締日
    vRecordS07T001T.KONKAISHIMEDD           := iSkyshimedd;                             -- 今回締日
    vRecordS07T001T.ZNKKURIKOSHIKIN         := NULL;                                    -- 前回繰越金額
    vRecordS07T001T.KONKAITAXNUKIURIKIN     := 0;                                       -- 今回税抜売上金額
    vRecordS07T001T.KONKAISHOHITAXKIN       := 0;                                       -- 今回消費税金額
    vRecordS07T001T.KONKAINYKNKIN           := 0;                                       -- 今回入金金額
    vRecordS07T001T.KURIKOSHIKIN            := 0;                                       -- 繰越金額
    vRecordS07T001T.KESHIKOMIKIN            := 0;                                       -- 消込金額
    vRecordS07T001T.KESHIKOMIZD             := 0;                                       -- 消込残高
    vRecordS07T001T.N8PERZEINUKIURIKIN      := 0;                                       -- ８％税抜売上金額
    vRecordS07T001T.N8PERSHOHITAXKIN        := 0;                                       -- ８％消費税金額
    vRecordS07T001T.N10PERZEINUKIURIKIN     := 0;                                       -- １０％税抜売上金額
    vRecordS07T001T.N10PERSHOHITAXKIN       := 0;                                       -- １０％消費税金額
    -- add 軽減税率変更対応_LI 20261127 Start
    vRecordS07T001T.OLDKEIGENZEINUKIURIKIN  := 0;                                       -- 軽減旧枠税抜売上金額（消費税改修）
    vRecordS07T001T.OLDKEIGENSHOHITAXKIN    := 0;                                       -- 軽減旧枠消費税金額（消費税改修）
    vRecordS07T001T.OLDIPPANZEINUKIURIKIN   := 0;                                       -- 一般旧枠税抜売上金額（消費税改修・当面0固定）
    vRecordS07T001T.OLDIPPANSHOHITAXKIN     := 0;                                       -- 一般旧枠消費税金額（消費税改修・当面0固定）
    -- add 軽減税率変更対応_LI 20261127 End
    vRecordS07T001T.TAXKEISANKBN            := NULL;                                    -- 税計算区分
    vRecordS07T001T.SKYICRNHYPRNTDD         := '00000000';                              -- 請求一覧表印刷日
    vRecordS07T001T.SKYSHOPRNTDD            := '00000000';                              -- 請求書印刷日
    vRecordS07T001T.SKYSHONO                := NULL;                                    -- 請求書番号
    vRecordS07T001T.SKYSHONOEDANO           := 0;                                       -- 請求書番号枝番
    vRecordS07T001T.RIYOMEISAISHOPRNTDD     := '00000000';                              -- 利用明細書印刷日
    vRecordS07T001T.RIYOMEISAISHONO         := NULL;                                    -- 利用明細書番号
    vRecordS07T001T.TOKUSOKUJOPRNTDD        := '00000000';                              -- 督促状印刷日
    vRecordS07T001T.TOKUSOKUJONO            := NULL;                                    -- 督促状番号
    vRecordS07T001T.CSVDATCHUSYUTSUDD       := '00000000';                              -- ＣＳＶデータ抽出日
    vRecordS07T001T.RGRAYURYO               := 0;                                       -- レギュラー有料
    vRecordS07T001T.RGRAMURYO               := 0;                                       -- レギュラー無料
    vRecordS07T001T.SRMYURYO                := 0;                                       -- スリム有料
    vRecordS07T001T.SRMMURYO                := 0;                                       -- スリム無料
    vRecordS07T001T.SONOTAYURYO             := 0;                                       -- その他有料
    vRecordS07T001T.SONOTAMURYO             := 0;                                       -- その他無料
    vRecordS07T001T.SEIHINMIZUURI           := 0;                                       -- 製品水売上
    vRecordS07T001T.RTLRKN                  := 0;                                       -- レンタル料金
    vRecordS07T001T.SONOTAURIKIN            := 0;                                       -- その他売上金額
    vRecordS07T001T.AQCRGRAKYNPNSURYO       := 0;                                       -- アクアクララレギュラーキャンペーン数量
    vRecordS07T001T.AQCRGRASVSURYO          := 0;                                       -- アクアクララレギュラーサービス数量
    vRecordS07T001T.AQCRGRAPUROMOSHONSURYO  := 0;                                       -- アクアクララレギュラープロモーション数量
    vRecordS07T001T.AQCRGRASIINSURYO        := 0;                                       -- アクアクララレギュラー試飲数量
    vRecordS07T001T.AQCRGRAMNTESURYO        := 0;                                       -- アクアクララレギュラーメンテ数量
    vRecordS07T001T.AQCRGRAKOKANSURYO       := 0;                                       -- アクアクララレギュラー交換数量
    vRecordS07T001T.AQCRGRAURISURYO         := 0;                                       -- アクアクララレギュラー売上数量
    vRecordS07T001T.AQCSRMKYNPNSURYO        := 0;                                       -- アクアクララスリムキャンペーン数量
    vRecordS07T001T.AQCSRMSVSURYO           := 0;                                       -- アクアクララスリムサービス数量
    vRecordS07T001T.AQCSRMPUROMOSHONSURYO   := 0;                                       -- アクアクララスリムプロモーション数量
    vRecordS07T001T.AQCSRMSIINSURYO         := 0;                                       -- アクアクララスリム試飲数量
    vRecordS07T001T.AQCSRMMNTESURYO         := 0;                                       -- アクアクララスリムメンテ数量
    vRecordS07T001T.AQCSRMKOKANSURYO        := 0;                                       -- アクアクララスリム交換数量
    vRecordS07T001T.AQCSRMURISURYO          := 0;                                       -- アクアクララスリム売上数量
    vRecordS07T001T.ETCSHNMZKYNPNSURYO      := 0;                                       -- その他製品水キャンペーン数量
    vRecordS07T001T.ETCSHNMZSVSURYO         := 0;                                       -- その他製品水サービス数量
    vRecordS07T001T.ETCSHNMZPUROMOSHONSURYO := 0;                                       -- その他製品水プロモーション数量
    vRecordS07T001T.ETCSHNMZSIINSURYO       := 0;                                       -- その他製品水試飲数量
    vRecordS07T001T.ETCSHNMZMNTESURYO       := 0;                                       -- その他製品水メンテ数量
    vRecordS07T001T.ETCSHNMZKOKANSURYO      := 0;                                       -- その他製品水交換数量
    vRecordS07T001T.ETCSHNMZURISURYO        := 0;                                       -- その他製品水売上数量
    vRecordS07T001T.SHNMZRGRAKIN            := 0;                                       -- 製品水レギュラー金額
    vRecordS07T001T.SHNMZSRMKIN             := 0;                                       -- 製品水スリム金額
    vRecordS07T001T.SHNMZSONOTAKIN          := 0;                                       -- 製品水その他金額
    vRecordS07T001T.RNTLRWSVKIN             := 0;                                       -- レンタル料サーバー金額
    vRecordS07T001T.RNTLRHKSHNKIN           := 0;                                       -- レンタル料他商品金額
    vRecordS07T001T.SNTSHNWSVKIN            := 0;                                       -- その他商品サーバー金額
    vRecordS07T001T.SNTSHNHKSHNKIN          := 0;                                       -- その他商品他商品金額
    vRecordS07T001T.SNTSHNKNPHNKIN          := 0;                                       -- その他商品キャンペーン品金額
    vRecordS07T001T.KAISHUHOHOKBN           := NULL;                                    -- 回収方法区分
    vRecordS07T001T.SHRYTIDYDMDD            := NULL;                                    -- 支払予定年月日
    vRecordS07T001T.KSISHIMEDYDMDD          := '00000000';                              -- 決済締年月日
    vRecordS07T001T.KSIKKNCD                := NULL;                                    -- 決済機関コード
    vRecordS07T001T.YUKOKBN                 := '1';                                     -- 有効区分
    vRecordS07T001T.TOSHAAPKIHONCD          := iUserApkihoncd;                          -- 登録者ＡＰ基本コード
    vRecordS07T001T.TOSHASHOPCD             := iUserShopcd;                             -- 登録者ショップコード
    vRecordS07T001T.TOSHACD                 := iTnshacd;                                -- 登録者コード
    vRecordS07T001T.INPTSYSTMYMD            := TO_CHAR(CURRENT_DATE, 'YYYYMMDD');       -- 登録システム日付
    vRecordS07T001T.INPTSYSTMJIKAN          := TO_CHAR(CURRENT_DATE, 'HH24MISS');       -- 登録システム時間
    vRecordS07T001T.INPTUNYOYMD             := GetOperationDate(iApkihoncd, iShopcd);   -- 登録運用日付
    vRecordS07T001T.INPTIPADRSE             := iIpadrse;                                -- 登録ＩＰアドレス
    vRecordS07T001T.INPTPGMID               := vPROGRAM_ID;                             -- 登録プログラムＩＤ
    vRecordS07T001T.KOSHAAPKIHONCD          := vRecordS07T001T.TOSHAAPKIHONCD;          -- 更新者ＡＰ基本コード
    vRecordS07T001T.KOSHASHOPCD             := vRecordS07T001T.TOSHASHOPCD;             -- 更新者ショップコード
    vRecordS07T001T.KOSHACD                 := vRecordS07T001T.TOSHACD;                 -- 更新者コード
    vRecordS07T001T.KSHNSYSTMYMD            := vRecordS07T001T.INPTSYSTMYMD;            -- 更新システム日付
    vRecordS07T001T.KSHNSYSTMJIKAN          := vRecordS07T001T.INPTSYSTMJIKAN;          -- 更新システム時間
    vRecordS07T001T.KSHNUNYOYMD             := vRecordS07T001T.INPTUNYOYMD;             -- 更新運用日付
    vRecordS07T001T.KSHNIPADRSE             := vRecordS07T001T.INPTIPADRSE;             -- 更新ＩＰアドレス
    vRecordS07T001T.KSHNPGMID               := vRecordS07T001T.INPTPGMID;               -- 更新プログラムＩＤ
  END SetS07T001TRecord;

  /*****************************************************************************/
  /* プロシージャ名  ：カテゴリ別集計用商品カテゴリコード設定                  */
  /*****************************************************************************/
  PROCEDURE SetCategoryCd IS
  BEGIN
    -- 商品大分類（製品水）取得（システム管理マスタ）
    BEGIN
      SELECT
          MOJISETTEIATAI
      INTO
          vCategorySeihinSui
      FROM
          S02M006T
      WHERE
              APKIHONCD = '000'
          AND SHOPCD    = '000'
          AND RCRDKBN   = 'M'
          AND SETTEINO  = '096'
          AND SETTEICD  = '1'
      ;
    EXCEPTION
      WHEN OTHERS THEN
        vCategorySeihinSui := NULL;
    END;

    -- 商品大分類（商品レンタル料）取得（システム管理マスタ）
    BEGIN
      SELECT
          MOJISETTEIATAI
      INTO
          vCategoryShnRental
      FROM
          S02M006T
      WHERE
              APKIHONCD = '000'
          AND SHOPCD    = '000'
          AND RCRDKBN   = 'M'
          AND SETTEINO  = '096'
          AND SETTEICD  = '2'
      ;
    EXCEPTION
      WHEN OTHERS THEN
        vCategoryShnRental := NULL;
    END;

    -- 商品大分類（サーバーレンタル料）取得（システム管理マスタ）
    BEGIN
      SELECT
          MOJISETTEIATAI
      INTO
          vCategorySvrRental
      FROM
          S02M006T
      WHERE
              APKIHONCD = '000'
          AND SHOPCD    = '000'
          AND RCRDKBN   = 'M'
          AND SETTEINO  = '096'
          AND SETTEICD  = '3'
      ;
    EXCEPTION
      WHEN OTHERS THEN
        vCategorySvrRental := NULL;
    END;

    -- 商品大分類（サーバー関連）取得（システム管理マスタ）
    BEGIN
      SELECT
          MOJISETTEIATAI
      INTO
          vCategorySvrKanren
      FROM
          S02M006T
      WHERE
              APKIHONCD = '000'
          AND SHOPCD    = '000'
          AND RCRDKBN   = 'M'
          AND SETTEINO  = '096'
          AND SETTEICD  = '4'
      ;
    EXCEPTION
      WHEN OTHERS THEN
        vCategorySvrKanren := NULL;
    END;

    -- 商品コード（製品水：レギュラー）取得（システム管理マスタ）
    BEGIN
      SELECT
          MOJISETTEIATAI
      INTO
          vShnCdRegular
      FROM
          S02M006T
      WHERE
              APKIHONCD = '000'
          AND SHOPCD    = '000'
          AND RCRDKBN   = 'M'
          AND SETTEINO  = '064'
          AND SETTEICD  = '1'
      ;
    EXCEPTION
      WHEN OTHERS THEN
        vShnCdRegular := NULL;
    END;

    -- 商品コード（製品水：スリム）取得（システム管理マスタ）
    BEGIN
      SELECT
          MOJISETTEIATAI
      INTO
          vShnCdSlim
      FROM
          S02M006T
      WHERE
              APKIHONCD = '000'
          AND SHOPCD    = '000'
          AND RCRDKBN   = 'M'
          AND SETTEINO  = '064'
          AND SETTEICD  = '2'
      ;
    EXCEPTION
      WHEN OTHERS THEN
        vShnCdSlim := NULL;
    END;
  END SetCategoryCd;

  /*****************************************************************************/
  /* プロシージャ名  ：支払予定日設定                                          */
  /* 引数            ：iShrsaito             支払サイト                        */
  /*                 ：iShrdd                支払日                            */
  /*****************************************************************************/
  PROCEDURE SetPaymentPlanDate(iShrsaito IN S02M017T.SHRSAITO%TYPE
                              ,iShrdd    IN S02M017T.SHRDD%TYPE) IS
  BEGIN
    vRecordS07T001T.SHRYTIDYDMDD := (
      CASE iShrsaito
        -- 支払サイト：「当月」の場合
        WHEN '1' THEN
          CONCAT(vRecordS07T001T.SHIMEDYDM, (
            CASE
              WHEN TO_CHAR(LAST_DAY(TO_DATE(vRecordS07T001T.SHIMEDYDM, 'YYYYMM')), 'DD') <= iShrdd THEN
                TO_CHAR(LAST_DAY(TO_DATE(vRecordS07T001T.SHIMEDYDM, 'YYYYMM')), 'DD')
              ELSE iShrdd
            END))
        -- 支払サイト：「翌月」の場合
        WHEN '2' THEN
          CONCAT(TO_CHAR(ADD_MONTHS(TO_DATE(vRecordS07T001T.SHIMEDYDM, 'YYYYMM'), 1), 'YYYYMM'), (
            CASE
              WHEN TO_CHAR(LAST_DAY(ADD_MONTHS(TO_DATE(vRecordS07T001T.SHIMEDYDM, 'YYYYMM'), 1)), 'DD') <= iShrdd THEN
                TO_CHAR(LAST_DAY(ADD_MONTHS(TO_DATE(vRecordS07T001T.SHIMEDYDM, 'YYYYMM'), 1)), 'DD')
              ELSE iShrdd
            END))
        -- 支払サイト：「翌々月」の場合
        WHEN '3' THEN
          CONCAT(TO_CHAR(ADD_MONTHS(TO_DATE(vRecordS07T001T.SHIMEDYDM, 'YYYYMM'), 2), 'YYYYMM'), (
            CASE
              WHEN TO_CHAR(LAST_DAY(ADD_MONTHS(TO_DATE(vRecordS07T001T.SHIMEDYDM, 'YYYYMM'), 2)), 'DD') <= iShrdd THEN
                TO_CHAR(LAST_DAY(ADD_MONTHS(TO_DATE(vRecordS07T001T.SHIMEDYDM, 'YYYYMM'), 2)), 'DD')
              ELSE iShrdd
            END))
        -- 支払サイト：「3ヶ月後」の場合
        WHEN '4' THEN
          CONCAT(TO_CHAR(ADD_MONTHS(TO_DATE(vRecordS07T001T.SHIMEDYDM, 'YYYYMM'), 3), 'YYYYMM'), (
            CASE
              WHEN TO_CHAR(LAST_DAY(ADD_MONTHS(TO_DATE(vRecordS07T001T.SHIMEDYDM, 'YYYYMM'), 3)), 'DD') <= iShrdd THEN
                TO_CHAR(LAST_DAY(ADD_MONTHS(TO_DATE(vRecordS07T001T.SHIMEDYDM, 'YYYYMM'), 3)), 'DD')
              ELSE iShrdd
            END))
        -- 支払サイト：「前払い」の場合
        WHEN '5' THEN
          CONCAT(vRecordS07T001T.SHIMEDYDM, (
            CASE
              WHEN TO_CHAR(LAST_DAY(TO_DATE(vRecordS07T001T.SHIMEDYDM, 'YYYYMM')), 'DD') <= vRecordS07T001T.SHIMEDD THEN
                TO_CHAR(LAST_DAY(TO_DATE(vRecordS07T001T.SHIMEDYDM, 'YYYYMM')), 'DD')
              ELSE vRecordS07T001T.SHIMEDD
            END))
        ELSE NULL
      END);
  END SetPaymentPlanDate;

  /*****************************************************************************/
  /* ファンクション名：売上明細テーブル数量・金額項目集計値設定                */
  /* 引数            ：iUridnpyno            売上伝票番号                      */
  /*                 ：iSkyshono             請求書番号                        */
  /*                 ：iSkyshonoedano        請求書番号枝番                    */
  /*                 ：oMeisaitaxkin         明細消費金額                      */
  /*                 ：oKonkaitaxnukiurikin  今回税抜売上金額                  */
  /*****************************************************************************/
  PROCEDURE SetSummaryValue(iUridnpyno            IN  S06T001T.URIDNPYNO%TYPE
                           ,iSkyshono             IN  S07T001T.SKYSHONO%TYPE
                           ,iSkyshonoedano        IN  S07T001T.SKYSHONOEDANO%TYPE
                           ,oMeisaitaxkin         OUT S07T001T.KONKAISHOHITAXKIN%TYPE
                           ,oKonkaitaxnukiurikin  OUT S07T001T.KONKAITAXNUKIURIKIN%TYPE) IS
    ---------------------------
    -- 変数定義
    ---------------------------
    wRgrayuryo               S07T001T.RGRAYURYO%TYPE;               -- レギュラー有料
    wRgramuryo               S07T001T.RGRAMURYO%TYPE;               -- レギュラー無料
    wSrmyuryo                S07T001T.SRMYURYO%TYPE;                -- スリム有料
    wSrmmuryo                S07T001T.SRMMURYO%TYPE;                -- スリム無料
    wSonotayuryo             S07T001T.SONOTAYURYO%TYPE;             -- その他有料
    wSonotamuryo             S07T001T.SONOTAMURYO%TYPE;             -- その他無料
    wSeihinmizuuri           S07T001T.SEIHINMIZUURI%TYPE;           -- 製品水売上
    wRtlrkn                  S07T001T.RTLRKN%TYPE;                  -- レンタル料金
    wSonotaurikin            S07T001T.SONOTAURIKIN%TYPE;            -- その他売上金額
    wAqcrgrakynpnsuryo       S07T001T.AQCRGRAKYNPNSURYO%TYPE;       -- アクアクララレギュラーキャンペーン数量
    wAqcrgrasvsuryo          S07T001T.AQCRGRASVSURYO%TYPE;          -- アクアクララレギュラーサービス数量
    wAqcrgrapuromoshonsuryo  S07T001T.AQCRGRAPUROMOSHONSURYO%TYPE;  -- アクアクララレギュラープロモーション数量
    wAqcrgrasiinsuryo        S07T001T.AQCRGRASIINSURYO%TYPE;        -- アクアクララレギュラー試飲数量
    wAqcrgramntesuryo        S07T001T.AQCRGRAMNTESURYO%TYPE;        -- アクアクララレギュラーメンテ数量
    wAqcrgrakokansuryo       S07T001T.AQCRGRAKOKANSURYO%TYPE;       -- アクアクララレギュラー交換数量
    wAqcrgraurisuryo         S07T001T.AQCRGRAURISURYO%TYPE;         -- アクアクララレギュラー売上数量
    wAqcsrmkynpnsuryo        S07T001T.AQCSRMKYNPNSURYO%TYPE;        -- アクアクララスリムキャンペーン数量
    wAqcsrmsvsuryo           S07T001T.AQCSRMSVSURYO%TYPE;           -- アクアクララスリムサービス数量
    wAqcsrmpuromoshonsuryo   S07T001T.AQCSRMPUROMOSHONSURYO%TYPE;   -- アクアクララスリムプロモーション数量
    wAqcsrmsiinsuryo         S07T001T.AQCSRMSIINSURYO%TYPE;         -- アクアクララスリム試飲数量
    wAqcsrmmntesuryo         S07T001T.AQCSRMMNTESURYO%TYPE;         -- アクアクララスリムメンテ数量
    wAqcsrmkokansuryo        S07T001T.AQCSRMKOKANSURYO%TYPE;        -- アクアクララスリム交換数量
    wAqcsrmurisuryo          S07T001T.AQCSRMURISURYO%TYPE;          -- アクアクララスリム売上数量
    wEtcshnmzkynpnsuryo      S07T001T.ETCSHNMZKYNPNSURYO%TYPE;      -- その他製品水キャンペーン数量
    wEtcshnmzsvsuryo         S07T001T.ETCSHNMZSVSURYO%TYPE;         -- その他製品水サービス数量
    wEtcshnmzpuromoshonsuryo S07T001T.ETCSHNMZPUROMOSHONSURYO%TYPE; -- その他製品水プロモーション数量
    wEtcshnmzsiinsuryo       S07T001T.ETCSHNMZSIINSURYO%TYPE;       -- その他製品水試飲数量
    wEtcshnmzmntesuryo       S07T001T.ETCSHNMZMNTESURYO%TYPE;       -- その他製品水メンテ数量
    wEtcshnmzkokansuryo      S07T001T.ETCSHNMZKOKANSURYO%TYPE;      -- その他製品水交換数量
    wEtcshnmzurisuryo        S07T001T.ETCSHNMZURISURYO%TYPE;        -- その他製品水売上数量
    wShnmzrgrakin            S07T001T.SHNMZRGRAKIN%TYPE;            -- 製品水レギュラー金額
    wShnmzsrmkin             S07T001T.SHNMZSRMKIN%TYPE;             -- 製品水スリム金額
    wShnmzsonotakin          S07T001T.SHNMZSONOTAKIN%TYPE;          -- 製品水その他金額
    wRntlrwsvkin             S07T001T.RNTLRWSVKIN%TYPE;             -- レンタル料サーバー金額
    wRntlrhkshnkin           S07T001T.RNTLRHKSHNKIN%TYPE;           -- レンタル料他商品金額
    wSntshnwsvkin            S07T001T.SNTSHNWSVKIN%TYPE;            -- その他商品サーバー金額
    wSntshnhkshnkin          S07T001T.SNTSHNHKSHNKIN%TYPE;          -- その他商品他商品金額
    wSntshnknphnkin          S07T001T.SNTSHNKNPHNKIN%TYPE;          -- その他商品キャンペーン品金額
    wN8pertaxnukiurikin      S07T001T.N8PERZEINUKIURIKIN%TYPE;      -- ８％税抜売上金額
    wN8pertaxnukiurikinTax   S07T001T.N8PERZEINUKIURIKIN%TYPE;      -- ８％税抜売上金額(消費税計算用)
    wN8pershohitaxkin        S07T001T.N8PERSHOHITAXKIN%TYPE;        -- ８％消費税金額
    wN10pertaxnukiurikin     S07T001T.N10PERZEINUKIURIKIN%TYPE;     -- １０％税抜売上金額
    wN10pertaxnukiurikinTax  S07T001T.N10PERZEINUKIURIKIN%TYPE;     -- １０％税抜売上金額(消費税計算用)
    wN10pershohitaxkin       S07T001T.N10PERSHOHITAXKIN%TYPE;       -- １０％消費税金額
    -- add 軽減税率変更対応_LI 20261127 Start
    wOldkeigenzeinukiurikin    S07T001T.OLDKEIGENZEINUKIURIKIN%TYPE;  -- 軽減旧枠税抜売上金額
    wOldkeigenzeinukiurikinTax S07T001T.OLDKEIGENZEINUKIURIKIN%TYPE;  -- 軽減旧枠税抜売上金額(消費税計算用)
    wOldkeigenshohitaxkin      S07T001T.OLDKEIGENSHOHITAXKIN%TYPE;    -- 軽減旧枠消費税金額
    wTaxRate4Shime             S02M091T.SHOHITAX%TYPE;                -- 税区分4の締日時点税率（新旧判定用）
    wMeisaitaxkin            S07T001T.N10PERSHOHITAXKIN%TYPE;       -- 明細消費税金額
    wTaxfreeurikin           S07T001T.KONKAITAXNUKIURIKIN%TYPE;     -- 非課税売上金額
    wNontaxableurikin        S07T001T.KONKAITAXNUKIURIKIN%TYPE;     -- 不課税売上金額
  BEGIN
    -- 消費税改修：新旧判定用に税区分4の締日時点税率を取得する
    SELECT SHOHITAX INTO wTaxRate4Shime FROM S02M091T
     WHERE TAXKBN = '4' AND TEKIYOSTRTDD <= vRecordS07T001T.KONKAISHIMEDD
       AND TEKIYOSHRYDD >= vRecordS07T001T.KONKAISHIMEDD AND YUKOKBN = '1';

    -- add 軽減税率変更対応_LI 20261127 End
    -- 売上明細テーブル（数量、金額項目）取得
    SELECT
        -- レギュラー有料
        NVL(SUM(
          CASE
            WHEN UMST.SHNCD IN (
              SELECT
                  M007.SHNCD
              FROM
                  S02M007T M007
              WHERE
                      M007.APKIHONCD  = '001'
                  AND M007.STKBN      = '0'
                  AND M007.LCTGRYCD   = '01'
                  AND M007.MCTGRYCD   = '001'
                  AND M007.SCTGRYCD   = '001'
                  AND M007.YUKOKBN    = '1'
              ) AND (KEIJOKBN = '01' OR KEIJOKBN = '02') THEN UMST.SURYO
            ELSE 0
          END), 0) AS RGRAYURYO
        -- レギュラー無料
        , NVL(SUM(
            CASE
              WHEN UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND M007.MCTGRYCD   = '001'
                    AND M007.SCTGRYCD   = '001'
                    AND M007.YUKOKBN    = '1'
                ) AND KEIJOKBN <> '01' AND KEIJOKBN <> '02' THEN UMST.SURYO
              ELSE 0
            END), 0) AS RGRAMURYO
        -- スリム有料
        , NVL(SUM(
            CASE
              WHEN UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND M007.MCTGRYCD   = '001'
                    AND M007.SCTGRYCD   = '002'
                    AND M007.YUKOKBN    = '1'
                ) AND (KEIJOKBN = '01' OR KEIJOKBN = '02') THEN UMST.SURYO
              ELSE 0
            END), 0) AS SRMYURYO
        -- スリム無料
        , NVL(SUM(
            CASE
              WHEN UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND M007.MCTGRYCD   = '001'
                    AND M007.SCTGRYCD   = '002'
                    AND M007.YUKOKBN    = '1'
                ) AND KEIJOKBN <> '01' AND KEIJOKBN <> '02' THEN UMST.SURYO
              ELSE 0
            END), 0) AS SRMMURYO
        -- その他有料
        , NVL(SUM(
            CASE
              WHEN UMST.LCTGRYCD = vCategorySeihinSui AND UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND (M007.MCTGRYCD, M007.SCTGRYCD) NOT IN (('001', '001'), ('001', '002'))
                    AND M007.YUKOKBN    = '1'
                ) AND (KEIJOKBN = '01' OR KEIJOKBN = '02') THEN UMST.SURYO
              ELSE 0
            END), 0) AS SONOTAYURYO
        -- その他無料
        , NVL(SUM(
            CASE
              WHEN UMST.LCTGRYCD = vCategorySeihinSui AND UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND (M007.MCTGRYCD, M007.SCTGRYCD) NOT IN (('001', '001'), ('001', '002'))
                    AND M007.YUKOKBN    = '1'
                ) AND KEIJOKBN <> '01' AND KEIJOKBN <> '02' THEN UMST.SURYO
              ELSE 0
            END), 0) AS SONOTAMURYO
        -- 製品水売上
        , NVL(SUM(CASE WHEN UMST.LCTGRYCD = vCategorySeihinSui THEN UMST.KIN ELSE 0 END), 0) AS SEIHINMIZUURI
        -- レンタル料金
        , NVL(SUM(CASE WHEN UMST.LCTGRYCD IN (vCategoryShnRental, vCategorySvrRental) THEN UMST.KIN ELSE 0 END), 0) AS RTLRKN
        -- その他売上金額
        , NVL(SUM(
            CASE
              WHEN UMST.LCTGRYCD NOT IN (vCategorySeihinSui, vCategoryShnRental, vCategorySvrRental) THEN UMST.KIN
              ELSE 0
            END), 0) AS SONOTAURIKIN
        -- アクアクララレギュラーキャンペーン数量
        , NVL(SUM(
            CASE
              WHEN UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND M007.MCTGRYCD   = '001'
                    AND M007.SCTGRYCD   = '001'
                    AND M007.YUKOKBN    = '1'
                ) AND KEIJOKBN = '02' THEN UMST.SURYO
              ELSE 0
            END), 0) AS AQCRGRAKYNPNSURYO
        -- アクアクララレギュラーサービス数量
        , NVL(SUM(
            CASE
              WHEN UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND M007.MCTGRYCD   = '001'
                    AND M007.SCTGRYCD   = '001'
                    AND M007.YUKOKBN    = '1'
                ) AND KEIJOKBN = '03' THEN UMST.SURYO
              ELSE 0
            END), 0) AS AQCRGRASVSURYO
        -- アクアクララレギュラープロモーション数量
        , NVL(SUM(
            CASE
              WHEN UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND M007.MCTGRYCD   = '001'
                    AND M007.SCTGRYCD   = '001'
                    AND M007.YUKOKBN    = '1'
                ) AND KEIJOKBN = '04' THEN UMST.SURYO
              ELSE 0
            END), 0) AS AQCRGRAPUROMOSHONSURYO
        -- アクアクララレギュラー試飲数量
        , NVL(SUM(
            CASE
              WHEN UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND M007.MCTGRYCD   = '001'
                    AND M007.SCTGRYCD   = '001'
                    AND M007.YUKOKBN    = '1'
                ) AND KEIJOKBN = '05' THEN UMST.SURYO
              ELSE 0
            END), 0) AS AQCRGRASIINSURYO
        -- アクアクララレギュラーメンテ数量
        , NVL(SUM(
            CASE
              WHEN UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND M007.MCTGRYCD   = '001'
                    AND M007.SCTGRYCD   = '001'
                    AND M007.YUKOKBN    = '1'
                ) AND KEIJOKBN = '06' THEN UMST.SURYO
              ELSE 0
            END), 0) AS AQCRGRAMNTESURYO
        -- アクアクララレギュラー交換数量
        , NVL(SUM(
            CASE
              WHEN UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND M007.MCTGRYCD   = '001'
                    AND M007.SCTGRYCD   = '001'
                    AND M007.YUKOKBN    = '1'
                ) AND KEIJOKBN = '07' THEN UMST.SURYO
              ELSE 0
            END), 0) AS AQCRGRAKOKANSURYO
        -- アクアクララレギュラー売上数量
        , NVL(SUM(
            CASE
              WHEN UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND M007.MCTGRYCD   = '001'
                    AND M007.SCTGRYCD   = '001'
                    AND M007.YUKOKBN    = '1'
                ) AND KEIJOKBN = '01' THEN UMST.SURYO
              ELSE 0
            END), 0) AS AQCRGRAURISURYO
        -- アクアクララスリムキャンペーン数量
        , NVL(SUM(
            CASE
              WHEN UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND M007.MCTGRYCD   = '001'
                    AND M007.SCTGRYCD   = '002'
                    AND M007.YUKOKBN    = '1'
                ) AND KEIJOKBN = '02' THEN UMST.SURYO
              ELSE 0
            END), 0) AS AQCSRMKYNPNSURYO
        -- アクアクララスリムサービス数量
        , NVL(SUM(
            CASE
              WHEN UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND M007.MCTGRYCD   = '001'
                    AND M007.SCTGRYCD   = '002'
                    AND M007.YUKOKBN    = '1'
                ) AND KEIJOKBN = '03' THEN UMST.SURYO
              ELSE 0
            END), 0) AS AQCSRMSVSURYO
        -- アクアクララスリムプロモーション数量
        , NVL(SUM(
            CASE
              WHEN UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND M007.MCTGRYCD   = '001'
                    AND M007.SCTGRYCD   = '002'
                    AND M007.YUKOKBN    = '1'
                ) AND KEIJOKBN = '04' THEN UMST.SURYO
              ELSE 0
            END), 0) AS AQCSRMPUROMOSHONSURYO
        -- アクアクララスリム試飲数量
        , NVL(SUM(
            CASE
              WHEN UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND M007.MCTGRYCD   = '001'
                    AND M007.SCTGRYCD   = '002'
                    AND M007.YUKOKBN    = '1'
                ) AND KEIJOKBN = '05' THEN UMST.SURYO
              ELSE 0
            END), 0) AS AQCSRMSIINSURYO
        -- アクアクララスリムメンテ数量
        , NVL(SUM(
            CASE
              WHEN UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND M007.MCTGRYCD   = '001'
                    AND M007.SCTGRYCD   = '002'
                    AND M007.YUKOKBN    = '1'
                ) AND KEIJOKBN = '06' THEN UMST.SURYO
              ELSE 0
            END), 0) AS AQCSRMMNTESURYO
        -- アクアクララスリム交換数量
        , NVL(SUM(
            CASE
              WHEN UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND M007.MCTGRYCD   = '001'
                    AND M007.SCTGRYCD   = '002'
                    AND M007.YUKOKBN    = '1'
                ) AND KEIJOKBN = '07' THEN UMST.SURYO
              ELSE 0
            END), 0) AS AQCSRMKOKANSURYO
        -- アクアクララスリム売上数量
        , NVL(SUM(
            CASE
              WHEN UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND M007.MCTGRYCD   = '001'
                    AND M007.SCTGRYCD   = '002'
                    AND M007.YUKOKBN    = '1'
                ) AND KEIJOKBN = '01' THEN UMST.SURYO
              ELSE 0
            END), 0) AS AQCSRMURISURYO
        -- その他製品水キャンペーン数量
        , NVL(SUM(
            CASE
              WHEN UMST.LCTGRYCD = vCategorySeihinSui AND UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND (M007.MCTGRYCD, M007.SCTGRYCD) NOT IN (('001', '001'), ('001', '002'))
                    AND M007.YUKOKBN    = '1'
                ) AND KEIJOKBN = '02' THEN UMST.SURYO
              ELSE 0
            END), 0) AS ETCSHNMZKYNPNSURYO
        -- その他製品水サービス数量
        , NVL(SUM(
            CASE
              WHEN UMST.LCTGRYCD = vCategorySeihinSui AND UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND (M007.MCTGRYCD, M007.SCTGRYCD) NOT IN (('001', '001'), ('001', '002'))
                    AND M007.YUKOKBN    = '1'
                ) AND KEIJOKBN = '03' THEN UMST.SURYO
              ELSE 0
            END), 0) AS ETCSHNMZSVSURYO
        -- その他製品水プロモーション数量
        , NVL(SUM(
            CASE
              WHEN UMST.LCTGRYCD = vCategorySeihinSui AND UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND (M007.MCTGRYCD, M007.SCTGRYCD) NOT IN (('001', '001'), ('001', '002'))
                    AND M007.YUKOKBN    = '1'
                ) AND KEIJOKBN = '04' THEN UMST.SURYO
              ELSE 0
            END), 0) AS ETCSHNMZPUROMOSHONSURYO
        -- その他製品水試飲数量
        , NVL(SUM(
            CASE
              WHEN UMST.LCTGRYCD = vCategorySeihinSui AND UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND (M007.MCTGRYCD, M007.SCTGRYCD) NOT IN (('001', '001'), ('001', '002'))
                    AND M007.YUKOKBN    = '1'
                ) AND KEIJOKBN = '05' THEN UMST.SURYO
              ELSE 0
            END), 0) AS ETCSHNMZSIINSURYO
        -- その他製品水メンテ数量
        , NVL(SUM(
            CASE
              WHEN UMST.LCTGRYCD = vCategorySeihinSui AND UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND (M007.MCTGRYCD, M007.SCTGRYCD) NOT IN (('001', '001'), ('001', '002'))
                    AND M007.YUKOKBN    = '1'
                ) AND KEIJOKBN = '06' THEN UMST.SURYO
              ELSE 0
            END), 0) AS ETCSHNMZMNTESURYO
        -- その他製品水交換数量
        , NVL(SUM(
            CASE
              WHEN UMST.LCTGRYCD = vCategorySeihinSui AND UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND (M007.MCTGRYCD, M007.SCTGRYCD) NOT IN (('001', '001'), ('001', '002'))
                    AND M007.YUKOKBN    = '1'
                ) AND KEIJOKBN = '07' THEN UMST.SURYO
              ELSE 0
            END), 0) AS ETCSHNMZKOKANSURYO
        -- その他製品水売上数量
        , NVL(SUM(
            CASE
              WHEN UMST.LCTGRYCD = vCategorySeihinSui AND UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND (M007.MCTGRYCD, M007.SCTGRYCD) NOT IN (('001', '001'), ('001', '002'))
                    AND M007.YUKOKBN    = '1'
                ) AND KEIJOKBN = '01' THEN UMST.SURYO
              ELSE 0
            END), 0) AS ETCSHNMZURISURYO
        -- 製品水レギュラー金額
        , NVL(SUM(
            CASE
              WHEN UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND M007.MCTGRYCD   = '001'
                    AND M007.SCTGRYCD   = '001'
                    AND M007.YUKOKBN    = '1'
                ) AND (KEIJOKBN = '01' OR KEIJOKBN = '02') THEN UMST.KIN
              ELSE 0
            END), 0) AS SHNMZRGRAKIN
        -- 製品水スリム金額
        , NVL(SUM(
            CASE
              WHEN UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND M007.MCTGRYCD   = '001'
                    AND M007.SCTGRYCD   = '002'
                    AND M007.YUKOKBN    = '1'
                ) AND (KEIJOKBN = '01' OR KEIJOKBN = '02') THEN UMST.KIN
              ELSE 0
            END), 0) AS SHNMZSRMKIN
        -- 製品水その他金額
        , NVL(SUM(
            CASE
              WHEN UMST.LCTGRYCD = vCategorySeihinSui AND UMST.SHNCD IN (
                SELECT
                    M007.SHNCD
                FROM
                    S02M007T M007
                WHERE
                        M007.APKIHONCD  = '001'
                    AND M007.STKBN      = '0'
                    AND M007.LCTGRYCD   = '01'
                    AND (M007.MCTGRYCD, M007.SCTGRYCD) NOT IN (('001', '001'), ('001', '002'))
                    AND M007.YUKOKBN    = '1'
                ) AND (KEIJOKBN = '01' OR KEIJOKBN = '02') THEN UMST.KIN
              ELSE 0
            END), 0) AS SHNMZSONOTAKIN
        -- レンタル料サーバー金額
        , NVL(SUM(CASE WHEN UMST.LCTGRYCD = vCategorySvrRental THEN UMST.KIN ELSE 0 END), 0) AS RNTLRWSVKIN
        -- レンタル料他商品金額
        , NVL(SUM(CASE WHEN UMST.LCTGRYCD = vCategoryShnRental THEN UMST.KIN ELSE 0 END), 0) AS RNTLRHKSHNKIN
        -- その他商品サーバー金額
        , NVL(SUM(CASE WHEN UMST.LCTGRYCD = vCategorySvrKanren AND KEIJOKBN <> '02' THEN UMST.KIN ELSE 0 END), 0) AS SNTSHNWSVKIN
        -- その他商品他商品金額
        , NVL(SUM(
            CASE
              WHEN UMST.LCTGRYCD NOT IN (vCategorySeihinSui, vCategoryShnRental, vCategorySvrRental, vCategorySvrKanren)
                AND KEIJOKBN <> '02' THEN UMST.KIN
              ELSE 0
            END), 0) AS SNTSHNHKSHNKIN
        -- その他商品キャンペーン品金額
        , NVL(SUM(
            CASE
              WHEN UMST.LCTGRYCD NOT IN (vCategorySeihinSui, vCategoryShnRental, vCategorySvrRental, vCategorySvrKanren)
                AND KEIJOKBN = '02' THEN UMST.KIN
              ELSE 0
            END), 0) AS SNTSHNKNPHNKIN
    INTO
          wRgrayuryo
        , wRgramuryo
        , wSrmyuryo
        , wSrmmuryo
        , wSonotayuryo
        , wSonotamuryo
        , wSeihinmizuuri
        , wRtlrkn
        , wSonotaurikin
        , wAqcrgrakynpnsuryo
        , wAqcrgrasvsuryo
        , wAqcrgrapuromoshonsuryo
        , wAqcrgrasiinsuryo
        , wAqcrgramntesuryo
        , wAqcrgrakokansuryo
        , wAqcrgraurisuryo
        , wAqcsrmkynpnsuryo
        , wAqcsrmsvsuryo
        , wAqcsrmpuromoshonsuryo
        , wAqcsrmsiinsuryo
        , wAqcsrmmntesuryo
        , wAqcsrmkokansuryo
        , wAqcsrmurisuryo
        , wEtcshnmzkynpnsuryo
        , wEtcshnmzsvsuryo
        , wEtcshnmzpuromoshonsuryo
        , wEtcshnmzsiinsuryo
        , wEtcshnmzmntesuryo
        , wEtcshnmzkokansuryo
        , wEtcshnmzurisuryo
        , wShnmzrgrakin
        , wShnmzsrmkin
        , wShnmzsonotakin
        , wRntlrwsvkin
        , wRntlrhkshnkin
        , wSntshnwsvkin
        , wSntshnhkshnkin
        , wSntshnknphnkin
    FROM
        S06T002T UMST
        INNER JOIN S06T001T HEAD
            ON (
                    HEAD.APKIHONCD      = UMST.APKIHONCD
                AND HEAD.SHOPCD         = UMST.SHOPCD
                AND HEAD.URIDNPYNO      = UMST.URIDNPYNO
                AND HEAD.EDANO          = UMST.EDANO
                AND HEAD.YUKOKBN        = '1'
            )
    WHERE
            UMST.APKIHONCD              = vRecordS07T001T.APKIHONCD
        AND UMST.SHOPCD                 = vRecordS07T001T.SHOPCD
        AND (iUridnpyno IS NULL OR UMST.URIDNPYNO = iUridnpyno)
        AND UMST.TANPINMEISAIFLG        = '1'
        AND UMST.YUKOKBN                = '1'
        AND EXISTS (
          SELECT
              SEIKYU.APKIHONCD
          FROM
              S07T006T SEIKYU
          WHERE
                  SEIKYU.APKIHONCD      = HEAD.APKIHONCD
              AND SEIKYU.SHOPCD         = HEAD.SHOPCD
              AND SEIKYU.SKYSHONO       = iSkyshono
              AND SEIKYU.SKYSHONOEDANO  = iSkyshonoedano
              AND SEIKYU.DNPYNO         = HEAD.URIDNPYNO
              AND SEIKYU.URINYKNKBN     = '0'
              AND SEIKYU.YUKOKBN        = '1'
        )
    ;

    -- 売上明細テーブル（税抜売上金額、消費税額項目）取得
    SELECT
        -- １０％税抜売上金額
          NVL(SUM(CASE WHEN UMST.TAXKBN = '1' THEN UMST.KIN ELSE 0 END), 0)                              AS N10PERTAXNUKIURIKIN
        -- １０％税抜売上金額
        , NVL(SUM(CASE WHEN UMST.KEIJOKBN IN ('01', '02') AND UMST.TAXKBN = '1' THEN UMST.KIN ELSE 0 END), 0)          AS N10PERTAXNUKIURIKINTAX
        -- 非課税売上金額
        , NVL(SUM(CASE WHEN UMST.TAXKBN = '2' THEN UMST.KIN ELSE 0 END), 0)                              AS TAXFREEURIKIN
        -- 不課税売上金額
        , NVL(SUM(CASE WHEN UMST.TAXKBN = '3' THEN UMST.KIN ELSE 0 END), 0)                              AS NONTAXABLEURIKIN
        -- modify 軽減税率変更対応_LI 20261127 Start
        -- 軽減税抜売上金額（新枠：売上日時点の税率が締日時点の税区分4税率と一致する分）
        , NVL(SUM(CASE WHEN UMST.TAXKBN IN ('4', '6', '7') AND TAXM.SHOHITAX = wTaxRate4Shime THEN UMST.KIN ELSE 0 END), 0)                              AS N8PERTAXNUKIURIKIN
        -- 軽減税抜売上金額(消費税計算用)（新枠）
        , NVL(SUM(CASE WHEN UMST.KEIJOKBN IN ('01', '02') AND UMST.TAXKBN IN ('4', '6', '7') AND TAXM.SHOHITAX = wTaxRate4Shime THEN UMST.KIN ELSE 0 END), 0)          AS N8PERTAXNUKIURIKINTAX
        -- １０％消費税金額
        , NVL(SUM(CASE WHEN UMST.KEIJOKBN IN ('01', '02') AND UMST.TAXKBN = '1' THEN UMST.SHOHITAXKIN ELSE 0 END), 0)  AS N10PERSHOHITAXKIN
        -- 軽減消費税金額（新枠）
        , NVL(SUM(CASE WHEN UMST.KEIJOKBN IN ('01', '02') AND UMST.TAXKBN IN ('4', '6', '7') AND TAXM.SHOHITAX = wTaxRate4Shime THEN UMST.SHOHITAXKIN ELSE 0 END), 0)  AS N8PERSHOHITAXKIN
        -- 軽減税抜売上金額（旧枠：売上日時点の税率が締日時点税率と異なる分）
        , NVL(SUM(CASE WHEN UMST.TAXKBN IN ('4', '6', '7') AND (TAXM.SHOHITAX IS NULL OR TAXM.SHOHITAX != wTaxRate4Shime) THEN UMST.KIN ELSE 0 END), 0)                              AS OLDKEIGENTAXNUKIURIKIN
        -- 軽減税抜売上金額(消費税計算用)（旧枠）
        , NVL(SUM(CASE WHEN UMST.KEIJOKBN IN ('01', '02') AND UMST.TAXKBN IN ('4', '6', '7') AND (TAXM.SHOHITAX IS NULL OR TAXM.SHOHITAX != wTaxRate4Shime) THEN UMST.KIN ELSE 0 END), 0)          AS OLDKEIGENTAXNUKIURIKINTAX
        -- 軽減消費税金額（旧枠）
        , NVL(SUM(CASE WHEN UMST.KEIJOKBN IN ('01', '02') AND UMST.TAXKBN IN ('4', '6', '7') AND (TAXM.SHOHITAX IS NULL OR TAXM.SHOHITAX != wTaxRate4Shime) THEN UMST.SHOHITAXKIN ELSE 0 END), 0)  AS OLDKEIGENSHOHITAXKIN
        -- 明細消費税金額
        , NVL(SUM(CASE WHEN UMST.SHNCD NOT IN ('099901', '099902', '099903', '099904') THEN UMST.SHOHITAXKIN ELSE 0 END), 0) AS MEISAITAXKIN
        -- modify 軽減税率変更対応_LI 20261127 End
    INTO
          wN10pertaxnukiurikin
        , wN10pertaxnukiurikinTax
        , wTaxfreeurikin
        , wNontaxableurikin
        , wN8pertaxnukiurikin
        , wN8pertaxnukiurikinTax
        , wN10pershohitaxkin
        , wN8pershohitaxkin
        -- add 軽減税率変更対応_LI 20261127 Start
        , wOldkeigenzeinukiurikin
        , wOldkeigenzeinukiurikinTax
        , wOldkeigenshohitaxkin
        -- add 軽減税率変更対応_LI 20261127 End
        , wMeisaitaxkin
    FROM
        S06T002T UMST
        INNER JOIN S06T001T HEAD
            ON (
                    HEAD.APKIHONCD      = UMST.APKIHONCD
                AND HEAD.SHOPCD         = UMST.SHOPCD
                AND HEAD.URIDNPYNO      = UMST.URIDNPYNO
                AND HEAD.EDANO          = UMST.EDANO
                AND HEAD.YUKOKBN        = '1'
            -- add 軽減税率変更対応_LI 20261127 Start
            )
        LEFT JOIN S02M091T TAXM
            ON (
                    TAXM.TAXKBN       = UMST.TAXKBN
                AND TAXM.TEKIYOSTRTDD <= HEAD.URIDD
                AND TAXM.TEKIYOSHRYDD >= HEAD.URIDD
                AND TAXM.YUKOKBN      = '1'
            -- add 軽減税率変更対応_LI 20261127 End
            )
    WHERE
            UMST.APKIHONCD              = vRecordS07T001T.APKIHONCD
        AND UMST.SHOPCD                 = vRecordS07T001T.SHOPCD
        AND (iUridnpyno IS NULL OR UMST.URIDNPYNO = iUridnpyno)
        AND UMST.NYURYOKUMEISAIFLG      = '1'
        AND UMST.YUKOKBN                = '1'
        AND EXISTS (
          SELECT
              SEIKYU.APKIHONCD
          FROM
              S07T006T SEIKYU
          WHERE
                  SEIKYU.APKIHONCD      = HEAD.APKIHONCD
              AND SEIKYU.SHOPCD         = HEAD.SHOPCD
              AND SEIKYU.SKYSHONO       = iSkyshono
              AND SEIKYU.SKYSHONOEDANO  = iSkyshonoedano
              AND SEIKYU.DNPYNO         = HEAD.URIDNPYNO
              AND SEIKYU.URINYKNKBN     = '0'
              AND SEIKYU.YUKOKBN        = '1'
        )
    ;

    -- 請求残高テーブル（数量、金額）項目値設定（売上明細テーブルの集計値を加算）
    vRecordS07T001T.RGRAYURYO               := vRecordS07T001T.RGRAYURYO + wRgrayuryo;                             -- レギュラー有料
    vRecordS07T001T.RGRAMURYO               := vRecordS07T001T.RGRAMURYO + wRgramuryo;                             -- レギュラー無料
    vRecordS07T001T.SRMYURYO                := vRecordS07T001T.SRMYURYO + wSrmyuryo;                               -- スリム有料
    vRecordS07T001T.SRMMURYO                := vRecordS07T001T.SRMMURYO+ wSrmmuryo;                                -- スリム無料
    vRecordS07T001T.SONOTAYURYO             := vRecordS07T001T.SONOTAYURYO + wSonotayuryo;                         -- その他有料
    vRecordS07T001T.SONOTAMURYO             := vRecordS07T001T.SONOTAMURYO + wSonotamuryo;                         -- その他無料
    vRecordS07T001T.SEIHINMIZUURI           := vRecordS07T001T.SEIHINMIZUURI + wSeihinmizuuri;                     -- 製品水売上
    vRecordS07T001T.RTLRKN                  := vRecordS07T001T.RTLRKN + wRtlrkn;                                   -- レンタル料金
    vRecordS07T001T.SONOTAURIKIN            := vRecordS07T001T.SONOTAURIKIN + wSonotaurikin;                       -- その他売上金額
    vRecordS07T001T.AQCRGRAKYNPNSURYO       := vRecordS07T001T.AQCRGRAKYNPNSURYO + wAqcrgrakynpnsuryo;             -- アクアクララレギュラーキャンペーン数量
    vRecordS07T001T.AQCRGRASVSURYO          := vRecordS07T001T.AQCRGRASVSURYO + wAqcrgrasvsuryo;                   -- アクアクララレギュラーサービス数量
    vRecordS07T001T.AQCRGRAPUROMOSHONSURYO  := vRecordS07T001T.AQCRGRAPUROMOSHONSURYO + wAqcrgrapuromoshonsuryo;   -- アクアクララレギュラープロモーション数量
    vRecordS07T001T.AQCRGRASIINSURYO        := vRecordS07T001T.AQCRGRASIINSURYO + wAqcrgrasiinsuryo;               -- アクアクララレギュラー試飲数量
    vRecordS07T001T.AQCRGRAMNTESURYO        := vRecordS07T001T.AQCRGRAMNTESURYO + wAqcrgramntesuryo;               -- アクアクララレギュラーメンテ数量
    vRecordS07T001T.AQCRGRAKOKANSURYO       := vRecordS07T001T.AQCRGRAKOKANSURYO + wAqcrgrakokansuryo;             -- アクアクララレギュラー交換数量
    vRecordS07T001T.AQCRGRAURISURYO         := vRecordS07T001T.AQCRGRAURISURYO + wAqcrgraurisuryo;                 -- アクアクララレギュラー売上数量
    vRecordS07T001T.AQCSRMKYNPNSURYO        := vRecordS07T001T.AQCSRMKYNPNSURYO + wAqcsrmkynpnsuryo;               -- アクアクララスリムキャンペーン数量
    vRecordS07T001T.AQCSRMSVSURYO           := vRecordS07T001T.AQCSRMSVSURYO + wAqcsrmsvsuryo;                     -- アクアクララスリムサービス数量
    vRecordS07T001T.AQCSRMPUROMOSHONSURYO   := vRecordS07T001T.AQCSRMPUROMOSHONSURYO + wAqcsrmpuromoshonsuryo;     -- アクアクララスリムプロモーション数量
    vRecordS07T001T.AQCSRMSIINSURYO         := vRecordS07T001T.AQCSRMSIINSURYO + wAqcsrmsiinsuryo;                 -- アクアクララスリム試飲数量
    vRecordS07T001T.AQCSRMMNTESURYO         := vRecordS07T001T.AQCSRMMNTESURYO + wAqcsrmmntesuryo;                 -- アクアクララスリムメンテ数量
    vRecordS07T001T.AQCSRMKOKANSURYO        := vRecordS07T001T.AQCSRMKOKANSURYO + wAqcsrmkokansuryo;               -- アクアクララスリム交換数量
    vRecordS07T001T.AQCSRMURISURYO          := vRecordS07T001T.AQCSRMURISURYO + wAqcsrmurisuryo;                   -- アクアクララスリム売上数量
    vRecordS07T001T.ETCSHNMZKYNPNSURYO      := vRecordS07T001T.ETCSHNMZKYNPNSURYO + wEtcshnmzkynpnsuryo;           -- その他製品水キャンペーン数量
    vRecordS07T001T.ETCSHNMZSVSURYO         := vRecordS07T001T.ETCSHNMZSVSURYO + wEtcshnmzsvsuryo;                 -- その他製品水サービス数量
    vRecordS07T001T.ETCSHNMZPUROMOSHONSURYO := vRecordS07T001T.ETCSHNMZPUROMOSHONSURYO + wEtcshnmzpuromoshonsuryo; -- その他製品水プロモーション数量
    vRecordS07T001T.ETCSHNMZSIINSURYO       := vRecordS07T001T.ETCSHNMZSIINSURYO + wEtcshnmzsiinsuryo;             -- その他製品水試飲数量
    vRecordS07T001T.ETCSHNMZMNTESURYO       := vRecordS07T001T.ETCSHNMZMNTESURYO + wEtcshnmzmntesuryo;             -- その他製品水メンテ数量
    vRecordS07T001T.ETCSHNMZKOKANSURYO      := vRecordS07T001T.ETCSHNMZKOKANSURYO + wEtcshnmzkokansuryo;           -- その他製品水交換数量
    vRecordS07T001T.ETCSHNMZURISURYO        := vRecordS07T001T.ETCSHNMZURISURYO + wEtcshnmzurisuryo;               -- その他製品水売上数量
    vRecordS07T001T.SHNMZRGRAKIN            := vRecordS07T001T.SHNMZRGRAKIN + wShnmzrgrakin;                       -- 製品水レギュラー金額
    vRecordS07T001T.SHNMZSRMKIN             := vRecordS07T001T.SHNMZSRMKIN + wShnmzsrmkin;                         -- 製品水スリム金額
    vRecordS07T001T.SHNMZSONOTAKIN          := vRecordS07T001T.SHNMZSONOTAKIN + wShnmzsonotakin;                   -- 製品水その他金額
    vRecordS07T001T.RNTLRWSVKIN             := vRecordS07T001T.RNTLRWSVKIN + wRntlrwsvkin;                         -- レンタル料サーバー金額
    vRecordS07T001T.RNTLRHKSHNKIN           := vRecordS07T001T.RNTLRHKSHNKIN + wRntlrhkshnkin;                     -- レンタル料他商品金額
    vRecordS07T001T.SNTSHNWSVKIN            := vRecordS07T001T.SNTSHNWSVKIN + wSntshnwsvkin;                       -- その他商品サーバー金額
    vRecordS07T001T.SNTSHNHKSHNKIN          := vRecordS07T001T.SNTSHNHKSHNKIN + wSntshnhkshnkin;                   -- その他商品他商品金額
    vRecordS07T001T.SNTSHNKNPHNKIN          := vRecordS07T001T.SNTSHNKNPHNKIN + wSntshnknphnkin;                   -- その他商品キャンペーン品金額
    vRecordS07T001T.N8PERZEINUKIURIKIN      := vRecordS07T001T.N8PERZEINUKIURIKIN + wN8pertaxnukiurikinTax;        -- ８％税抜売上金額
    vRecordS07T001T.N8PERSHOHITAXKIN        := vRecordS07T001T.N8PERSHOHITAXKIN + wN8pershohitaxkin;               -- ８％税抜売上金額
    -- modify 軽減税率変更対応_LI 20261127 Start
    vRecordS07T001T.OLDKEIGENZEINUKIURIKIN  := vRecordS07T001T.OLDKEIGENZEINUKIURIKIN + wOldkeigenzeinukiurikinTax;  -- 軽減旧枠税抜売上金額
    vRecordS07T001T.OLDKEIGENSHOHITAXKIN    := vRecordS07T001T.OLDKEIGENSHOHITAXKIN + wOldkeigenshohitaxkin;         -- 軽減旧枠消費税金額
    vRecordS07T001T.N10PERZEINUKIURIKIN     := vRecordS07T001T.N10PERZEINUKIURIKIN + wN10pertaxnukiurikinTax;      -- １０％税抜売上金額
    vRecordS07T001T.N10PERSHOHITAXKIN       := vRecordS07T001T.N10PERSHOHITAXKIN + wN10pershohitaxkin;             -- １０％税抜売上金額

    oMeisaitaxkin         := wMeisaitaxkin;                                                                        -- 明細消費税金額
    oKonkaitaxnukiurikin  := wN10pertaxnukiurikin + wN8pertaxnukiurikin + wOldkeigenzeinukiurikin + wTaxfreeurikin + wNontaxableurikin;      -- 今回税抜売上金額
    -- modify 軽減税率変更対応_LI 20261127 End

  EXCEPTION
    -- データが存在しない場合0を返却
    WHEN NO_DATA_FOUND THEN
      oMeisaitaxkin         := 0;
      oKonkaitaxnukiurikin  := 0;
  END SetSummaryValue;

  /*****************************************************************************/
  /* プロシージャ名  ：売上テーブル更新（請求締済の更新）                      */
  /* 引数            ：iSkyshono             請求書番号                        */
  /*                 ：iSkyshonoedano        請求書番号枝番                    */
  /*****************************************************************************/
  PROCEDURE UpdateS06T001TSeikyuFix(iSkyshono      IN S07T001T.SKYSHONO%TYPE
                                   ,iSkyshonoedano IN S07T001T.SKYSHONOEDANO%TYPE) IS
  BEGIN
    -- 売上テーブル更新（請求締済の更新）
    UPDATE S06T001T SALES
    SET
          SALES.SKYSAKICD         = vRecordS07T001T.SKYSAKICD
        , SALES.SKYSHIMEDYDM      = vRecordS07T001T.SHIMEDYDM
        , SALES.SKYSHIMEDD        = vRecordS07T001T.SHIMEDD
        , SALES.SKYPRCSJISSIDD    = vRecordS07T001T.SHIMEPRCSJISSIDD
        , SALES.SKYPRCSJISSIKBN   = vRecordS07T001T.SKYPRCSJISSIKBN
        , SALES.KOSHAAPKIHONCD    = vRecordS07T001T.KOSHAAPKIHONCD
        , SALES.KOSHASHOPCD       = vRecordS07T001T.KOSHASHOPCD
        , SALES.KOSHACD           = vRecordS07T001T.KOSHACD
        , SALES.KSHNSYSTMYMD      = vRecordS07T001T.KSHNSYSTMYMD
        , SALES.KSHNSYSTMJIKAN    = vRecordS07T001T.KSHNSYSTMJIKAN
        , SALES.KSHNUNYOYMD       = vRecordS07T001T.KSHNUNYOYMD
        , SALES.KSHNIPADRSE       = vRecordS07T001T.KSHNIPADRSE
        , SALES.KSHNPGMID         = vRecordS07T001T.KSHNPGMID
    WHERE
            SALES.APKIHONCD       = vRecordS07T001T.APKIHONCD
        AND SALES.SHOPCD          = vRecordS07T001T.SHOPCD
        AND SALES.YUKOKBN         = '1'
        AND EXISTS(
            SELECT
                SEIKYU.APKIHONCD
            FROM
                S07T006T SEIKYU
            WHERE
                    SEIKYU.APKIHONCD      = SALES.APKIHONCD
                AND SEIKYU.SHOPCD         = SALES.SHOPCD
                AND SEIKYU.DNPYNO         = SALES.URIDNPYNO
                AND SEIKYU.SKYSHONO       = iSkyshono
                AND SEIKYU.SKYSHONOEDANO  = iSkyshonoedano
                AND SEIKYU.URINYKNKBN     = '0'
        )
    ;
  END UpdateS06T001TSeikyuFix;

  /*****************************************************************************/
  /* プロシージャ名  ：売上テーブル更新（消費税請求締解除）                    */
  /* 引数            ：iUridnpyno            売上伝票番号                      */
  /*                 ：iDnpyno               伝票番号                          */
  /*****************************************************************************/
  PROCEDURE UpdateS06T001TTaxSeikyuFixCancel(iUridnpyno IN S06T001T.URIDNPYNO%TYPE
                                            ,iDnpyno    IN S06T001T.DNPYNO%TYPE) IS
  BEGIN
    -- 売上テーブル更新（消費税請求締解除）
    UPDATE S06T001T SALES
    SET
          SALES.DNPYNO            = iDnpyno
-- ACCIS2023-5830 Start ABI
        , SALES.SKYSHOOTPTARINASHIKBN = '1'
-- ACCIS2023-5830 End ABI
        , SALES.KOSHAAPKIHONCD    = vRecordS07T001T.KOSHAAPKIHONCD
        , SALES.KOSHASHOPCD       = vRecordS07T001T.KOSHASHOPCD
        , SALES.KOSHACD           = vRecordS07T001T.KOSHACD
        , SALES.KSHNSYSTMYMD      = vRecordS07T001T.KSHNSYSTMYMD
        , SALES.KSHNSYSTMJIKAN    = vRecordS07T001T.KSHNSYSTMJIKAN
        , SALES.KSHNUNYOYMD       = vRecordS07T001T.KSHNUNYOYMD
        , SALES.KSHNIPADRSE       = vRecordS07T001T.KSHNIPADRSE
        , SALES.KSHNPGMID         = vRecordS07T001T.KSHNPGMID
    WHERE
            SALES.APKIHONCD       = vRecordS07T001T.APKIHONCD
        AND SALES.SHOPCD          = vRecordS07T001T.SHOPCD
        AND SALES.URIDNPYNO       = iUridnpyno
        AND SALES.SKYPRCSJISSIKBN = 1
        AND SALES.YUKOKBN         = '1'
    ;
  END UpdateS06T001TTaxSeikyuFixCancel;

  /*****************************************************************************/
  /* プロシージャ名  ：売上テーブル更新（請求締解除）                          */
  /* 引数            ：iUridnpyno            売上伝票番号                      */
  /*****************************************************************************/
  PROCEDURE UpdateS06T001TSeikyuFixCancel(iUridnpyno IN S06T001T.URIDNPYNO%TYPE) IS
  BEGIN
    -- 売上テーブル更新（請求締解除）
    UPDATE S06T001T SALES
    SET
          SALES.SKYSHIMEDYDM      = NULL
        , SALES.SKYSHIMEDD        = NULL
        , SALES.SKYPRCSJISSIDD    = '00000000'
        , SALES.SKYPRCSJISSIKBN   = 0
        , SALES.KOSHAAPKIHONCD    = vRecordS07T001T.KOSHAAPKIHONCD
        , SALES.KOSHASHOPCD       = vRecordS07T001T.KOSHASHOPCD
        , SALES.KOSHACD           = vRecordS07T001T.KOSHACD
        , SALES.KSHNSYSTMYMD      = vRecordS07T001T.KSHNSYSTMYMD
        , SALES.KSHNSYSTMJIKAN    = vRecordS07T001T.KSHNSYSTMJIKAN
        , SALES.KSHNUNYOYMD       = vRecordS07T001T.KSHNUNYOYMD
        , SALES.KSHNIPADRSE       = vRecordS07T001T.KSHNIPADRSE
        , SALES.KSHNPGMID         = vRecordS07T001T.KSHNPGMID
    WHERE
            SALES.APKIHONCD       = vRecordS07T001T.APKIHONCD
        AND SALES.SHOPCD          = vRecordS07T001T.SHOPCD
        AND SALES.URIDNPYNO       = iUridnpyno
        AND SALES.SKYPRCSJISSIKBN = 1
        AND SALES.YUKOKBN         = '1'
    ;
  END UpdateS06T001TSeikyuFixCancel;

  /*****************************************************************************/
  /* プロシージャ名  ：売上テーブル生成（消費税・黒伝票の登録）                */
  /* 引数            ：iUridnpyno            売上伝票番号                      */
  /*****************************************************************************/
  PROCEDURE InsertS06T001TSeikyuFix(iUridnpyno IN S06T001T.URIDNPYNO%TYPE) IS
  BEGIN
    -- 売上テーブル生成（消費税・黒伝票の登録）
    INSERT INTO S06T001T(
          APKIHONCD
        , SHOPCD
        , URIDNPYNO
        , EDANO
        , ACPTDNPYNO
        , ACPTDNPYEDANO
        , TORIHIKIKBN
        , TOKUISAKICD
        , SIIRESAKICD
        , NYUKAMOTOCD
        , URIDD
        , ACPTDD
        , URIKIN
        , SHOHITAXKIN
        , SANKOUSHOHITAXKIN
        , ACPTTNSHACD
        , DLVRYTNSHACD
        , DLVRYKANKATSUAPKIHONCD
        , DLVRYKANKATSUSHOPCD
        , DLVRYKYOTENCD
        , DLVRYKBN
        , DLVRYCMPNYCD
        , SHUKKAMOTOKBN
        , EIGYOTNSHACD
        , JISSEKITNSHACD
        , SKYSAKICD
        , SKYSHIMEDYDM
        , SKYSHIMEDD
        , SKYPRCSJISSIDD
        , GETSUJISHIMEDYDM
        , GETSUJISHIMEDD
        , GETSUJIPRCSJISSIDD
        , URISHIMEPRCSDD
        , NEBIKISHIMEDYDM
        , NEBIKISHIMEDD
        , NEBIKIPRCSJISSIDD
        , DNPYBIKO
        , DNPYNO
        , SKYSHOOTPTARINASHIKBN
        , SKYPRCSJISSIKBN
        , DATSKSMOTOKBN
        , TAXKEISANKBN
        , SIIREDNPYNO
        , NYKNDNPYNO
        , NYKNKIN
        , NYKNKIND
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
          vRecordS07T001T.APKIHONCD
        , vRecordS07T001T.SHOPCD
        , iUridnpyno
        , 0
        , NULL
        , NULL
        , '1'   -- （売上）
        , vRecordS07T001T.TOKUISAKICD
        , NULL
        , NULL
        , vRecordS07T001T.KONKAISHIMEDD
        , '00000000'
        , 0
        -- modify 軽減税率変更対応_LI 20261127 Start
        , vRecordS07T001T.N8PERSHOHITAXKIN + vRecordS07T001T.N10PERSHOHITAXKIN + vRecordS07T001T.OLDKEIGENSHOHITAXKIN + vRecordS07T001T.OLDIPPANSHOHITAXKIN
        -- modify 軽減税率変更対応_LI 20261127 End
        , 0
        , NULL
        , NULL
        , NULL
        , NULL
        , NULL
        , NULL
        , NULL
        , '2'
        , NULL
        , NULL
        , vRecordS07T001T.SKYSAKICD
        , vRecordS07T001T.SHIMEDYDM
        , vRecordS07T001T.SHIMEDD
        , vRecordS07T001T.SHIMEPRCSJISSIDD
        , NULL
        , NULL
        , '00000000'
        , vRecordS07T001T.SHIMEPRCSJISSIDD
        , NULL
        , NULL
        , '00000000'
        , NULL
        , NULL
        , '0'   -- （出力対象）
        , 1     -- （月締請求処理）
        , '23'  -- （消費税算出）
        , '2'   -- （外税）
        , NULL
        , NULL
        , NULL
        , NULL
        , '1'   -- （有効）
        , vRecordS07T001T.TOSHAAPKIHONCD
        , vRecordS07T001T.TOSHASHOPCD
        , vRecordS07T001T.TOSHACD
        , vRecordS07T001T.INPTSYSTMYMD
        , vRecordS07T001T.INPTSYSTMJIKAN
        , vRecordS07T001T.INPTUNYOYMD
        , vRecordS07T001T.INPTIPADRSE
        , vRecordS07T001T.INPTPGMID
        , vRecordS07T001T.KOSHAAPKIHONCD
        , vRecordS07T001T.KOSHASHOPCD
        , vRecordS07T001T.KOSHACD
        , vRecordS07T001T.KSHNSYSTMYMD
        , vRecordS07T001T.KSHNSYSTMJIKAN
        , vRecordS07T001T.KSHNUNYOYMD
        , vRecordS07T001T.KSHNIPADRSE
        , vRecordS07T001T.KSHNPGMID
    );
  END InsertS06T001TSeikyuFix;

  /*****************************************************************************/
  /* プロシージャ名  ：売上テーブル生成（消費税・赤伝票の登録）                */
  /* 引数            ：iS06T001T             売上テーブルレコード              */
  /*                 ：iUridnpyno            売上伝票番号                      */
  /*****************************************************************************/
  PROCEDURE InsertS06T001TSeikyuFixCancel(iS06T001T  IN S06T001T%ROWTYPE
                                         ,iUridnpyno IN S06T001T.URIDNPYNO%TYPE) IS
  BEGIN
    -- 売上テーブル生成（消費税・赤伝票の登録）
    INSERT INTO S06T001T(
          APKIHONCD
        , SHOPCD
        , URIDNPYNO
        , EDANO
        , ACPTDNPYNO
        , ACPTDNPYEDANO
        , TORIHIKIKBN
        , TOKUISAKICD
        , SIIRESAKICD
        , NYUKAMOTOCD
        , URIDD
        , ACPTDD
        , URIKIN
        , SHOHITAXKIN
        , SANKOUSHOHITAXKIN
        , ACPTTNSHACD
        , DLVRYTNSHACD
        , DLVRYKANKATSUAPKIHONCD
        , DLVRYKANKATSUSHOPCD
        , DLVRYKYOTENCD
        , DLVRYKBN
        , DLVRYCMPNYCD
        , SHUKKAMOTOKBN
        , EIGYOTNSHACD
        , JISSEKITNSHACD
        , SKYSAKICD
        , SKYSHIMEDYDM
        , SKYSHIMEDD
        , SKYPRCSJISSIDD
        , GETSUJISHIMEDYDM
        , GETSUJISHIMEDD
        , GETSUJIPRCSJISSIDD
        , URISHIMEPRCSDD
        , NEBIKISHIMEDYDM
        , NEBIKISHIMEDD
        , NEBIKIPRCSJISSIDD
        , DNPYBIKO
        , DNPYNO
        , SKYSHOOTPTARINASHIKBN
        , SKYPRCSJISSIKBN
        , DATSKSMOTOKBN
        , TAXKEISANKBN
        , SIIREDNPYNO
        , NYKNDNPYNO
        , NYKNKIN
        , NYKNKIND
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
          iS06T001T.APKIHONCD
        , iS06T001T.SHOPCD
        , iUridnpyno
        , iS06T001T.EDANO
        , iS06T001T.ACPTDNPYNO
        , iS06T001T.ACPTDNPYEDANO
        , iS06T001T.TORIHIKIKBN
        , iS06T001T.TOKUISAKICD
        , iS06T001T.SIIRESAKICD
        , iS06T001T.NYUKAMOTOCD
        , iS06T001T.URIDD
        , iS06T001T.ACPTDD
        , iS06T001T.URIKIN
        , iS06T001T.SHOHITAXKIN * -1
        , iS06T001T.SANKOUSHOHITAXKIN
        , iS06T001T.ACPTTNSHACD
        , iS06T001T.DLVRYTNSHACD
        , iS06T001T.DLVRYKANKATSUAPKIHONCD
        , iS06T001T.DLVRYKANKATSUSHOPCD
        , iS06T001T.DLVRYKYOTENCD
        , iS06T001T.DLVRYKBN
        , iS06T001T.DLVRYCMPNYCD
        , iS06T001T.SHUKKAMOTOKBN
        , iS06T001T.EIGYOTNSHACD
        , iS06T001T.JISSEKITNSHACD
        , iS06T001T.SKYSAKICD
        , iS06T001T.SKYSHIMEDYDM
        , iS06T001T.SKYSHIMEDD
        , iS06T001T.SKYPRCSJISSIDD
        , NULL
        , NULL
        , '00000000'
        , iS06T001T.URISHIMEPRCSDD
        , iS06T001T.NEBIKISHIMEDYDM
        , iS06T001T.NEBIKISHIMEDD
        , iS06T001T.NEBIKIPRCSJISSIDD
        , iS06T001T.DNPYBIKO
        , iS06T001T.URIDNPYNO
-- ACCIS2023-5830 Start ABI
        , '1' -- （出力なし）
-- ACCIS2023-5830 End ABI
        , 1
        , iS06T001T.DATSKSMOTOKBN
        , iS06T001T.TAXKEISANKBN
        , iS06T001T.SIIREDNPYNO
        , iS06T001T.NYKNDNPYNO
        , iS06T001T.NYKNKIN
        , iS06T001T.NYKNKIND
        , iS06T001T.YUKOKBN
        , vRecordS07T001T.TOSHAAPKIHONCD
        , vRecordS07T001T.TOSHASHOPCD
        , vRecordS07T001T.TOSHACD
        , vRecordS07T001T.INPTSYSTMYMD
        , vRecordS07T001T.INPTSYSTMJIKAN
        , vRecordS07T001T.INPTUNYOYMD
        , vRecordS07T001T.INPTIPADRSE
        , vRecordS07T001T.INPTPGMID
        , vRecordS07T001T.KOSHAAPKIHONCD
        , vRecordS07T001T.KOSHASHOPCD
        , vRecordS07T001T.KOSHACD
        , vRecordS07T001T.KSHNSYSTMYMD
        , vRecordS07T001T.KSHNSYSTMJIKAN
        , vRecordS07T001T.KSHNUNYOYMD
        , vRecordS07T001T.KSHNIPADRSE
        , vRecordS07T001T.KSHNPGMID
    );
  END InsertS06T001TSeikyuFixCancel;

  /*****************************************************************************/
  /* プロシージャ名  ：売上明細テーブル更新（消費税請求締解除）                */
  /* 引数            ：iUridnpyno            売上伝票番号                      */
  /*****************************************************************************/
  PROCEDURE UpdateS06T002TSeikyuFixCancel(iUridnpyno IN S06T001T.URIDNPYNO%TYPE) IS
  BEGIN
    -- 売上明細テーブル更新（消費税請求締解除）
    UPDATE S06T002T SALES
    SET
          SALES.MEISAIBIKO        = '締解除取消'
        , SALES.KOSHAAPKIHONCD    = vRecordS07T001T.KOSHAAPKIHONCD
        , SALES.KOSHASHOPCD       = vRecordS07T001T.KOSHASHOPCD
        , SALES.KOSHACD           = vRecordS07T001T.KOSHACD
        , SALES.KSHNSYSTMYMD      = vRecordS07T001T.KSHNSYSTMYMD
        , SALES.KSHNSYSTMJIKAN    = vRecordS07T001T.KSHNSYSTMJIKAN
        , SALES.KSHNUNYOYMD       = vRecordS07T001T.KSHNUNYOYMD
        , SALES.KSHNIPADRSE       = vRecordS07T001T.KSHNIPADRSE
        , SALES.KSHNPGMID         = vRecordS07T001T.KSHNPGMID
    WHERE
            SALES.APKIHONCD       = vRecordS07T001T.APKIHONCD
        AND SALES.SHOPCD          = vRecordS07T001T.SHOPCD
        AND SALES.URIDNPYNO       = iUridnpyno
        AND SALES.YUKOKBN         = '1'
    ;
  END UpdateS06T002TSeikyuFixCancel;

  /*****************************************************************************/
  /* プロシージャ名  ：売上明細テーブル生成（消費税・黒伝票の登録）            */
  /* 引数            ：iUridnpyno            売上伝票番号                      */
  /*****************************************************************************/
  PROCEDURE InsertS06T002TSeikyuFix(iUridnpyno IN S06T001T.URIDNPYNO%TYPE) IS
  BEGIN
    -- 売上明細テーブル生成（消費税・黒伝票の登録）
    IF vRecordS07T001T.N10PERSHOHITAXKIN != 0 THEN
      INSERT INTO S06T002T(
            APKIHONCD
          , SHOPCD
          , URIDNPYNO
          , EDANO
          , URIDNPYMEISAINO
          , URIDNPYMEISAIEDANO
          , LCTGRYCD
          , MCTGRYCD
          , SCTGRYCD
          , SHNCD
          , KEIJOKBN
          , SURYO
          , HYOJUNUNT
          , UNT
          , KIN
          , UNTSHOHITAXKIN
          , SHOHITAXKIN
          , SANKOUSHOHITAXKIN
          , TAXKBN
          , MEISAIKBN
          , NYURYOKUMEISAIFLG
          , TANPINMEISAIFLG
          , MEISAIBIKO
          , STSHNCD
          , KATASIKI
          , SIIRESURYO
          , SIIREKIN
          , SIIREUNT
          , SIIRESHOHITAXKIN
          , ZAIKOKSHNSUMIFLG
          , TOKUISAKIKBN
          , JUTAKUDLVRYCSTKBN
          , ROYALTYSHIMEYMD
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
      )
      SELECT
            vRecordS07T001T.APKIHONCD
          , vRecordS07T001T.SHOPCD
          , iUridnpyno
          , 0
          , 1
          , 1
          , '99'      -- （消費税）
          , '001'     -- （消費税）
          , '001'     -- （消費税）
          , '099901'  -- （消費税）
          , '01'      -- （売上）
          , 1
          , 0
          , 0
          , 0
          , 0
          , vRecordS07T001T.N10PERSHOHITAXKIN
          , 0
          , '1'       -- （課税）
          , '0'       -- （商品）
          , '1'       -- （入力行）
          , '1'       -- （単品明細行）
          , NULL
          , NULL
          , NULL
          , 0
          , 0
          , 0
          , 0
          , '1'
          , TOKUISAKIKBN
          , JUTAKUDLVRYCSTKBN
          , '00000000'
          , '1'       -- （有効）
          , vRecordS07T001T.TOSHAAPKIHONCD
          , vRecordS07T001T.TOSHASHOPCD
          , vRecordS07T001T.TOSHACD
          , vRecordS07T001T.INPTSYSTMYMD
          , vRecordS07T001T.INPTSYSTMJIKAN
          , vRecordS07T001T.INPTUNYOYMD
          , vRecordS07T001T.INPTIPADRSE
          , vRecordS07T001T.INPTPGMID
          , vRecordS07T001T.KOSHAAPKIHONCD
          , vRecordS07T001T.KOSHASHOPCD
          , vRecordS07T001T.KOSHACD
          , vRecordS07T001T.KSHNSYSTMYMD
          , vRecordS07T001T.KSHNSYSTMJIKAN
          , vRecordS07T001T.KSHNUNYOYMD
          , vRecordS07T001T.KSHNIPADRSE
          , vRecordS07T001T.KSHNPGMID
      FROM
          S02M013T
      WHERE
              APKIHONCD   = vRecordS07T001T.APKIHONCD
          AND SHOPCD      = vRecordS07T001T.SHOPCD
          AND TOKUISAKICD = vRecordS07T001T.TOKUISAKICD
          AND YUKOKBN     = '1'
      ;
    END IF;

    -- 売上明細テーブル生成（軽減消費税・黒伝票の登録）
    IF vRecordS07T001T.N8PERSHOHITAXKIN != 0 THEN
      INSERT INTO S06T002T(
            APKIHONCD
          , SHOPCD
          , URIDNPYNO
          , EDANO
          , URIDNPYMEISAINO
          , URIDNPYMEISAIEDANO
          , LCTGRYCD
          , MCTGRYCD
          , SCTGRYCD
          , SHNCD
          , KEIJOKBN
          , SURYO
          , HYOJUNUNT
          , UNT
          , KIN
          , UNTSHOHITAXKIN
          , SHOHITAXKIN
          , SANKOUSHOHITAXKIN
          , TAXKBN
          , MEISAIKBN
          , NYURYOKUMEISAIFLG
          , TANPINMEISAIFLG
          , MEISAIBIKO
          , STSHNCD
          , KATASIKI
          , SIIRESURYO
          , SIIREKIN
          , SIIREUNT
          , SIIRESHOHITAXKIN
          , ZAIKOKSHNSUMIFLG
          , TOKUISAKIKBN
          , JUTAKUDLVRYCSTKBN
          , ROYALTYSHIMEYMD
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
      )
      SELECT
            vRecordS07T001T.APKIHONCD
          , vRecordS07T001T.SHOPCD
          , iUridnpyno
          , 0
          , 2
          , 1
          , '99'      -- （消費税）
          , '001'     -- （消費税）
          , '001'     -- （消費税）
          , '099902'  -- （軽減消費税）
          , '01'      -- （売上）
          , 1
          , 0
          , 0
          , 0
          , 0
          , vRecordS07T001T.N8PERSHOHITAXKIN
          -- modify 軽減税率変更対応_LI 20261127 Start
          , 0
          , '4'       -- （軽減税率）
          , '0'       -- （商品）
          , '1'       -- （入力行）
          , '1'       -- （単品明細行）
          , NULL
          , NULL
          , NULL
          , 0
          , 0
          , 0
          , 0
          , '1'
          , TOKUISAKIKBN
          , JUTAKUDLVRYCSTKBN
          , '00000000'
          , '1'       -- （有効）
          , vRecordS07T001T.TOSHAAPKIHONCD
          , vRecordS07T001T.TOSHASHOPCD
          , vRecordS07T001T.TOSHACD
          , vRecordS07T001T.INPTSYSTMYMD
          , vRecordS07T001T.INPTSYSTMJIKAN
          , vRecordS07T001T.INPTUNYOYMD
          , vRecordS07T001T.INPTIPADRSE
          , vRecordS07T001T.INPTPGMID
          , vRecordS07T001T.KOSHAAPKIHONCD
          , vRecordS07T001T.KOSHASHOPCD
          , vRecordS07T001T.KOSHACD
          , vRecordS07T001T.KSHNSYSTMYMD
          , vRecordS07T001T.KSHNSYSTMJIKAN
          , vRecordS07T001T.KSHNUNYOYMD
          , vRecordS07T001T.KSHNIPADRSE
          , vRecordS07T001T.KSHNPGMID
      FROM
          S02M013T
      WHERE
              APKIHONCD   = vRecordS07T001T.APKIHONCD
          AND SHOPCD      = vRecordS07T001T.SHOPCD
          AND TOKUISAKICD = vRecordS07T001T.TOKUISAKICD
          AND YUKOKBN     = '1'
      ;
    END IF;

    -- 売上明細テーブル生成（一般消費税・旧枠分、黒伝票の登録）
    IF vRecordS07T001T.OLDIPPANSHOHITAXKIN != 0 THEN
      INSERT INTO S06T002T(
            APKIHONCD
          , SHOPCD
          , URIDNPYNO
          , EDANO
          , URIDNPYMEISAINO
          , URIDNPYMEISAIEDANO
          , LCTGRYCD
          , MCTGRYCD
          , SCTGRYCD
          , SHNCD
          , KEIJOKBN
          , SURYO
          , HYOJUNUNT
          , UNT
          , KIN
          , UNTSHOHITAXKIN
          , SHOHITAXKIN
          , SANKOUSHOHITAXKIN
          , TAXKBN
          , MEISAIKBN
          , NYURYOKUMEISAIFLG
          , TANPINMEISAIFLG
          , MEISAIBIKO
          , STSHNCD
          , KATASIKI
          , SIIRESURYO
          , SIIREKIN
          , SIIREUNT
          , SIIRESHOHITAXKIN
          , ZAIKOKSHNSUMIFLG
          , TOKUISAKIKBN
          , JUTAKUDLVRYCSTKBN
          , ROYALTYSHIMEYMD
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
      )
      SELECT
            vRecordS07T001T.APKIHONCD
          , vRecordS07T001T.SHOPCD
          , iUridnpyno
          , 0
          , 3
          , 1
          , '99'      -- （消費税）
          , '001'     -- （消費税）
          , '001'     -- （消費税）
          , '099903'  -- （消費税）
          , '01'      -- （売上）
          , 1
          , 0
          , 0
          , 0
          , 0
          , vRecordS07T001T.OLDIPPANSHOHITAXKIN
          , 0
          , '1'       -- （課税）
          , '0'       -- （商品）
          , '1'       -- （入力行）
          , '1'       -- （単品明細行）
          , NULL
          , NULL
          , NULL
          , 0
          , 0
          , 0
          , 0
          , '1'
          , TOKUISAKIKBN
          , JUTAKUDLVRYCSTKBN
          , '00000000'
          , '1'       -- （有効）
          , vRecordS07T001T.TOSHAAPKIHONCD
          , vRecordS07T001T.TOSHASHOPCD
          , vRecordS07T001T.TOSHACD
          , vRecordS07T001T.INPTSYSTMYMD
          , vRecordS07T001T.INPTSYSTMJIKAN
          , vRecordS07T001T.INPTUNYOYMD
          , vRecordS07T001T.INPTIPADRSE
          , vRecordS07T001T.INPTPGMID
          , vRecordS07T001T.KOSHAAPKIHONCD
          , vRecordS07T001T.KOSHASHOPCD
          , vRecordS07T001T.KOSHACD
          , vRecordS07T001T.KSHNSYSTMYMD
          , vRecordS07T001T.KSHNSYSTMJIKAN
          , vRecordS07T001T.KSHNUNYOYMD
          , vRecordS07T001T.KSHNIPADRSE
          , vRecordS07T001T.KSHNPGMID
      FROM
          S02M013T
      WHERE
              APKIHONCD   = vRecordS07T001T.APKIHONCD
          AND SHOPCD      = vRecordS07T001T.SHOPCD
          AND TOKUISAKICD = vRecordS07T001T.TOKUISAKICD
          AND YUKOKBN     = '1'
      ;
    END IF;

    -- 売上明細テーブル生成（軽減消費税・黒伝票の登録）

    IF vRecordS07T001T.OLDKEIGENSHOHITAXKIN != 0 THEN
      INSERT INTO S06T002T(
            APKIHONCD
          , SHOPCD
          , URIDNPYNO
          , EDANO
          , URIDNPYMEISAINO
          , URIDNPYMEISAIEDANO
          , LCTGRYCD
          , MCTGRYCD
          , SCTGRYCD
          , SHNCD
          , KEIJOKBN
          , SURYO
          , HYOJUNUNT
          , UNT
          , KIN
          , UNTSHOHITAXKIN
          , SHOHITAXKIN
          , SANKOUSHOHITAXKIN
          , TAXKBN
          , MEISAIKBN
          , NYURYOKUMEISAIFLG
          , TANPINMEISAIFLG
          , MEISAIBIKO
          , STSHNCD
          , KATASIKI
          , SIIRESURYO
          , SIIREKIN
          , SIIREUNT
          , SIIRESHOHITAXKIN
          , ZAIKOKSHNSUMIFLG
          , TOKUISAKIKBN
          , JUTAKUDLVRYCSTKBN
          , ROYALTYSHIMEYMD
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
      )
      SELECT
            vRecordS07T001T.APKIHONCD
          , vRecordS07T001T.SHOPCD
          , iUridnpyno
          , 0
          , 4
          , 1
          , '99'      -- （消費税）
          , '001'     -- （消費税）
          , '001'     -- （消費税）
          , '099904'  -- （軽減消費税）
          , '01'      -- （売上）
          , 1
          , 0
          , 0
          , 0
          , 0
          , vRecordS07T001T.OLDKEIGENSHOHITAXKIN
          -- modify 軽減税率変更対応_LI 20261127 End
          , 0
          , '4'       -- （軽減税率）
          , '0'       -- （商品）
          , '1'       -- （入力行）
          , '1'       -- （単品明細行）
          , NULL
          , NULL
          , NULL
          , 0
          , 0
          , 0
          , 0
          , '1'
          , TOKUISAKIKBN
          , JUTAKUDLVRYCSTKBN
          , '00000000'
          , '1'       -- （有効）
          , vRecordS07T001T.TOSHAAPKIHONCD
          , vRecordS07T001T.TOSHASHOPCD
          , vRecordS07T001T.TOSHACD
          , vRecordS07T001T.INPTSYSTMYMD
          , vRecordS07T001T.INPTSYSTMJIKAN
          , vRecordS07T001T.INPTUNYOYMD
          , vRecordS07T001T.INPTIPADRSE
          , vRecordS07T001T.INPTPGMID
          , vRecordS07T001T.KOSHAAPKIHONCD
          , vRecordS07T001T.KOSHASHOPCD
          , vRecordS07T001T.KOSHACD
          , vRecordS07T001T.KSHNSYSTMYMD
          , vRecordS07T001T.KSHNSYSTMJIKAN
          , vRecordS07T001T.KSHNUNYOYMD
          , vRecordS07T001T.KSHNIPADRSE
          , vRecordS07T001T.KSHNPGMID
      FROM
          S02M013T
      WHERE
              APKIHONCD   = vRecordS07T001T.APKIHONCD
          AND SHOPCD      = vRecordS07T001T.SHOPCD
          AND TOKUISAKICD = vRecordS07T001T.TOKUISAKICD
          AND YUKOKBN     = '1'
      ;
    END IF;
  END InsertS06T002TSeikyuFix;

  /*****************************************************************************/
  /* プロシージャ名  ：売上明細テーブル生成（消費税・赤伝票の登録）            */
  /* 引数            ：iApkihoncd            AP基本コード                      */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iUridnpyno            売上伝票番号                      */
  /*                 ：iNewUridnpyno         （生成データ用）売上伝票番号      */
  /*****************************************************************************/
  PROCEDURE InsertS06T002TSeikyuFixCancel(iApkihoncd    IN S06T002T.APKIHONCD%TYPE
                                         ,iShopcd       IN S06T002T.SHOPCD%TYPE
                                         ,iUridnpyno    IN S06T001T.URIDNPYNO%TYPE
                                         ,iNewUridnpyno IN S06T001T.URIDNPYNO%TYPE) IS
  BEGIN
    -- 売上明細テーブル生成（消費税・赤伝票の登録）
    INSERT INTO S06T002T(
          APKIHONCD
        , SHOPCD
        , URIDNPYNO
        , EDANO
        , URIDNPYMEISAINO
        , URIDNPYMEISAIEDANO
        , LCTGRYCD
        , MCTGRYCD
        , SCTGRYCD
        , SHNCD
        , KEIJOKBN
        , SURYO
        , HYOJUNUNT
        , UNT
        , KIN
        , UNTSHOHITAXKIN
        , SHOHITAXKIN
        , SANKOUSHOHITAXKIN
        , TAXKBN
        , MEISAIKBN
        , NYURYOKUMEISAIFLG
        , TANPINMEISAIFLG
        , MEISAIBIKO
        , STSHNCD
        , KATASIKI
        , SIIRESURYO
        , SIIREKIN
        , SIIREUNT
        , SIIRESHOHITAXKIN
        , ZAIKOKSHNSUMIFLG
        , TOKUISAKIKBN
        , JUTAKUDLVRYCSTKBN
        , ROYALTYSHIMEYMD
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
    )
    SELECT
          UMST.APKIHONCD
        , UMST.SHOPCD
        , iNewUridnpyno
        , UMST.EDANO
        , UMST.URIDNPYMEISAINO
        , UMST.URIDNPYMEISAIEDANO
        , UMST.LCTGRYCD
        , UMST.MCTGRYCD
        , UMST.SCTGRYCD
        , UMST.SHNCD
        , UMST.KEIJOKBN
        , UMST.SURYO * -1
        , UMST.HYOJUNUNT
        , UMST.UNT
        , UMST.KIN
        , UMST.UNTSHOHITAXKIN
        , UMST.SHOHITAXKIN * -1
        , UMST.SANKOUSHOHITAXKIN
        , UMST.TAXKBN
        , UMST.MEISAIKBN
        , UMST.NYURYOKUMEISAIFLG
        , UMST.TANPINMEISAIFLG
        , '締解除取消'
        , UMST.STSHNCD
        , UMST.KATASIKI
        , UMST.SIIRESURYO
        , UMST.SIIREKIN
        , UMST.SIIREUNT
        , UMST.SIIRESHOHITAXKIN
        , UMST.ZAIKOKSHNSUMIFLG
        , TKSM.TOKUISAKIKBN
        , TKSM.JUTAKUDLVRYCSTKBN
        , '00000000'
        , UMST.YUKOKBN
        , vRecordS07T001T.TOSHAAPKIHONCD
        , vRecordS07T001T.TOSHASHOPCD
        , vRecordS07T001T.TOSHACD
        , vRecordS07T001T.INPTSYSTMYMD
        , vRecordS07T001T.INPTSYSTMJIKAN
        , vRecordS07T001T.INPTUNYOYMD
        , vRecordS07T001T.INPTIPADRSE
        , vRecordS07T001T.INPTPGMID
        , vRecordS07T001T.KOSHAAPKIHONCD
        , vRecordS07T001T.KOSHASHOPCD
        , vRecordS07T001T.KOSHACD
        , vRecordS07T001T.KSHNSYSTMYMD
        , vRecordS07T001T.KSHNSYSTMJIKAN
        , vRecordS07T001T.KSHNUNYOYMD
        , vRecordS07T001T.KSHNIPADRSE
        , vRecordS07T001T.KSHNPGMID
    FROM
        S06T002T UMST
        INNER JOIN S06T001T HEAD ON (
              HEAD.APKIHONCD    = UMST.APKIHONCD
          AND HEAD.SHOPCD       = UMST.SHOPCD
          AND HEAD.URIDNPYNO    = UMST.URIDNPYNO
          AND HEAD.EDANO        = UMST.EDANO
          AND HEAD.YUKOKBN      = '1'
        )
        INNER JOIN S02M013T TKSM ON (
              TKSM.APKIHONCD    = HEAD.APKIHONCD
          AND TKSM.SHOPCD       = HEAD.SHOPCD
          AND TKSM.TOKUISAKICD  = HEAD.TOKUISAKICD
          AND TKSM.YUKOKBN      = '1'
        )
    WHERE
            UMST.APKIHONCD      = iApkihoncd
        AND UMST.SHOPCD         = iShopcd
        AND UMST.URIDNPYNO      = iUridnpyno
        AND UMST.YUKOKBN        = '1'
    ;
  END InsertS06T002TSeikyuFixCancel;

  /*****************************************************************************/
  /* プロシージャ名  ：請求先マスタ更新（売上調整金額）                        */
  /* 引数            ：iUriageTyouseiKin     売上調整金額                      */
  /* 引数            ：iSkysakicd            請求先コード                      */
  /*****************************************************************************/
  PROCEDURE UpdateS02M017TUrichoseikin(iUrichoseikin IN S02M017T.URICHOSEIKIN%TYPE
                                       ,iSkysakicd   IN S02M017T.SKYSAKICD%TYPE) IS
  BEGIN
    -- 請求先マスタの更新（売上調整金額）
    UPDATE S02M017T
      SET
            URICHOSEIKIN    = iUrichoseikin
          , KOSHAAPKIHONCD  = vRecordS07T001T.KOSHAAPKIHONCD
          , KOSHASHOPCD     = vRecordS07T001T.KOSHASHOPCD
          , KOSHACD         = vRecordS07T001T.KOSHACD
          , KSHNSYSTMYMD    = vRecordS07T001T.KSHNSYSTMYMD
          , KSHNSYSTMJIKAN  = vRecordS07T001T.KSHNSYSTMJIKAN
          , KSHNUNYOYMD     = vRecordS07T001T.KSHNUNYOYMD
          , KSHNIPADRSE     = vRecordS07T001T.KSHNIPADRSE
          , KSHNPGMID       = vRecordS07T001T.KSHNPGMID
      WHERE
              APKIHONCD     = vRecordS07T001T.APKIHONCD
          AND SHOPCD        = vRecordS07T001T.SHOPCD
          AND SKYSAKICD     = iSkysakicd
    ;
  END UpdateS02M017TUrichoseikin;

  /*****************************************************************************/
  /* ファンクション名：売上調整金額取得                                        */
  /* 引数            ：iApkihoncd            AP基本コード                      */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iSkysakicd            請求先コード                      */
  /* 戻り値          ：売上調整金額                                            */
  /*****************************************************************************/
  FUNCTION GetUrichoseikin(iApkihoncd  IN VARCHAR2
                           ,iShopcd    IN VARCHAR2
                           ,iSkysakicd IN VARCHAR2) RETURN NUMBER IS
  ---------------------------
  -- 変数定義
  ---------------------------
  wUrichoseikin NUMBER; -- 売上調整金額
  BEGIN
    SELECT
        SUM(KESHIKOMIZD)
    INTO
        wUrichoseikin
    FROM
        S07T001T
    WHERE
        APKIHONCD       = iApkihoncd
        and SHOPCD      = iShopcd
        and SKYSAKICD   = iSkysakicd
        and KESHIKOMIZD < 0
        and YUKOKBN     = '1'
    ;
    IF wUrichoseikin IS NULL THEN
      RETURN 0;
    ELSE
      RETURN wUrichoseikin;
    END IF;
  END GetUrichoseikin;

  /*****************************************************************************/
  /* プロシージャ名  ：請求締処理管理テーブル更新（終了／待機）                */
  /*****************************************************************************/
  PROCEDURE UpdateS07T004TProcessEnd IS
    -- ACCIS2023-6671 start-ABI
    -- 自律型トランザクション プラグマ
    PRAGMA AUTONOMOUS_TRANSACTION;
    -- ACCIS2023-6671 end-ABI
  BEGIN
    -- 請求締処理管理テーブル更新（処理状態：「終了／待機」）
    UPDATE S07T004T
    SET
          PRCSJOTAI       = vPRCSJOTAI_COMPLETE
        , PRCSSHRYDD      = CASE WHEN RECOVSTRTDD IS NULL THEN TO_CHAR(CURRENT_DATE, 'YYYYMMDD') ELSE PRCSSHRYDD END
        , PRCSSHRYJIKAN   = CASE WHEN RECOVSTRTDD IS NULL THEN TO_CHAR(CURRENT_DATE, 'HH24MISS') ELSE PRCSSHRYJIKAN END
        , RECOVSHRYDD     = CASE WHEN RECOVSTRTDD IS NULL THEN NULL ELSE TO_CHAR(CURRENT_DATE, 'YYYYMMDD') END
        , RECOVSHRYJIKAN  = CASE WHEN RECOVSTRTDD IS NULL THEN NULL ELSE TO_CHAR(CURRENT_DATE, 'HH24MISS') END
        , KOSHAAPKIHONCD  = vRecordS07T001T.KOSHAAPKIHONCD
        , KOSHASHOPCD     = vRecordS07T001T.KOSHASHOPCD
        , KOSHACD         = vRecordS07T001T.KOSHACD
        , KSHNSYSTMYMD    = vRecordS07T001T.KSHNSYSTMYMD
        , KSHNSYSTMJIKAN  = vRecordS07T001T.KSHNSYSTMJIKAN
        , KSHNUNYOYMD     = vRecordS07T001T.KSHNUNYOYMD
        , KSHNIPADRSE     = vRecordS07T001T.KSHNIPADRSE
        , KSHNPGMID       = vRecordS07T001T.KSHNPGMID
    WHERE
            APKIHONCD     = vRecordS07T001T.APKIHONCD
        AND SHOPCD        = vRecordS07T001T.SHOPCD
    ;
    -- ACCIS2023-6671 start-ABI
    COMMIT;
    -- ACCIS2023-6671 end-ABI
  END UpdateS07T004TProcessEnd;

  /*****************************************************************************/
  /* プロシージャ名  ：請求締処理管理テーブル更新（エラー）                    */
  /* 引数            ：iMsg                 メッセージ                         */
  /*****************************************************************************/
  PROCEDURE UpdateS07T004TProcessError(iMsg IN S07T004T.MSG%TYPE) IS
    -- ACCIS2023-6671 start-ABI
    -- 自律型トランザクション プラグマ
    PRAGMA AUTONOMOUS_TRANSACTION;
    -- ACCIS2023-6671 end-ABI
  BEGIN
    -- 請求締処理管理テーブル更新（処理状態：「エラー」）
    UPDATE S07T004T
    SET
          PRCSJOTAI       = vPRCSJOTAI_ABORT
        , PRCSSHRYDD      = CASE WHEN RECOVSTRTDD IS NULL THEN TO_CHAR(CURRENT_DATE, 'YYYYMMDD') ELSE PRCSSHRYDD END
        , PRCSSHRYJIKAN   = CASE WHEN RECOVSTRTDD IS NULL THEN TO_CHAR(CURRENT_DATE, 'HH24MISS') ELSE PRCSSHRYJIKAN END
        , RECOVSHRYDD     = CASE WHEN RECOVSTRTDD IS NULL THEN NULL ELSE TO_CHAR(CURRENT_DATE, 'YYYYMMDD') END
        , RECOVSHRYJIKAN  = CASE WHEN RECOVSTRTDD IS NULL THEN NULL ELSE TO_CHAR(CURRENT_DATE, 'HH24MISS') END
        , MSG             = iMsg
        , KOSHAAPKIHONCD  = vRecordS07T001T.KOSHAAPKIHONCD
        , KOSHASHOPCD     = vRecordS07T001T.KOSHASHOPCD
        , KOSHACD         = vRecordS07T001T.KOSHACD
        , KSHNSYSTMYMD    = vRecordS07T001T.KSHNSYSTMYMD
        , KSHNSYSTMJIKAN  = vRecordS07T001T.KSHNSYSTMJIKAN
        , KSHNUNYOYMD     = vRecordS07T001T.KSHNUNYOYMD
        , KSHNIPADRSE     = vRecordS07T001T.KSHNIPADRSE
        , KSHNPGMID       = vRecordS07T001T.KSHNPGMID
    WHERE
            APKIHONCD     = vRecordS07T001T.APKIHONCD
        AND SHOPCD        = vRecordS07T001T.SHOPCD
    ;
    -- ACCIS2023-6671 start-ABI
    COMMIT;
    -- ACCIS2023-6671 end-ABI
  END UpdateS07T004TProcessError;

  /*****************************************************************************/
  /* プロシージャ名  ：請求締処理詳細管理テーブル更新・生成（処理開始）        */
  /* 引数            ：iPrcsid               処理ID                            */
  /*****************************************************************************/
  PROCEDURE UpdateS07T005TProcessStart(iPrcsid IN S07T005T.PRCSID%TYPE) IS
    -- ACCIS2023-6671 start-ABI
    -- 自律型トランザクション プラグマ
    PRAGMA AUTONOMOUS_TRANSACTION;
    -- ACCIS2023-6671 end-ABI
  BEGIN
    -- 請求締処理詳細管理テーブル更新・生成（処理状態：「処理開始」）
    MERGE INTO S07T005T
    USING DUAL ON(
          APKIHONCD       = vRecordS07T001T.APKIHONCD
      AND SHOPCD          = vRecordS07T001T.SHOPCD
      AND PRCSID          = iPrcsid
    )
    WHEN MATCHED THEN
      UPDATE SET
          PRCSJOTAI       = vPRCSJOTAI_START
        , PRCSSTRTDD      = NULL
        , PRCSSTRTJIKAN   = NULL
        , PRCSSHRYDD      = NULL
        , PRCSSHRYJIKAN   = NULL
        , RECOVSTRTDD     = NULL
        , RECOVSTRTJIKAN  = NULL
        , RECOVSHRYDD     = NULL
        , RECOVSHRYJIKAN  = NULL
        , MSG             = NULL
        , KOSHAAPKIHONCD  = vRecordS07T001T.KOSHAAPKIHONCD
        , KOSHASHOPCD     = vRecordS07T001T.KOSHASHOPCD
        , KOSHACD         = vRecordS07T001T.KOSHACD
        , KSHNSYSTMYMD    = vRecordS07T001T.KSHNSYSTMYMD
        , KSHNSYSTMJIKAN  = vRecordS07T001T.KSHNSYSTMJIKAN
        , KSHNUNYOYMD     = vRecordS07T001T.KSHNUNYOYMD
        , KSHNIPADRSE     = vRecordS07T001T.KSHNIPADRSE
        , KSHNPGMID       = vRecordS07T001T.KSHNPGMID
    WHEN NOT MATCHED THEN
      INSERT (
          APKIHONCD
        , SHOPCD
        , PRCSID
        , PRCSSTRTDD
        , PRCSSTRTJIKAN
        , PRCSSHRYDD
        , PRCSSHRYJIKAN
        , RECOVSTRTDD
        , RECOVSTRTJIKAN
        , RECOVSHRYDD
        , RECOVSHRYJIKAN
        , PRCSJOTAI
        , MSG
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
          vRecordS07T001T.APKIHONCD
        , vRecordS07T001T.SHOPCD
        , iPrcsid
        , NULL
        , NULL
        , NULL
        , NULL
        , NULL
        , NULL
        , NULL
        , NULL
        , vPRCSJOTAI_START
        , NULL
        , vRecordS07T001T.TOSHAAPKIHONCD
        , vRecordS07T001T.TOSHASHOPCD
        , vRecordS07T001T.TOSHACD
        , vRecordS07T001T.INPTSYSTMYMD
        , vRecordS07T001T.INPTSYSTMJIKAN
        , vRecordS07T001T.INPTUNYOYMD
        , vRecordS07T001T.INPTIPADRSE
        , vRecordS07T001T.INPTPGMID
        , vRecordS07T001T.KOSHAAPKIHONCD
        , vRecordS07T001T.KOSHASHOPCD
        , vRecordS07T001T.KOSHACD
        , vRecordS07T001T.KSHNSYSTMYMD
        , vRecordS07T001T.KSHNSYSTMJIKAN
        , vRecordS07T001T.KSHNUNYOYMD
        , vRecordS07T001T.KSHNIPADRSE
        , vRecordS07T001T.KSHNPGMID
      );
    -- ACCIS2023-6671 start-ABI
    COMMIT;
    -- ACCIS2023-6671 end-ABI
  END UpdateS07T005TProcessStart;

  /*****************************************************************************/
  /* プロシージャ名  ：請求締処理詳細管理テーブル更新（処理状態）              */
  /* 引数            ：iPrcsid              処理ID                             */
  /*                 ：iPrcsjotai           処理状態                           */
  /*                 ：iMsg                 メッセージ                         */
  /*****************************************************************************/
  PROCEDURE UpdateS07T005TProcessStatus(iPrcsid    IN S07T005T.PRCSID%TYPE
                                       ,iPrcsjotai IN S07T005T.PRCSJOTAI%TYPE
                                       ,iMsg       IN S07T005T.MSG%TYPE) IS
    -- ACCIS2023-6671 start-ABI
    -- 自律型トランザクション プラグマ
    PRAGMA AUTONOMOUS_TRANSACTION;
    -- ACCIS2023-6671 end-ABI
  BEGIN
    -- 処理状態：「実行中」の場合
    IF iPrcsjotai = vPRCSJOTAI_RUNNING THEN
      -- 請求締処理詳細管理テーブル更新（処理状態：「実行中」又は「リカバリ実行待」）
      UPDATE S07T005T
      SET
            PRCSJOTAI       = CASE PRCSJOTAI WHEN vPRCSJOTAI_START THEN vPRCSJOTAI_RUNNING ELSE vPRCSJOTAI_RECOV_RUNNING END
          , PRCSSTRTDD      = CASE PRCSJOTAI WHEN vPRCSJOTAI_START THEN TO_CHAR(CURRENT_DATE, 'YYYYMMDD') ELSE PRCSSTRTDD END
          , PRCSSTRTJIKAN   = CASE PRCSJOTAI WHEN vPRCSJOTAI_START THEN TO_CHAR(CURRENT_DATE, 'HH24MISS') ELSE PRCSSTRTJIKAN END
          , RECOVSTRTDD     = CASE PRCSJOTAI WHEN vPRCSJOTAI_RECOV_WAIT THEN TO_CHAR(CURRENT_DATE, 'YYYYMMDD') ELSE RECOVSTRTDD END
          , RECOVSTRTJIKAN  = CASE PRCSJOTAI WHEN vPRCSJOTAI_RECOV_WAIT THEN TO_CHAR(CURRENT_DATE, 'HH24MISS') ELSE RECOVSTRTJIKAN END
          , RECOVSHRYDD     = NULL
          , RECOVSHRYJIKAN  = NULL
          , MSG             = iMsg
          , KOSHAAPKIHONCD  = vRecordS07T001T.KOSHAAPKIHONCD
          , KOSHASHOPCD     = vRecordS07T001T.KOSHASHOPCD
          , KOSHACD         = vRecordS07T001T.KOSHACD
          , KSHNSYSTMYMD    = vRecordS07T001T.KSHNSYSTMYMD
          , KSHNSYSTMJIKAN  = vRecordS07T001T.KSHNSYSTMJIKAN
          , KSHNUNYOYMD     = vRecordS07T001T.KSHNUNYOYMD
          , KSHNIPADRSE     = vRecordS07T001T.KSHNIPADRSE
          , KSHNPGMID       = vRecordS07T001T.KSHNPGMID
      WHERE
              APKIHONCD     = vRecordS07T001T.APKIHONCD
          AND SHOPCD        = vRecordS07T001T.SHOPCD
          AND PRCSID        = iPrcsid;

    -- 処理状態：「終了／待機」、「エラー」の場合
    ELSIF iPrcsjotai IN (vPRCSJOTAI_COMPLETE, vPRCSJOTAI_ABORT) THEN
      -- 請求締処理詳細管理テーブル更新（処理状態：「終了／待機」又は「エラー」）
      UPDATE S07T005T
      SET
            PRCSJOTAI       = iPrcsjotai
          , PRCSSHRYDD      = CASE PRCSJOTAI WHEN vPRCSJOTAI_RUNNING THEN TO_CHAR(CURRENT_DATE, 'YYYYMMDD') ELSE PRCSSHRYDD END
          , PRCSSHRYJIKAN   = CASE PRCSJOTAI WHEN vPRCSJOTAI_RUNNING THEN TO_CHAR(CURRENT_DATE, 'HH24MISS') ELSE PRCSSHRYJIKAN END
          , RECOVSHRYDD     = CASE PRCSJOTAI WHEN vPRCSJOTAI_RECOV_RUNNING THEN TO_CHAR(CURRENT_DATE, 'YYYYMMDD') ELSE RECOVSHRYDD END
          , RECOVSHRYJIKAN  = CASE PRCSJOTAI WHEN vPRCSJOTAI_RECOV_RUNNING THEN TO_CHAR(CURRENT_DATE, 'HH24MISS') ELSE RECOVSHRYJIKAN END
          , MSG             = iMsg
          , KOSHAAPKIHONCD  = vRecordS07T001T.KOSHAAPKIHONCD
          , KOSHASHOPCD     = vRecordS07T001T.KOSHASHOPCD
          , KOSHACD         = vRecordS07T001T.KOSHACD
          , KSHNSYSTMYMD    = vRecordS07T001T.KSHNSYSTMYMD
          , KSHNSYSTMJIKAN  = vRecordS07T001T.KSHNSYSTMJIKAN
          , KSHNUNYOYMD     = vRecordS07T001T.KSHNUNYOYMD
          , KSHNIPADRSE     = vRecordS07T001T.KSHNIPADRSE
          , KSHNPGMID       = vRecordS07T001T.KSHNPGMID
      WHERE
              APKIHONCD     = vRecordS07T001T.APKIHONCD
          AND SHOPCD        = vRecordS07T001T.SHOPCD
          AND PRCSID        = iPrcsid;
    END IF;

    -- ACCIS2023-6671 start-ABI
    -- トランザクションコミット
    COMMIT;
    -- ACCIS2023-6671 end-ABI
  END UpdateS07T005TProcessStatus;

  /*****************************************************************************/
  /* プロシージャ名  ：請求明細関連テーブル生成（売上）                        */
  /* 引数            ：iSkyshono             請求書番号                        */
  /*                 ：iSkyshonoedano        請求書番号枝番                    */
  /*                 ：iUridnpyno            売上伝票番号                      */
  /*****************************************************************************/
  PROCEDURE InsertS07T006TBySales(iSkyshono      IN S07T001T.SKYSHONO%TYPE
                                 ,iSkyshonoedano IN S07T001T.SKYSHONOEDANO%TYPE
                                 ,iUridnpyno     IN S06T001T.URIDNPYNO%TYPE) IS
  BEGIN
    -- 請求明細関連テーブル生成（売上）
    INSERT INTO S07T006T(
          APKIHONCD
        , SHOPCD
        , SKYSHONO
        , SKYSHONOEDANO
        , DNPYNO
        , NYKNDNPYNOEDANO
        , URINYKNKBN
        , SKYSAKICD
        , TOKUISAKICD
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
    )
    SELECT
          vRecordS07T001T.APKIHONCD
        , vRecordS07T001T.SHOPCD
        , iSkyshono
        , iSkyshonoedano
        , URIDNPYNO
        , '0'
        , '0' -- （売上）
        , vRecordS07T001T.SKYSAKICD
        , vRecordS07T001T.TOKUISAKICD
        , '1' -- （有効）
        , vRecordS07T001T.TOSHAAPKIHONCD
        , vRecordS07T001T.TOSHASHOPCD
        , vRecordS07T001T.TOSHACD
        , vRecordS07T001T.INPTSYSTMYMD
        , vRecordS07T001T.INPTSYSTMJIKAN
        , vRecordS07T001T.INPTUNYOYMD
        , vRecordS07T001T.INPTIPADRSE
        , vRecordS07T001T.INPTPGMID
        , vRecordS07T001T.KOSHAAPKIHONCD
        , vRecordS07T001T.KOSHASHOPCD
        , vRecordS07T001T.KOSHACD
        , vRecordS07T001T.KSHNSYSTMYMD
        , vRecordS07T001T.KSHNSYSTMJIKAN
        , vRecordS07T001T.KSHNUNYOYMD
        , vRecordS07T001T.KSHNIPADRSE
        , vRecordS07T001T.KSHNPGMID
    FROM
        S06T001T
    WHERE
            APKIHONCD       = vRecordS07T001T.APKIHONCD
        AND SHOPCD          = vRecordS07T001T.SHOPCD
        AND TOKUISAKICD     = vRecordS07T001T.TOKUISAKICD
        AND URIDD           <= vRecordS07T001T.KONKAISHIMEDD
        AND URISHIMEPRCSDD  != '00000000'
        AND SKYPRCSJISSIDD  IN ('00000000', 'DAIBIKI')
        AND YUKOKBN         = '1'
        AND (iUridnpyno IS NULL OR URIDNPYNO = iUridnpyno)
    ;
  END InsertS07T006TBySales;

  /*****************************************************************************/
  /* プロシージャ名  ：請求明細関連テーブル生成（入金）                        */
  /* 引数            ：iSkyshono             請求書番号                        */
  /*                 ：iSkyshonoedano        請求書番号枝番                    */
  /*****************************************************************************/
  PROCEDURE InsertS07T006TByNyukin(iSkyshono      IN S07T001T.SKYSHONO%TYPE
                                  ,iSkyshonoedano IN S07T001T.SKYSHONOEDANO%TYPE) IS
  BEGIN
    -- 請求明細関連テーブル生成（入金）
    INSERT INTO S07T006T(
          APKIHONCD
        , SHOPCD
        , SKYSHONO
        , SKYSHONOEDANO
        , DNPYNO
        , NYKNDNPYNOEDANO
        , URINYKNKBN
        , SKYSAKICD
        , TOKUISAKICD
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
    )
    SELECT
          vRecordS07T001T.APKIHONCD
        , vRecordS07T001T.SHOPCD
        , iSkyshono
        , iSkyshonoedano
        , DNPYNO
        , NYKNDNPYNOEDANO
        , '1' -- （入金）
        , vRecordS07T001T.SKYSAKICD
        , vRecordS07T001T.TOKUISAKICD
        , '1' -- （有効）
        , vRecordS07T001T.TOSHAAPKIHONCD
        , vRecordS07T001T.TOSHASHOPCD
        , vRecordS07T001T.TOSHACD
        , vRecordS07T001T.INPTSYSTMYMD
        , vRecordS07T001T.INPTSYSTMJIKAN
        , vRecordS07T001T.INPTUNYOYMD
        , vRecordS07T001T.INPTIPADRSE
        , vRecordS07T001T.INPTPGMID
        , vRecordS07T001T.KOSHAAPKIHONCD
        , vRecordS07T001T.KOSHASHOPCD
        , vRecordS07T001T.KOSHACD
        , vRecordS07T001T.KSHNSYSTMYMD
        , vRecordS07T001T.KSHNSYSTMJIKAN
        , vRecordS07T001T.KSHNUNYOYMD
        , vRecordS07T001T.KSHNIPADRSE
        , vRecordS07T001T.KSHNPGMID
    FROM
        S10T001T
    WHERE
            APKIHONCD       = vRecordS07T001T.APKIHONCD
        AND SHOPCD          = vRecordS07T001T.SHOPCD
        AND SKYSAKICD       = vRecordS07T001T.SKYSAKICD
        AND TOKUISAKICD     = vRecordS07T001T.TOKUISAKICD
        AND NYKNDD          <= vRecordS07T001T.KONKAISHIMEDD
        AND URISHIMEPRCSDD  != '00000000'
        AND SKYPRCSJISSIDD  = '00000000'
        AND YUKOKBN         = '1'
    ;
  END InsertS07T006TByNyukin;

  /*****************************************************************************/
  /* プロシージャ名  ：請求明細関連テーブル生成                                */
  /* 引数            ：iSkyshono             請求書番号                        */
  /*                 ：iSkyshonoedano        請求書番号枝番                    */
  /*                 ：iUridnpyno            売上伝票番号                      */
  /*****************************************************************************/
  PROCEDURE InsertS07T006T(iSkyshono      IN S07T001T.SKYSHONO%TYPE
                          ,iSkyshonoedano IN S07T001T.SKYSHONOEDANO%TYPE
                          ,iUridnpyno     IN S06T001T.URIDNPYNO%TYPE) IS
  BEGIN
    -- 請求明細関連テーブル生成
    INSERT INTO S07T006T(
          APKIHONCD
        , SHOPCD
        , SKYSHONO
        , SKYSHONOEDANO
        , DNPYNO
        , NYKNDNPYNOEDANO
        , URINYKNKBN
        , SKYSAKICD
        , TOKUISAKICD
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
          vRecordS07T001T.APKIHONCD
        , vRecordS07T001T.SHOPCD
        , iSkyshono
        , iSkyshonoedano
        , iUridnpyno
        , '0' -- （売上）
        , '0' -- （売上）
        , vRecordS07T001T.SKYSAKICD
        , vRecordS07T001T.TOKUISAKICD
        , '1' -- （有効）
        , vRecordS07T001T.TOSHAAPKIHONCD
        , vRecordS07T001T.TOSHASHOPCD
        , vRecordS07T001T.TOSHACD
        , vRecordS07T001T.INPTSYSTMYMD
        , vRecordS07T001T.INPTSYSTMJIKAN
        , vRecordS07T001T.INPTUNYOYMD
        , vRecordS07T001T.INPTIPADRSE
        , vRecordS07T001T.INPTPGMID
        , vRecordS07T001T.KOSHAAPKIHONCD
        , vRecordS07T001T.KOSHASHOPCD
        , vRecordS07T001T.KOSHACD
        , vRecordS07T001T.KSHNSYSTMYMD
        , vRecordS07T001T.KSHNSYSTMJIKAN
        , vRecordS07T001T.KSHNUNYOYMD
        , vRecordS07T001T.KSHNIPADRSE
        , vRecordS07T001T.KSHNPGMID
    );
  END InsertS07T006T;

  /*****************************************************************************/
  /* プロシージャ名  ：請求締用対象得意先ワーク更新                            */
  /*****************************************************************************/
  PROCEDURE UpdateS07W006T IS
  BEGIN
    -- 請求締用対象得意先ワーク更新
    UPDATE S07W006T
    SET
          -- modify 軽減税率変更対応_LI 20261127 Start
          SHOHITAXKIN   = SHOHITAXKIN + vRecordS07T001T.N8PERSHOHITAXKIN + vRecordS07T001T.N10PERSHOHITAXKIN + vRecordS07T001T.OLDKEIGENSHOHITAXKIN + vRecordS07T001T.OLDIPPANSHOHITAXKIN
          -- modify 軽減税率変更対応_LI 20261127 End
    WHERE
            APKIHONCD   = vRecordS07T001T.APKIHONCD
        AND SHOPCD      = vRecordS07T001T.SHOPCD
        AND SKYSAKICD   = vRecordS07T001T.SKYSAKICD
        AND TOKUISAKICD = vRecordS07T001T.TOKUISAKICD
    ;
  END UpdateS07W006T;

  /*****************************************************************************/
  /* プロシージャ名  ：請求締用対象得意先ワーク更新（繰越金額）                */
  /* 引数            ：iZnkshimedd           前回締日                          */
  /*                 ：iKurikoshikin         繰越金額                          */
  /*****************************************************************************/
  PROCEDURE UpdateS07W006TKurikoshikin(iZnkshimedd   IN S07W006T.ZNKSHIMEDD%TYPE
                                      ,iKurikoshikin IN S07W006T.KURIKOSHIKIN%TYPE) IS
  BEGIN
    -- 請求締用対象得意先ワーク更新
    UPDATE S07W006T
    SET
          ZNKSHIMEDD      = iZnkshimedd
        , KURIKOSHIKIN    = iKurikoshikin
    WHERE
            APKIHONCD     = vRecordS07T001T.APKIHONCD
        AND SHOPCD        = vRecordS07T001T.SHOPCD
        AND SKYSAKICD     = vRecordS07T001T.SKYSAKICD
        AND TOKUISAKICD   = vRecordS07T001T.TOKUISAKICD
    ;
  END UpdateS07W006TKurikoshikin;

  /*****************************************************************************/
  /* プロシージャ名  ：請求締用対象得意先ワーク更新・生成                      */
  /*****************************************************************************/
  PROCEDURE MergeS07W006T IS
  BEGIN
    -- 請求締用対象得意先ワーク更新・生成
    MERGE INTO S07W006T
    USING DUAL ON(
          APKIHONCD       = vRecordS07T001T.APKIHONCD
      AND SHOPCD          = vRecordS07T001T.SHOPCD
      AND SKYSAKICD       = vRecordS07T001T.SKYSAKICD
      AND TOKUISAKICD     = vRecordS07T001T.TOKUISAKICD
    )
    WHEN MATCHED THEN
      UPDATE SET
          SHOHITAXKIN     = vRecordS07T001T.KONKAISHOHITAXKIN * -1
        , KOSHAAPKIHONCD  = vRecordS07T001T.KOSHAAPKIHONCD
        , KOSHASHOPCD     = vRecordS07T001T.KOSHASHOPCD
        , KOSHACD         = vRecordS07T001T.KOSHACD
        , KSHNSYSTMYMD    = vRecordS07T001T.KSHNSYSTMYMD
        , KSHNSYSTMJIKAN  = vRecordS07T001T.KSHNSYSTMJIKAN
        , KSHNUNYOYMD     = vRecordS07T001T.KSHNUNYOYMD
        , KSHNIPADRSE     = vRecordS07T001T.KSHNIPADRSE
        , KSHNPGMID       = vRecordS07T001T.KSHNPGMID
    WHEN NOT MATCHED THEN
      INSERT (
          APKIHONCD
        , SHOPCD
        , SKYSAKICD
        , TOKUISAKICD
        , ZNKSHIMEDD
        , SHIMESUMIFLG
        , KURIKOSHIKIN
        , SHOHITAXKIN
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
          vRecordS07T001T.APKIHONCD
        , vRecordS07T001T.SHOPCD
        , vRecordS07T001T.SKYSAKICD
        , vRecordS07T001T.TOKUISAKICD
        , NULL
        , NULL
        , NULL
        , vRecordS07T001T.KONKAISHOHITAXKIN * -1
        , vRecordS07T001T.TOSHAAPKIHONCD
        , vRecordS07T001T.TOSHASHOPCD
        , vRecordS07T001T.TOSHACD
        , vRecordS07T001T.INPTSYSTMYMD
        , vRecordS07T001T.INPTSYSTMJIKAN
        , vRecordS07T001T.INPTUNYOYMD
        , vRecordS07T001T.INPTIPADRSE
        , vRecordS07T001T.INPTPGMID
        , vRecordS07T001T.KOSHAAPKIHONCD
        , vRecordS07T001T.KOSHASHOPCD
        , vRecordS07T001T.KOSHACD
        , vRecordS07T001T.KSHNSYSTMYMD
        , vRecordS07T001T.KSHNSYSTMJIKAN
        , vRecordS07T001T.KSHNUNYOYMD
        , vRecordS07T001T.KSHNIPADRSE
        , vRecordS07T001T.KSHNPGMID
      );
  END MergeS07W006T;

  /****************************************************************************/
  /* プロシージャ名  ：請求残高テーブルデータ生成                             */
  /****************************************************************************/
  PROCEDURE InsertS07T001T IS
  BEGIN
    -- 請求残高テーブルデータ生成
    INSERT INTO S07T001T(
          APKIHONCD                               -- ＡＰ基本コード
        , SHOPCD                                  -- ショップコード
        , SKYSAKICD                               -- 請求先コード
        , TOKUISAKICD                             -- 得意先コード
        , SHIMEDYDM                               -- 締年月
        , SHIMEDD                                 -- 締日
        , RENBAN                                  -- 連番
        , SKYPRCSJISSIKBN                         -- 請求処理実施区分
        , SHIMEPRCSJISSIDD                        -- 締処理実施日
        , ZNKSHIMEDD                              -- 前回締日
        , KONKAISHIMEDD                           -- 今回締日
        , ZNKKURIKOSHIKIN                         -- 前回繰越金額
        , KONKAITAXNUKIURIKIN                     -- 今回税抜売上金額
        , KONKAISHOHITAXKIN                       -- 今回消費税金額
        , KONKAINYKNKIN                           -- 今回入金金額
        , KURIKOSHIKIN                            -- 繰越金額
        , KESHIKOMIKIN                            -- 消込金額
        , KESHIKOMIZD                             -- 消込残高
        , N8PERZEINUKIURIKIN                      -- ８％税抜売上金額
        , N8PERSHOHITAXKIN                        -- ８％消費税金額
        , N10PERZEINUKIURIKIN                     -- １０％税抜売上金額
        , N10PERSHOHITAXKIN                       -- １０％消費税金額
        -- add 軽減税率変更対応_LI 20261127 Start
        , OLDKEIGENZEINUKIURIKIN                  -- 軽減旧枠税抜売上金額（消費税改修）
        , OLDKEIGENSHOHITAXKIN                    -- 軽減旧枠消費税金額（消費税改修）
        , OLDIPPANZEINUKIURIKIN                   -- 一般旧枠税抜売上金額（消費税改修）
        , OLDIPPANSHOHITAXKIN                     -- 一般旧枠消費税金額（消費税改修）
        -- add 軽減税率変更対応_LI 20261127 End
        , TAXKEISANKBN                            -- 税計算区分
        , SKYICRNHYPRNTDD                         -- 請求一覧表印刷日
        , SKYSHOPRNTDD                            -- 請求書印刷日
        , SKYSHONO                                -- 請求書番号
        , SKYSHONOEDANO                           -- 請求書番号枝番
        , RIYOMEISAISHOPRNTDD                     -- 利用明細書印刷日
        , RIYOMEISAISHONO                         -- 利用明細書番号
        , TOKUSOKUJOPRNTDD                        -- 督促状印刷日
        , TOKUSOKUJONO                            -- 督促状番号
        , CSVDATCHUSYUTSUDD                       -- ＣＳＶデータ抽出日
        , RGRAYURYO                               -- レギュラー有料
        , RGRAMURYO                               -- レギュラー無料
        , SRMYURYO                                -- スリム有料
        , SRMMURYO                                -- スリム無料
        , SONOTAYURYO                             -- その他有料
        , SONOTAMURYO                             -- その他無料
        , SEIHINMIZUURI                           -- 製品水売上
        , RTLRKN                                  -- レンタル料金
        , SONOTAURIKIN                            -- その他売上金額
        , AQCRGRAKYNPNSURYO                       -- アクアクララレギュラーキャンペーン数量
        , AQCRGRASVSURYO                          -- アクアクララレギュラーサービス数量
        , AQCRGRAPUROMOSHONSURYO                  -- アクアクララレギュラープロモーション数量
        , AQCRGRASIINSURYO                        -- アクアクララレギュラー試飲数量
        , AQCRGRAMNTESURYO                        -- アクアクララレギュラーメンテ数量
        , AQCRGRAKOKANSURYO                       -- アクアクララレギュラー交換数量
        , AQCRGRAURISURYO                         -- アクアクララレギュラー売上数量
        , AQCSRMKYNPNSURYO                        -- アクアクララスリムキャンペーン数量
        , AQCSRMSVSURYO                           -- アクアクララスリムサービス数量
        , AQCSRMPUROMOSHONSURYO                   -- アクアクララスリムプロモーション数量
        , AQCSRMSIINSURYO                         -- アクアクララスリム試飲数量
        , AQCSRMMNTESURYO                         -- アクアクララスリムメンテ数量
        , AQCSRMKOKANSURYO                        -- アクアクララスリム交換数量
        , AQCSRMURISURYO                          -- アクアクララスリム売上数量
        , ETCSHNMZKYNPNSURYO                      -- その他製品水キャンペーン数量
        , ETCSHNMZSVSURYO                         -- その他製品水サービス数量
        , ETCSHNMZPUROMOSHONSURYO                 -- その他製品水プロモーション数量
        , ETCSHNMZSIINSURYO                       -- その他製品水試飲数量
        , ETCSHNMZMNTESURYO                       -- その他製品水メンテ数量
        , ETCSHNMZKOKANSURYO                      -- その他製品水交換数量
        , ETCSHNMZURISURYO                        -- その他製品水売上数量
        , SHNMZRGRAKIN                            -- 製品水レギュラー金額
        , SHNMZSRMKIN                             -- 製品水スリム金額
        , SHNMZSONOTAKIN                          -- 製品水その他金額
        , RNTLRWSVKIN                             -- レンタル料サーバー金額
        , RNTLRHKSHNKIN                           -- レンタル料他商品金額
        , SNTSHNWSVKIN                            -- その他商品サーバー金額
        , SNTSHNHKSHNKIN                          -- その他商品他商品金額
        , SNTSHNKNPHNKIN                          -- その他商品キャンペーン品金額
        , KAISHUHOHOKBN                           -- 回収方法区分
        , SHRYTIDYDMDD                            -- 支払予定年月日
        , KSISHIMEDYDMDD                          -- 決済締年月日
        , KSIKKNCD                                -- 決済機関コード
        , YUKOKBN                                 -- 有効区分
        , TOSHAAPKIHONCD                          -- 登録者ＡＰ基本コード
        , TOSHASHOPCD                             -- 登録者ショップコード
        , TOSHACD                                 -- 登録者コード
        , INPTSYSTMYMD                            -- 登録システム日付
        , INPTSYSTMJIKAN                          -- 登録システム時間
        , INPTUNYOYMD                             -- 登録運用日付
        , INPTIPADRSE                             -- 登録ＩＰアドレス
        , INPTPGMID                               -- 登録プログラムＩＤ
        , KOSHAAPKIHONCD                          -- 更新者ＡＰ基本コード
        , KOSHASHOPCD                             -- 更新者ショップコード
        , KOSHACD                                 -- 更新者コード
        , KSHNSYSTMYMD                            -- 更新システム日付
        , KSHNSYSTMJIKAN                          -- 更新システム時間
        , KSHNUNYOYMD                             -- 更新運用日付
        , KSHNIPADRSE                             -- 更新ＩＰアドレス
        , KSHNPGMID                               -- 更新プログラムＩＤ
    ) VALUES (
          vRecordS07T001T.APKIHONCD               -- ＡＰ基本コード
        , vRecordS07T001T.SHOPCD                  -- ショップコード
        , vRecordS07T001T.SKYSAKICD               -- 請求先コード
        , vRecordS07T001T.TOKUISAKICD             -- 得意先コード
        , vRecordS07T001T.SHIMEDYDM               -- 締年月
        , vRecordS07T001T.SHIMEDD                 -- 締日
        , vRecordS07T001T.RENBAN                  -- 連番
        , vRecordS07T001T.SKYPRCSJISSIKBN         -- 請求処理実施区分
        , vRecordS07T001T.SHIMEPRCSJISSIDD        -- 締処理実施日
        , vRecordS07T001T.ZNKSHIMEDD              -- 前回締日
        , vRecordS07T001T.KONKAISHIMEDD           -- 今回締日
        , vRecordS07T001T.ZNKKURIKOSHIKIN         -- 前回繰越金額
        , vRecordS07T001T.KONKAITAXNUKIURIKIN     -- 今回税抜売上金額
        , vRecordS07T001T.KONKAISHOHITAXKIN       -- 今回消費税金額
        , vRecordS07T001T.KONKAINYKNKIN           -- 今回入金金額
        , vRecordS07T001T.KURIKOSHIKIN            -- 繰越金額
        , vRecordS07T001T.KESHIKOMIKIN            -- 消込金額
        , vRecordS07T001T.KESHIKOMIZD             -- 消込残高
        , vRecordS07T001T.N8PERZEINUKIURIKIN      -- ８％税抜売上金額
        , vRecordS07T001T.N8PERSHOHITAXKIN        -- ８％消費税金額
        , vRecordS07T001T.N10PERZEINUKIURIKIN     -- １０％税抜売上金額
        , vRecordS07T001T.N10PERSHOHITAXKIN       -- １０％消費税金額
        -- add 軽減税率変更対応_LI 20261127 Start
        , vRecordS07T001T.OLDKEIGENZEINUKIURIKIN  -- 軽減旧枠税抜売上金額（消費税改修）
        , vRecordS07T001T.OLDKEIGENSHOHITAXKIN    -- 軽減旧枠消費税金額（消費税改修）
        , vRecordS07T001T.OLDIPPANZEINUKIURIKIN   -- 一般旧枠税抜売上金額（消費税改修）
        , vRecordS07T001T.OLDIPPANSHOHITAXKIN     -- 一般旧枠消費税金額（消費税改修）
        -- add 軽減税率変更対応_LI 20261127 End
        , vRecordS07T001T.TAXKEISANKBN            -- 税計算区分
        , vRecordS07T001T.SKYICRNHYPRNTDD         -- 請求一覧表印刷日
        , vRecordS07T001T.SKYSHOPRNTDD            -- 請求書印刷日
        , vRecordS07T001T.SKYSHONO                -- 請求書番号
        , vRecordS07T001T.SKYSHONOEDANO           -- 請求書番号枝番
        , vRecordS07T001T.RIYOMEISAISHOPRNTDD     -- 利用明細書印刷日
        , vRecordS07T001T.RIYOMEISAISHONO         -- 利用明細書番号
        , vRecordS07T001T.TOKUSOKUJOPRNTDD        -- 督促状印刷日
        , vRecordS07T001T.TOKUSOKUJONO            -- 督促状番号
        , vRecordS07T001T.CSVDATCHUSYUTSUDD       -- ＣＳＶデータ抽出日
        , vRecordS07T001T.RGRAYURYO               -- レギュラー有料
        , vRecordS07T001T.RGRAMURYO               -- レギュラー無料
        , vRecordS07T001T.SRMYURYO                -- スリム有料
        , vRecordS07T001T.SRMMURYO                -- スリム無料
        , vRecordS07T001T.SONOTAYURYO             -- その他有料
        , vRecordS07T001T.SONOTAMURYO             -- その他無料
        , vRecordS07T001T.SEIHINMIZUURI           -- 製品水売上
        , vRecordS07T001T.RTLRKN                  -- レンタル料金
        , vRecordS07T001T.SONOTAURIKIN            -- その他売上金額
        , vRecordS07T001T.AQCRGRAKYNPNSURYO       -- アクアクララレギュラーキャンペーン数量
        , vRecordS07T001T.AQCRGRASVSURYO          -- アクアクララレギュラーサービス数量
        , vRecordS07T001T.AQCRGRAPUROMOSHONSURYO  -- アクアクララレギュラープロモーション数量
        , vRecordS07T001T.AQCRGRASIINSURYO        -- アクアクララレギュラー試飲数量
        , vRecordS07T001T.AQCRGRAMNTESURYO        -- アクアクララレギュラーメンテ数量
        , vRecordS07T001T.AQCRGRAKOKANSURYO       -- アクアクララレギュラー交換数量
        , vRecordS07T001T.AQCRGRAURISURYO         -- アクアクララレギュラー売上数量
        , vRecordS07T001T.AQCSRMKYNPNSURYO        -- アクアクララスリムキャンペーン数量
        , vRecordS07T001T.AQCSRMSVSURYO           -- アクアクララスリムサービス数量
        , vRecordS07T001T.AQCSRMPUROMOSHONSURYO   -- アクアクララスリムプロモーション数量
        , vRecordS07T001T.AQCSRMSIINSURYO         -- アクアクララスリム試飲数量
        , vRecordS07T001T.AQCSRMMNTESURYO         -- アクアクララスリムメンテ数量
        , vRecordS07T001T.AQCSRMKOKANSURYO        -- アクアクララスリム交換数量
        , vRecordS07T001T.AQCSRMURISURYO          -- アクアクララスリム売上数量
        , vRecordS07T001T.ETCSHNMZKYNPNSURYO      -- その他製品水キャンペーン数量
        , vRecordS07T001T.ETCSHNMZSVSURYO         -- その他製品水サービス数量
        , vRecordS07T001T.ETCSHNMZPUROMOSHONSURYO -- その他製品水プロモーション数量
        , vRecordS07T001T.ETCSHNMZSIINSURYO       -- その他製品水試飲数量
        , vRecordS07T001T.ETCSHNMZMNTESURYO       -- その他製品水メンテ数量
        , vRecordS07T001T.ETCSHNMZKOKANSURYO      -- その他製品水交換数量
        , vRecordS07T001T.ETCSHNMZURISURYO        -- その他製品水売上数量
        , vRecordS07T001T.SHNMZRGRAKIN            -- 製品水レギュラー金額
        , vRecordS07T001T.SHNMZSRMKIN             -- 製品水スリム金額
        , vRecordS07T001T.SHNMZSONOTAKIN          -- 製品水その他金額
        , vRecordS07T001T.RNTLRWSVKIN             -- レンタル料サーバー金額
        , vRecordS07T001T.RNTLRHKSHNKIN           -- レンタル料他商品金額
        , vRecordS07T001T.SNTSHNWSVKIN            -- その他商品サーバー金額
        , vRecordS07T001T.SNTSHNHKSHNKIN          -- その他商品他商品金額
        , vRecordS07T001T.SNTSHNKNPHNKIN          -- その他商品キャンペーン品金額
        , vRecordS07T001T.KAISHUHOHOKBN           -- 回収方法区分
        , vRecordS07T001T.SHRYTIDYDMDD            -- 支払予定年月日
        , vRecordS07T001T.KSISHIMEDYDMDD          -- 決済締年月日
        , vRecordS07T001T.KSIKKNCD                -- 決済機関コード
        , vRecordS07T001T.YUKOKBN                 -- 有効区分
        , vRecordS07T001T.TOSHAAPKIHONCD          -- 登録者ＡＰ基本コード
        , vRecordS07T001T.TOSHASHOPCD             -- 登録者ショップコード
        , vRecordS07T001T.TOSHACD                 -- 登録者コード
        , vRecordS07T001T.INPTSYSTMYMD            -- 登録システム日付
        , vRecordS07T001T.INPTSYSTMJIKAN          -- 登録システム時間
        , vRecordS07T001T.INPTUNYOYMD             -- 登録運用日付
        , vRecordS07T001T.INPTIPADRSE             -- 登録ＩＰアドレス
        , vRecordS07T001T.INPTPGMID               -- 登録プログラムＩＤ
        , vRecordS07T001T.KOSHAAPKIHONCD          -- 更新者ＡＰ基本コード
        , vRecordS07T001T.KOSHASHOPCD             -- 更新者ショップコード
        , vRecordS07T001T.KOSHACD                 -- 更新者コード
        , vRecordS07T001T.KSHNSYSTMYMD            -- 更新システム日付
        , vRecordS07T001T.KSHNSYSTMJIKAN          -- 更新システム時間
        , vRecordS07T001T.KSHNUNYOYMD             -- 更新運用日付
        , vRecordS07T001T.KSHNIPADRSE             -- 更新ＩＰアドレス
        , vRecordS07T001T.KSHNPGMID               -- 更新プログラムＩＤ
    );
  END InsertS07T001T;

  /*****************************************************************************/
  /* プロシージャ名  ：請求残高テーブル削除                                    */
  /*****************************************************************************/
  PROCEDURE DeleteS07T001T IS
  BEGIN
    UPDATE S07T001T
    SET
          YUKOKBN          = '0'
        , KOSHAAPKIHONCD   = vRecordS07T001T.KOSHAAPKIHONCD
        , KOSHASHOPCD      = vRecordS07T001T.KOSHASHOPCD
        , KOSHACD          = vRecordS07T001T.KOSHACD
        , KSHNSYSTMYMD     = vRecordS07T001T.KSHNSYSTMYMD
        , KSHNSYSTMJIKAN   = vRecordS07T001T.KSHNSYSTMJIKAN
        , KSHNUNYOYMD      = vRecordS07T001T.KSHNUNYOYMD
        , KSHNIPADRSE      = vRecordS07T001T.KSHNIPADRSE
        , KSHNPGMID        = vRecordS07T001T.KSHNPGMID
    WHERE
            APKIHONCD      = vRecordS07T001T.APKIHONCD
        AND SHOPCD         = vRecordS07T001T.SHOPCD
        AND SKYSHONO       = vRecordS07T001T.SKYSHONO
        AND SKYSHONOEDANO  = vRecordS07T001T.SKYSHONOEDANO
        AND YUKOKBN        = '1'
    ;
  END DeleteS07T001T;

  /*****************************************************************************/
  /* プロシージャ名  ：入金明細テーブル更新（請求締済の更新）                  */
  /* 引数            ：iSkyshono             請求書番号                        */
  /*                 ：iSkyshonoedano        請求書番号枝番                    */
  /*****************************************************************************/
  PROCEDURE UpdateS10T001TSeikyuFix(iSkyshono      IN S07T001T.SKYSHONO%TYPE
                                   ,iSkyshonoedano IN S07T001T.SKYSHONOEDANO%TYPE) IS
  BEGIN
    -- 入金明細テーブル更新（請求締済の更新）
    UPDATE S10T001T NYUKIN
    SET
          SKYSHIMEDYDM          = vRecordS07T001T.SHIMEDYDM
        , SKYSHIMEDD            = vRecordS07T001T.SHIMEDD
        , SKYPRCSJISSIDD        = vRecordS07T001T.SHIMEPRCSJISSIDD
        , SKYPRCSJISSIKBN       = vRecordS07T001T.SKYPRCSJISSIKBN
        , KOSHAAPKIHONCD        = vRecordS07T001T.KOSHAAPKIHONCD
        , KOSHASHOPCD           = vRecordS07T001T.KOSHASHOPCD
        , KOSHACD               = vRecordS07T001T.KOSHACD
        , KSHNSYSTMYMD          = vRecordS07T001T.KSHNSYSTMYMD
        , KSHNSYSTMJIKAN        = vRecordS07T001T.KSHNSYSTMJIKAN
        , KSHNUNYOYMD           = vRecordS07T001T.KSHNUNYOYMD
        , KSHNIPADRSE           = vRecordS07T001T.KSHNIPADRSE
        , KSHNPGMID             = vRecordS07T001T.KSHNPGMID
    WHERE
            NYUKIN.APKIHONCD   = vRecordS07T001T.APKIHONCD
        AND NYUKIN.SHOPCD      = vRecordS07T001T.SHOPCD
        AND NYUKIN.SKYSAKICD   = vRecordS07T001T.SKYSAKICD
        AND NYUKIN.TOKUISAKICD = vRecordS07T001T.TOKUISAKICD
        AND EXISTS (
            SELECT
                SEIKYU.APKIHONCD
            FROM
                S07T006T SEIKYU
            WHERE
                    SEIKYU.APKIHONCD        = NYUKIN.APKIHONCD
                AND SEIKYU.SHOPCD           = NYUKIN.SHOPCD
                AND SEIKYU.SKYSHONO         = iSkyshono
                AND SEIKYU.SKYSHONOEDANO    = iSkyshonoedano
                AND SEIKYU.DNPYNO           = NYUKIN.DNPYNO
                AND SEIKYU.NYKNDNPYNOEDANO  = NYUKIN.NYKNDNPYNOEDANO
                AND SEIKYU.URINYKNKBN       = '1'
        )
    ;
  END UpdateS10T001TSeikyuFix;

  /*****************************************************************************/
  /* プロシージャ名  ：入金明細テーブル更新（請求締解除の更新）                */
  /* 引数            ：iDnpyno               伝票番号                          */
  /*                 ：iNykndnpynoedano      入金伝票番号枝番号                */
  /*****************************************************************************/
  PROCEDURE UpdateS10T001TSeikyuFixCancel(iDnpyno          IN S10T001T.DNPYNO%TYPE
                                         ,iNykndnpynoedano IN S10T001T.NYKNDNPYNOEDANO%TYPE) IS
  BEGIN
    -- 入金明細テーブル更新（請求締解除の更新）
    UPDATE S10T001T
    SET
          SKYSHIMEDYDM      = NULL
        , SKYSHIMEDD        = NULL
        , SKYPRCSJISSIDD    = '00000000'
        , SKYPRCSJISSIKBN   = 0 -- （未締）
        , KOSHAAPKIHONCD    = vRecordS07T001T.KOSHAAPKIHONCD
        , KOSHASHOPCD       = vRecordS07T001T.KOSHASHOPCD
        , KOSHACD           = vRecordS07T001T.KOSHACD
        , KSHNSYSTMYMD      = vRecordS07T001T.KSHNSYSTMYMD
        , KSHNSYSTMJIKAN    = vRecordS07T001T.KSHNSYSTMJIKAN
        , KSHNUNYOYMD       = vRecordS07T001T.KSHNUNYOYMD
        , KSHNIPADRSE       = vRecordS07T001T.KSHNIPADRSE
        , KSHNPGMID         = vRecordS07T001T.KSHNPGMID
    WHERE
            APKIHONCD       = vRecordS07T001T.APKIHONCD
        AND SHOPCD          = vRecordS07T001T.SHOPCD
        AND DNPYNO          = iDnpyno
        AND NYKNDNPYNOEDANO = iNykndnpynoedano
        AND TOKUISAKICD     = vRecordS07T001T.TOKUISAKICD
        AND SKYPRCSJISSIKBN = 1
        AND YUKOKBN         = '1'
    ;
  END UpdateS10T001TSeikyuFixCancel;

  /*****************************************************************************/
  /* プロシージャ名  ：売上解除処理                                            */
  /* 引数            ：iApkihoncd            AP基本コード                      */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iSkysakicd            請求先コード                      */
  /*                 ：iTokuisakicd          得意先コード                      */
  /*                 ：iDnpyno               伝票番号                          */
  /*****************************************************************************/
  PROCEDURE SalesCancelProcess(iApkihoncd   IN S07T006T.APKIHONCD%TYPE
                              ,iShopcd      IN S07T006T.SHOPCD%TYPE
                              ,iSkysakicd   IN S07T006T.SKYSAKICD%TYPE
                              ,iTokuisakicd IN S07T006T.TOKUISAKICD%TYPE
                              ,iDnpyno      IN S07T006T.DNPYNO%TYPE) IS
    ---------------------------
    -- カーソル定義
    ---------------------------
    -- 売上テーブルカーソル
    CURSOR curS06T001T IS
      SELECT
          *
      FROM
          S06T001T
      WHERE
              APKIHONCD       = iApkihoncd
          AND SHOPCD          = iShopcd
          AND SKYSAKICD       = iSkysakicd
          AND TOKUISAKICD     = iTokuisakicd
          AND URIDNPYNO       = iDnpyno
          AND SKYPRCSJISSIKBN = 1
          AND YUKOKBN         = '1'
      ;

    ---------------------------
    -- レコード定義
    ---------------------------
    recS06T001T    curS06T001T%ROWTYPE;     -- 売上テーブルカーソルレコード

    ---------------------------
    -- 変数定義
    ---------------------------
    wUridnpynoFrom S06T001T.URIDNPYNO%TYPE;         -- 売上伝票番号（開始）
    wUridnpynoTo   S06T001T.URIDNPYNO%TYPE;         -- 売上伝票番号（終了）
    wTax           S07T001T.KONKAISHOHITAXKIN%TYPE; -- 消費税額
  BEGIN
    -- 売上テーブルカーソルループ
    FOR recS06T001T IN curS06T001T LOOP
      -- データ作成元区分：「23:請求消費税算出」の場合
      IF recS06T001T.DATSKSMOTOKBN = '23' THEN
        -- 売上伝票番号採番
        Z1804B01.getSlipNoUriageNa(vRecordS07T001T.APKIHONCD, vRecordS07T001T.SHOPCD, 1, wUridnpynoFrom, wUridnpynoTo);
        -- 売上テーブル更新（消費税・黒伝票の更新）
        UpdateS06T001TTaxSeikyuFixCancel(recS06T001T.URIDNPYNO, wUridnpynoFrom);
        -- 売上明細テーブル更新（消費税・黒伝票の更新）
        UpdateS06T002TSeikyuFixCancel(recS06T001T.URIDNPYNO);
        -- 売上テーブルデータ生成（消費税・赤伝票の登録）
        InsertS06T001TSeikyuFixCancel(recS06T001T, wUridnpynoFrom);
        -- 売上明細テーブルデータ生成（消費税・赤伝票の登録）
        InsertS06T002TSeikyuFixCancel(recS06T001T.APKIHONCD, recS06T001T.SHOPCD, recS06T001T.URIDNPYNO, wUridnpynoFrom);
        -- 今回消費税金額設定
        wTax := GetTax(iApkihoncd, iShopcd, iDnpyno);
        vRecordS07T001T.KONKAISHOHITAXKIN := vRecordS07T001T.KONKAISHOHITAXKIN + wTax;

      -- データ作成元区分：「23:請求消費税算出」以外の場合
      ELSE
        -- 売上テーブル更新（請求締解除）
        UpdateS06T001TSeikyuFixCancel(recS06T001T.URIDNPYNO);
      END IF;
    END LOOP;
  END SalesCancelProcess;

  /*****************************************************************************/
  /* プロシージャ名  ：請求締処理                                              */
  /* 引数            ：iSkyshono             請求書番号                        */
  /*                 ：iUridnpyno            売上伝票番号                      */
  /*                 ：iSkyshonoedano        請求書番号枝番                    */
  /*                 ：iRenban               連番                              */
  /*                 ：iSalesFlg             （0:売上処理なし、1:売上処理あり）*/
  /*                 ：iNyukinFlg            （0:入金処理なし、1:入金処理あり）*/
  /*****************************************************************************/
  PROCEDURE SeikyuFixProcess(iSkyshono      IN S07T001T.SKYSHONO%TYPE
                            ,iUridnpyno     IN S06T001T.URIDNPYNO%TYPE
                            ,iSkyshonoedano IN S07T001T.SKYSHONOEDANO%TYPE
                            ,iRenban        IN S07T001T.RENBAN%TYPE
                            ,iSalesFlg      IN VARCHAR2
                            ,iNyukinFlg     IN VARCHAR2) IS
    ---------------------------
    -- 変数定義
    ---------------------------
    wMeisaitaxkin         S07T001T.KONKAISHOHITAXKIN%TYPE;    -- 明細消費税金額
    wUridnpynoFrom        S06T001T.URIDNPYNO%TYPE;            -- 売上伝票番号（開始）
    wUridnpynoTo          S06T001T.URIDNPYNO%TYPE;            -- 売上伝票番号（終了）
    wNyknkin              S10T001T.NYKNKIN%TYPE;              -- 入金金額
    wNebikikin            S10T001T.NEBIKIKIN%TYPE;            -- 値引金額
    wTesuryo              S10T001T.TESURYO%TYPE;              -- 手数料
    wKonkaitaxnukiurikin  S07T001T.KONKAITAXNUKIURIKIN%TYPE;  -- 今回税抜売上金額
    -- add 軽減税率変更対応_LI 20261127 Start
    wPrevStrtdd           S02M091T.TEKIYOSTRTDD%TYPE;          -- 税区分4の直前期間の適用開始日（消費税改修）
    -- add 軽減税率変更対応_LI 20261127 End
  BEGIN
    -- 請求残高テーブル（数量、金額）項目値クリア
    vRecordS07T001T.KONKAITAXNUKIURIKIN     := 0; -- 今回税抜売上金額
    vRecordS07T001T.KONKAISHOHITAXKIN       := 0; -- 今回消費税額
    vRecordS07T001T.KONKAINYKNKIN           := 0; -- 今回入金金額
    vRecordS07T001T.KESHIKOMIKIN            := 0; -- 消込金額
    vRecordS07T001T.KESHIKOMIZD             := 0; -- 消込残高
    vRecordS07T001T.N8PERZEINUKIURIKIN      := 0; -- ８％税抜売上金額
    vRecordS07T001T.N8PERSHOHITAXKIN        := 0; -- ８％消費税金額
    vRecordS07T001T.N10PERZEINUKIURIKIN     := 0; -- １０％税抜売上金額
    vRecordS07T001T.N10PERSHOHITAXKIN       := 0; -- １０％消費税金額
    -- add 軽減税率変更対応_LI 20261127 Start
    vRecordS07T001T.OLDKEIGENZEINUKIURIKIN  := 0; -- 軽減旧枠税抜売上金額（消費税改修）
    vRecordS07T001T.OLDKEIGENSHOHITAXKIN    := 0; -- 軽減旧枠消費税金額（消費税改修）
    vRecordS07T001T.OLDIPPANZEINUKIURIKIN   := 0; -- 一般旧枠税抜売上金額（消費税改修）
    vRecordS07T001T.OLDIPPANSHOHITAXKIN     := 0; -- 一般旧枠消費税金額（消費税改修）
    -- add 軽減税率変更対応_LI 20261127 End
    vRecordS07T001T.RGRAYURYO               := 0; -- レギュラー有料
    vRecordS07T001T.RGRAMURYO               := 0; -- レギュラー無料
    vRecordS07T001T.SRMYURYO                := 0; -- スリム有料
    vRecordS07T001T.SRMMURYO                := 0; -- スリム無料
    vRecordS07T001T.SONOTAYURYO             := 0; -- その他有料
    vRecordS07T001T.SONOTAMURYO             := 0; -- その他無料
    vRecordS07T001T.SEIHINMIZUURI           := 0; -- 製品水売上
    vRecordS07T001T.RTLRKN                  := 0; -- レンタル料金
    vRecordS07T001T.SONOTAURIKIN            := 0; -- その他売上金額
    vRecordS07T001T.AQCRGRAKYNPNSURYO       := 0; -- アクアクララレギュラーキャンペーン数量
    vRecordS07T001T.AQCRGRASVSURYO          := 0; -- アクアクララレギュラーサービス数量
    vRecordS07T001T.AQCRGRAPUROMOSHONSURYO  := 0; -- アクアクララレギュラープロモーション数量
    vRecordS07T001T.AQCRGRASIINSURYO        := 0; -- アクアクララレギュラー試飲数量
    vRecordS07T001T.AQCRGRAMNTESURYO        := 0; -- アクアクララレギュラーメンテ数量
    vRecordS07T001T.AQCRGRAKOKANSURYO       := 0; -- アクアクララレギュラー交換数量
    vRecordS07T001T.AQCRGRAURISURYO         := 0; -- アクアクララレギュラー売上数量
    vRecordS07T001T.AQCSRMKYNPNSURYO        := 0; -- アクアクララスリムキャンペーン数量
    vRecordS07T001T.AQCSRMSVSURYO           := 0; -- アクアクララスリムサービス数量
    vRecordS07T001T.AQCSRMPUROMOSHONSURYO   := 0; -- アクアクララスリムプロモーション数量
    vRecordS07T001T.AQCSRMSIINSURYO         := 0; -- アクアクララスリム試飲数量
    vRecordS07T001T.AQCSRMMNTESURYO         := 0; -- アクアクララスリムメンテ数量
    vRecordS07T001T.AQCSRMKOKANSURYO        := 0; -- アクアクララスリム交換数量
    vRecordS07T001T.AQCSRMURISURYO          := 0; -- アクアクララスリム売上数量
    vRecordS07T001T.ETCSHNMZKYNPNSURYO      := 0; -- その他製品水キャンペーン数量
    vRecordS07T001T.ETCSHNMZSVSURYO         := 0; -- その他製品水サービス数量
    vRecordS07T001T.ETCSHNMZPUROMOSHONSURYO := 0; -- その他製品水プロモーション数量
    vRecordS07T001T.ETCSHNMZSIINSURYO       := 0; -- その他製品水試飲数量
    vRecordS07T001T.ETCSHNMZMNTESURYO       := 0; -- その他製品水メンテ数量
    vRecordS07T001T.ETCSHNMZKOKANSURYO      := 0; -- その他製品水交換数量
    vRecordS07T001T.ETCSHNMZURISURYO        := 0; -- その他製品水売上数量
    vRecordS07T001T.SHNMZRGRAKIN            := 0; -- 製品水レギュラー金額
    vRecordS07T001T.SHNMZSRMKIN             := 0; -- 製品水スリム金額
    vRecordS07T001T.SHNMZSONOTAKIN          := 0; -- 製品水その他金額
    vRecordS07T001T.RNTLRWSVKIN             := 0; -- レンタル料サーバー金額
    vRecordS07T001T.RNTLRHKSHNKIN           := 0; -- レンタル料他商品金額
    vRecordS07T001T.SNTSHNWSVKIN            := 0; -- その他商品サーバー金額
    vRecordS07T001T.SNTSHNHKSHNKIN          := 0; -- その他商品他商品金額
    vRecordS07T001T.SNTSHNKNPHNKIN          := 0; -- その他商品キャンペーン品金額
    wMeisaitaxkin                           := 0; -- 明細消費税金額
    wKonkaitaxnukiurikin                    := 0; -- 今回税抜売上金額

    -- add 軽減税率変更対応_LI 20261127 Start
    -- 消費税改修：軽減旧枠の再計算用に、税区分4の直前期間の適用開始日を取得する
    SELECT MAX(TEKIYOSTRTDD) INTO wPrevStrtdd FROM S02M091T
     WHERE TAXKBN = '4' AND TEKIYOSHRYDD < vRecordS07T001T.KONKAISHIMEDD AND YUKOKBN = '1';

    -- add 軽減税率変更対応_LI 20261127 End
    -- 売上処理ありの場合
    IF iSalesFlg = '1' THEN
      -- 請求明細関連テーブル生成
      InsertS07T006TBySales(iSkyshono, iSkyshonoedano, iUridnpyno);
      -- 売上テーブル更新（請求締済の更新）
      UpdateS06T001TSeikyuFix(iSkyshono, iSkyshonoedano);
      -- 売上明細テーブル数量、金額項目集計値設定
      SetSummaryValue(iUridnpyno, iSkyshono, iSkyshonoedano, wMeisaitaxkin, wKonkaitaxnukiurikin);

      -- インボイス施行後の場合
      IF vInvoiceExecutionFlg = '1' THEN
        -- 明細消費税金額≠0且つ回収方法区分：代引き以外の場合
        IF wMeisaitaxkin != 0 AND vRecordS07T001T.KAISHUHOHOKBN != '7' THEN
          RAISE_APPLICATION_ERROR(-20014, 'インボイスに関連する売上が訂正されています。システム部に連絡してください。');
        ELSE
          -- 回収方法区分：代引き以外の場合、消費税明細データの生成
          IF vRecordS07T001T.KAISHUHOHOKBN != '7' THEN
            -- 8%消費税算出
            vRecordS07T001T.N8PERSHOHITAXKIN := Z1808B01.SyohizeiKeisan( '1', '1', '4', NULL, vRecordS07T001T.KONKAISHIMEDD,
              vRecordS07T001T.APKIHONCD, vRecordS07T001T.SHOPCD, NULL, 0, vRecordS07T001T.N8PERZEINUKIURIKIN);
            -- 10%消費税算出
            vRecordS07T001T.N10PERSHOHITAXKIN := Z1808B01.SyohizeiKeisan('1', '1', '1', NULL, vRecordS07T001T.KONKAISHIMEDD,
              vRecordS07T001T.APKIHONCD, vRecordS07T001T.SHOPCD, NULL, 0, vRecordS07T001T.N10PERZEINUKIURIKIN);
            -- modify 軽減税率変更対応_LI 20261127 Start
            -- 軽減旧枠消費税算出（税区分4の直前期間の税率で計算する。直前期間が無い場合は旧枠自体が発生しない）
            IF wPrevStrtdd IS NOT NULL THEN
              vRecordS07T001T.OLDKEIGENSHOHITAXKIN := Z1808B01.SyohizeiKeisan( '1', '1', '4', NULL, wPrevStrtdd,
                vRecordS07T001T.APKIHONCD, vRecordS07T001T.SHOPCD, NULL, 0, vRecordS07T001T.OLDKEIGENZEINUKIURIKIN);
            END IF;

            -- 消費税合算が0以外の場合
            IF vRecordS07T001T.N8PERSHOHITAXKIN + vRecordS07T001T.N10PERSHOHITAXKIN + vRecordS07T001T.OLDKEIGENSHOHITAXKIN + vRecordS07T001T.OLDIPPANSHOHITAXKIN != 0 THEN
            -- modify 軽減税率変更対応_LI 20261127 End
              -- 売上伝票番号採番
              Z1804B01.getSlipNoUriageNa(vRecordS07T001T.APKIHONCD, vRecordS07T001T.SHOPCD, 1, wUridnpynoFrom, wUridnpynoTo);
              -- 売上テーブルデータ生成（消費税・黒）
              InsertS06T001TSeikyuFix(wUridnpynoFrom);
              -- 売上明細テーブルデータ生成（消費税・黒）
              InsertS06T002TSeikyuFix(wUridnpynoFrom);
              -- 請求明細関連テーブルデータ生成
              InsertS07T006T(iSkyshono, iSkyshonoedano, wUridnpynoFrom);
              -- 請求締用対象得意先ワーク更新（消費税加算）
              UpdateS07W006T();
            END IF;
          END IF;
        END IF;
      END IF;
    END IF;

    -- 入金処理ありの場合
    IF iNyukinFlg = '1' THEN
      -- 請求明細関連テーブルデータ生成
      InsertS07T006TByNyukin(iSkyshono, iSkyshonoedano);
      -- 入金明細テーブル更新（請求締済の更新）
      UpdateS10T001TSeikyuFix(iSkyshono, iSkyshonoedano);

      -- 入金金額、値引金額、手数料合計値取得（入金明細テーブル）
      SELECT
          NVL(SUM(NYUKIN.NYKNKIN), 0), NVL(SUM(NYUKIN.NEBIKIKIN), 0), NVL(SUM(NYUKIN.TESURYO), 0)
      INTO
          wNyknkin, wNebikikin, wTesuryo
      FROM
          S10T001T NYUKIN
      WHERE
              NYUKIN.APKIHONCD   = vRecordS07T001T.APKIHONCD
          AND NYUKIN.SHOPCD      = vRecordS07T001T.SHOPCD
          AND NYUKIN.SKYSAKICD   = vRecordS07T001T.SKYSAKICD
          AND NYUKIN.TOKUISAKICD = vRecordS07T001T.TOKUISAKICD
          AND EXISTS (
              SELECT
                SEIKYU.APKIHONCD
              FROM
                  S07T006T SEIKYU
              WHERE
                      SEIKYU.APKIHONCD        = NYUKIN.APKIHONCD
                  AND SEIKYU.SHOPCD           = NYUKIN.SHOPCD
                  AND SEIKYU.SKYSHONO         = iSkyshono
                  AND SEIKYU.SKYSHONOEDANO    = iSkyshonoedano
                  AND SEIKYU.DNPYNO           = NYUKIN.DNPYNO
                  AND SEIKYU.NYKNDNPYNOEDANO  = NYUKIN.NYKNDNPYNOEDANO
                  AND SEIKYU.URINYKNKBN       = '1'
          )
      ;

      -- 今回入金金額設定
      vRecordS07T001T.KONKAINYKNKIN := vRecordS07T001T.KONKAINYKNKIN + wNyknkin + wNebikikin + wTesuryo;
    END IF;

    -- 請求残高テーブル（請求書番号、金額）項目値設定
    vRecordS07T001T.SKYSHONO            := iSkyshono;                               -- 請求書番号
    vRecordS07T001T.SKYSHONOEDANO       := iSkyshonoedano;                          -- 請求書番号枝番
    vRecordS07T001T.RIYOMEISAISHONO     := iSkyshono;                               -- 利用明細書番号
    vRecordS07T001T.TOKUSOKUJONO        := iSkyshono;                               -- 督促状番号
    vRecordS07T001T.RENBAN              := iRenban;                                 -- 連番
    vRecordS07T001T.KONKAITAXNUKIURIKIN := wKonkaitaxnukiurikin;                    -- 今回税抜売上金額
    vRecordS07T001T.KONKAISHOHITAXKIN
      -- modify 軽減税率変更対応_LI 20261127 Start
      := vRecordS07T001T.N8PERSHOHITAXKIN + vRecordS07T001T.N10PERSHOHITAXKIN + vRecordS07T001T.OLDKEIGENSHOHITAXKIN + vRecordS07T001T.OLDIPPANSHOHITAXKIN;      -- 今回消費税額
      -- modify 軽減税率変更対応_LI 20261127 End
    vRecordS07T001T.KURIKOSHIKIN
      := vRecordS07T001T.ZNKKURIKOSHIKIN + vRecordS07T001T.KONKAITAXNUKIURIKIN
        + vRecordS07T001T.KONKAISHOHITAXKIN - vRecordS07T001T.KONKAINYKNKIN;        -- 繰越金額
    vRecordS07T001T.KESHIKOMIZD
      := vRecordS07T001T.KONKAITAXNUKIURIKIN + vRecordS07T001T.KONKAISHOHITAXKIN;   -- 消込残高

    -- 請求残高テーブルデータ生成
    InsertS07T001T();
  END SeikyuFixProcess;

  /*****************************************************************************/
  /* プロシージャ名  ：請求締処理（親子）                                      */
  /* 引数            ：iSkyshono             請求書番号                        */
  /*                 ：iUridnpyno            売上伝票番号                      */
  /*                 ：iSkyshonoedano        請求書番号枝番                    */
  /*                 ：iRenban               連番                              */
  /*                 ：iSalesFlg             （0:売上処理なし、1:売上処理あり）*/
  /*                 ：iNyukinFlg            （0:入金処理なし、1:入金処理あり）*/
  /*                 ：iN8pershohitaxkin     8％消費税金額（親子関係合算）     */
  /*                 ：iN10pershohitaxkin    10％消費税金額（親子関係合算）    */
  /*****************************************************************************/
  PROCEDURE SeikyuFixProcessOyako(iSkyshono     IN S07T001T.SKYSHONO%TYPE
                            ,iUridnpyno         IN S06T001T.URIDNPYNO%TYPE
                            ,iSkyshonoedano     IN S07T001T.SKYSHONOEDANO%TYPE
                            ,iRenban            IN S07T001T.RENBAN%TYPE
                            ,iSalesFlg          IN VARCHAR2
                            ,iNyukinFlg         IN VARCHAR2
                            ,iN8pershohitaxkin  IN NUMBER
                            -- modify 軽減税率変更対応_LI 20261127 Start
                            ,iN10pershohitaxkin IN NUMBER
                            ,iOldkeigenshohitaxkin IN NUMBER) IS
                            -- modify 軽減税率変更対応_LI 20261127 End
    ---------------------------
    -- 変数定義
    ---------------------------
    wMeisaitaxkin         S07T001T.KONKAISHOHITAXKIN%TYPE;    -- 明細消費税金額
    wUridnpynoFrom        S06T001T.URIDNPYNO%TYPE;            -- 売上伝票番号（開始）
    wUridnpynoTo          S06T001T.URIDNPYNO%TYPE;            -- 売上伝票番号（終了）
    wNyknkin              S10T001T.NYKNKIN%TYPE;              -- 入金金額
    wNebikikin            S10T001T.NEBIKIKIN%TYPE;            -- 値引金額
    wTesuryo              S10T001T.TESURYO%TYPE;              -- 手数料
    wKonkaitaxnukiurikin  S07T001T.KONKAITAXNUKIURIKIN%TYPE;  -- 今回税抜売上金額
  BEGIN
    -- 請求残高テーブル（数量、金額）項目値クリア
    vRecordS07T001T.KONKAITAXNUKIURIKIN     := 0; -- 今回税抜売上金額
    vRecordS07T001T.KONKAISHOHITAXKIN       := 0; -- 今回消費税額
    vRecordS07T001T.KONKAINYKNKIN           := 0; -- 今回入金金額
    vRecordS07T001T.KESHIKOMIKIN            := 0; -- 消込金額
    vRecordS07T001T.KESHIKOMIZD             := 0; -- 消込残高
    vRecordS07T001T.N8PERZEINUKIURIKIN      := 0; -- ８％税抜売上金額
    vRecordS07T001T.N8PERSHOHITAXKIN        := 0; -- ８％消費税金額
    vRecordS07T001T.N10PERZEINUKIURIKIN     := 0; -- １０％税抜売上金額
    vRecordS07T001T.N10PERSHOHITAXKIN       := 0; -- １０％消費税金額
    -- add 軽減税率変更対応_LI 20261127 Start
    vRecordS07T001T.OLDKEIGENZEINUKIURIKIN  := 0; -- 軽減旧枠税抜売上金額（消費税改修）
    vRecordS07T001T.OLDKEIGENSHOHITAXKIN    := 0; -- 軽減旧枠消費税金額（消費税改修）
    vRecordS07T001T.OLDIPPANZEINUKIURIKIN   := 0; -- 一般旧枠税抜売上金額（消費税改修）
    vRecordS07T001T.OLDIPPANSHOHITAXKIN     := 0; -- 一般旧枠消費税金額（消費税改修）
    -- add 軽減税率変更対応_LI 20261127 End
    vRecordS07T001T.RGRAYURYO               := 0; -- レギュラー有料
    vRecordS07T001T.RGRAMURYO               := 0; -- レギュラー無料
    vRecordS07T001T.SRMYURYO                := 0; -- スリム有料
    vRecordS07T001T.SRMMURYO                := 0; -- スリム無料
    vRecordS07T001T.SONOTAYURYO             := 0; -- その他有料
    vRecordS07T001T.SONOTAMURYO             := 0; -- その他無料
    vRecordS07T001T.SEIHINMIZUURI           := 0; -- 製品水売上
    vRecordS07T001T.RTLRKN                  := 0; -- レンタル料金
    vRecordS07T001T.SONOTAURIKIN            := 0; -- その他売上金額
    vRecordS07T001T.AQCRGRAKYNPNSURYO       := 0; -- アクアクララレギュラーキャンペーン数量
    vRecordS07T001T.AQCRGRASVSURYO          := 0; -- アクアクララレギュラーサービス数量
    vRecordS07T001T.AQCRGRAPUROMOSHONSURYO  := 0; -- アクアクララレギュラープロモーション数量
    vRecordS07T001T.AQCRGRASIINSURYO        := 0; -- アクアクララレギュラー試飲数量
    vRecordS07T001T.AQCRGRAMNTESURYO        := 0; -- アクアクララレギュラーメンテ数量
    vRecordS07T001T.AQCRGRAKOKANSURYO       := 0; -- アクアクララレギュラー交換数量
    vRecordS07T001T.AQCRGRAURISURYO         := 0; -- アクアクララレギュラー売上数量
    vRecordS07T001T.AQCSRMKYNPNSURYO        := 0; -- アクアクララスリムキャンペーン数量
    vRecordS07T001T.AQCSRMSVSURYO           := 0; -- アクアクララスリムサービス数量
    vRecordS07T001T.AQCSRMPUROMOSHONSURYO   := 0; -- アクアクララスリムプロモーション数量
    vRecordS07T001T.AQCSRMSIINSURYO         := 0; -- アクアクララスリム試飲数量
    vRecordS07T001T.AQCSRMMNTESURYO         := 0; -- アクアクララスリムメンテ数量
    vRecordS07T001T.AQCSRMKOKANSURYO        := 0; -- アクアクララスリム交換数量
    vRecordS07T001T.AQCSRMURISURYO          := 0; -- アクアクララスリム売上数量
    vRecordS07T001T.ETCSHNMZKYNPNSURYO      := 0; -- その他製品水キャンペーン数量
    vRecordS07T001T.ETCSHNMZSVSURYO         := 0; -- その他製品水サービス数量
    vRecordS07T001T.ETCSHNMZPUROMOSHONSURYO := 0; -- その他製品水プロモーション数量
    vRecordS07T001T.ETCSHNMZSIINSURYO       := 0; -- その他製品水試飲数量
    vRecordS07T001T.ETCSHNMZMNTESURYO       := 0; -- その他製品水メンテ数量
    vRecordS07T001T.ETCSHNMZKOKANSURYO      := 0; -- その他製品水交換数量
    vRecordS07T001T.ETCSHNMZURISURYO        := 0; -- その他製品水売上数量
    vRecordS07T001T.SHNMZRGRAKIN            := 0; -- 製品水レギュラー金額
    vRecordS07T001T.SHNMZSRMKIN             := 0; -- 製品水スリム金額
    vRecordS07T001T.SHNMZSONOTAKIN          := 0; -- 製品水その他金額
    vRecordS07T001T.RNTLRWSVKIN             := 0; -- レンタル料サーバー金額
    vRecordS07T001T.RNTLRHKSHNKIN           := 0; -- レンタル料他商品金額
    vRecordS07T001T.SNTSHNWSVKIN            := 0; -- その他商品サーバー金額
    vRecordS07T001T.SNTSHNHKSHNKIN          := 0; -- その他商品他商品金額
    vRecordS07T001T.SNTSHNKNPHNKIN          := 0; -- その他商品キャンペーン品金額
    wMeisaitaxkin                           := 0; -- 明細消費税金額
    wKonkaitaxnukiurikin                    := 0; -- 今回税抜売上金額

    -- 売上処理ありの場合
    IF iSalesFlg = '1' THEN
      -- 請求明細関連テーブル生成
      InsertS07T006TBySales(iSkyshono, iSkyshonoedano, iUridnpyno);
      -- 売上テーブル更新（請求締済の更新）
      UpdateS06T001TSeikyuFix(iSkyshono, iSkyshonoedano);
      -- 売上明細テーブル数量、金額項目集計値設定
      SetSummaryValue(iUridnpyno, iSkyshono, iSkyshonoedano, wMeisaitaxkin, wKonkaitaxnukiurikin);

      -- インボイス施行後の場合
      IF vInvoiceExecutionFlg = '1' THEN
        -- 明細消費税金額≠0且つ回収方法区分：代引き以外の場合
        IF wMeisaitaxkin != 0 AND vRecordS07T001T.KAISHUHOHOKBN != '7' THEN
          RAISE_APPLICATION_ERROR(-20014, 'インボイスに関連する売上が訂正されています。システム部に連絡してください。');
        ELSE
          -- 回収方法区分：代引き以外の場合、消費税明細データの生成
          IF vRecordS07T001T.KAISHUHOHOKBN != '7' THEN

            -- パラメータで受け取った消費税を設定する。
            -- 8%消費税算出
            vRecordS07T001T.N8PERSHOHITAXKIN := iN8pershohitaxkin;
            -- 10%消費税算出
            vRecordS07T001T.N10PERSHOHITAXKIN := iN10pershohitaxkin;
            -- modify 軽減税率変更対応_LI 20261127 Start
            -- 軽減旧枠消費税算出（呼び出し側で直前税率により算出済みの値を設定）
            vRecordS07T001T.OLDKEIGENSHOHITAXKIN := iOldkeigenshohitaxkin;

            -- 消費税合算が0以外の場合
            IF vRecordS07T001T.N8PERSHOHITAXKIN + vRecordS07T001T.N10PERSHOHITAXKIN + vRecordS07T001T.OLDKEIGENSHOHITAXKIN + vRecordS07T001T.OLDIPPANSHOHITAXKIN != 0 THEN
            -- modify 軽減税率変更対応_LI 20261127 End
              -- 売上伝票番号採番
              Z1804B01.getSlipNoUriageNa(vRecordS07T001T.APKIHONCD, vRecordS07T001T.SHOPCD, 1, wUridnpynoFrom, wUridnpynoTo);
              -- 売上テーブルデータ生成（消費税・黒）
              InsertS06T001TSeikyuFix(wUridnpynoFrom);
              -- 売上明細テーブルデータ生成（消費税・黒）
              InsertS06T002TSeikyuFix(wUridnpynoFrom);
              -- 請求明細関連テーブルデータ生成
              InsertS07T006T(iSkyshono, iSkyshonoedano, wUridnpynoFrom);
              -- 請求締用対象得意先ワーク更新（消費税加算）
              UpdateS07W006T();
            END IF;
          END IF;
        END IF;
      END IF;
    END IF;

    -- 入金処理ありの場合
    IF iNyukinFlg = '1' THEN
      -- 請求明細関連テーブルデータ生成
      InsertS07T006TByNyukin(iSkyshono, iSkyshonoedano);
      -- 入金明細テーブル更新（請求締済の更新）
      UpdateS10T001TSeikyuFix(iSkyshono, iSkyshonoedano);

      -- 入金金額、値引金額、手数料合計値取得（入金明細テーブル）
      SELECT
          NVL(SUM(NYUKIN.NYKNKIN), 0), NVL(SUM(NYUKIN.NEBIKIKIN), 0), NVL(SUM(NYUKIN.TESURYO), 0)
      INTO
          wNyknkin, wNebikikin, wTesuryo
      FROM
          S10T001T NYUKIN
      WHERE
              NYUKIN.APKIHONCD   = vRecordS07T001T.APKIHONCD
          AND NYUKIN.SHOPCD      = vRecordS07T001T.SHOPCD
          AND NYUKIN.SKYSAKICD   = vRecordS07T001T.SKYSAKICD
          AND NYUKIN.TOKUISAKICD = vRecordS07T001T.TOKUISAKICD
          AND EXISTS (
              SELECT
                SEIKYU.APKIHONCD
              FROM
                  S07T006T SEIKYU
              WHERE
                      SEIKYU.APKIHONCD        = NYUKIN.APKIHONCD
                  AND SEIKYU.SHOPCD           = NYUKIN.SHOPCD
                  AND SEIKYU.SKYSHONO         = iSkyshono
                  AND SEIKYU.SKYSHONOEDANO    = iSkyshonoedano
                  AND SEIKYU.DNPYNO           = NYUKIN.DNPYNO
                  AND SEIKYU.NYKNDNPYNOEDANO  = NYUKIN.NYKNDNPYNOEDANO
                  AND SEIKYU.URINYKNKBN       = '1'
          )
      ;

      -- 今回入金金額設定
      vRecordS07T001T.KONKAINYKNKIN := vRecordS07T001T.KONKAINYKNKIN + wNyknkin + wNebikikin + wTesuryo;
    END IF;

    -- 請求残高テーブル（請求書番号、金額）項目値設定
    vRecordS07T001T.SKYSHONO            := iSkyshono;                               -- 請求書番号
    vRecordS07T001T.SKYSHONOEDANO       := iSkyshonoedano;                          -- 請求書番号枝番
    vRecordS07T001T.RIYOMEISAISHONO     := iSkyshono;                               -- 利用明細書番号
    vRecordS07T001T.TOKUSOKUJONO        := iSkyshono;                               -- 督促状番号
    vRecordS07T001T.RENBAN              := iRenban;                                 -- 連番
    vRecordS07T001T.KONKAITAXNUKIURIKIN := wKonkaitaxnukiurikin;                    -- 今回税抜売上金額
    vRecordS07T001T.KONKAISHOHITAXKIN
      -- modify 軽減税率変更対応_LI 20261127 Start
      := vRecordS07T001T.N8PERSHOHITAXKIN + vRecordS07T001T.N10PERSHOHITAXKIN + vRecordS07T001T.OLDKEIGENSHOHITAXKIN + vRecordS07T001T.OLDIPPANSHOHITAXKIN;      -- 今回消費税額
      -- modify 軽減税率変更対応_LI 20261127 End
    vRecordS07T001T.KURIKOSHIKIN
      := vRecordS07T001T.ZNKKURIKOSHIKIN + vRecordS07T001T.KONKAITAXNUKIURIKIN
        + vRecordS07T001T.KONKAISHOHITAXKIN - vRecordS07T001T.KONKAINYKNKIN;        -- 繰越金額
    vRecordS07T001T.KESHIKOMIZD
      := vRecordS07T001T.KONKAITAXNUKIURIKIN + vRecordS07T001T.KONKAISHOHITAXKIN;   -- 消込残高

    -- 請求残高テーブルデータ生成
    InsertS07T001T();
  END SeikyuFixProcessOyako;

  /*****************************************************************************/
  /* プロシージャ名  ：請求締解除処理                                          */
  /*****************************************************************************/
  PROCEDURE SeikyuFixCancelProcess IS
    ---------------------------
    -- カーソル定義
    ---------------------------
    -- 請求残高テーブルカーソル
    CURSOR curS07T001T IS
      SELECT
          TOKUISAKICD, SKYSHONO, SKYSHONOEDANO
      FROM
          S07T001T
      WHERE
              APKIHONCD       = vRecordS07T001T.APKIHONCD
          AND SHOPCD          = vRecordS07T001T.SHOPCD
          AND SKYSAKICD       = vRecordS07T001T.SKYSAKICD
          AND KONKAISHIMEDD   = vRecordS07T001T.KONKAISHIMEDD
          AND SKYPRCSJISSIKBN = '1'
          AND YUKOKBN         = '1'
      ORDER BY
          TOKUISAKICD, RENBAN
      ;

    -- 請求明細関連テーブルカーソル
    CURSOR curS07T006T IS
      SELECT
          APKIHONCD, SHOPCD, SKYSAKICD, TOKUISAKICD, SKYSHONO, DNPYNO, NYKNDNPYNOEDANO, URINYKNKBN
      FROM
          S07T006T SEIKYU
      WHERE
              APKIHONCD     = vRecordS07T001T.APKIHONCD
          AND SHOPCD        = vRecordS07T001T.SHOPCD
          AND SKYSHONO      = vRecordS07T001T.SKYSHONO
          AND SKYSHONOEDANO = vRecordS07T001T.SKYSHONOEDANO
          AND SKYSAKICD     = vRecordS07T001T.SKYSAKICD
          AND TOKUISAKICD   = vRecordS07T001T.TOKUISAKICD
          AND YUKOKBN       = '1'
      ;

    ---------------------------
    -- レコード定義
    ---------------------------
    recS07T001T       curS07T001T%ROWTYPE;        -- 請求残高テーブルカーソルレコード
    recS07T006T       curS07T006T%ROWTYPE;        -- 請求明細関連テーブルカーソルレコード

    ---------------------------
    -- 変数定義
    ---------------------------
    wRecordCount      NUMBER;                     -- データ件数
    wKonkaishimedd    S07T001T.KONKAISHIMEDD%TYPE;-- 今回締日
    wKurikoshikin     S07T001T.KURIKOSHIKIN%TYPE; -- 繰越金額
  BEGIN
    -- 請求残高テーブル（消込済）件数取得
    SELECT
        COUNT(APKIHONCD)
    INTO
        wRecordCount
    FROM
        S07T001T
    WHERE
            APKIHONCD       = vRecordS07T001T.APKIHONCD
        AND SHOPCD          = vRecordS07T001T.SHOPCD
        AND SKYSAKICD       = vRecordS07T001T.SKYSAKICD
        AND KONKAISHIMEDD   = vRecordS07T001T.KONKAISHIMEDD
        AND SKYPRCSJISSIKBN = '1'
        AND YUKOKBN         = '1'
        AND KESHIKOMIKIN    != 0
    ;

    -- 請求残高テーブル（消込済）件数≠0の場合
    IF wRecordCount != 0 THEN
      RAISE_APPLICATION_ERROR(-20015, '消込済のため締解除はできません。');
    END IF;

    -- 請求残高テーブルカーソルループ
    FOR recS07T001T IN curS07T001T LOOP
      -- 請求残高テーブル（得意先コード、請求書番号、請求書番号枝番、今回消費税金額）項目値設定
      vRecordS07T001T.TOKUISAKICD       := recS07T001T.TOKUISAKICD;   -- 得意先コード
      vRecordS07T001T.SKYSHONO          := recS07T001T.SKYSHONO;      -- 請求書番号
      vRecordS07T001T.SKYSHONOEDANO     := recS07T001T.SKYSHONOEDANO; -- 請求書番号枝番
      vRecordS07T001T.KONKAISHOHITAXKIN := 0;                         -- 今回消費税金額

      -- 請求明細関連テーブルカーソルループ
      FOR recS07T006T IN curS07T006T LOOP
        -- 売上入金区分：「0:売上」の場合
        IF recS07T006T.URINYKNKBN = '0' THEN
          -- 売上解除処理
          SalesCancelProcess(recS07T006T.APKIHONCD, recS07T006T.SHOPCD, recS07T006T.SKYSAKICD, recS07T006T.TOKUISAKICD, recS07T006T.DNPYNO);

        -- 売上入金区分：「0:売上」以外の場合
        ELSE
          -- 入金明細テーブル更新（請求締解除の更新）
          UpdateS10T001TSeikyuFixCancel(recS07T006T.DNPYNO, recS07T006T.NYKNDNPYNOEDANO);
        END IF;
      END LOOP;

      -- 請求締用対象得意先ワーク更新・生成（消費税金額）
      MergeS07W006T();

      -- 請求残高テーブルデータ削除（有効区分：0）
      DeleteS07T001T();

      -- 請求残高テーブル（今回締日、繰越金額）取得
      BEGIN
        SELECT
            KONKAISHIMEDD, KURIKOSHIKIN
        INTO
            wKonkaishimedd, wKurikoshikin
        FROM
            (
              SELECT
                    KONKAISHIMEDD
                  , KURIKOSHIKIN
                  , ROW_NUMBER() OVER (ORDER BY KONKAISHIMEDD DESC, RENBAN DESC) AS ROWNO
              FROM
                  S07T001T
              WHERE
                      APKIHONCD     = vRecordS07T001T.APKIHONCD
                  AND SHOPCD        = vRecordS07T001T.SHOPCD
                  AND SKYSAKICD     = vRecordS07T001T.SKYSAKICD
                  AND TOKUISAKICD   = vRecordS07T001T.TOKUISAKICD
                  AND KONKAISHIMEDD <= vRecordS07T001T.KONKAISHIMEDD
                  AND YUKOKBN       = '1'
            )
        WHERE
            ROWNO = 1
        ;
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          wKonkaishimedd  := NULL;
          wKurikoshikin   := 0;
      END;

      -- 請求締用対象得意先ワーク更新（繰越金額）
      UpdateS07W006TKurikoshikin(wKonkaishimedd ,wKurikoshikin);
    END LOOP;
  END SeikyuFixCancelProcess;

  /*****************************************************************************/
  /* プロシージャ名  ：マスタ反映処理                                          */
  /* 引数            ：iUnitFixProcessFlg    単体締処理フラグ(1:単体/0:その他) */
  /*****************************************************************************/
  PROCEDURE MasterUpdateProcess(iUnitFixProcessFlg IN NUMBER) IS
    ---------------------------
    -- カーソル定義
    ---------------------------
    -- 請求締用対象得意先ワークカーソル
    CURSOR curS07W006T IS
      SELECT
          TTSW.APKIHONCD,
          TTSW.SHOPCD,
          TTSW.TOKUISAKICD,
          TTSW.SKYSAKICD,
          TTSW.SHOHITAXKIN,
          SKSM.URICHOSEIKIN
      FROM
          S07W006T TTSW
          INNER JOIN S02M017T SKSM
              ON (
                      SKSM.APKIHONCD  = TTSW.APKIHONCD
                  AND SKSM.SHOPCD     = TTSW.SHOPCD
                  AND SKSM.SKYSAKICD  = TTSW.SKYSAKICD
              )
      WHERE
              TTSW.APKIHONCD = vRecordS07T001T.APKIHONCD
          AND TTSW.SHOPCD    = vRecordS07T001T.SHOPCD
          AND ((iUnitFixProcessFlg = 0) OR ((iUnitFixProcessFlg = 1)
                  AND TTSW.SKYSAKICD   = vRecordS07T001T.SKYSAKICD
                  AND TTSW.TOKUISAKICD = vRecordS07T001T.TOKUISAKICD
              )
          )
      ;

    ---------------------------
    -- レコード定義
    ---------------------------
    recS07W006T curS07W006T%ROWTYPE; -- 請求締用対象得意先ワークカーソルレコード

    ---------------------------
    -- 変数定義
    ---------------------------
    wSkysakicd    S07W006T.SKYSAKICD%TYPE; -- 請求先コード
    wUrichoseikin NUMBER;                  -- 売上調整金額
    wTokuisakijotai  S02M013T.TOKUISAKIJOTAI%TYPE;   -- 得意先状態
    wGenzaizd        S02M050T.GENZAIZD%TYPE;         -- 現在残高

  BEGIN
    wSkysakicd := NULL;       -- 請求先コード
    -- 請求締用対象得意先ワークカーソルループ
    FOR recS07W006T IN curS07W006T LOOP
      -- 得意先実績テーブル更新（消費税金額加算）
      IF recS07W006T.SHOHITAXKIN != 0 THEN
        UPDATE S02M050T
        SET
              GENZAIZD        = NVL(GENZAIZD, 0) + recS07W006T.SHOHITAXKIN
            , KOSHAAPKIHONCD  = vRecordS07T001T.KOSHAAPKIHONCD
            , KOSHASHOPCD     = vRecordS07T001T.KOSHASHOPCD
            , KOSHACD         = vRecordS07T001T.KOSHACD
            , KSHNSYSTMYMD    = vRecordS07T001T.KSHNSYSTMYMD
            , KSHNSYSTMJIKAN  = vRecordS07T001T.KSHNSYSTMJIKAN
            , KSHNUNYOYMD     = vRecordS07T001T.KSHNUNYOYMD
            , KSHNIPADRSE     = vRecordS07T001T.KSHNIPADRSE
            , KSHNPGMID       = vRecordS07T001T.KSHNPGMID
        WHERE
                APKIHONCD     = recS07W006T.APKIHONCD
            AND SHOPCD        = recS07W006T.SHOPCD
            AND TOKUISAKICD   = recS07W006T.TOKUISAKICD
            AND YUKOKBN       = '1'
        ;
      END IF;

      -- 請求マスタの売上調整金額更新処理
      IF (wSkysakicd IS NULL) OR (wSkysakicd != recS07W006T.SKYSAKICD) THEN
        wSkysakicd := recS07W006T.SKYSAKICD;
        -- 売上調整金額を取得
        wUrichoseikin := GetUrichoseikin(vRecordS07T001T.APKIHONCD, vRecordS07T001T.SHOPCD, wSkysakicd);
        IF wUrichoseikin != recS07W006T.URICHOSEIKIN THEN
          -- 請求先マスタの更新（売上調整金額）
          UpdateS02M017TUrichoseikin(wUrichoseikin, wSkysakicd);
        END IF;
      END IF;

      -- 得意先マスタ.得意先状態、得意先実績テーブル.現在残高の取得
      SELECT
        TSKM.TOKUISAKIJOTAI,
        NVL(TSJT.GENZAIZD, 0)
      INTO
        wTokuisakijotai,
        wGenzaizd
      FROM S02M013T TSKM
        LEFT JOIN S02M050T TSJT
          ON     TSKM.APKIHONCD     = TSJT.APKIHONCD
             AND TSKM.SHOPCD        = TSJT.SHOPCD
             AND TSKM.TOKUISAKICD   = TSJT.TOKUISAKICD
             AND TSJT.YUKOKBN       = '1'
      WHERE
            TSKM.APKIHONCD     = recS07W006T.APKIHONCD
        AND TSKM.SHOPCD        = recS07W006T.SHOPCD
        AND TSKM.TOKUISAKICD   = recS07W006T.TOKUISAKICD
        AND TSKM.YUKOKBN       = '1'
      ;

      -- 得意先状態が仮会員退会もしくは退会の場合、現在残高に応じて状態ステータスを更新
      IF wTokuisakijotai IN ('4', '6') THEN
        -- 得意先実績テーブルの現在残高＝０（一括入金処理で更新後）の時、
        IF  wGenzaizd = 0 THEN
          UPDATE  S02M013T  SET
                  JOTAISTS        =   (CASE WHEN JOTAISTS = '05' THEN '06' ELSE '10' END) -- 状態ステータス
                , KOSHAAPKIHONCD  =   vRecordS07T001T.KOSHAAPKIHONCD
                , KOSHASHOPCD     =   vRecordS07T001T.KOSHASHOPCD
                , KOSHACD         =   vRecordS07T001T.KOSHACD
                , KSHNSYSTMYMD    =   vRecordS07T001T.KSHNSYSTMYMD
                , KSHNSYSTMJIKAN  =   vRecordS07T001T.KSHNSYSTMJIKAN
                , KSHNUNYOYMD     =   vRecordS07T001T.KSHNUNYOYMD
                , KSHNIPADRSE     =   vRecordS07T001T.KSHNIPADRSE
                , KSHNPGMID       =   vRecordS07T001T.KSHNPGMID
           WHERE  APKIHONCD       =   recS07W006T.APKIHONCD
             AND  SHOPCD          =   recS07W006T.SHOPCD
             AND  TOKUISAKICD     =   recS07W006T.TOKUISAKICD
             AND  JOTAISTS        IN  ('05', '09')    -- 状態ステータス (05:仮会員退会(未払い有) 09:本会員退会(未払い有))
             AND  YUKOKBN         =   '1';
        ELSE
          UPDATE  S02M013T  SET
                  JOTAISTS        =   (CASE WHEN JOTAISTS = '06' THEN '05' ELSE '09' END) -- 状態ステータス(退会済を退会済(未払有へ))
                , KOSHAAPKIHONCD  =   vRecordS07T001T.KOSHAAPKIHONCD
                , KOSHASHOPCD     =   vRecordS07T001T.KOSHASHOPCD
                , KOSHACD         =   vRecordS07T001T.KOSHACD
                , KSHNSYSTMYMD    =   vRecordS07T001T.KSHNSYSTMYMD
                , KSHNSYSTMJIKAN  =   vRecordS07T001T.KSHNSYSTMJIKAN
                , KSHNUNYOYMD     =   vRecordS07T001T.KSHNUNYOYMD
                , KSHNIPADRSE     =   vRecordS07T001T.KSHNIPADRSE
                , KSHNPGMID       =   vRecordS07T001T.KSHNPGMID
           WHERE  APKIHONCD       =   recS07W006T.APKIHONCD
             AND  SHOPCD          =   recS07W006T.SHOPCD
             AND  TOKUISAKICD     =   recS07W006T.TOKUISAKICD
             AND  JOTAISTS        IN  ('06', '10')    -- 状態ステータス (06:仮会員退会済 10:本会員退会済)
             AND  YUKOKBN         =   '1';
        END IF;
      END IF;

    END LOOP;

    -- ACCIS2023-6671 start-ABI ※ここでコミットしない
    -- ACCIS2023-6671 end-ABI

  END MasterUpdateProcess;

  /*****************************************************************************/
  /* プロシージャ名  ：代引請求残高再計算（前回繰越金額、繰越金額）            */
  /* 引数            ：なし                                                    */
  /*****************************************************************************/
  PROCEDURE ReCalculate IS
    /* カーソル定義 */
    -- 未来日付の請求残高（更新対象）取得用カーソル
    CURSOR curFutureBillBalance IS
      SELECT
        *
      FROM S07T001T SZDT
      WHERE
        SZDT.YUKOKBN = '1'
        AND SZDT.APKIHONCD       = vRecordS07T001T.APKIHONCD
        AND SZDT.SHOPCD          = vRecordS07T001T.SHOPCD
        AND SZDT.SKYSAKICD       = vRecordS07T001T.SKYSAKICD
        AND SZDT.TOKUISAKICD     = vRecordS07T001T.TOKUISAKICD
        AND SZDT.SKYPRCSJISSIKBN = '2'
        AND SZDT.KONKAISHIMEDD   > vRecordS07T001T.KONKAISHIMEDD
      ORDER BY
        SZDT.APKIHONCD
        ,SZDT.SHOPCD
        ,SZDT.SKYSAKICD
        ,SZDT.TOKUISAKICD
        ,SZDT.SHIMEDYDM
        ,SZDT.SHIMEDD
        ,SZDT.RENBAN
      FOR UPDATE
      ;

    /* レコード定義 */
    recFutureBillBalance vRecordS07T001T%ROWTYPE;

    /* 変数定義 */
    wZnkKurikoshikin vRecordS07T001T.ZNKKURIKOSHIKIN%TYPE := vRecordS07T001T.KURIKOSHIKIN;

  BEGIN
    -- 開始ログ出力
    STARTLOG('ReCalculate');
    DEBUGLOG('ReCalculate', vRecordS07T001T.SKYSAKICD, vRecordS07T001T.TOKUISAKICD, '反映元請求書番号 : ' || vRecordS07T001T.SKYSHONO);
    DEBUGLOG('ReCalculate', vRecordS07T001T.SKYSAKICD, vRecordS07T001T.TOKUISAKICD, '反映元請求書番号枝番 : ' || vRecordS07T001T.SKYSHONOEDANO);
    DEBUGLOG('ReCalculate', vRecordS07T001T.SKYSAKICD, vRecordS07T001T.TOKUISAKICD, '反映元繰越金額 : ' || vRecordS07T001T.KURIKOSHIKIN);

    -- 更新対象請求残高レコード取得
    FOR recFutureBillBalance IN curFutureBillBalance LOOP
      DEBUGLOG('ReCalculate', vRecordS07T001T.SKYSAKICD, vRecordS07T001T.TOKUISAKICD, '請求書番号 : ' || recFutureBillBalance.SKYSHONO);
      DEBUGLOG('ReCalculate', vRecordS07T001T.SKYSAKICD, vRecordS07T001T.TOKUISAKICD, '再計算前_前回繰越金額 : ' || recFutureBillBalance.ZNKKURIKOSHIKIN);
      DEBUGLOG('ReCalculate', vRecordS07T001T.SKYSAKICD, vRecordS07T001T.TOKUISAKICD, '再計算前_繰越金額 : ' || recFutureBillBalance.KURIKOSHIKIN);
      -- 前回繰越金額および繰越金額の再計算
      recFutureBillBalance.ZNKKURIKOSHIKIN := wZnkKurikoshikin;
      recFutureBillBalance.KURIKOSHIKIN    := recFutureBillBalance.ZNKKURIKOSHIKIN + recFutureBillBalance.KONKAITAXNUKIURIKIN
        + recFutureBillBalance.KONKAISHOHITAXKIN - recFutureBillBalance.KONKAINYKNKIN;
      -- 請求残高テーブル更新（PKを条件指定で更新）
      UPDATE S07T001T SZDT
      SET
        SZDT.ZNKKURIKOSHIKIN = recFutureBillBalance.ZNKKURIKOSHIKIN
        ,SZDT.KURIKOSHIKIN   = recFutureBillBalance.KURIKOSHIKIN
        ,SZDT.KOSHAAPKIHONCD = vRecordS07T001T.KOSHAAPKIHONCD
        ,SZDT.KOSHASHOPCD    = vRecordS07T001T.KOSHASHOPCD
        ,SZDT.KOSHACD        = vRecordS07T001T.KOSHACD
        ,SZDT.KSHNSYSTMYMD   = vRecordS07T001T.KSHNSYSTMYMD
        ,SZDT.KSHNSYSTMJIKAN = vRecordS07T001T.KSHNSYSTMJIKAN
        ,SZDT.KSHNUNYOYMD    = vRecordS07T001T.KSHNUNYOYMD
        ,SZDT.KSHNIPADRSE    = vRecordS07T001T.KSHNIPADRSE
        ,SZDT.KSHNPGMID      = vRecordS07T001T.KSHNPGMID
      WHERE
        SZDT.APKIHONCD = recFutureBillBalance.APKIHONCD
        AND SZDT.SHOPCD = recFutureBillBalance.SHOPCD
        AND SZDT.SKYSHONO = recFutureBillBalance.SKYSHONO
        AND SZDT.SKYSHONOEDANO = recFutureBillBalance.SKYSHONOEDANO
      ;
      DEBUGLOG('ReCalculate', vRecordS07T001T.SKYSAKICD, vRecordS07T001T.TOKUISAKICD, '再計算後_前回繰越金額 : ' || recFutureBillBalance.ZNKKURIKOSHIKIN);
      DEBUGLOG('ReCalculate', vRecordS07T001T.SKYSAKICD, vRecordS07T001T.TOKUISAKICD, '再計算後_繰越金額 : ' || recFutureBillBalance.KURIKOSHIKIN);
      -- 変数に保持している前回繰越金額を更新
      wZnkKurikoshikin := recFutureBillBalance.KURIKOSHIKIN;
    END LOOP;
    -- 終了ログ出力
    ENDLOG('ReCalculate');
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      -- 更新対象対象データなし（正常終了）
      DEBUGLOG('ReCalculate', vRecordS07T001T.SKYSAKICD, vRecordS07T001T.TOKUISAKICD, '更新対象データなし');
      -- 終了ログ出力
      ENDLOG('ReCalculate');
    WHEN OTHERS THEN
      -- エラー情報をログに出力
      DEBUGLOG(
        'ReCalculate',
        vRecordS07T001T.SKYSAKICD,
        vRecordS07T001T.TOKUISAKICD,
        SUBSTRB(SQLERRM || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || DBMS_UTILITY.FORMAT_ERROR_STACK, 1, 512)
      );
      -- 終了ログ出力
      ENDLOG('ReCalculate');
      -- エラーは呼び元に返す
      RAISE;
  END ReCalculate;

  /*****************************************************************************/
  /* プロシージャ名  ：処理開始判定                                            */
  /* 引数            ：iIraidd               依頼日                            */
  /*                 ：iIraijikan            依頼時間                          */
  /*                 ：iSyoriMode            処理モード                        */
  /*****************************************************************************/
  PROCEDURE D1502000(iIraidd    IN VARCHAR2
                    ,iIraiJikan IN VARCHAR2
                    ,iSyoriMode IN VARCHAR2) IS
    ---------------------------
    -- 変数定義
    ---------------------------
    wPrcsjotai S07T004T.PRCSJOTAI%TYPE; -- 処理状態
    wPrcsMode  S07T004T.PRCSMODE%TYPE;  -- 処理モード

	-- ACCIS2023-6671 start-ABI
    -- 自律型トランザクション プラグマ
    PRAGMA AUTONOMOUS_TRANSACTION;
    -- ACCIS2023-6671 end-ABI
  BEGIN
    -- 次処理初期化
    vNextStep := NULL;

    BEGIN
      -- 処理状態、処理モード取得（請求締処理管理テーブル）
      SELECT
          PRCSJOTAI, PRCSMODE
      INTO
          wPrcsjotai, wPrcsMode
      FROM
          S07T004T
      WHERE
              APKIHONCD = vRecordS07T001T.APKIHONCD
          AND SHOPCD    = vRecordS07T001T.SHOPCD
      FOR UPDATE;

      -- 処理状態：「終了／待機」の場合
      IF wPrcsjotai = vPRCSJOTAI_COMPLETE THEN
        RAISE_APPLICATION_ERROR(-20008, '実行可能な状態ではないため、処理を続行できません。');

      -- 処理状態：「処理開始」の場合
      ELSIF wPrcsjotai = vPRCSJOTAI_START THEN
        -- 請求締処理管理テーブル.処理モードが引数.処理モードと同じ場合
        IF wPrcsMode = iSyoriMode THEN
          -- 請求締処理管理テーブル更新（処理状態：「実行中」）
          UPDATE S07T004T
          SET
                PRCSJOTAI       = vPRCSJOTAI_RUNNING
              , PRCSMODE        = iSyoriMode
              , PRCSSTRTDD      = TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
              , PRCSSTRTJIKAN   = TO_CHAR(CURRENT_DATE, 'hh24miss')
              , KOSHAAPKIHONCD  = vRecordS07T001T.KOSHAAPKIHONCD
              , KOSHASHOPCD     = vRecordS07T001T.KOSHASHOPCD
              , KOSHACD         = vRecordS07T001T.KOSHACD
              , KSHNSYSTMYMD    = vRecordS07T001T.KSHNSYSTMYMD
              , KSHNSYSTMJIKAN  = vRecordS07T001T.KSHNSYSTMJIKAN
              , KSHNUNYOYMD     = vRecordS07T001T.KSHNUNYOYMD
              , KSHNIPADRSE     = vRecordS07T001T.KSHNIPADRSE
              , KSHNPGMID       = vRecordS07T001T.KSHNPGMID
          WHERE
                  APKIHONCD     = vRecordS07T001T.APKIHONCD
              AND SHOPCD        = vRecordS07T001T.SHOPCD
          ;

          -- 処理モード：「1:月締請求処理」の場合
          IF iSyoriMode = '1' THEN
            -- 請求締処理詳細管理テーブル更新
            UpdateS07T005TProcessStart(vPRCSID_D1502010); -- （処理ID：「ワーククリア」）
            UpdateS07T005TProcessStart(vPRCSID_D1502100); -- （処理ID：「請求締解除処理」）
            UpdateS07T005TProcessStart(vPRCSID_D1502020); -- （処理ID：「得意先抽出」）
            UpdateS07T005TProcessStart(vPRCSID_D1502040); -- （処理ID：「請求締処理」）
            UpdateS07T005TProcessStart(vPRCSID_D1502060); -- （処理ID：「マスタ反映」）

            -- 次処理を「ワーククリア」に設定
            vNextStep := vPRCSID_D1502010;
          END IF;
        ELSE
          RAISE_APPLICATION_ERROR(-20005, '実行可能な状態ではないか、他の締め処理実行中のため、処理を続行できません。');
        END IF;

      -- 処理状態：「実行中」の場合
      ELSIF wPrcsjotai = vPRCSJOTAI_RUNNING THEN
        RAISE_APPLICATION_ERROR(-20001, '既に実行中のため、処理を続行できません。');

      -- 処理状態：「リカバリ実行待」の場合
      ELSIF wPrcsjotai = vPRCSJOTAI_RECOV_WAIT THEN
        BEGIN
          -- 処理ID（次処理）取得（請求締処理詳細管理テーブル）
          SELECT
              PRCSID
          INTO
              vNextStep
          FROM
              S07T005T
          WHERE
                  APKIHONCD = vRecordS07T001T.APKIHONCD
              AND SHOPCD    = vRecordS07T001T.SHOPCD
              AND PRCSJOTAI = vPRCSJOTAI_RECOV_WAIT
          ;

          -- 請求締処理管理テーブル更新（処理状態：「リカバリ実行中」）
          UPDATE S07T004T
          SET
                PRCSJOTAI       = vPRCSJOTAI_RECOV_RUNNING
              , RECOVSTRTDD     = TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
              , RECOVSTRTJIKAN  = TO_CHAR(CURRENT_DATE, 'hh24miss')
              , RECOVSHRYDD     = NULL
              , RECOVSHRYJIKAN  = NULL
              , MSG             = NULL
              , KOSHAAPKIHONCD  = vRecordS07T001T.KOSHAAPKIHONCD
              , KOSHASHOPCD     = vRecordS07T001T.KOSHASHOPCD
              , KOSHACD         = vRecordS07T001T.KOSHACD
              , KSHNSYSTMYMD    = vRecordS07T001T.KSHNSYSTMYMD
              , KSHNSYSTMJIKAN  = vRecordS07T001T.KSHNSYSTMJIKAN
              , KSHNUNYOYMD     = vRecordS07T001T.KSHNUNYOYMD
              , KSHNIPADRSE     = vRecordS07T001T.KSHNIPADRSE
              , KSHNPGMID       = vRecordS07T001T.KSHNPGMID
          WHERE
                  APKIHONCD     = vRecordS07T001T.APKIHONCD
              AND SHOPCD        = vRecordS07T001T.SHOPCD
          ;
        EXCEPTION
          -- 請求締処理詳細管理テーブルデータが存在しない場合
          WHEN  NO_DATA_FOUND   THEN
            RAISE_APPLICATION_ERROR(-20009, 'リカバリ実行の開始ポイントが見つかりません。');

          -- 請求締処理詳細管理テーブルデータが複数存在する場合
          WHEN TOO_MANY_ROWS THEN
            RAISE_APPLICATION_ERROR(-20010, 'リカバリ実行の開始ポイントが複数見つかりました。');
        END;

      -- 処理状態：「リカバリ実行中」の場合
      ELSIF wPrcsjotai = vPRCSJOTAI_RECOV_RUNNING THEN
        RAISE_APPLICATION_ERROR(-20003, '現在リカバリ実行中のため、処理を続行できません。');

      -- 処理状態：「エラー」の場合
      ELSIF wPrcsjotai = vPRCSJOTAI_ABORT THEN
        RAISE_APPLICATION_ERROR(-20011, 'エラーで終了しています。エラーの原因を解消しリカバリ実行を行うか、データを戻して再度実行してください。');
      END IF;

      -- ACCIS2023-6671 start-ABI ※条件部分を除去
      COMMIT; -- トランザクションコミット
      -- ACCIS2023-6671 end-ABI
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20012, '請求締処理の管理情報が存在しないため、処理を続行できません。');
    END;
  END D1502000;

  /*****************************************************************************/
  /* プロシージャ名  ：ワークデータクリア処理                                  */
  /*****************************************************************************/
  PROCEDURE D1502010 IS
  BEGIN
    -- 請求締処理詳細管理テーブル更新（処理状態：「実行中」）
    UpdateS07T005TProcessStatus(vPRCSID_D1502010, vPRCSJOTAI_RUNNING, NULL);
    -- 請求締対象得意先ワークデータ削除
    DELETE FROM S07W006T WHERE APKIHONCD = vRecordS07T001T.APKIHONCD AND SHOPCD = vRecordS07T001T.SHOPCD;
    -- ACCIS2023-6671 start-ABI ※ここでコミットしない
    -- ACCIS2023-6671 end-ABI
    -- 請求締処理詳細管理テーブル更新（処理状態：「終了／待機」）
    UpdateS07T005TProcessStatus(vPRCSID_D1502010, vPRCSJOTAI_COMPLETE, NULL);
  EXCEPTION
    WHEN OTHERS THEN
      -- トランザクションロールバック
      ROLLBACK;
      -- 請求締処理詳細管理テーブル更新（処理状態：「エラー」）
      UpdateS07T005TProcessStatus(
        vPRCSID_D1502010,
        vPRCSJOTAI_ABORT,
        SUBSTRB(SQLERRM || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || DBMS_UTILITY.FORMAT_ERROR_STACK, 1, 512));
      RAISE;
  END D1502010;

  /*****************************************************************************/
  /* プロシージャ名  ：得意先抽出処理                                          */
  /* 引数            ：iIraidd               依頼日                            */
  /*                 ：iIraijikan            依頼時間                          */
  /*****************************************************************************/
  PROCEDURE D1502020(iIraidd    IN VARCHAR2
                    ,iIraiJikan IN VARCHAR2) IS
    ---------------------------
    -- カーソル定義
    ---------------------------
    -- 請求先コード、締処理区分取得（請求締ワーク）カーソル
    CURSOR curS07W003T IS
      SELECT
          SKYSAKICD, SHIMEPRCSKBN
      FROM
          S07W003T
      WHERE
              APKIHONCD = vRecordS07T001T.APKIHONCD
          AND SHOPCD    = vRecordS07T001T.SHOPCD
          AND IRAIDD    = iIraidd
          AND IRAIJIKAN = iIraiJikan
      ;

    -- 得意先コード、今回締日、繰越金額（請求残高テーブル）カーソル
    CURSOR curS07T001T(pSkysakicd S07T001T.SKYSAKICD%TYPE) IS
      SELECT
          TOKUISAKICD, KONKAISHIMEDD, KURIKOSHIKIN
      FROM
          (
            SELECT
                  /*+ INDEX(SZDT S07T001T_IX5) */
                  SZDT.TOKUISAKICD
                , SZDT.KONKAISHIMEDD
                , SZDT.KURIKOSHIKIN
                , ROW_NUMBER() OVER (PARTITION BY SZDT.TOKUISAKICD ORDER BY SZDT.KONKAISHIMEDD DESC, SZDT.RENBAN DESC) AS ROWNO
            FROM
                S07T001T SZDT
            WHERE
                    SZDT.APKIHONCD        = vRecordS07T001T.APKIHONCD
                AND SZDT.SHOPCD           = vRecordS07T001T.SHOPCD
                AND SZDT.SKYSAKICD        = pSkysakicd
                AND SZDT.YUKOKBN          = '1'
                AND SZDT.KONKAISHIMEDD    <= vRecordS07T001T.KONKAISHIMEDD
          )
      WHERE
          ROWNO = 1
      ;

    ---------------------------
    -- レコード定義
    ---------------------------
    recS07W003T    curS07W003T%ROWTYPE;          -- 請求締ワークカーソルレコード

    ---------------------------
    -- 変数定義
    ---------------------------
    wTokuisakicd   S07T001T.TOKUISAKICD%TYPE;    -- 得意先コード
    wKonkaishimedd S07T001T.KONKAISHIMEDD%TYPE;  -- 今回締日
  BEGIN
    -- 請求締処理詳細管理テーブル更新
    UpdateS07T005TProcessStatus(vPRCSID_D1502020, vPRCSJOTAI_RUNNING, NULL);

    -- 請求締ワークカーソルループ
    FOR recS07W003T IN curS07W003T LOOP
      -- 請求締用対象得意先ワークデータ生成
      INSERT INTO S07W006T(
            APKIHONCD
          , SHOPCD
          , SKYSAKICD
          , TOKUISAKICD
          , SHIMESUMIFLG
          , ZNKSHIMEDD
          , KURIKOSHIKIN
          , SHOHITAXKIN
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
      )
      SELECT
            vRecordS07T001T.APKIHONCD
          , vRecordS07T001T.SHOPCD
          , recS07W003T.SKYSAKICD
          , TOKUISAKICD
          , '0'
          , '00000000'
          , 0
          , 0
          , vRecordS07T001T.TOSHAAPKIHONCD
          , vRecordS07T001T.TOSHASHOPCD
          , vRecordS07T001T.TOSHACD
          , vRecordS07T001T.INPTSYSTMYMD
          , vRecordS07T001T.INPTSYSTMJIKAN
          , vRecordS07T001T.INPTUNYOYMD
          , vRecordS07T001T.INPTIPADRSE
          , vRecordS07T001T.INPTPGMID
          , vRecordS07T001T.KOSHAAPKIHONCD
          , vRecordS07T001T.KOSHASHOPCD
          , vRecordS07T001T.KOSHACD
          , vRecordS07T001T.KSHNSYSTMYMD
          , vRecordS07T001T.KSHNSYSTMJIKAN
          , vRecordS07T001T.KSHNUNYOYMD
          , vRecordS07T001T.KSHNIPADRSE
          , vRecordS07T001T.KSHNPGMID
      FROM
          (
              -- 契約中の得意先
              SELECT
                  /*+ INDEX(TKSM S02M013T_IX6) */
                  TKSM.TOKUISAKICD
              FROM
                S02M013T TKSM
              WHERE
                TKSM.APKIHONCD = vRecordS07T001T.APKIHONCD
                AND TKSM.SHOPCD = vRecordS07T001T.SHOPCD
                AND TKSM.SKYSAKICD = recS07W003T.SKYSAKICD
                AND TKSM.JOTAISTS NOT IN ('02', '06', '10')
                AND TKSM.YUKOKBN = '1'
              -- 契約解除で請求締対象となる売上がある得意先
              UNION
              SELECT
                  /*+ USE_NL(URGT) INDEX(TKSM S02M013T_IX6, URGT S06T001T_IX6) */
                  TKSM.TOKUISAKICD
              FROM
                S02M013T TKSM
              WHERE
                TKSM.APKIHONCD = vRecordS07T001T.APKIHONCD
                AND TKSM.SHOPCD = vRecordS07T001T.SHOPCD
                AND TKSM.SKYSAKICD = recS07W003T.SKYSAKICD
                AND TKSM.JOTAISTS IN ('02', '06', '10')
                AND TKSM.YUKOKBN = '1'
                AND EXISTS (
                  SELECT
                      1
                  FROM
                    S06T001T URGT
                  WHERE
                    URGT.APKIHONCD = TKSM.APKIHONCD
                    AND URGT.SHOPCD = TKSM.SHOPCD
                    AND URGT.TOKUISAKICD = TKSM.TOKUISAKICD
                    AND URGT.URIDD <= vRecordS07T001T.KONKAISHIMEDD
                    AND URGT.URISHIMEPRCSDD <> '00000000'
                    AND URGT.SKYPRCSJISSIDD = '00000000'
                    AND URGT.YUKOKBN = '1'
                )
              -- 請求先に対する売上があるが、親子請求の親得意先が契約解除かつ売上がない場合の親得意先
              UNION
              SELECT
                  /*+ USE_NL(URGT) INDEX(TKSM S02M013T_IX6, URGT S06T001T_IX6) */
                  TKSM.TOKUISAKICD
              FROM
                S02M013T TKSM
              WHERE
                TKSM.APKIHONCD = vRecordS07T001T.APKIHONCD
                AND TKSM.SHOPCD = vRecordS07T001T.SHOPCD
                AND TKSM.SKYSAKICD = recS07W003T.SKYSAKICD
                AND TKSM.TOKUISAKICD = (
                  SELECT
                      SKYM.OYATOKUISAKICD
                  from
                    S02M017T SKYM
                  WHERE
                    SKYM.APKIHONCD = TKSM.APKIHONCD
                    AND SKYM.SHOPCD = TKSM.SHOPCD
                    AND TKSM.SKYSAKICD = SKYM.SKYSAKICD
                    AND SKYM.SKYKBN = '2'
                    AND SKYM.MEISAIPRNTKBN = '1'
                )
                AND TKSM.JOTAISTS IN ('02', '06', '10')
                AND TKSM.YUKOKBN = '1'
                AND EXISTS (
                  SELECT
                      1
                  FROM
                    S02M017T SKYM2
                    INNER JOIN S02M013T TKSM2 ON
                      TKSM2.APKIHONCD = SKYM2.APKIHONCD
                      AND TKSM2.SHOPCD = SKYM2.SHOPCD
                      AND TKSM2.SKYSAKICD = SKYM2.SKYSAKICD
                    INNER JOIN S06T001T URGT ON
                      URGT.APKIHONCD = TKSM2.APKIHONCD
                      AND URGT.SHOPCD = TKSM2.SHOPCD
                      AND URGT.TOKUISAKICD = TKSM2.TOKUISAKICD
                      AND URGT.URIDD <= vRecordS07T001T.KONKAISHIMEDD
                      AND URGT.URISHIMEPRCSDD <> '00000000'
                      AND URGT.SKYPRCSJISSIDD = '00000000'
                      AND URGT.YUKOKBN = '1'
                  WHERE
                    SKYM2.APKIHONCD = TKSM.APKIHONCD
                    AND SKYM2.SHOPCD = TKSM.SHOPCD
                    AND SKYM2.SKYSAKICD = TKSM.SKYSAKICD
                )
              -- 未処理分入金の得意先
              UNION
              SELECT
                  /*+ INDEX(NMST S10T001T_IX6) */
                  NMST.TOKUISAKICD
              FROM
                S10T001T NMST
              WHERE
                NMST.APKIHONCD = vRecordS07T001T.APKIHONCD
                AND NMST.SHOPCD = vRecordS07T001T.SHOPCD
                AND NMST.SKYSAKICD = recS07W003T.SKYSAKICD
                AND NMST.YUKOKBN = '1'
                AND NMST.NYKNDD <= vRecordS07T001T.KONKAISHIMEDD
                AND NMST.URISHIMEPRCSDD <> '00000000'
                AND NMST.SKYPRCSJISSIDD = '00000000'
                AND NMST.YUKOKBN = '1'
          )
      ;
    END LOOP;

    -- 請求締ワークカーソルループ
    FOR recS07W003T IN curS07W003T LOOP
      -- 請求残高テーブルカーソル
      FOR recS07T001T IN curS07T001T(recS07W003T.SKYSAKICD) LOOP
        -- 請求締用対象得意先ワークデータ更新（締済みフラグ、前回締日、繰越金額）
        UPDATE S07W006T TTKW
        SET
              SHIMESUMIFLG        = '0' -- （未締め）
            , ZNKSHIMEDD          = recS07T001T.KONKAISHIMEDD
            , KURIKOSHIKIN        = recS07T001T.KURIKOSHIKIN
        WHERE
                TTKW.APKIHONCD    = vRecordS07T001T.APKIHONCD
            AND TTKW.SHOPCD       = vRecordS07T001T.SHOPCD
            AND TTKW.SKYSAKICD    = recS07W003T.SKYSAKICD
            AND TTKW.TOKUISAKICD  = recS07T001T.TOKUISAKICD
        ;

        -- 更新件数＝0且つ繰越金額≠0の場合
        IF SQL%ROWCOUNT = 0 AND recS07T001T.KURIKOSHIKIN != 0 THEN
          -- ※別請求先に紐づいたが、繰越金額が残っている得意先

          -- 請求締用対象得意先ワークデータ生成
          INSERT INTO S07W006T(
                APKIHONCD
              , SHOPCD
              , SKYSAKICD
              , TOKUISAKICD
              , SHIMESUMIFLG
              , ZNKSHIMEDD
              , KURIKOSHIKIN
              , SHOHITAXKIN
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
                vRecordS07T001T.APKIHONCD
              , vRecordS07T001T.SHOPCD
              , recS07W003T.SKYSAKICD
              , recS07T001T.TOKUISAKICD
              , '0' -- （未締め）
              , recS07T001T.KONKAISHIMEDD
              , recS07T001T.KURIKOSHIKIN
              , 0
              , vRecordS07T001T.TOSHAAPKIHONCD
              , vRecordS07T001T.TOSHASHOPCD
              , vRecordS07T001T.TOSHACD
              , vRecordS07T001T.INPTSYSTMYMD
              , vRecordS07T001T.INPTSYSTMJIKAN
              , vRecordS07T001T.INPTUNYOYMD
              , vRecordS07T001T.INPTIPADRSE
              , vRecordS07T001T.INPTPGMID
              , vRecordS07T001T.KOSHAAPKIHONCD
              , vRecordS07T001T.KOSHASHOPCD
              , vRecordS07T001T.KOSHACD
              , vRecordS07T001T.KSHNSYSTMYMD
              , vRecordS07T001T.KSHNSYSTMJIKAN
              , vRecordS07T001T.KSHNUNYOYMD
              , vRecordS07T001T.KSHNIPADRSE
              , vRecordS07T001T.KSHNPGMID
          );
        END IF;
      END LOOP;
    END LOOP;

    -- ACCIS2023-6671 start-ABI ※ここでコミットしない
    -- ACCIS2023-6671 end-ABI

    -- 請求締処理詳細管理テーブル更新（処理状態：「終了／待機」）
    UpdateS07T005TProcessStatus(vPRCSID_D1502020, vPRCSJOTAI_COMPLETE, NULL);
  EXCEPTION
    WHEN OTHERS THEN
      -- トランザクションロールバック
      ROLLBACK;
      -- 請求締処理詳細管理テーブル更新（処理状態：「エラー」）
      UpdateS07T005TProcessStatus(
        vPRCSID_D1502020,
        vPRCSJOTAI_ABORT,
        SUBSTRB(SQLERRM || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || DBMS_UTILITY.FORMAT_ERROR_STACK, 1, 512));
      RAISE;
  END D1502020;

  /*****************************************************************************/
  /* プロシージャ名  ：請求締解除処理                                          */
  /* 引数            ：iIraidd               依頼日                            */
  /*                 ：iIraijikan            依頼時間                          */
  /*****************************************************************************/
  PROCEDURE D1502100(iIraidd    IN VARCHAR2
                    ,iIraiJikan IN VARCHAR2) IS
    ---------------------------
    -- カーソル定義
    ---------------------------
    -- 請求締ワークカーソル
    CURSOR curS07W003T IS
      SELECT
          SKYSAKICD
      FROM
          S07W003T
      WHERE
              APKIHONCD     = vRecordS07T001T.APKIHONCD
          AND SHOPCD        = vRecordS07T001T.SHOPCD
          AND IRAIDD        = iIraidd
          AND IRAIJIKAN     = iIraiJikan
          AND SHIMEPRCSKBN  = '2'
      ;

    ---------------------------
    -- レコード定義
    ---------------------------
    recS07W003T curS07W003T%ROWTYPE; -- 売上テーブルカーソルレコード

  BEGIN
    -- 請求締処理詳細管理テーブル更新（処理状態：「実行中」）
    UpdateS07T005TProcessStatus(vPRCSID_D1502100, vPRCSJOTAI_RUNNING, NULL);

    -- 請求締ワークカーソルループ
    FOR recS07W003T IN curS07W003T LOOP
      -- 請求残高テーブル（請求先コード）項目値設定
      vRecordS07T001T.SKYSAKICD := recS07W003T.SKYSAKICD;
      -- 請求締解除処理
      SeikyuFixCancelProcess();
    END LOOP;

    -- ACCIS2023-6671 start-ABI ※ここでコミットしない
    -- ACCIS2023-6671 end-ABI

    -- 請求締処理詳細管理テーブル更新（処理状態：「終了／待機」）
    UpdateS07T005TProcessStatus(vPRCSID_D1502100, vPRCSJOTAI_COMPLETE, NULL);

  EXCEPTION
    WHEN OTHERS THEN
      -- トランザクションロールバック
      ROLLBACK;
      -- 請求締処理詳細管理テーブル更新（処理状態：「エラー」）
      UpdateS07T005TProcessStatus(
        vPRCSID_D1502100,
        vPRCSJOTAI_ABORT,
        SUBSTRB(SQLERRM || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || DBMS_UTILITY.FORMAT_ERROR_STACK, 1, 512));
      RAISE;
  END D1502100;

  /*****************************************************************************/
  /* プロシージャ名  ：請求締処理                                              */
  /* 引数            ：iIraidd               依頼日                            */
  /*                 ：iIraijikan            依頼時間                          */
  /*****************************************************************************/
  PROCEDURE D1502040(iIraidd    IN VARCHAR2
                    ,iIraiJikan IN VARCHAR2) IS
    ---------------------------
    -- カーソル定義
    ---------------------------
    -- 請求締用対象得意先ワーク、請求先マスタ結合カーソル
    CURSOR curS07W006TS02M017T IS
      SELECT
            /*+ NO_INDEX_FFS(S07W006T S07W006T_PK) */
            TTSW.SKYSAKICD
          , TTSW.TOKUISAKICD
          , TTSW.SHIMESUMIFLG
          , TTSW.ZNKSHIMEDD
          , TTSW.KURIKOSHIKIN
          , SKSM.KAISHUHOHOKBN
          , SKSM.SHRSAITO
          , SKSM.SHRDD
          , SKSM.SKYSAKIKANRIKBN
          , SKSM.KSIKKNCD
          , SKSM.MEISAIPRNTKBN
          , SKSM.SKYKBN
          , SKSM.OYATOKUISAKICD AS SKSM_OYATOKUISAKICD
          , TKSM.TOKUISAKICD AS TKSM_TOKUISAKICD 
      FROM
          S07W006T TTSW
          INNER JOIN S02M017T SKSM
              ON (
                      SKSM.APKIHONCD  = TTSW.APKIHONCD
                  AND SKSM.SHOPCD     = TTSW.SHOPCD
                  AND SKSM.SKYSAKICD  = TTSW.SKYSAKICD
              )
          LEFT JOIN S02M013T TKSM
              ON (
                      TKSM.APKIHONCD  = TTSW.APKIHONCD
                  AND TKSM.SHOPCD     = TTSW.SHOPCD
                  AND TKSM.TOKUISAKICD= TTSW.TOKUISAKICD
                  AND TKSM.SKYSAKICD  = TTSW.SKYSAKICD
              )
      WHERE
              TTSW.APKIHONCD  = vRecordS07T001T.APKIHONCD
          AND TTSW.SHOPCD     = vRecordS07T001T.SHOPCD
      ORDER BY
              TTSW.SKYSAKICD
      ;

    -- 売上テーブルカーソル
    CURSOR curS06T001T IS
      SELECT
          URIDNPYNO
      FROM
          S06T001T
      WHERE
              APKIHONCD       = vRecordS07T001T.APKIHONCD
          AND SHOPCD          = vRecordS07T001T.SHOPCD
          AND TOKUISAKICD     = vRecordS07T001T.TOKUISAKICD
          AND URIDD           <= vRecordS07T001T.KONKAISHIMEDD
          AND URISHIMEPRCSDD  != '00000000'
          AND SKYPRCSJISSIDD  = '00000000'
          AND YUKOKBN         = '1'
      ORDER BY
          APKIHONCD
          ,SHOPCD
          ,URIDNPYNO
      ;

    ---------------------------
    -- レコード定義
    ---------------------------
    recS07W006TS02M017T curS07W006TS02M017T%ROWTYPE;  -- 請求締用対象得意先ワーク、請求先マスタ結合カーソルレコード
    recS06T001T         curS06T001T%ROWTYPE;          -- 売上テーブルカーソルレコード

    ---------------------------
    -- 変数定義
    ---------------------------
    wSkysakicd          S07W006T.SKYSAKICD%TYPE;      -- 請求先コード
    wSkyshono           S07T001T.SKYSHONO%TYPE;       -- 請求書番号
    wSkyshonoFrom       S07T001T.SKYSHONO%TYPE;       -- 請求書番号（開始値）
    wSkyshonoTo         S07T001T.SKYSHONO%TYPE;       -- 請求書番号（終了値）
    wSkyshonoedano      S07T001T.SKYSHONOEDANO%TYPE;  -- 請求書番号枝番
    wRenban             S07T001T.RENBAN%TYPE;         -- 連番
    wRecordCount        NUMBER;                       -- データ件数
    wUriNyukinExists    BOOLEAN := FALSE;             -- 売上・入金伝票存在有無
    wSum8perUriKin      NUMBER := 0;                  -- 売上金額8％（合計）※親子卸の請求先単位の合算用
    -- add 軽減税率変更対応_LI 20261127 Start
    wSum8perUriKinOld   NUMBER := 0;                  -- 売上金額軽減旧枠（合計）※親子卸の請求先単位の合算用（消費税改修）
    wOldkeigenshohitaxkin NUMBER := 0;                -- 消費税計算軽減旧枠用（消費税改修）
    wTaxRate4Shime      NUMBER;                       -- 税区分4の締日時点税率（新旧判定用）（消費税改修）
    wPrevStrtdd         VARCHAR2(8);                  -- 税区分4の直前期間の適用開始日（消費税改修）
    -- add 軽減税率変更対応_LI 20261127 End
    wSum10perUriKin     NUMBER := 0;                  -- 売上金額10％（合計）※親子卸の請求先単位の合算用
    wN8pershohitaxkin   NUMBER := 0;                  -- 消費税計算8パーセント用
    wN10pershohitaxkin  NUMBER := 0;                  -- 消費税計算10パーセント用
    wSyokaisyori        BOOLEAN := FALSE;             -- 初回処理フラグ
    wOyaCount           NUMBER := 0;                  -- 親子請求の親得意先存在チェック用のカウント
    -- ACCIS2023-6958_start
    wTokuisakicd        S07W006T.TOKUISAKICD%TYPE;    -- 得意先コード
    -- ACCIS2023-6958_end

  BEGIN

    -- 請求締処理詳細管理テーブル更新（処理状態：「実行中」）
    UpdateS07T005TProcessStatus(vPRCSID_D1502040, vPRCSJOTAI_RUNNING, NULL);

    -- カテゴリ別集計用カテゴリコード設定
    SetCategoryCd();

    -- インボイス施行前の場合
    IF vInvoiceExecutionFlg = '0' THEN
      -- 請求先コード初期化（請求先ブレイク用）
      wSkysakicd := 'Ｘ';

      -- 請求締用対象得意先ワーク、請求先マスタ結合カーソルループ
      FOR recS07W006TS02M017T IN curS07W006TS02M017T LOOP
        -- 請求先ブレイク時
        IF wSkysakicd != recS07W006TS02M017T.SKYSAKICD THEN
          -- 請求書番号採番処理
          Z1804B01.getSlipNoSeikyusho(vRecordS07T001T.APKIHONCD, vRecordS07T001T.SHOPCD, 1, wSkyshonoFrom, wSkyshonoTo);

          -- 項目値退避
          wSkysakicd      := recS07W006TS02M017T.SKYSAKICD;   -- 請求先コード
          wSkyshono       := wSkyshonoFrom;                   -- 請求書番号
          wSkyshonoedano  := 0;                               -- 請求書番号枝番

          -- 支払予定日設定
          SetPaymentPlanDate(recS07W006TS02M017T.SHRSAITO, recS07W006TS02M017T.SHRDD);
        END IF;

        -- 請求残高テーブル項目値設定
        -- （請求先コード、得意先コード、前回締日、回収方法区分、決済機関コード、前回繰越金額、繰越金額、税計算区分）
        vRecordS07T001T.SKYSAKICD       := recS07W006TS02M017T.SKYSAKICD;
        vRecordS07T001T.TOKUISAKICD     := recS07W006TS02M017T.TOKUISAKICD;
        vRecordS07T001T.ZNKSHIMEDD      := recS07W006TS02M017T.ZNKSHIMEDD;
        vRecordS07T001T.KAISHUHOHOKBN   := recS07W006TS02M017T.KAISHUHOHOKBN;
        vRecordS07T001T.KSIKKNCD        := recS07W006TS02M017T.KSIKKNCD;
        vRecordS07T001T.ZNKKURIKOSHIKIN := recS07W006TS02M017T.KURIKOSHIKIN;
        vRecordS07T001T.KURIKOSHIKIN    := 0;
        vRecordS07T001T.TAXKEISANKBN    := CASE recS07W006TS02M017T.SKYSAKIKANRIKBN WHEN '2' THEN '2' ELSE vTaxCalcDivision END;

        -- 新連番取得
        wRenban := GetNewRenban();

        -- 請求締処理
        SeikyuFixProcess(wSkyshono, NULL, wSkyshonoedano, wRenban,
          (CASE WHEN recS07W006TS02M017T.TKSM_TOKUISAKICD IS NULL THEN '0' ELSE '1' END), '1');

        -- 請求書番号枝番更新
        wSkyshonoedano := wSkyshonoedano + 1;
      END LOOP;

    -- インボイス施行後の場合
    ELSE
      -- 請求締用対象得意先ワーク、請求先マスタ結合カーソルループ
      FOR recS07W006TS02M017T IN curS07W006TS02M017T LOOP

        -- 請求残高テーブル項目値設定
        -- （請求先コード、得意先コード、前回締日、回収方法区分、決済機関コード、前回繰越金額、繰越金額、税計算区分）
        vRecordS07T001T.SKYSAKICD       := recS07W006TS02M017T.SKYSAKICD;
        vRecordS07T001T.TOKUISAKICD     := recS07W006TS02M017T.TOKUISAKICD;
        vRecordS07T001T.ZNKSHIMEDD      := recS07W006TS02M017T.ZNKSHIMEDD;
        vRecordS07T001T.KAISHUHOHOKBN   := recS07W006TS02M017T.KAISHUHOHOKBN;
        vRecordS07T001T.KSIKKNCD        := recS07W006TS02M017T.KSIKKNCD;
        vRecordS07T001T.ZNKKURIKOSHIKIN := recS07W006TS02M017T.KURIKOSHIKIN;
        vRecordS07T001T.KURIKOSHIKIN    := 0;
        vRecordS07T001T.TAXKEISANKBN    := CASE recS07W006TS02M017T.SKYSAKIKANRIKBN WHEN '2' THEN '2' ELSE vTaxCalcDivision END;

        -- 支払予定日設定
        SetPaymentPlanDate(recS07W006TS02M017T.SHRSAITO, recS07W006TS02M017T.SHRDD);

        -- 新連番取得
        wRenban := GetNewRenban();

        -- 回収方法区分：「代引き」の場合
        IF vRecordS07T001T.KAISHUHOHOKBN = '7' THEN
          -- ACCIS2023-6675 請求先ごとにフラグ（代引の場合しか使用しない）の値をリセットする処理を追加
          wUriNyukinExists := FALSE;
          IF recS07W006TS02M017T.TKSM_TOKUISAKICD IS NOT NULL THEN
            -- 売上テーブルカーソルループ
            FOR recS06T001T IN curS06T001T LOOP
              -- ACISS2023-6662 代引請求先の月締請求残高締解除後の請求締対応：請求書番号枝番を決定する処理を追加
              wSkyshonoedano := GetSkyshoBranchNumber(vRecordS07T001T.APKIHONCD, vRecordS07T001T.SHOPCD, recS06T001T.URIDNPYNO);
              -- 請求締処理
              SeikyuFixProcess(recS06T001T.URIDNPYNO, recS06T001T.URIDNPYNO, wSkyshonoedano, wRenban, '1', '1');
              -- 今作成した請求残高の繰越金額を、次に作る請求残高の前回繰越金額に設定する（代引は伝票単位に請求締するため）
              vRecordS07T001T.ZNKKURIKOSHIKIN := vRecordS07T001T.KURIKOSHIKIN;
              -- 連番加算
              wRenban := wRenban + 1;
              wUriNyukinExists := TRUE;
            END LOOP;
          END IF;

          -- 入金明細テーブル取得
          SELECT
              COUNT(APKIHONCD)
          INTO
              wRecordCount
          FROM
              S10T001T
          WHERE
                  APKIHONCD       = vRecordS07T001T.APKIHONCD
              AND SHOPCD          = vRecordS07T001T.SHOPCD
              AND SKYSAKICD       = vRecordS07T001T.SKYSAKICD
              AND TOKUISAKICD     = vRecordS07T001T.TOKUISAKICD
              AND NYKNDD          <= vRecordS07T001T.KONKAISHIMEDD
              AND URISHIMEPRCSDD  != '00000000'
              AND SKYPRCSJISSIDD  = '00000000'
              AND YUKOKBN         = '1'
          ;

          -- 入金明細テーブルデータが存在する場合
          IF wRecordCount > 0 THEN
            -- 請求書番号採番処理
            Z1804B01.getSlipNoSeikyusho(vRecordS07T001T.APKIHONCD, vRecordS07T001T.SHOPCD, 1, wSkyshonoFrom, wSkyshonoTo);
            -- 請求締処理
            SeikyuFixProcess(wSkyshonoFrom, NULL, 0, wRenban, '0', '1');
            wUriNyukinExists := TRUE;
          END IF;

          -- 売上伝票・入金伝票存在無しの場合
          IF NOT(wUriNyukinExists) THEN
            -- 請求書番号採番処理
            Z1804B01.getSlipNoSeikyusho(vRecordS07T001T.APKIHONCD, vRecordS07T001T.SHOPCD, 1, wSkyshonoFrom, wSkyshonoTo);
            -- 請求締処理
            SeikyuFixProcess(wSkyshonoFrom, NULL, 0, wRenban, '0', '0');
          END IF;

          -- ACCIS2023-6893 start-ABI
          -- 未来請求残高再計算処理
          ReCalculate();
          -- ACCIS2023-6893 end-ABI

        -- 回収方法区分：「代引き」以外の場合
        ELSE
          -- ACCIS2023-6958_start
          -- 請求区分が親子の条件を削除（親子解除した際に請求残高が残っていれば、解除前の請求先で請求残高を作成しているため）
          -- また、請求書番号が同一で採番されず、親子卸にも関わらず別々で印字されてしまう事象を考慮する対応
          -- ACCIS2023-5925_start
          IF '1' = recS07W006TS02M017T.MEISAIPRNTKBN THEN
          -- ACCIS2023-6958_end

            -- 請求先単位で初回レコードのみ（初回処理フラグ）で制御する
            if NOT(wSyokaisyori) THEN
              -- 請求先コード初期化（請求先ブレイク用）
              wSkysakicd := 'Ｘ';
              -- 初回処理フラグをTRUEに更新
              wSyokaisyori := TRUE;
            END IF;

            -- 請求先ブレイク時
            IF wSkysakicd != recS07W006TS02M017T.SKYSAKICD THEN

              -- 親得意先存在チェック（処理対象となる親子請求先の親得意先となっている得意先の請求先コードが同じかをチェックする）
              SELECT
                COUNT(1)
              INTO
                wOyaCount
              FROM
                S02M013T TKSM
              WHERE
                TKSM.YUKOKBN = '1'
                AND TKSM.APKIHONCD   = vRecordS07T001T.APKIHONCD
                AND TKSM.SHOPCD      = vRecordS07T001T.SHOPCD
                AND TKSM.SKYSAKICD   = recS07W006TS02M017T.SKYSAKICD
                AND TKSM.TOKUISAKICD = recS07W006TS02M017T.SKSM_OYATOKUISAKICD
              ;
              -- ACCIS2023-6958_start
              -- 請求区分が一般の場合は、必ずしも親得意先コードが設定されているわけではないため、親子の時のみチェックする条件を追加
              IF ('2' = recS07W006TS02M017T.SKYKBN AND wOyaCount = 0) THEN
                -- 請求先の親得意先になっている得意先の請求先が一致しない場合は、親得意先が存在しないとしてエラーにする
                RAISE_APPLICATION_ERROR(-20016, '親得意先コードが存在しません。請求先CD：' || recS07W006TS02M017T.SKYSAKICD);
              END IF;
              -- ACCIS2023-6958_end

              -- 請求書番号採番処理
              Z1804B01.getSlipNoSeikyusho(vRecordS07T001T.APKIHONCD, vRecordS07T001T.SHOPCD, 1, wSkyshonoFrom, wSkyshonoTo);

              -- 項目値退避
              wSkysakicd      := recS07W006TS02M017T.SKYSAKICD;   -- 請求先コード
              wSkyshono       := wSkyshonoFrom;                   -- 請求書番号
              wSkyshonoedano  := 0;                               -- 請求書番号枝番

              -- 支払予定日設定
              SetPaymentPlanDate(recS07W006TS02M017T.SHRSAITO, recS07W006TS02M017T.SHRDD);

              -- modify 軽減税率変更対応_LI 20261127 Start
              -- 消費税改修：新旧判定用に税区分4の締日時点税率と直前期間の適用開始日を取得する
              SELECT SHOHITAX INTO wTaxRate4Shime FROM S02M091T
               WHERE TAXKBN = '4' AND TEKIYOSTRTDD <= vRecordS07T001T.KONKAISHIMEDD
                 AND TEKIYOSHRYDD >= vRecordS07T001T.KONKAISHIMEDD AND YUKOKBN = '1';
              SELECT MAX(TEKIYOSTRTDD) INTO wPrevStrtdd FROM S02M091T
               WHERE TAXKBN = '4' AND TEKIYOSHRYDD < vRecordS07T001T.KONKAISHIMEDD AND YUKOKBN = '1';

              -- 売上テーブル.売上金額を請求先単位かつ税計算区分毎に集計する
              SELECT
                -- １０％税抜売上金額
                NVL(SUM(CASE WHEN UMST.KEIJOKBN IN ('01', '02') AND UMST.TAXKBN = '1' THEN UMST.KIN ELSE 0 END), 0) AS N10PERTAXNUKIURIKIN
                -- ８％税抜売上金額
                , NVL(SUM(CASE WHEN UMST.KEIJOKBN IN ('01', '02') AND UMST.TAXKBN IN ('4', '6', '7') AND TAXM.SHOHITAX = wTaxRate4Shime THEN UMST.KIN ELSE 0 END), 0) AS N8PERTAXNUKIURIKIN
                -- 軽減旧枠売上金額（売上日時点の税率が締日時点税率と異なる分）
                , NVL(SUM(CASE WHEN UMST.KEIJOKBN IN ('01', '02') AND UMST.TAXKBN IN ('4', '6', '7') AND (TAXM.SHOHITAX IS NULL OR TAXM.SHOHITAX != wTaxRate4Shime) THEN UMST.KIN ELSE 0 END), 0) AS OLDKEIGENTAXNUKIURIKIN
                INTO
                  wSum10perUriKin,
                  wSum8perUriKin,
                  wSum8perUriKinOld
              -- modify 軽減税率変更対応_LI 20261127 End
              FROM
                S06T001T HEAD
                INNER JOIN S06T002T UMST
                ON (
                      HEAD.APKIHONCD      = UMST.APKIHONCD
                  AND HEAD.SHOPCD         = UMST.SHOPCD
                  AND HEAD.URIDNPYNO      = UMST.URIDNPYNO
                  AND HEAD.EDANO          = UMST.EDANO
                  AND HEAD.YUKOKBN        = '1'
                  AND UMST.NYURYOKUMEISAIFLG = '1'
                -- add 軽減税率変更対応_LI 20261127 Start
                )
                LEFT JOIN S02M091T TAXM
                ON (
                      TAXM.TAXKBN       = UMST.TAXKBN
                  AND TAXM.TEKIYOSTRTDD <= HEAD.URIDD
                  AND TAXM.TEKIYOSHRYDD >= HEAD.URIDD
                  AND TAXM.YUKOKBN      = '1'
                -- add 軽減税率変更対応_LI 20261127 End
                )
              WHERE
                HEAD.APKIHONCD           = vRecordS07T001T.APKIHONCD
                AND HEAD.SHOPCD          = vRecordS07T001T.SHOPCD
                AND HEAD.TOKUISAKICD     IN (
                  SELECT
                    TOKUISAKICD
                  FROM S02M013T TKSM
                  WHERE
                    TKSM.APKIHONCD = HEAD.APKIHONCD
                    AND TKSM.SHOPCD = HEAD.SHOPCD
                    AND TKSM.SKYSAKICD = wSkysakicd
                    AND TKSM.YUKOKBN = '1'
                )
                AND HEAD.URIDD           <= vRecordS07T001T.KONKAISHIMEDD
                AND HEAD.URISHIMEPRCSDD  != '00000000'
                AND HEAD.SKYPRCSJISSIDD  IN ('00000000', 'DAIBIKI')
                AND HEAD.YUKOKBN         = '1'
              ;

              -- 請求先単位でサマリーした売上金額の消費税計算を行う。
              -- 8％消費税算出
              wN8pershohitaxkin := Z1808B01.SyohizeiKeisan( '1', '1', '4', NULL, vRecordS07T001T.KONKAISHIMEDD,
                vRecordS07T001T.APKIHONCD, vRecordS07T001T.SHOPCD, NULL, 0, wSum8perUriKin);

              -- 10％消費税算出
              wN10pershohitaxkin := Z1808B01.SyohizeiKeisan('1', '1', '1', NULL, vRecordS07T001T.KONKAISHIMEDD,
                vRecordS07T001T.APKIHONCD, vRecordS07T001T.SHOPCD, NULL, 0, wSum10perUriKin);
-- add 軽減税率変更対応_LI 20261127 Start

              -- 軽減旧枠消費税算出（税区分4の直前期間の税率で計算する。直前期間が無い場合は旧枠自体が発生しない）
              wOldkeigenshohitaxkin := 0;
              IF wPrevStrtdd IS NOT NULL THEN
                wOldkeigenshohitaxkin := Z1808B01.SyohizeiKeisan( '1', '1', '4', NULL, wPrevStrtdd,
                  vRecordS07T001T.APKIHONCD, vRecordS07T001T.SHOPCD, NULL, 0, wSum8perUriKinOld);
              END IF;
-- add 軽減税率変更対応_LI 20261127 End

            END IF;

            -- 請求残高テーブル項目値設定
            -- （請求先コード、得意先コード、前回締日、回収方法区分、決済機関コード、前回繰越金額、繰越金額、税計算区分）
            vRecordS07T001T.SKYSAKICD       := recS07W006TS02M017T.SKYSAKICD;
            vRecordS07T001T.TOKUISAKICD     := recS07W006TS02M017T.TOKUISAKICD;
            vRecordS07T001T.ZNKSHIMEDD      := recS07W006TS02M017T.ZNKSHIMEDD;
            vRecordS07T001T.KAISHUHOHOKBN   := recS07W006TS02M017T.KAISHUHOHOKBN;
            vRecordS07T001T.KSIKKNCD        := recS07W006TS02M017T.KSIKKNCD;
            vRecordS07T001T.ZNKKURIKOSHIKIN := recS07W006TS02M017T.KURIKOSHIKIN;
            vRecordS07T001T.KURIKOSHIKIN    := 0;
            vRecordS07T001T.TAXKEISANKBN    := CASE recS07W006TS02M017T.SKYSAKIKANRIKBN WHEN '2' THEN '2' ELSE vTaxCalcDivision END;

            -- 新連番取得
            wRenban := GetNewRenban();

            -- ACCIS2023-6958_start
            -- 消費税を計上する際の条件として、親子の場合は親得意先コードで問題ないが、
            -- 一般の場合は、親得意先コードの設定は必須ではないため、自身の得意先コードで判定する。
            IF ('2' = recS07W006TS02M017T.SKYKBN) THEN
                wTokuisakicd := recS07W006TS02M017T.SKSM_OYATOKUISAKICD;
            ELSE
                wTokuisakicd := recS07W006TS02M017T.TOKUISAKICD;
            END IF;

            -- 得意先コードの比較は、請求区分によって値を読み分ける
            -- TKSM_TOKUISAKICD：親子解除または請求先コードを変更した場合は、条件に合致しないためNULLとなる。
            IF (recS07W006TS02M017T.TKSM_TOKUISAKICD != wTokuisakicd) THEN
              -- 請求締処理（親子）（消費税金額は0を設定）
              SeikyuFixProcessOyako(wSkyshono, NULL, wSkyshonoedano, wRenban,
                -- modify 軽減税率変更対応_LI 20261127 Start
                (CASE WHEN recS07W006TS02M017T.TKSM_TOKUISAKICD IS NULL THEN '0' ELSE '1' END), '1', 0, 0, 0);
            ELSE
              -- 請求締処理（親子）（消費税金額はサマリーした結果を設定）
              SeikyuFixProcessOyako(wSkyshono, NULL, wSkyshonoedano, wRenban,
                (CASE WHEN recS07W006TS02M017T.TKSM_TOKUISAKICD IS NULL THEN '0' ELSE '1' END), '1', wN8pershohitaxkin, wN10pershohitaxkin, wOldkeigenshohitaxkin);
                -- modify 軽減税率変更対応_LI 20261127 End
            END IF;
            -- ACCIS2023-6958_end

            -- ACCIS2023-6992 start-ABI
            -- 請求区分が1（一般）の場合、未来請求残高再計算処理
            IF '1' = recS07W006TS02M017T.SKYKBN THEN
              ReCalculate();
            END IF;
            -- ACCIS2023-6992 end-ABI

            -- 請求書番号枝番更新
            wSkyshonoedano := wSkyshonoedano + 1;
          ELSE
            -- 請求書番号採番処理
            Z1804B01.getSlipNoSeikyusho(vRecordS07T001T.APKIHONCD, vRecordS07T001T.SHOPCD, 1, wSkyshonoFrom, wSkyshonoTo);
            -- 請求締処理
            SeikyuFixProcess(wSkyshonoFrom, NULL, 0, wRenban,
              (CASE WHEN recS07W006TS02M017T.TKSM_TOKUISAKICD IS NULL THEN '0' ELSE '1' END), '1');
            -- 未来請求残高再計算処理
            ReCalculate();
          END IF;
          --ACCIS2023-5925_end
        END IF;
      END LOOP;
      -- 初回処理をFALSEに更新
      wSyokaisyori := FALSE;
    END IF;

    -- ACCIS2023-6671 start-ABI ※ここでコミットしない
    -- ACCIS2023-6671 end-ABI

    -- 請求締処理詳細管理テーブル更新（処理状態：「終了／待機」）
    UpdateS07T005TProcessStatus(vPRCSID_D1502040, vPRCSJOTAI_COMPLETE, NULL);
  EXCEPTION
    WHEN OTHERS THEN
      -- トランザクションロールバック
      ROLLBACK;
      -- 請求締処理詳細管理テーブル更新（処理状態：「エラー」）
      UpdateS07T005TProcessStatus(
        vPRCSID_D1502040,
        vPRCSJOTAI_ABORT,
        SUBSTRB(SQLERRM || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || DBMS_UTILITY.FORMAT_ERROR_STACK, 1, 512));
      RAISE;
  END D1502040;

  /*****************************************************************************/
  /* プロシージャ名  ：マスタ反映                                              */
  /*****************************************************************************/
  PROCEDURE D1502060 IS
  BEGIN
    -- 請求締処理詳細管理テーブル更新（処理状態：「実行中」）
    UpdateS07T005TProcessStatus(vPRCSID_D1502060, vPRCSJOTAI_RUNNING, NULL);
    -- マスタ反映処理
    MasterUpdateProcess(0);
    -- 請求締処理詳細管理テーブル更新（処理状態：「終了／待機」）
    UpdateS07T005TProcessStatus(vPRCSID_D1502060, vPRCSJOTAI_COMPLETE, NULL);
  EXCEPTION
    WHEN OTHERS THEN
      -- トランザクションロールバック
      ROLLBACK;
      -- 請求締処理詳細管理テーブル更新（処理状態：「エラー」）
      UpdateS07T005TProcessStatus(
        vPRCSID_D1502060,
        vPRCSJOTAI_ABORT,
        SUBSTRB(SQLERRM || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || DBMS_UTILITY.FORMAT_ERROR_STACK, 1, 512));
      RAISE;
  END D1502060;

  /*****************************************************************************/
  /* プロシージャ名  ：処理終了判定処理                                        */
  /*****************************************************************************/
  PROCEDURE D1502999 IS
    ---------------------------
    -- 変数定義
    ---------------------------
    wRecordCount NUMBER;                                                            -- データ件数
    wMsg         S07T005T.MSG%TYPE DEFAULT '請求締め処理でエラーが発生しました。';  -- メッセージ
  BEGIN
    -- 請求締処理詳細管理テーブルデータ件数取得（処理状態：「終了／待機」）
    SELECT
        COUNT(*)
    INTO
        wRecordCount
    FROM
        S07T005T
    WHERE
            APKIHONCD = vRecordS07T001T.APKIHONCD
        AND SHOPCD    = vRecordS07T001T.SHOPCD
        AND PRCSJOTAI != vPRCSJOTAI_COMPLETE
    ;

    -- 請求締処理詳細管理テーブルデータ件数＝0の場合
    IF wRecordCount = 0 THEN
      -- 請求締処理管理テーブル更新（処理状態：「終了／待機」）
      UpdateS07T004TProcessEnd();
    ELSE
      -- 請求締処理管理テーブル更新（処理状態：「エラー」）
      UpdateS07T004TProcessError(wMsg);
    END IF;

    -- ACCIS2023-6671 start-ABI ※ここでコミットしない
    -- ACCIS2023-6671 end-ABI

    -- 請求締処理詳細管理テーブルデータ件数＝0の場合
    IF wRecordCount > 0 THEN
      RAISE_APPLICATION_ERROR(-20013, wMsg);
    END IF;
  END D1502999;

  /*****************************************************************************/
  /* プロシージャ名  ：請求締実行チェック＆開始                                */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iUserApkihoncd        担当者ＡＰ基本コード              */
  /*                 ：iUserShopcd           担当者ショップコード              */
  /*                 ：iTnshacd              担当者コード                      */
  /*                 ：iIpadrse              ＩＰアドレス                      */
  /*                 ：iIraidd               依頼日                            */
  /*                 ：iIraijikan            依頼時間                          */
  /*                 ：iSkyshimedd           請求締日                          */
  /*                 ：iSyoriMode            処理モード（1:請求締め 2:締解除） */
  /*****************************************************************************/
  PROCEDURE SeikyushimeCheckAndGo(iApkihoncd     IN VARCHAR2
                                 ,iShopcd        IN VARCHAR2
                                 ,iUserApkihoncd IN VARCHAR2
                                 ,iUserShopcd    IN VARCHAR2
                                 ,iTnshacd       IN VARCHAR2
                                 ,iIpadrse       IN VARCHAR2
                                 ,iIraidd        IN VARCHAR2
                                 ,iIraijikan     IN VARCHAR2
                                 ,iSkyshimedd    IN VARCHAR2
                                 ,iSyoriMode     IN VARCHAR2) IS
    ---------------------------
    -- 変数定義
    ---------------------------
    wPrcsjotai S07T004T.PRCSJOTAI%TYPE;   -- 処理状態
    wPrcsmode  S07T004T.PRCSMODE%TYPE;    -- 処理モード
    wUnyoDate  S07T001T.KSHNUNYOYMD%TYPE; -- 運用日付
  BEGIN
    -- 運用日付取得
    wUnyoDate := GetOperationDate(iApkihoncd, iShopcd);

    BEGIN
      -- 処理状態、処理モード取得＆排他制御（請求締処理管理テーブル）
      SELECT PRCSJOTAI, PRCSMODE INTO wPrcsjotai, wPrcsmode FROM S07T004T WHERE APKIHONCD = iApkihoncd AND SHOPCD = iShopcd FOR UPDATE;
    EXCEPTION
      -- 請求締処理管理テーブルが存在しない場合
      WHEN NO_DATA_FOUND THEN
        -- 請求締処理管理テーブルデータ生成
        INSERT INTO S07T004T (
            APKIHONCD
          , SHOPCD
          , SHIMEDYDM
          , SHIMEDD
          , KONKAISHIMEDD
          , IRAIDD
          , IRAIJIKAN
          , PRCSMODE
          , PRCSSTRTDD
          , PRCSSTRTJIKAN
          , PRCSSHRYDD
          , PRCSSHRYJIKAN
          , RECOVSTRTDD
          , RECOVSTRTJIKAN
          , RECOVSHRYDD
          , RECOVSHRYJIKAN
          , PRCSJOTAI
          , MSG
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
          , SUBSTR(iSkyshimedd, 1, 6)
          , SUBSTR(iSkyshimedd, 7, 2)
          , iSkyshimedd
          , iIraidd
          , iIraijikan
          , iSyoriMode
          , NULL
          , NULL
          , NULL
          , NULL
          , NULL
          , NULL
          , NULL
          , NULL
          , vPRCSJOTAI_START
          , NULL
          , iUserApkihoncd
          , iUserShopcd
          , iTnshacd
          , TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
          , TO_CHAR(CURRENT_DATE, 'HH24MISS')
          , wUnyoDate
          , iIpadrse
          , vPROGRAM_ID
          , iUserApkihoncd
          , iUserShopcd
          , iTnshacd
          , TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
          , TO_CHAR(CURRENT_DATE, 'HH24MISS')
          , wUnyoDate
          , iIpadrse
          , vPROGRAM_ID
        );
    END;

    -- 処理状態：「終了／待機」の場合
    IF wPrcsjotai = vPRCSJOTAI_COMPLETE THEN
      -- 請求締処理管理テーブル更新（処理状態：「処理開始」）
      UPDATE S07T004T
      SET
            SHIMEDYDM      = SUBSTR(iSkyshimedd, 1, 6)
          , SHIMEDD        = SUBSTR(iSkyshimedd, 7, 2)
          , KONKAISHIMEDD  = iSkyshimedd
          , IRAIDD         = iIraidd
          , IRAIJIKAN      = iIraijikan
          , PRCSJOTAI      = vPRCSJOTAI_START
          , PRCSMODE       = iSyoriMode
          , PRCSSTRTDD     = NULL
          , PRCSSTRTJIKAN  = NULL
          , PRCSSHRYDD     = NULL
          , PRCSSHRYJIKAN  = NULL
          , RECOVSTRTDD    = NULL
          , RECOVSTRTJIKAN = NULL
          , RECOVSHRYDD    = NULL
          , RECOVSHRYJIKAN = NULL
          , KOSHAAPKIHONCD = iUserApkihoncd
          , KOSHASHOPCD    = iUserShopcd
          , KOSHACD        = iTnshacd
          , KSHNSYSTMYMD   = TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
          , KSHNSYSTMJIKAN = TO_CHAR(CURRENT_DATE, 'HH24MISS')
          , KSHNUNYOYMD    = wUnyoDate
          , KSHNIPADRSE    = iIpadrse
          , KSHNPGMID      = vPROGRAM_ID
      WHERE
              APKIHONCD    = iApkihoncd
          AND SHOPCD       = iShopcd;

    -- 請求締処理管理テーブルの処理モードと同じ場合
    ELSIF NVL(wPrcsmode, iSyoriMode) = iSyoriMode THEN
      -- 処理状態：「処理開始」の場合
      IF wPrcsjotai = vPRCSJOTAI_START THEN
        RAISE_APPLICATION_ERROR(-20001, '既に実行中のため、処理を続行できません。');

      -- 処理状態：「実行中」の場合
      ELSIF wPrcsjotai = vPRCSJOTAI_RUNNING THEN
        RAISE_APPLICATION_ERROR(-20001, '既に実行中のため、処理を続行できません。');

      -- 処理状態：「リカバリ実行待」の場合
      ELSIF wPrcsjotai = vPRCSJOTAI_RECOV_WAIT THEN
        RAISE_APPLICATION_ERROR(-20002, '現在リカバリ実行待ちです。');

      -- 処理状態：「リカバリ実行中」の場合
      ELSIF wPrcsjotai = vPRCSJOTAI_RECOV_RUNNING THEN
        RAISE_APPLICATION_ERROR(-20003, '現在リカバリ実行中のため、処理を続行できません。');

      -- 処理状態：「エラー」の場合
      ELSIF wPrcsjotai = vPRCSJOTAI_ABORT THEN
        RAISE_APPLICATION_ERROR(-20004, 'エラーで終了しています。エラーの原因を解消しリカバリ実行を行うか、データを戻して再度実行してください。');
      END IF;

    -- 請求締処理管理テーブルの処理モードと異なる場合
    ELSE
      RAISE_APPLICATION_ERROR(-20005, '実行可能な状態ではないか、他の締め処理実行中のため、処理を続行できません。');
    END IF;
  EXCEPTION
    WHEN OTHERS THEN
      RAISE;
  END SeikyushimeCheckAndGo;

  /*****************************************************************************/
  /* プロシージャ名  ：請求締処理                                              */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iUserApkihoncd        担当者ＡＰ基本コード              */
  /*                 ：iUserShopcd           担当者ショップコード              */
  /*                 ：iTnshacd              担当者コード                      */
  /*                 ：iIpadrse              ＩＰアドレス                      */
  /*                 ：iIraidd               依頼日                            */
  /*                 ：iIraijikan            依頼時間                          */
  /*                 ：iSkyshimedd           請求締日                          */
  /*****************************************************************************/
  PROCEDURE SeikyuShime(iApkihoncd     IN VARCHAR2
                       ,iShopcd        IN VARCHAR2
                       ,iUserApkihoncd IN VARCHAR2
                       ,iUserShopcd    IN VARCHAR2
                       ,iTnshacd       IN VARCHAR2
                       ,iIpadrse       IN VARCHAR2
                       ,iIraidd        IN VARCHAR2
                       ,iIraiJikan     IN VARCHAR2
                       ,iSkyshimedd    IN VARCHAR2) IS
    ---------------------------
    -- 変数定義
    ---------------------------
    wInvoicShikoYmd           S02M046T.INVOICSHIKOYMD%TYPE;           -- インボイス施行日
    wTaxKeisanKbnUriKirikaeDd S02M046T.TAXKEISANKBNURIKIRIKAEDD%TYPE; -- 税計算区分売上切替日
    wShinTaxKeisanKbnUri      S02M046T.SHINTAXKEISANKBNURI%TYPE;      -- 新税計算区分売上
    wGenzTaxKeisanKbnUri      S02M046T.GENZTAXKEISANKBNURI%TYPE;      -- 現税計算区分売上
    wMsg                      S07T004T.MSG%TYPE;                      -- メッセージ
  BEGIN
    -- 請求残高テーブル項目値設定
    SetS07T001TRecord(iApkihoncd, iShopcd, iTnshacd, iIpadrse, iUserApkihoncd, iUserShopcd, iSkyshimedd);

    -- 請求処理実施区分設定（月締請求処理）
    vRecordS07T001T.SKYPRCSJISSIKBN := 1;

    -- ショップマスタ情報取得（インボイス施行日、税計算区分売上切替日、新税計算区分売上、現税計算区分売上）
    SELECT
        INVOICSHIKOYMD, TAXKEISANKBNURIKIRIKAEDD, SHINTAXKEISANKBNURI, GENZTAXKEISANKBNURI
    INTO
        wInvoicShikoYmd, wTaxKeisanKbnUriKirikaeDd, wShinTaxKeisanKbnUri, wGenzTaxKeisanKbnUri
    FROM
        S02M046T
    WHERE
            APKIHONCD = iApkihoncd
        AND SHOPCD    = iShopcd
    ;

    -- インボイス施行フラグ設定
    IF wInvoicShikoYmd > vRecordS07T001T.KONKAISHIMEDD THEN
      vInvoiceExecutionFlg := '0'; -- インボイス施行前
    ELSE
      vInvoiceExecutionFlg := '1'; -- インボイス施行後
    END IF;

    -- 税計算区分設定
    IF wTaxKeisanKbnUriKirikaeDd > vRecordS07T001T.KONKAISHIMEDD THEN
      vTaxCalcDivision := wGenzTaxKeisanKbnUri; -- 現税計算区分売上
    ELSE
      vTaxCalcDivision := wShinTaxKeisanKbnUri; -- 新税計算区分売上
    END IF;

    BEGIN
      -- 処理開始判定（1:月締請求処理）
      D1502000(iIraidd, iIraiJikan, '1');

      -- 次処理：「ワーククリア」の場合
      IF vNextStep = vPRCSID_D1502010 THEN
        D1502010();                    -- ワークデータクリア処理
        vNextStep := vPRCSID_D1502020; -- 次処理：「得意先抽出」設定
      END IF;

      -- 次処理：「得意先抽出」の場合
      IF vNextStep = vPRCSID_D1502020 THEN
        D1502020(iIraidd, iIraiJikan); -- 得意先抽出処理
        vNextStep := vPRCSID_D1502100; -- 次処理：「請求締解除処理」設定
      END IF;

      -- 次処理：「請求締解除処理」の場合
      IF vNextStep = vPRCSID_D1502100 THEN
        D1502100(iIraidd, iIraiJikan); -- 請求締解除処理
        vNextStep := vPRCSID_D1502040; -- 次処理：「請求締処理」設定
      END IF;

      -- 次処理：「請求締処理」の場合
      IF vNextStep = vPRCSID_D1502040 THEN
        D1502040(iIraidd, iIraiJikan); -- 請求締処理
        vNextStep := vPRCSID_D1502060; -- 次処理：「マスタ反映」設定
      END IF;

      -- 次処理：「マスタ反映」の場合
      IF vNextStep = vPRCSID_D1502060 THEN
        D1502060();                    -- マスタ反映処理
        vNextStep := vPRCSID_D1502999; -- 次処理：「処理終了判定」設定
      END IF;

      -- ACCIS2023-6671 start-ABI 請求締処理全体のトランザクションをコミット
      -- トランザクションコミット
      COMMIT;
      -- ACCIS2023-6671 end-ABI

    EXCEPTION
      WHEN OTHERS THEN
        -- メッセージ
        wMsg := SUBSTRB(SQLERRM || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || DBMS_UTILITY.FORMAT_ERROR_STACK, 1, 512);
        -- 請求締処理管理テーブル更新（処理状態：「エラー」）
        UpdateS07T004TProcessError(wMsg);
        -- ACCIS2023-6671 start-ABI 請求締処理全体のトランザクションをロールバック
        -- トランザクションロールバック
        ROLLBACK;
        -- ACCIS2023-6671 end-ABI
    END;
    -- 処理終了判定
    D1502999();
  END SeikyuShime;

  /*****************************************************************************/
  /* プロシージャ名  ：請求締解除処理                                          */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iUserApkihoncd        担当者ＡＰ基本コード              */
  /*                 ：iUserShopcd           担当者ショップコード              */
  /*                 ：iTnshacd              担当者コード                      */
  /*                 ：iIpadrse              ＩＰアドレス                      */
  /*                 ：iIraidd               依頼日                            */
  /*                 ：iIraijikan            依頼時間                          */
  /*                 ：iSkyshimedd           請求締日                          */
  /*****************************************************************************/
  PROCEDURE SeikyuKaijo(iApkihoncd     IN VARCHAR2
                       ,iShopcd        IN VARCHAR2
                       ,iUserApkihoncd IN VARCHAR2
                       ,iUserShopcd    IN VARCHAR2
                       ,iTnshacd       IN VARCHAR2
                       ,iIpadrse       IN VARCHAR2
                       ,iIraidd        IN VARCHAR2
                       ,iIraijikan     IN VARCHAR2
                       ,iSkyshimedd    IN VARCHAR2) IS
    ---------------------------
    -- カーソル定義
    ---------------------------
    -- 請求締ワークカーソル
    CURSOR curS07W003T IS
      SELECT
          SKYSAKICD
      FROM
          S07W003T
      WHERE
              APKIHONCD = iApkihoncd
          AND SHOPCD    = iShopcd
          AND IRAIDD    = iIraidd
          AND IRAIJIKAN = iIraijikan
      ;

    ---------------------------
    -- レコード定義
    ---------------------------
    recS07W003T curS07W003T%ROWTYPE; -- 請求締ワークカーソルレコード
  BEGIN
    -- 請求残高テーブル項目値設定
    SetS07T001TRecord(iApkihoncd, iShopcd, iTnshacd, iIpadrse, iUserApkihoncd, iUserShopcd, iSkyshimedd);

    -- 処理開始判定（2:請求締解除処理）
    D1502000(iIraidd, iIraiJikan, '2');

    -- 請求締用対象得意先ワークデータ削除
    DELETE FROM S07W006T WHERE APKIHONCD = iApkihoncd AND SHOPCD = iShopcd;

    -- 請求締ワークカーソルループ
    FOR recS07W003T IN curS07W003T LOOP
      -- 請求先コード設定
      vRecordS07T001T.SKYSAKICD := recS07W003T.SKYSAKICD;
      -- 請求締解除処理
      SeikyuFixCancelProcess();
    END LOOP;

    -- マスタ反映処理
    MasterUpdateProcess(0);

    -- 請求締処理管理テーブル更新（処理状態：「終了／待機中」）
    UpdateS07T004TProcessEnd();

    -- ACCIS2023-6671 start-ABI 請求締解除処理全体のトランザクションをコミット
    -- トランザクションコミット
    COMMIT;
    -- ACCIS2023-6671 end-ABI

  EXCEPTION
    WHEN OTHERS THEN
      -- 請求締処理管理テーブル更新（処理状態：「エラー」）
      UpdateS07T004TProcessError(SUBSTRB(SQLERRM || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || DBMS_UTILITY.FORMAT_ERROR_STACK, 1, 512));
      -- ACCIS2023-6671 start-ABI 請求締解除処理全体のトランザクションをロールバック
      -- トランザクションロールバック
      ROLLBACK;
      -- ACCIS2023-6671 end-ABI
      RAISE;
  END SeikyuKaijo;

  /*****************************************************************************/
  /* プロシージャ名  ：請求締処理                                              */
  /* 引数            ：iApkihoncd            ＡＰ基本コード                    */
  /*                 ：iShopcd               ショップコード                    */
  /*                 ：iUserApkihoncd        担当者ＡＰ基本コード              */
  /*                 ：iUserShopcd           担当者ショップコード              */
  /*                 ：iTnshacd              担当者コード                      */
  /*                 ：iIpadrse              ＩＰアドレス                      */
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
  PROCEDURE SeikyuShimeTantai(iApkihoncd     IN VARCHAR2
                             ,iShopcd        IN VARCHAR2
                             ,iUserApkihoncd IN VARCHAR2
                             ,iUserShopcd    IN VARCHAR2
                             ,iTnshacd       IN VARCHAR2
                             ,iIpadrse       IN VARCHAR2
                             ,iIraidd        IN VARCHAR2
                             ,iIraiJikan     IN VARCHAR2
                             ,iSkyshimedd    IN VARCHAR2
                             ,iSyoriMode     IN VARCHAR2
                             ,iSkysakicd     IN VARCHAR2
                             ,iTokuisakicd   IN VARCHAR2
                             ,iUridnpyno     IN VARCHAR2
                             ,iKaishuhohokbn IN VARCHAR2
                             ,iSingldnpyflg  IN VARCHAR2) IS
    ---------------------------
    -- 変数定義
    ---------------------------
    wInvoicShikoYmd           S02M046T.INVOICSHIKOYMD%TYPE;           -- インボイス施行日
    wTaxkeisankbnurikirikaedd S02M046T.TAXKEISANKBNURIKIRIKAEDD%TYPE; -- 税計算区分売上切替日
    wShintaxkeisankbnuri      S02M046T.SHINTAXKEISANKBNURI%TYPE;      -- 新税計算区分売上
    wGenztaxkeisankbnuri      S02M046T.GENZTAXKEISANKBNURI%TYPE;      -- 現税計算区分売上
    wShrsaito                 S02M017T.SHRSAITO%TYPE;                 -- 支払サイト
    wShrdd                    S02M017T.SHRDD%TYPE;                    -- 支払日
    wKsikkncd                 S02M017T.KSIKKNCD%TYPE;                 -- 決済機関コード
    wSkysakikanrikbn          S02M017T.SKYSAKIKANRIKBN%TYPE;          -- 請求先管理区分
    wRenban                   S07T001T.RENBAN%TYPE;                   -- 連番
    wSkyshonoedano            S07T001T.SKYSHONOEDANO%TYPE;            -- 請求書番号枝番
    wTaxkeisankbn             S07T001T.TAXKEISANKBN%TYPE;             -- 税計算区分
    wNyknflg                  VARCHAR2(1) := '1';                     -- 入金を請求締するかどうかを示すフラグ
    wTairyukbn                S19T001T.TAIRYUKBN%TYPE;                -- 滞留区分
    wTairyuhasseikeikadm      S19T001T.TAIRYUHASSEIKEIKADM%TYPE;      -- 滞留発生経過月数
    wTairyuhasseikaisu        S19T001T.TAIRYUHASSEIKAISU%TYPE;        -- 滞留発生回数
  BEGIN
    -- 請求残高テーブル項目値設定
    SetS07T001TRecord(iApkihoncd, iShopcd, iTnshacd, iIpadrse, iUserApkihoncd, iUserShopcd, iSkyshimedd);

    -- 請求処理実施区分設定（代引き時請求処理）
    vRecordS07T001T.SKYPRCSJISSIKBN := 2;

    -- インボイス施行日、税計算区分売上切替日、新税計算区分売上、現税計算区分売上取得（ショップマスタ）
    SELECT
        INVOICSHIKOYMD, TAXKEISANKBNURIKIRIKAEDD, SHINTAXKEISANKBNURI, GENZTAXKEISANKBNURI
    INTO
        wInvoicShikoYmd, wTaxkeisankbnurikirikaedd, wShintaxkeisankbnuri, wGenztaxkeisankbnuri
    FROM
        S02M046T
    WHERE
          APKIHONCD = iApkihoncd
      AND SHOPCD    = iShopcd
    ;

    -- インボイス施行フラグ設定
    IF wInvoicShikoYmd > vRecordS07T001T.SHIMEPRCSJISSIDD THEN
      vInvoiceExecutionFlg := '0'; -- インボイス施行前
    ELSE
      vInvoiceExecutionFlg := '1'; -- インボイス施行後
    END IF;

    -- 税計算区分設定
    IF wTaxkeisankbnurikirikaedd > vRecordS07T001T.KONKAISHIMEDD THEN
      wTaxkeisankbn := wGenztaxkeisankbnuri; -- 現税計算区分売上
    ELSE
      wTaxkeisankbn := wShintaxkeisankbnuri; -- 新税計算区分売上
    END IF;

    -- 請求残高テーブル（請求先コード、得意先コード、回収方法区分）項目値設定
    vRecordS07T001T.SKYSAKICD     := iSkysakicd;      -- 請求先コード
    vRecordS07T001T.TOKUISAKICD   := iTokuisakicd;    -- 得意先コード
    vRecordS07T001T.KAISHUHOHOKBN := iKaishuhohokbn;  -- 回収方法区分

    -- 請求締用対象得意先ワークデータ削除
    DELETE FROM S07W006T WHERE APKIHONCD = iApkihoncd AND SHOPCD = iShopcd;

    -- 請求締用対象得意先ワークデータ生成
    INSERT INTO S07W006T(
          APKIHONCD
        , SHOPCD
        , SKYSAKICD
        , TOKUISAKICD
        , SHIMESUMIFLG
        , ZNKSHIMEDD
        , KURIKOSHIKIN
        , SHOHITAXKIN
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
          vRecordS07T001T.APKIHONCD
        , vRecordS07T001T.SHOPCD
        , vRecordS07T001T.SKYSAKICD
        , vRecordS07T001T.TOKUISAKICD
        , '0' -- （未締め）
        , '00000000'
        , 0
        , 0
        , vRecordS07T001T.TOSHAAPKIHONCD
        , vRecordS07T001T.TOSHASHOPCD
        , vRecordS07T001T.TOSHACD
        , vRecordS07T001T.INPTSYSTMYMD
        , vRecordS07T001T.INPTSYSTMJIKAN
        , vRecordS07T001T.INPTUNYOYMD
        , vRecordS07T001T.INPTIPADRSE
        , vRecordS07T001T.INPTPGMID
        , vRecordS07T001T.KOSHAAPKIHONCD
        , vRecordS07T001T.KOSHASHOPCD
        , vRecordS07T001T.KOSHACD
        , vRecordS07T001T.KSHNSYSTMYMD
        , vRecordS07T001T.KSHNSYSTMJIKAN
        , vRecordS07T001T.KSHNUNYOYMD
        , vRecordS07T001T.KSHNIPADRSE
        , vRecordS07T001T.KSHNPGMID
    );

    -- 前回締日、前回繰越金額設定
    BEGIN
      SELECT
          KONKAISHIMEDD, KURIKOSHIKIN
      INTO
          vRecordS07T001T.ZNKSHIMEDD, vRecordS07T001T.ZNKKURIKOSHIKIN
      FROM
          (
            SELECT
                  KONKAISHIMEDD
                , KURIKOSHIKIN
                , ROW_NUMBER() OVER (PARTITION BY TOKUISAKICD ORDER BY KONKAISHIMEDD DESC, RENBAN DESC) AS ROWNO
            FROM
                S07T001T
            WHERE
                    APKIHONCD     = vRecordS07T001T.APKIHONCD
                AND SHOPCD        = vRecordS07T001T.SHOPCD
                AND SKYSAKICD     = vRecordS07T001T.SKYSAKICD
                AND TOKUISAKICD   = vRecordS07T001T.TOKUISAKICD
                AND YUKOKBN       = '1'
          )
      WHERE
          ROWNO = 1
      ;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        vRecordS07T001T.ZNKSHIMEDD      := NULL;
        vRecordS07T001T.ZNKKURIKOSHIKIN := 0;
    END;

    -- 前回締日≠NULL且つ今回締日＜前回締日の場合
    IF vRecordS07T001T.ZNKSHIMEDD IS NOT NULL AND vRecordS07T001T.KONKAISHIMEDD < vRecordS07T001T.ZNKSHIMEDD THEN
      vRecordS07T001T.SHIMEDYDM     := SUBSTRB(vRecordS07T001T.ZNKSHIMEDD, 0, 6);
      vRecordS07T001T.SHIMEDD       := SUBSTRB(vRecordS07T001T.ZNKSHIMEDD, 7, 2);
      vRecordS07T001T.KONKAISHIMEDD := vRecordS07T001T.ZNKSHIMEDD;
    END IF;

    -- カテゴリ別集計用カテゴリコード設定
    SetCategoryCd();

    -- ACCIS2023-6784 代引の月締請求残高を締解除した際の滞留更新でエラーになることがある問題の対応
    -- 請求先マスタから滞留情報の取得および前回滞留情報テーブルへの登録処理を追加

    -- 支払サイト、支払日、決済機関コード、請求先管理区分および滞留情報（滞留区分、滞留発生経過月数、滞留発生回数）取得（請求先マスタ）
    SELECT
        SHRSAITO, SHRDD, KSIKKNCD, SKYSAKIKANRIKBN, TAIRYUKBN ,TAIRYUHASSEIKEIKADM ,TAIRYUHASSEIKAISU
    INTO
        wShrsaito, wShrdd, wKsikkncd, wSkysakikanrikbn, wTairyukbn, wTairyuhasseikeikadm, wTairyuhasseikaisu
    FROM
        S02M017T
    WHERE
            APKIHONCD  = vRecordS07T001T.APKIHONCD
        AND SHOPCD     = vRecordS07T001T.SHOPCD
        AND SKYSAKICD  = vRecordS07T001T.SKYSAKICD
    ;

    -- 前回滞留情報テーブルに対して主キー（AP基本コード、ショップコード、請求先コード、締年月、請求締日）を条件指定し、
    -- 登録済レコードがあれば削除する
    DELETE FROM S19T001T
    WHERE
      APKIHONCD = vRecordS07T001T.APKIHONCD
      AND SHOPCD = vRecordS07T001T.SHOPCD
      AND SKYSAKICD = vRecordS07T001T.SKYSAKICD
      AND SHIMEDYDM = vRecordS07T001T.SHIMEDYDM
      AND SKYSHIMEDD = vRecordS07T001T.SHIMEDD
    ;
    -- 前回滞留情報テーブルに新規レコードを登録
    INSERT INTO S19T001T (
        APKIHONCD                                   -- ＡＰ基本コード
      , SHOPCD                                      -- ショップコード
      , SKYSAKICD                                   -- 請求先コード
      , SHIMEDYDM                                   -- 締年月
      , SKYSHIMEDD                                  -- 請求締日
      , TAIRYUKBN                                   -- 滞留区分
      , TAIRYUHASSEIKEIKADM                         -- 滞留発生経過月
      , TAIRYUHASSEIKAISU                           -- 滞留発生回数
      , YUKOKBN                                     -- 有効区分
      , TOSHAAPKIHONCD                              -- 登録者ＡＰ基本コード
      , TOSHASHOPCD                                 -- 登録者ショップコード
      , TOSHACD                                     -- 登録者コード
      , INPTSYSTMYMD                                -- 登録システム日付
      , INPTSYSTMJIKAN                              -- 登録システム時間
      , INPTUNYOYMD                                 -- 登録運用日付
      , INPTIPADRSE                                 -- 登録ＩＰアドレス
      , INPTPGMID                                   -- 登録プログラムＩＤ
      , KOSHAAPKIHONCD                              -- 更新者ＡＰ基本コード
      , KOSHASHOPCD                                 -- 更新者ショップコード
      , KOSHACD                                     -- 更新者コード
      , KSHNSYSTMYMD                                -- 更新システム日付
      , KSHNSYSTMJIKAN                              -- 更新システム時間
      , KSHNUNYOYMD                                 -- 更新運用日付
      , KSHNIPADRSE                                 -- 更新ＩＰアドレス
      , KSHNPGMID                                   -- 更新プログラムＩＤ
    ) VALUES (
        vRecordS07T001T.APKIHONCD                   -- ＡＰ基本コード
      , vRecordS07T001T.SHOPCD                      -- ショップコード
      , vRecordS07T001T.SKYSAKICD                   -- 請求先コード
      , vRecordS07T001T.SHIMEDYDM                   -- 締年月
      , vRecordS07T001T.SHIMEDD                     -- 請求締日
      , wTairyukbn                                  -- 滞留区分
      , wTairyuhasseikeikadm                        -- 滞留発生経過月
      , wTairyuhasseikaisu                          -- 滞留発生回数
      , '1'                                         -- 有効区分
      , vRecordS07T001T.TOSHAAPKIHONCD              -- 登録者ＡＰ基本コード
      , vRecordS07T001T.TOSHASHOPCD                 -- 登録者ショップコード
      , vRecordS07T001T.TOSHACD                     -- 登録者コード
      , vRecordS07T001T.INPTSYSTMYMD                -- 登録システム日付
      , vRecordS07T001T.INPTSYSTMJIKAN              -- 登録システム時刻
      , vRecordS07T001T.INPTUNYOYMD                 -- 登録運用日付
      , vRecordS07T001T.INPTIPADRSE                 -- 登録ＩＰアドレス
      , vRecordS07T001T.INPTPGMID                   -- 登録プログラムＩＤ
      , vRecordS07T001T.KOSHAAPKIHONCD              -- 更新者ＡＰ基本コード
      , vRecordS07T001T.KOSHASHOPCD                 -- 更新者ショップコード
      , vRecordS07T001T.KOSHACD                     -- 更新者コード
      , vRecordS07T001T.KSHNSYSTMYMD                -- 更新システム日付
      , vRecordS07T001T.KSHNSYSTMJIKAN              -- 更新システム時刻
      , vRecordS07T001T.KSHNUNYOYMD                 -- 更新運用日付
      , vRecordS07T001T.KSHNIPADRSE                 -- 更新ＩＰアドレス
      , vRecordS07T001T.KSHNPGMID                   -- 更新プログラムＩＤ
    );
    -- 支払予定日設定
    SetPaymentPlanDate(wShrsaito, wShrdd);

    -- 請求残高テーブル（決済機関コード、税計算区分）項目値設定
    vRecordS07T001T.KSIKKNCD      := wKsikkncd;
    vRecordS07T001T.TAXKEISANKBN  := CASE WHEN wSkysakikanrikbn = '2' THEN '2' ELSE wTaxkeisankbn END;

    -- 新連番取得
    wRenban := GetNewRenban();

-- ACCIS2023-6557 start ABI : 単体伝票締めフラグがONの場合、入金については請求締しない
    IF (iSingldnpyflg = '1') THEN
      wNyknflg := '0';
    END IF;
    -- ACISS2023-6662 代引請求先の月締請求残高締解除後の請求締対応：請求書番号枝番を決定する処理を追加
    wSkyshonoedano := GetSkyshoBranchNumber(vRecordS07T001T.APKIHONCD, vRecordS07T001T.SHOPCD, iUridnpyno);
    -- 請求締処理
    SeikyuFixProcess(iUridnpyno, iUridnpyno, wSkyshonoedano, wRenban, '1', wNyknflg);
-- ACCIS2023-6557 end ABI

    -- マスタ反映処理
    MasterUpdateProcess(1);
  END SeikyuShimeTantai;
END D1502B01;
/
exit;
