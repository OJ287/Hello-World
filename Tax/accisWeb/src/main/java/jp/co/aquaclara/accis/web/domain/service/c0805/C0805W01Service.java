package jp.co.aquaclara.accis.web.domain.service.c0805;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.constants.SystemConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.C0804B01Dao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M006TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M007TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M008TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M013TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M017TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M024TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M033TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M043TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M044TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M046TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M047TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M050TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M055TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M066TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02V001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T002TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T004TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T005TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T006TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T007TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T008TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S13T004TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.C0804B01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M006TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M006TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M007TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M007TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M024TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M033TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M033TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M043TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M050TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M050TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M050TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M055TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T004TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T004TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T005TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T005TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T006TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T006TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T007TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T007TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T008TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T008TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T008TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S13T004TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S13T004TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.MultiRglrAcptMstEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.RglrAcptMstEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M008TC0805W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M013TC0805W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M033TC0805W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M044TC0805W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M047TC0805W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M066TC0805W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S04T001TC0805W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S04T002TC0805W01Entity;
import jp.co.aquaclara.accis.common.util.business.NumberUtils;
import jp.co.aquaclara.accis.web.domain.dto.c0800.ComparableValueCurrentOrNew;
import jp.co.aquaclara.accis.web.domain.dto.c0800.HasConvertableNumericProperty;
import jp.co.aquaclara.accis.web.domain.dto.c0800.RegistrationInfoDto;
import jp.co.aquaclara.accis.web.domain.dto.c0805.C0805W01AcptRelationConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.c0805.C0805W01KeyDto;
import jp.co.aquaclara.accis.web.domain.dto.c0805.C0805W01ProcessConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.c0805.C0805W01SearchConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.c0805.C0805W01SearchResultDto;
import jp.co.aquaclara.accis.web.domain.dto.c0805.C0805W01TeikiAcptResultDto;
import jp.co.aquaclara.accis.web.domain.dto.c0805.C0805W01TokuisakiDataConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.c0805.C0805W01TokuisakiRelationDto;
import jp.co.aquaclara.accis.web.domain.dto.c0805.C0805W02KeyConditionDto;
import jp.co.aquaclara.accis.web.domain.service.business.SaibanService;
import jp.co.aquaclara.accis.web.domain.service.business.TaxService;
import jp.co.aquaclara.accis.web.domain.service.c0800.JuchuYykKshnService;
import jp.co.aquaclara.accis.web.domain.service.c0800.UtilityService;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * C0805w02_定期受注データ作成(指示)Service
 */
@Service
@Slf4j
public class C0805W01Service implements ComparableValueCurrentOrNew, HasConvertableNumericProperty {

    private static final String JIGYOSHOKANRI_NASHI_NM = "（事業所管理なし）";
    private static final long KYBTLHNS_MAX = 9999L;

    // 受注明細から商品入力行のみを取得するためのフィルタ
    Predicate<S04T002TC0805W01Entity> inputShnFilter =
        detail -> CommonConstants.FLG_ON.equals(detail.getNyuryokumeisaiflg()) // 入力明細フラグがON
            && CommonConstants.KBN_MEISAI_SHOHIN.equals(detail.getMeisaikbn()); // 明細区分が商品

    /** メッセージ **/
    @Autowired
    private MessageSource msg;

    /** 受注予約更新サービス **/
    @Autowired
    private JuchuYykKshnService juchuYykKshnService;

    /** システム管理マスタDAO **/
    @Autowired
    private S02M006TDao s02m006tDao;

    /** 基本商品マスタDAO **/
    @Autowired
    private S02M007TDao s02m007tDao;

    /** 展開マスタDAO **/
    @Autowired
    private S02M008TDao s02m008tDao;

    /** 請求先マスタDAO **/
    @Autowired
    private S02M017TDao s02m017tDao;

    /** 得意先マスタDAO **/
    @Autowired
    private S02M013TDao s02m013tDao;

    /** 定期受注マスタDAO **/
    @Autowired
    private S02M024TDao s02m024tDao;

    /** 配布物企画対象得意先マスタDAO **/
    @Autowired
    private S02M033TDao s02m033tDao;

    /** 事業所マスタDAO **/
    @Autowired
    private S02M047TDao s02m047tDao;

    /** 得意先実績テーブル **/
    @Autowired
    private S02M050TDao s02m050tDao;

    /** 特定定期受注マスタDAO **/
    @Autowired
    private S02M055TDao s02m055tDao;

    /** お気に入り商品マスタDAO **/
    @Autowired
    private S02M044TDao s02m044tDao;

    /** ショップ商品マスタDAO **/
    @Autowired
    private S02M043TDao s02m043tDao;

    /** ショップマスタDAO **/
    @Autowired
    private S02M046TDao s02m046tDao;

    /** 得意先別セット商品単価マスタDAO **/
    @Autowired
    private S02M066TDao s02m066tDao;

    /** 商品マスタビューDAO **/
    @Autowired
    private S02V001TDao s02v001tDao;

    /** 受注配達テーブルDAO **/
    @Autowired
    private S04T001TDao s04t001tDao;

    /** 受注配達明細テーブルDAO **/
    @Autowired
    private S04T002TDao s04t002tDao;

    /** 定期受注データ作成処理結果テーブルDAO **/
    @Autowired
    private S04T004TDao s04t004tDao;

    /** 配達拠点受注アラームテーブルDAO **/
    @Autowired
    private S04T005TDao s04t005tDao;

    /** 担当者受注アラームテーブルDAO **/
    @Autowired
    private S04T006TDao s04t006tDao;

    /** 配達エリア受注アラームテーブルDAO **/
    @Autowired
    private S04T007TDao s04t007tDao;

    /** 定期受注データ作成処理管理テーブルDAO **/
    @Autowired
    private S04T008TDao s04t008tDao;

    /** 在庫引当テーブルDAO **/
    @Autowired
    private S13T004TDao s13t004tDao;

    /** 商品明細消費税・金額計算Service **/
    @Autowired
    private TaxService taxService;

    /** 本部受注データ連携処 **/
    @Autowired
    private C0804B01Dao c0804b01Dao;

    /** ユーティリティサービス */
    @Autowired
    private UtilityService util;

    /** 採番サービス */
    @Autowired
    private SaibanService numberingService;

    /**
     * 初期表示メイン処理
     * 
     * @param conditionDto 検索条件
     * @return C0805W01SearchResultDto
     */
    public C0805W01SearchResultDto search(C0805W01SearchConditionDto conditionDto) {

        log.debug("[開始] 初期表示メイン処理");
        log.debug("\t検索条件DTO : {}", conditionDto);
        // ショップマスタ検索：単独TBL検索
        S02M046TEntityKey shopKey = new S02M046TEntityKey();
        shopKey.setApkihoncd(conditionDto.getApkihoncd());
        shopKey.setShopcd(conditionDto.getShopcd());
        // PKで検索し有効な場合に事業所管理の値を取得（無効な場合はエラーとする）
        String jigyoshokanri = Optional.ofNullable(s02m046tDao.selectByPrimaryKey(shopKey))
            .filter(shop -> CommonConstants.YUKOKBN_1.equals(shop.getYukokbn()))
            .map(S02M046TEntity::getJigyoshokanri).orElseThrow(() -> {
                return BusinessExceptionFactory.dataNotFound(MessageID.W084, "ショップマスタ");
            });

        // 前回定期受注データ作成処理結果リスト取得
        S02M047TC0805W01Entity s02m047tIn = new S02M047TC0805W01Entity();
        s02m047tIn.setApkihoncd(conditionDto.getApkihoncd());
        s02m047tIn.setShopcd(conditionDto.getShopcd());
        s02m047tIn.setJigyoshocd(conditionDto.getJigyoshocd());
        if (!util.isFlagged(jigyoshokanri)) {
            s02m047tIn.setJigyoshocd(CommonConstants.JIGYOSHOCD_00);
            s02m047tIn.setJigyoshonm(JIGYOSHOKANRI_NASHI_NM);
        }
        s02m047tIn.setJigyoshokanri(jigyoshokanri);
        List<S02M047TC0805W01Entity> s02m047tOutList =
            s02m047tDao.selectByC0805W01getZnkdlvryytidd(s02m047tIn);

        // 相関チェックNo2.
        String saveZnkdlvryytidd = "";
        boolean checkNo2Flg = false;

        for (S02M047TC0805W01Entity e : s02m047tOutList) {
            if (Strings.isEmpty(saveZnkdlvryytidd)) {
                saveZnkdlvryytidd = e.getZnkdlvryytidd();
            }
            if (!Strings.isEmpty(saveZnkdlvryytidd)
                && !saveZnkdlvryytidd.equals(e.getZnkdlvryytidd())) {
                checkNo2Flg = true;
            }
        }
        String message = "";
        if (checkNo2Flg) {
            message = (msg.getMessage(MessageID.W145.toString(), null, Locale.JAPAN));
        }

        // 返却値セット
        LocalDate operationDate = util.getLocalDate(conditionDto.getUnyodate());
        C0805W01SearchResultDto resultDto = new C0805W01SearchResultDto();
        resultDto.setDlvryytiddFrom(util.getDateString(operationDate.plusDays(1L)));
        resultDto.setDlvryytiddTo(util.getDateString(operationDate.plusMonths(1L)));
        resultDto.setJigyoshokanri(jigyoshokanri);
        resultDto.setDlvryytidd(util.getDateString(operationDate.plusDays(1L)));
        resultDto.setMsg(message);
        resultDto.setS02m047tOutList(s02m047tOutList);
        log.debug("\t検索結果DTO : {}", resultDto);
        log.debug("[終了] 初期表示メイン処理");
        return resultDto;

    }

    /**
     * 定期受注処理
     * 
     * @param conditionDto 実行条件
     * @return List<String>
     */
    @Transactional
    public List<String> regularAcptProcess(C0805W01ProcessConditionDto conditionDto) {

        log.debug("[開始] 定期受注処理");
        log.debug("\t作成対象期間（終了日は含まない） : {} ～ {}", conditionDto.getCreateStr(),
            conditionDto.getCreateEnd());

        // ショップマスタ検索：単独TBL検索
        S02M046TEntityExample shopCondition = new S02M046TEntityExample();
        shopCondition.createCriteria().andApkihoncdEqualTo(conditionDto.getApkihoncd())
            .andShopcdEqualTo(conditionDto.getShopcd())
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        S02M046TEntity shopEntity =
            s02m046tDao.selectByExample(shopCondition).stream().findFirst().get();

        // 定期受注データ作成処理管理テーブルロック処理
        List<S04T008TEntity> ctrlEntityList = getRglrAcptCtrlTblWithLock(conditionDto);

        // 定期受注マスタ対象の得意先リスト取得処理
        List<S02M013TC0805W01Entity> targetTokuisakiEntityList =
            getTargetTokuisakiList(conditionDto);

        // 得意先データリストへのセット
        List<C0805W01TokuisakiDataConditionDto> tokuisakiDataDtoList =
            targetTokuisakiEntityList.stream()
                // 対象得意先Entityを得意先データDTOに変換
                .map(targetTokuisakiEntityy -> {
                    C0805W01TokuisakiDataConditionDto tokuisakiDataDto =
                        new C0805W01TokuisakiDataConditionDto();
                    tokuisakiDataDto.setTargetTokuisaki(targetTokuisakiEntityy);
                    return tokuisakiDataDto;
                }).collect(util.toList());

        int totalDnpyCount = 0; // 作成する受注データ総数
        for (C0805W01TokuisakiDataConditionDto tokuisakiDataDto : tokuisakiDataDtoList) {
            log.debug("\t処理対象得意先 : {}-{}-{}", tokuisakiDataDto.getTargetTokuisaki().getApkihoncd(),
                tokuisakiDataDto.getTargetTokuisaki().getShopcd(),
                tokuisakiDataDto.getTargetTokuisaki().getTokuisakicd());
            // 得意先関係DTOを取得
            C0805W01TokuisakiRelationDto tRelation =
                getTokuisakiRelation(tokuisakiDataDto.getTargetTokuisaki());
            tokuisakiDataDto.setRelation(tRelation);
            // 請求先マスタ取得
            S02M017TEntityKey skysakiKey = new S02M017TEntityKey();
            skysakiKey.setApkihoncd(tokuisakiDataDto.getTargetTokuisaki().getApkihoncd());
            skysakiKey.setShopcd(tokuisakiDataDto.getTargetTokuisaki().getShopcd());
            skysakiKey.setSkysakicd(tRelation.getTokuisaki().getSkysakicd());
            S02M017TEntity skysakiEntity = s02m017tDao.selectByPrimaryKey(skysakiKey);
            // 委託フラグの初期化
            tokuisakiDataDto.setLinkable(false);
            // 定期受注データ作成処理
            int rglrAcptdnpyCount =
                createRegularAcptData(conditionDto, tokuisakiDataDto, shopEntity, skysakiEntity);
            log.debug("\t定期受注データ作成受注数 : {}件", rglrAcptdnpyCount);
            // 定期受注データ作成による受注データ数を加算
            totalDnpyCount += rglrAcptdnpyCount;
            // 定期受注データ作成数が0の場合、定期受注データ作成（マルチ）
            if (rglrAcptdnpyCount == 0) {
                // 定期受注データ作成（マルチ）による受注データ数を加算
                int rglracptdnpyMultiCount = createRegularAcptMultiData(conditionDto,
                    tokuisakiDataDto, shopEntity, skysakiEntity);
                log.debug("\t定期受注データ作成（マルチ）受注数 : {}件", rglracptdnpyMultiCount);
                totalDnpyCount += rglracptdnpyMultiCount;
            }
        }
        log.debug("\tトータル受注数 : {}件", totalDnpyCount);

        // 受注データ総数が0より大きい場合は受注データを登録
        if (totalDnpyCount > 0) {
            // 伝票番号採番
            String[] newSlipNoList = numberingService.getSlipNoJuchuBulk(
                conditionDto.getApkihoncd(), conditionDto.getShopcd(), totalDnpyCount);
            // 受注伝票番号開始・終了セット
            conditionDto.setAcptdnpynostr(newSlipNoList[0]);
            if (newSlipNoList.length == 1) {
                conditionDto.setAcptdnpynoend(newSlipNoList[0]);
            } else {
                conditionDto.setAcptdnpynoend(newSlipNoList[newSlipNoList.length - 1]);
            }
            log.debug("\t受注伝票番号（開始） : {}", conditionDto.getAcptdnpynostr());
            log.debug("\t受注伝票番号（終了） : {}", conditionDto.getAcptdnpynoend());
            // 受注伝票・明細更新処理
            log.debug("\t[開始] 定期受注伝票登録");
            updateAcptTable(conditionDto, tokuisakiDataDtoList, shopEntity);
            log.debug("\t[終了] 定期受注伝票登録");
        }

        // 定期受注作成管理テーブル更新
        log.debug("[開始] 定期受注作成管理テーブル更新");
        ctrlEntityList.forEach(entity -> updateRglrAcptCtrlTbl(conditionDto, entity));
        log.debug("[終了] 定期受注作成管理テーブル更新");

        // 定期受注作成結果テーブル更新
        updateRegularAcptResultTable(conditionDto);

        log.debug("[終了] 定期受注処理");
        return List.of();
    }

