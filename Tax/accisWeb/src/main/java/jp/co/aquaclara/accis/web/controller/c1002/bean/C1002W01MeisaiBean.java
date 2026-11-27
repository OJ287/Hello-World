package jp.co.aquaclara.accis.web.controller.c1002.bean;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import jp.co.aquaclara.accis.web.domain.dto.c0801.shnprice.C0801W02RangeShnPriceDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 売上入力-一覧情報Beanクラス.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class C1002W01MeisaiBean implements Serializable {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** AP基本コード */
    private String apkihoncd;
    /** ショップコード */
    private String shopcd;
    /** 枝番号 */
    private String edano;
    /** 配達区分 */
    private String dlvrykbn;
    /** 明細区分 */
    private String meisaikbn;
    /** 商品コード */
    private String shncdmei;
    /** 商品略称名 */
    private String shnnmryakushomei;
    /** 色 */
    private String iromei;
    /** サイズ */
    private String szmei;
    /** 型式 */
    private String katasikimei;
    /** 型式名称 */
    private String katasikinmmei;
    /** 計上区分（keijokbnSelectItems（売上計上区分セレクトボックスリストの選択値）） */
    private String keijokbnmei;
    /** 計上区分名称（keijokbnSelectItems（売上計上区分セレクトボックスリストの選択値）） */
    private String keijokbnnmmei;
    /** 売上数量 */
    private String suryo1mei;
    /** 売上数量単位 */
    private String trtskitani1mei;
    /** 売上入数 */
    private String irisu1mei;
    /** 売上入数単位 */
    private String irisutani1mei;
    /** 売上総数 */
    private String sokazu1mei;
    /** 売上単価 */
    private String unt1mei;
    /** 売上金額 */
    private String kin1mei;
    /** 売上消費税金額 */
    private String shohitaxkinn1mei;
    /** 仕入数量 */
    private String suryo2mei;
    /** 仕入数量単位 */
    private String trtskitani2mei;
    /** 仕入入数 */
    private String irisu2mei;
    /** 仕入入数単位 */
    private String irisutani2mei;
    /** 仕入総数 */
    private String sokazu2mei;
    /** 仕入単価 */
    private String unt2mei;
    /** 仕入金額 */
    private String kin2mei;
    /** 仕入消費税金額 */
    private String shohitaxkinn2mei;
    /** 標準単価 */
    private String hyojununt;
    /** 明細備考 */
    private String meisaibikomei;
    // hidden項目
    /** 受注伝票明細番号 */
    private String uridnpymeisaino;
    /** 受注伝票明細枝番号 */
    private String uridnpymeisaiedano;
    /** データ作成元区分 */
    private String datsksmotokbn;
    /** 大分類コード */
    private String lctgrycd;
    /** 中分類コード */
    private String mctgrycd;
    /** 小分類コード */
    private String sctgrycd;

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

    /** 販促区分 */
    private String hansokukbn;
    /** 販促開始日 */
    private String hansokustrtdd;
    /** 販促終了日 */
    private String hansokushrydd;
    /** 販促価格 */
    private String hansokuprice;
    /** 販促持ち帰り価格 */
    private String hansokumochikaeriprice;

    /** 料金種別特別単価フラグ */
    private String rknkindtokubetsuuntflg;
    /** 特別単価適用開始日 */
    private String tokubetsustrtdd;
    /** 特別単価適用終了日 */
    private String tokubetsushrydd;
    /** 特別単価配達価格 */
    private String tokubetsuprice;
    /** 特別単価持ち帰り価格 */
    private String tokubetsumochikaeriprice;

    @Builder.Default
    private List<C0801W02RangeShnPriceDto> tokubetsuPriceList = new LinkedList<>();

    /** 単価消費税金額 */
    private String untshohitaxkin;
    /** 仕入単価消費税金額 */
    private String siireuntshohitaxkin;
    /** 消費税金額 */
    private String shohitaxkin;
    /** 初期表示価格 */
    private String shokidsplyprice;
    /** 在庫単価 */
    private String zaikount;
    /** 商品税区分 */
    private String shntaxkbn;
    /** 単品明細フラグ */
    private String tanpinmeisaiflg;
    /** 税調整フラグ */
    private String zeicyoseiflg;
    /** 在庫更新済フラグ */
    public String zaikokshnsumiflg;
    /** 入力明細フラグ */
    public String nyuryokumeisaiflg;
    /** 税区分 */
    public String taxkbn;
    /** セット商品コード */
    public String stshncd;
    /** 在庫管理対象フラグ */
    private String zaikokanritrgtflg;
    /** セット区分 */
    private String stkbn;
    /** 価格区分 */
    private String pricekbn;
    /** 得意先区分 */
    private String tokuisakikbn;
    /** 「取消」モード時に裏で持つ消費税金額 */
    private String shohitaxkinnodisp;
    /** 「取消」モード時に裏で持つ参考消費税金額 */
    private String sankoushohitaxkinnodisp;

    /** 売上単価手入力有無 */
    private String manualChangedUnt;
    /** 売上金額手入力有無 */
    private String manualChangedKin;
    /** 予定ポイント */
    private String pointsu;
}
