package jp.co.aquaclara.accis.web.domain.service.d1501;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M006TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M006TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M091TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R30ListEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R30ListResultEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R40ListEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R40ListResultEntity;
import jp.co.aquaclara.accis.common.exception.BusinessException;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.domain.dto.d1501.D1501R40PrintDto;
import jp.co.aquaclara.accis.web.domain.dto.d1501.D1501R40TargetMeisaiDto;
import jp.co.aquaclara.accis.web.domain.service.common.OnlineBatchBaseService;
import jp.co.aquaclara.accis.web.domain.service.common.OnlineBatchCommonModel;
import jp.co.aquaclara.accis.web.domain.service.master.S02M006TService;

/**
 * ご利用明細書OnlineBatchService.
 * 
 * @author accisdev047
 *
 */
@Service
public class D1501R40Service extends OnlineBatchBaseService<D1501R40ParameterModel> {

    /**
     * 請求残高テーブルDao
     */
    @Autowired
    private S07T001TDao s07t001Dao;
    /**
     * システム管理マスタDao
     */
    @Autowired
    private S02M006TDao s02m006Dao;
    /**
     * 帳票出力 請求書（お支払明細書）サービスクラス
     */
    @Autowired
    private D1501R37Service d1501r37Service;

    /**
     * 帳票出力 ご利用明細書（今回引落予定額あり）サービスクラス
     */
    @Autowired
    private D1501R43Service d1501r43Service;

    /**
     * 帳票出力 ご利用明細書（今回引落予定額なし）サービスクラス
     */
    @Autowired
    private D1501R44Service d1501r44Service;

    /**
     * 帳票出力 共通サービスクラス
     */
    @Autowired
    private D1501R00Service d1501r00Service;

    /**
     * システム管理マスタ-Partsサービスクラス
     */
    @Autowired
    private S02M006TService s02m006tService;

    /**
     * 処理ID_引落予定額有
     */
    private static final String PROCESS_ID_HIKIOTOSHI_ARI = "D1501B06";

    /**
     * 処理ID_引落予定額無し
     */
    private static final String PROCESS_ID_HIKIOTOSHI_NASHI = "D1501B07";

    /**
     * ご利用明細書印刷DTO.控えの印刷：1(する)
     */
    private static final String HIKAE_PRINT_KBN_ENABLED = "1";
    /**
     * ご利用明細書印刷DTO.控えの印刷：0(しない)
     */
    private static final String HIKAE_PRINT_KBN_DISABLED = "0";

    /**
     * 控えの印刷：0(印刷しない)
     */
    private static final String PRINT_TYPE_MEISAI = "0";

    /**
     * 控えの印刷：2(印刷する)
     */
    private static final String PRINT_TYPE_MEISAI_AND_HIKAE = "2";

    /**
     * お知らせ伝票種別：6(ご利用明細書)
     */
    private static final String VOUCHER_TYPE_MEISAI = "6";

    /**
     * 入金明細印字フラグ：1（入金明細印刷する）
     */
    private static final String NYUKINMEISAI_PRINT_ENABLED = "1";

    /**
     * 引落予定額の印刷:1(する)
     */
    private static final String YOTEIGAKU_PRINT_ENABLED = "1";

    /**
     * 引落予定額の印刷:2(しない)
     */
    private static final String YOTEIGAKU_PRINT_DISABLED = "2";

    /**
     * プロセスID設定用 setProcessIdメソッド実行後値が入る
     */
    private String processId = "";


    @Override
    public String getProcessId() {
        return processId;
    }

