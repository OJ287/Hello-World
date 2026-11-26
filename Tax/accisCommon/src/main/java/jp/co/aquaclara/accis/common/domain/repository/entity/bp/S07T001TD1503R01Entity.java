package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntity;
import lombok.Data;

/**
 * Database Table Remarks: 請求先マスタ
 *
 * This class corresponds to the database table S02M017T
 */
@Data
public class S07T001TD1503R01Entity extends S07T001TEntity implements Serializable {

    /**
     * Database Column Remarks: 事業所コード
     *
     */
    private String jigyoshocd;

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", jigyoshocd=").append(jigyoshocd);
        sb.append("]");
        return sb.toString();
    }
}
