create or replace 
PACKAGE BODY F0802B01 
/*******************************************************************************
 * システム名       ：ＡＰ総合管理システム 
 * 機能名           ：モバイルシステム実績情報取込（チェック処理）
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
  /* 取込ファイル定義用変数 */
  TYPE lineprop_rec_type IS RECORD ( 
                         COLUMNID    VARCHAR2(50) -- 項目ＩＤ
                       , COLNAME     VARCHAR2(50) -- 項目名
                       , COLREQUIRED BOOLEAN      -- 必須フラグ
                       , COLTYPE     VARCHAR2(10) -- タイプ
                       , COLLENGTH   NUMBER(5,0)  -- 桁数
  ); 
  TYPE fileprop_rec_type IS TABLE OF lineprop_rec_type; 
  fileprop_rec fileprop_rec_type := fileprop_rec_type(); 
  /* ファイル取込用変数 */
  TYPE row_rec_type IS TABLE OF VARCHAR2(200); -- 取込行
  TYPE file_rec_type IS TABLE OF row_rec_type; -- 取込ファイル
  /* エラートラン共通項目 */
  TYPE error_rec_type IS RECORD ( 
                         APKIHONCD      S01T009T.APKIHONCD%type -- ＡＰ基本コード
                       , SHOPCD         S01T009T.SHOPCD%type    -- ショップコード
                       , KANRINO        S01T009T.KANRINO%type        -- 管理番号
                       , TORIKOMIRENBAN S01T009T.TORIKOMIRENBAN%type -- 取込連番
                       , SESSIONID S01T009T.SESSIONID%type  -- セッションＩＤ 
                       , TOKUISAKICD    VARCHAR2(6) -- 得意先コード
                       , SKYSAKICD      VARCHAR2(6) -- 請求先コード
  ); 
  error_rec error_rec_type; -- エラートラン共通項目
  /* キー項目データ */
  TYPE key_rec_type IS RECORD ( 
                    LINENO NUMBER -- 行数
                  , APKIHONCD        VARCHAR2(3) -- ＡＰ基本コード
                  , SHOPCD           VARCHAR2(3) -- ショップコード
                  , ACCISDNPYNO      VARCHAR2(11) -- ＡＣＣＩＳ伝票番号
                  , ACPTDNPYMEISAINO NUMBER(5,0) -- 受注伝票明細番号
  ); 
  TYPE key_list_type IS TABLE OF key_rec_type; -- キー項目データ
  /* モバイルシステム実績データ */
  TYPE jisseki_rec_type IS RECORD ( 
                        APKIHONCD      VARCHAR2(3)  -- ＡＰ基本コード
                      , SHOPCD         VARCHAR2(3)  -- ショップコード
                      , ACCISDNPYNO    VARCHAR2(11) -- ＡＣＣＩＳ伝票番号
                      , ACCISDNPYEDANO NUMBER(2,0)  -- ＡＣＣＩＳ伝票番号枝番
                      , ACPTKBN        VARCHAR2(1) -- 受注区分
                      , ACPTKBNSHS     VARCHAR2(2) -- 受注区分詳細
                      , TOKUISAKICD    VARCHAR2(6) -- 得意先コード
                      , DLVRYJISSEKIDD VARCHAR2(8) -- 配達実績日
                      , DLVRYTNSHACD   VARCHAR2(6) -- 配達担当者コード
                      , SKYSAKICD VARCHAR2(6)  -- 請求先コード
                      , NYKNKIND1 VARCHAR2(2)  -- 入金種別１
                      , NYKNKIN1  NUMBER(11,0) -- 入金金額１
                      , NYKNKIND2 VARCHAR2(2)  -- 入金種別２
                      , NYKNKIN2  NUMBER(11,0) -- 入金金額２
                      , KZBTLRGRAHNS      NUMBER(4,0) -- 回収実績ボトルレギュラー本数 
                      , KZBTLSLMHNS       NUMBER(4,0) -- 回収実績ボトルスリム本数 
                      , DLVRYSTS          VARCHAR2(2) -- 配達ステータス
                      , JIKAIDLVRYYTIDD   VARCHAR2(8) -- 次回配達予定日
                      , FZAIOKIJISSEKIFLG VARCHAR2(1) -- 不在置き実績フラグ
                      , DLVRYKBN          VARCHAR2(1) -- 配達区分 
                      , DLVRYCMPNYCD      VARCHAR2(3) -- 配達会社コード
                      /* 明細部 */
                      , ACPTDNPYMEISAINO    NUMBER(5,0) -- 受注伝票明細番号
                      , ACPTDNPYMEISAIEDANO NUMBER(2,0) -- 受注伝票明細枝番号
                      , SHNCD               VARCHAR2(6) -- 商品コード
                      , HAIFUBUTSUAPKIHONCD VARCHAR2(3) -- 配布物ＡＰ基本コード
                      , HAIFUBUTSUSHOPCD    VARCHAR2(3) -- 配布物ショップコード
                      , HAIFUBUTSUCD        VARCHAR2(5) -- 配布物コード
                      , KEIJOKBN   VARCHAR2(2)  -- 計上区分
                      , NOHINSURYO NUMBER(9,1)  -- 納品数量
                      , NOHINUNT   NUMBER(10,2) -- 納品単価
                      , NYURYOKUMEISAIFLG VARCHAR2(1)  -- 入力明細フラグ
                      , TANPINMEISAIFLG   VARCHAR2(1)  -- 単品明細フラグ
                      , STSHNCD           VARCHAR2(6)  -- セット商品コード
                      , MEISAIBIKO        VARCHAR2(40) -- 明細備考
                      , KATASIKI      VARCHAR2(15) -- 型式
                      , SRLNO         VARCHAR2(15) -- シリアル番号
                      , CUPHOLDERUMU  VARCHAR2(1)  -- カップホルダ有無
                      , LOCKTFOSET    VARCHAR2(2)  -- ロック付フォーセット
                      , FRONTPANELCD  VARCHAR2(6)  -- フロントパネルコード
                      , PANELNO       VARCHAR2(15) -- パネルNo
                      , STUPNAIYO     VARCHAR2(2)  -- 設置内容
                      , STUPBASYOKBN  VARCHAR2(2)  -- 設置場所区分
                      , STUPBASYOBIKO VARCHAR2(50) -- 設置場所備考
                      , HIKIAGENAIYO  VARCHAR2(2)  -- 引上内容
                      , SABUSTS       VARCHAR2(3)  -- サブステータス
                      , MEISAIBIKO_HIKIAGE VARCHAR2(40) -- 明細備考（引上）
                      , KONKAIMNTDD        VARCHAR2(8)  -- 今回メンテナンス日
                      , JIKAIMNTYTIDYDM    VARCHAR2(6)  -- 次回メンテナンス予定年月
                      , MNTKAISU           VARCHAR2(3)  -- メンテナンス回数
                      , NOHINKANRYOMEMO VARCHAR2(200) -- 納品完了メモ
-- インボイス対応追加 start ABI
                      , SHOHITAXKIN VARCHAR2(200) -- 消費税金額
-- インボイス対応追加 end ABI
  ); 
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
  
  /****************************************************************************/
  /* プロシージャ名 ：項目定義                                                    */
  /****************************************************************************/
  PROCEDURE setLineprop( iColumnid    VARCHAR2 -- 項目ＩＤ
                       , iColname     VARCHAR2 -- 項目名
                       , iColrequired BOOLEAN  -- 必須フラグ
                       , iColtype     VARCHAR2 -- タイプ
                       , iCollength   NUMBER   -- 桁数
  ) IS 
  lineprop_rec lineprop_rec_type := NULL; -- 項目属性
  BEGIN 
    fileprop_rec.EXTEND; 
    lineprop_rec.COLUMNID    := iColumnid;
    lineprop_rec.COLNAME     := iColname; 
    lineprop_rec.COLREQUIRED := iColrequired; 
    lineprop_rec.COLTYPE     := iColtype; 
    lineprop_rec.COLLENGTH   := iCollength; 
    fileprop_rec(fileprop_rec.LAST) := lineprop_rec; 
  END setLineprop; 
  
  /****************************************************************************/
  /* プロシージャ名 ：取込ＣＳＶ定義：モバイルシステム実績データ                      */
  /****************************************************************************/
  PROCEDURE defineCSV IS 
  BEGIN 
    fileprop_rec := fileprop_rec_type(); 
    setLineprop('APKIHONCD', 'ＡＰ基本コード', TRUE, '09', 3);
    setLineprop('SHOPCD', 'ショップコード', TRUE, '09', 3);
    setLineprop('ACCISDNPYNO', 'ＡＣＣＩＳ伝票番号', FALSE, 'AZ09', 11);
    setLineprop('ACCISDNPYEDANO', 'ＡＣＣＩＳ伝票番号枝番', FALSE, 'NUMBER0', (2 + 0));
    setLineprop('ACPTKBN', '受注区分', FALSE, '09', 1);
    setLineprop('ACPTKBNSHS', '受注区分詳細', FALSE, '09', 2);
    setLineprop('TOKUISAKICD', '得意先コード', FALSE, '09', 6);
    setLineprop('DLVRYJISSEKIDD', '配達実績日', FALSE, 'YYYYMMDD', 8);
    setLineprop('DLVRYTNSHACD', '配達担当者コード', FALSE, '09', 6);
    setLineprop('SKYSAKICD', '請求先コード', FALSE, '09', 6);
    setLineprop('NYKNKIND1', '入金種別１', FALSE, '09', 2);
    setLineprop('NYKNKIN1', '入金金額１', FALSE, 'NUMBER0', (11 + 0));
    setLineprop('NYKNKIND2', '入金種別２', FALSE, '09', 2);
    setLineprop('NYKNKIN2', '入金金額２', FALSE, 'NUMBER0', (11 + 0));
    setLineprop('KZBTLRGRAHNS', '回収実績ﾎﾞﾄﾙﾚｷﾞｭﾗｰ本数', FALSE, 'NUMBER0', (4 + 0));
    setLineprop('KZBTLSLMHNS', '回収実績ボトルスリム本数', FALSE, 'NUMBER0', (4 + 0));
    setLineprop('DLVRYSTS', '配達ステータス', FALSE, '09', 2);
    setLineprop('JIKAIDLVRYYTIDD', '次回配達予定日', FALSE, 'YYYYMMDD', 8);
    setLineprop('FZAIOKIJISSEKIFLG', '不在置き実績フラグ', FALSE, '01', 1);
    /* 明細部 */
    setLineprop('ACPTDNPYMEISAINO', '受注伝票明細番号', FALSE, 'NUMBER0', (5 + 0));
    setLineprop('ACPTDNPYMEISAIEDANO', '受注伝票明細枝番号', FALSE, 'NUMBER0', (2 + 0));
    setLineprop('SHNCD', '商品コード', FALSE, '09', 6);
    setLineprop('HAIFUBUTSUAPKIHONCD', '配布物ＡＰ基本コード', FALSE, '09', 3);
    setLineprop('HAIFUBUTSUSHOPCD', '配布物ショップコード', FALSE, '09', 3);
    setLineprop('HAIFUBUTSUCD', '配布物コード', FALSE, '09', 5);
    setLineprop('KEIJOKBN', '計上区分', FALSE, '09', 2);
    setLineprop('NOHINSURYO', '納品数量', FALSE, 'NUMBER1', (9 + 1));
    setLineprop('NOHINUNT', '納品単価', FALSE, 'NUMBER2', (10 + 2));
    setLineprop('NYURYOKUMEISAIFLG', '入力明細フラグ', FALSE, '09', 1);
    setLineprop('TANPINMEISAIFLG', '単品明細フラグ', FALSE, '09', 1);
    setLineprop('STSHNCD', 'セット商品コード', FALSE, '09', 6);
    setLineprop('MEISAIBIKO', '明細備考', FALSE, 'ALL', 40);
    setLineprop('KATASIKI', '型式', FALSE, 'VARCHAR2', 15);
    setLineprop('SRLNO', 'シリアル番号', FALSE, 'AZ09', 15);
    setLineprop('CUPHOLDERUMU', 'カップホルダ有無', FALSE, '01', 1);
    setLineprop('LOCKTFOSET', 'ロック付フォーセット', FALSE, '09', 2);
    setLineprop('FRONTPANELCD', 'フロントパネルコード', FALSE, '09', 6);
    setLineprop('PANELNO', 'パネルNo', FALSE, 'AZ09', 15);
    setLineprop('STUPNAIYO', '設置内容', FALSE, '09', 2);
    setLineprop('STUPBASYOKBN', '設置場所区分', FALSE, '09', 2);
    setLineprop('STUPBASYOBIKO', '設置場所備考', FALSE, 'ALL', 50);
    setLineprop('HIKIAGENAIYO', '引上内容', FALSE, '09', 2);
    setLineprop('SABUSTS', 'サブステータス', FALSE, '09', 3);
    setLineprop('MEISAIBIKO_HIKIAGE', '明細備考（引上）', FALSE, 'ALL', 40);
    setLineprop('KONKAIMNTDD', '今回メンテナンス日', FALSE, 'YYYYMMDD', 8);
    setLineprop('JIKAIMNTYTIDYDM', '次回メンテナンス予定年月', FALSE, 'YYYYMM', 6);
    setLineprop('MNTKAISU', 'メンテナンス回数', FALSE, '09', 3);
    setLineprop('NOHINKANRYOMEMO', '納品完了メモ', FALSE, 'ALL', 200);
-- インボイス対応追加 start ABI
    setLineprop('SHOHITAXKIN', '消費税金額', FALSE, 'NUMBER0', (11 + 0));
-- インボイス対応追加 end ABI
  END defineCSV; 
  
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
  /* プロシージャ名：エラートラン共通情報設定                                       */
  /* 引数        　    ：iApKihoncd           担当者ＡＰ基本コード                */
  /*                  ：iShopcd              担当者ショップコード                */
  /*                  ：iSessionid           セッションＩＤ                      */
  /****************************************************************************/
  PROCEDURE initErrorInfo( iApKihoncd    IN VARCHAR2
                         , iShopcd       IN VARCHAR2
                         , iSessionid    IN VARCHAR2
  ) IS 
  BEGIN
    -- エラー管理番号採番
