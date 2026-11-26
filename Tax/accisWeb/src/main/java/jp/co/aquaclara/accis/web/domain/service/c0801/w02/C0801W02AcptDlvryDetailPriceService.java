package jp.co.aquaclara.accis.web.domain.service.c0801.w02;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M091TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M091TEntityExample;
import jp.co.aquaclara.accis.common.util.business.NumberUtils;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02AcptDlvryDetailDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02AcptDlvryDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02ShopCalculateOptionDto;
import jp.co.aquaclara.accis.web.domain.service.c0800.UtilityService;
import jp.co.aquaclara.accis.web.domain.service.x9901.StringUtil;

/**
 * C0801W02_受注登録（受注登録）_受注配達明細価格サービス.
 */
@Service
public class C0801W02AcptDlvryDetailPriceService {

    // 受注配達明細DTOから受注金額への変換関数
    private static final Function<C0801W02AcptDlvryDetailDto, BigDecimal> ACPTKIN_MAPPTER =
        detailDto -> detailDto.getDecimalValue(detailDto.getAcptkin());
    // 受注配達明細DTOから納品金額への変換関数
    private static final Function<C0801W02AcptDlvryDetailDto, BigDecimal> NOHINKIN_MAPPER =
        detailDto -> detailDto.getDecimalValue(detailDto.getNohinkin());
    // 受注配達明細DTOから受注金額への変換関数
    private static final Function<C0801W02AcptDlvryDetailDto, BigDecimal> ACPTSHOHITAXKIN_MAPPTER =
        detailDto -> detailDto.getDecimalValue(detailDto.getAcptshohitaxkin());
    // 受注配達明細DTOから納品金額への変換関数
    private static final Function<C0801W02AcptDlvryDetailDto, BigDecimal> NOHINSHOHITAXKIN_MAPPER =
        detailDto -> detailDto.getDecimalValue(detailDto.getNohinshohitaxkin());
    // 税区分「1:課税」のフィルタ
    private static final Predicate<C0801W02AcptDlvryDetailDto> KAZEI_FILTER =
        detailDto -> CommonConstants.KBN_TAX_KAZEI.equals(detailDto.getShnTaxKbnByKeijoKbn());
    // 税区分「2:非課税」のフィルタ
    private static final Predicate<C0801W02AcptDlvryDetailDto> HIKAZEI_FILTER =
        detailDto -> CommonConstants.KBN_TAX_HIKAZEI.equals(detailDto.getShnTaxKbnByKeijoKbn());
    // 税区分「3:不課税」のフィルタ
    private static final Predicate<C0801W02AcptDlvryDetailDto> FUKAZEI_FILTER =
        detailDto -> CommonConstants.KBN_TAX_HUKAZEI.equals(detailDto.getShnTaxKbnByKeijoKbn());
    // 税区分「4:課税軽減」のフィルタ
    private static final Predicate<C0801W02AcptDlvryDetailDto> KEIGEN_FILTER =
        detailDto -> CommonConstants.KBN_TAX_KEIGEN.equals(detailDto.getShnTaxKbnByKeijoKbn());

    /** 消費税率管理マスタDAO */
    @Autowired
    private S02M091TDao s02m091tDao;

    /** ユーティリティ */
    @Autowired
    private UtilityService util;

