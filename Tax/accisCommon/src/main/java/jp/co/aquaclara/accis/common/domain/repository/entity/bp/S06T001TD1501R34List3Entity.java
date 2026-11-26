package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntityKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 請求書（CSVコンビニ)出力データEntity3.
 * 
 * @author accisdev047
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class S06T001TD1501R34List3Entity extends S07T001TEntityKey implements Serializable {

    private static final long serialVersionUID = 1L;
    /** 請求先コード */
    private String skysakicd;
    /** 締年月 */
    private String shimedydm;
    /** 締日 */
    private String shimedd;
    /** 締日 */
    private String unyoymd;

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
        sb.append(", unyoymd=").append(unyoymd);
        sb.append("]");
        return sb.toString();
    }
}