-- 本線対応変更 start ABI
--    S01SAIBAN.getNewNo( SAIBAN_CHECKERROR -- 採番種別番号：エラー管理番号採番
    Z1804B01.getNewNo( SAIBAN_CHECKERROR -- 採番種別番号：エラー管理番号採番
-- 本線対応変更 end ABI
                      , 0    -- チェックデジット付与フラグ
                      , 1    -- 採番回数
                      , iApKihoncd -- AP基本コード
                      , iShopcd    -- ショップコード
                      , NULL -- 得意先コード
                      , error_rec.KANRINO -- 採番開始値 
                      , error_rec.KANRINO -- 採番終了値 
    ); 
    
    error_rec.APKIHONCD := iApKihoncd; -- 担当者ＡＰ基本コード
    error_rec.SHOPCD    := iShopcd;    -- 担当者ショップコード
    error_rec.TORIKOMIRENBAN := 0;          -- 取込連番
    error_rec.SESSIONID      := iSessionid; -- セッションＩＤ
    error_rec.TOKUISAKICD := NULL; -- 得意先コード：毎行チェック時設定
    error_rec.SKYSAKICD   := NULL; -- 請求先コード：毎行チェック時設定
    
  END initErrorInfo;
  
  /****************************************************************************/
  /* ファンクション名 ：CSVファイル取込                                            */
  /* 引数        ：iFilepath         ファイルパス                                */
  /*             ：iFilename         ファイル名称                                */
  /*             ：iSpliter          区切文字                                   */
  /* return      ：returnFile        ファイル内容                                */
  /****************************************************************************/
  FUNCTION readCSV( iFilepath     IN VARCHAR2 
                  , iFilename     IN VARCHAR2 
                  , iSpliter      IN VARCHAR2
  ) RETURN file_rec_type IS returnFile file_rec_type; 
  file_handle  UTL_FILE.FILE_TYPE; -- 取込対象ファイル 
  file_buf     VARCHAR2(1000)     := NULL; -- テストＢＵＦ
  row_rec      row_rec_type; -- 対象行
  item_counter NUMBER(4)     := 1; -- 項目番号
  item_start   NUMBER(4)     := 1; -- 項目開始位置
  item_spliter NUMBER(4)     := 1; -- 区切文字位置
  BEGIN 
    S99_CHECK_LOG('F0802B01.readCSV', NULL, NULL, '1', 'S', '1', 'XXX', 0, NULL, NULL, NULL, NULL, 'F0802B01' , 'ＣＳＶファイル取込[開始] ' );
    
    -- 読み込みモードでファイルオープン
-- 本線対応変更 start ABI
--    file_handle := UTL_FILE.FOPEN(iFilepath, iFilename, 'r');
    FILEDOWNLOADFROMS3(iFilepath, iFilename);
    file_handle := UTL_FILE.FOPEN('PL_SQL_DIR2', iFilename, 'r');
-- 本線対応変更 end ABI
    
    returnFile := file_rec_type(); -- ファイル初期化
    LOOP 
      BEGIN 
        -- ファイルを1レコード読み込み
        UTL_FILE.GET_LINE(file_handle, file_buf); 
        
        -- 文字列を分割する
        item_counter := 1;
        item_start   := 1; 
        item_spliter := 1;
        row_rec := row_rec_type();
        LOOP 
          -- 区切位置
          item_spliter := INSTRB(file_buf, iSpliter, 1, item_counter); 
          
          -- 最後の項目
          IF item_spliter = 0 THEN 
             row_rec.EXTEND; 
             IF (LENGTHB(file_buf) - item_start < 0) THEN 
                row_rec(row_rec.LAST) := NULL; 
             ELSE 
                row_rec(row_rec.LAST) := SUBSTRB( file_buf, item_start, LENGTHB(file_buf) - item_start ); 
             END IF; 
          END IF; 
          EXIT WHEN item_spliter = 0 ;
          
          -- 項目取得
          row_rec.EXTEND; 
          row_rec(row_rec.LAST) := SUBSTRB( file_buf, item_start, item_spliter - item_start ); 
          -- 次の項目
          item_start   := item_spliter + 1;
          item_counter := item_counter + 1;
        END LOOP; 
        
        -- ファイル対象に挿入
        returnFile.EXTEND;
        returnFile(returnFile.LAST) := row_rec;
        
        -- ファイルの終端に来た場合は、ループ終了
        EXCEPTION 
          WHEN NO_DATA_FOUND THEN 
            EXIT; 
      END;
    END LOOP;
        
    -- ファイルクローズ
    UTL_FILE.FCLOSE(file_handle); 
    
    S99_CHECK_LOG('F0802B01.readCSV', NULL, NULL, '1', 'E', '1', 'XXX', 0, NULL, NULL, NULL, NULL, 'F0802B01' , 'ＣＳＶファイル取込[終了] ' );
    
    RETURN(returnFile);
    EXCEPTION
      WHEN OTHERS THEN
        -- ファイルクローズ
        UTL_FILE.FCLOSE_ALL; 
        RAISE_APPLICATION_ERROR(-20008, SQLERRM || 'ＣＳＶファイル内容読み込み時エラーが発生しました。');
    
  END readCSV;
  
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
  /* ファンクション名 ：EXCEPTION HANDLED 数値変換                                */
  /* 引数             ：iChar           文字列                                  */
  /* return           ：returnNumber    数値                                   */
  /****************************************************************************/
  FUNCTION toNumberOrNull( iChar      IN VARCHAR2 
  ) RETURN NUMBER IS returnNumber NUMBER;
  
  BEGIN 
    returnNumber := TO_NUMBER(iChar);
    
    RETURN(returnNumber);
    EXCEPTION 
      WHEN OTHERS THEN 
        RETURN(NULL); 
  END toNumberOrNull; 
  
  /****************************************************************************/
  /* プロシージャ名 ：エラー追加                                                  */
  /* 引数（ＩＮ）    ：iLineno          行番号                                   */
  /*                ：iKomokumei       項目名                                   */
  /*                ：iMessage         メッセージ                               */
  /****************************************************************************/
  PROCEDURE addError( iLineno      IN NUMBER -- 行番号
                    , iKomokumei   IN VARCHAR2 -- 項目名
                    , iMessage     IN VARCHAR2 -- メッセージ
  ) IS 
  wHanyoErrnaiyo S01T009T.HANYOERRNAIYO%type := NULL;
  BEGIN 
    -- エラー内容：エラーメッセージ(得意先コード,請求先コード)
    wHanyoErrnaiyo := iMessage 
                 || '(' || error_rec.TOKUISAKICD || ',' || error_rec.SKYSAKICD || ')';
    -- エラートランに登録
    INSERT INTO S01T009T ( 
                           APKIHONCD -- ＡＰ基本コード
                         , SHOPCD -- ショップコード
                         , KANRINO -- 管理番号
                         , TORIKOMIRENBAN -- 取込連番
                         , SESSIONID -- セションＩＤ
                         , PRCSJISSIDDTH -- 処理実施日時
                         , ERRKIND -- エラー種別
                         , TORIKOMIGYOKAZU -- 取込行数
                         , KOMOKUMEI -- 項目名
                         , HANYOERRNAIYO -- エラー内容
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
                           error_rec.APKIHONCD -- ＡＰ基本コード
                         , error_rec.SHOPCD    -- ショップコード
                         , error_rec.KANRINO   -- 管理番号
                         , error_rec.TORIKOMIRENBAN -- 取込連番
                         , error_rec.SESSIONID -- セションＩＤ
                         , userinfo_rec.SYSTMYMD || userinfo_rec.SYSTMJIKAN -- 処理実施日時
                         , '1' -- エラー種別
                         , NVL(iLineno, 0) -- 取込行数
                         , iKomokumei -- 項目名
                         , wHanyoErrnaiyo -- エラー内容
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
    
    -- 連番カウントアップ
    error_rec.TORIKOMIRENBAN := error_rec.TORIKOMIRENBAN + 1;
    
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR(-20012, SQLERRM || 'エラートランに登録時エラーが発生しました。');
    
  END addError;
  
  /****************************************************************************/
  /* ファンクション名 ：日付属性チェック                                           */
  /* 引数           ：iFormat         日付フォーマット                            */
  /*                ：iItem           チェック対象                               */
  /****************************************************************************/
  FUNCTION checkDate( iFormat      IN VARCHAR2 
                    , iItem        IN VARCHAR2 
  ) RETURN BOOLEAN IS returnResult BOOLEAN; 
  /* 変数宣言 */
  wDate VARCHAR(50) := 0; 
  BEGIN 
    wDate := TO_DATE( iItem, iFormat ); 
    RETURN(TRUE); 
    
    EXCEPTION 
      WHEN OTHERS THEN 
        RETURN(FALSE);
  END checkDate;
  
  /****************************************************************************/
  /* ファンクション名 ：項目基本属性チェック                                        */
  /* 引数           ：iLineno          行番号                                   */
  /*                ：iColumnno       項目番号                                  */
  /*                ：iItem           チェック対象                               */
  /****************************************************************************/
  FUNCTION checkBasics( iLineno      IN NUMBER 
                      , iColumnno    IN NUMBER 
                      , iItem        IN VARCHAR2 
  ) RETURN BOOLEAN IS returnResult BOOLEAN; 
  /* 変数宣言 */
  wNumber  NUMBER(11,2) := 0; 
  wdot     NUMBER(4)    := 0; 
  lShosu   NUMBER(2)    := 0; 
  BEGIN 
    -- 必須チェック
    IF fileprop_rec(iColumnno).COLREQUIRED AND iItem IS NULL THEN 
       addError( iLineno, fileprop_rec(iColumnno).COLNAME
               , '【W031】「'|| fileprop_rec(iColumnno).COLNAME ||'」を入力して下さい。'); 
       RETURN(FALSE);
    END IF; 
    
    -- 値がある場合、バイト数チェックと属性チェックをする
    IF iItem IS NULL OR iItem IN (TO_CHAR(CHR(13)), TO_CHAR(CHR(10))) THEN 
       RETURN(TRUE); 
    END IF;
    
    -- 入力可能文字数（バイト数）チェック 
    -- 　　数値のバイト数チェック：属性チェックと一緒 
    IF SUBSTRB(fileprop_rec(iColumnno).COLTYPE, 1, 6) = 'NUMBER' THEN 
       NULL; 
    -- 　　文字列のバイト数チェック 
    ELSIF LENGTHB(iItem) > fileprop_rec(iColumnno).COLLENGTH THEN 
       addError( iLineno, fileprop_rec(iColumnno).COLNAME
               , '【W102】「'|| fileprop_rec(iColumnno).COLNAME ||'」は'
                            || fileprop_rec(iColumnno).COLLENGTH ||'バイト以内で入力して下さい。'); 
       RETURN(FALSE); 
    END IF; 
    
    -- 入力属性チェック
    -- 　　(0, 1)
    IF fileprop_rec(iColumnno).COLTYPE = '01' THEN 
       FOR index_i IN 1..fileprop_rec(iColumnno).COLLENGTH LOOP 
           IF SUBSTRB(iItem, index_i, 1) < '0' OR SUBSTRB(iItem, index_i, 1) > '1' THEN 
             addError( iLineno, fileprop_rec(iColumnno).COLNAME
                     , '【W030】「'|| fileprop_rec(iColumnno).COLNAME ||'は'
                            || '半角数字0か1' ||'」を指定して下さい。');
             RETURN(FALSE);
           END IF; 
       END LOOP; 
    END IF; 
    -- 　　半角数字
    IF fileprop_rec(iColumnno).COLTYPE = '09' THEN 
       FOR index_i IN 1..fileprop_rec(iColumnno).COLLENGTH LOOP 
           IF SUBSTRB(iItem, index_i, 1) < '0' OR SUBSTRB(iItem, index_i, 1) > '9' THEN 
             addError( iLineno, fileprop_rec(iColumnno).COLNAME
                     , '【W030】「'|| fileprop_rec(iColumnno).COLNAME ||'は'
                            || '半角数字' ||'」を指定して下さい。');
             RETURN(FALSE);
           END IF; 
       END LOOP; 
    END IF; 
    -- 　　半角英数字
    IF fileprop_rec(iColumnno).COLTYPE = 'AZ09' THEN 
       FOR index_i IN 1..fileprop_rec(iColumnno).COLLENGTH LOOP 
           IF ( NOT ( SUBSTRB(iItem, index_i, 1) >= '0' AND SUBSTRB(iItem, index_i, 1) <= '9' ) ) 
              AND ( NOT ( UPPER(SUBSTRB(iItem, index_i, 1)) >= 'A' AND UPPER(SUBSTRB(iItem, index_i, 1)) <= 'Z' ) ) THEN 
             addError( iLineno, fileprop_rec(iColumnno).COLNAME 
                     , '【W030】「'|| fileprop_rec(iColumnno).COLNAME ||'は'
                            || '半角英数字' ||'」を指定して下さい。');
             RETURN(FALSE);
           END IF; 
       END LOOP; 
    END IF; 
    -- 　　数値
    IF SUBSTRB(fileprop_rec(iColumnno).COLTYPE, 1, 6) = 'NUMBER' THEN 
       wdot := INSTRB(iItem, '.', 1, 1); 
       lShosu := TO_NUMBER(SUBSTRB(fileprop_rec(iColumnno).COLTYPE, 7, 1)); 
       IF wdot > 0 THEN 
          -- 整数部 
          IF (wdot - 1) > (fileprop_rec(iColumnno).COLLENGTH - lShosu) THEN 
             addError( iLineno, fileprop_rec(iColumnno).COLNAME
                     , '【W030】「'|| fileprop_rec(iColumnno).COLNAME 
                                  ||'」は整数部' || fileprop_rec(iColumnno).COLLENGTH - lShosu || '桁、小数部' || TO_CHAR(lShosu) 
                                  || '桁以内の数値を指定して下さい。'); 
             RETURN(FALSE); 
          END IF; 
          -- 小数部 
          IF LENGTHB(iItem) - wdot > lShosu THEN 
             addError( iLineno, fileprop_rec(iColumnno).COLNAME
                     , '【W030】「'|| fileprop_rec(iColumnno).COLNAME 
                                  ||'」は整数部' || TO_CHAR(fileprop_rec(iColumnno).COLLENGTH - lShosu) || '桁、小数部' || TO_CHAR(lShosu) 
                                  || '桁以内の数値を指定して下さい。'); 
             RETURN(FALSE); 
          END IF; 
       ELSE 
          -- 整数部 
          IF LENGTHB(iItem) > (fileprop_rec(iColumnno).COLLENGTH - lShosu) THEN 
             addError( iLineno, fileprop_rec(iColumnno).COLNAME
                     , '【W030】「'|| fileprop_rec(iColumnno).COLNAME 
                                  ||'」は整数部' || TO_CHAR(fileprop_rec(iColumnno).COLLENGTH - lShosu) || '桁、小数部' || TO_CHAR(lShosu) 
                                  || '桁以内の数値を指定して下さい。'); 
             RETURN(FALSE); 
          END IF; 
       END IF; 
       -- 属性 
       BEGIN 
         wNumber := TO_NUMBER(iItem);
         EXCEPTION 
           WHEN OTHERS THEN 
             addError( iLineno, fileprop_rec(iColumnno).COLNAME
                     , '【W030】「'|| fileprop_rec(iColumnno).COLNAME ||'は'
                            || '数値' ||'」を指定して下さい。');
             RETURN(FALSE);
       END ; 
    END IF; 
    -- 　　日付：YYYYMMDD 
    IF fileprop_rec(iColumnno).COLTYPE = 'YYYYMMDD' THEN 
       IF NOT checkDate( fileprop_rec(iColumnno).COLTYPE, iItem ) THEN 
          addError( iLineno, fileprop_rec(iColumnno).COLNAME
                  , '【W030】「'|| fileprop_rec(iColumnno).COLNAME ||'は'
                         || '日付：YYYYMMDD' ||'」を指定して下さい。');
                RETURN(FALSE); 
       END IF; 
    END IF; 
    -- 　　日付：YYYYMM
    IF fileprop_rec(iColumnno).COLTYPE = 'YYYYMM' THEN 
       IF NOT checkDate( fileprop_rec(iColumnno).COLTYPE, iItem ) THEN 
          addError( iLineno, fileprop_rec(iColumnno).COLNAME
                  , '【W030】「'|| fileprop_rec(iColumnno).COLNAME ||'は'
                         || '日付：YYYYMM' ||'」を指定して下さい。');
                RETURN(FALSE); 
       END IF; 
    END IF; 
    -- 　　全角以外
    IF fileprop_rec(iColumnno).COLTYPE <> 'ALL' THEN 
       IF LENGTHB(iItem) <> LENGTH(iItem) THEN 
          addError( iLineno, fileprop_rec(iColumnno).COLNAME
                  , '【W030】「'|| fileprop_rec(iColumnno).COLNAME ||'は'
                         || '半角文字' ||'」を指定して下さい。'); 
          RETURN(FALSE); 
       END IF; 
    END IF; 
    
    RETURN(TRUE);
  END checkBasics;
  
-- インボイス対応追加 start ABI
  /****************************************************************************/
  /* ファンクション名 ：消費税金額チェック                                    */
  /* 引数           ：iLineno         行番号                                  */
  /*                ：iColumnno       項目番号                                */
  /*                ：iItem           チェック対象                            */
  /****************************************************************************/
  FUNCTION checkShohitaxkin( iLineno      IN NUMBER 
                           , iColumnno    IN NUMBER 
                           , iItem        IN VARCHAR2 
  ) RETURN BOOLEAN IS returnResult BOOLEAN; 
  /* 変数宣言 */
  wNumber  NUMBER(11,2) := 0; 
  wdot     NUMBER(4)    := 0; 
  lShosu   NUMBER(2)    := 0; 
  BEGIN 

    -- 全角以外
    IF fileprop_rec(iColumnno).COLTYPE <> 'ALL' THEN 
       IF LENGTHB(iItem) <> LENGTH(iItem) THEN 
          addError( iLineno, fileprop_rec(iColumnno).COLNAME
                  , '【W030】「'|| fileprop_rec(iColumnno).COLNAME ||'は'
                         || '半角文字' ||'」を指定して下さい。'); 
          RETURN(FALSE); 
       END IF; 
    END IF; 
    
    -- 数値
    IF SUBSTRB(fileprop_rec(iColumnno).COLTYPE, 1, 6) = 'NUMBER' THEN 
       wdot := INSTRB(iItem, '.', 1, 1); 
       lShosu := TO_NUMBER(SUBSTRB(fileprop_rec(iColumnno).COLTYPE, 7, 1)); 
       IF wdot > 0 THEN 
          -- 整数部 
          IF (wdot - 1) > (fileprop_rec(iColumnno).COLLENGTH - lShosu) THEN 
             addError( iLineno, fileprop_rec(iColumnno).COLNAME
                     , '【W030】「'|| fileprop_rec(iColumnno).COLNAME 
                                  ||'」は整数部' || fileprop_rec(iColumnno).COLLENGTH - lShosu || '桁、小数部' || TO_CHAR(lShosu) 
                                  || '桁以内の数値を指定して下さい。'); 
             RETURN(FALSE); 
          END IF; 
          -- 小数部 
          IF LENGTHB(iItem) - wdot > lShosu THEN 
             addError( iLineno, fileprop_rec(iColumnno).COLNAME
                     , '【W030】「'|| fileprop_rec(iColumnno).COLNAME 
                                  ||'」は整数部' || TO_CHAR(fileprop_rec(iColumnno).COLLENGTH - lShosu) || '桁、小数部' || TO_CHAR(lShosu) 
                                  || '桁以内の数値を指定して下さい。'); 
             RETURN(FALSE); 
          END IF; 
       ELSE 
          -- 整数部 
          IF LENGTHB(iItem) > (fileprop_rec(iColumnno).COLLENGTH - lShosu) THEN 
             addError( iLineno, fileprop_rec(iColumnno).COLNAME
                     , '【W030】「'|| fileprop_rec(iColumnno).COLNAME 
                                  ||'」は整数部' || TO_CHAR(fileprop_rec(iColumnno).COLLENGTH - lShosu) || '桁、小数部' || TO_CHAR(lShosu) 
                                  || '桁以内の数値を指定して下さい。'); 
             RETURN(FALSE); 
          END IF; 
       END IF; 
       -- 属性 
       BEGIN 
         wNumber := TO_NUMBER(iItem);
         EXCEPTION 
           WHEN OTHERS THEN 
             addError( iLineno, fileprop_rec(iColumnno).COLNAME
                     , '【W030】「'|| fileprop_rec(iColumnno).COLNAME ||'は'
                            || '数値' ||'」を指定して下さい。');
             RETURN(FALSE);
       END ; 
    END IF; 

    RETURN(TRUE);
  END checkShohitaxkin;
-- インボイス対応追加 end ABI

  /****************************************************************************/
  /* ファンクション名 ：ＡＰ基本マスタチェック                                      */
  /* 引数           ：iLineno         行番号                                    */
  /*                ：iColumnno       項目番号                                  */
  /*                ：iApkihoncd      ＡＰ基本コード                             */
  /****************************************************************************/
  FUNCTION checkApkihonMST( iLineno         IN NUMBER 
                          , iColumnno       IN NUMBER 
                          , iApkihoncd      IN VARCHAR2 
  ) RETURN BOOLEAN IS returnResult BOOLEAN; 
  /* 変数宣言 */
  wYukokbn VARCHAR2(1) := NULL; -- 有効区分
  BEGIN 
    SELECT APKM.YUKOKBN 
      INTO wYukokbn 
      FROM S02M001T APKM 
     WHERE APKM.APKIHONCD = iApkihoncd 
    ;
    -- AP基本マスタ有効チェック 
    IF wYukokbn <> '1' THEN 
       addError( iLineno, fileprop_rec(iColumnno).COLNAME
               , '【W041】'|| fileprop_rec(iColumnno).COLNAME ||'が無効です。'); 
       RETURN(FALSE);
    END IF; 

    RETURN(TRUE);
    -- AP基本マスタ存在チェック
    EXCEPTION 
      WHEN NO_DATA_FOUND THEN 
        addError( iLineno, fileprop_rec(iColumnno).COLNAME
               , '【W002】'|| fileprop_rec(iColumnno).COLNAME ||'が存在しません。'); 
        RETURN(FALSE);
    
  END checkApkihonMST;
  
  /****************************************************************************/
  /* ファンクション名 ：ショップマスタチェック                                      */
  /* 引数           ：iLineno         行番号                                    */
  /*                ：iColumnno       項目番号                                  */
  /*                ：iApkihoncd      ＡＰ基本コード                             */
  /*                ：iShopcd         ショップコード                             */
  /****************************************************************************/
  FUNCTION checkShopMST( iLineno         IN NUMBER 
                       , iColumnno       IN NUMBER 
                       , iApkihoncd      IN VARCHAR2 
                       , iShopcd         IN VARCHAR2 
  ) RETURN BOOLEAN IS returnResult BOOLEAN; 
  /* 変数宣言 */
  wYukokbn VARCHAR2(1) := NULL; -- 有効区分
  BEGIN 
    SELECT SHPM.YUKOKBN 
      INTO wYukokbn 
      FROM S02M046T SHPM 
     WHERE SHPM.APKIHONCD = iApkihoncd 
       AND SHPM.SHOPCD    = iShopcd 
    ;
    -- ショップマスタ有効チェック 
    IF wYukokbn <> '1' THEN 
       addError( iLineno, fileprop_rec(iColumnno).COLNAME
               , '【W041】'|| fileprop_rec(iColumnno).COLNAME ||'が無効です。'); 
       RETURN(FALSE);
    END IF; 
    
    RETURN(TRUE);
    -- ショップマスタ存在チェック
    EXCEPTION 
      WHEN NO_DATA_FOUND THEN 
        addError( iLineno, fileprop_rec(iColumnno).COLNAME
               , '【W002】'|| fileprop_rec(iColumnno).COLNAME ||'が存在しません。'); 
        RETURN(FALSE);
    
  END checkShopMST;
  
  /****************************************************************************/
  /* ファンクション名 ：同一伝票番号で異なる値チェック                               */
  /* 引数           ：iLineno         行番号                                    */
  /*                ：iColumnno       項目番号                                  */
  /*                ：iCurrent        チェック対象                              */
  /*                ：iHeader         伝票ヘッダ値                              */
  /****************************************************************************/
  FUNCTION checkSameDnpy( iLineno    IN NUMBER 
                        , iColumnno  IN NUMBER 
                        , iCurrent   IN VARCHAR2 
                        , iHeader    IN VARCHAR2 
  ) RETURN BOOLEAN IS returnResult BOOLEAN; 
  BEGIN 
    IF iCurrent IS NULL AND iHeader IS NULL THEN 
       RETURN(TRUE); 
    END IF; 
    IF iCurrent = iHeader THEN 
       RETURN(TRUE); 
    END IF; 
    
    addError( iLineno, fileprop_rec(iColumnno).COLNAME
            , '【W089】'|| fileprop_rec(iColumnno).COLNAME ||'は同じＡＣＣＩＳ伝票番号で、異なる値が存在する場合は取込できません。');
    RETURN(FALSE); 
    
  END checkSameDnpy;
  
  /****************************************************************************/
  /* ファンクション名 ：受注、計上区分NULLチェック                                  */
  /* 引数           ：iLineno         行番号                                    */
  /*                ：iColumnno       項目番号                                  */
  /*                ：iKbn            区分値                                    */
  /*                ：iAccisdnpyno    ＡＣＣＩＳ伝票番号                         */
  /****************************************************************************/
  FUNCTION checkKbnnullNewdnpy( iLineno         IN NUMBER 
                              , iColumnno       IN NUMBER 
                              , iKbn            IN VARCHAR2 
                              , iAccisdnpyno    IN VARCHAR2 
  ) RETURN BOOLEAN IS returnResult BOOLEAN; 
  BEGIN 
    -- 既存伝票
    IF SUBSTRB(iAccisdnpyno, 1, 1) = 'J' THEN 
       IF iKbn IS NOT NULL THEN 
          addError( iLineno, fileprop_rec(iColumnno).COLNAME
                  , '【W089】'|| fileprop_rec(iColumnno).COLNAME || 'は既存受注配達伝票の場合は入力できません。'); 
          RETURN(FALSE);
       END IF;
    -- 新規伝票
    ELSE 
       IF iKbn IS NULL THEN 
          addError( iLineno, fileprop_rec(iColumnno).COLNAME
                  , '【W030】'|| fileprop_rec(iColumnno).COLNAME ||'を指定して下さい。'); 
          RETURN(FALSE);
       END IF;
    END IF;
    
    RETURN(TRUE);
  END checkKbnnullNewdnpy;
  
  /****************************************************************************/
  /* ファンクション名 ：配達中伝票存在チェック                                      */
  /* 引数           ：iLineno            行番号                                 */
  /*                ：iColumnno          項目番号                               */
  /*                ：iApkihoncd         ＡＰ基本コード                          */
  /*                ：iShopcd            ショップコード                          */
  /*                ：iAccisDnpyno       ＡＣＣＩＳ伝票番号                      */
  /*                ：iAccisDnpyedano    ＡＣＣＩＳ伝票枝番号                     */
  /****************************************************************************/
  FUNCTION checkDnpyExist( iLineno    IN NUMBER 
                         , iColumnno  IN NUMBER 
                         , iApkihoncd      IN VARCHAR2 
                         , iShopcd         IN VARCHAR2 
                         , iAccisDnpyno    IN VARCHAR2 
                         , iAccisDnpyedano IN VARCHAR2 
  ) RETURN S04T001T%rowtype IS returnResult S04T001T%rowtype; 
  wDnpysts VARCHAR(2) := NULL; -- 伝票ステータス
  BEGIN 
    -- 既存伝票の場合のみチェック
    IF SUBSTRB(iAccisDnpyno, 1, 1) <> 'J' THEN 
       RETURN(NULL);
    END IF;
    
    SELECT JHTT.* 
      INTO returnResult 
      FROM S04T001T JHTT
     WHERE JHTT.APKIHONCD  = iApkihoncd 
       AND JHTT.SHOPCD     = iShopcd 
       AND JHTT.ACPTDNPYNO = iAccisDnpyno 
       AND JHTT.EDANO      = iAccisDnpyedano 
       AND JHTT.YUKOKBN = '1'
    ;
    
    -- 配達中伝票以外エラー
    IF returnResult.DNPYSTS <> '11' THEN 
       addError(iLineno, fileprop_rec(iColumnno - 1).COLNAME, '【W002】配達中の受注伝票が存在しません。');
       RETURN(returnResult);
    END IF;
    
    RETURN(returnResult);
    -- 伝票存在チェック
    EXCEPTION 
      WHEN OTHERS THEN 
        addError(iLineno, fileprop_rec(iColumnno - 1).COLNAME, '【W002】配達中の受注伝票が存在しません。');
        RETURN(NULL);
  END checkDnpyExist; 
  
  /****************************************************************************/
  /* ファンクション名 ：システム管理マスタ存在チェック                                  */
  /* 引数           ：iLineno         行番号                                    */
  /*                ：iColumnno       項目番号                                  */
  /*                ：iApkihoncd      ＡＰ基本コード                             */
  /*                ：iShopcd         ショップコード                             */
  /*                ：iSetteino       設定番号                                  */
  /*                ：iSetteicd       設定コード                                */
  /*                ：iOyasetteino    親設定番号                                */
  /*                ：iOyasetteicd    親設定コード                              */
  /****************************************************************************/
  FUNCTION checkSystemMST( iLineno         IN NUMBER 
                         , iColumnno       IN NUMBER 
                         , iApkihoncd      IN VARCHAR2 
                         , iShopcd         IN VARCHAR2
                         , iSetteino       IN VARCHAR2
                         , iSetteicd       IN VARCHAR2 
                         , iOyasetteino    IN VARCHAR2
                         , iOyasetteicd    IN VARCHAR2 
  ) RETURN BOOLEAN IS returnResult BOOLEAN; 
  /* 変数宣言 */
  wSetteicd VARCHAR2(20) := NULL;
  BEGIN 
    -- 親設定なし
    IF iOyasetteino IS NULL AND iOyasetteicd IS NULL THEN 
       SELECT SYSM.SETTEICD
         INTO wSetteicd
         FROM S02M006T SYSM
        WHERE SYSM.APKIHONCD = iApkihoncd 
          AND SYSM.SHOPCD    = iShopcd 
          AND SYSM.RCRDKBN   = 'M'
          AND SYSM.SETTEINO  = iSetteino 
          AND SYSM.SETTEICD  = iSetteicd 
          AND SYSM.YUKOKBN   = '1' 
       ;
    -- 親設定あり
    ELSE 
       SELECT SYSM.SETTEICD
         INTO wSetteicd
         FROM S02M006T SYSM
        WHERE SYSM.APKIHONCD = iApkihoncd 
          AND SYSM.SHOPCD    = iShopcd 
          AND SYSM.RCRDKBN   = 'M'
          AND SYSM.SETTEINO  = iSetteino 
          AND SYSM.SETTEICD  = iSetteicd 
          AND SYSM.OYASETTEINO = iOyasetteino 
          AND SYSM.OYASETTEICD = iOyasetteicd 
          AND SYSM.YUKOKBN   = '1' 
       ;
    END IF;
    
    RETURN(TRUE);
    EXCEPTION 
      WHEN NO_DATA_FOUND THEN 
        IF iSetteino = '112' AND iOyasetteino IS NOT NULL THEN 
           addError( iLineno, fileprop_rec(iColumnno).COLNAME
                   , '【W041】'||fileprop_rec(iColumnno).COLNAME||'が無効です。'); 
        ELSE 
           addError( iLineno, fileprop_rec(iColumnno).COLNAME
                   , '【W002】'||fileprop_rec(iColumnno).COLNAME||'が存在しません。'); 
        END IF; 
        RETURN(FALSE);
  END checkSystemMST;
  
  /****************************************************************************/
  /* ファンクション名 ：得意先マスタチェック                                        */
  /* 引数           ：iLineno         行番号                                    */
  /*                ：iColumnno       項目番号                                  */
  /*                ：iApkihoncd      ＡＰ基本コード                             */
  /*                ：iShopcd         ショップコード                             */
  /*                ：iTokuisakicd    得意先コード                              */
  /****************************************************************************/
  FUNCTION checkTokuisakiMST( iLineno         IN NUMBER 
                            , iColumnno       IN NUMBER 
                            , iApkihoncd      IN VARCHAR2 
                            , iShopcd         IN VARCHAR2 
                            , iTokuisakicd    IN VARCHAR2 
  ) RETURN S02M013T%rowtype IS returnResult S02M013T%rowtype; 
  BEGIN 
    SELECT TKSM.* 
      INTO returnResult 
      FROM S02M013T TKSM 
     WHERE TKSM.APKIHONCD   = iApkihoncd 
       AND TKSM.SHOPCD      = iShopcd 
       AND TKSM.TOKUISAKICD = iTokuisakicd 
    ;
    -- 得意先マスタ有効チェック 
    IF returnResult.YUKOKBN <> '1' THEN 
       addError( iLineno, fileprop_rec(iColumnno).COLNAME
               , '【W041】'|| fileprop_rec(iColumnno).COLNAME ||'が無効です。'); 
       RETURN(returnResult);
    END IF; 
    
    RETURN(returnResult);
    -- 得意先マスタ存在チェック
    EXCEPTION 
      WHEN NO_DATA_FOUND THEN 
        addError( iLineno, fileprop_rec(iColumnno).COLNAME
               , '【W002】'|| fileprop_rec(iColumnno).COLNAME ||'が存在しません。'); 
        RETURN(NULL);
    
  END checkTokuisakiMST;
  
  /****************************************************************************/
  /* ファンクション名 ：日付大小関係チェック                                        */
  /* 引数           ：iLineno         行番号                                    */
  /*                ：iColumnno       項目番号                                  */
  /*                ：iPastdate       過去日付                                  */
  /*                ：iPastname       過去日付名称                               */
  /*                ：iFuturedate     未来日付                                  */
  /*                ：iFuturename     未来日付名称                               */
  /****************************************************************************/
  FUNCTION checkPastFutureDate( iLineno         IN NUMBER 
                              , iColumnno       IN NUMBER 
                              , iPastdate       IN VARCHAR2 
                              , iPastname       IN VARCHAR2 
                              , iFuturedate     IN VARCHAR2 
                              , iFuturename     IN VARCHAR2 
  ) RETURN BOOLEAN IS returnResult BOOLEAN; 
  BEGIN 
    IF iFuturedate < iPastdate THEN 
       addError(iLineno, fileprop_rec(iColumnno).COLNAME, '【W011】「'|| iPastname ||'」と「'|| iFuturename ||'」の大小関係が正しくありません。');
       RETURN(FALSE);
    END IF; 
    RETURN(TRUE);
  END checkPastFutureDate;
    
  /****************************************************************************/
  /* ファンクション名 ：担当者マスタチェック                                        */
  /* 引数           ：iLineno         行番号                                    */
  /*                ：iColumnno       項目番号                                  */
  /*                ：iApkihoncd      ＡＰ基本コード                             */
  /*                ：iShopcd         ショップコード                             */
  /*                ：iTnshacd        担当者コード                              */
  /*                ：iKbn            担当者区分（配達：DLVRY）                   */
  /****************************************************************************/
  FUNCTION checkTnshaMST( iLineno         IN NUMBER 
                        , iColumnno       IN NUMBER 
                        , iApkihoncd      IN VARCHAR2 
                        , iShopcd         IN VARCHAR2 
                        , iTnshacd        IN VARCHAR2 
                        , iKbn            IN VARCHAR2 
  ) RETURN S02M003T%rowtype IS returnResult S02M003T%rowtype; 
  /* 変数宣言 */
  BEGIN 
    SELECT TNSM.* 
      INTO returnResult 
      FROM S02M003T TNSM 
     WHERE TNSM.APKIHONCD = iApkihoncd 
       AND TNSM.SHOPCD    = iShopcd 
       AND TNSM.TNSHACD   = iTnshacd 
    ;
    -- 担当者マスタ有効チェック 
    IF returnResult.YUKOKBN <> '1' THEN 
       addError( iLineno, fileprop_rec(iColumnno).COLNAME
               , '【W041】'|| fileprop_rec(iColumnno).COLNAME ||'が無効です。'); 
       RETURN(returnResult);
    END IF; 
    
    -- 担当者マスタ妥当性チェック
    IF iKbn = 'DLVRY' THEN -- 配達担当者
       IF returnResult.DLVRYTNSHAKBN <> '1' THEN 
          addError( iLineno, fileprop_rec(iColumnno).COLNAME
               , '【W067】担当者コードは配達担当者ではありません。'); 
          RETURN(returnResult);
       END IF; 
    END IF;
    
    RETURN(returnResult);
    -- 担当者マスタ存在チェック
    EXCEPTION 
      WHEN NO_DATA_FOUND THEN 
        addError( iLineno, fileprop_rec(iColumnno).COLNAME
               , '【W002】'|| fileprop_rec(iColumnno).COLNAME ||'が存在しません。'); 
        RETURN(returnResult);
    
  END checkTnshaMST;
  
  /****************************************************************************/
  /* ファンクション名 ：請求先マスタチェック                                        */
  /* 引数           ：iLineno         行番号                                    */
  /*                ：iColumnno       項目番号                                  */
  /*                ：iApkihoncd      ＡＰ基本コード                             */
  /*                ：iShopcd         ショップコード                             */
  /*                ：iSkysakicd      請求先コード                               */
  /****************************************************************************/
  FUNCTION checkSkysakiMST( iLineno         IN NUMBER 
                          , iColumnno       IN NUMBER 
                          , iApkihoncd      IN VARCHAR2 
                          , iShopcd         IN VARCHAR2 
                          , iSkysakicd      IN VARCHAR2 
  ) RETURN S02M017T%rowtype IS returnResult S02M017T%rowtype; 
  /* 変数宣言 */
  BEGIN 
    SELECT SKSM.* 
      INTO returnResult 
      FROM S02M017T SKSM 
     WHERE SKSM.APKIHONCD = iApkihoncd 
       AND SKSM.SHOPCD    = iShopcd 
       AND SKSM.SKYSAKICD = iSkysakicd 
    ;
    -- 請求先マスタ有効チェック 
    IF returnResult.YUKOKBN <> '1' THEN 
       addError( iLineno, fileprop_rec(iColumnno).COLNAME
               , '【W041】'|| fileprop_rec(iColumnno).COLNAME ||'が無効です。'); 
       RETURN(returnResult);
    END IF; 
    
    RETURN(returnResult);
    -- 請求先マスタ存在チェック
    EXCEPTION 
      WHEN NO_DATA_FOUND THEN 
        addError( iLineno, fileprop_rec(iColumnno).COLNAME
               , '【W002】'|| fileprop_rec(iColumnno).COLNAME ||'が存在しません。'); 
        RETURN(returnResult);
    
  END checkSkysakiMST;
  
  /****************************************************************************/
  /* ファンクション名 ：配達会社マスタチェック                                      */
  /* 引数           ：iLineno         行番号                                    */
  /*                ：iColumnno       項目番号                                  */
  /*                ：iApkihoncd      ＡＰ基本コード                             */
  /*                ：iShopcd         ショップコード                             */
  /*                ：iDlvrycmpnycd   配達会社コード                             */
  /****************************************************************************/
  FUNCTION checkDlvrycmpnyMST( iLineno         IN NUMBER 
                             , iColumnno       IN NUMBER 
                             , iApkihoncd      IN VARCHAR2 
                             , iShopcd         IN VARCHAR2 
                             , iDlvrycmpnycd   IN VARCHAR2 
  ) RETURN BOOLEAN IS returnResult BOOLEAN; 
  /* 変数宣言 */
  wYukokbn VARCHAR2(1) := NULL; -- 有効区分
  BEGIN 
    SELECT TNSM.YUKOKBN 
      INTO wYukokbn 
      FROM S02M019T TNSM 
     WHERE TNSM.APKIHONCD = iApkihoncd 
       AND TNSM.SHOPCD    = iShopcd 
       AND TNSM.DLVRYCMPNYCD = iDlvrycmpnycd 
    ;
    -- 配達会社マスタ有効チェック 
    IF wYukokbn <> '1' THEN 
       addError( iLineno, fileprop_rec(iColumnno).COLNAME
               , '【W041】'|| fileprop_rec(iColumnno).COLNAME ||'が無効です。'); 
       RETURN(FALSE);
    END IF; 
    
    RETURN(TRUE);
    -- 配達会社マスタ存在チェック
    EXCEPTION 
      WHEN NO_DATA_FOUND THEN 
        addError( iLineno, fileprop_rec(iColumnno).COLNAME
               , '【W002】'|| fileprop_rec(iColumnno).COLNAME ||'が存在しません。'); 
        RETURN(FALSE);
    
  END checkDlvrycmpnyMST;
  
  /****************************************************************************/
  /* ファンクション名 ：NULL関連性チェック                                         */
  /* 引数           ：iLineno         行番号                                    */
  /*                ：iItem1          項目１                                    */
  /*                ：iItemName1      項目名称１                                */
  /*                ：iItem2          項目２                                    */
  /*                ：iItemName2      項目名称２                                */
  /****************************************************************************/
  FUNCTION checkNullKanren( iLineno         IN NUMBER 
                          , iItem1          IN VARCHAR2 
                          , iItemName1      IN VARCHAR2 
                          , iItem2          IN VARCHAR2 
                          , iItemName2      IN VARCHAR2 
  ) RETURN BOOLEAN IS returnResult BOOLEAN; 
  BEGIN 
    IF iItem1 IS NOT NULL AND iItem2 IS NULL THEN 
       addError( iLineno, iItemName2
               , '【W082】'|| iItemName1 ||'が入力された場合'
                     ||'「'|| iItemName2 ||'」は必須です。'); 
       RETURN(FALSE);
    ELSIF iItem1 IS NULL AND iItem2 IS NOT NULL THEN 
       addError( iLineno, iItemName1 
               , '【W082】'|| iItemName2 ||'が入力された場合'
                     ||'「'|| iItemName1 ||'」は必須です。'); 
       RETURN(FALSE);
    END IF;
    
    RETURN(TRUE); 
  END checkNullKanren;
  
  /****************************************************************************/
  /* ファンクション名 ：キー重複チェック                                           */
  /* 引数           ：key_list         キー項目リスト                            */
  /*                ：key_rec          チェック対象                             */
  /****************************************************************************/
  FUNCTION checkDistinctKey( key_list    IN key_list_type 
                           , key_rec     IN key_rec_type 
  ) RETURN BOOLEAN IS returnResult BOOLEAN; 
  existed key_rec_type := NULL; -- ファイル内既存キー
  BEGIN 
    IF key_list.LAST IS NULL OR key_list.LAST = 0 THEN 
       RETURN(TRUE); 
    END IF; 

    FOR i IN 1..key_list.LAST LOOP 
        existed := key_list(i); 
        IF key_rec.APKIHONCD  = existed.APKIHONCD 
           AND key_rec.SHOPCD = existed.SHOPCD 
           AND key_rec.ACCISDNPYNO      = existed.ACCISDNPYNO 
           AND key_rec.ACPTDNPYMEISAINO = existed.ACPTDNPYMEISAINO THEN 
           addError( key_rec.lineno, '', 
                     '【W021】第'||existed.LINENO||'行目のデータとキーが重複しています。');
           RETURN(FALSE); 
        END IF; 
    END LOOP; 
    
    RETURN(TRUE); 
    
  END checkDistinctKey;
  
  /****************************************************************************/
  /* ファンクション名 ：商品マスタチェック                                          */
  /* 引数           ：iLineno         行番号                                    */
  /*                ：iColumnno       項目番号                                  */
  /*                ：iApkihoncd      ＡＰ基本コード                             */
  /*                ：iShopcd         ショップコード                             */
  /*                ：iTnshacd        商品コード                                */
  /****************************************************************************/
  FUNCTION checkShohinMST( iLineno         IN NUMBER 
                         , iColumnno       IN NUMBER 
                         , iApkihoncd      IN VARCHAR2 
                         , iShopcd         IN VARCHAR2 
                         , iShncd          IN VARCHAR2 
  ) RETURN S02V001T%rowtype IS returnResult S02V001T%rowtype; 
  BEGIN 
    SELECT SHMV.* 
      INTO returnResult 
      FROM S02V001T SHMV 
     WHERE SHMV.APKIHONCD = iApkihoncd 
       AND SHMV.SHOPCD    = iShopcd 
       AND SHMV.SHNCD     = iShncd 
    ;
    -- 商品マスタ有効チェック 
    IF returnResult.SHOPYUKOKBN <> '1' THEN 
       addError( iLineno, fileprop_rec(iColumnno).COLNAME
               , '【W041】'|| fileprop_rec(iColumnno).COLNAME ||'が無効です。'); 
       RETURN(returnResult);
    END IF; 
        
    RETURN(returnResult);
    -- 商品マスタ存在チェック
    EXCEPTION 
      WHEN NO_DATA_FOUND THEN 
        addError( iLineno, fileprop_rec(iColumnno).COLNAME
               , '【W002】'|| fileprop_rec(iColumnno).COLNAME ||'が存在しません。'); 
        RETURN(NULL);
    
  END checkShohinMST;
  
  /****************************************************************************/
  /* ファンクション名 ：商品マスタチェック（委託）                                   */
  /* 引数           ：iLineno         行番号                                    */
  /*                ：iColumnno       項目番号                                  */
  /*                ：iApkihoncd      ＡＰ基本コード（委託）                      */
  /*                ：iShopcd         ショップコード（委託）                      */
  /*                ：iTnshacd        商品コード                                */
  /****************************************************************************/
  FUNCTION checkAiteShohinMST( iLineno         IN NUMBER 
                             , iColumnno       IN NUMBER 
                             , iApkihoncd      IN VARCHAR2 
                             , iShopcd         IN VARCHAR2 
                             , iShncd          IN VARCHAR2 
  ) RETURN S02V001T%rowtype IS returnResult S02V001T%rowtype; 
  BEGIN 
    SELECT SHMV.* 
      INTO returnResult 
      FROM S02V001T SHMV 
     WHERE SHMV.APKIHONCD = iApkihoncd 
       AND SHMV.SHOPCD    = iShopcd 
       AND SHMV.SHNCD     = iShncd 
    ;
    -- 商品マスタ有効チェック 
    IF returnResult.SHOPYUKOKBN <> '1' THEN 
       addError( iLineno, fileprop_rec(iColumnno).COLNAME
               , '【W153】委託会社で取り扱っていない商品コードが含まれています。'); 
       RETURN(returnResult);
    END IF; 
        
    RETURN(returnResult);
    -- 商品マスタ存在チェック
    EXCEPTION 
      WHEN NO_DATA_FOUND THEN 
        addError( iLineno, fileprop_rec(iColumnno).COLNAME
               , '【W153】委託会社で取り扱っていない商品コードが含まれています。'); 
        RETURN(NULL);
    
  END checkAiteShohinMST;
  
  /****************************************************************************/
  /* ファンクション名 ：配布物マスタチェック                                        */
  /* 引数           ：iLineno         行番号                                    */
  /*                ：iColumnno       項目番号                                  */
  /*                ：iApkihoncd      ＡＰ基本コード                             */
  /*                ：iShopcd         ショップコード                             */
  /*                ：iHaifubutsucd   配布物コード                              */
  /****************************************************************************/
  FUNCTION checkHaifubutsuMST( iLineno         IN NUMBER 
                             , iColumnno       IN NUMBER 
                             , iApkihoncd      IN VARCHAR2 
                             , iShopcd         IN VARCHAR2 
                             , iHaifubutsucd   IN VARCHAR2 
  ) RETURN S02M035T%rowtype IS returnResult S02M035T%rowtype; 
  BEGIN 
    SELECT HFBM.* 
      INTO returnResult 
      FROM S02M035T HFBM 
     WHERE HFBM.APKIHONCD    = iApkihoncd 
       AND HFBM.SHOPCD       = iShopcd 
       AND HFBM.HAIFUBUTSUCD = iHaifubutsucd 
    ;
    -- 配布物マスタ有効チェック 
    IF returnResult.YUKOKBN <> '1' THEN 
       addError( iLineno, fileprop_rec(iColumnno).COLNAME
               , '【W041】'|| fileprop_rec(iColumnno).COLNAME ||'が無効です。'); 
       RETURN(returnResult);
    END IF; 
        
    RETURN(returnResult);
    -- 配布物マスタ存在チェック
    EXCEPTION 
      WHEN NO_DATA_FOUND THEN 
        addError( iLineno, fileprop_rec(iColumnno).COLNAME
               , '【W002】'|| fileprop_rec(iColumnno).COLNAME ||'が存在しません。'); 
        RETURN(NULL);
    
  END checkHaifubutsuMST;
  
  /****************************************************************************/
  /* ファンクション名 ：標準単価チェック                                           */
  /* 引数           ：iLineno         行番号                                    */
  /*                ：iColumnno       項目番号                                  */
  /*                ：iNohinunt       納品単価                                  */
  /*                ：iDlvryjissekidd 配達実績日                                */
  /*                ：iDlvrykbn       配達区分                                  */
  /*                ：iKeijokbn       計上区分                                  */
  /*                ：iTokuisaki      得意先                                   */
  /*                ：iShohin         商品                                     */
  /****************************************************************************/
  FUNCTION checkHyojununt( iLineno         IN NUMBER 
                         , iColumnno       IN NUMBER 
                         , iNohinunt       IN NUMBER 
                         , iDlvryjissekidd IN VARCHAR2 
                         , iDlvrykbn       IN VARCHAR2 
                         , iKeijokbn       IN VARCHAR2 
                         , iTokuisaki      IN S02M013T%rowtype 
                         , iShohin         IN S02V001T%rowtype 
  ) RETURN BOOLEAN IS returnResult BOOLEAN; 
  /* 変数宣言 */
  wPricekbn     VARCHAR(1) := NULL; -- 価格区分
  wHyojununt    NUMBER(10,2) := NULL; -- 標準価格
  wTokuisakiunt NUMBER(10,2) := NULL; -- 得意先別商品別価格
  wHansokuunt   NUMBER(10,2) := NULL; -- 販促価格
  wTokuisakisetunt NUMBER(10,2) := NULL; -- 得意先別セット商品別価格
  
  BEGIN 
    -- 価格区分
    wPricekbn := CASE WHEN iShohin.HYOJUNPRICEKIRIKAEDD IS NULL           THEN iShohin.SHINPRICEKBN 
                      WHEN iDlvryjissekidd < iShohin.HYOJUNPRICEKIRIKAEDD THEN iShohin.GENZPRICEKBN 
                                                                          ELSE iShohin.SHINPRICEKBN 
                 END;
    -- 価格区分：標準価格の場合のみチェック
    IF wPricekbn IS NULL OR wPricekbn <> '1' THEN 
       RETURN(TRUE);
    END IF; 
    -- 得意先区分：顧客の場合のみチェック 
    IF iTokuisaki.TOKUISAKIKBN <> '1' THEN 
       RETURN(TRUE); 
    END IF; 
    
    -- 標準価格
    wHyojununt := CASE WHEN iShohin.HYOJUNPRICEKIRIKAEDD IS NULL           THEN iShohin.SHINHYOJUNPRICE 
                       WHEN iDlvryjissekidd < iShohin.HYOJUNPRICEKIRIKAEDD THEN iShohin.GENZHYOJUNPRICE 
                                                                           ELSE iShohin.SHINHYOJUNPRICE 
                  END;
    -- 販促価格
    wHansokuunt := CASE WHEN iDlvryjissekidd BETWEEN iShohin.HANSOKUSTRTDD AND iShohin.HANSOKUSHRYDD 
                   THEN ( CASE WHEN iDlvrykbn = '3' THEN iShohin.HANSOKUMOCHIKAERIPRICE
                               ELSE iShohin.HANSOKUDLVRYPRICE 
                          END ) 
                   ELSE NULL END ;
    -- 得意先別商品別価格
    BEGIN 
      SELECT ( CASE WHEN iDlvrykbn = '3' THEN TKUM.MOCHIKAERIPRICE 
                    ELSE TKUM.DLVRYPRICE 
               END ) 
        INTO wTokuisakiunt 
        FROM S02M009T TKUM
        INNER JOIN S02M013T TKSM 
        ON ( TKSM.APKIHONCD = TKUM.APKIHONCD AND TKSM.SHOPCD = TKUM.SHOPCD 
            AND TKSM.TOKUISAKICD = TKUM.TOKUISAKICD 
            AND TKSM.RKNKINDTOKUBETSUUNTFLG = '1' -- 料金種別特別単価フラグ：1
           )
       WHERE TKUM.APKIHONCD   = iTokuisaki.APKIHONCD 
         AND TKUM.SHOPCD      = iTokuisaki.SHOPCD 
         AND TKUM.TOKUISAKICD = iTokuisaki.TOKUISAKICD 
         AND TKUM.SHNCD       = iShohin.SHNCD 
         AND iDlvryjissekidd BETWEEN TKUM.TEKIYOSTRTDD AND TKUM.TEKIYOSHRYDD 
         AND TKUM.YUKOKBN = '1'
      ;
      EXCEPTION 
        WHEN NO_DATA_FOUND THEN 
          NULL; 
    END; 
    
    -- 得意先別セット商品別価格
    BEGIN 
      SELECT 
          CASE WHEN iDlvrykbn = '3' THEN TSSM.MOCHIKAERIPRICE ELSE TSSM.DLVRYPRICE END
      INTO
          wTokuisakisetunt 
      FROM 
          S02M066T TSSM
          INNER JOIN S02M013T TKSM 
             ON (TKSM.APKIHONCD              = TSSM.APKIHONCD 
             AND TKSM.SHOPCD                 = TSSM.SHOPCD 
             AND TKSM.TOKUISAKICD            = TSSM.TOKUISAKICD 
             AND TKSM.RKNKINDTOKUBETSUUNTFLG = '1'              -- 料金種別特別単価フラグ：1
          )
      WHERE 
          TSSM.APKIHONCD   = iTokuisaki.APKIHONCD 
      AND TSSM.SHOPCD      = iTokuisaki.SHOPCD 
      AND TSSM.TOKUISAKICD = iTokuisaki.TOKUISAKICD 
      AND TSSM.STSHNCD     = iShohin.SHNCD 
      AND iDlvryjissekidd BETWEEN TSSM.TEKIYOSTRTDD AND TSSM.TEKIYOSHRYDD 
      AND TSSM.YUKOKBN     = '1'
      ;
    EXCEPTION 
      WHEN NO_DATA_FOUND THEN 
        NULL; 
    END; 
    
    -- チェック
    returnResult := TRUE;
    IF iKeijokbn = '01' THEN -- 計上区分：売上
       IF wTokuisakiunt IS NOT NULL THEN 
          IF iNohinunt <> wTokuisakiunt THEN -- 得意先別商品別価格
             returnResult := FALSE;
          END IF;
       ELSIF wTokuisakisetunt IS NOT NULL THEN 
          IF iNohinunt <> wTokuisakisetunt THEN -- 得意先別セット商品別価格
             returnResult := FALSE;
          END IF;
       ELSE 
          IF iNohinunt <> wHyojununt THEN    -- 標準価格
             returnResult := FALSE; 
          END IF; 
       END IF; 
    END IF; 
    IF iKeijokbn = '02' THEN -- 計上区分：キャンペーン
       IF wHansokuunt IS NOT NULL THEN
          IF iNohinunt <> wHansokuunt THEN   -- 販促価格
             returnResult := FALSE; 
          END IF; 
       ELSE 
          IF iNohinunt <> 0 THEN 
             returnResult := FALSE; -- 0
          END IF;
       END IF; 
    END IF;
    IF iKeijokbn = '08' THEN -- 計上区分：経費
       IF iNohinunt <> iShohin.ZAIKOUNT THEN 
          returnResult := FALSE; 
       END IF; 
    END IF;
    
    IF NOT returnResult THEN 
       addError(iLineno, fileprop_rec(iColumnno).COLNAME, '【W041】納品単価が無効です。');
       RETURN(FALSE);
    END IF; 
    
    RETURN(TRUE);
  END checkHyojununt;
  
  /****************************************************************************/
  /* ファンクション ：与信限度額チェック                                           */
  /* 引数           ：iLineno           行番号                                  */
  /*                ：iColumnno         項目番号                                */
  /*                ：iNohinunt         納品単価                                */
  /*                ：iNohinsuryo       納品数量                                */
  /*                ：iTokuisaki        得意先                                  */
  /****************************************************************************/
  FUNCTION checkYoshingendo( iLineno     IN NUMBER 
                           , iColumnno   IN NUMBER 
                           , iNohinunt   IN NUMBER 
                           , iNohinsuryo IN NUMBER 
                           , iTokuisaki  IN S02M013T%rowtype 
  ) RETURN BOOLEAN IS returnResult BOOLEAN; 
  /* 変数宣言 */
  wGenzaizd NUMBER(8,0) := 0; -- 現在残高
  wKeijomaeacptkin NUMBER(11) := 0; -- 計上前受注金額
  wUrikinhasukeisankbn      VARCHAR2(1)  := NULL;  -- 売上金額端数計算区分
  BEGIN 
    -- 与信限度額＝０の場合はチェック対象としない
    IF iTokuisaki.YOSHINGENDOKIN IS NULL OR iTokuisaki.YOSHINGENDOKIN = 0 THEN 
       RETURN(TRUE);
    END IF;
    
    SELECT TKJT.GENZAIZD
         , TKJT.KEIJOMAEACPTKIN
      INTO wGenzaizd 
         , wKeijomaeacptkin 
      FROM S02M050T TKJT 
     WHERE TKJT.APKIHONCD   = iTokuisaki.APKIHONCD 
       AND TKJT.SHOPCD      = iTokuisaki.SHOPCD 
       AND TKJT.TOKUISAKICD = iTokuisaki.TOKUISAKICD 
       AND TKJT.YUKOKBN = '1'
    ;
    -- 今回売上金額（納品数量＊納品単価）＋ 現在残高 ＋ 計上前受注金額 > 与信限度額の場合はエラー    
    -- ショップマスタ取得 
    SELECT
      SHPM.URIKINHASUKEISANKBN  -- 売上金額端数計算区分
    INTO wUrikinhasukeisankbn
    FROM
      S02M046T SHPM 
    WHERE
      SHPM.APKIHONCD = iTokuisaki.APKIHONCD 
      AND SHPM.SHOPCD = iTokuisaki.SHOPCD  
      AND SHPM.YUKOKBN = '1'
    ;
-- 本線対応変更 start ABI
--    IF (S01SyohizeiService.roundValue((iNohinunt * iNohinsuryo), wUrikinhasukeisankbn) + wGenzaizd + wKeijomaeacptkin) > iTokuisaki.YOSHINGENDOKIN THEN 
    IF (Z1808B01.roundValue((iNohinunt * iNohinsuryo), wUrikinhasukeisankbn) + wGenzaizd + wKeijomaeacptkin) > iTokuisaki.YOSHINGENDOKIN THEN 
-- 本線対応変更 end ABI
       addError(iLineno, fileprop_rec(iColumnno).COLNAME, '【W068】与信限度額を過ぎています。');
    END IF;
    
    RETURN(TRUE); 
    EXCEPTION 
      WHEN OTHERS THEN 
        RETURN(TRUE);
  END checkYoshingendo; 
  
  /****************************************************************************/
  /* ファンクション名 ：型式マスタチェック                                          */
  /* 引数           ：iLineno         行番号                                    */
  /*                ：iColumnno       項目番号                                  */
  /*                ：iApkihoncd      ＡＰ基本コード                             */
  /*                ：iShncd          商品コード                                */
  /*                ：iKatasiki       型式                                     */
  /****************************************************************************/
  FUNCTION checkKatasikiMST( iLineno         IN NUMBER 
                           , iColumnno       IN NUMBER 
                           , iApkihoncd      IN VARCHAR2 
                           , iShncd          IN VARCHAR2 
                           , iKatasiki       IN VARCHAR2 
  ) RETURN BOOLEAN IS returnResult BOOLEAN; 
  wYukokbn VARCHAR2(1) := NULL; -- 有効区分 
  BEGIN 
    SELECT KTSM.YUKOKBN 
      INTO wYukokbn 
      FROM S02M012T KTSM 
     WHERE KTSM.APKIHONCD = ( CASE WHEN SUBSTRB(iShncd, 1, 1) = '0' 
                                   THEN '001' -- 本部商品の場合は本部のＡＰ基本コードで検索
                                   ELSE iApkihoncd 
                              END ) 
       AND KTSM.SHNCD     = iShncd 
       AND KTSM.KATASIKI  = iKatasiki 
    ;
    -- 型式マスタ有効チェック 
    IF wYukokbn = '1' THEN 
       RETURN(TRUE); 
    ELSE 
       addError( iLineno, fileprop_rec(iColumnno).COLNAME
               , '【W041】'|| fileprop_rec(iColumnno).COLNAME ||'が無効です。'); 
       RETURN(FALSE);
    END IF; 
    
    RETURN(TRUE);
    -- 型式マスタ存在チェック
    EXCEPTION 
      WHEN NO_DATA_FOUND THEN 
        addError( iLineno, fileprop_rec(iColumnno).COLNAME
               , '【W002】'|| fileprop_rec(iColumnno).COLNAME ||'が存在しません。'); 
        RETURN(FALSE);
    
  END checkKatasikiMST;
  
  /****************************************************************************/
  /* ファンクション名 ：ウォーターサーバーシリアル管理マスタチェック                   */
  /* 引数           ：iLineno         行番号                                    */
  /*                ：iColumnno       項目番号                                  */
  /*                ：iStupnaiyo      設置内容                                  */
  /*                ：iSrlno          シリアル番号                               */
  /****************************************************************************/
  FUNCTION checkSRLWatersvrMST( iLineno         IN NUMBER 
                              , iColumnno       IN NUMBER 
                              , iStupnaiyo      IN VARCHAR2 
                              , iSrlno          IN VARCHAR2 
  ) RETURN S02M011T%rowtype IS returnResult S02M011T%rowtype; 
  BEGIN 
    
    SELECT WSVM.* 
      INTO returnResult 
      FROM S02M011T WSVM 
     WHERE WSVM.SRLNO   = iSrlno 
       AND WSVM.YUKOKBN = '1' 
    ;
    
    RETURN(returnResult);
    -- ウォーターサーバーシリアル管理マスタ存在チェック
    EXCEPTION 
      WHEN NO_DATA_FOUND THEN 
        addError( iLineno, fileprop_rec(iColumnno).COLNAME
               , '【W002】'|| fileprop_rec(iColumnno).COLNAME ||'が存在しません。'); 
        RETURN(NULL);
    
  END checkSRLWatersvrMST;
  
  /****************************************************************************/
  /* ファンクション名 ：設置先チェック                                             */
  /* 引数           ：iLineno         行番号                                    */
  /*                ：iColumnno       項目番号                                  */
  /*                ：iTokuisaki      得意先マスタ                               */
  /*                ：iWatersvr       ウォーターサーバーシリアル管理マスタ          */
  /*                ：iServer         得意先設置サーバー管理マスタ                 */
  /*                ：iStupnaiyo      設置内容                                  */
  /*                ：iHikiagenaiyo   引上内容                                  */
  /*                ：iDlvryjissekidd 配達実績日                                */
  /*                ：iKonkaimntdd    今回メンテナンス日                         */
  /*                ：iUnyoYmd        運用日付                                  */
  /****************************************************************************/
  FUNCTION checkStupsaki( iLineno         IN NUMBER 
                        , iColumnno       IN NUMBER 
                        , iTokuisaki      IN S02M013T%rowtype 
                        , iWatersvr       IN S02M011T%rowtype 
                        , iServer         IN S02M048T%rowtype 
                        , iStupnaiyo      IN VARCHAR2 
                        , iHikiagenaiyo   IN VARCHAR2 
                        , iDlvryjissekidd IN VARCHAR2 
                        , iKonkaimntdd    IN VARCHAR2 
                        , iUnyoYmd        IN VARCHAR2 
  ) RETURN BOOLEAN IS returnResult BOOLEAN; 
  isStup    BOOLEAN := FALSE; -- 設置
  isMent    BOOLEAN := FALSE; -- メンテナンス
  isHikiage BOOLEAN := FALSE; -- 引上 
  isPanel   BOOLEAN := FALSE; -- パネル
  BEGIN 
    -- 設置・メンテナンス・引上・パネル判定：
    -- 　　設置
    IF iStupnaiyo IS NOT NULL AND iDlvryjissekidd IS NOT NULL AND iDlvryjissekidd >= iWatersvr.PRCSDD THEN 
       IF iServer.HIKIAGEDD IS NULL THEN 
          isStup := TRUE; 
       END IF; 
    END IF; 
    -- 　　メンテナンス
    IF ( iKonkaimntdd IS NOT NULL AND checkDate('YYYYMMDD', iKonkaimntdd) AND iKonkaimntdd >= iWatersvr.PRCSDD ) 
       OR ( iKonkaimntdd IS NULL AND iServer.Konkaimntdd IS NOT NULL AND iServer.Konkaimntdd >= iWatersvr.PRCSDD ) THEN 
       IF iServer.HIKIAGEDD IS NULL THEN 
          isMent := TRUE; 
       END IF; 
    END IF; 
    -- 　　引上 
    IF iHikiagenaiyo IS NOT NULL AND iDlvryjissekidd IS NOT NULL AND iDlvryjissekidd >= iWatersvr.PRCSDD THEN 
       isHikiage := TRUE; 
    END IF; 
    -- 　　パネル
    IF NOT (isStup OR isMent OR isHikiage) THEN 
       IF iServer.HIKIAGEDD IS NULL AND iUnyoYmd >= iWatersvr.PRCSDD THEN 
          isPanel := TRUE; 
       END IF; 
    END IF; 
    
    -- 設置／メンテナンス／引上／パネルの場合、下記チェックを実行する
    IF isStup OR isMent OR isHikiage OR isPanel THEN 
    
       -- 設置先区分：'4'（得意先）
       IF iWatersvr.STUPSAKIKBN = '4' THEN 
          -- 同じ設置先得意先：ＯＫ
          IF iWatersvr.STUPSAKIAPKIHONCD = iTokuisaki.APKIHONCD AND iWatersvr.STUPSAKISHOPCD = iTokuisaki.SHOPCD 
             AND iWatersvr.STUPSAKITOKUISAKICD = iTokuisaki.TOKUISAKICD THEN 
             RETURN(TRUE);
          -- 異なる設置先ショップ
          ELSIF iWatersvr.STUPSAKIAPKIHONCD = iTokuisaki.APKIHONCD AND iWatersvr.STUPSAKISHOPCD <> iTokuisaki.SHOPCD THEN 
             -- メンテナンス／引上の場合：エラー
             IF isMent OR isHikiage THEN 
                addError( iLineno, fileprop_rec(iColumnno).COLNAME
                        , '【W050】他の得意先に設置中の時はそのサーバーの最新処理日より新しい日付の入力はできません。。'); 
                RETURN(FALSE);
             END IF;
          -- 上記以外：エラー 
          ELSE 
          addError( iLineno, fileprop_rec(iColumnno).COLNAME
                  , '【W050】他の得意先に設置中の時はそのサーバーの最新処理日より新しい日付の入力はできません。。'); 
          RETURN(FALSE);
       END IF; 
       
       -- 設置先区分：'2'（ＡＰ倉庫）
       ELSIF iWatersvr.STUPSAKIKBN = '2' THEN 
          -- 同じＡＰ
          IF iWatersvr.STUPSAKIAPKIHONCD = iTokuisaki.APKIHONCD THEN 
             RETURN(TRUE); 
          -- 異なるＡＰ：エラー
          ELSE 
             addError( iLineno, fileprop_rec(iColumnno).COLNAME
                     , '【W050】他の得意先に設置中の時はそのサーバーの最新処理日より新しい日付の入力はできません。。'); 
             RETURN(FALSE); 
          END IF; 
       
       -- 設置先区分：'3'（ショップ倉庫）
       ELSIF iWatersvr.STUPSAKIKBN = '3' THEN 
          -- 同じＡＰ
          IF iWatersvr.STUPSAKIAPKIHONCD = iTokuisaki.APKIHONCD THEN 
             RETURN(TRUE); 
          -- ショップ得意先で、設置先ショップコードが得意先ショップコード
          ELSIF iTokuisaki.TOKUISAKIKBN = '2' AND iTokuisaki.TOKUISAKISHOPCD IS NOT NULL 
             AND iWatersvr.STUPSAKISHOPCD = iTokuisaki.TOKUISAKISHOPCD THEN 
             -- 引上の場合：エラー
             IF isHikiage THEN 
                addError( iLineno, fileprop_rec(iColumnno).COLNAME
                        , '【W050】他の得意先に設置中の時はそのサーバーの最新処理日より新しい日付の入力はできません。。'); 
                RETURN(FALSE);
             END IF;
          -- 上記以外：エラー
          ELSE 
             addError( iLineno, fileprop_rec(iColumnno).COLNAME
                     , '【W050】他の得意先に設置中の時はそのサーバーの最新処理日より新しい日付の入力はできません。。'); 
             RETURN(FALSE);
          END IF; 
       
       -- 上記以外：設置先区分：'1'（ＡＣＩ倉庫）
       ELSE 
          addError( iLineno, fileprop_rec(iColumnno).COLNAME
                  , '【W050】最新の所在がＡＣＩの時はそのサーバーの最新処理日より新しい日付の入力はできません。。'); 
          RETURN(FALSE);
       END IF; 
    
    END IF; 
    
    RETURN(TRUE);
  END checkStupsaki;
  
  /****************************************************************************/
  /* ファンクション名 ：フロントパネルコード存在チェック                             */
  /* 引数           ：iLineno         行番号                                    */
  /*                ：iColumnno       項目番号                                  */
  /*                ：iApkihoncd      ＡＰ基本コード                             */
  /*                ：iShopcd         ショップコード                             */
  /*                ：iFrontpanelcd   フロントパネルＮｏ                         */
  /****************************************************************************/
  FUNCTION checkFrontpanel( iLineno         IN NUMBER 
                          , iColumnno       IN NUMBER 
                          , iApkihoncd      IN VARCHAR2 
                          , iShopcd         IN VARCHAR2 
                          , iFrontpanelcd   IN VARCHAR2 
  ) RETURN BOOLEAN IS returnResult BOOLEAN; 
  kbnShopyuko VARCHAR2(1) := NULL; -- ショップ有効区分 
  BEGIN 
    SELECT SHMV.SHOPYUKOKBN 
      INTO kbnShopyuko 
      FROM S02V001T SHMV 
     WHERE SHMV.APKIHONCD = iApkihoncd 
       AND SHMV.SHOPCD    = iShopcd 
       AND SHMV.SHNCD     = iFrontpanelcd 
    ;
    
    RETURN(TRUE);
    EXCEPTION 
      WHEN OTHERS THEN 
        addError( iLineno, fileprop_rec(iColumnno).COLNAME
                , '【W094】ショップ商品マスタに選択されたフロントパネルが設定されていません。');  
      RETURN(FALSE); 
    
  END checkFrontpanel;
  
  /****************************************************************************/
  /* ファンクション名 ：特別パネルシリアル管理マスタチェック                          */
  /* 引数           ：iLineno         行番号                                    */
  /*                ：iColumnno       項目番号                                  */
  /*                ：iPanelno        パネル番号                                */
  /****************************************************************************/
  FUNCTION checkSRLPanelMST( iLineno         IN NUMBER 
                           , iColumnno       IN NUMBER 
                           , iPanelno          IN VARCHAR2 
  ) RETURN S02M058T%rowtype IS returnResult S02M058T%rowtype; 
  BEGIN 
    SELECT SPNM.* 
      INTO returnResult 
      FROM S02M058T SPNM 
     WHERE SPNM.SRLNO   = iPanelno 
       AND SPNM.YUKOKBN = '1' 
    ;
    
    RETURN(returnResult);
    -- 特別パネルシリアル管理マスタ存在チェック
    EXCEPTION 
      WHEN NO_DATA_FOUND THEN 
        addError( iLineno, fileprop_rec(iColumnno).COLNAME
               , '【W002】'|| fileprop_rec(iColumnno).COLNAME ||'が存在しません。'); 
        RETURN(NULL);
    
  END checkSRLPanelMST;
  
  /****************************************************************************/
  /* ファンクション ：既存伝票明細取得（商品／代引き）                               */
  /* 引数           ：iApkihoncd           ＡＰ基本コード                        */
  /*                ：iShopcd              ショップコード                        */
  /*                ：iAcptdnpyno          受注伝票番号                          */
  /*                ：iEdano               枝番号                               */
  /*                ：iAcptdnpymeisaino    受注伝票明細番号                      */
  /*                ：iAcptdnpymeisaiedano 受注伝票明細枝番号                    */
  /****************************************************************************/
  FUNCTION getExistedShnMeisai( iApkihoncd  IN VARCHAR2 
                              , iShopcd     IN VARCHAR2 
                              , iAcptdnpyno IN VARCHAR2 
                              , iEdano      IN NUMBER 
                              , iAcptdnpymeisaino    IN NUMBER 
                              , iAcptdnpymeisaiedano IN NUMBER 
  ) RETURN S04T002T%rowtype IS returnResult S04T002T%rowtype; 
  BEGIN 
    SELECT JHMT.* 
      INTO returnResult 
      FROM S04T002T JHMT 
     WHERE JHMT.APKIHONCD  = iApkihoncd 
       AND JHMT.SHOPCD     = iShopcd 
       AND JHMT.ACPTDNPYNO = iAcptdnpyno 
       AND JHMT.EDANO      = iEdano 
       AND JHMT.ACPTDNPYMEISAINO    = iAcptdnpymeisaino 
       AND JHMT.ACPTDNPYMEISAIEDANO = iAcptdnpymeisaiedano 
       AND JHMT.MEISAIKBN IN ('0', '2') 
       AND JHMT.YUKOKBN = '1' 
    ; 
    RETURN(returnResult); 
    EXCEPTION 
      WHEN OTHERS THEN 
        RETURN(NULL); 
  END getExistedShnMeisai;
  
  /****************************************************************************/
  /* ファンクション ：既存得意先設置サーバー管理マスタ取得                            */
  /* 引数           ：iApkihoncd           ＡＰ基本コード                        */
  /*                ：iShopcd              ショップコード                        */
  /*                ：iTokuisakicd         得意先コード                          */
  /*                ：iShncd               商品コード                           */
  /*                ：iSrlno               シリアル番号                          */
  /****************************************************************************/
  FUNCTION getStupServer( iApkihoncd   IN VARCHAR2 
                        , iShopcd      IN VARCHAR2 
                        , iTokuisakicd IN VARCHAR2 
                        , iShncd       IN VARCHAR2 
                        , iSrlno       IN VARCHAR2 
  ) RETURN S02M048T%rowtype IS returnResult S02M048T%rowtype; 
  BEGIN 
    SELECT TSSM.* 
      INTO returnResult 
      FROM S02M048T TSSM 
     WHERE TSSM.APKIHONCD   = iApkihoncd 
       AND TSSM.SHOPCD      = iShopcd 
       AND TSSM.TOKUISAKICD = iTokuisakicd  
       AND TSSM.SHNCD       = iShncd  
       AND TSSM.SRLNO       = iSrlno 
       AND TSSM.YUKOKBN = '1' 
    ; 
    RETURN(returnResult); 
    EXCEPTION 
      WHEN OTHERS THEN 
        RETURN(NULL); 
  END getStupServer;
  
  /****************************************************************************/
  /* プロシージャ名 ：ファイル内容チェック                                         */
  /* 引数（ＩＮ）    ：file_rec          ファイル内容                             */
  /*                ：iTnshaapkihoncd   担当者ＡＰ基本コード                     */
  /*                ：iTnshashopcd      担当者ショップコード                     */
  /*                ：iTnshacd          担当者コード                            */
  /****************************************************************************/
  PROCEDURE checkCSV( file_rec        file_rec_type
                    , iTnshaapkihoncd IN VARCHAR2
                    , iTnshashopcd    IN VARCHAR2
                    , iTnshacd        IN VARCHAR2
  ) IS 
  /* 配列定義 */
  TYPE all_rec_type IS TABLE OF VARCHAR2(200) INDEX BY VARCHAR2(50); -- 文字型行データ
  /* 行処理用変数 */
  row_rec row_rec_type := row_rec_type(); -- 処理対象行
  current_rec  all_rec_type; -- 文字型行データ
  jisseki_rec  jisseki_rec_type := NULL; -- 属性変換済行データ
  columnno  NUMBER(4,0) := 0;     -- 項目番号
  itemValid BOOLEAN     := FALSE; -- 項目有効フラグ
  /* 同一伝票チェック用変数 */
  headerFLG BOOLEAN := TRUE; -- 同一伝票ヘッダフラグ
  header_rec all_rec_type; -- 同一伝票ヘッダデータ
  /* キー重複チェック用変数 */
  key_list key_list_type := key_list_type(); 
  key_rec  key_rec_type  := NULL; 
  /* その他チェック用変数 */
  wUnyoYmd VARCHAR2(8)      := NULL; -- 運用日付
  vRecordDnpy       S04T001T%rowtype := NULL; -- 既存受注伝票
  vRecordMeisai     S04T002T%rowtype := NULL; -- 既存受注伝票明細
  vRecordTokuisaki  S02M013T%rowtype := NULL; -- 得意先
  vRecordDlvrytnsha S02M003T%rowtype := NULL; -- 配達担当者
  vRecordSkysaki    S02M017T%rowtype := NULL; -- 請求先
  vRecordShohin     S02V001T%rowtype := NULL; -- 商品
  vRecordAiteShohin S02V001T%rowtype := NULL; -- 商品（委託）
  vRecordHaifubutsu S02M035T%rowtype := NULL; -- 配布物
  vRecordServer     S02M048T%rowtype := NULL; -- 得意先設置サーバー管理マスタ
  vRecordWatersvr   S02M011T%rowtype := NULL; -- ウォーターサーバーシリアル管理マスタ
  vRecordPanel      S02M058T%rowtype := NULL; -- 特別パネルシリアル管理マスタ
  BEGIN     
    wUnyoYmd := S01GETOPERATIONDATE(iTnshaapKihoncd, iTnshashopcd); -- 運用日付
    header_rec('ACCISDNPYNO') := NULL; 
    
    -- 実績データリスト
    FOR lineno IN 1..file_rec.LAST LOOP 
      -- 一行を取得する
      row_rec := file_rec(lineno); 
      jisseki_rec := NULL;
      columnno := 0;
      
      -- 項目数チェック：エラーの場合は次の一行に
      IF row_rec.LAST <> fileprop_rec.LAST THEN 
         error_rec.TOKUISAKICD := NULL;
         error_rec.SKYSAKICD   := NULL;
         addError(lineno, '', '【W026】ＣＳＶの項目数とテーブルの項目数が同じでない為、取込できません。'); 
         CONTINUE; 
      END IF; 
      
      -- 行データ読み込む
      FOR i IN 1..row_rec.LAST LOOP 
          current_rec(fileprop_rec(i).COLUMNID) := row_rec(i); 
      END LOOP;
      
      -- エラー情報に得意先コード、請求先コードを設定
      error_rec.TOKUISAKICD := current_rec('TOKUISAKICD');
      error_rec.SKYSAKICD := current_rec('SKYSAKICD');
      
      /* ＡＰ基本コード */
      columnno := columnno + 1; 
      -- 項目基本属性チェック
      IF checkBasics( lineno, columnno, current_rec('APKIHONCD') ) THEN 
         jisseki_rec.APKIHONCD := current_rec('APKIHONCD'); 
      END IF;
      -- 入力有時チェック
      IF jisseki_rec.APKIHONCD IS NOT NULL THEN 
         -- 他ＡＰチェック
         IF jisseki_rec.APKIHONCD <> iTnshaapkihoncd THEN 
            addError( lineno, fileprop_rec(columnno).COLNAME, '【W089】担当者とＡＰ基本コードが異なるデータは取込できません。');
         END IF;
         -- ＡＰ基本マスタチェック
         itemValid := checkApkihonMST(lineno, columnno, jisseki_rec.APKIHONCD); 
      END IF; 
      
      /* ショップコード */
      columnno := columnno + 1; 
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('SHOPCD') ) THEN 
         jisseki_rec.SHOPCD := current_rec('SHOPCD'); 
      END IF;
      -- 入力有時チェック
      IF jisseki_rec.SHOPCD IS NOT NULL THEN 
         -- 他ショップチェック
         IF jisseki_rec.SHOPCD <> iTnshashopcd THEN 
            addError( lineno, fileprop_rec(columnno).COLNAME, '【W089】担当者とショップコードが異なるデータは取込できません。');
         END IF;
         -- ショップマスタチェック
         IF jisseki_rec.APKIHONCD IS NOT NULL THEN 
            itemValid := checkShopMST(lineno, columnno, jisseki_rec.APKIHONCD, jisseki_rec.SHOPCD); 
         END IF; 
      END IF; 
      
      /* ＡＣＣＩＳ伝票番号 */
      columnno := columnno + 1; 
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('ACCISDNPYNO') ) THEN 
         jisseki_rec.ACCISDNPYNO := current_rec('ACCISDNPYNO'); 
      END IF;
      -- 必須チェック
      IF current_rec('ACPTKBN') IS NOT NULL -- 受注区分入力あり：配達実績の場合
         OR current_rec('SRLNO') IS NOT NULL -- シリアル番号設定あり：サーバー実績の場合
         OR current_rec('STUPNAIYO') IS NOT NULL -- 設置内容設定あり：サーバー実績の場合
      THEN 
         IF current_rec('ACCISDNPYNO') IS NULL THEN 
            addError( lineno, fileprop_rec(columnno).COLNAME
                    , '【W041】'|| fileprop_rec(columnno).COLNAME ||'を指定して下さい。'); 
         END IF;
      END IF;
      -- 同一伝票チェック：初期化
      IF jisseki_rec.ACCISDNPYNO IS NOT NULL AND jisseki_rec.ACCISDNPYNO = header_rec('ACCISDNPYNO') THEN 
         headerFLG := FALSE; 
      ELSE 
         headerFLG := TRUE; 
         header_rec.DELETE; 
         header_rec('ACCISDNPYNO') := jisseki_rec.ACCISDNPYNO; 
         vRecordDnpy := NULL;
      END IF; 
      
      /* ＡＣＣＩＳ伝票番号枝番 */
      columnno := columnno + 1; 
      IF headerFLG THEN 
         -- ヘッダ値セット
         header_rec('ACCISDNPYEDANO') := current_rec('ACCISDNPYEDANO'); 
         
         -- 項目基本属性チェック
         IF checkBasics(lineno, columnno, current_rec('ACCISDNPYEDANO') ) THEN 
            jisseki_rec.ACCISDNPYEDANO := TO_NUMBER(current_rec('ACCISDNPYEDANO')); 
         END IF;
         -- 実績データ：必須チェック
         IF current_rec('ACCISDNPYNO') IS NOT NULL 
            AND current_rec('ACCISDNPYEDANO') IS NULL 
         THEN 
            addError( lineno, fileprop_rec(columnno).COLNAME
                    , '【W041】'|| fileprop_rec(columnno).COLNAME ||'を指定して下さい。'); 
         END IF; 
         -- 入力有：配達中伝票存在チェック
         IF jisseki_rec.APKIHONCD IS NOT NULL AND jisseki_rec.SHOPCD IS NOT NULL 
            AND jisseki_rec.ACCISDNPYNO IS NOT NULL AND jisseki_rec.ACCISDNPYEDANO IS NOT NULL THEN 
            vRecordDnpy := checkDnpyExist( lineno, columnno
                                         , jisseki_rec.APKIHONCD, jisseki_rec.SHOPCD
                                         , jisseki_rec.ACCISDNPYNO, jisseki_rec.ACCISDNPYEDANO );
         END IF;
      ELSE 
         -- 同一伝票チェック
         itemValid := checkSameDnpy( lineno, columnno, current_rec('ACCISDNPYEDANO'), header_rec('ACCISDNPYEDANO') ); 
         jisseki_rec.ACCISDNPYEDANO := toNumberOrNull(header_rec('ACCISDNPYEDANO')); 
      END IF; 
      
      /* 受注区分 */ 
      columnno := columnno + 1;
      IF headerFLG THEN 
         -- ヘッダ値セット
         header_rec('ACPTKBN') := current_rec('ACPTKBN');
         -- 項目基本属性チェック
         IF checkBasics(lineno, columnno, current_rec('ACPTKBN') ) THEN 
            jisseki_rec.ACPTKBN := current_rec('ACPTKBN'); 
            
            -- 入力有：システム管理マスタチェック
            IF jisseki_rec.ACPTKBN IS NOT NULL THEN 
               itemValid := checkSystemMST(lineno, columnno, '000', '000', '052', jisseki_rec.ACPTKBN, NULL, NULL );
            END IF;
         END IF;
      ELSE 
         -- 同一伝票チェック
         itemValid := checkSameDnpy(lineno, columnno, current_rec('ACPTKBN'), header_rec('ACPTKBN'));
         jisseki_rec.ACPTKBN := header_rec('ACPTKBN');
      END IF;
      
      /* 受注区分詳細 */ 
      columnno := columnno + 1;
      IF headerFLG THEN 
         -- ヘッダ値セット
         header_rec('ACPTKBNSHS') := current_rec('ACPTKBNSHS');
         -- 項目基本属性チェック
         IF checkBasics(lineno, columnno, current_rec('ACPTKBNSHS') ) THEN 
            jisseki_rec.ACPTKBNSHS := current_rec('ACPTKBNSHS'); 
            
            -- 入力有：システム管理マスタチェック
            IF jisseki_rec.ACPTKBNSHS IS NOT NULL AND jisseki_rec.ACPTKBN IS NOT NULL THEN 
               itemValid := checkSystemMST(lineno, columnno, '000', '000', '053', jisseki_rec.ACPTKBNSHS, '052', jisseki_rec.ACPTKBN );
            END IF;
         END IF; 
         -- 受注区分との入力チェック 
         IF current_rec('ACPTKBN') IS NOT NULL AND current_rec('ACPTKBNSHS') IS NULL THEN 
            addError( lineno, fileprop_rec(columnno).COLNAME, '【W031】「受注詳細区分」を入力して下さい。'); 
         END IF; 
      ELSE 
         -- 同一伝票チェック
         itemValid := checkSameDnpy(lineno, columnno, current_rec('ACPTKBNSHS'), header_rec('ACPTKBNSHS'));
         jisseki_rec.ACPTKBNSHS := header_rec('ACPTKBNSHS');
      END IF;
      
      /* 得意先コード */
      columnno := columnno + 1;
      IF headerFLG THEN 
         -- ヘッダ値セット
         header_rec('TOKUISAKICD') := current_rec('TOKUISAKICD'); 
         vRecordTokuisaki := NULL; 
         -- 項目基本属性チェック
         IF checkBasics(lineno, columnno, current_rec('TOKUISAKICD') ) THEN 
            jisseki_rec.TOKUISAKICD := current_rec('TOKUISAKICD'); 
         END IF;
         -- 実績データ：必須チェック
         IF current_rec('ACCISDNPYNO') IS NOT NULL 
            AND current_rec('TOKUISAKICD') IS NULL 
         THEN 
            addError( lineno, fileprop_rec(columnno).COLNAME
                    , '【W041】'|| fileprop_rec(columnno).COLNAME ||'を指定して下さい。'); 
         END IF; 
         -- 入力有時チェック
         IF jisseki_rec.TOKUISAKICD IS NOT NULL THEN 
            -- 得意先マスタチェック
            IF jisseki_rec.APKIHONCD IS NOT NULL AND jisseki_rec.SHOPCD IS NOT NULL THEN 
               vRecordTokuisaki := checkTokuisakiMST(lineno, columnno, jisseki_rec.APKIHONCD, jisseki_rec.SHOPCD, jisseki_rec.TOKUISAKICD); 
            END IF; 
         END IF; 
      ELSE 
         -- 同一伝票チェック
         itemValid := checkSameDnpy(lineno, columnno, current_rec('TOKUISAKICD'), header_rec('TOKUISAKICD'));
         jisseki_rec.TOKUISAKICD := header_rec('TOKUISAKICD');
      END IF;
      
      /* 配達実績日 */
      columnno := columnno + 1;
      IF headerFLG THEN 
         -- ヘッダ値セット
         header_rec('DLVRYJISSEKIDD') := current_rec('DLVRYJISSEKIDD');
         -- 項目基本属性チェック
         IF checkBasics(lineno, columnno, current_rec('DLVRYJISSEKIDD') ) THEN 
            jisseki_rec.DLVRYJISSEKIDD := current_rec('DLVRYJISSEKIDD'); 
         END IF;
         -- 実績データ：必須チェック
         IF current_rec('ACCISDNPYNO') IS NOT NULL 
            AND current_rec('DLVRYJISSEKIDD') IS NULL 
         THEN 
            addError( lineno, fileprop_rec(columnno).COLNAME
                    , '【W041】'|| fileprop_rec(columnno).COLNAME ||'を指定して下さい。'); 
         END IF; 
         -- 入力有時チェック
         IF jisseki_rec.DLVRYJISSEKIDD IS NOT NULL THEN 
            -- 過去日チェック：運用日付より未来はエラー
            itemValid := checkPastFutureDate( lineno, columnno
                                            , jisseki_rec.DLVRYJISSEKIDD, fileprop_rec(columnno).COLNAME
                                            , wUnyoYmd, '運用日付');
            -- 配達実績日妥当性チェック
            IF vRecordDnpy.ACPTDD IS NOT NULL THEN 
               itemValid := checkPastFutureDate( lineno, columnno 
                                               , vRecordDnpy.ACPTDD, '受注日'
                                               , jisseki_rec.DLVRYJISSEKIDD,  fileprop_rec(columnno).COLNAME );
            END IF; 
         END IF; 
      ELSE 
         -- 同一伝票チェック
         itemValid := checkSameDnpy(lineno, columnno, current_rec('DLVRYJISSEKIDD'), header_rec('DLVRYJISSEKIDD'));
         jisseki_rec.DLVRYJISSEKIDD := header_rec('DLVRYJISSEKIDD');
      END IF;
      
      /* 配達担当者コード */
      columnno := columnno + 1;
      IF headerFLG THEN 
         -- ヘッダ値セット
         header_rec('DLVRYTNSHACD') := current_rec('DLVRYTNSHACD');
         vRecordDlvrytnsha := NULL; 
         -- 項目基本属性チェック
         IF checkBasics(lineno, columnno, current_rec('DLVRYTNSHACD') ) THEN 
            jisseki_rec.DLVRYTNSHACD := current_rec('DLVRYTNSHACD'); 
         END IF;
         -- 配達データ：入力チェック
         IF current_rec('ACPTKBN') IS NOT NULL THEN 
            -- 入力チェック：新規伝票は自社固定ので、担当者が必須
            IF vRecordDnpy.APKIHONCD IS NULL 
               AND current_rec('DLVRYTNSHACD') IS NULL THEN 
                   addError(lineno, fileprop_rec(columnno).COLNAME, '【W041】配達担当者コードを指定して下さい。'); 
            -- 入力チェック：既存伝票は配達区分よりチェック
            ELSE 
               -- 配達担当者入力チェック：持ち帰り 
               IF vRecordDnpy.DLVRYKBN IS NOT NULL AND vRecordDnpy.DLVRYKBN = '3' THEN 
                  IF current_rec('DLVRYTNSHACD') IS NOT NULL THEN 
                     addError(lineno, fileprop_rec(columnno).COLNAME, '【W089】配達担当者コードは持ち帰りの場合は入力できません。'); 
                  END IF;
               END IF;
               -- 配達担当者入力チェック：配達 
               IF vRecordDnpy.DLVRYKBN IS NOT NULL AND vRecordDnpy.DLVRYKBN IN ('1', '2') THEN 
                  IF current_rec('DLVRYTNSHACD') IS NULL THEN 
                     addError(lineno, fileprop_rec(columnno).COLNAME, '【W041】配達担当者コードを指定して下さい。'); 
                  END IF;
               END IF;
            END IF; 
         END IF; 
         -- 入力有時チェック
         IF jisseki_rec.DLVRYTNSHACD IS NOT NULL THEN 
            -- 担当者マスタチェック
            IF jisseki_rec.APKIHONCD IS NOT NULL AND jisseki_rec.SHOPCD IS NOT NULL THEN 
               vRecordDlvrytnsha := checkTnshaMST( lineno, columnno
                                                 , jisseki_rec.APKIHONCD, jisseki_rec.SHOPCD
                                                 , jisseki_rec.DLVRYTNSHACD, 'DLVRY'); 
            END IF; 
            -- 配達担当者と妥当性チェック：既存伝票の場合のみ 
            IF vRecordDnpy.APKIHONCD IS NOT NULL 
               AND vRecordDnpy.DLVRYKBN = '2' AND vRecordDlvrytnsha.TNSHACD IS NOT NULL THEN 
               IF vRecordDlvrytnsha.SHOZOKUDLVRYCMPNYCD IS NULL OR vRecordDnpy.DLVRYCMPNYCD IS NULL 
                  OR vRecordDlvrytnsha.SHOZOKUDLVRYCMPNYCD <> vRecordDnpy.DLVRYCMPNYCD THEN 
                  addError(lineno, fileprop_rec(columnno).COLNAME, '【W044】配達会社と担当者の所属配達会社が違います。'); 
               END IF; 
            END IF;
         END IF; 
      ELSE 
         -- 同一伝票チェック
         itemValid := checkSameDnpy(lineno, columnno, current_rec('DLVRYTNSHACD'), header_rec('DLVRYTNSHACD'));
         jisseki_rec.DLVRYTNSHACD := header_rec('DLVRYTNSHACD');
      END IF;
      
      /* 請求先コード */
      columnno := columnno + 1;
      IF headerFLG THEN 
         -- ヘッダ値セット
         header_rec('SKYSAKICD') := current_rec('SKYSAKICD');
         vRecordSkysaki := NULL; 
         -- 項目基本属性チェック
         IF checkBasics(lineno, columnno, current_rec('SKYSAKICD') ) THEN 
            jisseki_rec.SKYSAKICD := current_rec('SKYSAKICD'); 
         END IF; 
         -- 入金データ：必須チェック
         IF ( current_rec('NYKNKIND1') IS NOT NULL OR current_rec('NYKNKIND2') IS NOT NULL ) 
            AND current_rec('SKYSAKICD') IS NULL 
         THEN 
            addError( lineno, fileprop_rec(columnno).COLNAME
                    , '【W041】'|| fileprop_rec(columnno).COLNAME ||'を指定して下さい。'); 
         END IF; 
         -- 入力有時チェック
         IF jisseki_rec.SKYSAKICD IS NOT NULL THEN 
            -- 請求先マスタチェック
            IF jisseki_rec.APKIHONCD IS NOT NULL AND jisseki_rec.SHOPCD IS NOT NULL THEN 
               vRecordSkysaki := checkSkysakiMST( lineno, columnno
                                                , jisseki_rec.APKIHONCD, jisseki_rec.SHOPCD 
                                                , jisseki_rec.SKYSAKICD ); 
            END IF; 
         END IF; 
      ELSE 
         -- 同一伝票チェック
         itemValid := checkSameDnpy(lineno, columnno, current_rec('SKYSAKICD'), header_rec('SKYSAKICD'));
         jisseki_rec.SKYSAKICD := header_rec('SKYSAKICD');
      END IF;
      
      /* 入金種別１ */
      columnno := columnno + 1;
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('NYKNKIND1') ) THEN 
         jisseki_rec.NYKNKIND1 := current_rec('NYKNKIND1'); 
      END IF;
      -- 入力有時チェック
      IF jisseki_rec.NYKNKIND1 IS NOT NULL THEN 
         -- システム管理マスタ存在チェック IITEM
         itemValid := checkSystemMST(lineno, columnno, '000', '000', '050', jisseki_rec.NYKNKIND1, NULL, NULL );
      END IF; 
      
      /* NULL関連性チェック（入金種別１、入金金額１）*/
      itemValid := checkNullKanren(lineno, current_rec('NYKNKIND1'), '入金種別１'
                                         , current_rec('NYKNKIN1'), '入金金額１');
      
      /* 入金金額１ */
      columnno := columnno + 1;
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('NYKNKIN1') ) THEN 
         jisseki_rec.NYKNKIN1 := TO_NUMBER(current_rec('NYKNKIN1')); 
      END IF;
            
      /* 13：入金種別２ */
      columnno := columnno + 1;
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('NYKNKIND2') ) THEN 
         jisseki_rec.NYKNKIND2 := current_rec('NYKNKIND2'); 
      END IF;
      -- 入力有時チェック
      IF jisseki_rec.NYKNKIND2 IS NOT NULL THEN 
         -- システム管理マスタ存在チェック 
         itemValid := checkSystemMST(lineno, columnno, '000', '000', '050', jisseki_rec.NYKNKIND2, NULL, NULL );
      END IF; 
      
      /* NULL関連性チェック（入金種別１、入金金額１）*/
      itemValid := checkNullKanren(lineno, current_rec('NYKNKIND2'), '入金種別２'
                                         , current_rec('NYKNKIN2'), '入金金額２');
      
      /* 入金金額２ */
      columnno := columnno + 1;
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('NYKNKIN2') ) THEN 
         jisseki_rec.NYKNKIN2 := TO_NUMBER(current_rec('NYKNKIN2')); 
      END IF;
      
      /* 回収実績ボトルレギュラー本数 */ 
      columnno := columnno + 1; 
      IF headerFLG THEN 
         -- ヘッダ値セット
         header_rec('KZBTLRGRAHNS') := current_rec('KZBTLRGRAHNS');
         -- 項目基本属性チェック
         IF checkBasics(lineno, columnno, current_rec('KZBTLRGRAHNS') ) THEN 
            jisseki_rec.KZBTLRGRAHNS := TO_NUMBER(current_rec('KZBTLRGRAHNS')); 
         END IF;
      ELSE 
         -- 同一伝票チェック
         itemValid := checkSameDnpy(lineno, columnno, current_rec('KZBTLRGRAHNS'), header_rec('KZBTLRGRAHNS'));
         jisseki_rec.KZBTLRGRAHNS := toNumberOrNull(header_rec('KZBTLRGRAHNS'));
      END IF;
      
      /* 回収実績ボトルスリム本数 */ 
      columnno := columnno + 1;
      IF headerFLG THEN 
         -- ヘッダ値セット
         header_rec('KZBTLSLMHNS') := current_rec('KZBTLSLMHNS');
         -- 項目基本属性チェック
         IF checkBasics(lineno, columnno, current_rec('KZBTLSLMHNS') ) THEN 
            jisseki_rec.KZBTLSLMHNS := TO_NUMBER(current_rec('KZBTLSLMHNS')); 
         END IF;
      ELSE 
         -- 同一伝票チェック
         itemValid := checkSameDnpy(lineno, columnno, current_rec('KZBTLSLMHNS'), header_rec('KZBTLSLMHNS'));
         jisseki_rec.KZBTLSLMHNS := toNumberOrNull(header_rec('KZBTLSLMHNS'));
      END IF;
      
      /* 配達ステータス */ 
      columnno := columnno + 1;
      IF headerFLG THEN 
         -- ヘッダ値セット
         header_rec('DLVRYSTS') := current_rec('DLVRYSTS');
         -- 項目基本属性チェック
         IF checkBasics(lineno, columnno, current_rec('DLVRYSTS') ) THEN 
            jisseki_rec.DLVRYSTS := current_rec('DLVRYSTS'); 
         END IF;
         -- 配達データ：必須チェック
         IF current_rec('ACPTKBN') IS NOT NULL 
            AND current_rec('DLVRYSTS') IS NULL 
         THEN 
            addError( lineno, fileprop_rec(columnno).COLNAME
                    , '【W041】'|| fileprop_rec(columnno).COLNAME ||'を指定して下さい。'); 
         END IF; 
         -- 入力有：配達ステータス妥当性チェック：
         IF jisseki_rec.DLVRYSTS IS NOT NULL THEN 
            -- 右記以外エラー(12：配達済 13：未配達 14：不在未配達 19：配達時キャンセル) 
            IF jisseki_rec.DLVRYSTS NOT IN ( '12', '13', '14', '19' ) THEN 
               addError(lineno, fileprop_rec(columnno).COLNAME, '【W041】配達ステータスが無効です。'); 
            END IF; 
         END IF; 
      ELSE 
         -- 同一伝票チェック
         itemValid := checkSameDnpy(lineno, columnno, current_rec('DLVRYSTS'), header_rec('DLVRYSTS'));
         jisseki_rec.DLVRYSTS := header_rec('DLVRYSTS');
      END IF;
      
      /* 次回配達予定日 */
      columnno := columnno + 1; 
      IF headerFLG THEN 
         -- ヘッダ値セット
         header_rec('JIKAIDLVRYYTIDD') := current_rec('JIKAIDLVRYYTIDD');
         -- 項目基本属性チェック
         IF checkBasics(lineno, columnno, current_rec('JIKAIDLVRYYTIDD') ) THEN 
            jisseki_rec.JIKAIDLVRYYTIDD := current_rec('JIKAIDLVRYYTIDD'); 
         END IF;
         -- 次回配達予定日NULLチェック
         IF jisseki_rec.DLVRYSTS IS NOT NULL THEN 
            IF jisseki_rec.DLVRYSTS IN ('13', '14') AND jisseki_rec.JIKAIDLVRYYTIDD IS NULL THEN -- 配達ステータスが：13：未配達／14：不在未配達 の場合
               addError(lineno, fileprop_rec(columnno).COLNAME, '【W030】次回配達予定日を指定して下さい。'); 
            END IF; 
            IF jisseki_rec.DLVRYSTS NOT IN ('13', '14') AND jisseki_rec.JIKAIDLVRYYTIDD IS NOT NULL THEN -- その他配達ステータス：入力不可
               addError(lineno, fileprop_rec(columnno).COLNAME, '【W041】次回配達予定日が無効です。'); 
            END IF; 
         END IF; 
      ELSE 
         -- 同一伝票チェック
         itemValid := checkSameDnpy(lineno, columnno, current_rec('JIKAIDLVRYYTIDD'), header_rec('JIKAIDLVRYYTIDD'));
         jisseki_rec.JIKAIDLVRYYTIDD := header_rec('JIKAIDLVRYYTIDD');
      END IF;
      
      /* 不在置き実績フラグ */ 
      columnno := columnno + 1;
      IF headerFLG THEN 
         -- ヘッダ値セット
         header_rec('FZAIOKIJISSEKIFLG') := current_rec('FZAIOKIJISSEKIFLG');
         -- 項目基本属性チェック
         IF checkBasics(lineno, columnno, current_rec('FZAIOKIJISSEKIFLG') ) THEN 
            jisseki_rec.FZAIOKIJISSEKIFLG := current_rec('FZAIOKIJISSEKIFLG'); 
         END IF;
      ELSE 
         -- 同一伝票チェック
         itemValid := checkSameDnpy(lineno, columnno, current_rec('FZAIOKIJISSEKIFLG'), header_rec('FZAIOKIJISSEKIFLG'));
         jisseki_rec.FZAIOKIJISSEKIFLG := header_rec('FZAIOKIJISSEKIFLG');
      END IF;
      
      jisseki_rec.DLVRYKBN := NVL(vRecordDnpy.DLVRYKBN, '1');
      jisseki_rec.DLVRYCMPNYCD := NULL; 
      
      /* 明細部 */ 
      
      /* 受注伝票明細番号 */ 
      columnno := columnno + 1; 
