package jp.co.aquaclara.accis.web.domain.service.d1501;

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
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M017TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T004TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07W003TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S19T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T004TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07W003TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S19T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S19T001TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501W00AciKeiyuConditionEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501W00KaishuHohoConditionEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501W11Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501W11SearchConditionEntity;
import jp.co.aquaclara.accis.common.exception.BusinessException;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.web.domain.dto.d1501.D1501W11SearchConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.d1501.D1501W11SearchResultDto;
import jp.co.aquaclara.accis.web.domain.dto.d1501.D1501W11TargetMeisaiDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M006TDto;
import jp.co.aquaclara.accis.web.domain.service.c0800.UtilityService;
import jp.co.aquaclara.accis.web.domain.service.master.S02M006TService;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * 請求関連処理（請求締処理）-サービスクラス
 * 
 * @author accisdev044
 *
 */
@Service
@Slf4j
public class D1501W11Service {

    /** 明細1ページ当たりの行数 */
    public static final int PAGEABLE_SIZE = 30;
    /** メッセージID KEY */
    private static final String KEY_MESSAGE_ID = "MessageId";
    /** メッセージ KEY */
    private static final String KEY_MESSAGE = "Message";

    /** 処理 */
    public class Process {
        /** 請求締 */
        public static final String SEIKYU_FIX = "1";
        /** 請求締解除 */
        public static final String SEIKYU_FIX_CANCEL = "2";
    }

    /** 処理状態 */
    protected class ProcessStatus {
        /** 未処理 */
        public static final String UNPROCESSED = "0";
    }

    /** 締処理区分 */
    protected class FixProcess {
        /** 締 */
        public static final String FIX = "1";
        /** 再締 */
        public static final String REFIX = "2";
        /** 解除 */
        public static final String CANCEL = "3";
    }

    /** 請求関連処理ユーティリティサービス */
    @Autowired
    private D1501W00Service d1501w00service;
    /** システム管理マスタサービス */
    @Autowired
    private S02M006TService s02m006tservice;
    /** ユーティリティサービス */
    @Autowired
    private UtilityService util;

    /** 請求残高テーブルDao */
    @Autowired
    private S07T001TDao dao;
    /** 請求先マスタDao */
    @Autowired
    private S02M017TDao s02M017TDao;
    /** 請求締ワークDao */
    @Autowired
    private S07W003TDao s07W003TDao;
    /** 請求締処理管理テーブルDao */
    @Autowired
    private S07T004TDao s07T004TDao;
    /** 前回滞留情報テーブルDao */
    @Autowired
    private S19T001TDao s19T001TDao;
    /** 請求残高テーブルDao */
    @Autowired
    private S07T001TDao s07t001tDao;

    /**
     * 請求締処理対象結果取得
     *
     * @param conditionDto 請求締検索条件Dto
     * @return 請求締検索結果一覧Dtoリスト
     */
    public List<D1501W11SearchResultDto> search(D1501W11SearchConditionDto conditionDto) {
        // 回収方法選択リスト取得
        List<S07T001TD1501W00KaishuHohoConditionEntity> kaishuHohoList =
            S07T001TD1501W00KaishuHohoConditionEntity.create(conditionDto.getKaishuhohokbn1(),
                conditionDto.getKaishuhohokbn2(), conditionDto.getKaishuhohokbn3(),
                conditionDto.getKaishuhohokbn4(), conditionDto.getKaishuhohokbn5(),
                conditionDto.getKaishuhohokbn6(), conditionDto.getKaishuhohokbn7(),
                conditionDto.getKaishuhohokbn8(), conditionDto.getKaishuhohokbn9(),
                conditionDto.getKaishuhohokbn10(), conditionDto.getKaishuhohokbn11(),
                conditionDto.getKaishuhohocd2(), conditionDto.getKaishuhohocd5());

        // ACI本部NA区分リスト取得
        List<S07T001TD1501W00AciKeiyuConditionEntity> acikeiyuflgList =
            S07T001TD1501W00AciKeiyuConditionEntity.createCondition(conditionDto.getAcikeiyuflg1(),
                conditionDto.getAcikeiyuflg2());

        // 請求締検索条件Entity生成
        S07T001TD1501W11SearchConditionEntity entity =
            createSearchEntity(conditionDto, kaishuHohoList, acikeiyuflgList);

        // 請求締検索データ取得
        List<S07T001TD1501W11Entity> searchResultList = dao.selectForD1501W11List(entity);
        // 検索データ存在なしの場合エラー
        if (searchResultList.isEmpty()) {
            throw BusinessExceptionFactory.dataNotFound(MessageID.W083);
        }

        // 請求締検索結果一覧Dto生成
        List<D1501W11SearchResultDto> resultDto =
            createSearchResultDto(searchResultList, conditionDto.getSkyshimedate());
        return resultDto;
    }

