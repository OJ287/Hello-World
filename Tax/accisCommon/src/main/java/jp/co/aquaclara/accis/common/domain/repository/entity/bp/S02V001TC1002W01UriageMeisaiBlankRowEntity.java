package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * Database Table Remarks: 商品マスタビュー
 *
 * This class corresponds to the database table S02V001T
 */
@Data
public class S02V001TC1002W01UriageMeisaiBlankRowEntity implements Serializable {

    /**
     * Database Column Remarks: 商品コード
     *
     */
    private String shncd;

    /**
     * Database Column Remarks: 商品名称略称
     *
     */
    private String shnnmryakusho;

    /**
     * Database Column Remarks: 色
     *
     */
    private String iro;

    /**
     * Database Column Remarks: サイズ
     *
     */
    private String sz;

    /**
     * Database Column Remarks: 売上取扱単位名称
     *
     */
    private String uritrtskitaninm;

    /**
     * Database Column Remarks: 売上単位名称
     *
     */
    private String uritaninm;

    /**
     * Database Column Remarks: 売上入数
     *
     */
    private BigDecimal uriirisu;

    /**
     * Database Column Remarks: 仕入入数
     *
     */
    private BigDecimal siireirisu;

    /**
     * Database Column Remarks: 仕入取扱単位名称
     *
     */
    private String siiretrtskitaninm;

    /**
     * Database Column Remarks: 仕入単位名称
     *
     */
    private String siiretaninm;

    /**
     * Database Column Remarks: 標準価格
     *
     */
    private BigDecimal hyojunprice;

    /**
     * Database Column Remarks: 持ち帰り価格
     *
     */
    private BigDecimal mochikaeriprice;

    /**
     * Database Column Remarks: オープン価格
     *
     */
    private BigDecimal opnprice;

    /**
     * Database Column Remarks: オープン持ち帰り価格
     *
     */
    private BigDecimal opnmochikaeriprice;

    /**
     * Database Column Remarks: 大分類コード
     *
     */
    private String lctgrycd;

    /**
     * Database Column Remarks: 中分類コード
     *
     */
    private String mctgrycd;

    /**
     * Database Column Remarks: 小分類コード
     *
     */
    private String sctgrycd;

    /**
     * Database Column Remarks: 税区分
     *
     */
    private String taxkbn;

    /**
     * Database Column Remarks: セット区分
     *
     */
    private String stkbn;

    /**
     * Database Column Remarks: 価格区分
     *
     */
    private String pricekbn;

    /**
     * Database Column Remarks: 販促名称
     *
     */
    private String hansokunm;

    /**
     * Database Column Remarks: 販促開始日
     *
     */
    private String hansokustrtdd;

    /**
     * Database Column Remarks: 販促終了日
     *
     */
    private String hansokushrydd;

    /**
     * Database Column Remarks: 販促配達価格
     *
     */
    private BigDecimal hansokudlvryprice;

    /**
     * Database Column Remarks: 販促持ち帰り価格
     *
     */
    private BigDecimal hansokumochikaeriprice;

    /**
     * Database Column Remarks: 販促優先区分
     *
     */
    private String hansokuyusenkbn;

    /**
     * Database Column Remarks: 適用開始日（得意先別商品別単価マスタ）
     *
     */
    private String tstmtekiyostrtdd;

    /**
     * Database Column Remarks: 適用終了日（得意先別商品別単価マスタ）
     *
     */
    private String tstmtekiyoshrydd;

    /**
     * Database Column Remarks: 適用開始日（得意先別セット商品別単価マスタ）
     *
     */
    private String sttekiyostrtdd;

    /**
     * Database Column Remarks: 適用終了日（得意先別セット商品別単価マスタ）
     *
     */
    private String sttekiyoshrydd;

    /**
     * Database Column Remarks: 在庫単価
     *
     */
    private BigDecimal zaikount;

    /**
     * Database Column Remarks: 配達価格（得意先別商品別単価マスタ）
     *
     */
    private BigDecimal tstmdlvryprice;

    /**
     * Database Column Remarks: 持ち帰り価格（得意先別商品別単価マスタ）
     *
     */
    private BigDecimal tstmmochikaeriprice;

    /**
     * Database Column Remarks: 配達価格（得意先別セット商品別単価マスタ）
     *
     */
    private BigDecimal stdlvryprice;

    /**
     * Database Column Remarks: 持ち帰り価格（得意先別セット商品別単価マスタ）
     *
     */
    private BigDecimal stmochikaeriprice;

    /**
     * Database Column Remarks: 料金種別特別単価フラグ
     *
     */
    private String rknkindtokubetsuuntflg;

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
        sb.append(", shncd").append(shncd);
        sb.append(", shnnmryakusho").append(shnnmryakusho);
        sb.append(", iro").append(iro);
        sb.append(", sz").append(sz);
        sb.append(", uritrtskitaninm").append(uritrtskitaninm);
        sb.append(", uritaninm").append(uritaninm);
        sb.append(", uriirisu").append(uriirisu);
        sb.append(", siireirisu").append(siireirisu);
        sb.append(", siiretrtskitaninm").append(siiretrtskitaninm);
        sb.append(", siiretaninm").append(siiretaninm);
        sb.append(", hyojunprice").append(hyojunprice);
        sb.append(", mochikaeriprice").append(mochikaeriprice);
        sb.append(", opnprice").append(opnprice);
        sb.append(", opnmochikaeriprice").append(opnmochikaeriprice);
        sb.append(", lctgrycd").append(lctgrycd);
        sb.append(", mctgrycd").append(mctgrycd);
        sb.append(", sctgrycd").append(sctgrycd);
        sb.append(", taxkbn").append(taxkbn);
        sb.append(", stkbn").append(stkbn);
        sb.append(", pricekbn").append(pricekbn);
        sb.append(", hansokunm").append(hansokunm);
        sb.append(", hansokustrtdd").append(hansokustrtdd);
        sb.append(", hansokushrydd").append(hansokushrydd);
        sb.append(", hansokudlvryprice").append(hansokudlvryprice);
        sb.append(", hansokumochikaeriprice").append(hansokumochikaeriprice);
        sb.append(", hansokuyusenkbn").append(hansokuyusenkbn);
        sb.append(", tstmtekiyostrtdd").append(tstmtekiyostrtdd);
        sb.append(", tstmtekiyoshrydd").append(tstmtekiyoshrydd);
        sb.append(", sttekiyostrtdd").append(sttekiyostrtdd);
        sb.append(", sttekiyoshrydd").append(sttekiyoshrydd);
        sb.append(", zaikount").append(zaikount);
        sb.append(", tstmdlvryprice").append(tstmdlvryprice);
        sb.append(", tstmmochikaeriprice").append(tstmmochikaeriprice);
        sb.append(", stdlvryprice").append(stdlvryprice);
        sb.append(", stmochikaeriprice").append(stmochikaeriprice);
        sb.append(", rknkindtokubetsuuntflg").append(rknkindtokubetsuuntflg);
        sb.append(", meisaibiko").append(meisaibiko);
        sb.append("]");
        return sb.toString();
    }
}
