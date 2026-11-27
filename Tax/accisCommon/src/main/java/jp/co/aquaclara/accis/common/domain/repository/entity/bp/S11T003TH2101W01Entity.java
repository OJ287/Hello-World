package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S11T003TEntityKey;
import lombok.Data;

/**
 * Database Table Remarks: 入出庫実績テーブル
 *
 * This class corresponds to the database table S11T003T
 */
@Data
public class S11T003TH2101W01Entity extends S11T003TEntityKey implements Serializable {
    /**
     * Database Column Remarks: 型式
     *
     */
    private String katasiki;

    /**
     * Database Column Remarks: 更新システム日付
     *
     */
    private String kshnsysmymd;

    /**
     * Database Column Remarks: 更新システム時間
     *
     */
    private String kshnsystmjikan;

    /**
     * Database Column Remarks: 入出庫伝票明細番号
     *
     */
    private String nyushukkodnpymeisaino;

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
     * Database Column Remarks: 商品コード
     *
     */
    private String shncd;

    /**
     * Database Column Remarks: 数量
     *
     */
    private String suryo;

    /**
     * Database Column Remarks: 単価
     *
     */
    private String unt;

    /**
     * Database Column Remarks:金額
     *
     */
    private String kin;

    /**
     * Database Column Remarks: 明細備考
     *
     */
    private String meisaibiko;

    /**
     * Database Column Remarks: 商品AP基本コード
     *
     */
    private String shnapkihoncd;

    /**
     * Database Column Remarks: 商品名称略称
     *
     */
    private String shnnmryakusho;

    /**
     * Database Column Remarks: 価格区分
     *
     */
    private String pricekbn;

    /**
     * Database Column Remarks: 標準価格切替日
     *
     */
    private String hyojunpricekirikaedd;

    /**
     * Database Column Remarks: 現標準価格
     *
     */
    private String genzhyojunprice;

    /**
     * Database Column Remarks: 新標準価格
     *
     */
    private String shinhyojunprice;

    /**
     * Database Column Remarks: 現持ち帰り価格
     *
     */
    private String genzmochikaeriprice;

    /**
     * Database Column Remarks: 新持ち帰り価格
     *
     */
    private String shinmochikaeriprice;

    /**
     * Database Column Remarks: オープン価格切替日
     *
     */
    private String opnpricekirikaedd;

    /**
     * Database Column Remarks: 現オープン価格
     *
     */
    private String genzopnprice;

    /**
     * Database Column Remarks: 新オープン価格
     *
     */
    private String shinopnprice;

    /**
     * Database Column Remarks: 現オープン持ち帰り価格
     *
     */
    private String genzopnmochikaeriprice;

    /**
     * Database Column Remarks: 新オープン持ち帰り価格
     *
     */
    private String shinopnmochikaeriprice;

    /**
     * Database Column Remarks: 販促区分
     *
     */
    private String hansokukbn;

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
    private String hansokudlvryprice;

    /**
     * Database Column Remarks: 販促持ち帰り価格
     *
     */
    private String hansokumochikaeriprice;

    /**
     * Database Column Remarks: 在庫単価
     *
     */
    private String zaikount;

    /**
     * Database Column Remarks: 色
     *
     */
    private String iro;

    /**
     * Database Column Remarks: サイズ名称
     *
     */
    private String sz;

    /**
     * Database Column Remarks: 売上取扱単位名称
     *
     */
    private String uritrtskitani;

    /**
     * Database Column Remarks: 売上入数
     *
     */
    private String uriirisu;

    /**
     * Database Column Remarks: 売上単位名称
     *
     */
    private String uritani;

    /**
     * Database Column Remarks: 在庫管理対象フラグ
     *
     */
    private String zaikokanritrgtflgshmv;

    /**
     * Database Column Remarks: 在庫管理区分
     *
     */
    private String zaikokanrikbnshopm;

    /**
     * Database Column Remarks: 仕入取扱単位名称
     *
     */
    private String siiretrtskitani;

