create or replace 
PACKAGE BODY C0808B01 
/*******************************************************************************
 * システム名       ：ＡＰ総合管理システム
 * 機能名           ：マイアクア連携受注取込処理
 *
 *******************************************************************************
   Date       Author         Changes Key                                 Version
   -----------------------------------------------------------------------------
   2022-05-16 ABI)Yokota        Create                                     1.0.0
 ******************************************************************************/
 AS 
  
  /****************************************************************************/
  /* 共通変数宣言                                                             */
  /****************************************************************************/
  /* 定数宣言 */
  c_PROGRAMID         CONSTANT VARCHAR2(8) := 'C0808B01';
  c_SYS_APKIHONCD     CONSTANT VARCHAR2(3) := '000';
  c_SYS_SHOPCD        CONSTANT VARCHAR2(3) := '000';
  c_INSERT            CONSTANT VARCHAR2(1) := '1';  -- 処理タイプ．登録
  c_YOYAKUINSERT      CONSTANT VARCHAR2(1) := '2';  -- 処理タイプ．登録（予約あり）
  c_UPDATE            CONSTANT VARCHAR2(1) := '3';  -- 処理タイプ．更新
  c_CANCEL            CONSTANT VARCHAR2(1) := '4';  -- 処理タイプ．キャンセル
  c_DELETE            CONSTANT VARCHAR2(1) := '5';  -- 処理タイプ．削除
  c_HENKOYOKYU_INS    CONSTANT NUMBER(1,0) := 1;    -- 状態変更要求．追加
  c_HENKOYOKYU_UPD    CONSTANT NUMBER(1,0) := 2;    -- 状態変更要求．更新
  c_HENKOYOKYU_DEL    CONSTANT NUMBER(1,0) := 3;    -- 状態変更要求．削除
  c_ERRKBN_ERR        CONSTANT VARCHAR2(1) := '1';  -- エラー区分．エラー
  c_ERRKBN_WAR        CONSTANT VARCHAR2(1) := '2';  -- エラー区分．ワーニング
  c_DLVRY_MOCHIKAERI  CONSTANT VARCHAR2(1) := '3';  -- 配達区分．持ち帰り
  c_JIKYOTEN          CONSTANT VARCHAR2(1) := '0';  -- 拠点ステータス．自拠点
  c_TAKYOTEN          CONSTANT VARCHAR2(1) := '1';  -- 拠点ステータス．他拠点
  c_TSK_KOKYAKU       CONSTANT VARCHAR2(1) := '1';  -- 得意先区分．顧客
  c_JDCK_NAKOKYAKU    CONSTANT VARCHAR2(1) := '3';  -- 受託配達顧客区分．NA顧客
  c_JDCK_ITAKUKOKYAKU CONSTANT VARCHAR2(1) := '4';  -- 受託配達顧客区分．委託顧客
  c_MEISAI_SHOHIN     CONSTANT VARCHAR2(1) := '0';  -- 明細区分．商品
  c_MEISAI_HAIFU      CONSTANT VARCHAR2(1) := '1';  -- 明細区分．配布物
  c_MEISAI_TAX        CONSTANT VARCHAR2(1) := '3';  -- 明細区分．消費税
  
  /* 変数宣言 */  
  gErrFlg         VARCHAR2(1) := '0';  -- エラーフラグ
  gUnyoDate       S01T004T.INPTUNYOYMD%TYPE;
  gShoriType      VARCHAR2(1); -- 処理タイプ
  gWaterCtgry     S02M006T.MOJISETTEIATAI%TYPE; --製品水の大分類コード
  gSystmYmd       VARCHAR2(8);
  gSystmJikan     VARCHAR2(8);
  gIpAdrse        VARCHAR2(15); --IPアドレス
  
  TYPE ErrListtype IS TABLE OF S03T019T%rowtype INDEX BY PLS_INTEGER;
  
  /* 伝票VO */
  TYPE denVOtype IS RECORD (
                         APKIHONCD                 VARCHAR2(3)   DEFAULT NULL
                       , SHOPCD                    VARCHAR2(3)   DEFAULT NULL
                       , ACPTDNPYNO                VARCHAR2(11)  DEFAULT NULL
                       , EDANO                     NUMBER(2,0)   DEFAULT 0
                       , ITAKUAITEAPKIHONCD        VARCHAR2(3)   DEFAULT NULL
                       , ITAKUAITESHOPCD           VARCHAR2(3)   DEFAULT NULL
                       , ITAKUAITEACPTDNPYNO       VARCHAR2(11)  DEFAULT NULL
                       , ITAKUAITEEDANO            NUMBER(2,0)   DEFAULT 0
                       , URIDNPYNO                 VARCHAR2(11)  DEFAULT NULL
                       , DLVRYTRGTFLG              VARCHAR2(1)   DEFAULT '1'
                       , DNPYKBN                   VARCHAR2(2)   DEFAULT '14'
                       , TORIHIKIKBN               VARCHAR2(1)   DEFAULT '1'
                       , ACPTKBN                   VARCHAR2(1)   DEFAULT '1'
                       , ACPTKBNSHS                VARCHAR2(2)   DEFAULT '00'
                       , TOKUISAKICD               VARCHAR2(6)   DEFAULT NULL
                       , ACPTDD                    VARCHAR2(8)   DEFAULT NULL
                       , ACPTTNSHAAPKIHONCD        VARCHAR2(3)   DEFAULT NULL
                       , ACPTTNSHASHOPCD           VARCHAR2(3)   DEFAULT NULL
                       , ACPTTNSHACD               VARCHAR2(6)   DEFAULT NULL
                       , DLVRYYTIDD                VARCHAR2(8)   DEFAULT NULL
                       , DLVRYJISSEKIDD            VARCHAR2(8)   DEFAULT NULL
                       , DLVRYDD                   VARCHAR2(8)   DEFAULT NULL
                       , DLVRYKIBOJIKAN            VARCHAR2(4)   DEFAULT NULL
                       , DLVRYKIBOJIKANSHRY        VARCHAR2(4)   DEFAULT NULL
                       , DLVRYKIBOJIKANKBN         VARCHAR2(1)   DEFAULT NULL
                       , FZAIOKIKANOFLG            VARCHAR2(1)   DEFAULT NULL
                       , FZAIOKIJISSEKIFLG         VARCHAR2(1)   DEFAULT '0'
                       , DLVRYTNSHACD              VARCHAR2(6)   DEFAULT NULL
                       , DLVRYKANKATSUAPKIHONCD    VARCHAR2(3)   DEFAULT NULL
                       , DLVRYKANKATSUSHOPCD       VARCHAR2(3)   DEFAULT NULL
                       , DLVRYKYOTENCD             VARCHAR2(2)   DEFAULT NULL
                       , DLVRYAREACD               VARCHAR2(5)   DEFAULT NULL
                       , DLVRYKBN                  VARCHAR2(1)   DEFAULT NULL
                       , DLVRYCMPNYCD              VARCHAR2(3)   DEFAULT NULL
                       , AZUKARIBTLN1N2RITTORUHNS  NUMBER(7,0)   DEFAULT 0
                       , KYBTLN1N2RITTORUHNS       NUMBER(4,0)   DEFAULT 0
                       , KZBTLN1N2RITTORUHNS       NUMBER(4,0)   DEFAULT 0
                       , AZUKARIBTLN8RITTORUHNS    NUMBER(7,0)   DEFAULT 0
                       , KYBTLN8RITTORUHNS         NUMBER(4,0)   DEFAULT 0
                       , KZBTLN8RITTORUHNS         NUMBER(4,0)   DEFAULT 0
                       , PRNTMEMON1                VARCHAR2(80)  DEFAULT NULL
                       , PRNTMEMON2                VARCHAR2(80)  DEFAULT NULL
                       , PRNTMEMON3                VARCHAR2(80)  DEFAULT NULL
                       , ACPTSHIMEPRCSDD           VARCHAR2(8)   DEFAULT '00000000'
                       , ACPTSHIMETNSHAAPKIHONCD   VARCHAR2(3)   DEFAULT NULL
                       , ACPTSHIMETNSHASHOPCD      VARCHAR2(3)   DEFAULT NULL
                       , ACPTSHIMEPRCSTNSHACD      VARCHAR2(6)   DEFAULT NULL
                       , DLVRYJISSEKIPRCSDD        VARCHAR2(8)   DEFAULT '00000000'
                       , DLVRYJISSEKIMONOAPKIHONCD VARCHAR2(3)   DEFAULT NULL
                       , DLVRYJISSEKIMONOSHOPCD    VARCHAR2(3)   DEFAULT NULL
                       , DLVRYJISSEKIPRCSTNSHACD   VARCHAR2(6)   DEFAULT NULL
                       , URISHIMEPRCSDD            VARCHAR2(8)   DEFAULT '00000000'
                       , GETSUJISHIMEDYDM          VARCHAR2(6)   DEFAULT NULL
                       , GETSUJISHIMEDD            VARCHAR2(2)   DEFAULT NULL
                       , GETSUJIPRCSJISSIDD        VARCHAR2(8)   DEFAULT '00000000'
                       , DLVRYJUN                  VARCHAR2(7)   DEFAULT NULL
                       , NOSHOPRNTTRGTFLG          VARCHAR2(1)   DEFAULT '1'
                       , NOSHOPRNTSUMIFLG          VARCHAR2(1)   DEFAULT '0'
                       , NOSHOPRNTDD               VARCHAR2(8)   DEFAULT NULL
                       , NOSHOHIKAEPRNTTRGTFLG     VARCHAR2(1)   DEFAULT '1'
                       , NOSHOHIKAEPRNTSUMIFLG     VARCHAR2(1)   DEFAULT '0'
                       , NOSHOHIKAEPRNTDD          VARCHAR2(8)   DEFAULT NULL
                       , NYKNHYOPRNTTRGTFLG        VARCHAR2(1)   DEFAULT NULL
                       , NYKNHYOPRNTSUMIFLG        VARCHAR2(1)   DEFAULT '0'
                       , NYKNHYOPRNTDD             VARCHAR2(8)   DEFAULT NULL
                       , WSVSIJISHOPRNTTRGTFLG     VARCHAR2(1)   DEFAULT '0'
                       , WSVSIJISHOPRNTSUMIFLG     VARCHAR2(1)   DEFAULT '0'
                       , WSVSIJISHOPRNTDD          VARCHAR2(8)   DEFAULT NULL
-- インボイス対応追加 start ABI
                       , SINGLDNPYFLG              VARCHAR2(1)   DEFAULT '0'
-- インボイス対応追加 end ABI
                       , DNPYSTS                   VARCHAR2(2)   DEFAULT '02'
                       , ACPTSTS                   VARCHAR2(2)   DEFAULT '02'
                       , DLVRYSTS                  VARCHAR2(2)   DEFAULT '00'
                       , DATSKSMOTOKBN             VARCHAR2(2)   DEFAULT '22'
                       , SKSMOTOKANRIKBN           VARCHAR2(1)   DEFAULT NULL
                       , SKSMOTOKANRINO            VARCHAR2(15)  DEFAULT NULL
                       , ACPTHIKIATESAKIAPKIHONCD  VARCHAR2(3)   DEFAULT NULL
                       , ACPTHIKIATESAKISHOPCD     VARCHAR2(3)   DEFAULT NULL
                       , ACPTHIKIATESAKIKYOTENCD   VARCHAR2(2)   DEFAULT NULL
                       , DLVRYYTIRGRAYUSYOHNS      NUMBER(4, 0)  DEFAULT 0
                       , DLVRYYTIRGRAMUSHOHNS      NUMBER(4, 0)  DEFAULT 0
                       , DLVRYYTISRMYUSYOHNS       NUMBER(4, 0)  DEFAULT 0
                       , DLVRYYTISRMMUSHOHNS       NUMBER(4, 0)  DEFAULT 0
                       , DLVRYYTISONOTAYUSYOHNS    NUMBER(4, 0)  DEFAULT 0
                       , DLVRYYTISONOTAMUSHOHNS    NUMBER(4, 0)  DEFAULT 0
                       , DLVRYYTISEIHINMIZUURIKIN  NUMBER(11, 0) DEFAULT 0
                       , DLVRYYTISONOTAURIKIN      NUMBER(11, 0) DEFAULT 0
                       , DLVRYJISSEKIRGRAYUSYOHNS  NUMBER(4, 0)  DEFAULT 0
                       , DLVRYJISSEKIRGRAMUSHOHNS  NUMBER(4, 0)  DEFAULT 0
                       , DLVRYJISSEKISRMYUSYOHNS   NUMBER(4, 0)  DEFAULT 0
                       , DLVRYJISSEKISRMMUSHOHNS   NUMBER(4, 0)  DEFAULT 0
                       , DLVRYJISSEKISTYSHNS       NUMBER(4, 0)  DEFAULT 0
                       , DLVRYJISSEKISTMSHNS       NUMBER(4, 0)  DEFAULT 0
                       , DLVRYJISSEKISHSUAKIN      NUMBER(8, 0)  DEFAULT 0
                       , DLVRYJISSEKISONOTAURIKIN  NUMBER(8, 0)  DEFAULT 0
                       , ACPTKIN                   NUMBER(11, 0) DEFAULT 0
                       , ACPTSHOHITAXKIN           NUMBER(11, 0) DEFAULT 0
-- インボイス対応追加 start ABI
                       , ACPTIPPANNUKIKIN          NUMBER(11, 0) DEFAULT 0
                       , ACPTIPPANTAXKIN           NUMBER(11, 0) DEFAULT 0
                       , ACPTKEIGENNUKIKIN         NUMBER(11, 0) DEFAULT 0
                       , ACPTKEIGENTAXKIN          NUMBER(11, 0) DEFAULT 0
                       , ACPTHIKAZEIURIIKIN        NUMBER(11, 0) DEFAULT 0
                       , ACPTFUKAZEIURIIKIN        NUMBER(11, 0) DEFAULT 0
-- インボイス対応追加 end ABI
                       , NOHINKIN                  NUMBER(11, 0) DEFAULT 0
                       , NOHINSHOHITAXKIN          NUMBER(11, 0) DEFAULT 0
-- インボイス対応追加 start ABI
                       , NOHINIPPANNUKIKIN         NUMBER(11, 0) DEFAULT 0
                       , NOHINIPPANTAXKIN          NUMBER(11, 0) DEFAULT 0
                       , NOHINKEIGENNUKIKIN        NUMBER(11, 0) DEFAULT 0
                       , NOHINKEIGENTAXKIN         NUMBER(11, 0) DEFAULT 0
                       , NOHINHIKAZEIURIIKIN       NUMBER(11, 0) DEFAULT 0
                       , NOHINFUKAZEIURIIKIN       NUMBER(11, 0) DEFAULT 0
-- インボイス対応追加 end ABI
                       , KURERURITTORUSTUP         NUMBER(3, 0)  DEFAULT 0
                       , KURERURITTORUHIKIAGE      NUMBER(3, 0)  DEFAULT 0
                       , KURERUESUSTUP             NUMBER(3, 0)  DEFAULT 0
                       , KURERUESUHIKIAGE          NUMBER(3, 0)  DEFAULT 0
                       , SONOTASTUP                NUMBER(3, 0)  DEFAULT 0
                       , SONOTAHIKIAGE             NUMBER(3, 0)  DEFAULT 0
                       , TAXKEISANKBN              VARCHAR2(1)   DEFAULT NULL
                       , ZNKDLVRYDD                VARCHAR2(8)   DEFAULT NULL
                       , ZNKDLVRYHNS               NUMBER(4, 0)  DEFAULT 0
                       , JIKAIDLVRYYTIDD           VARCHAR2(8)   DEFAULT NULL
                       , ZNKSHIMEDD                VARCHAR2(8)   DEFAULT NULL
                       , ZNKKURIKOSHIKIN           NUMBER(11, 0) DEFAULT NULL
                       , ACPTTHDAIBIKIFLG          VARCHAR2(1)   DEFAULT '0'
                       , ACPTRCPTKEIROKBN          VARCHAR2(2)   DEFAULT NULL
                       , HAIFUBUTSUAPKIHONCD1      VARCHAR2(3)   DEFAULT NULL
                       , HAIFUBUTSUSHOPCD1         VARCHAR2(3)   DEFAULT NULL
                       , HAIFUBUTSUCD1             VARCHAR2(5)   DEFAULT NULL
                       , HAIFUBUTSUAPKIHONCD2      VARCHAR2(3)   DEFAULT NULL
                       , HAIFUBUTSUSHOPCD2         VARCHAR2(3)   DEFAULT NULL
                       , HAIFUBUTSUCD2             VARCHAR2(5)   DEFAULT NULL
                       , HAIFUBUTSUAPKIHONCD3      VARCHAR2(3)   DEFAULT NULL
                       , HAIFUBUTSUSHOPCD3         VARCHAR2(3)   DEFAULT NULL
                       , HAIFUBUTSUCD3             VARCHAR2(5)   DEFAULT NULL
                       , HAIFUBUTSUAPKIHONCD4      VARCHAR2(3)   DEFAULT NULL
                       , HAIFUBUTSUSHOPCD4         VARCHAR2(3)   DEFAULT NULL
                       , HAIFUBUTSUCD4             VARCHAR2(5)   DEFAULT NULL
                       , KYOTENSTS                 VARCHAR2(1)   DEFAULT NULL
                       , TOKUISAKIKBN              VARCHAR2(1)   DEFAULT NULL
                       , JUTAKUDLVRYCSTKBN         VARCHAR2(1)   DEFAULT NULL
                       , TOKUISAKIJOTAI            VARCHAR2(1)   DEFAULT NULL
                       , JOTAISTS                  VARCHAR2(2)   DEFAULT NULL
                       , ZAIKOKANRIKBN             VARCHAR2(1)   DEFAULT NULL
                       , SKYSAKICD                 VARCHAR2(6)   DEFAULT NULL
                       , KAISHUHOHOKBN             VARCHAR2(1)   DEFAULT NULL
                       , ACPTTEISHIFLG             VARCHAR2(1)   DEFAULT NULL
                       , YOYAKUFLG                 VARCHAR2(1)   DEFAULT '0'
                       , ITAKUAITETOKUISAKICD      VARCHAR2(6)   DEFAULT NULL
                       , YOSHINGENDOKIN            NUMBER(11, 0) DEFAULT 0
                       , GENZAIZD                  NUMBER(8, 0)  DEFAULT 0
                       , KEIJOMAEACPTKIN           NUMBER(11, 0) DEFAULT 0
                       , SAISHUACPTDD              VARCHAR2(8)   DEFAULT NULL
                       , KYUMINKBN                 VARCHAR2(1)   DEFAULT NULL
                       
                       , YUKOKBN                   VARCHAR2(1)   DEFAULT NULL
  ); 
  
  /* 明細VO */
  TYPE meisaiVOtype IS RECORD (
                         APKIHONCD                 VARCHAR2(3)     DEFAULT NULL
                       , SHOPCD                    VARCHAR2(3)     DEFAULT NULL
                       , ACPTDNPYNO                VARCHAR2(11)    DEFAULT NULL
                       , EDANO                     NUMBER(2,0)     DEFAULT 0
                       , ACPTDNPYMEISAINO          NUMBER(5,0)     DEFAULT 0
                       , ACPTDNPYMEISAIEDANO       NUMBER(2,0)     DEFAULT 0
                       , ITAKUAITEAPKIHONCD        VARCHAR2(3)     DEFAULT NULL
                       , ITAKUAITESHOPCD           VARCHAR2(3)     DEFAULT NULL
                       , ITAKUAITEACPTDNPYNO       VARCHAR2(11)    DEFAULT NULL
                       , ITAKUAITEEDANO            NUMBER(2,0)     DEFAULT NULL
                       , ITAKUAITEACPTDNPYMEISAINO NUMBER(5,0)     DEFAULT NULL
                       , ITAKUAITEACPTDNPYMEISAIEDANO NUMBER(2,0)  DEFAULT NULL
                       , LCTGRYCD                  VARCHAR2(3)     DEFAULT NULL
                       , MCTGRYCD                  VARCHAR2(3)     DEFAULT NULL
                       , SCTGRYCD                  VARCHAR2(3)     DEFAULT NULL
                       , SHNCD                     VARCHAR2(6)     DEFAULT NULL
                       , HAIFUBUTSUAPKIHONCD       VARCHAR2(3)     DEFAULT NULL
                       , HAIFUBUTSUSHOPCD          VARCHAR2(3)     DEFAULT NULL
                       , HAIFUBUTSUCD              VARCHAR2(5)     DEFAULT NULL
                       , KATASHIKI                 VARCHAR2(15)    DEFAULT NULL
                       , KEIJOKBN                  VARCHAR2(2)     DEFAULT NULL
                       , ACPTSURYO                 NUMBER(9,1)     DEFAULT NULL
                       , HYOJUNUNT                 NUMBER(10,2)    DEFAULT NULL
                       , ACPTUNT                   NUMBER(10,2)    DEFAULT NULL
                       , ACPTKIN                   NUMBER(11,0)    DEFAULT NULL
                       , ACPTUNTSHOHITAXKIN        NUMBER(11,0)    DEFAULT NULL
                       , ACPTSHOHITAXKIN           NUMBER(11,0)    DEFAULT NULL
                       , ITAKUCMPNYACPTUNT         NUMBER(10,2)    DEFAULT NULL
                       , ITAKUCMPNYACPTUNTSHOHITAXKIN NUMBER(11,0) DEFAULT NULL
                       , ITAKUCMPNYACPTSHOHITAXKIN NUMBER(11,0)    DEFAULT NULL
                       , NOHINSURYO                NUMBER(9,1)     DEFAULT 0
                       , NOHINUNT                  NUMBER(10,2)    DEFAULT 0
                       , NOHINKIN                  NUMBER(11,0)    DEFAULT 0
                       , NOHINUNTSHOHITAXKIN       NUMBER(11,0)    DEFAULT 0
                       , NOHINSHOHITAXKIN          NUMBER(11,0)    DEFAULT 0
                       , SOUKAISU                  NUMBER(2,0)     DEFAULT NULL
                       , ZANKAISU                  NUMBER(2,0)     DEFAULT NULL
                       , TAXKBN                    VARCHAR2(1)     DEFAULT NULL
                       , YUSYOKBN                  VARCHAR2(1)     DEFAULT NULL
                       , MEISAIKBN                 VARCHAR2(1)     DEFAULT NULL
                       , NYURYOKUMEISAIFLG         VARCHAR2(1)     DEFAULT NULL
                       , TANPINMEISAIFLG           VARCHAR2(1)     DEFAULT NULL
                       , STSHNCD                   VARCHAR2(6)     DEFAULT NULL
                       , MEISAIBIKO                VARCHAR2(40)    DEFAULT NULL
                       , DATSKSMOTOKBN             VARCHAR2(2)     DEFAULT NULL
                       , URIDNPYNO                 VARCHAR2(11)    DEFAULT NULL
                       , URIDNPYEDANO              NUMBER(2,0)     DEFAULT NULL
                       , URIDNPYMEISAINO           NUMBER(5,0)     DEFAULT NULL
                       , URIDNPYMEISAIEDANO        NUMBER(2,0)     DEFAULT NULL
                       , SHNAPKIHONCD              VARCHAR2(3)     DEFAULT NULL
                       , PRICEKBN                  VARCHAR2(1)     DEFAULT NULL
                       , HYOJUNPRICEKIRIKAEDD      VARCHAR2(8)     DEFAULT NULL
                       , GENZHYOJUNPRICE           NUMBER(10,2)    DEFAULT 0
                       , SHINHYOJUNPRICE           NUMBER(10,2)    DEFAULT 0
                       , GENZHYOJUNMOCHIKAERIPRICE NUMBER(10,2)    DEFAULT 0
                       , SHINHYOJUNMOCHIKAERIPRICE NUMBER(10,2)    DEFAULT 0
                       , SHOPPRICEKIRIKAEDD        VARCHAR2(8)     DEFAULT NULL
                       , GENZSHOPPRICE             NUMBER(10,2)    DEFAULT 0
                       , SHINSHOPPRICE             NUMBER(10,2)    DEFAULT 0
                       , GENZSHOPMOCHIKAERIPRICE   NUMBER(10,2)    DEFAULT 0
                       , SHINSHOPMOCHIKAERIPRICE   NUMBER(10,2)    DEFAULT 0
                       , HANSOKUKBN                VARCHAR2(1)     DEFAULT NULL
                       , HANSOKUSTRTDD             VARCHAR2(8)     DEFAULT NULL
                       , HANSOKUSHRYDD             VARCHAR2(8)     DEFAULT NULL
                       , HANSOKUPRICE              NUMBER(10,2)    DEFAULT 0
                       , HANSOKUMOCHIKAERIPRICE    NUMBER(10,2)    DEFAULT 0
                       , HANSOKUYUSENKBN           VARCHAR2(1)     DEFAULT NULL
                       , ZAIKOUNT                  NUMBER(10,2)    DEFAULT 0
                       , URIIRISU                  NUMBER(5,0)     DEFAULT 0
                       , TAXSETTEIKBN              VARCHAR2(1)     DEFAULT NULL
                       , ZAIKOARAMUSURYO           NUMBER(9,1)     DEFAULT 0
                       , TTPRICESTRTDD             VARCHAR2(8)     DEFAULT NULL
                       , TTPRICESHRYDD             VARCHAR2(8)     DEFAULT NULL
                       , TTPRICE                   NUMBER(10,2)    DEFAULT 0
                       , TTMOCHIKAERIPRICE         NUMBER(10,2)    DEFAULT 0
                       , ZAIKOKANRITRGTFLG         VARCHAR2(1)     DEFAULT NULL
                       , SETTTPRICESTRTDD          VARCHAR2(8)     DEFAULT NULL
                       , SETTTPRICESHRYDD          VARCHAR2(8)     DEFAULT NULL
                       , SETTTPRICE                NUMBER(10,2)    DEFAULT 0
                       , SETTTMOCHIKAERIPRICE      NUMBER(10,2)    DEFAULT 0
  ); 
  
  TYPE meisaiListtype IS TABLE OF meisaiVOtype INDEX BY PLS_INTEGER;
  
  TYPE yoyakuMeisaiNoListtype IS TABLE OF NUMBER(5,0) INDEX BY PLS_INTEGER;
  
  TYPE regularListtype IS TABLE OF VARCHAR(6) INDEX BY PLS_INTEGER;
  TYPE slimListtype IS TABLE OF VARCHAR(6) INDEX BY PLS_INTEGER;
  
  TYPE datsksmotokbnListtype IS TABLE OF VARCHAR(2) INDEX BY PLS_INTEGER;
  
  TYPE zaikokeyVOtype IS RECORD (
                         MODELTYPE                 VARCHAR2(8)   DEFAULT NULL
                       , CHECKFG                   BOOLEAN
                       , MINUSFG                   BOOLEAN
                       , APKIHONCD                 VARCHAR2(3)   DEFAULT NULL
                       , SHOPCD                    VARCHAR2(3)   DEFAULT NULL
                       , DLVRYKYOTENCD             VARCHAR2(2)   DEFAULT NULL
                       , FOURTHKEY                 VARCHAR2(6)   DEFAULT NULL
                       , YMD                       VARCHAR2(8)   DEFAULT NULL
                       , SURYO                     NUMBER(9,1)   DEFAULT 0
                       , ARAMUSURYO                NUMBER(9,1)   DEFAULT 0
                       , CHECKRESULT               BOOLEAN       DEFAULT TRUE
  );
  TYPE zaikokeyListtype IS TABLE OF zaikokeyVOtype INDEX BY PLS_INTEGER;
  
  -- 伝票VOを宣言
  mainVO denVOtype;  -- メインの伝票VO
  initVO denVOtype;  -- 初期化用の伝票VO
  
  meisaiList meisaiListtype;  -- 明細リスト
  
  regularList regularListtype; -- レギュラー商品リスト
  slimList slimListtype; --スリム商品リスト
  
  datsksmotokbnList datsksmotokbnListtype;
  
  ErrList ErrListtype; -- エラーリスト
  vRecordS04W003T S04W003T%rowtype; -- マイアクア受注連携ワーク
  
  /****************************************************************************/
  /* プロシージャ名：エラー情報追加                                           */
  /* 引数           ：iErrKind           エラー種別                           */
  /*                ：iMessage           エラーメッセージ                     */
  /****************************************************************************/
  PROCEDURE ErrAdd( iErrKind     IN VARCHAR2
                  , iMessage     IN VARCHAR2
  ) IS 
  vRecordS03T019T S03T019T%rowtype;          --マイアクア連携エラーテーブル
  BEGIN
    
    vRecordS03T019T.ERRKIND         := iErrKind;
    vRecordS03T019T.ERRNAIYO        := iMessage;
    ErrList(ErrList.COUNT + 1) := vRecordS03T019T;

    --エラーフラグを設定
    IF iErrKind = c_ERRKBN_ERR THEN
      gErrFlg := '1';
    END IF;
    
  END ErrAdd;
  
  /****************************************************************************/
  /* プロシージャ名：エラー情報登録                                           */
  /****************************************************************************/
  PROCEDURE ErrIns
  IS 
  vRecordS03T019T S03T019T%rowtype;          --マイアクア連携エラーテーブル
  BEGIN
    FOR i IN 1..ErrList.COUNT LOOP
      vRecordS03T019T := ErrList(i);
      -- エラー情報を登録
      INSERT INTO S03T019T
      ( APKIHONCD
      , SHOPCD
      , TAIOUJISSIKBN
      , ERRHASSEIDD
      , ERRHASSEIJIKAN
      , ERRKIND
      , RENKEIJOHOKBN
      , PRCSKBN
      , ACPTDNPYNO
      , ACPTDNPYEDANO
      , ORDERID
      , RENBAN
      , TOKUISAKICD
      , SKYSAKICD
      , ERRNAIYO
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
      , KSHNPGMID )
      SELECT
        MJRW.APKIHONCD
       ,MJRW.SHOPCD
       ,'0'
       ,gSystmYmd
       ,gSystmJikan
       ,vRecordS03T019T.ERRKIND
       ,'01'
       ,MJRW.JOTAIHENKOYOKYU
       ,vRecordS04W003T.ACPTDNPYNO
       ,vRecordS04W003T.EDANO
       ,MJRW.ORDERID
       ,NVL((SELECT MAX(RENBAN)
           FROM S03T019T
          WHERE ORDERID = vRecordS04W003T.ORDERID
            AND RENKEIJOHOKBN = '01'), 0) + 1
       ,MJRW.TOKUISAKICD
       ,null
       ,vRecordS03T019T.ERRNAIYO
       ,'1'
       ,MJRW.ACPTTNSHAAPKIHONCD
       ,MJRW.ACPTTNSHASHOPCD
       ,MJRW.ACPTTNSHACD
       ,gSystmYmd
       ,gSystmJikan
       ,gUnyoDate
       ,gIpAdrse
       ,c_PROGRAMID
       ,MJRW.ACPTTNSHAAPKIHONCD
       ,MJRW.ACPTTNSHASHOPCD
       ,MJRW.ACPTTNSHACD
       ,gSystmYmd
       ,gSystmJikan
       ,gUnyoDate
       ,gIpAdrse
       ,c_PROGRAMID
      FROM S04W003T MJRW
      WHERE MJRW.ORDERID = vRecordS04W003T.ORDERID
      ;
    END LOOP;
    
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20101, SQLERRM || 'エラー情報登録時エラーが発生しました。');
  END ErrIns;

  /*****************************************************************************/
  /* ファンクション名：現新判定                                                */
  /* 引数             ：iOldValue           現項目                             */
  /*                  ：iNewValue           新項目                             */
  /*                  ：iSwitchDay          切替日                             */
  /*                  ：iTargetDay          対象日                             */
  /* return           ：returnValue                                            */
  /*****************************************************************************/
  FUNCTION getCurrentValue( iOldValue      IN NUMBER
                          , iNewValue      IN NUMBER
                          , iSwitchDay     IN VARCHAR2
                          , iTargetDay     IN VARCHAR2
  ) RETURN NUMBER IS 
  returnValue NUMBER(10,2);
  BEGIN 
    IF iSwitchDay IS NULL THEN
      returnValue := iNewValue;
    ELSIF iTargetDay IS NULL THEN
      returnValue := iOldValue;
    ELSE
      IF iTargetDay >= iSwitchDay THEN
        returnValue := iNewValue;
      ELSE
        returnValue := iOldValue;
      END IF;
    END IF;
    
    RETURN(returnValue);
  END getCurrentValue;
  /*****************************************************************************/
  /* ファンクション名：現新判定                                                */
  /* 引数             ：iOldValue           現項目                             */
  /*                  ：iNewValue           新項目                             */
  /*                  ：iSwitchDay          切替日                             */
  /*                  ：iTargetDay          対象日                             */
  /* return           ：returnValue                                            */
  /*****************************************************************************/
  FUNCTION getCurrentValue( iOldValue      IN VARCHAR2
                          , iNewValue      IN VARCHAR2
                          , iSwitchDay     IN VARCHAR2
                          , iTargetDay     IN VARCHAR2
  ) RETURN VARCHAR2 IS 
  returnValue VARCHAR2(2);
  BEGIN 
    IF iSwitchDay IS NULL THEN
      returnValue := iNewValue;
    ELSIF iTargetDay IS NULL THEN
      returnValue := iOldValue;
    ELSE
      IF iTargetDay >= iSwitchDay THEN
        returnValue := iNewValue;
      ELSE
        returnValue := iOldValue;
      END IF;
    END IF;
    
    RETURN(returnValue);
  END getCurrentValue;
  
  /*****************************************************************************/
  /* ファンクション名：レギュラー商品判定                                      */
  /* 引数             ：iShncd              判定商品コード                     */
  /* return           ：returnResult                                           */
  /*****************************************************************************/
  FUNCTION isLegular( iShncd         IN VARCHAR2
  ) RETURN BOOLEAN IS 
  returnResult BOOLEAN := false;
  BEGIN 
    IF regularList.COUNT = 0 THEN
      RETURN(returnResult);
    END IF;
    
    FOR i IN 1..regularList.COUNT LOOP
      IF regularList(i) = iShncd THEN
        returnResult := true;
      END IF;
    END LOOP;
    
    RETURN(returnResult);
  END isLegular;
  
  /*****************************************************************************/
  /* ファンクション名：スリム商品判定                                          */
  /* 引数             ：iShncd              判定商品コード                     */
  /* return           ：returnResult                                           */
  /*****************************************************************************/
  FUNCTION isSlim( iShncd         IN VARCHAR2
  ) RETURN BOOLEAN IS 
  returnResult BOOLEAN := false;
  BEGIN 
    IF slimList.COUNT = 0 THEN
      RETURN(returnResult);
    END IF;
    
    FOR i IN 1..slimList.COUNT LOOP
      IF slimList(i) = iShncd THEN
        returnResult := true;
      END IF;
    END LOOP;
    
    RETURN(returnResult);
  END isSlim;
  
  /*****************************************************************************/
  /* ファンクション名：データ作成元区分判定                                    */
  /* 引数             ：iKbn              判定区分コード                       */
  /* return           ：returnResult                                           */
  /*****************************************************************************/
  FUNCTION isDatsksmotokbn( iKbn         IN VARCHAR2
  ) RETURN BOOLEAN IS 
  returnResult BOOLEAN := false;
  BEGIN 
    IF datsksmotokbnList.COUNT = 0 THEN
      RETURN(returnResult);
    END IF;
    
    FOR i IN 1..datsksmotokbnList.COUNT LOOP
      IF datsksmotokbnList(i) = iKbn THEN
        returnResult := true;
      END IF;
    END LOOP;
    
    RETURN(returnResult);
  END isDatsksmotokbn;
  
  /****************************************************************************/
  /* プロシージャ名：基本チェック                                             */
  /****************************************************************************/
  PROCEDURE kihonChk
  IS 
  /* 変数宣言 */
  wYukoChk        NUMBER(3,0);   -- 有効性チェック用
  wJotaiSts       S02M013T.JOTAISTS%TYPE;
  wJotaiStsNm     S02M006T.MOJISETTEIATAI%TYPE;
  wTrtskikbn      S02V001T.TRTSKIKBN%TYPE;
  wServerL        S02M006T.MOJISETTEIATAI%TYPE;
  wServerM        S02M006T.MOJISETTEIATAI%TYPE;
  wStkbn          S02V001T.STKBN%TYPE;
  wTokuisakikbn   S02M013T.TOKUISAKIKBN%TYPE;
  wWater          S02M006T.MOJISETTEIATAI%TYPE;
  wSuryohasukbn   S02V001T.SURYOHASUKANRIKBN%TYPE;
  wKyotencd       S02M013T.DLVRYKANKATSUKYOTENCD%TYPE;
  wAreacd         S02M013T.DLVRYAREACD%TYPE;
  
  wShncd          S04W003T.SHNCDN1%TYPE;
  wKatashiki      S04W003T.KATASIKIN1%TYPE;
  wKeijokbn       S04W003T.KEIJOKBNN1%TYPE;
  wSuryo          S04W003T.ACPTSURYON1%TYPE;
  wUnt            S04W003T.ACPTUNTN1%TYPE;
  wMeisaibiko     S04W003T.MEISAIBIKON1%TYPE;
  wDatsksmotokbn  S04W003T.DATSKSMOTOKBNN1%TYPE;
  wUrikinhasukeisankbn      VARCHAR2(1)  := NULL;  -- 売上金額端数計算区分
  
  BEGIN
  
    --予約伝票変更チェック２
    IF vRecordS04W003T.YOYAKUJOHOCHGFLG = '1' THEN
      ErrAdd(c_ERRKBN_ERR, 'EJ004:受注予約明細は変更できません。');
    --予約伝票削除チェック
    ELSIF vRecordS04W003T.YOYAKUJOHOCHGFLG = '2' THEN
      ErrAdd(c_ERRKBN_ERR, 'EJ005:受注予約が紐づいた伝票は取消できません。');
    END IF;
    
    --AP基本コード有効性チェック
    SELECT COUNT(1)
      INTO wYukoChk
      FROM S02M001T
     WHERE APKIHONCD = vRecordS04W003T.APKIHONCD
       AND YUKOKBN   = '1'
    ;
    IF wYukoChk = 0 THEN
      ErrAdd(c_ERRKBN_ERR, 'EJ006:AP基本コードが無効です。');
    END IF;
    
    --ショップコード有効性チェック
    SELECT COUNT(1)
      INTO wYukoChk
      FROM S02M046T
     WHERE APKIHONCD = vRecordS04W003T.APKIHONCD
       AND SHOPCD    = vRecordS04W003T.SHOPCD
       AND YUKOKBN   = '1'
    ;
    IF wYukoChk = 0 THEN
      ErrAdd(c_ERRKBN_ERR, 'EJ007:ショップコードが無効です。');
    END IF;
    
    --得意先コード有効性チェック
    BEGIN
      SELECT TSM.JOTAISTS
           , SKM.MOJISETTEIATAI
           , TSM.TOKUISAKIKBN
           , TSM.DLVRYKANKATSUKYOTENCD
           , TSM.DLVRYAREACD
        INTO wJotaiSts
           , wJotaiStsNm
           , wTokuisakikbn
           , wKyotencd
           , wAreacd
        FROM S02M013T TSM
        LEFT JOIN S02M006T SKM
          ON SKM.APKIHONCD = c_SYS_APKIHONCD
         AND SKM.SHOPCD    = c_SYS_SHOPCD
         AND SKM.SETTEINO  = '015'
         AND SKM.RCRDKBN   = 'M'
         AND SKM.SETTEICD  = TSM.JOTAISTS
       WHERE TSM.APKIHONCD   = vRecordS04W003T.APKIHONCD
         AND TSM.SHOPCD      = vRecordS04W003T.SHOPCD
         AND TSM.TOKUISAKICD = vRecordS04W003T.TOKUISAKICD
         AND TSM.YUKOKBN     = '1'
      ;
      EXCEPTION 
        WHEN NO_DATA_FOUND THEN 
          ErrAdd(c_ERRKBN_ERR, 'EJ008:得意先コードが無効です。');
    END;
    
    --受注可能チェック
    IF wJotaiSts IS NOT NULL AND wJotaiSts IN ('02', '05', '06', '09', '10') THEN
      ErrAdd(c_ERRKBN_ERR, 'EJ009:' || wJotaiStsNm || 'の時は受注登録できません。');
    END IF;
    
    --受注担当者AP基本コード有効性チェック
    SELECT COUNT(1)
      INTO wYukoChk
      FROM S02M001T
     WHERE APKIHONCD = vRecordS04W003T.ACPTTNSHAAPKIHONCD
       AND YUKOKBN   = '1'
    ;
    IF wYukoChk = 0 THEN
      ErrAdd(c_ERRKBN_ERR, 'EJ010:受注担当者AP基本コードが無効です。');
    END IF;
    
    --受注担当者ショップコード有効性チェック
    SELECT COUNT(1)
      INTO wYukoChk
      FROM S02M046T
     WHERE APKIHONCD = vRecordS04W003T.ACPTTNSHAAPKIHONCD
       AND SHOPCD    = vRecordS04W003T.ACPTTNSHASHOPCD
       AND YUKOKBN   = '1'
    ;
    IF wYukoChk = 0 THEN
      ErrAdd(c_ERRKBN_ERR, 'EJ011:受注担当者ショップコードが無効です。');
    END IF;
    
    --受注担当者コード有効性チェック
    SELECT COUNT(1)
      INTO wYukoChk
      FROM S02M003T
     WHERE APKIHONCD = vRecordS04W003T.ACPTTNSHAAPKIHONCD
       AND SHOPCD    = vRecordS04W003T.ACPTTNSHASHOPCD
       AND TNSHACD   = vRecordS04W003T.ACPTTNSHACD
       AND YUKOKBN   = '1'
    ;
    IF wYukoChk = 0 THEN
      ErrAdd(c_ERRKBN_ERR, 'EJ012:受注担当者コードが無効です。');
    END IF;
    
    --不在置き可能フラグチェック
    IF vRecordS04W003T.FZAIOKIKANOFLG NOT IN ('0', '1') THEN
      ErrAdd(c_ERRKBN_ERR, 'EJ013:不在置き可能フラグが無効です。');
    END IF;
    
    -- ショップマスタ取得 
    SELECT
      SHPM.URIKINHASUKEISANKBN  -- 売上金額端数計算区分
    INTO wUrikinhasukeisankbn
    FROM
      S02M046T SHPM 
    WHERE
      SHPM.APKIHONCD = vRecordS04W003T.APKIHONCD
      AND SHPM.SHOPCD = vRecordS04W003T.SHOPCD  
      AND SHPM.YUKOKBN = '1'
    ;
    
    FOR i IN 1..12 LOOP
      --明細情報をセット
      CASE i 
        WHEN 1 THEN
          wShncd         := vRecordS04W003T.SHNCDN1;
          wKatashiki     := vRecordS04W003T.KATASIKIN1;
          wKeijokbn      := vRecordS04W003T.KEIJOKBNN1;
          wSuryo         := vRecordS04W003T.ACPTSURYON1;
          wUnt           := vRecordS04W003T.ACPTUNTN1;
          wMeisaibiko    := vRecordS04W003T.MEISAIBIKON1;
          wDatsksmotokbn := vRecordS04W003T.DATSKSMOTOKBNN1;
        WHEN 2 THEN
          wShncd         := vRecordS04W003T.SHNCDN2;
          wKatashiki     := vRecordS04W003T.KATASIKIN2;
          wKeijokbn      := vRecordS04W003T.KEIJOKBNN2;
          wSuryo         := vRecordS04W003T.ACPTSURYON2;
          wUnt           := vRecordS04W003T.ACPTUNTN2;
          wMeisaibiko    := vRecordS04W003T.MEISAIBIKON2;
          wDatsksmotokbn := vRecordS04W003T.DATSKSMOTOKBNN2;
        WHEN 3 THEN
          wShncd         := vRecordS04W003T.SHNCDN3;
          wKatashiki     := vRecordS04W003T.KATASIKIN3;
          wKeijokbn      := vRecordS04W003T.KEIJOKBNN3;
          wSuryo         := vRecordS04W003T.ACPTSURYON3;
          wUnt           := vRecordS04W003T.ACPTUNTN3;
          wMeisaibiko    := vRecordS04W003T.MEISAIBIKON3;
          wDatsksmotokbn := vRecordS04W003T.DATSKSMOTOKBNN3;
        WHEN 4 THEN
          wShncd         := vRecordS04W003T.SHNCDN4;
          wKatashiki     := vRecordS04W003T.KATASIKIN4;
          wKeijokbn      := vRecordS04W003T.KEIJOKBNN4;
          wSuryo         := vRecordS04W003T.ACPTSURYON4;
          wUnt           := vRecordS04W003T.ACPTUNTN4;
          wMeisaibiko    := vRecordS04W003T.MEISAIBIKON4;
          wDatsksmotokbn := vRecordS04W003T.DATSKSMOTOKBNN4;
        WHEN 5 THEN
          wShncd         := vRecordS04W003T.SHNCDN5;
          wKatashiki     := vRecordS04W003T.KATASIKIN5;
          wKeijokbn      := vRecordS04W003T.KEIJOKBNN5;
          wSuryo         := vRecordS04W003T.ACPTSURYON5;
          wUnt           := vRecordS04W003T.ACPTUNTN5;
          wMeisaibiko    := vRecordS04W003T.MEISAIBIKON5;
          wDatsksmotokbn := vRecordS04W003T.DATSKSMOTOKBNN5;
        WHEN 6 THEN
          wShncd         := vRecordS04W003T.SHNCDN6;
          wKatashiki     := vRecordS04W003T.KATASIKIN6;
          wKeijokbn      := vRecordS04W003T.KEIJOKBNN6;
          wSuryo         := vRecordS04W003T.ACPTSURYON6;
          wUnt           := vRecordS04W003T.ACPTUNTN6;
          wMeisaibiko    := vRecordS04W003T.MEISAIBIKON6;
          wDatsksmotokbn := vRecordS04W003T.DATSKSMOTOKBNN6;
        WHEN 7 THEN
          wShncd         := vRecordS04W003T.SHNCDN7;
          wKatashiki     := vRecordS04W003T.KATASIKIN7;
          wKeijokbn      := vRecordS04W003T.KEIJOKBNN7;
          wSuryo         := vRecordS04W003T.ACPTSURYON7;
          wUnt           := vRecordS04W003T.ACPTUNTN7;
          wMeisaibiko    := vRecordS04W003T.MEISAIBIKON7;
          wDatsksmotokbn := vRecordS04W003T.DATSKSMOTOKBNN7;
        WHEN 8 THEN
          wShncd         := vRecordS04W003T.SHNCDN8;
          wKatashiki     := vRecordS04W003T.KATASIKIN8;
          wKeijokbn      := vRecordS04W003T.KEIJOKBNN8;
          wSuryo         := vRecordS04W003T.ACPTSURYON8;
          wUnt           := vRecordS04W003T.ACPTUNTN8;
          wMeisaibiko    := vRecordS04W003T.MEISAIBIKON8;
          wDatsksmotokbn := vRecordS04W003T.DATSKSMOTOKBNN8;
        WHEN 9 THEN
          wShncd         := vRecordS04W003T.SHNCDN9;
          wKatashiki     := vRecordS04W003T.KATASIKIN9;
          wKeijokbn      := vRecordS04W003T.KEIJOKBNN9;
          wSuryo         := vRecordS04W003T.ACPTSURYON9;
          wUnt           := vRecordS04W003T.ACPTUNTN9;
          wMeisaibiko    := vRecordS04W003T.MEISAIBIKON9;
          wDatsksmotokbn := vRecordS04W003T.DATSKSMOTOKBNN9;
        WHEN 10 THEN
          wShncd         := vRecordS04W003T.SHNCDN10;
          wKatashiki     := vRecordS04W003T.KATASIKIN10;
          wKeijokbn      := vRecordS04W003T.KEIJOKBNN10;
          wSuryo         := vRecordS04W003T.ACPTSURYON10;
          wUnt           := vRecordS04W003T.ACPTUNTN10;
          wMeisaibiko    := vRecordS04W003T.MEISAIBIKON10;
          wDatsksmotokbn := vRecordS04W003T.DATSKSMOTOKBNN10;
        WHEN 11 THEN
          wShncd         := vRecordS04W003T.SHNCDN11;
          wKatashiki     := vRecordS04W003T.KATASIKIN11;
          wKeijokbn      := vRecordS04W003T.KEIJOKBNN11;
          wSuryo         := vRecordS04W003T.ACPTSURYON11;
          wUnt           := vRecordS04W003T.ACPTUNTN11;
          wMeisaibiko    := vRecordS04W003T.MEISAIBIKON11;
          wDatsksmotokbn := vRecordS04W003T.DATSKSMOTOKBNN11;
        WHEN 12 THEN
          wShncd         := vRecordS04W003T.SHNCDN12;
          wKatashiki     := vRecordS04W003T.KATASIKIN12;
          wKeijokbn      := vRecordS04W003T.KEIJOKBNN12;
          wSuryo         := vRecordS04W003T.ACPTSURYON12;
          wUnt           := vRecordS04W003T.ACPTUNTN12;
          wMeisaibiko    := vRecordS04W003T.MEISAIBIKON12;
          wDatsksmotokbn := vRecordS04W003T.DATSKSMOTOKBNN12;
        ELSE
          NULL;
      END CASE;
      
      --商品コード入力チェック
      IF wShncd IS NULL AND ( wKeijokbn IS NOT NULL AND wSuryo IS NOT NULL AND wUnt IS NOT NULL ) THEN
        ErrAdd(c_ERRKBN_ERR, 'EJ014:商品コードを指定して下さい。' || '[' || TO_CHAR(i) || '行目]');
      ELSIF wShncd IS NOT NULL THEN
              --商品コード有効性チェック
        BEGIN
          SELECT SHN.TRTSKIKBN
               , SYSM_L.MOJISETTEIATAI
               , SYSM_M.MOJISETTEIATAI
               , SHN.STKBN
               , SYSM_WATER.MOJISETTEIATAI
               , SHN.SURYOHASUKANRIKBN
            INTO wTrtskikbn
               , wServerL   --大分類がサーバーの場合に値が設定される
               , wServerM   --中分類がサーバーの場合に値が設定される
               , wStkbn
               , wWater     --大分類が製品水の場合に値が設定される
               , wSuryohasukbn
            FROM S02V001T SHN
            LEFT JOIN S02M006T SYSM_L 
              ON SYSM_L.APKIHONCD      = c_SYS_APKIHONCD 
             AND SYSM_L.SHOPCD         = c_SYS_SHOPCD
             AND SYSM_L.SETTEINO       = '096' 
             AND SYSM_L.RCRDKBN        = 'M'
             AND SYSM_L.SETTEICD       = '4' 
             AND SYSM_L.YUKOKBN        = '1' 
             AND SYSM_L.MOJISETTEIATAI = SHN.LCTGRYCD
            LEFT JOIN S02M006T SYSM_M 
              ON SYSM_M.APKIHONCD      = c_SYS_APKIHONCD 
             AND SYSM_M.SHOPCD         = c_SYS_SHOPCD
             AND SYSM_M.SETTEINO       = '134' 
             AND SYSM_M.RCRDKBN        = 'M'
             AND SYSM_M.SETTEICD       = '41' 
             AND SYSM_M.YUKOKBN        = '1' 
             AND SYSM_M.MOJISETTEIATAI = SHN.MCTGRYCD
            LEFT JOIN S02M006T SYSM_WATER 
              ON SYSM_WATER.APKIHONCD      = c_SYS_APKIHONCD 
             AND SYSM_WATER.SHOPCD         = c_SYS_SHOPCD
             AND SYSM_WATER.SETTEINO       = '096' 
             AND SYSM_WATER.RCRDKBN        = 'M'
             AND SYSM_WATER.SETTEICD       = '1' 
             AND SYSM_WATER.YUKOKBN        = '1' 
             AND SYSM_WATER.MOJISETTEIATAI = SHN.LCTGRYCD
           WHERE SHN.APKIHONCD   = vRecordS04W003T.APKIHONCD
             AND SHN.SHOPCD      = vRecordS04W003T.SHOPCD
             AND SHN.SHNCD       = wShncd
             AND vRecordS04W003T.ACPTDD BETWEEN SHN.HANBAISTRTDD AND SHN.HANBAISHRYDD
             AND SHN.SHOPYUKOKBN = '1'
          ;
          EXCEPTION 
            WHEN NO_DATA_FOUND THEN 
              ErrAdd(c_ERRKBN_ERR, 'EJ015:商品コードが無効です。' || '[' || TO_CHAR(i) || '明細目]');
        END;
        
        --商品コード取扱区分チェック
        IF wTrtskikbn = '2' THEN
          ErrAdd(c_ERRKBN_ERR, 'EJ016:仕入のみの商品は入力できません。' || '[' || TO_CHAR(i) || '明細目]');
        END IF;
        
        --商品コードサーバーチェック
        IF wServerL IS NOT NULL AND wServerM IS NOT NULL THEN
          ErrAdd(c_ERRKBN_ERR, 'EJ017:サーバーは入力できません。' || '[' || TO_CHAR(i) || '明細目]');
        END IF;
        
        --商品コードセット品チェック2
        IF wKeijokbn = '02' AND wStkbn = '1' THEN
          ErrAdd(c_ERRKBN_ERR, 'EJ018:計上区分がキャンペーンの場合はセット商品は入力できません。' || '[' || TO_CHAR(i) || '明細目]');
        END IF;
        
        --計上区分入力チェック
        IF wShncd IS NOT NULL AND wKeijokbn IS NULL THEN
          ErrAdd(c_ERRKBN_ERR, 'EJ019:計上区分を指定して下さい。' || '[' || TO_CHAR(i) || '明細目]');
        END IF;
        
        --計上区分有効性チェック
        SELECT COUNT(1)
          INTO wYukoChk
          FROM S02M006T
         WHERE APKIHONCD = c_SYS_APKIHONCD
           AND SHOPCD    = c_SYS_SHOPCD
           AND SETTEINO  = '009'
           AND RCRDKBN   = 'M'
           AND SETTEICD  = wKeijokbn
           AND YUKOKBN   = '1'
        ;
        IF wYukoChk = 0 THEN
          ErrAdd(c_ERRKBN_ERR, 'EJ020:計上区分が無効です。' || '[' || TO_CHAR(i) || '明細目]');
        END IF;
        
        --計上区分組み合わせチェック
        IF wTokuisakikbn IN ('1', '5', '6') AND wWater IS NOT NULL AND wKeijokbn IN ('01', '02', '03', '04', '05')
         OR wTokuisakikbn IN ('1', '5', '6') AND wWater IS NULL AND wKeijokbn IN ('01', '02', '03', '04', '10') THEN
          --正しい組み合わせ
          NULL;
        ELSE
          ErrAdd(c_ERRKBN_ERR, 'EJ021:計上区分が無効です。' || '[' || TO_CHAR(i) || '明細目]');
        END IF;
        
        --受注数量入力チェック
        IF wShncd IS NOT NULL AND wSuryo IS NULL THEN
          ErrAdd(c_ERRKBN_ERR, 'EJ022:受注数量を指定して下さい。' || '[' || TO_CHAR(i) || '明細目]');
        END IF;
        
        --受注数量小数点チェック
        IF wSuryohasukbn = '0' AND MOD(wSuryo, 1) > 0 THEN
          ErrAdd(c_ERRKBN_ERR, 'EJ023:数量は整数を設定してください。' || '[' || TO_CHAR(i) || '明細目]');
        END IF;
        
        --受注単価入力チェック
        IF wShncd IS NOT NULL AND wUnt IS NULL THEN
          ErrAdd(c_ERRKBN_ERR, 'EJ024:受注単価を指定して下さい。' || '[' || TO_CHAR(i) || '明細目]');
        END IF;
        
        --受注金額チェック
-- 本線対応変更 start ABI
--        IF LENGTH(S01SyohizeiService.roundValue(wSuryo * wUnt, wUrikinhasukeisankbn)) > 11 THEN
        IF LENGTH(Z1808B01.roundValue(wSuryo * wUnt, wUrikinhasukeisankbn)) > 11 THEN
-- 本線対応変更 end ABI
          ErrAdd(c_ERRKBN_ERR, 'EJ025:受注金額が超過しています。' || '[' || TO_CHAR(i) || '明細目]');
        END IF;
        
        --データ作成元区分チェック
        IF isDatsksmotokbn(wDatsksmotokbn) = false THEN
          ErrAdd(c_ERRKBN_ERR, 'EJ043:データ作成元区分が無効です。' || '[' || TO_CHAR(i) || '明細目]');
        END IF;
      END IF;
      
    END LOOP;
    
    --配達拠点設定済みチェック
    IF wKyotencd IS NULL OR wAreacd IS NULL THEN
      ErrAdd(c_ERRKBN_ERR, 'EJ026:得意先マスタに配達拠点コードが設定されていません');
    END IF;
    
    --未対応エラー存在チェック
    IF vRecordS04W003T.JOTAIHENKOYOKYU IN (c_HENKOYOKYU_UPD, c_HENKOYOKYU_DEL) THEN
      SELECT COUNT(1)
        INTO wYukoChk
        FROM S03T019T
       WHERE APKIHONCD     = vRecordS04W003T.APKIHONCD
         AND SHOPCD        = vRecordS04W003T.SHOPCD
         AND RENKEIJOHOKBN = '01'
         AND ACPTDNPYNO    = vRecordS04W003T.ACPTDNPYNO
         AND ACPTDNPYEDANO = vRecordS04W003T.EDANO
         AND TAIOUJISSIKBN = '0'
         AND ERRKIND       = '1'
         AND YUKOKBN       = '1'
      ;
      IF wYukoChk > 0 THEN
        ErrAdd(c_ERRKBN_WAR, 'WJ001:同じ伝票番号で未対応のエラーが存在します。');
      END IF;
    END IF;
    
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20102, SQLERRM || '基本チェック時エラーが発生しました。');
  END kihonChk;
  
  /****************************************************************************/
  /* プロシージャ名：予約情報取得                                             */
  /****************************************************************************/
  PROCEDURE getYoyaku
  IS 
  BEGIN
    BEGIN
      SELECT
          JHTT.ACPTDNPYNO
        , JHTT.EDANO
        , JHTT.PRNTMEMON1
        , JHTT.PRNTMEMON2
        , JHTT.PRNTMEMON3
      INTO mainVO.ACPTDNPYNO
         , mainVO.EDANO
         , mainVO.PRNTMEMON1
         , mainVO.PRNTMEMON2
         , mainVO.PRNTMEMON3
      FROM
        S04T001T JHTT 
      WHERE
            JHTT.APKIHONCD   = vRecordS04W003T.APKIHONCD
        AND JHTT.SHOPCD      = vRecordS04W003T.SHOPCD
        AND JHTT.YUKOKBN     = '1' 
        AND JHTT.TOKUISAKICD = vRecordS04W003T.TOKUISAKICD
        AND JHTT.DNPYSTS     = '01' 
        AND ROWNUM <= 1
      ORDER BY
          JHTT.ACPTDNPYNO DESC
        , JHTT.EDANO DESC
      ;
        
      mainVO.YOYAKUFLG := '1';
      
    EXCEPTION 
      WHEN NO_DATA_FOUND THEN 
        --予約情報なし
        mainVO.YOYAKUFLG := '0';
        --DBMS_OUTPUT.PUT_LINE('予約情報無');
        NULL;
    END;

    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20103, SQLERRM || '予約情報取得時エラーが発生しました。');
  END getYoyaku;
  
  /****************************************************************************/
  /* プロシージャ名：受注伝票取得                                             */
  /* 引数           ：iApkihoncd            AP基本コード                      */
  /*                ：iShopcd               ショップコード                    */
  /*                ：iAcptdnpyno           受注伝票番号                      */
  /*                ：iEdano                枝番                              */
  /*                ：oDenVO                伝票VO                            */
  /****************************************************************************/
  PROCEDURE loadJuchuDenpyo( iApkihoncd     IN VARCHAR2
                           , iShopcd        IN VARCHAR2
                           , iAcptdnpyno    IN VARCHAR2
                           , iEdano         IN NUMBER
                           , oDenVO         OUT denVOtype
  ) IS 
  BEGIN
    -- 伝票を取得
    SELECT
        JHTT.APKIHONCD
      , JHTT.SHOPCD
      , JHTT.ACPTDNPYNO
      , JHTT.EDANO
      , JHTT.DLVRYTRGTFLG
      , JHTT.DNPYKBN
      , JHTT.TORIHIKIKBN
      , JHTT.ACPTKBN
      , JHTT.ACPTKBNSHS
      , JHTT.TOKUISAKICD
      , JHTT.ACPTDD
      , JHTT.ACPTTNSHAAPKIHONCD
      , JHTT.ACPTTNSHASHOPCD
      , JHTT.ACPTTNSHACD
      , JHTT.DLVRYYTIDD
      , JHTT.DLVRYJISSEKIDD
      , JHTT.DLVRYDD
      , JHTT.DLVRYKIBOJIKAN
      , JHTT.DLVRYKIBOJIKANKBN
      , JHTT.DLVRYKIBOJIKANSHRY
      , JHTT.FZAIOKIKANOFLG
      , JHTT.FZAIOKIJISSEKIFLG
      , JHTT.DLVRYTNSHACD
      , JHTT.DLVRYKANKATSUAPKIHONCD
      , JHTT.DLVRYKANKATSUSHOPCD
      , JHTT.DLVRYKYOTENCD
      , JHTT.DLVRYAREACD
      , JHTT.DLVRYKBN
      , JHTT.DLVRYCMPNYCD
      , JHTT.AZUKARIBTLN1N2RITTORUHNS
      , JHTT.KYBTLN1N2RITTORUHNS
      , JHTT.KZBTLN1N2RITTORUHNS
      , JHTT.AZUKARIBTLN8RITTORUHNS
      , JHTT.KYBTLN8RITTORUHNS
      , JHTT.KZBTLN8RITTORUHNS
      , JHTT.PRNTMEMON1
      , JHTT.PRNTMEMON2
      , JHTT.PRNTMEMON3
      , JHTT.ACPTSHIMEPRCSDD
      , JHTT.ACPTSHIMETNSHAAPKIHONCD
      , JHTT.ACPTSHIMETNSHASHOPCD
      , JHTT.ACPTSHIMEPRCSTNSHACD
      , JHTT.DLVRYJISSEKIPRCSDD
      , JHTT.DLVRYJISSEKIMONOAPKIHONCD
      , JHTT.DLVRYJISSEKIMONOSHOPCD
      , JHTT.DLVRYJISSEKIPRCSTNSHACD
      , JHTT.URISHIMEPRCSDD
      , JHTT.GETSUJISHIMEDYDM
      , JHTT.GETSUJISHIMEDD
      , JHTT.GETSUJIPRCSJISSIDD
      , JHTT.DLVRYJUN
      , JHTT.NOSHOPRNTTRGTFLG
      , JHTT.NOSHOPRNTSUMIFLG
      , JHTT.NOSHOPRNTDD
      , JHTT.NOSHOHIKAEPRNTTRGTFLG
      , JHTT.NOSHOHIKAEPRNTSUMIFLG
      , JHTT.NOSHOHIKAEPRNTDD
      , JHTT.NYKNHYOPRNTTRGTFLG
      , JHTT.NYKNHYOPRNTSUMIFLG
      , JHTT.NYKNHYOPRNTDD
      , JHTT.WSVSIJISHOPRNTTRGTFLG
      , JHTT.WSVSIJISHOPRNTSUMIFLG
      , JHTT.WSVSIJISHOPRNTDD
-- インボイス対応追加 start ABI
      , JHTT.SINGLDNPYFLG
-- インボイス対応追加 end ABI
      , JHTT.DNPYSTS
      , JHTT.ACPTSTS
      , JHTT.DLVRYSTS
      , JHTT.DATSKSMOTOKBN
      , JHTT.SKSMOTOKANRIKBN
      , JHTT.SKSMOTOKANRINO
      , JHTT.ACPTHIKIATESAKIAPKIHONCD
      , JHTT.ACPTHIKIATESAKISHOPCD
      , JHTT.ACPTHIKIATESAKIKYOTENCD
      , JHTT.DLVRYYTIRGRAYUSYOHNS
      , JHTT.DLVRYYTIRGRAMUSHOHNS
      , JHTT.DLVRYYTISRMYUSYOHNS
      , JHTT.DLVRYYTISRMMUSHOHNS
      , JHTT.DLVRYYTISONOTAYUSYOHNS
      , JHTT.DLVRYYTISONOTAMUSHOHNS
      , JHTT.DLVRYYTISEIHINMIZUURIKIN
      , JHTT.DLVRYYTISONOTAURIKIN
      , JHTT.DLVRYJISSEKIRGRAYUSYOHNS
      , JHTT.DLVRYJISSEKIRGRAMUSHOHNS
      , JHTT.DLVRYJISSEKISRMYUSYOHNS
      , JHTT.DLVRYJISSEKISRMMUSHOHNS
      , JHTT.DLVRYJISSEKISTYSHNS
      , JHTT.DLVRYJISSEKISTMSHNS
      , JHTT.DLVRYJISSEKISHSUAKIN
      , JHTT.DLVRYJISSEKISONOTAURIKIN
      , JHTT.ACPTKIN
      , JHTT.ACPTSHOHITAXKIN
-- インボイス対応追加 start ABI
      , JHTT.ACPTIPPANNUKIKIN
      , JHTT.ACPTIPPANTAXKIN
      , JHTT.ACPTKEIGENNUKIKIN
      , JHTT.ACPTKEIGENTAXKIN
      , JHTT.ACPTHIKAZEIURIIKIN
      , JHTT.ACPTFUKAZEIURIIKIN
-- インボイス対応追加 end ABI
      , JHTT.NOHINKIN
      , JHTT.NOHINSHOHITAXKIN
-- インボイス対応追加 start ABI
      , JHTT.NOHINIPPANNUKIKIN
      , JHTT.NOHINIPPANTAXKIN
      , JHTT.NOHINKEIGENNUKIKIN
      , JHTT.NOHINKEIGENTAXKIN
      , JHTT.NOHINHIKAZEIURIIKIN
      , JHTT.NOHINFUKAZEIURIIKIN
-- インボイス対応追加 end ABI
      , JHTT.KURERURITTORUSTUP
      , JHTT.KURERURITTORUHIKIAGE
      , JHTT.KURERUESUSTUP
      , JHTT.KURERUESUHIKIAGE
      , JHTT.SONOTASTUP
      , JHTT.SONOTAHIKIAGE
      , JHTT.TAXKEISANKBN
      , JHTT.ZNKDLVRYDD
      , JHTT.ZNKDLVRYHNS
      , JHTT.JIKAIDLVRYYTIDD
      , JHTT.ZNKSHIMEDD
      , JHTT.ZNKKURIKOSHIKIN
      , JHTT.ACPTTHDAIBIKIFLG
      , JHTT.ACPTRCPTKEIROKBN
      , SKSM.KAISHUHOHOKBN
      , JHTT.ACPTTHDAIBIKIFLG
      , JHTT.ITAKUAITEAPKIHONCD
      , JHTT.ITAKUAITESHOPCD
      , JHTT.ITAKUAITEACPTDNPYNO
      , JHTT.ITAKUAITEEDANO
      , JHTT.URIDNPYNO
      , SKSM.SKYSAKICD
      , TKSM.JOTAISTS
      , TKSM.ACPTTEISHIFLG
      , TKSM.TOKUISAKIKBN
      , TKSM.ITAKUAITETOKUISAKICD
      , SPM.ZAIKOKANRIKBN
      , JHTT.YUKOKBN
    INTO oDenVO.APKIHONCD
       , oDenVO.SHOPCD
       , oDenVO.ACPTDNPYNO
       , oDenVO.EDANO
       , oDenVO.DLVRYTRGTFLG
       , oDenVO.DNPYKBN
       , oDenVO.TORIHIKIKBN
       , oDenVO.ACPTKBN
       , oDenVO.ACPTKBNSHS
       , oDenVO.TOKUISAKICD
       , oDenVO.ACPTDD
       , oDenVO.ACPTTNSHAAPKIHONCD
       , oDenVO.ACPTTNSHASHOPCD
       , oDenVO.ACPTTNSHACD
       , oDenVO.DLVRYYTIDD
       , oDenVO.DLVRYJISSEKIDD
       , oDenVO.DLVRYDD
       , oDenVO.DLVRYKIBOJIKAN
       , oDenVO.DLVRYKIBOJIKANKBN
       , oDenVO.DLVRYKIBOJIKANSHRY
       , oDenVO.FZAIOKIKANOFLG
       , oDenVO.FZAIOKIJISSEKIFLG
       , oDenVO.DLVRYTNSHACD
       , oDenVO.DLVRYKANKATSUAPKIHONCD
       , oDenVO.DLVRYKANKATSUSHOPCD
       , oDenVO.DLVRYKYOTENCD
       , oDenVO.DLVRYAREACD
       , oDenVO.DLVRYKBN
       , oDenVO.DLVRYCMPNYCD
       , oDenVO.AZUKARIBTLN1N2RITTORUHNS
       , oDenVO.KYBTLN1N2RITTORUHNS
       , oDenVO.KZBTLN1N2RITTORUHNS
       , oDenVO.AZUKARIBTLN8RITTORUHNS
       , oDenVO.KYBTLN8RITTORUHNS
       , oDenVO.KZBTLN8RITTORUHNS
       , oDenVO.PRNTMEMON1
       , oDenVO.PRNTMEMON2
       , oDenVO.PRNTMEMON3
       , oDenVO.ACPTSHIMEPRCSDD
       , oDenVO.ACPTSHIMETNSHAAPKIHONCD
       , oDenVO.ACPTSHIMETNSHASHOPCD
       , oDenVO.ACPTSHIMEPRCSTNSHACD
       , oDenVO.DLVRYJISSEKIPRCSDD
       , oDenVO.DLVRYJISSEKIMONOAPKIHONCD
       , oDenVO.DLVRYJISSEKIMONOSHOPCD
       , oDenVO.DLVRYJISSEKIPRCSTNSHACD
       , oDenVO.URISHIMEPRCSDD
       , oDenVO.GETSUJISHIMEDYDM
       , oDenVO.GETSUJISHIMEDD
       , oDenVO.GETSUJIPRCSJISSIDD
       , oDenVO.DLVRYJUN
       , oDenVO.NOSHOPRNTTRGTFLG
       , oDenVO.NOSHOPRNTSUMIFLG
       , oDenVO.NOSHOPRNTDD
       , oDenVO.NOSHOHIKAEPRNTTRGTFLG
       , oDenVO.NOSHOHIKAEPRNTSUMIFLG
       , oDenVO.NOSHOHIKAEPRNTDD
       , oDenVO.NYKNHYOPRNTTRGTFLG
       , oDenVO.NYKNHYOPRNTSUMIFLG
       , oDenVO.NYKNHYOPRNTDD
       , oDenVO.WSVSIJISHOPRNTTRGTFLG
       , oDenVO.WSVSIJISHOPRNTSUMIFLG
       , oDenVO.WSVSIJISHOPRNTDD
-- インボイス対応追加 start ABI
       , oDenVO.SINGLDNPYFLG
-- インボイス対応追加 end ABI
       , oDenVO.DNPYSTS
       , oDenVO.ACPTSTS
       , oDenVO.DLVRYSTS
       , oDenVO.DATSKSMOTOKBN
       , oDenVO.SKSMOTOKANRIKBN
       , oDenVO.SKSMOTOKANRINO
       , oDenVO.ACPTHIKIATESAKIAPKIHONCD
       , oDenVO.ACPTHIKIATESAKISHOPCD
       , oDenVO.ACPTHIKIATESAKIKYOTENCD
       , oDenVO.DLVRYYTIRGRAYUSYOHNS
       , oDenVO.DLVRYYTIRGRAMUSHOHNS
       , oDenVO.DLVRYYTISRMYUSYOHNS
       , oDenVO.DLVRYYTISRMMUSHOHNS
       , oDenVO.DLVRYYTISONOTAYUSYOHNS
       , oDenVO.DLVRYYTISONOTAMUSHOHNS
       , oDenVO.DLVRYYTISEIHINMIZUURIKIN
       , oDenVO.DLVRYYTISONOTAURIKIN
       , oDenVO.DLVRYJISSEKIRGRAYUSYOHNS
       , oDenVO.DLVRYJISSEKIRGRAMUSHOHNS
       , oDenVO.DLVRYJISSEKISRMYUSYOHNS
       , oDenVO.DLVRYJISSEKISRMMUSHOHNS
       , oDenVO.DLVRYJISSEKISTYSHNS
       , oDenVO.DLVRYJISSEKISTMSHNS
       , oDenVO.DLVRYJISSEKISHSUAKIN
       , oDenVO.DLVRYJISSEKISONOTAURIKIN
       , oDenVO.ACPTKIN
       , oDenVO.ACPTSHOHITAXKIN
-- インボイス対応追加 start ABI
       , oDenVO.ACPTIPPANNUKIKIN
       , oDenVO.ACPTIPPANTAXKIN
       , oDenVO.ACPTKEIGENNUKIKIN
       , oDenVO.ACPTKEIGENTAXKIN
       , oDenVO.ACPTHIKAZEIURIIKIN
       , oDenVO.ACPTFUKAZEIURIIKIN
-- インボイス対応追加 end ABI
       , oDenVO.NOHINKIN
       , oDenVO.NOHINSHOHITAXKIN
-- インボイス対応追加 start ABI
       , oDenVO.NOHINIPPANNUKIKIN
       , oDenVO.NOHINIPPANTAXKIN
       , oDenVO.NOHINKEIGENNUKIKIN
       , oDenVO.NOHINKEIGENTAXKIN
       , oDenVO.NOHINHIKAZEIURIIKIN
       , oDenVO.NOHINFUKAZEIURIIKIN
-- インボイス対応追加 end ABI
       , oDenVO.KURERURITTORUSTUP
       , oDenVO.KURERURITTORUHIKIAGE
       , oDenVO.KURERUESUSTUP
       , oDenVO.KURERUESUHIKIAGE
       , oDenVO.SONOTASTUP
       , oDenVO.SONOTAHIKIAGE
       , oDenVO.TAXKEISANKBN
       , oDenVO.ZNKDLVRYDD
       , oDenVO.ZNKDLVRYHNS
       , oDenVO.JIKAIDLVRYYTIDD
       , oDenVO.ZNKSHIMEDD
       , oDenVO.ZNKKURIKOSHIKIN
       , oDenVO.ACPTTHDAIBIKIFLG
       , oDenVO.ACPTRCPTKEIROKBN
       , oDenVO.KAISHUHOHOKBN
       , oDenVO.ACPTTHDAIBIKIFLG
       , oDenVO.ITAKUAITEAPKIHONCD
       , oDenVO.ITAKUAITESHOPCD
       , oDenVO.ITAKUAITEACPTDNPYNO
       , oDenVO.ITAKUAITEEDANO
       , oDenVO.URIDNPYNO
       , oDenVO.SKYSAKICD
       , oDenVO.JOTAISTS
       , oDenVO.ACPTTEISHIFLG
       , oDenVO.TOKUISAKIKBN
       , oDenVO.ITAKUAITETOKUISAKICD
       , oDenVO.ZAIKOKANRIKBN
       , oDenVO.YUKOKBN
    FROM
      S04T001T JHTT
    LEFT OUTER JOIN S02M013T TKSM
      ON ( TKSM.APKIHONCD   = JHTT.APKIHONCD
       AND TKSM.SHOPCD      = JHTT.SHOPCD
       AND TKSM.TOKUISAKICD = JHTT.TOKUISAKICD
       AND TKSM.YUKOKBN     = '1'
      )
    LEFT OUTER JOIN S02M017T SKSM
      ON ( SKSM.APKIHONCD = JHTT.APKIHONCD
       AND SKSM.SHOPCD    = JHTT.SHOPCD
       AND SKSM.SKYSAKICD = TKSM.SKYSAKICD
       AND SKSM.YUKOKBN   = '1'
      )
    LEFT OUTER JOIN S02M046T SPM
      ON ( SPM.APKIHONCD  = JHTT.APKIHONCD
       AND SPM.SHOPCD     = JHTT.SHOPCD 
       AND SPM.YUKOKBN    = '1'
      )
    WHERE
          JHTT.APKIHONCD  = iApkihoncd
      AND JHTT.SHOPCD     = iShopcd
      AND JHTT.ACPTDNPYNO = iAcptdnpyno
      AND JHTT.EDANO      = iEdano
    ORDER BY 
        JHTT.ACPTDNPYNO DESC
      , JHTT.EDANO DESC
    FOR UPDATE OF JHTT.ACPTDNPYNO
    ; 
    
    --拠点ステータス
    IF oDenVO.APKIHONCD = oDenVO.DLVRYKANKATSUAPKIHONCD AND oDenVO.SHOPCD = oDenVO.DLVRYKANKATSUSHOPCD THEN
      oDenVO.KYOTENSTS := c_JIKYOTEN; --自拠点
    ELSE
      oDenVO.KYOTENSTS := c_TAKYOTEN; --他拠点
    END IF;
    
    EXCEPTION 
      WHEN NO_DATA_FOUND THEN 
        --伝票情報なし
        ErrAdd(c_ERRKBN_ERR, 'EJ044:受注伝票が存在しません。');
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20104, SQLERRM || '受注伝票取得時エラーが発生しました。');
  END loadJuchuDenpyo;

  /****************************************************************************/
  /* プロシージャ名：他拠点チェック                                           */
  /****************************************************************************/
  PROCEDURE takyotenChk
  IS 
  /* 変数宣言 */
  wYukoChk        NUMBER(1,0);   -- 有効性チェック用
  
  BEGIN
    
    FOR i IN 1..meisaiList.COUNT LOOP
    
      --委託AP/ショップ独自商品チェック
      SELECT COUNT(1)
        INTO wYukoChk
        FROM S02V001T SHN
       WHERE SHN.APKIHONCD   = mainVO.APKIHONCD
         AND SHN.SHOPCD      = mainVO.SHOPCD
         AND SHN.SHNCD       = meisaiList(i).SHNCD
         AND SHN.SHNZOKUSEI  = '1' --AP商品
         AND mainVO.ACPTDD BETWEEN SHN.HANBAISTRTDD AND SHN.HANBAISHRYDD
         AND SHN.SHOPYUKOKBN = '1'
      ;
      IF wYukoChk > 0 THEN
        ErrAdd(c_ERRKBN_ERR, 'EJ028:AP／ショップの商品が含まれています。' || '[' || TO_CHAR(i) || '明細目]');
      END IF;
      
      --委託ショップ商品マスタ存在チェック
      SELECT COUNT(1)
        INTO wYukoChk
        FROM S02V001T SHN
        JOIN S02M043T SHOPSHN
          ON SHOPSHN.APKIHONCD = SHN.APKIHONCD
         AND SHOPSHN.SHOPCD    = SHN.SHOPCD
         AND SHOPSHN.SHNCD     = SHN.SHNCD
         AND mainVO.ACPTDD BETWEEN SHOPSHN.HANBAISTRTDD AND SHOPSHN.HANBAISHRYDD
         AND SHOPSHN.YUKOKBN   = '1'
       WHERE SHN.APKIHONCD   = mainVO.APKIHONCD
         AND SHN.SHOPCD      = mainVO.SHOPCD
         AND SHN.SHNCD       = meisaiList(i).SHNCD
         AND SHN.SHNZOKUSEI  = '0' --本部商品
         AND SHN.SHOPYUKOKBN = '1'
      ;
      IF wYukoChk = 0 THEN
        ErrAdd(c_ERRKBN_ERR, 'EJ029:委託会社で取り扱っていない商品コードが含まれています。' || '[' || TO_CHAR(i) || '明細目]');
      END IF;
      
      --受託AP/ショップ独自商品チェック
      SELECT COUNT(1)
        INTO wYukoChk
        FROM S02V001T SHN
       WHERE SHN.APKIHONCD   = mainVO.ITAKUAITEAPKIHONCD
         AND SHN.SHOPCD      = mainVO.ITAKUAITESHOPCD
         AND SHN.SHNCD       = meisaiList(i).SHNCD
         AND SHN.SHNZOKUSEI  = '1' --AP商品
         AND mainVO.ACPTDD BETWEEN SHN.HANBAISTRTDD AND SHN.HANBAISHRYDD
         AND SHN.SHOPYUKOKBN = '1'
      ;
      IF wYukoChk > 0 THEN
        ErrAdd(c_ERRKBN_ERR, 'EJ030:AP／ショップの商品が含まれています。' || '[' || TO_CHAR(i) || '明細目]');
      END IF;
      
      --受託ショップ商品マスタ存在チェック
      SELECT COUNT(1)
        INTO wYukoChk
        FROM S02V001T SHN
        JOIN S02M043T SHOPSHN
          ON SHOPSHN.APKIHONCD = SHN.APKIHONCD
         AND SHOPSHN.SHOPCD    = SHN.SHOPCD
         AND SHOPSHN.SHNCD     = SHN.SHNCD
         AND mainVO.ACPTDD BETWEEN SHOPSHN.HANBAISTRTDD AND SHOPSHN.HANBAISHRYDD
         AND SHOPSHN.YUKOKBN   = '1'
       WHERE SHN.APKIHONCD   = mainVO.ITAKUAITEAPKIHONCD
         AND SHN.SHOPCD      = mainVO.ITAKUAITESHOPCD
         AND SHN.SHNCD       = meisaiList(i).SHNCD
         AND SHN.SHNZOKUSEI  = '0' --本部商品
         AND SHN.SHOPYUKOKBN = '1'
      ;
      IF wYukoChk = 0 THEN
        ErrAdd(c_ERRKBN_ERR, 'EJ031:受託会社で取り扱っていない商品コードが含まれています。' || '[' || TO_CHAR(i) || '明細目]');
      END IF;
      
    END LOOP;
    
    --受託得意先コード存在チェック
    SELECT COUNT(1)
      INTO wYukoChk
      FROM S02M013T TSM
     WHERE TSM.APKIHONCD   = mainVO.APKIHONCD
       AND TSM.SHOPCD      = mainVO.SHOPCD
       AND TSM.TOKUISAKICD = mainVO.TOKUISAKICD
       AND TSM.ITAKUAITETOKUISAKICD IS NULL
       AND TSM.YUKOKBN     = '1'
    ;
    IF wYukoChk > 0 THEN
      ErrAdd(c_ERRKBN_ERR, 'EJ032:委託会社の得意先マスタに委託相手コードが未設定です。');
    END IF;
    
    --受託得意先マスタ存在チェック
    SELECT COUNT(1)
      INTO wYukoChk
      FROM S02M013T TSM
      JOIN S02M013T TSMJ
        ON TSMJ.APKIHONCD   = TSM.ITAKUAITEAPKIHONCD
       AND TSMJ.SHOPCD      = TSM.ITAKUAITESHOPCD
       AND TSMJ.TOKUISAKICD = TSM.ITAKUAITETOKUISAKICD
       AND TSMJ.YUKOKBN     = '1'
     WHERE TSM.APKIHONCD   = mainVO.APKIHONCD
       AND TSM.SHOPCD      = mainVO.SHOPCD
       AND TSM.TOKUISAKICD = mainVO.TOKUISAKICD
       AND TSM.ITAKUAITETOKUISAKICD IS NOT NULL
       AND TSM.YUKOKBN     = '1'
    ;
    IF wYukoChk = 0 THEN
      ErrAdd(c_ERRKBN_ERR, 'EJ033:委託会社の得意先マスタが存在しません。');
    END IF;
    
    --委託相手コード連携チェック
    SELECT COUNT(1)
      INTO wYukoChk
      FROM S02M013T TSMJ
     WHERE TSMJ.APKIHONCD            = mainVO.ITAKUAITEAPKIHONCD
       AND TSMJ.SHOPCD               = mainVO.ITAKUAITESHOPCD
       AND TSMJ.TOKUISAKICD          = mainVO.ITAKUAITETOKUISAKICD
       AND TSMJ.ITAKUAITEAPKIHONCD   = mainVO.APKIHONCD
       AND TSMJ.ITAKUAITESHOPCD      = mainVO.SHOPCD
       AND TSMJ.ITAKUAITETOKUISAKICD = mainVO.TOKUISAKICD
       AND TSMJ.YUKOKBN     = '1'
    ;
    IF wYukoChk = 0 THEN
      ErrAdd(c_ERRKBN_ERR, 'EJ034:委託会社、受託会社の委託相手コードが紐付いていません。');
    END IF;

    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20105, SQLERRM || '他拠点チェック時エラーが発生しました。');
  END takyotenChk;
  
  /****************************************************************************/
  /* プロシージャ名：受注伝票チェック                                         */
  /****************************************************************************/
  PROCEDURE denpyoChk
  IS 
  /* 変数宣言 */
  wYukoChk        NUMBER(1,0);   -- 有効性チェック用
  wKako           S02M006T.SUCHISETTEIATAI%TYPE;   -- 過去日数
  wMirai          S02M006T.SUCHISETTEIATAI%TYPE;   -- 未来日数
  calendarModel   S02M051T%rowtype;
  eigyobiModel    S02M005T%rowtype;
  initWeek        NUMBER(1,0);
  initDayWeek     NUMBER(1,0);
  workDayWeek     NUMBER(3,0);
  listcnt         NUMBER(3,0) := 0;
  wkDays          NUMBER(3,0);
  systime         VARCHAR2(4);
  TYPE dlvWeektype IS TABLE OF VARCHAR2(1) INDEX BY PLS_INTEGER;
  dlvWeek dlvWeektype;
  TYPE dlvDaytype IS TABLE OF VARCHAR2(1) INDEX BY PLS_INTEGER;
  dlvDay dlvDaytype;
  TYPE dlvRequiredtype IS TABLE OF NUMBER(1,0) INDEX BY PLS_INTEGER;
  dlvRequired dlvRequiredtype;
  TYPE dlvClosingtype IS TABLE OF VARCHAR2(4) INDEX BY PLS_INTEGER;
  dlvClosing dlvClosingtype;
  
  TYPE haitatsuDateVotype IS RECORD (
                               INDEXNO        NUMBER(3,0)   DEFAULT 0
                             , APKIHONCD      VARCHAR2(3)   DEFAULT NULL
                             , SHOPCD         VARCHAR2(3)   DEFAULT NULL
                             , DLVRYKYOTENCD  VARCHAR2(2)   DEFAULT NULL
                             , DLVRYAREACD    VARCHAR2(5)   DEFAULT NULL
                             , DLVRYDATE      VARCHAR2(8)   DEFAULT NULL
                             , REQUIREDDAYS   NUMBER(1,0)   DEFAULT 0
                             , CLOSINGTIME    VARCHAR2(4)   DEFAULT NULL
                             , ACPTFLG        VARCHAR2(1)   DEFAULT NULL
                             , DLVRYFLG       VARCHAR2(1)   DEFAULT NULL
                             , HANTEISTATUS   VARCHAR2(7)   DEFAULT NULL
  );
  haitatsuDateVo haitatsuDateVotype;
  TYPE haitatsuDateListtype IS TABLE OF haitatsuDateVotype INDEX BY PLS_INTEGER;
  haitatsuDateList haitatsuDateListtype;
  
  CURSOR SELECT_EIGYOBI IS
    SELECT
       APKIHONCD
     , SHOPCD
     , EIGYODD
     , ACPTEIGYOKBN
     , DLVRYEIGYOKBN
     , STUPEIGYOKBN
     , SEISANEIGYOKBN
    FROM
      S02M005T
    WHERE
          APKIHONCD = mainVO.APKIHONCD
      AND SHOPCD = mainVO.SHOPCD
      AND EIGYODD >= gUnyoDate
      AND EIGYODD <= mainVO.DLVRYYTIDD
      AND YUKOKBN = '1'
    ORDER BY
      EIGYODD
    ;
  
  BEGIN
  
    --配達希望時間入力チェック
    IF mainVO.DLVRYKIBOJIKANKBN IN ('5', '6', '8') AND mainVO.DLVRYKIBOJIKAN IS NULL THEN
      ErrAdd(c_ERRKBN_ERR, 'EJ035:配達希望時刻を入力して下さい。');
    END IF;
    
    --配達希望時間終了入力チェック
    IF mainVO.DLVRYKIBOJIKANKBN = '8' AND mainVO.DLVRYKIBOJIKANSHRY IS NULL THEN
      ErrAdd(c_ERRKBN_ERR, 'EJ036:配達希望時刻終了を入力して下さい。');
    END IF;
    
    --配達希望時間範囲チェック
    IF mainVO.DLVRYKIBOJIKANKBN = '8' AND mainVO.DLVRYKIBOJIKAN > mainVO.DLVRYKIBOJIKANSHRY THEN
      ErrAdd(c_ERRKBN_ERR, 'EJ037:配達希望時刻と配達希望時刻終了の大小関係が正しくありません。');
    END IF;
    
    --受注日指定範囲チェック
    BEGIN
      SELECT SUCHISETTEIATAI
        INTO wKako
        FROM S02M006T SKM
       WHERE SKM.APKIHONCD   = mainVO.APKIHONCD
         AND SKM.SHOPCD      = mainVO.SHOPCD
         AND SKM.SETTEINO    = '115' --受注日
         AND SKM.SETTEICD    = '1' --過去日数
         AND SKM.YUKOKBN     = '1'
      ;
      EXCEPTION 
        WHEN NO_DATA_FOUND THEN 
          wKako := 0;
    END;
    BEGIN
      SELECT SUCHISETTEIATAI
        INTO wMirai
        FROM S02M006T SKM
       WHERE SKM.APKIHONCD   = mainVO.APKIHONCD
         AND SKM.SHOPCD      = mainVO.SHOPCD
         AND SKM.SETTEINO    = '115' --受注日
         AND SKM.SETTEICD    = '2' --未来日数
         AND SKM.YUKOKBN     = '1'
      ;
      EXCEPTION 
        WHEN NO_DATA_FOUND THEN 
          wMirai := 0;
    END;
    
-- 本線対応変更 start ABI
--    IF (TO_DATE(gUnyoDate) - wKako) > mainVO.ACPTDD OR (TO_DATE(gUnyoDate) + wMirai) < mainVO.ACPTDD THEN
    IF TO_CHAR(TO_DATE(gUnyoDate, 'YYYYMMDD') - wKako, 'YYYYMMDD') > mainVO.ACPTDD OR TO_CHAR(TO_DATE(gUnyoDate, 'YYYYMMDD') + wMirai, 'YYYYMMDD') < mainVO.ACPTDD THEN
-- 本線対応変更 end ABI
      ErrAdd(c_ERRKBN_ERR, 'EJ038:受注日は'||wKako||'日前～'||wMirai||'日先の間で入力して下さい。');
    END IF;
    
    --配達予定日指定範囲チェック
    BEGIN
      SELECT SUCHISETTEIATAI
        INTO wKako
        FROM S02M006T SKM
       WHERE SKM.APKIHONCD   = mainVO.APKIHONCD
         AND SKM.SHOPCD      = mainVO.SHOPCD
         AND SKM.SETTEINO    = '116' --配達予定日
         AND SKM.SETTEICD    = '1' --過去日数
         AND SKM.YUKOKBN     = '1'
      ;
      EXCEPTION 
        WHEN NO_DATA_FOUND THEN 
          wKako := 0;
    END;
    BEGIN
      SELECT SUCHISETTEIATAI
        INTO wMirai
        FROM S02M006T SKM
       WHERE SKM.APKIHONCD   = mainVO.APKIHONCD
         AND SKM.SHOPCD      = mainVO.SHOPCD
         AND SKM.SETTEINO    = '116' --配達予定日
         AND SKM.SETTEICD    = '2' --未来日数
         AND SKM.YUKOKBN     = '1'
      ;
      EXCEPTION 
        WHEN NO_DATA_FOUND THEN 
          wMirai := 0;
    END;
    
-- 本線対応変更 start ABI
--    IF (TO_DATE(gUnyoDate) - wKako) > mainVO.DLVRYYTIDD OR (TO_DATE(gUnyoDate) + wMirai) < mainVO.DLVRYYTIDD THEN
    IF TO_CHAR(TO_DATE(gUnyoDate, 'YYYYMMDD') - wKako, 'YYYYMMDD') > mainVO.DLVRYYTIDD OR TO_CHAR(TO_DATE(gUnyoDate, 'YYYYMMDD') + wMirai, 'YYYYMMDD') < mainVO.DLVRYYTIDD THEN
-- 本線対応変更 end ABI
      ErrAdd(c_ERRKBN_ERR, 'EJ038:配達予定日は'||wKako||'日前～'||wMirai||'日先の間で入力して下さい。');
    END IF;
    
    --受注日・配達予定日大小チェック
    IF mainVO.ACPTDD > mainVO.DLVRYYTIDD THEN
      ErrAdd(c_ERRKBN_ERR, 'EJ039:受注日と配達日の大小関係が正しくありません。');
    END IF;
    
    --配達予定日過去チェック
    IF gUnyoDate > mainVO.DLVRYYTIDD THEN
      ErrAdd(c_ERRKBN_WAR, 'WJ002:配達予定日が過去日です。');
    END IF;
    
    --配達可能チェック
    IF mainVO.DLVRYKBN <> c_DLVRY_MOCHIKAERI THEN
      BEGIN
        --配達エリアマスタからデータを取得
        --ACCIS2023-7365 配達エリアマスタからデータを取得する条件に有効区分を追加
        SELECT DLVRYSHUA
             , DLVRYSHUB
             , DLVRYSHUC
             , DLVRYSHUD
             , SUNDDLVRYKANODD
             , MONDDLVRYKANODD
             , TUEDDLVRYKANODD
             , WEDDDLVRYKANODD
             , THEDDLVRYKANODD
             , FRIDDLVRYKANODD
             , SATDDLVRYKANODD
             , SUNDHITSUYOEIGYODD
             , MONDHITSUYOEIGYODD
             , TUEDHITSUYOEIGYODD
             , WEDDHITSUYOEIGYODD
             , THEDHITSUYOEIGYODD
             , FRIDHITSUYOEIGYODD
             , SATDHITSUYOEIGYODD
             , SUNDACPTSHIMEJIKOKU
             , MONDACPTSHIMEJIKOKU
             , TUEDACPTSHIMEJIKOKU
             , WEDDACPTSHIMEJIKOKU
             , THEDACPTSHIMEJIKOKU
             , FRIDACPTSHIMEJIKOKU
             , SATDACPTSHIMEJIKOKU
          INTO dlvWeek(1) --配達エリアの配達WEEK取得
             , dlvWeek(2)
             , dlvWeek(3)
             , dlvWeek(4)
             , dlvDay(1) --配達エリアの配達可能日取得
             , dlvDay(2)
             , dlvDay(3)
             , dlvDay(4)
             , dlvDay(5)
             , dlvDay(6)
             , dlvDay(7)
             , dlvRequired(1) --配達エリアの必要日数取得
             , dlvRequired(2)
             , dlvRequired(3)
             , dlvRequired(4)
             , dlvRequired(5)
             , dlvRequired(6)
             , dlvRequired(7)
             , dlvClosing(1) --配達エリアの締め時間取得
             , dlvClosing(2)
             , dlvClosing(3)
             , dlvClosing(4)
             , dlvClosing(5)
             , dlvClosing(6)
             , dlvClosing(7)
          FROM S02M018T
         WHERE APKIHONCD     = mainVO.APKIHONCD
           AND SHOPCD        = mainVO.SHOPCD
           AND DLVRYKYOTENCD = mainVO.DLVRYKYOTENCD
           AND DLVRYAREACD   = mainVO.DLVRYAREACD
           AND TEKIYOSTRTDD  <= gUnyoDate
           AND TEKIYOSHRYDD  >= gUnyoDate
           AND YUKOKBN       = '1'
        ;
        
        --カレンダーマスタからデータを取得
        SELECT DLVRYSHUU
             , DLVRYDD
          INTO calendarModel.DLVRYSHUU
             , calendarModel.DLVRYDD
          FROM S02M051T
         WHERE APKIHONCD = mainVO.APKIHONCD
           AND SHOPCD    = mainVO.SHOPCD
           AND DLVRYDD   = gUnyoDate
        ;
        
        CASE  calendarModel.DLVRYSHUU
          WHEN 'A' THEN
            initWeek       := 0;
          WHEN 'B' THEN
            initWeek       := 1;
          WHEN 'C' THEN
            initWeek       := 2;
          ELSE
            initWeek       := 3;
        END CASE;
        
-- 本線対応変更 start ABI
        --initDayWeek := TO_CHAR(TO_DATE(calendarModel.DLVRYDD), 'D');
        initDayWeek := TO_CHAR(TO_DATE(calendarModel.DLVRYDD, 'YYYYMMDD'), 'D');
-- 本線対応変更 start ABI
        
        OPEN SELECT_EIGYOBI;
        LOOP FETCH SELECT_EIGYOBI INTO eigyobiModel.APKIHONCD
                                     , eigyobiModel.SHOPCD
                                     , eigyobiModel.EIGYODD
                                     , eigyobiModel.ACPTEIGYOKBN
                                     , eigyobiModel.DLVRYEIGYOKBN
                                     , eigyobiModel.STUPEIGYOKBN
                                     , eigyobiModel.SEISANEIGYOKBN;
        
          EXIT WHEN SELECT_EIGYOBI%NOTFOUND; 
          
          workDayWeek := initDayWeek + listcnt - 1;
          
          --基本属性をセット
          haitatsuDateVo.INDEXNO       := listcnt;
          haitatsuDateVo.APKIHONCD     := mainVO.APKIHONCD;
          haitatsuDateVo.SHOPCD        := mainVO.SHOPCD;
          haitatsuDateVo.DLVRYKYOTENCD := mainVO.DLVRYKYOTENCD;
          haitatsuDateVo.DLVRYAREACD   := mainVO.DLVRYAREACD;
          haitatsuDateVo.DLVRYDATE     := eigyobiModel.EIGYODD;
          --営業日フラグセット
          IF eigyobiModel.ACPTEIGYOKBN = '1' THEN
            haitatsuDateVo.ACPTFLG := '1';
          ELSE
            haitatsuDateVo.ACPTFLG := '0';
          END IF;
          --配達日フラグセット（配達営業日、配達ＷＥＥＫ、配達曜日ともに可能な日のみ'1'）
          IF eigyobiModel.DLVRYEIGYOKBN = '1' AND 
             dlvWeek(MOD((initWeek + TRUNC(workDayWeek / 7, 0)), 4) + 1) = '1' AND 
             dlvDay(MOD(workDayWeek, 7) + 1) = '1' THEN
            haitatsuDateVo.DLVRYFLG := '1';
          ELSE
            haitatsuDateVo.DLVRYFLG := '0';
          END IF;
          --必要日数セット
          haitatsuDateVo.REQUIREDDAYS := NVL(dlvRequired(MOD(workDayWeek, 7) + 1), 0);
          --締め時間セット
          haitatsuDateVo.CLOSINGTIME := dlvClosing(MOD(workDayWeek, 7) + 1);
          
          listcnt := listcnt + 1;
          
          --リストに追加
          haitatsuDateList(listcnt) := haitatsuDateVo;
        
        END LOOP;
        CLOSE SELECT_EIGYOBI;
        
        EXCEPTION 
          WHEN NO_DATA_FOUND THEN 
            NULL;
      END;
      
      IF haitatsuDateList.COUNT <> 0 THEN
        
        haitatsuDateVo := haitatsuDateList(haitatsuDateList.COUNT);
        haitatsuDateVo.HANTEISTATUS := 'UNAVAIL';
        
        IF haitatsuDateVo.DLVRYFLG = '1' THEN
          wkDays := haitatsuDateVo.REQUIREDDAYS;
          FOR indexcnt IN REVERSE 1..haitatsuDateVo.INDEXNO LOOP
            IF haitatsuDateList(indexcnt).ACPTFLG = '1' THEN
              wkDays := wkDays - 1;
            END IF;
          END LOOP;
          
          IF wkDays < 0 THEN
            haitatsuDateVo.HANTEISTATUS := 'AVAIL';
          ELSIF wkDays = 0 THEN
            haitatsuDateVo.HANTEISTATUS := 'AVAIL';
            
-- 本線対応変更 start ABI
            --SELECT TO_CHAR(SYSDATE, 'hh24mi')
            SELECT TO_CHAR(CURRENT_DATE, 'hh24mi')
-- 本線対応変更 end ABI
              INTO systime
              FROM dual
            ;
            
            IF haitatsuDateVo.CLOSINGTIME IS NULL OR systime IS NULL OR systime > haitatsuDateVo.CLOSINGTIME THEN
              haitatsuDateVo.HANTEISTATUS := 'OVER';
            END IF;
          END IF;
        END IF;
        
        --配達可能チェック
        IF haitatsuDateVo.HANTEISTATUS = 'OVER' AND haitatsuDateList(1).ACPTFLG = '1' THEN
          ErrAdd(c_ERRKBN_WAR, 'WJ003:入力された配達予定日の受注締め時間を過ぎています。');
        --配達可能チェック２
        ELSIF haitatsuDateVo.HANTEISTATUS = 'UNAVAIL' THEN
          ErrAdd(c_ERRKBN_WAR, 'WJ004:配達予定日は配達可能日ではありません。');
        END IF;
      END IF;
    END IF;
    
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20106, SQLERRM || '受注伝票チェック時エラーが発生しました。');
  END denpyoChk;
  
  /****************************************************************************/
  /* プロシージャ名：消費税、金額計算                                         */
  /* 引数           ：iTanka            単価                                  */
  /*                ：iSuryo            数量                                  */
  /*                ：iShnTaxkbn        商品単位の税区分                      */
  /*                ：iTaxsetteikbn     税設定区分                            */
  /*                ：iShncd            商品コード                            */
  /*                ：iKeijokbn         計上区分                              */
  /*                ：oTankaTax         単価消費税                            */
  /*                ：oTaxPrice         消費税金額                            */
  /*                ：oExcPrice         税抜金額                              */
  /****************************************************************************/
  PROCEDURE getPriceList( iTanka         IN NUMBER
                        , iSuryo         IN NUMBER
                        , iShnTaxkbn     IN VARCHAR2
                        , iTaxsetteikbn  IN VARCHAR2
                        , iShncd         IN VARCHAR2
                        , iKeijokbn      IN VARCHAR2
                        , oTankaTax      OUT NUMBER
                        , oTaxPrice      OUT NUMBER
                        , oExcPrice      OUT NUMBER
  ) IS 
  /* 変数宣言 */
  taxkbn VARCHAR2(1);
  wUrikinhasukeisankbn      VARCHAR2(1)  := NULL;  -- 売上金額端数計算区分
  BEGIN

    --税抜金額
    -- ショップマスタ取得 
    SELECT
      SHPM.URIKINHASUKEISANKBN  -- 売上金額端数計算区分
    INTO wUrikinhasukeisankbn
    FROM
      S02M046T SHPM 
    WHERE
      SHPM.APKIHONCD = mainVO.APKIHONCD 
      AND SHPM.SHOPCD = mainVO.SHOPCD  
      AND SHPM.YUKOKBN = '1'
    ;
-- 本線対応変更 start ABI
--    oExcPrice := S01SyohizeiService.roundValue(iTanka * iSuryo, wUrikinhasukeisankbn);
    oExcPrice := Z1808B01.roundValue(iTanka * iSuryo, wUrikinhasukeisankbn);
-- 本線対応変更 end ABI
    
-- インボイス対応削除 start ABI
--    IF iShnTaxkbn <> '5' THEN
-- インボイス対応変削除 end ABI
    --外税
    IF mainVO.TAXKEISANKBN = '2' THEN
      --単価消費税
      oTankaTax := 0;
      --消費税金額
-- 本線対応変更 start ABI
--      oTaxPrice := S01SyohizeiService.SyohizeiKeisan('1', --単体計算フラグ（する）
      oTaxPrice := Z1808B01.SyohizeiKeisan('1', --単体計算フラグ（する）
-- 本線対応変更 end ABI
                                           iTaxsetteikbn, --税設定区分
                                              iShnTaxkbn, --税区分
                                     mainVO.TAXKEISANKBN, --税計算区分
                                       mainVO.DLVRYYTIDD, --切り替え日：配達予定日
                                        mainVO.APKIHONCD, --AP基本コード
                                           mainVO.SHOPCD, --ショップコード
                                                    NULL, --仕入先コード
                                                    NULL, --数量
                                              oExcPrice); -- 金額
    --内税
    ELSE
      --単価消費税
-- 本線対応変更 start ABI
--      oTankaTax := S01SyohizeiService.SyohizeiKeisan('1', --単体計算フラグ（する）
      oTankaTax := Z1808B01.SyohizeiKeisan('1', --単体計算フラグ（する）
-- 本線対応変更 end ABI
                                           iTaxsetteikbn, --税設定区分
                                              iShnTaxkbn, --税区分
                                     mainVO.TAXKEISANKBN, --税計算区分
                                       mainVO.DLVRYYTIDD, --切り替え日：配達予定日
                                        mainVO.APKIHONCD, --AP基本コード
                                           mainVO.SHOPCD, --ショップコード
                                                    NULL, --仕入先コード
                                                    NULL, --数量
                                                 iTanka); -- 単価
      --消費税金額
      IF iTanka IS NULL OR iTanka = 0 THEN
-- 本線対応変更 start ABI
--        oTaxPrice := S01SyohizeiService.SyohizeiKeisan('1', --単体計算フラグ（する）
        oTaxPrice := Z1808B01.SyohizeiKeisan('1', --単体計算フラグ（する）
-- 本線対応変更 end ABI
                                             iTaxsetteikbn, --税設定区分
                                                iShnTaxkbn, --税区分
                                       mainVO.TAXKEISANKBN, --税計算区分
                                         mainVO.DLVRYYTIDD, --切り替え日：配達予定日
                                          mainVO.APKIHONCD, --AP基本コード
                                             mainVO.SHOPCD, --ショップコード
                                                      NULL, --仕入先コード
                                                      NULL, --数量
                                                oExcPrice); -- 金額
      ELSE
        oTaxPrice := oTankaTax * iSuryo;
      END IF;
    END IF;
-- インボイス対応削除 start ABI
--    ELSE
--          S01SyohizeiService.SetSyohizeiKeisan(iTaxsetteikbn, --税設定区分
--                                         mainVO.TAXKEISANKBN, --税計算区分
--                                           mainVO.DLVRYYTIDD, --切り替え日：配達予定日
--                                            mainVO.APKIHONCD, --AP基本コード
--                                               mainVO.SHOPCD, --ショップコード
--                                                      iSuryo, --数量
--                                                      iShncd, --商品コード
--                                             mainVO.DLVRYKBN, --配達区分
--                                                   iKeijokbn, --計上区分
--                                                   oTankaTax, --戻り値：単価消費税
--                                                   oTaxPrice); --戻り値：消費税
--    END IF;
-- インボイス対応削除 end ABI
    
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20107, SQLERRM || '消費税、金額計算時エラーが発生しました。');
  END getPriceList;
  
  /****************************************************************************/
  /* プロシージャ名：金額計算処理                                             */
  /****************************************************************************/
  PROCEDURE kingakuCalc
  IS 
  /* 変数宣言 */
  shohinModel S02V001T%rowtype;
  hansokuunt   NUMBER(10,2)    DEFAULT NULL;
  tokuisakiunt NUMBER(10,2)    DEFAULT NULL;
  sumKingaku    NUMBER(11,0)    DEFAULT 0;
  sumTaxKingaku NUMBER(11,0)    DEFAULT 0;
-- インボイス対応追加 start ABI
  sumAcptippannukikin    NUMBER(11,0)    DEFAULT 0;
  sumAcpthikazeiuriikin  NUMBER(11,0)    DEFAULT 0;
  sumAcptfukazeiuriikin  NUMBER(11,0)    DEFAULT 0;
  sumAcptkeigennukikin   NUMBER(11,0)    DEFAULT 0;
  wInvoicshikoymd           VARCHAR2(8) := NULL;   -- インボイス施行日
-- インボイス対応追加 end ABI
  wUrikinhasukeisankbn      VARCHAR2(1)  := NULL;  -- 売上金額端数計算区分
  tokuisakisetunt NUMBER(10,2) DEFAULT NULL;
  
  BEGIN
    -- ショップマスタ取得 
    SELECT
      SHPM.URIKINHASUKEISANKBN  -- 売上金額端数計算区分
-- インボイス対応追加 start ABI
    , SHPM.INVOICSHIKOYMD     -- インボイス施行日
-- インボイス対応追加 end ABI
    INTO wUrikinhasukeisankbn
-- インボイス対応追加 start ABI
       , wInvoicshikoymd
-- インボイス対応追加 end ABI
    FROM
      S02M046T SHPM 
    WHERE
      SHPM.APKIHONCD = mainVO.APKIHONCD 
      AND SHPM.SHOPCD = mainVO.SHOPCD  
      AND SHPM.YUKOKBN = '1'
    ;
    
    FOR i IN 1..meisaiList.COUNT LOOP
      
      IF meisaiList(i).KEIJOKBN NOT IN ('01', '02') THEN
        meisaiList(i).ACPTUNT := 0;
      END IF;
      
      --計上区分がキャンペーンで商品の販促設定：対象外の場合は単価0円
      IF meisaiList(i).KEIJOKBN = '02' AND meisaiList(i).HANSOKUKBN = '0' THEN
        meisaiList(i).ACPTUNT := 0;
      --得意先区分≠得意先,NA受託,配達受託の場合は置き換えない
      ELSIF mainVO.TOKUISAKIKBN NOT IN ('1', '5', '6') THEN
        NULL; 
      ELSE
        --価格区分が1：標準価格の場合のみ再セットする
        IF meisaiList(i).PRICEKBN = '1' THEN
          --計上区分＝「売上」「キャンペーン」、受注単価が0の場合に再セットする
          IF meisaiList(i).KEIJOKBN IN ('01', '02') AND meisaiList(i).ACPTUNT = 0 THEN
            --販促単価
            IF (mainVO.DLVRYYTIDD BETWEEN meisaiList(i).HANSOKUSTRTDD AND meisaiList(i).HANSOKUSHRYDD) THEN
            
              IF mainVO.DLVRYKBN = c_DLVRY_MOCHIKAERI THEN
                hansokuunt := meisaiList(i).HANSOKUMOCHIKAERIPRICE;
              ELSE
                hansokuunt := meisaiList(i).HANSOKUPRICE;
              END IF;
            ELSE
              hansokuunt := NULL;
            END IF;
            --得意先別商品別単価
            IF (mainVO.DLVRYYTIDD BETWEEN meisaiList(i).TTPRICESTRTDD AND meisaiList(i).TTPRICESHRYDD) THEN
            
              IF mainVO.DLVRYKBN = c_DLVRY_MOCHIKAERI THEN
                tokuisakiunt := meisaiList(i).TTMOCHIKAERIPRICE;
              ELSE
                tokuisakiunt := meisaiList(i).TTPRICE;
              END IF;
            ELSE
              tokuisakiunt := NULL;
            END IF;
            
            --得意先別セット商品別単価
            IF (mainVO.DLVRYYTIDD BETWEEN meisaiList(i).SETTTPRICESTRTDD AND meisaiList(i).SETTTPRICESHRYDD) THEN
              IF mainVO.DLVRYKBN = c_DLVRY_MOCHIKAERI THEN
                tokuisakisetunt := meisaiList(i).SETTTMOCHIKAERIPRICE;
              ELSE
                tokuisakisetunt := meisaiList(i).SETTTPRICE;
              END IF;
            ELSE
              tokuisakisetunt := NULL;
            END IF;
            
            IF meisaiList(i).KEIJOKBN = '01' AND tokuisakiunt IS NOT NULL THEN
              meisaiList(i).ACPTUNT := tokuisakiunt;
              
            ELSIF meisaiList(i).KEIJOKBN = '01' AND tokuisakisetunt IS NOT NULL THEN
              meisaiList(i).ACPTUNT := tokuisakisetunt;
            
            ELSIF meisaiList(i).KEIJOKBN = '02' THEN
              IF hansokuunt IS NULL THEN
                meisaiList(i).ACPTUNT := 0;
              ELSE
                meisaiList(i).ACPTUNT := hansokuunt;
              END IF;
              
            ELSE
              IF mainVO.DLVRYKBN = c_DLVRY_MOCHIKAERI THEN
                meisaiList(i).ACPTUNT := getCurrentValue(meisaiList(i).GENZHYOJUNMOCHIKAERIPRICE, meisaiList(i).SHINHYOJUNMOCHIKAERIPRICE, meisaiList(i).HYOJUNPRICEKIRIKAEDD, mainVO.DLVRYYTIDD);
              ELSE
                meisaiList(i).ACPTUNT := getCurrentValue(meisaiList(i).GENZHYOJUNPRICE, meisaiList(i).SHINHYOJUNPRICE, meisaiList(i).HYOJUNPRICEKIRIKAEDD, mainVO.DLVRYYTIDD);
              END IF;
            END IF;
          END IF;
        END IF;
      END IF;
      
-- インボイス対応削除 start ABI
--      IF meisaiList(i).TAXKBN IN ('4','5') AND mainVO.DLVRYYTIDD <= '20190930' THEN
--        meisaiList(i).TAXKBN := '1';
--      END IF;
-- インボイス対応削除 end ABI
      
      /* 消費税、金額計算部品 */
      IF meisaiList(i).TAXSETTEIKBN = '1' THEN
        getPriceList(meisaiList(i).ACPTUNT, meisaiList(i).ACPTSURYO, meisaiList(i).TAXKBN, meisaiList(i).TAXSETTEIKBN , meisaiList(i).SHNCD, meisaiList(i).KEIJOKBN,
                     meisaiList(i).ACPTUNTSHOHITAXKIN, meisaiList(i).ACPTSHOHITAXKIN, meisaiList(i).ACPTKIN);
      ELSE
        meisaiList(i).ACPTUNTSHOHITAXKIN := 0;
        meisaiList(i).ACPTSHOHITAXKIN := 0;
-- 本線対応変更 start ABI
--        meisaiList(i).ACPTKIN := S01SyohizeiService.roundValue(meisaiList(i).ACPTUNT * meisaiList(i).ACPTSURYO, wUrikinhasukeisankbn);
        meisaiList(i).ACPTKIN := Z1808B01.roundValue(meisaiList(i).ACPTUNT * meisaiList(i).ACPTSURYO, wUrikinhasukeisankbn);
-- 本線対応変更 end ABI
      END IF;
      
      sumKingaku := sumKingaku + meisaiList(i).ACPTKIN;
      sumTaxKingaku := sumTaxKingaku + meisaiList(i).ACPTSHOHITAXKIN;
-- インボイス対応追加 start ABI
       -- 税区分によって、以下の項目を設定
       -- 受注一般税抜金額、受注非課税金額、受注不課税売上金額、受注軽減税抜金額
       -- ※受注一般消費税金額、受注軽減消費税金額は加算完了後に消費税を算出して設定、受注金額、受注消費税金額も上記項目を合計して設定
       -- 税区分が「1:課税」の場合、受注一般税抜金額を加算
       IF meisaiList(i).TAXKBN = '1' THEN
         sumAcptippannukikin := sumAcptippannukikin     + meisaiList(i).ACPTKIN; -- 受注一般税抜金額
       -- 税区分が「2:非課税」の場合、受注非課税金額を加算
       ELSIF meisaiList(i).TAXKBN = '2' THEN
         sumAcpthikazeiuriikin := sumAcpthikazeiuriikin + meisaiList(i).ACPTKIN; -- 受注非課税金額
       -- 税区分が「3:不課税」の場合、受注不課税売上金額を加算
       ELSIF meisaiList(i).TAXKBN = '3' THEN
         sumAcptfukazeiuriikin := sumAcptfukazeiuriikin + meisaiList(i).ACPTKIN; -- 受注不課税売上金額
       -- 税区分が「4:課税軽減」の場合、受注軽減税抜金額を加算
       ELSIF meisaiList(i).TAXKBN = '4' THEN
         sumAcptkeigennukikin := sumAcptkeigennukikin   + meisaiList(i).ACPTKIN; -- 受注軽減税抜金額
       END IF;
-- インボイス対応追加 end ABI
    END LOOP;
    
    --明細の合計を設定
    mainVO.ACPTKIN := sumKingaku;
    mainVO.ACPTSHOHITAXKIN := sumTaxKingaku;
-- インボイス対応追加 start ABI
    mainVO.ACPTIPPANNUKIKIN   := sumAcptippannukikin;
    mainVO.ACPTHIKAZEIURIIKIN := sumAcpthikazeiuriikin;
    mainVO.ACPTFUKAZEIURIIKIN := sumAcptfukazeiuriikin;
    mainVO.ACPTKEIGENNUKIKIN  := sumAcptkeigennukikin;
    /* 設定あり：受注 */
    -- 配達予定日がインボイス施行日後の場合、受注金額に受注一般税抜金額、受注非課税金額、受注不課税売上金額、受注軽減税抜金額の合計値を設定
    IF mainVO.DLVRYYTIDD >= wInvoicshikoymd THEN
      mainVO.ACPTKIN := mainVO.ACPTIPPANNUKIKIN
                      + mainVO.ACPTHIKAZEIURIIKIN
                      + mainVO.ACPTFUKAZEIURIIKIN
                      + mainVO.ACPTKEIGENNUKIKIN;          -- 受注金額
    END IF;
    -- 受注一般消費税金額
-- 本線対応変更 start ABI
--    mainVO.ACPTIPPANTAXKIN := S01SyohizeiService.SyohizeiKeisan('1',   --単体計算フラグ（する）
    mainVO.ACPTIPPANTAXKIN := Z1808B01.SyohizeiKeisan('1',   --単体計算フラグ（する）
-- 本線対応変更 end ABI
                                                                '0',   --税設定区分
                                                                '1',   --税区分
                                                mainVO.TAXKEISANKBN,   --税計算区分
                                                  mainVO.DLVRYYTIDD,   --切り替え日：配達予定日
                                                   mainVO.APKIHONCD,   --AP基本コード
                                                      mainVO.SHOPCD,   --ショップコード
                                                               NULL,   --仕入先コード
                                                               NULL,   --数量
                                            mainVO.ACPTIPPANNUKIKIN);  -- 受注一般税抜金額
    -- 受注軽減消費税金額
-- 本線対応変更 start ABI
--    mainVO.ACPTKEIGENTAXKIN := S01SyohizeiService.SyohizeiKeisan('1',  --単体計算フラグ（する）
    mainVO.ACPTKEIGENTAXKIN := Z1808B01.SyohizeiKeisan('1',  --単体計算フラグ（する）
-- 本線対応変更 end ABI
                                                                 '0',  --税設定区分
                                                                 '4',  --税区分
                                                 mainVO.TAXKEISANKBN,  --税計算区分
                                                   mainVO.DLVRYYTIDD,  --切り替え日：配達予定日
                                                    mainVO.APKIHONCD,  --AP基本コード
                                                       mainVO.SHOPCD,  --ショップコード
                                                                NULL,  --仕入先コード
                                                                NULL,  --数量
                                            mainVO.ACPTKEIGENNUKIKIN); -- 受注軽減税抜金額
    -- 配達予定日がインボイス施行日後の場合、受注消費税金額に受注一般税抜金額、受注軽減税抜金額の合計値を設定
    IF mainVO.DLVRYYTIDD >= wInvoicshikoymd THEN
      mainVO.ACPTSHOHITAXKIN := mainVO.ACPTIPPANTAXKIN
                              + mainVO.ACPTKEIGENTAXKIN;   -- 受注消費税金額
    END IF;
-- インボイス対応追加 end ABI

    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20108, SQLERRM || '金額計算処理時エラーが発生しました。');
  END kingakuCalc;

  /****************************************************************************/
  /* プロシージャ名：受注明細情報を取得                                       */
  /* 引数           ：wYoyakuGyoFlg      予約行存在フラグ                     */
  /****************************************************************************/
  PROCEDURE loadJuchuMeisai( wYoyakuGyoFlg       OUT VARCHAR2
  ) IS 
  /* 変数宣言 */
  meisaiVO meisaiVOtype; 
  rowcnt   NUMBER(3,0) := 0;  -- 行カウント
  haifucnt NUMBER(1,0) := 0;  -- 配布物カウント
  
  hansokuunt      NUMBER(10,2) DEFAULT NULL;
  tokuisakiunt    NUMBER(10,2) DEFAULT NULL;
  hansokuyusenkbn VARCHAR2(1)  DEFAULT NULL;
  wUrikinhasukeisankbn      VARCHAR2(1)  := NULL;  -- 売上金額端数計算区分
  tokuisakisetunt NUMBER(10,2) DEFAULT NULL;
  
  CURSOR SELECT_MEISAI IS
    SELECT
        JHMT.APKIHONCD
      , JHMT.SHOPCD
      , JHMT.ACPTDNPYNO
      , JHMT.EDANO
      , JHMT.ACPTDNPYMEISAINO
      , JHMT.ACPTDNPYMEISAIEDANO
      , JHMT.ITAKUAITEAPKIHONCD
      , JHMT.ITAKUAITESHOPCD
      , JHMT.ITAKUAITEACPTDNPYNO
      , JHMT.ITAKUAITEEDANO
      , JHMT.ITAKUAITEACPTDNPYMEISAINO
      , JHMT.ITAKUAITEACPTDNPYMEISAIEDANO
      , JHMT.LCTGRYCD
      , JHMT.MCTGRYCD
      , JHMT.SCTGRYCD
      , JHMT.SHNCD
      , JHMT.HAIFUBUTSUAPKIHONCD
      , JHMT.HAIFUBUTSUSHOPCD
      , JHMT.HAIFUBUTSUCD
      , JHMT.KATASIKI
      , JHMT.KEIJOKBN
      , JHMT.ACPTSURYO
      , JHMT.HYOJUNUNT
      , JHMT.ACPTUNT
      , JHMT.ACPTKIN
      , JHMT.ACPTUNTSHOHITAXKIN
      , JHMT.ACPTSHOHITAXKIN
      , JHMT.ITAKUCMPNYACPTUNT
      , JHMT.ITAKUCMPNYACPTUNTSHOHITAXKIN
      , JHMT.ITAKUCMPNYACPTSHOHITAXKIN
      , JHMT.NOHINSURYO
      , JHMT.NOHINUNT
      , JHMT.NOHINKIN
      , JHMT.NOHINUNTSHOHITAXKIN
      , JHMT.NOHINSHOHITAXKIN
      , JHMT.SOUKAISU
      , JHMT.ZANKAISU
-- インボイス対応変更 start ABI
--      , CASE WHEN SHMV.TAXKBN IN ('4','5') AND mainVO.DLVRYYTIDD <= '20190930' THEN '1' ELSE SHMV.TAXKBN END
      , SHMV.TAXKBN
-- インボイス対応変更 end ABI
      , JHMT.YUSYOKBN
      , JHMT.MEISAIKBN
      , JHMT.NYURYOKUMEISAIFLG
      , JHMT.TANPINMEISAIFLG
      , JHMT.STSHNCD
      , JHMT.MEISAIBIKO
      , JHMT.DATSKSMOTOKBN
      , JHMT.URIDNPYNO
      , JHMT.URIDNPYEDANO
      , JHMT.URIDNPYMEISAINO
      , JHMT.URIDNPYMEISAIEDANO
      , SHMV.SHNAPKIHONCD
      , CASE WHEN SHMV.HYOJUNPRICEKIRIKAEDD <= mainVO.DLVRYYTIDD THEN SHMV.SHINPRICEKBN
        ELSE SHMV.GENZPRICEKBN END PRICEKBN
      , SHMV.HYOJUNPRICEKIRIKAEDD
      , SHMV.GENZHYOJUNPRICE
      , SHMV.SHINHYOJUNPRICE
      , SHMV.GENZMOCHIKAERIPRICE
      , SHMV.SHINMOCHIKAERIPRICE
      , SHMV.OPNPRICEKIRIKAEDD
      , SHMV.GENZOPNPRICE
      , SHMV.SHINOPNPRICE
      , SHMV.GENZOPNMOCHIKAERIPRICE
      , SHMV.SHINOPNMOCHIKAERIPRICE
      , SHMV.HANSOKUKBN
      , SHMV.HANSOKUSTRTDD
      , SHMV.HANSOKUSHRYDD
      , SHMV.HANSOKUDLVRYPRICE
      , SHMV.HANSOKUMOCHIKAERIPRICE
      , SHMV.HANSOKUYUSENKBN
      , SHMV.ZAIKOUNT
      , SHMV.URIIRISU
      , SHMV.TAXSETTEIKBN
      , SHMV.ZAIKOARAMUSURYO
      , CASE WHEN TKSM.RKNKINDTOKUBETSUUNTFLG = '1' THEN TSTM.TEKIYOSTRTDD
        ELSE NULL END TEKIYOSTRTDD
      , CASE WHEN TKSM.RKNKINDTOKUBETSUUNTFLG = '1' THEN TSTM.TEKIYOSHRYDD
        ELSE NULL END TEKIYOSHRYDD
      , CASE WHEN TKSM.RKNKINDTOKUBETSUUNTFLG = '1' THEN TSTM.DLVRYPRICE
        ELSE NULL END DLVRYPRICE
      , CASE WHEN TKSM.RKNKINDTOKUBETSUUNTFLG = '1' THEN TSTM.MOCHIKAERIPRICE
        ELSE NULL END MOCHIKAERIPRICE
      , SHMV.ZAIKOKANRITRGTFLG
      , CASE WHEN TKSM.RKNKINDTOKUBETSUUNTFLG = '1' THEN TSSM.TEKIYOSTRTDD
        ELSE NULL END SET_TEKIYOSTRTDD
      , CASE WHEN TKSM.RKNKINDTOKUBETSUUNTFLG = '1' THEN TSSM.TEKIYOSHRYDD
        ELSE NULL END SET_TEKIYOSHRYDD
      , CASE WHEN TKSM.RKNKINDTOKUBETSUUNTFLG = '1' THEN TSSM.DLVRYPRICE
        ELSE NULL END SET_DLVRYPRICE
      , CASE WHEN TKSM.RKNKINDTOKUBETSUUNTFLG = '1' THEN TSSM.MOCHIKAERIPRICE
        ELSE NULL END SET_MOCHIKAERIPRICE
    FROM
      S04T002T JHMT 
    JOIN
      S04T001T JHTT
      ON (JHTT.APKIHONCD  = JHMT.APKIHONCD
      AND JHTT.SHOPCD     = JHMT.SHOPCD
      AND JHTT.ACPTDNPYNO = JHMT.ACPTDNPYNO
      AND JHTT.EDANO = JHMT.EDANO)
    LEFT OUTER JOIN
      S02V001T SHMV
      ON (SHMV.APKIHONCD = JHMT.APKIHONCD
      AND SHMV.SHOPCD    = JHMT.SHOPCD
      AND SHMV.SHNCD     = JHMT.SHNCD)
    LEFT OUTER JOIN
      S02M013T TKSM
      ON (TKSM.APKIHONCD   = JHTT.APKIHONCD
      AND TKSM.SHOPCD      = JHTT.SHOPCD
      AND TKSM.TOKUISAKICD = JHTT.TOKUISAKICD)
    LEFT OUTER JOIN
      S02M009T TSTM
      ON (TSTM.APKIHONCD   = SHMV.APKIHONCD
      AND TSTM.SHOPCD      = SHMV.SHOPCD
      AND TSTM.SHNCD       = SHMV.SHNCD
      AND TSTM.TOKUISAKICD = JHTT.TOKUISAKICD 
      AND mainVO.DLVRYYTIDD BETWEEN TSTM.TEKIYOSTRTDD AND TSTM.TEKIYOSHRYDD
      AND TSTM.YUKOKBN = '1')
    LEFT OUTER JOIN
      S02M066T TSSM
      ON (TSSM.APKIHONCD   = SHMV.APKIHONCD
      AND TSSM.SHOPCD      = SHMV.SHOPCD
      AND TSSM.STSHNCD     = SHMV.SHNCD
      AND TSSM.TOKUISAKICD = JHTT.TOKUISAKICD 
      AND mainVO.DLVRYYTIDD BETWEEN TSSM.TEKIYOSTRTDD AND TSSM.TEKIYOSHRYDD
      AND TSSM.YUKOKBN     = '1'
    )
    
    WHERE
          JHMT.APKIHONCD  = mainVO.APKIHONCD
      AND JHMT.SHOPCD     = mainVO.SHOPCD
      AND JHMT.ACPTDNPYNO = mainVO.ACPTDNPYNO
      AND JHMT.EDANO      = mainVO.EDANO
    ORDER BY 
      JHMT.ACPTDNPYMEISAINO, JHMT.ACPTDNPYMEISAIEDANO
    FOR UPDATE OF JHMT.ACPTDNPYNO
    ;
  
  BEGIN
    wYoyakuGyoFlg := '0';
    -- ショップマスタ取得 
    SELECT
      SHPM.URIKINHASUKEISANKBN  -- 売上金額端数計算区分
    INTO wUrikinhasukeisankbn
    FROM
      S02M046T SHPM 
    WHERE
      SHPM.APKIHONCD = mainVO.APKIHONCD 
      AND SHPM.SHOPCD = mainVO.SHOPCD  
      AND SHPM.YUKOKBN = '1'
    ;
  
    OPEN SELECT_MEISAI;
    LOOP FETCH SELECT_MEISAI INTO meisaiVO;
      
      EXIT WHEN SELECT_MEISAI%NOTFOUND; 
      
      --入力行のみ取り込む
      IF meisaiVO.NYURYOKUMEISAIFLG = '1' THEN
      
        IF meisaiVO.MEISAIKBN = c_MEISAI_HAIFU THEN
          IF haifucnt = 0 THEN
            mainVO.HAIFUBUTSUAPKIHONCD1 := meisaiVO.HAIFUBUTSUAPKIHONCD;
            mainVO.HAIFUBUTSUSHOPCD1    := meisaiVO.HAIFUBUTSUSHOPCD;
            mainVO.HAIFUBUTSUCD1        := meisaiVO.HAIFUBUTSUCD;
          ELSIF haifucnt = 1 THEN
            mainVO.HAIFUBUTSUAPKIHONCD2 := meisaiVO.HAIFUBUTSUAPKIHONCD;
            mainVO.HAIFUBUTSUSHOPCD2    := meisaiVO.HAIFUBUTSUSHOPCD;
            mainVO.HAIFUBUTSUCD2        := meisaiVO.HAIFUBUTSUCD;
          ELSIF haifucnt = 2 THEN
            mainVO.HAIFUBUTSUAPKIHONCD3 := meisaiVO.HAIFUBUTSUAPKIHONCD;
            mainVO.HAIFUBUTSUSHOPCD3    := meisaiVO.HAIFUBUTSUSHOPCD;
            mainVO.HAIFUBUTSUCD3        := meisaiVO.HAIFUBUTSUCD;
          ELSIF haifucnt = 3 THEN
            mainVO.HAIFUBUTSUAPKIHONCD4 := meisaiVO.HAIFUBUTSUAPKIHONCD;
            mainVO.HAIFUBUTSUSHOPCD4    := meisaiVO.HAIFUBUTSUSHOPCD;
            mainVO.HAIFUBUTSUCD4        := meisaiVO.HAIFUBUTSUCD;
          END IF;
          
          haifucnt := haifucnt + 1;
          
        ELSE
          rowcnt := rowcnt + 1;
          
          --新規＆予約行の場合、単価を再計算する
          IF vRecordS04W003T.JOTAIHENKOYOKYU = c_HENKOYOKYU_INS AND meisaiVO.DATSKSMOTOKBN = '03' THEN
          --価格区分が1：標準価格の場合のみ再セットする
          IF meisaiVO.PRICEKBN = '1' THEN
            --計上区分＝「売上」「キャンペーン」の場合
            IF meisaiVO.KEIJOKBN IN ('01', '02') THEN
              --販促単価
              IF (mainVO.DLVRYYTIDD BETWEEN meisaiVO.HANSOKUSTRTDD AND meisaiVO.HANSOKUSHRYDD) THEN
                
                IF mainVO.DLVRYKBN = c_DLVRY_MOCHIKAERI THEN
                  hansokuunt := meisaiVO.HANSOKUMOCHIKAERIPRICE;
                ELSE
                  hansokuunt := meisaiVO.HANSOKUPRICE;
                END IF;
              ELSE
                hansokuunt := NULL;
              END IF;
              --得意先別商品別単価
              IF (mainVO.DLVRYYTIDD BETWEEN meisaiVO.TTPRICESTRTDD AND meisaiVO.TTPRICESHRYDD) THEN
                
                IF mainVO.DLVRYKBN = c_DLVRY_MOCHIKAERI THEN
                  tokuisakiunt := meisaiVO.TTMOCHIKAERIPRICE;
                ELSE
                  tokuisakiunt := meisaiVO.TTPRICE;
                END IF;
              ELSE
                tokuisakiunt := NULL;
              END IF;
              
              --得意先別セット商品別単価
              IF (mainVO.DLVRYYTIDD BETWEEN meisaiVO.SETTTPRICESTRTDD AND meisaiVO.SETTTPRICESHRYDD) THEN
                IF mainVO.DLVRYKBN = c_DLVRY_MOCHIKAERI THEN
                  tokuisakisetunt := meisaiVO.SETTTMOCHIKAERIPRICE;
                ELSE
                  tokuisakisetunt := meisaiVO.SETTTPRICE;
                END IF;
              ELSE
                tokuisakisetunt := NULL;
              END IF;
              
              --販促優先区分を取得
              SELECT HANSOKUYUSENKBN
                INTO hansokuyusenkbn
                FROM S02V001T
               WHERE APKIHONCD = mainVO.APKIHONCD
                 AND SHOPCD    = mainVO.SHOPCD
                 AND SHNCD     = meisaiVO.SHNCD
              ;
              
              IF tokuisakiunt IS NOT NULL THEN
                meisaiVO.KEIJOKBN := '01';
                meisaiVO.ACPTUNT  := tokuisakiunt;
              ELSIF tokuisakisetunt IS NOT NULL THEN
                meisaiVO.KEIJOKBN := '01';
                meisaiVO.ACPTUNT  := tokuisakisetunt;
              ELSIF hansokuunt IS NOT NULL AND hansokuyusenkbn = '1' THEN
                meisaiVO.KEIJOKBN := '02';
                meisaiVO.ACPTUNT := hansokuunt;
              ELSE
                IF mainVO.DLVRYKBN = c_DLVRY_MOCHIKAERI THEN
                  meisaiVO.ACPTUNT := getCurrentValue(meisaiVO.GENZHYOJUNMOCHIKAERIPRICE, meisaiVO.SHINHYOJUNMOCHIKAERIPRICE, meisaiVO.HYOJUNPRICEKIRIKAEDD, mainVO.DLVRYYTIDD);
                ELSE
                  meisaiVO.ACPTUNT := getCurrentValue(meisaiVO.GENZHYOJUNPRICE, meisaiVO.SHINHYOJUNPRICE, meisaiVO.HYOJUNPRICEKIRIKAEDD, mainVO.DLVRYYTIDD);
                END IF;
              END IF;
            ELSE
              meisaiVO.ACPTUNT := 0;
            END IF;
            END IF;
            /* 消費税、金額計算部品 */
            IF meisaiVO.TAXSETTEIKBN = '1' THEN
              getPriceList(meisaiVO.ACPTUNT, meisaiVO.ACPTSURYO, meisaiVO.TAXKBN, meisaiVO.TAXSETTEIKBN , meisaiVO.SHNCD, meisaiVO.KEIJOKBN,
                           meisaiVO.ACPTUNTSHOHITAXKIN, meisaiVO.ACPTSHOHITAXKIN, meisaiVO.ACPTKIN);
            ELSE
              meisaiVO.ACPTUNTSHOHITAXKIN := 0;
              meisaiVO.ACPTSHOHITAXKIN := 0;
-- 本線対応変更 start ABI
--              meisaiVO.ACPTKIN := S01SyohizeiService.roundValue(meisaiVO.ACPTUNT * meisaiVO.ACPTSURYO, wUrikinhasukeisankbn);
              meisaiVO.ACPTKIN := Z1808B01.roundValue(meisaiVO.ACPTUNT * meisaiVO.ACPTSURYO, wUrikinhasukeisankbn);
-- 本線対応変更 end ABI
            END IF;
            
          END IF;
          
          --明細リストに追加
          meisaiList(rowcnt) := meisaiVO;
          
          IF meisaiVO.DATSKSMOTOKBN = '03' THEN
            --予約行が存在
            wYoyakuGyoFlg := '1';
          END IF;
        END IF;
      END IF;
    
    END LOOP;
    CLOSE SELECT_MEISAI;
    
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20109, SQLERRM || '受注明細情報取得時エラーが発生しました。');
  END loadJuchuMeisai;

  /****************************************************************************/
  /* プロシージャ名：得意先配達情報を取得                                     */
  /* 引数           ：iApkihoncd            AP基本コード                      */
  /*                ：iShopcd               ショップコード                    */
  /*                ：iTokuisakicd          得意先コード                      */
  /*                ：ioDenVO               伝票VO                            */
  /*                ：iSetteiFg             設定フラグ                        */
  /****************************************************************************/
  PROCEDURE loadTokuisakiHaitatsuInfo( iApkihoncd     IN VARCHAR2
                                     , iShopcd        IN VARCHAR2
                                     , iTokuisakicd   IN VARCHAR2
                                     , iYoyakuFlg     IN VARCHAR2
                                     , ioDenVO        IN OUT denVOtype
                                     , iSetteiFg      IN BOOLEAN
  ) IS 
  /* 得意先配達情報VO */
  TYPE tdVOtype IS RECORD (
                         APKIHONCD                 VARCHAR2(3) -- AP基本コード
                       , SHOPCD                    VARCHAR2(3) -- ショップコード
                       , TOKUISAKICD               VARCHAR2(6)
                       , TOKUISAKIJOTAI            VARCHAR2(1)
                       , JOTAISTS                  VARCHAR2(2)
                       , TOKUISAKIKBN              VARCHAR2(1)
                       , ACPTTEISHIFLG             VARCHAR2(1)
                       , DLVRYKANKATSUAPKIHONCD    VARCHAR2(3)
                       , DLVRYKANKATSUSHOPCD       VARCHAR2(3)
                       , DLVRYKYOTENCD             VARCHAR2(2)
                       , DLVRYAREACD               VARCHAR2(5)
                       , DLVRYKBN                  VARCHAR2(1)
                       , DLVRYCMPNYCD              VARCHAR2(3)
                       , DLVRYTNSHACD              VARCHAR2(6)
                       , DLVRYJUN                  VARCHAR2(7)
                       , FZAIOKIKANOFLG            VARCHAR2(1)
                       , DLVRYKIBOJIKAN            VARCHAR2(4)
                       , DLVRYKIBOJIKANSHRY        VARCHAR2(4)
                       , DLVRYKIBOJIKANKBN         VARCHAR2(1)
                       , PRNTMEMON1                VARCHAR2(80)
                       , PRNTMEMON2                VARCHAR2(80)
                       , PRNTMEMON3                VARCHAR2(80)
                       , SKYSAKICD                 VARCHAR2(6)
                       , JUTAKUDLVRYCSTKBN         VARCHAR2(1)
                       , ITAKUAITEAPKIHONCD        VARCHAR2(3)
                       , ITAKUAITESHOPCD           VARCHAR2(3)
                       , ITAKUAITETOKUISAKICD      VARCHAR2(6)
                       , AZUKARIBTLN1N2RITTORUHNS  NUMBER(7,0)
                       , AZUKARIBTLN8RITTORUHNS    NUMBER(7,0)
                       , ZNKDLVRYDD                VARCHAR2(8)
                       , YOSHINGENDOKIN            NUMBER(11,0)
                       , GENZAIZD                  NUMBER(8,0)
                       , KEIJOMAEACPTKIN           NUMBER(11,0)
                       , SAISHUACPTDD              VARCHAR2(8)
                       , KYUMINKBN                 VARCHAR2(1)
                       , KAISHUHOHOKBN             VARCHAR2(1)
  );
  
  tdVO tdVOtype;
  
  BEGIN
  
    BEGIN
      SELECT
         TKSM.APKIHONCD
       , TKSM.SHOPCD
       , TKSM.TOKUISAKICD
       , TKSM.TOKUISAKIJOTAI
       , TKSM.JOTAISTS
       , TKSM.TOKUISAKIKBN
       , TKSM.ACPTTEISHIFLG
       , TKSM.DLVRYKANKATSUAPKIHONCD
       , TKSM.DLVRYKANKATSUSHOPCD
       , TKSM.DLVRYKANKATSUKYOTENCD
       , TKSM.DLVRYAREACD
       , TKSM.DLVRYKBN
       , TKSM.DLVRYCMPNYCD
       , TKSM.DLVRYTNSHACD
       , TKSM.DLVRYJUN
       , TKSM.FZAIOKIKANOFLG
       , TKSM.ACPTJIKANSHITEIJIKOKU
       , TKSM.ACPTJIKANSHITEIJIKOKUSHRY
       , TKSM.ACPTJIKANSHITEIKBN
       , TKSM.PRNTMEMON1
       , TKSM.PRNTMEMON2
       , TKSM.PRNTMEMON3
       , TKSM.SKYSAKICD
       , TKSM.JUTAKUDLVRYCSTKBN
       , TKSM.ITAKUAITEAPKIHONCD
       , TKSM.ITAKUAITESHOPCD
       , TKSM.ITAKUAITETOKUISAKICD
       , TJST.AZUKARIBTLN1N2RITTORUHNS
       , TJST.AZUKARIBTLN8RITTORUHNS
       , TJST.ZNKDLVRYDD
       , TKSM.YOSHINGENDOKIN
       , TJST.GENZAIZD
       , TJST.KEIJOMAEACPTKIN
       , TJST.SAISHUACPTDD
       , TKSM.KYUMINKBN
       , SKSM.KAISHUHOHOKBN
      INTO
       tdVO
      FROM
       S02M013T TKSM
      LEFT OUTER JOIN
       S02M050T TJST
        ON TJST.APKIHONCD   = TKSM.APKIHONCD
       AND TJST.SHOPCD      = TKSM.SHOPCD
       AND TJST.TOKUISAKICD = TKSM.TOKUISAKICD
      LEFT OUTER JOIN
       S02M017T SKSM
        ON SKSM.APKIHONCD   = TKSM.APKIHONCD
       AND SKSM.SHOPCD      = TKSM.SHOPCD
       AND SKSM.SKYSAKICD   = TKSM.SKYSAKICD
      WHERE
           TKSM.APKIHONCD   = iApkihoncd
       AND TKSM.SHOPCD      = iShopcd
       AND TKSM.TOKUISAKICD = iTokuisakicd
       AND TKSM.YUKOKBN     = '1'
     ;
    
    EXCEPTION 
      WHEN NO_DATA_FOUND THEN 
        --得意先が存在しない
        ErrAdd(c_ERRKBN_ERR, 'EJ008:得意先コードが無効です。');
        RETURN;
    END;
    
    --得意先情報を伝票VOに設定
    IF vRecordS04W003T.JOTAIHENKOYOKYU = c_HENKOYOKYU_INS AND iSetteiFg = true THEN
      --配達管轄AP基本コード
      ioDenVO.DLVRYKANKATSUAPKIHONCD := tdVO.DLVRYKANKATSUAPKIHONCD;
      --配達管轄ショップコード
      ioDenVO.DLVRYKANKATSUSHOPCD    := tdVO.DLVRYKANKATSUSHOPCD;
      --配達拠点コード
      ioDenVO.DLVRYKYOTENCD          := tdVO.DLVRYKYOTENCD;
      --配達担当者コード
      ioDenVO.DLVRYTNSHACD           := tdVO.DLVRYTNSHACD;
      --配達エリアコード
      ioDenVO.DLVRYAREACD            := tdVO.DLVRYAREACD;
      --配達区分
      ioDenVO.DLVRYKBN               := tdVO.DLVRYKBN;
      IF iYoyakuFlg = '0' THEN
        --印刷メモ
        ioDenVO.PRNTMEMON1             := tdVO.PRNTMEMON1;
        ioDenVO.PRNTMEMON2             := tdVO.PRNTMEMON2;
      END IF;
      --配達会社コード
      ioDenVO.DLVRYCMPNYCD           := tdVO.DLVRYCMPNYCD;
      --配達順
      ioDenVO.DLVRYJUN               := tdVO.DLVRYJUN;
      --受注引当先AP基本コード
      ioDenVO.ACPTHIKIATESAKIAPKIHONCD := tdVO.DLVRYKANKATSUAPKIHONCD;
      --受注引当先ショップコード
      ioDenVO.ACPTHIKIATESAKISHOPCD := tdVO.DLVRYKANKATSUSHOPCD;
      --受注引当先拠点コード
      ioDenVO.ACPTHIKIATESAKIKYOTENCD := tdVO.DLVRYKYOTENCD;
      --請求先コード
      ioDenVO.SKYSAKICD              := tdVO.SKYSAKICD;
      
      --入金票印刷対象フラグ
      ioDenVO.NYKNHYOPRNTTRGTFLG := CASE WHEN tdVO.KAISHUHOHOKBN = '7' THEN '1' ELSE '0' END; --代引きの場合は"1"を設定
      --受注時代引フラグ
      ioDenVO.ACPTTHDAIBIKIFLG := CASE WHEN tdVO.KAISHUHOHOKBN = '7' THEN '1' ELSE '0' END; --代引きの場合は"1"を設定
    END IF;
    --受託配達顧客区分
    ioDenVO.JUTAKUDLVRYCSTKBN := tdVO.JUTAKUDLVRYCSTKBN;
    --委託相手AP基本コード
    ioDenVO.ITAKUAITEAPKIHONCD := tdVO.ITAKUAITEAPKIHONCD;
    --委託相手ショップコード
    ioDenVO.ITAKUAITESHOPCD := tdVO.ITAKUAITESHOPCD;
    --委託相手得意先コード
    ioDenVO.ITAKUAITETOKUISAKICD := tdVO.ITAKUAITETOKUISAKICD;
      
    ioDenVO.TOKUISAKIKBN             := tdVO.TOKUISAKIKBN;
    ioDenVO.TOKUISAKIJOTAI           := tdVO.TOKUISAKIJOTAI;
    ioDenVO.JOTAISTS                 := tdVO.JOTAISTS;
    IF iSetteiFg = true THEN
      --得意先実績テーブルの値を伝票VOに設定する
      ioDenVO.AZUKARIBTLN1N2RITTORUHNS := tdVO.AZUKARIBTLN1N2RITTORUHNS;
      ioDenVO.AZUKARIBTLN8RITTORUHNS   := tdVO.AZUKARIBTLN8RITTORUHNS;
      ioDenVO.ZNKDLVRYDD               := tdVO.ZNKDLVRYDD;
    END IF;
    ioDenVO.YOSHINGENDOKIN           := tdVO.YOSHINGENDOKIN;
    ioDenVO.GENZAIZD                 := tdVO.GENZAIZD;
    ioDenVO.KEIJOMAEACPTKIN          := tdVO.KEIJOMAEACPTKIN;
    ioDenVO.SAISHUACPTDD             := tdVO.SAISHUACPTDD;
    ioDenVO.KYUMINKBN                := tdVO.KYUMINKBN;
    ioDenVO.KAISHUHOHOKBN            := tdVO.KAISHUHOHOKBN;

    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20110, SQLERRM || '得意先情報取得時エラーが発生しました。');
  END loadTokuisakiHaitatsuInfo;
  
  /****************************************************************************/
  /* プロシージャ名：配布物情報を取得                                         */
  /****************************************************************************/
  PROCEDURE getTokuisakiHaifubutsu
  IS 
  /* 変数宣言 */
  haifucnt          NUMBER(1,0) := 0;  -- 配布物カウント
  vRecordS02M033T   S02M033T%rowtype;  -- 配布物企画対象得意先マスタ
  
  CURSOR SELECT_HAIFU IS
    SELECT
      HTTM.APKIHONCD
     ,HTTM.SHOPCD
     ,HTTM.HAIFUBUTSUCD
    FROM
      S02M033T HTTM
    LEFT OUTER JOIN
      S02M035T HHBM
     ON (HHBM.APKIHONCD = HTTM.APKIHONCD
     AND HHBM.SHOPCD = HTTM.SHOPCD
     AND HHBM.HAIFUBUTSUCD = HTTM.HAIFUBUTSUCD)
    WHERE
         HTTM.APKIHONCD    = mainVO.APKIHONCD
     AND HTTM.SHOPCD       = mainVO.SHOPCD
     AND HTTM.TOKUISAKICD  = mainVO.TOKUISAKICD
     AND mainVO.DLVRYYTIDD BETWEEN HTTM.HAIFUDKIKANSTRTDD AND HTTM.HAIFUDKIKANSHRYDD
     AND HTTM.HAIFUTRGTSTS = '1'
     AND HTTM.HAIFUCLFLG   = '0' 
     AND HTTM.YUKOKBN      = '1'
     AND HHBM.YUKOKBN      = '1'
     AND HHBM.ACPTGAMENDSPLYKBN = '1'
    ORDER BY
      HTTM.HAIFUBUTSUCD
    ;
    
  BEGIN
  
    OPEN SELECT_HAIFU;
    LOOP FETCH SELECT_HAIFU INTO vRecordS02M033T.APKIHONCD
                                ,vRecordS02M033T.SHOPCD
                                ,vRecordS02M033T.HAIFUBUTSUCD;
      
      EXIT WHEN SELECT_HAIFU%NOTFOUND; 
      
      --配布物情報を設定
      IF haifucnt = 0 THEN
        mainVO.HAIFUBUTSUAPKIHONCD1 := vRecordS02M033T.APKIHONCD;
        mainVO.HAIFUBUTSUSHOPCD1    := vRecordS02M033T.SHOPCD;
        mainVO.HAIFUBUTSUCD1        := vRecordS02M033T.HAIFUBUTSUCD;
      ELSIF haifucnt = 1 THEN
        mainVO.HAIFUBUTSUAPKIHONCD2 := vRecordS02M033T.APKIHONCD;
        mainVO.HAIFUBUTSUSHOPCD2    := vRecordS02M033T.SHOPCD;
        mainVO.HAIFUBUTSUCD2        := vRecordS02M033T.HAIFUBUTSUCD;
      ELSIF haifucnt = 2 THEN
        mainVO.HAIFUBUTSUAPKIHONCD3 := vRecordS02M033T.APKIHONCD;
        mainVO.HAIFUBUTSUSHOPCD3    := vRecordS02M033T.SHOPCD;
        mainVO.HAIFUBUTSUCD3        := vRecordS02M033T.HAIFUBUTSUCD;
      ELSIF haifucnt = 3 THEN
        mainVO.HAIFUBUTSUAPKIHONCD4 := vRecordS02M033T.APKIHONCD;
        mainVO.HAIFUBUTSUSHOPCD4    := vRecordS02M033T.SHOPCD;
        mainVO.HAIFUBUTSUCD4        := vRecordS02M033T.HAIFUBUTSUCD;
      END IF;
      
      haifucnt := haifucnt + 1;
      
    END LOOP;
    CLOSE SELECT_HAIFU;
    
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20111, SQLERRM || '配布物情報取得時エラーが発生しました。');
  END getTokuisakiHaifubutsu;
  
  /****************************************************************************/
  /* プロシージャ名：ワークから伝票VOに値を設定                               */
  /****************************************************************************/
  PROCEDURE createJuchuHead
  IS 
  BEGIN
    --受注担当者
    mainVO.ACPTTNSHAAPKIHONCD  := vRecordS04W003T.ACPTTNSHAAPKIHONCD;
    mainVO.ACPTTNSHASHOPCD     := vRecordS04W003T.ACPTTNSHASHOPCD;
    mainVO.ACPTTNSHACD         := vRecordS04W003T.ACPTTNSHACD;
    --配達予定日
    mainVO.DLVRYYTIDD          := vRecordS04W003T.DLVRYYTIDD;
    --配達日
    mainVO.DLVRYDD             := vRecordS04W003T.DLVRYYTIDD;
    --配達希望時間
    mainVO.DLVRYKIBOJIKAN      := vRecordS04W003T.DLVRYKIBOJIKANFROM;
    mainVO.DLVRYKIBOJIKANSHRY  := vRecordS04W003T.DLVRYKIBOJIKANTO;
    mainVO.DLVRYKIBOJIKANKBN   := vRecordS04W003T.DLVRYKIBOJIKANKBN;
    --不在置き可能フラグ
    mainVO.FZAIOKIKANOFLG      := vRecordS04W003T.FZAIOKIKANOFLG;
    --回収予定ボトル１２L本数
    mainVO.KYBTLN1N2RITTORUHNS := vRecordS04W003T.KYBTLN1N2RITTORUHNS;
    --回収予定ボトル８L本数
    mainVO.KYBTLN8RITTORUHNS   := vRecordS04W003T.KYBTLN8RITTORUHNS;
    --印刷メモ３
    mainVO.PRNTMEMON3          := vRecordS04W003T.PRNTMEMON3;
    --作成元管理区分
    mainVO.SKSMOTOKANRIKBN     := vRecordS04W003T.SKSMOTOKANRIKBN;
    --作成元管理番号
    mainVO.SKSMOTOKANRINO      := vRecordS04W003T.SKSMOTOKANRINO;
    --受注受付経路区分
    mainVO.ACPTRCPTKEIROKBN    := vRecordS04W003T.ACPTRCPTKEIROKBN;
  
  END createJuchuHead;
  
  /****************************************************************************/
  /* プロシージャ名：ワークから明細情報を作成                                 */
  /****************************************************************************/
  PROCEDURE createJuchuMeisai
  IS 
  /* 変数宣言 */
  meisaiVO meisaiVOtype; 
  initVO meisaiVOtype;
  
  wShncd          S04W003T.SHNCDN1%TYPE;
  wKatashiki      S04W003T.KATASIKIN1%TYPE;
  wKeijokbn       S04W003T.KEIJOKBNN1%TYPE;
  wSuryo          S04W003T.ACPTSURYON1%TYPE;
  wUnt            S04W003T.ACPTUNTN1%TYPE;
  wMeisaibiko     S04W003T.MEISAIBIKON1%TYPE;
  wDatsksmotokbn  S04W003T.DATSKSMOTOKBNN1%TYPE;
  wUrikinhasukeisankbn      VARCHAR2(1)  := NULL;  -- 売上金額端数計算区分
  
  BEGIN
    -- ショップマスタ取得 
    SELECT
      SHPM.URIKINHASUKEISANKBN  -- 売上金額端数計算区分
    INTO wUrikinhasukeisankbn
    FROM
      S02M046T SHPM 
    WHERE
      SHPM.APKIHONCD = vRecordS04W003T.APKIHONCD 
      AND SHPM.SHOPCD = vRecordS04W003T.SHOPCD  
      AND SHPM.YUKOKBN = '1'
    ;
    
    FOR i IN 1..12 LOOP
      --明細情報をセット
      CASE i 
        WHEN 1 THEN
          wShncd         := vRecordS04W003T.SHNCDN1;
          wKatashiki     := vRecordS04W003T.KATASIKIN1;
          wKeijokbn      := vRecordS04W003T.KEIJOKBNN1;
          wSuryo         := vRecordS04W003T.ACPTSURYON1;
          wUnt           := vRecordS04W003T.ACPTUNTN1;
          wMeisaibiko    := vRecordS04W003T.MEISAIBIKON1;
          wDatsksmotokbn := vRecordS04W003T.DATSKSMOTOKBNN1;
        WHEN 2 THEN
          wShncd         := vRecordS04W003T.SHNCDN2;
          wKatashiki     := vRecordS04W003T.KATASIKIN2;
          wKeijokbn      := vRecordS04W003T.KEIJOKBNN2;
          wSuryo         := vRecordS04W003T.ACPTSURYON2;
          wUnt           := vRecordS04W003T.ACPTUNTN2;
          wMeisaibiko    := vRecordS04W003T.MEISAIBIKON2;
          wDatsksmotokbn := vRecordS04W003T.DATSKSMOTOKBNN2;
        WHEN 3 THEN
          wShncd         := vRecordS04W003T.SHNCDN3;
          wKatashiki     := vRecordS04W003T.KATASIKIN3;
          wKeijokbn      := vRecordS04W003T.KEIJOKBNN3;
          wSuryo         := vRecordS04W003T.ACPTSURYON3;
          wUnt           := vRecordS04W003T.ACPTUNTN3;
          wMeisaibiko    := vRecordS04W003T.MEISAIBIKON3;
          wDatsksmotokbn := vRecordS04W003T.DATSKSMOTOKBNN3;
        WHEN 4 THEN
          wShncd         := vRecordS04W003T.SHNCDN4;
          wKatashiki     := vRecordS04W003T.KATASIKIN4;
          wKeijokbn      := vRecordS04W003T.KEIJOKBNN4;
          wSuryo         := vRecordS04W003T.ACPTSURYON4;
          wUnt           := vRecordS04W003T.ACPTUNTN4;
          wMeisaibiko    := vRecordS04W003T.MEISAIBIKON4;
          wDatsksmotokbn := vRecordS04W003T.DATSKSMOTOKBNN4;
        WHEN 5 THEN
          wShncd         := vRecordS04W003T.SHNCDN5;
          wKatashiki     := vRecordS04W003T.KATASIKIN5;
          wKeijokbn      := vRecordS04W003T.KEIJOKBNN5;
          wSuryo         := vRecordS04W003T.ACPTSURYON5;
          wUnt           := vRecordS04W003T.ACPTUNTN5;
          wMeisaibiko    := vRecordS04W003T.MEISAIBIKON5;
          wDatsksmotokbn := vRecordS04W003T.DATSKSMOTOKBNN5;
        WHEN 6 THEN
          wShncd         := vRecordS04W003T.SHNCDN6;
          wKatashiki     := vRecordS04W003T.KATASIKIN6;
          wKeijokbn      := vRecordS04W003T.KEIJOKBNN6;
          wSuryo         := vRecordS04W003T.ACPTSURYON6;
          wUnt           := vRecordS04W003T.ACPTUNTN6;
          wMeisaibiko    := vRecordS04W003T.MEISAIBIKON6;
          wDatsksmotokbn := vRecordS04W003T.DATSKSMOTOKBNN6;
        WHEN 7 THEN
          wShncd         := vRecordS04W003T.SHNCDN7;
          wKatashiki     := vRecordS04W003T.KATASIKIN7;
          wKeijokbn      := vRecordS04W003T.KEIJOKBNN7;
          wSuryo         := vRecordS04W003T.ACPTSURYON7;
          wUnt           := vRecordS04W003T.ACPTUNTN7;
          wMeisaibiko    := vRecordS04W003T.MEISAIBIKON7;
          wDatsksmotokbn := vRecordS04W003T.DATSKSMOTOKBNN7;
        WHEN 8 THEN
          wShncd         := vRecordS04W003T.SHNCDN8;
          wKatashiki     := vRecordS04W003T.KATASIKIN8;
          wKeijokbn      := vRecordS04W003T.KEIJOKBNN8;
          wSuryo         := vRecordS04W003T.ACPTSURYON8;
          wUnt           := vRecordS04W003T.ACPTUNTN8;
          wMeisaibiko    := vRecordS04W003T.MEISAIBIKON8;
          wDatsksmotokbn := vRecordS04W003T.DATSKSMOTOKBNN8;
        WHEN 9 THEN
          wShncd         := vRecordS04W003T.SHNCDN9;
          wKatashiki     := vRecordS04W003T.KATASIKIN9;
          wKeijokbn      := vRecordS04W003T.KEIJOKBNN9;
          wSuryo         := vRecordS04W003T.ACPTSURYON9;
          wUnt           := vRecordS04W003T.ACPTUNTN9;
          wMeisaibiko    := vRecordS04W003T.MEISAIBIKON9;
          wDatsksmotokbn := vRecordS04W003T.DATSKSMOTOKBNN9;
        WHEN 10 THEN
          wShncd         := vRecordS04W003T.SHNCDN10;
          wKatashiki     := vRecordS04W003T.KATASIKIN10;
          wKeijokbn      := vRecordS04W003T.KEIJOKBNN10;
          wSuryo         := vRecordS04W003T.ACPTSURYON10;
          wUnt           := vRecordS04W003T.ACPTUNTN10;
          wMeisaibiko    := vRecordS04W003T.MEISAIBIKON10;
          wDatsksmotokbn := vRecordS04W003T.DATSKSMOTOKBNN10;
        WHEN 11 THEN
          wShncd         := vRecordS04W003T.SHNCDN11;
          wKatashiki     := vRecordS04W003T.KATASIKIN11;
          wKeijokbn      := vRecordS04W003T.KEIJOKBNN11;
          wSuryo         := vRecordS04W003T.ACPTSURYON11;
          wUnt           := vRecordS04W003T.ACPTUNTN11;
          wMeisaibiko    := vRecordS04W003T.MEISAIBIKON11;
          wDatsksmotokbn := vRecordS04W003T.DATSKSMOTOKBNN11;
        WHEN 12 THEN
          wShncd         := vRecordS04W003T.SHNCDN12;
          wKatashiki     := vRecordS04W003T.KATASIKIN12;
          wKeijokbn      := vRecordS04W003T.KEIJOKBNN12;
          wSuryo         := vRecordS04W003T.ACPTSURYON12;
          wUnt           := vRecordS04W003T.ACPTUNTN12;
          wMeisaibiko    := vRecordS04W003T.MEISAIBIKON12;
          wDatsksmotokbn := vRecordS04W003T.DATSKSMOTOKBNN12;
        ELSE
          NULL;
      END CASE;

      IF wShncd IS NOT NULL THEN
        meisaiVO.APKIHONCD := vRecordS04W003T.APKIHONCD;
        meisaiVO.SHOPCD := vRecordS04W003T.SHOPCD;
        meisaiVO.ACPTDNPYMEISAINO := meisaiList.COUNT + 1;
        meisaiVO.ACPTDNPYMEISAIEDANO := 0;

        --商品情報を取得
        SELECT SHN.LCTGRYCD
             , SHN.MCTGRYCD
             , SHN.SCTGRYCD
             , SHN.HYOJUNPRICEKIRIKAEDD
             , SHN.GENZHYOJUNPRICE
             , SHN.SHINHYOJUNPRICE
             , SHN.GENZMOCHIKAERIPRICE
             , SHN.SHINMOCHIKAERIPRICE
             , CASE WHEN SHN.TAXSETTEIKBN = '1' THEN 
-- インボイス対応変更 start ABI
--                 CASE WHEN SHN.TAXKBN IN ('4','5') AND vRecordS04W003T.DLVRYYTIDD <= '20190930' THEN '1' ELSE SHN.TAXKBN END
                 SHN.TAXKBN
-- インボイス対応変更 end ABI
               ELSE 
                 NULL 
               END TAXKBN
             , SHN.HANSOKUKBN
             , SHN.HANSOKUSTRTDD
             , SHN.HANSOKUSHRYDD
             , SHN.HANSOKUDLVRYPRICE
             , SHN.HANSOKUMOCHIKAERIPRICE
             , CASE WHEN SHN.HYOJUNPRICEKIRIKAEDD <= vRecordS04W003T.DLVRYYTIDD THEN SHN.SHINPRICEKBN
               ELSE SHN.GENZPRICEKBN END PRICEKBN
             , SHN.GENZOPNPRICE
             , SHN.SHINOPNPRICE
             , SHN.TAXSETTEIKBN
             , SHN.ZAIKOARAMUSURYO
             , SHN.GENZOPNMOCHIKAERIPRICE
             , SHN.SHINOPNMOCHIKAERIPRICE
             , CASE WHEN TKSM.RKNKINDTOKUBETSUUNTFLG = '1' THEN TSTM.TEKIYOSTRTDD
               ELSE NULL END TEKIYOSTRTDD
             , CASE WHEN TKSM.RKNKINDTOKUBETSUUNTFLG = '1' THEN TSTM.TEKIYOSHRYDD
               ELSE NULL END TEKIYOSHRYDD
             , CASE WHEN TKSM.RKNKINDTOKUBETSUUNTFLG = '1' THEN TSTM.DLVRYPRICE
               ELSE NULL END DLVRYPRICE
             , CASE WHEN TKSM.RKNKINDTOKUBETSUUNTFLG = '1' THEN TSTM.MOCHIKAERIPRICE
               ELSE NULL END MOCHIKAERIPRICE
             , SHN.ZAIKOKANRITRGTFLG
             , CASE WHEN SHN.STKBN = '1' THEN '0'
               ELSE '1' END TANPINMEISAIFLG
             , CASE WHEN TKSM.RKNKINDTOKUBETSUUNTFLG = '1' THEN TSSM.TEKIYOSTRTDD
               ELSE NULL END SET_TEKIYOSTRTDD
             , CASE WHEN TKSM.RKNKINDTOKUBETSUUNTFLG = '1' THEN TSSM.TEKIYOSHRYDD
               ELSE NULL END SET_TEKIYOSHRYDD
             , CASE WHEN TKSM.RKNKINDTOKUBETSUUNTFLG = '1' THEN TSSM.DLVRYPRICE
               ELSE NULL END SET_DLVRYPRICE
             , CASE WHEN TKSM.RKNKINDTOKUBETSUUNTFLG = '1' THEN TSSM.MOCHIKAERIPRICE
               ELSE NULL END SET_MOCHIKAERIPRICE
          INTO meisaiVO.LCTGRYCD
             , meisaiVO.MCTGRYCD
             , meisaiVO.SCTGRYCD
             , meisaiVO.HYOJUNPRICEKIRIKAEDD
             , meisaiVO.GENZHYOJUNPRICE
             , meisaiVO.SHINHYOJUNPRICE
             , meisaiVO.GENZHYOJUNMOCHIKAERIPRICE
             , meisaiVO.SHINHYOJUNMOCHIKAERIPRICE
             , meisaiVO.TAXKBN
             , meisaiVO.HANSOKUKBN
             , meisaiVO.HANSOKUSTRTDD
             , meisaiVO.HANSOKUSHRYDD
             , meisaiVO.HANSOKUPRICE
             , meisaiVO.HANSOKUMOCHIKAERIPRICE
             , meisaiVO.PRICEKBN
             , meisaiVO.GENZSHOPPRICE
             , meisaiVO.SHINSHOPPRICE
             , meisaiVO.TAXSETTEIKBN
             , meisaiVO.ZAIKOARAMUSURYO
             , meisaiVO.GENZSHOPMOCHIKAERIPRICE
             , meisaiVO.SHINSHOPMOCHIKAERIPRICE
             , meisaiVO.TTPRICESTRTDD
             , meisaiVO.TTPRICESHRYDD
             , meisaiVO.TTPRICE
             , meisaiVO.TTMOCHIKAERIPRICE
             , meisaiVO.ZAIKOKANRITRGTFLG
             , meisaiVO.TANPINMEISAIFLG
             , meisaiVO.SETTTPRICESTRTDD
             , meisaiVO.SETTTPRICESHRYDD
             , meisaiVO.SETTTPRICE
             , meisaiVO.SETTTMOCHIKAERIPRICE
             
        FROM S02V001T SHN
        LEFT OUTER JOIN S02M009T TSTM
          ON TSTM.APKIHONCD   = SHN.APKIHONCD
         AND TSTM.SHOPCD      = SHN.SHOPCD
         AND TSTM.SHNCD       = SHN.SHNCD
         AND TSTM.TOKUISAKICD = vRecordS04W003T.TOKUISAKICD 
         AND vRecordS04W003T.DLVRYYTIDD BETWEEN TSTM.TEKIYOSTRTDD AND TSTM.TEKIYOSHRYDD
         AND TSTM.YUKOKBN = '1'
        LEFT OUTER JOIN S02M066T TSSM
          ON TSSM.APKIHONCD   = SHN.APKIHONCD
         AND TSSM.SHOPCD      = SHN.SHOPCD
         AND TSSM.STSHNCD     = SHN.SHNCD
         AND TSSM.TOKUISAKICD = vRecordS04W003T.TOKUISAKICD 
         AND vRecordS04W003T.DLVRYYTIDD BETWEEN TSSM.TEKIYOSTRTDD AND TSSM.TEKIYOSHRYDD
         AND TSSM.YUKOKBN     = '1'
        LEFT OUTER JOIN S02M013T TKSM
          ON TKSM.APKIHONCD   = SHN.APKIHONCD
         AND TKSM.SHOPCD      = SHN.SHOPCD
         AND TKSM.TOKUISAKICD = vRecordS04W003T.TOKUISAKICD
        WHERE SHN.APKIHONCD   = vRecordS04W003T.APKIHONCD
          AND SHN.SHOPCD      = vRecordS04W003T.SHOPCD
          AND SHN.SHNCD       = wShncd
          AND vRecordS04W003T.ACPTDD BETWEEN SHN.HANBAISTRTDD AND SHN.HANBAISHRYDD
          AND SHN.SHOPYUKOKBN = '1'
        ;
        
        meisaiVO.SHNCD := wShncd;
        meisaiVO.KATASHIKI := wKatashiki;
        meisaiVO.KEIJOKBN := wKeijokbn;
        meisaiVO.ACPTSURYO := wSuryo;
        
        IF mainVO.DLVRYKBN = c_DLVRY_MOCHIKAERI THEN
          meisaiVO.HYOJUNUNT := getCurrentValue(meisaiVO.GENZHYOJUNMOCHIKAERIPRICE
                                              ,meisaiVO.SHINHYOJUNMOCHIKAERIPRICE
                                              ,meisaiVO.HYOJUNPRICEKIRIKAEDD
                                              ,vRecordS04W003T.DLVRYYTIDD);
        ELSE
          meisaiVO.HYOJUNUNT := getCurrentValue(meisaiVO.GENZHYOJUNPRICE
                                              ,meisaiVO.SHINHYOJUNPRICE
                                              ,meisaiVO.HYOJUNPRICEKIRIKAEDD
                                              ,vRecordS04W003T.DLVRYYTIDD);
        END IF;
        
        meisaiVO.ACPTUNT := wUnt;
-- 本線対応変更 start ABI
--        meisaiVO.ACPTKIN := S01SyohizeiService.roundValue(wSuryo * wUnt, wUrikinhasukeisankbn);
        meisaiVO.ACPTKIN := Z1808B01.roundValue(wSuryo * wUnt, wUrikinhasukeisankbn);
-- 本線対応変更 end ABI

        --計上区分 = "10:レンタル", "09:サブレンタル"の場合
        IF meisaiVO.KEIJOKBN IN ('10', '09') THEN
          meisaiVO.YUSYOKBN := '3'; --レンタル
        --計上区分 = "01:売上", "02:キャンペーン"の場合
        ELSIF meisaiVO.KEIJOKBN IN ('01', '02') THEN
          meisaiVO.YUSYOKBN := '1'; --有償
        --それ以外
        ELSE
          meisaiVO.YUSYOKBN := '2'; --無償
        END IF;
        
        meisaiVO.MEISAIKBN := c_MEISAI_SHOHIN;
        meisaiVO.NYURYOKUMEISAIFLG := '1';
        meisaiVO.MEISAIBIKO := wMeisaibiko;
        meisaiVO.DATSKSMOTOKBN := wDatsksmotokbn;
        
        --明細リストに追加
        meisaiList(meisaiVO.ACPTDNPYMEISAINO) := meisaiVO;

      END IF;
    END LOOP;
    
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20112, SQLERRM || 'ワークから明細情報を設定時エラーが発生しました。');
  END createJuchuMeisai;
  
  /****************************************************************************/
  /* プロシージャ名：受注伝票明細リストを取得                                 */
  /* 引数           ：iApkihoncd            AP基本コード                      */
  /*                ：iShopcd               ショップコード                    */
  /*                ：iAcptdnpyno           受注伝票番号                      */
  /*                ：iEdano                枝番                              */
  /*                ：oMeisaiList           明細リスト                        */
  /****************************************************************************/
  PROCEDURE getJuchuMeisai( iApkihoncd     IN VARCHAR2
                          , iShopcd        IN VARCHAR2
                          , iAcptdnpyno    IN VARCHAR2
                          , iEdano         IN NUMBER
                          , oMeisaiList    OUT meisaiListtype
  ) IS 
  
  CURSOR SELECT_MEISAI IS
  SELECT
      JHMT.APKIHONCD
    , JHMT.SHOPCD
    , JHMT.ACPTDNPYNO
    , JHMT.EDANO
    , JHMT.ACPTDNPYMEISAINO
    , JHMT.ACPTDNPYMEISAIEDANO
    , JHMT.ITAKUAITEAPKIHONCD
    , JHMT.ITAKUAITESHOPCD
    , JHMT.ITAKUAITEACPTDNPYNO
    , JHMT.ITAKUAITEEDANO
    , JHMT.ITAKUAITEACPTDNPYMEISAINO
    , JHMT.ITAKUAITEACPTDNPYMEISAIEDANO
    , JHMT.LCTGRYCD
    , JHMT.MCTGRYCD
    , JHMT.SCTGRYCD
    , JHMT.SHNCD
    , JHMT.HAIFUBUTSUAPKIHONCD
    , JHMT.HAIFUBUTSUSHOPCD
    , JHMT.HAIFUBUTSUCD
    , JHMT.KATASIKI
    , JHMT.KEIJOKBN
    , JHMT.ACPTSURYO
    , JHMT.HYOJUNUNT
    , JHMT.ACPTUNT
    , JHMT.ACPTKIN
    , JHMT.ACPTUNTSHOHITAXKIN
    , JHMT.ACPTSHOHITAXKIN
    , JHMT.ITAKUCMPNYACPTUNT
    , JHMT.ITAKUCMPNYACPTUNTSHOHITAXKIN
    , JHMT.ITAKUCMPNYACPTSHOHITAXKIN
    , JHMT.NOHINSURYO
    , JHMT.NOHINUNT
    , JHMT.NOHINKIN
    , JHMT.NOHINUNTSHOHITAXKIN
    , JHMT.NOHINSHOHITAXKIN
    , JHMT.SOUKAISU
    , JHMT.ZANKAISU
-- インボイス対応変更 start ABI
--    , CASE WHEN JHMT.TAXKBN IN ('4','5') AND JHTT.DLVRYYTIDD <= '20190930' THEN '1' ELSE JHMT.TAXKBN END
    , JHMT.TAXKBN
-- インボイス対応変更 end ABI
    , JHMT.YUSYOKBN
    , JHMT.MEISAIKBN
    , JHMT.NYURYOKUMEISAIFLG
    , JHMT.TANPINMEISAIFLG
    , JHMT.STSHNCD
    , JHMT.MEISAIBIKO
    , JHMT.DATSKSMOTOKBN
    , JHMT.URIDNPYNO
    , JHMT.URIDNPYEDANO
    , JHMT.URIDNPYMEISAINO
    , JHMT.URIDNPYMEISAIEDANO
    , SHMV.SHNAPKIHONCD
-- インボイス対応変更 start ABI
--    , CASE WHEN SHMV.HYOJUNPRICEKIRIKAEDD <= JHTT.ACPTDD THEN SHMV.SHINPRICEKBN
    , CASE WHEN SHMV.HYOJUNPRICEKIRIKAEDD <= JHTT.DLVRYYTIDD THEN SHMV.SHINPRICEKBN
-- インボイス対応変更 end ABI
      ELSE SHMV.GENZPRICEKBN END PRICEKBN
    , SHMV.HYOJUNPRICEKIRIKAEDD
    , SHMV.GENZHYOJUNPRICE
    , SHMV.SHINHYOJUNPRICE
    , SHMV.GENZMOCHIKAERIPRICE
    , SHMV.SHINMOCHIKAERIPRICE
    , SHMV.OPNPRICEKIRIKAEDD
    , SHMV.GENZOPNPRICE
    , SHMV.SHINOPNPRICE
    , SHMV.GENZOPNMOCHIKAERIPRICE
    , SHMV.SHINOPNMOCHIKAERIPRICE
    , SHMV.HANSOKUKBN
    , SHMV.HANSOKUSTRTDD
    , SHMV.HANSOKUSHRYDD
    , SHMV.HANSOKUDLVRYPRICE
    , SHMV.HANSOKUMOCHIKAERIPRICE
    , SHMV.HANSOKUYUSENKBN
    , SHMV.ZAIKOUNT
    , SHMV.URIIRISU
    , SHMV.TAXSETTEIKBN
    , SHMV.ZAIKOARAMUSURYO
    , CASE WHEN TKSM.RKNKINDTOKUBETSUUNTFLG = '1' THEN TSTM.TEKIYOSTRTDD
      ELSE NULL END TEKIYOSTRTDD
    , CASE WHEN TKSM.RKNKINDTOKUBETSUUNTFLG = '1' THEN TSTM.TEKIYOSHRYDD
      ELSE NULL END TEKIYOSHRYDD
    , CASE WHEN TKSM.RKNKINDTOKUBETSUUNTFLG = '1' THEN TSTM.DLVRYPRICE
      ELSE NULL END DLVRYPRICE
    , CASE WHEN TKSM.RKNKINDTOKUBETSUUNTFLG = '1' THEN TSTM.MOCHIKAERIPRICE
      ELSE NULL END MOCHIKAERIPRICE
    , SHMV.ZAIKOKANRITRGTFLG
    , CASE WHEN TKSM.RKNKINDTOKUBETSUUNTFLG = '1' THEN TSSM.TEKIYOSTRTDD
      ELSE NULL END SET_TEKIYOSTRTDD
    , CASE WHEN TKSM.RKNKINDTOKUBETSUUNTFLG = '1' THEN TSSM.TEKIYOSHRYDD
      ELSE NULL END SET_TEKIYOSHRYDD
    , CASE WHEN TKSM.RKNKINDTOKUBETSUUNTFLG = '1' THEN TSSM.DLVRYPRICE
      ELSE NULL END SET_DLVRYPRICE
    , CASE WHEN TKSM.RKNKINDTOKUBETSUUNTFLG = '1' THEN TSSM.MOCHIKAERIPRICE
      ELSE NULL END SET_MOCHIKAERIPRICE
    
  FROM
    S04T002T JHMT 
  INNER JOIN
    S04T001T JHTT
    ON (JHTT.APKIHONCD = JHMT.APKIHONCD
    AND JHTT.SHOPCD = JHMT.SHOPCD
    AND JHTT.ACPTDNPYNO = JHMT.ACPTDNPYNO
    AND JHTT.EDANO = JHMT.EDANO)
  LEFT OUTER JOIN
    S02V001T SHMV
    ON (SHMV.APKIHONCD = JHMT.APKIHONCD
    AND SHMV.SHOPCD = JHMT.SHOPCD
    AND SHMV.SHNCD = JHMT.SHNCD)
  LEFT OUTER JOIN
    S02M013T TKSM
    ON (TKSM.APKIHONCD= JHTT.APKIHONCD
    AND TKSM.SHOPCD = JHTT.SHOPCD
    AND TKSM.TOKUISAKICD = JHTT.TOKUISAKICD)
  LEFT OUTER JOIN
    S02M009T TSTM
    ON (TSTM.APKIHONCD= SHMV.APKIHONCD
    AND TSTM.SHOPCD = SHMV.SHOPCD
    AND TSTM.SHNCD = SHMV.SHNCD
    AND TSTM.TOKUISAKICD = JHTT.TOKUISAKICD 
    AND JHTT.DLVRYYTIDD BETWEEN TSTM.TEKIYOSTRTDD AND TSTM.TEKIYOSHRYDD
    AND TSTM.YUKOKBN = '1')
  LEFT OUTER JOIN
    S02M066T TSSM
    ON (TSSM.APKIHONCD   = SHMV.APKIHONCD
    AND TSSM.SHOPCD      = SHMV.SHOPCD
    AND TSSM.STSHNCD     = SHMV.SHNCD
    AND TSSM.TOKUISAKICD = JHTT.TOKUISAKICD 
    AND JHTT.DLVRYYTIDD BETWEEN TSSM.TEKIYOSTRTDD AND TSSM.TEKIYOSHRYDD
    AND TSSM.YUKOKBN     = '1'
  )
    
  WHERE
        JHMT.APKIHONCD = iApkihoncd
    AND JHMT.SHOPCD = iShopcd
    AND JHMT.ACPTDNPYNO = iAcptdnpyno
    AND JHMT.EDANO = iEdano
  ORDER BY 
    JHMT.ACPTDNPYMEISAINO, JHMT.ACPTDNPYMEISAIEDANO
  FOR UPDATE OF JHMT.ACPTDNPYNO
  ;
  meiVO meisaiVOtype;
  BEGIN
    --明細リストを作成
    OPEN SELECT_MEISAI;
    LOOP FETCH SELECT_MEISAI INTO meiVO;
      EXIT WHEN SELECT_MEISAI%NOTFOUND; 
      oMeisaiList(oMeisaiList.COUNT + 1) := meiVO;
    END LOOP;
    CLOSE SELECT_MEISAI;
    
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20113, SQLERRM || '受注明細リストを取得時エラーが発生しました。');
  END getJuchuMeisai;
  
  /****************************************************************************/
  /* プロシージャ名：受注伝票・明細取得                                       */
  /* 引数           ：oJuchuDenVO           伝票VO                            */
  /*                ：oMeisaiList           明細リスト                        */
  /****************************************************************************/
  PROCEDURE getJuchuDenpyoMeisai( iApkihoncd       IN VARCHAR2
                                , iShopcd          IN VARCHAR2
                                , iAcptDnpyno      IN VARCHAR2
                                , iEdano           IN NUMBER
                                , iTokuisakicd     IN VARCHAR2
                                , oJuchuDenVO      OUT denVOtype
                                , oMeisaiList      OUT meisaiListtype
  )
  IS 
  BEGIN
    --受注伝票の読み込み
    loadJuchuDenpyo(iApkihoncd, iShopcd, iAcptDnpyno, iEdano, oJuchuDenVO);
    --受注明細の読み込み
    getJuchuMeisai(iApkihoncd, iShopcd, iAcptDnpyno, iEdano, oMeisaiList);
    --得意先配達情報を伝票VOに設定
    loadTokuisakiHaitatsuInfo(iApkihoncd, iShopcd, iTokuisakicd, '0', oJuchuDenVO, false);

    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20114, SQLERRM || '受注伝票・明細取得時エラーが発生しました。');
  END getJuchuDenpyoMeisai;
  
  /****************************************************************************/
  /* プロシージャ名：在庫リスト作成                                           */
  /* 引数           ：iCancelFg             キャンセルフラグ                  */
  /*                ：iCheckFg              チェックフラグ                    */
  /*                ：iJuchuDenVO           伝票VO                            */
  /*                ：iMeisaiList           明細リスト                        */
  /*                ：oZaikoList            在庫リスト                        */
  /****************************************************************************/
  PROCEDURE createZaikoKeyList( iCancelFg        IN BOOLEAN
                              , iCheckFg         IN BOOLEAN
                              , iJuchuDenVO      IN denVOtype
                              , iMeisaiList      IN meisaiListtype
                              , oZaikoList       OUT zaikokeyListtype
  )
  IS 
  /* 変数宣言 */
  zaikokeyVO zaikokeyVOtype;
  meiVO meisaiVOtype;
  BEGIN
    FOR i IN 1..iMeisaiList.COUNT LOOP
      meiVO := iMeisaiList(i);
      --セット商品の入力行以外を対象とする
      IF meiVO.TANPINMEISAIFLG = '1' AND meiVO.MEISAIKBN = c_MEISAI_SHOHIN THEN
        IF isLegular(meiVO.SHNCD) OR isSlim(meiVO.SHNCD) THEN
          --配達拠点受注アラーム
          zaikokeyVO.MODELTYPE := 'S04T005T';
          zaikokeyVO.MINUSFG := iCancelFg;
          zaikokeyVO.CHECKFG := iCheckFg;
          zaikokeyVO.APKIHONCD := iJuchuDenVO.DLVRYKANKATSUAPKIHONCD;
          zaikokeyVO.SHOPCD := iJuchuDenVO.DLVRYKANKATSUSHOPCD;
          zaikokeyVO.DLVRYKYOTENCD := iJuchuDenVO.DLVRYKYOTENCD;
          zaikokeyVO.YMD := iJuchuDenVO.DLVRYYTIDD;
          zaikokeyVO.FOURTHKEY := meiVO.SHNCD;
          zaikokeyVO.ARAMUSURYO := meiVO.ZAIKOARAMUSURYO;
          zaikokeyVO.SURYO := meiVO.ACPTSURYO;
          zaikokeyVO.CHECKRESULT := TRUE;
          oZaikoList(oZaikoList.COUNT + 1) := zaikokeyVO;
        END IF;
        IF iJuchuDenVO.ZAIKOKANRIKBN IN ('1', '2') AND meiVO.ZAIKOKANRITRGTFLG = '1' THEN
          --在庫引当テーブル
          IF meiVO.LCTGRYCD = gWaterCtgry THEN
            zaikokeyVO.MODELTYPE := 'S13T004T';
            zaikokeyVO.MINUSFG := iCancelFg;
            zaikokeyVO.CHECKFG := FALSE;
            zaikokeyVO.APKIHONCD := iJuchuDenVO.DLVRYKANKATSUAPKIHONCD;
            zaikokeyVO.SHOPCD := iJuchuDenVO.DLVRYKANKATSUSHOPCD;
            zaikokeyVO.DLVRYKYOTENCD := iJuchuDenVO.DLVRYKYOTENCD;
            zaikokeyVO.YMD := iJuchuDenVO.DLVRYYTIDD;
            zaikokeyVO.FOURTHKEY := meiVO.SHNCD;
            zaikokeyVO.ARAMUSURYO := meiVO.ZAIKOARAMUSURYO;
            zaikokeyVO.SURYO := meiVO.ACPTSURYO;
            zaikokeyVO.CHECKRESULT := TRUE;
            oZaikoList(oZaikoList.COUNT + 1) := zaikokeyVO;
          ELSE
            zaikokeyVO.MODELTYPE := 'S13T004T';
            zaikokeyVO.MINUSFG := iCancelFg;
            zaikokeyVO.CHECKFG := iCheckFg;
            zaikokeyVO.APKIHONCD := iJuchuDenVO.DLVRYKANKATSUAPKIHONCD;
            zaikokeyVO.SHOPCD := iJuchuDenVO.DLVRYKANKATSUSHOPCD;
            zaikokeyVO.DLVRYKYOTENCD := iJuchuDenVO.DLVRYKYOTENCD;
            zaikokeyVO.YMD := iJuchuDenVO.DLVRYYTIDD;
            zaikokeyVO.FOURTHKEY := meiVO.SHNCD;
            zaikokeyVO.ARAMUSURYO := meiVO.ZAIKOARAMUSURYO;
            zaikokeyVO.SURYO := meiVO.ACPTSURYO;
            zaikokeyVO.CHECKRESULT := TRUE;
            oZaikoList(oZaikoList.COUNT + 1) := zaikokeyVO;
          END IF;
        END IF;
      END IF;
    END LOOP;
    --担当者受注アラーム
    IF iJuchuDenVO.DLVRYTNSHACD IS NOT NULL THEN
      zaikokeyVO.MODELTYPE := 'S04T006T';
      zaikokeyVO.MINUSFG := iCancelFg;
      zaikokeyVO.CHECKFG := iCheckFg;
      zaikokeyVO.APKIHONCD := iJuchuDenVO.DLVRYKANKATSUAPKIHONCD;
      zaikokeyVO.SHOPCD := iJuchuDenVO.DLVRYKANKATSUSHOPCD;
      zaikokeyVO.DLVRYKYOTENCD := '00';
      zaikokeyVO.YMD := iJuchuDenVO.DLVRYYTIDD;
      zaikokeyVO.FOURTHKEY := iJuchuDenVO.DLVRYTNSHACD;
      zaikokeyVO.ARAMUSURYO := 0;
      zaikokeyVO.SURYO := 1;
      zaikokeyVO.CHECKRESULT := TRUE;
      oZaikoList(oZaikoList.COUNT + 1) := zaikokeyVO;
    END IF;
    --配達エリア受注アラーム
    zaikokeyVO.MODELTYPE := 'S04T007T';
    zaikokeyVO.MINUSFG := iCancelFg;
    zaikokeyVO.CHECKFG := iCheckFg;
    zaikokeyVO.APKIHONCD := iJuchuDenVO.DLVRYKANKATSUAPKIHONCD;
    zaikokeyVO.SHOPCD := iJuchuDenVO.DLVRYKANKATSUSHOPCD;
    zaikokeyVO.DLVRYKYOTENCD := iJuchuDenVO.DLVRYKYOTENCD;
    zaikokeyVO.YMD := iJuchuDenVO.DLVRYYTIDD;
    zaikokeyVO.FOURTHKEY := iJuchuDenVO.DLVRYAREACD;
    zaikokeyVO.ARAMUSURYO := 0;
    zaikokeyVO.SURYO := 1;
    zaikokeyVO.CHECKRESULT := TRUE;
    oZaikoList(oZaikoList.COUNT + 1) := zaikokeyVO;
    
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20115, SQLERRM || '在庫リスト作成時エラーが発生しました。');
  END createZaikoKeyList;
  
  /****************************************************************************/
  /* プロシージャ名：得意先チェック更新処理                                   */
  /* 引数           ：iCancelFg            キャンセルフラグ                   */
  /*                ：iCheckFg             チェックフラグ                     */
  /*                ：iJuchuDenVO          伝票VO                             */
  /*                ：iMeisaiList          明細リスト                         */
  /*                ：iKyuminUpdFg         休眠更新フラグ                     */
  /****************************************************************************/
  PROCEDURE checkUpdateTokuisaki( iCancelFg        IN BOOLEAN
                                , iCheckFg         IN BOOLEAN
                                , iJuchuDenVO      IN denVOtype
                                , iMeisaiList      IN meisaiListtype
                                , iKyuminUpdFg     IN BOOLEAN
  )
  IS 
  /* 変数宣言 */
  vRecordS02M050T   S02M050T%rowtype; --得意先実績テーブル
  wSaishuAcptdd     VARCHAR2(8) := NULL;
  wKyuminJudgeCnt   S02M006T.SUCHISETTEIATAI%TYPE;
  wResultCnt        NUMBER(5,0);
  wLatestJuchuDate  VARCHAR2(8) := NULL;
  wKyuminStDate     VARCHAR2(8) := NULL;
  BEGIN
    IF iCheckFg = true THEN
      --得意先状態が申込受付、仮会員の場合
      IF iJuchuDenVO.TOKUISAKIJOTAI IN ('1', '3') THEN
        --明細単位に計上区分チェック
        FOR i IN 1..iMeisaiList.COUNT LOOP
          --有料の場合
          IF iMeisaiList(i).KEIJOKBN IN ('01', '02') THEN
            --得意先状態チェック1
            IF iJuchuDenVO.SKYSAKICD IS NULL THEN
              ErrAdd(c_ERRKBN_ERR, 'EJ040:請求先が存在しないため、有料の価格は設定できません。' || '[' || TO_CHAR(i) || '明細目]');
            --得意先状態チェック2
            ELSE
              ErrAdd(c_ERRKBN_WAR, 'WJ005:商品コード「' || iMeisaiList(i).SHNCD || '」に有料の価格が設定されています。' || '[' || TO_CHAR(i) || '明細目]');
            END IF;
          END IF;
        END LOOP;
      END IF;
    END IF;
    --与信限度額のチェック
    IF iJuchuDenVO.YOSHINGENDOKIN <> 0 AND
       (iJuchuDenVO.GENZAIZD + iJuchuDenVO.KEIJOMAEACPTKIN + iJuchuDenVO.ACPTKIN > iJuchuDenVO.YOSHINGENDOKIN) THEN
      ErrAdd(c_ERRKBN_WAR, 'WJ006:与信限度額を過ぎています。');
    END IF;
    
    IF gErrFlg = '0' THEN
      /* 得意先実績テーブルの更新 */
      vRecordS02M050T.KEIJOMAEACPTKIN := iJuchuDenVO.ACPTKIN + iJuchuDenVO.ACPTSHOHITAXKIN;
      --実績テーブルの最終受注日が伝票の受注日よりも古い場合
      IF iCancelFg = false AND iJuchuDenVO.SAISHUACPTDD < iJuchuDenVO.ACPTDD THEN
        vRecordS02M050T.SAISHUACPTDD := iJuchuDenVO.ACPTDD;
      --キャンセルの場合
      ELSIF iCancelFg = true THEN
        --最終受注日を再検索してセットする
        BEGIN
          SELECT
            MAX(ACPTDD) SAISHUACPTDD
          INTO
            wSaishuAcptdd
          FROM
            S04T001T
          WHERE
                APKIHONCD   = iJuchuDenVO.APKIHONCD
            AND SHOPCD      = iJuchuDenVO.SHOPCD
            AND TOKUISAKICD = iJuchuDenVO.TOKUISAKICD
            AND YUKOKBN     = '1' 
            AND ACPTDNPYNO  <> iJuchuDenVO.ACPTDNPYNO
            AND DNPYSTS IN ('02', '03', '11', '12')
          ;
          EXCEPTION 
            WHEN NO_DATA_FOUND THEN 
              wSaishuAcptdd := NULL;
        END;
        vRecordS02M050T.SAISHUACPTDD := wSaishuAcptdd;
        --計上金額をマイナスする
        vRecordS02M050T.KEIJOMAEACPTKIN := vRecordS02M050T.KEIJOMAEACPTKIN * -1;
      END IF;
      --処理後の金額が11桁を超える場合は更新しない
      IF LENGTH(iJuchuDenVO.KEIJOMAEACPTKIN + vRecordS02M050T.KEIJOMAEACPTKIN) > 11 THEN
        NULL;
      ELSE
        UPDATE S02M050T
           SET KOSHAAPKIHONCD  = vRecordS04W003T.ACPTTNSHAAPKIHONCD
             , KOSHASHOPCD     = vRecordS04W003T.ACPTTNSHASHOPCD
             , KOSHACD         = vRecordS04W003T.ACPTTNSHACD
             , KSHNSYSTMYMD    = gSystmYmd
             , KSHNSYSTMJIKAN  = gSystmJikan
             , KSHNUNYOYMD     = gUnyoDate
             , KSHNIPADRSE     = gIpadrse
             , KSHNPGMID       = c_PROGRAMID
             , KEIJOMAEACPTKIN = KEIJOMAEACPTKIN + vRecordS02M050T.KEIJOMAEACPTKIN
             , SAISHUACPTDD    = CASE WHEN vRecordS02M050T.SAISHUACPTDD IS NULL THEN SAISHUACPTDD
                                 ELSE vRecordS02M050T.SAISHUACPTDD END
         WHERE 
               APKIHONCD   = iJuchuDenVO.APKIHONCD
           AND SHOPCD      = iJuchuDenVO.SHOPCD
           AND TOKUISAKICD = iJuchuDenVO.TOKUISAKICD
           AND YUKOKBN     = '1'
        ;
        IF SQL%ROWCOUNT = 0 THEN
          ErrAdd(c_ERRKBN_ERR, 'EJ041:得意先実績テーブルが存在しません。');
        END IF;
      END IF;
    END IF;
    
    IF gErrFlg = '0' AND iKyuminUpdFg = true THEN
      /* 得意先マスタの更新 */
      IF iCancelFg = false AND iJuchuDenVO.KYUMINKBN = '1' THEN
        UPDATE S02M013T
           SET KOSHAAPKIHONCD  = vRecordS04W003T.ACPTTNSHAAPKIHONCD
             , KOSHASHOPCD     = vRecordS04W003T.ACPTTNSHASHOPCD
             , KOSHACD         = vRecordS04W003T.ACPTTNSHACD
             , KSHNSYSTMYMD    = gSystmYmd
             , KSHNSYSTMJIKAN  = gSystmJikan
             , KSHNUNYOYMD     = gUnyoDate
             , KSHNIPADRSE     = gIpadrse
             , KSHNPGMID       = c_PROGRAMID
             , KYUMINKBN       = '0'
         WHERE 
               APKIHONCD   = iJuchuDenVO.APKIHONCD
           AND SHOPCD      = iJuchuDenVO.SHOPCD
           AND TOKUISAKICD = iJuchuDenVO.TOKUISAKICD
           AND YUKOKBN     = '1'
        ;
      ELSIF iCancelFg = true AND iJuchuDenVO.KYUMINKBN = '0' THEN
        BEGIN
          SELECT SUCHISETTEIATAI
            INTO wKyuminJudgeCnt
            FROM S02M006T  
           WHERE APKIHONCD = c_SYS_APKIHONCD 
             AND SHOPCD    = c_SYS_SHOPCD
             AND SETTEINO  = '120' 
             AND RCRDKBN   = 'M'
             AND SETTEICD  = '1' 
             AND YUKOKBN   = '1'
          ;
          EXCEPTION 
            WHEN NO_DATA_FOUND THEN 
              wKyuminJudgeCnt := 0;
        END;
        IF wKyuminJudgeCnt > 0 THEN
          --運用日付-休眠判定日数
-- 本線対応変更 start ABI
          --wKyuminStDate := TO_CHAR(TO_DATE(gUnyoDate) - wKyuminJudgeCnt, 'YYYYMMDD');
          wKyuminStDate := TO_CHAR(TO_DATE(gUnyoDate, 'YYYYMMDD') - wKyuminJudgeCnt, 'YYYYMMDD');
-- 本線対応変更 end ABI

          SELECT COUNT(M013.APKIHONCD) AS CNT 
            INTO wResultCnt
            FROM S02M013T M013 
            JOIN S02M050T M050 
              ON M050.APKIHONCD     = M013.APKIHONCD 
             AND M050.SHOPCD        = M013.SHOPCD 
             AND M050.TOKUISAKICD   = M013.TOKUISAKICD 
             AND M050.ZNKDLVRYDNPYNO IS NOT NULL 
            JOIN S02M017T M017 
              ON M017.APKIHONCD     = M013.APKIHONCD 
             AND M017.SHOPCD        = M013.SHOPCD 
             AND M017.SKYSAKICD     = M013.SKYSAKICD 
             AND M017.YUKOKBN       = '1' 
           WHERE M013.TOKUISAKIKBN  = '1' 
             AND M013.TOKUISAKIJOTAI IN ('3', '5') 
             AND M013.YUKOKBN       = '1' 
             AND M013.APKIHONCD     =  iJuchuDenVO.APKIHONCD
             AND M013.SHOPCD        =  iJuchuDenVO.SHOPCD
             AND M013.TOKUISAKICD   =  iJuchuDenVO.TOKUISAKICD
             -- 未納受注が１件もない得意先の最終の配達済みの受注日が休眠期間指定日数よりも過去
             AND NOT EXISTS (SELECT '1' FROM S04T001T S04
                 WHERE S04.APKIHONCD   = M013.APKIHONCD
                   AND S04.SHOPCD      = M013.SHOPCD
                   AND S04.TOKUISAKICD = M013.TOKUISAKICD
                   AND S04.ACPTDNPYNO != iJuchuDenVO.ACPTDNPYNO 
                   AND S04.DNPYSTS    IN ('02','03','11')
                   AND S04.YUKOKBN     = '1')
          ;
          IF wResultCnt > 0 THEN
            --最新の受注日と休眠判定日を比較
            BEGIN
              SELECT MAX(T001.ACPTDD) AS MAX_ACPTDD 
                INTO wLatestJuchuDate
                FROM S04T001T T001 
               WHERE T001.APKIHONCD   = iJuchuDenVO.APKIHONCD
                 AND T001.SHOPCD      = iJuchuDenVO.SHOPCD
                 AND T001.TOKUISAKICD = iJuchuDenVO.TOKUISAKICD
                 AND T001.DNPYSTS     = '12'
                 AND T001.YUKOKBN     = '1' 
              ;
              EXCEPTION 
                WHEN NO_DATA_FOUND THEN 
                  wLatestJuchuDate := '00000000';
            END;
            IF wKyuminStDate > wLatestJuchuDate THEN
              UPDATE S02M013T
                 SET KOSHAAPKIHONCD  = vRecordS04W003T.ACPTTNSHAAPKIHONCD
                   , KOSHASHOPCD     = vRecordS04W003T.ACPTTNSHASHOPCD
                   , KOSHACD         = vRecordS04W003T.ACPTTNSHACD
                   , KSHNSYSTMYMD    = gSystmYmd
                   , KSHNSYSTMJIKAN  = gSystmJikan
                   , KSHNUNYOYMD     = gUnyoDate
                   , KSHNIPADRSE     = gIpadrse
                   , KSHNPGMID       = c_PROGRAMID
                   , KYUMINKBN       = '1'
               WHERE 
                     APKIHONCD   = iJuchuDenVO.APKIHONCD
                 AND SHOPCD      = iJuchuDenVO.SHOPCD
                 AND TOKUISAKICD = iJuchuDenVO.TOKUISAKICD
                 AND YUKOKBN     = '1'
              ;
            END IF;
          END IF;
        END IF;
      END IF;
    END IF;
    
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20116, SQLERRM || '得意先チェック更新時エラーが発生しました。');
  END checkUpdateTokuisaki;
  
  /****************************************************************************/
  /* プロシージャ名：在庫チェック更新                                         */
  /* 引数           ：iZaikoList            在庫リスト                        */
  /****************************************************************************/
  PROCEDURE checkUpdateZaiko( iZaikoList        IN zaikokeyListtype
  )
  IS 
  /* 変数宣言 */
  zaikoVO zaikokeyVOtype;
  wHikiatesumiZaikokazu S13T004T.HIKIATESUMIZAIKOKAZU%type;
  wZaikoSuryo           S13T001T.ZAIKOSURYO%type;
  wAlarm   S04T005T.ARAMUSETTEIACPTSURYO%type;
  wCurrent S04T005T.ACPTKAZU%type;
  wUpdateSuryo NUMBER(9,1);
  
  wCount                 NUMBER(2);          -- アラームデータ存在チェック用
  wYmd                   S04T005T.YMD%TYPE;  -- 年月日
  wYobi                  S04T005T.YOBI%TYPE; -- 曜日
  wAramusetteiAcptsuryo  S04T005T.ARAMUSETTEIACPTSURYO%TYPE; -- アラーム設定受注数量
  wAramusetteiAcptcnt    S04T006T.ARAMUSETTEIACPTCNT%TYPE; -- アラーム設定受注件数
  
  BEGIN
    IF iZaikoList.COUNT = 0 THEN
      return;
    END IF;
    
    FOR i IN 1..iZaikoList.COUNT LOOP
      zaikoVO := iZaikoList(i);
      --在庫引当テーブル
      IF zaikoVO.MODELTYPE = 'S13T004T' THEN
        BEGIN
          SELECT HIKIATESUMIZAIKOKAZU 
            INTO wHikiatesumiZaikokazu
            FROM S13T004T
           WHERE APKIHONCD     = zaikoVO.APKIHONCD
             AND SHOPCD        = zaikoVO.SHOPCD
             AND DLVRYKYOTENCD = zaikoVO.DLVRYKYOTENCD
             AND SHNCD         = zaikoVO.FOURTHKEY
          ;
          EXCEPTION 
            WHEN NO_DATA_FOUND THEN 
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
                     zaikoVO.APKIHONCD     -- 受注引当先ＡＰ基本コード
                   , zaikoVO.SHOPCD        -- 受注引当先ショップコード
                   , zaikoVO.DLVRYKYOTENCD -- 受注引当先拠点コード
                   , zaikoVO.FOURTHKEY     -- 商品コード
                   , 0                     -- 引当済在庫数
                   /* テーブル共通項目 */
                   , '1' -- 有効区分
                   , vRecordS04W003T.ACPTTNSHAAPKIHONCD -- 登録者ＡＰ基本コード
                   , vRecordS04W003T.ACPTTNSHASHOPCD    -- 登録者ショップコード
                   , vRecordS04W003T.ACPTTNSHACD        -- 登録者コード
                   , gSystmYmd                          -- 登録システム日付
                   , gSystmJikan                        -- 登録システム時間
                   , gUnyoDate                          -- 登録運用日付
                   , gIpAdrse                           -- 登録ＩＰアドレス
                   , c_PROGRAMID                        -- 登録プログラムＩＤ
                   , vRecordS04W003T.ACPTTNSHAAPKIHONCD -- 更新者ＡＰ基本コード
                   , vRecordS04W003T.ACPTTNSHASHOPCD    -- 更新者ショップコード
                   , vRecordS04W003T.ACPTTNSHACD        -- 更新者コード
                   , gSystmYmd                          -- 更新システム日付
                   , gSystmJikan                        -- 更新システム時間
                   , gUnyoDate                          -- 更新運用日付
                   , gIpAdrse                           -- 更新ＩＰアドレス
                   , c_PROGRAMID                        -- 更新プログラムＩＤ
              );
              wHikiatesumiZaikokazu := 0;
        END;
        --チェック処理
        IF zaikoVO.CHECKFG = true THEN
          BEGIN
            SELECT
              NVL(SUM(GZKT.ZAIKOSURYO), 0) ZAIKOSURYO
              INTO wZaikoSuryo
            FROM
              S13T001T GZKT
            LEFT OUTER JOIN
              S02M041T SKOM
              ON (SKOM.APKIHONCD = GZKT.APKIHONCD
              AND SKOM.SHOPCD    = GZKT.SHOPCD
              AND SKOM.SOKOCD    = GZKT.SOKOCD)
            WHERE
                 GZKT.APKIHONCD = zaikoVO.APKIHONCD
             AND GZKT.SHOPCD    = zaikoVO.SHOPCD
             AND GZKT.SHNCD     = zaikoVO.FOURTHKEY
             AND GZKT.YUKOKBN   = '1'
            GROUP BY
              SKOM.DLVRYKYOTENCD
            HAVING
              SKOM.DLVRYKYOTENCD = zaikoVO.DLVRYKYOTENCD
            ;
            
            IF zaikoVO.ARAMUSURYO <> 0 AND zaikoVO.ARAMUSURYO > wZaikoSuryo - wHikiatesumiZaikokazu - zaikoVO.SURYO THEN
              ErrAdd(c_ERRKBN_WAR, 'WJ007:商品コード「' || zaikoVO.FOURTHKEY || '」が在庫引当可能数を過ぎています。');
            END IF;
            
            EXCEPTION 
              WHEN NO_DATA_FOUND THEN 
                NULL;  --チェックは行わない
          END;
        END IF;
        IF zaikoVO.MINUSFG = true THEN
          wUpdateSuryo := zaikoVO.SURYO * -1;
        ELSE
          wUpdateSuryo := zaikoVO.SURYO;
        END IF;
        --更新処理
        UPDATE S13T004T
           SET HIKIATESUMIZAIKOKAZU = HIKIATESUMIZAIKOKAZU + wUpdateSuryo
             , KOSHAAPKIHONCD  = vRecordS04W003T.ACPTTNSHAAPKIHONCD
             , KOSHASHOPCD     = vRecordS04W003T.ACPTTNSHASHOPCD
             , KOSHACD         = vRecordS04W003T.ACPTTNSHACD
             , KSHNSYSTMYMD    = gSystmYmd
             , KSHNSYSTMJIKAN  = gSystmJikan
             , KSHNUNYOYMD     = gUnyoDate
             , KSHNIPADRSE     = gIpadrse
             , KSHNPGMID       = c_PROGRAMID
         WHERE APKIHONCD     = zaikoVO.APKIHONCD
           AND SHOPCD        = zaikoVO.SHOPCD
           AND DLVRYKYOTENCD = zaikoVO.DLVRYKYOTENCD
           AND SHNCD         = zaikoVO.FOURTHKEY
        ;
      ELSIF zaikoVO.MODELTYPE = 'S04T005T' THEN
        
        -- アラームデータが存在しない場合：新規登録
        SELECT COUNT(*)
          INTO wCount
          FROM S04T005T DKJA
         WHERE DKJA.APKIHONCD     = zaikoVO.APKIHONCD
           AND DKJA.SHOPCD        = zaikoVO.SHOPCD
           AND DKJA.DLVRYKYOTENCD = zaikoVO.DLVRYKYOTENCD
           AND DKJA.SHNCD         = zaikoVO.FOURTHKEY
           AND DKJA.RENBAN        = 1
        ;
        IF wCount = 0 THEN 
           FOR wRenban IN 1..7 LOOP
               -- 年月日
               wYmd  := TO_CHAR(TO_DATE(gUnyoDate, 'YYYYMMDD') + (wRenban - 1), 'YYYYMMDD');
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
                  WHERE DKAM.APKIHONCD     = zaikoVO.APKIHONCD
                    AND DKAM.SHOPCD        = zaikoVO.SHOPCD
                    AND DKAM.DLVRYKYOTENCD = zaikoVO.DLVRYKYOTENCD
                    AND DKAM.SHNCD         = zaikoVO.FOURTHKEY
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
                      zaikoVO.APKIHONCD     -- 配達管轄ＡＰ基本コード
                    , zaikoVO.SHOPCD        -- 配達管轄ショップコード
                    , zaikoVO.DLVRYKYOTENCD -- 配達拠点コード
                    , zaikoVO.FOURTHKEY     -- 商品コード
                    , wRenban               -- 連番
                    , wYmd                  -- 日付
                    , wYobi                 -- 曜日
                    , wAramusetteiAcptsuryo -- アラーム設定受注数量
                    , 0                     -- 受注数（初期値）
                    /* テーブル共通項目 */
                    , '1' -- 有効区分
                    , vRecordS04W003T.ACPTTNSHAAPKIHONCD -- 登録者ＡＰ基本コード
                    , vRecordS04W003T.ACPTTNSHASHOPCD    -- 登録者ショップコード
                    , vRecordS04W003T.ACPTTNSHACD        -- 登録者コード
                    , gSystmYmd                          -- 登録システム日付
                    , gSystmJikan                        -- 登録システム時間
                    , gUnyoDate                          -- 登録運用日付
                    , gIpAdrse                           -- 登録ＩＰアドレス
                    , c_PROGRAMID                        -- 登録プログラムＩＤ
                    , vRecordS04W003T.ACPTTNSHAAPKIHONCD -- 更新者ＡＰ基本コード
                    , vRecordS04W003T.ACPTTNSHASHOPCD    -- 更新者ショップコード
                    , vRecordS04W003T.ACPTTNSHACD        -- 更新者コード
                    , gSystmYmd                          -- 更新システム日付
                    , gSystmJikan                        -- 更新システム時間
                    , gUnyoDate                          -- 更新運用日付
                    , gIpAdrse                           -- 更新ＩＰアドレス
                    , c_PROGRAMID                        -- 更新プログラムＩＤ
               );
           END LOOP;
        END IF;
        
        BEGIN
          SELECT ARAMUSETTEIACPTSURYO
               , ACPTKAZU
            INTO wAlarm
               , wCurrent
           FROM S04T005T 
          WHERE APKIHONCD     = zaikoVO.APKIHONCD
            AND SHOPCD        = zaikoVO.SHOPCD
            AND DLVRYKYOTENCD = zaikoVO.DLVRYKYOTENCD
            AND SHNCD         = zaikoVO.FOURTHKEY
            AND YMD           = zaikoVO.YMD
            AND YUKOKBN       = '1'
          ;
          EXCEPTION 
            WHEN NO_DATA_FOUND THEN 
              CONTINUE;
        END;
        --チェック処理
        IF zaikoVO.CHECKFG = true THEN
          IF wAlarm <> 0 AND wAlarm < wCurrent + zaikoVO.SURYO THEN
            ErrAdd(c_ERRKBN_WAR, 'WJ008:商品コード「' || zaikoVO.FOURTHKEY || '」が配達拠点受注アラーム数を過ぎています。');
          END IF;
        END IF;
        IF zaikoVO.MINUSFG = true THEN
          wUpdateSuryo := zaikoVO.SURYO * -1;
        ELSE
          wUpdateSuryo := zaikoVO.SURYO;
        END IF;
        --更新処理
        UPDATE S04T005T
           SET ACPTKAZU = ACPTKAZU + wUpdateSuryo
             , KOSHAAPKIHONCD  = vRecordS04W003T.ACPTTNSHAAPKIHONCD
             , KOSHASHOPCD     = vRecordS04W003T.ACPTTNSHASHOPCD
             , KOSHACD         = vRecordS04W003T.ACPTTNSHACD
             , KSHNSYSTMYMD    = gSystmYmd
             , KSHNSYSTMJIKAN  = gSystmJikan
             , KSHNUNYOYMD     = gUnyoDate
             , KSHNIPADRSE     = gIpadrse
             , KSHNPGMID       = c_PROGRAMID
         WHERE APKIHONCD     = zaikoVO.APKIHONCD
           AND SHOPCD        = zaikoVO.SHOPCD
           AND DLVRYKYOTENCD = zaikoVO.DLVRYKYOTENCD
           AND SHNCD         = zaikoVO.FOURTHKEY
           AND YMD           = zaikoVO.YMD
           AND YUKOKBN       = '1'
        ;
      ELSIF zaikoVO.MODELTYPE = 'S04T006T' THEN
      
        -- アラームデータが存在しない場合：新規登録
        SELECT COUNT(*)
          INTO wCount
          FROM S04T006T TNJA
         WHERE TNJA.APKIHONCD     = zaikoVO.APKIHONCD
           AND TNJA.SHOPCD        = zaikoVO.SHOPCD
           AND TNJA.DLVRYKYOTENCD = zaikoVO.DLVRYKYOTENCD
           AND TNJA.TNSHACD       = zaikoVO.FOURTHKEY
           AND TNJA.RENBAN        = 1
        ;
        IF wCount = 0 THEN 
           FOR wRenban IN 1..7 LOOP
               -- 年月日
               wYmd  := TO_CHAR(TO_DATE(gUnyoDate, 'YYYYMMDD') + (wRenban - 1), 'YYYYMMDD');
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
                WHERE TNSM.APKIHONCD = zaikoVO.APKIHONCD
                  AND TNSM.SHOPCD    = zaikoVO.SHOPCD
                  AND TNSM.TNSHACD   = zaikoVO.FOURTHKEY
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
                      zaikoVO.APKIHONCD     -- 配達管轄ＡＰ基本コード
                    , zaikoVO.SHOPCD        -- 配達管轄ショップコード
                    , zaikoVO.DLVRYKYOTENCD -- 配達拠点コード
                    , zaikoVO.FOURTHKEY     -- 配達担当者コード
                    , wRenban               -- 連番
                    , wYmd                  -- 日付
                    , wYobi                 -- 曜日
                    , wAramusetteiAcptcnt   -- アラーム設定受注件数
                    , 0                     -- 受注件数（初期値）
                    /* テーブル共通項目 */
                    , '1' -- 有効区分
                    , vRecordS04W003T.ACPTTNSHAAPKIHONCD -- 登録者ＡＰ基本コード
                    , vRecordS04W003T.ACPTTNSHASHOPCD    -- 登録者ショップコード
                    , vRecordS04W003T.ACPTTNSHACD        -- 登録者コード
                    , gSystmYmd                          -- 登録システム日付
                    , gSystmJikan                        -- 登録システム時間
                    , gUnyoDate                          -- 登録運用日付
                    , gIpAdrse                           -- 登録ＩＰアドレス
                    , c_PROGRAMID                        -- 登録プログラムＩＤ
                    , vRecordS04W003T.ACPTTNSHAAPKIHONCD -- 更新者ＡＰ基本コード
                    , vRecordS04W003T.ACPTTNSHASHOPCD    -- 更新者ショップコード
                    , vRecordS04W003T.ACPTTNSHACD        -- 更新者コード
                    , gSystmYmd                          -- 更新システム日付
                    , gSystmJikan                        -- 更新システム時間
                    , gUnyoDate                          -- 更新運用日付
                    , gIpAdrse                           -- 更新ＩＰアドレス
                    , c_PROGRAMID                        -- 更新プログラムＩＤ
               );
           END LOOP;
        END IF;
      
        BEGIN
          SELECT ARAMUSETTEIACPTCNT
               , ACPTCNT
            INTO wAlarm
               , wCurrent
            FROM S04T006T 
           WHERE APKIHONCD     = zaikoVO.APKIHONCD
             AND SHOPCD        = zaikoVO.SHOPCD
             AND DLVRYKYOTENCD = zaikoVO.DLVRYKYOTENCD
             AND TNSHACD       = zaikoVO.FOURTHKEY
             AND YMD           = zaikoVO.YMD
             AND YUKOKBN       = '1'
          ;
          EXCEPTION 
            WHEN NO_DATA_FOUND THEN 
              CONTINUE;
        END;
        --チェック処理
        IF zaikoVO.CHECKFG = true THEN
          IF wAlarm <> 0 AND wAlarm < wCurrent + zaikoVO.SURYO THEN
            ErrAdd(c_ERRKBN_WAR, 'WJ009:担当者受注アラーム数を過ぎています。');
          END IF;
        END IF;
        IF zaikoVO.MINUSFG = true THEN
          wUpdateSuryo := zaikoVO.SURYO * -1;
        ELSE
          wUpdateSuryo := zaikoVO.SURYO;
        END IF;
        --更新処理
        UPDATE S04T006T
           SET ACPTCNT = ACPTCNT + wUpdateSuryo
             , KOSHAAPKIHONCD  = vRecordS04W003T.ACPTTNSHAAPKIHONCD
             , KOSHASHOPCD     = vRecordS04W003T.ACPTTNSHASHOPCD
             , KOSHACD         = vRecordS04W003T.ACPTTNSHACD
             , KSHNSYSTMYMD    = gSystmYmd
             , KSHNSYSTMJIKAN  = gSystmJikan
             , KSHNUNYOYMD     = gUnyoDate
             , KSHNIPADRSE     = gIpadrse
             , KSHNPGMID       = c_PROGRAMID
         WHERE APKIHONCD     = zaikoVO.APKIHONCD
           AND SHOPCD        = zaikoVO.SHOPCD
           AND DLVRYKYOTENCD = zaikoVO.DLVRYKYOTENCD
           AND TNSHACD       = zaikoVO.FOURTHKEY
           AND YMD           = zaikoVO.YMD
           AND YUKOKBN       = '1'
        ;
      ELSIF zaikoVO.MODELTYPE = 'S04T007T' THEN
      
        -- アラームデータが存在しない場合：新規登録
        SELECT COUNT(*)
          INTO wCount
          FROM S04T007T DAJA
         WHERE DAJA.APKIHONCD     = zaikoVO.APKIHONCD
           AND DAJA.SHOPCD        = zaikoVO.SHOPCD
           AND DAJA.DLVRYKYOTENCD = zaikoVO.DLVRYKYOTENCD
           AND DAJA.DLVRYAREACD   = zaikoVO.FOURTHKEY
           AND DAJA.RENBAN        = 1
        ;
        IF wCount = 0 THEN 
           FOR wRenban IN 1..7 LOOP
               -- 年月日
               wYmd  := TO_CHAR(TO_DATE(gUnyoDate, 'YYYYMMDD') + (wRenban - 1), 'YYYYMMDD');
               -- 曜日
               wYobi := TO_NUMBER(TO_CHAR(TO_DATE(wYmd, 'YYYYMMDD'), 'd')) - 1;
               -- アラーム受注設定数量取得
               -- ACCIS2023-7365 配達エリアマスタに対するクエリについて条件に有効区分を追加
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
                WHERE DARM.APKIHONCD     = zaikoVO.APKIHONCD
                  AND DARM.SHOPCD        = zaikoVO.SHOPCD
                  AND DARM.DLVRYKYOTENCD = zaikoVO.DLVRYKYOTENCD
                  AND DARM.DLVRYAREACD   = zaikoVO.FOURTHKEY
                  AND wYmd BETWEEN DARM.TEKIYOSTRTDD AND DARM.TEKIYOSHRYDD
                  AND DARM.YUKOKBN       = '1'
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
                      zaikoVO.APKIHONCD     -- 配達管轄ＡＰ基本コード
                    , zaikoVO.SHOPCD        -- 配達管轄ショップコード
                    , zaikoVO.DLVRYKYOTENCD -- 配達拠点コード
                    , zaikoVO.FOURTHKEY     -- 配達担当者コード
                    , wRenban               -- 連番
                    , wYmd                  -- 日付
                    , wYobi                 -- 曜日
                    , wAramusetteiAcptcnt   -- アラーム設定受注件数
                    , 0                     -- 受注件数（初期値）
                    /* テーブル共通項目 */
                    , '1' -- 有効区分
                    , vRecordS04W003T.ACPTTNSHAAPKIHONCD -- 登録者ＡＰ基本コード
                    , vRecordS04W003T.ACPTTNSHASHOPCD    -- 登録者ショップコード
                    , vRecordS04W003T.ACPTTNSHACD        -- 登録者コード
                    , gSystmYmd                          -- 登録システム日付
                    , gSystmJikan                        -- 登録システム時間
                    , gUnyoDate                          -- 登録運用日付
                    , gIpAdrse                           -- 登録ＩＰアドレス
                    , c_PROGRAMID                        -- 登録プログラムＩＤ
                    , vRecordS04W003T.ACPTTNSHAAPKIHONCD -- 更新者ＡＰ基本コード
                    , vRecordS04W003T.ACPTTNSHASHOPCD    -- 更新者ショップコード
                    , vRecordS04W003T.ACPTTNSHACD        -- 更新者コード
                    , gSystmYmd                          -- 更新システム日付
                    , gSystmJikan                        -- 更新システム時間
                    , gUnyoDate                          -- 更新運用日付
                    , gIpAdrse                           -- 更新ＩＰアドレス
                    , c_PROGRAMID                        -- 更新プログラムＩＤ
               );
           END LOOP;
        END IF;
      
        BEGIN
          SELECT ARAMUSETTEIACPTCNT
               , ACPTCNT
            INTO wAlarm
               , wCurrent
            FROM S04T007T 
           WHERE APKIHONCD     = zaikoVO.APKIHONCD
             AND SHOPCD        = zaikoVO.SHOPCD
             AND DLVRYKYOTENCD = zaikoVO.DLVRYKYOTENCD
             AND DLVRYAREACD   = zaikoVO.FOURTHKEY
             AND YMD           = zaikoVO.YMD
             AND YUKOKBN       = '1'
          ;
          EXCEPTION 
            WHEN NO_DATA_FOUND THEN 
              CONTINUE;
        END;
        --チェック処理
        IF zaikoVO.CHECKFG = true THEN
          IF wAlarm <> 0 AND wAlarm < wCurrent + zaikoVO.SURYO THEN
            ErrAdd(c_ERRKBN_WAR, 'WJ010:配達エリア受注アラーム数を過ぎています。');
          END IF;
        END IF;
        IF zaikoVO.MINUSFG = true THEN
          wUpdateSuryo := zaikoVO.SURYO * -1;
        ELSE
          wUpdateSuryo := zaikoVO.SURYO;
        END IF;
        --更新処理
        UPDATE S04T007T
           SET ACPTCNT = ACPTCNT + wUpdateSuryo
             , KOSHAAPKIHONCD  = vRecordS04W003T.ACPTTNSHAAPKIHONCD
             , KOSHASHOPCD     = vRecordS04W003T.ACPTTNSHASHOPCD
             , KOSHACD         = vRecordS04W003T.ACPTTNSHACD
             , KSHNSYSTMYMD    = gSystmYmd
             , KSHNSYSTMJIKAN  = gSystmJikan
             , KSHNUNYOYMD     = gUnyoDate
             , KSHNIPADRSE     = gIpadrse
             , KSHNPGMID       = c_PROGRAMID
         WHERE APKIHONCD     = zaikoVO.APKIHONCD
           AND SHOPCD        = zaikoVO.SHOPCD
           AND DLVRYKYOTENCD = zaikoVO.DLVRYKYOTENCD
           AND DLVRYAREACD   = zaikoVO.FOURTHKEY
           AND YMD           = zaikoVO.YMD
           AND YUKOKBN       = '1'
        ;
      END IF;
      
    END LOOP;
    
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20117, SQLERRM || '在庫チェック更新時エラーが発生しました。');
  END checkUpdateZaiko;

  /****************************************************************************/
  /* プロシージャ名：受注伝票更新                                             */
  /* 引数           ：iType            タイプ                                 */
  /*                ：iDenVO           伝票VO                                 */
  /****************************************************************************/
  PROCEDURE updateJuchuDenpyo( iType         IN VARCHAR2
                             , iDenVO        IN denVOtype
  )
  IS 
  BEGIN
    IF iType = c_CANCEL THEN
      UPDATE S04T001T
         SET DNPYSTS   = '09' --受注キャンセル
           , ACPTSTS   = '09' --受注キャンセル
           , DLVRYSTS  = '00' --設定なし
           , KOSHAAPKIHONCD  = vRecordS04W003T.ACPTTNSHAAPKIHONCD
           , KOSHASHOPCD     = vRecordS04W003T.ACPTTNSHASHOPCD
           , KOSHACD         = vRecordS04W003T.ACPTTNSHACD
           , KSHNSYSTMYMD    = gSystmYmd
           , KSHNSYSTMJIKAN  = gSystmJikan
           , KSHNUNYOYMD     = gUnyoDate
           , KSHNIPADRSE     = gIpadrse
           , KSHNPGMID       = c_PROGRAMID
       WHERE APKIHONCD   = iDenVO.APKIHONCD
         AND SHOPCD      = iDenVO.SHOPCD
         AND ACPTDNPYNO  = iDenVO.ACPTDNPYNO
         AND EDANO       = iDenVO.EDANO
      ;
      
      -- ACCIS2023-7326
      -- マイアクア連携受注取込で受注をキャンセルする場合、受注配達明細テーブルの更新関連項目を更新する
      -- 更新条件には受注伝票の主キー項目を指定し、紐づく受注配達明細を全て更新する
      UPDATE S04T002T
         SET KOSHAAPKIHONCD  = vRecordS04W003T.ACPTTNSHAAPKIHONCD
           , KOSHASHOPCD     = vRecordS04W003T.ACPTTNSHASHOPCD
           , KOSHACD         = vRecordS04W003T.ACPTTNSHACD
           , KSHNSYSTMYMD    = gSystmYmd
           , KSHNSYSTMJIKAN  = gSystmJikan
           , KSHNUNYOYMD     = gUnyoDate
           , KSHNIPADRSE     = gIpadrse
           , KSHNPGMID       = c_PROGRAMID
       WHERE APKIHONCD   = iDenVO.APKIHONCD
         AND SHOPCD      = iDenVO.SHOPCD
         AND ACPTDNPYNO  = iDenVO.ACPTDNPYNO
         AND EDANO       = iDenVO.EDANO
      ;
      
    ELSIF iType = c_DELETE THEN
      UPDATE S04T001T
         SET YUKOKBN = '0'
           , KOSHAAPKIHONCD  = vRecordS04W003T.ACPTTNSHAAPKIHONCD
           , KOSHASHOPCD     = vRecordS04W003T.ACPTTNSHASHOPCD
           , KOSHACD         = vRecordS04W003T.ACPTTNSHACD
           , KSHNSYSTMYMD    = gSystmYmd
           , KSHNSYSTMJIKAN  = gSystmJikan
           , KSHNUNYOYMD     = gUnyoDate
           , KSHNIPADRSE     = gIpadrse
           , KSHNPGMID       = c_PROGRAMID
       WHERE APKIHONCD   = iDenVO.APKIHONCD
         AND SHOPCD      = iDenVO.SHOPCD
         AND ACPTDNPYNO  = iDenVO.ACPTDNPYNO
         AND EDANO       = iDenVO.EDANO
      ;
    ELSE
      UPDATE S04T001T
         SET ACPTDD                   = vRecordS04W003T.ACPTDD
           , ACPTTNSHACD              = vRecordS04W003T.ACPTTNSHACD
           , DLVRYYTIDD               = vRecordS04W003T.DLVRYYTIDD
           , DLVRYDD                  = vRecordS04W003T.DLVRYYTIDD
           , DLVRYKIBOJIKAN           = vRecordS04W003T.DLVRYKIBOJIKANFROM
           , DLVRYKIBOJIKANSHRY       = vRecordS04W003T.DLVRYKIBOJIKANTO
           , DLVRYKIBOJIKANKBN        = vRecordS04W003T.DLVRYKIBOJIKANKBN
           , FZAIOKIKANOFLG           = vRecordS04W003T.FZAIOKIKANOFLG
           , AZUKARIBTLN1N2RITTORUHNS = iDenVO.AZUKARIBTLN1N2RITTORUHNS
           , KYBTLN1N2RITTORUHNS      = vRecordS04W003T.KYBTLN1N2RITTORUHNS
           , AZUKARIBTLN8RITTORUHNS   = iDenVO.AZUKARIBTLN8RITTORUHNS
           , KYBTLN8RITTORUHNS        = vRecordS04W003T.KYBTLN8RITTORUHNS
           , PRNTMEMON3               = vRecordS04W003T.PRNTMEMON3
           , SKSMOTOKANRIKBN          = vRecordS04W003T.SKSMOTOKANRIKBN
           , SKSMOTOKANRINO           = vRecordS04W003T.SKSMOTOKANRINO
           , DLVRYYTIRGRAYUSYOHNS     = iDenVO.DLVRYYTIRGRAYUSYOHNS
           , DLVRYYTIRGRAMUSHOHNS     = iDenVO.DLVRYYTIRGRAMUSHOHNS
           , DLVRYYTISRMYUSYOHNS      = iDenVO.DLVRYYTISRMYUSYOHNS
           , DLVRYYTISRMMUSHOHNS      = iDenVO.DLVRYYTISRMMUSHOHNS
           , DLVRYYTISONOTAYUSYOHNS   = iDenVO.DLVRYYTISONOTAYUSYOHNS
           , DLVRYYTISONOTAMUSHOHNS   = iDenVO.DLVRYYTISONOTAMUSHOHNS
           , DLVRYYTISEIHINMIZUURIKIN = iDenVO.DLVRYYTISEIHINMIZUURIKIN
           , DLVRYYTISONOTAURIKIN     = iDenVO.DLVRYYTISONOTAURIKIN
           , ACPTKIN                  = iDenVO.ACPTKIN
           , ACPTSHOHITAXKIN          = iDenVO.ACPTSHOHITAXKIN
-- インボイス対応追加 start ABI
           , ACPTIPPANNUKIKIN         = iDenVO.ACPTIPPANNUKIKIN
           , ACPTIPPANTAXKIN          = iDenVO.ACPTIPPANTAXKIN
           , ACPTKEIGENNUKIKIN        = iDenVO.ACPTKEIGENNUKIKIN
           , ACPTKEIGENTAXKIN         = iDenVO.ACPTKEIGENTAXKIN
           , ACPTHIKAZEIURIIKIN       = iDenVO.ACPTHIKAZEIURIIKIN
           , ACPTFUKAZEIURIIKIN       = iDenVO.ACPTFUKAZEIURIIKIN
-- インボイス対応追加 end ABI
           , ACPTRCPTKEIROKBN         = vRecordS04W003T.ACPTRCPTKEIROKBN
           , KOSHAAPKIHONCD  = vRecordS04W003T.ACPTTNSHAAPKIHONCD
           , KOSHASHOPCD     = vRecordS04W003T.ACPTTNSHASHOPCD
           , KOSHACD         = vRecordS04W003T.ACPTTNSHACD
           , KSHNSYSTMYMD    = gSystmYmd
           , KSHNSYSTMJIKAN  = gSystmJikan
           , KSHNUNYOYMD     = gUnyoDate
           , KSHNIPADRSE     = gIpadrse
           , KSHNPGMID       = c_PROGRAMID
       WHERE APKIHONCD   = iDenVO.APKIHONCD
         AND SHOPCD      = iDenVO.SHOPCD
         AND ACPTDNPYNO  = iDenVO.ACPTDNPYNO
         AND EDANO       = iDenVO.EDANO
      ;
    END IF;
    
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20118, SQLERRM || '受注伝票更新時エラーが発生しました。');
  END updateJuchuDenpyo;
  
  /****************************************************************************/
  /* プロシージャ名：受注伝票登録                                             */
  /* 引数           ：ioDenVO          伝票VO                                 */
  /*                ：iSaibanFg        採番フラグ                             */
  /****************************************************************************/
  PROCEDURE insertJuchuDenpyo( ioDenVO        IN OUT denVOtype
                             , iSaibanFg      IN BOOLEAN
  )
  IS 
  BEGIN
    IF iSaibanFg = true THEN
      --受注伝票採番
-- 本線対応変更 start ABI
--      S01SAIBAN.getSlipNoJuchu( ioDenVO.APKIHONCD, ioDenVO.SHOPCD, 1, ioDenVO.ACPTDNPYNO, ioDenVO.ACPTDNPYNO );
      Z1804B01.getSlipNoJuchu( ioDenVO.APKIHONCD, ioDenVO.SHOPCD, 1, ioDenVO.ACPTDNPYNO, ioDenVO.ACPTDNPYNO );
-- 本線対応変更 end ABI
    END IF;
    ioDenVO.EDANO := 0;
    
    --受注伝票登録
    INSERT INTO S04T001T
      ( APKIHONCD
      , SHOPCD
      , ACPTDNPYNO
      , EDANO
      , URIDNPYNO
      , DLVRYTRGTFLG
      , DNPYKBN
      , TORIHIKIKBN
      , ACPTKBN
      , ACPTKBNSHS
      , TOKUISAKICD
      , ACPTDD
      , ACPTTNSHAAPKIHONCD
      , ACPTTNSHASHOPCD
      , ACPTTNSHACD
      , DLVRYYTIDD
      , DLVRYJISSEKIDD
      , DLVRYDD
      , DLVRYKIBOJIKAN
      , DLVRYKIBOJIKANSHRY
      , DLVRYKIBOJIKANKBN
      , FZAIOKIKANOFLG
      , FZAIOKIJISSEKIFLG
      , DLVRYTNSHACD
      , DLVRYKANKATSUAPKIHONCD
      , DLVRYKANKATSUSHOPCD
      , DLVRYKYOTENCD
      , DLVRYAREACD
      , DLVRYKBN
      , DLVRYCMPNYCD
      , AZUKARIBTLN1N2RITTORUHNS
      , KYBTLN1N2RITTORUHNS
      , KZBTLN1N2RITTORUHNS
      , AZUKARIBTLN8RITTORUHNS
      , KYBTLN8RITTORUHNS
      , KZBTLN8RITTORUHNS
      , PRNTMEMON1
      , PRNTMEMON2
      , PRNTMEMON3
      , ACPTSHIMEPRCSDD
      , ACPTSHIMETNSHAAPKIHONCD
      , ACPTSHIMETNSHASHOPCD
      , ACPTSHIMEPRCSTNSHACD
      , DLVRYJISSEKIPRCSDD
      , DLVRYJISSEKIMONOAPKIHONCD
      , DLVRYJISSEKIMONOSHOPCD
      , DLVRYJISSEKIPRCSTNSHACD
      , URISHIMEPRCSDD
      , GETSUJISHIMEDYDM
      , GETSUJISHIMEDD
      , GETSUJIPRCSJISSIDD
      , DLVRYJUN
      , NOSHOPRNTTRGTFLG
      , NOSHOPRNTSUMIFLG
      , NOSHOPRNTDD
      , NOSHOHIKAEPRNTTRGTFLG
      , NOSHOHIKAEPRNTSUMIFLG
      , NOSHOHIKAEPRNTDD
      , NYKNHYOPRNTTRGTFLG
      , NYKNHYOPRNTSUMIFLG
      , NYKNHYOPRNTDD
      , WSVSIJISHOPRNTTRGTFLG
      , WSVSIJISHOPRNTSUMIFLG
      , WSVSIJISHOPRNTDD
-- インボイス対応追加 start ABI
      , SINGLDNPYFLG
-- インボイス対応追加 end ABI
      , DNPYSTS
      , ACPTSTS
      , DLVRYSTS
      , DATSKSMOTOKBN
      , SKSMOTOKANRIKBN
      , SKSMOTOKANRINO
      , ACPTHIKIATESAKIAPKIHONCD
      , ACPTHIKIATESAKISHOPCD
      , ACPTHIKIATESAKIKYOTENCD
      , DLVRYYTIRGRAYUSYOHNS
      , DLVRYYTIRGRAMUSHOHNS
      , DLVRYYTISRMYUSYOHNS
      , DLVRYYTISRMMUSHOHNS
      , DLVRYYTISONOTAYUSYOHNS
      , DLVRYYTISONOTAMUSHOHNS
      , DLVRYYTISEIHINMIZUURIKIN
      , DLVRYYTISONOTAURIKIN
      , DLVRYJISSEKIRGRAYUSYOHNS
      , DLVRYJISSEKIRGRAMUSHOHNS
      , DLVRYJISSEKISRMYUSYOHNS
      , DLVRYJISSEKISRMMUSHOHNS
      , DLVRYJISSEKISTYSHNS
      , DLVRYJISSEKISTMSHNS
      , DLVRYJISSEKISHSUAKIN
      , DLVRYJISSEKISONOTAURIKIN
      , ACPTKIN
      , ACPTSHOHITAXKIN
-- インボイス対応追加 start ABI
      , ACPTIPPANNUKIKIN
      , ACPTIPPANTAXKIN
      , ACPTKEIGENNUKIKIN
      , ACPTKEIGENTAXKIN
      , ACPTHIKAZEIURIIKIN
      , ACPTFUKAZEIURIIKIN
-- インボイス対応追加 end ABI
      , NOHINKIN
      , NOHINSHOHITAXKIN
-- インボイス対応追加 start ABI
      , NOHINIPPANNUKIKIN
      , NOHINIPPANTAXKIN
      , NOHINKEIGENNUKIKIN
      , NOHINKEIGENTAXKIN
      , NOHINHIKAZEIURIIKIN
      , NOHINFUKAZEIURIIKIN
-- インボイス対応追加 end ABI
      , KURERURITTORUSTUP
      , KURERURITTORUHIKIAGE
      , KURERUESUSTUP
      , KURERUESUHIKIAGE
      , SONOTASTUP
      , SONOTAHIKIAGE
      , TAXKEISANKBN
      , ZNKDLVRYHNS
      , JIKAIDLVRYYTIDD
      , ZNKSHIMEDD
      , ZNKKURIKOSHIKIN
      , ACPTTHDAIBIKIFLG
      , ACPTRCPTKEIROKBN
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
    VALUES 
      ( ioDenVO.APKIHONCD
      , ioDenVO.SHOPCD
      , ioDenVO.ACPTDNPYNO
      , ioDenVO.EDANO
      , ioDenVO.URIDNPYNO
      , ioDenVO.DLVRYTRGTFLG
      , ioDenVO.DNPYKBN
      , ioDenVO.TORIHIKIKBN
      , ioDenVO.ACPTKBN
      , ioDenVO.ACPTKBNSHS
      , ioDenVO.TOKUISAKICD
      , ioDenVO.ACPTDD
      , ioDenVO.ACPTTNSHAAPKIHONCD
      , ioDenVO.ACPTTNSHASHOPCD
      , ioDenVO.ACPTTNSHACD
      , ioDenVO.DLVRYYTIDD
      , ioDenVO.DLVRYJISSEKIDD
      , ioDenVO.DLVRYDD
      , ioDenVO.DLVRYKIBOJIKAN
      , ioDenVO.DLVRYKIBOJIKANSHRY
      , ioDenVO.DLVRYKIBOJIKANKBN
      , ioDenVO.FZAIOKIKANOFLG
      , ioDenVO.FZAIOKIJISSEKIFLG
      , ioDenVO.DLVRYTNSHACD
      , ioDenVO.DLVRYKANKATSUAPKIHONCD
      , ioDenVO.DLVRYKANKATSUSHOPCD
      , ioDenVO.DLVRYKYOTENCD
      , ioDenVO.DLVRYAREACD
      , ioDenVO.DLVRYKBN
      , ioDenVO.DLVRYCMPNYCD
      , ioDenVO.AZUKARIBTLN1N2RITTORUHNS
      , ioDenVO.KYBTLN1N2RITTORUHNS
      , ioDenVO.KZBTLN1N2RITTORUHNS
      , ioDenVO.AZUKARIBTLN8RITTORUHNS
      , ioDenVO.KYBTLN8RITTORUHNS
      , ioDenVO.KZBTLN8RITTORUHNS
      , ioDenVO.PRNTMEMON1
      , ioDenVO.PRNTMEMON2
      , ioDenVO.PRNTMEMON3
      , ioDenVO.ACPTSHIMEPRCSDD
      , ioDenVO.ACPTSHIMETNSHAAPKIHONCD
      , ioDenVO.ACPTSHIMETNSHASHOPCD
      , ioDenVO.ACPTSHIMEPRCSTNSHACD
      , ioDenVO.DLVRYJISSEKIPRCSDD
      , ioDenVO.DLVRYJISSEKIMONOAPKIHONCD
      , ioDenVO.DLVRYJISSEKIMONOSHOPCD
      , ioDenVO.DLVRYJISSEKIPRCSTNSHACD
      , ioDenVO.URISHIMEPRCSDD
      , ioDenVO.GETSUJISHIMEDYDM
      , ioDenVO.GETSUJISHIMEDD
      , ioDenVO.GETSUJIPRCSJISSIDD
      , ioDenVO.DLVRYJUN
      , ioDenVO.NOSHOPRNTTRGTFLG
      , ioDenVO.NOSHOPRNTSUMIFLG
      , ioDenVO.NOSHOPRNTDD
      , ioDenVO.NOSHOHIKAEPRNTTRGTFLG
      , ioDenVO.NOSHOHIKAEPRNTSUMIFLG
      , ioDenVO.NOSHOHIKAEPRNTDD
      , ioDenVO.NYKNHYOPRNTTRGTFLG
      , ioDenVO.NYKNHYOPRNTSUMIFLG
      , ioDenVO.NYKNHYOPRNTDD
      , ioDenVO.WSVSIJISHOPRNTTRGTFLG
      , ioDenVO.WSVSIJISHOPRNTSUMIFLG
      , ioDenVO.WSVSIJISHOPRNTDD
-- インボイス対応追加 start ABI
      , ioDenVO.SINGLDNPYFLG
-- インボイス対応追加 end ABI
      , ioDenVO.DNPYSTS
      , ioDenVO.ACPTSTS
      , ioDenVO.DLVRYSTS
      , ioDenVO.DATSKSMOTOKBN
      , ioDenVO.SKSMOTOKANRIKBN
      , ioDenVO.SKSMOTOKANRINO
      , ioDenVO.DLVRYKANKATSUAPKIHONCD
      , ioDenVO.DLVRYKANKATSUSHOPCD
      , ioDenVO.DLVRYKYOTENCD
      , ioDenVO.DLVRYYTIRGRAYUSYOHNS
      , ioDenVO.DLVRYYTIRGRAMUSHOHNS
      , ioDenVO.DLVRYYTISRMYUSYOHNS
      , ioDenVO.DLVRYYTISRMMUSHOHNS
      , ioDenVO.DLVRYYTISONOTAYUSYOHNS
      , ioDenVO.DLVRYYTISONOTAMUSHOHNS
      , ioDenVO.DLVRYYTISEIHINMIZUURIKIN
      , ioDenVO.DLVRYYTISONOTAURIKIN
      , ioDenVO.DLVRYJISSEKIRGRAYUSYOHNS
      , ioDenVO.DLVRYJISSEKIRGRAMUSHOHNS
      , ioDenVO.DLVRYJISSEKISRMYUSYOHNS
      , ioDenVO.DLVRYJISSEKISRMMUSHOHNS
      , ioDenVO.DLVRYJISSEKISTYSHNS
      , ioDenVO.DLVRYJISSEKISTMSHNS
      , ioDenVO.DLVRYJISSEKISHSUAKIN
      , ioDenVO.DLVRYJISSEKISONOTAURIKIN
      , ioDenVO.ACPTKIN
      , ioDenVO.ACPTSHOHITAXKIN
-- インボイス対応追加 start ABI
      , ioDenVO.ACPTIPPANNUKIKIN
      , ioDenVO.ACPTIPPANTAXKIN
      , ioDenVO.ACPTKEIGENNUKIKIN
      , ioDenVO.ACPTKEIGENTAXKIN
      , ioDenVO.ACPTHIKAZEIURIIKIN
      , ioDenVO.ACPTFUKAZEIURIIKIN
-- インボイス対応追加 end ABI
      , ioDenVO.NOHINKIN
      , ioDenVO.NOHINSHOHITAXKIN
-- インボイス対応追加 start ABI
      , ioDenVO.NOHINIPPANNUKIKIN
      , ioDenVO.NOHINIPPANTAXKIN
      , ioDenVO.NOHINKEIGENNUKIKIN
      , ioDenVO.NOHINKEIGENTAXKIN
      , ioDenVO.NOHINHIKAZEIURIIKIN
      , ioDenVO.NOHINFUKAZEIURIIKIN
-- インボイス対応追加 end ABI
      , ioDenVO.KURERURITTORUSTUP
      , ioDenVO.KURERURITTORUHIKIAGE
      , ioDenVO.KURERUESUSTUP
      , ioDenVO.KURERUESUHIKIAGE
      , ioDenVO.SONOTASTUP
      , ioDenVO.SONOTAHIKIAGE
      , ioDenVO.TAXKEISANKBN
      , ioDenVO.ZNKDLVRYHNS
      , ioDenVO.JIKAIDLVRYYTIDD
      , ioDenVO.ZNKSHIMEDD
      , ioDenVO.ZNKKURIKOSHIKIN
      , ioDenVO.ACPTTHDAIBIKIFLG
      , ioDenVO.ACPTRCPTKEIROKBN
      , '1'
      , vRecordS04W003T.ACPTTNSHAAPKIHONCD
      , vRecordS04W003T.ACPTTNSHASHOPCD
      , vRecordS04W003T.ACPTTNSHACD
      , gSystmYmd
      , gSystmJikan
      , gUnyoDate
      , gIpadrse
      , c_PROGRAMID
      , vRecordS04W003T.ACPTTNSHAAPKIHONCD
      , vRecordS04W003T.ACPTTNSHASHOPCD
      , vRecordS04W003T.ACPTTNSHACD
      , gSystmYmd
      , gSystmJikan
      , gUnyoDate
      , gIpadrse
      , c_PROGRAMID
      )
    ;
    
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20119, SQLERRM || '受注伝票登録時エラーが発生しました。');
  END insertJuchuDenpyo;
  
  /****************************************************************************/
  /* プロシージャ名：受注伝票明細登録                                         */
  /* 引数           ：iDenVO          伝票VO                                  */
  /*                ：iMeisaiList     明細リスト                              */
  /****************************************************************************/
  PROCEDURE insertJuchuMeisai( iDenVO         IN denVOtype
                             , iMeisaiList    IN meisaiListtype
  )
  IS 
  meiVO meisaiVOtype;
  initVO meisaiVOtype;
  BEGIN
    --旧受注明細を削除
    DELETE FROM S04T002T JHMT
     WHERE JHMT.APKIHONCD  = iDenVO.APKIHONCD
       AND JHMT.SHOPCD     = iDenVO.SHOPCD
       AND JHMT.ACPTDNPYNO = iDenVO.ACPTDNPYNO
       AND JHMT.EDANO      = iDenVO.EDANO
    ;
    
    --受注明細登録
    FOR i IN 1..iMeisaiList.COUNT LOOP
      meiVO := initVO;
      --値の設定
      meiVO.APKIHONCD           := iDenVO.APKIHONCD;
      meiVO.SHOPCD              := iDenVO.SHOPCD;
      meiVO.ACPTDNPYNO          := iDenVO.ACPTDNPYNO;
      meiVO.EDANO               := iDenVO.EDANO;
      meiVO.ACPTDNPYMEISAINO    := iMeisaiList(i).ACPTDNPYMEISAINO;
      meiVO.ACPTDNPYMEISAIEDANO := iMeisaiList(i).ACPTDNPYMEISAIEDANO;
      meiVO.DATSKSMOTOKBN       := iMeisaiList(i).DATSKSMOTOKBN;
      IF iMeisaiList(i).MEISAIKBN = c_MEISAI_HAIFU THEN
        meiVO.HAIFUBUTSUAPKIHONCD := iMeisaiList(i).HAIFUBUTSUAPKIHONCD;
        meiVO.HAIFUBUTSUSHOPCD    := iMeisaiList(i).HAIFUBUTSUSHOPCD;
        meiVO.HAIFUBUTSUCD        := iMeisaiList(i).HAIFUBUTSUCD;
        meiVO.ACPTSURYO           := iMeisaiList(i).ACPTSURYO;
        IF iDenVO.DATSKSMOTOKBN = '03' THEN
          meiVO.SOUKAISU := iMeisaiList(i).SOUKAISU;
          meiVO.ZANKAISU := iMeisaiList(i).ZANKAISU;
        END IF;
        meiVO.YUSYOKBN          := '0';
        meiVO.MEISAIKBN         := c_MEISAI_HAIFU;
        meiVO.NYURYOKUMEISAIFLG := '1';
        meiVO.TANPINMEISAIFLG   := '1';
      ELSE
        meiVO.LCTGRYCD  := iMeisaiList(i).LCTGRYCD;
        meiVO.MCTGRYCD  := iMeisaiList(i).MCTGRYCD;
        meiVO.SCTGRYCD  := iMeisaiList(i).SCTGRYCD;
        meiVO.SHNCD     := iMeisaiList(i).SHNCD;
        meiVO.KATASHIKI := iMeisaiList(i).KATASHIKI;
        meiVO.KEIJOKBN  := iMeisaiList(i).KEIJOKBN;
        meiVO.ACPTSURYO := iMeisaiList(i).ACPTSURYO;
        IF iDenVO.DATSKSMOTOKBN = '03' THEN
          meiVO.SOUKAISU := iMeisaiList(i).SOUKAISU;
          meiVO.ZANKAISU := iMeisaiList(i).ZANKAISU;
        END IF;
        meiVO.HYOJUNUNT := iMeisaiList(i).HYOJUNUNT;
        meiVO.ACPTUNT   := iMeisaiList(i).ACPTUNT;
        meiVO.TAXKBN    := iMeisaiList(i).TAXKBN;
        IF iMeisaiList(i).MEISAIKBN = c_MEISAI_TAX THEN
          meiVO.YUSYOKBN  := iMeisaiList(i).YUSYOKBN;
          meiVO.MEISAIKBN := iMeisaiList(i).MEISAIKBN;
        ELSE
          --計上区分 = "10:レンタル"の場合
          IF meiVO.KEIJOKBN IN ('10') THEN
            meiVO.YUSYOKBN := '3'; --レンタル
          --計上区分 = "01:売上", "02:キャンペーン"の場合
          ELSIF meiVO.KEIJOKBN IN ('01', '02') THEN
            meiVO.YUSYOKBN := '1'; --有償
          --それ以外
          ELSE
            meiVO.YUSYOKBN := '2'; --無償
          END IF;
          meiVO.MEISAIKBN := c_MEISAI_SHOHIN;
        END IF;
        meiVO.NYURYOKUMEISAIFLG  := iMeisaiList(i).NYURYOKUMEISAIFLG;
        meiVO.TANPINMEISAIFLG    := iMeisaiList(i).TANPINMEISAIFLG;
        meiVO.STSHNCD            := iMeisaiList(i).STSHNCD;
        meiVO.MEISAIBIKO         := iMeisaiList(i).MEISAIBIKO;
        meiVO.ACPTKIN            := iMeisaiList(i).ACPTKIN;
        meiVO.ACPTUNTSHOHITAXKIN := iMeisaiList(i).ACPTUNTSHOHITAXKIN;
        meiVO.ACPTSHOHITAXKIN    := iMeisaiList(i).ACPTSHOHITAXKIN;
      END IF;
      
      
      INSERT INTO S04T002T
        ( APKIHONCD
        , SHOPCD
        , ACPTDNPYNO
        , EDANO
        , ACPTDNPYMEISAINO
        , ACPTDNPYMEISAIEDANO
        , LCTGRYCD
        , MCTGRYCD
        , SCTGRYCD
        , SHNCD
        , HAIFUBUTSUAPKIHONCD
        , HAIFUBUTSUSHOPCD
        , HAIFUBUTSUCD
        , KATASIKI
        , KEIJOKBN
        , ACPTSURYO
        , HYOJUNUNT
        , ACPTUNT
        , ACPTKIN
        , ACPTUNTSHOHITAXKIN
        , ACPTSHOHITAXKIN
        , NOHINSURYO
        , NOHINUNT
        , NOHINKIN
        , NOHINUNTSHOHITAXKIN
        , NOHINSHOHITAXKIN
        , SOUKAISU
        , ZANKAISU
        , TAXKBN
        , YUSYOKBN
        , MEISAIKBN
        , NYURYOKUMEISAIFLG
        , TANPINMEISAIFLG
        , STSHNCD
        , MEISAIBIKO
        , DATSKSMOTOKBN
        , URIDNPYNO
        , URIDNPYEDANO
        , URIDNPYMEISAINO
        , URIDNPYMEISAIEDANO
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
      VALUES
        ( meiVO.APKIHONCD
        , meiVO.SHOPCD
        , meiVO.ACPTDNPYNO
        , meiVO.EDANO
        , meiVO.ACPTDNPYMEISAINO
        , meiVO.ACPTDNPYMEISAIEDANO
        , meiVO.LCTGRYCD
        , meiVO.MCTGRYCD
        , meiVO.SCTGRYCD
        , meiVO.SHNCD
        , meiVO.HAIFUBUTSUAPKIHONCD
        , meiVO.HAIFUBUTSUSHOPCD
        , meiVO.HAIFUBUTSUCD
        , meiVO.KATASHIKI
        , meiVO.KEIJOKBN
        , meiVO.ACPTSURYO
        , meiVO.HYOJUNUNT
        , meiVO.ACPTUNT
        , meiVO.ACPTKIN
        , meiVO.ACPTUNTSHOHITAXKIN
        , meiVO.ACPTSHOHITAXKIN
        , meiVO.NOHINSURYO
        , meiVO.NOHINUNT
        , meiVO.NOHINKIN
        , meiVO.NOHINUNTSHOHITAXKIN
        , meiVO.NOHINSHOHITAXKIN
        , meiVO.SOUKAISU
        , meiVO.ZANKAISU
        , meiVO.TAXKBN
        , meiVO.YUSYOKBN
        , meiVO.MEISAIKBN
        , meiVO.NYURYOKUMEISAIFLG
        , meiVO.TANPINMEISAIFLG
        , meiVO.STSHNCD
        , meiVO.MEISAIBIKO
        , meiVO.DATSKSMOTOKBN
        , meiVO.URIDNPYNO
        , meiVO.URIDNPYEDANO
        , meiVO.URIDNPYMEISAINO
        , meiVO.URIDNPYMEISAIEDANO
        , '1'
        , vRecordS04W003T.ACPTTNSHAAPKIHONCD
        , vRecordS04W003T.ACPTTNSHASHOPCD
        , vRecordS04W003T.ACPTTNSHACD
        , gSystmYmd
        , gSystmJikan
        , gUnyoDate
        , gIpadrse
        , c_PROGRAMID
        , vRecordS04W003T.ACPTTNSHAAPKIHONCD
        , vRecordS04W003T.ACPTTNSHASHOPCD
        , vRecordS04W003T.ACPTTNSHACD
        , gSystmYmd
        , gSystmJikan
        , gUnyoDate
        , gIpadrse
        , c_PROGRAMID
        )
      ;
    END LOOP;

    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20120, SQLERRM || '受注伝票明細登録時エラーが発生しました。');
  END insertJuchuMeisai;
  
  /****************************************************************************/
  /* プロシージャ名：配布物取込処理                                           */
  /* 引数           ：iCancelFg       キャンセルフラグ                        */
  /*                ：iDenVO          伝票VO                                  */
  /*                ：iMeisaiList     明細リスト                              */
  /****************************************************************************/
  PROCEDURE updateHaifubutsu( iCancelFlg     IN VARCHAR
                            , iDenVO         IN denVOtype
                            , iMeisaiList    IN meisaiListtype
  )
  IS 
  meiVO meisaiVOtype;
  BEGIN
    
    FOR i IN 1..iMeisaiList.COUNT LOOP
      meiVO := iMeisaiList(i);
      IF meiVO.MEISAIKBN = c_MEISAI_HAIFU THEN
        UPDATE S02M033T
           SET HAIFUTRGTSTS     = CASE WHEN iCancelFlg = '1' THEN '1' ELSE '2' END
             , ACPTDNPYNO       = CASE WHEN iCancelFlg = '1' THEN NULL ELSE iDenVO.ACPTDNPYNO END
             , EDANO            = CASE WHEN iCancelFlg = '1' THEN NULL ELSE iDenVO.EDANO END
             , ACPTDNPYMEISAINO = CASE WHEN iCancelFlg = '1' THEN NULL ELSE meiVO.ACPTDNPYMEISAINO END
             , ACPTTORIKOMIDD   = CASE WHEN iCancelFlg = '1' THEN NULL ELSE iDenVO.ACPTDD END
             , KOSHAAPKIHONCD  = vRecordS04W003T.ACPTTNSHAAPKIHONCD
             , KOSHASHOPCD     = vRecordS04W003T.ACPTTNSHASHOPCD
             , KOSHACD         = vRecordS04W003T.ACPTTNSHACD
             , KSHNSYSTMYMD    = gSystmYmd
             , KSHNSYSTMJIKAN  = gSystmJikan
             , KSHNUNYOYMD     = gUnyoDate
             , KSHNIPADRSE     = gIpadrse
             , KSHNPGMID       = c_PROGRAMID
         WHERE APKIHONCD    = iDenVO.APKIHONCD
           AND SHOPCD       = iDenVO.SHOPCD
           AND HAIFUBUTSUCD = meiVO.HAIFUBUTSUCD
           AND TOKUISAKICD  = iDenVO.TOKUISAKICD
        ;
      END IF;
    END LOOP;

    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20121, SQLERRM || '配布物取込処理時エラーが発生しました。');
  END updateHaifubutsu;
  
  /****************************************************************************/
  /* プロシージャ名：受注削除処理                                             */
  /* 引数           ：iYoyakuFg             予約フラグ                        */
  /*                ：iJuchuDenVO           伝票VO                            */
  /*                ：iMeisaiList           明細リスト                        */
  /*                ：iYoyakuList           予約明細番号リスト                */
  /****************************************************************************/
  PROCEDURE processJuchuCancel( iYoyakuFg        IN BOOLEAN
                              , iJuchuDenVO      IN denVOtype
                              , iMeisaiList      IN meisaiListtype
                              , iYoyakuList      IN yoyakuMeisaiNoListtype
  )
  IS 
  /* 変数宣言 */
  org_DenVO       denVOtype;
  org_MeisaiList  meisaiListtype;
  jutaku_DenVO    denVOtype;
  cancelZaikoList zaikokeyListtype;
  new_DenVO       denVOtype;
  new_MeisaiList  meisaiListtype;
  rycnt NUMBER(4,0) := 0; --レギュラー有償本数
  rmcnt NUMBER(4,0) := 0; --レギュラー無償本数
  sycnt NUMBER(4,0) := 0; --スリム有償本数
  smcnt NUMBER(4,0) := 0; --スリム有償本数
  oycnt NUMBER(4,0) := 0; --その他有償本数
  omcnt NUMBER(4,0) := 0; --その他有償本数
  wmn NUMBER(11,0) := 0; --製品水売上金額
  omn NUMBER(11,0) := 0; --その他売上金額
  wKyuminUpdFg BOOLEAN;
  BEGIN
    --既存伝票・明細取得処理
    getJuchuDenpyoMeisai(iJuchuDenVO.APKIHONCD, iJuchuDenVO.SHOPCD, iJuchuDenVO.ACPTDNPYNO, iJuchuDenVO.EDANO, iJuchuDenVO.TOKUISAKICD, org_DenVO, org_MeisaiList);
    
    IF iYoyakuFg = false THEN
      IF org_DenVO.KYOTENSTS = c_TAKYOTEN AND org_DenVO.ITAKUAITEACPTDNPYNO IS NOT NULL THEN
        --受託伝票取得処理
        loadJuchuDenpyo(org_DenVO.ITAKUAITEAPKIHONCD, org_DenVO.ITAKUAITESHOPCD, org_DenVO.ITAKUAITEACPTDNPYNO, org_DenVO.ITAKUAITEEDANO, jutaku_DenVO);
        IF jutaku_DenVO.DNPYSTS IN ('11', '12', '13', '14', '19') THEN
          ErrAdd(c_ERRKBN_ERR, 'EJ042:受託伝票が配達中、または配達結果が登録されています。');
        END IF;
      END IF;
      /* 得意先チェック更新処理 */
      IF gShoriType = c_UPDATE THEN
        wKyuminUpdFg := false;
      ELSE
        wKyuminUpdFg := true;
      END IF;
      checkUpdateTokuisaki(true, false, org_DenVO, org_MeisaiList, wKyuminUpdFg);
      
      IF org_DenVO.KYOTENSTS = c_JIKYOTEN THEN
        --在庫キャンセル処理
        createZaikoKeyList(true, false, org_DenVO, org_MeisaiList, cancelZaikoList);
        checkUpdateZaiko(cancelZaikoList);
      END IF;
      
      --配布物取込キャンセル処理
      updateHaifubutsu('1', org_DenVO, org_MeisaiList);
    END IF;
    
    IF gShoriType <> c_UPDATE THEN
      IF gShoriType = c_YOYAKUINSERT THEN
        --受注予約情報更新処理
        new_DenVO := org_DenVO;
        FOR i IN 1..org_MeisaiList.COUNT LOOP
          FOR j IN 1..iYoyakuList.COUNT LOOP
            IF iYoyakuList(j) = org_MeisaiList(i).ACPTDNPYMEISAINO THEN
              IF org_MeisaiList(i).ZANKAISU IS NOT NULL THEN
                org_MeisaiList(i).ZANKAISU := org_MeisaiList(i).ZANKAISU - 1;
              ELSE
                org_MeisaiList(i).ZANKAISU := 0;
              END IF;
            END IF;
          END LOOP;
          IF org_MeisaiList(i).ZANKAISU >= 1 THEN
            --明細リストに追加
            new_MeisaiList(new_MeisaiList.COUNT + 1) := org_MeisaiList(i);
          END IF;
        END LOOP;
        IF new_MeisaiList.COUNT >= 1 THEN
          --受注伝票番号を採番
-- 本線対応変更 start ABI
--          S01SAIBAN.getSlipNoJuchu( new_DenVO.APKIHONCD, new_DenVO.SHOPCD, 1, new_DenVO.ACPTDNPYNO, new_DenVO.ACPTDNPYNO );
          Z1804B01.getSlipNoJuchu( new_DenVO.APKIHONCD, new_DenVO.SHOPCD, 1, new_DenVO.ACPTDNPYNO, new_DenVO.ACPTDNPYNO );
-- 本線対応変更 end ABI
          FOR k IN 1..new_MeisaiList.COUNT LOOP
            /* 本数、金額の集計 */
            IF new_MeisaiList(k).TANPINMEISAIFLG = '1' AND new_MeisaiList(k).MEISAIKBN = c_MEISAI_SHOHIN THEN
              --本数集計処理
              IF isLegular(new_MeisaiList(k).SHNCD) THEN
                IF new_MeisaiList(k).KEIJOKBN IN ('01', '02') THEN
                  rycnt := rycnt + new_MeisaiList(k).ACPTSURYO;
                ELSE
                  rmcnt := rmcnt + new_MeisaiList(k).ACPTSURYO;
                END IF;
              ELSIF isSlim(new_MeisaiList(k).SHNCD) THEN
                IF new_MeisaiList(k).KEIJOKBN IN ('01', '02') THEN
                  sycnt := sycnt + new_MeisaiList(k).ACPTSURYO;
                ELSE
                  smcnt := smcnt + new_MeisaiList(k).ACPTSURYO;
                END IF;
              ELSIF new_MeisaiList(k).LCTGRYCD = gWaterCtgry THEN
                IF new_MeisaiList(k).KEIJOKBN IN ('01', '02') THEN
                  oycnt := oycnt + new_MeisaiList(k).ACPTSURYO;
                ELSE
                  omcnt := omcnt + new_MeisaiList(k).ACPTSURYO;
                END IF;
              END IF;
              --金額集計処理
              IF new_MeisaiList(k).LCTGRYCD = gWaterCtgry THEN
                wmn := wmn + new_MeisaiList(k).ACPTKIN;
              ELSE
                omn := omn + new_MeisaiList(k).ACPTKIN;
              END IF;
            END IF;
          END LOOP;
          --集計数量をセット
          new_DenVO.DLVRYYTIRGRAYUSYOHNS     := rycnt;
          new_DenVO.DLVRYYTIRGRAMUSHOHNS     := rmcnt;
          new_DenVO.DLVRYYTISRMYUSYOHNS      := sycnt;
          new_DenVO.DLVRYYTISRMMUSHOHNS      := smcnt;
          new_DenVO.DLVRYYTISONOTAYUSYOHNS   := oycnt;
          new_DenVO.DLVRYYTISONOTAMUSHOHNS   := omcnt;
          new_DenVO.DLVRYYTISEIHINMIZUURIKIN := wmn;
          new_DenVO.DLVRYYTISONOTAURIKIN     := omn;
          --受注伝票登録
          insertJuchuDenpyo(new_DenVO, false);
          --受注明細登録
          insertJuchuMeisai(new_DenVO, new_MeisaiList);
        END IF;
      END IF;
      --受注伝票の更新
      IF gShoriType = c_YOYAKUINSERT THEN
        updateJuchuDenpyo(c_DELETE, org_DenVO);
      ELSE
        updateJuchuDenpyo(c_CANCEL, org_DenVO);
      END IF;
    END IF;
    
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20122, SQLERRM || '受注削除処理時エラーが発生しました。');
  END processJuchuCancel;
  /****************************************************************************/
  /* プロシージャ名：受注削除処理                                             */
  /* 引数           ：iYoyakuFg             予約フラグ                        */
  /*                ：iJuchuDenVO           伝票VO                            */
  /*                ：iMeisaiList           明細リスト                        */
  /****************************************************************************/
  PROCEDURE processJuchuCancel( iYoyakuFg        IN BOOLEAN
                              , iJuchuDenVO      IN denVOtype
                              , iMeisaiList      IN meisaiListtype
  )  IS 
  /* 変数宣言 */
  wYoyakuList yoyakuMeisaiNoListtype;
  BEGIN
    processJuchuCancel(iYoyakuFg, iJuchuDenVO, iMeisaiList, wYoyakuList);
  END processJuchuCancel;

  
  /****************************************************************************/
  /* プロシージャ名：受注登録・更新処理                                        */
  /****************************************************************************/
  PROCEDURE processJuchuToroku
  IS 
  /* 変数宣言 */
  yoyakuMeisaiNoList yoyakuMeisaiNoListtype;
  rycnt NUMBER(4,0) := 0; --レギュラー有償本数
  rmcnt NUMBER(4,0) := 0; --レギュラー無償本数
  sycnt NUMBER(4,0) := 0; --スリム有償本数
  smcnt NUMBER(4,0) := 0; --スリム有償本数
  oycnt NUMBER(4,0) := 0; --その他有償本数
  omcnt NUMBER(4,0) := 0; --その他有償本数
  wmn NUMBER(11,0) := 0; --製品水売上金額
  omn NUMBER(11,0) := 0; --その他売上金額
  
  sumAcptTaxKin NUMBER(11,0);
  sumNohinTaxKin NUMBER(11,0);
  sumAiteTaxKin NUMBER(11,0);
  
  TYPE setListtype IS REF CURSOR;
  cur_set setListtype;
  TYPE childModeltype IS RECORD (
                           KOSEISHNCD            S02M008T.KOSEISHNCD%TYPE DEFAULT NULL
                         , KEIJOKBN              S02M008T.KEIJOKBN%TYPE DEFAULT NULL
                         , SURYO                 S02M008T.SURYO%TYPE DEFAULT 0
                         , DLVRYPRICE            S02M008T.DLVRYPRICE%TYPE DEFAULT 0
                         , MOCHIKAERIPRICE       S02M008T.MOCHIKAERIPRICE%TYPE DEFAULT 0
                         , LCTGRYCD              S02V001T.LCTGRYCD%TYPE DEFAULT NULL
                         , MCTGRYCD              S02V001T.MCTGRYCD%TYPE DEFAULT NULL
                         , SCTGRYCD              S02V001T.SCTGRYCD%TYPE DEFAULT NULL
                         , HYOJUNPRICEKIRIKAEDD  S02V001T.HYOJUNPRICEKIRIKAEDD%TYPE DEFAULT NULL
                         , GENZHYOJUNPRICE       S02V001T.GENZHYOJUNPRICE%TYPE DEFAULT 0
                         , SHINHYOJUNPRICE       S02V001T.SHINHYOJUNPRICE%TYPE DEFAULT 0
                         , GENZMOCHIKAERIPRICE   S02V001T.GENZMOCHIKAERIPRICE%TYPE DEFAULT 0
                         , SHINMOCHIKAERIPRICE   S02V001T.SHINMOCHIKAERIPRICE%TYPE DEFAULT 0
                         , ZAIKOKANRITRGTFLG     S02V001T.ZAIKOKANRITRGTFLG%TYPE DEFAULT NULL
                         , ZAIKOARAMUSURYO       S02V001T.ZAIKOARAMUSURYO%TYPE DEFAULT 0
                         , TAXKBN                S02V001T.TAXKBN%TYPE DEFAULT NULL
  );
  childModel childModeltype;
  childVO meisaiVOtype;
  loopcnt NUMBER(2,0) := 0;
  subList meisaiListtype;  -- 明細リスト（サブ）
  
  TYPE choseiModeltype IS RECORD (
                           SHNCD                 S02V001T.SHNCD%TYPE DEFAULT NULL
                         , LCTGRYCD              S02V001T.LCTGRYCD%TYPE DEFAULT NULL
                         , MCTGRYCD              S02V001T.MCTGRYCD%TYPE DEFAULT NULL
                         , SCTGRYCD              S02V001T.SCTGRYCD%TYPE DEFAULT NULL
                         , HYOJUNPRICEKIRIKAEDD  S02V001T.HYOJUNPRICEKIRIKAEDD%TYPE DEFAULT NULL
                         , GENZHYOJUNPRICE       S02V001T.GENZHYOJUNPRICE%TYPE DEFAULT 0
                         , SHINHYOJUNPRICE       S02V001T.SHINHYOJUNPRICE%TYPE DEFAULT 0
                         , GENZMOCHIKAERIPRICE   S02V001T.GENZMOCHIKAERIPRICE%TYPE DEFAULT 0
                         , SHINMOCHIKAERIPRICE   S02V001T.SHINMOCHIKAERIPRICE%TYPE DEFAULT 0
                         , TAXKBN                S02V001T.TAXKBN%TYPE DEFAULT NULL
                         , ZAIKOKANRITRGTFLG     S02V001T.ZAIKOKANRITRGTFLG%TYPE DEFAULT NULL
                         , ZAIKOARAMUSURYO       S02V001T.ZAIKOARAMUSURYO%TYPE DEFAULT 0
  );
  choseiModel choseiModeltype;
  choseichildVO meisaiVOtype;
  
  haifuVO meisaiVOtype;
  whaifu       VARCHAR2(6);
  whaifuApcd   VARCHAR2(3);
  whaifuShopcd VARCHAR2(3);
  
  org_DenVO       denVOtype;
  org_MeisaiList  meisaiListtype;
  cancelZaikoList zaikokeyListtype;
  updateZaikoList zaikokeyListtype;
  zaikoVO         zaikokeyVOtype;
  wHikiatesumiZaikokazu S13T004T.HIKIATESUMIZAIKOKAZU%type;
  wZaikoSuryo           S13T001T.ZAIKOSURYO%type;
  wAlarm   S04T005T.ARAMUSETTEIACPTSURYO%type;
  wCurrent S04T005T.ACPTKAZU%type;
  
  jutakuDenVO     denVOtype;
  
  maxmeisaicnt NUMBER(2,0) := 0;
  wUrikinhasukeisankbn      VARCHAR2(1)  := NULL;  -- 売上金額端数計算区分
-- インボイス対応追加 start ABI
  wInvoicshikoymd           VARCHAR2(8)  := NULL;  -- インボイス施行日
-- インボイス対応追加 end ABI
  
  BEGIN
    
    /* 受注予約を更新するリストを作成 */
    FOR i IN 1..meisaiList.COUNT LOOP
      IF meisaiList(i).DATSKSMOTOKBN = '03' THEN
        yoyakuMeisaiNoList(yoyakuMeisaiNoList.COUNT + 1) := meisaiList(i).ACPTDNPYMEISAINO;
      END IF;
    END LOOP;
    
    /* 在庫管理区分取得 */
    SELECT ZAIKOKANRIKBN
      INTO mainVO.ZAIKOKANRIKBN
      FROM S02M046T
     WHERE APKIHONCD = mainVO.APKIHONCD
       AND SHOPCD    = mainVO.SHOPCD
    ;

    -- ショップマスタ取得 
    SELECT
      SHPM.URIKINHASUKEISANKBN  -- 売上金額端数計算区分
-- インボイス対応追加 start ABI
    , SHPM.INVOICSHIKOYMD     -- インボイス施行日
-- インボイス対応追加 end ABI
    INTO wUrikinhasukeisankbn
-- インボイス対応追加 start ABI
       , wInvoicshikoymd
-- インボイス対応追加 end ABI
    FROM
      S02M046T SHPM 
    WHERE
      SHPM.APKIHONCD = mainVO.APKIHONCD 
      AND SHPM.SHOPCD = mainVO.SHOPCD  
      AND SHPM.YUKOKBN = '1'
    ;
    
    /* 明細リストをループ */
    FOR j IN 1..meisaiList.COUNT LOOP
      /* 明細情報設定 */
      meisaiList(j).ACPTDNPYMEISAINO := j; --受注明細番号
      meisaiList(j).NYURYOKUMEISAIFLG := '1'; --入力明細フラグ
      meisaiList(j).MEISAIKBN := c_MEISAI_SHOHIN; --明細区分
      
      /* 本数、金額の集計 */
      IF meisaiList(j).TANPINMEISAIFLG = '1' AND meisaiList(j).MEISAIKBN = c_MEISAI_SHOHIN THEN
        --本数集計処理
        IF isLegular(meisaiList(j).SHNCD) THEN
          IF meisaiList(j).KEIJOKBN IN ('01', '02') THEN
            rycnt := rycnt + meisaiList(j).ACPTSURYO;
          ELSE
            rmcnt := rmcnt + meisaiList(j).ACPTSURYO;
          END IF;
        ELSIF isSlim(meisaiList(j).SHNCD) THEN
          IF meisaiList(j).KEIJOKBN IN ('01', '02') THEN
            sycnt := sycnt + meisaiList(j).ACPTSURYO;
          ELSE
            smcnt := smcnt + meisaiList(j).ACPTSURYO;
          END IF;
        ELSIF meisaiList(j).LCTGRYCD = gWaterCtgry THEN
          IF meisaiList(j).KEIJOKBN IN ('01', '02') THEN
            oycnt := oycnt + meisaiList(j).ACPTSURYO;
          ELSE
            omcnt := omcnt + meisaiList(j).ACPTSURYO;
          END IF;
        END IF;
        --金額集計処理
        IF meisaiList(j).LCTGRYCD = gWaterCtgry THEN
          wmn := wmn + meisaiList(j).ACPTKIN;
        ELSE
          omn := omn + meisaiList(j).ACPTKIN;
        END IF;
      END IF;
      
      --入力行がセット商品の場合は展開する
      IF meisaiList(j).TANPINMEISAIFLG = '0' THEN
        meisaiList(j).STSHNCD := meisaiList(j).SHNCD;
        sumAcptTaxKin := 0;
        sumNohinTaxKin := 0;
        sumAiteTaxKin := 0;
        
        loopcnt := 0;
        /* 構成品リスト取得 */
        OPEN cur_set FOR 
          SELECT TKM.KOSEISHNCD
               , TKM.KEIJOKBN
               , TKM.SURYO
               , TKM.DLVRYPRICE
               , TKM.MOCHIKAERIPRICE
               , SHN.LCTGRYCD
               , SHN.MCTGRYCD
               , SHN.SCTGRYCD
               , SHN.HYOJUNPRICEKIRIKAEDD
               , SHN.GENZHYOJUNPRICE
               , SHN.SHINHYOJUNPRICE
               , SHN.GENZMOCHIKAERIPRICE
               , SHN.SHINMOCHIKAERIPRICE
               , SHN.ZAIKOKANRITRGTFLG
               , SHN.ZAIKOARAMUSURYO
               , SHN.TAXKBN
            FROM S02M008T TKM
            JOIN S02V001T SHN
              ON SHN.APKIHONCD    = mainVO.APKIHONCD
             AND SHN.SHOPCD       = mainVO.SHOPCD
             AND SHN.APKIHONCD = TKM.APKIHONCD
             AND SHN.SHNCD        = TKM.KOSEISHNCD
             AND SHN.SHOPYUKOKBN  = '1'
           WHERE TKM.STSHNCD = meisaiList(j).SHNCD
             AND TKM.YUKOKBN = '1'
           ORDER BY TKM.RENBAN
          ;
        LOOP FETCH cur_set INTO childModel;
          EXIT WHEN cur_set%NOTFOUND; 
          
          loopcnt := loopcnt + 1;
          /* 明細情報のセット */
          childVO := meisaiList(j);
          childVO.ACPTDNPYMEISAIEDANO := loopcnt;
          IF childVO.ITAKUAITEACPTDNPYMEISAIEDANO IS NOT NULL THEN
            childVO.ITAKUAITEACPTDNPYMEISAIEDANO := loopcnt;
          END IF;
          childVO.SHNCD := childModel.KOSEISHNCD;
          childVO.KATASHIKI := NULL;
          childVO.STSHNCD := meisaiList(j).SHNCD;
          childVO.NYURYOKUMEISAIFLG := '0';
          childVO.TANPINMEISAIFLG := '1';
          childVO.LCTGRYCD := childModel.LCTGRYCD;
          childVO.MCTGRYCD := childModel.MCTGRYCD;
          childVO.SCTGRYCD := childModel.SCTGRYCD;
-- 本線対応変更 start ABI
--          childVO.ACPTSURYO := S01SyohizeiService.roundValue(meisaiList(j).ACPTSURYO * childModel.SURYO, wUrikinhasukeisankbn);
          childVO.ACPTSURYO := Z1808B01.roundValue(meisaiList(j).ACPTSURYO * childModel.SURYO, wUrikinhasukeisankbn);
-- 本線対応変更 end ABI
          childVO.NOHINSURYO := 0;
          IF mainVO.DLVRYKBN = c_DLVRY_MOCHIKAERI THEN
            childVO.HYOJUNUNT := getCurrentValue(childModel.GENZMOCHIKAERIPRICE
                                               , childModel.SHINMOCHIKAERIPRICE
                                               , childModel.HYOJUNPRICEKIRIKAEDD
                                               , mainVO.DLVRYYTIDD);
          ELSE
            childVO.HYOJUNUNT := getCurrentValue(childModel.GENZHYOJUNPRICE
                                               , childModel.SHINHYOJUNPRICE
                                               , childModel.HYOJUNPRICEKIRIKAEDD
                                               , mainVO.DLVRYYTIDD);
          END IF;
          childVO.ZAIKOKANRITRGTFLG := childModel.ZAIKOKANRITRGTFLG;
          childVO.ZAIKOARAMUSURYO   := childModel.ZAIKOARAMUSURYO;
-- インボイス対応削除 start ABI
--          --親商品の税区分が「5:子明細商品」の場合、親の税区分を引き継がない
--          IF meisaiList(j).TAXKBN = '5' THEN
--            childVO.TAXKBN := childModel.TAXKBN;
--          END IF;
-- インボイス対応削除 end ABI
          --親商品が有料の場合は構成商品の計上区分をセットし、金額を算出する
          IF meisaiList(j).KEIJOKBN IN ('01', '02') THEN
            IF mainVO.DLVRYKBN = c_DLVRY_MOCHIKAERI THEN
              childVO.ACPTUNT := childModel.MOCHIKAERIPRICE;
            ELSE
              childVO.ACPTUNT := childModel.DLVRYPRICE;
            END IF;
            
            -- 適用期間内の得意先別セット商品単価がある場合は単価にセットする
            BEGIN
                 SELECT 
                     CASE 
                         WHEN mainVO.DLVRYKBN = '3' THEN TSTM.MOCHIKAERIPRICE           -- 持ち帰り価格
                         ELSE TSTM.DLVRYPRICE                                           -- 配達価格
                     END
                 INTO 
                     childVO.ACPTUNT
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
                         AND TSTM.SHNCD                  = childModel.KOSEISHNCD 
                         AND TSTM.YUKOKBN                = '1'
                     )
                 WHERE 
                     TSSM.APKIHONCD   = vRecordS04W003T.APKIHONCD                      -- ＡＰ基本コード
                 AND TSSM.SHOPCD      = vRecordS04W003T.SHOPCD                         -- ショップコード
                 AND TSSM.STSHNCD     = meisaiList(j).SHNCD                            -- セット商品コード
                 AND TSSM.TOKUISAKICD = vRecordS04W003T.TOKUISAKICD                    -- 得意先コード
                 AND mainVO.DLVRYYTIDD BETWEEN TSSM.TEKIYOSTRTDD AND TSSM.TEKIYOSHRYDD -- 配送予定日：適用期間内
                 AND TSSM.YUKOKBN     = '1' 
                 ;
            EXCEPTION 
              WHEN NO_DATA_FOUND THEN 
                 NULL;
              WHEN TOO_MANY_ROWS THEN
                 RAISE_APPLICATION_ERROR(-20015, '得意先別セット単価が複数見つかりました。');
            END;
            
            childVO.NOHINUNT := 0;
            childVO.KEIJOKBN := childModel.KEIJOKBN;
            IF childVO.TAXSETTEIKBN = '1' THEN
              getPriceList(childVO.ACPTUNT, childVO.ACPTSURYO, childVO.TAXKBN, childVO.TAXSETTEIKBN , childVO.SHNCD, childVO.KEIJOKBN, 
                           childVO.ACPTUNTSHOHITAXKIN, childVO.ACPTSHOHITAXKIN, childVO.ACPTKIN);
            ELSE
              childVO.ACPTUNTSHOHITAXKIN := 0;
              childVO.ACPTSHOHITAXKIN := 0;
-- 本線対応変更 start ABI
--              childVO.ACPTKIN := S01SyohizeiService.roundValue(childVO.ACPTUNT * childVO.ACPTSURYO, wUrikinhasukeisankbn);
              childVO.ACPTKIN := Z1808B01.roundValue(childVO.ACPTUNT * childVO.ACPTSURYO, wUrikinhasukeisankbn);
-- 本線対応変更 end ABI
            END IF;
            childVO.NOHINUNTSHOHITAXKIN := 0;
            childVO.NOHINSHOHITAXKIN := 0;
            childVO.NOHINKIN := 0;
            IF childVO.TAXSETTEIKBN = '1' AND childVO.TAXKBN NOT IN ('1','4','5') THEN
              childVO.ACPTUNTSHOHITAXKIN := 0;
              childVO.ACPTSHOHITAXKIN := 0;
            END IF;
          ELSE
            childVO.KEIJOKBN := meisaiList(j).KEIJOKBN;
            childVO.ACPTUNT := 0;
            childVO.ACPTUNTSHOHITAXKIN := 0;
            childVO.ACPTSHOHITAXKIN := 0;
            childVO.ACPTKIN := 0;
          END IF;
          
-- インボイス対応追加 start ABI
          -- 配達予定日<インボイス施行日の場合、セット商品親用の消費税合計変数に消費税を足しこむ
          IF mainVO.DLVRYYTIDD < wInvoicshikoymd THEN
-- インボイス対応追加 end ABI
            sumAcptTaxKin := sumAcptTaxKin + childVO.ACPTSHOHITAXKIN;
-- インボイス対応追加 start ABI
          ELSE
            childVO.ACPTSHOHITAXKIN := 0;
          END IF;
-- インボイス対応追加 end ABI
          
          --本数集計処理
          IF isLegular(childVO.SHNCD) THEN
            IF childVO.KEIJOKBN IN ('01', '02') THEN
              rycnt := rycnt + childVO.ACPTSURYO;
            ELSE
              rmcnt := rmcnt + childVO.ACPTSURYO;
            END IF;
          ELSIF isSlim(childVO.SHNCD) THEN
            IF childVO.KEIJOKBN IN ('01', '02') THEN
              sycnt := sycnt + childVO.ACPTSURYO;
            ELSE
              smcnt := smcnt + childVO.ACPTSURYO;
            END IF;
          ELSIF childVO.LCTGRYCD = gWaterCtgry THEN
            IF childVO.KEIJOKBN IN ('01', '02') THEN
              oycnt := oycnt + childVO.ACPTSURYO;
            ELSE
              omcnt := omcnt + childVO.ACPTSURYO;
            END IF;
          END IF;
          --金額集計処理
          IF childVO.LCTGRYCD = gWaterCtgry THEN
            wmn := wmn + childVO.ACPTKIN;
          ELSE
            omn := omn + childVO.ACPTKIN;
          END IF;
          /* 構成品リストに追加 */
          subList(subList.COUNT + 1) := childVO;
        END LOOP;
        CLOSE cur_set;

-- インボイス対応削除 start ABI
--        --親商品の税区分が「5:子明細商品」の場合、展開商品の消費税額集計を親商品の消費税額に設定
--        IF meisaiList(j).TAXKBN = '5' THEN
--          meisaiList(j).ACPTSHOHITAXKIN := sumAcptTaxKin;
--        END IF;
-- インボイス対応削除 end ABI
        
-- インボイス対応追加 start ABI
        -- 配達予定日がインボイス施行日前の場合、調整消費税を設定
        IF mainVO.DLVRYYTIDD < wInvoicshikoymd THEN
-- インボイス対応追加 end ABI
          /* 親商品の消費税額と展開商品の消費税額集計に差がある場合 */
          IF meisaiList(j).ACPTSHOHITAXKIN <> sumAcptTaxKin THEN
            --税調整商品コードを取得
            BEGIN
              SELECT SHN.SHNCD
                   , SHN.LCTGRYCD
                   , SHN.MCTGRYCD
                   , SHN.SCTGRYCD
                   , SHN.HYOJUNPRICEKIRIKAEDD
                   , SHN.GENZHYOJUNPRICE
                   , SHN.SHINHYOJUNPRICE
                   , SHN.GENZMOCHIKAERIPRICE
                   , SHN.SHINMOCHIKAERIPRICE
-- インボイス対応変更 start ABI
--                   , CASE WHEN SHN.TAXKBN IN ('4','5') AND mainVO.DLVRYYTIDD <= '20190930' THEN '1' ELSE SHN.TAXKBN END
                   , SHN.TAXKBN
-- インボイス対応変更 end ABI
                   , SHN.ZAIKOKANRITRGTFLG
                   , SHN.ZAIKOARAMUSURYO
                INTO choseiModel
                FROM S02M006T SKM
                JOIN S02V001T SHN
                  ON SHN.APKIHONCD = mainVO.APKIHONCD
                 AND SHN.SHOPCD    = mainVO.SHOPCD
                 AND SHN.SHNCD     = SKM.MOJISETTEIATAI
               WHERE SKM.APKIHONCD = c_SYS_APKIHONCD 
                 AND SKM.SHOPCD    = c_SYS_SHOPCD
                 AND SKM.SETTEINO  = '166' 
                 AND SKM.RCRDKBN   = 'M'
                 AND SKM.YUKOKBN   = '1'
              ;
              
              EXCEPTION 
                WHEN NO_DATA_FOUND THEN 
                  ErrAdd(c_ERRKBN_ERR, 'EJ999:調整商品が登録されていません。');
                  return;
            END;
            --明細情報を設定
            choseichildVO := meisaiList(j);
            choseichildVO.ACPTDNPYMEISAIEDANO := loopcnt + 1;
            choseichildVO.SHNCD := choseiModel.SHNCD;
            choseichildVO.KATASHIKI := NULL;
            choseichildVO.STSHNCD := meisaiList(j).SHNCD;
            choseichildVO.NYURYOKUMEISAIFLG := '0';
            choseichildVO.TANPINMEISAIFLG := '1';
            choseichildVO.LCTGRYCD := choseiModel.LCTGRYCD;
            choseichildVO.MCTGRYCD := choseiModel.MCTGRYCD;
            choseichildVO.SCTGRYCD := choseiModel.SCTGRYCD;
            choseichildVO.TAXKBN := meisaiList(j).TAXKBN;
            choseichildVO.MEISAIKBN := c_MEISAI_TAX;
            choseichildVO.YUSYOKBN := '0';
            choseichildVO.ACPTSURYO := 1;
            choseichildVO.NOHINSURYO := 0;
            IF mainVO.DLVRYKBN = c_DLVRY_MOCHIKAERI THEN
              choseichildVO.HYOJUNUNT := getCurrentValue(choseiModel.GENZMOCHIKAERIPRICE
                                                       , choseiModel.SHINMOCHIKAERIPRICE
                                                       , choseiModel.HYOJUNPRICEKIRIKAEDD
                                                       , mainVO.DLVRYYTIDD);
            ELSE
              choseichildVO.HYOJUNUNT := getCurrentValue(choseiModel.GENZHYOJUNPRICE
                                                       , choseiModel.SHINHYOJUNPRICE
                                                       , choseiModel.HYOJUNPRICEKIRIKAEDD
                                                       , mainVO.DLVRYYTIDD);
            END IF;
            choseichildVO.ZAIKOKANRITRGTFLG := choseiModel.ZAIKOKANRITRGTFLG;
            choseichildVO.ZAIKOARAMUSURYO   := choseiModel.ZAIKOARAMUSURYO;
            --親商品が有料の場合は構成商品の計上区分をセットし、金額を算出する
            IF meisaiList(j).KEIJOKBN IN ('01', '02') THEN
              choseichildVO.ACPTUNT := 0;
              choseichildVO.ACPTKIN := 0;
              choseichildVO.ACPTUNTSHOHITAXKIN := 0;
              choseichildVO.ACPTSHOHITAXKIN := meisaiList(j).ACPTSHOHITAXKIN - sumAcptTaxKin;
              choseichildVO.NOHINUNT := 0;
              choseichildVO.NOHINKIN := 0;
              choseichildVO.NOHINUNTSHOHITAXKIN := 0;
              choseichildVO.NOHINSHOHITAXKIN := 0;
              choseichildVO.KEIJOKBN := meisaiList(j).KEIJOKBN;
              
              subList(subList.COUNT + 1) := choseichildVO;
            END IF;
          END IF;
-- インボイス対応追加 start ABI
        END IF;
-- インボイス対応追加 end ABI
      END IF;
    END LOOP;
    
    /* 構成品リストを明細リストに追加 */
    IF subList.COUNT <> 0 THEN
      FOR k IN 1..subList.COUNT LOOP
        meisaiList(meisaiList.COUNT + 1) := subList(k);
      END LOOP;
    END IF;
    
    /* 配布物を明細リストに追加 */
    FOR m IN 1..meisaiList.COUNT LOOP
      IF maxmeisaicnt < meisaiList(m).ACPTDNPYMEISAINO THEN
        maxmeisaicnt := meisaiList(m).ACPTDNPYMEISAINO;
      END IF;
    END LOOP;
    
    FOR l IN 1..4 LOOP
      CASE l 
        WHEN 1 THEN
          whaifu         := mainVO.HAIFUBUTSUCD1;
          whaifuApcd     := mainVO.HAIFUBUTSUAPKIHONCD1;
          whaifuShopcd   := mainVO.HAIFUBUTSUSHOPCD1;
        WHEN 2 THEN
          whaifu         := mainVO.HAIFUBUTSUCD2;
          whaifuApcd     := mainVO.HAIFUBUTSUAPKIHONCD2;
          whaifuShopcd   := mainVO.HAIFUBUTSUSHOPCD2;
        WHEN 3 THEN
          whaifu         := mainVO.HAIFUBUTSUCD3;
          whaifuApcd     := mainVO.HAIFUBUTSUAPKIHONCD3;
          whaifuShopcd   := mainVO.HAIFUBUTSUSHOPCD3;
        WHEN 4 THEN
          whaifu         := mainVO.HAIFUBUTSUCD4;
          whaifuApcd     := mainVO.HAIFUBUTSUAPKIHONCD4;
          whaifuShopcd   := mainVO.HAIFUBUTSUSHOPCD4;
        ELSE
          NULL;
      END CASE;
      IF whaifu IS NOT NULL THEN
        haifuVO.APKIHONCD           := mainVO.APKIHONCD;
        haifuVO.SHOPCD              := mainVO.SHOPCD;
        haifuVO.ACPTDNPYNO          := mainVO.ACPTDNPYNO;
        haifuVO.EDANO               := mainVO.EDANO;
        haifuVO.ACPTDNPYMEISAINO    := maxmeisaicnt + 1;
        haifuVO.DATSKSMOTOKBN       := mainVO.DATSKSMOTOKBN;
        haifuVO.HAIFUBUTSUAPKIHONCD := whaifuApcd;
        haifuVO.HAIFUBUTSUSHOPCD    := whaifuShopcd;
        haifuVO.HAIFUBUTSUCD        := whaifu;
        haifuVO.ACPTSURYO           := 1;
        haifuVO.MEISAIKBN           := c_MEISAI_HAIFU;
        haifuVO.YUSYOKBN            := '0';
        haifuVO.NYURYOKUMEISAIFLG   := '1';
        haifuVO.TANPINMEISAIFLG     := '1';
        meisaiList(meisaiList.COUNT + 1) := haifuVO;
        maxmeisaicnt := maxmeisaicnt + 1;
      END IF;
    END LOOP;
    
    --集計数量をセット
    mainVO.DLVRYYTIRGRAYUSYOHNS     := rycnt;
    mainVO.DLVRYYTIRGRAMUSHOHNS     := rmcnt;
    mainVO.DLVRYYTISRMYUSYOHNS      := sycnt;
    mainVO.DLVRYYTISRMMUSHOHNS      := smcnt;
    mainVO.DLVRYYTISONOTAYUSYOHNS   := oycnt;
    mainVO.DLVRYYTISONOTAMUSHOHNS   := omcnt;
    mainVO.DLVRYYTISEIHINMIZUURIKIN := wmn;
    mainVO.DLVRYYTISONOTAURIKIN     := omn;
    
    IF gShoriType in (c_YOYAKUINSERT, c_UPDATE) THEN
      /* 受注伝票・明細情報を取得 */
      getJuchuDenpyoMeisai(mainVO.APKIHONCD, mainVO.SHOPCD, mainVO.ACPTDNPYNO, mainVO.EDANO, mainVO.TOKUISAKICD, org_DenVO, org_MeisaiList);
    END IF;
    /* 在庫更新リストを取得 */
    createZaikoKeyList(false, true, mainVO, meisaiList, updateZaikoList);
    
    --受注削除処理
    IF gShoriType = c_UPDATE THEN
      processJuchuCancel(false, org_DenVO, org_MeisaiList);
    ELSIF gShoriType = c_YOYAKUINSERT THEN
      processJuchuCancel(true, org_DenVO, org_MeisaiList, yoyakuMeisaiNoList);
    END IF;
    
    /* 得意先チェック更新処理 */
    checkUpdateTokuisaki(false, true, mainVO, meisaiList, true);
    
    IF gErrFlg = '0' THEN
      --自拠点の場合
      IF mainVO.KYOTENSTS = c_JIKYOTEN THEN
        /* 在庫チェック更新 */
        checkUpdateZaiko(updateZaikoList);
      --他拠点の場合
      ELSE
        IF updateZaikoList.COUNT > 0 THEN
          FOR m in 1..updateZaikoList.COUNT LOOP
            zaikoVO := updateZaikoList(m);
            --在庫引当テーブル
            IF zaikoVO.MODELTYPE = 'S13T004T' THEN
              BEGIN
                SELECT HIKIATESUMIZAIKOKAZU 
                  INTO wHikiatesumiZaikokazu
                  FROM S13T004T
                 WHERE APKIHONCD     = mainVO.DLVRYKANKATSUAPKIHONCD
                   AND SHOPCD        = mainVO.DLVRYKANKATSUSHOPCD
                   AND DLVRYKYOTENCD = zaikoVO.DLVRYKYOTENCD
                   AND SHNCD         = zaikoVO.FOURTHKEY
                ;

                --チェック処理
                IF zaikoVO.CHECKFG = true THEN
                  SELECT
                    NVL(SUM(GZKT.ZAIKOSURYO), 0) ZAIKOSURYO
                    INTO wZaikoSuryo
                  FROM
                    S13T001T GZKT
                  LEFT OUTER JOIN
                    S02M041T SKOM
                    ON (SKOM.APKIHONCD = GZKT.APKIHONCD
                    AND SKOM.SHOPCD    = GZKT.SHOPCD
                    AND SKOM.SOKOCD    = GZKT.SOKOCD)
                  WHERE
                       GZKT.APKIHONCD = zaikoVO.APKIHONCD
                   AND GZKT.SHOPCD    = zaikoVO.SHOPCD
                   AND GZKT.SHNCD     = zaikoVO.FOURTHKEY
                   AND GZKT.YUKOKBN   = '1'
                  GROUP BY
                    SKOM.DLVRYKYOTENCD
                  HAVING
                    SKOM.DLVRYKYOTENCD = zaikoVO.DLVRYKYOTENCD
                  ;
                  
                  IF zaikoVO.ARAMUSURYO <> 0 AND zaikoVO.ARAMUSURYO > wZaikoSuryo - wHikiatesumiZaikokazu - zaikoVO.SURYO THEN
                    ErrAdd(c_ERRKBN_WAR, 'WJ011:商品コード「' || zaikoVO.FOURTHKEY || '」は、委託先の在庫数量が不足しています。');
                  END IF;
                END IF;
                
                EXCEPTION 
                  WHEN NO_DATA_FOUND THEN 
                    CONTINUE;
              END;
            ELSIF zaikoVO.MODELTYPE = 'S04T005T' THEN
              BEGIN
                SELECT ARAMUSETTEIACPTSURYO
                     , ACPTKAZU
                  INTO wAlarm
                     , wCurrent
                 FROM S04T005T 
                WHERE APKIHONCD     = zaikoVO.APKIHONCD
                  AND SHOPCD        = zaikoVO.SHOPCD
                  AND DLVRYKYOTENCD = zaikoVO.DLVRYKYOTENCD
                  AND SHNCD         = zaikoVO.FOURTHKEY
                  AND YMD           = zaikoVO.YMD
                  AND YUKOKBN       = '1'
                ;
                EXCEPTION 
                  WHEN NO_DATA_FOUND THEN 
                    CONTINUE;
              END;
              --チェック処理
              IF zaikoVO.CHECKFG = true THEN
                IF wAlarm <> 0 AND wAlarm < wCurrent + zaikoVO.SURYO THEN
                  ErrAdd(c_ERRKBN_WAR, 'WJ012:商品コード「' || zaikoVO.FOURTHKEY || '」が委託先の配達拠点受注アラーム数を過ぎています。');
                END IF;
              END IF;
            ELSIF zaikoVO.MODELTYPE = 'S04T006T' THEN
              BEGIN
                SELECT HKJA.ARAMUSETTEIACPTCNT
                     , HKJA.ACPTCNT
                  INTO wAlarm
                     , wCurrent
                  FROM S04T006T HKJA 
                  JOIN S02M013T TSM
                    ON TSM.APKIHONCD      = HKJA.APKIHONCD
                   AND TSM.SHOPCD         = HKJA.SHOPCD
                   AND TSM.TOKUISAKICD    = mainVO.ITAKUAITETOKUISAKICD
                   AND TSM.YUKOKBN        = '1'
                 WHERE HKJA.APKIHONCD     = mainVO.ITAKUAITEAPKIHONCD
                   AND HKJA.SHOPCD        = mainVO.ITAKUAITESHOPCD
                   AND HKJA.DLVRYKYOTENCD = zaikoVO.DLVRYKYOTENCD
                   AND HKJA.TNSHACD       = TSM.DLVRYTNSHACD
                   AND HKJA.YMD           = zaikoVO.YMD
                   AND HKJA.YUKOKBN       = '1'
                ;
                EXCEPTION 
                  WHEN NO_DATA_FOUND THEN 
                    CONTINUE;
              END;
              --チェック処理
              IF zaikoVO.CHECKFG = true THEN
                IF wAlarm <> 0 AND wAlarm < wCurrent + zaikoVO.SURYO THEN
                  ErrAdd(c_ERRKBN_WAR, 'WJ013:委託先の配達担当者受注アラーム数を過ぎています。');
                END IF;
              END IF;
            ELSIF zaikoVO.MODELTYPE = 'S04T007T' THEN
              BEGIN
                SELECT ARAMUSETTEIACPTCNT
                     , ACPTCNT
                  INTO wAlarm
                     , wCurrent
                  FROM S04T007T 
                 WHERE APKIHONCD     = zaikoVO.APKIHONCD
                   AND SHOPCD        = zaikoVO.SHOPCD
                   AND DLVRYKYOTENCD = zaikoVO.DLVRYKYOTENCD
                   AND DLVRYAREACD   = zaikoVO.FOURTHKEY
                   AND YMD           = zaikoVO.YMD
                   AND YUKOKBN       = '1'
                ;
                EXCEPTION 
                  WHEN NO_DATA_FOUND THEN 
                    CONTINUE;
              END;
              --チェック処理
              IF zaikoVO.CHECKFG = true THEN
                IF wAlarm <> 0 AND wAlarm < wCurrent + zaikoVO.SURYO THEN
                  ErrAdd(c_ERRKBN_WAR, 'WJ014:委託先の配達エリア受注アラーム数を過ぎています。');
                END IF;
              END IF;
            END IF;
          END LOOP;
        END IF;
        IF gShoriType = c_UPDATE THEN
          --受託伝票ステータスチェック
          loadJuchuDenpyo(mainVO.ITAKUAITEAPKIHONCD, mainVO.ITAKUAITESHOPCD, mainVO.ITAKUAITEACPTDNPYNO, mainVO.ITAKUAITEEDANO, jutakuDenVO);
          IF jutakuDenVO.DNPYSTS IN ('11', '12', '13', '14', '19') THEN
            ErrAdd(c_ERRKBN_ERR, 'EJ042:受託伝票が配達中、または配達結果が登録されています。');
          END IF;
        END IF;
      END IF;
    END IF;
    
    IF gErrFlg = '0' THEN
      IF gShoriType = c_UPDATE THEN
        /* 受注伝票更新 */
        updateJuchuDenpyo(c_UPDATE, mainVO);
      ELSE
        /* 受注伝票登録 */
        insertJuchuDenpyo(mainVO, true);
        --エラーテーブル更新用にセット
        vRecordS04W003T.ACPTDNPYNO := mainVO.ACPTDNPYNO;
        vRecordS04W003T.EDANO := mainVO.EDANO;
      END IF;
      /* 受注明細登録 */
      insertJuchuMeisai(mainVO, meisaiList);
      
      /* 配布物取込処理 */
      updateHaifubutsu('0', mainVO, meisaiList);
      
      IF gShoriType IN (c_INSERT, c_YOYAKUINSERT) AND mainVO.KYOTENSTS = c_TAKYOTEN THEN
        C0804B01.HonbuJuchuDatRenkei(mainVO.APKIHONCD, mainVO.SHOPCD, mainVO.ACPTDNPYNO, mainVO.EDANO, '0');
      ELSIF gShoriType = c_UPDATE AND mainVO.KYOTENSTS = c_TAKYOTEN THEN
        --受託伝票ステータスチェック
        loadJuchuDenpyo(mainVO.ITAKUAITEAPKIHONCD, mainVO.ITAKUAITESHOPCD, mainVO.ITAKUAITEACPTDNPYNO, mainVO.ITAKUAITEEDANO, jutakuDenVO);
        IF jutakuDenVO.DNPYSTS IN ('11', '12', '13', '14', '19') THEN
          ErrAdd(c_ERRKBN_ERR, 'EJ042:受託伝票が配達中、または配達結果が登録されています。');
        ELSE
          C0804B01.HonbuJuchuDatRenkei(mainVO.APKIHONCD, mainVO.SHOPCD, mainVO.ACPTDNPYNO, mainVO.EDANO, '1');
        END IF;
      END IF;
    END IF;
    
    EXCEPTION 
      WHEN OTHERS THEN 
        RAISE_APPLICATION_ERROR( -20123, SQLERRM || '受注登録・更新処理時エラーが発生しました。');
  END processJuchuToroku;
  
  /****************************************************************************/
  /* プロシージャ名 ：マイアクア連携受注取込処理                              */
  /****************************************************************************/
  PROCEDURE MyAquaRenkeiJuchuTorikomi( pRenkeiJohokbn    IN VARCHAR2 -- 連携情報区分
  )
  IS
  
  /* 変数宣言 */
  wSubOrderKensakuFlg VARCHAR2(1) := '0'; -- 関連オーダーID検索フラグ
  wYoyakuGyoFlg VARCHAR2(1)       := '0'; --予約行存在フラグ
  
  CURSOR SELECT_REGULAR IS
  SELECT SHN.SHNCD
    FROM S02M007T SHN
    JOIN S02M006T SYSM_WATER 
      ON SYSM_WATER.APKIHONCD      = c_SYS_APKIHONCD 
     AND SYSM_WATER.SHOPCD         = c_SYS_SHOPCD
     AND SYSM_WATER.SETTEINO       = '096' 
     AND SYSM_WATER.RCRDKBN        = 'M'
     AND SYSM_WATER.SETTEICD       = '1' 
     AND SYSM_WATER.YUKOKBN        = '1' 
     AND SYSM_WATER.MOJISETTEIATAI = SHN.LCTGRYCD
    JOIN S02M006T SYSM_M 
      ON SYSM_M.APKIHONCD      = c_SYS_APKIHONCD 
     AND SYSM_M.SHOPCD         = c_SYS_SHOPCD
     AND SYSM_M.SETTEINO       = '134' 
     AND SYSM_M.RCRDKBN        = 'M'
     AND SYSM_M.SETTEICD       = '11' 
     AND SYSM_M.YUKOKBN        = '1' 
     AND SYSM_M.MOJISETTEIATAI = SHN.MCTGRYCD
   WHERE SHN.APKIHONCD = '001'
     AND SHN.STKBN     = '0'
     AND SHN.SCTGRYCD  = '001' --レギュラー
     AND SHN.YUKOKBN   = '1'
  ;

  CURSOR SELECT_SLIM IS
  SELECT SHN.SHNCD
    FROM S02M007T SHN
    JOIN S02M006T SYSM_WATER 
      ON SYSM_WATER.APKIHONCD      = c_SYS_APKIHONCD 
     AND SYSM_WATER.SHOPCD         = c_SYS_SHOPCD
     AND SYSM_WATER.SETTEINO       = '096' 
     AND SYSM_WATER.RCRDKBN        = 'M'
     AND SYSM_WATER.SETTEICD       = '1' 
     AND SYSM_WATER.YUKOKBN        = '1' 
     AND SYSM_WATER.MOJISETTEIATAI = SHN.LCTGRYCD
    JOIN S02M006T SYSM_M 
      ON SYSM_M.APKIHONCD      = c_SYS_APKIHONCD 
     AND SYSM_M.SHOPCD         = c_SYS_SHOPCD
     AND SYSM_M.SETTEINO       = '134' 
     AND SYSM_M.RCRDKBN        = 'M'
     AND SYSM_M.SETTEICD       = '11' 
     AND SYSM_M.YUKOKBN        = '1' 
     AND SYSM_M.MOJISETTEIATAI = SHN.MCTGRYCD
   WHERE SHN.APKIHONCD = '001'
     AND SHN.STKBN     = '0'
     AND SHN.SCTGRYCD  = '002' --スリム
     AND SHN.YUKOKBN   = '1'
  ;
  wShncd VARCHAR2(6);
  
  CURSOR SELECT_DATSKSMOTOKBN IS
  SELECT SYSM.SETTEICD
    FROM S02M006T SYSM 
   WHERE SYSM.APKIHONCD      = c_SYS_APKIHONCD 
     AND SYSM.SHOPCD         = c_SYS_SHOPCD
     AND SYSM.SETTEINO       = '093' 
     AND SYSM.RCRDKBN        = 'M'
     AND SYSM.YUKOKBN        = '1' 
  ;
  wDatsksmotokbn VARCHAR2(2);
  
  /* 対象データ抽出 */
  CURSOR SELECT_WORK IS
    SELECT *
      FROM S04W003T                  -- マイアクア受注連携ワーク
     WHERE ACCISPRCSJOTAIKBN = '1'  -- ACCIS処理状態区分
     ORDER BY APKIHONCD, SHOPCD, TOKUISAKICD, JOTAIHENKOYOKYUDDTH, ORDERID
    ;
    
  jutakuDenVO     denVOtype;
  
  shopModel S02M046T%rowtype;
  keisankbn S02M046T.GENZTAXKEISANKBNURI%type;
  
  BEGIN
    
    /* 運用日付取得 */
    gUnyoDate := S01GetOperationdate(c_SYS_APKIHONCD, c_SYS_SHOPCD);
    
    --IPアドレスを取得
    gIpAdrse := S01GetIpAddress;
    
    --レギュラー・スリムの商品リストを作成
    OPEN SELECT_REGULAR;
    LOOP FETCH SELECT_REGULAR INTO wShncd;
      EXIT WHEN SELECT_REGULAR%NOTFOUND; 
      regularList(regularList.COUNT + 1) := wShncd;
    END LOOP;
    CLOSE SELECT_REGULAR;
    
    OPEN SELECT_SLIM;
    LOOP FETCH SELECT_SLIM INTO wShncd;
      EXIT WHEN SELECT_SLIM%NOTFOUND; 
      slimList(slimList.COUNT + 1) := wShncd;
    END LOOP;
    CLOSE SELECT_SLIM;
    
    --製品水の大分類コードを取得
    SELECT MOJISETTEIATAI
      INTO gWaterCtgry
      FROM S02M006T  
     WHERE APKIHONCD = c_SYS_APKIHONCD 
       AND SHOPCD    = c_SYS_SHOPCD
       AND SETTEINO  = '096' 
       AND RCRDKBN   = 'M'
       AND SETTEICD  = '1' 
       AND YUKOKBN   = '1'
    ;
    
    --データ作成元区分のリストを取得
    OPEN SELECT_DATSKSMOTOKBN;
    LOOP FETCH SELECT_DATSKSMOTOKBN INTO wDatsksmotokbn;
      EXIT WHEN SELECT_DATSKSMOTOKBN%NOTFOUND; 
      datsksmotokbnList(datsksmotokbnList.COUNT + 1) := wDatsksmotokbn;
    END LOOP;
    CLOSE SELECT_DATSKSMOTOKBN;

    OPEN SELECT_WORK;
    LOOP FETCH SELECT_WORK INTO vRecordS04W003T;
      
      EXIT WHEN SELECT_WORK%NOTFOUND; 
      
      --システム時間を取得
-- 本線対応変更 start ABI
      --SELECT TO_CHAR(SYSDATE, 'YYYYMMDD')
      --     , TO_CHAR(SYSDATE, 'HH24MISS')
      SELECT TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
           , TO_CHAR(CURRENT_DATE, 'HH24MISS')
-- 本線対応変更 end ABI
        INTO gSystmYmd
           , gSystmJikan
        FROM DUAL
      ;
      --エラーフラグを初期化
      gErrFlg := '0';
      ErrList.DELETE;
      wSubOrderKensakuFlg := '0';
      wYoyakuGyoFlg := '0';
      --伝票VOを初期化
      mainVO := initVO;
      meisaiList.DELETE;
      
      /* 関連オーダーID検索 */
      IF vRecordS04W003T.JOTAIHENKOYOKYU IN (c_HENKOYOKYU_UPD, c_HENKOYOKYU_DEL) AND vRecordS04W003T.ACPTDNPYNO IS NULL THEN
        BEGIN
          wSubOrderKensakuFlg := '1';
          
          SELECT ACPTDNPYNO
                ,EDANO
            INTO vRecordS04W003T.ACPTDNPYNO
                ,vRecordS04W003T.EDANO
            FROM S04W003T
           WHERE ORDERID = vRecordS04W003T.SUBORDERID
          ;
          EXCEPTION 
            WHEN NO_DATA_FOUND THEN 
              --関連オーダーIDチェック
              ErrAdd(c_ERRKBN_ERR, 'EJ001:関連オーダーIDが無効です。');
        END;
        
        IF vRecordS04W003T.ACPTDNPYNO IS NULL THEN
          --受注伝票番号チェック
          ErrAdd(c_ERRKBN_ERR, 'EJ002:受注伝票番号が取得できません。');
        END IF;
        
      END IF;
      
      /* 基本チェック */
      kihonChk;
      
      /* 受注情報変換 */
      IF gErrFlg = '0' THEN
        
        --新規登録の場合
        IF vRecordS04W003T.JOTAIHENKOYOKYU = c_HENKOYOKYU_INS THEN
          --AP/ショップ/得意先コードを設定
          mainVO.APKIHONCD   := vRecordS04W003T.APKIHONCD;
          mainVO.SHOPCD      := vRecordS04W003T.SHOPCD;
          mainVO.TOKUISAKICD := vRecordS04W003T.TOKUISAKICD;
          
          --新規登録の場合
          getYoyaku;
          
          IF mainVO.YOYAKUFLG = '0' THEN
            gShoriType := c_INSERT;
          ELSE
            gShoriType := c_YOYAKUINSERT;
          END IF;
          
          --得意先配達情報を伝票VOに設定
          loadTokuisakiHaitatsuInfo(mainVO.APKIHONCD, mainVO.SHOPCD, mainVO.TOKUISAKICD, mainVO.YOYAKUFLG, mainVO, true);
          
          --受注日
          mainVO.ACPTDD := vRecordS04W003T.ACPTDD;
          --配達予定日
          mainVO.DLVRYYTIDD := vRecordS04W003T.DLVRYYTIDD;
          
          --税計算区分
          SELECT GENZTAXKEISANKBNURI
               , SHINTAXKEISANKBNURI
               , TAXKEISANKBNURIKIRIKAEDD 
            INTO shopModel.GENZTAXKEISANKBNURI
               , shopModel.SHINTAXKEISANKBNURI
               , shopModel.TAXKEISANKBNURIKIRIKAEDD
            FROM S02M046T
           WHERE APKIHONCD = mainVO.APKIHONCD
             AND SHOPCD    = mainVO.SHOPCD
          ;
-- インボイス対応変更 start ABI
--          keisankbn := getCurrentValue(shopModel.GENZTAXKEISANKBNURI, shopModel.SHINTAXKEISANKBNURI, shopModel.TAXKEISANKBNURIKIRIKAEDD, mainVO.ACPTDD);
          keisankbn := getCurrentValue(shopModel.GENZTAXKEISANKBNURI, shopModel.SHINTAXKEISANKBNURI, shopModel.TAXKEISANKBNURIKIRIKAEDD, mainVO.DLVRYYTIDD);
-- インボイス対応変更 end ABI
          mainVO.TAXKEISANKBN := CASE WHEN mainVO.TOKUISAKIKBN IN ('2', '3', '4', '5', '6') THEN '2' ELSE keisankbn END; --得意先が卸対象の場合は外税固定
          
          --受注配達明細の読み込み
          loadJuchuMeisai(wYoyakuGyoFlg);
          
          --拠点ステータス
          IF mainVO.TOKUISAKIKBN = c_TSK_KOKYAKU AND mainVO.JUTAKUDLVRYCSTKBN IN ( c_JDCK_NAKOKYAKU, c_JDCK_ITAKUKOKYAKU ) THEN
            mainVO.KYOTENSTS := c_TAKYOTEN; --他拠点
          ELSE
            mainVO.KYOTENSTS := c_JIKYOTEN; --自拠点
            --委託相手情報を削除
            mainVO.ITAKUAITEAPKIHONCD := NULL;
            mainVO.ITAKUAITESHOPCD := NULL;
            mainVO.ITAKUAITETOKUISAKICD := NULL;
          END IF;
          
          --ワークの情報を伝票VOに設定
          createJuchuHead;
          
          --ワークの情報を明細リストに追加
          createJuchuMeisai;
          
          --配布物を設定
          getTokuisakiHaifubutsu;
          
        --更新・削除の場合
        ELSIF vRecordS04W003T.JOTAIHENKOYOKYU IN (c_HENKOYOKYU_UPD, c_HENKOYOKYU_DEL) THEN
          
          --受注伝票の読み込み
          loadJuchuDenpyo(vRecordS04W003T.APKIHONCD, vRecordS04W003T.SHOPCD, vRecordS04W003T.ACPTDNPYNO, vRecordS04W003T.EDANO, mainVO);
          
          --伝票有効性チェック
          IF gErrFlg = '0' AND mainVO.YUKOKBN <> '1' OR mainVO.DNPYSTS = '09' THEN
            ErrAdd(c_ERRKBN_ERR, 'EJ045:既に受注伝票が削除されています。');
          ELSIF gErrFlg = '0' THEN
            
            --伝票ステータチェック
            IF mainVO.DNPYSTS IN ('11', '12', '13', '14', '19') THEN
              ErrAdd(c_ERRKBN_ERR, 'EJ027:既に配達予定データが作成されています。');
            END IF;
            
            --得意先配達情報を伝票VOに設定
            loadTokuisakiHaitatsuInfo(mainVO.APKIHONCD, mainVO.SHOPCD, mainVO.TOKUISAKICD, mainVO.YOYAKUFLG, mainVO, true);
            
            --受注日
            mainVO.ACPTDD := vRecordS04W003T.ACPTDD;
            --配達予定日
            mainVO.DLVRYYTIDD := vRecordS04W003T.DLVRYYTIDD;
            
            --税計算区分
            SELECT GENZTAXKEISANKBNURI
                 , SHINTAXKEISANKBNURI
                 , TAXKEISANKBNURIKIRIKAEDD 
              INTO shopModel.GENZTAXKEISANKBNURI
                 , shopModel.SHINTAXKEISANKBNURI
                 , shopModel.TAXKEISANKBNURIKIRIKAEDD
              FROM S02M046T
             WHERE APKIHONCD = mainVO.APKIHONCD
               AND SHOPCD    = mainVO.SHOPCD
            ;
-- インボイス対応変更 start ABI
--            keisankbn := getCurrentValue(shopModel.GENZTAXKEISANKBNURI, shopModel.SHINTAXKEISANKBNURI, shopModel.TAXKEISANKBNURIKIRIKAEDD, mainVO.ACPTDD);
            keisankbn := getCurrentValue(shopModel.GENZTAXKEISANKBNURI, shopModel.SHINTAXKEISANKBNURI, shopModel.TAXKEISANKBNURIKIRIKAEDD, mainVO.DLVRYYTIDD);
-- インボイス対応変更 end ABI
            mainVO.TAXKEISANKBN := CASE WHEN mainVO.TOKUISAKIKBN IN ('2', '3', '4', '5', '6') THEN '2' ELSE keisankbn END; --得意先が卸対象の場合は外税固定
            
            --受注配達明細の読み込み
            loadJuchuMeisai(wYoyakuGyoFlg);
            
            --予約伝票変更チェック１
            IF wSubOrderKensakuFlg = '1' AND wYoyakuGyoFlg = '1' THEN
              ErrAdd(c_ERRKBN_ERR, 'EJ003:受注予約が紐づいた伝票を作成中に更新されました。');
            END IF;
            
            --旧明細を削除
            meisaiList.DELETE;
            
            --ワークの情報を伝票VOに設定
            createJuchuHead;
            
            --ワークの情報を明細リストに追加
            createJuchuMeisai;
          
          END IF;
          
        END IF;
        
        IF gErrFlg = '0' AND mainVO.KYOTENSTS = c_TAKYOTEN THEN
          --他拠点チェック
          takyotenChk;
        END IF;
        
        IF gErrFlg = '0' AND vRecordS04W003T.JOTAIHENKOYOKYU IN (c_HENKOYOKYU_INS, c_HENKOYOKYU_UPD) THEN
          --受注伝票チェック
          denpyoChk;
        END IF;
        
      END IF;
      
      /* 金額計算処理 */
      IF gErrFlg = '0' THEN
        kingakuCalc;
      END IF;
      
      
      IF gErrFlg = '0' THEN
        --登録、更新の場合
        IF vRecordS04W003T.JOTAIHENKOYOKYU IN (c_HENKOYOKYU_INS, c_HENKOYOKYU_UPD) THEN
          IF vRecordS04W003T.JOTAIHENKOYOKYU = c_HENKOYOKYU_INS AND mainVO.YOYAKUFLG = '0' THEN
            gShoriType := c_INSERT;
          ELSIF vRecordS04W003T.JOTAIHENKOYOKYU = c_HENKOYOKYU_INS AND mainVO.YOYAKUFLG = '1' THEN
            gShoriType := c_YOYAKUINSERT;
          ELSE
            gShoriType := c_UPDATE;
          END IF;
          
          --受注登録・更新処理
          processJuchuToroku;
          
        --削除の場合
        ELSE
          gShoriType := c_CANCEL;
          
          --受注削除処理
          processJuchuCancel(false, mainVO, meisaiList);
          
          IF gErrFlg = '0' THEN
            IF mainVO.KYOTENSTS = c_TAKYOTEN THEN
              --受託伝票ステータスチェック
              loadJuchuDenpyo(mainVO.ITAKUAITEAPKIHONCD, mainVO.ITAKUAITESHOPCD, mainVO.ITAKUAITEACPTDNPYNO, mainVO.ITAKUAITEEDANO, jutakuDenVO);
              IF jutakuDenVO.DNPYSTS IN ('11', '12', '13', '14', '19') THEN
                ErrAdd(c_ERRKBN_ERR, 'EJ042:受託伝票が配達中、または配達結果が登録されています。');
              ELSE
                C0804B01.HonbuJuchuDatRenkei(mainVO.APKIHONCD, mainVO.SHOPCD, mainVO.ACPTDNPYNO, mainVO.EDANO, '2');
              END IF;
            END IF;
          END IF;
          
        END IF;
      END IF;
      
      --エラーが存在する場合
      IF gErrFlg = '1' THEN
        ROLLBACK;
      END IF;
      IF ErrList.COUNT > 0 THEN
        --エラーテーブルに書き込み
        ErrIns;
      END IF;
      
      /* 処理状態更新 */
      UPDATE S04W003T
         SET ACCISPRCSJOTAIKBN = '2'
           , ACPTDNPYNO        = CASE WHEN ACPTDNPYNO IS NULL THEN vRecordS04W003T.ACPTDNPYNO ELSE ACPTDNPYNO END
           , EDANO             = CASE WHEN ACPTDNPYNO IS NULL THEN vRecordS04W003T.EDANO ELSE EDANO END
           , JOTAIHENKODDTH    = TO_TIMESTAMP(gSystmYmd || gSystmJikan,'YYYYMMDDHH24MISS')
       WHERE ORDERID = vRecordS04W003T.ORDERID
      ;
      
      COMMIT;
      
    END LOOP;
    CLOSE SELECT_WORK;
    
  EXCEPTION
    WHEN OTHERS THEN 
      ROLLBACK;
      -- エラーログ出力
      S99_CHECK_LOG( 'C0808B01', vRecordS04W003T.APKIHONCD, vRecordS04W003T.SHOPCD
              , '1', 'E', '9', null, null 
              , vRecordS04W003T.ACPTTNSHAAPKIHONCD, vRecordS04W003T.ACPTTNSHASHOPCD, vRecordS04W003T.ACPTTNSHACD
              , gIpAdrse, c_PROGRAMID 
              , ' ORDERID '|| vRecordS04W003T.ORDERID || '：' || SQLERRM );
      RAISE_APPLICATION_ERROR(-20705, SQLERRM);
      
  END MyAquaRenkeiJuchuTorikomi;
  

END C0808B01;
/
exit;
