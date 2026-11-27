package jp.co.aquaclara.accis.web.controller.commons;

import java.io.Serializable;
import lombok.Data;

/**
 * 消費税情報
 */
@Data
public class TaxInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 税区分 */
    private String taxkbn;
    /** 適用開始日 */
    private String tekiyostrtdd;
    /** 適用終了日 */
    private String tekiyoshrydd;
    /** 消費税 */
    private String shohitax;
}
