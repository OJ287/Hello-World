package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import java.util.List;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 請求関連処理（共通）-検索条件Entity
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class S07T001TD1501W00SearchConditionEntity extends S07T001TEntity implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Database Column Remarks: 事業所コード
     *
     */
    private String jigyoshocd;

    /**
     * Database Column Remarks: 運用日付
     *
     */
    private String unyoymd;

    /**
     * Database Column Remarks: 明細最大件数
     *
     */
    private Integer meisaimaxline;

    /**
     * Database Column Remarks: ACI経由請求フラグ
     */
    private List<S07T001TD1501W00AciKeiyuConditionEntity> acikeiyuflgList;

    /**
     * Database Column Remarks: 回収方法リスト
     *
     */
    private List<S07T001TD1501W00KaishuHohoConditionEntity> kaishuHohoList;

    /**
     * toString
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", jigyoshocd=").append(jigyoshocd);
        sb.append(", unyoymd=").append(unyoymd);
        sb.append(", meisaimaxline=").append(meisaimaxline);
        sb.append(", kaishuHohoList=").append(kaishuHohoList);
        sb.append(", acikeiyuflgList=").append(acikeiyuflgList);
        sb.append("]");
        return sb.toString();
    }
}
