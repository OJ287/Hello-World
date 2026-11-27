package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import lombok.Data;

/**
 * Database Table Remarks: 商品マスタビュー
 *
 * This class corresponds to the database table S02V001T
 */
@Data
public class S02V001TA0301W02Entity implements Serializable {

    /**
     * Database Column Remarks: ＡＰ基本コード
     *
     */
    private String apkihoncd;

    /**
     * Database Column Remarks: ショップコード
     *
     */
    private String shopcd;

    /**
     * Database Column Remarks: 商品コード
     *
     */
    private String shncd;

    /**
     * Database Column Remarks: 商品名称
     *
     */
    private String shnnm;

    /**
     * Database Column Remarks: 商品属性
     *
     */
    private String shnzokusei;

    /**
     * Database Column Remarks: 商品属性名称
     *
     */
    private String shnzokuseinm;

    /**
     * Database Column Remarks: セット区分
     *
     */
    private String stkbn;

    /**
     * Database Column Remarks: セット区分：単独商品
     *
     */
    private String stkbn1;

    /**
     * Database Column Remarks: セット区分：セット商品
     *
     */
    private String stkbn2;

    /**
     * Database Column Remarks: 大分類コード
     *
     */
    private String lctgrycd;

    /**
     * Database Column Remarks: 中分類コード
     *
     */
    private String mctgrycd;

    /**
     * Database Column Remarks: 小分類コード
     *
     */
    private String sctgrycd;

    /**
     * Database Column Remarks: 大分類名称
     *
     */
    private String lctgrynm;

    /**
     * Database Column Remarks: 中分類名称
     *
     */
    private String mctgrynm;

    /**
     * Database Column Remarks: 小分類名称
     *
     */
    private String sctgrynm;

    /**
     * Database Column Remarks: 商品名称切替日
     *
     */
    private String shnnmkirikaedd;

    /**
     * Database Column Remarks: 現商品名称
     *
     */
    private String genzshnnm;

    /**
     * Database Column Remarks: 新商品名称
     *
     */
    private String shinshnnm;

    /**
     * Database Column Remarks: 現商品名称カナ
     *
     */
    private String genzshnnmkana;

    /**
     * Database Column Remarks: 新商品名称カナ
     *
     */
    private String shinshnnmkana;

    /**
     * Database Column Remarks: 現商品名称略称
     *
     */
    private String genzshnnmryakusho;

    /**
     * Database Column Remarks: 新商品名称略称
     *
     */
    private String shinshnnmryakusho;

    /**
     * Database Column Remarks: ショップ価格切替日
     *
     */
    private String shoppricekirikaedd;

    /**
     * Database Column Remarks: 現ショップ価格
     *
     */
    private String genzshopprice;

    /**
     * Database Column Remarks: 新ショップ価格
     *
     */
    private String shinshopprice;

    /**
     * Database Column Remarks: 現ショップ持ち帰り価格
     *
     */
    private String genzshopmochikaeriprice;

    /**
     * Database Column Remarks: 新ショップ持ち帰り価格
     *
     */
    private String shinshopmochikaeriprice;

    /**
     * Database Column Remarks: 色
     *
     */
    private String iro;

    /**
     * Database Column Remarks: サイズ
     *
     */
    private String sz;

    /**
     * Database Column Remarks: 取扱区分
     *
     */
    private String trtskikbn;

    /**
     * Database Column Remarks: 取扱区分名称
     *
     */
    private String trtskikbnnm;

    /**
     * Database Column Remarks: 数量端数管理区分
     *
     */
    private String suryohasukanrikbn;

    /**
     * Database Column Remarks: 販売開始日
     *
     */
    private String hanbaistrtdd;

    /**
     * Database Column Remarks: 販売終了日
     *
     */
    private String hanbaishrydd;

    /**
     * Database Column Remarks: 外部連携商品コード
     *
     */
    private String gaiburenkeishncd;

    /**
     * Database Column Remarks: 本部販売管理商品コード
     *
     */
    private String honbuhanbaikanrishncd;

    /**
     * Database Column Remarks: マージン対象区分
     *
     */
    private String mgntrgtkbn;

    /**
     * Database Column Remarks: 商品区分
     *
     */
    private String shnkbn;

    /**
     * Database Column Remarks: 税設定区分
     *
     */
    private String taxsetteikbn;

    /**
     * Database Column Remarks: 税区分
     *
     */
    private String taxkbn;

    /**
     * Database Column Remarks: 仕入入数
     *
     */
    private String siireirisu;

