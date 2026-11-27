package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import java.math.BigDecimal;
import jp.co.aquaclara.accis.common.domain.repository.entity.S06T002TEntityKey;
import lombok.Data;

/**
 * Database Table Remarks: 売上明細テーブル
 */
@Data
public class S06T002TC1002W01Entity extends S06T002TEntityKey implements Serializable {
    /**
     * Database Column Remarks: 枝番号
     */
    private Short edano;
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
     * Database Column Remarks: 商品コード
     */
    private String shncd;
    /**
     * Database Column Remarks: 商品名
     */
    private String shnnmryakusho;
    /**
     * Database Column Remarks: 計上区分
     */
    private String keijokbn;
    /**
     * Database Column Remarks: 数量
     */
    private BigDecimal suryo;
    /**
     * Database Column Remarks: 標準単価
     */
    private BigDecimal hyojununt;
    /**
     * Database Column Remarks: 単価
     */
    private BigDecimal unt;
    /**
     * Database Column Remarks: 金額
     */
    private Long kin;
    /**
     * Database Column Remarks: 単価消費税金額
     */
    private Long untshohitaxkin;
    /**
     * Database Column Remarks: 消費税金額
     */
    private Long shohitaxkin;
    /**
     * Database Column Remarks: 参考消費税金額
     */
    private Long sankoushohitaxkin;
    /**
     * Database Column Remarks: 税区分
     */
    private String taxkbn;
    /**
     * Database Column Remarks: 明細区分
     */
    private String meisaikbn;
    /**
     * Database Column Remarks: 入力明細フラグ
     */
    private String nyuryokumeisaiflg;
    /**
     * Database Column Remarks: 単品明細フラグ
     */
    private String tanpinmeisaiflg;
    /**
     * Database Column Remarks: 明細備考
     */
    private String meisaibiko;
    /**
     * Database Column Remarks: セット商品コード
     */
    private String stshncd;
    /**
     * Database Column Remarks: 型式
     */
    private String katasiki;
    /**
     * Database Column Remarks: 仕入数量
     */
    private BigDecimal siiresuryo;
    /**
     * Database Column Remarks: 仕入金額
     */
    private Long siirekin;
    /**
     * Database Column Remarks: 仕入単価
     */
    private BigDecimal siireunt;
    /**
     * Database Column Remarks: 仕入消費税金額
     */
    private Long siireshohitaxkin;
    /**
     * Database Column Remarks: 在庫更新済フラグ
     */
    private String zaikokshnsumiflg;

    /**
     * Database Column Remarks: 得意先区分
     */
    private String tokuisakikbn;
    /**
     * Database Column Remarks: 受託配達顧客区分
     */
    private String jutakudlvrycstkbn;
    /**
     * Database Column Remarks: ロイヤリティ締め年月日
     */
    private String royaltyshimeymd;

