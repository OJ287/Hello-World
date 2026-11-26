package jp.co.aquaclara.accis.web.domain.service.d1501;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M091TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R30ListEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R30ListResultEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R35ListEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R35ListResultEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R40ListEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R40ListResultEntity;
import jp.co.aquaclara.accis.common.exception.BusinessException;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.domain.dto.d1501.D1501R30PrintDto;
import jp.co.aquaclara.accis.web.domain.dto.d1501.D1501R30TargetMeisaiDto;
import jp.co.aquaclara.accis.web.domain.service.common.OnlineBatchBaseService;
import jp.co.aquaclara.accis.web.domain.service.common.OnlineBatchCommonModel;

/**
 * 請求書OnlineBatchService.
 * 
 * @author accisdev047
 *
 */
@Service
public class D1501R30Service extends OnlineBatchBaseService<D1501R30ParameterModel> {

    /**
     * 請求残高テーブルDao
     */
    @Autowired
    private S07T001TDao s07t001Dao;

    /**
     * 帳票出力 請求書（1行明細）サービスクラス
     */
    @Autowired
    private D1501R32Service d1501r32Service;

    /**
     * 帳票出力 請求書（お支払明細書）サービスクラス
     */
    @Autowired
    private D1501R37Service d1501r37Service;

    /**
     * 帳票出力 請求書（入金票兼領収書）サービスクラス
     */
    @Autowired
    private D1501R35Service d1501r35Service;

    /**
     * 帳票出力 共通サービスクラス
     */
    @Autowired
    private D1501R00Service d1501r00Service;

    /**
     * 処理ID
     */
    private static final String PROCESS_ID = "D1501B03";

    /**
     * 請求書印刷DTO.印刷種類１：1(ON)
     */
    private static final String SKYSHO_PRINT_KBN_ENABLED = "1";
    /**
     * 請求書印刷DTO.印刷種類１：0(OFF)
     */
    private static final String SKYSHO_PRINT_KBN_DISABLED = "0";
    /**
     * 請求書印刷DTO.印刷種類２：1(ON)
     */
    private static final String HIKAE_PRINT_KBN_ENABLED = "1";
    /**
     * 請求書印刷DTO.印刷種類２：0(OFF)
     */
    private static final String HIKAE_PRINT_KBN_DISABLED = "0";
    /**
     * 請求書印刷DTO.印刷種類３：1(ON)
     */
    private static final String RYOSYUSYO_PRINT_KBN_ENABLED = "1";
    /**
     * 控え：0(請求書のみ)
     */
    private static final String HIKAE_SKYSHO_ONLY = "0";

    /**
     * 控え：1(控えのみ)
     */
    private static final String HIKAE_HIKAE_ONLY = "1";

    /**
     * 控え：2(両方（請求書と控え）)
     */
    private static final String HIKAE_HIKAE_AND_SKYSHO = "2";

    /**
     * お知らせ伝票種別：2(請求書)
     */
    private static final String VOUCHER_TYPE_SKYSHO = "2";

    /**
     * PROCESS_IDを返却する
     * 
     * @return プロセスID
     */
    @Override
    public String getProcessId() {
        return PROCESS_ID;
    }

