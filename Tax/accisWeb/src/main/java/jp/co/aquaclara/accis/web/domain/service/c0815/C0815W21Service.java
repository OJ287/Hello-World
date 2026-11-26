package jp.co.aquaclara.accis.web.domain.service.c0815;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.thymeleaf.util.ListUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.constants.SystemConstants;
import jp.co.aquaclara.accis.common.constants.ValidationConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M003TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M009TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M013TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M033TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M035TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M046TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M066TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02V001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T002TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S06T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M003TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M003TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M009TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M033TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M033TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M035TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M035TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M066TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M066TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S06T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S06T001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02V001TC0815W21Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S04T001TC0815W21Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S04T002TC0815W21Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TC0815W21Entity;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.common.util.business.BusinessUtil;
import jp.co.aquaclara.accis.web.controller.c0815.bean.C0815W11MeisaiBean;
import jp.co.aquaclara.accis.web.controller.c0815.bean.C0815W21BaseBean;
import jp.co.aquaclara.accis.web.controller.c0815.bean.C0815W21FooterBean;
import jp.co.aquaclara.accis.web.controller.c0815.bean.C0815W21MeisaiSummaryBean;
import jp.co.aquaclara.accis.web.controller.c0815.form.C0815W21Form;
import jp.co.aquaclara.accis.web.controller.c0815.session.C0815W00Session;
import jp.co.aquaclara.accis.web.domain.dto.c0800.ComparableValueCurrentOrNew;
import jp.co.aquaclara.accis.web.domain.dto.c0815.C0815W11AcptDlvryMeisaiDto;
import jp.co.aquaclara.accis.web.domain.dto.c0815.C0815W21AddRowDto;
import jp.co.aquaclara.accis.web.domain.dto.c0815.C0815W21DisplayDto;
import jp.co.aquaclara.accis.web.domain.dto.c0815.C0815W21KeyDto;
import jp.co.aquaclara.accis.web.domain.dto.c0815.C0815W21MeisaiUpdateDto;
import jp.co.aquaclara.accis.web.domain.dto.c0815.C0815W21UpdateDto;
import jp.co.aquaclara.accis.web.domain.service.business.TaxService;
import jp.co.aquaclara.accis.web.domain.service.c0800.UtilityService;
import jp.co.aquaclara.accis.web.domain.service.master.S02M006TService;
import jp.co.aquaclara.accis.web.domain.service.x9901.KouseihinService;
import jp.co.aquaclara.accis.web.domain.service.x9901.StringUtil;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import jp.co.aquaclara.accis.web.exception.dto.FormField;
import lombok.extern.slf4j.Slf4j;

/**
 * 配達実績登録（個別入力）Service
 * 
 * @author accisdev006
 *
 */
@Service
@Slf4j
public class C0815W21Service implements ComparableValueCurrentOrNew {

    @Autowired
    private S02M003TDao s02m003tDao;

    @Autowired
    private S02M009TDao s02m009tDao;

    @Autowired
    private S02M013TDao s02m013tDao;

    @Autowired
    private S02M033TDao s02m033tDao;

    @Autowired
    private S02M035TDao s02m035tDao;

    @Autowired
    private S02M046TDao s02m046tDao;

    @Autowired
    private S02M066TDao s02m066tDao;

    @Autowired
    private S02V001TDao s02v001tDao;

    @Autowired
    private S04T001TDao s04t001tDao;

    @Autowired
    private S04T002TDao s04t002tDao;

    @Autowired
    private S06T001TDao s06t001tDao;

    @Autowired
    private S07T001TDao s07t001tDao;

    @Autowired
    private S02M006TService s02m006tService;

    @Autowired
    private C0815W00Session c0815W00Session;

    @Autowired
    private KouseihinService kouseihinService;

    /** 商品明細消費税・金額計算Service **/
    @Autowired
    private TaxService taxService;

    @Autowired
    private UtilityService util;

    private final String DEFAULT_STR = "";

    private final String MOCHIKAERI_TNSHA_NM = "持ち帰り";

    /**
     * 画面初期値取得処理
     * 
     * @param keyDto C0815W21KeyDto
     * @return C0815W21DisplayDto
     */
    public C0815W21DisplayDto init(C0815W21KeyDto keyDto) {
        C0815W21DisplayDto displayDto = new C0815W21DisplayDto();
        // 受注伝票データ取得
        S04T001TC0815W21Entity acptdnpyDataEntity = getAcptdnpyData(keyDto);
        // 価格情報設定
        setPriceinfo(displayDto, keyDto);
        // 受注伝票情報設定
        setAcptdnpyData(displayDto, acptdnpyDataEntity);
        // 配達実績登録（一括更新）画面情報設定
        setC0815W11FormData(displayDto, keyDto);
        // 受注明細情報設定
        setAcptmeisaiData(displayDto, keyDto);
        // インボイス施行日取得
        S02M046TEntity shopKey = new S02M046TEntity();
        shopKey.setApkihoncd(keyDto.getApkihoncd());
        shopKey.setShopcd(keyDto.getShopcd());
        String invoicshikoymd = Optional.ofNullable(s02m046tDao.selectByPrimaryKey(shopKey))
            .filter(e -> util.isAvailable(e.getYukokbn())).map(S02M046TEntity::getInvoicshikoymd)
            .orElse(SystemConstants.WORD_BLANK);
        String dlvrydd = keyDto.getDlvrydd();
        // 売上情報と連携している伝票のみを対象とする
        if (!StringUtil.isEmpty(invoicshikoymd) & !StringUtil.isEmpty(dlvrydd)
            && !StringUtils.isEmpty(displayDto.getUridnpyno())) {
            // 修正不可判定の設定(インボイス施行日以前)
            if (!DateTimeUtil.isLater(invoicshikoymd, dlvrydd)) {
                unmodifiableDecision(displayDto);
            }
            // 修正不可判定の設定（インボイス考慮、インボイス施行日以降）
            if (DateTimeUtil.isLater(invoicshikoymd, dlvrydd)) {
                unmodifiableDecisionForInvoice(displayDto);
            }
        }
        // 受託伝票判定の設定
        setJutakuDenpyoDecision(keyDto, displayDto);
        return displayDto;
    }

    /**
     * 行追加処理
     * 
     * @param dto C0815W21AddRowDto
     * @param form C0815W21Form
     * @return C0815W11AcptDlvryMeisaiDto
     */
    public C0815W11AcptDlvryMeisaiDto getAddRowData(C0815W21AddRowDto dto, C0815W21Form form) {
        S02V001TC0815W21Entity s02v001tKey = new S02V001TC0815W21Entity();
        s02v001tKey.setApkihoncd(dto.getApkihoncd());
        s02v001tKey.setShopcd(dto.getShopcd());
        s02v001tKey.setTokuisakicd(dto.getTokuisakicd());
        s02v001tKey.setUnyoymd(DateTimeUtil.toNoSlashedDate(dto.getDlvrydd()));
        s02v001tKey.setShncd(dto.getShncd());

        S02V001TC0815W21Entity s02v001tc0815w21Entity = new S02V001TC0815W21Entity();
        List<S02V001TC0815W21Entity> s02v001tc0815w21EntityList =
            s02v001tDao.selectByC0815W21(s02v001tKey);
        if (!ListUtils.isEmpty(s02v001tc0815w21EntityList)) {
            s02v001tc0815w21Entity = s02v001tc0815w21EntityList.get(0);
        }
        String lctgrycd =
            s02m006tService.searchForMoji(CommonConstants.APCD_000, CommonConstants.SYS_SHOPCD_000,
                CommonConstants.SYS_SETTEINO_096, CommonConstants.SETTEICD_096_4);
        String mctgrycd =
            s02m006tService.searchForMoji(CommonConstants.APCD_000, CommonConstants.SYS_SHOPCD_000,
                CommonConstants.SYS_SETTEINO_134, CommonConstants.SETTEICD_134_41);
        String tokuisakikbn = dto.getTokuisakikbn();

        if (CommonConstants.KBN_TOKUISAKI_KEIHI.equals(tokuisakikbn)) {
            dto.setKeijokbn(CommonConstants.KBN_KEIJO_KEIHI);
        } else if (lctgrycd.equals(s02v001tc0815w21Entity.getLctgrycd())
            && mctgrycd.equals(s02v001tc0815w21Entity.getMctgrycd())) {
            String acptkbn = dto.getAcptkbn();
            if (CommonConstants.KBN_ACPT_SHOHIN.equals(acptkbn)
                && CommonConstants.KBN_TOKUISAKI_SHOP.equals(tokuisakikbn)) {
                dto.setKeijokbn(CommonConstants.KBN_KEIJO_SUBRENTAL);
            } else if (CommonConstants.KBN_ACPT_SHOHIN.equals(acptkbn)
                && !CommonConstants.KBN_TOKUISAKI_SHOP.equals(tokuisakikbn)) {
                dto.setKeijokbn(CommonConstants.KBN_KEIJO_URIAGE);
            } else {
                dto.setKeijokbn(CommonConstants.KBN_KEIJO_RENTAL);
            }
        } else {
            dto.setKeijokbn(CommonConstants.KBN_KEIJO_URIAGE);
        }
        if (CommonConstants.KBN_KEIJO_KEIHI.equals(dto.getKeijokbn())) {
            dto.setHanbaipricemei(s02v001tc0815w21Entity.getZaikount());
        } else {
            dto.setHanbaipricemei("0");
        }

        return createMeisaiDto(dto, form, s02v001tc0815w21Entity);
    }

