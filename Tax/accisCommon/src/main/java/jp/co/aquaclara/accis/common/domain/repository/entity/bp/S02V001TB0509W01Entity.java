package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * Database Table Remarks: 商品マスタビュー
 *
 * This class corresponds to the database table S02V001T
 */
@Data
public class S02V001TB0509W01Entity implements Serializable {
    /**
     * Database Column Remarks: ＡＰ基本コード
     *
     */
    private String apkihoncd;

    /**
     * Database Column Remarks: ショップコード
     *
     */
    private String shopcd;

    /**
     * Database Column Remarks: 商品コード
     *
     */
    private String shncd;

    /**
     * Database Column Remarks: 商品属性
     *
     */
    private String shnzokusei;

    /**
     * Database Column Remarks: セット区分
     *
     */
    private String stkbn;

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
     * Database Column Remarks: 大分類名称
     *
     */
    private String lctgrynm;

    /**
     * Database Column Remarks: 中分類名称
     *
     */
    private String mctgrynm;

    /**
     * Database Column Remarks: 小分類名称
     *
     */
    private String sctgrynm;

    /**
     * Database Column Remarks: 商品名称切替日
     *
     */
    private String shnnmkirikaedd;

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
     * Database Column Remarks: 現商品名称カナ
     *
     */
    private String genzshnnmkana;

    /**
     * Database Column Remarks: 新商品名称カナ
     *
     */
    private String shinshnnmkana;

    /**
     * Database Column Remarks: 現商品名称略称
     *
     */
    private String genzshnnmryakusho;

    /**
     * Database Column Remarks: 新商品名称略称
     *
     */
    private String shinshnnmryakusho;

    /**
     * Database Column Remarks: ショップ価格切替日
     *
     */
    private String shoppricekirikaedd;

    /**
     * Database Column Remarks: 現ショップ価格
     *
     */
    private String genzshopprice;

    /**
     * Database Column Remarks: 新ショップ価格
     *
     */
    private String shinshopprice;

    /**
     * Database Column Remarks: 現ショップ持ち帰り価格
     *
     */
    private String genzshopmochikaeriprice;

    /**
     * Database Column Remarks: 新ショップ持ち帰り価格
     *
     */
    private String shinshopmochikaeriprice;

    /**
     * Database Column Remarks: 色
     *
     */
    private String iro;

    /**
     * Database Column Remarks: サイズ
     *
     */
    private String ｓｚ;

    /**
     * Database Column Remarks: サイズ名称
     *
     */
    private String ｓｚｎｍ;

    /**
     * Database Column Remarks: 取扱区分
     *
     */
    private String trtskikbn;

    /**
     * Database Column Remarks: 数量端数管理区分
     *
     */
    private String suryohasukanrikbn;

    /**
     * Database Column Remarks: 販売開始日
     *
     */
    private String hanbaistrtdd;

    /**
     * Database Column Remarks: 販売終了日
     *
     */
    private String hanbaishrydd;

    /**
     * Database Column Remarks: 外部連携商品コード
     *
     */
    private String gaiburenkeishncd;

    /**
     * Database Column Remarks: 本部販売管理商品コード
     *
     */
    private String honbuhanbaikanrishncd;

    /**
     * Database Column Remarks: マージン対象区分
     *
     */
    private String mgntrgtkbn;

    /**
     * Database Column Remarks: 商品区分
     *
     */
    private String shnkbn;

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
     * Database Column Remarks: 仕入取扱単位
     *
     */
    private String siiretrtskitani;

    /**
     * Database Column Remarks: 仕入取扱単位名称
     *
     */
    private String siiretrtskitaninm;

    /**
     * Database Column Remarks: 仕入入数
     *
     */
    private String siireirisu;

    /**
     * Database Column Remarks: 仕入単位
     *
     */
    private String siiretani;

    /**
     * Database Column Remarks: 仕入単位名称
     *
     */
    private String siiretaninm;

    /**
     * Database Column Remarks: 仕入単価
     *
     */
    private String siireunt;

    /**
     * Database Column Remarks: 発注ロット数
     *
     */
    private String hacchurottokazu;

    /**
     * Database Column Remarks: 売上取扱単位
     *
     */
    private String uritrtskitani;

    /**
     * Database Column Remarks: 売上取扱単位名称
     *
     */
    private String uritrtskitaninm;

