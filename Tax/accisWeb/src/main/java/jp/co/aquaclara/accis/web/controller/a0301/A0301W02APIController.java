package jp.co.aquaclara.accis.web.controller.a0301;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.constants.ValidationConstants;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V200TSetShnDetailEntity;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.controller.a0301.bean.A0301W02BaseBean;
import jp.co.aquaclara.accis.web.controller.a0301.bean.A0301W02BaseBean.MODE;
import jp.co.aquaclara.accis.web.controller.a0301.bean.A0301W02BaseTabBean;
import jp.co.aquaclara.accis.web.controller.a0301.bean.A0301W02SetMeisaiBean;
import jp.co.aquaclara.accis.web.controller.a0301.bean.A0301W02SetMeisaiBean.STATE;
import jp.co.aquaclara.accis.web.controller.a0301.bean.A0301W02SetSummaryBean;
import jp.co.aquaclara.accis.web.controller.a0301.bean.A0301W02ShopTabBean;
import jp.co.aquaclara.accis.web.controller.a0301.form.A0301W02Form;
import jp.co.aquaclara.accis.web.controller.a0301.session.A0301W02Session;
import jp.co.aquaclara.accis.web.controller.commons.AccisWebBaseController;
import jp.co.aquaclara.accis.web.controller.commons.AciRestResponse;
import jp.co.aquaclara.accis.web.controller.commons.DbManagementSession;
import jp.co.aquaclara.accis.web.controller.commons.UserSessionForm;
import jp.co.aquaclara.accis.web.controller.parts.master.bean.CommonPartsListBean;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M006TDto;
import jp.co.aquaclara.accis.web.domain.service.a0301.A0301W02Service;
import jp.co.aquaclara.accis.web.domain.service.master.S02M006TService;
import jp.co.aquaclara.accis.web.domain.service.x9901.StringUtil;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.AciDataConflictException;
import jp.co.aquaclara.accis.web.exception.AciDataNotFoundException;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import jp.co.aquaclara.accis.web.exception.dto.FormField;
import lombok.extern.slf4j.Slf4j;

/**
 * A0301W02_商品マスタ(登録画面)APIコントローラクラス.
 */
@RestController
@RequestMapping("/api/a0301w02")
@Slf4j
public class A0301W02APIController extends AccisWebBaseController {

    /**
     * ユーザーセッション
     */
    @Autowired
    private UserSessionForm userSession;

    /**
     * システム管理マスタサービスクラス
     */
    @Autowired
    private S02M006TService s02m006tService;

    /**
     * 商品マスタ(登録画面)サービスクラス
     */
    @Autowired
    private A0301W02Service a0301w02Service;

    /**
     * 商品マスタ（登録）セッション情報
     */
    @Autowired
    private A0301W02Session a0301w02session;

    // 明細1ページ当たりの行数
    private static final int PAGEABLE_SIZE = 999;

