package jp.co.aquaclara.accis.web.controller.h2101.bean;

import java.io.Serializable;
import javax.validation.constraints.NotEmpty;
import jp.co.aquaclara.accis.web.annotation.ValidationMessage;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 入出庫入力-一覧情報Beanクラス.
 */
@Data
@AllArgsConstructor
@Builder
public class H2101W01MeisaiBean implements Serializable {

    public H2101W01MeisaiBean() {
        super();
    }

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

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
    /** 数量2 */
    @NotEmpty
    @ValidationMessage(validation = NotEmpty.class, messageId = MessageID.W113, args = {"数量"})
    private String suryo2mei;
    /** 数量単位2 */
    private String uritani2mei;
    /** 入数2 */
    private String irisu2mei;
    /** 入数単位2 */
    private String irisutani2mei;
    /** 総数2 */
    private String sokazu2mei;
    /** 単価2 */
    private String unt2mei;
    /** 金額2 */
    private String kin2mei;
    /** 消費税金額2 */
    private String shohitaxkinn2mei;
    /** 明細備考 */
    private String meisaibikomei;
    // hidden項目
    /** 大分類コード */
    private String lctgrycd;
    /** 中分類コード */
    private String mctgrycd;
    /** 小分類コード */
    private String sctgrycd;
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
    /** 在庫単価 */
    private String zaikount;
    /** 商品税区分 */
    private String shntaxkbn;
    /** 在庫管理対象フラグ */
    private String zaikokanritrgtflg;
    /** 商品AP基本コード */
    private String shnapkihoncd;
}
