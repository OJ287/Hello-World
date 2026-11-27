package jp.co.aquaclara.accis.web.controller.h2101;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.constants.ValidationConstants;
import jp.co.aquaclara.accis.common.exception.BusinessException;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.controller.commons.AccisWebBaseController;
import jp.co.aquaclara.accis.web.controller.commons.AciRestResponse;
import jp.co.aquaclara.accis.web.controller.commons.DbManagementSession;
import jp.co.aquaclara.accis.web.controller.commons.LoginSession;
import jp.co.aquaclara.accis.web.controller.commons.UserSessionForm;
import jp.co.aquaclara.accis.web.controller.h2101.bean.H2101W01BaseBean;
import jp.co.aquaclara.accis.web.controller.h2101.bean.H2101W01CountBean;
import jp.co.aquaclara.accis.web.controller.h2101.bean.H2101W01MeisaiBean;
import jp.co.aquaclara.accis.web.controller.h2101.bean.H2101W01MoveBean;
import jp.co.aquaclara.accis.web.controller.h2101.bean.H2101W01SlipBean;
import jp.co.aquaclara.accis.web.controller.h2101.form.H2101W01Form;
import jp.co.aquaclara.accis.web.controller.h2101.session.H2101W00Session;
import jp.co.aquaclara.accis.web.controller.z9999.bean.Z9999W01JobStateBean;
import jp.co.aquaclara.accis.web.domain.dto.h2101.DNPYKBN;
import jp.co.aquaclara.accis.web.domain.dto.h2101.H2101W01SearchConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.h2101.H2101W01SearchResultDto;
import jp.co.aquaclara.accis.web.domain.dto.h2101.H2101W01SearchResultMeisaiDto;
import jp.co.aquaclara.accis.web.domain.dto.h2101.H2101W01UpdateDto;
import jp.co.aquaclara.accis.web.domain.dto.h2101.H2101W01UpdateMeisaiDto;
import jp.co.aquaclara.accis.web.domain.dto.h2101.PRCSKBN;
import jp.co.aquaclara.accis.web.domain.service.c0800.UtilityService;
import jp.co.aquaclara.accis.web.domain.service.common.async.AsyncManagementService;
import jp.co.aquaclara.accis.web.domain.service.common.async.AsyncManagementServiceModel;
import jp.co.aquaclara.accis.web.domain.service.h2101.H2101R01Service;
import jp.co.aquaclara.accis.web.domain.service.h2101.H2101W01Service;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.AciDataNotFoundException;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import jp.co.aquaclara.accis.web.exception.dto.FormField;

/**
 * H2101w01_入出庫入力APIコントローラクラス.
 */
@RestController
@RequestMapping("/api/h2101w01")
public class H2101W01APIController extends AccisWebBaseController {

    /**
     * 入出庫入力サービス
     */
    @Autowired
    private H2101W01Service h2101w01Service;
    /**
     * 入出庫入力関連セッション情報
     */
    @Autowired
    private H2101W00Session h2101w00Session;
    /**
     * 非同期処理管理(受付)サービス
     */
    @Autowired
    private AsyncManagementService<H2101W01UpdateDto> asyncManagementService;
    /**
     * 納品書控え／出荷伝票サービス
     */
    @Autowired
    private H2101R01Service h2101r01Service;
    /**
     * ユーザセッション情報
     */
    @Autowired
    private UserSessionForm userSessionForm;
    /**
     * ログインセッション情報
     */
    @Autowired
    private LoginSession loginSession;

    @Autowired
    private UtilityService util;

    /**
     * 検索処理
     * 
     * @param form H2101W01Form
     * @param dbManagementSession DbManagementSession
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param pageable Pageable
     * @param response HttpServletResponse
     * @return Page<H2101W01MeisaiBean>
     */
    @SuppressWarnings("unchecked")
    @GetMapping("/search/{apkihoncd}/{shopcd}")
    public Page<H2101W01MeisaiBean> search(@ModelAttribute("h2101W01Form") H2101W01Form form,
        @ModelAttribute(name = "dbManagementSession_H2101W01",
            binding = false) DbManagementSession dbManagementSession,
        @PathVariable("apkihoncd") String apkihoncd, @PathVariable("shopcd") String shopcd,
        @PageableDefault(size = 999) Pageable pageable, HttpServletResponse response) {

        H2101W01BaseBean baseBean = form.getBaseBean();
        H2101W01SlipBean slipBean = form.getSlipBean();

        List<H2101W01MeisaiBean> meisaiList = new ArrayList<>();

        // 行追加でコールされた場合
        if (!StringUtils.isEmpty(baseBean.getAddrowflg())) {
            // 相関チェック
            checkCommonParam(form);
            // 追加行商品データ取得
            H2101W01SearchResultMeisaiDto searchDto =
                h2101w01Service.getMeisaiDtoForAddRow(apkihoncd, shopcd, baseBean.getShncdadd(),
                    DateTimeUtil.toNoSlashedDate(slipBean.getSiiredd()),
                    dbManagementSession.getUnyoDate());
            H2101W01MeisaiBean meisaiBean = getMeisaiBean(searchDto);

            // セッションで保持する明細行に追加して返却する
            meisaiList = addRow(meisaiBean, baseBean);

            // 行挿入でコールされた場合
        } else if (!StringUtils.isEmpty(baseBean.getInsertionrowflg())) {
            // 相関チェック
            checkCommonParam(form);
            // 追加行商品データ取得
            H2101W01SearchResultMeisaiDto searchDto =
                h2101w01Service.getMeisaiDtoForAddRow(apkihoncd, shopcd, baseBean.getShncdadd(),
                    DateTimeUtil.toNoSlashedDate(slipBean.getSiiredd()),
                    dbManagementSession.getUnyoDate());
            H2101W01MeisaiBean meisaiBean = getMeisaiBean(searchDto);

            // セッションで保持する明細行に追加して返却する
            meisaiList = addRow(meisaiBean, baseBean);

            // 行削除でコールされた場合
        } else if (!StringUtils.isEmpty(baseBean.getDeleteindex())) {
            // セッションに保持する明細を行削除して返却する
            meisaiList = deleteRow(baseBean);
        } else {
            // 相関チェック
            checkParam(form);

            // 検索用Dto作成
            H2101W01SearchConditionDto conditionDto = createCondition(apkihoncd, shopcd,
                baseBean.getPrcskbn(), baseBean.getNyushukkodnpyno());

            H2101W01SearchResultDto resultDto = new H2101W01SearchResultDto();

            try {
                // Serviceの検索処理呼出
                resultDto = h2101w01Service.search(conditionDto);
            } catch (AciDataNotFoundException nfe) {
                // セッションで保持する明細行をクリアする
                h2101w00Session.setMeisaiBeanList(null);
                throw nfe;
            }

            List<H2101W01SearchResultMeisaiDto> resultList = resultDto.getMeisaiDtoList();

            // 最大表示件数オーバーの場合、ステータスコードを206とする
            resultList = (List<H2101W01SearchResultMeisaiDto>) chkLength(resultList, response);
            resultDto.setMeisaiDtoList(resultList);

            // セッションに退避
            setSession(resultDto);

            meisaiList = h2101w00Session.getMeisaiBeanList();
        }

        int totalRecordCount = meisaiList.size();

        if (totalRecordCount > 0) {
            meisaiList = super.selectPage(pageable, meisaiList);
        }
        Page<H2101W01MeisaiBean> result = new PageImpl<>(meisaiList, pageable, totalRecordCount);

        return result;
    }

