package jp.co.aquaclara.accis.web.controller.b1501.bean;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 請求入金照会-一覧情報Beanクラス.
 */
@Data
@AllArgsConstructor
public class B1501W11MeisaiBean implements Serializable {

    public B1501W11MeisaiBean() {
        super();
    }

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** 年月明細 */
    private String dydmmei;
    /** レギュラー有料 */
    private String rgrayuryomei;
    /** レギュラー無料 */
    private String rgramuryomei;
    /** スリム有料 */
    private String srmyuryomei;
    /** スリム無料 */
    private String srmmuryomei;
    /** その他有料 */
    private String sonotayuryomei;
    /** その他無料 */
    private String sonotamuryomei;
    /** 製品水売上 */
    private String seihinmizuurimei;
    /** レンタル料金 */
    private String rtlrknmei;
    /** その他売上 */
    private String sonotaurimei;
    /** 当月売上計 */
    private String totsukiurikeimei;
    /** 消費税金額 */
    private String uchishohitaxkinmei;
    /** 税計算区分 */
    private String shohitaxkbnmei;
    /** 当月税込売上 */
    private String taxkomiurikeikinmei;
    /** 前月残高 */
    private String maedmzdmei;
    /** 当月入金 */
    private String totsukinyknmei;
    /** 当月残高 */
    private String totsukizdmei;
    /** 回収方法 */
    private String kaishuhoho;
    /** 回収方法区分 */
    private String kaishuhohokbn;
    /** 連番 */
    private String renban;

}
