package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 請求関連処理（請求締処理）-請求締検索条件Entity
 * 
 * @author accisdev044
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class S07T001TD1501W11SearchConditionEntity extends S07T001TD1501W00SearchConditionEntity
    implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Database Column Remarks: 抽出締日
     *
     */
    private String extractshimedd;

    /**
     * Database Column Remarks: 抽出締年月日
     *
     */
    private String extractshimeymd;

    /**
     * Database Column Remarks: 締年月末日
     *
     */
    private String shimelastdd;

    /**
     * Database Column Remarks: 処理
     *
     */
    private String prcskbn;

    /**
     * Database Column Remarks: 対象データ
     *
     */
    private String trgtdat;

    /**
     * Database Column Remarks: カナ名称
     *
     */
    private String skysakinmkana;

    /**
     * Database Column Remarks: 得意先有無（有）
     *
     */
    private String ttumu1;

    /**
     * Database Column Remarks: 得意先有無（無）
     *
     */
    private String ttumu2;

    /**
     * Database Column Remarks: 請求先管理区分
     *
     */
    private String skysakikanrikbn;

    /**
     * Database Column Remarks: 請求区分（親子）
     *
     */
    private String skykbn1;

    /**
     * Database Column Remarks: 請求区分（一般）
     *
     */
    private String skykbn2;

    /**
     * Database Column Remarks: 早出し
     *
     */
    private String hayadasiflg;

    /**
     * toString
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", extractshimedd=").append(extractshimedd);
        sb.append(", extractshimeymd=").append(extractshimeymd);
        sb.append(", shimelastdd=").append(shimelastdd);
        sb.append(", prcskbn=").append(prcskbn);
        sb.append(", trgtdat=").append(trgtdat);
        sb.append(", skysakinmkana=").append(skysakinmkana);
        sb.append(", ttumu1=").append(ttumu1);
        sb.append(", ttumu2=").append(ttumu2);
        sb.append(", skysakikanrikbn=").append(skysakikanrikbn);
        sb.append(", skykbn1=").append(skykbn1);
        sb.append(", skykbn2=").append(skykbn2);
        sb.append(", hayadasiflg=").append(hayadasiflg);
        sb.append("]");
        return sb.toString();
    }
}
