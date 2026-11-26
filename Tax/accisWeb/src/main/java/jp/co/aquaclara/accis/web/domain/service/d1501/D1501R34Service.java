package jp.co.aquaclara.accis.web.domain.service.d1501;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S06T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S06T001TD1501R34List3Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S06T001TD1501R34List3ResultEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R34List1Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R34List1ResultEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R34List2Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R34List2ResultEntity;
import jp.co.aquaclara.accis.common.exception.BusinessException;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.domain.dto.d1501.D1501R30PrintDto;
import jp.co.aquaclara.accis.web.domain.dto.d1501.D1501R30TargetMeisaiDto;
import jp.co.aquaclara.accis.web.domain.service.common.OnlineBatchBaseService;
import jp.co.aquaclara.accis.web.domain.service.common.OnlineBatchCommonModel;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * 請求書（CSVコンビニ)-Service.
 * 
 * @author accisdev047
 *
 */
@Service
@Slf4j
public class D1501R34Service extends OnlineBatchBaseService<D1501R34ParameterModel> {
    /**
     * 帳票出力 共通サービスクラス
     */
    @Autowired
    private D1501R00Service d1501r00Service;

    /**
     * 請求残高テーブルDao
     */
    @Autowired
    private S07T001TDao s07t001Dao;

    /**
     * 売上テーブルDao
     */
    @Autowired
    private S06T001TDao s06t001Dao;

    /**
     * 処理ID
     */
    private static final String PROCESS_ID = "D1501B05";

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
     * コンビニCSV作成処理
     * 
     * @param paramModel 請求書（コンビニCSV）出力ParameterModel.
     * @param commonInfo 非同期処理の共通情報クラスModel
     */
    @Override
    protected void process(D1501R34ParameterModel paramModel, OnlineBatchCommonModel commonInfo)
        throws BusinessException, Exception {
        // 事業者登録番号取得
        S02M046TEntity shopMaster = getShopMasterRecord(paramModel.getPrintDto());
        String jigyoshaInptNo = ItemUtil.nullToString(shopMaster.getInvoicejigyoshainptno());
        // 中間データ格納用変数
        List<List<String>> csvDataList = new ArrayList<List<String>>();

        for (D1501R30TargetMeisaiDto dto : paramModel.getD1501r30TargetMeisaiDtoList()) {
            // 請求書（CSVコンビニ)出力データ 抽出処理1
            S07T001TD1501R34List1ResultEntity result1 =
                searchCsvcvs1(paramModel.getPrintDto(), dto);
            // 請求書（CSVコンビニ)出力データ 抽出処理2
            S07T001TD1501R34List2ResultEntity result2 =
                searchCsvcvs2(paramModel.getPrintDto(), dto);
            // 出力判定
            if (!isOutputTaeget(result1, result2)) {
                continue;
            }
            // 請求書（CSVコンビニ)出力データ 抽出処理3
            List<S06T001TD1501R34List3ResultEntity> result3 =
                searchCsvcvs3(paramModel.getPrintDto(), dto, result1);
            csvDataList.add(
                printDataEdit(paramModel.getPrintDto(), result1, result2, result3, jigyoshaInptNo));
        }
        // CSVを出力する
        super.createCsvFile(csvDataList, commonInfo.getFileNamne());
    }

    /**
     * 出力判定を行う
     * 
     * @param result1 請求書（CSVコンビニ)出力データ1
     * @param result2 請求書（CSVコンビニ)出力データ2
     * @return 出力対象データ：true 出力対象外データ：false
     */
    private boolean isOutputTaeget(S07T001TD1501R34List1ResultEntity result1,
        S07T001TD1501R34List2ResultEntity result2) {
        int kurikoshikin = Integer.parseInt(result2.getKurikoshikin());
        if (CommonConstants.SKYHOHOKBN_ZANDAKA.equals(result1.getSkyhohokbn())
            && kurikoshikin <= 0) {
            return false;
        }
        String sum = d1501r00Service.getSum(new ArrayList<String>(
            Arrays.asList(result2.getKonkaitaxnukiurikin(), result2.getKonkaishohitaxkin())));
        int totalKin = Integer.parseInt(sum);
        if (CommonConstants.SKYHOHOKBN_TOUGETSU.equals(result1.getSkyhohokbn()) && totalKin <= 0) {
            return false;
        }
        return true;
    }

