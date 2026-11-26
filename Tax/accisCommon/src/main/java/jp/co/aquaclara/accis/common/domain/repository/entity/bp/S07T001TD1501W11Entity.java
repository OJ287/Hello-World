package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntityKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 請求関連処理（請求締処理）-請求締検索結果一覧Entity
 * 
 * @author accisdev044
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class S07T001TD1501W11Entity extends S07T001TEntityKey implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

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
     * Database Column Remarks: 請求先コード
     * 
     */
    private String skysakicd;

    /**
     * Database Column Remarks: 選択表示区分
     *
     */
    private String sentakukbn;

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
     * Database Column Remarks: 回収方法名称
     *
     */
    private String kaishuhohokbnname;

    /**
     * Database Column Remarks: 回収方法（請求時）
     *
     */
    private String kaishuhohokbnseikyu;

    /**
     * Database Column Remarks: 回収方法名称（請求時）
     *
     */
    private String kaishuhohokbnnameseikyu;

    /**
     * Database Column Remarks: 滞留区分
     *
     */
    private String tairyukbn;

    /**
     * Database Column Remarks: 滞留区分名称
     *
     */
    private String tairyukbnname;

    /**
     * Database Column Remarks: 処理状態
     *
     */
    private String syoristatus;

    /**
     * toString
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", shimedydm=").append(shimedydm);
        sb.append(", shimedd=").append(shimedd);
        sb.append(", skysakicd=").append(skysakicd);
        sb.append(", sentakukbn=").append(sentakukbn);
        sb.append(", jigyoshocd=").append(jigyoshocd);
        sb.append(", skysakinm=").append(skysakinm);
        sb.append(", kaishuhohokbn=").append(kaishuhohokbn);
        sb.append(", kaishuhohokbnname=").append(kaishuhohokbnname);
        sb.append(", kaishuhohokbnseikyu=").append(kaishuhohokbnseikyu);
        sb.append(", kaishuhohokbnnameseikyu=").append(kaishuhohokbnnameseikyu);
        sb.append(", tairyukbn=").append(tairyukbn);
        sb.append(", tairyukbnname=").append(tairyukbnname);
        sb.append(", syoristatus=").append(syoristatus);
        sb.append("]");
        return sb.toString();
    }
}