-- インボイス対応追加 start
      IF current_rec('SHNCD') NOT IN ('099901', '099902') THEN -- 消費税商品データ以外の場合、チェック
-- インボイス対応追加 end
         -- 項目基本属性チェック
         IF checkBasics(lineno, columnno, current_rec('ACPTDNPYMEISAINO') ) THEN 
            jisseki_rec.ACPTDNPYMEISAINO := TO_NUMBER(current_rec('ACPTDNPYMEISAINO')); 
         END IF; 
         -- 実績データ：必須チェック
         IF current_rec('ACCISDNPYNO') IS NOT NULL 
            AND current_rec('ACPTDNPYMEISAINO') IS NULL 
         THEN 
            addError( lineno, fileprop_rec(columnno).COLNAME
                    , '【W041】'|| fileprop_rec(columnno).COLNAME ||'を指定して下さい。'); 
         END IF; 
         -- 入力有時チェック
         IF jisseki_rec.ACPTDNPYMEISAINO IS NOT NULL THEN 
            -- 重複チェック 
            IF jisseki_rec.APKIHONCD IS NOT NULL AND jisseki_rec.SHOPCD IS NOT NULL 
               AND jisseki_rec.ACCISDNPYNO IS NOT NULL THEN 
               
               key_rec.LINENO := lineno; 
               key_rec.APKIHONCD        := jisseki_rec.APKIHONCD; 
               key_rec.SHOPCD           := jisseki_rec.SHOPCD; 
               key_rec.ACCISDNPYNO      := jisseki_rec.ACCISDNPYNO; 
               key_rec.ACPTDNPYMEISAINO := jisseki_rec.ACPTDNPYMEISAINO; 
               
               IF checkDistinctKey( key_list, key_rec ) THEN 
                  key_list.EXTEND; 
                  key_list(key_list.LAST) := key_rec; 
               END IF; 
            END IF; 
         END IF; 