    /**
     * Database Column Remarks: 仕入単位
     *
     */
    private String siiretani;

    /**
     * Database Column Remarks: 仕入単位名称
     *
     */
    private String siiretaninm;

    /**
     * Database Column Remarks: 仕入単価
     *
     */
    private String siireunt;

    /**
     * Database Column Remarks: 発注ロット数
     *
     */
    private String hacchurottokazu;

    /**
     * Database Column Remarks: 仕入取扱単位
     *
     */
    private String siiretrtskitani;

    /**
     * Database Column Remarks: 売上取扱単位
     *
     */
    private String uritrtskitani;

    /**
     * Database Column Remarks: 売上取扱単位名称
     *
     */
    private String uritrtskitaninm;

    /**
     * Database Column Remarks: 売上入数
     *
     */
    private String uriirisu;

    /**
     * Database Column Remarks: 売上単位
     *
     */
    private String uritani;

    /**
     * Database Column Remarks: 販促区分
     *
     */
    private String hansokukbn;

    /**
     * Database Column Remarks: 販促開始日
     *
     */
    private String hansokustrtdd;

    /**
     * Database Column Remarks: 販促終了日
     *
     */
    private String hansokushrydd;

    /**
     * Database Column Remarks: 販促名称
     *
     */
    private String hansokunm;

    /**
     * Database Column Remarks: 販促配達価格
     *
     */
    private String hansokudlvryprice;

    /**
     * Database Column Remarks: 販促持ち帰り価格
     *
     */
    private String hansokumochikaeriprice;

    /**
     * Database Column Remarks: 販促優先区分
     *
     */
    private String hansokuyusenkbn;

    /**
     * Database Column Remarks: 在庫管理対象フラグ
     *
     */
    private String zaikokanritrgtflg;

    /**
     * Database Column Remarks: 原価計算区分
     *
     */
    private String genkakeisankbn;

    /**
     * Database Column Remarks: 標準原価
     *
     */
    private String hyojungenka;

    /**
     * Database Column Remarks: 在庫単価
     *
     */
    private String zaikount;

    /**
     * Database Column Remarks: 仕入単位変換区分
     *
     */
    private String siiretanihenkankbn;

    /**
     * Database Column Remarks: 商品ＡＰ基本コード
     *
     */
    private String shnapkihoncd;

    /**
     * Database Column Remarks: 在庫アラーム数量
     *
     */
    private String zaikoaramusuryo;

    /**
     * Database Column Remarks: 標準価格切替日
     *
     */
    private String hyojunpricekirikaedd;

    /**
     * Database Column Remarks: 現標準価格
     *
     */
    private String genzhyojunprice;

    /**
     * Database Column Remarks: 新標準価格
     *
     */
    private String shinhyojunprice;

    /**
     * Database Column Remarks: 現持ち帰り価格
     *
     */
    private String genzmochikaeriprice;

    /**
     * Database Column Remarks: 新持ち帰り価格
     *
     */
    private String shinmochikaeriprice;

    /**
     * Database Column Remarks: 価格区分
     *
     */
    private String pricekbn;

    /**
     * Database Column Remarks: 価格区分名称
     *
     */
    private String pricekbnnm;

    /**
     * Database Column Remarks: 現価格区分
     *
     */
    private String genzpricekbn;

    /**
     * Database Column Remarks: 現価格区分名称
     *
     */
    private String genzpricekbnnm;

    /**
     * Database Column Remarks: 新価格区分
     *
     */
    private String shinpricekbn;

    /**
     * Database Column Remarks: 新価格区分名称
     *
     */
    private String shinpricekbnnm;

    /**
     * Database Column Remarks: 基本商品備考
     *
     */
    private String kihonshnbiko;

    /**
     * Database Column Remarks: 基本商品登録者ＡＰ基本コード
     *
     */
    private String kihonshntoshaapkihoncd;

    /**
     * Database Column Remarks: 基本商品登録者ショップコード
     *
     */
    private String kihonshntoshashopcd;

    /**
     * Database Column Remarks: 基本商品登録者コード
     *
     */
    private String kihonshntoshacd;

    /**
     * Database Column Remarks: 基本商品登録者名称
     *
     */
    private String kihonshntoshanm;

    /**
     * Database Column Remarks: 基本商品登録システム日付
     *
     */
    private String kihonshninptsystmymd;

    /**
     * Database Column Remarks: 基本商品登録システム時間
     *
     */
    private String kihonshninptsystmjikan;

