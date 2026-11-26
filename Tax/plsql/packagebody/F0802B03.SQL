CREATE OR REPLACE PACKAGE BODY F0802B03
/*******************************************************************************
 * システム名       ：ＡＰ総合管理システム
 * 機能名           ：モバイルシステム実績情報取込（配達実績）
 *
 ******************************************************************************T
   Date       Author         Changes Key                                 Version
   -----------------------------------------------------------------------------
   2022-06-06 ABI)Yokota        Create                                     1.0.0
 ******************************************************************************/
 AS
  /****************************************************************************/
  /* 共通変数宣言                                                               */
  /****************************************************************************/
  /* データ作成元区分 */
  DATSKSMOTOKBN_NEW CONSTANT VARCHAR2(2) := '21'; -- データ作成元区分：モバイル実績取込
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
                     , ACPTKIN          S04T001T.ACPTKIN%TYPE          -- 受注金額
                     , ACPTSHOHITAXKIN  S04T001T.ACPTSHOHITAXKIN%TYPE  -- 受注消費税金額
                     , NOHINKIN         S04T001T.NOHINKIN%TYPE         -- 納品金額
                     , NOHINSHOHITAXKIN S04T001T.NOHINSHOHITAXKIN%TYPE -- 納品消費税金額
                     -- 得意先実績テーブル更新用
                     , RGRASURYO S04T001T.DLVRYJISSEKIRGRAYUSYOHNS%TYPE -- 配達レギュラー本数
                     , SRMSURYO  S04T001T.DLVRYJISSEKISRMYUSYOHNS%TYPE  -- 配達スリム本数
  );
  -- 合計用データ(キャンセル用)：定義
  TYPE sum_rec_cancel_type IS RECORD (
                     -- 得意先実績テーブル更新用
                     RGRASURYO S04T001T.DLVRYJISSEKIRGRAYUSYOHNS%TYPE -- 配達レギュラー本数
                     , SRMSURYO  S04T001T.DLVRYJISSEKISRMYUSYOHNS%TYPE  -- 配達スリム本数
                     , KZBTLN1N2RITTORUHNS S04T001T.KZBTLN1N2RITTORUHNS%TYPE -- 回収実績ボトル１２Ｌ本数
                     , KZBTLN8RITTORUHNS  S04T001T.KZBTLN8RITTORUHNS%TYPE  -- 回収実績ボトル８Ｌ本数
  );
-- インボイス対応追加 start ABI
  cmnAcptippannukikin    NUMBER(11,0) := 0;     -- 受注一般税抜金額
  cmnAcptippantaxkin     NUMBER(11,0) := 0;     -- 受注一般消費税金額
  cmnAcptkeigennukikin   NUMBER(11,0) := 0;     -- 受注軽減税抜金額
  cmnAcptkeigentaxkin    NUMBER(11,0) := 0;     -- 受注軽減消費税金額
  cmnAcpthikazeiuriikin  NUMBER(11,0) := 0;     -- 受注非課税金額
  cmnAcptfukazeiuriikin  NUMBER(11,0) := 0;     -- 受注不課税売上金額
  cmnNohinippannukikin   NUMBER(11,0) := 0;     -- 納品一般税抜金額
  cmnNohinippantaxkin    NUMBER(11,0) := 0;     -- 納品一般消費税金額
  cmnNohinkeigennukikin  NUMBER(11,0) := 0;     -- 納品軽減税抜金額
  cmnNohinkeigentaxkin   NUMBER(11,0) := 0;     -- 納品軽減消費税金額
  cmnNohinhikazeiuriikin NUMBER(11,0) := 0;     -- 納品非課税金額
  cmnNohinfukazeiuriikin NUMBER(11,0) := 0;     -- 納品不課税売上金額
  cmnSingldnpyflg        VARCHAR2(1)  := NULL;  -- 単体伝票締めフラグ
  cmnShimeippantaxkin    NUMBER(11,0) := NULL;     -- 締め後一般消費税金額
  cmnShimekeigentaxkin   NUMBER(11,0) := NULL;     -- 締め後軽減消費税金額
  cmnInvoiceshikoymdflg   VARCHAR2(1)  := '1';   -- インボイス施行日フラグ（0:インボイス施行前、1:インボイス施行後）
-- インボイス対応追加 end ABI
-- 本線対応追加 start ABI
  cmnCngdlvryddflg       BOOLEAN      := FALSE; -- 配達日変更
  cmnCngmeisuryoflg      BOOLEAN      := FALSE; -- 明細数量金額変更
  cmnAllzeromeisuryoflg  BOOLEAN      := TRUE;  -- 明細数量オールゼロフラグ
  cmnAkauridnpyno        VARCHAR2(11) := NULL;  -- 売上伝票番号（赤）
  cmnKurouridnpyno       VARCHAR2(11) := NULL;  -- 売上伝票番号（黒）
-- 本線対応追加 end ABI

  -- ACCIS2023-7212 受注登録時点の受注配達明細（展開行のみ）を退避させるためのデータ型を追加
  TYPE t_s04_map IS TABLE OF S04T002T%ROWTYPE INDEX BY VARCHAR2(26);

  -- ポイント制度対応追加 start ABI
  gParamApkihoncd         VARCHAR2(3);               -- 起動パラメータのAP基本コード
  gParamShopcd            VARCHAR2(3);               -- 起動パラメータのショップコード
  cmnRegistKanrino        VARCHAR2(15);              -- 登録した予定発生ポイントの管理番号
  TYPE                    S20W003TList IS TABLE OF S20W003T%ROWTYPE INDEX BY PLS_INTEGER;
  cmnAkadnPntT            S20T001T%ROWTYPE;          -- 赤伝用の元予定発生ポイントレコード保持用
  TYPE                    S20T002TList IS TABLE OF S20T002T%ROWTYPE INDEX BY PLS_INTEGER;
  cmnAkadnPntMeiTList     S20T002TList;              -- 赤伝用の元予定発生ポイント明細レコード保持用
  TYPE                    S20T004TList IS TABLE OF S20T004T%ROWTYPE INDEX BY PLS_INTEGER;
  cmnAkadnPntMeiksTList   S20T004TList;              -- 赤伝用の元予定発生ポイント明細計算詳細レコード保持用
  cmnAkadenSaibanno       VARCHAR2(15);              -- 赤伝用に採番した予定発行ポイントテーブル管理番号
  c_PRCSID       CONSTANT VARCHAR2(8) := 'F0802B03'; -- 処理ID
  -- ポイント制度対応追加 end ABI

  -- ACCIS2023-7212 受注配達明細の展開行を退避するリストのキーを得るための関数を追加
  /****************************************************************************/
  /* ファンクション名 ：受注配達明細テーブル主キー文字列取得                         */
  /* 引数             ：iDetail              受注配達明細テーブルのデータ         */
  /* return           ：                     主キーを1つの文字列として連結したもの */
  /****************************************************************************/
  FUNCTION get_s04_key(iDetail IN S04T002T%ROWTYPE) RETURN VARCHAR2 IS
  BEGIN
    RETURN LPAD(iDetail.APKIHONCD, 3, '0') ||
         LPAD(iDetail.SHOPCD, 3, '0') ||
         LPAD(iDetail.ACPTDNPYNO, 11, '0') ||
         LPAD(TO_CHAR(iDetail.EDANO), 2, '0') ||
         LPAD(TO_CHAR(iDetail.ACPTDNPYMEISAINO), 5, '0') ||
         LPAD(TO_CHAR(iDetail.ACPTDNPYMEISAIEDANO), 2, '0');
  END get_s04_key;

  -- ACCIS2023-7212 セット商品（入力行）の単価を取得する関数を追加
  /*****************************************************************************/
  /* プロシージャ名：セット商品単価取得処理                                    */
  /* 引数             ：header_rec     配達実績データヘッダ                    */
  /*                  ：meisai_rec     セット商品親明細行                      */
  /* return           ：returnPrice    単価                                    */
  /*****************************************************************************/
  FUNCTION getUntOfSetItem(
      header_rec    IN S05W001T%rowtype  -- 配達実績データヘッダ
    , meisai_rec    IN S04T002T%rowtype  -- セット商品親明細行（入力行）
  ) RETURN NUMBER IS
    -- 変数定義
    returnPrice NUMBER := 0; -- 単価（戻り値）
  BEGIN
    -- 商品マスタビューから標準単価を得意先別セット商品単価マスタから特別単価を取得し
    -- 特別単価が取得できている場合は特別単価をセット商品の単価として返却する
    SELECT
        CASE
        WHEN SUNT.DLVRYPRICE IS NOT NULL
          THEN getUnt(
          header_rec.DLVRYKBN
          , SHMV.HYOJUNPRICEKIRIKAEDD
          , header_rec.DLVRYJISSEKIDD
          , SUNT.DLVRYPRICE
          , SUNT.DLVRYPRICE
          , SUNT.MOCHIKAERIPRICE
          , SUNT.MOCHIKAERIPRICE
        )
        ELSE getUnt(
          header_rec.DLVRYKBN
          , SHMV.HYOJUNPRICEKIRIKAEDD
          , header_rec.DLVRYJISSEKIDD
          , SHMV.GENZHYOJUNPRICE
          , SHMV.SHINHYOJUNPRICE
          , SHMV.GENZMOCHIKAERIPRICE
          , SHMV.SHINMOCHIKAERIPRICE
        )
        END AS STSHNUNT
    INTO returnPrice
    FROM
      S02V001T SHMV
      LEFT JOIN (
        SELECT
            TSSM.APKIHONCD
          , TSSM.SHOPCD
          , TSSM.STSHNCD
          , TSSM.DLVRYPRICE
          , TSSM.MOCHIKAERIPRICE
        FROM
          S02M066T TSSM
          INNER JOIN S02M013T TKSM ON
            TKSM.APKIHONCD = TSSM.APKIHONCD
            AND TKSM.SHOPCD = TSSM.SHOPCD
            AND TKSM.TOKUISAKICD = TSSM.TOKUISAKICD
            AND TKSM.RKNKINDTOKUBETSUUNTFLG = '1'
            AND TKSM.YUKOKBN = '1'
        WHERE
          TSSM.TOKUISAKICD = header_rec.TOKUISAKICD
          AND header_rec.DLVRYJISSEKIDD BETWEEN TSSM.TEKIYOSTRTDD AND TSSM.TEKIYOSHRYDD
          AND TSSM.YUKOKBN = '1'
      ) SUNT ON
        SHMV.APKIHONCD = SUNT.APKIHONCD
        AND SHMV.SHOPCD = SUNT.SHOPCD
        AND SHMV.SHNCD = SUNT.STSHNCD
    WHERE
      SHMV.APKIHONCD = header_rec.APKIHONCD
      AND SHMV.SHOPCD = header_rec.SHOPCD
      AND SHMV.SHNCD = meisai_rec.SHNCD
      AND SHMV.KIHONSHNYUKOKBN = '1'
      AND SHMV.SHOPYUKOKBN = '1'
    ;
    RETURN(returnPrice);
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      RAISE_APPLICATION_ERROR(-20024, 'セット商品の単価取得に失敗しました（該当なし）。商品コード: ' || meisai_rec.SHNCD);
    WHEN TOO_MANY_ROWS THEN
      RAISE_APPLICATION_ERROR(-20025, 'セット商品の単価が複数件存在します。商品コード: ' || meisai_rec.SHNCD);
  END getUntOfSetItem;

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
  /* ファンクション名 ：単価判定                                               */
  /* 引数             ：iDlvryKbn             配達区分                         */
  /*                  ：iKirikaeDd            単価切替日                       */
  /*                  ：iKijunDd              基準日                           */
  /*                  ：iGenzDlvryPrice       現配達価格                       */
  /*                  ：iShinDlvryPrice       新配達価格                       */
  /*                  ：iGenzMochikaeriPrice  現持ち帰り価格                   */
  /*                  ：iShinMochikaeriPrice  新持ち帰り価格                   */
  /* return           ：returnPrice           単価                             */
  /*****************************************************************************/
  FUNCTION getUnt(
    iDlvryKbn      IN VARCHAR2
  , iKirikaeDd     IN VARCHAR2
  , iKijunDd       IN VARCHAR2
  , iGenzDlvryPrice     IN NUMBER
  , iShinDlvryPrice     IN NUMBER
  , iGenzMochikaeriPrice     IN NUMBER
  , iShinMochikaeriPrice     IN NUMBER
  ) RETURN NUMBER IS returnPrice NUMBER;
  BEGIN
    IF iDlvryKbn = '3' THEN
      IF iKirikaeDd IS NULL OR iKijunDd >= iKirikaeDd THEN
        returnPrice := iShinMochikaeriPrice;
      ELSE
        returnPrice := iGenzMochikaeriPrice;
      END IF;
    ELSE
      IF iKirikaeDd IS NULL OR iKijunDd >= iKirikaeDd THEN
        returnPrice := iShinDlvryPrice;
      ELSE
        returnPrice := iGenzDlvryPrice;
      END IF;
    END IF;
    RETURN(returnPrice);
  END getUnt;

  /*****************************************************************************/
  /* ファンクション名 ：展開商品受注単価取得                                   */
  /* 引数             ：iApKihonCd            配達区分                         */
  /*                  ：iShopcd               単価切替日                       */
  /*                  ：iStShnCd              セット商品コード                 */
  /*                  ：iTokuisakiCd          得意先コード                     */
  /*                  ：iRenban               連番                             */
  /*                  ：iShnCd                構成商品コード                   */
  /*                  ：iKijunDd              基準日                           */
  /*                  ：iDlvryKbn             配達区分                         */
  /* return           ：returnPrice           単価                             */
  /*****************************************************************************/
  FUNCTION getTenkaiUnt(
    iApKihonCd      IN VARCHAR2
  , iShopcd         IN VARCHAR2
  , iStShnCd        IN VARCHAR2
  , iTokuisakiCd    IN VARCHAR2
  , iRenban         IN NUMBER
  , iShnCd          IN VARCHAR2
  , iKijunDd        IN VARCHAR2
  , iDlvryKbn       IN VARCHAR2
  ) RETURN NUMBER IS returnPrice NUMBER;
  wApkihonCd VARCHAR2(3) := iApkihonCd; -- 展開マスタ検索用AP基本コード
  BEGIN
    returnPrice := null;
    -- 商品コードの先頭1文字が「0（本部商品）」の場合、展開マスタ検索用AP基本コードを「001」に変更する
    IF (INSTR(iShnCd, '0', 1) = 1) THEN
      wApkihonCd := '001';
    END IF;
    -- 展開マスタ
    BEGIN
      SELECT ( CASE iDlvryKbn WHEN '3' -- 配達区分：持ち帰り
                    THEN TNKM.MOCHIKAERIPRICE   -- 持ち帰り価格
                    ELSE TNKM.DLVRYPRICE
               END )
        INTO returnPrice
        FROM S02M008T TNKM
       WHERE TNKM.APKIHONCD  = wApkihonCd -- ＡＰ基本コード
         AND TNKM.STSHNCD    = iStShnCd   -- セット商品コード
         AND TNKM.RENBAN     = iRenban
         AND TNKM.KOSEISHNCD = iShnCd     -- 構成商品コード
         AND TNKM.YUKOKBN    = '1' -- 有効区分
      ;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        NULL;
    END;
    -- 得意先別セット商品単価展開マスタの単価
    -- 特別単価あり
    BEGIN
      SELECT CASE iDlvryKbn WHEN '3'
                  THEN TSTM.MOCHIKAERIPRICE -- 持ち帰り価格
                  ELSE TSTM.DLVRYPRICE      -- 配達価格
              END
         INTO returnPrice
        FROM S02M066T TSSM
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
         AND TSTM.SHNCD                  = iShnCd
         AND TSTM.RENBAN                 = iRenban
         AND TSTM.YUKOKBN                = '1'
       )
       WHERE TSSM.APKIHONCD   = iApkihonCd                                   -- ＡＰ基本コード
         AND TSSM.SHOPCD      = iShopCd                                      -- ショップコード
         AND TSSM.STSHNCD     = iStShnCd                                     -- セット商品コード
         AND TSSM.TOKUISAKICD = iTokuisakiCd                                 -- 得意先コード
         AND iKijunDd BETWEEN TSSM.TEKIYOSTRTDD AND TSSM.TEKIYOSHRYDD        -- 基準日：適用期間内
         AND TSSM.YUKOKBN     = '1'
       ;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        NULL;
      WHEN TOO_MANY_ROWS THEN
        RAISE_APPLICATION_ERROR(-20015, '得意先別セット単価展開が複数見つかりました。');
    END;
    RETURN(returnPrice);
  END getTenkaiUnt;

  /*****************************************************************************/
  /* プロシージャ名：伝票ステータスチェック                                         */
  /* 引数             ：work_rec         配達実績データ                          */
  /* 引数（ＯＵＴ）     ：acpt_old         更新前受注配達テーブル                   */
  /*****************************************************************************/
  PROCEDURE checkDnpysts( work_rec      IN S05W001T%rowtype
                        , acpt_old     OUT S04T001T%rowtype
  ) IS
  /* CURSOR:既存伝票 */
  CURSOR SELECT_DNPY IS
    SELECT JHTT.*
      FROM S04T001T JHTT
     WHERE JHTT.APKIHONCD  = work_rec.APKIHONCD
       AND JHTT.SHOPCD     = work_rec.SHOPCD
       AND JHTT.ACPTDNPYNO = work_rec.ACPTDNPYNO
       AND JHTT.EDANO   = work_rec.EDANO
       AND JHTT.YUKOKBN = '1'
    FOR UPDATE
    ;
  BEGIN
    FOR acpt_check IN SELECT_DNPY LOOP
        IF acpt_check.DNPYSTS = '11' THEN
           acpt_old := acpt_check;
        ELSE
           RAISE_APPLICATION_ERROR(-20006, '配達中以外の伝票：「' || work_rec.ACPTDNPYNO || '」は取込できません。');
        END IF;
    END LOOP;
  END checkDnpysts;

  /*****************************************************************************/
  /* プロシージャ名：消費税設定                                                   */
  /* 引数             ：meisai_rec           明細行                             */
  /*                  ：iSuryo               数量                              */
  /*                  ：iUnt                 単価                              */
  /*                  ：iTaxsetteikbn        税設定区分                         */
  /*                  ：iTaxkeisankbn        税計算区分                         */
  /*                  ：iKirikaebi           切替日                             */
  /*                  ：iDlvrykbn            配達区分                           */
  /*                  ：iDlvrykbn            税区分毎の合計金額設定判定区分     */
  /*                  ：iDlvrykbn            受注納品判定区分                   */
  /* 引数（ＯＵＴ）     ：oUntShohitax         単価消費税                         */
  /*                  ：oShohitax            消費税                             */
  /*****************************************************************************/
  PROCEDURE setTax( meisai_rec        IN S04T002T%rowtype -- 明細行
                  , iSuryo            IN NUMBER   -- 数量
                  , iUnt              IN NUMBER   -- 単価
                  , iApKihonCd        IN VARCHAR2 -- ＡＰ基本コード
                  , iShopCd           IN VARCHAR2 -- ショップコード
                  , iTaxkbn           IN VARCHAR2 -- 税区分
                  , iTaxsetteikbn     IN VARCHAR2 -- 税設定区分
                  , iTaxkeisankbn     IN VARCHAR2 -- 税計算区分
                  , iKirikaebi        IN VARCHAR2 -- 切替日
                  , iDlvrykbn         IN VARCHAR2 -- 配達区分
-- インボイス対応追加 start ABI
                  , iTaxkbnsumkinkbn  IN VARCHAR2 -- 税区分毎の合計金額設定判定区分（0:設定する、1:設定しない）
                  , iAcptnohinkbn     IN VARCHAR2 -- 受注納品判定区分（0:受注、1:納品）
-- インボイス対応追加 end ABI
                  , oUntshohitaxkin  OUT NUMBER -- 単価消費税
                  , oShohitaxkin     OUT NUMBER -- 消費税
  ) IS
  /* 変数宣言 */
  taxkbn                    VARCHAR2(1)  := NULL; -- 消費税計算用税区分
  kin                       NUMBER(11,0) := 0;    -- 消費税計算用金額
  wUrikinhasukeisankbn      VARCHAR2(1)  := NULL; -- 売上金額端数計算区分
  BEGIN
    -- 初期化
    oUntshohitaxkin := 0; -- 単価消費税
    oShohitaxkin    := 0; -- 消費税

    -- 消費税計算用税区分の取得
    IF meisai_rec.KEIJOKBN = '08' THEN -- 計上区分：経費
       taxkbn := '2';                  -- 非課税
    ELSE
       taxkbn := iTaxkbn; -- 商品税区分
    END IF;

    -- 消費税計算用金額
    -- ショップマスタ取得
    SELECT
      SHPM.URIKINHASUKEISANKBN  -- 売上金額端数計算区分
    INTO wUrikinhasukeisankbn
    FROM
      S02M046T SHPM
    WHERE
      SHPM.APKIHONCD = iApKihonCd
      AND SHPM.SHOPCD = iShopCd
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
                                                    iApKihonCd, --AP基本コード
                                                       iShopCd, --ショップコード
                                                        NULL, --仕入先コード
                                                        NULL, --数量
                                                        kin); -- 金額
-- インボイス対応追加 start ABI
      IF iTaxkbnsumkinkbn = '0' THEN
        -- 税区分毎の合計金額を加算
        -- パラメータ.税区分が"1"（課税）の場合、一般金額系に設定
        IF iTaxkbn = '1' THEN
          IF iAcptnohinkbn = '0' THEN -- 受注消費税算出の場合
            cmnAcptippannukikin  := cmnAcptippannukikin + kin;            -- 受注一般税抜金額
            cmnAcptippantaxkin   := cmnAcptippantaxkin + oShohitaxkin;    -- 受注一般消費税金額
          ELSIF iAcptnohinkbn = '1' THEN -- 納品消費税算出の場合
            cmnNohinippannukikin := cmnNohinippannukikin + kin;           -- 納品一般税抜金額
            cmnNohinippantaxkin  := cmnNohinippantaxkin + oShohitaxkin;   -- 納品一般消費税金額
          END IF;
        -- パラメータ.税区分が"2"（非課税）の場合、非課税金額系に設定
        ELSIF iTaxkbn = '2' THEN
          IF iAcptnohinkbn = '0' THEN -- 受注消費税算出の場合
            cmnAcpthikazeiuriikin  := cmnAcpthikazeiuriikin + kin;        -- 受注非課税金額
          ELSIF iAcptnohinkbn = '1' THEN -- 納品消費税算出の場合
            cmnNohinhikazeiuriikin := cmnNohinhikazeiuriikin + kin;       -- 納品非課税金額
          END IF;
        -- パラメータ.税区分が"3"（不課税）の場合、不課税金額系に設定
        ELSIF iTaxkbn = '3' THEN
          IF iAcptnohinkbn = '0' THEN -- 受注消費税算出の場合
            cmnAcptfukazeiuriikin  := cmnAcptfukazeiuriikin + kin;        -- 受注不課税売上金額
          ELSIF iAcptnohinkbn = '1' THEN -- 納品消費税算出の場合
            cmnNohinfukazeiuriikin := cmnNohinfukazeiuriikin + kin;       -- 納品不課税売上金額
          END IF;
        -- パラメータ.税区分が"4"（課税軽減）の場合、軽減金額系に設定
        ELSIF iTaxkbn = '4' THEN
          IF iAcptnohinkbn = '0' THEN -- 受注消費税算出の場合
            cmnAcptkeigennukikin  := cmnAcptkeigennukikin + kin;          -- 受注軽減税抜金額
            cmnAcptkeigentaxkin   := cmnAcptkeigentaxkin + oShohitaxkin;  -- 受注軽減消費税金額
          ELSIF iAcptnohinkbn = '1' THEN -- 納品消費税算出の場合
            cmnNohinkeigennukikin := cmnNohinkeigennukikin + kin;         -- 納品軽減税抜金額
            cmnNohinkeigentaxkin  := cmnNohinkeigentaxkin + oShohitaxkin; -- 納品軽減消費税金額
          END IF;
        END IF;
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
                                                     iApKihonCd, --AP基本コード
                                                        iShopCd, --ショップコード
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
                                                     iApKihonCd, --AP基本コード
                                                        iShopCd, --ショップコード
                                                           NULL, --仕入先コード
                                                           NULL, --数量
                                                           kin); -- 金額
       ELSE
          oShohitaxkin := oUntshohitaxkin * iSuryo;
       END IF;

-- インボイス対応追加 start ABI
      IF iTaxkbnsumkinkbn = '0' THEN
        -- 税区分毎の合計金額を加算
        -- パラメータ.税区分が"1"（課税）の場合、一般金額系に設定
        IF iTaxkbn = '1' THEN
          IF iAcptnohinkbn = '0' THEN -- 受注消費税算出の場合
            cmnAcptippannukikin  := cmnAcptippannukikin + kin;            -- 受注一般税抜金額
            cmnAcptippantaxkin   := cmnAcptippantaxkin + oShohitaxkin;    -- 受注一般消費税金額
          ELSIF iAcptnohinkbn = '1' THEN -- 納品消費税算出の場合
            cmnNohinippannukikin := cmnNohinippannukikin + kin;           -- 納品一般税抜金額
            cmnNohinippantaxkin  := cmnNohinippantaxkin + oShohitaxkin;   -- 納品一般消費税金額
          END IF;
        -- パラメータ.税区分が"2"（非課税）の場合、非課税金額系に設定
        ELSIF iTaxkbn = '2' THEN
          IF iAcptnohinkbn = '0' THEN -- 受注消費税算出の場合
            cmnAcpthikazeiuriikin  := cmnAcpthikazeiuriikin + kin;        -- 受注非課税金額
          ELSIF iAcptnohinkbn = '1' THEN -- 納品消費税算出の場合
            cmnNohinhikazeiuriikin := cmnNohinhikazeiuriikin + kin;       -- 納品非課税金額
          END IF;
        -- パラメータ.税区分が"3"（不課税）の場合、不課税金額系に設定
        ELSIF iTaxkbn = '3' THEN
          IF iAcptnohinkbn = '0' THEN -- 受注消費税算出の場合
            cmnAcptfukazeiuriikin  := cmnAcptfukazeiuriikin + kin;        -- 受注不課税売上金額
          ELSIF iAcptnohinkbn = '1' THEN -- 納品消費税算出の場合
            cmnNohinfukazeiuriikin := cmnNohinfukazeiuriikin + kin;       -- 納品不課税売上金額
          END IF;
        -- パラメータ.税区分が"4"（課税軽減）の場合、軽減金額系に設定
        ELSIF iTaxkbn = '4' THEN
          IF iAcptnohinkbn = '0' THEN -- 受注消費税算出の場合
            cmnAcptkeigennukikin  := cmnAcptkeigennukikin + kin;          -- 受注軽減税抜金額
            cmnAcptkeigentaxkin   := cmnAcptkeigentaxkin + oShohitaxkin;  -- 受注軽減消費税金額
          ELSIF iAcptnohinkbn = '1' THEN -- 納品消費税算出の場合
            cmnNohinkeigennukikin := cmnNohinkeigennukikin + kin;         -- 納品軽減税抜金額
            cmnNohinkeigentaxkin  := cmnNohinkeigentaxkin + oShohitaxkin; -- 納品軽減消費税金額
          END IF;
        END IF;
      END IF;
-- インボイス対応追加 end ABI

    END IF;
-- インボイス対応削除 start ABI
--    ELSE
--          S01SyohizeiService.SetSyohizeiKeisan(iTaxsetteikbn, --税設定区分
--                                               iTaxkeisankbn, --税計算区分
--                                                  iKirikaebi, --切り替え日
--                                                  iApKihonCd, --AP基本コード
--                                                     iShopCd, --ショップコード
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
  /* プロシージャ名：相手消費税設定                                                */
  /* 引数             ：meisai_rec           明細行                             */
  /*                  ：iSuryo              数量                               */
  /*                  ：iAiteUnt            単価（相手）                        */
  /*                  ：iAiteTaxsetteikbn   税設定区分（相手）                   */
  /*                  ：iAiteTaxkbn         税区分（相手）                       */
-- インボイス対応追加 start ABI
  /*                  ：iDlvrydd            配達日                               */
-- インボイス対応追加 end ABI
  /* 引数（ＯＵＴ）     ：oAiteUntShohitax    単価消費税（相手）                   */
  /*                  ：oAiteShohitax       消費税（相手）                       */
  /*****************************************************************************/
  PROCEDURE setAiteTax( meisai_rec        IN S04T002T%rowtype -- 明細行
                      , iSuryo            IN NUMBER   -- 数量
                      , iAiteUnt          IN NUMBER   -- 単価（相手）
                      , iAiteTaxsetteikbn    IN VARCHAR2 -- 税設定区分（相手）
                      , iAiteTaxkbn          IN VARCHAR2 -- 税区分（相手）
-- インボイス対応追加 start ABI
                      , iDlvrydd             IN VARCHAR2 -- 配達日
-- インボイス対応追加 end ABI
                      , oAiteUntshohitaxkin  OUT NUMBER -- 単価消費税（相手）
                      , oAiteShohitaxkin     OUT NUMBER -- 消費税（相手）
  ) IS
  /* 変数宣言 */
  taxkbn                    VARCHAR2(1)  := NULL; -- 消費税計算用税区分
  kin                       NUMBER(11,0) := 0;    -- 消費税計算用金額
  kbnAiteTaxkeisan          VARCHAR2(1)  := NULL; -- 相手税計算区分
  wDlvryytiDD               VARCHAR2(8)  := NULL; -- 配達予定日
  dlvrykbn                  VARCHAR2(2)  := NULL; -- 配達区分
  wUrikinhasukeisankbn      VARCHAR2(1)  := NULL;  -- 売上金額端数計算区分
-- インボイス対応追加 start ABI
  wInvoicshikoymd           VARCHAR2(8)  := NULL;  -- インボイス施行日
-- インボイス対応追加 end ABI
  BEGIN
    -- 初期化
    oAiteUntshohitaxkin := 0; -- 単価消費税（相手）
    oAiteShohitaxkin    := 0; -- 消費税（相手）

    -- 消費税計算用税区分の取得
    IF meisai_rec.KEIJOKBN = '08' THEN -- 計上区分：経費
       taxkbn := '2';                  -- 非課税
    ELSE
       taxkbn := iAiteTaxkbn; -- 商品税区分
    END IF;

    -- 相手税計算区分取得
    SELECT TAXKEISANKBN -- 税計算区分
         , DLVRYYTIDD -- 配達日予定日
         , DLVRYKBN -- 配達区分
      INTO kbnAiteTaxkeisan -- 税計算区分
         , wDlvryytiDD -- 配達日予定日
         , dlvrykbn -- 配達区分
      FROM S04T001T
     WHERE APKIHONCD = meisai_rec.ITAKUAITEAPKIHONCD
       AND SHOPCD = meisai_rec.ITAKUAITESHOPCD
       AND ACPTDNPYNO = meisai_rec.ITAKUAITEACPTDNPYNO
       AND EDANO = meisai_rec.ITAKUAITEEDANO
    ;

    -- 消費税計算用金額
    -- ショップマスタ取得
    SELECT SHPM.URIKINHASUKEISANKBN  -- 売上金額端数計算区分
-- インボイス対応追加 start ABI
         , SHPM.INVOICSHIKOYMD       -- インボイス施行日
-- インボイス対応追加 end ABI
    INTO wUrikinhasukeisankbn
-- インボイス対応追加 start ABI
       , wInvoicshikoymd
-- インボイス対応追加 end ABI
    FROM
      S02M046T SHPM
    WHERE
      SHPM.APKIHONCD = meisai_rec.ITAKUAITEAPKIHONCD
      AND SHPM.SHOPCD = meisai_rec.ITAKUAITESHOPCD
      AND SHPM.YUKOKBN = '1'
    ;
-- 本線対応変更 start ABI
--    kin := S01SyohizeiService.roundValue(iSuryo * iAiteUnt, wUrikinhasukeisankbn);
    kin := Z1808B01.roundValue(iSuryo * iAiteUnt, wUrikinhasukeisankbn);
-- 本線対応変更 end ABI

-- インボイス対応削除 start ABI
--    IF taxkbn <> '5' THEN
-- インボイス対応削除 end ABI
    -- 税計算区分：外税
    IF kbnAiteTaxkeisan = '2' THEN
       -- 単価消費税金額
       oAiteUntshohitaxkin := 0;
-- インボイス対応追加 start ABI
       -- 配達日がインボイス施行日前の場合、消費税を設定
       IF iDlvrydd < wInvoicshikoymd THEN
-- インボイス対応追加 end ABI
-- 本線対応変更 start ABI
--       oAiteShohitaxkin := S01SyohizeiService.SyohizeiKeisan('1', --単体計算フラグ（する）
          oAiteShohitaxkin := Z1808B01.SyohizeiKeisan('1', --単体計算フラグ（する）
-- 本線対応変更 end ABI
                                                  iAiteTaxsetteikbn, --税設定区分（相手）
                                                             taxkbn, --税区分（相手）
                                                   kbnAiteTaxkeisan, --税計算区分（相手）
                                                        wDlvryytiDD, --切り替え日
                                      meisai_rec.ITAKUAITEAPKIHONCD, --委託相手AP基本コード
                                         meisai_rec.ITAKUAITESHOPCD, --委託相手ショップコード
                                                               NULL, --仕入先コード
                                                               NULL, --数量
                                                               kin); -- 金額
-- インボイス対応追加 start ABI
       END IF;
-- インボイス対応追加 end ABI

    -- 税計算区分：内税
    ELSE
       -- 単価消費税金額
-- 本線対応変更 start ABI
--       oAiteUntshohitaxkin := S01SyohizeiService.SyohizeiKeisan('1', --単体計算フラグ（する）
       oAiteUntshohitaxkin := Z1808B01.SyohizeiKeisan('1', --単体計算フラグ（する）
-- 本線対応変更 end ABI
                                                  iAiteTaxsetteikbn, --税設定区分（相手）
                                                             taxkbn, --税区分
                                                   kbnAiteTaxkeisan, --税計算区分
                                                        wDlvryytiDD, --切り替え日
                                      meisai_rec.ITAKUAITEAPKIHONCD, --委託相手AP基本コード
                                         meisai_rec.ITAKUAITESHOPCD, --委託相手ショップコード
                                                           NULL, --仕入先コード
                                                           NULL, --数量
                                                       iAiteUnt); --単価
-- インボイス対応追加 start ABI
       -- 配達日がインボイス施行日前の場合、消費税金額を設定
       IF iDlvrydd < wInvoicshikoymd THEN
-- インボイス対応追加 end ABI
          IF iAiteUnt IS NULL OR iAiteUnt = 0 THEN
-- 本線対応変更 start ABI
--          oAiteShohitaxkin := S01SyohizeiService.SyohizeiKeisan('1', --単体計算フラグ（する）
             oAiteShohitaxkin := Z1808B01.SyohizeiKeisan('1', --単体計算フラグ（する）
-- 本線対応変更 end ABI
                                                     iAiteTaxsetteikbn, --税設定区分
                                                                taxkbn, --税区分
                                                      kbnAiteTaxkeisan, --税計算区分
                                                           wDlvryytiDD, --切り替え日
                                         meisai_rec.ITAKUAITEAPKIHONCD, --委託相手AP基本コード
                                            meisai_rec.ITAKUAITESHOPCD, --委託相手ショップコード
                                                                  NULL, --仕入先コード
                                                                  NULL, --数量
                                                                  kin); -- 金額
          ELSE
             oAiteShohitaxkin := oAiteUntshohitaxkin * iSuryo;
          END IF;
-- インボイス対応追加 start ABI
       END IF;
-- インボイス対応追加 end ABI
    END IF;
-- インボイス対応削除 start ABI
--    ELSE
--          S01SyohizeiService.SetSyohizeiKeisan(iAiteTaxsetteikbn, --税設定区分
--                                                kbnAiteTaxkeisan, --税計算区分
--                                                     wDlvryytiDD, --切り替え日
--                                   meisai_rec.ITAKUAITEAPKIHONCD, --AP基本コード
--                                      meisai_rec.ITAKUAITESHOPCD, --ショップコード
--                                                          iSuryo, --数量
--                                                meisai_rec.SHNCD, --商品コード
--                                                        dlvrykbn, --配達区分
--                                             meisai_rec.KEIJOKBN, --計上区分
--                                             oAiteUntshohitaxkin, --戻り値：単価消費税
--                                               oAiteShohitaxkin); --戻り値：消費税
--    END IF;
-- インボイス対応削除 end ABI
  END setAiteTax;

  /*****************************************************************************/
  /* プロシージャ名：本数、金額、サーバー集計                                       */
  /* 引数             ：meisai_rec         合計対象                             */
  /*                  ：iKeijokbn          計上区分                             */
  /*                  ：iWatercd           製品水大分類                         */
  /*                  ：iServerL           サーバー大分類                       */
  /*                  ：iServerM           サーバー本体中分類                    */
  /* 引数（IN OUT）    ：sum_rec            合計データ                           */
  /*****************************************************************************/
  PROCEDURE addAll( meisai_rec   IN S04T002T%rowtype -- 合計対象
                  , iKeijokbn    IN VARCHAR2 -- 計上区分
                  , iWatercd     IN VARCHAR2 -- 製品水大分類
                  , iServerL     IN VARCHAR2 -- サーバー大分類
                  , iServerM     IN VARCHAR2 -- サーバー本体中分類
                  , sum_rec  IN OUT sum_rec_type -- 合計データ
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
          sum_rec.DLVRYSHSUAKIN     := sum_rec.DLVRYSHSUAKIN     + meisai_rec.NOHINKIN;
       ELSE
          -- 配達実績その他売上金額
          sum_rec.DLVRYSONOTAURIKIN := sum_rec.DLVRYSONOTAURIKIN + meisai_rec.NOHINKIN;
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
       sum_rec.ACPTKIN  := sum_rec.ACPTKIN  + meisai_rec.ACPTKIN;  -- 受注金額
       sum_rec.ACPTSHOHITAXKIN  := sum_rec.ACPTSHOHITAXKIN  + meisai_rec.ACPTSHOHITAXKIN;  -- 受注消費税金額
       sum_rec.NOHINKIN := sum_rec.NOHINKIN + meisai_rec.NOHINKIN; -- 納品金額
       sum_rec.NOHINSHOHITAXKIN := sum_rec.NOHINSHOHITAXKIN + meisai_rec.NOHINSHOHITAXKIN; -- 納品消費税金額
    END IF;
  END addAll;

  /*****************************************************************************/
  /* プロシージャ名：キャンセル本数集計                                        */
  /* 引数             ：work_rec         配達実績ワークデータ                  */
  /* 引数（IN OUT）   ：sum_rec_cancel   合計データ(キャンセル用)              */
  /*****************************************************************************/
  PROCEDURE addAllCancel( work_rec           IN S05W001T%rowtype -- 配達実績ワークデータ
                        , sum_rec_cancel  IN OUT sum_rec_cancel_type -- 合計データ(キャンセル用)
  ) IS

  watercd         VARCHAR2(2)  := NULL;  -- 製品水大分類コード

  BEGIN

    watercd := getMojisetteiatai( '000', '000', '096', '1', NULL, NULL, '商品大分類コード：製品水' );

    -- キャンセル本数集計
    FOR meisai_cancel IN (
        SELECT
            LCTGRYCD               -- 大分類コード
            , SHNCD                -- 商品コード
            , JHMT.NOHINSURYO      -- 納品数量
            , JHMT.MEISAIKBN       -- 明細区分
            , JHMT.TANPINMEISAIFLG -- 単品明細フラグ
            , JHHT.KZBTLN1N2RITTORUHNS -- 回収実績ボトル１２Ｌ本数
            , JHHT.KZBTLN8RITTORUHNS   -- 回収実績ボトル８Ｌ本数
        FROM S04T002T JHMT
            INNER JOIN S04T001T JHHT
                ON ( JHHT.APKIHONCD      = JHMT.APKIHONCD
                    AND JHHT.SHOPCD     = JHMT.SHOPCD
                    AND JHHT.ACPTDNPYNO = JHMT.ACPTDNPYNO
                    AND JHHT.EDANO      = JHMT.EDANO
                )
        WHERE JHMT.APKIHONCD  = work_rec.APKIHONCD
            AND JHMT.SHOPCD     = work_rec.SHOPCD
            AND JHMT.ACPTDNPYNO = work_rec.ACPTDNPYNO
            AND JHMT.EDANO = work_rec.EDANO
            AND JHMT.YUKOKBN   = '1'
    ) LOOP

        IF meisai_cancel.TANPINMEISAIFLG = '1' AND meisai_cancel.MEISAIKBN = '0' THEN -- 単品商品
           -- 製品水合計
           IF meisai_cancel.LCTGRYCD = watercd THEN -- 製品水
              IF isRegular( meisai_cancel.SHNCD ) THEN -- レギュラー
                 sum_rec_cancel.RGRASURYO := sum_rec_cancel.RGRASURYO + meisai_cancel.NOHINSURYO; -- 配達レギュラー本数
                 sum_rec_cancel.KZBTLN1N2RITTORUHNS := meisai_cancel.KZBTLN1N2RITTORUHNS; -- 回収実績ボトル１２Ｌ本数
              ELSIF isSlim( meisai_cancel.SHNCD ) THEN -- スリム
                 sum_rec_cancel.SRMSURYO := sum_rec_cancel.SRMSURYO + meisai_cancel.NOHINSURYO; -- 配達スリム本数
                 sum_rec_cancel.KZBTLN8RITTORUHNS := meisai_cancel.KZBTLN8RITTORUHNS; -- 回収実績ボトル８Ｌ本数
              END IF;
           END IF;
        END IF;

    END LOOP;

  END addAllCancel;

  /****************************************************************************/
  /* プロシージャ名：基本情報設定                                                 */
  /* 引数        　    ：iApKihoncd           担当者ＡＰ基本コード                */
  /*                  ：iShopcd              担当者ショップコード                */
-- 本線対応追加 start ABI
  /*                  ：iTnshaapkihoncd      担当者ＡＰ基本コード             */
  /*                  ：iTnshashopcd         担当者ショップコード             */
-- 本線対応追加 end ABI
  /*                  ：iTnshacd             担当者コード                       */
  /*                  ：iIpaddress           ＩＰアドレス                       */
  /*                  ：iIpaddress           プログラムＩＤ                      */
  /****************************************************************************/
  PROCEDURE setUserInfo( iApKihoncd     IN VARCHAR2
                       , iShopcd        IN VARCHAR2
-- 本線対応追加 start ABI
                       , iTnshaapkihoncd IN VARCHAR2 -- 担当者ＡＰ基本コード
                       , iTnshashopcd    IN VARCHAR2 -- 担当者ショップコード
-- 本線対応追加 end ABI
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
-- 本線対応変更 start ABI
    --userinfo_rec.APKIHONCD := iApKihoncd; -- 担当者ＡＰ基本コード
    --userinfo_rec.SHOPCD    := iShopcd;    -- 担当者ショップコード
    userinfo_rec.APKIHONCD := iTnshaapkihoncd; -- 担当者ＡＰ基本コード
    userinfo_rec.SHOPCD    := iTnshashopcd;    -- 担当者ショップコード
-- 本線対応変更 end ABI
    userinfo_rec.TNSHACD   := iTnshacd;   -- 担当者コード
    userinfo_rec.IPADRSE   := iIpaddress; -- ＩＰアドレス
    userinfo_rec.PGMID     := iPgmid;     -- プログラムＩＤ

  END setUserInfo;

-- インボイス対応追加 start ABI
  /****************************************************************************/
  /* ファンクション名 ：インボイス施行日フラグ設定処理                        */
  /* 引数             ：iApKihoncd           ＡＰ基本コード                   */
  /*                  ：iShopcd              ショップコード                   */
  /*                  ：iTargetymd           対象日付                         */
  /* return           ：returnInvoiceFlg  インボイス施行日フラグ              */
  /****************************************************************************/
  FUNCTION setInvoiceFlg( iApKihoncd     IN VARCHAR2
                        , iShopcd        IN VARCHAR2
                        , iTargetymd     IN VARCHAR2
  ) RETURN VARCHAR2 IS returnInvoiceFlg VARCHAR2(1);

  -- 変数宣言
  wInvoicshikoymd VARCHAR2(8) := NULL;

  BEGIN
    -- インボイス施行日の取得
    SELECT SHPM.INVOICSHIKOYMD
      INTO wInvoicshikoymd
      FROM S02M046T SHPM
     WHERE SHPM.APKIHONCD      =  iApkihoncd
       AND SHPM.SHOPCD         =  iShopcd
       AND SHPM.YUKOKBN        =  '1'
    ;

    -- インボイス施行日 > パラメータ.対象日付の場合、戻り値にFALSEを設定
    IF wInvoicshikoymd > iTargetymd THEN
      RETURN('0');
    -- 上記以外の場合、戻り値にTRUEを設定
    ELSE
      RETURN('1');
    END IF;

  END setInvoiceFlg;
-- インボイス対応追加 end ABI

  /*****************************************************************************/
  /* プロシージャ名：在庫引当テーブル更新                                           */
  /* 引数             ：iAcpthikiatesakiapkihoncd   受注引当先ＡＰ基本コード       */
  /*                  ：iAcpthikiatesakishopcd      受注引当先ショップコード      */
  /*                  ：iAcpthikiatesakikyotencd    受注引当先拠点コード          */
  /*                  ：iShncd      商品コード                                  */
  /*                  ：iAcptsuryo  受注数量                                    */
  /*****************************************************************************/
  PROCEDURE updateZaikohikiate( iAcpthikiatesakiapkihoncd IN VARCHAR2 -- 受注引当先ＡＰ基本コード
                              , iAcpthikiatesakishopcd    IN VARCHAR2 -- 受注引当先ショップコード
                              , iAcpthikiatesakikyotencd  IN VARCHAR2 -- 受注引当先拠点コード
                              , iShncd     IN VARCHAR2 -- 商品コード
                              , iAcptsuryo IN NUMBER   -- 受注数量
  ) IS
  BEGIN
    UPDATE S13T004T ZKHT
       SET ZKHT.HIKIATESUMIZAIKOKAZU = ZKHT.HIKIATESUMIZAIKOKAZU - iAcptsuryo -- 引当済在庫数
         /* 更新者 */
         , ZKHT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
         , ZKHT.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
         , ZKHT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
         , ZKHT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
         , ZKHT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
         , ZKHT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
         , ZKHT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
         , ZKHT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
     WHERE ZKHT.APKIHONCD     = iAcpthikiatesakiapkihoncd
       AND ZKHT.SHOPCD        = iAcpthikiatesakishopcd
       AND ZKHT.DLVRYKYOTENCD = iAcpthikiatesakikyotencd
       AND ZKHT.SHNCD         = iShncd
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
              iAcpthikiatesakiapkihoncd -- 受注引当先ＡＰ基本コード
            , iAcpthikiatesakishopcd    -- 受注引当先ショップコード
            , iAcpthikiatesakikyotencd  -- 受注引当先拠点コード
            , iShncd     -- 商品コード
            , iAcptsuryo * (-1) -- 引当済在庫数
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
  END updateZaikohikiate;

  /*****************************************************************************/
  /* プロシージャ名：配達拠点受注アラームテーブル更新                                */
  /* 引数             ：iAcptsuryo        受注数量                              */
  /*                  ：vRecordS04T001T   受注配達テーブル                       */
  /*                  ：vRecordS04T002T   明細テーブル                           */
  /*****************************************************************************/
  PROCEDURE updateKyotenAlarm ( iAcptsuryo     IN NUMBER  -- 受注数量
                              , vRecordS04T001T IN S04T001T%rowtype
                              , vRecordS04T002T IN S04T002T%rowtype
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
     WHERE DKJA.APKIHONCD     = vRecordS04T001T.DLVRYKANKATSUAPKIHONCD
       AND DKJA.SHOPCD        = vRecordS04T001T.DLVRYKANKATSUSHOPCD
       AND DKJA.DLVRYKYOTENCD = vRecordS04T001T.DLVRYKYOTENCD
       AND DKJA.SHNCD         = vRecordS04T002T.SHNCD
       AND DKJA.RENBAN        = 1
    ;
    IF wCount = 0 THEN
       FOR wRenban IN 1..7 LOOP
           -- 年月日
           wYmd  := TO_CHAR(TO_DATE(userinfo_rec.UNYOYMD, 'YYYYMMDD') + (wRenban - 1), 'YYYYMMDD');
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
              WHERE DKAM.APKIHONCD = vRecordS04T001T.DLVRYKANKATSUAPKIHONCD
                AND DKAM.SHOPCD    = vRecordS04T001T.DLVRYKANKATSUSHOPCD
                AND DKAM.DLVRYKYOTENCD = vRecordS04T001T.DLVRYKYOTENCD
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
                  vRecordS04T001T.DLVRYKANKATSUAPKIHONCD -- 配達管轄ＡＰ基本コード
                , vRecordS04T001T.DLVRYKANKATSUSHOPCD    -- 配達管轄ショップコード
                , vRecordS04T001T.DLVRYKYOTENCD          -- 配達拠点コード
                , vRecordS04T002T.SHNCD                  -- 商品コード
                , wRenban -- 連番
                , wYmd    -- 日付
                , wYobi   -- 曜日
                , wAramusetteiAcptsuryo -- アラーム設定受注数量
                , 0 -- 受注数（初期値）
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
       END LOOP;
    END IF;

    UPDATE S04T005T DKJA
       SET DKJA.ACPTKAZU = DKJA.ACPTKAZU + iAcptsuryo -- 受注数
         /* 更新者 */
         , DKJA.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD -- 更新者ＡＰ基本コード
         , DKJA.KOSHASHOPCD    = userinfo_rec.SHOPCD    -- 更新者ショップコード
         , DKJA.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
         , DKJA.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
         , DKJA.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
         , DKJA.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
         , DKJA.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
         , DKJA.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
     WHERE DKJA.APKIHONCD     = vRecordS04T001T.DLVRYKANKATSUAPKIHONCD
       AND DKJA.SHOPCD        = vRecordS04T001T.DLVRYKANKATSUSHOPCD
       AND DKJA.DLVRYKYOTENCD = vRecordS04T001T.DLVRYKYOTENCD
       AND DKJA.SHNCD         = vRecordS04T002T.SHNCD
       AND DKJA.YMD           = vRecordS04T001T.DLVRYYTIDD
    ;
  END updateKyotenAlarm;

  /*****************************************************************************/
  /* プロシージャ名：担当者受注アラームテーブル更新                                  */
  /* 引数             ：iAcptcnt          受注件数                              */
  /*                  ：vRecordS04T001T   受注配達テーブル                       */
  /*****************************************************************************/
  PROCEDURE updateTnshaAlarm( iAcptcnt     IN NUMBER   -- 受注数量
                            , vRecordS04T001T IN S04T001T%rowtype
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
     WHERE TNJA.APKIHONCD     = vRecordS04T001T.DLVRYKANKATSUAPKIHONCD
       AND TNJA.SHOPCD        = vRecordS04T001T.DLVRYKANKATSUSHOPCD
       AND TNJA.DLVRYKYOTENCD = '00'
       AND TNJA.TNSHACD       = vRecordS04T001T.DLVRYTNSHACD
       AND TNJA.RENBAN        = 1
    ;
    IF wCount = 0 THEN
       FOR wRenban IN 1..7 LOOP
           -- 年月日
           wYmd  := TO_CHAR(TO_DATE(userinfo_rec.UNYOYMD, 'YYYYMMDD') + (wRenban - 1), 'YYYYMMDD');
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
            WHERE TNSM.APKIHONCD = vRecordS04T001T.DLVRYKANKATSUAPKIHONCD
              AND TNSM.SHOPCD    = vRecordS04T001T.DLVRYKANKATSUSHOPCD
              AND TNSM.TNSHACD   = vRecordS04T001T.DLVRYTNSHACD
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
                  vRecordS04T001T.DLVRYKANKATSUAPKIHONCD -- 配達管轄ＡＰ基本コード
                , vRecordS04T001T.DLVRYKANKATSUSHOPCD    -- 配達管轄ショップコード
                , '00' -- 配達拠点コード
                , vRecordS04T001T.DLVRYTNSHACD           -- 配達担当者コード
                , wRenban -- 連番
                , wYmd    -- 日付
                , wYobi   -- 曜日
                , wAramusetteiAcptcnt -- アラーム設定受注件数
                , 0 -- 受注件数（初期値）
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
       END LOOP;
    END IF;

    -- 担当者受注アラームテーブル更新
    UPDATE S04T006T TNJA
       SET TNJA.ACPTCNT = TNJA.ACPTCNT + iAcptcnt -- 受注件数
         /* 更新者 */
         , TNJA.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD -- 更新者ＡＰ基本コード
         , TNJA.KOSHASHOPCD    = userinfo_rec.SHOPCD    -- 更新者ショップコード
         , TNJA.KOSHACD        = userinfo_rec.TNSHACD   -- 更新者コード
         , TNJA.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
         , TNJA.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
         , TNJA.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
         , TNJA.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
         , TNJA.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
     WHERE TNJA.APKIHONCD     = vRecordS04T001T.DLVRYKANKATSUAPKIHONCD
       AND TNJA.SHOPCD        = vRecordS04T001T.DLVRYKANKATSUSHOPCD
       AND TNJA.DLVRYKYOTENCD = '00'
       AND TNJA.TNSHACD       = vRecordS04T001T.DLVRYTNSHACD
       AND TNJA.YMD           = vRecordS04T001T.DLVRYYTIDD
    ;

  END updateTnshaAlarm;

  /*****************************************************************************/
  /* プロシージャ名：配達エリア受注アラームテーブル更新                              */
  /* 引数             ：iAcptcnt          受注件数                              */
  /*                  ：iUnyoymd          運用日付                              */
  /*****************************************************************************/
  PROCEDURE updateAreaAlarm( iAcptcnt     IN NUMBER   -- 受注数量
                           , vRecordS04T001T IN S04T001T%rowtype
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
     WHERE DAJA.APKIHONCD     = vRecordS04T001T.DLVRYKANKATSUAPKIHONCD
       AND DAJA.SHOPCD        = vRecordS04T001T.DLVRYKANKATSUSHOPCD
       AND DAJA.DLVRYKYOTENCD = vRecordS04T001T.DLVRYKYOTENCD
       AND DAJA.DLVRYAREACD   = vRecordS04T001T.DLVRYAREACD
       AND DAJA.RENBAN        = 1
    ;
    IF wCount = 0 THEN
       FOR wRenban IN 1..7 LOOP
           -- 年月日
           wYmd  := TO_CHAR(TO_DATE(userinfo_rec.UNYOYMD, 'YYYYMMDD') + (wRenban - 1), 'YYYYMMDD');
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
            WHERE DARM.APKIHONCD = vRecordS04T001T.DLVRYKANKATSUAPKIHONCD
              AND DARM.SHOPCD    = vRecordS04T001T.DLVRYKANKATSUSHOPCD
              AND DARM.DLVRYKYOTENCD = vRecordS04T001T.DLVRYKYOTENCD
              AND DARM.DLVRYAREACD   = vRecordS04T001T.DLVRYAREACD
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
                  vRecordS04T001T.DLVRYKANKATSUAPKIHONCD -- 配達管轄ＡＰ基本コード
                , vRecordS04T001T.DLVRYKANKATSUSHOPCD    -- 配達管轄ショップコード
                , vRecordS04T001T.DLVRYKYOTENCD          -- 配達拠点コード
                , vRecordS04T001T.DLVRYAREACD            -- 配達担当者コード
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
     WHERE DAJA.APKIHONCD     = vRecordS04T001T.DLVRYKANKATSUAPKIHONCD
       AND DAJA.SHOPCD        = vRecordS04T001T.DLVRYKANKATSUSHOPCD
       AND DAJA.DLVRYKYOTENCD = vRecordS04T001T.DLVRYKYOTENCD
       AND DAJA.DLVRYAREACD   = vRecordS04T001T.DLVRYAREACD
       AND DAJA.YMD           = vRecordS04T001T.DLVRYYTIDD
    ;

  END updateAreaAlarm;

  /*****************************************************************************/
  /* プロシージャ名：配布物企画対象得意先マスタ更新：配布取消                         */
  /* 引数             ：iApkihoncd         ＡＰ基本コード                        */
  /*                  ：iShopcd            ショップコード                        */
  /*                  ：iHaifubutsucd      配布物コード                          */
  /*                  ：iTokuisakicd       得意先コード                          */
  /*****************************************************************************/
  PROCEDURE cancelHaifubutsuTKM( iApkihoncd    IN VARCHAR2 -- ＡＰ基本コード
                               , iShopcd       IN VARCHAR2 -- ショップコード
                               , iHaifubutsucd IN VARCHAR2 -- 配布物コード
                               , iTokuisakicd  IN VARCHAR2 -- 得意先コード
  ) IS
  BEGIN
    UPDATE S02M033T HTTM
       SET HTTM.HAIFUTRGTSTS = '1' -- 配布対象ステータス：キャンセル（'1'：未受注）
         , HTTM.ACPTDNPYNO = NULL -- 受注伝票番号
         , HTTM.EDANO      = NULL -- 枝番号
         , HTTM.ACPTDNPYMEISAINO = NULL -- 受注伝票明細番号
         , HTTM.ACPTTORIKOMIDD   = NULL -- 受注取込日
         , HTTM.DLVRYDD   = NULL -- 配達日
         /* 更新者 */
         , HTTM.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
         , HTTM.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
         , HTTM.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
         , HTTM.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
         , HTTM.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
         , HTTM.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
         , HTTM.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
         , HTTM.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
     WHERE HTTM.APKIHONCD    = iApkihoncd
       AND HTTM.SHOPCD       = iShopcd
       AND HTTM.HAIFUBUTSUCD = iHaifubutsucd
       AND HTTM.TOKUISAKICD  = iTokuisakicd
    ;
  END cancelHaifubutsuTKM;

  /*****************************************************************************/
  /* プロシージャ名：配布物企画対象得意先マスタ更新                                  */
  /* 引数             ：header_rec         受注配達伝票テーブル                   */
  /*                  ：meisai_rec         受注配達伝票明細テーブル               */
  /*****************************************************************************/
  PROCEDURE updateHaifubutsuTKM( header_rec    IN S04T001T%rowtype  -- 受注配達伝票テーブル
                               , meisai_rec    IN S04T002T%rowtype  -- 受注配達伝票明細テーブル
  ) IS
  BEGIN
    UPDATE S02M033T HTTM
       SET HTTM.HAIFUTRGTSTS = ( CASE WHEN header_rec.DLVRYSTS = '12' THEN '3'
                                      ELSE '2'
                                 END ) -- 配布対象ステータス：'2'：受注済; '3'：配達済）
         , HTTM.ACPTDNPYNO       = meisai_rec.ACPTDNPYNO       -- 受注伝票番号
         , HTTM.EDANO            = meisai_rec.EDANO            -- 枝番号
         , HTTM.ACPTDNPYMEISAINO = meisai_rec.ACPTDNPYMEISAINO -- 受注伝票明細番号
         , HTTM.ACPTTORIKOMIDD   = header_rec.ACPTDD           -- 受注取込日：受注日
         , HTTM.DLVRYDD          = ( CASE WHEN header_rec.DLVRYSTS = '12' THEN header_rec.DLVRYDD
                                                                          ELSE HTTM.DLVRYDD
                                     END ) -- 配達日：配達日
         /* 更新者 */
         , HTTM.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
         , HTTM.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
         , HTTM.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
         , HTTM.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
         , HTTM.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
         , HTTM.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
         , HTTM.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
         , HTTM.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
     WHERE HTTM.APKIHONCD    = meisai_rec.APKIHONCD
       AND HTTM.SHOPCD       = meisai_rec.SHOPCD
       AND HTTM.HAIFUBUTSUCD = meisai_rec.HAIFUBUTSUCD
       AND HTTM.TOKUISAKICD  = header_rec.TOKUISAKICD
    ;
  END updateHaifubutsuTKM;

  /*****************************************************************************/
  /* プロシージャ名：セット商品展開処理                                            */
  /* 引数             ：oya_rec        セット商品親明細行                         */
  /*                  ：header_rec     配達実績データヘッダ                       */
  /*                  ：iTaxkeisankbn  税計算区分                               */
  /*                  ：iEscapedList   退避済セット商品展開行リスト               */
  /*****************************************************************************/
  PROCEDURE processExpandMeisai( oya_rec       IN S04T002T%rowtype -- セット商品親明細行
                               , header_rec    IN S05W001T%rowtype -- 配達実績データヘッダ
                               , iTaxkeisankbn IN VARCHAR2 -- 税計算区分
                               , iEscapedList  IN t_s04_map -- 退避済セット商品展開行リスト
  ) IS
  /* 変数宣言 */
  child_rec S04T002T%rowtype := NULL;
  -- 消費税調整用
  sumKidAcptTax        NUMBER(11,0) := 0; -- 子商品受注消費税合計
  sumKidNohinTax       NUMBER(11,0) := 0; -- 子商品納品消費税合計
  sumKidAiteTax        NUMBER(11,0) := 0; -- 子商品相手消費税合計
  -- 相手税計算用
  kbnAiteTaxsettei     S02V001T.TAXSETTEIKBN%type := null; -- 相手税設定区分
  kbnAiteTax           S02V001T.TAXKBN%type := null;       -- 相手税区分
  wDlvryytiDD          S04T001T.DLVRYYTIDD%type := null; -- 配達予定日
  vUrikinhasukeisankbn VARCHAR2(1)  := NULL;  -- 売上金額端数計算区分
  wAcptTaxKbn          S02V001T.TAXKBN%type := null;       -- 受注税区分
  wAiteTokuisakiCd     S04T001T.TOKUISAKICD%type := null;  -- 委託相手得意先コード
  -- 展開マスタ検索用
  wApkihonCd           VARCHAR2(3)  := oya_rec.APKIHONCD;  -- AP基本コード

  -- ACCIS2023-7212 退避済セット商品展開行リストからデータを得るためのキーとマッチしたデータを保持する変数を追加
  v_key VARCHAR2(26) := NULL; -- 退避済セット商品展開行リストのキー
  v_escaped_rec S04T002T%ROWTYPE := NULL; -- 退避済セット商品展開行
  v_escaped_applied BOOLEAN := FALSE; -- 退避済セット商品展開行の情報を適用したかを示すフラグ

  /* CURSOR：構成商品 */
  CURSOR SELECT_KOSEI IS
    SELECT TNKM.KOSEISHNCD -- 構成商品コード
         , TNKM.SURYO -- 構成商品数量
         , TNKM.MOCHIKAERIPRICE -- 構成商品持ち帰り価格
         , TNKM.DLVRYPRICE      -- 構成商品配達価格
         , SHMV.LCTGRYCD -- 大分類コード
         , SHMV.MCTGRYCD -- 中分類コード
         , SHMV.SCTGRYCD -- 小分類コード
         -- 標準価格：配達区分、受注日より設定
         , ( CASE WHEN SHMV.HYOJUNPRICEKIRIKAEDD IS NULL OR header_rec.DLVRYJISSEKIDD >= SHMV.HYOJUNPRICEKIRIKAEDD
                  THEN ( CASE header_rec.DLVRYKBN
                              WHEN '3' THEN SHMV.SHINMOCHIKAERIPRICE -- 新持ち帰り価格
                              ELSE SHMV.SHINHYOJUNPRICE     -- 新標準価格
                         END )
                  ELSE ( CASE header_rec.DLVRYKBN
                              WHEN '3' THEN SHMV.GENZMOCHIKAERIPRICE -- 現持ち帰り価格
                              ELSE SHMV.GENZHYOJUNPRICE     -- 現標準価格
                         END )
             END ) HYOJUNUNT
         , CASE WHEN SHMV.TAXKBN IN ('4','5') AND header_rec.DLVRYJISSEKIDD <= '20190930' THEN '1' ELSE SHMV.TAXKBN END TAXKBN
         , CASE WHEN SHMV.TAXKBN IN ('4','5') AND wDlvryytiDD <= '20190930' THEN '1' ELSE SHMV.TAXKBN END ACPT_TAXKBN
         , SHMV.TAXSETTEIKBN -- 税設定区分
         , TNKM.RENBAN -- 連番
         , TNKM.KEIJOKBN -- 計上区分
      FROM S02M008T TNKM
     INNER JOIN S02V001T SHMV
             ON ( SHMV.APKIHONCD  = oya_rec.APKIHONCD
                  AND SHMV.SHOPCD = oya_rec.SHOPCD
                  AND SHMV.SHNCD  = TNKM.KOSEISHNCD
                  AND SHMV.SHOPYUKOKBN = '1'
                )
     WHERE TNKM.APKIHONCD  = wApkihonCd        -- ＡＰ基本コード
       AND TNKM.STSHNCD    = oya_rec.STSHNCD   -- セット商品コード
       AND TNKM.YUKOKBN    = '1' -- 有効区分
     ORDER BY TNKM.RENBAN
    ;
  BEGIN
    -- セット商品の商品コードの先頭1文字が「0（本部商品）」の場合、展開マスタ検索用AP基本コードを「001」に変更する
    IF (INSTR(oya_rec.STSHNCD, '0', 1) = 1) THEN
      wApkihonCd := '001';
    END IF;
    -- ショップマスタ取得
    SELECT
      SHPM.URIKINHASUKEISANKBN  -- 売上金額端数計算区分
    INTO vUrikinhasukeisankbn
    FROM
      S02M046T SHPM
    WHERE
      SHPM.APKIHONCD = oya_rec.APKIHONCD
      AND SHPM.SHOPCD = oya_rec.SHOPCD
      AND SHPM.YUKOKBN = '1'
    ;

  -- 配達予定日を取得
    BEGIN
      SELECT JHTT.DLVRYYTIDD
        INTO wDlvryytiDD
      FROM S04T001T JHTT
      WHERE JHTT.APKIHONCD  = header_rec.APKIHONCD
        AND JHTT.SHOPCD     = header_rec.SHOPCD
        AND JHTT.ACPTDNPYNO = header_rec.ACPTDNPYNO
        AND JHTT.EDANO      = header_rec.EDANO
        AND JHTT.YUKOKBN   = '1';
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        wDlvryytiDD := header_rec.DLVRYJISSEKIDD;
    END;

    child_rec := oya_rec;
    FOR kosei_rec IN SELECT_KOSEI LOOP
        -- ACCIS2023-7212 退避済セット商品展開行リストの情報を適用したかを示すフラグを初期化
        v_escaped_applied := FALSE;

        child_rec.ACPTDNPYMEISAIEDANO := child_rec.ACPTDNPYMEISAIEDANO + 1;
        child_rec.ITAKUAITEACPTDNPYMEISAIEDANO := ( CASE WHEN child_rec.ITAKUAITEACPTDNPYMEISAIEDANO IS NULL
                                                         THEN NULL
                                                         ELSE child_rec.ITAKUAITEACPTDNPYMEISAIEDANO + 1
                                                    END );
        child_rec.LCTGRYCD := kosei_rec.LCTGRYCD;
        child_rec.MCTGRYCD := kosei_rec.MCTGRYCD;
        child_rec.SCTGRYCD := kosei_rec.SCTGRYCD;
        child_rec.SHNCD    := kosei_rec.KOSEISHNCD;
        child_rec.KATASIKI := NULL; -- 型式
        -- 計上区分
        IF oya_rec.KEIJOKBN IN ('01', '02') THEN
            -- 親が有償の場合、構成商品の計上区分を使用
            child_rec.KEIJOKBN := kosei_rec.KEIJOKBN;
        ELSE
            -- 親が無償の場合、親の計上区分を使用
            child_rec.KEIJOKBN := oya_rec.KEIJOKBN;
        END IF;
        child_rec.HYOJUNUNT := kosei_rec.HYOJUNUNT;
        child_rec.ACPTSURYO := NVL(oya_rec.ACPTSURYO,0) * kosei_rec.SURYO ;
        child_rec.NOHINSURYO := NVL(oya_rec.NOHINSURYO,0) * kosei_rec.SURYO ;
-- インボイス対応削除 start ABI
--        --親商品の税区分が「5:子明細商品」の場合
--        IF oya_rec.TAXKBN = '5' THEN
--          child_rec.TAXKBN := kosei_rec.TAXKBN;
--        END IF;
-- インボイス対応削除 end ABI

        -- 単価、金額、消費税
        IF oya_rec.KEIJOKBN IN ( '01', '02' ) THEN -- 有料（親計上区分：売上／キャンペーン）
           IF child_rec.ACPTSURYO <> 0 THEN
              child_rec.ACPTUNT := ( CASE header_rec.DLVRYKBN WHEN '3' -- 配達区分：持ち帰り
                                          THEN kosei_rec.MOCHIKAERIPRICE   -- 持ち帰り価格
                                          ELSE kosei_rec.DLVRYPRICE END ); -- 配達価格
              -- 適用期間内の得意先別セット商品単価展開がある場合は受注単価にセットする
              BEGIN
                SELECT
                   CASE WHEN header_rec.DLVRYKBN = '3' THEN TSTM.MOCHIKAERIPRICE ELSE TSTM.DLVRYPRICE END
                INTO
                   child_rec.ACPTUNT
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
                       AND TSTM.RENBAN                 = kosei_rec.RENBAN
                       AND TSTM.YUKOKBN                = '1'
                   )
                WHERE
                    TSSM.APKIHONCD   = header_rec.APKIHONCD                                   -- ＡＰ基本コード
                AND TSSM.SHOPCD      = header_rec.SHOPCD                                      -- ショップコード
                AND TSSM.STSHNCD     = child_rec.STSHNCD                                      -- セット商品コード
                AND TSSM.TOKUISAKICD = header_rec.TOKUISAKICD                                 -- 得意先コード
                AND wDlvryytiDD BETWEEN TSSM.TEKIYOSTRTDD AND TSSM.TEKIYOSHRYDD               -- 配達予定日：適用期間内
                AND TSSM.YUKOKBN     = '1'
                ;
              EXCEPTION
                WHEN NO_DATA_FOUND THEN
                  NULL;
                WHEN TOO_MANY_ROWS THEN
                  RAISE_APPLICATION_ERROR(-20016, '得意先別セット展開単価が複数見つかりました。');
              END;

              -- 受注時税区分
              SELECT CASE WHEN SHMV.TAXKBN IN ('4','5') AND wDlvryytiDD <= '20190930' THEN '1' ELSE SHMV.TAXKBN END
              INTO   wAcptTaxKbn
              FROM S02V001T SHMV
              WHERE SHMV.APKIHONCD  = oya_rec.APKIHONCD
                AND SHMV.SHOPCD = oya_rec.SHOPCD
                AND SHMV.SHNCD = oya_rec.STSHNCD
                AND SHMV.SHOPYUKOKBN = '1';
-- インボイス対応削除 start ABI
--              IF oya_rec.TAXKBN = '5' THEN
--                wAcptTaxKbn := kosei_rec.ACPT_TAXKBN;
--              END IF;
-- インボイス対応削除 end ABI

-- 本線対応変更 start ABI
--              child_rec.ACPTKIN := S01SyohizeiService.roundValue(child_rec.ACPTUNT * child_rec.ACPTSURYO, vUrikinhasukeisankbn);  --受注金額

              -- ACCIS2023-7212 退避済の展開行リストから受注単価と委託会社受注単価を設定
              v_key := get_s04_key(child_rec);
              IF iEscapedList.EXISTS(v_key) THEN
                v_escaped_rec := iEscapedList(v_key);
                -- 退避済展開行の受注単価、と委託会社受注単価を展開した受注配達明細に設定
                child_rec.ACPTUNT := v_escaped_rec.ACPTUNT;
                child_rec.ITAKUCMPNYACPTUNT := v_escaped_rec.ITAKUCMPNYACPTUNT;
                -- 退避済セット商品展開行の情報を適用したのでフラグをTRUEに設定
                -- ※委託会社受注単価の設定をするかどうか後続処理で判定する際に用いる
                v_escaped_applied := TRUE;
              END IF;

              child_rec.ACPTKIN := Z1808B01.roundValue(child_rec.ACPTUNT * child_rec.ACPTSURYO, vUrikinhasukeisankbn);  --受注金額
-- 本線対応変更 end ABI
              -- 更新の場合、受注配達テーブル.配達日予定日。新規登録の場合、モバイル配達実績取込ワーク.配達実績日
              IF wDlvryytiDD IS NULL THEN
                wDlvryytiDD := header_rec.DLVRYJISSEKIDD;
              END IF;
              setTax( child_rec -- 明細行
                    , child_rec.ACPTSURYO -- 数量
                    , child_rec.ACPTUNT   -- 単価
                    , child_rec.APKIHONCd
                    , child_rec.SHOPCD
                    , wAcptTaxKbn         -- 税区分
                    , kosei_rec.TAXSETTEIKBN  -- 税設定区分
                    , iTaxkeisankbn -- 税計算区分
                    , wDlvryytiDD -- 切替日
                    , header_rec.DLVRYKBN --配達区分
-- インボイス対応追加 start ABI
                    , '1'
                    , '0'
-- インボイス対応追加 end ABI
                    , child_rec.ACPTUNTSHOHITAXKIN -- 単価消費税
                    , child_rec.ACPTSHOHITAXKIN    -- 消費税
              );
           END IF;
           IF child_rec.ITAKUAITEACPTDNPYMEISAIEDANO IS NOT NULL THEN
             -- セット商品の商品コードの先頭1文字が「0（本部商品）」の場合、展開マスタ検索用AP基本コードを「001」に変更する
             IF (INSTR(oya_rec.STSHNCD, '0', 1) = 1) THEN
               wApkihonCd := '001';
             ELSE
               wApkihonCd := child_rec.ITAKUAITEAPKIHONCD;
             END IF;
             BEGIN -- 委託会社情報再取得
               -- ACCIS2023-7212 委託会社受注単価のをマスタから設定するかどうかをフラグで制御するように修正
               IF NOT v_escaped_applied THEN
                 SELECT ( CASE header_rec.DLVRYKBN WHEN '3' -- 配達区分：持ち帰り
                          THEN TNKM.MOCHIKAERIPRICE -- 持ち帰り価格
                          ELSE TNKM.DLVRYPRICE      -- 配達価格
                          END )
                      , SHMV.TAXSETTEIKBN -- 税設定区分
                      , SHMV.TAXKBN -- 税区分
                 INTO child_rec.ITAKUCMPNYACPTUNT -- 相手受注単価
                     , kbnAiteTaxsettei -- 相手税設定区分
                     , kbnAiteTax       -- 相手税区分
                 FROM S02M008T TNKM
                 INNER JOIN S02V001T SHMV
                    ON ( SHMV.APKIHONCD  = child_rec.ITAKUAITEAPKIHONCD
                     AND SHMV.SHOPCD = child_rec.ITAKUAITESHOPCD
                     AND SHMV.SHNCD  = TNKM.KOSEISHNCD
                     AND SHMV.SHOPYUKOKBN = '1'
                 )
                 WHERE TNKM.APKIHONCD = child_rec.ITAKUAITEAPKIHONCD -- 委託相手ＡＰ基本コード
                   AND TNKM.STSHNCD   = child_rec.STSHNCD -- セット商品コード
                   AND TNKM.RENBAN   = kosei_rec.RENBAN  -- 連番
                   AND TNKM.YUKOKBN   = '1' -- 有効区分
                 ;
                 -- 委託相手得意先コードを取得
                 SELECT ITAKU.TOKUISAKICD
                 INTO wAiteTokuisakiCd
                 FROM S04T001T JUTAKU
                 INNER JOIN
                   S04T001T ITAKU
                   ON ITAKU.APKIHONCD     = JUTAKU.ITAKUAITEAPKIHONCD
                  AND ITAKU.SHOPCD        = JUTAKU.ITAKUAITESHOPCD
                  AND ITAKU.ACPTDNPYNO    = JUTAKU.ITAKUAITEACPTDNPYNO
                  AND ITAKU.EDANO         = JUTAKU.ITAKUAITEEDANO
                 WHERE JUTAKU.APKIHONCD   = header_rec.APKIHONCD
                   AND JUTAKU.SHOPCD      = header_rec.SHOPCD
                   AND JUTAKU.ACPTDNPYNO  = header_rec.ACPTDNPYNO
                   AND JUTAKU.EDANO       = header_rec.EDANO;
                 -- 委託相手の得意先別セット商品単価展開マスタから単価を取得
                 BEGIN
                   SELECT
                     CASE WHEN header_rec.DLVRYKBN = '3' THEN TSTM.MOCHIKAERIPRICE ELSE TSTM.DLVRYPRICE END
                   INTO
                     child_rec.ITAKUCMPNYACPTUNT
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
                      AND TSTM.RENBAN                 = kosei_rec.RENBAN
                      AND TSTM.YUKOKBN                = '1'
                   )
                   WHERE
                         TSSM.APKIHONCD   = child_rec.ITAKUAITEAPKIHONCD                                   -- ＡＰ基本コード
                     AND TSSM.SHOPCD      = child_rec.ITAKUAITESHOPCD                                      -- ショップコード
                     AND TSSM.STSHNCD     = child_rec.STSHNCD                                      -- セット商品コード
                     AND TSSM.TOKUISAKICD = wAiteTokuisakiCd                                       -- 得意先コード
                     AND wDlvryytiDD BETWEEN TSSM.TEKIYOSTRTDD AND TSSM.TEKIYOSHRYDD               -- 配達予定日：適用期間内
                     AND TSSM.YUKOKBN     = '1'
                   ;
                 EXCEPTION
                   WHEN NO_DATA_FOUND THEN
                     NULL;
                   WHEN TOO_MANY_ROWS THEN
                     RAISE_APPLICATION_ERROR(-20016, '得意先別セット展開単価が複数見つかりました。');
                 END;

                 kbnAiteTax := wAcptTaxKbn;
                 setAiteTax( child_rec -- 明細行
                           , child_rec.ACPTSURYO -- 数量
                           , child_rec.ITAKUCMPNYACPTUNT -- 単価（委託会社）
                           , kbnAiteTaxsettei -- 相手税設定区分
                           , kbnAiteTax       -- 相手税区分
-- インボイス対応追加 start ABI
                           , header_rec.DLVRYJISSEKIDD -- 配達実績日
-- インボイス対応追加 end ABI
                           , child_rec.ITAKUCMPNYACPTUNTSHOHITAXKIN -- 単価消費税（委託会社）
                           , child_rec.ITAKUCMPNYACPTSHOHITAXKIN    -- 消費税（委託会社）
                 );

               END IF;
             EXCEPTION
               WHEN OTHERS THEN
                 RAISE_APPLICATION_ERROR(-20010, SQLERRM || '委託と受託のセット商品構成が同じではありません。');
             END;
           END IF;

           IF child_rec.NOHINSURYO <> 0 THEN
              child_rec.NOHINUNT := ( CASE header_rec.DLVRYKBN WHEN '3' -- 配達区分：持ち帰り
                                           THEN kosei_rec.MOCHIKAERIPRICE   -- 持ち帰り価格
                                           ELSE kosei_rec.DLVRYPRICE END ); -- 配達価格

              -- 適用期間内の得意先別セット商品単価がある場合は納品単価にセットする
              BEGIN
                   SELECT
                       CASE WHEN header_rec.DLVRYKBN = '3' THEN TSTM.MOCHIKAERIPRICE ELSE TSTM.DLVRYPRICE END
                   INTO
                       child_rec.NOHINUNT
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
                           AND TSTM.RENBAN                 = kosei_rec.RENBAN
                           AND TSTM.YUKOKBN                = '1'
                       )
                   WHERE
                       TSSM.APKIHONCD   = header_rec.APKIHONCD                                   -- ＡＰ基本コード
                   AND TSSM.SHOPCD      = header_rec.SHOPCD                                      -- ショップコード
                   AND TSSM.STSHNCD     = child_rec.STSHNCD                                      -- セット商品コード
                   AND TSSM.TOKUISAKICD = header_rec.TOKUISAKICD                                 -- 得意先コード
                   AND header_rec.DLVRYJISSEKIDD BETWEEN TSSM.TEKIYOSTRTDD AND TSSM.TEKIYOSHRYDD -- 配送実績日：適用期間内
                   AND TSSM.YUKOKBN     = '1'
                   ;
              EXCEPTION
                WHEN NO_DATA_FOUND THEN
                   NULL;
                WHEN TOO_MANY_ROWS THEN
                   RAISE_APPLICATION_ERROR(-20016, '得意先別セット単価が複数見つかりました。');
              END;

-- 本線対応変更 start ABI
--              child_rec.NOHINKIN := S01SyohizeiService.roundValue(child_rec.NOHINUNT * child_rec.NOHINSURYO, vUrikinhasukeisankbn);  --納品金額
              child_rec.NOHINKIN := Z1808B01.roundValue(child_rec.NOHINUNT * child_rec.NOHINSURYO, vUrikinhasukeisankbn);  --納品金額
-- 本線対応変更 end ABI
              -- 受注消費税
-- インボイス対応削除 start ABI
--              IF oya_rec.TAXKBN = '5' THEN
--              child_rec.TAXKBN := kosei_rec.TAXKBN;
--              END IF;
-- インボイス対応削除 end ABI
              setTax( child_rec -- 明細行
                    , child_rec.NOHINSURYO -- 数量
                    , child_rec.NOHINUNT   -- 単価
                    , child_rec.APKIHONCD
                    , child_rec.SHOPCD
                    , child_rec.TAXKBN    -- 税区分
                    , kosei_rec.TAXSETTEIKBN  -- 税設定区分
                    , iTaxkeisankbn -- 税計算区分
                    , header_rec.DLVRYJISSEKIDD -- 切替日
                    , header_rec.DLVRYKBN --配達区分
-- インボイス対応追加 start ABI
                    , '1'
                    , '1'
-- インボイス対応追加 end ABI
                    , child_rec.NOHINUNTSHOHITAXKIN -- 単価消費税
                    , child_rec.NOHINSHOHITAXKIN    -- 消費税
              );
           END IF;

        END IF;
        child_rec.NYURYOKUMEISAIFLG := '0'; -- 入力明細フラグ
        child_rec.TANPINMEISAIFLG   := '1'; -- 単品明細フラグ
        child_rec.URIDNPYMEISAIEDANO := ( CASE WHEN child_rec.URIDNPYMEISAIEDANO IS NULL
                                               THEN NULL
                                               ELSE child_rec.URIDNPYMEISAIEDANO + 1
                                          END ); -- 売上伝票明細枝番号
-- インボイス対応追加 start ABI
        IF cmnInvoiceshikoymdflg = '1' THEN
           child_rec.NOHINSHOHITAXKIN := 0; -- 納品消費税金額
           child_rec.ACPTSHOHITAXKIN  := 0; -- 受注消費税金額
        END IF;
-- インボイス対応追加 end ABI

        -- 子商品登録
        INSERT INTO S04T002T VALUES child_rec;

        -- 消費税合計
        sumKidAcptTax  := sumKidAcptTax  + child_rec.ACPTSHOHITAXKIN;  -- 子商品受注消費税合計
        sumKidNohinTax := sumKidNohinTax + child_rec.NOHINSHOHITAXKIN; -- 子商品納品消費税合計
        sumKidAiteTax  := sumKidAiteTax  + NVL(child_rec.ITAKUCMPNYACPTSHOHITAXKIN, 0); -- 子商品相手消費税合計

    END LOOP;

-- インボイス対応追加 start ABI
    IF cmnInvoiceshikoymdflg = '0' THEN
-- インボイス対応追加 end ABI
      -- 消費税調整
      IF sumKidAcptTax <> oya_rec.ACPTSHOHITAXKIN      -- 受注消費税調整
         OR sumKidNohinTax <> oya_rec.NOHINSHOHITAXKIN -- 納品消費税調整
         THEN
         child_rec.ACPTDNPYMEISAIEDANO := child_rec.ACPTDNPYMEISAIEDANO + 1; -- 受注伝票明細枝番号
         child_rec.ITAKUAITEACPTDNPYMEISAIEDANO := ( CASE WHEN child_rec.ITAKUAITEACPTDNPYMEISAIEDANO IS NULL
                                                          THEN NULL
                                                          ELSE child_rec.ITAKUAITEACPTDNPYMEISAIEDANO + 1
                                                     END );
         -- 消費税調整商品コード取得
         child_rec.SHNCD := getMojisetteiatai( '000', '000', '166', NULL, NULL, NULL, '消費税調整商品コード');
         IF child_rec.SHNCD IS NULL THEN
            RAISE_APPLICATION_ERROR(-200016, '消費税調整商品コード　が設定されていません。');
         END IF;
         -- 商品マスタビューからの商品情報を取得する
         SELECT SHMV.LCTGRYCD -- 大分類コード
              , SHMV.MCTGRYCD -- 中分類コード
              , SHMV.SCTGRYCD -- 小分類コード
              -- 標準価格：配達区分、受注日より設定
              , ( CASE WHEN SHMV.HYOJUNPRICEKIRIKAEDD IS NULL OR header_rec.DLVRYJISSEKIDD >= SHMV.HYOJUNPRICEKIRIKAEDD
                       THEN ( CASE header_rec.DLVRYKBN
                                   WHEN '3' THEN SHMV.SHINMOCHIKAERIPRICE -- 新持ち帰り価格
                                   ELSE SHMV.SHINHYOJUNPRICE     -- 新標準価格
                              END )
                       ELSE ( CASE header_rec.DLVRYKBN
                                   WHEN '3' THEN SHMV.GENZMOCHIKAERIPRICE -- 現持ち帰り価格
                                   ELSE SHMV.GENZHYOJUNPRICE     -- 現標準価格
                              END )
                  END ) HYOJUNUNT
              -- 税区分
              , SHMV.TAXKBN
           INTO child_rec.LCTGRYCD -- 大分類コード
              , child_rec.MCTGRYCD -- 中分類コード
              , child_rec.SCTGRYCD -- 小分類コード
              , child_rec.HYOJUNUNT -- 標準価格
              , child_rec.TAXKBN    -- 税区分
           FROM S02V001T SHMV
          WHERE SHMV.APKIHONCD   = child_rec.APKIHONCD -- ＡＰ基本コード
            AND SHMV.SHOPCD      = child_rec.SHOPCD    -- ショップコード
            AND SHMV.SHNCD       = child_rec.SHNCD -- 消費税調整商品コード
            AND SHMV.SHOPYUKOKBN = '1' -- ショップ有効区分
         ;
         child_rec.TAXKBN := oya_rec.TAXKBN;
         child_rec.KATASIKI := NULL; -- 型式
          child_rec.KEIJOKBN := oya_rec.KEIJOKBN;
         -- 受注
         child_rec.ACPTSURYO := CASE WHEN oya_rec.ACPTSHOHITAXKIN <> sumKidAcptTax THEN 1 ELSE 0 END; -- 受注数量
         child_rec.HYOJUNUNT := 0; -- 標準単価
         child_rec.ACPTUNT   := 0; -- 受注単価
         child_rec.ACPTKIN   := 0; -- 受注金額
         child_rec.ACPTUNTSHOHITAXKIN := 0; -- 受注単価消費税金額
         child_rec.ACPTSHOHITAXKIN    := oya_rec.ACPTSHOHITAXKIN - sumKidAcptTax; -- 受注消費税金額

         -- 委託会社
         IF child_rec.ITAKUAITEACPTDNPYMEISAIEDANO IS NOT NULL THEN
            child_rec.ITAKUCMPNYACPTUNT   := 0;          -- 委託会社受注単価
            child_rec.ITAKUCMPNYACPTUNTSHOHITAXKIN := 0; -- 委託会社受注単価消費税金額
            child_rec.ITAKUCMPNYACPTSHOHITAXKIN := oya_rec.ITAKUCMPNYACPTSHOHITAXKIN - sumKidAiteTax; -- 委託会社受注消費税金額
         END IF;

         -- 納品
         child_rec.NOHINSURYO := CASE WHEN oya_rec.NOHINSHOHITAXKIN <> sumKidNohinTax THEN 1 ELSE 0 END; -- 納品数量
         child_rec.NOHINUNT := 0; -- 納品単価
         child_rec.NOHINKIN := 0; -- 納品金額
         child_rec.NOHINUNTSHOHITAXKIN := 0; -- 納品単価消費税金額
         child_rec.NOHINSHOHITAXKIN := oya_rec.NOHINSHOHITAXKIN - sumKidNohinTax; -- 納品消費税金額
         child_rec.YUSYOKBN  := '0'; -- 有償区分
         child_rec.MEISAIKBN := '3'; -- 明細区分：消費税調整
         child_rec.NYURYOKUMEISAIFLG := '0'; -- 入力明細フラグ：展開行
         child_rec.TANPINMEISAIFLG   := '1'; -- 単品明細フラグ：単品
         child_rec.URIDNPYMEISAIEDANO := ( CASE WHEN child_rec.URIDNPYMEISAIEDANO IS NULL
                                                THEN NULL
                                                ELSE child_rec.URIDNPYMEISAIEDANO + 1
                                           END ); -- 売上伝票明細枝番号

         -- 登録
         INSERT INTO S04T002T JHMT VALUES child_rec;

      END IF;
-- インボイス対応追加 start ABI
    END IF;
-- インボイス対応追加 end ABI

    EXCEPTION
      WHEN OTHERS THEN
        IF SELECT_KOSEI%ISOPEN THEN
           CLOSE SELECT_KOSEI;
        END IF;
        RAISE_APPLICATION_ERROR(-20010, SQLERRM || 'セット商品展開時エラーが発生しました。');
  END processExpandMeisai;

  -- ポイント制度対応追加 start ABI
  /******************************************************************************/
  /* ファンクション名 ：ポイントテーブルの管理番号採番                          */
  /* 引数             ：header_rec         配達実績データヘッダ                 */
  /* return           ：retPointKanrino    管理番号                             */
  /******************************************************************************/
  FUNCTION GetPointKanrino(header_rec        IN S05W001T%rowtype
  ) RETURN VARCHAR2 IS retPointKanrino       VARCHAR2(15);
  BEGIN
    -- 管理番号採番
    -- 引数ワーク(S20W002T)登録
    INSERT INTO S20W002T VALUES (
        header_rec.APKIHONCD      -- ＡＰ基本コード
      , header_rec.SHOPCD         -- ショップコード
      , userinfo_rec.APKIHONCD    -- 担当者ＡＰ基本コード
      , userinfo_rec.SHOPCD       -- 担当者ショップコード
      , userinfo_rec.TNSHACD      -- 担当者コード
      , c_PRCSID                  -- 処理ID
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
        header_rec.APKIHONCD         -- AP基本コード
      , header_rec.SHOPCD            -- ショップコード
      , userinfo_rec.APKIHONCD       -- 担当者ＡＰ基本コード
      , userinfo_rec.SHOPCD          -- 担当者ショップコード
      , userinfo_rec.TNSHACD         -- 担当者コード
      , c_PRCSID                     -- 処理ID
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
      AND PRCSID      = c_PRCSID;

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
  /*                ：header_rec      配達実績データヘッダ                     */
  /*****************************************************************************/
  PROCEDURE registPointHeaderTable(kanrino         IN VARCHAR2
                                  , header_rec     IN S05W001T%rowtype
  ) IS
  BEGIN
    -- S20T001T（予定発生ポイントテーブル）登録
    INSERT INTO S20T001T VALUES (
        header_rec.APKIHONCD      -- ＡＰ基本コード
      , header_rec.SHOPCD         -- ショップコード
      , kanrino                   -- 管理番号
      , header_rec.ACPTDNPYNO     -- 受注伝票番号
      , header_rec.EDANO          -- 受注枝番号
      , null                      -- 売上伝票番号（別途更新設定）
      , null                      -- 売上枝番号（別途更新設定）
      , header_rec.TOKUISAKICD    -- 得意先コード
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
  /* 引数           ：header_rec     配達実績データヘッダ                      */
  /*                ：meisai_rec     受注配達明細テーブル                      */
  /*****************************************************************************/
  PROCEDURE registPointMeisaiTable(header_rec             IN S05W001T%rowtype
                                  , meisai_rec            IN S04T002T%rowtype
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

  -- S04T001T 存在判定用
  wHasHeader NUMBER := 0;

  BEGIN
    /* S04T001Tの存在チェック */
    SELECT COUNT(*)
      INTO wHasHeader
      FROM S04T001T
    WHERE APKIHONCD  = header_rec.APKIHONCD
      AND SHOPCD     = header_rec.SHOPCD
      AND ACPTDNPYNO = meisai_rec.ACPTDNPYNO
      AND EDANO      = meisai_rec.EDANO
      AND YUKOKBN    = '1';

    -- S04T001Tのレコードがない場合
    IF wHasHeader = 0 THEN
      -- 後続処理で作成するS04T001TにINSERTする値を設定
      wAcptdd            := header_rec.DLVRYJISSEKIDD;
      wDlvrykibojikankbn := NULL;
      wDlvrykbn          := header_rec.DLVRYKBN;
      wAcptrcptkeirokbn  := '99';
    ELSE
      -- ポイント情報部品で引数ワークに設定する情報を取得する
      SELECT
        ACPTDD              -- 受注日
        , DLVRYKIBOJIKANKBN -- 配達希望時間区分
        , DLVRYKBN          -- 配達区分
        , ACPTRCPTKEIROKBN  -- 受注受付経路区分
      INTO
        wAcptdd
        , wDlvrykibojikankbn
        , wDlvrykbn
        , wAcptrcptkeirokbn
      FROM S04T001T
      WHERE APKIHONCD  = header_rec.APKIHONCD
        AND SHOPCD     = header_rec.SHOPCD
        AND ACPTDNPYNO = meisai_rec.ACPTDNPYNO
        AND EDANO      = meisai_rec.EDANO
        AND YUKOKBN    = '1';
    END IF;

    -- 明細分設定した引数ワークでポイント情報部品実行
    Z2401B01.PointinfbhnProc(
        header_rec.APKIHONCD      -- ＡＰ基本コード
      , header_rec.SHOPCD         -- ショップコード
      , userinfo_rec.APKIHONCD    -- 担当者ＡＰ基本コード
      , userinfo_rec.SHOPCD       -- 担当者ショップコード
      , userinfo_rec.TNSHACD      -- 担当者コード
      , c_PRCSID                  -- 処理ID
      , userinfo_rec.IPADRSE      -- ＩＰアドレス
      , '02'                      -- ポイント情報部品処理種別(一括計算)
      , header_rec.TOKUISAKICD    -- 得意先コード
      , '01'                      -- 伝達種別(受注)
      , wAcptdd                   -- 注文日:受注日
      , header_rec.DLVRYJISSEKIDD -- 配達実績日
      , wDlvrykibojikankbn        -- 配達希望時間区分
      , wDlvrykbn                 -- 配達区分
      , wAcptrcptkeirokbn);       -- 受注受付経路区分

    -- 処理結果ワーク(S20W003T)から実行結果読取り
    i := 0;
    FOR cS20W003T IN (SELECT * FROM S20W003T
                      WHERE TNSHAAPKIHONCD  = userinfo_rec.APKIHONCD
                        AND TNSHASHOPCD     = userinfo_rec.SHOPCD
                        AND TNSHACD         = userinfo_rec.TNSHACD
                        AND PRCSID          = c_PRCSID
                        AND YUKOKBN         = '1'
                      ORDER BY TO_NUMBER(GYOBANGOU))
    LOOP
      wS20W003TList(i).APKIHONCD := cS20W003T.APKIHONCD;                                -- ＡＰ基本コード
      wS20W003TList(i).SHOPCD := cS20W003T.SHOPCD;                                      -- ショップコード
      wS20W003TList(i).TNSHAAPKIHONCD := cS20W003T.TNSHAAPKIHONCD;                      -- 担当者ＡＰ基本コード
      wS20W003TList(i).TNSHASHOPCD := cS20W003T.TNSHASHOPCD;                            -- 担当者ショップコード
      wS20W003TList(i).TNSHACD := cS20W003T.TNSHACD;                                    -- 担当者コード
      wS20W003TList(i).PRCSID := c_PRCSID;                                              -- 処理ID
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
          S99_CHECK_LOG(userinfo_rec.PGMID, header_rec.APKIHONCD, header_rec.SHOPCD, '1', 'S', '1', 'XXX', 0, userinfo_rec.APKIHONCD,
            userinfo_rec.SHOPCD, userinfo_rec.TNSHACD, userinfo_rec.IPADRSE, userinfo_rec.PGMID , utl_lms.format_message('ポイントプログラムに不参加のショップです。ショップコード:[%s]', wS20W003TList(k).SHOPCD));
        ELSIF wS20W003TList(k).SYORIKEKKA = 3 THEN
          -- 得意先がポイントなし
          S99_CHECK_LOG(userinfo_rec.PGMID, header_rec.APKIHONCD, header_rec.SHOPCD, '1', 'S', '1', 'XXX', 0, userinfo_rec.APKIHONCD,
            userinfo_rec.SHOPCD, userinfo_rec.TNSHACD, userinfo_rec.IPADRSE, userinfo_rec.PGMID , utl_lms.format_message('ポイント管理を行わない得意先です。得意先コード:[%s]', wS20W003TList(k).TOKUISAKICD));
        ELSIF wS20W003TList(k).SYORIKEKKA = 4 THEN
          -- 商品がポイントなし
          S99_CHECK_LOG(userinfo_rec.PGMID, header_rec.APKIHONCD, header_rec.SHOPCD, '1', 'S', '1', 'XXX', 0, userinfo_rec.APKIHONCD,
            userinfo_rec.SHOPCD, userinfo_rec.TNSHACD, userinfo_rec.IPADRSE, userinfo_rec.PGMID , utl_lms.format_message('ポイントが発生しない商品です。商品コード:[%s]', wS20W003TList(k).SHNCD));
        ELSIF wS20W003TList(k).SYORIKEKKA = 5 THEN
          -- 得意先がマイアクア会員登録なし
          S99_CHECK_LOG(userinfo_rec.PGMID, header_rec.APKIHONCD, header_rec.SHOPCD, '1', 'S', '1', 'XXX', 0, userinfo_rec.APKIHONCD,
            userinfo_rec.SHOPCD, userinfo_rec.TNSHACD, userinfo_rec.IPADRSE, userinfo_rec.PGMID , utl_lms.format_message('マイアクア会員が未登録の得意先です。得意先コード:[%s]', wS20W003TList(k).TOKUISAKICD));
        ELSIF wS20W003TList(k).SYORIKEKKA = -1 THEN
          -- 取込画面側の入力CSVとマスタの不整合チェック以外の予期しないエラー発生時
          S99_CHECK_LOG(userinfo_rec.PGMID, header_rec.APKIHONCD, header_rec.SHOPCD, '1', 'S', '1', 'XXX', 0, userinfo_rec.APKIHONCD,
            userinfo_rec.SHOPCD, userinfo_rec.TNSHACD, userinfo_rec.IPADRSE, userinfo_rec.PGMID , 'ポイント情報部品(Z2401B01.PointinfbhnProc)の処理でエラーが発生しました。ログ(S99CHKLOG)を確認してください。');
          RAISE_APPLICATION_ERROR(-20719, 'ポイント情報部品(Z2401B01.PointinfbhnProc)の処理でエラーが発生しました。ログ(S99CHKLOG)を確認してください。');
        END IF;
      END LOOP;

      IF wYukoData = 1 THEN
        -- 有効なポイント情報をポイントテーブルに登録する
        wSaibanno := GetPointKanrino(header_rec);

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

  END registPointMeisaiTable;
  -- ポイント制度対応追加 end ABI

  /*****************************************************************************/
  /* プロシージャ名：受注配達明細データ作成処理                                     */
  /* 引数（ＩＮ）      ：header_rec      配達実績データヘッダ                      */
  /*                  ：iAccisdnpyno    ＡＣＣＩＳ伝票番号                              */
  /*                  ：iTaxkeisankbn   税計算区分                              */
  /* 引数（ＯＵＴ）     ：sum_rec         合計データ                              */
  /*****************************************************************************/
  PROCEDURE processJuchuMeisai( header_rec     IN S05W001T%rowtype -- 配達実績データヘッダ
                              , iAccisdnpyno   IN VARCHAR2 -- ＡＣＣＩＳ伝票番号
                              , iTaxkeisankbn  IN VARCHAR2 -- ヘッダ項目：税計算区分
                              , sum_rec       OUT sum_rec_type     -- 合計データ
  ) IS
  /* 変数宣言 */
  vRecordS04T002T      S04T002T%rowtype := NULL; -- 受注配達明細テーブル
  canceled             BOOLEAN := FALSE; -- キャンセル処理済フラグ
  -- 消費税計算用
  taxsetteikbn         VARCHAR2(1)  := NULL;  -- 税設定区分
  -- 本数、金額、サーバー合計用
  watercd              VARCHAR2(2)  := NULL;  -- 製品水大分類コード
  serverL              VARCHAR2(2)  := NULL;  -- サーバー大分類
  serverM              VARCHAR2(3)  := NULL;  -- サーバー中分類
  oyaKeijokbn          VARCHAR2(2)  := NULL;  -- 親計上区分
  addAllKeijokbn       VARCHAR2(2)  := NULL;  -- 親計上区分
  -- 明細番号採番用
  maxAcptdnpymeisaino  S04T002T.ACPTDNPYMEISAINO%type := 0; -- 明細番号最大値
  vUrikinhasukeisankbn VARCHAR2(1)  := NULL;  -- 売上金額端数計算区分

  -- ACCIS2023-7212 受注登録時点のセット商品展開行を退避させるためのリストを追加
  v_work_map t_s04_map;

  /* CURSOR：キャンセル配布物検索 */
  CURSOR SELECT_HAIFU IS
  SELECT JHMT.HAIFUBUTSUAPKIHONCD -- 配布物ＡＰ基本コード
       , JHMT.HAIFUBUTSUSHOPCD -- 配布物ショップコード
       , JHMT.HAIFUBUTSUCD -- 配布物コード
       , JHHT.TOKUISAKICD -- 得意先コード
    FROM S04T002T JHMT
   INNER JOIN S04T001T JHHT
           ON ( JHHT.APKIHONCD      = JHMT.APKIHONCD
                AND JHHT.SHOPCD     = JHMT.SHOPCD
                AND JHHT.ACPTDNPYNO = JHMT.ACPTDNPYNO
                AND JHHT.EDANO      = JHMT.EDANO
              )
   WHERE JHMT.APKIHONCD  = header_rec.APKIHONCD
     AND JHMT.SHOPCD     = header_rec.SHOPCD
     AND JHMT.ACPTDNPYNO = iAccisdnpyno
     AND JHMT.EDANO = header_rec.EDANO
     AND JHMT.YUKOKBN   = '1'
     AND JHMT.MEISAIKBN = '1'
  ; /* END CURSOR：キャンセル配布物検索 */

  /* CURSOR：配達実績情報検索（各明細行） */
  CURSOR SELECT_MEISAI IS
  SELECT DJTW.* -- 配達実績情報
       /* 明細区分 */
       , NVL( JHMT.MEISAIKBN, -- 既存商品／代引き
         ( CASE WHEN DJTW.SHNCD IS NOT NULL THEN '0' -- 商品追加行
                WHEN DJTW.HAIFUBUTSUCD IS NOT NULL THEN '1' -- 配布物
           END )
         ) MEISAIKBN_NEW
       /* 既存伝票情報 */
       , JHMT.ACPTDNPYMEISAINO ACPTDNPYMEISAINO_OLD -- 受注伝票明細番号
       , JHMT.SHNCD SHNCD_OLD -- 商品コード
       , JHMT.LCTGRYCD LCTGRYCD_OLD -- 大分類コード
       , JHMT.MCTGRYCD MCTGRYCD_OLD -- 中分類コード
       , JHMT.SCTGRYCD SCTGRYCD_OLD -- 小分類コード
       , JHMT.KEIJOKBN KEIJOKBN_OLD -- 計上区分
       , JHMT.KATASIKI KATASIKI_OLD -- 型式
       , JHMT.MEISAIBIKO MEISAIBIKO_OLD -- 明細備考
       , JHMT.ACPTSURYO ACPTSURYO_OLD -- 受注数量
       , JHMT.ACPTUNT ACPTUNT_OLD     -- 受注単価
       , JHMT.ACPTKIN ACPTKIN_OLD     -- 受注金額
       , JHMT.ACPTUNTSHOHITAXKIN ACPTUNTSHOHITAXKIN_OLD -- 受注単価消費税金額
       , JHMT.ACPTSHOHITAXKIN ACPTSHOHITAXKIN_OLD       -- 受注消費税金額
       , JHMT.HYOJUNUNT HYOJUNUNT_OLD -- 標準単価
       , JHMT.TAXKBN TAXKBN_OLD       -- 税区分
       , JHMT.ITAKUAITEAPKIHONCD ITAKUAITEAPKIHONCD_OLD   -- 委託相手ＡＰ基本コード
       , JHMT.ITAKUAITESHOPCD ITAKUAITESHOPCD_OLD         -- 委託相手ショップコード
       , JHMT.ITAKUAITEACPTDNPYNO ITAKUAITEACPTDNPYNO_OLD -- 委託相手受注伝票番号
       , JHMT.ITAKUAITEEDANO ITAKUAITEEDANO_OLD           -- 委託相手枝番号
       , JHMT.ITAKUAITEACPTDNPYMEISAINO ITAKUAITEACPTDNPYMEISAINO_OLD  -- 委託相手受注伝票明細番号
       , JHMT.ITAKUAITEACPTDNPYMEISAIEDANO AITEACPTDNPYMEISAIEDANO_OLD -- 委託相手受注伝票明細番号
       , JHMT.ITAKUCMPNYACPTUNT ITAKUCMPNYACPTUNT_OLD -- 委託会社受注単価
       , JHMT.ITAKUCMPNYACPTUNTSHOHITAXKIN AITEACPTUNTSHOHITAXKIN_OLD -- 委託会社受注単価消費税金額
       , JHMT.ITAKUCMPNYACPTSHOHITAXKIN ITAKUCMPNYACPTSHOHITAXKIN_OLD -- 委託会社受注消費税金額
       , JHMT.SOUKAISU SOUKAISU_OLD -- 総回数
       , JHMT.ZANKAISU ZANKAISU_OLD -- 残回数
       , JHMT.HAIFUBUTSUCD HAIFUBUTSUCD_OLD -- 既存伝票配布物コード
       -- データ作成元区分
       , ( CASE WHEN DJTW.HAIFUBUTSUCD IS NULL THEN JHMT.DATSKSMOTOKBN -- 商品／代引き
           ELSE ( SELECT MAX(HFB.DATSKSMOTOKBN) -- 配布物
                  FROM S04T002T HFB
                  WHERE HFB.APKIHONCD  = DJTW.APKIHONCD
                    AND HFB.SHOPCD     = DJTW.SHOPCD
                    AND HFB.ACPTDNPYNO = DJTW.ACPTDNPYNO
                    AND HFB.EDANO      = DJTW.EDANO
                    AND HFB.HAIFUBUTSUAPKIHONCD = DJTW.HAIFUBUTSUAPKIHONCD
                    AND HFB.HAIFUBUTSUSHOPCD = DJTW.HAIFUBUTSUSHOPCD
                    AND HFB.HAIFUBUTSUCD = DJTW.HAIFUBUTSUCD
                    AND HFB.YUKOKBN = '1'
                )
           END ) DATSKSMOTOKBN_OLD
       , JHMT.NYURYOKUMEISAIFLG NYURYOKUMEISAIFLG_OLD -- 入力明細フラグ
       , JHMT.TANPINMEISAIFLG TANPINMEISAIFLG_OLD     -- 単品明細フラグ
       , JHMT.STSHNCD STSHNCD_OLD                     -- セット商品コード
       /* 既存伝票情報：代引き更新用 */
       , JHMT.URIDNPYNO URIDNPYNO_OLD -- 売上伝票番号
       , JHMT.URIDNPYEDANO URIDNPYEDANO_OLD -- 売上伝票枝番号
       , JHMT.URIDNPYMEISAINO URIDNPYMEISAINO_OLD -- 売上伝票明細番号
       , JHMT.URIDNPYMEISAIEDANO URIDNPYMEISAIEDANO_OLD -- 売上伝票明細枝番号
    FROM ( SELECT * FROM S05W001T DJTW
           WHERE DJTW.APKIHONCD = header_rec.APKIHONCD
           AND DJTW.SHOPCD      = header_rec.SHOPCD
           AND DJTW.JISSEKIJOHOTORIKOMIKANRINO = header_rec.JISSEKIJOHOTORIKOMIKANRINO
           AND DJTW.ACPTDNPYNO = iAccisdnpyno
           AND DJTW.EDANO      = header_rec.EDANO
           AND DJTW.DATSKSFLG  = '0'
           AND DJTW.YUKOKBN    = '1'
           AND ( DJTW.ACPTDNPYMEISAIEDANO IS NULL OR DJTW.ACPTDNPYMEISAIEDANO = 0 )
           AND ( DJTW.NYURYOKUMEISAIFLG   IS NULL OR DJTW.NYURYOKUMEISAIFLG   = '1' )
           ) DJTW
    FULL OUTER JOIN ( SELECT * FROM S04T002T JHMT
           WHERE JHMT.APKIHONCD = header_rec.APKIHONCD
           AND JHMT.SHOPCD      = header_rec.SHOPCD
           AND JHMT.ACPTDNPYNO  = iAccisdnpyno
           AND JHMT.EDANO       = header_rec.EDANO
           AND JHMT.ACPTDNPYMEISAIEDANO = 0
           AND JHMT.NYURYOKUMEISAIFLG   = '1'
           AND JHMT.YUKOKBN    = '1'
           AND JHMT.MEISAIKBN IN ('0', '2')
           ) JHMT
    ON ( JHMT.APKIHONCD      = DJTW.APKIHONCD
         AND JHMT.SHOPCD     = DJTW.SHOPCD
         AND JHMT.ACPTDNPYNO = DJTW.ACPTDNPYNO
         AND JHMT.EDANO      = DJTW.EDANO
         AND JHMT.ACPTDNPYMEISAINO = DJTW.ACPTDNPYMEISAINO
         AND JHMT.ACPTDNPYMEISAIEDANO = DJTW.ACPTDNPYMEISAIEDANO
         AND JHMT.SHNCD = DJTW.SHNCD
         )
   ORDER BY -- 既存商品、追加商品、配布物、代引きでソート
         MEISAIKBN_NEW
       , JHMT.ACPTDNPYMEISAINO
       , DJTW.ACPTDNPYMEISAINO
  ; /* END CURSOR：配達実績情報検索（各明細行）*/
  -- ポイント制度対応追加 start ABI
  wGyoban            NUMBER(3) := 1;        -- 予定発生ポイントテーブルの行番号
  -- ポイント制度対応追加 end ABI
  BEGIN
    -- 配布物キャンセル処理
    FOR hfb_rec IN SELECT_HAIFU LOOP
        cancelHaifubutsuTKM(hfb_rec.HAIFUBUTSUAPKIHONCD, hfb_rec.HAIFUBUTSUSHOPCD, hfb_rec.HAIFUBUTSUCD, hfb_rec.TOKUISAKICD );
    END LOOP;

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

    watercd := getMojisetteiatai( '000', '000', '096', '1', NULL, NULL, '商品大分類コード：製品水' );
    serverL := getMojisetteiatai( '000', '000', '096', '4', NULL, NULL, '商品大分類コード：サーバー' );
    serverM := getMojisetteiatai( '000', '000', '134', '41', NULL, NULL, '商品中分類コード：サーバー本体' );

    -- ショップマスタ取得
    SELECT
      SHPM.URIKINHASUKEISANKBN  -- 売上金額端数計算区分
    INTO vUrikinhasukeisankbn
    FROM
      S02M046T SHPM
    WHERE
      SHPM.APKIHONCD = header_rec.APKIHONCD
      AND SHPM.SHOPCD = header_rec.SHOPCD
      AND SHPM.YUKOKBN = '1'
    ;

    -- 受託明細データを元に新しい明細データを作る
    FOR meisai_rec IN SELECT_MEISAI LOOP
-- インボイス対応追加 start ABI
        -- 一般消費税の場合、締め後一般消費税金額に設定
        IF meisai_rec.SHNCD = '099901' THEN
          cmnShimeippantaxkin := NVL(cmnShimeippantaxkin, 0) + meisai_rec.SHOHITAXKIN;
          CONTINUE;
        -- 軽減消費税の場合、締め後軽減消費税金額に設定
        ELSIF meisai_rec.SHNCD = '099902' THEN
          cmnShimekeigentaxkin := NVL(cmnShimekeigentaxkin, 0) + meisai_rec.SHOHITAXKIN;
          CONTINUE;
        END IF;
-- インボイス対応追加 end ABI
        -- キャンセル処理
        IF NOT canceled THEN
           -- ACCIS2024-7212 セット商品展開時に受注登録時点の単価を使用するため、削除前に展開行をリストに退避
           FOR rec IN (
             -- セット商品の展開行のみを受注配達明細テーブルから取得
             SELECT *
             FROM S04T002T
             WHERE APKIHONCD  = header_rec.APKIHONCD
               AND SHOPCD     = header_rec.SHOPCD
               AND ACPTDNPYNO = header_rec.ACPTDNPYNO
               AND EDANO      = header_rec.EDANO
               AND MEISAIKBN = '0'
               AND NYURYOKUMEISAIFLG = '0'
               AND YUKOKBN = '1'
           ) LOOP
              -- 受注配達明細テーブルの主キー項目（数値型は前0埋めして文字列化）を連結した値をキーとして取得データをリストに退避
              v_work_map(get_s04_key(rec)) := rec;
           END LOOP;
           DELETE FROM S04T002T JHMT
            WHERE JHMT.APKIHONCD  = header_rec.APKIHONCD
              AND JHMT.SHOPCD     = header_rec.SHOPCD
              AND JHMT.ACPTDNPYNO = header_rec.ACPTDNPYNO
              AND JHMT.EDANO      = header_rec.EDANO
           ;
           canceled := TRUE;
        END IF;

        -- 明細行初期化
        vRecordS04T002T := NULL;

        -- 受注配達明細テーブルに登録
        vRecordS04T002T.APKIHONCD  := header_rec.APKIHONCD;  -- ＡＰ基本コード
        vRecordS04T002T.SHOPCD     := header_rec.SHOPCD;     -- ショップコード
        vRecordS04T002T.ACPTDNPYNO := header_rec.ACPTDNPYNO; -- 受注伝票番号
        vRecordS04T002T.EDANO      := header_rec.EDANO;      -- 枝番号

        -- 受注伝票明細番号
        --     既存商品
        IF meisai_rec.MEISAIKBN_NEW = '0'
           AND meisai_rec.ACPTDNPYMEISAINO_OLD IS NOT NULL
           THEN
           vRecordS04T002T.ACPTDNPYMEISAINO := meisai_rec.ACPTDNPYMEISAINO_OLD;
        --     商品追加行、配布物、代引き
        ELSIF ( meisai_rec.MEISAIKBN_NEW = '0' AND meisai_rec.ACPTDNPYMEISAINO_OLD IS NULL )
           OR meisai_rec.MEISAIKBN_NEW IN ('1', '2')
           THEN
           vRecordS04T002T.ACPTDNPYMEISAINO := maxAcptdnpymeisaino + 1;
        END IF;
        maxAcptdnpymeisaino := vRecordS04T002T.ACPTDNPYMEISAINO;

        vRecordS04T002T.ACPTDNPYMEISAIEDANO := 0; -- 受注伝票明細枝番号
        vRecordS04T002T.SHNCD := NVL(meisai_rec.SHNCD_OLD, meisai_rec.SHNCD); -- 商品コード
        vRecordS04T002T.ITAKUAITEAPKIHONCD  := NVL(meisai_rec.ITAKUAITEAPKIHONCD_OLD, NULL);   -- 委託相手ＡＰ基本コード
        vRecordS04T002T.ITAKUAITESHOPCD     := NVL(meisai_rec.ITAKUAITESHOPCD_OLD, NULL);      -- 委託相手ショップコード
        vRecordS04T002T.ITAKUAITEACPTDNPYNO    := NVL(meisai_rec.ITAKUAITEACPTDNPYNO_OLD, NULL); -- 委託相手受注伝票番号
        vRecordS04T002T.ITAKUAITEEDANO      := NVL(meisai_rec.ITAKUAITEEDANO_OLD, NULL);       -- 委託相手枝番号
        vRecordS04T002T.ITAKUAITEACPTDNPYMEISAINO       := NVL(meisai_rec.ITAKUAITEACPTDNPYMEISAINO_OLD, NULL); -- 委託相手受注伝票明細番号
        vRecordS04T002T.ITAKUAITEACPTDNPYMEISAIEDANO := NVL(meisai_rec.AITEACPTDNPYMEISAIEDANO_OLD, NULL);    -- 委託相手受注伝票明細枝番号

        vRecordS04T002T.ACPTSURYO := NVL(meisai_rec.ACPTSURYO_OLD, 0); -- 受注数量
        vRecordS04T002T.ACPTUNT   := NVL(meisai_rec.ACPTUNT_OLD, 0);   -- 受注単価
        vRecordS04T002T.ACPTKIN   := NVL(meisai_rec.ACPTKIN_OLD, 0);   -- 受注金額
        vRecordS04T002T.ACPTUNTSHOHITAXKIN := NVL(meisai_rec.ACPTUNTSHOHITAXKIN_OLD, 0); -- 受注単価消費税金額
        vRecordS04T002T.ACPTSHOHITAXKIN    := NVL(meisai_rec.ACPTSHOHITAXKIN_OLD, 0);    -- 受注消費税金額
        vRecordS04T002T.ITAKUCMPNYACPTUNT            := NVL(meisai_rec.ITAKUCMPNYACPTUNT_OLD, 0);         -- 委託会社受注単価
        vRecordS04T002T.ITAKUCMPNYACPTUNTSHOHITAXKIN := NVL(meisai_rec.AITEACPTUNTSHOHITAXKIN_OLD, 0);    -- 委託会社受注単価消費税金額
        vRecordS04T002T.ITAKUCMPNYACPTSHOHITAXKIN    := NVL(meisai_rec.ITAKUCMPNYACPTSHOHITAXKIN_OLD, 0); -- 委託会社受注消費税金額
        vRecordS04T002T.SOUKAISU := NVL(meisai_rec.SOUKAISU_OLD, 0); -- 総回数
        vRecordS04T002T.ZANKAISU := NVL(meisai_rec.ZANKAISU_OLD, 0); -- 残回数

        vRecordS04T002T.MEISAIKBN := meisai_rec.MEISAIKBN_NEW; -- 明細区分

        -- 代引き以外の場合
        IF vRecordS04T002T.MEISAIKBN <> '2' THEN
           vRecordS04T002T.HAIFUBUTSUAPKIHONCD := meisai_rec.HAIFUBUTSUAPKIHONCD; -- 配布物ＡＰ基本コード
           vRecordS04T002T.HAIFUBUTSUSHOPCD    := meisai_rec.HAIFUBUTSUSHOPCD;    -- 配布物ショップコード
           vRecordS04T002T.HAIFUBUTSUCD        := meisai_rec.HAIFUBUTSUCD;        -- 配布物コード
           vRecordS04T002T.KATASIKI := NVL(meisai_rec.KATASIKI, meisai_rec.KATASIKI_OLD); -- 型式
           vRecordS04T002T.KEIJOKBN  := NVL(meisai_rec.KEIJOKBN, meisai_rec.KEIJOKBN_OLD); -- 計上区分

           vRecordS04T002T.NOHINSURYO := NVL(meisai_rec.NOHINSURYO, 0); -- 納品数量

           -- ACCIS2023-7212 計上区分が売上またはキャンペーンでセット商品（入力行）の場合、納品単価をマスタの内容で再設定
           -- meisai_recで納品単価をS05W001Tの金額から再設定したいのは既存明細のセット商品のみ
           IF vRecordS04T002T.KEIJOKBN IN ('01', '02') THEN
             IF meisai_rec.TANPINMEISAIFLG_OLD = '0'
               AND vRecordS04T002T.MEISAIKBN = '0' THEN
               -- 納品単価を再設定（納品金額計算時に使用しているmeisai_recの方に再設定）
               meisai_rec.NOHINUNT := getUntOfSetItem(header_rec, vRecordS04T002T);
             END IF;
           END IF;

           vRecordS04T002T.NOHINUNT   := NVL(meisai_rec.NOHINUNT, 0);   -- 納品単価
-- 本線対応変更 start ABI
--           vRecordS04T002T.NOHINKIN   := S01SyohizeiService.roundValue(NVL(meisai_rec.NOHINSURYO, 0) * NVL(meisai_rec.NOHINUNT, 0), vUrikinhasukeisankbn); -- 納品金額
           vRecordS04T002T.NOHINKIN   := Z1808B01.roundValue(NVL(meisai_rec.NOHINSURYO, 0) * NVL(meisai_rec.NOHINUNT, 0), vUrikinhasukeisankbn); -- 納品金額
-- 本線対応変更 end ABI

           vRecordS04T002T.NYURYOKUMEISAIFLG := '1'; -- 入力明細フラグ
           vRecordS04T002T.MEISAIBIKO        := meisai_rec.MEISAIBIKO; -- 明細備考
        -- 代引きの場合
        ELSIF vRecordS04T002T.MEISAIKBN = '2' THEN
           vRecordS04T002T.LCTGRYCD    := meisai_rec.LCTGRYCD_OLD; -- 大分類コード
           vRecordS04T002T.MCTGRYCD    := meisai_rec.MCTGRYCD_OLD; -- 中分類コード
           vRecordS04T002T.SCTGRYCD    := meisai_rec.SCTGRYCD_OLD; -- 小分類コード

           vRecordS04T002T.KATASIKI := meisai_rec.KATASIKI_OLD; -- 型式
           vRecordS04T002T.KEIJOKBN  := meisai_rec.KEIJOKBN_OLD; -- 計上区分

           vRecordS04T002T.HYOJUNUNT := meisai_rec.HYOJUNUNT_OLD; -- 標準単価
           vRecordS04T002T.TAXKBN := meisai_rec.TAXKBN_OLD; -- 税区分
           vRecordS04T002T.NOHINSURYO := NVL(meisai_rec.ACPTSURYO_OLD, 0); -- 納品数量
           vRecordS04T002T.NOHINUNT   := NVL(meisai_rec.ACPTUNT_OLD, 0);   -- 納品単価
           vRecordS04T002T.NOHINKIN   := NVL(meisai_rec.ACPTKIN_OLD, 0);   -- 納品金額
           vRecordS04T002T.NOHINUNTSHOHITAXKIN := NVL(meisai_rec.ACPTUNTSHOHITAXKIN_OLD, 0); -- 納品単価消費税金額
           vRecordS04T002T.NOHINSHOHITAXKIN := NVL(meisai_rec.ACPTSHOHITAXKIN_OLD, 0); -- 納品消費税金額

           vRecordS04T002T.NYURYOKUMEISAIFLG := meisai_rec.NYURYOKUMEISAIFLG_OLD; -- 入力明細フラグ
           vRecordS04T002T.TANPINMEISAIFLG := meisai_rec.TANPINMEISAIFLG_OLD; -- 単品明細フラグ
           vRecordS04T002T.STSHNCD := meisai_rec.STSHNCD_OLD; -- セット商品コード
           vRecordS04T002T.MEISAIBIKO := meisai_rec.MEISAIBIKO_OLD; -- 明細備考
        END IF;

        -- 有償区分
        vRecordS04T002T.YUSYOKBN := ( CASE WHEN vRecordS04T002T.HAIFUBUTSUCD IS NOT NULL THEN '0' -- 配布物の場合：0：設定なし(配布物など)
                                           WHEN vRecordS04T002T.SHNCD        IS NOT NULL          -- 商品／代引きの場合：
                                           THEN ( CASE WHEN vRecordS04T002T.KEIJOKBN = '08' THEN '9'          -- 計上区分が経費：9:経費
                                                       WHEN vRecordS04T002T.KEIJOKBN IN ('10', '09') THEN '3' -- 計上区分がレンタル：3:レンタル
                                                       WHEN vRecordS04T002T.KEIJOKBN IN ('01', '02') THEN '1' -- 計上区分が売上／キャンペーン：1：有償
                                                       ELSE '2' -- その他計上区分：2：無償
                                                  END )
                                      END );
        vRecordS04T002T.DATSKSMOTOKBN := NVL(meisai_rec.DATSKSMOTOKBN_OLD, DATSKSMOTOKBN_NEW); -- データ作成元区分
        vRecordS04T002T.URIDNPYNO := meisai_rec.URIDNPYNO_OLD; -- 売上伝票番号
        vRecordS04T002T.URIDNPYEDANO := meisai_rec.URIDNPYEDANO_OLD; -- 売上伝票枝番号
        vRecordS04T002T.URIDNPYMEISAINO    := meisai_rec.URIDNPYMEISAINO_OLD; -- 売上伝票明細番号
        vRecordS04T002T.URIDNPYMEISAIEDANO := meisai_rec.URIDNPYMEISAIEDANO_OLD; -- 売上伝票明細枝番号
        vRecordS04T002T.YUKOKBN       := '1';           -- 有効区分
        vRecordS04T002T.TOSHAAPKIHONCD := userinfo_rec.APKIHONCD;  -- 登録者ＡＰ基本コード
        vRecordS04T002T.TOSHASHOPCD    := userinfo_rec.SHOPCD;     -- 登録者ショップコード
        vRecordS04T002T.TOSHACD        := userinfo_rec.TNSHACD;    -- 登録者コード
        vRecordS04T002T.INPTSYSTMYMD   := userinfo_rec.SYSTMYMD;   -- 登録システム日付
        vRecordS04T002T.INPTSYSTMJIKAN := userinfo_rec.SYSTMJIKAN; -- 登録システム時間
        vRecordS04T002T.INPTUNYOYMD    := userinfo_rec.UNYOYMD;    -- 登録運用日付
        vRecordS04T002T.INPTIPADRSE    := userinfo_rec.IPADRSE;    -- 登録ＩＰアドレス
        vRecordS04T002T.INPTPGMID      := userinfo_rec.PGMID;      -- 登録プログラムＩＤ
        vRecordS04T002T.KOSHAAPKIHONCD := userinfo_rec.APKIHONCD;  -- 更新者ＡＰ基本コード
        vRecordS04T002T.KOSHASHOPCD    := userinfo_rec.SHOPCD;     -- 更新者ショップコード
        vRecordS04T002T.KOSHACD        := userinfo_rec.TNSHACD;    -- 更新者コード
        vRecordS04T002T.KSHNSYSTMYMD   := userinfo_rec.SYSTMYMD;   -- 更新システム日付
        vRecordS04T002T.KSHNSYSTMJIKAN := userinfo_rec.SYSTMJIKAN; -- 更新システム時間
        vRecordS04T002T.KSHNUNYOYMD    := userinfo_rec.UNYOYMD;    -- 更新運用日付
        vRecordS04T002T.KSHNIPADRSE    := userinfo_rec.IPADRSE;    -- 更新ＩＰアドレス
        vRecordS04T002T.KSHNPGMID      := userinfo_rec.PGMID;      -- 更新プログラムＩＤ

        -- 商品の場合
        IF vRecordS04T002T.SHNCD IS NOT NULL
           AND vRecordS04T002T.MEISAIKBN = '0' THEN
           -- 商品情報の取得
           SELECT SHMV.LCTGRYCD -- 大分類コード
                , SHMV.MCTGRYCD -- 中分類コード
                , SHMV.SCTGRYCD -- 小分類コード
                -- 単品明細フラグ
                , ( CASE WHEN SHMV.STKBN = '1' THEN '0'
                         ELSE '1'
                    END ) TANPINMEISAIFLG
                -- セット商品コード
                , ( CASE WHEN SHMV.STKBN = '1' THEN SHMV.SHNCD
                         ELSE NULL
                    END ) STSHNCD
                -- 標準価格：配達区分、受注日（配達実績日）より設定
                , ( CASE WHEN SHMV.HYOJUNPRICEKIRIKAEDD IS NULL OR header_rec.DLVRYJISSEKIDD >= SHMV.HYOJUNPRICEKIRIKAEDD
                         THEN ( CASE header_rec.DLVRYKBN
                                WHEN '3' THEN SHMV.SHINMOCHIKAERIPRICE -- 新持ち帰り価格
                                         ELSE SHMV.SHINHYOJUNPRICE     -- 新標準価格
                                END )
                         ELSE ( CASE header_rec.DLVRYKBN
                                WHEN '3' THEN SHMV.GENZMOCHIKAERIPRICE -- 現持ち帰り価格
                                         ELSE SHMV.GENZHYOJUNPRICE     -- 現標準価格
                                END )
                    END ) HYOJUNUNT
                , CASE WHEN SHMV.TAXKBN IN ('4','5') AND header_rec.DLVRYJISSEKIDD <= '20190930' THEN '1' ELSE SHMV.TAXKBN END TAXKBN
                , SHMV.TAXSETTEIKBN -- 税設定区分
             INTO vRecordS04T002T.LCTGRYCD -- 大分類コード
                , vRecordS04T002T.MCTGRYCD -- 中分類コード
                , vRecordS04T002T.SCTGRYCD -- 小分類コード
                , vRecordS04T002T.TANPINMEISAIFLG -- 単品明細フラグ
                , vRecordS04T002T.STSHNCD         -- セット商品コード
                , vRecordS04T002T.HYOJUNUNT -- 標準価格
                , vRecordS04T002T.TAXKBN -- 税区分
                , taxsetteikbn           -- 税設定区分
             FROM S02V001T SHMV
            WHERE SHMV.APKIHONCD = vRecordS04T002T.APKIHONCD
              AND SHMV.SHOPCD    = vRecordS04T002T.SHOPCD
              AND SHMV.SHNCD     = vRecordS04T002T.SHNCD
              AND SHMV.SHOPYUKOKBN = '1'
           ;

           -- 納品消費税
           setTax( vRecordS04T002T -- 明細行
                 , vRecordS04T002T.NOHINSURYO -- 数量
                 , vRecordS04T002T.NOHINUNT   -- 単価
                 , vRecordS04T002T.APKIHONCD
                 , vRecordS04T002T.SHOPCD
                 , vRecordS04T002T.TAXKBN    -- 税区分
                 , taxsetteikbn  -- 税設定区分
                 , iTaxkeisankbn -- 税計算区分
                 , header_rec.DLVRYJISSEKIDD -- 切替日
                 , header_rec.DLVRYKBN --配達区分
-- インボイス対応追加 start ABI
                 , '0'
                 , '1'
-- インボイス対応追加 end ABI
                 , vRecordS04T002T.NOHINUNTSHOHITAXKIN -- 単価消費税
                 , vRecordS04T002T.NOHINSHOHITAXKIN    -- 消費税
                 );
        END IF;

        -- 配布物の場合
        IF vRecordS04T002T.HAIFUBUTSUCD IS NOT NULL
           AND vRecordS04T002T.MEISAIKBN = '1' THEN
           vRecordS04T002T.KEIJOKBN  := NULL; -- 計上区分
           vRecordS04T002T.ACPTSURYO := 1;    -- 受注数量
           vRecordS04T002T.HYOJUNUNT := NULL; -- 標準単価
           vRecordS04T002T.ACPTUNT   := NULL; -- 受注単価
           vRecordS04T002T.ACPTKIN   := NULL; -- 受注金額
           vRecordS04T002T.ACPTUNTSHOHITAXKIN := NULL; -- 受注単価消費税金額
           vRecordS04T002T.ACPTSHOHITAXKIN    := NULL; -- 受注消費税金額
           vRecordS04T002T.NOHINSURYO := 1; -- 納品数量
           vRecordS04T002T.NOHINUNT   := 0; -- 納品単価
           vRecordS04T002T.NOHINKIN   := 0; -- 納品金額
           vRecordS04T002T.NOHINUNTSHOHITAXKIN := 0; -- 納品単価消費税金額
           vRecordS04T002T.NOHINSHOHITAXKIN    := 0; -- 納品消費税金額
           vRecordS04T002T.TANPINMEISAIFLG   := '1';   -- 単品明細フラグ
        END IF;

        INSERT INTO S04T002T JHMT VALUES vRecordS04T002T;

        -- ポイント制度対応追加 start ABI
        -- 引数ワーク(S20W002T)登録
        INSERT INTO S20W002T VALUES (
            header_rec.APKIHONCD                 -- ＡＰ基本コード
          , header_rec.SHOPCD                    -- ショップコード
          , userinfo_rec.APKIHONCD               -- 担当者ＡＰ基本コード
          , userinfo_rec.SHOPCD                  -- 担当者ショップコード
          , userinfo_rec.TNSHACD                 -- 担当者コード
          , c_PRCSID                             -- 処理ID
          , userinfo_rec.IPADRSE                 -- ＩＰアドレス
          , wGyoban                              -- 行番号
          , vRecordS04T002T.ACPTDNPYMEISAINO     -- 伝票明細番号
          , vRecordS04T002T.ACPTDNPYMEISAIEDANO  -- 伝票明細枝番号
          , vRecordS04T002T.SHNCD                -- 商品コード
          , vRecordS04T002T.KEIJOKBN             -- 計上区分
          , vRecordS04T002T.NOHINUNT             -- 商品単価
          , null                                 -- 画面からの発生
          , vRecordS04T002T.NOHINSURYO           -- 数量
          , vRecordS04T002T.MEISAIBIKO           -- 明細備考
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
        -- ポイント制度対応追加 end ABI

        -- セット商品展開
        IF vRecordS04T002T.TANPINMEISAIFLG = '0'
           AND vRecordS04T002T.MEISAIKBN = '0' THEN
           -- ACCIS2023-7212 引数に退避した受注配達明細リスト（展開行）を追加
           processExpandMeisai(vRecordS04T002T, header_rec, iTaxkeisankbn, v_work_map);
        END IF;
    END LOOP;

    -- ポイント制度対応追加 start ABI
    registPointMeisaiTable(header_rec
      , vRecordS04T002T);
    -- ポイント制度対応追加 end ABI

    -- 本数、金額、サーバー合計
    FOR meisai_new IN ( SELECT JHMT.*
                          FROM S04T002T JHMT
                         WHERE JHMT.APKIHONCD  = header_rec.APKIHONCD
                           AND JHMT.SHOPCD     = header_rec.SHOPCD
                           AND JHMT.ACPTDNPYNO = header_rec.ACPTDNPYNO
                           AND JHMT.EDANO      = header_rec.EDANO
                           AND JHMT.MEISAIKBN <> '2' -- 代引き以外
                           AND JHMT.YUKOKBN = '1'
                         ORDER BY JHMT.ACPTDNPYMEISAINO
                                , JHMT.ACPTDNPYMEISAIEDANO
                      ) LOOP

        IF meisai_new.NYURYOKUMEISAIFLG = '1' THEN
           oyaKeijokbn := meisai_new.KEIJOKBN;
        END IF;
        IF oyaKeijoKbn IN ('01', '02') THEN
            -- 親が有償の場合子の計上区分を使用
            addAllKeijokbn := meisai_new.KEIJOKBN;
        ELSE
            -- 親が有償の場合親の計上区分を使用
            addAllKeijokbn := oyaKeijokbn;
        END IF;

        addAll( meisai_new  -- 合計対象
              , addAllKeijokbn -- 計上区分
              , watercd -- 製品水大分類
              , serverL -- サーバー大分類
              , serverM -- サーバー本体中分類
              , sum_rec -- 合計データ
        );

    END LOOP;

    EXCEPTION
      WHEN OTHERS THEN
        IF SELECT_HAIFU%ISOPEN THEN
           CLOSE SELECT_HAIFU;
        END IF;
        IF SELECT_MEISAI%ISOPEN THEN
           CLOSE SELECT_MEISAI;
        END IF;
        RAISE_APPLICATION_ERROR(-20016, SQLERRM || '受注配達明細作成時エラーが発生しました。');
  END processJuchuMeisai;

-- 本線対応追加 start ABI
  /*****************************************************************************/
  /* プロシージャ名：受注配達明細データ作成WEB処理                             */
  /* 引数（ＩＮ）     ：header_rec      配達実績データヘッダ                   */
  /*                  ：iAccisdnpyno    ＡＣＣＩＳ伝票番号                     */
  /*                  ：iTaxkeisankbn   税計算区分                             */
  /* 引数（ＯＵＴ）   ：sum_rec         合計データ                             */
  /*****************************************************************************/
  PROCEDURE processJuchuMeisaiWeb( header_rec  IN S05W001T%rowtype -- 配達実績データヘッダ
                              , iAccisdnpyno   IN VARCHAR2         -- ＡＣＣＩＳ伝票番号
                              , iTaxkeisankbn  IN VARCHAR2         -- ヘッダ項目：税計算区分
                              , sum_rec       OUT sum_rec_type     -- 合計データ
  ) IS
  /* 変数宣言 */
  vRecordS04T002T      S04T002T%rowtype := NULL; -- 受注配達明細テーブル
  canceled             BOOLEAN := FALSE; -- キャンセル処理済フラグ
  -- 消費税計算用
  taxsetteikbn         VARCHAR2(1)  := NULL;  -- 税設定区分
  -- 本数、金額、サーバー合計用
  watercd              VARCHAR2(2)  := NULL;  -- 製品水大分類コード
  serverL              VARCHAR2(2)  := NULL;  -- サーバー大分類
  serverM              VARCHAR2(3)  := NULL;  -- サーバー中分類
  oyaKeijokbn          VARCHAR2(2)  := NULL;  -- 親計上区分
  addAllKeijokbn       VARCHAR2(2)  := NULL;  -- 親計上区分
  -- 明細番号採番用
  maxAcptdnpymeisaino  S04T002T.ACPTDNPYMEISAINO%type := 0; -- 明細番号最大値
  vUrikinhasukeisankbn VARCHAR2(1)  := NULL;  -- 売上金額端数計算区分

  -- ACCIS2023-7212 受注登録時点のセット商品展開行を退避させるためのリストを追加
  v_work_map t_s04_map;

  /* CURSOR：キャンセル配布物検索 */
  CURSOR SELECT_HAIFU IS
  SELECT JHMT.HAIFUBUTSUAPKIHONCD -- 配布物ＡＰ基本コード
       , JHMT.HAIFUBUTSUSHOPCD    -- 配布物ショップコード
       , JHMT.HAIFUBUTSUCD        -- 配布物コード
       , JHHT.TOKUISAKICD         -- 得意先コード
    FROM S04T002T JHMT
    LEFT JOIN S04T001T JHHT
           ON ( JHHT.APKIHONCD      = JHMT.APKIHONCD
                AND JHHT.SHOPCD     = JHMT.SHOPCD
                AND JHHT.ACPTDNPYNO = JHMT.ACPTDNPYNO
                AND JHHT.EDANO      = JHMT.EDANO
              )
   WHERE JHMT.APKIHONCD  = header_rec.APKIHONCD
     AND JHMT.SHOPCD     = header_rec.SHOPCD
     AND JHMT.ACPTDNPYNO = iAccisdnpyno
     AND JHMT.EDANO      = header_rec.EDANO
     AND JHMT.YUKOKBN    = '1'
     AND JHMT.MEISAIKBN  = '1'
  ; /* END CURSOR：キャンセル配布物検索 */

  /* CURSOR：配達実績情報検索（各明細行） */
  CURSOR SELECT_MEISAI IS
  SELECT DJTW.* -- 配達実績情報
       /* 明細区分 */
       , NVL( JHMT.MEISAIKBN, -- 既存商品／代引き
         ( CASE WHEN DJTW.SHNCD IS NOT NULL THEN '0' -- 商品追加行
                WHEN DJTW.HAIFUBUTSUCD IS NOT NULL THEN '1' -- 配布物
           END )
         ) MEISAIKBN_NEW
       /* 既存伝票情報 */
       , JHMT.ACPTDNPYMEISAINO ACPTDNPYMEISAINO_OLD -- 受注伝票明細番号
       , JHMT.SHNCD SHNCD_OLD -- 商品コード
       , JHMT.LCTGRYCD LCTGRYCD_OLD -- 大分類コード
       , JHMT.MCTGRYCD MCTGRYCD_OLD -- 中分類コード
       , JHMT.SCTGRYCD SCTGRYCD_OLD -- 小分類コード
       , JHMT.KEIJOKBN KEIJOKBN_OLD -- 計上区分
       , JHMT.KATASIKI KATASIKI_OLD -- 型式
       , JHMT.MEISAIBIKO MEISAIBIKO_OLD -- 明細備考
       , JHMT.ACPTSURYO ACPTSURYO_OLD -- 受注数量
       , JHMT.ACPTUNT ACPTUNT_OLD     -- 受注単価
       , JHMT.ACPTKIN ACPTKIN_OLD     -- 受注金額
       , JHMT.ACPTUNTSHOHITAXKIN ACPTUNTSHOHITAXKIN_OLD -- 受注単価消費税金額
       , JHMT.ACPTSHOHITAXKIN ACPTSHOHITAXKIN_OLD       -- 受注消費税金額
-- ACCIS2023-6518 start ABI 更新前受注配達明細テーブルの納品関連項目を追加
       , JHMT.NOHINSURYO NOHINSURYO_OLD -- 納品数量
       , JHMT.NOHINUNT NOHINUNT_OLD     -- 納品単価
       , JHMT.NOHINKIN NOHINKIN_OLD     -- 納品金額
       , JHMT.NOHINUNTSHOHITAXKIN NOHINUNTSHOHITAXKIN_OLD -- 納品単価消費税金額
       , JHMT.NOHINSHOHITAXKIN NOHINSHOHITAXKIN_OLD       -- 納品消費税金額
-- ACCIS2023-6518 end ABI
       , JHMT.HYOJUNUNT HYOJUNUNT_OLD -- 標準単価
       , JHMT.TAXKBN TAXKBN_OLD       -- 税区分
       , JHMT.ITAKUAITEAPKIHONCD ITAKUAITEAPKIHONCD_OLD   -- 委託相手ＡＰ基本コード
       , JHMT.ITAKUAITESHOPCD ITAKUAITESHOPCD_OLD         -- 委託相手ショップコード
       , JHMT.ITAKUAITEACPTDNPYNO ITAKUAITEACPTDNPYNO_OLD -- 委託相手受注伝票番号
       , JHMT.ITAKUAITEEDANO ITAKUAITEEDANO_OLD           -- 委託相手枝番号
       , JHMT.ITAKUAITEACPTDNPYMEISAINO ITAKUAITEACPTDNPYMEISAINO_OLD  -- 委託相手受注伝票明細番号
       , JHMT.ITAKUAITEACPTDNPYMEISAIEDANO AITEACPTDNPYMEISAIEDANO_OLD -- 委託相手受注伝票明細番号
       , JHMT.ITAKUCMPNYACPTUNT ITAKUCMPNYACPTUNT_OLD -- 委託会社受注単価
       , JHMT.ITAKUCMPNYACPTUNTSHOHITAXKIN AITEACPTUNTSHOHITAXKIN_OLD -- 委託会社受注単価消費税金額
       , JHMT.ITAKUCMPNYACPTSHOHITAXKIN ITAKUCMPNYACPTSHOHITAXKIN_OLD -- 委託会社受注消費税金額
       , JHMT.SOUKAISU SOUKAISU_OLD -- 総回数
       , JHMT.ZANKAISU ZANKAISU_OLD -- 残回数
       , JHMT.HAIFUBUTSUCD HAIFUBUTSUCD_OLD -- 既存伝票配布物コード
       -- データ作成元区分
       , ( CASE WHEN DJTW.HAIFUBUTSUCD IS NULL THEN JHMT.DATSKSMOTOKBN -- 商品／代引き
           ELSE ( SELECT MAX(HFB.DATSKSMOTOKBN) -- 配布物
                  FROM S04T002T HFB
                  WHERE HFB.APKIHONCD  = DJTW.APKIHONCD
                    AND HFB.SHOPCD     = DJTW.SHOPCD
                    AND HFB.ACPTDNPYNO = DJTW.ACPTDNPYNO
                    AND HFB.EDANO      = DJTW.EDANO
                    AND HFB.HAIFUBUTSUAPKIHONCD = DJTW.HAIFUBUTSUAPKIHONCD
                    AND HFB.HAIFUBUTSUSHOPCD = DJTW.HAIFUBUTSUSHOPCD
                    AND HFB.HAIFUBUTSUCD = DJTW.HAIFUBUTSUCD
                    AND HFB.YUKOKBN = '1'
                )
           END ) DATSKSMOTOKBN_OLD
       , JHMT.NYURYOKUMEISAIFLG NYURYOKUMEISAIFLG_OLD -- 入力明細フラグ
       , JHMT.TANPINMEISAIFLG TANPINMEISAIFLG_OLD     -- 単品明細フラグ
       , JHMT.STSHNCD STSHNCD_OLD                     -- セット商品コード
       /* 既存伝票情報：代引き更新用 */
       , JHMT.URIDNPYNO URIDNPYNO_OLD -- 売上伝票番号
       , JHMT.URIDNPYEDANO URIDNPYEDANO_OLD -- 売上伝票枝番号
       , JHMT.URIDNPYMEISAINO URIDNPYMEISAINO_OLD -- 売上伝票明細番号
       , JHMT.URIDNPYMEISAIEDANO URIDNPYMEISAIEDANO_OLD -- 売上伝票明細枝番号
       , JHMT.APKIHONCD APKIHONCD_OLD -- AP基本コード

    FROM ( SELECT * FROM S05W001T DJTW
           WHERE DJTW.APKIHONCD = header_rec.APKIHONCD
           AND DJTW.SHOPCD      = header_rec.SHOPCD
           AND DJTW.JISSEKIJOHOTORIKOMIKANRINO = header_rec.JISSEKIJOHOTORIKOMIKANRINO
           AND DJTW.ACPTDNPYNO = iAccisdnpyno
           AND DJTW.EDANO      = header_rec.EDANO
           AND DJTW.DATSKSFLG  = '0'
           AND DJTW.YUKOKBN    = '1'
           AND ( DJTW.ACPTDNPYMEISAIEDANO IS NULL OR DJTW.ACPTDNPYMEISAIEDANO = 0 )
           AND ( DJTW.NYURYOKUMEISAIFLG   IS NULL OR DJTW.NYURYOKUMEISAIFLG   = '1' )
           ) DJTW
    FULL OUTER JOIN ( SELECT * FROM S04T002T JHMT
           WHERE JHMT.APKIHONCD = header_rec.APKIHONCD
           AND JHMT.SHOPCD      = header_rec.SHOPCD
           AND JHMT.ACPTDNPYNO  = iAccisdnpyno
           AND JHMT.EDANO       = header_rec.EDANO
           AND JHMT.ACPTDNPYMEISAIEDANO = 0
           AND JHMT.NYURYOKUMEISAIFLG   = '1'
           AND JHMT.YUKOKBN    = '1'
           AND JHMT.MEISAIKBN IN ('0', '2')
           ) JHMT
    ON ( JHMT.APKIHONCD      = DJTW.APKIHONCD
         AND JHMT.SHOPCD     = DJTW.SHOPCD
         AND JHMT.ACPTDNPYNO = DJTW.ACPTDNPYNO
         AND JHMT.EDANO      = DJTW.EDANO
         AND JHMT.ACPTDNPYMEISAINO = DJTW.ACPTDNPYMEISAINO
         AND JHMT.ACPTDNPYMEISAIEDANO = DJTW.ACPTDNPYMEISAIEDANO
         AND JHMT.SHNCD = DJTW.SHNCD
         )
   ORDER BY -- 既存商品、追加商品、配布物、代引きでソート
         MEISAIKBN_NEW
       , JHMT.ACPTDNPYMEISAINO
       , DJTW.ACPTDNPYMEISAINO
  ; /* END CURSOR：配達実績情報検索（各明細行）*/
  -- ポイント制度対応追加 start ABI
  wGyoban            NUMBER(3) := 0;        -- 予定発生ポイントリストのインデックス
  j                  PLS_INTEGER := 0;      -- 予定発生ポイントレコードカウンタ
  iAkadenRegist      NUMBER(1) := 0;        -- 赤伝用ポイントレコード登録するか(0:しない 1:する)
  iKurodenRegist     NUMBER(1) := 0;        -- 黒伝用ポイントレコード登録するか(0:しない 1:する)
  TYPE               S20W002TList IS TABLE OF S20W002T%ROWTYPE INDEX BY PLS_INTEGER;
  wKuroS20W002TList  S20W002TList;          -- 黒伝用の処理ワークレコード保持用
  -- 赤伝発行対象となる売上伝票番号を受注配達テーブルから取得する
  CURSOR SELECT_ACPTD IS
  SELECT JHTT.*
    FROM S04T001T JHTT
  WHERE JHTT.APKIHONCD  = header_rec.APKIHONCD
    AND JHTT.SHOPCD     = header_rec.SHOPCD
    AND JHTT.ACPTDNPYNO = header_rec.ACPTDNPYNO
    AND JHTT.EDANO      = header_rec.EDANO
    AND JHTT.YUKOKBN    = '1';
  -- 赤伝対象の受注配達テーブルレコード
  vRecordS04T001T       S04T001T%rowtype := NULL; -- 受注配達テーブル
  -- 赤伝登録判定用
  CURSOR SELECT_ACPTD_AKADEN IS
  SELECT JHTT.*
    , DKTM.KYOTENSOKOCD
    , DTNM.TNSHASHARYOSOKOCD    -- 担当者車両倉庫コード
    , SHPM.ZAIKOKANRIKBN        -- 在庫管理区分
  FROM S04T001T JHTT
  LEFT OUTER JOIN S02M031T DKTM -- 配達拠点マスタ
    ON ( DKTM.APKIHONCD = JHTT.DLVRYKANKATSUAPKIHONCD
      AND DKTM.SHOPCD   = JHTT.DLVRYKANKATSUSHOPCD
      AND DKTM.DLVRYKYOTENCD = JHTT.DLVRYKYOTENCD
    )
  LEFT OUTER JOIN S02M003T DTNM -- 担当者マスタ（配達担当者）
    ON ( DTNM.APKIHONCD = JHTT.APKIHONCD
      AND DTNM.SHOPCD   = JHTT.SHOPCD
      AND DTNM.TNSHACD  = JHTT.DLVRYTNSHACD
    )
  LEFT OUTER JOIN S02M046T SHPM -- ショップマスタ
    ON ( SHPM.APKIHONCD = JHTT.APKIHONCD
      AND SHPM.SHOPCD   = JHTT.SHOPCD
    )
  WHERE JHTT.APKIHONCD  = header_rec.APKIHONCD
    AND JHTT.SHOPCD     = header_rec.SHOPCD
    AND JHTT.ACPTDNPYNO = header_rec.ACPTDNPYNO
    AND JHTT.EDANO      = header_rec.EDANO
    AND JHTT.YUKOKBN    = '1';
  -- ポイント制度対応追加 end ABI
  BEGIN
    -- 配布物キャンセル処理
    FOR hfb_rec IN SELECT_HAIFU LOOP
        cancelHaifubutsuTKM(hfb_rec.HAIFUBUTSUAPKIHONCD, hfb_rec.HAIFUBUTSUSHOPCD, hfb_rec.HAIFUBUTSUCD, hfb_rec.TOKUISAKICD );
    END LOOP;

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

    watercd := getMojisetteiatai( '000', '000', '096', '1', NULL, NULL, '商品大分類コード：製品水' );
    serverL := getMojisetteiatai( '000', '000', '096', '4', NULL, NULL, '商品大分類コード：サーバー' );
    serverM := getMojisetteiatai( '000', '000', '134', '41', NULL, NULL, '商品中分類コード：サーバー本体' );

    -- ショップマスタ取得
    SELECT
      SHPM.URIKINHASUKEISANKBN  -- 売上金額端数計算区分
    INTO vUrikinhasukeisankbn
    FROM
      S02M046T SHPM
    WHERE
      SHPM.APKIHONCD = header_rec.APKIHONCD
      AND SHPM.SHOPCD = header_rec.SHOPCD
      AND SHPM.YUKOKBN = '1'
    ;

    -- 受託明細データを元に新しい明細データを作る
    FOR meisai_rec IN SELECT_MEISAI LOOP
-- インボイス対応追加 start ABI
        -- 消費税商品の場合、消費税金額に設定する
        -- 一般消費税の場合、締め後一般消費税金額に設定
        IF meisai_rec.SHNCD = '099901' THEN
          cmnShimeippantaxkin := NVL(cmnShimeippantaxkin, 0) + meisai_rec.SHOHITAXKIN;
          CONTINUE;
        -- 軽減消費税の場合、締め後軽減消費税金額に設定
        ELSIF meisai_rec.SHNCD = '099902' THEN
          cmnShimekeigentaxkin := NVL(cmnShimekeigentaxkin, 0) + meisai_rec.SHOHITAXKIN;
          CONTINUE;
        END IF;
-- インボイス対応追加 end ABI

        -- キャンセル処理
        IF NOT canceled THEN
           -- ACCIS2024-7212 セット商品展開時に受注登録時点の単価を使用するため、削除前に展開行をリストに退避
           FOR rec IN (
             -- セット商品の展開行のみを受注配達明細テーブルから取得
             SELECT *
             FROM S04T002T
             WHERE APKIHONCD  = header_rec.APKIHONCD
               AND SHOPCD     = header_rec.SHOPCD
               AND ACPTDNPYNO = header_rec.ACPTDNPYNO
               AND EDANO      = header_rec.EDANO
               AND MEISAIKBN = '0'
               AND NYURYOKUMEISAIFLG = '0'
               AND YUKOKBN = '1'
           ) LOOP
              -- 受注配達明細テーブルの主キー項目（数値型は前0埋めして文字列化）を連結した値をキーとして取得データをリストに退避
              v_work_map(get_s04_key(rec)) := rec;
           END LOOP;
           DELETE FROM S04T002T JHMT
            WHERE JHMT.APKIHONCD  = header_rec.APKIHONCD
              AND JHMT.SHOPCD     = header_rec.SHOPCD
              AND JHMT.ACPTDNPYNO = header_rec.ACPTDNPYNO
              AND JHMT.EDANO      = header_rec.EDANO
           ;
           -- ポイント制度対応追加 start ABI
           -- 登録済の予定発生ポイントレコードから赤伝発行対象の売上伝票番号を取得する。
           FOR acpt_rec IN SELECT_ACPTD LOOP
             vRecordS04T001T := acpt_rec;
           END LOOP;

           -- 登録済の予定発生ポイントレコードの管理番号を取得する。
           FOR rec_point IN (
             SELECT
               PTTH.*
             FROM
               S20T001T PTTH
             WHERE PTTH.APKIHONCD  = header_rec.APKIHONCD
               AND PTTH.SHOPCD     = header_rec.SHOPCD
               AND PTTH.URIDNPYNO  = vRecordS04T001T.URIDNPYNO
           ) LOOP
              -- 既存の予定発生ポイントレコードを記憶
              cmnAkadnPntT := rec_point;
           END LOOP;
           IF cmnAkadnPntT.KANRINO IS NOT NULL THEN
             -- 管理番号で紐づく前回の予定発生ポイント明細レコードを記憶する。
             j := 0;
             FOR rec_pointms IN (
               SELECT
                 PTTM.*
               FROM
                 S20T002T PTTM
               WHERE PTTM.KANRINO  = cmnAkadnPntT.KANRINO
             ) LOOP
               -- 既存の予定発生ポイント明細レコードを記憶
               cmnAkadnPntMeiTList(j) := rec_pointms;
               j := j + 1;
             END LOOP;

             -- 管理番号で紐づく前回の予定発生ポイント明細計算詳細レコードを記憶する。
             j := 0;
             FOR rec_pointmsks IN (
               SELECT
                 PNKT.*
               FROM
                 S20T004T PNKT
               WHERE PNKT.KANRINO  = cmnAkadnPntT.KANRINO
             ) LOOP
               -- 既存の予定発生ポイント明細計算詳細レコードを記憶
               cmnAkadnPntMeiksTList(j) := rec_pointmsks;
               j := j + 1;
             END LOOP;
           END IF;
           -- ポイント制度対応追加 end ABI

           canceled := TRUE;
        END IF;

        -- 明細行初期化
        vRecordS04T002T := NULL;

        -- 受注配達明細テーブルに登録
        vRecordS04T002T.APKIHONCD  := header_rec.APKIHONCD;  -- ＡＰ基本コード
        vRecordS04T002T.SHOPCD     := header_rec.SHOPCD;     -- ショップコード
        vRecordS04T002T.ACPTDNPYNO := header_rec.ACPTDNPYNO; -- 受注伝票番号
        vRecordS04T002T.EDANO      := header_rec.EDANO;      -- 枝番号

        -- 受注伝票明細番号
        --     既存商品
        IF meisai_rec.MEISAIKBN_NEW = '0'
           AND meisai_rec.ACPTDNPYMEISAINO_OLD IS NOT NULL
           THEN
           vRecordS04T002T.ACPTDNPYMEISAINO := meisai_rec.ACPTDNPYMEISAINO_OLD;
        --     商品追加行、配布物、代引き
        ELSIF ( meisai_rec.MEISAIKBN_NEW = '0' AND meisai_rec.ACPTDNPYMEISAINO_OLD IS NULL )
           OR meisai_rec.MEISAIKBN_NEW IN ('1', '2')
           THEN
           vRecordS04T002T.ACPTDNPYMEISAINO := maxAcptdnpymeisaino + 1;
        END IF;
        maxAcptdnpymeisaino := vRecordS04T002T.ACPTDNPYMEISAINO;

        vRecordS04T002T.ACPTDNPYMEISAIEDANO := 0; -- 受注伝票明細枝番号
        vRecordS04T002T.SHNCD := NVL(meisai_rec.SHNCD_OLD, meisai_rec.SHNCD); -- 商品コード
        vRecordS04T002T.ITAKUAITEAPKIHONCD  := NVL(meisai_rec.ITAKUAITEAPKIHONCD_OLD, NULL);   -- 委託相手ＡＰ基本コード
        vRecordS04T002T.ITAKUAITESHOPCD     := NVL(meisai_rec.ITAKUAITESHOPCD_OLD, NULL);      -- 委託相手ショップコード
        vRecordS04T002T.ITAKUAITEACPTDNPYNO    := NVL(meisai_rec.ITAKUAITEACPTDNPYNO_OLD, NULL); -- 委託相手受注伝票番号
        vRecordS04T002T.ITAKUAITEEDANO      := NVL(meisai_rec.ITAKUAITEEDANO_OLD, NULL);       -- 委託相手枝番号
        vRecordS04T002T.ITAKUAITEACPTDNPYMEISAINO       := NVL(meisai_rec.ITAKUAITEACPTDNPYMEISAINO_OLD, NULL); -- 委託相手受注伝票明細番号
        vRecordS04T002T.ITAKUAITEACPTDNPYMEISAIEDANO := NVL(meisai_rec.AITEACPTDNPYMEISAIEDANO_OLD, NULL);    -- 委託相手受注伝票明細枝番号

        vRecordS04T002T.ACPTSURYO := NVL(meisai_rec.ACPTSURYO_OLD, 0); -- 受注数量
        vRecordS04T002T.ACPTUNT   := NVL(meisai_rec.ACPTUNT_OLD, 0);   -- 受注単価
        vRecordS04T002T.ACPTKIN   := NVL(meisai_rec.ACPTKIN_OLD, 0);   -- 受注金額
        vRecordS04T002T.ACPTUNTSHOHITAXKIN := NVL(meisai_rec.ACPTUNTSHOHITAXKIN_OLD, 0); -- 受注単価消費税金額
        vRecordS04T002T.ACPTSHOHITAXKIN    := NVL(meisai_rec.ACPTSHOHITAXKIN_OLD, 0);    -- 受注消費税金額
        vRecordS04T002T.ITAKUCMPNYACPTUNT            := NVL(meisai_rec.ITAKUCMPNYACPTUNT_OLD, 0);         -- 委託会社受注単価
        vRecordS04T002T.ITAKUCMPNYACPTUNTSHOHITAXKIN := NVL(meisai_rec.AITEACPTUNTSHOHITAXKIN_OLD, 0);    -- 委託会社受注単価消費税金額
        vRecordS04T002T.ITAKUCMPNYACPTSHOHITAXKIN    := NVL(meisai_rec.ITAKUCMPNYACPTSHOHITAXKIN_OLD, 0); -- 委託会社受注消費税金額
        vRecordS04T002T.SOUKAISU := NVL(meisai_rec.SOUKAISU_OLD, 0); -- 総回数
        vRecordS04T002T.ZANKAISU := NVL(meisai_rec.ZANKAISU_OLD, 0); -- 残回数

        vRecordS04T002T.MEISAIKBN := meisai_rec.MEISAIKBN_NEW; -- 明細区分

        -- 代引き以外の場合
        IF vRecordS04T002T.MEISAIKBN <> '2' THEN
           vRecordS04T002T.HAIFUBUTSUAPKIHONCD := meisai_rec.HAIFUBUTSUAPKIHONCD; -- 配布物ＡＰ基本コード
           vRecordS04T002T.HAIFUBUTSUSHOPCD    := meisai_rec.HAIFUBUTSUSHOPCD;    -- 配布物ショップコード
           vRecordS04T002T.HAIFUBUTSUCD        := meisai_rec.HAIFUBUTSUCD;        -- 配布物コード
           vRecordS04T002T.KATASIKI := NVL(meisai_rec.KATASIKI, meisai_rec.KATASIKI_OLD); -- 型式
           vRecordS04T002T.KEIJOKBN  := NVL(meisai_rec.KEIJOKBN, meisai_rec.KEIJOKBN_OLD); -- 計上区分

           vRecordS04T002T.NOHINSURYO := NVL(meisai_rec.NOHINSURYO, 0); -- 納品数量

           -- ACCIS2023-7212 計上区分が売上またはキャンペーンでセット商品（入力行）の場合、納品単価をマスタの内容で再設定
           -- meisai_recで納品単価をS05W001Tの金額から再設定したいのは既存明細のセット商品のみ
           IF vRecordS04T002T.KEIJOKBN IN ('01', '02') THEN
             IF meisai_rec.TANPINMEISAIFLG_OLD = '0'
               AND vRecordS04T002T.MEISAIKBN = '0' THEN
               -- 納品単価を再設定（納品金額計算時に使用しているmeisai_recの方に再設定）
               meisai_rec.NOHINUNT := getUntOfSetItem(header_rec, vRecordS04T002T);
             END IF;
           END IF;

           vRecordS04T002T.NOHINUNT   := NVL(meisai_rec.NOHINUNT, 0);   -- 納品単価
-- 本線対応変更 start ABI
--           vRecordS04T002T.NOHINKIN   := S01SyohizeiService.roundValue(NVL(meisai_rec.NOHINSURYO, 0) * NVL(meisai_rec.NOHINUNT, 0), vUrikinhasukeisankbn); -- 納品金額
           vRecordS04T002T.NOHINKIN   := Z1808B01.roundValue(NVL(meisai_rec.NOHINSURYO, 0) * NVL(meisai_rec.NOHINUNT, 0), vUrikinhasukeisankbn); -- 納品金額
-- 本線対応変更 end ABI

           vRecordS04T002T.NYURYOKUMEISAIFLG := '1'; -- 入力明細フラグ
           vRecordS04T002T.MEISAIBIKO        := meisai_rec.MEISAIBIKO; -- 明細備考
        -- 代引きの場合
        ELSIF vRecordS04T002T.MEISAIKBN = '2' THEN
           vRecordS04T002T.LCTGRYCD    := meisai_rec.LCTGRYCD_OLD; -- 大分類コード
           vRecordS04T002T.MCTGRYCD    := meisai_rec.MCTGRYCD_OLD; -- 中分類コード
           vRecordS04T002T.SCTGRYCD    := meisai_rec.SCTGRYCD_OLD; -- 小分類コード

           vRecordS04T002T.KATASIKI := meisai_rec.KATASIKI_OLD; -- 型式
           vRecordS04T002T.KEIJOKBN  := meisai_rec.KEIJOKBN_OLD; -- 計上区分

           vRecordS04T002T.HYOJUNUNT := meisai_rec.HYOJUNUNT_OLD; -- 標準単価
           vRecordS04T002T.TAXKBN := meisai_rec.TAXKBN_OLD; -- 税区分
           vRecordS04T002T.NOHINSURYO := NVL(meisai_rec.ACPTSURYO_OLD, 0); -- 納品数量
           vRecordS04T002T.NOHINUNT   := NVL(meisai_rec.ACPTUNT_OLD, 0);   -- 納品単価
           vRecordS04T002T.NOHINKIN   := NVL(meisai_rec.ACPTKIN_OLD, 0);   -- 納品金額
           vRecordS04T002T.NOHINUNTSHOHITAXKIN := NVL(meisai_rec.ACPTUNTSHOHITAXKIN_OLD, 0); -- 納品単価消費税金額
           vRecordS04T002T.NOHINSHOHITAXKIN := NVL(meisai_rec.ACPTSHOHITAXKIN_OLD, 0); -- 納品消費税金額

           vRecordS04T002T.NYURYOKUMEISAIFLG := meisai_rec.NYURYOKUMEISAIFLG_OLD; -- 入力明細フラグ
           vRecordS04T002T.TANPINMEISAIFLG := meisai_rec.TANPINMEISAIFLG_OLD; -- 単品明細フラグ
           vRecordS04T002T.STSHNCD := meisai_rec.STSHNCD_OLD; -- セット商品コード
           vRecordS04T002T.MEISAIBIKO := meisai_rec.MEISAIBIKO_OLD; -- 明細備考
        END IF;

        -- 有償区分
        vRecordS04T002T.YUSYOKBN := ( CASE WHEN vRecordS04T002T.HAIFUBUTSUCD IS NOT NULL THEN '0' -- 配布物の場合：0：設定なし(配布物など)
                                           WHEN vRecordS04T002T.SHNCD        IS NOT NULL          -- 商品／代引きの場合：
                                           THEN ( CASE WHEN vRecordS04T002T.KEIJOKBN = '08' THEN '9'          -- 計上区分が経費：9:経費
                                                       WHEN vRecordS04T002T.KEIJOKBN IN ('10', '09') THEN '3' -- 計上区分がレンタル：3:レンタル
                                                       WHEN vRecordS04T002T.KEIJOKBN IN ('01', '02') THEN '1' -- 計上区分が売上／キャンペーン：1：有償
                                                       ELSE '2' -- その他計上区分：2：無償
                                                  END )
                                      END );
        vRecordS04T002T.DATSKSMOTOKBN := NVL(meisai_rec.DATSKSMOTOKBN_OLD, DATSKSMOTOKBN_NEW); -- データ作成元区分
        vRecordS04T002T.URIDNPYNO := meisai_rec.URIDNPYNO_OLD; -- 売上伝票番号
        vRecordS04T002T.URIDNPYEDANO := meisai_rec.URIDNPYEDANO_OLD; -- 売上伝票枝番号
        vRecordS04T002T.URIDNPYMEISAINO    := meisai_rec.URIDNPYMEISAINO_OLD; -- 売上伝票明細番号
        vRecordS04T002T.URIDNPYMEISAIEDANO := meisai_rec.URIDNPYMEISAIEDANO_OLD; -- 売上伝票明細枝番号
        vRecordS04T002T.YUKOKBN       := '1';           -- 有効区分
        vRecordS04T002T.TOSHAAPKIHONCD := userinfo_rec.APKIHONCD;  -- 登録者ＡＰ基本コード
        vRecordS04T002T.TOSHASHOPCD    := userinfo_rec.SHOPCD;     -- 登録者ショップコード
        vRecordS04T002T.TOSHACD        := userinfo_rec.TNSHACD;    -- 登録者コード
        vRecordS04T002T.INPTSYSTMYMD   := userinfo_rec.SYSTMYMD;   -- 登録システム日付
        vRecordS04T002T.INPTSYSTMJIKAN := userinfo_rec.SYSTMJIKAN; -- 登録システム時間
        vRecordS04T002T.INPTUNYOYMD    := userinfo_rec.UNYOYMD;    -- 登録運用日付
        vRecordS04T002T.INPTIPADRSE    := userinfo_rec.IPADRSE;    -- 登録ＩＰアドレス
        vRecordS04T002T.INPTPGMID      := userinfo_rec.PGMID;      -- 登録プログラムＩＤ
        vRecordS04T002T.KOSHAAPKIHONCD := userinfo_rec.APKIHONCD;  -- 更新者ＡＰ基本コード
        vRecordS04T002T.KOSHASHOPCD    := userinfo_rec.SHOPCD;     -- 更新者ショップコード
        vRecordS04T002T.KOSHACD        := userinfo_rec.TNSHACD;    -- 更新者コード
        vRecordS04T002T.KSHNSYSTMYMD   := userinfo_rec.SYSTMYMD;   -- 更新システム日付
        vRecordS04T002T.KSHNSYSTMJIKAN := userinfo_rec.SYSTMJIKAN; -- 更新システム時間
        vRecordS04T002T.KSHNUNYOYMD    := userinfo_rec.UNYOYMD;    -- 更新運用日付
        vRecordS04T002T.KSHNIPADRSE    := userinfo_rec.IPADRSE;    -- 更新ＩＰアドレス
        vRecordS04T002T.KSHNPGMID      := userinfo_rec.PGMID;      -- 更新プログラムＩＤ

        -- 商品の場合
        IF vRecordS04T002T.SHNCD IS NOT NULL
           AND vRecordS04T002T.MEISAIKBN = '0' THEN
           -- 商品情報の取得
           SELECT SHMV.LCTGRYCD -- 大分類コード
                , SHMV.MCTGRYCD -- 中分類コード
                , SHMV.SCTGRYCD -- 小分類コード
                -- 単品明細フラグ
                , ( CASE WHEN SHMV.STKBN = '1' THEN '0'
                         ELSE '1'
                    END ) TANPINMEISAIFLG
                -- セット商品コード
                , ( CASE WHEN SHMV.STKBN = '1' THEN SHMV.SHNCD
                         ELSE NULL
                    END ) STSHNCD
                -- 標準価格：配達区分、受注日（配達実績日）より設定
                , ( CASE WHEN SHMV.HYOJUNPRICEKIRIKAEDD IS NULL OR header_rec.DLVRYJISSEKIDD >= SHMV.HYOJUNPRICEKIRIKAEDD
                         THEN ( CASE header_rec.DLVRYKBN
                                WHEN '3' THEN SHMV.SHINMOCHIKAERIPRICE -- 新持ち帰り価格
                                         ELSE SHMV.SHINHYOJUNPRICE     -- 新標準価格
                                END )
                         ELSE ( CASE header_rec.DLVRYKBN
                                WHEN '3' THEN SHMV.GENZMOCHIKAERIPRICE -- 現持ち帰り価格
                                         ELSE SHMV.GENZHYOJUNPRICE     -- 現標準価格
                                END )
                    END ) HYOJUNUNT
                , CASE WHEN SHMV.TAXKBN IN ('4','5') AND header_rec.DLVRYJISSEKIDD <= '20190930' THEN '1' ELSE SHMV.TAXKBN END TAXKBN
                , SHMV.TAXSETTEIKBN -- 税設定区分
             INTO vRecordS04T002T.LCTGRYCD -- 大分類コード
                , vRecordS04T002T.MCTGRYCD -- 中分類コード
                , vRecordS04T002T.SCTGRYCD -- 小分類コード
                , vRecordS04T002T.TANPINMEISAIFLG -- 単品明細フラグ
                , vRecordS04T002T.STSHNCD         -- セット商品コード
                , vRecordS04T002T.HYOJUNUNT -- 標準価格
                , vRecordS04T002T.TAXKBN -- 税区分
                , taxsetteikbn           -- 税設定区分
             FROM S02V001T SHMV
            WHERE SHMV.APKIHONCD = vRecordS04T002T.APKIHONCD
              AND SHMV.SHOPCD    = vRecordS04T002T.SHOPCD
              AND SHMV.SHNCD     = vRecordS04T002T.SHNCD
              AND SHMV.SHOPYUKOKBN = '1'
           ;

           -- 納品消費税
           setTax( vRecordS04T002T -- 明細行
                 , vRecordS04T002T.NOHINSURYO -- 数量
                 , vRecordS04T002T.NOHINUNT   -- 単価
                 , vRecordS04T002T.APKIHONCD
                 , vRecordS04T002T.SHOPCD
                 , vRecordS04T002T.TAXKBN    -- 税区分
                 , taxsetteikbn  -- 税設定区分
                 , iTaxkeisankbn -- 税計算区分
                 , header_rec.DLVRYJISSEKIDD -- 切替日
                 , header_rec.DLVRYKBN --配達区分
-- インボイス対応追加 start ABI
                , '0'
                , '1'
-- インボイス対応追加 end ABI
                 , vRecordS04T002T.NOHINUNTSHOHITAXKIN -- 単価消費税
                 , vRecordS04T002T.NOHINSHOHITAXKIN    -- 消費税
                 );
        END IF;

        -- 配布物の場合
        IF vRecordS04T002T.HAIFUBUTSUCD IS NOT NULL
           AND vRecordS04T002T.MEISAIKBN = '1' THEN
           vRecordS04T002T.KEIJOKBN  := NULL; -- 計上区分
           vRecordS04T002T.ACPTSURYO := 1;    -- 受注数量
           vRecordS04T002T.HYOJUNUNT := NULL; -- 標準単価
           vRecordS04T002T.ACPTUNT   := NULL; -- 受注単価
           vRecordS04T002T.ACPTKIN   := NULL; -- 受注金額
           vRecordS04T002T.ACPTUNTSHOHITAXKIN := NULL; -- 受注単価消費税金額
           vRecordS04T002T.ACPTSHOHITAXKIN    := NULL; -- 受注消費税金額
           vRecordS04T002T.NOHINSURYO := 1; -- 納品数量
           vRecordS04T002T.NOHINUNT   := 0; -- 納品単価
           vRecordS04T002T.NOHINKIN   := 0; -- 納品金額
           vRecordS04T002T.NOHINUNTSHOHITAXKIN := 0; -- 納品単価消費税金額
           vRecordS04T002T.NOHINSHOHITAXKIN    := 0; -- 納品消費税金額
           vRecordS04T002T.TANPINMEISAIFLG   := '1';   -- 単品明細フラグ
        END IF;

        INSERT INTO S04T002T JHMT VALUES vRecordS04T002T;

        -- ポイント制度対応追加 start ABI
        -- 引数ワーク登録内容をリストに記憶
          wKuroS20W002TList(wGyoban).APKIHONCD := header_rec.APKIHONCD;                       -- ＡＰ基本コード
          wKuroS20W002TList(wGyoban).SHOPCD := header_rec.SHOPCD;                             -- ショップコード
          wKuroS20W002TList(wGyoban).TNSHAAPKIHONCD := userinfo_rec.APKIHONCD;                -- 担当者ＡＰ基本コード
          wKuroS20W002TList(wGyoban).TNSHASHOPCD := userinfo_rec.SHOPCD;                      -- 担当者ショップコード
          wKuroS20W002TList(wGyoban).TNSHACD := userinfo_rec.TNSHACD;                         -- 担当者コード
          wKuroS20W002TList(wGyoban).PRCSID := c_PRCSID;                                      -- 処理ID
          wKuroS20W002TList(wGyoban).IPADRSE := userinfo_rec.IPADRSE;                         -- ＩＰアドレス
          wKuroS20W002TList(wGyoban).GYOBANGOU := wGyoban;                                    -- 行番号
          wKuroS20W002TList(wGyoban).DNPYMEISAINO := vRecordS04T002T.ACPTDNPYMEISAINO;        -- 伝票明細番号
          wKuroS20W002TList(wGyoban).DNPYMEISAIEDANO := vRecordS04T002T.ACPTDNPYMEISAIEDANO;  -- 伝票明細枝番号
          wKuroS20W002TList(wGyoban).SHNCD := vRecordS04T002T.SHNCD;                          -- 商品コード
          wKuroS20W002TList(wGyoban).KEIJOKBN := vRecordS04T002T.KEIJOKBN;                    -- 計上区分
          wKuroS20W002TList(wGyoban).SHNUNT := vRecordS04T002T.NOHINUNT;                      -- 商品単価
          wKuroS20W002TList(wGyoban).GAMENPOINT := null;                                      -- 画面からの発生
          wKuroS20W002TList(wGyoban).SURYO := vRecordS04T002T.NOHINSURYO;                     -- 数量
          wKuroS20W002TList(wGyoban).MEISAIBIKO := vRecordS04T002T.MEISAIBIKO;                -- 明細備考
          wKuroS20W002TList(wGyoban).YTHSPINTMEISAIKANRINO := null;                           -- 予定発生ポイント明細の管理番号
          wKuroS20W002TList(wGyoban).YTHSPINTMEISAIRENBAN := null;                            -- 予定発生ポイント明細の連番
          wKuroS20W002TList(wGyoban).URIKGNMSYUTOKUPOINTKIND := null;                         -- 売上還元ポイントマスタの取得種別
          wKuroS20W002TList(wGyoban).URIKGNMSYUTOKUPLNCD := null;                             -- 売上還元ポイントマスタの取得プランコード
          wKuroS20W002TList(wGyoban).YUKOKBN := '1';                                          -- 有効区分
          wKuroS20W002TList(wGyoban).TOSHAAPKIHONCD := userinfo_rec.APKIHONCD;                -- 登録者ＡＰ基本コード
          wKuroS20W002TList(wGyoban).TOSHASHOPCD := userinfo_rec.SHOPCD;                      -- 登録者ショップコード
          wKuroS20W002TList(wGyoban).TOSHACD := userinfo_rec.TNSHACD;                         -- 登録者コード
          wKuroS20W002TList(wGyoban).INPTSYSTMYMD := userinfo_rec.SYSTMYMD;                   -- 登録システム日付
          wKuroS20W002TList(wGyoban).INPTSYSTMJIKAN := userinfo_rec.SYSTMJIKAN;               -- 登録システム時間
          wKuroS20W002TList(wGyoban).INPTUNYOYMD := userinfo_rec.UNYOYMD;                     -- 登録運用日付
          wKuroS20W002TList(wGyoban).INPTIPADRSE := userinfo_rec.IPADRSE;                     -- 登録ＩＰアドレス
          wKuroS20W002TList(wGyoban).INPTPGMID := userinfo_rec.PGMID;                         -- 登録プログラムＩＤ
          wKuroS20W002TList(wGyoban).KOSHAAPKIHONCD := userinfo_rec.APKIHONCD;                -- 更新者AP基本コード
          wKuroS20W002TList(wGyoban).KOSHASHOPCD := userinfo_rec.SHOPCD;                      -- 更新者ショップコード
          wKuroS20W002TList(wGyoban).KOSHACD := userinfo_rec.TNSHACD;                         -- 更新者コード
          wKuroS20W002TList(wGyoban).KSHNSYSTMYMD := userinfo_rec.SYSTMYMD;                   -- 更新システム日付
          wKuroS20W002TList(wGyoban).KSHNSYSTMJIKAN := userinfo_rec.SYSTMJIKAN;               -- 更新システム時間
          wKuroS20W002TList(wGyoban).KSHNUNYOYMD := userinfo_rec.UNYOYMD;                     -- 更新運用日付
          wKuroS20W002TList(wGyoban).KSHNIPADRSE := userinfo_rec.IPADRSE;                     -- 更新ＩＰアドレス
          wKuroS20W002TList(wGyoban).KSHNPGMID := userinfo_rec.PGMID;                         -- 更新プログラムＩＤ

        wGyoban := wGyoban + 1;
        -- ポイント制度対応追加 end ABI

        -- セット商品展開
        IF vRecordS04T002T.TANPINMEISAIFLG = '0'
           AND vRecordS04T002T.MEISAIKBN = '0' THEN

           -- ACCIS2023-7212 引数に退避した受注配達明細リスト（展開行）を追加
           processExpandMeisai(vRecordS04T002T, header_rec, iTaxkeisankbn, v_work_map);

        END IF;

        -- 入力明細フラグ（配達実績取込ワーク）が"1"（入力行）、かつ明細区分（受注配達明細）が"0"（商品追加行）の場合、明細数量金額変更フラグ、および明細数量オールゼロフラグを設定する
        IF meisai_rec.NYURYOKUMEISAIFLG = '1' AND meisai_rec.MEISAIKBN_NEW = '0' THEN
          -- AP基本コード（受注配達明細）が未設定の場合、明細数量金額変更フラグにTRUEを設定
          IF meisai_rec.APKIHONCD_OLD IS NULL THEN
            cmnCngmeisuryoflg := TRUE;
          END IF;
-- ACCIS2023-6518 start ABI 明細数量金額変更フラグの設定を行う際、受注配達明細テーブルの受注関連の項目と比較するのではなく、納品関連の項目と比較するように修正
--                          ※配達売上済の受注に対する処理のため、既に納品数量が設定されており、変更有無はこれらの項目で行う必要がある
          -- 以下のいづれかに当てはまる場合、明細数量金額変更フラグにTRUEを設定
          --   ・納品数量（配達実績取込ワーク）と、納品数量（変更前受注配達明細）が一致しない。
          --   ・商品コード（配達実績取込ワーク）と、商品コード（変更前受注配達明細）が一致しない。
          --   ・納品単価（配達実績取込ワーク）と、納品単価（変更前受注配達明細）が一致しない。
          --   ・端数計算処理の戻り値（＝納品金額）と、納品金額（変更前受注配達明細）が一致しない。
          --   ・商品の場合に取得した納品消費税と、納品消費税金額（変更前受注配達明細）が一致しない。
          IF (meisai_rec.NOHINSURYO <> meisai_rec.NOHINSURYO_OLD)
              OR (meisai_rec.SHNCD <> meisai_rec.SHNCD_OLD)
              OR (meisai_rec.NOHINUNT <> meisai_rec.NOHINUNT_OLD)
-- 本線対応変更 start ABI
--              OR (S01SyohizeiService.roundValue(meisai_rec.NOHINSURYO * meisai_rec.NOHINUNT, vUrikinhasukeisankbn ) <> meisai_rec.ACPTKIN_OLD)
              OR (Z1808B01.roundValue(meisai_rec.NOHINSURYO * meisai_rec.NOHINUNT, vUrikinhasukeisankbn ) <> meisai_rec.NOHINKIN_OLD)
-- 本線対応変更 end ABI
              OR (vRecordS04T002T.NOHINSHOHITAXKIN <> meisai_rec.NOHINSHOHITAXKIN_OLD) THEN
-- ACCIS2023-6518 end ABI
            -- 売上データを作成する際に、参照するためのフラグ
            cmnCngmeisuryoflg := TRUE;
          END IF;

          -- 納品数量（配達実績取込ワーク）が0以外の場合、明細数量オールゼロフラグにFALSEを設定
          IF meisai_rec.NOHINSURYO <> '0' THEN
            -- 売上データを作成する際に、参照するためのフラグ
            -- ※このフラグは初期値がTRUEなので、処理対象の受注に1つでも納品数量が0ではない明細があるかを判定してる
            cmnAllzeromeisuryoflg := FALSE;
          END IF;
        END IF;
    END LOOP;

    -- ポイント制度対応追加 start ABI
    -- 赤伝と黒伝の予定発生ポイントレコード作成判定(ループしているが受注伝票単位なので複数回ループはしない)
    FOR acpt_rec IN SELECT_ACPTD_AKADEN LOOP
      IF cmnCngdlvryddflg = TRUE OR cmnCngmeisuryoflg = TRUE THEN
        IF acpt_rec.URIDNPYNO IS NOT NULL AND cmnAllzeromeisuryoflg = FALSE THEN
          -- 受注配達テーブルデータ.売上伝票番号が設定されていて、かつ明細数量オールゼロフラグがFALSE（納品数量が0以外）の場合、
          iAkadenRegist := 1;
          iKurodenRegist := 1;
        ELSIF acpt_rec.URIDNPYNO IS NOT NULL THEN
          -- 上記以外で受注配達テーブルデータ.売上伝票番号が設定されている場合、
          iAkadenRegist := 1;
        ELSIF cmnAllzeromeisuryoflg = FALSE THEN
          -- 上記以外で明細数量オールゼロフラグがFALSE（納品数量が0以外）の場合、
          iKurodenRegist := 1;
        END IF;
      END IF;
    END LOOP;

    -- 赤伝売上伝票と紐づく予定発生ポイントレコードがある場合に実施（ポイント制度前に登録された売上の場合は予定発生ポイントレコードはない）
    IF iAkadenRegist = 1 AND cmnAkadnPntT.KANRINO IS NOT NULL THEN
      -- 新しい管理番号を採番
      cmnAkadenSaibanno := GetPointKanrino(header_rec);

      -- 紐づく予定発生ポイントレコードがあれば赤伝用に複製して登録
      cmnAkadnPntT.KANRINO := cmnAkadenSaibanno;              -- 管理番号
      cmnAkadnPntT.YUKOKBN := '1';                            -- 有効区分
      cmnAkadnPntT.TOSHAAPKIHONCD := userinfo_rec.APKIHONCD;  -- 登録者ＡＰ基本コード
      cmnAkadnPntT.TOSHASHOPCD := userinfo_rec.SHOPCD;        -- 登録者ショップコード
      cmnAkadnPntT.TOSHACD := userinfo_rec.TNSHACD;           -- 登録者コード
      cmnAkadnPntT.INPTSYSTMYMD := userinfo_rec.SYSTMYMD;     -- 登録システム日付
      cmnAkadnPntT.INPTSYSTMJIKAN := userinfo_rec.SYSTMJIKAN; -- 登録システム時間
      cmnAkadnPntT.INPTUNYOYMD := userinfo_rec.UNYOYMD;       -- 登録運用日付
      cmnAkadnPntT.INPTIPADRSE := userinfo_rec.IPADRSE;       -- 登録ＩＰアドレス
      cmnAkadnPntT.INPTPGMID := userinfo_rec.PGMID;           -- 登録プログラムＩＤ
      cmnAkadnPntT.KOSHAAPKIHONCD := userinfo_rec.APKIHONCD;  -- 更新者ＡＰ基本コード
      cmnAkadnPntT.KOSHASHOPCD := userinfo_rec.SHOPCD;        -- 更新者ショップコード
      cmnAkadnPntT.KOSHACD := userinfo_rec.TNSHACD;           -- 更新者コード
      cmnAkadnPntT.KSHNSYSTMYMD := userinfo_rec.SYSTMYMD;     -- 更新システム日付
      cmnAkadnPntT.KSHNSYSTMJIKAN := userinfo_rec.SYSTMJIKAN; -- 更新システム時間
      cmnAkadnPntT.KSHNUNYOYMD := userinfo_rec.UNYOYMD;       -- 更新運用日付
      cmnAkadnPntT.KSHNIPADRSE := userinfo_rec.IPADRSE;       -- 更新ＩＰアドレス
      cmnAkadnPntT.KSHNPGMID := userinfo_rec.PGMID;           -- 更新プログラムＩＤ
      INSERT INTO S20T001T VALUES cmnAkadnPntT;

      -- 紐づくポイント明細テーブルの赤伝票作成を行う
      FOR idx IN cmnAkadnPntMeiTList.FIRST .. cmnAkadnPntMeiTList.LAST LOOP
        -- 予定発生ポイント明細レコードを赤伝用に複製し補正して登録
        cmnAkadnPntMeiTList(idx).KANRINO := cmnAkadenSaibanno;                           -- 管理番号
        cmnAkadnPntMeiTList(idx).POINTSU := ABS(cmnAkadnPntMeiTList(idx).POINTSU) * -1;  -- ポイント数(補正)
        cmnAkadnPntMeiTList(idx).POINTYTIDD := userinfo_rec.UNYOYMD;                     -- ポイント予定日付(運用日付)
        cmnAkadnPntMeiTList(idx).POINTHASSEIDD := NULL;                                  -- ポイント発生日付
        cmnAkadnPntMeiTList(idx).POINTMYAQUARENKEIDD := NULL;                            -- ポイントマイアクア連携日付
        cmnAkadnPntMeiTList(idx).YUKOKBN := '1';                                         -- 有効区分
        cmnAkadnPntMeiTList(idx).TOSHAAPKIHONCD := userinfo_rec.APKIHONCD;               -- 登録者ＡＰ基本コード
        cmnAkadnPntMeiTList(idx).TOSHASHOPCD := userinfo_rec.SHOPCD;                     -- 登録者ショップコード
        cmnAkadnPntMeiTList(idx).TOSHACD := userinfo_rec.TNSHACD;                        -- 登録者コード
        cmnAkadnPntMeiTList(idx).INPTSYSTMYMD := userinfo_rec.SYSTMYMD;                  -- 登録システム日付
        cmnAkadnPntMeiTList(idx).INPTSYSTMJIKAN := userinfo_rec.SYSTMJIKAN;              -- 登録システム時間
        cmnAkadnPntMeiTList(idx).INPTUNYOYMD := userinfo_rec.UNYOYMD;                    -- 登録運用日付
        cmnAkadnPntMeiTList(idx).INPTIPADRSE := userinfo_rec.IPADRSE;                    -- 登録ＩＰアドレス
        cmnAkadnPntMeiTList(idx).INPTPGMID := userinfo_rec.PGMID;                        -- 登録プログラムＩＤ
        cmnAkadnPntMeiTList(idx).KOSHAAPKIHONCD := userinfo_rec.APKIHONCD;               -- 更新者ＡＰ基本コード
        cmnAkadnPntMeiTList(idx).KOSHASHOPCD := userinfo_rec.SHOPCD;                     -- 更新者ショップコード
        cmnAkadnPntMeiTList(idx).KOSHACD := userinfo_rec.TNSHACD;                        -- 更新者コード
        cmnAkadnPntMeiTList(idx).KSHNSYSTMYMD := userinfo_rec.SYSTMYMD;                  -- 更新システム日付
        cmnAkadnPntMeiTList(idx).KSHNSYSTMJIKAN := userinfo_rec.SYSTMJIKAN;              -- 更新システム時間
        cmnAkadnPntMeiTList(idx).KSHNUNYOYMD := userinfo_rec.UNYOYMD;                    -- 更新運用日付
        cmnAkadnPntMeiTList(idx).KSHNIPADRSE := userinfo_rec.IPADRSE;                    -- 更新ＩＰアドレス
        cmnAkadnPntMeiTList(idx).KSHNPGMID := userinfo_rec.PGMID;                        -- 更新プログラムＩＤ
        INSERT INTO S20T002T VALUES cmnAkadnPntMeiTList(idx);
      END LOOP;

      -- 紐づくポイント明細計算詳細テーブルの赤伝票作成を行う
      FOR idx IN cmnAkadnPntMeiksTList.FIRST .. cmnAkadnPntMeiksTList.LAST LOOP
        -- 予定発生ポイント明細計算詳細レコードを赤伝用に複製し補正して登録
        cmnAkadnPntMeiksTList(idx).KANRINO := cmnAkadenSaibanno;                                        -- 管理番号
        cmnAkadnPntMeiksTList(idx).SURYO := ABS(cmnAkadnPntMeiksTList(idx).SURYO) * -1;                 -- 数量(補正)
        cmnAkadnPntMeiksTList(idx).KEISANPOINTSU := ABS(cmnAkadnPntMeiksTList(idx).KEISANPOINTSU) * -1; -- 計算ポイント数(補正)
        cmnAkadnPntMeiksTList(idx).YUKOKBN := '1';                                                      -- 有効区分
        cmnAkadnPntMeiksTList(idx).TOSHAAPKIHONCD := userinfo_rec.APKIHONCD;                            -- 登録者ＡＰ基本コード
        cmnAkadnPntMeiksTList(idx).TOSHASHOPCD := userinfo_rec.SHOPCD;                                  -- 登録者ショップコード
        cmnAkadnPntMeiksTList(idx).TOSHACD := userinfo_rec.TNSHACD;                                     -- 登録者コード
        cmnAkadnPntMeiksTList(idx).INPTSYSTMYMD := userinfo_rec.SYSTMYMD;                               -- 登録システム日付
        cmnAkadnPntMeiksTList(idx).INPTSYSTMJIKAN := userinfo_rec.SYSTMJIKAN;                           -- 登録システム時間
        cmnAkadnPntMeiksTList(idx).INPTUNYOYMD := userinfo_rec.UNYOYMD;                                 -- 登録運用日付
        cmnAkadnPntMeiksTList(idx).INPTIPADRSE := userinfo_rec.IPADRSE;                                 -- 登録ＩＰアドレス
        cmnAkadnPntMeiksTList(idx).INPTPGMID := userinfo_rec.PGMID;                                     -- 登録プログラムＩＤ
        cmnAkadnPntMeiksTList(idx).KOSHAAPKIHONCD := userinfo_rec.APKIHONCD;                            -- 更新者ＡＰ基本コード
        cmnAkadnPntMeiksTList(idx).KOSHASHOPCD := userinfo_rec.SHOPCD;                                  -- 更新者ショップコード
        cmnAkadnPntMeiksTList(idx).KOSHACD := userinfo_rec.TNSHACD;                                     -- 更新者コード
        cmnAkadnPntMeiksTList(idx).KSHNSYSTMYMD := userinfo_rec.SYSTMYMD;                               -- 更新システム日付
        cmnAkadnPntMeiksTList(idx).KSHNSYSTMJIKAN := userinfo_rec.SYSTMJIKAN;                           -- 更新システム時間
        cmnAkadnPntMeiksTList(idx).KSHNUNYOYMD := userinfo_rec.UNYOYMD;                                 -- 更新運用日付
        cmnAkadnPntMeiksTList(idx).KSHNIPADRSE := userinfo_rec.IPADRSE;                                 -- 更新ＩＰアドレス
        cmnAkadnPntMeiksTList(idx).KSHNPGMID := userinfo_rec.PGMID;                                     -- 更新プログラムＩＤ
        INSERT INTO S20T004T VALUES cmnAkadnPntMeiksTList(idx);
      END LOOP;
    END IF;

    IF iKurodenRegist = 1 AND wKuroS20W002TList.COUNT > 0 THEN
      -- 黒伝登録する場合はリストに登録しておいた内容を引数ワークに登録
      FOR n IN wKuroS20W002TList.FIRST .. wKuroS20W002TList.LAST LOOP
        -- 引数ワーク(S20W002T)登録
        INSERT INTO S20W002T VALUES wKuroS20W002TList(n);
      END LOOP;

      registPointMeisaiTable(header_rec
        , vRecordS04T002T);
    END IF;
    -- ポイント制度対応追加 end ABI

    -- 本数、金額、サーバー合計
    FOR meisai_new IN ( SELECT JHMT.*
                          FROM S04T002T JHMT
                         WHERE JHMT.APKIHONCD  = header_rec.APKIHONCD
                           AND JHMT.SHOPCD     = header_rec.SHOPCD
                           AND JHMT.ACPTDNPYNO = header_rec.ACPTDNPYNO
                           AND JHMT.EDANO      = header_rec.EDANO
                           AND JHMT.MEISAIKBN <> '2' -- 代引き以外
                           AND JHMT.YUKOKBN = '1'
                         ORDER BY JHMT.ACPTDNPYMEISAINO
                                , JHMT.ACPTDNPYMEISAIEDANO
                      ) LOOP

        IF meisai_new.NYURYOKUMEISAIFLG = '1' THEN
           oyaKeijokbn := meisai_new.KEIJOKBN;
        END IF;
        IF oyaKeijoKbn IN ('01', '02') THEN
            -- 親が有償の場合子の計上区分を使用
            addAllKeijokbn := meisai_new.KEIJOKBN;
        ELSE
            -- 親が有償の場合親の計上区分を使用
            addAllKeijokbn := oyaKeijokbn;
        END IF;

        addAll( meisai_new  -- 合計対象
              , addAllKeijokbn -- 計上区分
              , watercd -- 製品水大分類
              , serverL -- サーバー大分類
              , serverM -- サーバー本体中分類
              , sum_rec -- 合計データ
        );

    END LOOP;

    EXCEPTION
      WHEN OTHERS THEN
        IF SELECT_HAIFU%ISOPEN THEN
           CLOSE SELECT_HAIFU;
        END IF;
        IF SELECT_MEISAI%ISOPEN THEN
           CLOSE SELECT_MEISAI;
        END IF;
        RAISE_APPLICATION_ERROR(-20016, SQLERRM || '受注配達明細作成時エラーが発生しました。');
  END processJuchuMeisaiWeb;
-- 本線対応追加 end ABI

  /*****************************************************************************/
  /* プロシージャ名：受託配達テーブル配達済更新                                     */
  /* 引数             ：header_rec      配達実績ワークデータヘッダ                 */
  /*                  ：sum_rec         合計データ                              */
  /*                  ：tokuisakiMST    得意先マスタ                             */
  /*                  ：tokuisakiJST    得意先実績テーブル                       */
  /*                  ：iTaxkeisankbn   税計算区分                              */
  /*                  ：iKaishuhohokbn  回収方法区分                            */
  /*****************************************************************************/
  PROCEDURE processJuchuDelivered( header_rec  IN S05W001T%rowtype -- 配達実績ワークデータヘッダ
                                 , sum_rec     IN sum_rec_type     -- 合計データ
                                 , tokuisakiMST   IN S02M013T%rowtype -- 得意先マスタ
                                 , tokuisakiJST   IN S02M050T%rowtype -- 得意先実績テーブル
                                 , iTaxkeisankbn  IN VARCHAR2 -- 税計算区分
                                 , iKaishuhohokbn IN VARCHAR2 -- 回収方法区分
-- インボイス対応追加 start ABI
                                 , acpt_old     IN OUT S04T001T%rowtype
-- インボイス対応追加 end ABI
  ) IS
  /* CURSOR：配布物更新用ヘッダ */
  CURSOR SELECT_HFBH IS
    SELECT JHTT.*
      FROM S04T001T JHTT
     WHERE JHTT.APKIHONCD  = header_rec.APKIHONCD
       AND JHTT.SHOPCD     = header_rec.SHOPCD
       AND JHTT.ACPTDNPYNO = header_rec.ACPTDNPYNO
       AND JHTT.EDANO      = header_rec.EDANO
       AND JHTT.YUKOKBN   = '1'
  ;
  /* CURSOR：配布物更新用明細 */
  CURSOR SELECT_HFBM IS
    SELECT JHMT.*
      FROM S04T002T JHMT
     WHERE JHMT.APKIHONCD  = header_rec.APKIHONCD
       AND JHMT.SHOPCD     = header_rec.SHOPCD
       AND JHMT.ACPTDNPYNO = header_rec.ACPTDNPYNO
       AND JHMT.EDANO      = header_rec.EDANO
       AND JHMT.MEISAIKBN = '1'
       AND JHMT.YUKOKBN   = '1'
  ;

  -- ACCIS2023-7212 消費税再計算用の変数を追加
  vIppanTax NUMBER; -- 一般
  vKeigenTax NUMBER; -- 軽減

  BEGIN
    -- ACCIS2023-7212 セット商品の納品単価が再設定され納品金額に差分が生じている可能性があるため
    -- cmnNohinippannukikinやcmnNohinkeigennukikinを元に税区分ごとの消費税を再計算する
    -- cmnShimeippantaxkin、cmnNohinkeigentaxkinに値が設定されていない場合は
    -- cmnNohinippantaxkinやcmnNohinkeigentaxkinといった明細処理で算出している値が設定されるため
    -- cmnShimeippantaxkinとcmnNohinkeigentaxkinに値が設定されている場合に再計算を行い
    -- 算出した値と差異が生じている場合は単価再設定により金額が変更されたものとして
    -- 算出した値を優先する
    -- 本処理は納品時点での仮税額の整合性を担保するものであり、請求締め時に正式な税額が再計算される
    IF cmnShimeippantaxkin IS NOT NULL THEN
      -- 一般税区分の消費税再計算
      vIppanTax := Z1808B01.SyohizeiKeisan(
        iSimpleFg      => '1',
        iTaxsetteikbn  => '1',
        iTaxkbn        => '1', -- 一般
        iTaxKeisankbn  => NULL,
        iKirikaebi     => header_rec.DLVRYJISSEKIDD,
        iApKihoncd     => header_rec.APKIHONCD,
        iShopcd        => header_rec.SHOPCD,
        iSiiresakicd   => NULL,
        iSuryo         => NULL,
        iKin           => TO_CHAR(cmnNohinippannukikin)
      );
      IF cmnShimeippantaxkin <> vIppanTax THEN
        cmnShimeippantaxkin := vIppanTax;
      END IF;
    END IF;
    IF cmnShimekeigentaxkin IS NOT NULL THEN
      -- 軽減税区分の消費税再計算
      vKeigenTax := Z1808B01.SyohizeiKeisan(
        iSimpleFg      => '1',
        iTaxsetteikbn  => '1',
        iTaxkbn        => '4', -- 軽減税区分
        iTaxKeisankbn  => NULL,
        iKirikaebi     => header_rec.DLVRYJISSEKIDD,
        iApKihoncd     => header_rec.APKIHONCD,
        iShopcd        => header_rec.SHOPCD,
        iSiiresakicd   => NULL,
        iSuryo         => NULL,
        iKin           => TO_CHAR(cmnNohinkeigennukikin)
      );
      IF cmnShimekeigentaxkin <> vKeigenTax THEN
        cmnShimekeigentaxkin := vKeigenTax;
      END IF;
    END IF;

    UPDATE S04T001T JHTT
       SET JHTT.DLVRYJISSEKIDD = header_rec.DLVRYJISSEKIDD -- 配達実績日
         , JHTT.DLVRYDD        = header_rec.DLVRYJISSEKIDD -- 配達日
           -- 不在置き実績フラグ
         , JHTT.FZAIOKIJISSEKIFLG = header_rec.FZAIOKIJISSEKIFLG
         , JHTT.KZBTLN1N2RITTORUHNS = header_rec.KZBTLN1N2RITTORUHNS -- 回収実績ボトル１２Ｌ本数
         , JHTT.KZBTLN8RITTORUHNS   = header_rec.KZBTLN8RITTORUHNS   -- 回収実績ボトル８Ｌ本数
         , JHTT.DNPYSTS  = header_rec.DLVRYSTS  -- 伝票ステータス
         , JHTT.DLVRYSTS = header_rec.DLVRYSTS -- 配達ステータス
         , JHTT.DLVRYTNSHACD = header_rec.DLVRYTNSHACD -- 配達担当者コード
         , JHTT.DLVRYCMPNYCD = header_rec.DLVRYCMPNYCD -- 配達会社コード
         , JHTT.DLVRYKBN = header_rec.DLVRYKBN -- 配達区分
         , JHTT.DLVRYJISSEKIPRCSDD = userinfo_rec.UNYOYMD -- 配達実績処理日
         , JHTT.DLVRYJISSEKIMONOAPKIHONCD = userinfo_rec.APKIHONCD -- 配達実績者ＡＰ基本コード
         , JHTT.DLVRYJISSEKIMONOSHOPCD    = userinfo_rec.SHOPCD    -- 配達実績者ショップコード
         , JHTT.DLVRYJISSEKIPRCSTNSHACD   = userinfo_rec.TNSHACD -- 配達実績処理担当者コード
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
-- インボイス対応変更 start ABI
         --, JHTT.NOHINSHOHITAXKIN = sum_rec.NOHINSHOHITAXKIN -- 納品消費税金額
         , JHTT.NOHINSHOHITAXKIN =
           CASE WHEN cmnShimeippantaxkin IS NULL OR cmnShimekeigentaxkin IS NULL THEN
             sum_rec.NOHINSHOHITAXKIN
           ELSE
             CASE WHEN cmnInvoiceshikoymdflg = 0 AND(iKaishuhohokbn <> '7' AND cmnSingldnpyflg = '0') THEN -- インボイス施行日前かつ代引きでない(回収方法区分が"7"(代引)以外かつ単体伝票締フラグOFF)の場合は明細の合算値
               sum_rec.NOHINSHOHITAXKIN
             ELSE
               cmnShimeippantaxkin + cmnShimekeigentaxkin
             END
           END -- 納品消費税金額
-- インボイス対応変更 end ABI
-- インボイス対応追加 start ABI
         , NOHINIPPANNUKIKIN = cmnNohinippannukikin -- 納品一般税抜金額
         , NOHINIPPANTAXKIN =
           CASE WHEN cmnShimeippantaxkin IS NULL THEN
              cmnNohinippantaxkin
           ELSE
              cmnShimeippantaxkin
           END -- 納品一般消費税金額
         , NOHINKEIGENNUKIKIN = cmnNohinkeigennukikin -- 納品軽減税抜金額
         , NOHINKEIGENTAXKIN =
           CASE WHEN cmnShimekeigentaxkin IS NULL THEN
              cmnNohinkeigentaxkin
           ELSE
              cmnShimekeigentaxkin
           END -- 納品軽減消費税金額
         , NOHINHIKAZEIURIIKIN = cmnNohinhikazeiuriikin -- 納品非課税金額
         , NOHINFUKAZEIURIIKIN = cmnNohinfukazeiuriikin -- 納品不課税売上金額
-- インボイス対応追加 end ABI
         , JHTT.URIDNPYNO = header_rec.ACPTDNPYNO -- 売上伝票番号
           /* 更新者 */
         , JHTT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
         , JHTT.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
         , JHTT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
         , JHTT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
         , JHTT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
         , JHTT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
         , JHTT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
         , JHTT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
     WHERE JHTT.APKIHONCD  = header_rec.APKIHONCD
       AND JHTT.SHOPCD     = header_rec.SHOPCD
       AND JHTT.ACPTDNPYNO = header_rec.ACPTDNPYNO
       AND JHTT.EDANO      = header_rec.EDANO
       AND JHTT.YUKOKBN = '1'
    ;
    IF SQL%ROWCOUNT = 0 THEN
       INSERT INTO S04T001T (
              APKIHONCD    -- ＡＰ基本コード
            , SHOPCD -- ショップコード
            , ACPTDNPYNO -- 受注伝票番号
            , EDANO -- 枝番号
            , DLVRYTRGTFLG -- 配達対象フラグ
            , DNPYKBN -- 伝票区分
            , TORIHIKIKBN -- 取引区分
            , ACPTKBN -- 受注区分
            , ACPTKBNSHS -- 受注区分詳細
            , TOKUISAKICD -- 得意先コード
            , ACPTDD -- 受注日
            , ACPTTNSHAAPKIHONCD -- 受注担当者ＡＰ基本コード
            , ACPTTNSHASHOPCD -- 受注担当者ショップコード
            , ACPTTNSHACD -- 受注担当者コード
            , DLVRYYTIDD -- 配達予定日
            , DLVRYJISSEKIDD -- 配達実績日
            , DLVRYDD -- 配達日
            , DLVRYKIBOJIKAN -- 配達希望時間
            , DLVRYKIBOJIKANSHRY -- 配達希望時間終了
            , DLVRYKIBOJIKANKBN -- 配達希望時間区分
            , FZAIOKIKANOFLG -- 不在置き可能フラグ
            , FZAIOKIJISSEKIFLG -- 不在置き実績フラグ
            , DLVRYTNSHACD -- 配達担当者コード
            , DLVRYKANKATSUAPKIHONCD -- 配達管轄ＡＰ基本コード
            , DLVRYKANKATSUSHOPCD -- 配達管轄ショップコード
            , DLVRYKYOTENCD -- 配達拠点コード
            , DLVRYAREACD -- 配達エリアコード
            , DLVRYKBN -- 配達区分
            , DLVRYCMPNYCD -- 配達会社コード
            , AZUKARIBTLN1N2RITTORUHNS -- 預かりボトル１２Ｌ本数
            , KYBTLN1N2RITTORUHNS -- 回収予定ボトル１２Ｌ本数
            , KZBTLN1N2RITTORUHNS -- 回収実績ボトル１２Ｌ本数
            , AZUKARIBTLN8RITTORUHNS -- 預かりボトル８Ｌ本数
            , KYBTLN8RITTORUHNS -- 回収予定ボトル８Ｌ本数
            , KZBTLN8RITTORUHNS -- 回収実績ボトル８Ｌ本数
            , PRNTMEMON1 -- 印刷メモ１
            , PRNTMEMON2 -- 印刷メモ２
            , PRNTMEMON3 -- 印刷メモ３
            , ACPTSHIMEPRCSDD -- 受注締処理日
            , ACPTSHIMETNSHAAPKIHONCD -- 受注締担当者ＡＰ基本コード
            , ACPTSHIMETNSHASHOPCD -- 受注締担当者ショップコード
            , ACPTSHIMEPRCSTNSHACD -- 受注締処理担当者コード
            , DLVRYJISSEKIPRCSDD -- 配達実績処理日
            , DLVRYJISSEKIMONOAPKIHONCD -- 配達実績者ＡＰ基本コード
            , DLVRYJISSEKIMONOSHOPCD -- 配達実績者ショップコード
            , DLVRYJISSEKIPRCSTNSHACD -- 配達実績処理担当者コード
            , URISHIMEPRCSDD -- 売上締処理日
            , GETSUJISHIMEDYDM -- 月次締年月
            , GETSUJISHIMEDD -- 月次締日
            , GETSUJIPRCSJISSIDD -- 月次処理実施日
            , DLVRYJUN -- 配達順
            , NOSHOPRNTTRGTFLG -- 納品書印刷対象フラグ
            , NOSHOPRNTSUMIFLG -- 納品書印刷済フラグ
            , NOSHOPRNTDD -- 納品書印刷日
            , NOSHOHIKAEPRNTTRGTFLG -- 納品書控印刷対象フラグ
            , NOSHOHIKAEPRNTSUMIFLG -- 納品書控印刷済フラグ
            , NOSHOHIKAEPRNTDD -- 納品書控印刷日
            , NYKNHYOPRNTTRGTFLG -- 入金票印刷対象フラグ
            , NYKNHYOPRNTSUMIFLG -- 入金票印刷済フラグ
            , NYKNHYOPRNTDD -- 入金票印刷日
            , WSVSIJISHOPRNTTRGTFLG -- サーバー指示書印刷対象フラグ
            , WSVSIJISHOPRNTSUMIFLG -- サーバー指示書印刷済フラグ
            , WSVSIJISHOPRNTDD -- サーバー指示書印刷日
            , DNPYSTS -- 伝票ステータス
            , ACPTSTS -- 受注ステータス
            , DLVRYSTS -- 配達ステータス
            , DATSKSMOTOKBN -- データ作成元区分
            , SKSMOTOKANRIKBN -- 作成元管理区分
            , SKSMOTOKANRINO -- 作成元管理番号
            , ACPTHIKIATESAKIAPKIHONCD -- 受注引当先ＡＰ基本コード
            , ACPTHIKIATESAKISHOPCD -- 受注引当先ショップコード
            , ACPTHIKIATESAKIKYOTENCD -- 受注引当先拠点コード
            , DLVRYYTIRGRAYUSYOHNS -- 配達予定レギュラー有償本数
            , DLVRYYTIRGRAMUSHOHNS -- 配達予定レギュラー無償本数
            , DLVRYYTISRMYUSYOHNS -- 配達予定スリム有償本数
            , DLVRYYTISRMMUSHOHNS -- 配達予定スリム無償本数
            , DLVRYYTISONOTAYUSYOHNS -- 配達予定その他有償本数
            , DLVRYYTISONOTAMUSHOHNS -- 配達予定その他無償本数
            , DLVRYYTISEIHINMIZUURIKIN -- 配達予定製品水売上金額
            , DLVRYYTISONOTAURIKIN -- 配達予定その他売上金額
            , DLVRYJISSEKIRGRAYUSYOHNS -- 配達実績レギュラー有償本数
            , DLVRYJISSEKIRGRAMUSHOHNS -- 配達実績レギュラー無償本数
            , DLVRYJISSEKISRMYUSYOHNS -- 配達実績スリム有償本数
            , DLVRYJISSEKISRMMUSHOHNS -- 配達実績スリム無償本数
            , DLVRYJISSEKISTYSHNS -- 配達実績その他有償本数
            , DLVRYJISSEKISTMSHNS -- 配達実績その他無償本数
            , DLVRYJISSEKISHSUAKIN -- 配達実績製品水売上金額
            , DLVRYJISSEKISONOTAURIKIN -- 配達実績その他売上金額
            , ACPTKIN -- 受注金額
            , ACPTSHOHITAXKIN -- 受注消費税金額
-- インボイス対応追加 start ABI
            , ACPTIPPANNUKIKIN   -- 受注一般税抜金額
            , ACPTIPPANTAXKIN    -- 受注一般消費税金額
            , ACPTKEIGENNUKIKIN  -- 受注軽減税抜金額
            , ACPTKEIGENTAXKIN   -- 受注軽減消費税金額
            , ACPTHIKAZEIURIIKIN -- 受注非課税金額
            , ACPTFUKAZEIURIIKIN -- 受注不課税売上金額
-- インボイス対応追加 end ABI
            , NOHINKIN -- 納品金額
            , NOHINSHOHITAXKIN -- 納品消費税金額
-- インボイス対応追加 start ABI
            , NOHINIPPANNUKIKIN   -- 納品一般税抜金額
            , NOHINIPPANTAXKIN    -- 納品一般消費税金額
            , NOHINKEIGENNUKIKIN  -- 納品軽減税抜金額
            , NOHINKEIGENTAXKIN   -- 納品軽減消費税金額
            , NOHINHIKAZEIURIIKIN -- 納品非課税金額
            , NOHINFUKAZEIURIIKIN -- 納品不課税売上金額
-- インボイス対応追加 end ABI
            , KURERURITTORUSTUP -- クレールＬ設置
            , KURERURITTORUHIKIAGE -- クレールＬ引上
            , KURERUESUSTUP -- クレールＳ設置
            , KURERUESUHIKIAGE -- クレールＳ引上
            , SONOTASTUP -- その他設置
            , SONOTAHIKIAGE -- その他引上
            , TAXKEISANKBN -- 税計算区分
            , ZNKDLVRYDD -- 前回配達日
            , ZNKDLVRYHNS -- 前回配達本数
            , JIKAIDLVRYYTIDD -- 次回配達予定日
            , ZNKSHIMEDD -- 前回締日
            , ZNKKURIKOSHIKIN -- 前回繰越金額
            , ACPTTHDAIBIKIFLG -- 受注時代引フラグ
            , ACPTRCPTKEIROKBN -- 受注受付経路区分
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
            , ITAKUAITEAPKIHONCD -- 委託相手ＡＰ基本コード
            , ITAKUAITESHOPCD -- 委託相手ショップコード
            , ITAKUAITEACPTDNPYNO -- 委託相手伝票番号
            , ITAKUAITEEDANO -- 委託相手枝番号
            , URIDNPYNO -- 売上伝票番号
       ) VALUES (
                header_rec.APKIHONCD    -- ＡＰ基本コード
              , header_rec.SHOPCD -- ショップコード
              , header_rec.ACPTDNPYNO -- 受注伝票番号
              , header_rec.EDANO -- 枝番号
              , '1' -- 配達対象フラグ
              -- 伝票区分
              , ( CASE WHEN tokuisakiMST.TOKUISAKIKBN = '9' THEN '15'
                       ELSE '14'
                  END )
              -- 取引区分
              , ( CASE WHEN tokuisakiMST.TOKUISAKIKBN = '9' THEN '3'
                       ELSE '1'
                  END )
              , header_rec.ACPTKBN -- 受注区分
              , header_rec.ACPTKBNSHS -- 受注区分詳細
              , header_rec.TOKUISAKICD -- 得意先コード
              , header_rec.DLVRYJISSEKIDD -- 受注日
              , userinfo_rec.APKIHONCD -- 受注担当者ＡＰ基本コード
              , userinfo_rec.SHOPCD -- 受注担当者ショップコード
              , userinfo_rec.TNSHACD -- 受注担当者コード
              , NULL -- 配達予定日
              , header_rec.DLVRYJISSEKIDD -- 配達実績日
              , header_rec.DLVRYJISSEKIDD -- 配達日
              , NULL -- 配達希望時間
              , NULL -- 配達希望時間終了
              , NULL -- 配達希望時間区分
              , tokuisakiMST.FZAIOKIKANOFLG  -- 不在置き可能フラグ
              , header_rec.FZAIOKIJISSEKIFLG -- 不在置き実績フラグ
              , header_rec.DLVRYTNSHACD -- 配達担当者コード
              , tokuisakiMST.DLVRYKANKATSUAPKIHONCD -- 配達管轄ＡＰ基本コード
              , tokuisakiMST.DLVRYKANKATSUSHOPCD    -- 配達管轄ショップコード
              , tokuisakiMST.DLVRYKANKATSUKYOTENCD  -- 配達拠点コード
              , tokuisakiMST.DLVRYAREACD  -- 配達エリアコード
              , header_rec.DLVRYKBN     -- 配達区分
              , header_rec.DLVRYCMPNYCD -- 配達会社コード
              , tokuisakiJST.AZUKARIBTLN1N2RITTORUHNS -- 預かりボトル１２Ｌ本数
              , 0 -- 回収予定ボトル１２Ｌ本数
              , header_rec.KZBTLN1N2RITTORUHNS -- 回収実績ボトル１２Ｌ本数
              , tokuisakiJST.AZUKARIBTLN8RITTORUHNS -- 預かりボトル８Ｌ本数
              , 0 -- 回収予定ボトル８Ｌ本数
              , header_rec.KZBTLN8RITTORUHNS -- 回収実績ボトル８Ｌ本数
              , tokuisakiMST.PRNTMEMON1 -- 印刷メモ１
              , tokuisakiMST.PRNTMEMON2 -- 印刷メモ２
              , tokuisakiMST.PRNTMEMON3 -- 印刷メモ３
              , userinfo_rec.UNYOYMD   -- 受注締処理日
              , userinfo_rec.APKIHONCD -- 受注締担当者ＡＰ基本コード
              , userinfo_rec.SHOPCD    -- 受注締担当者ショップコード
              , userinfo_rec.TNSHACD   -- 受注締処理担当者コード
              , userinfo_rec.UNYOYMD   -- 配達実績処理日
              , userinfo_rec.APKIHONCD -- 配達実績者ＡＰ基本コード
              , userinfo_rec.SHOPCD    -- 配達実績者ショップコード
              , userinfo_rec.TNSHACD   -- 配達実績処理担当者コード
              , '00000000' -- 売上締処理日
              , NULL -- 月次締年月
              , NULL -- 月次締日
              , NULL -- 月次処理実施日
              , tokuisakiMST.DLVRYJUN -- 配達順
              , '1'  -- 納品書印刷対象フラグ
              , '0'  -- 納品書印刷済フラグ
              , NULL -- 納品書印刷日
              , '1'  -- 納品書控印刷対象フラグ
              , '0'  -- 納品書控印刷済フラグ
              , NULL -- 納品書控印刷日
              -- 入金票印刷対象フラグ
              , ( CASE WHEN iKaishuhohokbn = '7' THEN '1'
                       ELSE '0'
                  END )
              , '0' -- 入金票印刷済フラグ
              , NULL -- 入金票印刷日
              -- サーバー指示書印刷対象フラグ
              , ( CASE WHEN header_rec.ACPTKBN = '1' THEN '0'
                       ELSE '1'
                  END )
              , '0' -- サーバー指示書印刷済フラグ
              , NULL -- サーバー指示書印刷日
              , header_rec.DLVRYSTS -- 伝票ステータス
              , '02' -- 受注ステータス
              , header_rec.DLVRYSTS -- 配達ステータス
              , DATSKSMOTOKBN_NEW -- データ作成元区分
              , NULL -- 作成元管理区分
              , NULL -- 作成元管理番号
              , tokuisakiMST.DLVRYKANKATSUAPKIHONCD -- 受注引当先ＡＰ基本コード
              , tokuisakiMST.DLVRYKANKATSUSHOPCD -- 受注引当先ショップコード
              , tokuisakiMST.DLVRYKANKATSUKYOTENCD -- 受注引当先拠点コード
              , 0 -- 配達予定レギュラー有償本数
              , 0 -- 配達予定レギュラー無償本数
              , 0 -- 配達予定スリム有償本数
              , 0 -- 配達予定スリム無償本数
              , 0 -- 配達予定その他有償本数
              , 0 -- 配達予定その他無償本数
              , 0 -- 配達予定製品水売上金額
              , 0 -- 配達予定その他売上金額
              , sum_rec.DLVRYRGRAYUSYOHNS -- 配達実績レギュラー有償本数
              , sum_rec.DLVRYRGRAMUSHOHNS -- 配達実績レギュラー無償本数
              , sum_rec.DLVRYSRMYUSYOHNS -- 配達実績スリム有償本数
              , sum_rec.DLVRYSRMMUSHOHNS -- 配達実績スリム無償本数
              , sum_rec.DLVRYSTYSHNS -- 配達実績その他有償本数
              , sum_rec.DLVRYSTMSHNS -- 配達実績その他無償本数
              , sum_rec.DLVRYSHSUAKIN     -- 配達実績製品水売上金額
              , sum_rec.DLVRYSONOTAURIKIN -- 配達実績その他売上金額
              , 0 -- 受注金額
              , 0 -- 受注消費税金額
-- インボイス対応追加 start ABI
              , cmnAcptippannukikin   -- 受注一般税抜金額
              , cmnAcptippantaxkin    -- 受注一般消費税金額
              , cmnAcptkeigennukikin  -- 受注軽減税抜金額
              , cmnAcptkeigentaxkin   -- 受注軽減消費税金額
              , cmnAcpthikazeiuriikin -- 受注非課税金額
              , cmnAcptfukazeiuriikin -- 受注不課税売上金額
-- インボイス対応追加 end ABI
              , sum_rec.NOHINKIN -- 納品金額
-- インボイス対応変更 start ABI
              --, sum_rec.NOHINSHOHITAXKIN -- 納品消費税金額
              , CASE WHEN cmnShimeippantaxkin IS NULL OR cmnShimekeigentaxkin IS NULL THEN
                   sum_rec.NOHINSHOHITAXKIN
                ELSE
                  CASE WHEN cmnInvoiceshikoymdflg = 0 AND(iKaishuhohokbn <> '7' AND cmnSingldnpyflg = '0') THEN -- インボイス施行日前かつ代引きでない(回収方法区分が"7"(代引)以外かつ単体伝票締フラグOFF)の場合は明細の合算値
                    sum_rec.NOHINSHOHITAXKIN
                  ELSE
                    cmnShimeippantaxkin + cmnShimekeigentaxkin
                  END
                END -- 納品消費税金額
-- インボイス対応変更 end ABI
-- インボイス対応追加 start ABI
              , cmnNohinippannukikin -- 納品一般税抜金額
              , CASE WHEN cmnShimeippantaxkin IS NULL THEN
                   cmnNohinippantaxkin
                ELSE
                   cmnShimeippantaxkin
                END -- 納品一般消費税金額
              , cmnNohinkeigennukikin -- 納品軽減税抜金額
              , CASE WHEN cmnShimekeigentaxkin IS NULL THEN
                   cmnNohinkeigentaxkin
                ELSE
                   cmnShimekeigentaxkin
                END -- 納品軽減消費税金額
              , cmnNohinhikazeiuriikin -- 納品非課税金額
              , cmnNohinfukazeiuriikin -- 納品不課税売上金額
-- インボイス対応追加 end ABI
              , sum_rec.KURERURITTORUSTUP -- クレールＬ設置
              , sum_rec.KURERURITTORUHIKIAGE -- クレールＬ引上
              , sum_rec.KURERUESUSTUP -- クレールＳ設置
              , sum_rec.KURERUESUHIKIAGE -- クレールＳ引上
              , sum_rec.SONOTASTUP -- その他設置
              , sum_rec.SONOTAHIKIAGE -- その他引上
              , iTaxkeisankbn -- 税計算区分
              , tokuisakiJST.ZNKDLVRYDD -- 前回配達日
              , 0 -- 前回配達本数
              , NULL -- 次回配達予定日
              , NULL -- 前回締日
              , NULL -- 前回繰越金額
-- インボイス対応変更 start ABI
              --, '0'  -- 受注時代引フラグ
              , ( CASE WHEN iKaishuhohokbn = '7' THEN '1'
                       ELSE '0'
                  END ) -- 受注時代引フラグ
-- インボイス対応変更 end ABI
              , '99' -- 受注受付経路区分：その他外部連携
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
              , NULL -- 委託相手ＡＰ基本コード
              , NULL -- 委託相手ショップコード
              , NULL -- 委託相手伝票番号
              , NULL -- 委託相手枝番号
              , header_rec.ACPTDNPYNO -- 売上伝票番号
       );

-- インボイス対応追加 start ABI
-- 更新前受注配達テーブルデータが設定されていない場合、登録した内容を更新前受注配達テーブルデータに設定する（代引きの予定外受注を考慮）
       IF acpt_old.ACPTDNPYNO IS NULL THEN
          acpt_old.APKIHONCD                 := header_rec.APKIHONCD;                  -- ＡＰ基本コード
          acpt_old.SHOPCD                    := header_rec.SHOPCD;                     -- ショップコード
          acpt_old.ACPTDNPYNO                := header_rec.ACPTDNPYNO;                 -- 受注伝票番号
          acpt_old.EDANO                     := header_rec.EDANO;                      -- 枝番号
          acpt_old.DLVRYTRGTFLG              := '1';                                   -- 配達対象フラグ
          acpt_old.DNPYKBN                   := ( CASE WHEN tokuisakiMST.TOKUISAKIKBN = '9' THEN '15' ELSE '14' END ); -- 伝票区分
          acpt_old.TORIHIKIKBN               := ( CASE WHEN tokuisakiMST.TOKUISAKIKBN = '9' THEN '3' ELSE '1' END ); -- 取引区分
          acpt_old.ACPTKBN                   := header_rec.ACPTKBN;                    -- 受注区分
          acpt_old.ACPTKBNSHS                := header_rec.ACPTKBNSHS;                 -- 受注区分詳細
          acpt_old.TOKUISAKICD               := header_rec.TOKUISAKICD;                -- 得意先コード
          acpt_old.ACPTDD                    := header_rec.DLVRYJISSEKIDD;             -- 受注日
          acpt_old.ACPTTNSHAAPKIHONCD        := userinfo_rec.APKIHONCD;                -- 受注担当者ＡＰ基本コード
          acpt_old.ACPTTNSHASHOPCD           := userinfo_rec.SHOPCD;                   -- 受注担当者ショップコード
          acpt_old.ACPTTNSHACD               := userinfo_rec.TNSHACD;                  -- 受注担当者コード
          acpt_old.DLVRYYTIDD                := NULL;                                  -- 配達予定日
          acpt_old.DLVRYJISSEKIDD            := header_rec.DLVRYJISSEKIDD;             -- 配達実績日
          acpt_old.DLVRYDD                   := header_rec.DLVRYJISSEKIDD;             -- 配達日
          acpt_old.DLVRYKIBOJIKAN            := NULL;                                  -- 配達希望時間
          acpt_old.DLVRYKIBOJIKANSHRY        := NULL;                                  -- 配達希望時間終了
          acpt_old.DLVRYKIBOJIKANKBN         := NULL;                                  -- 配達希望時間区分
          acpt_old.FZAIOKIKANOFLG            := tokuisakiMST.FZAIOKIKANOFLG;           -- 不在置き可能フラグ
          acpt_old.FZAIOKIJISSEKIFLG         := header_rec.FZAIOKIJISSEKIFLG;          -- 不在置き実績フラグ
          acpt_old.DLVRYTNSHACD              := header_rec.DLVRYTNSHACD;               -- 配達担当者コード
          acpt_old.DLVRYKANKATSUAPKIHONCD    := tokuisakiMST.DLVRYKANKATSUAPKIHONCD;   -- 配達管轄ＡＰ基本コード
          acpt_old.DLVRYKANKATSUSHOPCD       := tokuisakiMST.DLVRYKANKATSUSHOPCD;      -- 配達管轄ショップコード
          acpt_old.DLVRYKYOTENCD             := tokuisakiMST.DLVRYKANKATSUKYOTENCD;    -- 配達拠点コード
          acpt_old.DLVRYAREACD               := tokuisakiMST.DLVRYAREACD;              -- 配達エリアコード
          acpt_old.DLVRYKBN                  := header_rec.DLVRYKBN;                   -- 配達区分
          acpt_old.DLVRYCMPNYCD              := header_rec.DLVRYCMPNYCD;               -- 配達会社コード
          acpt_old.AZUKARIBTLN1N2RITTORUHNS  := tokuisakiJST.AZUKARIBTLN1N2RITTORUHNS; -- 預かりボトル１２Ｌ本数
          acpt_old.KYBTLN1N2RITTORUHNS       := 0;                                     -- 回収予定ボトル１２Ｌ本数
          acpt_old.KZBTLN1N2RITTORUHNS       := header_rec.KZBTLN1N2RITTORUHNS;        -- 回収実績ボトル１２Ｌ本数
          acpt_old.AZUKARIBTLN8RITTORUHNS    := tokuisakiJST.AZUKARIBTLN8RITTORUHNS;   -- 預かりボトル８Ｌ本数
          acpt_old.KYBTLN8RITTORUHNS         := 0;                                     -- 回収予定ボトル８Ｌ本数
          acpt_old.KZBTLN8RITTORUHNS         := header_rec.KZBTLN8RITTORUHNS;          -- 回収実績ボトル８Ｌ本数
          acpt_old.PRNTMEMON1                := tokuisakiMST.PRNTMEMON1;               -- 印刷メモ１
          acpt_old.PRNTMEMON2                := tokuisakiMST.PRNTMEMON2;               -- 印刷メモ２
          acpt_old.PRNTMEMON3                := tokuisakiMST.PRNTMEMON3;               -- 印刷メモ３
          acpt_old.ACPTSHIMEPRCSDD           := userinfo_rec.UNYOYMD;                  -- 受注締処理日
          acpt_old.ACPTSHIMETNSHAAPKIHONCD   := userinfo_rec.APKIHONCD;                -- 受注締担当者ＡＰ基本コード
          acpt_old.ACPTSHIMETNSHASHOPCD      := userinfo_rec.SHOPCD;                   -- 受注締担当者ショップコード
          acpt_old.ACPTSHIMEPRCSTNSHACD      := userinfo_rec.TNSHACD;                  -- 受注締処理担当者コード
          acpt_old.DLVRYJISSEKIPRCSDD        := userinfo_rec.UNYOYMD;                  -- 配達実績処理日
          acpt_old.DLVRYJISSEKIMONOAPKIHONCD := userinfo_rec.APKIHONCD;                -- 配達実績者ＡＰ基本コード
          acpt_old.DLVRYJISSEKIMONOSHOPCD    := userinfo_rec.SHOPCD;                   -- 配達実績者ショップコード
          acpt_old.DLVRYJISSEKIPRCSTNSHACD   := userinfo_rec.TNSHACD;                  -- 配達実績処理担当者コード
          acpt_old.URISHIMEPRCSDD            := '00000000';                            -- 売上締処理日
          acpt_old.GETSUJISHIMEDYDM          := NULL;                                  -- 月次締年月
          acpt_old.GETSUJISHIMEDD            := NULL;                                  -- 月次締日
          acpt_old.GETSUJIPRCSJISSIDD        := NULL;                                  -- 月次処理実施日
          acpt_old.DLVRYJUN                  := tokuisakiMST.DLVRYJUN;                 -- 配達順
          acpt_old.NOSHOPRNTTRGTFLG          := '1';                                   -- 納品書印刷対象フラグ
          acpt_old.NOSHOPRNTSUMIFLG          := '0';                                   -- 納品書印刷済フラグ
          acpt_old.NOSHOPRNTDD               := NULL;                                  -- 納品書印刷日
          acpt_old.NOSHOHIKAEPRNTTRGTFLG     := '1';                                   -- 納品書控印刷対象フラグ
          acpt_old.NOSHOHIKAEPRNTSUMIFLG     := '0';                                   -- 納品書控印刷済フラグ
          acpt_old.NOSHOHIKAEPRNTDD          := NULL;                                  -- 納品書控印刷日
          acpt_old.NYKNHYOPRNTTRGTFLG        := ( CASE WHEN iKaishuhohokbn = '7' THEN '1' ELSE '0' END ); -- 入金票印刷対象フラグ
          acpt_old.NYKNHYOPRNTSUMIFLG        := '0';                                   -- 入金票印刷済フラグ
          acpt_old.NYKNHYOPRNTDD             := NULL;                                  -- 入金票印刷日
          acpt_old.WSVSIJISHOPRNTTRGTFLG     := ( CASE WHEN header_rec.ACPTKBN = '1' THEN '0' ELSE '1' END ); -- サーバー指示書印刷対象フラグ
          acpt_old.WSVSIJISHOPRNTSUMIFLG     := '0';                                   -- サーバー指示書印刷済フラグ
          acpt_old.WSVSIJISHOPRNTDD          := NULL;                                  -- サーバー指示書印刷日
          acpt_old.DNPYSTS                   := header_rec.DLVRYSTS;                   -- 伝票ステータス
          acpt_old.ACPTSTS                   := '02';                                  -- 受注ステータス
          acpt_old.DLVRYSTS                  := header_rec.DLVRYSTS;                   -- 配達ステータス
          acpt_old.DATSKSMOTOKBN             := DATSKSMOTOKBN_NEW;                     -- データ作成元区分
          acpt_old.SKSMOTOKANRIKBN           := NULL;                                  -- 作成元管理区分
          acpt_old.SKSMOTOKANRINO            := NULL;                                  -- 作成元管理番号
          acpt_old.ACPTHIKIATESAKIAPKIHONCD  := tokuisakiMST.DLVRYKANKATSUAPKIHONCD;   -- 受注引当先ＡＰ基本コード
          acpt_old.ACPTHIKIATESAKISHOPCD     := tokuisakiMST.DLVRYKANKATSUSHOPCD;      -- 受注引当先ショップコード
          acpt_old.ACPTHIKIATESAKIKYOTENCD   := tokuisakiMST.DLVRYKANKATSUKYOTENCD;    -- 受注引当先拠点コード
          acpt_old.DLVRYYTIRGRAYUSYOHNS      := 0;                                     -- 配達予定レギュラー有償本数
          acpt_old.DLVRYYTIRGRAMUSHOHNS      := 0;                                     -- 配達予定レギュラー無償本数
          acpt_old.DLVRYYTISRMYUSYOHNS       := 0;                                     -- 配達予定スリム有償本数
          acpt_old.DLVRYYTISRMMUSHOHNS       := 0;                                     -- 配達予定スリム無償本数
          acpt_old.DLVRYYTISONOTAYUSYOHNS    := 0;                                     -- 配達予定その他有償本数
          acpt_old.DLVRYYTISONOTAMUSHOHNS    := 0;                                     -- 配達予定その他無償本数
          acpt_old.DLVRYYTISEIHINMIZUURIKIN  := 0;                                     -- 配達予定製品水売上金額
          acpt_old.DLVRYYTISONOTAURIKIN      := 0;                                     -- 配達予定その他売上金額
          acpt_old.DLVRYJISSEKIRGRAYUSYOHNS  := sum_rec.DLVRYRGRAYUSYOHNS;             -- 配達実績レギュラー有償本数
          acpt_old.DLVRYJISSEKIRGRAMUSHOHNS  := sum_rec.DLVRYRGRAMUSHOHNS;             -- 配達実績レギュラー無償本数
          acpt_old.DLVRYJISSEKISRMYUSYOHNS   := sum_rec.DLVRYSRMYUSYOHNS;              -- 配達実績スリム有償本数
          acpt_old.DLVRYJISSEKISRMMUSHOHNS   := sum_rec.DLVRYSRMMUSHOHNS;              -- 配達実績スリム無償本数
          acpt_old.DLVRYJISSEKISTYSHNS       := sum_rec.DLVRYSTYSHNS;                  -- 配達実績その他有償本数
          acpt_old.DLVRYJISSEKISTMSHNS       := sum_rec.DLVRYSTMSHNS;                  -- 配達実績その他無償本数
          acpt_old.DLVRYJISSEKISHSUAKIN      := sum_rec.DLVRYSHSUAKIN;                 -- 配達実績製品水売上金額
          acpt_old.DLVRYJISSEKISONOTAURIKIN  := sum_rec.DLVRYSONOTAURIKIN;             -- 配達実績その他売上金額
          acpt_old.ACPTKIN                   := 0;                                     -- 受注金額
          acpt_old.ACPTSHOHITAXKIN           := 0;                                     -- 受注消費税金額
          acpt_old.ACPTIPPANNUKIKIN          := cmnAcptippannukikin;                   -- 受注一般税抜金額
          acpt_old.ACPTIPPANTAXKIN           := cmnAcptippantaxkin;                    -- 受注一般消費税金額
          acpt_old.ACPTKEIGENNUKIKIN         := cmnAcptkeigennukikin;                  -- 受注軽減税抜金額
          acpt_old.ACPTKEIGENTAXKIN          := cmnAcptkeigentaxkin;                   -- 受注軽減消費税金額
          acpt_old.ACPTHIKAZEIURIIKIN        := cmnAcpthikazeiuriikin;                 -- 受注非課税金額
          acpt_old.ACPTFUKAZEIURIIKIN        := cmnAcptfukazeiuriikin;                 -- 受注不課税売上金額
          acpt_old.NOHINKIN                  := sum_rec.NOHINKIN;                      -- 納品金額
          acpt_old.NOHINSHOHITAXKIN          := ( CASE WHEN cmnShimeippantaxkin IS NULL OR cmnShimekeigentaxkin IS NULL THEN sum_rec.NOHINSHOHITAXKIN ELSE
                                                       CASE WHEN cmnInvoiceshikoymdflg = 0 THEN sum_rec.NOHINSHOHITAXKIN ELSE cmnShimeippantaxkin + cmnShimekeigentaxkin END
                                                  END ); -- 納品消費税金額
          acpt_old.NOHINIPPANNUKIKIN         := cmnNohinippannukikin;                  -- 納品一般税抜金額
          acpt_old.NOHINIPPANTAXKIN          := ( CASE WHEN cmnShimeippantaxkin IS NULL THEN cmnNohinippantaxkin ELSE cmnShimeippantaxkin END ); -- 納品一般消費税金額
          acpt_old.NOHINKEIGENNUKIKIN        := cmnNohinkeigennukikin;                 -- 納品軽減税抜金額
          acpt_old.NOHINKEIGENTAXKIN         := ( CASE WHEN cmnShimekeigentaxkin IS NULL THEN cmnNohinkeigentaxkin ELSE cmnShimekeigentaxkin END ); -- 納品軽減消費税金額
          acpt_old.NOHINHIKAZEIURIIKIN       := cmnNohinhikazeiuriikin;                -- 納品非課税金額
          acpt_old.NOHINFUKAZEIURIIKIN       := cmnNohinfukazeiuriikin;                -- 納品不課税売上金額
          acpt_old.KURERURITTORUSTUP         := sum_rec.KURERURITTORUSTUP;             -- クレールＬ設置
          acpt_old.KURERURITTORUHIKIAGE      := sum_rec.KURERURITTORUHIKIAGE;          -- クレールＬ引上
          acpt_old.KURERUESUSTUP             := sum_rec.KURERUESUSTUP;                 -- クレールＳ設置
          acpt_old.KURERUESUHIKIAGE          := sum_rec.KURERUESUHIKIAGE;              -- クレールＳ引上
          acpt_old.SONOTASTUP                := sum_rec.SONOTASTUP;                    -- その他設置
          acpt_old.SONOTAHIKIAGE             := sum_rec.SONOTAHIKIAGE;                 -- その他引上
          acpt_old.TAXKEISANKBN              := iTaxkeisankbn;                         -- 税計算区分
          acpt_old.ZNKDLVRYDD                := tokuisakiJST.ZNKDLVRYDD;               -- 前回配達日
          acpt_old.ZNKDLVRYHNS               := 0;                                     -- 前回配達本数
          acpt_old.JIKAIDLVRYYTIDD           := NULL;                                  -- 次回配達予定日
          acpt_old.ZNKSHIMEDD                := NULL;                                  -- 前回締日
          acpt_old.ZNKKURIKOSHIKIN           := NULL;                                  -- 前回繰越金額
          acpt_old.ACPTTHDAIBIKIFLG          := ( CASE WHEN iKaishuhohokbn = '7' THEN '1' ELSE '0' END ); -- 受注時代引フラグ
          acpt_old.ACPTRCPTKEIROKBN          := '99';                                  -- 受注受付経路区分
          acpt_old.YUKOKBN                   := '1';                                   -- 有効区分
          acpt_old.TOSHAAPKIHONCD            := userinfo_rec.APKIHONCD;                -- 登録者ＡＰ基本コード
          acpt_old.TOSHASHOPCD               := userinfo_rec.SHOPCD;                   -- 登録者ショップコード
          acpt_old.TOSHACD                   := userinfo_rec.TNSHACD;                  -- 登録者コード
          acpt_old.INPTSYSTMYMD              := userinfo_rec.SYSTMYMD;                 -- 登録システム日付
          acpt_old.INPTSYSTMJIKAN            := userinfo_rec.SYSTMJIKAN;               -- 登録システム時間
          acpt_old.INPTUNYOYMD               := userinfo_rec.UNYOYMD;                  -- 登録運用日付
          acpt_old.INPTIPADRSE               := userinfo_rec.IPADRSE;                  -- 登録ＩＰアドレス
          acpt_old.INPTPGMID                 := userinfo_rec.PGMID;                    -- 登録プログラムＩＤ
          acpt_old.KOSHAAPKIHONCD            := userinfo_rec.APKIHONCD;                -- 更新者ＡＰ基本コード
          acpt_old.KOSHASHOPCD               := userinfo_rec.SHOPCD;                   -- 更新者ショップコード
          acpt_old.KOSHACD                   := userinfo_rec.TNSHACD;                  -- 更新者コード
          acpt_old.KSHNSYSTMYMD              := userinfo_rec.SYSTMYMD;                 -- 更新システム日付
          acpt_old.KSHNSYSTMJIKAN            := userinfo_rec.SYSTMJIKAN;               -- 更新システム時間
          acpt_old.KSHNUNYOYMD               := userinfo_rec.UNYOYMD;                  -- 更新運用日付
          acpt_old.KSHNIPADRSE               := userinfo_rec.IPADRSE;                  -- 更新ＩＰアドレス
          acpt_old.KSHNPGMID                 := userinfo_rec.PGMID;                    -- 更新プログラムＩＤ
          acpt_old.ITAKUAITEAPKIHONCD        := NULL;                                  -- 委託相手ＡＰ基本コード
          acpt_old.ITAKUAITESHOPCD           := NULL;                                  -- 委託相手ショップコード
          acpt_old.ITAKUAITEACPTDNPYNO       := NULL;                                  -- 委託相手伝票番号
          acpt_old.ITAKUAITEEDANO            := NULL;                                  -- 委託相手枝番号
          acpt_old.URIDNPYNO                 := header_rec.ACPTDNPYNO;                 -- 売上伝票番号
       END IF;
-- インボイス対応追加 end ABI
    END IF;

    -- ポイント制度対応追加 start ABI
    -- 紐づけテーブルに有効設定があれば(ポイント発生)予定発生ポイント登録
    IF cmnRegistKanrino IS NOT NULL THEN
      registPointHeaderTable(cmnRegistKanrino
      , header_rec);
    END IF;
    -- ポイント制度対応追加 end ABI

-- インボイス対応追加 start ABI
    -- 変数の初期化
    cmnAcptippannukikin    := 0; -- 受注一般税抜金額
    cmnAcptippantaxkin     := 0; -- 受注一般消費税金額
    cmnAcptkeigennukikin   := 0; -- 受注軽減税抜金額
    cmnAcptkeigentaxkin    := 0; -- 受注軽減消費税金額
    cmnAcpthikazeiuriikin  := 0; -- 受注非課税金額
    cmnAcptfukazeiuriikin  := 0; -- 受注不課税売上金額
    cmnNohinippannukikin   := 0; -- 納品一般税抜金額
    cmnNohinippantaxkin    := 0; -- 納品一般消費税金額
    cmnNohinkeigennukikin  := 0; -- 納品軽減税抜金額
    cmnNohinkeigentaxkin   := 0; -- 納品軽減消費税金額
    cmnNohinhikazeiuriikin := 0; -- 納品非課税金額
    cmnNohinfukazeiuriikin := 0; -- 納品不課税売上金額
-- インボイス対応追加 end ABI

    -- 配布物更新
    FOR hfbh_rec IN SELECT_HFBH LOOP
      FOR hfbm_rec IN SELECT_HFBM LOOP
          updateHaifubutsuTKM( hfbh_rec, hfbm_rec );
      END LOOP;
    END LOOP;

    EXCEPTION
      WHEN OTHERS THEN
        IF SELECT_HFBH%ISOPEN THEN
           CLOSE SELECT_HFBH;
        END IF;
        IF SELECT_HFBM%ISOPEN THEN
           CLOSE SELECT_HFBM;
        END IF;
        RAISE_APPLICATION_ERROR(-20017, SQLERRM || '配達済受託配達テーブル作成時エラーが発生しました。');
  END processJuchuDelivered;

-- 本線対応追加 start ABI
  /*****************************************************************************/
  /* プロシージャ名：受託配達テーブル配達済更新WEB                             */
  /* 引数             ：header_rec      配達実績ワークデータヘッダ             */
  /*                  ：sum_rec         合計データ                             */
  /*                  ：tokuisakiMST    得意先マスタ                           */
  /*                  ：tokuisakiJST    得意先実績テーブル                     */
  /*                  ：iTaxkeisankbn   税計算区分                             */
  /*                  ：iKaishuhohokbn  回収方法区分                           */
  /*****************************************************************************/
  PROCEDURE processJuchuDeliveredWeb( header_rec     IN S05W001T%rowtype -- 配達実績ワークデータヘッダ
                                    , sum_rec        IN sum_rec_type     -- 合計データ
                                    , tokuisakiMST   IN S02M013T%rowtype -- 得意先マスタ
                                    , tokuisakiJST   IN S02M050T%rowtype -- 得意先実績テーブル
                                    , iTaxkeisankbn  IN VARCHAR2         -- 税計算区分
                                    , iKaishuhohokbn IN VARCHAR2         -- 回収方法区分
  ) IS
  /* CURSOR：配布物更新用ヘッダ */
  CURSOR SELECT_HFBH IS
    SELECT JHTT.*
      FROM S04T001T JHTT
     WHERE JHTT.APKIHONCD  = header_rec.APKIHONCD
       AND JHTT.SHOPCD     = header_rec.SHOPCD
       AND JHTT.ACPTDNPYNO = header_rec.ACPTDNPYNO
       AND JHTT.EDANO      = header_rec.EDANO
       AND JHTT.YUKOKBN   = '1'
  ;
  /* CURSOR：配布物更新用明細 */
  CURSOR SELECT_HFBM IS
    SELECT JHMT.*
      FROM S04T002T JHMT
     WHERE JHMT.APKIHONCD  = header_rec.APKIHONCD
       AND JHMT.SHOPCD     = header_rec.SHOPCD
       AND JHMT.ACPTDNPYNO = header_rec.ACPTDNPYNO
       AND JHMT.EDANO      = header_rec.EDANO
       AND JHMT.MEISAIKBN = '1'
       AND JHMT.YUKOKBN   = '1'
  ;

  -- ACCIS2023-7212 消費税再計算用の変数を追加
  vIppanTax NUMBER; -- 一般
  vKeigenTax NUMBER; -- 軽減

  BEGIN
    -- ACCIS2023-7212 セット商品の納品単価が再設定され納品金額に差分が生じている可能性があるため
    -- cmnNohinippannukikinやcmnNohinkeigennukikinを元に税区分ごとの消費税を再計算する
    -- cmnShimeippantaxkin、cmnNohinkeigentaxkinに値が設定されていない場合は
    -- cmnNohinippantaxkinやcmnNohinkeigentaxkinといった明細処理で算出している値が設定されるため
    -- cmnShimeippantaxkinとcmnNohinkeigentaxkinに値が設定されている場合に再計算を行い
    -- 算出した値と差異が生じている場合は単価再設定により金額が変更されたものとして
    -- 算出した値を優先する
    -- 本処理は納品時点での仮税額の整合性を担保するものであり、請求締め時に正式な税額が再計算される
    IF cmnShimeippantaxkin IS NOT NULL THEN
      -- 一般税区分の消費税再計算
      vIppanTax := Z1808B01.SyohizeiKeisan(
        iSimpleFg      => '1',
        iTaxsetteikbn  => '1',
        iTaxkbn        => '1', -- 一般
        iTaxKeisankbn  => NULL,
        iKirikaebi     => header_rec.DLVRYJISSEKIDD,
        iApKihoncd     => header_rec.APKIHONCD,
        iShopcd        => header_rec.SHOPCD,
        iSiiresakicd   => NULL,
        iSuryo         => NULL,
        iKin           => TO_CHAR(cmnNohinippannukikin)
      );
      IF cmnShimeippantaxkin <> vIppanTax THEN
        cmnShimeippantaxkin := vIppanTax;
      END IF;
    END IF;
    IF cmnShimekeigentaxkin IS NOT NULL THEN
      -- 軽減税区分の消費税再計算
      vKeigenTax := Z1808B01.SyohizeiKeisan(
        iSimpleFg      => '1',
        iTaxsetteikbn  => '1',
        iTaxkbn        => '4', -- 軽減税区分
        iTaxKeisankbn  => NULL,
        iKirikaebi     => header_rec.DLVRYJISSEKIDD,
        iApKihoncd     => header_rec.APKIHONCD,
        iShopcd        => header_rec.SHOPCD,
        iSiiresakicd   => NULL,
        iSuryo         => NULL,
        iKin           => TO_CHAR(cmnNohinkeigennukikin)
      );
      IF cmnShimekeigentaxkin <> vKeigenTax THEN
        cmnShimekeigentaxkin := vKeigenTax;
      END IF;
    END IF;

    UPDATE S04T001T JHTT
       SET JHTT.DLVRYJISSEKIDD = header_rec.DLVRYJISSEKIDD -- 配達実績日
         , JHTT.DLVRYDD        = header_rec.DLVRYJISSEKIDD -- 配達日
           -- 不在置き実績フラグ
         , JHTT.FZAIOKIJISSEKIFLG = header_rec.FZAIOKIJISSEKIFLG
         , JHTT.KZBTLN1N2RITTORUHNS = header_rec.KZBTLN1N2RITTORUHNS -- 回収実績ボトル１２Ｌ本数
         , JHTT.KZBTLN8RITTORUHNS   = header_rec.KZBTLN8RITTORUHNS   -- 回収実績ボトル８Ｌ本数
         , JHTT.DNPYSTS  = header_rec.DLVRYSTS  -- 伝票ステータス
         , JHTT.DLVRYSTS = header_rec.DLVRYSTS -- 配達ステータス
         , JHTT.DLVRYTNSHACD = header_rec.DLVRYTNSHACD -- 配達担当者コード
         , JHTT.DLVRYCMPNYCD = header_rec.DLVRYCMPNYCD -- 配達会社コード
         , JHTT.DLVRYKBN = header_rec.DLVRYKBN -- 配達区分
         , JHTT.DLVRYJISSEKIPRCSDD = userinfo_rec.UNYOYMD -- 配達実績処理日
         , JHTT.DLVRYJISSEKIMONOAPKIHONCD = userinfo_rec.APKIHONCD -- 配達実績者ＡＰ基本コード
         , JHTT.DLVRYJISSEKIMONOSHOPCD    = userinfo_rec.SHOPCD    -- 配達実績者ショップコード
         , JHTT.DLVRYJISSEKIPRCSTNSHACD   = userinfo_rec.TNSHACD -- 配達実績処理担当者コード
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
-- インボイス対応変更 start ABI
         --, JHTT.NOHINSHOHITAXKIN = sum_rec.NOHINSHOHITAXKIN -- 納品消費税金額
         , JHTT.NOHINSHOHITAXKIN =
           CASE WHEN cmnShimeippantaxkin IS NULL OR cmnShimekeigentaxkin IS NULL THEN
             sum_rec.NOHINSHOHITAXKIN
           ELSE
             CASE WHEN cmnInvoiceshikoymdflg = 0 AND(iKaishuhohokbn <> '7' AND cmnSingldnpyflg = '0') THEN -- インボイス施行日前かつ代引きでない(回収方法区分が"7"(代引)以外かつ単体伝票締フラグOFF)の場合は明細の合算値
               sum_rec.NOHINSHOHITAXKIN
             ELSE
               cmnShimeippantaxkin + cmnShimekeigentaxkin
             END
           END -- 納品消費税金額
-- インボイス対応変更 end ABI
-- インボイス対応追加 start ABI
         , NOHINIPPANNUKIKIN = cmnNohinippannukikin -- 納品一般税抜金額
         , NOHINIPPANTAXKIN =
           CASE WHEN cmnShimeippantaxkin IS NULL THEN
              cmnNohinippantaxkin
           ELSE
              cmnShimeippantaxkin
           END -- 納品一般消費税金額
         , NOHINKEIGENNUKIKIN = cmnNohinkeigennukikin -- 納品軽減税抜金額
         , NOHINKEIGENTAXKIN =
           CASE WHEN cmnShimekeigentaxkin IS NULL THEN
              cmnNohinkeigentaxkin
           ELSE
              cmnShimekeigentaxkin
           END -- 納品軽減消費税金額
         , NOHINHIKAZEIURIIKIN = cmnNohinhikazeiuriikin -- 納品非課税金額
         , NOHINFUKAZEIURIIKIN = cmnNohinfukazeiuriikin -- 納品不課税売上金額
-- インボイス対応追加 end ABI
-- ACCIS2023-6156対応 start ABI
--         , JHTT.URIDNPYNO = header_rec.ACPTDNPYNO -- 売上伝票番号
-- ACCIS2023-6156対応 end   ABI
           /* 更新者 */
         , JHTT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
         , JHTT.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
         , JHTT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
         , JHTT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
         , JHTT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
         , JHTT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
         , JHTT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
         , JHTT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
     WHERE JHTT.APKIHONCD  = header_rec.APKIHONCD
       AND JHTT.SHOPCD     = header_rec.SHOPCD
       AND JHTT.ACPTDNPYNO = header_rec.ACPTDNPYNO
       AND JHTT.EDANO      = header_rec.EDANO
       AND JHTT.YUKOKBN = '1'
    ;
    IF SQL%ROWCOUNT = 0 THEN
       INSERT INTO S04T001T (
              APKIHONCD    -- ＡＰ基本コード
            , SHOPCD -- ショップコード
            , ACPTDNPYNO -- 受注伝票番号
            , EDANO -- 枝番号
            , DLVRYTRGTFLG -- 配達対象フラグ
            , DNPYKBN -- 伝票区分
            , TORIHIKIKBN -- 取引区分
            , ACPTKBN -- 受注区分
            , ACPTKBNSHS -- 受注区分詳細
            , TOKUISAKICD -- 得意先コード
            , ACPTDD -- 受注日
            , ACPTTNSHAAPKIHONCD -- 受注担当者ＡＰ基本コード
            , ACPTTNSHASHOPCD -- 受注担当者ショップコード
            , ACPTTNSHACD -- 受注担当者コード
            , DLVRYYTIDD -- 配達予定日
            , DLVRYJISSEKIDD -- 配達実績日
            , DLVRYDD -- 配達日
            , DLVRYKIBOJIKAN -- 配達希望時間
            , DLVRYKIBOJIKANSHRY -- 配達希望時間終了
            , DLVRYKIBOJIKANKBN -- 配達希望時間区分
            , FZAIOKIKANOFLG -- 不在置き可能フラグ
            , FZAIOKIJISSEKIFLG -- 不在置き実績フラグ
            , DLVRYTNSHACD -- 配達担当者コード
            , DLVRYKANKATSUAPKIHONCD -- 配達管轄ＡＰ基本コード
            , DLVRYKANKATSUSHOPCD -- 配達管轄ショップコード
            , DLVRYKYOTENCD -- 配達拠点コード
            , DLVRYAREACD -- 配達エリアコード
            , DLVRYKBN -- 配達区分
            , DLVRYCMPNYCD -- 配達会社コード
            , AZUKARIBTLN1N2RITTORUHNS -- 預かりボトル１２Ｌ本数
            , KYBTLN1N2RITTORUHNS -- 回収予定ボトル１２Ｌ本数
            , KZBTLN1N2RITTORUHNS -- 回収実績ボトル１２Ｌ本数
            , AZUKARIBTLN8RITTORUHNS -- 預かりボトル８Ｌ本数
            , KYBTLN8RITTORUHNS -- 回収予定ボトル８Ｌ本数
            , KZBTLN8RITTORUHNS -- 回収実績ボトル８Ｌ本数
            , PRNTMEMON1 -- 印刷メモ１
            , PRNTMEMON2 -- 印刷メモ２
            , PRNTMEMON3 -- 印刷メモ３
            , ACPTSHIMEPRCSDD -- 受注締処理日
            , ACPTSHIMETNSHAAPKIHONCD -- 受注締担当者ＡＰ基本コード
            , ACPTSHIMETNSHASHOPCD -- 受注締担当者ショップコード
            , ACPTSHIMEPRCSTNSHACD -- 受注締処理担当者コード
            , DLVRYJISSEKIPRCSDD -- 配達実績処理日
            , DLVRYJISSEKIMONOAPKIHONCD -- 配達実績者ＡＰ基本コード
            , DLVRYJISSEKIMONOSHOPCD -- 配達実績者ショップコード
            , DLVRYJISSEKIPRCSTNSHACD -- 配達実績処理担当者コード
            , URISHIMEPRCSDD -- 売上締処理日
            , GETSUJISHIMEDYDM -- 月次締年月
            , GETSUJISHIMEDD -- 月次締日
            , GETSUJIPRCSJISSIDD -- 月次処理実施日
            , DLVRYJUN -- 配達順
            , NOSHOPRNTTRGTFLG -- 納品書印刷対象フラグ
            , NOSHOPRNTSUMIFLG -- 納品書印刷済フラグ
            , NOSHOPRNTDD -- 納品書印刷日
            , NOSHOHIKAEPRNTTRGTFLG -- 納品書控印刷対象フラグ
            , NOSHOHIKAEPRNTSUMIFLG -- 納品書控印刷済フラグ
            , NOSHOHIKAEPRNTDD -- 納品書控印刷日
            , NYKNHYOPRNTTRGTFLG -- 入金票印刷対象フラグ
            , NYKNHYOPRNTSUMIFLG -- 入金票印刷済フラグ
            , NYKNHYOPRNTDD -- 入金票印刷日
            , WSVSIJISHOPRNTTRGTFLG -- サーバー指示書印刷対象フラグ
            , WSVSIJISHOPRNTSUMIFLG -- サーバー指示書印刷済フラグ
            , WSVSIJISHOPRNTDD -- サーバー指示書印刷日
            , DNPYSTS -- 伝票ステータス
            , ACPTSTS -- 受注ステータス
            , DLVRYSTS -- 配達ステータス
            , DATSKSMOTOKBN -- データ作成元区分
            , SKSMOTOKANRIKBN -- 作成元管理区分
            , SKSMOTOKANRINO -- 作成元管理番号
            , ACPTHIKIATESAKIAPKIHONCD -- 受注引当先ＡＰ基本コード
            , ACPTHIKIATESAKISHOPCD -- 受注引当先ショップコード
            , ACPTHIKIATESAKIKYOTENCD -- 受注引当先拠点コード
            , DLVRYYTIRGRAYUSYOHNS -- 配達予定レギュラー有償本数
            , DLVRYYTIRGRAMUSHOHNS -- 配達予定レギュラー無償本数
            , DLVRYYTISRMYUSYOHNS -- 配達予定スリム有償本数
            , DLVRYYTISRMMUSHOHNS -- 配達予定スリム無償本数
            , DLVRYYTISONOTAYUSYOHNS -- 配達予定その他有償本数
            , DLVRYYTISONOTAMUSHOHNS -- 配達予定その他無償本数
            , DLVRYYTISEIHINMIZUURIKIN -- 配達予定製品水売上金額
            , DLVRYYTISONOTAURIKIN -- 配達予定その他売上金額
            , DLVRYJISSEKIRGRAYUSYOHNS -- 配達実績レギュラー有償本数
            , DLVRYJISSEKIRGRAMUSHOHNS -- 配達実績レギュラー無償本数
            , DLVRYJISSEKISRMYUSYOHNS -- 配達実績スリム有償本数
            , DLVRYJISSEKISRMMUSHOHNS -- 配達実績スリム無償本数
            , DLVRYJISSEKISTYSHNS -- 配達実績その他有償本数
            , DLVRYJISSEKISTMSHNS -- 配達実績その他無償本数
            , DLVRYJISSEKISHSUAKIN -- 配達実績製品水売上金額
            , DLVRYJISSEKISONOTAURIKIN -- 配達実績その他売上金額
            , ACPTKIN -- 受注金額
            , ACPTSHOHITAXKIN -- 受注消費税金額
-- インボイス対応追加 start ABI
            , ACPTIPPANNUKIKIN   -- 受注一般税抜金額
            , ACPTIPPANTAXKIN    -- 受注一般消費税金額
            , ACPTKEIGENNUKIKIN  -- 受注軽減税抜金額
            , ACPTKEIGENTAXKIN   -- 受注軽減消費税金額
            , ACPTHIKAZEIURIIKIN -- 受注非課税金額
            , ACPTFUKAZEIURIIKIN -- 受注不課税売上金額
-- インボイス対応追加 end ABI
            , NOHINKIN -- 納品金額
            , NOHINSHOHITAXKIN -- 納品消費税金額
-- インボイス対応追加 start ABI
            , NOHINIPPANNUKIKIN   -- 納品一般税抜金額
            , NOHINIPPANTAXKIN    -- 納品一般消費税金額
            , NOHINKEIGENNUKIKIN  -- 納品軽減税抜金額
            , NOHINKEIGENTAXKIN   -- 納品軽減消費税金額
            , NOHINHIKAZEIURIIKIN -- 納品非課税金額
            , NOHINFUKAZEIURIIKIN -- 納品不課税売上金額
-- インボイス対応追加 end ABI
            , KURERURITTORUSTUP -- クレールＬ設置
            , KURERURITTORUHIKIAGE -- クレールＬ引上
            , KURERUESUSTUP -- クレールＳ設置
            , KURERUESUHIKIAGE -- クレールＳ引上
            , SONOTASTUP -- その他設置
            , SONOTAHIKIAGE -- その他引上
            , TAXKEISANKBN -- 税計算区分
            , ZNKDLVRYDD -- 前回配達日
            , ZNKDLVRYHNS -- 前回配達本数
            , JIKAIDLVRYYTIDD -- 次回配達予定日
            , ZNKSHIMEDD -- 前回締日
            , ZNKKURIKOSHIKIN -- 前回繰越金額
            , ACPTTHDAIBIKIFLG -- 受注時代引フラグ
            , ACPTRCPTKEIROKBN -- 受注受付経路区分
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
            , ITAKUAITEAPKIHONCD -- 委託相手ＡＰ基本コード
            , ITAKUAITESHOPCD -- 委託相手ショップコード
            , ITAKUAITEACPTDNPYNO -- 委託相手伝票番号
            , ITAKUAITEEDANO -- 委託相手枝番号
            , URIDNPYNO -- 売上伝票番号
       ) VALUES (
                header_rec.APKIHONCD    -- ＡＰ基本コード
              , header_rec.SHOPCD -- ショップコード
              , header_rec.ACPTDNPYNO -- 受注伝票番号
              , header_rec.EDANO -- 枝番号
              , '1' -- 配達対象フラグ
              -- 伝票区分
              , ( CASE WHEN tokuisakiMST.TOKUISAKIKBN = '9' THEN '15'
                       ELSE '14'
                  END )
              -- 取引区分
              , ( CASE WHEN tokuisakiMST.TOKUISAKIKBN = '9' THEN '3'
                       ELSE '1'
                  END )
              , header_rec.ACPTKBN -- 受注区分
              , header_rec.ACPTKBNSHS -- 受注区分詳細
              , header_rec.TOKUISAKICD -- 得意先コード
              , header_rec.DLVRYJISSEKIDD -- 受注日
              , userinfo_rec.APKIHONCD -- 受注担当者ＡＰ基本コード
              , userinfo_rec.SHOPCD -- 受注担当者ショップコード
              , userinfo_rec.TNSHACD -- 受注担当者コード
              , NULL -- 配達予定日
              , header_rec.DLVRYJISSEKIDD -- 配達実績日
              , header_rec.DLVRYJISSEKIDD -- 配達日
              , NULL -- 配達希望時間
              , NULL -- 配達希望時間終了
              , NULL -- 配達希望時間区分
              , tokuisakiMST.FZAIOKIKANOFLG  -- 不在置き可能フラグ
              , header_rec.FZAIOKIJISSEKIFLG -- 不在置き実績フラグ
              , header_rec.DLVRYTNSHACD -- 配達担当者コード
              , tokuisakiMST.DLVRYKANKATSUAPKIHONCD -- 配達管轄ＡＰ基本コード
              , tokuisakiMST.DLVRYKANKATSUSHOPCD    -- 配達管轄ショップコード
              , tokuisakiMST.DLVRYKANKATSUKYOTENCD  -- 配達拠点コード
              , tokuisakiMST.DLVRYAREACD  -- 配達エリアコード
              , header_rec.DLVRYKBN     -- 配達区分
              , header_rec.DLVRYCMPNYCD -- 配達会社コード
              , tokuisakiJST.AZUKARIBTLN1N2RITTORUHNS -- 預かりボトル１２Ｌ本数
              , 0 -- 回収予定ボトル１２Ｌ本数
              , header_rec.KZBTLN1N2RITTORUHNS -- 回収実績ボトル１２Ｌ本数
              , tokuisakiJST.AZUKARIBTLN8RITTORUHNS -- 預かりボトル８Ｌ本数
              , 0 -- 回収予定ボトル８Ｌ本数
              , header_rec.KZBTLN8RITTORUHNS -- 回収実績ボトル８Ｌ本数
              , tokuisakiMST.PRNTMEMON1 -- 印刷メモ１
              , tokuisakiMST.PRNTMEMON2 -- 印刷メモ２
              , tokuisakiMST.PRNTMEMON3 -- 印刷メモ３
              , userinfo_rec.UNYOYMD   -- 受注締処理日
              , userinfo_rec.APKIHONCD -- 受注締担当者ＡＰ基本コード
              , userinfo_rec.SHOPCD    -- 受注締担当者ショップコード
              , userinfo_rec.TNSHACD   -- 受注締処理担当者コード
              , userinfo_rec.UNYOYMD   -- 配達実績処理日
              , userinfo_rec.APKIHONCD -- 配達実績者ＡＰ基本コード
              , userinfo_rec.SHOPCD    -- 配達実績者ショップコード
              , userinfo_rec.TNSHACD   -- 配達実績処理担当者コード
              , '00000000' -- 売上締処理日
              , NULL -- 月次締年月
              , NULL -- 月次締日
              , NULL -- 月次処理実施日
              , tokuisakiMST.DLVRYJUN -- 配達順
              , '1'  -- 納品書印刷対象フラグ
              , '0'  -- 納品書印刷済フラグ
              , NULL -- 納品書印刷日
              , '1'  -- 納品書控印刷対象フラグ
              , '0'  -- 納品書控印刷済フラグ
              , NULL -- 納品書控印刷日
              -- 入金票印刷対象フラグ
              , ( CASE WHEN iKaishuhohokbn = '7' THEN '1'
                       ELSE '0'
                  END )
              , '0' -- 入金票印刷済フラグ
              , NULL -- 入金票印刷日
              -- サーバー指示書印刷対象フラグ
              , ( CASE WHEN header_rec.ACPTKBN = '1' THEN '0'
                       ELSE '1'
                  END )
              , '0' -- サーバー指示書印刷済フラグ
              , NULL -- サーバー指示書印刷日
              , header_rec.DLVRYSTS -- 伝票ステータス
              , '02' -- 受注ステータス
              , header_rec.DLVRYSTS -- 配達ステータス
              , DATSKSMOTOKBN_NEW -- データ作成元区分
              , NULL -- 作成元管理区分
              , NULL -- 作成元管理番号
              , tokuisakiMST.DLVRYKANKATSUAPKIHONCD -- 受注引当先ＡＰ基本コード
              , tokuisakiMST.DLVRYKANKATSUSHOPCD -- 受注引当先ショップコード
              , tokuisakiMST.DLVRYKANKATSUKYOTENCD -- 受注引当先拠点コード
              , 0 -- 配達予定レギュラー有償本数
              , 0 -- 配達予定レギュラー無償本数
              , 0 -- 配達予定スリム有償本数
              , 0 -- 配達予定スリム無償本数
              , 0 -- 配達予定その他有償本数
              , 0 -- 配達予定その他無償本数
              , 0 -- 配達予定製品水売上金額
              , 0 -- 配達予定その他売上金額
              , sum_rec.DLVRYRGRAYUSYOHNS -- 配達実績レギュラー有償本数
              , sum_rec.DLVRYRGRAMUSHOHNS -- 配達実績レギュラー無償本数
              , sum_rec.DLVRYSRMYUSYOHNS -- 配達実績スリム有償本数
              , sum_rec.DLVRYSRMMUSHOHNS -- 配達実績スリム無償本数
              , sum_rec.DLVRYSTYSHNS -- 配達実績その他有償本数
              , sum_rec.DLVRYSTMSHNS -- 配達実績その他無償本数
              , sum_rec.DLVRYSHSUAKIN     -- 配達実績製品水売上金額
              , sum_rec.DLVRYSONOTAURIKIN -- 配達実績その他売上金額
              , 0 -- 受注金額
              , 0 -- 受注消費税金額
-- インボイス対応追加 start ABI
              , cmnAcptippannukikin   -- 受注一般税抜金額
              , cmnAcptippantaxkin    -- 受注一般消費税金額
              , cmnAcptkeigennukikin  -- 受注軽減税抜金額
              , cmnAcptkeigentaxkin   -- 受注軽減消費税金額
              , cmnAcpthikazeiuriikin -- 受注非課税金額
              , cmnAcptfukazeiuriikin -- 受注不課税売上金額
-- インボイス対応追加 end ABI
              , sum_rec.NOHINKIN -- 納品金額
-- インボイス対応変更 start ABI
              --, sum_rec.NOHINSHOHITAXKIN -- 納品消費税金額
              , CASE WHEN cmnShimeippantaxkin IS NULL OR cmnShimekeigentaxkin IS NULL THEN
                   sum_rec.NOHINSHOHITAXKIN
                ELSE
                  CASE WHEN cmnInvoiceshikoymdflg = 0 AND(iKaishuhohokbn <> '7' AND cmnSingldnpyflg = '0') THEN -- インボイス施行日前かつ代引きでない(回収方法区分が"7"(代引)以外かつ単体伝票締フラグOFF)の場合は明細の合算値
                    sum_rec.NOHINSHOHITAXKIN
                  ELSE
                    cmnShimeippantaxkin + cmnShimekeigentaxkin
                  END
                END -- 納品消費税金額
-- インボイス対応変更 end ABI
-- インボイス対応追加 start ABI
              , cmnNohinippannukikin -- 納品一般税抜金額
              , CASE WHEN cmnShimeippantaxkin IS NULL THEN
                   cmnNohinippantaxkin
                ELSE
                   cmnShimeippantaxkin
                END -- 納品一般消費税金額
              , cmnNohinkeigennukikin -- 納品軽減税抜金額
              , CASE WHEN cmnShimekeigentaxkin IS NULL THEN
                   cmnNohinkeigentaxkin
                ELSE
                   cmnShimekeigentaxkin
                END -- 納品軽減消費税金額
              , cmnNohinhikazeiuriikin -- 納品非課税金額
              , cmnNohinfukazeiuriikin -- 納品不課税売上金額
-- インボイス対応追加 end ABI
              , sum_rec.KURERURITTORUSTUP -- クレールＬ設置
              , sum_rec.KURERURITTORUHIKIAGE -- クレールＬ引上
              , sum_rec.KURERUESUSTUP -- クレールＳ設置
              , sum_rec.KURERUESUHIKIAGE -- クレールＳ引上
              , sum_rec.SONOTASTUP -- その他設置
              , sum_rec.SONOTAHIKIAGE -- その他引上
              , iTaxkeisankbn -- 税計算区分
              , tokuisakiJST.ZNKDLVRYDD -- 前回配達日
              , 0 -- 前回配達本数
              , NULL -- 次回配達予定日
              , NULL -- 前回締日
              , NULL -- 前回繰越金額
              , '0'  -- 受注時代引フラグ
              , '99' -- 受注受付経路区分：その他外部連携
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
              , NULL -- 委託相手ＡＰ基本コード
              , NULL -- 委託相手ショップコード
              , NULL -- 委託相手伝票番号
              , NULL -- 委託相手枝番号
              , header_rec.ACPTDNPYNO -- 売上伝票番号
       );
    END IF;

    -- ポイント制度対応追加 start ABI
    -- 紐づけテーブルに有効設定があれば(ポイント発生)予定発生ポイント登録
    IF cmnRegistKanrino IS NOT NULL THEN
      registPointHeaderTable(cmnRegistKanrino
      , header_rec);
    END IF;
    -- ポイント制度対応追加 end ABI

-- インボイス対応追加 start ABI
    -- 変数の初期化
    cmnAcptippannukikin    := 0; -- 受注一般税抜金額
    cmnAcptippantaxkin     := 0; -- 受注一般消費税金額
    cmnAcptkeigennukikin   := 0; -- 受注軽減税抜金額
    cmnAcptkeigentaxkin    := 0; -- 受注軽減消費税金額
    cmnAcpthikazeiuriikin  := 0; -- 受注非課税金額
    cmnAcptfukazeiuriikin  := 0; -- 受注不課税売上金額
    cmnNohinippannukikin   := 0; -- 納品一般税抜金額
    cmnNohinippantaxkin    := 0; -- 納品一般消費税金額
    cmnNohinkeigennukikin  := 0; -- 納品軽減税抜金額
    cmnNohinkeigentaxkin   := 0; -- 納品軽減消費税金額
    cmnNohinhikazeiuriikin := 0; -- 納品非課税金額
    cmnNohinfukazeiuriikin := 0; -- 納品不課税売上金額
-- インボイス対応追加 end ABI

    -- 配布物更新
    FOR hfbh_rec IN SELECT_HFBH LOOP
      FOR hfbm_rec IN SELECT_HFBM LOOP
          updateHaifubutsuTKM( hfbh_rec, hfbm_rec );
      END LOOP;
    END LOOP;

    EXCEPTION
      WHEN OTHERS THEN
        IF SELECT_HFBH%ISOPEN THEN
           CLOSE SELECT_HFBH;
        END IF;
        IF SELECT_HFBM%ISOPEN THEN
           CLOSE SELECT_HFBM;
        END IF;
        RAISE_APPLICATION_ERROR(-20017, SQLERRM || '配達済受託配達テーブル作成時エラーが発生しました。');
  END processJuchuDeliveredWeb;
-- 本線対応追加 end ABI

-- ACCIS2023-6615対応 start ABI
  /*****************************************************************************/
  /* プロシージャ名：印刷済フラグ更新                                          */
  /* 引数             ：header_rec      配達実績ワークデータヘッダ             */
  /*****************************************************************************/
  PROCEDURE updatePrntsumiflg( header_rec     IN S05W001T%rowtype -- 配達実績ワークデータヘッダ
  ) IS

  BEGIN
    UPDATE S04T001T
       SET NOSHOPRNTSUMIFLG =
               CASE WHEN NOSHOPRNTTRGTFLG = '1' AND NOSHOPRNTSUMIFLG = '0' THEN
                   '1'
               ELSE
                   NOSHOPRNTSUMIFLG
               END -- 納品書印刷済フラグ
         , NOSHOPRNTDD =
               CASE WHEN NOSHOPRNTTRGTFLG = '1' AND NOSHOPRNTSUMIFLG = '0' THEN
                   header_rec.DLVRYJISSEKIDD
               ELSE
                   NOSHOPRNTDD
               END -- 納品書印刷日
         , NOSHOHIKAEPRNTSUMIFLG =
               CASE WHEN NOSHOHIKAEPRNTTRGTFLG = '1' AND NOSHOHIKAEPRNTSUMIFLG = '0' THEN
                   '1'
               ELSE
                   NOSHOHIKAEPRNTSUMIFLG
               END -- 納品書控印刷済フラグ
         , NOSHOHIKAEPRNTDD =
               CASE WHEN NOSHOHIKAEPRNTTRGTFLG = '1' AND NOSHOHIKAEPRNTSUMIFLG = '0' THEN
                   header_rec.DLVRYJISSEKIDD
               ELSE
                   NOSHOHIKAEPRNTDD
               END -- 納品書控印刷日
         , NYKNHYOPRNTSUMIFLG =
               CASE WHEN NYKNHYOPRNTTRGTFLG = '1' AND NYKNHYOPRNTSUMIFLG = '0' THEN
                   '1'
               ELSE
                   NYKNHYOPRNTSUMIFLG
               END -- 入金票印刷済フラグ
         , NYKNHYOPRNTDD =
               CASE WHEN NYKNHYOPRNTTRGTFLG = '1' AND NYKNHYOPRNTSUMIFLG = '0' THEN
                   header_rec.DLVRYJISSEKIDD
               ELSE
                   NYKNHYOPRNTDD
               END -- 入金票印刷日
         , WSVSIJISHOPRNTSUMIFLG =
               CASE WHEN WSVSIJISHOPRNTTRGTFLG = '1' AND WSVSIJISHOPRNTSUMIFLG = '0' THEN
                   '1'
               ELSE
                   WSVSIJISHOPRNTSUMIFLG
               END -- サーバー指示書印刷済フラグ
         , WSVSIJISHOPRNTDD =
               CASE WHEN WSVSIJISHOPRNTTRGTFLG = '1' AND WSVSIJISHOPRNTSUMIFLG = '0' THEN
                   header_rec.DLVRYJISSEKIDD
               ELSE
                   WSVSIJISHOPRNTDD
               END -- サーバー指示書印刷日
         , KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
         , KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
         , KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
         , KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
         , KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
         , KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
         , KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
         , KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
     WHERE APKIHONCD  = header_rec.APKIHONCD
       AND SHOPCD     = header_rec.SHOPCD
       AND ACPTDNPYNO = header_rec.ACPTDNPYNO
       AND EDANO      = header_rec.EDANO
       AND YUKOKBN = '1'
    ;

    EXCEPTION
      WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20017, SQLERRM || '印刷済フラグ更新時エラーが発生しました。');
  END updatePrntsumiflg;
-- ACCIS2023-6615対応 end   ABI

  /*****************************************************************************/
  /* プロシージャ名：受託配達テーブル未配達更新                                     */
  /* 引数             ：header_rec      配達実績ワークデータヘッダ                 */
  /*                  ：acpt_old        更新前受注配達テーブル                    */
  /*****************************************************************************/
  PROCEDURE processJuchuNotDeliver( header_rec  IN S05W001T%rowtype -- 配達実績ワークデータヘッダ
                                  , acpt_old    IN S04T001T%rowtype -- 更新前受注配達テーブル
  ) IS
  /* CURSOR：配布物キャンセル用明細 */
  CURSOR SELECT_HFB IS
    SELECT JHMT.*
      FROM S04T002T JHMT
     WHERE JHMT.APKIHONCD  = header_rec.APKIHONCD
       AND JHMT.SHOPCD     = header_rec.SHOPCD
       AND JHMT.ACPTDNPYNO = header_rec.ACPTDNPYNO
       AND JHMT.EDANO      = header_rec.EDANO
       AND JHMT.MEISAIKBN = '1' -- 配布物
       AND JHMT.YUKOKBN   = '1'
  ;
  /* CURSOR：代引きキャンセル用明細 */
  CURSOR SELECT_DBKM IS
    SELECT JHMT.*
      FROM S04T002T JHMT
     INNER JOIN S06T001T URIT
        ON ( URIT.APKIHONCD = JHMT.APKIHONCD
           AND URIT.SHOPCD = JHMT.SHOPCD
           AND URIT.URIDNPYNO = JHMT.URIDNPYNO
           AND URIT.EDANO = JHMT.URIDNPYEDANO
        )
     WHERE JHMT.APKIHONCD  = header_rec.APKIHONCD
       AND JHMT.SHOPCD     = header_rec.SHOPCD
       AND JHMT.ACPTDNPYNO = header_rec.ACPTDNPYNO
       AND JHMT.EDANO      = header_rec.EDANO
       AND JHMT.MEISAIKBN = '2' -- 代引き
       AND JHMT.YUKOKBN   = '1'
  ;
  BEGIN
    UPDATE S04T001T JHTT
       SET /* 受託のデータ */
           JHTT.DLVRYJISSEKIDD = header_rec.DLVRYJISSEKIDD -- 配達実績日
         , JHTT.DLVRYDD        = header_rec.DLVRYJISSEKIDD -- 配達日
         , JHTT.DLVRYTNSHACD   = header_rec.DLVRYTNSHACD   -- 配達担当者コード
           -- 配達実績処理日
         , JHTT.DLVRYJISSEKIPRCSDD = ( CASE WHEN header_rec.DLVRYSTS IN ('13', '14')
                                            THEN header_rec.JIKAIDLVRYYTIDD -- 次回配達予定日
                                            ELSE userinfo_rec.UNYOYMD -- 運用日付
                                       END )
         , JHTT.DNPYSTS  = header_rec.DLVRYSTS -- 伝票ステータス
         , JHTT.DLVRYSTS = header_rec.DLVRYSTS -- 配達ステータス
         , JHTT.DLVRYJISSEKIMONOAPKIHONCD = userinfo_rec.APKIHONCD -- 配達実績者ＡＰ基本コード
         , JHTT.DLVRYJISSEKIMONOSHOPCD    = userinfo_rec.SHOPCD    -- 配達実績者ショップコード
         , JHTT.DLVRYJISSEKIPRCSTNSHACD   = userinfo_rec.TNSHACD   -- 配達実績処理担当者コード
         , JHTT.YUKOKBN  = header_rec.YUKOKBN  -- 有効区分
           /* 更新者 */
         , JHTT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
         , JHTT.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
         , JHTT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
         , JHTT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
         , JHTT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
         , JHTT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
         , JHTT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
         , JHTT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
     WHERE JHTT.APKIHONCD  = header_rec.APKIHONCD
       AND JHTT.SHOPCD     = header_rec.SHOPCD
       AND JHTT.ACPTDNPYNO = header_rec.ACPTDNPYNO
       AND JHTT.EDANO      = header_rec.EDANO
       AND JHTT.YUKOKBN = '1'
    ;

-- インボイス対応追加 start ABI
    -- 変数の初期化
    cmnAcptippannukikin    := 0; -- 受注一般税抜金額
    cmnAcptippantaxkin     := 0; -- 受注一般消費税金額
    cmnAcptkeigennukikin   := 0; -- 受注軽減税抜金額
    cmnAcptkeigentaxkin    := 0; -- 受注軽減消費税金額
    cmnAcpthikazeiuriikin  := 0; -- 受注非課税金額
    cmnAcptfukazeiuriikin  := 0; -- 受注不課税売上金額
    cmnNohinippannukikin   := 0; -- 納品一般税抜金額
    cmnNohinippantaxkin    := 0; -- 納品一般消費税金額
    cmnNohinkeigennukikin  := 0; -- 納品軽減税抜金額
    cmnNohinkeigentaxkin   := 0; -- 納品軽減消費税金額
    cmnNohinhikazeiuriikin := 0; -- 納品非課税金額
    cmnNohinfukazeiuriikin := 0; -- 納品不課税売上金額
-- インボイス対応追加 end ABI

    -- 配布物キャンセル
    FOR hfb_rec IN SELECT_HFB LOOP
        cancelHaifubutsuTKM( header_rec.APKIHONCD, header_rec.SHOPCD, hfb_rec.HAIFUBUTSUCD, header_rec.TOKUISAKICD );
    END LOOP;

    -- 代引き処理
-- インボイス対応変更 start ABI
--    IF acpt_old.ACPTTHDAIBIKIFLG = '1' THEN
--       FOR daibiki_meisai IN SELECT_DBKM LOOP
--           -- 売上テーブル更新
--           UPDATE S06T001T URIT
--              SET URIT.SKYPRCSJISSIDD = '00000000' -- 請求処理実施日
--                , URIT.URISHIMEPRCSDD = URIT.URIDD -- 売上締処理日
--                  /* 更新者 */
--                , URIT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
--                , URIT.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
--                , URIT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
--                , URIT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
--                , URIT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
--                , URIT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
--                , URIT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
--                , URIT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
--            WHERE URIT.APKIHONCD = daibiki_meisai.APKIHONCD
--              AND URIT.SHOPCD = daibiki_meisai.SHOPCD
--              AND URIT.URIDNPYNO = daibiki_meisai.URIDNPYNO
--           ;
--           -- 受注配達明細テーブル削除
--           DELETE FROM S04T002T
--           WHERE APKIHONCD = daibiki_meisai.APKIHONCD
--             AND SHOPCD = daibiki_meisai.SHOPCD
--             AND ACPTDNPYNO = daibiki_meisai.ACPTDNPYNO
--             AND EDANO = daibiki_meisai.EDANO
--             AND ACPTDNPYMEISAINO = daibiki_meisai.ACPTDNPYMEISAINO
--             AND ACPTDNPYMEISAIEDANO = daibiki_meisai.ACPTDNPYMEISAIEDANO
--           ;
--       END LOOP;
    IF acpt_old.ACPTTHDAIBIKIFLG = '1' OR acpt_old.SINGLDNPYFLG = '1' THEN
       -- 売上テーブル更新
       UPDATE S06T001T URIT
          SET URIT.SKYPRCSJISSIDD = '00000000' -- 請求処理実施日
            , URIT.URISHIMEPRCSDD = URIT.URIDD -- 売上締処理日
            , URIT.ACPTDNPYNO     = NULL       -- 受注伝票番号
            , URIT.ACPTDNPYEDANO  = NULL       -- 受注伝票枝番号
              /* 更新者 */
            , URIT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
            , URIT.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
            , URIT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
            , URIT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
            , URIT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
            , URIT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
            , URIT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
            , URIT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
        WHERE URIT.APKIHONCD = header_rec.APKIHONCD
          AND URIT.SHOPCD = header_rec.SHOPCD
          AND URIT.ACPTDNPYNO = header_rec.ACPTDNPYNO
          AND URIT.ACPTDNPYEDANO = header_rec.EDANO
       ;
-- インボイス対応変更 end ABI
    END IF;

    EXCEPTION
      WHEN OTHERS THEN
        IF SELECT_HFB%ISOPEN THEN
           CLOSE SELECT_HFB;
        END IF;
        IF SELECT_DBKM%ISOPEN THEN
           CLOSE SELECT_DBKM;
        END IF;
        RAISE_APPLICATION_ERROR(-20018, SQLERRM || '未配達受託配達テーブル更新時エラーが発生しました。');
  END processJuchuNotDeliver;

-- 本線対応追加 start ABI
  /*****************************************************************************/
  /* プロシージャ名：受託配達テーブル未配達更新WEB                             */
  /* 引数             ：header_rec      配達実績ワークデータヘッダ             */
  /*                  ：acpt_old        更新前受注配達テーブル                 */
  /*****************************************************************************/
  PROCEDURE processJuchuNotDeliverWeb( header_rec  IN S05W001T%rowtype -- 配達実績ワークデータヘッダ
                                     , acpt_old    IN S04T001T%rowtype -- 更新前受注配達テーブル
  ) IS
  /* CURSOR：配布物キャンセル用明細 */
  CURSOR SELECT_HFB IS
    SELECT JHMT.*
      FROM S04T002T JHMT
     WHERE JHMT.APKIHONCD  = header_rec.APKIHONCD
       AND JHMT.SHOPCD     = header_rec.SHOPCD
       AND JHMT.ACPTDNPYNO = header_rec.ACPTDNPYNO
       AND JHMT.EDANO      = header_rec.EDANO
       AND JHMT.MEISAIKBN = '1' -- 配布物
       AND JHMT.YUKOKBN   = '1'
  ;
  /* CURSOR：代引きキャンセル用明細 */
  CURSOR SELECT_DBKM IS
    SELECT JHMT.*
      FROM S04T002T JHMT
     INNER JOIN S06T001T URIT
        ON ( URIT.APKIHONCD = JHMT.APKIHONCD
           AND URIT.SHOPCD = JHMT.SHOPCD
           AND URIT.URIDNPYNO = JHMT.URIDNPYNO
           AND URIT.EDANO = JHMT.URIDNPYEDANO
        )
     WHERE JHMT.APKIHONCD  = header_rec.APKIHONCD
       AND JHMT.SHOPCD     = header_rec.SHOPCD
       AND JHMT.ACPTDNPYNO = header_rec.ACPTDNPYNO
       AND JHMT.EDANO      = header_rec.EDANO
       AND JHMT.MEISAIKBN = '2' -- 代引き
       AND JHMT.YUKOKBN   = '1'
  ;
  BEGIN
    UPDATE S04T001T JHTT
       SET /* 受託のデータ */
           JHTT.DLVRYJISSEKIDD = header_rec.DLVRYJISSEKIDD -- 配達実績日
         , JHTT.DLVRYDD        = header_rec.DLVRYJISSEKIDD -- 配達日
         , JHTT.DLVRYTNSHACD   = header_rec.DLVRYTNSHACD   -- 配達担当者コード
           -- 配達実績処理日
         , JHTT.DLVRYJISSEKIPRCSDD = ( CASE WHEN header_rec.DLVRYSTS IN ('13', '14')
                                            THEN header_rec.JIKAIDLVRYYTIDD -- 次回配達予定日
                                            ELSE userinfo_rec.UNYOYMD -- 運用日付
                                       END )
         , JHTT.DNPYSTS  = header_rec.DLVRYSTS -- 伝票ステータス
         , JHTT.DLVRYSTS = header_rec.DLVRYSTS -- 配達ステータス
         , JHTT.DLVRYJISSEKIMONOAPKIHONCD = userinfo_rec.APKIHONCD -- 配達実績者ＡＰ基本コード
         , JHTT.DLVRYJISSEKIMONOSHOPCD    = userinfo_rec.SHOPCD    -- 配達実績者ショップコード
         , JHTT.DLVRYJISSEKIPRCSTNSHACD   = userinfo_rec.TNSHACD   -- 配達実績処理担当者コード
         , JHTT.YUKOKBN  = header_rec.YUKOKBN  -- 有効区分
           /* 更新者 */
         , JHTT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
         , JHTT.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
         , JHTT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
         , JHTT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
         , JHTT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
         , JHTT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
         , JHTT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
         , JHTT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
     WHERE JHTT.APKIHONCD  = header_rec.APKIHONCD
       AND JHTT.SHOPCD     = header_rec.SHOPCD
       AND JHTT.ACPTDNPYNO = header_rec.ACPTDNPYNO
       AND JHTT.EDANO      = header_rec.EDANO
       AND JHTT.YUKOKBN = '1'
    ;

-- インボイス対応追加 start ABI
    -- 変数の初期化
    cmnAcptippannukikin    := 0; -- 受注一般税抜金額
    cmnAcptippantaxkin     := 0; -- 受注一般消費税金額
    cmnAcptkeigennukikin   := 0; -- 受注軽減税抜金額
    cmnAcptkeigentaxkin    := 0; -- 受注軽減消費税金額
    cmnAcpthikazeiuriikin  := 0; -- 受注非課税金額
    cmnAcptfukazeiuriikin  := 0; -- 受注不課税売上金額
    cmnNohinippannukikin   := 0; -- 納品一般税抜金額
    cmnNohinippantaxkin    := 0; -- 納品一般消費税金額
    cmnNohinkeigennukikin  := 0; -- 納品軽減税抜金額
    cmnNohinkeigentaxkin   := 0; -- 納品軽減消費税金額
    cmnNohinhikazeiuriikin := 0; -- 納品非課税金額
    cmnNohinfukazeiuriikin := 0; -- 納品不課税売上金額
-- インボイス対応追加 end ABI

    -- 配布物キャンセル
    FOR hfb_rec IN SELECT_HFB LOOP
        cancelHaifubutsuTKM( header_rec.APKIHONCD, header_rec.SHOPCD, hfb_rec.HAIFUBUTSUCD, header_rec.TOKUISAKICD );
    END LOOP;

    -- 代引き処理
-- インボイス対応変更 start ABI
--    IF acpt_old.ACPTTHDAIBIKIFLG = '1' THEN
--       FOR daibiki_meisai IN SELECT_DBKM LOOP
--           -- 売上テーブル更新
--           UPDATE S06T001T URIT
--              SET URIT.SKYPRCSJISSIDD = '00000000' -- 請求処理実施日
--                , URIT.URISHIMEPRCSDD = URIT.URIDD -- 売上締処理日
--                  /* 更新者 */
--                , URIT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
--                , URIT.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
--                , URIT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
--                , URIT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
--                , URIT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
--                , URIT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
--                , URIT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
--                , URIT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
--            WHERE URIT.APKIHONCD = daibiki_meisai.APKIHONCD
--              AND URIT.SHOPCD = daibiki_meisai.SHOPCD
--              AND URIT.URIDNPYNO = daibiki_meisai.URIDNPYNO
--           ;
--           -- 受注配達明細テーブル削除
--           DELETE FROM S04T002T
--           WHERE APKIHONCD = daibiki_meisai.APKIHONCD
--             AND SHOPCD = daibiki_meisai.SHOPCD
--             AND ACPTDNPYNO = daibiki_meisai.ACPTDNPYNO
--             AND EDANO = daibiki_meisai.EDANO
--             AND ACPTDNPYMEISAINO = daibiki_meisai.ACPTDNPYMEISAINO
--             AND ACPTDNPYMEISAIEDANO = daibiki_meisai.ACPTDNPYMEISAIEDANO
--           ;
--       END LOOP;
    IF acpt_old.ACPTTHDAIBIKIFLG = '1' OR acpt_old.SINGLDNPYFLG = '1' THEN
       -- 売上テーブル更新
       UPDATE S06T001T URIT
          SET URIT.SKYPRCSJISSIDD = '00000000' -- 請求処理実施日
            , URIT.URISHIMEPRCSDD = URIT.URIDD -- 売上締処理日
            , URIT.ACPTDNPYNO     = NULL       -- 受注伝票番号
            , URIT.ACPTDNPYEDANO  = NULL       -- 受注伝票枝番号
              /* 更新者 */
            , URIT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
            , URIT.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
            , URIT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
            , URIT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
            , URIT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
            , URIT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
            , URIT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
            , URIT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
        WHERE URIT.APKIHONCD = header_rec.APKIHONCD
          AND URIT.SHOPCD = header_rec.SHOPCD
          AND URIT.ACPTDNPYNO = header_rec.ACPTDNPYNO
          AND URIT.ACPTDNPYEDANO = header_rec.EDANO
       ;
-- インボイス対応変更 end ABI
    END IF;

    EXCEPTION
      WHEN OTHERS THEN
        IF SELECT_HFB%ISOPEN THEN
           CLOSE SELECT_HFB;
        END IF;
        IF SELECT_DBKM%ISOPEN THEN
           CLOSE SELECT_DBKM;
        END IF;
        RAISE_APPLICATION_ERROR(-20018, SQLERRM || '未配達受託配達テーブル更新時エラーが発生しました。');
  END processJuchuNotDeliverWeb;
-- 本線対応追加 end ABI

  /*****************************************************************************/
  /* プロシージャ名：再配達データ作成処理                                           */
  /* 引数（ＩＮ）      ：work_rec      配達実績データヘッダ                        */
  /*****************************************************************************/
  PROCEDURE processSaihaitatsu( work_rec     IN S05W001T%rowtype -- 配達実績データヘッダ
  ) IS
  /* 変数宣言 */
  header_rec S04T001T%rowtype := NULL; -- 受注配達テーブル
  wTaxsetteiKbn S02V001T.TAXSETTEIKBN%type := null; -- 税設定区分
  wTaxKbn             S02V001T.TAXKBN%type := null; -- 税区分
  wAcptTaxSum                 NUMBER(11,0) := 0;    -- 受注消費税合計
  wKidAcptTaxSum              NUMBER(11,0) := 0;    -- 子商品受注消費税合計
  wKidLastNo                  NUMBER(2,0)  := 0;    -- 子商品最終明細枝番号
  oya_rec                 S04T002T%rowtype := NULL; -- 親レコード
  meisai_rec              S04T002T%rowtype := NULL; -- 明細レコード
  wRknKindTokubetsuUntFlg     S02M013T.RKNKINDTOKUBETSUUNTFLG%type := null;    -- 料金種別特別単価フラグ
  wHyojunUnt                  S04T002T.HYOJUNUNT%type := null;
  wHansokuunt                 S04T002T.ACPTUNT%type := null;
  wPricekbn                   VARCHAR2(1) := null;
  wOpenUnt                    S04T002T.ACPTUNT%type := null;
  wTempUnt                    S04T002T.ACPTUNT%type := null;
  wHansokuYusenKbn            VARCHAR2(1) := null;
  wStKbn                      VARCHAR2(1) := null;
  wTokuisakiUnt               S04T002T.ACPTUNT%type := null;
  wTokuisakiSetUnt            S04T002T.ACPTUNT%type := null;
  wUrikinHasuKeisanKbn        VARCHAR2(1)  := NULL;       -- 売上金額端数計算区分
  wAiteTokuisakiCd            S02M013T.TOKUISAKICD%type := null;
  wAiteDlvryKbn               S04T001T.DLVRYKBN%type := null;
  wAiteRknKindTokubetsuUntFlg S02M013T.RKNKINDTOKUBETSUUNTFLG%type := null;    -- 料金種別特別単価フラグ
  wAiteTaxSetteiKbn           S02V001T.TAXSETTEIKBN%type := null; -- 税設定区分
  wAiteTaxkeisanKbn           S02M046T.SHINTAXKEISANKBNURI%type := null; -- 税計算区分
  /* CURSOR：受注配達明細テーブル */
  CURSOR SELECT_MEISAI IS
    SELECT JHMT.*
      FROM S04T002T JHMT
     WHERE JHMT.APKIHONCD  = work_rec.APKIHONCD
       AND JHMT.SHOPCD     = work_rec.SHOPCD
       AND JHMT.ACPTDNPYNO = work_rec.ACPTDNPYNO
       AND JHMT.EDANO = work_rec.EDANO
       AND JHMT.YUKOKBN    = '1'
       AND JHMT.MEISAIKBN <> '3' -- 消費税調整以外の明細を対象とする
  ;
  BEGIN
    -- キャンセル対象抽出
    SELECT JHTT.*
      INTO header_rec
      FROM S04T001T JHTT
     WHERE JHTT.APKIHONCD  = work_rec.APKIHONCD
       AND JHTT.SHOPCD     = work_rec.SHOPCD
       AND JHTT.ACPTDNPYNO = work_rec.ACPTDNPYNO
       AND JHTT.EDANO      = work_rec.EDANO
    ;

    -- 受注配達テーブル登録
    header_rec.EDANO := header_rec.EDANO + 1;  -- 枝番号
    header_rec.DLVRYYTIDD     := work_rec.JIKAIDLVRYYTIDD; -- 配達予定日
    header_rec.DLVRYJISSEKIDD := NULL; -- 配達実績日
    header_rec.DLVRYDD        := work_rec.JIKAIDLVRYYTIDD; -- 配達日
    header_rec.FZAIOKIJISSEKIFLG := '0'; -- 不在置き実績フラグ
    header_rec.DLVRYTNSHACD := work_rec.DLVRYTNSHACD; -- 配達担当者コード
    header_rec.KZBTLN1N2RITTORUHNS := 0; -- 回収実績ボトル１２Ｌ本数
    header_rec.KZBTLN8RITTORUHNS   := 0; -- 回収実績ボトル８Ｌ本数
    header_rec.DLVRYJISSEKIPRCSDD := '00000000'; -- 配達実績処理日
    header_rec.DLVRYJISSEKIMONOAPKIHONCD := NULL; -- 配達実績者ＡＰ基本コード
    header_rec.DLVRYJISSEKIMONOSHOPCD    := NULL; -- 配達実績者ショップコード
    header_rec.DLVRYJISSEKIPRCSTNSHACD   := NUll; -- 配達実績処理担当者コード
    header_rec.URISHIMEPRCSDD := '00000000'; -- 売上締処理日
    header_rec.GETSUJISHIMEDD := NULL; -- 月次締年月
    header_rec.GETSUJISHIMEDD := NULL; -- 月次締日
    header_rec.GETSUJIPRCSJISSIDD := NULL; -- 月次処理実施日
    header_rec.DNPYSTS  := '03'; -- 伝票ステータス
    header_rec.ACPTSTS  := '03'; -- 受注ステータス
    header_rec.DLVRYSTS := '00'; -- 配達ステータス
    header_rec.DATSKSMOTOKBN := DATSKSMOTOKBN_NEW; -- データ作成元区分
    header_rec.DLVRYJISSEKIRGRAYUSYOHNS := 0; -- 配達実績レギュラー有償本数
    header_rec.DLVRYJISSEKIRGRAMUSHOHNS := 0; -- 配達実績レギュラー無償本数
    header_rec.DLVRYJISSEKISRMYUSYOHNS := 0; -- 配達実績スリム有償本数
    header_rec.DLVRYJISSEKISRMMUSHOHNS := 0; -- 配達実績スリム無償本数
    header_rec.DLVRYJISSEKISTYSHNS := 0; -- 配達実績その他有償本数
    header_rec.DLVRYJISSEKISTMSHNS := 0; -- 配達実績その他無償本数
    header_rec.DLVRYJISSEKISHSUAKIN     := 0; -- 配達実績製品水売上金額
    header_rec.DLVRYJISSEKISONOTAURIKIN := 0; -- 配達実績その他売上金額
    header_rec.YUKOKBN := '1'; -- 有効区分
    /* 登録・更新者 */
    header_rec.TOSHAAPKIHONCD := userinfo_rec.APKIHONCD;  -- 登録者ＡＰ基本コード
    header_rec.TOSHASHOPCD    := userinfo_rec.SHOPCD;     -- 登録者ショップコード
    header_rec.TOSHACD        := userinfo_rec.TNSHACD;    -- 登録者コード
    header_rec.INPTSYSTMYMD   := userinfo_rec.SYSTMYMD;   -- 登録システム日付
    header_rec.INPTSYSTMJIKAN := userinfo_rec.SYSTMJIKAN; -- 登録システム時間
    header_rec.INPTUNYOYMD    := userinfo_rec.UNYOYMD;    -- 登録運用日付
    header_rec.INPTIPADRSE    := userinfo_rec.IPADRSE;    -- 登録ＩＰアドレス
    header_rec.INPTPGMID      := userinfo_rec.PGMID;      -- 登録プログラムＩＤ
    header_rec.KOSHAAPKIHONCD := userinfo_rec.APKIHONCD;  -- 更新者ＡＰ基本コード
    header_rec.KOSHASHOPCD    := userinfo_rec.SHOPCD;     -- 更新者ショップコード
    header_rec.KOSHACD        := userinfo_rec.TNSHACD;    -- 更新者コード
    header_rec.KSHNSYSTMYMD   := userinfo_rec.SYSTMYMD;   -- 更新システム日付
    header_rec.KSHNSYSTMJIKAN := userinfo_rec.SYSTMJIKAN; -- 更新システム時間
    header_rec.KSHNUNYOYMD    := userinfo_rec.UNYOYMD;    -- 更新運用日付
    header_rec.KSHNIPADRSE    := userinfo_rec.IPADRSE;    -- 更新ＩＰアドレス
    header_rec.KSHNPGMID      := userinfo_rec.PGMID;      -- 更新プログラムＩＤ
    -- 得意先マスタ参照
    SELECT RKNKINDTOKUBEtsUUNTFLG
      INTO wRknKindTokubetsuUntFlg
      FROM S02M013T
     WHERE APKIHONCD    = header_rec.APKIHONCD
       AND SHOPCD       = header_rec.SHOPCD
       AND TOKUISAKICD  = header_rec.TOKUISAKICD;
    -- ショップマスタ取得
    SELECT SHPM.URIKINHASUKEISANKBN  -- 売上金額端数計算区分
      INTO wUrikinHasuKeisanKbn
      FROM S02M046T SHPM
     WHERE SHPM.APKIHONCD = header_rec.APKIHONCD
       AND SHPM.SHOPCD    = header_rec.SHOPCD
       AND SHPM.YUKOKBN   = '1';
    -- 配達受託の場合
    IF header_rec.ITAKUAITEACPTDNPYNO IS NOT NULL THEN
      SELECT JHHT.TOKUISAKICD
            ,JHHT.DLVRYKBN
            ,TKSM.RKNKINDTOKUBETSUUNTFLG
            , ( CASE WHEN TKSM.TOKUISAKIKBN IN ('2', '3', '4', '5', '6') -- 卸対象
                     THEN '2'
                     ELSE ( CASE WHEN userinfo_rec.UNYOYMD IS NULL OR userinfo_rec.UNYOYMD >= SHPM.TAXKEISANKBNURIKIRIKAEDD
                                 THEN SHPM.SHINTAXKEISANKBNURI
                                 ELSE SHPM.GENZTAXKEISANKBNURI
                            END )
                     END ) TAXKEISANKBN
        INTO wAiteTokuisakiCd
            ,wAiteDlvryKbn
            ,wAiteRknKindTokubetsuUntFlg
            ,wAiteTaxKeisanKbn
        FROM S04T001T JHHT
        INNER JOIN S02M013T TKSM
                ON TKSM.APKIHONCD    = JHHT.APKIHONCD
               AND TKSM.SHOPCD       = JHHT.SHOPCD
               AND TKSM.TOKUISAKICD  = JHHT.TOKUISAKICD
        INNER JOIN S02M046T SHPM
                ON SHPM.APKIHONCD    = JHHT.APKIHONCD
               AND SHPM.SHOPCD       = JHHT.SHOPCD
      WHERE JHHT.APKIHONCD    = header_rec.ITAKUAITEAPKIHONCD
        AND JHHT.SHOPCD       = header_rec.ITAKUAITESHOPCD
        AND JHHT.ACPTDNPYNO   = header_rec.ITAKUAITEACPTDNPYNO
        AND JHHT.EDANO        = header_rec.ITAKUAITEEDANO;
    END IF;
    header_rec.ITAKUAITEEDANO := ( CASE WHEN header_rec.ITAKUAITEEDANO IS NULL THEN NULL
                                        ELSE header_rec.ITAKUAITEEDANO + 1
                                   END ); -- 委託相手枝番号

    -- 受託明細データを元に新しい明細データを作る
    OPEN SELECT_MEISAI;
      LOOP
        FETCH SELECT_MEISAI INTO meisai_rec;
        IF SELECT_MEISAI%ROWCOUNT = 0 THEN
           RAISE_APPLICATION_ERROR(-20015, '受託の明細データが存在しません。');
        END IF;

        -- 消費税調整
        IF (SELECT_MEISAI%NOTFOUND -- 末行
           OR ( SELECT_MEISAI%ROWCOUNT > 1 AND meisai_rec.NYURYOKUMEISAIFLG = '1' ) -- 入力行
           )
           AND oya_rec.TANPINMEISAIFLG = '0' THEN -- 前回親明細がセット商品
-- インボイス対応変更 start ABI
--          IF wKidAcptTaxSum <> oya_rec.ACPTSHOHITAXKIN THEN      -- 受注消費税調整
          -- インボイス施行日前で子商品受注消費税合計と親レコードの受注消費税金額が異なる場合、消費税調整を実行する
          IF wKidAcptTaxSum <> oya_rec.ACPTSHOHITAXKIN AND cmnInvoiceshikoymdflg = '0' THEN      -- 受注消費税調整
-- インボイス対応変更 end ABI

             oya_rec.ACPTDNPYMEISAIEDANO          := wKidLastNo + 1; -- 受注伝票明細枝番号
             IF oya_rec.ITAKUAITEACPTDNPYMEISAIEDANO IS NOT NULL THEN
                oya_rec.ITAKUAITEACPTDNPYMEISAIEDANO := wKidLastNo + 1; -- 委託相手受注明細枝番号
             END IF;
             -- 消費税調整商品コード取得
             oya_rec.SHNCD := getMojisetteiatai( '000', '000', '166', NULL, NULL, NULL, '消費税調整商品コード');
             IF oya_rec.SHNCD IS NULL THEN
                 RAISE_APPLICATION_ERROR(-20021, '消費税調整商品コード　が設定されていません。');
             END IF;
             -- 商品マスタビューからの商品情報を取得する
             SELECT
                    SHMV.LCTGRYCD -- 大分類コード
                  , SHMV.MCTGRYCD -- 中分類コード
                  , SHMV.SCTGRYCD -- 小分類コード
                  -- 標準価格：配達区分、受注日より設定
                  , ( CASE WHEN SHMV.HYOJUNPRICEKIRIKAEDD IS NULL OR header_rec.DLVRYYTIDD >= SHMV.HYOJUNPRICEKIRIKAEDD
                           THEN ( CASE header_rec.DLVRYKBN
                                       WHEN '3' THEN SHMV.SHINMOCHIKAERIPRICE -- 新持ち帰り価格
                                       ELSE SHMV.SHINHYOJUNPRICE     -- 新標準価格
                                  END )
                           ELSE ( CASE header_rec.DLVRYKBN
                                       WHEN '3' THEN SHMV.GENZMOCHIKAERIPRICE -- 現持ち帰り価格
                                       ELSE SHMV.GENZHYOJUNPRICE     -- 現標準価格
                                  END )
                      END ) HYOJUNUNT
                   , SHMV.TAXKBN  -- 税区分
              INTO
                    oya_rec.LCTGRYCD  -- 大分類コード
                  , oya_rec.MCTGRYCD  -- 中分類コード
                  , oya_rec.SCTGRYCD  -- 小分類コード
                  , oya_rec.HYOJUNUNT -- 標準価格
                  , wTaxkbn           -- 税区分
              FROM S02V001T SHMV
              WHERE SHMV.APKIHONCD   = oya_rec.APKIHONCD -- ＡＰ基本コード
                AND SHMV.SHOPCD      = oya_rec.SHOPCD    -- ショップコード
                AND SHMV.SHNCD       = oya_rec.SHNCD -- 消費税調整商品コード
                AND SHMV.SHOPYUKOKBN = '1' -- ショップ有効区分
             ;
-- インボイス対応削除 start ABI
--             IF oya_rec.TAXKBN IN ('5') THEN
--               --親商品の税区分が"5:子明細商品"の場合、商品マスタビューの税区分をセット
--               oya_rec.TAXKBN := wTaxkbn;
--             END IF;
-- インボイス対応削除 end ABI
-- インボイス対応変更 start ABI
--             IF oya_rec.TAXKBN IN ('4','5') AND header_rec.DLVRYYTIDD <= '20190930' THEN
             IF oya_rec.TAXKBN IN ('4') AND header_rec.DLVRYYTIDD <= '20190930' THEN
-- インボイス対応変更 end ABI
               oya_rec.TAXKBN := '1';
             END IF;
             -- 受注
             oya_rec.ACPTSURYO := 1; -- 受注数量
             oya_rec.HYOJUNUNT := 0; -- 標準単価
             oya_rec.ACPTUNT   := 0; -- 受注単価
             oya_rec.ACPTKIN   := 0; -- 受注金額
             oya_rec.ACPTUNTSHOHITAXKIN := 0; -- 受注単価消費税金額
             oya_rec.ACPTSHOHITAXKIN    := oya_rec.ACPTSHOHITAXKIN - wKidAcptTaxSum; -- 受注消費税金額

             -- 納品
             oya_rec.NOHINSURYO := 0; -- 納品数量
             oya_rec.NOHINUNT := 0; -- 納品単価
             oya_rec.NOHINKIN := 0; -- 納品金額
             oya_rec.NOHINUNTSHOHITAXKIN := 0; -- 納品単価消費税金額
             oya_rec.NOHINSHOHITAXKIN := 0; -- 納品消費税金額
             oya_rec.YUSYOKBN  := '0'; -- 有償区分
             oya_rec.MEISAIKBN := '3'; -- 明細区分：消費税調整
             oya_rec.NYURYOKUMEISAIFLG := '0'; -- 入力明細フラグ：展開行
             oya_rec.TANPINMEISAIFLG   := '1'; -- 単品明細フラグ：単品
             IF oya_rec.URIDNPYMEISAIEDANO IS NOT NULL THEN
                oya_rec.URIDNPYMEISAIEDANO := wKidLastNo + 1; -- 売上伝票明細枝番号
             END IF;

            IF header_rec.ITAKUAITEAPKIHONCD IS NOT NULL THEN
               oya_rec.ITAKUCMPNYACPTUNT := 0; -- 委託会社受注単価
               oya_rec.ITAKUCMPNYACPTUNTSHOHITAXKIN   := 0; -- 委託会社受注単価消費税額
               oya_rec.ITAKUCMPNYACPTSHOHITAXKIN   := 0; -- 委託会社受注消費税額
             END IF;

             -- 登録
             INSERT INTO S04T002T JHMT VALUES oya_rec;
          END IF;
        END IF;
        EXIT WHEN SELECT_MEISAI%NOTFOUND;

        -- 再配達設定
        meisai_rec.EDANO := meisai_rec.EDANO + 1;      -- 枝番号
        meisai_rec.ITAKUAITEEDANO := ( CASE WHEN meisai_rec.ITAKUAITEEDANO IS NULL THEN NULL
                                            ELSE meisai_rec.ITAKUAITEEDANO + 1
                                       END );      -- 委託相手枝番号
        meisai_rec.TOSHAAPKIHONCD := userinfo_rec.APKIHONCD;  -- 登録者ＡＰ基本コード
        meisai_rec.TOSHASHOPCD    := userinfo_rec.SHOPCD;     -- 登録者ショップコード
        meisai_rec.TOSHACD        := userinfo_rec.TNSHACD;    -- 登録者コード
        meisai_rec.INPTSYSTMYMD   := userinfo_rec.SYSTMYMD;   -- 登録システム日付
        meisai_rec.INPTSYSTMJIKAN := userinfo_rec.SYSTMJIKAN; -- 登録システム時間
        meisai_rec.INPTUNYOYMD    := userinfo_rec.UNYOYMD;    -- 登録運用日付
        meisai_rec.INPTIPADRSE    := userinfo_rec.IPADRSE;    -- 登録ＩＰアドレス
        meisai_rec.INPTPGMID      := userinfo_rec.PGMID;      -- 登録プログラムＩＤ
        meisai_rec.KOSHAAPKIHONCD := userinfo_rec.APKIHONCD;  -- 更新者ＡＰ基本コード
        meisai_rec.KOSHASHOPCD    := userinfo_rec.SHOPCD;     -- 更新者ショップコード
        meisai_rec.KOSHACD        := userinfo_rec.TNSHACD;    -- 更新者コード
        meisai_rec.KSHNSYSTMYMD   := userinfo_rec.SYSTMYMD;   -- 更新システム日付
        meisai_rec.KSHNSYSTMJIKAN := userinfo_rec.SYSTMJIKAN; -- 更新システム時間
        meisai_rec.KSHNUNYOYMD    := userinfo_rec.UNYOYMD;    -- 更新運用日付
        meisai_rec.KSHNIPADRSE    := userinfo_rec.IPADRSE;    -- 更新ＩＰアドレス
        meisai_rec.KSHNPGMID      := userinfo_rec.PGMID;      -- 更新プログラムＩＤ

        -- ACCIS2023-5847 start ABI 配布物については商品マスタビューから情報を取得しない
        IF meisai_rec.MEISAIKBN <> '1' THEN
          SELECT
            SHMV.TAXSETTEIKBN
            ,SHMV.TAXKBN
            -- 標準価格：配達区分、配達予定日より設定
            , getUnt(
                  header_rec.DLVRYKBN
                , SHMV.HYOJUNPRICEKIRIKAEDD
                , header_rec.DLVRYYTIDD
                , SHMV.GENZHYOJUNPRICE, SHMV.SHINHYOJUNPRICE
                , SHMV.GENZMOCHIKAERIPRICE, SHMV.SHINMOCHIKAERIPRICE
              ) HYOJUNUNT
          INTO
            wTaxsetteiKbn  -- 税設定区分
            ,wTaxKbn       -- 税区分
            ,wHyojunUnt -- 標準価格
          FROM
            S02V001T SHMV
          WHERE
            SHMV.APKIHONCD = meisai_rec.APKIHONCD
            AND SHMV.SHOPCD = meisai_rec.SHOPCD
            AND SHMV.SHNCD = meisai_rec.SHNCD
            AND SHMV.SHOPYUKOKBN = '1'
          ;
        END IF;
        -- ACCIS2023-5847 end ABI 配布物については商品マスタビューから情報を取得しない

-- インボイス対応変更 start ABI
--        IF wTaxKbn IN ('4','5') AND header_rec.DLVRYYTIDD <= '20190930' THEN
        IF wTaxKbn IN ('4') AND header_rec.DLVRYYTIDD <= '20190930' THEN
-- インボイス対応変更 end ABI
           wTaxKbn := '1';
        END IF;
        meisai_rec.TAXKBN := wTaxKbn;

        -- 標準単価
        meisai_rec.HYOJUNUNT    := wHyojunUnt;

        -- 展開行の場合
        IF meisai_rec.NYURYOKUMEISAIFLG = '0' THEN
-- インボイス対応削除 start ABI
--          IF oya_rec.TAXKBN <> '5' THEN
--            -- 親商品の税区分で登録
--            meisai_rec.TAXKBN := oya_rec.TAXKBN;
--          END IF;
-- インボイス対応削除 end ABI

          -- 展開マスタの単価
          wTempUnt := getTenkaiUnt(
                         meisai_rec.APKIHONCD
                       , meisai_rec.SHOPCD
                       , meisai_rec.STSHNCD
                       , header_rec.TOKUISAKICD
                       , meisai_rec.ACPTDNPYMEISAIEDANO
                       , meisai_rec.SHNCD
                       , header_rec.DLVRYYTIDD
                       , header_rec.DLVRYKBN);
          IF wTempUnt IS NOT NULL THEN
            meisai_rec.ACPTUNT := wTempUnt;
          END IF;
          -- 委託相手の展開マスタの単価
          IF header_rec.ITAKUAITEAPKIHONCD IS NOT NULL THEN
            meisai_rec.ITAKUCMPNYACPTUNT := 0;
            meisai_rec.ITAKUCMPNYACPTUNTSHOHITAXKIN := 0;
            meisai_rec.ITAKUCMPNYACPTSHOHITAXKIN := 0;
            wTempUnt := getTenkaiUnt(
                           header_rec.ITAKUAITEAPKIHONCD
                         , header_rec.ITAKUAITESHOPCD
                         , meisai_rec.STSHNCD
                         , wAiteTokuisakiCd
                         , meisai_rec.ITAKUAITEACPTDNPYMEISAIEDANO
                         , meisai_rec.SHNCD
                         , header_rec.DLVRYYTIDD
                         , wAiteDlvryKbn);
            IF wTempUnt IS NOT NULL THEN
              meisai_rec.ITAKUCMPNYACPTUNT := wTempUnt;
            END IF;
            -- 委託相手の税設定区分
            SELECT SHMV.TAXSETTEIKBN
              INTO wAiteTaxsetteiKbn  -- 税設定区分
              FROM S02V001T SHMV
             WHERE SHMV.APKIHONCD   = header_rec.ITAKUAITEAPKIHONCD
               AND SHMV.SHOPCD      = header_rec.ITAKUAITESHOPCD
               AND SHMV.SHNCD       = meisai_rec.SHNCD
               AND SHMV.SHOPYUKOKBN = '1';
          END IF;
        ELSE
          /* 入力明細行の場合 */
          -- 計上区分：売上／キャンペーン
          IF meisai_rec.KEIJOKBN IN ( '01', '02' ) THEN
            BEGIN
              SELECT
                -- 販促価格：配達区分より設定
                 ( CASE WHEN SHMV.HANSOKUKBN = '1' -- 販促区分：有り
                         AND header_rec.DLVRYYTIDD BETWEEN SHMV.HANSOKUSTRTDD AND SHMV.HANSOKUSHRYDD -- 配送予定日：販促期間内
                     THEN CASE WHEN header_rec.DLVRYKBN = '3' -- 配達区分：持ち帰り
                               THEN SHMV.HANSOKUMOCHIKAERIPRICE -- 販促持ち帰り価格
                               ELSE SHMV.HANSOKUDLVRYPRICE      -- 販促配達価格
                          END
                     ELSE NULL
                   END
                 ) HANSOKUUNT
                -- 価格区分
                ,( CASE WHEN ( SHMV.HYOJUNPRICEKIRIKAEDD IS NULL OR header_rec.DLVRYYTIDD >= SHMV.HYOJUNPRICEKIRIKAEDD)
                        THEN SHMV.SHINPRICEKBN -- 新価格区分
                        ELSE SHMV.GENZPRICEKBN -- 現価格区分
                   END ) PRICEKBN
                -- オーブン価格：配達区分、受注日より設定
                , getUnt(
                      header_rec.DLVRYKBN
                    , SHMV.OPNPRICEKIRIKAEDD
                    , header_rec.DLVRYYTIDD
                    , SHMV.GENZOPNPRICE, SHMV.SHINOPNPRICE
                    , SHMV.GENZOPNMOCHIKAERIPRICE, SHMV.SHINOPNMOCHIKAERIPRICE
                  ) OPENUNT
                , SHMV.HANSOKUYUSENKBN -- 販促優先区分
                , SHMV.STKBN                -- セット区分
                -- 得意先単価
                , getUnt(
                      header_rec.DLVRYKBN
                    , header_rec.DLVRYYTIDD
                    , header_rec.DLVRYYTIDD
                    , TSTM.DLVRYPRICE, TSTM.DLVRYPRICE
                    , TSTM.MOCHIKAERIPRICE, TSTM.MOCHIKAERIPRICE
                ) TOKUISAKIUNT
                -- 得意先セット単価
                , getUnt(
                      header_rec.DLVRYKBN
                    , header_rec.DLVRYYTIDD
                    , header_rec.DLVRYYTIDD
                    , TSSM.DLVRYPRICE, TSSM.DLVRYPRICE
                    , TSSM.MOCHIKAERIPRICE, TSSM.MOCHIKAERIPRICE
                ) TOKUISAKISETUNT
              INTO wHansokuunt               -- 販促単価
                 , wPricekbn                 -- 価格区分
                 , wOpenUnt                  -- オーブン単価
                 , wHansokuYusenKbn          -- 販促優先区分
                 , wStKbn                    -- セット区分
                 , wTokuisakiUnt
                 , wTokuisakiSetUnt
              FROM S02V001T SHMV
              LEFT JOIN S02M009T TSTM
                     ON TSTM.APKIHONCD      = SHMV.APKIHONCD
                    AND TSTM.SHOPCD         = SHMV.SHOPCD
                    AND TSTM.SHNCD          = SHMV.SHNCD
                    AND TSTM.TOKUISAKICD    = header_rec.TOKUISAKICD
                    AND header_rec.DLVRYYTIDD BETWEEN TSTM.TEKIYOSTRTDD AND TSTM.TEKIYOSHRYDD
                    AND TSTM.YUKOKBN        = '1'
              LEFT JOIN S02M066T TSSM
                     ON TSSM.APKIHONCD      = SHMV.APKIHONCD
                    AND TSSM.SHOPCD         = SHMV.SHOPCD
                    AND TSSM.STSHNCD        = SHMV.SHNCD
                    AND TSSM.TOKUISAKICD    = header_rec.TOKUISAKICD
                    AND header_rec.DLVRYYTIDD BETWEEN TSSM.TEKIYOSTRTDD AND TSSM.TEKIYOSHRYDD
                    AND TSSM.YUKOKBN        = '1'
              WHERE SHMV.APKIHONCD = meisai_rec.APKIHONCD -- ＡＰ基本コード
                AND SHMV.SHOPCD    = meisai_rec.SHOPCD    -- ショップコード
                AND SHMV.SHNCD     = meisai_rec.SHNCD     -- 商品コード
                AND SHMV.SHOPYUKOKBN = '1' -- ショップ有効区分
              ;
            EXCEPTION
              WHEN TOO_MANY_ROWS THEN
                RAISE_APPLICATION_ERROR(-20015, '得意先別セット単価が複数見つかりました。');
            END;

            -- 受注単価
            meisai_rec.ACPTUNT      := NULL;
            -- 特別単価あり
            IF wRknKindTokubetsuUntFlg = '1' THEN
              IF wStKbn = '1' THEN
                IF wTokuisakiSetUnt IS NOT NULL THEN
                  meisai_rec.ACPTUNT := wTokuisakiSetUnt;
                  meisai_rec.KEIJOKBN := '01';
                END IF;
              ELSE
                IF wTokuisakiUnt IS NOT NULL THEN
                  meisai_rec.ACPTUNT := wTokuisakiUnt;
                  meisai_rec.KEIJOKBN := '01';
                END IF;
              END IF;
            END IF;
            -- 得意先別商品別単価がない場合
            IF meisai_rec.ACPTUNT IS NULL THEN
              -- 計上区分がキャンペーンの場合：販促単価／0
              IF meisai_rec.KEIJOKBN = '02' THEN
                meisai_rec.ACPTUNT := NVL(wHansokuunt, 0); -- 受注単価：販促単価
              END IF;
              -- 計上区分が売上の場合：販促単価／ショップ単価
              IF meisai_rec.KEIJOKBN = '01' THEN
                meisai_rec.ACPTUNT  := ( CASE WHEN wPricekbn = '1'
                                              THEN meisai_rec.HYOJUNUNT
                                              ELSE wOpenUnt
                                         END ); -- 受注単価：標準／オーブン単価
                IF wHansokuUnt IS NOT NULL AND wHansokuYusenKbn = '1' THEN -- 販促優先区分
                  meisai_rec.ACPTUNT  := wHansokuUnt; -- 受注単価：販促単価
                  meisai_rec.KEIJOKBN := '02';        -- 計上区分：キャンペーン
                END IF;
              END IF;
            END IF;

            -- 委託会社受注単価
            IF header_rec.ITAKUAITEAPKIHONCD IS NOT NULL THEN
              meisai_rec.ITAKUCMPNYACPTUNT := 0;
              meisai_rec.ITAKUCMPNYACPTUNTSHOHITAXKIN := 0;
              meisai_rec.ITAKUCMPNYACPTSHOHITAXKIN := 0;
              BEGIN
                SELECT
                  -- 販促価格：配達区分より設定
                   ( CASE WHEN SHMV.HANSOKUKBN = '1' -- 販促区分：有り
                           AND header_rec.DLVRYYTIDD BETWEEN SHMV.HANSOKUSTRTDD AND SHMV.HANSOKUSHRYDD -- 配送予定日：販促期間内
                       THEN CASE WHEN header_rec.DLVRYKBN = '3' -- 配達区分：持ち帰り
                                 THEN SHMV.HANSOKUMOCHIKAERIPRICE -- 販促持ち帰り価格
                                 ELSE SHMV.HANSOKUDLVRYPRICE      -- 販促配達価格
                            END
                       ELSE NULL
                     END
                   ) HANSOKUUNT
                  -- 価格区分
                  ,( CASE WHEN ( SHMV.HYOJUNPRICEKIRIKAEDD IS NULL OR header_rec.DLVRYYTIDD >= SHMV.HYOJUNPRICEKIRIKAEDD)
                          THEN SHMV.SHINPRICEKBN -- 新価格区分
                          ELSE SHMV.GENZPRICEKBN -- 現価格区分
                     END ) PRICEKBN
                  -- オーブン価格：配達区分、受注日より設定
                  , getUnt(
                        wAiteDlvryKbn
                      , SHMV.OPNPRICEKIRIKAEDD
                      , header_rec.DLVRYYTIDD
                      , SHMV.GENZOPNPRICE, SHMV.SHINOPNPRICE
                      , SHMV.GENZOPNMOCHIKAERIPRICE, SHMV.SHINOPNMOCHIKAERIPRICE
                    ) OPENUNT
                  , SHMV.HANSOKUYUSENKBN -- 販促優先区分
                  , SHMV.STKBN                -- セット区分
                  -- 得意先単価
                  , getUnt(
                        wAiteDlvryKbn
                      , header_rec.DLVRYYTIDD
                      , header_rec.DLVRYYTIDD
                      , TSTM.DLVRYPRICE, TSTM.DLVRYPRICE
                      , TSTM.MOCHIKAERIPRICE, TSTM.MOCHIKAERIPRICE
                  ) TOKUISAKIUNT
                  -- 得意先セット単価
                  , getUnt(
                        wAiteDlvryKbn
                      , header_rec.DLVRYYTIDD
                      , header_rec.DLVRYYTIDD
                      , TSSM.DLVRYPRICE, TSSM.DLVRYPRICE
                      , TSSM.MOCHIKAERIPRICE, TSSM.MOCHIKAERIPRICE
                  ) TOKUISAKISETUNT
                  , TAXSETTEIKBN
                INTO wHansokuunt               -- 販促単価
                   , wPricekbn                 -- 価格区分
                   , wOpenUnt                  -- オーブン単価
                   , wHansokuYusenKbn          -- 販促優先区分
                   , wStKbn                    -- セット区分
                   , wTokuisakiUnt
                   , wTokuisakiSetUnt
                   , wAiteTaxSetteiKbn
                FROM S02V001T SHMV
                LEFT JOIN S02M009T TSTM
                       ON TSTM.APKIHONCD      = SHMV.APKIHONCD
                      AND TSTM.SHOPCD         = SHMV.SHOPCD
                      AND TSTM.SHNCD          = SHMV.SHNCD
                      AND TSTM.TOKUISAKICD    = wAiteTokuisakiCd
                      AND header_rec.DLVRYYTIDD BETWEEN TSTM.TEKIYOSTRTDD AND TSTM.TEKIYOSHRYDD
                      AND TSTM.YUKOKBN        = '1'
                LEFT JOIN S02M066T TSSM
                       ON TSSM.APKIHONCD      = SHMV.APKIHONCD
                      AND TSSM.SHOPCD         = SHMV.SHOPCD
                      AND TSSM.STSHNCD        = SHMV.SHNCD
                      AND TSSM.TOKUISAKICD    = wAiteTokuisakiCd
                      AND header_rec.DLVRYYTIDD BETWEEN TSSM.TEKIYOSTRTDD AND TSSM.TEKIYOSHRYDD
                      AND TSSM.YUKOKBN        = '1'
                WHERE SHMV.APKIHONCD = header_rec.ITAKUAITEAPKIHONCD -- ＡＰ基本コード
                  AND SHMV.SHOPCD    = header_rec.ITAKUAITESHOPCD    -- ショップコード
                  AND SHMV.SHNCD     = meisai_rec.SHNCD              -- 商品コード
                  AND SHMV.SHOPYUKOKBN = '1' -- ショップ有効区分
                ;
              EXCEPTION
                WHEN TOO_MANY_ROWS THEN
                  RAISE_APPLICATION_ERROR(-20015, '得意先別セット単価が複数見つかりました。（委託先）');
              END;
              wTempUnt := null;
              -- 特別単価あり
              IF wAiteRknKindTokubetsuUntFlg = '1' THEN
                IF wStKbn = '1' THEN
                  IF wTokuisakiSetUnt IS NOT NULL THEN
                    wTempUnt := wTokuisakiSetUnt;
                  END IF;
                ELSE
                  IF wTokuisakiUnt IS NOT NULL THEN
                    wTempUnt := wTokuisakiUnt;
                  END IF;
                END IF;
              END IF;
              -- 得意先別商品別単価がない場合
              IF wTempUnt IS NULL THEN
                -- 計上区分がキャンペーンの場合：販促単価／0
                IF meisai_rec.KEIJOKBN = '02' THEN
                  wTempUnt := NVL(wHansokuunt, 0); -- 受注単価：販促単価
                END IF;
                -- 計上区分が売上の場合：販促単価／ショップ単価
                IF meisai_rec.KEIJOKBN = '01' THEN
                  wTempUnt  := ( CASE WHEN wPricekbn = '1'
                                      THEN meisai_rec.HYOJUNUNT
                                      ELSE wOpenUnt
                                 END ); -- 受注単価：標準／オーブン単価
                  IF wHansokuUnt IS NOT NULL AND wHansokuYusenKbn = '1' THEN -- 販促優先区分
                    wTempUnt  := wHansokuUnt; -- 受注単価：販促単価
                  END IF;
                END IF;
              END IF;
              --
              IF wTempUnt IS NOT NULL THEN
                meisai_rec.ITAKUCMPNYACPTUNT := wTempUnt;
              END IF;
            END IF;
          END IF;
        END IF;

-- ACCIS2023-5847 start ABI 配布物の明細は受注金額、受注消費税金額等の計算を行わない
        IF meisai_rec.MEISAIKBN <> '1' THEN
        -- 受注金額
-- 本線対応変更 start ABI
--        meisai_rec.ACPTKIN := S01SyohizeiService.roundValue(meisai_rec.ACPTUNT * meisai_rec.ACPTSURYO, wUrikinHasuKeisanKbn);
          meisai_rec.ACPTKIN := Z1808B01.roundValue(meisai_rec.ACPTUNT * meisai_rec.ACPTSURYO, wUrikinHasuKeisanKbn);
-- 本線対応変更 end ABI

          -- 受注消費税
          setTax( meisai_rec -- 明細行
               , meisai_rec.ACPTSURYO            -- 数量
               , meisai_rec.ACPTUNT              -- 単価
               , meisai_rec.APKIHONCD
               , meisai_rec.SHOPCD
               , meisai_rec.TAXKBN               -- 税区分
               , wTaxsetteiKbn                   -- 税設定区分
               , header_rec.TAXKEISANKBN         -- 税計算区分
               , header_rec.DLVRYYTIDD           -- 切替日：配達予定日
               , header_rec.DLVRYKBN             -- 配達区分
-- インボイス対応追加 start ABI
                  , CASE WHEN meisai_rec.NYURYOKUMEISAIFLG = '0' THEN '1' ELSE '0' END
                  , '0'
-- インボイス対応追加 end ABI
               , meisai_rec.ACPTUNTSHOHITAXKIN   -- 受注単価消費税金額
               , meisai_rec.ACPTSHOHITAXKIN      -- 受注消費税金額
               );

          -- 受注消費税合計を取得
          IF  meisai_rec.NYURYOKUMEISAIFLG = '1' THEN
             wAcptTaxSum := wAcptTaxSum + meisai_rec.ACPTSHOHITAXKIN;
          END IF;

          -- 配達受託の場合委託相手の消費税を算出
          IF header_rec.ITAKUAITEAPKIHONCD IS NOT NULL THEN
            -- 委託会社受注消費税
            setTax( meisai_rec -- 明細行
                  , meisai_rec.ACPTSURYO            -- 数量
                  , meisai_rec.ITAKUCMPNYACPTUNT    -- 単価
                  , header_rec.ITAKUAITEAPKIHONCD
                  , header_rec.ITAKUAITESHOPCD
                  , meisai_rec.TAXKBN               -- 税区分
                  , wAiteTaxSetteiKbn               -- 税設定区分
                  , wAiteTaxKeisanKbn               -- 税計算区分
                  , header_rec.DLVRYYTIDD           -- 切替日：配達予定日
                  , wAiteDlvryKbn                   -- 配達区分
-- インボイス対応追加 start ABI
                  , '1'
                  , '0'
-- インボイス対応追加 end ABI
                  , meisai_rec.ITAKUCMPNYACPTUNTSHOHITAXKIN   -- 受注単価消費税金額
                  , meisai_rec.ITAKUCMPNYACPTSHOHITAXKIN      -- 受注消費税金額
            );
          END IF;

-- インボイス対応追加 start ABI
          -- インボイス施行日後で展開行の場合、受注消費税金額に0を設定
          IF cmnInvoiceshikoymdflg = '1' AND meisai_rec.NYURYOKUMEISAIFLG = '0' THEN
             meisai_rec.ACPTSHOHITAXKIN           := 0; -- 受注消費税金額
             meisai_rec.ITAKUCMPNYACPTSHOHITAXKIN := 0; -- 委託会社受注消費税金額
          END IF;
-- インボイス対応追加 end ABI
        END IF;
-- ACCIS2023-5847 end ABI

-- ACCIS2023-5852 start ABI 配布物は新しく明細を登録せず、セット品の処理も行わない
        IF meisai_rec.MEISAIKBN <> '1' THEN
-- ACCIS2023-5852 end ABI

          -- 登録
          INSERT INTO S04T002T VALUES meisai_rec;

          -- 親商品情報更新
          IF meisai_rec.NYURYOKUMEISAIFLG = '1' THEN
             wKidAcptTaxSum  := 0; -- 子商品受注消費税合計
             wKidLastNo      := meisai_rec.ACPTDNPYMEISAIEDANO; -- 子商品最終明細枝番号
             oya_rec         := meisai_rec;
          ELSE
             wKidAcptTaxSum  := wKidAcptTaxSum + meisai_rec.ACPTSHOHITAXKIN;   -- 子商品受注消費税合計
             wKidLastNo      := meisai_rec.ACPTDNPYMEISAIEDANO; -- 子商品最終明細枝番号
          END IF;

-- ACCIS2023-5852 start ABI 配布物は新しく明細を登録せず、セット品の処理も行わない
        END IF;
-- ACCIS2023-5852 end ABI

        -- 配布物マスタ更新
        IF meisai_rec.MEISAIKBN = '1' THEN
           updateHaifubutsuTKM( header_rec, meisai_rec );
        END IF;

        IF meisai_rec.MEISAIKBN = '0' AND meisai_rec.TANPINMEISAIFLG = '1'
           AND meisai_rec.LCTGRYCD = '01' AND meisai_rec.MCTGRYCD = '001' THEN
           -- 配達拠点受注アラームテーブル登録
           updateKyotenAlarm ( meisai_rec.ACPTSURYO
                             , header_rec
                             , meisai_rec);
        END IF;

    END LOOP;
    CLOSE SELECT_MEISAI;

    -- 担当者受注アラームテーブル
    IF header_rec.DLVRYKYOTENCD IS NOT NULL AND header_rec.DLVRYTNSHACD IS NOT NULL THEN -- 配達拠点・配達担当者設定あり
       updateTnshaAlarm( 1, header_rec);
    END IF;
    -- 配達エリア受注アラームテーブル
    IF header_rec.DLVRYKYOTENCD IS NOT NULL AND header_rec.DLVRYAREACD IS NOT NULL THEN -- 配達拠点・配達担当者設定あり
       updateAreaAlarm( 1, header_rec);
    END IF;

    -- 受注配達テーブル
    header_rec.ACPTSHOHITAXKIN := wAcptTaxSum;
-- インボイス対応追加 start ABI
    header_rec.ACPTIPPANNUKIKIN    := cmnAcptippannukikin;    -- 受注一般税抜金額
    header_rec.ACPTIPPANTAXKIN     := cmnAcptippantaxkin;     -- 受注一般消費税金額
    header_rec.ACPTKEIGENNUKIKIN   := cmnAcptkeigennukikin;   -- 受注軽減税抜金額
    header_rec.ACPTKEIGENTAXKIN    := cmnAcptkeigentaxkin;    -- 受注軽減消費税金額
    header_rec.ACPTHIKAZEIURIIKIN  := cmnAcpthikazeiuriikin;  -- 受注非課税金額
    header_rec.ACPTFUKAZEIURIIKIN  := cmnAcptfukazeiuriikin;  -- 受注不課税売上金額
    header_rec.NOHINIPPANNUKIKIN   := cmnNohinippannukikin;   -- 納品一般税抜金額
    header_rec.NOHINIPPANTAXKIN    := cmnNohinippantaxkin;    -- 納品一般消費税金額
    header_rec.NOHINKEIGENNUKIKIN  := cmnNohinkeigennukikin;  -- 納品軽減税抜金額
    header_rec.NOHINKEIGENTAXKIN   := cmnNohinkeigentaxkin;   -- 納品軽減消費税金額
    header_rec.NOHINHIKAZEIURIIKIN := cmnNohinhikazeiuriikin; -- 納品非課税金額
    header_rec.NOHINFUKAZEIURIIKIN := cmnNohinfukazeiuriikin; -- 納品不課税売上金額
-- インボイス対応追加 end ABI
    INSERT INTO S04T001T VALUES header_rec;

-- インボイス対応追加 start ABI
    -- 変数の初期化
    cmnAcptippannukikin    := 0; -- 受注一般税抜金額
    cmnAcptippantaxkin     := 0; -- 受注一般消費税金額
    cmnAcptkeigennukikin   := 0; -- 受注軽減税抜金額
    cmnAcptkeigentaxkin    := 0; -- 受注軽減消費税金額
    cmnAcpthikazeiuriikin  := 0; -- 受注非課税金額
    cmnAcptfukazeiuriikin  := 0; -- 受注不課税売上金額
    cmnNohinippannukikin   := 0; -- 納品一般税抜金額
    cmnNohinippantaxkin    := 0; -- 納品一般消費税金額
    cmnNohinkeigennukikin  := 0; -- 納品軽減税抜金額
    cmnNohinkeigentaxkin   := 0; -- 納品軽減消費税金額
    cmnNohinhikazeiuriikin := 0; -- 納品非課税金額
    cmnNohinfukazeiuriikin := 0; -- 納品不課税売上金額
-- インボイス対応追加 end ABI

    EXCEPTION
      WHEN OTHERS THEN
        IF SELECT_MEISAI%ISOPEN THEN
           CLOSE SELECT_MEISAI;
        END IF;
        RAISE_APPLICATION_ERROR(-20012, SQLERRM || '再配達データ作成時エラーが発生しました。');
  END processSaihaitatsu;

  /*****************************************************************************/
  /* プロシージャ名：売上データ作成処理                                            */
  /* 引数（ＩＮ）      ：work_rec        配達実績データヘッダ                      */
  /*                  ：tokuisakiMST    得意先マスタ                            */
  /*                  ：iKaishuhohokbn  回収方法区分                            */
  /*****************************************************************************/
  PROCEDURE processUriage( work_rec     IN S05W001T%rowtype -- 配達実績データヘッダ
                         , tokuisakiMST IN S02M013T%rowtype -- 得意先マスタ
                         , iKaishuhohokbn  IN VARCHAR2 -- 回収方法区分
  ) IS
  /* 変数宣言 */
  allZero BOOLEAN := TRUE; -- 明細納品数量オールゼロ
-- インボイス対応追加 start ABI
  uriSankoushohitaxkin  NUMBER(11,0)     := 0;    -- 売上参考消費税金額
-- インボイス対応追加 end ABI
  /* CURSOR：受注配達テーブル */
  CURSOR SELECT_ACPTD IS
    SELECT JHTT.*
         , DKTM.KYOTENSOKOCD
         , DTNM.TNSHASHARYOSOKOCD -- 担当者車両倉庫コード
         , SHPM.ZAIKOKANRIKBN -- 在庫管理区分
      FROM S04T001T JHTT
      LEFT OUTER JOIN S02M031T DKTM -- 配達拠点マスタ
              ON ( DKTM.APKIHONCD  = JHTT.DLVRYKANKATSUAPKIHONCD
                   AND DKTM.SHOPCD = JHTT.DLVRYKANKATSUSHOPCD
                   AND DKTM.DLVRYKYOTENCD = JHTT.DLVRYKYOTENCD
                 )
      LEFT OUTER JOIN S02M003T DTNM -- 担当者マスタ（配達担当者）
              ON ( DTNM.APKIHONCD  = JHTT.APKIHONCD
                   AND DTNM.SHOPCD = JHTT.SHOPCD
                   AND DTNM.TNSHACD = JHTT.DLVRYTNSHACD
                 )
      LEFT OUTER JOIN S02M046T SHPM -- ショップマスタ
              ON ( SHPM.APKIHONCD  = JHTT.APKIHONCD
                   AND SHPM.SHOPCD = JHTT.SHOPCD
                 )
     WHERE JHTT.APKIHONCD  = work_rec.APKIHONCD
       AND JHTT.SHOPCD     = work_rec.SHOPCD
       AND JHTT.ACPTDNPYNO = work_rec.ACPTDNPYNO
       AND JHTT.EDANO      = work_rec.EDANO
       AND JHTT.YUKOKBN = '1'
  ;
  /* CURSOR：受注配達明細テーブル */
  CURSOR SELECT_ACPTM IS
    SELECT JHMT.*
      FROM S04T002T JHMT
     WHERE JHMT.APKIHONCD  = work_rec.APKIHONCD
       AND JHMT.SHOPCD     = work_rec.SHOPCD
       AND JHMT.ACPTDNPYNO = work_rec.ACPTDNPYNO
       AND JHMT.EDANO      = work_rec.EDANO
       AND JHMT.MEISAIKBN IN ('0', '3') -- 商品、消費税調整のみを対象とする
       AND JHMT.YUKOKBN = '1'
  ;
  BEGIN
    FOR acpt_rec IN SELECT_ACPTD LOOP
      allZero := TRUE;
-- インボイス対応追加 start ABI
        uriSankoushohitaxkin := 0;
-- インボイス対応追加 end ABI
      -- 明細テーブル
      FOR meisai_rec IN SELECT_ACPTM LOOP
        IF meisai_rec.NOHINSURYO <> 0 THEN
           allZero := FALSE;
           -- 入出庫実績テーブル
           INSERT INTO S11T004T (
               APKIHONCD -- ＡＰ基本コード
             , SHOPCD -- ショップコード
             , NYUSHUKKODNPYNO -- 入出庫伝票番号
             , NYUSHUKKODNPYMEISAINO -- 入出庫伝票明細番号
             , NYUSHUKKODNPYMEISAIEDANO -- 入出庫伝票明細枝番号
             , NYUSHUKKOKBN -- 入出庫区分
             , LCTGRYCD -- 大分類コード
             , MCTGRYCD -- 中分類コード
             , SCTGRYCD -- 小分類コード
             , SHNCD -- 商品コード
             , SURYO -- 数量
             , UNT -- 単価
             , KIN -- 金額
             , MEISAIBIKO -- 明細備考
             , KATASIKI -- 型式
             , ZAIKOKSHNSUMIFLG -- 在庫更新済フラグ
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
                 meisai_rec.APKIHONCD -- ＡＰ基本コード
               , meisai_rec.SHOPCD -- ショップコード
               , meisai_rec.ACPTDNPYNO -- 入出庫伝票番号
               , meisai_rec.ACPTDNPYMEISAINO -- 入出庫伝票明細番号
               , meisai_rec.ACPTDNPYMEISAIEDANO -- 入出庫伝票明細枝番号
               , '2' -- 入出庫区分
               , meisai_rec.LCTGRYCD -- 大分類コード
               , meisai_rec.MCTGRYCD -- 中分類コード
               , meisai_rec.SCTGRYCD -- 小分類コード
               , meisai_rec.SHNCD -- 商品コード
               , meisai_rec.NOHINSURYO -- 数量
               , meisai_rec.NOHINUNT -- 単価
               , meisai_rec.NOHINKIN -- 金額
               , meisai_rec.MEISAIBIKO -- 明細備考
               , meisai_rec.KATASIKI -- 型式
               , '1' -- 在庫更新済フラグ
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

           -- 売上明細テーブル
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
             , JUTAKUDLVRYCSTKBN -- 顧客区分
-- インボイス対応追加 end ABI
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
                 meisai_rec.APKIHONCD -- ＡＰ基本コード
               , meisai_rec.SHOPCD -- ショップコード
               , meisai_rec.ACPTDNPYNO -- 売上伝票番号
               , meisai_rec.EDANO -- 枝番号
               , meisai_rec.ACPTDNPYMEISAINO -- 売上伝票明細番号
               , meisai_rec.ACPTDNPYMEISAIEDANO -- 売上伝票明細枝番号
               , meisai_rec.LCTGRYCD -- 大分類コード
               , meisai_rec.MCTGRYCD -- 中分類コード
               , meisai_rec.SCTGRYCD -- 小分類コード
               , meisai_rec.SHNCD -- 商品コード
               , meisai_rec.KEIJOKBN -- 計上区分
               , meisai_rec.NOHINSURYO -- 数量
               , meisai_rec.HYOJUNUNT -- 標準単価
               , meisai_rec.NOHINUNT -- 単価
               , meisai_rec.NOHINKIN -- 金額
               , meisai_rec.NOHINUNTSHOHITAXKIN -- 単価消費税金額
-- インボイス対応変更 start ABI
               --, meisai_rec.NOHINSHOHITAXKIN -- 消費税金額
               , CASE WHEN ( acpt_rec.ACPTTHDAIBIKIFLG = '1' OR acpt_rec.SINGLDNPYFLG = '1') THEN -- 受注時代引フラグが"1"、または単体伝票締めフラグが"1"
                   0
                 ELSE
                   CASE WHEN cmnInvoiceshikoymdflg = '0' THEN -- インボイス施行日前の場合
                     meisai_rec.NOHINSHOHITAXKIN
                   ELSE
                     0
                   END
                 END                           -- 消費税金額
-- インボイス対応変更 end ABI
-- インボイス対応追加 start ABI
               , CASE WHEN ( acpt_rec.ACPTTHDAIBIKIFLG = '1' OR acpt_rec.SINGLDNPYFLG = '1') THEN -- 受注時代引フラグが"1"、または単体伝票締めフラグが"1"
                   meisai_rec.NOHINSHOHITAXKIN
                 ELSE
                   CASE WHEN cmnInvoiceshikoymdflg = '0' THEN -- インボイス施行日前の場合
                     0
                   ELSE
                     meisai_rec.NOHINSHOHITAXKIN
                   END
                 END                           -- 参考消費税金額
-- インボイス対応追加 end ABI
               , meisai_rec.TAXKBN -- 税区分
               , meisai_rec.MEISAIKBN -- 明細区分
               , meisai_rec.NYURYOKUMEISAIFLG -- 入力明細フラグ
               , meisai_rec.TANPINMEISAIFLG -- 単品明細フラグ
               , meisai_rec.MEISAIBIKO -- 明細備考
               , meisai_rec.STSHNCD -- セット商品コード
               , meisai_rec.KATASIKI -- 型式
               , NULL -- 仕入数量
               , NULL -- 仕入金額
               , NULL -- 仕入単価
               , NULL -- 仕入消費税金額
               , '1' -- 在庫更新済フラグ
-- インボイス対応追加 start ABI
               , tokuisakiMST.TOKUISAKIKBN -- 得意先区分
               , CASE WHEN tokuisakiMST.TOKUISAKIKBN ＝ '1' AND tokuisakiMST.JUTAKUDLVRYCSTKBN = '4' AND acpt_rec.ITAKUAITEACPTDNPYNO IS NULL THEN
                   '1'
                 ELSE
                   tokuisakiMST.JUTAKUDLVRYCSTKBN
                 END -- 顧客区分
-- インボイス対応追加 end ABI
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

-- インボイス対応追加 start ABI
           -- 構成商品以外の場合、売上参考消費税金額に受注配達明細データの納品消費税金額を加算
           IF NOT(meisai_rec.NYURYOKUMEISAIFLG = '0' and meisai_rec.TANPINMEISAIFLG = '1') THEN
             uriSankoushohitaxkin := uriSankoushohitaxkin + meisai_rec.NOHINSHOHITAXKIN;
           END IF;
-- インボイス対応追加 end ABI
        END IF;
      END LOOP;

      IF NOT allZero THEN
        -- 入出庫実績テーブル
        INSERT INTO S11T003T (
               APKIHONCD -- ＡＰ基本コード
             , SHOPCD    -- ショップコード
             , NYUSHUKKODNPYNO -- 入出庫伝票番号
             , NYUSHUKKOKBN -- 入出庫区分
             , TORIHIKIKBN -- 取引区分
             , NYUKAKBN -- 入荷区分
             , NYUKOSAKISIIRECD -- 入庫先仕入コード
             , NYUKOSAKISOKOCD -- 入庫先倉庫コード
             , NYUSHUKKODD -- 入出庫日
             , SHUKKOSAKISOKOCD -- 出庫先倉庫コード
             , SHUKKOSAKIKEIHICD -- 出庫先経費コード
             , JISSEKITNSHACD -- 実績担当者コード
             , KIN -- 金額
             , GETSUJISHIMEDYDM    -- 月次締年月
             , GETSUJISHIMEDD    -- 月次締日
             , GETSUJIPRCSJISSIDD -- 月次処理実施日
             , DNPYBIKO -- 伝票備考
             , DATSKSMOTOKBN -- データ作成元区分
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
                 acpt_rec.APKIHONCD -- ＡＰ基本コード
               , acpt_rec.SHOPCD    -- ショップコード
               , acpt_rec.ACPTDNPYNO -- 入出庫伝票番号
               , '2' -- 入出庫区分：出庫(2)
               , '6' -- 取引区分：出庫(2)
               , NULL -- 入荷区分
               , NULL -- 入庫先仕入コード
               -- 入庫先倉庫コード
               , ( CASE WHEN acpt_rec.ZAIKOKANRIKBN = '2' -- 在庫管理区分：担当者倉庫あり
                        THEN NVL(acpt_rec.TNSHASHARYOSOKOCD, acpt_rec.KYOTENSOKOCD) -- 担当者車両倉庫コード
                        ELSE acpt_rec.KYOTENSOKOCD -- 拠点倉庫コード
                   END )
               , acpt_rec.DLVRYDD -- 入出庫日
               -- 出庫先倉庫コード
               , ( CASE WHEN acpt_rec.ZAIKOKANRIKBN = '2' -- 在庫管理区分：担当者倉庫あり
                        THEN NVL(acpt_rec.TNSHASHARYOSOKOCD, acpt_rec.KYOTENSOKOCD) -- 担当者車両倉庫コード
                        ELSE acpt_rec.KYOTENSOKOCD -- 拠点倉庫コード
                   END )
               -- 出庫先経費コード
               , ( CASE WHEN tokuisakiMST.TOKUISAKIKBN = '9' THEN acpt_rec.TOKUISAKICD
                    ELSE NULL
               END )
               -- 実績担当者コード
               , ( CASE WHEN tokuisakiMST.URIKEIJOTNSHAKBN = '2' THEN tokuisakiMST.EIGYOTNSHACD
                    ELSE acpt_rec.DLVRYTNSHACD
               END )
               , acpt_rec.NOHINKIN -- 金額
               , NULL -- 月次締年月
               , NULL -- 月次締日
               -- 月次処理実施日
               , ( CASE WHEN iKaishuhohokbn = '7' THEN 'DAIBIKI'
                        ELSE '00000000'
                   END )
               , acpt_rec.PRNTMEMON1 -- 伝票備考
               , DATSKSMOTOKBN_NEW -- データ作成元区分
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

        -- 売上テーブル
        INSERT INTO S06T001T URIT (
               APKIHONCD -- ＡＰ基本コード
             , SHOPCD -- ショップコード
             , URIDNPYNO -- 売上伝票番号
             , EDANO -- 枝番号
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
-- インボイス対応追加 start ABIb
             , NEBIKISHIMEDYDM    -- 値引締年月
             , NEBIKISHIMEDD      -- 値引締日
             , NEBIKIPRCSJISSIDD  -- 値引処理実施日
-- インボイス対応追加 end ABI
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
             , ACPTDNPYNO -- 受注伝票番号
             , ACPTDNPYEDANO -- 受注伝票枝番号
        ) VALUES (
                 acpt_rec.APKIHONCD -- ＡＰ基本コード
               , acpt_rec.SHOPCD -- ショップコード
               , acpt_rec.ACPTDNPYNO -- 売上伝票番号
               , acpt_rec.EDANO -- 枝番号
               , acpt_rec.TORIHIKIKBN -- 取引区分
               , acpt_rec.TOKUISAKICD -- 得意先コード
               , NULL -- 仕入先コード
               -- 入荷元コード
               , ( CASE WHEN acpt_rec.ZAIKOKANRIKBN = '2' -- 在庫管理区分：担当者倉庫あり
                        THEN NVL(acpt_rec.TNSHASHARYOSOKOCD, acpt_rec.KYOTENSOKOCD) -- 担当者車両倉庫コード
                        ELSE acpt_rec.KYOTENSOKOCD -- 拠点倉庫コード
                   END )
               , acpt_rec.DLVRYDD -- 売上日
               , acpt_rec.ACPTDD -- 受注日
               , acpt_rec.NOHINKIN -- 売上金額
-- インボイス対応変更 start ABI
               --, acpt_rec.NOHINSHOHITAXKIN -- 消費税金額
               , CASE WHEN ( acpt_rec.ACPTTHDAIBIKIFLG = '1' OR acpt_rec.SINGLDNPYFLG = '1') THEN -- 代引きの場合、0固定
                   0
                 ELSE -- 代引き以外の場合、インボイス施行日によって設定値を変更
                   CASE WHEN cmnInvoiceshikoymdflg = '0' THEN
                     acpt_rec.NOHINSHOHITAXKIN
                   ELSE
                     0
                   END
                 END                         -- 消費税金額
-- インボイス対応変更 end ABI
-- インボイス対応追加 start ABI
               , CASE WHEN ( acpt_rec.ACPTTHDAIBIKIFLG = '1' OR acpt_rec.SINGLDNPYFLG = '1') THEN -- 代引きの場合、参考消費税金額を設定
                   uriSankoushohitaxkin
                 ELSE -- 代引き以外の場合、インボイス施行日によって設定値を変更
                   CASE WHEN cmnInvoiceshikoymdflg = '0' THEN
                     0
                   ELSE
                     uriSankoushohitaxkin
                   END
                 END                         -- 参考消費税金額
-- インボイス対応追加 end ABI
               , acpt_rec.ACPTTNSHACD -- 受注担当者コード
               , acpt_rec.DLVRYTNSHACD -- 配達担当者コード
               , acpt_rec.DLVRYKANKATSUAPKIHONCD -- 配達管轄ＡＰ基本コード
               , acpt_rec.DLVRYKANKATSUSHOPCD -- 配達管轄ショップコード
               , acpt_rec.DLVRYKYOTENCD -- 配達拠点コード
               , acpt_rec.DLVRYKBN -- 配達区分
               , acpt_rec.DLVRYCMPNYCD -- 配達会社コード
               , NULL -- 出荷元区分
               , tokuisakiMST.EIGYOTNSHACD -- 営業担当者コード
               -- 実績担当者コード
               , ( CASE WHEN tokuisakiMST.URIKEIJOTNSHAKBN = '2' THEN tokuisakiMST.EIGYOTNSHACD
                    ELSE acpt_rec.DLVRYTNSHACD
               END )
               , tokuisakiMST.SKYSAKICD -- 請求先コード
               , NULL -- 請求締年月
               , NULL -- 請求締日
               -- 請求処理実施日
               , ( CASE WHEN iKaishuhohokbn = '7' THEN 'DAIBIKI'
                        ELSE '00000000'
               END )
               , NULL -- 月次締年月
               , NULL -- 月次締日
               , '00000000' -- 月次処理実施日
               , acpt_rec.DLVRYDD -- 売上締処理日
-- インボイス対応追加 start ABIb
               , NULL       -- 値引締年月
               , NULL       -- 値引締日
               , '00000000' -- 値引処理実施日
-- インボイス対応追加 end ABI
               , acpt_rec.PRNTMEMON1 -- 伝票備考
               , NULL -- 伝票番号
               , '0' -- 請求書出力有無区分
               , '0' -- 請求処理実施区分
               , DATSKSMOTOKBN_NEW -- データ作成元区分
               , acpt_rec.TAXKEISANKBN -- 税計算区分
               , acpt_rec.ACPTDNPYNO -- 仕入伝票番号
               , NULL -- 入金伝票番号
               , NULL -- 入金金額
               , NULL -- 入金種別
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
               , acpt_rec.ACPTDNPYNO -- 受注伝票番号
               , acpt_rec.EDANO      -- 受注伝票枝番号
        );

        -- ポイント制度対応追加 start ABI
        -- 登録した予定発生ポイントテーブルに売上情報を追記する。
        -- 予定発生ポイントテーブルに(S20T001T)に売上伝票番号、売上伝票枝番号を登録する
        UPDATE S20T001T
        SET
          URIDNPYNO = acpt_rec.ACPTDNPYNO
          , URIEDANO = acpt_rec.EDANO
        WHERE
          KANRINO = cmnRegistKanrino
          AND ACPTDNPYNO = acpt_rec.ACPTDNPYNO
          AND ACPTEDANO = acpt_rec.EDANO;
        -- ポイント制度対応追加 end ABI
      END IF;
    END LOOP;

    EXCEPTION
      WHEN OTHERS THEN
        IF SELECT_ACPTD%ISOPEN THEN
           CLOSE SELECT_ACPTD;
        END IF;
        IF SELECT_ACPTM%ISOPEN THEN
           CLOSE SELECT_ACPTM;
        END IF;
        RAISE_APPLICATION_ERROR(-20013, SQLERRM || '売上データ作成時エラーが発生しました。');
  END processUriage;

-- 本線対応追加 start ABI
  /*****************************************************************************/
  /* プロシージャ名：売上データ作成WEB処理                                     */
  /* 引数（ＩＮ）     ：work_rec        配達実績データヘッダ                   */
  /*                  ：tokuisakiMST    得意先マスタ                           */
  /*                  ：iKaishuhohokbn  回収方法区分                           */
  /*****************************************************************************/
  PROCEDURE processUriageWeb( work_rec        IN S05W001T%rowtype -- 配達実績データヘッダ
                            , tokuisakiMST    IN S02M013T%rowtype -- 得意先マスタ
                            , iKaishuhohokbn  IN VARCHAR2         -- 回収方法区分
  ) IS
  TYPE S06T002T_LIST  IS TABLE OF S06T002T%rowtype;
  /* 変数宣言 */
  allZero               BOOLEAN          := TRUE;            -- 明細納品数量オールゼロ
-- インボイス対応追加 start ABI
  uriSankoushohitaxkin  NUMBER(11,0)     := 0;               -- 売上参考消費税金額
-- インボイス対応追加 end ABI
  rUridnpyoStart        VARCHAR2(11)     := NULL;            -- 売上伝票番号（赤）（採番部品の戻り値：売上伝票番号開始値）
  rUridnpyoEnd          VARCHAR2(11)     := NULL;            -- 売上伝票番号（黒）（採番部品の戻り値：売上伝票番号終了値）
  uriTBL                S06T001T%rowtype := NULL;            -- 売上データ
  insAkadnUriT          S06T001T%rowtype := NULL;            -- 売上テーブル（赤伝登録用）
  insAkadnUriMeiT       S06T002T%rowtype := NULL;            -- 売上明細テーブル（赤伝登録用）
  insAkadnNyushukkoT    S11T003T%rowtype := NULL;            -- 入出庫実績テーブル（赤伝登録用）
  insAkadnNyushukkoMeiT S11T004T%rowtype := NULL;            -- 入出庫実績明細テーブル（赤伝登録用）
  arrayCnt              NUMBER           := 1;               -- 配列カウント
  insAkadnUriMeiTList   S06T002T_LIST    := S06T002T_LIST(); -- 売上明細テーブル（赤伝登録用）リスト

  /* CURSOR：受注配達テーブル */
  CURSOR SELECT_ACPTD IS
    SELECT JHTT.*
         , DKTM.KYOTENSOKOCD
         , DTNM.TNSHASHARYOSOKOCD -- 担当者車両倉庫コード
         , SHPM.ZAIKOKANRIKBN -- 在庫管理区分
      FROM S04T001T JHTT
      LEFT OUTER JOIN S02M031T DKTM -- 配達拠点マスタ
              ON ( DKTM.APKIHONCD  = JHTT.DLVRYKANKATSUAPKIHONCD
                   AND DKTM.SHOPCD = JHTT.DLVRYKANKATSUSHOPCD
                   AND DKTM.DLVRYKYOTENCD = JHTT.DLVRYKYOTENCD
                 )
      LEFT OUTER JOIN S02M003T DTNM -- 担当者マスタ（配達担当者）
              ON ( DTNM.APKIHONCD  = JHTT.APKIHONCD
                   AND DTNM.SHOPCD = JHTT.SHOPCD
                   AND DTNM.TNSHACD = JHTT.DLVRYTNSHACD
                 )
      LEFT OUTER JOIN S02M046T SHPM -- ショップマスタ
              ON ( SHPM.APKIHONCD  = JHTT.APKIHONCD
                   AND SHPM.SHOPCD = JHTT.SHOPCD
                 )
     WHERE JHTT.APKIHONCD  = work_rec.APKIHONCD
       AND JHTT.SHOPCD     = work_rec.SHOPCD
       AND JHTT.ACPTDNPYNO = work_rec.ACPTDNPYNO
       AND JHTT.EDANO      = work_rec.EDANO
       AND JHTT.YUKOKBN = '1'
  ;
  /* CURSOR：受注配達明細テーブル */
  CURSOR SELECT_ACPTM IS
    SELECT JHMT.*
      FROM S04T002T JHMT
     WHERE JHMT.APKIHONCD  = work_rec.APKIHONCD
       AND JHMT.SHOPCD     = work_rec.SHOPCD
       AND JHMT.ACPTDNPYNO = work_rec.ACPTDNPYNO
       AND JHMT.EDANO      = work_rec.EDANO
       AND JHMT.MEISAIKBN IN ('0', '3') -- 商品、消費税調整のみを対象とする
       AND JHMT.YUKOKBN = '1'
  ;
  -- ポイント制度対応追加 start ABI
  wKurouriknrino        VARCHAR2(15);                   -- 黒伝用登録予定発行ポイントレコードの管理番号
  -- ポイント制度対応追加 end ABI
  BEGIN
    FOR acpt_rec IN SELECT_ACPTD LOOP
      -- 配列カウントの初期化
      arrayCnt := 1;
      -- ポイント制度対応追加 start ABI
      wKurouriknrino := NULL;
      -- ポイント制度対応追加 start ABI
      -- 売上伝票番号を採番する
      /*
       * 配達売上済の受注に対して配達実績登録した場合の売上伝票作成パターンについて
       *   1）赤伝票と黒伝票を作成 … 配達予定日や納品数量や納品金額など売上金額に変更が生じる場合
       *   2）赤伝票のみ作成 … 納品数量が全て0に変更された → 配達実績を取り消すので売上も取り消す
       *                        ※このとき受注配達テーブル.売上伝票番号は空にして更新する
       *   3）黒伝票のみ作成 … 一度配達実績が取り消された受注に対して配達実績登録する場合 → 新しい内容で売上を作る
       */
      -- 配達日変更がTRUE（変更あり）、または明細数量金額変更がTRUE（変更あり）の場合、
      IF cmnCngdlvryddflg = TRUE OR cmnCngmeisuryoflg = TRUE THEN
        -- 受注配達テーブルデータ.売上伝票番号が設定されていて、かつ明細数量オールゼロフラグがFALSE（納品数量が0以外）の場合、
        IF acpt_rec.URIDNPYNO IS NOT NULL AND cmnAllzeromeisuryoflg = FALSE THEN
          -- 採番は共通部品より取得する
-- 本線対応変更 start ABI
--          S01SAIBAN.getSlipNoUriage(acpt_rec.APKIHONCD, acpt_rec.SHOPCD, 2, rUridnpyoStart, rUridnpyoEnd);
          Z1804B01.getSlipNoUriage(acpt_rec.APKIHONCD, acpt_rec.SHOPCD, 2, rUridnpyoStart, rUridnpyoEnd);
-- 本線対応変更 end ABI
          -- 取得結果を売上伝票番号（赤）、売上伝票番号（黒）に設定する ※1）のケース
          cmnAkauridnpyno := rUridnpyoStart;
          cmnKurouridnpyno := rUridnpyoEnd;
        -- 上記以外で受注配達テーブルデータ.売上伝票番号が設定されている場合、
        ELSIF acpt_rec.URIDNPYNO IS NOT NULL THEN
          -- 採番は共通部品より取得する
-- 本線対応変更 start ABI
--          S01SAIBAN.getSlipNoUriage(acpt_rec.APKIHONCD, acpt_rec.SHOPCD, 1, rUridnpyoStart, rUridnpyoEnd);
          Z1804B01.getSlipNoUriage(acpt_rec.APKIHONCD, acpt_rec.SHOPCD, 1, rUridnpyoStart, rUridnpyoEnd);
-- 本線対応変更 end ABI
          -- 取得結果を売上伝票番号（赤）に設定する ※2）のケース
          cmnAkauridnpyno := rUridnpyoStart;
        -- 上記以外で明細数量オールゼロフラグがFALSE（納品数量が0以外）の場合、
        ELSIF cmnAllzeromeisuryoflg = FALSE THEN
          -- 採番は共通部品より取得する
-- 本線対応変更 start ABI
--          S01SAIBAN.getSlipNoUriage(acpt_rec.APKIHONCD, acpt_rec.SHOPCD, 1, rUridnpyoStart, rUridnpyoEnd);
          Z1804B01.getSlipNoUriage(acpt_rec.APKIHONCD, acpt_rec.SHOPCD, 1, rUridnpyoStart, rUridnpyoEnd);
-- 本線対応変更 end ABI
          -- 取得結果を売上伝票番号（黒）に設定する ※3）のケース
          cmnKurouridnpyno := rUridnpyoStart;
        END IF;
      END IF;

      -- 赤伝票作成を行う
      IF cmnAkauridnpyno IS NOT NULL THEN
        -- 売上データを取得
        SELECT URIT.*
          INTO uriTBL
        FROM S06T001T URIT
        WHERE URIT.APKIHONCD = work_rec.APKIHONCD
          AND URIT.SHOPCD = work_rec.SHOPCD
          AND URIT.URIDNPYNO = acpt_rec.URIDNPYNO
        FOR UPDATE
        ;

        -- 売上テーブルの赤伝票作成を行う
        -- 売上テーブル（赤伝登録用）に取得した売上データをコピーした後、登録値を設定
        insAkadnUriT := uriTBL;
        insAkadnUriT.URIDNPYNO := cmnAkauridnpyno;                                     -- 売上伝票番号
        insAkadnUriT.DNPYNO    := uriTBL.URIDNPYNO;                                    -- 伝票番号
        insAkadnUriT.URIDD     := uriTBL.URIDD;                                        -- 売上日
        IF uriTBL.URIKIN IS NOT NULL OR uriTBL.URIKIN <> '' THEN                       -- 売上金額
          insAkadnUriT.URIKIN := uriTBL.URIKIN * -1;
        ELSE
          insAkadnUriT.URIKIN := '';
        END IF;
        IF uriTBL.SHOHITAXKIN IS NOT NULL OR uriTBL.SHOHITAXKIN <> '' THEN             -- 消費税金額
          insAkadnUriT.SHOHITAXKIN := uriTBL.SHOHITAXKIN * -1;
        ELSE
          insAkadnUriT.SHOHITAXKIN := '';
        END IF;
-- インボイス対応追加 start ABI
        IF uriTBL.SANKOUSHOHITAXKIN IS NOT NULL OR uriTBL.SANKOUSHOHITAXKIN <> '' THEN -- 参考消費税金額
          insAkadnUriT.SANKOUSHOHITAXKIN := uriTBL.SANKOUSHOHITAXKIN * -1;
        ELSE
          insAkadnUriT.SANKOUSHOHITAXKIN := '';
        END IF;
-- インボイス対応追加 end ABI
        insAkadnUriT.EDANO                 := '0';                                     -- 枝番号
        insAkadnUriT.SKYSHIMEDYDM          := '';                                      -- 請求締年月
        insAkadnUriT.SKYSHIMEDD            := '';                                      -- 請求締日
        insAkadnUriT.SKYPRCSJISSIDD        := '00000000';                              -- 請求処理実施日
        insAkadnUriT.GETSUJISHIMEDYDM      := '';                                      -- 月次締年月
        insAkadnUriT.GETSUJISHIMEDD        := '';                                      -- 月次締日
        insAkadnUriT.GETSUJIPRCSJISSIDD    := '00000000';                              -- 月次処理実施日
-- インボイス対応追加 start ABI
        insAkadnUriT.NEBIKISHIMEDYDM       := NULL;                                    -- 値引締年月
        insAkadnUriT.NEBIKISHIMEDD         := NULL;                                    -- 値引締日
        insAkadnUriT.NEBIKIPRCSJISSIDD     := '00000000';                              -- 値引処理実施日
-- インボイス対応追加 end ABI
        insAkadnUriT.URISHIMEPRCSDD        := uriTBL.URIDD;                            -- 売上締処理日
        insAkadnUriT.DATSKSMOTOKBN         := '16';                                    -- データ作成元区分
        insAkadnUriT.SIIREDNPYNO           := cmnAkauridnpyno;                         -- 仕入伝票番号
        insAkadnUriT.NYKNDNPYNO            := '';                                      -- 入金伝票番号
        insAkadnUriT.NYKNKIN               := '';                                      -- 入金金額
        insAkadnUriT.NYKNKIND              := '';                                      -- 入金種別
        insAkadnUriT.SKYSHOOTPTARINASHIKBN := '1';                                     -- 請求書出力有無区分
        IF uriTBL.TORIHIKIKBN IS NULL OR uriTBL.TORIHIKIKBN = '' THEN                  -- 取引区分
          insAkadnUriT.TORIHIKIKBN := '6';
        END IF;

        -- 入出庫実績テーブル（赤伝登録用）に初期値の値を設定
        insAkadnNyushukkoT.NYUSHUKKOKBN       := '2';        -- 入出庫区分
        insAkadnNyushukkoT.TORIHIKIKBN        := '1';        -- 取引区分
        insAkadnNyushukkoT.NYUKAKBN           := '';         -- 入荷区分
        insAkadnNyushukkoT.NYUKOSAKISIIRECD   := '';         -- 入庫先仕入コード
        insAkadnNyushukkoT.NYUKOSAKISOKOCD    := '';         -- 入庫先倉庫コード
        insAkadnNyushukkoT.NYUSHUKKODD        := '00000000'; -- 入出庫日
        insAkadnNyushukkoT.SHUKKOSAKISOKOCD   := '';         -- 出庫先倉庫コード
        insAkadnNyushukkoT.SHUKKOSAKIKEIHICD  := '';         -- 出庫先経費コード
        insAkadnNyushukkoT.JISSEKITNSHACD     := '';         -- 実績担当者コード
        insAkadnNyushukkoT.GETSUJISHIMEDYDM   := '';         -- 月次締年月
        insAkadnNyushukkoT.GETSUJISHIMEDD     := '';         -- 月次締日
        insAkadnNyushukkoT.GETSUJIPRCSJISSIDD := '';         -- 月次処理実施日
        insAkadnNyushukkoT.DNPYBIKO           := '';         -- 伝票備考
        insAkadnNyushukkoT.YUKOKBN            := '';         -- 有効区分
        -- 入出庫実績テーブル（赤伝登録用）に登録値を設定
        insAkadnNyushukkoT.APKIHONCD          := insAkadnUriT.APKIHONCD;          -- ＡＰ基本コード
        insAkadnNyushukkoT.SHOPCD             := insAkadnUriT.SHOPCD;             -- ショップコード
        insAkadnNyushukkoT.NYUSHUKKODNPYNO    := insAkadnUriT.URIDNPYNO;          -- 入出庫伝票番号
        insAkadnNyushukkoT.TORIHIKIKBN        := '6';                             -- 取引区分
        insAkadnNyushukkoT.NYUKOSAKISIIRECD   := insAkadnUriT.SIIRESAKICD;        -- 入庫先仕入コード
        insAkadnNyushukkoT.NYUSHUKKODD        := insAkadnUriT.URIDD;              -- 入出庫日
        insAkadnNyushukkoT.SHUKKOSAKIKEIHICD  := insAkadnUriT.TOKUISAKICD;        -- 出庫先経費コード
        insAkadnNyushukkoT.JISSEKITNSHACD     := insAkadnUriT.JISSEKITNSHACD;     -- 実績担当者コード
        insAkadnNyushukkoT.KIN                := '0';                             -- 金額
        insAkadnNyushukkoT.GETSUJISHIMEDYDM   := insAkadnUriT.GETSUJISHIMEDYDM;   -- 月次締年月
        insAkadnNyushukkoT.GETSUJISHIMEDD     := insAkadnUriT.GETSUJISHIMEDD;     -- 月次締日
        insAkadnNyushukkoT.GETSUJIPRCSJISSIDD := insAkadnUriT.GETSUJIPRCSJISSIDD; -- 月次処理実施日
        insAkadnNyushukkoT.DNPYBIKO           := insAkadnUriT.DNPYBIKO;           -- 伝票備考
        insAkadnNyushukkoT.DATSKSMOTOKBN      := insAkadnUriT.DATSKSMOTOKBN;      -- データ作成元区分
        insAkadnNyushukkoT.YUKOKBN            := '1';                             -- 有効区分
        insAkadnNyushukkoT.TOSHAAPKIHONCD := userinfo_rec.APKIHONCD;  -- 登録者ＡＰ基本コード
        insAkadnNyushukkoT.TOSHASHOPCD    := userinfo_rec.SHOPCD;     -- 登録者ショップコード
        insAkadnNyushukkoT.TOSHACD        := userinfo_rec.TNSHACD;    -- 登録者コード
        insAkadnNyushukkoT.INPTSYSTMYMD   := userinfo_rec.SYSTMYMD;   -- 登録システム日付
        insAkadnNyushukkoT.INPTSYSTMJIKAN := userinfo_rec.SYSTMJIKAN; -- 登録システム時間
        insAkadnNyushukkoT.INPTUNYOYMD    := userinfo_rec.UNYOYMD;    -- 登録運用日付
        insAkadnNyushukkoT.INPTIPADRSE    := userinfo_rec.IPADRSE;    -- 登録ＩＰアドレス
        insAkadnNyushukkoT.INPTPGMID      := userinfo_rec.PGMID;      -- 登録プログラムＩＤ
        insAkadnNyushukkoT.KOSHAAPKIHONCD := userinfo_rec.APKIHONCD;  -- 更新者ＡＰ基本コード
        insAkadnNyushukkoT.KOSHASHOPCD    := userinfo_rec.SHOPCD;     -- 更新者ショップコード
        insAkadnNyushukkoT.KOSHACD        := userinfo_rec.TNSHACD;    -- 更新者コード
        insAkadnNyushukkoT.KSHNSYSTMYMD   := userinfo_rec.SYSTMYMD;   -- 更新システム日付
        insAkadnNyushukkoT.KSHNSYSTMJIKAN := userinfo_rec.SYSTMJIKAN; -- 更新システム時間
        insAkadnNyushukkoT.KSHNUNYOYMD    := userinfo_rec.UNYOYMD;    -- 更新運用日付
        insAkadnNyushukkoT.KSHNIPADRSE    := userinfo_rec.IPADRSE;    -- 更新ＩＰアドレス
        insAkadnNyushukkoT.KSHNPGMID      := userinfo_rec.PGMID;      -- 更新プログラムＩＤ

        -- ＡＰ基本コードと配達管轄ＡＰ基本コード、ショップコードと配達管轄ショップコードが同一の場合、入出庫実績テーブルに登録する
        IF acpt_rec.APKIHONCD = acpt_rec.DLVRYKANKATSUAPKIHONCD AND acpt_rec.SHOPCD = acpt_rec.DLVRYKANKATSUSHOPCD THEN
          INSERT INTO S11T003T VALUES insAkadnNyushukkoT;
        END IF;

        -- 売上明細テーブルの赤伝票作成を行う
        FOR urimei_rec IN (SELECT UHMT.*
                          FROM S06T002T UHMT
                          WHERE UHMT.APKIHONCD = acpt_rec.APKIHONCD
                            AND UHMT.SHOPCD = acpt_rec.SHOPCD
                            AND UHMT.URIDNPYNO = acpt_rec.URIDNPYNO
                          ORDER BY UHMT.URIDNPYMEISAINO, UHMT.URIDNPYMEISAIEDANO
                          FOR UPDATE ) LOOP
          -- 売上明細テーブル（赤伝登録用）に取得した売上明細データをコピーした後、登録値を設定
          insAkadnUriMeiT := urimei_rec;
          insAkadnUriMeiT.URIDNPYNO         := insAkadnUriT.URIDNPYNO;                           -- 売上伝票番号
          insAkadnUriMeiT.EDANO             := insAkadnUriT.EDANO;                               -- 枝番号
          IF urimei_rec.SURYO IS NOT NULL OR urimei_rec.SURYO <> '' THEN                         -- 数量
            insAkadnUriMeiT.SURYO := urimei_rec.SURYO * -1;
          ELSE
            insAkadnUriMeiT.SURYO := '';
          END IF;
          IF urimei_rec.KIN IS NOT NULL OR urimei_rec.KIN <> '' THEN                             -- 金額
            insAkadnUriMeiT.KIN := urimei_rec.KIN * -1;
          ELSE
            insAkadnUriMeiT.KIN := '';
          END IF;
          IF urimei_rec.SHOHITAXKIN IS NOT NULL OR urimei_rec.SHOHITAXKIN <> '' THEN             -- 消費税金額
            insAkadnUriMeiT.SHOHITAXKIN := urimei_rec.SHOHITAXKIN * -1;
          ELSE
            insAkadnUriMeiT.SHOHITAXKIN := '';
          END IF;
-- インボイス対応追加 start ABI
          IF urimei_rec.SANKOUSHOHITAXKIN IS NOT NULL OR urimei_rec.SANKOUSHOHITAXKIN <> '' THEN -- 参考消費税金額
            insAkadnUriMeiT.SANKOUSHOHITAXKIN := urimei_rec.SANKOUSHOHITAXKIN * -1;
          ELSE
            insAkadnUriMeiT.SANKOUSHOHITAXKIN := '';
          END IF;
-- インボイス対応追加 end ABI
          IF urimei_rec.SIIRESURYO IS NOT NULL OR urimei_rec.SIIRESURYO <> '' THEN               -- 仕入数量
            insAkadnUriMeiT.SIIRESURYO := urimei_rec.SIIRESURYO * -1;
          ELSE
            insAkadnUriMeiT.SIIRESURYO := '';
          END IF;
          IF urimei_rec.SIIREKIN IS NOT NULL OR urimei_rec.SIIREKIN <> '' THEN                   -- 仕入金額
            insAkadnUriMeiT.SIIREKIN := urimei_rec.SIIREKIN * -1;
          ELSE
            insAkadnUriMeiT.SIIREKIN := '';
          END IF;
          IF urimei_rec.SIIRESHOHITAXKIN IS NOT NULL OR urimei_rec.SIIRESHOHITAXKIN <> '' THEN   -- 仕入消費税金額
            insAkadnUriMeiT.SIIRESHOHITAXKIN := urimei_rec.SIIRESHOHITAXKIN * -1;
          ELSE
            insAkadnUriMeiT.SIIRESHOHITAXKIN := '';
          END IF;
-- ACCIS2023-6680 要望対応 start ABI
          insAkadnUriMeiT.ROYALTYSHIMEYMD    := '00000000';                                      -- ロイヤリティ締め年月日
-- ACCIS2023-6680 要望対応 end ABI

          -- 入出庫実績明細テーブル（赤伝登録用）に初期値の値を設定
          insAkadnNyushukkoMeiT.NYUSHUKKOKBN := '2'; -- 入出庫区分
          insAkadnNyushukkoMeiT.LCTGRYCD     := '';  -- 大分類コード
          insAkadnNyushukkoMeiT.MCTGRYCD     := '';  -- 中分類コード
          insAkadnNyushukkoMeiT.SCTGRYCD     := '';  -- 小分類コード
          insAkadnNyushukkoMeiT.SHNCD        := '';  -- 商品コード
          insAkadnNyushukkoMeiT.SURYO        := '0'; -- 数量
          insAkadnNyushukkoMeiT.UNT          := '0'; -- 単価
          insAkadnNyushukkoMeiT.KIN          := '0'; -- 金額
          insAkadnNyushukkoMeiT.MEISAIBIKO   := '';  -- 明細備考
          insAkadnNyushukkoMeiT.KATASIKI     := '';  -- 型式
          insAkadnNyushukkoMeiT.YUKOKBN      := '1'; -- 有効区分
          -- 入出庫実績明細テーブル（赤伝登録用）に登録値を設定
          insAkadnNyushukkoMeiT.APKIHONCD                := insAkadnUriT.APKIHONCD;          -- ＡＰ基本コード
          insAkadnNyushukkoMeiT.SHOPCD                   := insAkadnUriT.SHOPCD;             -- ショップコード
          insAkadnNyushukkoMeiT.NYUSHUKKODNPYNO          := cmnAkauridnpyno;                 -- 入出庫伝票番号
          insAkadnNyushukkoMeiT.NYUSHUKKODNPYMEISAINO    := insAkadnUriMeiT.URIDNPYMEISAINO; -- 入出庫伝票明細番号
          IF insAkadnUriMeiT.TANPINMEISAIFLG = '0' THEN -- 入出庫伝票明細枝番号
            insAkadnNyushukkoMeiT.NYUSHUKKODNPYMEISAIEDANO := '0';
          ELSE
            insAkadnNyushukkoMeiT.NYUSHUKKODNPYMEISAIEDANO := insAkadnUriMeiT.URIDNPYMEISAIEDANO;
          END IF;
          insAkadnNyushukkoMeiT.LCTGRYCD                 := insAkadnUriMeiT.LCTGRYCD; -- 大分類コード
          insAkadnNyushukkoMeiT.MCTGRYCD                 := insAkadnUriMeiT.MCTGRYCD; -- 中分類コード
          insAkadnNyushukkoMeiT.SCTGRYCD                 := insAkadnUriMeiT.SCTGRYCD; -- 小分類コード
          insAkadnNyushukkoMeiT.SHNCD                    := insAkadnUriMeiT.SHNCD;    -- 商品コード
          IF insAkadnUriMeiT.SURYO IS NOT NULL OR insAkadnUriMeiT.SURYO <> '' THEN -- 数量
            insAkadnNyushukkoMeiT.SURYO := insAkadnUriMeiT.SURYO;
          ELSE
            insAkadnNyushukkoMeiT.SURYO := '0';
          END IF;
          IF insAkadnUriMeiT.UNT IS NOT NULL OR insAkadnUriMeiT.UNT <> '' THEN     -- 単価
            insAkadnNyushukkoMeiT.UNT := insAkadnUriMeiT.UNT;
          ELSE
            insAkadnNyushukkoMeiT.UNT := '0';
          END IF;
          IF insAkadnUriMeiT.KIN IS NOT NULL OR insAkadnUriMeiT.KIN <> '' THEN     -- 金額
            insAkadnNyushukkoMeiT.KIN := insAkadnUriMeiT.KIN;
          ELSE
            insAkadnNyushukkoMeiT.KIN := '0';
          END IF;
          insAkadnNyushukkoMeiT.MEISAIBIKO               := insAkadnUriMeiT.MEISAIBIKO; -- 明細備考
          insAkadnNyushukkoMeiT.KATASIKI                 := insAkadnUriMeiT.KATASIKI; -- 型式
          insAkadnNyushukkoMeiT.ZAIKOKSHNSUMIFLG         := insAkadnUriMeiT.ZAIKOKSHNSUMIFLG; -- 在庫更新済フラグ
          insAkadnNyushukkoMeiT.TOSHAAPKIHONCD           := userinfo_rec.APKIHONCD;  -- 登録者ＡＰ基本コード
          insAkadnNyushukkoMeiT.TOSHASHOPCD              := userinfo_rec.SHOPCD;     -- 登録者ショップコード
          insAkadnNyushukkoMeiT.TOSHACD                  := userinfo_rec.TNSHACD;    -- 登録者コード
          insAkadnNyushukkoMeiT.INPTSYSTMYMD             := userinfo_rec.SYSTMYMD;   -- 登録システム日付
          insAkadnNyushukkoMeiT.INPTSYSTMJIKAN           := userinfo_rec.SYSTMJIKAN; -- 登録システム時間
          insAkadnNyushukkoMeiT.INPTUNYOYMD              := userinfo_rec.UNYOYMD;    -- 登録運用日付
          insAkadnNyushukkoMeiT.INPTIPADRSE              := userinfo_rec.IPADRSE;    -- 登録ＩＰアドレス
          insAkadnNyushukkoMeiT.INPTPGMID                := userinfo_rec.PGMID;      -- 登録プログラムＩＤ
          insAkadnNyushukkoMeiT.KOSHAAPKIHONCD           := userinfo_rec.APKIHONCD;  -- 更新者ＡＰ基本コード
          insAkadnNyushukkoMeiT.KOSHASHOPCD              := userinfo_rec.SHOPCD;     -- 更新者ショップコード
          insAkadnNyushukkoMeiT.KOSHACD                  := userinfo_rec.TNSHACD;    -- 更新者コード
          insAkadnNyushukkoMeiT.KSHNSYSTMYMD             := userinfo_rec.SYSTMYMD;   -- 更新システム日付
          insAkadnNyushukkoMeiT.KSHNSYSTMJIKAN           := userinfo_rec.SYSTMJIKAN; -- 更新システム時間
          insAkadnNyushukkoMeiT.KSHNUNYOYMD              := userinfo_rec.UNYOYMD;    -- 更新運用日付
          insAkadnNyushukkoMeiT.KSHNIPADRSE              := userinfo_rec.IPADRSE;    -- 更新ＩＰアドレス
          insAkadnNyushukkoMeiT.KSHNPGMID                := userinfo_rec.PGMID;      -- 更新プログラムＩＤ

          -- ＡＰ基本コードと配達管轄ＡＰ基本コード、ショップコードと配達管轄ショップコードが同一の場合、入出庫実績テーブルに登録する
          IF acpt_rec.APKIHONCD = acpt_rec.DLVRYKANKATSUAPKIHONCD AND acpt_rec.SHOPCD = acpt_rec.DLVRYKANKATSUSHOPCD THEN
            INSERT INTO S11T004T VALUES insAkadnNyushukkoMeiT;
          END IF;

          -- 売上明細テーブル（赤伝登録用）を配列に格納
          insAkadnUriMeiT.TOSHAAPKIHONCD := userinfo_rec.APKIHONCD;  -- 登録者ＡＰ基本コード
          insAkadnUriMeiT.TOSHASHOPCD    := userinfo_rec.SHOPCD;     -- 登録者ショップコード
          insAkadnUriMeiT.TOSHACD        := userinfo_rec.TNSHACD;    -- 登録者コード
          insAkadnUriMeiT.INPTSYSTMYMD   := userinfo_rec.SYSTMYMD;   -- 登録システム日付
          insAkadnUriMeiT.INPTSYSTMJIKAN := userinfo_rec.SYSTMJIKAN; -- 登録システム時間
          insAkadnUriMeiT.INPTUNYOYMD    := userinfo_rec.UNYOYMD;    -- 登録運用日付
          insAkadnUriMeiT.INPTIPADRSE    := userinfo_rec.IPADRSE;    -- 登録ＩＰアドレス
          insAkadnUriMeiT.INPTPGMID      := userinfo_rec.PGMID;      -- 登録プログラムＩＤ
          insAkadnUriMeiT.KOSHAAPKIHONCD := userinfo_rec.APKIHONCD;  -- 更新者ＡＰ基本コード
          insAkadnUriMeiT.KOSHASHOPCD    := userinfo_rec.SHOPCD;     -- 更新者ショップコード
          insAkadnUriMeiT.KOSHACD        := userinfo_rec.TNSHACD;    -- 更新者コード
          insAkadnUriMeiT.KSHNSYSTMYMD   := userinfo_rec.SYSTMYMD;   -- 更新システム日付
          insAkadnUriMeiT.KSHNSYSTMJIKAN := userinfo_rec.SYSTMJIKAN; -- 更新システム時間
          insAkadnUriMeiT.KSHNUNYOYMD    := userinfo_rec.UNYOYMD;    -- 更新運用日付
          insAkadnUriMeiT.KSHNIPADRSE    := userinfo_rec.IPADRSE;    -- 更新ＩＰアドレス
          insAkadnUriMeiT.KSHNPGMID      := userinfo_rec.PGMID;      -- 更新プログラムＩＤ
          insAkadnUriMeiTList.EXTEND;
          insAkadnUriMeiTList(arrayCnt) := insAkadnUriMeiT;
          arrayCnt := arrayCnt + 1;
        END LOOP;

        -- 売上テーブル（赤伝登録用）を売上テーブルに登録
        insAkadnUriT.TOSHAAPKIHONCD := userinfo_rec.APKIHONCD;  -- 登録者ＡＰ基本コード
        insAkadnUriT.TOSHASHOPCD    := userinfo_rec.SHOPCD;     -- 登録者ショップコード
        insAkadnUriT.TOSHACD        := userinfo_rec.TNSHACD;    -- 登録者コード
        insAkadnUriT.INPTSYSTMYMD   := userinfo_rec.SYSTMYMD;   -- 登録システム日付
        insAkadnUriT.INPTSYSTMJIKAN := userinfo_rec.SYSTMJIKAN; -- 登録システム時間
        insAkadnUriT.INPTUNYOYMD    := userinfo_rec.UNYOYMD;    -- 登録運用日付
        insAkadnUriT.INPTIPADRSE    := userinfo_rec.IPADRSE;    -- 登録ＩＰアドレス
        insAkadnUriT.INPTPGMID      := userinfo_rec.PGMID;      -- 登録プログラムＩＤ
        insAkadnUriT.KOSHAAPKIHONCD := userinfo_rec.APKIHONCD;  -- 更新者ＡＰ基本コード
        insAkadnUriT.KOSHASHOPCD    := userinfo_rec.SHOPCD;     -- 更新者ショップコード
        insAkadnUriT.KOSHACD        := userinfo_rec.TNSHACD;    -- 更新者コード
        insAkadnUriT.KSHNSYSTMYMD   := userinfo_rec.SYSTMYMD;   -- 更新システム日付
        insAkadnUriT.KSHNSYSTMJIKAN := userinfo_rec.SYSTMJIKAN; -- 更新システム時間
        insAkadnUriT.KSHNUNYOYMD    := userinfo_rec.UNYOYMD;    -- 更新運用日付
        insAkadnUriT.KSHNIPADRSE    := userinfo_rec.IPADRSE;    -- 更新ＩＰアドレス
        insAkadnUriT.KSHNPGMID      := userinfo_rec.PGMID;      -- 更新プログラムＩＤ
        INSERT INTO S06T001T VALUES insAkadnUriT;

        -- 売上明細テーブル（赤伝登録用）を売上明細テーブルに登録
        FOR i IN insAkadnUriMeiTList.first..insAkadnUriMeiTList.last LOOP
          INSERT INTO S06T002T VALUES insAkadnUriMeiTList(i);
        END LOOP;

        -- 取得した売上データを更新する
        UPDATE S06T001T URIT
          SET URIT.DNPYNO                = cmnAkauridnpyno         -- 伝票番号
            , URIT.SKYSHOOTPTARINASHIKBN = '1'                     -- 請求書出力有無区分
            , URIT.KOSHAAPKIHONCD        = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
            , URIT.KOSHASHOPCD           = userinfo_rec.SHOPCD     -- 更新者ショップコード
            , URIT.KOSHACD               = userinfo_rec.TNSHACD    -- 更新者コード
            , URIT.KSHNSYSTMYMD          = userinfo_rec.SYSTMYMD   -- 更新システム日付
            , URIT.KSHNSYSTMJIKAN        = userinfo_rec.SYSTMJIKAN -- 更新システム時間
            , URIT.KSHNUNYOYMD           = userinfo_rec.UNYOYMD    -- 更新運用日付
            , URIT.KSHNIPADRSE           = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
            , URIT.KSHNPGMID             = userinfo_rec.PGMID      -- 更新プログラムＩＤ
        WHERE URIT.APKIHONCD = uriTBL.APKIHONCD
          AND URIT.SHOPCD    = uriTBL.SHOPCD
          AND URIT.URIDNPYNO = uriTBL.URIDNPYNO
        ;
      END IF;

      -- 黒伝票作成を行う
      IF cmnKurouridnpyno IS NOT NULL THEN
        allZero := TRUE;
-- インボイス対応追加 start ABI
        uriSankoushohitaxkin := 0;
-- インボイス対応追加 end ABI
        -- 明細テーブル
        FOR meisai_rec IN SELECT_ACPTM LOOP
          IF meisai_rec.NOHINSURYO <> 0 THEN
             allZero := FALSE;
             -- 入出庫実績テーブル
             INSERT INTO S11T004T (
                 APKIHONCD -- ＡＰ基本コード
               , SHOPCD -- ショップコード
               , NYUSHUKKODNPYNO -- 入出庫伝票番号
               , NYUSHUKKODNPYMEISAINO -- 入出庫伝票明細番号
               , NYUSHUKKODNPYMEISAIEDANO -- 入出庫伝票明細枝番号
               , NYUSHUKKOKBN -- 入出庫区分
               , LCTGRYCD -- 大分類コード
               , MCTGRYCD -- 中分類コード
               , SCTGRYCD -- 小分類コード
               , SHNCD -- 商品コード
               , SURYO -- 数量
               , UNT -- 単価
               , KIN -- 金額
               , MEISAIBIKO -- 明細備考
               , KATASIKI -- 型式
               , ZAIKOKSHNSUMIFLG -- 在庫更新済フラグ
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
                   meisai_rec.APKIHONCD -- ＡＰ基本コード
                 , meisai_rec.SHOPCD -- ショップコード
                 , cmnKurouridnpyno -- 入出庫伝票番号
                 , meisai_rec.ACPTDNPYMEISAINO -- 入出庫伝票明細番号
                 , meisai_rec.ACPTDNPYMEISAIEDANO -- 入出庫伝票明細枝番号
                 , '2' -- 入出庫区分
                 , meisai_rec.LCTGRYCD -- 大分類コード
                 , meisai_rec.MCTGRYCD -- 中分類コード
                 , meisai_rec.SCTGRYCD -- 小分類コード
                 , meisai_rec.SHNCD -- 商品コード
                 , meisai_rec.NOHINSURYO -- 数量
                 , meisai_rec.NOHINUNT -- 単価
                 , meisai_rec.NOHINKIN -- 金額
                 , meisai_rec.MEISAIBIKO -- 明細備考
                 , meisai_rec.KATASIKI -- 型式
                 , '1' -- 在庫更新済フラグ
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

             -- 売上明細テーブル
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
               , JUTAKUDLVRYCSTKBN -- 顧客区分
-- インボイス対応追加 end ABI
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
                   meisai_rec.APKIHONCD -- ＡＰ基本コード
                 , meisai_rec.SHOPCD -- ショップコード
                 , cmnKurouridnpyno -- 売上伝票番号
                 , 0 -- 枝番号
                 , meisai_rec.ACPTDNPYMEISAINO -- 売上伝票明細番号
                 , meisai_rec.ACPTDNPYMEISAIEDANO -- 売上伝票明細枝番号
                 , meisai_rec.LCTGRYCD -- 大分類コード
                 , meisai_rec.MCTGRYCD -- 中分類コード
                 , meisai_rec.SCTGRYCD -- 小分類コード
                 , meisai_rec.SHNCD -- 商品コード
                 , meisai_rec.KEIJOKBN -- 計上区分
                 , meisai_rec.NOHINSURYO -- 数量
                 , meisai_rec.HYOJUNUNT -- 標準単価
                 , meisai_rec.NOHINUNT -- 単価
                 , meisai_rec.NOHINKIN -- 金額
                 , meisai_rec.NOHINUNTSHOHITAXKIN -- 単価消費税金額
-- インボイス対応変更 start ABI
                 --, meisai_rec.NOHINSHOHITAXKIN -- 消費税金額
                 , CASE WHEN ( acpt_rec.ACPTTHDAIBIKIFLG = '1' OR acpt_rec.SINGLDNPYFLG = '1') THEN -- 受注時代引フラグが"1"、または単体伝票締めフラグが"1"
                     0
                   ELSE
                     CASE WHEN cmnInvoiceshikoymdflg = '0' THEN -- インボイス施行日前の場合
                       meisai_rec.NOHINSHOHITAXKIN
                     ELSE
                       0
                     END
                   END                           -- 消費税金額
-- インボイス対応変更 end ABI
-- インボイス対応追加 start ABI
                 , CASE WHEN ( acpt_rec.ACPTTHDAIBIKIFLG = '1' OR acpt_rec.SINGLDNPYFLG = '1') THEN -- 受注時代引フラグが"1"、または単体伝票締めフラグが"1"
                     meisai_rec.NOHINSHOHITAXKIN
                   ELSE
                     CASE WHEN cmnInvoiceshikoymdflg = '0' THEN -- インボイス施行日前の場合
                       0
                     ELSE
                       meisai_rec.NOHINSHOHITAXKIN
                     END
                   END                           -- 参考消費税金額
-- インボイス対応追加 end ABI
                 , meisai_rec.TAXKBN -- 税区分
                 , meisai_rec.MEISAIKBN -- 明細区分
                 , meisai_rec.NYURYOKUMEISAIFLG -- 入力明細フラグ
                 , meisai_rec.TANPINMEISAIFLG -- 単品明細フラグ
                 , meisai_rec.MEISAIBIKO -- 明細備考
                 , meisai_rec.STSHNCD -- セット商品コード
                 , meisai_rec.KATASIKI -- 型式
                 , NULL -- 仕入数量
                 , NULL -- 仕入金額
                 , NULL -- 仕入単価
                 , NULL -- 仕入消費税金額
                 , '1' -- 在庫更新済フラグ
-- インボイス対応追加 start ABI
                 , tokuisakiMST.TOKUISAKIKBN -- 得意先区分
                 , CASE WHEN tokuisakiMST.TOKUISAKIKBN ＝ '1' AND tokuisakiMST.JUTAKUDLVRYCSTKBN = '4' AND acpt_rec.ITAKUAITEACPTDNPYNO IS NULL THEN
                     '1'
                   ELSE
                     tokuisakiMST.JUTAKUDLVRYCSTKBN
                   END -- 顧客区分
-- インボイス対応追加 end ABI
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

-- インボイス対応追加 start ABI
             -- 構成商品以外の場合、売上参考消費税金額に受注配達明細データの納品消費税金額を加算
             IF NOT(meisai_rec.NYURYOKUMEISAIFLG = '0' and meisai_rec.TANPINMEISAIFLG = '1') THEN
               uriSankoushohitaxkin := uriSankoushohitaxkin + meisai_rec.NOHINSHOHITAXKIN;
             END IF;
-- インボイス対応追加 end ABI
          END IF;
        END LOOP;

        IF NOT allZero THEN
          -- 入出庫実績テーブル
          INSERT INTO S11T003T (
                 APKIHONCD -- ＡＰ基本コード
               , SHOPCD    -- ショップコード
               , NYUSHUKKODNPYNO -- 入出庫伝票番号
               , NYUSHUKKOKBN -- 入出庫区分
               , TORIHIKIKBN -- 取引区分
               , NYUKAKBN -- 入荷区分
               , NYUKOSAKISIIRECD -- 入庫先仕入コード
               , NYUKOSAKISOKOCD -- 入庫先倉庫コード
               , NYUSHUKKODD -- 入出庫日
               , SHUKKOSAKISOKOCD -- 出庫先倉庫コード
               , SHUKKOSAKIKEIHICD -- 出庫先経費コード
               , JISSEKITNSHACD -- 実績担当者コード
               , KIN -- 金額
               , GETSUJISHIMEDYDM    -- 月次締年月
               , GETSUJISHIMEDD    -- 月次締日
               , GETSUJIPRCSJISSIDD -- 月次処理実施日
               , DNPYBIKO -- 伝票備考
               , DATSKSMOTOKBN -- データ作成元区分
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
                   acpt_rec.APKIHONCD -- ＡＰ基本コード
                 , acpt_rec.SHOPCD    -- ショップコード
                 , cmnKurouridnpyno -- 入出庫伝票番号
                 , '2' -- 入出庫区分：出庫(2)
                 , '6' -- 取引区分：出庫(2)
                 , NULL -- 入荷区分
                 , NULL -- 入庫先仕入コード
                 -- 入庫先倉庫コード
                 , ( CASE WHEN acpt_rec.ZAIKOKANRIKBN = '2' -- 在庫管理区分：担当者倉庫あり
                          THEN NVL(acpt_rec.TNSHASHARYOSOKOCD, acpt_rec.KYOTENSOKOCD) -- 担当者車両倉庫コード
                          ELSE acpt_rec.KYOTENSOKOCD -- 拠点倉庫コード
                     END )
                 , acpt_rec.DLVRYDD -- 入出庫日
                 -- 出庫先倉庫コード
                 , ( CASE WHEN acpt_rec.ZAIKOKANRIKBN = '2' -- 在庫管理区分：担当者倉庫あり
                          THEN NVL(acpt_rec.TNSHASHARYOSOKOCD, acpt_rec.KYOTENSOKOCD) -- 担当者車両倉庫コード
                          ELSE acpt_rec.KYOTENSOKOCD -- 拠点倉庫コード
                     END )
                 -- 出庫先経費コード
                 , ( CASE WHEN tokuisakiMST.TOKUISAKIKBN = '9' THEN acpt_rec.TOKUISAKICD
                      ELSE NULL
                 END )
                 -- 実績担当者コード
                 , ( CASE WHEN tokuisakiMST.URIKEIJOTNSHAKBN = '2' THEN tokuisakiMST.EIGYOTNSHACD
                      ELSE acpt_rec.DLVRYTNSHACD
                 END )
                 , acpt_rec.NOHINKIN -- 金額
                 , NULL -- 月次締年月
                 , NULL -- 月次締日
                 -- 月次処理実施日
                 , ( CASE WHEN iKaishuhohokbn = '7' THEN 'DAIBIKI'
                          ELSE '00000000'
                     END )
                 , acpt_rec.PRNTMEMON1 -- 伝票備考
                 , DATSKSMOTOKBN_NEW -- データ作成元区分
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

          -- 売上テーブル
          INSERT INTO S06T001T URIT (
                 APKIHONCD -- ＡＰ基本コード
               , SHOPCD -- ショップコード
               , URIDNPYNO -- 売上伝票番号
               , EDANO -- 枝番号
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
               -- ACCIS2023-6685対応 start ABI
               , NEBIKISHIMEDYDM   -- 値引締年月
               , NEBIKISHIMEDD     -- 値引締日
               , NEBIKIPRCSJISSIDD -- 値引処理実施日
               -- ACCIS2023-6685対応 end ABI
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
               , ACPTDNPYNO -- 受注伝票番号
               , ACPTDNPYEDANO -- 受注伝票枝番号
          ) VALUES (
                   acpt_rec.APKIHONCD -- ＡＰ基本コード
                 , acpt_rec.SHOPCD -- ショップコード
                 , cmnKurouridnpyno -- 売上伝票番号
                 , 0 -- 枝番号
                 , acpt_rec.TORIHIKIKBN -- 取引区分
                 , acpt_rec.TOKUISAKICD -- 得意先コード
                 , NULL -- 仕入先コード
                 -- 入荷元コード
                 , ( CASE WHEN acpt_rec.ZAIKOKANRIKBN = '2' -- 在庫管理区分：担当者倉庫あり
                          THEN NVL(acpt_rec.TNSHASHARYOSOKOCD, acpt_rec.KYOTENSOKOCD) -- 担当者車両倉庫コード
                          ELSE acpt_rec.KYOTENSOKOCD -- 拠点倉庫コード
                     END )
                 , acpt_rec.DLVRYDD -- 売上日
                 , acpt_rec.ACPTDD -- 受注日
                 , acpt_rec.NOHINKIN -- 売上金額
-- インボイス対応変更 start ABI
                 --, acpt_rec.NOHINSHOHITAXKIN -- 消費税金額
                 , CASE WHEN ( acpt_rec.ACPTTHDAIBIKIFLG = '1' OR acpt_rec.SINGLDNPYFLG = '1') THEN -- 代引きの場合、0固定
                     0
                   ELSE -- 代引き以外の場合、インボイス施行日によって設定値を変更
                     CASE WHEN cmnInvoiceshikoymdflg = '0' THEN
                       acpt_rec.NOHINSHOHITAXKIN
                     ELSE
                       0
                     END
                   END                         -- 消費税金額
-- インボイス対応変更 end ABI
-- インボイス対応追加 start ABI
                 , CASE WHEN ( acpt_rec.ACPTTHDAIBIKIFLG = '1' OR acpt_rec.SINGLDNPYFLG = '1') THEN -- 代引きの場合、参考消費税金額を設定
                     uriSankoushohitaxkin
                   ELSE -- 代引き以外の場合、インボイス施行日によって設定値を変更
                     CASE WHEN cmnInvoiceshikoymdflg = '0' THEN
                       0
                     ELSE
                       uriSankoushohitaxkin
                     END
                   END                         -- 参考消費税金額
-- インボイス対応追加 end ABI
                 , acpt_rec.ACPTTNSHACD -- 受注担当者コード
                 , acpt_rec.DLVRYTNSHACD -- 配達担当者コード
                 , acpt_rec.DLVRYKANKATSUAPKIHONCD -- 配達管轄ＡＰ基本コード
                 , acpt_rec.DLVRYKANKATSUSHOPCD -- 配達管轄ショップコード
                 , acpt_rec.DLVRYKYOTENCD -- 配達拠点コード
                 , acpt_rec.DLVRYKBN -- 配達区分
                 , acpt_rec.DLVRYCMPNYCD -- 配達会社コード
                 , NULL -- 出荷元区分
                 , tokuisakiMST.EIGYOTNSHACD -- 営業担当者コード
                 -- 実績担当者コード
                 , ( CASE WHEN tokuisakiMST.URIKEIJOTNSHAKBN = '2' THEN tokuisakiMST.EIGYOTNSHACD
                      ELSE acpt_rec.DLVRYTNSHACD
                 END )
                 , tokuisakiMST.SKYSAKICD -- 請求先コード
                 , NULL -- 請求締年月
                 , NULL -- 請求締日
                 -- 請求処理実施日
                 , ( CASE WHEN iKaishuhohokbn = '7' THEN 'DAIBIKI'
                          ELSE '00000000'
                 END )
                 , NULL -- 月次締年月
                 , NULL -- 月次締日
                 , '00000000' -- 月次処理実施日
                 , acpt_rec.DLVRYDD -- 売上締処理日
                 -- ACCIS2023-6685対応 start ABI
                 , NULL       -- 値引締年月
                 , NULL       -- 値引締日
                 , '00000000' -- 値引処理実施日
                 -- ACCIS2023-6685対応 start ABI
                 , acpt_rec.PRNTMEMON1 -- 伝票備考
                 , NULL -- 伝票番号
                 , '0' -- 請求書出力有無区分
                 , '0' -- 請求処理実施区分
                 , DATSKSMOTOKBN_NEW -- データ作成元区分
                 , acpt_rec.TAXKEISANKBN -- 税計算区分
                 , acpt_rec.ACPTDNPYNO -- 仕入伝票番号
                 , NULL -- 入金伝票番号
                 , NULL -- 入金金額
                 , NULL -- 入金種別
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
                 , acpt_rec.ACPTDNPYNO -- 受注伝票番号
                 , acpt_rec.EDANO      -- 受注伝票枝番号
          );

          -- ポイント制度対応追加 start ABI
          -- 登録した予定発生ポイントテーブルに売上情報を追記する。
          -- 予定発生ポイントテーブルに(S20T001T)に売上伝票番号、売上伝票枝番号を登録する
          UPDATE S20T001T
          SET
            URIDNPYNO = acpt_rec.ACPTDNPYNO
            , URIEDANO = acpt_rec.EDANO
          WHERE
            KANRINO = cmnRegistKanrino
            AND ACPTDNPYNO = acpt_rec.ACPTDNPYNO
            AND ACPTEDANO = acpt_rec.EDANO;

          wKurouriknrino := cmnRegistKanrino;
          -- ポイント制度対応追加 end ABI
        END IF;
      END IF;

      -- 黒伝、または赤伝作成する場合、受注配達データの売上伝票番号を更新する
      IF cmnCngdlvryddflg = TRUE OR cmnCngmeisuryoflg = TRUE THEN
        UPDATE S04T001T JHTT
        SET JHTT.URIDNPYNO = cmnKurouridnpyno -- 売上伝票番号
            /* 更新者 */
          , JHTT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
          , JHTT.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
          , JHTT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
          , JHTT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
          , JHTT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
          , JHTT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
          , JHTT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
          , JHTT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
        WHERE JHTT.APKIHONCD  = acpt_rec.APKIHONCD
          AND JHTT.SHOPCD     = acpt_rec.SHOPCD
          AND JHTT.ACPTDNPYNO = acpt_rec.ACPTDNPYNO
          AND JHTT.EDANO      = acpt_rec.EDANO
          AND JHTT.YUKOKBN = '1'
        ;

        -- ポイント制度対応追加 start ABI
        -- 黒伝票分の売上伝票番号更新
        IF wKurouriknrino IS NOT NULL THEN
          UPDATE S20T001T YHPT
            SET YHPT.URIDNPYNO      = cmnKurouridnpyno        -- 売上伝票番号
              , YHPT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
              , YHPT.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
              , YHPT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
              , YHPT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
              , YHPT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
              , YHPT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
              , YHPT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
              , YHPT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
            WHERE YHPT.APKIHONCD    = acpt_rec.APKIHONCD
              AND YHPT.SHOPCD       = acpt_rec.SHOPCD
              AND YHPT.KANRINO      = wKurouriknrino
              AND YHPT.YUKOKBN      = '1';
        END IF;

        -- 赤伝票分の売上伝票番号更新
        IF cmnAkadenSaibanno IS NOT NULL THEN
          UPDATE S20T001T YHPT
            SET YHPT.URIDNPYNO      = cmnAkauridnpyno         -- 売上伝票番号
              , YHPT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
              , YHPT.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
              , YHPT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
              , YHPT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
              , YHPT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
              , YHPT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
              , YHPT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
              , YHPT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
            WHERE YHPT.APKIHONCD    = acpt_rec.APKIHONCD
              AND YHPT.SHOPCD       = acpt_rec.SHOPCD
              AND YHPT.KANRINO      = cmnAkadenSaibanno
              AND YHPT.YUKOKBN      = '1';
        END IF;
        -- ポイント制度対応追加 end ABI
      ELSE
        -- ACCIS2023-6735 売上データが存在しない場合にエラーとなる不具合の対応 start ABI
        BEGIN
          -- 赤伝票も黒伝票も作らない場合、売上明細テーブル.明細備考を受注配達明細テーブル.明細備考の値で更新する
          -- 受注と紐づいている売上データを取得
          SELECT URIT.*
            INTO uriTBL
          FROM S06T001T URIT
          WHERE URIT.APKIHONCD = work_rec.APKIHONCD
            AND URIT.SHOPCD    = work_rec.SHOPCD
            AND URIT.URIDNPYNO = acpt_rec.URIDNPYNO
          ;

          -- ACCIS2023-6937 start ABI
          -- 受注に紐づく売上情報を更新（配達担当者コード）
          UPDATE S06T001T URIT
          SET URIT.DLVRYTNSHACD       = acpt_rec.DLVRYTNSHACD   -- 配達担当者コード
            /* 更新タイムスタンプなど */
            , URIT.KOSHAAPKIHONCD     = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
            , URIT.KOSHASHOPCD        = userinfo_rec.SHOPCD     -- 更新者ショップコード
            , URIT.KOSHACD            = userinfo_rec.TNSHACD    -- 更新者コード
            , URIT.KSHNSYSTMYMD       = userinfo_rec.SYSTMYMD   -- 更新システム日付
            , URIT.KSHNSYSTMJIKAN     = userinfo_rec.SYSTMJIKAN -- 更新システム時間
            , URIT.KSHNUNYOYMD        = userinfo_rec.UNYOYMD    -- 更新運用日付
            , URIT.KSHNIPADRSE        = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
            , URIT.KSHNPGMID          = userinfo_rec.PGMID      -- 更新プログラムＩＤ
          WHERE
            URIT.APKIHONCD     = work_rec.APKIHONCD
            AND URIT.SHOPCD    = work_rec.SHOPCD
            AND URIT.URIDNPYNO = acpt_rec.URIDNPYNO
          ;
          -- ACCIS2023-6937 end ABI

          -- 受注配達明細ごとに紐づいている売上明細を更新
          FOR meisai_rec IN SELECT_ACPTM LOOP
            -- 受注配達明細と紐づいている売上明細テーブルのデータで
            -- 明細備考の値が異なっている場合、受注配達明細テーブル.明細備考の値を反映する
            UPDATE S06T002T URMT                                            -- 売上明細テーブル
            SET URMT.MEISAIBIKO           =  meisai_rec.MEISAIBIKO          -- 明細備考
              /* 更新タイムスタンプなど */
              , URMT.KOSHAAPKIHONCD       =  userinfo_rec.APKIHONCD         -- 更新者ＡＰ基本コード
              , URMT.KOSHASHOPCD          =  userinfo_rec.SHOPCD            -- 更新者ショップコード
              , URMT.KOSHACD              =  userinfo_rec.TNSHACD           -- 更新者コード
              , URMT.KSHNSYSTMYMD         =  userinfo_rec.SYSTMYMD          -- 更新システム日付
              , URMT.KSHNSYSTMJIKAN       =  userinfo_rec.SYSTMJIKAN        -- 更新システム時間
              , URMT.KSHNUNYOYMD          =  userinfo_rec.UNYOYMD           -- 更新運用日付
              , URMT.KSHNIPADRSE          =  userinfo_rec.IPADRSE           -- 更新ＩＰアドレス
              , URMT.KSHNPGMID            =  userinfo_rec.PGMID             -- 更新プログラムＩＤ
            WHERE URMT.APKIHONCD          =  uriTBL.APKIHONCD               -- ＡＰ基本コード（受注に紐づく売上伝票の明細）
              AND URMT.SHOPCD             =  uriTBL.SHOPCD                  -- ショップコード（受注に紐づく売上伝票の明細）
              AND URMT.URIDNPYNO          =  uriTBL.URIDNPYNO               -- 売上伝票番号（受注に紐づく売上伝票の明細）
              AND URMT.EDANO              =  uriTBL.EDANO                   -- 枝番号（受注に紐づく売上伝票の明細）
              AND URMT.URIDNPYMEISAINO    =  meisai_rec.ACPTDNPYMEISAINO    -- 売上伝票明細番号が受注伝票明細番号と一致
              AND URMT.URIDNPYMEISAIEDANO =  meisai_rec.ACPTDNPYMEISAIEDANO -- 売上伝票明細枝番号が受注伝票明細枝番号と一致
              -- 明細備考が異なるもののみ更新（空の場合に対応するためNVLで空の場合に固定値に変換して比較）
              AND NVL(URMT.MEISAIBIKO, '__NULLVALUE__') <> NVL(meisai_rec.MEISAIBIKO, '__NULLVALUE__')
            ;
          END LOOP; -- 受注配達明細テーブル LOOP
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            -- 売上データが存在しない場合、明細備考の更新を行う必要なし
            NULL;
        END;
        -- ACCIS2023-6735 end ABI

      END IF;

    END LOOP;

    EXCEPTION
      WHEN OTHERS THEN
        IF SELECT_ACPTD%ISOPEN THEN
           CLOSE SELECT_ACPTD;
        END IF;
        IF SELECT_ACPTM%ISOPEN THEN
           CLOSE SELECT_ACPTM;
        END IF;
        RAISE_APPLICATION_ERROR(-20013, SQLERRM || '売上データ作成時エラーが発生しました。');
  END processUriageWeb;
-- 本線対応追加 end ABI

  /*****************************************************************************/
  /* プロシージャ名：代引き更新処理                                                */
  /* 引数（ＩＮ）      ：work_rec        配達実績データヘッダ                      */
  /*                  ：tokuisakiMST    得意先マスタ                            */
  /*                  ：kbnKaishuhoho   回収方法区分                            */
  /*****************************************************************************/
  PROCEDURE processDaibiki( work_rec     IN S05W001T%rowtype -- 配達実績データヘッダ
                          , tokuisakiMST IN S02M013T%rowtype -- 得意先マスタ
-- インボイス対応追加 start ABI
                          , kbnKaishuhoho IN VARCHAR2        -- 回収方法区分
-- インボイス対応追加 end ABI
  ) IS
  /* 変数宣言 */
-- インボイス対応追加 start ABI
  uridnpyno       VARCHAR(11) := NULL; -- 売上伝票番号
  edano           NUMBER      := 0;    -- 枝番号
  uridnpymeisaino NUMBER      := 0;    -- 売上伝票明細番号
-- インボイス対応追加 end ABI
  /* CURSOR：受注配達テーブル */
  CURSOR SELECT_DAIBIKI IS
    SELECT JHTT.*
      FROM S04T001T JHTT
     WHERE JHTT.APKIHONCD  = work_rec.APKIHONCD
       AND JHTT.SHOPCD     = work_rec.SHOPCD
       AND JHTT.ACPTDNPYNO = work_rec.ACPTDNPYNO
       AND JHTT.EDANO      = work_rec.EDANO
-- インボイス対応変更 start ABI
       --AND JHTT.ACPTTHDAIBIKIFLG = '1' -- 受注時代引フラグが"1"
       AND (JHTT.ACPTTHDAIBIKIFLG = '1' -- 受注時代引フラグが"1"
            OR JHTT.SINGLDNPYFLG = '1') -- 単体伝票出力フラグが"1"
-- インボイス対応変更 end ABI
       AND JHTT.YUKOKBN = '1'
  ;
-- インボイス対応変更 start ABI
  --/* CURSOR：受注配達明細テーブル */
  --CURSOR SELECT_DBKM IS
    --SELECT JHMT.*
    --     , URIT.URIDD
    --  FROM S04T002T JHMT
    -- INNER JOIN S06T001T URIT
    --    ON ( URIT.APKIHONCD = JHMT.APKIHONCD
    --       AND URIT.SHOPCD = JHMT.SHOPCD
    --       AND URIT.URIDNPYNO = JHMT.URIDNPYNO
    --       AND URIT.EDANO = JHMT.URIDNPYEDANO
    --    )
    -- WHERE JHMT.APKIHONCD  = work_rec.APKIHONCD
    --   AND JHMT.SHOPCD     = work_rec.SHOPCD
    --   AND JHMT.ACPTDNPYNO = work_rec.ACPTDNPYNO
    --   AND JHMT.EDANO      = work_rec.EDANO
    --   AND JHMT.MEISAIKBN = '2' -- 明細区分:2（代引き）
    --   AND JHMT.URIDNPYNO IS NOT NULL
    --   AND JHMT.YUKOKBN = '1'
  /* CURSOR：売上テーブル */
  CURSOR SELECT_DBKM IS
    SELECT URIT.*
      FROM S06T001T URIT
     WHERE URIT.APKIHONCD  = work_rec.APKIHONCD
       AND URIT.SHOPCD     = work_rec.SHOPCD
       AND URIT.ACPTDNPYNO = work_rec.ACPTDNPYNO
       AND URIT.URIDNPYNO <> work_rec.ACPTDNPYNO
       AND URIT.SKYSHIMEDYDM IS NULL
       AND URIT.SKYSHIMEDD IS NULL
       AND URIT.YUKOKBN = '1'
-- インボイス対応変更 end ABI
  ;
  BEGIN
    FOR daibiki_rec IN SELECT_DAIBIKI LOOP
-- インボイス対応削除 start ABI
      ---- 請求残高テーブル：通常分更新
      --UPDATE S07T001T SKZT
      --   SET SKZT.KONKAITAXNUKIURIKIN = SKZT.KONKAITAXNUKIURIKIN + daibiki_rec.NOHINKIN
      --     , SKZT.KONKAISHOHITAXKIN = SKZT.KONKAISHOHITAXKIN + daibiki_rec.NOHINSHOHITAXKIN
      --     , SKZT.KESHIKOMIZD = SKZT.KESHIKOMIZD + daibiki_rec.NOHINKIN + daibiki_rec.NOHINSHOHITAXKIN
      --     , SKZT.SKYSHONO = daibiki_rec.ACPTDNPYNO
      --      /* 更新者 */
      --     , SKZT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
      --     , SKZT.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
      --     , SKZT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
      --     , SKZT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
      --     , SKZT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
      --     , SKZT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
      --     , SKZT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
      --     , SKZT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
      -- WHERE SKZT.APKIHONCD = daibiki_rec.APKIHONCD
      --   AND SKZT.SHOPCD = daibiki_rec.SHOPCD
      --   AND SKZT.SKYSAKICD = tokuisakiMST.SKYSAKICD
      --   AND SKZT.TOKUISAKICD = daibiki_rec.TOKUISAKICD
      --   AND SKZT.SHIMEDYDM = SUBSTRB(daibiki_rec.DLVRYJISSEKIDD, 1, 6)
      --   AND SKZT.SHIMEDD = SUBSTRB(daibiki_rec.DLVRYJISSEKIDD, 7, 2)
      --   AND SKZT.SKYPRCSJISSIKBN = '2' -- 2：代引き時請求処理
      --;

      ---- 更新対象がない場合は登録
      --IF SQL%ROWCOUNT = 0 THEN
      -- INSERT INTO S07T001T (
      --        APKIHONCD -- ＡＰ基本コード
      --      , SHOPCD -- ショップコード
      --      , SKYSAKICD -- 請求先コード
      --      , TOKUISAKICD -- 得意先コード
      --      , SHIMEDYDM -- 締年月
      --      , SHIMEDD -- 締日
      --      , SKYPRCSJISSIKBN -- 請求処理実施区分
      --      , SHIMEPRCSJISSIDD -- 締処理実施日
      --      , ZNKSHIMEDD -- 前回締日
      --      , KONKAISHIMEDD -- 今回締日
      --      , ZNKKURIKOSHIKIN -- 前回繰越金額
      --      , KONKAITAXNUKIURIKIN -- 今回税抜売上金額
      --      , KONKAISHOHITAXKIN -- 今回消費税金額
      --      , KONKAINYKNKIN -- 今回入金金額
      --      , KURIKOSHIKIN -- 繰越金額
      --      , KESHIKOMIKIN -- 消込金額
      --      , KESHIKOMIZD -- 消込残高
      --      , SKYICRNHYPRNTDD -- 請求一覧表印刷日
      --      , SKYSHOPRNTDD -- 請求書印刷日
      --      , SKYSHONO -- 請求書番号
      --      , SHOKAISKYSHONO -- 初回請求書番号
      --      , RIYOMEISAISHOPRNTDD -- 利用明細書印刷日
      --      , RIYOMEISAISHONO -- 利用明細書番号
      --      , TOKUSOKUJOPRNTDD -- 督促状印刷日
      --      , TOKUSOKUJONO -- 督促状番号
      --      , CSVDATCHUSYUTSUDD -- ＣＳＶデータ抽出日
      --      , RGRAYURYO -- レギュラー有料
      --      , RGRAMURYO -- レギュラー無料
      --      , SRMYURYO -- スリム有料
      --      , SRMMURYO -- スリム無料
      --      , SONOTAYURYO -- その他有料
      --      , SONOTAMURYO -- その他無料
      --      , SEIHINMIZUURI -- 製品水売上
      --      , RTLRKN -- レンタル料金
      --      , SONOTAURIKIN -- その他売上金額
      --      , AQCRGRAKYNPNSURYO -- アクアクララレギュラーキャンペーン数量
      --      , AQCRGRASVSURYO -- アクアクララレギュラーサービス数量
      --      , AQCRGRAPUROMOSHONSURYO -- アクアクララレギュラープロモーション数量
      --      , AQCRGRASIINSURYO -- アクアクララレギュラー試飲数量
      --      , AQCRGRAMNTESURYO -- アクアクララレギュラーメンテ数量
      --      , AQCRGRAKOKANSURYO -- アクアクララレギュラー交換数量
      --      , AQCRGRAURISURYO -- アクアクララレギュラー売上数量
      --      , AQCSRMKYNPNSURYO -- アクアクララスリムキャンペーン数量
      --      , AQCSRMSVSURYO -- アクアクララスリムサービス数量
      --      , AQCSRMPUROMOSHONSURYO -- アクアクララスリムプロモーション数量
      --      , AQCSRMSIINSURYO -- アクアクララスリム試飲数量
      --      , AQCSRMMNTESURYO -- アクアクララスリムメンテ数量
      --      , AQCSRMKOKANSURYO -- アクアクララスリム交換数量
      --      , AQCSRMURISURYO -- アクアクララスリム売上数量
      --      , ETCSHNMZKYNPNSURYO -- その他製品水キャンペーン数量
      --      , ETCSHNMZSVSURYO -- その他製品水サービス数量
      --      , ETCSHNMZPUROMOSHONSURYO -- その他製品水プロモーション数量
      --      , ETCSHNMZSIINSURYO -- その他製品水試飲数量
      --      , ETCSHNMZMNTESURYO -- その他製品水メンテ数量
      --      , ETCSHNMZKOKANSURYO -- その他製品水交換数量
      --      , ETCSHNMZURISURYO -- その他製品水売上数量
      --      , SHNMZRGRAKIN -- 製品水レギュラー金額
      --      , SHNMZSRMKIN -- 製品水スリム金額
      --      , SHNMZSONOTAKIN -- 製品水その他金額
      --      , RNTLRWSVKIN -- レンタル料サーバー金額
      --      , RNTLRHKSHNKIN -- レンタル料他商品金額
      --      , SNTSHNWSVKIN -- その他商品サーバー金額
      --      , SNTSHNHKSHNKIN -- その他商品他商品金額
      --      , SNTSHNKNPHNKIN -- その他商品キャンペーン品金額
      --      , KAISHUHOHOKBN -- 回収方法区分
      --      , SHRYTIDYDMDD -- 支払予定年月日
      --      , KSISHIMEDYDMDD -- 決済締年月日
      --      , KSIKKNCD -- 決済機関コード
      --      , YUKOKBN -- 有効区分
      --       /* 更新者 */
      --      , TOSHAAPKIHONCD -- 登録者ＡＰ基本コード
      --      , TOSHASHOPCD -- 登録者ショップコード
      --      , TOSHACD -- 登録者コード
      --      , INPTSYSTMYMD -- 登録システム日付
      --      , INPTSYSTMJIKAN -- 登録システム時間
      --      , INPTUNYOYMD -- 登録運用日付
      --      , INPTIPADRSE -- 登録ＩＰアドレス
      --      , INPTPGMID -- 登録プログラムＩＤ
      --      , KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
      --      , KOSHASHOPCD -- 更新者ショップコード
      --      , KOSHACD -- 更新者コード
      --      , KSHNSYSTMYMD -- 更新システム日付
      --      , KSHNSYSTMJIKAN -- 更新システム時間
      --      , KSHNUNYOYMD -- 更新運用日付
      --      , KSHNIPADRSE -- 更新ＩＰアドレス
      --      , KSHNPGMID -- 更新プログラムＩＤ
      -- ) VALUES (
      --          daibiki_rec.APKIHONCD -- ＡＰ基本コード
      --        , daibiki_rec.SHOPCD -- ショップコード
      --        , tokuisakiMST.SKYSAKICD -- 請求先コード
      --        , daibiki_rec.TOKUISAKICD -- 得意先コード
      --        , SUBSTRB(daibiki_rec.DLVRYJISSEKIDD, 1, 6) -- 締年月
      --        , SUBSTRB(daibiki_rec.DLVRYJISSEKIDD, 7, 2) -- 締日
      --        , '2' -- 請求処理実施区分：2（代引き時請求処理
      --        , userinfo_rec.UNYOYMD -- 締処理実施日
      --        , '00000000' -- 前回締日
      --        , daibiki_rec.DLVRYJISSEKIDD -- 今回締日
      --        , 0 -- 前回繰越金額
      --        , daibiki_rec.NOHINKIN -- 今回税抜売上金額
      --        , daibiki_rec.NOHINSHOHITAXKIN -- 今回消費税金額
      --        , 0 -- 今回入金金額
      --        , 0 -- 繰越金額
      --        , 0 -- 消込金額
      --        , daibiki_rec.NOHINKIN + daibiki_rec.NOHINSHOHITAXKIN -- 消込残高
      --        , '00000000' -- 請求一覧表印刷日
      --        , '00000000' -- 請求書印刷日
      --        , daibiki_rec.ACPTDNPYNO -- 請求書番号
      --        , daibiki_rec.ACPTDNPYNO -- 初回請求書番号
      --        , '00000000' -- 利用明細書印刷日
      --        , '000000000000' -- 利用明細書番号
      --        , '00000000' -- 督促状印刷日
      --        , '000000000000' -- 督促状番号
      --        , '00000000' -- ＣＳＶデータ抽出日
      --        , 0 -- レギュラー有料
      --        , 0 -- レギュラー無料
      --        , 0 -- スリム有料
      --        , 0 -- スリム無料
      --        , 0 -- その他有料
      --        , 0 -- その他無料
      --        , 0 -- 製品水売上
      --        , 0 -- レンタル料金
      --        , 0 -- その他売上金額
      --        , 0 -- アクアクララレギュラーキャンペーン数量
      --        , 0 -- アクアクララレギュラーサービス数量
      --        , 0 -- アクアクララレギュラープロモーション数量
      --        , 0 -- アクアクララレギュラー試飲数量
      --        , 0 -- アクアクララレギュラーメンテ数量
      --        , 0 -- アクアクララレギュラー交換数量
      --        , 0 -- アクアクララレギュラー売上数量
      --        , 0 -- アクアクララスリムキャンペーン数量
      --        , 0 -- アクアクララスリムサービス数量
      --        , 0 -- アクアクララスリムプロモーション数量
      --        , 0 -- アクアクララスリム試飲数量
      --        , 0 -- アクアクララスリムメンテ数量
      --        , 0 -- アクアクララスリム交換数量
      --        , 0 -- アクアクララスリム売上数量
      --        , 0 -- その他製品水キャンペーン数量
      --        , 0 -- その他製品水サービス数量
      --        , 0 -- その他製品水プロモーション数量
      --        , 0 -- その他製品水試飲数量
      --        , 0 -- その他製品水メンテ数量
      --        , 0 -- その他製品水交換数量
      --        , 0 -- その他製品水売上数量
      --        , 0 -- 製品水レギュラー金額
      --        , 0 -- 製品水スリム金額
      --        , 0 -- 製品水その他金額
      --        , 0 -- レンタル料サーバー金額
      --        , 0 -- レンタル料他商品金額
      --        , 0 -- その他商品サーバー金額
      --        , 0 -- その他商品他商品金額
      --        , 0 -- その他商品キャンペーン品金額
      --        , '7' -- 回収方法区分：7（代引き）
      --        , NULL -- 支払予定年月日
      --        , '00000000' -- 決済締年月日
      --        , NULL -- 決済機関コード
      --        , '1' -- 有効区分
      --          /* 更新者 */
      --        , userinfo_rec.APKIHONCD  -- 登録者ＡＰ基本コード
      --        , userinfo_rec.SHOPCD     -- 登録者ショップコード
      --        , userinfo_rec.TNSHACD    -- 登録者コード
      --        , userinfo_rec.SYSTMYMD   -- 登録システム日付
      --        , userinfo_rec.SYSTMJIKAN -- 登録システム時間
      --        , userinfo_rec.UNYOYMD    -- 登録運用日付
      --        , userinfo_rec.IPADRSE    -- 登録ＩＰアドレス
      --        , userinfo_rec.PGMID      -- 登録プログラムＩＤ
      --        , userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
      --        , userinfo_rec.SHOPCD     -- 更新者ショップコード
      --        , userinfo_rec.TNSHACD    -- 更新者コード
      --        , userinfo_rec.SYSTMYMD   -- 更新システム日付
      --        , userinfo_rec.SYSTMJIKAN -- 更新システム時間
      --        , userinfo_rec.UNYOYMD    -- 更新運用日付
      --        , userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
      --        , userinfo_rec.PGMID      -- 更新プログラムＩＤ
      -- );
      --END IF;
-- インボイス対応削除 end ABI

-- インボイス対応追加 start ABI
      -- 変数の定義
      -- 売上伝票番号（黒）が設定されている場合、売上伝票番号に売上伝票番号（黒）を設定する
      IF cmnKurouridnpyno IS NOT NULL THEN
        uridnpyno       := cmnKurouridnpyno;
        edano           := 0;
      ELSE
        uridnpyno       := daibiki_rec.ACPTDNPYNO;
        edano           := daibiki_rec.EDANO;
      END IF;
      SELECT NVL(MAX(ACPTDNPYMEISAINO),0)
        INTO uridnpymeisaino
        FROM S04T002T
       WHERE APKIHONCD  = daibiki_rec.APKIHONCD
         AND SHOPCD     = daibiki_rec.SHOPCD
         AND ACPTDNPYNO = daibiki_rec.ACPTDNPYNO
         AND EDANO      = daibiki_rec.EDANO
         AND YUKOKBN    = '1';
-- インボイス対応追加 end ABI
      FOR daibikimei_rec IN SELECT_DBKM LOOP
        -- 売上テーブル更新
        UPDATE S06T001T DAIBIKI_URI
           SET URISHIMEPRCSDD = daibiki_rec.DLVRYDD -- 売上締実施日
         WHERE DAIBIKI_URI.APKIHONCD = daibikimei_rec.APKIHONCD
           AND DAIBIKI_URI.SHOPCD = daibikimei_rec.SHOPCD
           AND DAIBIKI_URI.URIDNPYNO = daibikimei_rec.URIDNPYNO
        ;

-- インボイス対応削除 start ABI
        ---- 請求残高テーブル：代引き分更新
        --UPDATE S07T001T SKZT
        --   SET SKZT.KONKAITAXNUKIURIKIN = SKZT.KONKAITAXNUKIURIKIN + daibikimei_rec.NOHINKIN
        --     , SKZT.KONKAISHOHITAXKIN = SKZT.KONKAISHOHITAXKIN + daibikimei_rec.NOHINSHOHITAXKIN
        --     , SKZT.KESHIKOMIZD = SKZT.KESHIKOMIZD + daibikimei_rec.NOHINKIN + daibikimei_rec.NOHINSHOHITAXKIN
        --      /* 更新者 */
        --     , SKZT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
        --     , SKZT.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
        --     , SKZT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
        --     , SKZT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
        --     , SKZT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
        --     , SKZT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
        --     , SKZT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
        --     , SKZT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
        -- WHERE SKZT.APKIHONCD = daibiki_rec.APKIHONCD
        --   AND SKZT.SHOPCD = daibiki_rec.SHOPCD
        --   AND SKZT.SKYSAKICD = tokuisakiMST.SKYSAKICD
        --   AND SKZT.TOKUISAKICD = daibiki_rec.TOKUISAKICD
        --   AND SKZT.SHIMEDYDM = SUBSTRB(daibikimei_rec.URIDD, 1, 6)
        --   AND SKZT.SHIMEDD = SUBSTRB(daibikimei_rec.URIDD, 7, 2)
        --   AND SKZT.SKYPRCSJISSIKBN = '2' -- 2：代引き時請求処理
        --;
        ---- 更新対象がない場合は登録
        --IF SQL%ROWCOUNT = 0 THEN
        -- INSERT INTO S07T001T (
        --        APKIHONCD -- ＡＰ基本コード
        --      , SHOPCD -- ショップコード
        --      , SKYSAKICD -- 請求先コード
        --      , TOKUISAKICD -- 得意先コード
        --      , SHIMEDYDM -- 締年月
        --      , SHIMEDD -- 締日
        --      , SKYPRCSJISSIKBN -- 請求処理実施区分
        --      , SHIMEPRCSJISSIDD -- 締処理実施日
        --      , ZNKSHIMEDD -- 前回締日
        --      , KONKAISHIMEDD -- 今回締日
        --      , ZNKKURIKOSHIKIN -- 前回繰越金額
        --      , KONKAITAXNUKIURIKIN -- 今回税抜売上金額
        --      , KONKAISHOHITAXKIN -- 今回消費税金額
        --      , KONKAINYKNKIN -- 今回入金金額
        --      , KURIKOSHIKIN -- 繰越金額
        --      , KESHIKOMIKIN -- 消込金額
        --      , KESHIKOMIZD -- 消込残高
        --      , SKYICRNHYPRNTDD -- 請求一覧表印刷日
        --      , SKYSHOPRNTDD -- 請求書印刷日
        --      , SKYSHONO -- 請求書番号
        --      , SHOKAISKYSHONO -- 初回請求書番号
        --      , RIYOMEISAISHOPRNTDD -- 利用明細書印刷日
        --      , RIYOMEISAISHONO -- 利用明細書番号
        --      , TOKUSOKUJOPRNTDD -- 督促状印刷日
        --      , TOKUSOKUJONO -- 督促状番号
        --      , CSVDATCHUSYUTSUDD -- ＣＳＶデータ抽出日
        --      , RGRAYURYO -- レギュラー有料
        --      , RGRAMURYO -- レギュラー無料
        --      , SRMYURYO -- スリム有料
        --      , SRMMURYO -- スリム無料
        --      , SONOTAYURYO -- その他有料
        --      , SONOTAMURYO -- その他無料
        --      , SEIHINMIZUURI -- 製品水売上
        --      , RTLRKN -- レンタル料金
        --      , SONOTAURIKIN -- その他売上金額
        --      , AQCRGRAKYNPNSURYO -- アクアクララレギュラーキャンペーン数量
        --      , AQCRGRASVSURYO -- アクアクララレギュラーサービス数量
        --      , AQCRGRAPUROMOSHONSURYO -- アクアクララレギュラープロモーション数量
        --      , AQCRGRASIINSURYO -- アクアクララレギュラー試飲数量
        --      , AQCRGRAMNTESURYO -- アクアクララレギュラーメンテ数量
        --      , AQCRGRAKOKANSURYO -- アクアクララレギュラー交換数量
        --      , AQCRGRAURISURYO -- アクアクララレギュラー売上数量
        --      , AQCSRMKYNPNSURYO -- アクアクララスリムキャンペーン数量
        --      , AQCSRMSVSURYO -- アクアクララスリムサービス数量
        --      , AQCSRMPUROMOSHONSURYO -- アクアクララスリムプロモーション数量
        --      , AQCSRMSIINSURYO -- アクアクララスリム試飲数量
        --      , AQCSRMMNTESURYO -- アクアクララスリムメンテ数量
        --      , AQCSRMKOKANSURYO -- アクアクララスリム交換数量
        --      , AQCSRMURISURYO -- アクアクララスリム売上数量
        --      , ETCSHNMZKYNPNSURYO -- その他製品水キャンペーン数量
        --      , ETCSHNMZSVSURYO -- その他製品水サービス数量
        --      , ETCSHNMZPUROMOSHONSURYO -- その他製品水プロモーション数量
        --      , ETCSHNMZSIINSURYO -- その他製品水試飲数量
        --      , ETCSHNMZMNTESURYO -- その他製品水メンテ数量
        --      , ETCSHNMZKOKANSURYO -- その他製品水交換数量
        --      , ETCSHNMZURISURYO -- その他製品水売上数量
        --      , SHNMZRGRAKIN -- 製品水レギュラー金額
        --      , SHNMZSRMKIN -- 製品水スリム金額
        --      , SHNMZSONOTAKIN -- 製品水その他金額
        --      , RNTLRWSVKIN -- レンタル料サーバー金額
        --      , RNTLRHKSHNKIN -- レンタル料他商品金額
        --      , SNTSHNWSVKIN -- その他商品サーバー金額
        --      , SNTSHNHKSHNKIN -- その他商品他商品金額
        --      , SNTSHNKNPHNKIN -- その他商品キャンペーン品金額
        --      , KAISHUHOHOKBN -- 回収方法区分
        --      , SHRYTIDYDMDD -- 支払予定年月日
        --      , KSISHIMEDYDMDD -- 決済締年月日
        --      , KSIKKNCD -- 決済機関コード
        --      , YUKOKBN -- 有効区分
        --      , TOSHAAPKIHONCD -- 登録者ＡＰ基本コード
        --      , TOSHASHOPCD -- 登録者ショップコード
        --      , TOSHACD -- 登録者コード
        --      , INPTSYSTMYMD -- 登録システム日付
        --      , INPTSYSTMJIKAN -- 登録システム時間
        --      , INPTUNYOYMD -- 登録運用日付
        --      , INPTIPADRSE -- 登録ＩＰアドレス
        --      , INPTPGMID -- 登録プログラムＩＤ
        --      , KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
        --      , KOSHASHOPCD -- 更新者ショップコード
        --      , KOSHACD -- 更新者コード
        --      , KSHNSYSTMYMD -- 更新システム日付
        --      , KSHNSYSTMJIKAN -- 更新システム時間
        --      , KSHNUNYOYMD -- 更新運用日付
        --      , KSHNIPADRSE -- 更新ＩＰアドレス
        --      , KSHNPGMID -- 更新プログラムＩＤ
        -- ) VALUES (
        --          daibiki_rec.APKIHONCD -- ＡＰ基本コード
        --        , daibiki_rec.SHOPCD -- ショップコード
        --        , tokuisakiMST.SKYSAKICD -- 請求先コード
        --        , daibiki_rec.TOKUISAKICD -- 得意先コード
        --        , SUBSTRB(daibikimei_rec.URIDD, 1, 6) -- 締年月
        --        , SUBSTRB(daibikimei_rec.URIDD, 7, 2) -- 締日
        --        , '2' -- 請求処理実施区分：2：代引き時請求処理
        --        , userinfo_rec.UNYOYMD -- 締処理実施日
        --        , '00000000' -- 前回締日
        --        , daibikimei_rec.URIDD -- 今回締日
        --        , 0 -- 前回繰越金額
        --        , daibikimei_rec.NOHINKIN -- 今回税抜売上金額
        --        , daibikimei_rec.NOHINSHOHITAXKIN -- 今回消費税金額
        --        , 0 -- 今回入金金額
        --        , 0 -- 繰越金額
        --        , 0 -- 消込金額
        --        , daibikimei_rec.NOHINKIN + daibikimei_rec.NOHINSHOHITAXKIN -- 消込残高
        --        , '00000000' -- 請求一覧表印刷日
        --        , '00000000' -- 請求書印刷日
        --        , daibikimei_rec.ACPTDNPYNO -- 請求書番号
        --        , daibikimei_rec.URIDNPYNO -- 初回請求書番号
        --        , '00000000' -- 利用明細書印刷日
        --        , '000000000000' -- 利用明細書番号
        --        , '00000000' -- 督促状印刷日
        --        , '000000000000' -- 督促状番号
        --        , '00000000' -- ＣＳＶデータ抽出日
        --        , 0 -- レギュラー有料
        --        , 0 -- レギュラー無料
        --        , 0 -- スリム有料
        --        , 0 -- スリム無料
        --        , 0 -- その他有料
        --        , 0 -- その他無料
        --        , 0 -- 製品水売上
        --        , 0 -- レンタル料金
        --        , 0 -- その他売上金額
        --        , 0 -- アクアクララレギュラーキャンペーン数量
        --        , 0 -- アクアクララレギュラーサービス数量
        --        , 0 -- アクアクララレギュラープロモーション数量
        --        , 0 -- アクアクララレギュラー試飲数量
        --        , 0 -- アクアクララレギュラーメンテ数量
        --        , 0 -- アクアクララレギュラー交換数量
        --        , 0 -- アクアクララレギュラー売上数量
        --        , 0 -- アクアクララスリムキャンペーン数量
        --        , 0 -- アクアクララスリムサービス数量
        --        , 0 -- アクアクララスリムプロモーション数量
        --        , 0 -- アクアクララスリム試飲数量
        --        , 0 -- アクアクララスリムメンテ数量
        --        , 0 -- アクアクララスリム交換数量
        --        , 0 -- アクアクララスリム売上数量
        --        , 0 -- その他製品水キャンペーン数量
        --        , 0 -- その他製品水サービス数量
        --        , 0 -- その他製品水プロモーション数量
        --        , 0 -- その他製品水試飲数量
        --        , 0 -- その他製品水メンテ数量
        --        , 0 -- その他製品水交換数量
        --        , 0 -- その他製品水売上数量
        --        , 0 -- 製品水レギュラー金額
        --        , 0 -- 製品水スリム金額
        --        , 0 -- 製品水その他金額
        --        , 0 -- レンタル料サーバー金額
        --        , 0 -- レンタル料他商品金額
        --        , 0 -- その他商品サーバー金額
        --        , 0 -- その他商品他商品金額
        --        , 0 -- その他商品キャンペーン品金額
        --        , '7' -- 回収方法区分：7：代引き
        --        , NULL -- 支払予定年月日
        --        , '00000000' -- 決済締年月日
        --        , NULL -- 決済機関コード
        --        , '1' -- 有効区分
        --        , userinfo_rec.APKIHONCD  -- 登録者ＡＰ基本コード
        --        , userinfo_rec.SHOPCD     -- 登録者ショップコード
        --        , userinfo_rec.TNSHACD    -- 登録者コード
        --        , userinfo_rec.SYSTMYMD   -- 登録システム日付
        --        , userinfo_rec.SYSTMJIKAN -- 登録システム時間
        --        , userinfo_rec.UNYOYMD    -- 登録運用日付
        --        , userinfo_rec.IPADRSE    -- 登録ＩＰアドレス
        --        , userinfo_rec.PGMID      -- 登録プログラムＩＤ
        --        , userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
        --        , userinfo_rec.SHOPCD     -- 更新者ショップコード
        --        , userinfo_rec.TNSHACD    -- 更新者コード
        --        , userinfo_rec.SYSTMYMD   -- 更新システム日付
        --        , userinfo_rec.SYSTMJIKAN -- 更新システム時間
        --        , userinfo_rec.UNYOYMD    -- 更新運用日付
        --        , userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
        --        , userinfo_rec.PGMID      -- 更新プログラムＩＤ
        -- );
        --END IF;
-- インボイス対応削除 end ABI
      END LOOP;

-- インボイス対応追加 start ABI
      -- 税区分ごとの消費税金額を売上明細テーブルに登録
      -- 締め後一般消費税金額が0ではない場合、売上明細テーブルに一般消費税を登録する
      IF NVL(cmnShimeippantaxkin, 0) <> 0 THEN
        -- 売上伝票明細番号をカウントアップ
        uridnpymeisaino := uridnpymeisaino + 1;
        -- 売上明細テーブルを登録
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
             , TOKUISAKIKBN       -- 得意先区分
             , JUTAKUDLVRYCSTKBN  -- 受託配達顧客区分
             , YUKOKBN            -- 有効区分
             , TOSHAAPKIHONCD     -- 登録者ＡＰ基本コード
             , TOSHASHOPCD        -- 登録者ショップコード
             , TOSHACD            -- 登録者コード
             , INPTSYSTMYMD       -- 登録システム日付
             , INPTSYSTMJIKAN     -- 登録システム時間
             , INPTUNYOYMD        -- 登録運用日付
             , INPTIPADRSE        -- 登録ＩＰアドレス
             , INPTPGMID          -- 登録プログラムＩＤ
             , KOSHAAPKIHONCD     -- 更新者ＡＰ基本コード
             , KOSHASHOPCD        -- 更新者ショップコード
             , KOSHACD            -- 更新者コード
             , KSHNSYSTMYMD       -- 更新システム日付
             , KSHNSYSTMJIKAN     -- 更新システム時間
             , KSHNUNYOYMD        -- 更新運用日付
             , KSHNIPADRSE        -- 更新ＩＰアドレス
             , KSHNPGMID          -- 更新プログラムＩＤ
        )
        VALUES (
               daibiki_rec.APKIHONCD        -- ＡＰ基本コード
             , daibiki_rec.SHOPCD           -- ショップコード
             , uridnpyno                    -- 売上伝票番号
             , edano                        -- 枝番号
             , uridnpymeisaino              -- 売上伝票明細番号
             , 0                            -- 売上伝票明細枝番号
             , '99'                         -- 大分類コード
             , '001'                        -- 中分類コード
             , '001'                        -- 小分類コード
             , '099901'                     -- 商品コード
             , '01'                         -- 計上区分
             , 1                            -- 数量
             , 0                            -- 標準単価
             , 0                            -- 単価
             , 0                            -- 金額
             , daibiki_rec.NOHINIPPANTAXKIN -- 単価消費税金額
             , daibiki_rec.NOHINIPPANTAXKIN -- 消費税金額
             , 0                            -- 参考消費税金額
             , '1'                          -- 税区分
             , '0'                          -- 明細区分
             , '1'                          -- 入力明細フラグ
             , '1'                          -- 単品明細フラグ
             , NULL                         -- 明細備考
             , NULL                         -- セット商品コード
             , NULL                         -- 型式
             , NULL                         -- 仕入数量
             , NULL                         -- 仕入金額
             , NULL                         -- 仕入単価
             , NULL                         -- 仕入消費税金額
             , NULL                         -- 在庫更新済フラグ
             , tokuisakiMST.TOKUISAKIKBN    -- 得意先区分
             , CASE WHEN tokuisakiMST.TOKUISAKIKBN = '1'
                AND tokuisakiMST.JUTAKUDLVRYCSTKBN = '4'
                AND daibiki_rec.ITAKUAITEACPTDNPYNO IS NULL THEN
                 '1'
               ELSE
                 tokuisakiMST.JUTAKUDLVRYCSTKBN
               END                          -- 受託配達顧客区分
             , '1'                          -- 有効区分
               /* 更新者 */
             , userinfo_rec.APKIHONCD       -- 登録者ＡＰ基本コード
             , userinfo_rec.SHOPCD          -- 登録者ショップコード
             , userinfo_rec.TNSHACD         -- 登録者コード
             , userinfo_rec.SYSTMYMD        -- 登録システム日付
             , userinfo_rec.SYSTMJIKAN      -- 登録システム時間
             , userinfo_rec.UNYOYMD         -- 登録運用日付
             , userinfo_rec.IPADRSE         -- 登録ＩＰアドレス
             , userinfo_rec.PGMID           -- 登録プログラムＩＤ
             , userinfo_rec.APKIHONCD       -- 更新者ＡＰ基本コード
             , userinfo_rec.SHOPCD          -- 更新者ショップコード
             , userinfo_rec.TNSHACD         -- 更新者コード
             , userinfo_rec.SYSTMYMD        -- 更新システム日付
             , userinfo_rec.SYSTMJIKAN      -- 更新システム時間
             , userinfo_rec.UNYOYMD         -- 更新運用日付
             , userinfo_rec.IPADRSE         -- 更新ＩＰアドレス
             , userinfo_rec.PGMID           -- 更新プログラムＩＤ
        );
      END IF;
      -- 締め後軽減消費税金額が0ではない場合、売上明細テーブルに軽減消費税を登録する
      IF NVL(cmnShimekeigentaxkin, 0) <> 0 THEN
        -- 売上伝票明細番号をカウントアップ
        uridnpymeisaino := uridnpymeisaino + 1;
        -- 売上明細テーブルを登録
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
             , TOKUISAKIKBN       -- 得意先区分
             , JUTAKUDLVRYCSTKBN  -- 受託配達顧客区分
             , YUKOKBN            -- 有効区分
             , TOSHAAPKIHONCD     -- 登録者ＡＰ基本コード
             , TOSHASHOPCD        -- 登録者ショップコード
             , TOSHACD            -- 登録者コード
             , INPTSYSTMYMD       -- 登録システム日付
             , INPTSYSTMJIKAN     -- 登録システム時間
             , INPTUNYOYMD        -- 登録運用日付
             , INPTIPADRSE        -- 登録ＩＰアドレス
             , INPTPGMID          -- 登録プログラムＩＤ
             , KOSHAAPKIHONCD     -- 更新者ＡＰ基本コード
             , KOSHASHOPCD        -- 更新者ショップコード
             , KOSHACD            -- 更新者コード
             , KSHNSYSTMYMD       -- 更新システム日付
             , KSHNSYSTMJIKAN     -- 更新システム時間
             , KSHNUNYOYMD        -- 更新運用日付
             , KSHNIPADRSE        -- 更新ＩＰアドレス
             , KSHNPGMID          -- 更新プログラムＩＤ
        )
        VALUES (
               daibiki_rec.APKIHONCD        -- ＡＰ基本コード
             , daibiki_rec.SHOPCD           -- ショップコード
             , uridnpyno                    -- 売上伝票番号
             , edano                        -- 枝番号
             , uridnpymeisaino              -- 売上伝票明細番号
             , 0                            -- 売上伝票明細枝番号
             , '99'                         -- 大分類コード
             , '001'                        -- 中分類コード
             , '002'                        -- 小分類コード
             , '099902'                     -- 商品コード
             , '01'                         -- 計上区分
             , 1                            -- 数量
             , 0                            -- 標準単価
             , 0                            -- 単価
             , 0                            -- 金額
             , daibiki_rec.NOHINKEIGENTAXKIN -- 単価消費税金額
             , daibiki_rec.NOHINKEIGENTAXKIN -- 消費税金額
             , 0                            -- 参考消費税金額
             , '4'                          -- 税区分
             , '0'                          -- 明細区分
             , '1'                          -- 入力明細フラグ
             , '1'                          -- 単品明細フラグ
             , NULL                         -- 明細備考
             , NULL                         -- セット商品コード
             , NULL                         -- 型式
             , NULL                         -- 仕入数量
             , NULL                         -- 仕入金額
             , NULL                         -- 仕入単価
             , NULL                         -- 仕入消費税金額
             , NULL                         -- 在庫更新済フラグ
             , tokuisakiMST.TOKUISAKIKBN    -- 得意先区分
             , CASE WHEN tokuisakiMST.TOKUISAKIKBN = '1'
                AND tokuisakiMST.JUTAKUDLVRYCSTKBN = '4'
                AND daibiki_rec.ITAKUAITEACPTDNPYNO IS NULL THEN
                 '1'
               ELSE
                 tokuisakiMST.JUTAKUDLVRYCSTKBN
               END                          -- 受託配達顧客区分
             , '1'                          -- 有効区分
               /* 更新者 */
             , userinfo_rec.APKIHONCD       -- 登録者ＡＰ基本コード
             , userinfo_rec.SHOPCD          -- 登録者ショップコード
             , userinfo_rec.TNSHACD         -- 登録者コード
             , userinfo_rec.SYSTMYMD        -- 登録システム日付
             , userinfo_rec.SYSTMJIKAN      -- 登録システム時間
             , userinfo_rec.UNYOYMD         -- 登録運用日付
             , userinfo_rec.IPADRSE         -- 登録ＩＰアドレス
             , userinfo_rec.PGMID           -- 登録プログラムＩＤ
             , userinfo_rec.APKIHONCD       -- 更新者ＡＰ基本コード
             , userinfo_rec.SHOPCD          -- 更新者ショップコード
             , userinfo_rec.TNSHACD         -- 更新者コード
             , userinfo_rec.SYSTMYMD        -- 更新システム日付
             , userinfo_rec.SYSTMJIKAN      -- 更新システム時間
             , userinfo_rec.UNYOYMD         -- 更新運用日付
             , userinfo_rec.IPADRSE         -- 更新ＩＰアドレス
             , userinfo_rec.PGMID           -- 更新プログラムＩＤ
        );
      END IF;

      -- 売上テーブルを更新する
      UPDATE S06T001T URIT
         SET URIT.SHOHITAXKIN = daibiki_rec.NOHINSHOHITAXKIN
            /* 更新者 */
           , URIT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
           , URIT.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
           , URIT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
           , URIT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
           , URIT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
           , URIT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
           , URIT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
           , URIT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
       WHERE URIT.APKIHONCD = daibiki_rec.APKIHONCD
         AND URIT.SHOPCD = daibiki_rec.SHOPCD
         AND URIT.URIDNPYNO = daibiki_rec.URIDNPYNO
      ;

      -- 請求締処理を行う
      D1502B01.SeikyuShimeTantai(daibiki_rec.APKIHONCD      -- ＡＰ基本コード
                               , daibiki_rec.SHOPCD         -- ショップコード
                               , userinfo_rec.APKIHONCD     -- 担当者ＡＰ基本コード
                               , userinfo_rec.SHOPCD        -- 担当者ショップコード
                               , userinfo_rec.TNSHACD       -- 担当者コード
                               , userinfo_rec.IPADRSE       -- ＩＰアドレス
                               , userinfo_rec.SYSTMYMD      -- 依頼日
                               , userinfo_rec.SYSTMJIKAN    -- 依頼時間
                               , daibiki_rec.DLVRYJISSEKIDD -- 請求締日
                               , '3'                        -- 処理モード
                               , tokuisakiMST.SKYSAKICD     -- 請求先コード
                               , tokuisakiMST.TOKUISAKICD   -- 得意先コード
                               , uridnpyno                  -- 売上伝票番号
                               , kbnKaishuhoho              -- 回収方法区分
                               , daibiki_rec.SINGLDNPYFLG   -- 単体伝票締めフラグ
                                );

      -- 未締売上分の売上データに対しても請求締処理を行う
      FOR mijime_uri IN (SELECT URIT.*
                           FROM S06T001T URIT
                          WHERE URIT.APKIHONCD  = daibiki_rec.APKIHONCD
                            AND URIT.SHOPCD     = daibiki_rec.SHOPCD
                            AND URIT.ACPTDNPYNO = daibiki_rec.ACPTDNPYNO
                            AND URIT.URIDNPYNO  <> URIT.ACPTDNPYNO
                            AND URIT.SKYSHIMEDYDM IS NULL
                            AND URIT.SKYSHIMEDD IS NULL
                            AND URIT.YUKOKBN = '1') LOOP
        -- 赤伝票、黒伝票のデータの場合、スキップ
        IF mijime_uri.URIDNPYNO = uridnpyno OR mijime_uri.URIDNPYNO = cmnAkauridnpyno THEN
          CONTINUE;
        END IF;
        -- 未締売上分の請求締処理を行う
        D1502B01.SeikyuShimeTantai(daibiki_rec.APKIHONCD      -- ＡＰ基本コード
                                 , daibiki_rec.SHOPCD         -- ショップコード
                                 , userinfo_rec.APKIHONCD     -- 担当者ＡＰ基本コード
                                 , userinfo_rec.SHOPCD        -- 担当者ショップコード
                                 , userinfo_rec.TNSHACD       -- 担当者コード
                                 , userinfo_rec.IPADRSE       -- ＩＰアドレス
                                 , userinfo_rec.SYSTMYMD      -- 依頼日
                                 , userinfo_rec.SYSTMJIKAN    -- 依頼時間
                                 , mijime_uri.URIDD           -- 請求締日
                                 , '3'                        -- 処理モード
                                 , tokuisakiMST.SKYSAKICD     -- 請求先コード
                                 , tokuisakiMST.TOKUISAKICD   -- 得意先コード
                                 , mijime_uri.URIDNPYNO       -- 売上伝票番号
                                 , kbnKaishuhoho              -- 回収方法区分
                                 , daibiki_rec.SINGLDNPYFLG   -- 単体伝票締めフラグ
                                  );
      END LOOP;

      -- 代引き（受注時代引きフラグが"1"（代引き）、または単体伝票締めフラグが"1"（単体伝票締めする））の場合、請求締ワークを更新し、滞留更新処理を行う
      IF daibiki_rec.ACPTTHDAIBIKIFLG = '1' OR daibiki_rec.SINGLDNPYFLG = '1' THEN
        -- 滞留更新処理を呼び出す
        D1504B01.Tairyukousin(daibiki_rec.APKIHONCD                    -- ＡＰ基本コード
                            , daibiki_rec.SHOPCD                       -- ショップコード
                            , userinfo_rec.APKIHONCD                   -- 担当者ＡＰ基本コード
                            , userinfo_rec.SHOPCD                      -- 担当者ショップコード
                            , userinfo_rec.TNSHACD                     -- 担当者コード
                            , userinfo_rec.IPADRSE                     -- ＩＰアドレス
                            , userinfo_rec.SYSTMYMD                    -- 依頼日
                            , userinfo_rec.SYSTMJIKAN                  -- 依頼時間
                            , daibiki_rec.DLVRYJISSEKIDD               -- 処理日
                            , '1'                                      -- 処理区分
                            , 0                                        -- 入金金額
                            , tokuisakiMST.SKYSAKICD                   -- 対象請求先
                            , SUBSTR(daibiki_rec.DLVRYJISSEKIDD, 1, 6) -- 締年月
        );
      END IF;
-- インボイス対応追加 end ABI
    END LOOP;

    EXCEPTION
      WHEN OTHERS THEN
        IF SELECT_DAIBIKI%ISOPEN THEN
           CLOSE SELECT_DAIBIKI;
        END IF;
        IF SELECT_DBKM%ISOPEN THEN
           CLOSE SELECT_DBKM;
        END IF;
        RAISE_APPLICATION_ERROR(-20023, SQLERRM || '代引き売上データ更新時エラーが発生しました。');
  END processDaibiki;

-- 本線対応追加 start ABI
  /*****************************************************************************/
  /* プロシージャ名：代引き更新WEB処理                                         */
  /* 引数（ＩＮ）      ：work_rec        配達実績データヘッダ                  */
  /*                   ：tokuisakiMST    得意先マスタ                          */
  /*                   ：kbnKaishuhoho   回収方法区分                          */
  /*****************************************************************************/
  PROCEDURE processDaibikiWeb( work_rec     IN S05W001T%rowtype -- 配達実績データヘッダ
                             , tokuisakiMST IN S02M013T%rowtype -- 得意先マスタ
-- インボイス対応追加 start ABI
                             , kbnKaishuhoho IN VARCHAR2        -- 回収方法区分
-- インボイス対応追加 end ABI
  ) IS
  /* 変数宣言 */
-- インボイス対応追加 start ABI
  uridnpyno       VARCHAR(11) := NULL; -- 売上伝票番号
  edano           NUMBER      := 0;    -- 枝番号
  uridnpymeisaino NUMBER      := 0;    -- 売上伝票明細番号
-- インボイス対応追加 end ABI
  /* CURSOR：受注配達テーブル */
  CURSOR SELECT_DAIBIKI IS
    SELECT JHTT.*
      FROM S04T001T JHTT
     WHERE JHTT.APKIHONCD  = work_rec.APKIHONCD
       AND JHTT.SHOPCD     = work_rec.SHOPCD
       AND JHTT.ACPTDNPYNO = work_rec.ACPTDNPYNO
       AND JHTT.EDANO      = work_rec.EDANO
-- インボイス対応変更 start ABI
       --AND JHTT.ACPTTHDAIBIKIFLG = '1' -- 受注時代引フラグが"1"
       AND (JHTT.ACPTTHDAIBIKIFLG = '1' -- 受注時代引フラグが"1"
            OR JHTT.SINGLDNPYFLG = '1') -- 単体伝票出力フラグが"1"
-- インボイス対応変更 end ABI
       AND JHTT.YUKOKBN = '1'
  ;
-- インボイス対応変更 start ABI
  --/* CURSOR：売上テーブル */
  --CURSOR SELECT_DBKM IS
    --SELECT JHMT.*
    --     , URIT.URIDD
    --  FROM S04T002T JHMT
    -- INNER JOIN S06T001T URIT
    --    ON ( URIT.APKIHONCD = JHMT.APKIHONCD
    --       AND URIT.SHOPCD = JHMT.SHOPCD
    --       AND URIT.URIDNPYNO = JHMT.URIDNPYNO
    --       AND URIT.EDANO = JHMT.URIDNPYEDANO
    --    )
    -- WHERE JHMT.APKIHONCD  = work_rec.APKIHONCD
    --   AND JHMT.SHOPCD     = work_rec.SHOPCD
    --   AND JHMT.ACPTDNPYNO = work_rec.ACPTDNPYNO
    --   AND JHMT.EDANO      = work_rec.EDANO
    --   AND JHMT.MEISAIKBN = '2' -- 明細区分:2（代引き）
    --   AND JHMT.URIDNPYNO IS NOT NULL
    --   AND JHMT.YUKOKBN = '1'
  /* CURSOR：売上テーブル */
  CURSOR SELECT_DBKM IS
    SELECT URIT.*
      FROM S06T001T URIT
     WHERE URIT.APKIHONCD  = work_rec.APKIHONCD
       AND URIT.SHOPCD     = work_rec.SHOPCD
       AND URIT.ACPTDNPYNO = work_rec.ACPTDNPYNO
       AND URIT.URIDNPYNO <> work_rec.ACPTDNPYNO
       AND URIT.SKYSHIMEDYDM IS NULL
       AND URIT.SKYSHIMEDD IS NULL
       AND URIT.YUKOKBN = '1'
-- インボイス対応変更 end ABI
  ;
  BEGIN
    FOR daibiki_rec IN SELECT_DAIBIKI LOOP
-- インボイス対応削除 start ABI
      ---- 請求残高テーブル：通常分更新
      --UPDATE S07T001T SKZT
      --   SET SKZT.KONKAITAXNUKIURIKIN = SKZT.KONKAITAXNUKIURIKIN + daibiki_rec.NOHINKIN
      --     , SKZT.KONKAISHOHITAXKIN = SKZT.KONKAISHOHITAXKIN + daibiki_rec.NOHINSHOHITAXKIN
      --     , SKZT.KESHIKOMIZD = SKZT.KESHIKOMIZD + daibiki_rec.NOHINKIN + daibiki_rec.NOHINSHOHITAXKIN
      --     , SKZT.SKYSHONO = daibiki_rec.ACPTDNPYNO
      --      /* 更新者 */
      --     , SKZT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
      --     , SKZT.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
      --     , SKZT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
      --     , SKZT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
      --     , SKZT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
      --     , SKZT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
      --     , SKZT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
      --     , SKZT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
      -- WHERE SKZT.APKIHONCD = daibiki_rec.APKIHONCD
      --   AND SKZT.SHOPCD = daibiki_rec.SHOPCD
      --   AND SKZT.SKYSAKICD = tokuisakiMST.SKYSAKICD
      --   AND SKZT.TOKUISAKICD = daibiki_rec.TOKUISAKICD
      --   AND SKZT.SHIMEDYDM = SUBSTRB(daibiki_rec.DLVRYJISSEKIDD, 1, 6)
      --   AND SKZT.SHIMEDD = SUBSTRB(daibiki_rec.DLVRYJISSEKIDD, 7, 2)
      --   AND SKZT.SKYPRCSJISSIKBN = '2' -- 2：代引き時請求処理
      --;

      ---- 更新対象がない場合は登録
      --IF SQL%ROWCOUNT = 0 THEN
      -- INSERT INTO S07T001T (
      --        APKIHONCD -- ＡＰ基本コード
      --      , SHOPCD -- ショップコード
      --      , SKYSAKICD -- 請求先コード
      --      , TOKUISAKICD -- 得意先コード
      --      , SHIMEDYDM -- 締年月
      --      , SHIMEDD -- 締日
      --      , SKYPRCSJISSIKBN -- 請求処理実施区分
      --      , SHIMEPRCSJISSIDD -- 締処理実施日
      --      , ZNKSHIMEDD -- 前回締日
      --      , KONKAISHIMEDD -- 今回締日
      --      , ZNKKURIKOSHIKIN -- 前回繰越金額
      --      , KONKAITAXNUKIURIKIN -- 今回税抜売上金額
      --      , KONKAISHOHITAXKIN -- 今回消費税金額
      --      , KONKAINYKNKIN -- 今回入金金額
      --      , KURIKOSHIKIN -- 繰越金額
      --      , KESHIKOMIKIN -- 消込金額
      --      , KESHIKOMIZD -- 消込残高
      --      , SKYICRNHYPRNTDD -- 請求一覧表印刷日
      --      , SKYSHOPRNTDD -- 請求書印刷日
      --      , SKYSHONO -- 請求書番号
      --      , SHOKAISKYSHONO -- 初回請求書番号
      --      , RIYOMEISAISHOPRNTDD -- 利用明細書印刷日
      --      , RIYOMEISAISHONO -- 利用明細書番号
      --      , TOKUSOKUJOPRNTDD -- 督促状印刷日
      --      , TOKUSOKUJONO -- 督促状番号
      --      , CSVDATCHUSYUTSUDD -- ＣＳＶデータ抽出日
      --      , RGRAYURYO -- レギュラー有料
      --      , RGRAMURYO -- レギュラー無料
      --      , SRMYURYO -- スリム有料
      --      , SRMMURYO -- スリム無料
      --      , SONOTAYURYO -- その他有料
      --      , SONOTAMURYO -- その他無料
      --      , SEIHINMIZUURI -- 製品水売上
      --      , RTLRKN -- レンタル料金
      --      , SONOTAURIKIN -- その他売上金額
      --      , AQCRGRAKYNPNSURYO -- アクアクララレギュラーキャンペーン数量
      --      , AQCRGRASVSURYO -- アクアクララレギュラーサービス数量
      --      , AQCRGRAPUROMOSHONSURYO -- アクアクララレギュラープロモーション数量
      --      , AQCRGRASIINSURYO -- アクアクララレギュラー試飲数量
      --      , AQCRGRAMNTESURYO -- アクアクララレギュラーメンテ数量
      --      , AQCRGRAKOKANSURYO -- アクアクララレギュラー交換数量
      --      , AQCRGRAURISURYO -- アクアクララレギュラー売上数量
      --      , AQCSRMKYNPNSURYO -- アクアクララスリムキャンペーン数量
      --      , AQCSRMSVSURYO -- アクアクララスリムサービス数量
      --      , AQCSRMPUROMOSHONSURYO -- アクアクララスリムプロモーション数量
      --      , AQCSRMSIINSURYO -- アクアクララスリム試飲数量
      --      , AQCSRMMNTESURYO -- アクアクララスリムメンテ数量
      --      , AQCSRMKOKANSURYO -- アクアクララスリム交換数量
      --      , AQCSRMURISURYO -- アクアクララスリム売上数量
      --      , ETCSHNMZKYNPNSURYO -- その他製品水キャンペーン数量
      --      , ETCSHNMZSVSURYO -- その他製品水サービス数量
      --      , ETCSHNMZPUROMOSHONSURYO -- その他製品水プロモーション数量
      --      , ETCSHNMZSIINSURYO -- その他製品水試飲数量
      --      , ETCSHNMZMNTESURYO -- その他製品水メンテ数量
      --      , ETCSHNMZKOKANSURYO -- その他製品水交換数量
      --      , ETCSHNMZURISURYO -- その他製品水売上数量
      --      , SHNMZRGRAKIN -- 製品水レギュラー金額
      --      , SHNMZSRMKIN -- 製品水スリム金額
      --      , SHNMZSONOTAKIN -- 製品水その他金額
      --      , RNTLRWSVKIN -- レンタル料サーバー金額
      --      , RNTLRHKSHNKIN -- レンタル料他商品金額
      --      , SNTSHNWSVKIN -- その他商品サーバー金額
      --      , SNTSHNHKSHNKIN -- その他商品他商品金額
      --      , SNTSHNKNPHNKIN -- その他商品キャンペーン品金額
      --      , KAISHUHOHOKBN -- 回収方法区分
      --      , SHRYTIDYDMDD -- 支払予定年月日
      --      , KSISHIMEDYDMDD -- 決済締年月日
      --      , KSIKKNCD -- 決済機関コード
      --      , YUKOKBN -- 有効区分
      --       /* 更新者 */
      --      , TOSHAAPKIHONCD -- 登録者ＡＰ基本コード
      --      , TOSHASHOPCD -- 登録者ショップコード
      --      , TOSHACD -- 登録者コード
      --      , INPTSYSTMYMD -- 登録システム日付
      --      , INPTSYSTMJIKAN -- 登録システム時間
      --      , INPTUNYOYMD -- 登録運用日付
      --      , INPTIPADRSE -- 登録ＩＰアドレス
      --      , INPTPGMID -- 登録プログラムＩＤ
      --      , KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
      --      , KOSHASHOPCD -- 更新者ショップコード
      --      , KOSHACD -- 更新者コード
      --      , KSHNSYSTMYMD -- 更新システム日付
      --      , KSHNSYSTMJIKAN -- 更新システム時間
      --      , KSHNUNYOYMD -- 更新運用日付
      --      , KSHNIPADRSE -- 更新ＩＰアドレス
      --      , KSHNPGMID -- 更新プログラムＩＤ
      -- ) VALUES (
      --          daibiki_rec.APKIHONCD -- ＡＰ基本コード
      --        , daibiki_rec.SHOPCD -- ショップコード
      --        , tokuisakiMST.SKYSAKICD -- 請求先コード
      --        , daibiki_rec.TOKUISAKICD -- 得意先コード
      --        , SUBSTRB(daibiki_rec.DLVRYJISSEKIDD, 1, 6) -- 締年月
      --        , SUBSTRB(daibiki_rec.DLVRYJISSEKIDD, 7, 2) -- 締日
      --        , '2' -- 請求処理実施区分：2（代引き時請求処理
      --        , userinfo_rec.UNYOYMD -- 締処理実施日
      --        , '00000000' -- 前回締日
      --        , daibiki_rec.DLVRYJISSEKIDD -- 今回締日
      --        , 0 -- 前回繰越金額
      --        , daibiki_rec.NOHINKIN -- 今回税抜売上金額
      --        , daibiki_rec.NOHINSHOHITAXKIN -- 今回消費税金額
      --        , 0 -- 今回入金金額
      --        , 0 -- 繰越金額
      --        , 0 -- 消込金額
      --        , daibiki_rec.NOHINKIN + daibiki_rec.NOHINSHOHITAXKIN -- 消込残高
      --        , '00000000' -- 請求一覧表印刷日
      --        , '00000000' -- 請求書印刷日
      --        , daibiki_rec.ACPTDNPYNO -- 請求書番号
      --        , daibiki_rec.ACPTDNPYNO -- 初回請求書番号
      --        , '00000000' -- 利用明細書印刷日
      --        , '000000000000' -- 利用明細書番号
      --        , '00000000' -- 督促状印刷日
      --        , '000000000000' -- 督促状番号
      --        , '00000000' -- ＣＳＶデータ抽出日
      --        , 0 -- レギュラー有料
      --        , 0 -- レギュラー無料
      --        , 0 -- スリム有料
      --        , 0 -- スリム無料
      --        , 0 -- その他有料
      --        , 0 -- その他無料
      --        , 0 -- 製品水売上
      --        , 0 -- レンタル料金
      --        , 0 -- その他売上金額
      --        , 0 -- アクアクララレギュラーキャンペーン数量
      --        , 0 -- アクアクララレギュラーサービス数量
      --        , 0 -- アクアクララレギュラープロモーション数量
      --        , 0 -- アクアクララレギュラー試飲数量
      --        , 0 -- アクアクララレギュラーメンテ数量
      --        , 0 -- アクアクララレギュラー交換数量
      --        , 0 -- アクアクララレギュラー売上数量
      --        , 0 -- アクアクララスリムキャンペーン数量
      --        , 0 -- アクアクララスリムサービス数量
      --        , 0 -- アクアクララスリムプロモーション数量
      --        , 0 -- アクアクララスリム試飲数量
      --        , 0 -- アクアクララスリムメンテ数量
      --        , 0 -- アクアクララスリム交換数量
      --        , 0 -- アクアクララスリム売上数量
      --        , 0 -- その他製品水キャンペーン数量
      --        , 0 -- その他製品水サービス数量
      --        , 0 -- その他製品水プロモーション数量
      --        , 0 -- その他製品水試飲数量
      --        , 0 -- その他製品水メンテ数量
      --        , 0 -- その他製品水交換数量
      --        , 0 -- その他製品水売上数量
      --        , 0 -- 製品水レギュラー金額
      --        , 0 -- 製品水スリム金額
      --        , 0 -- 製品水その他金額
      --        , 0 -- レンタル料サーバー金額
      --        , 0 -- レンタル料他商品金額
      --        , 0 -- その他商品サーバー金額
      --        , 0 -- その他商品他商品金額
      --        , 0 -- その他商品キャンペーン品金額
      --        , '7' -- 回収方法区分：7（代引き）
      --        , NULL -- 支払予定年月日
      --        , '00000000' -- 決済締年月日
      --        , NULL -- 決済機関コード
      --        , '1' -- 有効区分
      --          /* 更新者 */
      --        , userinfo_rec.APKIHONCD  -- 登録者ＡＰ基本コード
      --        , userinfo_rec.SHOPCD     -- 登録者ショップコード
      --        , userinfo_rec.TNSHACD    -- 登録者コード
      --        , userinfo_rec.SYSTMYMD   -- 登録システム日付
      --        , userinfo_rec.SYSTMJIKAN -- 登録システム時間
      --        , userinfo_rec.UNYOYMD    -- 登録運用日付
      --        , userinfo_rec.IPADRSE    -- 登録ＩＰアドレス
      --        , userinfo_rec.PGMID      -- 登録プログラムＩＤ
      --        , userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
      --        , userinfo_rec.SHOPCD     -- 更新者ショップコード
      --        , userinfo_rec.TNSHACD    -- 更新者コード
      --        , userinfo_rec.SYSTMYMD   -- 更新システム日付
      --        , userinfo_rec.SYSTMJIKAN -- 更新システム時間
      --        , userinfo_rec.UNYOYMD    -- 更新運用日付
      --        , userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
      --        , userinfo_rec.PGMID      -- 更新プログラムＩＤ
      -- );
      --END IF;
-- インボイス対応削除 end ABI

-- インボイス対応追加 start ABI
      -- 変数の定義
      -- 売上伝票番号（黒）が設定されている場合、売上伝票番号に売上伝票番号（黒）を設定する
      IF cmnKurouridnpyno IS NOT NULL THEN
        uridnpyno       := cmnKurouridnpyno;
        edano           := 0;
      ELSE
        uridnpyno       := daibiki_rec.ACPTDNPYNO;
        edano           := daibiki_rec.EDANO;
      END IF;
      SELECT NVL(MAX(ACPTDNPYMEISAINO),0)
        INTO uridnpymeisaino
        FROM S04T002T
       WHERE APKIHONCD  = daibiki_rec.APKIHONCD
         AND SHOPCD     = daibiki_rec.SHOPCD
         AND ACPTDNPYNO = daibiki_rec.ACPTDNPYNO
         AND EDANO      = daibiki_rec.EDANO
         AND YUKOKBN    = '1';
-- インボイス対応追加 end ABI
      FOR daibikimei_rec IN SELECT_DBKM LOOP
        -- 売上テーブル更新
        UPDATE S06T001T DAIBIKI_URI
           SET URISHIMEPRCSDD = daibiki_rec.DLVRYDD -- 売上締実施日
         WHERE DAIBIKI_URI.APKIHONCD = daibikimei_rec.APKIHONCD
           AND DAIBIKI_URI.SHOPCD = daibikimei_rec.SHOPCD
           AND DAIBIKI_URI.URIDNPYNO = daibikimei_rec.URIDNPYNO
        ;

-- インボイス対応削除 start ABI
        ---- 請求残高テーブル：代引き分更新
        --UPDATE S07T001T SKZT
        --   SET SKZT.KONKAITAXNUKIURIKIN = SKZT.KONKAITAXNUKIURIKIN + daibikimei_rec.NOHINKIN
        --     , SKZT.KONKAISHOHITAXKIN = SKZT.KONKAISHOHITAXKIN + daibikimei_rec.NOHINSHOHITAXKIN
        --     , SKZT.KESHIKOMIZD = SKZT.KESHIKOMIZD + daibikimei_rec.NOHINKIN + daibikimei_rec.NOHINSHOHITAXKIN
        --      /* 更新者 */
        --     , SKZT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
        --     , SKZT.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
        --     , SKZT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
        --     , SKZT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
        --     , SKZT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
        --     , SKZT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
        --     , SKZT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
        --     , SKZT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
        -- WHERE SKZT.APKIHONCD = daibiki_rec.APKIHONCD
        --   AND SKZT.SHOPCD = daibiki_rec.SHOPCD
        --   AND SKZT.SKYSAKICD = tokuisakiMST.SKYSAKICD
        --   AND SKZT.TOKUISAKICD = daibiki_rec.TOKUISAKICD
        --   AND SKZT.SHIMEDYDM = SUBSTRB(daibikimei_rec.URIDD, 1, 6)
        --   AND SKZT.SHIMEDD = SUBSTRB(daibikimei_rec.URIDD, 7, 2)
        --   AND SKZT.SKYPRCSJISSIKBN = '2' -- 2：代引き時請求処理
        --;
        ---- 更新対象がない場合は登録
        --IF SQL%ROWCOUNT = 0 THEN
        -- INSERT INTO S07T001T (
        --        APKIHONCD -- ＡＰ基本コード
        --      , SHOPCD -- ショップコード
        --      , SKYSAKICD -- 請求先コード
        --      , TOKUISAKICD -- 得意先コード
        --      , SHIMEDYDM -- 締年月
        --      , SHIMEDD -- 締日
        --      , SKYPRCSJISSIKBN -- 請求処理実施区分
        --      , SHIMEPRCSJISSIDD -- 締処理実施日
        --      , ZNKSHIMEDD -- 前回締日
        --      , KONKAISHIMEDD -- 今回締日
        --      , ZNKKURIKOSHIKIN -- 前回繰越金額
        --      , KONKAITAXNUKIURIKIN -- 今回税抜売上金額
        --      , KONKAISHOHITAXKIN -- 今回消費税金額
        --      , KONKAINYKNKIN -- 今回入金金額
        --      , KURIKOSHIKIN -- 繰越金額
        --      , KESHIKOMIKIN -- 消込金額
        --      , KESHIKOMIZD -- 消込残高
        --      , SKYICRNHYPRNTDD -- 請求一覧表印刷日
        --      , SKYSHOPRNTDD -- 請求書印刷日
        --      , SKYSHONO -- 請求書番号
        --      , SHOKAISKYSHONO -- 初回請求書番号
        --      , RIYOMEISAISHOPRNTDD -- 利用明細書印刷日
        --      , RIYOMEISAISHONO -- 利用明細書番号
        --      , TOKUSOKUJOPRNTDD -- 督促状印刷日
        --      , TOKUSOKUJONO -- 督促状番号
        --      , CSVDATCHUSYUTSUDD -- ＣＳＶデータ抽出日
        --      , RGRAYURYO -- レギュラー有料
        --      , RGRAMURYO -- レギュラー無料
        --      , SRMYURYO -- スリム有料
        --      , SRMMURYO -- スリム無料
        --      , SONOTAYURYO -- その他有料
        --      , SONOTAMURYO -- その他無料
        --      , SEIHINMIZUURI -- 製品水売上
        --      , RTLRKN -- レンタル料金
        --      , SONOTAURIKIN -- その他売上金額
        --      , AQCRGRAKYNPNSURYO -- アクアクララレギュラーキャンペーン数量
        --      , AQCRGRASVSURYO -- アクアクララレギュラーサービス数量
        --      , AQCRGRAPUROMOSHONSURYO -- アクアクララレギュラープロモーション数量
        --      , AQCRGRASIINSURYO -- アクアクララレギュラー試飲数量
        --      , AQCRGRAMNTESURYO -- アクアクララレギュラーメンテ数量
        --      , AQCRGRAKOKANSURYO -- アクアクララレギュラー交換数量
        --      , AQCRGRAURISURYO -- アクアクララレギュラー売上数量
        --      , AQCSRMKYNPNSURYO -- アクアクララスリムキャンペーン数量
        --      , AQCSRMSVSURYO -- アクアクララスリムサービス数量
        --      , AQCSRMPUROMOSHONSURYO -- アクアクララスリムプロモーション数量
        --      , AQCSRMSIINSURYO -- アクアクララスリム試飲数量
        --      , AQCSRMMNTESURYO -- アクアクララスリムメンテ数量
        --      , AQCSRMKOKANSURYO -- アクアクララスリム交換数量
        --      , AQCSRMURISURYO -- アクアクララスリム売上数量
        --      , ETCSHNMZKYNPNSURYO -- その他製品水キャンペーン数量
        --      , ETCSHNMZSVSURYO -- その他製品水サービス数量
        --      , ETCSHNMZPUROMOSHONSURYO -- その他製品水プロモーション数量
        --      , ETCSHNMZSIINSURYO -- その他製品水試飲数量
        --      , ETCSHNMZMNTESURYO -- その他製品水メンテ数量
        --      , ETCSHNMZKOKANSURYO -- その他製品水交換数量
        --      , ETCSHNMZURISURYO -- その他製品水売上数量
        --      , SHNMZRGRAKIN -- 製品水レギュラー金額
        --      , SHNMZSRMKIN -- 製品水スリム金額
        --      , SHNMZSONOTAKIN -- 製品水その他金額
        --      , RNTLRWSVKIN -- レンタル料サーバー金額
        --      , RNTLRHKSHNKIN -- レンタル料他商品金額
        --      , SNTSHNWSVKIN -- その他商品サーバー金額
        --      , SNTSHNHKSHNKIN -- その他商品他商品金額
        --      , SNTSHNKNPHNKIN -- その他商品キャンペーン品金額
        --      , KAISHUHOHOKBN -- 回収方法区分
        --      , SHRYTIDYDMDD -- 支払予定年月日
        --      , KSISHIMEDYDMDD -- 決済締年月日
        --      , KSIKKNCD -- 決済機関コード
        --      , YUKOKBN -- 有効区分
        --      , TOSHAAPKIHONCD -- 登録者ＡＰ基本コード
        --      , TOSHASHOPCD -- 登録者ショップコード
        --      , TOSHACD -- 登録者コード
        --      , INPTSYSTMYMD -- 登録システム日付
        --      , INPTSYSTMJIKAN -- 登録システム時間
        --      , INPTUNYOYMD -- 登録運用日付
        --      , INPTIPADRSE -- 登録ＩＰアドレス
        --      , INPTPGMID -- 登録プログラムＩＤ
        --      , KOSHAAPKIHONCD -- 更新者ＡＰ基本コード
        --      , KOSHASHOPCD -- 更新者ショップコード
        --      , KOSHACD -- 更新者コード
        --      , KSHNSYSTMYMD -- 更新システム日付
        --      , KSHNSYSTMJIKAN -- 更新システム時間
        --      , KSHNUNYOYMD -- 更新運用日付
        --      , KSHNIPADRSE -- 更新ＩＰアドレス
        --      , KSHNPGMID -- 更新プログラムＩＤ
        -- ) VALUES (
        --          daibiki_rec.APKIHONCD -- ＡＰ基本コード
        --        , daibiki_rec.SHOPCD -- ショップコード
        --        , tokuisakiMST.SKYSAKICD -- 請求先コード
        --        , daibiki_rec.TOKUISAKICD -- 得意先コード
        --        , SUBSTRB(daibikimei_rec.URIDD, 1, 6) -- 締年月
        --        , SUBSTRB(daibikimei_rec.URIDD, 7, 2) -- 締日
        --        , '2' -- 請求処理実施区分：2：代引き時請求処理
        --        , userinfo_rec.UNYOYMD -- 締処理実施日
        --        , '00000000' -- 前回締日
        --        , daibikimei_rec.URIDD -- 今回締日
        --        , 0 -- 前回繰越金額
        --        , daibikimei_rec.NOHINKIN -- 今回税抜売上金額
        --        , daibikimei_rec.NOHINSHOHITAXKIN -- 今回消費税金額
        --        , 0 -- 今回入金金額
        --        , 0 -- 繰越金額
        --        , 0 -- 消込金額
        --        , daibikimei_rec.NOHINKIN + daibikimei_rec.NOHINSHOHITAXKIN -- 消込残高
        --        , '00000000' -- 請求一覧表印刷日
        --        , '00000000' -- 請求書印刷日
        --        , daibikimei_rec.ACPTDNPYNO -- 請求書番号
        --        , daibikimei_rec.URIDNPYNO -- 初回請求書番号
        --        , '00000000' -- 利用明細書印刷日
        --        , '000000000000' -- 利用明細書番号
        --        , '00000000' -- 督促状印刷日
        --        , '000000000000' -- 督促状番号
        --        , '00000000' -- ＣＳＶデータ抽出日
        --        , 0 -- レギュラー有料
        --        , 0 -- レギュラー無料
        --        , 0 -- スリム有料
        --        , 0 -- スリム無料
        --        , 0 -- その他有料
        --        , 0 -- その他無料
        --        , 0 -- 製品水売上
        --        , 0 -- レンタル料金
        --        , 0 -- その他売上金額
        --        , 0 -- アクアクララレギュラーキャンペーン数量
        --        , 0 -- アクアクララレギュラーサービス数量
        --        , 0 -- アクアクララレギュラープロモーション数量
        --        , 0 -- アクアクララレギュラー試飲数量
        --        , 0 -- アクアクララレギュラーメンテ数量
        --        , 0 -- アクアクララレギュラー交換数量
        --        , 0 -- アクアクララレギュラー売上数量
        --        , 0 -- アクアクララスリムキャンペーン数量
        --        , 0 -- アクアクララスリムサービス数量
        --        , 0 -- アクアクララスリムプロモーション数量
        --        , 0 -- アクアクララスリム試飲数量
        --        , 0 -- アクアクララスリムメンテ数量
        --        , 0 -- アクアクララスリム交換数量
        --        , 0 -- アクアクララスリム売上数量
        --        , 0 -- その他製品水キャンペーン数量
        --        , 0 -- その他製品水サービス数量
        --        , 0 -- その他製品水プロモーション数量
        --        , 0 -- その他製品水試飲数量
        --        , 0 -- その他製品水メンテ数量
        --        , 0 -- その他製品水交換数量
        --        , 0 -- その他製品水売上数量
        --        , 0 -- 製品水レギュラー金額
        --        , 0 -- 製品水スリム金額
        --        , 0 -- 製品水その他金額
        --        , 0 -- レンタル料サーバー金額
        --        , 0 -- レンタル料他商品金額
        --        , 0 -- その他商品サーバー金額
        --        , 0 -- その他商品他商品金額
        --        , 0 -- その他商品キャンペーン品金額
        --        , '7' -- 回収方法区分：7：代引き
        --        , NULL -- 支払予定年月日
        --        , '00000000' -- 決済締年月日
        --        , NULL -- 決済機関コード
        --        , '1' -- 有効区分
        --        , userinfo_rec.APKIHONCD  -- 登録者ＡＰ基本コード
        --        , userinfo_rec.SHOPCD     -- 登録者ショップコード
        --        , userinfo_rec.TNSHACD    -- 登録者コード
        --        , userinfo_rec.SYSTMYMD   -- 登録システム日付
        --        , userinfo_rec.SYSTMJIKAN -- 登録システム時間
        --        , userinfo_rec.UNYOYMD    -- 登録運用日付
        --        , userinfo_rec.IPADRSE    -- 登録ＩＰアドレス
        --        , userinfo_rec.PGMID      -- 登録プログラムＩＤ
        --        , userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
        --        , userinfo_rec.SHOPCD     -- 更新者ショップコード
        --        , userinfo_rec.TNSHACD    -- 更新者コード
        --        , userinfo_rec.SYSTMYMD   -- 更新システム日付
        --        , userinfo_rec.SYSTMJIKAN -- 更新システム時間
        --        , userinfo_rec.UNYOYMD    -- 更新運用日付
        --        , userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
        --        , userinfo_rec.PGMID      -- 更新プログラムＩＤ
        -- );
        --END IF;
-- インボイス対応削除 end ABI
      END LOOP;

-- インボイス対応追加 start ABI
      -- 税区分ごとの消費税金額を売上明細テーブルに登録
      -- 締め後一般消費税金額が0を超過している場合、売上明細テーブルに一般消費税を登録する
      IF NVL(cmnShimeippantaxkin, 0) > 0 THEN
        -- 売上伝票明細番号をカウントアップ
        uridnpymeisaino := uridnpymeisaino + 1;
        -- 売上明細テーブルを登録
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
             , TOKUISAKIKBN       -- 得意先区分
             , JUTAKUDLVRYCSTKBN  -- 受託配達顧客区分
             , YUKOKBN            -- 有効区分
             , TOSHAAPKIHONCD     -- 登録者ＡＰ基本コード
             , TOSHASHOPCD        -- 登録者ショップコード
             , TOSHACD            -- 登録者コード
             , INPTSYSTMYMD       -- 登録システム日付
             , INPTSYSTMJIKAN     -- 登録システム時間
             , INPTUNYOYMD        -- 登録運用日付
             , INPTIPADRSE        -- 登録ＩＰアドレス
             , INPTPGMID          -- 登録プログラムＩＤ
             , KOSHAAPKIHONCD     -- 更新者ＡＰ基本コード
             , KOSHASHOPCD        -- 更新者ショップコード
             , KOSHACD            -- 更新者コード
             , KSHNSYSTMYMD       -- 更新システム日付
             , KSHNSYSTMJIKAN     -- 更新システム時間
             , KSHNUNYOYMD        -- 更新運用日付
             , KSHNIPADRSE        -- 更新ＩＰアドレス
             , KSHNPGMID          -- 更新プログラムＩＤ
        )
        VALUES (
               daibiki_rec.APKIHONCD        -- ＡＰ基本コード
             , daibiki_rec.SHOPCD           -- ショップコード
             , uridnpyno                    -- 売上伝票番号
             , edano                        -- 枝番号
             , uridnpymeisaino              -- 売上伝票明細番号
             , 0                            -- 売上伝票明細枝番号
             , '99'                         -- 大分類コード
             , '001'                        -- 中分類コード
             , '001'                        -- 小分類コード
             , '099901'                     -- 商品コード
             , '01'                         -- 計上区分
             , 1                            -- 数量
             , 0                            -- 標準単価
             , 0                            -- 単価
             , 0                            -- 金額
             , daibiki_rec.NOHINIPPANTAXKIN -- 単価消費税金額
             , daibiki_rec.NOHINIPPANTAXKIN -- 消費税金額
             , 0                            -- 参考消費税金額
             , '1'                          -- 税区分
             , '0'                          -- 明細区分
             , '1'                          -- 入力明細フラグ
             , '1'                          -- 単品明細フラグ
             , NULL                         -- 明細備考
             , NULL                         -- セット商品コード
             , NULL                         -- 型式
             , NULL                         -- 仕入数量
             , NULL                         -- 仕入金額
             , NULL                         -- 仕入単価
             , NULL                         -- 仕入消費税金額
             , NULL                         -- 在庫更新済フラグ
             , tokuisakiMST.TOKUISAKIKBN    -- 得意先区分
             , CASE WHEN tokuisakiMST.TOKUISAKIKBN = '1'
                AND tokuisakiMST.JUTAKUDLVRYCSTKBN = '4'
                AND daibiki_rec.ITAKUAITEACPTDNPYNO IS NULL THEN
                 '1'
               ELSE
                 tokuisakiMST.JUTAKUDLVRYCSTKBN
               END                          -- 受託配達顧客区分
             , '1'                          -- 有効区分
               /* 更新者 */
             , userinfo_rec.APKIHONCD       -- 登録者ＡＰ基本コード
             , userinfo_rec.SHOPCD          -- 登録者ショップコード
             , userinfo_rec.TNSHACD         -- 登録者コード
             , userinfo_rec.SYSTMYMD        -- 登録システム日付
             , userinfo_rec.SYSTMJIKAN      -- 登録システム時間
             , userinfo_rec.UNYOYMD         -- 登録運用日付
             , userinfo_rec.IPADRSE         -- 登録ＩＰアドレス
             , userinfo_rec.PGMID           -- 登録プログラムＩＤ
             , userinfo_rec.APKIHONCD       -- 更新者ＡＰ基本コード
             , userinfo_rec.SHOPCD          -- 更新者ショップコード
             , userinfo_rec.TNSHACD         -- 更新者コード
             , userinfo_rec.SYSTMYMD        -- 更新システム日付
             , userinfo_rec.SYSTMJIKAN      -- 更新システム時間
             , userinfo_rec.UNYOYMD         -- 更新運用日付
             , userinfo_rec.IPADRSE         -- 更新ＩＰアドレス
             , userinfo_rec.PGMID           -- 更新プログラムＩＤ
        );
      END IF;
      -- 締め後軽減消費税金額が0を超過している場合、売上明細テーブルに軽減消費税を登録する
      IF NVL(cmnShimekeigentaxkin, 0) > 0 THEN
        -- 売上伝票明細番号をカウントアップ
        uridnpymeisaino := uridnpymeisaino + 1;
        -- 売上明細テーブルを登録
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
             , TOKUISAKIKBN       -- 得意先区分
             , JUTAKUDLVRYCSTKBN  -- 受託配達顧客区分
             , YUKOKBN            -- 有効区分
             , TOSHAAPKIHONCD     -- 登録者ＡＰ基本コード
             , TOSHASHOPCD        -- 登録者ショップコード
             , TOSHACD            -- 登録者コード
             , INPTSYSTMYMD       -- 登録システム日付
             , INPTSYSTMJIKAN     -- 登録システム時間
             , INPTUNYOYMD        -- 登録運用日付
             , INPTIPADRSE        -- 登録ＩＰアドレス
             , INPTPGMID          -- 登録プログラムＩＤ
             , KOSHAAPKIHONCD     -- 更新者ＡＰ基本コード
             , KOSHASHOPCD        -- 更新者ショップコード
             , KOSHACD            -- 更新者コード
             , KSHNSYSTMYMD       -- 更新システム日付
             , KSHNSYSTMJIKAN     -- 更新システム時間
             , KSHNUNYOYMD        -- 更新運用日付
             , KSHNIPADRSE        -- 更新ＩＰアドレス
             , KSHNPGMID          -- 更新プログラムＩＤ
        )
        VALUES (
               daibiki_rec.APKIHONCD        -- ＡＰ基本コード
             , daibiki_rec.SHOPCD           -- ショップコード
             , uridnpyno                    -- 売上伝票番号
             , edano                        -- 枝番号
             , uridnpymeisaino              -- 売上伝票明細番号
             , 0                            -- 売上伝票明細枝番号
             , '99'                         -- 大分類コード
             , '001'                        -- 中分類コード
             , '002'                        -- 小分類コード
             , '099902'                     -- 商品コード
             , '01'                         -- 計上区分
             , 1                            -- 数量
             , 0                            -- 標準単価
             , 0                            -- 単価
             , 0                            -- 金額
             , daibiki_rec.NOHINKEIGENTAXKIN -- 単価消費税金額
             , daibiki_rec.NOHINKEIGENTAXKIN -- 消費税金額
             , 0                            -- 参考消費税金額
             , '4'                          -- 税区分
             , '0'                          -- 明細区分
             , '1'                          -- 入力明細フラグ
             , '1'                          -- 単品明細フラグ
             , NULL                         -- 明細備考
             , NULL                         -- セット商品コード
             , NULL                         -- 型式
             , NULL                         -- 仕入数量
             , NULL                         -- 仕入金額
             , NULL                         -- 仕入単価
             , NULL                         -- 仕入消費税金額
             , NULL                         -- 在庫更新済フラグ
             , tokuisakiMST.TOKUISAKIKBN    -- 得意先区分
             , CASE WHEN tokuisakiMST.TOKUISAKIKBN = '1'
                AND tokuisakiMST.JUTAKUDLVRYCSTKBN = '4'
                AND daibiki_rec.ITAKUAITEACPTDNPYNO IS NULL THEN
                 '1'
               ELSE
                 tokuisakiMST.JUTAKUDLVRYCSTKBN
               END                          -- 受託配達顧客区分
             , '1'                          -- 有効区分
               /* 更新者 */
             , userinfo_rec.APKIHONCD       -- 登録者ＡＰ基本コード
             , userinfo_rec.SHOPCD          -- 登録者ショップコード
             , userinfo_rec.TNSHACD         -- 登録者コード
             , userinfo_rec.SYSTMYMD        -- 登録システム日付
             , userinfo_rec.SYSTMJIKAN      -- 登録システム時間
             , userinfo_rec.UNYOYMD         -- 登録運用日付
             , userinfo_rec.IPADRSE         -- 登録ＩＰアドレス
             , userinfo_rec.PGMID           -- 登録プログラムＩＤ
             , userinfo_rec.APKIHONCD       -- 更新者ＡＰ基本コード
             , userinfo_rec.SHOPCD          -- 更新者ショップコード
             , userinfo_rec.TNSHACD         -- 更新者コード
             , userinfo_rec.SYSTMYMD        -- 更新システム日付
             , userinfo_rec.SYSTMJIKAN      -- 更新システム時間
             , userinfo_rec.UNYOYMD         -- 更新運用日付
             , userinfo_rec.IPADRSE         -- 更新ＩＰアドレス
             , userinfo_rec.PGMID           -- 更新プログラムＩＤ
        );
      END IF;

      -- 売上テーブルを更新する
      UPDATE S06T001T URIT
         SET URIT.SHOHITAXKIN = daibiki_rec.NOHINSHOHITAXKIN
            /* 更新者 */
           , URIT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
           , URIT.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
           , URIT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
           , URIT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
           , URIT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
           , URIT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
           , URIT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
           , URIT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
       WHERE URIT.APKIHONCD = daibiki_rec.APKIHONCD
         AND URIT.SHOPCD = daibiki_rec.SHOPCD
         AND URIT.URIDNPYNO = daibiki_rec.URIDNPYNO
      ;

      -- 請求締処理を行う
      D1502B01.SeikyuShimeTantai(daibiki_rec.APKIHONCD      -- ＡＰ基本コード
                               , daibiki_rec.SHOPCD         -- ショップコード
                               , userinfo_rec.APKIHONCD     -- 担当者ＡＰ基本コード
                               , userinfo_rec.SHOPCD        -- 担当者ショップコード
                               , userinfo_rec.TNSHACD       -- 担当者コード
                               , userinfo_rec.IPADRSE       -- ＩＰアドレス
                               , userinfo_rec.SYSTMYMD      -- 依頼日
                               , userinfo_rec.SYSTMJIKAN    -- 依頼時間
                               , daibiki_rec.DLVRYJISSEKIDD -- 請求締日
                               , '3'                        -- 処理モード
                               , tokuisakiMST.SKYSAKICD     -- 請求先コード
                               , tokuisakiMST.TOKUISAKICD   -- 得意先コード
                               , uridnpyno                  -- 売上伝票番号
                               , kbnKaishuhoho              -- 回収方法区分
                               , daibiki_rec.SINGLDNPYFLG   -- 単体伝票締めフラグ
                                );

      -- 未締売上分の売上データに対しても請求締処理を行う
      FOR mijime_uri IN (SELECT URIT.*
                           FROM S06T001T URIT
                          WHERE URIT.APKIHONCD  = daibiki_rec.APKIHONCD
                            AND URIT.SHOPCD     = daibiki_rec.SHOPCD
                            AND URIT.ACPTDNPYNO = daibiki_rec.ACPTDNPYNO
                            AND URIT.URIDNPYNO  <> URIT.ACPTDNPYNO
                            AND URIT.SKYSHIMEDYDM IS NULL
                            AND URIT.SKYSHIMEDD IS NULL
                            AND URIT.YUKOKBN = '1') LOOP
        -- 赤伝票、黒伝票のデータの場合、スキップ
        IF mijime_uri.URIDNPYNO = uridnpyno OR mijime_uri.URIDNPYNO = cmnAkauridnpyno THEN
          CONTINUE;
        END IF;
        -- 未締売上分の請求締処理を行う
        D1502B01.SeikyuShimeTantai(daibiki_rec.APKIHONCD      -- ＡＰ基本コード
                                 , daibiki_rec.SHOPCD         -- ショップコード
                                 , userinfo_rec.APKIHONCD     -- 担当者ＡＰ基本コード
                                 , userinfo_rec.SHOPCD        -- 担当者ショップコード
                                 , userinfo_rec.TNSHACD       -- 担当者コード
                                 , userinfo_rec.IPADRSE       -- ＩＰアドレス
                                 , userinfo_rec.SYSTMYMD      -- 依頼日
                                 , userinfo_rec.SYSTMJIKAN    -- 依頼時間
                                 , mijime_uri.URIDD           -- 請求締日
                                 , '3'                        -- 処理モード
                                 , tokuisakiMST.SKYSAKICD     -- 請求先コード
                                 , tokuisakiMST.TOKUISAKICD   -- 得意先コード
                                 , mijime_uri.URIDNPYNO       -- 売上伝票番号
                                 , kbnKaishuhoho              -- 回収方法区分
                                 , daibiki_rec.SINGLDNPYFLG   -- 単体伝票締めフラグ
                                  );
      END LOOP;

      -- 代引き（受注時代引きフラグが"1"（代引き）、または単体伝票締めフラグが"1"（単体伝票締めする））の場合、請求締ワークを更新し、滞留更新処理を行う
      IF daibiki_rec.ACPTTHDAIBIKIFLG = '1' OR daibiki_rec.SINGLDNPYFLG = '1' THEN
        -- 滞留更新処理を呼び出す
        D1504B01.Tairyukousin(daibiki_rec.APKIHONCD                    -- ＡＰ基本コード
                            , daibiki_rec.SHOPCD                       -- ショップコード
                            , userinfo_rec.APKIHONCD                   -- 担当者ＡＰ基本コード
                            , userinfo_rec.SHOPCD                      -- 担当者ショップコード
                            , userinfo_rec.TNSHACD                     -- 担当者コード
                            , userinfo_rec.IPADRSE                     -- ＩＰアドレス
                            , userinfo_rec.SYSTMYMD                    -- 依頼日
                            , userinfo_rec.SYSTMJIKAN                  -- 依頼時間
                            , daibiki_rec.DLVRYJISSEKIDD               -- 処理日
                            , '1'                                      -- 処理区分
                            , 0                                        -- 入金金額
                            , tokuisakiMST.SKYSAKICD                   -- 対象請求先
                            , SUBSTR(daibiki_rec.DLVRYJISSEKIDD, 1, 6) -- 締年月
        );
      END IF;
-- インボイス対応追加 end ABI
    END LOOP;

    EXCEPTION
      WHEN OTHERS THEN
        IF SELECT_DAIBIKI%ISOPEN THEN
           CLOSE SELECT_DAIBIKI;
        END IF;
        IF SELECT_DBKM%ISOPEN THEN
           CLOSE SELECT_DBKM;
        END IF;
        RAISE_APPLICATION_ERROR(-20023, SQLERRM || '代引き売上データ更新時エラーが発生しました。');
  END processDaibikiWeb;
-- 本線対応追加 end ABI

  /*****************************************************************************/
  /* プロシージャ名：在庫データ作成処理                                            */
  /* 引数（ＩＮ）      ：work_rec         配達実績データヘッダ                     */
  /*                  ：tokuisakiMST     得意先マスタ                           */
  /*                  ：iShopzaikokrkbn  ショップ在庫管理区分                    */
  /*                  ：iZaikoprcsdydm   ショップ在庫処理年月                     */
  /*                  ：iShopShimedd     ショップ締日                            */
  /*****************************************************************************/
  PROCEDURE processZaiko( work_rec     IN S05W001T%rowtype -- 配達実績データヘッダ
                        , tokuisakiMST IN S02M013T%rowtype -- 得意先マスタ
                        , iShopzaikokrkbn IN VARCHAR2 -- ショップ在庫管理区分
                        , iZaikoprcsdydm  IN VARCHAR2 -- ショップ在庫処理年月
                        , iShopShimedd    IN VARCHAR2 -- ショップ締日
  ) IS
  /* 変数宣言 */
  zaikosimedydm VARCHAR2(6) := NULL; -- 在庫締年月
  zaikooisaiban NUMBER := NULL; -- 在庫入出庫明細テーブル採番
  serverL VARCHAR2(2)  := NULL; -- サーバー大分類
  serverM VARCHAR2(3)  := NULL; -- サーバー中分類
  /* CURSOR：受注配達テーブル */
  CURSOR SELECT_ACPTD IS
    SELECT JHTT.*
         , ( CASE WHEN iShopzaikokrkbn IS NULL OR iShopzaikokrkbn <> '2'
                  THEN DKTM.KYOTENSOKOCD
                  WHEN TNSM.TNSHASHARYOSOKOCD IS NULL
                  THEN DKTM.KYOTENSOKOCD
                  ELSE TNSM.TNSHASHARYOSOKOCD
             END ) GZKT_SOKOCD
      FROM S04T001T JHTT
      LEFT OUTER JOIN S02M031T DKTM -- 配達拠点マスタ
              ON ( DKTM.APKIHONCD  = JHTT.DLVRYKANKATSUAPKIHONCD
                   AND DKTM.SHOPCD = JHTT.DLVRYKANKATSUSHOPCD
                   AND DKTM.DLVRYKYOTENCD = JHTT.DLVRYKYOTENCD
                 )
      LEFT OUTER JOIN S02M003T TNSM -- 担当者マスタ
              ON ( TNSM.APKIHONCD  = JHTT.DLVRYKANKATSUAPKIHONCD
                   AND TNSM.SHOPCD = JHTT.DLVRYKANKATSUSHOPCD
                   AND TNSM.TNSHACD = JHTT.DLVRYTNSHACD
                 )
     WHERE JHTT.APKIHONCD  = work_rec.APKIHONCD
       AND JHTT.SHOPCD     = work_rec.SHOPCD
       AND JHTT.ACPTDNPYNO = work_rec.ACPTDNPYNO
       AND JHTT.EDANO      = work_rec.EDANO
       AND JHTT.YUKOKBN = '1'
  ;
  /* CURSOR：受注配達明細テーブル */
  CURSOR SELECT_ACPTM IS
    SELECT JHMT.*
         , SHMV.ZAIKOKANRITRGTFLG
      FROM S04T002T JHMT
      LEFT OUTER JOIN S02V001T SHMV
              ON ( SHMV.APKIHONCD  = JHMT.APKIHONCD
                   AND SHMV.SHOPCD = JHMT.SHOPCD
                   AND SHMV.SHNCD  = JHMT.SHNCD
                 )
     WHERE JHMT.APKIHONCD  = work_rec.APKIHONCD
       AND JHMT.SHOPCD     = work_rec.SHOPCD
       AND JHMT.ACPTDNPYNO = work_rec.ACPTDNPYNO
       AND JHMT.EDANO      = work_rec.EDANO
       AND JHMT.YUKOKBN = '1'
  ;
  BEGIN
    serverL := getMojisetteiatai( '000', '000', '096', '4', NULL, NULL, '商品大分類コード：サーバー' );
    serverM := getMojisetteiatai( '000', '000', '134', '41', NULL, NULL, '商品中分類コード：サーバー本体' );

    FOR acpt_rec IN SELECT_ACPTD LOOP
    FOR meisai_rec IN SELECT_ACPTM LOOP

        -- 通常分更新
        IF iShopzaikokrkbn IS NOT NULL AND iShopzaikokrkbn <> '0'
           AND meisai_rec.MEISAIKBN = '0'
           AND meisai_rec.TANPINMEISAIFLG = '1' AND meisai_rec.ZAIKOKANRITRGTFLG IS NOT NULL AND meisai_rec.ZAIKOKANRITRGTFLG <> '0'
           AND work_rec.DLVRYJISSEKIDD <= userinfo_rec.UNYOYMD
           AND meisai_rec.NOHINSURYO <> 0
           THEN
           -- 現在庫テーブル
           -- 更新
           UPDATE S13T001T GZKT
              SET GZKT.ZAIKOSURYO = GZKT.ZAIKOSURYO + ( meisai_rec.NOHINSURYO * (-1) ) -- 在庫数量：在庫数量＋納品数量*(-1)
                 /* 更新者 */
                , GZKT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
                , GZKT.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
                , GZKT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
                , GZKT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
                , GZKT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
                , GZKT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
                , GZKT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
                , GZKT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
            WHERE GZKT.APKIHONCD = acpt_rec.DLVRYKANKATSUAPKIHONCD
              AND GZKT.SHOPCD    = acpt_rec.DLVRYKANKATSUSHOPCD
              AND GZKT.SOKOCD    = acpt_rec.GZKT_SOKOCD
              AND GZKT.SHNCD     = meisai_rec.SHNCD
           ;
           -- 更新項目がないの場合：登録
           IF SQL%ROWCOUNT = 0 THEN
              INSERT INTO S13T001T (
                     APKIHONCD  -- AP基本コード
                   , SHOPCD     -- ショップコード
                   , SOKOCD     -- 倉庫コード
                   , SHNCD      -- 商品コード
                   , ZAIKOSURYO -- 在庫数量
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
                     acpt_rec.DLVRYKANKATSUAPKIHONCD   -- AP基本コード
                   , acpt_rec.DLVRYKANKATSUSHOPCD      -- ショップコード
                   , acpt_rec.GZKT_SOKOCD -- 倉庫コード
                   , meisai_rec.SHNCD     -- 商品コード
                   , meisai_rec.NOHINSURYO * (-1) -- 数量
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

           -- 在庫締年月取得
           zaikosimedydm := NULL;
           -- ACCIS2023-7167 在庫年月決定時、運用日ではなく配達実績日を基準とするように修正
           IF SUBSTRB(acpt_rec.DLVRYJISSEKIDD,7,2) > iShopShimedd THEN
              zaikosimedydm := TO_CHAR( ADD_MONTHS( TO_DATE(SUBSTRB(acpt_rec.DLVRYJISSEKIDD, 1, 6),'YYYYMM'), 1), 'YYYYMM');
           ELSE
              zaikosimedydm := SUBSTRB(acpt_rec.DLVRYJISSEKIDD,1,6);
           END IF;
           IF iZaikoprcsdydm >= zaikosimedydm THEN
              zaikosimedydm := iZaikoPrcsDydm; -- 在庫処理年月が新しい場合：締年月は在庫処理年月
           END IF;

           -- 在庫テーブル
           -- 更新
           UPDATE S13T002T ZKT
              SET ZKT.TOTSUKIURIKAZU         = ( CASE WHEN tokuisakiMST.TOKUISAKIKBN <> '9'
                                                      THEN ZKT.TOTSUKIURIKAZU + meisai_rec.NOHINSURYO
                                                      ELSE ZKT.TOTSUKIURIKAZU
                                                 END ) -- 当月売上数
                , ZKT.TOTSUKIKEIHISHUKKOKAZU = ( CASE WHEN tokuisakiMST.TOKUISAKIKBN = '9'
                                                      THEN ZKT.TOTSUKIKEIHISHUKKOKAZU + meisai_rec.NOHINSURYO
                                                      ELSE ZKT.TOTSUKIKEIHISHUKKOKAZU
                                                 END ) -- 当月経費出庫数
                , ZKT.TOTSUKIMATSUZAIKOKAZU = ZKT.TOTSUKIMATSUZAIKOKAZU + meisai_rec.NOHINSURYO * (-1) -- 当月末在庫数
                 /* 更新者 */
                , ZKT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
                , ZKT.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
                , ZKT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
                , ZKT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
                , ZKT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
                , ZKT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
                , ZKT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
                , ZKT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
            WHERE ZKT.APKIHONCD = acpt_rec.DLVRYKANKATSUAPKIHONCD
              AND ZKT.SHOPCD    = acpt_rec.DLVRYKANKATSUSHOPCD
              AND ZKT.SOKOCD    = acpt_rec.GZKT_SOKOCD
              AND ZKT.SHNCD     = meisai_rec.SHNCD
              AND ZKT.SHIMEDYDM = zaikosimedydm
           ;
           -- 更新項目がないの場合：登録
           IF SQL%ROWCOUNT = 0 THEN
              INSERT INTO S13T002T (
                     APKIHONCD  -- AP基本コード
                   , SHOPCD     -- ショップコード
                   , SOKOCD     -- 倉庫コード
                   , SHNCD      -- 商品コード
                   , SHIMEDYDM  -- 締年月
                   , MAEDMMATSUZAIKOKAZU -- 前月末在庫数
                   , TOTSUKISIIREKAZU -- 当月仕入数
                   , TOTSUKISIIREKIN -- 当月仕入金額
                   , TOTSUKISEIZOKAZU -- 当月製造数
                   , TOTSUKICHOSEINYUKOKAZU -- 当月調整入庫数
                   , TOTSUKISONOTANYUKOKAZU    -- 当月その他入庫数
                   , TOTSUKIURIKAZU -- 当月売上数
                   , TOTSUKICHOSEISHUKKOKAZU -- 当月調整出庫数
                   , TOTSUKIKEIHISHUKKOKAZU -- 当月経費出庫数
                   , TOTSUKISONOTASHUKKOKAZU -- 当月その他出庫数
                   , TOTSUKIMATSUZAIKOKAZU -- 当月末在庫数
                   , TOTSUKITNORSSURYO -- 当月棚卸数量
                   , TOTSUKIKFSKSURYO -- 当月過不足数量
                   , ZAIKOUNT -- 在庫単価
                   , SHIMEFLG -- 締フラグ
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
                     acpt_rec.DLVRYKANKATSUAPKIHONCD   -- AP基本コード
                   , acpt_rec.DLVRYKANKATSUSHOPCD      -- ショップコード
                   , acpt_rec.GZKT_SOKOCD -- 倉庫コード
                   , meisai_rec.SHNCD     -- 商品コード
                   , zaikosimedydm  -- 締年月
                   , 0 -- 前月末在庫数
                   , 0 -- 当月仕入数
                   , 0 -- 当月仕入金額
                   , 0 -- 当月製造数
                   , 0 -- 当月調整入庫数
                   , 0    -- 当月その他入庫数
                   , ( CASE WHEN tokuisakiMST.TOKUISAKIKBN <> '9'
                            THEN meisai_rec.NOHINSURYO
                            ELSE 0
                       END ) -- 当月売上数
                   , 0 -- 当月調整出庫数
                   , ( CASE WHEN tokuisakiMST.TOKUISAKIKBN = '9'
                            THEN meisai_rec.NOHINSURYO
                            ELSE 0
                       END ) -- 当月経費出庫数
                   , 0 -- 当月その他出庫数
                   , meisai_rec.NOHINSURYO * (-1) -- 当月末在庫数
                   , NULL -- 当月棚卸数量
                   , NULL -- 当月過不足数量
                   , 0 -- 在庫単価
                   , '0' -- 締フラグ
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

           -- 在庫入出庫明細テーブル
           zaikooisaiban := 0;
           -- 連番の取得
           SELECT COUNT(*)
             INTO zaikooisaiban
             FROM S13T003T ZMT
            WHERE ZMT.APKIHONCD   = acpt_rec.DLVRYKANKATSUAPKIHONCD
              AND ZMT.SHOPCD      = acpt_rec.DLVRYKANKATSUSHOPCD
              AND ZMT.SOKOCD      = acpt_rec.GZKT_SOKOCD
              AND ZMT.SHNCD       = meisai_rec.SHNCD
              AND ZMT.NYUSHUKKODD = acpt_rec.DLVRYJISSEKIDD
              AND ZMT.DNPYNO      = acpt_rec.ACPTDNPYNO
              AND ZMT.DNPYGYONO   = meisai_Rec.ACPTDNPYMEISAINO
           ;
           INSERT INTO S13T003T (
                  APKIHONCD -- AP基本コード
                , SHOPCD    -- ショップコード
                , SOKOCD -- 倉庫コード
                , SHNCD  -- 商品コード
                , SHIMEDYDM   -- 締年月
                , NYUSHUKKODD -- 入出庫日
                , DNPYNO -- 伝票番号
                , EDANO  -- 枝番号
                , DNPYGYONO -- 伝票行番号
                , RENBAN -- 連番
                , DNPYKBN -- 伝票区分
                , NYUSHUKKOKIND -- 入出庫種別
                , NYUSHUKKOKBN  -- 入出庫区分
                , NYUSHUKKOSAKIAPKIHONCD   -- 入出庫先ＡＰ基本コード
                , NYUSHUKKOSAKISHOPCD      -- 入出庫先ショップコード
                , NYUSHUKKOSAKITOKUISAKICD -- 入出庫先得意先コード
                , NYUSHUKKOSAKISOKOCD -- 入出庫先倉庫コード
                , NYUKOMOTOPLNTCD -- 入庫元プラントコード
                , NYUKOMOTOSIIRESAKICD -- 入庫元仕入先コード
                , NYUKOSURYO  -- 入庫数量
                , SHUKKOSURYO -- 出庫数量：数量
                , DATSKSMOTOKBN -- データ作成元区分
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
                    acpt_rec.DLVRYKANKATSUAPKIHONCD   -- AP基本コード
                  , acpt_rec.DLVRYKANKATSUSHOPCD   -- ショップコード
                  , acpt_rec.GZKT_SOKOCD -- 倉庫コード
                  , meisai_rec.SHNCD     -- 商品コード
                  , zaikosimedydm           -- 締年月
                  , acpt_rec.DLVRYJISSEKIDD -- 入出庫日
                  , acpt_rec.ACPTDNPYNO -- 伝票番号
                  , acpt_rec.EDANO      -- 枝番号
                  , meisai_rec.ACPTDNPYMEISAINO -- 伝票行番号
                  , NVL(zaikooisaiban,0) + 1 -- 連番
                  , acpt_rec.DNPYKBN -- 伝票区分
                  , ( CASE WHEN tokuisakiMST.TOKUISAKIKBN = '9' THEN '05' -- 入出庫種別：経費出庫(15)の場合は'05'
                           ELSE '02' -- 入出庫種別：売上出庫(14)の場合は'02'
                      END )
                  , '2' -- 入出庫区分
                  , acpt_rec.APKIHONCD   -- 入出庫先ＡＰ基本コード：AP基本コード
                  , acpt_rec.SHOPCD      -- 入出庫先ショップコード：ショップコード
                  , acpt_rec.TOKUISAKICD -- 入出庫先得意先コード：  得意先コード
                  , NULL -- 入出庫先倉庫コード
                  , NULL -- 入庫元プラントコード
                  , NULL -- 入庫元仕入先コード
                  , 0    -- 入庫数量
                  , meisai_rec.NOHINSURYO -- 出庫数量：数量
                  , DATSKSMOTOKBN_NEW -- データ作成元区分
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

        -- サブレンタル更新
        IF iShopzaikokrkbn IS NOT NULL AND iShopzaikokrkbn <> '0'
           AND meisai_rec.MEISAIKBN = '0'
           AND meisai_rec.TANPINMEISAIFLG = '1' AND meisai_rec.ZAIKOKANRITRGTFLG IS NOT NULL AND meisai_rec.ZAIKOKANRITRGTFLG <> '0'
           AND work_rec.DLVRYJISSEKIDD <= userinfo_rec.UNYOYMD
           -- サーバー商品明細
           AND meisai_rec.LCTGRYCD = serverL AND meisai_rec.MCTGRYCD = serverM
           -- レンタル／サブレンタル
           AND meisai_rec.KEIJOKBN IN ('10', '09')
           -- ショップ／ショップ顧客得意先
           AND ( tokuisakiMST.TOKUISAKIKBN = '2'
                 OR ( tokuisakiMST.TOKUISAKIKBN = '1' AND tokuisakiMST.JUTAKUDLVRYCSTKBN = '2' )
               )
           AND meisai_rec.NOHINSURYO <> 0
           THEN

           -- 現在庫テーブル
           -- 更新
           UPDATE S13T001T GZKT
              SET GZKT.ZAIKOSURYO = GZKT.ZAIKOSURYO + meisai_rec.NOHINSURYO -- 在庫数量：在庫数量＋納品数量*(-1)
                 /* 更新者 */
                , GZKT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
                , GZKT.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
                , GZKT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
                , GZKT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
                , GZKT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
                , GZKT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
                , GZKT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
                , GZKT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
            WHERE GZKT.APKIHONCD = acpt_rec.APKIHONCD
              AND GZKT.SHOPCD    = acpt_rec.SHOPCD
              AND GZKT.SOKOCD    = tokuisakiMST.SABURTLSOKOCD
              AND GZKT.SHNCD     = meisai_rec.SHNCD
           ;
           -- 更新項目がないの場合：登録
           IF SQL%ROWCOUNT = 0 THEN
              INSERT INTO S13T001T (
                     APKIHONCD  -- AP基本コード
                   , SHOPCD     -- ショップコード
                   , SOKOCD     -- 倉庫コード
                   , SHNCD      -- 商品コード
                   , ZAIKOSURYO -- 在庫数量
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
                     acpt_rec.APKIHONCD   -- AP基本コード
                   , acpt_rec.SHOPCD      -- ショップコード
                   , tokuisakiMST.SABURTLSOKOCD -- 倉庫コード
                   , meisai_rec.SHNCD     -- 商品コード
                   , meisai_rec.NOHINSURYO -- 数量
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

           -- 在庫締年月取得
           zaikosimedydm := NULL;
           -- ACCIS2023-7167 在庫年月決定時、運用日ではなく配達実績日を基準とするように修正
           IF SUBSTRB(acpt_rec.DLVRYJISSEKIDD,7,2) > iShopShimedd THEN
              zaikosimedydm := TO_CHAR( ADD_MONTHS( TO_DATE(SUBSTRB(acpt_rec.DLVRYJISSEKIDD, 1, 6),'YYYYMM'), 1), 'YYYYMM' );
           ELSE
              zaikosimedydm := SUBSTRB(acpt_rec.DLVRYJISSEKIDD,1,6);
           END IF;
           IF iZaikoprcsdydm >= zaikosimedydm THEN
              zaikosimedydm := iZaikoPrcsDydm; -- 在庫処理年月が新しい場合：締年月は在庫処理年月
           END IF;

           -- 在庫テーブル
           -- 更新
           UPDATE S13T002T ZKT
              SET ZKT.TOTSUKISONOTANYUKOKAZU = ZKT.TOTSUKISONOTANYUKOKAZU + meisai_rec.NOHINSURYO -- 当月その他入庫数
                , ZKT.TOTSUKIMATSUZAIKOKAZU = ZKT.TOTSUKIKEIHISHUKKOKAZU + meisai_rec.NOHINSURYO -- 当月末在庫数
                 /* 更新者 */
                , ZKT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
                , ZKT.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
                , ZKT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
                , ZKT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
                , ZKT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
                , ZKT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
                , ZKT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
                , ZKT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
            WHERE ZKT.APKIHONCD = acpt_rec.APKIHONCD
              AND ZKT.SHOPCD    = acpt_rec.SHOPCD
              AND ZKT.SOKOCD    = tokuisakiMST.SABURTLSOKOCD
              AND ZKT.SHNCD     = meisai_rec.SHNCD
              AND ZKT.SHIMEDYDM = zaikosimedydm
           ;
           -- 更新項目がないの場合：登録
           IF SQL%ROWCOUNT = 0 THEN
              INSERT INTO S13T002T (
                     APKIHONCD  -- AP基本コード
                   , SHOPCD     -- ショップコード
                   , SOKOCD     -- 倉庫コード
                   , SHNCD      -- 商品コード
                   , SHIMEDYDM  -- 締年月
                   , MAEDMMATSUZAIKOKAZU -- 前月末在庫数
                   , TOTSUKISIIREKAZU -- 当月仕入数
                   , TOTSUKISIIREKIN -- 当月仕入金額
                   , TOTSUKISEIZOKAZU -- 当月製造数
                   , TOTSUKICHOSEINYUKOKAZU -- 当月調整入庫数
                   , TOTSUKISONOTANYUKOKAZU    -- 当月その他入庫数
                   , TOTSUKIURIKAZU -- 当月売上数
                   , TOTSUKICHOSEISHUKKOKAZU -- 当月調整出庫数
                   , TOTSUKIKEIHISHUKKOKAZU -- 当月経費出庫数
                   , TOTSUKISONOTASHUKKOKAZU -- 当月その他出庫数
                   , TOTSUKIMATSUZAIKOKAZU -- 当月末在庫数
                   , TOTSUKITNORSSURYO -- 当月棚卸数量
                   , TOTSUKIKFSKSURYO -- 当月過不足数量
                   , ZAIKOUNT -- 在庫単価
                   , SHIMEFLG -- 締フラグ
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
                     acpt_rec.APKIHONCD -- AP基本コード
                   , acpt_rec.SHOPCD    -- ショップコード
                   , tokuisakiMST.SABURTLSOKOCD -- 倉庫コード
                   , meisai_rec.SHNCD   -- 商品コード
                   , zaikosimedydm  -- 締年月
                   , 0 -- 前月末在庫数
                   , 0 -- 当月仕入数
                   , 0 -- 当月仕入金額
                   , 0 -- 当月製造数
                   , 0 -- 当月調整入庫数
                   , meisai_rec.NOHINSURYO-- 当月その他入庫数
                   , 0 -- 当月売上数
                   , 0 -- 当月調整出庫数
                   , 0 -- 当月経費出庫数
                   , 0 -- 当月その他出庫数
                   , meisai_rec.NOHINSURYO -- 当月末在庫数
                   , NULL -- 当月棚卸数量
                   , NULL -- 当月過不足数量
                   , 0 -- 在庫単価
                   , '0' -- 締フラグ
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

           -- 在庫入出庫明細テーブル
           zaikooisaiban := 0;
           -- 連番の取得
           SELECT COUNT(*)
             INTO zaikooisaiban
             FROM S13T003T ZMT
            WHERE ZMT.APKIHONCD   = acpt_rec.APKIHONCD
              AND ZMT.SHOPCD      = acpt_rec.SHOPCD
              AND ZMT.SOKOCD      = tokuisakiMST.SABURTLSOKOCD
              AND ZMT.SHNCD       = meisai_rec.SHNCD
              AND ZMT.NYUSHUKKODD = acpt_rec.DLVRYJISSEKIDD
              AND ZMT.DNPYNO      = acpt_rec.ACPTDNPYNO
              AND ZMT.DNPYGYONO   = meisai_Rec.ACPTDNPYMEISAINO
           ;
           INSERT INTO S13T003T (
                  APKIHONCD -- AP基本コード
                , SHOPCD    -- ショップコード
                , SOKOCD -- 倉庫コード
                , SHNCD  -- 商品コード
                , SHIMEDYDM   -- 締年月
                , NYUSHUKKODD -- 入出庫日
                , DNPYNO -- 伝票番号
                , EDANO  -- 枝番号
                , DNPYGYONO -- 伝票行番号
                , RENBAN -- 連番
                , DNPYKBN -- 伝票区分
                , NYUSHUKKOKIND -- 入出庫種別
                , NYUSHUKKOKBN  -- 入出庫区分
                , NYUSHUKKOSAKIAPKIHONCD   -- 入出庫先ＡＰ基本コード
                , NYUSHUKKOSAKISHOPCD      -- 入出庫先ショップコード
                , NYUSHUKKOSAKITOKUISAKICD -- 入出庫先得意先コード
                , NYUSHUKKOSAKISOKOCD -- 入出庫先倉庫コード
                , NYUKOMOTOPLNTCD -- 入庫元プラントコード
                , NYUKOMOTOSIIRESAKICD -- 入庫元仕入先コード
                , NYUKOSURYO  -- 入庫数量
                , SHUKKOSURYO -- 出庫数量：数量
                , DATSKSMOTOKBN -- データ作成元区分
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
                    acpt_rec.APKIHONCD -- AP基本コード
                  , acpt_rec.SHOPCD    -- ショップコード
                  , tokuisakiMST.SABURTLSOKOCD -- 倉庫コード
                  , meisai_rec.SHNCD   -- 商品コード
                  , zaikosimedydm      -- 締年月
                  , acpt_rec.DLVRYJISSEKIDD -- 入出庫日
                  , acpt_rec.ACPTDNPYNO -- 伝票番号
                  , acpt_rec.EDANO      -- 枝番号
                  , meisai_rec.ACPTDNPYMEISAINO -- 伝票行番号
                  , NVL(zaikooisaiban,0) + 1 -- 連番
                  , '22' -- 伝票区分
                  , '04' -- 入出庫種別：倉庫移動(22)の場合は'04'
                  , '1' -- 入出庫区分
                  , acpt_rec.DLVRYKANKATSUAPKIHONCD -- 入出庫先ＡＰ基本コード：AP基本コード
                  , acpt_rec.DLVRYKANKATSUSHOPCD    -- 入出庫先ショップコード：ショップコード
                  , NULL -- 入出庫先得意先コード
                  , acpt_rec.GZKT_SOKOCD -- 入出庫先倉庫コード
                  , NULL -- 入庫元プラントコード
                  , NULL -- 入庫元仕入先コード
                  , meisai_rec.NOHINSURYO -- 入庫数量
                  , 0 -- 出庫数量：数量
                  , DATSKSMOTOKBN_NEW -- データ作成元区分
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

    END LOOP;
    END LOOP;

    EXCEPTION
      WHEN OTHERS THEN
        IF SELECT_ACPTD%ISOPEN THEN
           CLOSE SELECT_ACPTD;
        END IF;
        IF SELECT_ACPTM%ISOPEN THEN
           CLOSE SELECT_ACPTM;
        END IF;
        RAISE_APPLICATION_ERROR(-20014, SQLERRM || '在庫データ作成時エラーが発生しました。');
  END processZaiko;

-- 本線対応追加 start ABI
  /*****************************************************************************/
  /* プロシージャ名：在庫データ作成WEB処理                                     */
  /* 引数（ＩＮ）     ：work_rec         配達実績データヘッダ                  */
  /*                  ：tokuisakiMST     得意先マスタ                          */
  /*                  ：iShopzaikokrkbn  ショップ在庫管理区分                  */
  /*                  ：iZaikoprcsdydm   ショップ在庫処理年月                  */
  /*                  ：iShopShimedd     ショップ締日                          */
  /*****************************************************************************/
  PROCEDURE processZaikoWeb( work_rec        IN S05W001T%rowtype -- 配達実績データヘッダ
                           , tokuisakiMST    IN S02M013T%rowtype -- 得意先マスタ
                           , iShopzaikokrkbn IN VARCHAR2         -- ショップ在庫管理区分
                           , iZaikoprcsdydm  IN VARCHAR2         -- ショップ在庫処理年月
                           , iShopShimedd    IN VARCHAR2         -- ショップ締日
  ) IS
  TYPE S13T003T_LIST  IS TABLE OF S13T003T%rowtype;
  /* 変数宣言 */
  zaikosimedydm VARCHAR2(6) := NULL; -- 在庫締年月
  zaikooisaiban NUMBER := NULL; -- 在庫入出庫明細テーブル採番
  serverL VARCHAR2(2)  := NULL; -- サーバー大分類
  serverM VARCHAR2(3)  := NULL; -- サーバー中分類
  insAkadnGenzaikoT       S13T001T%rowtype := NULL; -- 現在庫テーブル（赤伝登録用）
  insAkadnZaikoT          S13T002T%rowtype := NULL; -- 在庫テーブル（赤伝登録用）
  insAkadnZaikoMeiT       S13T003T%rowtype := NULL; -- 在庫入出庫明細テーブル（赤伝登録用）
  insAkadnZaikoMeiTList   S13T003T_LIST    := S13T003T_LIST(); -- 在庫入出庫テーブル（赤伝登録用）リスト
  arrayCnt NUMBER := 1; -- 配列カウント
  /* CURSOR：受注配達テーブル */
  CURSOR SELECT_ACPTD IS
    SELECT JHTT.*
         , ( CASE WHEN iShopzaikokrkbn IS NULL OR iShopzaikokrkbn <> '2'
                  THEN DKTM.KYOTENSOKOCD
                  WHEN TNSM.TNSHASHARYOSOKOCD IS NULL
                  THEN DKTM.KYOTENSOKOCD
                  ELSE TNSM.TNSHASHARYOSOKOCD
             END ) GZKT_SOKOCD
      FROM S04T001T JHTT
      LEFT OUTER JOIN S02M031T DKTM -- 配達拠点マスタ
              ON ( DKTM.APKIHONCD  = JHTT.DLVRYKANKATSUAPKIHONCD
                   AND DKTM.SHOPCD = JHTT.DLVRYKANKATSUSHOPCD
                   AND DKTM.DLVRYKYOTENCD = JHTT.DLVRYKYOTENCD
                 )
      LEFT OUTER JOIN S02M003T TNSM -- 担当者マスタ
              ON ( TNSM.APKIHONCD  = JHTT.DLVRYKANKATSUAPKIHONCD
                   AND TNSM.SHOPCD = JHTT.DLVRYKANKATSUSHOPCD
                   AND TNSM.TNSHACD = JHTT.DLVRYTNSHACD
                 )
     WHERE JHTT.APKIHONCD  = work_rec.APKIHONCD
       AND JHTT.SHOPCD     = work_rec.SHOPCD
       AND JHTT.ACPTDNPYNO = work_rec.ACPTDNPYNO
       AND JHTT.EDANO      = work_rec.EDANO
       AND JHTT.YUKOKBN = '1'
  ;
  /* CURSOR：受注配達明細テーブル */
  CURSOR SELECT_ACPTM IS
    SELECT JHMT.*
         , SHMV.ZAIKOKANRITRGTFLG
      FROM S04T002T JHMT
      LEFT OUTER JOIN S02V001T SHMV
              ON ( SHMV.APKIHONCD  = JHMT.APKIHONCD
                   AND SHMV.SHOPCD = JHMT.SHOPCD
                   AND SHMV.SHNCD  = JHMT.SHNCD
                 )
     WHERE JHMT.APKIHONCD  = work_rec.APKIHONCD
       AND JHMT.SHOPCD     = work_rec.SHOPCD
       AND JHMT.ACPTDNPYNO = work_rec.ACPTDNPYNO
       AND JHMT.EDANO      = work_rec.EDANO
       AND JHMT.YUKOKBN = '1'
  ;
  BEGIN
    serverL := getMojisetteiatai( '000', '000', '096', '4', NULL, NULL, '商品大分類コード：サーバー' );
    serverM := getMojisetteiatai( '000', '000', '134', '41', NULL, NULL, '商品中分類コード：サーバー本体' );

    FOR acpt_rec IN SELECT_ACPTD LOOP
      -- 在庫入出庫テーブル（赤伝登録用）リスト、および配列カウントの初期化
      arrayCnt := 1;
      insAkadnZaikoMeiTList := S13T003T_LIST();

      -- 以下の全てに該当する場合、赤伝用の在庫データを作成する
      --  ・ＡＰ基本コードと配達管轄ＡＰ基本コードが同一
      --  ・ショップコードと配達管轄ショップコードが同一
      --  ・売上伝票番号（赤）が設定されている
      IF acpt_rec.APKIHONCD = acpt_rec.DLVRYKANKATSUAPKIHONCD AND
         acpt_rec.SHOPCD = acpt_rec.DLVRYKANKATSUSHOPCD AND
         cmnAkauridnpyno IS NOT NULL THEN
        FOR meisai_rec IN (SELECT ZNMT.*
                             FROM S13T003T ZNMT
                            WHERE ZNMT.DNPYNO IN (
                                                  SELECT URI.URIDNPYNO
                                                    FROM S06T001T URI
                                                   WHERE URI.APKIHONCD = acpt_rec.APKIHONCD
                                                     AND URI.SHOPCD = acpt_rec.SHOPCD
                                                     AND URI.ACPTDNPYNO = acpt_rec.ACPTDNPYNO
                                                     AND URI.ACPTDNPYEDANO = acpt_rec.EDANO
                                                     AND URI.DNPYNO = cmnAkauridnpyno
                                                )
                              AND ((
                                        ZNMT.NYUSHUKKOKBN = '1' --入庫
                                    AND ZNMT.APKIHONCD = acpt_rec.APKIHONCD
                                    AND ZNMT.SHOPCD = acpt_rec.SHOPCD
                                   ) OR (
                                        ZNMT.NYUSHUKKOKBN = '2' --出庫
                                    AND ZNMT.APKIHONCD = acpt_rec.APKIHONCD
                                    AND ZNMT.SHOPCD = acpt_rec.SHOPCD
                                   ))
                              AND ZNMT.YUKOKBN = '1') LOOP
          -- 在庫入出庫明細テーブル（赤伝登録用）を設定する
          insAkadnZaikoMeiT := meisai_rec;
          insAkadnZaikoMeiT.DNPYNO        := cmnAkauridnpyno;             -- 伝票番号
          insAkadnZaikoMeiT.EDANO         := '0';                         -- 枝番号
          -- 締年月
          -- 入出庫日の桁数が6桁を超過している場合、入出庫日の先頭6桁を設定
          IF LENGTHB(meisai_rec.NYUSHUKKODD) > 6 THEN
            insAkadnZaikoMeiT.SHIMEDYDM := SUBSTR(meisai_rec.NYUSHUKKODD, 1, 6);
          END IF;
          -- 入出庫日の7から8桁目が、パラメータ.ショップ締日を超過している場合、入出庫日の翌月を算出し、先頭6桁を設定
          IF SUBSTRB(meisai_rec.NYUSHUKKODD, 7, 2) > iShopShimedd THEN
            -- insAkadnZaikoMeiT.SHIMEDYDM := SUBSTR(ADD_MONTHS(meisai_rec.NYUSHUKKODD, 1), 1, 6);
            insAkadnZaikoMeiT.SHIMEDYDM := TO_CHAR( ADD_MONTHS( TO_DATE(SUBSTR(meisai_rec.NYUSHUKKODD, 1, 6), 'YYYYMM'), 1), 'YYYYMM');
          END IF;
          -- 在庫処理年月の値以下の場合、在庫処理年月を設定
          IF iZaikoprcsdydm >= insAkadnZaikoMeiT.SHIMEDYDM THEN
            insAkadnZaikoMeiT.SHIMEDYDM := iZaikoPrcsDydm;
          END IF;
          insAkadnZaikoMeiT.DATSKSMOTOKBN := '16';                        -- データ作成元区分
          insAkadnZaikoMeiT.NYUKOSURYO    := meisai_rec.NYUKOSURYO * -1;  -- 入庫数量
          insAkadnZaikoMeiT.SHUKKOSURYO   := meisai_rec.SHUKKOSURYO * -1; -- 出庫数量

          -- ACCIS2023-6830 start-ABI 赤伝票分の在庫入出庫明細テーブル登録データのタイムスタンプ設定を追加
          -- タイムスタンプ設定
          insAkadnZaikoMeiT.TOSHAAPKIHONCD := userinfo_rec.APKIHONCD;      -- 登録者ＡＰ基本コード
          insAkadnZaikoMeiT.TOSHASHOPCD    := userinfo_rec.SHOPCD;         -- 登録者ショップコード
          insAkadnZaikoMeiT.TOSHACD        := userinfo_rec.TNSHACD;        -- 登録者コード
          insAkadnZaikoMeiT.INPTSYSTMYMD   := userinfo_rec.SYSTMYMD;       -- 登録システム日付
          insAkadnZaikoMeiT.INPTSYSTMJIKAN := userinfo_rec.SYSTMJIKAN;     -- 登録システム時間
          insAkadnZaikoMeiT.INPTUNYOYMD    := userinfo_rec.UNYOYMD;        -- 登録運用日付
          insAkadnZaikoMeiT.INPTIPADRSE    := userinfo_rec.IPADRSE;        -- 登録ＩＰアドレス
          insAkadnZaikoMeiT.INPTPGMID      := userinfo_rec.PGMID;          -- 登録プログラムＩＤ
          insAkadnZaikoMeiT.KOSHAAPKIHONCD := userinfo_rec.APKIHONCD;      -- 更新者ＡＰ基本コード
          insAkadnZaikoMeiT.KOSHASHOPCD    := userinfo_rec.SHOPCD;         -- 更新者ショップコード
          insAkadnZaikoMeiT.KOSHACD        := userinfo_rec.TNSHACD;        -- 更新者コード
          insAkadnZaikoMeiT.KSHNSYSTMYMD   := userinfo_rec.SYSTMYMD;       -- 更新システム日付
          insAkadnZaikoMeiT.KSHNSYSTMJIKAN := userinfo_rec.SYSTMJIKAN;     -- 更新システム時間
          insAkadnZaikoMeiT.KSHNUNYOYMD    := userinfo_rec.UNYOYMD;        -- 更新運用日付
          insAkadnZaikoMeiT.KSHNIPADRSE    := userinfo_rec.IPADRSE;        -- 更新ＩＰアドレス
          insAkadnZaikoMeiT.KSHNPGMID      := userinfo_rec.PGMID;          -- 更新プログラムＩＤ
          -- ACCIS2023-6830 end-ABI

          insAkadnZaikoMeiTList.EXTEND;
          insAkadnZaikoMeiTList(arrayCnt) := insAkadnZaikoMeiT;
          arrayCnt := arrayCnt + 1;

          -- 現在庫テーブル（赤伝登録用）を設定する
          -- 現在庫データを取得する
          BEGIN
            SELECT GZIK.*
              INTO insAkadnGenzaikoT
              FROM S13T001T GZIK
             WHERE GZIK.APKIHONCD = meisai_rec.APKIHONCD
               AND GZIK.SHOPCD    = meisai_rec.SHOPCD
               AND GZIK.SOKOCD    = meisai_rec.SOKOCD
               AND GZIK.SHNCD     = meisai_rec.SHNCD
            FOR UPDATE
            ;
            -- 現在庫データが取得できた場合、在庫数量を更新
            UPDATE S13T001T GZIK
               SET GZIK.ZAIKOSURYO     = GZIK.ZAIKOSURYO - meisai_rec.NYUKOSURYO + meisai_rec.SHUKKOSURYO -- 在庫数量
                 /* 更新者 */
                 , GZIK.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD       -- 更新者ＡＰ基本コード
                 , GZIK.KOSHASHOPCD    = userinfo_rec.SHOPCD          -- 更新者ショップコード
                 , GZIK.KOSHACD        = userinfo_rec.TNSHACD         -- 更新者コード
                 , GZIK.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD        -- 更新システム日付
                 , GZIK.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN      -- 更新システム時間
                 , GZIK.KSHNUNYOYMD    = userinfo_rec.UNYOYMD         -- 更新運用日付
                 , GZIK.KSHNIPADRSE    = userinfo_rec.IPADRSE         -- 更新ＩＰアドレス
                 , GZIK.KSHNPGMID      = userinfo_rec.PGMID           -- 更新プログラムＩＤ
             WHERE GZIK.APKIHONCD      = insAkadnGenzaikoT.APKIHONCD  -- AP基本コード
               AND GZIK.SHOPCD         = insAkadnGenzaikoT.SHOPCD     -- ショップコード
               AND GZIK.SOKOCD         = insAkadnGenzaikoT.SOKOCD     -- 倉庫コード
               AND GZIK.SHNCD          = insAkadnGenzaikoT.SHNCD      -- 商品コード
            ;
          EXCEPTION
            WHEN NO_DATA_FOUND THEN
              -- 現在庫データが取得できない場合、現在庫テーブル（赤伝登録用）を設定し登録
              insAkadnGenzaikoT.APKIHONCD      := meisai_rec.APKIHONCD;        -- AP基本コード
              insAkadnGenzaikoT.SHOPCD         := meisai_rec.SHOPCD;           -- ショップコード
              insAkadnGenzaikoT.SOKOCD         := meisai_rec.SOKOCD;           -- 倉庫コード
              insAkadnGenzaikoT.SHNCD          := meisai_rec.SHNCD;            -- 商品コード
              insAkadnGenzaikoT.ZAIKOSURYO     := (meisai_rec.NYUKOSURYO + meisai_rec.SHUKKOSURYO) * 1; -- 在庫数量
              insAkadnGenzaikoT.YUKOKBN        := '1';                         -- 有効区分
              insAkadnGenzaikoT.TOSHAAPKIHONCD := userinfo_rec.APKIHONCD;      -- 登録者ＡＰ基本コード
              insAkadnGenzaikoT.TOSHASHOPCD    := userinfo_rec.SHOPCD;         -- 登録者ショップコード
              insAkadnGenzaikoT.TOSHACD        := userinfo_rec.TNSHACD;        -- 登録者コード
              insAkadnGenzaikoT.INPTSYSTMYMD   := userinfo_rec.SYSTMYMD;       -- 登録システム日付
              insAkadnGenzaikoT.INPTSYSTMJIKAN := userinfo_rec.SYSTMJIKAN;     -- 登録システム時間
              insAkadnGenzaikoT.INPTUNYOYMD    := userinfo_rec.UNYOYMD;        -- 登録運用日付
              insAkadnGenzaikoT.INPTIPADRSE    := userinfo_rec.IPADRSE;        -- 登録ＩＰアドレス
              insAkadnGenzaikoT.INPTPGMID      := userinfo_rec.PGMID;          -- 登録プログラムＩＤ
              insAkadnGenzaikoT.KOSHAAPKIHONCD := userinfo_rec.APKIHONCD;      -- 更新者ＡＰ基本コード
              insAkadnGenzaikoT.KOSHASHOPCD    := userinfo_rec.SHOPCD;         -- 更新者ショップコード
              insAkadnGenzaikoT.KOSHACD        := userinfo_rec.TNSHACD;        -- 更新者コード
              insAkadnGenzaikoT.KSHNSYSTMYMD   := userinfo_rec.SYSTMYMD;       -- 更新システム日付
              insAkadnGenzaikoT.KSHNSYSTMJIKAN := userinfo_rec.SYSTMJIKAN;     -- 更新システム時間
              insAkadnGenzaikoT.KSHNUNYOYMD    := userinfo_rec.UNYOYMD;        -- 更新運用日付
              insAkadnGenzaikoT.KSHNIPADRSE    := userinfo_rec.IPADRSE;        -- 更新ＩＰアドレス
              insAkadnGenzaikoT.KSHNPGMID      := userinfo_rec.PGMID;          -- 更新プログラムＩＤ

              INSERT INTO S13T001T VALUES insAkadnGenzaikoT;
          END;

          -- 在庫テーブル（赤伝登録用）を設定する
          -- 在庫データを取得する
          BEGIN
            SELECT ZIKO.*
              INTO insAkadnZaikoT
              FROM S13T002T ZIKO
             WHERE ZIKO.APKIHONCD = meisai_rec.APKIHONCD
               AND ZIKO.SHOPCD    = meisai_rec.SHOPCD
               AND ZIKO.SOKOCD    = meisai_rec.SOKOCD
               AND ZIKO.SHNCD     = meisai_rec.SHNCD
               -- ACCIS2023-7129 在庫処理年月更新前に配達実績登録されているデータの赤伝票を処理する際
               --                作成する赤伝票分の在庫入出庫明細テーブルの締年月と更新する在庫テーブルの年月にズレが生じ
               --                在庫テーブル上の在庫と入出庫明細を締年月で集計した際の在庫数に差異が生じる不具合の修正
               --                在庫テーブル更新条件の締年月を元データの締年月ではなく作成する赤伝票の締年月に変更
               --AND ZIKO.SHIMEDYDM = meisai_rec.SHIMEDYDM
               AND ZIKO.SHIMEDYDM = insAkadnZaikoMeiT.SHIMEDYDM
            FOR UPDATE
            ;

            -- 在庫データが取得できた場合、当月情報を更新
            UPDATE S13T002T ZIKO
               SET ZIKO.TOTSUKISONOTANYUKOKAZU = CASE WHEN meisai_rec.NYUSHUKKOKBN = '1' THEN -- 当月その他入庫数
                                                   insAkadnZaikoT.TOTSUKISONOTANYUKOKAZU - meisai_rec.NYUKOSURYO
                                                 ELSE
                                                   insAkadnZaikoT.TOTSUKISONOTANYUKOKAZU
                                                 END
                 , ZIKO.TOTSUKIMATSUZAIKOKAZU  = CASE WHEN meisai_rec.NYUSHUKKOKBN = '1' THEN -- 当月末在庫数
                                                   insAkadnZaikoT.TOTSUKIMATSUZAIKOKAZU - meisai_rec.NYUKOSURYO
                                                 WHEN meisai_rec.NYUSHUKKOKBN = '2' THEN
                                                   insAkadnZaikoT.TOTSUKIMATSUZAIKOKAZU + meisai_rec.SHUKKOSURYO
                                                 ELSE
                                                   insAkadnZaikoT.TOTSUKIMATSUZAIKOKAZU
                                                 END
                 , ZIKO.TOTSUKIURIKAZU         = CASE WHEN meisai_rec.NYUSHUKKOKBN = '2' AND meisai_rec.DNPYKBN = '14' THEN -- 当月売上数
                                                   insAkadnZaikoT.TOTSUKIURIKAZU - meisai_rec.SHUKKOSURYO
                                                 ELSE
                                                   insAkadnZaikoT.TOTSUKIURIKAZU
                                                 END
                 , ZIKO.TOTSUKIKEIHISHUKKOKAZU = CASE WHEN meisai_rec.NYUSHUKKOKBN = '2' AND meisai_rec.DNPYKBN = '15' THEN -- 当月経費出庫数
                                                   insAkadnZaikoT.TOTSUKIKEIHISHUKKOKAZU - meisai_rec.SHUKKOSURYO
                                                 ELSE
                                                   insAkadnZaikoT.TOTSUKIKEIHISHUKKOKAZU
                                                 END
                 /* 更新者 */
                 , ZIKO.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD       -- 更新者ＡＰ基本コード
                 , ZIKO.KOSHASHOPCD    = userinfo_rec.SHOPCD          -- 更新者ショップコード
                 , ZIKO.KOSHACD        = userinfo_rec.TNSHACD         -- 更新者コード
                 , ZIKO.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD        -- 更新システム日付
                 , ZIKO.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN      -- 更新システム時間
                 , ZIKO.KSHNUNYOYMD    = userinfo_rec.UNYOYMD         -- 更新運用日付
                 , ZIKO.KSHNIPADRSE    = userinfo_rec.IPADRSE         -- 更新ＩＰアドレス
                 , ZIKO.KSHNPGMID      = userinfo_rec.PGMID           -- 更新プログラムＩＤ
             WHERE ZIKO.APKIHONCD      = insAkadnZaikoT.APKIHONCD     -- AP基本コード
               AND ZIKO.SHOPCD         = insAkadnZaikoT.SHOPCD        -- ショップコード
               AND ZIKO.SOKOCD         = insAkadnZaikoT.SOKOCD        -- 倉庫コード
               AND ZIKO.SHNCD          = insAkadnZaikoT.SHNCD         -- 商品コード
               -- ACCIS2023-7165 在庫テーブルの更新条件として指定する締年月を元伝票の締年月ではなく、算出した在庫年月に修正
               AND ZIKO.SHIMEDYDM      = insAkadnZaikoMeiT.SHIMEDYDM  -- 締年月
            ;
          EXCEPTION
            WHEN NO_DATA_FOUND THEN
              -- 在庫データが取得できない場合、在庫テーブル（赤伝登録用）を設定し登録
              insAkadnZaikoT.APKIHONCD               := meisai_rec.APKIHONCD; -- ＡＰ基本コード
              insAkadnZaikoT.SHOPCD                  := meisai_rec.SHOPCD;    -- ショップコード
              insAkadnZaikoT.SOKOCD                  := meisai_rec.SOKOCD;    -- 倉庫コード
              insAkadnZaikoT.SHNCD                   := meisai_rec.SHNCD;     -- 商品コード
              -- ACCIS2023-7165 在庫テーブル新規登録時の締年月を元伝票の締年月ではなく算出した在庫年月に修正
              insAkadnZaikoT.SHIMEDYDM               := insAkadnZaikoMeiT.SHIMEDYDM; -- 締年月
              insAkadnZaikoT.MAEDMMATSUZAIKOKAZU     := '0';                  -- 前月末在庫数
              insAkadnZaikoT.TOTSUKISIIREKAZU        := '0';                  -- 当月仕入数
              insAkadnZaikoT.TOTSUKISIIREKIN         := '0';                  -- 当月仕入金額
              insAkadnZaikoT.TOTSUKISEIZOKAZU        := '0';                  -- 当月製造数
              insAkadnZaikoT.TOTSUKICHOSEINYUKOKAZU  := '0';                  -- 当月調整入庫数
              IF meisai_rec.NYUSHUKKOKBN = '1' THEN                           -- 当月その他入庫数
                insAkadnZaikoT.TOTSUKISONOTANYUKOKAZU  := meisai_rec.NYUKOSURYO * -1;
              ELSE
                insAkadnZaikoT.TOTSUKISONOTANYUKOKAZU  := '0';
              END IF;
              IF meisai_rec.NYUSHUKKOKBN = '2' AND meisai_rec.DNPYKBN = '14' THEN -- 当月売上数
                insAkadnZaikoT.TOTSUKIURIKAZU := meisai_rec.SHUKKOSURYO * -1;
              ELSE
                insAkadnZaikoT.TOTSUKIURIKAZU := '0';
              END IF;
              insAkadnZaikoT.TOTSUKICHOSEISHUKKOKAZU := '0';                      -- 当月調整出庫数
              IF meisai_rec.NYUSHUKKOKBN = '2' AND meisai_rec.DNPYKBN = '15' THEN -- 当月経費出庫数
                insAkadnZaikoT.TOTSUKIKEIHISHUKKOKAZU  := meisai_rec.SHUKKOSURYO * -1;
              ELSE
                insAkadnZaikoT.TOTSUKIKEIHISHUKKOKAZU  := '0';
              END IF;
              insAkadnZaikoT.TOTSUKISONOTASHUKKOKAZU := '0';                  -- 当月その他出庫数
              IF meisai_rec.NYUSHUKKOKBN = '1' THEN                           -- 当月末在庫数
                insAkadnZaikoT.TOTSUKIMATSUZAIKOKAZU := meisai_rec.NYUKOSURYO * -1;
              ELSIF meisai_rec.NYUSHUKKOKBN = '2' THEN
                insAkadnZaikoT.TOTSUKIMATSUZAIKOKAZU := meisai_rec.SHUKKOSURYO * -1;
              ELSE
                insAkadnZaikoT.TOTSUKIMATSUZAIKOKAZU := '0';
              END IF;
              insAkadnZaikoT.TOTSUKITNORSSURYO       := '0';                  -- 当月棚卸数量
              insAkadnZaikoT.TOTSUKIKFSKSURYO        := '0';                  -- 当月過不足数量
              insAkadnZaikoT.ZAIKOUNT                := '0';                  -- 在庫単価
              insAkadnZaikoT.SHIMEFLG                := '0';                  -- 締フラグ
              insAkadnZaikoT.YUKOKBN        := '1';                         -- 有効区分
              insAkadnZaikoT.TOSHAAPKIHONCD := userinfo_rec.APKIHONCD;      -- 登録者ＡＰ基本コード
              insAkadnZaikoT.TOSHASHOPCD    := userinfo_rec.SHOPCD;         -- 登録者ショップコード
              insAkadnZaikoT.TOSHACD        := userinfo_rec.TNSHACD;        -- 登録者コード
              insAkadnZaikoT.INPTSYSTMYMD   := userinfo_rec.SYSTMYMD;       -- 登録システム日付
              insAkadnZaikoT.INPTSYSTMJIKAN := userinfo_rec.SYSTMJIKAN;     -- 登録システム時間
              insAkadnZaikoT.INPTUNYOYMD    := userinfo_rec.UNYOYMD;        -- 登録運用日付
              insAkadnZaikoT.INPTIPADRSE    := userinfo_rec.IPADRSE;        -- 登録ＩＰアドレス
              insAkadnZaikoT.INPTPGMID      := userinfo_rec.PGMID;          -- 登録プログラムＩＤ
              insAkadnZaikoT.KOSHAAPKIHONCD := userinfo_rec.APKIHONCD;      -- 更新者ＡＰ基本コード
              insAkadnZaikoT.KOSHASHOPCD    := userinfo_rec.SHOPCD;         -- 更新者ショップコード
              insAkadnZaikoT.KOSHACD        := userinfo_rec.TNSHACD;        -- 更新者コード
              insAkadnZaikoT.KSHNSYSTMYMD   := userinfo_rec.SYSTMYMD;       -- 更新システム日付
              insAkadnZaikoT.KSHNSYSTMJIKAN := userinfo_rec.SYSTMJIKAN;     -- 更新システム時間
              insAkadnZaikoT.KSHNUNYOYMD    := userinfo_rec.UNYOYMD;        -- 更新運用日付
              insAkadnZaikoT.KSHNIPADRSE    := userinfo_rec.IPADRSE;        -- 更新ＩＰアドレス
              insAkadnZaikoT.KSHNPGMID      := userinfo_rec.PGMID;          -- 更新プログラムＩＤ

              INSERT INTO S13T002T VALUES insAkadnZaikoT;
          END;
        END LOOP;
        -- 在庫入出庫明細テーブル（赤伝登録用）を在庫入出庫明細テーブルに登録
        IF insAkadnZaikoMeiTList.COUNT > 0 THEN
           FOR i IN insAkadnZaikoMeiTList.first..insAkadnZaikoMeiTList.last LOOP
             IF insAkadnZaikoMeiTList.EXISTS(i) THEN
               INSERT INTO S13T003T VALUES insAkadnZaikoMeiTList(i);
             END IF;
           END LOOP;
        END IF;
      END IF;

      -- 以下の全てに該当する場合、赤伝用の在庫データを作成する
      --  ・ＡＰ基本コードと配達管轄ＡＰ基本コードが同一
      --  ・ショップコードと配達管轄ショップコードが同一
      --  ・売上伝票番号（黒）が設定されている
      IF acpt_rec.APKIHONCD = acpt_rec.DLVRYKANKATSUAPKIHONCD AND
         acpt_rec.SHOPCD = acpt_rec.DLVRYKANKATSUSHOPCD AND
         cmnKurouridnpyno IS NOT NULL THEN
        FOR meisai_rec IN SELECT_ACPTM LOOP
          -- 通常分更新
          IF iShopzaikokrkbn IS NOT NULL AND iShopzaikokrkbn <> '0'
             AND meisai_rec.MEISAIKBN = '0'
             AND meisai_rec.TANPINMEISAIFLG = '1' AND meisai_rec.ZAIKOKANRITRGTFLG IS NOT NULL AND meisai_rec.ZAIKOKANRITRGTFLG <> '0'
             AND work_rec.DLVRYJISSEKIDD <= userinfo_rec.UNYOYMD
             AND meisai_rec.NOHINSURYO <> 0
             THEN
             -- 現在庫テーブル
             -- 更新
             UPDATE S13T001T GZKT
                SET GZKT.ZAIKOSURYO = GZKT.ZAIKOSURYO + ( meisai_rec.NOHINSURYO * (-1) ) -- 在庫数量：在庫数量＋納品数量*(-1)
                   /* 更新者 */
                  , GZKT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
                  , GZKT.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
                  , GZKT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
                  , GZKT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
                  , GZKT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
                  , GZKT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
                  , GZKT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
                  , GZKT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
              WHERE GZKT.APKIHONCD = acpt_rec.DLVRYKANKATSUAPKIHONCD
                AND GZKT.SHOPCD    = acpt_rec.DLVRYKANKATSUSHOPCD
                AND GZKT.SOKOCD    = acpt_rec.GZKT_SOKOCD
                AND GZKT.SHNCD     = meisai_rec.SHNCD
             ;
             -- 更新項目がないの場合：登録
             IF SQL%ROWCOUNT = 0 THEN
                INSERT INTO S13T001T (
                       APKIHONCD  -- AP基本コード
                     , SHOPCD     -- ショップコード
                     , SOKOCD     -- 倉庫コード
                     , SHNCD      -- 商品コード
                     , ZAIKOSURYO -- 在庫数量
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
                       acpt_rec.DLVRYKANKATSUAPKIHONCD   -- AP基本コード
                     , acpt_rec.DLVRYKANKATSUSHOPCD      -- ショップコード
                     , acpt_rec.GZKT_SOKOCD -- 倉庫コード
                     , meisai_rec.SHNCD     -- 商品コード
                     , meisai_rec.NOHINSURYO * (-1) -- 数量
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

             -- 在庫締年月取得
             zaikosimedydm := NULL;
             -- ACCIS2023-7167 在庫年月決定時、運用日ではなく配達実績日を基準とするように修正
             IF SUBSTRB(acpt_rec.DLVRYJISSEKIDD,7,2) > iShopShimedd THEN
                zaikosimedydm := TO_CHAR( ADD_MONTHS( TO_DATE(SUBSTRB(acpt_rec.DLVRYJISSEKIDD, 1, 6),'YYYYMM'), 1), 'YYYYMM');
             ELSE
                zaikosimedydm := SUBSTRB(acpt_rec.DLVRYJISSEKIDD,1,6);
             END IF;
             IF iZaikoprcsdydm >= zaikosimedydm THEN
                zaikosimedydm := iZaikoPrcsDydm; -- 在庫処理年月が新しい場合：締年月は在庫処理年月
             END IF;

             -- 在庫テーブル
             -- 更新
             UPDATE S13T002T ZKT
                SET ZKT.TOTSUKIURIKAZU         = ( CASE WHEN tokuisakiMST.TOKUISAKIKBN <> '9'
                                                        THEN ZKT.TOTSUKIURIKAZU + meisai_rec.NOHINSURYO
                                                        ELSE ZKT.TOTSUKIURIKAZU
                                                   END ) -- 当月売上数
                  , ZKT.TOTSUKIKEIHISHUKKOKAZU = ( CASE WHEN tokuisakiMST.TOKUISAKIKBN = '9'
                                                        THEN ZKT.TOTSUKIKEIHISHUKKOKAZU + meisai_rec.NOHINSURYO
                                                        ELSE ZKT.TOTSUKIKEIHISHUKKOKAZU
                                                   END ) -- 当月経費出庫数
                  , ZKT.TOTSUKIMATSUZAIKOKAZU = ZKT.TOTSUKIMATSUZAIKOKAZU + meisai_rec.NOHINSURYO * (-1) -- 当月末在庫数
                   /* 更新者 */
                  , ZKT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
                  , ZKT.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
                  , ZKT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
                  , ZKT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
                  , ZKT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
                  , ZKT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
                  , ZKT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
                  , ZKT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
              WHERE ZKT.APKIHONCD = acpt_rec.DLVRYKANKATSUAPKIHONCD
                AND ZKT.SHOPCD    = acpt_rec.DLVRYKANKATSUSHOPCD
                AND ZKT.SOKOCD    = acpt_rec.GZKT_SOKOCD
                AND ZKT.SHNCD     = meisai_rec.SHNCD
                AND ZKT.SHIMEDYDM = zaikosimedydm
             ;
             -- 更新項目がないの場合：登録
             IF SQL%ROWCOUNT = 0 THEN
                INSERT INTO S13T002T (
                       APKIHONCD  -- AP基本コード
                     , SHOPCD     -- ショップコード
                     , SOKOCD     -- 倉庫コード
                     , SHNCD      -- 商品コード
                     , SHIMEDYDM  -- 締年月
                     , MAEDMMATSUZAIKOKAZU -- 前月末在庫数
                     , TOTSUKISIIREKAZU -- 当月仕入数
                     , TOTSUKISIIREKIN -- 当月仕入金額
                     , TOTSUKISEIZOKAZU -- 当月製造数
                     , TOTSUKICHOSEINYUKOKAZU -- 当月調整入庫数
                     , TOTSUKISONOTANYUKOKAZU    -- 当月その他入庫数
                     , TOTSUKIURIKAZU -- 当月売上数
                     , TOTSUKICHOSEISHUKKOKAZU -- 当月調整出庫数
                     , TOTSUKIKEIHISHUKKOKAZU -- 当月経費出庫数
                     , TOTSUKISONOTASHUKKOKAZU -- 当月その他出庫数
                     , TOTSUKIMATSUZAIKOKAZU -- 当月末在庫数
                     , TOTSUKITNORSSURYO -- 当月棚卸数量
                     , TOTSUKIKFSKSURYO -- 当月過不足数量
                     , ZAIKOUNT -- 在庫単価
                     , SHIMEFLG -- 締フラグ
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
                       acpt_rec.DLVRYKANKATSUAPKIHONCD   -- AP基本コード
                     , acpt_rec.DLVRYKANKATSUSHOPCD      -- ショップコード
                     , acpt_rec.GZKT_SOKOCD -- 倉庫コード
                     , meisai_rec.SHNCD     -- 商品コード
                     , zaikosimedydm  -- 締年月
                     , 0 -- 前月末在庫数
                     , 0 -- 当月仕入数
                     , 0 -- 当月仕入金額
                     , 0 -- 当月製造数
                     , 0 -- 当月調整入庫数
                     , 0    -- 当月その他入庫数
                     , ( CASE WHEN tokuisakiMST.TOKUISAKIKBN <> '9'
                              THEN meisai_rec.NOHINSURYO
                              ELSE 0
                         END ) -- 当月売上数
                     , 0 -- 当月調整出庫数
                     , ( CASE WHEN tokuisakiMST.TOKUISAKIKBN = '9'
                              THEN meisai_rec.NOHINSURYO
                              ELSE 0
                         END ) -- 当月経費出庫数
                     , 0 -- 当月その他出庫数
                     , meisai_rec.NOHINSURYO * (-1) -- 当月末在庫数
                     , NULL -- 当月棚卸数量
                     , NULL -- 当月過不足数量
                     , 0 -- 在庫単価
                     , '0' -- 締フラグ
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

             -- 在庫入出庫明細テーブル
             zaikooisaiban := 0;
             -- 連番の取得
             SELECT COUNT(*)
               INTO zaikooisaiban
               FROM S13T003T ZMT
              WHERE ZMT.APKIHONCD   = acpt_rec.DLVRYKANKATSUAPKIHONCD
                AND ZMT.SHOPCD      = acpt_rec.DLVRYKANKATSUSHOPCD
                AND ZMT.SOKOCD      = acpt_rec.GZKT_SOKOCD
                AND ZMT.SHNCD       = meisai_rec.SHNCD
                AND ZMT.NYUSHUKKODD = acpt_rec.DLVRYJISSEKIDD
                AND ZMT.DNPYNO      = cmnKurouridnpyno
                AND ZMT.DNPYGYONO   = meisai_Rec.ACPTDNPYMEISAINO
             ;
             INSERT INTO S13T003T (
                    APKIHONCD -- AP基本コード
                  , SHOPCD    -- ショップコード
                  , SOKOCD -- 倉庫コード
                  , SHNCD  -- 商品コード
                  , SHIMEDYDM   -- 締年月
                  , NYUSHUKKODD -- 入出庫日
                  , DNPYNO -- 伝票番号
                  , EDANO  -- 枝番号
                  , DNPYGYONO -- 伝票行番号
                  , RENBAN -- 連番
                  , DNPYKBN -- 伝票区分
                  , NYUSHUKKOKIND -- 入出庫種別
                  , NYUSHUKKOKBN  -- 入出庫区分
                  , NYUSHUKKOSAKIAPKIHONCD   -- 入出庫先ＡＰ基本コード
                  , NYUSHUKKOSAKISHOPCD      -- 入出庫先ショップコード
                  , NYUSHUKKOSAKITOKUISAKICD -- 入出庫先得意先コード
                  , NYUSHUKKOSAKISOKOCD -- 入出庫先倉庫コード
                  , NYUKOMOTOPLNTCD -- 入庫元プラントコード
                  , NYUKOMOTOSIIRESAKICD -- 入庫元仕入先コード
                  , NYUKOSURYO  -- 入庫数量
                  , SHUKKOSURYO -- 出庫数量：数量
                  , DATSKSMOTOKBN -- データ作成元区分
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
                      acpt_rec.DLVRYKANKATSUAPKIHONCD   -- AP基本コード
                    , acpt_rec.DLVRYKANKATSUSHOPCD   -- ショップコード
                    , acpt_rec.GZKT_SOKOCD -- 倉庫コード
                    , meisai_rec.SHNCD     -- 商品コード
                    , zaikosimedydm           -- 締年月
                    , acpt_rec.DLVRYJISSEKIDD -- 入出庫日
                    , cmnKurouridnpyno -- 伝票番号
                    , acpt_rec.EDANO      -- 枝番号
                    , meisai_rec.ACPTDNPYMEISAINO -- 伝票行番号
                    , NVL(zaikooisaiban,0) + 1 -- 連番
                    , acpt_rec.DNPYKBN -- 伝票区分
                    , ( CASE WHEN tokuisakiMST.TOKUISAKIKBN = '9' THEN '05' -- 入出庫種別：経費出庫(15)の場合は'05'
                             ELSE '02' -- 入出庫種別：売上出庫(14)の場合は'02'
                        END )
                    , '2' -- 入出庫区分
                    , acpt_rec.APKIHONCD   -- 入出庫先ＡＰ基本コード：AP基本コード
                    , acpt_rec.SHOPCD      -- 入出庫先ショップコード：ショップコード
                    , acpt_rec.TOKUISAKICD -- 入出庫先得意先コード：  得意先コード
                    , NULL -- 入出庫先倉庫コード
                    , NULL -- 入庫元プラントコード
                    , NULL -- 入庫元仕入先コード
                    , 0    -- 入庫数量
                    , meisai_rec.NOHINSURYO -- 出庫数量：数量
                    , DATSKSMOTOKBN_NEW -- データ作成元区分
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

          -- サブレンタル更新
          IF iShopzaikokrkbn IS NOT NULL AND iShopzaikokrkbn <> '0'
             AND meisai_rec.MEISAIKBN = '0'
             AND meisai_rec.TANPINMEISAIFLG = '1' AND meisai_rec.ZAIKOKANRITRGTFLG IS NOT NULL AND meisai_rec.ZAIKOKANRITRGTFLG <> '0'
             AND work_rec.DLVRYJISSEKIDD <= userinfo_rec.UNYOYMD
             -- サーバー商品明細
             AND meisai_rec.LCTGRYCD = serverL AND meisai_rec.MCTGRYCD = serverM
             -- レンタル／サブレンタル
             AND meisai_rec.KEIJOKBN IN ('10', '09')
             -- ショップ／ショップ顧客得意先
             AND ( tokuisakiMST.TOKUISAKIKBN = '2'
                   OR ( tokuisakiMST.TOKUISAKIKBN = '1' AND tokuisakiMST.JUTAKUDLVRYCSTKBN = '2' )
                 )
             AND meisai_rec.NOHINSURYO <> 0
             THEN

             -- 現在庫テーブル
             -- 更新
             UPDATE S13T001T GZKT
                SET GZKT.ZAIKOSURYO = GZKT.ZAIKOSURYO + meisai_rec.NOHINSURYO -- 在庫数量：在庫数量＋納品数量*(-1)
                   /* 更新者 */
                  , GZKT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
                  , GZKT.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
                  , GZKT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
                  , GZKT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
                  , GZKT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
                  , GZKT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
                  , GZKT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
                  , GZKT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
              WHERE GZKT.APKIHONCD = acpt_rec.APKIHONCD
                AND GZKT.SHOPCD    = acpt_rec.SHOPCD
                AND GZKT.SOKOCD    = tokuisakiMST.SABURTLSOKOCD
                AND GZKT.SHNCD     = meisai_rec.SHNCD
             ;
             -- 更新項目がないの場合：登録
             IF SQL%ROWCOUNT = 0 THEN
                INSERT INTO S13T001T (
                       APKIHONCD  -- AP基本コード
                     , SHOPCD     -- ショップコード
                     , SOKOCD     -- 倉庫コード
                     , SHNCD      -- 商品コード
                     , ZAIKOSURYO -- 在庫数量
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
                       acpt_rec.APKIHONCD   -- AP基本コード
                     , acpt_rec.SHOPCD      -- ショップコード
                     , tokuisakiMST.SABURTLSOKOCD -- 倉庫コード
                     , meisai_rec.SHNCD     -- 商品コード
                     , meisai_rec.NOHINSURYO -- 数量
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

             -- 在庫締年月取得
             zaikosimedydm := NULL;
             -- ACCIS2023-7167 在庫年月決定時、運用日ではなく配達実績日を基準とするように修正
             IF SUBSTRB(acpt_rec.DLVRYJISSEKIDD,7,2) > iShopShimedd THEN
                zaikosimedydm := TO_CHAR( ADD_MONTHS( TO_DATE(SUBSTRB(acpt_rec.DLVRYJISSEKIDD, 1, 6),'YYYYMM'), 1), 'YYYYMM');
             ELSE
                zaikosimedydm := SUBSTRB(acpt_rec.DLVRYJISSEKIDD,1,6);
             END IF;
             IF iZaikoprcsdydm >= zaikosimedydm THEN
                zaikosimedydm := iZaikoPrcsDydm; -- 在庫処理年月が新しい場合：締年月は在庫処理年月
             END IF;

             -- 在庫テーブル
             -- 更新
             UPDATE S13T002T ZKT
                SET ZKT.TOTSUKISONOTANYUKOKAZU = ZKT.TOTSUKISONOTANYUKOKAZU + meisai_rec.NOHINSURYO -- 当月その他入庫数
                  , ZKT.TOTSUKIMATSUZAIKOKAZU = ZKT.TOTSUKIKEIHISHUKKOKAZU + meisai_rec.NOHINSURYO -- 当月末在庫数
                   /* 更新者 */
                  , ZKT.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
                  , ZKT.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
                  , ZKT.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
                  , ZKT.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
                  , ZKT.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
                  , ZKT.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
                  , ZKT.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
                  , ZKT.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
              WHERE ZKT.APKIHONCD = acpt_rec.APKIHONCD
                AND ZKT.SHOPCD    = acpt_rec.SHOPCD
                AND ZKT.SOKOCD    = tokuisakiMST.SABURTLSOKOCD
                AND ZKT.SHNCD     = meisai_rec.SHNCD
                AND ZKT.SHIMEDYDM = zaikosimedydm
             ;
             -- 更新項目がないの場合：登録
             IF SQL%ROWCOUNT = 0 THEN
                INSERT INTO S13T002T (
                       APKIHONCD  -- AP基本コード
                     , SHOPCD     -- ショップコード
                     , SOKOCD     -- 倉庫コード
                     , SHNCD      -- 商品コード
                     , SHIMEDYDM  -- 締年月
                     , MAEDMMATSUZAIKOKAZU -- 前月末在庫数
                     , TOTSUKISIIREKAZU -- 当月仕入数
                     , TOTSUKISIIREKIN -- 当月仕入金額
                     , TOTSUKISEIZOKAZU -- 当月製造数
                     , TOTSUKICHOSEINYUKOKAZU -- 当月調整入庫数
                     , TOTSUKISONOTANYUKOKAZU    -- 当月その他入庫数
                     , TOTSUKIURIKAZU -- 当月売上数
                     , TOTSUKICHOSEISHUKKOKAZU -- 当月調整出庫数
                     , TOTSUKIKEIHISHUKKOKAZU -- 当月経費出庫数
                     , TOTSUKISONOTASHUKKOKAZU -- 当月その他出庫数
                     , TOTSUKIMATSUZAIKOKAZU -- 当月末在庫数
                     , TOTSUKITNORSSURYO -- 当月棚卸数量
                     , TOTSUKIKFSKSURYO -- 当月過不足数量
                     , ZAIKOUNT -- 在庫単価
                     , SHIMEFLG -- 締フラグ
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
                       acpt_rec.APKIHONCD -- AP基本コード
                     , acpt_rec.SHOPCD    -- ショップコード
                     , tokuisakiMST.SABURTLSOKOCD -- 倉庫コード
                     , meisai_rec.SHNCD   -- 商品コード
                     , zaikosimedydm  -- 締年月
                     , 0 -- 前月末在庫数
                     , 0 -- 当月仕入数
                     , 0 -- 当月仕入金額
                     , 0 -- 当月製造数
                     , 0 -- 当月調整入庫数
                     , meisai_rec.NOHINSURYO-- 当月その他入庫数
                     , 0 -- 当月売上数
                     , 0 -- 当月調整出庫数
                     , 0 -- 当月経費出庫数
                     , 0 -- 当月その他出庫数
                     , meisai_rec.NOHINSURYO -- 当月末在庫数
                     , NULL -- 当月棚卸数量
                     , NULL -- 当月過不足数量
                     , 0 -- 在庫単価
                     , '0' -- 締フラグ
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

             -- 在庫入出庫明細テーブル
             zaikooisaiban := 0;
             -- 連番の取得
             SELECT COUNT(*)
               INTO zaikooisaiban
               FROM S13T003T ZMT
              WHERE ZMT.APKIHONCD   = acpt_rec.APKIHONCD
                AND ZMT.SHOPCD      = acpt_rec.SHOPCD
                AND ZMT.SOKOCD      = tokuisakiMST.SABURTLSOKOCD
                AND ZMT.SHNCD       = meisai_rec.SHNCD
                AND ZMT.NYUSHUKKODD = acpt_rec.DLVRYJISSEKIDD
                AND ZMT.DNPYNO      = cmnKurouridnpyno
                AND ZMT.DNPYGYONO   = meisai_Rec.ACPTDNPYMEISAINO
             ;
             INSERT INTO S13T003T (
                    APKIHONCD -- AP基本コード
                  , SHOPCD    -- ショップコード
                  , SOKOCD -- 倉庫コード
                  , SHNCD  -- 商品コード
                  , SHIMEDYDM   -- 締年月
                  , NYUSHUKKODD -- 入出庫日
                  , DNPYNO -- 伝票番号
                  , EDANO  -- 枝番号
                  , DNPYGYONO -- 伝票行番号
                  , RENBAN -- 連番
                  , DNPYKBN -- 伝票区分
                  , NYUSHUKKOKIND -- 入出庫種別
                  , NYUSHUKKOKBN  -- 入出庫区分
                  , NYUSHUKKOSAKIAPKIHONCD   -- 入出庫先ＡＰ基本コード
                  , NYUSHUKKOSAKISHOPCD      -- 入出庫先ショップコード
                  , NYUSHUKKOSAKITOKUISAKICD -- 入出庫先得意先コード
                  , NYUSHUKKOSAKISOKOCD -- 入出庫先倉庫コード
                  , NYUKOMOTOPLNTCD -- 入庫元プラントコード
                  , NYUKOMOTOSIIRESAKICD -- 入庫元仕入先コード
                  , NYUKOSURYO  -- 入庫数量
                  , SHUKKOSURYO -- 出庫数量：数量
                  , DATSKSMOTOKBN -- データ作成元区分
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
                      acpt_rec.APKIHONCD -- AP基本コード
                    , acpt_rec.SHOPCD    -- ショップコード
                    , tokuisakiMST.SABURTLSOKOCD -- 倉庫コード
                    , meisai_rec.SHNCD   -- 商品コード
                    , zaikosimedydm      -- 締年月
                    , acpt_rec.DLVRYJISSEKIDD -- 入出庫日
                    , cmnKurouridnpyno -- 伝票番号
                    , acpt_rec.EDANO      -- 枝番号
                    , meisai_rec.ACPTDNPYMEISAINO -- 伝票行番号
                    , NVL(zaikooisaiban,0) + 1 -- 連番
                    , '22' -- 伝票区分
                    , '04' -- 入出庫種別：倉庫移動(22)の場合は'04'
                    , '1' -- 入出庫区分
                    , acpt_rec.DLVRYKANKATSUAPKIHONCD -- 入出庫先ＡＰ基本コード：AP基本コード
                    , acpt_rec.DLVRYKANKATSUSHOPCD    -- 入出庫先ショップコード：ショップコード
                    , NULL -- 入出庫先得意先コード
                    , acpt_rec.GZKT_SOKOCD -- 入出庫先倉庫コード
                    , NULL -- 入庫元プラントコード
                    , NULL -- 入庫元仕入先コード
                    , meisai_rec.NOHINSURYO -- 入庫数量
                    , 0 -- 出庫数量：数量
                    , DATSKSMOTOKBN_NEW -- データ作成元区分
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
        END LOOP;
      END IF;
    END LOOP;

    EXCEPTION
      WHEN OTHERS THEN
        IF SELECT_ACPTD%ISOPEN THEN
           CLOSE SELECT_ACPTD;
        END IF;
        IF SELECT_ACPTM%ISOPEN THEN
           CLOSE SELECT_ACPTM;
        END IF;
        RAISE_APPLICATION_ERROR(-20014, SQLERRM || '在庫データ作成時エラーが発生しました。');
  END processZaikoWeb;
-- 本線対応追加 end ABI

  /*****************************************************************************/
  /* プロシージャ名：在庫引当データ更新処理                                         */
  /* 引数（ＩＮ）      ：work_rec          配達実績データヘッダ                    */
  /*                  ：iShopzaikokrkbn   ショップ在庫管理区分                    */
  /*****************************************************************************/
  PROCEDURE processZkHikiate( work_rec        IN S05W001T%rowtype -- 配達実績データヘッダ
                            , iShopzaikokrkbn IN VARCHAR2 -- ショップ在庫管理区分
  ) IS
  /* 変数宣言 */
  /* CURSOR：受注配達テーブル */
  CURSOR SELECT_ACPTD IS
    SELECT JHTT.*
      FROM S04T001T JHTT
     WHERE JHTT.APKIHONCD  = work_rec.APKIHONCD
       AND JHTT.SHOPCD     = work_rec.SHOPCD
       AND JHTT.ACPTDNPYNO = work_rec.ACPTDNPYNO
       AND JHTT.EDANO      = work_rec.EDANO
       AND JHTT.YUKOKBN = '1'
  ;
  /* CURSOR：受注配達明細テーブル */
  CURSOR SELECT_ACPTM IS
    SELECT JHMT.*
         , SHMV.ZAIKOKANRITRGTFLG
      FROM S04T002T JHMT
      LEFT OUTER JOIN S02V001T SHMV
              ON ( SHMV.APKIHONCD  = JHMT.APKIHONCD
                   AND SHMV.SHOPCD = JHMT.SHOPCD
                   AND SHMV.SHNCD  = JHMT.SHNCD
                 )
     WHERE JHMT.APKIHONCD  = work_rec.APKIHONCD
       AND JHMT.SHOPCD     = work_rec.SHOPCD
       AND JHMT.ACPTDNPYNO = work_rec.ACPTDNPYNO
       AND JHMT.EDANO      = work_rec.EDANO
       AND JHMT.YUKOKBN = '1'
  ;
  BEGIN
    FOR acpt_rec IN SELECT_ACPTD LOOP
    FOR meisai_rec IN SELECT_ACPTM LOOP

        -- 在庫引当テーブル更新
        IF iShopzaikokrkbn IS NOT NULL AND iShopzaikokrkbn <> '0'
           AND meisai_rec.MEISAIKBN = '0'
           AND meisai_rec.TANPINMEISAIFLG = '1' AND meisai_rec.ZAIKOKANRITRGTFLG IS NOT NULL AND meisai_rec.ZAIKOKANRITRGTFLG <> '0'
           THEN
           updateZaikohikiate( acpt_rec.ACPTHIKIATESAKIAPKIHONCD, acpt_rec.ACPTHIKIATESAKISHOPCD, acpt_rec.ACPTHIKIATESAKIKYOTENCD
                             , meisai_rec.SHNCD, meisai_rec.ACPTSURYO );
        END IF;

    END LOOP;
    END LOOP;

    EXCEPTION
      WHEN OTHERS THEN
        IF SELECT_ACPTD%ISOPEN THEN
           CLOSE SELECT_ACPTD;
        END IF;
        IF SELECT_ACPTM%ISOPEN THEN
           CLOSE SELECT_ACPTM;
        END IF;
        RAISE_APPLICATION_ERROR(-20020, SQLERRM || '在庫引当データ更新時エラーが発生しました。');
  END processZkHikiate;

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
                   , KIST.KOSHACD        = userinfo_rec.TNSHACD   -- 更新者コード
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
            , KIST.KOSHACD        = userinfo_rec.TNSHACD   -- 更新者コード
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
                 , userinfo_rec.TNSHACD   -- 登録者コード
                 , userinfo_rec.SYSTMYMD   -- 登録システム日付
                 , userinfo_rec.SYSTMJIKAN -- 登録システム時間
                 , userinfo_rec.UNYOYMD    -- 登録運用日付
                 , userinfo_rec.IPADRSE    -- 登録ＩＰアドレス
                 , userinfo_rec.PGMID      -- 登録プログラムＩＤ
                 , userinfo_rec.APKIHONCD -- 更新者ＡＰ基本コード
                 , userinfo_rec.SHOPCD    -- 更新者ショップコード
                 , userinfo_rec.TNSHACD   -- 更新者コード
                 , userinfo_rec.SYSTMYMD   -- 更新システム日付
                 , userinfo_rec.SYSTMJIKAN -- 更新システム時間
                 , userinfo_rec.UNYOYMD    -- 更新運用日付
                 , userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
                 , userinfo_rec.PGMID      -- 更新プログラムＩＤ
          );
       END IF;
    END IF;
  END processKaiinNyukai;

  /****************************************************************************/
  /* プロシージャ名 ：配達実績得意先一連処理                                        */
  /* 引数           ：iTokuisaki           得意先マスタ                          */
  /*                ：iTokuisakiJST        得意先実績テーブル                    */
  /*                ：iDlvrytnshakshnkbn   ショップマスタ.実績担当者更新区分       */
  /*                ：work_rec             配達実績ワークデータ                   */
  /*                ：sum_rec              合計データ                           */
  /****************************************************************************/
  PROCEDURE processTokuisakiDlvry( iTokuisaki    IN S02M013T%rowtype -- 得意先マスタ
                                 , iTokuisakiJST IN S02M050T%rowtype -- 得意先実績テーブル
                                 , iDlvrytnshakshnkbn IN VARCHAR2 -- ショップマスタ.実績担当者更新区分
                                 , work_rec      IN S05W001T%rowtype -- 配達実績ワークデータ
                                 , sum_rec       IN sum_rec_type -- 合計データ
  ) IS
  /* 変数宣言 */
  delivered_rec      S04T001T%rowtype := NULL; -- 更新後受注配達データ
  isDlvryInfoChanged VARCHAR2(1) := '0'; -- 配達情報変更フラグ
  isDlvryjunChanged  VARCHAR2(1) := '0'; -- 配達順変更フラグ
  isNyukai           VARCHAR2(1) := '0'; -- 入会フラグ
  isShokaisvstup     VARCHAR2(1) := '0'; -- 初回サーバー設置フラグ
  isSvhikiage        VARCHAR2(1) := '0'; -- サーバー引上フラグ
  isIyakukin         VARCHAR2(1) := '0'; -- 違約金更新フラグ
  nServermei NUMBER := 0; -- サーバー明細行数
  nIyakukin  NUMBER := 0; -- 違約金
  saiban     NUMBER := 0; -- 違約金管理番号採番
  nIyakukinkikan NUMBER := 0; -- 違約金適用期間
  nIyakukinkin   NUMBER := 0; -- 違約金金額
  -- 会員一連更新用変数
  doKaiin    VARCHAR2(1)  := '0';  -- 会員作成有無
  cdKaiin    VARCHAR2(10) := NULL; -- 会員コード（自動採番）
  BEGIN
    -- 更新後受注配達データ取得
    SELECT JHTT.*
      INTO delivered_rec
      FROM S04T001T JHTT
     WHERE JHTT.APKIHONCD  = work_rec.APKIHONCD
       AND JHTT.SHOPCD     = work_rec.SHOPCD
       AND JHTT.ACPTDNPYNO = work_rec.ACPTDNPYNO
       AND JHTT.EDANO      = work_rec.EDANO
       AND JHTT.YUKOKBN = '1'
    ;
    -- 状態１：配達情報変更の場合
    IF iDlvrytnshakshnkbn IN ('1', '2') -- 配達担当者実績更新区分：1担当者／2担当者・配達順
       AND delivered_rec.DLVRYKBN <> '3'            -- 持ち帰り以外
       THEN
       IF ( delivered_rec.DLVRYKBN IS NOT NULL AND iTokuisaki.DLVRYKBN <> delivered_rec.DLVRYKBN ) -- 配達区分変更
          OR ( delivered_rec.DLVRYCMPNYCD IS NOT NULL AND iTokuisaki.DLVRYCMPNYCD <> delivered_rec.DLVRYCMPNYCD ) -- 配達会社コード変更
          OR ( delivered_rec.DLVRYTNSHACD IS NOT NULL AND iTokuisaki.DLVRYTNSHACD <> delivered_rec.DLVRYTNSHACD ) -- 配達担当者コード変更
          OR ( delivered_rec.DLVRYKBN IS NOT NULL AND iTokuisaki.DLVRYKBN IS NULL )
          OR ( delivered_rec.DLVRYCMPNYCD IS NOT NULL AND iTokuisaki.DLVRYCMPNYCD IS NULL )
          OR ( delivered_rec.DLVRYTNSHACD IS NOT NULL AND iTokuisaki.DLVRYTNSHACD IS NULL)
          THEN
          isDlvryInfoChanged := '1';
       END IF;
    END IF;
    IF iDlvrytnshakshnkbn = '2' THEN -- 実績担当者更新区分：担当者・配達順
       IF ( delivered_rec.DLVRYJUN IS NOT NULL AND iTokuisaki.DLVRYJUN <> delivered_rec.DLVRYJUN ) -- 配達順変更
          OR ( delivered_rec.DLVRYJUN IS NOT NULL AND iTokuisaki.DLVRYJUN IS NULL )
          THEN
          isDlvryjunChanged := '1';
       END IF;
    END IF;

    -- 状態２：入会の場合
    IF iTokuisaki.STARTUP = '1' AND iTokuisaki.TOKUISAKIJOTAI = '1'
       AND delivered_rec.ACPTKBN = '2' AND delivered_rec.ACPTKBNSHS = '22'
       THEN
       isNyukai := '1';
    END IF;

    -- 状態３：初回サーバー設置
    IF iTokuisaki.SHOKAIWSVSTUPDD IS NULL THEN
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
       AND iTokuisaki.TAIKAIDD IS NOT NULL
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
      IF iTokuisaki.TOKUISAKIKBN = '1' THEN
         -- 入会の場合：自動採番
         IF isNyukai = '1' AND iTokuisaki.KAIINCD IS NULL THEN
            -- 管理番号の採番
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

    -- 得意先マスタ更新
    IF isDlvryInfoChanged = '1' OR isDlvryjunChanged = '1' OR isNyukai = '1' OR isShokaisvstup = '1' OR isSvhikiage = '1' THEN
       UPDATE S02M013T TKSM
          SET TKSM.TOKUISAKIJOTAI    = ( CASE WHEN isNyukai = '1' THEN '5'
                                              WHEN isShokaisvstup = '1' THEN '3'
                                            -- ACCIS2023-5699 start ABI
                                            -- 引上で得意先状態が5の場合、6
                                              WHEN isSvhikiage = '1' AND TKSM.TOKUISAKIJOTAI = '5' THEN '6'
                                            -- ACCIS2023-5699 end ABI
                                            ELSE TKSM.TOKUISAKIJOTAI
                                      END ) -- 得意先状態：本会員 ※２の場合
            , TKSM.JOTAISTS       = ( CASE WHEN isNyukai = '1' THEN '07'
                                           WHEN isSvhikiage = '1' THEN
                                           ( CASE WHEN (NVL(iTokuisakiJST.GENZAIZD, 0) + delivered_rec.NOHINKIN + delivered_rec.NOHINSHOHITAXKIN) <> 0
                                                  THEN ( CASE WHEN TKSM.TOKUISAKIJOTAI = '4' THEN '05' -- 仮会員退会(未払い有)
                                                              -- ACCIS2023-5699 start ABI
                                                              -- 得意先状態が5の場合、09
                                                              -- WHEN TKSM.TOKUISAKIJOTAI = '6' THEN '09' -- 退会（未払い有）
                                                              WHEN TKSM.TOKUISAKIJOTAI = '5' THEN '09' -- 退会（未払い有）
                                                              -- ACCIS2023-5699 end ABI
                                                              ELSE TKSM.JOTAISTS
                                                         END )
                                                  ELSE ( CASE WHEN TKSM.TOKUISAKIJOTAI = '4' THEN '06' -- 仮会員退会
                                                              -- ACCIS2023-5699 start ABI
                                                              -- 得意先状態が5の場合、10
                                                              -- WHEN TKSM.TOKUISAKIJOTAI = '6' THEN '10' -- 退会
                                                              WHEN TKSM.TOKUISAKIJOTAI = '5' THEN '10' -- 退会
                                                              -- ACCIS2023-5699 end ABI
                                                              ELSE TKSM.JOTAISTS
                                                         END )
                                             END )
                                           WHEN isShokaisvstup = '1' THEN '03'
                                           ELSE TKSM.JOTAISTS
                                      END ) -- 状態ステータス：継続中 ※２の場合
            , TKSM.NYUKAIDD       = ( CASE WHEN isNyukai = '1' THEN work_rec.DLVRYJISSEKIDD
                                           ELSE TKSM.NYUKAIDD
                                      END ) -- 入会日 ※２の場合、値を設定する
            , TKSM.SHOKAIWSVSTUPDD      = ( CASE WHEN isShokaisvstup = '1' THEN work_rec.DLVRYJISSEKIDD
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
            , TKSM.WSVHIKIAGEDD            = ( CASE WHEN isSvhikiage = '1' THEN work_rec.DLVRYJISSEKIDD
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
            , TKSM.DLVRYCMPNYCD =  ( CASE WHEN isDlvryInfoChanged = '1' THEN delivered_rec.DLVRYCMPNYCD
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
            , TKSM.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
            , TKSM.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
            , TKSM.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
            , TKSM.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
            , TKSM.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
            , TKSM.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
            , TKSM.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
            , TKSM.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
        WHERE TKSM.APKIHONCD   = iTokuisaki.APKIHONCD
          AND TKSM.SHOPCD      = iTokuisaki.SHOPCD
          AND TKSM.TOKUISAKICD = iTokuisaki.TOKUISAKICD
          AND TKSM.YUKOKBN = '1'
       ;
    END IF;

    -- 新規受注配達の場合：休眠区分更新
    IF iTokuisaki.KYUMINKBN = '1' THEN
       UPDATE S02M013T TKSM
          SET TKSM.KYUMINKBN = '0'
            /* 更新者：更新なし */
        WHERE TKSM.APKIHONCD   = iTokuisaki.APKIHONCD
          AND TKSM.SHOPCD      = iTokuisaki.SHOPCD
          AND TKSM.TOKUISAKICD = iTokuisaki.TOKUISAKICD
          AND TKSM.YUKOKBN = '1'
       ;
    END IF;

    -- 得意先実績テーブル
    UPDATE S02M050T TKJT
-- インボイス対応変更 start ABI
       --SET TKJT.GENZAIZD = ( NVL(TKJT.GENZAIZD, 0) + delivered_rec.NOHINKIN + delivered_rec.NOHINSHOHITAXKIN ) -- 現在残高
       --, TKJT.KEIJOMAEACPTKIN = ( NVL(TKJT.KEIJOMAEACPTKIN, 0) - delivered_rec.ACPTKIN - delivered_rec.ACPTSHOHITAXKIN ) -- 計上前受注金額
       SET TKJT.GENZAIZD = ( CASE WHEN delivered_rec.ACPTTHDAIBIKIFLG = '1' OR delivered_rec.SINGLDNPYFLG = '1' THEN -- 代引きの場合
                                              NVL(TKJT.GENZAIZD, 0) + delivered_rec.NOHINKIN + delivered_rec.NOHINSHOHITAXKIN
                                            ELSE
                                              CASE WHEN cmnInvoiceshikoymdflg = '0' THEN -- インボイス施行前の場合
                                                NVL(TKJT.GENZAIZD, 0) + delivered_rec.NOHINKIN + delivered_rec.NOHINSHOHITAXKIN
                                              ELSE -- インボイス施行後
                                                NVL(TKJT.GENZAIZD, 0) + delivered_rec.NOHINKIN
                                              END
                                            END ) -- 現在残高

         , TKJT.KEIJOMAEACPTKIN = ( CASE WHEN delivered_rec.ACPTTHDAIBIKIFLG = '1' OR delivered_rec.SINGLDNPYFLG = '1' THEN -- 代引きの場合
                                                        NVL(TKJT.KEIJOMAEACPTKIN, 0) - delivered_rec.ACPTKIN - delivered_rec.ACPTSHOHITAXKIN
                                                      ELSE
                                                        CASE WHEN cmnInvoiceshikoymdflg = '0' THEN -- インボイス施行前の場合
                                                          NVL(TKJT.KEIJOMAEACPTKIN, 0) - delivered_rec.ACPTKIN - delivered_rec.ACPTSHOHITAXKIN
                                                        ELSE -- インボイス施行後
                                                          NVL(TKJT.KEIJOMAEACPTKIN, 0) - delivered_rec.ACPTKIN
                                                        END
                                                     END) -- 計上前受注金額
-- インボイス対応変更 end ABI
         , TKJT.NYKGSHOKAIDLVRYDNPYNO = ( CASE WHEN TKJT.NYKGSHOKAIDLVRYDNPYNO IS NULL
                                                    AND ( iTokuisaki.NYUKAIDD IS NOT NULL OR isNyukai = '1' )
                                               THEN work_rec.ACPTDNPYNO
                                               ELSE TKJT.NYKGSHOKAIDLVRYDNPYNO
                                          END )-- 入会後初回配達伝票番号
         , TKJT.NKGSKDLVRYDNPYEDANO = ( CASE WHEN TKJT.NKGSKDLVRYDNPYEDANO IS NULL
                                                  AND ( iTokuisaki.NYUKAIDD IS NOT NULL OR isNyukai = '1' )
                                             THEN work_rec.EDANO
                                             ELSE TKJT.NKGSKDLVRYDNPYEDANO
                                        END ) -- 入会後初回配達伝票枝番号
         , TKJT.NYKGSHOKAIDLVRYDD  = ( CASE WHEN TKJT.NYKGSHOKAIDLVRYDD IS NULL
                                                 AND ( iTokuisaki.NYUKAIDD IS NOT NULL OR isNyukai = '1' )
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
         , TKJT.AZUKARIBTLN1N2RITTORUHNS = ( CASE WHEN ( TKJT.AZUKARIBTLN1N2RITTORUHNS + sum_rec.RGRASURYO - delivered_rec.KZBTLN1N2RITTORUHNS ) < 0 THEN 0
                                                  ELSE TKJT.AZUKARIBTLN1N2RITTORUHNS + sum_rec.RGRASURYO - delivered_rec.KZBTLN1N2RITTORUHNS
                                             END ) -- 預かりボトル１２Ｌ本数
         , TKJT.AZUKARIBTLN8RITTORUHNS = ( CASE WHEN ( TKJT.AZUKARIBTLN8RITTORUHNS + sum_rec.SRMSURYO - delivered_rec.KZBTLN8RITTORUHNS ) < 0 THEN 0
                                                ELSE TKJT.AZUKARIBTLN8RITTORUHNS + sum_rec.SRMSURYO - delivered_rec.KZBTLN8RITTORUHNS
                                           END ) -- 預かりボトル８Ｌ本数
         /* 更新者：更新なし */
     WHERE TKJT.APKIHONCD   = iTokuisaki.APKIHONCD
       AND TKJT.SHOPCD      = iTokuisaki.SHOPCD
       AND TKJT.TOKUISAKICD = iTokuisaki.TOKUISAKICD
       AND TKJT.YUKOKBN = '1'
    ;

    -- 得意先違約金管理テーブル
    IF iTokuisaki.STARTUP = '1' AND iTokuisaki.TOKUISAKIJOTAI = '1'
       AND delivered_rec.ACPTKBN = '2' AND delivered_rec.ACPTKBNSHS = '22'
       THEN
       isIyakukin := '1';
    END IF;
    IF iTokuisaki.IYAKUKINKANRIFLG = '1'
       AND isIyakukin = '1'
       THEN
       -- 違約金管理テーブルにデータ存在チェック
       SELECT COUNT(IYKT.APKIHONCD)
         INTO nIyakukin
         FROM S03T003T IYKT
        WHERE IYKT.APKIHONCD = iTokuisaki.APKIHONCD
          AND IYKT.SHOPCD = iTokuisaki.SHOPCD
          AND IYKT.TOKUISAKICD = iTokuisaki.TOKUISAKICD
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
             WHERE IYKT.APKIHONCD = iTokuisaki.APKIHONCD
               AND IYKT.SHOPCD = iTokuisaki.SHOPCD
               AND IYKT.TOKUISAKICD = iTokuisaki.TOKUISAKICD
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
              iTokuisaki.APKIHONCD -- ＡＰ基本コード
            , iTokuisaki.SHOPCD -- ショップコード
            , iTokuisaki.TOKUISAKICD -- 得意先コード
            , NVL(saiban,0) + 1 -- 違約金管理番号
            , '0'  -- 違約金種別：料金プランコード
            , '01' -- 料金プランコード：基本プラン
            , NULL -- サーバー機種コード
            -- 違約金適用開始年月
            , SUBSTRB(( CASE WHEN isNyukai = '1' THEN work_rec.DLVRYJISSEKIDD
                             ELSE iTokuisaki.NYUKAIDD
                        END ), 1, 6)
            , TO_CHAR(ADD_MONTHS( TO_DATE(SUBSTRB(( CASE WHEN isNyukai = '1' THEN work_rec.DLVRYJISSEKIDD
                                            ELSE iTokuisaki.NYUKAIDD
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
          processKaiinNyukai(iTokuisaki.APKIHONCD, iTokuisaki.SHOPCD, iTokuisaki.TOKUISAKICD);
       END IF;
       -- 会員履歴作成
       IF isDlvryInfoChanged = '1' OR isNyukai = '1' OR isShokaisvstup = '1' OR isSvhikiage = '1' THEN
-- 本線対応変更 start ABI
--          S0168Q00.createKaiinRireki( iTokuisaki.APKIHONCD   -- ＡＰ基本コード
          Z0509B01.createKaiinRireki( iTokuisaki.APKIHONCD   -- ＡＰ基本コード
-- 本線対応変更 end ABI
                                    , iTokuisaki.SHOPCD      -- ショップコード
                                    , iTokuisaki.TOKUISAKICD -- 得意先コード
                                    , NULL -- 変更前紹介得意先コード
                                    , iTokuisaki.SKYSAKICD   -- 請求先コード
                                    , userinfo_rec.APKIHONCD -- 担当者ＡＰ基本コード
                                    , userinfo_rec.SHOPCD    -- 担当者ショップコード
                                    , userinfo_rec.TNSHACD   -- 担当者コード
                                    , userinfo_rec.IPADRSE   -- ＩＰアドレス
                                    , 'S0512W00' -- プログラムＩＤ：配達実績登録
                                    );
       END IF;
    END IF;

    EXCEPTION
      WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20019, SQLERRM || '配達済得意先・会員一連更新時エラーが発生しました。');
  END processTokuisakiDlvry;

-- 本線対応追加 start ABI
  /****************************************************************************/
  /* プロシージャ名 ：配達実績得意先一連WEB処理                               */
  /* 引数           ：iTokuisaki           得意先マスタ                       */
  /*                ：iTokuisakiJST        得意先実績テーブル                 */
  /*                ：iDlvrytnshakshnkbn   ショップマスタ.実績担当者更新区分  */
  /*                ：work_rec             配達実績ワークデータ               */
  /*                ：sum_rec              合計データ                         */
  /****************************************************************************/
  PROCEDURE processTokuisakiDlvryWeb( iTokuisaki         IN S02M013T%rowtype -- 得意先マスタ
                                    , iTokuisakiJST      IN S02M050T%rowtype -- 得意先実績テーブル
                                    , iDlvrytnshakshnkbn IN VARCHAR2         -- ショップマスタ.実績担当者更新区分
                                    , work_rec           IN S05W001T%rowtype -- 配達実績ワークデータ
                                    , sum_rec            IN sum_rec_type     -- 合計データ
                                    , before_webDnpySts  IN VARCHAR2         -- 更新前_WEB伝票ステータス
                                    , sum_rec_cancel     IN sum_rec_cancel_type -- 合計データ(キャンセル用)
  ) IS
  /* 変数宣言 */
  delivered_rec      S04T001T%rowtype := NULL; -- 更新後受注配達データ
  isDlvryInfoChanged VARCHAR2(1) := '0'; -- 配達情報変更フラグ
  isDlvryjunChanged  VARCHAR2(1) := '0'; -- 配達順変更フラグ
  isNyukai           VARCHAR2(1) := '0'; -- 入会フラグ
  isShokaisvstup     VARCHAR2(1) := '0'; -- 初回サーバー設置フラグ
  isSvhikiage        VARCHAR2(1) := '0'; -- サーバー引上フラグ
  isIyakukin         VARCHAR2(1) := '0'; -- 違約金更新フラグ
  nServermei NUMBER := 0; -- サーバー明細行数
  nIyakukin  NUMBER := 0; -- 違約金
  saiban     NUMBER := 0; -- 違約金管理番号採番
  nIyakukinkikan NUMBER := 0; -- 違約金適用期間
  nIyakukinkin   NUMBER := 0; -- 違約金金額
  -- 会員一連更新用変数
  doKaiin    VARCHAR2(1)  := '0';  -- 会員作成有無
  cdKaiin    VARCHAR2(10) := NULL; -- 会員コード（自動採番）

  genzaizdUrikin        NUMBER := 0;   -- 売上金額
  genzaizdShohitaxkin   NUMBER := 0;   -- 消費税金額

  BEGIN
    -- 更新後受注配達データ取得
    SELECT JHTT.*
      INTO delivered_rec
      FROM S04T001T JHTT
     WHERE JHTT.APKIHONCD  = work_rec.APKIHONCD
       AND JHTT.SHOPCD     = work_rec.SHOPCD
       AND JHTT.ACPTDNPYNO = work_rec.ACPTDNPYNO
       AND JHTT.EDANO      = work_rec.EDANO
       AND JHTT.YUKOKBN = '1'
    ;
    -- 状態１：配達情報変更の場合
    IF iDlvrytnshakshnkbn IN ('1', '2') -- 配達担当者実績更新区分：1担当者／2担当者・配達順
       AND delivered_rec.DLVRYKBN <> '3'            -- 持ち帰り以外
       THEN
       IF ( delivered_rec.DLVRYKBN IS NOT NULL AND iTokuisaki.DLVRYKBN <> delivered_rec.DLVRYKBN ) -- 配達区分変更
          OR ( delivered_rec.DLVRYCMPNYCD IS NOT NULL AND iTokuisaki.DLVRYCMPNYCD <> delivered_rec.DLVRYCMPNYCD ) -- 配達会社コード変更
          OR ( delivered_rec.DLVRYTNSHACD IS NOT NULL AND iTokuisaki.DLVRYTNSHACD <> delivered_rec.DLVRYTNSHACD ) -- 配達担当者コード変更
          OR ( delivered_rec.DLVRYKBN IS NOT NULL AND iTokuisaki.DLVRYKBN IS NULL )
          OR ( delivered_rec.DLVRYCMPNYCD IS NOT NULL AND iTokuisaki.DLVRYCMPNYCD IS NULL )
          OR ( delivered_rec.DLVRYTNSHACD IS NOT NULL AND iTokuisaki.DLVRYTNSHACD IS NULL)
          THEN
          isDlvryInfoChanged := '1';
       END IF;
    END IF;
    IF iDlvrytnshakshnkbn = '2' THEN -- 実績担当者更新区分：担当者・配達順
       IF ( delivered_rec.DLVRYJUN IS NOT NULL AND iTokuisaki.DLVRYJUN <> delivered_rec.DLVRYJUN ) -- 配達順変更
          OR ( delivered_rec.DLVRYJUN IS NOT NULL AND iTokuisaki.DLVRYJUN IS NULL )
          THEN
          isDlvryjunChanged := '1';
       END IF;
    END IF;

    -- 状態２：入会の場合
    IF iTokuisaki.STARTUP = '1' AND iTokuisaki.TOKUISAKIJOTAI = '1'
       AND delivered_rec.ACPTKBN = '2' AND delivered_rec.ACPTKBNSHS = '22'
       THEN
       isNyukai := '1';
    END IF;

    -- 状態３：初回サーバー設置
    IF iTokuisaki.SHOKAIWSVSTUPDD IS NULL THEN
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
       AND iTokuisaki.TAIKAIDD IS NOT NULL
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
      IF iTokuisaki.TOKUISAKIKBN = '1' THEN
         -- 入会の場合：自動採番
         IF isNyukai = '1' AND iTokuisaki.KAIINCD IS NULL THEN
            -- 管理番号の採番
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

    -- 得意先マスタ更新
    IF isDlvryInfoChanged = '1' OR isDlvryjunChanged = '1' OR isNyukai = '1' OR isShokaisvstup = '1' OR isSvhikiage = '1' THEN
       UPDATE S02M013T TKSM
          SET TKSM.TOKUISAKIJOTAI    = ( CASE WHEN isNyukai = '1' THEN '5'
                                              WHEN isShokaisvstup = '1' THEN '3'
                                            -- ACCIS2023-5699 start ABI
                                            -- 引上で得意先状態が5の場合、6
                                              WHEN isSvhikiage = '1' AND TKSM.TOKUISAKIJOTAI = '5' THEN '6'
                                            -- ACCIS2023-5699 end ABI
                                            ELSE TKSM.TOKUISAKIJOTAI
                                      END ) -- 得意先状態：本会員 ※２の場合
            , TKSM.JOTAISTS       = ( CASE WHEN isNyukai = '1' THEN '07'
                                           WHEN isSvhikiage = '1' THEN
                                           ( CASE WHEN (NVL(iTokuisakiJST.GENZAIZD, 0) + delivered_rec.NOHINKIN + delivered_rec.NOHINSHOHITAXKIN) <> 0
                                                  THEN ( CASE WHEN TKSM.TOKUISAKIJOTAI = '4' THEN '05' -- 仮会員退会(未払い有)
                                                              -- ACCIS2023-5699 start ABI
                                                              -- 得意先状態が5の場合、09
                                                              -- WHEN TKSM.TOKUISAKIJOTAI = '6' THEN '09' -- 退会（未払い有）
                                                              WHEN TKSM.TOKUISAKIJOTAI = '5' THEN '09' -- 退会（未払い有）
                                                              -- ACCIS2023-5699 end ABI
                                                              ELSE TKSM.JOTAISTS
                                                         END )
                                                  ELSE ( CASE WHEN TKSM.TOKUISAKIJOTAI = '4' THEN '06' -- 仮会員退会
                                                              -- ACCIS2023-5699 start ABI
                                                              -- 得意先状態が5の場合、10
                                                              -- WHEN TKSM.TOKUISAKIJOTAI = '6' THEN '10' -- 退会
                                                              WHEN TKSM.TOKUISAKIJOTAI = '5' THEN '10' -- 退会
                                                              -- ACCIS2023-5699 end ABI
                                                              ELSE TKSM.JOTAISTS
                                                         END )
                                             END )
                                           WHEN isShokaisvstup = '1' THEN '03'
                                           ELSE TKSM.JOTAISTS
                                      END ) -- 状態ステータス：継続中 ※２の場合
            , TKSM.NYUKAIDD       = ( CASE WHEN isNyukai = '1' THEN work_rec.DLVRYJISSEKIDD
                                           ELSE TKSM.NYUKAIDD
                                      END ) -- 入会日 ※２の場合、値を設定する
            , TKSM.SHOKAIWSVSTUPDD      = ( CASE WHEN isShokaisvstup = '1' THEN work_rec.DLVRYJISSEKIDD
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
            , TKSM.WSVHIKIAGEDD            = ( CASE WHEN isSvhikiage = '1' THEN work_rec.DLVRYJISSEKIDD
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
            , TKSM.DLVRYCMPNYCD =  ( CASE WHEN isDlvryInfoChanged = '1' THEN delivered_rec.DLVRYCMPNYCD
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
            , TKSM.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
            , TKSM.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
            , TKSM.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
            , TKSM.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
            , TKSM.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
            , TKSM.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
            , TKSM.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
            , TKSM.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
        WHERE TKSM.APKIHONCD   = iTokuisaki.APKIHONCD
          AND TKSM.SHOPCD      = iTokuisaki.SHOPCD
          AND TKSM.TOKUISAKICD = iTokuisaki.TOKUISAKICD
          AND TKSM.YUKOKBN = '1'
       ;
    END IF;

    -- 新規受注配達の場合：休眠区分更新
    IF iTokuisaki.KYUMINKBN = '1' THEN
       UPDATE S02M013T TKSM
          SET TKSM.KYUMINKBN = '0'
            /* 更新者：更新なし */
        WHERE TKSM.APKIHONCD   = iTokuisaki.APKIHONCD
          AND TKSM.SHOPCD      = iTokuisaki.SHOPCD
          AND TKSM.TOKUISAKICD = iTokuisaki.TOKUISAKICD
          AND TKSM.YUKOKBN = '1'
       ;
    END IF;

    -- 得意先実績テーブル

    -- @. 画面.未配達区分が'3'(配達キャンセル)以外の場合（C0815W11DlvryJissekiDto.getDlvrysts()で、未配達区分の値を条件として配達ステータスを設定）
    IF work_rec.DLVRYSTS <> '19' THEN

        -- @-1.「更新前の受注配達テーブル(S04T001T).伝票ステータス ≠ '12'」の場合
        IF before_webDnpySts <> '12' THEN

            UPDATE S02M050T TKJT
                -- インボイス対応変更 start ABI
                --SET TKJT.GENZAIZD = ( NVL(TKJT.GENZAIZD, 0) + delivered_rec.NOHINKIN + delivered_rec.NOHINSHOHITAXKIN ) -- 現在残高
                --, TKJT.KEIJOMAEACPTKIN = ( NVL(TKJT.KEIJOMAEACPTKIN, 0) - delivered_rec.ACPTKIN - delivered_rec.ACPTSHOHITAXKIN ) -- 計上前受注金額
                SET TKJT.GENZAIZD = ( CASE WHEN delivered_rec.ACPTTHDAIBIKIFLG = '1' OR delivered_rec.SINGLDNPYFLG = '1' THEN -- 代引きの場合
                                                       NVL(TKJT.GENZAIZD, 0) + delivered_rec.NOHINKIN + delivered_rec.NOHINSHOHITAXKIN
                                                     ELSE
                                                       CASE WHEN cmnInvoiceshikoymdflg = '0' THEN -- インボイス施行前の場合
                                                         NVL(TKJT.GENZAIZD, 0) + delivered_rec.NOHINKIN + delivered_rec.NOHINSHOHITAXKIN
                                                       ELSE -- インボイス施行後
                                                         NVL(TKJT.GENZAIZD, 0) + delivered_rec.NOHINKIN
                                                       END
                                                     END ) -- 現在残高

                  , TKJT.KEIJOMAEACPTKIN = ( CASE WHEN delivered_rec.ACPTTHDAIBIKIFLG = '1' OR delivered_rec.SINGLDNPYFLG = '1' THEN -- 代引きの場合
                                                                 NVL(TKJT.KEIJOMAEACPTKIN, 0) - delivered_rec.ACPTKIN - delivered_rec.ACPTSHOHITAXKIN
                                                               ELSE
                                                                 CASE WHEN cmnInvoiceshikoymdflg = '0' THEN -- インボイス施行前の場合
                                                                   NVL(TKJT.KEIJOMAEACPTKIN, 0) - delivered_rec.ACPTKIN - delivered_rec.ACPTSHOHITAXKIN
                                                                 ELSE -- インボイス施行後
                                                                   NVL(TKJT.KEIJOMAEACPTKIN, 0) - delivered_rec.ACPTKIN
                                                                 END
                                                              END) -- 計上前受注金額
                -- インボイス対応変更 end ABI
                  , TKJT.NYKGSHOKAIDLVRYDNPYNO = ( CASE WHEN TKJT.NYKGSHOKAIDLVRYDNPYNO IS NULL
                                                             AND ( iTokuisaki.NYUKAIDD IS NOT NULL OR isNyukai = '1' )
                                                        THEN work_rec.ACPTDNPYNO
                                                        ELSE TKJT.NYKGSHOKAIDLVRYDNPYNO
                                                   END )-- 入会後初回配達伝票番号
                  , TKJT.NKGSKDLVRYDNPYEDANO = ( CASE WHEN TKJT.NKGSKDLVRYDNPYEDANO IS NULL
                                                           AND ( iTokuisaki.NYUKAIDD IS NOT NULL OR isNyukai = '1' )
                                                      THEN work_rec.EDANO
                                                      ELSE TKJT.NKGSKDLVRYDNPYEDANO
                                                 END ) -- 入会後初回配達伝票枝番号
                  , TKJT.NYKGSHOKAIDLVRYDD  = ( CASE WHEN TKJT.NYKGSHOKAIDLVRYDD IS NULL
                                                          AND ( iTokuisaki.NYUKAIDD IS NOT NULL OR isNyukai = '1' )
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
                  , TKJT.AZUKARIBTLN1N2RITTORUHNS = ( CASE WHEN ( TKJT.AZUKARIBTLN1N2RITTORUHNS + sum_rec.RGRASURYO - delivered_rec.KZBTLN1N2RITTORUHNS ) < 0 THEN 0
                                                           ELSE TKJT.AZUKARIBTLN1N2RITTORUHNS + sum_rec.RGRASURYO - delivered_rec.KZBTLN1N2RITTORUHNS
                                                      END ) -- 預かりボトル１２Ｌ本数
                  , TKJT.AZUKARIBTLN8RITTORUHNS = ( CASE WHEN ( TKJT.AZUKARIBTLN8RITTORUHNS + sum_rec.SRMSURYO - delivered_rec.KZBTLN8RITTORUHNS ) < 0 THEN 0
                                                         ELSE TKJT.AZUKARIBTLN8RITTORUHNS + sum_rec.SRMSURYO - delivered_rec.KZBTLN8RITTORUHNS
                                                    END ) -- 預かりボトル８Ｌ本数
                  /* 更新者：更新なし */
             WHERE TKJT.APKIHONCD   = iTokuisaki.APKIHONCD
                 AND TKJT.SHOPCD      = iTokuisaki.SHOPCD
                 AND TKJT.TOKUISAKICD = iTokuisaki.TOKUISAKICD
                 AND TKJT.YUKOKBN = '1'
            ;

        END IF;  -- @-1 END

        -- @-2.「更新前の受注配達テーブル(S04T001T).伝票ステータス ＝ '12'」の場合
        IF before_webDnpySts = '12' THEN

            -- 得意先実績テーブル現在残高：取消（売上伝票番号（赤）が存在する場合）
            IF cmnAkauridnpyno IS NOT NULL THEN
                SELECT
                  NVL(URI.URIKIN, 0)
                  , NVL(URI.SHOHITAXKIN, 0)
                INTO
                  genzaizdUrikin
                  , genzaizdShohitaxkin
                FROM S06T001T URI
                WHERE URI.APKIHONCD = iTokuisaki.APKIHONCD
                  AND URI.SHOPCD = iTokuisaki.SHOPCD
                  AND URI.URIDNPYNO = cmnAkauridnpyno
                ;
            END IF;

            -- 得意先実績テーブル現在残高：更新（売上伝票番号（黒）が存在する場合）
            IF cmnKurouridnpyno IS NOT NULL THEN
                genzaizdUrikin       := delivered_rec.NOHINKIN;
                genzaizdShohitaxkin  := delivered_rec.NOHINSHOHITAXKIN;
            END IF;

            UPDATE S02M050T TKJT
                -- インボイス対応変更 start ABI
                --SET TKJT.GENZAIZD = ( NVL(TKJT.GENZAIZD, 0) + delivered_rec.NOHINKIN + delivered_rec.NOHINSHOHITAXKIN ) -- 現在残高
                --, TKJT.KEIJOMAEACPTKIN = ( NVL(TKJT.KEIJOMAEACPTKIN, 0) - delivered_rec.ACPTKIN - delivered_rec.ACPTSHOHITAXKIN ) -- 計上前受注金額
                SET TKJT.GENZAIZD = ( CASE WHEN delivered_rec.ACPTTHDAIBIKIFLG = '1' OR delivered_rec.SINGLDNPYFLG = '1' THEN -- 代引きの場合
                                                       NVL(TKJT.GENZAIZD, 0) + genzaizdUrikin + genzaizdShohitaxkin
                                                     ELSE
                                                       CASE WHEN cmnInvoiceshikoymdflg = '0' THEN -- インボイス施行前の場合
                                                         NVL(TKJT.GENZAIZD, 0) + genzaizdUrikin + genzaizdShohitaxkin
                                                       ELSE -- インボイス施行後
                                                         NVL(TKJT.GENZAIZD, 0) + genzaizdUrikin
                                                       END
                                                     END ) -- 現在残高
                -- インボイス対応変更 end ABI

                  , TKJT.NYKGSHOKAIDLVRYDNPYNO = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.NYKGSHOKAIDLVRYDNPYNO
                                                             AND delivered_rec.EDANO = TKJT.NKGSKDLVRYDNPYEDANO
                                                             AND cmnKurouridnpyno IS NULL
                                                          THEN null
                                                        ELSE TKJT.NYKGSHOKAIDLVRYDNPYNO
                                                   END )-- 入会後初回配達伝票番号
                  , TKJT.NKGSKDLVRYDNPYEDANO = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.NYKGSHOKAIDLVRYDNPYNO
                                                             AND delivered_rec.EDANO = TKJT.NKGSKDLVRYDNPYEDANO
                                                             AND cmnKurouridnpyno IS NULL
                                                          THEN null
                                                        ELSE TKJT.NKGSKDLVRYDNPYEDANO
                                                 END ) -- 入会後初回配達伝票枝番号
                  , TKJT.NYKGSHOKAIDLVRYDD = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.NYKGSHOKAIDLVRYDNPYNO
                                                             AND delivered_rec.EDANO = TKJT.NKGSKDLVRYDNPYEDANO
                                                             AND cmnKurouridnpyno IS NULL
                                                      THEN null
                                                    WHEN delivered_rec.ACPTDNPYNO = TKJT.NYKGSHOKAIDLVRYDNPYNO
                                                             AND delivered_rec.EDANO = TKJT.NKGSKDLVRYDNPYEDANO
                                                      THEN  delivered_rec.DLVRYDD
                                                    ELSE
                                                      TKJT.NYKGSHOKAIDLVRYDD
                                               END ) -- 入会後初回配達日

                  -- 最終受注日：新規配達受注の場合更新
                  , TKJT.SAISHUACPTDD = ( CASE WHEN delivered_rec.ACPTDD > TKJT.SAISHUACPTDD THEN delivered_rec.ACPTDD
                                               ELSE TKJT.SAISHUACPTDD
                                          END )
                  /* 前回配達情報 */
                  , TKJT.ZNKDLVRYACPTDD = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZNKDLVRYDNPYNO
                                                        THEN delivered_rec.ACPTDD
                                                        ELSE TKJT.ZNKDLVRYACPTDD
                                                 END ) -- 前回配達受注日
                  , TKJT.ZNKDLVRYDD = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZNKDLVRYDNPYNO
                                                        THEN delivered_rec.DLVRYJISSEKIDD
                                                        ELSE TKJT.ZNKDLVRYDD
                                                 END ) -- 前回配達日
                  , TKJT.ZNKDLVRYRGRAYUSYOHNS = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZNKDLVRYDNPYNO
                                                        THEN delivered_rec.DLVRYJISSEKIRGRAYUSYOHNS
                                                        ELSE TKJT.ZNKDLVRYRGRAYUSYOHNS
                                                 END ) -- 前回配達レギュラー有償本数
                  , TKJT.ZNKDLVRYRGRAMUSHOHNS = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZNKDLVRYDNPYNO
                                                        THEN delivered_rec.DLVRYJISSEKIRGRAMUSHOHNS
                                                        ELSE TKJT.ZNKDLVRYRGRAMUSHOHNS
                                                 END ) -- 前回配達レギュラー無償本数
                  , TKJT.ZNKDLVRYSRMYUSYOHNS = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZNKDLVRYDNPYNO
                                                        THEN delivered_rec.DLVRYJISSEKISRMYUSYOHNS
                                                        ELSE TKJT.ZNKDLVRYSRMYUSYOHNS
                                                 END ) -- 前回配達スリム有償本数
                  , TKJT.ZNKDLVRYSRMMUSHOHNS = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZNKDLVRYDNPYNO
                                                        THEN delivered_rec.DLVRYJISSEKISRMMUSHOHNS
                                                        ELSE TKJT.ZNKDLVRYSRMMUSHOHNS
                                                 END ) -- 前回配達スリム無償本数
                  , TKJT.ZNKDLVRYSONOTAYUSYOHNS = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZNKDLVRYDNPYNO
                                                        THEN delivered_rec.DLVRYJISSEKISTYSHNS
                                                        ELSE TKJT.ZNKDLVRYSONOTAYUSYOHNS
                                                 END ) -- 前回配達その他有償本数
                  , TKJT.ZNKDLVRYSONOTAMUSHOHNS = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZNKDLVRYDNPYNO
                                                        THEN delivered_rec.DLVRYJISSEKISTMSHNS
                                                        ELSE TKJT.ZNKDLVRYSONOTAMUSHOHNS
                                                 END ) -- 前回配達その他無償本数
                  , TKJT.ZNKDLVRYSEIHINMIZUURIKIN = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZNKDLVRYDNPYNO
                                                        THEN delivered_rec.DLVRYJISSEKISHSUAKIN
                                                        ELSE TKJT.ZNKDLVRYSEIHINMIZUURIKIN
                                                 END ) -- 前回配達製品水売上金額
                  , TKJT.ZNKDLVRYSONOTAURIKIN = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZNKDLVRYDNPYNO
                                                        THEN delivered_rec.DLVRYJISSEKISONOTAURIKIN
                                                        ELSE TKJT.ZNKDLVRYSONOTAURIKIN
                                                 END ) -- 前回配達その他売上金額
                  /* 前々回配達情報 */
                  , TKJT.ZZKAIDLVRYACPTDD = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZZKHTDNPYNO
                                                        THEN delivered_rec.ACPTDD
                                                        ELSE TKJT.ZZKAIDLVRYACPTDD
                                                 END ) -- 前々回配達受注日
                  , TKJT.ZZKHTDD = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZZKHTDNPYNO
                                                        THEN delivered_rec.DLVRYJISSEKIDD
                                                        ELSE TKJT.ZZKHTDD
                                                 END ) -- 前々回配達日
                  , TKJT.ZZKHTRGRAYUSYOHNS = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZZKHTDNPYNO
                                                        THEN delivered_rec.DLVRYJISSEKIRGRAYUSYOHNS
                                                        ELSE TKJT.ZZKHTRGRAYUSYOHNS
                                                 END ) -- 前々回配達レギュラー有償本数
                  , TKJT.ZZKHTRGRAMUSHOHNS = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZZKHTDNPYNO
                                                        THEN delivered_rec.DLVRYJISSEKIRGRAMUSHOHNS
                                                        ELSE TKJT.ZZKHTRGRAMUSHOHNS
                                                 END ) -- 前々回配達レギュラー無償本数
                  , TKJT.ZZKHTSRMYUSYOHNS = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZZKHTDNPYNO
                                                        THEN delivered_rec.DLVRYJISSEKISRMYUSYOHNS
                                                        ELSE TKJT.ZZKHTSRMYUSYOHNS
                                                 END ) -- 前々回配達スリム有償本数
                  , TKJT.ZZKHTSRMMUSHOHNS = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZZKHTDNPYNO
                                                        THEN delivered_rec.DLVRYJISSEKISRMMUSHOHNS
                                                        ELSE TKJT.ZZKHTSRMMUSHOHNS
                                                 END ) -- 前々回配達スリム無償本数
                  , TKJT.ZZKHTSONOTAYUSYOHNS = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZZKHTDNPYNO
                                                        THEN delivered_rec.DLVRYJISSEKISTYSHNS
                                                        ELSE TKJT.ZZKHTSONOTAYUSYOHNS
                                                 END ) -- 前々回配達その他有償本数
                  , TKJT.ZZKHTSONOTAMUSHOHNS = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZZKHTDNPYNO
                                                        THEN delivered_rec.DLVRYJISSEKISTMSHNS
                                                        ELSE TKJT.ZZKHTSONOTAMUSHOHNS
                                                 END ) -- 前々回配達その他無償本数
                  , TKJT.ZZKHTSEIHINMIZUURIKIN = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZZKHTDNPYNO
                                                        THEN delivered_rec.DLVRYJISSEKISHSUAKIN
                                                        ELSE TKJT.ZZKHTSEIHINMIZUURIKIN
                                                 END ) -- 前々回配達製品水売上金額
                  , TKJT.ZZKHTSONOTAURIKIN = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZZKHTDNPYNO
                                                        THEN delivered_rec.DLVRYJISSEKISONOTAURIKIN
                                                        ELSE TKJT.ZZKHTSONOTAURIKIN
                                                 END ) -- 前々回配達その他売上金額
                  /* 前々前回配達情報 */
                  , TKJT.ZZZKHTACPTDD = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZZZKHTDNPYNO
                                                        THEN delivered_rec.ACPTDD
                                                        ELSE TKJT.ZZZKHTACPTDD
                                                 END ) -- 前々前回配達受注日
                  , TKJT.ZZZKHTDD = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZZZKHTDNPYNO
                                                        THEN delivered_rec.DLVRYJISSEKIDD
                                                        ELSE TKJT.ZZZKHTDD
                                                 END ) -- 前々前回配達日
                  , TKJT.ZZZKHTRGRAYUSYOHNS = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZZZKHTDNPYNO
                                                        THEN delivered_rec.DLVRYJISSEKIRGRAYUSYOHNS
                                                        ELSE TKJT.ZZZKHTRGRAYUSYOHNS
                                                 END ) -- 前々前回配達レギュラー有償本数
                  , TKJT.ZZZKHTRGRAMUSHOHNS = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZZZKHTDNPYNO
                                                        THEN delivered_rec.DLVRYJISSEKIRGRAMUSHOHNS
                                                        ELSE TKJT.ZZZKHTRGRAMUSHOHNS
                                                 END ) -- 前々前回配達レギュラー無償本数
                  , TKJT.ZZZKHTSRMYUSYOHNS = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZZZKHTDNPYNO
                                                        THEN delivered_rec.DLVRYJISSEKISRMYUSYOHNS
                                                        ELSE TKJT.ZZZKHTSRMYUSYOHNS
                                                 END ) -- 前々前回配達スリム有償本数
                  , TKJT.ZZZKHTSRMMUSHOHNS = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZZZKHTDNPYNO
                                                        THEN delivered_rec.DLVRYJISSEKISRMMUSHOHNS
                                                        ELSE TKJT.ZZZKHTSRMMUSHOHNS
                                                 END ) -- 前々前回配達スリム無償本数
                  , TKJT.ZZZKHTSONOTAYUSYOHNS = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZZZKHTDNPYNO
                                                        THEN delivered_rec.DLVRYJISSEKISTYSHNS
                                                        ELSE TKJT.ZZZKHTSONOTAYUSYOHNS
                                                 END ) -- 前々前回配達その他有償本数
                  , TKJT.ZZZKHTSONOTAMUSHOHNS = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZZZKHTDNPYNO
                                                        THEN delivered_rec.DLVRYJISSEKISTMSHNS
                                                        ELSE TKJT.ZZZKHTSONOTAMUSHOHNS
                                                 END ) -- 前々前回配達その他無償本数
                  , TKJT.ZZZKHTSEIHINMIZUURIKIN = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZZZKHTDNPYNO
                                                        THEN delivered_rec.DLVRYJISSEKISHSUAKIN
                                                        ELSE TKJT.ZZZKHTSEIHINMIZUURIKIN
                                                 END ) -- 前々前回配達製品水売上金額
                  , TKJT.ZZZKHTSONOTAURIKIN = ( CASE WHEN delivered_rec.ACPTDNPYNO = TKJT.ZZZKHTDNPYNO
                                                        THEN delivered_rec.DLVRYJISSEKISONOTAURIKIN
                                                        ELSE TKJT.ZZZKHTSONOTAURIKIN
                                                 END ) -- 前々前回配達その他売上金額

                  , TKJT.AZUKARIBTLN1N2RITTORUHNS = ( CASE WHEN ( TKJT.AZUKARIBTLN1N2RITTORUHNS + sum_rec.RGRASURYO - sum_rec_cancel.RGRASURYO - delivered_rec.KZBTLN1N2RITTORUHNS + sum_rec_cancel.KZBTLN1N2RITTORUHNS ) < 0 THEN 0
                                                           ELSE TKJT.AZUKARIBTLN1N2RITTORUHNS + sum_rec.RGRASURYO - sum_rec_cancel.RGRASURYO - delivered_rec.KZBTLN1N2RITTORUHNS + sum_rec_cancel.KZBTLN1N2RITTORUHNS
                                                      END ) -- 預かりボトル１２Ｌ本数
                  , TKJT.AZUKARIBTLN8RITTORUHNS = ( CASE WHEN ( TKJT.AZUKARIBTLN8RITTORUHNS + sum_rec.SRMSURYO - sum_rec_cancel.SRMSURYO - delivered_rec.KZBTLN8RITTORUHNS + sum_rec_cancel.KZBTLN8RITTORUHNS ) < 0 THEN 0
                                                         ELSE TKJT.AZUKARIBTLN8RITTORUHNS + sum_rec.SRMSURYO - sum_rec_cancel.SRMSURYO - delivered_rec.KZBTLN8RITTORUHNS + sum_rec_cancel.KZBTLN8RITTORUHNS
                                                    END ) -- 預かりボトル８Ｌ本数
                  /* 更新者：更新なし */
             WHERE TKJT.APKIHONCD   = iTokuisaki.APKIHONCD
                 AND TKJT.SHOPCD      = iTokuisaki.SHOPCD
                 AND TKJT.TOKUISAKICD = iTokuisaki.TOKUISAKICD
                 AND TKJT.YUKOKBN = '1'
            ;

        END IF;  -- @-2 END
    END IF;  -- @ END

    -- A. 画面.未配達区分が'3'(配達キャンセル)の場合（C0815W11DlvryJissekiDto.getDlvrysts()で、未配達区分の値を条件として配達ステータスを設定）
    IF work_rec.DLVRYSTS = '19' THEN

        UPDATE S02M050T TKJT
           -- インボイス対応変更 start ABI
           --SET TKJT.KEIJOMAEACPTKIN = ( NVL(TKJT.KEIJOMAEACPTKIN, 0) - delivered_rec.ACPTKIN - delivered_rec.ACPTSHOHITAXKIN ) -- 計上前受注金額
           SET TKJT.KEIJOMAEACPTKIN = ( CASE WHEN delivered_rec.ACPTTHDAIBIKIFLG = '1' OR delivered_rec.SINGLDNPYFLG = '1' THEN -- 代引きの場合
                                                            NVL(TKJT.KEIJOMAEACPTKIN, 0) - delivered_rec.ACPTKIN - delivered_rec.ACPTSHOHITAXKIN
                                                          ELSE
                                                            CASE WHEN cmnInvoiceshikoymdflg = '0' THEN -- インボイス施行前の場合
                                                              NVL(TKJT.KEIJOMAEACPTKIN, 0) - delivered_rec.ACPTKIN - delivered_rec.ACPTSHOHITAXKIN
                                                            ELSE -- インボイス施行後
                                                              NVL(TKJT.KEIJOMAEACPTKIN, 0) - delivered_rec.ACPTKIN
                                                            END
                                                         END) -- 計上前受注金額
           -- インボイス対応変更 end ABI
             -- 最終受注日：新規配達受注の場合更新
             , TKJT.SAISHUACPTDD = ( CASE WHEN delivered_rec.ACPTDD > TKJT.SAISHUACPTDD THEN delivered_rec.ACPTDD
                                          ELSE TKJT.SAISHUACPTDD
                                     END )
             , TKJT.AZUKARIBTLN1N2RITTORUHNS = ( CASE WHEN ( TKJT.AZUKARIBTLN1N2RITTORUHNS + ((sum_rec_cancel.RGRASURYO - sum_rec_cancel.KZBTLN1N2RITTORUHNS) * -1) ) < 0 THEN 0
                                                      ELSE TKJT.AZUKARIBTLN1N2RITTORUHNS + ((sum_rec_cancel.RGRASURYO - sum_rec_cancel.KZBTLN1N2RITTORUHNS) * -1)
                                                 END ) -- 預かりボトル１２Ｌ本数
             , TKJT.AZUKARIBTLN8RITTORUHNS = ( CASE WHEN ( TKJT.AZUKARIBTLN8RITTORUHNS + ((sum_rec_cancel.SRMSURYO - sum_rec_cancel.KZBTLN8RITTORUHNS) * -1) ) < 0 THEN 0
                                                    ELSE TKJT.AZUKARIBTLN8RITTORUHNS + ((sum_rec_cancel.SRMSURYO - sum_rec_cancel.KZBTLN8RITTORUHNS) * -1)
                                               END ) -- 預かりボトル８Ｌ本数
             /* 更新者：更新なし */
         WHERE TKJT.APKIHONCD   = iTokuisaki.APKIHONCD
             AND TKJT.SHOPCD      = iTokuisaki.SHOPCD
             AND TKJT.TOKUISAKICD = iTokuisaki.TOKUISAKICD
             AND TKJT.YUKOKBN = '1'
        ;

    END IF;  -- A END


    -- 得意先違約金管理テーブル
    IF iTokuisaki.STARTUP = '1' AND iTokuisaki.TOKUISAKIJOTAI = '1'
       AND delivered_rec.ACPTKBN = '2' AND delivered_rec.ACPTKBNSHS = '22'
       THEN
       isIyakukin := '1';
    END IF;
    IF iTokuisaki.IYAKUKINKANRIFLG = '1'
       AND isIyakukin = '1'
       THEN
       -- 違約金管理テーブルにデータ存在チェック
       SELECT COUNT(IYKT.APKIHONCD)
         INTO nIyakukin
         FROM S03T003T IYKT
        WHERE IYKT.APKIHONCD = iTokuisaki.APKIHONCD
          AND IYKT.SHOPCD = iTokuisaki.SHOPCD
          AND IYKT.TOKUISAKICD = iTokuisaki.TOKUISAKICD
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
             WHERE IYKT.APKIHONCD = iTokuisaki.APKIHONCD
               AND IYKT.SHOPCD = iTokuisaki.SHOPCD
               AND IYKT.TOKUISAKICD = iTokuisaki.TOKUISAKICD
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
              iTokuisaki.APKIHONCD -- ＡＰ基本コード
            , iTokuisaki.SHOPCD -- ショップコード
            , iTokuisaki.TOKUISAKICD -- 得意先コード
            , NVL(saiban,0) + 1 -- 違約金管理番号
            , '0'  -- 違約金種別：料金プランコード
            , '01' -- 料金プランコード：基本プラン
            , NULL -- サーバー機種コード
            -- 違約金適用開始年月
            , SUBSTRB(( CASE WHEN isNyukai = '1' THEN work_rec.DLVRYJISSEKIDD
                             ELSE iTokuisaki.NYUKAIDD
                        END ), 1, 6)
            , TO_CHAR(ADD_MONTHS( TO_DATE(SUBSTRB(( CASE WHEN isNyukai = '1' THEN work_rec.DLVRYJISSEKIDD
                                            ELSE iTokuisaki.NYUKAIDD
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
          processKaiinNyukai(iTokuisaki.APKIHONCD, iTokuisaki.SHOPCD, iTokuisaki.TOKUISAKICD);
       END IF;
       -- 会員履歴作成
       IF isDlvryInfoChanged = '1' OR isNyukai = '1' OR isShokaisvstup = '1' OR isSvhikiage = '1' THEN
-- 本線対応変更 start ABI
--          S0168Q00.createKaiinRireki( iTokuisaki.APKIHONCD   -- ＡＰ基本コード
          Z0509B01.createKaiinRireki( iTokuisaki.APKIHONCD   -- ＡＰ基本コード
-- 本線対応変更 end ABI
                                    , iTokuisaki.SHOPCD      -- ショップコード
                                    , iTokuisaki.TOKUISAKICD -- 得意先コード
                                    , NULL -- 変更前紹介得意先コード
                                    , iTokuisaki.SKYSAKICD   -- 請求先コード
                                    , userinfo_rec.APKIHONCD -- 担当者ＡＰ基本コード
                                    , userinfo_rec.SHOPCD    -- 担当者ショップコード
                                    , userinfo_rec.TNSHACD   -- 担当者コード
                                    , userinfo_rec.IPADRSE   -- ＩＰアドレス
                                    , 'S0512W00' -- プログラムＩＤ：配達実績登録
                                    );
       END IF;
    END IF;

    EXCEPTION
      WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20019, SQLERRM || '配達済得意先・会員一連更新時エラーが発生しました。');
  END processTokuisakiDlvryWeb;
-- 本線対応追加 end ABI

  /****************************************************************************/
  /* プロシージャ名 ：キャンセル時得意先休眠処理                                     */
  /* 引数             ：iTokuisakiMST      得意先マスタ                          */
  /*                  ：iTokuisakiJST      得意先実績テーブル                    */
  /*                  ：work_rec           配達実績ワークデータ                   */
  /*****************************************************************************/
  PROCEDURE processTokuisakiCancel( iTokuisakiMST IN S02M013T%rowtype -- 得意先マスタ
                                  , iTokuisakiJST IN S02M050T%rowtype -- 得意先実績テーブル
                                  , work_rec      IN S05W001T%rowtype -- 配達実績ワークデータ
  ) IS
  /* 変数宣言 */
  canceled_rec      S04T001T%rowtype := NULL; -- 更新後受注配達データ
  wCount            NUMBER(2,0); -- 休眠対象得意先判定（処理中伝票数）
  wKyuminJudgeCnt   S02M006T.SUCHISETTEIATAI%TYPE;  -- 得意先休眠判定日数
  wKyuminStDate     S02M050T.SAISHUACPTDD%type;     -- 休眠開始日
  wMaxAcptDd        S04T001T.ACPTDD%TYPE;           -- 最終受注日

  BEGIN
    -- 更新後受注配達データ取得
    SELECT JHTT.*
      INTO canceled_rec
      FROM S04T001T JHTT
     WHERE JHTT.APKIHONCD  = work_rec.APKIHONCD
       AND JHTT.SHOPCD     = work_rec.SHOPCD
       AND JHTT.ACPTDNPYNO = work_rec.ACPTDNPYNO
       AND JHTT.EDANO      = work_rec.EDANO
       AND JHTT.YUKOKBN = '1'
    ;
    -- 最終受注日の取得
    SELECT NVL(MAX(JHTT.ACPTDD), '00000000')
      INTO wMaxAcptDd
      FROM S04T001T JHTT
     WHERE JHTT.APKIHONCD   = iTokuisakiMST.APKIHONCD
       AND JHTT.SHOPCD      = iTokuisakiMST.SHOPCD
       AND JHTT.TOKUISAKICD = iTokuisakiMST.TOKUISAKICD
       AND JHTT.ACPTDNPYNO <> work_rec.ACPTDNPYNO
       AND JHTT.DNPYSTS    IN ('02','03','11','12') -- 伝票ステータス：受注／再配達／配達中
       AND JHTT.YUKOKBN     = '1'
    ;

    -- 休眠対象得意先更新
    IF iTokuisakiJST.ZNKDLVRYDNPYNO IS NOT NULL -- １回以上配達実績がある
       AND iTokuisakiMST.SKYSAKICD IS NOT NULL -- 請求先あり
       AND iTokuisakiMST.TOKUISAKIKBN = '1' -- 得意先区分：顧客
       AND iTokuisakiMST.TOKUISAKIJOTAI IN ('3', '5') -- 得意先状態：仮会員／本会員
       THEN

       SELECT COUNT(*)
         INTO wCount
         FROM S04T001T JHTT
        WHERE JHTT.APKIHONCD   = iTokuisakiMST.APKIHONCD
          AND JHTT.SHOPCD      = iTokuisakiMST.SHOPCD
          AND JHTT.TOKUISAKICD = iTokuisakiMST.TOKUISAKICD
          AND JHTT.ACPTDNPYNO <> work_rec.ACPTDNPYNO -- 処理中伝票以外
          AND JHTT.DNPYSTS    IN ('02','03','11') -- 伝票ステータス：受注／再配達／配達中
          AND JHTT.YUKOKBN     = '1'
       ;
       -- 未納受注が１件もないの場合のみは休眠対象
       IF wCount = 0 THEN
          -- システム管理マスタから休眠判定日数を取得
          wKyuminJudgeCnt := getSuchisetteiatai( '000', '000', '120', '得意先休眠判定日数' );
          IF wKyuminJudgeCnt IS NOT NULL AND wKyuminJudgeCnt > 0 THEN
             -- 休眠判定日付の算出
             SELECT TO_CHAR(TO_DATE(userinfo_rec.UNYOYMD, 'YYYYMMDD') - wKyuminJudgeCnt, 'YYYYMMDD')
               INTO wKyuminStDate
               FROM dual;

             -- 休眠更新
             IF wMaxAcptDd < wKyuminStDate THEN
                UPDATE S02M013T TKSM
                   SET TKSM.KYUMINKBN = '1'
                     /* 更新者：更新なし */
                 WHERE TKSM.APKIHONCD   = iTokuisakiMST.APKIHONCD
                   AND TKSM.SHOPCD      = iTokuisakiMST.SHOPCD
                   AND TKSM.TOKUISAKICD = iTokuisakiMST.TOKUISAKICD
                   AND TKSM.YUKOKBN = '1'
                ;
             END IF;
          END IF;
       END IF;
    END IF;

    -- 得意先実績テーブル
    UPDATE S02M050T TKJT
-- インボイス対応変更 start ABI
       --SET TKJT.KEIJOMAEACPTKIN = ( NVL(TKJT.KEIJOMAEACPTKIN, 0) - canceled_rec.ACPTKIN - canceled_rec.ACPTSHOHITAXKIN ) -- 計上前受注金額
       SET TKJT.KEIJOMAEACPTKIN = ( CASE WHEN canceled_rec.ACPTTHDAIBIKIFLG = '1' OR canceled_rec.SINGLDNPYFLG = '1' THEN -- 代引きの場合
                                                        NVL(TKJT.KEIJOMAEACPTKIN, 0) - canceled_rec.ACPTKIN - canceled_rec.ACPTSHOHITAXKIN
                                                      ELSE
                                                        CASE WHEN cmnInvoiceshikoymdflg = '0' THEN -- インボイス施行前の場合
                                                          NVL(TKJT.KEIJOMAEACPTKIN, 0) - canceled_rec.ACPTKIN - canceled_rec.ACPTSHOHITAXKIN
                                                        ELSE -- インボイス施行後
                                                          NVL(TKJT.KEIJOMAEACPTKIN, 0) - canceled_rec.ACPTKIN
                                                        END
                                                     END) -- 計上前受注金額
-- インボイス対応変更 end ABI
         , TKJT.SAISHUACPTDD = wMaxAcptDd -- 最終受注日
         /* 更新者：更新なし */
     WHERE TKJT.APKIHONCD   = iTokuisakiMST.APKIHONCD
       AND TKJT.SHOPCD      = iTokuisakiMST.SHOPCD
       AND TKJT.TOKUISAKICD = iTokuisakiMST.TOKUISAKICD
       AND TKJT.YUKOKBN = '1'
     ;
  END processTokuisakiCancel;

  /****************************************************************************/
  /* プロシージャ名 ：モバイル配達実績情報取込ワーク更新                              */
  /* 引数           ：iRecordS05W001T     配達実績ワークデータ                    */
  /*                ：iAccisdnpyno        ＡＣＣＩＳ伝票番号                     */
  /****************************************************************************/
  PROCEDURE updateDlvryWRK( work_rec      IN S05W001T%rowtype -- 配達実績ワークデータ
                          , iAccisdnpyno  IN VARCHAR2 -- ＡＣＣＩＳ伝票番号
  ) IS
  BEGIN
    UPDATE S05W001T DLJW
       SET DLJW.DATSKSFLG      = '1' -- デーた作成フラグ
         , DLJW.KOSHAAPKIHONCD = userinfo_rec.APKIHONCD  -- 更新者ＡＰ基本コード
         , DLJW.KOSHASHOPCD    = userinfo_rec.SHOPCD     -- 更新者ショップコード
         , DLJW.KOSHACD        = userinfo_rec.TNSHACD    -- 更新者コード
         , DLJW.KSHNSYSTMYMD   = userinfo_rec.SYSTMYMD   -- 更新システム日付
         , DLJW.KSHNSYSTMJIKAN = userinfo_rec.SYSTMJIKAN -- 更新システム時間
         , DLJW.KSHNUNYOYMD    = userinfo_rec.UNYOYMD    -- 更新運用日付
         , DLJW.KSHNIPADRSE    = userinfo_rec.IPADRSE    -- 更新ＩＰアドレス
         , DLJW.KSHNPGMID      = userinfo_rec.PGMID      -- 更新プログラムＩＤ
     WHERE DLJW.JISSEKIJOHOTORIKOMIKANRINO = work_rec.JISSEKIJOHOTORIKOMIKANRINO -- 実績情報取込管理番号
       AND DLJW.ACPTDNPYNO  = iAccisdnpyno -- 伝票番号
       AND DLJW.EDANO       = work_rec.EDANO -- 枝番号
       AND DLJW.APKIHONCD   = work_rec.APKIHONCD -- ＡＰ基本コード
       AND DLJW.SHOPCD      = work_rec.SHOPCD -- ショップコード
       AND DLJW.TOKUISAKICD = work_rec.TOKUISAKICD -- 得意先コード
       AND DLJW.DATSKSFLG = '0'
       AND DLJW.YUKOKBN   = '1'
    ;
  END updateDlvryWRK;

-- 本線対応追加 start ABI
  /****************************************************************************/
  /* プロシージャ名 ：配達更新WEB処理                                         */
  /* 引数           ：work_rec            配達実績ワークデータ                */
  /*                ：sum_rec             合計データ                          */
  /*                ：iTokuisakiMST       得意先マスタデータ                  */
  /*                ：iTokuisakiJST       得意先実績テーブル                  */
  /*                ：iKbnTaxkeisan       税計算区分                          */
  /*                ：iKbnKaishuhoho      回収方法区分                        */
  /*                ：iKbnDlvrytnshakshn  実績担当者更新区分                  */
  /*                ：iKbnZaikokanri      ショップ在庫管理区分                */
  /*                ：iDydmZaikoprcs      ショップ在庫処理年月                */
  /*                ：iDdShopShime        ショップ締日                        */
  /****************************************************************************/
  PROCEDURE updateHaitatsuWeb( work_rec           IN S05W001T%rowtype  -- 配達実績ワークデータ
                             , sum_rec            IN OUT sum_rec_type  -- 合計データ
                             , iTokuisakiMST      IN S02M013T%rowtype  -- 得意先マスタデータ
                             , iTokuisakiJST      IN S02M050T%rowtype  -- 得意先実績テーブル
                             , iKbnTaxkeisan      IN VARCHAR2          -- 税計算区分
                             , iKbnKaishuhoho     IN VARCHAR2          -- 回収方法区分
                             , iKbnDlvrytnshakshn IN VARCHAR2          -- 実績担当者更新区分
                             , iKbnZaikokanri     IN VARCHAR2          -- ショップ在庫管理区分
                             , iDydmZaikoprcs     IN VARCHAR2          -- ショップ在庫処理年月
                             , iDdShopShime       IN VARCHAR2          -- ショップ締日
  ) IS
  /* 変数宣言 */
  bef_update_rec S04T001T%rowtype := NULL; -- 更新前受注配達データ
  sum_rec_cancel  sum_rec_cancel_type := NULL; -- 合計データ(キャンセル用)

  BEGIN
    -- 更新前受注配達データ取得
    SELECT JHTT.*
      INTO bef_update_rec
      FROM S04T001T JHTT
     WHERE JHTT.APKIHONCD  = work_rec.APKIHONCD
       AND JHTT.SHOPCD     = work_rec.SHOPCD
       AND JHTT.ACPTDNPYNO = work_rec.ACPTDNPYNO
       AND JHTT.EDANO      = work_rec.EDANO
       AND JHTT.YUKOKBN = '1'
     FOR UPDATE
    ;

    -- 受注配達明細テーブルからキャンセル本数を集計(更新前の情報で集計)
    sum_rec_cancel.RGRASURYO := 0; -- 配達レギュラー本数(キャンセル)
    sum_rec_cancel.SRMSURYO  := 0; -- 配達スリム本数(キャンセル)
    sum_rec_cancel.KZBTLN1N2RITTORUHNS := 0; -- 回収実績ボトル１２Ｌ本数
    sum_rec_cancel.KZBTLN8RITTORUHNS  := 0; -- 回収実績ボトル８Ｌ本数
    addAllCancel(work_rec, sum_rec_cancel);

    -- 更新前受注配達データの配達日と配達実績ワークデータの配達実績日が異なる場合、配達日変更フラグをTRUEに設定
    IF bef_update_rec.DLVRYDD <> work_rec.DLVRYJISSEKIDD THEN
      cmnCngdlvryddflg := TRUE;
    END IF;

    -- 更新前受注配達テーブルデータ.伝票ステータスが"12"（配達売上済）の場合、配達売上済の処理を行う
    IF bef_update_rec.DNPYSTS = '12' THEN
      -- 受注配達明細データ作成WEB処理を実行する
      processJuchuMeisaiWeb(work_rec, work_rec.ACPTDNPYNO, iKbnTaxkeisan, sum_rec );

      -- 受注配達テーブル配達済更新WEB処理を実行する
      processJuchuDeliveredWeb(work_rec, sum_rec, iTokuisakiMST, iTokuisakiJST, iKbnTaxkeisan, iKbnKaishuhoho );

      -- 売上データ作成WEB処理を実行する
      processUriageWeb(work_rec, iTokuisakiMST, iKbnKaishuhoho );

      -- 代引き（受注時代引きフラグが"1"（代引き）、または単体伝票締めフラグが"1"（単体伝票締めする））の場合、代引き更新WEB処理を実行する
      IF bef_update_rec.ACPTTHDAIBIKIFLG = '1' OR bef_update_rec.SINGLDNPYFLG = '1' THEN
      -- 代引き更新時、請求先マスタの値に関係なく回収方法区分は「7：代引」で処理する（単体伝票締めの場合を考慮）
        processDaibikiWeb(work_rec, iTokuisakiMST, '7' );
      END IF;

      -- 配達実績得意先一連処理を実行する
      processTokuisakiDlvryWeb(iTokuisakiMST, iTokuisakiJST, iKbnDlvrytnshakshn, work_rec, sum_rec , bef_update_rec.DNPYSTS, sum_rec_cancel);

      -- 在庫データ作成WEB処理を実行する
      processZaikoWeb(work_rec, iTokuisakiMST, iKbnZaikokanri, iDydmZaikoprcs, iDdShopShime );

    -- 更新前受注配達テーブルデータ.伝票ステータスが"13"（未配達）または、"14"（不在未配達）または"19"（配達時キャンセル）の場合、未配達更新の処理を行う
    ELSIF bef_update_rec.DNPYSTS IN ('13', '14', '19') THEN
      -- 受注配達テーブル未配達更新WEB処理を実行する
      processJuchuNotDeliverWeb(work_rec, bef_update_rec );
    END IF;

    -- モバイル配達実績情報取込ワーク更新処理を実行する
    updateDlvryWRK( work_rec, work_rec.ACPTDNPYNO );

  END updateHaitatsuWeb;
-- 本線対応追加 end ABI

  /****************************************************************************/
  /* プロシージャ名 ：配達実績情報更新処理                                         */
-- 本線対応追加 start ABI
  /* 引数           ：iApkihoncd         ＡＰ基本コード                       */
  /*                ：iShopcd            ショップコード                       */
-- 本線対応追加 end ABI
  /*                ：iTnshaapkihoncd    担当者ＡＰ基本コード                    */
  /*                ：iTnshashopcd       担当者ショップコード                    */
  /*                ：iTnshacd           担当者コード                           */
  /*                ：iIpadrse           ＩＰアドレス                           */
  /*                ：iJissekiTRKMNO     実績情報取込管理番号                    */
-- 本線対応追加 start ABI
  /*                ：iWebMobileKbn      WEBモバイル区分                      */
-- 本線対応追加 end ABI
  /****************************************************************************/
-- 本線対応追加 start ABI
  PROCEDURE UpdateDlvryJisseki( iApkihoncd      IN VARCHAR2 -- ＡＰ基本コード
                              , iShopcd         IN VARCHAR2 -- ショップコード
-- 本線対応追加 end ABI
                              , iTnshaapkihoncd IN VARCHAR2 -- 担当者ＡＰ基本コード
                              , iTnshashopcd    IN VARCHAR2 -- 担当者ショップコード
                              , iTnshacd        IN VARCHAR2 -- 担当者コード
                              , iIpadrse        IN VARCHAR2 -- ＩＰアドレス
                              , iJissekiTRKMNO  IN VARCHAR2 -- 実績情報取込管理番号
-- 本線対応追加 start ABI
                              , iWebMobileKbn   IN VARCHAR2 -- WEBモバイル区分（0:WEB、1:モバイル）
-- 本線対応追加 end ABI
  ) IS
  /* 変数宣言 */
  work_rec     S05W001T%rowtype := NULL; -- 配達実績ワークデータ
  noAccisdnpy  VARCHAR2(11) := NULL; -- ＡＣＣＩＳ伝票番号

  sum_rec      sum_rec_type     := NULL; -- 合計データ
  acpt_old     S04T001T%rowtype := NULL; -- 更新前受注配達テーブル
  tokuisakiMST S02M013T%rowtype := NULL; -- 得意先マスタ
  tokuisakiJST S02M050T%rowtype := NULL; -- 得意先実績テーブル

  kbnTaxkeisan  VARCHAR2(1) := NULL; -- 税計算区分
  kbnKaishuhoho VARCHAR2(1) := NULL; -- 回収方法区分
  kbnDlvrytnshakshn VARCHAR2(1) := NULL; -- 実績担当者更新区分
  kbnZaikokanri     VARCHAR2(1) := NULL; -- 在庫管理区分
  dydmZaikoprcs     VARCHAR2(6) := NULL; -- ショップ在庫処理年月
  ddShopShime       VARCHAR2(2) := NULL; -- ショップ締日
-- 本線対応追加 start ABI
  webDnpySts        VARCHAR2(2) := NULL; -- WEB伝票ステータス
-- 本線対応追加 end ABI

  /* CURSOR：配達実績ワークデータ抽出 */
  CURSOR SELECT_WORK IS
    SELECT DJTW.*
      FROM S05W001T DJTW
-- 本線対応変更 start ABI
     --WHERE DJTW.APKIHONCD = iTnshaapkihoncd
     --  AND DJTW.SHOPCD    = iTnshashopcd
     WHERE DJTW.APKIHONCD = iApkihoncd
       AND DJTW.SHOPCD    = iShopcd
-- 本線対応変更 end ABI
       AND DJTW.JISSEKIJOHOTORIKOMIKANRINO = iJissekiTRKMNO
       AND DJTW.DATSKSFLG = '0'
       AND DJTW.YUKOKBN   = '1'
     ORDER BY DJTW.APKIHONCD
            , DJTW.SHOPCD
            , DJTW.JISSEKIJOHOTORIKOMIKANRINO
            , DJTW.ACPTDNPYNO
            , DJTW.EDANO
            , DJTW.ACPTDNPYMEISAINO
  ;
  BEGIN
  -- ポイント制度対応追加 start ABI
  S99_CHECK_LOG('F0802B03', iApkihoncd, iShopcd, '1', 'S', '1', 'XXX', 0, iTnshaapkihoncd, iTnshashopcd, iTnshacd, iIpadrse, 'F0802B03' , '[開始]');
  gParamApkihoncd := iApkihoncd;
  gParamShopcd := iShopcd;
  -- ポイント制度対応追加 end ABI

-- 本線対応追加 start ABI
    IF iApkihoncd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20001,
                              'パラメータ.ＡＰ基本コード が設定されていません。');
    END IF;
    IF iShopcd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20002,
                              'パラメータ.ショップコード  が設定されていません。');
    END IF;
-- 本線対応追加 end ABI
    IF iTnshaapkihoncd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20001,
                              'パラメータ.担当者ＡＰ基本コード が設定されていません。');
    END IF;
    IF iTnshashopcd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20002,
                              'パラメータ.担当者ショップコード  が設定されていません。');
    END IF;
    IF iTnshacd IS NULL THEN
      RAISE_APPLICATION_ERROR(-20003,
                              'パラメータ.担当者コード  が設定されていません。');
    END IF;
    IF iIpadrse IS NULL THEN
      RAISE_APPLICATION_ERROR(-20004,
                              'パラメータ．ＩＰアドレス　が設定されていません。');
    END IF;
    -- 入力パラメータチェック
    IF iJissekiTRKMNO IS NULL THEN
      RAISE_APPLICATION_ERROR(-20005,
                              'パラメータ.実績情報取込管理番号 が設定されていません。');
    END IF;

-- 本線対応追加 start ABI
    IF iWebMobileKbn IS NULL THEN
      RAISE_APPLICATION_ERROR(-20007,
                              'パラメータ.WEBモバイル区分 が設定されていません。');
    END IF;
-- 本線対応追加 end ABI

    -- テーブル共通基本情報設定
-- 本線対応変更 start ABI
    --setUserInfo(iTnshaapkihoncd, iTnshashopcd, iTnshacd, iIpadrse, 'F0802B03');
    setUserInfo(iApkihoncd, iShopcd, iTnshaapkihoncd, iTnshashopcd, iTnshacd, iIpadrse, 'F0802B03');
-- 本線対応変更 end ABI

    -- 処理対象伝票番号抽出
    FOR vRecord IN SELECT_WORK LOOP

        -- 伝票単位で処理を実行する
        IF noAccisdnpy IS NULL
           OR noAccisdnpy||work_rec.EDANO <> vRecord.ACPTDNPYNO||vRecord.EDANO THEN
           -- 初期化
           work_rec := vRecord;
           sum_rec  := NULL;
-- インボイス対応追加 start ABI
           -- 下記変数を初期化
           cmnAcptippannukikin    := 0;     -- 受注一般税抜金額
           cmnAcptippantaxkin     := 0;     -- 受注一般消費税金額
           cmnAcptkeigennukikin   := 0;     -- 受注軽減税抜金額
           cmnAcptkeigentaxkin    := 0;     -- 受注軽減消費税金額
           cmnAcpthikazeiuriikin  := 0;     -- 受注非課税金額
           cmnAcptfukazeiuriikin  := 0;     -- 受注不課税売上金額
           cmnNohinippannukikin   := 0;     -- 納品一般税抜金額
           cmnNohinippantaxkin    := 0;     -- 納品一般消費税金額
           cmnNohinkeigennukikin  := 0;     -- 納品軽減税抜金額
           cmnNohinkeigentaxkin   := 0;     -- 納品軽減消費税金額
           cmnNohinhikazeiuriikin := 0;     -- 納品非課税金額
           cmnNohinfukazeiuriikin := 0;     -- 納品不課税売上金額
           cmnSingldnpyflg        := NULL;  -- 単体伝票締めフラグ
           cmnShimeippantaxkin    := NULL;  -- 締め後一般消費税金額
           cmnShimekeigentaxkin   := NULL;  -- 締め後軽減消費税金額
           cmnCngdlvryddflg       := FALSE; -- 配達日変更
           cmnCngmeisuryoflg      := FALSE; -- 明細数量金額変更
           cmnAllzeromeisuryoflg  := TRUE;  -- 明細数量オールゼロフラグ
           cmnAkauridnpyno        := NULL;  -- 売上伝票番号（赤）
           cmnKurouridnpyno       := NULL;  -- 売上伝票番号（黒）
           -- インボイス施行日フラグの設定
           cmnInvoiceshikoymdflg := setInvoiceFlg(iApkihoncd, iShopcd, work_rec.DLVRYJISSEKIDD);
-- インボイス対応追加 end ABI

-- 本線対応追加 start ABI
           -- パラメータ.WEBモバイル区分が"0"（WEB）の場合、伝票ステータスを取得
           IF iWebMobileKbn = '0' THEN
             BEGIN
               SELECT JHHT.DNPYSTS
                 INTO webDnpySts
                 FROM S04T001T JHHT
                WHERE JHHT.APKIHONCD  = work_rec.APKIHONCD
                  AND JHHT.SHOPCD     = work_rec.SHOPCD
                  AND JHHT.ACPTDNPYNO = work_rec.ACPTDNPYNO
                  AND JHHT.EDANO      = work_rec.EDANO
               ;
               EXCEPTION
                 WHEN OTHERS THEN
                   webDnpySts := NULL;
             END;
           END IF;
-- 本線対応追加 end ABI

           -- 受注伝票番号採番
           noAccisdnpy := work_rec.ACPTDNPYNO;
-- インボイス対応削除 start ABI
           --IF INSTR(work_rec.ACPTDNPYNO, 'J', 1) <> 1 THEN
           --   Z1804B01.getSlipNoJuchu( work_rec.APKIHONCD, work_rec.SHOPCD, 1, work_rec.ACPTDNPYNO, work_rec.ACPTDNPYNO );
           --   work_rec.EDANO := 0;
-- インボイス対応削除 end ABI
           -- 配達ステータスチェック
-- 本線対応変更 start ABI
--           ELSE
           -- WEBモバイル区分が"1"（モバイル）の場合、伝票ステータスチェックを行い配達区分、配達会社コードを設定する
           IF iWebMobileKbn = '1' THEN
-- 本線対応変更 end ABI
              checkDnpysts( work_rec, acpt_old );
-- ACCIS2023-6134対応 start ABI
--              work_rec.DLVRYKBN := acpt_old.DLVRYKBN;
--              work_rec.DLVRYCMPNYCD := acpt_old.DLVRYCMPNYCD;
-- ACCIS2023-6134対応 end   ABI
-- 本線対応追加 start ABI
           -- 上記以外（WEBモバイル区分が"1"（モバイル）以外）の場合、更新前受注配達テーブルデータを取得する。
           ELSE
              BEGIN
                -- 更新前受注配達データ取得
                SELECT JHTT.*
                  INTO acpt_old
                  FROM S04T001T JHTT
                 WHERE JHTT.APKIHONCD  = work_rec.APKIHONCD
                   AND JHTT.SHOPCD     = work_rec.SHOPCD
                   AND JHTT.ACPTDNPYNO = work_rec.ACPTDNPYNO
                   AND JHTT.EDANO      = work_rec.EDANO
                   AND JHTT.YUKOKBN = '1'
                ;
                EXCEPTION
                  WHEN OTHERS THEN
                    acpt_old := NULL;
              END;
-- 本線対応追加 end ABI
           END IF;

-- ACCIS2023-6134、ACCIS2023-6349対応 start ABI
           -- WEBモバイル区分が1（モバイル）かつ受注伝票番号の頭文字がM以外の場合、
           --   ・配達区分、配達会社コードを更新前受注配達テーブルから設定
           -- 上記以外の場合（下記参照）はモバイル実績情報取込ワークの項目値のままとする
           --   ・モバイルで伝票番号がMから始まらない受注伝票の場合 → ワークには配達区分が1（自社）、配達会社コードはNULLで設定済
           --   ・WEBの場合 → 配達実績登録（個別入力）で変更されてる場合があるため、その内容をワークから取得する
           IF iWebMobileKbn = '1' AND INSTR(work_rec.ACPTDNPYNO, 'M', 1) <> 1 THEN
              work_rec.DLVRYKBN := acpt_old.DLVRYKBN;
              work_rec.DLVRYCMPNYCD := acpt_old.DLVRYCMPNYCD;
           END IF;
-- ACCIS2023-6134、ACCIS2023-6349対応 end   ABI

           -- 得意先マスタ、得意先実績テーブル取得
           BEGIN
             SELECT TKSM.*
               INTO tokuisakiMST
               FROM S02M013T TKSM
              WHERE TKSM.APKIHONCD = work_rec.APKIHONCD
                AND TKSM.SHOPCD    = work_rec.SHOPCD
                AND TKSM.TOKUISAKICD = work_rec.TOKUISAKICD
              FOR UPDATE
             ;
             SELECT TKJT.*
               INTO tokuisakiJST
               FROM S02M050T TKJT
              WHERE TKJT.APKIHONCD = work_rec.APKIHONCD
                AND TKJT.SHOPCD    = work_rec.SHOPCD
                AND TKJT.TOKUISAKICD = work_rec.TOKUISAKICD
              FOR UPDATE
             ;
             -- 税計算区分、実績担当者更新区分、在庫管理区分を取得
             SELECT ( CASE WHEN tokuisakiMST.TOKUISAKIKBN IN ('2', '3', '4', '5', '6') -- 卸対象
                           THEN '2'
                           ELSE ( CASE WHEN userinfo_rec.UNYOYMD IS NULL OR userinfo_rec.UNYOYMD >= SHPM.TAXKEISANKBNURIKIRIKAEDD
                                       THEN SHPM.SHINTAXKEISANKBNURI
                                       ELSE SHPM.GENZTAXKEISANKBNURI
                                  END )
                      END ) TAXKEISANKBN
                  , SHPM.DLVRYTNSHAJISSEKIKSHNKBN
                  , SHPM.ZAIKOKANRIKBN
                  , SHPM.ZAIKOPRCSDYDM -- ショップ在庫処理年月
                  , SHPM.SHIMEDD -- ショップ締日
               INTO kbnTaxkeisan
                  , kbnDlvrytnshakshn
                  , kbnZaikokanri
                  , dydmZaikoprcs -- ショップ在庫処理年月
                  , ddShopShime   -- ショップ締日
               FROM S02M046T SHPM
              WHERE SHPM.APKIHONCD  = work_rec.APKIHONCD
                    AND SHPM.SHOPCD = work_rec.SHOPCD
             ;
             -- 回収方法区分取得
             kbnKaishuhoho := NULL;
             IF tokuisakiMST.SKYSAKICD IS NOT NULL THEN
                SELECT SKSM.KAISHUHOHOKBN
                  INTO kbnKaishuhoho
                  FROM S02M017T SKSM
                 WHERE SKSM.APKIHONCD = tokuisakiMST.APKIHONCD
                   AND SKSM.SHOPCD    = tokuisakiMST.SHOPCD
                   AND SKSM.SKYSAKICD = tokuisakiMST.SKYSAKICD
                ;
             END IF;

             EXCEPTION
               WHEN OTHERS THEN
                 RAISE_APPLICATION_ERROR(-20011, SQLERRM || '得意先一連検索時エラーが発生しました。');
           END;

-- 本線対応追加 start ABI
           -- 単体伝票締めフラグを取得
           BEGIN
             SELECT NVL(JHHT.SINGLDNPYFLG, '0')
               INTO cmnSingldnpyflg
               FROM S04T001T JHHT
              WHERE JHHT.APKIHONCD  = work_rec.APKIHONCD
                AND JHHT.SHOPCD     = work_rec.SHOPCD
                AND JHHT.ACPTDNPYNO = work_rec.ACPTDNPYNO
                AND JHHT.EDANO      = work_rec.EDANO
                AND JHHT.YUKOKBN    = '1'
             ;
             EXCEPTION
               WHEN OTHERS THEN
                 cmnSingldnpyflg := '0';
           END;

-- ACCIS2023-6615対応 start ABI
           -- WEBモバイル区分が"0"（WEB）かつ、配達ステータスが"12"（配達売上済）の場合、印刷済フラグを更新する
           IF iWebMobileKbn = '0' AND work_rec.DLVRYSTS = '12' THEN
             updatePrntsumiflg( work_rec );
           END IF;
-- ACCIS2023-6615対応 end   ABI

           -- ポイント制度対応追加 start ABI
           -- 紐づけテーブル初期化
           cmnRegistKanrino := NULL;
           -- 登録済でキャンセルする予定発生ポイントテーブルレコード初期化
           cmnAkadnPntT := NULL;
           -- 登録済でキャンセルする予定発生ポイントテーブルレコード初期化
           cmnAkadnPntMeiTList := S20T002TList();
           -- 登録済でキャンセルする予定発生ポイントテーブルレコード初期化
           cmnAkadnPntMeiksTList := S20T004TList();
           -- 赤伝用採番管理番号初期化
           cmnAkadenSaibanno := NULL;
           -- ポイント制度対応追加 end ABI

           -- WEBモバイル区分が"0"（WEB）で、かつWEB伝票ステータスが"12"（配達売上済）、"13"（未配達）、"14"（不在不配達）、"19"（配達時キャンセル）の場合、
           -- 配達更新処理（WEB）を実行する。
           IF iWebMobileKbn = '0' AND webDnpySts IN ('12','13','14','19') THEN
             -- 配達更新WEB処理を実行
             updateHaitatsuWeb( work_rec, sum_rec, tokuisakiMST, tokuisakiJST, kbnTaxkeisan, kbnKaishuhoho, kbnDlvrytnshakshn, kbnZaikokanri, dydmZaikoprcs, ddShopShime);
             -- 伝票単位でコミット
             COMMIT;
             -- 次のモバイル配達実績取込ワークデータへ
             CONTINUE;
           END IF;
-- 本線対応追加 end ABI

           -- 受注配達処理
           IF work_rec.DLVRYSTS = '12' THEN               -- 伝票ステータス：配達売上済
              processJuchuMeisai( work_rec, noAccisdnpy, kbnTaxkeisan, sum_rec );
-- インボイス対応変更 start ABI
              -- processJuchuDelivered( work_rec, sum_rec, tokuisakiMST, tokuisakiJST, kbnTaxkeisan, kbnKaishuhoho );
              processJuchuDelivered( work_rec, sum_rec, tokuisakiMST, tokuisakiJST, kbnTaxkeisan, kbnKaishuhoho, acpt_old );
-- インボイス対応変更 end ABI
           ELSIF work_rec.DLVRYSTS IN ( '13', '14' ) THEN -- 伝票ステータス：未配達／不在未配達
              processJuchuNotDeliver( work_rec, acpt_old );
              -- 再配達
              processSaihaitatsu( work_rec );
           ELSIF work_rec.DLVRYSTS = '19' THEN            -- 伝票ステータス：配達時キャンセル
              processJuchuNotDeliver( work_rec, acpt_old );
           ELSE -- その他の伝票ステータス
              RAISE_APPLICATION_ERROR(-20015, '配達ステータスは無効です。');
           END IF;

           -- 売上処理
           IF work_rec.DLVRYSTS = '12' THEN
              processUriage( work_rec, tokuisakiMST, kbnKaishuhoho );
           END IF;

           -- 代引き処理
-- インボイス対応変更 start ABI
--           IF acpt_old.ACPTTHDAIBIKIFLG = '1'
--              AND work_rec.DLVRYSTS = '12' THEN
--              processDaibiki( work_rec, tokuisakiMST );
           -- 配達ステータスが"12"（配達売上済）で、
           -- 更新前受注配達データ.受注時代引きフラグが"1"（代引き）、または単体伝票締めフラグが"1"（単体伝票締めする）の場合、代引き更新を実施
           IF (acpt_old.ACPTTHDAIBIKIFLG = '1' OR cmnSingldnpyflg = '1' )
              AND work_rec.DLVRYSTS = '12' THEN
              -- 代引き更新時、請求先マスタの値に関係なく回収方法区分は「7：代引」で処理する（単体伝票締めの場合を考慮）
              processDaibiki( work_rec, tokuisakiMST, '7' );
-- インボイス対応変更 end ABI
           END IF;

           -- 得意先処理
           IF work_rec.DLVRYSTS = '12' THEN
              processTokuisakiDlvry( tokuisakiMST, tokuisakiJST, kbnDlvrytnshakshn, work_rec, sum_rec );
           END IF;

           IF work_rec.DLVRYSTS = '19' THEN
              processTokuisakiCancel( tokuisakiMST, tokuisakiJST, work_rec );
           END IF;

           -- 在庫処理
           IF work_rec.DLVRYSTS = '12' THEN
              processZaiko( work_rec, tokuisakiMST, kbnZaikokanri, dydmZaikoprcs, ddShopShime );
           END IF;

           -- 在庫引当処理
           IF work_rec.DLVRYSTS IN ( '12', '19' ) THEN
              processZkHikiate( work_rec, kbnZaikokanri );
           END IF;

           -- 取込済更新
           updateDlvryWRK( work_rec, noAccisdnpy );

           -- 伝票単位でコミット
           COMMIT;

        END IF;
    END LOOP;

    -- ポイント制度対応追加 start ABI
    S99_CHECK_LOG(userinfo_rec.PGMID, gParamApkihoncd, gParamShopcd, '1', 'E', '1', 'XXX', 0, userinfo_rec.APKIHONCD, userinfo_rec.SHOPCD, userinfo_rec.TNSHACD, userinfo_rec.IPADRSE, userinfo_rec.PGMID , '[終了]');
    -- ポイント制度対応追加 end ABI

    -- EXCEPTION：処理終了
    EXCEPTION
      WHEN OTHERS THEN
        ROLLBACK;
        -- カーソル オープンであれば、クローズする
        IF SELECT_WORK%ISOPEN THEN
           CLOSE SELECT_WORK;
        END IF;
        -- 共通変数削除
        -- エラー
        RAISE_APPLICATION_ERROR(-20705, SQLERRM);

  END UpdateDlvryJisseki;

END F0802B03;
/
exit;
