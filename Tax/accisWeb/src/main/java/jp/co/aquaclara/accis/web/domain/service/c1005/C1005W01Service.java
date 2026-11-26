package jp.co.aquaclara.accis.web.domain.service.c1005;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.constants.SystemConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M003TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M017TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T004TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S10T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S10T003TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S10T004TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S10W001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S10W003TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M003TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M003TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T004TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T004TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S10T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S10T001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S10T003TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S10T003TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S10T004TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S10T004TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S10W001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S10W003TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S10T001TC1005W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S10T001TC1005W01MeisaiEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S10T004TC1005W01KeshikomiKinEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S10W001TC1005W01IkkatsuKeshikomiEntity;
import jp.co.aquaclara.accis.common.exception.BusinessException;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.controller.commons.DbManagementSession;
import jp.co.aquaclara.accis.web.domain.dto.c1005.C1005W01HeaderDto;
import jp.co.aquaclara.accis.web.domain.dto.c1005.C1005W01MeisaiDto;
import jp.co.aquaclara.accis.web.domain.service.business.SaibanService;
import jp.co.aquaclara.accis.web.domain.service.master.S02M006TService;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import jp.co.aquaclara.accis.web.exception.dto.FormField;
import lombok.extern.slf4j.Slf4j;

/**
 * C1005W01 入金入力（入金登録）-サービスクラス
 * 
 * @author accisdev044
 *
 */
@Service
@Slf4j
public class C1005W01Service {

    /** 入金種別：売上調整 */
    private static final String NYKNKIND_SALESADJUST = "31";
    /** メッセージID KEY */
    private static final String KEY_MESSAGE_ID = "MessageId";
    /** メッセージ KEY */
    private static final String KEY_MESSAGE = "Message";
    /** 明細行数 */
    public static final int PAGEABLE_SIZE = 30;

    /** 処理区分 */
    public class ProcessType {
        /** 入金 */
        public static final String CASH_RECEIPT = "1";
        /** 調整 */
        public static final String ADJUST = "3";
        /** 取消 */
        public static final String CANCEL = "2";
    }

    /** 担当者マスタDao */
    @Autowired
    protected S02M003TDao s02m003tDao;
    /** 請求先マスタDao */
    @Autowired
    protected S02M017TDao s02m017tDao;
    /** 請求残高テーブルDao */
    @Autowired
    protected S07T001TDao s07t001tDao;
    /** 請求締処理テーブルDao */
    @Autowired
    private S07T004TDao s07t004tDao;
    /** 入金管理テーブルDao */
    @Autowired
    protected S10T003TDao s10t003tDao;
    /** 入金明細テーブルDao */
    @Autowired
    protected S10T001TDao s10t001tDao;
    /** 入金取込ワークDao */
    @Autowired
    protected S10W001TDao s10w001tDao;
    /** 入金消込ワークDao */
    @Autowired
    protected S10W003TDao s10w003tDao;
    /** 入金消込履歴テーブルDao */
    @Autowired
    protected S10T004TDao s10t004tDao;

    /** システム管理マスタサービス */
    @Autowired
    private S02M006TService s02m006tService;
    /** 採番処理サービス */
    @Autowired
    private SaibanService saibanService;

    /**
     * 伝票ヘッダ情報取得
     * 
     * @param dto 入金入力ヘッダ情報Dto
     */
    public void getVoucherHeaderInfo(C1005W01HeaderDto dto) {

        // 請求先マスタデータ取得
        S02M017TEntityKey s02M017TEntityKey = new S02M017TEntityKey();
        s02M017TEntityKey.setApkihoncd(dto.getApkihoncd());
        s02M017TEntityKey.setShopcd(dto.getShopcd());
        s02M017TEntityKey.setSkysakicd(dto.getSkysakicd());
        S02M017TEntity s02M017TEntity = s02m017tDao.selectByPrimaryKey(s02M017TEntityKey);

        // 請求先マスタ情報設定（過入金額、売上調整金額、回収担当者コード）
        if (s02M017TEntity != null) {
            dto.setKairekin(s02M017TEntity.getKairekin());
            dto.setUrichoseikin(s02M017TEntity.getUrichoseikin() * -1L);
            dto.setKaishutnshacd(s02M017TEntity.getKaishutnshacd());
        }

        // 担当者マスタデータ取得
        S02M003TEntityKey s02M003TEntityKey = new S02M003TEntityKey();
        s02M003TEntityKey.setApkihoncd(dto.getApkihoncd());
        s02M003TEntityKey.setShopcd(dto.getShopcd());
        s02M003TEntityKey.setTnshacd(dto.getKaishutnshacd());
        S02M003TEntity s02M003TEntity = s02m003tDao.selectByPrimaryKey(s02M003TEntityKey);

        // 回収担当者名設定
        if (s02M003TEntity != null) {
            dto.setKaishutnshanm(s02M003TEntity.getTnshamei());
        }
    }

    /**
     * 今回消込金額明細項目名取得
     * 
     * @param rowNum 行番号
     * @return 今回消込金額明細項目名
     */
    public FormField getKonkaikshkmkinmeiFormField(int rowNum) {
        int rowIndex = rowNum;
        int currentPage = rowNum / PAGEABLE_SIZE;
        if (rowNum > PAGEABLE_SIZE) {
            rowIndex = rowNum - (currentPage * PAGEABLE_SIZE);
        }

        String field = String.format("meisaiBeanList[%d].konkaikshkmkinmei", rowIndex - 1);
        return new FormField(field, "今回消込金額", currentPage);
    }

    /**
     * （確定時）相関チェック
     * 
     * @param dto 入金入力ヘッダ情報Dto
     * @throws BusinessException 業務例外
     */
    public void confirmCorrelationCheck(C1005W01HeaderDto dto) throws BusinessException {
        // 請求先マスタデータ取得
        S02M017TEntityKey key = new S02M017TEntityKey();
        key.setApkihoncd(dto.getApkihoncd());
        key.setShopcd(dto.getShopcd());
        key.setSkysakicd(dto.getSkysakicd());
        S02M017TEntity entity = s02m017tDao.selectByPrimaryKey(key);

        if (entity == null) {
            throw BusinessExceptionFactory.validationFailed(new FormField("ksscd", "請求先コード", 0),
                MessageID.W084, "請求先");
        } else if (!entity.getYukokbn().equals(CommonConstants.YUKOKBN_1)) {
            throw BusinessExceptionFactory.validationFailed(new FormField("ksscd", "請求先コード", 0),
                MessageID.W123, "請求先");
        }
    }

