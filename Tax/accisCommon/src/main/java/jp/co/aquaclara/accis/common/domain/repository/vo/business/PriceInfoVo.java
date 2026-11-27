package jp.co.aquaclara.accis.common.domain.repository.vo.business;

import java.io.Serializable;
import java.math.RoundingMode;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M021TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.util.business.BusinessUtil;



/**
 * 価格情報クラスです。
 * 
 * @author Isac)Katayama
 * @version 1.0.0 2007-05-06
 */
public class PriceInfoVo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * 消費税(shohitaxrate)
     */
    public String shohitaxrate;
    /**
     * 計算区分(keisankbn)
     */
    public String keisankbn;
    /**
     * 税区分(taxkbn)
     */
    public String taxkbn;
    /**
     * 税端数計算区分(taxhasukeisankbn)
     */
    public String taxhasukeisankbn;
    /**
     * 売上金額端数計算区分(urikinhasukeisankbn)
     */
    public String urikinhasukeisankbn;
    /**
     * 計算方法(keisanhoho)
     */
    public String keisanhoho;

    /**
     * 対象日(targetDate)
     */
    public String targetDate;


    /**
     * コンストラクタ
     * 
     * @param shohitaxrate 消費税
     * @param keisankbn 計算区分
     * @param taxkbn 税区分
     * @param taxhasukeisankbn 端数計算区分
     * @param urikinhasukeisankbn 売上金額端数計算区分
     * @param keisanhoho 計算方法
     */
    public PriceInfoVo(String shohitaxrate, String keisankbn, String taxkbn,
        String taxhasukeisankbn, String urikinhasukeisankbn, String keisanhoho) {
        this.shohitaxrate = shohitaxrate;
        this.keisankbn = keisankbn;
        this.taxkbn = taxkbn;
        this.taxhasukeisankbn = taxhasukeisankbn;
        this.urikinhasukeisankbn = urikinhasukeisankbn;
        this.keisanhoho = keisanhoho;
    }

    /**
     * コンストラクタ
     * 
     * @param model ショップマスタモデル
     * @param targetDate 対象日
     */
    public PriceInfoVo(S02M046TEntity model, String targetDate) {
        super();

        this.setShohitaxrate(BusinessUtil.getCurrentValue(model.getGenzshohitax(),
            model.getShinshohitax(), model.getShohitaxkirikaedd(), targetDate).toString());
        this.setKeisankbn(BusinessUtil.getCurrentValue(model.getGenztaxkeisankbnuri(),
            model.getShintaxkeisankbnuri(), model.getTaxkeisankbnurikirikaedd(), targetDate));
        this.setTaxkbn(CommonConstants.KBN_TAX_KAZEI);
        this.setTaxhasukeisankbn(model.getTaxhasukeisankbn());
        this.setUrikinhasukeisankbn(model.getUrikinhasukeisankbn());
        this.setKeisanhoho(CommonConstants.KBN_KEISANHOHO_MEISAI);
        // 軽減税率対応 ryu 2019-05-21 Start
        this.setTargetDate(targetDate);
        // 軽減税率対応 ryu 2019-05-21 End
    }

    // ■:1 Base)Arai 2014-01-17
    /**
     * コンストラクタ
     * 
     * @param model ショップマスタモデル
     * @param taxrateTargetDate 税率対象日
     * @param taxkeisankbnTargetDate 税計算区分対象日
     */
    public PriceInfoVo(S02M046TEntity model, String taxrateTargetDate,
        String taxkeisankbnTargetDate) {
        super();

        this.setShohitaxrate(BusinessUtil.getCurrentValue(model.getGenzshohitax(),
            model.getShinshohitax(), model.getShohitaxkirikaedd(), taxrateTargetDate).toString());
        this.setKeisankbn(BusinessUtil.getCurrentValue(model.getGenztaxkeisankbnuri(),
            model.getShintaxkeisankbnuri(), model.getTaxkeisankbnurikirikaedd(),
            taxkeisankbnTargetDate));
        this.setTaxkbn(CommonConstants.KBN_TAX_KAZEI);
        this.setTaxhasukeisankbn(model.getTaxhasukeisankbn());
        this.setUrikinhasukeisankbn(model.getUrikinhasukeisankbn());
        this.setKeisanhoho(CommonConstants.KBN_KEISANHOHO_MEISAI);
        // 軽減税率対応 ryu 2019-05-21 Start
        this.setTargetDate(taxrateTargetDate);
        // 軽減税率対応 ryu 2019-05-21 End
    }
    // ■:2 Base)Arai 2014-01-17

    /**
     * コンストラクタ
     * 
     * @param shopModel ショップマスタモデル
     * @param shiiresakiModel 仕入先マスタモデル
     * @param targetDate 対象日
     */
    public PriceInfoVo(S02M046TEntity shopModel, S02M021TEntity shiiresakiModel,
        String targetDate) {
        super();

        this.setShohitaxrate(BusinessUtil.getCurrentValue(shopModel.getGenzshohitax(),
            shopModel.getShinshohitax(), shopModel.getShohitaxkirikaedd(), targetDate).toString());
        this.setKeisankbn(shiiresakiModel.getTaxkeisankbn());
        this.setTaxkbn(shiiresakiModel.getTaxkbn());
        this.setTaxhasukeisankbn(shiiresakiModel.getHasukeisankbn());

        // 要望対応add 仕入端数計算区分に変更 ryu 20190722
        this.setUrikinhasukeisankbn(shiiresakiModel.getSiirekinhasukeisankbn());
        // 要望対応add 仕入端数計算区分に変更 ryu 20190722

        this.setKeisanhoho(shiiresakiModel.getKeisanhoho());
        // 軽減税率対応 ryu 2019-05-21 Start
        this.setTargetDate(targetDate);
        // 軽減税率対応 ryu 2019-05-21 End
    }

    /**
     * 税端数計算区分をBigDecimalの丸めモードとして取得します。
     * 
     * @return String 結果
     */
    public RoundingMode getTaxRoundingMode() {
        return convertHasukbn(this.taxhasukeisankbn);
    }

    /**
     * 売上金額端数計算区分をBigDecimalの丸めモードとして取得します。
     * 
     * @return String 結果
     */
    public RoundingMode getUrikinRoundingMode() {
        return convertHasukbn(this.urikinhasukeisankbn);
    }

    /**
     * taxhasukeisankbnを取得する。
     * 
     * @return taxhasukeisankbn
     */
    public String getTaxhasukeisankbn() {
        return taxhasukeisankbn;
    }


    /**
     * taxhasukeisankbnを設定する。
     * 
     * @param taxhasukeisankbn 設定する taxhasukeisankbn
     */
    public void setTaxhasukeisankbn(String taxhasukeisankbn) {
        this.taxhasukeisankbn = taxhasukeisankbn;
    }


    /**
     * keisankbnを取得する。
     * 
     * @return keisankbn
     */
    public String getKeisankbn() {
        return keisankbn;
    }


    /**
     * keisankbnを設定する。
     * 
     * @param keisankbn 設定する keisankbn
     */
    public void setKeisankbn(String keisankbn) {
        this.keisankbn = keisankbn;
    }


    /**
     * shohitaxrateを取得する。
     * 
     * @return shohitaxrate
     */
    public String getShohitaxrate() {
        return shohitaxrate;
    }


    /**
     * shohitaxrateを設定する。
     * 
     * @param shohitaxrate 設定する shohitaxrate
     */
    public void setShohitaxrate(String shohitaxrate) {
        this.shohitaxrate = shohitaxrate;
    }


    /**
     * taxkbnを取得する。
     * 
     * @return taxkbn
     */
    public String getTaxkbn() {
        return taxkbn;
    }


    /**
     * taxkbnを設定する。
     * 
     * @param taxkbn 設定する taxkbn
     */
    public void setTaxkbn(String taxkbn) {
        this.taxkbn = taxkbn;
    }

    /**
     * 売上金額端数計算区分を設定します。
     * 
     * @param urikinhasukeisankbn 売上金額端数計算区分
     */
    public void setUrikinhasukeisankbn(String urikinhasukeisankbn) {
        this.urikinhasukeisankbn = urikinhasukeisankbn;
    }

    /**
     * 売上金額端数計算区分を取得します。
     * 
     * @return 売上金額端数計算区分
     */
    public String getUrikinhasukeisankbn() {
        return this.urikinhasukeisankbn;
    }

    /**
     * 計算方法を設定します。
     * 
     * @param keisanhoho 計算方法
     */
    public void setKeisanhoho(String keisanhoho) {
        this.keisanhoho = keisanhoho;
    }

    /**
     * 計算方法を取得します。
     * 
     * @return 計算方法
     */
    public String getKeisanhoho() {
        return this.keisanhoho;
    }

    // 軽減税率対応 ryu 2019-05-21 Start
    /**
     * 対象日を設定します。
     * 
     * @param targetDate 対象日
     */
    public void setTargetDate(String targetDate) {
        this.targetDate = targetDate;
    }

    /**
     * 対象日を取得します。
     * 
     * @return 対象日
     */
    public String getTargetDate() {
        return this.targetDate;
    }
    // 軽減税率対応 ryu 2019-05-21 End

    /**
     * 端数計算区分をBigDecimalの丸めモードに変換します。
     * 
     * @param hasukbn 端数計算区分
     * @return String 結果
     */
    public static RoundingMode convertHasukbn(String hasukbn) {
        if (CommonConstants.KBN_HASU_KIRISHUTE.equals(hasukbn)) {
            // ■:1 Isac)Shiba 2008-05-17
            return RoundingMode.DOWN;
            // ■:2 Isac)Shiba 2008-05-17
        }
        if (CommonConstants.KBN_HASU_KIRIAGE.equals(hasukbn)) {
            // ■:1 Isac)Shiba 2008-05-17
            return RoundingMode.UP;
            // ■:2 Isac)Shiba 2008-05-17
        } else {
            return RoundingMode.HALF_UP;
        }
    }
}
