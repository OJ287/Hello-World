package jp.co.aquaclara.accis.web.controller.b1501.bean;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 請求入金照会（詳細）-明細1Beanクラス.
 */
@Data
@AllArgsConstructor
public class B1501W21M1MeisaiBean implements Serializable {

    public B1501W21M1MeisaiBean() {
        super();
    }

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** 年月日 */
    private String dydmmei;
    /** 得意先状態名称 */
    private String tokuisakijotainm;
    /** 状態ステータス */
    private String jotaistsnm;
    /** 請求先コード */
    private String skysakicdmei;
    /** 請求先名称 */
    private String skysakinmn1mei;
    /** 事業所コード */
    private String jigyoshocdmei;
    /** 事業所名称 */
    private String jigyoshonmmei;
    /** 前回ご請求額 */
    private String znkskykinmei;
    /** ご入金額 */
    private String nyknkin1mei;
    /** 今回ご利用額 */
    private String konkairiyokinmei;
    /** (内)消費税額 */
    private String shohitaxkin1mei;
    /** 税込ご利用額 */
    private String taxkomiskyzdkinmei;
    /** 8%税込ご利用額計 */
    private String n8perzeikomiriyogakumei;
    /** 10%税込ご利用額計 */
    private String n10perzeikomiriyogakumei;
    /** 今回請求金額 */
    private String konkaiskykinmei;
    /** 選択 */
    private String sentakumei;
    /** 有効区分 */
    private String yukokbn;
    /** 検索時選択フラグ */
    private String searchcheckflag;
}