    /**
     * Database Column Remarks: 基本商品更新者ＡＰ基本コード
     *
     */
    private String kihonshnkoshaapkihoncd;

    /**
     * Database Column Remarks: 基本商品更新者ショップコード
     *
     */
    private String kihonshnkoshashopcd;

    /**
     * Database Column Remarks: 基本商品更新者コード
     *
     */
    private String kihonshnkoshacd;

    /**
     * Database Column Remarks: 基本商品更新者名称
     *
     */
    private String kihonshnkoshanm;

    /**
     * Database Column Remarks: 基本商品更新システム日付
     *
     */
    private String kihonshnkshnsystmymd;

    /**
     * Database Column Remarks: 基本商品更新システム時間
     *
     */
    private String kihonshnkshnsystmjikan;

    /**
     * Database Column Remarks: オープン価格切替日
     *
     */
    private String opnpricekirikaedd;

    /**
     * Database Column Remarks: 現オープン価格
     *
     */
    private String genzopnprice;

    /**
     * Database Column Remarks: 新オープン価格
     *
     */
    private String shinopnprice;

    /**
     * Database Column Remarks: 現オープン持ち帰り価格
     *
     */
    private String genzopnmochikaeriprice;

    /**
     * Database Column Remarks: 新オープン持ち帰り価格
     *
     */
    private String shinopnmochikaeriprice;

    /**
     * Database Column Remarks: ショップ備考
     *
     */
    private String shopbiko;

    /**
     * Database Column Remarks: 基本商品有効区分
     *
     */
    private String kihonshnyukokbn;

    /**
     * Database Column Remarks: ショップ有効区分
     *
     */
    private String shopyukokbn;

    /**
     * Database Column Remarks: ショップ登録者ＡＰ基本コード
     *
     */
    private String shoptoshaapkihoncd;

    /**
     * Database Column Remarks: ショップ登録者ショップコード
     *
     */
    private String shoptoshashopcd;

    /**
     * Database Column Remarks: ショップ登録者コード
     *
     */
    private String shoptoshacd;

    /**
     * Database Column Remarks: ショップ登録者名称
     *
     */
    private String shoptoshanm;

    /**
     * Database Column Remarks: ショップ登録システム日付
     *
     */
    private String shopinptsystmymd;

    /**
     * Database Column Remarks: ショップ登録システム時間
     *
     */
    private String shopinptsystmjikan;

    /**
     * Database Column Remarks: ショップ更新者ＡＰ基本コード
     *
     */
    private String shopkoshaapkihoncd;

    /**
     * Database Column Remarks: ショップ更新者ショップコード
     *
     */
    private String shopkoshashopcd;

    /**
     * Database Column Remarks: ショップ更新者コード
     *
     */
    private String shopkoshacd;

    /**
     * Database Column Remarks: ショップ更新者名称
     *
     */
    private String shopkoshanm;

    /**
     * Database Column Remarks: ショップ更新システム日付
     *
     */
    private String shopkshnsystmymd;

    /**
     * Database Column Remarks: ショップ更新システム時間
     *
     */
    private String shopkshnsystmjikan;

    /**
     * Database Column Remarks: 会員サイト連携区分
     *
     */
    private String kaiinsaitorenkeikbn;

    /**
     * Database Column Remarks: モバイル利用有無
     *
     */
    private String mobileriyoumu;

    /**
     * Database Column Remarks: 登録者ＡＰ基本コード
     *
     */
    private String toshaapkihoncd;

    /**
     * Database Column Remarks: 登録者ショップコード
     *
     */
    private String toshashopcd;

    /**
     * Database Column Remarks: 登録者コード
     *
     */
    private String toshacd;

    /**
     * Database Column Remarks: 登録者名称
     *
     */
    private String toshanm;

    /**
     * Database Column Remarks: 登録システム日付
     *
     */
    private String inptsystmymd;

    /**
     * Database Column Remarks: 登録システム時間
     *
     */
    private String inptsystmjikan;

    /**
     * Database Column Remarks: 更新者ＡＰ基本コード
     *
     */
    private String koshaapkihoncd;

    /**
     * Database Column Remarks: 更新者ショップコード
     *
     */
    private String koshashopcd;

    /**
     * Database Column Remarks: 更新者コード
     *
     */
    private String koshacd;

    /**
     * Database Column Remarks: 更新者名称
     *
     */
    private String koshanm;

    /**
     * Database Column Remarks: 更新システム日付
     *
     */
    private String kshnsystmymd;