    /**
     * 請求書CSV、請求書CSV（控え）の作成処理
     * 
     * @param paramModel 請求書出力（メイン）ParameterModel.
     * @param commonInfo 非同期処理の共通情報クラスModel
     */
    @Override
    protected void process(D1501R30ParameterModel paramModel, OnlineBatchCommonModel commonInfo)
        throws BusinessException, Exception {
        // 請求書CSV、請求書（控え）CSVの中間データ
        Map<String, List<List<String>>> skyshoDataMap = new HashMap<String, List<List<String>>>();
        // 領収書CSVの中間データ
        List<List<String>> ryosyusyoList = new ArrayList<>();
        // 画像ファイル名設定
        List<String> imageFileNames = new ArrayList<>();

        // 差出人イメージの設定
        S02M046TEntity shopMasterRecord = getShopMasterRecord(paramModel.getPrintDto());
        imageFileNames.add(ItemUtil.nullToString(shopMasterRecord.getSshdshhitoimage()));

        // 請求書印刷Dto.印刷種類１ ＝ 1（ON：請求書） or 請求書印刷Dto.印刷種類２ ＝ 1（ON：控え） の場合
        if (SKYSHO_PRINT_KBN_ENABLED.equals(paramModel.getPrintDto().getSkyshoprntkbn())
            || HIKAE_PRINT_KBN_ENABLED.equals(paramModel.getPrintDto().getHikaeprntkbn())) {
            // 控え区分の取得
            String hikae = getHikae(paramModel.getPrintDto().getSkyshoprntkbn(),
                paramModel.getPrintDto().getHikaeprntkbn());
            // 請求書中間データ作成
            skyshoDataMap.putAll(createSkyshoData(paramModel.getPrintDto(),
                paramModel.getD1501r30TargetMeisaiDtoList(), hikae, shopMasterRecord));
        }

        // 請求書印刷Dto.印刷種類３ ＝ 1（ON：領収書） の場合
        if (RYOSYUSYO_PRINT_KBN_ENABLED.equals(paramModel.getPrintDto().getRyoshushoprntkbn())) {
            // 領収書中間データ作成
            ryosyusyoList.addAll(createRyosyusyoData(paramModel.getPrintDto(),
                paramModel.getD1501r30TargetMeisaiDtoList(),
                ItemUtil.nullToString(shopMasterRecord.getSshdshhitoimage())));
        }

        // CSV作成処理に渡す中間データList
        List<List<String>> csvData = new ArrayList<>();
        // 中間データを一つのlistに統合
        if (skyshoDataMap.get("請求書CSV") != null) {
            csvData.addAll(skyshoDataMap.get("請求書CSV"));
        }
        if (skyshoDataMap.get("請求書（控）CSV") != null) {
            csvData.addAll(skyshoDataMap.get("請求書（控）CSV"));
        }
        csvData.addAll(ryosyusyoList);
        // 帳票（PDFファイル）を出力する
        super.createReportFile(csvData, commonInfo.getFileNamne(), imageFileNames);
    }

    /**
     * 控えの設定値を返却する
     * 
     * @param skyshoPrintKbn 請求書の印刷種別
     * @param hikaePrintKbn 控えの印刷種別
     * @return 0（請求書のみ） 1（控えのみ） 2（両方）
     */
    private String getHikae(String skyshoPrintKbn, String hikaePrintKbn) {
        String hikae = null;
        if (SKYSHO_PRINT_KBN_ENABLED.equals(skyshoPrintKbn)
            && HIKAE_PRINT_KBN_DISABLED.equals(hikaePrintKbn)) {
            hikae = HIKAE_SKYSHO_ONLY;
        } else if (SKYSHO_PRINT_KBN_DISABLED.equals(skyshoPrintKbn)
            && HIKAE_PRINT_KBN_ENABLED.equals(hikaePrintKbn)) {
            hikae = HIKAE_HIKAE_ONLY;
        } else if (SKYSHO_PRINT_KBN_ENABLED.equals(skyshoPrintKbn)
            && HIKAE_PRINT_KBN_ENABLED.equals(hikaePrintKbn)) {
            hikae = HIKAE_HIKAE_AND_SKYSHO;
        }
        return hikae;
    }

