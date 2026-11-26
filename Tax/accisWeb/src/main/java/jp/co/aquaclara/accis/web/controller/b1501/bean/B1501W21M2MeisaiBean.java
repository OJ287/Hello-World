package jp.co.aquaclara.accis.web.controller.b1501.bean;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 請求入金照会（詳細）-明細2Beanクラス.
 */
@Data
@AllArgsConstructor
public class B1501W21M2MeisaiBean implements Serializable {

    public B1501W21M2MeisaiBean() {
        super();
    }

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** 日付 */
    private String ymdmei;
    /** 伝票番号 */
    private String dnpynomei;
    /** 枝番号 */
    private String edanomei;
    /** 商品コード */
    private String shncdmei;
    /** 商品名称略称 */
    private String shnnmryakushomei;
    /** 計上区分名称 */
    private String keijokbnnmmei;
    /** 数量 */
    private String suryomei;
    /** 単位 */
    private String tanimei;
    /** 入数 */
    private String irisu;
    /** 総数 */
    private String sokazu;
    /** 単価 */
    private String untmei;
    /** 売上金額 */
    private String urikinmei;
    /** 税率 */
    private String taxratemei;
    /** 消費税 */
    private String shohitaxkin2mei;
    /** 入金金額 */
    private String nyknkin2mei;
    /** 備考 */
    private String bikomei;
    /** 売上ポイント */
    private String uripoint;
    /** 特別ポイント */
    private String tkbtpoint;
    /** ポイント内訳 */
    private String pointutwk;
    /** 合計ポイント */
    private String smpoint;
    /** ポイント連携日付 */
    private String pointrenkeidd;
    /** 色名称 */
    private String ironmmei;
    /** サイズ名称 */
    private String sznmmei;
    /** 型式 */
    private String katasikimei;
    /** 得意先コード */
    private String tokuisakicdmei;
    /** 得意先名称 */
    private String tokuisakinmmei;
    /** 登録年月日 */
    private String inptsystmymdmei;
    /** 登録時間 */
    private String inptsystmjikanmei;
    /** 登録者AP基本コード */
    private String toshaapkihoncdmei;
    /** 登録者ショップコード */
    private String toshashopcdmei;
    /** 登録者コード */
    private String toshacdmei;
    /** 登録者名称 */
    private String toshameimei;
    /** 請求書出力有無区分 */
    private String skyshootptarinashikbnmei;
    /** 消費税フラグ */
    private String taxflag;
    /** 売上/入金取消フラグ */
    private String urinykntrksflag;
    /** 印刷可能フラグ */
    private String insatable;
    /** 8%の売上金額合計 */
    private String n8peruri;
    /** 8%の消費税額合計 */
    private String n8pershohzei;
    /** 10%の売上金額合計 */
    private String n10peruri;
    /** 10%の消費税額合計 */
    private String n10pershohzei;
    /** 金額合計1 */
    private String kinsm1;
    /** 金額合計2 */
    private String kinsm2;
    /** 金額合計3 */
    private String kinsm3;
    /** ポイント合計 */
    private String pointsm;
}
