package jp.co.aquaclara.accis.web.controller.c0815;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.ListUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.constants.SystemConstants;
import jp.co.aquaclara.accis.common.constants.ValidationConstants;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.controller.c0815.bean.C0815W11BaseBean;
import jp.co.aquaclara.accis.web.controller.c0815.bean.C0815W11MeisaiBean;
import jp.co.aquaclara.accis.web.controller.c0815.bean.C0815W21BaseBean;
import jp.co.aquaclara.accis.web.controller.c0815.bean.C0815W21FooterBean;
import jp.co.aquaclara.accis.web.controller.c0815.bean.C0815W21MeisaiSummaryBean;
import jp.co.aquaclara.accis.web.controller.c0815.form.C0815W11Form;
import jp.co.aquaclara.accis.web.controller.c0815.form.C0815W21Form;
import jp.co.aquaclara.accis.web.controller.c0815.session.C0815W00Session;
import jp.co.aquaclara.accis.web.controller.commons.AciRestResponse;
import jp.co.aquaclara.accis.web.controller.commons.DbManagementSession;
import jp.co.aquaclara.accis.web.domain.dto.c0815.C0815W11AcptDlvryMeisaiDto;
import jp.co.aquaclara.accis.web.domain.dto.c0815.C0815W21AddRowDto;
import jp.co.aquaclara.accis.web.domain.dto.c0815.C0815W21MeisaiUpdateDto;
import jp.co.aquaclara.accis.web.domain.dto.c0815.C0815W21UpdateDto;
import jp.co.aquaclara.accis.web.domain.service.c0800.UtilityService;
import jp.co.aquaclara.accis.web.domain.service.c0815.C0815W21Service;
import jp.co.aquaclara.accis.web.domain.service.master.S02M006TService;
import jp.co.aquaclara.accis.web.domain.service.x9901.StringUtil;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import jp.co.aquaclara.accis.web.exception.dto.FormField;
import lombok.extern.slf4j.Slf4j;

/**
 * C0815W21_配達実績登録（個別入力）APIコントローラクラス.
 */
@RestController
@RequestMapping("/api/c0815w21")
@Slf4j
public class C0815W21APIController {

    private static final String ZERO_STRING = BigDecimal.ZERO.stripTrailingZeros().toPlainString();

    @Autowired
    private C0815W00Session c0815W00Session;

    @Autowired
    private C0815W21Service c0815w21Service;

    @Autowired
    private S02M006TService s02m006tService;

    @Autowired
    private C0815W11Form c0815w11Form;

    @Autowired
    private UtilityService util;

    private final String MOCHIKAERI_TNSHA_NM = "持ち帰り";

    /**
     * 更新前チェック処理
     * 
     * @param form C0815W21Form
     * @param dbManagementSession DbManagementSession
     * @return AciRestResponse<C0815W21Form>
     */
    @PutMapping("check")
    public List<String> preUpdateCheck(@RequestBody @Validated C0815W21Form form,
        @ModelAttribute(name = "dbManagementSession_C0815W21",
            binding = true) DbManagementSession dbManagementSession) {

        int selfposition = Integer.valueOf(form.getBaseBean().getSelfposition());
        if (hasSessionMeisaiData(selfposition)) {
            // 受注配達明細DTOリストに対して画面入力内容を反映
            C0815W11MeisaiBean acptdnpyBean = c0815W00Session.getMeisaiBeanList().get(selfposition);
            List<C0815W11AcptDlvryMeisaiDto> detailList =
                createAcptDlvryMeisaiDtoList(form, selfposition);
            // 受注配達明細DTOリストに含まれる配布物明細のうち、画面でチェックがONのものだけにフィルタする
            List<C0815W11AcptDlvryMeisaiDto> inputShnDetailDtoList = detailList.stream()
                // 配布物は除外する
                .filter(detailDto -> !CommonConstants.KBN_MEISAI_HAIHUBUTSU
                    .equals(detailDto.getMeisaikbn()))
                .collect(util.toList());
            List<C0815W11AcptDlvryMeisaiDto> haifubutsuDetailDtoList = detailList.stream()
                // 配布物のみにする
                .filter(detailDto -> CommonConstants.KBN_MEISAI_HAIHUBUTSU
                    .equals(detailDto.getMeisaikbn()))
                // チェックがONのものだけにする
                .filter(detailDto -> util.isFlagged(detailDto.getHaifuflg()))
                .collect(util.toList());
            detailList = util.mergeList(inputShnDetailDtoList, haifubutsuDetailDtoList);
            dump(detailList);
            // 商品入力行の明細DTOと配布物のDTOをマージする
            acptdnpyBean.setAcptDlvryMeisaiList(detailList);
            // 受注データを更新
            c0815W00Session.getMeisaiBeanList().set(selfposition, acptdnpyBean);
        }

        // 相関チェック
        List<String> warningMessageList = new ArrayList<>();
        updateCheckParam(selfposition, form, warningMessageList);
        C0815W21UpdateDto dto = createUpdateDto(form);
        c0815w21Service.updateCheckParam(dto, form, warningMessageList);

        return warningMessageList;
    }

    /**
     * 更新処理
     * 
     * @param form フォーム
     * @param dbManagementSession DB管理セッション情報
     * @return レスポンス
     */
    @PutMapping("update")
    public AciRestResponse<C0815W21Form> update(@RequestBody @Validated C0815W21Form form,
        @ModelAttribute(name = "dbManagementSession_C0815W21",
            binding = true) DbManagementSession dbManagementSession) {
        // 配達実績明細に更新結果を格納
        int selfposition = Integer.valueOf(form.getBaseBean().getSelfposition());
        if (hasSessionMeisaiData(selfposition)) {
            C0815W11MeisaiBean acptdnpyBeanInSession =
                c0815W00Session.getMeisaiBeanList().get(selfposition);
            // 数量が0となっている追加された明細を削除し、明細番号を再設定
            acptdnpyBeanInSession.setAcptDlvryMeisaiList(
                removeZeroDetail(acptdnpyBeanInSession.getAcptDlvryMeisaiList()));
            // 受注データ更新
            acptdnpyBeanInSession.setKshntrgtflg(CommonConstants.FLG_ON); // 更新対象フラグをON
            acptdnpyBeanInSession.setShuseiarinashiflg(CommonConstants.FLG_ON); // 修正有無フラグ
            acptdnpyBeanInSession.setDispshuseiarinashiflg(CommonConstants.FLG_ON); // 表示中修正有無フラグ
            // 画面の内容からセッションに反映するべき項目を更新する
            C0815W21BaseBean baseBean = form.getBaseBean();
            if (!ObjectUtils.isEmpty(baseBean)) {
                // 配達日
                acptdnpyBeanInSession.setDlvryddmei(baseBean.getDlvrydd());
                // 配達担当者コード
                acptdnpyBeanInSession.setDlvrytnshameimei(baseBean.getDlvrytnshacd());
                // 配達担当者名
                acptdnpyBeanInSession.setDlvrytnshanmmei(baseBean.getTnshamei());
                // 配達会社コードの選択内容に応じて配達区分を設定
                if (CommonConstants.CD_DLVRYCMP_MOCHIKAERI.equals(baseBean.getDlvrycmpnynm())) {
                    acptdnpyBeanInSession.setDlvrykbn(CommonConstants.KBN_DLVRY_MOCHIKAERI);
                    // 持ち帰りの場合は一括画面の配達担当者コードを持ち帰りにする
                    acptdnpyBeanInSession
                        .setDlvrytnshameimei(CommonConstants.CD_DLVRYCMP_MOCHIKAERI);
                    acptdnpyBeanInSession.setDlvrytnshanmmei(MOCHIKAERI_TNSHA_NM);
                } else if (CommonConstants.CD_DLVRYCMP_JISHA.equals(baseBean.getDlvrycmpnynm())) {
                    acptdnpyBeanInSession.setDlvrykbn(CommonConstants.KBN_DLVRY_JISHA);
                } else {
                    acptdnpyBeanInSession.setDlvrykbn(CommonConstants.KBN_DLVRY_TASHA);
                }
                // 配達会社コード
                acptdnpyBeanInSession.setDlvrycmpnycd(baseBean.getDlvrycmpnynm());
                // 自社の場合は配達会社コードをクリアする
                if (CommonConstants.CD_DLVRYCMP_JISHA.equals(baseBean.getDlvrycmpnynm())) {
                    acptdnpyBeanInSession.setDlvrycmpnycd(SystemConstants.WORD_BLANK);
                }
                // 不在置き実績フラグ
                acptdnpyBeanInSession.setFzaiokijissekiflgmei(baseBean.getFzaiokijissekiflg());
            }
            if (!ObjectUtils.isEmpty(form.getFooterBean())) {
                // 回収ボトル
                acptdnpyBeanInSession.setKaishun1n2rittorukaishu(
                    ItemUtil.removeComma(form.getFooterBean().getKaishun1n2rittorukaishu()));
                acptdnpyBeanInSession.setKaishun7rittorukaishu(
                    ItemUtil.removeComma(form.getFooterBean().getKaishun8rittorukaishu()));
            }
        }

        // 更新結果の返却
        C0815W11BaseBean c0815w11BaseBean = new C0815W11BaseBean();
        c0815w11BaseBean.setUpdatedflag(CommonConstants.FLG_ON);
        c0815w11Form.setBaseBean(c0815w11BaseBean);
        AciRestResponse<C0815W21Form> res = new AciRestResponse<>(form);
        return res;
    }