    /**
     * 請求締検索結果一覧Dto生成
     *
     * @param searchResultList 請求締検索結果一覧Entity
     * @param shimedd 締日
     * @return 請求締検索結果一覧Dto
     */
    private List<D1501W11SearchResultDto> createSearchResultDto(
        List<S07T001TD1501W11Entity> searchResultList, String shimedd) {
        List<D1501W11SearchResultDto> result = new ArrayList<>();
        for (S07T001TD1501W11Entity entity : searchResultList) {
            D1501W11SearchResultDto dto = new D1501W11SearchResultDto();
            dto.setSentakukbn(entity.getSentakukbn());
            dto.setApkihoncd(entity.getApkihoncd());
            dto.setShopcd(entity.getShopcd());
            dto.setSkysakicd(entity.getSkysakicd());
            dto.setSkysakimei(entity.getSkysakinm());
            dto.setJigyoshocd(entity.getJigyoshocd());
            dto.setShimeddmei(shimedd);
            dto.setKaishuhohomei(entity.getKaishuhohokbnname());
            dto.setHdnkaishuhohomei(entity.getKaishuhohokbn());
            dto.setKaishuhohomei2(entity.getKaishuhohokbnnameseikyu());
            dto.setHdnkaishuhohomei2(entity.getKaishuhohokbnseikyu());
            dto.setTairyukbnmei(entity.getTairyukbnname());
            dto.setPrcsjotai(entity.getSyoristatus());
            dto.setPrcsjotaimei(d1501w00service.getPrcsjotaime(entity.getSyoristatus()));
            result.add(dto);
        }

        return result;
    }

    /**
     * 請求締検索条件Entity生成
     *
     * @param dto 請求締検索条件Dto
     * @param kaishuHohoList 回収方法検索条件Entityリスト
     * @param acikeiyuflgList ACI本部NA区分検索条件Entiryリスト
     * @return 請求締検索条件Entity
     */
    private S07T001TD1501W11SearchConditionEntity createSearchEntity(D1501W11SearchConditionDto dto,
        List<S07T001TD1501W00KaishuHohoConditionEntity> kaishuHohoList,
        List<S07T001TD1501W00AciKeiyuConditionEntity> acikeiyuflgList) {
        S07T001TD1501W11SearchConditionEntity entity = new S07T001TD1501W11SearchConditionEntity();
        // AP基本コード
        entity.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        entity.setShopcd(dto.getShopcd());
        // 事業所コード
        entity.setJigyoshocd(dto.getJigyoshocd());
        // 締年月
        entity.setShimedydm(DateTimeUtil.toNoSlashedDateYYYYMM(dto.getShimedd1()));
        // 締日
        entity.setShimedd(dto.getShimedd3());
        // 抽出締日
        entity.setExtractshimedd(dto.getSkyshimeday());
        // 抽出締年月日
        entity.setExtractshimeymd(DateTimeUtil.toNoSlashedDate(dto.getSkyshimedate()));
        // 締年月末日
        entity.setShimelastdd(dto.getSkyshimelastday());
        // 処理
        entity.setPrcskbn(dto.getPrcskbn());
        // 対象データ
        entity.setTrgtdat(dto.getTrgtdat());
        // 請求先コード
        entity.setSkysakicd(dto.getSkysakicd());
        // カナ名称
        entity.setSkysakinmkana(dto.getSkysakinmkana());
        // 得意先有無（有）
        entity.setTtumu1(Objects.toString(dto.getTtumu1(), SystemConstants.WORD_BLANK));
        // 得意先有無（無）
        entity.setTtumu2(Objects.toString(dto.getTtumu2(), SystemConstants.WORD_BLANK));
        // 請求先管理区分
        entity.setSkysakikanrikbn(
            Objects.toString(dto.getSkysakikanrikbn(), SystemConstants.WORD_BLANK));
        // 請求区分（親子）
        entity.setSkykbn1(Objects.toString(dto.getSkykbn1(), SystemConstants.WORD_BLANK));
        // 請求区分（一般）
        entity.setSkykbn2(Objects.toString(dto.getSkykbn2(), SystemConstants.WORD_BLANK));
        // 早出し
        entity.setHayadasiflg(Objects.toString(dto.getHayadasiflg(), SystemConstants.WORD_BLANK));
        // 回収方法リスト
        entity.setKaishuHohoList(kaishuHohoList);
        // 運用日付
        entity.setUnyoymd(dto.getDbManagementSession().getUnyoDate());
        // 明細最大件数
        entity.setMeisaimaxline(dto.getMeisaimaxline());

        // ACI本部NA区分リスト
        entity.setAcikeiyuflgList(acikeiyuflgList);

        return entity;
    }

