package jp.co.aquaclara.accis.web.domain.service.c0801.w02;

import static jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02ShopCalculateOptionDto.REDUCED_TAX_RATE_STRDD;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.C0804B01Dao;
import jp.co.aquaclara.accis.common.domain.repository.dao.C0809B11Dao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M008TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T002TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S06T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.C0804B01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.C0809B11Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M008TSetShohinEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T005TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T006TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T007TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S06T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S06T001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S13T004TEntity;
import jp.co.aquaclara.accis.common.exception.BusinessException;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02AcptDlvryDetailDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02AcptDlvryDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02AcptDlvrySummaryDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02ProcessingContextDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02ProcessingErrorDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02ProcessingResultDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02SettingDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02SettingDto.PRCSKBN;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02SettingOptionDto.PROCESS_TYPE;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02ShnDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02ShopMasterDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02TokuisakiDlvryDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02TokuisakiJissekiDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02TokuisakiMasterDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.inventory.C0801W02DlvryareaOrderAlarmKeyDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.inventory.C0801W02DlvrykyotenOrderAlarmKeyDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.inventory.C0801W02InventoryReservationAlarmKeyDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.inventory.C0801W02TnshaOrderAlarmKeyDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.shnprice.C0801W02SwitchableShnPriceDto;
import jp.co.aquaclara.accis.web.domain.service.business.SaibanService;
import jp.co.aquaclara.accis.web.domain.service.c0800.JuchuYykKshnService;
import jp.co.aquaclara.accis.web.domain.service.c0800.UtilityService;
import jp.co.aquaclara.accis.web.domain.service.c0801.w02.alarm.C0801W02DlvryareaOrderAlarmService;
import jp.co.aquaclara.accis.web.domain.service.c0801.w02.alarm.C0801W02DlvrykyotenOrderAlarmService;
import jp.co.aquaclara.accis.web.domain.service.c0801.w02.alarm.C0801W02InventoryReservationAlarmService;
import jp.co.aquaclara.accis.web.domain.service.c0801.w02.alarm.C0801W02TnshaOrderAlarmService;
import jp.co.aquaclara.accis.web.domain.service.x9901.StringUtil;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * C0802W01_受注登録（受注登録）_受注配達登録サービス
 */
@Service
public class C0801W02AcptDlvryRegistrationService {

    private static final String ZERO = BigDecimal.ZERO.stripTrailingZeros().toPlainString();

    /** ユーティリティ */
    @Autowired
    private UtilityService util;

    /** 受注予約更新部品 */
    @Autowired
    private JuchuYykKshnService juchuYykKshnService;

    /** 配布物サービス */
    @Autowired
    private C0801W02HaifubutsuService haifubutsuService;

    /** 設引予約サービス */
    @Autowired
    private C0801W02SagyoyoyakuService sagyoyoyakuService;

    /** 得意先サービス */
    @Autowired
    private C0801W02TokuisakiService tokuisakiService;

    /** 商品サービス */
    @Autowired
    private C0801W02ShnService shnService;

    /** 受注配達検索サービス */
    @Autowired
    private C0801W02AcptDlvryFindService finder;

    /** 受注配達バリデーションサービス */
    @Autowired
    private C0801W02AcptDlvryValidationService validator;

    /** 受注配達明細検索サービス */
    @Autowired
    private C0801W02AcptDlvryDetailFindService detailFinder;

    /** 受注配達明細価格サービス */
    @Autowired
    private C0801W02AcptDlvryDetailPriceService priceService;

    /** 在庫引当アラームサービス */
    @Autowired
    private C0801W02InventoryReservationAlarmService inventoryAlarmService;

    /** 配達拠点受注アラームサービス */
    @Autowired
    private C0801W02DlvrykyotenOrderAlarmService kyotenAlarmService;

    /** 担当者受注アラームサービス */
    @Autowired
    private C0801W02TnshaOrderAlarmService tnshaAlarmService;

    /** 配達エリア受注アラームサービス */
    @Autowired
    private C0801W02DlvryareaOrderAlarmService areaAlarmService;

    /** 採番サービス */
    @Autowired
    private SaibanService numberingService;

    /** 受注配達テーブルDAO */
    @Autowired
    private S04T001TDao s04t001tDao;

    /** 受注配達明細テーブルDAO */
    @Autowired
    private S04T002TDao s04t002tDao;

    /** 売上テーブルDAO */
    @Autowired
    private S06T001TDao s06t001tDao;

    /** 本部受注データ連携DAO */
    @Autowired
    private C0804B01Dao c0804b01Dao;

    /** 展開マスタDAO */
    @Autowired
    private S02M008TDao s02m008tDao;

    /** 配達関連処理（配達データ作成）DAO */
    @Autowired
    private C0809B11Dao c0809b11Dao;

    /** トランザクションテンプレート */
    @Autowired
    private TransactionTemplate tx;

    /**
     * 更新前チェック
     * 
     * @param settingDto 受注登録設定DTO
     * @return ワーニングメッセージリスト
     */
    public List<String> checkOnly(C0801W02SettingDto settingDto) {
        // チェックのみを行うための設定
        settingDto.getOption().setOnlyCheck(true);
        // トランザクション処理
        return tx.execute(status -> {
            // トランザクションは無条件にロールバックする
            status.setRollbackOnly();
            // 処理実行
            C0801W02ProcessingResultDto result = process(settingDto)
                // 結果確定
                .verify();
            // ワーニングメッセージリストを返す
            return result.getWarningMessages();
        });
    }

    /**
     * 受注配達登録処理<br>
     * <br>
     * <ul>
     * <li>更新前チェック</li>
     * <li>受注登録</li>
     * <li>受注予約更新</li>
     * <li>本部受注データ連携</li>
     * <li>配達関連処理</li>
     * </ul>
     * 
     * @param settingDto 受注登録設定DTO
     * @return 処理結果
     */
    @Transactional(rollbackFor = BusinessException.class)
    public C0801W02ProcessingResultDto regist(C0801W02SettingDto settingDto) {
        // ワーニングメッセージは無視する
        settingDto.getOption().setIgnoredWarning(true);
        // 処理実行
        C0801W02ProcessingResultDto result = process(settingDto)
            // 受註締め処理はこのタイミングで行う（更新前チェックの時はやらない）
            .processIfNoError(settingDto,
                dto -> closeOrder(dto.getAcptDlvryDto(), dto.getContext()),
                // 受注締め処理を行う場合（更新後に印刷ボタンクリック時）のみ
                dto -> dto.getOption().isClosing())
            // 結果確定
            .verify();
        // ワーニングメッセージのクリア
        if (result.hasWarning()) {
            // ワーニングメッセージはクリア
            result.getWarningMessages().clear();
        }
        // 更新結果を処理結果に設定
        result.setAcptDlvryDto(settingDto.getAcptDlvryDto());
        result.setDetailList(settingDto.getDetailList());

        // 戻り値を返却
        return result;
    }

    /**
     * チェック処理および更新処理の実行.<br>
     * 
     * @param settingDto 受注登録設定DTO
     * @return 処理結果
     */
    protected C0801W02ProcessingResultDto process(C0801W02SettingDto settingDto) {
        // 受注登録設定DTOのフラグを変更
        settingDto.getOption().setRegist(true);
        // 処理結果を初期化
        C0801W02ProcessingResultDto result = C0801W02ProcessingResultDto.builder().build()
            // 受注配達明細に対する単項目チェック
            .processIfNoError(settingDto,
                dto -> validator.validateSingleItemOfDetail(dto.getDetailList(),
                    dto.getAcptDlvryDto().getDlvryytidd()))
            // 得意先状態チェック
            .processIfNoError(settingDto, dto -> tokuisakiService.validateTokuisaki(dto))
            // マイアクア連携チェック
            .processIfNoError(settingDto,
                dto -> validator.checkMyAquaLinkage(dto.getAcptDlvryDto()))
            // 同時実行チェック（受注締めを行う場合）
            .processIfNoError(settingDto, dto -> validator.checkConcurrency(dto.getAcptDlvryDto()),
                dto -> dto.getOption().isClosing())
            // 処理区分チェック（受注締めを行う場合）
            .processIfNoError(settingDto,
                dto -> validator.checkPrcskbnForClosingOrder(dto.getPrcskbn()),
                dto -> dto.getOption().isClosing())
            // 他拠点の場合かつ委託元の場合 … 委託相手得意先関連チェック
            .processIfNoError(settingDto, dto -> tokuisakiService.validateItakuaite(dto),
                dto -> dto.getAcptDlvryDto().isTakyoten() && dto.getTokuisakiInfo().isItakumoto())
            // 委受託の場合 … 得意先マスタ委受託系項目設定チェック
            .processIfNoError(settingDto, dto -> tokuisakiService.validateIjutaku(dto),
                dto -> dto.getTokuisakiInfo().isItakusaki() || dto.getAcptDlvryDto().isTakyoten())
            // 他拠点の場合かつ委託元の場合 … 委託元商品関連チェック
            .processIfNoError(settingDto, dto -> validator.validateItakumotoDetail(dto),
                dto -> dto.getAcptDlvryDto().isTakyoten() && dto.getTokuisakiInfo().isItakumoto())
            // 委託先かつ委託相手受注配達伝票番号が設定されている場合 … 委託先受注配達明細関連チェック
            .processIfNoError(settingDto, dto -> validator.validateItakusakiDetail(dto),
                dto -> dto.getTokuisakiInfo().isItakusaki()
                    && !StringUtil.isEmpty(dto.getAcptDlvryDto().getItakuaiteacptdnpyno()))
            .processIfNoError(settingDto, dto -> {
                // 単価再設定及び再計算
                detailFinder.reCalculate(dto);
                // 例外発生以外にエラーとならないので処理結果はデフォルト返却
                return C0801W02ProcessingResultDto.builder().build();
            })
            // 更新処理実行
            .processIfNoError(settingDto, dto -> executeUpdate(dto));
        // 戻り値を返却
        return result;
    }