-- インボイス対応追加 start
      END IF; 
-- インボイス対応追加 end
      
      /* 受注伝票明細枝番号 */
      columnno := columnno + 1;
-- インボイス対応追加 start
      IF current_rec('SHNCD') NOT IN ('099901', '099902') THEN -- 消費税商品データ以外の場合、チェック
-- インボイス対応追加 end
         -- 項目基本属性チェック
         IF checkBasics(lineno, columnno, current_rec('ACPTDNPYMEISAIEDANO') ) THEN 
            jisseki_rec.ACPTDNPYMEISAIEDANO := TO_NUMBER(current_rec('ACPTDNPYMEISAIEDANO')); 
         END IF; 
         -- 実績データ：必須チェック
         IF current_rec('ACCISDNPYNO') IS NOT NULL 
            AND current_rec('ACPTDNPYMEISAIEDANO') IS NULL 
         THEN 
            addError( lineno, fileprop_rec(columnno).COLNAME
                    , '【W041】'|| fileprop_rec(columnno).COLNAME ||'を指定して下さい。'); 
         END IF; 
-- インボイス対応追加 start
      END IF; 
-- インボイス対応追加 end
      
      /* 既存伝票明細取得 */
      vRecordMeisai := NULL; 
      IF vRecordDnpy.APKIHONCD IS NOT NULL 
         AND jisseki_rec.ACPTDNPYMEISAINO IS NOT NULL AND jisseki_rec.ACPTDNPYMEISAIEDANO IS NOT NULL THEN 
         vRecordMeisai := getExistedShnMeisai( vRecordDnpy.APKIHONCD, vRecordDnpy.SHOPCD, vRecordDnpy.ACPTDNPYNO, vRecordDnpy.EDANO 
                                             , jisseki_rec.ACPTDNPYMEISAINO, jisseki_rec.ACPTDNPYMEISAIEDANO ); 
      END IF; 
      
      /* 商品コード */
      columnno := columnno + 1;
      vRecordShohin     := NULL;
      vRecordAiteShohin := NULL;
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('SHNCD') ) THEN 
         jisseki_rec.SHNCD := current_rec('SHNCD'); 
         -- 入力有時チェック
         IF jisseki_rec.SHNCD IS NOT NULL THEN 