    /**
     * 入金票兼得領収書CSVの中間データリストを作成する
     * 
     * @param printDto 請求書印刷DTO.
     * @param d1501w31TargetMeisaiDtoList 請求書対象明細DTOのリスト
     * @param sshdshhitoImage 差出人イメージ
     * @return 入金票兼得領収書CSVのリスト
     */
    private List<List<String>> createRyosyusyoData(D1501R30PrintDto printDto,
        List<D1501R30TargetMeisaiDto> d1501w31TargetMeisaiDtoList, String sshdshhitoImage) {
        // 入金票兼得領収書CSV
        List<List<String>> ryosyusyoCsv = new ArrayList<>();
        for (D1501R30TargetMeisaiDto d1501w31TargetMeisaiDto : d1501w31TargetMeisaiDtoList) {
            List<S07T001TD1501R35ListResultEntity> s07t001td1501r35ListResultEntityList =
                selectForD1501R35List(printDto, d1501w31TargetMeisaiDto.getSkysakicd());
            ryosyusyoCsv.addAll(d1501r35Service.printDataEdit(s07t001td1501r35ListResultEntityList,
                printDto.getSystemDate(), sshdshhitoImage));
        }
        return ryosyusyoCsv;
    }

    /**
     * 請求書と請求書（控え）の中間データのリストをMapで返却する
     * 
     * @param printDto 請求書印刷DTO.
     * @param d1501w31TargetMeisaiDtoList 請求書対象明細DTOのリスト
     * @param hikae 控えフラグ
     * @param shopMasterRecord ショップマスタ検索結果Entity
     * @return 請求書と請求書（控え）の中間データのリストのMap
     */
    @Transactional
    private Map<String, List<List<String>>> createSkyshoData(D1501R30PrintDto printDto,
        List<D1501R30TargetMeisaiDto> d1501w31TargetMeisaiDtoList, String hikae,
        S02M046TEntity shopMasterRecord) {
        // 請求書CSV
        List<List<String>> skyshoCsv = new ArrayList<>();
        // 請求書（控え）CSV
        List<List<String>> skyshoHikaeCsv = new ArrayList<>();
        // 請求書入金明細印字フラグの取得
        String skyshonyknmeisaiinjiFlg =
            shopMasterRecord == null ? CommonConstants.SKYSHONYKNMEISAIINJIFLG_OFF
                : shopMasterRecord.getSkyshonyknmeisaiinjiflg();

        // インボイス施行フラグ、差出人イメージの取得
        String invoiceEnforcementFlg = "";
        String sshdshhitoimage = "";
        if (shopMasterRecord != null) {
            invoiceEnforcementFlg =
                d1501r00Service.getInvoiceEnforcementFlg(printDto.getShimedydm(),
                    printDto.getSkyshimeday(), shopMasterRecord.getInvoicshikoymd());
            sshdshhitoimage = ItemUtil.nullToString(shopMasterRecord.getSshdshhitoimage());
        }

        // 消費税率管理マスタの取得(通常税率)
        BigDecimal noramlTaxRate = getTaxRate(printDto, CommonConstants.KBN_TAX_KAZEI);
        // 消費税率管理マスタの取得(軽減税率)
        BigDecimal reducedTaxRate = getTaxRate(printDto, CommonConstants.KBN_TAX_KEIGEN);

        // メイン処理 印刷を行う請求書分ループを行う
        for (D1501R30TargetMeisaiDto d1501w31TargetMeisaiDto : d1501w31TargetMeisaiDtoList) {
            // 請求残高テーブルの更新
            // 請求書の印刷種別が（1：控えのみ）の場合、請求残高テーブル（請求書印刷日）は更新しない
            if (!"1".equals(hikae)) {
                updateS07T001T(printDto, d1501w31TargetMeisaiDto.getSkysakicd());
            }
            // 請求書出力データ抽出
            List<S07T001TD1501R30ListResultEntity> s07t001td1501r30ListResultEntity =
                selectForD1501R30List(printDto, d1501w31TargetMeisaiDto.getSkysakicd(),
                    skyshonyknmeisaiinjiFlg, invoiceEnforcementFlg);
            ArrayList<List<String>> skyshoPrintDataList = d1501r32Service.printDataEdit(
                s07t001td1501r30ListResultEntity, printDto.getShimedydm(),
                printDto.getSkyshimeday(), D1501R00Constants.TYOHYO_KBN_SKYSHO,
                invoiceEnforcementFlg, D1501R00Constants.CREDIT_POSTING_DAY_UNUSED,
                ItemUtil.convertToFullWidthNumbers(noramlTaxRate.toString()) + "％",
                ItemUtil.convertToFullWidthNumbers(reducedTaxRate.toString()) + "％",
                D1501R00Constants.TRANSEFER_INFO_FLAG_ENABLED, printDto.getSystemDate(),
                sshdshhitoimage, null, printDto.getTairyuoverprntkbn());

            if (HIKAE_SKYSHO_ONLY.equals(hikae) || HIKAE_HIKAE_AND_SKYSHO.equals(hikae)) {
                // 請求書CSVリストに請求書出力データを追加
                skyshoCsv.addAll(skyshoPrintDataList);
            }
            if (HIKAE_HIKAE_ONLY.equals(hikae) || HIKAE_HIKAE_AND_SKYSHO.equals(hikae)) {
                // 控えCSVリストに請求書出力データ（控え）を追加
                skyshoHikaeCsv.addAll(d1501r00Service.createHikae(skyshoPrintDataList,
                    D1501R00Constants.TITLE_INDEX_SKYSHO));
            }
            // 請求先マスタを検索
            S02M017TEntity skysakiMasterRecord =
                d1501r00Service.selectS02M017T(printDto.getApkihoncd(), printDto.getShopcd(),
                    d1501w31TargetMeisaiDto.getSkysakicd());

            // 変数.インボイス施行フラグ ＝ １ かつ 請求先マスタEntity.請求区分 ＝ '2'（親子）かつ 請求先マスタEntity.明細印刷区分 ＝
            // '1'（親子卸）以外の場合にお支払明細書の印刷を行う。
            if (D1501R00Constants.INVOICE_ENFORCEMENT_FLAG_ENABLED.equals(invoiceEnforcementFlg)
                && CommonConstants.SKYKBN_2.equals(skysakiMasterRecord.getSkykbn())
                && !CommonConstants.MEISAIPRNTKBN_ISSHO
                    .equals(skysakiMasterRecord.getMeisaiprntkbn())) {
                // お支払明細書データ出力処理
                List<S07T001TD1501R40ListResultEntity> s07t001td1501r40ResultListEntity =
                    selectForD1501R40List(printDto, d1501w31TargetMeisaiDto.getSkysakicd());
                // 帳票出力用にデータ成形
                ArrayList<List<String>> meisaisyoPrintDataList = d1501r37Service.printDataEdit(
                    s07t001td1501r40ResultListEntity, printDto.getShimedydm(),
                    printDto.getSkyshimeday(), D1501R00Constants.TYOHYO_KBN_SKYSHO,
                    printDto.getSystemDate(), sshdshhitoimage);

                if (HIKAE_SKYSHO_ONLY.equals(hikae) || HIKAE_HIKAE_AND_SKYSHO.equals(hikae)) {
                    // 請求書CSVリストにお支払明細書出力データを追加
                    skyshoCsv.addAll(meisaisyoPrintDataList);
                }
                if (HIKAE_HIKAE_ONLY.equals(hikae) || HIKAE_HIKAE_AND_SKYSHO.equals(hikae)) {
                    // 控えCSVリストにお支払明細書出力データ（控え）を追加
                    skyshoHikaeCsv.addAll(d1501r00Service.createHikae(meisaisyoPrintDataList,
                        D1501R00Constants.TITLE_INDEX_SHIHARAI_MEISAI));
                }
            }
        }
        Map<String, List<List<String>>> csvMap = new HashMap<String, List<List<String>>>();
        csvMap.put("請求書CSV", skyshoCsv);
        csvMap.put("請求書（控）CSV", skyshoHikaeCsv);
        return csvMap;
    }