    /**
     * 受注伝票の受注金額および納品金額を計算する
     * 
     * @param acptDlvryDto 受注配達DTO
     * @param detailList 受注配達明細DTOリスト
     * @param option 計算用オプション
     */
    public void calculateOrderSlip(C0801W02AcptDlvryDto acptDlvryDto,
        List<C0801W02AcptDlvryDetailDto> detailList, C0801W02ShopCalculateOptionDto option) {
        // 代引判定フラグ
        boolean isCashOnDelivery = util.isFlagged(acptDlvryDto.getAcptthdaibikiflg());
        // 受注金額計算
        BigDecimal[] acptPriceList = getPriceList(isCashOnDelivery, detailList,
            acptDlvryDto.getDlvryytidd(), option, ACPTKIN_MAPPTER, ACPTSHOHITAXKIN_MAPPTER);
        // 納品金額計算
        BigDecimal[] nohinPriceList = getPriceList(isCashOnDelivery, detailList,
            acptDlvryDto.getAcptdd(), option, NOHINKIN_MAPPER, NOHINSHOHITAXKIN_MAPPER);
        // 受注金額
        acptDlvryDto.setAcptkin(util.getString(acptPriceList[0]));
        // 受注消費税金額
        acptDlvryDto.setAcptshohitaxkin(util.getString(acptPriceList[1]));
        // 受注一般税抜金額
        acptDlvryDto.setAcptippannukikin(util.getString(acptPriceList[2]));
        // 受注一般消費税金額
        acptDlvryDto.setAcptippantaxkin(util.getString(acptPriceList[3]));
        // 受注非課税金額
        acptDlvryDto.setAcpthikazeiuriikin(util.getString(acptPriceList[4]));
        // 受注不課税売上金額
        acptDlvryDto.setAcptfukazeiuriikin(util.getString(acptPriceList[5]));
        // 受注軽減税抜金額
        acptDlvryDto.setAcptkeigennukikin(util.getString(acptPriceList[6]));
        // 受注軽減消費税金額
        acptDlvryDto.setAcptkeigentaxkin(util.getString(acptPriceList[7]));
        // 納品金額
        acptDlvryDto.setNohinkin(util.getString(nohinPriceList[0]));
        // 納品消費税金額
        acptDlvryDto.setNohinshohitaxkin(util.getString(nohinPriceList[1]));
        // 納品一般税抜金額
        acptDlvryDto.setNohinippannukikin(util.getString(nohinPriceList[2]));
        // 納品一般消費税金額
        acptDlvryDto.setNohinippantaxkin(util.getString(nohinPriceList[3]));
        // 納品非課税金額
        acptDlvryDto.setNohinhikazeiuriikin(util.getString(nohinPriceList[4]));
        // 納品不課税売上金額
        acptDlvryDto.setNohinfukazeiuriikin(util.getString(nohinPriceList[5]));
        // 納品軽減税抜金額
        acptDlvryDto.setNohinkeigennukikin(util.getString(nohinPriceList[6]));
        // 納品軽減消費税金額
        acptDlvryDto.setNohinkeigentaxkin(util.getString(nohinPriceList[7]));
    }

