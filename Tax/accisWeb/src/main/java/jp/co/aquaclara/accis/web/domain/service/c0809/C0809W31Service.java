package jp.co.aquaclara.accis.web.domain.service.c0809;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M003TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M006TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M008TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M013TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M017TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M041TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M046TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M050TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02V001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T002TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T005TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T006TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T007TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S05T002TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T004TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S13T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S13T004TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M003TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M003TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M006TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M007TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M041TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M050TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M050TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T005TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T005TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T006TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T006TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T007TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T007TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S05T002TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S05T002TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T004TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S13T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S13T001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S13T004TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S13T004TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S04T001TC0809W31Entity;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.web.controller.commons.DbManagementSession;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02AcptDlvryDetailDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02AcptDlvryDetailDto.DETAIL_TYPE;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02AcptDlvryDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02AcptDlvrySummaryDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02AcptRelationDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02ProcessingContextDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02SettingOptionDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02ShnDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02ShopCalculateOptionDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02ShopMasterDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02TokuisakiMasterDto;
import jp.co.aquaclara.accis.web.domain.dto.c0809.C0809W31SearchConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.c0809.C0809W31SearchResultDto;
import jp.co.aquaclara.accis.web.domain.dto.c0809.C0809W31UpdateDto;
import jp.co.aquaclara.accis.web.domain.service.c0800.UtilityService;
import jp.co.aquaclara.accis.web.domain.service.c0801.w02.C0801W02AcptDlvryDetailFindService;
import jp.co.aquaclara.accis.web.domain.service.c0801.w02.C0801W02AcptDlvryDetailPriceService;
import jp.co.aquaclara.accis.web.domain.service.c0801.w02.C0801W02AcptDlvryRegistrationService;
import jp.co.aquaclara.accis.web.domain.service.c0801.w02.C0801W02ShnService;
import jp.co.aquaclara.accis.web.domain.service.c0801.w02.C0801W02TokuisakiService;
import jp.co.aquaclara.accis.web.domain.service.master.S02M007TService;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * 配達関連処理（排圧予定データ修正）サービスクラス
 */
@Service
@Slf4j
public class C0809W31Service {

    private static final String ZERO = BigDecimal.ZERO.stripTrailingZeros().toPlainString();

    /** 受注配達テーブルDAO */
    @Autowired
    private S04T001TDao s04t001tDao;

    /** 受注配達明細テーブルDAO */
    @Autowired
    private S04T002TDao s04t002tDao;

    /** システム管理マスタDAO */
    @Autowired
    private S02M006TDao s02m006tDao;

    /** 担当者マスタDAO */
    @Autowired
    private S02M003TDao s02m003tDao;

    /** 展開マスタDAO */
    @Autowired
    private S02M008TDao s02m008tDao;

    /** 得意先マスタDAO */
    @Autowired
    private S02M013TDao s02m013tDao;

    /** 請求先マスタDAO */
    @Autowired
    private S02M017TDao s02m017tDao;

    /** 倉庫マスタDAO */
    @Autowired
    private S02M041TDao s02m041tDao;

    /** ショップマスタDAO */
    @Autowired
    private S02M046TDao s02m046tDao;

    /** 得意先実績テーブルDAO */
    @Autowired
    private S02M050TDao s02m050tDao;

    /** 商品マスタビューDAO */
    @Autowired
    private S02V001TDao s02v001tDao;

    /** 配達拠点受注アラームテーブルDAO */
    @Autowired
    private S04T005TDao s04t005tDao;

    /** 担当者受注アラームテーブルDAO */
    @Autowired
    private S04T006TDao s04t006tDao;

    /** 配達エリア受注アラームテーブルDAO */
    @Autowired
    private S04T007TDao s04t007tDao;

    /** 配達予定処理管理テーブルDAO */
    @Autowired
    private S05T002TDao s05t002tDao;

    /** 請求締処理管理テーブルDAO */
    @Autowired
    private S07T004TDao s07t004tDao;

    /** 現在庫テーブルDAO */
    @Autowired
    private S13T001TDao s13t001tDao;

    /** 在庫引当テーブルDAO */
    @Autowired
    private S13T004TDao s13t004tDao;

    /** 基本商品マスタ-Partsサービスクラス */
    @Autowired
    private S02M007TService basicItemMasterPartsService;

    /** ユーティリティサービス */
    @Autowired
    private UtilityService util;

    /** 商品サービス */
    @Autowired
    private C0801W02ShnService shnService;

    /** 受注配達明細検索サービス */
    @Autowired
    private C0801W02AcptDlvryDetailFindService detailFinder;

    /** 受注配達明細価格サービス */
    @Autowired
    private C0801W02AcptDlvryDetailPriceService priceService;

    /** 受注配達登録サービス */
    @Autowired
    private C0801W02AcptDlvryRegistrationService registService;

    /** 得意先サービス */
    @Autowired
    private C0801W02TokuisakiService tokuisakiService;

    /**
     * 配達予定データ修正検索処理
     * 
     * @param searchConditionDto 検索条件
     * @return 検索結果
     */
    public List<C0809W31SearchResultDto> search(C0809W31SearchConditionDto searchConditionDto) {
        // 相関チェック
        checkBeforeSearch(searchConditionDto);
        // 検索条件を生成する
        S04T001TC0809W31Entity searchConditionEntity = createCustomEntity(searchConditionDto);
        // 検索を行いEntityリストをDTOリストに変換して返す
        return createResultList(s04t001tDao.selectByC0809W31(searchConditionEntity));
    }

    /**
     * 配達予定データ修正検索条件を生成する。
     * 
     * @param searchConditionDto 検索条件DTO
     * @return 検索条件Entity
     */
    private S04T001TC0809W31Entity createCustomEntity(
        C0809W31SearchConditionDto searchConditionDto) {

        // 商品レギュラーボトル情報
        String regularItemsShncd = getShncdCondition(basicItemMasterPartsService.searchForCtgryList(
            CommonConstants.APCD_001, CommonConstants.STKBN_0, CommonConstants.LCTGRYCD_01,
            CommonConstants.MCTGRYCD_001, CommonConstants.SCTGRYCD_001));

        // 商品スリムボトル情報
        String shohinSlim = getShncdCondition(basicItemMasterPartsService.searchForCtgryList(
            CommonConstants.APCD_001, CommonConstants.STKBN_0, CommonConstants.LCTGRYCD_01,
            CommonConstants.MCTGRYCD_001, CommonConstants.SCTGRYCD_002));

        // 検索条件Entityを生成
        S04T001TC0809W31Entity searchConditionEntity = new S04T001TC0809W31Entity();
        // AP基本コード
        searchConditionEntity.setApkihoncd(searchConditionDto.getApkihoncd());
        // ショップコード
        searchConditionEntity.setShopcd(searchConditionDto.getShopcd());
        // 配達予定日1
        searchConditionEntity
            .setDlvryytidd(DateTimeUtil.toNoSlashedDate(searchConditionDto.getDlvryytidd1()));
        // 配達予定日2
        searchConditionEntity.setDlvryytidd2(searchConditionDto.getDlvryytidd2());
        // 配達担当者コード
        searchConditionEntity.setDlvrytnshacd(searchConditionDto.getDlvrytnshacd());
        // 伝票番号開始
        searchConditionEntity.setDnpynostr(searchConditionDto.getDnpynostr());
        // 伝票番号終了
        searchConditionEntity.setDnpynoend(searchConditionDto.getDnpynoend());
        // 配達エリア指定
        searchConditionEntity
            .setDlvryareashitei(getDlvryareacdList(searchConditionDto.getDlvryareashitei()));
        // 配達拠点コード
        searchConditionEntity.setDlvrykyotencd(searchConditionDto.getDlvrykyotencd());
        // 配達区分
        searchConditionEntity.setDlvrykbn1(searchConditionDto.getDlvrykbn1());
        // 配達区分他社便
        searchConditionEntity.setDlvrykbn2(searchConditionDto.getDlvrykbn2());
        // AP基本コード
        searchConditionEntity.setApkihoncd1(searchConditionDto.getApkihoncd1());
        // ショップコード
        searchConditionEntity.setShopcd1(searchConditionDto.getShopcd1());
        // 得意先コード
        searchConditionEntity.setTokuisakicd(searchConditionDto.getTokuisakicd());
        // 大分類コード
        searchConditionEntity.setLctgrycd(CommonConstants.LCTGRYCD_01);
        // 商品コード（レギュラー）
        searchConditionEntity.setShohinRegular(regularItemsShncd);
        // 商品コード（スリム）
        searchConditionEntity.setShohinSlim(shohinSlim);
        // 有効区分
        searchConditionEntity.setYukokbn(CommonConstants.YUKOKBN_1);
        // 伝票ステータス
        searchConditionEntity.setDnpysts(CommonConstants.DNPYSTS_11);
        // 表示順出力制御の取得
        S02M006TEntityExample sysMngMasterCondition = new S02M006TEntityExample();
        sysMngMasterCondition.createCriteria()
            // AP基本コード
            .andApkihoncdEqualTo(searchConditionDto.getApkihoncd())
            // ショップコード
            .andShopcdEqualTo(searchConditionDto.getShopcd())
            // 設定番号
            .andSetteinoEqualTo(CommonConstants.SYS_SETTEINO_140)
            // レコード区分
            .andRcrdkbnEqualTo(CommonConstants.RECORD_KBN_M)
            // 設定コード
            .andSetteicdEqualTo(CommonConstants.SETTEICD_140_1)
            // 有効区分
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        // システム管理マスタから表示順設定を取得し、検索条件Entityに設定する（クエリ定義では1か1以外かでソート順が決まる）
        s02m006tDao.selectByExample(sysMngMasterCondition).stream().findFirst()
            // システム管理マスタEntityから文字設定値を取得
            .map(entity -> entity.getMojisetteiatai())
            // 検索結果が設定されていればその値を設定
            .ifPresent(setting -> searchConditionEntity.setOrderd(setting));
        return searchConditionEntity;
    }

    /**
     * 基本商品マスタEntityリストを元に検索条件に用いる商品コードのカンマ区切り文字列を取得する
     * 
     * @param searchResultList 基本商品マスタEntityリスト
     * @return 商品コードをシングルクォートで囲ったものを感まで連結した文字列
     */
    private String getShncdCondition(List<S02M007TEntity> basicShnEntityList) {
        return basicShnEntityList.stream()
            // Entityをシングルクォートで囲った商品コードに変換
            .map(entity -> String.format("'%s'", entity.getShncd()))
            // カンマで連結
            .collect(Collectors.joining(","));
    }

    /**
     * 配達エリアコードCSV文字列をリストで取得する
     * 
     * @param dlvryareacdCsv 配達エリアコードCSV文字列
     * @return 配達エリアコードリスト（引数がnullまたは空文字の場合はnull）
     */
    private List<String> getDlvryareacdList(String dlvryareacdCsv) {
        if (StringUtils.isEmpty(dlvryareacdCsv)) {
            return null;
        }
        return Arrays.stream(dlvryareacdCsv.split(","))
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * 検索時相関チェックを実行する
     *
     * @param conditionDto 検索条件DTO
     */
    private void checkBeforeSearch(C0809W31SearchConditionDto conditionDto) {

        // 担当者コードチェック
        if (!StringUtils.isEmpty(conditionDto.getDlvrytnshacd())) {
            S02M003TEntityKey key = new S02M003TEntityKey();
            key.setApkihoncd(conditionDto.getApkihoncd());
            key.setShopcd(conditionDto.getShopcd());
            key.setTnshacd(conditionDto.getDlvrytnshacd());
            S02M003TEntity tnshaEntity = s02m003tDao.selectByPrimaryKey(key);
            // 担当者マスタ存在チェック
            if (Objects.isNull(tnshaEntity) || !util.isAvailable(tnshaEntity.getYukokbn())
                || !util.isFlagged(tnshaEntity.getDlvrytnshakbn())) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W084, "配達担当者コード");
            }
        }