    /**
     * 戻るボタン押下時処理
     * 
     * @param form C0815W21Form
     * @return AciRestResponse<C0815W21Form>
     */
    @PutMapping("back")
    public AciRestResponse<C0815W21Form> update(@RequestBody C0815W21Form form) {
        // 更新結果の返却
        C0815W11BaseBean c0815w11BaseBean = new C0815W11BaseBean();
        c0815w11BaseBean.setUpdatedflag(CommonConstants.FLG_ON);
        c0815w11Form.setBaseBean(c0815w11BaseBean);
        AciRestResponse<C0815W21Form> res = new AciRestResponse<>(form);
        return res;
    }

    /**
     * 明細情報取得処理
     * 
     * @return List<C0815W21MeisaiSummaryBean>
     */
    @GetMapping("meisai")
    public List<C0815W21MeisaiSummaryBean> meisai(
        @ModelAttribute("c0815W21Form") C0815W21Form form) {

        C0815W21BaseBean baseBean = form.getBaseBean();
        int selfposition = Integer.parseInt(baseBean.getSelfposition());
        List<C0815W21MeisaiSummaryBean> meisaiBeanList = new ArrayList<>();
        // セッションから明細データを作成
        if (hasSessionMeisaiData(selfposition)) {
            meisaiBeanList = crateMeisaiSummaryBeanList(
                c0815W00Session.getMeisaiBeanList().get(selfposition).getAcptDlvryMeisaiList());
        }

        return meisaiBeanList;
    }

    /**
     * 行追加時入力チェック
     * 
     * @param form C0815W21Form
     */
    @PutMapping("addrowCheckParam")
    public AciRestResponse<C0815W21Form> addgyoCheckParam(@RequestBody C0815W21Form form) {
        // 伝票番号に「※修正不可」が表示されている場合エラー
        if (CommonConstants.FLG_ON.equals(form.getBaseBean().getHdnshuseihuka())) {
            FormField field = new FormField("");
            throw BusinessExceptionFactory.validationFailed(field, MessageID.W260,
                "修正不可配達情報の為、行追加できません。");
        }
        String dlvrydd = form.getBaseBean().getDlvrydd();
        if (StringUtils.isEmpty(dlvrydd)) {
            FormField formField = new FormField("baseBean.dlvrydd");
            throw BusinessExceptionFactory.validationFailed(formField, MessageID.W011, "配達日");
        } else {
            if (!dlvrydd.matches(ValidationConstants.REGEXP_YYYY_MM_DD_SLASH)) {
                FormField formField = new FormField("baseBean.dlvrydd");
                throw BusinessExceptionFactory.validationFailed(formField, MessageID.W016, "配達日");
            }
        }

        return new AciRestResponse<C0815W21Form>(form);
    }

    /**
     * 行追加処理
     * 
     * @param form
     */
    @PutMapping("addrow")
    public AciRestResponse<C0815W21Form> addgyo(@RequestBody C0815W21Form form) {
        C0815W21BaseBean baseBean = form.getBaseBean();
        int selfposition = Integer.parseInt(baseBean.getSelfposition());
        // 行追加処理
        if (CommonConstants.FLG_ON.equals(baseBean.getAddgyoflag())) {
            // 行追加前にの画面入力データをセッションに反映
            if (hasSessionMeisaiData(selfposition)) {
                List<C0815W11AcptDlvryMeisaiDto> detailList =
                    createAcptDlvryMeisaiDtoList(form, selfposition);
                dump(detailList);
                c0815W00Session.getMeisaiBeanList().get(selfposition)
                    .setAcptDlvryMeisaiList(detailList);
            }
            C0815W21AddRowDto dto = new C0815W21AddRowDto();
            dto.setApkihoncd(baseBean.getApkihoncd1());
            dto.setShopcd(baseBean.getShopcd());
            dto.setTokuisakicd(form.getKttcd());
            dto.setDlvrydd(baseBean.getDlvrydd());
            dto.setShncd(form.getMeisaiHeaderBean().getShncdadd());
            dto.setEdano(baseBean.getEdano());
            dto.setTokuisakikbn(baseBean.getTokuisakikbn());
            dto.setAcptkbn(form.getMeisaiHeaderBean().getHdnacptkbn());
            dto.setDlvryytidd(baseBean.getDlvryytidd());
            dto.setSelfposition(selfposition);
            // ここで追加した行の内容がセッションに反映される
            addRow(dto, form);

        }

        return new AciRestResponse<>(form);
    }

    /**
     * 行追加処理
     * 
     * @param dto 行追加する受注配達明細DTO
     * @param form フォーム
     */
    private void addRow(C0815W21AddRowDto dto, C0815W21Form form) {
        int selfposition = dto.getSelfposition();
        if (hasSessionMeisaiData(selfposition)) {
            // 追加行の作成
            C0815W11AcptDlvryMeisaiDto meisaiDto = c0815w21Service.getAddRowData(dto, form);
            // 受注伝票から受注配達明細に設定
            C0815W11MeisaiBean acptdnpyBean = c0815W00Session.getMeisaiBeanList().get(selfposition);
            List<C0815W11AcptDlvryMeisaiDto> detailDtoList = acptdnpyBean.getAcptDlvryMeisaiList();
            meisaiDto.setAcptdnpyno(acptdnpyBean.getDnpynomei());
            short edano =
                Short.parseShort(Objects.requireNonNullElse(acptdnpyBean.getEdano(), ZERO_STRING));
            meisaiDto.setEdano(edano);
            meisaiDto.setAcptdnpymeisaino(Objects.toString(detailDtoList.size() + 1));
            meisaiDto.setAcptdnpymeisaiedano(ZERO_STRING);
            log.debug("追加するDTO : {}", meisaiDto);
            // セッションで保持する明細行に追加
            detailDtoList.add(meisaiDto);
            dump(detailDtoList, "行追加後の受注配達明細DTOリスト");
        }

    }