    /**
     * お支払明細書データ抽出処理を実行し検索結果をListで返却する
     * 
     * @param printDto 請求書印刷DTO.
     * @param skysakicd 請求先コード
     * @return セレクト結果
     */
    private List<S07T001TD1501R40ListResultEntity> selectForD1501R40List(D1501R30PrintDto printDto,
        String skysakicd) {
        S07T001TD1501R40ListEntity entity =
            createS07T001TD1501R40ListEntity(printDto.getApkihoncd(), printDto.getShopcd(),
                skysakicd, printDto.getShimedydm(), printDto.getSkyshimeday());
        return s07t001Dao.selectForD1501R40List(entity);
    }

    /**
     * お支払明細書印刷データ取得Entityを作成する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param skysakicd 請求先コード
     * @param shimedydm 締年月
     * @param shimedd 締日
     * @return お支払明細書印刷データ取得Entity
     */
    private S07T001TD1501R40ListEntity createS07T001TD1501R40ListEntity(String apkihoncd,
        String shopcd, String skysakicd, String shimedydm, String shimedd) {
        S07T001TD1501R40ListEntity entity = new S07T001TD1501R40ListEntity();
        entity.setApkihoncd(apkihoncd);
        entity.setShopcd(shopcd);
        entity.setSkysakicd(skysakicd);
        entity.setShimedydm(shimedydm);
        entity.setShimedd(shimedd);
        return entity;
    }