    /**
     * 請求書コンビニデータ取得1を実行し実行結果を返却
     * 
     * @param printDto 請求書印刷DTO.
     * @param targetMeisaiDto 請求書対象明細DTO.
     * @return カウント結果
     */
    private S07T001TD1501R34List1ResultEntity searchCsvcvs1(D1501R30PrintDto printDto,
        D1501R30TargetMeisaiDto targetMeisaiDto) {
        S07T001TD1501R34List1Entity entity = new S07T001TD1501R34List1Entity();
        entity = createCsvcvs1Eentity(printDto, targetMeisaiDto);
        return s07t001Dao.selectForD1501R34List1(entity).get(0);
    }

    /**
     * 請求書CSVコンビニデータ取得1Entityを生成する
     * 
     * @param printDto 請求書印刷DTO.
     * @param targetMeisaiDto 請求書対象明細DTO.
     * @return 請求書Entity
     */
    private S07T001TD1501R34List1Entity createCsvcvs1Eentity(D1501R30PrintDto printDto,
        D1501R30TargetMeisaiDto targetMeisaiDto) {
        S07T001TD1501R34List1Entity entity = new S07T001TD1501R34List1Entity();
        // AP基本コード
        entity.setApkihoncd(printDto.getApkihoncd());
        // ショップコード
        entity.setShopcd(printDto.getShopcd());
        // 請求先コード
        entity.setSkysakicd(targetMeisaiDto.getSkysakicd());
        // 締年月
        entity.setShimedydm(printDto.getShimedydm());
        // 締日
        entity.setShimedd(printDto.getSkyshimeday());
        return entity;
    }

    /**
     * 請求書コンビニデータ取得2を実行し実行結果を返却
     * 
     * @param printDto 請求書印刷DTO.
     * @param targetMeisaiDto 請求書対象明細DTO.
     * @return カウント結果
     */
    private S07T001TD1501R34List2ResultEntity searchCsvcvs2(D1501R30PrintDto printDto,
        D1501R30TargetMeisaiDto targetMeisaiDto) {
        S07T001TD1501R34List2Entity entity = new S07T001TD1501R34List2Entity();
        entity = createCsvcvs2Eentity(printDto, targetMeisaiDto);
        return s07t001Dao.selectForD1501R34List2(entity).get(0);
    }

    /**
     * 請求書CSVコンビニデータ取得2Entityを生成する
     * 
     * @param printDto 請求書印刷DTO.
     * @param targetMeisaiDto 請求書対象明細DTO.
     * @return 請求書Entity
     */
    private S07T001TD1501R34List2Entity createCsvcvs2Eentity(D1501R30PrintDto printDto,
        D1501R30TargetMeisaiDto targetMeisaiDto) {
        S07T001TD1501R34List2Entity entity = new S07T001TD1501R34List2Entity();
        // AP基本コード
        entity.setApkihoncd(printDto.getApkihoncd());
        // ショップコード
        entity.setShopcd(printDto.getShopcd());
        // 請求先コード
        entity.setSkysakicd(targetMeisaiDto.getSkysakicd());
        // 締年月
        entity.setShimedydm(printDto.getShimedydm());
        // 締日
        entity.setShimedd(printDto.getSkyshimeday());
        return entity;
    }

    /**
     * 請求書コンビニデータ取得3を実行し実行結果を返却
     * 
     * @param printDto 請求書印刷DTO.
     * @param targetMeisaiDto 請求書対象明細DTO.
     * @return カウント結果
     */
    private List<S06T001TD1501R34List3ResultEntity> searchCsvcvs3(D1501R30PrintDto printDto,
        D1501R30TargetMeisaiDto targetMeisaiDto, S07T001TD1501R34List1ResultEntity resultentity) {
        S06T001TD1501R34List3Entity entity = new S06T001TD1501R34List3Entity();
        entity = createCsvcvs3Eentity(printDto, targetMeisaiDto, resultentity);
        return s06t001Dao.selectForD1501R34List3(entity);
    }

