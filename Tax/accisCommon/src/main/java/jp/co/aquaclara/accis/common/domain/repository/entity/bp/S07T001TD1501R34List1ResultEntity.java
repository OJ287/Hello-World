package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntityKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 請求書（CSVコンビニ)出力データResultEntity1.
 * 
 * @author accisdev047
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class S07T001TD1501R34List1ResultEntity extends S07T001TEntityKey implements Serializable {
    /**
     * serialVersionUID
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * Database Column Remarks:請求方法区分
     *
     */
    private String skyhohokbn;
    /**
     * Database Column Remarks:請求先名称1
     *
     */
    private String skysakinmn1;
    /**
     * Database Column Remarks:郵便番号
     *
     */
    private String yubinno;
    /**
     * Database Column Remarks:住所１
     *
     */
    private String adrsn1;
    /**
     * Database Column Remarks:住所２
     *
     */
    private String adrsn2;
    /**
     * Database Column Remarks:運用日付
     *
     */
    private String unyoymd;
    /**
     * Database Column Remarks:CVS支払期日
     *
     */
    private String siharauymd;
    /**
     * Database Column Remarks: 請求先コード
     *
     */
    private String skysakicd;

    /**
     * toString
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", skyhohokbn=").append(skyhohokbn);
        sb.append(", skysakinmn1=").append(skysakinmn1);
        sb.append(", yubinno=").append(yubinno);
        sb.append(", adrsn1=").append(adrsn1);
        sb.append(", adrsn2=").append(adrsn2);
        sb.append(", unyoymd=").append(unyoymd);
        sb.append(", siharauymd=").append(siharauymd);
        sb.append(", skysakicd=").append(skysakicd);
        sb.append("]");
        return sb.toString();
    }
}