    /**
     * （検索時）相関チェック
     * 
     * @param dto 入金入力ヘッダ情報Dto
     * @return チェック結果（True:正常／False:異常）
     * @throws BusinessException 業務例外
     * @throws ParseException 型変換例外
     */
    public boolean searchCorrelationCheck(C1005W01HeaderDto dto)
        throws BusinessException, ParseException {

        // 入金伝票チェック
        String skyprcsjissidd = nyukinVoucherCheck(dto); // 請求処理実施日取得
        // 消込請求残高チェック
        clearingSeikyuBalanceCheck(dto);
        // 請求締め状態チェック
        boolean result = true;
        if (!skyprcsjissidd.equals(CommonConstants.DEF_DATE_00000000)) {
            result = seikyuFixStatusCheck(dto);
        }
        return result;
    }

    /**
     * （入金登録時）相関チェック（処理前）
     * 
     * @param dto 入金入力ヘッダ情報Dto
     * @throws BusinessException 業務例外
     * @throws ParseException 型変換例外
     */
    public void entryCorrelationBeforeCheck(C1005W01HeaderDto dto)
        throws BusinessException, ParseException {

        // 伝票番号チェック
        if (dto.getPrcskbn().equals(ProcessType.CANCEL)) { // 処理区分：取消の場合
            // 伝票番号が検索時と異なる場合エラー
            if (!dto.getDnpyno().equals(dto.getHdndnpyno())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.nykndnpyno", "伝票番号", 0), MessageID.W264);
            }

            // （検索時）相関チェック
            this.searchCorrelationCheck(dto);
        }

        // 入金日チェック
        if (dto.getPrcskbn().equals(ProcessType.CASH_RECEIPT)
            || dto.getPrcskbn().equals(ProcessType.ADJUST)) { // 処理区分：入金、調整の場合
            // 入金日未入力の場合エラー
            if (StringUtils.isEmpty(dto.getNykndd())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.nykndd", "入金日", 0), MessageID.W011, "入金日");
            }

            // 運用日付取得
            String unyoDate = s02m006tService.searchForMoji(dto.getApkihoncd(), dto.getShopcd(),
                CommonConstants.SYS_SETTEINO_038, CommonConstants.SETTEICD_038_1);

            // 入金伝票期間（過去日数）取得
            final Integer boforeDays =
                getNyukinVoucherTermDays(dto, CommonConstants.SETTEICD_041_1);
            // 入金伝票期間（未来日数）取得
            final Integer afterDays = getNyukinVoucherTermDays(dto, CommonConstants.SETTEICD_041_2);

            // 運用日付―入金伝票期間（過去日数）算出
            Calendar beforeDate = Calendar.getInstance();
            beforeDate.setTime(new SimpleDateFormat("yyyyMMdd").parse(unyoDate));
            beforeDate.add(Calendar.DATE, -boforeDays);
            // 運用日付＋入金伝票期間（未来日数）算出
            Calendar afterDate = Calendar.getInstance();
            afterDate.setTime(new SimpleDateFormat("yyyyMMdd").parse(unyoDate));
            afterDate.add(Calendar.DATE, afterDays);
            // 入金日取得
            Date nyukinDate = new SimpleDateFormat("yyyyMMdd").parse(dto.getNykndd());

            // 『運用日付―入金伝票期間（過去日数）＞入金日』又は『運用日付＋入金伝票期間（未来日数）＜入金日』の場合エラー
            if (beforeDate.getTime().compareTo(nyukinDate) == 1
                || afterDate.getTime().compareTo(nyukinDate) == -1) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.nykndd", "入金日", 0), MessageID.W115, "入金日",
                    new SimpleDateFormat("yyyy/MM/dd").format(beforeDate.getTime()),
                    new SimpleDateFormat("yyyy/MM/dd").format(afterDate.getTime()));
            }
        }

        // 入金種別チェック
        if (dto.getPrcskbn().equals(ProcessType.CASH_RECEIPT)) { // 処理区分：入金の場合
            // 入金種別未設定の場合エラー
            if (StringUtils.isEmpty(dto.getNyknkind())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.nyknkind", "入金種別", 0), MessageID.W011, "入金種別");
            }
        }

        List<FormField> formFieldList = new ArrayList<FormField>();
        formFieldList.add(new FormField("baseBean.nyknkin", "入金金額", 0));
        formFieldList.add(new FormField("baseBean.nebikikin", "値引額", 0));
        formFieldList.add(new FormField("baseBean.tesuryo", "手数料", 0));

        // 入金符号チェック
        if (dto.getPrcskbn().equals(ProcessType.CASH_RECEIPT)) { // 処理区分：入金の場合
            // 入金金額、値引金額、手数料の符号が不一致の場合エラー
            if (!((dto.getNyknkin() >= 0L && dto.getNebikikin() >= 0L && dto.getTesuryo() >= 0)
                || (dto.getNyknkin() <= 0L && dto.getNebikikin() <= 0L && dto.getTesuryo() <= 0))) {
                throw BusinessExceptionFactory.validationFailed(formFieldList, MessageID.W127,
                    "プラス", "マイナス");
            }
        }

        // 入金額チェック
        if (dto.getPrcskbn().equals(ProcessType.CASH_RECEIPT)) { // 処理区分：入金の場合
            // 入金金額、値引金額、手数料、過入金額が0の場合エラー
            if (dto.getNyknkin() == 0L && dto.getNebikikin() == 0L && dto.getTesuryo() == 0
                && dto.getKairekin() == 0L) {
                throw BusinessExceptionFactory.validationFailed(formFieldList, MessageID.W177,
                    "消込を行える金額");
            }
        } else if (dto.getPrcskbn().equals(ProcessType.ADJUST)) { // 処理区分：調整の場合
            // 売上調整金額が0の場合エラー
            if (dto.getUrichoseikin() == 0L) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W177, "消込を行える売上調整額");
            }
        }

        // 回収担当者コードチェック
        // 担当者マスタデータ取得
        S02M003TEntityKey key = new S02M003TEntityKey();
        key.setApkihoncd(dto.getApkihoncd());
        key.setShopcd(dto.getShopcd());
        key.setTnshacd(dto.getKaishutnshacd());
        S02M003TEntity entity = s02m003tDao.selectByPrimaryKey(key);
        // 担当者マスタデータなしの場合エラー
        if (entity == null) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.kaishutnshacd", "回収担当者コード", 0), MessageID.W171, "回収担当者コード",
                "使用");
        }

        // 処理区分：入金、調整の場合
        if (dto.getPrcskbn().equals(ProcessType.CASH_RECEIPT)
            || dto.getPrcskbn().equals(ProcessType.ADJUST)) {
            // 担当者マスタデータが削除されている場合エラー
            if (!entity.getYukokbn().equals(CommonConstants.FLG_ON)) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.kaishutnshacd", "回収担当者コード", 0), MessageID.W171,
                    "回収担当者コード", "使用");
            }
        }
    }

    /**
     * （入金登録時）相関チェック（処理後）
     * 
     * @param dto 入金入力ヘッダ情報Dto
     * @param meisaiDtoList 消込対象明細Dtoリスト
     * @return チェック結果（True:正常／False:異常）
     * @throws BusinessException 業務例外
     * @throws ParseException 型変換例外
     */
    public boolean entryCorrelationAfterCheck(C1005W01HeaderDto dto,
        List<C1005W01MeisaiDto> meisaiDtoList) throws BusinessException, ParseException {
        if (dto.getPrcskbn().equals(ProcessType.CASH_RECEIPT)) { // 処理区分：入金
            // 合計入金額＝0且つ明細データ0件の場合エラー
            if (dto.getNyknsmkin() == 0L && meisaiDtoList.size() == 0) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W177, "未消込残高を持つ請求残高");
            }

            // 今回消込残高＜0の場合
            if (dto.getKonkaikeshizd() < 0L) {
                // 明細データ件数＞0の場合エラー
                if (meisaiDtoList.size() > 0) {
                    throw BusinessExceptionFactory.validationFailed(MessageID.W124,
                        "過去の入金額の合計より大きいマイナス入金");
                }

                // 合計入金額＜0且つ明細データ0件の場合エラー
                if (dto.getNyknsmkin() < 0L && meisaiDtoList.size() == 0) {
                    throw BusinessExceptionFactory.validationFailed(MessageID.W124,
                        "過去の入金額の合計より大きいマイナス入金");
                }
            }
        } else if (dto.getPrcskbn().equals(ProcessType.ADJUST)) { // 処理区分：調整
            // 明細データ0件の場合エラー
            if (meisaiDtoList.size() == 0) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W177, "未消込残高を持つ請求残高");
            }
        }

        // 締日最大値取得
        // 月締め最大締日
        String maxShimedydm1 =
            this.s07t001tDao.selectForMaxShimebiByPrcsjissikbn(dto.getApkihoncd(), dto.getShopcd(),
                dto.getSkysakicd(), CommonConstants.SKYPRCSJISSIKBN_TSUKIJIME);

        if (!StringUtils.isEmpty(maxShimedydm1)
            && DateTimeUtil.isLater(dto.getNykndd(), maxShimedydm1)) {
            return false;
        }

        // 代引（単体）締め最大締日
        String maxShimedydm2 =
            this.s07t001tDao.selectForMaxShimebiByPrcsjissikbn(dto.getApkihoncd(), dto.getShopcd(),
                dto.getSkysakicd(), CommonConstants.SKYPRCSJISSIKBN_DAIBIKI);

        if (!StringUtils.isEmpty(maxShimedydm2)
            && !DateTimeUtil.isLater(maxShimedydm2, dto.getNykndd())) {
            return false;
        }

        return true;
    }

    /**
     * （更新時）相関チェック
     * 
     * @param dto 入金入力ヘッダ情報Dto
     * @param meisaiDtoList 消込対象明細Dtoリスト
     * @throws BusinessException 業務例外
     */
    public void updateCorrelationCheck(C1005W01HeaderDto dto, List<C1005W01MeisaiDto> meisaiDtoList)
        throws BusinessException {

        // 処理区分：入金、調整の場合
        if (dto.getPrcskbn().equals(ProcessType.CASH_RECEIPT)
            || dto.getPrcskbn().equals(ProcessType.ADJUST)) {
            int rowNum = 1;
            for (C1005W01MeisaiDto meisaiDto : meisaiDtoList) {
                // 消込対象明細の場合
                if (meisaiDto.getKeshikomi().equals(CommonConstants.FLG_ON)) {
                    FormField formField = this.getKonkaikshkmkinmeiFormField(rowNum);

                    // 合計消込可能額＞0且つ今回消込金額＜0の場合エラー
                    if (dto.getKshkmkanokinsm() > 0L && meisaiDto.getKonkaikeshikomikin() < 0L) {
                        throw BusinessExceptionFactory.validationFailed(formField, MessageID.W124,
                            "消込可能額と符号が異なる消込額");
                    }
                    // 合計消込可能額＜0且つ今回消込金額＞0の場合エラー
                    if (dto.getKshkmkanokinsm() < 0L && meisaiDto.getKonkaikeshikomikin() > 0L) {
                        throw BusinessExceptionFactory.validationFailed(formField, MessageID.W127,
                            "プラス", "マイナス");
                    }
                    // 今回消込金額＝0の場合エラー
                    if (meisaiDto.getKonkaikeshikomikin() == 0L) {
                        throw BusinessExceptionFactory.validationFailed(formField, MessageID.W171,
                            "消込を選択した場合", "今回消込金額は省略");
                    }

                    if (meisaiDto.getKonkaikeshikomikin() >= 0L) {
                        // （当月売上金額－消込済金額）＜今回消込金額の場合エラー
                        if (meisaiDto.getTouurikin() - meisaiDto.getKeshikomizumikin() < meisaiDto
                            .getKonkaikeshikomikin()) {
                            throw BusinessExceptionFactory.validationFailed(formField,
                                MessageID.W135, "消込金額");
                        }
                    } else {
                        // 消込済金額＜今回消込金額（絶対値）の場合エラー
                        if (meisaiDto.getKeshikomizumikin() < Math
                            .abs(meisaiDto.getKonkaikeshikomikin())) {
                            throw BusinessExceptionFactory.validationFailed(formField,
                                MessageID.W135, "消込金額");
                        }
                    }
                }
                rowNum++;
            }

            // 合計消込可能額＜今回消込金額合計 の場合エラー
            if (dto.getKshkmkanokinsm() < dto.getKonkaikeshikinsm()) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W135, "消込金額");
            }
            // 今回消込残高≠0且つ未消し残額合計≠0の場合エラー
            if (dto.getKonkaikeshizd() != 0L && dto.getMikeshizankinsm() != 0L) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W105,
                    "今回消込残高が０以外ですが、消し込みを行える未消し残額");
            }
        }
    }

    /**
     * 入金伝票チェック
     * 
     * @param dto 入金入力ヘッダ情報Dto
     * @return 請求処理実施日
     * @throws BusinessException 業務例外
     * @throws ParseException 型変換例外
     */
    protected String nyukinVoucherCheck(C1005W01HeaderDto dto)
        throws BusinessException, ParseException {
        FormField formField = new FormField("baseBean.nykndnpyno", "伝票番号", 0);

        // 入金管理テーブルデータ取得
        S10T003TEntityExample s10T003TEntityExample = new S10T003TEntityExample();
        s10T003TEntityExample.createCriteria().andApkihoncdEqualTo(dto.getApkihoncd())
            .andShopcdEqualTo(dto.getShopcd())
            .andNykndnpynoEqualTo(Objects.toString(dto.getDnpyno(), SystemConstants.WORD_BLANK))
            .andSkysakicdEqualTo(dto.getSkysakicd()).andYukokbnEqualTo(CommonConstants.FLG_ON);
        List<S10T003TEntity> s10T003TEntityList =
            s10t003tDao.selectByExample(s10T003TEntityExample);

        // 入金管理テーブルが存在しない場合エラー
        if (s10T003TEntityList.isEmpty()) {
            throw BusinessExceptionFactory.validationFailed(formField, MessageID.W084, "伝票番号");
        }

        // 運用日付取得
        String unyoDate = this.s02m006tService.searchForMoji(dto.getApkihoncd(), dto.getShopcd(),
            CommonConstants.SYS_SETTEINO_038, CommonConstants.SETTEICD_038_1);
        // 入金伝票日付期間（過去日数）取得
        Integer beforeDays = getNyukinVoucherTermDays(dto, CommonConstants.SETTEICD_041_1);

        // 運用日付―入金伝票番号（過去日数）算出
        Calendar beforeDate = Calendar.getInstance();
        beforeDate.setTime(new SimpleDateFormat("yyyyMMdd").parse(unyoDate));
        beforeDate.add(Calendar.DATE, -beforeDays);
        String beforeDateText = new SimpleDateFormat("yyyyMMdd").format(beforeDate.getTime());
        // 運用日付―入金伝票番号（過去日数）＞入金日の場合エラー
        if (!DateTimeUtil.isLater(beforeDateText, s10T003TEntityList.get(0).getNykndd())) {
            throw BusinessExceptionFactory.validationFailed(formField, MessageID.W171,
                "入金日が" + DateTimeUtil.toSlashedDate(beforeDateText) + "より過去の伝票", "取消");
        }

        // 入金明細テーブルデータ取得
        S10T001TEntityExample s10T001TEntityExample = new S10T001TEntityExample();
        s10T001TEntityExample.createCriteria().andApkihoncdEqualTo(dto.getApkihoncd())
            .andShopcdEqualTo(dto.getShopcd()).andSkysakicdEqualTo(dto.getSkysakicd())
            .andDnpynoEqualTo(Objects.toString(dto.getDnpyno(), SystemConstants.WORD_BLANK))
            .andYukokbnEqualTo(CommonConstants.FLG_ON);
        List<S10T001TEntity> s10T001TEntityList =
            s10t001tDao.selectByExample(s10T001TEntityExample);

        // 入金明細テーブルが存在しない場合エラー
        if (s10T001TEntityList == null || s10T001TEntityList.size() == 0) {
            throw BusinessExceptionFactory.validationFailed(formField, MessageID.W084, "伝票番号");
        }

        // 取消済みの場合エラー
        if (!StringUtils.isEmpty(s10T001TEntityList.get(0).getTrksdnpyno())
            && !s10T001TEntityList.get(0).getTrksdnpyno().equals("00000000000")) {
            throw BusinessExceptionFactory.validationFailed(formField, MessageID.W133, "取消");
        }

        // 売上入力の場合エラー
        if (s10T001TEntityList.get(0).getDatsksmotokbn().equals(CommonConstants.SETTEICD_093_05)) {
            throw BusinessExceptionFactory.validationFailed(formField, MessageID.W171,
                "売上入力で入力した入金", "入金入力での取消は");
        }

        for (S10T001TEntity entity : s10T001TEntityList) {
            // 消込金額≠0の場合
            if (entity.getKeshikomikin() != 0L) {
                // 入金消込履歴テーブルデータ取得
                S10T004TEntityExample example = new S10T004TEntityExample();
                example.createCriteria().andApkihoncdEqualTo(entity.getApkihoncd())
                    .andShopcdEqualTo(entity.getShopcd()).andNykndnpynoEqualTo(entity.getDnpyno())
                    .andYukokbnEqualTo(CommonConstants.FLG_ON);
                List<S10T004TEntity> entityList = s10t004tDao.selectByExample(example);

                // 入金消込履歴テーブルデータが存在しない場合エラー
                if (entityList.size() == 0) {
                    throw BusinessExceptionFactory.validationFailed(formField, MessageID.W171,
                        "消込履歴情報の存在しない入金データの取消", "行うことは");
                }
            }
        }

        // 請求処理実施日返却
        return s10T001TEntityList.get(0).getSkyprcsjissidd();
    }

    /**
     * 入金伝票日付期間日数取得
     * 
     * @param dto 入金入力ヘッダ情報Dto
     * @param setteicd 設定コード
     * @return 入金伝票日付期間日数
     */
    private Integer getNyukinVoucherTermDays(C1005W01HeaderDto dto, String setteicd) {
        try {
            return s02m006tService.searchForSuchi(dto.getApkihoncd(), dto.getShopcd(),
                CommonConstants.SYS_SETTEINO_041, setteicd);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 消込請求残高チェック
     * 
     * @param dto 入金入力ヘッダ情報Dto
     * @throws BusinessException 業務例外
     */
    protected void clearingSeikyuBalanceCheck(C1005W01HeaderDto dto) throws BusinessException {
        FormField formField = new FormField("baseBean.nykndnpyno", "伝票番号", 0);

        // 入金消込先請求残高取得
        List<S10T004TC1005W01KeshikomiKinEntity> entityList =
            s10t004tDao.selectForC1005W01NyknKeshikomiSkyZan(dto.getApkihoncd(), dto.getShopcd(),
                dto.getSkysakicd(), dto.getDnpyno());

        // 入金消込先請求残高データ0件の場合
        // クエリ結果が0件というのは入金消込履歴テーブルまたは入金明細テーブルに該当データが存在しない場合
        log.debug("入金消込先請求残高データ : {}件", entityList.size());
        if (entityList.size() == 0) {
            // 消込履歴がない入金伝票の場合、消込請求残高チェック不要
            return;
        }

        for (S10T004TC1005W01KeshikomiKinEntity entity : entityList) {
            // 請求残高消込金額＝NULLの場合エラー
            // 請求残高テーブルは外部結合しているため、請求残高消込金額がNULLというのは該当する請求残高が存在しない場合
            if (!StringUtils.isEmpty(entity.getSkyshono())
                && entity.getSkyzkeshikomikin() == null) {
                throw BusinessExceptionFactory.validationFailed(formField, MessageID.W255,
                    "消込先の請求締めが解除されているため取消");
            }

            if (!StringUtils.isEmpty(entity.getSkyshono())
                && !entity.getNyknkind().equals(NYKNKIND_SALESADJUST)) {
                if (entity.getNkkrkeshikomikin() > 0L) {
                    // 入金種別：売上調整以外＆入金消込履歴消込金額＞0＆入金消込履歴消込金額＞請求残高消込金額の場合エラー
                    if (entity.getNkkrkeshikomikin() > entity.getSkyzkeshikomikin()) {
                        throw BusinessExceptionFactory.validationFailed(formField, MessageID.W194);
                    }
                } else if (entity.getNkkrkeshikomikin() < 0L) {
                    // 入金種別：売上調整以外＆入金消込履歴消込金額＜0＆（入金消込履歴消込金額×-1）＞請求残高消込残高の場合エラー
                    if ((entity.getNkkrkeshikomikin() * -1) > entity.getSkyzkeshikomizd()) {
                        throw BusinessExceptionFactory.validationFailed(formField, MessageID.W194);
                    }
                }
            } else if (entity.getNyknkind().equals(NYKNKIND_SALESADJUST)) {
                if (entity.getNkkrkeshikomikin() > 0L) {
                    // 入金種別：売上調整＆入金消込履歴消込金額＞0＆入金消込履歴消込金額＞請求残高消込金額の場合エラー
                    if (entity.getNkkrkeshikomikin() > entity.getSkyzkeshikomikin()) {
                        throw BusinessExceptionFactory.validationFailed(formField, MessageID.W194);
                    }
                }
            }
        }
    }

    /**
     * 請求締め状態チェック
     * 
     * @param dto 入金入力ヘッダ情報Dto
     * @return チェック結果（True:正常／False:異常）
     * @throws BusinessException 業務例外
     */
    private boolean seikyuFixStatusCheck(C1005W01HeaderDto dto) throws BusinessException {

        // 最大請求締年月日取得
        String maxSeikyuFixDate = s07t001tDao.selectByF0501W35shimedydm(dto.getApkihoncd(),
            dto.getShopcd(), dto.getSkysakicd());
        // 最小請求締年月日取得
        String minSeikyuFixDate = s10t001tDao.selectForC1005W01MinShimeDydmdd(dto.getApkihoncd(),
            dto.getShopcd(), dto.getSkysakicd(), dto.getDnpyno());

        if (StringUtils.isEmpty(maxSeikyuFixDate) || StringUtils.isEmpty(minSeikyuFixDate)) {
            // 請求締年月日が未設定の場合
            return true;
        } else if (!maxSeikyuFixDate.equals(minSeikyuFixDate)) {
            // 最大請求締年月日≠最小請求締年月日の場合
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.nykndnpyno", "伝票番号", 0), MessageID.W133, "請求締処理");
        } else {
            return false;
        }
    }

    /**
     * 取消入金情報取得
     * 
     * @param dto 入金入力ヘッダ情報Dto
     */
    public void getCancelNyukinInfo(C1005W01HeaderDto dto) {

        // 取消入金情報取得
        S10T001TC1005W01Entity entity = s10t001tDao.selectForC1005W01NyknInfo(dto.getApkihoncd(),
            dto.getShopcd(), dto.getSkysakicd(), dto.getDnpyno());

        // 入金入力ヘッダ情報Dto設定
        // （入金日、入金種別、入金金額、値引金額、手数料、売上調整金額、明細備考、過入金額、回収担当者コード、回収担当者名）
        dto.setNykndd(entity.getNykndd());
        dto.setNyknkind(entity.getNyknkind());
        dto.setNyknkin(entity.getNyknkin());
        dto.setNebikikin(entity.getNebikikin());
        dto.setTesuryo(entity.getTesuryo());
        dto.setUrichoseikin(entity.getUrichoseikin());
        dto.setMeisaibiko(entity.getBiko());
        dto.setKairekin(0L);
        dto.setKaishutnshacd(entity.getKaishutnshacd());
        dto.setKaishutnshanm(entity.getKaishutnshanm());
    }

    /**
     * 請求先マスタ情報取得
     * 
     * @param dto 入金入力ヘッダ情報Dto
     */
    public void getSeikyuMasterInfo(C1005W01HeaderDto dto) {

        // 請求先マスタデータ取得
        S02M017TEntityKey key = new S02M017TEntityKey();
        key.setApkihoncd(dto.getApkihoncd());
        key.setShopcd(dto.getShopcd());
        key.setSkysakicd(dto.getSkysakicd());
        S02M017TEntity entity = s02m017tDao.selectByPrimaryKey(key);

        // 入金入力ヘッダ情報Dto設定（過入金額、売上調整金額）
        dto.setKairekin(entity.getKairekin());
        dto.setUrichoseikin(entity.getUrichoseikin() * -1L);
    }

    /**
     * 対象データ取得
     * 
     * @param dto 入金入力ヘッダ情報Dto
     * @return 消込対象明細Dtoリスト
     */
    public List<C1005W01MeisaiDto> getTargetData(C1005W01HeaderDto dto) {

        // 消込対象明細データ取得
        List<S10T001TC1005W01MeisaiEntity> entityList = s10t001tDao.selectForC1005W01MeisaiList(
            dto.getApkihoncd(), dto.getShopcd(), dto.getSkysakicd(), dto.getDnpyno(),
            dto.getPrcskbn(), dto.getKshkmkanokinsm() < 0L, dto.getDetailDispLimitSize() + 1);

        // 消込対象明細Dtoリスト生成
        List<C1005W01MeisaiDto> resultDtoList = new ArrayList<C1005W01MeisaiDto>();
        for (S10T001TC1005W01MeisaiEntity entity : entityList) {
            C1005W01MeisaiDto resultDto = new C1005W01MeisaiDto();
            resultDto.setSkyshono(entity.getSkyshono());
            resultDto.setTokuisakicd(entity.getTokuisakicd());
            resultDto.setTokuisakinm(entity.getTokuisakinm());
            resultDto.setSkydydmdd(entity.getKonkaishimedd());
            resultDto.setTouurikin(entity.getTaxurikin());
            resultDto.setKeshikomizumikin(entity.getKeshikomikin());
            resultDto.setKeshikomizd(entity.getKeshikomizd());
            resultDto.setUpdkeshikomikin(entity.getUpdkeshikomikin());
            resultDto.setKonkaikeshikomikin(0L);
            resultDto.setKeshikomi(CommonConstants.FLG_OFF);
            resultDtoList.add(resultDto);
        }

        // 合計金額取得
        return getSumKingaku(dto, resultDtoList);
    }

    /**
     * 合計金額取得
     * 
     * @param dto 入金入力ヘッダ情報Dto
     * @param resultDtoList 消込対象明細Dtoリスト
     * @return 消込対象明細Dtoリスト
     */
    protected List<C1005W01MeisaiDto> getSumKingaku(C1005W01HeaderDto dto,
        List<C1005W01MeisaiDto> resultDtoList) {

        // 集計用変数定義（売上金額合計、消込済金額合計、消込金額合計、未消し残額合計）
        Long urikinsum = 0L;
        Long keshikomizumikinsum = 0L;
        Long keshikomikinsum = 0L;
        Long mikeshizankinsum = 0L;

        for (C1005W01MeisaiDto resultDto : resultDtoList) {
            // 処理区分：取消以外の場合
            if (!dto.getPrcskbn().equals(ProcessType.CANCEL)) {
                // 合計消込可能額≧0の場合
                if (dto.getKshkmkanokinsm() >= 0L) {
                    // 消込金額合計＋消込残高≦合計消込可能額の場合
                    if (keshikomikinsum + resultDto.getKeshikomizd() <= dto.getKshkmkanokinsm()) {
                        resultDto.setKonkaikeshikomikin(resultDto.getKeshikomizd());
                        resultDto.setKeshikomi(CommonConstants.FLG_ON);

                        // 消込金額合計＋消込残高＞合計消込可能額且つ消込金額合計＜合計消込可能額の場合
                    } else if (keshikomikinsum < dto.getKshkmkanokinsm()) {
                        resultDto.setKonkaikeshikomikin(dto.getKshkmkanokinsm() - keshikomikinsum);
                        resultDto.setKeshikomi(CommonConstants.FLG_ON);
                    }
                } else {
                    // 消込金額合計（絶対値）＋消込済金額≦合計消込可能金額（絶対値）の場合
                    if (Math.abs(keshikomikinsum) + resultDto.getKeshikomizumikin() <= Math
                        .abs(dto.getKshkmkanokinsm())) {
                        resultDto.setKonkaikeshikomikin(resultDto.getKeshikomizumikin() * -1L);
                        resultDto.setKeshikomi(CommonConstants.FLG_ON);

                        // 消込金額合計（絶対値）＋消込済金額＞合計消込可能額（絶対値）且つ消込金額合計（絶対値）＜合計消込可能額（絶対値）の場合
                    } else if (Math.abs(keshikomikinsum) < Math.abs(dto.getKshkmkanokinsm())) {
                        resultDto.setKonkaikeshikomikin(dto.getKshkmkanokinsm() - keshikomikinsum);
                        resultDto.setKeshikomi(CommonConstants.FLG_ON);
                    }
                }
            } else {
                // 取消消込金額≧0の場合
                if (resultDto.getUpdkeshikomikin() >= 0L) {
                    // 消込済金額≧取消消込金額の場合
                    if (resultDto.getKeshikomizumikin() >= resultDto.getUpdkeshikomikin()) {
                        resultDto.setKonkaikeshikomikin(resultDto.getUpdkeshikomikin() * -1);
                        resultDto.setKeshikomi(CommonConstants.FLG_ON);
                    } else {
                        resultDto.setKonkaikeshikomikin(resultDto.getKeshikomizumikin() * -1);
                        resultDto.setKeshikomi(CommonConstants.FLG_ON);
                    }
                } else {
                    // 消込残高≧取消消込金額（絶対値）の場合
                    if (resultDto.getKeshikomizd() >= Math.abs(resultDto.getUpdkeshikomikin())) {
                        resultDto.setKonkaikeshikomikin(resultDto.getUpdkeshikomikin() * -1);
                        resultDto.setKeshikomi(CommonConstants.FLG_ON);
                    } else {
                        resultDto.setKonkaikeshikomikin(resultDto.getKeshikomizd());
                        resultDto.setKeshikomi(CommonConstants.FLG_ON);
                    }
                }
            }

            // 未消し残高設定（当月売上金額－消込済金額－今回消込金額）
            Long touurikin = resultDto.getTouurikin();
            Long keshikomizumikin = resultDto.getKeshikomizumikin();
            Long konkaikeshikomikin = resultDto.getKonkaikeshikomikin();
            Long mikeshizankin = touurikin - keshikomizumikin - konkaikeshikomikin;
            resultDto.setMikeshizankin(mikeshizankin);

            // 集計用変数設定（売上金額合計、消込済金額合計、消込金額合計、未消し残額合計）
            urikinsum += touurikin;
            keshikomizumikinsum += keshikomizumikin;
            keshikomikinsum += konkaikeshikomikin;
            mikeshizankinsum += mikeshizankin;
        }

        // 入金入力ヘッダ情報Dto設定（当月売上金額合計、消込済金額合計、今回消込金額合計、未消し残額合計、今回消込残高）
        dto.setTouurikinsm(urikinsum);
        dto.setKeshizumikinsm(keshikomizumikinsum);
        dto.setKonkaikeshikinsm(keshikomikinsum);
        dto.setMikeshizankinsm(mikeshizankinsum);
        dto.setKonkaikeshizd(dto.getKshkmkanokinsm() - dto.getKonkaikeshikinsm());

        return resultDtoList;
    }

    /**
     * 合計金額算出
     * 
     * @param dto 入金入力ヘッダ情報Dto
     * @param meisaiDtoList 消込対象明細Dtoリスト
     */
    public void calcSumKingaku(C1005W01HeaderDto dto, List<C1005W01MeisaiDto> meisaiDtoList) {

        // 集計用変数定義（売上金額合計、消込済金額合計、消込金額合計、未消し残額合計）
        Long urikinsum = 0L;
        Long keshikomizumikinsum = 0L;
        Long keshikomikinsum = 0L;
        Long mikeshizankinsum = 0L;

        // 集計用変数設定（売上金額合計、消込済金額合計、消込金額合計、未消し残額合計）
        for (C1005W01MeisaiDto meisaiDto : meisaiDtoList) {
            urikinsum += meisaiDto.getTouurikin();
            keshikomizumikinsum += meisaiDto.getKeshikomizumikin();
            keshikomikinsum += meisaiDto.getKonkaikeshikomikin();
            mikeshizankinsum += meisaiDto.getMikeshizankin();
        }

        // 入金入力ヘッダ情報Dto設定
        // （合計入金額、合計消込可能金額、当月売上金額合計、消込済金額合計、今回消込金額合計、未消し残額合計、今回消込残高）
        dto.setNyknsmkin(dto.getNyknkin() + dto.getNebikikin() + dto.getTesuryo());
        Long kshkmkanokinsm = 0L;
        if (dto.getPrcskbn().equals(ProcessType.CASH_RECEIPT)) {
            kshkmkanokinsm = dto.getNyknsmkin() + dto.getKairekin();
        } else if (dto.getPrcskbn().equals(ProcessType.ADJUST)) {
            kshkmkanokinsm = dto.getNyknsmkin() + dto.getUrichoseikin();
        } else if (dto.getPrcskbn().equals(ProcessType.CANCEL)) {
            kshkmkanokinsm = dto.getNyknsmkin() + dto.getKairekin() + dto.getUrichoseikin();
        }
        dto.setKshkmkanokinsm(kshkmkanokinsm);
        dto.setTouurikinsm(urikinsum);
        dto.setKeshizumikinsm(keshikomizumikinsum);
        dto.setKonkaikeshikinsm(keshikomikinsum);
        dto.setMikeshizankinsm(mikeshizankinsum);
        dto.setKonkaikeshizd(dto.getKshkmkanokinsm() - dto.getKonkaikeshikinsm());
    }

    /**
     * 請求締処理存在チェック
     * 
     * @param dto 入金入力ヘッダ情報Dto
     * @throws BusinessException 業務例外
     */
    public void seikyuFixProcessExsitCheck(C1005W01HeaderDto dto) throws BusinessException {

        // 請求締処理管理テーブルデータ取得
        S07T004TEntityKey key = new S07T004TEntityKey();
        key.setApkihoncd(dto.getApkihoncd());
        key.setShopcd(dto.getShopcd());
        S07T004TEntity entity = s07t004tDao.selectByPrimaryKey(key);

        if (entity != null) {
            // 処理状態：終了以外の場合エラー
            if (!entity.getPrcsjotai().equals(CommonConstants.PRCSJOTAI_0)) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W260,
                    "請求締め処理が実行中です。しばらくお待ちください。");
            }
        }
    }

    /**
     * 入金登録
     * 
     * @param dto 入金入力ヘッダ情報Dto
     * @param meisaiDtoList 消込対象明細Dtoリスト
     * @param dbManagementSession DB管理セッション情報
     * @return 入金伝票番号
     * @throws Exception システム例外
     */
    @Transactional(rollbackFor = Exception.class)
    public String createNyukin(C1005W01HeaderDto dto, List<C1005W01MeisaiDto> meisaiDtoList,
        DbManagementSession dbManagementSession) throws Exception {

        // 入金ワーク登録（入金伝票番号取得）
        String nyukinVoucherNo = this.createNyukinWork(dto, meisaiDtoList, dbManagementSession);

        // 一括入金消込処理実行
        this.bulkNyukinClearing(dto, nyukinVoucherNo, dbManagementSession);

        return nyukinVoucherNo;
    }


    /**
     * 入金ワーク登録
     * 
     * @param dto 入金入力ヘッダ情報Dto
     * @param meisaiDtoList 消込対象明細Dtoリスト
     * @param dbManagementSession DB管理セッション情報
     * @return 入金伝票番号
     */
    protected String createNyukinWork(C1005W01HeaderDto dto, List<C1005W01MeisaiDto> meisaiDtoList,
        DbManagementSession dbManagementSession) {

        // 入金伝票番号採番
        String dnpyno = saibanService.getSlipNoNyukin(dto.getApkihoncd(), dto.getShopcd());

        // 入金取込ワークデータ生成
        S10W001TEntity entity = new S10W001TEntity();
        entity.setApkihoncd(dto.getApkihoncd());
        entity.setShopcd(dto.getShopcd());
        entity.setDatsksmotokbn(CommonConstants.KBN_SAKUSEIMOTO_NYUUKINNYURYOKU);
        entity.setDnpyno(dnpyno);
        entity.setSkysakicd(dto.getSkysakicd());
        entity.setNykndd(dto.getNykndd());
        entity.setNyknkind(dto.getNyknkind());
        entity.setNyknkin(dto.getNyknkin());
        entity.setNebikikin(dto.getNebikikin());
        entity.setTesuryo(dto.getTesuryo());
        entity.setBiko(dto.getMeisaibiko());
        entity.setKaishutnshacd(dto.getKaishutnshacd());
        entity.setNykndatsksflg(CommonConstants.FLG_OFF);
        entity.setToshaapkihoncd(dbManagementSession.getApkihoncd());
        entity.setToshashopcd(dbManagementSession.getShopcd());
        entity.setToshacd(dbManagementSession.getTnshacd());
        entity.setInptsystmymd(dbManagementSession.getSystemDate());
        entity.setInptsystmjikan(dbManagementSession.getSystemTime());
        entity.setInptunyoymd(dbManagementSession.getUnyoDate());
        entity.setInptipadrse(dbManagementSession.getIpAddress());
        entity.setInptpgmid(dbManagementSession.getProgramId());
        entity.setKoshaapkihoncd(dbManagementSession.getApkihoncd());
        entity.setKoshashopcd(dbManagementSession.getShopcd());
        entity.setKoshacd(dbManagementSession.getTnshacd());
        entity.setKshnsystmymd(dbManagementSession.getSystemDate());
        entity.setKshnsystmjikan(dbManagementSession.getSystemTime());
        entity.setKshnunyoymd(dbManagementSession.getUnyoDate());
        entity.setKshnipadrse(dbManagementSession.getIpAddress());
        entity.setKshnpgmid(dbManagementSession.getProgramId());
        s10w001tDao.insert(entity);

        // 処理区分：入金、調整の場合
        if (dto.getPrcskbn().equals(ProcessType.CASH_RECEIPT)
            || dto.getPrcskbn().equals(ProcessType.ADJUST)) {
            // 入金消込ワークデータ生成
            for (C1005W01MeisaiDto meisaiDto : meisaiDtoList) {
                if (meisaiDto.getKeshikomi().equals(CommonConstants.FLG_ON)) {
                    S10W003TEntity s10W003TEntity = new S10W003TEntity();
                    s10W003TEntity.setApkihoncd(dto.getApkihoncd());
                    s10W003TEntity.setShopcd(dto.getShopcd());
                    s10W003TEntity.setDenpyono(dnpyno);
                    s10W003TEntity.setSkyshono(meisaiDto.getSkyshono());
                    s10W003TEntity.setTokuisakicd(meisaiDto.getTokuisakicd());
                    s10W003TEntity.setKeshikomikin(meisaiDto.getKonkaikeshikomikin());
                    s10W003TEntity.setKanyukindenno(null);
                    s10W003TEntity.setToshaapkihoncd(dbManagementSession.getApkihoncd());
                    s10W003TEntity.setToshashopcd(dbManagementSession.getShopcd());
                    s10W003TEntity.setToshacd(dbManagementSession.getTnshacd());
                    s10W003TEntity.setInptsystmymd(dbManagementSession.getSystemDate());
                    s10W003TEntity.setInptsystmjikan(dbManagementSession.getSystemTime());
                    s10W003TEntity.setInptunyoymd(dbManagementSession.getUnyoDate());
                    s10W003TEntity.setInptipadrse(dbManagementSession.getIpAddress());
                    s10W003TEntity.setInptpgmid(dbManagementSession.getProgramId());
                    s10W003TEntity.setKoshaapkihoncd(dbManagementSession.getApkihoncd());
                    s10W003TEntity.setKoshashopcd(dbManagementSession.getShopcd());
                    s10W003TEntity.setKoshacd(dbManagementSession.getTnshacd());
                    s10W003TEntity.setKshnsystmymd(dbManagementSession.getSystemDate());
                    s10W003TEntity.setKshnsystmjikan(dbManagementSession.getSystemTime());
                    s10W003TEntity.setKshnunyoymd(dbManagementSession.getUnyoDate());
                    s10W003TEntity.setKshnipadrse(dbManagementSession.getIpAddress());
                    s10W003TEntity.setKshnpgmid(dbManagementSession.getProgramId());
                    s10w003tDao.insert(s10W003TEntity);
                }
            }
        }

        return dnpyno;
    }

    /**
     * 一括入金消込処理
     * 
     * @param dto 入金入力ヘッダ情報Dto
     * @param dnpyno 入金伝票番号
     * @param dbManagementSession DB管理セッション情報
     * @throws Exception システム例外
     * @throws BusinessException 業務例外
     */
    public void bulkNyukinClearing(C1005W01HeaderDto dto, String dnpyno,
        DbManagementSession dbManagementSession) throws Exception, BusinessException {

        // 一括入金消込Entity生成
        S10W001TC1005W01IkkatsuKeshikomiEntity entity =
            new S10W001TC1005W01IkkatsuKeshikomiEntity();
        entity.setApkihoncd(dto.getApkihoncd());
        entity.setShopcd(dto.getShopcd());
        entity.setKoshaapkihoncd(dbManagementSession.getApkihoncd());
        entity.setKoshashopcd(dbManagementSession.getShopcd());
        entity.setKoshacd(dbManagementSession.getTnshacd());
        entity.setKshnipadrse(dbManagementSession.getIpAddress());
        entity.setDatsksmotokbn(CommonConstants.KBN_SAKUSEIMOTO_NYUUKINNYURYOKU);
        entity.setNykndnpynofrom(dnpyno);
        entity.setNykndnpynoto(
            !dto.getPrcskbn().equals(ProcessType.CANCEL) ? dnpyno : dto.getDnpyno());
        entity.setSkyshootptarinashikbn(
            ItemUtil.nullToValue(dto.getSkyshootptkbn(), CommonConstants.FLG_OFF));

        try {
            switch (dto.getPrcskbn()) {
                // 処理区分：入金
                case ProcessType.CASH_RECEIPT:
                    // 一括入金消込
                    s10w001tDao.executeNyukinKeshikomi(entity);
                    break;

                // 処理区分：調整
                case ProcessType.ADJUST:
                    // 一括入金消込（調整）
                    s10w001tDao.executeINyukinUriChosei(entity);
                    break;

                // 処理区分：取消
                case ProcessType.CANCEL:
                    // 一括入金消込（取消）
                    s10w001tDao.executeNyukinTorikeshi(entity);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Map<String, Object> message = getBusinessErrorMessage(e);
            throw BusinessExceptionFactory.validationFailed((MessageID) message.get(KEY_MESSAGE_ID),
                message.get(KEY_MESSAGE).toString());
        }
    }

    /**
     * 業務例外メッセージ取得
     * 
     * @param e Exception
     * @return 業務例外メッセージ
     * @throws Exception システム例外
     */
    private Map<String, Object> getBusinessErrorMessage(Exception e) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String[] messageList = e.getMessage().split("\n");
        for (String messageItem : messageList) {
            if (isSQLError(messageItem)) {
                int start = messageItem.indexOf("SQLException") + 25;
                result.put(KEY_MESSAGE_ID, MessageID.W178);
                result.put(KEY_MESSAGE, messageItem.substring(start, messageItem.length()));
                return result;
            }
        }

        throw e;
    }

    /**
     * SQL業務例外メッセージ判定
     * 
     * @param message メッセージ
     * @return SQL業務例外判定結果値（True:SQL業務例外／False:その他例外）
     */
    private boolean isSQLError(String message) {
        List<String> errorIdList =
            new ArrayList<String>(Arrays.asList("ORA-20001", "ORA-20002", "ORA-20003", "ORA-20004",
                "ORA-20005", "ORA-20006", "ORA-20007", "ORA-20008", "ORA-20009", "ORA-20010",
                "ORA-20011", "ORA-20012", "ORA-20013", "ORA-20014", "ORA-20015"));
        if (message.contains("SQLException")) {
            for (String errorId : errorIdList) {
                if (message.contains(errorId)) {
                    return true;
                }
            }
        }
        return false;
    }
}
