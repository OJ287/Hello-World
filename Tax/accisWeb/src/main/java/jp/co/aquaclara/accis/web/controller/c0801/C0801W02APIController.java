package jp.co.aquaclara.accis.web.controller.c0801;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.exception.BusinessException;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.controller.c0801.bean.C0801W02BaseBean;
import jp.co.aquaclara.accis.web.controller.c0801.bean.C0801W02FooterBean;
import jp.co.aquaclara.accis.web.controller.c0801.bean.C0801W02MeisaiBean;
import jp.co.aquaclara.accis.web.controller.c0801.bean.C0801W02MeisaiDetailBean;
import jp.co.aquaclara.accis.web.controller.c0801.bean.price.C0801W02SwitchableShnPriceBean;
import jp.co.aquaclara.accis.web.controller.c0801.bean.price.C0802W01RangeShnPriceBean;
import jp.co.aquaclara.accis.web.controller.c0801.form.C0801W02Form;
import jp.co.aquaclara.accis.web.controller.c0801.session.C0801W02Session;
import jp.co.aquaclara.accis.web.controller.commons.AccisWebBaseController;
import jp.co.aquaclara.accis.web.controller.commons.AciRestResponse;
import jp.co.aquaclara.accis.web.controller.commons.DbManagementSession;
import jp.co.aquaclara.accis.web.controller.commons.UserSessionForm;
import jp.co.aquaclara.accis.web.controller.parts.master.bean.CommonPartsListBean;
import jp.co.aquaclara.accis.web.controller.z9999.bean.Z9999W01JobStateBean;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02AcptDlvryDetailDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02AcptDlvryDetailDto.DETAIL_TYPE;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02AcptDlvryDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02DlvryAreaDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02HaifubutsuDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02ProcessingContextDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02ProcessingResultDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02SettingDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02SettingDto.PRCSKBN;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02SettingOptionDto;
import jp.co.aquaclara.accis.web.domain.dto.c0809.C0809R40PrintContentsDto;
import jp.co.aquaclara.accis.web.domain.service.c0800.UtilityService;
import jp.co.aquaclara.accis.web.domain.service.c0801.w02.C0801W02AcptDlvryDetailFindService;
import jp.co.aquaclara.accis.web.domain.service.c0801.w02.C0801W02AcptDlvryFindService;
import jp.co.aquaclara.accis.web.domain.service.c0801.w02.C0801W02AcptDlvryPrePrintService;
import jp.co.aquaclara.accis.web.domain.service.c0801.w02.C0801W02AcptDlvryRegistrationService;
import jp.co.aquaclara.accis.web.domain.service.c0801.w02.C0801W02DlvryService;
import jp.co.aquaclara.accis.web.domain.service.c0801.w02.C0801W02SysMngService;
import jp.co.aquaclara.accis.web.domain.service.c0801.w02.C0801W02TokuisakiService;
import jp.co.aquaclara.accis.web.domain.service.c0809.C0809R40Service;
import jp.co.aquaclara.accis.web.domain.service.common.async.AsyncManagementService;
import jp.co.aquaclara.accis.web.domain.service.common.async.AsyncManagementServiceModel;
import jp.co.aquaclara.accis.web.domain.service.z0501.Z0501W01Service;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;

/**
 * C0801W02_受注登録（受注登録）APIコントローラクラス.
 */
@RestController
@RequestMapping("/api/c0801w02")
@SessionAttributes({"dbManagementSession_C0801W02"})
@Validated
public class C0801W02APIController extends AccisWebBaseController {

    /** 配達サービス */
    @Autowired
    private C0801W02DlvryService dlvryService;

    /** システム管理マスタサービス */
    @Autowired
    private C0801W02SysMngService sysMngService;

    /** 受注配達検索サービス */
    @Autowired
    private C0801W02AcptDlvryFindService finder;

    /** 受注配達明細検索サービス */
    @Autowired
    private C0801W02AcptDlvryDetailFindService detailFinder;

    /** 受注配達登録サービス */
    @Autowired
    private C0801W02AcptDlvryRegistrationService register;

    /** 受注配達印刷前サービス */
    @Autowired
    private C0801W02AcptDlvryPrePrintService prePrintService;

    /** 得意先サービス */
    @Autowired
    private C0801W02TokuisakiService tokuisakiService;

    /** ユーザセッション情報 */
    @Autowired
    private UserSessionForm userSessionForm;

    /** 配達伝票出力サービス */
    @Autowired
    private C0809R40Service reportService;

    /** 非同期処理管理(受付)サービス */
    @Autowired
    private AsyncManagementService<C0809R40PrintContentsDto> asyncManagementService;

    /** Utilityサービス */
    @Autowired
    private UtilityService util;

    /** 得意先共通サービス */
    @Autowired
    private Z0501W01Service kttService;

    /** 受注登録セッション情報 */
    @Autowired
    private C0801W02Session session;

    /**
     * 初期表示（得意先確定）の際に行う得意先に関する相関チェック
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     */
    @GetMapping("tokuisakicheck/{apkihoncd}/{shopcd}/{tokuisakicd}")
    public void checkTokuisakiForInit(@PathVariable("apkihoncd") String apkihoncd,
        @PathVariable("shopcd") String shopcd, @PathVariable("tokuisakicd") String tokuisakicd) {
        kttService.checkForC0801W00(apkihoncd, shopcd, tokuisakicd);
    }

    /**
     * 更新後に印刷前チェック処理
     * 
     * @param form フォーム
     * @param dbManagementSession DB管理セッション情報
     * @return ワーニングメッセージリスト
     */
    @PutMapping("printcheck")
    public List<String> checkBeforePrint(@RequestBody @Validated C0801W02Form form,
        @SessionAttribute("dbManagementSession_C0801W02") DbManagementSession dbManagementSession) {
        C0801W02SettingDto settingDto = createDtoFromForm(form, dbManagementSession);
        reloadMasterData(settingDto);
        C0801W02SettingOptionDto option = settingDto.getOption();
        // 伝票ステータスにより受注予約データかどうか判定し、処理種別を設定
        option.setProcessType(C0801W02SettingOptionDto.getProcessTypeForUpdate(
            CommonConstants.DNPYSTS_01.equals(form.getBaseBean().getDnpysts()),
            settingDto.getPrcskbn()));
        // 受注締め処理を行うようにオプション設定
        option.setClosing(true);
        // チェック処理実行
        return register.checkOnly(settingDto);
    }


