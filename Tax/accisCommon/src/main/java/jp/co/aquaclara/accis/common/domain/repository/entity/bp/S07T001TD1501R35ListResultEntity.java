package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntityKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 月締めの入金兼領収書印刷データ取得結果Entity.
 * 
 * @author accisdev047
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class S07T001TD1501R35ListResultEntity extends S07T001TEntityKey implements Serializable {
    /**
     * serialVersionUID
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * Database Column Remarks:繰越金額
     *
     */
    private String kurikoshikin;
    /**
     * Database Column Remarks:今回税抜売上金額
     *
     */
    private String konkaitaxnukiurikin;
    /**
     * Database Column Remarks:今回消費税金額
     *
     */
    private String konkaishohitaxkin;
    /**
     * Database Column Remarks:請求先コード
     *
     */
    private String skysakicd;
    /**
     * Database Column Remarks:得意先コード
     *
     */
    private String tokuisakicd;
    /**
     * Database Column Remarks:集金日,支払予定年月日
     *
     */
    private String shukindydmdd;
    /**
     * Database Column Remarks:請求区分
     *
     */
    private String skykbn;
    /**
     * Database Column Remarks:請求方法区分
     *
     */
    private String skyhohokbn;
    /**
     * Database Column Remarks:明細印刷区分
     *
     */
    private String meisaiprntkbn;
    /**
     * Database Column Remarks:請求先郵便番号
     *
     */
    private String sksyubinno;
    /**
     * Database Column Remarks:請求先住所１
     *
     */
    private String sksadrsn1;
    /**
     * Database Column Remarks:請求先住所２
     *
     */
    private String sksadrsn2;
    /**
     * Database Column Remarks:請求先住所３
     *
     */
    private String sksadrsn3;
    /**
     * Database Column Remarks:請求先連絡先番号区分１
     *
     */
    private String sksrenrakusakinokbnn1;
    /**
     * Database Column Remarks:請求先連絡先番号１
     *
     */
    private String sksrenrakusakinon1;
    /**
     * Database Column Remarks:請求先連絡先番号区分２
     *
     */
    private String sksrenrakusakinokbnn2;
    /**
     * Database Column Remarks:請求先連絡先番号２
     *
     */
    private String sksrenrakusakinon2;
    /**
     * Database Column Remarks:請求先連絡先番号区分３
     *
     */
    private String sksrenrakusakinokbnn3;
    /**
     * Database Column Remarks:請求先連絡先番号３
     *
     */
    private String sksrenrakusakinon3;
    /**
     * Database Column Remarks:請求先連絡先番号区分４
     *
     */
    private String sksrenrakusakinokbnn4;
    /**
     * Database Column Remarks:請求先連絡先番号４
     *
     */
    private String sksrenrakusakinon4;
    /**
     * Database Column Remarks:請求先名称１
     *
     */
    private String sksskysakinmn1;
    /**
     * Database Column Remarks:請求先名称２
     *
     */
    private String sksskysakinmn2;
    /**
     * Database Column Remarks:請求先敬称区分
     *
     */
    private String sksskysakikeishokbn;
    /**
     * Database Column Remarks:請求先敬称区分名称
     *
     */
    private String sksskysakikeishokbnnm;
    /**
     * Database Column Remarks:得意先郵便番号
     *
     */
    private String tksyubinno;
    /**
     * Database Column Remarks:得意先住所１
     *
     */
    private String tksadrsn1;
    /**
     * Database Column Remarks:得意先住所２
     *
     */
    private String tksadrsn2;
    /**
     * Database Column Remarks:得意先住所３
     *
     */
    private String tksadrsn3;
    /**
     * Database Column Remarks:得意先連絡先番号区分１
     *
     */
    private String tksrenrakusakinokbnn1;
    /**
     * Database Column Remarks:得意先連絡先番号１
     *
     */
    private String tksrenrakusakinon1;
    /**
     * Database Column Remarks:得意先連絡先番号区分２
     *
     */
    private String tksrenrakusakinokbnn2;
    /**
     * Database Column Remarks:得意先連絡先番号２
     *
     */
    private String tksrenrakusakinon2;
    /**
     * Database Column Remarks:得意先連絡先番号区分３
     *
     */
    private String tksrenrakusakinokbnn3;
    /**
     * Database Column Remarks:得意先連絡先番号３
     *
     */
    private String tksrenrakusakinon3;
    /**
     * Database Column Remarks:得意先連絡先番号区分４
     *
     */
    private String tksrenrakusakinokbnn4;
    /**
     * Database Column Remarks:得意先連絡先番号４
     *
     */
    private String tksrenrakusakinon4;
    /**
     * Database Column Remarks:得意先名称１
     *
     */
    private String tkstokuisakinmn1;
    /**
     * Database Column Remarks:得意先名称２
     *
     */
    private String tkstokuisakinmn2;
    /**
     * Database Column Remarks:得意先敬称区分
     *
     */
    private String tkstokuisakikeishokbn;
    /**
     * Database Column Remarks:得意先敬称区分
     *
     */
    private String tkstokuisakikeishokbnnm;
    /**
     * Database Column Remarks:得意先区分
     *
     */
    private String tkstokuisakikbn;
    /**
     * Database Column Remarks:担当者名
     *
     */
    private String tnshamei;
    /**
     * Database Column Remarks:ショップ名称
     *
     */
    private String shopnm;
    /**
     * Database Column Remarks:差出人イメージ
     *
     */
    private String sshdshhitoimage;
    /**
     * Database Column Remarks:回収方法区分
     *
     */
    private String kaishuhohokbn;
    /**
     * Database Column Remarks:領収金額
     *
     */
    private String ryosyukin;

    /**
     * toString
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", kurikoshikin=").append(kurikoshikin);
        sb.append(", konkaitaxnukiurikin=").append(konkaitaxnukiurikin);
        sb.append(", konkaishohitaxkin=").append(konkaishohitaxkin);
        sb.append(", skysakicd=").append(skysakicd);
        sb.append(", tokuisakicd=").append(tokuisakicd);
        sb.append(", shukindydmdd=").append(shukindydmdd);
        sb.append(", skykbn=").append(skykbn);
        sb.append(", skyhohokbn=").append(skyhohokbn);
        sb.append(", meisaiprntkbn=").append(meisaiprntkbn);
        sb.append(", sksyubinno=").append(sksyubinno);
        sb.append(", sksadrsn1=").append(sksadrsn1);
        sb.append(", sksadrsn2=").append(sksadrsn2);
        sb.append(", sksadrsn3=").append(sksadrsn3);
        sb.append(", sksrenrakusakinokbnn1=").append(sksrenrakusakinokbnn1);
        sb.append(", sksrenrakusakinon1=").append(sksrenrakusakinon1);
        sb.append(", sksrenrakusakinokbnn2=").append(sksrenrakusakinokbnn2);
        sb.append(", sksrenrakusakinon2=").append(sksrenrakusakinon2);
        sb.append(", sksrenrakusakinokbnn3=").append(sksrenrakusakinokbnn3);
        sb.append(", sksrenrakusakinon3=").append(sksrenrakusakinon3);
        sb.append(", sksrenrakusakinokbnn4=").append(sksrenrakusakinokbnn4);
        sb.append(", sksrenrakusakinon4=").append(sksrenrakusakinon4);
        sb.append(", sksskysakinmn1=").append(sksskysakinmn1);
        sb.append(", sksskysakinmn2=").append(sksskysakinmn2);
        sb.append(", sksskysakikeishokbn=").append(sksskysakikeishokbn);
        sb.append(", sksskysakikeishokbnnm=").append(sksskysakikeishokbnnm);
        sb.append(", tksyubinno=").append(tksyubinno);
        sb.append(", tksadrsn1=").append(tksadrsn1);
        sb.append(", tksadrsn2=").append(tksadrsn2);
        sb.append(", tksadrsn3=").append(tksadrsn3);
        sb.append(", tksrenrakusakinokbnn1=").append(tksrenrakusakinokbnn1);
        sb.append(", tksrenrakusakinon1=").append(tksrenrakusakinon1);
        sb.append(", tksrenrakusakinokbnn2=").append(tksrenrakusakinokbnn2);
        sb.append(", tksrenrakusakinon2=").append(tksrenrakusakinon2);
        sb.append(", tksrenrakusakinokbnn3=").append(tksrenrakusakinokbnn3);
        sb.append(", tksrenrakusakinon3=").append(tksrenrakusakinon3);
        sb.append(", tksrenrakusakinokbnn4=").append(tksrenrakusakinokbnn4);
        sb.append(", tksrenrakusakinon4=").append(tksrenrakusakinon4);
        sb.append(", tkstokuisakinmn1=").append(tkstokuisakinmn1);
        sb.append(", tkstokuisakinmn2=").append(tkstokuisakinmn2);
        sb.append(", tkstokuisakikeishokbn=").append(tkstokuisakikeishokbn);
        sb.append(", tkstokuisakikeishokbnnm=").append(tkstokuisakikeishokbnnm);
        sb.append(", tkstokuisakikbn=").append(tkstokuisakikbn);
        sb.append(", tnshamei=").append(tnshamei);
        sb.append(", shopnm=").append(shopnm);
        sb.append(", sshdshhitoimage=").append(sshdshhitoimage);
        sb.append(", kaishuhohokbn=").append(kaishuhohokbn);
        sb.append(", ryosyukin=").append(ryosyukin);
        sb.append("]");
        return sb.toString();
    }
}
