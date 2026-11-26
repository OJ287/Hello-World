package jp.co.aquaclara.accis.web.domain.service.d1501;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.constants.SystemConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R33ListEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R33ListResultEntity;
import jp.co.aquaclara.accis.common.exception.BusinessException;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.common.util.ListCreatorUtil;
import jp.co.aquaclara.accis.web.domain.dto.b0503.D0503W02VirtualAccountDto;
import jp.co.aquaclara.accis.web.domain.dto.d1501.D1501R30PrintDto;
import jp.co.aquaclara.accis.web.domain.dto.d1501.D1501R30TargetMeisaiDto;
import jp.co.aquaclara.accis.web.domain.service.b0503.B0503W02VirtualAccountService;
import jp.co.aquaclara.accis.web.domain.service.common.OnlineBatchBaseService;
import jp.co.aquaclara.accis.web.domain.service.common.OnlineBatchCommonModel;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;

/**
 * 帳票出力 請求書（CSV）-Service.
 * 
 * @author accisdev047
 *
 */
@Service
public class D1501R33Service extends OnlineBatchBaseService<D1501R33ParameterModel> {
    /**
     * 帳票出力 共通サービスクラス
     */
    @Autowired
    private D1501R00Service d1501r00Service;

    /** バーチャル口座サービス */
    @Autowired
    private B0503W02VirtualAccountService vaService;

    /**
     * 請求残高テーブルDao
     */
    @Autowired
    private S07T001TDao s07t001Dao;

    /**
     * 売上入金区分：0(得意先名表示行)
     */
    private static final String NYUKIN_URIAGE_KBN_TOKUISAKI_HYOUJI = "0";
    /**
     * 売上入金区分：1(売上明細)
     */
    private static final String NYUKIN_URIAGE_KBN_URIAGE_MEISAI = "1";
    /**
     * 売上入金区分：2(売上明細)
     */
    private static final String NYUKIN_URIAGE_KBN_NYUKIN_GOUKEI = "2";
    /**
     * 売上入金区分：3(入金明細)
     */
    private static final String NYUKIN_URIAGE_KBN_NYUKIN_MEISAI = "3";
    /**
     * 行種別:1(得意先名表示行)
     */
    private static final String ROW_TYPE_TOKUISAKI_HYOUJI = "1";
    /**
     * 行種別:2(売上明細表示行)
     */
    private static final String ROW_TYPE_URIAGE_MEISAI_HYOUJI = "2";
    /**
     * 行種別:3(売上合計表示行)
     */
    private static final String ROW_TYPE_URIAGE_GOUKEI_HYOUJI = "3";
    /**
     * 行種別:4(入金明細表示行)
     */
    private static final String ROW_TYPE_NYUKIN_MEISAI_HYOUJI = "4";
    /**
     * 明細区分：0(得意先名表示行)
     */
    private static final String MEISAI_KBN_TOKUSAKSAKI = "0";
    /**
     * 明細区分：1(売上明細、入金明細)
     */
    private static final String MEISAI_KBN_URIAGE_NYUKIN_MEISAI = "1";
    /**
     * 明細区分：2(売上合計、入金合計)
     */
    private static final String MEISAI_KBN_URIAGE_NYUKIN_GOUKEI = "2";
    /**
     * 明細区分：4(請求先単位の集計欄表示行)
     */
    private static final String MEISAI_KBN_SYUKEI = "4";
    /**
     * 明細区分：5(8%税金額表示行)
     */
    private static final String MEISAI_KBN_8PER_HYOUJI = "5";
    /**
     * 明細区分：6(10%税金額表示行)
     */
    private static final String MEISAI_KBN_10PER_HYOUJI = "6";
    /**
     * 明細区分：7(非課税売上表示行)
     */
    private static final String MEISAI_KBN_HIKAZEI_HYOUJI = "7";
    /**
     * 明細区分：8(不課税売上表示行)
     */
    private static final String MEISAI_KBN_HUKAZEI_HYOUJI = "8";
    /**
     * 空白表示用の定数
     */
    private static final String CSV_BLANK = "";
    /**
     * ゼロ表示用の定数
     */
    private static final String CSV_ZERO = "0";

    /**
     * 処理ID
     */
    private static final String PROCESS_ID = "D1501B04";

    /**
     * 請求書印刷DTO.印刷種類２：1(ON)
     */
    private static final String HIKAE_PRINT_KBN_ENABLED = "1";


    /**
     * プロセスIDを返却する
     * 
     * @return プロセスID
     */
    @Override
    public String getProcessId() {
        return PROCESS_ID;
    }

    /**
     * CSV作成処理
     * 
     * @param paramModel 請求書（CSV）出力ParameterModel.
     * @param commonInfo 非同期処理の共通情報クラスModel
     */
    @Override
    protected void process(D1501R33ParameterModel paramModel, OnlineBatchCommonModel commonInfo)
        throws BusinessException, Exception {
        // 請求書（CSV）中間データリスト
        List<List<String>> csvDataList = new ArrayList<List<String>>();
        // 控え中間データリスト
        List<List<String>> hikaeDataList = new ArrayList<List<String>>();
        // ショップマスタの取得
        S02M046TEntity shopMasterRecord = getShopMasterRecord(paramModel.getPrintDto());
        // インボイス施行フラグの取得
        String invoiceEnforcementFlg =
            d1501r00Service.getInvoiceEnforcementFlg(paramModel.getPrintDto().getShimedydm(),
                paramModel.getPrintDto().getSkyshimeday(), shopMasterRecord.getInvoicshikoymd());
        // 差出人イメージ
        String fromImage = shopMasterRecord.getSshdshhitoimage();
        for (D1501R30TargetMeisaiDto dto : paramModel.getD1501r30TargetMeisaiDtoList()) {
            // 請求残高テーブルの更新
            updateS07T001T(paramModel.getPrintDto(), dto.getSkysakicd());
            // 請求書（CSV）出力データ抽出
            List<S07T001TD1501R33ListResultEntity> list =
                selectForD1501R33List(paramModel.getPrintDto(), dto.getSkysakicd());
            // Csv出力用データの成形
            csvDataList.addAll(printDataEdit(paramModel.getPrintDto(), list, invoiceEnforcementFlg,
                fromImage, D1501R00Constants.HIKAE_OUTPUT_FLAG_DISABLED));
            if (HIKAE_PRINT_KBN_ENABLED.equals(paramModel.getPrintDto().getHikaeprntkbn())) {
                // 控えCSVリストに請求書出力データ（控え）を追加
                hikaeDataList.addAll(printDataEdit(paramModel.getPrintDto(), list,
                    invoiceEnforcementFlg, fromImage, D1501R00Constants.HIKAE_OUTPUT_FLAG_ENABLED));
            }
        }
        // 請求書中間データに控えの中間データを追加
        csvDataList.addAll(hikaeDataList);
        // CSVを出力する
        super.createCsvFile(csvDataList, commonInfo.getFileNamne());
    }

    /**
     * 請求書CSVデータ取得
     * 
     * @param printDto 請求書印刷DTO.
     * @param skysakicd 請求先コード
     */
    private List<S07T001TD1501R33ListResultEntity> selectForD1501R33List(D1501R30PrintDto printDto,
        String skysakicd) {
        S07T001TD1501R33ListEntity entity =
            createS07T001TD1501R33ListEntity(printDto.getApkihoncd(), printDto.getShopcd(),
                skysakicd, printDto.getShimedydm(), printDto.getSkyshimeday());
        return s07t001Dao.selectForD1501R33List(entity);
    }

    /**
     * 請求書CSVデータ取得のEntity作成
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param skysakicd 請求先コード
     * @param shimedydm 締年月
     * @param shimedd 締日
     * @return 請求書CSVデータ取得Entity
     */
    private S07T001TD1501R33ListEntity createS07T001TD1501R33ListEntity(String apkihoncd,
        String shopcd, String skysakicd, String shimedydm, String shimedd) {
        S07T001TD1501R33ListEntity entity = new S07T001TD1501R33ListEntity();
        // AP基本コード
        entity.setApkihoncd(apkihoncd);
        // ショップコード
        entity.setShopcd(shopcd);
        // 請求先コード
        entity.setSkysakicd(skysakicd);
        // 締年月
        entity.setShimedydm(shimedydm);
        // 締日
        entity.setShimedd(shimedd);
        return entity;
    }

    /**
     * ショップマスタを検索して検索結果リストから一つ目のレコードを返却する
     * 
     * @param printDto 請求関連処理（請求書）-請求書印刷DTO.
     * @return セレクト結果
     */
    private S02M046TEntity getShopMasterRecord(D1501R30PrintDto printDto) {
        List<S02M046TEntity> s02m046tEntityList = d1501r00Service.selectS02M046T(
            printDto.getApkihoncd(), printDto.getShopcd(), CommonConstants.YUKOKBN_1);
        return s02m046tEntityList.get(0);
    }

