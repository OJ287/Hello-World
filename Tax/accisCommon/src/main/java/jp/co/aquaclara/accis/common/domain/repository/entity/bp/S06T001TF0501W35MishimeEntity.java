package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S06T001TEntityKey;
import lombok.Data;

@Data
public class S06T001TF0501W35MishimeEntity extends S06T001TEntityKey implements Serializable {

    // 請求先コード
    private String skysakicd;

    // 日付初期化
    private String initymd;

    // 代引き
    private String daibiki;

    // 有効区分
    private String yukokbn;

    // 入力明細フラグ
    private String nyuryokumeisaiflg;

    // 金額
    private String kin;

    // 税区分
    private String taxkbn;

    // 売上日
    private String uridd;

    // 税計算区分
    private String taxkeisankbn;

    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", skysakicd=").append(skysakicd);
        sb.append(", initymd=").append(initymd);
        sb.append(", daibiki=").append(daibiki);
        sb.append(", yukokbn=").append(yukokbn);
        sb.append(", nyuryokumeisaiflg=").append(nyuryokumeisaiflg);
        sb.append(", kin=").append(kin);
        sb.append(", taxkbn=").append(taxkbn);
        sb.append(", uridd=").append(uridd);
        sb.append(", taxkeisankbn=").append(taxkeisankbn);
        sb.append("]");
        return sb.toString();
    }
}