    /**
     * C0815W11AcptDlvryMeisaiDtoリスト作成処理.<br>
     * <br>
     * 画面の内容を反映した、受注配達明細DTOリストを生成する
     * 
     * @param form フォーム
     * @param selfposition 一括入力画面での伝票リストの行番号
     * @return 受注配達明細DTOリスト
     */
    private List<C0815W11AcptDlvryMeisaiDto> createAcptDlvryMeisaiDtoList(C0815W21Form form,
        int selfposition) {
        // セッション内に受注伝票の情報が存在しない場合は空のリストを返却
        if (!hasSessionMeisaiData(selfposition)) {
            log.debug("\tセッション情報に指定された受注伝票が存在しない（INDEX : {}）", selfposition);
            log.debug("END - createAcptDlvryMeisaiDtoList");
            return new ArrayList<>();
        }

        // 画面の内容
        C0815W21FooterBean footerBean = form.getFooterBean();
        List<C0815W21MeisaiSummaryBean> detailBeanList = form.getMeisaiSummaryBeanList();

        // セッションで保持している受注伝票および受注配達明細DTOリスト
        C0815W11MeisaiBean acptMeisaiBean = c0815W00Session.getMeisaiBeanList().get(selfposition);
        List<C0815W11AcptDlvryMeisaiDto> detailDtoList = acptMeisaiBean.getAcptDlvryMeisaiList();

        // 商品入力行のみ受注配達明細DTOリスト
        List<C0815W11AcptDlvryMeisaiDto> inputShnDetailDtoList = detailDtoList.stream()
            // 配布物は除外する
            .filter(detailDto -> !CommonConstants.KBN_MEISAI_HAIHUBUTSU
                .equals(detailDto.getMeisaikbn()))
            .collect(util.toList());

        // セッションで保持している商品入力行に対して画面の内容を反映する
        inputShnDetailDtoList.forEach(util.withIndexConsumer(0, (detailDto, index) -> {
            // 画面入力内容
            // ※配達実績登録（個別入力）では、行追加（リストの最後に追加）のみ画面で行えるので「配布物以外の明細行」はセッションと画面で並び順が一致している
            C0815W21MeisaiSummaryBean detailBean = detailBeanList.get(index);
            // 受注配達明細DTOに画面入力内容を反映
            detailDto.setSuryo(detailBean.getSuryomei());
            detailDto.setKatasiki(detailBean.getKatasiki());
            detailDto.setKeijokbn(detailBean.getKeijokbn());
            detailDto.setKeijokbn2(detailBean.getKeijokbn2());
            detailDto.setKeijokbnnm(detailBean.getKeijokbnnmmei());
            detailDto.setNohinsuryo(ItemUtil.removeComma(detailBean.getSuryomei()));
            detailDto.setAcptsuryo(ItemUtil.removeComma(detailBean.getSuryomei()));
            detailDto.setSuryo(ItemUtil.removeComma(detailBean.getSuryomei()));
            detailDto.setSokazu(ItemUtil.removeComma(detailBean.getSokazumei()));
            detailDto.setHyojunprice(ItemUtil.removeComma(detailBean.getHyojunpricemei()));
            detailDto.setNohinunt(ItemUtil.removeComma(detailBean.getHanbaipricemei()));
            detailDto.setAcptunt(ItemUtil.removeComma(detailBean.getHanbaipricemei()));
            detailDto.setHanbaiprice(ItemUtil.removeComma(detailBean.getHanbaipricemei()));
            detailDto.setKin(ItemUtil.removeComma(detailBean.getKinmei()));
            detailDto.setShohitaxkin(
                ItemUtil.removeComma(ItemUtil.removeComma(detailBean.getShohitaxkin())));
            detailDto.setMeisaibiko(detailBean.getMeisaibikomei());
            detailDto.setAddgyoflg(detailBean.getAddgyoflg());
            detailDto.setMeisaiposition(String.valueOf(index));
        }));

        // 配布物設定（IntStream#range…開始番号は含む、終了番号は含まないなので1～4を処理する）
        List<C0815W11AcptDlvryMeisaiDto> haifubutsuDetailDtoList = IntStream.range(1, 5)
            // 配布物情報を設定し受注配達明細DTOに変換
            .mapToObj(index -> setHaifubutsuInfo(acptMeisaiBean, footerBean, index))
            // 空の場合は除外（配布物コードが未設定の場合に空になる）
            .filter(Optional::isPresent)
            // 受注配達明細DTOを取り出し、リストにする
            .map(opt -> opt.get()).collect(util.toList());

        // 商品入力行と配布物をマージ
        List<C0815W11AcptDlvryMeisaiDto> result =
            util.mergeList(inputShnDetailDtoList, haifubutsuDetailDtoList);

        // 受注伝票明細番号と受注伝票明細枝番号の付与
        result.forEach(util.withIndexConsumer(1, (detailDto, index) -> {
            // 1から順番に明細番号を付与
            detailDto.setAcptdnpymeisaino(Objects.toString(index));
            // 商品入力行と配布物の明細は明細枝番号は0
            detailDto.setAcptdnpymeisaiedano(ZERO_STRING);
        }));
        return result;
    }