        // 得意先コードチェック
        if (!StringUtils.isEmpty(conditionDto.getTokuisakicd())) {
            // SELECT実行
            S02M013TEntity tokuisakiEntity = getTokuisakiMaster(conditionDto.getApkihoncd(),
                conditionDto.getShopcd(), conditionDto.getTokuisakicd());
            // 得意先マスタ存在チェック
            if (Objects.isNull(tokuisakiEntity)
                || !util.isAvailable(tokuisakiEntity.getYukokbn())) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W084, "得意先コード");
            }
        }

    }

    /**
     * 検索結果Entityリストをもとに検索結果DTOリストを生成する
     * 
     * @param resultEntityList 検索結果Entityリスト
     * @return 検索結果DTOリスト
     */
    private List<C0809W31SearchResultDto> createResultList(
        List<S04T001TC0809W31Entity> resultEntityList) {
        // 検索結果が0件の場合
        if (resultEntityList.size() == 0) {
            throw BusinessExceptionFactory.dataNotFound(MessageID.W083);
        }
        return util.convertList(resultEntityList, C0809W31SearchResultDto::create);
    }

    /**
     * アラームチェック
     * 
     * @param updateDtoList 更新用DTOリスト
     * @return ワーニングメッセージリスト
     */
    public List<String> checkAlarm(List<C0809W31UpdateDto> updateDtoList) {
        if (Objects.isNull(updateDtoList) || updateDtoList.isEmpty()) {
            // リストが空の場合は何もせずに空のリストを返す
            return List.of();
        }
        // ワーニングメッセージリストを初期化
        List<String> messages = new LinkedList<>();
        // 受注関係DTOを生成
        C0801W02AcptRelationDto baseRelation = createRelation();
        // 更新DTOごとにアラームチェック
        for (C0809W31UpdateDto updateDto : updateDtoList) {
            // 得意先マスタ取得
            S02M013TEntity tokuisakiEntity = getTokuisakiMaster(updateDto.getApkihoncd(),
                updateDto.getShopcd(), updateDto.getTokuisakicdmei());
            // 受注配達テーブルから受注データ取得
            S04T001TEntity juchuEntity =
                getJuchuEntity(updateDto.getApkihoncd(), updateDto.getShopcd(),
                    updateDto.getNohindnpynomei(), updateDto.getEdanomei(), false);
            // ショップマスタからショップの情報を取得（配達管轄）
            S02M046TEntity shopMasterEntity = getShopMaster(juchuEntity.getDlvrykankatsuapkihoncd(),
                juchuEntity.getDlvrykankatsushopcd());
            // 受注関係DTOに受注配達テーブルの内容を反映
            C0801W02AcptRelationDto dnpyRelation =
                reflectToRelation(baseRelation, juchuEntity, tokuisakiEntity);
            // 委受託以外の場合のみアラームチェックを行う
            if (dnpyRelation.isIjutaku()) {
                log.debug("委受託の場合はアラームチェック対象外（得意先区分：{}, 受託配達顧客区分：{}）",
                    dnpyRelation.getTokuisakikbn(), dnpyRelation.getJutakudlvrycstkbn());
                continue;
            }
            // 受注配達明細テーブルから伝票の明細情報を取得
            List<S04T002TEntity> detailEntityList = getJuchuDetailEntities(juchuEntity).stream()
                // アラームチェック対象となる明細のみにフィルタ
                .filter(getDetailFilterForAlarm()).collect(util.toList());
            // 明細単位のアラームチェック
            for (S04T002TEntity detailEntity : detailEntityList) {
                // 明細の情報を反映した受注関係DTOを生成
                C0801W02AcptRelationDto detailRelation = dnpyRelation.copyWith(detailEntity);
                // 在庫引当チェック
                checkInventoryAlarm(juchuEntity, detailEntity, detailRelation, shopMasterEntity)
                    // ワーニングメッセージがあればリストに追加
                    .ifPresent(message -> messages.add(message));
                // 配達拠点受注アラームチェック
                checkDlvrykyotenAlarm(juchuEntity, detailEntity, detailRelation,
                    updateDto.getDlvryytiddmei())
                        // ワーニングメッセージがあればリストに追加
                        .ifPresent(message -> messages.add(message));
            }
            // 担当者受注アラームチェック
            checkTnshaAlarm(juchuEntity, updateDto.getDlvrytnshacdmei(),
                updateDto.getDlvryytiddmei())
                    // ワーニングメッセージがあればリストに追加
                    .ifPresent(message -> messages.add(message));
            // 配達エリア受注アラームチェック
            checkDlvryareaAlarm(juchuEntity, updateDto.getDlvryytiddmei())
                // ワーニングメッセージがあればリストに追加
                .ifPresent(message -> messages.add(message));
        }
        return messages;
    }

    /**
     * 在庫引当チェックを行い、アラームメッセージを取得する
     * 
     * @param juchuEntity 受注配達テーブルEntity
     * @param detailEntity 受注配達明細テーブルEntity
     * @param relationDto 受注関係DTO
     * @param shopMasterEntity ショップマスタEntity
     * @return 引当数量を考慮した在庫が閾値を下回った場合ワーニング
     */
    private Optional<String> checkInventoryAlarm(S04T001TEntity juchuEntity,
        S04T002TEntity detailEntity, C0801W02AcptRelationDto relationDto,
        S02M046TEntity shopMasterEntity) {
        // ショップの在庫管理区分がONの場合のみ
        if (!util.isFlagged(shopMasterEntity.getZaikokanrikbn())) {
            return Optional.empty();
        }
        // 商品マスタビューから商品の情報を取得(配達管轄）
        final S02V001TEntity shnEntity = getShnMasterView(juchuEntity.getDlvrykankatsuapkihoncd(),
            juchuEntity.getDlvrykankatsushopcd(), detailEntity.getShncd());
        // 商品の在庫管理対象フラグがONの場合のみ
        if (!util.isFlagged(shnEntity.getZaikokanritrgtflg())) {
            return Optional.empty();
        }
        final BigDecimal inventoryAlarmSetting =
            Objects.isNull(shnEntity.getZaikoaramusuryo()) ? BigDecimal.ZERO
                : BigDecimal.valueOf(shnEntity.getZaikoaramusuryo());
        // 製品水以外の場合にチェックを行う
        if (relationDto.isWater()) {
            return Optional.empty();
        }
        // 商品の在庫情報がない場合はチェック不要
        if (!isExistsCurrentInventoryData(juchuEntity, detailEntity)) {
            return Optional.empty();
        }
        // 現在庫テーブルから配達拠点の在庫数量を取得
        final BigDecimal currentInventorySuryo =
            s13t001tDao.getCurrentInventoryAmount(juchuEntity.getApkihoncd(),
                juchuEntity.getShopcd(), detailEntity.getShncd(), juchuEntity.getDlvrykyotencd());
        // 配達予定日の配達拠点受注アラームを取得
        return getInventoryReservation(juchuEntity, detailEntity)
            // 商品の在庫アラーム数量が0以外の場合のみ判定する
            .filter(entity -> !Objects.equals(BigDecimal.ZERO, inventoryAlarmSetting))
            // 配達予定データ修正で明細の受注数量が変わることはない→キャンセル処理不要
            // アラームチェック
            .filter(entity -> {
                // 引当反映済在庫数 ＝現在庫数－引当済在庫数
                BigDecimal actualInventorySuryo =
                    currentInventorySuryo.subtract(entity.getHikiatesumizaikokazu());
                log.debug("\t受注配達テーブル.配達拠点コード : {}", juchuEntity.getDlvrykyotencd());
                log.debug("\t受注配達明細テーブル.商品コード : {}", detailEntity.getShncd());
                log.debug("\t商品マスタビュー.在庫アラーム数量 : {}", inventoryAlarmSetting);
                log.debug("\t現在庫数 : {}", currentInventorySuryo);
                log.debug("\t引当反映済在庫数 : {}", actualInventorySuryo);
                log.debug("\t判定結果 : {}", util.orLess(inventoryAlarmSetting, actualInventorySuryo));
                return util.orLess(inventoryAlarmSetting, actualInventorySuryo);
            })
            // ワーニングメッセージに変換
            .map(entity -> util.getMessage(MessageID.W150,
                String.format("商品コード「{}」が在庫引当可能数", detailEntity.getShncd())));
    }

    /**
     * 在庫引当テーブルから在庫引当情報を取得する
     * 
     * @param juchuEntity 受注配達テーブルEntity
     * @param detailEntity 受注配達明細テーブルEntity
     * @return 在庫引当情報
     */
    private Optional<S13T004TEntity> getInventoryReservation(S04T001TEntity juchuEntity,
        S04T002TEntity detailEntity) {
        S13T004TEntityExample condition = new S13T004TEntityExample();
        condition.createCriteria()
            // AP基本コード（配達管轄）
            .andApkihoncdEqualTo(juchuEntity.getDlvrykankatsuapkihoncd())
            // ショップコード（配達管轄）
            .andShopcdEqualTo(juchuEntity.getDlvrykankatsushopcd())
            // 商品コード
            .andShncdEqualTo(detailEntity.getShncd())
            // 配達拠点コード
            .andDlvrykyotencdEqualTo(juchuEntity.getDlvrykyotencd())
            // 有効区分（有効）
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        return s13t004tDao.selectByExample(condition).stream().findFirst();
    }

    /**
     * 商品情報を取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param shncd 商品コード
     * @return 商品情報
     */
    private S02V001TEntity getShnMasterView(String apkihoncd, String shopcd, String shncd) {
        S02V001TEntityExample condition = new S02V001TEntityExample();
        condition.createCriteria()
            // AP基本コード
            .andApkihoncdEqualTo(apkihoncd)
            // ショップコード
            .andShopcdEqualTo(shopcd)
            // 商品コード
            .andShncdEqualTo(shncd)
            // ショップ有効区分が有効
            .andShopyukokbnEqualTo(CommonConstants.YUKOKBN_1);
        return s02v001tDao.selectByExample(condition).stream().findFirst().get();
    }

    /**
     * 配達拠点受注アラームチェックを行い、アラームメッセージを取得する
     * 
     * @param juchuEntity 受注配達テーブルEntity
     * @param detailEntity 受注配達明細テーブルEntity
     * @param acptRelation 受注関係DTO
     * @param ymd 日付
     * @return 閾値が受注数以下となった場合、ワーニングメッセージ
     */
    private Optional<String> checkDlvrykyotenAlarm(S04T001TEntity juchuEntity,
        S04T002TEntity detailEntity, C0801W02AcptRelationDto acptRelation, String ymd) {
        // 配達拠点受注アラームチェックの対象となる商品はアクアクララレギュラー・スリムのみ
        if (!acptRelation.isAquaclara()) {
            return Optional.empty();
        }
        // 配達予定日の配達拠点受注アラームを取得
        return getDlvrykyotenAlarmByDate(juchuEntity, detailEntity, ymd)
            // アラーム設定受注件数が0以外の場合のみ判定する
            .filter(entity -> !BigDecimal.ZERO.equals(entity.getAramusetteiacptsuryo()))
            // 配達予定日の変更がない場合は受注件数を減算しておく（キャンセル）
            .map(entity -> {
                if (Objects.equals(juchuEntity.getDlvryytidd(), ymd)) {
                    entity.setAcptkazu(entity.getAcptkazu().subtract(detailEntity.getAcptsuryo()));
                }
                log.debug("\t受注配達テーブル.配達拠点コード : {}", juchuEntity.getDlvrykyotencd());
                log.debug("\t受注配達明細テーブル.商品コード : {}", detailEntity.getShncd());
                log.debug("\t受注配達明細テーブル.受注数量", detailEntity.getAcptsuryo());
                log.debug("\t配達拠点受注アラームテーブル.日付 : {}", entity.getYmd());
                log.debug("\t配達拠点受注アラームテーブル.アラーム設定受注件数 : {}", entity.getAramusetteiacptsuryo());
                log.debug("^t配達拠点受注アラームテーブル.受注件数 : {}", entity.getAcptkazu());
                return entity;
            })
            // アラームチェック
            .filter(entity -> util.orLess(entity.getAramusetteiacptsuryo(),
                entity.getAcptkazu().add(detailEntity.getAcptsuryo())))
            // ワーニングメッセージに変換
            .map(entity -> util.getMessage(MessageID.W150, "配達拠点受注アラーム数"));
    }


    /**
     * 配達拠点受注アラームテーブルから指定された日付のアラーム情報を取得する
     * 
     * @param juchuEntity 受注配達テーブルEntity
     * @param detailEntity 受注配達明細テーブルEntity
     * @param ymd 日付
     * @return 配達拠点受注アラーム情報
     */
    private Optional<S04T005TEntity> getDlvrykyotenAlarmByDate(S04T001TEntity juchuEntity,
        S04T002TEntity detailEntity, String ymd) {
        S04T005TEntityExample condition = new S04T005TEntityExample();
        condition.createCriteria()
            // AP基本コード（配達管轄）
            .andApkihoncdEqualTo(juchuEntity.getDlvrykankatsuapkihoncd())
            // ショップコード（配達管轄）
            .andShopcdEqualTo(juchuEntity.getDlvrykankatsushopcd())
            // 商品コード
            .andShncdEqualTo(detailEntity.getShncd())
            // 配達拠点コード
            .andDlvrykyotencdEqualTo(juchuEntity.getDlvrykyotencd())
            // 日付
            .andYmdEqualTo(ymd)
            // 有効区分（有効）
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        return s04t005tDao.selectByExample(condition).stream().findFirst();
    }

    /**
     * 担当者受注アラームチェックを行い、アラームメッセージを取得する
     * 
     * @param juchuEntity 受注配達テーブルEntity
     * @param tnshacd 担当者コード
     * @param ymd 日付
     * @return 閾値が受注件数以下となった場合、ワーニングメッセージ
     */
    private Optional<String> checkTnshaAlarm(S04T001TEntity juchuEntity, String tnshacd,
        String ymd) {
        // 配達予定日の担当者受注アラームを取得
        return getTnshaAlarmByDate(juchuEntity, tnshacd, ymd)
            // アラーム設定受注件数が0以外の場合のみ判定する
            .filter(entity -> !BigDecimal.ZERO.equals(entity.getAramusetteiacptcnt()))
            // 配達予定日、配達担当者の変更がない場合は受注件数を減算しておく（キャンセル）
            .map(entity -> {
                if (Objects.equals(juchuEntity.getDlvryytidd(), ymd)) {
                    if (Objects.equals(juchuEntity.getDlvrytnshacd(), tnshacd)) {
                        entity.setAcptcnt(entity.getAcptcnt().subtract(BigDecimal.ONE));
                    }
                }
                log.debug("\t受注配達テーブル.配達担当者コード : {}", juchuEntity.getDlvrytnshacd());
                log.debug("\t担当者受注アラームテーブル.日付 : {}", entity.getYmd());
                log.debug("\t担当者受注アラームテーブル.アラーム設定受注件数 : {}", entity.getAramusetteiacptcnt());
                log.debug("^t担当者受注アラームテーブル.受注件数 : {}", entity.getAcptcnt());
                return entity;
            })
            // アラームチェック
            .filter(entity -> util.orLess(entity.getAramusetteiacptcnt(),
                entity.getAcptcnt().add(BigDecimal.ONE)))
            // ワーニングメッセージに変換
            .map(entity -> util.getMessage(MessageID.W150, "担当者受注アラーム数"));
    }

    /**
     * 指定された担当者、日付の担当者受注アラーム情報を取得する
     * 
     * @param juchuEntity 受注配達テーブルEntity
     * @param tnshacd 担当者コード
     * @param ymd 日付
     * @return 担当者受注アラーム情報
     */
    private Optional<S04T006TEntity> getTnshaAlarmByDate(S04T001TEntity juchuEntity, String tnshacd,
        String ymd) {
        S04T006TEntityExample condition = new S04T006TEntityExample();
        condition.createCriteria()
            // AP基本コード（配達管轄）
            .andApkihoncdEqualTo(juchuEntity.getDlvrykankatsuapkihoncd())
            // ショップコード（配達管轄）
            .andShopcdEqualTo(juchuEntity.getDlvrykankatsushopcd())
            // 配達エリアコード
            .andTnshacdEqualTo(tnshacd)
            // 配達拠点コード
            .andDlvrykyotencdEqualTo(juchuEntity.getDlvrykyotencd())
            // 日付
            .andYmdEqualTo(ymd)
            // 有効区分（有効）
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        return s04t006tDao.selectByExample(condition).stream().findFirst();
    }

    /**
     * 配達エリア受注アラームチェックを行い、アラームメッセージを取得する
     * 
     * @param juchuEntity 受注配達テーブルEntity
     * @param ymd 日付
     * @return 閾値が受注件数以下となった場合、ワーニングメッセージ
     */
    private Optional<String> checkDlvryareaAlarm(S04T001TEntity juchuEntity, String ymd) {
        // 配達予定日の配達エリア受注アラームを取得
        return getDlvryareaAlarmByDate(juchuEntity, ymd)
            // アラーム設定受注件数が0以外の場合のみ判定する
            .filter(entity -> !BigDecimal.ZERO.equals(entity.getAramusetteiacptcnt()))
            // 配達予定日の変更がない場合は受注件数を減算しておく（キャンセル）
            .map(entity -> {
                if (Objects.equals(juchuEntity.getDlvryytidd(), ymd)) {
                    entity.setAcptcnt(entity.getAcptcnt().subtract(BigDecimal.ONE));
                }
                log.debug("\t受注配達テーブル.配達エリアコード : {}", juchuEntity.getDlvryareacd());
                log.debug("\t配達エリア受注アラームテーブル.日付 : {}", entity.getYmd());
                log.debug("\t配達エリア受注アラームテーブル.アラーム設定受注件数 : {}", entity.getAramusetteiacptcnt());
                log.debug("^t配達エリア受注アラームテーブル.受注件数 : {}", entity.getAcptcnt());
                return entity;
            })
            // アラームチェック
            .filter(entity -> util.orLess(entity.getAramusetteiacptcnt(),
                entity.getAcptcnt().add(BigDecimal.ONE)))
            // ワーニングメッセージに変換
            .map(entity -> util.getMessage(MessageID.W150, "配達エリア受注アラーム数"));
    }

    /**
     * 配達エリア受注アラームテーブルから指定された日付のアラーム情報を取得する
     * 
     * @param juchuEntity 受注配達テーブルEntity
     * @param ymd 日付
     * @return 配達エリア受注アラーム情報
     */
    private Optional<S04T007TEntity> getDlvryareaAlarmByDate(S04T001TEntity juchuEntity,
        String ymd) {
        S04T007TEntityExample condition = new S04T007TEntityExample();
        condition.createCriteria()
            // AP基本コード（配達管轄）
            .andApkihoncdEqualTo(juchuEntity.getDlvrykankatsuapkihoncd())
            // ショップコード（配達管轄）
            .andShopcdEqualTo(juchuEntity.getDlvrykankatsushopcd())
            // 配達エリアコード
            .andDlvryareacdEqualTo(juchuEntity.getDlvryareacd())
            // 配達拠点コード
            .andDlvrykyotencdEqualTo(juchuEntity.getDlvrykyotencd())
            // 日付
            .andYmdEqualTo(ymd)
            // 有効区分（有効）
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        return s04t007tDao.selectByExample(condition).stream().findFirst();
    }

    /**
     * 在庫引当チェックに必要な現在庫情報が存在するかをチェックする
     * 
     * @param juchuEntity 受注配達テーブルEntity
     * @param detailEntity 受注配達明細テーブルEntity
     * @return 現在庫情報が存在すればtrue
     */
    private boolean isExistsCurrentInventoryData(S04T001TEntity juchuEntity,
        S04T002TEntity detailEntity) {
        S13T001TEntityExample currentInventoryCondition = new S13T001TEntityExample();
        currentInventoryCondition.createCriteria()
            // AP基本コード
            .andApkihoncdEqualTo(juchuEntity.getDlvrykankatsuapkihoncd())
            // ショップコード
            .andShopcdEqualTo(juchuEntity.getDlvrykankatsushopcd())
            // 商品コード
            .andShncdEqualTo(detailEntity.getShncd())
            // 有効区分
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<S13T001TEntity> currentInventoryList =
            s13t001tDao.selectByExample(currentInventoryCondition);
        if (currentInventoryList.isEmpty()) {
            log.debug("\t現在庫テーブルに商品(コード={})の在庫情報が存在しないため、チェック不要。", detailEntity.getShncd());
            return false;
        }
        S02M041TEntityExample warehouseContdition = new S02M041TEntityExample();
        warehouseContdition.createCriteria()
            // AP基本コード
            .andApkihoncdEqualTo(juchuEntity.getDlvrykankatsuapkihoncd())
            // ショップコード
            .andShopcdEqualTo(juchuEntity.getDlvrykankatsushopcd())
            // 配達拠点コード
            .andDlvrykyotencdEqualTo(juchuEntity.getDlvrykyotencd())
            // 有効区分
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<String> warehouseCodeList =
            util.convertList(s02m041tDao.selectByExample(warehouseContdition), e -> e.getSokocd());
        if (warehouseCodeList.isEmpty()) {
            log.debug("\t倉庫マスタに配達拠点(コード={})の倉庫情報が存在しないため、チェック不要。", juchuEntity.getDlvrykyotencd());
            return false;
        }
        // 現在庫情報と倉庫情報を倉庫コードでマッチングし、1件でも存在すれば現在庫情報ありとする
        return currentInventoryList.stream()
            .anyMatch(inventory -> warehouseCodeList.contains(inventory.getSokocd()));
    }

    /**
     * 受注関係DTOを生成する（受注配達テーブルや、受注配達明細テーブルの内容に依存しない項目のみ初期化）
     * 
     * @return 受注関係DTO
     */
    private C0801W02AcptRelationDto createRelation() {
        return C0801W02AcptRelationDto.builder()
            // 商品大分類コード：製品水
            .watercd(shnService.getWatercd())
            // 商品大分類コード：サーバ関連用
            .serverlcd(shnService.getServerLcd())
            // 商品中分類コード：サーバ関連本体
            .servermcd(shnService.getServerMcd())
            // 商品コードリスト：アクアクララ「レギュラー」
            .regularcdList(shnService.getItemcdlistByminor(CommonConstants.SCTGRYCD_001))
            // 商品コードリスト：アクアクララ「スリム」
            .slimcdList(shnService.getItemcdlistByminor(CommonConstants.SCTGRYCD_002)).build();
    }

    /**
     * 受注配達テーブル、得意先マスタの内容を反映した受注関係DTOを生成して返す
     * 
     * @param source 元となる受注関係DTO
     * @param juchuEntity 受注配達テーブルEntity
     * @param tokuisakiEntity 得意先マスタエンティティ
     * @return 受注配達テーブルの内容を反映した受注関係DTOの別インスタンス
     */
    private C0801W02AcptRelationDto reflectToRelation(C0801W02AcptRelationDto source,
        S04T001TEntity juchuEntity, S02M013TEntity tokuisakiEntity) {
        // 元となるDTOのインスタンスとは別のインスタンスを生成して返す
        return source.toBuilder()
            // 得意先区分
            .tokuisakikbn(tokuisakiEntity.getTokuisakikbn())
            // 受託配達顧客区分
            .jutakudlvrycstkbn(tokuisakiEntity.getJutakudlvrycstkbn())
            // 得意先状態
            .tokuisakijotai(tokuisakiEntity.getTokuisakijotai())
            // 状態ステータス
            .jotaists(tokuisakiEntity.getJotaists())
            // 受注区分
            .acptkbn(juchuEntity.getAcptkbn())
            // 受注区分詳細
            .acptkbnshs(juchuEntity.getAcptkbnshs())
            // 有効区分
            .yukokbn(juchuEntity.getYukokbn()).build();
    }

    /**
     * 配達予定処理を開始する（実際の更新処理とは別トランザクションとする）
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param dlvrykyotencd 配達拠点コード
     * @param dbManagementSession DB管理セッション情報
     */
    @Transactional
    public void startProcess(String apkihoncd, String shopcd, String dlvrykyotencd,
        DbManagementSession dbManagementSession) {
        // 相関チェック（同時実行チェック） … エラー時は例外スロー
        checkProcessStatus(apkihoncd, shopcd, dlvrykyotencd);
        // ステータスを「1：処理中」に更新
        C0801W02ProcessingContextDto context =
            C0801W02ProcessingContextDto.create(dbManagementSession);
        updateProcessStatus(apkihoncd, shopcd, dlvrykyotencd, CommonConstants.PRCSJOTAI_1, context);
    }

    /**
     * 配達予定処理を終了する（実際の更新処理とは別トランザクションとする）
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param dlvrykyotencd 配達拠点コード
     * @param dbManagementSession DB管理セッション
     */
    @Transactional
    public void finishProcess(String apkihoncd, String shopcd, String dlvrykyotencd,
        DbManagementSession dbManagementSession) {
        // ステータスを「0：未処理」に更新
        C0801W02ProcessingContextDto context =
            C0801W02ProcessingContextDto.create(dbManagementSession);
        updateProcessStatus(apkihoncd, shopcd, dlvrykyotencd, CommonConstants.PRCSJOTAI_0, context);
    }

    /**
     * 配達予定データ修正に伴い、配達予定処理管理テーブルのステータスを更新する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param dlvrykyotencd 配達拠点コード
     * @param status ステータス（0：未処理/１：処理中）
     * @param context コンテキスト
     */
    protected void updateProcessStatus(String apkihoncd, String shopcd, String dlvrykyotencd,
        String status, C0801W02ProcessingContextDto context) {
        // 指定された配達拠点の配達予定処理管理テーブルの行を取得（ロックする）
        final S05T002TEntity key = new S05T002TEntity();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setDlvrykyotencd(dlvrykyotencd);
        // 配達予定処理管理テーブルから配達拠点のレコードを取得（行ロック）する
        Optional<S05T002TEntity> locked =
            Optional.ofNullable(s05t002tDao.selectByPrimaryKeyForLock(key));
        // ロック結果からEntityを取得、取得できない場合はキーを元に新規レコード登録用のEntityを取得
        S05T002TEntity record = locked.orElseGet(() -> {
            key.setToshaapkihoncd(context.getApkihoncd());
            key.setToshashopcd(context.getShopcd());
            key.setToshacd(context.getTnshacd());
            key.setInptsystmymd(context.getSystemDate());
            key.setInptsystmjikan(context.getSystemTime());
            key.setInptunyoymd(context.getOperationDate());
            key.setInptipadrse(context.getIpAddress());
            key.setInptpgmid(context.getProgramId());
            return key;
        });
        // ステータス、有効区分設定
        record.setPrcsjotai(status);
        record.setYukokbn(CommonConstants.YUKOKBN_1);
        // 更新タイムスタンプ等の設定
        record.setKoshaapkihoncd(context.getApkihoncd());
        record.setKoshashopcd(context.getShopcd());
        record.setKoshacd(context.getTnshacd());
        record.setKshnsystmymd(context.getSystemDate());
        record.setKshnsystmjikan(context.getSystemTime());
        record.setKshnunyoymd(context.getOperationDate());
        record.setKshnipadrse(context.getIpAddress());
        record.setKshnpgmid(context.getProgramId());
        log.debug("S05T002T_配達予定処理管理テーブルへの反映内容: {}", record);
        // 登録または更新を実行
        int count = locked.isPresent() ? s05t002tDao.updateByPrimaryKey(record)
            : s05t002tDao.insert(record);
        log.debug("S05T002T_配達予定処理管理テーブル登録/更新件数 : {}", count);
    }

    /**
     * 指定された配達拠点について、配達予定処理が実行中かチェックする
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param dlvrykyotencd 配達拠点コード
     */
    private void checkProcessStatus(String apkihoncd, String shopcd, String dlvrykyotencd) {
        // 配達予定処理同時実行チェック
        S05T002TEntityExample dlvryYtikey = new S05T002TEntityExample();
        dlvryYtikey.createCriteria()
            // AP基本コード
            .andApkihoncdEqualTo(apkihoncd)
            // ショップコード
            .andShopcdEqualTo(shopcd)
            // 配達拠点コード
            .andDlvrykyotencdEqualTo(dlvrykyotencd)
            // 処理状態が「処理中」
            .andPrcsjotaiEqualTo(CommonConstants.PRCSJOTAI_1);
        if (!s05t002tDao.selectByExample(dlvryYtikey).isEmpty()) {
            throw BusinessExceptionFactory.validationFailed(MessageID.W148, "配達予定データ作成",
                "別の担当者で実行中");
        }
    }

    /**
     * 配達予定データ修正
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd 商品コード
     * @param dlvrykyotencd 配達拠点コード
     * @param updateDtoList 更新用DTOリスト
     * @param dbManagementSession DB管理セッション情報
     */
    @Transactional
    public void update(String apkihoncd, String shopcd, String dlvrykyotencd,
        List<C0809W31UpdateDto> updateDtoList, DbManagementSession dbManagementSession) {
        // 伝票ごとの相関チェック
        updateDtoList.forEach(updateDto -> checkBeforeUpdate(updateDto));
        // ショップ、配達拠点ごとの相関チェック
        checkBeforeUpdate(apkihoncd, shopcd, dlvrykyotencd);
        // コンテキスト初期化
        C0801W02ProcessingContextDto context =
            C0801W02ProcessingContextDto.create(dbManagementSession);
        // ショップマスタ取得
        S02M046TEntity shopEntity = getShopMaster(apkihoncd, shopcd);
        // 基本となる受注関係VOを生成
        C0801W02AcptRelationDto baseRelation = createRelation();
        // 更新対象となる伝票に対して処理を行う
        for (C0809W31UpdateDto updateDto : updateDtoList) {
            // 得意先マスタを取得
            S02M013TEntity tokuisakiEntity = getTokuisakiMaster(updateDto.getApkihoncd(),
                updateDto.getShopcd(), updateDto.getTokuisakicdmei());
            // 得意先実績テーブルを取得（行ロックする）
            S02M050TEntityKey jissekiKey = new S02M050TEntityKey();
            jissekiKey.setApkihoncd(tokuisakiEntity.getApkihoncd());
            jissekiKey.setShopcd(tokuisakiEntity.getShopcd());
            jissekiKey.setTokuisakicd(tokuisakiEntity.getTokuisakicd());
            S02M050TEntity jissekiEntity = s02m050tDao.selectByPrimaryKeyForLock(jissekiKey);
            // 受注配達テーブルを取得（行ロックする）
            S04T001TEntity juchuEntity = getJuchuEntity(apkihoncd, shopcd,
                updateDto.getNohindnpynomei(), updateDto.getEdanomei(), true);
            // 配達予定日の変更有無を判定する
            if (Objects.equals(juchuEntity.getDlvryytidd(), updateDto.getDlvryytiddmei())) {
                log.debug("配達予定日変更なし : {}", updateDto.getDlvryytiddmei());
                // 配達予定日変更なし
                if (!Objects.equals(juchuEntity.getDlvrytnshacd(),
                    updateDto.getDlvrytnshacdmei())) {
                    // 配達担当者が変更されている場合は担当者受注アラームを更新
                    updateTnshaAlarm(updateDto, juchuEntity, context);
                }
                // 受注配達テーブル更新用のEntityを生成
                S04T001TEntity record = createEntityForUpdate(juchuEntity, updateDto, context);
                log.debug("受注配達テーブル更新内容 : {}", record);
                // 受注配達テーブル更新条件を生成（初期表示時の更新タイムスタンプを条件に含める）
                S04T001TEntityExample condition = createConditionForUpdate(juchuEntity, updateDto);
                // 更新処理実行
                if (s04t001tDao.updateByExample(record, condition) == 0) {
                    // 排他チェックエラー
                    throw BusinessExceptionFactory.validationFailed(MessageID.W090);
                }
            } else {
                // 配達予定日変更あり
                // 担当者受注アラーム更新
                updateTnshaAlarm(updateDto, juchuEntity, context);
                // 配達エリア受注アラーム更新
                updateDlvryareaAlarm(updateDto, juchuEntity, context);
                // 受注配達明細テーブルから受注配達明細を取得する
                List<S04T002TEntity> allDetailEntityList = getJuchuDetailEntities(juchuEntity);
                // 受注伝票の内容を反映した受注関係VOを生成
                C0801W02AcptRelationDto dnpyRelation =
                    reflectToRelation(baseRelation, juchuEntity, tokuisakiEntity);
                // アラーム更新対象の明細のみ
                allDetailEntityList.stream().filter(getDetailFilterForAlarm())
                    // 配達拠点受注アラーム更新
                    .forEach(detailEntity -> {
                        C0801W02AcptRelationDto detailRelation =
                            dnpyRelation.copyWith(detailEntity);
                        updateDlvrykyotenAlarm(updateDto, juchuEntity, detailEntity, detailRelation,
                            context);
                    });
                // 受注伝票更新処理
                resetDeliveringData(updateDto, juchuEntity, shopEntity, tokuisakiEntity,
                    jissekiEntity, dnpyRelation, context);
            }
        }
    }

    /**
     * 更新時相関チェックを実行する（配達拠点単位）
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param dlvrykyotencd 配達拠点コード
     */
    private void checkBeforeUpdate(String apkihoncd, String shopcd, String dlvrykyotencd) {
        // 請求締処理管理テーブル処理状態チェック
        S07T004TEntityExample skyshimekey = new S07T004TEntityExample();
        skyshimekey.createCriteria()
            // AP基本コード
            .andApkihoncdEqualTo(apkihoncd)
            // ショップコード
            .andShopcdEqualTo(shopcd)
            // 処理状態が「終了/待機」以外
            .andPrcsjotaiNotEqualTo(CommonConstants.PRCSJOTAI_0);
        if (!s07t004tDao.selectByExample(skyshimekey).isEmpty()) {
            throw BusinessExceptionFactory.validationFailed(MessageID.W260, "処理中です。しばらくお待ちください");
        }
    }

    /**
     * 更新時相関チェックを実行する（伝票単位）
     *
     * @param dto 登録更新用DTO
     */
    private void checkBeforeUpdate(C0809W31UpdateDto dto) {

        // 担当者コードチェック
        S02M003TEntityKey tnshaKey = new S02M003TEntityKey();
        tnshaKey.setApkihoncd(dto.getApkihoncd());
        tnshaKey.setShopcd(dto.getShopcd());
        tnshaKey.setTnshacd(dto.getDlvrytnshacdmei());
        S02M003TEntity tnshaEntity = s02m003tDao.selectByPrimaryKey(tnshaKey);
        // 担当者マスタ存在チェック
        if (Objects.isNull(tnshaEntity)) {
            throw BusinessExceptionFactory.validationFailed(MessageID.W084, "配達担当者コード");
        }
        // 担当者マスタ有効チェック
        if (!util.isAvailable(tnshaEntity.getYukokbn())) {
            throw BusinessExceptionFactory.validationFailed(MessageID.W123, "配達担当者コード");
        }
        // 担当者マスタ配達担当者チェック
        if (!util.isFlagged(tnshaEntity.getDlvrytnshakbn())) {
            throw BusinessExceptionFactory.validationFailed(MessageID.W112, "配達担当者コードには配達担当者");
        }

        // 配達会社必須入力チェック
        if (Objects.equals(CommonConstants.KBN_DLVRY_TASHA, dto.getDlvrykbnmei())) {
            if (StringUtils.isEmpty(tnshaEntity.getShozokudlvrycmpnycd()) || !Objects
                .equals(tnshaEntity.getShozokudlvrycmpnycd(), dto.getDlvrycmpnycdmei())) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W126, "配達会社",
                    "配達担当者の所属配達会社");
            }
        }
    }

    /**
     * 配達拠点受注アラームを更新する
     * 
     * @param updateDto 更新用DTO
     * @param juchuEntity 受注配達テーブルEntity
     * @param detailEntity 受注配達明細テーブルEntity
     * @param acptRelation 受注関係DTO
     * @param context コンテキスト
     */
    private void updateDlvrykyotenAlarm(C0809W31UpdateDto updateDto, S04T001TEntity juchuEntity,
        S04T002TEntity detailEntity, C0801W02AcptRelationDto acptRelation,
        C0801W02ProcessingContextDto context) {
        // 配達拠点受注アラームの対象となる商品はアクアクララ レギュラー/スリムのみ
        if (!acptRelation.isAquaclara()) {
            return;
        }
        // 変更前の配達エリアの受注アラームをキャンセル
        Optional<S04T005TEntity> toCancelAlarm =
            getDlvrykyotenAlarmByDate(juchuEntity, detailEntity, juchuEntity.getDlvryytidd());
        // 担当者受注アラームが取得できた場合
        toCancelAlarm.ifPresent(key -> {
            // 行ロックを取得
            S04T005TEntity record = s04t005tDao.selectByPrimaryKeyForLock(key);
            if (Objects.nonNull(record)) {
                // キャンセル内容を設定
                record.setAcptkazu(record.getAcptkazu().subtract(detailEntity.getAcptsuryo()));
                record.setKoshaapkihoncd(context.getApkihoncd());
                record.setKoshashopcd(context.getShopcd());
                record.setKoshacd(context.getTnshacd());
                record.setKshnsystmymd(context.getSystemDate());
                record.setKshnsystmjikan(context.getSystemTime());
                record.setKshnunyoymd(context.getOperationDate());
                record.setKshnipadrse(context.getIpAddress());
                record.setKshnpgmid(context.getProgramId());
                // 更新
                s04t005tDao.updateByPrimaryKey(record);
            }
        });
        // 変更後の配達エリア受注アラームを更新
        Optional<S04T005TEntity> toUpdateAlarm =
            getDlvrykyotenAlarmByDate(juchuEntity, detailEntity, updateDto.getDlvryytiddmei());
        // 担当者受注アラームが取得できた場合
        toUpdateAlarm.ifPresent(key -> {
            // 行ロックを取得
            S04T005TEntity record = s04t005tDao.selectByPrimaryKeyForLock(key);
            if (Objects.nonNull(record)) {
                // 更新内容を設定
                record.setAcptkazu(record.getAcptkazu().add(detailEntity.getAcptsuryo()));
                record.setKoshaapkihoncd(context.getApkihoncd());
                record.setKoshashopcd(context.getShopcd());
                record.setKoshacd(context.getTnshacd());
                record.setKshnsystmymd(context.getSystemDate());
                record.setKshnsystmjikan(context.getSystemTime());
                record.setKshnunyoymd(context.getOperationDate());
                record.setKshnipadrse(context.getIpAddress());
                record.setKshnpgmid(context.getProgramId());
                // 更新
                s04t005tDao.updateByPrimaryKey(record);
            }
        });
    }

    /**
     * 配達エリア受注アラームを更新する
     * 
     * @param updateDto 更新用DTO
     * @param juchuEntity 受注配達テーブルEntity
     * @param context コンテキスト
     */
    private void updateDlvryareaAlarm(C0809W31UpdateDto updateDto, S04T001TEntity juchuEntity,
        C0801W02ProcessingContextDto context) {
        // 変更前の配達エリアの受注アラームをキャンセル
        Optional<S04T007TEntity> toCancelAlarm =
            getDlvryareaAlarmByDate(juchuEntity, juchuEntity.getDlvryytidd());
        // 配達エリア受注アラームが取得できた場合
        toCancelAlarm.ifPresent(key -> {
            // 行ロックを取得
            S04T007TEntity record = s04t007tDao.selectByPrimaryKeyForLock(key);
            if (Objects.nonNull(record)) {
                // キャンセル内容を設定
                record.setAcptcnt(record.getAcptcnt().subtract(BigDecimal.ONE));
                record.setKoshaapkihoncd(context.getApkihoncd());
                record.setKoshashopcd(context.getShopcd());
                record.setKoshacd(context.getTnshacd());
                record.setKshnsystmymd(context.getSystemDate());
                record.setKshnsystmjikan(context.getSystemTime());
                record.setKshnunyoymd(context.getOperationDate());
                record.setKshnipadrse(context.getIpAddress());
                record.setKshnpgmid(context.getProgramId());
                // 更新
                s04t007tDao.updateByPrimaryKey(record);
            }
        });
        // 変更後の配達エリア受注アラームを更新
        Optional<S04T007TEntity> toUpdateAlarm =
            getDlvryareaAlarmByDate(juchuEntity, updateDto.getDlvryytiddmei());
        // 配達エリア受注アラームが取得できた場合
        toUpdateAlarm.ifPresent(key -> {
            // 行ロックを取得
            S04T007TEntity record = s04t007tDao.selectByPrimaryKeyForLock(key);
            if (Objects.nonNull(record)) {
                // 更新内容を設定
                record.setAcptcnt(record.getAcptcnt().add(BigDecimal.ONE));
                record.setKoshaapkihoncd(context.getApkihoncd());
                record.setKoshashopcd(context.getShopcd());
                record.setKoshacd(context.getTnshacd());
                record.setKshnsystmymd(context.getSystemDate());
                record.setKshnsystmjikan(context.getSystemTime());
                record.setKshnunyoymd(context.getOperationDate());
                record.setKshnipadrse(context.getIpAddress());
                record.setKshnpgmid(context.getProgramId());
                // 更新
                s04t007tDao.updateByPrimaryKey(record);
            }
        });
    }

    /**
     * 担当者受注アラームを更新する
     * 
     * @param updateDto 更新用DTO
     * @param juchuEntity 受注配達テーブルEntity
     * @param context コンテキスト
     */
    private void updateTnshaAlarm(C0809W31UpdateDto updateDto, S04T001TEntity juchuEntity,
        C0801W02ProcessingContextDto context) {
        // 変更前の配達担当者の受注アラームをキャンセル（配達担当者が設定されている場合のみ）
        if (!StringUtils.isEmpty(juchuEntity.getDlvrytnshacd())) {
            Optional<S04T006TEntity> toCancelAlarm = getTnshaAlarmByDate(juchuEntity,
                juchuEntity.getDlvrytnshacd(), juchuEntity.getDlvryytidd());
            // 担当者受注アラームが取得できた場合
            toCancelAlarm.ifPresent(key -> {
                // 行ロックを取得
                S04T006TEntity record = s04t006tDao.selectByPrimaryKeyForLock(key);
                if (Objects.nonNull(record)) {
                    // キャンセル内容を設定
                    record.setAcptcnt(record.getAcptcnt().subtract(BigDecimal.ONE));
                    record.setKoshaapkihoncd(context.getApkihoncd());
                    record.setKoshashopcd(context.getShopcd());
                    record.setKoshacd(context.getTnshacd());
                    record.setKshnsystmymd(context.getSystemDate());
                    record.setKshnsystmjikan(context.getSystemTime());
                    record.setKshnunyoymd(context.getOperationDate());
                    record.setKshnipadrse(context.getIpAddress());
                    record.setKshnpgmid(context.getProgramId());
                    // 更新
                    s04t006tDao.updateByPrimaryKey(record);
                }
            });
        }
        // 変更後の配達担当者受注アラームを更新（配達担当者が設定されている場合のみ）
        if (!StringUtils.isEmpty(updateDto.getDlvrytnshacdmei())) {
            Optional<S04T006TEntity> toUpdateAlarm = getTnshaAlarmByDate(juchuEntity,
                updateDto.getDlvrytnshacdmei(), updateDto.getDlvryytiddmei());
            // 担当者受注アラームが取得できた場合
            toUpdateAlarm.ifPresent(key -> {
                // 行ロックを取得
                S04T006TEntity record = s04t006tDao.selectByPrimaryKeyForLock(key);
                if (Objects.nonNull(record)) {
                    // 更新内容を設定
                    record.setAcptcnt(record.getAcptcnt().add(BigDecimal.ONE));
                    record.setKoshaapkihoncd(context.getApkihoncd());
                    record.setKoshashopcd(context.getShopcd());
                    record.setKoshacd(context.getTnshacd());
                    record.setKshnsystmymd(context.getSystemDate());
                    record.setKshnsystmjikan(context.getSystemTime());
                    record.setKshnunyoymd(context.getOperationDate());
                    record.setKshnipadrse(context.getIpAddress());
                    record.setKshnpgmid(context.getProgramId());
                    // 更新
                    s04t006tDao.updateByPrimaryKey(record);
                }
            });
        }
    }

    /**
     * 受注アラームの対象となる明細に絞り込むためのフィルタを取得する
     */
    private Predicate<S04T002TEntity> getDetailFilterForAlarm() {
        Predicate<S04T002TEntity> onlyInput = e -> util.isFlagged(e.getNyuryokumeisaiflg());
        Predicate<S04T002TEntity> notHaifubutsu = e -> StringUtils.isEmpty(e.getHaifubutsucd());
        Predicate<S04T002TEntity> onlySingleShn = e -> util.isFlagged(e.getTanpinmeisaiflg());
        Predicate<S04T002TEntity> onlyShn =
            e -> CommonConstants.KBN_MEISAI_SHOHIN.equals(e.getMeisaikbn());
        return onlyInput.and(notHaifubutsu).and(onlySingleShn).and(onlyShn);
    }

    /**
     * 配達予定日の変更に伴い、配達中の受注データをリセットする
     * 
     * @param updateDto 更新用DTO
     * @param juchuEntity 受注配達テーブルEntity
     * @param shopEntity ショップマスタEntity
     * @param tokuisakiEntity 得意先マスタEntity
     * @param jissekiEntity 得意先実績テーブルEntity
     * @param dnpyRelation 受注伝票の内容を反映してある受注関係DTO
     * @param context コンテキスト
     */
    private void resetDeliveringData(C0809W31UpdateDto updateDto, S04T001TEntity juchuEntity,
        S02M046TEntity shopEntity, S02M013TEntity tokuisakiEntity, S02M050TEntity jissekiEntity,
        C0801W02AcptRelationDto dnpyRelation, C0801W02ProcessingContextDto context) {
        // ショップ情報
        C0801W02ShopMasterDto shopDto = C0801W02ShopMasterDto.create(shopEntity);
        C0801W02ShopCalculateOptionDto shopCalcOption = detailFinder.createShopCalculateOptionDto(
            shopDto, updateDto.getDlvryytiddmei(), juchuEntity.getAcptdd());
        // 得意先情報
        C0801W02TokuisakiMasterDto tokuisakiDto =
            C0801W02TokuisakiMasterDto.create(tokuisakiEntity);
        // 受注伝票取得
        C0801W02AcptDlvryDto juchuDto = C0801W02AcptDlvryDto.create(juchuEntity, tokuisakiDto);
        // 変更された配達予定日、配達区分を受注配達DTOに設定
        juchuDto.setDlvryytidd(updateDto.getDlvryytiddmei());
        juchuDto.setDlvrykbn(updateDto.getDlvrykbnmei());
        // 集計DTOを初期化
        juchuDto.setSummary(C0801W02AcptDlvrySummaryDto.builder().build());
        // 商品入力行以外も含めた明細を取得するためのオプション設定
        C0801W02SettingOptionDto optionDto =
            C0801W02SettingOptionDto.builder().regist(true).build();
        // 受注配達明細DTOを取得（単価情報等もまとめて取得）
        List<C0801W02AcptDlvryDetailDto> detailDtoList =
            detailFinder.getAcptDlvryDetailList(juchuDto, shopDto, optionDto);
        // 入力・商品行のみの明細リスト
        List<C0801W02AcptDlvryDetailDto> inputShnDetailDtoList = detailDtoList.stream()
            // 入力明細フラグがON（構成商品や調整消費税の明細は除外）
            .filter(detailDto -> util.isFlagged(detailDto.getNyuryokumeisaiflg()))
            // 明細区分が商品（配布物の明細はこの条件で除外）
            .filter(detailDto -> CommonConstants.KBN_MEISAI_SHOHIN.equals(detailDto.getMeisaikbn()))
            .collect(util.toList());
        // 商品入力行の単価再設定および再計算
        for (C0801W02AcptDlvryDetailDto detailDto : inputShnDetailDtoList) {
            log.debug("商品入力行の単価再設定および再計算");
            log.debug("\t受注伝票明細番号 : {} / 受注配達明細枝番号 : {} / 商品コード : {}",
                detailDto.getAcptdnpymeisaino(), detailDto.getAcptdnpymeisaiedano(),
                detailDto.getShncd());
            detailFinder.reEditUnt(true, juchuDto.getDlvrykbn(), juchuDto.getDlvryytidd(),
                detailDto, shopCalcOption, dnpyRelation);
            detailDto.setShnDto(shnService.getShnInfo(detailDto.getApkihoncd(),
                detailDto.getShopcd(), detailDto.getShncd()), juchuDto.getDlvryytidd());
            detailDto.setType(DETAIL_TYPE.MODIFIED);
        }
        // セット商品のみの明細リスト（セット商品の単価再設定、再計算は既に行っている）
        List<C0801W02AcptDlvryDetailDto> stShnDetailDtoList = inputShnDetailDtoList.stream()
            // セット商品コードが設定されている
            .filter(detailDto -> !StringUtils.isEmpty(detailDto.getStshncd()))
            .collect(util.toList());
        // セット品構成明細のみのリスト（調整消費税明細も含む）
        List<C0801W02AcptDlvryDetailDto> allChildShnDetailDtoList = detailDtoList.stream()
            // 入力明細フラグがOFF
            .filter(detailDto -> !util.isFlagged(detailDto.getNyuryokumeisaiflg()))
            // セット商品コードが設定されている
            .filter(detailDto -> !StringUtils.isEmpty(detailDto.getStshncd()))
            .collect(util.toList());
        // セット商品の構成商品に関する単価再設定および再計算
        for (C0801W02AcptDlvryDetailDto stShnDetailDto : stShnDetailDtoList) {
            // 構成商品明細リストから自身の構成明細を取得
            List<C0801W02AcptDlvryDetailDto> childShnDetailDtoList =
                allChildShnDetailDtoList.stream()
                    // 受注伝票明細番号が同じもの
                    .filter(child -> Objects.equals(child.getAcptdnpymeisaino(),
                        stShnDetailDto.getAcptdnpymeisaino()))
                    .collect(util.toList());
            // 構成商品について単価再設定と再計算を行い、調整消費税が必要な場合は取得する
            reCalcBundleShnDetailList(juchuDto, stShnDetailDto, childShnDetailDtoList,
                shopCalcOption)
                    // 調整消費税明細を追加する
                    .ifPresent(d -> detailDtoList.add(d));
        }

        // 受注配達明細をソート
        detailDtoList.sort(C0801W02AcptDlvryDetailDto.getOrderBy());
        // 受注配達明細テーブル更新
        for (C0801W02AcptDlvryDetailDto detailDto : detailDtoList) {
            log.debug("単価再設定および再計算後の受注配達明細(配達予定日 : {})", juchuDto.getDlvryytidd());
            debugLogDetail(detailDto);
            // 明細をサマリして、受注伝票の配達予定集計項目に必要な情報を算出
            if (detailDto.isCountable()) {
                log.debug("\t集計可能な受注配達明細を集計（単品かつ商品行）");
                juchuDto.getSummary().addAll(detailDto);
            }
            // 受注配達明細テーブルに再計算結果等の変更内容を反映
            saveDetail(juchuDto, detailDto, context);
        }

        // 受注伝票に対する再計算
        // 受注伝票の受注金額、受注消費税金額等を再計算
        log.debug("受注伝票の再計算前");
        debugLogJuchuDnpy(juchuEntity, juchuDto);
        // 受注伝票の金額計算時に用いる明細は商品・入力行のみ（配布物明細や構成商品の明細は必要ない）
        priceService.calculateOrderSlip(juchuDto, inputShnDetailDtoList, shopCalcOption);
        log.debug("受注伝票の再計算後");
        debugLogJuchuDnpy(juchuEntity, juchuDto);

        // 得意先実績テーブル更新（計上前受注金額更新）
        tokuisakiService.updateBeforeRecordAcptKin(juchuDto, juchuEntity, jissekiEntity,
            shopCalcOption, context);

        // 受注配達テーブル更新条件
        S04T001TEntityExample updateCondition = createConditionForUpdate(juchuEntity, updateDto);
        // 更新内容をDTOからEntityに反映
        // 配達予定日の変更有無に関係なく編集する項目の設定
        S04T001TEntity updatedJuchuEntity =
            createEntityForUpdate(juchuDto, juchuEntity, updateDto, tokuisakiDto, context);
        log.debug("受注配達テーブルの更新内容 : {}", updatedJuchuEntity);
        // 受注配達テーブル更新
        if (s04t001tDao.updateByExample(updatedJuchuEntity, updateCondition) == 0) {
            // 排他チェックエラー
            throw BusinessExceptionFactory.validationFailed(MessageID.W090);
        }

        // 代引時未締売上データ解除
        registService.releaseUnclosedSales(context, juchuDto);
    }

    /**
     * 受注配達明細テーブルに単価再設定および再計算結果を保存する
     * 
     * @param juchuDto 受注配達DTO
     * @param detailDto 受注配達明細DTO
     * @param context コンテキスト
     */
    private void saveDetail(C0801W02AcptDlvryDto juchuDto, C0801W02AcptDlvryDetailDto detailDto,
        C0801W02ProcessingContextDto context) {
        S04T002TEntityKey detailKey = detailDto.createEntityKey();
        switch (detailDto.getType()) {
            case TO_ADD:
                // 調整消費税の受注配達明細の追加
                S04T002TEntity detailEntityForInsert = detailDto.createNewEntity(context);
                detailDto.fillPropertiesToEntity(detailEntityForInsert, context, juchuDto);
                s04t002tDao.insert(detailEntityForInsert);
                break;
            case TO_REMOVE:
                // 調整消費税の受注配達明細削除
                s04t002tDao.deleteByPrimaryKey(detailKey);
                break;
            case MODIFIED:
                // 行ロックを取得
                S04T002TEntity detailEntityForUpdate =
                    s04t002tDao.selectByPrimaryKeyForLock(detailKey);
                // 更新タイムスタンプ設定および単価再設定結果、再計算結果を反映
                detailDto.setContextForUpdate(detailEntityForUpdate, context);
                detailEntityForUpdate
                    .setHyojununt(detailDto.getDecimalValue(detailDto.getHyojununt()));
                detailEntityForUpdate.setAcptunt(detailDto.getDecimalValue(detailDto.getAcptunt()));
                detailEntityForUpdate.setAcptuntshohitaxkin(
                    detailDto.getLongValue(detailDto.getAcptuntshohitaxkin()));
                detailEntityForUpdate.setAcptkin(detailDto.getLongValue(detailDto.getAcptkin()));
                detailEntityForUpdate
                    .setAcptshohitaxkin(detailDto.getLongValue(detailDto.getAcptshohitaxkin()));
                // 受注配達明細テーブル更新
                s04t002tDao.updateByPrimaryKey(detailEntityForUpdate);
                break;
            default:
                // 明細のタイプが上記以外の場合はDB更新対象外
                break;
        }
    }

    /**
     * セット商品の構成商品の単価再設定および再計算を行い、調整消費税明細を返す
     * 
     * @param juchuDto 受注配達DTO
     * @param stShnDetailDto セット商品の受注配達明細DTO
     * @param childShnDetailDtoList 構成商品の受注配達明細DTOリスト
     * @param shopCalcOption ショップ計算オプション
     * @return 調整消費税の受注配達明細DTO（追加する必要がない場合はempty)
     */
    private Optional<C0801W02AcptDlvryDetailDto> reCalcBundleShnDetailList(
        C0801W02AcptDlvryDto juchuDto, C0801W02AcptDlvryDetailDto stShnDetailDto,
        List<C0801W02AcptDlvryDetailDto> childShnDetailDtoList,
        C0801W02ShopCalculateOptionDto shopCalcOption) {
        // セット商品の商品情報
        C0801W02ShnDto stShnDto = stShnDetailDto.getShnDto();
        // 構成商品の商品情報リストを展開マスタの情報を元に取得する
        List<C0801W02ShnDto> bundleShnDtoList = util.convertList(
            s02m008tDao.getSetShohinList(stShnDetailDto.getApkihoncd(), stShnDetailDto.getShopcd(),
                stShnDetailDto.getShncd()),
            bundleEntity -> shnService.getBundleShnInfo(stShnDto, bundleEntity));
        // 構成商品に対して単価再設定および再計算
        bundleShnDtoList.stream()
            .forEachOrdered(util.withIndexConsumer(0, (bundleShnDto, index) -> {
                // セット商品は受注中または配達中の受注がある場合、構成を変更することは出来ない（商品マスタの仕様）
                // 受注登録時、セット商品の展開処理では展開マスタの連番順に明細を生成しており、
                // 展開マスタを連番順に取得していれば受注配達明細側と同じ並び順をしている（調整消費税の明細が存在する場合、最後に追加している）
                C0801W02AcptDlvryDetailDto bundleDetailDto = childShnDetailDtoList.get(index);
                reCalcBundleDetail(juchuDto, stShnDetailDto, bundleDetailDto, bundleShnDto,
                    shopCalcOption);
            }));
        // 構成商品の明細リストから調整消費税の明細を取得
        log.debug("調整消費税処理");
        Optional<C0801W02AcptDlvryDetailDto> adjustedTaxDetail = childShnDetailDtoList.stream()
            .filter(bundleDetailDto -> CommonConstants.KBN_MEISAI_SHOHIZEI
                .equals(bundleDetailDto.getMeisaikbn()))
            .findFirst();
        log.debug("\t登録済調整消費税明細有無 : {}", adjustedTaxDetail.isPresent());
        // 配達予定日がインボイス施行日より前の場合、調整消費税明細を追加または変更する
        if (!shopCalcOption.isAfterShopInvoiceDate(juchuDto.getDlvryytidd())) {
            log.debug("\tインボイス施行日前");
            // 構成商品の受注明細DTOリストの消費税金額を集計
            BigDecimal bundleTaxSummary = childShnDetailDtoList.stream()
                .filter(bundleDetailDto -> CommonConstants.KBN_MEISAI_SHOHIN
                    .equals(bundleDetailDto.getMeisaikbn()))
                .map(bundleDetailDto -> bundleDetailDto
                    .getDecimalValue(bundleDetailDto.getAcptshohitaxkin()))
                .reduce((current, acptshohitaxkin) -> current.add(acptshohitaxkin))
                .orElse(BigDecimal.ZERO);
            log.debug("\t構成商品明細の受注消費税金額をサマリ : {}", bundleTaxSummary);
            // セット商品の受注配達明細の消費税と集計値に差を算出
            BigDecimal diff = stShnDetailDto.getDecimalValue(stShnDetailDto.getAcptshohitaxkin())
                .subtract(bundleTaxSummary);
            log.debug("\tセット商品の受注消費税金額と構成商品の受注消費税金額サマリの差額 : {}", diff);
            // セット商品の消費税と比較、差額がある場合は調整消費税明細を編集
            if (BigDecimal.ZERO.compareTo(diff) != 0) {
                if (adjustedTaxDetail.isPresent()) {
                    // 調整消費税明細が存在する場合は受注消費税金額に算出した差額を設定
                    log.debug("\t差額ありの場合は既存の調整消費税明細の受注消費税金額に差額を設定");
                    adjustedTaxDetail.get()
                        .setAcptshohitaxkin(adjustedTaxDetail.get().getString(diff));
                    adjustedTaxDetail.get().setType(DETAIL_TYPE.MODIFIED);
                    return Optional.empty();
                } else {
                    // 存在しない場合は調整消費税明細を作り、受注伝票に紐づいている受注配達明細リストに追加する
                    log.debug("\t差額ありの場合は調整消費税明細を作成して追加");
                    C0801W02AcptDlvryDetailDto taxDetailDto = detailFinder.createadjustedTaxDetail(
                        juchuDto, stShnDetailDto, childShnDetailDtoList, diff);
                    taxDetailDto.setType(DETAIL_TYPE.TO_ADD);
                    return Optional.ofNullable(taxDetailDto);
                }
            } else {
                // 差額がない場合、既に存在していた調整消費税明細は不要
                log.debug("\t差額がなくなった場合は既存の調整消費税明細は削除対象");
                adjustedTaxDetail
                    .ifPresent(taxDetailDto -> taxDetailDto.setType(DETAIL_TYPE.TO_REMOVE));
                return Optional.empty();
            }
        } else {
            // インボイス施行日以降は調整消費税の受注配達明細は不要
            log.debug("インボイス施行日以降は調整消費税明細不要");
            adjustedTaxDetail
                .ifPresent(taxDetailDto -> taxDetailDto.setType(DETAIL_TYPE.TO_REMOVE));
            return Optional.empty();
        }

    }

    /**
     * セット商品の構成商品について単価再設定および再計算を行う
     * 
     * @param juchuDto 受注配達DTO
     * @param stShnDetailDto セット商品の受注配達明細DTO（親）
     * @param bundleDetailDto 構成商品の受注配達明細DTO（子）
     * @param bundleShnDto 構成商品情報（展開マスタの内容を反映したもの）
     * @param shopCalcOption ショップ計算オプション
     */
    private void reCalcBundleDetail(C0801W02AcptDlvryDto juchuDto,
        C0801W02AcptDlvryDetailDto stShnDetailDto, C0801W02AcptDlvryDetailDto bundleDetailDto,
        C0801W02ShnDto bundleShnDto, C0801W02ShopCalculateOptionDto shopCalcOption) {
        bundleDetailDto.setType(DETAIL_TYPE.MODIFIED);
        log.debug("セット構成商品の単価再設定および再計算");
        log.debug("構成商品情報 : {}", bundleShnDto);
        log.debug("\t受注伝票明細番号 : {} / 受注配達明細枝番号 : {} / セット商品コード : {} / 商品コード : {}",
            bundleDetailDto.getAcptdnpymeisaino(), bundleDetailDto.getAcptdnpymeisaiedano(),
            bundleDetailDto.getStshncd(), bundleDetailDto.getShncd());
        // 受注配達明細に商品情報を設定
        bundleDetailDto.setShnDto(bundleShnDto, juchuDto.getDlvryytidd());
        // 受注関係DTOを設定
        bundleDetailDto.setAcptRelation(stShnDetailDto.getAcptRelation().copyWith(bundleDetailDto));
        // 標準単価再設定
        log.debug("\t変更前標準単価 : {}", bundleDetailDto.getHyojununt());
        bundleDetailDto.setHyojununt(
            bundleDetailDto.getHyojunShnPriceDto().getPriceByDlvrykbn(juchuDto.getDlvrykbn()));
        log.debug("\t変更後標準単価 : {}", bundleDetailDto.getHyojununt());
        // セット商品が有料の場合(無料の場合は構成商品の受注単価は0円なので再設定不要）
        if (stShnDetailDto.getAcptRelation().isYuryo()) {
            // 特別単価情報を取得
            Optional<String> tokubetsuUnt = tokuisakiService.getBundleShnTokubetsuUnt(
                // AP基本コード
                juchuDto.getApkihoncd(),
                // ショップコード
                juchuDto.getShopcd(),
                // 得意先コード
                juchuDto.getTokuisakicd(),
                // 配達予定日
                juchuDto.getDlvryytidd(),
                // セット商品コード
                bundleDetailDto.getStshncd(),
                // 連番
                bundleShnDto.getSeq(),
                // 料金種別特別単価フラグ
                stShnDetailDto.getRknkindtokubetsuuntflg())
                // DTOが設定されてれば配達区分に応じた特別価格を取得
                .map(d -> d.getPriceByDlvrykbn(juchuDto.getDlvrykbn()));
            // 受注単価設定（特別単価が取得できれば設定、そうでない場合はショップ価格を設定）
            log.debug("\t変更前受注単価 : {}", bundleDetailDto.getAcptunt());
            bundleDetailDto.setAcptunt(tokubetsuUnt.orElse(
                bundleDetailDto.getShopShnPriceDto().getPriceByDlvrykbn(juchuDto.getDlvrykbn())));
            log.debug("\t変更後受注単価 : {}", bundleDetailDto.getAcptunt());
            // 受注金額、受注消費税金額の再計算
            priceService.calculateAcptkin(juchuDto.getDlvryytidd(), bundleDetailDto);
            // インボイス施行日以降は構成商品の受注消費税金額を0円にする
            if (shopCalcOption.isAfterShopInvoiceDate(juchuDto.getDlvryytidd())) {
                log.debug("\tインボイス施行日以降は構成商品の受注消費税金額を0円にする");
                bundleDetailDto.setAcptshohitaxkin(ZERO);
            }
            // 非課税/不課税の場合の消費税額設定
            if (util.isFlagged(stShnDetailDto.getShnDto().getTaxsetteikbn())) {
                // セット商品が税設定するかつ構成商品が非課税または不課税の場合
                if (CommonConstants.KBN_TAX_HIKAZEI.equals(bundleDetailDto.getTaxkbn())
                    || CommonConstants.KBN_TAX_HUKAZEI.equals(bundleDetailDto.getTaxkbn())) {
                    log.debug("セット商品が税設定あり、構成商品が非課税または不課税 … 消費税項目に0円を設定");
                    // 消費税項目には0を設定
                    bundleDetailDto.setAcptuntshohitaxkin(ZERO);
                    bundleDetailDto.setAcptshohitaxkin(ZERO);
                }
            }
        }
    }

    /**
     * ショップマスタを取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @return ショップマスタEntity
     */
    private S02M046TEntity getShopMaster(String apkihoncd, String shopcd) {
        S02M046TEntityKey key = new S02M046TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        return s02m046tDao.selectByPrimaryKey(key);
    }

    /**
     * 更新用の受注配達テーブルEntityを生成する
     * 
     * @param source 元となる受注配達テーブルEntity
     * @param updateDto 更新用DTO
     * @param context コンテキスト
     * @return 更新内容を反映した新しい受注配達テーブルEntity
     */
    private S04T001TEntity createEntityForUpdate(S04T001TEntity source, C0809W31UpdateDto updateDto,
        C0801W02ProcessingContextDto context) {
        // MybatisのDaoは同じクエリが同じパラメータで実行された場合にキャッシュされたEntityを返すため
        // 別のインスタンスを生成し、内容をコピーした上で更新内容を反映する
        S04T001TEntity updated = new S04T001TEntity();
        BeanUtils.copyProperties(source, updated);
        // 配達予定日
        updated.setDlvryytidd(updateDto.getDlvryytiddmei());
        // 配達区分
        updated.setDlvrykbn(updateDto.getDlvrykbnmei());
        // 配達希望時間区分
        updated.setDlvrykibojikankbn(updateDto.getDlvrykibojikantaimei());
        // 配達希望時間
        updated.setDlvrykibojikan(updateDto.getDlvryytijikokumei());
        // 配達希望時間（終了）
        updated.setDlvrykibojikanshry(updateDto.getDlvryytijikokumei2());
        // 配達会社コード
        updated.setDlvrycmpnycd(updateDto.getDlvrycmpnycdmei());
        // 配達担当者コード
        updated.setDlvrytnshacd(updateDto.getDlvrytnshacdmei());
        // 配達順
        updated.setDlvryjun(updateDto.getDlvryjun());
        // 更新タイムスタンプ
        updated.setKoshaapkihoncd(context.getApkihoncd());
        updated.setKoshashopcd(context.getShopcd());
        updated.setKoshacd(context.getTnshacd());
        updated.setKshnsystmymd(context.getSystemDate());
        updated.setKshnsystmjikan(context.getSystemTime());
        updated.setKshnunyoymd(context.getOperationDate());
        updated.setKshnipadrse(context.getIpAddress());
        updated.setKshnpgmid(context.getProgramId());
        return updated;
    }

    /**
     * 配達予定日の変更による単価再設定および再計算の内容を反映した受注配達テーブルEntityを生成する
     * 
     * @param juchuDto 受注配達DTO（変更後の受注データ）
     * @param juchuEntity 受注配達テーブルEntity（変更前の受注データ）
     * @param updateDto 更新用DTO
     * @param tokuisakiDto 得意先マスタDTO
     * @param context コンテキスト
     * @return 受注配達テーブルEntity（変更内容反映済）
     */
    private S04T001TEntity createEntityForUpdate(C0801W02AcptDlvryDto juchuDto,
        S04T001TEntity juchuEntity, C0809W31UpdateDto updateDto,
        C0801W02TokuisakiMasterDto tokuisakiDto, C0801W02ProcessingContextDto context) {
        // 配達予定日の変更有無に関係なく編集する項目の設定
        S04T001TEntity updatedJuchuEntity = createEntityForUpdate(juchuEntity, updateDto, context);
        // 配達日
        updatedJuchuEntity.setDlvrydd(updatedJuchuEntity.getDlvryytidd());
        // 受注締処理日
        updatedJuchuEntity.setAcptshimeprcsdd(CommonConstants.DEF_DATE_00000000);
        // 受注締担当者AP基本コード
        updatedJuchuEntity.setAcptshimetnshaapkihoncd("");
        // 受注締担当者ショップコード
        updatedJuchuEntity.setAcptshimetnshashopcd("");
        // 受注締担当者コード
        updatedJuchuEntity.setAcptshimeprcstnshacd("");
        // 前回配達日
        updatedJuchuEntity.setZnkdlvrydd("");
        // 前回配達本数
        updatedJuchuEntity.setZnkdlvryhns((short) 0);
        // 次回配達予定日
        updatedJuchuEntity.setJikaidlvryytidd("");
        // 前回締日
        updatedJuchuEntity.setZnkshimedd("");
        // 前回繰越金額
        updatedJuchuEntity.setZnkkurikoshikin(null);
        // 配達ステータス
        updatedJuchuEntity.setDlvrysts(CommonConstants.KBN_DLVRYSTS_NONE);
        // 伝票ステータス
        updatedJuchuEntity.setDnpysts(updatedJuchuEntity.getAcptsts());
        // 帳票出力関連項目
        // 納品書印刷対象フラグ
        updatedJuchuEntity.setNoshoprnttrgtflg(CommonConstants.FLG_ON);
        // 納品書控印刷対象フラグ
        updatedJuchuEntity.setNoshohikaeprnttrgtflg(CommonConstants.FLG_ON);
        // 入金票印刷対象フラグ
        S02M017TEntityKey skysakiKey = new S02M017TEntityKey();
        skysakiKey.setApkihoncd(juchuDto.getApkihoncd());
        skysakiKey.setShopcd(juchuDto.getShopcd());
        skysakiKey.setSkysakicd(tokuisakiDto.getSkysakicd());
        S02M017TEntity skysakiEntity = s02m017tDao.selectByPrimaryKey(skysakiKey);
        String kaishuhohokbn =
            Objects.isNull(skysakiEntity) ? "" : skysakiEntity.getKaishuhohokbn();
        updatedJuchuEntity.setNyknhyoprnttrgtflg(
            // 単体伝票締めフラグがONまたは回収方法区分が代引
            CommonConstants.FLG_ON.equals(updatedJuchuEntity.getSingldnpyflg())
                || CommonConstants.KBN_KAISHUHOHO_DAIBIKI.equals(kaishuhohokbn)
                    // 条件を満たした場合はONを設定する
                    ? CommonConstants.FLG_ON
                    : CommonConstants.FLG_OFF);
        // サーバ指示書印刷対象フラグ
        updatedJuchuEntity.setWsvsijishoprnttrgtflg(
            juchuDto.isShohinAcpt() ? CommonConstants.FLG_OFF : CommonConstants.FLG_ON);

        // 伝票ステータス：配達中 の場合、印刷関連項目をリセットする
        if (CommonConstants.DNPYSTS_11.equals(juchuEntity.getDnpysts())) {
            updatedJuchuEntity.setNoshoprntsumiflg(CommonConstants.FLG_OFF); // 納品書印刷済フラグ
            updatedJuchuEntity.setNoshoprntdd(""); // 納品書印刷日
            updatedJuchuEntity.setNoshohikaeprntsumiflg(CommonConstants.FLG_OFF); // 納品書控印刷済フラグ
            updatedJuchuEntity.setNoshohikaeprntdd(""); // 納品書控印刷日
            updatedJuchuEntity.setNyknhyoprntsumiflg(CommonConstants.FLG_OFF); // 入金票印刷済フラグ
            updatedJuchuEntity.setNyknhyoprntdd(""); // 入金票印刷日
            updatedJuchuEntity.setWsvsijishoprntsumiflg(CommonConstants.FLG_OFF); // サーバー指示書印刷済フラグ
            updatedJuchuEntity.setWsvsijishoprntdd(""); // サーバー指示書印刷日
        }

        // 金額系を設定
        C0801W02AcptDlvrySummaryDto summary = juchuDto.getSummary();
        // 配達予定レギュラー有償本数
        updatedJuchuEntity.setDlvryytirgrayusyohns(summary.getRycnt());
        // 配達予定レギュラー無償本数
        updatedJuchuEntity.setDlvryytirgramushohns(summary.getRmcnt());
        // 配達予定スリム有償本数
        updatedJuchuEntity.setDlvryytisrmyusyohns(summary.getSycnt());
        // 配達予定スリム無償本数
        updatedJuchuEntity.setDlvryytisrmmushohns(summary.getSmcnt());
        // 配達予定その他有償本数
        updatedJuchuEntity.setDlvryytisonotayusyohns(summary.getOycnt());
        // 配達予定その他無償本数
        updatedJuchuEntity.setDlvryytisonotamushohns(summary.getOmcnt());
        if (juchuDto.isKeihiTokuisaki()) {
            // 配達予定製品水売上金額
            updatedJuchuEntity.setDlvryytiseihinmizuurikin(0L);
            // 配達予定その他売上金額
            updatedJuchuEntity.setDlvryytisonotaurikin(0L);
        } else {
            // 配達予定製品水売上金額
            updatedJuchuEntity.setDlvryytiseihinmizuurikin(summary.getWmn());
            // 配達予定その他売上金額
            updatedJuchuEntity.setDlvryytisonotaurikin(summary.getOmn());
        }
        // 受注一般税抜金額
        updatedJuchuEntity
            .setAcptippannukikin(juchuDto.getLongValue(juchuDto.getAcptippannukikin()));
        // 受注一般消費税金額
        updatedJuchuEntity.setAcptippantaxkin(juchuDto.getLongValue(juchuDto.getAcptippantaxkin()));
        // 受注軽減税抜金額
        updatedJuchuEntity
            .setAcptkeigennukikin(juchuDto.getLongValue(juchuDto.getAcptkeigennukikin()));
        // 受注軽減消費税金額
        updatedJuchuEntity
            .setAcptkeigentaxkin(juchuDto.getLongValue(juchuDto.getAcptkeigentaxkin()));
        // 受注非課税金額
        updatedJuchuEntity
            .setAcpthikazeiuriikin(juchuDto.getLongValue(juchuDto.getAcpthikazeiuriikin()));
        // 受注不課税売上金額
        updatedJuchuEntity
            .setAcptfukazeiuriikin(juchuDto.getLongValue(juchuDto.getAcptfukazeiuriikin()));
        return updatedJuchuEntity;
    }

    /**
     * 受注配達テーブルの更新条件（PK＋更新タイムスタンプ）を生成する
     * 
     * @param primaryKey 更新対象のPK
     * @param updateDto 更新用DTO
     * @return 更新条件
     */
    private S04T001TEntityExample createConditionForUpdate(S04T001TEntity primaryKey,
        C0809W31UpdateDto updateDto) {
        S04T001TEntityExample condition = new S04T001TEntityExample();
        condition.createCriteria()
            // AP基本コード
            .andApkihoncdEqualTo(primaryKey.getApkihoncd())
            // ショップコード
            .andShopcdEqualTo(primaryKey.getShopcd())
            // 受注伝票番号
            .andAcptdnpynoEqualTo(primaryKey.getAcptdnpyno())
            // 枝番号
            .andEdanoEqualTo(primaryKey.getEdano())
            // 更新システム日付
            .andKshnsystmymdEqualTo(updateDto.getKshnsystmymd())
            // 更新システム時間
            .andKshnsystmjikanEqualTo(updateDto.getKshnsystmjikan());
        return condition;
    }

    /**
     * 得意先マスタから得意先情報を取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @return 得意先情報
     */
    private S02M013TEntity getTokuisakiMaster(String apkihoncd, String shopcd, String tokuisakicd) {
        S02M013TEntityKey key = new S02M013TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setTokuisakicd(tokuisakicd);
        return s02m013tDao.selectByPrimaryKey(key);
    }

    /**
     * 受注配達テーブルから受注伝票を取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param acptdnpyno 受注伝票番号
     * @param edano 枝番号
     * @param lock ロックする場合はtrueを指定する
     * @return 受注配達テーブルEntity
     */
    private S04T001TEntity getJuchuEntity(String apkihoncd, String shopcd, String acptdnpyno,
        String edano, boolean lock) {
        S04T001TEntityKey key = new S04T001TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setAcptdnpyno(acptdnpyno);
        key.setEdano(Short.valueOf(edano));
        return lock ? s04t001tDao.selectByPrimaryKeyForLock(key)
            : s04t001tDao.selectByPrimaryKey(key);
    }

    /**
     * 指定された受注伝票に紐づく受注配達明細のリストを取得する
     * 
     * @param juchuEntity 受注配達テーブルEntity
     * @return 受注配達明細テーブルEntityリスト
     */
    private List<S04T002TEntity> getJuchuDetailEntities(S04T001TEntity juchuEntity) {
        S04T002TEntityExample condition = new S04T002TEntityExample();
        condition.createCriteria()
            // AP基本コード
            .andApkihoncdEqualTo(juchuEntity.getApkihoncd())
            // ショップコード
            .andShopcdEqualTo(juchuEntity.getShopcd())
            // 受注伝票番号
            .andAcptdnpynoEqualTo(juchuEntity.getAcptdnpyno())
            // 枝番号
            .andEdanoEqualTo(juchuEntity.getEdano())
            // 有効区分が有効
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        return s04t002tDao.selectByExample(condition);
    }

    /**
     * 受注伝票の内容をデバッグログに出力する
     * 
     * @param juchuEntity 受注配達テーブルEntity
     * @param juchuDto 受注配達DTO
     */
    private void debugLogJuchuDnpy(S04T001TEntity juchuEntity, C0801W02AcptDlvryDto juchuDto) {
        log.debug("\t受注金額={}, 受注消費税金額={}", juchuDto.getAcptkin(), juchuDto.getAcptshohitaxkin());
        log.debug("\t受注一般税抜金額={}", juchuDto.getAcptippannukikin());
        log.debug("\t受注一般消費税金額={}", juchuDto.getAcptippantaxkin());
        log.debug("\t受注軽減税抜金額={}", juchuDto.getAcptkeigennukikin());
        log.debug("\t受注軽減消費税金額={}", juchuDto.getAcptkeigentaxkin());
        log.debug("\t受注非課税金額={}", juchuDto.getAcpthikazeiuriikin());
        log.debug("\t受注不課税売上金額={}", juchuDto.getAcptfukazeiuriikin());
        log.debug("\t配達予定製品水売上 : {}", juchuEntity.getDlvryytiseihinmizuurikin());
        log.debug("\t配達予定その他売上 : {}", juchuEntity.getDlvryytisonotaurikin());
    }

    /**
     * 受注配達明細の内容をデバッグログに出力する
     * 
     * @param detailDto 受注配達明細DTO
     */
    private void debugLogDetail(C0801W02AcptDlvryDetailDto detailDto) {
        log.debug("\t受注伝票番号 : {}-{}", detailDto.getAcptdnpyno(), detailDto.getEdano());
        log.debug("\t受注伝票明細番号 : {}-{}", detailDto.getAcptdnpymeisaino(),
            detailDto.getAcptdnpymeisaiedano());
        log.debug("\tタイプ : {}", detailDto.getType());
        log.debug("\t商品コード : {}(明細区分 : {} / 入力明細フラグ : {}/ 単品明細フラグ : {})", detailDto.getShncd(),
            detailDto.getMeisaikbn(), detailDto.getNyuryokumeisaiflg(),
            detailDto.getTanpinmeisaiflg());
        log.debug("\t配布物コード : {} / セット商品コード : {}", detailDto.getHaifubutsucd(),
            detailDto.getStshncd());
        log.debug("\t計上区分 : {} / 税区分 : {}", detailDto.getKeijokbn(), detailDto.getTaxkbn());
        log.debug("\t標準単価 : {}", detailDto.getHyojununt());
        log.debug("\t受注単価 : {}", detailDto.getAcptunt());
        log.debug("\t受注単価消費税金額 : {}", detailDto.getAcptuntshohitaxkin());
        log.debug("\t受注数量 : {}", detailDto.getAcptsuryo());
        log.debug("\t受注金額 : {}", detailDto.getAcptkin());
        log.debug("\t受注消費税金額 : {}", detailDto.getAcptshohitaxkin());
    }

}
