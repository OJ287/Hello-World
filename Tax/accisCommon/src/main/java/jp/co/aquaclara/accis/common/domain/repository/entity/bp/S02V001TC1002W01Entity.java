package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 売上明細伝票検索Entity
 */
@Data
public class S02V001TC1002W01Entity implements Serializable {
    /**
     * Database Column Remarks: 商品ＡＰ基本コード
     */
    private String shnapkihoncd;

    /**
     * Database Column Remarks: 商品コード
     */
    private String shncd;

    /**
     * Database Column Remarks: セット区分
     */
    private String stkbn;

    /**
     * Database Column Remarks: 大分類コード
     */
    private String lctgrycd;

    /**
     * Database Column Remarks: 中分類コード
     */
    private String mctgrycd;

    /**
     * Database Column Remarks: 小分類コード
     */
    private String sctgrycd;

    /**
     * Database Column Remarks: 商品名略称
     */
    private String shnnmryakusho;

    /**
     * Database Column Remarks: 価格区分
     */
    private String pricekbn;

    /**
     * Database Column Remarks: 標準価格切替日
     */
    private String hyojunpricekirikaedd;

    /**
     * Database Column Remarks: 現標準価格
     */
    private BigDecimal genzhyojunprice;

    /**
     * Database Column Remarks: 新標準価格
     */
    private BigDecimal shinhyojunprice;

    /**
     * Database Column Remarks: 現持ち帰り価格
     */
    private BigDecimal genzmochikaeriprice;

    /**
     * Database Column Remarks: 新持ち帰り価格
     */
    private BigDecimal shinmochikaeriprice;

    /**
     * Database Column Remarks: オープン価格切替日
     */
    private String opnpricekirikaedd;

    /**
     * Database Column Remarks: 現オープン価格
     */
    private BigDecimal genzopnprice;

    /**
     * Database Column Remarks: 新オープン価格
     */
    private BigDecimal shinopnprice;

    /**
     * Database Column Remarks: 現オープン持ち帰り価格
     */
    private BigDecimal genzopnmochikaeriprice;

    /**
     * Database Column Remarks: 新オープン持ち帰り価格
     */
    private BigDecimal shinopnmochikaeriprice;

    /**
     * Database Column Remarks: 販促名称
     */
    private String hansokunm;

    /**
     * Database Column Remarks: 販促区分
     */
    private String hansokukbn;

    /**
     * Database Column Remarks: 販促開始日
     */
    private String hansokustrtdd;

    /**
     * Database Column Remarks: 販促終了日
     */
    private String hansokushrydd;

    /**
     * Database Column Remarks: 販促配達価格
     */
    private BigDecimal hansokudlvryprice;

    /**
     * Database Column Remarks: 販促持ち帰り価格
     */
    private BigDecimal hansokumochikaeriprice;

    /**
     * Database Column Remarks: 販促優先区分
     */
    private String hansokuyusenkbn;
    
    /**
     * Database Column Remarks: 仕入単価
     */
    private BigDecimal siireunt;

    /**
     * Database Column Remarks: 在庫単価
     */
    private BigDecimal zaikount;

    /**
     * Database Column Remarks: 色
     */
    private String iro;

    /**
     * Database Column Remarks: サイズ名称
     */
    private String sz;

    /**
     * Database Column Remarks: 売上取扱単位名称
     */
    private String uritrtskitani;

    /**
     * Database Column Remarks: 売上入数
     */
    private Integer uriirisu;

    /**
     * Database Column Remarks: 売上単位名称
     */
    private String uritani;

    /**
     * Database Column Remarks: 仕入取扱単位名称
     */
    private String siiretrtskitani;

    /**
     * Database Column Remarks: 仕入入数
     */
    private Integer siireirisu;

    /**
     * Database Column Remarks: 仕入単位名称
     */
    private String siiretani;

    /**
     * Database Column Remarks: 税設定区分
     */
    private String taxsetteikbn;

    /**
     * Database Column Remarks: 税区分
     */
    private String taxkbn;
    
    /**
     * Database Column Remarks: 在庫アラーム数量
     */
    private Integer zaikoaramusuryo;
    
    /**
     * Database Column Remarks: 取扱区分
     */
    private String trtskikbn;

    /**
     * Database Column Remarks: 在庫管理対象フラグ
     */
    private String zaikokanritrgtflgshmv;

    /**
     * Database Column Remarks: 在庫管理区分
     */
    private String zaikokanrikbnshopm;

    /**
     * Database Column Remarks: 仕入単位変換区分
     */
    private String siiretanihenkankbn;
    
    /**
     * Database Column Remarks: 特別単価区分
     */
    private String tokubetsuuntkbn;

    /**
     * Database Column Remarks: 適用開始日(得意先別商品別単価マスタ)
     */
    private String tekiyostrtdd;
    
    /**
     * Database Column Remarks: 適用終了日(得意先別商品別単価マスタ)
     */
    private String tekiyoshrydd;

    /**
     * Database Column Remarks: 配達価格(得意先別商品別単価マスタ)
     */
    private BigDecimal dlvryprice;

