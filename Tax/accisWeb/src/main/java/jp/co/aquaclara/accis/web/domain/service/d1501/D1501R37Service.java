package jp.co.aquaclara.accis.web.domain.service.d1501;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R40ListResultEntity;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.common.util.ListCreatorUtil;

/**
 * 帳票出力 お支払明細書-Service.
 * 
 * @author accisdev047
 *
 */
@Service
public class D1501R37Service {
    /**
     * 帳票出力 共通サービスクラス
     */
    @Autowired
    private D1501R00Service d1501r00Service;

    /**
     * ご利用明細CSVを作成する
     * 
     * @param s07t001td1501r40ResultEntity お支払明細書印刷データ取得結果Entityのリスト
     * @param shimedydm 締年月
     * @param shimedd 締日
     * @param reportType 帳票区分
     * @param systemDate システム時間
     * @param sshdshhitoImage 差出人イメージ
     * @return ご利用明細CSVデータリスト
     */
    public ArrayList<List<String>> printDataEdit(
        List<S07T001TD1501R40ListResultEntity> s07t001td1501r40ResultEntity, String shimedydm,
        String shimedd, String reportType, String systemDate, String sshdshhitoImage) {
        // ご利用明細CSVデータ
        ArrayList<List<String>> csvDataList = new ArrayList<List<String>>();
        for (S07T001TD1501R40ListResultEntity entity : s07t001td1501r40ResultEntity) {
            csvDataList.add(
                createCsvData(entity, shimedydm, shimedd, systemDate, sshdshhitoImage, reportType));
        }
        return csvDataList;
    }

