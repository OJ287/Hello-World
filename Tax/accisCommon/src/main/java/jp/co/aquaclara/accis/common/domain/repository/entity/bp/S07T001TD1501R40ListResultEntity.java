
package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import java.math.BigDecimal;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntityKey;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * お支払明細書印刷データ取得結果Entity.
 * 
 * @author accisdev047
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class S07T001TD1501R40ListResultEntity extends S07T001TEntityKey implements Serializable {
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
     * Database Column Remarks:郵便番号
     *
     */
    private String yubinno;
    /**
     * Database Column Remarks:請求先住所1
     *
     */
    private String adrsn1;
    /**
     * Database Column Remarks:請求先住所2
     *
     */
    private String adrsn2;
    /**
     * Database Column Remarks:請求先住所3
     *
     */
    private String adrsn3;
    /**
     * Database Column Remarks:請求先名称1
     *
     */
    private String skysakinmn1;
    /**
     * Database Column Remarks:請求先名称2
     *
     */
    private String skysakinmn2;
    /**
     * Database Column Remarks:敬称
     *
     */
    private String skysakikeishonm;
    /**
     * Database Column Remarks:差出人イメージ
     *
     */
    private String sshdshhitoimage;
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
     * Database Column Remarks:前回繰越金
     *
     */
    private String znkkurikoshikin;
    /**
     * Database Column Remarks:今回入金金額
     *
     */
    private String konkainyknkin;
    /**
     * Database Column Remarks:税区分
     *
     */
    private String taxkeisankbn;
    /**
     * Database Column Remarks:請求方法区分
     *
     */
    private String skyhohokbn;
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
     * Database Column Remarks:繰越金額
     *
     */
    private String kurikoshikin;
    /**
     * Database Column Remarks:前回繰越金（合計）
     *
     */
    private String znkkurikoshikinsum;
    /**
     * Database Column Remarks:今回入金金額（合計）
     *
     */
    private String konkainyknkinsum;
    /**
     * Database Column Remarks:今回税抜売上金額（合計）
     *
     */
    private String konkaitaxnukiurikinsum;
    /**
     * Database Column Remarks:今回消費税金額（合計）
     *
     */
    private String konkaishohitaxkinsum;
    /**
     * Database Column Remarks:繰越金額（合計）
     *
     */
    private String kurikoshikinsum;
    /**
     * Database Column Remarks:決済金額
     *
     */
    private String ksikin;
    /**
     * Database Column Remarks:郵便番号バーコード
     *
     */
    private String yubinnobakodo;

    /**
     * 振替予定金額を返却する
     * 
     * @return 振替予定金額
     */
    public String getScheduledTransferAmount() {
        if (this.getKsikin() == null) {
            if (CommonConstants.SKYHOHOKBN_ZANDAKA.equals(this.getSkyhohokbn())) {
                return this.getKurikoshikinsum();
            }
            if (CommonConstants.SKYHOHOKBN_TOUGETSU.equals(this.getSkyhohokbn())) {
                return getKonkaitaxkomiurikinSum();
            }
            return "";
        } else {
            return this.getKsikin();
        }
    }

    /**
     * 今回消費税込売上額合計を取得する
     * 
     * @return 今回消費税金額(合計)と今回消費税抜売上額（合計）を足した値
     */
    private String getKonkaitaxkomiurikinSum() {
        BigDecimal taxNukiKinSum =
            new BigDecimal(ItemUtil.nullToValue(this.getKonkaitaxnukiurikinsum(), "0"));
        BigDecimal taxKinSum =
            new BigDecimal(ItemUtil.nullToValue(this.getKonkaishohitaxkinsum(), "0"));
        BigDecimal total = taxKinSum.add(taxNukiKinSum);
        return total.toString();
    }

    /**
     * 今回消費税込売上額を取得する
     * 
     * @return 今回消費税金額と今回消費税抜売上額を足した値
     */
    private String getKonkaitaxkomiurikin() {
        BigDecimal taxNukiKin =
            new BigDecimal(ItemUtil.nullToValue(this.getKonkaitaxnukiurikin(), "0"));
        BigDecimal taxKin = new BigDecimal(ItemUtil.nullToValue(this.getKonkaishohitaxkin(), "0"));
        BigDecimal total = taxKin.add(taxNukiKin);
        return total.toString();
    }

    /**
     * 今回ご請求額明細を返却
     * 
     * @return 残高請求：繰越金額 残高請求以外：今回税込売上金額
     */
    public String getKonkaiSkykinMeisai() {
        if (CommonConstants.SKYHOHOKBN_ZANDAKA.equals(this.getSkyhohokbn())) {
            return this.getKurikoshikin();
        }
        return getKonkaitaxkomiurikin();
    }

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
        sb.append(", yubinno=").append(yubinno);
        sb.append(", adrsn1=").append(adrsn1);
        sb.append(", adrsn2=").append(adrsn2);
        sb.append(", adrsn3=").append(adrsn3);
        sb.append(", skysakinmn1=").append(skysakinmn1);
        sb.append(", skysakinmn2=").append(skysakinmn2);
        sb.append(", skysakikeishonm=").append(skysakikeishonm);
        sb.append(", sshdshhitoimage=").append(sshdshhitoimage);
        sb.append(", tokuisakicd=").append(tokuisakicd);
        sb.append(", tokuisakinmn1=").append(tokuisakinmn1);
        sb.append(", znkkurikoshikin=").append(znkkurikoshikin);
        sb.append(", konkainyknkin=").append(konkainyknkin);
        sb.append(", taxkeisankbn=").append(taxkeisankbn);
        sb.append(", skyhohokbn=").append(skyhohokbn);
        sb.append(", konkaitaxnukiurikin=").append(konkaitaxnukiurikin);
        sb.append(", konkaishohitaxkin=").append(konkaishohitaxkin);
        sb.append(", kurikoshikin=").append(kurikoshikin);
        sb.append(", znkkurikoshikinsum=").append(znkkurikoshikinsum);
        sb.append(", konkainyknkinsum=").append(konkainyknkinsum);
        sb.append(", konkaitaxnukiurikinsum=").append(konkaitaxnukiurikinsum);
        sb.append(", konkaishohitaxkinsum=").append(konkaishohitaxkinsum);
        sb.append(", kurikoshikinsum=").append(kurikoshikinsum);
        sb.append(", ksikin=").append(ksikin);
        sb.append(", yubinnobakodo=").append(yubinnobakodo);
        sb.append("]");
        return sb.toString();
    }
}