-- インボイス対応追加 start
            IF current_rec('SHNCD') NOT IN ('099901', '099902') THEN -- 消費税商品データ以外の場合、チェック
-- インボイス対応追加 end
               -- 商品マスタチェック
               IF jisseki_rec.APKIHONCD IS NOT NULL AND jisseki_rec.SHOPCD IS NOT NULL THEN 
                  vRecordShohin := checkShohinMST(lineno, columnno, jisseki_rec.APKIHONCD, jisseki_rec.SHOPCD, jisseki_rec.SHNCD);
               END IF; 
               -- 商品行明細区分チェック：既存伝票明細の場合
               IF vRecordMeisai.ACPTDNPYMEISAINO IS NOT NULL THEN 
                  IF vRecordMeisai.MEISAIKBN NOT IN ( '0', '2' ) 
                     OR vRecordMeisai.SHNCD <> jisseki_rec.SHNCD THEN 
                     addError(lineno, fileprop_rec(columnno).COLNAME, '【W044】取込対象と既存伝票明細が違います。'); 
                  END IF; 
               END IF; 
               -- 商品販売期間外チェック
               IF jisseki_rec.DLVRYJISSEKIDD IS NOT NULL AND vRecordShohin.SHNCD IS NOT NULL THEN 
                  IF vRecordShohin.HANBAISTRTDD IS NULL OR vRecordShohin.HANBAISHRYDD IS NULL 
                     OR jisseki_rec.DLVRYJISSEKIDD < vRecordShohin.HANBAISTRTDD OR jisseki_rec.DLVRYJISSEKIDD > vRecordShohin.HANBAISHRYDD THEN 
                     addError(lineno, fileprop_rec(columnno).COLNAME, '【W088】「'|| fileprop_rec(columnno).COLNAME ||'」は販売期間外です。'); 
                  END IF; 
               END IF; 
               -- 商品得意先区分チェック
               IF vRecordTokuisaki.TOKUISAKICD IS NOT NULL AND vRecordShohin.SHNCD IS NOT NULL THEN 
                  IF vRecordTokuisaki.TOKUISAKIKBN = '9' AND vRecordShohin.STKBN = '1' THEN -- 経費得意先＋セット商品はエラー
                     addError(lineno, fileprop_rec(columnno).COLNAME, '【W041】経費得意先の場合セット商品が無効です。');
                  END IF; 
               END IF; 
               -- 商品取扱区分チェック
               IF vRecordShohin.SHNCD IS NOT NULL THEN 
                  IF vRecordShohin.TRTSKIKBN = '2' THEN -- 取扱区分が 2：仕入のみ はエラー
                     addError(lineno, fileprop_rec(columnno).COLNAME, '【W041】商品コードの取扱区分が無効です。');
                  END IF; 
               END IF; 
               -- キャンペーン時セット商品チェック
               IF vRecordShohin.SHNCD IS NOT NULL AND current_rec('KEIJOKBN') IS NOT NULL 
                  THEN 
                  IF vRecordShohin.STKBN = '1' AND current_rec('KEIJOKBN') = '02' THEN 
                     addError(lineno, fileprop_rec(columnno).COLNAME, '【W042】計上区分がキャンペーンの場合はセット商品は入力できません。');
                  END IF; 
               END IF;
               -- 委受託伝票の場合
               IF vRecordDnpy.ITAKUAITEACPTDNPYNO IS NOT NULL THEN 
                  -- 商品マスタ存在チェック（委託）
                  vRecordAiteShohin := checkAiteShohinMST(lineno, columnno, vRecordDnpy.ITAKUAITEAPKIHONCD, vRecordDnpy.ITAKUAITESHOPCD, jisseki_rec.SHNCD);
                  -- 独自商品チェック（委託）
                  IF vRecordShohin.SHNCD IS NOT NULL 
                     AND vRecordShohin.SHNZOKUSEI = '1' -- 1:ＡＰ商品
                     THEN 
                     addError(lineno, fileprop_rec(columnno).COLNAME, '【W151】AP／ショップの商品が含まれています。');
                  END IF; 
               END IF; 
