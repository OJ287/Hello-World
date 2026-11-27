package jp.co.aquaclara.accis.web.domain.dto.c0801;

import java.io.Serializable;
import java.math.RoundingMode;
import java.time.LocalDate;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.web.domain.dto.c0800.ComparableValueCurrentOrNew;
import lombok.Builder;
import lombok.Data;

/**
 * 受注登録（受注登録）_ショップ計算オプションDTO
 */
@Data
@Builder
public class C0801W02ShopCalculateOptionDto
    implements Serializable, ComparableValueCurrentOrNew {
    private static final long serialVersionUID = 1L;

    /** 消費税軽減税率施行日 */
    public static final String REDUCED_TAX_RATE_STRDD = "20191001";

    /** インボイス施行日 */
    private String invoicshikoymd;
    /** 現消費税率 */
    private String genzshohitaxrate;
    /** 新消費税率 */
    private String shinshohitaxrate;
    /** 消費税切替日 */
    private String shohitaxkirikaedd;
    /** 現計算区分 */
    private String genzkeisankbn;
    /** 新計算区分 */
    private String shinkeisankbn;
    /** 税計算区分切替日 */
    private String taxkeisankbnurikirikaedd;
    /** 税区分 */
    private String taxkbn;
    /** 税端数計算区分 */
    private String taxhasukeisankbn;
    /** 売上金額端数計算区分 */
    private String urikinhasukeisankbn;
    /** 計算方法 */
    private String keisanhoho;
    /** 対象日 */
    private String targetDate;
    /** 税計算区分対象日 */
    private String taxkeisankbnTargetDate;

    /**
     * 計算区分を取得する.
     * 
     * @return 計算区分
     */
    public String getKeisankbn() {
        return getCurrentValue(genzkeisankbn, shinkeisankbn, taxkeisankbnurikirikaedd,
            taxkeisankbnTargetDate);
    }

    /**
     * 消費税率を取得する.
     * 
     * @return 消費税率
     */
    public String getShohitaxrate() {
        return getCurrentValue(genzshohitaxrate, shinshohitaxrate, shohitaxkirikaedd, targetDate);
    }

    /**
     * 税端数計算区分に応じたRoundingModeを取得します.
     * 
     * @return RoundingMode
     */
    public RoundingMode getTaxHasuRoundingMode() {
        return convertHasukeisankbn(taxhasukeisankbn);
    }

    /**
     * 売上金額端数計算区分に応じたRoundingModeを取得します.
     * 
     * @return RoundingMode
     */
    public RoundingMode getKinHasuRoundingMode() {
        return convertHasukeisankbn(urikinhasukeisankbn);
    }

    /**
     * 指定された日付がショップマスタのインボイス施行日以降（当日を含む）かを返す
     * 
     * @param targetDate 日付
     * @return 指定された日付がインボイス施行日以降ならばtrue
     */
    public boolean isAfterShopInvoiceDate(String targetDate) {
        return isAfterShopInvoiceDate(getLocalDate(targetDate));
    }

    /**
     * 指定された日付がショップマスタのインボイス施行日以降（当日を含む）かを返す
     * 
     * @param targetDate 日付
     * @return 指定された日付がインボイス施行日以降ならばtrue
     */
    public boolean isAfterShopInvoiceDate(LocalDate targetDate) {
        LocalDate invoiceDate = getLocalDate(getInvoicshikoymd());
        return invoiceDate.isEqual(targetDate) || invoiceDate.isBefore(targetDate);
    }

    /**
     * 端数計算区分をRoundingModeに変換します.
     * 
     * @param hasukeisankbn 端数計算区分
     * @return RoundingMode
     */
    private RoundingMode convertHasukeisankbn(String hasukeisankbn) {
        switch (hasukeisankbn) {
            case CommonConstants.KBN_HASU_KIRISHUTE:
                return RoundingMode.DOWN;
            case CommonConstants.KBN_HASU_KIRIAGE:
                return RoundingMode.UP;
            default:
                return RoundingMode.HALF_UP;

        }
    }
}