    /**
     * 受注登録処理を実行する
     * 
     * @param settingDto 受注登録設定DTO
     * @return 処理結果
     */
    protected C0801W02ProcessingResultDto executeUpdate(C0801W02SettingDto settingDto) {
        C0801W02AcptDlvryDto acptDlvryDto = settingDto.getAcptDlvryDto();
        C0801W02TokuisakiMasterDto tokuisakiDto = settingDto.getTokuisakiInfo();
        // 更新用プロパティ設定 … 委託元の場合
        if (acptDlvryDto.isItakumoto()) {
            if (acptDlvryDto.isJikyoten()) {
                // 自拠点ならば得意先の情報を伝票の配達管轄とする
                acptDlvryDto.setDlvrykankatsuapkihoncd(tokuisakiDto.getApkihoncd());
                acptDlvryDto.setDlvrykankatsushopcd(tokuisakiDto.getShopcd());
                // 配達拠点や配達エリアは委託元で自拠点の場合画面入力値を使う（新規のときしか変更できない）
            } else {
                // 他拠点ならば得意先の配達管轄を伝票の配達管轄とし、拠点とエリアも設定する
                acptDlvryDto.setDlvrykankatsuapkihoncd(tokuisakiDto.getDlvrykankatsuapkihoncd());
                acptDlvryDto.setDlvrykankatsushopcd(tokuisakiDto.getDlvrykankatsushopcd());
                acptDlvryDto.setDlvrykyotencd(tokuisakiDto.getDlvrykankatsukyotencd());
                acptDlvryDto.setDlvryareacd(tokuisakiDto.getDlvryareacd());
            }
        }
        // 処理区分が新規以外の場合、委託相手の受注配達DTOを取得
        Optional<C0801W02AcptDlvryDto> itakuaiteAcptDlvryDto =
            finder.getItakuaiteAcptDlvryDto(acptDlvryDto, settingDto.getItakuaiteInfo())
                .filter(dto -> settingDto.getPrcskbn() != PRCSKBN.INSERT);
        // 処理結果を初期化
        C0801W02ProcessingResultDto result = C0801W02ProcessingResultDto.builder().build();
        // 処理区分に応じた登録/更新処理
        if (settingDto.getPrcskbn() == PRCSKBN.CANCEL) {
            result
                // キャンセル処理実行
                .processIfNoError(settingDto, dto -> processCancel(dto, Collections.emptyList()))
                // 更新時、他拠点かつキャンセル可能ならば、委託相手受注配達伝票のキャンセル
                .processIfNoError(settingDto,
                    dto -> linkHeadquartersOrderData(dto.getAcptDlvryDto(), LINKAGE_MODE.CANCEL),
                    dto -> !dto.getOption().isOnlyCheck() && dto.getAcptDlvryDto().isTakyoten()
                        && itakuaiteAcptDlvryDto.isPresent()
                        && itakuaiteAcptDlvryDto.get().isCancelable());
        } else if (settingDto.getPrcskbn() == PRCSKBN.DELETE) {
            result
                // 受注伝票の削除処理実行
                .processIfNoError(settingDto, dto -> processCancel(dto, Collections.emptyList()))
                // 更新時、他拠点かつキャンセル可能ならば、委託相手受注配達伝票の削除
                .processIfNoError(settingDto,
                    dto -> linkHeadquartersOrderData(dto.getAcptDlvryDto(), LINKAGE_MODE.DELETE),
                    dto -> !dto.getOption().isOnlyCheck() && dto.getAcptDlvryDto().isTakyoten()
                        && itakuaiteAcptDlvryDto.isPresent()
                        && itakuaiteAcptDlvryDto.get().isCancelable());
        } else {
            // 受注伝票の登録・更新処理実行
            result.processIfNoError(settingDto, dto -> processUpdate(dto));
        }
        // ACCIS2023-7609 【作業予約機能改善】受注登録画面改修
        result.processIfNoError(settingDto, dto -> sagyoyoyakuService.execute(dto),
            dto -> !dto.getOption().isOnlyCheck());
        // 処理結果を返却
        return result;
    }