    /**
     * ショップマスタを検索して検索結果リストから一つ目のレコードを返却する
     * 
     * @param printDto 請求書印刷DTO.
     * @return セレクト結果
     */
    private S02M046TEntity getShopMasterRecord(D1501R30PrintDto printDto) {
        List<S02M046TEntity> s02m046tEntityList = d1501r00Service.selectS02M046T(
            printDto.getApkihoncd(), printDto.getShopcd(), CommonConstants.YUKOKBN_1);
        return s02m046tEntityList.get(0);
    }

    /**
     * 消費税率管理マスタを検索して検索結果リストから一つ目の消費税率を返却する
     * 
     * @param printDto 請求書印刷DTO.
     * @param taxkbn 税区分
     * @return セレクト結果
     */
    private BigDecimal getTaxRate(D1501R30PrintDto printDto, String taxkbn) {
        List<S02M091TEntity> s02m091tEntityList =
            d1501r00Service.selectS02M091T(printDto.getShimedydm() + printDto.getSkyshimeday(),
                printDto.getShimedydm() + printDto.getSkyshimeday(), taxkbn);
        return s02m091tEntityList.get(0).getShohitax();
    }

    /**
     * 請求残高テーブルの更新
     * 
     * @param printDto 請求書印刷DTO.
     * @param skysakicd 請求先コード
     */
    private void updateS07T001T(D1501R30PrintDto printDto, String skysakicd) {
        S07T001TEntity entity = createS07T001TEntity(printDto, skysakicd);
        s07t001Dao.updateForD1501R30Printed(entity);
    }

    /**
     * 請求残高テーブルの更新Entity生成
     * 
     * @param printDto 請求書印刷DTO.
     * @param skysakicd 請求先コード
     * @return 請求残高テーブルの更新Entity
     */
    private S07T001TEntity createS07T001TEntity(D1501R30PrintDto printDto, String skysakicd) {
        S07T001TEntity entity = new S07T001TEntity();
        // AP基本コード
        entity.setApkihoncd(printDto.getApkihoncd());
        // ショップコード
        entity.setShopcd(printDto.getShopcd());
        // 請求先コード
        entity.setSkysakicd(skysakicd);
        // 締年月
        entity.setShimedydm(printDto.getShimedydm());
        // 締日
        entity.setShimedd(printDto.getSkyshimeday());
        // 請求書印刷日
        entity.setSkyshoprntdd(printDto.getUnyoymd());
        // 更新者AP基本コード
        entity.setKoshaapkihoncd(printDto.getLoginUserapkihoncd());
        // 更新者ショップコード
        entity.setKoshashopcd(printDto.getLoginUsershopcd());
        // 更新者コード
        entity.setKoshacd(printDto.getLoginUsertnshacd());
        // 更新システム日付
        entity.setKshnsystmymd(printDto.getSystemDate());
        // 更新システム時間
        entity.setKshnsystmjikan(printDto.getSystemTime());
        // 更新運用日付
        entity.setKshnunyoymd(printDto.getUnyoymd());
        // 更新IPアドレス
        entity.setKshnipadrse(printDto.getIpAddress());
        // 更新プログラムID
        entity.setKshnpgmid(getProcessId());
        return entity;

    }