    /**
     * 配布物の内容を受注データに反映し、受注配達明細DTOを生成する
     * 
     * @param acptdnpyBean 受注データ
     * @param footerBean フッター情報
     * @param index インデックス（1～4）
     * @return 受注配達明細DTO（配布物コードが未設定の場合はOptional.empty()）
     */
    private Optional<C0815W11AcptDlvryMeisaiDto> setHaifubutsuInfo(C0815W11MeisaiBean acptdnpyBean,
        C0815W21FooterBean footerBean, int index) {
        C0815W11AcptDlvryMeisaiDto haifubutsudto = new C0815W11AcptDlvryMeisaiDto();
        // 何番目の配布物を処理するかで分岐
        switch (index) {
            case 1:
                acptdnpyBean.setHaifuflg1(footerBean.getHaifuflg1());
                acptdnpyBean.setHaifubutsucd1(footerBean.getHaifubutsucd1());
                acptdnpyBean.setHaifubutsunm1(footerBean.getHaifubutsunm1());
                acptdnpyBean.setHaifubutsuapkihoncd1(footerBean.getHaifubutsuapkihoncd1());
                acptdnpyBean.setHaifubutsushopcd1(footerBean.getHaifubutsushopcd1());
                haifubutsudto.setHaifuflg(footerBean.getHaifuflg1());
                haifubutsudto.setHaifubutsuapkihoncd(footerBean.getHaifubutsuapkihoncd1());
                haifubutsudto.setHaifubutsushopcd(footerBean.getHaifubutsushopcd1());
                haifubutsudto.setHaifubutsucd(footerBean.getHaifubutsucd1());
                haifubutsudto.setHaifubutsunm(footerBean.getHaifubutsunm1());
                break;
            case 2:
                acptdnpyBean.setHaifuflg2(footerBean.getHaifuflg2());
                acptdnpyBean.setHaifubutsucd2(footerBean.getHaifubutsucd2());
                acptdnpyBean.setHaifubutsunm2(footerBean.getHaifubutsunm2());
                acptdnpyBean.setHaifubutsuapkihoncd2(footerBean.getHaifubutsuapkihoncd2());
                acptdnpyBean.setHaifubutsushopcd2(footerBean.getHaifubutsushopcd2());
                haifubutsudto.setHaifuflg(footerBean.getHaifuflg2());
                haifubutsudto.setHaifubutsuapkihoncd(footerBean.getHaifubutsuapkihoncd2());
                haifubutsudto.setHaifubutsushopcd(footerBean.getHaifubutsushopcd2());
                haifubutsudto.setHaifubutsucd(footerBean.getHaifubutsucd2());
                haifubutsudto.setHaifubutsunm(footerBean.getHaifubutsunm2());
                break;
            case 3:
                acptdnpyBean.setHaifuflg3(footerBean.getHaifuflg3());
                acptdnpyBean.setHaifubutsucd3(footerBean.getHaifubutsucd3());
                acptdnpyBean.setHaifubutsunm3(footerBean.getHaifubutsunm3());
                acptdnpyBean.setHaifubutsuapkihoncd3(footerBean.getHaifubutsuapkihoncd3());
                acptdnpyBean.setHaifubutsushopcd3(footerBean.getHaifubutsushopcd3());
                haifubutsudto.setHaifuflg(footerBean.getHaifuflg3());
                haifubutsudto.setHaifubutsuapkihoncd(footerBean.getHaifubutsuapkihoncd3());
                haifubutsudto.setHaifubutsushopcd(footerBean.getHaifubutsushopcd3());
                haifubutsudto.setHaifubutsucd(footerBean.getHaifubutsucd3());
                haifubutsudto.setHaifubutsunm(footerBean.getHaifubutsunm3());
                break;
            case 4:
                acptdnpyBean.setHaifuflg4(footerBean.getHaifuflg4());
                acptdnpyBean.setHaifubutsucd4(footerBean.getHaifubutsucd4());
                acptdnpyBean.setHaifubutsunm4(footerBean.getHaifubutsunm4());
                acptdnpyBean.setHaifubutsuapkihoncd4(footerBean.getHaifubutsuapkihoncd4());
                acptdnpyBean.setHaifubutsushopcd4(footerBean.getHaifubutsushopcd4());
                haifubutsudto.setHaifuflg(footerBean.getHaifuflg4());
                haifubutsudto.setHaifubutsuapkihoncd(footerBean.getHaifubutsuapkihoncd4());
                haifubutsudto.setHaifubutsushopcd(footerBean.getHaifubutsushopcd4());
                haifubutsudto.setHaifubutsucd(footerBean.getHaifubutsucd4());
                haifubutsudto.setHaifubutsunm(footerBean.getHaifubutsunm4());
        }
        if (StringUtils.isEmpty(haifubutsudto.getHaifubutsucd())) {
            // 配布物コードが未設定の場合は空を返す
            return Optional.empty();
        }
        // 伝票レベルまたは配布物明細共通の項目編集
        haifubutsudto.setApkihoncd(acptdnpyBean.getApkihoncdmei());
        haifubutsudto.setShopcd(acptdnpyBean.getShopcdmei());
        haifubutsudto.setAcptdnpyno(acptdnpyBean.getDnpynomei());
        haifubutsudto.setEdano(
            Short.valueOf(Objects.requireNonNullElse(acptdnpyBean.getEdano(), ZERO_STRING)));
        haifubutsudto.setMeisaikbn(CommonConstants.KBN_MEISAI_HAIHUBUTSU);
        haifubutsudto.setTanpinmeisaiflg(CommonConstants.FLG_ON);
        haifubutsudto.setNyuryokumeisaiflg(CommonConstants.FLG_ON);
        haifubutsudto.setAcptsuryo(BigDecimal.ONE.stripTrailingZeros().toPlainString());
        if (CommonConstants.DNPYSTS_12.equals(acptdnpyBean.getDnpysts())) {
            haifubutsudto.setNohinsuryo(haifubutsudto.getAcptsuryo());
        }
        haifubutsudto.setDatsksmotokbn(CommonConstants.KBN_SAKUSEIMOTO_HAITATSUJISSEKI);
        haifubutsudto.setYusyokbn(CommonConstants.KBN_YUSHO_NONE);
        haifubutsudto.setYukokbn(CommonConstants.FLG_ON);
        return Optional.of(haifubutsudto);
    }

    /**
     * 受注配達明細DTOリストの中身をデバッグログに出力する
     * 
     * @param detailDtoList 受注配達明細DTOリスト
     */
    private void dump(List<C0815W11AcptDlvryMeisaiDto> detailDtoList) {
        dump(detailDtoList, null);
    }

    /**
     * 受注配達明細DTOリストの中身をデバッグログに出力する
     * 
     * @param detailDtoList 受注配達明細DTOリスト
     * @param message タイトルとして出力するメッセージ
     */
    private void dump(List<C0815W11AcptDlvryMeisaiDto> detailDtoList, String message) {
        List<C0815W11AcptDlvryMeisaiDto> source =
            Objects.requireNonNullElse(detailDtoList, List.of());
        String title = Objects.requireNonNullElse(message, "受注配達明細DTOリスト");

        log.debug("=== {} ===", title);
        log.debug("受注配達明細リスト件数 : {}件", source.size());
        source.forEach(util.withIndexConsumer(1, (detailDto, index) -> {
            log.debug("\t--- {}件目の受注配達明細DTO ---", index);
            log.debug("\t受注伝票番号 : {}-{}-{}-{}", detailDto.getApkihoncd(), detailDto.getShopcd(),
                detailDto.getAcptdnpyno(), detailDto.getEdano());
            log.debug("\t受注伝票明細番号 : {}-{}", detailDto.getAcptdnpymeisaino(),
                detailDto.getAcptdnpymeisaiedano());
            log.debug("\t明細区分: {} / 単品明細フラグ : {} / 計上区分 : {}", detailDto.getMeisaikbn(),
                detailDto.getTanpinmeisaiflg(), detailDto.getKeijokbn());
            if (CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(detailDto.getMeisaikbn())) {
                log.debug("\t配布物コード : {} / 配布物名 : {}", detailDto.getHaifubutsucd(),
                    detailDto.getHaifubutsunm());
            } else {
                log.debug("\t行番号 : {}", detailDto.getMeisaiposition());
                log.debug("\t行追加フラグ : {}", detailDto.getAddgyoflg());
                log.debug("\t商品コード : {} / 商品名略称 : {}", detailDto.getShncd(),
                    detailDto.getShnnmryakusho());
                log.debug("\t数量 : {}", detailDto.getSuryo());
                log.debug("\t標準価格 : {}円 / 販売価格 : {}円", detailDto.getHyojunprice(),
                    detailDto.getHanbaiprice());
                log.debug("\t標準単価 : {}円 / 受注単価 : {}円", detailDto.getHyojununt(),
                    detailDto.getAcptunt());
                log.debug("\t受注金額 : {}円 / 受注消費税金額 : {}円", detailDto.getAcptkin(),
                    detailDto.getAcptshohitaxkin());
                log.debug("\t明細備考 : {}", detailDto.getMeisaibiko());
            }
            log.debug("\t------");
        }));
        log.debug("==============================");
    }