-- インボイス対応追加 start ABI
            END IF; 
            -- 商品コードが"099901"（消費税）または"099902"（軽減消費税）の場合
            IF jisseki_rec.SHNCD = '099901' OR jisseki_rec.SHNCD = '099902' THEN 
               -- 商品消費税金額チェック
               IF current_rec('SHOHITAXKIN') IS NULL THEN 
                  addError(lineno, fileprop_rec(columnno).COLNAME, '【W041】消費税金額を指定してください。');
               END IF; 
            END IF; 
-- インボイス対応追加 end ABI
         END IF; 
      END IF; 
      
      /* 商品と配布物入力チェック（配達実績データ）*/
      IF jisseki_rec.ACPTKBN IS NOT NULL THEN 
         IF ( current_rec('SHNCD') IS NULL AND current_rec('HAIFUBUTSUCD') IS NULL ) 
            OR ( current_rec('SHNCD') IS NOT NULL AND current_rec('HAIFUBUTSUCD') IS NOT NULL ) THEN 
               addError(lineno, fileprop_rec(columnno).COLNAME, '【W031】配達実績データの場合、商品と配布物に片方を入力して下さい。'); 
         END IF; 
      END IF; 
      
      /* 配布物ＡＰ基本コード */ 
      columnno := columnno + 1;
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('HAIFUBUTSUAPKIHONCD') ) THEN 
         jisseki_rec.HAIFUBUTSUAPKIHONCD := current_rec('HAIFUBUTSUAPKIHONCD'); 
         -- 入力有時チェック
         IF jisseki_rec.HAIFUBUTSUAPKIHONCD IS NOT NULL THEN 
            -- ＡＰ基本マスタチェック
            itemValid := checkApkihonMST(lineno, columnno, jisseki_rec.HAIFUBUTSUAPKIHONCD); 
         END IF; 
      END IF; 
      
      /* 配布物ショップコード */ 
      columnno := columnno + 1;
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('HAIFUBUTSUSHOPCD') ) THEN 
         jisseki_rec.HAIFUBUTSUSHOPCD := current_rec('HAIFUBUTSUSHOPCD'); 
         -- 入力有：ショップマスタチェック
         IF jisseki_rec.HAIFUBUTSUSHOPCD IS NOT NULL AND jisseki_rec.HAIFUBUTSUSHOPCD IS NOT NULL THEN 
            itemValid := checkShopMST(lineno, columnno, jisseki_rec.HAIFUBUTSUAPKIHONCD, jisseki_rec.HAIFUBUTSUSHOPCD); 
         END IF; 
      END IF; 
      
      /* 配布物コード */ 
      columnno := columnno + 1;
      vRecordHaifubutsu := NULL;
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('HAIFUBUTSUCD') ) THEN 
         jisseki_rec.HAIFUBUTSUCD := current_rec('HAIFUBUTSUCD'); 
         -- 入力有時チェック 
         IF jisseki_rec.HAIFUBUTSUCD IS NOT NULL THEN 
            -- 配布物マスタチェック
            IF jisseki_rec.HAIFUBUTSUAPKIHONCD IS NOT NULL AND jisseki_rec.HAIFUBUTSUSHOPCD IS NOT NULL THEN 
               vRecordHaifubutsu := checkHaifubutsuMST(lineno, columnno, jisseki_rec.HAIFUBUTSUAPKIHONCD, jisseki_rec.HAIFUBUTSUSHOPCD, jisseki_rec.HAIFUBUTSUCD); 
            END IF; 
            -- 配布物配布期間外チェック
            IF jisseki_rec.DLVRYJISSEKIDD IS NOT NULL AND vRecordHaifubutsu.HAIFUBUTSUCD IS NOT NULL THEN 
               IF jisseki_rec.DLVRYJISSEKIDD < vRecordHaifubutsu.HAIFUDKIKANSTRTDD 
                  OR jisseki_rec.DLVRYJISSEKIDD > vRecordHaifubutsu.HAIFUDKIKANSHRYDD THEN 
                  addError(lineno, fileprop_rec(columnno).COLNAME, '【W088】「'|| fileprop_rec(columnno).COLNAME ||'」は配布期間外です。'); 
               END IF;
            END IF; 
         END IF; 
      END IF; 
      
      /* 計上区分 */ 
      columnno := columnno + 1;
