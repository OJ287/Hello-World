package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntityKey;
import lombok.Data;

/**
 * Database Table Remarks: 配布物情報データ
 *
 * This class corresponds to the database table S04T001T
 */
@Data
public class S04T001TC0810R00HandoutsEntity extends S04T001TEntityKey implements Serializable {

    // 明細区分_配布物
    private String meisaikbn_1;
    // 税区分_子明細商品の構成品
    private String taxkbn_5;
    // セット商品明細印字フラグ_印字する
    private String stshnmeisaiinjiflg;
    // 入力明細フラグ_入力行
    private String nyuryokumeisaiflg;

    // 配布物名称
    private String haifubutsunm;
    // 納品数量
    private String nohinsuryo;
    // 受注伝票番号明細
    private String acptdnpynomei;
    // 枝番号明細
    private String edanomei;

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
        sb.append(", haifubutsunm=").append(haifubutsunm);
        sb.append(", nohinsuryo=").append(nohinsuryo);
        sb.append(", acptdnpynomei=").append(acptdnpynomei);
        sb.append(", edanomei=").append(edanomei);
        sb.append("]");
        return sb.toString();
    }
}
