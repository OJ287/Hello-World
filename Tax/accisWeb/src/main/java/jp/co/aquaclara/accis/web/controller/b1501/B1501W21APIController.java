package jp.co.aquaclara.accis.web.controller.b1501;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.thymeleaf.util.ListUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.exception.BusinessException;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.web.controller.b1501.bean.B1501W21M1MeisaiBean;
import jp.co.aquaclara.accis.web.controller.b1501.bean.B1501W21M2MeisaiBean;
import jp.co.aquaclara.accis.web.controller.b1501.form.B1501W21Form;
import jp.co.aquaclara.accis.web.controller.b1501.session.B1501W00Session;
import jp.co.aquaclara.accis.web.controller.commons.AccisWebBaseController;
import jp.co.aquaclara.accis.web.controller.commons.AciRestResponse;
import jp.co.aquaclara.accis.web.controller.commons.DbManagementSession;
import jp.co.aquaclara.accis.web.controller.commons.UserSessionForm;
import jp.co.aquaclara.accis.web.controller.z9999.bean.Z9999W01JobStateBean;
import jp.co.aquaclara.accis.web.domain.dto.b1501.B1501W21M1SearchMeisaiDto;
import jp.co.aquaclara.accis.web.domain.dto.b1501.B1501W21M2SearchMeisaiDto;
import jp.co.aquaclara.accis.web.domain.dto.b1501.B1501W21PrintConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.b1501.B1501W21PrintMeisaiDto;
import jp.co.aquaclara.accis.web.domain.dto.b1501.B1501W21SearchConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.b1501.B1501W21SearchSkysakiDto;
import jp.co.aquaclara.accis.web.domain.dto.c0800.ComparableValueCurrentOrNew;
import jp.co.aquaclara.accis.web.domain.dto.c0800.HasConvertableNumericProperty;
import jp.co.aquaclara.accis.web.domain.dto.z0502.Z0502W01SkysakiDto;
import jp.co.aquaclara.accis.web.domain.service.b1501.B1501R21Service;
import jp.co.aquaclara.accis.web.domain.service.b1501.B1501W21Service;
import jp.co.aquaclara.accis.web.domain.service.c0800.UtilityService;
import jp.co.aquaclara.accis.web.domain.service.common.async.AsyncManagementService;
import jp.co.aquaclara.accis.web.domain.service.common.async.AsyncManagementServiceModel;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * B1501W21_請求入金照会（詳細）APIコントローラクラス.
 */
