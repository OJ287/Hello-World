package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S06T001TEntity;
import lombok.Data;

/**
 * Database Table Remarks: 売上テーブル
 *
 * This class corresponds to the database table S06T001T
 */
@Data
public class S06T001TC0501W02Entity extends S06T001TEntity implements Serializable {
    /**
     * Database Column Remarks: 単価消費税金額
     *
     */
    private Long taxkin;

    /**
     * Database Column Remarks: 8%税込売上金額
     *
     */
    private Long n8perzeikomiuri;

    /**
     * Database Column Remarks: 10%税込売上金額
     *
     */
    private Long n10perzeikomiuri;

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
        sb.append(", taxkin=").append(taxkin);
        sb.append(", n8perzeikomiuri=").append(n8perzeikomiuri);
        sb.append(", n10perzeikomiuri=").append(n10perzeikomiuri);
        sb.append("]");
        return sb.toString();
    }
}
