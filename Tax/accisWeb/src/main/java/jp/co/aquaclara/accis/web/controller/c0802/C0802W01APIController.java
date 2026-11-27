package jp.co.aquaclara.accis.web.controller.c0802;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.constants.ValidationConstants;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.controller.c0802.bean.C0802W01BaseBean;
import jp.co.aquaclara.accis.web.controller.c0802.bean.C0802W01MeisaiDetailBean;
import jp.co.aquaclara.accis.web.controller.c0802.form.C0802W01Form;
import jp.co.aquaclara.accis.web.controller.c0802.session.C0802W00Session;
import jp.co.aquaclara.accis.web.controller.commons.AccisWebBaseController;
import jp.co.aquaclara.accis.web.controller.commons.AciRestResponse;
import jp.co.aquaclara.accis.web.controller.commons.DbManagementSession;
import jp.co.aquaclara.accis.web.controller.commons.TaxInfo;
import jp.co.aquaclara.accis.web.controller.commons.TaxSession;
import jp.co.aquaclara.accis.web.controller.commons.UserSessionForm;
import jp.co.aquaclara.accis.web.controller.parts.master.bean.CommonPartsListBean;
import jp.co.aquaclara.accis.web.domain.dto.c0802.C0802W01RegistrationConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.c0802.C0802W01RegistrationMeisaiDto;
import jp.co.aquaclara.accis.web.domain.dto.c0802.C0802W01SearchConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.c0802.C0802W01SearchResultDto;
import jp.co.aquaclara.accis.web.domain.service.c0800.UtilityService;
import jp.co.aquaclara.accis.web.domain.service.c0802.C0802W01Service;
import jp.co.aquaclara.accis.web.domain.service.z0501.Z0501W01Service;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import jp.co.aquaclara.accis.web.exception.dto.FormField;
import lombok.extern.slf4j.Slf4j;

/**
 * C0802W01_受注明細予約登録コントローラクラス.
 */
@RestController
@SessionAttributes({"dbManagementSession_C0802W01"})
@RequestMapping("/api/c0802w01")
@Slf4j
public class C0802W01APIController extends AccisWebBaseController {

    /** 受注予約登録サービス */
    @Autowired
    private C0802W01Service service;

    /** 得意先共通ヘッダサービス */
    @Autowired
    private Z0501W01Service z0501w01Service;

    /** ユーザセッション情報 */
    @Autowired
    private UserSessionForm userSessionForm;

    /** 明細データセッション情報 */
    @Autowired
    private C0802W00Session c0802w00session;

    /** 消費税セッション情報 */
    @Autowired
    private TaxSession taxSession;