    /**
     * Database Column Remarks: 売上入数
     *
     */
    private String uriirisu;

    /**
     * Database Column Remarks: 売上単位
     *
     */
    private String uritani;

    /**
     * Database Column Remarks: 売上単位名称
     *
     */
    private String uritaninm;

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
     * Database Column Remarks: 販促名称
     *
     */
    private String hansokunm;

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
     * Database Column Remarks: 販促優先区分
     *
     */
    private String hansokuyusenkbn;

    /**
     * Database Column Remarks: 在庫管理対象フラグ
     *
     */
    private String zaikokanritrgtflg;

    /**
     * Database Column Remarks: 原価計算区分
     *
     */
    private String genkakeisankbn;

    /**
     * Database Column Remarks: 標準原価
     *
     */
    private String hyojungenka;

    /**
     * Database Column Remarks: 在庫単価
     *
     */
    private String zaikount;

    /**
     * Database Column Remarks: 仕入単位変換区分
     *
     */
    private String siiretanihenkankbn;

    /**
     * Database Column Remarks: 在庫アラーム数量
     *
     */
    private String zaikoaramusuryo;

    /**
     * Database Column Remarks: 商品ＡＰ基本コード
     *
     */
    private String shnapkihoncd;

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
     * Database Column Remarks: 現価格区分
     *
     */
    private String genzpricekbn;

    /**
     * Database Column Remarks: 新価格区分
     *
     */
    private String shinpricekbn;

    /**
     * Database Column Remarks: 基本商品備考
     *
     */
    private String kihonshnbiko;

    /**
     * Database Column Remarks: 基本商品有効区分
     *
     */
    private String kihonshnyukokbn;

    /**
     * Database Column Remarks: 基本商品登録者ＡＰ基本コード
     *
     */
    private String kihonshntoshaapkihoncd;

    /**
     * Database Column Remarks: 基本商品登録者ショップコード
     *
     */
    private String kihonshntoshashopcd;

    /**
     * Database Column Remarks: 基本商品登録者コード
     *
     */
    private String kihonshntoshacd;

    /**
     * Database Column Remarks: 基本商品登録システム日付
     *
     */
    private String kihonshninptsystmymd;

    /**
     * Database Column Remarks: 基本商品登録システム時間
     *
     */
    private String kihonshninptsystmjikan;

    /**
     * Database Column Remarks: 基本商品更新者ＡＰ基本コード
     *
     */
    private String kihonshnkoshaapkihoncd;

    /**
     * Database Column Remarks: 基本商品更新者ショップコード
     *
     */
    private String kihonshnkoshashopcd;

    /**
     * Database Column Remarks: 基本商品更新者コード
     *
     */
    private String kihonshnkoshacd;

    /**
     * Database Column Remarks: 基本商品更新システム日付
     *
     */
    private String kihonshnkshnsystmymd;

    /**
     * Database Column Remarks: 基本商品更新システム時間
     *
     */
    private String kihonshnkshnsystmjikan;

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
     * Database Column Remarks: ショップ備考
     *
     */
    private String shopbiko;

    /**
     * Database Column Remarks: ショップ有効区分
     *
     */
    private String shopyukokbn;

    /**
     * Database Column Remarks: ショップ登録者ＡＰ基本コード
     *
     */
    private String shoptoshaapkihoncd;

    /**
     * Database Column Remarks: ショップ登録者ショップコード
     *
     */
    private String shoptoshashopcd;

    /**
     * Database Column Remarks: ショップ登録者コード
     *
     */
    private String shoptoshacd;

    /**
     * Database Column Remarks: ショップ登録システム日付
     *
     */
    private String shopinptsystmymd;

    /**
     * Database Column Remarks: ショップ登録システム時間
     *
     */
    private String shopinptsystmjikan;

    /**
     * Database Column Remarks: ショップ更新者ＡＰ基本コード
     *
     */
    private String shopkoshaapkihoncd;

    /**
     * Database Column Remarks: ショップ更新者ショップコード
     *
     */
    private String shopkoshashopcd;

    /**
     * Database Column Remarks: ショップ更新者コード
     *
     */
    private String shopkoshacd;

    /**
     * Database Column Remarks: ショップ更新システム日付
     *
     */
    private String shopkshnsystmymd;