    /**
     * 定期受注データ作成処理管理テーブルロック処理
     * 
     * @param conditionDto 実行条件
     * @return 定期受注データ作成処理管理テーブルEntityリスト
     */
    private List<S04T008TEntity> getRglrAcptCtrlTblWithLock(
        C0805W01ProcessConditionDto conditionDto) {
        log.debug("[START] 定期受注データ作成処理管理テーブルロック処理");
        log.debug("\tAP基本コード = {}", conditionDto.getApkihoncd());
        log.debug("\tショップコード = {}", conditionDto.getShopcd());
        log.debug("\t事業所コード = {}", conditionDto.getJigyoshocd());
        log.debug("\t事業所管理 = {}", conditionDto.getJigyoshokanri());
        log.debug("\t運用日付 = {}", conditionDto.getUnyodate());
        // 処理対象となる事業所を事業所管理マスタから取得する（事業所管理しない場合は「00」）
        S02M047TC0805W01Entity criteria = new S02M047TC0805W01Entity();
        criteria.setApkihoncd(conditionDto.getApkihoncd());
        criteria.setShopcd(conditionDto.getShopcd());
        criteria.setJigyoshocd(conditionDto.getJigyoshocd());
        if (!util.isFlagged(conditionDto.getJigyoshokanri())) {
            criteria.setJigyoshocd(CommonConstants.JIGYOSHOCD_00);
            criteria.setJigyoshonm(JIGYOSHOKANRI_NASHI_NM);
        }
        criteria.setJigyoshokanri(conditionDto.getJigyoshokanri());
        List<S02M047TC0805W01Entity> jigyoshoList =
            s02m047tDao.selectByC0805W01lockTeikiKanri(criteria);
        // 定期受注データ作成処理管理テーブルにデータがない場合、新規レコードを登録
        jigyoshoList.stream()
            // クエリ結果の定期受注データ事業所コードが空の場合が該当
            .filter(entity -> StringUtils.isEmpty(entity.getTsktjigyoshocd()))
            // 新規登録用のEntityに変換
            .map(source -> {
                S04T008TEntity entity = new S04T008TEntity();
                entity.setApkihoncd(source.getApkihoncd()); // AP基本コード
                entity.setShopcd(source.getShopcd()); // ショップコード
                entity.setJigyoshocd(source.getJigyoshocd()); // 事業所コード
                entity.setYukokbn(CommonConstants.YUKOKBN_1); // 有効区分
                entity.setToshaapkihoncd(conditionDto.getLoginUserApkihoncd()); // 登録者ＡＰ基本コード
                entity.setToshashopcd(conditionDto.getLoginUserShopcd()); // 登録者ショップコード
                entity.setToshacd(conditionDto.getLoginUserTnshacd()); // 登録者コード
                entity.setInptsystmymd(conditionDto.getSystemDate()); // 登録システム日付
                entity.setInptsystmjikan(conditionDto.getSystemTime()); // 登録システム時間
                entity.setInptunyoymd(conditionDto.getUnyodate()); // 登録運用日付
                entity.setInptipadrse(conditionDto.getIpAddress()); // 登録ＩＰアドレス
                entity.setInptpgmid(conditionDto.getProgramId()); // 登録プログラムＩＤ
                entity.setKoshaapkihoncd(conditionDto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
                entity.setKoshashopcd(conditionDto.getLoginUserShopcd()); // 更新者ショップコード
                entity.setKoshacd(conditionDto.getLoginUserTnshacd()); // 更新者コード
                entity.setKshnsystmymd(conditionDto.getSystemDate()); // 更新システム日付
                entity.setKshnsystmjikan(conditionDto.getSystemTime()); // 更新システム時間
                entity.setKshnunyoymd(conditionDto.getUnyodate()); // 更新運用日付
                entity.setKshnipadrse(conditionDto.getIpAddress()); // 更新ＩＰアドレス
                entity.setKshnpgmid(conditionDto.getProgramId()); // 更新プログラムＩＤ
                return entity;
            })
            // 新規レコード登録
            .forEach(entity -> s04t008tDao.insert(entity));
        // 定期受注データ作成処理管理テーブルの行ロックを取得する
        List<S04T008TEntity> result = jigyoshoList.stream().map(jigyoshoEntity -> {
            S04T008TEntityKey key = new S04T008TEntityKey();
            key.setApkihoncd(jigyoshoEntity.getApkihoncd());
            key.setShopcd(jigyoshoEntity.getShopcd());
            key.setJigyoshocd(jigyoshoEntity.getJigyoshocd());
            return s04t008tDao.selectByPrimaryKeyForLock(key);
        }).collect(util.toList());
        log.debug("ロック取得件数 : {}件", result.size());
        log.debug("[END] 定期受注データ作成処理管理テーブルロック処理");
        return result;
    }

    /**
     * 定期受注データ作成処理管理テーブル更新
     * 
     * @param conditionDto 実行条件
     * @param entity 更新対象Entity
     */
    private void updateRglrAcptCtrlTbl(C0805W01ProcessConditionDto conditionDto,
        S04T008TEntity entity) {
        // タイムスタンプを退避
        String kshnsystmymd = entity.getKshnsystmymd();
        String kshnsystmjikan = entity.getKshnsystmjikan();
        // 更新内容をEntityに設定
        entity.setZnkdlvryytidd(conditionDto.getDlvryytidd()); // 前回配達予定日
        entity.setPrcstnshacd(conditionDto.getPrcstnshacd()); // 処理担当者コード
        entity.setKoshaapkihoncd(conditionDto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        entity.setKoshashopcd(conditionDto.getLoginUserShopcd()); // 更新者ショップコード
        entity.setKoshacd(conditionDto.getLoginUserTnshacd()); // 更新者コード
        entity.setKshnsystmymd(conditionDto.getSystemDate()); // 更新システム日付
        entity.setKshnsystmjikan(conditionDto.getSystemTime()); // 更新システム時間
        entity.setKshnunyoymd(conditionDto.getUnyodate()); // 更新運用日付
        entity.setKshnipadrse(conditionDto.getIpAddress()); // 更新ＩＰアドレス
        entity.setKshnpgmid(conditionDto.getProgramId()); // 更新プログラムＩＤ

        // 更新条件（プライマリキーとロック取得時のタイムスタンプを条件に指定）
        S04T008TEntityExample updateCondition = new S04T008TEntityExample();
        updateCondition.createCriteria()
            // AP基本コード
            .andApkihoncdEqualTo(entity.getApkihoncd())
            // ショップコード
            .andShopcdEqualTo(entity.getShopcd())
            // 事業所コード
            .andJigyoshocdEqualTo(entity.getJigyoshocd())
            // 更新システム日付
            .andKshnsystmymdEqualTo(kshnsystmymd)
            // 更新システム時刻
            .andKshnsystmjikanEqualTo(kshnsystmjikan);

        // 更新結果が0件の場合はエラーとする
        if (s04t008tDao.updateByExample(entity, updateCondition) == 0) {
            throw BusinessExceptionFactory.validationFailed(MessageID.W090);
        }
    }

    /**
     * 定期受注マスタ対象の得意先リスト取得処理
     * 
     * @param conditionDto 実行条件
     * @return List<S02M013TC0805W01Entity>
     */
    private List<S02M013TC0805W01Entity> getTargetTokuisakiList(
        C0805W01ProcessConditionDto conditionDto) {
        log.debug("[開始] 定期受注マスタ対象の得意先リスト取得");
        // 定期受注マスタ対象得意先リスト取得
        S02M013TC0805W01Entity condition = new S02M013TC0805W01Entity();
        condition.setApkihoncd(conditionDto.getApkihoncd());
        condition.setShopcd(conditionDto.getShopcd());
        if (!Strings.isEmpty(conditionDto.getJigyoshocd())) {
            condition.setJigyoshocd(conditionDto.getJigyoshocd());
        }
        condition.setUnyodate(conditionDto.getUnyodate());
        log.debug("[終了] 定期受注マスタ対象の得意先リスト取得");
        return s02m013tDao.selectByC0805W01getTeikiTokuisakiList(condition);
    }

    /**
     * 定期受注データ作成処理
     * 
     * @param conditionDto 実行条件
     * @param tokuisakiData 得意先データDTO
     * @param shopEntity ショップマスタEntity
     * @param skysakiEntity 請求先マスタEntity
     * @return 作成する受注データ数
     */
    private int createRegularAcptData(C0805W01ProcessConditionDto conditionDto,
        C0805W01TokuisakiDataConditionDto tokuisakiData, S02M046TEntity shopEntity,
        S02M017TEntity skysakiEntity) {

        log.debug("\t[開始] 定期受注データ作成処理");
        S02M013TC0805W01Entity regularAcptTarget = tokuisakiData.getTargetTokuisaki();

        // 得意先関係DTOを取得
        C0805W01TokuisakiRelationDto tRelation = tokuisakiData.getRelation();
        S02M013TEntity tokuisakiMstEntity = tRelation.getTokuisaki();

        // 作成対象開始日（運用日の翌日から）
        String createStr = conditionDto.getCreateStr();
        // 作成対象終了日の取得（配達予定日の翌日まで）
        String createEnd = conditionDto.getCreateEnd();

        // 定期受注マスタ検索（有効なものを次回予定日の昇順に取得）
        S02M024TEntityExample rglrAcptMstCondition = new S02M024TEntityExample();
        rglrAcptMstCondition.createCriteria()
            // AP基本コード
            .andApkihoncdEqualTo(regularAcptTarget.getApkihoncd())
            // ショップコード
            .andShopcdEqualTo(regularAcptTarget.getShopcd())
            // 得意先コード
            .andTokuisakicdEqualTo(regularAcptTarget.getTokuisakicd())
            // 有効区分
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        rglrAcptMstCondition.setOrderByClause("JIKAIYTIDD ASC");
        List<RglrAcptMstEntity> rglrAcptMstEntityList =
            s02m024tDao.selectByExample(rglrAcptMstCondition).stream()
                .map(RglrAcptMstEntity::create).collect(util.toList());

        // 受注予約伝票を取得
        // 配達予定日は配達エリアマスタの結合条件だが、配達パターン等は定期受注データ作成に影響ない
        // よって、定期受注マスタ単位に取得する必要はなく、得意先単位で取得すればよい
        S04T001TC0805W01Entity reservedAcptdnpyEntity = getReservedAcptdnpyEntity(
            regularAcptTarget.getApkihoncd(), regularAcptTarget.getShopcd(),
            regularAcptTarget.getTokuisakicd(), conditionDto.getDlvryytidd());

        // 作成する受注に対して受注予約の内容を反映する回数
        int reservedDataSetToAcptdnpyCount = 1;
        // 作成対象終了超過フラグの初期化（リストが空の場合は超過しているとみなして処理
        boolean overflg = rglrAcptMstEntityList.size() == 0;
        // 初回日
        String firstDay = "";
        // 最終配達予定日
        String lastDlvryytidd = "";
        // 定期受注結果リスト
        List<C0805W01TeikiAcptResultDto> teikiAcptResultDtoList = new ArrayList<>();
        // 伝票データリスト
        List<S02M013TC0805W01Entity> tokuisakiRglrAcptdnpyEntityList = new ArrayList<>();
        // 超過フラグが立つまで繰り返し
        while (!overflg) {
            // 定期受注データ作成
            for (RglrAcptMstEntity rglrAcptMstEntity : rglrAcptMstEntityList) {
                // 作成対象終了判定（次回予定日が作成対象終了日以降の場合、ループ終了）
                if (util.isLater(rglrAcptMstEntity.getJikaiytidd(), createEnd)) {
                    overflg = true;
                    break;
                }

                // 受注停止フラグチェック
                if (util.isFlagged(tokuisakiMstEntity.getAcptteishiflg())) {
                    regularAcptTarget.setDlvryytidd(rglrAcptMstEntity.getJikaiytidd());
                    teikiAcptResultDtoList
                        .add(C0805W01TeikiAcptResultDto.errorAcptStopped(regularAcptTarget, msg));
                }

                // 次回予定日の設定
                // 受注停止または、運用日付を超えるまでは更新対象とせず、SKIPする
                if (util.isFlagged(tokuisakiMstEntity.getAcptteishiflg())
                    || util.isAfter(createStr, rglrAcptMstEntity.getJikaiytidd())) {
                    // ※定期受注の場合、サイクルは28日固定（4週後が次回になる）
                    // ※受注データは作成しないが定期受注マスタは更新するため
                    rglrAcptMstEntity.setJikaiytidd(getCycledJikaiytidd(rglrAcptMstEntity));
                    continue;
                } else {
                    // 受注停止ではなく初回日が次回予定日よりも後の場合
                    if (Strings.isEmpty(firstDay)
                        || util.isAfter(firstDay, rglrAcptMstEntity.getJikaiytidd())) {
                        // 初回日が空または初回日が次回予定日より後の場合、初回日に次回予定日を設定
                        firstDay = rglrAcptMstEntity.getJikaiytidd();
                    }
                }

                // 得意先受注データ（得意先マスタの内容に受注伝票の内容を加えたデータ）作成
                S02M013TC0805W01Entity tokuisakiRglrAcptdnpyEntity =
                    createTokuisakiRglrAcptdnpyEntity(conditionDto, rglrAcptMstEntity, shopEntity,
                        skysakiEntity);

                // 受注予約明細取得（受注予約伝票に紐づく明細のうち、残回数が残っているものを対象にする）
                List<S04T002TC0805W01Entity> acptDetailEntityList = new LinkedList<>();
                if (Objects.nonNull(reservedAcptdnpyEntity)) {
                    // 受注予約データには配達予定日が設定されていないので次回予定日を設定
                    reservedAcptdnpyEntity
                        .setDlvryytidd(tokuisakiRglrAcptdnpyEntity.getDlvryytidd());
                    // 受注明細データ取得
                    acptDetailEntityList = getReservedDetailEntityList(reservedAcptdnpyEntity,
                        reservedDataSetToAcptdnpyCount);
                    // キャンセル用受注予約データの設定
                    // ※定期受注マスタから作成する受注データ全てに設定するが実際に処理するのは1回のみ
                    tokuisakiRglrAcptdnpyEntity
                        .setCancelReservedAcptdnpyEntity(reservedAcptdnpyEntity);
                    reservedDataSetToAcptdnpyCount++;
                }

                // 配布物取込処理（初回のみ）
                List<S02M033TC0805W01Entity> haifubutsuList =
                    firstDay.equals(rglrAcptMstEntity.getJikaiytidd())
                        ? getHaifubutsuList(tokuisakiRglrAcptdnpyEntity)
                        : List.of();

                // 定期受注明細マスタの内容を元に受注明細を生成
                S02M044TC0805W01Entity rglrAcptDetailMstCondition = new S02M044TC0805W01Entity();
                rglrAcptDetailMstCondition.setApkihoncd(tokuisakiRglrAcptdnpyEntity.getApkihoncd());
                rglrAcptDetailMstCondition.setShopcd(tokuisakiRglrAcptdnpyEntity.getShopcd());
                rglrAcptDetailMstCondition
                    .setTokuisakicd(tokuisakiRglrAcptdnpyEntity.getTokuisakicd());
                rglrAcptDetailMstCondition.setAcptdd(tokuisakiRglrAcptdnpyEntity.getAcptdd());
                rglrAcptDetailMstCondition
                    .setDlvryytidd(tokuisakiRglrAcptdnpyEntity.getDlvryytidd());
                // タイプを0にすると定期受注明細マスタ、1にすると特定定期受注明細マスタから取得する
                rglrAcptDetailMstCondition.setType(S02M044TC0805W01Entity.TYPE_NORMAL);
                // 下記条件設定は定期受注明細マスタから取得する場合独自の設定
                rglrAcptDetailMstCondition.setDlvryshuu(tokuisakiRglrAcptdnpyEntity.getDlvryshuu());
                rglrAcptDetailMstCondition
                    .setShiteiyobi(tokuisakiRglrAcptdnpyEntity.getShiteiyobi());
                List<S02M044TC0805W01Entity> rglrAcptDetailMstEntityList =
                    s02m044tDao.selectByC0805W01(rglrAcptDetailMstCondition);

                // 委託元得意先の場合
                if (tokuisakiData.isTakyoten() && isItakumotoTokuisaki(tokuisakiMstEntity)) {
                    // 本部受注データ連携チェック
                    Optional<C0805W01TeikiAcptResultDto> resultDto =
                        checkItakusaki(tokuisakiMstEntity, rglrAcptDetailMstEntityList,
                            tokuisakiRglrAcptdnpyEntity);
                    if (resultDto.isPresent()) {
                        // エラーがある場合、スキップして次のサイクルも処理しない
                        overflg = true;
                        rglrAcptMstEntity.setError(true);
                        teikiAcptResultDtoList.add(resultDto.get());
                        log.debug("\t\t\tチェックエラーありのためスキップ");
                        log.debug("\t\t\tエラー内容 : {}", resultDto.get().getMsg());
                        continue;
                    } else {
                        // エラー無しの場合、本部受注データ連携可能とする
                        tokuisakiData.setLinkable(true);
                    }
                }

                // 明細設定
                if (!Objects.isNull(rglrAcptDetailMstEntityList)) {
                    // 受注明細データEntityの追加
                    changeEntity(acptDetailEntityList, rglrAcptDetailMstEntityList,
                        tokuisakiRglrAcptdnpyEntity, shopEntity);
                }

                // 受注明細リストのセット商品を展開
                List<S04T002TC0805W01Entity> retList = deploymentSetShohin(
                    tokuisakiRglrAcptdnpyEntity, acptDetailEntityList, shopEntity, haifubutsuList);
                tokuisakiRglrAcptdnpyEntity.setDetailEntityList(retList);

                // 本部受注データ連携する場合、受注配達明細の委託相手受注伝票明細番号とその枝番号を設定する
                if (tokuisakiData.isLinkable()) {
                    tokuisakiRglrAcptdnpyEntity.getDetailEntityList().forEach(detail -> {
                        detail.setItakuaiteacptdnpymeisaino(detail.getAcptdnpymeisaino());
                        detail.setItakuaiteacptdnpymeisaiedano(detail.getAcptdnpymeisaiedano());
                    });
                }

                // 与信限度チェック
                if (checkCreditlimit(tokuisakiData, tRelation, tokuisakiRglrAcptdnpyEntity,
                    shopEntity)) {
                    teikiAcptResultDtoList.add(C0805W01TeikiAcptResultDto
                        .errorCreditLimit(tokuisakiRglrAcptdnpyEntity, msg));
                }
                // 得意先データ追加
                log.debug("\t\t\t受注データ作成対象として追加");
                log.debug("\t\t\t\t配達WEEK : {} 指定曜日 : {} 次回予定日 : {}",
                    rglrAcptMstEntity.getDlvryshuu(), rglrAcptMstEntity.getShiteiyobi(),
                    rglrAcptMstEntity.getJikaiytidd());
                tokuisakiRglrAcptdnpyEntityList.add(tokuisakiRglrAcptdnpyEntity);
                // 最終配達予定日
                if (StringUtils.isEmpty(lastDlvryytidd)
                    || util.isLater(rglrAcptMstEntity.getJikaiytidd(), lastDlvryytidd)) {
                    lastDlvryytidd = rglrAcptMstEntity.getJikaiytidd();
                }
                // 次回予定日加算
                rglrAcptMstEntity.setJikaiytidd(getCycledJikaiytidd(rglrAcptMstEntity));

            }

        }

        // 定期伝票データEntityリスト
        tokuisakiData.setRglrAcptMstList(rglrAcptMstEntityList);

        // 伝票データEntityのセット
        tokuisakiData.setAcptdnpyList(tokuisakiRglrAcptdnpyEntityList);

        // 定期受注結果のセット
        conditionDto.setTeikiAcptResultList(
            util.mergeList(conditionDto.getTeikiAcptResultList(), teikiAcptResultDtoList));

        // 最終定期配達予定日セット
        if (!StringUtils.isEmpty(lastDlvryytidd)) {
            tokuisakiData.setLastdlvryytidd(lastDlvryytidd);
        }

        // 得意先関係Entityをセット
        tokuisakiData.setRelation(tRelation);

        log.debug("\t[終了] 定期受注データ作成処理");
        return tokuisakiRglrAcptdnpyEntityList.size();

    }

    /**
     * 定期受注データ作成（マルチ）処理
     * 
     * @param conditionDto 実行条件
     * @param tokuisakiData 得意先データDTO
     * @param shopEntity ショップマスタEntity
     * @param skysakiEntity 請求先マスタEntity
     * @return 作成する受注データ数
     */
    private int createRegularAcptMultiData(C0805W01ProcessConditionDto conditionDto,
        C0805W01TokuisakiDataConditionDto tokuisakiData, S02M046TEntity shopEntity,
        S02M017TEntity skysakiEntity) {

        log.debug("\t[開始] 定期受注データ作成（マルチ）処理");
        S02M013TC0805W01Entity regularAcptTarget = tokuisakiData.getTargetTokuisaki();

        // 得意先関係DTOを取得
        C0805W01TokuisakiRelationDto tRelation = tokuisakiData.getRelation();
        S02M013TEntity tokuisakiMstEntity = tRelation.getTokuisaki();

        // 作成対象開始日（運用日の翌日から）
        String createStr = conditionDto.getCreateStr();
        // 作成対象終了日の取得（配達予定日の翌日まで）
        String createEnd = conditionDto.getCreateEnd();

        // 特定定期受注伝票リスト取得：単独TBL検索
        S02M055TEntityExample multiRglrAcptMstCondition = new S02M055TEntityExample();
        multiRglrAcptMstCondition.createCriteria()
            // AP基本コード
            .andApkihoncdEqualTo(regularAcptTarget.getApkihoncd())
            // ショップコード
            .andShopcdEqualTo(regularAcptTarget.getShopcd())
            // 得意先コード
            .andTokuisakicdEqualTo(regularAcptTarget.getTokuisakicd())
            // 有効区分
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        // 次回予定日の昇順にソート
        multiRglrAcptMstCondition.setOrderByClause("JIKAIYTIDD ASC");
        List<MultiRglrAcptMstEntity> multiRglrAcptMstEntityList =
            s02m055tDao.selectByExample(multiRglrAcptMstCondition).stream()
                .map(MultiRglrAcptMstEntity::create).collect(util.toList());

        // 受注予約伝票を取得
        // 配達予定日は配達エリアマスタの結合条件だが、配達パターン等は定期受注データ作成に影響ない
        // よって、定期受注マスタ単位に取得する必要はなく、得意先単位で取得すればよい
        S04T001TC0805W01Entity reservedAcptdnpyEntity = getReservedAcptdnpyEntity(
            regularAcptTarget.getApkihoncd(), regularAcptTarget.getShopcd(),
            regularAcptTarget.getTokuisakicd(), conditionDto.getDlvryytidd());

        // 作成する受注に対して受注予約の内容を反映する回数
        int reservedDataSetToAcptdnpyCount = 1;
        // 作成対象終了超過フラグの初期化
        boolean overflg = multiRglrAcptMstEntityList.stream()
            // 次回予定日を取得
            .map(MultiRglrAcptMstEntity::getJikaiytidd)
            // 1件目を判定対象とする（次回予定日の昇順のリストのため）
            .findFirst()
            // 次回予定日が作成対象終了日を超えているか判定
            .map(jikaiytidd -> util.isAfter(jikaiytidd, createEnd))
            // リストが加等の場合も作成対象終了日を超えているとみなす
            .orElse(Boolean.TRUE);

        if (!multiRglrAcptMstEntityList.isEmpty()) {
            // 特定定期受注マスタEntityリストから最大次回予定日を取得（指定区分：範囲指定用の処理）
            final String maxDlvryytidd = multiRglrAcptMstEntityList.stream()
                // StreamをListの要素数-1分スキップし要素を取得
                .skip(multiRglrAcptMstEntityList.size() - 1).findFirst()
                // 次回予定日を取得（リストが空の場合は空文字）
                .map(MultiRglrAcptMstEntity::getJikaiytidd).orElse(SystemConstants.WORD_BLANK);
            // 各特定定期受注マスタエンティティに最大次回予定日と連番（1から開始）を設定
            multiRglrAcptMstEntityList.stream().forEach(util.withIndexConsumer(1, (e, i) -> {
                e.setMaxJikaiytidd(maxDlvryytidd);
                e.setSeq(i);
                e.setSeqWork(i);
            }));
        }

        // 初回日
        String firstDay = "";
        // 最終配達予定日
        String lastDlvryytidd = "";
        // 定期受注結果リスト
        List<C0805W01TeikiAcptResultDto> teikiAcptResultDtoList = new ArrayList<>();
        // 伝票データリスト
        List<S02M013TC0805W01Entity> tokuisakiRglrAcptdnpyEntityList = new ArrayList<>();

        // 超過フラグが立つまで繰り返し
        while (!overflg) {

            // 定期受注データ作成
            for (MultiRglrAcptMstEntity multiRglrAcptMstEntity : multiRglrAcptMstEntityList) {
                // 作成対象終了判定
                boolean allJikaiytiddIsLater = multiRglrAcptMstEntityList.stream()
                    .allMatch(e -> util.isLater(e.getJikaiytidd(), createEnd));
                if (allJikaiytiddIsLater) {
                    // 特定定期受注マスタEntityリスト内の要素全ての
                    // 次回予定日が作成対象終了日よりも後の場合はループ終了
                    overflg = true;
                    break;
                } else if (util.isLater(multiRglrAcptMstEntity.getJikaiytidd(), createEnd)) {
                    // 処理中の特定定期受注マスタの次回予定日が作成対象終了日よりあとの場合はスキップ
                    continue;
                }

                // 受注停止フラグチェック
                if (util.isFlagged(tokuisakiMstEntity.getAcptteishiflg())) {
                    regularAcptTarget.setDlvryytidd(multiRglrAcptMstEntity.getJikaiytidd());
                    teikiAcptResultDtoList
                        .add(C0805W01TeikiAcptResultDto.errorAcptStopped(regularAcptTarget, msg));
                }

                // 次回予定日の設定
                // 受注停止または、運用日付を超えるまでは更新対象とせず、SKIPする
                if (util.isFlagged(tokuisakiMstEntity.getAcptteishiflg())
                    || util.isAfter(createStr, multiRglrAcptMstEntity.getJikaiytidd())) {
                    // ※特定定期受注の場合、サイクルは指定区分ごとに異なる
                    // ※受注データは作成しないが定期受注マスタは更新するため
                    multiRglrAcptMstEntity
                        .setJikaiytidd(getCycledJikaiytidd(multiRglrAcptMstEntity));
                    continue;
                } else {
                    // 受注停止ではなく初回日が次回予定日よりも後の場合
                    if (Strings.isEmpty(firstDay)
                        || util.isAfter(firstDay, multiRglrAcptMstEntity.getJikaiytidd())) {
                        // 初回日が空または初回日が次回予定日より後の場合、初回日に次回予定日を設定
                        firstDay = multiRglrAcptMstEntity.getJikaiytidd();
                    }
                }

                // 得意先受注データ（得意先マスタの内容に受注伝票の内容を加えたデータ）作成
                S02M013TC0805W01Entity tokuisakiRglrAcptdnpyEntity =
                    createTokuisakiRglrAcptdnpyEntity(conditionDto, multiRglrAcptMstEntity,
                        shopEntity, skysakiEntity);

                // 受注予約明細取得（受注予約伝票に紐づく明細のうち、残回数が残っているものを対象にする）
                List<S04T002TC0805W01Entity> acptDetailEntityList = new LinkedList<>();
                if (Objects.nonNull(reservedAcptdnpyEntity)) {
                    // 受注予約データには配達予定日が設定されていないので次回予定日を設定
                    reservedAcptdnpyEntity
                        .setDlvryytidd(tokuisakiRglrAcptdnpyEntity.getDlvryytidd());
                    // 受注明細データ取得
                    acptDetailEntityList = getReservedDetailEntityList(reservedAcptdnpyEntity,
                        reservedDataSetToAcptdnpyCount);
                    // キャンセル用受注予約データの設定
                    // ※定期受注マスタから作成する受注データ全てに設定するが実際に処理するのは1回のみ
                    tokuisakiRglrAcptdnpyEntity
                        .setCancelReservedAcptdnpyEntity(reservedAcptdnpyEntity);
                    reservedDataSetToAcptdnpyCount++;
                }

                // 配布物取込処理（初回のみ）
                List<S02M033TC0805W01Entity> haifubutsuList =
                    firstDay.equals(multiRglrAcptMstEntity.getJikaiytidd())
                        ? getHaifubutsuList(tokuisakiRglrAcptdnpyEntity)
                        : List.of();

                // 特定定期受注明細マスタの内容を元に受注明細を生成
                S02M044TC0805W01Entity multiRglrAcptDetailMstCondition =
                    new S02M044TC0805W01Entity();
                multiRglrAcptDetailMstCondition
                    .setApkihoncd(tokuisakiRglrAcptdnpyEntity.getApkihoncd());
                multiRglrAcptDetailMstCondition.setShopcd(tokuisakiRglrAcptdnpyEntity.getShopcd());
                multiRglrAcptDetailMstCondition
                    .setTokuisakicd(tokuisakiRglrAcptdnpyEntity.getTokuisakicd());
                multiRglrAcptDetailMstCondition.setAcptdd(tokuisakiRglrAcptdnpyEntity.getAcptdd());
                multiRglrAcptDetailMstCondition
                    .setDlvryytidd(tokuisakiRglrAcptdnpyEntity.getDlvryytidd());
                // タイプを0にすると定期受注明細マスタ、1にすると特定定期受注明細マスタから取得する
                multiRglrAcptDetailMstCondition.setType(S02M044TC0805W01Entity.TYPE_MULTI);
                // 下記条件設定は特定定期受注明細マスタから取得する場合独自の設定
                multiRglrAcptDetailMstCondition
                    .setShiteiyouso(tokuisakiRglrAcptdnpyEntity.getShiteiyouso());
                multiRglrAcptDetailMstCondition
                    .setShiteikbn(tokuisakiRglrAcptdnpyEntity.getShiteikbn());
                List<S02M044TC0805W01Entity> multiRglrAcptDetailMstEntityList =
                    s02m044tDao.selectByC0805W01(multiRglrAcptDetailMstCondition);

                // 委託元得意先の場合
                if (tokuisakiData.isTakyoten() && isItakumotoTokuisaki(tokuisakiMstEntity)) {
                    // 本部受注データ連携チェック
                    Optional<C0805W01TeikiAcptResultDto> resultDto =
                        checkItakusaki(tokuisakiMstEntity, multiRglrAcptDetailMstEntityList,
                            tokuisakiRglrAcptdnpyEntity);
                    if (resultDto.isPresent()) {
                        // エラーがある場合、スキップして次のサイクルも処理しない
                        overflg = true;
                        multiRglrAcptMstEntity.setError(true);
                        teikiAcptResultDtoList.add(resultDto.get());
                        log.debug("\t\t\tチェックエラーありのためスキップ");
                        log.debug("\t\t\tエラー内容 : {}", resultDto.get().getMsg());
                        continue;
                    } else {
                        // エラー無しの場合、本部受注データ連携可能とする
                        tokuisakiData.setLinkable(true);
                    }
                }

                // 明細設定
                if (!Objects.isNull(multiRglrAcptDetailMstEntityList)) {
                    // 受注明細データEntityの追加
                    changeEntity(acptDetailEntityList, multiRglrAcptDetailMstEntityList,
                        tokuisakiRglrAcptdnpyEntity, shopEntity);
                }

                // 受注明細リストのセット商品を展開
                List<S04T002TC0805W01Entity> retList = deploymentSetShohin(
                    tokuisakiRglrAcptdnpyEntity, acptDetailEntityList, shopEntity, haifubutsuList);
                tokuisakiRglrAcptdnpyEntity.setDetailEntityList(retList);

                // 本部受注データ連携する場合、受注配達明細の委託相手受注伝票明細番号とその枝番号を設定する
                if (tokuisakiData.isLinkable()) {
                    tokuisakiRglrAcptdnpyEntity.getDetailEntityList().forEach(detail -> {
                        detail.setItakuaiteacptdnpymeisaino(detail.getAcptdnpymeisaino());
                        detail.setItakuaiteacptdnpymeisaiedano(detail.getAcptdnpymeisaiedano());
                    });
                }

                // 与信限度チェック
                if (checkCreditlimit(tokuisakiData, tRelation, tokuisakiRglrAcptdnpyEntity,
                    shopEntity)) {
                    teikiAcptResultDtoList.add(C0805W01TeikiAcptResultDto
                        .errorCreditLimit(tokuisakiRglrAcptdnpyEntity, msg));
                }

                // 得意先データ追加
                log.debug("\t\t\t受注データ作成対象として追加");
                log.debug("\t\t\t\t指定区分 : {} 指定要素 : {} 次回予定日 : {}",
                    multiRglrAcptMstEntity.getShiteikbn(), multiRglrAcptMstEntity.getShiteiyouso(),
                    multiRglrAcptMstEntity.getJikaiytidd());
                tokuisakiRglrAcptdnpyEntityList.add(tokuisakiRglrAcptdnpyEntity);

                // 最終配達予定日を更新（受注データを作成する場合のみ更新する）
                if (StringUtils.isEmpty(lastDlvryytidd)
                    || util.isLater(multiRglrAcptMstEntity.getJikaiytidd(), lastDlvryytidd)) {
                    lastDlvryytidd = multiRglrAcptMstEntity.getJikaiytidd();
                }

                // 次回予定日のサイクル算出
                multiRglrAcptMstEntity.setJikaiytidd(getCycledJikaiytidd(multiRglrAcptMstEntity));
            }

            if (multiRglrAcptMstEntityList.size() > 0) {
                // 最大次回予定日と連番設定
                setMaxJikaiytidd(multiRglrAcptMstEntityList);
                // 次回予定日でソート
                multiRglrAcptMstEntityList
                    .sort(Comparator.comparing(MultiRglrAcptMstEntity::getJikaiytidd));
            }

        }

        // 特定定期伝票データEntityリスト
        tokuisakiData.setRglrAcptMultiMstList(multiRglrAcptMstEntityList);

        // 伝票データEntityの追加
        tokuisakiData.setAcptdnpyList(
            util.mergeList(tokuisakiData.getAcptdnpyList(), tokuisakiRglrAcptdnpyEntityList));

        // 定期受注結果のセット
        conditionDto.setTeikiAcptResultList(
            util.mergeList(conditionDto.getTeikiAcptResultList(), teikiAcptResultDtoList));

        // 最終定期配達予定日セット（受注データ作成対象のうち最大の配達予定日を設定する）
        if (!StringUtils.isEmpty(lastDlvryytidd)) {
            tokuisakiData.setLastdlvryytidd(lastDlvryytidd);
        }

        // 得意先関係DTOを設定
        tokuisakiData.setRelation(tRelation);

        log.debug("\t[終了] 定期受注データ作成（マルチ）処理");
        return tokuisakiRglrAcptdnpyEntityList.size();

    }

    /**
     * 特定定期受注マスタEntityリストに最大次回予定日を設定し、連番を再設定する
     * 
     * @param list 特定定期受注マスタEntityリスト
     */
    private void setMaxJikaiytidd(List<MultiRglrAcptMstEntity> list) {
        // ワーク（次回予定日と連番を設定したもの）リストを生成
        List<MultiRglrAcptMstEntity> workList = list.stream().map(e -> {
            MultiRglrAcptMstEntity work = new MultiRglrAcptMstEntity();
            work.setJikaiytidd(e.getJikaiytidd());
            work.setSeq(e.getSeq());
            return work;
        }).collect(util.toList());
        // ワークリストを次回予定日の昇順にソート
        workList.sort(Comparator.comparing(MultiRglrAcptMstEntity::getJikaiytidd));
        // ワークリストにワーク連番を設定
        workList.stream().forEach(util.withIndexConsumer(1, (e, i) -> e.setSeqWork(i)));
        // ワークリストから最大の次回予定日（最後尾の要素の次回予定日）を取得
        String maxJikaiytidd =
            workList.stream().skip(workList.size() - 1).findFirst().get().getJikaiytidd();
        // ワークリストから最大次回予定日とワーク連番を元のリストに設定
        list.stream().forEach(e -> {
            e.setMaxJikaiytidd(maxJikaiytidd);
            // ワークリストから連番が同じ要素を取得しワーク連番を反映する
            e.setSeqWork(workList.stream().filter(w -> e.getSeq() == w.getSeq()).findFirst().get()
                .getSeqWork());
        });
        // 元のリストのワーク連番を連番に反映する
        list.stream().forEach(e -> e.setSeq(e.getSeqWork()));
    }

    /**
     * 受注伝票情報作成（定期受注）
     * 
     * @param conditionDto 実行条件
     * @param rglrAcptMstEntity 定期受注マスタEntity
     * @param shopEntity ショップマスタEntity
     * @param skysakiEntity 請求先Entity
     * @return 得意先受注伝票Entity
     */
    private S02M013TC0805W01Entity createTokuisakiRglrAcptdnpyEntity(
        C0805W01ProcessConditionDto conditionDto, RglrAcptMstEntity rglrAcptMstEntity,
        S02M046TEntity shopEntity, S02M017TEntity skysakiEntity) {

        // 得意先配達情報取得
        S02M013TC0805W01Entity tokuisakiDlvryCondition = new S02M013TC0805W01Entity();
        tokuisakiDlvryCondition.setApkihoncd(rglrAcptMstEntity.getApkihoncd());
        tokuisakiDlvryCondition.setShopcd(rglrAcptMstEntity.getShopcd());
        tokuisakiDlvryCondition.setTokuisakicd(rglrAcptMstEntity.getTokuisakicd());
        tokuisakiDlvryCondition.setUnyodate(rglrAcptMstEntity.getJikaiytidd());

        S02M013TC0805W01Entity tokuisakiDlvryEntity =
            s02m013tDao.selectByC0805W01getTokuisakiDlvryInfo(tokuisakiDlvryCondition);

        tokuisakiDlvryEntity.setApkihoncd(rglrAcptMstEntity.getApkihoncd());
        tokuisakiDlvryEntity.setShopcd(rglrAcptMstEntity.getShopcd());
        tokuisakiDlvryEntity.setEdano((short) 0);
        tokuisakiDlvryEntity.setTokuisakicd(rglrAcptMstEntity.getTokuisakicd());
        tokuisakiDlvryEntity.setDlvryytidd(rglrAcptMstEntity.getJikaiytidd());
        tokuisakiDlvryEntity.setDlvryshuu(rglrAcptMstEntity.getDlvryshuu());
        tokuisakiDlvryEntity.setShiteiyobi(rglrAcptMstEntity.getShiteiyobi());
        tokuisakiDlvryEntity.setDlvrytrgtflg(CommonConstants.FLG_ON);
        tokuisakiDlvryEntity.setAcptkbn(CommonConstants.KBN_ACPT_SHOHIN);
        tokuisakiDlvryEntity.setAcptkbnshs(CommonConstants.KBN_ACPTSYS_NONE);
        tokuisakiDlvryEntity.setAcptdd(conditionDto.getUnyodate());
        tokuisakiDlvryEntity.setAcpttnshaapkihoncd(conditionDto.getAcpttnshaapkihoncd());
        tokuisakiDlvryEntity.setAcpttnshashopcd(conditionDto.getAcpttnshashopcd());
        tokuisakiDlvryEntity.setAcpttnshacd(conditionDto.getAcpttnshacd());
        tokuisakiDlvryEntity.setDnpysts(CommonConstants.DNPYSTS_02);
        tokuisakiDlvryEntity.setDatsksmotokbn(CommonConstants.KBN_SAKUSEIMOTO_TEIKIJUCHU);
        tokuisakiDlvryEntity.setZaikokanrikbn(shopEntity.getZaikokanrikbn());
        // 請求先マスタ.回収方法区分が代引の場合のみ受注時代引フラグをONにする
        tokuisakiDlvryEntity.setAcptthdaibikiflg(
            CommonConstants.KAISHUHOHOKBN_7.equals(skysakiEntity.getKaishuhohokbn())
                ? CommonConstants.FLG_ON
                : CommonConstants.FLG_OFF); // 受注時代引フラグ
        if (Strings.isEmpty(tokuisakiDlvryEntity.getAcptjikanshiteikbn())) {
            // 得意先マスタ.受注時間指定区分が未設定の場合は0：なしにする
            tokuisakiDlvryEntity.setAcptjikanshiteikbn("0");
        }
        // 税計算区分設定
        if (isOroshi(tokuisakiDlvryEntity)) {
            // 卸の場合は外税固定
            tokuisakiDlvryEntity.setTaxkeisankbn(CommonConstants.KBN_TAXKEISAN_EX);
        } else {
            // それ以外はショップマスタより運用日時点の税計算区分を設定
            tokuisakiDlvryEntity.setTaxkeisankbn(getCurrentValue(
                shopEntity.getGenztaxkeisankbnuri(), shopEntity.getShintaxkeisankbnuri(),
                shopEntity.getShohitaxkirikaedd(), conditionDto.getUnyodate()));
        }

        return tokuisakiDlvryEntity;
    }

    /**
     * 受注伝票情報作成（定期受注マルチ）
     * 
     * @param conditionDto 実行条件
     * @param rglrAcptMultiMstEntity 特定定期受注マスタEntity
     * @param shopEntity ショップマスタEntity
     * @param skysakiEntity 請求先Entity
     * @return 得意先受注伝票Entity
     */
    private S02M013TC0805W01Entity createTokuisakiRglrAcptdnpyEntity(
        C0805W01ProcessConditionDto conditionDto, MultiRglrAcptMstEntity rglrAcptMultiMstEntity,
        S02M046TEntity shopEntity, S02M017TEntity skysakiEntity) {

        // 得意先配達情報取得
        S02M013TC0805W01Entity tokuisakiDlvryCondition = new S02M013TC0805W01Entity();
        tokuisakiDlvryCondition.setApkihoncd(rglrAcptMultiMstEntity.getApkihoncd());
        tokuisakiDlvryCondition.setShopcd(rglrAcptMultiMstEntity.getShopcd());
        tokuisakiDlvryCondition.setTokuisakicd(rglrAcptMultiMstEntity.getTokuisakicd());
        tokuisakiDlvryCondition.setUnyodate(rglrAcptMultiMstEntity.getJikaiytidd());

        S02M013TC0805W01Entity tokuisakiDlvryEntity =
            s02m013tDao.selectByC0805W01getTokuisakiDlvryInfo(tokuisakiDlvryCondition);

        tokuisakiDlvryEntity.setApkihoncd(rglrAcptMultiMstEntity.getApkihoncd());
        tokuisakiDlvryEntity.setShopcd(rglrAcptMultiMstEntity.getShopcd());
        tokuisakiDlvryEntity.setEdano((short) 0);
        tokuisakiDlvryEntity.setTokuisakicd(rglrAcptMultiMstEntity.getTokuisakicd());
        tokuisakiDlvryEntity.setDlvryytidd(rglrAcptMultiMstEntity.getJikaiytidd());
        tokuisakiDlvryEntity.setShiteikbn(rglrAcptMultiMstEntity.getShiteikbn());
        tokuisakiDlvryEntity
            .setShiteiyouso(String.valueOf(rglrAcptMultiMstEntity.getShiteiyouso()));
        tokuisakiDlvryEntity.setDlvrytrgtflg(CommonConstants.FLG_ON);
        tokuisakiDlvryEntity.setAcptkbn(CommonConstants.KBN_ACPT_SHOHIN);
        tokuisakiDlvryEntity.setAcptkbnshs(CommonConstants.KBN_ACPTSYS_NONE);
        tokuisakiDlvryEntity.setAcptdd(conditionDto.getUnyodate());
        tokuisakiDlvryEntity.setAcpttnshaapkihoncd(conditionDto.getAcpttnshaapkihoncd());
        tokuisakiDlvryEntity.setAcpttnshashopcd(conditionDto.getAcpttnshashopcd());
        tokuisakiDlvryEntity.setAcpttnshacd(conditionDto.getAcpttnshacd());
        tokuisakiDlvryEntity.setDnpysts(CommonConstants.DNPYSTS_02);
        tokuisakiDlvryEntity.setDatsksmotokbn(CommonConstants.KBN_SAKUSEIMOTO_TEIKIJUCHU);
        tokuisakiDlvryEntity.setZaikokanrikbn(shopEntity.getZaikokanrikbn());
        // 請求先マスタ.回収方法区分が代引の場合のみ受注時代引フラグをONにする
        tokuisakiDlvryEntity.setAcptthdaibikiflg(
            CommonConstants.KAISHUHOHOKBN_7.equals(skysakiEntity.getKaishuhohokbn())
                ? CommonConstants.FLG_ON
                : CommonConstants.FLG_OFF); // 受注時代引フラグ
        if (Strings.isEmpty(tokuisakiDlvryEntity.getAcptjikanshiteikbn())) {
            tokuisakiDlvryEntity.setAcptjikanshiteikbn("0");
        }
        // 税計算区分設定
        if (isOroshi(tokuisakiDlvryEntity)) {
            // 卸の場合は外税固定
            tokuisakiDlvryEntity.setTaxkeisankbn(CommonConstants.KBN_TAXKEISAN_EX);
        } else {
            // それ以外はショップマスタより運用日時点の税計算区分を設定
            tokuisakiDlvryEntity.setTaxkeisankbn(getCurrentValue(
                shopEntity.getGenztaxkeisankbnuri(), shopEntity.getShintaxkeisankbnuri(),
                shopEntity.getShohitaxkirikaedd(), conditionDto.getUnyodate()));
        }

        return tokuisakiDlvryEntity;
    }

    /**
     * 受注伝票・明細更新処理
     * 
     * @param conditionDto 実行条件
     * @param tokuisakiDataDtoList 得意先データEntityリスト
     * @param shopMstEntity ショップマスタEntity
     */
    private void updateAcptTable(C0805W01ProcessConditionDto conditionDto,
        List<C0805W01TokuisakiDataConditionDto> tokuisakiDataDtoList,
        S02M046TEntity shopMstEntity) {

        log.debug("[開始] 受注伝票・明細更新処理");
        // 得意先データループ
        for (C0805W01TokuisakiDataConditionDto tokuisakiData : tokuisakiDataDtoList) {
            // 受注伝票単位処理
            // 受注伝票データループ
            List<C0805W01TeikiAcptResultDto> teikiAcptResultDtoList = new ArrayList<>();
            // 受注伝票データカウンター
            int headCounter = 0;
            for (S02M013TC0805W01Entity headEntity : tokuisakiData.getAcptdnpyList()) {
                headCounter++;
                // 受注伝票番号
                headEntity.setAcptdnpyno(conditionDto.getCurrentAcptdnpyno());
                // 得意先区分
                String tokuisakikbn = headEntity.getTokuisakikbn();
                // 得意先状態
                String tokuisakijotai = headEntity.getTokuisakijotai();
                // ショップマスタの納品書お預け容器表示区分がONの場合は、回収予定本数を設定
                if (util.isFlagged(shopMstEntity.getNoshoazyokikazudsplykbn())) {
                    // 得意先区分が顧客またはNA受託または配達受託の場合、デフォルト値設定
                    if (CommonConstants.KBN_TOKUISAKI_KOKUAKU.equals(tokuisakikbn)
                        || CommonConstants.KBN_TOKUISAKI_NAJUTAKU.equals(tokuisakikbn)
                        || CommonConstants.KBN_TOKUISAKI_DLVRYJUTAKU.equals(tokuisakikbn)) {
                        headEntity.setKaishyubtln1n2rittoruhns(getDefaultKybtlhns(
                            headEntity.getAzukaribtln1n2rittoruhns(), tokuisakijotai));
                        headEntity.setKaishyubtln8rittoruhns(getDefaultKybtlhns(
                            headEntity.getAzukaribtln8rittoruhns(), tokuisakijotai));
                    }
                } else {
                    // ショップマスタの納品書お預け容器表示区分がONの場合は、回収予定本数１２Ｌ、回収予定本数８Ｌに0を設定
                    headEntity.setKaishyubtln1n2rittoruhns("0");
                    headEntity.setKaishyubtln8rittoruhns("0");
                }
                // 受注受付経路区分（04：定期受注）
                headEntity.setAcptrcptkeirokbn("04");
                // 受注配達明細に受注伝票番号を設定
                headEntity.getDetailEntityList()
                    .forEach(e -> e.setAcptdnpyno(headEntity.getAcptdnpyno()));
                // 商品入力行について、受注配達明細の課税区分ごとの税抜金額および消費税金額を集計する
                List<S04T002TC0805W01Entity> inputShnDetailList = headEntity.getDetailEntityList()
                    .stream().filter(inputShnFilter).collect(util.toList());
                // 一般税抜金額
                long acptippannukikin = inputShnDetailList.stream()
                    .filter(
                        detail -> CommonConstants.KBN_TAX_KAZEI.equals(getDetailShntaxkbn(detail)))
                    .map(S04T002TC0805W01Entity::getAcptkin).mapToLong(k -> k).sum();
                // 軽減税抜金額
                long acptkeigennukikin = inputShnDetailList.stream()
                    .filter(
                        detail -> CommonConstants.KBN_TAX_KEIGEN.equals(getDetailShntaxkbn(detail)))
                    .map(S04T002TC0805W01Entity::getAcptkin).mapToLong(k -> k).sum();
                // 非課税
                long acpthikazeiuriikin = inputShnDetailList.stream()
                    .filter(detail -> CommonConstants.KBN_TAX_HIKAZEI
                        .equals(getDetailShntaxkbn(detail)))
                    .map(S04T002TC0805W01Entity::getAcptkin).mapToLong(k -> k).sum();
                // 不課税
                long acptfukazeiuriikin = inputShnDetailList.stream()
                    .filter(detail -> CommonConstants.KBN_TAX_HUKAZEI
                        .equals(getDetailShntaxkbn(detail)))
                    .map(S04T002TC0805W01Entity::getAcptkin).mapToLong(k -> k).sum();
                // 一般消費税
                String ippanPriceList[] = taxService.getPriceList(
                    // 税抜金額
                    String.valueOf(acptippannukikin),
                    // 数量（1で計算）
                    "1",
                    // 税区分
                    CommonConstants.KBN_TAX_KAZEI,
                    // 得意先区分
                    headEntity.getTokuisakikbn(),
                    // 計算機準備（配達予定日）
                    headEntity.getDlvryytidd(),
                    // ショップマスタ
                    shopMstEntity);
                long acptippantaxkin = Long.parseLong(ippanPriceList[1]);
                // 軽減消費税
                String keigenPriceList[] = taxService.getPriceList(
                    // 税抜金額
                    String.valueOf(acptkeigennukikin),
                    // 数量（1で計算）
                    "1",
                    // 税区分
                    CommonConstants.KBN_TAX_KEIGEN,
                    // 得意先区分
                    headEntity.getTokuisakikbn(),
                    // 計算機準備（配達予定日）
                    headEntity.getDlvryytidd(),
                    // ショップマスタ
                    shopMstEntity);
                long acptkeigentaxkin = Long.parseLong(keigenPriceList[1]);

                // 受注伝票の受注金額、受注消費税金額の算出（各集計金額を加算）
                long acptkin =
                    acptippannukikin + acptkeigennukikin + acpthikazeiuriikin + acptfukazeiuriikin;
                long acptshohitaxkin = acptippantaxkin + acptkeigentaxkin;
                // 代引以外かつ配達予定日がインボイス施行日前の場合
                if (!util.isFlagged(headEntity.getAcptthdaibikiflg()) && !util
                    .isLater(headEntity.getDlvryytidd(), shopMstEntity.getInvoicshikoymd())) {
                    // 受注伝票の受注金額、受注消費税金額には、商品入力行の受注金額、受注消費税金額のサマリを設定
                    acptkin = inputShnDetailList.stream().map(S04T002TC0805W01Entity::getAcptkin)
                        .mapToLong(k -> k).sum();
                    acptshohitaxkin = inputShnDetailList.stream()
                        .map(S04T002TC0805W01Entity::getAcptshohitaxkin).mapToLong(k -> k).sum();
                }

                // 受注伝票データEntityに税計算用内部変数値を設定する。
                headEntity.setAcptkin(acptkin);
                headEntity.setAcptshohitaxkin(acptshohitaxkin);
                headEntity.setAcptippannukikin(acptippannukikin);
                headEntity.setAcptippantaxkin(acptippantaxkin);
                headEntity.setAcptkeigennukikin(acptkeigennukikin);
                headEntity.setAcptkeigentaxkin(acptkeigentaxkin);
                headEntity.setAcpthikazeiuriikin(acpthikazeiuriikin);
                headEntity.setAcptfukazeiuriikin(acptfukazeiuriikin);

                // 受注伝票・明細更新処理
                // 受注伝票新規登録
                createAcptdnpy(conditionDto, headEntity);
                // 受注明細新規登録
                createAcptMeisai(conditionDto, headEntity);

                // 受注予約を取り込んだ場合は受注予約データの更新（得意先に対して登録する受注の最後に実行）
                if (!Objects.isNull(headEntity.getCancelReservedAcptdnpyEntity())
                    && headCounter == tokuisakiData.getAcptdnpyList().size()) {
                    updateReservedAcptData(conditionDto,
                        headEntity.getCancelReservedAcptdnpyEntity(),
                        tokuisakiData.getAcptdnpyList().size());
                }

                // 配布物取込処理
                createHaifubutsu(conditionDto, headEntity);

                // 伝票単位で正常終了メッセージをセットする
                teikiAcptResultDtoList.add(C0805W01TeikiAcptResultDto.success(headEntity, msg));
            } // 伝票のループ __ ここまで

            // 定期受注結果のセット
            conditionDto.setTeikiAcptResultList(
                util.mergeList(conditionDto.getTeikiAcptResultList(), teikiAcptResultDtoList));

            // 定期受注マスタ更新
            tokuisakiData.getRglrAcptMstList().stream()
                // エラーがないものだけ更新
                .filter(e -> !e.isError()).forEach(e -> {
                    updateTeikiJuchu(conditionDto, e);
                });
            // 定期受注マスタ(マルチ)更新
            if (Objects.nonNull(tokuisakiData.getRglrAcptMultiMstList())) {
                // 定期受注マスタ（マルチ）作成処理は
                // 定期受注マスタ作成で受注データを作成した場合呼ばれないため
                tokuisakiData.getRglrAcptMultiMstList().stream()
                    // エラーがないものだけ更新
                    .filter(e -> !e.isError()).forEach(e -> {
                        updateTokuteiTeikiJuchu(conditionDto, e);
                    });
            }

            // 得意先実績テーブル更新
            if (tokuisakiData.getAcptdnpyList().size() > 0) {
                // 得意先実績テーブル更新
                if (!Objects.isNull(tokuisakiData.getRelation().getJisseki())) {
                    log.debug("\t得意先実績テーブル更新");
                    if (updateTokuisakiJiseki(conditionDto, tokuisakiData) == 0) {
                        // 更新件数が0(排他制御)の場合、エラー
                        throw BusinessExceptionFactory.validationFailed(MessageID.W090);
                    }
                }
                // 得意先マスタ更新
                if (!Strings.isEmpty(tokuisakiData.getRelation().getTokuisaki().getKyuminkbn())) {
                    log.debug("\t得意先マスタ更新");
                    updateTokuisakiMaster(conditionDto, tokuisakiData);
                }

            }

            // 委託会社（本部受注データ連携しない）の場合、アラーム情報を更新しない。
            if (!tokuisakiData.isLinkable()) {
                // エラーがない場合のみ
                if (!tokuisakiData.hasError()) {
                    log.debug("在庫・アラーム処理 … 得意先コード : {}",
                        tokuisakiData.getTargetTokuisaki().getTokuisakicd());
                    // 「製品水」の商品大分類コード
                    String shnmctgrycd = getSystemMasterValue(CommonConstants.SYS_SETTEINO_096,
                        CommonConstants.SETTEICD_096_1);
                    // 「サーバー関連用」の商品大分類コード
                    String srvlctgrycd = getSystemMasterValue(CommonConstants.SYS_SETTEINO_096,
                        CommonConstants.SETTEICD_096_4);
                    // 「サーバー関連本体」の商品大分類コード
                    String srvmctgrycd = getSystemMasterValue(CommonConstants.SYS_SETTEINO_134,
                        CommonConstants.SETTEICD_134_41);
                    // 「レギュラー」の商品コードリスト
                    S02M007TEntityExample S02M007TExample = new S02M007TEntityExample();
                    S02M007TExample.createCriteria().andApkihoncdEqualTo(CommonConstants.APCD_001)
                        .andStkbnEqualTo(CommonConstants.STKBN_0)
                        .andLctgrycdEqualTo(CommonConstants.LCTGRYCD_01)
                        .andMctgrycdEqualTo(CommonConstants.MCTGRYCD_001)
                        .andSctgrycdEqualTo(CommonConstants.SCTGRYCD_001)
                        .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
                    List<S02M007TEntity> s02m007tRgOutList =
                        s02m007tDao.selectByExample(S02M007TExample);
                    List<String> rgralist = new ArrayList<>();
                    for (S02M007TEntity s02m007tEntity : s02m007tRgOutList) {
                        rgralist.add(s02m007tEntity.getShncd());
                    }

                    // 「スリム」の商品コードリスト
                    S02M007TExample = new S02M007TEntityExample();
                    S02M007TExample.createCriteria().andApkihoncdEqualTo(CommonConstants.APCD_001)
                        .andStkbnEqualTo(CommonConstants.STKBN_0)
                        .andLctgrycdEqualTo(CommonConstants.LCTGRYCD_01)
                        .andMctgrycdEqualTo(CommonConstants.MCTGRYCD_001)
                        .andSctgrycdEqualTo(CommonConstants.SCTGRYCD_002)
                        .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
                    List<S02M007TEntity> s02m007tSlOutList =
                        s02m007tDao.selectByExample(S02M007TExample);
                    List<String> srmlist = new ArrayList<>();
                    for (S02M007TEntity s02m007tEntity : s02m007tSlOutList) {
                        srmlist.add(s02m007tEntity.getShncd());
                    }

                    // 受注関係Entityの設定
                    C0805W01AcptRelationConditionDto acptRlationDto =
                        new C0805W01AcptRelationConditionDto();

                    acptRlationDto.setShnmctgrycd(shnmctgrycd);
                    acptRlationDto.setSrvlctgrycd(srvlctgrycd);
                    acptRlationDto.setSrvmctgrycd(srvmctgrycd);
                    acptRlationDto.setRgralist(rgralist);
                    acptRlationDto.setSrmlist(srmlist);
                    conditionDto.setAcptRlationDto(acptRlationDto);

                    // 得意先マスタ検索：単独TBL検索
                    S02M013TEntity tokuisakiEntity =
                        getTokuisakiMst(tokuisakiData.getTargetTokuisaki().getApkihoncd(),
                            tokuisakiData.getTargetTokuisaki().getShopcd(),
                            tokuisakiData.getTargetTokuisaki().getTokuisakicd());

                    List<C0805W01KeyDto> keyDtoList = new ArrayList<>();
                    for (int i = 0; i < 4; i++) {
                        C0805W01KeyDto keyDto = new C0805W01KeyDto();
                        if (i == 0) {
                            keyDto.setZaikoupdatetype("1");
                        } else if (i == 1) {
                            keyDto.setZaikoupdatetype("2");
                        } else if (i == 2) {
                            keyDto.setZaikoupdatetype("3");
                        } else if (i == 3) {
                            keyDto.setZaikoupdatetype("5");
                        }
                        if (!(i == 3 && CommonConstants.ZAIKOKANRIKBN_0
                            .equals(shopMstEntity.getZaikokanrikbn()))) {
                            // 定期受注在庫更新用サマリリスト取得
                            keyDto.setSummaryList(getSummeryList(conditionDto, tokuisakiEntity,
                                keyDto.getZaikoupdatetype()));
                            keyDtoList.add(keyDto);
                        }
                    }
                    // 受注在庫更新
                    updateZaiko(conditionDto, keyDtoList);
                }
            } else {
                log.debug("\t本部受注データ連携実行 … 得意先コード : {}",
                    tokuisakiData.getTargetTokuisaki().getTokuisakicd());
                // 本部受注データ連携
                tokuisakiData.getAcptdnpyList().stream().forEach(acptDnpy -> {
                    log.debug("\t\t連携対象受注伝票番号 : {}", acptDnpy.getAcptdnpyno());
                    C0804B01Entity acptLinkageParamter = new C0804B01Entity();
                    acptLinkageParamter.setApkihoncd(acptDnpy.getApkihoncd());
                    acptLinkageParamter.setShopcd(acptDnpy.getShopcd());
                    acptLinkageParamter.setAcptdnpyno(acptDnpy.getAcptdnpyno());
                    acptLinkageParamter.setEdano((int) acptDnpy.getEdano());
                    acptLinkageParamter.setMode(CommonConstants.HONBURENKEI_SHINKI);
                    c0804b01Dao.honbuJuchuDatRenkei(acptLinkageParamter);
                });
            }
        }

        log.debug("[終了] 受注伝票・明細更新処理");
    }

    /**
     * 定期受注作成結果テーブル更新
     * 
     * @param conditionDto 実行条件
     */
    private void updateRegularAcptResultTable(C0805W01ProcessConditionDto conditionDto) {
        log.debug("[開始] 定期受注作成結果テーブル更新");
        // 定期受注データ作成処理結果テーブル削除
        S04T004TEntityExample deleteCondition = new S04T004TEntityExample();
        S04T004TEntityExample.Criteria criteria =
            deleteCondition.createCriteria().andApkihoncdEqualTo(conditionDto.getApkihoncd())
                .andShopcdEqualTo(conditionDto.getShopcd());
        // 事業所コードが指定されている場合は事業所コードも削除条件に加える
        if (!Strings.isEmpty(conditionDto.getJigyoshocd())) {
            criteria.andJigyoshocdEqualTo(conditionDto.getJigyoshocd());
        }
        int deletedCount = s04t004tDao.deleteByExample(deleteCondition);
        log.debug("\t定期受注作成結果テーブル削除件数 : {}件", deletedCount);
        // 定期受注データ作成処理結果テーブル登録
        List<C0805W01TeikiAcptResultDto> resultList =
            Objects.requireNonNullElse(conditionDto.getTeikiAcptResultList(), List.of());
        try {
            log.debug("\t[開始] 定期受注作成結果テーブル登録");
            resultList.stream()
                // Entityに変換（行番号大は1から付与する）
                .map(util.withIndexFunction(1,
                    (resultDto, counter) -> createResultEntity(conditionDto, resultDto, counter)))
                // 登録
                .forEach(entity -> s04t004tDao.insertSelective(entity));
        } catch (DuplicateKeyException e) {
            // 一意制約違反の場合、エラー
            throw BusinessExceptionFactory.validationFailed(MessageID.W089);
        } finally {
            log.debug("\t[終了] 定期受注作成結果テーブル登録");
        }
        log.debug("[終了] 定期受注作成結果テーブル更新");
    }

    /**
     * 定期受注作成結果テーブル新規登録用Entityを生成する
     * 
     * @param conditionDto 実行条件
     * @param resultDto 定期受注作成処理結果DTO
     * @param counter 連番
     * @return 定期受注作成処理結果テーブルEntity
     */
    private S04T004TEntity createResultEntity(C0805W01ProcessConditionDto conditionDto,
        C0805W01TeikiAcptResultDto resultDto, int counter) {
        S04T004TEntity entity = new S04T004TEntity();
        entity.setApkihoncd(resultDto.getApkihoncd()); // AP基本コード
        entity.setShopcd(resultDto.getShopcd()); // ショップコード
        entity.setJigyoshocd(resultDto.getJigyoshocd()); // 事業所コード
        entity.setTokuisakicd(resultDto.getTokuisakicd()); // 得意先コード
        entity.setGyonol(counter);// 行番号大
        entity.setDlvryytidd(resultDto.getDlvryytidd()); // 配達予定日
        entity.setAcptdnpyno(resultDto.getAcptdnpyno()); // 受注伝票番号
        entity.setPrcstnshacd(conditionDto.getPrcstnshacd()); // 処理担当者コード
        entity.setJikkodd(conditionDto.getUnyodate()); // 実行日
        entity.setErrkbn(resultDto.getErrkbn());// エラー区分
        entity.setMsg(resultDto.getMsg()); // メッセージ
        entity.setYukokbn(CommonConstants.YUKOKBN_1); // 有効区分
        entity.setToshaapkihoncd(conditionDto.getLoginUserApkihoncd()); // 登録者ＡＰ基本コード
        entity.setToshashopcd(conditionDto.getLoginUserShopcd()); // 登録者ショップコード
        entity.setToshacd(conditionDto.getLoginUserTnshacd()); // 登録者コード
        entity.setInptsystmymd(conditionDto.getSystemDate()); // 登録システム日付
        entity.setInptsystmjikan(conditionDto.getSystemTime()); // 登録システム時間
        entity.setInptunyoymd(conditionDto.getUnyodate()); // 登録運用日付
        entity.setInptipadrse(conditionDto.getIpAddress()); // 登録ＩＰアドレス
        entity.setInptpgmid(conditionDto.getProgramId()); // 登録プログラムＩＤ
        entity.setKoshaapkihoncd(conditionDto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        entity.setKoshashopcd(conditionDto.getLoginUserShopcd()); // 更新者ショップコード
        entity.setKoshacd(conditionDto.getLoginUserTnshacd()); // 更新者コード
        entity.setKshnsystmymd(conditionDto.getSystemDate()); // 更新システム日付
        entity.setKshnsystmjikan(conditionDto.getSystemTime()); // 更新システム時間
        entity.setKshnunyoymd(conditionDto.getUnyodate()); // 更新運用日付
        entity.setKshnipadrse(conditionDto.getIpAddress()); // 更新ＩＰアドレス
        entity.setKshnpgmid(conditionDto.getProgramId()); // 更新プログラムＩＤ
        return entity;
    }

    /**
     * 受注伝票新規登録
     * 
     * @param conditionDto 実行条件
     * @param entity
     * @return
     */
    private void createAcptdnpy(C0805W01ProcessConditionDto conditionDto,
        S02M013TC0805W01Entity entity) {
        try {
            // INSERT実行
            s04t001tDao.insertSelective(createAcptDlvryTblEntity(conditionDto, entity));
        } catch (DuplicateKeyException e) {
            // 一意制約違反の場合、エラー
            throw BusinessExceptionFactory.validationFailed(MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(MessageID.E002);
        }
    }

    /**
     * 受注配達テーブル登録時の値を設定する。
     * 
     * @param conditionDto 実行条件
     * @param tokuisakiAcptdnpyEntity 得意先受注データEntity
     * @return 受注配達テーブルEntity
     */
    private S04T001TEntity createAcptDlvryTblEntity(C0805W01ProcessConditionDto conditionDto,
        S02M013TC0805W01Entity tokuisakiAcptdnpyEntity) {

        S04T001TEntity entity = new S04T001TEntity();
        entity.setApkihoncd(tokuisakiAcptdnpyEntity.getApkihoncd()); // AP基本コード
        entity.setShopcd(tokuisakiAcptdnpyEntity.getShopcd()); // ショップコード
        entity.setAcptdnpyno(tokuisakiAcptdnpyEntity.getAcptdnpyno()); // 受注伝票番号
        entity.setEdano((short) 0); // 枝番号
        entity.setItakuaiteapkihoncd(""); // 委託相手ＡＰ基本コード
        entity.setItakuaiteshopcd(""); // 委託相手ショップコード
        entity.setItakuaiteacptdnpyno(""); // 委託相手受注伝票番号
        entity.setItakuaiteedano(null); // 委託相手枝番号
        entity.setDlvrytrgtflg("1"); // 配達対象フラグ
        if (CommonConstants.KBN_TOKUISAKI_KEIHI.equals(tokuisakiAcptdnpyEntity.getTokuisakikbn())) {
            entity.setDnpykbn(CommonConstants.KBN_KEIHI_SYUKKO);
        } else {
            entity.setDnpykbn(CommonConstants.KBN_URIAGE_SYUKKO);
        } // 伝票区分
        if (CommonConstants.KBN_TOKUISAKI_KEIHI.equals(tokuisakiAcptdnpyEntity.getTokuisakikbn())) {
            entity.setTorihikikbn(CommonConstants.KBN_ACPTTORIHIKI_KEIHISHUKKO);
        } else {
            entity.setTorihikikbn(CommonConstants.KBN_ACPTTORIHIKI_JUCHU);
        } // 取引区分（受注予約伝票を登録することはないので経費かどうかだけが問題）
        entity.setAcptkbn(tokuisakiAcptdnpyEntity.getAcptkbn()); // 受注区分
        entity.setAcptkbnshs(tokuisakiAcptdnpyEntity.getAcptkbnshs()); // 受注区分詳細
        entity.setTokuisakicd(tokuisakiAcptdnpyEntity.getTokuisakicd()); // 得意先コード
        entity.setAcptdd(tokuisakiAcptdnpyEntity.getAcptdd()); // 受注日
        entity.setAcpttnshaapkihoncd(conditionDto.getAcpttnshaapkihoncd()); // 受注担当者ＡＰ基本コード
        entity.setAcpttnshashopcd(conditionDto.getAcpttnshashopcd()); // 受注担当者ショップコード
        entity.setAcpttnshacd(conditionDto.getAcpttnshacd()); // 受注担当者コード
        entity.setDlvryytidd(tokuisakiAcptdnpyEntity.getDlvryytidd());// 配達予定日（受注予約ではないので配達予定日は設定する）
        entity.setDlvryjissekidd(""); // 配達実績日
        entity.setDlvrydd(tokuisakiAcptdnpyEntity.getDlvryytidd()); // 配達日
        entity.setDlvrykibojikan(tokuisakiAcptdnpyEntity.getAcptjikanshiteijikoku()); // 配達希望時間
        entity.setDlvrykibojikanshry(tokuisakiAcptdnpyEntity.getAcptjikanshiteijikokushry()); // 配達希望時間終了
        entity.setDlvrykibojikankbn(tokuisakiAcptdnpyEntity.getAcptjikanshiteikbn()); // 配達希望時間区分
        entity.setFzaiokikanoflg(tokuisakiAcptdnpyEntity.getFzaiokikanoflg()); // 不在置き可能フラグ
        entity.setFzaiokijissekiflg(CommonConstants.FLG_OFF); // 不在置き実績フラグ
        entity.setDlvrytnshacd(tokuisakiAcptdnpyEntity.getDlvrytnshacd()); // 配達担当者コード
        entity.setDlvrykankatsuapkihoncd(tokuisakiAcptdnpyEntity.getDlvrykankatsuapkihoncd()); // 配達管轄ＡＰ基本コード
        entity.setDlvrykankatsushopcd(tokuisakiAcptdnpyEntity.getDlvrykankatsushopcd()); // 配達管轄ショップコード
        entity.setDlvrykyotencd(tokuisakiAcptdnpyEntity.getDlvrykankatsukyotencd()); // 配達拠点コード
        entity.setDlvryareacd(tokuisakiAcptdnpyEntity.getDlvryareacd()); // 配達エリアコード
        entity.setDlvrykbn(tokuisakiAcptdnpyEntity.getDlvrykbn()); // 配達区分
        entity.setDlvrycmpnycd(tokuisakiAcptdnpyEntity.getDlvrycmpnycd()); // 配達会社コード
        entity.setAzukaribtln1n2rittoruhns(
            StringUtils.isEmpty(tokuisakiAcptdnpyEntity.getAzukaribtln1n2rittoruhns()) ? 0
                : Integer.parseInt(tokuisakiAcptdnpyEntity.getAzukaribtln1n2rittoruhns())); // 預かりボトル１２Ｌ本数
        entity.setKybtln1n2rittoruhns(
            StringUtils.isEmpty(tokuisakiAcptdnpyEntity.getKaishyubtln1n2rittoruhns()) ? 0
                : Short.parseShort(tokuisakiAcptdnpyEntity.getKaishyubtln1n2rittoruhns())); // 回収予定ボトル１２Ｌ本数
        entity.setKzbtln1n2rittoruhns((short) 0); // 回収実績ボトル１２Ｌ本数
        entity.setAzukaribtln8rittoruhns(
            StringUtils.isEmpty(tokuisakiAcptdnpyEntity.getAzukaribtln8rittoruhns()) ? 0
                : Integer.parseInt(tokuisakiAcptdnpyEntity.getAzukaribtln8rittoruhns())); // 預かりボトル８Ｌ本数
        entity.setKybtln8rittoruhns(
            StringUtils.isEmpty(tokuisakiAcptdnpyEntity.getKaishyubtln8rittoruhns()) ? 0
                : Short.parseShort(tokuisakiAcptdnpyEntity.getKaishyubtln8rittoruhns())); // 回収予定ボトル８Ｌ本数
        entity.setKzbtln8rittoruhns((short) 0); // 回収実績ボトル８Ｌ本数
        entity.setPrntmemon1(tokuisakiAcptdnpyEntity.getPrntmemon1()); // 印刷メモ１
        entity.setPrntmemon2(tokuisakiAcptdnpyEntity.getPrntmemon2()); // 印刷メモ２
        entity.setPrntmemon3(tokuisakiAcptdnpyEntity.getPrntmemon3()); // 印刷メモ３
        entity.setAcptshimeprcsdd(CommonConstants.DEF_DATE_00000000); // 受注締担当者ＡＰ基本コード
        entity.setAcptshimetnshaapkihoncd(""); // 受注締担当者ＡＰ基本コード
        entity.setAcptshimetnshashopcd(""); // 受注締担当者ショップコード
        entity.setAcptshimeprcstnshacd(""); // 受注締処理担当者コード
        entity.setDlvryjissekiprcsdd(CommonConstants.DEF_DATE_00000000); // 配達実績処理日
        entity.setDlvryjissekimonoapkihoncd(""); // 配達実績者ＡＰ基本コード
        entity.setDlvryjissekimonoshopcd(""); // 配達実績者ショップコード
        entity.setDlvryjissekiprcstnshacd(""); // 配達実績処理担当者コード
        entity.setUrishimeprcsdd(CommonConstants.DEF_DATE_00000000); // 売上締処理日
        entity.setGetsujishimedydm(""); // 月次締年月
        entity.setGetsujishimedd(""); // 月次締日
        entity.setGetsujiprcsjissidd(CommonConstants.DEF_DATE_00000000); // 月次処理実施日
        entity.setDlvryjun(tokuisakiAcptdnpyEntity.getDlvryjun()); // 配達順

        entity.setNoshoprnttrgtflg(CommonConstants.FLG_ON); // 納品書印刷対象フラグ
        entity.setNoshoprntsumiflg(CommonConstants.FLG_OFF); // 納品書印刷済フラグ
        entity.setNoshoprntdd(""); // 納品書印刷日
        entity.setNoshohikaeprnttrgtflg(CommonConstants.FLG_ON); // 納品書控印刷対象フラグ
        entity.setNoshohikaeprntsumiflg(CommonConstants.FLG_OFF); // 納品書控印刷済フラグ
        entity.setNoshohikaeprntdd(""); // 納品書控印刷日
        entity.setNyknhyoprnttrgtflg(
            util.isFlagged(tokuisakiAcptdnpyEntity.getAcptthdaibikiflg()) ? CommonConstants.FLG_ON
                : CommonConstants.FLG_OFF);// 入金票印刷対象フラグ
        entity.setNyknhyoprntsumiflg(CommonConstants.FLG_OFF); // 入金票印刷済フラグ
        entity.setNyknhyoprntdd("");// 入金票印刷日
        entity.setWsvsijishoprnttrgtflg(CommonConstants.FLG_OFF); // サーバー指示書印刷対象フラグ
        entity.setWsvsijishoprntsumiflg(CommonConstants.FLG_OFF); // サーバー指示書印刷済フラグ
        entity.setWsvsijishoprntdd(""); // サーバー指示書印刷日

        entity.setSingldnpyflg(CommonConstants.FLG_OFF); // 単体伝票締めフラグ
        entity.setDnpysts(tokuisakiAcptdnpyEntity.getDnpysts());// 伝票ステータス(定期受注なので常に受注）
        entity.setAcptsts(tokuisakiAcptdnpyEntity.getDnpysts()); // 受注ステータス(定期受注なので常に受注）

        entity.setDlvrysts(CommonConstants.KBN_DLVRYSTS_NONE); // 配達ステータス
        entity.setDatsksmotokbn(tokuisakiAcptdnpyEntity.getDatsksmotokbn()); // データ作成元区分
        entity.setSksmotokanrikbn(""); // 作成元管理区分
        entity.setSksmotokanrino(""); // 作成元管理番号
        entity.setAcpthikiatesakiapkihoncd(tokuisakiAcptdnpyEntity.getDlvrykankatsuapkihoncd()); // 受注引当先ＡＰ基本コード
        entity.setAcpthikiatesakishopcd(tokuisakiAcptdnpyEntity.getDlvrykankatsushopcd()); // 受注引当先ショップコード
        entity.setAcpthikiatesakikyotencd(tokuisakiAcptdnpyEntity.getDlvrykankatsukyotencd()); // 受注引当先拠点コード
        entity.setDlvryytirgrayusyohns(tokuisakiAcptdnpyEntity.getRgrayusyohns().shortValue()); // 配達予定レギュラー有償本数
        entity.setDlvryytirgramushohns(tokuisakiAcptdnpyEntity.getRgramushohns().shortValue()); // 配達予定レギュラー無償本数
        entity.setDlvryytisrmyusyohns(tokuisakiAcptdnpyEntity.getSrmyusyohns().shortValue()); // 配達予定スリム有償本数
        entity.setDlvryytisrmmushohns(tokuisakiAcptdnpyEntity.getSrmmushohns().shortValue()); // 配達予定スリム無償本数
        entity.setDlvryytisonotayusyohns(tokuisakiAcptdnpyEntity.getSonotayusyohns().shortValue()); // 配達予定その他有償本数
        entity.setDlvryytisonotamushohns(tokuisakiAcptdnpyEntity.getSonotamushohns().shortValue()); // 配達予定その他無償本数
        entity
            .setDlvryytiseihinmizuurikin(tokuisakiAcptdnpyEntity.getSeihinmizuurikin().longValue()); // 配達予定製品水売上金額
        entity.setDlvryytisonotaurikin(tokuisakiAcptdnpyEntity.getSonotaurikin().longValue()); // 配達予定その他売上金額
        entity.setDlvryjissekirgrayusyohns((short) 0); // 配達実績レギュラー有償本数
        entity.setDlvryjissekirgramushohns((short) 0); // 配達実績レギュラー無償本数
        entity.setDlvryjissekisrmyusyohns((short) 0); // 配達実績スリム有償本数
        entity.setDlvryjissekisrmmushohns((short) 0); // 配達実績スリム無償本数
        entity.setDlvryjissekistyshns((short) 0); // 配達実績その他有償本数
        entity.setDlvryjissekistmshns((short) 0); // 配達実績その他無償本数
        entity.setDlvryjissekishsuakin(0); // 配達実績製品水売上金額
        entity.setDlvryjissekisonotaurikin(0); // 配達実績その他売上金額
        entity.setAcptkin(tokuisakiAcptdnpyEntity.getAcptkin()); // 受注金額
        entity.setAcptshohitaxkin(tokuisakiAcptdnpyEntity.getAcptshohitaxkin()); // 受注消費税金額
        entity.setAcptippannukikin(tokuisakiAcptdnpyEntity.getAcptippannukikin()); // 受注一般税抜金額
        entity.setAcptippantaxkin(tokuisakiAcptdnpyEntity.getAcptippantaxkin()); // 受注一般消費税金額
        entity.setAcptkeigennukikin(tokuisakiAcptdnpyEntity.getAcptkeigennukikin()); // 受注軽減税抜金額
        entity.setAcptkeigentaxkin(tokuisakiAcptdnpyEntity.getAcptkeigentaxkin()); // 受注軽減消費税金額
        entity.setAcpthikazeiuriikin(tokuisakiAcptdnpyEntity.getAcpthikazeiuriikin()); // 受注非課税金額
        entity.setAcptfukazeiuriikin(tokuisakiAcptdnpyEntity.getAcptfukazeiuriikin()); // 受注不課税売上金額
        entity.setNohinkin((long) 0); // 納品金額
        entity.setNohinshohitaxkin((long) 0); // 納品消費税金額
        entity.setNohinippannukikin((long) 0); // 納品一般税抜金額
        entity.setNohinippantaxkin((long) 0); // 納品一般消費税金額
        entity.setNohinkeigennukikin((long) 0); // 納品軽減税抜金額
        entity.setNohinkeigentaxkin((long) 0); // 納品軽減消費税金額
        entity.setNohinhikazeiuriikin((long) 0); // 納品非課税金額
        entity.setNohinfukazeiuriikin((long) 0); // 納品不課税売上金額
        entity.setKurerurittorustup((short) 0); // クレールＬ設置
        entity.setKurerurittoruhikiage((short) 0); // クレールＬ引上
        entity.setKureruesustup((short) 0); // クレールＳ設置
        entity.setKureruesuhikiage((short) 0); // クレールＳ引上
        entity.setSonotastup((short) 0); // その他設置
        entity.setSonotahikiage((short) 0); // その他引上
        entity.setTaxkeisankbn(tokuisakiAcptdnpyEntity.getTaxkeisankbn()); // 税計算区分（卸判定は実施済）
        entity.setZnkdlvrydd(""); // 前回配達日
        entity.setZnkdlvryhns((short) 0); // 前回配達本数
        entity.setJikaidlvryytidd(""); // 次回配達予定日
        entity.setZnkshimedd(""); // 前回締日
        entity.setZnkkurikoshikin(null); // 前回繰越金額
        entity.setAcptthdaibikiflg(tokuisakiAcptdnpyEntity.getAcptthdaibikiflg()); // 受注時代引フラグ
        entity.setAcptrcptkeirokbn(tokuisakiAcptdnpyEntity.getAcptrcptkeirokbn()); // 受注受付経路区分
        entity.setYukokbn(CommonConstants.YUKOKBN_1); // 有効区分
        entity.setToshaapkihoncd(conditionDto.getLoginUserApkihoncd()); // 登録者ＡＰ基本コード
        entity.setToshashopcd(conditionDto.getLoginUserShopcd()); // 登録者ショップコード
        entity.setToshacd(conditionDto.getLoginUserTnshacd()); // 登録者コード
        entity.setInptsystmymd(conditionDto.getSystemDate()); // 登録システム日付
        entity.setInptsystmjikan(conditionDto.getSystemTime()); // 登録システム時間
        entity.setInptunyoymd(conditionDto.getUnyodate()); // 登録運用日付
        entity.setInptipadrse(conditionDto.getIpAddress()); // 登録ＩＰアドレス
        entity.setInptpgmid(conditionDto.getProgramId()); // 登録プログラムＩＤ
        entity.setKoshaapkihoncd(conditionDto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        entity.setKoshashopcd(conditionDto.getLoginUserShopcd()); // 更新者ショップコード
        entity.setKoshacd(conditionDto.getLoginUserTnshacd()); // 更新者コード
        entity.setKshnsystmymd(conditionDto.getSystemDate()); // 更新システム日付
        entity.setKshnsystmjikan(conditionDto.getSystemTime()); // 更新システム時間
        entity.setKshnunyoymd(conditionDto.getUnyodate()); // 更新運用日付
        entity.setKshnipadrse(conditionDto.getIpAddress()); // 更新ＩＰアドレス
        entity.setKshnpgmid(conditionDto.getProgramId()); // 更新プログラムＩＤ

        return entity;

    }

    /**
     * 受注明細新規登録
     * 
     * @param conditionDto 実行条件
     * @param entity 受注伝票データEntity
     * @return
     */
    private void createAcptMeisai(C0805W01ProcessConditionDto conditionDto,
        S02M013TC0805W01Entity entity) {

        try {
            // DELETE実行
            s04t002tDao.deleteByExample(createDeleteConditionS04t002tEntity(entity));
            for (S04T002TC0805W01Entity meisaiDto : entity.getDetailEntityList()) {
                // INSERT実行
                s04t002tDao
                    .insertSelective(createInsertS04t002tEntity(conditionDto, entity, meisaiDto));
            }
        } catch (DuplicateKeyException e) {
            // 一意制約違反の場合、エラー
            throw BusinessExceptionFactory.validationFailed(MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(MessageID.E002);
        }
    }

    /**
     * 受注配達明細テーブル削除時の条件値を設定する。
     * 
     * @param dto 削除用DTO
     * @return S04T002TEntityExample
     */
    private S04T002TEntityExample createDeleteConditionS04t002tEntity(
        S02M013TC0805W01Entity c0805w01Entity) {

        S04T002TEntityExample entity = new S04T002TEntityExample();

        // クライテリア(SQL条件)の生成
        entity.createCriteria().andApkihoncdEqualTo(c0805w01Entity.getApkihoncd())
            .andShopcdEqualTo(c0805w01Entity.getShopcd())
            .andAcptdnpynoEqualTo(c0805w01Entity.getAcptdnpyno())
            .andEdanoEqualTo(c0805w01Entity.getEdano());

        return entity;
    }

    /**
     * 受注配達明細テーブル登録時の値を設定する。
     * 
     * @param conditionDto 実行条件
     * @param entity 受注伝票データEntity
     * @param meisaiEntity 受注伝票明細データEntity
     * @return S04T002TEntity
     */
    private S04T002TEntity createInsertS04t002tEntity(C0805W01ProcessConditionDto conditionDto,
        S02M013TC0805W01Entity inDto, S04T002TC0805W01Entity meisaiEntity) {

        S04T002TEntity entity = new S04T002TEntity();

        entity.setApkihoncd(inDto.getApkihoncd()); // AP基本コード
        entity.setShopcd(inDto.getShopcd()); // ショップコード
        entity.setAcptdnpyno(inDto.getAcptdnpyno()); // 受注伝票番号
        entity.setEdano((short) 0); // 枝番号
        entity.setAcptdnpymeisaino(meisaiEntity.getAcptdnpymeisaino()); // 受注伝票明細番号
        entity.setAcptdnpymeisaiedano(meisaiEntity.getAcptdnpymeisaiedano()); // 受注伝票明細枝番号
        entity.setItakuaiteapkihoncd(meisaiEntity.getItakuaiteapkihoncd()); // 委託相手ＡＰ基本コード
        entity.setItakuaiteshopcd(meisaiEntity.getItakuaiteshopcd()); // 委託相手ショップコード
        entity.setItakuaiteacptdnpyno(meisaiEntity.getItakuaiteacptdnpyno()); // 委託相手受注伝票番号
        entity.setItakuaiteedano(meisaiEntity.getItakuaiteedano()); // 委託相手枝番号
        entity.setItakuaiteacptdnpymeisaino(meisaiEntity.getItakuaiteacptdnpymeisaino()); // 委託相手受注伝票明細番号
        entity.setItakuaiteacptdnpymeisaiedano(meisaiEntity.getItakuaiteacptdnpymeisaiedano()); // 委託相手受注伝票明細枝番号
        if (!CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(meisaiEntity.getMeisaikbn())) {
            entity.setLctgrycd(meisaiEntity.getLctgrycd()); // 大分類コード
            entity.setMctgrycd(meisaiEntity.getMctgrycd()); // 中分類コード
            entity.setSctgrycd(meisaiEntity.getSctgrycd()); // 小分類コード
            entity.setShncd(meisaiEntity.getShncd()); // 商品コード
        }
        if (CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(meisaiEntity.getMeisaikbn())) {
            entity.setHaifubutsuapkihoncd(meisaiEntity.getHaifubutsuapkihoncd()); // ＡＰ基本コード
            entity.setHaifubutsushopcd(meisaiEntity.getHaifubutsushopcd()); // 配布物ショップコード
            entity.setHaifubutsucd(meisaiEntity.getHaifubutsucd()); // 配布物コード
        }
        if (!CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(meisaiEntity.getMeisaikbn())) {
            entity.setKatasiki(meisaiEntity.getKatasiki()); // 型式
            entity.setKeijokbn(meisaiEntity.getKeijokbn()); // 計上区分
        }
        entity.setAcptsuryo(meisaiEntity.getAcptsuryo()); // 受注数量
        if (!CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(meisaiEntity.getMeisaikbn())) {
            entity.setHyojununt(meisaiEntity.getHyojununt()); // 標準単価
            entity.setAcptunt(meisaiEntity.getAcptunt()); // 受注単価
            entity.setAcptkin(meisaiEntity.getAcptkin()); // 受注金額
            entity.setAcptuntshohitaxkin(meisaiEntity.getAcptuntshohitaxkin()); // 受注単価消費税金額
            entity.setAcptshohitaxkin(meisaiEntity.getAcptshohitaxkin()); // 受注消費税金額
        }
        entity.setItakucmpnyacptunt(meisaiEntity.getItakucmpnyacptunt()); // 委託会社受注単価
        entity.setItakucmpnyacptuntshohitaxkin(meisaiEntity.getItakucmpnyacptuntshohitaxkin()); // 委託会社受注単価消費税金額
        entity.setItakucmpnyacptshohitaxkin(meisaiEntity.getItakucmpnyacptshohitaxkin()); // 委託会社受注消費税金額
        // if (!CommonConstants.KBN_MEISAI_2.equals(meisaiEntity.getMeisaikbn())) {
        entity.setNohinsuryo(BigDecimal.ZERO); // 納品数量
        entity.setNohinunt(BigDecimal.ZERO); // 納品単価
        entity.setNohinkin((long) 0); // 納品金額
        entity.setNohinuntshohitaxkin((long) 0); // 納品単価消費税金額
        entity.setNohinshohitaxkin((long) 0); // 納品消費税金額
        // }
        entity.setSoukaisu(meisaiEntity.getSoukaisu()); // 総回数
        entity.setZankaisu(meisaiEntity.getZankaisu()); // 残回数
        if (!CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(meisaiEntity.getMeisaikbn())) {
            entity.setTaxkbn(meisaiEntity.getTaxkbn()); // 税区分
        }
        if (CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(meisaiEntity.getMeisaikbn())
            || CommonConstants.KBN_MEISAI_3.equals(meisaiEntity.getMeisaikbn())) {
            entity.setYusyokbn(meisaiEntity.getYusyokbn());
        } else {
            if (CommonConstants.KBN_KEIJO_KEIHI.equals(meisaiEntity.getKeijokbn())) {
                entity.setYusyokbn(CommonConstants.KBN_YUSHO_KEIHI);
            } else if (CommonConstants.KBN_KEIJO_SUBRENTAL.equals(meisaiEntity.getKeijokbn())
                || CommonConstants.KBN_KEIJO_RENTAL.equals(meisaiEntity.getKeijokbn())) {
                entity.setYusyokbn(CommonConstants.KBN_YUSHO_RENTAL);
            } else if (CommonConstants.KBN_KEIJO_URIAGE.equals(meisaiEntity.getKeijokbn())
                || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(meisaiEntity.getKeijokbn())) {
                entity.setYusyokbn(CommonConstants.KBN_YUSHO_YUSHO);
            } else {
                entity.setYusyokbn(CommonConstants.KBN_YUSHO_MUSHO);
            }
        } // 有償区分
        entity.setMeisaikbn(meisaiEntity.getMeisaikbn()); // 明細区分
        if (CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(meisaiEntity.getMeisaikbn())) {
            entity.setNyuryokumeisaiflg(CommonConstants.FLG_ON); // 入力明細フラグ
            entity.setTanpinmeisaiflg(CommonConstants.FLG_ON); // 単品明細フラグ
        } else {
            entity.setNyuryokumeisaiflg(meisaiEntity.getNyuryokumeisaiflg()); // 入力明細フラグ
            entity.setTanpinmeisaiflg(meisaiEntity.getTanpinmeisaiflg()); // 単品明細フラグ
        }
        if (!CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(meisaiEntity.getMeisaikbn())) {
            entity.setStshncd(meisaiEntity.getStshncd()); // セット商品コード
            entity.setMeisaibiko(meisaiEntity.getMeisaibiko()); // 明細備考
        }
        entity.setDatsksmotokbn(meisaiEntity.getDatsksmotokbn()); // データ作成元区分
        if (!CommonConstants.KBN_MEISAI_2.equals(meisaiEntity.getMeisaikbn())) {
            entity.setUridnpyno(meisaiEntity.getUridnpyno()); // 売上伝票番号
            entity.setUridnpyedano(meisaiEntity.getUridnpyedano()); // 売上伝票枝番号
            entity.setUridnpymeisaino(meisaiEntity.getUridnpymeisaino()); // 売上伝票明細番号
            entity.setUridnpymeisaiedano(meisaiEntity.getUridnpymeisaiedano()); // 売上伝票明細枝番号
        }
        entity.setYukokbn(CommonConstants.YUKOKBN_1); // 有効区分
        entity.setToshaapkihoncd(conditionDto.getLoginUserApkihoncd()); // 登録者ＡＰ基本コード
        entity.setToshashopcd(conditionDto.getLoginUserShopcd()); // 登録者ショップコード
        entity.setToshacd(conditionDto.getLoginUserTnshacd()); // 登録者コード
        entity.setInptsystmymd(conditionDto.getSystemDate()); // 登録システム日付
        entity.setInptsystmjikan(conditionDto.getSystemTime()); // 登録システム時間
        entity.setInptunyoymd(conditionDto.getUnyodate()); // 登録運用日付
        entity.setInptipadrse(conditionDto.getIpAddress()); // 登録ＩＰアドレス
        entity.setInptpgmid(conditionDto.getProgramId()); // 登録プログラムＩＤ
        entity.setKoshaapkihoncd(conditionDto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        entity.setKoshashopcd(conditionDto.getLoginUserShopcd()); // 更新者ショップコード
        entity.setKoshacd(conditionDto.getLoginUserTnshacd()); // 更新者コード
        entity.setKshnsystmymd(conditionDto.getSystemDate()); // 更新システム日付
        entity.setKshnsystmjikan(conditionDto.getSystemTime()); // 更新システム時間
        entity.setKshnunyoymd(conditionDto.getUnyodate()); // 更新運用日付
        entity.setKshnipadrse(conditionDto.getIpAddress()); // 更新ＩＰアドレス
        entity.setKshnpgmid(conditionDto.getProgramId()); // 更新プログラムＩＤ

        return entity;

    }

    /**
     * 配布物取込処理
     * 
     * @param conditionDto 実行条件
     * @param entity 受注伝票データEntity
     * @return
     */
    private void createHaifubutsu(C0805W01ProcessConditionDto conditionDto,
        S02M013TC0805W01Entity entity) {

        int result = 0;
        for (S04T002TC0805W01Entity meisaiDto : entity.getDetailEntityList()) {
            if (CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(meisaiDto.getMeisaikbn())) {
                try {

                    // UPDATE実行
                    result = s02m033tDao.updateByExampleSelective(
                        createUpdateSetS02m033tEntity(conditionDto, entity, meisaiDto),
                        createUpdateConditionS02m033tEntity(entity, meisaiDto));
                } catch (RuntimeException e) {
                    throw BusinessExceptionFactory.validationFailed(MessageID.E002);
                }

                if (result == 0) {
                    // 更新件数が0(排他制御)の場合、エラー
                    throw BusinessExceptionFactory.validationFailed(MessageID.W090);
                }
            }
        }

    }

    /**
     * 配布物企画対象得意先マスタ更新時のset値を設定する。
     * 
     * @param dto
     * @param headEntity
     * @param headEntity
     * @param meisaiEntity
     * @return S02M033TEntity
     */
    private S02M033TEntity createUpdateSetS02m033tEntity(C0805W01ProcessConditionDto dto,
        S02M013TC0805W01Entity headEntity, S04T002TC0805W01Entity meisaiEntity) {

        S02M033TEntity entity = new S02M033TEntity();

        entity.setApkihoncd(headEntity.getApkihoncd()); // ＡＰ基本コード
        entity.setShopcd(headEntity.getShopcd()); // ショップコード
        entity.setHaifubutsucd(meisaiEntity.getHaifubutsucd()); // 配布物コード
        entity.setTokuisakicd(headEntity.getTokuisakicd()); // 得意先コード
        entity.setHaifutrgtsts(CommonConstants.HAIFUTRGTSTS_2); // 配布対象ステータス
        entity.setAcptdnpyno(headEntity.getAcptdnpyno()); // 受注伝票番号
        entity.setEdano(headEntity.getEdano()); // 枝番号
        entity.setAcptdnpymeisaino(meisaiEntity.getAcptdnpymeisaino()); // 受注伝票明細番号
        entity.setAcpttorikomidd(headEntity.getAcptdd()); // 受注取込日
        entity.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        entity.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        entity.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        entity.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        entity.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        entity.setKshnunyoymd(dto.getUnyodate()); // 更新運用日付
        entity.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        entity.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return entity;
    }

    /**
     * 配布物企画対象得意先マスタ更新時の条件値を設定する。
     * 
     * @param headEntity
     * @param meisaiEntity
     * @return S02M033TEntityExample
     */
    private S02M033TEntityExample createUpdateConditionS02m033tEntity(
        S02M013TC0805W01Entity headEntity, S04T002TC0805W01Entity meisaiEntity) {

        S02M033TEntityExample entity = new S02M033TEntityExample();

        // クライテリア(SQL条件)の生成
        entity.createCriteria().andApkihoncdEqualTo(headEntity.getApkihoncd())
            .andShopcdEqualTo(headEntity.getShopcd())
            .andTokuisakicdEqualTo(headEntity.getTokuisakicd())
            .andHaifubutsucdEqualTo(meisaiEntity.getHaifubutsucd())
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1)
            .andHaifubutsucdNotEqualTo(CommonConstants.HAIFUTRGTSTS_2);

        return entity;
    }


    /**
     * 定期受注マスタ更新。
     * 
     * @param conditionDto 実行条件
     * @param entity
     * @return
     */
    private void updateTeikiJuchu(C0805W01ProcessConditionDto conditionDto,
        RglrAcptMstEntity entity) {

        int result = 0;
        try {
            // UPDATE実行
            result = s02m024tDao.updateByExampleSelective(
                createUpdateSetS02m024tEntity(conditionDto, entity),
                createUpdateConditionS02m024tEntity(entity));
        } catch (RuntimeException e) {
            throw BusinessExceptionFactory.validationFailed(MessageID.E002);
        }

        if (result == 0) {
            // 更新件数が0(排他制御)の場合、エラー
            throw BusinessExceptionFactory.validationFailed(MessageID.W090);
        }

    }

    /**
     * 定期受注マスタ更新時のset値を設定する。
     * 
     * @param dto
     * @param inEntity
     * @return S02M024TEntity
     */
    private RglrAcptMstEntity createUpdateSetS02m024tEntity(C0805W01ProcessConditionDto dto,
        RglrAcptMstEntity inEntity) {

        RglrAcptMstEntity entity = new RglrAcptMstEntity();

        entity.setJikaiytidd(inEntity.getJikaiytidd()); // 次回予定日
        entity.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        entity.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        entity.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        entity.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        entity.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        entity.setKshnunyoymd(dto.getUnyodate()); // 更新運用日付
        entity.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        entity.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return entity;
    }

    /**
     * 定期受注マスタ更新時の条件値を設定する。
     * 
     * @param inEntity
     * @return S02M024TEntityExample
     */
    private S02M024TEntityExample createUpdateConditionS02m024tEntity(RglrAcptMstEntity inEntity) {

        S02M024TEntityExample entity = new S02M024TEntityExample();

        // クライテリア(SQL条件)の生成
        entity.createCriteria().andApkihoncdEqualTo(inEntity.getApkihoncd())
            .andShopcdEqualTo(inEntity.getShopcd()).andTokuisakicdEqualTo(inEntity.getTokuisakicd())
            .andDlvryshuuEqualTo(inEntity.getDlvryshuu())
            .andShiteiyobiEqualTo(inEntity.getShiteiyobi())
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

        return entity;
    }

    /**
     * 特定定期受注マスタ更新。
     * 
     * @param conditionDto 実行条件
     * @param entity
     */
    private void updateTokuteiTeikiJuchu(C0805W01ProcessConditionDto conditionDto,
        MultiRglrAcptMstEntity entity) {

        int result = 0;
        try {
            // UPDATE実行
            result = s02m055tDao.updateByExampleSelective(
                createUpdateSetS02m055tEntity(conditionDto, entity),
                createUpdateConditionS02m055tEntity(entity));
        } catch (RuntimeException e) {
            throw BusinessExceptionFactory.validationFailed(MessageID.E002);
        }

        if (result == 0) {
            // 更新件数が0(排他制御)の場合、エラー
            throw BusinessExceptionFactory.validationFailed(MessageID.W090);
        }
    }

    /**
     * 特定定期受注マスタ更新時のset値を設定する。
     * 
     * @param dto
     * @param inEntity
     * @return S02M055TEntity
     */
    private MultiRglrAcptMstEntity createUpdateSetS02m055tEntity(C0805W01ProcessConditionDto dto,
        MultiRglrAcptMstEntity inEntity) {

        MultiRglrAcptMstEntity entity = new MultiRglrAcptMstEntity();

        entity.setJikaiytidd(inEntity.getJikaiytidd()); // 次回予定日
        entity.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        entity.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        entity.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        entity.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        entity.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        entity.setKshnunyoymd(dto.getUnyodate()); // 更新運用日付
        entity.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        entity.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return entity;
    }

    /**
     * 特定定期受注マスタ更新時の条件値を設定する。
     * 
     * @param inEntity
     * @return MultiRglrAcptMstEntityExample
     */
    private S02M055TEntityExample createUpdateConditionS02m055tEntity(
        MultiRglrAcptMstEntity inEntity) {

        S02M055TEntityExample entity = new S02M055TEntityExample();

        // クライテリア(SQL条件)の生成
        entity.createCriteria().andApkihoncdEqualTo(inEntity.getApkihoncd())
            .andShopcdEqualTo(inEntity.getShopcd()).andTokuisakicdEqualTo(inEntity.getTokuisakicd())
            .andShiteikbnEqualTo(inEntity.getShiteikbn())
            .andShiteiyousoEqualTo(inEntity.getShiteiyouso())
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

        return entity;
    }

    /**
     * 得意先実績テーブル更新
     * 
     * @param conditionDto 実行条件
     * @param tokuisakiData
     * @return int
     */
    private int updateTokuisakiJiseki(C0805W01ProcessConditionDto conditionDto,
        C0805W01TokuisakiDataConditionDto tokuisakiData) {

        int result = 0;

        if (Objects.isNull(tokuisakiData.getRelation().getJisseki())) {
            return result;
        }

        String keijomaeacptkin =
            String.valueOf(tokuisakiData.getRelation().getJisseki().getKeijomaeacptkin()
                + tokuisakiData.getKonkaiKeijoKin());
        if (11 < keijomaeacptkin.length()) {
            return result;
        }

        try {
            // UPDATE実行
            result = s02m050tDao.updateByExampleSelective(
                createUpdateSetS02m050tEntity(conditionDto, tokuisakiData),
                createUpdateConditionS02m050tEntity(tokuisakiData));
        } catch (RuntimeException e) {
            throw BusinessExceptionFactory.validationFailed(MessageID.E002);
        }

        if (result == 0) {
            // 更新件数が0(排他制御)の場合、エラー
            throw BusinessExceptionFactory.validationFailed(MessageID.W090);
        }

        return result;
    }

    /**
     * 得意先実績テーブル更新時のset値を設定する。
     * 
     * @param dto 実行条件
     * @param tokuisakiData
     * @return S02M050TEntity
     */
    private S02M050TEntity createUpdateSetS02m050tEntity(C0805W01ProcessConditionDto dto,
        C0805W01TokuisakiDataConditionDto tokuisakiData) {

        S02M050TEntity entity = new S02M050TEntity();
        if (!Strings.isEmpty(dto.getUnyodate())) {
            String lastAcptdd = tokuisakiData.getRelation().getJisseki().getSaishuacptdd();
            if (StringUtils.isEmpty(lastAcptdd) || util.isLater(dto.getUnyodate(), lastAcptdd)) {
                entity.setSaishuacptdd(dto.getUnyodate());
            }
        } // 最終受注日
        entity.setSaishuteikiacptsijidd(dto.getDlvryytidd()); // 最終定期受注指示日
        if (!Strings.isEmpty(tokuisakiData.getLastdlvryytidd())) {
            entity.setSaishuteikiacptdlvryytidd(tokuisakiData.getLastdlvryytidd()); // 最終定期受注配達予定日
        }
        entity.setKeijomaeacptkin(tokuisakiData.getRelation().getJisseki().getKeijomaeacptkin()
            + tokuisakiData.getKonkaiKeijoKin()); // 計上前受注金額
        entity.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        entity.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        entity.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        entity.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        entity.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        entity.setKshnunyoymd(dto.getUnyodate()); // 更新運用日付
        entity.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        entity.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return entity;
    }

    /**
     * 得意先実績テーブル更新時の条件値を設定する。
     * 
     * @param C0805W01TokuisakiDataConditionDto tokuisakiData
     * @return S02M050TEntityExample
     */
    private S02M050TEntityExample createUpdateConditionS02m050tEntity(
        C0805W01TokuisakiDataConditionDto tokuisakiData) {

        // クライテリア(SQL条件)の生成
        S02M050TEntityExample entity = new S02M050TEntityExample();
        entity.createCriteria()
            .andApkihoncdEqualTo(tokuisakiData.getRelation().getJisseki().getApkihoncd())
            .andShopcdEqualTo(tokuisakiData.getRelation().getJisseki().getShopcd())
            .andTokuisakicdEqualTo(tokuisakiData.getRelation().getJisseki().getTokuisakicd())
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

        return entity;
    }

    /**
     * 得意先マスタ更新
     * 
     * @param conditionDto 実行条件
     * @param tokuisakiData
     * @return
     */
    private void updateTokuisakiMaster(C0805W01ProcessConditionDto conditionDto,
        C0805W01TokuisakiDataConditionDto tokuisakiData) {
        int result = 0;
        try {
            // UPDATE実行
            result = s02m013tDao.updateByExampleSelective(
                createUpdateSetS02m013tEntity(conditionDto, tokuisakiData),
                createUpdateConditionS02m013tEntity(tokuisakiData));
        } catch (RuntimeException e) {
            throw BusinessExceptionFactory.validationFailed(MessageID.E002);
        }
        if (result == 0) {
            // 更新件数が0(排他制御)の場合、エラー
            throw BusinessExceptionFactory.validationFailed(MessageID.W090);
        }

    }

    /**
     * 得意先マスタ更新時のset値を設定する。
     * 
     * @param dto 実行条件
     * @param tokuisakiData
     * @return S02M013TEntity
     */
    private S02M013TEntity createUpdateSetS02m013tEntity(C0805W01ProcessConditionDto dto,
        C0805W01TokuisakiDataConditionDto tokuisakiData) {

        S02M013TEntity entity = new S02M013TEntity();

        entity.setTokuisakijotai(tokuisakiData.getRelation().getTokuisaki().getTokuisakijotai()); // 得意先状態
        entity.setJotaists(tokuisakiData.getRelation().getTokuisaki().getJotaists()); // 状態ステータス
        entity.setKyuminkbn(CommonConstants.KYUMINKBN_0); // 休眠区分
        entity.setNyukaidd(tokuisakiData.getRelation().getTokuisaki().getNyukaidd()); // 入会日

        entity.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        entity.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        entity.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        entity.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        entity.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        entity.setKshnunyoymd(dto.getUnyodate()); // 更新運用日付
        entity.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        entity.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return entity;
    }

    /**
     * 得意先マスタル更新時の条件値を設定する。
     * 
     * @param C0805W01TokuisakiDataConditionDto tokuisakiData
     * @return S02M013TEntityExample
     */
    private S02M013TEntityExample createUpdateConditionS02m013tEntity(
        C0805W01TokuisakiDataConditionDto tokuisakiData) {

        S02M013TEntityExample entity = new S02M013TEntityExample();

        // クライテリア(SQL条件)の生成
        entity.createCriteria()
            .andApkihoncdEqualTo(tokuisakiData.getRelation().getTokuisaki().getApkihoncd())
            .andShopcdEqualTo(tokuisakiData.getRelation().getTokuisaki().getShopcd())
            .andTokuisakicdEqualTo(tokuisakiData.getRelation().getTokuisaki().getTokuisakicd())
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

        return entity;
    }

    /**
     * システム管理マスタ設定値取得
     * 
     * @param setteino
     * @param setteicd
     * @return String
     */
    private String getSystemMasterValue(String setteino, String setteicd) {

        return s02m006tDao.findConfig(setteino, setteicd);

    }

    /**
     * 定期受注在庫更新用サマリリスト取得
     * 
     * @param conditionDto 実行条件
     * @param s02m013tEntity 得意先マスタEntity
     * @param zaikoupdatetype 在庫タイプ
     * @return List<C0805W02KeyConditionDto>
     */
    private List<C0805W02KeyConditionDto> getSummeryList(C0805W01ProcessConditionDto conditionDto,
        S02M013TEntity s02m013tEntity, String zaikoupdatetype) {

        S04T001TC0805W01Entity S04T001TC0805W01EntityIn = new S04T001TC0805W01Entity();
        S04T001TC0805W01EntityIn.setApkihoncd(conditionDto.getApkihoncd());
        S04T001TC0805W01EntityIn.setShopcd(conditionDto.getShopcd());
        S04T001TC0805W01EntityIn.setAcptdnpynostr(conditionDto.getAcptdnpynostr());
        S04T001TC0805W01EntityIn.setAcptdnpynoend(conditionDto.getAcptdnpynoend());
        S04T001TC0805W01EntityIn.setTokuisakicd(s02m013tEntity.getTokuisakicd());
        S04T001TC0805W01EntityIn.setRgralist(conditionDto.getAcptRlationDto().getRgralist());
        S04T001TC0805W01EntityIn.setSrmlist(conditionDto.getAcptRlationDto().getSrmlist());

        List<String> shncdList = new ArrayList<>();
        for (String shncd : conditionDto.getAcptRlationDto().getRgralist()) {
            shncdList.add(shncd);
        }
        for (String shncd : conditionDto.getAcptRlationDto().getSrmlist()) {
            shncdList.add(shncd);
        }
        S04T001TC0805W01EntityIn.setShncdlist(shncdList);

        List<S04T001TC0805W01Entity> S04T001TC0805W01EntityOutList = new ArrayList<>();
        if ("1".equals(zaikoupdatetype)) {
            S04T001TC0805W01EntityOutList = s04t001tDao
                .selectByC0805W01getTeikiZaikoListHonburenkeiSqlS04T005T(S04T001TC0805W01EntityIn);
        } else if ("2".equals(zaikoupdatetype)) {
            S04T001TC0805W01EntityOutList = s04t001tDao
                .selectByC0805W01getTeikiZaikoListHonburenkeiSqlS04T006T(S04T001TC0805W01EntityIn);
        } else if ("3".equals(zaikoupdatetype)) {
            S04T001TC0805W01EntityOutList = s04t001tDao
                .selectByC0805W01getTeikiZaikoListHonburenkeiSqlS04T007T(S04T001TC0805W01EntityIn);
        } else if ("5".equals(zaikoupdatetype)) {
            S04T001TC0805W01EntityOutList = s04t001tDao
                .selectByC0805W01getTeikiZaikoListHonburenkeiSqlS13T004T(S04T001TC0805W01EntityIn);
        }

        List<C0805W02KeyConditionDto> keyDtoList = new ArrayList<>();

        for (S04T001TC0805W01Entity entity : S04T001TC0805W01EntityOutList) {
            C0805W02KeyConditionDto key = new C0805W02KeyConditionDto();
            // モデルタイプ
            key.setModeltype(conditionDto.getZaikoupdatetype());
            // マイナスフラグ
            key.setMinusflg(false);
            // チェックフラグ
            key.setCheckflg(false);
            // AP基本コード
            key.setApkihoncd(entity.getDlvrykankatsuapkihoncd());
            // ショップコード
            key.setShopcd(entity.getDlvrykankatsushopcd());
            // 配達拠点コード
            if ("2".equals(conditionDto.getZaikoupdatetype())) {
                key.setDlvrykyotencd("00");
            } else {
                key.setDlvrykyotencd(entity.getDlvrykyotencd());
            }
            // 年月日
            if ("5".equals(conditionDto.getZaikoupdatetype())) {
                key.setDydmdd("");
            } else {
                key.setDydmdd(entity.getDlvryytidd());
            }
            // テーブル固有プライマリーキー
            key.setPrimarykey(entity.getFourthkey());
            // アラーム数量
            key.setAlarmsuu("0");
            // 数量
            key.setSuu(entity.getAcptsuryo());
            // チェック結果
            key.setCheckflg(true);

            keyDtoList.add(key);
        }

        return keyDtoList;
    }

    /**
     * 受注予約データ更新処理
     * 
     * @param conditionDto 実行条件
     * @param reservedAcptEntity 受注予約データEntity
     * @param zanCount 受注予約を反映した受注の件数（残回数をこの分減らす）
     */
    private void updateReservedAcptData(C0805W01ProcessConditionDto conditionDto,
        S04T001TC0805W01Entity reservedAcptEntity, int zanCount) {

        // 受注配達テーブルロック処理
        S04T001TC0805W01Entity s04t001tIn = new S04T001TC0805W01Entity();
        s04t001tIn.setApkihoncd(reservedAcptEntity.getApkihoncd());
        s04t001tIn.setShopcd(reservedAcptEntity.getShopcd());
        s04t001tIn.setAcptdnpyno(reservedAcptEntity.getAcptdnpyno());
        s04t001tIn.setEdano(reservedAcptEntity.getEdano());
        s04t001tDao.selectByC0805W01forUpdate(s04t001tIn);

        // 受注配達明細データ：単独TBL検索
        S04T002TEntityExample searchDetailCondition = new S04T002TEntityExample();
        searchDetailCondition.createCriteria()
            .andApkihoncdEqualTo(reservedAcptEntity.getApkihoncd())
            .andShopcdEqualTo(reservedAcptEntity.getShopcd())
            .andAcptdnpynoEqualTo(reservedAcptEntity.getAcptdnpyno())
            .andEdanoEqualTo(reservedAcptEntity.getEdano())
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<S04T002TEntity> reservedDetailList =
            s04t002tDao.selectByExample(searchDetailCondition);

        // 明細番号リストを作成
        List<String> yoyakuMeisaiNoList = util.convertList(reservedDetailList,
            detailEntity -> String.valueOf(detailEntity.getAcptdnpymeisaino()));

        // 受注予約更新部品呼び出し（残回数がある場合にあたらしい受注予約伝票を登録）
        RegistrationInfoDto regInfo = new RegistrationInfoDto();
        regInfo.setLoginUserApkihoncd(conditionDto.getLoginUserApkihoncd());
        regInfo.setLoginUserShopcd(conditionDto.getLoginUserShopcd());
        regInfo.setLoginUserTnshacd(conditionDto.getLoginUserTnshacd());
        regInfo.setSystemDate(conditionDto.getSystemDate());
        regInfo.setSystemTime(conditionDto.getSystemTime());
        regInfo.setIpAddress(conditionDto.getIpAddress());
        regInfo.setProgramId(conditionDto.getProgramId());
        juchuYykKshnService.juchuYykKshn(reservedAcptEntity.getApkihoncd(),
            reservedAcptEntity.getShopcd(), reservedAcptEntity.getAcptdnpyno(),
            String.valueOf(reservedAcptEntity.getEdano()), yoyakuMeisaiNoList, (short) zanCount,
            conditionDto.getUnyodate(), false, regInfo);

        // 受注に反映済の受注予約伝票は論理削除する
        updateJuchuDenpyo(conditionDto, reservedAcptEntity);
    }

    /**
     * 受注伝票更新
     * 
     * @param conditionDto 実行条件
     * @param s04t002tc0805w01cancelEntityOut 受注予約データEntity
     */
    private void updateJuchuDenpyo(C0805W01ProcessConditionDto conditionDto,
        S04T001TC0805W01Entity s04t002tc0805w01cancelEntityOut) {

        // 受注配達テーブルロック処理
        S04T001TC0805W01Entity s04t001tIn = new S04T001TC0805W01Entity();
        s04t001tIn.setApkihoncd(s04t002tc0805w01cancelEntityOut.getApkihoncd());
        s04t001tIn.setShopcd(s04t002tc0805w01cancelEntityOut.getShopcd());
        s04t001tIn.setAcptdnpyno(s04t002tc0805w01cancelEntityOut.getAcptdnpyno());
        s04t001tIn.setEdano(s04t002tc0805w01cancelEntityOut.getEdano());
        S04T001TC0805W01Entity s04t001tOut = s04t001tDao.selectByC0805W01forUpdate(s04t001tIn);

        // 有効区分セット
        S04T001TEntity s04t001tEntity = new S04T001TEntity();
        s04t001tEntity.setYukokbn(CommonConstants.YUKOKBN_0);

        int result = 0;
        try {
            // UPDATE実行
            result =
                s04t001tDao.updateByExampleSelective(createUpdateSetS04t001tEntity(conditionDto),
                    createUpdateConditionS04t001tEntity(s04t001tOut));
        } catch (RuntimeException e) {
            throw BusinessExceptionFactory.validationFailed(MessageID.E002);
        }

        if (result == 0) {
            // 更新件数が0(排他制御)の場合、エラー
            throw BusinessExceptionFactory.validationFailed(MessageID.W090);
        }

    }

    /**
     * 更新時のset値を設定する。
     * 
     * @param conditionDto 実行条件
     * @return S04T001TEntity
     */
    private S04T001TEntity createUpdateSetS04t001tEntity(C0805W01ProcessConditionDto conditionDto) {

        S04T001TEntity entity = new S04T001TEntity();

        entity.setYukokbn(CommonConstants.YUKOKBN_0); // 有効区分
        entity.setKoshaapkihoncd(conditionDto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        entity.setKoshashopcd(conditionDto.getLoginUserShopcd()); // 更新者ショップコード
        entity.setKoshacd(conditionDto.getLoginUserTnshacd()); // 更新者コード
        entity.setKshnsystmymd(conditionDto.getSystemDate()); // 更新システム日付
        entity.setKshnsystmjikan(conditionDto.getSystemTime()); // 更新システム時間
        entity.setKshnunyoymd(conditionDto.getUnyodate()); // 更新運用日付
        entity.setKshnipadrse(conditionDto.getIpAddress()); // 更新ＩＰアドレス
        entity.setKshnpgmid(conditionDto.getProgramId()); // 更新プログラムＩＤ

        return entity;
    }

    /**
     * 更新時の条件値を設定する。
     * 
     * @param s04t001tOut 受注配達データ
     * @return S04T001TEntityExample
     */
    private S04T001TEntityExample createUpdateConditionS04t001tEntity(
        S04T001TC0805W01Entity s04t001tOut) {

        S04T001TEntityExample entity = new S04T001TEntityExample();

        // クライテリア(SQL条件)の生成
        entity.createCriteria().andApkihoncdEqualTo(s04t001tOut.getApkihoncd())
            .andShopcdEqualTo(s04t001tOut.getShopcd())
            .andAcptdnpynoEqualTo(s04t001tOut.getAcptdnpyno())
            .andEdanoEqualTo(s04t001tOut.getEdano())
            .andKshnsystmymdEqualTo(s04t001tOut.getKshnsystmymd())
            .andKshnsystmjikanEqualTo(s04t001tOut.getKshnsystmjikan());

        return entity;
    }

    /**
     * 受注在庫更新
     * 
     * @param conditionDto 条件DTO
     * @param keyDtoList キーリスト
     */
    private void updateZaiko(C0805W01ProcessConditionDto conditionDto,
        List<C0805W01KeyDto> keyDtoList) {

        for (C0805W01KeyDto key : keyDtoList) {
            for (C0805W02KeyConditionDto summaryData : key.getSummaryList()) {
                switch (key.getZaikoupdatetype()) {
                    case "1": // 配達拠点受注アラームテーブル
                        s04t005tAlarmUpdate(conditionDto, summaryData);
                        break;
                    case "2": // 担当者受注アラームテーブル
                        s04t006tAlarmUpdate(conditionDto, summaryData);
                        break;
                    case "3": // 配達エリア受注アラームテーブル
                        s04t007tAlarmUpdate(conditionDto, summaryData);
                        break;
                    case "5": // 在庫引当テーブル
                        s13t004tZaikoUpdate(conditionDto, summaryData);
                        break;
                }
            }
        }
    }

    /**
     * 配達拠点受注アラームテーブルテーブル更新
     * 
     * @param conditionDto 条件DTO
     * @param summaryData 定期受注在庫更新用サマリデータ
     */
    private void s04t005tAlarmUpdate(C0805W01ProcessConditionDto conditionDto,
        C0805W02KeyConditionDto summaryData) {

        // 配達拠点受注アラームテーブル検索：単独TBL検索
        S04T005TEntityExample s04t005tExample = new S04T005TEntityExample();
        s04t005tExample.createCriteria().andApkihoncdEqualTo(summaryData.getApkihoncd())
            .andShopcdEqualTo(summaryData.getShopcd())
            .andDlvrykyotencdEqualTo(summaryData.getDlvrykyotencd())
            .andShncdEqualTo(summaryData.getPrimarykey()).andYmdEqualTo(summaryData.getDydmdd())
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<S04T005TEntity> s04t005tOutList = s04t005tDao.selectByExample(s04t005tExample);

        if (s04t005tOutList.size() > 0) {
            S04T005TEntity s04t005tOut = s04t005tOutList.get(0);

            // 受注数セット
            BigDecimal acptkazu =
                s04t005tOut.getAcptkazu().add(new BigDecimal(summaryData.getSuu()));

            // 配達拠点受注アラームテーブル更新
            int result = 0;
            try {
                // UPDATE実行
                result = s04t005tDao.updateByExampleSelective(
                    createUpdateSetS04t005tEntity(conditionDto, acptkazu),
                    createUpdateConditionS04t005tEntity(s04t005tOut));
            } catch (RuntimeException e) {
                throw BusinessExceptionFactory.validationFailed(MessageID.E002);
            }

            if (result == 0) {
                // 更新件数が0(排他制御)の場合、エラー
                throw BusinessExceptionFactory.validationFailed(MessageID.W090);
            }
        }

    }

    /**
     * 更新時のset値を設定する。
     * 
     * @param conditionDto 条件DTO
     * @param suryo 数量
     * @return S04T005TEntity
     */
    private S04T005TEntity createUpdateSetS04t005tEntity(C0805W01ProcessConditionDto conditionDto,
        BigDecimal suryo) {

        S04T005TEntity entity = new S04T005TEntity();

        entity.setAcptkazu(suryo); // 受注数
        entity.setKoshaapkihoncd(conditionDto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        entity.setKoshashopcd(conditionDto.getLoginUserShopcd()); // 更新者ショップコード
        entity.setKoshacd(conditionDto.getLoginUserTnshacd()); // 更新者コード
        entity.setKshnsystmymd(conditionDto.getSystemDate()); // 更新システム日付
        entity.setKshnsystmjikan(conditionDto.getSystemTime()); // 更新システム時間
        entity.setKshnunyoymd(conditionDto.getUnyodate()); // 更新運用日付
        entity.setKshnipadrse(conditionDto.getIpAddress()); // 更新ＩＰアドレス
        entity.setKshnpgmid(conditionDto.getProgramId()); // 更新プログラムＩＤ

        return entity;
    }

    /**
     * 更新時の条件値を設定する。
     * 
     * @param s04t005tOut 配達拠点受注アラームデータ
     * @return S04T005TEntityExample
     */
    private S04T005TEntityExample createUpdateConditionS04t005tEntity(S04T005TEntity s04t005tOut) {

        S04T005TEntityExample entity = new S04T005TEntityExample();

        // クライテリア(SQL条件)の生成
        entity.createCriteria().andApkihoncdEqualTo(s04t005tOut.getApkihoncd())
            .andShopcdEqualTo(s04t005tOut.getShopcd())
            .andDlvrykyotencdEqualTo(s04t005tOut.getDlvrykyotencd())
            .andShncdEqualTo(s04t005tOut.getShncd()).andRenbanEqualTo(s04t005tOut.getRenban())
            .andKshnsystmymdEqualTo(s04t005tOut.getKshnsystmymd())
            .andKshnsystmjikanEqualTo(s04t005tOut.getKshnsystmjikan());

        return entity;
    }

    /**
     * 担当者受注アラームテーブル更新
     * 
     * @param conditionDto 条件DTO
     * @param summaryData 定期受注在庫更新用サマリデータ
     */
    private void s04t006tAlarmUpdate(C0805W01ProcessConditionDto conditionDto,
        C0805W02KeyConditionDto summaryData) {

        // 担当者受注アラームテーブル検索：単独TBL検索
        S04T006TEntityExample s04t006tExample = new S04T006TEntityExample();
        s04t006tExample.createCriteria().andApkihoncdEqualTo(summaryData.getApkihoncd())
            .andShopcdEqualTo(summaryData.getShopcd())
            .andDlvrykyotencdEqualTo(summaryData.getDlvrykyotencd())
            .andTnshacdEqualTo(summaryData.getPrimarykey()).andYmdEqualTo(summaryData.getDydmdd())
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<S04T006TEntity> s04t006tOutList = s04t006tDao.selectByExample(s04t006tExample);

        if (s04t006tOutList.size() > 0) {

            S04T006TEntity s04t006tOut = s04t006tOutList.get(0);

            // 受注件数セット
            BigDecimal acptcnt = s04t006tOut.getAcptcnt().add(new BigDecimal(summaryData.getSuu()));

            // 担当者受注アラームテーブル更新
            int result = 0;
            try {
                // UPDATE実行
                result = s04t006tDao.updateByExampleSelective(
                    createUpdateSetS04t006tEntity(conditionDto, acptcnt),
                    createUpdateConditionS04t006tEntity(s04t006tOut));
            } catch (RuntimeException e) {
                throw BusinessExceptionFactory.validationFailed(MessageID.E002);
            }

            if (result == 0) {
                // 更新件数が0(排他制御)の場合、エラー
                throw BusinessExceptionFactory.validationFailed(MessageID.W090);
            }
        }

    }

    /**
     * 更新時のset値を設定する。
     * 
     * @param conditionDto 条件DTO
     * @param suryo 数量
     * @return S04T006TEntity
     */
    private S04T006TEntity createUpdateSetS04t006tEntity(C0805W01ProcessConditionDto conditionDto,
        BigDecimal suryo) {

        S04T006TEntity entity = new S04T006TEntity();

        entity.setAcptcnt(suryo); // 受注件数
        entity.setKoshaapkihoncd(conditionDto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        entity.setKoshashopcd(conditionDto.getLoginUserShopcd()); // 更新者ショップコード
        entity.setKoshacd(conditionDto.getLoginUserTnshacd()); // 更新者コード
        entity.setKshnsystmymd(conditionDto.getSystemDate()); // 更新システム日付
        entity.setKshnsystmjikan(conditionDto.getSystemTime()); // 更新システム時間
        entity.setKshnunyoymd(conditionDto.getUnyodate()); // 更新運用日付
        entity.setKshnipadrse(conditionDto.getIpAddress()); // 更新ＩＰアドレス
        entity.setKshnpgmid(conditionDto.getProgramId()); // 更新プログラムＩＤ

        return entity;
    }

    /**
     * 更新時の条件値を設定する。
     * 
     * @param s04t006tOut 担当者受注アラームデータ
     * @return S04T006TEntityExample
     */
    private S04T006TEntityExample createUpdateConditionS04t006tEntity(S04T006TEntity s04t006tOut) {

        S04T006TEntityExample entity = new S04T006TEntityExample();

        // クライテリア(SQL条件)の生成
        entity.createCriteria().andApkihoncdEqualTo(s04t006tOut.getApkihoncd())
            .andShopcdEqualTo(s04t006tOut.getShopcd())
            .andDlvrykyotencdEqualTo(s04t006tOut.getDlvrykyotencd())
            .andTnshacdEqualTo(s04t006tOut.getTnshacd()).andRenbanEqualTo(s04t006tOut.getRenban())
            .andKshnsystmymdEqualTo(s04t006tOut.getKshnsystmymd())
            .andKshnsystmjikanEqualTo(s04t006tOut.getKshnsystmjikan());

        return entity;
    }

    /**
     * 配達エリア受注アラームテーブル更新
     * 
     * @param conditionDto 条件DTO
     * @param summaryData 定期受注在庫更新用サマリデータ
     */
    private void s04t007tAlarmUpdate(C0805W01ProcessConditionDto conditionDto,
        C0805W02KeyConditionDto summaryData) {

        // 配達エリア受注アラームテーブル検索：単独TBL検索
        S04T007TEntityExample s04t007tExample = new S04T007TEntityExample();
        s04t007tExample.createCriteria().andApkihoncdEqualTo(summaryData.getApkihoncd())
            .andShopcdEqualTo(summaryData.getShopcd())
            .andDlvrykyotencdEqualTo(summaryData.getDlvrykyotencd())
            .andDlvryareacdEqualTo(summaryData.getPrimarykey())
            .andYmdEqualTo(summaryData.getDydmdd()).andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<S04T007TEntity> s04t007tOutList = s04t007tDao.selectByExample(s04t007tExample);

        if (s04t007tOutList.size() > 0) {
            S04T007TEntity s04t007tOut = s04t007tOutList.get(0);

            // 受注件数
            BigDecimal acptcnt = s04t007tOut.getAcptcnt().add(new BigDecimal(summaryData.getSuu()));

            // 配達エリア受注アラームテーブ更新
            int result = 0;
            try {
                // UPDATE実行
                result = s04t007tDao.updateByExampleSelective(
                    createUpdateSetS04t007tEntity(conditionDto, acptcnt),
                    createUpdateConditionS04t007tEntity(s04t007tOut));
            } catch (RuntimeException e) {
                throw BusinessExceptionFactory.validationFailed(MessageID.E002);
            }

            if (result == 0) {
                // 更新件数が0(排他制御)の場合、エラー
                throw BusinessExceptionFactory.validationFailed(MessageID.W090);
            }
        }
    }

    /**
     * 更新時のset値を設定する。
     * 
     * @param conditionDto 条件DTO
     * @param suryo 数量
     * @return S04T007TEntity
     */
    private S04T007TEntity createUpdateSetS04t007tEntity(C0805W01ProcessConditionDto conditionDto,
        BigDecimal suryo) {

        S04T007TEntity entity = new S04T007TEntity();

        entity.setAcptcnt(suryo); // 受注件数
        entity.setKoshaapkihoncd(conditionDto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        entity.setKoshashopcd(conditionDto.getLoginUserShopcd()); // 更新者ショップコード
        entity.setKoshacd(conditionDto.getLoginUserTnshacd()); // 更新者コード
        entity.setKshnsystmymd(conditionDto.getSystemDate()); // 更新システム日付
        entity.setKshnsystmjikan(conditionDto.getSystemTime()); // 更新システム時間
        entity.setKshnunyoymd(conditionDto.getUnyodate()); // 更新運用日付
        entity.setKshnipadrse(conditionDto.getIpAddress()); // 更新ＩＰアドレス
        entity.setKshnpgmid(conditionDto.getProgramId()); // 更新プログラムＩＤ

        return entity;
    }

    /**
     * 更新時の条件値を設定する。
     * 
     * @param s04t007tOut 担当者受注アラームデータ
     * @return S04T007TEntityExample
     */
    private S04T007TEntityExample createUpdateConditionS04t007tEntity(S04T007TEntity s04t007tOut) {

        S04T007TEntityExample entity = new S04T007TEntityExample();

        // クライテリア(SQL条件)の生成
        S04T007TEntityExample.Criteria criteria = entity.createCriteria();
        criteria.andApkihoncdEqualTo(s04t007tOut.getApkihoncd())
            .andShopcdEqualTo(s04t007tOut.getShopcd())
            .andDlvrykyotencdEqualTo(s04t007tOut.getDlvrykyotencd())
            .andDlvryareacdEqualTo(s04t007tOut.getDlvryareacd())
            .andRenbanEqualTo(s04t007tOut.getRenban())
            .andKshnsystmymdEqualTo(s04t007tOut.getKshnsystmymd())
            .andKshnsystmjikanEqualTo(s04t007tOut.getKshnsystmjikan());

        return entity;
    }

    /**
     * 在庫引当テーブル更新
     * 
     * @param conditionDto 条件DTO
     * @param summaryData 定期受注在庫更新用サマリデータ
     */
    private void s13t004tZaikoUpdate(C0805W01ProcessConditionDto conditionDto,
        C0805W02KeyConditionDto summaryData) {

        // 在庫引当テーブル検索：単独TBL検索
        S13T004TEntityExample s13t004tExample = new S13T004TEntityExample();
        S13T004TEntityExample.Criteria s13t004tCriteria = s13t004tExample.createCriteria();
        s13t004tCriteria.andApkihoncdEqualTo(summaryData.getApkihoncd())
            .andShopcdEqualTo(summaryData.getShopcd())
            .andDlvrykyotencdEqualTo(summaryData.getDlvrykyotencd())
            .andShncdEqualTo(summaryData.getPrimarykey())
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<S13T004TEntity> s13m004tOutList = s13t004tDao.selectByExample(s13t004tExample);

        if (s13m004tOutList.size() > 0) {
            S13T004TEntity s13m004tOut = s13m004tOutList.get(0);

            // 引当在庫数セット
            BigDecimal hikiatesumizaikokazu =
                s13m004tOut.getHikiatesumizaikokazu().add(new BigDecimal(summaryData.getSuu()));

            // 在庫テーブル更新
            int result = 0;
            try {
                // UPDATE実行
                result = s13t004tDao.updateByExampleSelective(
                    createUpdateSetS13t004tEntity(conditionDto, hikiatesumizaikokazu),
                    createUpdateConditionS13t004tEntity(s13m004tOut));
            } catch (RuntimeException e) {
                throw BusinessExceptionFactory.validationFailed(MessageID.E002);
            }

            if (result == 0) {
                // 更新件数が0(排他制御)の場合、エラー
                throw BusinessExceptionFactory.validationFailed(MessageID.W090);
            }
        }
    }

    /**
     * 更新時のset値を設定する。
     * 
     * @param conditionDto 条件DTO
     * @param suryo 数量
     * @return S13T004TEntity
     */
    private S13T004TEntity createUpdateSetS13t004tEntity(C0805W01ProcessConditionDto conditionDto,
        BigDecimal suryo) {

        S13T004TEntity entity = new S13T004TEntity();

        entity.setHikiatesumizaikokazu(suryo); // 引当済在庫数
        entity.setKoshaapkihoncd(conditionDto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        entity.setKoshashopcd(conditionDto.getLoginUserShopcd()); // 更新者ショップコード
        entity.setKoshacd(conditionDto.getLoginUserTnshacd()); // 更新者コード
        entity.setKshnsystmymd(conditionDto.getSystemDate()); // 更新システム日付
        entity.setKshnsystmjikan(conditionDto.getSystemTime()); // 更新システム時間
        entity.setKshnunyoymd(conditionDto.getUnyodate()); // 更新運用日付
        entity.setKshnipadrse(conditionDto.getIpAddress()); // 更新ＩＰアドレス
        entity.setKshnpgmid(conditionDto.getProgramId()); // 更新プログラムＩＤ

        return entity;
    }

    /**
     * 更新時の条件値を設定する。
     * 
     * @param s13t004tOut 在庫引当データ
     * @return S13T004TEntityExample
     */
    private S13T004TEntityExample createUpdateConditionS13t004tEntity(S13T004TEntity s13t004tOut) {

        S13T004TEntityExample entity = new S13T004TEntityExample();

        // クライテリア(SQL条件)の生成
        S13T004TEntityExample.Criteria criteria = entity.createCriteria();
        criteria.andApkihoncdEqualTo(s13t004tOut.getApkihoncd())
            .andShopcdEqualTo(s13t004tOut.getShopcd())
            .andDlvrykyotencdEqualTo(s13t004tOut.getDlvrykyotencd())
            .andShncdEqualTo(s13t004tOut.getShncd())
            .andKshnsystmymdEqualTo(s13t004tOut.getKshnsystmymd())
            .andKshnsystmjikanEqualTo(s13t004tOut.getKshnsystmjikan());

        return entity;
    }

    /**
     * 受注予約伝票情報取得
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @param jikaiytidd 次回予定日
     * @return 受注予約伝票
     */
    private S04T001TC0805W01Entity getReservedAcptdnpyEntity(String apkihoncd, String shopcd,
        String tokuisakicd, String jikaiytidd) {
        S04T001TC0805W01Entity condition = new S04T001TC0805W01Entity();
        condition.setApkihoncd(apkihoncd);
        condition.setShopcd(shopcd);
        condition.setTokuisakicd(tokuisakicd);
        condition.setUnyodate(jikaiytidd);
        return s04t001tDao.selectByC0805W01getAcptdnpydata(condition);
    }

    /**
     * 指定された受注予約に紐づく受注予約明細リストを取得する
     * 
     * @param reservedAcptdnpyEntity 受注予約伝票Entity
     * @param reservedDataSetToAcptdnpyCount 受注予約明細を紐づけた受注データの数
     * @return 受注予約明細Entityリスト（残回数が0より大きいもののみ）
     */
    private List<S04T002TC0805W01Entity> getReservedDetailEntityList(
        S04T001TC0805W01Entity reservedAcptdnpyEntity, int reservedDataSetToAcptdnpyCount) {
        log.debug("[開始] 受注予約明細取得");
        log.debug("\t受注予約の受注伝票番号 : {}-{}-{}-{}", reservedAcptdnpyEntity.getApkihoncd(),
            reservedAcptdnpyEntity.getShopcd(), reservedAcptdnpyEntity.getAcptdnpyno(),
            reservedAcptdnpyEntity.getEdano());
        // 受注明細データ取得
        S04T002TC0805W01Entity acptDetailTblCondition = new S04T002TC0805W01Entity();
        acptDetailTblCondition.setApkihoncd(reservedAcptdnpyEntity.getApkihoncd());
        acptDetailTblCondition.setShopcd(reservedAcptdnpyEntity.getShopcd());
        acptDetailTblCondition.setAcptdnpyno(reservedAcptdnpyEntity.getAcptdnpyno());
        acptDetailTblCondition.setEdano(reservedAcptdnpyEntity.getEdano());
        // 価格基準日は配達予定日
        acptDetailTblCondition.setKakakugetbasedd(reservedAcptdnpyEntity.getDlvryytidd());
        List<S04T002TC0805W01Entity> reservedDetailEntityList =
            s04t002tDao.selectByC0805W01getJuchuMeisai(acptDetailTblCondition).stream()
                // 入力行のみに絞り込む（セット品の展開は別途実施）
                .filter(e -> util.isFlagged(e.getNyuryokumeisaiflg()))
                // 残回数が残っているもののみに絞り込む
                .filter(e -> {
                    short remainedCount = e.getZankaisu();
                    log.debug("\t\t受注予約明細（明細番号:{}-{} / 商品コード : {}）の残回数 : {}",
                        e.getAcptdnpymeisaino(), e.getAcptdnpymeisaiedano(), e.getShncd(),
                        remainedCount);
                    log.debug("\t\t受注予約明細を反映する受注データ数 : {}", reservedDataSetToAcptdnpyCount);
                    return remainedCount - reservedDataSetToAcptdnpyCount >= 0;
                }).collect(util.toList());
        log.debug("\t受注予約明細の件数 : {}件", reservedDetailEntityList.size());
        log.debug("[終了] 受注予約明細取得");
        return reservedDetailEntityList;
    }

    /**
     * 与信限度チェック
     * 
     * @param tokuisakiData 得意先データEntity
     * @param tRelation 得意先関係DTO
     * @param tokuisakiRglrAcptdnpyEntity 得意先定期受注伝票Entity
     * @param shopEntity ショップマスタEntity
     * @return 与信限度額を超えている場合にtrue
     */
    private boolean checkCreditlimit(C0805W01TokuisakiDataConditionDto tokuisakiData,
        C0805W01TokuisakiRelationDto tRelation, S02M013TC0805W01Entity tokuisakiRglrAcptdnpyEntity,
        S02M046TEntity shopEntity) {
        // 受注配達明細（商品入力行のみ）の受注金額と受注消費税金額をサマリし、伝票に設定
        tokuisakiRglrAcptdnpyEntity.setAcptkin(tokuisakiRglrAcptdnpyEntity.getDetailEntityList()
            .stream().filter(inputShnFilter).mapToLong(S04T002TC0805W01Entity::getAcptkin).sum());
        tokuisakiRglrAcptdnpyEntity.setAcptshohitaxkin(
            tokuisakiRglrAcptdnpyEntity.getDetailEntityList().stream().filter(inputShnFilter)
                .mapToLong(S04T002TC0805W01Entity::getAcptshohitaxkin).sum());
        // 今回計上金額に受注伝票の金額を反映する
        long workKin = tokuisakiData.getKonkaiKeijoKin();
        if (util.isLater(tokuisakiRglrAcptdnpyEntity.getDlvryytidd(),
            shopEntity.getInvoicshikoymd())) {
            // 配達予定日がインボイス施行日以降の場合、受注金額のみ反映
            workKin += tokuisakiRglrAcptdnpyEntity.getAcptkin();
        } else {
            // 配達予定日がインボイス施行日より前の場合、受注金額と受注消費税金額を反映
            workKin += (tokuisakiRglrAcptdnpyEntity.getAcptkin()
                + tokuisakiRglrAcptdnpyEntity.getAcptshohitaxkin());
        }
        tokuisakiData.setKonkaiKeijoKin(workKin);
        // 得意先マスタ.与信限度額が"未設定（NULL）以外"かつ、"0以外"の場合、与信限度額を超えていないか判定する。
        if (!Objects.isNull(tRelation.getTokuisaki().getYoshingendokin())
            && tRelation.getTokuisaki().getYoshingendokin() != 0) {
            // 与信限度額 < 現在残高＋計上前受注金額＋今回計上金額ならば超過とする
            if (tRelation.getTokuisaki().getYoshingendokin() < tRelation.getJisseki().getGenzaizd()
                + tRelation.getJisseki().getKeijomaeacptkin() + workKin) {
                return true;
            }
        }
        return false;
    }

    /**
     * 受注明細リストのセット商品を展開
     * 
     * @param s02m013tAcptDenpyoInfo 得意先配達情報データEntity
     * @param s04t002tc0805w01entityOutList 受注明細データEntity
     * @param s02m046tIn ショップマスタEntity
     * @param s02m033tEntityOutList 配布物企画対象得意先マスタEntity
     * @return List<S04T002TC0805W01Entity>
     */
    private List<S04T002TC0805W01Entity> deploymentSetShohin(
        S02M013TC0805W01Entity s02m013tAcptDenpyoInfo,
        List<S04T002TC0805W01Entity> s04t002tc0805w01entityOutList, S02M046TEntity s02m046tIn,
        List<S02M033TC0805W01Entity> s02m033tEntityOutList) {

        List<S04T002TC0805W01Entity> retList = new ArrayList<>();
        List<S04T002TC0805W01Entity> retTmpList = new ArrayList<>();

        // 「製品水」の商品大分類コード
        String shnmctgrycd =
            getSystemMasterValue(CommonConstants.SYS_SETTEINO_096, CommonConstants.SETTEICD_096_1);
        // 「サーバー関連用」の商品大分類コード
        String srvlctgrycd =
            getSystemMasterValue(CommonConstants.SYS_SETTEINO_096, CommonConstants.SETTEICD_096_4);
        // 「サーバー関連本体」の商品大分類コード
        String srvmctgrycd =
            getSystemMasterValue(CommonConstants.SYS_SETTEINO_134, CommonConstants.SETTEICD_134_41);
        // 「レギュラー」の商品コードリスト
        S02M007TEntityExample S02M007TExample = new S02M007TEntityExample();
        S02M007TEntityExample.Criteria S02M007TCriteria = S02M007TExample.createCriteria();
        S02M007TCriteria.andApkihoncdEqualTo(CommonConstants.APCD_001)
            .andStkbnEqualTo(CommonConstants.STKBN_0)
            .andLctgrycdEqualTo(CommonConstants.LCTGRYCD_01)
            .andMctgrycdEqualTo(CommonConstants.MCTGRYCD_001)
            .andSctgrycdEqualTo(CommonConstants.SCTGRYCD_001)
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<S02M007TEntity> s02m007tRgOutList = s02m007tDao.selectByExample(S02M007TExample);
        List<String> rgralist = new ArrayList<>();
        for (S02M007TEntity s02m007tEntity : s02m007tRgOutList) {
            rgralist.add(s02m007tEntity.getShncd());
        }

        // 「スリム」の商品コードリスト
        S02M007TExample = new S02M007TEntityExample();
        S02M007TCriteria = S02M007TExample.createCriteria();
        S02M007TCriteria.andApkihoncdEqualTo(CommonConstants.APCD_001)
            .andStkbnEqualTo(CommonConstants.STKBN_0)
            .andLctgrycdEqualTo(CommonConstants.LCTGRYCD_01)
            .andMctgrycdEqualTo(CommonConstants.MCTGRYCD_001)
            .andSctgrycdEqualTo(CommonConstants.SCTGRYCD_002)
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<S02M007TEntity> s02m007tSlOutList = s02m007tDao.selectByExample(S02M007TExample);
        List<String> srmlist = new ArrayList<>();
        for (S02M007TEntity s02m007tEntity : s02m007tSlOutList) {
            srmlist.add(s02m007tEntity.getShncd());
        }

        // 受注関係Entityの設定
        C0805W01AcptRelationConditionDto acptrelationdto = new C0805W01AcptRelationConditionDto();
        acptrelationdto.setShnmctgrycd(shnmctgrycd);
        acptrelationdto.setSrvlctgrycd(srvlctgrycd);
        acptrelationdto.setSrvmctgrycd(srvmctgrycd);
        acptrelationdto.setRgralist(rgralist);
        acptrelationdto.setSrmlist(srmlist);
        acptrelationdto.setTokuisakikbn(s02m013tAcptDenpyoInfo.getTokuisakikbn());
        acptrelationdto.setAcptkbn(s02m013tAcptDenpyoInfo.getAcptkbn());
        acptrelationdto.setAcptkbnshs(s02m013tAcptDenpyoInfo.getAcptkbnshs());

        // ボトル本数、売上、サーバ台数集計
        int counter = 0;
        BigDecimal rgrayusyohns = BigDecimal.ZERO;
        BigDecimal rgramushohns = BigDecimal.ZERO;
        BigDecimal srmyusyohns = BigDecimal.ZERO;
        BigDecimal srmmushohns = BigDecimal.ZERO;
        BigDecimal sonotayusyohns = BigDecimal.ZERO;
        BigDecimal sonotamushohns = BigDecimal.ZERO;
        BigDecimal seihinmizuurikin = BigDecimal.ZERO;
        BigDecimal sonotaurikin = BigDecimal.ZERO;
        BigDecimal srvchngesuu = BigDecimal.ZERO;

        for (S04T002TC0805W01Entity c805w01Entity : s04t002tc0805w01entityOutList) {
            counter++;

            long saveAcptshohitaxkin = (long) 0;
            if (c805w01Entity.getAcptshohitaxkin() != null) {
                saveAcptshohitaxkin = c805w01Entity.getAcptshohitaxkin();
            }
            // 商品マスタビュー(単独TBL検索）
            S02V001TEntity shnEntity = getShnInfoFromView(s02m013tAcptDenpyoInfo.getApkihoncd(),
                s02m013tAcptDenpyoInfo.getShopcd(), c805w01Entity.getShncd()).get();

            // 商品マスタビューEntity.税設定区分が「0:しない」の場合、商品マスタビューEntity.税区分は「null」（課税）として設定
            if ("0".equals(shnEntity.getTaxsetteikbn())) {
                shnEntity.setTaxkbn(null);
            }

            // 受注明細データEntityの設定
            c805w01Entity.setLctgrycd(shnEntity.getLctgrycd());
            c805w01Entity.setMctgrycd(shnEntity.getMctgrycd());
            c805w01Entity.setSctgrycd(shnEntity.getSctgrycd());
            c805w01Entity.setTaxkbn(shnEntity.getTaxkbn());
            // 受注関係Entityをコピーし、商品の情報を追加
            c805w01Entity.setRelshnmctgrycd(acptrelationdto.getShnmctgrycd());
            c805w01Entity.setRelsrvlctgrycd(acptrelationdto.getSrvlctgrycd());
            c805w01Entity.setRelsrvmctgrycd(acptrelationdto.getSrvmctgrycd());
            c805w01Entity.setRelkeijokbn(c805w01Entity.getKeijokbn());
            c805w01Entity.setRelrgralist(acptrelationdto.getRgralist());
            c805w01Entity.setRelsrmlist(acptrelationdto.getSrmlist());
            c805w01Entity.setReltokuisakikbn(acptrelationdto.getTokuisakikbn());
            c805w01Entity.setRelacptkbn(acptrelationdto.getAcptkbn());
            c805w01Entity.setRelacptkbnshs(acptrelationdto.getAcptkbnshs());
            c805w01Entity.setRellctgrycd(c805w01Entity.getLctgrycd());
            c805w01Entity.setRelmctgrycd(c805w01Entity.getMctgrycd());
            c805w01Entity.setRelshncd(c805w01Entity.getShncd());
            // 受注明細データEntityに商品情報を設定
            c805w01Entity.setS02v001TEntity(shnEntity);
            // 受注明細データEntity値セット
            c805w01Entity.setAcptdnpymeisaino(counter);
            c805w01Entity.setNyuryokumeisaiflg(CommonConstants.FLG_ON);
            c805w01Entity.setMeisaikbn(CommonConstants.KBN_MEISAI_SHOHIN);

            // 本数、金額、サーバー交換台数の集計（セット商品は対象外）
            if (CommonConstants.TANPINMEISAIFLG_1.equals(c805w01Entity.getTanpinmeisaiflg())
                && CommonConstants.KBN_MEISAI_SHOHIN.equals(c805w01Entity.getMeisaikbn())) {

                // 受注伝票集計Entity
                // レギュラー有償本数
                if (c805w01Entity.getRelrgralist().indexOf(c805w01Entity.getShncd()) >= 0
                    && (CommonConstants.KBN_KEIJO_URIAGE.equals(c805w01Entity.getKeijokbn())
                        || CommonConstants.KBN_KEIJO_CAMPAIGN
                            .equals(c805w01Entity.getKeijokbn()))) {
                    rgrayusyohns = rgrayusyohns.add(c805w01Entity.getAcptsuryo());
                }
                // レギュラー無償本数
                if (c805w01Entity.getRelrgralist().indexOf(c805w01Entity.getShncd()) >= 0
                    && !(CommonConstants.KBN_KEIJO_URIAGE.equals(c805w01Entity.getKeijokbn())
                        || CommonConstants.KBN_KEIJO_CAMPAIGN
                            .equals(c805w01Entity.getKeijokbn()))) {
                    rgramushohns = rgramushohns.add(c805w01Entity.getAcptsuryo());
                }
                // スリム有償本数
                if (c805w01Entity.getRelsrmlist().indexOf(c805w01Entity.getShncd()) >= 0
                    && (CommonConstants.KBN_KEIJO_URIAGE.equals(c805w01Entity.getKeijokbn())
                        || CommonConstants.KBN_KEIJO_CAMPAIGN
                            .equals(c805w01Entity.getKeijokbn()))) {
                    srmyusyohns = srmyusyohns.add(c805w01Entity.getAcptsuryo());
                }
                // スリム無償本数
                if (c805w01Entity.getRelsrmlist().indexOf(c805w01Entity.getShncd()) >= 0
                    && !(CommonConstants.KBN_KEIJO_URIAGE.equals(c805w01Entity.getKeijokbn())
                        || CommonConstants.KBN_KEIJO_CAMPAIGN
                            .equals(c805w01Entity.getKeijokbn()))) {
                    srmmushohns = srmmushohns.add(c805w01Entity.getAcptsuryo());
                }
                // その他有償本数
                if (c805w01Entity.getRelshnmctgrycd().equals(c805w01Entity.getLctgrycd())
                    && c805w01Entity.getRelrgralist().indexOf(c805w01Entity.getShncd()) < 0
                    && c805w01Entity.getRelsrmlist().indexOf(c805w01Entity.getShncd()) < 0
                    && (CommonConstants.KBN_KEIJO_URIAGE.equals(c805w01Entity.getKeijokbn())
                        || CommonConstants.KBN_KEIJO_CAMPAIGN
                            .equals(c805w01Entity.getKeijokbn()))) {
                    sonotayusyohns = sonotayusyohns.add(c805w01Entity.getAcptsuryo());
                }
                // その他無償本数
                if (c805w01Entity.getRelshnmctgrycd().equals(c805w01Entity.getLctgrycd())
                    && c805w01Entity.getRelrgralist().indexOf(c805w01Entity.getShncd()) < 0
                    && c805w01Entity.getRelsrmlist().indexOf(c805w01Entity.getShncd()) < 0
                    && !(CommonConstants.KBN_KEIJO_URIAGE.equals(c805w01Entity.getKeijokbn())
                        || CommonConstants.KBN_KEIJO_CAMPAIGN
                            .equals(c805w01Entity.getKeijokbn()))) {
                    sonotamushohns = sonotamushohns.add(c805w01Entity.getAcptsuryo());
                }
                // 製品水売上金
                if (c805w01Entity.getRelshnmctgrycd().equals(c805w01Entity.getLctgrycd())) {
                    seihinmizuurikin =
                        seihinmizuurikin.add(new BigDecimal(c805w01Entity.getAcptkin()));
                }
                // その他売上金
                if (!c805w01Entity.getRelshnmctgrycd().equals(c805w01Entity.getLctgrycd())) {
                    sonotaurikin = sonotaurikin.add(new BigDecimal(c805w01Entity.getAcptkin()));
                }
                // サーバー交換台数
                if (c805w01Entity.getRelsrvlctgrycd().equals(c805w01Entity.getLctgrycd())
                    && c805w01Entity.getRelsrvmctgrycd().equals(c805w01Entity.getMctgrycd())
                    && (CommonConstants.KBN_ACPT_KOKAN.equals(c805w01Entity.getRelacptkbn())
                        || CommonConstants.KBN_ACPT_MAINT.equals(c805w01Entity.getRelacptkbn()))
                    && CommonConstants.KBN_ACPTSYS_KOKAN.equals(c805w01Entity.getRelacptkbnshs())) {
                    srvchngesuu = srvchngesuu.add(c805w01Entity.getAcptsuryo());
                }
            }

            // セット商品の場合、構成商品を子明細として展開
            if (!util.isFlagged(c805w01Entity.getTanpinmeisaiflg())) {
                // セット商品コード
                c805w01Entity.setStshncd(c805w01Entity.getShncd());

                // セット商品リスト取得（展開マスタ）
                S02M008TC0805W01Entity s02m008tc0805w01EntityIn = new S02M008TC0805W01Entity();
                s02m008tc0805w01EntityIn.setApkihoncd(c805w01Entity.getApkihoncd());
                s02m008tc0805w01EntityIn.setShopcd(c805w01Entity.getShopcd());
                s02m008tc0805w01EntityIn.setStshncd(c805w01Entity.getShncd());
                List<S02M008TC0805W01Entity> s02m008tc0805w01EntityOutList =
                    s02m008tDao.selectByC0805W01(s02m008tc0805w01EntityIn);

                // 受注消費税金額
                long reAcptshohitaxkin = 0;
                // 子明細Entity
                S04T002TC0805W01Entity childc805w01Entity = new S04T002TC0805W01Entity();
                // 構成品の明細情報を生成
                for (int m = 0; m < s02m008tc0805w01EntityOutList.size(); m++) {
                    // 商品マスタビュー(単独TBL検索）
                    S02V001TEntity koseiShnEntity =
                        getShnInfoFromView(s02m013tAcptDenpyoInfo.getApkihoncd(),
                            s02m013tAcptDenpyoInfo.getShopcd(),
                            s02m008tc0805w01EntityOutList.get(m).getKoseishncd()).get();
                    // 受注明細データEntityを子明細データEntityにコピー
                    childc805w01Entity = copyAcptDetailEntity(c805w01Entity);
                    // 受注伝票明細枝番号を設定
                    childc805w01Entity.setAcptdnpymeisaiedano((short) (m + 1));
                    // 委託相手受注伝票明細枝番号
                    if (c805w01Entity.getItakuaiteacptdnpymeisaino() != null) {
                        childc805w01Entity.setItakuaiteacptdnpymeisaiedano((short) (m + 1));
                    }
                    // 商品コード
                    childc805w01Entity
                        .setShncd(s02m008tc0805w01EntityOutList.get(m).getKoseishncd());
                    // 型式
                    childc805w01Entity.setKatasiki(null);
                    // セット商品コード
                    childc805w01Entity.setStshncd(c805w01Entity.getShncd());
                    // 入力明細フラグ
                    childc805w01Entity.setNyuryokumeisaiflg(CommonConstants.FLG_OFF);
                    // 単品明細フラグ
                    childc805w01Entity.setTanpinmeisaiflg(CommonConstants.FLG_ON);
                    // 大分類コード
                    childc805w01Entity.setLctgrycd(koseiShnEntity.getLctgrycd());
                    // 中分類コード
                    childc805w01Entity.setMctgrycd(koseiShnEntity.getMctgrycd());
                    // 小分類コード
                    childc805w01Entity.setSctgrycd(koseiShnEntity.getSctgrycd());
                    // 税区分
                    childc805w01Entity.setTaxkbn(koseiShnEntity.getTaxkbn());

                    // 製品水大分類コード
                    childc805w01Entity.setRelshnmctgrycd(acptrelationdto.getShnmctgrycd());
                    // サーバー大分類コード
                    childc805w01Entity.setRelsrvlctgrycd(acptrelationdto.getSrvlctgrycd());
                    // サーバー中分類コード
                    childc805w01Entity.setRelsrvmctgrycd(acptrelationdto.getSrvmctgrycd());
                    // レギュラーの商品コードリスト
                    childc805w01Entity.setRelrgralist(acptrelationdto.getRgralist());
                    // スリムの商品コードリスト
                    childc805w01Entity.setRelsrmlist(acptrelationdto.getSrmlist());
                    // 得意先区分
                    childc805w01Entity.setReltokuisakikbn(acptrelationdto.getTokuisakikbn());
                    // 受注区分
                    childc805w01Entity.setRelacptkbn(acptrelationdto.getAcptkbn());
                    // 受注区分詳細
                    childc805w01Entity.setRelacptkbnshs(acptrelationdto.getAcptkbnshs());
                    // 計上区分
                    childc805w01Entity.setRelkeijokbn(acptrelationdto.getKeijokbn());
                    // 商品大分類コード
                    childc805w01Entity.setRellctgrycd(childc805w01Entity.getLctgrycd());
                    // 商品中分類コード
                    childc805w01Entity.setRelmctgrycd(childc805w01Entity.getMctgrycd());
                    // 商品コード
                    childc805w01Entity.setRelshncd(childc805w01Entity.getShncd());
                    // 商品情報
                    childc805w01Entity.setS02v001TEntity(koseiShnEntity);
                    // 受注子商品税区分、納品子商品税区分の設定
                    String acptkoshnkbn = koseiShnEntity.getTaxkbn();
                    // String nohinkoshnkbn = koseiS02V001TOutList.get(0).getTaxkbn();
                    // 受注数量
                    childc805w01Entity.setAcptsuryo(c805w01Entity.getAcptsuryo()
                        .multiply(s02m008tc0805w01EntityOutList.get(m).getSuryo()));
                    // 納品数量
                    BigDecimal nohinsuryo =
                        (c805w01Entity.getNohinsuryo() == null) ? BigDecimal.ZERO
                            : c805w01Entity.getNohinsuryo();
                    childc805w01Entity.setNohinsuryo(
                        nohinsuryo.multiply(s02m008tc0805w01EntityOutList.get(m).getSuryo()));
                    // 標準価格
                    if (CommonConstants.KBN_DLVRY_MOCHIKAERI
                        .equals(s02m013tAcptDenpyoInfo.getDlvrykbn())) {
                        childc805w01Entity
                            .setHyojununt(getCurrentValue(koseiShnEntity.getGenzmochikaeriprice(),
                                koseiShnEntity.getShinmochikaeriprice(),
                                koseiShnEntity.getHyojunpricekirikaedd(),
                                s02m013tAcptDenpyoInfo.getDlvryytidd()));

                    } else {
                        childc805w01Entity
                            .setHyojununt(getCurrentValue(koseiShnEntity.getGenzhyojunprice(),
                                koseiShnEntity.getShinhyojunprice(),
                                koseiShnEntity.getHyojunpricekirikaedd(),
                                s02m013tAcptDenpyoInfo.getDlvryytidd()));
                    }

                    // 子明細データEntityの金額系項目の設定 d-5-8-1
                    if (CommonConstants.KBN_KEIJO_URIAGE.equals(c805w01Entity.getKeijokbn())
                        || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(c805w01Entity.getKeijokbn())) {
                        // 得意先別セット商品取得
                        S02M066TC0805W01Entity s02m066tc0805w01EntityIn =
                            new S02M066TC0805W01Entity();
                        s02m066tc0805w01EntityIn.setRenban(
                            String.valueOf(s02m008tc0805w01EntityOutList.get(m).getRenban()));
                        s02m066tc0805w01EntityIn
                            .setApkihoncd(s02m013tAcptDenpyoInfo.getApkihoncd());
                        s02m066tc0805w01EntityIn.setShopcd(s02m013tAcptDenpyoInfo.getShopcd());
                        s02m066tc0805w01EntityIn
                            .setStshncd(s02m008tc0805w01EntityOutList.get(m).getStshncd());
                        s02m066tc0805w01EntityIn
                            .setTokuisakicd(s02m013tAcptDenpyoInfo.getTokuisakicd());
                        s02m066tc0805w01EntityIn
                            .setTankabasedd(s02m013tAcptDenpyoInfo.getDlvryytidd());

                        List<S02M066TC0805W01Entity> s02m066tc0805w01EntityOutList =
                            s02m066tDao.selectByC0805W01(s02m066tc0805w01EntityIn);

                        // 受注単価取得
                        BigDecimal juchutanka = BigDecimal.ZERO;
                        if (s02m066tc0805w01EntityOutList.size() > 0
                            && s02m013tAcptDenpyoInfo.getDlvryytidd().compareTo(
                                s02m066tc0805w01EntityOutList.get(0).getTekiyostrtdd()) >= 0
                            && s02m066tc0805w01EntityOutList.get(0).getTekiyoshrydd()
                                .compareTo(s02m013tAcptDenpyoInfo.getDlvryytidd()) >= 0) {
                            if (CommonConstants.KBN_DLVRY_MOCHIKAERI
                                .equals(s02m013tAcptDenpyoInfo.getDlvrykbn())) {
                                juchutanka =
                                    s02m066tc0805w01EntityOutList.get(0).getMochikaeriprice();
                            } else {
                                juchutanka = s02m066tc0805w01EntityOutList.get(0).getDlvryprice();
                            }
                        } else {
                            if (CommonConstants.KBN_DLVRY_MOCHIKAERI
                                .equals(s02m013tAcptDenpyoInfo.getDlvrykbn())) {
                                juchutanka =
                                    s02m008tc0805w01EntityOutList.get(m).getMochikaeriprice();
                            } else {
                                juchutanka = s02m008tc0805w01EntityOutList.get(m).getDlvryprice();
                            }
                        }

                        // 受注単価消費税金額、受注消費税金額、受注金額の取得
                        String priceList[] = taxService.getPriceList(String.valueOf(juchutanka),
                            String.valueOf(childc805w01Entity.getAcptsuryo()), acptkoshnkbn,
                            s02m013tAcptDenpyoInfo.getTokuisakikbn(),
                            s02m013tAcptDenpyoInfo.getDlvryytidd(), s02m046tIn);

                        // 子商品の計上区分、および金額系項目を設定
                        // 計上区分
                        childc805w01Entity
                            .setKeijokbn(s02m008tc0805w01EntityOutList.get(m).getKeijokbn());
                        // 受注単価
                        childc805w01Entity.setAcptunt(juchutanka);
                        // 受注単価消費税金額
                        childc805w01Entity.setAcptuntshohitaxkin(Long.parseLong(priceList[0]));
                        if ("0".equals(shnEntity.getTaxsetteikbn())
                            && (CommonConstants.KBN_TAX_HIKAZEI
                                .equals(childc805w01Entity.getTaxkbn())
                                || CommonConstants.KBN_TAX_HUKAZEI
                                    .equals(childc805w01Entity.getTaxkbn()))) {
                            childc805w01Entity.setAcptuntshohitaxkin((long) 0);
                        }
                        // 受注消費税金額
                        childc805w01Entity.setAcptshohitaxkin(Long.parseLong(priceList[1]));
                        if ("0".equals(shnEntity.getTaxsetteikbn())
                            && (CommonConstants.KBN_TAX_HIKAZEI
                                .equals(childc805w01Entity.getTaxkbn())
                                || CommonConstants.KBN_TAX_HUKAZEI
                                    .equals(childc805w01Entity.getTaxkbn()))) {
                            childc805w01Entity.setAcptshohitaxkin((long) 0);
                        }
                        if (s02m013tAcptDenpyoInfo.getDlvryytidd()
                            .compareTo(s02m046tIn.getInvoicshikoymd()) >= 0) {
                            childc805w01Entity.setAcptshohitaxkin((long) 0);
                        }
                        // 受注金額
                        childc805w01Entity.setAcptkin(Long.parseLong(priceList[2]));
                        // 納品単価
                        childc805w01Entity.setNohinunt(BigDecimal.ZERO);
                        // 納品単価消費税金額
                        childc805w01Entity.setNohinuntshohitaxkin((long) 0);
                        // 納品消費税金額
                        childc805w01Entity.setNohinshohitaxkin((long) 0);
                        // 納品金額
                        childc805w01Entity.setNohinkin((long) 0);

                    } else {
                        // 計上区分
                        childc805w01Entity.setKeijokbn(c805w01Entity.getKeijokbn());
                        // 受注単価
                        childc805w01Entity.setAcptunt(BigDecimal.ZERO);
                        // 受注単価消費税金額
                        childc805w01Entity.setAcptuntshohitaxkin((long) 0);
                        // 受注消費税金額
                        childc805w01Entity.setAcptshohitaxkin((long) 0);
                        // 受注金額
                        childc805w01Entity.setAcptkin((long) 0);
                        // 納品単価
                        childc805w01Entity.setNohinunt(BigDecimal.ZERO);
                        // 納品単価消費税金額
                        childc805w01Entity.setNohinuntshohitaxkin((long) 0);
                        // 納品消費税金額
                        childc805w01Entity.setNohinshohitaxkin((long) 0);
                        // 納品金額
                        childc805w01Entity.setNohinkin((long) 0);
                    }

                    // 受注消費税金額
                    reAcptshohitaxkin = reAcptshohitaxkin + childc805w01Entity.getAcptshohitaxkin();
                    // 税区分
                    childc805w01Entity.setTaxkbn(koseiShnEntity.getTaxkbn());

                    // 本数、金額、サーバー交換台数の集計
                    if (CommonConstants.TANPINMEISAIFLG_1
                        .equals(childc805w01Entity.getTanpinmeisaiflg())
                        && CommonConstants.KBN_MEISAI_SHOHIN
                            .equals(childc805w01Entity.getMeisaikbn())) {

                        // 受注伝票集計Entity
                        // レギュラー有償本数
                        if (childc805w01Entity.getRelrgralist()
                            .indexOf(childc805w01Entity.getShncd()) >= 0
                            && (CommonConstants.KBN_KEIJO_URIAGE
                                .equals(childc805w01Entity.getKeijokbn())
                                || CommonConstants.KBN_KEIJO_CAMPAIGN
                                    .equals(childc805w01Entity.getKeijokbn()))) {
                            rgrayusyohns = rgrayusyohns.add(childc805w01Entity.getAcptsuryo());
                        }
                        // レギュラー無償本数
                        if (childc805w01Entity.getRelrgralist()
                            .indexOf(childc805w01Entity.getShncd()) >= 0
                            && !(CommonConstants.KBN_KEIJO_URIAGE
                                .equals(childc805w01Entity.getKeijokbn())
                                || CommonConstants.KBN_KEIJO_CAMPAIGN
                                    .equals(childc805w01Entity.getKeijokbn()))) {
                            rgramushohns = rgramushohns.add(childc805w01Entity.getAcptsuryo());
                        }
                        // スリム有償本数
                        if (childc805w01Entity.getRelsrmlist()
                            .indexOf(childc805w01Entity.getShncd()) >= 0
                            && (CommonConstants.KBN_KEIJO_URIAGE
                                .equals(childc805w01Entity.getKeijokbn())
                                || CommonConstants.KBN_KEIJO_CAMPAIGN
                                    .equals(childc805w01Entity.getKeijokbn()))) {
                            srmyusyohns = srmyusyohns.add(childc805w01Entity.getAcptsuryo());
                        }
                        // スリム無償本数
                        if (childc805w01Entity.getRelsrmlist()
                            .indexOf(childc805w01Entity.getShncd()) >= 0
                            && !(CommonConstants.KBN_KEIJO_URIAGE
                                .equals(childc805w01Entity.getKeijokbn())
                                || CommonConstants.KBN_KEIJO_CAMPAIGN
                                    .equals(childc805w01Entity.getKeijokbn()))) {
                            srmmushohns = srmmushohns.add(childc805w01Entity.getAcptsuryo());
                        }
                        // その他有償本数
                        if (childc805w01Entity.getRelshnmctgrycd()
                            .equals(childc805w01Entity.getLctgrycd())
                            && childc805w01Entity.getRelrgralist()
                                .indexOf(childc805w01Entity.getShncd()) < 0
                            && childc805w01Entity.getRelsrmlist()
                                .indexOf(childc805w01Entity.getShncd()) < 0
                            && (CommonConstants.KBN_KEIJO_URIAGE
                                .equals(childc805w01Entity.getKeijokbn())
                                || CommonConstants.KBN_KEIJO_CAMPAIGN
                                    .equals(childc805w01Entity.getKeijokbn()))) {
                            sonotayusyohns = sonotayusyohns.add(childc805w01Entity.getAcptsuryo());
                        }
                        // その他無償本数
                        if (childc805w01Entity.getRelshnmctgrycd()
                            .equals(childc805w01Entity.getLctgrycd())
                            && childc805w01Entity.getRelrgralist()
                                .indexOf(childc805w01Entity.getShncd()) < 0
                            && childc805w01Entity.getRelsrmlist()
                                .indexOf(childc805w01Entity.getShncd()) < 0
                            && !(CommonConstants.KBN_KEIJO_URIAGE
                                .equals(childc805w01Entity.getKeijokbn())
                                || CommonConstants.KBN_KEIJO_CAMPAIGN
                                    .equals(childc805w01Entity.getKeijokbn()))) {
                            sonotamushohns = sonotamushohns.add(childc805w01Entity.getAcptsuryo());
                        }
                        // 製品水売上金
                        if (childc805w01Entity.getRelshnmctgrycd()
                            .equals(childc805w01Entity.getLctgrycd())) {
                            seihinmizuurikin = seihinmizuurikin
                                .add(new BigDecimal(childc805w01Entity.getAcptkin()));
                        }
                        // その他売上金
                        if (!childc805w01Entity.getRelshnmctgrycd()
                            .equals(childc805w01Entity.getLctgrycd())) {
                            sonotaurikin =
                                sonotaurikin.add(new BigDecimal(childc805w01Entity.getAcptkin()));
                        }
                        // サーバー交換台数
                        if (childc805w01Entity.getRelsrvlctgrycd()
                            .equals(childc805w01Entity.getLctgrycd())
                            && childc805w01Entity.getRelsrvmctgrycd()
                                .equals(childc805w01Entity.getMctgrycd())
                            && (CommonConstants.KBN_ACPT_KOKAN
                                .equals(childc805w01Entity.getRelacptkbn())
                                || CommonConstants.KBN_ACPT_MAINT
                                    .equals(childc805w01Entity.getRelacptkbn()))
                            && CommonConstants.KBN_ACPTSYS_KOKAN
                                .equals(childc805w01Entity.getRelacptkbnshs())) {
                            srvchngesuu = srvchngesuu.add(childc805w01Entity.getAcptsuryo());

                        }
                    }
                    // 構成商品（子明細データEntity）を構成商品リスト（List<子明細データEntity>）に追加する
                    retTmpList.add(childc805w01Entity);

                } // 構成品 d-5

                if (s02m046tIn.getInvoicshikoymd()
                    .compareTo(s02m013tAcptDenpyoInfo.getDlvryytidd()) > 0) {
                    // 調整消費税金額
                    long ajustAcptshohitaxkin = saveAcptshohitaxkin - reAcptshohitaxkin;

                    if (ajustAcptshohitaxkin != (long) 0) {
                        // セット商品消費税調整明細生成
                        S04T002TC0805W01Entity ajustAcptshohitaxkinEntity =
                            createAjustAcptshohitaxData(s02m013tAcptDenpyoInfo, c805w01Entity,
                                childc805w01Entity, ajustAcptshohitaxkin);

                        retTmpList.add(ajustAcptshohitaxkinEntity);
                    }
                }
            } // セット商品展開(d)

            // 返却用にオブジェクトをコピー
            retList.add(copyAcptDetailEntity(c805w01Entity));

        }

        s02m013tAcptDenpyoInfo.setRgrayusyohns(rgrayusyohns);
        s02m013tAcptDenpyoInfo.setRgramushohns(rgramushohns);
        s02m013tAcptDenpyoInfo.setSrmyusyohns(srmyusyohns);
        s02m013tAcptDenpyoInfo.setSrmmushohns(srmmushohns);
        s02m013tAcptDenpyoInfo.setSonotayusyohns(sonotayusyohns);
        s02m013tAcptDenpyoInfo.setSonotamushohns(sonotamushohns);
        s02m013tAcptDenpyoInfo.setSeihinmizuurikin(seihinmizuurikin);
        s02m013tAcptDenpyoInfo.setSonotaurikin(sonotaurikin);
        s02m013tAcptDenpyoInfo.setSrvchngesuu(srvchngesuu);

        retList.addAll(retTmpList);

        // 受注配達明細リストから明細番号の最大値を取得
        int acptdnpymeisaino = 0;
        for (int i = 0; i < retList.size(); i++) {
            if (retList.get(i).getAcptdnpymeisaino() > acptdnpymeisaino) {
                acptdnpymeisaino = retList.get(i).getAcptdnpymeisaino();
            }
        }

        // 配布物の明細を作成
        int indxCounter = 0;
        for (S02M033TC0805W01Entity s02m033tEntity : s02m033tEntityOutList) {
            // 配布物は最大4件まで
            indxCounter++;
            if (indxCounter > 4) {
                break;
            }
            // 受注伝票明細番号をインクリメント
            acptdnpymeisaino++;
            // 受注明細を生成
            S04T002TC0805W01Entity s04t002tEntity = new S04T002TC0805W01Entity();
            // AP基本コード
            s04t002tEntity.setApkihoncd(s02m013tAcptDenpyoInfo.getApkihoncd());
            // ショップコード
            s04t002tEntity.setShopcd(s02m013tAcptDenpyoInfo.getShopcd());
            // 受注伝票番号
            s04t002tEntity.setAcptdnpyno("");
            // 枝番号
            s04t002tEntity.setEdano(s02m013tAcptDenpyoInfo.getEdano());
            // 受注伝票明細番号
            s04t002tEntity.setAcptdnpymeisaino(acptdnpymeisaino);
            // 受注伝票明細枝番号
            s04t002tEntity.setAcptdnpymeisaiedano((short) 0);
            // データ作成元区分
            s04t002tEntity.setDatsksmotokbn(s02m013tAcptDenpyoInfo.getDatsksmotokbn());
            // 配布物AP基本コード
            s04t002tEntity.setHaifubutsuapkihoncd(s02m033tEntity.getApkihoncd());
            // 配布物ショップコード
            s04t002tEntity.setHaifubutsushopcd(s02m033tEntity.getShopcd());
            // 配布物コード
            s04t002tEntity.setHaifubutsucd(s02m033tEntity.getHaifubutsucd());
            // 受注数量
            s04t002tEntity.setAcptsuryo(BigDecimal.ONE);
            // 委託相手AP基本コード
            s04t002tEntity.setItakuaiteapkihoncd(s02m013tAcptDenpyoInfo.getItakuaiteapkihoncd());
            // 委託相手ショップコード
            s04t002tEntity.setItakuaiteshopcd(s02m013tAcptDenpyoInfo.getItakuaiteshopcd());
            // 委託相手受注伝票番号
            s04t002tEntity.setItakuaiteacptdnpyno(s02m013tAcptDenpyoInfo.getItakuaiteacptdnpyno());
            // 明細区分
            s04t002tEntity.setMeisaikbn(CommonConstants.KBN_MEISAI_HAIHUBUTSU);
            // 有償区分
            s04t002tEntity.setYusyokbn(CommonConstants.KBN_YUSHO_NONE);

            retList.add(s04t002tEntity);
        }

        // List<受注明細データEntity>を以下の順番（ 受注配達明細テーブルのプライマリーキー順）にソート
        List<S04T002TC0805W01Entity> retSortList = retList.stream()
            .sorted(Comparator.comparing(S04T002TC0805W01Entity::getApkihoncd)
                .thenComparing(S04T002TC0805W01Entity::getShopcd)
                .thenComparing(S04T002TC0805W01Entity::getAcptdnpyno)
                .thenComparing(S04T002TC0805W01Entity::getEdano)
                .thenComparing(S04T002TC0805W01Entity::getAcptdnpymeisaino)
                .thenComparing(S04T002TC0805W01Entity::getAcptdnpymeisaiedano))
            .collect(Collectors.toList());

        return retSortList;

    }

    /**
     * セット商品消費税調整明細生成
     * 
     * @param s02m013tAcptDenpyoInfo 得意先配達情報データEntity
     * @param c805w01Entity 受注明細データEntity
     * @param childc805w01Entity 子明細データEntity
     * @param ajustAcptshohitaxkin 調整消費税金額
     * @return S04T002TC0805W01Entity
     */
    private S04T002TC0805W01Entity createAjustAcptshohitaxData(
        S02M013TC0805W01Entity s02m013tAcptDenpyoInfo, S04T002TC0805W01Entity c805w01Entity,
        S04T002TC0805W01Entity childc805w01Entity, long ajustAcptshohitaxkin) {

        // システム管理マスタ検索：単独TBL検索
        S02M006TEntityExample S02M006TExample = new S02M006TEntityExample();
        S02M006TExample.createCriteria().andApkihoncdEqualTo(CommonConstants.APCD_000)
            .andShopcdEqualTo(CommonConstants.SYS_SHOPCD_000)
            .andSetteinoEqualTo(CommonConstants.SYS_SETTEINO_166)
            .andRcrdkbnEqualTo(CommonConstants.KBN_RECORD_MEISAI)
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<S02M006TEntity> S02M006tOutList = s02m006tDao.selectByExample(S02M006TExample);

        // 調整消費税明細に設定するの大分類コード、中分類コード、小分類コード、税区分を取得
        // 商品マスタビュー(単独TBL検索）
        S02V001TEntity shnEntity = getShnInfoFromView(s02m013tAcptDenpyoInfo.getApkihoncd(),
            s02m013tAcptDenpyoInfo.getShopcd(), c805w01Entity.getShncd()).get();

        // 標準単価取得
        BigDecimal hyojununt = BigDecimal.ZERO;
        if (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(s02m013tAcptDenpyoInfo.getDlvrykbn())) {
            hyojununt = getCurrentValue(shnEntity.getGenzmochikaeriprice(),
                shnEntity.getShinmochikaeriprice(), shnEntity.getHyojunpricekirikaedd(),
                s02m013tAcptDenpyoInfo.getDlvryytidd());

        } else {
            hyojununt =
                getCurrentValue(shnEntity.getGenzhyojunprice(), shnEntity.getShinhyojunprice(),
                    shnEntity.getHyojunpricekirikaedd(), s02m013tAcptDenpyoInfo.getDlvryytidd());
        }

        S04T002TC0805W01Entity ajustAcptshohitaxkinEntity = copyAcptDetailEntity(c805w01Entity);

        // 受注伝票明細枝番号
        ajustAcptshohitaxkinEntity.setAcptdnpymeisaiedano(
            (short) (childc805w01Entity.getAcptdnpymeisaiedano() + ((short) 1)));
        // 委託相手受注伝票明細番号
        if (childc805w01Entity.getItakuaiteacptdnpymeisaino() != null) {
            ajustAcptshohitaxkinEntity.setItakuaiteacptdnpymeisaino(
                childc805w01Entity.getItakuaiteacptdnpymeisaino() + 1);
        }
        // 商品コード
        ajustAcptshohitaxkinEntity.setShncd(S02M006tOutList.get(0).getMojisetteiatai());
        // 型式
        ajustAcptshohitaxkinEntity.setKatasiki(null);
        // セット商品コード
        ajustAcptshohitaxkinEntity.setStshncd(c805w01Entity.getShncd());
        // 入力明細フラグ
        ajustAcptshohitaxkinEntity.setNyuryokumeisaiflg(CommonConstants.NYURYOKUMEISAIFLG_0);
        // 単品明細フラグ
        ajustAcptshohitaxkinEntity.setTanpinmeisaiflg(CommonConstants.TANPINMEISAIFLG_1);
        // 大分類コード
        ajustAcptshohitaxkinEntity.setLctgrycd(shnEntity.getLctgrycd());
        // 中分類コード
        ajustAcptshohitaxkinEntity.setMctgrycd(shnEntity.getMctgrycd());
        // 小分類コード
        ajustAcptshohitaxkinEntity.setSctgrycd(shnEntity.getSctgrycd());
        // 税区分
        ajustAcptshohitaxkinEntity.setTaxkbn(shnEntity.getTaxkbn());
        // 明細区分
        ajustAcptshohitaxkinEntity.setMeisaikbn(CommonConstants.KBN_MEISAI_SHOHIZEI);
        // 有償区分
        ajustAcptshohitaxkinEntity.setYusyokbn(CommonConstants.KBN_YUSHO_NONE);
        // 受注数量
        ajustAcptshohitaxkinEntity.setAcptsuryo(BigDecimal.ZERO);
        // 納品数量
        ajustAcptshohitaxkinEntity.setNohinsuryo(BigDecimal.ZERO);
        // 標準単価
        ajustAcptshohitaxkinEntity.setHyojununt(hyojununt);
        // 受注単価
        ajustAcptshohitaxkinEntity.setAcptunt(BigDecimal.ZERO);
        // 受注金額
        ajustAcptshohitaxkinEntity.setAcptkin((long) 0);
        // 受注単価消費税金額
        ajustAcptshohitaxkinEntity.setAcptuntshohitaxkin((long) 0);
        // 受注消費税金額
        ajustAcptshohitaxkinEntity.setAcptshohitaxkin(ajustAcptshohitaxkin);
        // 納品単価
        ajustAcptshohitaxkinEntity.setNohinunt(BigDecimal.ZERO);
        // 納品金額
        ajustAcptshohitaxkinEntity.setNohinkin((long) 0);
        // 納品単価消費税金額
        ajustAcptshohitaxkinEntity.setNohinuntshohitaxkin((long) 0);
        // 納品消費税金額
        ajustAcptshohitaxkinEntity.setNohinshohitaxkin((long) 0);
        // 委託会社受注単価
        ajustAcptshohitaxkinEntity.setItakucmpnyacptunt(BigDecimal.ZERO);
        // 委託会社受注単価消費税金額
        ajustAcptshohitaxkinEntity.setItakucmpnyacptuntshohitaxkin((long) 0);
        // 委託会社受注消費税金額
        ajustAcptshohitaxkinEntity.setItakucmpnyacptshohitaxkin((long) 0);

        return ajustAcptshohitaxkinEntity;

    }

    /**
     * エンティティ変換<br>
     * <br>
     * 
     * @param s04t002tEntityList 受注配達明細エンティティリスト
     * @param s02m044tEntityList 定期受注明細マスタエンティティリスト
     * @param s02m013tAcptDenpyoInfo 得意先受注伝票エンティティ
     * @param s02m046tIn ショップマスタEntity
     * @return
     */
    private void changeEntity(List<S04T002TC0805W01Entity> s04t002tEntityList,
        List<S02M044TC0805W01Entity> s02m044tEntityList,
        S02M013TC0805W01Entity s02m013tAcptDenpyoInfo, S02M046TEntity s02m046Entity) {

        // 引数で指定されるリストには受注予約明細が入ってる場合があるため
        int acptdnpymeisaino = s04t002tEntityList.size();
        for (S02M044TC0805W01Entity s02m044tEntity : s02m044tEntityList) {
            S04T002TC0805W01Entity s04t002tEntity = new S04T002TC0805W01Entity();
            acptdnpymeisaino++;

            // ＡＰ基本コード
            s04t002tEntity.setApkihoncd(s02m013tAcptDenpyoInfo.getApkihoncd());
            // ショップコード
            s04t002tEntity.setShopcd(s02m013tAcptDenpyoInfo.getShopcd());
            // 受注伝票番号（登録時に採番）
            s04t002tEntity.setAcptdnpyno("");
            // 枝番号
            s04t002tEntity.setEdano(s02m013tAcptDenpyoInfo.getEdano());
            // 受注伝票明細番号
            s04t002tEntity.setAcptdnpymeisaino(acptdnpymeisaino);
            // 受注伝票明細枝番号
            s04t002tEntity.setAcptdnpymeisaiedano((short) 0);
            // データ作成元区分
            s04t002tEntity.setDatsksmotokbn(s02m013tAcptDenpyoInfo.getDatsksmotokbn());

            // 商品ＡＰ基本コード
            s04t002tEntity.setShnapkihoncd(s02m044tEntity.getShnapkihoncd());
            // 商品コード
            s04t002tEntity.setShncd(s02m044tEntity.getShncd());
            // セット区分
            s04t002tEntity.setStkbn(s02m044tEntity.getStkbn());
            // 大分類コード
            s04t002tEntity.setLctgrycd(s02m044tEntity.getLctgrycd());
            // 中分類コード
            s04t002tEntity.setMctgrycd(s02m044tEntity.getMctgrycd());
            // 小分類コード
            s04t002tEntity.setSctgrycd(s02m044tEntity.getSctgrycd());
            // 商品名略称
            s04t002tEntity.setShnnmryakusho(s02m044tEntity.getShnnmryakusho());
            // 価格区分
            s04t002tEntity.setPricekbn(s02m044tEntity.getPricekbn());
            // 標準価格切替日;
            s04t002tEntity.setHyojunpricekirikaedd(s02m044tEntity.getHyojunpricekirikaedd());
            // 現標準価格;
            s04t002tEntity.setGenzhyojunprice(s02m044tEntity.getGenzhyojunprice());
            // 新標準価格;
            s04t002tEntity.setShinhyojunprice(s02m044tEntity.getShinhyojunprice());
            // 現持ち帰り価格;
            s04t002tEntity.setGenzmochikaeriprice(s02m044tEntity.getGenzmochikaeriprice());
            // 新持ち帰り価格;
            s04t002tEntity.setShinmochikaeriprice(s02m044tEntity.getShinmochikaeriprice());
            // オープン価格切替日;
            s04t002tEntity.setOpnpricekirikaedd(s02m044tEntity.getOpnpricekirikaedd());
            // 現オープン価格;
            s04t002tEntity.setGenzopnprice(s02m044tEntity.getGenzopnprice());
            // 新オープン価格;
            s04t002tEntity.setShinopnprice(s02m044tEntity.getShinopnprice());
            // 現オープン持ち帰り価格;
            s04t002tEntity.setGenzopnmochikaeriprice(s02m044tEntity.getGenzopnmochikaeriprice());
            // 新オープン持ち帰り価格;
            s04t002tEntity.setShinopnmochikaeriprice(s02m044tEntity.getShinopnmochikaeriprice());
            // 販促区分;
            s04t002tEntity.setHansokukbn(s02m044tEntity.getHansokukbn());
            // 販促開始日;
            s04t002tEntity.setHansokustrtdd(s02m044tEntity.getHansokustrtdd());
            // 販促終了日;
            s04t002tEntity.setHansokushrydd(s02m044tEntity.getHansokushrydd());
            // 販促配達価格;
            s04t002tEntity.setHansokudlvryprice(s02m044tEntity.getHansokudlvryprice());
            // 販促持ち帰り価格;
            s04t002tEntity.setHansokumochikaeriprice(s02m044tEntity.getHansokumochikaeriprice());
            // 販促優先区分;
            s04t002tEntity.setHansokuyusenkbn(s02m044tEntity.getHansokuyusenkbn());
            // 在庫単価;
            s04t002tEntity.setZaikount(s02m044tEntity.getZaikount());
            // 色
            s04t002tEntity.setIro(s02m044tEntity.getIro());
            // サイズ名称
            s04t002tEntity.setSz(s02m044tEntity.getIro());
            // 売上取扱単位名称
            s04t002tEntity.setUritrtskitani(s02m044tEntity.getUritrtskitani());
            // 売上入数
            s04t002tEntity.setUriirisu(s02m044tEntity.getUriirisu());
            // 売上単位名称
            s04t002tEntity.setUritani(s02m044tEntity.getUritani());
            // 税設定区分
            s04t002tEntity.setTaxsetteikbn(s02m044tEntity.getTaxsetteikbn());
            // 在庫アラーム数量
            s04t002tEntity.setZaikoaramusuryo(s02m044tEntity.getZaikoaramusuryo());
            // 取扱区分
            s04t002tEntity.setTrtskikbn(s02m044tEntity.getTrtskikbn());
            // 特別単価区分
            s04t002tEntity.setTokubetsuuntkbn(s02m044tEntity.getTokubetsuuntkbn());
            // 適用開始日
            s04t002tEntity.setTekiyostrtdd(s02m044tEntity.getTekiyostrtdd());
            // 適用終了日
            s04t002tEntity.setTekiyoshrydd(s02m044tEntity.getTekiyoshrydd());
            // 配達価格
            s04t002tEntity.setDlvryprice(s02m044tEntity.getDlvryprice());
            // 持ち帰り価格
            s04t002tEntity.setMochikaeriprice(s02m044tEntity.getMochikaeriprice());
            // 商品名称;
            s04t002tEntity.setShnnm(s02m044tEntity.getShnnm());
            // 標準価格;
            s04t002tEntity.setHyojunprice(s02m044tEntity.getHyojunprice());
            // 適用開始日;
            s04t002tEntity.setSet_tekiyostrtdd(s02m044tEntity.getSet_tekiyostrtdd());
            // 適用終了日;
            s04t002tEntity.setSet_tekiyoshrydd(s02m044tEntity.getSet_tekiyoshrydd());
            // 配達価格;
            s04t002tEntity.setSet_dlvryprice(s02m044tEntity.getSet_dlvryprice());
            // 持ち帰り価格;
            s04t002tEntity.setSet_mochikaeriprice(s02m044tEntity.getSet_mochikaeriprice());
            // 料金種別特別単価フラグ;
            s04t002tEntity.setRknkindtokubetsuuntflg(s02m044tEntity.getRknkindtokubetsuuntflg());
            // 計上区分
            if (CommonConstants.KBN_TOKUISAKI_KEIHI
                .equals(s02m013tAcptDenpyoInfo.getTokuisakikbn())) {
                // 得意先区分が経費の場合、計上区分は経費
                s04t002tEntity.setKeijokbn(CommonConstants.KBN_KEIJO_KEIHI);
            } else {
                // それ以外ならば定期受注明細マスタまたは特定定期受注明細マスタの計上区分を設定
                s04t002tEntity.setKeijokbn(s02m044tEntity.getKeijokbn());
            }
            // 型式
            s04t002tEntity.setKatasiki(s02m044tEntity.getKatasiki());
            // 明細備考
            s04t002tEntity.setMeisaibiko(s02m044tEntity.getMeisaibiko());
            // 受注数量
            s04t002tEntity.setAcptsuryo(new BigDecimal(s02m044tEntity.getAcptsuryo()));
            if (CommonConstants.FLG_ON.equals(s02m044tEntity.getTaxsetteikbn())) {
                // 商品税区分
                s04t002tEntity.setShntaxkbn(s02m044tEntity.getTaxkbn());
                // 税区分
                s04t002tEntity.setTaxkbn(s02m044tEntity.getTaxkbn());
            } else {
                // 商品税区分
                s04t002tEntity.setShntaxkbn(null);
                // 税区分
                s04t002tEntity.setTaxkbn(null);
            }
            // 明細区分
            s04t002tEntity.setMeisaikbn(CommonConstants.KBN_MEISAI_SHOHIN);
            // 入力明細フラグ
            s04t002tEntity.setNyuryokumeisaiflg(CommonConstants.FLG_ON);
            if (CommonConstants.FLG_ON.equals(s02m044tEntity.getStkbn())) {
                // 単品明細フラグ
                s04t002tEntity.setTanpinmeisaiflg(CommonConstants.FLG_OFF);
                // セット商品コード
                s04t002tEntity.setStshncd(s02m044tEntity.getShncd());
            } else {
                // 単品明細フラグ
                s04t002tEntity.setTanpinmeisaiflg(CommonConstants.FLG_ON);
            }
            // 有効区分
            s04t002tEntity.setYukokbn(CommonConstants.YUKOKBN_1);

            // 受注単価決定時に用いるショップ価格設定
            if (CommonConstants.KBN_PRICE_HYOJUN.equals(s02m044tEntity.getPricekbn())) {
                s02m044tEntity.setShoppricekirikaedd(s02m044tEntity.getHyojunpricekirikaedd());
                s02m044tEntity.setGenzshopprice(s02m044tEntity.getGenzhyojunprice());
                s02m044tEntity.setShinshopprice(s02m044tEntity.getShinhyojunprice());
                s02m044tEntity.setGenzshopmochikaeriprice(s02m044tEntity.getGenzmochikaeriprice());
                s02m044tEntity.setShinshopmochikaeriprice(s02m044tEntity.getShinmochikaeriprice());
            } else {
                s02m044tEntity.setShoppricekirikaedd(s02m044tEntity.getOpnpricekirikaedd());
                s02m044tEntity.setGenzshopprice(s02m044tEntity.getGenzopnprice());
                s02m044tEntity.setShinshopprice(s02m044tEntity.getShinopnprice());
                s02m044tEntity
                    .setGenzshopmochikaeriprice(s02m044tEntity.getGenzopnmochikaeriprice());
                s02m044tEntity
                    .setShinshopmochikaeriprice(s02m044tEntity.getShinopnmochikaeriprice());
            }
            // 販促価格
            if (!CommonConstants.KBN_SAKUSEIMOTO_JUCHUYOYAKU
                .equals(s02m013tAcptDenpyoInfo.getDatsksmotokbn())
                && CommonConstants.FLG_ON.equals(s02m044tEntity.getHansokukbn())) {
                // データ作成元区分が受注予約以外かつ販促区分がONの場合は販促価格設定
                s02m044tEntity.setHansokuprice(s02m044tEntity.getHansokudlvryprice());
            }
            // 得意先別特別価格
            if (CommonConstants.FLG_ON.equals(s02m044tEntity.getRknkindtokubetsuuntflg())) {
                // 料金種別特別単価フラグがONの場合に設定
                if (CommonConstants.FLG_ON.equals(s02m044tEntity.getStkbn())) {
                    // セット商品の場合
                    s02m044tEntity.setTtpricestrtdd(s02m044tEntity.getSet_tekiyostrtdd());
                    s02m044tEntity.setTtpriceshrydd(s02m044tEntity.getSet_tekiyoshrydd());
                    s02m044tEntity.setTtprice(s02m044tEntity.getSet_dlvryprice());
                    s02m044tEntity.setTtmochikaeriprice(s02m044tEntity.getSet_mochikaeriprice());
                } else {
                    // セット商品以外の場合
                    s02m044tEntity.setTtpricestrtdd(s02m044tEntity.getTekiyostrtdd());
                    s02m044tEntity.setTtpriceshrydd(s02m044tEntity.getTekiyoshrydd());
                    s02m044tEntity.setTtprice(s02m044tEntity.getDlvryprice());
                    s02m044tEntity.setTtmochikaeriprice(s02m044tEntity.getMochikaeriprice());
                }
                // セット商品の特別価格情報は別に保持する
                s02m044tEntity.setSetTekiyostrtdd(s02m044tEntity.getSet_tekiyostrtdd());
                s02m044tEntity.setSetTekiyoshrydd(s02m044tEntity.getSet_tekiyoshrydd());
                s02m044tEntity.setSetDlvryprice(s02m044tEntity.getSet_dlvryprice());
                s02m044tEntity.setSetMochikaeriprice(s02m044tEntity.getSet_mochikaeriprice());
            }
            // 販促対応しない場合は関連項目をクリア
            if (!CommonConstants.FLG_ON.equals(s02m044tEntity.getHansokutaiou())) {
                s02m044tEntity.setHansokustrtdd("");
                s02m044tEntity.setHansokushrydd("");
                s02m044tEntity.setHansokuprice("");
                s02m044tEntity.setHansokumochikaeriprice("");
                s02m044tEntity.setHansokuyusenkbn("");
            }

            // 標準単価取得
            BigDecimal hyojununt = BigDecimal.ZERO;
            if (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(s02m013tAcptDenpyoInfo.getDlvrykbn())) {
                hyojununt = getCurrentValue(new BigDecimal(s02m044tEntity.getGenzmochikaeriprice()),
                    new BigDecimal(s02m044tEntity.getShinmochikaeriprice()),
                    s02m044tEntity.getHyojunpricekirikaedd(),
                    s02m013tAcptDenpyoInfo.getDlvryytidd());

            } else {
                hyojununt = getCurrentValue(new BigDecimal(s02m044tEntity.getGenzhyojunprice()),
                    new BigDecimal(s02m044tEntity.getShinhyojunprice()),
                    s02m044tEntity.getHyojunpricekirikaedd(),
                    s02m013tAcptDenpyoInfo.getDlvryytidd());
            }
            s04t002tEntity.setHyojununt(hyojununt);

            // 受注単価設定
            getAcptunt(s02m044tEntity, s02m013tAcptDenpyoInfo, s04t002tEntity);

            // 商品税区分
            s04t002tEntity.setShntaxkbn(s02m044tEntity.getTaxkbn());
            // 受注単価、消費税、金額計算
            String taxkbn = s04t002tEntity.getShntaxkbn();
            if (CommonConstants.KBN_KEIJO_KEIHI.equals(s04t002tEntity.getKeijokbn())) {
                taxkbn = CommonConstants.KBN_TAX_HIKAZEI;
            }
            String priceList[] =
                taxService.getPriceList(String.valueOf(s04t002tEntity.getAcptunt()),
                    s02m044tEntity.getAcptsuryo(), taxkbn, s02m013tAcptDenpyoInfo.getTokuisakikbn(),
                    s02m013tAcptDenpyoInfo.getDlvryytidd(), s02m046Entity);
            // 受注単価消費税金額
            s04t002tEntity.setAcptuntshohitaxkin(Long.parseLong(priceList[0]));
            // 受注消費税金額
            s04t002tEntity.setAcptshohitaxkin(Long.parseLong(priceList[1]));
            // 受注金額
            s04t002tEntity.setAcptkin(Long.parseLong(priceList[2]));

            s04t002tEntityList.add(s04t002tEntity);
        }

    }

    /**
     * 受注単価取得
     * 
     * @param s02m044tEntity 受注伝票明細リスト取得Entity
     * @param s02m013tAcptDenpyoInfo 得意先配達情報データEntity
     * @param s04t002tEntity 受注明細リスEntityト
     * @return
     */
    private void getAcptunt(S02M044TC0805W01Entity s02m044tEntity,
        S02M013TC0805W01Entity s02m013tAcptDenpyoInfo, S04T002TC0805W01Entity s04t002tEntity) {

        String dlvryKbn = s02m013tAcptDenpyoInfo.getDlvrykbn();
        String dlvryYtiDd = s02m013tAcptDenpyoInfo.getDlvryytidd();

        // 計上区分＝「経費」の時は受注単価をショップ商品マスタの在庫単価にする
        if (CommonConstants.KBN_KEIJO_KEIHI.equals(s02m044tEntity.getKeijokbn())) {
            if (Strings.isEmpty(s02m044tEntity.getZaikount())) {
                s04t002tEntity.setAcptunt(BigDecimal.ZERO);
            } else {
                s04t002tEntity.setAcptunt(new BigDecimal(s02m044tEntity.getZaikount()));
            }
            // 計上区分＝「売上」「キャンペーン」の時は得意先別商品別、販促単価の適用判定を行う
        } else if (CommonConstants.KBN_KEIJO_URIAGE.equals(s02m044tEntity.getKeijokbn())
            || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(s02m044tEntity.getKeijokbn())) {
            String hansokuunt = getCampaignTanka(s02m044tEntity, dlvryKbn, dlvryYtiDd);
            String tokuisakiunt = getTokuisakiTanka(s02m044tEntity, dlvryKbn, dlvryYtiDd);
            String tokuisakisetunt = getTokuisakiSetTanka(s02m044tEntity, dlvryKbn, dlvryYtiDd);

            // 得意先別商品別単価の適用期間内の場合は受注単価を得意先別商品別単価にする
            if (!Strings.isEmpty(tokuisakiunt)) {
                s04t002tEntity.setKeijokbn(CommonConstants.KBN_KEIJO_URIAGE);
                s04t002tEntity.setAcptunt(new BigDecimal(tokuisakiunt));

                // 得意先別セット商品別単価の適用期間内の場合は受注単価を得意先別セット商品別単価にする
            } else if (!Strings.isEmpty(tokuisakisetunt)) {
                s04t002tEntity.setKeijokbn(CommonConstants.KBN_KEIJO_URIAGE);
                s04t002tEntity.setAcptunt(new BigDecimal(tokuisakisetunt));

            } else {
                // 計上区分がキャンペーン（お気に入りマスタからの登録）の場合は単価のみ設定
                if (CommonConstants.KBN_KEIJO_CAMPAIGN.equals(s02m044tEntity.getKeijokbn())) {
                    // 販促期間内は販促価格をセット
                    if (!Strings.isEmpty(hansokuunt)) {
                        s04t002tEntity.setAcptunt(new BigDecimal(hansokuunt));
                    }
                    // 販促期間外は0円をセット
                    else {
                        s04t002tEntity.setAcptunt(BigDecimal.ZERO);
                    }
                }
                // 行挿入や行追加などの計上区分：売上の場合、販促設定適用範囲内ならキャンペーンに設定する
                else {
                    // 販促期間内＋販促設定：対象内
                    if (hansokuunt != null) {
                        // 販促優先区分＝"する"の場合は計上区分をキャンペーン、販促価格に設定
                        if (CommonConstants.FLG_ON.equals(s02m044tEntity.getHansokuyusenkbn())) {
                            s04t002tEntity.setKeijokbn(CommonConstants.KBN_KEIJO_CAMPAIGN);
                            s04t002tEntity.setAcptunt(new BigDecimal(hansokuunt));
                        }
                        // 販促優先区分＝"しない"の場合、売上価格のみ設定
                        else {
                            // 配達区分＝「持ち帰り」の場合
                            if (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(dlvryKbn)) {
                                s04t002tEntity.setAcptunt(new BigDecimal(
                                    getCurrentValue(s02m044tEntity.getGenzshopmochikaeriprice(),
                                        s02m044tEntity.getShinshopmochikaeriprice(),
                                        s02m044tEntity.getShoppricekirikaedd(), dlvryYtiDd)));

                                // 配達区分＝「配達」の場合
                            } else {
                                s04t002tEntity.setAcptunt(new BigDecimal(
                                    getCurrentValue(s02m044tEntity.getGenzshopprice(),
                                        s02m044tEntity.getShinshopprice(),
                                        s02m044tEntity.getShoppricekirikaedd(), dlvryYtiDd)));
                            }
                        }
                    }
                    // 販促期間外＋販促設定：対象外 売上価格を設定
                    else {
                        // 配達区分＝「持ち帰り」の場合
                        if (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(dlvryKbn)) {
                            s04t002tEntity.setAcptunt(new BigDecimal(
                                getCurrentValue(s02m044tEntity.getGenzshopmochikaeriprice(),
                                    s02m044tEntity.getShinshopmochikaeriprice(),
                                    s02m044tEntity.getShoppricekirikaedd(), dlvryYtiDd)));

                            // 配達区分＝「配達」の場合
                        } else {
                            s04t002tEntity.setAcptunt(
                                new BigDecimal(getCurrentValue(s02m044tEntity.getGenzshopprice(),
                                    s02m044tEntity.getShinshopprice(),
                                    s02m044tEntity.getShoppricekirikaedd(), dlvryYtiDd)));
                        }
                    }
                }
            }

            // 計上区分＝上記以外の時は０にする
        } else {
            s04t002tEntity.setAcptunt(BigDecimal.ZERO);
        }

    }

    /**
     * 販促単価取得
     * 
     * @param s02m044tEntity 受注伝票明細リスト取得Entity
     * @param dlvryKbn 配達区分
     * @param dlvryYtiDd 配達予定日
     * @return キャンペーン単価
     */
    public String getCampaignTanka(S02M044TC0805W01Entity s02m044tEntity, String dlvryKbn,
        String dlvryYtiDd) {

        // 販促設定：対象外の場合はキャンペーン対象外とする
        if (CommonConstants.FLG_OFF.equals(s02m044tEntity.getHansokukbn())) {
            return null;
        }

        // 適用期間の判定
        if (checkDatePeriod(dlvryYtiDd, s02m044tEntity.getHansokustrtdd(),
            s02m044tEntity.getHansokushrydd())) {
            return (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(dlvryKbn))
                ? s02m044tEntity.getHansokumochikaeriprice()
                : s02m044tEntity.getHansokuprice();

        } else {
            return null;
        }
    }

    /**
     * 得意先単価取得
     * 
     * @param s02m044tEntity 受注伝票明細リスト取得Entity
     * @param dlvryKbn 配達区分
     * @param dlvryYtiDd 配達予定日
     * @return キャンペーン単価
     */
    public String getTokuisakiTanka(S02M044TC0805W01Entity s02m044tEntity, String dlvryKbn,
        String dlvryYtiDd) {

        // 料金種別特別設定：対象外の場合はキャンペーン対象外とする
        if (CommonConstants.FLG_OFF.equals(s02m044tEntity.getRknkindtokubetsuuntflg())) {
            return null;
        }

        // 適用期間の判定
        if (checkDatePeriod(dlvryYtiDd, s02m044tEntity.getTtpricestrtdd(),
            s02m044tEntity.getTtpriceshrydd())) {
            return (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(dlvryKbn))
                ? s02m044tEntity.getTtmochikaeriprice()
                : s02m044tEntity.getTtprice();

        } else {
            return null;
        }
    }

    /**
     * 得意先セット単価取得
     * 
     * @param s02m044tEntity 受注伝票明細リスト取得Entity
     * @param dlvryKbn 配達区分
     * @param dlvryYtiDd 配達予定日
     * @return キャンペーン単価
     */
    public String getTokuisakiSetTanka(S02M044TC0805W01Entity s02m044tEntity, String dlvryKbn,
        String dlvryYtiDd) {

        // 料金種別特別設定：対象外の場合はキャンペーン対象外とする
        if (CommonConstants.FLG_OFF.equals(s02m044tEntity.getRknkindtokubetsuuntflg())) {
            return null;
        }

        // 適用期間の判定
        if (checkDatePeriod(dlvryYtiDd, s02m044tEntity.getSetTekiyostrtdd(),
            s02m044tEntity.getSetTekiyoshrydd())) {
            return (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(dlvryKbn))
                ? s02m044tEntity.getSetMochikaeriprice()
                : s02m044tEntity.getSetDlvryprice();

        } else {
            return null;
        }
    }

    /**
     * 日付の期間チェック 開始日～終了日にチェック対象日が存在するか確認します
     * 
     * @param targetDate チェック対象日
     * @param startDate 開始日
     * @param endDate 終了日
     * @return 結果
     */
    private boolean checkDatePeriod(String targetDate, String startDate, String endDate) {
        if (Strings.isEmpty(targetDate) || Strings.isEmpty(startDate) || Strings.isEmpty(endDate)) {
            return false;
        }
        return util.isDateBetween(targetDate, startDate, endDate);
    }

    /**
     * 受注配達明細Entityのコピーを生成する
     * 
     * @param source 元となる受注配達明細Entity
     * @return コピーされた受注配達明細Entity
     */
    private S04T002TC0805W01Entity copyAcptDetailEntity(S04T002TC0805W01Entity source) {
        S04T002TC0805W01Entity copied = new S04T002TC0805W01Entity();
        BeanUtils.copyProperties(source, copied);
        return copied;
    }

    /**
     * 定期受注マスタについて次のサイクルの次回予定日を取得する
     * 
     * @param rglrAcptMstEntity 定期受注マスタEntity
     * @return 次サイクルの次回予定日
     */
    private String getCycledJikaiytidd(RglrAcptMstEntity rglrAcptMstEntity) {
        return getDayofweekSpecifiedJikaiytidd(rglrAcptMstEntity.getJikaiytidd());
    }

    /**
     * 指定区分：日付指定の次サイクルの次回予定日を取得する
     * 
     * @param baseDate 現在の日付
     * @return 次サイクルの次回予定日
     */
    private String getDayofweekSpecifiedJikaiytidd(String baseDate) {
        return util.getDateString(util.getLocalDate(baseDate).plusDays(28L));
    }

    /**
     * 特定定期受注マスタについて次のサイクルの次回予定日を取得する
     * 
     * @param multiRglrAcptMstEntity 特定定期受注マスタEntity
     * @return 次サイクルの次回予定日
     */
    private String getCycledJikaiytidd(MultiRglrAcptMstEntity multiRglrAcptMstEntity) {
        log.debug("\t\t特定定期受注マスタのサイクル算出");
        log.debug("\t\t\t指定区分 : {}", multiRglrAcptMstEntity.getShiteikbn());
        log.debug("\t\t\t次回予定日 : {}", multiRglrAcptMstEntity.getJikaiytidd());
        String result = null;
        switch (multiRglrAcptMstEntity.getShiteikbn()) {
            case CommonConstants.SHITEI_KBN_0:
                // 指定区分：曜日指定
                log.debug("\t\t\t指定区分：曜日指定の場合（指定曜日 : {}）", multiRglrAcptMstEntity.getShiteiyobi());
                result = getDayofweekSpecifiedJikaiytidd(multiRglrAcptMstEntity.getJikaiytidd());
                break;
            case CommonConstants.SHITEI_KBN_1:
                // 指定区分：日付指定
                log.debug("\t\t\t指定区分：日付指定の場合（指定日 : {}）", multiRglrAcptMstEntity.getShiteidd());
                // 次回予定日をLocalDateに変換
                LocalDate jikaiytidd = util.getLocalDate(multiRglrAcptMstEntity.getJikaiytidd());
                log.debug("\t\t\t次回予定日のLocalDatgeを取得 : {}", jikaiytidd);
                // 次回予定日に対して一月シフトした日付を取得する（存在しない日付の場合、月末日に丸められる）
                LocalDate dateOfNextMonth = jikaiytidd.plusMonths(1L);
                log.debug("\t\t\t次回予定日の翌月（plusMonths）の年月日を取得 : {}", dateOfNextMonth);
                // 指定日が「31（月末）」の場合
                if (Objects.equals(UtilityService.SPECIFIED_LAST_DAY_OF_MONTH,
                    multiRglrAcptMstEntity.getShiteidd())) {
                    // 月末の場合、月末日に調整する
                    // ※2月など、日数が31より小さい日付の場合、1ヶ月シフトすると月末にならないため
                    result = util
                        .getDateString(dateOfNextMonth.with(TemporalAdjusters.lastDayOfMonth()));
                    break;
                }
                // 指定日が「31（月末）」以外の場合
                int jikaiytiddDayOfMonth = jikaiytidd.getDayOfMonth();
                int dateOfNextMonthDayOfMonth = dateOfNextMonth.getDayOfMonth();
                log.debug("\t\t\t次回予定日の日を取得 : {}", jikaiytiddDayOfMonth);
                log.debug("\t\t\t次回予定日の翌月の日を取得 : {}", dateOfNextMonthDayOfMonth);
                // 月末指定以外の場合、2月のように29日や30日が存在しない場合（シフト時点で日が一致しなくなる）
                if (jikaiytiddDayOfMonth != dateOfNextMonthDayOfMonth) {
                    // 翌々月にシフトする（2ヶ月連続してシフト結果の日が一致しなくなることはない）
                    dateOfNextMonth = jikaiytidd.plusMonths(2L);
                }
                result = util.getDateString(dateOfNextMonth);
                break;
            case CommonConstants.SHITEI_KBN_2:
                // 指定区分：特定指定（第何週目の何曜日という指定方法）
                log.debug("\t\t\t指定区分：特定指定の場合（指定曜日 : {} / 指定週 : {}）",
                    multiRglrAcptMstEntity.getShiteiyobi(), multiRglrAcptMstEntity.getShiteishu());
                // 基準日を次回予定日の翌日とする
                LocalDate baseDate =
                    util.getLocalDate(multiRglrAcptMstEntity.getJikaiytidd()).plusDays(1L);
                // 基準日の曜日（日曜始まり）を算出
                int baseDayOfWeek = util.getDayOfWeekValue(baseDate);
                // 指定曜日と次回予定日の曜日の差分を算出（負数になる場合は1週間の日数を加算）
                int diffDayOfWeek =
                    util.getIntValue(multiRglrAcptMstEntity.getShiteiyobi()) - baseDayOfWeek;
                diffDayOfWeek = diffDayOfWeek < 0 ? diffDayOfWeek + UtilityService.DAY_OF_WEEK_COUNT
                    : diffDayOfWeek;
                // 暫定の次回予定日を算出
                LocalDate temporalJikaiytidd = baseDate.plusDays(diffDayOfWeek);
                // 暫定次回予定日が何週目かを取得、指定週と比較し一致するまで7日ずつシフトする
                int specifiedWeekNum = util.getIntValue(multiRglrAcptMstEntity.getShiteishu());
                while (specifiedWeekNum != temporalJikaiytidd
                    .get(ChronoField.ALIGNED_WEEK_OF_MONTH)) {
                    temporalJikaiytidd =
                        temporalJikaiytidd.plusDays(UtilityService.DAY_OF_WEEK_COUNT);
                }
                result = util.getDateString(temporalJikaiytidd);
                break;
            case CommonConstants.SHITEI_KBN_3:
                // 指定区分：範囲指定
                log.debug("\t\t\t指定区分：範囲指定の場合（範囲日数 : {}）", multiRglrAcptMstEntity.getHaninissu());
                // 最大次回予定日を基準日とし、範囲日数に連番を乗じた日数を加算し、次サイクルの次回予定日とする
                LocalDate maxJikaiytidd =
                    util.getLocalDate(multiRglrAcptMstEntity.getMaxJikaiytidd());
                LocalDate nextJikaiytidd = maxJikaiytidd.plusDays(
                    multiRglrAcptMstEntity.getHaninissu() * multiRglrAcptMstEntity.getSeq());
                result = util.getDateString(nextJikaiytidd);
        }
        log.debug("\t\t\t次サイクルの次回予定日 : {}", result);
        return result;
    }

    /**
     * 配布物リストを取得する
     * 
     * @param tokuisakiRglrAcptdnpyEntity 得意先定期受注伝票Entity
     * @return 配布物Entityリスト
     */
    private List<S02M033TC0805W01Entity> getHaifubutsuList(
        S02M013TC0805W01Entity tokuisakiRglrAcptdnpyEntity) {
        S02M033TC0805W01Entity haifuCondition = new S02M033TC0805W01Entity();
        haifuCondition.setApkihoncd(tokuisakiRglrAcptdnpyEntity.getApkihoncd());
        haifuCondition.setShopcd(tokuisakiRglrAcptdnpyEntity.getShopcd());
        haifuCondition.setTokuisakicd(tokuisakiRglrAcptdnpyEntity.getTokuisakicd());
        haifuCondition.setDlvryytidd(tokuisakiRglrAcptdnpyEntity.getDlvryytidd());
        return s02m033tDao.selectByC0805W01(haifuCondition);
    }

    /**
     * 得意先関係DTOを取得する
     * 
     * @param regularAcptTarget 定期受注対象得意先
     * @return 得意先関係DTO
     */
    private C0805W01TokuisakiRelationDto getTokuisakiRelation(
        S02M013TC0805W01Entity regularAcptTarget) {
        S02M013TEntity tokuisakiMstEntity = getTokuisakiMst(regularAcptTarget.getApkihoncd(),
            regularAcptTarget.getShopcd(), regularAcptTarget.getTokuisakicd());
        S02M050TEntityKey jissekiKey = new S02M050TEntityKey();
        jissekiKey.setApkihoncd(regularAcptTarget.getApkihoncd());
        jissekiKey.setShopcd(regularAcptTarget.getShopcd());
        jissekiKey.setTokuisakicd(regularAcptTarget.getTokuisakicd());
        S02M050TEntity jissekiEntity = s02m050tDao.selectByPrimaryKeyForLock(jissekiKey);
        C0805W01TokuisakiRelationDto tokuisakiRelationDto = new C0805W01TokuisakiRelationDto();
        tokuisakiRelationDto.setTokuisaki(tokuisakiMstEntity);
        tokuisakiRelationDto.setJisseki(jissekiEntity);
        return tokuisakiRelationDto;
    }

    /**
     * 得意先マスタから有効な得意先情報を取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @return 得意先Entity
     */
    private S02M013TEntity getTokuisakiMst(String apkihoncd, String shopcd, String tokuisakicd) {
        S02M013TEntityKey tokuisakiKey = new S02M013TEntityKey();
        tokuisakiKey.setApkihoncd(apkihoncd);
        tokuisakiKey.setShopcd(shopcd);
        tokuisakiKey.setTokuisakicd(tokuisakicd);
        return Optional.ofNullable(s02m013tDao.selectByPrimaryKey(tokuisakiKey))
            .filter(entity -> util.isAvailable(entity.getYukokbn())).get();
    }

    /**
     * 得意先が委託元であるかを返す
     * 
     * @param tokuisakiEntity 得意先Entity
     * @return 得意先がNA委託または配達委託の場合true
     */
    private boolean isItakumotoTokuisaki(S02M013TEntity tokuisakiEntity) {
        return CommonConstants.KBN_TOKUISAKI_KOKUAKU.equals(tokuisakiEntity.getTokuisakikbn())
            && (CommonConstants.KBN_JUTAKUDLVTOKUI_NAKOKYAKU
                .equals(tokuisakiEntity.getJutakudlvrycstkbn())
                || CommonConstants.KBN_JUTAKUDLVTOKUI_ITAKUKOKYAKU
                    .equals(tokuisakiEntity.getJutakudlvrycstkbn()));
    }

    /**
     * 得意先が卸対象かを返す
     * 
     * @param tokuisakiEntity 得意先Entity
     * @return 卸の場合true
     */
    private boolean isOroshi(S02M013TC0805W01Entity tokuisakiEntity) {
        String tokuisakikbn = tokuisakiEntity.getTokuisakikbn();
        return CommonConstants.KBN_TOKUISAKI_SHOP.equals(tokuisakikbn)
            || CommonConstants.KBN_TOKUISAKI_AP.equals(tokuisakikbn)
            || CommonConstants.KBN_TOKUISAKI_ACI.equals(tokuisakikbn)
            || CommonConstants.KBN_TOKUISAKI_NAJUTAKU.equals(tokuisakikbn)
            || CommonConstants.KBN_TOKUISAKI_DLVRYJUTAKU.equals(tokuisakikbn);
    }

    /**
     * 得意先に委託相手得意先が設定されているかを返す
     * 
     * @param tokuisakiEntity 得意先Entity
     * @return 委託相手の情報が設定されていればtrue
     */
    private boolean isSetItakuaite(S02M013TEntity tokuisakiEntity) {
        return !StringUtils.isEmpty(tokuisakiEntity.getItakuaiteapkihoncd())
            && !StringUtils.isEmpty(tokuisakiEntity.getItakuaiteshopcd())
            && !StringUtils.isEmpty(tokuisakiEntity.getItakuaitetokuisakicd());
    }

    /**
     * 指定された得意先が得意先マスタに存在するかを返す
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @return 存在する場合はtrue
     */
    private boolean isExistsTokuisaki(String apkihoncd, String shopcd, String tokuisakicd) {
        S02M013TEntityExample ex = new S02M013TEntityExample();
        ex.createCriteria().andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd)
            .andTokuisakicdEqualTo(tokuisakicd).andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        return s02m013tDao.countByExample(ex) != 0;
    }

    /**
     * 2つの得意先が連携しているかを判定する
     * 
     * @param tokuisaki 得意先
     * @param itakuaite 委託相手
     * @return 連携していればtrue
     */
    private boolean isLinkedTokuisaki(S02M013TEntity tokuisaki, S02M013TEntity itakuaite) {
        if (Objects.isNull(tokuisaki) || Objects.isNull(itakuaite)) {
            return false;
        }
        return Objects.equals(tokuisaki.getApkihoncd(), itakuaite.getItakuaiteapkihoncd())
            && Objects.equals(tokuisaki.getShopcd(), itakuaite.getItakuaiteshopcd())
            && Objects.equals(tokuisaki.getTokuisakicd(), itakuaite.getItakuaitetokuisakicd());
    }

    /**
     * 得意先に配達管轄が設定されているかを返す
     * 
     * @param tokuisakiEntity 得意先Entity
     * @return 設定されている場合はtrue
     */
    private boolean isSetDlvryKankatsu(S02M013TEntity tokuisakiEntity) {
        return !StringUtils.isEmpty(tokuisakiEntity.getDlvrykankatsuapkihoncd())
            && !StringUtils.isEmpty(tokuisakiEntity.getDlvrykankatsushopcd())
            && !StringUtils.isEmpty(tokuisakiEntity.getDlvrykankatsukyotencd());
    }

    /**
     * 商品マスタビューから商品情報を取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param shncd 商品コード
     * @return 商品情報
     */
    private Optional<S02V001TEntity> getShnInfoFromView(String apkihoncd, String shopcd,
        String shncd) {
        S02V001TEntityKey key = new S02V001TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setShncd(shncd);
        return Optional.ofNullable(s02v001tDao.selectByPrimaryKey(key));
    }

    /**
     * 指定された商品がショップ商品マスタに存在するかを返す
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param shncd 商品コード
     * @return 存在する場合true
     */
    private boolean isExistsShopShnMst(String apkihoncd, String shopcd, String shncd) {
        S02M043TEntityExample example = new S02M043TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd)
            .andShncdEqualTo(shncd).andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        return s02m043tDao.countByExample(example) > 0;
    }

    /**
     * 委託相手に関するチェックを行う
     * 
     * @param tokuisakiMstEntity 得意先マスタEntity
     * @param rglrAcptDetailMstEntityList 定期受注明細マスタEntity
     * @param tokuisakiRglrAcptdnpyEntity 得意先定期受注Entity
     * @return チェック結果（エラー無しの場合は空）
     */
    private Optional<C0805W01TeikiAcptResultDto> checkItakusaki(S02M013TEntity tokuisakiMstEntity,
        List<S02M044TC0805W01Entity> rglrAcptDetailMstEntityList,
        S02M013TC0805W01Entity tokuisakiRglrAcptdnpyEntity) {
        // 委託得意先コード存在チェック
        if (!isSetItakuaite(tokuisakiMstEntity)) {
            return Optional.of(
                C0805W01TeikiAcptResultDto.errorNothingItakuaite(tokuisakiRglrAcptdnpyEntity, msg));
        }

        // 委託得意先マスタ存在チェック
        if (!isExistsTokuisaki(tokuisakiMstEntity.getItakuaiteapkihoncd(),
            tokuisakiMstEntity.getItakuaiteshopcd(),
            tokuisakiMstEntity.getItakuaitetokuisakicd())) {
            return Optional.of(C0805W01TeikiAcptResultDto
                .errorNotExistsItakuaite(tokuisakiRglrAcptdnpyEntity, msg));
        }
        S02M013TEntity itakuaiteEntity = getTokuisakiMst(tokuisakiMstEntity.getItakuaiteapkihoncd(),
            tokuisakiMstEntity.getItakuaiteshopcd(), tokuisakiMstEntity.getItakuaitetokuisakicd());
        // 委託相手コード連携チェック（委託）
        if (!isLinkedTokuisaki(tokuisakiMstEntity, itakuaiteEntity)) {
            return Optional
                .of(C0805W01TeikiAcptResultDto.errorCooperation(tokuisakiRglrAcptdnpyEntity, msg));
        }

        // 明細に対してチェックを行う
        for (S02M044TC0805W01Entity s02m044tEntity : rglrAcptDetailMstEntityList) {
            // 委託AP／ショップ独自商品チェック
            if (!Strings.isEmpty(s02m044tEntity.getShncd())) {
                String shnzokusei =
                    // 商品マスタビューから商品情報を取得
                    getShnInfoFromView(s02m044tEntity.getApkihoncd(), s02m044tEntity.getShopcd(),
                        s02m044tEntity.getShncd())
                            // 商品属性を取得
                            .map(S02V001TEntity::getShnzokusei)
                            // 該当データがない場合は本部商品として扱う
                            .orElse(CommonConstants.KBN_SHNZOKUSEI_HONBU);
                if (CommonConstants.KBN_SHNZOKUSEI_AP.equals(shnzokusei)) {
                    // AP商品が含まれている場合はエラーとする
                    return Optional.of(C0805W01TeikiAcptResultDto
                        .errorContainsApOriginalShn(tokuisakiRglrAcptdnpyEntity, msg));
                }

                // 委託ショップ商品マスタ存在チェック
                if (!isExistsShopShnMst(tokuisakiMstEntity.getItakuaiteapkihoncd(),
                    tokuisakiMstEntity.getItakuaiteshopcd(), s02m044tEntity.getShncd())) {
                    return Optional.of(C0805W01TeikiAcptResultDto
                        .errorNotExistsItakuaiteShn(tokuisakiRglrAcptdnpyEntity, msg));
                }
            }
        }

        // 委託配達拠点コード存在チェック
        if (!isSetDlvryKankatsu(itakuaiteEntity)) {
            return Optional.of(C0805W01TeikiAcptResultDto
                .errorNothingItakuaiteKyoten(tokuisakiRglrAcptdnpyEntity, msg));
        }
        return Optional.empty();
    }

    /**
     * 回収予定本数のデフォルト値を取得する.
     * 
     * @param azukarihns 預かり本数
     * @return 回収予定本数
     */
    private String getDefaultKybtlhns(String azukarihns, String tokuisakijotai) {
        // 得意先状態が「仮会員解約（4）」または「退会（6）」の時を退会済とみなす
        boolean isWithdrawing =
            CommonConstants.KBN_TOKUISAKIJOTAI_KARIKAIYAKU.equals(tokuisakijotai)
                || CommonConstants.KBN_TOKUISAKIJOTAI_TAIKAI.equals(tokuisakijotai);
        if (!NumberUtils.isNumber(azukarihns, true)) {
            // 預かり本数が数値として有効でない場合は0を返す
            return BigDecimal.ZERO.toString();
        }
        BigDecimal azukari = new BigDecimal(azukarihns);
        // 預かり本数が負数なら0を返す
        // 上記以外で退会済ならば預かり本数を返す
        // 退会済でない場合は預かり本数 - 1を返す
        // 算出した回収予定ボトル本数が最大値（9999）を超える場合は最大値を返す
        return azukari.signum() <= 0 ? BigDecimal.ZERO.toString()
            : getString(isWithdrawing ? Long.valueOf(azukarihns) : Long.valueOf(azukarihns) - 1,
                KYBTLHNS_MAX);
    }

    /**
     * 計上区分を考慮した受注配達明細の税区分を取得する
     * 
     * @param detail 受注配達明細
     * @return 計上区分
     */
    private String getDetailShntaxkbn(S04T002TC0805W01Entity detail) {
        return CommonConstants.KBN_KEIJO_KEIHI.equals(detail.getKeijokbn())
            ? CommonConstants.KBN_TAX_HIKAZEI
            : detail.getShntaxkbn();
    }

}