    /**
     * ご利用明細書のオンラインバッチ処理
     * 
     * @param paramModel ご利用明細書出力ParameterModel.
     * @param commonInfo 非同期処理の共通情報クラスModel
     */
    @Override
    @Transactional
    protected void process(D1501R40ParameterModel paramModel, OnlineBatchCommonModel commonInfo)
        throws BusinessException, Exception {
        // ご利用明細書CSV
        List<List<String>> meisaiCsv = new ArrayList<>();
        // ご利用明細書（控え）CSV
        List<List<String>> meisaiHikaeCsv = new ArrayList<>();
        // ショップマスタの取得
        S02M046TEntity shopMasterRecord = getShopMasterRecord(paramModel.getPrintDto());
        // インボイス施行フラグの取得
        String invoiceEnforcementFlg =
            d1501r00Service.getInvoiceEnforcementFlg(paramModel.getPrintDto().getShimedydm(),
                paramModel.getPrintDto().getSkyshimeday(), shopMasterRecord.getInvoicshikoymd());
        // クレジット計上日の取得
        Integer creditpostingDate = getCreditPostingDate();
        // 消費税率管理マスタの取得(通常税率)
        BigDecimal noramlTaxRate =
            getTaxRate(paramModel.getPrintDto(), CommonConstants.KBN_TAX_KAZEI);
        // 消費税率管理マスタの取得(軽減税率)
        BigDecimal reducedTaxRate =
            getTaxRate(paramModel.getPrintDto(), CommonConstants.KBN_TAX_KEIGEN);
        // 消費税率を全角数字で％付きに変換
        String fullWidthNoramlTaxRate =
            ItemUtil.convertToFullWidthNumbers(noramlTaxRate.toString()) + "％";
        String fullWidthReducedTaxRate =
            ItemUtil.convertToFullWidthNumbers(reducedTaxRate.toString()) + "％";

        for (D1501R40TargetMeisaiDto dto : paramModel.getD1501w41TargetMeisaiDtoList()) {
            // 請求残高更新処理
            updateS07T001T(paramModel.getPrintDto(), dto.getSkysakicd());
            // 控え印刷チェック
            String hikae = getHikae(paramModel.getPrintDto().getHikaeprntkbn());
            // ご利用明細書出力データ抽出
            List<S07T001TD1501R30ListResultEntity> s07t001td1501r30ListResultEntity =
                selectForD1501R30List(paramModel.getPrintDto(), dto.getSkysakicd(),
                    invoiceEnforcementFlg);
            // 対象ショップの「お振込先」の印字区分の情報をシステム管理マスタより取得する。
            String prntpaykbn = createS02M006TEntity(paramModel.getPrintDto());

            // ご利用明細書中間データ作成
            ArrayList<List<String>> printDataList =
                printDataEdit(paramModel.getPrintDto(), s07t001td1501r30ListResultEntity,
                    creditpostingDate, fullWidthNoramlTaxRate, fullWidthReducedTaxRate,
                    ItemUtil.nullToString(shopMasterRecord.getSshdshhitoimage()),
                    invoiceEnforcementFlg, prntpaykbn);
            // ご利用明細書中間データを追加
            meisaiCsv.addAll(printDataList);
            // ご利用明細書中間データの控え作成
            if (PRINT_TYPE_MEISAI_AND_HIKAE.equals(hikae)) {
                meisaiHikaeCsv.addAll(d1501r00Service.createHikae(printDataList, 13));
            }
            // 請求先マスタを検索
            S02M017TEntity skysakiMasterRecord =
                d1501r00Service.selectS02M017T(paramModel.getPrintDto().getApkihoncd(),
                    paramModel.getPrintDto().getShopcd(), dto.getSkysakicd());
            // 変数.インボイス施行フラグ ＝ １ かつ 請求先マスタEntity.請求区分 ＝ '2'（親子）かつ 請求先マスタEntity.明細印刷区分 ＝
            // '1'（親子卸）以外の場合にお支払明細書の印刷を行う。
            if (D1501R00Constants.INVOICE_ENFORCEMENT_FLAG_ENABLED.equals(invoiceEnforcementFlg)
                && CommonConstants.SKYKBN_2.equals(skysakiMasterRecord.getSkykbn())
                && !CommonConstants.MEISAIPRNTKBN_ISSHO
                    .equals(skysakiMasterRecord.getMeisaiprntkbn())) {
                // お支払明細書データ出力処理
                List<S07T001TD1501R40ListResultEntity> s07t001td1501r40ResultListEntity =
                    selectForD1501R40List(paramModel.getPrintDto(), dto.getSkysakicd());
                // 帳票出力用にデータ成形
                ArrayList<List<String>> meisaisyoPrintDataList = d1501r37Service.printDataEdit(
                    s07t001td1501r40ResultListEntity, paramModel.getPrintDto().getShimedydm(),
                    paramModel.getPrintDto().getSkyshimeday(),
                    D1501R00Constants.TYOHYO_KBN_GORIYOMEISAI_HIKIOTOSHI_YOTEI_ARI,
                    paramModel.getPrintDto().getSystemDate(),
                    ItemUtil.nullToString(shopMasterRecord.getSshdshhitoimage()));
                meisaiCsv.addAll(meisaisyoPrintDataList);
                if (PRINT_TYPE_MEISAI_AND_HIKAE.equals(hikae)) {
                    // お支払明細書出力データ（控え）を追加
                    meisaiHikaeCsv.addAll(d1501r00Service.createHikae(meisaisyoPrintDataList, 14));
                }
            }
        }
        // CSV作成処理に渡す中間データList
        List<List<String>> csvData = new ArrayList<>();
        csvData.addAll(meisaiCsv);
        csvData.addAll(meisaiHikaeCsv);
        // 画像ファイル名設定
        List<String> imageFileNames = new ArrayList<>();
        imageFileNames.add(ItemUtil.nullToString(shopMasterRecord.getSshdshhitoimage()));
        // 帳票（PDFファイル）を出力する
        super.createReportFile(csvData, commonInfo.getFileNamne(), imageFileNames);
    }