    /**
     * 指定された受注配達明細DTOから行追加かつ数量が0のデータを削除し、受注伝票明細番号と受注伝票明細枝番号を再設定する
     * 
     * @param source 元となる受注配達明細DTOリスト
     * @return 受注配達明細DTOリスト
     */
    private List<C0815W11AcptDlvryMeisaiDto> removeZeroDetail(
        List<C0815W11AcptDlvryMeisaiDto> source) {
        log.debug("START - removeZeroDetail");
        dump(source, "処理前の受注配達明細DTOリスト");
        List<C0815W11AcptDlvryMeisaiDto> inputShnDetailDtoList = source.stream()
            // 配布物を除外（配布物コードが未設定のデータのみにフィルタ）
            .filter(detailDto -> StringUtils.isEmpty(detailDto.getHaifubutsucd()))
            // 追加行かつ数量が0の明細を除外（追加行ではないまたは数量が0ではない）
            .filter(detailDto -> !util.isFlagged(detailDto.getAddgyoflg())
                || !Objects.equals(ZERO_STRING, detailDto.getAcptsuryo()))
            // リストを生成
            .collect(util.toList());
        dump(inputShnDetailDtoList, "数量が0の追加行を削除した受注配達明細DTOリスト（配布物以外）");
        List<C0815W11AcptDlvryMeisaiDto> haifubutsuDetailDtoList = source.stream()
            // 配布物コードが設定されているもののみにフィルタ
            .filter(detailDto -> !StringUtils.isEmpty(detailDto.getHaifubutsucd()))
            // リストを生成
            .collect(util.toList());
        // リストをマージ
        List<C0815W11AcptDlvryMeisaiDto> result =
            util.mergeList(inputShnDetailDtoList, haifubutsuDetailDtoList);
        // 受注伝票明細番号と受注伝票明細枝番号の付与
        result.forEach(util.withIndexConsumer(1, (detailDto, index) -> {
            // 1から順番に明細番号を付与
            detailDto.setAcptdnpymeisaino(Objects.toString(index));
            // 商品入力行と配布物の明細は明細枝番号は0
            detailDto.setAcptdnpymeisaiedano(ZERO_STRING);
        }));
        dump(result, "明細番号等を再設定した受注配達明細DTOリスト");
        log.debug("END - removeZeroDetail");
        return result;
    }