    /**
     * 受注登録/更新処理
     * 
     * @param settingDto 受注登録設定DTO
     * @return 処理結果
     */
    protected C0801W02ProcessingResultDto processUpdate(C0801W02SettingDto settingDto) {
        C0801W02ProcessingResultDto result = C0801W02ProcessingResultDto.builder().build();
        C0801W02ProcessingContextDto context = settingDto.getContext();
        C0801W02AcptDlvryDto acptDlvryDto = settingDto.getAcptDlvryDto();
        C0801W02ShopMasterDto shopDto = settingDto.getShopInfo();
        PROCESS_TYPE processType = settingDto.getOption().getProcessType();
        // 更新用プロパティ設定
        // 新規または予約更新の場合は伝票ステータス設定
        if (processType == PROCESS_TYPE.INSERT || processType == PROCESS_TYPE.RESERVE_INSERT) {
            acptDlvryDto.setDnpysts(CommonConstants.DNPYSTS_02);
        }
        // 在庫管理区分(ショップマスタ.在庫管理区分が0以外ならON、0ならOFF）
        acptDlvryDto.setZaikokanrikbn(
            CommonConstants.FLG_OFF.equals(shopDto.getZaikokanrikbn()) ? CommonConstants.FLG_OFF
                : CommonConstants.FLG_ON);
        // 税計算区分（受注日時点の値をショップマスタから取得して設定）
        acptDlvryDto.setTaxkeisankbn(shopDto.getTaxkeisankbnuri(context.getOperationDate()));
        // 受注予約更新する受注伝票明細番号リストを作成
        List<String> reservedAcptDnpyNoList =
            createReservedDetailNumberList(settingDto.getDetailList());
        // 明細展開・集計処理
        List<C0801W02AcptDlvryDetailDto> expandedDetailList = expandDetail(settingDto);
        // 展開した受注配達明細DTOリストは受注登録設定DTOに設定しておく
        settingDto.setDetailList(expandedDetailList);
        // 更新対象となるデータの行ロックを取得
        Optional<S04T001TEntity> acptDlvryEntity = finder.getAcptDlvryEntity(
            acptDlvryDto.getApkihoncd(), acptDlvryDto.getShopcd(), acptDlvryDto.getAcptdnpyno(),
            acptDlvryDto.getEdano(), !settingDto.getOption().isOnlyCheck());
        // 更新時は受注ステータス取得および設定
        acptDlvryEntity.filter(entity -> processType == PROCESS_TYPE.UPDATE)
            .ifPresent(entity -> acptDlvryDto.setAcptsts(entity.getAcptsts()));

        // 更新用受注アラームキー作成し、ロックを取得（更新前チェック時のみチェックを行うようにキーを生成）
        Map<String, C0801W02InventoryReservationAlarmKeyDto> updateInvKeyMap =
            inventoryAlarmService.createKeyMap(context, acptDlvryDto, expandedDetailList, false,
                settingDto.getOption().isOnlyCheck());
        List<C0801W02InventoryReservationAlarmKeyDto> updateInvKeyList =
            util.mapValuesToList(updateInvKeyMap);
        if (!settingDto.getOption().isOnlyCheck()) {
            inventoryAlarmService.lockTable(updateInvKeyList);
        }
        Map<String, C0801W02DlvrykyotenOrderAlarmKeyDto> updateKyotenKeyMap =
            kyotenAlarmService.createKeyMap(context, acptDlvryDto, expandedDetailList, false,
                settingDto.getOption().isOnlyCheck());
        List<C0801W02DlvrykyotenOrderAlarmKeyDto> updateKyotenKeyList =
            util.mapValuesToList(updateKyotenKeyMap);
        if (!settingDto.getOption().isOnlyCheck()) {
            kyotenAlarmService.lockTable(updateKyotenKeyList);
        }
        Map<String, C0801W02TnshaOrderAlarmKeyDto> updateTnshaKeyMap =
            tnshaAlarmService.createKeyMap(context, acptDlvryDto, expandedDetailList, false,
                settingDto.getOption().isOnlyCheck());
        List<C0801W02TnshaOrderAlarmKeyDto> updateTnshaKeyList =
            util.mapValuesToList(updateTnshaKeyMap);
        if (!settingDto.getOption().isOnlyCheck()) {
            tnshaAlarmService.lockTable(updateTnshaKeyList);
        }
        Map<String, C0801W02DlvryareaOrderAlarmKeyDto> updateAreaKeyMap =
            areaAlarmService.createKeyMap(context, acptDlvryDto, expandedDetailList, false,
                settingDto.getOption().isOnlyCheck());
        List<C0801W02DlvryareaOrderAlarmKeyDto> updateAreaKeyList =
            util.mapValuesToList(updateAreaKeyMap);
        if (!settingDto.getOption().isOnlyCheck()) {
            areaAlarmService.lockTable(updateAreaKeyList);
        }

        result
            // 受注キャンセル（更新または予約登録の場合）
            .processIfNoError(settingDto, dto -> processCancel(dto, reservedAcptDnpyNoList),
                dto -> processType == PROCESS_TYPE.UPDATE
                    || processType == PROCESS_TYPE.RESERVE_INSERT)
            // 受注伝票チェック
            .processIfNoError(settingDto, dto -> validator.validateAcptDnpy(acptDlvryDto))
            // 得意先チェック更新
            .processIfNoError(settingDto, dto -> updateTokuisaki(dto, false, true))
            // 受注明細チェック
            .processIfNoError(settingDto,
                dto -> validator.validateDetail(acptDlvryDto, expandedDetailList,
                    dto.getShopInfo()))
            // 受注伝票日時チェック
            .processIfNoError(settingDto,
                dto -> validator.validateDateTimeProperties(acptDlvryDto,
                    dto.getContext().getOperationDate()))
            // 配布物関連チェック
            .processIfNoError(settingDto, dto -> haifubutsuService.validate(acptDlvryDto));

        // 在庫チェック更新
        if (acptDlvryDto.isJikyoten()) {
            // 更新前チェック時はチェックのみ行い、更新時は更新のみ（KeyのisCheckedはfalseになってる）を行う
            if (settingDto.getOption().isOnlyCheck()) {
                // 処理区分が新規以外の場合はキャンセル分の受注アラームキーを生成する（新規のときは空でよい）
                C0801W02AcptDlvryDto acptDlvryDtoFromDb = settingDto.getAcptDlvryDto(true);
                List<C0801W02AcptDlvryDetailDto> detailListFromDb = settingDto.getDetailList(true);
                Map<String, C0801W02InventoryReservationAlarmKeyDto> cancelInvKeyMap =
                    processType == PROCESS_TYPE.INSERT || processType == PROCESS_TYPE.RESERVE_INSERT
                        ? Map.of()
                        : inventoryAlarmService.createKeyMap(context, acptDlvryDtoFromDb,
                            detailListFromDb, true, false);
                Map<String, C0801W02DlvrykyotenOrderAlarmKeyDto> cancelKyotenKeyMap =
                    processType == PROCESS_TYPE.INSERT || processType == PROCESS_TYPE.RESERVE_INSERT
                        ? Map.of()
                        : kyotenAlarmService.createKeyMap(context, acptDlvryDtoFromDb,
                            detailListFromDb, true, false);
                Map<String, C0801W02TnshaOrderAlarmKeyDto> cancelTnshaKeyMap =
                    processType == PROCESS_TYPE.INSERT || processType == PROCESS_TYPE.RESERVE_INSERT
                        ? Map.of()
                        : tnshaAlarmService.createKeyMap(context, acptDlvryDtoFromDb,
                            detailListFromDb, true, false);
                Map<String, C0801W02DlvryareaOrderAlarmKeyDto> cancelAreaKeyMap =
                    processType == PROCESS_TYPE.INSERT || processType == PROCESS_TYPE.RESERVE_INSERT
                        ? Map.of()
                        : areaAlarmService.createKeyMap(context, acptDlvryDtoFromDb,
                            detailListFromDb, true, false);
                // 更新前チェック時は受注アラームのキャンセルは行われてないので、キャンセル分の数量を反映した上でチェックを実行
                result
                    // 在庫引当
                    .processIfNoError(settingDto,
                        dto -> inventoryAlarmService.checkOnly(inventoryAlarmService
                            .reflectCalcellation(updateInvKeyMap, cancelInvKeyMap)))
                    // 配達拠点受注アラーム
                    .processIfNoError(settingDto,
                        dto -> kyotenAlarmService.checkOnly(kyotenAlarmService
                            .reflectCalcellation(updateKyotenKeyMap, cancelKyotenKeyMap)))
                    // 担当者受注アラーム
                    .processIfNoError(settingDto,
                        dto -> tnshaAlarmService.checkOnly(tnshaAlarmService
                            .reflectCalcellation(updateTnshaKeyMap, cancelTnshaKeyMap)))
                    // 配達エリア受注アラーム
                    .processIfNoError(settingDto, dto -> areaAlarmService.checkOnly(
                        areaAlarmService.reflectCalcellation(updateAreaKeyMap, cancelAreaKeyMap)));
            } else {
                result
                    // 在庫引当
                    .processIfNoError(settingDto,
                        dto -> inventoryAlarmService.checkAndUpdate(updateInvKeyList))
                    // 配達拠点受注アラーム
                    .processIfNoError(settingDto,
                        dto -> kyotenAlarmService.checkAndUpdate(updateKyotenKeyList))
                    // 担当者受注アラーム
                    .processIfNoError(settingDto,
                        dto -> tnshaAlarmService.checkAndUpdate(updateTnshaKeyList))
                    // 配達エリア受注アラーム
                    .processIfNoError(settingDto,
                        dto -> areaAlarmService.checkAndUpdate(updateAreaKeyList));
            }
        } else {
            // 他拠点の場合（チェックのみ）
            result
                // 委託相手在庫引当アラームチェック
                .processIfNoError(updateInvKeyList, keyDtoList -> {
                    C0801W02ProcessingResultDto r = C0801W02ProcessingResultDto.builder().build();
                    keyDtoList.forEach(keyDto -> {
                        // 配達管轄の在庫引当アラームを生成
                        C0801W02InventoryReservationAlarmKeyDto k =
                            keyDto.toBuilder().apkihoncd(acptDlvryDto.getDlvrykankatsuapkihoncd())
                                .shopcd(acptDlvryDto.getDlvrykankatsushopcd()).build();
                        Optional<S13T004TEntity> entity =
                            settingDto.getOption().isOnlyCheck() ? inventoryAlarmService.getTable(k)
                                : inventoryAlarmService.lockTable(k);
                        entity.ifPresent(e -> {
                            inventoryAlarmService.checkOnly(k, e);
                            // 委託相手の在庫アラームについてはメッセージが別
                            if (k.isChecked() && !k.isValid()) {
                                result.addWarningMessage(util.getMessage(MessageID.W257,
                                    String.format("商品コード「%s」", k.getConcreteKey())));
                            }
                        });
                    });
                    return r;
                })
                // 配達拠点受注アラームチェック
                .processIfNoError(updateKyotenKeyList, keyDtoList -> {
                    C0801W02ProcessingResultDto r = C0801W02ProcessingResultDto.builder().build();
                    keyDtoList.forEach(keyDto -> {
                        Optional<S04T005TEntity> entity = settingDto.getOption().isOnlyCheck()
                            ? kyotenAlarmService.getTable(keyDto)
                            : kyotenAlarmService.lockTable(keyDto);
                        entity.ifPresent(e -> {
                            kyotenAlarmService.checkOnly(keyDto, e);
                            if (keyDto.isChecked() && !keyDto.isValid()) {
                                result.addWarningMessage(util.getMessage(keyDto.getMessageId(),
                                    String.format("商品コード「%s」が委託先の%s", keyDto.getConcreteKey(),
                                        keyDto.getType().getAlarmCountLabel())));
                            }
                        });
                    });
                    return r;
                })
                // 委託相手担当者受注アラームチェック
                .processIfNoError(updateTnshaKeyList, keyDtoList -> {
                    C0801W02ProcessingResultDto r = C0801W02ProcessingResultDto.builder().build();
                    keyDtoList.forEach(keyDto -> {
                        // 委託相手の配達担当者コードに関する受注アラームを生成
                        C0801W02TnshaOrderAlarmKeyDto k = keyDto.toBuilder()
                            .concreteKey(settingDto.getItakuaiteInfo().getDlvrytnshacd()).build();
                        Optional<S04T006TEntity> entity =
                            settingDto.getOption().isOnlyCheck() ? tnshaAlarmService.getTable(k)
                                : tnshaAlarmService.lockTable(k);
                        entity.ifPresent(e -> {
                            tnshaAlarmService.checkOnly(k, e);
                            if (k.isChecked() && !k.isValid()) {
                                result.addWarningMessage(util.getMessage(k.getMessageId(),
                                    String.format("委託先の%s", k.getMessageArg())));
                            }
                        });
                    });
                    return r;
                })
                // 配達エリア受注アラームチェック
                .processIfNoError(updateAreaKeyList, keyDtoList -> {
                    C0801W02ProcessingResultDto r = C0801W02ProcessingResultDto.builder().build();
                    keyDtoList.forEach(keyDto -> {
                        Optional<S04T007TEntity> entity =
                            settingDto.getOption().isOnlyCheck() ? areaAlarmService.getTable(keyDto)
                                : areaAlarmService.lockTable(keyDto);
                        entity.ifPresent(e -> {
                            areaAlarmService.checkOnly(keyDto, e);
                            if (keyDto.isChecked() && !keyDto.isValid()) {
                                result.addWarningMessage(util.getMessage(keyDto.getMessageId(),
                                    String.format("委託先の%s", keyDto.getMessageArg())));
                            }
                        });
                    });
                    return r;
                })
                // 受託側の伝票ステータスチェック
                .processIfNoError(settingDto,
                    dto -> validator.validateItakusakiDnpysts(acptDlvryDto, dto.getItakuaiteInfo()),
                    // 委託元の場合にチェック実行
                    dto -> acptDlvryDto.isItakumoto());
        }
        // 受注伝票の更新（更新前チェックのときはどちらも実施しない）
        if (processType == PROCESS_TYPE.INSERT || processType == PROCESS_TYPE.RESERVE_INSERT) {
            result.processIfNoError(settingDto, dto -> {
                // 受注配達テーブル登録
                C0801W02ProcessingResultDto r = insertAcptDnpy(context, acptDlvryDto);
                if (!r.hasError()) {
                    // 明細に受注伝票番号と枝番号を設定
                    expandedDetailList.forEach(detailDto -> {
                        detailDto.setAcptdnpyno(acptDlvryDto.getAcptdnpyno());
                        detailDto.setEdano(acptDlvryDto.getEdano());
                    });
                }
                return r;
            }, dto -> !dto.getOption().isOnlyCheck());
        } else {
            // 受注配達テーブル更新
            result
                // 相関チェックNo50 排他チェック
                .processIfNoError(settingDto,
                    dto -> validator.exclusiveCheck(dto.getAcptDlvryDto(), acptDlvryEntity)
                    // 処理種別が更新の場合にチェック
                    , dto -> processType == PROCESS_TYPE.UPDATE)
                .processIfNoError(settingDto,
                    dto -> updateAcptDnpy(context, processType, acptDlvryDto, acptDlvryEntity),
                    dto -> !dto.getOption().isOnlyCheck());
        }
        result
            // 更新時は、受注配達明細登録
            .processIfNoError(settingDto,
                dto -> insertDetailList(context, acptDlvryDto, expandedDetailList),
                dto -> !dto.getOption().isOnlyCheck())
            // 更新時、配布物取り込み処理
            .processIfNoError(settingDto, dto -> haifubutsuService.update(dto.getContext(),
                acptDlvryDto, expandedDetailList, false), dto -> !dto.getOption().isOnlyCheck());
        // 拠点ステータスが「1:他拠点」の場合は本部受注データ連携
        if (acptDlvryDto.isTakyoten()) {
            if (processType == PROCESS_TYPE.INSERT || processType == PROCESS_TYPE.RESERVE_INSERT) {
                // 更新時、登録処理種別が登録または予約登録ならば処理モードは新規
                result.processIfNoError(settingDto,
                    dto -> linkHeadquartersOrderData(dto.getAcptDlvryDto(), LINKAGE_MODE.REGIST),
                    dto -> !dto.getOption().isOnlyCheck());
            } else {
                // 委託元の場合かつ受託側の伝票ステータスが変更可能な場合
                if (acptDlvryDto.isItakumoto()) {
                    Optional<C0801W02AcptDlvryDto> itakusakiAcptDlvryDto = finder
                        .getItakuaiteAcptDlvryDto(acptDlvryDto, settingDto.getItakuaiteInfo());
                    if (itakusakiAcptDlvryDto.isPresent()
                        && itakusakiAcptDlvryDto.get().isCancelable()) {
                        // 更新時、登録処理種別が更新ならば処理モードは更新
                        result.processIfNoError(settingDto,
                            dto -> linkHeadquartersOrderData(dto.getAcptDlvryDto(),
                                LINKAGE_MODE.UPDATE),
                            dto -> !dto.getOption().isOnlyCheck());
                    }
                }
            }
        }
        return result;
    }

