package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntityKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 請求書CSVデータ取得結果Entity.
 * 
 * @author accisdev047
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class S07T001TD1501R33ListResultEntity extends S07T001TEntityKey implements Serializable {
    /**
     * serialVersionUID
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * Database Column Remarks:請求先コード
     *
     */
    private String skysakicd;
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
    private String yubinno;
    /**
     * Database Column Remarks:請求先住所１
     *
     */
    private String adrsn1;
    /**
     * Database Column Remarks:請求先住所２
     *
     */
    private String adrsn2;
    /**
     * Database Column Remarks:請求先住所３
     *
     */
    private String adrsn3;
    /**
     * Database Column Remarks:請求先名称１
     *
     */
    private String skysakinmn1;
    /**
     * Database Column Remarks:請求先名称２
     *
     */
    private String skysakinmn2;
    /**
     * Database Column Remarks:請求先敬称区分
     *
     */
    private String skysakikeishokbn;
    /**
     * Database Column Remarks:請求先敬称区分名
     *
     */
    private String skysakikeishokbnnm;
    /**
     * Database Column Remarks:郵便番号バーコード
     *
     */
    private String yubinnobakodo;
    /**
     * Database Column Remarks:税計算区分
     *
     */
    private String taxkeisankbn;
    /**
     * Database Column Remarks:印刷備考
     *
     */
    private String prntbiko;
    /**
     * Database Column Remarks:伝票お知らせ内容１
     *
     */
    private String dnpyoshirasenaiyon1;
    /**
     * Database Column Remarks:伝票お知らせ内容２
     *
     */
    private String dnpyoshirasenaiyon2;
    /**
     * Database Column Remarks:伝票お知らせ内容３
     *
     */
    private String dnpyoshirasenaiyon3;
    /**
     * Database Column Remarks:伝票お知らせ内容４
     *
     */
    private String dnpyoshirasenaiyon4;
    /**
     * Database Column Remarks:伝票お知らせ内容５
     *
     */
    private String dnpyoshirasenaiyon5;
    /**
     * Database Column Remarks:伝票お知らせ内容６
     *
     */
    private String dnpyoshirasenaiyon6;
    /**
     * Database Column Remarks:お知らせ２
     *
     */
    private String dnpyoshirase2;
    /**
     * Database Column Remarks:前回ご請求額
     *
     */
    private String znkkurikoshikin;
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
     * Database Column Remarks:今回入金金額
     *
     */
    private String konkainyknkin;
    /**
     * Database Column Remarks:繰越金額
     *
     */
    private String kurikoshikin;
    /**
     * Database Column Remarks:得意先コード
     *
     */
    private String tokuisakicd;
    /**
     * Database Column Remarks:得意先名称
     *
     */
    private String tokuisakinmn1;
    /**
     * Database Column Remarks:銀行名称１
     *
     */
    private String kinyukknnm;
    /**
     * Database Column Remarks支店名称１:
     *
     */
    private String kinyukknshitennm;
    /**
     * Database Column Remarks:口座種別１
     *
     */
    private String kinyukknkzkind;
    /**
     * Database Column Remarks:振込口座番号１
     *
     */
    private String kinyukknkzno;
    /**
     * Database Column Remarks:銀行名称２
     *
     */
    private String kinyukknn2nm;
    /**
     * Database Column Remarks:支店名称２
     *
     */
    private String kinyukknn2shitennm;
    /**
     * Database Column Remarks:口座種別２
     *
     */
    private String kinyukknn2kzkind;
    /**
     * Database Column Remarks:振込口座番号２
     *
     */
    private String kinyukknn2kzno;
    /**
     * Database Column Remarks:日付明細
     *
     */
    private String uridd;
    /**
     * Database Column Remarks:入力明細フラグ
     *
     */
    private String nyuryokumeisaiflg;
    /**
     * Database Column Remarks:単品明細フラグ
     *
     */
    private String tanpinmeisaiflg;
    /**
     * Database Column Remarks:伝票番号明細
     *
     */
    private String uridnpyno;
    /**
     * Database Column Remarks:伝票行番号明細
     *
     */
    private String edano;
    /**
     * Database Column Remarks:商品名称明細
     *
     */
    private String shnnm;
    /**
     * Database Column Remarks:計上区分明細
     *
     */
    private String keijokbn;
    /**
     * Database Column Remarks:明細備考明細
     *
     */
    private String meisaibiko;
    /**
     * Database Column Remarks:数量明細
     *
     */
    private String suryo;
    /**
     * Database Column Remarks:単位明細
     *
     */
    private String uritaninm;
    /**
     * Database Column Remarks:単価明細
     *
     */
    private String unt;
    /**
     * Database Column Remarks:単価消費税明細
     *
     */
    private String untshohitaxkin;
    /**
     * Database Column Remarks:金額明細
     *
     */
    private String kin;
    /**
     * Database Column Remarks:内消費税明細
     *
     */
    private String shohitaxkin;
    /**
     * Database Column Remarks:税区分
     *
     */
    private String taxkbn;
    /**
     * Database Column Remarks:構成品の親商品コード
     *
     */
    private String stshncd;
    /**
     * Database Column Remarks:構成品の親商品税区分
     *
     */
    private String sttaxkbn;
    /**
     * Database Column Remarks:売上伝票明細番号
     *
     */
    private String uridnpymeisaino;
    /**
     * Database Column Remarks:売上伝票明細枝番号
     *
     */
    private String uridnpymeisaiedano;
    /**
     * Database Column Remarks:入金種別
     *
     */
    private String nyknkind;
    /**
     * Database Column Remarks:消費税レコードフラグ
     *
     */
    private String taxrecflg;
    /**
     * Database Column Remarks:売上、入金のflag
     *
     */
    private String flag1;
    /**
     * Database Column Remarks:売上、入金のflag
     *
     */
    private String flag2;
    /**
     * Database Column Remarks:回収方法区分
     *
     */
    private String kaishuhohokbn;
    /**
     * Database Column Remarks:回収方法区分名
     *
     */
    private String kaishuhohokbnnm;
    /**
     * Database Column Remarks:支払予定日
     *
     */
    private String shrytidydmdd;
    /**
     * Database Column Remarks:請求先マスタ・金融機関名称カナ
     *
     */
    private String sksmkinyukknnmkana;
    /**
     * Database Column Remarks:請求先マスタ・金融機関支店名称カナ
     *
     */
    private String sksmkinyukknshitennmkana;
    /**
     * Database Column Remarks:請求先マスタ・金融機関口座種別
     *
     */
    private String sksmkinyukknkzkind;
    /**
     * Database Column Remarks:請求先マスタ・金融機関口座番号
     *
     */
    private String sksmkinyukknkzno;
    /**
     * Database Column Remarks:請求先マスタ・金融機関口座種別名
     *
     */
    private String sksmkinyukknkzkindnm;
    /**
     * Database Column Remarks:決済機関マスタ・決済機関名称
     *
     */
    private String sksmksikknnm;
    /**
     * Database Column Remarks:得意先マスタ・外部識別コード
     *
     */
    private String tokuisakigaibuskbtscd;
    /**
     * Database Column Remarks:請求先マスタ・外部連携コード
     *
     */
    private String gaiburenkeicd;

    /**
     * Database Column Remarks:得意先マスタ・料金プラン設定日
     *
     */
    private String rknplnsetteidd;
    /**
     * Database Column Remarks:得意先マスタ・料金プラン継続更新日
     *
     */
    private String rknplnkeizokukshndd;
    /**
     * Database Column Remarks:得意先マスタ・特別単価区分
     *
     */
    private String tokubetsuuntkbn;
    /**
     * Database Column Remarks:プランコード
     *
     */
    private String plncd;
    /**
     * Database Column Remarks:システム管理マスタ・料金プラン適用月数
     *
     */
    private String rknplnkikan;
    /**
     * Database Column Remarks:システム管理マスタ・特別単価区分名
     *
     */
    private String tokubetsuuntkbnnm;

    /**
     * toString
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", skysakicd=").append(skysakicd);
        sb.append(", skykbn=").append(skykbn);
        sb.append(", skyhohokbn=").append(skyhohokbn);
        sb.append(", meisaiprntkbn=").append(meisaiprntkbn);
        sb.append(", yubinno=").append(yubinno);
        sb.append(", adrsn1=").append(adrsn1);
        sb.append(", adrsn2=").append(adrsn2);
        sb.append(", adrsn3=").append(adrsn3);
        sb.append(", skysakinmn1=").append(skysakinmn1);
        sb.append(", skysakinmn2=").append(skysakinmn2);
        sb.append(", skysakikeishokbn=").append(skysakikeishokbn);
        sb.append(", skysakikeishokbnnm=").append(skysakikeishokbnnm);
        sb.append(", yubinnobakodo=").append(yubinnobakodo);
        sb.append(", taxkeisankbn=").append(taxkeisankbn);
        sb.append(", prntbiko=").append(prntbiko);
        sb.append(", dnpyoshirasenaiyon1=").append(dnpyoshirasenaiyon1);
        sb.append(", dnpyoshirasenaiyon2=").append(dnpyoshirasenaiyon2);
        sb.append(", dnpyoshirasenaiyon3=").append(dnpyoshirasenaiyon3);
        sb.append(", dnpyoshirasenaiyon4=").append(dnpyoshirasenaiyon4);
        sb.append(", dnpyoshirasenaiyon5=").append(dnpyoshirasenaiyon5);
        sb.append(", dnpyoshirasenaiyon6=").append(dnpyoshirasenaiyon6);
        sb.append(", dnpyoshirase2=").append(dnpyoshirase2);
        sb.append(", znkkurikoshikin=").append(znkkurikoshikin);
        sb.append(", konkaitaxnukiurikin=").append(konkaitaxnukiurikin);
        sb.append(", konkaishohitaxkin=").append(konkaishohitaxkin);
        sb.append(", konkainyknkin=").append(konkainyknkin);
        sb.append(", kurikoshikin=").append(kurikoshikin);
        sb.append(", tokuisakicd=").append(tokuisakicd);
        sb.append(", tokuisakinmn1=").append(tokuisakinmn1);
        sb.append(", kinyukknnm=").append(kinyukknnm);
        sb.append(", kinyukknshitennm=").append(kinyukknshitennm);
        sb.append(", kinyukknkzkind=").append(kinyukknkzkind);
        sb.append(", kinyukknkzno=").append(kinyukknkzno);
        sb.append(", kinyukknn2nm=").append(kinyukknn2nm);
        sb.append(", kinyukknn2shitennm=").append(kinyukknn2shitennm);
        sb.append(", kinyukknn2kzkind=").append(kinyukknn2kzkind);
        sb.append(", kinyukknn2kzno=").append(kinyukknn2kzno);
        sb.append(", uridd=").append(uridd);
        sb.append(", nyuryokumeisaiflg=").append(nyuryokumeisaiflg);
        sb.append(", tanpinmeisaiflg=").append(tanpinmeisaiflg);
        sb.append(", uridnpyno=").append(uridnpyno);
        sb.append(", edano=").append(edano);
        sb.append(", shnnm=").append(shnnm);
        sb.append(", keijokbn=").append(keijokbn);
        sb.append(", meisaibiko=").append(meisaibiko);
        sb.append(", suryo=").append(suryo);
        sb.append(", uritaninm=").append(uritaninm);
        sb.append(", unt=").append(unt);
        sb.append(", untshohitaxkin=").append(untshohitaxkin);
        sb.append(", kin=").append(kin);
        sb.append(", shohitaxkin=").append(shohitaxkin);
        sb.append(", taxkbn=").append(taxkbn);
        sb.append(", stshncd=").append(stshncd);
        sb.append(", sttaxkbn=").append(sttaxkbn);
        sb.append(", uridnpymeisaino=").append(uridnpymeisaino);
        sb.append(", uridnpymeisaiedano=").append(uridnpymeisaiedano);
        sb.append(", nyknkind=").append(nyknkind);
        sb.append(", taxrecflg=").append(taxrecflg);
        sb.append(", flag1=").append(flag1);
        sb.append(", flag2=").append(flag2);
        sb.append(", kaishuhohokbn=").append(kaishuhohokbn);
        sb.append(", kaishuhohokbnnm=").append(kaishuhohokbnnm);
        sb.append(", shrytidydmdd=").append(shrytidydmdd);
        sb.append(", sksmkinyukknnmkana=").append(sksmkinyukknnmkana);
        sb.append(", sksmkinyukknshitennmkana=").append(sksmkinyukknshitennmkana);
        sb.append(", sksmkinyukknkzkind=").append(sksmkinyukknkzkind);
        sb.append(", sksmkinyukknkzno=").append(sksmkinyukknkzno);
        sb.append(", sksmkinyukknkzkindnm=").append(sksmkinyukknkzkindnm);
        sb.append(", sksmksikknnm=").append(sksmksikknnm);
        sb.append(", tokuisakigaibuskbtscd=").append(tokuisakigaibuskbtscd);
        sb.append(", gaiburenkeicd=").append(gaiburenkeicd);
        sb.append(", rknplnsetteidd=").append(rknplnsetteidd);
        sb.append(", rknplnkeizokukshndd=").append(rknplnkeizokukshndd);
        sb.append(", tokubetsuuntkbn=").append(tokubetsuuntkbn);
        sb.append(", plncd=").append(plncd);
        sb.append(", rknplnkikan=").append(rknplnkikan);
        sb.append(", tokubetsuuntkbnnm=").append(tokubetsuuntkbnnm);
        sb.append("]");
        return sb.toString();
    }
}