    /**
     * ご利用明細CSVの設定値リストを返却する
     * 
     * @param entity S07T001TD1501R40ResultEntity
     * @param shimedydm 締年月
     * @param shimedd 締日
     * @param systemDate システム時間
     * @param sshdshhitoImage 差出人イメージ
     * @param reportType 帳票区分
     * @return ご利用明細CSVの設定値リスト
     */
    private List<String> createCsvData(S07T001TD1501R40ListResultEntity entity, String shimedydm,
        String shimedd, String systemDate, String sshdshhitoImage, String reportType) {
        List<String> csvData = new ArrayList<String>();
        // プログラムID
        csvData.add(ListCreatorUtil.wrapQuote(D1501R00Constants.PROGRAM_ID_OSHIHARAIMEISAISYO));
        // 発行日
        csvData.add(ListCreatorUtil.wrapQuote(systemDate));
        // 締日
        csvData.add(ListCreatorUtil.wrapQuote(shimedydm + shimedd));
        // 請求先郵便番号
        csvData.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getYubinno())));
        // ロゴ
        csvData.add(ListCreatorUtil.wrapQuote(sshdshhitoImage));
        // 請求先住所1
        csvData.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getAdrsn1())));
        // 請求先住所2
        csvData.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getAdrsn2())));
        // 請求先住所3
        csvData.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getAdrsn3())));
        // AP基本コード
        csvData.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getApkihoncd())));
        // ショップコード
        csvData.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getShopcd())));
        // 請求先コード
        csvData.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getSkysakicd())));
        // 請求先名称1
        csvData.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getSkysakinmn1())));
        // 請求先名称2
        csvData.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getSkysakinmn2())));
        // 敬称
        csvData.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getSkysakikeishonm())));
        // 帳票タイトル
        csvData.add(ListCreatorUtil.wrapQuote("お支払明細書"));
        // 消費税項目タイトル
        csvData.add(ListCreatorUtil.wrapQuote(
            CommonConstants.KBN_TAXKEISAN_IN.equals(entity.getTaxkeisankbn()) ? "内消費税額" : "消費税額"));
        // 前回ご請求額格納
        csvData.add(ListCreatorUtil.wrapQuote(getLastBilledAmountStore(entity)));
        // ご入金額格納
        csvData.add(ListCreatorUtil.wrapQuote(getDepositAmountStore(entity)));
        // お繰越金額格納
        csvData.add(ListCreatorUtil.wrapQuote(getCarryforwardsStore(entity)));
        // 今回ご利用額格納
        csvData.add(ListCreatorUtil.wrapQuote(getKonkaigoriyougakuKakunou(entity)));
        // 内消費税格納
        csvData.add(ListCreatorUtil.wrapQuote(d1501r00Service
            .amountFormatter(ItemUtil.nullToString(entity.getKonkaishohitaxkinsum()))));
        // 今回ご請求額格納
        csvData.add(ListCreatorUtil.wrapQuote(getBilledAmountStore(entity)));
        // 請求書番号
        csvData.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getSkyshono())));
        // 得意先コード
        csvData.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getTokuisakicd())));
        // 得意先名称
        csvData.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getTokuisakinmn1())));
        // 前回ご請求額明細
        csvData.add(ListCreatorUtil.wrapQuote(getLastBilledAmount(entity)));
        // ご入金額明細
        csvData.add(ListCreatorUtil.wrapQuote(getDepositAmount(entity)));
        // お繰越金額明細
        csvData.add(ListCreatorUtil.wrapQuote(getCarryforwards(entity)));
        // 今回ご利用額明細
        csvData.add(ListCreatorUtil.wrapQuote(getKonkaigoriyougakuMeisai(entity)));
        // 内消費税明細
        csvData
            .add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getKonkaishohitaxkin())));
        // 今回ご請求額明細
        csvData.add(ListCreatorUtil
            .wrapQuote(d1501r00Service.amountFormatter(entity.getKonkaiSkykinMeisai())));
        // 振替予定金額名称
        csvData.add(ListCreatorUtil.wrapQuote(getScheduledTransferAmountName(reportType)));
        // 振替予定金額
        csvData.add(ListCreatorUtil.wrapQuote(getScheduledTransferAmount(reportType, entity)));
        // 郵便番号バーコード
        csvData.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getYubinnobakodo())));
        return csvData;
    }

    /**
     * 今回ご利用額格納に設定する値を返却する
     * 
     * @param entity S07T001TD1501R40ResultEntity
     * @return 今回ご利用額格納
     */
    private String getKonkaigoriyougakuKakunou(S07T001TD1501R40ListResultEntity entity) {
        String amount = getUsageAmount(ItemUtil.nullToString(entity.getTaxkeisankbn()),
            ItemUtil.nullToString(entity.getKonkaitaxnukiurikinsum()),
            ItemUtil.nullToString(entity.getKonkaishohitaxkinsum()));
        return d1501r00Service.amountFormatter(amount);
    }

    /**
     * 今回ご利用額明細に設定する値を返却する
     * 
     * @param entity S07T001TD1501R40ResultEntity
     * @return 今回ご利用額明細
     */
    private String getKonkaigoriyougakuMeisai(S07T001TD1501R40ListResultEntity entity) {
        return getUsageAmount(ItemUtil.nullToString(entity.getTaxkeisankbn()),
            ItemUtil.nullToString(entity.getKonkaitaxnukiurikin()),
            ItemUtil.nullToString(entity.getKonkaishohitaxkin()));
    }

    /**
     * 今回ご利用額格納 今回ご利用明細に設定する値を返却する
     * 
     * @param taxkeisankbn 税計算区分
     * @param nonTaxAmount 今回税抜き売上金額 今回税抜き売上金額(合計)
     * @param tax 今回消費税 今回消費税(合計)
     * @return 今回ご利用額格納または今回ご利用明細に設定する値
     */
    private String getUsageAmount(String taxkeisankbn, String nonTaxAmount, String tax) {
        String s = "";
        if (CommonConstants.KBN_TAXKEISAN_IN.equals(taxkeisankbn)) {
            s = d1501r00Service.getSum(new ArrayList<String>(Arrays.asList(nonTaxAmount, tax)));
        }
        if (CommonConstants.KBN_TAXKEISAN_EX.equals(taxkeisankbn)) {
            s = nonTaxAmount;
        }
        return s;
    }

    /**
     * お繰越金額に設定する値を返却する
     * 
     * @param entity S07T001TD1501R40ResultEntity
     * @return お繰越金額
     */
    private String getCarryforwards(S07T001TD1501R40ListResultEntity entity) {
        return getMaskedCarryforwardsOrUnmaskedCarryforwards(
            ItemUtil.nullToString(entity.getSkyhohokbn()),
            ItemUtil.nullToString(entity.getZnkkurikoshikin()),
            ItemUtil.nullToString(entity.getKonkainyknkin()));
    }

    /**
     * お繰越金額格納に設定する値を返却する
     * 
     * @param entity S07T001TD1501R40ResultEntity
     * @return お繰越金額格納
     */
    private String getCarryforwardsStore(S07T001TD1501R40ListResultEntity entity) {
        return getMaskedCarryforwardsOrUnmaskedCarryforwards(
            ItemUtil.nullToString(entity.getSkyhohokbn()),
            ItemUtil.nullToString(entity.getZnkkurikoshikinsum()),
            ItemUtil.nullToString(entity.getKonkainyknkinsum()));
    }

    /**
     * お繰越金額明細、お繰越金額格納に設定する値を返却する
     * 
     * @param skyhohokbn 請求方法区分
     * @param lastTimeCarriedForward 前回繰越金 前回繰越金（合計）
     * @param deposit 今回入金金額 今回入金金額（合計）
     * @return お繰越金額明細
     */
    private String getMaskedCarryforwardsOrUnmaskedCarryforwards(String skyhohokbn,
        String lastTimeCarriedForward, String deposit) {
        String s = "*********";
        if (CommonConstants.SKYHOHOKBN_ZANDAKA.equals(skyhohokbn)) {
            s = d1501r00Service.getDifference(lastTimeCarriedForward, deposit);
            return d1501r00Service.amountFormatter(s);
        }
        return s;
    }

    /**
     * 請求方法区分でマスクする値を判定し返却する
     * 
     * @param skyhohokbn 請求方法区分
     * @param unmaskedValue マスクしない場合に返却する値
     * @return 請求方法区分が残高請求の場合マスクしていない文字列 以外はマスクした文字列
     */
    private String getMaskedOrUnmasked(String skyhohokbn, String unmaskedValue) {
        return CommonConstants.SKYHOHOKBN_ZANDAKA.equals(skyhohokbn) ? unmaskedValue : "*********";
    }

    /**
     * 前回ご請求金額格納に設定する値を返却する
     * 
     * @param entity S07T001TD1501R40ListResultEntity
     * @return 前回ご請求金額格納
     */
    private String getLastBilledAmountStore(S07T001TD1501R40ListResultEntity entity) {
        String amount =
            d1501r00Service.amountFormatter(ItemUtil.nullToString(entity.getZnkkurikoshikinsum()));
        return getMaskedOrUnmasked(ItemUtil.nullToString(entity.getSkyhohokbn()), amount);
    }

    /**
     * ご入金額格納に設定する値を返却する
     * 
     * @param entity S07T001TD1501R40ListResultEntity
     * @return ご入金額格納
     */
    private String getDepositAmountStore(S07T001TD1501R40ListResultEntity entity) {
        String amount =
            d1501r00Service.amountFormatter(ItemUtil.nullToString(entity.getKonkainyknkinsum()));
        return getMaskedOrUnmasked(ItemUtil.nullToString(entity.getSkyhohokbn()), amount);
    }

    /**
     * 今回ご請求額格納に設定する値を返却する
     * 
     * @param entity S07T001TD1501R40ListResultEntity
     * @return 今回ご請求額格納
     */
    private String getBilledAmountStore(S07T001TD1501R40ListResultEntity entity) {
        String s = "";
        if (CommonConstants.SKYHOHOKBN_ZANDAKA.equals(entity.getSkyhohokbn())) {
            s = ItemUtil.nullToString(entity.getKurikoshikinsum());
        }
        if (!CommonConstants.SKYHOHOKBN_ZANDAKA.equals(entity.getSkyhohokbn())) {
            s = d1501r00Service.getSum(new ArrayList<String>(
                Arrays.asList(ItemUtil.nullToString(entity.getKonkaitaxnukiurikinsum()),
                    ItemUtil.nullToString(entity.getKonkaishohitaxkinsum()))));
        }
        return d1501r00Service.amountFormatter(s);
    }


    /**
     * 前回ご請求金額に設定する値を返却する
     * 
     * @param entity S07T001TD1501R40ListResultEntity
     * @return 前回ご請求金額
     */
    private String getLastBilledAmount(S07T001TD1501R40ListResultEntity entity) {
        String amount =
            d1501r00Service.amountFormatter(ItemUtil.nullToString(entity.getZnkkurikoshikin()));
        return getMaskedOrUnmasked(ItemUtil.nullToString(entity.getSkyhohokbn()), amount);
    }

    /**
     * ご入金額に設定する値を返却する
     * 
     * @param entity S07T001TD1501R40ListResultEntity
     * @return ご入金額
     */
    private String getDepositAmount(S07T001TD1501R40ListResultEntity entity) {
        String amount =
            d1501r00Service.amountFormatter(ItemUtil.nullToString(entity.getKonkainyknkin()));
        return getMaskedOrUnmasked(ItemUtil.nullToString(entity.getSkyhohokbn()), amount);
    }

    /**
     * 振替予定金額名称を返却する
     * 
     * @param reportType 帳票区分
     * @return 帳票区分：請求書の場合空それ以外は振替予定金額の文字列を返却する
     */
    private String getScheduledTransferAmountName(String reportType) {
        return D1501R00Constants.TYOHYO_KBN_SKYSHO.equals(reportType) ? "" : "振替予定金額";
    }

    /**
     * 振替予定金額を返却する
     * 
     * @param reportType 帳票区分
     * @param entity S07T001TD1501R40ListResultEntity
     * @return 振替予定金額
     */
    private String getScheduledTransferAmount(String reportType,
        S07T001TD1501R40ListResultEntity entity) {
        if (!D1501R00Constants.TYOHYO_KBN_SKYSHO.equals(reportType)) {
            return entity.getScheduledTransferAmount();
        }
        return "";
    }
}
