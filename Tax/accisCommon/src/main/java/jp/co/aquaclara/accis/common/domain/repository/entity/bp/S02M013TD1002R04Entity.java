package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntityKey;
import lombok.Data;

/**
 * Database Table Remarks: 得意先マスタ
 *
 * This class corresponds to the database table S02M013T
 */
@Data
public class S02M013TD1002R04Entity extends S02M013TEntityKey implements Serializable {

    /**
     * Database Column Remarks: 得意先名称１
     *
     */
    private String tokuisakinmn1;

    /**
     * Database Column Remarks: 営業担当者コード
     *
     */
    private String eigyotnshacd;

    /**
     * Database Column Remarks: 担当者名
     *
     */
    private String tnshamei;

    /**
     * Database Column Remarks: 状態ステータス
     *
     */
    private String jotaists;

    /**
     * Database Column Remarks: 得意先区分
     *
     */
    private String tokuisakikbn;

    /**
     * Database Column Remarks: 受託配達顧客区分
     *
     */
    private String jutakudlvrycstkbn;

    /**
     * Database Column Remarks: 請求先コード
     *
     */
    private String skysakicd;

    /**
     * Database Column Remarks: 本部エージェントコード
     *
     */
    private String honbuejntcd;

    /**
     * Database Column Remarks: 獲得エージェントコード
     *
     */
    private String kakutokuejntcd;

    /**
     * Database Column Remarks: 得意先状態名称
     *
     */
    private String tokuisakijotainm;

    /**
     * Database Column Remarks: 状態ステータス名称
     *
     */
    private String jotaistsnm;

    /**
     * Database Column Remarks: 得意先状態
     *
     */
    private String tokuisakijotai;

    /**
     * Database Column Remarks: 状態名称
     *
     */
    private String jotainmn;

    /**
     * Database Column Remarks: 利用サーバー区分
     *
     */
    private String riyowsvkbn;

    /**
     * Database Column Remarks: 利用サーバー区分名称
     *
     */
    private String riyowsvkbnnm;

    /**
     * Database Column Remarks: 事業所コード
     *
     */
    private String jigyoshocd;

    /**
     * Database Column Remarks: 事業所名称
     *
     */
    private String jigyoshonm;

    /**
     * Database Column Remarks: ショップ名称
     *
     */
    private String shopnm;

    /**
     * Database Column Remarks: AP基本コード
     *
     */
    private String apkihoncdur;

    /**
     * Database Column Remarks: ショップコード
     *
     */
    private String shopcdur;

    /**
     * Database Column Remarks: 得意先コード
     *
     */
    private String tokuisakicdur;

    /**
     * Database Column Remarks: レギュラー有料
     *
     */
    private String rgrayuryo;

    /**
     * Database Column Remarks: レギュラー無料
     *
     */
    private String rgramuryo;

    /**
     * Database Column Remarks: スリム有料
     *
     */
    private String srmyuryo;

    /**
     * Database Column Remarks: スリム無料
     *
     */
    private String srmmuryo;

    /**
     * Database Column Remarks: その他有料
     *
     */
    private String sonotayuryo;

    /**
     * Database Column Remarks: その他無料
     *
     */
    private String sonotamuryo;

    /**
     * Database Column Remarks: 製品水売上
     *
     */
    private String seihinmizuuri;

    /**
     * Database Column Remarks: レンタル料金
     *
     */
    private String rtlrkn;

    /**
     * Database Column Remarks: その他売上金
     *
     */
    private String sonotaurikin;

    /**
     * Database Column Remarks: 今回消費税金額
     *
     */
    private String konkaishohitaxkin;

    /**
     * Database Column Remarks: 今回収納代行売上金額
     *
     */
    private String konkaishunodaikourikin;

    /**
     * Database Column Remarks: 今回収納代行消費税金額
     *
     */
    private String konkaishunodaikoshohitaxkin;

    /**
     * Database Column Remarks: ８％税抜売上金額
     *
     */
    private String n8pertaxnukiurikin;

    /**
     * Database Column Remarks: ８％消費税金額
     *
     */
    private String n8pershohitaxkin;

    /**
     * Database Column Remarks: １０％税抜売上金額
     *
     */
    private String n10pertaxnukiurikin;

    /**
     * Database Column Remarks: １０％消費税金額
     *
     */
    private String n10pershohitaxkin;

    /**
     * Database Column Remarks: AP基本コード
     *
     */
    private String apkihoncdnk;

    /**
     * Database Column Remarks: ショップコード
     *
     */
    private String shopcdnk;

    /**
     * Database Column Remarks: 得意先コード
     *
     */
    private String tokuisakicdnk;