    /**
     * 更新前チェック処理
     * 
     * @param form フォーム
     * @param dbManagementSession DB管理セッション情報
     * @return ワーニングメッセージリスト
     */
    @PutMapping("check")
    public List<String> checkBeforeUpdate(@RequestBody @Validated C0801W02Form form,
        @SessionAttribute("dbManagementSession_C0801W02") DbManagementSession dbManagementSession) {
        C0801W02SettingDto settingDto = createDtoFromForm(form, dbManagementSession);
        reloadMasterData(settingDto);
        C0801W02SettingOptionDto option = settingDto.getOption();
        // 伝票ステータスにより受注予約データかどうか判定し、処理種別を設定
        option.setProcessType(C0801W02SettingOptionDto.getProcessTypeForUpdate(
            CommonConstants.DNPYSTS_01.equals(form.getBaseBean().getDnpysts()),
            settingDto.getPrcskbn()));
        // チェック処理実行
        return register.checkOnly(settingDto);
    }

    /**
     * 更新
     * 
     * @param form フォーム
     * @param dbManagementSession DB管理セッション情報
     * @return レスポンス
     */
    @PutMapping("update")
    public AciRestResponse<C0801W02Form> doUpdate(@RequestBody @Validated C0801W02Form form,
        @SessionAttribute("dbManagementSession_C0801W02") DbManagementSession dbManagementSession) {
        // レスポンスを生成
        AciRestResponse<C0801W02Form> response = new AciRestResponse<>(form);
        // DTOをFormから生成
        C0801W02SettingDto settingDto = createDtoFromForm(form, dbManagementSession);
        reloadMasterData(settingDto);
        C0801W02SettingOptionDto option = settingDto.getOption();
        // 伝票ステータスにより受注予約データかどうか判定し、処理種別を設定
        option.setProcessType(C0801W02SettingOptionDto.getProcessTypeForUpdate(
            CommonConstants.DNPYSTS_01.equals(form.getBaseBean().getDnpysts()),
            settingDto.getPrcskbn()));
        // 登録処理実行
        register.regist(settingDto);
        return response;
    }