    // 数量や金額に設定するゼロ
    private static final String ZERO = BigDecimal.ZERO.toPlainString();

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage,
        MethodParameter parameter, Type targetType,
        Class<? extends HttpMessageConverter<?>> converterType) {

        if (!(body instanceof A0301W02Form)) {
            return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
        }

        // RequestBody(マッピング後)のformの値を取得
        @SuppressWarnings("unused")
        A0301W02Form form = (A0301W02Form) body;

        return body;
    }

    /**
     * 明細検索処理
     * 
     * @param form
     * @param dbManagementSession
     * @param apkihoncd
     * @param shopcd
     * @param pageable
     * @return
     */
    @GetMapping("/search/{apkihoncd}/{shopcd}")
    public Page<A0301W02SetMeisaiBean> search(@ModelAttribute("a0301W02Form") A0301W02Form form,
        @ModelAttribute(name = "dbManagementSession_A0301W02",
            binding = true) DbManagementSession dbManagementSession,
        @PathVariable("apkihoncd") String apkihoncd, @PathVariable("shopcd") String shopcd,
        @PageableDefault(size = PAGEABLE_SIZE) Pageable pageable) {

        A0301W02BaseBean baseBean = form.getBaseBean();
        List<A0301W02SetMeisaiBean> setMeisalBeanList = new ArrayList<>();

        // セット区分が「1:セット商品」でない場合は空の検索結果を表示
        if (!CommonConstants.FLG_ON.equals(form.getBaseBean().getStkbnhidden())) {
            a0301w02session.setMeisaiBeanList(setMeisalBeanList);
            // 初期表示明細Listは別のインスタンスにする
            a0301w02session.setInitialMeisaiBeanList(new ArrayList<>());
            return new PageImpl<>(setMeisalBeanList, pageable, 0);
        }
        String stshncd = form.getBaseBean().getShncd();
        String shnapkihoncd = form.getShopTabBean().getShnapkihoncd();
        String unyodate = dbManagementSession.getUnyoDate();
        if (!StringUtil.isEmpty(form.getBaseBean().getAddrowflag())) {
            // 行追加でコールされた場合、明細リストに行を追加する
            setMeisalBeanList = addRow(form, dbManagementSession);
            // 追加したページ（最終ページ）を表示させる
            double maxPageCount =
                Math.ceil((double) setMeisalBeanList.size() / (double) pageable.getPageSize());
            pageable = pageable.withPage((int) maxPageCount - 1);
        } else if (!StringUtil.isEmpty(form.getBaseBean().getDeleteindex())) {
            // 削除でコールされた場合
            setMeisalBeanList = deleteRow(baseBean);
            pageable = pageable.withPage(Integer.parseInt(baseBean.getPagenum()));
            // 削除の結果該当ページがない場合は1ページ前を表示する
            if (!setMeisalBeanList.isEmpty() && setMeisalBeanList.size() <= pageable.getOffset()) {
                pageable = pageable.withPage(pageable.getPageNumber() - 1);
            }
        } else if (CommonConstants.FLG_ON.equals(form.getBaseBean().getPagerflag())) {
            // ページャークリックでコールされた場合、セッションから明細リストを取得
            setMeisalBeanList = a0301w02session.getMeisaiBeanList();
        } else {
            // 初期表示
            try {
                setMeisalBeanList = a0301w02Service.getSetMeisaiBeanList(apkihoncd, shopcd, stshncd,
                    shnapkihoncd, unyodate);
                // 売上端数管理区分に応じた丸めモードを取得
                String hasukbn = a0301w02session.getUrikinhasukeisankbn();
                final RoundingMode mode = convertHasukeisankbn(hasukbn);
                setMeisalBeanList.stream().forEach(bean -> {
                    // 総数の計算
                    bean.setSokazu(
                        getDecimalValue(bean.getSuryo()).multiply(getDecimalValue(bean.getIrisu1()))
                            .stripTrailingZeros().toPlainString());
                    // 配達金額
                    bean.setDlvrykin(getDecimalValue(bean.getDlvryhanbaiprice())
                        .multiply(getDecimalValue(bean.getSuryo())).setScale(2, mode)
                        .stripTrailingZeros().toPlainString());
                    // 持ち帰り金額
                    bean.setMochikaerikin(getDecimalValue(bean.getMchikerihanbaiprice())
                        .multiply(getDecimalValue(bean.getSuryo())).setScale(2, mode)
                        .stripTrailingZeros().toPlainString());
                });
                // セッションに保持
                a0301w02session.setMeisaiBeanList(setMeisalBeanList);
                // 初期表示時のデータは別のインスタンスにする
                a0301w02session.setInitialMeisaiBeanList(
                    setMeisalBeanList.stream().map(origin -> origin.toBuilder().build())
                        .collect(Collectors.toCollection(LinkedList::new)));
                // 次の連番を算出し、セッションに保持
                a0301w02session.setNextSeq(getLastSeq());
            } catch (AciDataNotFoundException nfe) {
                a0301w02session.setMeisaiBeanList(null);
                throw nfe;
            }
        }
        int totalRecordCount = setMeisalBeanList.size();
        if (totalRecordCount > 0) {
            setMeisalBeanList = super.selectPage(pageable, setMeisalBeanList);
        }
        return new PageImpl<>(setMeisalBeanList, pageable, totalRecordCount);
    }

    /**
     * 登録更新処理
     * 
     * @param form
     * @param dbManagementSession
     * @return
     */
    @PutMapping("update")
    public AciRestResponse<A0301W02Form> update(@RequestBody @Validated A0301W02Form form,
        @ModelAttribute(name = "dbManagementSession_A0301W02",
            binding = false) DbManagementSession dbManagementSession) {

        // 事前準備
        preExecute(form);
        // 単項目チェック
        validate(form, dbManagementSession);
        // 相関チェック
        check(form, dbManagementSession);

        A0301W02BaseBean baseBean = form.getBaseBean();
        A0301W02BaseTabBean baseTabBean = form.getBaseTabBean();
        A0301W02ShopTabBean shopTabBean = form.getShopTabBean();
        MODE mode = baseBean.getMode();

        try {
            if (MODE.INSERT.equals(mode)) {
                // 登録モードの場合
                // 新規商品情報を登録し、採番された商品コードを取得
                String shncd = a0301w02Service.insert(form, dbManagementSession);
                // 採番された商品コードをフォームに設定
                baseBean.setShncd(shncd);
                // 登録者情報をフォーム（基本情報タブ、ショップ情報タブ）に設定
                baseTabBean.setInsertedTimestamp(dbManagementSession, userSession);
                shopTabBean.setInsertedTimestamp(dbManagementSession, userSession);
                // 更新者情報をフォーム（基本情報タブ）に設定
                baseTabBean.setUpdatedTimestamp(dbManagementSession, userSession);
            } else if (MODE.UPDATE.equals(mode)) {
                // 更新モードの場合
                // 基本情報タブの更新要否
                boolean updBaseTabFlg = !baseBean.isDisabledTabItem();
                // セット商品情報タブの更新要否
                boolean updSetTabFlg = !baseBean.isDisabledTabItem()
                    && CommonConstants.FLG_ON.equals(baseBean.getStkbn());
                // 更新処理
                a0301w02Service.update(form, updBaseTabFlg, updSetTabFlg, dbManagementSession);
                // 更新者情報をフォーム（基本情報タブ）に設定
                if (updBaseTabFlg) {
                    baseTabBean.setUpdatedTimestamp(dbManagementSession, userSession);
                }
            }
            // 更新者情報をフォーム（ショップ情報タブ）に設定
            shopTabBean.setUpdatedTimestamp(dbManagementSession, userSession);
            // レスポンスを返却
            return new AciRestResponse<>(form);
        } catch (Exception ex) {
            ex.printStackTrace();
            if (!(ex instanceof AciDataConflictException)) {
                // 予期せぬ例外はラップしてからスローする
                throw BusinessExceptionFactory.validationFailed(ex, MessageID.E002);
            }
            throw ex;
        }
    }

    /**
     * 登録更新前のチェックおよび警告メッセージ設定
     * 
     * @param form フォーム
     * @param dbManagementSession DB管理セッション情報
     * @return メッセージリスト
     */
    @PutMapping("check")
    public List<String> checkBeforeUpdate(@RequestBody @Validated A0301W02Form form,
        @ModelAttribute(name = "dbManagementSession_A0301W02",
            binding = false) DbManagementSession dbManagementSession) {
        // 事前準備
        preExecute(form);
        // 単項目チェック
        validate(form, dbManagementSession);
        // 相関チェック
        check(form, dbManagementSession);
        // ワーニングチェック
        List<String> messageList = new ArrayList<>();
        String operationDate = dbManagementSession.getUnyoDate();
        A0301W02BaseTabBean baseTabBean = form.getBaseTabBean();
        A0301W02ShopTabBean shopTabBean = form.getShopTabBean();
        // 各種切替日に値の変更がある場合、運用日以前の日付だった場合はワーニング
        if (isLaterIfModified(baseTabBean.getShnnmkirikaedd(), baseTabBean.getInitshnnmkirikaedd(),
            operationDate)) {
            BusinessExceptionFactory.setMessageList(messageList, MessageID.W201, "商品名称切替日",
                "運用日付以前");
        }
        if (isLaterIfModified(baseTabBean.getHyojunpricekirikaedd(),
            baseTabBean.getInithyojunpricekirikaedd(), operationDate)) {
            BusinessExceptionFactory.setMessageList(messageList, MessageID.W201, "標準価格切替日",
                "運用日付以前");
        }
        if (isLaterIfModified(shopTabBean.getOpnpricekirikaedd(),
            shopTabBean.getInitopnpricekirikaedd(), operationDate)) {
            BusinessExceptionFactory.setMessageList(messageList, MessageID.W201, "オープン価格切替日",
                "運用日付以前");
        }
        if (isLaterIfModified(shopTabBean.getShnpointkirikaedd(),
                shopTabBean.getInitshnpointkirikaedd(), operationDate)) {
            BusinessExceptionFactory.setMessageList(messageList, MessageID.W201, "ポイント発生切替日",
                "運用日付以前");
        }
        if (isLaterIfModified(shopTabBean.getMtmkipointkirikaedd(),
            shopTabBean.getInitmtmkipointkirikaedd(), operationDate)) {
            BusinessExceptionFactory.setMessageList(messageList, MessageID.W201, "まとめ買いポイント発生切替日",
                "運用日付以前");
        }

        // ワーニングメッセージがある場合は確認メッセージも追加
        if (!messageList.isEmpty()) {
            messageList.add(messageSource.getMessage(MessageID.I002.toString()));
        }
        return messageList;
    }

    /**
     * 「セット商品情報タブ-合計欄」取得処理
     * 
     * @param form フォーム
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @return サマリ結果
     */
    @GetMapping("/summary/{apkihoncd}/{shopcd}")
    public A0301W02SetSummaryBean summary(@ModelAttribute("a0301W02Form") A0301W02Form form,
        @PathVariable("apkihoncd") String apkihoncd, @PathVariable("shopcd") String shopcd) {
        log.debug("売上端数管理区分 : {}", a0301w02session.getUrikinhasukeisankbn());
        List<A0301W02SetMeisaiBean> detailList = a0301w02session.getMeisaiBeanList();
        log.debug("明細リスト : {}", detailList);
        String hasukbn = a0301w02session.getUrikinhasukeisankbn();
        // 配達金額合計
        BigDecimal dlvrykin = summaryDlvryprice(detailList, hasukbn);
        // 持ち帰り金額合計
        BigDecimal mochikaerikin = summaryMochikaeriprice(detailList, hasukbn);
        A0301W02SetSummaryBean summaryBean = new A0301W02SetSummaryBean(
            // 配達金額合計
            dlvrykin.stripTrailingZeros().toPlainString(),
            // 持ち帰り金額合計
            mochikaerikin.stripTrailingZeros().toPlainString());
        log.debug("Summary Bean : {}", summaryBean);
        return summaryBean;
    }

    /**
     * 明細行変更処理
     * 
     * @param meisaiBean 明細Bean
     * @param pagenum 選択中のページ番号
     * @param index 変更する明細のインデックス
     * @return レスポンス
     */
    @PutMapping("/changerow/{pagenum}/{index}")
    public AciRestResponse<Void> changeRow(@RequestBody A0301W02SetMeisaiBean meisaiBean,
        @PathVariable("pagenum") int pagenum, @PathVariable("index") int index) {

        // 変更する行の特定
        int i = pagenum * PAGEABLE_SIZE + index;
        // セッションで保持する明細行の入力項目を変更する
        A0301W02SetMeisaiBean detailBean = a0301w02session.getMeisaiBeanList().get(i);

        detailBean.setKeijokbn1(meisaiBean.getKeijokbn1());
        if (CommonConstants.KBN_KEIJO_URIAGE.equals(meisaiBean.getKeijokbn1())) {
            detailBean.setDlvryhanbaiprice(meisaiBean.getDlvryhanbaiprice());
            detailBean.setMchikerihanbaiprice(meisaiBean.getMchikerihanbaiprice());
        } else {
            // 計上区分が売上以外の場合は単価を0円にする
            detailBean.setDlvryhanbaiprice(ZERO);
            detailBean.setMchikerihanbaiprice(ZERO);
        }

        detailBean.setSuryo(meisaiBean.getSuryo());
        // 総数の計算
        detailBean.setSokazu(
            getDecimalValue(detailBean.getSuryo()).multiply(getDecimalValue(detailBean.getIrisu1()))
                .stripTrailingZeros().toPlainString());

        // 金額を計算する
        String hasukbn = a0301w02session.getUrikinhasukeisankbn();
        // 売上端数管理区分に応じた丸めモードを取得
        final RoundingMode mode = convertHasukeisankbn(hasukbn);
        // 配達金額
        detailBean.setDlvrykin(getDecimalValue(detailBean.getDlvryhanbaiprice())
            .multiply(getDecimalValue(detailBean.getSuryo())).setScale(2, mode).stripTrailingZeros()
            .toPlainString());
        // 持ち帰り金額
        detailBean.setMochikaerikin(getDecimalValue(detailBean.getMchikerihanbaiprice())
            .multiply(getDecimalValue(detailBean.getSuryo())).setScale(2, mode).stripTrailingZeros()
            .toPlainString());
        // 明細状態
        if (detailBean.getState() == STATE.LOADED) {
            // ロードされた明細の場合は状態を変更(行追加で増えた明細はADDEDのままでよい）
            detailBean.setState(STATE.MODIFIED);
        }
        log.debug("更新された行の内容 : {}", detailBean);
        // 更新結果の返却
        return new AciRestResponse<Void>(null);
    }

    /**
     * セッションクリア処理
     * 
     * @return
     */
    @PutMapping("/clearsession")
    public AciRestResponse<Void> clearSession() {

        // セッションで保持する明細行をクリアする
        a0301w02session.setNextSeq(0);
        a0301w02session.setUrikinhasukeisankbn(null);
        a0301w02session.setMeisaiBeanList(null);
        a0301w02session.setInitialMeisaiBeanList(null);

        // 更新結果の返却
        AciRestResponse<Void> res = new AciRestResponse<Void>(null);

        return res;
    }

    /**
     * 計上区分プルダウンリストの内容をシステム管理マスタから取得する
     * 
     * @return 計上区分プルダウンリスト
     */
    @GetMapping("/keijolist")
    public List<CommonPartsListBean> getKeijokbnList() {
        S02M006TDto condition = new S02M006TDto();
        condition.setApkihoncd(CommonConstants.APCD_000);
        condition.setShopcd(CommonConstants.SYS_SHOPCD_000);
        condition.setSetteino(CommonConstants.SYS_SETTEINO_009);
        // システム管理マスタ検索
        return s02m006tService.search(condition, null, null).stream()
            // 計上区分「01：売上」、「03：サービス」、「05：試飲」のみにフィルタ
            .filter(dto -> CommonConstants.KBN_KEIJO_URIAGE.equals(dto.getSetteicd())
                || CommonConstants.KBN_KEIJO_SERVICE.equals(dto.getSetteicd())
                || CommonConstants.KBN_KEIJO_SHIIN.equals(dto.getSetteicd()))
            // Beanに変換
            .map(dto -> new CommonPartsListBean(dto.getSetteicd(), dto.getMojisetteiatai()))
            // リストにする
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * 明細行追加処理
     * 
     * @return B0801W02M2MeisaiBeanリスト
     */
    private List<A0301W02SetMeisaiBean> addRow(A0301W02Form form,
        DbManagementSession dbManagementSession) {

        // 明細が0件の場合はセッションに作成
        if (ObjectUtils.isEmpty(a0301w02session.getMeisaiBeanList())) {
            a0301w02session.setMeisaiBeanList(new ArrayList<>());
        }
        // 商品情報を取得
        List<S02V200TSetShnDetailEntity> resultList = a0301w02Service.selectSetShn(
            form.getBaseBean().getApkihoncd(), form.getBaseBean().getShopcd(),
            form.getSetTabBean().getShncdadd(), dbManagementSession.getUnyoDate());
        // 追加行の作成
        List<A0301W02SetMeisaiBean> additionalList = resultList.stream()
            .map(withIndexFunction(a0301w02session.getNextSeq(), (entity, seq) -> {
                A0301W02SetMeisaiBean addBean = A0301W02SetMeisaiBean.builder()
                    // AP基本コード
                    .apkihoncd(entity.getApkihoncd())
                    // ショップコード
                    .shopcd(entity.getShopcd())
                    // セット商品コード
                    .stshncd(form.getBaseBean().getShncd())
                    // 連番
                    .renban(String.valueOf(seq))
                    // 商品コード
                    .shncd2(entity.getShncd())
                    // 構成商品AP基本コード
                    .koseishnapkihoncd(entity.getShnapkihoncd())
                    // 構成商品コード
                    .koseishncd(entity.getShncd())
                    // 商品名称
                    .shnnm3(entity.getShnnm())
                    // 計上区分
                    .keijokbn1(CommonConstants.KBN_KEIJO_URIAGE)
                    // 標準配達価格
                    .dlvryhyojunprice(ItemUtil.editComma(entity.getHyojunprice()))
                    // 標準持ち帰り価格
                    .mchikerihyojunprice(ItemUtil.editComma(entity.getMochikaeriprice()))
                    // 販売配達価格
                    .dlvryhanbaiprice(ZERO)
                    // 販売持ち帰り価格
                    .mchikerihanbaiprice(ZERO)
                    // 数量
                    .suryo(ZERO)
                    // 総数
                    .sokazu(ZERO)
                    // 配達金額
                    .dlvrykin(ZERO)
                    // 持ち帰り金額
                    .mochikaerikin(ZERO)
                    // 売上入数
                    .irisu1(entity.getUriirisu())
                    // 入数単位
                    .irisutani(entity.getIrisutani())
                    // 価格区分
                    .pricekbn2(entity.getPricekbn())
                    // 価格区分名
                    .pricekbnnm2(entity.getPricekbnnm())
                    // 取扱区分
                    .trtskikbn3(entity.getTrtskikbn())
                    // セット区分
                    .stkbn1(entity.getStkbn())
                    // 数量端数管理区分
                    .suryohasukanrikbn(entity.getSuryohasukanrikbn())
                    // 有効区分
                    .yukokbn(entity.getYukokbn())
                    // 更新システム日付
                    .kshnsystmymd(dbManagementSession.getSystemDate())
                    // 更新システム時刻
                    .kshnsystmjikan(dbManagementSession.getSystemTime())
                    // 明細状態
                    .state(STATE.ADDED)
                    // インスタンス生成
                    .build();
                log.debug("追加する明細行の内容 : {}", addBean);
                return addBean;
            })).collect(Collectors.toCollection(LinkedList::new));
        // セッションで保持する明細行の最後に追加
        a0301w02session.getMeisaiBeanList().addAll(additionalList);
        // 次に付与する連番を算出しセッションに反映
        a0301w02session.setNextSeq(getLastSeq());
        // セッションで保持する明細行を返却
        return a0301w02session.getMeisaiBeanList();
    }

    /**
     * セット商品明細リストを元に次に付与する連番の値を取得する
     * 
     * @param detailList セット商品明細リスト
     * @return 連番
     */
    private int getLastSeq() {
        // セッション内の明細リストに明細状態が「追加」のデータがあるかを判定
        boolean isExistsAddedDetail = a0301w02session.getMeisaiBeanList().stream()
            .anyMatch(detail -> STATE.ADDED == detail.getState());
        log.debug("セッション内の明細リストに「追加」のデータがあるかどうか？ : {}", isExistsAddedDetail);
        // 連番算出に用いる明細リストを決定する
        List<A0301W02SetMeisaiBean> detailList = isExistsAddedDetail
            // 行追加されたデータが存在する場合は現在の明細リスト
            ? a0301w02session.getMeisaiBeanList()
            // そうでない場合は初期表示したときの明細リスト
            : a0301w02session.getInitialMeisaiBeanList();
        // 明細リストが空の場合（新規登録の場合）は次の連番として1を返す
        int ret = detailList.isEmpty() ? A0301W02SetMeisaiBean.FIRST_SEQUENCE
            // それ以外の場合は明細リストの最後尾の連番+1
            : detailList.stream()
                // 最後尾までスキップし、要素を取得
                .skip(detailList.size() - 1).findFirst()
                // 連番を取得し整数に変換、取得できない場合は1とする
                .map(detail -> Integer.parseInt(detail.getRenban()) + 1)
                .orElse(A0301W02SetMeisaiBean.FIRST_SEQUENCE);
        log.debug("次に付与する行の連番は >>> {}", ret);
        return ret;
    }

    /**
     * 明細行削除処理
     * 
     * @param baseBean BaseBean
     * @return 行を削除した明細リスト
     */
    private List<A0301W02SetMeisaiBean> deleteRow(A0301W02BaseBean baseBean) {

        List<A0301W02SetMeisaiBean> meisaiList = new ArrayList<>();
        int index = Integer.parseInt(baseBean.getDeleteindex());
        int pagenum = Integer.parseInt(baseBean.getPagenum());
        // 削除行の特定
        int i = pagenum * PAGEABLE_SIZE + index;

        // セッションで保持する明細行を削除
        A0301W02SetMeisaiBean target = a0301w02session.getMeisaiBeanList().remove(i);
        // 削除行がテーブルに登録済データの場合、更新時に削除データを識別するため状態を設定しておく
        a0301w02session.getInitialMeisaiBeanList().stream()
            // 明細BeanはAP基本コード、セット商品コード、連番が等価性対象フィールド
            .filter(initialBean -> initialBean.equals(target)).findFirst()
            // 状態を削除に変更
            .ifPresent(initialBean -> initialBean.setState(STATE.REMOVED));
        // 次に付与する連番を算出し、セッションに反映
        a0301w02session.setNextSeq(getLastSeq());
        // セッションで保持する明細行を返却
        meisaiList = a0301w02session.getMeisaiBeanList();
        return meisaiList;
    }

    /**
     * 明細リストの配達価格の合計を算出する
     * 
     * @param detailList 明細リスト
     * @param hasukbn 売上端数管理区分
     * @return 合計金額
     */
    private BigDecimal summaryDlvryprice(List<A0301W02SetMeisaiBean> detailList, String hasukbn) {
        // 売上端数管理区分に応じた丸めモードを取得
        final RoundingMode mode = convertHasukeisankbn(hasukbn);
        return detailList.stream()
            // 販売配達価格と数量を乗じ、端数処理する
            .map(detail -> {
                BigDecimal suryo = getDecimalValue(detail.getSuryo());
                BigDecimal unt = getDecimalValue(detail.getDlvryhanbaiprice());
                return suryo.multiply(unt).setScale(2, mode);
            })
            // サマリする
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 明細リストの持ち帰り価格の合計を算出する
     * 
     * @param detailList 明細リスト
     * @param hasukbn 売上端数管理区分
     * @return 合計金額
     */
    private BigDecimal summaryMochikaeriprice(List<A0301W02SetMeisaiBean> detailList,
        String hasukbn) {
        // 売上端数管理区分に応じた丸めモードを取得
        final RoundingMode mode = convertHasukeisankbn(hasukbn);
        return detailList.stream()
            // 販売配達価格と数量を乗じ、端数処理する
            .map(detail -> {
                BigDecimal suryo = getDecimalValue(detail.getSuryo());
                BigDecimal unt = getDecimalValue(detail.getMchikerihanbaiprice());
                return suryo.multiply(unt).setScale(2, mode);
            })
            // サマリする
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 更新前チェック、更新時の前処理
     * 
     * @param form フォーム
     */
    private void preExecute(A0301W02Form form) {
        // セッションで保持しているセット商品明細行リストをフォームに設定
        form.setMeisaiBeanList(a0301w02session.getMeisaiBeanList());
        form.setInitialMeisaiBeanList(a0301w02session.getInitialMeisaiBeanList());
        // 更新モードの場合、「セット区分」はhidden項目の値を用いる
        A0301W02BaseBean baseBean = form.getBaseBean();
        if (MODE.UPDATE.equals(baseBean.getMode())) {
            baseBean.setStkbn(baseBean.getStkbnhidden());
        }
        // セット区分が「セット商品」の場合
        A0301W02BaseTabBean baseTabBean = form.getBaseTabBean();
        A0301W02ShopTabBean shopTabBean = form.getShopTabBean();
        if (CommonConstants.FLG_ON.equals(baseBean.getStkbn())) {
            // 現新価格区分には「標準価格」を設定
            baseTabBean.setGenzpricekbn(CommonConstants.KBN_PRICE_HYOJUN);
            baseTabBean.setShinpricekbn(CommonConstants.KBN_PRICE_HYOJUN);
            // 販促設定は対象外にする
            shopTabBean.setHansokusettei(CommonConstants.FLG_OFF);
            // 販促優先区分は「する」にする
            shopTabBean.setHansokuyusenkbn(CommonConstants.FLG_ON);
            // 明細のサマリ金額を現標準単価に設定
            BigDecimal dlvrySum = summaryDlvryprice(form.getMeisaiBeanList(),
                a0301w02session.getUrikinhasukeisankbn());
            BigDecimal mochikaeriSum = summaryMochikaeriprice(form.getMeisaiBeanList(),
                a0301w02session.getUrikinhasukeisankbn());
            baseTabBean.setGenzhyojundlvryprice(dlvrySum.stripTrailingZeros().toPlainString());
            baseTabBean
                .setGenzhyojunmochikaeriprice(mochikaeriSum.stripTrailingZeros().toPlainString());
            // 更新モードの場合は明細のサマリを新標準価格にも設定
            if (MODE.UPDATE.equals(baseBean.getMode())) {
                baseTabBean.setShinhyojundlvryprice(baseTabBean.getGenzhyojundlvryprice());
                baseTabBean
                    .setShinhyojunmochikaeriprice(baseTabBean.getGenzhyojunmochikaeriprice());
            }
        }
        log.debug("A0301W02Form : {}", form);
    }

    /**
     * 単項目チェック
     * 
     * @param form フォーム
     * @param dbManagementSession DB管理セッション情報
     */
    private void validate(A0301W02Form form, DbManagementSession dbManagementSession) {
        validateBaseTab(form, dbManagementSession);
        validateSetTab(form, dbManagementSession);
        validateShopTab(form, dbManagementSession);
    }

    /**
     * 単項目チェック：基本情報タブ
     * 
     * @param form フォーム
     * @param dbManagementSession DB管理セッション情報
     */
    private void validateBaseTab(A0301W02Form form, DbManagementSession dbManagementSession) {
        A0301W02BaseBean baseBean = form.getBaseBean();
        // 基本情報タブが入力不可ならばチェックは不要
        if (baseBean.isDisabledTabItem()) {
            return;
        }
        A0301W02BaseTabBean baseTabBean = form.getBaseTabBean();
        log.debug("Base Tab Bean : {}", baseTabBean);
        // 「大分類コード」必須チェック
        if (StringUtil.isEmpty(baseTabBean.getLctgrycd())) {
            raiseValidationError("baseTabBean.lctgrycd", MessageID.W011, "大分類コード");
        }
        // 「中分類コード」必須チェック
        if (StringUtil.isEmpty(baseTabBean.getMctgrycd())) {
            raiseValidationError("baseTabBean.mctgrycd", MessageID.W011, "中分類コード");
        }
        // 「小分類コード」必須チェック
        if (StringUtil.isEmpty(baseTabBean.getSctgrycd())) {
            raiseValidationError("baseTabBean.sctgrycd", MessageID.W011, "小分類コード");
        }
        // 「（現）商品名称」必須チェック
        if (StringUtil.isEmpty(baseTabBean.getGenzshnnm())) {
            raiseValidationError("baseTabBean.genzshnnm", MessageID.W011, "現商品名称");
        }
        // 「（現）商品名称略称カナ（半角）」必須チェック
        if (StringUtil.isEmpty(baseTabBean.getGenzshnnmkana())) {
            raiseValidationError("baseTabBean.genzshnnmkana", MessageID.W011, "現商品名称カナ(半角)");
        }
        // 「（現）商品名称略称」必須チェック
        if (StringUtil.isEmpty(baseTabBean.getGenzshnnmryakusho())) {
            raiseValidationError("baseTabBean.genzshnnmryakusho", MessageID.W011, "現商品名称略称");
        }

        // 「（現）標準価格_配達」必須チェック
        if (StringUtil.isEmpty(baseTabBean.getGenzhyojundlvryprice())) {
            raiseValidationError("baseTabBean.genzhyojundlvryprice", MessageID.W011, "配達（現）");
        }
        // 「（現）標準価格_持ち帰り」必須チェック
        if (StringUtil.isEmpty(baseTabBean.getGenzhyojunmochikaeriprice())) {
            raiseValidationError("baseTabBean.genzhyojunmochikaeriprice", MessageID.W011,
                "持ち帰り（現）");
        }

        // セット区分が「セット商品」の場合は明細行の単項目チェックが終わってからチェックする
        if (!CommonConstants.FLG_ON.equals(baseBean.getStkbn())) {
            // 「（現）標準価格_配達」バリデーション
            validateUnt(baseTabBean.getGenzhyojundlvryprice(), "baseTabBean.genzhyojundlvryprice",
                "配達（現）");
            // 「（現）標準価格_持ち帰り」バリデーション
            validateUnt(baseTabBean.getGenzhyojunmochikaeriprice(),
                "baseTabBean.genzhyojunmochikaeriprice", "持ち帰り（現）");
        }

        // 更新モードの場合のみ行うチェック
        if (MODE.UPDATE.equals(baseBean.getMode())) {
            // 「（新）商品名称」必須チェック
            if (StringUtil.isEmpty(baseTabBean.getShinshnnm())) {
                raiseValidationError("baseTabBean.shinshnnm", MessageID.W011, "新商品名称");
            }
            // 「（新）商品名称略称カナ（半角）」必須チェック
            if (StringUtil.isEmpty(baseTabBean.getShinshnnmkana())) {
                raiseValidationError("baseTabBean.shinshnnmkana", MessageID.W011, "新商品名称カナ(半角)");
            }
            // 「（新）商品名称略称」必須チェック
            if (StringUtil.isEmpty(baseTabBean.getShinshnnmryakusho())) {
                raiseValidationError("baseTabBean.shinshnnmryakusho", MessageID.W011, "新商品名称略称");
            }

            // 「（新）標準価格_配達」必須チェック
            if (StringUtil.isEmpty(baseTabBean.getShinhyojundlvryprice())) {
                raiseValidationError("baseTabBean.shinhyojundlvryprice", MessageID.W011, "配達（新）");
            }
            // 「（新）標準価格_持ち帰り」必須チェック
            if (StringUtil.isEmpty(baseTabBean.getShinhyojunmochikaeriprice())) {
                raiseValidationError("baseTabBean.shinhyojunmochikaeriprice", MessageID.W011,
                    "持ち帰り（新）");
            }

            // セット区分が「セット商品」の場合は現新が同じ金額となるためチェック不要
            // 標準価格切替日の入力が行われるのはセット商品以外
            if (!CommonConstants.FLG_ON.equals(baseBean.getStkbn())) {
                // 「（新）標準価格_配達」バリデーション
                validateUnt(baseTabBean.getShinhyojundlvryprice(),
                    "baseTabBean.shinhyojundlvryprice", "配達（新）");
                // 「（新）標準価格_持ち帰り」バリデーション
                validateUnt(baseTabBean.getShinhyojunmochikaeriprice(),
                    "baseTabBean.shinhyojunmochikaeriprice", "持ち帰り（新）");
                // 「標準価格切替日」必須チェック
                if (StringUtil.isEmpty(baseTabBean.getHyojunpricekirikaedd())) {
                    raiseValidationError("baseTabBean.hyojunpricekirikaedd", MessageID.W011,
                        "標準価格切替日");
                }
            }
            // 「商品名称切替日」必須チェック
            if (StringUtil.isEmpty(baseTabBean.getShnnmkirikaedd())) {
                raiseValidationError("baseTabBean.shnnmkirikaedd", MessageID.W011, "商品名称切替日");
            }
        }
    }

    /**
     * 単項目チェック：セット商品情報タブ
     * 
     * @param form フォーム
     * @param dbManagementSession DB管理セッション情報
     */
    private void validateSetTab(A0301W02Form form, DbManagementSession dbManagementSession) {
        A0301W02BaseBean baseBean = form.getBaseBean();
        // セット商品情報タブが入力可能かつセット区分が「セット商品」の場合のみ実施するチェック
        if (baseBean.isDisabledTabItem() || !CommonConstants.FLG_ON.equals(baseBean.getStkbn())) {
            return;
        }
        A0301W02BaseTabBean baseTabBean = form.getBaseTabBean();
        form.getMeisaiBeanList().stream().forEach(withIndexConsumer(0, (detail, index) -> {
            // 「配達価格_配達」必須チェック
            if (StringUtil.isEmpty(detail.getDlvryhanbaiprice())) {
                raiseValidationError(getDetailFieldName("dlvryhanbaiprice", index), MessageID.W037,
                    index + 1, "配達販売価格");
            }
            // 「配達価格_配達」バリデーション
            validateUnt(detail.getDlvryhanbaiprice(), "dlvryhanbaiprice", "配達販売価格", index);

            // 「配達価格_持ち帰り」必須チェック
            if (StringUtil.isEmpty(detail.getMchikerihanbaiprice())) {
                raiseValidationError(getDetailFieldName("mchikerihanbaiprice", index),
                    MessageID.W037, index + 1, "持帰販売価格");
            }
            // 「配達価格_持ち帰り」バリデーション
            validateUnt(detail.getMchikerihanbaiprice(), "mchikerihanbaiprice", "持帰販売価格", index);

            // 「数量」 必須チェック
            if (StringUtil.isEmpty(detail.getSuryo())) {
                raiseValidationError(getDetailFieldName("suryo", index), MessageID.W037, index + 1,
                    "数量");
            }
            // 「数量」バリデーション
            validateSuryo(detail.getSuryo(), "suryo", "数量", index);

            // 「総数」桁数チェック
            if (!StringUtil.isEmpty(detail.getSokazu()) && detail.getSokazu().length() > 7) {
                raiseValidationError(getDetailFieldName("sokazu", index), MessageID.W035, index + 1,
                    "総数", "7");
            }

            // 「金額_配達」バリデーション
            validatePrice(detail.getDlvrykin(), "dlvrykin", "配達金額", index);

            // 「金額_持ち帰り」 バリデーション
            validatePrice(detail.getMochikaerikin(), "mochikaerikin", "持帰金額", index);
        }));
        // 「（現）標準価格_配達」バリデーション ※明細行のサマリ結果
        validateUnt(baseTabBean.getGenzhyojundlvryprice(), "baseTabBean.genzhyojundlvryprice",
            "配達（現）");
        // 「（現）標準価格_持ち帰り」バリデーション ※明細行のサマリ結果
        validateUnt(baseTabBean.getGenzhyojunmochikaeriprice(),
            "baseTabBean.genzhyojunmochikaeriprice", "持ち帰り（現）");
    }

    /**
     * 単項目チェック：ショップ情報タブ
     * 
     * @param form フォーム
     * @param dbManagementSession DB管理セッション情報
     */
    private void validateShopTab(A0301W02Form form, DbManagementSession dbManagementSession) {
        A0301W02ShopTabBean shopTabBean = form.getShopTabBean();

        // 仕入情報 - 入数
        if (!StringUtil.isEmpty(shopTabBean.getSiireirisu())) {
            String value = ItemUtil.removeComma(shopTabBean.getSiireirisu());
            // 数値チェック
            if (!value.matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS)) {
                raiseValidationError("shopTabBean.siireirisu", MessageID.W013, "仕入入数", "数字のみ");
            }
            // 正数チェック
            if (getDecimalValue(value).signum() <= 0) {
                raiseValidationError("shopTabBean.siireirisu", MessageID.W159, "仕入入数", "0");
            }
        }

        // 仕入情報 - 仕入単価 バリデーション
        validateUnt(shopTabBean.getSiireunt(), "shopTabBean.siireunt", "仕入単価");

        // 仕入情報 - 発注ロット数
        if (!StringUtil.isEmpty(shopTabBean.getHacchurottokazu())) {
            String value = ItemUtil.removeComma(shopTabBean.getHacchurottokazu());
            // 数値チェック
            if (!value.matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS)) {
                raiseValidationError("shopTabBean.hacchurottokazu", MessageID.W013, "発注ロット数",
                    "数字のみ");
            }
        }

        // 売上・取扱情報 - 入数
        if (!StringUtil.isEmpty(shopTabBean.getUriirisu())) {
            String value = ItemUtil.removeComma(shopTabBean.getUriirisu());
            // 数値チェック
            if (!value.matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS)) {
                raiseValidationError("shopTabBean.uriirisu", MessageID.W013, "売上入数", "数字のみ");
            }
            // 正数チェック
            if (getDecimalValue(value).signum() <= 0) {
                raiseValidationError("shopTabBean.uriirisu", MessageID.W159, "売上入数", "0");
            }
        }

        // オープン価格_(現)_配達 バリデーション
        validateUnt(shopTabBean.getGendlvryprice2(), "shopTabBean.gendlvryprice2", "オープン価格（配達）");
        // オープン価格_(現)_持帰 バリデーション
        validateUnt(shopTabBean.getGenzmochikaeriprice(), "shopTabBean.genzmochikaeriprice",
            "オープン価格（持ち帰り）");
        // オープン価格_(新)_配達 バリデーション
        validateUnt(shopTabBean.getShindlvryprice1(), "shopTabBean.shindlvryprice1", "新オープン価格（配達）");
        // オープン価格_(新)_持帰 バリデーション
        validateUnt(shopTabBean.getShinmochikaeriprice2(), "shopTabBean.shinmochikaeriprice2",
            "新オープン価格（持ち帰り）");

        // まとめ買いポイント発生（新）_数量 バリデーション
        validatePointSuryou(shopTabBean.getShinmtmkisuryo(),"shopTabBean.shinmtmkisuryo","まとめ買い数量（新）");
        // まとめ買いポイント発生（新）_発生ポイント バリデーション
        validatePointHassei(shopTabBean.getShinmtmkipointsu(),"shopTabBean.shinmtmkipointsu","まとめ買い発生ポイント（新）");
        // まとめ買いポイント発生（新）_数量閾値1 バリデーション
        validatePointSuryou(shopTabBean.getShinmtmkidankaisuryo1(),"shopTabBean.shinmtmkidankaisuryo1","まとめ買い段階数量閾値１（新）");
        // まとめ買いポイント発生（新）_発生ポイント1 バリデーション
        validatePointHassei(shopTabBean.getShinmtmkidankaipointsu1(),"shopTabBean.shinmtmkidankaipointsu1","まとめ買い段階発生ポイント１（新）");
        // まとめ買いポイント発生（新）_数量閾値2 バリデーション
        validatePointSuryou(shopTabBean.getShinmtmkidankaisuryo2(),"shopTabBean.shinmtmkidankaisuryo2","まとめ買い段階数量閾値2（新）");
        // まとめ買いポイント発生（新）_発生ポイント2 バリデーション
        validatePointHassei(shopTabBean.getShinmtmkidankaipointsu2(),"shopTabBean.shinmtmkidankaipointsu2","まとめ買い段階発生ポイント2（新）");
        // まとめ買いポイント発生（新）_数量閾値3 バリデーション
        validatePointSuryou(shopTabBean.getShinmtmkidankaisuryo3(),"shopTabBean.shinmtmkidankaisuryo3","まとめ買い段階数量閾値3（新）");
        // まとめ買いポイント発生（新）_発生ポイント3 バリデーション
        validatePointHassei(shopTabBean.getShinmtmkidankaipointsu3(),"shopTabBean.shinmtmkidankaipointsu3","まとめ買い段階発生ポイント3（新）");
        // まとめ買いポイント発生（新）_数量閾値4 バリデーション
        validatePointSuryou(shopTabBean.getShinmtmkidankaisuryo4(),"shopTabBean.shinmtmkidankaisuryo4","まとめ買い段階数量閾値4（新）");
        // まとめ買いポイント発生（新）_発生ポイント4 バリデーション
        validatePointHassei(shopTabBean.getShinmtmkidankaipointsu4(),"shopTabBean.shinmtmkidankaipointsu4","まとめ買い段階発生ポイント4（新）");
        // まとめ買いポイント発生（新）_数量閾値5 バリデーション
        validatePointSuryou(shopTabBean.getShinmtmkidankaisuryo5(),"shopTabBean.shinmtmkidankaisuryo5","まとめ買い段階数量閾値5（新）");
        // まとめ買いポイント発生（新）_発生ポイント5 バリデーション
        validatePointHassei(shopTabBean.getShinmtmkidankaipointsu5(),"shopTabBean.shinmtmkidankaipointsu5","まとめ買い段階発生ポイント5（新）");

        // 「（現）ポイント発生_ステージ設定×倍率」バリデーション
        if (!StringUtil.isEmpty(shopTabBean.getGenshnpointsu1())) {
            validateKangenPoint(shopTabBean.getGenshnpointsu1(),"shopTabBean.genshnpointsu1","ステージ設定×倍率（現）");
        }
        // 「（現）ポイント発生_固定」バリデーション
        if (!StringUtil.isEmpty(shopTabBean.getGenshnpointsu2())) {
            validateKoteiPoint(shopTabBean.getGenshnpointsu2(),"shopTabBean.genshnpointsu2","固定（現）");
        }
        // まとめ買いポイント発生（現）_数量 バリデーション
        validatePointSuryou(shopTabBean.getGenmtmkisuryo(),"shopTabBean.genmtmkisuryo","まとめ買い数量（現）");
        // まとめ買いポイント発生（現）_発生ポイント バリデーション
        validatePointHassei(shopTabBean.getGenmtmkipointsu(),"shopTabBean.genmtmkipointsu","まとめ買い発生ポイント（現）");
        // まとめ買いポイント発生（現）_数量閾値1 バリデーション
        validatePointSuryou(shopTabBean.getGenmtmkidankaisuryo1(),"shopTabBean.genmtmkidankaisuryo1","まとめ買い段階数量閾値１（現）");
        // まとめ買いポイント発生（現）_発生ポイント1 バリデーション
        validatePointHassei(shopTabBean.getGenmtmkidankaipointsu1(),"shopTabBean.genmtmkidankaipointsu1","まとめ買い段階発生ポイント１（現）");
        // まとめ買いポイント発生（現）_数量閾値2 バリデーション
        validatePointSuryou(shopTabBean.getGenmtmkidankaisuryo2(),"shopTabBean.genmtmkidankaisuryo2","まとめ買い段階数量閾値2（現）");
        // まとめ買いポイント発生（現）_発生ポイント2 バリデーション
        validatePointHassei(shopTabBean.getGenmtmkidankaipointsu2(),"shopTabBean.genmtmkidankaipointsu2","まとめ買い段階発生ポイント2（現）");
        // まとめ買いポイント発生（現）_数量閾値3 バリデーション
        validatePointSuryou(shopTabBean.getGenmtmkidankaisuryo3(),"shopTabBean.genmtmkidankaisuryo3","まとめ買い段階数量閾値3（現）");
        // まとめ買いポイント発生（現）_発生ポイント3 バリデーション
        validatePointHassei(shopTabBean.getGenmtmkidankaipointsu3(),"shopTabBean.genmtmkidankaipointsu3","まとめ買い段階発生ポイント3（現）");
        // まとめ買いポイント発生（現）_数量閾値4 バリデーション
        validatePointSuryou(shopTabBean.getGenmtmkidankaisuryo4(),"shopTabBean.genmtmkidankaisuryo4","まとめ買い段階数量閾値4（現）");
        // まとめ買いポイント発生（現）_発生ポイント4 バリデーション
        validatePointHassei(shopTabBean.getGenmtmkidankaipointsu4(),"shopTabBean.genmtmkidankaipointsu4","まとめ買い段階発生ポイント4（現）");
        // まとめ買いポイント発生（現）_数量閾値5 バリデーション
        validatePointSuryou(shopTabBean.getGenmtmkidankaisuryo5(),"shopTabBean.genmtmkidankaisuryo5","まとめ買い段階数量閾値5（現）");
        // まとめ買いポイント発生（現）_発生ポイント5 バリデーション
        validatePointHassei(shopTabBean.getGenmtmkidankaipointsu5(),"shopTabBean.genmtmkidankaipointsu5","まとめ買い段階発生ポイント5（現）");
        // 更新モードの場合のみ行うチェック
        if (MODE.UPDATE.equals(form.getBaseBean().getMode())) {
            // 「（新）ポイント発生_ステージ設定×倍率」バリデーション
            if (!StringUtil.isEmpty(shopTabBean.getShinshnpointsu1())) {
                validateKangenPoint(shopTabBean.getShinshnpointsu1(),"shopTabBean.shinshnpointsu1","ステージ設定×倍率（新）");
            }
            // 「（新）ポイント発生_固定」バリデーション
            if (!StringUtil.isEmpty(shopTabBean.getShinshnpointsu2())) {
                validateKoteiPoint(shopTabBean.getShinshnpointsu2(),"shopTabBean.shinshnpointsu2","固定（新）");
            }
            // 「ポイント発生切替日」必須チェック
            if (StringUtil.isEmpty(shopTabBean.getShnpointkirikaedd())) {
                raiseValidationError("shopTabBean.shnpointkirikaedd", MessageID.W011, "ポイント発生切替日");
            }
            // 「まとめ買いポイント発生切替日」必須チェック
            if (StringUtil.isEmpty(shopTabBean.getMtmkipointkirikaedd())) {
                raiseValidationError("shopTabBean.mtmkipointkirikaedd", MessageID.W011, "まとめ買いポイント発生切替日");
            }
        }

        // 販促設定_配達価格 バリデーション
        validateUnt(shopTabBean.getDlvryprice(), "shopTabBean.dlvryprice", "配達価格");
        // 販促設定_持ち帰り価格 バリデーション
        validateUnt(shopTabBean.getOmchkrprice(), "shopTabBean.omchkrprice", "持ち帰り価格");

        // 在庫単価 バリデーション
        validateUnt(shopTabBean.getZaikount1(), "shopTabBean.zaikount1", "在庫単価");

        // 在庫アラーム数量
        if (!StringUtil.isEmpty(shopTabBean.getZaikoaramusuryo())) {
            String value = ItemUtil.removeComma(shopTabBean.getZaikoaramusuryo());
            if (!value.matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS)) {
                raiseValidationError("shopTabBean.zaikoaramusuryo", MessageID.W013, "在庫アラーム数量",
                    "数字のみ");
            }
        }
    }

    /**
     * 相関チェック
     * 
     * @param form フォーム
     * @param dbManagementSession DB管理セッション情報
     */
    private void check(A0301W02Form form, DbManagementSession dbManagementSession) {

        /*
         * 日付範囲チェックについては発生しないので実装しない（相関チェックNo10,11,21,22,41,42,43,49,54）
         */

        A0301W02BaseBean baseBean = form.getBaseBean();
        A0301W02BaseTabBean baseTabBean = form.getBaseTabBean();
        A0301W02ShopTabBean shopTabBean = form.getShopTabBean();
        List<A0301W02SetMeisaiBean> detailList = form.getMeisaiBeanList();

        // ショップ情報タブの相関チェック
        checkShopTab(form, dbManagementSession);

        if (CommonConstants.FLG_ON.equals(baseBean.getStkbn())) {
            // セット商品の場合の相関チェック
            // 相関チェックNo3
            if (detailList.isEmpty()) {
                raiseValidationError("baseBean.stkbn", MessageID.W113, "セット商品");
            }
            // 相関チェックNo4
            if (CommonConstants.KBN_TORIATSUKAI_URIAGESHIIRE.equals(baseTabBean.getTrtskikbn())
                || CommonConstants.KBN_TORIATSUKAI_SHIIRE.equals(baseTabBean.getTrtskikbn())) {
                List<FormField> fields = List.of(new FormField("baseBean.stkbn"),
                    new FormField("baseTabBean.trtskikbn"));
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W171, "セット商品に",
                    "取扱区分が売上のみ以外は選択");
            }
            // 相関チェックNo5
            if (CommonConstants.FLG_ON.equals(shopTabBean.getZaikokanrikbn())) {
                List<FormField> fields = List.of(new FormField("baseBean.stkbn"),
                    new FormField("shopTabBean.zaikokanrikbn"));
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W171, "セット商品に",
                    "在庫管理区分「する」は選択");
            }

            // セット商品明細行の相関チェック
            checkSetTab(form, dbManagementSession);

            // 相関チェックNo8
            if (CommonConstants.KBN_TAX_KEIGEN.equals(shopTabBean.getTaxkbn())) {
                final BigDecimal limit = new BigDecimal("10000");
                BigDecimal dlvrySum =
                    summaryDlvryprice(detailList, a0301w02session.getUrikinhasukeisankbn());
                BigDecimal mochikaeriSum =
                    summaryMochikaeriprice(detailList, a0301w02session.getUrikinhasukeisankbn());
                if (limit.compareTo(dlvrySum) < 0 || limit.compareTo(mochikaeriSum) < 0) {
                    raiseValidationError("shopTabBean.taxkbn", MessageID.W171,
                        "セット商品の価格が10000円を超える場合", "税区分に「軽減税率」が選択");
                }
            }
            // 相関チェックNo9
            if (MODE.UPDATE.equals(baseBean.getMode())) {
                long count = a0301w02Service.countJuchuDenpyo(baseBean.getApkihoncd(),
                    baseBean.getShopcd(), baseBean.getShncd());
                if (count > 0) {
                    raiseValidationError("baseBean.stkbn", MessageID.W171, "受注中の伝票が存在する場合",
                        "セット商品の更新は");
                }
            }
        } else {
            // 単独商品の場合の相関チェック
            // 相関チェックNo1
            if (!detailList.isEmpty()) {
                raiseValidationError("baseBean.stkbn", MessageID.W106, "セット商品");
            }
            // 相関チェックNo2
            if (CommonConstants.KBN_TAX_KOMEISAI.equals(shopTabBean.getTaxkbn())) {
                raiseValidationError("baseBean.taxkbn", MessageID.W106, "単独商品", "税区分に「子明細商品」");
            }

            // 更新モードの場合のみチェックする
            if (MODE.UPDATE.equals(baseBean.getMode())) {
                // ショップ情報タブ.有効区分を有効から無効に変更した場合
                if (CommonConstants.YUKOKBN_1.equals(shopTabBean.getYukokbn0())
                    && CommonConstants.YUKOKBN_0.equals(shopTabBean.getYukokbn())) {
                    // 有効なセット商品が存在しているかのチェック
                    if (a0301w02Service.isExistsAvailableStshn(baseBean.getApkihoncd(),
                        baseBean.getShopcd(), baseBean.getShncd(), shopTabBean.getShnapkihoncd())) {
                        raiseValidationError("shopTabBean.yukokbn", MessageID.W260,
                            "有効なセット商品に含まれているため無効にすることは出来ません");
                    }
                }
            }
        }

    }

    /**
     * 相関チェック：セット商品情報タブ
     * 
     * @param form フォーム
     * @param dbManagementSession DB管理セッション情報
     */
    private void checkSetTab(A0301W02Form form, DbManagementSession dbManagementSession) {
        // セット区分が「セット商品」の場合のみ実施するチェック
        A0301W02BaseBean baseBean = form.getBaseBean();
        if (!CommonConstants.FLG_ON.equals(baseBean.getStkbn())) {
            return;
        }
        // 調整用商品コード取得
        String chouseiShncd =
            s02m006tService.searchForMoji(CommonConstants.APCD_000, CommonConstants.SYS_SHOPCD_000,
                CommonConstants.SYS_SETTEINO_139, CommonConstants.SETTEICD_139_1);

        // セット商品の明細行に対してチェックを行う
        form.getMeisaiBeanList().stream().forEach(withIndexConsumer(0, (detail, index) -> {
            // 相関チェックNo6
            if (CommonConstants.KBN_TORIATSUKAI_SHIIRE.equals(detail.getTrtskikbn3())) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W168);
            }
            // 相関チェックNo7
            if (CommonConstants.FLG_ON.equals(detail.getStkbn1())) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W169);
            }
            // 相関チェックNo18
            if (CommonConstants.FLG_OFF.equals(form.getBaseTabBean().getSuryohasukanrikbn())) {
                if (detail.getSuryo().indexOf(".") > 0) {
                    raiseValidationError(getDetailFieldName("suryo", index), MessageID.W171,
                        "数量端数管理区分がなしの場合", "小数の値を入力");
                }
            }
            // 相関チェックNo19
            if (chouseiShncd.equals(detail.getShncd2())) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W171, "調整用商品コード", "入力");
            }
            // 相関チェックNo20
            if (StringUtil.isEmpty(detail.getShnnm3())) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W260,
                    "取り込まれていない構成商品があります");
            }

            // 相関チェックNo47
            if (CommonConstants.YUKOKBN_0.equals(detail.getYukokbn())) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W260,
                    "構成商品に無効な商品が含まれています");
            }
        }));
    }

    /**
     * 相関チェック：ショップ情報タブ
     * 
     * @param form フォーム
     * @param dbManagementSession DB管理セッション情報
     */
    private void checkShopTab(A0301W02Form form, DbManagementSession dbManagementSession) {
        A0301W02BaseBean baseBean = form.getBaseBean();
        A0301W02BaseTabBean baseTabBean = form.getBaseTabBean();
        A0301W02ShopTabBean shopTabBean = form.getShopTabBean();

        // 相関チェックNo23
        if (!DateTimeUtil.isLater(shopTabBean.getHanbaidkikanstr(),
            shopTabBean.getHanbaidkikanend())) {
            List<FormField> fields = List.of(new FormField("shopTabBean.hanbaidkikanstr"),
                new FormField("shopTabBean.hanbaidkikanend"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W093, "販売期間（開始）",
                "販売期間（終了）");
        }

        // 相関チェックNo24
        if (MODE.UPDATE.equals(baseBean.getMode())) {
            if (CommonConstants.KBN_SHNZOKUSEI_HONBU.equals(baseBean.getShnzokusei())
                && CommonConstants.APCD_001.equals(baseBean.getApkihoncd())
                && !StringUtil.isEmpty(baseBean.getShncd())) {
                String taxkbn = a0301w02Service.getHnbShnTaxKbn(baseBean.getShncd());
                if (!StringUtil.isEmpty(taxkbn) && !taxkbn.equals(shopTabBean.getTaxkbn())) {
                    raiseValidationError("shopTabBean.taxkbn", MessageID.W171, "本部商品の税区分と異なる税区分",
                        "選択");
                }
            }

        }

        // 仕入情報（取扱区分が「売上仕入両方」または「仕入のみ」の場合）
        if (CommonConstants.KBN_TORIATSUKAI_URIAGESHIIRE.equals(baseTabBean.getTrtskikbn())
            || CommonConstants.KBN_TORIATSUKAI_SHIIRE.equals(baseTabBean.getTrtskikbn())) {
            // 相関チェックNo30
            if (StringUtil.isEmpty(shopTabBean.getSiiretrtskitani())) {
                raiseValidationError("shopTabBean.siiretrtskitani", MessageID.W113, "仕入取扱単位");
            }
            // 相関チェックNo31
            if (StringUtil.isEmpty(shopTabBean.getSiireirisu())) {
                raiseValidationError("shopTabBean.siireirisu", MessageID.W113, "仕入入数");
            }
            // 相関チェックNo32
            if (StringUtil.isEmpty(shopTabBean.getIrisutani1())) {
                raiseValidationError("shopTabBean.irisutani1", MessageID.W113, "仕入入数単位");
            }
            // 相関チェックNo33
            if (StringUtil.isEmpty(shopTabBean.getSiireunt())) {
                raiseValidationError("shopTabBean.siireunt", MessageID.W113, "仕入単価");
            }
            // 相関チェックNo34
            if (StringUtil.isEmpty(shopTabBean.getHacchurottokazu())) {
                raiseValidationError("shopTabBean.hacchurottokazu", MessageID.W113, "発注ロット数");
            }
        }

        // 売上・取扱情報（取扱区分が「仕入のみ」以外の場合）
        if (!CommonConstants.KBN_TORIATSUKAI_SHIIRE.equals(baseTabBean.getTrtskikbn())) {
            // 相関チェックNo35
            if (StringUtil.isEmpty(shopTabBean.getUritrtskitani())) {
                raiseValidationError("shopTabBean.uritrtskitani", MessageID.W113, "売上取扱単位");
            }
            // 相関チェックNo36
            if (StringUtil.isEmpty(shopTabBean.getUriirisu())) {
                raiseValidationError("shopTabBean.uriirisu", MessageID.W113, "売上入数");
            }
            // 相関チェックNo37
            if (StringUtil.isEmpty(shopTabBean.getIrisutani2())) {
                raiseValidationError("shopTabBean.irisutani2", MessageID.W113, "売上入数単位");
            }
        }

        // 仕入単位変換区分（取扱区分が「売上のみ」の場合）
        if (!CommonConstants.KBN_TORIATSUKAI_URIAGESHIIRE.equals(baseTabBean.getTrtskikbn())
            && !CommonConstants.KBN_TORIATSUKAI_SHIIRE.equals(baseTabBean.getTrtskikbn())) {
            // 相関チェックNo38
            if (CommonConstants.FLG_ON.equals(shopTabBean.getSiiretanihenkankbn())) {
                raiseValidationError("shopTabBean.siiretanihenkankbn", MessageID.W171,
                    "取扱区分が「売上のみ」の場合", "仕入単位変換区分「あり」は選択");
            }
        }

        // 仕入単位変換区分（取扱区分が「仕入のみ」の場合）
        if (CommonConstants.KBN_TORIATSUKAI_SHIIRE.equals(baseTabBean.getTrtskikbn())) {
            // 相関チェックNo17
            if (CommonConstants.FLG_ON.equals(shopTabBean.getSiiretanihenkankbn())) {
                raiseValidationError("shopTabBean.trtskikbn", MessageID.W250, "取扱区分：仕入のみ",
                    "仕入単位変換区分：あり");
            }
        }

        // 取扱単位（取扱区分が「売上仕入両方」の場合）
        if (CommonConstants.KBN_TORIATSUKAI_URIAGESHIIRE.equals(baseTabBean.getTrtskikbn())) {
            // 仕入単位変換区分が「あり」の場合
            if (CommonConstants.FLG_ON.equals(shopTabBean.getSiiretanihenkankbn())) {
                // 相関チェックNo40 仕入単位変換
                if (!Objects.equals(shopTabBean.getIrisutani1(), shopTabBean.getUritrtskitani())) {
                    List<FormField> fields = List.of(new FormField("shopTabBean.irisutani1"),
                        new FormField("shopTabBean.uritrtskitani"));
                    throw BusinessExceptionFactory.validationFailed(fields, MessageID.W126,
                        "仕入入数単位", "売上取扱単位");
                }
            }
            // 仕入取扱単位と売上取扱単位の値が異なる場合
            if (!Objects.equals(shopTabBean.getSiiretrtskitani(), shopTabBean.getUritrtskitani())) {
                // 相関チェックNo39 取扱単位
                if (CommonConstants.FLG_OFF.equals(shopTabBean.getSiiretanihenkankbn())) {
                    List<FormField> fields = List.of(new FormField("shopTabBean.siiretrtskitani"),
                        new FormField("shopTabBean.uritrtskitani"));
                    throw BusinessExceptionFactory.validationFailed(fields, MessageID.W126,
                        "仕入取扱単位", "売上取扱単位");
                }
            }
        }

        // オープン価格（価格区分が「オープン価格」の場合のみ）
        if (CommonConstants.KBN_PRICE_OPEN.equals(baseTabBean.getGenzpricekbn())) {
            // 相関チェックNo15
            if (StringUtil.isEmpty(shopTabBean.getGendlvryprice2())) {
                raiseValidationError("shopTabBean.gendlvryprice2", MessageID.W113, "オープン価格（配達）");
            }
            // 相関チェックNo16
            if (StringUtil.isEmpty(shopTabBean.getGenzmochikaeriprice())) {
                raiseValidationError("shopTabBean.genzmochikaeriprice", MessageID.W113,
                    "オープン価格（持ち帰り）");
            }
            if (MODE.UPDATE.equals(baseBean.getMode())) {
                // 相関チェックNo12
                if (StringUtil.isEmpty(shopTabBean.getShindlvryprice1())) {
                    raiseValidationError("shopTabBean.shindlvryprice1", MessageID.W113,
                        "新オープン価格（配達）");
                }
                // 相関チェックNo13
                if (StringUtil.isEmpty(shopTabBean.getShinmochikaeriprice2())) {
                    raiseValidationError("shopTabBean.shinmochikaeriprice2", MessageID.W113,
                        "新オープン価格（持ち帰り）");
                }
                // 相関チェックNo14
                if (StringUtil.isEmpty(shopTabBean.getOpnpricekirikaedd())) {
                    raiseValidationError("shopTabBean.opnpricekirikaedd", MessageID.W113, "切替日");
                }
            }
        }

        // ポイント発生区分（新）が空でない場合
        if(!StringUtil.isEmpty(shopTabBean.getShinshnpointhasseikbn())) {
            // 相関チェックNo.50
            if(shopTabBean.getShinshnpointhasseikbn().equals(CommonConstants.SHNPOINTHASSEIKBN_VAL_2) 
                    && StringUtil.isEmpty(shopTabBean.getShinshnpointsu1())) {
                raiseValidationError("shopTabBean.shinshnpointsu1", MessageID.W113, "ステージ設定×倍率（新）");
            }
            // 相関チェックNo.51
            if(shopTabBean.getShinshnpointhasseikbn().equals(CommonConstants.SHNPOINTHASSEIKBN_VAL_3) 
                    && StringUtil.isEmpty(shopTabBean.getShinshnpointsu2())) {
                raiseValidationError("shopTabBean.shinshnpointsu2", MessageID.W113, "固定値（新）");
            }
        }
        // ポイント発生区分（現）が空でない場合
        if(!StringUtil.isEmpty(shopTabBean.getGenshnpointhasseikbn())) {
            // 相関チェックNo.52
            if(shopTabBean.getGenshnpointhasseikbn().equals(CommonConstants.SHNPOINTHASSEIKBN_VAL_2) 
                    && StringUtil.isEmpty(shopTabBean.getGenshnpointsu1())) {
                raiseValidationError("shopTabBean.genshnpointsu1", MessageID.W113, "ステージ設定×倍率（現）");
            }
            // 相関チェックNo.53
            if(shopTabBean.getGenshnpointhasseikbn().equals(CommonConstants.SHNPOINTHASSEIKBN_VAL_3) 
                    && StringUtil.isEmpty(shopTabBean.getGenshnpointsu2())) {
                raiseValidationError("shopTabBean.genshnpointsu2", MessageID.W113, "固定値（現）");
            }
        }
        // まとめ買いポイント発生区分（新）が空でない場合
        if(!StringUtil.isEmpty(shopTabBean.getShinmtmkipointkbn())) {
            // 相関チェックNo.55
            if ((shopTabBean.getShinmtmkipointkbn().equals(CommonConstants.MTMKIPOINTKBNMEI_VAL_1)
                    || shopTabBean.getShinmtmkipointkbn().equals(CommonConstants.MTMKIPOINTKBNMEI_VAL_2))
                    && StringUtil.isEmpty(shopTabBean.getShinmtmkisuryo())) {
                raiseValidationError("shopTabBean.shinmtmkisuryo", MessageID.W113, "まとめ買い数量（新）");
            }
            // 相関チェックNo.56
            if ((shopTabBean.getShinmtmkipointkbn().equals(CommonConstants.MTMKIPOINTKBNMEI_VAL_1)
                    || shopTabBean.getShinmtmkipointkbn().equals(CommonConstants.MTMKIPOINTKBNMEI_VAL_2))
                    && StringUtil.isEmpty(shopTabBean.getShinmtmkipointsu())) {
                raiseValidationError("shopTabBean.shinmtmkipointsu", MessageID.W113, "まとめ買い発生ポイント（新）");
            }
            // ポイント発生区分が段階的に発生の場合
            if (shopTabBean.getShinmtmkipointkbn().equals(CommonConstants.MTMKIPOINTKBNMEI_VAL_3)) {
                // 必須チェック
                // 相関チェックNo.57
                if (StringUtil.isEmpty(shopTabBean.getShinmtmkidankaisuryo1())) {
                    raiseValidationError("shopTabBean.shinmtmkidankaisuryo1", MessageID.W113, "数量閾値1（新）");
                }
                // 相関チェックNo.58
                if (StringUtil.isEmpty(shopTabBean.getShinmtmkidankaipointsu1())) {
                    raiseValidationError("shopTabBean.shinmtmkidankaipointsu1", MessageID.W113, "発生1（新）");
                }
                // 閾値入力チェック
                // 相関チェックNo.59
                if (!StringUtil.isEmpty(shopTabBean.getShinmtmkidankaisuryo3())
                        && StringUtil.isEmpty(shopTabBean.getShinmtmkidankaisuryo2())) {
                    raiseValidationError("shopTabBean.shinmtmkidankaisuryo2", MessageID.W247, "数量閾値3（新）","数量閾値2（新）");
                }
                // 相関チェックNo.60
                if (!StringUtil.isEmpty(shopTabBean.getShinmtmkidankaisuryo4())
                        && StringUtil.isEmpty(shopTabBean.getShinmtmkidankaisuryo3())) {
                    raiseValidationError("shopTabBean.shinmtmkidankaisuryo3", MessageID.W247, "数量閾値4（新）","数量閾値2,3（新）");
                }
                // 相関チェックNo.61
                if (!StringUtil.isEmpty(shopTabBean.getShinmtmkidankaisuryo5())
                        && StringUtil.isEmpty(shopTabBean.getShinmtmkidankaisuryo4())) {
                    raiseValidationError("shopTabBean.shinmtmkidankaisuryo4", MessageID.W247, "数量閾値5（新）","数量閾値2,3,4（新）");
                }
                // 入力必須チェック
                // 相関チェックNo.62
                if (!StringUtil.isEmpty(shopTabBean.getShinmtmkidankaisuryo2())
                        && StringUtil.isEmpty(shopTabBean.getShinmtmkidankaipointsu2())) {
                    raiseValidationError("shopTabBean.shinmtmkidankaipointsu2", MessageID.W247, "数量閾値2（新）","発生2（新）");
                }
                // 相関チェックNo.63
                if (StringUtil.isEmpty(shopTabBean.getShinmtmkidankaisuryo2())
                        && !StringUtil.isEmpty(shopTabBean.getShinmtmkidankaipointsu2())) {
                    raiseValidationError("shopTabBean.shinmtmkidankaisuryo2", MessageID.W247, "発生2（新）","数量閾値2（新）");
                }
                // 相関チェックNo.64
                if (!StringUtil.isEmpty(shopTabBean.getShinmtmkidankaisuryo3())
                        && StringUtil.isEmpty(shopTabBean.getShinmtmkidankaipointsu3())) {
                    raiseValidationError("shopTabBean.shinmtmkidankaipointsu3", MessageID.W247, "数量閾値3（新）","発生3（新）");
                }
                // 相関チェックNo.65
                if (StringUtil.isEmpty(shopTabBean.getShinmtmkidankaisuryo3())
                        && !StringUtil.isEmpty(shopTabBean.getShinmtmkidankaipointsu3())) {
                    raiseValidationError("shopTabBean.shinmtmkidankaisuryo3", MessageID.W247, "発生3（新）","数量閾値3（新）");
                }
                // 相関チェックNo.66
                if (!StringUtil.isEmpty(shopTabBean.getShinmtmkidankaisuryo4())
                        && StringUtil.isEmpty(shopTabBean.getShinmtmkidankaipointsu4())) {
                    raiseValidationError("shopTabBean.shinmtmkidankaipointsu4", MessageID.W247, "数量閾値4（新）","発生4（新）");
                }
                // 相関チェックNo.67
                if (StringUtil.isEmpty(shopTabBean.getShinmtmkidankaisuryo4())
                        && !StringUtil.isEmpty(shopTabBean.getShinmtmkidankaipointsu4())) {
                    raiseValidationError("shopTabBean.shinmtmkidankaisuryo4", MessageID.W247, "発生4（新）","数量閾値4（新）");
                }
                // 相関チェックNo.68
                if (!StringUtil.isEmpty(shopTabBean.getShinmtmkidankaisuryo5())
                        && StringUtil.isEmpty(shopTabBean.getShinmtmkidankaipointsu5())) {
                    raiseValidationError("shopTabBean.shinmtmkidankaipointsu5", MessageID.W247, "数量閾値5（新）","発生5（新）");
                }
                // 相関チェックNo.69
                if (StringUtil.isEmpty(shopTabBean.getShinmtmkidankaisuryo5())
                        && !StringUtil.isEmpty(shopTabBean.getShinmtmkidankaipointsu5())) {
                    raiseValidationError("shopTabBean.shinmtmkidankaisuryo5", MessageID.W247, "発生5（新）","数量閾値5（新）");
                }
                // 閾値チェック
                // 相関チェックNo.70
                if (!StringUtil.isEmpty(shopTabBean.getShinmtmkidankaisuryo1())
                        && !StringUtil.isEmpty(shopTabBean.getShinmtmkidankaisuryo2())) {
                    int shinmtmkidankaisuryo1 = Integer.parseInt(ItemUtil.removeComma(shopTabBean.getShinmtmkidankaisuryo1()));
                    int shinmtmkidankaisuryo2 = Integer.parseInt(ItemUtil.removeComma(shopTabBean.getShinmtmkidankaisuryo2()));
                    if (shinmtmkidankaisuryo1 >= shinmtmkidankaisuryo2) {
                        raiseValidationError("shopTabBean.shinmtmkidankaisuryo2", MessageID.W278, "2","1");
                    }
                }
                // 相関チェックNo.71
                if (!StringUtil.isEmpty(shopTabBean.getShinmtmkidankaisuryo2())
                        && !StringUtil.isEmpty(shopTabBean.getShinmtmkidankaisuryo3())) {
                    int shinmtmkidankaisuryo2 = Integer.parseInt(ItemUtil.removeComma(shopTabBean.getShinmtmkidankaisuryo2()));
                    int shinmtmkidankaisuryo3 = Integer.parseInt(ItemUtil.removeComma(shopTabBean.getShinmtmkidankaisuryo3()));
                    if (shinmtmkidankaisuryo2 >= shinmtmkidankaisuryo3) {
                        raiseValidationError("shopTabBean.shinmtmkidankaisuryo3", MessageID.W278, "3","2");
                    }
                }
                // 相関チェックNo.72
                if (!StringUtil.isEmpty(shopTabBean.getShinmtmkidankaisuryo3())
                        && !StringUtil.isEmpty(shopTabBean.getShinmtmkidankaisuryo4())) {
                    int shinmtmkidankaisuryo3 = Integer.parseInt(ItemUtil.removeComma(shopTabBean.getShinmtmkidankaisuryo3()));
                    int shinmtmkidankaisuryo4 = Integer.parseInt(ItemUtil.removeComma(shopTabBean.getShinmtmkidankaisuryo4()));
                    if (shinmtmkidankaisuryo3 >= shinmtmkidankaisuryo4) {
                        raiseValidationError("shopTabBean.shinmtmkidankaisuryo4", MessageID.W278, "4","3");
                    }
                }
                // 相関チェックNo.73
                if (!StringUtil.isEmpty(shopTabBean.getShinmtmkidankaisuryo4())
                        && !StringUtil.isEmpty(shopTabBean.getShinmtmkidankaisuryo5())) {
                    int shinmtmkidankaisuryo4 = Integer.parseInt(ItemUtil.removeComma(shopTabBean.getShinmtmkidankaisuryo4()));
                    int shinmtmkidankaisuryo5 = Integer.parseInt(ItemUtil.removeComma(shopTabBean.getShinmtmkidankaisuryo5()));
                    if (shinmtmkidankaisuryo4 >= shinmtmkidankaisuryo5) {
                        raiseValidationError("shopTabBean.shinmtmkidankaisuryo5", MessageID.W278, "5","4");
                    }
                }
            }

            // まとめ買いポイント発生区分（現）が空でない場合
            if(!StringUtil.isEmpty(shopTabBean.getGenmtmkipointkbn())) {
                // 相関チェックNo.74
                if((shopTabBean.getGenmtmkipointkbn().equals(CommonConstants.MTMKIPOINTKBNMEI_VAL_1)
                        || shopTabBean.getGenmtmkipointkbn().equals(CommonConstants.MTMKIPOINTKBNMEI_VAL_2))
                        && StringUtil.isEmpty(shopTabBean.getGenmtmkisuryo())) {
                    raiseValidationError("shopTabBean.genmtmkisuryo", MessageID.W113, "まとめ買い数量（現）");
                }
                // 相関チェックNo.75
                if((shopTabBean.getGenmtmkipointkbn().equals(CommonConstants.MTMKIPOINTKBNMEI_VAL_1)
                        || shopTabBean.getGenmtmkipointkbn().equals(CommonConstants.MTMKIPOINTKBNMEI_VAL_2))
                        && StringUtil.isEmpty(shopTabBean.getGenmtmkipointsu())) {
                    raiseValidationError("shopTabBean.genmtmkipointsu", MessageID.W113, "まとめ買い発生ポイント（現）");
                }
                // （現）ポイント発生区分が段階的に発生の場合
                if (shopTabBean.getGenmtmkipointkbn().equals(CommonConstants.MTMKIPOINTKBNMEI_VAL_3)) {
                    // 必須チェック
                    // 相関チェックNo.76
                    if (StringUtil.isEmpty(shopTabBean.getGenmtmkidankaisuryo1())) {
                        raiseValidationError("shopTabBean.genmtmkidankaisuryo1", MessageID.W113, "数量閾値1（現）");
                    }
                    // 相関チェックNo.77
                    if (StringUtil.isEmpty(shopTabBean.getGenmtmkidankaipointsu1())) {
                        raiseValidationError("shopTabBean.genmtmkidankaipointsu1", MessageID.W113, "発生1（現）");
                    }
                    // 閾値入力チェック
                    // 相関チェックNo.78
                    if (!StringUtil.isEmpty(shopTabBean.getGenmtmkidankaisuryo3())
                            && StringUtil.isEmpty(shopTabBean.getGenmtmkidankaisuryo2())) {
                        raiseValidationError("shopTabBean.genmtmkidankaisuryo2", MessageID.W247, "数量閾値3（現）","数量閾値2（現）");
                    }
                    // 相関チェックNo.79
                    if (!StringUtil.isEmpty(shopTabBean.getGenmtmkidankaisuryo4())
                            && StringUtil.isEmpty(shopTabBean.getGenmtmkidankaisuryo3())) {
                        raiseValidationError("shopTabBean.genmtmkidankaisuryo3", MessageID.W247, "数量閾値4（現）","数量閾値2,3（現）");
                    }
                    // 相関チェックNo.80
                    if (!StringUtil.isEmpty(shopTabBean.getGenmtmkidankaisuryo5())
                            && StringUtil.isEmpty(shopTabBean.getGenmtmkidankaisuryo4())) {
                        raiseValidationError("shopTabBean.genmtmkidankaisuryo4", MessageID.W247, "数量閾値5（現）","数量閾値2,3,4（現）");
                    }
                    // 入力必須チェック
                    // 相関チェックNo.81
                    if (!StringUtil.isEmpty(shopTabBean.getGenmtmkidankaisuryo2())
                            && StringUtil.isEmpty(shopTabBean.getGenmtmkidankaipointsu2())) {
                        raiseValidationError("shopTabBean.genmtmkidankaipointsu2", MessageID.W247, "数量閾値2（現）","発生2（現）");
                    }
                    // 相関チェックNo.82
                    if (StringUtil.isEmpty(shopTabBean.getGenmtmkidankaisuryo2())
                            && !StringUtil.isEmpty(shopTabBean.getGenmtmkidankaipointsu2())) {
                        raiseValidationError("shopTabBean.genmtmkidankaisuryo2", MessageID.W247, "発生2（現）","数量閾値2（現）");
                    }
                    // 相関チェックNo.83
                    if (!StringUtil.isEmpty(shopTabBean.getGenmtmkidankaisuryo3())
                            && StringUtil.isEmpty(shopTabBean.getGenmtmkidankaipointsu3())) {
                        raiseValidationError("shopTabBean.genmtmkidankaipointsu3", MessageID.W247, "数量閾値3（現）","発生3（現）");
                    }
                    // 相関チェックNo.84
                    if (StringUtil.isEmpty(shopTabBean.getGenmtmkidankaisuryo3())
                            && !StringUtil.isEmpty(shopTabBean.getGenmtmkidankaipointsu3())) {
                        raiseValidationError("shopTabBean.genmtmkidankaisuryo3", MessageID.W247, "発生3（現）","数量閾値3（現）");
                    }
                    // 相関チェックNo.85
                    if (!StringUtil.isEmpty(shopTabBean.getGenmtmkidankaisuryo4())
                            && StringUtil.isEmpty(shopTabBean.getGenmtmkidankaipointsu4())) {
                        raiseValidationError("shopTabBean.genmtmkidankaipointsu4", MessageID.W247, "数量閾値4（現）","発生4（現）");
                    }
                    // 相関チェックNo.86
                    if (StringUtil.isEmpty(shopTabBean.getGenmtmkidankaisuryo4())
                            && !StringUtil.isEmpty(shopTabBean.getGenmtmkidankaipointsu4())) {
                        raiseValidationError("shopTabBean.genmtmkidankaisuryo4", MessageID.W247, "発生4（現）","数量閾値4（現）");
                    }
                    // 相関チェックNo.87
                    if (!StringUtil.isEmpty(shopTabBean.getGenmtmkidankaisuryo5())
                            && StringUtil.isEmpty(shopTabBean.getGenmtmkidankaipointsu5())) {
                        raiseValidationError("shopTabBean.genmtmkidankaipointsu5", MessageID.W247, "数量閾値5（現）","発生5（現）");
                    }
                    // 相関チェックNo.88
                    if (StringUtil.isEmpty(shopTabBean.getGenmtmkidankaisuryo5())
                            && !StringUtil.isEmpty(shopTabBean.getGenmtmkidankaipointsu5())) {
                        raiseValidationError("shopTabBean.genmtmkidankaisuryo5", MessageID.W247, "発生5（現）","数量閾値5（現）");
                    }
                    // 閾値チェック
                    // 相関チェックNo.89
                    if (!StringUtil.isEmpty(shopTabBean.getGenmtmkidankaisuryo1()) 
                            && !StringUtil.isEmpty(shopTabBean.getGenmtmkidankaisuryo2())) {
                        int genmtmkidankaisuryo1 = Integer.parseInt(ItemUtil.removeComma(shopTabBean.getGenmtmkidankaisuryo1()));
                        int genmtmkidankaisuryo2 = Integer.parseInt(ItemUtil.removeComma(shopTabBean.getGenmtmkidankaisuryo2()));
                        if (genmtmkidankaisuryo1 >= genmtmkidankaisuryo2) {
                            raiseValidationError("shopTabBean.genmtmkidankaisuryo2", MessageID.W278, "2","1");
                        }
                    }
                    // 相関チェックNo.90
                    if (!StringUtil.isEmpty(shopTabBean.getGenmtmkidankaisuryo2()) 
                            && !StringUtil.isEmpty(shopTabBean.getGenmtmkidankaisuryo3())) {
                        int genmtmkidankaisuryo2 = Integer.parseInt(ItemUtil.removeComma(shopTabBean.getGenmtmkidankaisuryo2()));
                        int genmtmkidankaisuryo3 = Integer.parseInt(ItemUtil.removeComma(shopTabBean.getGenmtmkidankaisuryo3()));
                        if (genmtmkidankaisuryo2 >= genmtmkidankaisuryo3) {
                            raiseValidationError("shopTabBean.genmtmkidankaisuryo3", MessageID.W278, "3","2");
                        }
                    }
                    // 相関チェックNo.91
                    if (!StringUtil.isEmpty(shopTabBean.getGenmtmkidankaisuryo3()) 
                            && !StringUtil.isEmpty(shopTabBean.getGenmtmkidankaisuryo4())) {
                        int genmtmkidankaisuryo3 = Integer.parseInt(ItemUtil.removeComma(shopTabBean.getGenmtmkidankaisuryo3()));
                        int genmtmkidankaisuryo4 = Integer.parseInt(ItemUtil.removeComma(shopTabBean.getGenmtmkidankaisuryo4()));
                        if (genmtmkidankaisuryo3 >= genmtmkidankaisuryo4) {
                            raiseValidationError("shopTabBean.genmtmkidankaisuryo4", MessageID.W278, "4","3");
                        }
                    }
                    // 相関チェックNo.92
                    if (!StringUtil.isEmpty(shopTabBean.getGenmtmkidankaisuryo4()) 
                            && !StringUtil.isEmpty(shopTabBean.getGenmtmkidankaisuryo5())) {
                        int genmtmkidankaisuryo4 = Integer.parseInt(ItemUtil.removeComma(shopTabBean.getGenmtmkidankaisuryo4()));
                        int genmtmkidankaisuryo5 = Integer.parseInt(ItemUtil.removeComma(shopTabBean.getGenmtmkidankaisuryo5()));
                        if (genmtmkidankaisuryo4 >= genmtmkidankaisuryo5) {
                            raiseValidationError("shopTabBean.genmtmkidankaisuryo5", MessageID.W278, "5","4");
                        }
                    }
                }
            }
        }

        // 販促設定（販促設定が「対象」の場合のみ）
        if (CommonConstants.FLG_ON.equals(shopTabBean.getHansokusettei())) {
            // 相関チェックNo25
            if (StringUtil.isEmpty(shopTabBean.getTekiyodkikanstr())) {
                raiseValidationError("shopTabBean.tekiyodkikanstr", MessageID.W117, "販促設定",
                    "適用期間（開始）");
            }
            // 相関チェックNo26
            if (StringUtil.isEmpty(shopTabBean.getTekiyodkikanend())) {
                raiseValidationError("shopTabBean.tekiyodkikanend", MessageID.W117, "販促設定",
                    "適用期間（終了）");
            }
            // 相関チェックNo27
            if (StringUtil.isEmpty(shopTabBean.getHansokunm())) {
                raiseValidationError("shopTabBean.hansokunm", MessageID.W117, "販促設定", "名称");
            }
            // 相関チェックNo28
            if (StringUtil.isEmpty(shopTabBean.getDlvryprice())) {
                raiseValidationError("shopTabBean.dlvryprice", MessageID.W117, "販促設定", "配達価格");
            }
            // 相関チェックNo29
            if (StringUtil.isEmpty(shopTabBean.getOmchkrprice())) {
                raiseValidationError("shopTabBean.omchkrprice", MessageID.W117, "販促設定", "持ち帰り価格");
            }
            // 相関チェックNo44
            if (!DateTimeUtil.isLater(shopTabBean.getTekiyodkikanstr(),
                shopTabBean.getTekiyodkikanend())) {
                List<FormField> fields = List.of(new FormField("shopTabBean.tekiyodkikanstr"),
                    new FormField("shopTabBean.tekiyodkikanend"));
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W093,
                    "販促設定_適用期間（開始）", "販促設定_適用期間（終了）");
            }
        }
    }

    /**
     * 単価に対するバリデーションを実施する
     * 
     * @param unt 単価
     * @param fieldName フィールド名
     * @param label ラベル
     */
    private void validateUnt(String unt, String fieldName, String label) {
        // 未入力の時は何もしない
        if (StringUtil.isEmpty(unt)) {
            return;
        }
        // 数字／マイナス記号／小数点チェック
        if (!ItemUtil.removeComma(unt)
            .matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS_DECIMAL)) {
            raiseValidationError(fieldName, MessageID.W013, label, "数字とマイナス記号と小数点");
        }
        // 整数部桁数チェック
        if (getIntegerPartLength(unt) > 8) {
            raiseValidationError(fieldName, MessageID.W018, label, "8");
        }
        // 小数部桁数チェック
        if (getDecimalPartLength(unt) > 2) {
            raiseValidationError(fieldName, MessageID.W010, label, "2");
        }
    }

    /**
     * ポイント発生：ステージ設定×倍率に対するバリデーションを実施する
     * 
     * @param point ポイント
     * @param fieldName フィールド名
     * @param label ラベル
     */
    private void validateKangenPoint(String point, String fieldName, String label) {
        // 未入力の時は何もしない
        if (StringUtil.isEmpty(point)) {
            return;
        }
        // 数字／小数点チェック
        if (!ItemUtil.removeComma(point)
            .matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS_DECIMALPOINT)) {
            raiseValidationError(fieldName, MessageID.W013, label, "数字と小数点");
        }
        // 整数部桁数チェック
        if (getIntegerPartLength(point) > 5) {
            raiseValidationError(fieldName, MessageID.W018, label, "5");
        }
        // 小数部桁数チェック
        if (getDecimalPartLength(point) > 2) {
            raiseValidationError(fieldName, MessageID.W010, label, "2");
        }
    }

    /**
     * ポイント固定に対するバリデーションを実施する
     * 
     * @param point ポイント
     * @param fieldName フィールド名
     * @param label ラベル
     */
    private void validateKoteiPoint(String point, String fieldName, String label) {
        // 未入力の時は何もしない
        if (StringUtil.isEmpty(point)) {
            return;
        }
        // 数字チェック
        if (!ItemUtil.removeComma(point)
            .matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS)) {
            raiseValidationError(fieldName, MessageID.W013, label, "数字のみ");
        }
        // 整数部桁数チェック
        if (getIntegerPartLength(point) > 5) {
            raiseValidationError(fieldName, MessageID.W018, label, "5");
        }
    }

    /**
     * ポイント発生-数量に対するバリデーションを実施する
     * 
     * @param point ポイント
     * @param fieldName フィールド名
     * @param label ラベル
     */
    private void validatePointSuryou(String point, String fieldName, String label) {
        // 未入力の時は何もしない
        if (StringUtil.isEmpty(point)) {
            return;
        }
        // 数字チェック
        if (!ItemUtil.removeComma(point)
            .matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS)) {
            raiseValidationError(fieldName, MessageID.W013, label, "数字のみ");
        }
        // 整数部桁数チェック
        if (getIntegerPartLength(point) > 5) {
            raiseValidationError(fieldName, MessageID.W018, label, "5");
        }
    }

    /**
     * ポイント発生-発生ポイントに対するバリデーションを実施する
     * 
     * @param point ポイント
     * @param fieldName フィールド名
     * @param label ラベル
     */
    private void validatePointHassei(String point, String fieldName, String label) {
        // 未入力の時は何もしない
        if (StringUtil.isEmpty(point)) {
            return;
        }
        // 数字チェック
        if (!ItemUtil.removeComma(point)
            .matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS)) {
            raiseValidationError(fieldName, MessageID.W013, label, "数字のみ");
        }
        // 整数部桁数チェック
        if (getIntegerPartLength(point) > 6) {
            raiseValidationError(fieldName, MessageID.W018, label, "6");
        }
    }

    /**
     * 明細行の単価に対するバリデーションを実施する
     * 
     * @param unt 単価
     * @param itemName 項目名（Bean名や添字は含めない）
     * @param label ラベル
     * @param index 行番号
     */
    private void validateUnt(String unt, String itemName, String label, int index) {
        // 未入力の時は何もしない
        if (StringUtil.isEmpty(unt)) {
            return;
        }
        // フィールド名を編集
        final String fieldName = getDetailFieldName(itemName, index);
        // 桁数チェック
        if (unt.length() > 10) {
            raiseValidationError(fieldName, MessageID.W035, index + 1, label, "10");
        }
        // 数字／マイナス記号／小数点チェック
        if (!ItemUtil.removeComma(unt)
            .matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS_DECIMAL)) {
            raiseValidationError(fieldName, MessageID.W039, index + 1, label, "数字とマイナス記号と小数点");
        }
        // 整数部桁数チェック
        if (getIntegerPartLength(unt) > 8) {
            raiseValidationError(fieldName, MessageID.W044, index + 1, label, "8");
        }
        // 小数部桁数チェック
        if (getDecimalPartLength(unt) > 2) {
            raiseValidationError(fieldName, MessageID.W036, index + 1, label, "2");
        }
    }

    /**
     * 金額に対するバリデーションを行う
     * 
     * @param price 金額
     * @param itemName 項目名
     * @param label ラベル
     * @param index 行番号
     */
    private void validatePrice(String price, String itemName, String label, int index) {
        // 未入力の時は何もしない
        if (StringUtil.isEmpty(price)) {
            return;
        }
        // フィールド名を編集
        final String fieldName = getDetailFieldName(itemName, index);
        // 整数部桁数チェック
        if (getIntegerPartLength(price) > 10) {
            raiseValidationError(fieldName, MessageID.W044, index + 1, label, "10");
        }
        // 小数部桁数チェック
        if (getDecimalPartLength(price) > 2) {
            raiseValidationError(fieldName, MessageID.W036, index + 1, label, "2");
        }
    }

    /**
     * 数量に対するバリデーションを行う
     * 
     * @param suryo 数量
     * @param itemName 項目名
     * @param label ラベル
     * @param index 行番号
     */
    private void validateSuryo(String suryo, String itemName, String label, int index) {
        // 未入力の時は何もしない
        if (StringUtil.isEmpty(suryo)) {
            return;
        }
        // フィールド名を編集
        final String fieldName = getDetailFieldName(itemName, index);
        // 数字／マイナス記号／小数点チェック
        if (!ItemUtil.removeComma(suryo)
            .matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS_DECIMAL)) {
            raiseValidationError(fieldName, MessageID.W039, index + 1, label, "数字とマイナス記号と小数点");
        }
        // 整数部桁数チェック
        if (getIntegerPartLength(suryo) > 9) {
            raiseValidationError(fieldName, MessageID.W035, index + 1, label, "9");
        }
        // 小数部桁数チェック
        if (getDecimalPartLength(suryo) > 1) {
            raiseValidationError(fieldName, MessageID.W036, index + 1, label, "1");
        }
        // 正数チェック
        if (getDecimalValue(suryo).signum() <= 0) {
            raiseValidationError(fieldName, MessageID.W048, index + 1, label, "0");
        }
    }

    /**
     * 切替日に入力があり変更されている場合に運用日より前かどうかを返す
     * 
     * @param value 切替日
     * @param initial 初期値
     * @param operationDate 運用日
     * @return 変更ありで運用日より前の日付であればtrue
     */
    private boolean isLaterIfModified(String value, String initial, String operationDate) {
        // 入力があり、初期値から変更されている場合、運用日より前ならばtrue
        return !StringUtil.isEmpty(value) && !value.equals(initial)
            && !DateTimeUtil.isLater(operationDate, DateTimeUtil.toNoSlashedDate(value));
    }

    /**
     * 明細行のフィールド名を取得する
     * 
     * @param itemName 項目名
     * @param index 行番号
     * @return フィールド名
     */
    private String getDetailFieldName(String itemName, int index) {
        return String.format("meisaiBeanList[%d].%s", index, itemName);
    }

    /**
     * バリデーションエラーを発生させる
     * 
     * @param fieldName フィールド名
     * @param messageId メッセージID
     * @param args メッセージ可変要素
     */
    private void raiseValidationError(String fieldName, MessageID messageId, Object... args) {
        List<FormField> fields = List.of(new FormField(fieldName));
        throw BusinessExceptionFactory.validationFailed(fields, messageId, args);
    }

    /**
     * 文字列をBigDecimalで取得する
     * 
     * @param value 文字列
     * @return BigDecimal（数値として無効な場合はBigDecimal.ZERO）
     */
    private BigDecimal getDecimalValue(String value) {
        String noCommaValue = ItemUtil.removeComma(value);
        if (!noCommaValue.matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS_DECIMAL)) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(noCommaValue);
    }

    /**
     * 整数部の桁数を取得
     * 
     * @param doubleAsString
     * @return
     */
    private int getIntegerPartLength(String doubleAsString) {
        doubleAsString = ItemUtil.removeComma(doubleAsString);
        int indexOfDecimal = doubleAsString.indexOf(".");
        if (indexOfDecimal == -1) {
            return doubleAsString.length();
        }
        return doubleAsString.substring(0, indexOfDecimal).length();
    }

    /**
     * 小数部の桁数を取得する
     * 
     * @param doubleAsString
     * @return
     */
    private int getDecimalPartLength(String doubleAsString) {
        doubleAsString = ItemUtil.removeComma(doubleAsString);
        int indexOfDecimal = doubleAsString.indexOf(".");
        if (indexOfDecimal == -1) {
            return 0;
        }
        return doubleAsString.substring(indexOfDecimal + 1).length();
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

    /**
     * インデックス付きのConsumerを取得する.
     * 
     * @param <T> consumerで処理するデータの型
     * @param start 添字の開始番号
     * @param consumer Indexとともに行う処理
     * @return インデックス利用可能なConsumer
     */
    private <T> Consumer<T> withIndexConsumer(int start, ObjIntConsumer<T> consumer) {
        int counter[] = {start};
        return obj -> consumer.accept(obj, counter[0]++);
    }

    /**
     * インデックス付きのFunctionを取得する.
     * 
     * @param <T> Functionの引数の型
     * @param <R> Functionの戻り値の型
     * @param start 添字の開始番号
     * @param function Indexと共に行う変換処理
     * @return インデックス利用可能なFunction
     */
    private <T, R> Function<T, R> withIndexFunction(int start, BiFunction<T, Integer, R> function) {
        int counter[] = {start};
        return obj -> function.apply(obj, counter[0]++);
    }
}
