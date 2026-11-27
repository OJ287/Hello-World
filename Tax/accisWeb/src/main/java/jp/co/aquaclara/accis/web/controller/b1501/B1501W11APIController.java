package jp.co.aquaclara.accis.web.controller.b1501;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.controller.b1501.bean.B1501W11BaseBean;
import jp.co.aquaclara.accis.web.controller.b1501.bean.B1501W11MeisaiBean;
import jp.co.aquaclara.accis.web.controller.b1501.form.B1501W11Form;
import jp.co.aquaclara.accis.web.controller.commons.AccisWebBaseController;
import jp.co.aquaclara.accis.web.controller.commons.DbManagementSession;
import jp.co.aquaclara.accis.web.domain.dto.b1501.B1501W11SearchConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.b1501.B1501W11SearchResultCurrDto;
import jp.co.aquaclara.accis.web.domain.dto.b1501.B1501W11SearchResultDto;
import jp.co.aquaclara.accis.web.domain.dto.b1501.B1501W11SearchResultListDto;
import jp.co.aquaclara.accis.web.domain.service.b1501.B1501W11Service;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import jp.co.aquaclara.accis.web.exception.dto.FormField;

/**
 * B1501W11_請求入金照会APIコントローラクラス.
 */
@RestController
@SessionAttributes(types = {DbManagementSession.class})
@RequestMapping("/api/b1501w11")
public class B1501W11APIController extends AccisWebBaseController {

    @ModelAttribute("dbManagementSession_B1501W11")
    public DbManagementSession setDbManagementSession() {
        return new DbManagementSession();
    }

    @Autowired
    private B1501W11Service service;

    // 現在行の年月表示
    private final String CUR_YM = "現在";

    // 現在行の数値初期値
    private final String CUR_INI = "0";

    // 月末日
    private final String CUR_END_DATE = "31";

    /**
     * 検索ボタン押下時検索処理.
     * 
     * @param form B1501W11Form
     * @return List<B1501W11MeisaiBean>
     */
    @GetMapping("search")
    public List<B1501W11MeisaiBean> search(
        @Validated @ModelAttribute("b1501W01Form") B1501W11Form form,
        @ModelAttribute(name = "dbManagementSession_B1501W11",
            binding = false) DbManagementSession dbManagementSession) {

        // 相関チェック
        checkParam(form.getBaseBean().getDydm1());

        // 検索条件パラメータ作成
        B1501W11SearchConditionDto conditionDto =
            createCondition(form.getKsscd(), form.getBaseBean());

        // 検索処理
        B1501W11SearchResultDto result = service.search(conditionDto);

        List<B1501W11MeisaiBean> meisaiBeanList =
            getMeisaiBean(dbManagementSession.getUnyoDate(), result);

        return meisaiBeanList;

    }

    /**
     * 検索条件DTOを生成する.
     * 
     * @param skySakiCd 請求先コード
     * @param bean 請求入金照会-基本情報Beanクラス
     * @return B1501W11SearchConditionDto B1501W01_請求入金照会検索条件DTO
     */
    private B1501W11SearchConditionDto createCondition(String skySakiCd, B1501W11BaseBean bean) {

        B1501W11SearchConditionDto dto = new B1501W11SearchConditionDto();

        // AP基本コードの設定
        dto.setApkihoncd(bean.getApkihoncd());
        // ショップコードの設定
        dto.setShopcd(bean.getShopcd());
        // 請求先コードの設定
        dto.setSkysakicd(skySakiCd);
        // 締年月
        if (!StringUtils.isEmpty(bean.getDydm1())) {
            dto.setDydm(DateTimeUtil.toNoSlashedDateYYYYMM(bean.getDydm1()));
        }

        return dto;
    }

    /**
     * B1501W11MeisaiBean設定.
     * 
     * @param unyoDt 運用日付
     * @param dto 検索結果Dto
     * @return List<B1501W11MeisaiBean>
     */
    private List<B1501W11MeisaiBean> getMeisaiBean(String unyoDt, B1501W11SearchResultDto dto) {

        List<B1501W11MeisaiBean> meisaiBeanList = new LinkedList<B1501W11MeisaiBean>();

        // 税計算区分算出
        String taxCls = getTaxCls(dto, unyoDt);

        // インボイス施行日と比較するための日付編集
        String targetDt = unyoDt.substring(0, 6).concat(CUR_END_DATE.equals(dto.getShimedd())
            ? DateTimeUtil.getLastDate(DateTimeUtil.toSlashedDateYYYYMM(unyoDt), dto.getShimedd())
            : dto.getShimedd());

        // 明細行の編集
        if (dto.getDetailList().size() != 0) {
            meisaiBeanList = getMeisaiBeanDetail(taxCls, targetDt, dto);
        }

        // 現在行の編集
        meisaiBeanList.add(getMeisaiBeanCurrent(taxCls, targetDt, dto));

        // 税計算区分の設定（消費税項目のラベル切替に使用)
        meisaiBeanList.get(0).setShohitaxkbnmei(taxCls); // 税計算区分

        return meisaiBeanList;
    }

