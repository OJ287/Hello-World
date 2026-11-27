CREATE OR REPLACE PACKAGE BODY        B0602B01
/*******************************************************************************
 * システム名       ：ＡＰ総合管理システム
 * 機能名           ：NA請求抽出処理
 *
 ******************************************************************************T
   Date       Author         Changes Key                                 Version
   -----------------------------------------------------------------------------
   2022-05-16 ABI)Yokota       Create                                      1.0.0
 ******************************************************************************/
 AS
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
) RETURN NUMBER IS returnValue NUMBER(10,2);
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

-- 本線対応変更 start ABI
--PROCEDURE getNaDate( iApkihoncd    IN VARCHAR2 /*ＡＰ基本コード*/
PROCEDURE getNaData( iApkihoncd    IN VARCHAR2 /*ＡＰ基本コード*/
-- 本線対応変更 end ABI
                     , iShopcd       IN VARCHAR2 /*ショップコード*/
-- インボイス対応削除 start ABI
--                     , iSeikyusakicd IN VARCHAR2 /*請求先コード*/
-- インボイス対応削除 end ABI
                     , iPrcsym       IN VARCHAR2 /*締年月*/
                     , iShimedayi    IN VARCHAR2 /*締日*/
                     , iChargeShacd  IN VARCHAR2) /*担当者コード*/
  IS

   /* 定数*/
  PGMID             VARCHAR2(8)   :=  'B0602B01';
  /* 変数*/
  NaAciApkihoncd     S02M057T.ACIAPKIHONCD%type;     /*NA請求マスタ.ACIAP基本コード*/
  NaAciShopcd        S02M057T.ACISHOPCD%type;        /*NA請求マスタ.ACIショップコード*/
  NaAciSiiresakicd   S02M057T.ACISIIRESAKICD%type;   /*NA請求マスタ.ACI仕入先コード*/
  NaAciTokuisakicd   S02M057T.ACITOKUISAKICD%type;   /*NA請求マスタ.ACI得意先コード*/
  NaApApkihoncd      S02M057T.APAPKIHONCD%type;      /*NA請求マスタ.APAP基本コード*/
  NaApShopcd         S02M057T.APSHOPCD%type;         /*NA請求マスタ.APショップコード*/
  NaApSkysakicd      S02M057T.APSKYSAKICD%type;      /*NA請求マスタ.AP請求先コード*/
  NaApTokuisakicd    S02M057T.APTOKUISAKICD%type;    /*NA請求マスタ.AP得意先コード*/

  TEigyoTnshacd      S02M013T.EIGYOTNSHACD%type;     /*得意先マスタ.営業担当者コード*/
  TSkysakicd        S02M013T.SKYSAKICD%type;        /*得意先マスタ.請求先コード*/
-- インボイス対応追加 start ABI
  TTokuisakikbn      S02M013T.TOKUISAKIKBN%type;      /*得意先マスタ.得意先区分*/
  TJutakudlvrycstkbn S02M013T.JUTAKUDLVRYCSTKBN%type; /*得意先マスタ.受託配達顧客区分*/
-- インボイス対応追加 end ABI

  ShopShinTax   S02M046T.SHINTAXKEISANKBNURI%type;     /*ショップマスタ.新税計算区分売上*/
  ShopGenTax    S02M046T.GENZTAXKEISANKBNURI%type;     /*ショップマスタ.現税計算区分売上*/
  ShopTaxKbn    S02M046T.TAXKEISANKBNURIKIRIKAEDD%type;  /*ショップマスタ.税計算区分売上切替日*/

-- インボイス対応削除 start ABI
--  SiReKeisanKbn S02M021T.TAXKEISANKBN%type;            /*仕入先マスタ.税計算区分*/
--  SiReKeisanHo  S02M021T.KEISANHOHO%type;            /*仕入先マスタ.計算方法*/
--  SiReTaxKbn    S02M021T.TAXKBN%type;               /*仕入先マスタ.税区分*/
-- インボイス対応削除 end ABI

  SkySkysakicd  S02M017T.SKYSAKICD%type;            /*請求先マスタ.請求先コード*/
  SkySkynm      S02M017T.SKYSAKINMN1%type;           /*請求先マスタ.請求先名称*/

  Uriage             S06T001T%ROWTYPE;         /*売上テーブル*/
  ShnShnzokusei      S02V001T.SHNZOKUSEI%type;       /*商品マスタビュー.商品属性*/
  ShnTaxkbn          S02V001T.TAXKBN%type;           /*商品マスタビュー.税区分*/
  ShnTaxSetteikbn    S02V001T.TAXSETTEIKBN%type;     /*商品マスタビュー.税設定区分*/

  AciShnCnt    NUMBER   := 0;      /*商品マスタビュー件数（ACI側）*/
  WKUpdCnt     NUMBER   := 0;

  sUnyoDate   VARCHAR2(20)  := NULL;    /* 運用日付*/
  sIpAddress  VARCHAR2(20) := NULL;    /* IPアドレス*/

  GetKingaku     NUMBER  := 0;      /*金額*/
  SumKingaku       NUMBER  := 0;      /*金額の合計*/
  GetShohikingaku  NUMBER  := 0;      /*消費税金額*/
  SumShohiKingaku  NUMBER  := 0;      /*消費税金額の合計*/
-- インボイス対応削除 start ABI
--  SumSiireKingaku        NUMBER  := 0;      /*仕入金額の合計*/
--  SumSiireShohiKingaku  NUMBER  := 0;      /*仕入消費税金額の合計*/
-- インボイス対応削除 end ABI
  DlvryPrice     NUMBER;            /*単価*/
  rUridnpyoStart   VARCHAR2(11);      /*売上伝票番号開始値*/
  rUridnpyoEnd     VARCHAR2(11);      /*売上伝票番号終了値*/
  rShoiTaxKin     NUMBER  := 0;      /*消費税金額*/
  rUntShohiTaxKin  NUMBER  := 0;      /*単価消費税金額*/
  UDnpyoFlg       BOOLEAN  := FALSE;    /*ACIテーブル作成フラグ*/
  ErrorFlg       BOOLEAN  := FALSE;    /*本部商品判別フラグ*/
  MeiCreateFlg     BOOLEAN  := FALSE;    /*明細テーブル作成フラグ*/
  DeleteFlg       BOOLEAN  := FALSE;    /*テーブル削除フラグ*/
  TaxKBN       VARCHAR2(1);        /*税計算区分*/
  NaSeikyuzumi S07W007T%ROWTYPE;  /*NA請求抽出済ワーク*/
-- インボイス対応削除 start ABI
--  wSiireMeisaiNo      S11T002T.SIIREDNPYMEISAINO%type;
-- インボイス対応削除 end ABI
  wTaxChoseiDnpyNo    S06T002T.URIDNPYNO%type;
  wTaxChoseiEdaNo     S06T002T.EDANO%type;
  wTaxChoseiMeisaiNo  S06T002T.URIDNPYMEISAINO%type;
-- インボイス対応削除 start ABI
--  wTaxChoseiOyaTax    S06T002T.SHOHITAXKIN%type;
--  wTaxChoseiKoTax     S06T002T.SHOHITAXKIN%type;
-- インボイス対応削除 end ABI
  wOyaTaxKBN          S02V001T.TAXKBN%type;        /*（セット親）税区分*/
  wRecTaxKBN          S02V001T.TAXKBN%type;        /*（登録用）税区分*/
  wChoseiMeisai       S06T002T%ROWTYPE;         /*売上明細テーブル*/
  wShnErrFlg          BOOLEAN := FALSE;         /* 商品エラーフラグ */
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
  wChoseiModel choseiModeltype;
  
  /*NA請求抽出済ワークより伝票番号の取得*/
  CURSOR cNazumiwork IS SELECT *
               FROM   S07W007T
              WHERE
                  ACIAPKIHONCD     =  NaAciApkihoncd   /*ACIAP基本コード*/
              AND ACISHOPCD        =  NaAciShopcd      /*ACIショップコード*/
              AND APAPKIHONCD      =  NaApApkihoncd   /*APAP基本コード*/
              AND APSHOPCD         =  NaApShopcd      /*APショップコード*/
              AND APTOKUISAKICD    =  NaApTokuisakicd /*AP得意先コード*/
              AND SHIMEDYDM        =  iPrcsym;        /*請求締年月*/

    /*売上テーブルよりデータ抽出*/
  CURSOR cUriage IS SELECT *
              FROM
                   S06T001T S06
              WHERE
                S06.APKIHONCD          =  NaApApkihoncd    /*AP基本コード*/
                AND S06.SHOPCD         =  NaApShopcd       /*ショップコード*/
                AND S06.TOKUISAKICD    =  NaApTokuisakicd  /*得意先コード*/
                AND S06.SKYSAKICD      =  NaApSkysakicd    /*請求先コード*/
                AND S06.SKYSHIMEDYDM   =  iPrcsym           /*請求締年月*/
                AND NOT EXISTS (
                      SELECT 1 FROM S07W007T S07
                      WHERE S07.APAPKIHONCD = S06.APKIHONCD
                      AND   S07.APSHOPCD    = S06.SHOPCD
                      AND   S07.SHIMEDYDM   = S06.SKYSHIMEDYDM
                      AND   S07.DNPYNO      = S06.URIDNPYNO
                    );

BEGIN
  /* DBサーバーのIPアドレス取得*/
  sIpAddress := S01GetIpAddress();

  /*AP商品エラーワークの全削除*/
-- 本線対応変更 start ABI
  --DELETE FROM S07W005T_NA
  DELETE FROM S07W005T
-- 本線対応変更 end ABI
   WHERE ACIAPKIHONCD = iApkihoncd
     AND ACISHOPCD    = iShopcd;

  /*NA請求得意先実績ワークの全削除*/
  DELETE FROM S07W008T
   WHERE ACIAPKIHONCD = iApkihoncd
     AND ACISHOPCD    = iShopcd;
WKUpdCnt := SQL%ROWCOUNT;
S99_CHECK_LOG('B0602B01', iApkihoncd, iShopcd, '1', 'D', '1', 'S07W008T', WKUpdCnt, null, null, null, null, null, 'NA請求得意先実績ワークの全削除');

-- 本線対応変更 start ABI
  --FOR vRec IN (SELECT * FROM S07W004T_NA
  FOR vRec IN (SELECT * FROM S07W004T
-- 本線対応変更 end ABI
                WHERE ACIAPKIHONCD = iApkihoncd
                  AND ACISHOPCD = iShopcd) LOOP

    /* 運用日付取得*/
    sUnyoDate  := S01GetOperationdate(vRec.APAPKIHONCD, vRec.APSHOPCD);
    /*NA請求マスタより該当データの取得*/
    BEGIN
      SELECT ACIAPKIHONCD
          ,ACISHOPCD
          ,ACISIIRESAKICD
          ,ACITOKUISAKICD
          ,APAPKIHONCD
          ,APSHOPCD
          ,APSKYSAKICD
          ,APTOKUISAKICD
      INTO
           NaAciApkihoncd
          ,NaAciShopcd
          ,NaAciSiiresakicd
          ,NaAciTokuisakicd
          ,NaApApkihoncd
          ,NaApShopcd
          ,NaApSkysakicd
          ,NaApTokuisakicd
      FROM  S02M057T
      WHERE
            ACIAPKIHONCD   = vRec.ACIAPKIHONCD    /*ACIAP基本コード*/
      AND   ACISHOPCD      = vRec.ACISHOPCD       /*ACIショップコード*/
      AND   APAPKIHONCD    = vRec.APAPKIHONCD     /*APAP基本コード*/
      AND   APSHOPCD       = vRec.APSHOPCD        /*APショップコード*/
      AND   APTOKUISAKICD  = vRec.APTOKUISAKICD;  /*AP得意先コード*/

    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        NULL;
    END;

    /*得意先マスタより営担当者コード,請求先コードの取得*/
    BEGIN
      SELECT EIGYOTNSHACD
           ,SKYSAKICD
-- インボイス対応追加 start ABI
           ,TOKUISAKIKBN
           ,JUTAKUDLVRYCSTKBN
-- インボイス対応追加 end ABI
      INTO   TEigyoTnshacd
           ,TSkysakicd
-- インボイス対応追加 start ABI
           ,TTokuisakikbn
           ,TJutakudlvrycstkbn
-- インボイス対応追加 end ABI
      FROM   S02M013T
      WHERE
             APKIHONCD    =  NaAciApkihoncd   /*AP基本コード*/
      AND    SHOPCD       =  NaAciShopcd       /*ショップコード*/
      AND    TOKUISAKICD  =  NaAciTokuisakicd; /*得意先コード*/

    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        NULL;
    END;

    /*Na請求抽出済ワークから1年前のデータを全削除*/
    DELETE FROM (SELECT * FROM S07W007T
    WHERE
        ACIAPKIHONCD = NaAciApkihoncd
    AND ACISHOPCD    = NaAciShopcd
    AND CONCAT(SHIMEDYDM,SHIMEDD)   <= add_months(CONCAT(iPrcsym,iShimedayi),-12) - (iShimedayi - 1));

    FOR cVerc IN cNazumiwork LOOP
      /*ACI側売上テーブルの売上伝票番号を取得*/
      FOR cDeleteNo IN (SELECT URIDNPYNO
                             ,DNPYNO
                             ,TOKUISAKICD
                             ,(NVL(URIKIN, 0) + NVL(SHOHITAXKIN, 0)) URIKIN
                        FROM  S06T001T
                        WHERE APKIHONCD = NaAciApkihoncd
                        AND   SHOPCD    = NaAciShopcd
                        AND   URIDNPYNO = cVerc.URIDNPYNO
                        AND   SKYPRCSJISSIDD = '00000000')
      LOOP

        /*取得した売上データの伝票番号が空白でない場合、取り消し伝票を削除する*/
        IF cDeleteNo.DNPYNO IS NOT NULL THEN
            /*NA請求得意先実績ワークに減算分売上金額を登録*/
          UPDATE S07W008T
             SET GENZAIZD       = GENZAIZD + cDeleteNo.URIKIN /* 赤伝票の売上金額*/
               , KOSHAAPKIHONCD = vRec.KOSHAAPKIHONCD    /*更新者AP基本コード*/
               , KOSHASHOPCD    = vRec.KOSHASHOPCD       /*更新者ショップコード*/
               , KOSHACD        = iChargeShacd           /*更新者コード*/
-- 本線対応変更 start ABI
               --, KSHNSYSTMYMD   = TO_CHAR(SYSDATE, 'YYYYMMDD')
               --, KSHNSYSTMJIKAN = TO_CHAR(SYSDATE, 'HH24MISS')
               , KSHNSYSTMYMD   = TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
               , KSHNSYSTMJIKAN = TO_CHAR(CURRENT_DATE, 'HH24MISS')
-- 本線対応変更 end ABI
               , KSHNUNYOYMD    = sUnyoDate
               , KSHNIPADRSE    = sIpAddress
               , KSHNPGMID      = PGMID
           WHERE ACIAPKIHONCD   = NaAciApkihoncd         /*ACIAP基本コード*/
             AND ACISHOPCD      = NaAciShopcd            /*ACIショップコード*/
             AND ACITOKUISAKICD = cDeleteNo.TOKUISAKICD; /*売上テーブル.得意先コード*/
WKUpdCnt := SQL%ROWCOUNT;
S99_CHECK_LOG('B0602B01', iApkihoncd, iShopcd, '1', 'U', '1', 'S07W008T', WKUpdCnt, null, null, null, null, null, 'NaAciApkihoncd:['||NaAciApkihoncd||'] NaAciShopcd:['||NaAciShopcd||'] cDeleteNo.TOKUISAKICD:['||cDeleteNo.TOKUISAKICD||'] '||'NA請求得意先実績ワークに減算分売上金額を登録1');

          IF    WKUpdCnt    =   0   THEN
            /*NA請求得意先実績ワークに減算分売上金額を登録*/
            INSERT INTO S07W008T
            (
               ACIAPKIHONCD    /*ACIAP基本コード*/
              ,ACISHOPCD       /*ACIショップコード*/
              ,ACITOKUISAKICD  /*ACI得意先コード*/
              ,GENZAIZD        /*現在残高*/
              ,TOSHAAPKIHONCD  /*登録者ＡＰ基本コード*/
              ,TOSHASHOPCD     /*登録者ショップコード*/
              ,TOSHACD         /*登録者コード*/
              ,INPTSYSTMYMD    /*登録システム日付*/
              ,INPTSYSTMJIKAN  /*登録システム時間*/
              ,INPTUNYOYMD     /*登録運用日付*/
              ,INPTIPADRSE     /*登録ＩＰアドレス*/
              ,INPTPGMID       /*登録プログラムＩＤ*/
              ,KOSHAAPKIHONCD  /*更新者ＡＰ基本コード*/
              ,KOSHASHOPCD     /*更新者ショップコード*/
              ,KOSHACD         /*更新者コード*/
              ,KSHNSYSTMYMD    /*更新システム日付*/
              ,KSHNSYSTMJIKAN  /*更新システム時間*/
              ,KSHNUNYOYMD     /*更新運用日付*/
              ,KSHNIPADRSE     /*更新ＩＰアドレス*/
              ,KSHNPGMID       /*更新プログラムＩＤ*/
            )
            VALUES
            (
               NaAciApkihoncd        /*ACIAP基本コード*/
              ,NaAciShopcd           /*ACIショップコード*/
              ,cDeleteNo.TOKUISAKICD /*売上テーブル.得意先コード*/
              ,cDeleteNo.URIKIN      /*(売上テーブル.金額+消費税金額)*-1*/
              ,vRec.TOSHAAPKIHONCD   /*登録者AP基本コード*/
              ,vRec.TOSHASHOPCD      /*登録者ショップコード*/
              ,iChargeShacd          /*登録者コード*/
-- 本線対応変更 start ABI
              --,TO_CHAR(SYSDATE, 'YYYYMMDD')
              --,TO_CHAR(SYSDATE, 'HH24MISS')
              ,TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
              ,TO_CHAR(CURRENT_DATE, 'HH24MISS')
-- 本線対応変更 end ABI
              ,sUnyoDate
              ,sIpAddress
              ,PGMID
              ,vRec.KOSHAAPKIHONCD   /*更新者AP基本コード*/
              ,vRec.KOSHASHOPCD      /*更新者ショップコード*/
              ,iChargeShacd          /*更新者コード*/
-- 本線対応変更 start ABI
              --,TO_CHAR(SYSDATE, 'YYYYMMDD')
              --,TO_CHAR(SYSDATE, 'HH24MISS')
              ,TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
              ,TO_CHAR(CURRENT_DATE, 'HH24MISS')
-- 本線対応変更 end ABI
              ,sUnyoDate
              ,sIpAddress
              ,PGMID
            );
WKUpdCnt := SQL%ROWCOUNT;
S99_CHECK_LOG('B0602B01', iApkihoncd, iShopcd, '1', 'I', '1', 'S07W008T', WKUpdCnt, null, null, null, null, null, 'NaAciApkihoncd:['||NaAciApkihoncd||'] NaAciShopcd:['||NaAciShopcd||'] cDeleteNo.TOKUISAKICD:['||cDeleteNo.TOKUISAKICD||'] '||'NA請求得意先実績ワークに減算分売上金額を登録1');
          END IF;
          /*売上テーブルの削除*/
          DELETE FROM S06T001T
          WHERE
              APKIHONCD      =  NaAciApkihoncd        /*AP基本コード*/
          AND SHOPCD         =  NaAciShopcd           /*ショップコード*/
          AND URIDNPYNO      =  cDeleteNo.DNPYNO ;    /*売上伝票番号*/

          /*売上明細テーブルの削除*/
          DELETE FROM S06T002T
          WHERE
              APKIHONCD      =  NaAciApkihoncd        /*AP基本コード*/
          AND SHOPCD         =  NaAciShopcd           /*ショップコード*/
          AND URIDNPYNO      =  cDeleteNo.DNPYNO ;    /*売上伝票番号*/
-- インボイス対応削除 start ABI
--          /*仕入計上テーブルの削除*/
--          DELETE FROM S11T001T
--          WHERE
--              APKIHONCD      =  NaAciApkihoncd        /*AP基本コード*/
--          AND SHOPCD         =  NaAciShopcd           /*ショップコード*/
--          AND SIIREDNPYNO    =  cDeleteNo.DNPYNO ;    /*仕入伝票番号*/
--
--          /*仕入計上明細テーブルの削除*/
--          DELETE FROM S11T002T
--          WHERE
--              APKIHONCD      =  NaAciApkihoncd         /*AP基本コード*/
--          AND SHOPCD         =  NaAciShopcd            /*ショップコード*/
--          AND SIIREDNPYNO    =  cDeleteNo.DNPYNO ;     /*仕入伝票番号*/
-- インボイス対応削除 end ABI
        END IF;
            /*NA請求得意先実績ワークに減算分売上金額を登録*/
          UPDATE S07W008T
             SET GENZAIZD       = GENZAIZD - cDeleteNo.URIKIN
               , KOSHAAPKIHONCD = vRec.KOSHAAPKIHONCD
               , KOSHASHOPCD    = vRec.KOSHASHOPCD
               , KOSHACD        = iChargeShacd
-- 本線対応変更 start ABI
               --, KSHNSYSTMYMD   = TO_CHAR(SYSDATE, 'YYYYMMDD')
               --, KSHNSYSTMJIKAN = TO_CHAR(SYSDATE, 'HH24MISS')
               , KSHNSYSTMYMD   = TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
               , KSHNSYSTMJIKAN = TO_CHAR(CURRENT_DATE, 'HH24MISS')
-- 本線対応変更 end ABI
               , KSHNUNYOYMD    = sUnyoDate
               , KSHNIPADRSE    = sIpAddress
               , KSHNPGMID      = PGMID
           WHERE ACIAPKIHONCD   = NaAciApkihoncd
             AND ACISHOPCD      = NaAciShopcd
             AND ACITOKUISAKICD = cDeleteNo.TOKUISAKICD;
WKUpdCnt := SQL%ROWCOUNT;
S99_CHECK_LOG('B0602B01', iApkihoncd, iShopcd, '1', 'U', '1', 'S07W008T', WKUpdCnt, null, null, null, null, null, 'NaAciApkihoncd:['||NaAciApkihoncd||'] NaAciShopcd:['||NaAciShopcd||'] cDeleteNo.TOKUISAKICD:['||cDeleteNo.TOKUISAKICD||'] '||'NA請求得意先実績ワークに減算分売上金額を登録2');
          IF    WKUpdCnt    =   0   THEN
            /*NA請求得意先実績ワークに減算分売上金額を登録*/
            INSERT INTO S07W008T
            (
               ACIAPKIHONCD    /*ACIAP基本コード*/
              ,ACISHOPCD       /*ACIショップコード*/
              ,ACITOKUISAKICD  /*ACI得意先コード*/
              ,GENZAIZD        /*現在残高*/
              ,TOSHAAPKIHONCD      /*登録者ＡＰ基本コード*/
              ,TOSHASHOPCD      /*登録者ショップコード*/
              ,TOSHACD        /*登録者コード*/
              ,INPTSYSTMYMD      /*登録システム日付*/
              ,INPTSYSTMJIKAN      /*登録システム時間*/
              ,INPTUNYOYMD      /*登録運用日付*/
              ,INPTIPADRSE      /*登録ＩＰアドレス*/
              ,INPTPGMID        /*登録プログラムＩＤ*/
              ,KOSHAAPKIHONCD      /*更新者ＡＰ基本コード*/
              ,KOSHASHOPCD      /*更新者ショップコード*/
              ,KOSHACD        /*更新者コード*/
              ,KSHNSYSTMYMD      /*更新システム日付*/
              ,KSHNSYSTMJIKAN      /*更新システム時間*/
              ,KSHNUNYOYMD      /*更新運用日付*/
              ,KSHNIPADRSE      /*更新ＩＰアドレス*/
              ,KSHNPGMID        /*更新プログラムＩＤ*/
            )
            VALUES
            (
               NaAciApkihoncd        /*ACIAP基本コード*/
              ,NaAciShopcd           /*ACIショップコード*/
              ,cDeleteNo.TOKUISAKICD /*売上テーブル.得意先コード*/
              ,0 - cDeleteNo.URIKIN  /*売上テーブル.金額+消費税金額*/
              ,vRec.TOSHAAPKIHONCD     /*登録者AP基本コード*/
              ,vRec.TOSHASHOPCD       /*登録者ショップコード*/
              ,iChargeShacd       /*登録者コード*/
-- 本線対応変更 start ABI
              --,TO_CHAR(SYSDATE, 'YYYYMMDD')
              --,TO_CHAR(SYSDATE, 'HH24MISS')
              ,TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
              ,TO_CHAR(CURRENT_DATE, 'HH24MISS')
-- 本線対応変更 end ABI
              ,sUnyoDate
              ,sIpAddress
              ,PGMID
              ,vRec.KOSHAAPKIHONCD     /*更新者AP基本コード*/
              ,vRec.KOSHASHOPCD       /*更新者ショップコード*/
              ,iChargeShacd       /*更新者コード*/
-- 本線対応変更 start ABI
              --,TO_CHAR(SYSDATE, 'YYYYMMDD')
              --,TO_CHAR(SYSDATE, 'HH24MISS')
              ,TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
              ,TO_CHAR(CURRENT_DATE, 'HH24MISS')
-- 本線対応変更 end ABI
              ,sUnyoDate
              ,sIpAddress
              ,PGMID
            );
WKUpdCnt := SQL%ROWCOUNT;
S99_CHECK_LOG('B0602B01', iApkihoncd, iShopcd, '1', 'I', '1', 'S07W008T', WKUpdCnt, null, null, null, null, null, 'NaAciApkihoncd:['||NaAciApkihoncd||'] NaAciShopcd:['||NaAciShopcd||'] cDeleteNo.TOKUISAKICD:['||cDeleteNo.TOKUISAKICD||'] '||'NA請求得意先実績ワークに減算分売上金額を登録2');

          END IF;

        /*売上テーブルの削除*/
        DELETE FROM S06T001T
        WHERE
            APKIHONCD      =  NaAciApkihoncd        /*AP基本コード*/
        AND SHOPCD         =  NaAciShopcd           /*ショップコード*/
        AND URIDNPYNO      =  cDeleteNo.URIDNPYNO ;  /*売上伝票番号*/

        /*売上明細テーブルの削除*/
        DELETE FROM S06T002T
        WHERE
            APKIHONCD      =  NaAciApkihoncd        /*AP基本コード*/
        AND SHOPCD         =  NaAciShopcd           /*ショップコード*/
        AND URIDNPYNO      =  cDeleteNo.URIDNPYNO ;  /*売上伝票番号*/
-- インボイス対応削除 start ABI
--        /*仕入計上テーブルの削除*/
--        DELETE FROM S11T001T
--        WHERE
--            APKIHONCD      =  NaAciApkihoncd        /*AP基本コード*/
--        AND SHOPCD         =  NaAciShopcd           /*ショップコード*/
--        AND SIIREDNPYNO    =  cDeleteNo.URIDNPYNO ;  /*仕入伝票番号*/
--
--        /*仕入計上明細テーブルの削除*/
--        DELETE FROM S11T002T
--        WHERE
--            APKIHONCD      =  NaAciApkihoncd         /*AP基本コード*/
--        AND SHOPCD         =  NaAciShopcd            /*ショップコード*/
--        AND SIIREDNPYNO    =  cDeleteNo.URIDNPYNO ;   /*仕入伝票番号*/
-- インボイス対応削除 end ABI

        /*NA請求抽出済ワークの削除*/
        DELETE FROM S07W007T
        WHERE
            ACIAPKIHONCD   =  NaAciApkihoncd /*AP基本コード*/
        AND ACISHOPCD      =  NaAciShopcd    /*ショップコード*/
        AND URIDNPYNO      =  cDeleteNo.URIDNPYNO;/*仕入伝票番号*/
      END LOOP;
    END LOOP;

    /*売上テーブルの抽出*/
    FOR cUriv IN cUriage LOOP
-- インボイス対応削除 start ABI
--      /*仕入先マスタのデータ取得*/
--      BEGIN
--        SELECT
--          TAXKEISANKBN
--           ,KEISANHOHO
--           ,TAXKBN
--        INTO
--          SiReKeisanKbn
--           ,SiReKeisanHo
--           ,SiReTaxKbn
--        FROM
--          S02M021T
--        WHERE
--          APKIHONCD   =  NaAciApkihoncd
--        AND  SHOPCD      =  NaAciShopcd
--        AND SIIRESAKICD =  NaAciSiiresakicd;
--
--      EXCEPTION
--        WHEN NO_DATA_FOUND  THEN
--          NULL;
--      END ;
-- インボイス対応削除 end ABI

-- インボイス対応追加 start ABI
      -- 売上データ.データ作成元区分 = 23（請求書消費税算出）の場合、次の売上データの処理を実行する
      IF cUriv.DATSKSMOTOKBN = '23' THEN
        CONTINUE;
      END IF;
-- インボイス対応追加 end ABI

      /*請求先マスタのデータ取得*/
      BEGIN
        SELECT
           SKYSAKICD
           ,SKYSAKINMN1
        INTO
           SkySkysakicd
           ,SkySkynm
        FROM
           S02M017T
        WHERE
               APKIHONCD   =  NaApApkihoncd
           AND SHOPCD      =  NaApShopcd
           AND SKYSAKICD   =  NaApSkysakicd;
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          NULL;
      END ;
      /*ショップマスタの取得*/
      BEGIN
        SELECT SHINTAXKEISANKBNURI
            ,GENZTAXKEISANKBNURI
            ,TAXKEISANKBNURIKIRIKAEDD
        INTO   ShopShinTax
            ,ShopGenTax
            ,ShopTaxKbn
        FROM
          S02M046T
        WHERE
          APKIHONCD   =  NaAciApkihoncd  /*AP基本コード*/
        AND SHOPCD    =  NaAciShopcd  ;  /*ショップコード*/
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          NULL;
      END ;

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
--             , CASE WHEN SHN.TAXKBN IN ('4','5') AND cUriv.URIDD <= '20190930' THEN '1' ELSE SHN.TAXKBN END
             , SHN.TAXKBN
-- インボイス対応変更 end ABI
             , SHN.ZAIKOKANRITRGTFLG
             , SHN.ZAIKOARAMUSURYO
          INTO wChoseiModel
          FROM S02M006T SKM
          JOIN S02V001T SHN
            ON SHN.APKIHONCD = NaAciApkihoncd
           AND SHN.SHOPCD    = NaAciShopcd
           AND SHN.SHNCD     = SKM.MOJISETTEIATAI
         WHERE SKM.APKIHONCD = '000'
           AND SKM.SHOPCD    = '000'
           AND SKM.SETTEINO  = '166' 
           AND SKM.RCRDKBN   = 'M'
           AND SKM.YUKOKBN   = '1';
        EXCEPTION 
          WHEN NO_DATA_FOUND THEN 
            RAISE_APPLICATION_ERROR(-20021, 'EJ999:調整商品が登録されていません。');
      END;

      rUridnpyoStart  := NULL;
      rUridnpyoEnd    := NULL;
      
      /*各変数の初期化*/
      ErrorFlg        := FALSE ; /*マスタテーブル作成フラグ*/
      MeiCreateFlg    := FALSE ; /*明細テーブル作成フラグ*/
      SumKingaku      := 0;
      SumShohiKingaku := 0;
-- インボイス対応削除 start ABI
--      SumSiireKingaku      := 0;
--      SumSiireShohiKingaku := 0;
      
--      wSiireMeisaiNo := 0;
-- インボイス対応削除 end ABI

      /*税計算区分値取得*/
      IF cUriv.URIDD < ShopTaxKbn THEN
        TaxKBN := ShopGenTax;
      ELSE
        TaxKBN := ShopShinTax ;
      END IF;

      /*取得した売上データの伝票番号が空の場合にのみデータ作成*/
      IF cUriv.DNPYNO IS NULL THEN
        /*売上明細テーブルの抽出*/
        FOR vCur IN (SELECT * FROM S06T002T
            WHERE
                APKIHONCD      =  NaApApkihoncd
            AND SHOPCD         =  NaApShopcd
            
            AND URIDNPYNO      =  cUriv.URIDNPYNO ORDER BY URIDNPYMEISAINO, URIDNPYMEISAIEDANO) LOOP

-- インボイス対応追加 start ABI
          -- 大分類コード = 99(消費税) かつ 中分類コード = 001(消費税) かつ小分類コード = 001(消費税) または 002(軽減消費税) の場合、次の売上明細データの処理を実行する
          IF vCur.LCTGRYCD = '99' AND vCur.MCTGRYCD = '001' AND (vCur.SCTGRYCD = '001' OR vCur.SCTGRYCD = '002') THEN
            CONTINUE;
          END IF;
-- インボイス対応追加 end ABI

          /*商品マスタビューの抽出*/
          BEGIN
            SELECT SHNZOKUSEI
                 ,TAXKBN
                 ,TAXSETTEIKBN
            INTO   ShnShnzokusei
                 ,ShnTaxkbn
                 ,ShnTaxSetteikbn
            FROM
                 S02V001T
            WHERE
              APKIHONCD     =  vCur.APKIHONCD  /*AP基本コード*/
            AND SHOPCD      =  vCur.SHOPCD     /*ショップコード*/
            AND SHNCD       =  vCur.SHNCD;     /*商品コード*/

          EXCEPTION
            WHEN NO_DATA_FOUND THEN
              NULL;
          END ;

          /* ACI側の商品マスタビュー存在チェック*/
          BEGIN
            SELECT COUNT(*)
            INTO AciShnCnt
            FROM
                 S02V001T
            WHERE
                APKIHONCD   =  NaAciApkihoncd  /*AP基本コード*/
            AND SHOPCD      =  NaAciShopcd     /*ショップコード*/
            AND SHNCD       =  vCur.SHNCD;     /*商品コード*/
          END ;

          /* ACI側に商品情報が商品マスタビューに存在しない場合はAP商品エラー扱いにする*/
          IF AciShnCnt = 0 THEN
              ShnShnzokusei := '1';
          END IF;

          DlvryPrice := 0;
          -- 基本は商品エラーあり
          wShnErrFlg := TRUE;

          IF ShnShnzokusei = '0' AND MeiCreateFlg = FALSE THEN /*商品属性が0(本部商品)*/
            ErrorFlg := TRUE ;

            /*単価計算*/
            -- 商品が本部商品であれば特別単価有無でエラー判定（消費税調整データは特別単価チェック不要）
            wShnErrFlg := FALSE;
            IF vCur.KEIJOKBN = '01' THEN /*抽出した明細の計上区分が01(売上)の場合のみ単価を計算*/
              BEGIN
                
                -- 入力行且つ単独商品の場合
                IF vCur.NYURYOKUMEISAIFLG = '1' AND vCur.TANPINMEISAIFLG = '1' THEN
                  -- 単品入力行も親として税区分を退避
                  wOyaTaxKBN := ShnTaxkbn;
                
                SELECT CASE WHEN cUriv.DLVRYKBN IS NULL OR cUriv.DLVRYKBN = '1' OR cUriv.DLVRYKBN ='2' THEN s009t.DLVRYPRICE     /*配達価格*/
                     WHEN cUriv.DLVRYKBN ='3' THEN s009t.MOCHIKAERIPRICE END   /*持ち帰り価格*/
                INTO   DlvryPrice
                FROM
                     S02M009T s009t
                WHERE
                         s009t.APKIHONCD   = NaAciApkihoncd    /*AP基本コード*/
                     AND s009t.SHOPCD       = NaAciShopcd      /*ショップコード*/
                     AND s009t.TOKUISAKICD = NaAciTokuisakicd  /*得意先コード*/
                     AND s009t.SHNCD        = vCur.SHNCD      /*商品コード*/
                     AND cUriv.URIDD  BETWEEN s009t.TEKIYOSTRTDD AND s009t.TEKIYOSHRYDD
                     AND s009t.YUKOKBN = '1'
                     AND ROWNUM <= 1;
                
                -- 入力行且つセット商品の場合
                ELSIF vCur.NYURYOKUMEISAIFLG = '1' AND vCur.TANPINMEISAIFLG = '0' THEN
                  
                  -- 親の税区分を退避
                  wOyaTaxKBN := ShnTaxkbn;
                  
                  -- 得意先別セット商品単価マスタ取得
                  SELECT
                    CASE 
                      WHEN cUriv.DLVRYKBN IS NULL OR cUriv.DLVRYKBN = '1' OR cUriv.DLVRYKBN ='2' THEN s066t.DLVRYPRICE /*配達価格*/
                      WHEN cUriv.DLVRYKBN = '3' THEN s066t.MOCHIKAERIPRICE                                             /*持ち帰り価格*/
                    END 
                  INTO
                    DlvryPrice
                  FROM
                    S02M066T s066t 
                  WHERE
                      s066t.APKIHONCD   = NaAciApkihoncd    /*AP基本コード*/
                  AND s066t.SHOPCD      = NaAciShopcd       /*ショップコード*/
                  AND s066t.STSHNCD     = vCur.SHNCD        /*商品コード*/
                  AND s066t.TOKUISAKICD = NaAciTokuisakicd  /*得意先コード*/
                  AND cUriv.URIDD BETWEEN s066t.TEKIYOSTRTDD AND s066t.TEKIYOSHRYDD 
                  AND s066t.YUKOKBN     = '1'
                  AND ROWNUM            <= 1
                  ;
                  
                -- セット商品展開行且つ単独商品かつ商品の場合
                ELSIF vCur.NYURYOKUMEISAIFLG = '0' AND vCur.TANPINMEISAIFLG = '1' AND vCur.MEISAIKBN = '0' THEN
                  
                  -- 得意先別セット商品単価展開マスタ取得
                  SELECT
                    CASE 
                      WHEN cUriv.DLVRYKBN IS NULL OR cUriv.DLVRYKBN = '1' OR cUriv.DLVRYKBN ='2' THEN s067t.DLVRYPRICE /*配達価格*/
                      WHEN cUriv.DLVRYKBN = '3' THEN s067t.MOCHIKAERIPRICE                                             /*持ち帰り価格*/
                    END 
                  INTO
                    DlvryPrice
                  FROM
                    S02M066T s066t 
                    INNER JOIN S02M067T s067t 
                      ON (s067t.APKIHONCD    = s066t.APKIHONCD 
                      AND s067t.SHOPCD       = s066t.SHOPCD 
                      AND s067t.STSHNCD      = s066t.STSHNCD 
                      AND s067t.TOKUISAKICD  = s066t.TOKUISAKICD 
                      AND s067t.TEKIYOSTRTDD = s066t.TEKIYOSTRTDD 
                      AND s067t.RENBAN       = vCur.URIDNPYMEISAIEDANO  /*売上伝票明細枝番号*/
                      AND s067t.SHNCD        = vCur.SHNCD               /*商品コード*/
                      AND s067t.YUKOKBN      = '1' 
                    )
                  WHERE
                      s066t.APKIHONCD   = NaAciApkihoncd    /*AP基本コード*/
                  AND s066t.SHOPCD      = NaAciShopcd       /*ショップコード*/
                  AND s066t.STSHNCD     = vCur.STSHNCD      /*セット商品コード*/
                  AND s066t.TOKUISAKICD = NaAciTokuisakicd  /*得意先コード*/
                  AND cUriv.URIDD BETWEEN s066t.TEKIYOSTRTDD AND s066t.TEKIYOSHRYDD 
                  AND s066t.YUKOKBN     = '1'
                  AND ROWNUM            <= 1
                  ;
                  
                END IF;

              EXCEPTION
                WHEN NO_DATA_FOUND THEN
                  DlvryPrice := null;
                  -- 消費税調整データ以外で特別単価が存在しない場合エラー
                  wShnErrFlg := TRUE;
              END ;
            ELSE
              wShnErrFlg := TRUE;
            END IF;
          END IF;

          IF wShnErrFlg = FALSE AND ShnShnzokusei = '0' AND MeiCreateFlg = FALSE THEN /*商品属性が0(本部商品)*/
-- インボイス対応削除 start ABI
--            IF (wOyaTaxKbn = '4' or  wOyaTaxKbn = '5') AND  cUriv.URIDD < '20191001' THEN
--              wRecTaxKbn  := '1';
-- インボイス対応削除 end ABI
            -- 入力行の場合は商品マスタビューの税区分
-- インボイス対応変更 start ABI
--            ELSIF vCur.NYURYOKUMEISAIFLG = '1' THEN
            IF vCur.NYURYOKUMEISAIFLG = '1' THEN
-- インボイス対応変更 end ABI
              wRecTaxKbn  := ShnTaxkbn;
-- インボイス対応削除 start ABI
--            -- セット商品展開行で親商品の税区分が「5:子明細商品」の場合、親の税区分を引き継がない
--            ELSIF wOyaTaxKbn = '5' THEN
--              wRecTaxKbn  := ShnTaxkbn;
-- インボイス対応削除 end ABI
            -- セット商品展開行で親商品の税区分が「5:子明細商品」以外の場合、親の税区分
            ELSE
              wRecTaxKbn  := wOyaTaxKbn;
            END IF;
            /*単価消費税金額は共通部品を使用して計算する*/
            IF ShnTaxSetteikbn = '1' THEN
-- 本線対応変更 start ABI
--              rUntShohiTaxKin := S01SyohizeiService.SyohizeiKeisan('1',          /*単体計算フラグ (0:しない　1:する)*/
              rUntShohiTaxKin := Z1808B01.SyohizeiKeisan('1',          /*単体計算フラグ (0:しない　1:する)*/
-- 本線対応変更 end ABI
                                          ShnTaxSetteikbn, /*税設定区分*/
                                          wRecTaxKbn,        /*税区分*/
                                          TaxKBN,    /*ACIショップマスタ.現税計算区分売上 OR 新税計算区分売上*/
                                          cUriv.URIDD,        /*切り替え日*/
                                          NaAciApkihoncd,      /*AP基本コード*/
                                          NaAciShopcd,        /*ショップコード*/
                                          NULL,            /*仕入先コード*/
                                          NULL,            /*数量*/
                                          DlvryPrice);        /*税抜金額（税抜単価）*/
            ELSE
-- 本線対応変更 start ABI
--              rUntShohiTaxKin := S01SyohizeiService.SyohizeiKeisan('1',
              rUntShohiTaxKin := Z1808B01.SyohizeiKeisan('1',
-- 本線対応変更 end ABI
                                          ShnTaxSetteikbn, /*税設定区分*/
                                          '1',
                                          TaxKBN,    /*ACIショップマスタ.現税計算区分売上 OR 新税計算区分売上*/
                                          cUriv.URIDD,
                                          NaAciApkihoncd,
                                          NaAciShopcd,
                                          NULL,
                                          NULL,
                                          DlvryPrice);
            END IF ;
            /*消費税金額は共通部品を使用して計算する*/
            IF ShnTaxSetteikbn = '1' THEN
-- 本線対応変更 start ABI
--              rShoiTaxKin := S01SyohizeiService.SyohizeiKeisan(0,              /*単体計算フラグ (0:しない　1:する)*/
              rShoiTaxKin := Z1808B01.SyohizeiKeisan(0,              /*単体計算フラグ (0:しない　1:する)*/
-- 本線対応変更 end ABI
                                        ShnTaxSetteikbn, /*税設定区分*/
                                        wRecTaxKbn,        /*税区分*/
                                        TaxKBN,    /*ACIショップマスタ.現税計算区分売上 OR 新税計算区分売上*/
                                        cUriv.URIDD,        /*切り替え日*/
                                        NaAciApkihoncd,      /*AP基本コード*/
                                        NaAciShopcd,        /*ショップコード*/
                                        NULL,            /*仕入先コード*/
                                        vCur.SURYO,        /*数量*/
                                        DlvryPrice); /*税抜金額（税抜単価）*/
            ELSE
-- 本線対応変更 start ABI
--                rShoiTaxKin := S01SyohizeiService.SyohizeiKeisan(0,
                rShoiTaxKin := Z1808B01.SyohizeiKeisan(0,
-- 本線対応変更 end ABI
                                         ShnTaxSetteikbn, /*税設定区分*/
                                         '1',
                                         TaxKBN,    /*ACIショップマスタ.現税計算区分売上 OR 新税計算区分売上*/
                                         cUriv.URIDD,
                                         NaAciApkihoncd,
                                         NaAciShopcd,
                                         NULL,
                                         vCur.SURYO,
                                         DlvryPrice);
            END IF;
            IF  rUridnpyoStart IS NULL THEN
              /*採番は共通部品より取得する*/
-- 本線対応変更 start ABI
--              S01SAIBAN.getSlipNoUriageNa(NaAciApkihoncd,NaAciShopcd,1,rUridnpyoStart,rUridnpyoEnd);
              Z1804B01.getSlipNoUriageNa(NaAciApkihoncd,NaAciShopcd,1,rUridnpyoStart,rUridnpyoEnd);
-- 本線対応変更 end ABI
            END IF;

            /*売上明細テーブルの作成*/
            INSERT INTO S06T002T
            (
               APKIHONCD      /*ＡＰ基本コード*/
              ,SHOPCD        /*ショップコード*/
              ,URIDNPYNO      /*売上伝票番号*/
              ,EDANO        /*枝番号*/
              ,URIDNPYMEISAINO  /*売上伝票明細番号*/
              ,URIDNPYMEISAIEDANO  /*売上伝票明細枝番号*/
              ,LCTGRYCD      /*大分類コード*/
              ,MCTGRYCD      /*中分類コード*/
              ,SCTGRYCD      /*小分類コード*/
              ,SHNCD        /*商品コード*/
              ,KEIJOKBN      /*計上区分*/
              ,SURYO        /*数量*/
              ,HYOJUNUNT      /*標準単価*/
              ,UNT        /*単価*/
              ,KIN        /*金額*/
              ,UNTSHOHITAXKIN    /*単価消費税金額*/
              ,SHOHITAXKIN    /*消費税金額*/
-- インボイス対応追加 start ABI
              ,SANKOUSHOHITAXKIN /*参考消費税金額*/
-- インボイス対応追加 end ABI
              ,TAXKBN        /*税区分*/
              ,MEISAIKBN      /*明細区分*/
              ,NYURYOKUMEISAIFLG  /*入力明細フラグ*/
              ,TANPINMEISAIFLG  /*単品明細フラグ*/
              ,MEISAIBIKO      /*明細備考*/
              ,STSHNCD      /*セット商品コード*/
              ,KATASIKI      /*型式*/
              ,siiresuryo   /*仕入数量*/
              ,SIIREKIN      /*仕入金額*/
              ,SIIREUNT      /*仕入単価*/
              ,SIIRESHOHITAXKIN  /*仕入消費税金額*/
              ,ZAIKOKSHNSUMIFLG  /*在庫更新済フラグ*/
-- インボイス対応追加 start ABI
              ,TOKUISAKIKBN       /*得意先区分*/
              ,JUTAKUDLVRYCSTKBN  /*受託配達顧客区分*/
-- インボイス対応追加 end ABI
              ,YUKOKBN      /*有効区分*/
              ,TOSHAAPKIHONCD    /*登録者ＡＰ基本コード*/
              ,TOSHASHOPCD    /*登録者ショップコード*/
              ,TOSHACD      /*登録者コード*/
              ,INPTSYSTMYMD    /*登録システム日付*/
              ,INPTSYSTMJIKAN    /*登録システム時間*/
              ,INPTUNYOYMD    /*登録運用日付*/
              ,INPTIPADRSE    /*登録ＩＰアドレス*/
              ,INPTPGMID      /*登録プログラムＩＤ*/
              ,KOSHAAPKIHONCD    /*更新者ＡＰ基本コード*/
              ,KOSHASHOPCD    /*更新者ショップコード*/
              ,KOSHACD      /*更新者コード*/
              ,KSHNSYSTMYMD    /*更新システム日付*/
              ,KSHNSYSTMJIKAN    /*更新システム時間*/
              ,KSHNUNYOYMD    /*更新運用日付*/
              ,KSHNIPADRSE    /*更新ＩＰアドレス*/
              ,KSHNPGMID      /*更新プログラムＩＤ*/
            )
            VALUES
            (
               NaAciApkihoncd            /*Na請求マスタ.ACIAP基本コード*/
              ,NaAciShopcd           /*Na請求マスタ.ACIショップコード*/
              ,rUridnpyoStart         /*売上テーブル.ACI売上伝票番号*/
              ,cUriv.EDANO           /*売上テーブル.ACI枝番号*/
              ,vCur.URIDNPYMEISAINO      /*売上明細テーブル.売上伝票明細番号*/
              ,vCur.URIDNPYMEISAIEDANO  /*売上明細テーブル.売上伝票明細枝番号*/
              ,vCur.LCTGRYCD        /*売上明細テーブル.大分類コード*/
              ,vCur.MCTGRYCD        /*売上明細テーブル.中分類コード*/
              ,vCur.SCTGRYCD        /*売上明細テーブル.小分類コード*/
              ,vCur.SHNCD          /*売上明細テーブル.商品コード*/
              ,vCur.KEIJOKBN        /*売上明細テーブル.計上区分*/
              ,vCur.SURYO          /*売上明細テーブル.数量*/
              ,vCur.HYOJUNUNT        /*売上明細テーブル.標準単価*/
              ,DlvryPrice                /*単価*/
              ,DlvryPrice * vCur.SURYO   /*金額*/
              ,CASE WHEN TaxKBN = '2' THEN 0
               ELSE  rUntShohiTaxKin END  /*単価消費税金額*/
-- インボイス対応変更 start ABI
--              ,rShoiTaxKin          /*消費税金額*/
               -- 売上明細テーブル.消費税金額が設定されている場合、変数.消費税金額
              ,CASE WHEN vCur.SHOHITAXKIN IS NOT NULL AND vCur.SHOHITAXKIN <> 0 THEN rShoiTaxKin
               ELSE 0 END  /*消費税金額*/
-- インボイス対応変更 end ABI
-- インボイス対応追加 start ABI
               -- 売上明細テーブル.参考消費税金額が設定されている場合、変数.消費税金額
              ,CASE WHEN vCur.SANKOUSHOHITAXKIN IS NOT NULL AND vCur.SANKOUSHOHITAXKIN <> 0 THEN rShoiTaxKin
               ELSE 0 END  /*参考消費税金額*/
-- インボイス対応追加 end ABI
              ,wRecTaxKbn                 /* 税区分 */
              ,vCur.MEISAIKBN        /*売上明細テーブル.明細区分*/
              ,vCur.NYURYOKUMEISAIFLG    /*売上明細テーブル.入力明細フラグ*/
              ,vCur.TANPINMEISAIFLG    /*売上明細テーブル.単品明細フラグ*/
              ,vCur.MEISAIBIKO      /*売上明細テーブル.明細備考*/
              ,vCur.STSHNCD        /*売上明細テーブル.セット商品コード*/
              ,vCur.KATASIKI        /*売上明細テーブル.型式*/
              ,vCur.SURYO          /*売上明細テーブル.数量*/
              ,vCur.KIN          /*売上明細テーブル.金額*/
              ,vCur.UNT          /*売上明細テーブル.単価*/
              ,vCur.SHOHITAXKIN      /*売上明細テーブル.消費税金額*/
              ,'1'
-- インボイス対応追加 start ABI
              ,TTokuisakikbn      /*得意先区分*/
              ,TJutakudlvrycstkbn /*受託配達顧客区分*/
-- インボイス対応追加 end ABI
              ,'1'
              ,vRec.TOSHAAPKIHONCD     /*登録者AP基本コード*/
              ,vRec.TOSHASHOPCD       /*登録者ショップコード*/
              ,iChargeShacd       /*登録者コード*/
-- 本線対応変更 start ABI
              --,TO_CHAR(SYSDATE, 'YYYYMMDD')
              --,TO_CHAR(SYSDATE, 'HH24MISS')
              ,TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
              ,TO_CHAR(CURRENT_DATE, 'HH24MISS')
-- 本線対応変更 end ABI
              ,sUnyoDate
              ,sIpAddress
              ,PGMID
              ,vRec.KOSHAAPKIHONCD     /*更新者AP基本コード*/
              ,vRec.KOSHASHOPCD       /*更新者ショップコード*/
              ,iChargeShacd       /*更新者コード*/
-- 本線対応変更 start ABI
              --,TO_CHAR(SYSDATE, 'YYYYMMDD')
              --,TO_CHAR(SYSDATE, 'HH24MISS')
              ,TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
              ,TO_CHAR(CURRENT_DATE, 'HH24MISS')
-- 本線対応変更 end ABI
              ,sUnyoDate
              ,sIpAddress
              ,PGMID
            );

-- インボイス対応削除 start ABI
--            -- 単独商品の場合
--            IF vCur.TANPINMEISAIFLG = '1' THEN
--            
--            /*仕入計上明細テーブル*/
--            wSiireMeisaiNo  := wSiireMeisaiNo + 1;
--            INSERT INTO S11T002T
--            (
--               APKIHONCD      /*ＡＰ基本コード*/
--              ,SHOPCD        /*ショップコード*/
--              ,SIIREDNPYNO    /*仕入伝票番号*/
--              ,SIIREDNPYMEISAINO  /*仕入伝票明細番号*/
--              ,LCTGRYCD      /*大分類コード*/
--              ,MCTGRYCD      /*中分類コード*/
--              ,SCTGRYCD      /*小分類コード*/
--              ,SHNCD        /*商品コード*/
--              ,SURYO        /*数量*/
--              ,HYOJUNUNT      /*標準単価*/
--              ,UNT        /*単価*/
--              ,KIN        /*金額*/
--              ,UNTSHOHITAXKIN    /*単価消費税金額*/
--              ,SHOHITAXKIN    /*消費税金額*/
--              ,TAXKBN        /*税区分*/
--              ,MEISAIBIKO      /*明細備考*/
--              ,KATASIKI      /*型式*/
--              ,YUKOKBN      /*有効区分*/
--              ,TOSHAAPKIHONCD    /*登録者ＡＰ基本コード*/
--              ,TOSHASHOPCD    /*登録者ショップコード*/
--              ,TOSHACD      /*登録者コード*/
--              ,INPTSYSTMYMD    /*登録システム日付*/
--              ,INPTSYSTMJIKAN    /*登録システム時間*/
--              ,INPTUNYOYMD    /*登録運用日付*/
--              ,INPTIPADRSE    /*登録ＩＰアドレス*/
--              ,INPTPGMID      /*登録プログラムＩＤ*/
--              ,KOSHAAPKIHONCD    /*更新者ＡＰ基本コード*/
--              ,KOSHASHOPCD    /*更新者ショップコード*/
--              ,KOSHACD      /*更新者コード*/
--              ,KSHNSYSTMYMD    /*更新システム日付*/
--              ,KSHNSYSTMJIKAN    /*更新システム時間*/
--              ,KSHNUNYOYMD    /*更新運用日付*/
--              ,KSHNIPADRSE    /*更新ＩＰアドレス*/
--              ,KSHNPGMID      /*更新プログラムＩＤ*/
--            )
--            VALUES
--            (
--               NaAciApkihoncd            /*Na請求マスタ.ACIAP基本コード*/
--              ,NaAciShopcd           /*Na請求マスタ.ACIショップコード*/
--              ,rUridnpyoStart         /*売上テーブル.ACI売上伝票番号*/
--              ,wSiireMeisaiNo
--              ,vCur.LCTGRYCD        /*売上明細テーブル.大分類コード*/
--              ,vCur.MCTGRYCD        /*売上明細テーブル.中分類コード*/
--              ,vCur.SCTGRYCD        /*売上明細テーブル.小分類コード*/
--              ,vCur.SHNCD          /*売上明細テーブル.商品コード*/
--              ,vCur.SURYO          /*売上明細テーブル.数量*/
--              ,vCur.HYOJUNUNT        /*売上明細テーブル.標準単価*/
--              ,vCur.UNT          /*売上明細テーブル.単価*/
--              ,vCur.KIN          /*売上明細テーブル.金額*/
--              ,vCur.UNTSHOHITAXKIN    /*売上明細テーブル.単価消費税金額*/
--              ,vCur.SHOHITAXKIN      /*売上明細テーブル.消費税金額*/
--              ,wRecTaxKbn                 /* 税区分 */
--              ,SUBSTRB(SkySkysakicd || ' ' || SkySkynm, 1, 40)  /*請求先マスタ.コード + 請求先マスタ.名称*/
--              ,vCur.KATASIKI        /*売上明細テーブル.型式*/
--              ,'1'
--              ,vRec.TOSHAAPKIHONCD     /*登録者AP基本コード*/
--              ,vRec.TOSHASHOPCD       /*登録者ショップコード*/
--              ,iChargeShacd       /*登録者コード*/
--              ,TO_CHAR(SYSDATE, 'YYYYMMDD')
--              ,TO_CHAR(SYSDATE, 'HH24MISS')
--              ,sUnyoDate
--              ,sIpAddress
--              ,PGMID
--              ,vRec.KOSHAAPKIHONCD     /*更新者AP基本コード*/
--              ,vRec.KOSHASHOPCD       /*更新者ショップコード*/
--              ,iChargeShacd       /*更新者コード*/
--              ,TO_CHAR(SYSDATE, 'YYYYMMDD')
--              ,TO_CHAR(SYSDATE, 'HH24MISS')
--              ,sUnyoDate
--              ,sIpAddress
--              ,PGMID
--            );
--            SumSiireKingaku      := SumSiireKingaku + vCur.KIN;   /*仕入金額の加算*/
--            SumSiireShohiKingaku := SumSiireShohiKingaku + vCur.SHOHITAXKIN;    /*仕入消費税金額の加算*/
--            END IF;
-- インボイス対応削除 end ABI
            
            -- 売上ヘッダー用の金額集計は入力行のみ
            IF vCur.NYURYOKUMEISAIFLG = '1' THEN
            /*金額と消費税金額の取得*/
            SELECT KIN
-- インボイス対応変更 start
--              ,SHOHITAXKIN
              ,CASE WHEN SHOHITAXKIN IS NOT NULL AND SHOHITAXKIN <> 0 THEN SHOHITAXKIN ELSE SANKOUSHOHITAXKIN END
-- インボイス対応変更 end
            INTO  GetKingaku
              ,GetShohikingaku
            FROM  S06T002T
            WHERE
              APKIHONCD          =  NaAciApkihoncd           /*AP基本コード*/
            AND SHOPCD             =  NaAciShopcd              /*ショップコード*/
            AND URIDNPYNO          =  rUridnpyoStart           /*売上伝票番号*/
            AND URIDNPYMEISAINO    =  vCur.URIDNPYMEISAINO     /*売上伝票明細番号*/
            AND URIDNPYMEISAIEDANO =  vCur.URIDNPYMEISAIEDANO; /*売上伝票明細枝番号*/

            SumKingaku := SumKingaku + GetKingaku;           /*金額の加算*/
            SumShohiKingaku := SumShohiKingaku + GetShohikingaku ; /*消費税金額の加算*/
            END IF;

          ELSIF wShnErrFlg = FALSE AND ShnShnzokusei = '1' AND MeiCreateFlg = FALSE THEN /*商品属性が1(AP商品)*/

            /*AP商品エラーワークテーブルの作成*/
-- 本線対応変更 start ABI
            --INSERT INTO S07W005T_NA
            INSERT INTO S07W005T
-- 本線対応変更 end ABI
            (
               ACIAPKIHONCD    /*ACIAP基本コード*/
              ,ACISHOPCD       /*ACIショップコード*/
              ,APKIHONCD        /*ＡＰ基本コード*/
              ,SHOPCD          /*ショップコード*/
              ,DNPYNO          /*伝票番号*/
              ,URIDNPYMEISAINO    /*売上伝票明細番号*/
              ,URIDNPYMEISAIEDANO    /*売上伝票明細枝番号*/
              ,ACISKYSAKICD      /*ACI請求先コード*/
              ,TOKUISAKICD      /*得意先コード*/
              ,YMD          /*日付*/
              ,SHIMEDYDMDD      /*締年月日*/
              ,SHNCD          /*商品コード*/
              ,SURYO          /*数量*/
              ,UNT          /*単価*/
              ,URIKIN          /*売上金額*/
              ,SHOHITAXKIN      /*消費税金額*/
              ,TOSHAAPKIHONCD      /*登録者ＡＰ基本コード*/
              ,TOSHASHOPCD      /*登録者ショップコード*/
              ,TOSHACD        /*登録者コード*/
              ,INPTSYSTMYMD      /*登録システム日付*/
              ,INPTSYSTMJIKAN      /*登録システム時間*/
              ,INPTUNYOYMD      /*登録運用日付*/
              ,INPTIPADRSE      /*登録ＩＰアドレス*/
              ,INPTPGMID        /*登録プログラムＩＤ*/
              ,KOSHAAPKIHONCD      /*更新者ＡＰ基本コード*/
              ,KOSHASHOPCD      /*更新者ショップコード*/
              ,KOSHACD        /*更新者コード*/
              ,KSHNSYSTMYMD      /*更新システム日付*/
              ,KSHNSYSTMJIKAN      /*更新システム時間*/
              ,KSHNUNYOYMD      /*更新運用日付*/
              ,KSHNIPADRSE      /*更新ＩＰアドレス*/
              ,KSHNPGMID        /*更新プログラムＩＤ*/
            )
            VALUES
            (
               NaAciApkihoncd      /*NA請求変換マスタ.ACIAP基本コード*/
              ,NaAciShopcd         /*NA請求変換マスタ.ACIショップコード*/
              ,NaApApkihoncd        /*NA請求変換マスタ.APAP基本コード*/
              ,NaApShopcd        /*NA請求変換マスタ.APショップコード*/
              ,vCur.URIDNPYNO        /*売上明細テーブル.売上伝票番号*/
              ,vCur.URIDNPYMEISAINO    /*売上明細テーブル.売上伝票明細番号*/
              ,vCur.URIDNPYMEISAIEDANO  /*売上明細テーブル.売上伝票明細枝番号*/
              ,TSkysakicd        /*得意先マスタ.請求先コード*/
              ,NaApTokuisakicd        /*NA請求変換マスタ.AP得意先コード*/
              ,cUriv.URIDD        /*売上テーブル.売上日*/
              ,SUBSTR(CONCAT(cUriv.SKYSHIMEDYDM , cUriv.SKYSHIMEDD) ,0, 8)      /*売上テーブル.売上日*/
              ,vCur.SHNCD          /*売上明細テーブル.商品コード*/
              ,vCur.SURYO          /*売上明細テーブル.数量*/
              ,vCur.UNT          /*売上明細テーブル.単価*/
              ,vCur.KIN          /*売上明細テーブル.金額*/
              ,vCur.SHOHITAXKIN      /*売上明細テーブル.消費税金額*/
              ,vRec.TOSHAAPKIHONCD     /*登録者AP基本コード*/
              ,vRec.TOSHASHOPCD       /*登録者ショップコード*/
              ,iChargeShacd       /*登録者コード*/
-- 本線対応変更 start ABI
              --,TO_CHAR(SYSDATE, 'YYYYMMDD')
              --,TO_CHAR(SYSDATE, 'HH24MISS')
              ,TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
              ,TO_CHAR(CURRENT_DATE, 'HH24MISS')
-- 本線対応変更 end ABI
              ,sUnyoDate
              ,sIpAddress
              ,PGMID
              ,vRec.KOSHAAPKIHONCD     /*更新者AP基本コード*/
              ,vRec.KOSHASHOPCD       /*更新者ショップコード*/
              ,iChargeShacd       /*更新者コード*/
-- 本線対応変更 start ABI
              --,TO_CHAR(SYSDATE, 'YYYYMMDD')
              --,TO_CHAR(SYSDATE, 'HH24MISS')
              ,TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
              ,TO_CHAR(CURRENT_DATE, 'HH24MISS')
-- 本線対応変更 end ABI
              ,sUnyoDate
              ,sIpAddress
              ,PGMID
            );
          ELSIF wShnErrFlg = TRUE AND MeiCreateFlg = FALSE THEN /* 消費税調整以外の特別単価に登録されていない商品が対象*/
            /*AP商品エラーワークテーブルの作成*/
-- 本線対応変更 start ABI
            --INSERT INTO S07W005T_NA
            INSERT INTO S07W005T
-- 本線対応変更 end ABI
            (
               ACIAPKIHONCD    /*ACIAP基本コード*/
              ,ACISHOPCD       /*ACIショップコード*/
              ,APKIHONCD        /*ＡＰ基本コード*/
              ,SHOPCD          /*ショップコード*/
              ,DNPYNO          /*伝票番号*/
              ,URIDNPYMEISAINO    /*売上伝票明細番号*/
              ,URIDNPYMEISAIEDANO    /*売上伝票明細枝番号*/
              ,ACISKYSAKICD      /*ACI請求先コード*/
              ,TOKUISAKICD      /*得意先コード*/
              ,YMD          /*日付*/
              ,SHIMEDYDMDD      /*締年月日*/
              ,SHNCD          /*商品コード*/
              ,SURYO          /*数量*/
              ,UNT          /*単価*/
              ,URIKIN          /*売上金額*/
              ,SHOHITAXKIN      /*消費税金額*/
              ,TOSHAAPKIHONCD      /*登録者ＡＰ基本コード*/
              ,TOSHASHOPCD      /*登録者ショップコード*/
              ,TOSHACD        /*登録者コード*/
              ,INPTSYSTMYMD      /*登録システム日付*/
              ,INPTSYSTMJIKAN      /*登録システム時間*/
              ,INPTUNYOYMD      /*登録運用日付*/
              ,INPTIPADRSE      /*登録ＩＰアドレス*/
              ,INPTPGMID        /*登録プログラムＩＤ*/
              ,KOSHAAPKIHONCD      /*更新者ＡＰ基本コード*/
              ,KOSHASHOPCD      /*更新者ショップコード*/
              ,KOSHACD        /*更新者コード*/
              ,KSHNSYSTMYMD      /*更新システム日付*/
              ,KSHNSYSTMJIKAN      /*更新システム時間*/
              ,KSHNUNYOYMD      /*更新運用日付*/
              ,KSHNIPADRSE      /*更新ＩＰアドレス*/
              ,KSHNPGMID        /*更新プログラムＩＤ*/
            )
            VALUES
            (
               NaAciApkihoncd      /*NA請求変換マスタ.ACIAP基本コード*/
              ,NaAciShopcd         /*NA請求変換マスタ.ACIショップコード*/
              ,NaApApkihoncd        /*NA請求変換マスタ.APAP基本コード*/
              ,NaApShopcd        /*NA請求変換マスタ.APショップコード*/
              ,vCur.URIDNPYNO        /*売上明細テーブル.売上伝票番号*/
              ,vCur.URIDNPYMEISAINO    /*売上明細テーブル.売上伝票明細番号*/
              ,vCur.URIDNPYMEISAIEDANO  /*売上明細テーブル.売上伝票明細枝番号*/
              ,TSkysakicd        /*得意先マスタ.請求先コード*/
              ,NaApTokuisakicd        /*NA請求変換マスタ.AP得意先コード*/
              ,cUriv.URIDD        /*売上テーブル.売上日*/
              ,SUBSTR(CONCAT(cUriv.SKYSHIMEDYDM , cUriv.SKYSHIMEDD) ,0, 8)      /*売上テーブル.売上日*/
              ,vCur.SHNCD          /*売上明細テーブル.商品コード*/
              ,vCur.SURYO          /*売上明細テーブル.数量*/
              ,vCur.UNT          /*売上明細テーブル.単価*/
              ,vCur.KIN          /*売上明細テーブル.金額*/
              ,vCur.SHOHITAXKIN      /*売上明細テーブル.消費税金額*/
              ,vRec.TOSHAAPKIHONCD     /*登録者AP基本コード*/
              ,vRec.TOSHASHOPCD       /*登録者ショップコード*/
              ,iChargeShacd       /*登録者コード*/
-- 本線対応変更 start ABI
              --,TO_CHAR(SYSDATE, 'YYYYMMDD')
              --,TO_CHAR(SYSDATE, 'HH24MISS')
              ,TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
              ,TO_CHAR(CURRENT_DATE, 'HH24MISS')
-- 本線対応変更 end ABI
              ,sUnyoDate
              ,sIpAddress
              ,PGMID
              ,vRec.KOSHAAPKIHONCD     /*更新者AP基本コード*/
              ,vRec.KOSHASHOPCD       /*更新者ショップコード*/
              ,iChargeShacd       /*更新者コード*/
-- 本線対応変更 start ABI
              --,TO_CHAR(SYSDATE, 'YYYYMMDD')
              --,TO_CHAR(SYSDATE, 'HH24MISS')
              ,TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
              ,TO_CHAR(CURRENT_DATE, 'HH24MISS')
-- 本線対応変更 end ABI
              ,sUnyoDate
              ,sIpAddress
              ,PGMID
            );
          END IF ;

        END LOOP;

        IF rUridnpyoStart IS NOT NULL AND ErrorFlg = TRUE THEN /*明細データ(本部商品)が存在する場合*/

          /* セット商品の親と子で消費税額に差がある場合消費税調整レコードを作成する */
          -- セット商品の親と子で消費税額に差がある明細を検索
          FOR curT IN (SELECT URIDNPYNO
                             ,EDANO
                             ,URIDNPYMEISAINO
                             ,MAX(URIDNPYMEISAIEDANO) AS MAXEDANO
                             ,SUM(CASE WHEN (SHNCD = STSHNCD) THEN SHOHITAXKIN ELSE 0 END) AS OYA
                             ,SUM(CASE WHEN (SHNCD = STSHNCD) THEN 0 ELSE SHOHITAXKIN END) AS KO
-- インボイス対応追加 start ABI
                             ,SUM(CASE WHEN (SHNCD = STSHNCD) THEN SANKOUSHOHITAXKIN ELSE 0 END) AS OYASANKOU
                             ,SUM(CASE WHEN (SHNCD = STSHNCD) THEN 0 ELSE SANKOUSHOHITAXKIN END) AS KOSANKOU
-- インボイス対応追加 end ABI
                         FROM S06T002T
                        WHERE APKIHONCD   = NaAciApkihoncd        /*Na請求マスタ.ACIAP基本コード*/
                          AND SHOPCD      = NaAciShopcd           /*Na請求マスタ.ACIショップコード*/
                          AND URIDNPYNO   = rUridnpyoStart
                          AND EDANO       = cUriv.EDANO
                          AND STSHNCD     IS NOT NULL
                        GROUP BY URIDNPYNO
                                ,EDANO
                                ,URIDNPYMEISAINO
-- インボイス対応変更 start ABI
                       --HAVING SUM(CASE WHEN (SHNCD = STSHNCD) THEN SHOHITAXKIN ELSE 0 END) != SUM(CASE WHEN (SHNCD = STSHNCD) THEN 0 ELSE SHOHITAXKIN END) ) LOOP
                       HAVING SUM(CASE WHEN (SHNCD = STSHNCD) THEN SHOHITAXKIN ELSE 0 END) != SUM(CASE WHEN (SHNCD = STSHNCD) THEN 0 ELSE SHOHITAXKIN END)
                           OR SUM(CASE WHEN (SHNCD = STSHNCD) THEN SANKOUSHOHITAXKIN ELSE 0 END) != SUM(CASE WHEN (SHNCD = STSHNCD) THEN 0 ELSE SANKOUSHOHITAXKIN END)
                       ) LOOP
-- インボイス対応変更 end ABI
            BEGIN
              BEGIN
                -- 消費税調整明細行を検索
                SELECT *
                  INTO wChoseiMeisai
                  FROM S06T002T
                 WHERE APKIHONCD          = NaAciApkihoncd
                   AND SHOPCD             = NaAciShopcd
                   AND URIDNPYNO          = curT.URIDNPYNO
                   AND EDANO              = curT.EDANO
                   AND URIDNPYMEISAINO    = curT.URIDNPYMEISAINO
                   AND URIDNPYMEISAIEDANO = curT.MAXEDANO
                   AND MEISAIKBN          = '3'
                   AND YUKOKBN            = '1';
              EXCEPTION
                WHEN NO_DATA_FOUND THEN
                  -- 消費税調整明細が存在しない場合はベースとする親商品明細を取得
                  SELECT *
                    INTO wChoseiMeisai
                    FROM S06T002T
                   WHERE APKIHONCD          = NaAciApkihoncd
                     AND SHOPCD             = NaAciShopcd
                     AND URIDNPYNO          = curT.URIDNPYNO
                     AND EDANO              = curT.EDANO
                     AND URIDNPYMEISAINO    = curT.URIDNPYMEISAINO
                     AND URIDNPYMEISAIEDANO = 0;
              END;

              wChoseiMeisai.SHOHITAXKIN := curT.OYA - curT.KO;  -- 消費税金額
-- インボイス対応追加 start ABI
              wChoseiMeisai.SANKOUSHOHITAXKIN := curT.OYASANKOU - curT.KOSANKOU;  -- 参考消費税金額
-- インボイス対応追加 end ABI

              IF wChoseiMeisai.MEISAIKBN != '3' THEN
                -- 親明細をベースにした場合
                wChoseiMeisai.URIDNPYMEISAIEDANO := curT.MAXEDANO + 1;  -- 売上伝票明細枝番号
                wChoseiMeisai.LCTGRYCD := wChoseiModel.LCTGRYCD; -- 大分類コード
                wChoseiMeisai.MCTGRYCD := wChoseiModel.MCTGRYCD; -- 中分類コード
                wChoseiMeisai.SCTGRYCD := wChoseiModel.SCTGRYCD; -- 小分類コード
                wChoseiMeisai.STSHNCD := wChoseiMeisai.SHNCD;   -- セット商品コード
                wChoseiMeisai.SHNCD := wChoseiModel.SHNCD;       -- 商品コード
                wChoseiMeisai.SURYO := 1;                       -- 数量
                IF cUriv.DLVRYKBN = '3' THEN -- 持ち帰り        -- 標準単価
                  wChoseiMeisai.HYOJUNUNT := getCurrentValue(wChoseiModel.GENZMOCHIKAERIPRICE
                                                   , wChoseiModel.SHINMOCHIKAERIPRICE
                                                   , wChoseiModel.HYOJUNPRICEKIRIKAEDD
                                                   , cUriv.URIDD);
                ELSE
                  wChoseiMeisai.HYOJUNUNT := getCurrentValue(wChoseiModel.GENZHYOJUNPRICE
                                                   , wChoseiModel.SHINHYOJUNPRICE
                                                   , wChoseiModel.HYOJUNPRICEKIRIKAEDD
                                                   , cUriv.URIDD);
                END IF;
                wChoseiMeisai.UNT := 0;                         -- 単価
                wChoseiMeisai.KIN := 0;                         -- 金額
                wChoseiMeisai.UNTSHOHITAXKIN := 0;              -- 単価消費税金額
                wChoseiMeisai.TAXKBN := wRecTaxKbn;             -- 税区分
                wChoseiMeisai.MEISAIKBN := '3';                 -- 明細区分
                wChoseiMeisai.NYURYOKUMEISAIFLG := '0';         -- 入力明細フラグ
                wChoseiMeisai.TANPINMEISAIFLG := '1';           -- 単品明細フラグ
                wChoseiMeisai.MEISAIBIKO := NULL;               -- 明細備考
                wChoseiMeisai.KATASIKI := NULL;                 -- 型式
                wChoseiMeisai.SIIRESURYO := 0;                  -- 仕入数量
                wChoseiMeisai.SIIREKIN := 0;                    -- 仕入金額
                wChoseiMeisai.SIIREUNT := 0;                    -- 仕入単価
                wChoseiMeisai.SIIRESHOHITAXKIN := 0;            -- 仕入消費税金額
                wChoseiMeisai.ZAIKOKSHNSUMIFLG := '1';          -- 在庫更新済フラグ
                -- 消費税調整レコード登録
                INSERT INTO S06T002T VALUES wChoseiMeisai;
              ELSE
                -- 消費税調整レコード更新
                UPDATE S06T002T
                   SET SHOHITAXKIN        = wChoseiMeisai.SHOHITAXKIN
-- インボイス対応追加 start ABI
                     , SANKOUSHOHITAXKIN  = wChoseiMeisai.SANKOUSHOHITAXKIN
-- インボイス対応追加 end ABI
                 WHERE APKIHONCD          = wChoseiMeisai.APKIHONCD
                   AND SHOPCD             = wChoseiMeisai.SHOPCD
                   AND URIDNPYNO          = wChoseiMeisai.URIDNPYNO
                   AND EDANO              = wChoseiMeisai.EDANO
                   AND URIDNPYMEISAINO    = wChoseiMeisai.URIDNPYMEISAINO
                   AND URIDNPYMEISAIEDANO = wChoseiMeisai.URIDNPYMEISAIEDANO;
              END IF;
            END;
          END LOOP;
        /*売上テーブルの作成*/
        INSERT INTO S06T001T
        (
           APKIHONCD        /*ＡＰ基本コード*/
          ,SHOPCD          /*ショップコード*/
          ,URIDNPYNO        /*売上伝票番号*/
          ,EDANO          /*枝番号*/
          ,TORIHIKIKBN      /*取引区分*/
          ,TOKUISAKICD      /*得意先コード*/
          ,SIIRESAKICD      /*仕入先コード*/
          ,URIDD          /*売上日*/
          ,ACPTDD          /*受注日*/
          ,URIKIN          /*売上金額*/
          ,SHOHITAXKIN      /*消費税金額*/
-- インボイス対応追加 start ABI
          ,SANKOUSHOHITAXKIN /*参考消費税金額*/
-- インボイス対応追加 end ABI
          ,EIGYOTNSHACD      /*営業担当者コード*/
          ,SKYSAKICD        /*請求先コード*/
          ,SKYPRCSJISSIDD      /*請求処理実施日*/
          ,GETSUJIPRCSJISSIDD    /*月次処理実施日*/
          ,URISHIMEPRCSDD      /*売上締処理日*/
-- インボイス対応追加 start ABI
          ,NEBIKIPRCSJISSIDD /*値引処理実施日*/
-- インボイス対応追加 end ABI
          ,DNPYBIKO        /*伝票備考*/
          ,SKYSHOOTPTARINASHIKBN  /*請求書出力有無区分*/
          ,SKYPRCSJISSIKBN    /*請求処理実施区分*/
          ,DATSKSMOTOKBN      /*データ作成元区分*/
          ,TAXKEISANKBN      /*税計算区分*/
          ,SIIREDNPYNO      /*仕入伝票番号*/
          ,YUKOKBN        /*有効区分*/
          ,TOSHAAPKIHONCD      /*登録者ＡＰ基本コード*/
          ,TOSHASHOPCD      /*登録者ショップコード*/
          ,TOSHACD        /*登録者コード*/
          ,INPTSYSTMYMD      /*登録システム日付*/
          ,INPTSYSTMJIKAN      /*登録システム時間*/
          ,INPTUNYOYMD      /*登録運用日付*/
          ,INPTIPADRSE      /*登録ＩＰアドレス*/
          ,INPTPGMID        /*登録プログラムＩＤ*/
          ,KOSHAAPKIHONCD      /*更新者ＡＰ基本コード*/
          ,KOSHASHOPCD      /*更新者ショップコード*/
          ,KOSHACD        /*更新者コード*/
          ,KSHNSYSTMYMD      /*更新システム日付*/
          ,KSHNSYSTMJIKAN      /*更新システム時間*/
          ,KSHNUNYOYMD      /*更新運用日付*/
          ,KSHNIPADRSE      /*更新ＩＰアドレス*/
          ,KSHNPGMID        /*更新プログラムＩＤ*/
        )
        VALUES
        (
           NaAciApkihoncd        /*Na請求マスタ.ACIAP基本コード*/
          ,NaAciShopcd       /*Na請求マスタ.ACIショップコード*/
          ,rUridnpyoStart       /*採番マスタ.採番*/
          ,cUriv.EDANO       /*売上テーブル.枝番号*/
          ,cUriv.TORIHIKIKBN   /*売上テーブル.取引区分*/
          ,NaAciTokuisakicd     /*Na請求マスタ.ACI得意先コード*/
          ,NaAciSiiresakicd     /*Na請求マスタ.ACI仕入先コード*/
          ,cUriv.URIDD       /*売上テーブル.売上日*/
          ,cUriv.ACPTDD       /*売上テーブル.受注日*/
          ,SumKingaku         /*売上明細テーブル.金額の合計*/
-- インボイス対応変更 start ABI
          --,SumShohiKingaku     /*売上明細テーブル.消費税金額の合計*/
          -- 売上テーブル.消費税金額が設定されている場合、変数.消費税金額
          ,CASE WHEN cUriv.SHOHITAXKIN IS NOT NULL AND cUriv.SHOHITAXKIN <> 0 THEN SumShohiKingaku
           ELSE 0 END  /*消費税金額*/
-- インボイス対応変更 end ABI
-- インボイス対応追加 start ABI
          -- 売上テーブル.参考消費税金額が設定されている場合、変数.消費税金額
          ,CASE WHEN cUriv.SANKOUSHOHITAXKIN IS NOT NULL AND cUriv.SANKOUSHOHITAXKIN <> 0 THEN SumShohiKingaku
           ELSE 0 END  /*参考消費税金額*/
-- インボイス対応追加 end ABI
          ,TEigyoTnshacd       /*得意先マスタ.営業担当者コード*/
          ,TSkysakicd       /*得意先マスタ.請求先コード*/
          ,'00000000'
          ,'00000000'
            ,cUriv.URIDD       /*売上テーブル.売上日*/
-- インボイス対応追加 start ABI
          ,'00000000'        /*値引処理実施日*/
-- インボイス対応追加 end ABI
          ,cUriv.APKIHONCD || '-' || cUriv.SHOPCD || '-' || cUriv.URIDNPYNO   /*売上テーブル.ACI用APコード + ショップコード + 伝票番号*/
          ,'0'
          ,0
          ,'17'
          ,TaxKBN         /*ショップマスタ.現税計算区分売上 OR 新税計算区分売上*/
          ,rUridnpyoStart     /*採番マスタ.採番*/
          ,'1'
          ,vRec.TOSHAAPKIHONCD     /*登録者AP基本コード*/
          ,vRec.TOSHASHOPCD       /*登録者ショップコード*/
          ,iChargeShacd       /*登録者コード*/
-- 本線対応変更 start ABI
          --,TO_CHAR(SYSDATE, 'YYYYMMDD')
          --,TO_CHAR(SYSDATE, 'HH24MISS')
          ,TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
          ,TO_CHAR(CURRENT_DATE, 'HH24MISS')
-- 本線対応変更 end ABI
          ,sUnyoDate
          ,sIpAddress
          ,PGMID
          ,vRec.KOSHAAPKIHONCD     /*更新者AP基本コード*/
          ,vRec.KOSHASHOPCD       /*更新者ショップコード*/
          ,iChargeShacd       /*更新者コード*/
-- 本線対応変更 start ABI
          --,TO_CHAR(SYSDATE, 'YYYYMMDD')
          --,TO_CHAR(SYSDATE, 'HH24MISS')
          ,TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
          ,TO_CHAR(CURRENT_DATE, 'HH24MISS')
-- 本線対応変更 end ABI
          ,sUnyoDate
          ,sIpAddress
          ,PGMID
        );
--   インボイス対応削除 start ABI
--        /*仕入計上テーブルの作成*/
--        INSERT INTO S11T001T
--        (
--           APKIHONCD        /*AP基本コード*/
--          ,SHOPCD          /*ショップコード*/
--          ,SIIREDNPYNO      /*仕入伝票番号*/
--          ,TORIHIKIKBN      /*取引区分*/
--          ,SIIRESAKICD      /*仕入先コード*/
--          ,SIIREDD        /*仕入日*/
--          ,SHUKKASAKICD      /*出荷先コード*/
--          ,SIIREKIN        /*仕入金額*/
--          ,SHOHITAXKIN      /*消費税金額*/
--          ,JISSEKITNSHACD      /*実績担当者コード*/
--          ,KAIKAKEPRCSJISSIDD    /*買掛処理実施日*/
--          ,GETSUJIPRCSJISSIDD    /*月次処理実施日*/
--          ,DNPYBIKO        /*伝票備考*/
--          ,DATSKSMOTOKBN      /*データ作成元区分*/
--          ,TAXKEISANKBN      /*税計算区分*/
--          ,KEISANHOHO        /*計算方法*/
--          ,YUKOKBN        /*有効区分*/
--          ,TOSHAAPKIHONCD      /*登録者AP基本コード*/
--          ,TOSHASHOPCD      /*登録者ショップコード*/
--          ,TOSHACD        /*登録者コード*/
--          ,INPTSYSTMYMD      /*登録システム日付*/
--          ,INPTSYSTMJIKAN      /*登録システム時間*/
--          ,INPTUNYOYMD      /*登録運用日付*/
--          ,INPTIPADRSE      /*登録IPアドレス*/
--          ,INPTPGMID        /*登録プログラムID*/
--          ,KOSHAAPKIHONCD      /*更新者AP基本コード*/
--          ,KOSHASHOPCD      /*更新者ショップコード*/
--          ,KOSHACD        /*更新者コード*/
--          ,KSHNSYSTMYMD      /*更新システム日付*/
--          ,KSHNSYSTMJIKAN      /*更新システム時間*/
--          ,KSHNUNYOYMD      /*更新運用日付*/
--          ,KSHNIPADRSE      /*更新IPアドレス*/
--          ,KSHNPGMID        /*更新プログラムID*/
--        )
--        VALUES
--        (
--           NaAciApkihoncd        /*Na請求マスタ.ACIAP基本コード*/
--          ,NaAciShopcd       /*Na請求マスタ.ACIショップコード*/
--          ,rUridnpyoStart       /*売上テーブル.ACI売上伝票番号*/
--          ,'3'
--          ,NaAciSiiresakicd     /*Na請求マスタ.ACI仕入先コード*/
--          ,cUriv.URIDD       /*売上テーブル.売上日*/
--          ,NaAciTokuisakicd     /*Na請求マスタ.ACI得意先コード*/
--          ,SumSiireKingaku         /*売上明細テーブル.金額の合計*/
--          ,SumSiireShohiKingaku     /*売上明細テーブル.消費税金額の合計*/
--          ,'000000'
--          ,'00000000'
--          ,'00000000'
--          ,cUriv.APKIHONCD || '-' || cUriv.SHOPCD || '-' || cUriv.URIDNPYNO   /*売上テーブル.ACI用APコード + ショップコード + 伝票番号*/
--          ,'17'
--          ,SiReKeisanKbn       /*仕入先マスタ.税計算区分*/
--          ,SiReKeisanHo       /*仕入先マスタ.計算方法*/
--          ,'1'
--          ,vRec.TOSHAAPKIHONCD     /*登録者AP基本コード*/
--          ,vRec.TOSHASHOPCD       /*登録者ショップコード*/
--          ,iChargeShacd       /*登録者コード*/
--          ,TO_CHAR(SYSDATE, 'YYYYMMDD')
--          ,TO_CHAR(SYSDATE, 'HH24MISS')
--          ,sUnyoDate
--          ,sIpAddress
--          ,PGMID
--          ,vRec.KOSHAAPKIHONCD     /*更新者AP基本コード*/
--          ,vRec.KOSHASHOPCD       /*更新者ショップコード*/
--          ,iChargeShacd       /*更新者コード*/
--          ,TO_CHAR(SYSDATE, 'YYYYMMDD')
--          ,TO_CHAR(SYSDATE, 'HH24MISS')
--          ,sUnyoDate
--          ,sIpAddress
--          ,PGMID
--        );
-- インボイス対応削除 end ABI
        /* NA請求抽出済ワーク*/
        INSERT INTO S07W007T
        (
           ACIAPKIHONCD      /*ACIAP基本コード*/
          ,ACISHOPCD        /*ACIショップコード*/
          ,URIDNPYNO        /*売上伝票番号*/
          ,APAPKIHONCD      /*APAP基本コード*/
          ,APSHOPCD         /*APショップコード*/
          ,APTOKUISAKICD    /*AP得意先コード*/
          ,DNPYNO           /*伝票番号*/
          ,SHIMEDYDM        /*締年月*/
          ,SHIMEDD          /*締日*/
          ,TOSHAAPKIHONCD    /*登録者ＡＰ基本コード*/
          ,TOSHASHOPCD      /*登録者ショップコード*/
          ,TOSHACD          /*登録者コード*/
          ,INPTSYSTMYMD      /*登録システム日付*/
          ,INPTSYSTMJIKAN    /*登録システム時間*/
          ,INPTUNYOYMD      /*登録運用日付*/
          ,INPTIPADRSE      /*登録ＩＰアドレス*/
          ,INPTPGMID        /*登録プログラムＩＤ*/
          ,KOSHAAPKIHONCD    /*更新者ＡＰ基本コード*/
          ,KOSHASHOPCD      /*更新者ショップコード*/
          ,KOSHACD          /*更新者コード*/
          ,KSHNSYSTMYMD      /*更新システム日付*/
          ,KSHNSYSTMJIKAN    /*更新システム時間*/
          ,KSHNUNYOYMD      /*更新運用日付*/
          ,KSHNIPADRSE      /*更新ＩＰアドレス*/
          ,KSHNPGMID        /*更新プログラムＩＤ*/
        )
        VALUES
        (
           NaAciApkihoncd        /*Na請求マスタ.ACIAP基本コード*/
          ,NaAciShopcd       /*Na請求マスタ.ACIショップコード*/
          ,rUridnpyoStart       /*売上テーブル.ACI売上伝票番号*/
          ,NaApApkihoncd        /*NA請求変換マスタ.APAP基本コード*/
          ,NaApShopcd        /*NA請求変換マスタ.APショップコード*/
          ,NaApTokuisakicd        /*NA請求変換マスタ.AP得意先コード*/
          ,cUriv.URIDNPYNO   /*AP側売上伝票番号*/
          ,iPrcsym           /*締年月*/
          ,iShimedayi        /*締日*/
          ,vRec.TOSHAAPKIHONCD     /*登録者AP基本コード*/
          ,vRec.TOSHASHOPCD       /*登録者ショップコード*/
          ,iChargeShacd       /*登録者コード*/
-- 本線対応変更 start ABI
          --,TO_CHAR(SYSDATE, 'YYYYMMDD')
          --,TO_CHAR(SYSDATE, 'HH24MISS')
          ,TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
          ,TO_CHAR(CURRENT_DATE, 'HH24MISS')
-- 本線対応変更 end ABI
          ,sUnyoDate
          ,sIpAddress
          ,PGMID
          ,vRec.KOSHAAPKIHONCD     /*更新者AP基本コード*/
          ,vRec.KOSHASHOPCD       /*更新者ショップコード*/
          ,iChargeShacd       /*更新者コード*/
-- 本線対応変更 start ABI
          --,TO_CHAR(SYSDATE, 'YYYYMMDD')
          --,TO_CHAR(SYSDATE, 'HH24MISS')
          ,TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
          ,TO_CHAR(CURRENT_DATE, 'HH24MISS')
-- 本線対応変更 end ABI
          ,sUnyoDate
          ,sIpAddress
          ,PGMID
        );
          /*NA請求得意先実績ワークに加算分売上金額を登録*/
        UPDATE S07W008T
           SET GENZAIZD       = GENZAIZD + SumKingaku + SumShohiKingaku
             , KOSHAAPKIHONCD = vRec.KOSHAAPKIHONCD    /*更新者AP基本コード*/
             , KOSHASHOPCD    = vRec.KOSHASHOPCD       /*更新者ショップコード*/
             , KOSHACD        = iChargeShacd           /*更新者コード*/
-- 本線対応変更 start ABI
             --, KSHNSYSTMYMD   = TO_CHAR(SYSDATE, 'YYYYMMDD')
             --, KSHNSYSTMJIKAN = TO_CHAR(SYSDATE, 'HH24MISS')
             , KSHNSYSTMYMD   = TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
             , KSHNSYSTMJIKAN = TO_CHAR(CURRENT_DATE, 'HH24MISS')
-- 本線対応変更 end ABI
             , KSHNUNYOYMD    = sUnyoDate
             , KSHNIPADRSE    = sIpAddress
             , KSHNPGMID      = PGMID
         WHERE ACIAPKIHONCD   = NaAciApkihoncd         /*ACIAP基本コード*/
           AND ACISHOPCD      = NaAciShopcd            /*ACIショップコード*/
           AND ACITOKUISAKICD = NaAciTokuisakicd;      /*売上テーブル.得意先コード*/

        IF    SQL%ROWCOUNT    =   0   THEN
          /*NA請求得意先実績ワークに加算分売上金額を登録*/
          INSERT INTO S07W008T
          (
             ACIAPKIHONCD    /*ACIAP基本コード*/
            ,ACISHOPCD       /*ACIショップコード*/
            ,ACITOKUISAKICD  /*ACI得意先コード*/
            ,GENZAIZD        /*現在残高*/
            ,TOSHAAPKIHONCD  /*登録者ＡＰ基本コード*/
            ,TOSHASHOPCD     /*登録者ショップコード*/
            ,TOSHACD         /*登録者コード*/
            ,INPTSYSTMYMD    /*登録システム日付*/
            ,INPTSYSTMJIKAN  /*登録システム時間*/
            ,INPTUNYOYMD     /*登録運用日付*/
            ,INPTIPADRSE     /*登録ＩＰアドレス*/
            ,INPTPGMID       /*登録プログラムＩＤ*/
            ,KOSHAAPKIHONCD  /*更新者ＡＰ基本コード*/
            ,KOSHASHOPCD     /*更新者ショップコード*/
            ,KOSHACD         /*更新者コード*/
            ,KSHNSYSTMYMD    /*更新システム日付*/
            ,KSHNSYSTMJIKAN  /*更新システム時間*/
            ,KSHNUNYOYMD     /*更新運用日付*/
            ,KSHNIPADRSE     /*更新ＩＰアドレス*/
            ,KSHNPGMID       /*更新プログラムＩＤ*/
          )
          VALUES
          (
             NaAciApkihoncd               /*ACIAP基本コード*/
            ,NaAciShopcd                  /*ACIショップコード*/
            ,NaAciTokuisakicd             /*売上テーブル.得意先コード*/
            ,SumKingaku + SumShohiKingaku /*(売上テーブル.金額+消費税金額)*-1*/
            ,vRec.TOSHAAPKIHONCD          /*登録者AP基本コード*/
            ,vRec.TOSHASHOPCD             /*登録者ショップコード*/
            ,iChargeShacd                 /*登録者コード*/
-- 本線対応変更 start ABI
            --,TO_CHAR(SYSDATE, 'YYYYMMDD')
            --,TO_CHAR(SYSDATE, 'HH24MISS')
            ,TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
            ,TO_CHAR(CURRENT_DATE, 'HH24MISS')
-- 本線対応変更 end ABI
            ,sUnyoDate
            ,sIpAddress
            ,PGMID
            ,vRec.KOSHAAPKIHONCD          /*更新者AP基本コード*/
            ,vRec.KOSHASHOPCD             /*更新者ショップコード*/
            ,iChargeShacd                 /*更新者コード*/
-- 本線対応変更 start ABI
            --,TO_CHAR(SYSDATE, 'YYYYMMDD')
            --,TO_CHAR(SYSDATE, 'HH24MISS')
            ,TO_CHAR(CURRENT_DATE, 'YYYYMMDD')
            ,TO_CHAR(CURRENT_DATE, 'HH24MISS')
-- 本線対応変更 end ABI
            ,sUnyoDate
            ,sIpAddress
            ,PGMID
          );

        END IF;
        END IF;
      END IF;
    END LOOP;
  END LOOP;
  /*得意先実績テーブルの現在残高を更新*/
  UPDATE S02M050T TJST
     SET TJST.GENZAIZD =
           CASE WHEN
             TJST.GENZAIZD +
               (SELECT NATW.GENZAIZD FROM S07W008T NATW
                WHERE NATW.ACIAPKIHONCD   = TJST.APKIHONCD
                  AND NATW.ACISHOPCD      = TJST.SHOPCD
                  AND NATW.ACITOKUISAKICD = TJST.TOKUISAKICD) > 99999999 THEN 99999999
           ELSE TJST.GENZAIZD +
               (SELECT NATW.GENZAIZD FROM S07W008T NATW
                WHERE NATW.ACIAPKIHONCD   = TJST.APKIHONCD
                  AND NATW.ACISHOPCD      = TJST.SHOPCD
                  AND NATW.ACITOKUISAKICD = TJST.TOKUISAKICD) END
        ,TJST.KOSHAAPKIHONCD = iApkihoncd                   /*更新者ＡＰ基本コード*/
        ,TJST.KOSHASHOPCD    = iShopcd                      /*更新者ショップコード*/
        ,TJST.KOSHACD        = iChargeShacd                 /*更新者コード*/
-- 本線対応変更 start ABI
        --,TJST.KSHNSYSTMYMD   = TO_CHAR(SYSDATE, 'YYYYMMDD') /*更新システム日付*/
        --,TJST.KSHNSYSTMJIKAN = TO_CHAR(SYSDATE, 'HH24MISS') /*更新システム時間*/
        ,TJST.KSHNSYSTMYMD   = TO_CHAR(CURRENT_DATE, 'YYYYMMDD') /*更新システム日付*/
        ,TJST.KSHNSYSTMJIKAN = TO_CHAR(CURRENT_DATE, 'HH24MISS') /*更新システム時間*/
-- 本線対応変更 end ABI
        ,TJST.KSHNUNYOYMD    = sUnyoDate                    /*更新運用日付*/
        ,TJST.KSHNIPADRSE    = sIpAddress                   /*更新ＩＰアドレス*/
        ,TJST.KSHNPGMID      = PGMID                        /*更新プログラムＩＤ*/
   WHERE EXISTS (
            SELECT 1 FROM S07W008T NATW
            WHERE NATW.ACIAPKIHONCD   = TJST.APKIHONCD
              AND NATW.ACISHOPCD      = TJST.SHOPCD
              AND NATW.ACITOKUISAKICD = TJST.TOKUISAKICD
   );
END getNaData;
END B0602B01;
/
exit;
