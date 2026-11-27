package jp.co.aquaclara.accis.web.controller.c0815.bean;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * C0815W21_配達実績登録（個別入力）-一覧情報Beanクラス.
 */
@Data
@AllArgsConstructor
public class C0815W21MeisaiSummaryBean implements Serializable {

    public C0815W21MeisaiSummaryBean() {
        super();
    }

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** 商品コード明細 */
    private String shncdmei;
    /** 商品名称明細 */
    private String shnnmmei;
    /** 色名称明細 */
    private String ironmmei;
    /** サイズ名称明細 */
    private String sznmmei;
    /** 型式明細 */
    private String katasiki;
    /** 行追加フラグ */
    private String addgyoflg;
    /** 計上区分名称明細 */
    private String keijokbnnmmei;
    /** 計上区分 */
    private String keijokbn;
    /** 数量明細 */
    private String suryomei;
    /** 単位明細 */
    private String tanimei;
    /** 入数明細 */
    private String irisumei;
    /** 入数単位明細 */
    private String irisutanimei;
    /** 総数明細 */
    private String sokazumei;
    /** 標準価格明細 */
    private String hyojunpricemei;
    /** 販売価格明細 */
    private String hanbaipricemei;
    /** 金額明細 */
    private String kinmei;
    /** 明細備考番号 */
    private String meisaibikono;
    /** 明細備考明細 */
    private String meisaibikomei;
    /** 単価消費税金額 */
    private String untshohitaxkin;
    /** 消費税金額 */
    private String shohitaxkin;
    /** 商品税区分 */
    private String shntaxkbn;
    /** 標準価格切替日 */
    private String hyojunpricekirikaedd;
    /** 現標準価格 */
    private String genzhyojunprice;
    /** 新標準価格 */
    private String shinhyojunprice;
    /** 現標準持ち帰り価格 */
    private String genzhyojunmochikaeriprice;
    /** 新標準持ち帰り価格 */
    private String shinhyojunmochikaeriprice;
    /** ショップ価格切替日 */
    private String shoppricekirikaedd;
    /** 現ショップ価格 */
    private String genzshopprice;
    /** 新ショップ価格 */
    private String shinshopprice;
    /** 現ショップ持ち帰り価格 */
    private String genzshopmochikaeriprice;
    /** 新ショップ持ち帰り価格 */
    private String shinshopmochikaeriprice;
    /** 販促開始日 */
    private String hansokustrtdd;
    /** 販促終了日 */
    private String hansokushrydd;
    /** 販促価格 */
    private String hansokuprice;
    /** 販促持ち帰り価格 */
    private String hansokumochikaeriprice;
    /** 得意先価格開始日 */
    private String ttpricestrtdd;
    /** 得意先価格終了日 */
    private String ttpriceshrydd;
    /** 得意先価格 */
    private String ttprice;
    /** 得意先持ち帰り価格 */
    private String ttmochikaeriprice;
    /** 初期表示価格 */
    private String shokidsplyprice;
    /** 計上区分２ */
    private String keijokbn2;
    /** 価格区分 */
    private String pricekbn;
    /** 単品明細フラグ */
    private String tanpinmeisaiflg;
    /** 特別単価Json */
    private String tokubetsuUntJson;
    /** 受注明細番号 */
    private String acptdnpymeisaino;
    /** 受注明細枝番号 */
    private String acptdnpymeisaiedano;
    /** 枝番号 */
    private String edano;
    /** 配布物AP基本コード */
    private String haifubutsuapkihoncd;
    /** 配布物ショップコード */
    private String haifubutsushopcd;
    /** 配布物コード */
    private String haifubutsucd;
    /** 納品単価 */
    private String nohinunt;
    /** 商品大分類 */
    private String shnlctgrycd;
    /** 商品中分類 */
    private String shnmctgrycd;
    /** 税区分 */
    private String taxkbn;
    /** セット区分 */
    private String stkbn;
    /** 有償区分 */
    private String yusyokbn;
    /** 大分類コード */
    private String lctgrycd;
    /** 入力明細区分 */
    private String nyuryokumeisaiflg;
    /** 受注消費税 */
    private String acptshohitaxkin;
    /** 受注単価 */
    private String acptunt;
    /** 売上伝票 */
    private String uridnpyno;
    /** 売上伝票番号 */
    private String uridnpyedano;
    /** 売上伝票明細番号 */
    private String uridnpymeisaino;
    /** 売上伝票明細枝番号 */
    private String uridnpymeisaiedano;

}