-- インボイス対応追加 start
      IF current_rec('SHNCD') NOT IN ('099901', '099902') THEN -- 消費税商品データ以外の場合、チェック
-- インボイス対応追加 end
         -- 項目基本属性チェック
         IF checkBasics(lineno, columnno, current_rec('KEIJOKBN') ) THEN 
            jisseki_rec.KEIJOKBN := current_rec('KEIJOKBN'); 
            -- 配達商品明細データ：必須チェック
            IF current_rec('ACPTKBN') IS NOT NULL 
               AND current_rec('SHNCD') IS NOT NULL 
               AND current_rec('KEIJOKBN') IS NULL 
               THEN 
               addError( lineno, fileprop_rec(columnno).COLNAME
                    , '【W041】'|| fileprop_rec(columnno).COLNAME ||'を指定して下さい。');
            END IF; 
            -- 入力有時チェック
            IF jisseki_rec.KEIJOKBN IS NOT NULL THEN 
               -- システム管理マスタ存在チェック
               itemValid := checkSystemMST(lineno, columnno, '000', '000', '009', jisseki_rec.KEIJOKBN, NULL, NULL );
               -- 得意先請求先チェック
               IF vRecordTokuisaki.TOKUISAKICD IS NOT NULL THEN 
                  IF vRecordTokuisaki.SKYSAKICD IS NULL AND jisseki_rec.KEIJOKBN IN ('01', '02', '08') THEN -- 売上／キャンペーン／経費の場合で、請求先コード未設定はエラー
                     addError(lineno, fileprop_rec(columnno).COLNAME, '【W041】計上区分が無効です。');
                  END IF; 
               END IF; 
               -- 計上区分相違チェック
               IF jisseki_rec.ACPTKBN IS NOT NULL 
                  AND jisseki_rec.SHNCD IS NOT NULL 
                  AND vRecordMeisai.ACPTDNPYMEISAINO IS NOT NULL 
                  THEN 
                  IF vRecordMeisai.KEIJOKBN <> jisseki_rec.KEIJOKBN THEN 
                     addError(lineno, fileprop_rec(columnno).COLNAME, '【W041】計上区分が無効です。');
                  END IF; 
               END IF; 
            END IF; 
         END IF; 
-- インボイス対応追加 start
      END IF; 
-- インボイス対応追加 end
      
      /* 納品数量 */
      columnno := columnno + 1;
-- インボイス対応追加 start
      IF current_rec('SHNCD') NOT IN ('099901', '099902') THEN -- 消費税商品データ以外の場合、チェック
-- インボイス対応追加 end
         -- 項目基本属性チェック
         IF checkBasics(lineno, columnno, current_rec('NOHINSURYO') ) THEN 
            jisseki_rec.NOHINSURYO := TO_NUMBER(current_rec('NOHINSURYO')); 
         END IF; 
         -- 配達商品明細データ：必須チェック
         IF current_rec('ACPTKBN') IS NOT NULL 
            AND current_rec('SHNCD') IS NOT NULL 
            AND current_rec('NOHINSURYO') IS NULL 
         THEN 
            addError( lineno, fileprop_rec(columnno).COLNAME
                    , '【W041】'|| fileprop_rec(columnno).COLNAME ||'を指定して下さい。');
         END IF; 
         -- 入力有時チェック
         IF jisseki_rec.NOHINSURYO IS NOT NULL THEN 
            -- 納品数量端数管理チェック
            IF vRecordShohin.SHNCD IS NOT NULL THEN 
               IF vRecordShohin.SURYOHASUKANRIKBN = '0' AND MOD(jisseki_rec.NOHINSURYO, 1) <> 0 THEN -- 端数管理しないで小数はエラー
                  addError(lineno, fileprop_rec(columnno).COLNAME, '【W067】納品数量は整数ではありません。');
               END IF; 
            END IF; 
         END IF; 
-- インボイス対応追加 start
      END IF; 
-- インボイス対応追加 end
      
      /* 納品単価 */ 
      columnno := columnno + 1;
-- インボイス対応追加 start
      IF current_rec('SHNCD') NOT IN ('099901', '099902') THEN -- 消費税商品データ以外の場合、チェック
-- インボイス対応追加 end
         -- 項目基本属性チェック
         IF checkBasics(lineno, columnno, current_rec('NOHINUNT') ) THEN 
            jisseki_rec.NOHINUNT := TO_NUMBER(current_rec('NOHINUNT')); 
         END IF; 
         -- 配達データ：必須チェック
         IF current_rec('ACPTKBN') IS NOT NULL 
            AND current_rec('SHNCD') IS NOT NULL 
            AND current_rec('NOHINUNT') IS NULL 
         THEN 
            addError( lineno, fileprop_rec(columnno).COLNAME
                    , '【W041】'|| fileprop_rec(columnno).COLNAME ||'を指定して下さい。');
         END IF; 
         -- 入力有時チェック
         IF jisseki_rec.NOHINUNT IS NOT NULL THEN 
            -- 納品単価無料計上区分チェック
            IF jisseki_rec.KEIJOKBN IS NOT NULL AND jisseki_rec.KEIJOKBN NOT IN ('01', '02', '08') 
               AND jisseki_rec.NOHINUNT IS NOT NULL AND jisseki_rec.NOHINUNT <> 0 THEN 
               addError(lineno, fileprop_rec(columnno).COLNAME, '【W050】計上区分が無料の時は納品単価の入力はできません。');
            END IF; 
            -- 標準単価チェック 
            IF vRecordShohin.SHNCD IS NOT NULL AND jisseki_rec.DLVRYKBN IS NOT NULL AND jisseki_rec.KEIJOKBN IS NOT NULL 
               AND jisseki_rec.DLVRYJISSEKIDD IS NOT NULL AND vRecordTokuisaki.TOKUISAKICD IS NOT NULL THEN 
               itemValid := checkHyojununt(lineno, columnno, jisseki_rec.NOHINUNT, jisseki_rec.DLVRYJISSEKIDD, jisseki_rec.DLVRYKBN, jisseki_rec.KEIJOKBN, vRecordTokuisaki, vRecordShohin);
            END IF; 
            -- 与信限度額チェック
            IF jisseki_rec.NOHINUNT > 0 AND jisseki_rec.NOHINSURYO IS NOT NULL 
               AND vRecordTokuisaki.TOKUISAKICD IS NOT NULL THEN 
               itemValid := checkYoshingendo(lineno, columnno, jisseki_rec.NOHINUNT, jisseki_rec.NOHINSURYO, vRecordTokuisaki);
            END IF; 
         END IF;
-- インボイス対応追加 start
      END IF; 
