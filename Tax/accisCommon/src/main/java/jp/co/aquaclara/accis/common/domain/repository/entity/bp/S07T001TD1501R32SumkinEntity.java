package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntityKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 請求書のヘッダ部情報の金額情報抽出用結果Entity.
 * 
 * @author accisdev008
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class S07T001TD1501R32SumkinEntity extends S07T001TEntityKey implements Serializable {

    /**
     * 前回ご請求金額
     */
    private String znkkurikoshikin;

    /**
     * 今回ご利用金額
     */
    private String konkaitaxnukiurikin;

    /**
     * 消費税額
     */
    private String shohitaxkin;

    /**
     * ご入金額
     */
    private String konkainyknkin;

    /**
     * 今回ご請求額
     */
    private String kurikoshikin;

    /**
     * serialVersionUID
     *
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
        sb.append(", znkkurikoshikin=").append(znkkurikoshikin);
        sb.append(", konkaitaxnukiurikin=").append(konkaitaxnukiurikin);
        sb.append(", shohitaxkin=").append(shohitaxkin);
        sb.append(", konkainyknkin=").append(konkainyknkin);
        sb.append(", kurikoshikin=").append(kurikoshikin);
        sb.append("]");
        return sb.toString();
    }
}
