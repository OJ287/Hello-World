package jp.co.aquaclara.accis.web.domain.dto.a0301;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A0301w02_商品マスタ(登録画面)初期表示DTO.
 * 
 * @author accisdev009
 *
 */
@Data
@AllArgsConstructor
public class A0301W02DisplayDto {

    public A0301W02DisplayDto() {
        super();
    }

    // ＡＰ基本コード
    private String apkihoncd;

    // ショップコード
    private String shopcd;

    // セット区分
    private String stkbn;

    // 商品コード
    private String shncd;

    // 商品名称
    private String shnnm;

    // 商品ＡＰ基本コード
    private String shnapkihoncd;

    // 取扱区分名称
    private String trtskikbnnm;

    // 商品属性
    private String shnzokusei;

    // 商品属性名称
    private String shnzokuseinm;

    // 価格区分
    private String pricekbn;

    // 価格区分名称
    private String pricekbnnm;

    // 大分類コード
    private String lctgrycd;

    // 中分類コード
    private String mctgrycd;

    // 小分類コード
    private String sctgrycd;

    // 大分類コード名称
    private String lctgrynm;

    // 中分類コード名称
    private String mctgrynm;

    // 小分類コード名称
    private String sctgrynm;

    // 新商品名称
    private String shinshnnm;

    // 新商品名称カナ
    private String shinshnnmkana;

    // 新商品名称略称
    private String shinshnnmryakusho;

    // 新商品名称切替日
    private String shnnmkirikaedd;

    // 現商品名称
    private String genzshnnm;

    // 現商品名称カナ
    private String genzshnnmkana;

    // 現商品名称略称
    private String genzshnnmryakusho;

    // 新標準価格
    private String shinhyojunprice;

    // 新持ち帰り価格
    private String shinmochikaeriprice;

    // 新価格区分
    private String shinpricekbn;

    // 標準価格切替日
    private String hyojunpricekirikaedd;

    // 現標準価格
    private String genzhyojunprice;

    // 現持ち帰り価格
    private String genzmochikaeriprice;

    // 配達標準価格
    private String hyojundlvryprice;

    // 持ち帰り標準価格
    private String hyojunmochikaeriprice;

    // 現価格区分
    private String genzpricekbn;

    // 商品ポイント切替日
    private String shnpointkirikaedd;

    // 新商品ポイント発生区分
    private String shinshnpointhasseikbn;

    // 現商品ポイント発生区分
    private String genshnpointhasseikbn;

    // 新還元率値
    private String shinshnpointsu1;

    // 新固定値
    private String shinshnpointsu2;

    // 現還元率値
    private String genshnpointsu1;

    // 現固定値
    private String genshnpointsu2;

    // サイズ
    private String sz;

    // 色
    private String iro;

    // 取扱区分
    private String trtskikbn;

    // 数量端数管理区分
    private String suryohasukanrikbn;

    // 商品区分
    private String shnkbn;

    // 備考（基本情報）
    private String biko1;

    // 基本商品有効区分
    private String kihonshnyukokbn;

    // 基本商品有効区分：有効
    private String kihonshnyukokbn1;

    // 基本商品有効区分：無効
    private String kihonshnyukokbn2;

    // 基本商品登録システム日付
    private String kihonshninptsystmymd;

    // 基本商品登録システム時間
    private String kihonshninptsystmjikan;

    // 基本商品登録者ＡＰ基本コード
    private String kihonshntoshaapkihoncd;

    // 基本商品登録者ショップコード
    private String kihonshntoshashopcd;

    // 基本商品登録者コード
    private String kihonshntoshacd;

    // 基本商品登録者名
    private String kihonshntoshanm;

    // 基本商品更新システム日付
    private String kihonshnkshnsystmymd;

    // 基本商品更新システム時間
    private String kihonshnkshnsystmjikan;

    // 基本商品更新者ＡＰ基本コード
    private String kihonshnkoshaapkihoncd;

    // 基本商品更新者ショップコード
    private String kihonshnkoshashopcd;

    // 基本商品更新者名
    private String kihonshnkoshacd;

    // 基本商品更新者コード
    private String kihonshnkoshanm;

    // ショップ商品登録システム日付
    private String shopinptsystmymd;

    // ショップ商品登録システム時間
    private String shopinptsystmjikan;

    // ショップ商品登録者ＡＰ基本コード
    private String shoptoshaapkihoncd;

    // ショップ商品登録者ショップコード
    private String shoptoshashopcd;

    // ショップ商品登録者コード
    private String shoptoshacd;

