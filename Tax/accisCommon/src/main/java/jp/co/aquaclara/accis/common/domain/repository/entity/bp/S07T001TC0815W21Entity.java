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
public class S07T001TC0815W21Entity extends S07T001TEntityKey implements Serializable {
    /**
     * Database Column Remarks: 請求先コード
     *
     */
    private String skysakicd;

    /**
     * Database Column Remarks: 請求処理実施区分
     *
     */
    private String skyprcsjissikbn;

    /**
     * Database Column Remarks: 有効区分
     *
     */
    private String yukokbn;

    /**
     * Database Column Remarks: 受注伝票番号
     *
     */
    private String acptdnpyno;

    /**
     * Database Column Remarks: 消込金
     *
     */
    private String keshikomikin;

    /**
     * Database Column Remarks: 得意先コード
     *
     */
    private String tokuisakicd;

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
        sb.append(", skyprcsjissikbn=").append(skyprcsjissikbn);
        sb.append(", yukokbn=").append(yukokbn);
        sb.append(", acptdnpyno=").append(acptdnpyno);
        sb.append("]");
        return sb.toString();
    }

}
