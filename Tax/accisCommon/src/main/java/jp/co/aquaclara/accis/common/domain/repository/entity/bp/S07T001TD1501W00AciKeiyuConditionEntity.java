/**
 * 
 */
package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 請求関連処理用ACI本部NA区分検索条件Entity
 */
@Getter
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode
public class S07T001TD1501W00AciKeiyuConditionEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 区分値 */
    private String value;

    /**
     * 検索条件用のEntityリストを生成します
     * 
     * @param acikeiyuflg1 ACI本部NA区分（NA売上連携）の入力値
     * @param acikeiyuflg2 ACI本部NA区分（NA委受託連携）の入力値
     * @return 検索条件Entityリスト
     */
    public static List<S07T001TD1501W00AciKeiyuConditionEntity> createCondition(String acikeiyuflg1,
        String acikeiyuflg2) {
        List<S07T001TD1501W00AciKeiyuConditionEntity> condition = new ArrayList<>();
        if (CommonConstants.ACI_KEIYU_KSIKBN_1.equals(acikeiyuflg1)) {
            condition
                .add(S07T001TD1501W00AciKeiyuConditionEntity.builder().value(acikeiyuflg1).build());
        }
        if (CommonConstants.ACI_KEIYU_KSIKBN_2.equals(acikeiyuflg2)) {
            condition
                .add(S07T001TD1501W00AciKeiyuConditionEntity.builder().value(acikeiyuflg2).build());
        }
        return condition;
    }
}