    /**
     * 税計算区分取得.
     * 
     * @param dto 検索結果Dto
     * @param unyoDt 運用日付
     * @return 税計算区分
     */
    private String getTaxCls(B1501W11SearchResultDto dto, String UnyoDt) {

        // 請求先管理区分が卸(2)の場合
        if (CommonConstants.SKYSAKIKANRIKBN_2.equals(dto.getSkysakikanrikbn())) {
            // 外税(2)を返却
            return CommonConstants.KBN_TAXKEISAN_EX;
        }

        // 上記以外の場合
        // 運用日付>=税計算区分売上切替日の場合、新税計算区分売上
        // 運用日付<税計算区分売上切替日の場合、現税計算区分売上
        return ItemUtil.getItem(dto.getGenztaxkeisankbnuri(), dto.getShintaxkeisankbnuri(),
            dto.getTaxkeisankbnurikirikaedd(), UnyoDt);

    }

    /**
     * 明細行編集.
     * 
     * @param taxCls 税計算区分
     * @param targetDt 運用年月+締日
     * @param dto 請求入金照会検索結果DTO
     * @return 画面一覧情報
     */
    private List<B1501W11MeisaiBean> getMeisaiBeanDetail(String taxCls, String targetDt,
        B1501W11SearchResultDto dto) {

        List<B1501W11MeisaiBean> meisaiList = new ArrayList<>();
        List<B1501W11SearchResultListDto> detDtoList = dto.getDetailList();

        for (B1501W11SearchResultListDto detDto : detDtoList) {

            B1501W11MeisaiBean bean = new B1501W11MeisaiBean();

            BigDecimal taxInAmt = new BigDecimal(detDto.getKonkaitaxnukiurikin()); // 今回税抜売上金額
            BigDecimal tax = new BigDecimal(detDto.getKonkaishohitaxkin()); // 今回消費税金額

            // 締年月、締日はDB上のNotNullであり値の担保が取れるためnullチェックは不要(スラッシュ編集するので本来は必要)
            bean.setDydmmei(
                DateTimeUtil.toSlashedDate(detDto.getShimedydm() + detDto.getShimedd())); // 年月明細
            bean.setRgrayuryomei(detDto.getRgrayuryo()); // レギュラー有料
            bean.setRgramuryomei(detDto.getRgramuryo()); // レギュラー無料
            bean.setSrmyuryomei(detDto.getSrmyuryo()); // スリム有料
            bean.setSrmmuryomei(detDto.getSrmmuryo()); // スリム無料
            bean.setSonotayuryomei(detDto.getSonotayuryo()); // その他有料
            bean.setSonotamuryomei(detDto.getSonotamuryo()); // その他無料
            bean.setSeihinmizuurimei(detDto.getSeihinmizuuri()); // 製品水売上
            bean.setRtlrknmei(detDto.getRtlrkn()); // レンタル料金
            bean.setSonotaurimei(detDto.getSonotaurikin()); // その他売上
            bean.setTotsukiurikeimei(
                !CommonConstants.KBN_TAXKEISAN_IN.equals(taxCls) ? detDto.getKonkaitaxnukiurikin()
                    : String.valueOf(taxInAmt.add(tax))); // 当月売上計
            bean.setUchishohitaxkinmei(detDto.getKonkaishohitaxkin()); // 消費税金額
            bean.setTaxkomiurikeikinmei(detDto.getTaxkomiurikeikin()); // 当月税込売上
            bean.setMaedmzdmei(detDto.getZnkkurikoshikin()); // 前月残高
            bean.setTotsukinyknmei(detDto.getKonkainyknkin()); // 当月入金
            bean.setTotsukizdmei(detDto.getKurikoshikin()); // 当月残高
            bean.setKaishuhoho(detDto.getKaishuhohonm()); // 回収方法
            bean.setKaishuhohokbn(detDto.getKaishuhohokbn()); // 回収方法区分
            bean.setRenban(detDto.getRenban()); // 連番

            meisaiList.add(bean);
        }

        return meisaiList;
    }