    /**
     * 受注配達明細DTOリストについて、税区分ごとの金額サマリと税額計算を行う. <br>
     * 算出結果は配列内に下記の通り設定される
     * <ol start="0">
     * <li>税抜金額</li>
     * <li>消費税金額</li>
     * <li>一般税抜金額</li>
     * <li>一般消費税金額</li>
     * <li>非課税金額</li>
     * <li>不課税金額</li>
     * <li>軽減税抜金額</li>
     * <li>軽減消費税金額</li>
     * </ol>
     * 
     * @param isCashOnDelivery 代引の場合true
     * @param detailList 受注配達明細DTOリスト
     * @param targetDate 対象日
     * @param option 計算用オプション
     * @param kinMapper 受注配達明細DTOから計算対象とする金額を得るための変換関数
     * @param taxMapper 受注配達明細DTOから計算対象とする消費税額を得るための変換関数
     * @return 計算結果
     */
    protected BigDecimal[] getPriceList(boolean isCashOnDelivery,
        List<C0801W02AcptDlvryDetailDto> detailList, String targetDate,
        C0801W02ShopCalculateOptionDto option,
        Function<C0801W02AcptDlvryDetailDto, BigDecimal> kinMapper,
        Function<C0801W02AcptDlvryDetailDto, BigDecimal> taxMapper) {
        // 税区分「1:課税」の金額をサマリ
        BigDecimal ippannukikin = detailList.stream().filter(KAZEI_FILTER).map(kinMapper)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 税区分「2:非課税」の金額をサマリ
        BigDecimal hikazeiuriikin = detailList.stream().filter(HIKAZEI_FILTER).map(kinMapper)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 税区分「3:不課税」の金額をサマリ
        BigDecimal fukazeiuriikin = detailList.stream().filter(FUKAZEI_FILTER).map(kinMapper)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 税区分「4:課税軽減」の金額をサマリ
        BigDecimal keigennukikin = detailList.stream().filter(KEIGEN_FILTER).map(kinMapper)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 課税と課税軽減のの消費税率を取得
        BigDecimal kazeiTaxRate = getShohiTaxRate(CommonConstants.KBN_TAX_KAZEI, targetDate);
        BigDecimal keigenTaxRate = getShohiTaxRate(CommonConstants.KBN_TAX_KEIGEN, targetDate);
        // 課税と課税軽減の消費税金額を算出
        BigDecimal kazeiTax =
            ippannukikin.multiply(kazeiTaxRate).setScale(0, option.getTaxHasuRoundingMode());
        BigDecimal keigenTax =
            keigennukikin.multiply(keigenTaxRate).setScale(0, option.getTaxHasuRoundingMode());
        // 金額、消費税金額の算出
        BigDecimal kin = ippannukikin.add(keigennukikin).add(hikazeiuriikin).add(fukazeiuriikin);
        BigDecimal tax = kazeiTax.add(keigenTax);
        if (!isCashOnDelivery && !util.isLater(targetDate, option.getInvoicshikoymd())) {
            // 代引以外かつ対象日がインボイス施行日以前の場合
            // 金額＝明細の金額をサマリ
            kin = detailList.stream().map(kinMapper).reduce(BigDecimal.ZERO, BigDecimal::add);
            // 消費税金額＝明細の消費税金額をサマリ
            tax = detailList.stream().map(taxMapper).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        // 算出結果を配列に設定して返す
        return new BigDecimal[] {
            // 金額
            kin,
            // 消費税金額
            tax,
            // 一般金額
            ippannukikin,
            // 一般消費税金額
            kazeiTax,
            // 非課税金額
            hikazeiuriikin,
            // 不課税金額
            fukazeiuriikin,
            // 軽減金額
            keigennukikin,
            // 軽減消費税金額
            keigenTax};
    }

    /**
     * 明細の金額を計算する.
     *
     * @param unt 単価
     * @param suryo数量
     * @param shntaxkbn 商品税区分
     * @param targetDate 対象日（インボイス施行日判定用）
     * @param detailDto 受注配達明細データ
     * @return 計算結果（受注単価消費税金額、受注消費税金額、受注金額（※税抜金額））
     */
    public BigDecimal[] getPriceList(BigDecimal unt, BigDecimal suryo, String shntaxkbn,
        String targetDate, C0801W02AcptDlvryDetailDto detailDto) {

        C0801W02ShopCalculateOptionDto calcOptionDto = detailDto.getCalculateOptionDto();
        // 数量、単価が有効な場合は計算する
        if (NumberUtils.isNumber(detailDto.getAcptsuryo(), true)
            && NumberUtils.isNumber(detailDto.getAcptunt(), true)) {
            // ゼロで初期化
            BigDecimal taxPrice = BigDecimal.ZERO;
            BigDecimal untShohiTax = BigDecimal.ZERO;
            // 税抜金額（数量×単価）
            BigDecimal amountWithoutTax =
                suryo.multiply(unt).setScale(0, calcOptionDto.getKinHasuRoundingMode());
            // 税込金額（税抜金額で初期化する）
            BigDecimal amountWithTax = amountWithoutTax;
            // 消費税率を取得する
            BigDecimal shohiTaxRate = getShohiTaxRate(shntaxkbn, calcOptionDto.getTargetDate());
            // 明細から商品税区分が取得できない場合は、ショップ価格情報の税区分を使用する
            String taxkbn = Objects.requireNonNullElse(shntaxkbn, calcOptionDto.getTaxkbn());
            // 税区分が課税または軽減税率の場合は消費税を計算
            if (CommonConstants.KBN_TAX_KAZEI.equals(taxkbn)
                || CommonConstants.KBN_TAX_KEIGEN.equals(taxkbn)) {
                // 単価消費税を算出
                untShohiTax =
                    unt.multiply(shohiTaxRate).setScale(0, calcOptionDto.getTaxHasuRoundingMode());
                // 税計算区分
                String keisankbn;
                if (util.isLater(targetDate, calcOptionDto.getInvoicshikoymd())) {
                    // インボイス施工後
                    keisankbn = CommonConstants.KBN_TAXKEISAN_EX;
                } else {
                    // インボイス施行日前(卸対象は外税、それ以外はショップマスタの税計算区分）
                    if (detailDto.getAcptRelation().isWholesale()) {
                        keisankbn = CommonConstants.KBN_TAXKEISAN_EX;
                    } else {
                        keisankbn = calcOptionDto.getKeisankbn();
                    }
                }
                // 消費税計算
                if (CommonConstants.KBN_TAXKEISAN_IN.equals(keisankbn)) {
                    // 内税(商品1つの税込金額に対して数量を乗算し、税込金額を算出）
                    amountWithTax = unt.add(untShohiTax).multiply(suryo).setScale(0,
                        calcOptionDto.getKinHasuRoundingMode());
                    taxPrice = amountWithTax.subtract(amountWithoutTax);
                } else {
                    // 外税（全体の税抜金額から消費税額を算出してから税込金額を算出）
                    taxPrice = amountWithoutTax.multiply(shohiTaxRate).setScale(0,
                        calcOptionDto.getTaxHasuRoundingMode());
                    amountWithTax = amountWithoutTax.add(taxPrice);
                    // 商品単価に対する消費税額はゼロとする
                    untShohiTax = BigDecimal.ZERO;
                }
            }
            return new BigDecimal[] {untShohiTax, taxPrice, amountWithoutTax};
        } else {
            // 数量や単価が適切でない場合は全てゼロを設定
            return new BigDecimal[] {BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};
        }
    }

    /**
     * 明細の受注金額を計算する.<br>
     * <br>
     * 受注配達明細データの以下の項目に計算結果を設定する
     * <ul>
     * <li>受注単価消費税金額</li>
     * <li>受注消費税金額</li>
     * <li>受注金額</li>
     * </ul>
     *
     * @param targetDate 対象日（インボイス施行日判定用）
     * @param detailDto 受注配達明細データ
     */
    public void calculateAcptkin(String targetDate, C0801W02AcptDlvryDetailDto detailDto) {
        // 計算用に型変換
        BigDecimal suryo = detailDto.getDecimalValue(detailDto.getAcptsuryo());
        BigDecimal unt = detailDto.getDecimalValue(detailDto.getAcptunt());
        BigDecimal[] priceList =
            getPriceList(unt, suryo, detailDto.getShnTaxKbnByKeijoKbn(), targetDate, detailDto);
        detailDto.setAcptuntshohitaxkin(priceList[0].stripTrailingZeros().toPlainString());
        detailDto.setAcptshohitaxkin(priceList[1].stripTrailingZeros().toPlainString());
        detailDto.setAcptkin(priceList[2].stripTrailingZeros().toPlainString());
    }

    /**
     * 明細の納品金額を計算する.<br>
     * <br>
     * 受注配達明細データの以下の項目に計算結果を設定する
     * <ul>
     * <li>納品単価消費税金額</li>
     * <li>納品消費税金額</li>
     * <li>納品金額</li>
     * </ul>
     *
     * @param targetDate 対象日（インボイス施行日判定用）
     * @param detailDto 受注配達明細データ
     */
    public void calculateNohinkin(String targetDate, C0801W02AcptDlvryDetailDto detailDto) {
        // 計算用に型変換
        BigDecimal suryo = detailDto.getDecimalValue(detailDto.getNohinsuryo());
        BigDecimal unt = detailDto.getDecimalValue(detailDto.getNohinunt());
        BigDecimal[] priceList =
            getPriceList(unt, suryo, detailDto.getShnTaxKbnByKeijoKbn(), targetDate, detailDto);
        detailDto.setNohinuntshohitaxkin(priceList[0].stripTrailingZeros().toPlainString());
        detailDto.setNohinshohitaxkin(priceList[1].stripTrailingZeros().toPlainString());
        detailDto.setNohinkin(priceList[2].stripTrailingZeros().toPlainString());
    }

    /**
     * 消費税率を取得する.
     * 
     * @param shntaxkbn 商品税区分
     * @param targetDate 対象日
     * @return 消費税率（小数）
     */
    protected BigDecimal getShohiTaxRate(String shntaxkbn, String targetDate) {
        if (StringUtil.isEmpty(shntaxkbn)) {
            return BigDecimal.ZERO;
        }
        // 消費税率管理マスタから消費税率を取得する
        S02M091TEntityExample condition = new S02M091TEntityExample();
        condition.createCriteria()
            // 税区分
            .andTaxkbnEqualTo(shntaxkbn)
            // 適用開始日
            .andTekiyostrtddLessThanOrEqualTo(targetDate)
            // 適用終了日
            .andTekiyoshryddGreaterThanOrEqualTo(targetDate)
            // 有効区分
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        // 取得結果から消費税率を取得、取得できない場合は0を返す
        return s02m091tDao.selectByExample(condition).stream().findFirst()
            .map(entity -> entity.getShohitax()).orElse(BigDecimal.ZERO).scaleByPowerOfTen(-2);
    }
}