    /**
     * Database Column Remarks: 今回入金金額
     *
     */
    private String konkainyknkin;

    /**
     * Database Column Remarks: AP基本コード
     *
     */
    private String apkihoncdzd;

    /**
     * Database Column Remarks: ショップコード
     *
     */
    private String shopcdzd;

    /**
     * Database Column Remarks: 得意先コード
     *
     */
    private String tokuisakicdzd;

    /**
     * Database Column Remarks: 前回繰越金額
     *
     */
    private String znkkurikoshikin;

    /**
     * Database Column Remarks: 当月売上金額
     *
     */
    private String totsukiurikeimei;

    /**
     * Database Column Remarks: 繰越金額
     *
     */
    private String kurikoshikin;

    /**
     * Database Column Remarks: 得意先区分名称
     *
     */
    private String tokuisakikbnnm;

    /**
     * Database Column Remarks: 受託配達顧客区分名称
     *
     */
    private String jutakudlvrycstkbnnm;

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
        sb.append(", tokuisakinmn1=").append(tokuisakinmn1);
        sb.append(", eigyotnshacd=").append(eigyotnshacd);
        sb.append(", tnshamei=").append(tnshamei);
        sb.append(", jotaists=").append(jotaists);
        sb.append(", tokuisakikbn=").append(tokuisakikbn);
        sb.append(", jutakudlvrycstkbn=").append(jutakudlvrycstkbn);
        sb.append(", skysakicd=").append(skysakicd);
        sb.append(", honbuejntcd=").append(honbuejntcd);
        sb.append(", kakutokuejntcd=").append(kakutokuejntcd);
        sb.append(", tokuisakijotainm=").append(tokuisakijotainm);
        sb.append(", jotaistsnm=").append(jotaistsnm);
        sb.append(", tokuisakijotai=").append(tokuisakijotai);
        sb.append(", jotainmn=").append(jotainmn);
        sb.append(", riyowsvkbn=").append(riyowsvkbn);
        sb.append(", riyowsvkbnnm=").append(riyowsvkbnnm);
        sb.append(", jigyoshocd=").append(jigyoshocd);
        sb.append(", jigyoshonm=").append(jigyoshonm);
        sb.append(", shopnm=").append(shopnm);
        sb.append(", apkihoncdur=").append(apkihoncdur);
        sb.append(", shopcdur=").append(shopcdur);
        sb.append(", tokuisakicdur=").append(tokuisakicdur);
        sb.append(", rgrayuryo=").append(rgrayuryo);
        sb.append(", rgramuryo=").append(rgramuryo);
        sb.append(", srmyuryo=").append(srmyuryo);
        sb.append(", srmmuryo=").append(srmmuryo);
        sb.append(", sonotayuryo=").append(sonotayuryo);
        sb.append(", sonotamuryo=").append(sonotamuryo);
        sb.append(", seihinmizuuri=").append(seihinmizuuri);
        sb.append(", rtlrkn=").append(rtlrkn);
        sb.append(", sonotaurikin=").append(sonotaurikin);
        sb.append(", konkaishohitaxkin=").append(konkaishohitaxkin);
        sb.append(", konkaishunodaikourikin=").append(konkaishunodaikourikin);
        sb.append(", konkaishunodaikoshohitaxkin=").append(konkaishunodaikoshohitaxkin);
        sb.append(", n8pertaxnukiurikin=").append(n8pertaxnukiurikin);
        sb.append(", n8pershohitaxkin=").append(n8pershohitaxkin);
        sb.append(", n10pertaxnukiurikin=").append(n10pertaxnukiurikin);
        sb.append(", n10pershohitaxkin=").append(n10pershohitaxkin);
        sb.append(", apkihoncdnk=").append(apkihoncdnk);
        sb.append(", shopcdnk=").append(shopcdnk);
        sb.append(", tokuisakicdnk=").append(tokuisakicdnk);
        sb.append(", konkainyknkin=").append(konkainyknkin);
        sb.append(", apkihoncdzd=").append(apkihoncdzd);
        sb.append(", shopcdzd=").append(shopcdzd);
        sb.append(", tokuisakicdzd=").append(tokuisakicdzd);
        sb.append(", znkkurikoshikin=").append(znkkurikoshikin);
        sb.append(", totsukiurikeimei=").append(totsukiurikeimei);
        sb.append(", kurikoshikin=").append(kurikoshikin);
        sb.append(", tokuisakikbnnm=").append(tokuisakikbnnm);
        sb.append(", jutakudlvrycstkbnnm=").append(jutakudlvrycstkbnnm);
        sb.append("]");
        return sb.toString();
    }
}