    /**
     * Database Column Remarks: 仕入入数
     *
     */
    private String siireirisu;

    /**
     * Database Column Remarks: 仕入単位名称
     *
     */
    private String siiretani;

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
     * Database Column Remarks: 在庫アラーム数量
     *
     */
    private String zaikoaramusuryo;

    /**
     * Database Column Remarks: 取扱区分
     *
     */
    private String trtskikbn;

    /**
     * Database Column Remarks: 仕入総数
     *
     */
    private String siiresokazu;

    /**
     * Database Column Remarks: 売上総数
     *
     */
    private String urisokazu;

    /**
     * 検索条件: 伝票区分
     *
     */
    private String dnpykbn;

    /**
     * 検索条件: 明細最大件数
     *
     */
    private Integer rownum;

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
        sb.append(", katasiki=").append(katasiki);
        sb.append(", kshnsysmymd=").append(kshnsysmymd);
        sb.append(", kshnsystmjikan=").append(kshnsystmjikan);
        sb.append(", nyushukkodnpymeisaino=").append(nyushukkodnpymeisaino);
        sb.append(", lctgrycd=").append(lctgrycd);
        sb.append(", mctgrycd=").append(mctgrycd);
        sb.append(", sctgrycd=").append(sctgrycd);
        sb.append(", shncd=").append(shncd);
        sb.append(", suryo=").append(suryo);
        sb.append(", unt=").append(unt);
        sb.append(", kin=").append(kin);
        sb.append(", meisaibiko=").append(meisaibiko);
        sb.append(", shnapkihoncd=").append(shnapkihoncd);
        sb.append(", shnnmryakusho=").append(shnnmryakusho);
        sb.append(", pricekbn=").append(pricekbn);
        sb.append(", hyojunpricekirikaedd=").append(hyojunpricekirikaedd);
        sb.append(", genzhyojunprice=").append(genzhyojunprice);
        sb.append(", shinhyojunprice=").append(shinhyojunprice);
        sb.append(", genzmochikaeriprice=").append(genzmochikaeriprice);
        sb.append(", shinmochikaeriprice=").append(shinmochikaeriprice);
        sb.append(", opnpricekirikaedd=").append(opnpricekirikaedd);
        sb.append(", genzponprice=").append(genzopnprice);
        sb.append(", shinopnprice=").append(shinopnprice);
        sb.append(", genzopnmochikaeriprice=").append(genzopnmochikaeriprice);
        sb.append(", shinopnmochikaeriprice=").append(shinopnmochikaeriprice);
        sb.append(", hansokukbn=").append(hansokukbn);
        sb.append(", hansokustrtdd=").append(hansokustrtdd);
        sb.append(", hansokushrydd=").append(hansokushrydd);
        sb.append(", hansokudlvryprice=").append(hansokudlvryprice);
        sb.append(", hansokumochikaeriprice=").append(hansokumochikaeriprice);
        sb.append(", zaikount=").append(zaikount);
        sb.append(", iro=").append(iro);
        sb.append(", sz=").append(sz);
        sb.append(", uritrtskitani=").append(uritrtskitani);
        sb.append(", uriirisu=").append(uriirisu);
        sb.append(", uritani=").append(uritani);
        sb.append(", zaikokanritrgtflgshmv=").append(zaikokanritrgtflgshmv);
        sb.append(", zaikokanrikbnshopm=").append(zaikokanrikbnshopm);
        sb.append(", siiretrtskitani=").append(siiretrtskitani);
        sb.append(", siireirisu=").append(siireirisu);
        sb.append(", siiretani=").append(siiretani);
        sb.append(", taxsetteikbn=").append(taxsetteikbn);
        sb.append(", taxkbn=").append(taxkbn);
        sb.append(", zaikoaramusuryo=").append(zaikoaramusuryo);
        sb.append(", trtskikbn=").append(trtskikbn);
        sb.append(", siiresokazu=").append(siiresokazu);
        sb.append(", urisokazu=").append(urisokazu);
        sb.append("]");
        return sb.toString();
    }
}
