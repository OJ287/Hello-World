package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 請求関連処理（請求締処理）-請求締処理実行Entity
 * 
 * @author accisdev044
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class S07T001TD1501W11SkyShimeEntity extends S07T001TEntity implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Database Column Remarks: 請求締年月日
     *
     */
    private String seikyuFixDate;

    /**
     * toString
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", seikyuFixDate=").append(seikyuFixDate);
        sb.append("]");
        return sb.toString();
    }
}
