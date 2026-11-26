package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Database Table Remarks: 請求残高テーブル
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class S07T001TD1603W01Entity extends S07T001TEntity implements Serializable {
    /**
     * Database Column Remarks: 請求先コード
     *
     */
    private String skysakicd;

    /**
     * Database Column Remarks: 決済機関コード
     *
     */
    private String ksikkncd;

    /**
     * Database Column Remarks: 金融機関コード
     *
     */
    private String kinyukkncd;

    /**
     * Database Column Remarks: 金融機関支店コード
     *
     */
    private String kinyukknshitencd;

    /**
     * Database Column Remarks: 金融機関支店名称カナ
     *
     */
    private String kinyukknshitennmkana;

    /**
     * Database Column Remarks: 金融機関名称カナ
     *
     */
    private String kinyukknnmkana;

    /**
     * Database Column Remarks: 金融機関口座種別
     *
     */
    private String kinyukknkzkind;

    /**
     * Database Column Remarks: 金融機関口座番号
     *
     */
    private String kinyukknkzno;

    /**
     * Database Column Remarks: 金融機関口座名義カナ
     *
     */
    private String kinyukknkzmeigikana;

    /**
     * Database Column Remarks: 全銀顧客コード
     *
     */
    private String zengincstcd;

    /**
     * Database Column Remarks: 回収方法区分
     *
     */
    private String kaishuhohokbn;

    /**
     * Database Column Remarks: クレジット会員ID
     *
     */
    private String crdtcrdno;

    /**
     * Database Column Remarks: クレジット会員備考
     *
     */
    private String crdtcrdmeigi;

    /**
     * Database Column Remarks: 登録年月
     *
     */
    private String crdtcrdyukokgn;

    /**
     * Database Column Remarks: 締年月
     *
     */
    private String shimedydm;

    /**
     * Database Column Remarks: 支払サイト
     *
     */
    private String shrsaito;

    /**
     * Database Column Remarks: 支払日
     *
     */
    private String shrdd;

    /**
     * Database Column Remarks: 請求方法区分
     *
     */
    private String skyhohokbn;

    /**
     * Database Column Remarks: 繰越金額
     *
     */
    private Long kurikoshikin_sum;

    private String skyshimedydm;

    /**
     * Database Column Remarks: 今回税抜売上金額
     *
     */
    private Long konkaitaxnukiurikin_sum;

    /**
     * Database Column Remarks: 今回消費税金額
     *
     */
    private Long konkaishohitaxkin_sum;

    /**
     * Database Column Remarks: 「入金金額＋値引金額＋手数料」
     *
     */
    private Long kin3mei;

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
        sb.append(", skysakicd=").append(skysakicd);
        sb.append(", ksikkncd=").append(ksikkncd);
        sb.append(", kinyukkncd=").append(kinyukkncd);
        sb.append(", kinyukknshitencd=").append(kinyukknshitencd);
        sb.append(", kinyukknshitennmkana=").append(kinyukknshitennmkana);
        sb.append(", kinyukknnmkana=").append(kinyukknnmkana);
        sb.append(", kinyukknkzkind=").append(kinyukknkzkind);
        sb.append(", kinyukknkzno=").append(kinyukknkzno);
        sb.append(", kinyukknkzmeigikana=").append(kinyukknkzmeigikana);
        sb.append(", zengincstcd=").append(zengincstcd);
        sb.append(", kaishuhohokbn=").append(kaishuhohokbn);
        sb.append(", crdtcrdno=").append(crdtcrdno);
        sb.append(", crdtcrdmeigi=").append(crdtcrdmeigi);
        sb.append(", crdtcrdyukokgn=").append(crdtcrdyukokgn);
        sb.append(", shimedydm=").append(shimedydm);
        sb.append(", shrsaito=").append(shrsaito);
        sb.append(", shrdd=").append(shrdd);
        sb.append(", skyhohokbn=").append(skyhohokbn);
        sb.append(", kurikoshikin_sum=").append(kurikoshikin_sum);
        sb.append(", skyshimedydm=").append(skyshimedydm);
        sb.append(", konkaitaxnukiurikin_sum=").append(konkaitaxnukiurikin_sum);
        sb.append(", konkaishohitaxkin_sum=").append(konkaishohitaxkin_sum);
        sb.append(", kin3mei=").append(kin3mei);
        sb.append("]");

        return sb.toString();
    }
}