    /**
     * Database Column Remarks: 更新システム時間
     *
     */
    private String kshnsystmjikan;

    /**
     */
    private String unyodate;

    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", apkihoncd=").append(apkihoncd);
        sb.append(", shopcd=").append(shopcd);
        sb.append(", shncd=").append(shncd);
        sb.append(", shnnm=").append(shnnm);
        sb.append(", shnzokusei=").append(shnzokusei);
        sb.append(", shnzokuseinm=").append(shnzokuseinm);
        sb.append(", stkbn=").append(stkbn);
        sb.append(", lctgrycd=").append(lctgrycd);
        sb.append(", mctgrycd=").append(mctgrycd);
        sb.append(", sctgrycd=").append(sctgrycd);
        sb.append(", lctgrynm=").append(lctgrynm);
        sb.append(", mctgrynm=").append(mctgrynm);
        sb.append(", sctgrynm=").append(sctgrynm);
        sb.append(", shnnmkirikaedd=").append(shnnmkirikaedd);
        sb.append(", genzshnnm=").append(genzshnnm);
        sb.append(", shinshnnm=").append(shinshnnm);
        sb.append(", genzshnnmkana=").append(genzshnnmkana);
        sb.append(", shinshnnmkana=").append(shinshnnmkana);
        sb.append(", genzshnnmryakusho=").append(genzshnnmryakusho);
        sb.append(", shinshnnmryakusho=").append(shinshnnmryakusho);
        sb.append(", shoppricekirikaedd=").append(shoppricekirikaedd);
        sb.append(", genzshopprice=").append(genzshopprice);
        sb.append(", shinshopprice=").append(shinshopprice);
        sb.append(", genzshopmochikaeriprice=").append(genzshopmochikaeriprice);
        sb.append(", shinshopmochikaeriprice=").append(shinshopmochikaeriprice);
        sb.append(", iro=").append(iro);
        sb.append(", sz=").append(sz);
        sb.append(", trtskikbn=").append(trtskikbn);
        sb.append(", trtskikbnnm=").append(trtskikbnnm);
        sb.append(", suryohasukanrikbn=").append(suryohasukanrikbn);
        sb.append(", hanbaistrtdd=").append(hanbaistrtdd);
        sb.append(", hanbaishrydd=").append(hanbaishrydd);
        sb.append(", gaiburenkeishncd=").append(gaiburenkeishncd);
        sb.append(", honbuhanbaikanrishncd=").append(honbuhanbaikanrishncd);
        sb.append(", mgntrgtkbn=").append(mgntrgtkbn);
        sb.append(", shnkbn=").append(shnkbn);
        sb.append(", taxsetteikbn=").append(taxsetteikbn);
        sb.append(", taxkbn=").append(taxkbn);
        sb.append(", siireirisu=").append(siireirisu);
        sb.append(", siiretani=").append(siiretani);
        sb.append(", siiretaninm=").append(siiretaninm);
        sb.append(", siireunt=").append(siireunt);
        sb.append(", hacchurottokazu=").append(hacchurottokazu);
        sb.append(", siiretrtskitani=").append(siiretrtskitani);
        sb.append(", uritrtskitani=").append(uritrtskitani);
        sb.append(", uritrtskitaninm=").append(uritrtskitaninm);
        sb.append(", uriirisu=").append(uriirisu);
        sb.append(", uritani=").append(uritani);
        sb.append(", hansokukbn=").append(hansokukbn);
        sb.append(", hansokustrtdd=").append(hansokustrtdd);
        sb.append(", hansokushrydd=").append(hansokushrydd);
        sb.append(", hansokunm=").append(hansokunm);
        sb.append(", hansokudlvryprice=").append(hansokudlvryprice);
        sb.append(", hansokumochikaeriprice=").append(hansokumochikaeriprice);
        sb.append(", hansokuyusenkbn=").append(hansokuyusenkbn);
        sb.append(", zaikokanritrgtflg=").append(zaikokanritrgtflg);
        sb.append(", genkakeisankbn=").append(genkakeisankbn);
        sb.append(", hyojungenka=").append(hyojungenka);
        sb.append(", zaikount=").append(zaikount);
        sb.append(", siiretanihenkankbn=").append(siiretanihenkankbn);
        sb.append(", zaikoaramusuryo=").append(zaikoaramusuryo);
        sb.append(", shnapkihoncd=").append(shnapkihoncd);
        sb.append(", hyojunpricekirikaedd=").append(hyojunpricekirikaedd);
        sb.append(", genzhyojunprice=").append(genzhyojunprice);
        sb.append(", shinhyojunprice=").append(shinhyojunprice);
        sb.append(", genzmochikaeriprice=").append(genzmochikaeriprice);
        sb.append(", shinmochikaeriprice=").append(shinmochikaeriprice);
        sb.append(", pricekbn=").append(pricekbn);
        sb.append(", pricekbnnm=").append(pricekbnnm);
        sb.append(", genzpricekbn=").append(genzpricekbn);
        sb.append(", genzpricekbnnm=").append(genzpricekbnnm);
        sb.append(", shinpricekbn=").append(shinpricekbn);
        sb.append(", shinpricekbnnm=").append(shinpricekbnnm);
        sb.append(", kihonshnbiko=").append(kihonshnbiko);
        sb.append(", kihonshntoshaapkihoncd=").append(kihonshntoshaapkihoncd);
        sb.append(", kihonshntoshashopcd=").append(kihonshntoshashopcd);
        sb.append(", kihonshntoshacd=").append(kihonshntoshacd);
        sb.append(", kihonshntoshanm=").append(kihonshntoshanm);
        sb.append(", kihonshninptsystmymd=").append(kihonshninptsystmymd);
        sb.append(", kihonshninptsystmjikan=").append(kihonshninptsystmjikan);
        sb.append(", kihonshnkoshaapkihoncd=").append(kihonshnkoshaapkihoncd);
        sb.append(", kihonshnkoshashopcd=").append(kihonshnkoshashopcd);
        sb.append(", kihonshnkoshacd=").append(kihonshnkoshacd);
        sb.append(", kihonshnkoshanm=").append(kihonshnkoshanm);
        sb.append(", kihonshnkshnsystmymd=").append(kihonshnkshnsystmymd);
        sb.append(", kihonshnkshnsystmjikan=").append(kihonshnkshnsystmjikan);
        sb.append(", opnpricekirikaedd=").append(opnpricekirikaedd);
        sb.append(", genzopnprice=").append(genzopnprice);
        sb.append(", shinopnprice=").append(shinopnprice);
        sb.append(", genzopnmochikaeriprice=").append(genzopnmochikaeriprice);
        sb.append(", shinopnmochikaeriprice=").append(shinopnmochikaeriprice);
        sb.append(", shopbiko=").append(shopbiko);
        sb.append(", kihonshnyukokbn=").append(kihonshnyukokbn);
        sb.append(", shopyukokbn=").append(shopyukokbn);
        sb.append(", shoptoshaapkihoncd=").append(shoptoshaapkihoncd);
        sb.append(", shoptoshashopcd=").append(shoptoshashopcd);
        sb.append(", shoptoshacd=").append(shoptoshacd);
        sb.append(", shoptoshanm=").append(shoptoshanm);
        sb.append(", shopinptsystmymd=").append(shopinptsystmymd);
        sb.append(", shopinptsystmjikan=").append(shopinptsystmjikan);
        sb.append(", shopkoshaapkihoncd=").append(shopkoshaapkihoncd);
        sb.append(", shopkoshashopcd=").append(shopkoshashopcd);
        sb.append(", shopkoshacd=").append(shopkoshacd);
        sb.append(", shopkoshanm=").append(shopkoshanm);
        sb.append(", shopkshnsystmymd=").append(shopkshnsystmymd);
        sb.append(", shopkshnsystmjikan=").append(shopkshnsystmjikan);
        sb.append(", kaiinsaitorenkeikbn=").append(kaiinsaitorenkeikbn);
        sb.append(", mobileriyoumu=").append(mobileriyoumu);
        sb.append(", toshaapkihoncd=").append(toshaapkihoncd);
        sb.append(", toshashopcd=").append(toshashopcd);
        sb.append(", toshacd=").append(toshacd);
        sb.append(", toshanm=").append(toshanm);
        sb.append(", inptsystmymd=").append(inptsystmymd);
        sb.append(", inptsystmjikan=").append(inptsystmjikan);
        sb.append(", koshaapkihoncd=").append(koshaapkihoncd);
        sb.append(", koshashopcd=").append(koshashopcd);
        sb.append(", koshacd=").append(koshacd);
        sb.append(", koshanm=").append(koshanm);
        sb.append(", kshnsystmymd=").append(kshnsystmymd);
        sb.append(", kshnsystmjikan=").append(kshnsystmjikan);
        sb.append(", unyodate=").append(unyodate);
        sb.append("]");

        return sb.toString();
    }
}