    /**
     * Database Column Remarks: 持ち帰り価格(得意先別商品別単価マスタ)
     */
    private BigDecimal mochikaeriprice;

    /**
     * Database Column Remarks: 適用開始日(得意先別セット商品単価マスタ)
     */
    private String setTekiyostrtdd;
    
    /**
     * Database Column Remarks: 適用終了日(得意先別セット商品単価マスタ)
     */
    private String setTekiyoshrydd;

    /**
     * Database Column Remarks: 配達価格(得意先別セット商品単価マスタ)
     */
    private BigDecimal setDlvryprice;

    /**
     * Database Column Remarks: 持ち帰り価格(得意先別セット商品単価マスタ)
     */
    private BigDecimal setMochikaeriprice;
    
    /**
     * Database Column Remarks: 料金種別特別単価フラグ
     */
    private String rknkindtokubetsuuntflg;
    
    /**
     * 検索条件: AP基本コード
     */
    private String apkihoncd;

    /**
     * 検索条件: ショップコード
     */
    private String shopcd;
    
    /**
     * 検索条件: 得意先コード
     */
    private String tokuisakicd;
    
    /**
     * 検索条件: 売上日
     */
    private String uridd;

    /**
     * シリアルバージョン
     */
    private static final long serialVersionUID = 1L;

    /**
     * 文字列表現
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", apkihoncd=").append(apkihoncd);
        sb.append(", shopcd=").append(shopcd);
        sb.append(", shnapkihoncd=").append(shnapkihoncd);
        sb.append(", shncd=").append(shncd);
        sb.append(", stkbn=").append(stkbn);
        sb.append(", lctgrycd=").append(lctgrycd);
        sb.append(", mctgrycd=").append(mctgrycd);
        sb.append(", sctgrycd=").append(sctgrycd);
        sb.append(", shnnmryakusho=").append(shnnmryakusho);
        sb.append(", pricekbn=").append(pricekbn);
        sb.append(", hyojunpricekirikaedd=").append(hyojunpricekirikaedd);
        sb.append(", genzhyojunprice=").append(genzhyojunprice);
        sb.append(", shinhyojunprice=").append(shinhyojunprice);
        sb.append(", genzmochikaeriprice=").append(genzmochikaeriprice);
        sb.append(", shinmochikaeriprice=").append(shinmochikaeriprice);
        sb.append(", opnpricekirikaedd=").append(opnpricekirikaedd);
        sb.append(", genzopnprice=").append(genzopnprice);
        sb.append(", shinopnprice=").append(shinopnprice);
        sb.append(", genzopnmochikaeriprice=").append(genzopnmochikaeriprice);
        sb.append(", shinopnmochikaeriprice=").append(shinopnmochikaeriprice);
        sb.append(", hansokunm=").append(hansokunm);
        sb.append(", hansokukbn=").append(hansokukbn);
        sb.append(", hansokustrtdd=").append(hansokustrtdd);
        sb.append(", hansokushrydd=").append(hansokushrydd);
        sb.append(", hansokudlvryprice=").append(hansokudlvryprice);
        sb.append(", hansokumochikaeriprice=").append(hansokumochikaeriprice);
        sb.append(", hansokuyusenkbn=").append(hansokuyusenkbn);
        sb.append(", siireunt=").append(siireunt);
        sb.append(", zaikount=").append(zaikount);
        sb.append(", iro=").append(iro);
        sb.append(", sz=").append(sz);
        sb.append(", uritrtskitani=").append(uritrtskitani);
        sb.append(", uriirisu=").append(uriirisu);
        sb.append(", uritani=").append(uritani);
        sb.append(", siiretrtskitani=").append(siiretrtskitani);
        sb.append(", siireirisu=").append(siireirisu);
        sb.append(", siiretani=").append(siiretani);
        sb.append(", taxsetteikbn=").append(taxsetteikbn);
        sb.append(", taxkbn=").append(taxkbn);
        sb.append(", zaikoaramusuryo=").append(zaikoaramusuryo);
        sb.append(", trtskikbn=").append(trtskikbn);
        sb.append(", zaikokanritrgtflgshmv=").append(zaikokanritrgtflgshmv);
        sb.append(", zaikokanrikbnshopm=").append(zaikokanrikbnshopm);
        sb.append(", siiretanihenkankbn=").append(siiretanihenkankbn);
        sb.append(", tokubetsuuntkbn=").append(tokubetsuuntkbn);
        sb.append(", tekiyostrtdd=").append(tekiyostrtdd);
        sb.append(", tekiyoshrydd=").append(tekiyoshrydd);
        sb.append(", dlvryprice=").append(dlvryprice);
        sb.append(", mochikaeriprice=").append(mochikaeriprice);
        sb.append(", setTekiyostrtdd=").append(setTekiyostrtdd);
        sb.append(", setTekiyoshrydd=").append(setTekiyoshrydd);
        sb.append(", setDlvryprice=").append(setDlvryprice);
        sb.append(", setMochikaeriprice=").append(setMochikaeriprice);
        sb.append(", rknkindtokubetsuuntflg=").append(rknkindtokubetsuuntflg);
        sb.append("]");
        return sb.toString();
    }
}