    /**
     * 現在行編集.
     * 
     * @param taxCls 税計算区分
     * @param targetDt 運用年月+締日
     * @param dto 請求入金照会検索結果DTO
     * @return 画面一覧情報
     */
    private B1501W11MeisaiBean getMeisaiBeanCurrent(String taxCls, String targetDt,
        B1501W11SearchResultDto dto) {

        B1501W11MeisaiBean bean = initMeisaiBeanCurrent();

        B1501W11SearchResultCurrDto currDto = dto.getCurrentInfo();

        // 取得結果が空の場合、初期値のまま返却
        if (ObjectUtils.isEmpty(currDto)) {
            return bean;
        }

        // 当月残高の算出
        BigDecimal preBal = new BigDecimal(currDto.getKurikoshikin()); // 前月残高
        BigDecimal salAmtTaxIn = new BigDecimal(currDto.getTaxkomiurikeikin()); // 税込売上金額
        BigDecimal paymt = new BigDecimal(currDto.getNyknkin()); // 当月入金
        BigDecimal curBal = preBal.subtract(paymt); // 前月残高から当月入金を引いた額

        bean.setRgrayuryomei(currDto.getRgrayuryo()); // レギュラー有料
        bean.setRgramuryomei(currDto.getRgramuryo()); // レギュラー無料
        bean.setSrmyuryomei(currDto.getSrmyuryo()); // スリム有料
        bean.setSrmmuryomei(currDto.getSrmmuryo()); // スリム無料
        bean.setSonotayuryomei(currDto.getSonotayuryo()); // その他有料
        bean.setSonotamuryomei(currDto.getSonotamuryo()); // その他無料
        bean.setSeihinmizuurimei(currDto.getSeihinmizuuri()); // 製品水売上
        bean.setRtlrknmei(currDto.getRtlrkn()); // レンタル料金
        bean.setSonotaurimei(currDto.getSonotaurikin()); // その他売上
        bean.setTotsukiurikeimei(
            !CommonConstants.KBN_TAXKEISAN_IN.equals(taxCls) ? currDto.getKonkaitaxnukiurikin()
                : currDto.getTaxkomiurikeikin()); // 当月売上計
        bean.setUchishohitaxkinmei(currDto.getShohitaxkin()); // 消費税金額
        bean.setTaxkomiurikeikinmei(currDto.getTaxkomiurikeikin()); // 当月税込売上
        bean.setMaedmzdmei(currDto.getKurikoshikin()); // 前月残高
        bean.setTotsukinyknmei(currDto.getNyknkin()); // 当月入金
        bean.setTotsukizdmei(String.valueOf(curBal.add(salAmtTaxIn))); // 当月残高
        bean.setKaishuhohokbn(currDto.getKaishuhohokbn()); // 回収方法区分
        bean.setKaishuhoho(currDto.getKaishuhohonm()); // 回収方法名称

        return bean;
    }

    /**
     * 明細Bean初期化(現在行).
     * 
     * @return 明細Bean(現在行に合わせて初期化)
     */
    private B1501W11MeisaiBean initMeisaiBeanCurrent() {

        // 年月を「現在」、回収方法以外の項目を0で初期化
        B1501W11MeisaiBean meisaiList = new B1501W11MeisaiBean(CUR_YM, CUR_INI, CUR_INI, CUR_INI,
            CUR_INI, CUR_INI, CUR_INI, CUR_INI, CUR_INI, CUR_INI, CUR_INI, CUR_INI, CUR_INI,
            CUR_INI, CUR_INI, CUR_INI, CUR_INI, null, null, CUR_INI);

        return meisaiList;
    }

    /**
     * 相関チェックを実行する
     * 
     * @param dydm 年月（入力項目)
     */
    private void checkParam(String dydm) {

        // 年月に入力があった場合のみ相関チェック実施
        if (!StringUtils.isEmpty(dydm)) {
            // 年月が1900/01～9999/12の範囲外の場合、エラー
            // ※設計通りに実装しているが単項目チェックの日付妥当性チェックの正規表現で同様のチェックをしているため当該チェックは通らない
            if (!DateTimeUtil.isDateBetween(dydm, CommonConstants.YEARMONTH_MIN,
                CommonConstants.YEARMONTH_MAX)) {

                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.dydm1", "年月"), MessageID.W033, "年月",
                    CommonConstants.YEARMONTH_MIN, CommonConstants.YEARMONTH_MAX);
            }
        }
    }
}