    /**
     * ご利用明細書中間データ作成を実施する
     * 
     * @param printDto ご利用明細書中間データ
     * @param entity S07T001TD1501R30ListResultEntity
     * @param creditpostingDate クレジット計上日
     * @param fullWidthNoramlTaxRate 通常税率
     * @param fullWidthReducedTaxRate 軽減税率
     * @param sshdshhitoImage 差出人イメージ
     * @param prntpaykbn お振込先印刷有無
     * @return ご利用明細書中間データ
     */
    ArrayList<List<String>> printDataEdit(D1501R40PrintDto printDto,
        List<S07T001TD1501R30ListResultEntity> entity, int creditpostingDate,
        String fullWidthNoramlTaxRate, String fullWidthReducedTaxRate, String sshdshhitoImage,
        String invoiceEnforcementFlg, String prntpaykbn) {
        ArrayList<List<String>> printDataList = new ArrayList<List<String>>();
        if (YOTEIGAKU_PRINT_ENABLED.equals(printDto.getHikiotoshi())) {
            // 今回引落予定額有
            printDataList.addAll(d1501r43Service.printDataEdit(entity, printDto.getShimedydm(),
                printDto.getSkyshimeday(),
                D1501R00Constants.TYOHYO_KBN_GORIYOMEISAI_HIKIOTOSHI_YOTEI_ARI,
                invoiceEnforcementFlg, creditpostingDate, fullWidthNoramlTaxRate,
                fullWidthReducedTaxRate, D1501R00Constants.TRANSEFER_INFO_FLAG_ENABLED,
                printDto.getSystemDate(), sshdshhitoImage, prntpaykbn,
                printDto.getTairyuoverprntkbn()));
        } else if (YOTEIGAKU_PRINT_DISABLED.equals(printDto.getHikiotoshi())) {
            // 今回引落予定額無
            printDataList.addAll(d1501r44Service.printDataEdit(entity, printDto.getShimedydm(),
                printDto.getSkyshimeday(),
                D1501R00Constants.TYOHYO_KBN_GORIYOMEISAI_HIKIOTOSHI_YOTEI_NASHI,
                invoiceEnforcementFlg, creditpostingDate, fullWidthNoramlTaxRate,
                fullWidthReducedTaxRate, D1501R00Constants.TRANSEFER_INFO_FLAG_ENABLED,
                printDto.getSystemDate(), sshdshhitoImage, prntpaykbn,
                printDto.getTairyuoverprntkbn()));
        }
        return printDataList;
    }

    /**
     * ショップマスタを検索して検索結果リストから一つ目のレコードを返却する
     * 
     * @param printDto ご利用明細書印刷DTO.
     * @return セレクト結果
     */
    private S02M046TEntity getShopMasterRecord(D1501R40PrintDto printDto) {
        List<S02M046TEntity> s02m046tEntityList = d1501r00Service.selectS02M046T(
            printDto.getApkihoncd(), printDto.getShopcd(), CommonConstants.YUKOKBN_1);
        return s02m046tEntityList.get(0);
    }

    /**
     * クレジット計上日をシステム管理マスタより取得する
     * 
     * @return クレジット計上日
     */
    public Integer getCreditPostingDate() {
        return s02m006tService.searchForSuchi("000", "000", "238", "1");
    }

    /**
     * 消費税率管理マスタを検索して検索結果リストから一つ目の消費税率を返却する
     * 
     * @param printDto ご利用明細書印刷DTO.
     * @param taxkbn 税区分
     * @return 消費税率
     */
    private BigDecimal getTaxRate(D1501R40PrintDto printDto, String taxkbn) {
        String skyshimeDate = printDto.getShimedydm() + printDto.getSkyshimeday();
        List<S02M091TEntity> s02m091tEntityList =
            d1501r00Service.selectS02M091T(skyshimeDate, skyshimeDate, taxkbn);
        return s02m091tEntityList.get(0).getShohitax();
    }

    /**
     * 請求残高テーブルの更新
     * 
     * @param printDto ご利用明細書印刷DTO.
     * @param skysakicd 請求先コード
     */
    private void updateS07T001T(D1501R40PrintDto printDto, String skysakicd) {
        S07T001TEntity entity = createS07T001TEntity(printDto, skysakicd);
        s07t001Dao.updateForD1501R40Printed(entity);
    }

