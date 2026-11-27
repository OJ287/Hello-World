package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntityKey;
import lombok.Data;

/**
 * Database Table Remarks: 商品マスタビュー
 *
 * This class corresponds to the database table S02V001T
 */
@Data
public class S02V001TB0501W02Entity extends S02V001TEntityKey implements Serializable {

    /**
     * Database Column Remarks: 商品名称略称
     *
     */
    private String shnnmryakusho;

    /**
     * Database Column Remarks: 標準価格
     *
     */
    private String hyojunprice;

    /**
     * Database Column Remarks: 税設定区分
     *
     */
    private String taxsetteikbn;

    /**
     * Database Column Remarks: 税区分
     *
     */
    private String taxkbn;

    /**
     * 検索条件: 運用日付
     *
     */
    private String unyodd;

    /**
     * 検索条件: 定数_有効区分_1（有効）
     */
    private String constyukokbn1 = CommonConstants.YUKOKBN_1;

    /**
     * 検索条件: 定数_AP基本コード_000
     */
    private String constapkihoncd000 = CommonConstants.APCD_000;

    /**
     * 検索条件: 定数_ショップコード_000
     */
    private String constshopcd000 = CommonConstants.SYS_SHOPCD_000;

    /**
     * 検索条件: 定数_レコード区分_M（明細）
     */
    private String constrecordkbnm = CommonConstants.RECORD_KBN_M;

    /**
     * 検索条件: 定数_設定番号_096（商品大分類）
     */
    private String constsetteino096 = CommonConstants.SYS_SETTEINO_096;

    /**
     * 検索条件: 定数_設定コード_096_3（ＳＶ用商品大分類）
     */
    private String constsetteicd096_3 = CommonConstants.SETTEICD_096_3;

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
        sb.append(", shnnmryakusho=").append(shnnmryakusho);
        sb.append(", hyojunprice=").append(hyojunprice);
        sb.append(", taxsetteikbn=").append(taxsetteikbn);
        sb.append(", taxkbn=").append(taxkbn);
        sb.append("]");
        return sb.toString();
    }
}