    /**
     * 相関チェック(共通)
     * 
     * @param form H2101W01Form
     */
    private void checkCommonParam(H2101W01Form form) {
        // 処理区分「1:新規」のチェック
        PRCSKBN prcskbn = PRCSKBN.of(form.getBaseBean().getPrcskbn());
        if (PRCSKBN.REGIST == prcskbn) {
            // 入出庫日必須チェック
            if (StringUtils.isEmpty(form.getSlipBean().getSiiredd())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("slipBean.siiredd", "入出庫日"), MessageID.W011, "入出庫日");
            }
            // 入出庫日日付フォーマットチェック
            if (!form.getSlipBean().getSiiredd()
                .matches(ValidationConstants.REGEXP_YYYY_MM_DD_SLASH)) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("slipBean.siiredd", "入出庫日"), MessageID.W016, "入出庫日");
            }
        }
    }

    /**
     * 検索処理用相関チェック
     * 
     * @param form H2101W01Form
     */
    private void checkParam(H2101W01Form form) {
        // 処理区分「2:取消」、かつ伝票番号が未設定の場合エラー
        PRCSKBN prcskbn = PRCSKBN.of(form.getBaseBean().getPrcskbn());
        if (PRCSKBN.CANCEL == prcskbn
            && StringUtils.isEmpty(form.getBaseBean().getNyushukkodnpyno())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.nyushukkodnpyno", "伝票番号"), MessageID.W011, "伝票番号");
        }
    }

    /**
     * H2101W01MeisaiBean設定
     * 
     * @param dto H2101W01SearchResultMeisaiDto
     * @return H2101W01MeisaiBean
     */
    private H2101W01MeisaiBean getMeisaiBean(H2101W01SearchResultMeisaiDto dto) {
        return new H2101W01MeisaiBean(dto.getShncd(), // 商品コード
            dto.getShnnm(), // 商品略称名
            dto.getIro(), // 色
            dto.getSznm(), // サイズ
            dto.getKatasiki(), // 型式
            dto.getKatasikimei(), // 型式名称
            ItemUtil.editCommaDecimal(dto.getSuryo()), // 数量2
            dto.getSiiretrtskitaninm(), // 数量単位2
            ItemUtil.editComma(dto.getIrisu()), // 入数2
            dto.getSiiretaninm(), // 入数単位2
            ItemUtil.editCommaDecimal(dto.getSokazu()), // 総数2
            ItemUtil.editComma(dto.getUnt()), // 単価2
            ItemUtil.editComma(dto.getKin()), // 金額2
            "", // 消費税金額2
            dto.getMeisaibiko(), // 明細備考
            dto.getLctgrycd(), // 大分類コード
            dto.getMctgrycd(), // 中分類コード
            dto.getSctgrycd(), // 小分類コード
            dto.getPricekbn(), // 価格区分
            dto.getHyojunpricekirikaedd(), // 標準価格切替日
            dto.getGenzhyojunprice(), // 現標準価格
            dto.getGenzmochikaeriprice(), // 現標準持ち帰り価格
            dto.getShinhyojunprice(), // 新標準価格
            dto.getShinmochikaeriprice(), // 新標準価格持ち帰り価格
            dto.getOpnpricekirikaedd(), // ショップ価格切替日
            dto.getGenzopnprice(), // 現ショップ価格
            dto.getGenzopnmochikaeriprice(), // 現ショップ持ち帰り価格
            dto.getShinopnprice(), // 新ショップ価格
            dto.getShinopnmochikaeriprice(), // 新ショップ持ち帰り価格
            dto.getHansokustrtdd(), // 販促開始日
            dto.getHansokushrydd(), // 販促終了日
            dto.getHansokudlvryprice(), // 販促価格
            dto.getHansokumochikaeriprice(), // 販促持ち帰り価格
            dto.getZaikount(), // 在庫単価
            "", // 商品税区分
            dto.getZaikokanritrgtflgshmv(), // 在庫管理対象フラグ
            dto.getShnapkihoncd() // 商品AP基本コード
        );
    }

    /**
     * 行追加処理(行追加/挿入処理から呼出)
     * 
     * @param addBean H2101W01MeisaiBean
     * @param baseBean H2101W01BaseBean
     * @return H2101W01MeisaiBeanリスト
     */
    private List<H2101W01MeisaiBean> addRow(H2101W01MeisaiBean addBean, H2101W01BaseBean baseBean) {
        // 行追加フラグ
        String gyoAddFlg = baseBean.getAddrowflg();
        // 追加行
        int index = util.getIntValue(baseBean.getInsertindex());
        // 明細が0件の場合はセッションに作成
        if (ObjectUtils.isEmpty(h2101w00Session.getMeisaiBeanList())) {
            h2101w00Session.setMeisaiBeanList(new ArrayList<>());
        }
        // セッションで保持する明細行に追加
        if (!StringUtils.isEmpty(gyoAddFlg)) {
            // 行追加の場合、最下部に追加
            h2101w00Session.getMeisaiBeanList().add(addBean);
        } else {
            // 行挿入の場合、該当行の上部に追加
            h2101w00Session.getMeisaiBeanList().add(index, addBean);
        }
        // セッションで保持する明細行を返却
        return h2101w00Session.getMeisaiBeanList();
    }

    /**
     * 明細行削除処理
     * 
     * @param baseBean H2101W01BaseBean
     * @return H2101W01MeisaiBeanリスト
     */
    private List<H2101W01MeisaiBean> deleteRow(H2101W01BaseBean baseBean) {
        // 削除行の特定
        int index = util.getIntValue(baseBean.getDeleteindex());
        // セッションで保持する明細行を削除
        h2101w00Session.getMeisaiBeanList().remove(index);
        // セッションで保持する明細行を返却
        return h2101w00Session.getMeisaiBeanList();
    }

    /**
     * 検索用Dto生成
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param prcskbn 処理区分
     * @param nyushukkodnpyno 伝票番号
     * @return dto H2101W01SearchConditionDto
     */
    private H2101W01SearchConditionDto createCondition(String apkihoncd, String shopcd,
        String prcskbn, String nyushukkodnpyno) {

        H2101W01SearchConditionDto dto = new H2101W01SearchConditionDto();
        // AP基本コードの設定
        dto.setApkihoncd(apkihoncd);
        // ショップコードの設定
        dto.setShopcd(shopcd);
        // 処理区分の設定
        dto.setPrcskbn(prcskbn);
        // 入出庫伝票番号の設定
        dto.setNyushukkodnpyno(nyushukkodnpyno);
        // 明細最大件数
        dto.setLimit(loginSession.getMaxDisplayCount());

        return dto;
    }

    /**
     * 検索結果をMeisaiBean変換
     * 
     * @param resultList List<H2101W1SearchResultDto>
     * @return meisaiBeanList List<H2101W01MeisaiBean>
     */
    private List<H2101W01MeisaiBean> getInitMeisaiListBean(
        List<H2101W01SearchResultMeisaiDto> resultList) {

        return util.convertList(resultList, (resultData) -> {
            return H2101W01MeisaiBean.builder()
                // 商品コード
                .shncdmei(resultData.getShncd())
                // 商品略称名
                .shnnmryakushomei(resultData.getShnnm())
                // 色
                .iromei(resultData.getIro())
                // サイズ
                .szmei(resultData.getSznm())
                // 型式
                .katasikimei(resultData.getKatasiki())
                // 型式名称
                .katasikinmmei(resultData.getKatasikimei())
                // 数量2
                .suryo2mei(ItemUtil.editCommaDecimal(resultData.getSuryo()))
                // 数量単位2
                .uritani2mei(resultData.getSiiretrtskitaninm())
                // 入数2
                .irisu2mei(ItemUtil.editComma(resultData.getIrisu()))
                // 入数単位2
                .irisutani2mei(resultData.getSiiretaninm())
                // 総数2
                .sokazu2mei(ItemUtil.editCommaDecimal(resultData.getSokazu()))
                // 単価2
                .unt2mei(ItemUtil.editComma(resultData.getUnt()))
                // 金額2
                .kin2mei(ItemUtil.editComma(resultData.getKin()))
                // 明細備考
                .meisaibikomei(resultData.getMeisaibiko())
                // 大分類コード
                .lctgrycd(resultData.getLctgrycd())
                // 中分類コード
                .mctgrycd(resultData.getMctgrycd())
                // 小分類コード
                .sctgrycd(resultData.getSctgrycd())
                // 価格区分
                .pricekbn(resultData.getPricekbn())
                // 標準価格切替日
                .hyojunpricekirikaedd(resultData.getHyojunpricekirikaedd())
                // 現標準価格
                .genzhyojunprice(resultData.getGenzhyojunprice())
                // 現標準持ち帰り価格
                .genzhyojunmochikaeriprice(resultData.getGenzmochikaeriprice())
                // 新標準価格
                .shinhyojunprice(resultData.getShinhyojunprice())
                // 新標準持ち帰り価格
                .shinhyojunmochikaeriprice(resultData.getShinmochikaeriprice())
                // ショップ価格切替日
                .shoppricekirikaedd(resultData.getOpnpricekirikaedd())
                // 現ショップ価格
                .genzshopprice(resultData.getGenzopnprice())
                // 現ショップ持ち帰り価格
                .genzshopmochikaeriprice(resultData.getGenzopnmochikaeriprice())
                // 新ショップ価格
                .shinshopprice(resultData.getShinopnprice())
                // 新ショップ持ち帰り価格
                .shinshopmochikaeriprice(resultData.getShinopnmochikaeriprice())
                // 販促開始日
                .hansokustrtdd(resultData.getHansokustrtdd())
                // 販促終了日
                .hansokushrydd(resultData.getHansokushrydd())
                // 販促価格
                .hansokuprice(resultData.getHansokudlvryprice())
                // 販促持ち帰り価格
                .hansokumochikaeriprice(resultData.getHansokumochikaeriprice())
                // 在庫単価
                .zaikount(resultData.getZaikount())
                // 商品税区分
                .shntaxkbn(resultData.getTaxkbn())
                // 在庫管理対象フラグ
                .zaikokanritrgtflg(resultData.getZaikokanritrgtflgshmv()).build();
        });
    }

    /**
     * CountBean用検索処理
     * 
     * @param form H2101W01Form
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param nyushukkodnpyno 入出庫伝票番号
     * @return countBean H2101W01CountBean
     */
    @GetMapping("/count/{apkihoncd}/{shopcd}")
    public H2101W01CountBean setCountBean(@ModelAttribute("h2101W01Form") H2101W01Form form,
        @PathVariable("apkihoncd") String apkihoncd, @PathVariable("shopcd") String shopcd,
        @RequestParam(name = "nyushukkodnpyno", required = false) String nyushukkodnpyno) {
        // セッションで保持するCountBeanの返却
        return h2101w00Session.getCountBean();
    }

    /**
     * 検索結果をCountBean設定
     * 
     * @param result H2101W01SearchResultDto
     * @return countBean H2101W01CountBean
     */
    private H2101W01CountBean getInitCountBean(H2101W01SearchResultDto result) {
        return H2101W01CountBean.builder().siirekin(result.getSiirekin()).build();
    }

    /**
     * MoveBean用検索処理
     * 
     * @param form H2101W01Form
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param nyushukkodnpyno 入出庫伝票番号
     * @return moveBean H2101W01MoveBean
     */
    @GetMapping("/move/{apkihoncd}/{shopcd}")
    public H2101W01MoveBean setMoveBean(@ModelAttribute("h2101W01Form") H2101W01Form form,
        @PathVariable("apkihoncd") String apkihoncd, @PathVariable("shopcd") String shopcd,
        @RequestParam(name = "nyushukkodnpyno", required = true) String nyushukkodnpyno) {
        // セッションで保持するMoveBeanの返却
        return h2101w00Session.getMoveBean();
    }

    /**
     * 検索結果をMoveBean設定
     * 
     * @param result H2101W01SearchResultDto
     * @return moveBean H2101W01MoveBean
     */
    private H2101W01MoveBean getInitMoveBean(H2101W01SearchResultDto result) {
        H2101W01MoveBean moveBean = new H2101W01MoveBean();
        // 取引区分1
        moveBean.setTorihikikbn1(result.getTorihikikbn1());
        // 取引区分1名称
        moveBean.setTorihikikbn1nm(result.getTorihikikbn1nm());
        // 実績担当者コード1
        moveBean.setJissekitnshacd1(result.getJissekitnshacd1());
        // 担当者名称1
        moveBean.setTnshamei1(result.getTnshamei1());
        // 倉庫1
        moveBean.setSoko1("");
        // 移動元倉庫コード
        moveBean.setNyukosakisokocd(result.getNyukosakisokocd());
        // 移動元倉庫名称
        moveBean.setSokonmn1(result.getSokonmn1());
        // 指定無1
        moveBean.setShiteins1("");
        // 取引区分2
        moveBean.setTorihikikbn2(result.getTorihikikbn2());
        // 取引区分2名称
        moveBean.setTorihikikbn2nm(result.getTorihikikbn2nm());
        // 実績担当者コード2
        moveBean.setJissekitnshacd2(result.getJissekitnshacd2());
        // 担当者名称2
        moveBean.setTnshamei2(result.getTnshamei2());
        // 倉庫2
        moveBean.setSoko2("");
        // 移動先倉庫コード
        moveBean.setShukkosakisokocd(result.getShukkasakisokocd());
        // 移動先倉庫名称
        moveBean.setSokonmn2(result.getSokonmn2());
        // 指定無2
        moveBean.setShiteins2("");
        return moveBean;
    }

    /**
     * SlipBean検索処理
     * 
     * @param form H2101W01Form
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param nyushukkodnpyno 入出庫伝票番号
     * @return slipBean H2101W01SlipBean
     */
    @GetMapping("/slip/{apkihoncd}/{shopcd}/{nyushukkodnpyno}")
    public H2101W01SlipBean slip(@ModelAttribute("h2101W01Form") H2101W01Form form,
        @PathVariable("apkihoncd") String apkihoncd, @PathVariable("shopcd") String shopcd,
        @PathVariable("nyushukkodnpyno") String nyushukkodnpyno) {
        // セッションで保持するSlipBeanの返却
        return h2101w00Session.getSlipBean();
    }

    /**
     * 検索結果をSlipBean設定
     * 
     * @param result H2101W01SearchResultDto
     * @return slipBean H2101W01SlipBean
     */
    private H2101W01SlipBean getInitSlipBean(H2101W01SearchResultDto result) {
        H2101W01SlipBean slipBean = new H2101W01SlipBean();
        // 入出庫日
        slipBean.setSiiredd(result.getSiiredd());
        // 伝票区分（dnpykbnSelectItems（伝票区分セレクトボックスリストの選択値））
        slipBean.setDnpykbn(result.getDnpykbn());
        // 伝票区分ラベル
        slipBean.setDnpykbncaption(result.getDnpykbnnm());
        // 伝票番号
        slipBean.setDnpyno(result.getDnpyno());
        // 備考
        slipBean.setBiko(result.getBiko());
        return slipBean;
    }

    /**
     * 更新処理
     * 
     * @param form H2101W01Form
     * @param dbManagementSession DBManagementSession
     * @return res AciResponse<H2101W01Form>
     */
    @PutMapping("update")
    public AciRestResponse<H2101W01Form> update(@Validated @RequestBody H2101W01Form form,
        @ModelAttribute(name = "dbManagementSession_H2101W01",
            binding = true) DbManagementSession dbManagementSession) {

        // 相関チェック
        checkCommonParam(form);
        checkUpdateParam(form, h2101w00Session.getMeisaiBeanList());

        // 更新用DTO作成
        H2101W01UpdateDto updateDto = createUpdateDto(form, dbManagementSession);
        List<H2101W01UpdateMeisaiDto> updateMeisaiDto =
            createUpdateMeisaiDto(h2101w00Session.getMeisaiBeanList(), dbManagementSession);

        // 更新処理実行
        h2101w01Service.update(updateDto, updateMeisaiDto);

        // formBeanを更新
        form.setMeisaiBeanList(null);
        form.getSlipBean().setDnpyno(updateDto.getNewdnpyno());

        // 更新結果の返却
        AciRestResponse<H2101W01Form> res = new AciRestResponse<>(form);

        return res;
    }

    /**
     * 更新後に印刷処理
     * 
     * @param form H2101W01Form
     * @param dbManagementSession DBManagementSession
     * @return res AciRestResponse<Z9999W01JobStateBean>
     */
    @PutMapping("updateprint")
    public AciRestResponse<Z9999W01JobStateBean> updateWithPrint(
        @Validated @RequestBody H2101W01Form form,
        @ModelAttribute(name = "dbManagementSession_H2101W01",
            binding = true) DbManagementSession dbManagementSession) {

        // 相関チェック
        checkCommonParam(form);
        checkUpdateParam(form, h2101w00Session.getMeisaiBeanList());

        // 更新用DTO作成
        H2101W01UpdateDto updateDto = createUpdateDto(form, dbManagementSession);
        List<H2101W01UpdateMeisaiDto> updateMeisaiDto =
            createUpdateMeisaiDto(h2101w00Session.getMeisaiBeanList(), dbManagementSession);

        // 更新処理実行
        h2101w01Service.update(updateDto, updateMeisaiDto);

        // 伝票番号をセッションにセット
        h2101w00Session.setNewDnpyno(updateDto.getNewdnpyno());

        // セッションクリア
        clearSession();

        AsyncManagementServiceModel<H2101W01UpdateDto> asyncManagementServiceModel =
            new AsyncManagementServiceModel<>(updateDto, userSessionForm, dbManagementSession,
                h2101r01Service);
        try {
            // 非同期業務処理実行＆受付ID取得
            Z9999W01JobStateBean jobStateBean =
                asyncManagementService.entryAsyncService(asyncManagementServiceModel);

            // 取得した受付IDをファイルダウンロード画面へのパラメータとして設定する
            return new AciRestResponse<Z9999W01JobStateBean>(jobStateBean);
        } catch (BusinessException be) {
            // 業務例外の場合、エラーメッセージを画面表示する
            throw BusinessExceptionFactory.validationFailed(MessageID.W260, be.getMessage());
        } catch (Exception e) {
            // システム例外の場合、エラーメッセージを画面表示する
            throw BusinessExceptionFactory.validationFailed(MessageID.E002);
        }
    }

    /**
     * 新規採番伝票番号取得処理
     * 
     * @return 新規採番した入出庫伝票番号
     */
    @GetMapping("newdnpyno")
    public String newdnpyno() {
        // セッションで保持する新規採番伝票番号を返却
        return h2101w00Session.getNewDnpyno();
    }

    /**
     * 更新処理用相関チェック
     * 
     * @param form H2101W01Form
     * @param meisaiBeanList List<H2101W01MeisaiBean>
     */
    private void checkUpdateParam(H2101W01Form form, List<H2101W01MeisaiBean> meisaiBeanList) {

        PRCSKBN prcskbn = PRCSKBN.of(form.getBaseBean().getPrcskbn());
        DNPYKBN dnpykbn = DNPYKBN.of(form.getSlipBean().getDnpykbn());

        /* 相関チェック */
        // 処理区分「1:新規」のチェック
        if (PRCSKBN.REGIST == prcskbn) {
            // 明細行存在チェック
            if (ObjectUtils.isEmpty(meisaiBeanList)) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W104);
            }
            // 伝票区分「14：出庫」「13:調整出庫」「22:倉庫移動」のチェック
            if (DNPYKBN.SHIPMENT == dnpykbn || DNPYKBN.ADJUST_SHIPMENT == dnpykbn
                || DNPYKBN.MOVEMENT == dnpykbn) {
                // 実績担当者コード（移動元）が｢正数または0｣以外の場合、エラー
                if (!StringUtils.isEmpty(form.getMoveBean().getJissekitnshacd1())
                    && !form.getMoveBean().getJissekitnshacd1()
                        .matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS)) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("moveBean.jissekitnshacd1", "実績担当者コード（移動元）"), MessageID.W013,
                        "実績担当者コード（移動元）", "0～9のみ");
                }
                // 倉庫コード（移動元）が未入力(非空文字)の場合、エラー
                if (StringUtils.isEmpty(form.getMoveBean().getNyukosakisokocd())) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("moveBean.nyukosakisokocd", "倉庫コード（移動元）"), MessageID.W011,
                        "倉庫コード（移動元）");
                }
                // 倉庫コード（移動元）が｢正数または0｣以外の場合、エラー
                if (!form.getMoveBean().getNyukosakisokocd()
                    .matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS)) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("moveBean.nyukosakisokocd", "倉庫コード（移動元）"), MessageID.W013,
                        "倉庫コード（移動元）", "0～9のみ");
                }
            }
            // 伝票区分「04:入庫」「03:調整入庫」「22:倉庫移動」のチェック
            if (DNPYKBN.RECEIPT == dnpykbn || DNPYKBN.ADJUST_RECEIPT == dnpykbn
                || DNPYKBN.MOVEMENT == dnpykbn) {
                // 実績担当者コード（移動先）が｢正数または0｣以外の場合、エラー
                if (!StringUtils.isEmpty(form.getMoveBean().getJissekitnshacd2())
                    && !form.getMoveBean().getJissekitnshacd2()
                        .matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS)) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("moveBean.jissekitnshacd2", "実績担当者コード（移動先）"), MessageID.W013,
                        "実績担当者コード（移動先）", "0～9のみ");
                }
                // 倉庫コード（移動先）が未入力(非空文字)の場合、エラー
                if (StringUtils.isEmpty(form.getMoveBean().getShukkosakisokocd())) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("moveBean.shukkosakisokocd", "倉庫コード（移動先）"), MessageID.W011,
                        "倉庫コード（移動先）");
                }
                // 倉庫コード（移動先）が｢正数または0｣以外の場合、エラー
                if (!form.getMoveBean().getShukkosakisokocd()
                    .matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS)) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("moveBean.shukkosakisokocd", "倉庫コード（移動先）"), MessageID.W013,
                        "倉庫コード（移動先）", "0～9のみ");
                }
            }
        }
        // 処理区分「1:新規」「2:取消」のチェック
        if (PRCSKBN.REGIST == prcskbn || PRCSKBN.CANCEL == prcskbn) {
            // 伝票区分「14：出庫」「13:調整出庫」「22:倉庫移動」のチェック
            if (DNPYKBN.SHIPMENT == dnpykbn || DNPYKBN.ADJUST_SHIPMENT == dnpykbn
                || DNPYKBN.MOVEMENT == dnpykbn) {
                // 移動元倉庫コード必須チェック
                if (StringUtils.isEmpty(form.getMoveBean().getNyukosakisokocd())) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("moveBean.nyukosakisokocd", "移動元：倉庫コード"), MessageID.W011,
                        "移動元：倉庫コード");
                }
            }
            // 伝票区分「04:入庫」「03:調整入庫」「22:倉庫移動」のチェック
            if (DNPYKBN.RECEIPT == dnpykbn || DNPYKBN.ADJUST_RECEIPT == dnpykbn
                || DNPYKBN.MOVEMENT == dnpykbn) {
                // 移動先倉庫コード必須チェック
                if (StringUtils.isEmpty(form.getMoveBean().getShukkosakisokocd())) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("moveBean.shukkosakisokocd", "移動先：倉庫コード"), MessageID.W011,
                        "移動先：倉庫コード");
                }
            }
            // 伝票区分「22:倉庫移動」のチェック
            if (DNPYKBN.MOVEMENT == dnpykbn) {
                // 移動元倉庫コード・移動先倉庫コード不一致チェック
                if (form.getMoveBean().getNyukosakisokocd()
                    .equals(form.getMoveBean().getShukkosakisokocd())) {
                    List<FormField> fields = new ArrayList<>();
                    fields.add(new FormField("moveBean.nyukosakisokocd", "移動元：倉庫コード"));
                    fields.add(new FormField("moveBean.shukkosakisokocd", "移動先：倉庫コード"));
                    throw BusinessExceptionFactory.validationFailed(fields, MessageID.W103,
                        "倉庫コード");
                }
            }
        }

        /* 明細行チェック */
        meisaiBeanList.forEach(util.withIndexConsumer(1, (meisaiBean, index) -> {
            Pattern decimalPattern =
                Pattern.compile(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS_DECIMAL);
            /* 数量チェック */
            FormField suryoField =
                new FormField(String.format("meisaiBeanList[%d].suryo2mei", index - 1), "数量");
            String suryo = ItemUtil.removeComma(meisaiBean.getSuryo2mei());
            // 数量フォーマットチェック
            Matcher suryoMatcher = decimalPattern.matcher(suryo);
            if (!suryoMatcher.matches()) {
                throw BusinessExceptionFactory.validationFailed(suryoField, MessageID.W039, index,
                    "数量", "数字とマイナス記号と小数点");
            }
            if (BigDecimal.ZERO.stripTrailingZeros().toPlainString().equals(suryo)) {
                throw BusinessExceptionFactory.validationFailed(suryoField, MessageID.W113,
                    "数量には0以外");
            }
            // 数量整数部桁数チェック
            if (ItemUtil.getIntegerPartLength(suryo) > 5) {
                throw BusinessExceptionFactory.validationFailed(suryoField, MessageID.W044, index,
                    "数量", "5");
            }
            // 数量小数部桁数チェック
            if (ItemUtil.getDecimalPartLength(suryo) > 1) {
                throw BusinessExceptionFactory.validationFailed(suryoField, MessageID.W036, index,
                    "数量", "1");
            }
            // 数量文字列長チェック
            if ((ItemUtil.getIntegerPartLength(suryo) + ItemUtil.getDecimalPartLength(suryo)) > 6) {
                throw BusinessExceptionFactory.validationFailed(suryoField, MessageID.W035, index,
                    "数量", "6");
            }

            // 単価に設定値がある場合のみチェック
            if (!StringUtils.isEmpty(meisaiBean.getUnt2mei())) {
                /* 単価チェック */
                FormField untField =
                    new FormField(String.format("meisaiBeanList[%d].unt2mei", index - 1), "単価");
                String unt = ItemUtil.removeComma(meisaiBean.getUnt2mei());
                // 単価フォーマットチェック
                Matcher untMatcher = decimalPattern.matcher(unt);
                if (!untMatcher.matches()) {
                    throw BusinessExceptionFactory.validationFailed(untField, MessageID.W039, index,
                        "単価", "数字とマイナス記号と小数点");
                }
                // 単価整数部桁数チェック
                if (ItemUtil.getIntegerPartLength(unt) > 6) {
                    throw BusinessExceptionFactory.validationFailed(untField, MessageID.W044, index,
                        "単価", "6");
                }
                // 単価小数部桁数チェック
                if (ItemUtil.getDecimalPartLength(unt) > 2) {
                    throw BusinessExceptionFactory.validationFailed(untField, MessageID.W036, index,
                        "単価", "2");
                }
                // 単価文字列長チェック
                if ((ItemUtil.getIntegerPartLength(unt) + ItemUtil.getDecimalPartLength(unt)) > 8) {
                    throw BusinessExceptionFactory.validationFailed(untField, MessageID.W035, index,
                        "単価", "8");
                }
            }

            // 金額に設定値がある場合のみチェック
            if (!StringUtils.isEmpty(meisaiBean.getKin2mei())) {
                /* 金額チェック */
                String kin = ItemUtil.removeComma(meisaiBean.getKin2mei());
                FormField kinField =
                    new FormField(String.format("meisaiBeanList[%d].kin2mei", index - 1), "金額");
                // 金額フォーマットチェック
                Pattern kinPattern =
                    Pattern.compile(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS_HYPHEN);
                Matcher kinMatcher = kinPattern.matcher(kin);
                if (!kinMatcher.matches()) {
                    throw BusinessExceptionFactory.validationFailed(kinField, MessageID.W039, index,
                        "金額", "数字とマイナス記号");
                }
                // 金額文字列長チェック
                if (ItemUtil.getIntegerPartLength(kin) > 8) {
                    throw BusinessExceptionFactory.validationFailed(kinField, MessageID.W035, index,
                        "金額", "8");
                }
            }
            // 備考
            // 文字列長チェック
            if (!StringUtils.isEmpty(meisaiBean.getMeisaibikomei())
                && !ItemUtil.isByteSizeValid(meisaiBean.getMeisaibikomei(), 30)) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField(String.format("meisaiBeanList[%d].meisaibikomei", index - 1),
                        "備考"),
                    MessageID.W035, index, "備考", "30");
            }
        }));
    }

    /**
     * 更新処理用Dto生成
     * 
     * @param form H2101W01Form
     * @param dbManagementSession DBManagementSession
     * @return dto H2101W01UpdateDto
     */
    private H2101W01UpdateDto createUpdateDto(H2101W01Form form,
        DbManagementSession dbManagementSession) {
        H2101W01UpdateDto dto = new H2101W01UpdateDto();
        // AP基本コード
        dto.setApkihoncd(form.getBaseBean().getApkihoncd());
        // ショップコード
        dto.setShopcd(form.getBaseBean().getShopcd());
        // 入出庫伝票番号
        if (!StringUtils.isEmpty(form.getBaseBean().getNyushukkodnpyno())) {
            dto.setNyushukkodnpyno(form.getBaseBean().getNyushukkodnpyno());
        }
        // 処理区分
        dto.setPrcskbn(form.getBaseBean().getPrcskbn());
        // 移動元倉庫コード
        if (!StringUtils.isEmpty(form.getMoveBean().getNyukosakisokocd())) {
            dto.setIdomotosokocd(form.getMoveBean().getNyukosakisokocd());
        }
        // 移動元実績担当者コード
        if (!StringUtils.isEmpty(form.getMoveBean().getJissekitnshacd1())) {
            dto.setIdomotojissekitnshacd(form.getMoveBean().getJissekitnshacd1());
        }
        // 移動先倉庫コード
        if (!StringUtils.isEmpty(form.getMoveBean().getShukkosakisokocd())) {
            dto.setIdosakisokocd(form.getMoveBean().getShukkosakisokocd());
        }
        // 移動先実績担当者コード
        if (!StringUtils.isEmpty(form.getMoveBean().getJissekitnshacd2())) {
            dto.setIdosakijissekitnshacd(form.getMoveBean().getJissekitnshacd2());
        }
        // 合計金額
        dto.setTotalkin(ItemUtil.removeComma(form.getCountBean().getSiirekin()));
        // 入出庫日
        dto.setNyushukkodd(DateTimeUtil.toNoSlashedDate(form.getSlipBean().getSiiredd()));
        // 伝票区分
        dto.setDnpykbn(form.getSlipBean().getDnpykbn());
        // 伝票備考
        dto.setDnpybiko(form.getSlipBean().getBiko());
        // 有効区分
        dto.setYukokbn(CommonConstants.YUKOKBN_1);
        // 担当者AP基本コード
        dto.setTnshaapkihoncd(dbManagementSession.getApkihoncd());
        // 担当者ショップコード
        dto.setTnshashopcd(dbManagementSession.getShopcd());
        // 担当者コード
        dto.setTnshacd(dbManagementSession.getTnshacd());
        // システム日付
        dto.setSystmymd(dbManagementSession.getSystemDate());
        // システム時間
        dto.setSystmjikan(dbManagementSession.getSystemTime());
        // 運用日付
        dto.setUnyoymd(dbManagementSession.getUnyoDate());
        // IPアドレス
        dto.setIpadrse(dbManagementSession.getIpAddress());
        // プログラムID
        dto.setProgramid(dbManagementSession.getProgramId());
        // 更新システム日付
        dto.setUpdateDateForLock(dbManagementSession.getUpdateDateForLock());
        // 更新システム時間
        dto.setUpdateTimeForLock(dbManagementSession.getUpdateTimeForLock());
        return dto;
    }

    /**
     * 更新処理用明細Dto生成
     * 
     * @param meisaiBeanList<H2101W01MeisaiBean>
     * @param dbManagementSession DBManagementSession
     * @return dtoList List<H2101W01UpdateMeisaiDto>
     */
    private List<H2101W01UpdateMeisaiDto> createUpdateMeisaiDto(
        List<H2101W01MeisaiBean> meisaiBeanList, DbManagementSession dbManagementSession) {

        List<H2101W01UpdateMeisaiDto> dtoList =
            meisaiBeanList.stream().map(util.withIndexFunction(1, (meisaiBean, meisaino) -> {
                H2101W01UpdateMeisaiDto dto = new H2101W01UpdateMeisaiDto();
                // 入出庫伝票明細番号
                dto.setNyushukkodnpymeisaino(meisaino);
                // 商品コード
                dto.setShncdmei(meisaiBean.getShncdmei());
                // 数量
                dto.setSuryomei(new BigDecimal(ItemUtil.removeComma(meisaiBean.getSuryo2mei())));
                // 単価
                if (!StringUtils.isEmpty(meisaiBean.getUnt2mei())) {
                    dto.setUntmei(new BigDecimal(ItemUtil.removeComma(meisaiBean.getUnt2mei())));
                }
                // 金額
                if (!StringUtils.isEmpty(meisaiBean.getKin2mei())) {
                    dto.setKinmei(Long.parseLong(ItemUtil.removeComma(meisaiBean.getKin2mei())));
                }
                // 型式
                if (!StringUtils.isEmpty(meisaiBean.getKatasikimei())) {
                    dto.setKatasikimei(meisaiBean.getKatasikimei());
                }
                // 明細備考
                if (!StringUtils.isEmpty(meisaiBean.getMeisaibikomei())) {
                    dto.setMeisaibikomei(meisaiBean.getMeisaibikomei());
                }
                // 明細行番号
                dto.setMeisaigyono(meisaino);
                return dto;
            })).collect(util.toList());

        return dtoList;
    }

    /**
     * 明細行変更処理
     * 
     * @param meisaiBean H2101W01MeisaiBean
     * @param index 変更する明細のインデックス
     * @return res AciRestResponse<Void>
     */
    @PutMapping("/changerow/{index}")
    public AciRestResponse<H2101W01CountBean> changeRow(@RequestBody H2101W01MeisaiBean meisaiBean,
        @PathVariable("index") int index,
        @RequestParam(name = "deleteindex", required = false) String deleteindex) {

        if (StringUtils.isEmpty(deleteindex)) {
            // セッションで保持する明細行の入力項目を変更する
            H2101W01MeisaiBean session = h2101w00Session.getMeisaiBeanList().get(index);
            // 型式
            session.setKatasikimei(meisaiBean.getKatasikimei());
            // 数量
            session.setSuryo2mei(meisaiBean.getSuryo2mei());
            // 総数
            session.setSokazu2mei(meisaiBean.getSokazu2mei());
            // 単価
            session.setUnt2mei(meisaiBean.getUnt2mei());
            // 金額
            session.setKin2mei(meisaiBean.getKin2mei());
            // 備考
            session.setMeisaibikomei(meisaiBean.getMeisaibikomei());
        }

        // 合計金額の変更
        long totalKin = h2101w00Session.getMeisaiBeanList().stream()
            // 金額を取得
            .map(H2101W01MeisaiBean::getKin2mei)
            // カンマを除去（空文字の場合は0が返る）
            .map(v -> ItemUtil.removeComma(v))
            // longに変換
            .mapToLong(v -> util.getLongValue(v))
            // サマリ
            .sum();
        // 更新結果の返却
        return new AciRestResponse<>(H2101W01CountBean.builder()
            // カンマを付与
            .siirekin(String.format("%,d", totalKin)).build());
    }

    /**
     * セッションクリア処理
     * 
     * @return res AciRestResponse<Void>
     */
    @PutMapping("/clearsession")
    public AciRestResponse<Void> clearSession() {
        // セッションで保持する明細行をクリアする
        h2101w00Session.setMoveBean(null);
        h2101w00Session.setCountBean(null);
        h2101w00Session.setSlipBean(null);
        h2101w00Session.setMeisaiBeanList(null);
        // 更新結果の返却
        return new AciRestResponse<>(null);
    }

    /**
     * セッション設定処理
     * 
     * @param resultDto H2101W01SearchResultDto
     */
    private void setSession(H2101W01SearchResultDto resultDto) {
        h2101w00Session.setMoveBean(getInitMoveBean(resultDto)); // 移動情報Bean
        h2101w00Session.setCountBean(getInitCountBean(resultDto)); // 合計情報Bean
        h2101w00Session.setSlipBean(getInitSlipBean(resultDto)); // 伝票情報Bean
        h2101w00Session.setMeisaiBeanList(getInitMeisaiListBean(resultDto.getMeisaiDtoList())); // 明細Beanリスト
    }
}