    /**
     * 受注キャンセル/削除処理
     * 
     * @param settingDto 受注登録設定DTO
     * @param reservedAcptDnpyNoList 受注予約明細の受注伝票明細番号リスト
     * @return 処理結果
     */
    protected C0801W02ProcessingResultDto processCancel(C0801W02SettingDto settingDto,
        List<String> reservedAcptDnpyNoList) {
        C0801W02ProcessingResultDto result = C0801W02ProcessingResultDto.builder().build();
        // 更新用プロパティ設定（データベースから情報を取得）
        setExistsAcptDnpy(settingDto);
        // 処理対象とする受注配達DTO
        C0801W02AcptDlvryDto acptDlvryDto = settingDto.getAcptDlvryDto(true);
        // 処理対象とする受注配達明細DTOリスト
        List<C0801W02AcptDlvryDetailDto> detailList = settingDto.getDetailList(true);
        // 更新対象となる受注配達テーブルのレコードをロック
        Optional<S04T001TEntity> acptDlvryEntity = finder.getAcptDlvryEntity(
            acptDlvryDto.getApkihoncd(), acptDlvryDto.getShopcd(), acptDlvryDto.getAcptdnpyno(),
            acptDlvryDto.getEdano(), !settingDto.getOption().isOnlyCheck());
        // 受注予約データを元に新規登録する場合は、以下の処理を実施しない。
        if (settingDto.getOption().getProcessType() != PROCESS_TYPE.RESERVE_INSERT) {
            result
                // 相関チェックNo64 伝票ステータスチェック ※委託相手
                .processIfNoError(settingDto,
                    dto -> validator.validateItakusakiDnpysts(acptDlvryDto, dto.getItakuaiteInfo()),
                    // 委託元の場合にチェック実行（処理区分が更新の場合は更新処理でチェックを行うのでこのタイミングでは実行しない）
                    dto -> acptDlvryDto.isItakumoto() && dto.getPrcskbn() != PRCSKBN.UPDATE)
                // 更新時、代引き時未締め売上解除
                .processIfNoError(settingDto,
                    dto -> releaseUnclosedSalesCOD(dto.getContext(), dto.getAcptDlvryDto(true)),
                    dto -> !dto.getOption().isOnlyCheck())
                // 得意先、得意先実績の更新
                .processIfNoError(settingDto, dto -> updateTokuisaki(dto,
                    // 登録処理種別が更新以外はキャンセル処理とする
                    dto.getOption().getProcessType() != PROCESS_TYPE.UPDATE,
                    // 更新前チェックは実行しない
                    false))
                // 更新時、配布物キャンセル処理
                .processIfNoError(settingDto, dto -> haifubutsuService.update(dto.getContext(),
                    acptDlvryDto, detailList, true), dto -> !dto.getOption().isOnlyCheck());
            // 更新前チェックではなく、拠点ステータスが自拠点の場合
            if (!settingDto.getOption().isOnlyCheck()
                && settingDto.getAcptDlvryDto().isJikyoten()) {
                // キャンセル対象の受注伝票を元に各受注アラームにキャンセル内容を反映
                result
                    // 在庫引当アラーム処理
                    .processIfNoError(settingDto,
                        dto -> inventoryAlarmService.execute(dto.getContext(), acptDlvryDto,
                            detailList, true, false))
                    // 配達拠点受注アラーム処理
                    .processIfNoError(settingDto,
                        dto -> kyotenAlarmService.execute(dto.getContext(), acptDlvryDto,
                            detailList, true, false))
                    // 担当者受注アラーム処理
                    .processIfNoError(settingDto,
                        dto -> tnshaAlarmService.execute(dto.getContext(), acptDlvryDto, detailList,
                            true, false))
                    // 配達エリア受注アラーム処理
                    .processIfNoError(settingDto, dto -> areaAlarmService.execute(dto.getContext(),
                        acptDlvryDto, detailList, true, false));
            }
        }
        result
            // 配達対象フラグ設定、受注予約更新部品呼び出し
            .processIfNoError(settingDto,
                dto -> callReservedAcptDlvryUpdate(dto, reservedAcptDnpyNoList),
                // 処理種別が予約更新の場合に実施
                dto -> !dto.getOption().isOnlyCheck()
                    && dto.getOption().getProcessType() == PROCESS_TYPE.RESERVE_INSERT)
            // 相関チェックNo50 排他チェック
            .processIfNoError(settingDto,
                dto -> validator.exclusiveCheck(dto.getAcptDlvryDto(), acptDlvryEntity),
                // 処理種別が更新以外の場合に排他チェック
                dto -> dto.getOption().getProcessType() != PROCESS_TYPE.UPDATE)
            // 受注伝票の更新
            .processIfNoError(settingDto,
                dto -> updateAcptDnpy(dto.getContext(), dto.getOption().getProcessType(),
                    dto.getAcptDlvryDtoFromDb(), acptDlvryEntity),
                // 更新前チェックの際にDB更新は行わない
                dto -> !dto.getOption().isOnlyCheck()
                    // 予約更新の場合は、予約明細を含んで受注登録する場合のみ
                    && ((dto.getOption().getProcessType() == PROCESS_TYPE.RESERVE_INSERT
                        && !reservedAcptDnpyNoList.isEmpty())
                        // または処理種別が更新以外の場合
                        || dto.getOption().getProcessType() != PROCESS_TYPE.UPDATE));
        return result;
    }

    /**
     * 受注配達明細DTOリストを展開（セット商品の展開、配布物明細の作成）する
     * 
     * @param settingDto 受注登録設定DTO
     * @return 受注配達明細DTOリスト
     */
    protected List<C0801W02AcptDlvryDetailDto> expandDetail(C0801W02SettingDto settingDto) {
        // 受注配達DTO
        C0801W02AcptDlvryDto acptDlvryDto = settingDto.getAcptDlvryDto();
        // 集計結果を初期化
        acptDlvryDto.setSummary(C0801W02AcptDlvrySummaryDto.builder().build());
        // 元となる受注配達明細DTOリスト
        List<C0801W02AcptDlvryDetailDto> detailList = settingDto.getDetailList();
        // 受注配達明細DTOへの更新用プロパティ設定および本数/金額集計
        detailList.forEach(util.withIndexConsumer(0, (detailDto, i) -> {
            // 受注配達明細番号の設定
            detailDto.setAcptdnpymeisaino(util.getString(i + 1));
            // 入力明細フラグの設定
            detailDto.setNyuryokumeisaiflg(CommonConstants.FLG_ON);
            // 納品金額が設定されていない場合は商品行とする
            if (StringUtil.isEmpty(detailDto.getNohinkin())) {
                detailDto.setMeisaikbn(CommonConstants.KBN_MEISAI_SHOHIN);
            }
            // 商品情報設定および本数・金額集計
            if (!StringUtil.isEmpty(detailDto.getShncd())) {
                C0801W02ShnDto shnDto = shnService.getShnInfo(detailDto.getApkihoncd(),
                    detailDto.getShopcd(), detailDto.getShncd());
                detailDto.setShnDto(shnDto, acptDlvryDto.getDlvryytidd());
                if (detailDto.isCountable()) {
                    acptDlvryDto.getSummary().addAll(detailDto);
                }
            }
        }));
        // セット商品以外の受注配達明細DTOリスト
        List<C0801W02AcptDlvryDetailDto> onlySingleShnDetailList =
            detailList.stream().filter(detailDto -> util.isFlagged(detailDto.getTanpinmeisaiflg()))
                .collect(util.toList());
        // 受注配達明細DTOリスト内のセット商品を展開
        List<C0801W02AcptDlvryDetailDto> expandStShnDetailList = detailList.stream()
            // セット商品のみに絞り込み
            .filter(detailDto -> !util.isFlagged(detailDto.getTanpinmeisaiflg()))
            // 構成商品の明細および必要に応じて調整消費税の明細を作成しListにする
            .map(detailDto -> expandStShn(acptDlvryDto, detailDto))
            // 平坦化（List<List<E>> → List<E>）
            .flatMap(List::stream).collect(util.toList());
        // 配布物の受注配達明細を作成
        List<C0801W02AcptDlvryDetailDto> haifubutsuDetailList =
            createHaifubutsuDetail(acptDlvryDto, detailList.size() + 1);
        // 受注配達明細DTOリストをマージ
        List<C0801W02AcptDlvryDetailDto> mergedList = util.mergeIntoList(
            // セット商品以外の明細リスト
            onlySingleShnDetailList,
            // 展開したセット商品の明細リスト
            expandStShnDetailList,
            // 配布物の明細リスト
            haifubutsuDetailList);
        // キーの昇順にソート
        mergedList.sort(C0801W02AcptDlvryDetailDto.getOrderBy());
        return mergedList;
    }

