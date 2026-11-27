package jp.co.aquaclara.accis.web.controller.c0802.bean;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 受注明細情報Beanクラス(一覧表示部分).
 */
@Data
@AllArgsConstructor
public class C0802W01MeisaiDetailBean implements Serializable {

    public C0802W01MeisaiDetailBean() {
        super();
    }

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** 商品コード */
    private String shncd;
    /** 商品名称略称 */
    private String shnnmryakusho;
    /** 色 */
    private String iro;
    /** サイズ */
    private String sz;

    /** 型式 */
    private String katasiki;
    /** 計上区分 */
    private String keijokbn;
    /** 受注数量 */
    private String acptsuryo;
    /** 受注数量単位 */
    private String uritrtskitani;
    /** 売上入数 */
    private String uriirisu;
    /** 売上入数単位 */
    private String uritani;
    /** 総数 */
    private String sokazu;
    /** 標準単価 */
    private String hyojununt;
    /** 受注単価 */
    private String acptunt;
    /** 受注単価活性非活性制御区分 */
    private String acptuntcntlkbn;
    /** 金額 */
    private String kin;

    /** 総回数 */
    private String soukaisumei;
    /** 残回数 */
    private String zankaisumei;
    /** 明細備考 */
    private String meisaibiko;
    /** 行番号（明細） */
    private Integer gyono;
    /** 更新対象フラグ */
    private String kshntrgtflg;
    /** 大分類コード */
    private String lctgrycd;
    /** 中分類コード */
    private String mctgrycd;
    /** 小分類コード */
    private String sctgrycd;
    /** 受注伝票明細番号 */
    private String acptdnpymeisaino;
    /** 受注伝票明細枝番号 */
    private String acptdnpymeisaiedano;
    /** データ作成元区分 */
    private String datsksmotokbn;
    /** 価格区分 */
    private String pricekbn;
    /** 標準価格切替日 */
    private String hyojunpricekirikaedd;
    /** 現標準価格 */
    private String genzhyojunprice;
    /** 現標準持ち帰り価格 */
    private String genzhyojunmochikaeriprice;
    /** 新標準価格 */
    private String shinhyojunprice;
    /** 新標準持ち帰り価格 */
    private String shinhyojunmochikaeriprice;
    /** ショップ価格切替日 */
    private String shoppricekirikaedd;
    /** 現ショップ価格 */
    private String genzshopprice;
    /** 現ショップ持ち帰り価格 */
    private String genzshopmochikaeriprice;
    /** 新ショップ価格 */
    private String shinshopprice;
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
    /** 受注単価消費税金額 */
    private String acptuntshohitaxkin;
    /** 受注消費税金額 */
    private String acptshohitaxkin;
    /** 初期表示価格 */
    private String shokidsplyprice;
    /** 在庫単価 */
    private String zaikount;
    /** 商品税区分 */
    private String shntaxkbn;
    /** 単品明細フラグ */
    private String tanpinmeisaiflg;
    /** 更新処理フラグ */
    private String updateflg;
    /** 税区分 */
    private String taxkbn;
    /** 有償区分 */
    private String yusyokbn;
    /** 明細区分 */
    private String meisaikbn;
    /** 入力明細フラグ */
    private String nyuryokumeisaiflg;
    /** セット商品コード */
    private String stshncd;
    /** 得意先区分 */
    private String tokuisakikbn;

    /** 相関チェック用インデックス */
    private int idxForCheck;

}