    /**
     * 配布物がマスタから有効な配布物の情報を取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param haifubutsucd 配布物コード
     * @return 配布物マスタEntity（存在しない場合はOptional.empty()）
     */
    private Optional<S02M035TEntity> getValidHaifubutsu(String apkihoncd, String shopcd,
        String haifubutsucd) {
        S02M035TEntityExample condition = new S02M035TEntityExample();
        condition.createCriteria().andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd)
            .andHaifubutsucdEqualTo(haifubutsucd).andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        return s02m035tDao.selectByExample(condition).stream().findFirst();
    }

    /**
     * 配布物企画対象得意先マスタ関連の相関チェック（相関チェックNo.26, 27）
     * 
     * @param haifubutsuapkihoncd 配布物AP基本コード
     * @param haifubutsushopcd 配布物ショップコード
     * @param haifubutsucd 配布物コード
     * @param updateDto 更新DTO
     */
    private void checkPlanningHaifubutsu(String haifubutsuapkihoncd, String haifubutsushopcd,
        String haifubutsucd, C0815W21UpdateDto updateDto, String hauhuNo) {
        // 配布物企画対象得意先マスタを取得
        S02M033TEntity plan = getPlanningHaifubutsuEntity(haifubutsuapkihoncd, haifubutsushopcd,
            haifubutsucd, updateDto.getTokuisakicd());
        // 相関チェックNo.26 配布物有効チェック
        if (Objects.isNull(plan) || !util.isAvailable(plan.getYukokbn())
            || util.isFlagged(plan.getHaifuclflg())) {
            FormField field = new FormField("footerBean.haifubutsucd" + hauhuNo, "配布物" + hauhuNo);
            throw BusinessExceptionFactory.validationFailed(field, MessageID.W106, "無効な配布物");
        }
        // 相関チェックNo.27 配布物配布対象ステータスチェック
        if (!isValidHaifubutsuStatus(plan, updateDto.getDnpyno(), updateDto.getEdano())) {
            FormField field = new FormField("footerBean.haifubutsucd" + hauhuNo, "配布物" + hauhuNo);
            throw BusinessExceptionFactory.validationFailed(field, MessageID.W096);
        }
    }

    /**
     * 配布物企画対象得意先マスタから配布物の企画情報を取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param haifubutsucd 配布物コード
     * @param tokuisakicd 得意先コード
     * @return 配布物企画情報
     */
    private S02M033TEntity getPlanningHaifubutsuEntity(String apkihoncd, String shopcd,
        String haifubutsucd, String tokuisakicd) {
        S02M033TEntityKey key = new S02M033TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setHaifubutsucd(haifubutsucd);
        key.setTokuisakicd(tokuisakicd);
        return s02m033tDao.selectByPrimaryKey(key);
    }

    /**
     * 配布物の配布対象ステータスを元に妥当性をチェックする
     * 
     * @param plan 配布物企画情報
     * @param 更新用DTO
     * @return 未納品ならばtrue
     */
    protected boolean isValidHaifubutsuStatus(S02M033TEntity plan, String dnpyno, String edano) {
        if (Objects.isNull(plan) || !util.isAvailable(plan.getYukokbn())) {
            // 配布物企画対象得意先マスタのデータが有効でない場合は妥当ではない
            return false;
        }
        // 配布対象ステータスにより判定する
        if (Objects.equals(plan.getHaifutrgtsts(), CommonConstants.KBN_HAIFUSTS_MINOHIN)) {
            // 未納品であれば妥当とする
            return true;
        }
        // 未納品以外の場合（受注済または配達済）、対象となる受注が処理中の受注であれば妥当とする
        // ※他の受注で取り込んだり配達されている配布物はエラーとする
        return (Objects.equals(plan.getAcptdnpyno(), dnpyno)
            && Objects.equals(util.getString(plan.getEdano()), edano));
    }

    /**
     * 更新前チェック（DBアクセスあり）
     * 
     * @param updateDto
     * @param form
     * @param warningMessageList
     */
    public void updateCheckParam(C0815W21UpdateDto updateDto, C0815W21Form form,
        List<String> warningMessageList) {

        // チェック用データ取得
        String dlvrydd = updateDto.getDlvrydd();
        C0815W21FooterBean footerBean = form.getFooterBean();

        // 配達会社が持ち帰りではない場合
        if (!CommonConstants.CD_DLVRYCMP_JISHA.equals(updateDto.getDlvrycmpnynm())
            && !CommonConstants.CD_DLVRYCMP_MOCHIKAERI.equals(updateDto.getDlvrycmpnynm())
            && !StringUtils.isEmpty(updateDto.getDlvrytnshacd())) {
            // 担当者マスタモデル取得
            S02M003TEntityExample tantoshaExample = new S02M003TEntityExample();
            S02M003TEntityExample.Criteria tantoshaCriteria = tantoshaExample.createCriteria();
            tantoshaCriteria.andApkihoncdEqualTo(updateDto.getApkihoncd());
            tantoshaCriteria.andShopcdEqualTo(updateDto.getShopcd());
            tantoshaCriteria.andTnshacdEqualTo(updateDto.getDlvrytnshacd());
            List<S02M003TEntity> tantoshaEntityList = s02m003tDao.selectByExample(tantoshaExample);
            // 配達担当者の所属配達会社コードが配達会社コードと一致しない場合エラー
            if (!ListUtils.isEmpty(tantoshaEntityList)
                && !ObjectUtils.nullSafeEquals(updateDto.getDlvrycmpnynm(),
                    tantoshaEntityList.get(0).getShozokudlvrycmpnycd())) {
                List<FormField> fieldList =
                    Arrays.asList(new FormField("baseBean.dlvrytnshacd", "配達担当者名"),
                        new FormField("baseBean.dlvrycmpnynm", "配達会社名"));
                throw BusinessExceptionFactory.validationFailed(fieldList, MessageID.W126, "配達会社",
                    "担当者の所属配達会社");
            }
        }
        // 配布物1～4の存在チェック
        if (CommonConstants.FLG_ON.equals(footerBean.getHaifuflg1())
            && !StringUtils.isEmpty(footerBean.getHaifubutsuapkihoncd1())
            && !StringUtils.isEmpty(footerBean.getHaifubutsushopcd1())
            && !StringUtils.isEmpty(footerBean.getHaifubutsucd1())) {
            Optional<S02M035TEntity> haifubutsuEntity =
                getValidHaifubutsu(footerBean.getHaifubutsuapkihoncd1(),
                    footerBean.getHaifubutsushopcd1(), footerBean.getHaifubutsucd1());
            if (haifubutsuEntity.isEmpty()) {
                FormField field = new FormField("footerBean.haifubutsucd1", "配布物1");
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W123, "配布物");
            }
            S02M035TEntity e = haifubutsuEntity.get();
            String haifudkikanstrtdd = e.getHaifudkikanstrtdd();
            String haifudkikanshrydd = e.getHaifudkikanshrydd();
            if (!DateTimeUtil.isDateBetween(dlvrydd, haifudkikanstrtdd, haifudkikanshrydd)) {
                FormField field = new FormField("footerBean.haifubutsucd1", "配布物1");
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W170, "配布物", "配布");
            }
            // 相関チェックNo.26, 27の実施
            checkPlanningHaifubutsu(footerBean.getHaifubutsuapkihoncd1(),
                footerBean.getHaifubutsushopcd1(), footerBean.getHaifubutsucd1(), updateDto, "1");
        }
        if (CommonConstants.FLG_ON.equals(footerBean.getHaifuflg2())
            && !StringUtils.isEmpty(footerBean.getHaifubutsuapkihoncd2())
            && !StringUtils.isEmpty(footerBean.getHaifubutsushopcd2())
            && !StringUtils.isEmpty(footerBean.getHaifubutsucd2())) {
            Optional<S02M035TEntity> haifubutsuEntity =
                getValidHaifubutsu(footerBean.getHaifubutsuapkihoncd2(),
                    footerBean.getHaifubutsushopcd2(), footerBean.getHaifubutsucd2());
            if (haifubutsuEntity.isEmpty()) {
                FormField field = new FormField("footerBean.haifubutsucd2", "配布物2");
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W123, "配布物");
            }
            S02M035TEntity e = haifubutsuEntity.get();
            String haifudkikanstrtdd = e.getHaifudkikanstrtdd();
            String haifudkikanshrydd = e.getHaifudkikanshrydd();
            if (!DateTimeUtil.isDateBetween(dlvrydd, haifudkikanstrtdd, haifudkikanshrydd)) {
                FormField field = new FormField("footerBean.haifubutsucd2", "配布物2");
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W170, "配布物", "配布");
            }
            // 相関チェックNo.26, 27の実施
            checkPlanningHaifubutsu(footerBean.getHaifubutsuapkihoncd2(),
                footerBean.getHaifubutsushopcd2(), footerBean.getHaifubutsucd2(), updateDto, "2");
        }
        if (CommonConstants.FLG_ON.equals(footerBean.getHaifuflg3())
            && !StringUtils.isEmpty(footerBean.getHaifubutsuapkihoncd3())
            && !StringUtils.isEmpty(footerBean.getHaifubutsushopcd3())
            && !StringUtils.isEmpty(footerBean.getHaifubutsucd3())) {
            Optional<S02M035TEntity> haifubutsuEntity =
                getValidHaifubutsu(footerBean.getHaifubutsuapkihoncd3(),
                    footerBean.getHaifubutsushopcd3(), footerBean.getHaifubutsucd3());
            if (haifubutsuEntity.isEmpty()) {
                FormField field = new FormField("footerBean.haifubutsucd3", "配布物3");
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W123, "配布物");
            }
            S02M035TEntity e = haifubutsuEntity.get();
            String haifudkikanstrtdd = e.getHaifudkikanstrtdd();
            String haifudkikanshrydd = e.getHaifudkikanshrydd();
            if (!DateTimeUtil.isDateBetween(dlvrydd, haifudkikanstrtdd, haifudkikanshrydd)) {
                FormField field = new FormField("footerBean.haifubutsucd3", "配布物3");
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W170, "配布物", "配布");
            }
            // 相関チェックNo.26, 27の実施
            checkPlanningHaifubutsu(footerBean.getHaifubutsuapkihoncd3(),
                footerBean.getHaifubutsushopcd3(), footerBean.getHaifubutsucd3(), updateDto, "3");
        }
        if (CommonConstants.FLG_ON.equals(footerBean.getHaifuflg4())
            && !StringUtils.isEmpty(footerBean.getHaifubutsuapkihoncd4())
            && !StringUtils.isEmpty(footerBean.getHaifubutsushopcd4())
            && !StringUtils.isEmpty(footerBean.getHaifubutsucd4())) {
            Optional<S02M035TEntity> haifubutsuEntity =
                getValidHaifubutsu(footerBean.getHaifubutsuapkihoncd4(),
                    footerBean.getHaifubutsushopcd4(), footerBean.getHaifubutsucd4());
            if (haifubutsuEntity.isEmpty()) {
                FormField field = new FormField("footerBean.haifubutsucd4", "配布物4");
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W123, "配布物");
            }
            S02M035TEntity e = haifubutsuEntity.get();
            String haifudkikanstrtdd = e.getHaifudkikanstrtdd();
            String haifudkikanshrydd = e.getHaifudkikanshrydd();
            if (!DateTimeUtil.isDateBetween(dlvrydd, haifudkikanstrtdd, haifudkikanshrydd)) {
                FormField field = new FormField("footerBean.haifubutsucd4", "配布物4");
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W170, "配布物", "配布");
            }
            // 相関チェックNo.26, 27の実施
            checkPlanningHaifubutsu(footerBean.getHaifubutsuapkihoncd4(),
                footerBean.getHaifubutsushopcd4(), footerBean.getHaifubutsucd4(), updateDto, "4");
        }

        // 配達売上時チェック
        checkDelivered(updateDto, form, warningMessageList);

        // 明細の相関チェックを実施
        checkParamMeisai(updateDto);

    }

    /**
     * 配達売上時チェック
     * 
     * @param updateDto
     * @param form
     * @param warningMessageList
     */
    private void checkDelivered(C0815W21UpdateDto updateDto, C0815W21Form form,
        List<String> warningMessageList) {
        // チェック用データ取得
        C0815W21BaseBean baseBean = form.getBaseBean();
        String apkihoncd = updateDto.getApkihoncd();
        String shopcd = updateDto.getShopcd();
        String dnpyno = updateDto.getDnpyno();
        String dlvrydd = updateDto.getDlvrydd();
        String edano = baseBean.getEdano();
        boolean dlvryddChangedFg = false; // 配達日変更
        boolean amountChangedFg = false; // 明細数量金額変更

        // インボイス施行日を取得
        S02M046TEntityExample invoicshikoymdExample = new S02M046TEntityExample();
        S02M046TEntityExample.Criteria invoicshikoymdCrteria =
            invoicshikoymdExample.createCriteria();
        invoicshikoymdCrteria.andApkihoncdEqualTo(apkihoncd);
        invoicshikoymdCrteria.andShopcdEqualTo(shopcd);
        invoicshikoymdCrteria.andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<S02M046TEntity> invoicshikoymdEntityList =
            s02m046tDao.selectByExample(invoicshikoymdExample);
        S02M046TEntity invoicshikoymdEntity =
            !ListUtils.isEmpty(invoicshikoymdEntityList) ? invoicshikoymdEntityList.get(0)
                : new S02M046TEntity();

        // 得意先マスタを取得
        S02M013TEntityExample s02m013Example = new S02M013TEntityExample();
        S02M013TEntityExample.Criteria s02m013tCriteria = s02m013Example.createCriteria();
        s02m013tCriteria.andApkihoncdEqualTo(apkihoncd);
        s02m013tCriteria.andShopcdEqualTo(shopcd);
        s02m013tCriteria.andTokuisakicdEqualTo(updateDto.getTokuisakicd());
        s02m013tCriteria.andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<S02M013TEntity> s02m013tEntityList = s02m013tDao.selectByExample(s02m013Example);
        S02M013TEntity s02m013tEntity =
            !ListUtils.isEmpty(s02m013tEntityList) ? s02m013tEntityList.get(0)
                : new S02M013TEntity();

        // S04T001T_受注配達テーブルを検索する。s04t001t
        S04T001TEntityExample s04t001tExample = new S04T001TEntityExample();
        S04T001TEntityExample.Criteria s04t001tCriteria = s04t001tExample.createCriteria();
        s04t001tCriteria.andApkihoncdEqualTo(apkihoncd);
        s04t001tCriteria.andShopcdEqualTo(shopcd);
        s04t001tCriteria.andAcptdnpynoEqualTo(dnpyno);
        s04t001tCriteria.andEdanoEqualTo(Short.valueOf(updateDto.getEdano()));
        s04t001tCriteria.andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<S04T001TEntity> s04t001tEntityList = s04t001tDao.selectByExample(s04t001tExample);
        S04T001TEntity s04t001tEntity =
            !ListUtils.isEmpty(s04t001tEntityList) ? s04t001tEntityList.get(0)
                : new S04T001TEntity();

        // 配達日変更
        if (!StringUtils.isEmpty(s04t001tEntity.getDlvrydd())
            && !dlvrydd.equals(s04t001tEntity.getDlvrydd())) {
            dlvryddChangedFg = true;
        }

        // 売上情報と連携している伝票のみを対象とする
        String uridenpyno = s04t001tEntity.getUridnpyno();
        if (StringUtils.isEmpty(uridenpyno)) {
            return;
        }

        String dnpysts = s04t001tEntity.getDnpysts();
        String oldDlvrydd = s04t001tEntity.getDlvrydd();

        // S06T001T_売上テーブルを検索する。s06t001t
        S06T001TEntityExample s06t001tExample = new S06T001TEntityExample();
        S06T001TEntityExample.Criteria s06t001tCriteria = s06t001tExample.createCriteria();
        s06t001tCriteria.andApkihoncdEqualTo(apkihoncd);
        s06t001tCriteria.andShopcdEqualTo(shopcd);
        if (!StringUtil.isEmpty(s04t001tEntity.getUridnpyno())) {
            s06t001tCriteria.andUridnpynoEqualTo(s04t001tEntity.getUridnpyno());
        } else {
            s06t001tCriteria.andUridnpynoIsNull();
        }
        s06t001tCriteria.andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<S06T001TEntity> s06t001tEntityList = s06t001tDao.selectByExample(s06t001tExample);
        S06T001TEntity s06t001tEntity =
            !ListUtils.isEmpty(s06t001tEntityList) ? s06t001tEntityList.get(0)
                : new S06T001TEntity();

        // 売上情報と連携している伝票のみを対象とする
        if (StringUtils.isEmpty(s06t001tEntity.getApkihoncd())) {
            return;
        }

        if (!ListUtils.isEmpty(form.getMeisaiSummaryBeanList())) {
            for (C0815W21MeisaiSummaryBean bean : form.getMeisaiSummaryBeanList()) {
                // 明細情報変更チェック
                // 更新前明細行取得
                short shortEdano = !StringUtils.isEmpty(edano) ? Short.parseShort(edano) : 0;
                int acptdnpymeisaino = !StringUtils.isEmpty(bean.getAcptdnpymeisaino())
                    ? Integer.parseInt(bean.getAcptdnpymeisaino())
                    : 0;
                short acptdnpymeisaiedano = !StringUtils.isEmpty(bean.getAcptdnpymeisaiedano())
                    ? Short.parseShort(bean.getAcptdnpymeisaiedano())
                    : 0;
                S04T002TEntityKey entitykey = new S04T002TEntityKey();
                entitykey.setApkihoncd(apkihoncd);
                entitykey.setShopcd(shopcd);
                entitykey.setAcptdnpyno(dnpyno);
                entitykey.setEdano(shortEdano);
                entitykey.setAcptdnpymeisaino(acptdnpymeisaino);
                entitykey.setAcptdnpymeisaiedano(acptdnpymeisaiedano);
                S04T002TEntity meisaiOld = s04t002tDao.selectByPrimaryKey(entitykey);

                // 配達済の場合：明細数量・金額変更有無チェック
                String suryo = ItemUtil.removeComma(bean.getSuryomei());
                String nouhinunt = ItemUtil.removeComma(bean.getHanbaipricemei());
                String nouhinkin = ItemUtil.removeComma(bean.getKinmei());
                if (CommonConstants.DNPYSTS_12.equals(dnpysts)) {
                    // 行追加かつ数量が0ではない場合
                    if (meisaiOld == null) {
                        if (Objects.equals("0", suryo)) {
                            amountChangedFg = true;
                        }
                    }
                    // 数量・価格・金額を変更する場合
                    else if (!suryo.equals(meisaiOld.getNohinsuryo().toString())
                        || !nouhinunt.equals(meisaiOld.getNohinunt().toString())
                        || !nouhinkin.equals(meisaiOld.getNohinkin().toString())) {
                        amountChangedFg = true;
                    }
                    // 明細行の追加や明細行の数量、金額の変更が1件でもあればそれ以外を確認する必要なし
                    if (amountChangedFg) {
                        break;
                    }
                }
            }
        }

        C0815W21FooterBean footerBean = form.getFooterBean();
        String kinSm = ItemUtil
            .removeComma(BusinessUtil.getString(footerBean.getSm(), SystemConstants.WORD_BLANK));
        String shohitaxkin = ItemUtil.removeComma(
            BusinessUtil.getString(footerBean.getUchishohitaxkin(), SystemConstants.WORD_BLANK));
        // 変更項目が全て同一の場合、チェックを実施しない
        if (!dlvryddChangedFg && !amountChangedFg
            && kinSm.equals(s04t001tEntity.getNohinkin().toString())
            && shohitaxkin.equals(s04t001tEntity.getNohinshohitaxkin().toString())) {
            return;
        }

        List<S07T001TEntity> s07t001tEntityList = new ArrayList<>();
        // S07T001T_請求残高テーブルを検索する。s07t001t
        S07T001TEntityExample s07t001tExample = null;
        if (CommonConstants.FLG_ON.equals(s04t001tEntity.getAcptthdaibikiflg())) {
            s07t001tExample = new S07T001TEntityExample();
            S07T001TEntityExample.Criteria s07t001tCriteria = s07t001tExample.createCriteria();
            s07t001tCriteria.andApkihoncdEqualTo(apkihoncd);
            s07t001tCriteria.andShopcdEqualTo(shopcd);
            // 取得元の値によって条件を切替
            if (!StringUtils.isEmpty(s02m013tEntity.getSkysakicd())) {
                s07t001tCriteria.andSkysakicdEqualTo(s02m013tEntity.getSkysakicd());
            } else {
                s07t001tCriteria.andSkysakicdIsNull();
            }
            s07t001tCriteria.andTokuisakicdEqualTo(updateDto.getTokuisakicd());
            if (!StringUtils.isEmpty(oldDlvrydd)) {
                s07t001tCriteria.andShimedydmEqualTo(oldDlvrydd.substring(0, 6)); // 締年月
                s07t001tCriteria.andShimeddEqualTo(oldDlvrydd.substring(6, 8)); // 締日
            } else {
                s07t001tCriteria.andShimedydmIsNull();
                s07t001tCriteria.andShimeddIsNull();
            }
            s07t001tCriteria
                .andSkyprcsjissikbnEqualTo(Short.valueOf(CommonConstants.SKYPRCSJISSIKBN_DAIBIKI));
            s07t001tCriteria.andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        } else {
            s07t001tExample = new S07T001TEntityExample();
            S07T001TEntityExample.Criteria s07t001tCriteria = s07t001tExample.createCriteria();
            s07t001tCriteria.andApkihoncdEqualTo(apkihoncd);
            s07t001tCriteria.andShopcdEqualTo(shopcd);
            // 取得元の値によって条件を切替
            if (!StringUtils.isEmpty(s02m013tEntity.getSkysakicd())) {
                s07t001tCriteria.andSkysakicdEqualTo(s02m013tEntity.getSkysakicd());
            } else {
                s07t001tCriteria.andSkysakicdIsNull();
            }
            s07t001tCriteria.andTokuisakicdEqualTo(updateDto.getTokuisakicd());
            if (!StringUtils.isEmpty(s06t001tEntity.getSkyshimedydm())) {
                s07t001tCriteria.andShimedydmEqualTo(s06t001tEntity.getSkyshimedydm());
            } else {
                s07t001tCriteria.andShimedydmIsNull();
            }
            if (!StringUtils.isEmpty(s06t001tEntity.getSkyshimedd())) {
                s07t001tCriteria.andShimeddEqualTo(s06t001tEntity.getSkyshimedd());
            } else {
                s07t001tCriteria.andShimeddIsNull();
            }
            s07t001tCriteria.andSkyprcsjissikbnEqualTo(
                Short.valueOf(CommonConstants.SKYPRCSJISSIKBN_TSUKIJIME));
            s07t001tCriteria.andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

        }
        if (!ObjectUtils.isEmpty(s07t001tExample)) {
            s07t001tEntityList = s07t001tDao.selectByExample(s07t001tExample);
        }
        S07T001TEntity s07t001tEntity =
            !ListUtils.isEmpty(s07t001tEntityList) ? s07t001tEntityList.get(0)
                : new S07T001TEntity();

        String invoicshikoymd = BusinessUtil.getString(invoicshikoymdEntity.getInvoicshikoymd(),
            SystemConstants.WORD_BLANK);
        dnpysts = s04t001tEntity.getDnpysts();
        // インボイス施行日以前かつ、受注配達テーブル．伝票ステータス＝“12：配達売上済”の場合
        if (!StringUtils.isEmpty(invoicshikoymd) && DateTimeUtil.isLater(dlvrydd, invoicshikoymd)
            && CommonConstants.DNPYSTS_12.equals(dnpysts)) {
            // 請求残高テーブルのレコードが存在している場合
            Long keshikomikin = s07t001tEntity.getKeshikomikin();
            // 請求残高テーブル．消込金額≠０ （入金消込済み）の伝票を更新する場合、エラーとする。
            if (keshikomikin != null && keshikomikin != 0) {
                FormField field = new FormField("", "");
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W230, dnpyno,
                    "入金消込済みデータ");
            }

            String skyprcsjissidd = s06t001tEntity.getSkyprcsjissidd();
            String skyshimeymd = new StringBuilder()
                .append(BusinessUtil.getString(s06t001tEntity.getSkyshimedydm(),
                    SystemConstants.WORD_BLANK))
                .append(BusinessUtil.getString(s06t001tEntity.getSkyshimedd(),
                    SystemConstants.WORD_BLANK))
                .toString();

            S07T001TC0815W21Entity maxShimeymdEntity = new S07T001TC0815W21Entity();
            maxShimeymdEntity.setApkihoncd(apkihoncd);
            maxShimeymdEntity.setShopcd(shopcd);
            maxShimeymdEntity.setTokuisakicd(updateDto.getTokuisakicd());
            maxShimeymdEntity.setSkysakicd(s02m013tEntity.getSkysakicd());
            maxShimeymdEntity.setYukokbn(CommonConstants.YUKOKBN_1);
            String maxShimeymd = s07t001tDao.selectByMaxShimedydm(maxShimeymdEntity);
            // 請求締めが完了している伝票を更新する場合、エラーとする。
            if (!CommonConstants.DEF_DATE_00000000.equals(skyprcsjissidd)
                && !CommonConstants.DAIBIKI.equals(skyprcsjissidd)
                && (StringUtils.isEmpty(maxShimeymd) || !maxShimeymd.equals(skyshimeymd))) {
                FormField field = new FormField("", "");
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W230, dnpyno,
                    "請求済みデータ");
            }

            // 消込が完了している（請求残高テーブル．消込金額＝０ かつ、
            // 売上テーブル．請求処理実施日≠“00000000”かつ、≠“ＤＡＩＢＩＫＩ”）伝票を更新する場合、ワーニングとする
            if (keshikomikin != null && keshikomikin == 0
                && !CommonConstants.DEF_DATE_00000000.equals(skyprcsjissidd)
                && !CommonConstants.DAIBIKI.equals(skyprcsjissidd)) {
                BusinessExceptionFactory.setMessageList(warningMessageList, MessageID.W231);
            }
        }

        // インボイス施行日以降の場合かつ、受注配達テーブル．伝票ステータス＝“12：配達売上済”の場合
        if (!StringUtils.isEmpty(invoicshikoymd) && DateTimeUtil.isLater(invoicshikoymd, dlvrydd)
            && CommonConstants.DNPYSTS_12.equals(dnpysts)) {
            boolean mishimeFlg = false;
            S06T001TEntityExample s06t001MishimeExample = new S06T001TEntityExample();

            S06T001TEntityExample.Criteria s06t001MishimeCriteria =
                s06t001MishimeExample.createCriteria();
            s06t001MishimeCriteria.andApkihoncdEqualTo(apkihoncd);
            s06t001MishimeCriteria.andShopcdEqualTo(shopcd);
            s06t001MishimeCriteria.andAcptdnpynoEqualTo(dnpyno);
            s06t001MishimeCriteria.andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
            S06T001TEntityExample.Criteria orCriteria = s06t001MishimeExample.createCriteria();
            orCriteria.andApkihoncdEqualTo(apkihoncd);
            orCriteria.andShopcdEqualTo(shopcd);
            orCriteria.andUridnpynoEqualTo(dnpyno);
            orCriteria.andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
            s06t001MishimeExample.or(orCriteria);
            List<S06T001TEntity> s06t001MishimeEntityList =
                s06t001tDao.selectByExample(s06t001MishimeExample);
            if (!ListUtils.isEmpty(s06t001MishimeEntityList)) {
                for (S06T001TEntity inEntity : s06t001MishimeEntityList) {
                    String skyshimeymd = inEntity.getSkyshimedydm();
                    if (!StringUtils.isEmpty(skyshimeymd) && !"000000".equals(skyshimeymd)) {
                        mishimeFlg = true;
                    }
                }
            }
            if (mishimeFlg) {
                FormField field = new FormField("", "");
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W260,
                    "請求締めがされている為、修正・取消はできません。締め解除を行った後に、再度修正をしてください。");
            }
        }

        // 受注配達テーブル．伝票ステータス＝“12：配達売上済” かつ、
        // 得意先マスタ．顧客区分＝“5：ＮＡ受託” または、“6：配達受託” の伝票を更新する場合、ワーニングとする
        if (CommonConstants.DNPYSTS_12.equals(s04t001tEntity.getDnpysts())
            && (CommonConstants.KBN_JUTAKUDLVTOKUI_NAJUTAKU
                .equals(s02m013tEntity.getJutakudlvrycstkbn())
                || CommonConstants.KBN_JUTAKUDLVTOKUI_DLVRYJUTAKU
                    .equals(s02m013tEntity.getJutakudlvrycstkbn()))) {
            BusinessExceptionFactory.setMessageList(warningMessageList, MessageID.W232);
        }
    }

    /**
     * 相関チェック（明細）
     * 
     * @param dto C0815W21UpdateDto
     */
    private void checkParamMeisai(C0815W21UpdateDto dto) {
        // チェック用データ取得
        String apkihoncd = dto.getApkihoncd();
        String shopcd = dto.getShopcd();
        String dnpyno = dto.getDnpyno();

        // 得意先マスタを取得
        S02M013TEntityExample s02m013Example = new S02M013TEntityExample();
        S02M013TEntityExample.Criteria s02m013tCriteria = s02m013Example.createCriteria();
        s02m013tCriteria.andApkihoncdEqualTo(apkihoncd);
        s02m013tCriteria.andShopcdEqualTo(shopcd);
        s02m013tCriteria.andTokuisakicdEqualTo(dto.getTokuisakicd());
        s02m013tCriteria.andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<S02M013TEntity> s02m013tEntityList = s02m013tDao.selectByExample(s02m013Example);
        S02M013TEntity s02m013tEntity =
            !ListUtils.isEmpty(s02m013tEntityList) ? s02m013tEntityList.get(0)
                : new S02M013TEntity();

        // S04T001T_受注配達テーブルを検索する。s04t001t
        S04T001TEntityExample s04t001tExample = new S04T001TEntityExample();
        S04T001TEntityExample.Criteria s04t001tCriteria = s04t001tExample.createCriteria();
        s04t001tCriteria.andApkihoncdEqualTo(apkihoncd);
        s04t001tCriteria.andShopcdEqualTo(shopcd);
        s04t001tCriteria.andAcptdnpynoEqualTo(dnpyno);
        s04t001tCriteria.andEdanoEqualTo(Short.valueOf(dto.getEdano()));
        s04t001tCriteria.andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<S04T001TEntity> s04t001tEntityList = s04t001tDao.selectByExample(s04t001tExample);
        S04T001TEntity s04t001tEntity =
            !ListUtils.isEmpty(s04t001tEntityList) ? s04t001tEntityList.get(0)
                : new S04T001TEntity();

        // 委託相手のAP基本コード、ショップコード、得意先コードが紐づいている場合
        String itakuaiteapkihoncd = BusinessUtil.getString(s02m013tEntity.getItakuaiteapkihoncd(),
            SystemConstants.WORD_BLANK);
        String itakuaiteshopcd =
            BusinessUtil.getString(s02m013tEntity.getItakuaiteshopcd(), SystemConstants.WORD_BLANK);
        String itakuaitetokuisakicd = BusinessUtil
            .getString(s02m013tEntity.getItakuaitetokuisakicd(), SystemConstants.WORD_BLANK);

        // S02M013T_得意先マスタから委託相手の得意先情報を検索する。
        S02M013TEntityExample s02m013ItakuExample = new S02M013TEntityExample();
        S02M013TEntityExample.Criteria s02m013ItakuCriteria = s02m013ItakuExample.createCriteria();
        // 取得元の値によって条件を切替
        if (!StringUtils.isEmpty(itakuaiteapkihoncd)) {
            s02m013ItakuCriteria.andApkihoncdEqualTo(itakuaiteapkihoncd);
        } else {
            s02m013ItakuCriteria.andApkihoncdIsNull();
        }
        if (!StringUtils.isEmpty(itakuaiteshopcd)) {
            s02m013ItakuCriteria.andShopcdEqualTo(itakuaiteshopcd);
        } else {
            s02m013ItakuCriteria.andShopcdIsNull();
        }
        if (!StringUtils.isEmpty(itakuaitetokuisakicd)) {
            s02m013ItakuCriteria.andTokuisakicdEqualTo(itakuaitetokuisakicd);
        } else {
            s02m013ItakuCriteria.andTokuisakicdIsNull();
        }
        s02m013ItakuCriteria.andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<S02M013TEntity> s02m013ItakuEntityList =
            s02m013tDao.selectByExample(s02m013ItakuExample);
        S02M013TEntity s02m013ItakuEntity =
            !ListUtils.isEmpty(s02m013ItakuEntityList) ? s02m013ItakuEntityList.get(0)
                : new S02M013TEntity();

        // 明細チェック処理
        for (C0815W21MeisaiUpdateDto meisaiDto : dto.getMeisaiUpdateDtoList()) {
            // 配布物の場合、チェックしない
            if (CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(meisaiDto.getMeisaikbn())) {
                continue;
            }
            // 行追加により追加した明細かつ数量が0の場合はチェックしない
            if (util.isFlagged(meisaiDto.getAddgyoflg())
                && Objects.equals("0", meisaiDto.getSuryomei())) {
                continue;
            }
            int meisaiposition = Integer.parseInt(meisaiDto.getMeisaiposition());
            // S02V001T_商品マスタビューを検索する。
            S02V001TEntityExample s02v001tExample = new S02V001TEntityExample();
            S02V001TEntityExample.Criteria s02v001tCriteria = s02v001tExample.createCriteria();
            s02v001tCriteria.andApkihoncdEqualTo(apkihoncd);
            s02v001tCriteria.andShopcdEqualTo(shopcd);
            s02v001tCriteria.andShncdEqualTo(meisaiDto.getShncdmei());
            s02v001tCriteria.andKihonshnyukokbnEqualTo(CommonConstants.YUKOKBN_1);
            List<S02V001TEntity> s02v001tEntityList = s02v001tDao.selectByExample(s02v001tExample);
            S02V001TEntity s02v001tEntity =
                !ListUtils.isEmpty(s02v001tEntityList) ? s02v001tEntityList.get(0)
                    : new S02V001TEntity();

            // S02V001T_商品マスタビューを検索する。(委託相手）
            S02V001TEntityExample s02v001tItakuExample = new S02V001TEntityExample();
            S02V001TEntityExample.Criteria s02v001tItakuCriteria =
                s02v001tItakuExample.createCriteria();
            // 取得元の値によって条件を切り替える
            if (!StringUtils.isEmpty(itakuaiteapkihoncd)) {
                s02v001tItakuCriteria.andApkihoncdEqualTo(itakuaiteapkihoncd);
            } else {
                s02v001tItakuCriteria.andApkihoncdIsNull();
            }
            if (!StringUtils.isEmpty(itakuaiteshopcd)) {
                s02v001tItakuCriteria.andShopcdEqualTo(itakuaiteshopcd);
            } else {
                s02v001tItakuCriteria.andShopcdIsNull();
            }
            s02v001tItakuCriteria.andShncdEqualTo(meisaiDto.getShncdmei());
            s02v001tItakuCriteria.andShopyukokbnEqualTo(CommonConstants.YUKOKBN_1);
            List<S02V001TEntity> s02v001tItakuEntityList =
                s02v001tDao.selectByExample(s02v001tItakuExample);
            S02V001TEntity s02v001tItakuEntity =
                !ListUtils.isEmpty(s02v001tItakuEntityList) ? s02v001tItakuEntityList.get(0)
                    : new S02V001TEntity();

            String jutakudlvrycstkbn = BusinessUtil.getString(s02m013tEntity.getJutakudlvrycstkbn(),
                SystemConstants.WORD_BLANK);
            String itakuaiteacptdnpyno = BusinessUtil
                .getString(s04t001tEntity.getItakuaiteacptdnpyno(), SystemConstants.WORD_BLANK);
            String itakuShncd =
                BusinessUtil.getString(s02v001tItakuEntity.getShncd(), SystemConstants.WORD_BLANK);
            String s02m013tApkihoncd =
                BusinessUtil.getString(s02m013tEntity.getApkihoncd(), SystemConstants.WORD_BLANK);
            String s02m013tShopcd =
                BusinessUtil.getString(s02m013tEntity.getShopcd(), SystemConstants.WORD_BLANK);
            String s02m013tTokuisakicd =
                BusinessUtil.getString(s02m013tEntity.getTokuisakicd(), SystemConstants.WORD_BLANK);
            String shnzokusei =
                BusinessUtil.getString(s02v001tEntity.getShnzokusei(), SystemConstants.WORD_BLANK);
            // 委託相手が存在している場合
            String itakumotoApkihoncd = BusinessUtil
                .getString(s02m013ItakuEntity.getItakuaiteapkihoncd(), SystemConstants.WORD_BLANK);
            String itakumotoShopcd = BusinessUtil.getString(s02m013ItakuEntity.getItakuaiteshopcd(),
                SystemConstants.WORD_BLANK);
            String itakumotoTokuisakicd = BusinessUtil.getString(
                s02m013ItakuEntity.getItakuaitetokuisakicd(), SystemConstants.WORD_BLANK);

            // 得意先マスタ．顧客区分＝“3：ＮＡ顧客” または、“4：委託顧客”の場合
            if ((CommonConstants.KBN_JUTAKUDLVTOKUI_NAKOKYAKU.equals(jutakudlvrycstkbn)
                || CommonConstants.KBN_JUTAKUDLVTOKUI_ITAKUKOKYAKU.equals(jutakudlvrycstkbn))) {
                // 得意先マスタ．顧客区分＝“3：ＮＡ顧客” または、“4：委託顧客” かつ、
                // 受注配達テーブル．委託相手伝票番号が存在する かつ、
                // 基本商品マスタ．商品属性＝“1：ＡＰ商品” が含まれている場合、エラー
                if (!StringUtils.isEmpty(itakuaiteacptdnpyno)
                    && CommonConstants.KBN_SHNZOKUSEI_AP.equals(shnzokusei)) {
                    FormField field = new FormField(
                        String.format("meisaiSummaryBeanList[%d].shncdmei", meisaiposition),
                        "商品コード");
                    throw BusinessExceptionFactory.validationFailed(field, MessageID.W233);
                }
                // 得意先マスタ．顧客区分＝“3：ＮＡ顧客” または、“4：委託顧客” かつ、
                // 受注配達テーブル．委託相手伝票番号が存在する かつ、
                // 受託のＡＰ／ショップコードに紐づく商品コードがショップ商品マスタに存在しない場合、エラーとする
                if (!StringUtils.isEmpty(itakuaiteacptdnpyno) && StringUtils.isEmpty(itakuShncd)) {
                    FormField field = new FormField(
                        String.format("meisaiSummaryBeanList[%d].shncdmei", meisaiposition),
                        "商品コード");
                    throw BusinessExceptionFactory.validationFailed(field, MessageID.W235);
                }
                // 得意先マスタ．顧客区分＝“3：ＮＡ顧客” または、“4：委託顧客” かつ、
                // 得意先マスタ．委託相手ＡＰ基本コード または、委託相手ショップコード または、委託相手得意先コード が未入力の場合、エラーとする
                if ((StringUtils.isEmpty(itakuaiteapkihoncd) || StringUtils.isEmpty(itakuaiteshopcd)
                    || StringUtils.isEmpty(itakuaitetokuisakicd))) {
                    FormField field = new FormField("", "");
                    throw BusinessExceptionFactory.validationFailed(field, MessageID.W238);
                }
                // 得意先マスタ．顧客区分＝“3：ＮＡ顧客” または、“4：委託顧客” かつ、
                // 委託の委託相手コードに紐づく得意先マスタが存在しない場合、エラーとする
                if (StringUtils.isEmpty(s02m013ItakuEntity.getApkihoncd())) {
                    FormField field = new FormField("", "");
                    throw BusinessExceptionFactory.validationFailed(field, MessageID.W237);
                }
                // 得意先マスタ．顧客区分＝“3：ＮＡ顧客” または、“4：委託顧客” かつ、
                // 得意先マスタ．ＡＰ基本コード≠得意先マスタ２．委託相手ＡＰ基本コード または、
                // 得意先マスタ．ショップコード≠得意先マスタ２．委託相手ショップコード または、
                // 得意先マスタ．得意先コード≠得意先マスタ２．委託相手得意先コード の場合、エラーとする
                if ((!s02m013tApkihoncd.equals(itakumotoApkihoncd)
                    || !s02m013tShopcd.equals(itakumotoShopcd)
                    || !s02m013tTokuisakicd.equals(itakumotoTokuisakicd))) {
                    FormField field = new FormField("", "");
                    throw BusinessExceptionFactory.validationFailed(field, MessageID.W240);
                }
            }
            // 得意先マスタ．顧客区分＝“5：ＮＡ受託” または、“6：配達受託”の場合
            if ((CommonConstants.KBN_JUTAKUDLVTOKUI_NAJUTAKU.equals(jutakudlvrycstkbn)
                || CommonConstants.KBN_JUTAKUDLVTOKUI_DLVRYJUTAKU.equals(jutakudlvrycstkbn))) {
                // 得意先マスタ．顧客区分＝“5：ＮＡ受託” または、“6：配達受託” かつ、
                // 受注配達テーブル．委託相手伝票番号が存在する かつ、
                // 基本商品マスタ．商品属性＝“1：ＡＰ商品” が含まれている場合、エラー
                if (!StringUtils.isEmpty(itakuaiteacptdnpyno)
                    && CommonConstants.KBN_SHNZOKUSEI_AP.equals(shnzokusei)) {
                    FormField field = new FormField(
                        String.format("meisaiSummaryBeanList[%d].shncdmei", meisaiposition),
                        "商品コード");
                    throw BusinessExceptionFactory.validationFailed(field, MessageID.W233);
                }
                // 得意先マスタ．顧客区分＝“5：ＮＡ受託” または、“6：配達受託” かつ、
                // 受注配達テーブル．委託相手伝票番号が存在するかつ
                // 委託のＡＰ／ショップコードに紐づく商品コードがショップ商品マスタに存在しない場合、エラー
                if (!StringUtils.isEmpty(itakuaiteacptdnpyno) && StringUtils.isEmpty(itakuShncd)) {
                    FormField field = new FormField(
                        String.format("meisaiSummaryBeanList[%d].shncdmei", meisaiposition),
                        "商品コード");
                    throw BusinessExceptionFactory.validationFailed(field, MessageID.W235);
                }
                // 得意先マスタ．顧客区分＝“5：ＮＡ受託” または、“6：配達受託” かつ、
                // 得意先マスタ．委託相手ＡＰ基本コード または、委託相手ショップコード または、委託相手得意先コード が未入力の場合、エラーとする
                if ((StringUtils.isEmpty(itakuaiteapkihoncd) || StringUtils.isEmpty(itakuaiteshopcd)
                    || StringUtils.isEmpty(itakuaitetokuisakicd))) {
                    FormField field = new FormField("", "");
                    throw BusinessExceptionFactory.validationFailed(field, MessageID.W236);
                }
                // 得意先マスタ．顧客区分＝“5：ＮＡ受託” または、“6：配達受託” かつ、
                // 委託の委託相手コードに紐づく得意先マスタが存在しない場合、エラーとする
                if (StringUtils.isEmpty(s02m013ItakuEntity.getApkihoncd())) {
                    FormField field = new FormField("", "");
                    throw BusinessExceptionFactory.validationFailed(field, MessageID.W239);
                }
                // 得意先マスタ．顧客区分＝“5：ＮＡ受託” または、“6：配達受託” かつ、
                // 得意先マスタ．ＡＰ基本コード≠得意先マスタ２．委託相手ＡＰ基本コード または、
                // 得意先マスタ．ショップコード≠得意先マスタ２．委託相手ショップコード または、
                // 得意先マスタ．得意先コード≠得意先マスタ２．委託相手得意先コード の場合、エラーとする。
                if ((!s02m013tApkihoncd.equals(itakumotoApkihoncd)
                    || !s02m013tShopcd.equals(itakumotoShopcd)
                    || !s02m013tTokuisakicd.equals(itakumotoTokuisakicd))) {
                    FormField field = new FormField("", "");
                    throw BusinessExceptionFactory.validationFailed(field, MessageID.W240);
                }
            }

            // 請求先未設定時に計上区分に売上は指定不可
            String skysakicd = s02m013tEntity.getSkysakicd();
            String keijokbn = meisaiDto.getKeijokbn2();
            // 得意先の請求先コードが未設定で計上区分が01:売上の場合エラー
            if (StringUtils.isEmpty(skysakicd)
                && CommonConstants.KBN_KEIJO_URIAGE.equals(keijokbn)) {
                FormField field = new FormField("", "");
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W132,
                    "得意先の請求先情報が未設定", "計上区分に売上の指定");
            }
            // 得意先の請求先コードが未設定で計上区分が02:キャンペーンの場合エラー
            if (StringUtils.isEmpty(skysakicd)
                && CommonConstants.KBN_KEIJO_CAMPAIGN.equals(keijokbn)) {
                FormField field = new FormField("", "");
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W132,
                    "得意先の請求先情報が未設定", "計上区分にキャンペーンの指定");
            }

            // ACCIS2023-7154 商品の販売期間チェック
            ensureSalePeriodValid(dto.getDlvrydd(), s02v001tEntity, meisaiDto);
            // ACCIS2023-7154 委託相手の受注情報がある場合は委託相手商品に関する販売期間チェックを行う
            if (!StringUtils.isEmpty(itakuaiteacptdnpyno)) {
                ensureConsigneeProductSalePeriodValid(dto.getDlvrydd(), s02v001tItakuEntity,
                    meisaiDto);
            }
        }
    }

    /**
     * 配達日が商品の販売期間内かどうかをチェックする.
     * 
     * @param dlvrydd 配達日
     * @param productEntity 商品情報
     * @param detailDto 明細情報
     */
    private void ensureSalePeriodValid(String dlvrydd, S02V001TEntity productEntity,
        C0815W21MeisaiUpdateDto detailDto) {
        validateSalePeriod(dlvrydd, productEntity, detailDto.getSuryomei(),
            detailDto.getMeisaiposition(), "販売");
    }

    /**
     * 配達日が委託相手の商品の販売期間内かどうかをチェックする.
     * 
     * @param dlvrydd 配達日
     * @param productEntity 商品情報
     * @param detailDto 明細情報
     */
    private void ensureConsigneeProductSalePeriodValid(String dlvrydd, S02V001TEntity productEntity,
        C0815W21MeisaiUpdateDto detailDto) {
        validateSalePeriod(dlvrydd, productEntity, detailDto.getSuryomei(),
            detailDto.getMeisaiposition(), "委託相手の商品が販売");
    }

    /**
     * 配達日が商品の販売期間内かどうかをチェックする.
     * 
     * @param dlvrydd 配達日
     * @param productEntity 商品情報
     * @param detailQuantity 明細数量
     * @param orderLineNumber 明細行番号
     * @param messageArg メッセージの可変要素
     */
    private void validateSalePeriod(String dlvrydd, S02V001TEntity productEntity,
        String detailQuantity, String orderLineNumber, String messageArg) {
        log.debug("*** ACCIS2023-7154 対応 : 販売期間チェック ***");
        log.debug("配達日 : {}", dlvrydd);
        log.debug("商品コード : {}", productEntity.getShncd());
        log.debug("販売開始日 : {}", productEntity.getHanbaistrtdd());
        log.debug("販売終了日 : {}", productEntity.getHanbaishrydd());
        log.debug("明細数量 : {}", detailQuantity);
        log.debug("明細行番号 : {}", orderLineNumber);
        log.debug("メッセージパラメータ : {}", messageArg);
        BigDecimal quantity = util.getDecimalValue(detailQuantity);
        // 明細の数量が0の場合はチェック不要
        if (BigDecimal.ZERO.equals(quantity)) {
            return;
        }
        // 配達日が商品の販売期間に含まれているかをチェック
        if (!util.isDateBetween(dlvrydd, productEntity.getHanbaistrtdd(),
            productEntity.getHanbaishrydd())) {
            // 販売期間街の場合はエラー
            FormField field = new FormField(String.format("meisaiSummaryBeanList[%d].shncdmei",
                util.getIntValue(orderLineNumber)), "商品コード");
            throw BusinessExceptionFactory.validationFailed(field, MessageID.W130, messageArg);
        }
    }


    /**
     * 行追加用データ作成
     * 
     * @param addRowDto C0815W21AddRowDto
     * @param entity S02V001TC0815W21Entity
     * @return C0815W11AcptDlvryMeisaiDto
     */
    private C0815W11AcptDlvryMeisaiDto createMeisaiDto(C0815W21AddRowDto addRowDto,
        C0815W21Form form, S02V001TC0815W21Entity entity) {
        C0815W11AcptDlvryMeisaiDto meisaiDto = new C0815W11AcptDlvryMeisaiDto();

        meisaiDto.setApkihoncd(addRowDto.getApkihoncd());
        meisaiDto.setShopcd(addRowDto.getShopcd());
        meisaiDto.setShnapkihoncd(entity.getShnapkihoncd());
        meisaiDto.setShncd(entity.getShncd());
        meisaiDto.setSokazu("0");
        meisaiDto.setHyojunprice(entity.getGenzhyojunprice());
        meisaiDto.setHanbaiprice(addRowDto.getHanbaipricemei());
        meisaiDto.setShokidsplyprice(addRowDto.getHanbaipricemei());
        meisaiDto.setLctgrycd(entity.getLctgrycd());
        meisaiDto.setMctgrycd(entity.getMctgrycd());
        meisaiDto.setSctgrycd(entity.getSctgrycd());
        meisaiDto.setShnnmryakusho(entity.getShnnmryakusho());
        meisaiDto.setPricekbn(entity.getPricekbn());
        meisaiDto.setHyojunpricekirikaedd(entity.getHyojunpricekirikaedd());
        meisaiDto.setGenzhyojunprice(
            BusinessUtil.getString(entity.getGenzhyojunprice(), BigDecimal.ZERO.toString()));
        meisaiDto.setShinhyojunprice(
            BusinessUtil.getString(entity.getShinhyojunprice(), BigDecimal.ZERO.toString()));
        meisaiDto.setGenzhyojunmochikaeriprice(
            BusinessUtil.getString(entity.getGenzmochikaeriprice(), BigDecimal.ZERO.toString()));
        meisaiDto.setShinhyojunmochikaeriprice(
            BusinessUtil.getString(entity.getShinmochikaeriprice(), BigDecimal.ZERO.toString()));

        // ショップ切替日、現ショップ価格、新ショップ価格、現ショップ持ち帰り価格、新ショップ持ち帰り価格
        if (CommonConstants.KBN_PRICE_HYOJUN.equals(entity.getPricekbn())) {
            meisaiDto.setShoppricekirikaedd(entity.getHyojunpricekirikaedd());
            meisaiDto.setGenzshopprice(
                BusinessUtil.getString(entity.getGenzhyojunprice(), BigDecimal.ZERO.toString()));
            meisaiDto.setShinshopprice(
                BusinessUtil.getString(entity.getShinhyojunprice(), BigDecimal.ZERO.toString()));
            meisaiDto.setGenzshopmochikaeriprice(BusinessUtil
                .getString(entity.getGenzmochikaeriprice(), BigDecimal.ZERO.toString()));
            meisaiDto.setShinshopmochikaeriprice(BusinessUtil
                .getString(entity.getShinmochikaeriprice(), BigDecimal.ZERO.toString()));
        } else {
            meisaiDto.setShoppricekirikaedd(entity.getOpnpricekirikaedd());
            meisaiDto.setGenzshopprice(
                BusinessUtil.getString(entity.getGenzopnprice(), BigDecimal.ZERO.toString()));
            meisaiDto.setShinshopprice(
                BusinessUtil.getString(entity.getShinopnprice(), BigDecimal.ZERO.toString()));
            meisaiDto.setGenzshopmochikaeriprice(BusinessUtil
                .getString(entity.getGenzopnmochikaeriprice(), BigDecimal.ZERO.toString()));
            meisaiDto.setShinshopmochikaeriprice(BusinessUtil
                .getString(entity.getShinopnmochikaeriprice(), BigDecimal.ZERO.toString()));
        }

        // 販促開始日、販促終了日、販促配達価格、販促持ち帰り価格
        if (CommonConstants.FLG_ON.equals(entity.getHansokukbn())) {
            meisaiDto.setHansokustrtdd(entity.getHansokustrtdd());
            meisaiDto.setHansokushrydd(entity.getHansokushrydd());
            meisaiDto.setHansokuprice(
                BusinessUtil.getString(entity.getHansokudlvryprice(), BigDecimal.ZERO.toString()));
            meisaiDto.setHansokumochikaeriprice(BusinessUtil
                .getString(entity.getHansokumochikaeriprice(), BigDecimal.ZERO.toString()));
            meisaiDto.setHansokuyusenkbn(entity.getHansokuyusenkbn());
        }
        meisaiDto.setHansokukbn(entity.getHansokukbn());
        meisaiDto.setHansokunm(entity.getHansokunm());
        meisaiDto.setZaikount(entity.getZaikount());
        meisaiDto.setIro(entity.getIro());
        meisaiDto.setUriirisu(entity.getUriirisu());

        // 税区分
        if (CommonConstants.FLG_ON.equals(entity.getTaxsetteikbn())) {
            meisaiDto.setShntaxkbn(entity.getTaxkbn());
            meisaiDto.setTaxkbn(entity.getTaxkbn());
        } else {
            meisaiDto.setShntaxkbn(null);
            meisaiDto.setTaxkbn(null);
        }


        // 得意先価格開始日、得意先価格終了日、得意先価格、得意先持ち帰り価格
        if (CommonConstants.FLG_ON.equals(entity.getRknkindtokubetsuuntflg())) {
            if (CommonConstants.FLG_ON.equals(entity.getStkbn())) {
                meisaiDto.setTtpricestrtdd(entity.getSet_tekiyostrtdd());
                meisaiDto.setTtpriceshrydd(entity.getSet_tekiyoshrydd());
                meisaiDto.setTtprice(
                    BusinessUtil.getString(entity.getSet_dlvryprice(), BigDecimal.ZERO.toString()));
                meisaiDto.setTtmochikaeriprice(BusinessUtil
                    .getString(entity.getSet_mochikaeriprice(), BigDecimal.ZERO.toString()));
            } else {
                meisaiDto.setTtpricestrtdd(entity.getTekiyostrtdd());
                meisaiDto.setTtpriceshrydd(entity.getTekiyoshrydd());
                meisaiDto.setTtprice(
                    BusinessUtil.getString(entity.getDlvryprice(), BigDecimal.ZERO.toString()));
                meisaiDto.setTtmochikaeriprice(BusinessUtil.getString(entity.getMochikaeriprice(),
                    BigDecimal.ZERO.toString()));
            }
        }
        meisaiDto.setSz(entity.getSz());
        meisaiDto.setUritrtskitani(entity.getUritrtskitani());
        meisaiDto.setUritani(entity.getUritani());
        meisaiDto.setMeisaikbn(CommonConstants.KBN_MEISAI_SHOHIN);
        meisaiDto.setNyuryokumeisaiflg(CommonConstants.FLG_ON);
        // 単品明細フラグ
        if (CommonConstants.FLG_ON.equals(entity.getStkbn())) {
            meisaiDto.setTanpinmeisaiflg(CommonConstants.FLG_OFF);
            meisaiDto.setStshncd(meisaiDto.getShncd());
        } else {
            meisaiDto.setTanpinmeisaiflg(CommonConstants.FLG_ON);
        }
        meisaiDto.setShnnm(entity.getShnnm());
        meisaiDto.setHyojunprice(entity.getHyojunprice());
        meisaiDto.setYukokbn(CommonConstants.FLG_ON);
        // セット得意先価格開始日、セット得意先価格終了日、セット得意先価格、セット得意先持ち帰り価格
        if (CommonConstants.FLG_ON.equals(entity.getRknkindtokubetsuuntflg())) {
            meisaiDto.setSet_tekiyostrtdd(entity.getSet_tekiyostrtdd());
            meisaiDto.setSet_tekiyoshrydd(entity.getSet_tekiyoshrydd());
            meisaiDto.setSet_dlvryprice(
                BusinessUtil.getString(entity.getSet_dlvryprice(), BigDecimal.ZERO.toString()));
            meisaiDto.setSet_mochikaeriprice(BusinessUtil.getString(entity.getSet_mochikaeriprice(),
                BigDecimal.ZERO.toString()));
        }
        meisaiDto.setShncd(addRowDto.getShncd());
        meisaiDto.setShnnm(entity.getShnnm());
        meisaiDto.setIro(entity.getIro());
        meisaiDto.setSz(entity.getSz());
        meisaiDto.setKeijokbn(addRowDto.getKeijokbn());
        meisaiDto.setKeijokbn2(addRowDto.getKeijokbn());
        meisaiDto.setTani(entity.getUritrtskitani());
        meisaiDto.setIrisu(entity.getUriirisu());
        meisaiDto.setIrisutani(entity.getUritani());

        // 現在標準単価取得
        String dlvryytidd = StringUtils.isEmpty(form.getBaseBean().getDlvryytidd()) ? ""
            : form.getBaseBean().getDlvryytidd();
        if (dlvryytidd.matches(ValidationConstants.REGEXP_YYYY_MM_DD_SLASH)) {
            dlvryytidd = DateTimeUtil.toNoSlashedDate(dlvryytidd);
        }
        meisaiDto.setHyojununt(
            getCurrentHyojununt(form.getBaseBean().getDlvrykbn(), dlvryytidd, entity));

        // 現在受注単価設定
        setCurrentAcptUnt(form, entity, meisaiDto);

        // キャンペーンで明細備考が空白の場合はキャンペーン名称を明細備考にセットする
        if (CommonConstants.KBN_KEIJO_CAMPAIGN.equals(meisaiDto.getKeijokbn())
            && StringUtil.isEmpty(meisaiDto.getMeisaibiko())) {
            String hansokuunt =
                getCampaignTanka(form.getBaseBean().getDlvrykbn(), dlvryytidd, meisaiDto);
            // 販促優先区分＝"する"で販促価格がNULL以外(販促設定:対象＋適用期間内)の場合のみ明細備考をセットする
            if (CommonConstants.FLG_ON.equals(meisaiDto.getHansokuyusenkbn())
                && !StringUtils.isEmpty(hansokuunt)) {
                meisaiDto.setMeisaibiko(meisaiDto.getHansokunm());
            }
        }

        S02M013TEntityKey s02m013EntityKey = new S02M013TEntityKey();
        s02m013EntityKey.setApkihoncd(form.getBaseBean().getApkihoncd1());
        s02m013EntityKey.setShopcd(form.getBaseBean().getShopcd());
        s02m013EntityKey.setTokuisakicd(form.getKttcd());
        S02M013TEntity s02m013tEntity = s02m013tDao.selectByPrimaryKey(s02m013EntityKey);
        // 得意先別特別単価リスト
        if (CommonConstants.FLG_ON.equals(s02m013tEntity.getRknkindtokubetsuuntflg())) {
            List<?> tokubetsuUntList;
            if (CommonConstants.FLG_ON.equals(meisaiDto.getTanpinmeisaiflg())) {

                // 得意先の商品別特別単価を取得
                S02M009TEntity tokubetsuuntKey = new S02M009TEntity();
                tokubetsuuntKey.setApkihoncd(form.getBaseBean().getApkihoncd1());
                tokubetsuuntKey.setShopcd(form.getBaseBean().getShopcd());
                tokubetsuuntKey.setShncd(meisaiDto.getShncd());
                tokubetsuuntKey.setTokuisakicd(form.getKttcd());
                tokubetsuUntList = s02m009tDao.getShnTokubetsuUnt(tokubetsuuntKey);
            } else {

                // 得意先のセット商品別特別単価を取得
                S02M066TEntityKey tokubetsuuntKey = new S02M066TEntityKey();
                tokubetsuuntKey.setApkihoncd(form.getBaseBean().getApkihoncd1());
                tokubetsuuntKey.setShopcd(form.getBaseBean().getShopcd());
                tokubetsuuntKey.setStshncd(meisaiDto.getShncd());
                tokubetsuuntKey.setTokuisakicd(form.getKttcd());
                tokubetsuUntList = s02m066tDao.getStShnTokubetsuUnt(tokubetsuuntKey);
            }
            meisaiDto.setTokubetsuUntJson(
                convertTokubetsuuntList(tokubetsuUntList, meisaiDto.getShncd()));
        }
        meisaiDto.setAddgyoflg(CommonConstants.FLG_ON);

        return meisaiDto;
    }

    /**
     * 価格情報設定
     * 
     * @param displayDto C0815W21DisplayDto
     * @param keyDto C0815W21KeyDto
     */
    private void setPriceinfo(C0815W21DisplayDto displayDto, C0815W21KeyDto keyDto) {
        S02M046TEntity keyEntity = new S02M046TEntity();
        keyEntity.setApkihoncd(keyDto.getApkihoncd());
        keyEntity.setShopcd(keyDto.getShopcd());

        S02M046TEntity result = s02m046tDao.selectByPrimaryKey(keyEntity);
        StringBuffer buf = new StringBuffer();
        buf.append("");
        if (!ObjectUtils.isEmpty(result)) {
            buf.append("{");
            buf.append("type:1");
            buf.append(",shohitaxkirikaedd:").append(result.getShohitaxkirikaedd()).append("");
            buf.append(",shinshohitax:").append(result.getShinshohitax()).append("");
            buf.append(",genzshohitax:").append(result.getGenzshohitax()).append("");
            buf.append(",taxkeisankbnurikirikaedd:").append(result.getTaxkeisankbnurikirikaedd())
                .append("");
            buf.append(",shintaxkeisankbnuri:").append(result.getShintaxkeisankbnuri()).append("");
            buf.append(",genztaxkeisankbnuri:").append(result.getGenztaxkeisankbnuri()).append("");
            buf.append(",taxhasukeisankbn:").append(result.getTaxhasukeisankbn()).append("");
            buf.append(",keisanhoho:1");
            buf.append(",taxkbn:1");
            buf.append(",urikinhasukeisankbn:").append(result.getUrikinhasukeisankbn()).append("");
            buf.append("}");
        }

        displayDto.setPriceInfo(buf.toString());
    }

    /**
     * 受注伝票データ設定
     * 
     * @param displayDto C0815W21DisplayDto
     * @param entity S04T001TC0815W21Entity
     */
    private void setAcptdnpyData(C0815W21DisplayDto displayDto, S04T001TC0815W21Entity entity) {
        displayDto.setDlvrykyotennm(entity.getDlvrykyotennm());
        displayDto.setDlvryareanm(entity.getDlvryareanm());
        displayDto.setDlvryptnnm(entity.getDlvryptnnm());
        displayDto.setTokuisakikbn(entity.getTokuisakikbn());
        displayDto.setDnpysts(entity.getDnpysts());
        displayDto.setPrntmemon1(entity.getPrntmemon1());
        displayDto.setPrntmemon2(entity.getPrntmemon2());
        displayDto.setPrntmemon3(entity.getPrntmemon3());
        displayDto.setKaishun1n2rittoruazuk(entity.getKaishun1n2rittoruazuk());
        displayDto.setKaishun8rittoruazuk(entity.getKaishun8rittoruazuk());
        displayDto.setAcptkbnshs(entity.getAcptkbnshs());
        displayDto.setDlvrykbn(entity.getDlvrykbn());
        displayDto.setDlvrycmpnynm(entity.getDlvrycmpnycd());
        displayDto.setDlvrytnshacd(entity.getDlvrytnshacd());
        displayDto.setUridnpyno(entity.getUridnpyno());
        displayDto.setSkysakicd(entity.getSkysakicd());
        displayDto.setKaishuhohokbn(entity.getKaishuhohokbn());
        displayDto.setItakuaiteapkihoncd(entity.getItakuaiteapkihoncd());
        displayDto.setItakuaiteshopcd(entity.getItakuaiteshopcd());
        displayDto.setItakuaiteacptdnpyno(entity.getItakuaiteacptdnpyno());
        displayDto.setItakuaiteedano(entity.getItakuaiteedano());
        displayDto.setAcptdd(entity.getAcptdd());
        displayDto.setDlvryytidd(entity.getDlvryytidd());
        displayDto.setDlvrykibojikan(entity.getDlvrykibojikan());
        displayDto.setDlvrykibojikanto(entity.getDlvrykibojikanto());
        displayDto.setDlvrytnshamei(entity.getDlvrytnshamei());
        displayDto.setDatsksmotokbn(entity.getDatsksmotokbnmei());
        displayDto.setDlvrykibojikankbnnm(entity.getDlvrykibojikankbnnm());
        displayDto.setNohinsakijoho1(entity.getNohinsakijohonmei1());
        displayDto.setNohinsakijoho2(entity.getNohinsakijohonmei2());
        displayDto.setAcptkbn(entity.getAcptkbnnm());
        displayDto.setHdnacptkbn(entity.getAcptkbn());
    }

    /**
     * C0815W21DisplayDto初期化
     * 
     * @param displayDto C0815W21DisplayDto
     * @param keyDto C0815W21KeyDto
     */
    private void setC0815W11FormData(C0815W21DisplayDto displayDto, C0815W21KeyDto keyDto) {
        int selfposition = Integer.parseInt(keyDto.getSelfposition());
        displayDto.setApkihoncd(keyDto.getApkihoncd());
        displayDto.setShopcd(keyDto.getShopcd());
        displayDto.setTokuisakicd(keyDto.getTokuisakicd());
        displayDto.setDnpyno(keyDto.getDnpyno());
        displayDto.setEdano(keyDto.getEdano());

        // 一括画面の内容を反映する
        if (hasSessionMeisaiData(selfposition)) {
            C0815W11MeisaiBean meisaiBean = c0815W00Session.getMeisaiBeanList().get(selfposition);
            // 配達日
            String dlvrydd = DateTimeUtil.toNoSlashedDate(meisaiBean.getDlvryddmei());
            // 配達担当者コード（プルダウンの値はコード）
            String dlvrytnshacd = meisaiBean.getDlvrytnshameimei();
            // 配達担当者名（hiddenの方、配達会社名が入ってる）
            String dlvrytnshanm = meisaiBean.getDlvrytnshanmmei();
            // 一括画面で配達担当者が「持ち帰り」だった場合
            if (CommonConstants.CD_DLVRYCMP_MOCHIKAERI.equals(dlvrytnshacd)
                || MOCHIKAERI_TNSHA_NM.equals(dlvrytnshanm)) {
                // 配達担当者コード、配達担当者名は空にする
                dlvrytnshacd = SystemConstants.WORD_BLANK;
                dlvrytnshanm = SystemConstants.WORD_BLANK;
            }
            displayDto.setDlvrydd(dlvrydd);
            keyDto.setDlvrydd(dlvrydd);
            // 配達担当者コード
            displayDto.setDlvrytnshacd(dlvrytnshacd);
            // 配達担当者名
            displayDto.setDlvrytnshamei(dlvrytnshanm);
            // 配達区分
            displayDto.setDlvrykbn(meisaiBean.getDlvrykbn());
            // 配達会社コード
            displayDto.setDlvrycmpnynm(meisaiBean.getDlvrycmpnycd());
            displayDto.setTnshamei(dlvrytnshanm);
            displayDto.setSelfposition(keyDto.getSelfposition());
            displayDto.setFzaiokikanoflg(meisaiBean.getFzaiokikanoflgmei());
            displayDto.setFzaiokijissekiflg(meisaiBean.getFzaiokijissekiflgmei());
            displayDto.setKaishun1n2rittorukaishu(meisaiBean.getKaishun1n2rittorukaishu());
            displayDto.setKaishun8rittorukaishu(meisaiBean.getKaishun7rittorukaishu());
            // 配布物フラグ、配布物コード、配布物名称、配布物AP基本コード、配布物ショップコード、受注伝票明細番号、データ作成元区分（各１～４）
            displayDto.setHaifuflg1(meisaiBean.getHaifuflg1());
            displayDto.setHaifubutsucd1(meisaiBean.getHaifubutsucd1());
            displayDto.setHaifubutsunm1(meisaiBean.getHaifubutsunm1());
            displayDto.setHaifubutsuapkihoncd1(meisaiBean.getHaifubutsuapkihoncd1());
            displayDto.setHaifubutsushopcd1(meisaiBean.getHaifubutsushopcd1());
            displayDto.setAcptdnpymeisaino1(meisaiBean.getAcptdnpymeisaino1());
            displayDto.setDatsksmotokbn1(meisaiBean.getDatsksmotokbn1());
            displayDto.setHaifuflg2(meisaiBean.getHaifuflg2());
            displayDto.setHaifubutsucd2(meisaiBean.getHaifubutsucd2());
            displayDto.setHaifubutsunm2(meisaiBean.getHaifubutsunm2());
            displayDto.setHaifubutsuapkihoncd2(meisaiBean.getHaifubutsuapkihoncd2());
            displayDto.setHaifubutsushopcd2(meisaiBean.getHaifubutsushopcd2());
            displayDto.setAcptdnpymeisaino2(meisaiBean.getAcptdnpymeisaino2());
            displayDto.setDatsksmotokbn2(meisaiBean.getDatsksmotokbn2());
            displayDto.setHaifuflg3(meisaiBean.getHaifuflg3());
            displayDto.setHaifubutsucd3(meisaiBean.getHaifubutsucd3());
            displayDto.setHaifubutsunm3(meisaiBean.getHaifubutsunm3());
            displayDto.setHaifubutsuapkihoncd3(meisaiBean.getHaifubutsuapkihoncd3());
            displayDto.setHaifubutsushopcd3(meisaiBean.getHaifubutsushopcd3());
            displayDto.setAcptdnpymeisaino3(meisaiBean.getAcptdnpymeisaino3());
            displayDto.setDatsksmotokbn3(meisaiBean.getDatsksmotokbn3());
            displayDto.setHaifuflg4(meisaiBean.getHaifuflg4());
            displayDto.setHaifubutsucd4(meisaiBean.getHaifubutsucd4());
            displayDto.setHaifubutsunm4(meisaiBean.getHaifubutsunm4());
            displayDto.setHaifubutsuapkihoncd4(meisaiBean.getHaifubutsuapkihoncd4());
            displayDto.setHaifubutsushopcd4(meisaiBean.getHaifubutsushopcd4());
            displayDto.setAcptdnpymeisaino4(meisaiBean.getAcptdnpymeisaino4());
            displayDto.setDatsksmotokbn4(meisaiBean.getDatsksmotokbn4());
            keyDto.setShuseiarinashiflg(meisaiBean.getShuseiarinashiflg());
        }
    }

    /**
     * 受注配達明細リスト設定
     * 
     * @param keyDto C0815W21KeyDto
     */
    private void setAcptmeisaiList(C0815W21KeyDto keyDto) {
        S04T002TC0815W21Entity s04t002tC0815W21Entity = new S04T002TC0815W21Entity();
        s04t002tC0815W21Entity.setKakakugetbasedd(keyDto.getDlvrydd());
        s04t002tC0815W21Entity.setApkihoncd(keyDto.getApkihoncd());
        s04t002tC0815W21Entity.setShopcd(keyDto.getShopcd());
        s04t002tC0815W21Entity.setAcptdnpyno(keyDto.getDnpyno());
        s04t002tC0815W21Entity.setEdano(Short.valueOf(keyDto.getEdano()));

        List<S04T002TC0815W21Entity> acptmeisaiList =
            s04t002tDao.selectByC0815W21AcptmeisaiList(s04t002tC0815W21Entity);

        S02M013TEntityKey key = new S02M013TEntityKey();
        key.setApkihoncd(keyDto.getApkihoncd());
        key.setShopcd(keyDto.getShopcd());
        key.setTokuisakicd(keyDto.getTokuisakicd());
        S02M013TEntity s02m013tEntity = s02m013tDao.selectByPrimaryKey(key);

        List<S04T002TC0815W21Entity> setList = new ArrayList<>();
        if (!ListUtils.isEmpty(acptmeisaiList)) {
            for (S04T002TC0815W21Entity entity : acptmeisaiList) {

                if (CommonConstants.FLG_ON.equals(entity.getNyuryokumeisaiflg())
                    && CommonConstants.KBN_MEISAI_SHOHIN.equals(entity.getMeisaikbn())
                    && CommonConstants.FLG_ON.equals(s02m013tEntity.getRknkindtokubetsuuntflg())) {
                    if (CommonConstants.FLG_ON.equals(entity.getTanpinmeisaiflg())) {

                        // 得意先の商品別特別単価を取得
                        S02M009TEntity tokubetsuuntKey = new S02M009TEntity();
                        tokubetsuuntKey.setApkihoncd(keyDto.getApkihoncd());
                        tokubetsuuntKey.setShopcd(keyDto.getShopcd());
                        tokubetsuuntKey.setShncd(entity.getShncd());
                        tokubetsuuntKey.setTokuisakicd(keyDto.getTokuisakicd());
                        List<S02M009TEntity> shnTokubetsuUntList =
                            s02m009tDao.getShnTokubetsuUnt(tokubetsuuntKey);
                        entity.setTokubetsuUntJson(
                            convertTokubetsuuntList(shnTokubetsuUntList, entity.getShncd()));
                    } else {

                        // 得意先のセット商品別特別単価を取得
                        S02M066TEntityKey tokubetsuuntKey = new S02M066TEntityKey();
                        tokubetsuuntKey.setApkihoncd(keyDto.getApkihoncd());
                        tokubetsuuntKey.setShopcd(keyDto.getShopcd());
                        tokubetsuuntKey.setStshncd(entity.getShncd());
                        tokubetsuuntKey.setTokuisakicd(keyDto.getTokuisakicd());
                        List<S02M066TEntity> s02m066tEntityList =
                            s02m066tDao.getStShnTokubetsuUnt(tokubetsuuntKey);
                        entity.setTokubetsuUntJson(
                            convertTokubetsuuntList(s02m066tEntityList, entity.getShncd()));
                    }
                }
                setList.add(entity);
            }
        }

        keyDto.setAcptmeisaiList(setList);
    }

    /**
     * 受注明細情報設定
     * 
     * @param displayDto C0815W21DisplayDto
     * @param keyDto C0815W21KeyDto
     */
    private void setAcptmeisaiData(C0815W21DisplayDto displayDto, C0815W21KeyDto keyDto) {
        if (!CommonConstants.FLG_ON.equals(keyDto.getShuseiarinashiflg())) {
            // 受注明細一覧設定
            setAcptmeisaiList(keyDto);
            c0815W00Session.getMeisaiBeanList().get(Integer.parseInt(keyDto.getSelfposition()))
                .setAcptDlvryMeisaiList(
                    createMeisaiDtoList(keyDto.getAcptmeisaiList(), displayDto));
        }
    }

    /**
     * 受注伝票情報取得
     * 
     * @param dto C0815W21KeyDto
     * @return S04T001TC0815W21Entity
     */
    private S04T001TC0815W21Entity getAcptdnpyData(C0815W21KeyDto dto) {

        S04T001TC0815W21Entity entityKey = new S04T001TC0815W21Entity();
        // AP基本コード
        entityKey.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        entityKey.setShopcd(dto.getShopcd());
        // 受注伝票番号
        entityKey.setAcptdnpyno(dto.getDnpyno());
        // 枝番号
        entityKey.setEdano(Short.valueOf(dto.getEdano()));
        // 得意先コード
        entityKey.setTokuisakicd(dto.getTokuisakicd());
        // 運用日付
        entityKey.setUnyoymd(dto.getUnyoymd());
        // システムAP基本コード
        entityKey.setSystmapkihoncd(CommonConstants.APCD_000);
        // システムショップコード
        entityKey.setSystmshopcd(CommonConstants.SYS_SHOPCD_000);
        // 伝票ステータス設定番号
        entityKey.setDnpystsno(CommonConstants.SYS_SETTEINO_026);
        // システムレコード区分
        entityKey.setSystmrecodkbn(CommonConstants.RECORD_KBN_M);
        // データ作成元区分設定番号
        entityKey.setDatsksmotokbnno(CommonConstants.SYS_SETTEINO_093);
        // 受注区分設定番号
        entityKey.setAcptkbnno(CommonConstants.SYS_SETTEINO_052);
        // 受注区分詳細設定番号
        entityKey.setAcptkbnshsno(CommonConstants.SYS_SETTEINO_053);
        // 納品先情報1設定番号
        entityKey.setNohinsakijohon1no(CommonConstants.SYS_SETTEINO_056);
        // 納品先情報２設定番号
        entityKey.setNohinsakijohon2no(CommonConstants.SYS_SETTEINO_057);
        // 配達希望時間区分設定番号
        entityKey.setDlvrykibojikankbn(CommonConstants.SYS_SETTEINO_012);
        // 受注予約フラグ
        entityKey.setAcptyoyakuflg("false");

        return s04t001tDao.selectByC0815W21AcptdnpyData(entityKey);
    }

    /**
     * 修正不可判定
     * 
     * @param dto C0815W21DisplayDto
     */
    private void unmodifiableDecision(C0815W21DisplayDto displayDto) {
        // 売上伝票
        S06T001TEntityExample example = new S06T001TEntityExample();
        // クライテリアの生成
        S06T001TEntityExample.Criteria criteria = example.createCriteria();
        criteria.andApkihoncdEqualTo(displayDto.getApkihoncd());
        criteria.andShopcdEqualTo(displayDto.getShopcd());
        if (!StringUtils.isEmpty(displayDto.getUridnpyno())) {
            criteria.andUridnpynoEqualTo(displayDto.getUridnpyno());
        } else {
            criteria.andUridnpynoIsNull();
        }
        // SELECT実行
        List<S06T001TEntity> s06m001tResult = s06t001tDao.selectByExample(example);
        if (!ListUtils.isEmpty(s06m001tResult)) {
            // 入金消込判定
            if (nyukinkeshikomizumiDecision(displayDto, s06m001tResult)) {
                displayDto.setShuseihuka(CommonConstants.FLG_ON);
            }
            // 請求済み判定
            if (skykeshikomizumiDecision(displayDto, s06m001tResult)) {
                displayDto.setShuseihuka(CommonConstants.FLG_ON);
            }
        }
    }

    /**
     * 入金消込済み判定
     * 
     * @param dto C0815W21DisplayDto
     * @param s06m001tEntityList List<S06T001TEntity>
     * @return 判定結果
     */
    private boolean nyukinkeshikomizumiDecision(C0815W21DisplayDto dto,
        List<S06T001TEntity> s06m001tEntityList) {
        List<S07T001TEntity> result = new ArrayList<>();
        String kaisyuhohokbn = dto.getKaishuhohokbn();
        if (CommonConstants.KBN_KAISHUHOHO_DAIBIKI.equals(kaisyuhohokbn)) {
            S07T001TEntityExample example = new S07T001TEntityExample();
            // クライテリア(SQL条件)の生成
            S07T001TEntityExample.Criteria criteria = example.createCriteria();
            criteria.andApkihoncdEqualTo(dto.getApkihoncd());
            criteria.andShopcdEqualTo(dto.getShopcd());
            if (!StringUtils.isEmpty(dto.getSkysakicd())) {
                criteria.andSkysakicdEqualTo(dto.getSkysakicd());
            } else {
                criteria.andSkysakicdIsNull();
            }
            criteria.andTokuisakicdEqualTo(dto.getTokuisakicd());
            if (!StringUtils.isEmpty(dto.getDlvrydd())) {
                criteria.andShimedydmEqualTo(dto.getDlvrydd().substring(0, 6));
                criteria.andShimeddEqualTo(dto.getDlvrydd().substring(6, 8));
            } else {
                criteria.andShimedydmIsNull();
                criteria.andShimeddIsNull();
            }
            criteria.andSkyprcsjissikbnEqualTo(
                Short.parseShort(CommonConstants.SKYPRCSJISSIKBN_DAIBIKI));
            criteria.andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

            result = s07t001tDao.selectByExample(example);
        } else {
            if (!ListUtils.isEmpty(s06m001tEntityList)) {
                S07T001TEntityExample example = new S07T001TEntityExample();
                // クライテリア(SQL条件)の生成
                S07T001TEntityExample.Criteria criteria = example.createCriteria();
                criteria.andApkihoncdEqualTo(dto.getApkihoncd());
                criteria.andShopcdEqualTo(dto.getShopcd());
                if (!StringUtils.isEmpty(dto.getSkysakicd())) {
                    criteria.andSkysakicdEqualTo(dto.getSkysakicd());
                } else {
                    criteria.andSkysakicdIsNull();
                }
                criteria.andTokuisakicdEqualTo(dto.getTokuisakicd());
                if (!StringUtils.isEmpty(s06m001tEntityList.get(0).getSkyshimedydm())) {
                    criteria.andShimedydmEqualTo(s06m001tEntityList.get(0).getSkyshimedydm());
                } else {
                    criteria.andShimedydmIsNull();
                }
                if (!StringUtils.isEmpty(s06m001tEntityList.get(0).getSkyshimedd())) {
                    criteria.andShimeddEqualTo(s06m001tEntityList.get(0).getSkyshimedd());
                } else {
                    criteria.andShimeddIsNull();
                }
                criteria.andSkyprcsjissikbnEqualTo(
                    Short.parseShort(CommonConstants.SKYPRCSJISSIKBN_TSUKIJIME));
                criteria.andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

                result = s07t001tDao.selectByExample(example);
            }
        }
        if (!ListUtils.isEmpty(result) && result.get(0).getKeshikomikin() != 0) {
            return true;
        }

        return false;
    }

    /**
     * 請求済み判定
     * 
     * @param dto C0815W21DisplayDto
     * @param s06m001tEntityList List<S06T001TEntity>
     * @return 判定結果
     */
    private boolean skykeshikomizumiDecision(C0815W21DisplayDto dto,
        List<S06T001TEntity> s06m001tEntityList) {
        String skyprcsjissidd =
            !ListUtils.isEmpty(s06m001tEntityList) ? s06m001tEntityList.get(0).getSkyprcsjissidd()
                : SystemConstants.WORD_BLANK;

        // 売上テーブルの請求処理実施日が「00000000」（未締め）以外である。
        if (CommonConstants.DEF_DATE_00000000.equals(skyprcsjissidd)) {
            return false;
        }

        // SQL条件の生成
        S07T001TC0815W21Entity key = new S07T001TC0815W21Entity();
        key.setApkihoncd(dto.getApkihoncd());
        key.setShopcd(dto.getShopcd());
        key.setSkysakicd(dto.getSkysakicd());
        key.setTokuisakicd(dto.getTokuisakicd());
        key.setYukokbn(CommonConstants.YUKOKBN_1);

        // 請求残高テーブルの締年月日最大値を取得する
        String maxShimedydm = s07t001tDao.selectByMaxShimedydm(key);
        if (StringUtils.isEmpty(maxShimedydm)) {
            return true;
        }

        // 請求残高テーブル．締日最大値≠売上テーブル．請求締日： 請求済
        StringBuilder skyshimedd = new StringBuilder();
        skyshimedd.append(
            !ListUtils.isEmpty(s06m001tEntityList) ? s06m001tEntityList.get(0).getSkyshimedydm()
                : SystemConstants.WORD_BLANK);
        skyshimedd.append(
            !ListUtils.isEmpty(s06m001tEntityList) ? s06m001tEntityList.get(0).getSkyshimedd()
                : SystemConstants.WORD_BLANK);
        if (!maxShimedydm.equals(skyshimedd.toString())) {
            return true;
        }

        return false;
    }

    /**
     * 修正不可判定（インボイス考慮）
     * 
     * @param dto C0815W21DisplayDto
     */
    private void unmodifiableDecisionForInvoice(C0815W21DisplayDto displayDto) {
        // 売上テーブル検索
        // 検索条件
        // ・AP基本コード
        // ・ショップコード
        // ・有効区分が有効
        // ・売上伝票番号が受注配達テーブル.売上伝票番号と同じ
        S06T001TEntityExample example = new S06T001TEntityExample();
        example.createCriteria()
            // AP基本コード
            .andApkihoncdEqualTo(displayDto.getApkihoncd())
            // ショップコード
            .andShopcdEqualTo(displayDto.getShopcd())
            // 売上伝票番号
            .andUridnpynoEqualTo(displayDto.getUridnpyno())
            // 有効区分が有効
            .andYukokbnEqualTo(CommonConstants.FLG_ON);
        List<S06T001TEntity> s06m001tResult = s06t001tDao.selectByExample(example);
        // 検索結果に請求締年月がNULLまたは「000000」以外のデータが含むか
        boolean isExistsShimezumi =
            s06m001tResult.stream().anyMatch(e -> !(StringUtils.isEmpty(e.getSkyshimedydm())
                || Objects.equals("000000", e.getSkyshimedydm())));
        if (isExistsShimezumi) {
            // 含む場合は修正不可とする
            displayDto.setShuseihuka(CommonConstants.FLG_ON);
        }
    }

    /**
     * 受託伝票判定の設定
     * 
     * @param keyDto C0815W21KeyDto
     * @param displayDto C0815W21DisplayDto
     */
    private void setJutakuDenpyoDecision(C0815W21KeyDto keyDto, C0815W21DisplayDto displayDto) {
        S02M013TEntityExample example = new S02M013TEntityExample();
        // クライテリア(SQL条件)の生成
        S02M013TEntityExample.Criteria criteria = example.createCriteria();
        criteria.andApkihoncdEqualTo(displayDto.getApkihoncd());
        criteria.andShopcdEqualTo(displayDto.getShopcd());
        criteria.andTokuisakicdEqualTo(displayDto.getTokuisakicd());

        List<S02M013TEntity> result = s02m013tDao.selectByExample(example);
        String cstKbn = "";
        if (!ListUtils.isEmpty(result)) {
            cstKbn = result.get(0).getJutakudlvrycstkbn();
        }
        if ((CommonConstants.JUTAKUDLVRYCSTKBN_5.equals(cstKbn)
            || CommonConstants.JUTAKUDLVRYCSTKBN_6.equals(cstKbn))
            && (!StringUtils.isEmpty(displayDto.getItakuaiteapkihoncd())
                && !StringUtils.isEmpty(displayDto.getItakuaiteshopcd())
                && !StringUtils.isEmpty(displayDto.getItakuaiteacptdnpyno())
                && !StringUtils.isEmpty(displayDto.getItakuaiteedano()))) {
            displayDto.setJutakudenpyoflg(CommonConstants.FLG_ON);
        }
    }

    /**
     * 価格再編集
     * 
     * @param displayDto C0815W21DisplayDto
     * @param meisaiDto C0815W11AcptDlvryMeisaiDto
     * @param entity S04T002TC0815W21Entity
     */
    private void setReEditUnt(C0815W21DisplayDto displayDto, C0815W11AcptDlvryMeisaiDto meisaiDto,
        S04T002TC0815W21Entity entity) {
        if (CommonConstants.FLG_ON.equals(entity.getNyuryokumeisaiflg())
            && CommonConstants.KBN_MEISAI_SHOHIN.equals(entity.getMeisaikbn())) {
            if (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(displayDto.getDlvrykbn())) {
                meisaiDto.setHyojununt(this.getCurrentValue(entity.getGenzmochikaeriprice(),
                    entity.getShinmochikaeriprice(), entity.getHyojunpricekirikaedd(),
                    displayDto.getDlvryytidd()));
            } else {
                meisaiDto.setHyojununt(
                    this.getCurrentValue(entity.getGenzhyojunprice(), entity.getShinhyojunprice(),
                        entity.getHyojunpricekirikaedd(), displayDto.getDlvryytidd()));
            }
        }
    }

    /**
     * 現在標準単価取得
     * 
     * @param displayDto C0815W21DisplayDto
     * @param entity S04T002TC0815W21Entity
     * @return String
     */
    private String getCurrentHyojununt(String dlvrykbn, String dlvryytidd,
        S02V001TC0815W21Entity entity) {
        if (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(dlvrykbn)) {
            return this.getCurrentValue(entity.getGenzmochikaeriprice(),
                entity.getShinmochikaeriprice(), entity.getHyojunpricekirikaedd(), dlvryytidd);
        } else {
            return this.getCurrentValue(entity.getGenzhyojunprice(), entity.getShinhyojunprice(),
                entity.getHyojunpricekirikaedd(), dlvryytidd);
        }
    }

    /**
     * 現在受注単価設定
     * 
     * @param form C0815W21Form
     * @param entity S02V001TC0815W21Entity
     * @param meisaiDto C0815W11AcptDlvryMeisaiDto
     */
    private void setCurrentAcptUnt(C0815W21Form form, S02V001TC0815W21Entity entity,
        C0815W11AcptDlvryMeisaiDto meisaiDto) {
        C0815W21BaseBean baseBean = form.getBaseBean();
        String dlvryKbn = baseBean.getDlvrykbn();
        // 配達日がセットされていない場合は配達予定日をセット
        String targetDD = StringUtil.isEmpty(baseBean.getDlvrydd())
            ? DateTimeUtil.toNoSlashedDate(baseBean.getDlvryytidd())
            : DateTimeUtil.toNoSlashedDate(baseBean.getDlvrydd());
        // 計上区分＝「経費」の時は受注単価をショップ商品マスタの在庫単価にする
        if (CommonConstants.KBN_KEIJO_KEIHI.equals(meisaiDto.getKeijokbn())) {
            meisaiDto.setAcptunt(BusinessUtil.getString(meisaiDto.getZaikount(), "0"));

            // 計上区分＝「売上」「キャンペーン」の時は得意先別商品別、販促単価の適用判定を行う
        } else if (CommonConstants.KBN_KEIJO_URIAGE.equals(meisaiDto.getKeijokbn())
            || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(meisaiDto.getKeijokbn())) {
            String hansokuunt = getCampaignTanka(dlvryKbn, targetDD, meisaiDto);
            String tokuisakiunt = getTokuisakiTanka(dlvryKbn, targetDD, meisaiDto);
            String tokuisakisetunt = getTokuisakiSetTanka(dlvryKbn, targetDD, meisaiDto);

            // 得意先別商品別単価の適用期間内の場合は受注単価を得意先別商品別単価にする
            if (!StringUtils.isEmpty(tokuisakiunt)) {
                meisaiDto.setKeijokbn(CommonConstants.KBN_KEIJO_URIAGE);
                meisaiDto.setAcptunt(tokuisakiunt);

                // 得意先別セット商品別単価の適用期間内の場合は受注単価を得意先別セット商品別単価にする
            } else if (!StringUtils.isEmpty(tokuisakisetunt)) {
                meisaiDto.setKeijokbn(CommonConstants.KBN_KEIJO_URIAGE);
                meisaiDto.setAcptunt(tokuisakisetunt);

            } else {
                // 計上区分がキャンペーン（お気に入りマスタからの登録）の場合は単価のみ設定
                if (CommonConstants.KBN_KEIJO_CAMPAIGN.equals(meisaiDto.getKeijokbn())) {
                    // 販促期間内は販促価格をセット
                    if (!StringUtils.isEmpty(hansokuunt)) {
                        meisaiDto.setAcptunt(hansokuunt);
                    }
                    // 販促期間外は0円をセット
                    else {
                        meisaiDto.setAcptunt("0");
                    }
                } else {
                    // 行挿入や行追加などの計上区分：売上の場合、販促設定適用範囲内ならキャンペーンに設定する
                    // 販促期間内＋販促設定：対象内
                    if (!StringUtils.isEmpty(hansokuunt)) {
                        // 販促優先区分＝"する"の場合は計上区分をキャンペーン、販促価格に設定
                        if (CommonConstants.FLG_ON.equals(meisaiDto.getHansokuyusenkbn())) {
                            meisaiDto.setKeijokbn(CommonConstants.KBN_KEIJO_CAMPAIGN);
                            meisaiDto.setAcptunt(hansokuunt);
                        }
                        // 販促優先区分＝"しない"の場合、売上価格のみ設定
                        else {
                            // 配達区分＝「持ち帰り」の場合
                            if (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(dlvryKbn)) {
                                meisaiDto.setAcptunt(
                                    this.getCurrentValue(meisaiDto.getGenzshopmochikaeriprice(),
                                        meisaiDto.getShinshopmochikaeriprice(),
                                        meisaiDto.getShoppricekirikaedd(), targetDD));

                                // 配達区分＝「配達」の場合
                            } else {
                                meisaiDto.setAcptunt(this.getCurrentValue(
                                    meisaiDto.getGenzshopprice(), meisaiDto.getShinshopprice(),
                                    meisaiDto.getShoppricekirikaedd(), targetDD));
                            }
                        }
                    } else {
                        // 販促期間外＋販促設定：対象外 売上価格を設定
                        // 配達区分＝「持ち帰り」の場合
                        if (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(dlvryKbn)) {
                            meisaiDto.setAcptunt(
                                this.getCurrentValue(meisaiDto.getGenzshopmochikaeriprice(),
                                    meisaiDto.getShinshopmochikaeriprice(),
                                    meisaiDto.getShoppricekirikaedd(), targetDD));

                            // 配達区分＝「配達」の場合
                        } else {
                            meisaiDto.setAcptunt(this.getCurrentValue(meisaiDto.getGenzshopprice(),
                                meisaiDto.getShinshopprice(), meisaiDto.getShoppricekirikaedd(),
                                targetDD));
                        }
                    }
                }
            }

        } else {
            // 計上区分＝上記以外の時は０にする
            meisaiDto.setAcptunt("0");
        }

        meisaiDto.setHanbaiprice(meisaiDto.getAcptunt());
        meisaiDto.setShokidsplyprice(meisaiDto.getAcptunt());

        // 消費税、金額の算出
        String[] priceList = null;
        if (CommonConstants.KBN_TAX_KOMEISAI.equals(meisaiDto.getShntaxkbn())) {
            priceList =
                kouseihinService.getKouseihinTax(baseBean.getApkihoncd1(), baseBean.getShopcd(),
                    meisaiDto.getShncd(), String.valueOf(meisaiDto.getAcptsuryo()), form.getKttcd(),
                    meisaiDto.getKeijokbn(), targetDD);
        } else {
            S02M046TEntityKey key = new S02M046TEntityKey();
            key.setApkihoncd(baseBean.getApkihoncd1());
            key.setShopcd(baseBean.getShopcd());
            S02M046TEntity shopMaster = s02m046tDao.selectByPrimaryKey(key);

            priceList = taxService.getPriceList(meisaiDto.getAcptunt(), meisaiDto.getAcptsuryo(),
                getShnTaxKbnByKeijoKbn(meisaiDto.getKeijokbn(), meisaiDto.getShntaxkbn()),
                baseBean.getTokuisakikbn(), targetDD, shopMaster);
        }
        meisaiDto.setAcptuntshohitaxkin(priceList[0]);
        meisaiDto.setAcptshohitaxkin(priceList[1]);
        meisaiDto.setAcptkin(priceList[2]);
    }

    /**
     * キャンペーン単価を取得
     * 
     * @param dlvrykbn String
     * @param dlvryytidd String
     * @param meisaiDto C0815W11AcptDlvryMeisaiDto
     * @return String
     */
    private String getCampaignTanka(String dlvrykbn, String dlvryytidd,
        C0815W11AcptDlvryMeisaiDto meisaiDto) {
        // 販促設定：対象外の場合はキャンペーン対象外とする
        if (CommonConstants.FLG_OFF.equals(meisaiDto.getHansokukbn())) {
            return "";
        }

        // 適用期間の判定
        if (BusinessUtil.checkDatePeriod(dlvryytidd, meisaiDto.getHansokustrtdd(),
            meisaiDto.getHansokushrydd())) {
            return (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(dlvrykbn))
                ? meisaiDto.getHansokumochikaeriprice()
                : meisaiDto.getHansokuprice();
        } else {
            return "";
        }
    }

    /**
     * 得意先別商品別単価を取得
     * 
     * @param dlvryKbn String
     * @param dlvryYtiDd String
     * @param meisaiDto C0815W11AcptDlvryMeisaiDto
     * @return String
     */
    private String getTokuisakiTanka(String dlvryKbn, String dlvryYtiDd,
        C0815W11AcptDlvryMeisaiDto meisaiDto) {
        // 適用期間の判定
        if (BusinessUtil.checkDatePeriod(dlvryYtiDd, meisaiDto.getTtpricestrtdd(),
            meisaiDto.getTtpriceshrydd())) {
            return (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(dlvryKbn))
                ? meisaiDto.getTtmochikaeriprice()
                : meisaiDto.getTtprice();
        } else {
            return "";
        }
    }

    /**
     * 得意先別セット商品別単価を取得
     * 
     * @param dlvryKbn String
     * @param dlvryYtiDd String
     * @param meisaiDto C0815W11AcptDlvryMeisaiDto
     * @return String
     */
    private String getTokuisakiSetTanka(String dlvryKbn, String dlvryYtiDd,
        C0815W11AcptDlvryMeisaiDto meisaiDto) {
        // 適用期間の判定
        if (BusinessUtil.checkDatePeriod(dlvryYtiDd, meisaiDto.getSet_tekiyostrtdd(),
            meisaiDto.getSet_tekiyoshrydd())) {
            return (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(dlvryKbn))
                ? meisaiDto.getSet_mochikaeriprice()
                : meisaiDto.getSet_dlvryprice();
        } else {
            return "";
        }
    }

    /**
     * 商品税区分取得（計上区分の判断有り）
     * 
     * @param keijokbn 計上区分
     * @param shntaxkbn 商品税区分
     * @return String 結果
     */
    private static String getShnTaxKbnByKeijoKbn(String keijokbn, String shntaxkbn) {
        return CommonConstants.KBN_KEIJO_KEIHI.equals(keijokbn) ? CommonConstants.KBN_TAX_HIKAZEI
            : shntaxkbn;
    }

    /**
     * 受注配達明細エンティティから明細リストを作成する（初回修正時）
     * 
     * @param entityList List<S04T002TC0815W21Entity>
     * @param displayDto C0815W21DisplayDto
     * @return List<C0815W11AcptDlvryMeisaiDto>
     */
    private List<C0815W11AcptDlvryMeisaiDto> createMeisaiDtoList(
        List<S04T002TC0815W21Entity> entityList, C0815W21DisplayDto displayDto) {
        List<C0815W11AcptDlvryMeisaiDto> meisaiDtoList = new ArrayList<>();

        int haihuCount = 0;
        for (S04T002TC0815W21Entity entity : entityList) {
            if (!(CommonConstants.NYURYOKUMEISAIFLG_0.equals(entity.getNyuryokumeisaiflg())
                && CommonConstants.TANPINMEISAIFLG_1.equals(entity.getTanpinmeisaiflg()))) {
                C0815W11AcptDlvryMeisaiDto meisaiDto = new C0815W11AcptDlvryMeisaiDto();
                if (CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(entity.getMeisaikbn())) {
                    if (haihuCount == 0) {
                        displayDto.setHaifuflg1(CommonConstants.FLG_ON);
                        displayDto.setHaifubutsucd1(entity.getHaifubutsucd());
                        displayDto.setHaifubutsunm1(entity.getHaifubutsunm());
                        displayDto.setHaifubutsuapkihoncd1(entity.getHaifubutsuapkihoncd());
                        displayDto.setHaifubutsushopcd1(entity.getHaifubutsushopcd());
                        displayDto.setAcptdnpymeisaino1(entity.getAcptdnpymeisaiedano().toString());
                        displayDto.setDatsksmotokbn1(entity.getDatsksmotokbn());
                    } else if (haihuCount == 1) {
                        displayDto.setHaifuflg2(CommonConstants.FLG_ON);
                        displayDto.setHaifubutsucd2(entity.getHaifubutsucd());
                        displayDto.setHaifubutsunm2(entity.getHaifubutsunm());
                        displayDto.setHaifubutsuapkihoncd2(entity.getHaifubutsuapkihoncd());
                        displayDto.setHaifubutsushopcd2(entity.getHaifubutsushopcd());
                        displayDto.setAcptdnpymeisaino2(entity.getAcptdnpymeisaiedano().toString());
                        displayDto.setDatsksmotokbn2(entity.getDatsksmotokbn());
                    } else if (haihuCount == 2) {
                        displayDto.setHaifuflg3(CommonConstants.FLG_ON);
                        displayDto.setHaifubutsucd3(entity.getHaifubutsucd());
                        displayDto.setHaifubutsunm3(entity.getHaifubutsunm());
                        displayDto.setHaifubutsuapkihoncd3(entity.getHaifubutsuapkihoncd());
                        displayDto.setHaifubutsushopcd3(entity.getHaifubutsushopcd());
                        displayDto.setAcptdnpymeisaino3(entity.getAcptdnpymeisaiedano().toString());
                        displayDto.setDatsksmotokbn3(entity.getDatsksmotokbn());
                    } else if (haihuCount == 3) {
                        displayDto.setHaifuflg4(CommonConstants.FLG_ON);
                        displayDto.setHaifubutsucd4(entity.getHaifubutsucd());
                        displayDto.setHaifubutsunm4(entity.getHaifubutsunm());
                        displayDto.setHaifubutsuapkihoncd4(entity.getHaifubutsuapkihoncd());
                        displayDto.setHaifubutsushopcd4(entity.getHaifubutsushopcd());
                        displayDto.setAcptdnpymeisaino4(entity.getAcptdnpymeisaiedano().toString());
                        displayDto.setDatsksmotokbn4(entity.getDatsksmotokbn());
                    }
                    haihuCount++;
                } else {
                    String suryo;
                    String hanbaiprice;
                    String kin;
                    String shohitaxkin;
                    String untshohitaxkin;
                    if (CommonConstants.DNPYSTS_11.equals(displayDto.getDnpysts())) {
                        suryo = entity.getAcptsuryo();
                        hanbaiprice = entity.getAcptunt();
                        kin = entity.getAcptkin();
                        shohitaxkin = entity.getAcptshohitaxkin();
                        untshohitaxkin = entity.getAcptuntshohitaxkin();
                    } else {
                        suryo = entity.getNohinsuryo();
                        hanbaiprice = entity.getNohinunt();
                        kin = entity.getNohinkin();
                        shohitaxkin = entity.getNohinshohitaxkin();
                        untshohitaxkin = entity.getNohinuntshohitaxkin();
                    }
                    meisaiDto.setApkihoncd(entity.getApkihoncd()); // AP基本コード
                    meisaiDto.setShopcd(entity.getShopcd()); // ショップコード
                    meisaiDto.setAcptdnpyno(entity.getAcptdnpyno()); // 受注伝票番号
                    meisaiDto.setEdano(entity.getEdano()); // 枝番号
                    meisaiDto.setGenzhyojunprice(BusinessUtil.getString(entity.getGenzhyojunprice(),
                        BigDecimal.ZERO.toString())); // 現在標準価格
                    meisaiDto.setShinhyojunprice(BusinessUtil.getString(entity.getShinhyojunprice(),
                        BigDecimal.ZERO.toString())); // 新標準価格
                    meisaiDto.setGenzhyojunmochikaeriprice(BusinessUtil
                        .getString(entity.getGenzmochikaeriprice(), BigDecimal.ZERO.toString())); // 現在標準持ち帰り価格
                    meisaiDto.setShinhyojunmochikaeriprice(BusinessUtil
                        .getString(entity.getShinmochikaeriprice(), BigDecimal.ZERO.toString())); // 新標準持ち帰り価格
                    meisaiDto.setGenzshopmochikaeriprice(BigDecimal.ZERO.toString()); // 現在ショップ持ち帰り価格
                    meisaiDto.setShinshopmochikaeriprice(BigDecimal.ZERO.toString()); // 新ショップ持ち帰り価格
                    meisaiDto.setHansokuprice(BigDecimal.ZERO.toString()); // 販促価格
                    meisaiDto.setHansokumochikaeriprice(BigDecimal.ZERO.toString()); // 販促持ち帰り価格
                    meisaiDto.setSet_dlvryprice(BigDecimal.ZERO.toString()); // セット価格
                    meisaiDto.setSet_mochikaeriprice(BigDecimal.ZERO.toString()); // セット持ち帰り価格
                    meisaiDto.setPricekbn(entity.getPricekbn()); // 価格区分
                    meisaiDto.setHyojunpricekirikaedd(entity.getHyojunpricekirikaedd()); // 標準価格切替日
                    meisaiDto.setGenzhyojunprice(BusinessUtil.getString(entity.getGenzhyojunprice(),
                        BigDecimal.ZERO.toString())); // 現在標準価格
                    meisaiDto.setShinhyojunprice(BusinessUtil.getString(entity.getShinhyojunprice(),
                        BigDecimal.ZERO.toString())); // 新標準価格
                    meisaiDto.setGenzmochikaeriprice(BusinessUtil
                        .getString(entity.getGenzmochikaeriprice(), BigDecimal.ZERO.toString())); // 現在持ち帰り価格
                    meisaiDto.setShinmochikaeriprice(BusinessUtil
                        .getString(entity.getShinmochikaeriprice(), BigDecimal.ZERO.toString())); // 新持ち帰り価格
                    meisaiDto.setOpnpricekirikaedd(entity.getOpnpricekirikaedd()); // オープン価格切替日
                    meisaiDto.setGenzopnprice(BusinessUtil.getString(entity.getGenzopnprice(),
                        BigDecimal.ZERO.toString())); // 現在オープン価格
                    meisaiDto.setShinopnprice(BusinessUtil.getString(entity.getShinopnprice(),
                        BigDecimal.ZERO.toString())); // 新オープン価格
                    meisaiDto.setGenzopnmochikaeriprice(BusinessUtil
                        .getString(entity.getGenzopnmochikaeriprice(), BigDecimal.ZERO.toString())); // 現在オープン持ち帰り価格
                    meisaiDto.setShinopnmochikaeriprice(BusinessUtil
                        .getString(entity.getShinopnmochikaeriprice(), BigDecimal.ZERO.toString())); // 新オープン持ち帰り価格
                    meisaiDto.setHansokukbn(entity.getHansokukbn()); // 販促区分
                    meisaiDto.setHansokustrtdd(entity.getHansokustrtdd()); // 販促開始日
                    meisaiDto.setHansokushrydd(entity.getHansokushrydd()); // 販促終了日
                    meisaiDto.setHansokudlvryprice(BusinessUtil
                        .getString(entity.getHansokudlvryprice(), BigDecimal.ZERO.toString())); // 販促配達価格
                    meisaiDto.setHansokumochikaeriprice(BusinessUtil
                        .getString(entity.getHansokumochikaeriprice(), BigDecimal.ZERO.toString())); // 販促持ち帰り価格
                    meisaiDto.setHyojunprice(BusinessUtil.getString(entity.getHyojunprice(),
                        BigDecimal.ZERO.toString())); // 標準価格
                    meisaiDto.setTekiyostrtdd(entity.getTekiyostrtdd()); // 適用開始日
                    meisaiDto.setTekiyoshrydd(entity.getTekiyoshrydd()); // 適用終了日
                    meisaiDto.setDlvryprice(
                        BusinessUtil.getString(entity.getDlvryprice(), BigDecimal.ZERO.toString())); // 配達価格
                    meisaiDto.setMochikaeriprice(BusinessUtil.getString(entity.getMochikaeriprice(),
                        BigDecimal.ZERO.toString())); // 持ち帰り価格
                    meisaiDto.setStkbn(entity.getStkbn()); // セット区分
                    meisaiDto.setSet_tekiyostrtdd(entity.getSet_tekiyostrtdd()); // セット適用開始日
                    meisaiDto.setSet_tekiyoshrydd(entity.getSet_tekiyoshrydd()); // セット適用終了日
                    meisaiDto.setSet_dlvryprice(BusinessUtil.getString(entity.getSet_dlvryprice(),
                        BigDecimal.ZERO.toString())); // セット配達価格
                    meisaiDto.setSet_mochikaeriprice(BusinessUtil
                        .getString(entity.getSet_mochikaeriprice(), BigDecimal.ZERO.toString())); // セット持ち帰り価格
                    meisaiDto.setRknkindtokubetsuuntflg(entity.getRknkindtokubetsuuntflg()); // 料金種別特別単価フラグ
                    meisaiDto.setTani(entity.getUritrtskitani()); // 料金種別特別単価フラグ
                    meisaiDto.setHansokuyusenkbn(entity.getHansokuyusenkbn()); // 販促優先区分
                    meisaiDto.setZaikount(
                        BusinessUtil.getString(entity.getZaikount(), BigDecimal.ZERO.toString())); // 在庫単価
                    meisaiDto.setAcptdnpymeisaino(entity.getAcptdnpymeisaino().toString()); // 受注伝票明細番号
                    meisaiDto.setAcptdnpymeisaiedano(entity.getAcptdnpymeisaiedano().toString()); // 受注伝票明細枝番号
                    meisaiDto.setItakuaiteapkihoncd(entity.getItakuaiteapkihoncd()); // 委託相手AP基本コード
                    meisaiDto.setItakuaiteshopcd(entity.getItakuaiteshopcd()); // 委託相手ショップコード
                    meisaiDto.setItakuaiteacptdnpyno(entity.getItakuaiteacptdnpyno()); // 委託相手受注伝票番号
                    meisaiDto.setItakuaiteedano(
                        BusinessUtil.getString(entity.getItakuaiteedano(), DEFAULT_STR)); // 委託相手枝番号
                    meisaiDto.setItakuaiteacptdnpymeisaino(entity.getItakuaiteacptdnpymeisaino()); // 委託相手受注伝票明細番号
                    meisaiDto
                        .setItakuaiteacptdnpymeisaiedano(entity.getItakuaiteacptdnpymeisaiedano()); // 委託相手受注伝票明細枝番号
                    meisaiDto.setLctgrycd(entity.getLctgrycd()); // 大分類コード
                    meisaiDto.setMctgrycd(entity.getMctgrycd()); // 中分類コード
                    meisaiDto.setSctgrycd(entity.getSctgrycd()); // 小分類コード
                    meisaiDto.setShncd(entity.getShncd()); // 商品コード
                    meisaiDto.setHaifubutsuapkihoncd(entity.getHaifubutsuapkihoncd()); // 配布物AP基本コード
                    meisaiDto.setHaifubutsushopcd(entity.getHaifubutsushopcd()); // 配布物ショップコード
                    meisaiDto.setHaifubutsucd(entity.getHaifubutsucd()); // 配布物コード
                    meisaiDto.setKatasiki(entity.getKatasiki()); // 型式
                    meisaiDto.setKeijokbn(entity.getKeijokbn()); // 計上区分
                    meisaiDto.setKeijokbn2(entity.getKeijokbn()); // 計上区分2
                    meisaiDto.setAcptsuryo(entity.getAcptsuryo()); // 受注数量
                    meisaiDto.setHyojununt(entity.getHyojununt()); // 標準単価
                    meisaiDto.setAcptunt(entity.getAcptunt()); // 受注単価
                    meisaiDto.setAcptkin(entity.getAcptkin()); // 受注金額
                    meisaiDto.setAcptuntshohitaxkin(BusinessUtil
                        .getString(entity.getAcptuntshohitaxkin(), BigDecimal.ZERO.toString())); // 受注単価消費税
                    meisaiDto.setAcptshohitaxkin(BusinessUtil.getString(entity.getAcptshohitaxkin(),
                        BigDecimal.ZERO.toString())); // 受注消費税
                    meisaiDto.setItakucmpnyacptunt(entity.getItakucmpnyacptunt()); // 委託会社受注単価
                    meisaiDto.setItakucmpnyacptuntshohitaxkin(BusinessUtil.getString(
                        entity.getItakucmpnyacptuntshohitaxkin(), BigDecimal.ZERO.toString())); // 委託会社受注単価消費税金額
                    meisaiDto.setItakucmpnyacptshohitaxkin(BusinessUtil.getString(
                        entity.getItakucmpnyacptshohitaxkin(), BigDecimal.ZERO.toString())); // 委託会社消費税金額
                    meisaiDto.setNohinsuryo(entity.getNohinsuryo()); // 納品数量
                    meisaiDto.setNohinunt(entity.getNohinunt()); // 納品単価
                    meisaiDto.setNohinkin(entity.getNohinkin()); // 納品金額
                    meisaiDto.setNohinuntshohitaxkin(BusinessUtil
                        .getString(entity.getNohinuntshohitaxkin(), BigDecimal.ZERO.toString())); // 納品単価消費税金額
                    meisaiDto.setNohinshohitaxkin(BusinessUtil
                        .getString(entity.getNohinshohitaxkin(), BigDecimal.ZERO.toString())); // 納品消費税金額
                    meisaiDto.setZankaisu(entity.getZankaisu()); // 残回収
                    meisaiDto.setSoukaisu(entity.getSoukaisu()); // 総回数
                    meisaiDto.setTaxkbn(entity.getTaxkbn()); // 税区分
                    meisaiDto.setYusyokbn(entity.getYusyokbn()); // 有償区分
                    meisaiDto.setMeisaikbn(entity.getMeisaikbn()); // 明細区分
                    meisaiDto.setNyuryokumeisaiflg(entity.getNyuryokumeisaiflg()); // 入力明細区分
                    meisaiDto.setTanpinmeisaiflg(entity.getTanpinmeisaiflg()); // 単品明細フラグ
                    meisaiDto.setStshncd(entity.getStshncd()); // セット商品コード
                    meisaiDto.setMeisaibiko(entity.getMeisaibiko()); // 明細備考
                    meisaiDto.setDatsksmotokbn(entity.getDatsksmotokbn()); // データ作成元区分
                    meisaiDto.setYukokbn(entity.getYukokbn()); // 有効区分
                    meisaiDto.setKshnsystmymd(entity.getKshnsystmymd()); // 更新システム日付
                    meisaiDto.setKshnsystmjikan(entity.getKshnsystmjikan()); // 更新システム時間
                    meisaiDto.setShnapkihoncd(entity.getShnapkihoncd()); // 商品AP基本コード
                    meisaiDto.setShnnmryakusho(entity.getShnnmryakusho()); // 商品名称略称
                    meisaiDto.setIro(entity.getIro()); // 色
                    meisaiDto.setSz(entity.getSz()); // サイズ
                    meisaiDto.setUritrtskitani(entity.getUritrtskitani()); // 売上取扱単位
                    meisaiDto.setUriirisu(entity.getUriirisu()); // 売上入数
                    meisaiDto.setIrisu(entity.getUriirisu()); // 売上入数
                    meisaiDto.setUritani(entity.getUritani()); // 売上単位)
                    meisaiDto.setIrisutani(entity.getUritani()); // 入数単位
                    meisaiDto.setTaxsetteikbn(entity.getTaxsetteikbn()); // 税設定区分
                    meisaiDto.setShntaxkbn(entity.getShntaxkbn()); // 商品税区分
                    meisaiDto.setZaikoaramusuryo(entity.getZaikoaramusuryo()); // 在庫アラーム数量
                    meisaiDto.setTrtskikbn(entity.getTrtskikbn()); // 取り扱い区分
                    meisaiDto.setTokubetsuuntkbn(entity.getTokubetsuuntkbn()); // 特別単価区分
                    meisaiDto.setKeijokbnnm(entity.getKeijokbnnm()); // 計上区分名称
                    meisaiDto.setKeijokbn(entity.getKeijokbn()); // 計上区分
                    meisaiDto.setHaifubutsunm(entity.getHaifubutsunm()); // 配布物名称
                    meisaiDto.setSokazu(entity.getSokazu()); // 総数
                    meisaiDto.setShnnm(entity.getShnnm()); // 商品名称略称
                    meisaiDto.setUridnpyno(entity.getUridnpyno()); // 売上伝票
                    meisaiDto.setUridnpyedano(entity.getUridnpyedano()); // 売上伝票番号
                    meisaiDto.setUridnpymeisaino(entity.getUridnpymeisaino()); // 売上伝票明細番号
                    meisaiDto.setUridnpymeisaiedano(entity.getUridnpymeisaiedano()); // 売上伝票明細枝番号
                    meisaiDto.setToshaapkihoncd(entity.getToshaapkihoncd()); // 登録者AP基本コード
                    meisaiDto.setToshashopcd(entity.getToshashopcd()); // 登録者ショップコード
                    meisaiDto.setToshacd(entity.getToshacd()); // 登録者コード
                    meisaiDto.setInptsystmymd(entity.getInptsystmymd()); // 登録システム日付
                    meisaiDto.setInptsystmjikan(entity.getInptsystmjikan()); // 登録システム時間
                    meisaiDto.setKoshaapkihoncd(entity.getKoshaapkihoncd()); // 更新者AP基本コード
                    meisaiDto.setKoshashopcd(entity.getKoshashopcd()); // 更新者ショップコード
                    meisaiDto.setKoshacd(entity.getKoshacd()); // 更新者コード
                    meisaiDto.setKshnsystmymd(entity.getKshnsystmymd()); // 更新システム日付
                    meisaiDto.setKshnsystmjikan(entity.getKshnsystmjikan()); // 更新システム時間
                    meisaiDto.setToshamei(entity.getToshamei()); // 登録者名
                    meisaiDto.setKoshamei(entity.getKoshamei()); // 更新者名
                    meisaiDto.setSuryo(suryo); // 数量
                    meisaiDto.setKin(kin); // 金額
                    meisaiDto.setHanbaiprice(hanbaiprice); // 販売価格
                    meisaiDto.setShokidsplyprice(hanbaiprice); // 販売価格
                    meisaiDto.setShohitaxkin(shohitaxkin); // 消費税金額
                    meisaiDto.setUntshohitaxkin(untshohitaxkin); // 単価消費税金額

                    // ショップ切替日、現ショップ価格、新ショップ価格、現ショップ持ち帰り価格、新ショップ持ち帰り価格
                    if (CommonConstants.KBN_PRICE_HYOJUN.equals(entity.getPricekbn())) {
                        meisaiDto.setShoppricekirikaedd(entity.getHyojunpricekirikaedd());
                        meisaiDto.setGenzshopprice(BusinessUtil
                            .getString(entity.getGenzhyojunprice(), BigDecimal.ZERO.toString()));
                        meisaiDto.setShinshopprice(BusinessUtil
                            .getString(entity.getShinhyojunprice(), BigDecimal.ZERO.toString()));
                        meisaiDto.setGenzshopmochikaeriprice(Objects
                            .toString(entity.getGenzmochikaeriprice(), BigDecimal.ZERO.toString()));
                        meisaiDto.setShinshopmochikaeriprice(Objects
                            .toString(entity.getShinmochikaeriprice(), BigDecimal.ZERO.toString()));
                    } else {
                        meisaiDto.setShoppricekirikaedd(entity.getOpnpricekirikaedd());
                        meisaiDto.setGenzshopprice(BusinessUtil.getString(entity.getGenzopnprice(),
                            BigDecimal.ZERO.toString()));
                        meisaiDto.setShinshopprice(BusinessUtil.getString(entity.getShinopnprice(),
                            BigDecimal.ZERO.toString()));
                        meisaiDto.setGenzshopmochikaeriprice(BusinessUtil.getString(
                            entity.getGenzopnmochikaeriprice(), BigDecimal.ZERO.toString()));
                        meisaiDto.setShinshopmochikaeriprice(BusinessUtil.getString(
                            entity.getShinopnmochikaeriprice(), BigDecimal.ZERO.toString()));
                    }

                    // 販促開始日、販促終了日、販促配達価格、販促持ち帰り価格
                    if (!CommonConstants.KBN_SAKUSEIMOTO_JUCHUYOYAKU
                        .equals(entity.getDatsksmotokbn())
                        && CommonConstants.FLG_ON.equals(entity.getHansokukbn())) {
                        meisaiDto.setHansokustrtdd(entity.getHansokustrtdd());
                        meisaiDto.setHansokushrydd(entity.getHansokushrydd());
                        meisaiDto.setHansokuprice(BusinessUtil
                            .getString(entity.getHansokudlvryprice(), BigDecimal.ZERO.toString()));
                        meisaiDto.setHansokumochikaeriprice(BusinessUtil.getString(
                            entity.getHansokumochikaeriprice(), BigDecimal.ZERO.toString()));
                        meisaiDto.setHansokuyusenkbn(entity.getHansokuyusenkbn());
                    }

                    // 得意先価格開始日、得意先価格終了日、得意先価格、得意先持ち帰り価格
                    if (CommonConstants.FLG_ON.equals(entity.getRknkindtokubetsuuntflg())) {
                        if (CommonConstants.FLG_ON.equals(entity.getStkbn())) {
                            meisaiDto.setTtpricestrtdd(entity.getSet_tekiyostrtdd());
                            meisaiDto.setTtpriceshrydd(entity.getSet_tekiyoshrydd());
                            meisaiDto.setTtprice(BusinessUtil.getString(entity.getSet_dlvryprice(),
                                BigDecimal.ZERO.toString()));
                            meisaiDto.setTtmochikaeriprice(BusinessUtil.getString(
                                entity.getSet_mochikaeriprice(), BigDecimal.ZERO.toString()));
                        } else {
                            meisaiDto.setTtpricestrtdd(entity.getTekiyostrtdd());
                            meisaiDto.setTtpriceshrydd(entity.getTekiyoshrydd());
                            meisaiDto.setTtprice(BusinessUtil.getString(entity.getDlvryprice(),
                                BigDecimal.ZERO.toString()));
                            meisaiDto.setTtmochikaeriprice(Objects
                                .toString(entity.getMochikaeriprice(), BigDecimal.ZERO.toString()));
                        }
                    }
                    // 商品税区分
                    if (CommonConstants.FLG_ON.equals(entity.getTaxsetteikbn())) {
                        meisaiDto.setShntaxkbn(entity.getShntaxkbn());
                    }
                    // セット得意先価格開始日、セット得意先価格終了日、セット得意先価格、セット得意先持ち帰り価格
                    if (CommonConstants.FLG_ON.equals(entity.getRknkindtokubetsuuntflg())) {
                        meisaiDto.setSet_tekiyostrtdd(entity.getSet_tekiyostrtdd());
                        meisaiDto.setSet_tekiyoshrydd(entity.getSet_tekiyoshrydd());
                        meisaiDto.setSet_dlvryprice(BusinessUtil
                            .getString(entity.getSet_dlvryprice(), BigDecimal.ZERO.toString()));
                        meisaiDto.setSet_mochikaeriprice(Objects
                            .toString(entity.getSet_mochikaeriprice(), BigDecimal.ZERO.toString()));
                    }

                    // 標準価格再編集
                    setReEditUnt(displayDto, meisaiDto, entity);

                    meisaiDto.setTokubetsuUntJson(entity.getTokubetsuUntJson());
                    meisaiDtoList.add(meisaiDto);
                }
            }
        }
        return meisaiDtoList;
    }

    /**
     * 得意先別特別単価リストを変換（Javascriptオブジェクト形式)
     * 
     * @param tokubetsuUntList List<?>
     * @return String
     */
    private String convertTokubetsuuntList(List<?> tokubetsuUntList, String shncd) {
        StringBuilder str = new StringBuilder();
        boolean isFirst = true;
        str.append("[");
        for (Object e : tokubetsuUntList) {
            String tekiyostrtdd = "";
            String tekiyoshrydd = "";
            String dlvryprice = "";
            String mochikaeriprice = "";
            if (e.getClass().equals(S02M009TEntity.class)) {
                S02M009TEntity s02m009tEntity = (S02M009TEntity) e;
                tekiyostrtdd = s02m009tEntity.getTekiyostrtdd();
                tekiyoshrydd = s02m009tEntity.getTekiyoshrydd();
                dlvryprice = s02m009tEntity.getDlvryprice().toString();
                mochikaeriprice = s02m009tEntity.getMochikaeriprice().toString();
            } else if (e.getClass().equals(S02M066TEntity.class)) {
                S02M066TEntity s02m066tEntity = (S02M066TEntity) e;
                tekiyostrtdd = s02m066tEntity.getTekiyostrtdd();
                tekiyoshrydd = s02m066tEntity.getTekiyoshrydd();
                dlvryprice = s02m066tEntity.getDlvryprice().toString();
                mochikaeriprice = s02m066tEntity.getMochikaeriprice().toString();
            }
            if (!isFirst) {
                str.append(",");
            }
            str.append("{");
            str.append("shncd:\'").append(shncd);
            str.append("\',tekiyostrtdd:\'").append(tekiyostrtdd);
            str.append("\',tekiyoshrydd:\'").append(tekiyoshrydd);
            str.append("\',dlvryprice:\'").append(dlvryprice);
            str.append("\',mochikaeriprice:\'").append(mochikaeriprice);
            str.append("\'}");
            isFirst = false;
        }
        str.append("]");

        return str.toString();
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
