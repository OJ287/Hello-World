package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntityKey;
import lombok.Data;

/**
 * Database Table Remarks: 請求残高テーブル
 *
 * This class corresponds to the database table S07T001T
 */
@Data
public class S07T001TC0501W02Entity extends S07T001TEntityKey implements Serializable {
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
     * Database Column Remarks: 前回繰越金額
     *
     */
    private Long znkkurikoshikin;

    /**
     * Database Column Remarks: 今回入金金額
     *
     */
    private Long konkainyknkin;

    /**
     * Database Column Remarks: 今回売上金額
     *
     */
    private Long uriagegaku;

    /**
     * Database Column Remarks: 今回税抜売上金額
     *
     */
    private Long konkaitaxnukiurikin;

    /**
     * Database Column Remarks: 今回消費税金額
     *
     */
    private Long konkaishohitaxkin;

    /**
     * Database Column Remarks: ８％税抜売上金額
     *
     */
    private String n8perzeikomiuri;

    /**
     * Database Column Remarks: １０％税抜売上金額
     *
     */
    private String n10perzeikomiuri;

    /**
     * Database Column Remarks: 今回消費税金額
     *
     */
    private String kurikoshikin;

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
        sb.append(", shimedd=").append(shimedd);
        sb.append(", znkkurikoshikin=").append(znkkurikoshikin);
        sb.append(", konkainyknkin=").append(konkainyknkin);
        sb.append(", uriagegaku=").append(uriagegaku);
        sb.append(", konkaitaxnukiurikin=").append(konkaitaxnukiurikin);
        sb.append(", konkaishohitaxkin=").append(konkaishohitaxkin);
        sb.append(", n8perzeikomiuri=").append(n8perzeikomiuri);
        sb.append(", n10perzeikomiuri=").append(n10perzeikomiuri);
        sb.append(", kurikoshikin=").append(kurikoshikin);
        sb.append("]");
        return sb.toString();
    }
}
