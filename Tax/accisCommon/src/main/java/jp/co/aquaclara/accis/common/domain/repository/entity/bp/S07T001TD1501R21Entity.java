package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntityKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 請求一覧表Entity
 * 
 * @author accisdev047
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class S07T001TD1501R21Entity extends S07T001TEntityKey implements Serializable {

    /**
     * Database Column Remarks: 請求先コード
     *
     */
    private String skysakicd;
    /**
     * Database Column Remarks: 締年月
     *
     */
    private String shimedydm;
    /**
     * Database Column Remarks: 締日
     *
     */
    private String shimedd;
    /**
     * Database Column Remarks: 請求締年月日
     *
     */
    private String skydydmdd;
    /**
     * Database Column Remarks: 有効区分
     *
     */
    private String yukokbn;

    /** 回収担当者名 */
    private String kaishutnshanm;

    /** 消費税項目名 */
    private String syouhikin;

    /** 回収方法 */
    private String kaishuhoho;

    /** 決済機関コード */
    private String ksikkncd;

    /** 滞留区分 */
    private String tairyukbn;

    /** 滞留区分名称 */
    private String tairyukbnnm;

    /** 滞留金額 */
    private String tairyukin;

    /** 請求区分（親子） */
    private String skykbn1;

    /** 得意先状態 */
    private String tokuisakijotai;

    /** 得意先コード */
    private String tokuisakicd;

    /** 得意先名称 */
    private String tokuisakinmn1;

    /** 前回繰越金額 */
    private String znkkurikoshikin;

    /** 今回入金金額 */
    private String konkainyknkin;

    /** 当月売上額 */
    private String kin;

    /** 今回消費税金額 */
    private String konkaishohitaxkin;

    /** 繰越金額 */
    private String kurikoshikin;

    /** 現在残高 */
    private String genzaizd;

    /** 請求区分 */
    private String skykbn;

    /** ログイン担当者AP基本コード */
    private String loginUserapkihoncd;

    /** ログイン担当者ショップコード */
    private String loginUsershopcd;

    /** ログイン担当者コード */
    private String loginUsertnshacd;

    /** ログイン担当者ショップ名称 */
    private String loginUsershopnm;

    /** ログイン担当者名称 */
    private String loginUsertnshamei;

    /** ログイン担当者事業者コード */
    private String loginUserjigyoshocd;

    /** ログイン担当者事業所名称 */
    private String loginUserjigyoshonm;

    /** 運用日付 */
    private String unyoymd;

    /** 事業所コード */
    private String jigyoshocd;

    /** 事業所名称 */
    private String jigyoshonm;

    /**
     * Database Column Remarks: カウント結果
     *
     */
    private int cnt;

    /**
     * Database Column Remarks: ショップ名称
     *
     */
    private String shopnm;

    /**
     * Database Column Remarks: 営業担当者名称
     *
     */
    private String tnshamei1;

    /**
     * Database Column Remarks: 回収担当者名称
     *
     */
    private String tnshamei2;

    /**
     * Database Column Remarks: 請求先名称
     *
     */
    private String skysakinmn1;

    /**
     * Database Column Remarks: 回収方法区分
     *
     */
    private String kaishuhohokbn;

    private static final long serialVersionUID = 1L;

    /**
     * 文字列変換
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", skysakicd=").append(skysakicd);
        sb.append(", shimedydm=").append(shimedydm);
        sb.append(", shimedd=").append(shimedd);
        sb.append(", skydydmdd=").append(skydydmdd);
        sb.append(", yukokbn=").append(yukokbn);
        sb.append(", shopnm=").append(shopnm);
        sb.append(", tnshamei1=").append(tnshamei1);
        sb.append(", kaishutnshanm=").append(kaishutnshanm);
        sb.append(", syouhikin=").append(syouhikin);
        sb.append(", skysakinmn1=").append(skysakinmn1);
        sb.append(", kaishuhoho=").append(kaishuhoho);
        sb.append(", ksikkncd=").append(ksikkncd);
        sb.append(", tairyukbn=").append(tairyukbn);
        sb.append(", tairyukbnnm=").append(tairyukbnnm);
        sb.append(", tairyukin=").append(tairyukin);
        sb.append(", skykbn1=").append(skykbn1);
        sb.append(", tokuisakijotai=").append(tokuisakijotai);
        sb.append(", tokuisakicd=").append(tokuisakicd);
        sb.append(", tokuisakinm1=").append(tokuisakinmn1);
        sb.append(", znkkurikoshikin=").append(znkkurikoshikin);
        sb.append(", konkainyknkin=").append(konkainyknkin);
        sb.append(", kin=").append(kin);
        sb.append(", konkaishohitaxkin=").append(konkaishohitaxkin);
        sb.append(", kurikoshikin=").append(kurikoshikin);
        sb.append(", genzaizd=").append(genzaizd);
        sb.append(", skykbn=").append(skykbn);
        sb.append(", loginUserapkihoncd=").append(loginUserapkihoncd);
        sb.append(", loginUsershopcd=").append(loginUsershopcd);
        sb.append(", loginUsertnshacd=").append(loginUsertnshacd);
        sb.append(", loginUsershopnm=").append(loginUsershopnm);
        sb.append(", loginUsertnshamei=").append(loginUsertnshamei);
        sb.append(", loginUserjigyoshocd=").append(loginUserjigyoshocd);
        sb.append(", loginUserjigyoshonm=").append(loginUserjigyoshonm);
        sb.append(", unyoymd=").append(unyoymd);
        sb.append(", jigyoshocd=").append(jigyoshocd);
        sb.append(", jigyoshonm=").append(jigyoshonm);
        sb.append(", cnt=").append(cnt);
        sb.append(", shopnm=").append(shopnm);
        sb.append(", tnshamei2=").append(tnshamei2);
        sb.append(", kaishuhohokbn=").append(kaishuhohokbn);
        sb.append("]");
        return sb.toString();
    }
}