    /**
     * 請求残高テーブルの更新
     * 
     * @param printDto 請求関連処理（請求書）-請求書印刷DTO.
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
     * 請求書出力用データ編集処理
     * 
     * @param s07t001td1501r33ListResultEntityList 請求書CSVデータ取得結果Entityリスト
     * @param invoiceEnforcementFlg インボイス施行フラグ
     * @param fromImage 差出人イメージ
     * @param hikaeOutputFlg 控え出力フラグ
     * @return 請求書CSVデータList
     */
    public List<List<String>> printDataEdit(D1501R30PrintDto printDto,
        List<S07T001TD1501R33ListResultEntity> s07t001td1501r33ListResultEntityList,
        String invoiceEnforcementFlg, String fromImage, String hikaeOutputFlg) {
        // 出力データリスト
        List<List<String>> outputDataList = new ArrayList<>();
        // データmapリスト
        List<HashMap<String, Object>> mapList = new ArrayList<>();
        // wk前回ご請求額
        BigDecimal wkZnkKuriKoshiKin = new BigDecimal("0");
        // wkご入金額
        BigDecimal wkKonkaiNyknKin = new BigDecimal("0");
        // wk消費税金額
        BigDecimal wkKonkaiShohiTaxKin = new BigDecimal("0");
        // wk今回ご請求額
        BigDecimal wkKurikoshiKin = new BigDecimal("0");
        // wk税抜売上金額
        BigDecimal wkZeinukiKin = new BigDecimal("0");
        // wk得意先コード
        String wktokuisakicd = null;
        for (S07T001TD1501R33ListResultEntity entity : s07t001td1501r33ListResultEntityList) {
            // ACCIS2023-7283 バーチャル口座対応
            // 請求先のバーチャル口座情報を取得し、利用する場合振込先関連項目の値を
            // ショップマスタから取得した内容ではなくバーチャル口座マスタの内容に上書き
            D0503W02VirtualAccountDto vaDto = vaService.getVirtualAccount(printDto.getApkihoncd(),
                printDto.getShopcd(), entity.getSkysakicd());
            if (vaDto.isUsed()) {
                // 銀行名称１ … バーチャル口座の金融機関名称と金融機関支店名称を全角スペースで連結した文字列を設定
                entity.setKinyukknnm(vaService.getBankName(vaDto));
                // 支店名称１ … バーチャル口座の口座種別、口座番号、口座名義を全角スペースで連結した文字列を設定
                entity.setKinyukknshitennm(vaService.getBranchName(vaDto));
                // 口座種別１、口座番号１、銀行名称２、支店名称２、口座種別２、口座番号２はクリアする
                entity.setKinyukknkzkind(SystemConstants.WORD_BLANK);
                entity.setKinyukknkzno(SystemConstants.WORD_BLANK);
                entity.setKinyukknn2nm(SystemConstants.WORD_BLANK);
                entity.setKinyukknn2shitennm(SystemConstants.WORD_BLANK);
                entity.setKinyukknn2kzkind(SystemConstants.WORD_BLANK);
                entity.setKinyukknn2kzno(SystemConstants.WORD_BLANK);
            }

            // 請求先単位の合計額を集計
            if (wktokuisakicd == null) {
                wkZnkKuriKoshiKin =
                    getSum(wkZnkKuriKoshiKin, new BigDecimal(entity.getZnkkurikoshikin()));
                wkKonkaiNyknKin =
                    getSum(wkKonkaiNyknKin, new BigDecimal(entity.getKonkainyknkin()));
                wkZeinukiKin =
                    getSum(wkZeinukiKin, new BigDecimal(entity.getKonkaitaxnukiurikin()));
                wkKonkaiShohiTaxKin =
                    getSum(wkKonkaiShohiTaxKin, new BigDecimal(entity.getKonkaishohitaxkin()));
                wkKurikoshiKin = getSum(wkKurikoshiKin, new BigDecimal(entity.getKurikoshikin()));
                wktokuisakicd = ItemUtil.nullToString(entity.getTokuisakicd());
            }
            // 請求先単位の合計額を集計
            if (!wktokuisakicd.equals(entity.getTokuisakicd())) {
                wkZnkKuriKoshiKin =
                    getSum(wkZnkKuriKoshiKin, new BigDecimal(entity.getZnkkurikoshikin()));
                wkKonkaiNyknKin =
                    getSum(wkKonkaiNyknKin, new BigDecimal(entity.getKonkainyknkin()));
                wkZeinukiKin =
                    getSum(wkZeinukiKin, new BigDecimal(entity.getKonkaitaxnukiurikin()));
                wkKonkaiShohiTaxKin =
                    getSum(wkKonkaiShohiTaxKin, new BigDecimal(entity.getKonkaishohitaxkin()));
                wkKurikoshiKin = getSum(wkKurikoshiKin, new BigDecimal(entity.getKurikoshikin()));
                wktokuisakicd = ItemUtil.nullToString(entity.getTokuisakicd());
            }
            // 請求書（CSV)出力データEntity の値をmap変数へ格納する
            mapList.add(setHashMap(entity));
        }
        // 出力データの初期化
        int lineCount = 1;
        // wk明細用カウンター
        int wkMeisaiCounter = 0;
        // 編集対象の業種別
        String rowType = ROW_TYPE_TOKUISAKI_HYOUJI;
        // wk金額集計
        BigDecimal wkSumKin = new BigDecimal("0");
        // wk消費税集計
        BigDecimal wkSumShohiTaxKin = new BigDecimal("0");
        // wk非課税売上合計
        BigDecimal wkSumHikazeiKin = new BigDecimal("0");
        // wk不課税売上合計
        BigDecimal wkSumHukazeiKin = new BigDecimal("0");
        // wk8%税抜売上合計
        BigDecimal wkN8PerZeinukiUriKin = new BigDecimal("0");
        // wk8%消費税合計
        BigDecimal wkN8PerSumShohiTaxKin = new BigDecimal("0");
        // wk10%税抜売上合計
        BigDecimal wkN10PerZeinukiUriKin = new BigDecimal("0");
        // wk10%消費税合計
        BigDecimal wkN10PerSumShohiTaxKin = new BigDecimal("0");

        for (int i = 0; i < mapList.size(); i++) {
            HashMap<String, Object> hashMap = (HashMap<String, Object>) mapList.get(i);
            // 親子一緒の場合の金額集計
            if (D1501R00Constants.INVOICE_ENFORCEMENT_FLAG_DISABLED.equals(invoiceEnforcementFlg)
                && CommonConstants.MEISAIPRNTKBN_ISSHO
                    .equals(hashMap.get("MEISAIPRNTKBN").toString())) {
                // 前回ご請求額
                hashMap.put("ZNKKURIKOSHIKIN", wkZnkKuriKoshiKin);
                // ご入金額
                hashMap.put("KONKAINYKNKIN", wkKonkaiNyknKin);
                // 税抜売上金額
                hashMap.put("KONKAITAXNUKIURIKIN", wkZeinukiKin);
                // 消費税額
                hashMap.put("KONKAISHOHITAXKIN", wkKonkaiShohiTaxKin);
                // 今回ご請求額
                hashMap.put("KURIKOSHIKIN", wkKurikoshiKin);
            }
            // 得意先名表示行の処理
            if (ROW_TYPE_TOKUISAKI_HYOUJI.equals(rowType)) {
                // CSV作成処理
                int addRowNum = createCsv(printDto, outputDataList, hashMap, MEISAI_KBN_TOKUSAKSAKI,
                    NYUKIN_URIAGE_KBN_TOKUISAKI_HYOUJI, lineCount, invoiceEnforcementFlg,
                    hikaeOutputFlg, fromImage);
                lineCount = lineCount + addRowNum;
                // 変数設定
                rowType = ROW_TYPE_URIAGE_MEISAI_HYOUJI;
                if (StringUtils.isEmpty(hashMap.get("FLAG2").toString())) {
                    // 売上明細データ無しパターン
                    rowType = ROW_TYPE_URIAGE_GOUKEI_HYOUJI;
                }
            }
            // 売上明細表示行の処理
            if (ROW_TYPE_URIAGE_MEISAI_HYOUJI.equals(rowType)
                && "1".equals(hashMap.get("FLAG1").toString())) {
                // 消費税レコードでない場合売上明細行の出力処理を実行する
                if (D1501R00Constants.TAX_RECORD_FLAG_NOT_TAX
                    .equals(hashMap.get("TAXRECFLG").toString())) {
                    // 枝番号の編集
                    if (i == 0) {
                        wkMeisaiCounter = 1;
                    } else if (i > 0 && mapList.get(i - 1).get("URIDNPYNO").toString()
                        .equals(hashMap.get("URIDNPYNO").toString())) {
                        wkMeisaiCounter++;
                    } else {
                        wkMeisaiCounter = 1;
                    }
                    // 枝番設定
                    hashMap.put("URIDNPYMEISAIEDANO", getEdaNo(wkMeisaiCounter));
                    // CSV作成処理
                    int addRowNum = createCsv(printDto, outputDataList, hashMap,
                        MEISAI_KBN_URIAGE_NYUKIN_MEISAI, NYUKIN_URIAGE_KBN_URIAGE_MEISAI, lineCount,
                        invoiceEnforcementFlg, hikaeOutputFlg, fromImage);
                    // 追加行数を加算
                    lineCount = lineCount + addRowNum;
                }
                // 売上金額集計
                if (!CommonConstants.NYURYOKUMEISAIFLG_0
                    .equals(hashMap.get("NYURYOKUMEISAIFLG").toString())) {
                    // wk金額集計に金額明細を加算
                    wkSumKin = getSum(wkSumKin, (BigDecimal) hashMap.get("KIN"));
                    // wk消費税集計に消費税明細を加算
                    wkSumShohiTaxKin =
                        getSum(wkSumShohiTaxKin, (BigDecimal) hashMap.get("SHOHITAXKIN"));
                }

                // 税率別売上集計
                if (!CommonConstants.NYURYOKUMEISAIFLG_0
                    .equals(hashMap.get("NYURYOKUMEISAIFLG").toString())) {
                    if (CommonConstants.KBN_TAX_KAZEI.equals(hashMap.get("TAXKBN").toString())) {
                        // wk10%税抜売上合計に金額明細を加算
                        wkN10PerZeinukiUriKin =
                            getSum(wkN10PerZeinukiUriKin, (BigDecimal) hashMap.get("KIN"));
                        // wk10%消費税合計に消費税明細を加算
                        wkN10PerSumShohiTaxKin =
                            getSum(wkN10PerSumShohiTaxKin, (BigDecimal) hashMap.get("SHOHITAXKIN"));
                    }
                    if (CommonConstants.KBN_TAX_KEIGEN.equals(hashMap.get("TAXKBN").toString())) {
                        // wk8%税抜売上合計に金額明細を加算
                        wkN8PerZeinukiUriKin =
                            getSum(wkN8PerZeinukiUriKin, (BigDecimal) hashMap.get("KIN"));
                        // wk8%消費税合計に消費税明細を加算
                        wkN8PerSumShohiTaxKin =
                            getSum(wkN8PerSumShohiTaxKin, (BigDecimal) hashMap.get("SHOHITAXKIN"));
                    }
                    if (CommonConstants.KBN_TAX_HIKAZEI.equals(hashMap.get("TAXKBN").toString())) {
                        // wk非課税売上合計に金額明細を加算
                        wkSumHikazeiKin = getSum(wkSumHikazeiKin, (BigDecimal) hashMap.get("KIN"));
                    }
                    if (CommonConstants.KBN_TAX_HUKAZEI.equals(hashMap.get("TAXKBN").toString())) {
                        // wk不課税売上合計に金額明細を加算
                        wkSumHukazeiKin = getSum(wkSumHukazeiKin, (BigDecimal) hashMap.get("KIN"));
                    }
                }

                if (CommonConstants.NYURYOKUMEISAIFLG_0
                    .equals(hashMap.get("NYURYOKUMEISAIFLG").toString())
                    && CommonConstants.KBN_TAX_KOMEISAI
                        .equals(hashMap.get("STTAXKBN").toString())) {
                    if (CommonConstants.KBN_TAX_KAZEI.equals(hashMap.get("TAXKBN").toString())) {
                        // wk10%税抜売上合計に金額明細を加算
                        wkN10PerZeinukiUriKin =
                            getSum(wkN10PerZeinukiUriKin, (BigDecimal) hashMap.get("KIN"));
                        // wk10%消費税合計に消費税明細を加算
                        wkN10PerSumShohiTaxKin =
                            getSum(wkN10PerSumShohiTaxKin, (BigDecimal) hashMap.get("SHOHITAXKIN"));
                    }
                    if (CommonConstants.KBN_TAX_KEIGEN.equals(hashMap.get("TAXKBN").toString())) {
                        // wk8%税抜売上合計に金額明細を加算
                        wkN8PerZeinukiUriKin =
                            getSum(wkN8PerZeinukiUriKin, (BigDecimal) hashMap.get("KIN"));
                        // wk8%消費税合計に消費税明細を加算
                        wkN8PerSumShohiTaxKin =
                            getSum(wkN8PerSumShohiTaxKin, (BigDecimal) hashMap.get("SHOHITAXKIN"));
                    }
                }

                // 次のデータが入金の場合、売上合計表示行を出力するよう、行種別を設定する
                if (isUriageGoukeiHyouji(i, mapList)) {
                    rowType = ROW_TYPE_URIAGE_GOUKEI_HYOUJI;
                }
            }

            // 売上合計表示行の処理
            if (ROW_TYPE_URIAGE_GOUKEI_HYOUJI.equals(rowType)) {
                // 出力項目の編集
                hashMap.put("SUMKIN", wkSumKin);
                hashMap.put("SUMSHOHITAXKIN", wkSumShohiTaxKin);
                hashMap.put("SHNNM", "");
                // CSV作成処理
                int addRowNum = createCsv(printDto, outputDataList, hashMap,
                    MEISAI_KBN_URIAGE_NYUKIN_GOUKEI, NYUKIN_URIAGE_KBN_URIAGE_MEISAI, lineCount,
                    invoiceEnforcementFlg, hikaeOutputFlg, fromImage);
                // 変数設定
                lineCount = lineCount + addRowNum;
                wkSumKin = new BigDecimal("0");
                wkSumShohiTaxKin = new BigDecimal("0");
                wkMeisaiCounter = 0;
                rowType = ROW_TYPE_NYUKIN_MEISAI_HYOUJI;
                if (isLastData(i, mapList)) {
                    // 税率別合計の出力
                    addRowNum = setTaxRateTotal(printDto, outputDataList, hashMap,
                        wkN8PerZeinukiUriKin, wkN8PerSumShohiTaxKin, wkN10PerZeinukiUriKin,
                        wkN10PerSumShohiTaxKin, wkSumHikazeiKin, wkSumHukazeiKin, lineCount,
                        invoiceEnforcementFlg, hikaeOutputFlg, fromImage);
                    lineCount = lineCount + addRowNum;
                }
                continue;
            }

            // 入金明細表示行の処理
            if (ROW_TYPE_NYUKIN_MEISAI_HYOUJI.equals(rowType)) {
                // 売上入金のFLAG2が空でない場合、入金明細行を出力する
                if (!"".equals(hashMap.get("FLAG2").toString())) {
                    // 枝番号の編集
                    if (i == 0) {
                        wkMeisaiCounter = 1;
                    } else if (i > 0 && mapList.get(i - 1).get("URIDNPYNO").toString()
                        .equals(hashMap.get("URIDNPYNO").toString())) {
                        wkMeisaiCounter++;
                    } else {
                        wkMeisaiCounter = 1;
                    }
                    // 枝番設定
                    hashMap.put("URIDNPYMEISAIEDANO", getEdaNo(wkMeisaiCounter));
                    if (!CommonConstants.SKYHOHOKBN_TOUGETSU.equals(hashMap.get("SKYHOHOKBN"))) {
                        // CSV作成処理
                        int addRowNum = createCsv(printDto, outputDataList, hashMap,
                            MEISAI_KBN_URIAGE_NYUKIN_MEISAI, NYUKIN_URIAGE_KBN_NYUKIN_MEISAI,
                            lineCount, invoiceEnforcementFlg, hikaeOutputFlg, fromImage);
                        // 追加行数を加算
                        lineCount = lineCount + addRowNum;
                    }
                    // wk金額集計に金額明細を加算
                    wkSumKin = getSum(wkSumKin, (BigDecimal) hashMap.get("KIN"));
                    // wk消費税集計を初期化
                    wkSumShohiTaxKin = new BigDecimal("0");
                }

                // 入金合計を出力
                if (isNyukinGoukei(i, hashMap.get("FLAG2").toString(), mapList)) {
                    // 出力項目の編集
                    hashMap.put("SUMKIN", wkSumKin);
                    hashMap.put("SUMSHOHITAXKIN", wkSumShohiTaxKin);
                    hashMap.put("SHNNM", "");
                    if (!CommonConstants.SKYHOHOKBN_TOUGETSU
                        .equals(hashMap.get("SKYHOHOKBN").toString())) {
                        // CSV作成処理
                        int addRowNum = createCsv(printDto, outputDataList, hashMap,
                            MEISAI_KBN_URIAGE_NYUKIN_GOUKEI, NYUKIN_URIAGE_KBN_NYUKIN_GOUKEI,
                            lineCount, invoiceEnforcementFlg, hikaeOutputFlg, fromImage);
                        // 追加行数を加算
                        lineCount = lineCount + addRowNum;
                    }
                }


                if (isTaxRateTotal(i, hashMap.get("FLAG2").toString(), mapList)) {
                    // 税率別合計の出力
                    int addRowNum = setTaxRateTotal(printDto, outputDataList, hashMap,
                        wkN8PerZeinukiUriKin, wkN8PerSumShohiTaxKin, wkN10PerZeinukiUriKin,
                        wkN10PerSumShohiTaxKin, wkSumHikazeiKin, wkSumHukazeiKin, lineCount,
                        invoiceEnforcementFlg, hikaeOutputFlg, fromImage);
                    // 追加行数を加算
                    lineCount = lineCount + addRowNum;
                    // 変数設定
                    wkMeisaiCounter = 0;
                    wkSumKin = new BigDecimal("0");
                    wkSumShohiTaxKin = new BigDecimal("0");
                    wkSumHikazeiKin = new BigDecimal("0");
                    wkSumHukazeiKin = new BigDecimal("0");
                    wkN8PerZeinukiUriKin = new BigDecimal("0");
                    wkN8PerSumShohiTaxKin = new BigDecimal("0");
                    wkN10PerZeinukiUriKin = new BigDecimal("0");
                    wkN10PerSumShohiTaxKin = new BigDecimal("0");
                    rowType = ROW_TYPE_TOKUISAKI_HYOUJI;
                    if (D1501R00Constants.INVOICE_ENFORCEMENT_FLAG_ENABLED
                        .equals(invoiceEnforcementFlg)
                        || CommonConstants.MEISAIPRNTKBN_KOBETSU
                            .equals(hashMap.get("MEISAIPRNTKBN").toString())) {
                        lineCount = 1;
                    }
                }
            }
        }
        if (outputDataList.size() == 0) {
            throw BusinessExceptionFactory.dataNotFound(MessageID.W083);
        }
        return outputDataList;
    }