@RestController
@SessionAttributes(types = {DbManagementSession.class})
@RequestMapping("/api/b1501w21")
@Slf4j
public class B1501W21APIController extends AccisWebBaseController
    implements ComparableValueCurrentOrNew, HasConvertableNumericProperty {

    /**
     * 非同期処理管理（受付）サービス
     */
    @Autowired
    private AsyncManagementService<B1501W21PrintConditionDto> asyncManagementService;
    /**
     * 請求入金明細サービス
     */
    @Autowired
    private B1501R21Service b1501r21Service;
    /**
     * 請求入金照会セッション情報
     */
    @Autowired
    private B1501W00Session b1501w00Session;
    /**
     * 請求入金照会(詳細)サービス
     */
    @Autowired
    private B1501W21Service b1501w21Service;

    /** ユーティリティ */
    @Autowired
    private UtilityService util;

    /**
     * ユーザセッション情報
     */
    @Autowired
    private UserSessionForm userSessionForm;

    /** 1ページあたりの表示上限件数 */
    private final int PAGEABLE_SIZE = 500;

    /**
     * 明細１取得処理.
     * 
     * @param form B1501W21Form
     * @param dbManagementSession DbManagementSession
     * @param pageable Pageable
     * @return Page<B1501W21M1MeisaiBean>
     */
    @GetMapping("/search1")
    public Page<B1501W21M1MeisaiBean> search1(@ModelAttribute("b1501W21Form") B1501W21Form form,
        @ModelAttribute(name = "dbManagementSession_B1501W21",
            binding = false) DbManagementSession dbManagementSession,
        @PageableDefault(size = PAGEABLE_SIZE) Pageable pageable) {

        // 検索条件DTO生成
        B1501W21SearchConditionDto dto = createCondition(form, null, dbManagementSession);
        // 検索結果取得と検索結果DTO生成
        List<B1501W21M1MeisaiBean> meisaiBeanList =
            getInitM1MeisaiBeanList(b1501w21Service.searchMeisai1(dto),
                form.getBaseBean().getDydm(), form.getBaseBean().getShohitaxkomokumei());
        // 検索結果をセッションに設定
        b1501w00Session.setM1MeisaiBeanList(meisaiBeanList);

        int totalRecordCount = meisaiBeanList.size();
        if (meisaiBeanList.size() > 0) {
            meisaiBeanList = super.selectPage(pageable, meisaiBeanList);
        }
        Page<B1501W21M1MeisaiBean> result =
            new PageImpl<>(meisaiBeanList, pageable, totalRecordCount);

        return result;
    }

    /**
     * 明細２取得処理.
     * 
     * @param form B1501W21Form
     * @param pageable Pageable
     * @param dbManagementSession DbManagementSession
     * @param response HttpServletResponse
     * @return Page<B1501W21M2MeisaiBean>
     */
    @SuppressWarnings("unchecked")
    @GetMapping("/search2")
    public Page<B1501W21M2MeisaiBean> search2(@ModelAttribute("b1501W21Form") B1501W21Form form,
        @ModelAttribute(name = "dbManagementSession_B1501W21",
            binding = false) DbManagementSession dbManagementSession,
        @PageableDefault(size = PAGEABLE_SIZE) Pageable pageable, HttpServletResponse response) {
        // 初期表示処理かつ明細が存在する場合、相関チェック兼得意先コードリスト生成
        List<String> tokuisakicdList = new ArrayList<>();
        if (!CommonConstants.FLG_ON.equals(form.getBaseBean().getShokihyojiflag())
            && !ListUtils.isEmpty(b1501w00Session.getM1MeisaiBeanList())) {
            tokuisakicdList = checkParam(b1501w00Session.getM1MeisaiBeanList());
        }

        // 検索要DTO生成
        B1501W21SearchConditionDto dto =
            createCondition(form, tokuisakicdList, dbManagementSession);
        log.debug("検索用DTO : {}", dto);

        // 検索処理取得と検索結果DTO生成
        List<B1501W21M2MeisaiBean> meisaiBeanList =
            getInitM2MeisaiBeanList(b1501w21Service.searchMeisai2(dto),
                form.getBaseBean().getDydm(), dto.getTaxkeisankbn(), dbManagementSession);
        // 最大表示件数オーバーの場合、ステータスコードを206とする
        meisaiBeanList = (List<B1501W21M2MeisaiBean>) chkLength(meisaiBeanList, response);

        int totalRecordCount = meisaiBeanList.size();
        meisaiBeanList = super.selectPage(pageable, meisaiBeanList);
        Page<B1501W21M2MeisaiBean> result =
            new PageImpl<>(meisaiBeanList, pageable, totalRecordCount);

        return result;
    }

    /**
     * 印刷処理.
     * 
     * @param form B1501W21Form
     * @param result BindingResult
     * @param dbManagementSession DbManagementSession
     * @return AciRestResponse<Z9999W01JobStateBean>
     */
    @PutMapping("/print")
    public AciRestResponse<Z9999W01JobStateBean> print(@Validated @RequestBody B1501W21Form form,
        BindingResult result, @ModelAttribute(name = "dbManagementSession_B1501W21",
            binding = false) DbManagementSession dbManagementSession) {

        // 請求先共通ヘッダ同等情報の取得
        B1501W21SearchSkysakiDto skysakiDto = createSkysakiCondition(form, dbManagementSession);

        // 印刷処理に渡すDTO生成
        B1501W21PrintConditionDto printConditionDto =
            createPrintCondition(form, skysakiDto, dbManagementSession);
        // 相関チェック
        b1501r21Service.processBeforeCheck(printConditionDto);

        // 非同期処理の受付用パラメータ設定
        AsyncManagementServiceModel<B1501W21PrintConditionDto> asyncManagementServiceModel =
            new AsyncManagementServiceModel<>(printConditionDto, this.userSessionForm,
                dbManagementSession, b1501r21Service);
        try {
            // 非同期業務処理実行＆受付ID取得
            Z9999W01JobStateBean jobStateBean =
                asyncManagementService.entryAsyncService(asyncManagementServiceModel);

            // 取得した受付IDをファイルダウンロード画面へのパラメータとして設定する
            return new AciRestResponse<>(jobStateBean);
        } catch (BusinessException be) {
            // 業務例外の場合、エラーメッセージを画面表示する
            throw BusinessExceptionFactory.validationFailed(MessageID.W260, be.getMessage());
        } catch (Exception e) {
            // システム例外の場合、エラーメッセージを画面表示する
            throw BusinessExceptionFactory.validationFailed(MessageID.E002);
        }
    }

    /**
     * 明細行変更処理
     * 
     * @param m1MeisaiBean B1501W21MeisaiBean
     * @param index 変更対象の明細番号
     * @return AciRestResponse<Void>
     */
    @PutMapping("/changerow/{index}")
    public AciRestResponse<Void> changeRow(@RequestBody B1501W21M1MeisaiBean m1MeisaiBean,
        @PathVariable("index") int index) {
        // セッションで保持する明細行の入力項目を変更する
        B1501W21M1MeisaiBean session = b1501w00Session.getM1MeisaiBeanList().get(index);

        session.setSentakumei(m1MeisaiBean.getSentakumei()); // 選択

        // 更新結果の返却
        return new AciRestResponse<>(null);
    }

    /**
     * セッションクリア処理
     * 
     * @return AciRestResponse<Void>
     */
    @PutMapping("/clearsession")
    public AciRestResponse<Void> clearSession() {
        // セッションで保持する明細行をクリア
        b1501w00Session.setM1MeisaiBeanList(null);

        // 更新結果の返却
        return new AciRestResponse<>(null);
    }

    /**
     * 検索用DTO生成
     * 
     * @param form 請求入金照会(詳細)Form
     * @param tokuisakicdList 選択された得意先コード
     * @param dbManagementSession DbManagementSession
     * @return B1501W21SearchConditionDto
     */
    private B1501W21SearchConditionDto createCondition(B1501W21Form form,
        List<String> tokuisakicdList, DbManagementSession dbManagementSession) {
        return new B1501W21SearchConditionDto(form.getBaseBean().getApkihoncd(), // AP基本コード
            form.getBaseBean().getShopcd(), // ショップコード
            form.getKsscd(), // 請求先コード
            tokuisakicdList, // 得意先コードリスト
            // 未締(現在)の場合はシステム日付、締済(締め年月日あり)の場合は締め年月日を設定
            StringUtils.isEmpty(form.getBaseBean().getDydm()) ? DateTimeUtil.getSystemDate()
                : form.getBaseBean().getDydm(), // 年月日
            form.getBaseBean().getKaishuhohokbn(), // 回収方法区分
            StringUtils.isEmpty(form.getBaseBean().getRenban()) ? ""
                : form.getBaseBean().getRenban(), // 連番
            form.getBaseBean().getUrinykntrkssentaku(), // 売上入金取消チェック
            dbManagementSession.getUnyoDate(), // 運用日付
            "", // 税計算区分
            "", // 締日
            "", // 税端数計算区分
            "", // インボイス施行日
            // 未締(現在)の場合はフラグON、締済(締め年月日あり)の場合はフラグOFFを設定
            StringUtils.isEmpty(form.getBaseBean().getDydm()) ? CommonConstants.FLG_ON
                : CommonConstants.FLG_OFF, // 年月日フラグ
            form.getBaseBean().getShokihyojiflag(), // 初期表示フラグ
            loginSession.getMaxDisplayCount() // 最大表示件数
        );
    }

    /**
     * B1501W21M1MeisaiBean設定.
     * 
     * @param meisaiDtoList 請求入金照会(詳細)検索結果(明細エリア)リスト
     * @param dydmdd 処理対象年月日
     * @param shohitaxkomoku 消費税項目
     * @return List<B1501W21M1MeisaiBean>
     */
    private List<B1501W21M1MeisaiBean> getInitM1MeisaiBeanList(
        List<B1501W21M1SearchMeisaiDto> meisaiDtoList, String dydmdd, String shohitaxkomoku) {
        List<B1501W21M1MeisaiBean> meisaiBeanList = new LinkedList<B1501W21M1MeisaiBean>();

        if (!ObjectUtils.isEmpty(meisaiDtoList) && meisaiDtoList.size() > 0) {
            for (B1501W21M1SearchMeisaiDto meisaiDto : meisaiDtoList) {
                meisaiBeanList.add(new B1501W21M1MeisaiBean(DateTimeUtil.toSlashedDate(dydmdd), // 年月日
                    meisaiDto.getTokuisakijotainm(), // 得意先状態名
                    meisaiDto.getJotaistsnm(), // 状態ステータス名
                    meisaiDto.getSkysakicd(), // 請求先コード
                    meisaiDto.getSkysakinm(), // 請求先名称
                    meisaiDto.getJigyoshocd(), // 事業所コード
                    meisaiDto.getJigyoshonm(), // 事業所名称
                    meisaiDto.getZnkskykin(), // 前回ご請求額
                    meisaiDto.getNyukinkin(), // ご入金額
                    // 消費税項目が「1:内税」の場合は今回利用金額+消費税金額、それ以外の場合は今回利用金額を設定
                    CommonConstants.KBN_TAXKEISAN_IN.equals(shohitaxkomoku)
                        ? getString(getIntValue(meisaiDto.getKonkairiyokin())
                            + getIntValue(meisaiDto.getShohitaxkin()))
                        : meisaiDto.getKonkairiyokin(), // 今回ご利用額
                    meisaiDto.getShohitaxkin(), // (内)消費税額
                    meisaiDto.getTaxkomiskyzkin(), // 税込ご利用額
                    meisaiDto.getN8perzeikomiriyogaku(), // 8%税込ご利用額
                    meisaiDto.getN10perzeikomiriyogakumei(), // 10%税込ご利用額
                    meisaiDto.getKonkaiskykinmei(), // 今回請求金額
                    meisaiDto.getSentaku(), // 選択
                    CommonConstants.YUKOKBN_1, // 有効区分
                    meisaiDto.getSentaku() // 検索時選択フラグ
                ));
            }
        }

        return meisaiBeanList;
    }

    /**
     * 相関チェック
     * 
     * @param meisaiBeanList 請求入金照会(詳細)明細エリアデータリスト
     * @return 処理対象得意先コードリスト
     */
    private List<String> checkParam(List<B1501W21M1MeisaiBean> meisaiBeanList) {
        int cnt = 0;
        int index = 0;
        List<String> tokuisakicdList = new ArrayList<>();
        // 明細エリアデータ件数分処理を繰り返し
        for (B1501W21M1MeisaiBean meisaiBean : meisaiBeanList) {
            // 選択チェックボックスにチェックがある場合
            if (CommonConstants.FLG_ON.equals(meisaiBean.getSentakumei())) {
                // 請求先コードに値が存在し、返却リストに同請求先が存在しない場合、返却リストに請求先コードを設定
                if (!StringUtils.isEmpty(meisaiBean.getSkysakicdmei())
                    && !tokuisakicdList.contains(meisaiBean.getSkysakicdmei())) {
                    tokuisakicdList.add(meisaiBean.getSkysakicdmei());
                }
                cnt++;
            }
            // 検索時選択フラグを更新
            B1501W21M1MeisaiBean session = b1501w00Session.getM1MeisaiBeanList().get(index);
            session.setSearchcheckflag(CommonConstants.FLG_ON);
            index++;
        }
        // リスト件数が0件の場合、未選択エラー
        if (cnt == 0) {
            throw BusinessExceptionFactory.validationFailed(MessageID.W091, "選択");
        }
        return tokuisakicdList;
    }

    /**
     * B1501W21M2MeisaiBean設定.
     * 
     * @param meisaiDtoList 請求入金照会(詳細)検索結果(明細エリア)リスト
     * @param dydmdd 処理対象年月日
     * @param shohitaxkomoku 消費税項目
     * @param dbManagementSession DbManagementSession
     * @return List<B1501W21M2MeisaiBean>
     */
    private List<B1501W21M2MeisaiBean> getInitM2MeisaiBeanList(
        List<B1501W21M2SearchMeisaiDto> meisaiDtoList, String dydmdd, String shohitaxkomoku,
        DbManagementSession dbManagementSession) {
        return meisaiDtoList.stream().map(meisaiDto -> {
            boolean isNotDeposit = StringUtils.isEmpty(meisaiDto.getNyukinkindnm());
            boolean isNotTaxData = StringUtils.isEmpty(meisaiDto.getTaxflag());
            B1501W21M2MeisaiBean meisaiBean = new B1501W21M2MeisaiBean();
            // 日付
            meisaiBean.setYmdmei(DateTimeUtil.toSlashedDate(meisaiDto.getYmd()));
            // 伝票番号
            meisaiBean.setDnpynomei(meisaiDto.getPyno());
            // 枝番号
            meisaiBean.setEdanomei(meisaiDto.getEdano());
            // 商品コード
            meisaiBean.setShncdmei(meisaiDto.getShncd());
            // 商品名称略称（入金以外：商品名称略称 / 入金：入金種別名称）
            meisaiBean.setShnnmryakushomei(
                isNotDeposit
                    ? getCurrentValue(meisaiDto.getGenzshnnmryakusho(),
                        meisaiDto.getShinshnnmryakusho(), meisaiDto.getShnnmkirikaedd(),
                        dbManagementSession.getUnyoDate())
                    : meisaiDto.getNyukinkindnm());
            // 計上区分
            meisaiBean.setKeijokbnnmmei(meisaiDto.getKeijokbnnm());
            // 数量
            meisaiBean.setSuryomei(meisaiDto.getSuryo());
            // 売上取扱単位
            meisaiBean.setTanimei(meisaiDto.getUritrtskitaninm());
            // 売上入数
            meisaiBean.setIrisu(meisaiDto.getUriirisu());
            // 総数
            meisaiBean.setSokazu(meisaiDto.getSokazu());
            // 単価（入金、消費税データでない場合のみ設定）
            meisaiBean.setUntmei(isNotDeposit && isNotTaxData ? meisaiDto.getUnt() : null);
            // 売上金額（入金、消費税データでない場合のみ設定）
            if (isNotDeposit && isNotTaxData) {
                meisaiBean.setUrikinmei(meisaiDto.getKin());
            }
            // 税率
            meisaiBean.setTaxratemei(meisaiDto.getTaxrate());
            // 消費税金額（入金でない場合のみ設定）
            meisaiBean.setShohitaxkin2mei(isNotDeposit ? meisaiDto.getShohitaxkin() : null);
            // 入金金額（入金のみ設定）
            meisaiBean.setNyknkin2mei(isNotDeposit ? null : meisaiDto.getNyknkin());
            // 備考
            meisaiBean.setBikomei(meisaiDto.getBiko());
            // 売上ポイント
            meisaiBean.setUripoint(isNotDeposit && isNotTaxData ? meisaiDto.getUripoint() : null);
            // 特別ポイント
            meisaiBean.setTkbtpoint(isNotDeposit && isNotTaxData ? meisaiDto.getTkbtpoint() : null);
            // ポイント内訳
            meisaiBean.setPointutwk(isNotDeposit && isNotTaxData ? meisaiDto.getPointutwk() : null);
            // 合計ポイント
            meisaiBean.setSmpoint(isNotDeposit && isNotTaxData ? meisaiDto.getSmpoint() : null);
            // ポイント連携日付
            meisaiBean.setPointrenkeidd(isNotDeposit && isNotTaxData ? DateTimeUtil.toSlashedDate(meisaiDto.getPointrenkeidd()) : null);
            // 色名称
            meisaiBean.setIronmmei(meisaiDto.getIro());
            // サイズ名称
            meisaiBean.setSznmmei(meisaiDto.getSznm());
            // 型式
            meisaiBean.setKatasikimei(meisaiDto.getKatasiki());
            // 得意先コード
            meisaiBean.setTokuisakicdmei(meisaiDto.getTokuisakicd());
            // 得意先名称
            meisaiBean.setTokuisakinmmei(meisaiDto.getTokuisakinmn1());
            // 登録システム日付（スラッシュ補完）
            meisaiBean.setInptsystmymdmei(DateTimeUtil.toSlashedDate(meisaiDto.getInptsystmymd()));
            // 登録システム時刻（コロン補完）
            meisaiBean.setInptsystmjikanmei(
                DateTimeUtil.toColonedDateHHMMSS(meisaiDto.getInptsystmjikan()));
            // 登録担当者AP基本コード
            meisaiBean.setToshaapkihoncdmei(meisaiDto.getToshaapkihoncd());
            // 登録担当者ショップコード
            meisaiBean.setToshashopcdmei(meisaiDto.getToshashopcd());
            // 登録担当者コード
            meisaiBean.setToshacdmei(meisaiDto.getToshacd());
            // 登録担当者名
            meisaiBean.setToshameimei(meisaiDto.getTnshamei());
            // 請求書出力有無区分名
            meisaiBean.setSkyshootptarinashikbnmei(meisaiDto.getSkyshootptarinashikbn());
            // 消費税フラグ
            meisaiBean.setTaxflag(meisaiDto.getTaxflag());
            // 印刷可能フラグ（現在表示の場合：印刷不可 / 年月指定の場合：印刷可能）
            meisaiBean.setInsatable(
                StringUtils.isEmpty(dydmdd) ? CommonConstants.FLG_OFF : CommonConstants.FLG_ON);
            // 軽減税率売上金額合計
            meisaiBean.setN8peruri(getString(meisaiDto.getN8peruri()));
            // 軽減税率消費税金額合計
            meisaiBean.setN8pershohzei(getString(meisaiDto.getN8pershohzei()));
            // 一般税率売上金額合計
            meisaiBean.setN10peruri(getString(meisaiDto.getN10peruri()));
            // 一般税率消費税金額合計
            meisaiBean.setN10pershohzei(getString(meisaiDto.getN10pershohzei()));
            // 売上金額合計
            meisaiBean.setKinsm1(getString(meisaiDto.getKinsm1()));
            // 消費税金額合計
            meisaiBean.setKinsm2(getString(meisaiDto.getKinsm2()));
            // 入金金額合計
            meisaiBean.setKinsm3(getString(meisaiDto.getKinsm3()));
            // ポイント合計
            meisaiBean.setPointsm(getString(meisaiDto.getPointsm()));
            return meisaiBean;
        }).collect(util.toList());
    }

    /**
     * 請求先共通ヘッダ同等情報取得処理
     * 
     * @param form B1501W21Form
     * @param dbManagementSession DbManagementSession
     * @return 請求先共通ヘッダ検索条件DTO
     */
    private B1501W21SearchSkysakiDto createSkysakiCondition(B1501W21Form form,
        DbManagementSession dbManagementSession) {
        // 請求先共通ヘッダ検索条件DTOを生成
        Z0502W01SkysakiDto conditionDto =
            Z0502W01SkysakiDto.builder().apkihoncd(form.getBaseBean().getApkihoncd()) // AP基本コード
                .shopcd(form.getBaseBean().getShopcd()) // ショップコード
                .skysakicd(form.getKsscd()) // 請求先コード
                .unyodate(dbManagementSession.getUnyoDate()) // 運用日付
                .build();

        return b1501w21Service.searchSkysaki(conditionDto);
    }

    /**
     * 請求入金照会(詳細)印刷処理受渡用DTO生成.
     * 
     * @param form B1501W21Form
     * @param dto 請求先共通ヘッダ検索条件DTO
     * @param dbManagementSession DbManagementSession
     * @return 請求入金照会(詳細)印刷処理受渡用DTO
     */
    private B1501W21PrintConditionDto createPrintCondition(B1501W21Form form,
        B1501W21SearchSkysakiDto dto, DbManagementSession dbManagementSession) {
        B1501W21PrintConditionDto printConditionDto = new B1501W21PrintConditionDto();
        // 税計算区分
        // 請求先管理区分が「2:卸」の場合は「2:外税」、それ以外の場合は税計算区分切替日に沿った税計算区分を設定
        printConditionDto
            .setTaxkeisankbn(CommonConstants.SKYSAKIKANRIKBN_2.equals(dto.getSkysakikanrikbn())
                ? CommonConstants.KBN_TAXKEISAN_EX
                : getCurrentValue(dto.getGenztaxkeisankbnuri(), dto.getShintaxkeisankbnuri(),
                    dto.getTaxkeisankbnurikirikaedd(), dbManagementSession.getUnyoDate()));
        // 年月日フラグ
        // 未締(現在)の場合はフラグON、締済(締め年月日あり)の場合はフラグOFFを設定
        printConditionDto.setDydmddflag(
            StringUtils.isEmpty(form.getBaseBean().getDydm()) ? CommonConstants.FLG_ON
                : CommonConstants.FLG_OFF);
        // 明細データ
        printConditionDto.setMeisaidata(createPrintMeisaidata(b1501w00Session.getM1MeisaiBeanList(),
            form.getBaseBean().getApkihoncd(), form.getBaseBean().getShopcd()));
        // AP基本コード
        printConditionDto.setApkihoncd(form.getBaseBean().getApkihoncd());
        // ショップコード
        printConditionDto.setShopcd(form.getBaseBean().getShopcd());
        // ショップ名称
        printConditionDto.setShopnm(form.getBaseBean().getShopnm());
        // 請求先コード
        printConditionDto.setSkysakicd(form.getKsscd());
        // 印刷ボタン利用フラグ
        printConditionDto.setInsatflag(form.getFooterBean().getInsatflag());
        // 担当者コード
        printConditionDto.setTnshacd(userSessionForm.getTnshacd());
        // 担当者名
        printConditionDto.setTnshamei(userSessionForm.getTnshamei());
        // 請求先名称
        printConditionDto.setSkysakinmn(dto.getSkysakinmn());
        // 請求区分名称
        printConditionDto.setSkykbnnm(dto.getSkykbnnm());
        // 締日
        printConditionDto.setShimedd(dto.getSkyshimedd());
        // 支払サイト
        printConditionDto.setShrsaito(dto.getShrsaito());
        // 現在残高
        printConditionDto.setGenzaizandaka(dto.getGenzaizandaka());
        // 滞留金額
        printConditionDto.setTairyukin(dto.getTairyukin());
        // 請求先郵便番号
        printConditionDto.setSkysakiyubinno(dto.getYubinno());
        // 住所１
        printConditionDto.setAdrs1(dto.getAdrsn1());
        // 住所２
        printConditionDto.setAdrs2(dto.getAdrsn2());
        // 住所３
        printConditionDto.setAdrs3(dto.getAdrsn3());
        // 外部連携コード
        printConditionDto.setGaiburenkeicd(dto.getGaiburenkeicd());
        // 営業担当者名
        printConditionDto.setEigyotnshamei(dto.getEigyotnshamei());
        // 請求先カナ名称
        printConditionDto.setSkysakinmkana(dto.getSkysakinmkana());
        // 回収方法区分名称
        printConditionDto.setKaishuhohokbnnm(dto.getKaishuhohokbnnm());
        // 連絡先番号１
        printConditionDto.setRenrakusakinon(dto.getRenrakusakinon());
        // 支払日
        printConditionDto.setShrdd(dto.getShrdd());
        // 売上入金取消選択
        printConditionDto.setUriagenyukincnclsentaku(form.getBaseBean().getUrinykntrkssentaku());
        // 回収方法区分
        printConditionDto.setKaishuhohokbn(form.getBaseBean().getKaishuhohokbn());
        // インボイス施行日
        printConditionDto.setInvoicshikoymd(dto.getInvoicshikoymd());
        // 税端数計算区分
        printConditionDto.setTaxhasukeisankbn(dto.getTaxhasukeisankbn());
        // 運用年月
        printConditionDto.setUnyodydm(dbManagementSession.getUnyoDate().substring(0, 6));
        // 連番
        printConditionDto.setRenban(form.getBaseBean().getRenban());

        return printConditionDto;
    }

    /**
     * 請求入金照会(詳細)印刷処理受渡用明細DTO生成.
     * 
     * @param meisaiBeanList 請求入金照会(詳細)検索結果(明細エリア)リスト
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @return 請求入金照会(詳細)印刷処理受渡用明細DTO
     */
    private List<B1501W21PrintMeisaiDto> createPrintMeisaidata(
        List<B1501W21M1MeisaiBean> meisaiBeanList, String apkihoncd, String shopcd) {
        List<B1501W21PrintMeisaiDto> meisaiDtoList = new ArrayList<>();
        int cnt = 0;
        // 明細エリアリストの件数分処理を繰り返し
        for (B1501W21M1MeisaiBean meisaiBean : meisaiBeanList) {
            B1501W21PrintMeisaiDto meisaiDto = new B1501W21PrintMeisaiDto();
            // 明細一行目のデータ、もしくは検索処理実施時にチェックありだったデータのみリストに設定
            if (cnt == 0 || CommonConstants.FLG_ON.equals((meisaiBean.getSearchcheckflag()))) {
                // 年月日
                meisaiDto.setDydmmei(DateTimeUtil.toNoSlashedDate(meisaiBean.getDydmmei()));
                // 前月残高
                meisaiDto.setZngtzndkmei(meisaiBean.getZnkskykinmei());
                // 当月入金額
                meisaiDto.setTogtnyknkinmei(meisaiBean.getNyknkin1mei());
                // 当月売上額
                meisaiDto.setTogturagkinmei(meisaiBean.getKonkairiyokinmei());
                // 消費税額
                meisaiDto.setShohitaxkinmei(meisaiBean.getShohitaxkin1mei());
                // 請求金額
                meisaiDto.setSkykinmei(meisaiBean.getKonkaiskykinmei());
                // 得意先コード
                meisaiDto.setTokuisakicdmei(meisaiBean.getSkysakicdmei());
                // 事業所コード
                meisaiDto.setJigyoshocdmei(meisaiBean.getJigyoshocdmei());
                // 事業所名称
                meisaiDto.setJigyoshonmmei(meisaiBean.getJigyoshonmmei());

                meisaiDtoList.add(meisaiDto);
            }
            cnt++;
        }
        return meisaiDtoList;
    }
}
