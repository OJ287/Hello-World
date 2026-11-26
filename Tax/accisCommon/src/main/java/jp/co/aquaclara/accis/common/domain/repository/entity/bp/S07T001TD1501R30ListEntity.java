package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntityKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 請求書出力データ抽出用Entity.
 * 
 * @author accisdev047
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class S07T001TD1501R30ListEntity extends S07T001TEntityKey implements Serializable {
    /**
     * serialVersionUID
     *
     */
    private static final long serialVersionUID = 1L;

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
     * toString
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
        sb.append("]");
        return sb.toString();
    }
}
