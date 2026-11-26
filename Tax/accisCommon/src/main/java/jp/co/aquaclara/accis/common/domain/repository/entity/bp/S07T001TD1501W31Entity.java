package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntityKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 請求関連処理(請求書)-請求書検索検索結果Entity
 * 
 * @author accisdev047
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class S07T001TD1501W31Entity extends S07T001TEntityKey implements Serializable {
    /**
     * serialVersionUID
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Database Column Remarks: 事業所コード
     *
     */
    private String jigyoshocd;

    /**
     * Database Column Remarks: 請求先名称
     *
     */
    private String skysakinm;

    /**
     * Database Column Remarks: 回収方法
     *
     */
    private String kaishuhohokbn;

    /**
     * Database Column Remarks: 回収方法名
     *
     */
    private String kaishuhohokbnnm;

    /**
     * Database Column Remarks: 滞留区分
     *
     */
    private String tairyukbn;

    /**
     * Database Column Remarks: 滞留区分名
     *
     */
    private String tairyukbnnm;

    /**
     * Database Column Remarks: 処理状態
     *
     */
    private String syoristatus;

    /**
     * Database Column Remarks: 処理状態名称
     *
     */
    private String syoristatusnm;
    /**
     * Database Column Remarks: 請求先コード
     *
     */
    private String skysakicd;

    /**
     * Database Column Remarks: 締年月
     *
     */
    private String shimedydm;

    /**
     * Database Column Remarks: 締日
     *
     */
    private String shimedd;

    /**
     * Database Column Remarks: 郵便番号
     *
     */
    private String yubinno;

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
        sb.append(", skysakinm=").append(skysakinm);
        sb.append(", kaishuhohokbn=").append(kaishuhohokbn);
        sb.append(", kaishuhohokbnnm=").append(kaishuhohokbnnm);
        sb.append(", tairyukbn=").append(tairyukbn);
        sb.append(", tairyukbnnm=").append(tairyukbnnm);
        sb.append(", syoristatus=").append(syoristatus);
        sb.append(", syoristatusnm=").append(syoristatusnm);
        sb.append(", skysakicd=").append(skysakicd);
        sb.append(", shimedydm=").append(shimedydm);
        sb.append(", shimedd=").append(shimedd);
        sb.append(", yubinno=").append(yubinno);
        sb.append("]");
        return sb.toString();
    }
}