    /**
     * セット商品受注配達明細展開
     * 
     * @param acptDlvryDto 受注配達DTO
     * @param detailDto セット商品の受注配達明細DTO
     * @return 展開された受注配達明細DTOリスト
     */
    protected List<C0801W02AcptDlvryDetailDto> expandStShn(C0801W02AcptDlvryDto acptDlvryDto,
        C0801W02AcptDlvryDetailDto detailDto) {
        // セット商品の構成商品リストを取得
        List<S02M008TSetShohinEntity> composition = s02m008tDao.getSetShohinList(
            detailDto.getApkihoncd(), detailDto.getShopcd(), detailDto.getShncd());
        // セット商品を構成する商品の受注配達明細DTOリストを生成
        List<C0801W02AcptDlvryDetailDto> bundleDetailList =
            composition.stream().map(util.withIndexFunction(0, (bundle, index) -> {
                // 元となる受注配達明細DTOのコピーを取得
                C0801W02AcptDlvryDetailDto bundleDetailDto = detailDto.toBuilder()
                    // 受注伝票明細枝番号
                    .acptdnpymeisaiedano(util.getString(index + 1))
                    // 商品コード
                    .shncd(bundle.getKoseishncd())
                    // 型式
                    .katasiki(null)
                    // セット商品コード
                    .stshncd(detailDto.getShncd())
                    // 入力明細フラグ
                    .nyuryokumeisaiflg(CommonConstants.FLG_OFF)
                    // 単品明細フラグ
                    .tanpinmeisaiflg(CommonConstants.FLG_ON).build();
                // 明細に委託相手受注伝票明細番号が設定されている場合
                if (!StringUtil.isEmpty(detailDto.getItakuaiteacptdnpymeisaino())) {
                    // 委託相手受注伝票明細枝番号を設定
                    bundleDetailDto.setItakuaiteacptdnpymeisaiedano(util.getString(index + 1));
                }
                // セット商品を構成する商品の情報を取得
                C0801W02ShnDto bundleShnDto =
                    shnService.getBundleShnInfo(acptDlvryDto.getApkihoncd(),
                        acptDlvryDto.getShopcd(), detailDto.getShncd(), bundle);
                // セット商品の展開行について、標準単価は商品マスタビューから取得する
                C0801W02ShnDto bundleShnDtoForHyojununt = shnService.getShnInfo(
                    acptDlvryDto.getApkihoncd(), acptDlvryDto.getShopcd(), bundle.getKoseishncd());
                // 標準単価DTOを生成
                C0801W02SwitchableShnPriceDto hyojunPriceDto =
                    bundleShnDtoForHyojununt.getHyojunPriceDto(acptDlvryDto.getDlvryytidd());
                // 商品情報を明細に設定
                bundleDetailDto.setShnDto(bundleShnDto, acptDlvryDto.getDlvryytidd());
                // 受注数量（受注数量が未設定または0の明細はオミット済のためそのまま乗算）
                bundleDetailDto
                    .setAcptsuryo(bundleDetailDto.getDecimalValue(bundleDetailDto.getAcptsuryo())
                        .multiply(bundle.getSuryo()).stripTrailingZeros().toPlainString());
                // 納品数量（納品数量が未設定または0の場合、getDecimalValueは0を返すのでそのまま乗算）
                bundleDetailDto
                    .setNohinsuryo(bundleDetailDto.getDecimalValue(bundleDetailDto.getNohinsuryo())
                        .multiply(bundle.getSuryo()).stripTrailingZeros().toPlainString());
                // 標準単価（配達区分に応じた配達予定日時点の標準価格を設定）
                bundleDetailDto
                    .setHyojununt(hyojunPriceDto.getPriceByDlvrykbn(acptDlvryDto.getDlvrykbn()));
                // 計上区分および各種金額算出
                if (detailDto.getAcptRelation().isYuryo()) {
                    // セット商品が有料の場合
                    // 計上区分は構成商品の計上区分を設定
                    bundleDetailDto.setKeijokbn(bundle.getKeijokbn());
                    // 構成商品の得意先別特別単価を取得
                    Optional<String> tokubetsuUnt = tokuisakiService.getBundleShnTokubetsuUnt(
                        // AP基本コード
                        acptDlvryDto.getApkihoncd(),
                        // ショップコード
                        acptDlvryDto.getShopcd(),
                        // 得意先コード
                        acptDlvryDto.getTokuisakicd(),
                        // 配達予定日
                        acptDlvryDto.getDlvryytidd(),
                        // セット商品コード
                        bundle.getStshncd(),
                        // 連番
                        bundle.getRenban(),
                        // 料金種別特別単価フラグ
                        detailDto.getRknkindtokubetsuuntflg())
                        // DTOが設定されてれば配達区分に応じた特別価格を取得
                        .map(d -> d.getPriceByDlvrykbn(acptDlvryDto.getDlvrykbn()));
                    // 受注単価設定（特別単価が取得できれば設定、そうでない場合はショップ価格を設定）
                    bundleDetailDto.setAcptunt(tokubetsuUnt.orElse(bundleDetailDto
                        .getShopShnPriceDto().getPriceByDlvrykbn(acptDlvryDto.getDlvrykbn())));
                    // 納品単価設定
                    if (util.isZeroString(bundleDetailDto.getNohinsuryo())) {
                        // 納品数量が0なら納品単価も0を設定
                        bundleDetailDto.setNohinunt(ZERO);
                    } else {
                        // 配達日時点の特別単価を取得、取得できない場合はショップ価格を用いる
                        tokubetsuUnt = tokuisakiService.getBundleShnTokubetsuUnt(
                            // AP基本コード
                            acptDlvryDto.getApkihoncd(),
                            // ショップコード
                            acptDlvryDto.getShopcd(),
                            // 得意先コード
                            acptDlvryDto.getTokuisakicd(),
                            // 配達日
                            acptDlvryDto.getDlvrydd(),
                            // セット商品コード
                            bundle.getStshncd(),
                            // 連番
                            bundle.getRenban(),
                            // 料金種別特別単価フラグ
                            detailDto.getRknkindtokubetsuuntflg())
                            // DTOが設定されてれば配達区分に応じた特別価格を取得
                            .map(d -> d.getPriceByDlvrykbn(acptDlvryDto.getDlvrykbn()));
                        bundleDetailDto.setNohinunt(tokubetsuUnt.orElse(bundleDetailDto
                            .getShopShnPriceDto().getPriceByDlvrykbn(acptDlvryDto.getDlvrykbn())));
                    }
                    // 受注関係DTOを構成商品の受注配達明細用に設定する
                    bundleDetailDto
                        .setAcptRelation(detailDto.getAcptRelation().copyWith(bundleDetailDto));
                    // 受注金額算出
                    priceService.calculateAcptkin(acptDlvryDto.getDlvryytidd(), bundleDetailDto);
                    // 納品金額算出
                    priceService.calculateNohinkin(acptDlvryDto.getAcptdd(), bundleDetailDto);
                    // インボイス施行日以降は構成商品の受注消費税金額および納品消費税金額を0円にする
                    if (util.isLater(acptDlvryDto.getDlvryytidd(),
                        detailDto.getCalculateOptionDto().getInvoicshikoymd())) {
                        bundleDetailDto.setAcptshohitaxkin(ZERO);
                        bundleDetailDto.setNohinshohitaxkin(ZERO);
                    }
                    // 非課税/不課税の場合の消費税額設定
                    if (util.isFlagged(detailDto.getShnDto().getTaxsetteikbn())) {
                        // セット商品が税設定するかつ構成商品が非課税または不課税の場合
                        if (CommonConstants.KBN_TAX_HIKAZEI.equals(bundleDetailDto.getTaxkbn())
                            || CommonConstants.KBN_TAX_HUKAZEI
                                .equals(bundleDetailDto.getTaxkbn())) {
                            // 消費税項目には0を設定
                            bundleDetailDto.setAcptuntshohitaxkin(ZERO);
                            bundleDetailDto.setAcptshohitaxkin(ZERO);
                            bundleDetailDto.setNohinuntshohitaxkin(ZERO);
                            bundleDetailDto.setNohinshohitaxkin(ZERO);
                        }
                    }
                } else {
                    // 受注関係DTOを構成商品の受注配達明細用に設定する
                    bundleDetailDto
                        .setAcptRelation(detailDto.getAcptRelation().copyWith(bundleDetailDto));
                    // セット商品が無料の場合、計上区分をセット商品のものにして金額を0にする
                    bundleDetailDto.setKeijokbn(detailDto.getKeijokbn());
                    bundleDetailDto.setAcptunt(ZERO);
                    bundleDetailDto.setAcptuntshohitaxkin(ZERO);
                    bundleDetailDto.setAcptshohitaxkin(ZERO);
                    bundleDetailDto.setAcptkin(ZERO);
                    bundleDetailDto.setNohinunt(ZERO);
                    bundleDetailDto.setNohinuntshohitaxkin(ZERO);
                    bundleDetailDto.setNohinshohitaxkin(ZERO);
                    bundleDetailDto.setNohinkin(ZERO);
                }
                // 本数/金額集計
                acptDlvryDto.getSummary().addAll(bundleDetailDto);
                // 構成商品の受注配達明細DTOを返す
                return bundleDetailDto;
            })).collect(util.toList());
        // インボイス施行日以前の場合
        if (!util.isLater(acptDlvryDto.getDlvryytidd(),
            detailDto.getCalculateOptionDto().getInvoicshikoymd())) {
            // 構成商品の受注明細DTOリストの消費税金額を集計
            BigDecimal bundleTaxSummary = bundleDetailList.stream()
                .map(bundleDto -> bundleDto.getDecimalValue(bundleDto.getAcptshohitaxkin()))
                .reduce((current, acptshohitaxkin) -> current.add(acptshohitaxkin))
                .orElse(BigDecimal.ZERO);
            // セット商品の受注配達明細の消費税と集計値に差を算出
            BigDecimal diff = detailDto.getDecimalValue(detailDto.getAcptshohitaxkin())
                .subtract(bundleTaxSummary);
            // セット商品の受注消費税金額と構成商品の受注消費税金額集計値に差がある場合
            if (BigDecimal.ZERO.compareTo(diff) != 0) {
                // 調整消費税明細の作成し、構成商品受注配達明細DTOリストに追加
                C0801W02AcptDlvryDetailDto adjustedTaxDetail = detailFinder
                    .createadjustedTaxDetail(acptDlvryDto, detailDto, bundleDetailList, diff);
                bundleDetailList.add(adjustedTaxDetail);
            }
        }
        // セット商品と構成商品の明細をマージして返却
        return util.mergeIntoList(Stream.of(detailDto).collect(util.toList()), bundleDetailList);
    }

