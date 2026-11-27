package jp.co.aquaclara.accis.web.controller.a0301.bean;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import jp.co.aquaclara.accis.common.constants.ValidationConstants;
import jp.co.aquaclara.accis.web.annotation.ByteSize;
import jp.co.aquaclara.accis.web.annotation.ValidationMessage;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品マスタ(検索画面)-ショップ情報タブBeanクラス.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class A0301W02ShopTabBean
    implements Serializable, InsertedTimestampSetter, UpdatedTimestampSetter {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** 取り扱い期間(開始) */
    @NotBlank
    @ValidationMessage(validation = NotBlank.class, messageId = MessageID.W011, args = {"販売期間（開始）"})
    @Pattern(regexp = ValidationConstants.REGEXP_YYYY_MM_DD_SLASH)
    @ValidationMessage(validation = Pattern.class, messageId = MessageID.W016, args = {"販売期間（開始）"})
    private String hanbaidkikanstr;

    /** 取り扱い期間(終了) */
    @NotBlank
    @ValidationMessage(validation = NotBlank.class, messageId = MessageID.W011, args = {"販売期間（終了）"})
    @Pattern(regexp = ValidationConstants.REGEXP_YYYY_MM_DD_SLASH)
    @ValidationMessage(validation = Pattern.class, messageId = MessageID.W016, args = {"販売期間（終了）"})
    private String hanbaidkikanend;

    /** 外部連携商品コード */
    @Pattern(regexp = ValidationConstants.REGEXP_HALF_WIDTH_ALPHABET_NUMBERS_SYMBOLS)
    @ValidationMessage(validation = Pattern.class, messageId = MessageID.W013,
        args = {"外部連携商品コード", "英数字記号（'\"*%|以外）のみ"})
    private String gaiburenkeishncd;

    /** 本部販売管理商品コード */
    @Pattern(regexp = ValidationConstants.REGEXP_HALF_WIDTH_ALPHABET_NUMBERS_SYMBOLS)
    @ValidationMessage(validation = Pattern.class, messageId = MessageID.W013,
        args = {"本部販売管理商品コード", "英数字記号（'\"*%|以外）のみ"})
    private String honbuhanbaikanrishncd;

    /** マージン対象区分 */
    private String mgntrgtkbn;

    /** 商品区分 */
    private String shnkbn;

    /** モバイル利用 */
    private String mobileriyoumu;

    /** 税設定区分 */
    private String taxsetteikbn;

    /** 税区分 */
    @NotBlank
    @ValidationMessage(validation = NotBlank.class, messageId = MessageID.W011, args = {"税区分"})
    private String taxkbn;

    /** 仕入情報 - 取扱単位 */
    private String siiretrtskitani;

    /** 仕入情報 - 入数 */
    private String siireirisu;

    /** 仕入情報 - 入数単位 */
    private String irisutani1;

    /** 仕入情報 - 仕入単価 */
    private String siireunt;

    /** 仕入情報 - 発注ロット数 */
    private String hacchurottokazu;

    /** 売上・取扱情報 - 取扱単位 */
    private String uritrtskitani;

    /** 売上・取扱情報 - 入数 */
    private String uriirisu;

    /** 売上・取扱情報 - 入数単位 */
    private String irisutani2;

    /** オープン価格 - 配達(新) */
    private String shindlvryprice1;

    /** オープン価格 - 切替日 */
    @Pattern(regexp = ValidationConstants.REGEXP_YYYY_MM_DD_SLASH)
    @ValidationMessage(validation = Pattern.class, messageId = MessageID.W016, args = {"オープン価格切替日"})
    private String opnpricekirikaedd;

    /** オープン価格 - 切替日（初期値） */
    private String initopnpricekirikaedd;

    /** オープン価格 - 配達(現) */
    private String gendlvryprice2;

    /** オープン価格 - 持ち帰り(新) */
    private String shinmochikaeriprice2;

    /** オープン価格 - 持ち帰り(現) */
    private String genzmochikaeriprice;

    /** オープン価格 - 標準配達 */
    private String hyojundlvryprice;

    /** オープン価格 - 標準持ち帰り */
    private String hyojunmochikaeriprice;

    /** ポイント発生区分(新) */
    private String shinshnpointhasseikbn;

    /** ポイント発生区分(現) */
    private String genshnpointhasseikbn;

    /** 還元率値(新) */
    private String shinshnpointsu1;

    /** 固定値(新) */
    private String shinshnpointsu2;

    /** 還元率値(現) */
    private String genshnpointsu1;

    /** 固定値(現) */
    private String genshnpointsu2;

    /** ポイント発生切替日 */
    @Pattern(regexp = ValidationConstants.REGEXP_YYYY_MM_DD_SLASH)
    @ValidationMessage(validation = Pattern.class, messageId = MessageID.W016, args = {"ポイント発生切替日"})
    private String shnpointkirikaedd;

    /** 新ポイント発生切替日（初期値） */
    private String initshnpointkirikaedd;

    /** まとめ買いポイント発生 - まとめ買いポイント発生区分(新) */
    private String shinmtmkipointkbn;

    /** まとめ買いポイント発生 - まとめ買いポイント発生区分(現) */
    private String genmtmkipointkbn;

    /** まとめ買いポイント発生 - 数量(新) */
    private String shinmtmkisuryo;

    /** まとめ買いポイント発生 - 数量(現) */
    private String genmtmkisuryo;

    /** まとめ買いポイント発生 - 発生ポイント数(新) */
    private String shinmtmkipointsu;

    /** まとめ買いポイント発生 - 発生ポイント数(現) */
    private String genmtmkipointsu;

    /** まとめ買いポイント発生 - 数量閾値1(新) */
    private String shinmtmkidankaisuryo1;

    /** まとめ買いポイント発生 - 数量閾値1(現) */
    private String genmtmkidankaisuryo1;

    /** まとめ買いポイント発生 - 発生1(新) */
    private String shinmtmkidankaipointsu1;

    /** まとめ買いポイント発生 - 発生1(現) */
    private String genmtmkidankaipointsu1;

    /** まとめ買いポイント発生 - 数量閾値2(新) */
    private String shinmtmkidankaisuryo2;

    /** まとめ買いポイント発生 - 数量閾値2(現) */
    private String genmtmkidankaisuryo2;

    /** まとめ買いポイント発生 - 発生2(新) */
    private String shinmtmkidankaipointsu2;

    /** まとめ買いポイント発生 - 発生2(現) */
    private String genmtmkidankaipointsu2;

    /** まとめ買いポイント発生 - 数量閾値3(新) */
    private String shinmtmkidankaisuryo3;

    /** まとめ買いポイント発生 - 数量閾値3(現) */
    private String genmtmkidankaisuryo3;

    /** まとめ買いポイント発生 - 発生3(新) */
    private String shinmtmkidankaipointsu3;

    /** まとめ買いポイント発生 - 発生3(現) */
    private String genmtmkidankaipointsu3;

    /** まとめ買いポイント発生 - 数量閾値4(新) */
    private String shinmtmkidankaisuryo4;

    /** まとめ買いポイント発生 - 数量閾値4(現) */
    private String genmtmkidankaisuryo4;

    /** まとめ買いポイント発生 - 発生4(新) */
    private String shinmtmkidankaipointsu4;

    /** まとめ買いポイント発生 - 発生4(現) */
    private String genmtmkidankaipointsu4;

    /** まとめ買いポイント発生 - 数量閾値5(新) */
    private String shinmtmkidankaisuryo5;

    /** まとめ買いポイント発生 - 数量閾値5(現) */
    private String genmtmkidankaisuryo5;

    /** まとめ買いポイント発生 - 発生5(新) */
    private String shinmtmkidankaipointsu5;

    /** まとめ買いポイント発生 - 発生5(現) */
    private String genmtmkidankaipointsu5;

    /** まとめ買いポイント発生 - 切替日 */
    @Pattern(regexp = ValidationConstants.REGEXP_YYYY_MM_DD_SLASH)
    @ValidationMessage(validation = Pattern.class, messageId = MessageID.W016, args = {"まとめ買いポイント発生切替日"})
    private String mtmkipointkirikaedd;

    /** まとめ買いポイント発生 - 切替日（初期値） */
    private String initmtmkipointkirikaedd;

    /** 販促設定 */
    private String hansokusettei;

    /** 販促設定-適用期間(開始) */
    @Pattern(regexp = ValidationConstants.REGEXP_YYYY_MM_DD_SLASH)
    @ValidationMessage(validation = Pattern.class, messageId = MessageID.W016, args = {"適用期間（開始）"})
    private String tekiyodkikanstr;

    /** 販促設定-適用期間(終了) */
    @Pattern(regexp = ValidationConstants.REGEXP_YYYY_MM_DD_SLASH)
    @ValidationMessage(validation = Pattern.class, messageId = MessageID.W016, args = {"適用期間（終了）"})
    private String tekiyodkikanend;

    /** 販促設定-名称 */
    @ByteSize(size = 40)
    @ValidationMessage(validation = ByteSize.class, messageId = MessageID.W009)
    private String hansokunm;

    /** 販促設定-配達価格 */
    private String dlvryprice;

    /** 販促設定-持ち帰り価格 */
    private String omchkrprice;

    /** 販促設定-優先表示 */
    private String hansokuyusenkbn;

    /** 在庫管理区分 */
    private String zaikokanrikbn;

    /** 原価計算区分 */
    private String genkakeisankbn;

    /** 在庫単価１ */
    private String zaikount1;

    /** 仕入単位変換区分 */
    private String siiretanihenkankbn;

    /** 仕入単位 */
    private String siiretani;

    /** 仕入単位名称 */
    private String siiretaninm;

    /** 在庫アラーム数量 */
    private String zaikoaramusuryo;

    /** 備考 */
    @ByteSize(size = 200)
    @ValidationMessage(validation = ByteSize.class, messageId = MessageID.W009)
    private String biko2;

    /** 有効区分 */
    private String yukokbn;

    /** 有効区分（変更前） */
    private String yukokbn0;

    /** 登録日 */
    private String shopinptsystmymd;

    /** 登録時間 */
    private String shopinptsystmjikan;

    /** 登録者AP基本コード */
    private String shoptoshaapkihoncd;

    /** 登録者ショップコード */
    private String shoptoshashopcd;

    /** 登録者コード */
    private String shoptoshacd;

    /** 登録者名 */
    private String shoptoshamei;

    /** 更新日 */
    private String shopkshnsystmymd;

    /** 更新時間 */
    private String shopkshnsystmjikan;

    /** 更新AP基本コード */
    private String shopkoshaapkihoncd;

    /** 更新ショップコード */
    private String shopkoshashopcd;

    /** 更新者コード */
    private String shopkoshacd;

    /** 更新者名 */
    private String shopkoshamei;

    /** 確認メッセージ */
    private String confirmmess;

    /** 商品ＡＰ基本コード */
    private String shnapkihoncd;

    /** 格納時間 */
    private String kakunojikan;

    /** 格納担当者 */
    private String kakunotnsha;

    /** 格納日付 */
    private String kakunoymd;

    /** 確認フラグ */
    private String kakninflg;

    /** エラーID */
    private String errids;

    @Override
    public void setUpdatedApkihoncd(String apkihoncd) {
        setShopkoshaapkihoncd(apkihoncd);
    }

    @Override
    public void setUpdatedShopcd(String shopcd) {
        setShopkoshashopcd(shopcd);
    }

    @Override
    public void setUpdatedTnshacd(String tnshacd) {
        setShopkoshacd(tnshacd);
    }

    @Override
    public void setUpdatedTnshamei(String tnshanm) {
        setShopkoshamei(tnshanm);
    }

    @Override
    public void setUpdatedSystmymd(String date) {
        setShopkshnsystmymd(date);
    }

    @Override
    public void setUpdatedSystmjikan(String time) {
        setShopkshnsystmjikan(time);
    }

    @Override
    public void setInsertedApkihoncd(String apkihoncd) {
        setShoptoshaapkihoncd(apkihoncd);
    }

    @Override
    public void setInsertedShopcd(String shopcd) {
        setShoptoshashopcd(shopcd);
    }

    @Override
    public void setInsertedTnshacd(String tnshacd) {
        setShoptoshacd(tnshacd);
    }

    @Override
    public void setInsertedTnshamei(String tnshanm) {
        setShoptoshamei(tnshanm);
    }

    @Override
    public void setInsertedSystmymd(String date) {
        setShopinptsystmymd(date);
    }

    @Override
    public void setInsertedSystmjikan(String time) {
        setShopinptsystmjikan(time);
    }

}