    // ショップ商品登録者名
    private String shoptoshanm;

    // ショップ商品更新システム日付
    private String shopkshnsystmymd;

    // ショップ商品更新システム時間
    private String shopkshnsystmjikan;

    // ショップ商品更新者ＡＰ基本コード
    private String shopkoshaapkihoncd;

    // ショップ商品更新者ショップコード
    private String shopkoshashopcd;

    // ショップ商品更新者名
    private String shopkoshacd;

    // ショップ商品更新者コード
    private String shopkoshanm;

    // 販売開始日
    private String hanbaistrtdd;

    // 販売終了日
    private String hanbaishrydd;

    // 外部連携商品コード
    private String gaiburenkeishncd;

    // 本部販売管理商品コード
    private String honbuhanbaikanrishncd;

    // マージン対象区分
    private String mgntrgtkbn;

    // モバイル利用有無
    private String mobileriyoumu;

    // 税設定区分
    private String taxsetteikbn;

    // 税区分
    private String taxkbn;

    // 仕入取扱単位
    private String siiretrtskitani;

    // 仕入入数
    private String siireirisu;

    // 仕入単位
    private String siiretani;

    // 仕入単位名称
    private String siiretaninm;

    // 仕入単価
    private String siireunt;

    // 発注ロット数
    private String hacchurottokazu;

    // 売上取扱単位
    private String uritrtskitani;

    // 売上入数
    private String uriirisu;

    // 売上単位
    private String uritani;

    // 新オープン価格
    private String shinopnprice;

    // 新オープン持ち帰り価格
    private String shinopnmochikaeriprice;

    // オープン価格切替日
    private String opnpricekirikaedd;

    // 現オープン価格
    private String genzopnprice;

    // 現オープン持ち帰り価格
    private String genzopnmochikaeriprice;

    // まとめ買いポイント切替日
    private String mtmkipointkirikaedd;

    // 新まとめ買いポイント区分
    private String shinmtmkipointkbn;

    // 現まとめ買いポイント区分
    private String genmtmkipointkbn;

    // 新まとめ買い数量
    private String shinmtmkisuryo;

    // 現まとめ買い数量
    private String genmtmkisuryo;

    // 新まとめ買いポイント数
    private String shinmtmkipointsu;

    // 現まとめ買いポイント数
    private String genmtmkipointsu;

    // 新数量閾値1
    private String shinmtmkidankaisuryo1;

    // 現数量閾値1
    private String genmtmkidankaisuryo1;

    // 新発生1
    private String shinmtmkidankaipointsu1;

    // 現発生1
    private String genmtmkidankaipointsu1;

    // 新数量閾値2
    private String shinmtmkidankaisuryo2;

    // 現数量閾値2
    private String genmtmkidankaisuryo2;

    // 新発生2
    private String shinmtmkidankaipointsu2;

    // 現発生2
    private String genmtmkidankaipointsu2;

    // 新数量閾値3
    private String shinmtmkidankaisuryo3;

    // 現数量閾値3
    private String genmtmkidankaisuryo3;

    // 新発生3
    private String shinmtmkidankaipointsu3;

    // 現発生3
    private String genmtmkidankaipointsu3;

    // 新数量閾値4
    private String shinmtmkidankaisuryo4;

    // 現数量閾値4
    private String genmtmkidankaisuryo4;

    // 新発生4
    private String shinmtmkidankaipointsu4;

    // 現発生4
    private String genmtmkidankaipointsu4;

    // 新数量閾値5
    private String shinmtmkidankaisuryo5;

    // 現数量閾値5
    private String genmtmkidankaisuryo5;

    // 新発生5
    private String shinmtmkidankaipointsu5;

    // 現発生5
    private String genmtmkidankaipointsu5;

    // 販促区分
    private String hansokukbn;

    // 販促開始日
    private String hansokustrtdd;

    // 販促終了日
    private String hansokushrydd;

    // 販促名称
    private String hansokunm;

    // 販促配達価格
    private String hansokudlvryprice;

    // 販促持ち帰り価格
    private String hansokumochikaeriprice;

    // 販促優先区分
    private String hansokuyusenkbn;

    // 在庫管理対象フラグ
    private String zaikokanritrgtflg;

    // 原価計算区分
    private String genkakeisankbn;

    // 在庫単価
    private String zaikount;

    // 仕入単位変換区分
    private String siiretanihenkankbn;

    // 在庫アラーム数量
    private String zaikoaramusuryo;

    // ショップ備考
    private String shopbiko;

    // ショップ有効区分
    private String shopyukokbn;
}
