package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntity;
import lombok.Data;

/**
 * Database Table Remarks: 請求残高テーブル
 *
 * This class corresponds to the database table S07T001T
 */
@Data
public class S07T001TF0501W35Entity extends S07T001TEntity implements Serializable {

    /**
     * Database Column Remarks: 今回税抜売上金額
     *
     */
    private Long uriagegaku;

    /**
     * Database Column Remarks: ８％税抜売上金額
     *
     */
    private Long n8perzeikomiuri;

    /**
     * Database Column Remarks: １０％税抜売上金額
     *
     */
    private Long n10perzeikomiuri;

    /**
     * Database Column Remarks: 現税計算区分売上
     *
     */
    private String genztaxkeisankbnuri;

    /**
     * Database Column Remarks: 新税計算区分売上
     *
     */
    private String shintaxkeisankbnuri;

    /**
     * Database Column Remarks: 税計算区分売上切替日
     *
     */
    private String taxkeisankbnurikirikaedd;

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
        sb.append(", uriagegaku=").append(uriagegaku);
        sb.append(", n8perzeikomiuri=").append(n8perzeikomiuri);
        sb.append(", n10perzeikomiuri=").append(n10perzeikomiuri);
        sb.append(", genztaxkeisankbnuri=").append(genztaxkeisankbnuri);
        sb.append(", shintaxkeisankbnuri=").append(shintaxkeisankbnuri);
        sb.append(", taxkeisankbnurikirikaedd=").append(taxkeisankbnurikirikaedd);
        sb.append("]");
        return sb.toString();
    }
}
