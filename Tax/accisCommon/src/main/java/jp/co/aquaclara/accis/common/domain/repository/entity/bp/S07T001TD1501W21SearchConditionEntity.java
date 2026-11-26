package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 請求関連処理（請求一覧表）-請求一覧表検索条件Entity
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class S07T001TD1501W21SearchConditionEntity extends S07T001TD1501W00SearchConditionEntity
    implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Database Column Remarks: 営業担当者コード
     *
     */
    private String eigyotnshacd;
    /**
     * Database Column Remarks: 回収担当者コード
     *
     */
    private String kaishutnshacd;
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
     * Database Column Remarks: 抽出締日
     *
     */
    private String extractshimedd;
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
     * Database Column Remarks: 請求明細区分１
     *
     */
    private String skymeisaisofukbn1;
    /**
     * Database Column Remarks: 請求明細区分2
     *
     */
    private String skymeisaisofukbn2;

    /**
     * 文字列変換
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", eigyotnshacd=").append(eigyotnshacd);
        sb.append(", kaishutnshacd=").append(kaishutnshacd);
        sb.append(", skysakinmkana=").append(skysakinmkana);
        sb.append(", ttumu1=").append(ttumu1);
        sb.append(", ttumu2=").append(ttumu2);
        sb.append(", skysakikanrikbn=").append(skysakikanrikbn);
        sb.append(", extractshimedd=").append(extractshimedd);
        sb.append(", skykbn1=").append(skykbn1);
        sb.append(", skykbn2=").append(skykbn2);
        sb.append(", hayadasiflg=").append(hayadasiflg);
        sb.append(", skymeisaisofukbn1=").append(skymeisaisofukbn1);
        sb.append(", skymeisaisofukbn2=").append(skymeisaisofukbn2);
        sb.append("]");
        return sb.toString();
    }
}