    /**
     * 相関、単項目チェック
     * 
     * @param selfposition セッション内の受注データの何番目か
     * @param form フォーム
     * @param warningMessageList ワーニングメッセージリスト
     */
    private void updateCheckParam(int selfposition, C0815W21Form form,
        List<String> warningMessageList) {
        // 取消伝票かを判定するためのフラグ
        boolean torikeshidnpyoflg = true;
        // 単項目チェック
        C0815W21BaseBean baseBean = form.getBaseBean();
        C0815W21FooterBean footerBean = form.getFooterBean();
        if (!ObjectUtils.isEmpty(footerBean)) {
            String kaishun1n2rittorukaishu =
                ItemUtil.removeComma(footerBean.getKaishun1n2rittorukaishu());
            String kaishun8rittoruazuk =
                ItemUtil.removeComma(footerBean.getKaishun8rittorukaishu());
            // 回収１２Ｌ回収が数字とマイナス記号と小数点以外の場合エラー
            if (!kaishun1n2rittorukaishu
                .matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS_DECIMAL)) {
                FormField field = new FormField("footerBean.kaishun1n2rittorukaishu", "回収１２Ｌ回収");
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W013, "回収１２Ｌ回収",
                    "数字とマイナス記号と小数点");
            }
            // 回収８Ⅼ回収が数字とマイナス記号と小数点以外の場合エラー
            if (!kaishun8rittoruazuk
                .matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS_DECIMAL)) {
                FormField field = new FormField("footerBean.kaishun8rittorukaishu", "回収７L回収");
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W013, "回収７L回収",
                    "数字とマイナス記号と小数点");
            }
        }
        // 相関チェック
        if (!CommonConstants.CD_DLVRYCMP_MOCHIKAERI.equals(baseBean.getDlvrycmpnynm())) {
            // 配達会社が持ち帰り以外で配達担当者コードが未入力の場合エラー
            if (StringUtils.isEmpty(baseBean.getDlvrytnshacd())) {
                FormField field = new FormField("baseBean.dlvrytnshacd", "配達担当者");
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W011, "配達担当者");
            }
        }
        // 配布物コード１～４の中で同一の配布物にチェックがついている場合エラーを表示
        String haifubutsucd1 = SystemConstants.WORD_BLANK;
        String haifubutsucd2 = SystemConstants.WORD_BLANK;
        String haifubutsucd3 = SystemConstants.WORD_BLANK;
        String haifubutsucd4 = SystemConstants.WORD_BLANK;
        if (CommonConstants.FLG_ON.equals(footerBean.getHaifuflg1())) {
            haifubutsucd1 = footerBean.getHaifubutsucd1();
        }
        if (CommonConstants.FLG_ON.equals(footerBean.getHaifuflg2())) {
            haifubutsucd2 = footerBean.getHaifubutsucd2();
            if (!StringUtils.isEmpty(haifubutsucd2) && (haifubutsucd2.equals(haifubutsucd1)
                || haifubutsucd2.equals(haifubutsucd3) || haifubutsucd2.equals(haifubutsucd4))) {
                FormField field = new FormField("footerBean.haifubutsucd2", "配布物2");
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W103, "配布物");
            }
        }
        if (CommonConstants.FLG_ON.equals(footerBean.getHaifuflg3())) {
            haifubutsucd3 = footerBean.getHaifubutsucd3();
            if (!StringUtils.isEmpty(haifubutsucd3) && (haifubutsucd3.equals(haifubutsucd1)
                || haifubutsucd3.equals(haifubutsucd2) || haifubutsucd3.equals(haifubutsucd4))) {
                FormField field = new FormField("footerBean.haifubutsucd3", "配布物3");
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W103, "配布物");
            }
        }
        if (CommonConstants.FLG_ON.equals(footerBean.getHaifuflg4())) {
            haifubutsucd4 = footerBean.getHaifubutsucd4();
            if (!StringUtils.isEmpty(haifubutsucd4) && (haifubutsucd4.equals(haifubutsucd1)
                || haifubutsucd4.equals(haifubutsucd2) || haifubutsucd4.equals(haifubutsucd3))) {
                FormField field = new FormField("footerBean.haifubutsucd4", "配布物4");
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W103, "配布物");
            }
        }
        if (!StringUtils.isEmpty(baseBean.getDlvrydd())) {
            // 配達日が空でない場合
            if (CommonConstants.DATE_MIN.compareTo(baseBean.getDlvrydd()) >= 0
                || CommonConstants.DATE_MAX.compareTo(baseBean.getDlvrydd()) <= 0) {
                // 日付が1900/01/01～9999/12/31の範囲外の場合、エラー
                FormField field = new FormField("baseBean.dlvrydd", "配達日");
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W033, "配達日",
                    CommonConstants.DATE_MIN, CommonConstants.DATE_MAX);
            }

        }
        // 明細行のチェック
        List<C0815W11AcptDlvryMeisaiDto> detailDtoList =
            c0815W00Session.getMeisaiBeanList().get(selfposition).getAcptDlvryMeisaiList();
        for (C0815W11AcptDlvryMeisaiDto detailDto : detailDtoList) {
            // 配布物の場合、チェックしない
            if (CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(detailDto.getMeisaikbn())) {
                continue;
            }
            // 行追加により追加した明細かつ数量が0の場合はチェックしない
            if (util.isFlagged(detailDto.getAddgyoflg())
                && Objects.equals(ZERO_STRING, detailDto.getSuryo())) {
                continue;
            }
            int index = Integer.parseInt(detailDto.getMeisaiposition());
            // 数量が空欄の場合エラー
            if (StringUtils.isEmpty(detailDto.getSuryo())) {
                FormField field =
                    new FormField(String.format("meisaiSummaryBeanList[%d].suryomei", index), "数量");
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W037, index + 1,
                    "数量");
            }
            // 数量が数字とマイナス記号と小数点以外の場合エラー
            if (!detailDto.getSuryo()
                .matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS_DECIMAL)) {
                FormField field =
                    new FormField(String.format("meisaiSummaryBeanList[%d].suryomei", index), "数量");
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W039, index + 1,
                    "数量", "数字とマイナス記号と小数点");
            }
            // 受注データに数量が0以外の明細が含まれている場合は取消伝票ではないとする
            if (torikeshidnpyoflg && !"0".equals(detailDto.getSuryo())) {
                torikeshidnpyoflg = false;
            }
            // 販売価格が空欄の場合、エラー
            if (StringUtils.isEmpty(detailDto.getHanbaiprice())) {
                FormField field = new FormField(
                    String.format("meisaiSummaryBeanList[%d].hanbaiprice", index), "販売価格");
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W037, index + 1,
                    "販売価格");
            }
            // 販売価格が数字とマイナス記号と小数点以外の場合エラー
            if (!ItemUtil.removeComma(detailDto.getHanbaiprice())
                .matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS_DECIMAL)) {
                FormField field = new FormField(
                    String.format("meisaiSummaryBeanList[%d].hanbaiprice", index), "販売価格");
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W039, index + 1,
                    "販売価格", "数字とマイナス記号と小数点");
            }
            // 得意先区分＝経費でセット商品を設定していた場合エラー
            if (CommonConstants.KBN_TOKUISAKI_KEIHI.equals(baseBean.getTokuisakikbn())
                && CommonConstants.FLG_OFF.equals(detailDto.getTanpinmeisaiflg())) {
                FormField field = new FormField(
                    String.format("meisaiSummaryBeanList[%d].shncdmei", index), "商品コード");
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W124, "セット商品");
            }

            // 計上区分＝キャンペーンでセット商品を設定していた場合エラー
            if (CommonConstants.KBN_KEIJO_CAMPAIGN.equals(detailDto.getKeijokbn())
                && CommonConstants.FLG_OFF.equals(detailDto.getTanpinmeisaiflg())) {
                FormField field = new FormField(
                    String.format("meisaiSummaryBeanList[%d].shncdmei", index), "商品コード");
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W124,
                    "計上区分がキャンペーンの場合はセット商品");
            }

            // 明細備考(バイト数チェック)
            if (!StringUtil.isEmpty(detailDto.getMeisaibiko())) {
                int max = 40;
                if (!ItemUtil.isByteSizeValid(detailDto.getMeisaibiko(), max)) {
                    FormField field = new FormField(
                        String.format("meisaiSummaryBeanList[%d].meisaibikomei", index), "明細備考");
                    throw BusinessExceptionFactory.validationFailed(field, MessageID.W035,
                        index + 1, "明細備考", max);
                }
            }

        }
        // 取消伝票か確認
        if (torikeshidnpyoflg) {
            // 配達ステータスが配達中の場合エラー
            if (CommonConstants.DNPYSTS_11.equals(baseBean.getDnpysts())) {
                FormField field = new FormField("");
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W121,
                    "配達数量が全て０の場合", "一覧画面にて配達時キャンセル");
            }
            List<String> acptkbnshsList = Arrays.asList(CommonConstants.KBN_ACPTSYS_SHIINSETTI,
                CommonConstants.KBN_ACPTSYS_SHOKAISETTI, CommonConstants.KBN_ACPTSYS_TSUIKASETTI,
                CommonConstants.KBN_ACPTSYS_PROMOTION, CommonConstants.KBN_ACPTSYS_KOKAN,
                CommonConstants.KBN_ACPTSYS_SHIINHIKIAGE, CommonConstants.KBN_ACPTSYS_THUTOHIKIAGE,
                CommonConstants.KBN_ACPTSYS_TAIKAIHIKIAGE);
            // 該当する受注区分詳細の場合ワーニング
            String acptkbnshs = footerBean.getAcptkbnshs();
            if (acptkbnshsList.contains(acptkbnshs)) {
                String acptkbnstr = s02m006tService.searchForMoji(CommonConstants.APCD_000,
                    CommonConstants.SYS_SHOPCD_000, CommonConstants.SYS_SETTEINO_053, acptkbnshs);
                BusinessExceptionFactory.setMessageList(warningMessageList, MessageID.W260,
                    acptkbnstr + "の配達伝票を取り消します。得意先の会員状況、サーバー設置状況を確認してください。");
            }
            // 配布物が設定されている場合ワーニング
            if (CommonConstants.FLG_ON.equals(footerBean.getHaifuflg1())
                || CommonConstants.FLG_ON.equals(footerBean.getHaifuflg2())
                || CommonConstants.FLG_ON.equals(footerBean.getHaifuflg3())
                || CommonConstants.FLG_ON.equals(footerBean.getHaifuflg4())) {
                BusinessExceptionFactory.setMessageList(warningMessageList, MessageID.W260,
                    "配布物を含んだ配達伝票を取り消します。配布物の配達状況を確認してください。");
            }
        }
    }

    /**
     * C0815W21MeisaiSummaryBeanリストを作成
     * 
     * @param acptDlvryMeisaiList List<C0815W11AcptDlvryMeisaiDto>
     * @return List<C0815W21MeisaiSummaryBean>
     */
    private List<C0815W21MeisaiSummaryBean> crateMeisaiSummaryBeanList(
        List<C0815W11AcptDlvryMeisaiDto> acptDlvryMeisaiList) {
        List<C0815W21MeisaiSummaryBean> retList = new ArrayList<>();
        for (C0815W11AcptDlvryMeisaiDto meisaiDto : acptDlvryMeisaiList) {
            if (CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(meisaiDto.getMeisaikbn())
                || !StringUtils.isEmpty(meisaiDto.getHaifubutsucd())) {
                continue;
            } else {
                C0815W21MeisaiSummaryBean meisaiSummaryBean = new C0815W21MeisaiSummaryBean();
                meisaiSummaryBean.setAcptdnpymeisaino(meisaiDto.getAcptdnpymeisaino());
                meisaiSummaryBean.setAcptdnpymeisaiedano(meisaiDto.getAcptdnpymeisaiedano());
                meisaiSummaryBean
                    .setEdano(Objects.toString(meisaiDto.getEdano(), SystemConstants.WORD_BLANK));
                meisaiSummaryBean.setShncdmei(meisaiDto.getShncd());
                meisaiSummaryBean.setHaifubutsuapkihoncd(meisaiDto.getHaifubutsuapkihoncd());
                meisaiSummaryBean.setHaifubutsushopcd(meisaiDto.getHaifubutsushopcd());
                meisaiSummaryBean.setHaifubutsucd(meisaiDto.getHaifubutsucd());
                meisaiSummaryBean.setKeijokbn(meisaiDto.getKeijokbn());
                meisaiSummaryBean.setKeijokbnnmmei(meisaiDto.getKeijokbnnm());
                meisaiSummaryBean.setSuryomei(meisaiDto.getNohinsuryo());
                meisaiSummaryBean.setNohinunt(meisaiDto.getNohinunt());
                meisaiSummaryBean.setMeisaibikomei(meisaiDto.getMeisaibiko());
                meisaiSummaryBean.setShnlctgrycd(meisaiDto.getLctgrycd());
                meisaiSummaryBean.setShnmctgrycd(meisaiDto.getMctgrycd());
                meisaiSummaryBean.setShnnmmei(meisaiDto.getShnnmryakusho());
                meisaiSummaryBean.setHanbaipricemei(meisaiDto.getHanbaiprice());
                meisaiSummaryBean.setShokidsplyprice(meisaiDto.getShokidsplyprice());
                meisaiSummaryBean.setKinmei(meisaiDto.getKin());
                meisaiSummaryBean.setShohitaxkin(meisaiDto.getShohitaxkin());
                meisaiSummaryBean.setIronmmei(meisaiDto.getIro());
                meisaiSummaryBean.setSznmmei(meisaiDto.getSz());
                meisaiSummaryBean.setIrisumei(meisaiDto.getIrisu());
                meisaiSummaryBean.setIrisutanimei(meisaiDto.getIrisutani());
                meisaiSummaryBean.setSokazumei(meisaiDto.getSokazu());
                meisaiSummaryBean.setKatasiki(meisaiDto.getKatasiki());
                meisaiSummaryBean.setShntaxkbn(meisaiDto.getShntaxkbn());
                meisaiSummaryBean.setTaxkbn(meisaiDto.getTaxkbn());
                meisaiSummaryBean.setSuryomei(meisaiDto.getSuryo());
                meisaiSummaryBean.setTanimei(meisaiDto.getTani());
                meisaiSummaryBean.setHyojunpricemei(meisaiDto.getHyojunprice());
                meisaiSummaryBean.setHyojunpricekirikaedd(meisaiDto.getHyojunpricekirikaedd());
                meisaiSummaryBean.setGenzhyojunprice(meisaiDto.getGenzhyojunprice());
                meisaiSummaryBean.setShinhyojunprice(meisaiDto.getShinhyojunprice());
                meisaiSummaryBean
                    .setGenzhyojunmochikaeriprice(meisaiDto.getGenzhyojunmochikaeriprice());
                meisaiSummaryBean
                    .setShinhyojunmochikaeriprice(meisaiDto.getShinhyojunmochikaeriprice());
                meisaiSummaryBean.setShoppricekirikaedd(meisaiDto.getShoppricekirikaedd());
                meisaiSummaryBean.setGenzshopprice(meisaiDto.getGenzshopprice());
                meisaiSummaryBean.setShinshopprice(meisaiDto.getShinshopprice());
                meisaiSummaryBean.setHansokustrtdd(meisaiDto.getHansokustrtdd());
                meisaiSummaryBean.setHansokushrydd(meisaiDto.getHansokushrydd());
                meisaiSummaryBean.setHansokuprice(meisaiDto.getHansokuprice());
                meisaiSummaryBean.setHansokumochikaeriprice(meisaiDto.getHansokumochikaeriprice());
                meisaiSummaryBean.setTtpricestrtdd(meisaiDto.getTtpricestrtdd());
                meisaiSummaryBean.setTtpriceshrydd(meisaiDto.getTtpriceshrydd());
                meisaiSummaryBean.setTtprice(meisaiDto.getTtprice());
                meisaiSummaryBean.setTtmochikaeriprice(meisaiDto.getTtmochikaeriprice());
                meisaiSummaryBean.setKeijokbn2(meisaiDto.getKeijokbn2());
                meisaiSummaryBean.setTanpinmeisaiflg(meisaiDto.getTanpinmeisaiflg());
                meisaiSummaryBean.setPricekbn(meisaiDto.getPricekbn());
                meisaiSummaryBean.setAddgyoflg(meisaiDto.getAddgyoflg());
                meisaiSummaryBean.setTokubetsuUntJson(meisaiDto.getTokubetsuUntJson());
                meisaiSummaryBean
                    .setGenzshopmochikaeriprice(meisaiDto.getGenzshopmochikaeriprice());
                meisaiSummaryBean
                    .setShinshopmochikaeriprice(meisaiDto.getShinshopmochikaeriprice());

                retList.add(meisaiSummaryBean);
            }
        }

        return retList;
    }

    /**
     * 更新用DTOを作成する
     * 
     * @return C0815W21UpdateDto
     */
    private C0815W21UpdateDto createUpdateDto(C0815W21Form form) {
        C0815W21UpdateDto dto = new C0815W21UpdateDto();
        String dlvrydd = "";
        if (!StringUtils.isEmpty(form.getBaseBean().getDlvrydd()) && form.getBaseBean().getDlvrydd()
            .matches(ValidationConstants.REGEXP_YYYY_MM_DD_SLASH)) {
            dlvrydd = DateTimeUtil.toNoSlashedDate(form.getBaseBean().getDlvrydd());
        }

        dto.setApkihoncd(form.getBaseBean().getApkihoncd1());
        dto.setShopcd(form.getBaseBean().getShopcd());
        dto.setDlvrytnshacd(form.getBaseBean().getDlvrytnshacd());
        dto.setDnpyno(form.getBaseBean().getDnpyno());
        dto.setTokuisakicd(form.getKttcd());
        dto.setDlvrycmpnynm(form.getBaseBean().getDlvrycmpnynm());
        dto.setEdano(form.getBaseBean().getEdano());
        dto.setDlvrydd(dlvrydd);
        dto.setMeisaiUpdateDtoList(
            createMeisaiUpdateDtoList(Integer.parseInt(form.getBaseBean().getSelfposition())));

        return dto;
    }

    /**
     * 明細更新用DTOを作成する
     * 
     * @param meisaiSummaryBeanList List<C0815W21MeisaiSummaryBean>
     * @return List<C0815W21MeisaiUpdateDto>
     */
    private List<C0815W21MeisaiUpdateDto> createMeisaiUpdateDtoList(int selfposition) {
        List<C0815W21MeisaiUpdateDto> meisaiDtoList = new ArrayList<>();
        if (hasSessionMeisaiData(selfposition)) {
            for (C0815W11AcptDlvryMeisaiDto acptdlvrymeisaiDto : c0815W00Session.getMeisaiBeanList()
                .get(selfposition).getAcptDlvryMeisaiList()) {
                C0815W21MeisaiUpdateDto meisaiUpdateDto = new C0815W21MeisaiUpdateDto();
                meisaiUpdateDto.setShncdmei(acptdlvrymeisaiDto.getShncd());
                meisaiUpdateDto.setShnnmmei(acptdlvrymeisaiDto.getShnnmryakusho());
                meisaiUpdateDto.setIronmmei(acptdlvrymeisaiDto.getIro());
                meisaiUpdateDto.setSznmmei(acptdlvrymeisaiDto.getSz());
                meisaiUpdateDto.setKatasikimei(acptdlvrymeisaiDto.getKatasiki());
                meisaiUpdateDto.setAddgyoflg(acptdlvrymeisaiDto.getAddgyoflg());
                meisaiUpdateDto.setKeijokbnnmmei(acptdlvrymeisaiDto.getKeijokbnnm());
                meisaiUpdateDto.setKeijokbn(acptdlvrymeisaiDto.getKeijokbn());
                meisaiUpdateDto.setSuryomei(acptdlvrymeisaiDto.getSuryo());
                meisaiUpdateDto.setTanimei(acptdlvrymeisaiDto.getTani());
                meisaiUpdateDto.setIrisumei(acptdlvrymeisaiDto.getUriirisu());
                meisaiUpdateDto.setIrisutanimei(acptdlvrymeisaiDto.getIrisutani());
                meisaiUpdateDto.setSokazumei(acptdlvrymeisaiDto.getSokazu());
                meisaiUpdateDto.setHyojunpricemei(acptdlvrymeisaiDto.getHyojunprice());
                meisaiUpdateDto.setHanbaipricemei(acptdlvrymeisaiDto.getHanbaiprice());
                meisaiUpdateDto.setKinmei(acptdlvrymeisaiDto.getKin());
                meisaiUpdateDto.setMeisaibikono(acptdlvrymeisaiDto.getMeisaibikono());
                meisaiUpdateDto.setMeisaibikomei(acptdlvrymeisaiDto.getMeisaibiko());
                meisaiUpdateDto.setUntshohitaxkin(acptdlvrymeisaiDto.getUntshohitaxkin());
                meisaiUpdateDto.setShohitaxkin(acptdlvrymeisaiDto.getShohitaxkin());
                meisaiUpdateDto.setShntaxkbn(acptdlvrymeisaiDto.getShntaxkbn());
                meisaiUpdateDto
                    .setHyojunpricekirikaedd(acptdlvrymeisaiDto.getHyojunpricekirikaedd());
                meisaiUpdateDto.setGenzhyojunprice(acptdlvrymeisaiDto.getGenzhyojunprice());
                meisaiUpdateDto.setShinhyojunprice(acptdlvrymeisaiDto.getShinhyojunprice());
                meisaiUpdateDto.setGenzhyojunmochikaeriprice(
                    acptdlvrymeisaiDto.getGenzhyojunmochikaeriprice());
                meisaiUpdateDto.setShinhyojunmochikaeriprice(
                    acptdlvrymeisaiDto.getShinhyojunmochikaeriprice());
                meisaiUpdateDto.setShoppricekirikaedd(acptdlvrymeisaiDto.getShoppricekirikaedd());
                meisaiUpdateDto.setGenzshopprice(acptdlvrymeisaiDto.getGenzshopprice());
                meisaiUpdateDto.setShinshopprice(acptdlvrymeisaiDto.getShinshopprice());
                meisaiUpdateDto
                    .setGenzshopmochikaeriprice(acptdlvrymeisaiDto.getGenzshopmochikaeriprice());
                meisaiUpdateDto
                    .setShinshopmochikaeriprice(acptdlvrymeisaiDto.getShinshopmochikaeriprice());
                meisaiUpdateDto.setHansokustrtdd(acptdlvrymeisaiDto.getHansokustrtdd());
                meisaiUpdateDto.setHansokushrydd(acptdlvrymeisaiDto.getHansokushrydd());
                meisaiUpdateDto.setHansokuprice(acptdlvrymeisaiDto.getHansokuprice());
                meisaiUpdateDto
                    .setHansokumochikaeriprice(acptdlvrymeisaiDto.getHansokumochikaeriprice());
                meisaiUpdateDto.setTtpricestrtdd(acptdlvrymeisaiDto.getTtpricestrtdd());
                meisaiUpdateDto.setTtpriceshrydd(acptdlvrymeisaiDto.getTtpriceshrydd());
                meisaiUpdateDto.setTtprice(acptdlvrymeisaiDto.getTtprice());
                meisaiUpdateDto.setTtmochikaeriprice(acptdlvrymeisaiDto.getTtmochikaeriprice());
                meisaiUpdateDto.setShokidsplyprice(acptdlvrymeisaiDto.getShokidsplyprice());
                meisaiUpdateDto.setKeijokbn2(acptdlvrymeisaiDto.getKeijokbn2());
                meisaiUpdateDto.setPricekbn(acptdlvrymeisaiDto.getPricekbn());
                meisaiUpdateDto.setTanpinmeisaiflg(acptdlvrymeisaiDto.getTanpinmeisaiflg());
                meisaiUpdateDto.setStkbn(acptdlvrymeisaiDto.getStkbn());
                meisaiUpdateDto.setMeisaiposition(acptdlvrymeisaiDto.getMeisaiposition());
                meisaiUpdateDto.setMeisaikbn(acptdlvrymeisaiDto.getMeisaikbn());

                meisaiDtoList.add(meisaiUpdateDto);
            }
        }

        return meisaiDtoList;
    }


    /**
     * 有償区分を返します。
     * 
     * @param keijokbn 計上区分
     * @param acptunt 受注単価
     * @return 有償区分
     */
    public static String getYushokbn(String keijokbn, String acptunt) {
        if (isKeihi(keijokbn)) {
            return CommonConstants.KBN_YUSHO_KEIHI;

        } else if (isRental(keijokbn)) {
            return CommonConstants.KBN_YUSHO_RENTAL;

        } else if (isYuryo(keijokbn)) {
            return CommonConstants.KBN_YUSHO_YUSHO;

        } else {
            return CommonConstants.KBN_YUSHO_MUSHO;
        }
    }

    /**
     * 経費区分。
     * 
     * @param keijokbn 計上区分
     * @return boolean 経費の場合はtrue、それ以外の場合はfalse
     */
    private static boolean isKeihi(String keijokbn) {
        return CommonConstants.KBN_KEIJO_KEIHI.equals(keijokbn);
    }

    /**
     * レンタル区分。
     * 
     * @param keijokbn 計上区分
     * @return boolean レンタルの場合はtrue、それ以外の場合はfalse
     */
    private static boolean isRental(String keijokbn) {
        return (CommonConstants.KBN_KEIJO_RENTAL.equals(keijokbn)
            || CommonConstants.KBN_KEIJO_SUBRENTAL.equals(keijokbn));
    }

    /**
     * 有料区分。
     * 
     * @param keijokbn 計上区分
     * @return boolean 有料の場合はtrue、無料の場合はfalse
     */
    private static boolean isYuryo(String keijokbn) {
        return (CommonConstants.KBN_KEIJO_URIAGE.equals(keijokbn)
            || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(keijokbn));
    }

    /**
     * セッションのデータ有無確認
     * 
     * @param selfposition int
     * @return boolean
     */
    private boolean hasSessionMeisaiData(int selfposition) {
        if (ObjectUtils.isEmpty(c0815W00Session)) {
            return false;
        }
        if (ListUtils.isEmpty(c0815W00Session.getMeisaiBeanList())) {
            return false;
        }
        if (ObjectUtils.isEmpty(c0815W00Session.getMeisaiBeanList().get(selfposition))) {
            return false;
        }
        return true;
    }
}
