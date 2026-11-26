package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M024TEntity;
import lombok.Data;

/**
 * Database Table Remarks: 請求残高テーブル
 *
 * This class corresponds to the database table S07T001T
 */
@Data
public class S02M024TF0501W37Entity extends S02M024TEntity implements Serializable {

    /**
     * Database Column Remarks: 次回予定日
     *
     */
    private String jikaiytidd;

    /**
     * Database Column Remarks: 商品コード
     *
     */
    private String shncd;

    /**
     * Database Column Remarks: 現商品名称
     *
     */
    private String genzshnnm;

    /**
     * Database Column Remarks: 新商品名称
     *
     */
    private String shinshnnm;

    /**
     * Database Column Remarks: 商品名称切替日
     *
     */
    private String shnnmkirikaedd;

    /**
     * Database Column Remarks: 数量
     *
     */
    private int suryo;

    /**
     * Database Column Remarks: 売上取扱単位名称
     *
     */
    private String uritrtskitaninm;

    /**
     * Database Column Remarks: 計上区分名称
     *
     */
    private String keijokbnnm;

    /**
     * Database Column Remarks: 明細備考
     *
     */
    private String meisaibiko;

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
        sb.append(", jikaiytidd=").append(jikaiytidd);
        sb.append(", shncd=").append(shncd);
        sb.append(", genzshnnm=").append(genzshnnm);
        sb.append(", shinshnnm=").append(shinshnnm);
        sb.append(", shnnmkirikaedd=").append(shnnmkirikaedd);
        sb.append(", suryo=").append(suryo);
        sb.append(", uritrtskitaninm=").append(uritrtskitaninm);
        sb.append(", keijokbnnm=").append(keijokbnnm);
        sb.append(", meisaibiko=").append(meisaibiko);
        sb.append("]");
        return sb.toString();
    }
}