    /**
     * 請求書出力データ抽出用Entityを生成する
     * 
     * @param printDto 請求書印刷DTO.
     * @param skysakicd 請求先コード
     * @return S07T001TD1501R30ListEntity
     */
    private S07T001TD1501R30ListEntity createS07T001TD1501R30ListEntity(D1501R30PrintDto printDto,
        String skysakicd) {
        S07T001TD1501R30ListEntity entity = new S07T001TD1501R30ListEntity();
        // AP基本コード
        entity.setApkihoncd(printDto.getApkihoncd());
        // ショップコード
        entity.setShopcd(printDto.getShopcd());
        // 請求先コード
        entity.setSkysakicd(skysakicd);
        // 締年月
        entity.setShimedydm(printDto.getShimedydm());
        // 締日
        entity.setShimedd(printDto.getSkyshimeday());
        return entity;
    }

    /**
     * 請求書出力データを返却する。
     * 
     * @param printDto 請求書印刷DTO.
     * @param skysakicd 請求先コード
     * @param skyshonyknmeisaiinjiFlg 請求書入金明細印字フラグ
     * @param invoiceEnforcementFlg インボイス施行フラグ
     * @return 請求書出力データのリスト
     */
    private List<S07T001TD1501R30ListResultEntity> selectForD1501R30List(D1501R30PrintDto printDto,
        String skysakicd, String skyshonyknmeisaiinjiFlg, String invoiceEnforcementFlg) {
        S07T001TD1501R30ListEntity entity = createS07T001TD1501R30ListEntity(printDto, skysakicd);
        return s07t001Dao.selectForD1501R30List(entity, skyshonyknmeisaiinjiFlg,
            VOUCHER_TYPE_SKYSHO, invoiceEnforcementFlg);
    }

    /**
     * 入金兼領収書印刷データ取得Entityを生成する
     * 
     * @param printDto 請求書印刷DTO.
     * @param skysakicd 請求先コード
     * @return S07T001TD1501R30ListEntity
     */
    private S07T001TD1501R35ListEntity createS07T001TD1501R35ListEntity(D1501R30PrintDto printDto,
        String skysakicd) {
        S07T001TD1501R35ListEntity entity = new S07T001TD1501R35ListEntity();
        // AP基本コード
        entity.setApkihoncd(printDto.getApkihoncd());
        // ショップコード
        entity.setShopcd(printDto.getShopcd());
        // 請求先コード
        entity.setSkysakicd(skysakicd);
        // 締年月
        entity.setShimedydm(printDto.getShimedydm());
        // 締日
        entity.setShimedd(printDto.getSkyshimeday());
        return entity;
    }

    /**
     * 入金兼領収書印刷データを返却する。
     * 
     * @param printDto 請求書印刷DTO.
     * @param skysakicd 請求先コード
     * @return 入金兼領収書印刷データのリスト
     */
    private List<S07T001TD1501R35ListResultEntity> selectForD1501R35List(D1501R30PrintDto printDto,
        String skysakicd) {
        S07T001TD1501R35ListEntity entity = createS07T001TD1501R35ListEntity(printDto, skysakicd);
        return s07t001Dao.selectForD1501R35List(entity);
    }
}
