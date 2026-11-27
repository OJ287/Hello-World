package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M044TEntity;
import lombok.Data;

/**
 * Database Table Remarks: お気に入り商品マスタ
 *
 */
@Data
public class S02M044TC0805W01Entity extends S02M044TEntity implements Serializable {

    /** 種別：定期受注 */
    public static final String TYPE_NORMAL = "0";
    /** 種別：特定定期受注 */
    public static final String TYPE_MULTI = "1";

    private String type;
    private String tokuisakicd;
    private String acptdd;
    private String dlvryytidd;
    private String shnapkihoncd;
    private String shncd;
    private String stkbn;
    private String lctgrycd;
    private String mctgrycd;
    private String sctgrycd;
    private String shnnmryakusho;
    private String pricekbn;
    private String hyojunpricekirikaedd;
    private String genzhyojunprice;
    private String shinhyojunprice;
    private String genzmochikaeriprice;
    private String shinmochikaeriprice;
    private String opnpricekirikaedd;
    private String genzopnprice;
    private String shinopnprice;
    private String genzopnmochikaeriprice;
    private String shinopnmochikaeriprice;
    private String hansokunm;
    private String hansokukbn;
    private String hansokustrtdd;
    private String hansokushrydd;
    private String hansokudlvryprice;
    private String hansokumochikaeriprice;
    private String hansokuyusenkbn;
    private String zaikount;
    private String iro;
    private String sz;
    private String uritrtskitani;
    private String uriirisu;
    private String uritani;
    private String taxsetteikbn;
    private String taxkbn;
    private String zaikoaramusuryo;
    private String trtskikbn;
    private String tokubetsuuntkbn;
    private String tekiyostrtdd;
    private String tekiyoshrydd;
    private String dlvryprice;
    private String mochikaeriprice;
    private String shnnm;
    private String hyojunprice;
    private String set_tekiyostrtdd;
    private String set_tekiyoshrydd;
    private String set_dlvryprice;
    private String set_mochikaeriprice;
    private String rknkindtokubetsuuntflg;
    private String dlvryshuu;
    private String shiteiyobi;
    private String shiteiyouso;
    private String shiteikbn;
    private String katasiki;
    private String hansokutaiou;
    private String acptsuryo;
    private String meisaibiko;
    private String hansokuprice;
    private String shoppricekirikaedd;
    private String genzshopprice;
    private String shinshopprice;
    private String genzshopmochikaeriprice;
    private String shinshopmochikaeriprice;
    private String ttpricestrtdd;
    private String ttpriceshrydd;
    private String ttprice;
    private String ttmochikaeriprice;
    private String setTekiyostrtdd;
    private String setTekiyoshrydd;
    private String setDlvryprice;
    private String setMochikaeriprice;

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
        sb.append(", type=").append(type);
        sb.append(", tokuisakicd=").append(tokuisakicd);
        sb.append(", acptdd=").append(acptdd);
        sb.append(", dlvryytidd=").append(dlvryytidd);
        sb.append(", shnapkihoncd=").append(shnapkihoncd);
        sb.append(", shncd=").append(shncd);
        sb.append(", stkbn=").append(stkbn);
        sb.append(", lctgrycd=").append(lctgrycd);
        sb.append(", mctgrycd=").append(mctgrycd);
        sb.append(", sctgrycd=").append(sctgrycd);
        sb.append(", shnnmryakusho=").append(shnnmryakusho);
        sb.append(", pricekbn=").append(pricekbn);
        sb.append(", hyojunpricekirikaedd=").append(hyojunpricekirikaedd);
        sb.append(", genzhyojunprice=").append(genzhyojunprice);
        sb.append(", shinhyojunprice=").append(shinhyojunprice);
        sb.append(", genzmochikaeriprice=").append(genzmochikaeriprice);
        sb.append(", shinmochikaeriprice=").append(shinmochikaeriprice);
        sb.append(", opnpricekirikaedd=").append(opnpricekirikaedd);
        sb.append(", genzopnprice=").append(genzopnprice);
        sb.append(", shinopnprice=").append(shinopnprice);
        sb.append(", genzopnmochikaeriprice=").append(genzopnmochikaeriprice);
        sb.append(", shinopnmochikaeriprice=").append(shinopnmochikaeriprice);
        sb.append(", hansokunm=").append(hansokunm);
        sb.append(", hansokukbn=").append(hansokukbn);
        sb.append(", hansokustrtdd=").append(hansokustrtdd);
        sb.append(", hansokushrydd=").append(hansokushrydd);
        sb.append(", hansokudlvryprice=").append(hansokudlvryprice);
        sb.append(", hansokumochikaeriprice=").append(hansokumochikaeriprice);
        sb.append(", hansokuyusenkbn=").append(hansokuyusenkbn);
        sb.append(", zaikount=").append(zaikount);
        sb.append(", iro=").append(iro);
        sb.append(", sznm sz=").append(sz);
        sb.append(", uritrtskitani=").append(uritrtskitani);
        sb.append(", uriirisu=").append(uriirisu);
        sb.append(", uritaninm uritani=").append(uritani);
        sb.append(", taxsetteikbn=").append(taxsetteikbn);
        sb.append(", taxkbn=").append(taxkbn);
        sb.append(", zaikoaramusuryo=").append(zaikoaramusuryo);
        sb.append(", trtskikbn=").append(trtskikbn);
        sb.append(", tokubetsuuntkbn=").append(tokubetsuuntkbn);
        sb.append(", tekiyostrtdd=").append(tekiyostrtdd);
        sb.append(", tekiyoshrydd=").append(tekiyoshrydd);
        sb.append(", dlvryprice=").append(dlvryprice);
        sb.append(", mochikaeriprice=").append(mochikaeriprice);
        sb.append(", shnnm=").append(shnnm);
        sb.append(", hyojunprice=").append(hyojunprice);
        sb.append(", set_tekiyostrtdd=").append(set_tekiyostrtdd);
        sb.append(", set_tekiyoshrydd=").append(set_tekiyoshrydd);
        sb.append(", set_dlvryprice=").append(set_dlvryprice);
        sb.append(", set_mochikaeriprice=").append(set_mochikaeriprice);
        sb.append(", rknkindtokubetsuuntflg=").append(rknkindtokubetsuuntflg);
        sb.append(", dlvryshuu=").append(dlvryshuu);
        sb.append(", shiteiyobi=").append(shiteiyobi);
        sb.append(", shiteiyouso=").append(shiteiyouso);
        sb.append(", shiteikbn=").append(shiteikbn);
        sb.append(", katasiki=").append(katasiki);
        sb.append(", hansokutaiou=").append(hansokutaiou);
        sb.append(", acptsuryo=").append(acptsuryo);
        sb.append(", meisaibiko=").append(meisaibiko);
        sb.append(", hansokuprice=").append(hansokuprice);
        sb.append(", shoppricekirikaedd=").append(shoppricekirikaedd);
        sb.append(", genzshopprice=").append(genzshopprice);
        sb.append(", shinshopprice=").append(shinshopprice);
        sb.append(", genzshopmochikaeriprice=").append(genzshopmochikaeriprice);
        sb.append(", shinshopmochikaeriprice=").append(shinshopmochikaeriprice);
        sb.append(", ttpricestrtdd=").append(ttpricestrtdd);
        sb.append(", ttpriceshrydd=").append(ttpriceshrydd);
        sb.append(", ttprice=").append(ttprice);
        sb.append(", ttmochikaeriprice=").append(ttmochikaeriprice);
        sb.append(", setTekiyostrtdd=").append(setTekiyostrtdd);
        sb.append(", setTekiyoshrydd=").append(setTekiyoshrydd);
        sb.append(", setDlvryprice=").append(setDlvryprice);
        sb.append(", setMochikaeriprice=").append(setMochikaeriprice);

        sb.append("]");
        return sb.toString();

    }
}
