package jp.co.aquaclara.accis.common.domain.repository.entity.custom;

import java.io.Serializable;
import java.math.BigDecimal;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M043TEntityKey;
import lombok.Data;

/**
 * Database Table Remarks:
 *   ショップ商品マスタ
 *
 * This class corresponds to the database table S02M043T
 */
@Data
public class S02M043TCustomEntity extends S02M043TEntityKey implements Serializable {
    /**
     * Database Column Remarks:
     *   販売開始日
     *
     */
    private String hanbaistrtdd;

    /**
     * Database Column Remarks:
     *   販売終了日
     *
     */
    private String hanbaishrydd;

    /**
     * Database Column Remarks:
     *   外部連携商品コード
     *
     */
    private String gaiburenkeishncd;

    /**
     * Database Column Remarks:
     *   本部販売管理商品コード
     *
     */
    private String honbuhanbaikanrishncd;

    /**
     * Database Column Remarks:
     *   マージン対象区分
     *
     */
    private String mgntrgtkbn;

    /**
     * Database Column Remarks:
     *   商品区分
     *
     */
    private String shnkbn;

    /**
     * Database Column Remarks:
     *   モバイル利用有無
     *
     */
    private String mobileriyoumu;

    /**
     * Database Column Remarks:
     *   税設定区分
     *
     */
    private String taxsetteikbn;

    /**
     * Database Column Remarks:
     *   税区分
     *
     */
    private String taxkbn;

    /**
     * Database Column Remarks:
     *   仕入取扱単位
     *
     */
    private String siiretrtskitani;

    /**
     * Database Column Remarks:
     *   仕入入数
     *
     */
    private Integer siireirisu;

    /**
     * Database Column Remarks:
     *   仕入単位
     *
     */
    private String siiretani;

    /**
     * Database Column Remarks:
     *   仕入単価
     *
     */
    private BigDecimal siireunt;

    /**
     * Database Column Remarks:
     *   発注ロット数
     *
     */
    private Short hacchurottokazu;

    /**
     * Database Column Remarks:
     *   売上取扱単位
     *
     */
    private String uritrtskitani;

    /**
     * Database Column Remarks:
     *   売上入数
     *
     */
    private Integer uriirisu;

    /**
     * Database Column Remarks:
     *   売上単位
     *
     */
    private String uritani;

    /**
     * Database Column Remarks:
     *   オープン価格切替日
     *
     */
    private String opnpricekirikaedd;

    /**
     * Database Column Remarks:
     *   現オープン価格
     *
     */
    private BigDecimal genzopnprice;

    /**
     * Database Column Remarks:
     *   新オープン価格
     *
     */
    private BigDecimal shinopnprice;

    /**
     * Database Column Remarks:
     *   現オープン持ち帰り価格
     *
     */
    private BigDecimal genzopnmochikaeriprice;

    /**
     * Database Column Remarks:
     *   新オープン持ち帰り価格
     *
     */
    private BigDecimal shinopnmochikaeriprice;

    /**
     * Database Column Remarks:
     *   販促区分
     *
     */
    private String hansokukbn;

    /**
     * Database Column Remarks:
     *   販促開始日
     *
     */
    private String hansokustrtdd;

    /**
     * Database Column Remarks:
     *   販促終了日
     *
     */
    private String hansokushrydd;

    /**
     * Database Column Remarks:
     *   販促名称
     *
     */
    private String hansokunm;

    /**
     * Database Column Remarks:
     *   販促配達価格
     *
     */
    private BigDecimal hansokudlvryprice;

    /**
     * Database Column Remarks:
     *   販促持ち帰り価格
     *
     */
    private BigDecimal hansokumochikaeriprice;

    /**
     * Database Column Remarks:
     *   販促優先区分
     *
     */
    private String hansokuyusenkbn;

    /**
     * Database Column Remarks:
     *   在庫管理対象フラグ
     *
     */
    private String zaikokanritrgtflg;

    /**
     * Database Column Remarks:
     *   原価計算区分
     *
     */
    private String genkakeisankbn;

    /**
     * Database Column Remarks:
     *   標準原価
     *
     */
    private BigDecimal hyojungenka;

    /**
     * Database Column Remarks:
     *   在庫単価
     *
     */
    private BigDecimal zaikount;

    /**
     * Database Column Remarks:
     *   仕入単位変換区分
     *
     */
    private String siiretanihenkankbn;

    /**
     * Database Column Remarks:
     *   在庫アラーム数量
     *
     */
    private Integer zaikoaramusuryo;

    /**
     * Database Column Remarks:
     *   備考
     *
     */
    private String biko;

    /**
     * Database Column Remarks:
     *   会員サイト連携区分
     *
     */
    private String kaiinsaitorenkeikbn;

    /**
     * Database Column Remarks:
     *   有効区分
     *
     */
    private String yukokbn;

    /**
     * Database Column Remarks:
     *   登録者ＡＰ基本コード
     *
     */
    private String toshaapkihoncd;

