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
public class S07T001TB1601W01Entity extends S07T001TEntityKey implements Serializable {

    /**
     * Database Column Remarks: 繰越金額（合計）
     *
     */
    private String kurikoshikingoukei;

    /**
     * Database Column Remarks: 今回消費税込金額（合計）
     *
     */
    private String konkaishohitaxkingoukei;

    /**
     * 検索条件: 請求締年月
     *
     */
    private String skyshimedydm;

    /**
     * 検索条件: 請求締日
     *
     */
    private String skyshimedd;

    /**
     * 検索条件: 請求先コード
     *
     */
    private String skysakicd;

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
        sb.append(", kurikoshikingoukei=").append(kurikoshikingoukei);
        sb.append(", konkaishohitaxkingoukei=").append(konkaishohitaxkingoukei);
        sb.append("]");
        return sb.toString();
    }
}
