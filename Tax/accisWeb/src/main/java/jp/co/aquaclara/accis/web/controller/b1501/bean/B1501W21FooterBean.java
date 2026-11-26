package jp.co.aquaclara.accis.web.controller.b1501.bean;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 請求入金照会（詳細）-フッター情報Beanクラス.
 */
@Data
@AllArgsConstructor
public class B1501W21FooterBean implements Serializable {

    public B1501W21FooterBean() {
        super();
    }

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

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
    /** 印刷ボタン利用フラグ */
    private String insatable;
    /** 処理フラグ */
    private String insatflag;
}
