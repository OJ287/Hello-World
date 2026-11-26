package jp.co.aquaclara.accis.web.domain.service.business;

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
import jp.co.aquaclara.accis.common.util.business.BusinessUtil;
import jp.co.aquaclara.accis.web.domain.service.x9901.StringUtil;

/**
 * 商品明細消費税・金額計算Service
 * 
 * @author accisdev041
 *
 */
@Service
public class TaxService {

    /** 消費税率管理マスタDAO **/
    @Autowired
    private S02M091TDao s02m091tDao;

    /**
     * 初期表示メイン処理
     * 
     * @param tankaStr 単価
     * @param suryoStr 数量
     * @param shntaxkbn 商品税区分
     * @param tokuisakikbn 得意先区分
     * @param kijundd 基準日
     * @param s02m046tEntity ショップマスタEntity
     * @return String[単価消費税、消費税金額、税抜金額、税込金額]
     */
    public String[] getPriceList(String tankaStr, String suryoStr, String shntaxkbn,
        String tokuisakikbn, String kijundd, S02M046TEntity s02m046tEntity) {

        // 引数．単価、数量が数値の場合、以下金額計算を行う
        if (isNumeric(tankaStr) && isNumeric(suryoStr)) {
            // 単価、数量セット
            BigDecimal tanka = new BigDecimal(tankaStr);
            BigDecimal suryo = new BigDecimal(suryoStr);
            // 戻り値初期化
            BigDecimal tankaTax = BigDecimal.ZERO; // 単価消費税
            BigDecimal taxprice = BigDecimal.ZERO; // 消費税金額
            BigDecimal excprice = BigDecimal.ZERO; // 税抜金額
            BigDecimal incprice = BigDecimal.ZERO; // 税込金額

            // 商品消費税取得
            String shohiTax = "0";
            S02M091TEntityExample S02M091TExample = new S02M091TEntityExample();
            jp.co.aquaclara.accis.common.domain.repository.entity.S02M091TEntityExample.Criteria S02M091TCriteria =
                S02M091TExample.createCriteria();
            S02M091TCriteria.andTaxkbnEqualTo(shntaxkbn).andTekiyostrtddLessThanOrEqualTo(kijundd)
                .andTekiyoshryddGreaterThanOrEqualTo(kijundd)
                .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
            List<S02M091TEntity> s02m091tOutList = s02m091tDao.selectByExample(S02M091TExample);
            if (s02m091tOutList.size() > 0) {
                shohiTax = String.valueOf(s02m091tOutList.get(0).getShohitax());
            }
            String taxRate = "0";
            if (isNumeric(shohiTax)) {
                if (shohiTax.length() > 1) {
                    taxRate = "0." + shohiTax;
                } else {
                    taxRate = "0.0" + shohiTax;
                }
            }

            // 税区分、端数計算区分を取得
            int taxRounding = Integer.parseInt(s02m046tEntity.getTaxhasukeisankbn());
            int uriRounding = Integer.parseInt(s02m046tEntity.getUrikinhasukeisankbn());

            // 税抜金額計算
            switch (uriRounding) {
                case 1: // 四捨五入
                    excprice = tanka.multiply(suryo).setScale(0, RoundingMode.HALF_UP);
                    break;
                case 2: // 切捨
                    excprice = tanka.multiply(suryo).setScale(0, RoundingMode.DOWN);
                    break;
                case 3: // 切上
                    excprice = tanka.multiply(suryo).setScale(0, RoundingMode.UP);
                    break;
            }


            if (CommonConstants.KBN_TAX_KAZEI.equals(shntaxkbn)
                || CommonConstants.KBN_TAX_KEIGEN.equals(shntaxkbn) || isNumeric(shohiTax)) {
                // 単価消費税計算
                switch (taxRounding) {
                    case 1: // 四捨五入
                        tankaTax = tanka.multiply(new BigDecimal(taxRate)).setScale(0,
                            RoundingMode.HALF_UP);
                        break;
                    case 2: // 切捨
                        tankaTax =
                            tanka.multiply(new BigDecimal(taxRate)).setScale(0, RoundingMode.DOWN);
                        break;
                    case 3: // 切上
                        tankaTax =
                            tanka.multiply(new BigDecimal(taxRate)).setScale(0, RoundingMode.UP);
                        break;
                }

                // 税計算区分のセット
                String keisan = "";
                if (kijundd.compareTo(s02m046tEntity.getInvoicshikoymd()) >= 0) {
                    // インボイス施行日後
                    keisan = CommonConstants.KBN_TAXKEISAN_EX;

                } else {
                    // インボイス施行日前
                    if (CommonConstants.KBN_TOKUISAKI_SHOP.equals(tokuisakikbn)
                        || CommonConstants.KBN_TOKUISAKI_AP.equals(tokuisakikbn)
                        || CommonConstants.KBN_TOKUISAKI_ACI.equals(tokuisakikbn)
                        || CommonConstants.KBN_TOKUISAKI_NAJUTAKU.equals(tokuisakikbn)
                        || CommonConstants.KBN_TOKUISAKI_DLVRYJUTAKU.equals(tokuisakikbn)) {
                        keisan = CommonConstants.KBN_TAXKEISAN_EX;
                    } else {
                        keisan =
                            BusinessUtil.getCurrentValue(s02m046tEntity.getGenztaxkeisankbnuri(),
                                s02m046tEntity.getShintaxkeisankbnuri(),
                                s02m046tEntity.getTaxkeisankbnurikirikaedd(), kijundd);
                    }
                }

                // 内税の場合
                if (keisan.equals(CommonConstants.KBN_TAXKEISAN_IN)) {
                    // 税込金額計算
                    switch (uriRounding) {
                        case 1: // 四捨五入
                            incprice = tanka.add(tankaTax).multiply(suryo).setScale(0,
                                RoundingMode.HALF_UP);
                            break;
                        case 2: // 切捨
                            incprice =
                                tanka.add(tankaTax).multiply(suryo).setScale(0, RoundingMode.DOWN);
                            break;
                        case 3: // 切上
                            incprice =
                                tanka.add(tankaTax).multiply(suryo).setScale(0, RoundingMode.UP);
                            break;
                    }
                    // 消費税金額計算
                    taxprice = incprice.subtract(excprice);

                    // 外税の場合
                } else {
                    // 消費税金額計算
                    switch (taxRounding) {
                        case 1: // 四捨五入
                            taxprice = excprice.multiply(new BigDecimal(taxRate)).setScale(0,
                                RoundingMode.HALF_UP);
                            break;
                        case 2: // 切捨
                            taxprice = excprice.multiply(new BigDecimal(taxRate)).setScale(0,
                                RoundingMode.DOWN);
                            break;
                        case 3: // 切上
                            taxprice = excprice.multiply(new BigDecimal(taxRate)).setScale(0,
                                RoundingMode.UP);
                            break;
                    }
                    // 税込金額計算
                    incprice = excprice.add(taxprice);
                    // 外税の場合は単価消費税を０とする
                    tankaTax = BigDecimal.ZERO;
                }
            } else {
                // 上記以外の場合、税込金額に税抜金額を設定する
                incprice = excprice;
            }

            return new String[] {tankaTax.toString(), taxprice.toString(), excprice.toString(),
                incprice.toString()};
        } else {
            return new String[] {"0", "0", "0", "0"};
        }
    }

    /**
     * 文字列数値判定
     * 
     * @param str 判定文字列
     * @return 数値：true 数値以外:false
     */
    private boolean isNumeric(String str) {
        if (StringUtil.isEmpty(str)) {
            return false;
        }
        return str.matches("[+-]?\\d*(\\.\\d+)?");
    }
}
