package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import java.util.List;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntityKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 入金管理一覧検索条件Entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class S07T001TC1006W01ConditionEntity extends S07T001TEntityKey implements Serializable {

    /**
     * Database Column Remarks: 事業所コード選択
     *
     */
    private String jigyoshosentaku;

    /**
     * Database Column Remarks: 請求先コード
     *
     */
    private String skysakicd;

    /**
     * Database Column Remarks: 外部連携コード
     *
     */
    private String gaiburenkeicd;

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
     * Database Column Remarks: 回収方法リスト
     *
     */
    private List<S07T001TD1501W00KaishuHohoConditionEntity> kaishuHohoList;

    /**
     * Database Column Remarks: 決済機関コードリスト
     *
     */
    private List<String> ksikkncdlist;

    /**
     * Database Column Remarks: 口座名義カナ
     *
     */
    private String kzmeigikana;

    /**
     * Database Column Remarks: 口座振替連携コード
     *
     */
    private String kzfurikaerenkeicd;

    /**
     * Database Column Remarks: 入金状態
     *
     */
    private String nyknjotai;

    /**
     * Database Column Remarks: 滞留区分リスト
     *
     */
    private List<String> tairyukbnlist;

    /**
     * Database Column Remarks: 請求年月日開始
     *
     */
    private String skydydmddstrt;

    /**
     * Database Column Remarks: 請求年月日終了
     *
     */
    private String skydydmddshry;

    /**
     * Database Column Remarks: 入金日開始
     *
     */
    private String nyknddstrt;

    /**
     * Database Column Remarks: 入金日終了
     *
     */
    private String nyknddshry;

    /**
     * Database Column Remarks: 入金予定日開始
     *
     */
    private String nyknytiddstrt;

    /**
     * Database Column Remarks: 入金予定日終了
     *
     */
    private String nyknytiddshry;

    /**
     * Database Column Remarks: 入金種別リスト
     *
     */
    private List<String> nyknkindkbnlist;

    /**
     * Database Column Remarks: 入金伝票No
     *
     */
    private String denpyono;

    /**
     * Database Column Remarks: 明細最大件数
     *
     */
    private int meisaisaidaicnt;

    /**
     * 請求残高が無い入金を含めるフラグ
     */
    private String includenobalanceflg;

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
        sb.append(", skysakicd=").append(skysakicd);
        sb.append(", gaiburenkeicd=").append(gaiburenkeicd);
        sb.append(", skysakinmn=").append(skysakinmn);
        sb.append(", skysakinmkana=").append(skysakinmkana);
        sb.append(", kaishuHohoList=").append(kaishuHohoList);
        sb.append(", ksikkncdlist=").append(ksikkncdlist);
        sb.append(", kzmeigikana=").append(kzmeigikana);
        sb.append(", kzfurikaerenkeicd=").append(kzfurikaerenkeicd);
        sb.append(", nyknjotai=").append(nyknjotai);
        sb.append(", tairyukbnlist=").append(tairyukbnlist);
        sb.append(", skydydmddstrt=").append(skydydmddstrt);
        sb.append(", skydydmddshry=").append(skydydmddshry);
        sb.append(", nyknddstrt=").append(nyknddstrt);
        sb.append(", nyknddshry=").append(nyknddshry);
        sb.append(", nyknytiddstrt=").append(nyknytiddstrt);
        sb.append(", nyknytiddshry=").append(nyknytiddshry);
        sb.append(", nyknkindkbnlist=").append(nyknkindkbnlist);
        sb.append(", denpyono=").append(denpyono);
        sb.append(", meisaisaidaicnt=").append(meisaisaidaicnt);
        sb.append(", includenobalanceflg=").append(includenobalanceflg);
        sb.append("]");
        return sb.toString();
    }
}