    /**
     * （検索ボタン押下時）相関チェック
     * 
     * @param conditionDto 請求締検索条件Dto
     */
    public void searchCorrelationCheck(D1501W11SearchConditionDto conditionDto) {
        // 得意先有無選択チェック
        if (d1501w00service.isTtumuEmpty(conditionDto.getTtumu1(), conditionDto.getTtumu2())) {
            throw BusinessExceptionFactory.validationFailed(d1501w00service.getTtumuFormFieldList(),
                MessageID.W091, "得意先有無");
        }

        // 請求区分選択チェック
        if (d1501w00service.isSkykbnEmpty(conditionDto.getSkykbn1(), conditionDto.getSkykbn2())) {
            throw BusinessExceptionFactory
                .validationFailed(d1501w00service.getSkykbnFormFieldList(), MessageID.W091, "請求区分");
        }

        // 回収方法選択チェック
        if (d1501w00service.isKaishuhohokbnEmpty(conditionDto.getKaishuhohokbn1(),
            conditionDto.getKaishuhohokbn2(), conditionDto.getKaishuhohokbn3(),
            conditionDto.getKaishuhohokbn4(), conditionDto.getKaishuhohokbn5(),
            conditionDto.getKaishuhohokbn6(), conditionDto.getKaishuhohokbn7(),
            conditionDto.getKaishuhohokbn8(), conditionDto.getKaishuhohokbn9(),
            conditionDto.getKaishuhohokbn10(), conditionDto.getKaishuhohokbn11())) {
            throw BusinessExceptionFactory.validationFailed(
                d1501w00service.getKaishuhohokbnFormFieldList(), MessageID.W091, "回収方法");
        }
    }

