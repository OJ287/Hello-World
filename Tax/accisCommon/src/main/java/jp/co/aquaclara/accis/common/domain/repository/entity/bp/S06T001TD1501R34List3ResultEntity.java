package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S06T001TEntityKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 請求書（CSVコンビニ)出力データResultEntity3.
 * 
 * @author accisdev047
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class S06T001TD1501R34List3ResultEntity extends S06T001TEntityKey implements Serializable {
    /**
     * serialVersionUID
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * Database Column Remarks:売上日
     *
     */
    private String ymd;
    /**
     * Database Column Remarks:商品コード
     *
     */
    private String shncd;
    /**
     * Database Column Remarks:商品名称
     *
     */
    private String shnnm;
    /**
     * Database Column Remarks:税区分
     *
     */
    private String taxkbn;
    /**
     * Database Column Remarks:数量
     *
     */
    private String suryo;
    /**
     * Database Column Remarks:売上金額
     *
     */
    private String kin;
    /**
     * Database Column Remarks:単位名称
     *
     */
    private String uritrtskitaninm;
    /**
     * Database Column Remarks:売上計上区分
     *
     */
    private String keijokbn;

    /**
     * toString
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", ymd=").append(ymd);
        sb.append(", shncd=").append(shncd);
        sb.append(", shnnm=").append(shnnm);
        sb.append(", taxkbn=").append(taxkbn);
        sb.append(", suryo=").append(suryo);
        sb.append(", kin=").append(kin);
        sb.append(", uritrtskitaninm=").append(uritrtskitaninm);
        sb.append(", keijokbn=").append(keijokbn);
        sb.append("]");
        return sb.toString();
    }
}