    /**
     * 配布物の受注配達明細DTOリストを生成する
     * 
     * @param acptDlvryDto 受注配達DTO
     * @param start 受注伝票明細番号の開始番号
     * @return 受注配達明細DTOリスト
     */
    protected List<C0801W02AcptDlvryDetailDto> createHaifubutsuDetail(
        C0801W02AcptDlvryDto acptDlvryDto, int start) {
        // 配布物の受注配達明細を作成
        // 配布物用の受注配達明細DTOのテンプレート（受注配達DTOの内容を反映）
        C0801W02AcptDlvryDetailDto haifuDetailTemplate = C0801W02AcptDlvryDetailDto.builder()
            // AP基本コード
            .apkihoncd(acptDlvryDto.getApkihoncd())
            // ショップコード
            .shopcd(acptDlvryDto.getShopcd())
            // 受注伝票番号
            .acptdnpyno(acptDlvryDto.getAcptdnpyno())
            // 枝番号
            .edano(acptDlvryDto.getEdano())
            // 受注伝票明細枝番号（0固定）
            .acptdnpymeisaiedano(BigDecimal.ZERO.stripTrailingZeros().toPlainString())
            // 配布物AP基本コード
            .haifubutsuapkihoncd(acptDlvryDto.getApkihoncd())
            // 配布物ショップコード
            .haifubutsushopcd(acptDlvryDto.getShopcd())
            // データ作成元区分
            .datsksmotokbn(acptDlvryDto.getDatsksmotokbn())
            // 委託相手AP基本コード
            .itakuaiteapkihoncd(acptDlvryDto.getItakuaiteapkihoncd())
            // 委託相手ショップコード
            .itakuaiteshopcd(acptDlvryDto.getItakuaiteshopcd())
            // 委託相手受注伝票番号
            .itakuaiteacptdnpyno(acptDlvryDto.getItakuaiteacptdnpyno())
            // 委託相手枝番号
            .itakuaiteedano(acptDlvryDto.getItakuaiteedano())
            // 数量（1固定）
            .acptsuryo(BigDecimal.ONE.stripTrailingZeros().toPlainString())
            // 有償区分（無償）
            .yusyokbn(CommonConstants.KBN_YUSHO_MUSHO)
            // 明細区分（配布物）
            .meisaikbn(CommonConstants.KBN_MEISAI_HAIHUBUTSU)
            // 入力明細フラグ（ON）
            .nyuryokumeisaiflg(CommonConstants.FLG_ON)
            // 単品明細フラグ（ON）
            .tanpinmeisaiflg(CommonConstants.FLG_ON).build();
        return acptDlvryDto.getHaifubutsuList().stream()
            // 配布物コードが設定されているDTOのみ処理の対象とする
            .filter(dto -> !StringUtil.isEmpty(dto.getHaifubutsucd()))
            // 配布物DTOをもとに受注配達明細DTOに変換
            .map(util.withIndexFunction(start,
                // テンプレートの内容を元にする
                (haifuDto, i) -> haifuDetailTemplate.toBuilder()
                    // 配布物AP基本コード
                    .haifubutsuapkihoncd(haifuDto.getApkihoncd())
                    // 配布物ショップコード
                    .haifubutsushopcd(haifuDto.getShopcd())
                    // 配布物コード
                    .haifubutsucd(haifuDto.getHaifubutsucd())
                    // 受注伝票明細番号
                    .acptdnpymeisaino(util.getString(i))
                    // 委託相手受注伝票明細番号
                    .itakuaiteacptdnpymeisaino(haifuDto.getItakuaiteacptdnpymeisaiedano())
                    // 委託相手受注伝票明細枝番号
                    .itakuaiteacptdnpymeisaiedano(haifuDto.getItakuaiteacptdnpymeisaiedano())
                    .build()))
            .collect(util.toList());
    }

    /**
     * 受注配達明細DTOリストから受注予約明細の番号リストを生成する
     * 
     * @param detailList 受注予約明細DTOリスト
     * @return 受注予約明細番号リスト
     */
    protected List<String> createReservedDetailNumberList(
        List<C0801W02AcptDlvryDetailDto> detailList) {
        return util.convertList(detailList.stream().filter(detailDto -> detailDto.isReserved()),
            detailDto -> detailDto.getAcptdnpymeisaino());
    }

    /**
     * 受注予約更新部品により受注予約の更新を行う
     * 
     * @param settingDto 受注登録設定DTO
     * @param reservedAcptDnpyNoList 受注予約明細番号リスト
     * @return 処理結果
     */
    protected C0801W02ProcessingResultDto callReservedAcptDlvryUpdate(C0801W02SettingDto settingDto,
        List<String> reservedAcptDnpyNoList) {
        C0801W02ProcessingResultDto result = C0801W02ProcessingResultDto.builder().build();
        // 配達対象フラグをOFFに設定
        settingDto.getAcptDlvryDto().setDlvrytrgtflg(CommonConstants.FLG_OFF);
        if (settingDto.getOption().getProcessType() == PROCESS_TYPE.RESERVE_INSERT) {
            if (settingDto.getAcptDlvryDto().isRegisterd()) {
                // 引数で指定された予約明細番号リストが空の場合、受注配達明細DTOリストを変換して用いる
                List<String> detailNumberList = reservedAcptDnpyNoList.isEmpty()
                    ? createReservedDetailNumberList(settingDto.getDetailList())
                    : reservedAcptDnpyNoList;
                // 受注予約更新部品を呼出
                juchuYykKshnService.juchuYykKshn(
                    // AP基本コード
                    settingDto.getAcptDlvryDto().getApkihoncd(),
                    // ショップコード
                    settingDto.getAcptDlvryDto().getShopcd(),
                    // 受注伝票番号
                    settingDto.getAcptDlvryDto().getAcptdnpyno(),
                    // 枝番号
                    settingDto.getAcptDlvryDto().getEdano(),
                    // 受注伝票明細番号リスト
                    detailNumberList,
                    // 残回数（0固定）
                    (short) 0,
                    // 運用日付
                    settingDto.getContext().getOperationDate(),
                    // 配達実績フラグ（受注登録なのでfalse)
                    false,
                    // 登録情報DTO
                    settingDto.getContext().getRegistrationInfoDto());
            }
        }
        return result;
    }

    /**
     * 本部受注データ連携（バッチ）を実行する
     * 
     * <ul>
     * <li>相関チェックNo63 オンラインバッチ異常終了</li>
     * </ul>
     * 
     * @param acptDlvryDto 受注配達DTO
     * @param mode 連携モード
     * @return 処理結果
     */
    protected C0801W02ProcessingResultDto linkHeadquartersOrderData(
        C0801W02AcptDlvryDto acptDlvryDto, LINKAGE_MODE mode) {
        C0801W02ProcessingResultDto result = C0801W02ProcessingResultDto.builder().build();
        try {
            C0804B01Entity param = new C0804B01Entity();
            param.setApkihoncd(acptDlvryDto.getApkihoncd());
            param.setShopcd(acptDlvryDto.getShopcd());
            param.setAcptdnpyno(acptDlvryDto.getAcptdnpyno());
            param.setEdano(acptDlvryDto.getIntValue(acptDlvryDto.getEdano()));
            param.setMode(mode.getValue());
            c0804b01Dao.honbuJuchuDatRenkei(param);
        } catch (Exception e) {
            C0801W02ProcessingErrorDto error =
                C0801W02ProcessingErrorDto.builder().messageId(MessageID.W167).cause(e).build();
            result.setError(error);
        }
        return result;
    }

    /**
     * 受注締め処理
     * 
     * <ul>
     * <li>相関チェックNo63 オンラインバッチ異常終了</li>
     * </ul>
     * 
     * @param acptDlvryDto 受注配達DTO
     * @param context コンテキスト
     * @return 処理結果
     */
    protected C0801W02ProcessingResultDto closeOrder(C0801W02AcptDlvryDto acptDlvryDto,
        C0801W02ProcessingContextDto context) {
        C0801W02ProcessingResultDto result = C0801W02ProcessingResultDto.builder().build();
        // 起動パラメータ設定
        C0809B11Entity parameter = new C0809B11Entity();
        parameter.setApkihoncd(acptDlvryDto.getApkihoncd());
        parameter.setShopcd(acptDlvryDto.getShopcd());
        parameter.setAcptdnpyno(acptDlvryDto.getAcptdnpyno());
        parameter.setEdano(acptDlvryDto.getEdano());
        parameter.setKoshaapkihoncd(context.getApkihoncd());
        parameter.setKoshashopcd(context.getShopcd());
        parameter.setKoshacd(context.getTnshacd());
        parameter.setKshnipadrse(context.getIpAddress());
        // バッチ処理呼び出し
        c0809b11Dao.getDeliveryPlanDateByDnpyno(parameter);
        if (parameter.getReturnedValue() != 0) {
            // バッチ処理結果が「0：正常」ではない場合、エラーとする
            C0801W02ProcessingErrorDto error = C0801W02ProcessingErrorDto.builder()
                .messageId(MessageID.W161).build().addMessageArg("受注締め処理");
            result.setError(error);
        }
        return result;
    }