    /**
     * 請求残高テーブルの更新Entity生成
     * 
     * @param printDto ご利用明細書印刷DTO.
     * @param skysakicd 請求先コード
     * @return 請求残高テーブルの更新Entity
     */
    private S07T001TEntity createS07T001TEntity(D1501R40PrintDto printDto, String skysakicd) {
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
        // ご利用明細書印刷日
        entity.setRiyomeisaishoprntdd(printDto.getUnyoymd());
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
     * 控え印刷チェックを実施
     * 
     * @param hikaePrintKbn 控えの印刷種別
     * @return 0（控え印刷なし） 2（控え印刷あり）
     */
    private String getHikae(String hikaePrintKbn) {
        String result = "";
        if (HIKAE_PRINT_KBN_ENABLED.equals(hikaePrintKbn)) {
            result = PRINT_TYPE_MEISAI_AND_HIKAE;
        } else if (HIKAE_PRINT_KBN_DISABLED.equals(hikaePrintKbn)) {
            result = PRINT_TYPE_MEISAI;
        }
        return result;
    }

    /**
     * 請求書出力データ抽出用Entityを生成する
     * 
     * @param printDto ご利用明細書印刷DTO.
     * @param skysakicd 請求先コード
     * @return S07T001TD1501R30ListEntity
     */
    private S07T001TD1501R30ListEntity createS07T001TD1501R30ListEntity(D1501R40PrintDto printDto,
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
     * @param printDto printDto ご利用明細書印刷DTO.
     * @param skysakicd 請求先コード
     * @param invoiceEnforcementFlg インボイス施行フラグ
     * @return 請求書出力データのリスト
     */
    private List<S07T001TD1501R30ListResultEntity> selectForD1501R30List(D1501R40PrintDto printDto,
        String skysakicd, String invoiceEnforcementFlg) {
        S07T001TD1501R30ListEntity entity = createS07T001TD1501R30ListEntity(printDto, skysakicd);
        return s07t001Dao.selectForD1501R30List(entity, NYUKINMEISAI_PRINT_ENABLED,
            VOUCHER_TYPE_MEISAI, invoiceEnforcementFlg);
    }

    /**
     * お支払明細書データ抽出処理を実行し検索結果をListで返却する
     * 
     * @param printDto ご利用明細書印刷DTO.
     * @param skysakicd 請求先コード
     * @return セレクト結果
     */
    private List<S07T001TD1501R40ListResultEntity> selectForD1501R40List(D1501R40PrintDto printDto,
        String skysakicd) {
        S07T001TD1501R40ListEntity entity =
            createS07T001TD1501R40ListEntityEntity(printDto.getApkihoncd(), printDto.getShopcd(),
                skysakicd, printDto.getShimedydm(), printDto.getSkyshimeday());
        return s07t001Dao.selectForD1501R40List(entity);
    }

    /**
     * お支払明細書データ抽出処理を実行し検索結果をListで返却する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param skysakicd 請求先コード
     * @return セレクト結果
     */
    private S07T001TD1501R40ListEntity createS07T001TD1501R40ListEntityEntity(String apkihoncd,
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
     * 画面のラジオボタン「引落予定額の印刷」の設定値を元にプロセスＩＤの設定を行う
     * 
     * @param radioButtonValue ラジオボタンの設定値
     */
    public void setProcessId(String radioButtonValue) {
        if (YOTEIGAKU_PRINT_ENABLED.equals(radioButtonValue)) {
            processId = PROCESS_ID_HIKIOTOSHI_ARI;
        } else if (YOTEIGAKU_PRINT_DISABLED.equals(radioButtonValue)) {
            processId = PROCESS_ID_HIKIOTOSHI_NASHI;
        }
    }

    /**
     * システム管理マスタより「お振込先」の印字区分を取得する。
     * 
     * @param printDto
     * @return お振込先印字区分（0：印字しない、1:印字する）
     */
    private String createS02M006TEntity(D1501R40PrintDto printDto) {

        S02M006TEntity s02m006t = new S02M006TEntity();
        s02m006t.setApkihoncd(printDto.getApkihoncd());
        s02m006t.setShopcd(printDto.getShopcd());
        s02m006t.setRcrdkbn(CommonConstants.RECORD_KBN_M);
        s02m006t.setSetteino(CommonConstants.SYS_SETTEINO_270);
        s02m006t.setSetteicd(CommonConstants.FLG_ON);
        s02m006t = s02m006Dao.selectByPrimaryKey(s02m006t);

        // システム管理マスタからお振込先印字区分を取得出来なかった場合、NULLを返却
        if (StringUtils.isEmpty(s02m006t)) {
            return null;
        }

        return s02m006t.getMojisetteiatai();
    }
}