    /**
     * 請求書CSVコンビニデータ取得3Entityを生成する
     * 
     * @param printDto 請求書印刷DTO.
     * @param targetMeisaiDto 請求書対象明細DTO.
     * @return 請求書Entity
     */
    private S06T001TD1501R34List3Entity createCsvcvs3Eentity(D1501R30PrintDto printDto,
        D1501R30TargetMeisaiDto targetMeisaiDto, S07T001TD1501R34List1ResultEntity resultEntity) {
        S06T001TD1501R34List3Entity entity = new S06T001TD1501R34List3Entity();
        // AP基本コード
        entity.setApkihoncd(printDto.getApkihoncd());
        // ショップコード
        entity.setShopcd(printDto.getShopcd());
        // 請求先コード
        entity.setSkysakicd(targetMeisaiDto.getSkysakicd());
        // 締年月
        entity.setShimedydm(printDto.getShimedydm());
        // 締日
        entity.setShimedd(printDto.getSkyshimeday());
        // 運用日付
        entity.setUnyoymd(resultEntity.getUnyoymd());
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
     * コンビニCSV帳票出力用のListを作成
     * 
     * @param printDto 請求書印刷DTO.
     * @param d1501r34list1ResultEntity 請求書（CSVコンビニ)出力データResultEntity1
     * @param d1501r34list2ResultEntity 請求書（CSVコンビニ)出力データResultEntity2
     * @param d1501r34list3ResultEntityList 請求書（CSVコンビニ)出力データResultEntity3
     * @param jigyoshaInptNo 事業所登録番号
     * @return コンビニCSV帳票出力用のList
     */
    public List<String> printDataEdit(D1501R30PrintDto printDto,
        S07T001TD1501R34List1ResultEntity d1501r34list1ResultEntity,
        S07T001TD1501R34List2ResultEntity d1501r34list2ResultEntity,
        List<S06T001TD1501R34List3ResultEntity> d1501r34list3ResultEntityList,
        String jigyoshaInptNo) {
        // データ用リスト変数を初期化
        List<String> lineData = new ArrayList<String>();
        int lineNum = 0;
        // 行データ1~11を格納
        lineData.addAll(
            get1stTo11thElementsInLineData(d1501r34list1ResultEntity, d1501r34list2ResultEntity));
        // 自由項目1を格納
        lineData
            .add(getCvsFreeItem1(ItemUtil.nullToString(d1501r34list1ResultEntity.getSkyhohokbn()),
                ItemUtil.nullToString(d1501r34list2ResultEntity.getKurikoshikin()),
                ItemUtil.nullToString(d1501r34list2ResultEntity.getKonkaitaxnukiurikin()),
                ItemUtil.nullToString(d1501r34list2ResultEntity.getKonkaishohitaxkin()),
                jigyoshaInptNo));
        // 変数設定
        lineNum = 1;
        String kurikoshiKin = d1501r00Service.getDifference(
            ItemUtil.nullToString(d1501r34list2ResultEntity.getZnkkurikoshikin()),
            ItemUtil.nullToString(d1501r34list2ResultEntity.getKonkainyknkin()));
        // 前回繰越金額の編集
        if (CommonConstants.SKYHOHOKBN_ZANDAKA.equals(d1501r34list1ResultEntity.getSkyhohokbn())) {
            if (!"0".equals(kurikoshiKin)) {
                lineData.add(getZenkaiKurikoshiKin(kurikoshiKin));
                lineNum++;
            }
        }
        // 当月金額合計の編集
        if ((CommonConstants.SKYHOHOKBN_ZANDAKA.equals(d1501r34list1ResultEntity.getSkyhohokbn())
            && d1501r34list3ResultEntityList.size() > 0)
            || CommonConstants.SKYHOHOKBN_TOUGETSU
                .equals(d1501r34list1ResultEntity.getSkyhohokbn())) {
            // 当月金額合計
            lineData.add(getTougetuTotal(
                ItemUtil.nullToString(d1501r34list2ResultEntity.getKonkaitaxnukiurikin()),
                ItemUtil.nullToString(d1501r34list2ResultEntity.getKonkaishohitaxkin())));
            if (CommonConstants.KBN_TAXKEISAN_IN
                .equals(d1501r34list2ResultEntity.getTaxkeisankbn())) {
                // 税率内訳（税計算区分１の場合）
                lineData.add(getIncludedTaxDetail(
                    ItemUtil.nullToString(d1501r34list2ResultEntity.getN8pershohitaxkin()),
                    ItemUtil.nullToString(d1501r34list2ResultEntity.getN8perzeinukiurikin()),
                    ItemUtil.nullToString(d1501r34list2ResultEntity.getN10pershohitaxkin()),
                    ItemUtil.nullToString(d1501r34list2ResultEntity.getN10perzeinukiurikin())));
            }
            if (CommonConstants.KBN_TAX_HIKAZEI
                .equals(d1501r34list2ResultEntity.getTaxkeisankbn())) {
                // 税率内訳（税計算区分２の場合）
                lineData.add(getExcludedTaxDetail(
                    ItemUtil.nullToString(d1501r34list2ResultEntity.getN8pershohitaxkin()),
                    ItemUtil.nullToString(d1501r34list2ResultEntity.getN8perzeinukiurikin()),
                    ItemUtil.nullToString(d1501r34list2ResultEntity.getN10pershohitaxkin()),
                    ItemUtil.nullToString(d1501r34list2ResultEntity.getN10perzeinukiurikin())));
            }
            lineData.add("　＜明細＞　　　　　　　　　（※印は軽減税率対象商品です。）");
            lineNum += 3;
        }

        int meisaiCount = 0;
        for (S06T001TD1501R34List3ResultEntity entity : d1501r34list3ResultEntityList) {
            // 売上明細行の編集
            lineData.add(getUriageMeisai(entity));
            lineNum++;
            meisaiCount++;
            if (lineNum == 9) {
                break;
            }
        }

        if (lineNum == 9) {
            // 明細あふれ閾値の設定
            int meisaiLimit =
                getMeisaiLimit(d1501r34list1ResultEntity.getSkyhohokbn(), kurikoshiKin);
            if (d1501r34list3ResultEntityList.size() == meisaiLimit) {
                // 売上明細行の編集
                lineData.add(getUriageMeisai(d1501r34list3ResultEntityList.get(meisaiCount)));
                // CSV管理ID
                lineData.add("");
                lineNum += 2;
            }
            if (d1501r34list3ResultEntityList.size() > meisaiLimit) {
                // 売上明細行の編集
                lineData.add("以下、ご利用明細書をご覧ください。");
                // CSV管理ID
                lineData.add("MEISAI OVER");
                lineNum += 2;
            }
        }
        for (int i = lineNum; i <= 10; i++) {
            lineData.add("");
            lineNum++;
        }
        return lineData;
    }

    /**
     * 明細あふれ閾値を返却する
     * 
     * @param skyhohoKbn 請求方法区分
     * @param kurikoshiKin 繰越金額
     * @return 明細あふれ閾値
     */
    private int getMeisaiLimit(String skyhohoKbn, String kurikoshiKin) {
        return CommonConstants.SKYHOHOKBN_ZANDAKA.equals(skyhohoKbn) && !"0".equals(kurikoshiKin)
            ? 5
            : 6;
    }

    /**
     * 売上明細行を返却する
     * 
     * @param entity 請求書（CSVコンビニ)出力データResultEntity3.
     * @return 売上明細行
     */
    private String getUriageMeisai(S06T001TD1501R34List3ResultEntity entity) {
        // 軽減税率の印を取得
        String mark = getMark(ItemUtil.nullToString(entity.getYmd()),
            ItemUtil.nullToString(entity.getTaxkbn()));
        // 売上明細行を取得
        String uriageMeisai = createUriageMeisaiValue(ItemUtil.nullToString(entity.getYmd()),
            ItemUtil.nullToString(entity.getShnnm()), mark,
            ItemUtil.nullToString(entity.getKeijokbn()), ItemUtil.nullToString(entity.getSuryo()),
            ItemUtil.nullToString(entity.getUritrtskitaninm()),
            ItemUtil.nullToString(entity.getKin()));
        return uriageMeisai;
    }

    /**
     * 売上明細に設定する値を成形して返却する
     * 
     * @param ymd 売上日
     * @param shnnm 商品名
     * @param mark 軽減税率印
     * @param keijokbn 計上区分
     * @param suryo 数量
     * @param uritrtskitaninm 単位名称
     * @param kin 売上金額
     * @return 売上明細に設定する値
     */
    private String createUriageMeisaiValue(String ymd, String shnnm, String mark, String keijokbn,
        String suryo, String uritrtskitaninm, String kin) {
        String meisaiYmd = getMMDD(ymd);
        String meisaiShnnm = padBack(shnnm, 30, ' ');
        String meisaiKeijokbn = padBack(keijokbn, 12, ' ');
        String meisaiSuryo = getSuryo(suryo);
        String meisaiUritrtskitaninm = padBack(uritrtskitaninm, 4, ' ');
        String meisaiKin = getCvsFreeItemAmountValue(kin);
        String result = meisaiYmd + " " + meisaiShnnm + mark + meisaiKeijokbn + meisaiSuryo
            + meisaiUritrtskitaninm + " " + meisaiKin + "円";
        return result;
    }

    /**
     * 数量を返却する
     * 
     * @param suryo 数量
     * @return 数量(小数点以下を取り除き８桁未満の場合前方を半角スペースで埋めた状態)
     */
    private String getSuryo(String suryo) {
        String formating = formatSuryo(suryo);
        String result = padFront(formating, 8, ' ');
        return result;
    }

    /**
     * 数量をフォーマットして返却する
     * 
     * @param suryo 数量
     * @return フォーマット処理した数量
     */
    private String formatSuryo(String suryo) {
        if (StringUtils.isEmpty(suryo)) {
            return "";
        }
        int dotindex = suryo.indexOf('.');
        if (dotindex >= 0) {
            if ("0".equals(suryo.substring(dotindex))) {
                return suryo.substring(0, dotindex);
            }
        }
        return suryo;
    }

    /**
     * 引数に与えられたStringの長さがlengthになるように、文字列の最後尾にpadを加えた新しいStringを返します。
     * lengthが引数のStringの長さに等しいかまたは小さい場合、引数のStringを返します。
     * 
     * @param target 詰め物をしたいString
     * @param length 生成したいStringの長さ
     * @param pad 詰めたいchar
     * @return 引数のtargetの最後尾にpadを詰めてlengthの長さにした新しいString
     */
    private String padBack(String target, int length, char pad) {
        String s = StringUtils.isEmpty(target) ? "" : target;
        int padCount;
        try {
            padCount = length - s.getBytes("MS932").length;
        } catch (UnsupportedEncodingException e) {
            log.warn(e.getMessage(), e);
            throw BusinessExceptionFactory.validationFailed(MessageID.E002);
        }
        if (padCount > 0) {
            StringBuffer b = new StringBuffer(s);
            for (int i = 0; i < padCount; i++) {
                b.append(pad);
            }
            return b.toString();
        } else {
            return s;
        }
    }

    /**
     * 軽減税率印を返却する
     * 
     * @param ymd 売上日
     * @param taxKbn 税区分
     * @return 軽減税率印
     */
    private String getMark(String ymd, String taxKbn) {
        String result = "　";
        if ("20190930".compareTo(ymd) < 0 && CommonConstants.KBN_TAX_KEIGEN.equals(taxKbn)) {
            result = "※";
        }
        return result;
    }

    /**
     * 税計算区分が内税の場合の当月合計金額設定値を返却する
     * 
     * @param n8pershohitaxkin ８％消費税金額
     * @param n8perzeinukiurikin ８％税抜売上金額
     * @param n10pershohitaxkin １０％消費税金額
     * @param n10perzeinukiurikin １０％税抜売上金額
     * @return 設定値
     */
    private String getIncludedTaxDetail(String n8pershohitaxkin, String n8perzeinukiurikin,
        String n10pershohitaxkin, String n10perzeinukiurikin) {
        String n8 = d1501r00Service
            .getSum(new ArrayList<String>(Arrays.asList(n8pershohitaxkin, n8perzeinukiurikin)));
        String n10 = d1501r00Service
            .getSum(new ArrayList<String>(Arrays.asList(n10pershohitaxkin, n10perzeinukiurikin)));
        String result = "税率別内訳＜8%税込金額 : " + getCvsFreeItemAmountValue(n8) + "円　10%税込金額 : "
            + getCvsFreeItemAmountValue(n10) + "円＞";
        return result;
    }

    /**
     * 税計算区分が外税の場合の当月合計金額設定値を返却する
     * 
     * @param n8pershohitaxkin ８％消費税金額
     * @param n8perzeinukiurikin ８％税抜売上金額
     * @param n10pershohitaxkin １０％消費税金額
     * @param n10perzeinukiurikin １０％税抜売上金額
     * @return 設定値
     */
    private String getExcludedTaxDetail(String n8pershohitaxkin, String n8perzeinukiurikin,
        String n10pershohitaxkin, String n10perzeinukiurikin) {
        String result = "税率別内訳 8%売上 " + getCvsFreeItemAmountValue(n8perzeinukiurikin) + " 税 "
            + getCvsFreeItemAmountValue(n8pershohitaxkin) + " 10%売上 "
            + getCvsFreeItemAmountValue(n10perzeinukiurikin) + " 税 "
            + getCvsFreeItemAmountValue(n10pershohitaxkin);
        return result;
    }

    /**
     * 当月金額合計を返却する
     * 
     * @param konkaiTaxNukiUriKin 今回消費税抜き売上金額
     * @param konkaiShohiTaxKin 今回消費税金額
     * @return 当月合計金額
     */
    private String getTougetuTotal(String konkaiTaxNukiUriKin, String konkaiShohiTaxKin) {
        String amount = d1501r00Service
            .getSum(new ArrayList<String>(Arrays.asList(konkaiTaxNukiUriKin, konkaiShohiTaxKin)));
        String result = "当月合計　 : " + getCvsFreeItemAmountValue(amount) + "円";
        return result;
    }

    /**
     * 前回繰越金額を返却する
     * 
     * @param kurikoshiKin 前回繰越金額
     * @return 前回繰越金額
     */
    private String getZenkaiKurikoshiKin(String kurikoshiKin) {
        String result = "お繰越金額 : " + getCvsFreeItemAmountValue(kurikoshiKin) + "円";
        return result;
    }


    /**
     * 自由項目１の設定値を返却する
     * 
     * @param skyhohoKbn 請求方法区分
     * @param kurikoshiKin 繰越金
     * @param konkaiTaxNukiUriKin 今回消費税抜き売上金額
     * @param konkaiShohiTaxKin 今回消費税金額
     * @param jigyoshaInptNo 事業者登録番号
     * @return 自由項目１の設定値
     */
    private String getCvsFreeItem1(String skyhohoKbn, String kurikoshiKin,
        String konkaiTaxNukiUriKin, String konkaiShohiTaxKin, String jigyoshaInptNo) {
        String result;
        String amount;
        if (CommonConstants.SKYHOHOKBN_ZANDAKA.equals(skyhohoKbn)) {
            amount = kurikoshiKin;
        } else {
            amount = d1501r00Service.getSum(
                new ArrayList<String>(Arrays.asList(konkaiTaxNukiUriKin, konkaiShohiTaxKin)));
        }
        result = "＜＜ 今回ご請求金額 : " + getCvsFreeItemAmountValue(amount) + "円 ＞＞ 登録番号 " + jigyoshaInptNo;
        return result;
    }

    /**
     * 自由項目で使用する金額を加工して返却する
     * 
     * @param value 加工対象
     * @return 下６桁の金額
     */
    private String getCvsFreeItemAmountValue(String value) {
        String result = "";
        if (value.length() >= 6) {
            result = value.substring(value.length() - 6);
        } else {
            result = padFront(value, 6, ' ');
        }
        return replaceCommaToSpace(result);
    }

    /**
     * 引数の文字列内に含まれる半角カンマを半角スペースに置換して返却する
     * 
     * @param value 置換対象文字列
     * @return 置換後文字列
     */
    private String replaceCommaToSpace(String value) {
        return value.replace(",", " ");
    }

    /**
     * 行データ１から１１行目までの設定をListに設定して返却する
     * 
     * @param result1 請求書（CSVコンビニ)出力データEntity１
     * @param result2 請求書（CSVコンビニ)出力データEntity１
     * @return list6
     */
    private List<String> get1stTo11thElementsInLineData(S07T001TD1501R34List1ResultEntity result1,
        S07T001TD1501R34List2ResultEntity result2) {
        List<String> list = new ArrayList<String>();
        // CVSコード
        list.add(getCvsCode(result1.getSkysakicd(), result1.getUnyoymd()));
        // CVS名称
        list.add(replaceCommaToSpace(ItemUtil.nullToString(result1.getSkysakinmn1())));
        // CVS郵便番号
        list.add(replaceCommaToSpace(ItemUtil.nullToString(result1.getYubinno())));
        // CVS住所１
        list.add(replaceCommaToSpace(ItemUtil.nullToString(result1.getAdrsn1())));
        // CVS住所２
        list.add(replaceCommaToSpace(ItemUtil.nullToString(result1.getAdrsn2())));
        // CVS金額
        list.add(getCvsKin(result1.getSkyhohokbn(), result2.getKurikoshikin(),
            result2.getKonkaitaxnukiurikin(), result2.getKonkaishohitaxkin()));
        // CVS印紙ﾌﾗｸﾞ
        list.add("");
        // CVS消費税
        list.add("");
        // CVS支払期限
        list.add(ItemUtil.nullToString(result1.getSiharauymd()));
        // CVS払込票発行日
        list.add(ItemUtil.nullToString(result1.getUnyoymd()));
        // CVS印刷日
        list.add(ItemUtil.nullToString(result1.getUnyoymd()));
        return list;
    }

    /**
     * CVSコードを取得する
     * 
     * @param skysakiCd 請求先コード
     * @param unyoymd 運用日付
     * @return 請求先コード 請求先コード ＋ 運用日付の先頭４桁目から５桁
     */
    private String getCvsCode(String skysakiCd, String unyoymd) {
        return skysakiCd + unyoymd.substring(3, 8);
    }

    /**
     * CVS金額の設定値を取得する
     * 
     * @param skyhohoKbn 請求方法区分
     * @param kurikoshiKin 繰越金
     * @param konkaiTaxNukiUriKin 今回税抜き売上金額
     * @param konkaiShohiTaxKin 今回消費税金額
     * @return 請求方法区分１:繰越金額 請求方法区分２：今回税抜売上金額＋今回消費税金額
     * 
     */
    private String getCvsKin(String skyhohoKbn, String kurikoshiKin, String konkaiTaxNukiUriKin,
        String konkaiShohiTaxKin) {
        return CommonConstants.SKYHOHOKBN_ZANDAKA.equals(skyhohoKbn) ? kurikoshiKin
            : d1501r00Service.getSum(
                new ArrayList<String>(Arrays.asList(konkaiTaxNukiUriKin, konkaiShohiTaxKin)));
    }

    /**
     * 日付をフォーマットします。
     * 
     * @param strDate 日付 yyyyMMdd形式
     * @return z9/z9形式の日付
     */
    private String getMMDD(String strDate) {
        if (StringUtils.isEmpty(strDate) || strDate.length() != 8) {
            return "";
        }
        String month = strDate.substring(4, 6);
        String day = strDate.substring(6, 8);
        // 2桁に満たない場合前方にスペースを追加
        month = padFront(String.valueOf(Integer.valueOf(month).intValue()), 2, ' ');
        day = padFront(String.valueOf(Integer.valueOf(day).intValue()), 2, ' ');
        return month + "/" + day;
    }

    /**
     * 引数に与えられたStringの長さがlengthになるように、文字列の先頭にpadを加えた新しいStringを返します。
     * lengthが引数のStringの長さに等しいかまたは小さい場合、引数のStringを返します。 またnullを第一引数に渡すとすべてpadがパッドされたStringが返されます。
     * 
     * @param s 詰め物をしたいString
     * @param length 生成したいStringの長さ
     * @param pad 詰めたいchar
     * @return 引数のStirngの先頭にpadを詰めてlengthの長さにした新しいString
     */
    private String padFront(String s, int length, char pad) {
        if (s == null) {
            s = "";
        }
        int padCount = length - s.length();
        if (padCount > 0) {
            StringBuffer b = new StringBuffer("");
            for (int i = 0; i < padCount; i++) {
                b.append(pad);
            }
            b.append(s);
            return b.toString();
        } else {
            return s;
        }
    }
}
