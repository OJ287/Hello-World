package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 入金消込履歴データCSV情報Entity
 * 
 * @author accisdev043
 *
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class S07T001TC1006W01CsvDataEntity extends S07T001TEntity implements Serializable {

    /**
     * Database Column Remarks: 事業所コード選択
     *
     */
    private String jigyoshosentaku;

    /**
     * Database Column Remarks: 請求先名称
     *
     */
    private String skysakinmn;

    /**
     * Database Column Remarks: 請求先名称カナ
     *
     */
    private String skysakinmkana;

    /**
     * Database Column Remarks: 請求締日
     *
     */
    private String shimeymd;

    /**
     * Database Column Remarks: 得意先名称
     *
     */
    private String tokuisakinmn1;

    /**
     * Database Column Remarks: 税込売上金額（今回税抜売上金額＋今回消費税金額）
     *
     */
    private Integer sumkin;

    /**
     * Database Column Remarks: 入金日
     *
     */
    private String nyknymd;

    /**
     * Database Column Remarks: 消込日
     *
     */
    private String kshkmdd;

    /**
     * Database Column Remarks: 入金種別
     *
     */
    private String nykinkind;

    /**
     * Database Column Remarks: 消込種別
     *
     */
    private String kskinkind;

    /**
     * Database Column Remarks: 入金伝票No
     *
     */
    private String denpyono;

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * toString
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", jigyoshosentaku=").append(jigyoshosentaku);
        sb.append(", skysakinmn=").append(skysakinmn);
        sb.append(", skysakinmkana=").append(skysakinmkana);
        sb.append(", shimeymd=").append(shimeymd);
        sb.append(", tokuisakinmn1=").append(tokuisakinmn1);
        sb.append(", sumkin=").append(sumkin);
        sb.append(", nyknymd=").append(nyknymd);
        sb.append(", kshkmdd=").append(kshkmdd);
        sb.append(", nykinkind=").append(nykinkind);
        sb.append(", kskinkind=").append(kskinkind);
        sb.append(", denpyono=").append(denpyono);
        sb.append("]");
        return sb.toString();
    }
}
