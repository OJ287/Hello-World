package jp.co.aquaclara.accis.web.domain.service.x9901;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M091TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M091TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M091TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.PriceInfoVo;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.TokuisakiRelationVo;
import jp.co.aquaclara.accis.common.util.business.BusinessUtil;
import jp.co.aquaclara.accis.common.util.business.NumberUtils;

/**
 * 価格サービス業務ロジックを記述するクラス。
 * 
 */
@Service
public class PriceService {

    @Autowired
    private S02M091TDao s02m091tDao;

    @Autowired
    private CommonService commonService;

    // private static final Logger LOGGER = (Logger) Logger.getLogger(PriceService.class);

    /**
     * コンストラクタ
     * 
     * @param tran トランザクション
     */
    public PriceService() {
        super();
    }

    /**
     * 価格情報を取得します（ショップマスタより）。
     * 
     * @param apKihonCd AP基本コード
     * @param shopCd ショップコード
     * @param targetDate 対象日
     * @return S01PriceInfoVo 結果
     * @throws ApplicationException アプリケーション例外時
     * @throws SystemException システム例外
     */
    public PriceInfoVo getPriceInfo(String apKihonCd, String shopCd, String targetDate) {

        // サービスクラス取得
        // CommonService s02Service = new CommonService();

        // ショップマスタより価格情報生成
        S02M046TEntity shopModel = commonService.getShopModel(apKihonCd, shopCd);
        PriceInfoVo price = new PriceInfoVo(shopModel, targetDate);

        return price;
    }

    /**
     * 消費税を取得します。
     *
     * @return 消費税
     */
    private String getShohitax(String shnTaxkbn, String targetDate) {
        String taxkbn = "";
        targetDate = BusinessUtil.getLastdayOfMonthYMD(targetDate);
        S02M091TEntityExample entityKey = new S02M091TEntityExample();
        entityKey.createCriteria().andTaxkbnEqualTo(shnTaxkbn)
            .andTekiyostrtddLessThanOrEqualTo(targetDate)
            .andTekiyoshryddGreaterThanOrEqualTo(targetDate)
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<S02M091TEntity> list = s02m091tDao.selectByExample(entityKey);
        if (list.size() != 0) {
            BigDecimal bd = list.get(0).getShohitax();
            if (bd != null) {
                taxkbn = bd.toString();
            }
        }
        return taxkbn;
    }


    /**
     * 消費税の税率を返します。
     * 
     * @param taxKbn : KBN_TAX_KAZEI / KBN_TAX_KEIGEN
     * @param targetDate
     * @return 税率 (例: "0.1" / "0.08")
     */
    public String getShohiTaxString(String taxKbn, String targetDate) {
        String shohiTax = getShohitax(taxKbn, targetDate);
        String taxRate = "0";
        if (NumberUtils.isNumber(shohiTax)) {
            if (shohiTax.length() > 1) {
                taxRate = "0." + shohiTax;
            } else {
                taxRate = "0.0" + shohiTax;
            }
        }
        return taxRate;
    }

    /**
     * 明細の消費税、金額を計算します。
     * 
     * @param tankaStr 単価
     * @param suryoStr 数量
     * @param shntaxkbn 商品単位の税区分
     * @param tokuisakikbn 得意先区分
     * @return String[] 結果（単価消費税、消費税金額、税抜金額、税込金額）
     */
    public String[] getPriceList(PriceInfoVo vo, String tankaStr, String suryoStr, String shntaxkbn,
        String tokuisakikbn) {

        // 単価、数量が有効の時のみ算出
        if (NumberUtils.isNumber(tankaStr, true) && NumberUtils.isNumber(suryoStr)) {
            BigDecimal tanka = new BigDecimal(tankaStr);
            BigDecimal suryo = new BigDecimal(suryoStr);
            BigDecimal tankaTax = new BigDecimal(0);
            BigDecimal taxprice = new BigDecimal(0);
            BigDecimal excprice = new BigDecimal(0);
            BigDecimal incprice = new BigDecimal(0);

            // 軽減税率対応 ryu 2019-05-21 Start
            String shohiTax = getShohitax(shntaxkbn, vo.targetDate);
            // 軽減税率対応 ryu 2019-05-21 End

            // ■:1 Isac)Shiba 2008-08-19
            String taxRate = "0";
            // 軽減税率対応 ryu 2019-05-21 Start
            if (NumberUtils.isNumber(shohiTax)) {
                if (shohiTax.length() > 1) {

                    taxRate = "0." + shohiTax;

                } else {

                    taxRate = "0.0" + shohiTax;
                    // 軽減税率対応 ryu 2019-05-21 End
                }
            }
            // ■:2 Isac)Shiba 2008-08-19

            // 税区分、端数計算区分を取得
            String taxkbn = (shntaxkbn == null) ? vo.taxkbn : shntaxkbn;
            RoundingMode taxRounding = vo.getTaxRoundingMode();
            RoundingMode uriRounding = vo.getUrikinRoundingMode();

            // 税抜金額計算
            excprice = tanka.multiply(suryo).setScale(0, uriRounding);

            // 課税の時のみ消費税算出を行う

            // 軽減税率対応 ryu 2019-05-21 Start
            if (CommonConstants.KBN_TAX_KAZEI.equals(taxkbn)
                || CommonConstants.KBN_TAX_KEIGEN.equals(taxkbn)
                    && NumberUtils.isNumber(shohiTax)) {
                // 軽減税率対応 ryu 2019-05-21 End

                // ■:1 Isac)Shiba 2008-08-19
                // 単価消費税の算出
                tankaTax = tanka.multiply(new BigDecimal(taxRate)).setScale(0, taxRounding);
                // ■:2 Isac)Shiba 2008-08-19

                // 税計算区分のセット（卸対象の場合は外税固定とする）
                String keisan =
                    TokuisakiRelationVo.checkOroshi(tokuisakikbn) ? CommonConstants.KBN_TAXKEISAN_EX
                        : vo.keisankbn;

                // 内税の場合
                if (CommonConstants.KBN_TAXKEISAN_IN.equals(keisan)) {

                    // 税込金額計算
                    incprice = tanka.add(tankaTax).multiply(suryo).setScale(0, uriRounding);

                    // 消費税金額計算
                    taxprice = incprice.subtract(excprice);

                    // 外税の場合
                } else {

                    // 消費税金額計算
                    // ■:1 Isac)Shiba 2008-08-19
                    taxprice = excprice.multiply(new BigDecimal(taxRate)).setScale(0, taxRounding);
                    // ■:2 Isac)Shiba 2008-08-19

                    // 税込金額計算
                    incprice = excprice.add(taxprice);

                    // 外税の場合は単価消費税を０とする
                    tankaTax = new BigDecimal(0);
                }

            } else {
                incprice = excprice;
            }

            return new String[] {tankaTax.toString(), taxprice.toString(), excprice.toString(),
                incprice.toString()};
        } else {
            return new String[] {"0", "0", "0", "0"};
        }
    }
}