    /**
     * （実行ボタン押下時）相関チェック
     * 
     * @param dto 請求締検索条件Dto
     * @param targetMeisaiDtoList 請求締対象明細Dtoリスト
     * @throws BusinessException 業務例外
     * @throws ParseException 型変換例外
     */
    public void execCorrelationCheck(D1501W11SearchConditionDto dto,
        List<D1501W11TargetMeisaiDto> targetMeisaiDtoList)
        throws BusinessException, ParseException {
        // 0件チェック
        if (targetMeisaiDtoList.size() == 0) {
            throw BusinessExceptionFactory.validationFailed(MessageID.W091, "請求先");
        }

        // 1件のみチェック
        if (Process.SEIKYU_FIX_CANCEL.equals(dto.getPrcskbn()) && targetMeisaiDtoList.size() > 1) {
            throw BusinessExceptionFactory.validationFailed(MessageID.W098, "1件のみ");
        }

        // 締可能期間外チェック
        if (Process.SEIKYU_FIX.equals(dto.getPrcskbn())) {
            // 運用日付取得
            final String unyoDate = dto.getDbManagementSession().getUnyoDate();
            // 請求締年月日To（請求締年月日－基準日数）取得
            String seikyuFixDateTo = this.getSeikyuFixDateTo(dto.getSkyshimedate());
            // 運用日付≦請求締年月日Toの場合
            if (DateTimeUtil.isLater(unyoDate, seikyuFixDateTo)) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W250, "運用日付が締可能期間の期間外",
                    "選択明細");
            }
        }

        // 締処理の場合に処理対象となる請求先の検索ボタンクリック時の処理状態が未処理だった場合
        // 別の処理により請求締が実行されているかをチェックする
        if (Process.SEIKYU_FIX.equals(dto.getPrcskbn())) {
            List<D1501W11TargetMeisaiDto> statusModified = targetMeisaiDtoList.stream()
                // 検索時に処理状態が未処理だったもののみチェック対象とする
                .filter(detailDto -> ProcessStatus.UNPROCESSED.equals(detailDto.getPrcsjotai()))
                // 請求残高テーブルに有効な請求残高が存在しているものに絞り込み
                .filter(detailDto -> isExistsBillingBalance(dto.getApkihoncd(), dto.getShopcd(),
                    detailDto.getSkysakicd(), dto.getShimedd1()))
                // Stream<E>→List<E>
                .collect(util.toList());
            // ログに該当した請求先の情報を出力
            statusModified.forEach(detailDto -> log.debug("{}-{}-{}の{}の請求残高は既に存在しています。",
                dto.getApkihoncd(), dto.getShopcd(), detailDto.getSkysakicd(), dto.getShimedd1()));
            // 1件でも該当するものがある場合はエラーとする
            if (!statusModified.isEmpty()) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W260,
                    "他の締処理により請求締された請求先が含まれています、データを確認してください。");
            }
        }

    }

    /**
     * 指定された請求先、締年月の有効な月締請求残高が存在するかを返します。
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param skysakicd 請求先コード
     * @param fixYearMonth 締年月
     * @return 存在する場合はtrue
     */
    private boolean isExistsBillingBalance(String apkihoncd, String shopcd, String skysakicd,
        String fixYearMonth) {
        S07T001TEntityExample condition = new S07T001TEntityExample();

        // 最新の請求締日を取得
        String skyshimedd = getSkyshimedd(apkihoncd, shopcd, skysakicd);

        if (!StringUtils.isEmpty(skyshimedd)) {
            // 請求残高抽出用に用いる締日（月末指定の際は実際の月末日にする）
            if ("31".equals(skyshimedd)) {
                skyshimedd =
                    DateTimeUtil.getLastDate(DateTimeUtil.toNoSlashedDateYYYYMM(fixYearMonth));
            }
        }

        // 検索条件
        condition.createCriteria()
            // AP基本コード
            .andApkihoncdEqualTo(apkihoncd)
            // ショップコード
            .andShopcdEqualTo(shopcd)
            // 請求先コード
            .andSkysakicdEqualTo(skysakicd)
            // 締年月
            .andShimedydmEqualTo(DateTimeUtil.toNoSlashedDateYYYYMM(fixYearMonth))
            // 締日
            .andShimeddEqualTo(skyshimedd.substring(skyshimedd.length() - 2))
            // 有効区分が1（有効）
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1)
            // 請求処理実施区分が1（月締請求締処理）
            .andSkyprcsjissikbnEqualTo(
                util.getShortValue(CommonConstants.SKYPRCSJISSIKBN_TSUKIJIME));
        return s07t001tDao.countByExample(condition) > 0L;
    }

    /**
     * 
     * 最新の請求締日を取得
     * 
     * @param apkihoncd
     * @param shopcd
     * @param skysakicd
     * @return 請求締日
     */
    private String getSkyshimedd(String apkihoncd, String shopcd, String skysakicd) {

        // 検索条件
        S02M017TEntityKey key = new S02M017TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setSkysakicd(skysakicd);

        // 検索
        S02M017TEntity entity = new S02M017TEntity();
        entity = s02M017TDao.selectByPrimaryKey(key);

        return entity.getSkyshimedd();
    }

    /**
     * 請求締年月日To取得
     * 
     * @param seikyuFixDate 請求締年月日
     * @return 請求締年月日To
     * @throws ParseException 型変換例外
     */
    private String getSeikyuFixDateTo(String seikyuFixDate) throws ParseException {
        // 基準日数取得
        String baseDays = this.getBaseDays();
        // 請求締年月日To算出
        Calendar result = Calendar.getInstance();
        result.setTime(new SimpleDateFormat("yyyy/MM/dd").parse(seikyuFixDate));
        result.add(Calendar.DATE, -Integer.parseInt(baseDays));
        return new SimpleDateFormat("yyyyMMdd").format(result.getTime());
    }

    /**
     * 基準日数取得
     * 
     * @return 基準日数
     */
    protected String getBaseDays() {
        S02M006TDto dto = new S02M006TDto();
        dto.setApkihoncd(CommonConstants.APCD_000);
        dto.setShopcd(CommonConstants.SYS_SHOPCD_000);
        dto.setSetteino(CommonConstants.SYS_SETTEINO_218);
        dto.setSetteicd(CommonConstants.SETTEICD_218_1);
        return s02m006tservice.searchForMoji(dto);
    }

    /**
     * 請求締日未来日チェック
     * 
     * @param dto 請求締検索条件Dto
     * @return チェック結果（True:警告なし／False:警告あり）
     * @throws BusinessException 業務例外
     * @throws ParseException 型変換例外
     */
    public boolean seikyuFixDateOverCheck(D1501W11SearchConditionDto dto)
        throws BusinessException, ParseException {
        // 運用日付取得
        String unyoDate = dto.getDbManagementSession().getUnyoDate();
        // 請求締年月日≦運用日付の場合
        if (DateTimeUtil.isLater(dto.getSkyshimedate(), unyoDate)) {
            return true;
        }

        // 請求締未来日比較用年月取得
        String compareYearMonth = this.getSeikyuFixAfterYearMonth(dto, dto.getSkyshimedate());
        // 締年月＝請求締未来日比較用年月の場合
        if (dto.getShimedd1().equals(compareYearMonth)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 請求締未来日比較用年月取得
     * 
     * @param dto 請求締検索条件Dto
     * @return 請求締未来日比較用年月
     * @throws BusinessException 業務例外
     * @throws ParseException 型変換例外
     */
    private String getSeikyuFixAfterYearMonth(D1501W11SearchConditionDto dto, String seikyuFixDate)
        throws BusinessException, ParseException {
        String unyoDate = dto.getDbManagementSession().getUnyoDate();
        String seikyuFixAfterDate;

        // 請求締未来日制御日数取得
        Integer afterDays = this.getSeikyuFixAfterDate(dto.getApkihoncd(), dto.getShopcd());

        if (afterDays != null) {
            // 請求締未来日付に運用日付＋請求締未来日制御日数を設定
            Calendar date = Calendar.getInstance();
            date.setTime(new SimpleDateFormat("yyyyMMdd").parse(unyoDate));
            date.add(Calendar.DATE, afterDays);
            seikyuFixAfterDate = new SimpleDateFormat("yyyyMMdd").format(date.getTime());

            // 請求締年月日＞請求締未来日付の場合
            if (seikyuFixDate.compareTo(seikyuFixAfterDate) == 1) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W115, "締年月日",
                    SystemConstants.WORD_BLANK, seikyuFixAfterDate);
            }
        } else {
            // 請求締未来日付に運用日付設定
            seikyuFixAfterDate = unyoDate;
        }

        // 請求締未来日比較用年月返却
        Date result = new SimpleDateFormat("yyyyMMdd").parse(seikyuFixAfterDate);
        return new SimpleDateFormat("yyyyMM").format(result);
    }

    /**
     * 請求締未来日取得
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @return 請求締未来日
     */
    protected Integer getSeikyuFixAfterDate(String apkihoncd, String shopcd) {
        try {
            S02M006TDto s02M006TDto = new S02M006TDto();
            s02M006TDto.setApkihoncd(apkihoncd);
            s02M006TDto.setShopcd(shopcd);
            s02M006TDto.setSetteino(CommonConstants.SYS_SETTEINO_907);
            s02M006TDto.setSetteicd(CommonConstants.SETTEICD_907_1);
            return s02m006tservice.searchForSuchi(s02M006TDto);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * 請求締処理チェック＆開始処理
     * 
     * @param conditionDto 請求締検索条件Dto
     * @throws Exception システム例外
     * @throws BusinessException 業務例外
     */
    @Transactional(rollbackFor = BusinessException.class)
    public void seikyushimeCheckAndGo(D1501W11SearchConditionDto conditionDto)
        throws Exception, BusinessException {
        try {
            // 請求締処理実行Entity生成
            S07T004TEntity entity = new S07T004TEntity();
            entity.setApkihoncd(conditionDto.getApkihoncd());
            entity.setShopcd(conditionDto.getShopcd());
            entity.setKonkaishimedd(DateTimeUtil.toNoSlashedDate(conditionDto.getSkyshimedate()));
            entity.setPrcsmode(conditionDto.getPrcskbn());
            entity.setKoshaapkihoncd(conditionDto.getDbManagementSession().getApkihoncd());
            entity.setKoshashopcd(conditionDto.getDbManagementSession().getShopcd());
            entity.setKoshacd(conditionDto.getDbManagementSession().getTnshacd());
            entity.setKshnunyoymd(conditionDto.getDbManagementSession().getUnyoDate());
            entity.setKshnipadrse(conditionDto.getDbManagementSession().getIpAddress());
            entity.setKshnpgmid(conditionDto.getDbManagementSession().getProgramId());
            entity.setKshnsystmymd(conditionDto.getDbManagementSession().getSystemDate());
            entity.setKshnsystmjikan(conditionDto.getDbManagementSession().getSystemTime());

            // 請求締処理チェック＆開始処理
            s07T004TDao.seikyushimeCheckAndGo(entity);
        } catch (Exception e) {
            Map<String, Object> message = getBusinessErrorMessage(e);
            throw BusinessExceptionFactory.validationFailed((MessageID) message.get(KEY_MESSAGE_ID),
                message.get(KEY_MESSAGE).toString());
        }
    }

    /**
     * 業務例外メッセージ取得
     * 
     * @param e 例外
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

    /** SQLエラーコードリスト */
    private final List<String> sqlErrorCodeList = Arrays.asList("ORA-20001", "ORA-20002",
        "ORA-20003", "ORA-20004", "ORA-20005", "ORA-20006", "ORA-20007", "ORA-20008", "ORA-20009",
        "ORA-20010", "ORA-20011", "ORA-20012", "ORA-20013", "ORA-20014", "ORA-20015");

    /**
     * SQL業務例外メッセージ判定
     * 
     * @param message メッセージ
     * @return SQL業務例外判定結果値（True:SQL業務例外／False:その他例外）
     */
    private boolean isSQLError(String message) {
        if (message.contains("SQLException")) {
            for (String sqlErrorCode : this.sqlErrorCodeList) {
                if (message.contains(sqlErrorCode)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 請求締対象データ準備
     * 
     * @param dto 請求締検索条件Dto
     * @param targetMeisaiDtoList 請求締対象明細Dtoリスト
     */
    @Transactional(rollbackFor = BusinessException.class)
    public void seikyuFixTargetDataPreparation(D1501W11SearchConditionDto dto,
        List<D1501W11TargetMeisaiDto> targetMeisaiDtoList) {
        for (D1501W11TargetMeisaiDto target : targetMeisaiDtoList) {
            // 未処理の場合
            if (ProcessStatus.UNPROCESSED.equals(target.getPrcsjotai())) {
                // 前回滞留情報テーブルデータ削除
                S19T001TEntityKey s19T001TEntityKey = new S19T001TEntityKey();
                s19T001TEntityKey.setApkihoncd(dto.getApkihoncd());
                s19T001TEntityKey.setShopcd(dto.getShopcd());
                s19T001TEntityKey.setSkysakicd(target.getSkysakicd());
                s19T001TEntityKey
                    .setShimedydm(DateTimeUtil.toNoSlashedDateYYYYMM(dto.getShimedd1()));
                s19T001TEntityKey.setSkyshimedd(dto.getSkyshimeday());
                s19T001TDao.deleteByPrimaryKey(s19T001TEntityKey);

                // 請求先マスタデータ取得
                S02M017TEntityKey s02M017TEntityKey = new S02M017TEntityKey();
                s02M017TEntityKey.setApkihoncd(dto.getApkihoncd());
                s02M017TEntityKey.setShopcd(dto.getShopcd());
                s02M017TEntityKey.setSkysakicd(target.getSkysakicd());
                S02M017TEntity s02M017TEntity = s02M017TDao.selectByPrimaryKey(s02M017TEntityKey);

                // 前回滞留情報テーブルデータ生成
                S19T001TEntity s19T001TEntity = new S19T001TEntity();
                s19T001TEntity.setApkihoncd(s02M017TEntity.getApkihoncd());
                s19T001TEntity.setShopcd(s02M017TEntity.getShopcd());
                s19T001TEntity.setSkysakicd(s02M017TEntity.getSkysakicd());
                s19T001TEntity.setShimedydm(DateTimeUtil.toNoSlashedDateYYYYMM(dto.getShimedd1()));
                s19T001TEntity.setSkyshimedd(dto.getSkyshimeday());
                s19T001TEntity.setTairyukbn(s02M017TEntity.getTairyukbn());
                s19T001TEntity.setTairyuhasseikeikadm(s02M017TEntity.getTairyuhasseikeikadm());
                s19T001TEntity.setTairyuhasseikaisu(s02M017TEntity.getTairyuhasseikaisu());
                s19T001TEntity.setYukokbn(CommonConstants.FLG_ON);
                s19T001TEntity.setToshaapkihoncd(dto.getDbManagementSession().getApkihoncd());
                s19T001TEntity.setToshashopcd(dto.getDbManagementSession().getShopcd());
                s19T001TEntity.setToshacd(dto.getDbManagementSession().getTnshacd());
                s19T001TEntity.setInptsystmymd(dto.getDbManagementSession().getSystemDate());
                s19T001TEntity.setInptsystmjikan(dto.getDbManagementSession().getSystemTime());
                s19T001TEntity.setInptunyoymd(dto.getDbManagementSession().getUnyoDate());
                s19T001TEntity.setInptipadrse(dto.getDbManagementSession().getIpAddress());
                s19T001TEntity.setInptpgmid(dto.getDbManagementSession().getProgramId());
                s19T001TEntity.setKoshaapkihoncd(dto.getDbManagementSession().getApkihoncd());
                s19T001TEntity.setKoshashopcd(dto.getDbManagementSession().getShopcd());
                s19T001TEntity.setKoshacd(dto.getDbManagementSession().getTnshacd());
                s19T001TEntity.setKshnsystmymd(dto.getDbManagementSession().getSystemDate());
                s19T001TEntity.setKshnsystmjikan(dto.getDbManagementSession().getSystemTime());
                s19T001TEntity.setKshnunyoymd(dto.getDbManagementSession().getUnyoDate());
                s19T001TEntity.setKshnipadrse(dto.getDbManagementSession().getIpAddress());
                s19T001TEntity.setKshnpgmid(dto.getDbManagementSession().getProgramId());
                s19T001TDao.insert(s19T001TEntity);
            }

            // 請求締ワークデータ生成
            S07W003TEntity s07W003TEntity = new S07W003TEntity();
            s07W003TEntity.setApkihoncd(dto.getApkihoncd());
            s07W003TEntity.setShopcd(dto.getShopcd());
            s07W003TEntity.setIraidd(dto.getDbManagementSession().getSystemDate());
            s07W003TEntity.setIraijikan(dto.getDbManagementSession().getSystemTime());
            s07W003TEntity.setSkysakicd(target.getSkysakicd());
            s07W003TEntity
                .setShimeprcskbn(this.getShimeprcskbn(dto.getPrcskbn(), target.getPrcsjotai()));
            s07W003TEntity.setToshaapkihoncd(dto.getDbManagementSession().getApkihoncd());
            s07W003TEntity.setToshashopcd(dto.getDbManagementSession().getShopcd());
            s07W003TEntity.setToshacd(dto.getDbManagementSession().getTnshacd());
            s07W003TEntity.setInptsystmymd(dto.getDbManagementSession().getSystemDate());
            s07W003TEntity.setInptsystmjikan(dto.getDbManagementSession().getSystemTime());
            s07W003TEntity.setInptunyoymd(dto.getDbManagementSession().getUnyoDate());
            s07W003TEntity.setInptipadrse(dto.getDbManagementSession().getIpAddress());
            s07W003TEntity.setInptpgmid(dto.getDbManagementSession().getProgramId());
            s07W003TEntity.setKoshaapkihoncd(dto.getDbManagementSession().getApkihoncd());
            s07W003TEntity.setKoshashopcd(dto.getDbManagementSession().getShopcd());
            s07W003TEntity.setKoshacd(dto.getDbManagementSession().getTnshacd());
            s07W003TEntity.setKshnsystmymd(dto.getDbManagementSession().getSystemDate());
            s07W003TEntity.setKshnsystmjikan(dto.getDbManagementSession().getSystemTime());
            s07W003TEntity.setKshnunyoymd(dto.getDbManagementSession().getUnyoDate());
            s07W003TEntity.setKshnipadrse(dto.getDbManagementSession().getIpAddress());
            s07W003TEntity.setKshnpgmid(dto.getDbManagementSession().getProgramId());
            s07W003TDao.insert(s07W003TEntity);
        }
    }

    /**
     * 締処理区分取得
     * 
     * @param prcskbn 処理
     * @param prcsjotai 処理状態
     * @return 締年月区分
     */
    protected String getShimeprcskbn(String prcskbn, String prcsjotai) {
        if (prcskbn.equals(Process.SEIKYU_FIX)) {
            return ProcessStatus.UNPROCESSED.equals(prcsjotai) ? FixProcess.FIX : FixProcess.REFIX;
        }
        return FixProcess.CANCEL;
    }
}