    /** ユーティリティ */
    @Autowired
    private UtilityService util;

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage,
        MethodParameter parameter, Type targetType,
        Class<? extends HttpMessageConverter<?>> converterType) {

        // インスタンスがC0802W01Formでない場合、処理を抜ける
        if (!(body instanceof C0802W01Form)) {
            return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
        }

        return body;
    }

    /**
     * 初期表示処理（ヘッダー部）
     * 
     * @param form c0802W01Form
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param kttcd 得意先コード
     * @param acptdnpyno 得意先コード
     * @param edano 得意先コード
     * @param kinoid 機能ID
     * @return C0802W01BaseBean
     */
    @GetMapping("/search/{apkihoncd}/{shopcd}/{kttcd}/{acptdnpyno}/{edano}/{kinoid}")
    public C0802W01BaseBean search(@ModelAttribute("c0802W01Form") C0802W01Form form,
        @PathVariable("apkihoncd") String apkihoncd, @PathVariable("shopcd") String shopcd,
        @PathVariable("kttcd") String kttcd, @PathVariable("acptdnpyno") String acptdnpyno,
        @PathVariable("edano") String edano, @PathVariable("kinoid") String kinoid,
        @SessionAttribute("dbManagementSession_C0802W01") DbManagementSession dbSession) {

        log.debug("DB管理セッションの運用日付 : {}", dbSession.getUnyoDate());

        // 得意先共通ヘッダ相関チェック
        z0501w01Service.checkForC0802W01(apkihoncd, shopcd, kttcd);

        // 検索条件パラメータ作成
        C0802W01SearchConditionDto conditionDto =
            createCondition(apkihoncd, shopcd, kttcd, acptdnpyno, edano, kinoid, dbSession);

        // 検索処理
        C0802W01SearchResultDto resultDto = service.getInitDisplay(conditionDto);

        C0802W01BaseBean baseBean = setBaseBean(resultDto, dbSession);

        return baseBean;
    }

    /**
     * 初期表示処理（明細部）
     * 
     * @param form c0802W01Form
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param response Httpレスポンス
     * @return C0802W01MeisaiDetailBean
     */
    @SuppressWarnings("unchecked")
    @GetMapping("/meisailist/{apkihoncd}/{shopcd}")
    public List<C0802W01MeisaiDetailBean> meisailist(
        @ModelAttribute("c0802W01Form") C0802W01Form form,
        @PathVariable("apkihoncd") String apkihoncd, @PathVariable("shopcd") String shopcd,
        @SessionAttribute("dbManagementSession_C0802W01") DbManagementSession dbSession,
        HttpServletResponse response) {

        log.debug("DB管理セッションの運用日付 : {}", dbSession.getUnyoDate());

        // 検索条件パラメータ作成
        C0802W01SearchConditionDto conditionDto = createCondition(apkihoncd, shopcd,
            form.getBaseBean().getTokuisakicd(), form.getBaseBean().getAcptdnpyno(),
            form.getBaseBean().getEdano(), form.getBaseBean().getKinoid(), dbSession);

        // 検索処理
        C0802W01SearchResultDto resultDto = service.getInitDisplay(conditionDto);

        List<C0802W01MeisaiDetailBean> meisaiDetailBeanList = setMeisaiDetailBean(resultDto, form);
        c0802w00session.setDetailBeanList(meisaiDetailBeanList);

        // 最大表示件数オーバーの場合、ステータスコードを206とする
        meisaiDetailBeanList =
            (List<C0802W01MeisaiDetailBean>) chkLength(meisaiDetailBeanList, response);

        return meisaiDetailBeanList;
    }

    /**
     * お気に入り表示
     * 
     * @param form C0802W01Form
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @return List<C0802W01MeisaiDetailBean>
     */
    @GetMapping("/okinilist/{apkihoncd}/{shopcd}")
    public List<C0802W01MeisaiDetailBean> okinilist(
        @ModelAttribute("c0802W01Form") C0802W01Form form,
        @PathVariable("apkihoncd") String apkihoncd, @PathVariable("shopcd") String shopcd,
        @SessionAttribute("dbManagementSession_C0802W01") DbManagementSession dbSession) {

        // 検索条件パラメータ作成
        C0802W01SearchConditionDto conditionDto =
            createOkinilistCondition(apkihoncd, shopcd, form, dbSession);

        // 検索処理
        C0802W01SearchResultDto resultDto = service.getOkiniList(conditionDto);

        List<C0802W01MeisaiDetailBean> meisaiDetailBeanList = new ArrayList<>();

        for (C0802W01MeisaiDetailBean entity : c0802w00session.getDetailBeanList()) {

            boolean acptsuryoFlg = false;
            if ((!Strings.isEmpty(entity.getAcptsuryo()) && !"0".equals(entity.getAcptsuryo()))) {
                acptsuryoFlg = true;
            }
            boolean soukaisumeiFlg = false;
            if ((!Strings.isEmpty(entity.getSoukaisumei())
                && !"0".equals(entity.getSoukaisumei()))) {
                soukaisumeiFlg = true;
            }
            boolean zankaisumeiFlg = false;
            if ((!Strings.isEmpty(entity.getZankaisumei())
                && !"0".equals(entity.getZankaisumei()))) {
                zankaisumeiFlg = true;
            }

            if (acptsuryoFlg || soukaisumeiFlg || zankaisumeiFlg) {
                meisaiDetailBeanList.add(entity);
            }

        }

        if (!Objects.isNull(resultDto.getSearchResultMeisaiDtoList())) {
            meisaiDetailBeanList.addAll(setMeisaiDetailBean(resultDto, form));
        }

        c0802w00session.setDetailBeanList(meisaiDetailBeanList);
        return meisaiDetailBeanList;
    }

    /**
     * 行挿入
     * 
     * @param form C0802W01Form
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @return List<C0802W01MeisaiDetailBean>
     */
    @GetMapping("/gyosonyu/{apkihoncd}/{shopcd}")
    public List<C0802W01MeisaiDetailBean> gyosonyu(
        @ModelAttribute("c0802W01Form") C0802W01Form form,
        @PathVariable("apkihoncd") String apkihoncd, @PathVariable("shopcd") String shopcd,
        @SessionAttribute("dbManagementSession_C0802W01") DbManagementSession dbSession) {

        // 検索条件パラメータ作成
        C0802W01SearchConditionDto conditionDto =
            createGyosonyuCondition(apkihoncd, shopcd, form, dbSession);

        // 検索処理
        C0802W01SearchResultDto resultDto = service.getRenkeiShnList(conditionDto);

        List<C0802W01MeisaiDetailBean> meisaiDetailBeanList = new ArrayList<>();

        int meisaiIndex = Integer.parseInt(form.getBaseBean().getMeisaiindex().replace("'", ""));
        int index = 0;
        if (c0802w00session.getDetailBeanList().size() == 0) {
            if (!Objects.isNull(resultDto.getSearchResultMeisaiDtoList())) {
                List<C0802W01MeisaiDetailBean> beanList = setMeisaiDetailBean(resultDto, form);
                meisaiDetailBeanList.add(beanList.get(0));
            }
        } else {
            for (C0802W01MeisaiDetailBean entity : c0802w00session.getDetailBeanList()) {

                if (index == meisaiIndex) {
                    if (!Objects.isNull(resultDto.getSearchResultMeisaiDtoList())) {
                        List<C0802W01MeisaiDetailBean> beanList =
                            setMeisaiDetailBean(resultDto, form);
                        meisaiDetailBeanList.add(beanList.get(0));
                    }
                }

                boolean acptsuryoFlg = false;
                if ((!Strings.isEmpty(entity.getAcptsuryo())
                    && !"0".equals(entity.getAcptsuryo()))) {
                    acptsuryoFlg = true;
                }
                boolean soukaisumeiFlg = false;
                if ((!Strings.isEmpty(entity.getSoukaisumei())
                    && !"0".equals(entity.getSoukaisumei()))) {
                    soukaisumeiFlg = true;
                }
                boolean zankaisumeiFlg = false;
                if ((!Strings.isEmpty(entity.getZankaisumei())
                    && !"0".equals(entity.getZankaisumei()))) {
                    zankaisumeiFlg = true;
                }

                if (acptsuryoFlg || soukaisumeiFlg || zankaisumeiFlg) {
                    meisaiDetailBeanList.add(entity);
                }

                index++;
            }
            if (meisaiIndex == -1) {
                if (!Objects.isNull(resultDto.getSearchResultMeisaiDtoList())) {
                    List<C0802W01MeisaiDetailBean> beanList = setMeisaiDetailBean(resultDto, form);
                    meisaiDetailBeanList.add(beanList.get(0));
                }
            }
        }

        c0802w00session.setDetailBeanList(meisaiDetailBeanList);
        return meisaiDetailBeanList;
    }

    /**
     * 行複写
     * 
     * @param form C0802W01Form
     * @return List<C0802W01MeisaiDetailBean>
     */
    @GetMapping("/gyofukusha")
    public List<C0802W01MeisaiDetailBean> gyofukusha(
        @ModelAttribute("c0802W01Form") C0802W01Form form) {

        List<C0802W01MeisaiDetailBean> meisaiDetailBeanList = new ArrayList<>();

        int meisaiIndex = Integer.parseInt(form.getBaseBean().getMeisaiindex().replace("'", ""));
        int index = 0;

        for (C0802W01MeisaiDetailBean entity : c0802w00session.getDetailBeanList()) {

            boolean acptsuryoFlg = false;
            if ((!Strings.isEmpty(entity.getAcptsuryo()) && !"0".equals(entity.getAcptsuryo()))) {
                acptsuryoFlg = true;
            }
            boolean soukaisumeiFlg = false;
            if ((!Strings.isEmpty(entity.getSoukaisumei())
                && !"0".equals(entity.getSoukaisumei()))) {
                soukaisumeiFlg = true;
            }
            boolean zankaisumeiFlg = false;
            if ((!Strings.isEmpty(entity.getZankaisumei())
                && !"0".equals(entity.getZankaisumei()))) {
                zankaisumeiFlg = true;
            }

            if (acptsuryoFlg || soukaisumeiFlg || zankaisumeiFlg) {
                meisaiDetailBeanList.add(entity);
            }
            if (index == meisaiIndex) {
                if ("1".equals(entity.getUpdateflg()) || (!Strings.isEmpty(entity.getAcptsuryo())
                    && !"0".equals(entity.getAcptsuryo()))) {
                    C0802W01MeisaiDetailBean bean = new C0802W01MeisaiDetailBean();
                    bean.setShncd(entity.getShncd()); // 商品コード
                    bean.setShnnmryakusho(entity.getShnnmryakusho()); // 商品名称略称
                    bean.setIro(entity.getIro()); // 色
                    bean.setSz(entity.getSz()); // サイズ
                    bean.setKatasiki(entity.getKatasiki()); // 型式
                    bean.setKeijokbn(entity.getKeijokbn()); // 計上区分
                    bean.setAcptsuryo(entity.getAcptsuryo()); // 受注数量
                    bean.setUritrtskitani(entity.getUritrtskitani()); // 受注数量単位
                    bean.setUriirisu(entity.getUriirisu()); // 売上入数
                    bean.setUritani(entity.getUritani()); // 売上入数単位
                    bean.setSokazu(entity.getSokazu()); // 総数
                    bean.setHyojununt(entity.getHyojununt()); // 標準単価
                    bean.setAcptuntcntlkbn(entity.getAcptuntcntlkbn()); // 受注単価活性非活性制御区分
                    bean.setAcptunt(entity.getAcptunt()); // 受注単価
                    bean.setKin(entity.getKin()); // 金額
                    bean.setSoukaisumei(entity.getSoukaisumei()); // 総回数
                    bean.setZankaisumei(entity.getZankaisumei()); // 残回数
                    bean.setMeisaibiko(entity.getMeisaibiko()); // 明細備考
                    bean.setGyono(entity.getGyono()); // 行番号（明細）
                    bean.setKshntrgtflg(entity.getKshntrgtflg()); // 更新対象フラグ
                    bean.setLctgrycd(entity.getLctgrycd()); // 大分類コード
                    bean.setMctgrycd(entity.getMctgrycd()); // 中分類コード
                    bean.setSctgrycd(entity.getSctgrycd()); // 小分類コード
                    bean.setAcptdnpymeisaino(entity.getAcptdnpymeisaino()); // 受注伝票明細番号
                    bean.setAcptdnpymeisaiedano(entity.getAcptdnpymeisaiedano()); // 受注伝票明細枝番号
                    bean.setDatsksmotokbn(entity.getDatsksmotokbn()); // データ作成元区分
                    bean.setPricekbn(entity.getPricekbn()); // 価格区分
                    bean.setHyojunpricekirikaedd(entity.getHyojunpricekirikaedd()); // 標準価格切替日
                    bean.setGenzhyojunprice(entity.getGenzhyojunprice()); // 現標準価格
                    bean.setGenzhyojunmochikaeriprice(entity.getGenzhyojunmochikaeriprice()); // 現標準持ち帰り価格
                    bean.setShinhyojunprice(entity.getShinhyojunprice()); // 新標準価格
                    bean.setShinhyojunmochikaeriprice(entity.getShinhyojunmochikaeriprice()); // 新標準持ち帰り価格
                    bean.setShoppricekirikaedd(entity.getShoppricekirikaedd()); // ショップ価格切替日
                    bean.setGenzshopprice(entity.getGenzshopprice()); // 現ショップ価格
                    bean.setGenzshopmochikaeriprice(entity.getGenzshopmochikaeriprice()); // 現ショップ持ち帰り価格
                    bean.setShinshopprice(entity.getShinshopprice()); // 新ショップ価格
                    bean.setShinshopmochikaeriprice(entity.getShinshopmochikaeriprice()); // 新ショップ持ち帰り価格
                    bean.setHansokustrtdd(entity.getHansokustrtdd()); // 販促開始日
                    bean.setHansokushrydd(entity.getHansokushrydd()); // 販促終了日
                    bean.setHansokuprice(entity.getHansokuprice()); // 販促価格
                    bean.setHansokumochikaeriprice(entity.getHansokumochikaeriprice()); // 販促持ち帰り価格
                    bean.setTtpricestrtdd(entity.getTtpricestrtdd()); // 得意先価格開始日
                    bean.setTtpriceshrydd(entity.getTtpriceshrydd()); // 得意先価格終了日
                    bean.setTtprice(entity.getTtprice()); // 得意先価格
                    bean.setTtmochikaeriprice(entity.getTtmochikaeriprice()); // 得意先持ち帰り価格
                    bean.setAcptuntshohitaxkin(entity.getAcptuntshohitaxkin()); // 受注単価消費税金額
                    bean.setAcptshohitaxkin(entity.getAcptshohitaxkin()); // 受注消費税金額
                    bean.setShokidsplyprice(entity.getShokidsplyprice()); // 初期表示価格
                    bean.setZaikount(entity.getZaikount()); // 在庫単価
                    bean.setShntaxkbn(entity.getShntaxkbn()); // 商品税区分
                    bean.setTanpinmeisaiflg(entity.getTanpinmeisaiflg()); // 単品明細フラグ
                    bean.setUpdateflg("0"); // 更新処理フラグ
                    bean.setTaxkbn(entity.getTaxkbn()); // 税区分
                    bean.setYusyokbn(entity.getYusyokbn()); // 有償区分
                    bean.setMeisaikbn(entity.getMeisaikbn()); // 明細区分
                    bean.setNyuryokumeisaiflg(entity.getNyuryokumeisaiflg()); // 入力明細フラグ
                    bean.setStshncd(entity.getStshncd()); // セット商品コード
                    bean.setTokuisakikbn(form.getBaseBean().getTokuisakikbn()); // 得意先区分
                    meisaiDetailBeanList.add(bean);
                }
            }
            index++;
        }

        c0802w00session.setDetailBeanList(meisaiDetailBeanList);
        return meisaiDetailBeanList;

    }

    /**
     * 明細行変更処理
     * 
     * @param meisaiBean
     * @param index 変更する明細のインデックス
     * @return
     */
    @PutMapping("/changerow/{index}")
    public AciRestResponse<Void> changeRow(@RequestBody C0802W01MeisaiDetailBean meisaiBean,
        @PathVariable("index") int index) {

        // セッションで保持する明細行の入力項目を変更する
        List<C0802W01MeisaiDetailBean> sessionList = c0802w00session.getDetailBeanList();
        C0802W01MeisaiDetailBean target = sessionList.get(index);
        target.setShncd(meisaiBean.getShncd()); // 商品コード
        target.setShnnmryakusho(meisaiBean.getShnnmryakusho()); // 商品名称略称
        target.setIro(meisaiBean.getIro()); // 色
        target.setSz(meisaiBean.getSz()); // サイズ
        target.setKatasiki(meisaiBean.getKatasiki()); // 型式
        target.setKeijokbn(meisaiBean.getKeijokbn()); // 計上区分
        target.setAcptsuryo(ItemUtil.removeComma(meisaiBean.getAcptsuryo())); // 受注数量
        target.setUritrtskitani(meisaiBean.getUritrtskitani()); // 受注数量単位
        target.setUriirisu(meisaiBean.getUriirisu()); // 売上入数
        target.setUritani(meisaiBean.getUritani()); // 売上入数単位
        target.setSokazu(ItemUtil.removeComma(meisaiBean.getSokazu())); // 総数
        target.setHyojununt(ItemUtil.removeComma(meisaiBean.getHyojununt())); // 標準単価
        target.setAcptunt(ItemUtil.removeComma(meisaiBean.getAcptunt())); // 受注単価
        target.setKin(ItemUtil.removeComma(meisaiBean.getKin())); // 金額
        target.setSoukaisumei(meisaiBean.getSoukaisumei()); // 総回数
        target.setZankaisumei(meisaiBean.getZankaisumei()); // 残回数
        target.setMeisaibiko(meisaiBean.getMeisaibiko()); // 明細備考
        target.setGyono(meisaiBean.getGyono()); // 行番号（明細）
        target.setKshntrgtflg(meisaiBean.getKshntrgtflg()); // 更新対象フラグ
        target.setLctgrycd(meisaiBean.getLctgrycd()); // 大分類コード
        target.setMctgrycd(meisaiBean.getMctgrycd()); // 中分類コード
        target.setSctgrycd(meisaiBean.getSctgrycd()); // 小分類コード
        target.setAcptdnpymeisaino(meisaiBean.getAcptdnpymeisaino()); // 受注伝票明細番号
        target.setAcptdnpymeisaiedano(meisaiBean.getAcptdnpymeisaiedano()); // 受注伝票明細枝番号
        target.setDatsksmotokbn(meisaiBean.getDatsksmotokbn()); // データ作成元区分
        target.setPricekbn(meisaiBean.getPricekbn()); // 価格区分
        target.setHyojunpricekirikaedd(meisaiBean.getHyojunpricekirikaedd()); // 標準価格切替日
        target.setGenzhyojunprice(meisaiBean.getGenzhyojunprice()); // 現標準価格
        target.setGenzhyojunmochikaeriprice(meisaiBean.getGenzhyojunmochikaeriprice()); // 現標準持ち帰り価格
        target.setShinhyojunprice(meisaiBean.getShinhyojunprice()); // 新標準価格
        target.setShinhyojunmochikaeriprice(meisaiBean.getShinhyojunmochikaeriprice()); // 新標準持ち帰り価格
        target.setShoppricekirikaedd(meisaiBean.getShoppricekirikaedd()); // ショップ価格切替日
        target.setGenzshopprice(meisaiBean.getGenzshopprice()); // 現ショップ価格
        target.setGenzshopmochikaeriprice(meisaiBean.getGenzshopmochikaeriprice()); // 現ショップ持ち帰り価格
        target.setShinshopprice(meisaiBean.getShinshopprice()); // 新ショップ価格
        target.setShinshopmochikaeriprice(meisaiBean.getShinshopmochikaeriprice()); // 新ショップ持ち帰り価格
        target.setHansokustrtdd(meisaiBean.getHansokustrtdd()); // 販促開始日
        target.setHansokushrydd(meisaiBean.getHansokushrydd()); // 販促終了日
        target.setHansokuprice(meisaiBean.getHansokuprice()); // 販促価格
        target.setHansokumochikaeriprice(meisaiBean.getHansokumochikaeriprice()); // 販促持ち帰り価格
        target.setTtpricestrtdd(meisaiBean.getTtpricestrtdd()); // 得意先価格開始日
        target.setTtpriceshrydd(meisaiBean.getTtpriceshrydd()); // 得意先価格終了日
        target.setTtprice(meisaiBean.getTtprice()); // 得意先価格
        target.setTtmochikaeriprice(meisaiBean.getTtmochikaeriprice()); // 得意先持ち帰り価格
        target.setAcptuntshohitaxkin(meisaiBean.getAcptuntshohitaxkin()); // 受注単価消費税金額
        target.setAcptshohitaxkin(meisaiBean.getAcptshohitaxkin()); // 受注消費税金額
        target.setShokidsplyprice(meisaiBean.getShokidsplyprice()); // 初期表示価格
        target.setZaikount(meisaiBean.getZaikount()); // 在庫単価
        target.setShntaxkbn(meisaiBean.getShntaxkbn()); // 商品税区分
        target.setTanpinmeisaiflg(meisaiBean.getTanpinmeisaiflg()); // 単品明細フラグ
        target.setUpdateflg(meisaiBean.getUpdateflg()); // 更新処理フラグ
        target.setTaxkbn(meisaiBean.getTaxkbn()); // 税区分
        target.setYusyokbn(meisaiBean.getYusyokbn()); // 有償区分
        target.setMeisaikbn(meisaiBean.getMeisaikbn()); // 明細区分
        target.setNyuryokumeisaiflg(meisaiBean.getNyuryokumeisaiflg()); // 入力明細フラグ
        target.setStshncd(meisaiBean.getStshncd()); // セット商品コード

        // 更新結果の返却
        return new AciRestResponse<>(null);
    }

    /**
     * 得意先区分に応じた計上区分のListを取得する。
     * 
     * @param tokuisakikubun
     * @return CommonPartsListBean
     */
    @GetMapping("/keijyokubun/{tokuisakikubun}")
    public List<CommonPartsListBean> getKeijyoKbn(
        @PathVariable("tokuisakikubun") String tokuisakikubun) {
        return util.convertList(service.getKeijyokubunList(tokuisakikubun), dto -> {
            return new CommonPartsListBean(dto.getKey(), dto.getValue());
        });
    }

    /**
     * 検索条件DTOを生成する.
     * 
     * @param apkihoncd
     * @param shopcd
     * @param kttcd
     * @param acptdnpyno
     * @param edano
     * @param kinoid
     * @param dbSession
     * @return C0805W02SearchConditionDto
     */
    private C0802W01SearchConditionDto createCondition(String apkihoncd, String shopcd,
        String kttcd, String acptdnpyno, String edano, String kinoid,
        DbManagementSession dbSession) {
        C0802W01SearchConditionDto dto = new C0802W01SearchConditionDto();
        dto.setApkihoncd(apkihoncd);
        dto.setShopcd(shopcd);
        dto.setTokuisakicd(kttcd);
        dto.setYoyakuflg(true);
        if ("c0806w01".equals(kinoid)) {
            dto.setYoyakuflg(false);
            dto.setAcptdnpyno(acptdnpyno);
            dto.setEdano(edano);
        }
        dto.setUnyodate(dbSession.getUnyoDate());
        return dto;
    }

    /**
     * 検索条件DTOを生成する.
     * 
     * @param apkihoncd
     * @param shopcd
     * @param C0802W01Form
     * @param dbSession
     * @return C0805W02SearchConditionDto
     */
    private C0802W01SearchConditionDto createOkinilistCondition(String apkihoncd, String shopcd,
        C0802W01Form form, DbManagementSession dbSession) {

        C0802W01SearchConditionDto dto = new C0802W01SearchConditionDto();
        dto.setApkihoncd(apkihoncd);
        dto.setShopcd(shopcd);
        dto.setTokuisakicd(form.getBaseBean().getTokuisakicd());
        dto.setDlvrykbn(form.getBaseBean().getDlvrykbn());
        dto.setTokuisakikbn(form.getBaseBean().getTokuisakikbn());
        dto.setUnyodate(dbSession.getUnyoDate());

        return dto;
    }

    /**
     * 検索条件DTOを生成する.
     * 
     * @param apkihoncd
     * @param shopcd
     * @param form
     * @return C0805W02SearchConditionDto
     */
    private C0802W01SearchConditionDto createGyosonyuCondition(String apkihoncd, String shopcd,
        C0802W01Form form, DbManagementSession dbSession) {

        C0802W01SearchConditionDto dto = new C0802W01SearchConditionDto();
        dto.setApkihoncd(apkihoncd);
        dto.setShopcd(shopcd);
        dto.setTokuisakicd(form.getBaseBean().getTokuisakicd());
        dto.setDlvrykbn(form.getBaseBean().getDlvrykbn());
        dto.setTokuisakikbn(form.getBaseBean().getTokuisakikbn());
        dto.setUnyodate(dbSession.getUnyoDate());
        dto.setRenkeishncd(form.getBaseBean().getRenkeishncd());

        return dto;
    }

    /**
     * 検索条件DTOを生成する.
     * 
     * @param C0802W01SearchResultDto
     * @return C0802W01BaseBean
     */
    private C0802W01BaseBean setBaseBean(C0802W01SearchResultDto dto,
        DbManagementSession dbSession) {

        C0802W01BaseBean bean = new C0802W01BaseBean();

        if ("0".equals(dto.getTokuisakidata())) {
            bean.setTokuisakidata("0");
        } else {
            bean.setTokuisakidata("1");
            // 処理区分
            bean.setPrcskbn(dto.getShorikbn());
            // 請求先コード
            bean.setSkysakicd(dto.getSkysakicd());
            // 配達拠点コード
            bean.setDlvrykyotencd(dto.getDlvrykyotencd());
            // 配達拠点名称
            bean.setDlvrykyotennm(dto.getDlvrykyotennm());
            // 配達エリアコード
            bean.setDlvryareacd(dto.getDlvryareacd());
            // 配達エリア名称
            bean.setDlvryareanm(dto.getDlvryareanm());
            // 配達会社コード
            bean.setDlvrycmpnycd(dto.getDlvrycmpnycd());
            // 配達会社名称
            bean.setDlvrycmpnynm(dto.getDlvrycmpnynm());
            // 配達管轄AP基本コード
            bean.setDlvrykankatsuapkihoncd(dto.getDlvrykankatsuapkihoncd());
            // 配達管轄ショップコード
            bean.setDlvrykankatsushopcd(dto.getDlvrykankatsushopcd());
            // 配達パターン
            bean.setDlvryptnnm(dto.getDlvrypatern());
            // 配達担当者
            bean.setDlvrytnshacd(ItemUtil.nullToString(dto.getDlvrytnshacd()));
            bean.setDlvrytnshamei(ItemUtil.nullToString(dto.getDlvrytnshamei()));
            // 伝票番号
            bean.setAcptdnpyno(dto.getAcptdnpyno());
            if (!Strings.isEmpty(dto.getAcptdnpyno())) {
                bean.setAcptdnpynodelim("-");
            } else {
                bean.setAcptdnpynodelim("");
            }
            bean.setEdano(dto.getEdano());
            bean.setDatsksmotokbnnm(dto.getDatsksmotokbnmei());
            // 不在置きフラグ
            bean.setFzaiokikanoflg(dto.getFzaiokikanoflg());
            // 納品先情報
            bean.setNohinsakijohon1("");
            bean.setNohinsakijohon2("");
            if (!Strings.isEmpty(dto.getNohinsakijohonmei1())) {
                bean.setNohinsakijohon1(dto.getNohinsakijohonmei1());
            }
            if (!Strings.isEmpty(dto.getNohinsakijohonmei2())) {
                bean.setNohinsakijohon2(dto.getNohinsakijohonmei2());
            }

            // 受注日
            bean.setAcptdd(dbSession.getUnyoDate());
            // 得意先状態
            bean.setTokuisakijotai(dto.getTokuisakijotai());
            // 得意先区分
            bean.setTokuisakikbn(dto.getTokuisakikbn());
            // 印刷メモ1
            bean.setPrntmemon1(dto.getPrntmemon1());
            // 印刷メモ2
            bean.setPrntmemon2(dto.getPrntmemon2());
            // 印刷メモ3
            bean.setPrntmemon3(dto.getPrntmemon3());
            // 配達順
            bean.setDlvryjun(dto.getDlvryjun());
            // 受注受付経路区分
            bean.setAcptrcptkeirokbn(CommonConstants.SETTEICD_093_03);
            // 配達区分
            bean.setDlvrykbn(dto.getDlvrykbn());

            // 持ち帰りフラグ
            bean.setMochikaeriflg("");
            if (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(dto.getDlvrykbn())) {
                bean.setMochikaeriflg("M");
            }
        }
        // 価格情報
        bean.setPriceinfo(dto.getPriceinfo());
        // 消費税情報
        bean.setJsshohitaxvo(getJsshohitaxvo());
        return bean;
    }

    /**
     * 検索条件DTOを生成する.
     * 
     * @param C0802W01SearchResultDto
     * @return C0802W01MeisaiDetailBean
     */
    private List<C0802W01MeisaiDetailBean> setMeisaiDetailBean(C0802W01SearchResultDto dto,
        C0802W01Form form) {
        if (ObjectUtils.isEmpty(dto.getSearchResultMeisaiDtoList())) {
            return new LinkedList<>();
        }
        return util.convertList(dto.getSearchResultMeisaiDtoList(), detailDto -> {
            C0802W01MeisaiDetailBean detailBean = new C0802W01MeisaiDetailBean();
            // 商品コード
            detailBean.setShncd(detailDto.getShncd());
            // 商品名称略称
            detailBean.setShnnmryakusho(detailDto.getShnnmryakusho());
            // 色
            detailBean.setIro(detailDto.getIro());
            // サイズ
            detailBean.setSz(detailDto.getSz());
            // 型式
            detailBean.setKatasiki(detailDto.getKatasiki());
            // 計上区分
            detailBean.setKeijokbn(detailDto.getKeijokbn());
            // 数量
            if (!Objects.isNull(detailDto.getAcptsuryo())) {
                detailBean.setAcptsuryo(String.valueOf(detailDto.getAcptsuryo()));
            }
            // 受注数量単位
            detailBean.setUritrtskitani(detailDto.getUritrtskitani());
            // 単位
            detailBean.setUritani(detailDto.getUritani());
            // 入数
            detailBean.setUriirisu(detailDto.getUriirisu());
            // 総数
            detailBean.setSokazu(detailDto.getSokazu());
            // 標準価格
            detailBean.setHyojununt(detailDto.getHyojununt());

            // 受注単価活性非活性制御区分
            detailBean.setAcptuntcntlkbn("0");
            String tokuisakikbn = "";
            if (!Strings.isEmpty(dto.getTokuisakikbn())) {
                tokuisakikbn = dto.getTokuisakikbn();
            } else {
                tokuisakikbn = detailDto.getTokuisakikbn();
            }

            if (CommonConstants.NYURYOKUMEISAIFLG_0.equals(detailDto.getTanpinmeisaiflg())) {
                detailBean.setAcptuntcntlkbn("0");
            } else if (!CommonConstants.KBN_TOKUISAKI_KOKUAKU.equals(tokuisakikbn)) {

                if (CommonConstants.KBN_KEIJO_KEIHI.equals(detailDto.getKeijokbn())
                    || CommonConstants.KBN_KEIJO_URIAGE.equals(detailDto.getKeijokbn())
                    || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(detailDto.getKeijokbn())) {
                    detailBean.setAcptuntcntlkbn("1");
                } else {
                    detailBean.setAcptuntcntlkbn("0");
                }

            } else if (CommonConstants.KBN_PRICE_HYOJUN.equals(detailDto.getPricekbn())) {
                detailBean.setAcptuntcntlkbn("0");
            } else if (CommonConstants.KBN_PRICE_OPEN.equals(detailDto.getPricekbn())) {
                detailBean.setAcptuntcntlkbn("1");
            } else {
                if (CommonConstants.KBN_TOKUISAKI_KOKUAKU.equals(tokuisakikbn)) {
                    if (CommonConstants.KBN_KEIJO_URIAGE.equals(detailDto.getKeijokbn())
                        || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(detailDto.getKeijokbn())) {
                        detailBean.setAcptuntcntlkbn("1");
                    } else {
                        detailBean.setAcptuntcntlkbn("0");
                    }
                }
            }

            // 販売価格
            detailBean.setAcptunt(detailDto.getAcptunt());
            // 金額
            detailBean.setKin(String.valueOf(detailDto.getAcptkin()));
            // 総回数
            if (!Objects.isNull(detailDto.getSoukaisu())) {
                detailBean.setSoukaisumei(String.valueOf(detailDto.getSoukaisu()));
            }
            // 残回数
            if (!Objects.isNull(detailDto.getZankaisu())) {
                detailBean.setZankaisumei(String.valueOf(detailDto.getZankaisu()));
            }
            // 明細備考
            detailBean.setMeisaibiko(detailDto.getMeisaibiko());
            // 大分類コード
            detailBean.setLctgrycd(detailDto.getLctgrycd());
            // 中分類コード
            detailBean.setMctgrycd(detailDto.getMctgrycd());
            // 小分類コード
            detailBean.setSctgrycd(detailDto.getSctgrycd());
            // 行番号（明細）
            detailBean.setGyono(detailDto.getGyono());
            // 更新処理フラグ
            detailBean.setUpdateflg(detailDto.getUpdateflg());
            // 受注伝票明細番号
            if (!Objects.isNull(detailDto.getAcptdnpymeisaino())) {
                detailBean.setAcptdnpymeisaino(String.valueOf(detailDto.getAcptdnpymeisaino()));
            }
            // 受注伝票明細枝番号
            if (!Objects.isNull(detailDto.getAcptdnpymeisaiedano())) {
                detailBean
                    .setAcptdnpymeisaiedano(String.valueOf(detailDto.getAcptdnpymeisaiedano()));
            }
            // 税区分
            detailBean.setTaxkbn(detailDto.getTaxkbn());
            // 有償区分
            detailBean.setYusyokbn(detailDto.getYusyokbn());
            // 明細区分
            detailBean.setMeisaikbn(detailDto.getMeisaikbn());
            // 入力明細フラグ
            detailBean.setNyuryokumeisaiflg(detailDto.getNyuryokumeisaiflg());
            // 単品明細フラグ
            detailBean.setTanpinmeisaiflg(detailDto.getTanpinmeisaiflg());
            // セット商品コード
            detailBean.setStshncd(detailDto.getStshncd());
            // 得意先区分
            detailBean.setTokuisakikbn(form.getBaseBean().getTokuisakikbn());
            // 価格区分
            detailBean.setPricekbn(detailDto.getPricekbn());

            // 標準価格切替日
            detailBean.setHyojunpricekirikaedd(detailDto.getHyojunpricekirikaedd());
            // 現標準価格
            detailBean.setGenzhyojunprice(detailDto.getGenzhyojunprice());
            // 新標準価格
            detailBean.setShinhyojunprice(detailDto.getShinhyojunprice());
            // 現標準持ち帰り価格
            detailBean.setGenzhyojunmochikaeriprice(detailDto.getGenzmochikaeriprice());
            // 新標準持ち帰り価格
            detailBean.setShinhyojunmochikaeriprice(detailDto.getShinmochikaeriprice());
            // ショップ価格切替日
            detailBean.setShoppricekirikaedd(detailDto.getShoppricekirikaedd());
            // 現ショップ価格
            detailBean.setGenzshopprice(detailDto.getGenzshopprice());
            // 新ショップ価格
            detailBean.setShinshopprice(detailDto.getShinshopprice());
            // 現ショップ持ち帰り価格
            detailBean.setGenzshopmochikaeriprice(detailDto.getGenzshopmochikaeriprice());
            // 新ショップ持ち帰り価格
            detailBean.setShinshopmochikaeriprice(detailDto.getShinshopmochikaeriprice());
            // 販促開始日
            detailBean.setHansokustrtdd(detailDto.getHansokustrtdd());
            // 販促終了日
            detailBean.setHansokushrydd(detailDto.getHansokushrydd());
            // 販促価格
            detailBean.setHansokuprice(detailDto.getHansokudlvryprice());
            // 販促持ち帰り価格
            detailBean.setHansokumochikaeriprice(detailDto.getHansokumochikaeriprice());
            // 得意先価格開始日
            detailBean.setTtpricestrtdd(detailDto.getTtpricestrtdd());
            // 得意先価格終了日
            detailBean.setTtpriceshrydd(detailDto.getTtpriceshrydd());
            // 得意先価格
            detailBean.setTtprice(detailDto.getTtprice());
            // 得意先持ち帰り価格
            detailBean.setTtmochikaeriprice(detailDto.getTtmochikaeriprice());
            return detailBean;
        });
    }

    /**
     * 登録更新処理
     * 
     * @param C0802W01Form
     * @param dbManagementSession
     * @return AciRestResponse
     * @throws Exception
     */
    @PutMapping("update")
    public AciRestResponse<C0802W01Form> update(@RequestBody C0802W01Form form,
        @SessionAttribute("dbManagementSession_C0802W01") DbManagementSession dbSession)
        throws Exception {

        log.debug("DB管理セッションの運用日付 : {}", dbSession.getUnyoDate());

        // 検索条件パラメータ作成
        C0802W01RegistrationConditionDto conditionDto = createRegistrationDto(form, dbSession);

        // 登録更新処理
        service.registration(conditionDto);

        // 登録更新正常終了後は得意先コードがクリアされる（明細を再度表示するには得意先を確定する必要がある）ので
        // セッション情報をクリアしておく（数量、残回数、総回数が全て空白や0になっていて登録されなかった明細行も画面に残ってしまうため）
        if (Objects.nonNull(c0802w00session.getDetailBeanList())) {
            c0802w00session.getDetailBeanList().clear();
        }

        // 更新結果の返却
        AciRestResponse<C0802W01Form> res = new AciRestResponse<>(form);

        return res;
    }

    /**
     * 項目チェック
     * 
     * @param form
     * @throws Exception
     */
    private void checkData(C0802W01Form form) throws Exception {

        // ACCIS2023-7060対応のため、処理ロジックを全体的に修正

        // 数量、総回数、残回数の全てが未入力の行をフィルタした上でチェックを行う
        List<C0802W01MeisaiDetailBean> detailBeanList =
            getAvailabledDetailBeanList(form.getDetailBeanList());

        // 相関チェック No.4 → 有効な明細が1件以上存在していること
        if (detailBeanList.isEmpty()) {
            // 行追加などせずに更新ボタンをクリックした場合、ListがNULLになっている
            throw BusinessExceptionFactory.validationFailed(MessageID.W113, "有効な明細");
        }

        detailBeanList.stream().forEach(detailBean -> {
            // 相関チェック No.1 → 数量、残回数、総回数の全てに入力があること
            List<FormField> formFieldList = new ArrayList<>();
            // 各項目ごとにチェックを行い、エラーの場合はエラーフィールド情報をリストに追加
            if (util.isZeroString(detailBean.getAcptsuryo())) {
                formFieldList.add(createErrorField("acptsuryo", detailBean.getIdxForCheck(), "数量"));
            }
            if (util.isZeroString(detailBean.getZankaisumei())) {
                formFieldList
                    .add(createErrorField("zankaisumei", detailBean.getIdxForCheck(), "残回数"));
            }
            if (util.isZeroString(detailBean.getSoukaisumei())) {
                formFieldList
                    .add(createErrorField("soukaisumei", detailBean.getIdxForCheck(), "総回数"));
            }
            // エラーフィールド情報が空ではない場合、例外をスローする
            if (!formFieldList.isEmpty()) {
                throw BusinessExceptionFactory.validationFailed(formFieldList, MessageID.W209);
            }

            // 各種単項目チェック

            // 単項目：数量：入力パターン
            String tmpAcptsuryo = detailBean.getAcptsuryo().replace(",", "");
            if (!tmpAcptsuryo.matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS_DECIMAL)) {
                throw BusinessExceptionFactory.validationFailed(
                    createErrorField("acptsuryo", detailBean.getIdxForCheck(), "数量"),
                    MessageID.W039, detailBean.getIdxForCheck(), "数量", "数字とマイナス記号と小数点");
            }
            // 単項目：数量：文字長
            tmpAcptsuryo = tmpAcptsuryo.replace("-", "");
            if (tmpAcptsuryo.length() >= 6) {
                throw BusinessExceptionFactory.validationFailed(
                    createErrorField("acptsuryo", detailBean.getIdxForCheck(), "数量"),
                    MessageID.W035, detailBean.getIdxForCheck(), "数量", "6");
            }
            // 単項目：総回数：入力パターン
            if (!detailBean.getSoukaisumei()
                .matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS)) {
                throw BusinessExceptionFactory.validationFailed(
                    createErrorField("soukaisumei", detailBean.getIdxForCheck(), "総回数"),
                    MessageID.W039, detailBean.getIdxForCheck(), "総回数", "数字のみ");

            }
            // 単項目：残回数：入力パターン
            if (!detailBean.getZankaisumei()
                .matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS)) {
                throw BusinessExceptionFactory.validationFailed(
                    createErrorField("zankaisumei", detailBean.getIdxForCheck(), "残回数"),
                    MessageID.W039, detailBean.getIdxForCheck(), "残回数", "数字のみ");
            }

            // 相関チェックNo.2 → 設定されている回数が1以上であること
            if (util.getIntValue(detailBean.getSoukaisumei()) < 1) {
                formFieldList
                    .add(createErrorField("zankaisumei", detailBean.getIdxForCheck(), "残回数"));
            }
            if (util.getIntValue(detailBean.getZankaisumei()) < 1) {
                formFieldList
                    .add(createErrorField("soukaisumei", detailBean.getIdxForCheck(), "総回数"));
            }
            // エラーフィールド情報が空ではない場合、例外をスローする
            if (!formFieldList.isEmpty()) {
                throw BusinessExceptionFactory.validationFailed(formFieldList, MessageID.W209);
            }

            // 単項目：販売価格：必須
            if (Strings.isEmpty(detailBean.getAcptunt())) {
                throw BusinessExceptionFactory.validationFailed(
                    createErrorField("acptunt", detailBean.getIdxForCheck(), "販売価格"),
                    MessageID.W037, detailBean.getIdxForCheck(), "販売価格");
            }
            // 単項目：販売価格：入力パターン
            String tmpAcptunt = detailBean.getAcptunt().replace(",", "");
            if (!tmpAcptunt.matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS_DECIMAL)) {
                throw BusinessExceptionFactory.validationFailed(
                    createErrorField("acptunt", detailBean.getIdxForCheck(), "販売価格"),
                    MessageID.W039, detailBean.getIdxForCheck(), "販売価格", "数字とマイナス記号と小数点");
            }
            // 単項目：販売価格：文字長
            tmpAcptunt = tmpAcptunt.replace("-", "");
            if (tmpAcptunt.length() >= 10) {
                throw BusinessExceptionFactory.validationFailed(
                    createErrorField("acptunt", detailBean.getIdxForCheck(), "販売価格"),
                    MessageID.W035, detailBean.getIdxForCheck(), "販売価格", "10");
            }
            // 単項目：明細備考：バイト数
            if (!ItemUtil.isByteSizeValid(detailBean.getMeisaibiko(), 40)) {
                throw BusinessExceptionFactory.validationFailed(
                    createErrorField("meisaibiko", detailBean.getIdxForCheck(), "明細備考"),
                    MessageID.W009, detailBean.getIdxForCheck());
            }
        });

        // 印刷メモ１～３チェック（アノテーションチェック不可のため）
        isPrntmemonByteCheck(form.getBaseBean().getPrntmemon1(), "1");
        isPrntmemonByteCheck(form.getBaseBean().getPrntmemon2(), "2");
        isPrntmemonByteCheck(form.getBaseBean().getPrntmemon3(), "3");
    }

    /**
     * 印刷メモの文字列長チェックを行う
     * 
     * @param prntmemon 印刷メモ
     * @param no No
     */
    private void isPrntmemonByteCheck(String prntmemon, String no) {
        if (!ObjectUtils.isEmpty(prntmemon)) {
            if (!ItemUtil.isByteSizeValid(prntmemon, 80)) {
                throw BusinessExceptionFactory
                    .validationFailed(new FormField(String.format("baseBean.prntmemon%s", no),
                        String.format("印刷メモ", no)), MessageID.W009);
            }
        }
    }

    /**
     * 受注配達明細に対するバリデーションエラー時のフィールド情報を生成する
     * 
     * @param fieldName フィールド名（物理名）
     * @param rowNumber 行番号
     * @param label ラベル（論理名）
     * @return フィールド情報
     */
    private FormField createErrorField(String fieldName, int rowNumber, String label) {
        return new FormField(String.format("detailBeanList[%d].%s", rowNumber - 1, fieldName),
            label);
    }

    /**
     * 検索条件DTOを生成する.
     * 
     * @param form
     * @param dbSession
     * @return C0805W02SearchConditionDto
     */
    private C0802W01RegistrationConditionDto createRegistrationDto(C0802W01Form form,
        DbManagementSession dbSession) {

        C0802W01RegistrationConditionDto dto = new C0802W01RegistrationConditionDto();

        // ＡＰ基本コード
        dto.setApkihoncd(form.getBaseBean().getApkihoncd());
        // ショップコード
        dto.setShopcd(form.getBaseBean().getShopcd());
        // 得意先コード
        dto.setTokuisakicd(form.getBaseBean().getTokuisakicd());
        // 運用日付
        dto.setUnyodate(dbSession.getUnyoDate());
        // 処理区分
        dto.setShorikbn(form.getBaseBean().getPrcskbn());
        // 受注伝票番号
        dto.setAcptdnpyno(form.getBaseBean().getAcptdnpyno());
        // 枝番号
        dto.setEdano(form.getBaseBean().getEdano());
        // 配達区分
        dto.setDlvrykbn(form.getBaseBean().getDlvrykbn());
        // 配達拠点コード
        dto.setDlvrykyotencd(form.getBaseBean().getDlvrykyotencd());
        // 配達管轄AP基本コード
        dto.setDlvrykankatsuapkihoncd(form.getBaseBean().getDlvrykankatsuapkihoncd());
        // 配達管轄ショップコード
        dto.setDlvrykankatsushopcd(form.getBaseBean().getDlvrykankatsushopcd());
        // 配達会社コード
        dto.setDlvrycmpnycd(form.getBaseBean().getDlvrycmpnycd());
        // 配達担当者コード
        dto.setDlvrytnshacd(form.getBaseBean().getDlvrytnshacd());
        // 留守置き
        dto.setFzaiokikanoflg(form.getBaseBean().getFzaiokikanoflg());
        // 得意先区分
        dto.setTokuisakikbn(form.getBaseBean().getTokuisakikbn());
        // 得意先状態
        dto.setTokuisakijotai(form.getBaseBean().getTokuisakijotai());
        // 印刷メモ1
        dto.setPrntmemon1(form.getBaseBean().getPrntmemon1());
        // 印刷メモ2
        dto.setPrntmemon2(form.getBaseBean().getPrntmemon2());
        // 印刷メモ3
        dto.setPrntmemon3(form.getBaseBean().getPrntmemon3());
        // 配達順
        dto.setDlvryjun(form.getBaseBean().getDlvryjun());
        // 受注受付経路区分
        dto.setAcptrcptkeirokbn(form.getBaseBean().getAcptrcptkeirokbn());
        // 配達エリアコード
        dto.setDlvryareacd(form.getBaseBean().getDlvryareacd());
        // 請求先コード
        dto.setSkysakicd(form.getBaseBean().getSkysakicd());
        // 受注担当者ＡＰ基本コード
        dto.setAcpttnshaapkihoncd(userSessionForm.getApkihoncd());
        // 受注担当者ショップコード
        dto.setAcpttnshashopcd(userSessionForm.getShopcd());
        // 受注担当者コード
        dto.setAcpttnshacd(userSessionForm.getTnshacd());
        // ログインユーザＡＰ基本コード
        dto.setLoginUserApkihoncd(userSessionForm.getApkihoncd());
        // ログインユーザショップコード
        dto.setLoginUserShopcd(userSessionForm.getShopcd());
        // ログインユーザ担当者コード
        dto.setLoginUserTnshacd(userSessionForm.getTnshacd());
        // 登録システム日付
        dto.setSystemDate(dbSession.getSystemDate());
        // 登録システム時間
        dto.setSystemTime(dbSession.getSystemTime());
        // 登録IPアドレス
        dto.setIpAddress(dbSession.getIpAddress());
        // 登録プログラムID
        dto.setProgramId(dbSession.getProgramId());

        // 数量、総回数、残回数の全てが未入力の行をフィルタする
        List<C0802W01MeisaiDetailBean> detailBeanList =
            getAvailabledDetailBeanList(form.getDetailBeanList());

        // 明細Beanリストを明細DTOリストに変換
        dto.setRegistrationMeisaiDtoList(util.convertList(detailBeanList, (detailBean) -> {
            C0802W01RegistrationMeisaiDto detailDto = new C0802W01RegistrationMeisaiDto();
            // 商品コード
            detailDto.setShncd(detailBean.getShncd());
            // 色
            detailDto.setIro(detailBean.getIro());
            // サイズ
            detailDto.setSz(detailBean.getSz());
            // 型式
            detailDto.setKatasiki(detailBean.getKatasiki());
            // 計上区分
            detailDto.setKeijokbn(detailBean.getKeijokbn());
            // 数量
            detailDto.setAcptsuryo(
                util.getNullableDecimalValue(ItemUtil.removeCommaDef(detailBean.getAcptsuryo())));
            // 入数
            detailDto.setUriirisu(ItemUtil.removeCommaDef(detailBean.getUriirisu()));
            // 総数
            detailDto.setSokazu(ItemUtil.removeCommaDef(detailBean.getSokazu()));
            // 標準価格
            detailDto.setHyojununt(ItemUtil.removeCommaDef(detailBean.getHyojununt()));
            // 販売価格
            detailDto.setAcptunt(ItemUtil.removeCommaDef(detailBean.getAcptunt()));
            // 金額
            detailDto.setAcptkin(
                util.getNullableLongValue(ItemUtil.removeCommaDef(detailBean.getKin())));
            // 総回数
            detailDto.setSoukaisu(
                util.getNullableShortValue(ItemUtil.removeCommaDef(detailBean.getSoukaisumei())));
            // 残回数
            detailDto.setZankaisu(
                util.getNullableShortValue(ItemUtil.removeCommaDef(detailBean.getZankaisumei())));
            // 明細備考
            detailDto.setMeisaibiko(detailBean.getMeisaibiko());
            // 大分類コード
            detailDto.setLctgrycd(detailBean.getLctgrycd());
            // 中分類コード
            detailDto.setMctgrycd(detailBean.getMctgrycd());
            // 小分類コード
            detailDto.setSctgrycd(detailBean.getSctgrycd());
            // 行番号（明細）
            detailDto.setGyono(detailBean.getGyono());
            // 更新処理フラグ
            detailDto.setUpdateflg(detailBean.getUpdateflg());
            // 受注伝票明細番号
            detailDto
                .setAcptdnpymeisaino(util.getNullableIntValue(detailBean.getAcptdnpymeisaino()));
            // 受注伝票明細枝番号
            detailDto.setAcptdnpymeisaiedano(
                util.getNullableShortValue(detailBean.getAcptdnpymeisaiedano()));
            // 税区分
            detailDto.setTaxkbn(detailBean.getTaxkbn());
            // 有償区分
            detailDto.setYusyokbn(detailBean.getYusyokbn());
            // 明細区分
            detailDto.setMeisaikbn(detailBean.getMeisaikbn());
            // 入力明細フラグ
            detailDto.setNyuryokumeisaiflg(detailBean.getNyuryokumeisaiflg());
            // 単品明細フラグ
            detailDto.setTanpinmeisaiflg(detailBean.getTanpinmeisaiflg());
            // セット商品コード
            detailDto.setStshncd(detailBean.getStshncd());
            return detailDto;
        }));

        return dto;
    }

    /**
     * 更新前チェック処理
     * 
     * @param form
     * @param dbSession
     * @return List<String>
     * @throws Exception
     */
    @PutMapping("check")
    public List<String> check(@RequestBody C0802W01Form form,
        @SessionAttribute("dbManagementSession_C0802W01") DbManagementSession dbSession)
        throws Exception {

        log.debug("DB管理セッションの運用日付 : {}", dbSession.getUnyoDate());

        // 項目チェック
        checkData(form);

        // 検索条件パラメータ作成
        C0802W01RegistrationConditionDto conditionDto = createRegistrationDto(form, dbSession);

        // 更新前チェック処理
        return service.checkParamPre(conditionDto);
    }

    /**
     * 消費税情報取得
     * 
     * @return List<String>
     */
    private List<String> getJsshohitaxvo() {
        List<String> taxinfoList = new ArrayList<>();
        for (TaxInfo taxinfo : taxSession.getTaxInfo()) {
            StringBuilder buf = new StringBuilder();
            buf.append("{taxkbn:").append(taxinfo.getTaxkbn());
            buf.append(",tekiyostrtdd:").append(taxinfo.getTekiyostrtdd());
            buf.append(",tekiyoshrydd:").append(taxinfo.getTekiyoshrydd());
            buf.append(",shohitax:").append(taxinfo.getShohitax());
            buf.append("}");
            taxinfoList.add(buf.toString());
        }
        return taxinfoList;
    }

    /**
     * 有効な明細行のみにフィルタされたリストを取得する
     * 
     * @param baseList 元となる明細リスト
     * @return フィルタされた明細リスト
     */
    private List<C0802W01MeisaiDetailBean> getAvailabledDetailBeanList(
        List<C0802W01MeisaiDetailBean> baseList) {

        // 数量、総回数、残回数の全てが未入力の行をフィルタした上でチェックを行う
        List<C0802W01MeisaiDetailBean> detailBeanList =
            Objects.requireNonNullElse(baseList, Collections.emptyList());
        return detailBeanList.stream()
            // フィルタ前に行番号を保持させる
            .map(util.withIndexFunction(1, (detailBean, idx) -> {
                detailBean.setIdxForCheck(idx);
                return detailBean;
            }))
            // 数量、総回数、残回数に入力があるデータのみにフィルタする
            .filter(detailBean -> !(util.isZeroString(detailBean.getAcptsuryo())
                && util.isZeroString(detailBean.getZankaisumei())
                && util.isZeroString(detailBean.getSoukaisumei())))
            .collect(util.toList());
    }
}