    /**
     * Database Column Remarks:
     *   登録者ショップコード
     *
     */
    private String toshashopcd;

    /**
     * Database Column Remarks:
     *   登録者コード
     *
     */
    private String toshacd;

    /**
     * Database Column Remarks:
     *   登録システム日付
     *
     */
    private String inptsystmymd;

    /**
     * Database Column Remarks:
     *   登録システム時間
     *
     */
    private String inptsystmjikan;

    /**
     * Database Column Remarks:
     *   登録運用日付
     *
     */
    private String inptunyoymd;

    /**
     * Database Column Remarks:
     *   登録ＩＰアドレス
     *
     */
    private String inptipadrse;

    /**
     * Database Column Remarks:
     *   登録プログラムＩＤ
     *
     */
    private String inptpgmid;

    /**
     * Database Column Remarks:
     *   更新者ＡＰ基本コード
     *
     */
    private String koshaapkihoncd;

    /**
     * Database Column Remarks:
     *   更新者ショップコード
     *
     */
    private String koshashopcd;

    /**
     * Database Column Remarks:
     *   更新者コード
     *
     */
    private String koshacd;

    /**
     * Database Column Remarks:
     *   更新システム日付
     *
     */
    private String kshnsystmymd;

    /**
     * Database Column Remarks:
     *   更新システム時間
     *
     */
    private String kshnsystmjikan;

    /**
     * Database Column Remarks:
     *   更新運用日付
     *
     */
    private String kshnunyoymd;

    /**
     * Database Column Remarks:
     *   更新ＩＰアドレス
     *
     */
    private String kshnipadrse;

    /**
     * Database Column Remarks:
     *   更新プログラムＩＤ
     *
     */
    private String kshnpgmid;
    
    /**
     * Database Column Remarks:
     *   現商品名称
     *
     */
    private String genzshnnm;

    /**
     * This field corresponds to the database table S02M043T
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method corresponds to the database table S02M043T
     *
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", hanbaistrtdd=").append(hanbaistrtdd);
        sb.append(", hanbaishrydd=").append(hanbaishrydd);
        sb.append(", gaiburenkeishncd=").append(gaiburenkeishncd);
        sb.append(", honbuhanbaikanrishncd=").append(honbuhanbaikanrishncd);
        sb.append(", mgntrgtkbn=").append(mgntrgtkbn);
        sb.append(", shnkbn=").append(shnkbn);
        sb.append(", mobileriyoumu=").append(mobileriyoumu);
        sb.append(", taxsetteikbn=").append(taxsetteikbn);
        sb.append(", taxkbn=").append(taxkbn);
        sb.append(", siiretrtskitani=").append(siiretrtskitani);
        sb.append(", siireirisu=").append(siireirisu);
        sb.append(", siiretani=").append(siiretani);
        sb.append(", siireunt=").append(siireunt);
        sb.append(", hacchurottokazu=").append(hacchurottokazu);
        sb.append(", uritrtskitani=").append(uritrtskitani);
        sb.append(", uriirisu=").append(uriirisu);
        sb.append(", uritani=").append(uritani);
        sb.append(", opnpricekirikaedd=").append(opnpricekirikaedd);
        sb.append(", genzopnprice=").append(genzopnprice);
        sb.append(", shinopnprice=").append(shinopnprice);
        sb.append(", genzopnmochikaeriprice=").append(genzopnmochikaeriprice);
        sb.append(", shinopnmochikaeriprice=").append(shinopnmochikaeriprice);
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
        sb.append(", biko=").append(biko);
        sb.append(", kaiinsaitorenkeikbn=").append(kaiinsaitorenkeikbn);
        sb.append(", yukokbn=").append(yukokbn);
        sb.append(", toshaapkihoncd=").append(toshaapkihoncd);
        sb.append(", toshashopcd=").append(toshashopcd);
        sb.append(", toshacd=").append(toshacd);
        sb.append(", inptsystmymd=").append(inptsystmymd);
        sb.append(", inptsystmjikan=").append(inptsystmjikan);
        sb.append(", inptunyoymd=").append(inptunyoymd);
        sb.append(", inptipadrse=").append(inptipadrse);
        sb.append(", inptpgmid=").append(inptpgmid);
        sb.append(", koshaapkihoncd=").append(koshaapkihoncd);
        sb.append(", koshashopcd=").append(koshashopcd);
        sb.append(", koshacd=").append(koshacd);
        sb.append(", kshnsystmymd=").append(kshnsystmymd);
        sb.append(", kshnsystmjikan=").append(kshnsystmjikan);
        sb.append(", kshnunyoymd=").append(kshnunyoymd);
        sb.append(", kshnipadrse=").append(kshnipadrse);
        sb.append(", kshnpgmid=").append(kshnpgmid);
        sb.append(", genzshnnm=").append(genzshnnm);
        sb.append("]");
        return sb.toString();
    }
}