    /**
     * Database Column Remarks: ショップ更新システム時間
     *
     */
    private String shopkshnsystmjikan;

    /**
     * Database Column Remarks: 会員サイト連携区分
     *
     */
    private String kaiinsaitorenkeikbn;

    /**
     * Database Column Remarks: モバイル利用有無
     *
     */
    private String mobileriyoumu;

    /**
     * Database Column Remarks: 登録者ＡＰ基本コード
     *
     */
    private String toshaapkihoncd;

    /**
     * Database Column Remarks: 登録者ショップコード
     *
     */
    private String toshashopcd;

    /**
     * Database Column Remarks: 登録者コード
     *
     */
    private String toshacd;

    /**
     * Database Column Remarks: 登録システム日付
     *
     */
    private String inptsystmymd;

    /**
     * Database Column Remarks: 登録システム時間
     *
     */
    private String inptsystmjikan;

    /**
     * Database Column Remarks: 登録運用日付
     *
     */
    private String inptunyoymd;

    /**
     * Database Column Remarks: 登録ＩＰアドレス
     *
     */
    private String inptipadrse;

    /**
     * Database Column Remarks: 登録プログラムＩＤ
     *
     */
    private String inptpgmid;

    /**
     * Database Column Remarks: 更新者ＡＰ基本コード
     *
     */
    private String koshaapkihoncd;

    /**
     * Database Column Remarks: 更新者ショップコード
     *
     */
    private String koshashopcd;

    /**
     * Database Column Remarks: 更新者コード
     *
     */
    private String koshacd;

    /**
     * Database Column Remarks: 更新システム日付
     *
     */
    private String kshnsystmymd;

    /**
     * Database Column Remarks: 更新システム時間
     *
     */
    private String kshnsystmjikan;

    /**
     * Database Column Remarks: 更新運用日付
     *
     */
    private String kshnunyoymd;

    /**
     * Database Column Remarks: 更新ＩＰアドレス
     *
     */
    private String kshnipadrse;

    /**
     * Database Column Remarks: 更新プログラムＩＤ
     *
     */
    private String kshnpgmid;

    /**
     * Database Column Remarks: 有効区分名
     *
     */
    private String yukokbnnm;

    /**
     * Database Column Remarks: セット区分名
     *
     */
    private String stkbnmei;

    /**
     * Database Column Remarks: 現価格区分名
     *
     */
    private String genzpricekbnmei;

    /**
     * Database Column Remarks: 新価格区分名
     *
     */
    private String shinpricekbnmei;

    /**
     * 検索条件: 商品名称
     *
     */
    private String shnnm;

    /**
     * 検索条件: セット区分
     *
     */
    private List<String> stkbnList;

    /**
     * 検索条件: 運用日付
     *
     */
    private String unyoYmd;

    /**
     * 検索条件: 価格区分
     *
     */
    private List<String> pricekbnList;

    /**
     * 検索条件: 販売開始日 開始
     *
     */
    private String hanbaistrtddstr;

    /**
     * 検索条件: 販売開始日 終了
     *
     */
    private String hanbaistrtddend;

    /**
     * 検索条件: 販売終了日 開始
     *
     */
    private String hanbaishryddstr;

    /**
     * 検索条件: 販売終了日 終了
     *
     */
    private String hanbaishryddend;

    /**
     * 検索条件: 有効区分 有効
     *
     */
    private String yukokbn1;

    /**
     * 検索条件: 有効区分 無効
     *
     */
    private String yukokbn2;

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
        sb.append("Hash = ").append(hashCode());
        sb.append(", apkihoncd=").append(apkihoncd);
        sb.append(", shopcd=").append(shopcd);
        sb.append(", shncd=").append(shncd);
        sb.append(", shnnm=").append(shnnm);
        sb.append(", lctgrycd=").append(lctgrycd);
        sb.append(", mctgrycd=").append(mctgrycd);
        sb.append(", sctgrycd=").append(sctgrycd);
        sb.append(", hanbaistrtddstr=").append(hanbaistrtddstr);
        sb.append(", hanbaistrtddend=").append(hanbaistrtddend);
        sb.append(", hanbaishryddstr=").append(hanbaishryddstr);
        sb.append(", hanbaishryddend=").append(hanbaishryddend);
        sb.append("]");
        return sb.toString();
    }
}
