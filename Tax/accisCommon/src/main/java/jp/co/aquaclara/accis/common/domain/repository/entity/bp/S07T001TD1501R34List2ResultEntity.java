package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntityKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 請求書（CSVコンビニ)出力データResultEntity2.
 * 
 * @author accisdev047
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class S07T001TD1501R34List2ResultEntity extends S07T001TEntityKey implements Serializable {
    /**
     * serialVersionUID
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * Database Column Remarks:繰越金額
     *
     */
    private String kurikoshikin;
    /**
     * Database Column Remarks:今回税抜売上金額
     *
     */
    private String konkaitaxnukiurikin;
    /**
     * Database Column Remarks:今回消費税金額
     *
     */
    private String konkaishohitaxkin;
    /**
     * Database Column Remarks:前回繰越金額
     *
     */
    private String znkkurikoshikin;
    /**
     * Database Column Remarks:８％税抜売上金額
     *
     */
    private String n8perzeinukiurikin;
    /**
     * Database Column Remarks:８％消費税金額
     *
     */
    private String n8pershohitaxkin;
    /**
     * Database Column Remarks:１０％税抜売上金額
     *
     */
    private String n10perzeinukiurikin;
    /**
     * Database Column Remarks:１０％消費税金額
     *
     */
    private String n10pershohitaxkin;
    /**
     * Database Column Remarks:今回入金金額
     *
     */
    private String konkainyknkin;
    /**
     * Database Column Remarks:税計算区分
     *
     */
    private String taxkeisankbn;

    /**
     * toString
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", kurikoshikin=").append(kurikoshikin);
        sb.append(", konkaitaxnukiurikin=").append(konkaitaxnukiurikin);
        sb.append(", konkaishohitaxkin=").append(konkaishohitaxkin);
        sb.append(", znkkurikoshikin=").append(znkkurikoshikin);
        sb.append(", n8perzeinukiurikin=").append(n8perzeinukiurikin);
        sb.append(", n8pershohitaxkin=").append(n8pershohitaxkin);
        sb.append(", n10perzeinukiurikin=").append(n10perzeinukiurikin);
        sb.append(", n10pershohitaxkin=").append(n10pershohitaxkin);
        sb.append(", konkainyknkin=").append(konkainyknkin);
        sb.append(", taxkeisankbn=").append(taxkeisankbn);
        sb.append("]");
        return sb.toString();
    }
}