    /**
     * 登録済の受注伝票および受注配達明細を取得し、受注登録設定DTOに設定する
     * 
     * @param settingDto 受注登録設定DTO
     */
    protected void setExistsAcptDnpy(C0801W02SettingDto settingDto) {

        C0801W02AcptDlvryDto acptDlvryDto = settingDto.getAcptDlvryDto();
        C0801W02TokuisakiMasterDto tokuisakiDto = settingDto.getTokuisakiInfo();
        // データベースの内容を元に受注配達DTOを取得
        C0801W02AcptDlvryDto existsAcptDlvryDto = finder.getAcptDlvryData(
            acptDlvryDto.getApkihoncd(), acptDlvryDto.getShopcd(), acptDlvryDto.getAcptdnpyno(),
            acptDlvryDto.getEdano(), tokuisakiDto, settingDto.getContext().getOperationDate());
        // 受注配達明細DTOリストを再取得（配布物、入力行以外も取得）
        C0801W02ShopMasterDto shopDto = settingDto.getShopInfo();
        List<C0801W02AcptDlvryDetailDto> existsDetailList = detailFinder
            .getAcptDlvryDetailList(existsAcptDlvryDto, shopDto, settingDto.getOption());
        // 在庫管理区分（ショップマスタから設定）
        existsAcptDlvryDto.setZaikokanrikbn(
            CommonConstants.FLG_OFF.equals(shopDto.getZaikokanrikbn()) ? CommonConstants.FLG_OFF
                : CommonConstants.FLG_ON);
        // 税計算区分（ショップマスタから設定）
        existsAcptDlvryDto.setTaxkeisankbn(
            shopDto.getTaxkeisankbnuri(settingDto.getContext().getOperationDate()));
        // 得意先配達情報から受注配達DTOに値を設定
        C0801W02TokuisakiDlvryDto tokuisakiDlvryDto = settingDto.getTokuisakiDlvryDto();
        // 請求先コード
        existsAcptDlvryDto.setSkysakicd(tokuisakiDlvryDto.getSkysakicd());
        // 配達エリア関連（未設定の場合）
        if (StringUtil.isEmpty(existsAcptDlvryDto.getDlvryareacd())) {
            existsAcptDlvryDto.setDlvryareacd(tokuisakiDlvryDto.getDlvryareacd());
            existsAcptDlvryDto.setDlvryareanm(tokuisakiDlvryDto.getDlvryareanm());
            existsAcptDlvryDto.setDlvrypatern(tokuisakiDlvryDto.getDlvrypatern());
        }
        // 配達希望時間関連項目（未設定の場合）
        if (StringUtil.isEmpty(existsAcptDlvryDto.getDlvrykibojikankbn())) {
            existsAcptDlvryDto.setDlvrykibojikan(tokuisakiDlvryDto.getAcptjikanshiteijikoku());
            existsAcptDlvryDto
                .setDlvrykibojikanshry(tokuisakiDlvryDto.getAcptjikanshiteijikokushry());
            existsAcptDlvryDto.setDlvrykibojikankbn(tokuisakiDlvryDto.getAcptjikanshiteikbn());
        }
        // 配達順（未設定の場合）
        if (StringUtil.isEmpty(existsAcptDlvryDto.getDlvryjun())) {
            existsAcptDlvryDto.setDlvryjun(tokuisakiDlvryDto.getDlvryjun());
        }
        // 明細プロパティセットおよび集計
        C0801W02AcptDlvrySummaryDto summary = C0801W02AcptDlvrySummaryDto.builder().build();
        existsDetailList.forEach(
            d -> setPropertiesAndSummary(d, summary, settingDto.getContext().getOperationDate()));
        // 集計結果を受注配達DTOに設定
        existsAcptDlvryDto.setSummary(summary);
        // 取得結果を受注登録設定DTOに設定
        settingDto.setAcptDlvryDtoFromDb(existsAcptDlvryDto);
        // 受注配達明細DTOリストを受注登録設定DTOに反映
        settingDto.setDetailListFromDb(existsDetailList);
    }

    /**
     * 受注配達明細DTOに更新用プロパティを設定し集計を行う
     * 
     * @param detailDto 受注配達明細DTO
     * @param summary 受注伝票集計DTO
     * @param targetDate 対象日
     */
    protected void setPropertiesAndSummary(C0801W02AcptDlvryDetailDto detailDto,
        C0801W02AcptDlvrySummaryDto summary, String targetDate) {
        // 商品行のみ対象とする
        if (CommonConstants.KBN_MEISAI_SHOHIN.equals(detailDto.getMeisaikbn())) {
            // 商品コードが設定されていれば
            if (!StringUtil.isEmpty(detailDto.getShncd())) {
                C0801W02ShnDto shnDto = shnService.getShnInfo(detailDto.getApkihoncd(),
                    detailDto.getShopcd(), detailDto.getShncd());
                // 商品情報を明細に設定
                detailDto.setShnDto(shnDto, targetDate);
                // 対象日が軽減税率施行日以前
                if (!util.isLater(targetDate, REDUCED_TAX_RATE_STRDD)) {
                    // 税区分が「4：課税軽減」の場合
                    if (CommonConstants.KBN_TAX_KEIGEN.equals(detailDto.getTaxkbn())) {
                        // 税区分を「1：課税」に変更する
                        detailDto.setTaxkbn(CommonConstants.KBN_TAX_KAZEI);
                    }
                }
                // 構成品の税区分は親商品の税区分にする
                if (!util.isFlagged(detailDto.getNyuryokumeisaiflg())) {
                    C0801W02ShnDto parentShn = shnService.getShnInfo(detailDto.getApkihoncd(),
                        detailDto.getShopcd(), detailDto.getStshncd());
                    detailDto.setTaxkbn(parentShn.getTaxkbn());
                    // 対象日が軽減税率施行日以前かつ税区分が「4：課税軽減」
                    if (!util.isLater(targetDate, REDUCED_TAX_RATE_STRDD)) {
                        if (CommonConstants.KBN_TAX_KEIGEN.equals(detailDto.getTaxkbn())) {
                            // 税区分を「1：課税」に変更する
                            detailDto.setTaxkbn(CommonConstants.KBN_TAX_KAZEI);
                        }
                    }
                }
                // 本数集計対象であれば集計する
                if (detailDto.isCountable()) {
                    summary.addAll(detailDto);
                }
            }
        }
    }

    /**
     * 代引き受注に対する未締め売上の解除を行う<br>
     * <br>
     * インボイス対応<br>
     * ※CODは「Cash On Delivery（代引）」の略
     * 
     * @param context コンテキスト
     * @param acptDlvryDto 受注配達DTO
     */
    public void releaseUnclosedSales(C0801W02ProcessingContextDto context,
        C0801W02AcptDlvryDto acptDlvryDto) {
        // 受注時代引フラグがONの場合のみ、未締め売上解除を行う
        if (!util.isFlagged(acptDlvryDto.getAcptthdaibikiflg())) {
            return;
        }
        // 売上テーブルから未締めデータを取得する
        S06T001TEntityExample condition = new S06T001TEntityExample();
        condition.createCriteria()
            // AP基本コード
            .andApkihoncdEqualTo(acptDlvryDto.getApkihoncd())
            // ショップコード
            .andShopcdEqualTo(acptDlvryDto.getShopcd())
            // 受注伝票番号
            .andAcptdnpynoEqualTo(acptDlvryDto.getAcptdnpyno())
            // 売上伝票番号が受注伝票番号と一致しない
            .andUridnpynoNotEqualTo(acptDlvryDto.getAcptdnpyno())
            // 請求締年月がnull
            .andSkyshimedydmIsNull()
            // 請求締日がnull
            .andSkyshimeddIsNull()
            // 有効区分が有効
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<S06T001TEntity> unclosedSalesEntityList = s06t001tDao.selectByExample(condition);
        unclosedSalesEntityList.forEach(unclosedSalesEntity -> {
            // 請求締め処理日をデフォルトに変更
            unclosedSalesEntity.setSkyprcsjissidd(CommonConstants.DEF_DATE_00000000);
            // 売上締め処理日を売り上げ日に変更
            unclosedSalesEntity.setUrishimeprcsdd(unclosedSalesEntity.getUridd());
            // 受注伝票番号をnullに変更
            unclosedSalesEntity.setAcptdnpyno(null);
            // 受注伝票枝番号をnullに変更
            unclosedSalesEntity.setAcptdnpyedano(null);
            // タイムスタンプなどを設定
            unclosedSalesEntity.setKoshaapkihoncd(context.getApkihoncd());
            unclosedSalesEntity.setKoshashopcd(context.getShopcd());
            unclosedSalesEntity.setKoshacd(context.getTnshacd());
            unclosedSalesEntity.setKshnsystmymd(context.getSystemDate());
            unclosedSalesEntity.setKshnsystmjikan(context.getSystemTime());
            unclosedSalesEntity.setKshnunyoymd(context.getOperationDate());
            unclosedSalesEntity.setKshnipadrse(context.getIpAddress());
            unclosedSalesEntity.setKshnpgmid(context.getProgramId());
            // 売上テーブルに変更内容を反映
            s06t001tDao.updateByPrimaryKey(unclosedSalesEntity);
        });

    }

    /**
     * 代引き受注に対する未締め売上の解除を行う<br>
     * <br>
     * インボイス対応<br>
     * ※CODは「Cash On Delivery（代引）」の略
     * 
     * @param context コンテキスト
     * @param acptDlvryDto 受注配達DTO
     * @return 処理結果
     */
    protected C0801W02ProcessingResultDto releaseUnclosedSalesCOD(
        C0801W02ProcessingContextDto context, C0801W02AcptDlvryDto acptDlvryDto) {
        C0801W02ProcessingResultDto result = C0801W02ProcessingResultDto.builder().build();
        releaseUnclosedSales(context, acptDlvryDto);
        return result;
    }