    /**
     * 更新後に印刷
     * 
     * @param form フォーム
     * @param dbManagementSession DB管理セッション
     * @return レスポンス
     */
    @PutMapping("print")
    public AciRestResponse<Z9999W01JobStateBean> doPrint(@RequestBody @Validated C0801W02Form form,
        @SessionAttribute("dbManagementSession_C0801W02") DbManagementSession dbManagementSession) {
        // DTOをFormから生成
        C0801W02SettingDto settingDto = createDtoFromForm(form, dbManagementSession);
        reloadMasterData(settingDto);
        C0801W02SettingOptionDto option = settingDto.getOption();
        // 伝票ステータスにより受注予約データかどうか判定し、処理種別を設定
        option.setProcessType(C0801W02SettingOptionDto.getProcessTypeForUpdate(
            CommonConstants.DNPYSTS_01.equals(form.getBaseBean().getDnpysts()),
            settingDto.getPrcskbn()));
        // 受注締め処理を行うようにオプション設定
        option.setClosing(true);
        // 登録処理実行
        C0801W02ProcessingResultDto result = register.regist(settingDto);
        // 処理結果を受注登録設定DTOに反映
        settingDto.setAcptDlvryDto(result.getAcptDlvryDto());
        settingDto.setDetailList(result.getDetailList());
        // 帳票出力用パラメータを生成
        C0809R40PrintContentsDto printParameter = prePrintService.createParameter(settingDto);
        // 非同期処理受付用パラメータ
        AsyncManagementServiceModel<C0809R40PrintContentsDto> asyncModel =
            new AsyncManagementServiceModel<>(
                // 帳票出力用パラメータ
                printParameter,
                // ユーザセッション
                userSessionForm,
                // DB管理セッション情報
                dbManagementSession,
                // 配達伝票出力サービス
                reportService);
        try {
            // 非同期処理呼び出し、受付ID取得
            Z9999W01JobStateBean state = asyncManagementService.entryAsyncService(asyncModel);
            // レスポンスを生成
            return new AciRestResponse<>(state);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                // BusinessExceptionはそのままスローする
                throw e;
            }
            // それ以外はシステムエラーにラップしてスローする
            throw BusinessExceptionFactory.systemError(e);
        }
    }

    /**
     * セッション内の受注配達明細DTOにリクエストの内容と反映する
     * 
     * @param detailBean 受注配達明細Bean
     * @param idx 行番号
     * @return RESTレスポンス
     */
    @PutMapping("syncdetail/{idx}")
    public AciRestResponse<Void> syncDetailInSession(
        @RequestBody C0801W02MeisaiDetailBean detailBean, @PathVariable("idx") int idx) {
        C0801W02AcptDlvryDetailDto origin = session.getDetailList().get(idx);
        C0801W02AcptDlvryDetailDto modified = origin.toBuilder()
            // タイプ（更新データ）
            .type(DETAIL_TYPE.MODIFIED)
            // 計上区分
            .keijokbn(detailBean.getKeijokbn())
            // 型式
            .katasiki(detailBean.getKatasiki())
            // 受注数量
            .acptsuryo(util.removeComma(detailBean.getAcptsuryo()))
            // 総数
            .sokazu(util.removeComma(detailBean.getSokazu()))
            // 標準単価（標準価格）
            .hyojununt(util.removeComma(detailBean.getHyojununt()))
            // 受注単価（販売価格）
            .acptunt(util.removeComma(detailBean.getAcptunt()))
            // 初期表示金額
            .shokidsplyprice(util.removeComma(detailBean.getShokidsplyprice()))
            // 受注単価消費税金額
            .acptuntshohitaxkin(util.removeComma(detailBean.getAcptuntshohitaxkin()))
            // 受注消費税金額
            .acptshohitaxkin(util.removeComma(detailBean.getAcptshohitaxkin()))
            // 金額
            .acptkin(util.removeComma(detailBean.getKin()))
            // 明細備考
            .meisaibiko(detailBean.getMeisaibiko())
            // DTOをBuild
            .build();
        // Sessionに反映
        session.getDetailList().set(idx, modified);
        return new AciRestResponse<>(null);
    }

    /**
     * 不要受注配達明細行削除.<br>
     * <br>
     * セッションで保持している受注配達明細Beanリストから数量が未設定または0のデータをフィルタして返す
     * 
     * @return 受注配達明細Beanリスト
     */
    @GetMapping("omitlist")
    public List<C0801W02MeisaiDetailBean> omitDetailList() {
        // 数量が未設定または0となっている明細行をフィルタする
        List<C0801W02AcptDlvryDetailDto> omitList = detailFinder.omitList(session.getDetailList());
        // 明細リストをSessionに反映
        session.setDetailList(omitList);
        // Beanに変換
        return convert(omitList);
    }

    /**
     * 初期表示用に受注配達明細リストを取得する.
     * 
     * @param form フォーム
     * @param dbManagementSession DB管理セッション情報
     * @param response レスポンス
     * @return 受注配達明細Beanリスト
     */
    @SuppressWarnings("unchecked")
    @GetMapping("meisailist")
    public List<C0801W02MeisaiDetailBean> getDetailListForInit(
        @ModelAttribute("c0801W02Form") C0801W02Form form,
        @SessionAttribute("dbManagementSession_C0801W02") DbManagementSession dbManagementSession,
        HttpServletResponse response) {

        // 受注登録設定DTOを生成する
        C0801W02SettingDto settingDto = createDtoFromForm(form, dbManagementSession);
        // 処理区分と受注配達データの存在有無に応じて明細を取得する
        List<C0801W02AcptDlvryDetailDto> detailList = Collections.emptyList();
        if (PRCSKBN.INSERT == settingDto.getPrcskbn()) {
            if (settingDto.getAcptDlvryDto().isLoaded()) {
                // 新規で受注配達データありの場合（受注予約データが存在）
                detailList = detailFinder.getAcptDlvryDetailList(settingDto);
            }
            // 新規の際はお気に入り商品を元に明細を取得し、元の明細リストにマージする
            // その際に受注数量が0または無効な明細を除外する
            List<C0801W02AcptDlvryDetailDto> favoriteList =
                detailFinder.getFavoriteShnDetailList(settingDto);
            detailList = detailFinder.mergeList(detailList, favoriteList);
        } else {
            detailList = detailFinder.getAcptDlvryDetailList(settingDto);
        }
        // 最大表示件数制御
        detailList = (List<C0801W02AcptDlvryDetailDto>) chkLength(detailList, response);
        // 取得した明細はSessionに保持する
        session.setDetailList(detailList);

        return convert(detailList);
    }

    /**
     * サーバ関連受注配達明細リストを取得し、既存の明細にマージする.
     * 
     * @param form フォーム
     * @param dbManagementSession DB管理セッション情報
     * @param response レスポンス
     * @return 受注配達明細Beanリスト
     */
    @SuppressWarnings("unchecked")
    @GetMapping("meisailistwzserver")
    public List<C0801W02MeisaiDetailBean> getDetailListWithServerInfo(
        @ModelAttribute("c0801W02Form") @Valid C0801W02Form form,
        @SessionAttribute("dbManagementSession_C0801W02") DbManagementSession dbManagementSession,
        HttpServletResponse response) {

        C0801W02SettingDto settingDto = createDtoFromForm(form, dbManagementSession);

        // サーバ関連受注配達明細リスト取得処理
        List<C0801W02AcptDlvryDetailDto> serverList =
            detailFinder.getInstalledServerDetailList(settingDto);

        // 取得した明細とセッション内の明細をマージ
        List<C0801W02AcptDlvryDetailDto> mergedList =
            detailFinder.mergeList(session.getDetailList(), serverList);
        // 最大表示件数制御
        mergedList = (List<C0801W02AcptDlvryDetailDto>) chkLength(mergedList, response);
        // セッション内の明細リストを更新
        session.setDetailList(mergedList);
        return convert(mergedList);
    }

    /**
     * 前回受注の明細を取得し、既存の明細にマージする.
     * 
     * @param form フォーム
     * @param dbManagementSession DB管理セッション情報
     * @param response レスポンス
     * @return 受注配達明細Beanリスト
     */
    @SuppressWarnings("unchecked")
    @GetMapping("meisailistwzpre")
    public List<C0801W02MeisaiDetailBean> getDetailListWithPrevious(
        @ModelAttribute("c0801W02Form") @Valid C0801W02Form form,
        @SessionAttribute("dbManagementSession_C0801W02") DbManagementSession dbManagementSession,
        HttpServletResponse response) {

        C0801W02SettingDto settingDto = createDtoFromForm(form, dbManagementSession);
        if (Objects.isNull(settingDto.getPreviousAcptDlvryDto())) {
            // 前回受注データが取得できない場合はセッションの明細リストを返却
            return convert(session.getDetailList());
        }
        // 前回受注配達明細リストを取得する
        List<C0801W02AcptDlvryDetailDto> previousDetailDtoList =
            detailFinder.getPreviousAcptDlvryDetailList(settingDto);
        // 前回受注配達明細リストを既存の明細リストにマージする
        List<C0801W02AcptDlvryDetailDto> mergedList =
            detailFinder.mergeList(session.getDetailList(), previousDetailDtoList);
        // 最大表示件数制御
        mergedList = (List<C0801W02AcptDlvryDetailDto>) chkLength(mergedList, response);
        // セッション内の明細リストを更新
        session.setDetailList(mergedList);
        // DTOのリストをBeanのリストに変換して返却する
        return convert(mergedList);
    }

    /**
     * お気に入り商品受注明細配達リストを取得し、既存明細にマージする.
     * 
     * @param form フォーム
     * @param dbManagementSession DB管理セッション情報
     * @param response レスポンス
     * @return 受注配達明細Beanリスト
     */
    @SuppressWarnings("unchecked")
    @GetMapping("meisailistwzfavorite")
    public List<C0801W02MeisaiDetailBean> getDetailListWithFavoriteShn(
        @ModelAttribute("c0801W02Form") @Valid C0801W02Form form,
        @SessionAttribute("dbManagementSession_C0801W02") DbManagementSession dbManagementSession,
        HttpServletResponse response) {

        C0801W02SettingDto settingDto = createDtoFromForm(form, dbManagementSession);

        // お気に入り商品受注配達明細リストを取得する
        List<C0801W02AcptDlvryDetailDto> favoriteList =
            detailFinder.getFavoriteShnDetailList(settingDto);

        // お気に入り商品受注配達明細リストを既存の明細リストにマージする
        List<C0801W02AcptDlvryDetailDto> mergedList =
            detailFinder.mergeList(session.getDetailList(), favoriteList);
        // 最大表示件数制御
        mergedList = (List<C0801W02AcptDlvryDetailDto>) chkLength(mergedList, response);
        // セッション内の明細リストを更新
        session.setDetailList(mergedList);

        return convert(mergedList);
    }

    /**
     * 行追加/行挿入クリックイベント前の単項目チェックを行うためのエンドポイント.<br>
     * 
     * @param form フォーム
     * @param result リクエスト内容のバインド結果
     * @return メッセージリスト（空のリスト）
     */
    @PutMapping("validate")
    public List<String> validateBeforeInsert(@RequestBody @Validated C0801W02Form form) {
        return Collections.emptyList();
    }

    /**
     * 指定された商品の受注配達明細を追加し、受注配達明細リストを取得する.<br>
     * <br>
     * 指定された行番号が負数の場合、明細リストの最後尾に追加する.<br>
     * それ以外の場合は指定された行に新しい明細を挿入する.<br>
     * 
     * @param form フォーム
     * @param dbManagementSession DB管理セッション情報
     * @param shncd 商品コード
     * @param idx 行番号
     * @param response レスポンス
     * @return 受注配達明細Beanリスト
     */
    @SuppressWarnings("unchecked")
    @GetMapping("insertdetailrow/{shncd}/{idx}")
    public List<C0801W02MeisaiDetailBean> insertDetailRow(
        @ModelAttribute("c0801W02Form") @Valid C0801W02Form form,
        @SessionAttribute("dbManagementSession_C0801W02") DbManagementSession dbManagementSession,
        @PathVariable("shncd") String shncd, @PathVariable("idx") int idx,
        HttpServletResponse response) {

        C0801W02SettingDto settingDto = createDtoFromForm(form, dbManagementSession);
        // 指定された商品コードの受注配達明細リストを取得
        List<C0801W02AcptDlvryDetailDto> shnDetailList =
            detailFinder.getShnDetailList(settingDto, shncd);
        // セッション内の受注配達明細リストに商品の受注配達明細リストを挿入
        List<C0801W02AcptDlvryDetailDto> insertedList =
            detailFinder.insertDtoToList(session.getDetailList(), shnDetailList, idx);
        // 最大表示件数制御
        insertedList = (List<C0801W02AcptDlvryDetailDto>) chkLength(insertedList, response);
        // セッション内の明細リストを更新
        session.setDetailList(insertedList);

        return convert(insertedList);
    }

    /**
     * 指定された行の受注配達明細を複写し、受注配達明細リストを取得する
     * 
     * @param idx 元となる受注配達明細の行番号
     * @param response レスポンス
     * @return 受注配達明細Beanリスト
     */
    @SuppressWarnings("unchecked")
    @GetMapping("copydetailrow/{idx}")
    public List<C0801W02MeisaiDetailBean> copyDetailRow(@PathVariable("idx") int idx,
        HttpServletResponse response) {

        // 指定された行の受注配達明細DTOのコピーを取得
        C0801W02AcptDlvryDetailDto copy = detailFinder.copyDetail(session.getDetailList().get(idx));
        // 受注配達明細DTOリストにコピーを追加
        List<C0801W02AcptDlvryDetailDto> addedList =
            detailFinder.appendDtoToList(session.getDetailList(), copy, idx);
        // 最大表示件数制御
        addedList = (List<C0801W02AcptDlvryDetailDto>) chkLength(addedList, response);
        // セッション内の明細リストを更新
        session.setDetailList(addedList);
        return convert(addedList);
    }

    /**
     * 設置サーバ台数チェック.<br>
     * <br>
     * チェックエラーの際は例外をスローする.
     * 
     * @param form フォーム
     * @param dbManagementSession DB管理セッション情報
     * @return メッセージリスト（空のリスト）
     */
    @PutMapping("checkinstalledserver")
    public List<String> checkInstalledServer(@RequestBody C0801W02Form form,
        @SessionAttribute("dbManagementSession_C0801W02") DbManagementSession dbManagementSession) {
        C0801W02SettingDto settingDto = createDtoFromForm(form, dbManagementSession);

        // 設置サーバ台数チェック処理
        if (!tokuisakiService.checkNumberOfServersInstalled(settingDto.getTokuisakiInfo())) {
            throw BusinessExceptionFactory.dataNotFound(MessageID.W177, "得意先設置サーバー管理マスタに情報");
        }

        return Collections.emptyList();
    }

    /**
     * 自拠点／他拠点の切り替えを行う.
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param dlvrykyotencd 配達拠点コード
     * @param dlvryareacd 配達エリアコード
     * @param kyotensts 拠点ステータス
     * @param dbManagementSession DB管理セッション情報
     * @return 切替結果
     */
    @GetMapping("change/{apkihoncd}/{shopcd}/{dlvrykyotencd}/{dlvryareacd}/{kyotensts}")
    public Map<String, String> changeKyoten(@PathVariable("apkihoncd") String apkihoncd,
        @PathVariable("shopcd") String shopcd, @PathVariable("dlvrykyotencd") String dlvrykyotencd,
        @PathVariable("dlvryareacd") String dlvryareacd,
        @PathVariable("kyotensts") String kyotensts,
        @ModelAttribute("dbManagementSession_C0801W02") DbManagementSession dbManagementSession) {

        // 拠点ステータスの更新（自拠点↔他拠点）
        kyotensts = session.getAcptDlvryDto().switchKyotensts();
        // 配達エリアを取得
        C0801W02DlvryAreaDto area = dlvryService.getDlvryarea(apkihoncd, shopcd, dlvrykyotencd,
            dlvryareacd, dbManagementSession.getUnyoDate());
        // 配達パターンを取得
        String pattern = dlvryService.getDlvrypattern(area);
        Map<String, String> result = new LinkedHashMap<>();
        result.put("kyotensts", kyotensts);
        result.put("dlvrypattern", pattern);

        return result;
    }

    /**
     * 配達拠点リストを取得する（プルダウン用）.
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param dlvrykyotencd 配達拠点コード
     * @return 配達拠点リスト
     */
    @GetMapping("kyotenlist/{apkihoncd}/{shopcd}")
    public List<CommonPartsListBean> getDlvrykyotenList(@PathVariable("apkihoncd") String apkihoncd,
        @PathVariable("shopcd") String shopcd,
        @RequestParam(required = false) Optional<String> dlvrykyotencd) {
        List<CommonPartsListBean> dlvrykyotenList =
            util.convertList(dlvryService.getDlvryKyotenList(apkihoncd, shopcd), dlvryKyotenDto -> {
                CommonPartsListBean bean = new CommonPartsListBean(
                    dlvryKyotenDto.getDlvrykyotencd(), dlvryKyotenDto.getDlvrykyotennm());
                // 設定値が指定された配達拠点と一致すればselectedをtrueにする
                bean.setSelected(
                    dlvrykyotencd.isPresent() && dlvrykyotencd.get().equals(bean.getValue()));
                return bean;
            });
        // リスト内にselectedがtrueのBeanが存在しない場合は先頭のBeanのselectedをtrueにする
        if (!dlvrykyotenList.isEmpty()
            && !dlvrykyotenList.stream().anyMatch(bean -> bean.isSelected())) {
            dlvrykyotenList.stream().findFirst().get().setSelected(true);
        }
        return dlvrykyotenList;
    }

    /**
     * 配達エリアリストを取得する（プルダウン用）.
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param dlvrykyotencd 配達拠点コード
     * @param dlvryareacd 配達エリアコード
     * @param dbManagementSession DB管理セッション情報
     * @return 配達エリアリスト
     */
    @GetMapping("arealist/{apkihoncd}/{shopcd}/{dlvrykyotencd}")
    public List<CommonPartsListBean> getDlvryareaList(@PathVariable("apkihoncd") String apkihoncd,
        @PathVariable("shopcd") String shopcd, @PathVariable("dlvrykyotencd") String dlvrykyotencd,
        @RequestParam(required = false) Optional<String> dlvryareacd,
        @SessionAttribute("dbManagementSession_C0801W02") DbManagementSession dbManagementSession) {

        List<CommonPartsListBean> areaList =
            util.convertList(dlvryService.getDlvryAreaList(apkihoncd, shopcd, dlvrykyotencd,
                dbManagementSession.getUnyoDate()), dlvryAreaDto -> {
                    CommonPartsListBean bean = new CommonPartsListBean(
                        dlvryAreaDto.getDlvryareacd(), dlvryAreaDto.getDlvryareanm());
                    // 設定値が指定された配達エリアと一致すればselectedをtrueにする
                    bean.setSelected(
                        dlvryareacd.isPresent() && dlvryareacd.get().equals(bean.getValue()));
                    return bean;
                });
        // リスト内にselectedがtrueのBeanが存在しない場合は先頭のBeanのselectedをtrueにする
        if (!areaList.isEmpty() && !areaList.stream().anyMatch(bean -> bean.isSelected())) {
            areaList.stream().findFirst().get().setSelected(true);
        }
        return areaList;
    }

    /**
     * 配達会社リストを取得する（プルダウン用）.
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param dlvrycmpnycd 配達会社コード
     * @return 配達会社リスト
     */
    @GetMapping("companylist/{apkihoncd}/{shopcd}")
    public List<CommonPartsListBean> getDlvryCompanyList(
        @PathVariable("apkihoncd") String apkihoncd, @PathVariable("shopcd") String shopcd,
        @RequestParam(required = false) Optional<String> dlvrycmpnycd) {
        return util.convertList(dlvryService.getDlvryCompanyList(apkihoncd, shopcd),
            dlvryCompanyDto -> {
                CommonPartsListBean bean = new CommonPartsListBean(
                    dlvryCompanyDto.getDlvrycmpnycd(), dlvryCompanyDto.getDlvrycmpnynm());
                // 設定値が指定された配達会社と一致すればselectedをtrueにする
                bean.setSelected(
                    dlvrycmpnycd.isPresent() && dlvrycmpnycd.get().equals(bean.getValue()));
                return bean;
            });
    }

    /**
     * 配達パターンを取得する.
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param dlvrykyotencd 配達拠点コード
     * @param dlvryareacd 配達エリアコード
     * @param dbManagementSession DB管理セッション情報
     * @return 配達パターン
     */
    @GetMapping("dlvrypattern/{apkihoncd}/{shopcd}/{dlvrykyotencd}/{dlvryareacd}")
    public String getDlvrypattern(@PathVariable("apkihoncd") String apkihoncd,
        @PathVariable("shopcd") String shopcd, @PathVariable("dlvrykyotencd") String dlvrykyotencd,
        @PathVariable("dlvryareacd") String dlvryareacd,
        @ModelAttribute("dbManagementSession_C0801W02") DbManagementSession dbManagementSession) {
        // 配達エリアを取得
        C0801W02DlvryAreaDto area = dlvryService.getDlvryarea(apkihoncd, shopcd, dlvrykyotencd,
            dlvryareacd, dbManagementSession.getUnyoDate());
        // 配達パターンを取得
        String pattern = dlvryService.getDlvrypattern(area);
        return pattern;
    }

    /**
     * 処理区分リストを取得する（ラジオボタン用）.
     * 
     * @return 処理区分リスト
     */
    @GetMapping("prcskbnlist")
    public List<CommonPartsListBean> getPrcskbnList() {
        return util.convertList(Arrays.stream(C0801W02SettingDto.PRCSKBN.class.getEnumConstants()),
            e -> new CommonPartsListBean(e.getValue(), e.getLabel()));
    }

    /**
     * 受注区分リストを取得する（ラジオボタン用）.
     * 
     * @param tokuisakikbn 得意先区分
     * @return 受注区分リスト
     */
    @GetMapping("acptkbnlist")
    public List<CommonPartsListBean> getAcptKbnList() {
        // 得意先区分についてはSessionから取得する
        String tokuisakikbn =
            Optional.ofNullable(session.getTokuisakiDto()).map(d -> d.getTokuisakikbn()).orElse("");

        // システム管理マスタから受注区分リストを取得し重複を除外
        return util.convertList(
            util.distinct(sysMngService.getAcptKbnList().stream()).filter(originDto -> {
                // 得意先区分が「得意先（1）」、「NA受託（5）」、「配達受託（6）」以外のとき
                if (!CommonConstants.KBN_TOKUISAKI_KOKUAKU.equals(tokuisakikbn)
                    && !CommonConstants.KBN_TOKUISAKI_NAJUTAKU.equals(tokuisakikbn)
                    && !CommonConstants.KBN_TOKUISAKI_DLVRYJUTAKU.equals(tokuisakikbn)) {
                    // 「商品受注（01）」だけにする
                    return CommonConstants.KBN_ACPT_SHOHIN.equals(originDto.getSetteicd());
                }
                // 条件を満たさない場合はフィルタリングしない
                return true;
            }), sysMngDto -> new CommonPartsListBean(sysMngDto.getSetteicd(),
                sysMngDto.getSetteiatai()));
    }

    /**
     * 受注区分詳細リストを取得する（プルダウン用）
     * 
     * @param acptkbn 受注区分
     * @param acptkbnshs プルダウンで選択する値
     * @return 受注区分詳細リスト
     */
    @GetMapping("acptkbnshslist/{acptkbn}")
    public List<CommonPartsListBean> getAcptkbnshsList(@PathVariable("acptkbn") String acptkbn,
        @RequestParam(required = false) Optional<String> acptkbnshs) {
        List<CommonPartsListBean> acptkbnshsList = sysMngService.getAcptKbnList().stream()
            // 受注区分でフィルタリング
            .filter(sysMngDto -> Objects.equals(acptkbn, sysMngDto.getSetteicd()))
            // DTOからBeanに変換（子の項目を用いる）
            .map(sysMngDto -> {
                CommonPartsListBean bean = new CommonPartsListBean(sysMngDto.getChildSetteicd(),
                    sysMngDto.getChildSetteiatai());
                // 設定値が指定された受注区分詳細と一致すればselectedをtrueにする
                bean.setSelected(
                    acptkbnshs.isPresent() && acptkbnshs.get().equals(bean.getValue()));
                return bean;
            })
            // リストを生成
            .collect(util.toList());
        // リスト内にselectedがtrueのBeanが存在しない場合は先頭のBeanのselectedをtrueにする
        if (acptkbnshsList.size() != 0
            && !acptkbnshsList.stream().anyMatch(bean -> bean.isSelected())) {
            acptkbnshsList.stream().findFirst().get().setSelected(true);
        }
        return acptkbnshsList;
    }

    /**
     * 得意先区分に応じた計上区分リストを取得する.
     * 
     * @param tokuisakikbn 得意先区分
     * @return 計上区分リスト
     */
    @GetMapping("keijokbnlist/{tokuisakikbn}")
    public List<CommonPartsListBean> getKeijokbnList(
        @PathVariable("tokuisakikbn") String tokuisakikbn) {
        return util.convertList(sysMngService.getKeijokbnList(tokuisakikbn),
            sysMngDto -> new CommonPartsListBean(sysMngDto.getSetteicd(),
                sysMngDto.getSetteiatai()));
    }

    /**
     * 受注配達明細DTOの内容を元に受注配達明細Beanを生成する.
     * 
     * @param detailDto 受注配達明細DTO
     * @return 受注配達明細Bean
     */
    private C0801W02MeisaiDetailBean createMeisaiDetailBean(C0801W02AcptDlvryDetailDto detailDto) {
        C0801W02MeisaiDetailBean bean = C0801W02MeisaiDetailBean.builder()
            // 過去に手入力で受注単価を変更したことがあるか
            .untChangedBefore(detailDto.isUntChangedBefore())
            // 明細行番号
            .rowIndex(detailDto.getRowIndex())
            // 商品コード
            .shncd(detailDto.getShncd())
            // 商品名称略称
            .shnnmryakusho(detailDto.getShnnmryakusho())
            // 色
            .iro(detailDto.getIro())
            // サイズ名称
            .sz(detailDto.getSz())
            // 型式
            .katasiki(detailDto.getKatasiki())
            // 計上区分
            .keijokbn(detailDto.getKeijokbn())
            // 数量
            .acptsuryo(detailDto.getAcptsuryo())
            // 売上取扱単位
            .uritrtskitani(detailDto.getUritrtskitani())
            // 売上入数
            .uriirisu(detailDto.getUriirisu())
            // 売上単位
            .uritani(detailDto.getUritani())
            // 総数
            .sokazu(detailDto.getSokazu())
            // 標準単価
            .hyojununt(detailDto.getHyojununt())
            // 受注単価
            .acptunt(detailDto.getAcptunt())
            // 明細備考
            .meisaibiko(detailDto.getMeisaibiko())
            // 受注伝票明細番号
            .acptdnpymeisaino(detailDto.getAcptdnpymeisaino())
            // 受注伝票明細枝番号
            .acptdnpymeisaiedano(detailDto.getAcptdnpymeisaiedano())
            // データ作成元区分
            .datsksmotokbn(detailDto.getDatsksmotokbn())
            // 大分類コード
            .lctgrycd(detailDto.getLctgrycd())
            // 中分類コード
            .mctgrycd(detailDto.getMctgrycd())
            // 小分類コード
            .sctgrycd(detailDto.getSctgrycd())
            // 料金種別特別単価フラグ
            .rknkindtokubetsuuntflg(detailDto.getRknkindtokubetsuuntflg())
            // 販促優先区分
            .hansokuyusenkbn(detailDto.getHansokuyusenkbn())
            // 販促区分
            .hansokukbn(detailDto.getHansokukbn())
            // 現計算区分
            .genzkeisankbn(detailDto.getCalculateOptionDto().getGenzkeisankbn())
            // 新計算区分
            .shinkeisankbn(detailDto.getCalculateOptionDto().getShinkeisankbn())
            // 税計算区分切替日
            .taxkeisankbnurikirikaedd(
                detailDto.getCalculateOptionDto().getTaxkeisankbnurikirikaedd())
            // 消費税切替日
            .shohitaxkirikaedd(detailDto.getCalculateOptionDto().getShohitaxkirikaedd())
            // 計算方法
            .keisanhoho(detailDto.getCalculateOptionDto().getKeisanhoho())
            // 税端数計算区分
            .taxhasukeisankbn(detailDto.getCalculateOptionDto().getTaxhasukeisankbn())
            // 売上金額端数計算区分
            .urikinhasukeisankbn(detailDto.getCalculateOptionDto().getUrikinhasukeisankbn())
            // 現価格区分
            .shinpricekbn(detailDto.getPricekbn())
            // 新価格区分
            .genzpricekbn(detailDto.getPricekbn())
            // 受注単価消費税金額
            .acptuntshohitaxkin(detailDto.getAcptuntshohitaxkin())
            // 受注消費税金額
            .acptshohitaxkin(detailDto.getAcptshohitaxkin())
            // 初期表示価格
            .shokidsplyprice(detailDto.getShokidsplyprice())
            // 在庫単価
            .zaikount(detailDto.getZaikount())
            // 単品明細フラグ
            .tanpinmeisaiflg(detailDto.getTanpinmeisaiflg())
            // 現価格区分
            .genzpricekbn(detailDto.getGenzpricekbn())
            // 新価格区分
            .shinpricekbn(detailDto.getShinpricekbn())
            // 商品税区分
            .shntaxkbn(detailDto.getShntaxkbn())
            // 受注金額
            .kin(detailDto.getAcptkin()).build();
        // 標準価格情報
        bean.setHyojunpriceBean(C0801W02SwitchableShnPriceBean.builder()
            // 現価格
            .genzprice(detailDto.getHyojunShnPriceDto().getGenzprice())
            // 現持ち帰り価格
            .genzmochikaeriprice(detailDto.getHyojunShnPriceDto().getGenzmochikaeriprice())
            // 新価格
            .shinprice(detailDto.getHyojunShnPriceDto().getShinprice())
            // 新持ち帰り価格
            .shinmochikaeriprice(detailDto.getHyojunShnPriceDto().getShinmochikaeriprice())
            // 価格切替日
            .pricekirikaedd(detailDto.getHyojunShnPriceDto().getKakakukirikaedd()).build());
        // オープン価格情報
        bean.setOpenpriceBean(C0801W02SwitchableShnPriceBean.builder()
            // 現価格
            .genzprice(detailDto.getOpenShnPriceDto().getGenzprice())
            // 現持ち帰り価格
            .genzmochikaeriprice(detailDto.getOpenShnPriceDto().getGenzmochikaeriprice())
            // 新価格
            .shinprice(detailDto.getOpenShnPriceDto().getShinprice())
            // 新持ち帰り価格
            .shinmochikaeriprice(detailDto.getOpenShnPriceDto().getShinmochikaeriprice())
            // 価格切替日
            .pricekirikaedd(detailDto.getOpenShnPriceDto().getKakakukirikaedd()).build());
        // 販促価格情報
        if (util.isFlagged(detailDto.getHansokukbn())) {
            bean.setHansokupriceBean(C0802W01RangeShnPriceBean.builder()
                // 適用開始日
                .strtdd(detailDto.getHansokuShnPriceDto().getStrtdd())
                // 適用終了日
                .shrydd(detailDto.getHansokuShnPriceDto().getShrydd())
                // 価格
                .price(detailDto.getHansokuShnPriceDto().getDlvryprice())
                // 持ち帰り価格
                .mochikaeriprice(detailDto.getHansokuShnPriceDto().getMochikaeriprice()).build());
        }
        if (util.isFlagged(detailDto.getRknkindtokubetsuuntflg())) {
            // 得意先別特別商品価格情報
            bean.setTokubetsupriceBean(C0802W01RangeShnPriceBean.builder()
                // 適用開始日
                .strtdd(detailDto.getTokubetsuShnPriceDto().getStrtdd())
                // 適用終了日
                .shrydd(detailDto.getTokubetsuShnPriceDto().getShrydd())
                // 価格
                .price(detailDto.getTokubetsuShnPriceDto().getDlvryprice())
                // 持ち帰り価格
                .mochikaeriprice(detailDto.getTokubetsuShnPriceDto().getMochikaeriprice()).build());
            // 得意先別特別商品価格情報リスト
            bean.setTokubetsupriceBeanList(detailDto.getTokubetsuUntList().stream()
                .map(dto -> C0802W01RangeShnPriceBean.builder()
                    // 適用開始日
                    .strtdd(dto.getStrtdd())
                    // 適用終了日
                    .shrydd(dto.getShrydd())
                    // 価格（適用可否に関係なく取得する）
                    .price(dto.getDlvrypriceForce())
                    // 持ち帰り価格（適用可否に関係なく取得する）
                    .mochikaeriprice(dto.getMochikaeripriceForce()).build())
                .collect(util.toList()));
        }
        return bean;
    }

    /**
     * フォームの内容とセッション情報を元に受注登録設定DTOを構築する.
     * 
     * @param form フォーム
     * @param dbManagementSession DB管理セッション
     * @return 受注登録設定DTO
     */
    private C0801W02SettingDto createDtoFromForm(C0801W02Form form,
        DbManagementSession dbManagementSession) {

        C0801W02BaseBean baseBean = form.getBaseBean();
        C0801W02MeisaiBean meisaiBean = form.getMeisaiBean();
        C0801W02FooterBean footerBean = form.getFooterBean();

        // セッション内の受注配達DTOのコピーを取得
        C0801W02AcptDlvryDto acptDlvryDto = session.getAcptDlvryDto().toBuilder().build();
        // 画面入力内容をSessionやDTOに反映
        // 配達拠点コード
        acptDlvryDto.setDlvrykyotencd(baseBean.getDlvrykyotencd());
        // 配達エリアコード
        acptDlvryDto.setDlvryareacd(baseBean.getDlvryareacd());
        // 配達会社コード（自社の場合はnullに設定）
        acptDlvryDto.setDlvrycmpnycd(
            CommonConstants.CD_DLVRYCMP_JISHA.equals(baseBean.getDlvrycmpnycd()) ? null
                : baseBean.getDlvrycmpnycd());
        // 配達担当者コード
        acptDlvryDto.setDlvrytnshacd(baseBean.getDlvrytnshacd());
        // 受注日
        acptDlvryDto.setAcptdd(DateTimeUtil.toNoSlashedDate(baseBean.getAcptdd()));
        // 受注受付経路区分
        acptDlvryDto.setAcptrcptkeirokbn(baseBean.getAcptrcptkeirokbn());
        // 配達予定日
        acptDlvryDto.setDlvryytidd(DateTimeUtil.toNoSlashedDate(baseBean.getDlvryytidd()));
        // 配達希望時間区分
        acptDlvryDto.setDlvrykibojikankbn(baseBean.getDlvrykibojikankbn());
        // 指定時間（開始）
        acptDlvryDto
            .setDlvrykibojikan(DateTimeUtil.toNoColonDateHHMM(baseBean.getDlvrykibojikan()));
        // 指定時間（終了）
        acptDlvryDto.setDlvrykibojikanshry(
            DateTimeUtil.toNoColonDateHHMM(baseBean.getDlvrykibojikanshry()));
        // 配達日
        acptDlvryDto.setDlvrydd("");
        // 留守置き
        acptDlvryDto.setFzaiokikanoflg(baseBean.getFzaiokikanoflg());
        // 受注区分
        acptDlvryDto.setAcptkbn(meisaiBean.getAcptkbn());
        // 受注区分詳細
        acptDlvryDto.setAcptkbnshs(meisaiBean.getAcptkbnshs());
        // 印刷メモ１～３
        acptDlvryDto.setPrntmemon1(footerBean.getPrntmemon1());
        acptDlvryDto.setPrntmemon2(footerBean.getPrntmemon2());
        acptDlvryDto.setPrntmemon3(footerBean.getPrntmemon3());
        // 預かりボトル１２Ｌ本数
        acptDlvryDto.setAzukaribtln1n2rittoruhns(footerBean.getAzukaribtln1n2rittoruhns());
        // 預かりボトル８Ｌ本数
        acptDlvryDto.setAzukaribtln8rittoruhns(footerBean.getAzukaribtln8rittoruhns());
        // 回収予定ボトル１２Ｌ本数
        acptDlvryDto
            .setKybtln1n2rittoruhns(ItemUtil.removeComma(footerBean.getKybtln1n2rittoruhns()));
        // 回収予定ボトル８Ｌ本数
        acptDlvryDto.setKybtln8rittoruhns(ItemUtil.removeComma(footerBean.getKybtln8rittoruhns()));
        // 配布物１～４
        List<C0801W02HaifubutsuDto> haifubutsuList = util.convertList(form.getHaifubutsuBeanList(),
            bean -> C0801W02HaifubutsuDto.create(bean));
        acptDlvryDto.setHaifubutsuList(haifubutsuList);
        // 単体伝票締め（チェックボックスがOFFや非活性の場合はOFF）
        acptDlvryDto.setSingldnpyflg(
            Objects.requireNonNullElse(baseBean.getSingldnpyflg(), CommonConstants.FLG_OFF));
        // 配達区分（配達会社コードの値に応じて設定する）
        if (CommonConstants.CD_DLVRYCMP_MOCHIKAERI.equals(baseBean.getDlvrycmpnycd())) {
            acptDlvryDto.setDlvrykbn(CommonConstants.KBN_DLVRY_MOCHIKAERI);
        } else if (CommonConstants.CD_DLVRYCMP_JISHA.equals(baseBean.getDlvrycmpnycd())) {
            acptDlvryDto.setDlvrykbn(CommonConstants.KBN_DLVRY_JISHA);
        } else if (StringUtils.isEmpty(baseBean.getDlvrycmpnycd())) {
            acptDlvryDto.setDlvrykbn(CommonConstants.KBN_DLVRY_JISHA);
        } else {
            acptDlvryDto.setDlvrykbn(CommonConstants.KBN_DLVRY_TASHA);
        }
        // 受注担当者AP基本コード
        acptDlvryDto.setAcpttnshaapkihoncd(baseBean.getAcpttnshaapkihoncd());
        // 受注担当者ショップコード
        acptDlvryDto.setAcpttnshashopcd(baseBean.getAcpttnshashopcd());
        // 受注担当者コード
        acptDlvryDto.setAcpttnshacd(baseBean.getAcpttnshacd());
        // 配達パターン
        acptDlvryDto.setDlvrypatern(baseBean.getDlvrypattern());
        // 有効区分
        acptDlvryDto.setYukokbn(baseBean.getYukokbn());
        // 委託相手AP基本コード
        acptDlvryDto.setItakuaiteapkihoncd(baseBean.getItakuaiteapkihoncd());
        // 委託相手ショップコード
        acptDlvryDto.setItakuaiteshopcd(baseBean.getItakuaiteshopcd());
        // 委託相手受注伝票番号
        acptDlvryDto.setItakuaiteacptdnpyno(baseBean.getItakuaiteacptdnpyno());
        // 伝票ステータス
        acptDlvryDto.setDnpysts(baseBean.getDnpysts());
        // 拠点ステータス
        acptDlvryDto.setKyotensts(baseBean.getKyotensts());
        // 配達管轄AP基本コード
        acptDlvryDto.setDlvrykankatsuapkihoncd(baseBean.getDlvrykankatsuapkihoncd());
        // 配達管轄ショップコード
        acptDlvryDto.setDlvrykankatsushopcd(baseBean.getDlvrykankatsushopcd());
        // 納品先情報１
        acptDlvryDto.setNohinsakijohonmei1(baseBean.getNohinsakijohon1());
        // 納品先情報２
        acptDlvryDto.setNohinsakijohonmei1(baseBean.getNohinsakijohon2());
        // 受注配達明細リストについては明細のコピーを生成してリストにする
        List<C0801W02AcptDlvryDetailDto> copiedList =
            util.convertList(session.getDetailList(), d -> detailFinder.copyDetail(d));
        C0801W02SettingDto settingDto = C0801W02SettingDto.builder()
            // AP基本コード
            .apkihoncd(baseBean.getApkihoncd())
            // ショップコード
            .shopcd(baseBean.getShopcd())
            // 得意先コード
            .tokuisakicd(baseBean.getTokuisakicd())
            // 受注伝票番号
            .acptdnpyno(baseBean.getAcptdnpyno())
            // 枝番号
            .edano(baseBean.getEdano())
            // 処理区分
            .prcskbn(PRCSKBN.of(baseBean.getPrcskbn()))
            // コンテキスト
            .context(C0801W02ProcessingContextDto.create(dbManagementSession))
            // 受注配達データ
            .acptDlvryDto(acptDlvryDto)
            // 受注配達明細DTOリスト
            .detailList(copiedList)
            // 前回受注データ
            .previousAcptDlvryDto(session.getPreviousAcptDlvryDto())
            // 得意先配達データ
            .tokuisakiDlvryDto(session.getTokuisakiDlvryDto())
            // 得意先情報
            .tokuisakiInfo(session.getTokuisakiDto())
            // 委託相手得意先情報
            .itakuaiteInfo(session.getItakuaiteTokuisakiDto())
            // ショップ情報
            .shopInfo(session.getShopDto())
            // 請求先情報
            .skysakiInfo(session.getSkysakiDto())
            // DTOを構築
            .build();
        // デフォルトのオプションを設定
        settingDto
            .setOption(C0801W02SettingOptionDto.builder().prcskbn(settingDto.getPrcskbn()).build());
        return settingDto;
    }

    /**
     * 受注配達明細DTOリストを受注配達明細BeanListに変換する
     * 
     * @param dtoList 受注配達明細DTOリスト
     * @return 受注配達明細Beanリスト
     */
    protected List<C0801W02MeisaiDetailBean> convert(List<C0801W02AcptDlvryDetailDto> dtoList) {
        return util.convertList(dtoList, dto -> createMeisaiDetailBean(dto));
    }

    /**
     * 受注登録設定DTOで保持しているマスタ（ショップ、得意先、請求先）データをリロードする<br>
     * <br>
     * 取得した内容はSessionにも反映する<br>
     * 
     * @param settingDto 受注登録設定DTO
     */
    protected void reloadMasterData(C0801W02SettingDto settingDto) {
        settingDto = finder.loadMasterData(settingDto);
        session.setShopDto(settingDto.getShopInfo());
        session.setTokuisakiDto(settingDto.getTokuisakiInfo());
        session.setItakuaiteTokuisakiDto(settingDto.getItakuaiteInfo());
        session.setSkysakiDto(settingDto.getSkysakiInfo());
    }
}