-- インボイス対応追加 end
      
      /* 入力明細フラグ */ 
      columnno := columnno + 1;
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('NYURYOKUMEISAIFLG') ) THEN 
         jisseki_rec.NYURYOKUMEISAIFLG := current_rec('NYURYOKUMEISAIFLG'); 
      END IF;
      -- “1:入力行”または未設定以外のチェック
      IF current_rec('NYURYOKUMEISAIFLG') IS NOT NULL 
         AND current_rec('NYURYOKUMEISAIFLG') <> '1' 
      THEN 
         addError( lineno, fileprop_rec(columnno).COLNAME
                 , '【W089】入力明細フラグは“1：入力行”以外は入力できません。');
      END IF;
      
      /* 単品明細フラグ */ 
      columnno := columnno + 1;
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('TANPINMEISAIFLG') ) THEN 
         jisseki_rec.TANPINMEISAIFLG := current_rec('TANPINMEISAIFLG'); 
      END IF;
      
      /* セット商品コード */ 
      columnno := columnno + 1;
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('STSHNCD') ) THEN 
         jisseki_rec.STSHNCD := current_rec('STSHNCD'); 
      END IF;
      
      /* 明細備考 */ 
      columnno := columnno + 1;
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('MEISAIBIKO') ) THEN 
         jisseki_rec.MEISAIBIKO := current_rec('MEISAIBIKO'); 
         IF jisseki_rec.MEISAIBIKO IS NOT NULL AND jisseki_rec.HAIFUBUTSUCD IS NOT NULL THEN 
            addError(lineno, fileprop_rec(columnno).COLNAME, '【W067】明細行が配布物の時は明細備考の入力はできません。');
         END IF; 
      END IF;
      
      /* 型式 */ 
      columnno := columnno + 1;
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('KATASIKI') ) THEN 
         jisseki_rec.KATASIKI := current_rec('KATASIKI'); 
         -- 入力有時チェック 
         IF jisseki_rec.KATASIKI IS NOT NULL THEN 
            -- 型式マスタ存在チェック
            IF jisseki_rec.APKIHONCD IS NOT NULL AND jisseki_rec.SHNCD IS NOT NULL THEN 
               itemValid := checkKatasikiMST(lineno, columnno, jisseki_rec.APKIHONCD, jisseki_rec.SHNCD, jisseki_rec.KATASIKI); 
            END IF; 
         END IF; 
      END IF;
      
      /* シリアル番号 */ 
      columnno := columnno + 1; 
      vRecordServer   := NULL; 
      vRecordWatersvr := NULL; 
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('SRLNO') ) THEN 
         jisseki_rec.SRLNO := current_rec('SRLNO'); 
      END IF;
      -- 得意先設置サーバー管理マスタ取得 
      IF jisseki_rec.SRLNO IS NOT NULL 
         AND jisseki_rec.APKIHONCD IS NOT NULL AND jisseki_rec.SHOPCD IS NOT NULL 
         AND jisseki_rec.TOKUISAKICD IS NOT NULL AND jisseki_rec.SHNCD IS NOT NULL THEN 
         vRecordServer := getStupServer( jisseki_rec.APKIHONCD, jisseki_rec.SHOPCD, jisseki_rec.TOKUISAKICD
                                       , jisseki_rec.SHNCD, jisseki_rec.SRLNO ); 
      END IF; 
      -- 入力有時チェック
      IF jisseki_rec.SRLNO IS NOT NULL THEN 
         -- ウォーターサーバーシリアル管理マスタ存在チェック
         vRecordWatersvr := checkSRLWatersvrMST(lineno, columnno, current_rec('STUPNAIYO'), jisseki_rec.SRLNO); 
         -- ウォーターサーバー商品コードチェック
         IF vRecordWatersvr.SHNCD IS NOT NULL AND jisseki_rec.SHNCD IS NOT NULL THEN 
            IF vRecordWatersvr.SHNCD <> jisseki_rec.SHNCD THEN 
               addError(lineno, fileprop_rec(columnno).COLNAME, '【W042】商品コードが異なるシリアル№は入力できません。'); 
            END IF; 
         END IF; 
         -- 管理元ＡＰ－ショップチェック
         IF vRecordWatersvr.KANRIAPKIHONCD IS NOT NULL AND vRecordWatersvr.KANRISHOPCD IS NOT NULL 
            AND jisseki_rec.APKIHONCD IS NOT NULL AND jisseki_rec.SHOPCD IS NOT NULL THEN 
            IF vRecordWatersvr.KANRIAPKIHONCD <> jisseki_rec.APKIHONCD 
               OR vRecordWatersvr.KANRISHOPCD <> jisseki_rec.SHOPCD THEN 
               addError(lineno, fileprop_rec(columnno).COLNAME, '【W042】管理元が異なるサーバーは入力できません。'); 
            END IF; 
         END IF; 
         -- 設置先チェック
         IF vRecordTokuisaki.TOKUISAKICD IS NOT NULL AND vRecordWatersvr.SRLNO IS NOT NULL THEN 
            itemValid := checkStupsaki( lineno, columnno 
                                      , vRecordTokuisaki -- 得意先
                                      , vRecordWatersvr  -- ウォーターサーバー
                                      , vRecordServer    -- 得意先設置サーバー
                                      , current_rec('STUPNAIYO') -- 設置内容 
                                      , current_rec('HIKIAGENAIYO') -- 引上内容 
                                      , jisseki_rec.DLVRYJISSEKIDD -- 設置／引上日
                                      , current_rec('KONKAIMNTDD') -- 今回メンテナンス日  
                                      , wUnyoYmd         -- 運用日付 
            ); 
         END IF; 
      END IF;
      
      /* カップホルダ有無 */ 
      columnno := columnno + 1;
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('CUPHOLDERUMU') ) THEN 
         jisseki_rec.CUPHOLDERUMU := current_rec('CUPHOLDERUMU'); 
      END IF;
      
      /* ロック付フォーセット */ 
      columnno := columnno + 1; 
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('LOCKTFOSET') ) THEN 
         jisseki_rec.LOCKTFOSET := current_rec('LOCKTFOSET'); 
      END IF;
       -- 入力有時チェック
      IF jisseki_rec.LOCKTFOSET IS NOT NULL THEN 
         -- システム管理マスタ存在チェック
         itemValid := checkSystemMST( lineno, columnno, '000', '000', '144', jisseki_rec.LOCKTFOSET, NULL, NULL ); 
      END IF; 
      
      /* フロントパネルコード */ 
      columnno := columnno + 1;
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('FRONTPANELCD') ) THEN 
         jisseki_rec.FRONTPANELCD := current_rec('FRONTPANELCD'); 
      END IF;
      -- 入力チェック
      IF current_rec('PANELNO') IS NOT NULL 
         AND current_rec('FRONTPANELCD') IS NULL 
         THEN 
         addError(lineno, fileprop_rec(columnno).COLNAME
                  , '【W082】パネルNoが入力された場合、「フロントパネルコード」は必須です。');
      END IF;
      -- 入力有時チェック
      IF jisseki_rec.FRONTPANELCD IS NOT NULL THEN 
         -- フロントパネルコード存在チェック
         IF jisseki_rec.APKIHONCD IS NOT NULL AND jisseki_rec.SHOPCD IS NOT NULL THEN 
            itemValid := checkFrontpanel(lineno, columnno, jisseki_rec.APKIHONCD, jisseki_rec.SHOPCD, jisseki_rec.FRONTPANELCD);
         END IF; 
      END IF; 
      
      /* パネルNo */ 
      columnno := columnno + 1;
      vRecordPanel := NULL; 
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('PANELNO') ) THEN 
         jisseki_rec.PANELNO := current_rec('PANELNO'); 
      END IF;
      -- 入力有時チェック
      IF jisseki_rec.PANELNO IS NOT NULL THEN 
         -- パネルNo存在チェック
         vRecordPanel := checkSRLPanelMST(lineno, columnno, jisseki_rec.PANELNO);
         -- パネル商品コードチェック
         IF vRecordPanel.SHNCD IS NOT NULL AND jisseki_rec.FRONTPANELCD IS NOT NULL THEN 
            IF vRecordPanel.SHNCD <> jisseki_rec.FRONTPANELCD THEN 
               addError(lineno, fileprop_rec(columnno).COLNAME, '【W042】商品コードが異なるフロントパネルシリアル№は入力できません。'); 
            END IF; 
         END IF; 
         -- 管理元ＡＰ－ショップチェック
         IF vRecordPanel.STUPSAKIAPKIHONCD IS NOT NULL AND vRecordPanel.STUPSAKISHOPCD IS NOT NULL 
            AND jisseki_rec.APKIHONCD IS NOT NULL AND jisseki_rec.SHOPCD IS NOT NULL THEN 
            IF vRecordPanel.STUPSAKIAPKIHONCD <> jisseki_rec.APKIHONCD 
               OR vRecordPanel.STUPSAKISHOPCD <> jisseki_rec.SHOPCD THEN 
               addError(lineno, fileprop_rec(columnno).COLNAME, '【W089】所在が異なるパネルは入力できません。'); 
            END IF; 
         END IF; 
         IF vRecordPanel.SZIKBN IS NOT NULL AND vRecordPanel.SZIKBN = '4' 
            AND jisseki_rec.APKIHONCD IS NOT NULL AND jisseki_rec.SHOPCD IS NOT NULL AND jisseki_rec.TOKUISAKICD IS NOT NULL THEN 
            IF vRecordPanel.STUPSAKITOKUISAKICD <> jisseki_rec.TOKUISAKICD THEN 
               addError(lineno, fileprop_rec(columnno).COLNAME, '【W024】入力されたパネルNoには別の得意先が設定されています。'); 
            END IF; 
         END IF; 
      END IF; 
      
      /* 設置内容 */ 
      columnno := columnno + 1;
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('STUPNAIYO') ) THEN 
         jisseki_rec.STUPNAIYO := current_rec('STUPNAIYO'); 
      END IF;
      -- 入力有時チェック
      IF jisseki_rec.STUPNAIYO IS NOT NULL THEN 
         -- システム管理マスタ存在チェック
         itemValid := checkSystemMST( lineno, columnno, '000', '000', '053', jisseki_rec.STUPNAIYO, NULL, NULL ); 
      END IF; 
      
      /* 設置場所区分 */ 
      columnno := columnno + 1;
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('STUPBASYOKBN') ) THEN 
         jisseki_rec.STUPBASYOKBN := current_rec('STUPBASYOKBN'); 
      END IF;
      -- 入力有時チェック
      IF jisseki_rec.STUPBASYOKBN IS NOT NULL THEN 
         -- システム管理マスタ存在チェック
         itemValid := checkSystemMST( lineno, columnno, '000', '000', '184', jisseki_rec.STUPBASYOKBN, NULL, NULL ); 
      END IF; 
      
      /* 設置場所備考 */ 
      columnno := columnno + 1;
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('STUPBASYOBIKO') ) THEN 
         jisseki_rec.STUPBASYOBIKO := current_rec('STUPBASYOBIKO'); 
      END IF;
      
      /* 引上内容 */ 
      columnno := columnno + 1;
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('HIKIAGENAIYO') ) THEN 
         jisseki_rec.HIKIAGENAIYO := current_rec('HIKIAGENAIYO'); 
      END IF;
      -- 入力有時チェック
      IF jisseki_rec.HIKIAGENAIYO IS NOT NULL THEN 
         -- システム管理マスタ存在チェック
         itemValid := checkSystemMST( lineno, columnno, '000', '000', '053', jisseki_rec.HIKIAGENAIYO, NULL, NULL ); 
      END IF; 
      
      /* サブステータス */ 
      columnno := columnno + 1;
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('SABUSTS') ) THEN 
         jisseki_rec.SABUSTS := current_rec('SABUSTS'); 
         -- 入力チェック
         IF current_rec('HIKIAGENAIYO') IS NOT NULL AND current_rec('SABUSTS') IS NULL THEN 
            addError(lineno, fileprop_rec(columnno).COLNAME, '【W030】サブステータスを指定して下さい。'); 
         END IF; 
      END IF;
      -- 入力有時チェック
      IF jisseki_rec.SABUSTS IS NOT NULL THEN 
         -- システム管理マスタ存在チェック
         itemValid := checkSystemMST( lineno, columnno, '000', '000', '112', jisseki_rec.SABUSTS, NULL, NULL ); 
         -- サブステータス有効チェック
         IF current_rec('HIKIAGENAIYO') IS NOT NULL AND jisseki_rec.KEIJOKBN IS NOT NULL 
            AND vRecordTokuisaki.TOKUISAKICD IS NOT NULL THEN 
            -- 計上区分が売上、移動ステータスが返品
            IF jisseki_rec.KEIJOKBN = '01' THEN 
               itemValid := checkSystemMST( lineno, columnno, '000', '000', '112', jisseki_rec.SABUSTS, '111', '02' );  
            -- 計上区分がサブレンタル、移動ステータスがｻﾌﾞﾚﾝﾀﾙ返品
            ELSIF jisseki_rec.KEIJOKBN = '09' THEN 
               itemValid := checkSystemMST( lineno, columnno, '000', '000', '112', jisseki_rec.SABUSTS, '111', '06' );  
            -- 計上区分がレンタル、得意先区分が顧客、顧客区分がショップ顧客、移動ステータスがｻﾌﾞﾚﾝﾀﾙ返品 
            ELSIF jisseki_rec.KEIJOKBN = '10' 
               AND vRecordTokuisaki.TOKUISAKIKBN = '1' AND vRecordTokuisaki.JUTAKUDLVRYCSTKBN = '2' THEN 
               itemValid := checkSystemMST( lineno, columnno, '000', '000', '112', jisseki_rec.SABUSTS, '111', '06' );  
            -- 計上区分がレンタル、得意先が上記以外、移動ステータスが引上 
            ELSIF jisseki_rec.KEIJOKBN = '10' 
               AND ( vRecordTokuisaki.TOKUISAKIKBN <> '1' OR vRecordTokuisaki.JUTAKUDLVRYCSTKBN <> '2' ) THEN 
               itemValid := checkSystemMST( lineno, columnno, '000', '000', '112', jisseki_rec.SABUSTS, '111', '08' );  
            END IF; 
         END IF; 
      END IF; 
      
      /* 明細備考（引上） */ 
      columnno := columnno + 1;
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('MEISAIBIKO_HIKIAGE') ) THEN 
         jisseki_rec.MEISAIBIKO_HIKIAGE := current_rec('MEISAIBIKO_HIKIAGE'); 
      END IF;
      
      /* 今回メンテナンス日 */ 
      columnno := columnno + 1;
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('KONKAIMNTDD') ) THEN 
         jisseki_rec.KONKAIMNTDD := current_rec('KONKAIMNTDD'); 
      END IF;
      -- 入力有時チェック
      IF jisseki_rec.KONKAIMNTDD IS NOT NULL THEN 
         -- 過去日チェック：運用日付より未来はエラー
         itemValid := checkPastFutureDate( lineno, columnno
                                         , jisseki_rec.KONKAIMNTDD, fileprop_rec(columnno).COLNAME
                                         , wUnyoYmd, '運用日付');
         -- 今回メンテナンス日妥当性チェック
         IF vRecordServer.KONKAIMNTDD IS NOT NULL THEN 
            -- 前回メンテナンス日と比較
            itemValid := checkPastFutureDate( lineno, columnno 
                                            , vRecordServer.KONKAIMNTDD, '前回メンテナンス日'
                                            , jisseki_rec.KONKAIMNTDD,  fileprop_rec(columnno).COLNAME );
         END IF; 
         IF vRecordServer.STUPDD IS NOT NULL THEN 
            -- 設置日と比較
            itemValid := checkPastFutureDate( lineno, columnno 
                                            , vRecordServer.STUPDD, '設置日'
                                            , jisseki_rec.KONKAIMNTDD,  fileprop_rec(columnno).COLNAME );
         END IF; 
         IF vRecordServer.STUPDD IS NULL AND jisseki_rec.DLVRYJISSEKIDD IS NOT NULL THEN 
            -- 配達実績日と比較
            itemValid := checkPastFutureDate( lineno, columnno 
                                            , jisseki_rec.DLVRYJISSEKIDD, '配達実績日'
                                            , jisseki_rec.KONKAIMNTDD,  fileprop_rec(columnno).COLNAME );
         END IF; 
      END IF; 
      
      /* 次回メンテナンス予定年月 */ 
      columnno := columnno + 1;
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('JIKAIMNTYTIDYDM') ) THEN 
         jisseki_rec.JIKAIMNTYTIDYDM := current_rec('JIKAIMNTYTIDYDM'); 
      END IF;
      -- 入力有時チェック
      IF jisseki_rec.JIKAIMNTYTIDYDM IS NOT NULL THEN 
         -- 今回メンテナンス日より過去はエラー
         IF jisseki_rec.KONKAIMNTDD IS NOT NULL THEN 
            itemValid := checkPastFutureDate( lineno, columnno
                                            , SUBSTRB(jisseki_rec.KONKAIMNTDD, 1, 6), '今回メンテナンス日' 
                                            , jisseki_rec.JIKAIMNTYTIDYDM, fileprop_rec(columnno).COLNAME ); 
         ELSE 
            addError(lineno, fileprop_rec(columnno).COLNAME, '【W011】「今回メンテナンス日」と「次回メンテナンス年月」の大小関係が正しくありません。');
         END IF; 
         -- 前回メンテナンス日と比較
         IF vRecordServer.KONKAIMNTDD IS NOT NULL THEN 
            itemValid := checkPastFutureDate( lineno, columnno 
                                            , SUBSTRB(vRecordServer.KONKAIMNTDD, 1, 6), '前回メンテナンス日'
                                            , jisseki_rec.JIKAIMNTYTIDYDM,  fileprop_rec(columnno).COLNAME );
         END IF; 
         -- 設置日と比較
         IF vRecordServer.STUPDD IS NOT NULL THEN 
            itemValid := checkPastFutureDate( lineno, columnno 
                                            , SUBSTRB(vRecordServer.STUPDD, 1, 6), '設置日'
                                            , jisseki_rec.JIKAIMNTYTIDYDM,  fileprop_rec(columnno).COLNAME );
         END IF; 
         -- 配達実績日と比較
         IF vRecordServer.STUPDD IS NULL AND jisseki_rec.DLVRYJISSEKIDD IS NOT NULL THEN 
            itemValid := checkPastFutureDate( lineno, columnno 
                                            , SUBSTRB(jisseki_rec.DLVRYJISSEKIDD, 1, 6), '配達実績日'
                                            , jisseki_rec.JIKAIMNTYTIDYDM,  fileprop_rec(columnno).COLNAME );
         END IF; 
      END IF; 
      
      /* メンテナンス回数 */ 
      columnno := columnno + 1;
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('MNTKAISU') ) THEN 
         jisseki_rec.MNTKAISU := current_rec('MNTKAISU'); 
      END IF;
      
      /* 納品完了メモ */ 
      columnno := columnno + 1;
      -- 項目基本属性チェック
      IF checkBasics(lineno, columnno, current_rec('NOHINKANRYOMEMO') ) THEN 
         jisseki_rec.NOHINKANRYOMEMO := current_rec('NOHINKANRYOMEMO'); 
      END IF;
      
-- インボイス対応追加 start ABI
      /* 消費税金額 */ 
      columnno := columnno + 1;
      -- 消費税金額チェック
      IF checkShohitaxkin(lineno, columnno, current_rec('SHOHITAXKIN') ) THEN 
         jisseki_rec.SHOHITAXKIN := current_rec('SHOHITAXKIN'); 
      END IF;
-- インボイス対応追加 end ABI
      
      -- チェック終了
    END LOOP;
    
    EXCEPTION
      WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20009, SQLERRM || 'チェック時エラーが発生しました。');
    
  END checkCSV;
        
  /****************************************************************************/
  /* プロシージャ名 ：処理件数取得                                                 */
  /* 引数（ＩＮ）    ：file_rec          ファイル内容                             */
  /*                ：iSessionid        セッションＩＤ                           */
  /* 引数（ＯＵＴ）  ：oPrcscnt          処理件数                                 */
  /*                ：oErrorcnt         エラー件数                              */
  /*                ：oSeijocnt         正常件数                                */
  /****************************************************************************/
  PROCEDURE getCnt( file_rec        IN file_rec_type
                  , iSessionid      IN VARCHAR2
                  , oPrcscnt    OUT VARCHAR2
                  , oErrorcnt   OUT VARCHAR2
                  , oSeijocnt   OUT VARCHAR2
  ) IS 
  BEGIN 
    -- 処理件数
    oPrcscnt := file_rec.LAST; 
    -- エラー件数
    SELECT NVL(COUNT(DISTINCT ERT.TORIKOMIGYOKAZU), 0)
      INTO oErrorcnt
      FROM S01T009T ERT
     WHERE ERT.APKIHONCD = error_rec.APKIHONCD 
       AND ERT.SHOPCD    = error_rec.SHOPCD 
       AND ERT.KANRINO   = error_rec.KANRINO 
       AND ERT.SESSIONID = error_rec.SESSIONID 
       AND ERT.PRCSJISSIDDTH = userinfo_rec.SYSTMYMD || userinfo_rec.SYSTMJIKAN 
       AND ERT.ERRKIND = '1' 
       AND ERT.YUKOKBN = '1' 
    ;
    -- 正常件数
    oSeijocnt := oPrcscnt - oErrorcnt; 
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR(-20011, SQLERRM || '処理件数取得時エラーが発生しました。');
        
  END getCnt;
  
  /****************************************************************************/
  /* プロシージャ名 ：モバイルシステム実績データチェック　                            */
  /* 引数           ：iTnshaapkihoncd    担当者ＡＰ基本コード                     */
  /*                ：iTnshashopcd       担当者ショップコード                    */
  /*                ：iTnshacd           担当者コード                           */
  /*                ：iIpadrse           ＩＰアドレス                           */
  /*                ：iFilename          ファイル名                             */
  /*                ：iSessionid         セッションＩＤ                          */
  /* 引数（ＯＵＴ）   ：oKanrino          管理番号                                */
  /*                ：oPrcscnt          処理件数                                */
  /*                ：oErrorcnt         エラー件数                              */
  /*                ：oSeijocnt         正常件数                                */
  /****************************************************************************/
  PROCEDURE MobileJissekiCheck( iTnshaapkihoncd IN VARCHAR2 -- 担当者ＡＰ基本コード
                              , iTnshashopcd    IN VARCHAR2 -- 担当者ショップコード
                              , iTnshacd        IN VARCHAR2 -- 担当者コード
                              , iIpadrse        IN VARCHAR2 -- ＩＰアドレス
                              , iFilename       IN VARCHAR2 -- ファイル名
                              , iSessionid      IN VARCHAR2 -- セッションＩＤ
                              , oKanrino       OUT VARCHAR2 -- 管理番号
                              , oPrcscnt       OUT NUMBER -- 処理件数
                              , oErrorcnt      OUT NUMBER -- エラー件数
                              , oSeijocnt      OUT NUMBER -- 正常件数                              
  ) IS 
  /* ファイル取込用変数 */
  file_path    VARCHAR2(150) := NULL; -- 取込ファイル格納先パス
  file_rec     file_rec_type := file_rec_type(); -- 取込対象ファイル
  BEGIN 
    -- 入力パラメータチェック
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
    IF iFilename IS NULL THEN 
      RAISE_APPLICATION_ERROR(-20005, 
                              'パラメータ．ファイル名　が設定されていません。');
    END IF; 
    IF iSessionid IS NULL THEN 
      RAISE_APPLICATION_ERROR(-20010, 
                              'パラメータ．セッションＩＤ　が設定されていません。');
    END IF; 
    
    -- 戻り値初期化
    oKanrino  := NULL;
    oPrcscnt  := 0; 
    oErrorcnt := 0;
    oSeijocnt := 0;
    
    -- 取込ファイルパスの取得
-- 本線対応変更 start ABI
    -- S3ではディレクトリオブジェクトを参照できないため、直フォルダパスを取得
    --file_path := getMojisetteiatai('000', '000', '230', '1', NULL, NULL, 'モバイル取込ファイル格納先');
    file_path := getMojisetteiatai('000', '000', '230', '2', NULL, NULL, 'モバイル取込ファイル格納先');
-- 本線対応変更 end ABI
    IF file_path IS NULL THEN 
      RAISE_APPLICATION_ERROR(-20007,
                              'モバイル取込ファイル格納先　が設定されていません。');
    END IF; 
    
    -- ファイル取込
    file_rec := readCSV( file_path, iFilename, ',' );
    
    -- 取込ファイル定義：モバイルシステム実績データ
    defineCSV; 
    
    -- テーブル共通基本情報設定
    setUserInfo( iTnshaapkihoncd, iTnshashopcd, iTnshacd, iIpadrse, 'F0802B01' );
    
    -- エラートラン用共通情報
    initErrorInfo( iTnshaapkihoncd, iTnshashopcd, iSessionid );
    
    -- 空ファイルチェック
    IF file_rec.LAST IS NULL OR file_rec.LAST = 0 THEN 
       addError('', '', '【W002】「有効行」が存在しません。');
       oPrcscnt  := 1; 
       oErrorcnt := 1;
    END IF; 
    
    -- ファイル内容チェック 
    IF file_rec.LAST > 0 THEN 
       S99_CHECK_LOG('F0802B01.checkCSV', iTnshaapkihoncd, iTnshashopcd, '1', 'S', '1', 'xxx', 0, iTnshaapkihoncd, iTnshashopcd, iTnshacd, iIpadrse, 'F0802B01' , 'ファイル内容チェック[開始] ' );
       checkCSV(file_rec, iTnshaapkihoncd, iTnshashopcd, iTnshacd); 
       S99_CHECK_LOG('F0802B01.checkCSV', iTnshaapkihoncd, iTnshashopcd, '1', 'E', '1', 'xxx', 0, iTnshaapkihoncd, iTnshashopcd, iTnshacd, iIpadrse, 'F0802B01' , 'ファイル内容チェック[終了] ' );
    END IF; 
    
    -- 戻り値：エラー管理番号 
    oKanrino := error_rec.KANRINO; 
    -- 戻り値：処理件数取得 
    IF file_rec.LAST > 0 THEN 
       S99_CHECK_LOG('F0802B01.prcscnt', iTnshaapkihoncd, iTnshashopcd, '1', 'S', '1', 'xxx', 0, iTnshaapkihoncd, iTnshashopcd, iTnshacd, iIpadrse, 'F0802B01' , '処理件数取得[開始] ' );
       getCnt(file_rec, iSessionid, oPrcscnt, oErrorcnt, oSeijocnt); 
       S99_CHECK_LOG('F0802B01.prcscnt', iTnshaapkihoncd, iTnshashopcd, '1', 'E', '1', 'xxx', 0, iTnshaapkihoncd, iTnshashopcd, iTnshacd, iIpadrse, 'F0802B01' , '処理件数取得[終了] ' );
    END IF; 
    
    EXCEPTION 
      WHEN OTHERS THEN 
        -- エラー
        RAISE_APPLICATION_ERROR(-20705, SQLERRM);
    
  END MobileJissekiCheck; 
  
END F0802B01;
/
exit;