    /**
     * 請求書（CSV)出力データEntity の値をhashmapへ格納する
     * 
     * @param entity S07T001TD1501R33ListResultEntity
     * @return HashMap
     */
    private HashMap<String, Object> setHashMap(S07T001TD1501R33ListResultEntity entity) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        // 請求先コード
        map.put("SKYSAKICD", ItemUtil.nullToString(entity.getSkysakicd()));
        // 請求区分
        map.put("SKYKBN", ItemUtil.nullToString(entity.getSkykbn()));
        // 請求方法区分
        map.put("SKYHOHOKBN", ItemUtil.nullToString(entity.getSkyhohokbn()));
        // 明細印刷区分
        map.put("MEISAIPRNTKBN", ItemUtil.nullToString(entity.getMeisaiprntkbn()));
        // 請求書番号
        map.put("SKYSHONO", ItemUtil.nullToString(entity.getSkyshono()));
        // 請求先郵便番号
        map.put("YUBINNO", ItemUtil.nullToString(entity.getYubinno()));
        // 請求先住所１
        map.put("ADRSN1", ItemUtil.nullToString(entity.getAdrsn1()));
        // 請求先住所２
        map.put("ADRSN2", ItemUtil.nullToString(entity.getAdrsn2()));
        // 請求先住所３
        map.put("ADRSN3", ItemUtil.nullToString(entity.getAdrsn3()));
        // 請求先名称１
        map.put("SKYSAKINMN1", ItemUtil.nullToString(entity.getSkysakinmn1()));
        // 請求先名称２
        map.put("SKYSAKINMN2", ItemUtil.nullToString(entity.getSkysakinmn2()));
        // 請求先敬称区分名
        map.put("SKYSAKIKEISHOKBNNM", ItemUtil.nullToString(entity.getSkysakikeishokbnnm()));
        // 金融機関名称カナ
        map.put("SKSMKINYUKKNNMKANA", ItemUtil.nullToString(entity.getSksmkinyukknnmkana()));
        // 金融機関支店名称カナ
        map.put("SKSMKINYUKKNSHITENNMKANA",
            ItemUtil.nullToString(entity.getSksmkinyukknshitennmkana()));
        // 金融機関口座種別
        map.put("SKSMKINYUKKNKZKIND", ItemUtil.nullToString(entity.getSksmkinyukknkzkind()));
        // 金融機関口座種別名
        map.put("SKSMKINYUKKNKZKINDNM", ItemUtil.nullToString(entity.getSksmkinyukknkzkindnm()));
        // 金融機関口座番号
        map.put("SKSMKINYUKKNKZNO", ItemUtil.nullToString(entity.getSksmkinyukknkzno()));
        // 決済機関名称
        map.put("SKSMKSIKKNNM", ItemUtil.nullToString(entity.getSksmksikknnm()));
        // 得意先外部識別コード
        map.put("TOKUISAKIGAIBUSKBTSCD", ItemUtil.nullToString(entity.getTokuisakigaibuskbtscd()));
        // 請求先マスタ・外部連携コード
        map.put("GAIBURENKEICD", ItemUtil.nullToString(entity.getGaiburenkeicd()));
        // 郵便番号バーコード
        map.put("YUBINNOBAKODO", ItemUtil.nullToString(entity.getYubinnobakodo()));
        // 税計算区分
        map.put("TAXKEISANKBN", ItemUtil.nullToString(entity.getTaxkeisankbn()));
        // 前回ご請求額
        map.put("ZNKKURIKOSHIKIN", getBigDecimalValue(entity.getZnkkurikoshikin()));
        // ご入金額
        map.put("KONKAINYKNKIN", getBigDecimalValue(entity.getKonkainyknkin()));
        // 消費税
        map.put("KONKAISHOHITAXKIN", getBigDecimalValue(entity.getKonkaishohitaxkin()));
        // 今回ご請求額（繰越金）
        map.put("KURIKOSHIKIN", getBigDecimalValue(entity.getKurikoshikin()));
        // 税抜売上金額
        map.put("KONKAITAXNUKIURIKIN", getBigDecimalValue(entity.getKonkaitaxnukiurikin()));
        // 得意先コード
        map.put("TOKUISAKICD", ItemUtil.nullToString(entity.getTokuisakicd()));
        // 得意先名称
        map.put("TOKUISAKINMN1", ItemUtil.nullToString(entity.getTokuisakinmn1()));
        // 特別単価区分
        map.put("TOKUBETSUUNTKBN", ItemUtil.nullToString(entity.getTokubetsuuntkbn()));
        // 特別単価区分名
        map.put("TOKUBETSUUNTKBNNM", ItemUtil.nullToString(entity.getTokubetsuuntkbnnm()));
        // 料金プラン設定日
        map.put("RKNPLNSETTEIDD", ItemUtil.nullToString(entity.getRknplnsetteidd()));
        // 料金プラン継続更新日
        map.put("RKNPLNKEIZOKUKSHNDD", ItemUtil.nullToString(entity.getRknplnkeizokukshndd()));
        // 料金プラン期間
        map.put("RKNPLNKIKAN", ItemUtil.nullToString(entity.getRknplnkikan()));
        // 銀行名称１
        map.put("KINYUKKNNM", ItemUtil.nullToString(entity.getKinyukknnm()));
        // 支店名称１
        map.put("KINYUKKNSHITENNM", ItemUtil.nullToString(entity.getKinyukknshitennm()));
        // 口座種別１
        map.put("KINYUKKNKZKIND", ItemUtil.nullToString(entity.getKinyukknkzkind()));
        // 振込口座番号1
        map.put("KINYUKKNKZNO", ItemUtil.nullToString(entity.getKinyukknkzno()));
        // 銀行名称2
        map.put("KINYUKKNN2NM", ItemUtil.nullToString(entity.getKinyukknn2nm()));
        // 支店名称2
        map.put("KINYUKKNN2SHITENNM", ItemUtil.nullToString(entity.getKinyukknn2shitennm()));
        // 口座種別2
        map.put("KINYUKKNN2KZKIND", ItemUtil.nullToString(entity.getKinyukknn2kzkind()));
        // 振込口座番号2
        map.put("KINYUKKNN2KZNO", ItemUtil.nullToString(entity.getKinyukknn2kzno()));
        // 日付明細
        map.put("URIDD", ItemUtil.nullToString(entity.getUridd()));
        // 入力明細フラグ
        map.put("NYURYOKUMEISAIFLG", ItemUtil.nullToString(entity.getNyuryokumeisaiflg()));
        // 単品明細フラグ
        map.put("TANPINMEISAIFLG", ItemUtil.nullToString(entity.getTanpinmeisaiflg()));
        // 伝票番号明細
        map.put("URIDNPYNO", ItemUtil.nullToString(entity.getUridnpyno()));
        // 伝票行番号明細
        map.put("URIDNPYMEISAIEDANO", ItemUtil.nullToString(entity.getUridnpymeisaiedano()));
        // 商品名称明細
        map.put("SHNNM", ItemUtil.nullToString(entity.getShnnm()));
        // 計上区分明細
        map.put("KEIJOKBN", ItemUtil.nullToString(entity.getKeijokbn()));
        // 明細備考明細
        map.put("MEISAIBIKO", ItemUtil.nullToString(entity.getMeisaibiko()));
        // 数量明細
        map.put("SURYO", ItemUtil.nullToString(entity.getSuryo()));
        // 単位明細
        map.put("URITANINM", ItemUtil.nullToString(entity.getUritaninm()));
        // 単価明細
        map.put("UNT", getBigDecimalValue(entity.getUnt()));
        // 単価消費税明細
        map.put("UNTSHOHITAXKIN", getBigDecimalValue(entity.getUntshohitaxkin()));
        // 金額明細
        map.put("KIN", getBigDecimalValue(entity.getKin()));
        // 金額合計
        map.put("SUMKIN", getBigDecimalValue("0"));
        // 消費税明細
        map.put("SHOHITAXKIN", getBigDecimalValue(entity.getShohitaxkin()));
        // 消費税合計
        map.put("SUMSHOHITAXKIN", getBigDecimalValue("0"));
        // 8％売上合計
        map.put("N8PERZEINUKIURIKIN", getBigDecimalValue("0"));
        // 8％消費税合計
        map.put("N8PERSUMSHOHITAXKIN", getBigDecimalValue("0"));
        // 10％売上合計
        map.put("N10PERZEINUKIURIKIN", getBigDecimalValue("0"));
        // 10%消費税合計
        map.put("N10PERSUMSHOHITAXKIN", getBigDecimalValue("0"));
        // 非課税売上合計
        map.put("SUMHIKAZEIKIN", getBigDecimalValue("0"));
        // 不課税売上合計
        map.put("SUMHUKAZEIKIN", getBigDecimalValue("0"));
        // 税区分
        map.put("TAXKBN", ItemUtil.nullToString(entity.getTaxkbn()));
        // セット商品の商品コード
        map.put("STSHNCD", ItemUtil.nullToString(entity.getStshncd()));
        // セット商品の税区分
        map.put("STTAXKBN", ItemUtil.nullToString(entity.getSttaxkbn()));
        // お知らせ1-1
        map.put("DNPYOSHIRASENAIYON1", ItemUtil.nullToString(entity.getDnpyoshirasenaiyon1()));
        // お知らせ1-2
        map.put("DNPYOSHIRASENAIYON2", ItemUtil.nullToString(entity.getDnpyoshirasenaiyon2()));
        // お知らせ1-3
        map.put("DNPYOSHIRASENAIYON3", ItemUtil.nullToString(entity.getDnpyoshirasenaiyon3()));
        // お知らせ1-4
        map.put("DNPYOSHIRASENAIYON4", ItemUtil.nullToString(entity.getDnpyoshirasenaiyon4()));
        // お知らせ1-5
        map.put("DNPYOSHIRASENAIYON5", ItemUtil.nullToString(entity.getDnpyoshirasenaiyon5()));
        // お知らせ1-6
        map.put("DNPYOSHIRASENAIYON6", ItemUtil.nullToString(entity.getDnpyoshirasenaiyon6()));
        // お知らせ２
        map.put("DNPYOSHIRASE2", ItemUtil.nullToString(entity.getDnpyoshirase2()));
        // 印刷備考
        map.put("PRNTBIKO", ItemUtil.nullToString(entity.getPrntbiko()));
        // 回収方法区分
        map.put("KAISHUHOHOKBN", ItemUtil.nullToString(entity.getKaishuhohokbn()));
        // 回収方法区分名称
        map.put("KAISHUHOHOKBNNM", ItemUtil.nullToString(entity.getKaishuhohokbnnm()));
        // 支払予定日
        map.put("SHRYTIDYDMDD", ItemUtil.nullToString(entity.getShrytidydmdd()));
        // 消費税レコードフラグ
        map.put("TAXRECFLG", ItemUtil.nullToString(entity.getTaxrecflg()));
        // 売上、入金のFLAG1
        map.put("FLAG1", ItemUtil.nullToString(entity.getFlag1()));
        // 売上、入金のFLAG2
        map.put("FLAG2", ItemUtil.nullToString(entity.getFlag2()));
        return map;
    }

    /**
     * 数値の文字列をbigdecimal型に変換する
     * 
     * @param value チェックする値
     * @return valueが空、nullの場合は0、それ以外の場合はvalueの値をbigDecimal型で返却する
     */
    private BigDecimal getBigDecimalValue(String value) {
        return StringUtils.isEmpty(value) ? new BigDecimal("0") : new BigDecimal(value);
    }

    /**
     * 足し算を行い返却する
     * 
     * @param value1 加算元の値
     * @param value2 加算する値
     * @return 加算後の値
     */
    private BigDecimal getSum(BigDecimal value1, BigDecimal value2) {
        return value1.add(value2);
    }

    /**
     * CSV作成処理を実行する
     * 
     * @param printDto 請求書印刷DTO
     * @param outputDataList 出力データリスト
     * @param hashMap データmap
     * @param meisaiKbn 明細区分
     * @param uriageNyukinKbn 売上入金区分
     * @param line 行数
     * @param invoiceEnforcementFlg インボイス施行フラグ
     * @param hikaeOutputFlg 控え出力フラグ
     * @param fromImage 差出人イメージ
     * @return 追加行数
     */
    private int createCsv(D1501R30PrintDto printDto, List<List<String>> outputDataList,
        HashMap<String, Object> hashMap, String meisaiKbn, String uriageNyukinKbn, int line,
        String invoiceEnforcementFlg, String hikaeOutputFlg, String fromImage) {
        // 変数設定
        int lineNum = line;
        int addLineNum = 1;
        if (line == 1 || (line - 16) % 28 == 0) {
            // 請求先ヘッダー情報を出力
            createCsvRowData(printDto, outputDataList, hashMap, MEISAI_KBN_SYUKEI, "0", lineNum,
                invoiceEnforcementFlg, hikaeOutputFlg, fromImage);
            lineNum++;
            addLineNum++;
        }
        // データ行を出力
        createCsvRowData(printDto, outputDataList, hashMap, meisaiKbn, uriageNyukinKbn, lineNum,
            invoiceEnforcementFlg, hikaeOutputFlg, fromImage);
        return addLineNum;
    }

    /**
     * CSV行データの作成を実行する
     * 
     * @param printDto 請求書印刷DTO
     * @param outputDataList 出力データリスト
     * @param hashMap データmap
     * @param meisaiKbn 明細区分
     * @param uriageNyukinKbn 売上入金区分
     * @param line 行数
     * @param invoiceEnforcementFlg インボイス施行フラグ
     * @param hikaeOutputFlg 控え出力フラグ
     * @param fromImage 差出人イメージ
     */
    private void createCsvRowData(D1501R30PrintDto printDto, List<List<String>> outputDataList,
        HashMap<String, Object> hashMap, String meisaiKbn, String uriageNyukinKbn, int line,
        String invoiceEnforcementFlg, String hikaeOutputFlg, String fromImage) {
        List<String> list = new ArrayList<>();
        // 発行日
        list.add(printDto.getSystemDate());
        // 締日
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(printDto.getSkyshimedate())));
        // 帳票タイトル
        list.add(ListCreatorUtil.wrapQuote(getReportTitle(hikaeOutputFlg)));
        // 請求書番号
        list.add(getSkyshoNo(hashMap.get("SKYSHONO").toString()));
        // 請求先郵便番号
        list.add(ListCreatorUtil.wrapQuote(hashMap.get("YUBINNO").toString()));
        // ロゴ
        list.add(ListCreatorUtil.wrapQuote(fromImage));
        // 請求先住所１
        list.add(ListCreatorUtil.wrapQuote(hashMap.get("ADRSN1").toString()));
        // 請求先住所２
        list.add(ListCreatorUtil.wrapQuote(hashMap.get("ADRSN2").toString()));
        // 請求先住所３
        list.add(ListCreatorUtil.wrapQuote(hashMap.get("ADRSN3").toString()));
        // AP基本コード
        list.add(ListCreatorUtil.wrapQuote(printDto.getApkihoncd()));
        // ショップコード
        list.add(ListCreatorUtil.wrapQuote(printDto.getShopcd()));
        // 請求先コード
        list.add(ListCreatorUtil.wrapQuote(hashMap.get("SKYSAKICD").toString()));
        // 請求先名称１
        list.add(ListCreatorUtil.wrapQuote(hashMap.get("SKYSAKINMN1").toString()));
        // 請求先名称２
        list.add(ListCreatorUtil.wrapQuote(hashMap.get("SKYSAKINMN2").toString()));
        // 敬称
        list.add(ListCreatorUtil.wrapQuote(hashMap.get("SKYSAKIKEISHOKBNNM").toString()));
        // 郵便番号バーコード
        list.add(ListCreatorUtil.wrapQuote(hashMap.get("YUBINNOBAKODO").toString()));
        // 印字内容１～４
        list.addAll(
            getPrintContents(printDto.getShimedydm(), hashMap.get("KAISHUHOHOKBN").toString()));
        // 消費税項目名
        list.add(ListCreatorUtil.wrapQuote(getTaxItemName(hashMap.get("TAXKEISANKBN").toString())));
        // 前回ご請求額
        list.add(ListCreatorUtil.wrapQuote(getZenkaiSkyGaku(hashMap.get("SKYHOHOKBN").toString(),
            (BigDecimal) hashMap.get("ZNKKURIKOSHIKIN"))));
        // ご入金額
        list.add(ListCreatorUtil.wrapQuote(getNyukinGaku(hashMap.get("SKYHOHOKBN").toString(),
            (BigDecimal) hashMap.get("KONKAINYKNKIN"))));
        // 今回ご利用額
        list.add(getKonkaiGoriyoGaku(hashMap.get("TAXKEISANKBN").toString(),
            (BigDecimal) hashMap.get("KONKAITAXNUKIURIKIN"),
            (BigDecimal) hashMap.get("KONKAISHOHITAXKIN")));
        // 内消費税１
        list.add(hashMap.get("KONKAISHOHITAXKIN").toString());
        // 今回ご請求額
        list.add(ListCreatorUtil.wrapQuote(getKonkaiSkyGaku(hashMap.get("SKYHOHOKBN").toString(),
            (BigDecimal) hashMap.get("KURIKOSHIKIN"),
            (BigDecimal) hashMap.get("KONKAITAXNUKIURIKIN"),
            (BigDecimal) hashMap.get("KONKAISHOHITAXKIN"))));
        // お繰越金額
        list.add(ListCreatorUtil.wrapQuote(getKurikoshiKin(hashMap.get("SKYHOHOKBN").toString(),
            (BigDecimal) hashMap.get("ZNKKURIKOSHIKIN"),
            (BigDecimal) hashMap.get("KONKAINYKNKIN"))));
        // 得意先コード
        list.add(ListCreatorUtil.wrapQuote(hashMap.get("TOKUISAKICD").toString()));
        // 得意先名称
        list.add(ListCreatorUtil.wrapQuote(
            d1501r00Service.getCustomerName(hashMap.get("RKNPLNKEIZOKUKSHNDD").toString(),
                hashMap.get("RKNPLNSETTEIDD").toString(), hashMap.get("TOKUBETSUUNTKBN").toString(),
                hashMap.get("RKNPLNKIKAN").toString(), hashMap.get("TOKUBETSUUNTKBNNM").toString(),
                hashMap.get("TOKUISAKINMN1").toString(), printDto.getShimedydm())));
        // 日付明細
        // 明細区分が"1"以外の場合、初期値を設定
        if (!MEISAI_KBN_URIAGE_NYUKIN_MEISAI.equals(meisaiKbn)) {
            list.add(CSV_BLANK);
        } else {
            list.add(hashMap.get("URIDD").toString());
        }
        // 明細伝票番号格納
        // 明細区分が"1"以外の場合、初期値を設定
        if (!MEISAI_KBN_URIAGE_NYUKIN_MEISAI.equals(meisaiKbn)) {
            list.add(CSV_BLANK);
        } else {
            list.add(ListCreatorUtil.wrapQuote(hashMap.get("URIDNPYNO").toString()));
        }
        // 明細行番号格納
        // 明細区分が"1"以外の場合、初期値を設定
        if (!MEISAI_KBN_URIAGE_NYUKIN_MEISAI.equals(meisaiKbn)) {
            list.add(CSV_BLANK);
        } else {
            list.add(ListCreatorUtil
                .wrapQuote(getMeisaiLineNo(hashMap.get("NYURYOKUMEISAIFLG").toString(),
                    hashMap.get("URIDNPYMEISAIEDANO").toString())));
        }
        // 商品名称明細
        // 明細区分が"1"以外の場合、初期値を設定
        if (!MEISAI_KBN_URIAGE_NYUKIN_MEISAI.equals(meisaiKbn)) {
            list.add(CSV_BLANK);
        } else {
            list.add(ListCreatorUtil.wrapQuote(
                d1501r00Service.getItemNameDetail(hashMap.get("TANPINMEISAIFLG").toString(),
                    hashMap.get("STSHNCD").toString(), hashMap.get("SHNNM").toString())));
        }
        // 税区分印
        // 明細区分が"1"以外の場合、初期値を設定
        if (!MEISAI_KBN_URIAGE_NYUKIN_MEISAI.equals(meisaiKbn)) {
            list.add(CSV_BLANK);
        } else {
            list.add(ListCreatorUtil.wrapQuote(
                d1501r00Service.getTaxTypeDetail(hashMap.get("TANPINMEISAIFLG").toString(),
                    hashMap.get("NYURYOKUMEISAIFLG").toString(), hashMap.get("TAXKBN").toString(),
                    hashMap.get("STTAXKBN").toString())));
        }
        // 明細備考明細
        // 明細区分が"1"以外の場合、初期値を設定
        if (!MEISAI_KBN_URIAGE_NYUKIN_MEISAI.equals(meisaiKbn)) {
            list.add(CSV_BLANK);
        } else {
            list.add(ListCreatorUtil.wrapQuote(hashMap.get("MEISAIBIKO").toString()));
        }
        // 計上区分明細
        // 明細区分が"1"以外の場合、初期値を設定
        if (!MEISAI_KBN_URIAGE_NYUKIN_MEISAI.equals(meisaiKbn)) {
            list.add(CSV_BLANK);
        } else {
            list.add(ListCreatorUtil.wrapQuote(hashMap.get("KEIJOKBN").toString()));
        }
        // 明細数量格納
        // 明細区分が"1"以外の場合、初期値を設定
        if (!MEISAI_KBN_URIAGE_NYUKIN_MEISAI.equals(meisaiKbn)) {
            list.add(CSV_BLANK);
        } else {
            list.add(hashMap.get("SURYO").toString());
        }
        // 明細単位格納
        // 明細区分が"1"以外の場合、初期値を設定
        if (!MEISAI_KBN_URIAGE_NYUKIN_MEISAI.equals(meisaiKbn)) {
            list.add(CSV_BLANK);
        } else {
            list.add(ListCreatorUtil.wrapQuote(hashMap.get("URITANINM").toString()));
        }
        // 明細単価格納
        // 明細区分が"1"以外の場合、初期値を設定
        if (!MEISAI_KBN_URIAGE_NYUKIN_MEISAI.equals(meisaiKbn)) {
            list.add(CSV_ZERO);
        } else {
            list.add(getMeisaiUnt(hashMap.get("NYURYOKUMEISAIFLG").toString(),
                hashMap.get("TAXKEISANKBN").toString(), (BigDecimal) hashMap.get("UNT"),
                (BigDecimal) hashMap.get("UNTSHOHITAXKIN")));
        }
        // 金額明細
        // 明細区分が"1"以外の場合、初期値を設定
        if (!MEISAI_KBN_URIAGE_NYUKIN_MEISAI.equals(meisaiKbn)) {
            list.add(CSV_ZERO);
        } else {
            list.add(getMeisaiKin(hashMap.get("NYURYOKUMEISAIFLG").toString(),
                hashMap.get("TAXKEISANKBN").toString(), (BigDecimal) hashMap.get("KIN"),
                (BigDecimal) hashMap.get("SHOHITAXKIN")));
        }
        // 明細内消費税格納
        // 明細区分が"1"以外の場合、初期値を設定
        if (!MEISAI_KBN_URIAGE_NYUKIN_MEISAI.equals(meisaiKbn)) {
            list.add(CSV_ZERO);
        } else {
            list.add(getMeisaiTax(hashMap.get("NYURYOKUMEISAIFLG").toString(),
                hashMap.get("SHOHITAXKIN").toString()));
        }
        // 金額合計
        list.add(getTotalKin(meisaiKbn, invoiceEnforcementFlg,
            hashMap.get("TAXKEISANKBN").toString(), (BigDecimal) hashMap.get("N8PERSUMSHOHITAXKIN"),
            (BigDecimal) hashMap.get("N8PERZEINUKIURIKIN"),
            (BigDecimal) hashMap.get("N10PERSUMSHOHITAXKIN"),
            (BigDecimal) hashMap.get("N10PERZEINUKIURIKIN"),
            (BigDecimal) hashMap.get("SUMHIKAZEIKIN"), (BigDecimal) hashMap.get("SUMHUKAZEIKIN"),
            (BigDecimal) hashMap.get("SUMKIN"), (BigDecimal) hashMap.get("SUMSHOHITAXKIN")));
        // 明細内消費税合計格納
        list.add(getTotalTax(meisaiKbn, invoiceEnforcementFlg,
            hashMap.get("TAXKEISANKBN").toString(), (BigDecimal) hashMap.get("N8PERSUMSHOHITAXKIN"),
            (BigDecimal) hashMap.get("N10PERSUMSHOHITAXKIN"),
            (BigDecimal) hashMap.get("SUMSHOHITAXKIN")));
        // お知らせ欄１
        list.add(ListCreatorUtil.wrapQuote(hashMap.get("DNPYOSHIRASENAIYON1").toString()));
        // お知らせ欄２
        list.add(ListCreatorUtil.wrapQuote(hashMap.get("DNPYOSHIRASENAIYON2").toString()));
        // お知らせ欄３
        list.add(ListCreatorUtil.wrapQuote(hashMap.get("DNPYOSHIRASENAIYON3").toString()));
        // お知らせ欄４
        list.add(ListCreatorUtil.wrapQuote(hashMap.get("DNPYOSHIRASENAIYON4").toString()));
        // お知らせ欄５
        list.add(ListCreatorUtil.wrapQuote(hashMap.get("DNPYOSHIRASENAIYON5").toString()));
        // お知らせ欄６
        list.add(ListCreatorUtil.wrapQuote(hashMap.get("DNPYOSHIRASENAIYON6").toString()));
        // 備考欄
        list.add(ListCreatorUtil
            .wrapQuote(ListCreatorUtil.encodeCrlf(hashMap.get("PRNTBIKO").toString())));
        // お知らせ２
        list.add(ListCreatorUtil.wrapQuote(hashMap.get("DNPYOSHIRASE2").toString()));
        // 銀行名称１
        list.add(ListCreatorUtil.wrapQuote(hashMap.get("KINYUKKNNM").toString()));
        // 支店名称１
        list.add(ListCreatorUtil.wrapQuote(hashMap.get("KINYUKKNSHITENNM").toString()));
        // 口座種別１
        list.add(ListCreatorUtil.wrapQuote(hashMap.get("KINYUKKNKZKIND").toString()));
        // 振込口座番号１
        list.add(ListCreatorUtil.wrapQuote(hashMap.get("KINYUKKNKZNO").toString()));
        // 銀行名称２
        list.add(ListCreatorUtil.wrapQuote(hashMap.get("KINYUKKNN2NM").toString()));
        // 支店名称２
        list.add(ListCreatorUtil.wrapQuote(hashMap.get("KINYUKKNN2SHITENNM").toString()));
        // 口座種別２
        list.add(ListCreatorUtil.wrapQuote(hashMap.get("KINYUKKNN2KZKIND").toString()));
        // 振込口座番号２
        list.add(ListCreatorUtil.wrapQuote(hashMap.get("KINYUKKNN2KZNO").toString()));
        // お支払い方法お支払い期日１
        list.add(
            d1501r00Service.getPaymentMethodAndPayDeadline1(D1501R00Constants.TYOHYO_KBN_SKYSHO,
                hashMap.get("KAISHUHOHOKBN").toString(), hashMap.get("KAISHUHOHOKBNNM").toString(),
                hashMap.get("SHRYTIDYDMDD").toString(), "", ""));
        // お支払い方法お支払い期日２
        list.add(ListCreatorUtil.wrapQuote(d1501r00Service.getPaymentMethodAndPayDeadline2(
            hashMap.get("KAISHUHOHOKBN").toString(), hashMap.get("SKSMKINYUKKNNMKANA").toString(),
            hashMap.get("SKSMKINYUKKNKZKINDNM").toString(), hashMap.get("SKSMKSIKKNNM").toString(),
            hashMap.get("SKSMKINYUKKNKZNO").toString(),
            hashMap.get("SKSMKINYUKKNSHITENNMKANA").toString())));
        // 入金売上区分
        list.add(ListCreatorUtil.wrapQuote(uriageNyukinKbn));
        // 明細区分
        list.add(ListCreatorUtil.wrapQuote(meisaiKbn));
        // ヘッダ表示区分
        list.add(ListCreatorUtil.wrapQuote(getHeaderHyojiKbn(hashMap.get("SKYKBN").toString(),
            hashMap.get("MEISAIPRNTKBN").toString(), hikaeOutputFlg,
            hashMap.get("SKYSAKICD").toString(), hashMap.get("TOKUISAKICD").toString())));
        // CNT
        list.add(String.valueOf(line));
        // 得意先外部識別コード
        list.add(ListCreatorUtil.wrapQuote(hashMap.get("TOKUISAKIGAIBUSKBTSCD").toString()));
        // 請求先外部連携コード
        list.add(ListCreatorUtil.wrapQuote(hashMap.get("GAIBURENKEICD").toString()));
        // 非出力空データＮ
        list.add("");
        outputDataList.add(list);
    }

    /**
     * ヘッダ表示区分を返却
     * 
     * @param skyKbn 請求区分
     * @param hikaeOutputFlg 控え出力フラグ
     * @param skysakiCd 請求先コード
     * @param tokuisakiCd 得意先コード
     * @return ヘッダ表示区分
     */
    private String getHeaderHyojiKbn(String skyKbn, String meisaiPtintKbn, String hikaeOutputFlg,
        String skysakiCd, String tokuisakiCd) {
        String title = getReportTitle(hikaeOutputFlg);
        if (CommonConstants.SKYKBN_1.equals(skyKbn)) {
            if (CommonConstants.MEISAIPRNTKBN_ISSHO.equals(meisaiPtintKbn)) {
                return skysakiCd + "、" + title;
            } else if (CommonConstants.MEISAIPRNTKBN_KOBETSU.equals(meisaiPtintKbn)) {
                if (skysakiCd.equals(tokuisakiCd)) {
                    return skysakiCd + "、" + title;
                } else {
                    return skysakiCd + "、" + tokuisakiCd + "、" + title;
                }
            }
        } else if (CommonConstants.SKYKBN_2.equals(skyKbn)) {
            if (CommonConstants.MEISAIPRNTKBN_ISSHO.equals(meisaiPtintKbn)) {
                return skysakiCd + "、" + title;
            } else if (CommonConstants.MEISAIPRNTKBN_KOBETSU.equals(meisaiPtintKbn)) {
                return tokuisakiCd + "、" + title;
            }
        }
        return "";
    }

    /**
     * 明細内消費税合計格納を返却する
     * 
     * @param meisaiKbn 明細区分
     * @param invoiceEnforcementFlg インボイス施行フラグ
     * @param taxKeisanFlg 税計算区分
     * @param n8ShohiTaxSum 8%消費税合計
     * @param n10ShohiTaxSum 10%消費税合計
     * @param taxSum 消費税合計
     * @return 金額合計にセットする値
     */
    private String getTotalTax(String meisaiKbn, String invoiceEnforcementFlg, String taxKeisanFlg,
        BigDecimal n8ShohiTaxSum, BigDecimal n10ShohiTaxSum, BigDecimal taxSum) {
        if (MEISAI_KBN_8PER_HYOUJI.equals(meisaiKbn)) {
            if (D1501R00Constants.INVOICE_ENFORCEMENT_FLAG_DISABLED.equals(invoiceEnforcementFlg)) {
                return "";
            } else {
                return n8ShohiTaxSum.toString();
            }
        } else if (MEISAI_KBN_10PER_HYOUJI.equals(meisaiKbn)) {
            if (D1501R00Constants.INVOICE_ENFORCEMENT_FLAG_DISABLED.equals(invoiceEnforcementFlg)) {
                return "";
            } else {
                return n10ShohiTaxSum.toString();
            }
        } else if (MEISAI_KBN_HIKAZEI_HYOUJI.equals(meisaiKbn)) {
            return "";
        } else if (MEISAI_KBN_HUKAZEI_HYOUJI.equals(meisaiKbn)) {
            return "";
        } else {
            return taxSum.toString();
        }
    }

    /**
     * 金額合計を返却する
     * 
     * @param meisaiKbn 明細区分
     * @param invoiceEnforcementFlg インボイス施行フラグ
     * @param taxKeisanFlg 税計算区分
     * @param n8ShohiTaxSum 8%消費税合計
     * @param n8TaxNukiSum 8%税抜売上合計
     * @param n10ShohiTaxSum 10%消費税合計
     * @param n10TaxNukiSum 10%税抜売上合計
     * @param hikazeiKinSum 非課税売上合計
     * @param hukazeiKinSum 不課税売上合計
     * @param kinSum 金額合計
     * @param shohiTaxKin 消費税合計
     * @return 金額合計にセットする値
     */
    private String getTotalKin(String meisaiKbn, String invoiceEnforcementFlg, String taxKeisanFlg,
        BigDecimal n8ShohiTaxSum, BigDecimal n8TaxNukiSum, BigDecimal n10ShohiTaxSum,
        BigDecimal n10TaxNukiSum, BigDecimal hikazeiKinSum, BigDecimal hukazeiKinSum,
        BigDecimal kinSum, BigDecimal shohiTaxKin) {
        if (MEISAI_KBN_URIAGE_NYUKIN_GOUKEI.equals(meisaiKbn)) {
            if (CommonConstants.KBN_TAXKEISAN_IN.equals(taxKeisanFlg)) {
                BigDecimal sum = getSum(kinSum, shohiTaxKin);
                return sum.toString();
            } else {
                return kinSum.toString();
            }
        } else if (MEISAI_KBN_8PER_HYOUJI.equals(meisaiKbn)) {
            if (D1501R00Constants.INVOICE_ENFORCEMENT_FLAG_DISABLED.equals(invoiceEnforcementFlg)
                || CommonConstants.KBN_TAXKEISAN_IN.equals(taxKeisanFlg)) {
                BigDecimal sum = getSum(n8ShohiTaxSum, n8TaxNukiSum);
                return sum.toString();
            } else {
                return n8TaxNukiSum.toString();
            }
        } else if (MEISAI_KBN_10PER_HYOUJI.equals(meisaiKbn)) {
            if (D1501R00Constants.INVOICE_ENFORCEMENT_FLAG_DISABLED.equals(invoiceEnforcementFlg)
                || CommonConstants.KBN_TAXKEISAN_IN.equals(taxKeisanFlg)) {
                BigDecimal sum = getSum(n10ShohiTaxSum, n10TaxNukiSum);
                return sum.toString();
            } else {
                return n10TaxNukiSum.toString();
            }
        } else if (MEISAI_KBN_HIKAZEI_HYOUJI.equals(meisaiKbn)) {
            return hikazeiKinSum.toString();
        } else if (MEISAI_KBN_HUKAZEI_HYOUJI.equals(meisaiKbn)) {
            return hukazeiKinSum.toString();
        } else {
            return kinSum.toString();
        }
    }

    /**
     * 明細内消費税格納を返却する
     * 
     * @param meisaiFlg 入力明細フラグ
     * @param tax 消費税明細
     * @return 入力明細フラグ1:空 入力明細フラグ1以外: 消費税明細
     */
    private String getMeisaiTax(String meisaiFlg, String tax) {
        return CommonConstants.NYURYOKUMEISAIFLG_0.equals(meisaiFlg) ? "" : tax;
    }

    /**
     * 金額明細を返却する
     * 
     * @param meisaiFlg 入力明細フラグ
     * @param taxKeisanFlg 税計算区分
     * @param kin 金額明細
     * @param kinTax 消費税明細
     * @return 入力明細フラグ0:空 入力明細フラグ1以外かつ税計算区分1: 単価明細 + 単価消費税明細 入力明細フラグ1以外かつ税計算区分1以外: 単価明細
     */
    private String getMeisaiKin(String meisaiFlg, String taxKeisanFlg, BigDecimal kin,
        BigDecimal kinTax) {
        if (CommonConstants.NYURYOKUMEISAIFLG_0.equals(meisaiFlg)) {
            return "";
        } else {
            if (CommonConstants.KBN_TAXKEISAN_IN.equals(taxKeisanFlg)) {
                BigDecimal sum = getSum(kin, kinTax);
                return sum.toString();
            } else {
                return kin.toString();
            }
        }
    }

    /**
     * 明細単価格納を返却する
     * 
     * @param meisaiFlg 入力明細フラグ
     * @param taxKeisanFlg 税計算区分
     * @param unt 単価明細
     * @param untTax 単価消費税明細
     * @return 入力明細フラグ0:空 入力明細フラグ1以外かつ税計算区分1: 単価明細 + 単価消費税明細 入力明細フラグ1以外かつ税計算区分1以外: 単価明細
     */
    private String getMeisaiUnt(String meisaiFlg, String taxKeisanFlg, BigDecimal unt,
        BigDecimal untTax) {
        if (CommonConstants.NYURYOKUMEISAIFLG_0.equals(meisaiFlg)) {
            return "";
        } else {
            if (CommonConstants.KBN_TAXKEISAN_IN.equals(taxKeisanFlg)) {
                BigDecimal sum = getSum(unt, untTax);
                return sum.toString();
            } else {
                return unt.toString();
            }
        }
    }

    /**
     * 明細行番号格納を返却する
     * 
     * @param meisaiFlg 入力明細フラグ
     * @param no 売上伝票明細枝番
     * @return 入力明細フラグ1:空 入力明細フラグ1以外: 売上伝票明細枝番
     */
    private String getMeisaiLineNo(String meisaiFlg, String no) {
        return CommonConstants.NYURYOKUMEISAIFLG_0.equals(meisaiFlg) ? "" : no;
    }

    /**
     * 印字内容1~4をListで返却する
     * 
     * @param shimedydm 締年月
     * @param kaishuhohokbn 回収方法区分
     * @return 印字内容List
     */
    private List<String> getPrintContents(String shimedydm, String kaishuhohokbn) {
        String printYearMonth = d1501r00Service.getPrintYearMonth(shimedydm);
        return d1501r00Service.getPrintContents(D1501R00Constants.TYOHYO_KBN_SKYSHO_CSV,
            printYearMonth, kaishuhohokbn);
    }

    /**
     * お繰越金額を返却する
     * 
     * @param skyhohoKbn 請求方法区分
     * @param znkKurikoshiKin 前回ご請求額
     * @param konkaiNyknKin ご入金額
     * @return 請求方法区分1:前回ご請求額 - ご入金額 請求方法区分1以外:"*********"
     */
    private String getKurikoshiKin(String skyhohoKbn, BigDecimal znkKurikoshiKin,
        BigDecimal konkaiNyknKin) {
        if (CommonConstants.SKYHOHOKBN_ZANDAKA.equals(skyhohoKbn)) {
            BigDecimal diff = znkKurikoshiKin.subtract(konkaiNyknKin);
            return d1501r00Service.amountFormatter(diff.toString());
        } else {

            return "*********";
        }
    }

    /**
     * 今回ご請求額を返却
     * 
     * @param skyhohoKbn 請求方法区分
     * @param konkaiSkyGaku 今回ご請求額（繰越金）
     * @param zeinukiKin 税抜売上金額
     * @param konkaiShohiTaxKin 消費税額
     * @return 請求方法区分1:今回ご請求額 請求方法区分1以外:税抜売上金額 + 消費税額を返却
     */
    private String getKonkaiSkyGaku(String skyhohoKbn, BigDecimal konkaiSkyGaku,
        BigDecimal zeinukiKin, BigDecimal konkaiShohiTaxKin) {
        BigDecimal result = CommonConstants.SKYHOHOKBN_ZANDAKA.equals(skyhohoKbn) ? konkaiSkyGaku
            : getSum(zeinukiKin, konkaiShohiTaxKin);
        return d1501r00Service.amountFormatter(result.toString());
    }

    /**
     * 今回ご利用額を返却
     * 
     * @param taxKesanKbn 税計算区分
     * @param zeinukiKin 税抜売上金額
     * @param konkaiShohiTaxKin 消費税額
     * @return 税計算区分1:税抜売上金額 税計算区分1以外:税抜売上金額 + 消費税額を返却
     */
    private String getKonkaiGoriyoGaku(String taxKesanKbn, BigDecimal zeinukiKin,
        BigDecimal konkaiShohiTaxKin) {
        return CommonConstants.KBN_TAXKEISAN_IN.equals(taxKesanKbn)
            ? getSum(zeinukiKin, konkaiShohiTaxKin).toString()
            : zeinukiKin.toString();
    }

    /**
     * ご入金額を返却
     * 
     * @param skyhohoKbn 請求方法区分
     * @param nyukinGaku ご入金額
     * @return 請求方法区分1:ご入金額 請求方法区分1以外:「*********」を返却
     */
    private String getNyukinGaku(String skyhohoKbn, BigDecimal nyukinGaku) {
        return CommonConstants.SKYHOHOKBN_ZANDAKA.equals(skyhohoKbn)
            ? d1501r00Service.amountFormatter(nyukinGaku.toString())
            : "*********";
    }

    /**
     * 前回ご請求額を返却
     * 
     * @param skyhohoKbn 請求方法区分
     * @param zenkaiSkyGaku 前回ご請求額
     * @return 請求方法区分1:前回ご請求額 請求方法区分1以外:「*********」を返却
     */
    private String getZenkaiSkyGaku(String skyhohoKbn, BigDecimal zenkaiSkyGaku) {
        return CommonConstants.SKYHOHOKBN_ZANDAKA.equals(skyhohoKbn)
            ? d1501r00Service.amountFormatter(zenkaiSkyGaku.toString())
            : "*********";
    }

    /**
     * 消費税項目名を返却する
     * 
     * @param taxKesanKbn 税計算区分
     * @return 税計算区分1:内消費税 税計算区分1以外:消費税額を返却
     */
    private String getTaxItemName(String taxKesanKbn) {
        return CommonConstants.KBN_TAXKEISAN_IN.equals(taxKesanKbn) ? "内消費税額" : "消費税額";
    }

    /**
     * 請求書番号が12桁未満の場合先頭を0埋めで12桁に変換して返却する
     * 
     * @param skyshoNo 請求書番号
     * @return 12桁請求書番号
     */
    private String getSkyshoNo(String skyshoNo) {
        return zeroPadding(12, skyshoNo);
    }

    /**
     * 枝番が2桁未満の場合先頭を0埋めで2桁に変換して返却する
     * 
     * 
     * @param edaNo 枝番
     * @return 2桁枝番
     */
    private String getEdaNo(int edaNo) {
        String s = Integer.toString(edaNo);
        return zeroPadding(2, s);
    }

    /**
     * 前方0埋めした値を返却する
     * 
     * @param digit 0埋め後の桁数
     * @param target 前埋めする前の値
     * @return 前方0埋めした値
     */
    private String zeroPadding(int digit, String target) {
        String res = target;
        if (digit > target.length()) {
            for (int i = 0; i < (digit - target.length()); i++) {
                res = "0" + res;
            }
        }
        return res;
    }

    /**
     * 帳票タイトルを返却する
     * 
     * @param hikaeOutputFlg 控え出力フラグ
     * @return 帳票タイトル 控え出力ﾌﾗｸﾞが0の場合「請求書」 1の場合「請求書（控）」を返却
     */
    private String getReportTitle(String hikaeOutputFlg) {
        if (D1501R00Constants.HIKAE_OUTPUT_FLAG_DISABLED.equals(hikaeOutputFlg)) {
            return "請求書";
        } else if (D1501R00Constants.HIKAE_OUTPUT_FLAG_ENABLED.equals(hikaeOutputFlg)) {
            return "請求書（控）";
        }
        return "";
    }

    /**
     * 売上合計表示行であるか判定を行う
     * 
     * @param loopIndex ループインデックス
     * @param mapList 請求書mapList
     * @return 売上合計表示行を出力する:true 売上合計表示行を出力しない：false
     */
    private boolean isUriageGoukeiHyouji(int loopIndex, List<HashMap<String, Object>> mapList) {
        int mapListSize = mapList.size();
        int add1Index = loopIndex + 1;
        if (add1Index < mapListSize) {
            String flag1 = mapList.get(add1Index).get("FLAG1").toString();
            if (!"1".equals(flag1)) {
                return true;
            }
        }
        if (isLastData(loopIndex, mapList)) {
            return true;
        }
        return false;
    }

    /**
     * 入金合計を出力するか判定を行う
     * 
     * @param loopIndex ループインデックス
     * @param flag2 売上、入金のFLAG2
     * @param mapList 請求書mapList
     * @return 入金合計を出力する:true 入金合計を出力しない：false
     */
    private boolean isNyukinGoukei(int loopIndex, String flag2,
        List<HashMap<String, Object>> mapList) {
        int mapListSize = mapList.size();
        int add1Index = loopIndex + 1;
        if (!"".equals(flag2)) {
            if (add1Index < mapListSize) {
                String flag1 = mapList.get(add1Index).get("FLAG1").toString();
                if (!"2".equals(flag1)) {
                    return true;
                }
            }
            if (isLastData(loopIndex, mapList)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 税別合計を出力するか判定を行う
     * 
     * @param loopIndex ループインデックス
     * @param flag2 売上、入金のFLAG2
     * @param mapList 請求書mapList
     * @return 税別合計を出力する:true 税別合計を出力しない：false
     */
    private boolean isTaxRateTotal(int loopIndex, String flag2,
        List<HashMap<String, Object>> mapList) {
        int mapListSize = mapList.size();
        int add1Index = loopIndex + 1;
        if ("".equals(flag2)) {
            return true;
        }
        if (add1Index < mapListSize) {
            String flag1 = mapList.get(add1Index).get("FLAG1").toString();
            if (!"2".equals(flag1)) {
                return true;
            }
        }
        if (isLastData(loopIndex, mapList)) {
            return true;
        }
        return false;
    }

    /**
     * 税率別合計を算出して出力データリストにセットする
     * 
     * @param printDto 請求書印刷DTO
     * @param outputDataList 出力データリスト
     * @param hashMap データmap
     * @param n8PerZeinukiUriKin 8%税抜売上合計
     * @param n8PerSumShohiTaxKin 8%消費税合計
     * @param n10PerZeinukiUriKin 10%税抜売上合計
     * @param n10PerSumShohiTaxKin 10%消費税合計
     * @param sumHikazeiKin 非課税売上合計
     * @param sumHukazeiKin 不課税売上合計
     * @param lineCount 行数
     * @param invoiceEnforcementFlg インボイス施行フラグ
     * @param hikaeOutputFlg 控え出力フラグ
     * @param fromImage 差出人イメージ
     * @return 出力データリストへの追加行数
     */
    private int setTaxRateTotal(D1501R30PrintDto printDto, List<List<String>> outputDataList,
        HashMap<String, Object> hashMap, BigDecimal n8PerZeinukiUriKin,
        BigDecimal n8PerSumShohiTaxKin, BigDecimal n10PerZeinukiUriKin,
        BigDecimal n10PerSumShohiTaxKin, BigDecimal sumHikazeiKin, BigDecimal sumHukazeiKin,
        int lineCount, String invoiceEnforcementFlg, String hikaeOutputFlg, String fromImage) {
        // 出力項目の編集
        hashMap.put("SUMHIKAZEIKIN", sumHikazeiKin);
        hashMap.put("SUMHUKAZEIKIN", sumHukazeiKin);
        hashMap.put("N8PERZEINUKIURIKIN", n8PerZeinukiUriKin);
        hashMap.put("N8PERSUMSHOHITAXKIN", n8PerSumShohiTaxKin);
        hashMap.put("N10PERZEINUKIURIKIN", n10PerZeinukiUriKin);
        hashMap.put("N10PERSUMSHOHITAXKIN", n10PerSumShohiTaxKin);
        // 追加行数
        int addRow = 0;
        // CSV作成処理
        int addRowNum = createCsv(printDto, outputDataList, hashMap, MEISAI_KBN_8PER_HYOUJI,
            NYUKIN_URIAGE_KBN_URIAGE_MEISAI, lineCount, invoiceEnforcementFlg, hikaeOutputFlg,
            fromImage);
        // 追加行数を加算
        addRow += addRowNum;
        // CSV作成処理
        addRowNum = createCsv(printDto, outputDataList, hashMap, MEISAI_KBN_10PER_HYOUJI,
            NYUKIN_URIAGE_KBN_URIAGE_MEISAI, lineCount + addRow, invoiceEnforcementFlg,
            hikaeOutputFlg, fromImage);
        // 追加行数を加算
        addRow += addRowNum;
        if (BigDecimal.ZERO.compareTo(sumHikazeiKin) != 0) {
            // CSV作成処理
            addRowNum = createCsv(printDto, outputDataList, hashMap, MEISAI_KBN_HIKAZEI_HYOUJI,
                NYUKIN_URIAGE_KBN_URIAGE_MEISAI, lineCount + addRow, invoiceEnforcementFlg,
                hikaeOutputFlg, fromImage);
            // 追加行数を加算
            addRow += addRowNum;
        }
        if (BigDecimal.ZERO.compareTo(sumHukazeiKin) != 0) {
            // CSV作成処理
            addRowNum = createCsv(printDto, outputDataList, hashMap, MEISAI_KBN_HUKAZEI_HYOUJI,
                NYUKIN_URIAGE_KBN_URIAGE_MEISAI, lineCount + addRow, invoiceEnforcementFlg,
                hikaeOutputFlg, fromImage);
            // 追加行数を加算
            addRow += addRowNum;
        }
        return addRow;
    }

    /**
     * 最終データであるか調べる
     * 
     * @param loopIndex ループインデックス
     * @param mapList 請求書mapList
     * @return 最終データの場合trueそれ以外はfalse
     */
    boolean isLastData(int loopIndex, List<HashMap<String, Object>> mapList) {
        int mapListSize = mapList.size();
        int add1Index = loopIndex + 1;
        return add1Index == mapListSize;
    }
}