    /**
     * 得意先、得意先実績の更新を行う<br>
     * 
     * @param settingDto 受注登録設定DTO
     * @param isCanceled キャンセルかどうか
     * @param isChecked 更新前のチェックを行うか
     * @return 処理結果
     */
    protected C0801W02ProcessingResultDto updateTokuisaki(C0801W02SettingDto settingDto,
        boolean isCanceled, boolean isChecked) {
        C0801W02ProcessingResultDto result = C0801W02ProcessingResultDto.builder().build();
        C0801W02AcptDlvryDto acptDlvryDto = settingDto.getAcptDlvryDto(isCanceled);
        C0801W02TokuisakiMasterDto tokuisakiDto = settingDto.getTokuisakiInfo();
        // 得意先実績データを取得
        Optional<C0801W02TokuisakiJissekiDto> tokuisakiJisseki =
            tokuisakiService.getTokuisakijisseki(
                // AP基本コード
                acptDlvryDto.getApkihoncd(),
                // ショップコード
                acptDlvryDto.getShopcd(),
                // 得意先コード
                acptDlvryDto.getTokuisakicd(),
                // ロックする(チェック時はロックしない)
                !settingDto.getOption().isOnlyCheck());
        // 受注登録設定DTOに反映
        settingDto.setTokuisakiJissekiDto(tokuisakiJisseki.orElse(C0801W02TokuisakiJissekiDto
            .builder().apkihoncd(acptDlvryDto.getApkihoncd()).shopcd(acptDlvryDto.getShopcd())
            .tokuisakicd(acptDlvryDto.getTokuisakicd()).build()));
        // 受注時代引フラグ、請求先コードと配達順を伝票に設定
        acptDlvryDto.setAcptthdaibikiflg(CommonConstants.KBN_KAISHUHOHO_DAIBIKI
            .equals(settingDto.getSkysakiInfo().getKaishuhohokbn()) ? CommonConstants.FLG_ON
                : CommonConstants.FLG_OFF);
        acptDlvryDto.setSkysakicd(tokuisakiDto.getSkysakicd());
        acptDlvryDto.setDlvryjun(tokuisakiDto.getDlvryjun());
        // ACCIS2023-7184 更新処理時に実行しているキャンセル処理において、計上前受注金額が正しく算出されない不具合の対応
        // このメソッドがキャンセル処理から呼ばれているのかを判定するためのフラグを追加
        // 当メソッドのisCheckedがfalseで呼ばれる箇所はキャンセル処理からの呼び出ししかない
        boolean isCalledFromProcessCancel = isCanceled || !isChecked;
        // 更新処理実行
        result
            // 得意先に対する更新前チェック
            .processIfNoError(settingDto,
                dto -> tokuisakiService.validateBeforeUpdate(dto.getTokuisakiInfo(),
                    acptDlvryDto.isReserved()),
                // チェックを行う場合のみ
                dto -> isChecked)
            // 受注区分、受注区分詳細と得意先の整合性チェック
            .processIfNoError(settingDto,
                dto -> validator.validateBeforeTokuisakiUpdate(dto, isCanceled),
                // チェックを行う場合のみ
                dto -> isChecked)
            // 与信限度額のチェック
            .processIfNoError(settingDto,
                dto -> tokuisakiService.checkCreditBalance(dto.getTokuisakiRelation(),
                    acptDlvryDto.getLongValue(acptDlvryDto.getAcptkin())),
                // チェックを行う場合のみ
                dto -> isChecked)
            // 得意先実績の更新
            .processIfNoError(settingDto,
                // 相関チェックNo29 得意先実績テーブル更新件数チェック
                // ACCIS2023-7184 処理区分が更新の場合であっても、更新前のキャンセルであればキャンセルとして処理するように修正
                dto -> tokuisakiService.updateTokuisakiJisseki(dto.getTokuisakiRelation(),
                    dto.getContext(), settingDto.getAcptDlvryDto(isCalledFromProcessCancel),
                    dto.getShopInfo(), isCalledFromProcessCancel),
                // 受注予約伝票ではないかつ得意先実績が取得できた場合のみ実施
                dto -> !dto.getOption().isOnlyCheck() && !acptDlvryDto.isReserved()
                    && !StringUtil.isEmpty(dto.getTokuisakiJissekiDto().getApkihoncd()))
            // 得意先の休眠区分更新
            .processIfNoError(settingDto,
                dto -> tokuisakiService.updateKyuminkbn(dto.getTokuisakiRelation(),
                    settingDto.getContext(), acptDlvryDto, isCanceled),
                // 更新時かつ受注予約伝票ではない場合のみ実施
                dto -> !dto.getOption().isOnlyCheck() && !acptDlvryDto.isReserved());
        return result;
    }

    /**
     * 受注配達テーブルに新規データを登録する
     * 
     * @param context コンテキスト
     * @param acptDlvryDto 受注配達DTO
     * @return 処理結果
     */
    protected C0801W02ProcessingResultDto insertAcptDnpy(C0801W02ProcessingContextDto context,
        C0801W02AcptDlvryDto acptDlvryDto) {
        C0801W02ProcessingResultDto result = C0801W02ProcessingResultDto.builder().build();
        S04T001TEntity entity = acptDlvryDto.createNewEntity(context);
        // 受注伝票番号を採番
        String acptdnpyno =
            numberingService.getSlipNoJuchu(acptDlvryDto.getApkihoncd(), acptDlvryDto.getShopcd());
        acptDlvryDto.setAcptdnpyno(acptdnpyno);
        // 受注伝票枝番号は新規登録の場合「0」
        acptDlvryDto.setEdano(ZERO);
        acptDlvryDto.fillPropertiesToEntity(entity, context);
        if (s04t001tDao.insert(entity) != 1) {
            // 登録エラー（排他エラーにする）
            C0801W02ProcessingErrorDto error =
                C0801W02ProcessingErrorDto.builder().messageId(MessageID.W090).build();
            result.setError(error);
            return result;
        }
        return result;
    }

    /**
     * 受注配達明細テーブルに新規データを登録する<br>
     * ※更新時は一旦既存データを削除して新しい明細データを登録する
     * 
     * @param context コンテキスト
     * @param acptDlvryDto 受注配達DTO
     * @param detailList 受注配達明細DTOリスト
     * @return 処理結果
     */
    protected C0801W02ProcessingResultDto insertDetailList(C0801W02ProcessingContextDto context,
        C0801W02AcptDlvryDto acptDlvryDto, List<C0801W02AcptDlvryDetailDto> detailList) {
        C0801W02ProcessingResultDto result = C0801W02ProcessingResultDto.builder().build();
        // 受注配達明細DTOリストを受注配達明細テーブルEntityリストへ変換
        List<S04T002TEntity> entityList = util.convertList(detailList, detailDto -> {
            S04T002TEntity entity = detailDto.createNewEntity(context);
            if (CommonConstants.KBN_SAKUSEIMOTO_JUCHUYOYAKU
                .equals(acptDlvryDto.getDatsksmotokbn())) {
                // 総回数
                entity.setSoukaisu(detailDto.getShortValue(detailDto.getSoukaisu()));
                // 残回数
                entity.setZankaisu(detailDto.getShortValue(detailDto.getZankaisu()));
            }
            detailDto.fillPropertiesToEntity(entity, context, acptDlvryDto);
            return entity;
        });
        // テーブル内のデータは削除する
        S04T002TEntityExample condition = new S04T002TEntityExample();
        condition.createCriteria().andApkihoncdEqualTo(acptDlvryDto.getApkihoncd())
            .andShopcdEqualTo(acptDlvryDto.getShopcd())
            .andAcptdnpynoEqualTo(acptDlvryDto.getAcptdnpyno())
            .andEdanoEqualTo(util.getShortValue(acptDlvryDto.getEdano()));
        s04t002tDao.deleteByExample(condition);
        for (S04T002TEntity entity : entityList) {
            if (s04t002tDao.insert(entity) != 1) {
                // 登録件数が1件ではない場合、排他エラーとする
                C0801W02ProcessingErrorDto error =
                    C0801W02ProcessingErrorDto.builder().messageId(MessageID.W090).build();
                result.setError(error);
                return result;
            }
        }
        return result;
    }

    /**
     * 受注配達テーブルを更新する
     * 
     * @param context コンテキスト
     * @param type 登録処理種別
     * @param acptDlvryDto 受注配達DTO
     * @param entityOptional 受注配達明細テーブルEntity
     * @return 処理結果
     */
    protected C0801W02ProcessingResultDto updateAcptDnpy(C0801W02ProcessingContextDto context,
        PROCESS_TYPE type, C0801W02AcptDlvryDto acptDlvryDto,
        Optional<S04T001TEntity> entityOptional) {
        C0801W02ProcessingResultDto result = C0801W02ProcessingResultDto.builder().build();
        // 登録処理種別が予約登録の場合は削除に切り替えて処理する
        type = type == PROCESS_TYPE.RESERVE_INSERT ? PROCESS_TYPE.DELETE : type;
        // このメソッドが呼ばれてるまでにOptionalがemptyになっていればエラーとしている
        S04T001TEntity entity = entityOptional.get();
        switch (type) {
            case CANCEL:
                // 受注伝票の各種ステータスを変更
                entity.setDnpysts(CommonConstants.DNPYSTS_09);
                entity.setAcptsts(CommonConstants.DNPYSTS_09);
                entity.setDlvrysts(CommonConstants.DNPYSTS_00);
                acptDlvryDto.setContextForUpdate(entity, context);
                break;
            case DELETE:
                // 有効区分を無効に設定
                entity.setYukokbn(CommonConstants.YUKOKBN_0);
                acptDlvryDto.setContextForUpdate(entity, context);
                break;
            default:
                // DTOの内容をEntityに反映
                acptDlvryDto.fillPropertiesToEntity(entity, context);
                break;
        }
        // 更新処理実行
        int count = s04t001tDao.updateByPrimaryKey(entity);
        // 更新件数が0件の場合、排他エラー（これは更新なのにレコードが消えてた場合）
        if (count == 0) {
            C0801W02ProcessingErrorDto error =
                C0801W02ProcessingErrorDto.builder().messageId(MessageID.W090).build();
            result.setError(error);
            return result;
        }
        return result;
    }

    /**
     * 本部受注データ連携モード列挙
     */
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static enum LINKAGE_MODE {
        /** 新規 */
        REGIST(CommonConstants.HONBURENKEI_SHINKI),
        /** 更新 */
        UPDATE("1"),
        /** キャンセル */
        CANCEL("2"),
        /** 削除 */
        DELETE("3");

        private String value;
    }
}