    /**
     * Database Column Remarks: 有効区分
     */
    private String yukokbn;
    /**
     * Database Column Remarks: 登録者ＡＰ基本コード
     */
    private String toshaapkihoncd;
    /**
     * Database Column Remarks: 登録者ショップコード
     */
    private String toshashopcd;
    /**
     * Database Column Remarks: 登録者コード
     */
    private String toshacd;
    /**
     * Database Column Remarks: 登録者名
     */
    private String toshanm;
    /**
     * Database Column Remarks: 登録システム日付
     */
    private String inptsystmymd;
    /**
     * Database Column Remarks: 登録システム時間
     */
    private String inptsystmjikan;
    /**
     * Database Column Remarks: 登録運用日付
     */
    private String inptunyoymd;
    /**
     * Database Column Remarks: 登録ＩＰアドレス
     */
    private String inptipadrse;
    /**
     * Database Column Remarks: 登録プログラムＩＤ
     */
    private String inptpgmid;
    /**
     * Database Column Remarks: 更新者ＡＰ基本コード
     */
    private String koshaapkihoncd;
    /**
     * Database Column Remarks: 更新者ショップコード
     */
    private String koshashopcd;
    /**
     * Database Column Remarks: 更新者コード
     */
    private String koshacd;
    /**
     * Database Column Remarks: 更新者名
     */
    private String koshanm;
    /**
     * Database Column Remarks: 更新システム日付
     */
    private String kshnsystmymd;
    /**
     * Database Column Remarks: 更新システム時間
     */
    private String kshnsystmjikan;
    /**
     * Database Column Remarks: 更新運用日付
     */
    private String kshnunyoymd;
    /**
     * Database Column Remarks: 更新ＩＰアドレス
     */
    private String kshnipadrse;
    /**
     * Database Column Remarks: 更新プログラムＩＤ
     */
    private String kshnpgmid;
    /**
     * Database Column Remarks: 更新プログラムＩＤ
     */
    private String shnapkihoncd;
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
     * Database Column Remarks: 色
     */
    private String iro;
    /**
     * Database Column Remarks: サイズ名
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
     * Database Column Remarks: 仕入単位
     */
    private String siiretani;
    /**
     * Database Column Remarks: 在庫管理対象フラグ
     */
    private String zaikokanritrgtflg;
    /**
     * Database Column Remarks: 税設定区分
     */
    private String taxsetteikbn;
    /**
     * Database Column Remarks: 在庫アラーム数量
     */
    private Integer zaikoaramusuryo;
    /**
     * Database Column Remarks: 取扱区分
     */
    private String trtskikbn;
    /**
     * Database Column Remarks: 適用開始日
     */
    private String tekiyostrtdd;
    /**
     * Database Column Remarks: 適用終了日
     */
    private String tekiyoshrydd;
    /**
     * Database Column Remarks: 配達価格
     */
    private BigDecimal dlvryprice;
    /**
     * Database Column Remarks: 持ち帰り価格
     */
    private BigDecimal mochikaeriprice;
    /**
     * Database Column Remarks: 総数
     */
    private BigDecimal sokazu;
    /**
     * Database Column Remarks: 仕入総数
     */
    private BigDecimal siiresokazu;
    /**
     * Database Column Remarks: 計上区分名称
     */
    private String keijokbnnm;
    /**
     * 検索条件: rownum
     */
    private Integer rownum;
    /**
     * シリアルバージョン
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * S06T002T
     * 
     * @mbg.generated
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", edano=").append(edano);
        sb.append(", lctgrycd=").append(lctgrycd);
        sb.append(", mctgrycd=").append(mctgrycd);
        sb.append(", sctgrycd=").append(sctgrycd);
        sb.append(", shncd=").append(shncd);
        sb.append(", keijokbn=").append(keijokbn);
        sb.append(", suryo=").append(suryo);
        sb.append(", hyojununt=").append(hyojununt);
        sb.append(", unt=").append(unt);
        sb.append(", kin=").append(kin);
        sb.append(", untshohitaxkin=").append(untshohitaxkin);
        sb.append(", shohitaxkin=").append(shohitaxkin);
        sb.append(", sankoushohitaxkin=").append(sankoushohitaxkin);
        sb.append(", tokuisakikbn=").append(tokuisakikbn);
        sb.append(", jutakudlvrycstkbn=").append(jutakudlvrycstkbn);
        sb.append(", royaltyshimeymd=").append(royaltyshimeymd);
        sb.append(", taxkbn=").append(taxkbn);
        sb.append(", meisaikbn=").append(meisaikbn);
        sb.append(", nyuryokumeisaiflg=").append(nyuryokumeisaiflg);
        sb.append(", tanpinmeisaiflg=").append(tanpinmeisaiflg);
        sb.append(", meisaibiko=").append(meisaibiko);
        sb.append(", stshncd=").append(stshncd);
        sb.append(", katasiki=").append(katasiki);
        sb.append(", siiresuryo=").append(siiresuryo);
        sb.append(", siirekin=").append(siirekin);
        sb.append(", siireunt=").append(siireunt);
        sb.append(", siireshohitaxkin=").append(siireshohitaxkin);
        sb.append(", zaikokshnsumiflg=").append(zaikokshnsumiflg);
        sb.append(", yukokbn=").append(yukokbn);
        sb.append(", toshaapkihoncd=").append(toshaapkihoncd);
        sb.append(", toshashopcd=").append(toshashopcd);
        sb.append(", toshacd=").append(toshacd);
        sb.append(", inptsystmymd=").append(inptsystmymd);
        sb.append(", inptsystmjikan=").append(inptsystmjikan);
        sb.append(", inptunyoymd=").append(inptunyoymd);
        sb.append(", inptipadrse=").append(inptipadrse);
        sb.append(", inptpgmid=").append(inptpgmid);
        sb.append(", koshaapkihoncd=").append(koshaapkihoncd);
        sb.append(", koshashopcd=").append(koshashopcd);
        sb.append(", koshacd=").append(koshacd);
        sb.append(", kshnsystmymd=").append(kshnsystmymd);
        sb.append(", kshnsystmjikan=").append(kshnsystmjikan);
        sb.append(", kshnunyoymd=").append(kshnunyoymd);
        sb.append(", kshnipadrse=").append(kshnipadrse);
        sb.append(", kshnpgmid=").append(kshnpgmid);
        sb.append("]");
        return sb.toString();
    }
}
