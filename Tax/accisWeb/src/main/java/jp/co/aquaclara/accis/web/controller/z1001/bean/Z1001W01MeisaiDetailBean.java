package jp.co.aquaclara.accis.web.controller.z1001.bean;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Z1001W01 売上照会Beanクラス(明細部分).
 */
@Data
@AllArgsConstructor
public class Z1001W01MeisaiDetailBean implements Serializable {

    public Z1001W01MeisaiDetailBean() {
        super();
    }

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** 商品CD */
    private String shncd;
    /** 商品略称名 */
    private String shnnmryakusho;
    /** 色 */
    private String iro;
    /** サイズ */
    private String sz;
    /** 型式 */
    private String katasikinm;
    /** 売上計上区分 */
    private String keijokbnnm;
    /** 売上数量 */
    private String suryo1;
    /** 売上取扱単位 */
    private String trtskitani11;
    /** 売上入数 */
    private String irisu1;
    /** 売上入数単位 */
    private String irisutani1;
    /** 総数１ */
    private String sokazu1;
    /** 単価１ */
    private String unt1;
    /** 金額１ */
    private String kin1;
    /** 消費税金額１ */
    private String shohitaxkinn1;
    /** 仕入数量 */
    private String suryo2;
    /** 仕入取扱単位 */
    private String trtskitani2;
    /** 仕入入数 */
    private String irisu2;
    /** 仕入入数単位 */
    private String irisutani2;
    /** 総数２ */
    private String sokazu2;
    /** 単価２ */
    private String unt2;
    /** 金額２ */
    private String kin2;
    /** 消費税金額２ */
    private String shohitaxkinn2;
    /** 備考 */
    private String meisaibiko;
    /** 登録システム日付 */
    private String inptsystmymd;
    /** 登録システム時間 */
    private String inptsystmjikan;
    /** 登録者ＡＰ基本コード */
    private String toshaapkihoncd;
    /** 登録者ショップコード */
    private String toshashopcd;
    /** 登録者コード */
    private String toshacd;
    /** 登録者名 */
    private String toshamei;
    /** 更新システム日付 */
    private String kshnsystmymd;
    /** 更新システム時間 */
    private String kshnsystmjikan;
    /** 更新者ＡＰ基本コード */
    private String koshaapkihoncd;
    /** 更新者ショップコード */
    private String koshashopcd;
    /** 更新者コード */
    private String koshacd;
    /** 更新者名 */
    private String koshamei;
    /** 更新対象フラグ 隠しフィールド */
    private String kshntrgtflg;
    /** 売上伝票明細番号 隠しフィールド */
    private String uridnpymeisaino;
    /** 売上伝票明細枝番号 隠しフィールド */
    private String uridnpymeisaiedano;
    /** データ作成元区分 隠しフィールド */
    private String datsksmotokbn;
    /** 大分類コード 隠しフィールド */
    private String lctgrycd;
    /** 中分類コード 隠しフィールド */
    private String mctgrycd;
    /** 小分類コード 隠しフィールド */
    private String sctgrycd;
    /** 価格区分 隠しフィールド */
    private String pricekbn;
    /** 標準価格切替日 隠しフィールド */
    private String hyojunpricekirikaedd;
    /** 現標準価格 隠しフィールド */
    private String genzhyojunprice;
    /** 現標準持ち帰り価格 隠しフィールド */
    private String genzhyojunmochikaeriprice;
    /** 新標準価格 隠しフィールド */
    private String shinhyojunprice;
    /** 新標準持ち帰り価格 隠しフィールド */
    private String shinhyojunmochikaeriprice;
    /** ショップ価格切替日 隠しフィールド */
    private String shoppricekirikaedd;
    /** 現ショップ価格 隠しフィールド */
    private String genzshopprice;
    /** 現ショップ持ち帰り価格 隠しフィールド */
    private String genzshopmochikaeriprice;
    /** 新ショップ価格 隠しフィールド */
    private String shinshopprice;
    /** 新ショップ持ち帰り価格 隠しフィールド */
    private String shinshopmochikaeriprice;
    /** 販促開始日 隠しフィールド */
    private String hansokustrtdd;
    /** 販促終了日 隠しフィールド */
    private String hansokushrydd;
    /** 販促価格 隠しフィールド */
    private String hansokuprice;
    /** 販促持ち帰り価格 隠しフィールド */
    private String hansokumochikaeriprice;
    /** 得意先価格開始日 隠しフィールド */
    private String ttpricestrtdd;
    /** 得意先価格終了日 隠しフィールド */
    private String ttpriceshrydd;
    /** 得意先価格 隠しフィールド */
    private String ttprice;
    /** 得意先持ち帰り価格 隠しフィールド */
    private String ttmochikaeriprice;
    /** 単価消費税金額 隠しフィールド */
    /** 消費税金額 隠しフィールド */
    private String shohitaxkin;
    /** 初期表示価格 隠しフィールド */
    private String shokidsplyprice;
    /** 在庫単価 隠しフィールド */
    private String zaikount;
    /** 商品税区分 隠しフィールド */
    private String shntaxkbn;

}
