package jp.co.aquaclara.accis.web.domain.service.d1501;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.constants.SystemConstants;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R30ListResultEntity;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.common.util.ListCreatorUtil;
import jp.co.aquaclara.accis.web.domain.dto.b0503.D0503W02VirtualAccountDto;
import jp.co.aquaclara.accis.web.domain.service.b0503.B0503W02VirtualAccountService;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;

/**
 * 帳票出力 請求書（1行明細）-Service.
 * 
 * @author accisdev047
 *
 */
@Service
public class D1501R32Service {
    /**
     * 帳票出力 共通サービスクラス
     */
    @Autowired
    private D1501R00Service d1501r00Service;

    @Autowired
    private B0503W02VirtualAccountService vaService;

    /**
     * 売上入金区分：1(売上)
     */
    private static final String URIAGE_NYUKIN_KBN_URIAGE = "1";
    /**
     * 売上入金区分：2(入金)
     */
    private static final String URIAGE_NYUKIN_KBN_NYUKIN = "2";

    /**
     * 売上入金区分：3(売上合計)
     */
    private static final String URIAGE_NYUKIN_KBN_URIAGE_GOUKEI = "3";
    /**
     * 売上入金区分：4(入金合計)
     */
    private static final String URIAGE_NYUKIN_KBN_NYUKIN_GOUKEI = "4";
    /**
     * 売上入金区分：5(空行)
     */
    private static final String URIAGE_NYUKIN_KBN_KARA = "5";
    /**
     * 売上入金区分：6(8%税込み売上金額合計)
     */
    private static final String URIAGE_NYUKIN_KBN_URIAGE_GOUKEI_8PERCENT_TAX_KOMI = "6";
    /**
     * 売上入金区分：7(10%税込み売上金額合計)
     */
    private static final String URIAGE_NYUKIN_KBN_URIAGE_GOUKEI_10PERCENT_TAX_KOMI = "7";
    /**
     * 売上入金区分：8(非課税売上)
     */
    private static final String URIAGE_NYUKIN_KBN_URIAGE_NOT_TAX = "8";
    /**
     * 売上入金区分：9(不課税売上)
     */
    private static final String URIAGE_NYUKIN_KBN_URIAGE_FREE_TAX = "9";
    /**
     * 売上入金区分：10(8%/10%対象税抜き売上金額)
     */
    private static final String URIAGE_NYUKIN_KBN_URIAGE_GOUKEI_TAX_NUKI = "10";
    /**
     * 売上入金区分：11(8%/10%対象消費税金額)
     */
    private static final String URIAGE_NYUKIN_KBN_TAX_GOUKEI = "11";
    /**
     * 売上入金区分：12(当月ご利用金額合計表示)
     */
    private static final String URIAGE_NYUKIN_KBN_TOUGETSU_GORIYOUKINGAKU_GOUKEI = "12";
    /**
     * 売上入金区分：13(8%/10%税込売上合計金額)
     */
    private static final String URIAGE_NYUKIN_KBN_URIAGE_GOUKEI_TAX = "13";
    /**
     * 売上入金区分：14(獲得予定ポイント表示)
     */
    private static final String URIAGE_NYUKIN_KBN_KAKUTOKU_YOTEI_POINT = "14";
    /**
     * 売上入金区分：15(ポイントステージ表示)
     */
    private static final String URIAGE_NYUKIN_KBN_POINT_RNK = "15";

    /**
     * フッター出力位置
     */
    private static final int FOOTERPOINT = 23;

    /**
     * 請求書出力用データ編集処理
     * 
     * @param s07t001td1501r30ListResultEntityList 請求書出力データEntityリスト
     * @param shimedydm 締年月
     * @param shimedd 締日
     * @param reportType 帳票区分
     * @param invoiceEnforcementFlag インボイス施行フラグ
     * @param creditPostingDay クレジット計上日
     * @param normalTaxRate 通常税率
     * @param reducedTaxRate 軽減税率
     * @param transferInfoPrintFlag 振込情報印刷フラグ
     * @param systemDate システム日付
     * @param sshdshhitoImage 差出人イメージ
     * @param prntpaykbn お振込先印刷区分
     * @param tairyuoverprntkbn 滞留超過額の印刷区分
     * @return 請求書CSVデータList
     */
    public ArrayList<List<String>> printDataEdit(
        List<S07T001TD1501R30ListResultEntity> s07t001td1501r30ListResultEntityList,
        String shimedydm, String shimedd, String reportType, String invoiceEnforcementFlag,
        String creditPostingDay, String normalTaxRate, String reducedTaxRate,
        String transferInfoPrintFlag, String systemDate, String sshdshhitoImage, String prntpaykbn,
        String tairyuoverprntkbn) {

        // 請求書CSVデータ
        ArrayList<List<String>> csvData = new ArrayList<>();
        // 締日
        String shimeDate = shimedydm + shimedd;
        // 印字内容年月
        String printYearMonth = d1501r00Service.getPrintYearMonth(shimedydm);
        // 帳票名
        String reportName = getReportName(reportType);
        // プログラムID
        String programId = getProgramId(reportType);
        // システム日付
        String systemdydm = systemDate;
        // 売上入金区分
        String urinykkbn = URIAGE_NYUKIN_KBN_URIAGE;
        // 売上伝票番号
        String uridnpyno = "";
        // 得意先コード
        String tokuisakicd = "";
        // 得意先名称
        String tokuisakiName = "";
        // AP基本コード
        String apkihoncd = "";
        // ショップコード
        String shopcd = "";
        // 請求先コード
        String skysakicd = "";
        // 請求書番号
        String skyshono = "";
        // 回収方法区分
        String kaishuhohokbn = "";
        // 回収方法区分名称
        String kaishuhohokbnnm = "";
        // 支払予定年月日
        String shrytidydmdd = "";
        // 今回ご利用金額
        String konkaigoriyokin = "0";
        // 前回繰越金額
        String znkkurikoshikin = "0";
        // 今回消費税金額
        String konkaishohitaxkin = "0";
        // 今回入金金額
        String konkainyknkin = "0";
        // 繰越金額
        String kurikoshikin = "0";
        // 振替予定金額
        String hurikaeYoteikin = "0";
        // お繰越金額
        String okurikoshikin = "0";
        // 請求区分
        String skykbn = "";
        // 請求先郵便番号
        String yubinno = "";
        // 請求先住所１
        String adrsn1 = "";
        // 請求先住所２
        String adrsn2 = "";
        // 請求先住所３
        String adrsn3 = "";
        // 請求先名称１
        String skysakinmn1 = "";
        // 請求先名称２
        String skysakinmn2 = "";
        // 敬称区分
        String keishokbn = "";
        // 印刷備考
        String prntbiko = "";
        // 請求先金融機関名称カナ
        String sksmkinyukknnmkana = "";
        // 請求先金融機関支店名称カナ
        String sksmkinyukknshitennmkana = "";
        // 請求先金融機関口座種別名称
        String sksmkinyukknkzkindnm = "";
        // 請求先金融機関口座番号
        String sksmkinyukknkzno = "";
        // 決済機関名称
        String sksmksikknnm = "";
        // 明細印刷区分
        String meisaiprntkbn = "";
        // 改ページ用得意先コード
        String breaktokuisakicd = "";
        // 郵便番号バーコード
        String yubinnobakodo = "";
        // 差出人イメージ
        String sshdshhitoimage = "";
        // 伝票お知らせ内容１
        String dnpyoshirasenaiyon1 = "";
        // 伝票お知らせ内容２
        String dnpyoshirasenaiyon2 = "";
        // 伝票お知らせ内容３
        String dnpyoshirasenaiyon3 = "";
        // 伝票お知らせ内容４
        String dnpyoshirasenaiyon4 = "";
        // 伝票お知らせ内容５
        String dnpyoshirasenaiyon5 = "";
        // 伝票お知らせ内容６
        String dnpyoshirasenaiyon6 = "";
        // 消費税項目名
        String taxkumknm = "";
        // 滞留金額
        String tairyukin = "";
        // 滞留名
        String tairynm = "";
        // お知らせ２
        String oshirase2 = "";
        // 税計算区分
        String taxkeisankbn = "";
        // 振込先１
        String hurikomisaki1 = "";
        // 振込先２
        String hurikomisaki2 = "";
        // 金額合計
        String ttlkin = "0";
        // 消費税合計
        String totaltax = "0";
        // 不課税金額合計
        String freetaxkin = "0";
        // 非課税金額合計
        String nottaxkin = "0";
        // 8%税込み売上金額合計
        String tax8percenturikin = "0";
        // 8%税抜き売上金額合計
        String tax8percentnukiurikin = "0";
        // 8%消費税金額合計
        String tax8percentkin = "0";
        // 10%税込み売上金額合計
        String tax10percenturikin = "0";
        // 10%税抜き売上金額合計
        String tax10percentnukiurikin = "0";
        // 10%消費税金額合計
        String tax10percentkin = "0";
        // ポイントフラグ
        boolean pointflg = false;
        // 獲得予定ポイント
        String kakutokuyoteipoint = "0";
        // ポイントステージ名称
        String pointrnk = "";
        // 出力件数
        int outputTotal = 0;
        // 得意先件数
        int tokuisakiCount = 0;
        // 改ページ単位出力件数
        int breakPageOutputCount = 0;
        // インボイス施行施行フラグ
        String invoiceFlag = invoiceEnforcementFlag;
        // クレジット計上日
        String creditPostingdd = creditPostingDay;
        // 通常税率
        String normalTax = normalTaxRate;
        // 軽減税率
        String reducedTax = reducedTaxRate;
        // 振込情報印刷フラグ
        String transferInfoPrintFlg = transferInfoPrintFlag;
        // ACI経由決済区分
        String acikeiyuksikbn = "";
        // 集計方法判定用のブレイクキー１
        String breakKey;
        // 集計方法判定用のブレイクキー２
        String entityBreakKey;
        // ブレイク前の得意先名称
        String pretokuisakinm = "";

        // 請求書出力データentityをループして出力データを設定
        for (S07T001TD1501R30ListResultEntity entity : s07t001td1501r30ListResultEntityList) {

            // インボイス施行日前の場合、請求先単位で集計を行う。
            if ("0".equals(invoiceFlag)) {
                // インボイス施行前の場合、得意先単位で集計
                breakKey = tokuisakicd;
                entityBreakKey = entity.getTokuisakicd();
            } else {
                // インボイス施行日後かつ明細印刷区分（1:親子卸）の場合、請求先単位で集計を行う。
                // インボイス施行日後かつ明細印刷区分（2：親子別々）の場合、得意先単位で集計を行う。
                // クエリで検索結果取得時に請求先コードと得意先コード取得を判定している。
                breakKey = breaktokuisakicd;
                entityBreakKey = entity.getBreaktokuisakicd();
            }

            // ブレイクキーが異なる場合、得意先件数を初期化する。
            if (!breakKey.equals(entityBreakKey)) {
                tokuisakiCount = 0;
            }

            // ポイントフラグの設定
            // ショップポイントマスタ.ポイント制度参加有無 = 1(あり) かつ 得意先ポイントマスタ.ポイント管理有無 = 1(あり)
            if ("1".equals(entity.getPointsystemjoinkbn())
                && "1".equals(entity.getPointkanriumu())) {
                pointflg = true;
                kakutokuyoteipoint = entity.getKakutokuyoteipoint();
                pointrnk = entity.getPointrnkmei();
            }

            // 変数退避
            kaishuhohokbn = ItemUtil.nullToString(entity.getKaishuhohokbn());
            kaishuhohokbnnm = ItemUtil.nullToString(entity.getKaishuhohokbnnm());
            skykbn = ItemUtil.nullToString(entity.getSkykbn());
            meisaiprntkbn = ItemUtil.nullToString(entity.getMeisaiprntkbn());
            shrytidydmdd = ItemUtil.nullToString(entity.getShrytidydmdd());
            sksmkinyukknkzno = ItemUtil.nullToString(entity.getSksmkinyukknkzno());
            sksmkinyukknnmkana = ItemUtil.nullToString(entity.getSksmkinyukknnmkana());
            sksmkinyukknshitennmkana = ItemUtil.nullToString(entity.getSksmkinyukknshitennmkana());
            sksmkinyukknkzkindnm = ItemUtil.nullToString(entity.getSksmkinyukknkzkindnm());
            sksmksikknnm = ItemUtil.nullToString(entity.getSksmksikknnm());
            dnpyoshirasenaiyon1 = ItemUtil.nullToString(entity.getDnpyoshirasenaiyon1());
            dnpyoshirasenaiyon2 = ItemUtil.nullToString(entity.getDnpyoshirasenaiyon2());
            dnpyoshirasenaiyon3 = ItemUtil.nullToString(entity.getDnpyoshirasenaiyon3());
            dnpyoshirasenaiyon4 = ItemUtil.nullToString(entity.getDnpyoshirasenaiyon4());
            dnpyoshirasenaiyon5 = ItemUtil.nullToString(entity.getDnpyoshirasenaiyon5());
            dnpyoshirasenaiyon6 = ItemUtil.nullToString(entity.getDnpyoshirasenaiyon6());
            prntbiko = ListCreatorUtil.encodeCrlf(ItemUtil.nullToString(entity.getPrntbiko()));
            oshirase2 = ItemUtil.nullToString(entity.getOsirase2());
            acikeiyuksikbn = ItemUtil.nullToString(entity.getAcikeiyuksikbn());

            // 振込先1振込先2設定
            if (outputTotal == 0
                && D1501R00Constants.TRANSEFER_INFO_FLAG_ENABLED.equals(transferInfoPrintFlg)) {

                // ACCIS2023-7283 バーチャル口座対応
                // 請求先のバーチャル口座情報を取得
                D0503W02VirtualAccountDto vaDto = vaService.getVirtualAccount(entity.getApkihoncd(),
                    entity.getShopcd(), entity.getSkysakicd());

                if (vaDto.isUsed()) {
                    // バーチャル口座を利用する請求先の場合、振込先名称としてバーチャル口座の内容を設定
                    hurikomisaki1 = vaService.getPayeeName(vaDto);
                    // お振込先②は不要（空文字設定）
                    hurikomisaki2 = SystemConstants.WORD_BLANK;
                } else {
                    // 利用しない場合は従来処理（ショップマスタの内容）
                    hurikomisaki1 = getPayee(ItemUtil.nullToString(entity.getKinyukknnm()),
                        ItemUtil.nullToString(entity.getKinyukknshitennm()),
                        ItemUtil.nullToString(entity.getKinyukknkzkindnm()),
                        ItemUtil.nullToString(entity.getKinyukknkzno()),
                        ItemUtil.nullToString(entity.getKinyukknkzmeigi()));
                    hurikomisaki2 = getPayee(ItemUtil.nullToString(entity.getKinyukknn2nm()),
                        ItemUtil.nullToString(entity.getKinyukknn2shitennm()),
                        ItemUtil.nullToString(entity.getKinyukkn2kzkindnm()),
                        ItemUtil.nullToString(entity.getKinyukknn2kzno()),
                        ItemUtil.nullToString(entity.getKinyukknn2kzmeigi()));
                }
            }

            // 得意先名称取得
            tokuisakiName = entity.getTokuisakiName(shimeDate);

            // 変数の売上入金区分とentityの売上入金区分か変数の改ページ用得意先コードとentityの改ページ用得意先コードと
            // インボイス施行日後 かつ 明細印刷区分が（1:親子卸） かつ 変数の得意先コードとentityの得意先コードが一致していない場合の処理
            if (!urinykkbn.equals(entity.getUrinykkbn()) || !breakKey.equals(entityBreakKey)
                || ("1".equals(invoiceFlag) && "1".equals(entity.getMeisaiprntkbn())
                    && !tokuisakicd.equals(entity.getTokuisakicd()))) {
                // 出力件数が1件以上存在する場合の処理
                if (outputTotal > 0) {
                    // 売上入金合計の設定
                    List<String> totalSalesDeposit = new ArrayList<>();
                    totalSalesDeposit.addAll(getTotalSalesDeposit(programId, yubinno, adrsn1,
                        adrsn2, adrsn3, apkihoncd, shopcd, skysakicd, skysakinmn1, skysakinmn2,
                        keishokbn, yubinnobakodo, systemdydm, reportName, shimeDate, skyshono,
                        sshdshhitoimage, reportType, printYearMonth, kaishuhohokbn, taxkumknm,
                        konkaigoriyokin, znkkurikoshikin, konkainyknkin, konkaishohitaxkin,
                        kurikoshikin, tairyukin, breaktokuisakicd, tokuisakicd, dnpyoshirasenaiyon1,
                        dnpyoshirasenaiyon2, dnpyoshirasenaiyon3, dnpyoshirasenaiyon4,
                        dnpyoshirasenaiyon5, dnpyoshirasenaiyon6, oshirase2, prntbiko,
                        kaishuhohokbnnm, shrytidydmdd, creditPostingdd, acikeiyuksikbn,
                        sksmkinyukknnmkana, sksmkinyukknkzkindnm, sksmksikknnm, sksmkinyukknkzno,
                        sksmkinyukknshitennmkana, hurikomisaki1, hurikomisaki2, ttlkin, totaltax,
                        urinykkbn, tairynm, okurikoshikin, entity, pretokuisakinm,
                        breakPageOutputCount, hurikaeYoteikin, prntpaykbn, tairyuoverprntkbn));
                    // List<請求書CSVデータ> へ売上入金合計を追加
                    csvData.add(totalSalesDeposit);
                    // 改ページ出力件数をインクリメント
                    breakPageOutputCount++;

                    if (!breakKey.equals(entityBreakKey)) {
                        // 税額合計出力処理
                        csvData.addAll(totalTaxOutput(taxkeisankbn, freetaxkin, nottaxkin,
                            programId, yubinno, adrsn1, adrsn2, adrsn3, apkihoncd, shopcd,
                            skysakicd, skysakinmn1, skysakinmn2, keishokbn, yubinnobakodo,
                            systemdydm, reportName, shimeDate, skyshono, sshdshhitoimage,
                            reportType, printYearMonth, kaishuhohokbn, taxkumknm, tokuisakicd,
                            breaktokuisakicd, tax8percenturikin, tax8percentkin,
                            tax8percentnukiurikin, tax10percenturikin, tax10percentnukiurikin,
                            tax10percentkin, ttlkin, reducedTax, normalTax, totaltax, tairynm));
                        // 不課税金額合計
                        freetaxkin = "0";
                        // 非課税金額合計
                        nottaxkin = "0";
                        // 8%税込み売上金額合計
                        tax8percenturikin = "0";
                        // 8%税抜き売上金額合計
                        tax8percentnukiurikin = "0";
                        // 8%消費税金額合計
                        tax8percentkin = "0";
                        // 10%税込み売上金額合計
                        tax10percenturikin = "0";
                        // 10%税抜き売上金額合計
                        tax10percentnukiurikin = "0";
                        // 10%消費税金額合計
                        tax10percentkin = "0";
                    }
                    // 金額合計
                    ttlkin = "0";
                    // 消費税合計
                    totaltax = "0";
                }

                // フッターを出力するためのカウントクリア
                if (!breaktokuisakicd.equals(entity.getBreaktokuisakicd())) {
                    breakPageOutputCount = 0;
                }

                if (tokuisakiCount == 0 && URIAGE_NYUKIN_KBN_NYUKIN.equals(entity.getUrinykkbn())) {
                    // 売上合計の設定
                    List<String> totalSales = new ArrayList<>();
                    totalSales.addAll(getTotalSales(programId, keishokbn, systemdydm, reportName,
                        shimeDate, reportType, dnpyoshirasenaiyon1, dnpyoshirasenaiyon2,
                        dnpyoshirasenaiyon3, dnpyoshirasenaiyon4, dnpyoshirasenaiyon5,
                        dnpyoshirasenaiyon6, printYearMonth, kaishuhohokbn, prntbiko, oshirase2,
                        kaishuhohokbnnm, shrytidydmdd, creditPostingdd, acikeiyuksikbn,
                        sksmkinyukknnmkana, sksmkinyukknkzkindnm, sksmksikknnm, sksmkinyukknkzno,
                        sksmkinyukknshitennmkana, hurikomisaki1, hurikomisaki2, invoiceFlag, entity,
                        tokuisakiName, prntpaykbn, tairyuoverprntkbn));
                    // List<請求書CSVデータ> へ売上合計を追加
                    csvData.add(totalSales);

                    // 変数退避2
                    znkkurikoshikin = getVariableSave2Value(entity.getSkyhohokbn(),
                        entity.getZnkkurikoshikin(), "*********");
                    konkainyknkin = getVariableSave2Value(entity.getSkyhohokbn(),
                        entity.getKonkainyknkin(), "*********");
                    kurikoshikin = getVariableSave2Value(entity.getSkyhohokbn(),
                        entity.getKurikoshikin(), entity.getKonkaigoriyokin());
                    okurikoshikin =
                        getVariableSave2Value(entity.getSkyhohokbn(), d1501r00Service.getDifference(
                            entity.getZnkkurikoshikin(), entity.getKonkainyknkin()), "*********");
                    hurikaeYoteikin = entity.getHurikaeYoteikin(invoiceEnforcementFlag, reportType);
                    // 改ページ出力件数をインクリメント
                    breakPageOutputCount++;
                }

                // 売上伝票番号クリア
                uridnpyno = SystemConstants.WORD_BLANK;
            }

            if (entity.shouldSetUriageNyukinMeisai()) {
                // 売上入金明細の設定
                List<String> salesDepositDetail = new ArrayList<>();
                salesDepositDetail.addAll(getSalesDepositDetail(programId, keishokbn, reportType,
                    printYearMonth, kaishuhohokbn, systemdydm, reportName, shimeDate,
                    breaktokuisakicd, dnpyoshirasenaiyon1, dnpyoshirasenaiyon2, dnpyoshirasenaiyon3,
                    dnpyoshirasenaiyon4, dnpyoshirasenaiyon5, dnpyoshirasenaiyon6, oshirase2,
                    prntbiko, uridnpyno, kaishuhohokbnnm, shrytidydmdd, creditPostingdd,
                    acikeiyuksikbn, sksmkinyukknnmkana, sksmkinyukknkzkindnm, sksmksikknnm,
                    sksmkinyukknkzno, sksmkinyukknshitennmkana, hurikomisaki1, hurikomisaki2,
                    invoiceFlag, entity, tokuisakiName, breakPageOutputCount, prntpaykbn,
                    tairyuoverprntkbn));
                // List<請求書CSVデータ> へ売上入金明細を追加
                csvData.add(salesDepositDetail);
            }
            // 出力件数
            outputTotal = outputTotal + 1;
            // 得意先件数
            tokuisakiCount = tokuisakiCount + 1;

            // 変数退避（ヘッダー項目退避）
            keishokbn = ItemUtil.nullToString(entity.getSkysakikeishokbnnm());
            skyshono = entity.getSkyshonoToFormat(reportType);
            taxkumknm = getTaxName(entity);
            znkkurikoshikin = getLastTimeAmountCarriedForward(entity);
            konkainyknkin = getThisTimeDepositAmount(entity);
            kurikoshikin = getAmountCarriedForward(entity);
            hurikaeYoteikin = entity.getHurikaeYoteikin(invoiceEnforcementFlag, reportType);
            yubinno = ItemUtil.nullToString(entity.getYubinno());
            adrsn1 = ItemUtil.nullToString(entity.getAdrsn1());
            adrsn2 = ItemUtil.nullToString(entity.getAdrsn2());
            adrsn3 = ItemUtil.nullToString(entity.getAdrsn3());
            apkihoncd = ItemUtil.nullToString(entity.getApkihoncd());
            shopcd = ItemUtil.nullToString(entity.getShopcd());
            skysakicd = ItemUtil.nullToString(entity.getSkysakicd());
            skysakinmn1 = ItemUtil.nullToString(entity.getSkysakinmn1());
            skysakinmn2 = ItemUtil.nullToString(entity.getSkysakinmn2());
            yubinnobakodo = ItemUtil.nullToString(entity.getYubinnobakodo());
            sshdshhitoimage = sshdshhitoImage;
            konkaigoriyokin = ItemUtil.nullToString(entity.getKonkaiGoriyouGaku());
            konkaishohitaxkin = ItemUtil.nullToString(entity.getKonkaishohitaxkin());
            // 滞留金と滞留金名称は請求方法区分「1：残高請求」 かつ 画面.滞留超過額の印刷「1:する」場合のみ設定
            if (CommonConstants.SKYHOHOKBN_ZANDAKA.equals(entity.getSkyhohokbn())) {
                if (CommonConstants.FLG_ON.equals(tairyuoverprntkbn)) {
                    tairyukin = ItemUtil.nullToString(entity.getTairyukin());
                    tairynm = ItemUtil.nullToString(entity.getTairyukinmei());
                }
            }
            uridnpyno = ItemUtil.nullToString(entity.getUridnpyno());
            breaktokuisakicd = ItemUtil.nullToString(entity.getBreaktokuisakicd());
            okurikoshikin = getOkurikoshikin(entity);

            if (CommonConstants.NYURYOKUMEISAIFLG_1.equals(entity.getNyuryokumeisaiflg())
                || URIAGE_NYUKIN_KBN_NYUKIN.equals(entity.getUrinykkbn())) {
                ttlkin = d1501r00Service
                    .getSum(new ArrayList<String>(Arrays.asList(ttlkin, entity.getKin())));
                totaltax = d1501r00Service.getSum(
                    new ArrayList<String>(Arrays.asList(totaltax, entity.getShohitaxkin())));
                if (CommonConstants.KBN_TAX_KAZEI.equals(entity.getTaxkbn())) {
                    // ・変数.10%税抜売上金額合計 ← 変数.10%税抜売上金額合計 ＋ 請求書出力データEntity.金額
                    tax10percentnukiurikin = d1501r00Service.getSum(new ArrayList<String>(
                        Arrays.asList(tax10percentnukiurikin, entity.getKin())));
                    // ・変数.10%消費税金額合計 ← 変数.10%消費税金額合計 ＋ 請求書出力データEntity.消費税金額
                    tax10percentkin = d1501r00Service.getSum(new ArrayList<String>(
                        Arrays.asList(tax10percentkin, entity.getShohitaxkin())));
                    // ・変数.10%税込売上金額合計 ← 変数.10%税込売上金額合計 ＋ 請求書出力データEntity.金額 ＋ 請求書出力データEntity.消費税金額
                    tax10percenturikin = d1501r00Service.getSum(new ArrayList<String>(Arrays
                        .asList(tax10percenturikin, entity.getKin(), entity.getShohitaxkin())));
                }
                if (CommonConstants.KBN_TAX_KEIGEN.equals(entity.getTaxkbn())) {
                    // ・変数.8%税抜き売上金額合計 ← 変数.8%税抜き売上金額合計 ＋ 請求書出力データEntity.金額
                    tax8percentnukiurikin = d1501r00Service.getSum(new ArrayList<String>(
                        Arrays.asList(tax8percentnukiurikin, entity.getKin())));
                    // ・変数.8%消費税金額合計 ← 変数.8%消費税金額合計 ＋ 請求書出力データEntity.消費税金額
                    tax8percentkin = d1501r00Service.getSum(new ArrayList<String>(
                        Arrays.asList(tax8percentkin, entity.getShohitaxkin())));
                    // ・変数.8%税込売上金額合計 ← 変数.8%税込売上金額合計 ＋ 請求書出力データEntity.金額 ＋ 請求書出力データEntity.消費税金額
                    tax8percenturikin = d1501r00Service.getSum(new ArrayList<String>(Arrays
                        .asList(tax8percenturikin, entity.getKin(), entity.getShohitaxkin())));
                }
                if (CommonConstants.KBN_TAX_HIKAZEI.equals(entity.getTaxkbn())) {
                    // ・変数.非課税金額合計 ← 変数.非課税金額合計 ＋ 請求書出力データEntity.金額
                    nottaxkin = d1501r00Service
                        .getSum(new ArrayList<String>(Arrays.asList(nottaxkin, entity.getKin())));
                }
                if (CommonConstants.KBN_TAX_HUKAZEI.equals(entity.getTaxkbn())) {
                    // ・変数.不課税金額合計 ← 変数.不課税金額合計 ＋ 請求書出力データEntity.金額
                    freetaxkin = d1501r00Service
                        .getSum(new ArrayList<String>(Arrays.asList(freetaxkin, entity.getKin())));
                }
            } else if (!CommonConstants.NYURYOKUMEISAIFLG_1.equals(entity.getNyuryokumeisaiflg())) {
                if (CommonConstants.KBN_TAX_KOMEISAI.equals(entity.getOyasttaxkbn())) {
                    if (CommonConstants.KBN_TAX_KAZEI.equals(entity.getTaxkbn())) {
                        // ・変数.10%税抜売上金額合計 ← 変数.10%税抜売上金額合計 ＋ 請求書出力データEntity.金額
                        tax10percentnukiurikin = d1501r00Service.getSum(new ArrayList<String>(
                            Arrays.asList(tax10percentnukiurikin, entity.getKin())));
                        // ・変数.10%消費税金額合計 ← 変数.10%消費税金額合計 ＋ 請求書出力データEntity.消費税金額
                        tax10percentkin = d1501r00Service.getSum(new ArrayList<String>(
                            Arrays.asList(tax10percentkin, entity.getShohitaxkin())));
                        // ・変数.10%税込売上金額合計 ← 変数.10%税込売上金額合計 ＋ 請求書出力データEntity.金額 ＋
                        // 請求書出力データEntity.消費税金額
                        tax10percenturikin = d1501r00Service.getSum(new ArrayList<String>(Arrays
                            .asList(tax10percenturikin, entity.getKin(), entity.getShohitaxkin())));
                    }
                    if (CommonConstants.KBN_TAX_KEIGEN.equals(entity.getTaxkbn())) {
                        // ・変数.8%税抜き売上金額合計 ← 変数.8%税抜き売上金額合計 ＋ 請求書出力データEntity.金額
                        tax8percentnukiurikin = d1501r00Service.getSum(new ArrayList<String>(
                            Arrays.asList(tax8percentnukiurikin, entity.getKin())));
                        // ・変数.8%消費税金額合計 ← 変数.8%消費税金額合計 ＋ 請求書出力データEntity.消費税金額
                        tax8percentkin = d1501r00Service.getSum(new ArrayList<String>(
                            Arrays.asList(tax8percentkin, entity.getShohitaxkin())));
                        // ・変数.8%税込売上金額合計 ← 変数.8%税込売上金額合計 ＋ 請求書出力データEntity.金額 ＋ 請求書出力データEntity.消費税金額
                        tax8percenturikin = d1501r00Service.getSum(new ArrayList<String>(Arrays
                            .asList(tax8percenturikin, entity.getKin(), entity.getShohitaxkin())));
                    }
                }
            }
            urinykkbn = ItemUtil.nullToString(entity.getUrinykkbn());
            tokuisakicd = ItemUtil.nullToString(entity.getTokuisakicd());
            acikeiyuksikbn = ItemUtil.nullToString(entity.getAcikeiyuksikbn());
            taxkeisankbn = ItemUtil.nullToString(entity.getTaxkeisankbn());
            breakKey = ItemUtil.nullToString(entity.getTokuisakicd());
            pretokuisakinm = tokuisakiName;
        }

        if (outputTotal > 0) {
            // 売上入金最終行を設定
            List<String> salesDepositEndLine = new ArrayList<>();
            salesDepositEndLine.addAll(getSalesDepositEndLine(programId, yubinno, adrsn1, adrsn2,
                adrsn3, apkihoncd, shopcd, skysakicd, skysakinmn1, skysakinmn2, keishokbn,
                yubinnobakodo, systemdydm, reportName, shimeDate, skyshono, sshdshhitoimage,
                reportType, printYearMonth, kaishuhohokbn, taxkumknm, tokuisakicd, urinykkbn,
                ttlkin, totaltax, breaktokuisakicd, tairynm, meisaiprntkbn, skykbn, tokuisakiName,
                konkaigoriyokin, znkkurikoshikin, konkainyknkin, konkaishohitaxkin, kurikoshikin,
                tairyukin, dnpyoshirasenaiyon1, dnpyoshirasenaiyon2, dnpyoshirasenaiyon3,
                dnpyoshirasenaiyon4, dnpyoshirasenaiyon5, dnpyoshirasenaiyon6, prntbiko, oshirase2,
                kaishuhohokbnnm, shrytidydmdd, creditPostingdd, acikeiyuksikbn, sksmkinyukknnmkana,
                sksmkinyukknkzkindnm, sksmksikknnm, sksmkinyukknkzno, sksmkinyukknshitennmkana,
                hurikomisaki1, hurikomisaki2, s07t001td1501r30ListResultEntityList,
                breakPageOutputCount, okurikoshikin, hurikaeYoteikin, prntpaykbn,
                tairyuoverprntkbn));
            // List<請求書CSVデータ> へ売上入金最終行を追加
            csvData.add(salesDepositEndLine);
            // 税額合計出力処理
            csvData.addAll(totalTaxOutput(taxkeisankbn, freetaxkin, nottaxkin, programId, yubinno,
                adrsn1, adrsn2, adrsn3, apkihoncd, shopcd, skysakicd, skysakinmn1, skysakinmn2,
                keishokbn, yubinnobakodo, systemdydm, reportName, shimeDate, skyshono,
                sshdshhitoimage, reportType, printYearMonth, kaishuhohokbn, taxkumknm, tokuisakicd,
                breaktokuisakicd, tax8percenturikin, tax8percentkin, tax8percentnukiurikin,
                tax10percenturikin, tax10percentnukiurikin, tax10percentkin, ttlkin, reducedTax,
                normalTax, totaltax, tairynm));

            // ポイントフラグが有効な場合のみポイント行を出力
            if (pointflg) {
                // ポイント項目出力処理
                csvData.addAll(pointOutput(programId, yubinno, adrsn1, adrsn2, adrsn3, apkihoncd,
                    shopcd, skysakicd, skysakinmn1, skysakinmn2, keishokbn, yubinnobakodo,
                    systemdydm, reportName, shimeDate, skyshono, sshdshhitoimage, reportType,
                    printYearMonth, kaishuhohokbn, taxkumknm, tokuisakicd, breaktokuisakicd,
                    tairynm, kakutokuyoteipoint, pointrnk));
            }

        }
        if (csvData.isEmpty()) {
            throw BusinessExceptionFactory.dataNotFound(MessageID.W083);
        }

        // フッターカウントの付与処理
        addFooterCnt(csvData, reportType);

        return csvData;
    }

    /**
     * フッターカウント付与処理
     * 
     * @param csvData
     * @param reportType
     */
    private void addFooterCnt(ArrayList<List<String>> csvData, String reportType) {
        Integer footerCnt = 0;
        String pretokuisakicd = "";
        String prebreakTokuisakicd = "";
        List<String> blankrecord = new ArrayList<>();

        // 請求書番号単位のCSV出力結果件数分、繰り返す
        for (int i = 0; i < csvData.size(); i++) {

            List<String> row = csvData.get(i);

            // 改ページ用得意先コードが切り替わった場合、フッターカウントを初期化
            if (!prebreakTokuisakicd.equals(row.get(57))) {
                if (footerCnt != 0 && footerCnt < FOOTERPOINT) {
                    // 23行分まで空行を補完
                    insertBlankLines(csvData, FOOTERPOINT - footerCnt, i,
                        blankrecord.get(29).replaceAll("\"", ""), blankrecord, footerCnt,
                        reportType);

                    // 補完した行数分 繰り返し位置を更新する
                    i = i + (FOOTERPOINT - footerCnt);
                }
                footerCnt = 0;
            }

            // 得意先コード
            String tokuisakicd = row.get(29).replaceAll("\"", "");
            // 入金売上区分
            String nykinurikbn = row.get(53).replaceAll("\"", "");
            // 消費税項目名
            String taxnm = row.get(21).replaceAll("\"", "");

            // フッターカウント加算
            footerCnt++;
            // 得意先が切り替わった場合のフッターカウント制御処理
            // ACCIS2023-7480 得意先コードが切り替わらなくても請求書番号が切り替わった際にもカウント制御するように条件部を修正
            if (!pretokuisakicd.equals(tokuisakicd) || !prebreakTokuisakicd.equals(row.get(57))) {
                // レコードクリア
                blankrecord.clear();
                // レコード退避
                blankrecord.addAll(row);
                // フッターカウントが23の場合は空行補完して得意先名称を次ページに出力するように対応
                // フッターカウントが23以外は自動生成行を加味して、フッターカウントを2加算する。
                // ページ末尾に得意先名称の出力がある場合、空行を２行で補完する。
                if (footerCnt == FOOTERPOINT) {
                    // フッターカウントがフッター出力位置と同じ場合、空行補完してフッター出力制御を行う。
                    List<String> blankLine1 =
                        createBlankLine(pretokuisakicd, blankrecord, reportType);
                    List<String> blankLine2 =
                        createBlankLine(pretokuisakicd, blankrecord, reportType);

                    // フッターカウント付与処理
                    blankLine1.add((footerCnt++).toString());
                    blankLine2.add((footerCnt++).toString());

                    // CSVデータに値を追加
                    csvData.add(i++, blankLine1);
                    csvData.add(i++, blankLine2);

                    // 空行補完後に得意先名称の出力を行うため、フッターカウントを加算する。（得意先名称出力時は2加算する仕様としているため）
                    footerCnt++;
                } else if (footerCnt == 24) {
                    // ２ページ目の先頭に得意先名称が出力される場合、得意先名称の前に空行１行補完する
                    List<String> blankLine =
                        createBlankLine(pretokuisakicd, blankrecord, reportType);

                    // フッターカウント付与処理
                    blankLine.add((footerCnt++).toString());

                    // CSVデータに値を追加
                    csvData.add(i++, blankLine);

                    // 空行補完後に得意先名称の出力を行うため、フッターカウントを加算する。（得意先名称出力時は2加算する仕様としているため）
                    footerCnt++;
                } else if (footerCnt % 45 == 23) {
                    // ３ページ目以降の先頭に得意先名称が出力される場合、得意先名称の前に空行２行補完する
                    List<String> blankLine1 =
                        createBlankLine(pretokuisakicd, blankrecord, reportType);
                    List<String> blankLine2 =
                        createBlankLine(pretokuisakicd, blankrecord, reportType);

                    // フッターカウント付与処理
                    blankLine1.add((footerCnt++).toString());
                    blankLine2.add((footerCnt++).toString());

                    // CSVデータに値を追加
                    csvData.add(i++, blankLine1);
                    csvData.add(i++, blankLine2);

                    // 空行補完後に得意先名称の出力を行うため、フッターカウントを加算する。（得意先名称出力時は2加算する仕様としているため）
                    footerCnt++;
                } else if (footerCnt % 45 == 24) {
                    // ３ページ目以降の先頭に得意先名称が出力される場合、得意先名称の前に空行１行補完する
                    List<String> blankLine =
                        createBlankLine(pretokuisakicd, blankrecord, reportType);

                    // フッターカウント付与処理
                    blankLine.add((footerCnt++).toString());

                    // CSVデータに値を追加
                    csvData.add(i++, blankLine);

                    // 空行補完後に得意先名称の出力を行うため、フッターカウントを加算する。（得意先名称出力時は2加算する仕様としているため）
                    footerCnt++;
                } else {
                    footerCnt++;
                }
                // 入金売上区分(3:売上合計)を出力する際、"内消費税額"を印字する場合、出力レコードは2行あるため加算する。
                if ("3".equals(nykinurikbn) && "内消費税額".equals(taxnm)) {
                    if (footerCnt < FOOTERPOINT - 1) {
                        footerCnt++;
                    }
                }
            } else {
                // 入金売上区分で出力されるレコード数が異なるので、加算する。
                if ("3".equals(nykinurikbn) && "内消費税額".equals(taxnm)) {
                    if (footerCnt < FOOTERPOINT - 1) {
                        footerCnt++;
                    }
                }
            }
            // レコード追加
            row.add(footerCnt.toString());
            // 得意先コードを退避
            pretokuisakicd = tokuisakicd;
            // 改ページ用得意先コードを退避
            prebreakTokuisakicd = row.get(57);
        }

        // 空行の補正処理（フッター出力件数22件以下の場合）
        if (footerCnt <= FOOTERPOINT - 1) {
            // フッター出力行数が23未満の場合、空行レコードを補完する。
            for (int j = ++footerCnt; j <= FOOTERPOINT; j++) {
                List<String> blankLine = createBlankLine(blankrecord.get(29).replaceAll("\"", ""),
                    blankrecord, reportType);

                // フッターカウント付与処理
                blankLine.add((footerCnt++).toString());

                // CSVの末尾に追加
                csvData.add(blankLine);
            }
        }
    }

    /**
     * フッターの出力行数分、空行データで補完する。
     * 
     * @param csvData
     * @param lines
     * @param i
     * @param tokuisakicd
     * @param blankrecord
     * @param footerCnt
     */
    private void insertBlankLines(ArrayList<List<String>> csvData, int lines, int i,
        String tokuisakicd, List<String> blankrecord, Integer footerCnt, String reportType) {

        for (int j = 0; j < lines; j++) {
            List<String> l = createBlankLine(tokuisakicd, blankrecord, reportType);
            // フッターカウント付与処理
            l.add((++footerCnt).toString());
            csvData.add(i++, l);
        }
    }

    /**
     * 行データの52列目以降の項目補正処理
     * 
     * @param pretokuisakicd
     * @param blankrecord
     * @return 空行で補完したリスト
     */
    private List<String> createBlankLine(String pretokuisakicd, List<String> blankrecord,
        String reportType) {
        List<String> commonBlankLineList = getCommonBlankLine(
            blankrecord.get(0).replaceAll("\"", ""), blankrecord.get(1).replaceAll("\"", ""),
            blankrecord.get(2).replaceAll("\"", ""), blankrecord.get(3).replaceAll("\"", ""),
            blankrecord.get(4).replaceAll("\"", ""), blankrecord.get(5).replaceAll("\"", ""),
            blankrecord.get(6).replaceAll("\"", ""), blankrecord.get(7).replaceAll("\"", ""),
            blankrecord.get(8).replaceAll("\"", ""), blankrecord.get(9).replaceAll("\"", ""),
            blankrecord.get(10).replaceAll("\"", ""), blankrecord.get(11).replaceAll("\"", ""),
            blankrecord.get(12).replaceAll("\"", ""), blankrecord.get(13).replaceAll("\"", ""),
            blankrecord.get(14).replaceAll("\"", ""), blankrecord.get(15).replaceAll("\"", ""),
            blankrecord.get(16).replaceAll("\"", ""), D1501R00Constants.TYOHYO_KBN_SKYSHO, "", "",
            blankrecord.get(21).replaceAll("\"", ""), pretokuisakicd);

        // 52列目以降の項目補正
        List<String> workList = getBlankLine(commonBlankLineList,
            blankrecord.get(57).replaceAll("\"", ""), reportType, "");
        return workList;
    }

    /**
     * 税額合計出力処理
     * 
     * @param taxkeisankbn 税計算区分
     * @param freetaxkin 不課税金額
     * @param nottaxkin 非課税金額
     * @param programId プログラムID
     * @param yubinno 郵便番号
     * @param adrsn1 請求先住所1
     * @param adrsn2 請求先住所2
     * @param adrsn3 請求先住所3
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param skysakicd 請求先コード
     * @param skysakinmn1 請求先名称1
     * @param skysakinmn2 請求先名称2
     * @param keishokbn 敬称区分
     * @param yubinnobakodo 郵便バーコード
     * @param systemdydm システム日付
     * @param reportName 帳票名
     * @param shimeDate 締日
     * @param skyshono 請求書番号
     * @param sshdshhitoimage 差出人イメージ
     * @param reportType 帳票区分
     * @param printYearMonth 印字日時
     * @param kaishuhohokbn 回収方法区分
     * @param taxkumknm 消費税項目名
     * @param tokuisakicd 得意先コード
     * @param breaktokuisakicd 改ページ用得意先コード
     * @param tax8percenturikin 8%税込み売上金額合計
     * @param tax8percentkin 8%消費税合計
     * @param tax8percentnukiurikin 8%税抜き売上金額合計
     * @param tax10percenturikin 10%税込み売上金額合計
     * @param tax10percentnukiurikin 10%税抜き売上金額合計
     * @param tax10percentkin 10%消費税合計
     * @param ttlkin 合計金額
     * @param reducedTax 軽減税率
     * @param normalTax 通常税率
     * @param totaltax 合計消費税
     * @param tairynm 滞留名
     * @return 税額合計出力処理で設定する各設定値のArrayList
     */
    private ArrayList<List<String>> totalTaxOutput(String taxkeisankbn, String freetaxkin,
        String nottaxkin, String programId, String yubinno, String adrsn1, String adrsn2,
        String adrsn3, String apkihoncd, String shopcd, String skysakicd, String skysakinmn1,
        String skysakinmn2, String keishokbn, String yubinnobakodo, String systemdydm,
        String reportName, String shimeDate, String skyshono, String sshdshhitoimage,
        String reportType, String printYearMonth, String kaishuhohokbn, String taxkumknm,
        String tokuisakicd, String breaktokuisakicd, String tax8percenturikin,
        String tax8percentkin, String tax8percentnukiurikin, String tax10percenturikin,
        String tax10percentnukiurikin, String tax10percentkin, String ttlkin, String reducedTax,
        String normalTax, String totaltax, String tairynm) {
        // プログラムID～格納用銀行名称2を取得
        List<String> commonBlankLineList = getCommonBlankLine(programId, yubinno, adrsn1, adrsn2,
            adrsn3, apkihoncd, shopcd, skysakicd, skysakinmn1, skysakinmn2, keishokbn,
            yubinnobakodo, systemdydm, reportName, shimeDate, skyshono, sshdshhitoimage, reportType,
            printYearMonth, kaishuhohokbn, taxkumknm, tokuisakicd);
        ArrayList<List<String>> arrayList = new ArrayList<List<String>>();
        // 空行を設定
        arrayList.add(getBlankLine(commonBlankLineList, breaktokuisakicd, reportType, tairynm));
        if (CommonConstants.KBN_TAXKEISAN_IN.equals(taxkeisankbn)) {
            // 8%税込み売上金額合計 の編集
            arrayList.add(getTotalSalesIncludingTaxFor8Percent(commonBlankLineList,
                breaktokuisakicd, tax8percenturikin, reducedTax, reportType, tairynm));
            // 10%税込み売上金額合計 の編集
            arrayList.add(getTotalSalesIncludingTaxFor10Percent(commonBlankLineList,
                breaktokuisakicd, tax10percenturikin, normalTax, reportType, tairynm));
            // 変数.非課税合計 ≠ 0 の場合、「非課税金額」の編集
            if (!"0".equals(nottaxkin)) {
                arrayList.add(getTotalAmountForNotTax(commonBlankLineList, breaktokuisakicd,
                    nottaxkin, reportType, tairynm));
            }
            // 変数.不課税合計 ≠ 0 の場合、「不課税金額」の編集
            if (!"0".equals(freetaxkin)) {
                arrayList.add(getTotalAmountForFreeTax(commonBlankLineList, breaktokuisakicd,
                    freetaxkin, reportType, tairynm));
            }
        } else {
            // 8%税抜き売上合計 の編集
            arrayList.add(getTotalSalesExcludingTax(commonBlankLineList, breaktokuisakicd,
                tax8percentnukiurikin, reducedTax, reportType, tairynm));
            // 8%消費税合計 の編集
            arrayList.add(getTotalTax(commonBlankLineList, breaktokuisakicd, tax8percentkin,
                reducedTax, reportType, tairynm));
            // 8%税込売上合計 の編集
            arrayList.add(getTotalUriageTax(commonBlankLineList, breaktokuisakicd,
                tax8percenturikin, reducedTax, reportType, tairynm));
            // 10%税抜き売上合計 の編集
            arrayList.add(getTotalSalesExcludingTax(commonBlankLineList, breaktokuisakicd,
                tax10percentnukiurikin, normalTax, reportType, tairynm));
            // 10%消費税合計 の編集
            arrayList.add(getTotalTax(commonBlankLineList, breaktokuisakicd, tax10percentkin,
                normalTax, reportType, tairynm));
            // 10%税込売上合計 の編集
            arrayList.add(getTotalUriageTax(commonBlankLineList, breaktokuisakicd,
                tax10percenturikin, normalTax, reportType, tairynm));

            // 変数.非課税合計 ≠ 0 の場合、「非課税金額」の編集
            if (!"0".equals(nottaxkin)) {
                arrayList.add(getTotalAmountForNotTax(commonBlankLineList, breaktokuisakicd,
                    nottaxkin, reportType, tairynm));
            }
            // 変数.不課税合計 ≠ 0 の場合、「不課税金額」の編集
            if (!"0".equals(freetaxkin)) {
                arrayList.add(getTotalAmountForFreeTax(commonBlankLineList, breaktokuisakicd,
                    freetaxkin, reportType, tairynm));
            }
            // 当月ご利用額合計（税込み） の編集
            arrayList.add(getTotalAmountForCurrentMonth(commonBlankLineList, breaktokuisakicd,
                tax8percentnukiurikin, tax8percentkin, tax10percentnukiurikin, tax10percentkin,
                nottaxkin, freetaxkin, reportType, tairynm));
        }

        return arrayList;
    }


    /**
     * 当月ご利用額合計（税込み）のリストを返却する
     * 
     * @param commonBlankLine 空行の52行目までのリスト
     * @param breaktokuisakicd 改行得意先コード
     * @param tax8percentnukiurikin 8%税抜き売上金額合計
     * @param tax8percentkin 8%消費税合計
     * @param tax10percentnukiurikin 10%税抜き売上金額合計
     * @param tax10percentkin 10%消費税合計
     * @param nottaxkin 非課税金額合計
     * @param freetaxkin 不課税金額合計
     * @param reportType 帳票種別
     * @param tairynm 滞留名
     * @return 当月ご利用額合計（税込み）List
     */
    private List<String> getTotalAmountForCurrentMonth(List<String> commonBlankLine,
        String breaktokuisakicd, String tax8percentnukiurikin, String tax8percentkin,
        String tax10percentnukiurikin, String tax10percentkin, String nottaxkin, String freetaxkin,
        String reportType, String tairynm) {
        String total =
            d1501r00Service.getSum(new ArrayList<String>(Arrays.asList(tax8percentnukiurikin,
                tax8percentkin, tax10percentnukiurikin, tax10percentkin, nottaxkin, freetaxkin)));
        return getTaxTotalSales(commonBlankLine, URIAGE_NYUKIN_KBN_TOUGETSU_GORIYOUKINGAKU_GOUKEI,
            breaktokuisakicd, total, "", reportType, tairynm, "", "");
    }

    /**
     * 消費税合計のリストを返却する
     * 
     * @param commonBlankLine 空行の52行目までのリスト
     * @param breaktokuisakicd 改行得意先コード
     * @param taxkin 消費税合計
     * @param taxRate 税率
     * @param reportType 帳票種別
     * @param tairynm 滞留名
     * @return 消費税合計List
     */
    private List<String> getTotalTax(List<String> commonBlankLine, String breaktokuisakicd,
        String taxkin, String taxRate, String reportType, String tairynm) {
        return getTaxTotalSales(commonBlankLine, URIAGE_NYUKIN_KBN_TAX_GOUKEI, breaktokuisakicd,
            taxkin, taxRate, reportType, tairynm, "", "");
    }

    /**
     * 税抜き売上金額合計のリストを返却する
     * 
     * @param commonBlankLine 空行の52行目までのリスト
     * @param breaktokuisakicd 改行得意先コード
     * @param taxnukiurikin 税抜き売上金額合計
     * @param taxRate 税率
     * @param reportType 帳票種別
     * @param tairynm 滞留名
     * @return 税抜き売上金額合計List
     */
    private List<String> getTotalSalesExcludingTax(List<String> commonBlankLine,
        String breaktokuisakicd, String taxnukiurikin, String taxRate, String reportType,
        String tairynm) {
        return getTaxTotalSales(commonBlankLine, URIAGE_NYUKIN_KBN_URIAGE_GOUKEI_TAX_NUKI,
            breaktokuisakicd, taxnukiurikin, taxRate, reportType, tairynm, "", "");
    }

    /**
     * 税込の売上金額のリストを返却する
     * 
     * @param commonBlankLine 空行の52行目までのリスト
     * @param breaktokuisakicd 改ページ用得意先コード
     * @param taxurikin 税別の税込売上金額の合計（8%/10%)
     * @param taxrate 税率
     * @param reportType 帳票種別
     * @param tairynm 滞留名
     * @return
     */
    private List<String> getTotalUriageTax(List<String> commonBlankLine, String breaktokuisakicd,
        String taxurikin, String taxrate, String reportType, String tairynm) {
        return getTaxTotalSales(commonBlankLine, URIAGE_NYUKIN_KBN_URIAGE_GOUKEI_TAX,
            breaktokuisakicd, taxurikin, taxrate, reportType, tairynm, "", "");
    }

    /**
     * 不課税金額合計のリストを返却する
     * 
     * @param commonBlankLine 空行の52行目までのリスト
     * @param breaktokuisakicd 改行得意先コード
     * @param freetaxkin 不課税金額合計
     * @param reportType 帳票種別
     * @param tairynm 滞留名
     * @return 不課税金額合計List
     */
    private List<String> getTotalAmountForFreeTax(List<String> commonBlankLine,
        String breaktokuisakicd, String freetaxkin, String reportType, String tairynm) {
        return getTaxTotalSales(commonBlankLine, URIAGE_NYUKIN_KBN_URIAGE_FREE_TAX,
            breaktokuisakicd, freetaxkin, "", reportType, tairynm, "", "");
    }

    /**
     * 非課税金額合計のリストを返却する
     * 
     * @param commonBlankLine 空行の52行目までのリスト
     * @param breaktokuisakicd 改行得意先コード
     * @param nottaxkin 非課税金額合計
     * @param reportType 帳票種別
     * @param tairynm 滞留名
     * @return 非課税金額合計List
     */
    private List<String> getTotalAmountForNotTax(List<String> commonBlankLine,
        String breaktokuisakicd, String nottaxkin, String reportType, String tairynm) {
        return getTaxTotalSales(commonBlankLine, URIAGE_NYUKIN_KBN_URIAGE_NOT_TAX, breaktokuisakicd,
            nottaxkin, "", reportType, tairynm, "", "");
    }

    /**
     * 8%税込み売上金額合計のリストを返却する
     * 
     * @param commonBlankLine 空行の52行目までのリスト
     * @param breaktokuisakicd 改行得意先コード
     * @param tax8percenturikin 8%税込み売上金額合計
     * @param reducedTax 軽減税率
     * @param reportType 帳票種別
     * @param tairynm 滞留名
     * @return 8%税込み売上金額合計List
     */
    private List<String> getTotalSalesIncludingTaxFor8Percent(List<String> commonBlankLine,
        String breaktokuisakicd, String tax8percenturikin, String reducedTax, String reportType,
        String tairynm) {
        return getTaxTotalSales(commonBlankLine, URIAGE_NYUKIN_KBN_URIAGE_GOUKEI_8PERCENT_TAX_KOMI,
            breaktokuisakicd, tax8percenturikin, reducedTax, reportType, tairynm, "", "");
    }

    /**
     * 10%税込み売上金額合計のリストを返却する
     * 
     * @param commonBlankLine 空行の52行目までのリスト
     * @param breaktokuisakicd 改行得意先コード
     * @param tax10percenturikin 10%税込み売上金額合計
     * @param normalTax 通常税率
     * @param reportType 帳票種別
     * @param tairynm 滞留名
     * @return 10%税込み売上金額合計List
     */
    private List<String> getTotalSalesIncludingTaxFor10Percent(List<String> commonBlankLine,
        String breaktokuisakicd, String tax10percenturikin, String normalTax, String reportType,
        String tairynm) {
        return getTaxTotalSales(commonBlankLine, URIAGE_NYUKIN_KBN_URIAGE_GOUKEI_10PERCENT_TAX_KOMI,
            breaktokuisakicd, tax10percenturikin, normalTax, reportType, tairynm, "", "");
    }

    /**
     * 税額合計出力処理
     * 
     * @param programId プログラムID
     * @param yubinno 郵便番号
     * @param adrsn1 請求先住所1
     * @param adrsn2 請求先住所2
     * @param adrsn3 請求先住所3
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param skysakicd 請求先コード
     * @param skysakinmn1 請求先名称1
     * @param skysakinmn2 請求先名称2
     * @param keishokbn 敬称区分
     * @param yubinnobakodo 郵便バーコード
     * @param systemdydm システム日付
     * @param reportName 帳票名
     * @param shimeDate 締日
     * @param skyshono 請求書番号
     * @param sshdshhitoimage 差出人イメージ
     * @param reportType 帳票区分
     * @param printYearMonth 印字日時
     * @param kaishuhohokbn 回収方法区分
     * @param taxkumknm 消費税項目名
     * @param tokuisakicd 得意先コード
     * @param breaktokuisakicd 改ページ用得意先コード
     * @param tairynm 滞留名
     * @param kakutokuyoteipoint 獲得予定ポイント
     * @param pointrnk ポイントステージ
     * @return 税額合計出力処理で設定する各設定値のArrayList
     */
    private ArrayList<List<String>> pointOutput(String programId, String yubinno, String adrsn1,
        String adrsn2, String adrsn3, String apkihoncd, String shopcd, String skysakicd,
        String skysakinmn1, String skysakinmn2, String keishokbn, String yubinnobakodo,
        String systemdydm, String reportName, String shimeDate, String skyshono,
        String sshdshhitoimage, String reportType, String printYearMonth, String kaishuhohokbn,
        String taxkumknm, String tokuisakicd, String breaktokuisakicd, String tairynm,
        String kakutokuyoteipoint, String pointrnk) {
        // プログラムID～格納用銀行名称2を取得
        List<String> commonBlankLineList = getCommonBlankLine(programId, yubinno, adrsn1, adrsn2,
            adrsn3, apkihoncd, shopcd, skysakicd, skysakinmn1, skysakinmn2, keishokbn,
            yubinnobakodo, systemdydm, reportName, shimeDate, skyshono, sshdshhitoimage, reportType,
            printYearMonth, kaishuhohokbn, taxkumknm, tokuisakicd);
        ArrayList<List<String>> arrayList = new ArrayList<List<String>>();

        // 獲得予定ポイント
        arrayList.add(getKakutokuYoteiPoint(commonBlankLineList, breaktokuisakicd, reportType,
            tairynm, kakutokuyoteipoint, pointrnk));
        // ポイントステージ
        arrayList.add(getPointRnk(commonBlankLineList, breaktokuisakicd, reportType, tairynm,
            kakutokuyoteipoint, pointrnk));

        return arrayList;
    }

    /**
     * 獲得予定ポイントのリストを返却する
     * 
     * @param commonBlankLine 空行の52行目までのリスト
     * @param breaktokuisakicd 改行得意先コード
     * @param reportType 帳票種別
     * @param tairynm 滞留名
     * @param kakutokuyoteipoint 獲得予定ポイント
     * @param pointrnk ポイントステージ
     * @return 獲得予定ポイントList
     */
    private List<String> getKakutokuYoteiPoint(List<String> commonBlankLine,
        String breaktokuisakicd, String reportType, String tairynm, String kakutokuyoteipoint,
        String pointrnk) {
        return getTaxTotalSales(commonBlankLine, URIAGE_NYUKIN_KBN_KAKUTOKU_YOTEI_POINT,
            breaktokuisakicd, "", "", reportType, tairynm, kakutokuyoteipoint, pointrnk);
    }

    /**
     * ポイントステージのリストを返却する
     * 
     * @param commonBlankLine 空行の52行目までのリスト
     * @param breaktokuisakicd 改行得意先コード
     * @param reportType 帳票種別
     * @param tairynm 滞留名
     * @param kakutokuyoteipoint 獲得予定ポイント
     * @param pointrnk ポイントステージ
     * @return 獲得予定ポイントList
     */
    private List<String> getPointRnk(List<String> commonBlankLine, String breaktokuisakicd,
        String reportType, String tairynm, String kakutokuyoteipoint, String pointrnk) {
        return getTaxTotalSales(commonBlankLine, URIAGE_NYUKIN_KBN_POINT_RNK, breaktokuisakicd, "",
            "", reportType, tairynm, kakutokuyoteipoint, pointrnk);
    }

    /**
     * 空行のリストを返却する
     * 
     * @param commonBlankLine 空行の52行目までのリスト
     * @param breaktokuisakicd 改ページ用得意先コード
     * @param reportType 帳票種別
     * @param tairynm 滞留名
     * @return 空行List
     */
    private List<String> getBlankLine(List<String> commonBlankLine, String breaktokuisakicd,
        String reportType, String tairynm) {
        return getTaxTotalSales(commonBlankLine, URIAGE_NYUKIN_KBN_KARA, breaktokuisakicd, "0", "",
            reportType, tairynm, "", "");
    }

    /**
     * 空行５２行目までと入金売上区分,明細金額合計,明細消費税合計,明細金額消費税合計,改ページ用得意先コード,内お支払期日超過残高,振替予定金額名称,税率を設定したリストを返却する
     * 
     * @param commonBlankLine プログラムＩＤ～格納用銀行名称２
     * @param salesDepositType 売上入金区分
     * @param breaktokuisakicd 改ページ用得意先コード
     * @param taxTotalAmount 明細金額消費税合計
     * @param taxRate 税率
     * @param reportType 帳票種別
     * @param tairynm 滞留名
     * @param kakutokuyoteipoint 獲得予定ポイント
     * @param pointrnk ポイントステージ
     * @return 各種金額のリスト
     */
    private List<String> getTaxTotalSales(List<String> commonBlankLine, String salesDepositType,
        String breaktokuisakicd, String taxTotalAmount, String taxRate, String reportType,
        String tairynm, String kakutokuyoteipoint, String pointrnk) {
        List<String> list = new ArrayList<>();
        // 空行の52行目までを設定
        list.addAll(commonBlankLine);
        // 入金売上区分
        list.add(salesDepositType);
        // 明細金額合計
        list.add("0");
        // 明細消費税合計
        list.add("0");
        // 明細金額消費税合計
        list.add(taxTotalAmount);
        // 改ページ用得意先コード
        list.add(ListCreatorUtil.wrapQuote(breaktokuisakicd));
        // 内お支払期日超過残高
        list.add(tairynm);
        // 振替予定金額名称
        list.add("");
        // 税率
        list.add(taxRate);
        // お繰越金額
        list.add("");
        // 獲得予定ポイント
        list.add(kakutokuyoteipoint);
        // ポイントステージ
        list.add(pointrnk);
        return list;
    }

    /**
     * 売上入金明細のリストを返却する
     * 
     * @param programId プログラムID
     * @param keishokbn 敬称区分
     * @param reportType 帳票区分
     * @param printYearMonth 印字年月
     * @param kaishuhohokbn 回収方法区分
     * @param systemdydm システム日付
     * @param reportName 帳票名
     * @param shimeDate 締日
     * @param breaktokuisakicd 改ページ用得意先コード
     * @param dnpyoshirasenaiyon1 伝票お知らせ内容1
     * @param dnpyoshirasenaiyon2 伝票お知らせ内容2
     * @param dnpyoshirasenaiyon3 伝票お知らせ内容3
     * @param dnpyoshirasenaiyon4 伝票お知らせ内容4
     * @param dnpyoshirasenaiyon5 伝票お知らせ内容5
     * @param dnpyoshirasenaiyon6 伝票お知らせ内容6
     * @param oshirase2 お知らせ2
     * @param prntbiko 印刷備考
     * @param uridnpyno 売上伝票番号
     * @param kaishuhohokbnnm 回収方法区分名称
     * @param shrytidydmdd 支払予定年月日
     * @param creditPostingdd クレジット計上日
     * @param acikeiyuksikbn ACI決済区分名称
     * @param sksmkinyukknkzkindnm 請求先金融機関口座種別名称
     * @param sksmksikknnm 決済機関名称
     * @param sksmkinyukknkzno 請求先金融機関口座番号
     * @param sksmkinyukknshitennmkana 請求先金融機関支店名称カナ
     * @param hurikomisaki1 振込先1
     * @param hurikomisaki2 振込先2
     * @param invoiceFlag インボイス施行フラグ
     * @param entity 請求書出力データEntity
     * @param tokuisakiName 得意先名称
     * @param breakPageOutputCount 改ページ単位出力件数
     * @param prntpaykbn お振込先印刷区分
     * @param tairyuoverprntkbn 滞留超過額の印刷区分
     * @return 売上入金明細List
     */
    private List<String> getSalesDepositDetail(String programId, String keishokbn,
        String reportType, String printYearMonth, String kaishuhohokbn, String systemdydm,
        String reportName, String shimeDate, String breaktokuisakicd, String dnpyoshirasenaiyon1,
        String dnpyoshirasenaiyon2, String dnpyoshirasenaiyon3, String dnpyoshirasenaiyon4,
        String dnpyoshirasenaiyon5, String dnpyoshirasenaiyon6, String oshirase2, String prntbiko,
        String uridnpyno, String kaishuhohokbnnm, String shrytidydmdd, String creditPostingdd,
        String acikeiyuksikbn, String sksmkinyukknnmkana, String sksmkinyukknkzkindnm,
        String sksmksikknnm, String sksmkinyukknkzno, String sksmkinyukknshitennmkana,
        String hurikomisaki1, String hurikomisaki2, String invoiceFlag,
        S07T001TD1501R30ListResultEntity entity, String tokuisakiName, int breakPageOutputCount,
        String prntpaykbn, String tairyuoverprntkbn) {
        List<String> list = new ArrayList<>();
        // プログラムID
        list.add(programId);
        // 請求先郵便番号
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getYubinno())));
        // 請求先住所1
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getAdrsn1())));
        // 請求先住所2
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getAdrsn2())));
        // 請求先住所3
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getAdrsn3())));
        // AP基本コード
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getApkihoncd())));
        // ショップコード
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getShopcd())));
        // 請求先コード
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getSkysakicd())));
        // 請求先名称1
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getSkysakinmn1())));
        // 請求先名称2
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getSkysakinmn2())));
        // 敬称
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getSkysakikeishokbnnm())));
        // 郵便番号バーコード
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getYubinnobakodo())));
        // 発行日
        list.add(ItemUtil.nullToString(systemdydm));
        // 帳票タイトル
        list.add(ItemUtil.nullToString(reportName));
        // 締日
        list.add(ListCreatorUtil.wrapQuote(shimeDate));
        // 請求書番号
        list.add(entity.getSkyshonoToFormat(reportType));
        // ロゴ
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getSshdshhitoimage())));
        // 印字内容1~4
        list.addAll(d1501r00Service.getPrintContents(reportType, printYearMonth, kaishuhohokbn));
        // 消費税項目名
        list.add(ListCreatorUtil.wrapQuote(getTaxName(entity)));
        // 今回ご利用金額
        list.add(ListCreatorUtil
            .wrapQuote(getAmountForSalesDepositDetail(entity, breakPageOutputCount)));
        // 前回ご請求金額
        list.add(ListCreatorUtil
            .wrapQuote(getLastAmountForSalesDepositDetail(entity, breakPageOutputCount)));
        // ご入金額
        list.add(ListCreatorUtil
            .wrapQuote(getDepositAmountForSalesDepositDetail(entity, breakPageOutputCount)));
        // 内消費税1
        list.add(ListCreatorUtil
            .wrapQuote(getKonkaiShohiTaxKinForSalesDepositDetail(entity, breakPageOutputCount)));
        // 今回ご請求金額
        list.add(ListCreatorUtil
            .wrapQuote(getBillingAmountForSalesDepositDetail(entity, breakPageOutputCount)));
        // 内お支払い期日超過額
        if (isTairyuPrntKbn(entity.getSkyhohokbn(), tairyuoverprntkbn)) {
            list.add(ListCreatorUtil.wrapQuote(
                getOverduePaymentAmountForSalesDepositDetail(entity, breakPageOutputCount)));
        } else {
            list.add("");
        }
        // 振替予定金額
        list.add(ListCreatorUtil
            .wrapQuote(getScheduledTransferAmount(invoiceFlag, entity, breakPageOutputCount)));
        // 得意先コード
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getTokuisakicd())));
        // 得意先名称
        list.add(ListCreatorUtil.wrapQuote(tokuisakiName));
        // 単価明細
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getTani())));
        // 商品名称明細
        list.add(ListCreatorUtil.wrapQuote(getItemNameDetailForSalesDepositDetail(entity)));
        // 税区分印明細
        list.add(ListCreatorUtil.wrapQuote(getTaxTypeDetailForSalesDepositDetail(entity)));
        // 明細備考明細
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getMeisaibiko())));
        // 数量明細
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getSuryo())));
        // 単価明細
        String untmei = getUnitPriceDetailForSalesDepositDetail(entity);
        if ("0".equals(untmei)) {
            // 単価が"0"の場合、空を設定
            list.add(ListCreatorUtil.wrapQuote(""));
        } else {
            list.add(ListCreatorUtil.wrapQuote(getUnitPriceDetailForSalesDepositDetail(entity)));
        }
        // 金額明細
        list.add(ListCreatorUtil.wrapQuote(getAmountDetailForSalesDepositDetail(entity)));
        // 日付明細
        list.add(ListCreatorUtil.wrapQuote(getDateDetailForSalesDepositDetail(entity, uridnpyno)));
        // 格納用お知らせ欄1-6,格納用備考欄,格納用お知らせ２
        list.addAll(getNoticeAndRemarksForSalesDepositDetail(entity, dnpyoshirasenaiyon1,
            dnpyoshirasenaiyon2, dnpyoshirasenaiyon3, dnpyoshirasenaiyon4, dnpyoshirasenaiyon5,
            dnpyoshirasenaiyon6, oshirase2, prntbiko, reportType, breakPageOutputCount));
        // 格納用お支払い方法お支払期限1
        list.add(ListCreatorUtil.wrapQuote(getPaymentMethodAndPayDeadline1ForSalesDepositDetail(
            reportType, kaishuhohokbn, kaishuhohokbnnm, shrytidydmdd, creditPostingdd, entity,
            acikeiyuksikbn, breakPageOutputCount)));
        // 格納用お支払い方法お支払期限2
        list.add(ListCreatorUtil.wrapQuote(getPaymentMethodAndPayDeadline2ForSalesDepositDetail(
            entity, kaishuhohokbn, sksmkinyukknnmkana, sksmkinyukknkzkindnm, sksmksikknnm,
            sksmkinyukknkzno, sksmkinyukknshitennmkana, breakPageOutputCount)));
        // 格納用お振込先タイトル1、格納用銀行名称1設定
        list.addAll(getTransferAccountAndBankNameForSalesDepositDetail(kaishuhohokbn, "お振込先①",
            hurikomisaki1, entity, breakPageOutputCount, reportType, prntpaykbn));
        // 格納用お振込先タイトル2、格納用銀行名称2設定
        list.addAll(getTransferAccountAndBankNameForSalesDepositDetail(kaishuhohokbn, "お振込先②",
            hurikomisaki2, entity, breakPageOutputCount, reportType, prntpaykbn));
        // 売上入金区分
        list.add(ItemUtil.nullToString(entity.getUrinykkbn()));
        // 明細金額合計
        list.add("0");
        // 明細消費税合計
        list.add("0");
        // 明細金額消費税合計
        list.add("0");
        // 改ページ用得意先コード
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getBreaktokuisakicd())));
        // 内お支払期日超過残高
        if (isTairyuPrntKbn(entity.getSkyhohokbn(), tairyuoverprntkbn)) {
            list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getTairyukinmei())));
        } else {
            list.add("");
        }
        // 振替予定金額名称
        list.add("");
        // 税率
        list.add("");
        // お繰越金額
        list.add(ListCreatorUtil
            .wrapQuote(getSalesDepositDetailOkurikoshikin(entity, breakPageOutputCount)));
        // 獲得予定ポイント
        list.add("");
        // ポイントステージ
        list.add("");
        return list;
    }

    /**
     * 売上入金最終行のリストを返却する
     * 
     * @param programId プログラムID
     * @param yubinno 郵便番号
     * @param adrsn1 請求先住所1
     * @param adrsn2 請求先住所2
     * @param adrsn3 請求先住所3
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param skysakicd 請求先コード
     * @param skysakinmn1 請求先名称1
     * @param skysakinmn2 請求先名称2
     * @param keishokbn 敬称区分
     * @param yubinnobakodo 郵便番号バーコード
     * @param systemdydm システム日付
     * @param reportName 帳票名
     * @param shimeDate 締日
     * @param skyshono 請求書番号
     * @param sshdshhitoimage 差出人イメージ
     * @param reportType 帳票区分
     * @param printYearMonth 印字日時
     * @param kaishuhohokbn 回収方法区分
     * @param taxkumknm 消費税項目名
     * @param tokuisakicd 得意先コード
     * @param urinykkbn 売上入金区分
     * @param ttlkin 明細金額合計
     * @param totaltax 明細消費税合計
     * @param breaktokuisakicd 改ページ用得意先コード
     * @param tairynm 滞留名
     * @param meisaiprntkbn 明細印刷区分
     * @param skykbn 請求区分
     * @param tokuisakiName 得意先名称
     * @param konkaigoriyokin 今回ご利用額
     * @param znkkurikoshikin 前回繰越金額
     * @param konkainyknkin 今回入金金額
     * @param konkaishohitaxkin 今回消費税金額
     * @param kurikoshikin 繰越金額
     * @param tairyukin 滞留金額
     * @param dnpyoshirasenaiyon1 伝票お知らせ内容1
     * @param dnpyoshirasenaiyon2 伝票お知らせ内容2
     * @param dnpyoshirasenaiyon3 伝票お知らせ内容3
     * @param dnpyoshirasenaiyon4 伝票お知らせ内容4
     * @param dnpyoshirasenaiyon5 伝票お知らせ内容5
     * @param dnpyoshirasenaiyon6 伝票お知らせ内容6
     * @param oshirase2 お知らせ2
     * @param kaishuhohokbnnm 回収方法区分名称
     * @param shrytidydmdd 支払予定年月日
     * @param creditPostingdd クレジット計上日
     * @param acikeiyuksikbn ACI経由決済区分
     * @param sksmkinyukknnmkana 請求先金融機関名称カナ
     * @param sksmkinyukknkzkindnm 請求先金融機関口座種別名称
     * @param sksmksikknnm 決済機関名称
     * @param sksmkinyukknkzno 請求先金融機関口座番号
     * @param sksmkinyukknshitennmkana 請求先金融機関支店名称カナ
     * @param hurikomisaki1 振込先1
     * @param hurikomisaki2 振込先2
     * @param entityList 請求書出力データEntityリスト
     * @param breakPageOutputCount 改ページ単位出力件数
     * @param okurikoshikin お繰越金
     * @param hurikaeYoteikin 振替予定金額
     * @param tairyuoverprntkbn 滞留超過額の印刷区分
     * @return 売上最終入金行List
     */
    private List<String> getSalesDepositEndLine(String programId, String yubinno, String adrsn1,
        String adrsn2, String adrsn3, String apkihoncd, String shopcd, String skysakicd,
        String skysakinmn1, String skysakinmn2, String keishokbn, String yubinnobakodo,
        String systemdydm, String reportName, String shimeDate, String skyshono,
        String sshdshhitoimage, String reportType, String printYearMonth, String kaishuhohokbn,
        String taxkumknm, String tokuisakicd, String urinykkbn, String ttlkin, String totaltax,
        String breaktokuisakicd, String tairynm, String meisaiprntkbn, String skykbn,
        String tokuisakiName, String konkaigoriyokin, String znkkurikoshikin, String konkainyknkin,
        String konkaishohitaxkin, String kurikoshikin, String tairyukin, String dnpyoshirasenaiyon1,
        String dnpyoshirasenaiyon2, String dnpyoshirasenaiyon3, String dnpyoshirasenaiyon4,
        String dnpyoshirasenaiyon5, String dnpyoshirasenaiyon6, String prntbiko, String oshirase2,
        String kaishuhohokbnnm, String shrytidydmdd, String creditPostingdd, String acikeiyuksikbn,
        String sksmkinyukknnmkana, String sksmkinyukknkzkindnm, String sksmksikknnm,
        String sksmkinyukknkzno, String sksmkinyukknshitennmkana, String hurikomisaki1,
        String hurikomisaki2, List<S07T001TD1501R30ListResultEntity> entityList,
        int breakPageOutputCount, String okurikoshikin, String hurikaeYoteikin, String prntpaykbn,
        String tairyuoverprntkbn) {
        // 最後から二つ目のentityを取得
        S07T001TD1501R30ListResultEntity entity =
            entityList.size() >= 2 ? entityList.get(entityList.size() - 2) : entityList.get(0);
        // 改ページ用得意先コードを取得
        String lastBreakTokuisakiCd = entityList.size() >= 2 ? breaktokuisakicd : "";
        List<String> list = new ArrayList<>();
        // プログラムID
        list.add(programId);
        // 請求先郵便番号
        list.add(ListCreatorUtil.wrapQuote(yubinno));
        // 請求先住所1
        list.add(ListCreatorUtil.wrapQuote(adrsn1));
        // 請求先住所2
        list.add(ListCreatorUtil.wrapQuote(adrsn2));
        // 請求先住所3
        list.add(ListCreatorUtil.wrapQuote(adrsn3));
        // AP基本コード
        list.add(ListCreatorUtil.wrapQuote(apkihoncd));
        // ショップコード
        list.add(ListCreatorUtil.wrapQuote(shopcd));
        // 請求先コード
        list.add(ListCreatorUtil.wrapQuote(skysakicd));
        // 請求先名称1
        list.add(ListCreatorUtil.wrapQuote(skysakinmn1));
        // 請求先名称2
        list.add(ListCreatorUtil.wrapQuote(skysakinmn2));
        // 敬称
        list.add(ListCreatorUtil.wrapQuote(keishokbn));
        // 郵便番号バーコード
        list.add(ListCreatorUtil.wrapQuote(yubinnobakodo));
        // 発行日
        list.add(systemdydm);
        // 帳票タイトル
        list.add(reportName);
        // 締日
        list.add(ListCreatorUtil.wrapQuote(shimeDate));
        // 請求書番号
        list.add(skyshono);
        // ロゴ
        list.add(ListCreatorUtil.wrapQuote(sshdshhitoimage));
        // 印字内容1～4
        list.addAll(d1501r00Service.getPrintContents(reportType, printYearMonth, kaishuhohokbn));
        // 消費税項目名
        list.add(ListCreatorUtil.wrapQuote(taxkumknm));
        // 今回ご利用額、前回ご請求額、ご入金額、内消費税1、今回ご請求額、振替予定金額
        list.addAll(getAmounts(konkaigoriyokin, znkkurikoshikin, konkainyknkin, konkaishohitaxkin,
            kurikoshikin, tairyukin, lastBreakTokuisakiCd, entity, breakPageOutputCount,
            hurikaeYoteikin));
        // 得意先コード
        list.add(ListCreatorUtil.wrapQuote(tokuisakicd));
        // 得意先名称
        list.add(ListCreatorUtil.wrapQuote(tokuisakiName));
        // 単位明細
        list.add("");
        // 商品名称明細
        list.add("");
        // 税区分印明細
        list.add("");
        // 明細備考明細
        list.add("");
        // 数量明細
        list.add("");
        // 単価明細
        list.add("");
        // 金額明細
        list.add("");
        // 日付明細
        list.add("");
        // 格納用お知らせ欄1-6,格納用備考欄,格納用お知らせ２
        list.addAll(getNoticeAndRemarksForSalesDepositDetail(entity, dnpyoshirasenaiyon1,
            dnpyoshirasenaiyon2, dnpyoshirasenaiyon3, dnpyoshirasenaiyon4, dnpyoshirasenaiyon5,
            dnpyoshirasenaiyon6, oshirase2, prntbiko, reportType, breakPageOutputCount));
        // 格納用お支払い方法お支払期限1
        list.add(ListCreatorUtil.wrapQuote(getPaymentMethodAndPayDeadline1ForSalesDepositDetail(
            reportType, kaishuhohokbn, kaishuhohokbnnm, shrytidydmdd, creditPostingdd, entity,
            acikeiyuksikbn, breakPageOutputCount)));
        // 格納用お支払い方法お支払期限2
        list.add(ListCreatorUtil.wrapQuote(getPaymentMethodAndPayDeadline2ForSalesDepositDetail(
            entity, kaishuhohokbn, sksmkinyukknnmkana, sksmkinyukknkzkindnm, sksmksikknnm,
            sksmkinyukknkzno, sksmkinyukknshitennmkana, breakPageOutputCount)));
        // 格納用お振込先タイトル1、格納用銀行名称1設定
        list.addAll(getTransferAccountAndBankNameForSalesDepositDetail(kaishuhohokbn, "お振込先①",
            hurikomisaki1, entity, breakPageOutputCount, reportType, prntpaykbn));
        // 格納用お振込先タイトル2、格納用銀行名称2設定
        list.addAll(getTransferAccountAndBankNameForSalesDepositDetail(kaishuhohokbn, "お振込先②",
            hurikomisaki2, entity, breakPageOutputCount, reportType, prntpaykbn));
        // 売上入金区分
        list.add(getSalesDepositType(urinykkbn));
        // 明細金額合計
        list.add(ItemUtil.nullToString(ttlkin));
        // 明細消費税合計
        list.add(ItemUtil.nullToString(totaltax));
        // 明細金額消費税合計
        list.add(d1501r00Service.getSum(new ArrayList<String>(Arrays.asList(ttlkin, totaltax))));
        // 改ページ用得意先コード
        list.add(ListCreatorUtil.wrapQuote(breaktokuisakicd));
        // 内お支払期日超過残高
        if (isTairyuPrntKbn(entity.getSkyhohokbn(), tairyuoverprntkbn)) {
            list.add(ListCreatorUtil.wrapQuote(tairynm));
        } else {
            list.add("");
        }
        // 振替予定金額名称
        list.add("");
        // 税率
        list.add("");
        // お繰越金額
        list.add(getUrinyukinOkurikoshikin(okurikoshikin, breaktokuisakicd, breakPageOutputCount,
            entity));
        // 獲得予定ポイント
        list.add("");
        // ポイントステージ
        list.add("");
        return list;
    }

    /**
     * 空行の52行目までのリストを返却する
     * 
     * @param programId プログラムID
     * @param yubinno 郵便番号
     * @param adrsn1 請求先住所1
     * @param adrsn2 請求先住所2
     * @param adrsn3 請求先住所3
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param skysakicd 請求先コード
     * @param skysakinmn1 請求先名称1
     * @param skysakinmn2 請求先名称2
     * @param keishokbn 敬称区分
     * @param yubinnobakodo 郵便番号バーコード
     * @param systemdydm システム日付
     * @param reportName 帳票名
     * @param shimeDate 締日
     * @param skyshono 請求書番号
     * @param sshdshhitoimage 差出人イメージ
     * @param reportType 帳票区分
     * @param printYearMonth 印字日時
     * @param kaishuhohokbn 回収方法区分
     * @param taxkumknm 消費税項目名
     * @param tokuisakicd 得意先コード
     * @return 空行の52行目までのList
     */
    private List<String> getCommonBlankLine(String programId, String yubinno, String adrsn1,
        String adrsn2, String adrsn3, String apkihoncd, String shopcd, String skysakicd,
        String skysakinmn1, String skysakinmn2, String keishokbn, String yubinnobakodo,
        String systemdydm, String reportName, String shimeDate, String skyshono,
        String sshdshhitoimage, String reportType, String printYearMonth, String kaishuhohokbn,
        String taxkumknm, String tokuisakicd) {
        List<String> list = new ArrayList<>();
        // プログラムID
        list.add((programId));
        // 請求先郵便番号
        list.add(ListCreatorUtil.wrapQuote(yubinno));
        // 請求先住所1
        list.add(ListCreatorUtil.wrapQuote(adrsn1));
        // 請求先住所2
        list.add(ListCreatorUtil.wrapQuote(adrsn2));
        // 請求先住所3
        list.add(ListCreatorUtil.wrapQuote(adrsn3));
        // AP基本コード
        list.add(ListCreatorUtil.wrapQuote(apkihoncd));
        // ショップコード
        list.add(ListCreatorUtil.wrapQuote(shopcd));
        // 請求先コード
        list.add(ListCreatorUtil.wrapQuote(skysakicd));
        // 請求先名称1
        list.add(ListCreatorUtil.wrapQuote(skysakinmn1));
        // 請求先名称2
        list.add(ListCreatorUtil.wrapQuote(skysakinmn2));
        // 敬称
        list.add(ListCreatorUtil.wrapQuote(keishokbn));
        // 郵便番号バーコード
        list.add(ListCreatorUtil.wrapQuote(yubinnobakodo));
        // 発行日
        list.add(ItemUtil.nullToString(systemdydm));
        // 帳票タイトル
        list.add(ItemUtil.nullToString(reportName));
        // 締日
        list.add(ListCreatorUtil.wrapQuote(shimeDate));
        // 請求書番号
        list.add(ItemUtil.nullToString(skyshono));
        // ロゴ
        list.add(ListCreatorUtil.wrapQuote(sshdshhitoimage));
        // 印字内容1~5
        list.addAll(d1501r00Service.getPrintContents(reportType, printYearMonth, kaishuhohokbn));
        // 消費税項目名
        list.add(ListCreatorUtil.wrapQuote(taxkumknm));
        // 今回ご利用金額
        list.add("");
        // 前回ご請求金額
        list.add("");
        // ご入金額
        list.add("");
        // 内消費税1
        list.add("");
        // 今回ご請求金額
        list.add("");
        // 内お支払い期限超過額
        list.add("");
        // 振替予定金額
        list.add("");
        // 得意先コード
        list.add(ListCreatorUtil.wrapQuote(tokuisakicd));
        // 得意先名称
        list.add("");
        // 単価明細
        list.add("");
        // 商品名称明細
        list.add("");
        // 税区分印明細
        list.add("");
        // 明細備考明細
        list.add("");
        // 数量明細
        list.add("");
        // 単価明細
        list.add("");
        // 金額明細
        list.add("");
        // 日付明細
        list.add("");
        // 格納用お知らせ欄1-6,格納用備考欄,格納用お知らせ２
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        // 格納用お支払い方法お支払期限1
        list.add("");
        // 格納用お支払い方法お支払期限2
        list.add("");
        // 格納用お振込先タイトル1、格納用銀行名称1設定
        list.add("");
        list.add("");
        // 格納用お振込先タイトル2、格納用銀行名称2設定
        list.add("");
        list.add("");
        return list;
    }

    /**
     * 売上合計のリストを返却する
     * 
     * @param programId プログラムID
     * @param keishokbn 敬称区分
     * @param systemdydm システム日付
     * @param reportName 帳票名
     * @param shimeDate 締日
     * @param reportType 帳票区分
     * @param dnpyoshirasenaiyon1 伝票お知らせ内容1
     * @param dnpyoshirasenaiyon2 伝票お知らせ内容2
     * @param dnpyoshirasenaiyon3 伝票お知らせ内容3
     * @param dnpyoshirasenaiyon4 伝票お知らせ内容4
     * @param dnpyoshirasenaiyon5 伝票お知らせ内容5
     * @param dnpyoshirasenaiyon6 伝票お知らせ内容6
     * @param printYearMonth 印字年月
     * @param kaishuhohokbn 回収方法区分
     * @param prntbiko 印刷備考
     * @param oshirase2 お知らせ2
     * @param kaishuhohokbnnm 回収方法区分名称
     * @param shrytidydmdd 支払予定年月日
     * @param creditPostingdd クレジット計上日
     * @param acikeiyuksikbn ACI経由決済区分
     * @param sksmkinyukknnmkana 請求先金融機関名称カナ
     * @param sksmkinyukknkzkindnm 請求先金融機関口座種別名称
     * @param sksmksikknnm 決済機関名称
     * @param sksmkinyukknkzno 請求先金融機関口座番号
     * @param sksmkinyukknshitennmkana 請求先金融機関支店名称カナ
     * @param hurikomisaki1 振込先1
     * @param hurikomisaki2 振込先2
     * @param invoiceFlag インボイス施行フラグ
     * @param entity 請求書出力データEntity
     * @param tokuisakiName 得意先名称
     * @param prntpaykbn お振込先印刷区分
     * @param tairyuoverprntkbn 滞留超過額の印刷区分
     * @return 売上合計List
     */
    private List<String> getTotalSales(String programId, String keishokbn, String systemdydm,
        String reportName, String shimeDate, String reportType, String dnpyoshirasenaiyon1,
        String dnpyoshirasenaiyon2, String dnpyoshirasenaiyon3, String dnpyoshirasenaiyon4,
        String dnpyoshirasenaiyon5, String dnpyoshirasenaiyon6, String printYearMonth,
        String kaishuhohokbn, String prntbiko, String oshirase2, String kaishuhohokbnnm,
        String shrytidydmdd, String creditPostingdd, String acikeiyuksikbn,
        String sksmkinyukknnmkana, String sksmkinyukknkzkindnm, String sksmksikknnm,
        String sksmkinyukknkzno, String sksmkinyukknshitennmkana, String hurikomisaki1,
        String hurikomisaki2, String invoiceFlag, S07T001TD1501R30ListResultEntity entity,
        String tokuisakiName, String prntpaykbn, String tairyuoverprntkbn) {
        List<String> list = new ArrayList<>();
        // プログラムID
        list.add(programId);
        // 請求先郵便番号
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getYubinno())));
        // 請求先住所1
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getAdrsn1())));
        // 請求先住所2
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getAdrsn2())));
        // 請求先住所3
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getAdrsn3())));
        // AP基本コード
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getApkihoncd())));
        // ショップコード
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getShopcd())));
        // 請求先コード
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getSkysakicd())));
        // 請求先名称1
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getSkysakinmn1())));
        // 請求先名称2
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getSkysakinmn2())));
        // 敬称
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getSkysakikeishokbnnm())));
        // 郵便番号バーコード
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getYubinnobakodo())));
        // 発行日
        list.add(ItemUtil.nullToString(systemdydm));
        // 帳票タイトル
        list.add(ItemUtil.nullToString(reportName));
        // 締日
        list.add(ListCreatorUtil.wrapQuote(shimeDate));
        // 請求書番号
        list.add(ItemUtil.nullToString(entity.getSkyshonoToFormat(reportType)));
        // ロゴ
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getSshdshhitoimage())));
        // 印字内容1~5
        list.addAll(d1501r00Service.getPrintContents(reportType, printYearMonth, kaishuhohokbn));
        // 消費税項目名
        list.add(ListCreatorUtil.wrapQuote(getTaxName(entity)));
        // 今回ご利用金額
        list.add(ListCreatorUtil.wrapQuote(d1501r00Service.amountFormatter(getAmount(entity))));
        // 前回ご請求額
        list.add(ListCreatorUtil.wrapQuote(getLastAmount(entity)));
        // ご入金額
        list.add(ListCreatorUtil.wrapQuote(getDepositAmount(entity)));
        // 内消費税1
        list.add(ListCreatorUtil
            .wrapQuote(d1501r00Service.amountFormatter(entity.getKonkaishohitaxkin())));
        // 今回ご請求額
        list.add(
            ListCreatorUtil.wrapQuote(d1501r00Service.amountFormatter(getBillingAmount(entity))));
        // 内お支払い期日超過額
        if (isTairyuPrntKbn(entity.getSkyhohokbn(), tairyuoverprntkbn)) {
            list.add(
                ListCreatorUtil.wrapQuote(d1501r00Service.amountFormatter(entity.getTairyukin())));
        } else {
            list.add("");
        }
        // 振替予定金額
        list.add(ListCreatorUtil
            .wrapQuote(getScheduledTransferAmountForTotalSales(reportType, invoiceFlag, entity)));
        // 得意先コード
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getTokuisakicd())));
        // 得意先名称
        list.add(ListCreatorUtil.wrapQuote(tokuisakiName));
        // 単位明細
        list.add("");
        // 商品名称明細
        list.add("");
        // 税区分明細
        list.add("");
        // 明細備考明細
        list.add("");
        // 数量明細
        list.add("");
        // 単価明細
        list.add("");
        // 金額明細
        list.add("");
        // 日付明細
        list.add("");
        // 格納用お知らせ欄1
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(dnpyoshirasenaiyon1)));
        // 格納用お知らせ欄2
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(dnpyoshirasenaiyon2)));
        // 格納用お知らせ欄3
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(dnpyoshirasenaiyon3)));
        // 格納用お知らせ欄4
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(dnpyoshirasenaiyon4)));
        // 格納用お知らせ欄5
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(dnpyoshirasenaiyon5)));
        // 格納用お知らせ欄6
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(dnpyoshirasenaiyon6)));
        // 格納用印刷備考欄
        list.add(ListCreatorUtil.wrapQuote(prntbiko));
        // 格納用お知らせ2
        list.add(D1501R00Constants.TYOHYO_KBN_SKYSHO.equals(reportType)
            ? ItemUtil.nullToString(oshirase2)
            : "");
        // 格納用お支払い方法お支払期限1
        list.add(
            ListCreatorUtil.wrapQuote(d1501r00Service.getPaymentMethodAndPayDeadline1(reportType,
                kaishuhohokbn, kaishuhohokbnnm, shrytidydmdd, creditPostingdd, acikeiyuksikbn)));
        // 格納用お支払い方法お支払期限2
        list.add(ListCreatorUtil.wrapQuote(
            d1501r00Service.getPaymentMethodAndPayDeadline2(kaishuhohokbn, sksmkinyukknnmkana,
                sksmkinyukknkzkindnm, sksmksikknnm, sksmkinyukknkzno, sksmkinyukknshitennmkana)));
        // 格納用お振込先タイトル1、格納用銀行名称1設定
        list.addAll(getTransferAccountAndBankName(kaishuhohokbn, "お振込先①", hurikomisaki1, reportType,
            prntpaykbn));
        // 格納用お振込先タイトル2、格納用銀行名称2設定
        list.addAll(getTransferAccountAndBankName(kaishuhohokbn, "お振込先②", hurikomisaki2, reportType,
            prntpaykbn));
        // 売上入金区分
        list.add(URIAGE_NYUKIN_KBN_URIAGE_GOUKEI);
        // 明細金額合計
        list.add("0");
        // 明細消費税合計
        list.add("0");
        // 明細金額消費税合計
        list.add("0");
        // 改ページ用得意先コード
        list.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getBreaktokuisakicd())));
        // 内お支払期日超過残高
        if (isTairyuPrntKbn(entity.getSkyhohokbn(), tairyuoverprntkbn)) {
            list.add(ListCreatorUtil.wrapQuote(entity.getTairyukinmei()));
        } else {
            list.add("");
        }
        // 振替予定金額名称
        list.add("");
        // 税率
        list.add("");
        // お繰越金額
        list.add(ListCreatorUtil.wrapQuote(getTotalSalesOkurikoshikin(entity)));
        // 獲得予定ポイント
        list.add("");
        // ポイントステージ
        list.add("");
        return list;
    }

    /**
     * 売上入金合計のリストを返却する
     * 
     * @param programId プログラムID
     * @param yubinno 郵便番号
     * @param adrsn1 請求先住所1
     * @param adrsn2 請求先住所2
     * @param adrsn3 請求先住所3
     * @param apkihoncd AP基本コード
     * @param skysakicd ショップコード
     * @param skysakinmn1 請求先名称1
     * @param skysakinmn2 請求先名称2
     * @param keishokbn 敬称
     * @param yubinnobakodo 郵便番号バーコード
     * @param systemdydm システム日付
     * @param reportName 帳票タイトル
     * @param shimeDate 締日
     * @param skyshono 請求書番号
     * @param sshdshhitoimage ロゴ
     * @param reportType 帳票区分
     * @param printYearMonth 印字年月
     * @param kaishuhohokbn 回収方法区分
     * @param taxkumknm 消費税項目名
     * @param konkaigoriyokin 今回ご利用額
     * @param znkkurikoshikin 前回ご請求額
     * @param konkainyknkin 今回入金金額
     * @param konkaishohitaxkin 今回消費税金額
     * @param kurikoshikin 繰越金額
     * @param tairyukin 滞留金額
     * @param breaktokuisakicd 改ページ用得意先コード
     * @param tokuisakicd 得意先コード
     * @param dnpyoshirasenaiyon1 伝票お知らせ内容1
     * @param dnpyoshirasenaiyon2 伝票お知らせ内容2
     * @param dnpyoshirasenaiyon3 伝票お知らせ内容3
     * @param dnpyoshirasenaiyon4 伝票お知らせ内容4
     * @param dnpyoshirasenaiyon5 伝票お知らせ内容5
     * @param dnpyoshirasenaiyon6 伝票お知らせ内容6
     * @param oshirase2 お知らせ2
     * @param prntbiko 印刷備考
     * @param kaishuhohokbnnm 回収方法区分名称
     * @param shrytidydmdd 支払予定年月日
     * @param creditPostingdd クレジット計上日
     * @param acikeiyuksikbn ACI経由決済区分
     * @param sksmkinyukknnmkana 請求先金融機関名称カナ
     * @param sksmkinyukknkzkindnm 請求先金融機関口座種別名称
     * @param sksmksikknnm 決済機関名称
     * @param sksmkinyukknkzno 請求先金融機関口座番号
     * @param sksmkinyukknshitennmkana 請求先金融機関支店名称カナ
     * @param hurikomisaki1 振込先1
     * @param hurikomisaki2 振込先2
     * @param ttlkin 金額合計
     * @param totaltax 消費税合計
     * @param urinykkbn 売上入金区分
     * @param tairynm 滞留名
     * @param okurikoshikin お繰越金
     * @param entity 請求書出力データEntity
     * @param breakPageOutputCount 改ページ単位出力件数
     * @param hurikaeYoteikin 振替予定金額
     * @param prntpaykbn お振込先印字区分
     * @param tairyuoverprntkbn お支払期日超過額の印字区分
     * @return 売上入金合計List
     */
    private List<String> getTotalSalesDeposit(String programId, String yubinno, String adrsn1,
        String adrsn2, String adrsn3, String apkihoncd, String shopcd, String skysakicd,
        String skysakinmn1, String skysakinmn2, String keishokbn, String yubinnobakodo,
        String systemdydm, String reportName, String shimeDate, String skyshono,
        String sshdshhitoimage, String reportType, String printYearMonth, String kaishuhohokbn,
        String taxkumknm, String konkaigoriyokin, String znkkurikoshikin, String konkainyknkin,
        String konkaishohitaxkin, String kurikoshikin, String tairyukin, String breaktokuisakicd,
        String tokuisakicd, String dnpyoshirasenaiyon1, String dnpyoshirasenaiyon2,
        String dnpyoshirasenaiyon3, String dnpyoshirasenaiyon4, String dnpyoshirasenaiyon5,
        String dnpyoshirasenaiyon6, String oshirase2, String prntbiko, String kaishuhohokbnnm,
        String shrytidydmdd, String creditPostingdd, String acikeiyuksikbn,
        String sksmkinyukknnmkana, String sksmkinyukknkzkindnm, String sksmksikknnm,
        String sksmkinyukknkzno, String sksmkinyukknshitennmkana, String hurikomisaki1,
        String hurikomisaki2, String ttlkin, String totaltax, String urinykkbn, String tairynm,
        String okurikoshikin, S07T001TD1501R30ListResultEntity entity, String tokuisakiName,
        int breakPageOutputCount, String hurikaeYoteikin, String prntpaykbn,
        String tairyuoverprntkbn) {

        List<String> list = new ArrayList<>();
        // プログラムID
        list.add(programId);
        // 請求先郵便番号
        list.add(ListCreatorUtil.wrapQuote(yubinno));
        // 請求先住所1
        list.add(ListCreatorUtil.wrapQuote(adrsn1));
        // 請求先住所2
        list.add(ListCreatorUtil.wrapQuote(adrsn2));
        // 請求先住所3
        list.add(ListCreatorUtil.wrapQuote(adrsn3));
        // AP基本コード
        list.add(ListCreatorUtil.wrapQuote(apkihoncd));
        // ショップコード
        list.add(ListCreatorUtil.wrapQuote(shopcd));
        // 請求先コード
        list.add(ListCreatorUtil.wrapQuote(skysakicd));
        // 請求先名称1
        list.add(ListCreatorUtil.wrapQuote(skysakinmn1));
        // 請求先名称2
        list.add(ListCreatorUtil.wrapQuote(skysakinmn2));
        // 敬称
        list.add(ListCreatorUtil.wrapQuote(keishokbn));
        // 郵便番号バーコード
        list.add(ListCreatorUtil.wrapQuote(yubinnobakodo));
        // 発行日
        list.add(ItemUtil.nullToString(systemdydm));
        // 帳票タイトル
        list.add(ItemUtil.nullToString(reportName));
        // 締日
        list.add(ListCreatorUtil.wrapQuote(shimeDate));
        // 請求書番号
        list.add(ItemUtil.nullToString(skyshono));
        // ロゴ
        list.add(ListCreatorUtil.wrapQuote(sshdshhitoimage));
        // 印字内容1～4
        list.addAll(d1501r00Service.getPrintContents(reportType, printYearMonth, kaishuhohokbn));
        // 消費税項目名
        list.add(ListCreatorUtil.wrapQuote(taxkumknm));
        // 今回ご利用額、前回ご請求額、ご入金額、内消費税1、今回ご請求額、振替予定金額
        list.addAll(getAmounts(konkaigoriyokin, znkkurikoshikin, konkainyknkin, konkaishohitaxkin,
            kurikoshikin, tairyukin, breaktokuisakicd, entity, breakPageOutputCount,
            hurikaeYoteikin));
        // 得意先コード
        list.add(ListCreatorUtil.wrapQuote(tokuisakicd));
        // 得意先名称
        list.add(ListCreatorUtil.wrapQuote(tokuisakiName));
        // 単位明細
        list.add("");
        // 商品名称明細
        list.add("");
        // 税区分印明細
        list.add("");
        // 明細備考明細
        list.add("");
        // 数量明細
        list.add("");
        // 単価明細
        list.add("");
        // 金額明細
        list.add("");
        // 日付明細
        list.add("");
        // 格納用お知らせ欄1-6,格納用備考欄,格納用お知らせ２
        list.addAll(getNoticeAndRemarksForSalesDepositDetail(entity, dnpyoshirasenaiyon1,
            dnpyoshirasenaiyon2, dnpyoshirasenaiyon3, dnpyoshirasenaiyon4, dnpyoshirasenaiyon5,
            dnpyoshirasenaiyon6, oshirase2, prntbiko, reportType, breakPageOutputCount));
        // 格納用お支払い方法お支払期限1
        list.add(ListCreatorUtil.wrapQuote(getPaymentMethodAndPayDeadline1ForSalesDepositDetail(
            reportType, kaishuhohokbn, kaishuhohokbnnm, shrytidydmdd, creditPostingdd, entity,
            acikeiyuksikbn, breakPageOutputCount)));
        // 格納用お支払い方法お支払期限2
        list.add(ListCreatorUtil.wrapQuote(getPaymentMethodAndPayDeadline2ForSalesDepositDetail(
            entity, kaishuhohokbn, sksmkinyukknnmkana, sksmkinyukknkzkindnm, sksmksikknnm,
            sksmkinyukknkzno, sksmkinyukknshitennmkana, breakPageOutputCount)));

        // 格納用お振込先タイトル1、格納用銀行名称1設定
        list.addAll(getTransferAccountAndBankNameForSalesDepositDetail(kaishuhohokbn, "お振込先①",
            hurikomisaki1, entity, breakPageOutputCount, reportType, prntpaykbn));
        // 格納用お振込先タイトル2、格納用銀行名称2設定
        list.addAll(getTransferAccountAndBankNameForSalesDepositDetail(kaishuhohokbn, "お振込先②",
            hurikomisaki2, entity, breakPageOutputCount, reportType, prntpaykbn));
        // 売上入金区分
        list.add(URIAGE_NYUKIN_KBN_URIAGE.equals(urinykkbn) ? URIAGE_NYUKIN_KBN_URIAGE_GOUKEI
            : URIAGE_NYUKIN_KBN_NYUKIN_GOUKEI);
        // 明細金額合計
        list.add(ItemUtil.nullToString(ttlkin));
        // 明細消費税合計
        list.add(ItemUtil.nullToString(totaltax));
        // 明細金額消費税合計
        list.add(d1501r00Service.getSum(new ArrayList<String>(Arrays.asList(ttlkin, totaltax))));
        // 改ページ用得意先コード
        list.add(ListCreatorUtil.wrapQuote(breaktokuisakicd));
        // お支払期限超過残高
        if (isTairyuPrntKbn(entity.getSkyhohokbn(), tairyuoverprntkbn)) {
            list.add(ListCreatorUtil.wrapQuote(tairynm));
        } else {
            list.add("");
        }
        // 振替予定金額名称
        list.add("");
        // 税率
        list.add("");
        // お繰越金
        list.add(getUrinyukinOkurikoshikin(okurikoshikin, breaktokuisakicd, breakPageOutputCount,
            entity));
        // 獲得予定ポイント
        list.add("");
        // ポイントステージ
        list.add("");
        return list;
    }

    /**
     * 売上入金合計の今回ご利用額、前回ご請求額、ご入金額、内消費税1、今回ご請求額、振替予定金額をListで返却する
     * 
     * @param konkaigoriyokin 今回ご利用額
     * @param znkkurikoshikin 前回繰越金
     * @param konkainyknkin 今回入金金額
     * @param konkaishohitaxkin 今回消費税金額
     * @param kurikoshikin 繰越金額
     * @param tairyukin 滞留金額
     * @param breaktokuisakicd 改ページ用得意先コード
     * @param entity 請求書出力データEntity
     * @param breakPageOutputCount 改ページ単位出力件数
     * @param hurikaeYoteikin 振替予定金額
     * @return 今回ご利用額、前回ご請求額、ご入金額、内消費税1、今回ご請求額、振替予定金額List
     */
    private List<String> getAmounts(String konkaigoriyokin, String znkkurikoshikin,
        String konkainyknkin, String konkaishohitaxkin, String kurikoshikin, String tairyukin,
        String breaktokuisakicd, S07T001TD1501R30ListResultEntity entity, int breakPageOutputCount,
        String hurikaeYoteikin) {
        List<String> list = new ArrayList<>();
        if (!breaktokuisakicd.equals(entity.getBreaktokuisakicd()) || breakPageOutputCount == 0) {
            list.add(ListCreatorUtil.wrapQuote(d1501r00Service.amountFormatter(konkaigoriyokin)));
            list.add(ListCreatorUtil.wrapQuote(d1501r00Service.amountFormatter(znkkurikoshikin)));
            list.add(ListCreatorUtil.wrapQuote(d1501r00Service.amountFormatter(konkainyknkin)));
            list.add(ListCreatorUtil.wrapQuote(d1501r00Service.amountFormatter(konkaishohitaxkin)));
            list.add(ListCreatorUtil.wrapQuote(d1501r00Service.amountFormatter(kurikoshikin)));
            list.add(ListCreatorUtil.wrapQuote(d1501r00Service.amountFormatter(tairyukin)));
            list.add(ListCreatorUtil.wrapQuote(d1501r00Service.amountFormatter(hurikaeYoteikin)));
        } else {
            list.add("");
            list.add("");
            list.add("");
            list.add("");
            list.add("");
            list.add("");
            list.add("");
        }
        return list;
    }

    /**
     * 売上入金区分を返却する
     * 
     * @param urinykkbn 売上入金区分
     * @return 売上入金区分=1（売上）の場合（売上合計）：2 以外の場合：4（入金合計）
     */
    private String getSalesDepositType(String urinykkbn) {
        return URIAGE_NYUKIN_KBN_URIAGE.equals(urinykkbn) ? URIAGE_NYUKIN_KBN_URIAGE_GOUKEI
            : URIAGE_NYUKIN_KBN_NYUKIN_GOUKEI;
    }

    /**
     * 変数退避（ヘッダー項目）の繰越金額を返却する
     * 
     * @param entity 請求書出力データEntity
     * @return ご入金額
     */
    private String getAmountCarriedForward(S07T001TD1501R30ListResultEntity entity) {
        if (CommonConstants.SKYHOHOKBN_ZANDAKA.equals(entity.getSkyhohokbn())) {
            return entity.getKurikoshikin();
        } else {
            return entity.getKonkaitaxkomiurikin();
        }
    }

    /**
     * 変数退避（ヘッダー項目）の今回入金金額を返却する
     * 
     * @param entity 請求書出力データEntity
     * @return ご入金額
     */
    private String getThisTimeDepositAmount(S07T001TD1501R30ListResultEntity entity) {
        return CommonConstants.SKYHOHOKBN_ZANDAKA.equals(entity.getSkyhohokbn())
            ? entity.getKonkainyknkin()
            : "*********";
    }

    /**
     * 変数退避（ヘッダー項目）の前回繰越金を返却する
     * 
     * @param entity 請求書出力データEntity
     * @return ご入金額
     */
    private String getLastTimeAmountCarriedForward(S07T001TD1501R30ListResultEntity entity) {
        return CommonConstants.SKYHOHOKBN_ZANDAKA.equals(entity.getSkyhohokbn())
            ? entity.getZnkkurikoshikin()
            : "*********";
    }

    /**
     * 売上入金明細,売上入金合計の格納用お知らせ1-6,格納用備考欄,格納用お知らせ2のリストを返却する
     * 
     * @param entity 請求書出力データEntity
     * @param dnpyoshirasenaiyon1 伝票お知らせ内容1
     * @param dnpyoshirasenaiyon2 伝票お知らせ内容2
     * @param dnpyoshirasenaiyon3 伝票お知らせ内容3
     * @param dnpyoshirasenaiyon4 伝票お知らせ内容4
     * @param dnpyoshirasenaiyon5 伝票お知らせ内容5
     * @param dnpyoshirasenaiyon6 伝票お知らせ内容6
     * @param oshirase2 お知らせ2
     * @param prntbiko 印刷備考
     * @param reportType 帳票種別
     * @return 格納用お知らせ1-6,格納用備考欄,格納用お知らせ2のリスト
     */
    private List<String> getNoticeAndRemarksForSalesDepositDetail(
        S07T001TD1501R30ListResultEntity entity, String dnpyoshirasenaiyon1,
        String dnpyoshirasenaiyon2, String dnpyoshirasenaiyon3, String dnpyoshirasenaiyon4,
        String dnpyoshirasenaiyon5, String dnpyoshirasenaiyon6, String oshirase2, String prntbiko,
        String reportType, int breakPageOutputCount) {
        List<String> list = new ArrayList<>();
        if (breakPageOutputCount == 0) {
            list.add(ListCreatorUtil.wrapQuote(dnpyoshirasenaiyon1));
            list.add(ListCreatorUtil.wrapQuote(dnpyoshirasenaiyon2));
            list.add(ListCreatorUtil.wrapQuote(dnpyoshirasenaiyon3));
            list.add(ListCreatorUtil.wrapQuote(dnpyoshirasenaiyon4));
            list.add(ListCreatorUtil.wrapQuote(dnpyoshirasenaiyon5));
            list.add(ListCreatorUtil.wrapQuote(dnpyoshirasenaiyon6));
            list.add(ListCreatorUtil.wrapQuote(prntbiko));
            if (D1501R00Constants.TYOHYO_KBN_SKYSHO.equals(reportType)) {
                list.add(oshirase2);
            } else {
                list.add("");
            }
        } else {
            list.add("");
            list.add("");
            list.add("");
            list.add("");
            list.add("");
            list.add("");
            list.add("");
            list.add("");
        }
        return list;
    }

    /**
     * 売上入金明細の今回ご請求額を返却する
     * 
     * @param entity 請求書出力データEntity
     * @return 今回ご請求額
     */
    private String getDateDetailForSalesDepositDetail(S07T001TD1501R30ListResultEntity entity,
        String uridnpyno) {
        return uridnpyno.equals(entity.getUridnpyno()) ? "" : entity.getUridd();
    }

    /**
     * 売上入金明細で使用する金額明細を返却する
     * 
     * @param entity 請求書出力データEntity
     * @return 金額明細
     */
    private String getAmountDetailForSalesDepositDetail(S07T001TD1501R30ListResultEntity entity) {
        if (CommonConstants.KBN_TAXKEISAN_EX.equals(entity.getTaxkeisankbn())) {
            if (CommonConstants.TANPINMEISAIFLG_1.equals(entity.getTanpinmeisaiflg())
                && !StringUtils.isEmpty(entity.getStshncd())) {
                return "";
            } else {
                return ItemUtil.nullToString(entity.getKin());
            }
        } else {
            if (CommonConstants.TANPINMEISAIFLG_1.equals(entity.getTanpinmeisaiflg())
                && !StringUtils.isEmpty(entity.getStshncd())) {
                return "";
            } else {
                return d1501r00Service.getSum(
                    new ArrayList<String>(Arrays.asList(entity.getKin(), entity.getShohitaxkin())));
            }
        }
    }


    /**
     * 売上入金明細で使用する単価明細を返却する
     * 
     * @param entity 請求書出力データEntity
     * @return 単価明細
     */
    private String getUnitPriceDetailForSalesDepositDetail(
        S07T001TD1501R30ListResultEntity entity) {
        if (CommonConstants.KBN_TAXKEISAN_EX.equals(entity.getTaxkeisankbn())) {
            if (CommonConstants.TANPINMEISAIFLG_1.equals(entity.getTanpinmeisaiflg())
                && !StringUtils.isEmpty(entity.getStshncd())) {
                return "";
            } else {
                return ItemUtil.nullToString(entity.getUnt());
            }
        } else {
            if (CommonConstants.TANPINMEISAIFLG_1.equals(entity.getTanpinmeisaiflg())
                && !StringUtils.isEmpty(entity.getStshncd())) {
                return "";
            } else {
                return d1501r00Service.getSum(new ArrayList<String>(
                    Arrays.asList(entity.getUnt(), entity.getUntshohitaxkin())));
            }
        }
    }

    /**
     * 売上入金明細で使用する税区分印明細を返却する
     * 
     * @param entity 請求書出力データEntity
     * @return 税区分印明細
     */
    private String getTaxTypeDetailForSalesDepositDetail(S07T001TD1501R30ListResultEntity entity) {
        return d1501r00Service.getTaxTypeDetail(entity.getTanpinmeisaiflg(),
            entity.getNyuryokumeisaiflg(), entity.getTaxkbn(), entity.getOyasttaxkbn());
    }

    /**
     * 売上入金明細で使用する商品名称明細を返却する
     * 
     * @param entity 請求書出力データEntity
     * @return 商品名称明細
     */
    private String getItemNameDetailForSalesDepositDetail(S07T001TD1501R30ListResultEntity entity) {
        return d1501r00Service.getItemNameDetail(entity.getTanpinmeisaiflg(), entity.getStshncd(),
            entity.getShnnm());
    }

    /**
     * 売上入金明細で使用する内消費税１を返却
     * 
     * @param entity 請求書出力データEntity
     * @param breakPageOutputCount 改ページ単位出力件数
     * @return 今回消費税金額
     */
    private String getKonkaiShohiTaxKinForSalesDepositDetail(
        S07T001TD1501R30ListResultEntity entity, int breakPageOutputCount) {
        String s = breakPageOutputCount == 0 ? entity.getKonkaishohitaxkin() : "";
        s = StringUtils.isEmpty(s) ? s : d1501r00Service.amountFormatter(s);
        return s;
    }

    /**
     * 売上入金明細で使用する内お支払期日超過額を返却
     * 
     * @param entity 請求書出力データEntity
     * @param breakPageOutputCount 改ページ単位出力件数
     * @return 内お支払い期限超過額
     */
    private String getOverduePaymentAmountForSalesDepositDetail(
        S07T001TD1501R30ListResultEntity entity, int breakPageOutputCount) {
        String s = breakPageOutputCount == 0 ? entity.getTairyukin() : "";
        s = StringUtils.isEmpty(s) ? s : d1501r00Service.amountFormatter(s);
        return s;
    }

    /**
     * 売上入金明細で使用する今回ご請求金額を返却
     * 
     * @param entity 請求書出力データEntity
     * @param breakPageOutputCount 改ページ単位出力件数
     * @return 今回ご請求金額
     */
    private String getBillingAmountForSalesDepositDetail(S07T001TD1501R30ListResultEntity entity,
        int breakPageOutputCount) {
        if (breakPageOutputCount == 0) {
            if (CommonConstants.SKYHOHOKBN_ZANDAKA.equals(entity.getSkyhohokbn())) {
                return d1501r00Service.amountFormatter(entity.getKurikoshikin());
            } else {
                // 今回税抜売上金額 ＋今回消費税金額
                return d1501r00Service.amountFormatter(entity.getKonkaitaxkomiurikin());
            }
        } else {
            return "";
        }
    }

    /**
     * 売上入金明細で使用するご入金額を返却
     * 
     * @param entity 請求書出力データEntity
     * @param breakPageOutputCount 改ページ単位出力件数
     * @return ご入金額
     */
    private String getDepositAmountForSalesDepositDetail(S07T001TD1501R30ListResultEntity entity,
        int breakPageOutputCount) {
        if (breakPageOutputCount == 0) {
            return getDepositAmount(entity);
        } else {
            return "";
        }
    }

    /**
     * 売上入金明細で使用する前回ご請求金額を返却
     * 
     * @param entity 請求書出力データEntity
     * @param breakPageOutputCount 改ページ単位出力件数
     * @return 前回ご請求金額
     */
    private String getLastAmountForSalesDepositDetail(S07T001TD1501R30ListResultEntity entity,
        int breakPageOutputCount) {
        if (breakPageOutputCount == 0) {
            return getLastAmount(entity);
        } else {
            return "";
        }
    }

    /**
     * 変数退避2で設定する値を返却する。
     * 
     * @param skyhohokbn 請求方法区分
     * @param matchValue 一致時に返却する値
     * @param unmatchValue 一致していない場合に返却する値
     * @return 引数の請求方法区分が１の場合、matchValue以外はunmatchValue
     */
    private String getVariableSave2Value(String skyhohokbn, String matchValue,
        String unmatchValue) {
        return CommonConstants.SKYHOHOKBN_ZANDAKA.equals(skyhohokbn) ? matchValue : unmatchValue;
    }

    /**
     * 売上入金明細用の今回ご利用金額を返却
     * 
     * @param entity 請求書出力データEntity
     * @param breakPageOutputCount 改ページ単位出力件数
     * @return 今回ご利用金額
     */
    private String getAmountForSalesDepositDetail(S07T001TD1501R30ListResultEntity entity,
        int breakPageOutputCount) {
        if (breakPageOutputCount == 0) {
            String amount = getAmount(entity);
            return StringUtils.isEmpty(amount) ? amount : d1501r00Service.amountFormatter(amount);
        } else {
            return "";
        }
    }

    /**
     * 振込先を返却する
     * 
     * @param kinyukknnm 金融機関名称
     * @param kinyukknshitennm 金融機関支店名称
     * @param kinyukknkzkindnm 金融機関口座種別名称
     * @param kinyukknkzno 金融機関口座番号
     * @param kinyukknkzmeigi 金融機関口座名義
     * @return 振込先
     */
    private String getPayee(String kinyukknnm, String kinyukknshitennm, String kinyukknkzkindnm,
        String kinyukknkzno, String kinyukknkzmeigi) {
        String s = "";
        if (!StringUtils.isEmpty(kinyukknnm)) {
            s = s + kinyukknnm + "　";
        }

        if (!StringUtils.isEmpty(kinyukknshitennm)) {
            s = s + kinyukknshitennm + "　";
        }

        if (!StringUtils.isEmpty(kinyukknkzkindnm)) {
            s = s + kinyukknkzkindnm + "　";
        }

        if (!StringUtils.isEmpty(kinyukknkzno)) {
            s = s + kinyukknkzno + "　";
        }

        if (!StringUtils.isEmpty(kinyukknkzmeigi)) {
            s = s + kinyukknkzmeigi;
        }
        return s;
    }

    /**
     * 格納用お振込先タイトル、格納用銀行名称のlistを返却する
     * 
     * @param kaishuhohokbn 回収方法区分
     * @param title お振込先タイトル
     * @param hurikomisaki 振込先
     * @param entity 請求書出力データEntity
     * @param breakPageOutputCount 改ページ単位出力件数
     * @param reportType 帳票種別
     * @param prntpaykbn お振込先印刷区分
     * @return 格納用お振込先タイトル、格納用銀行名称のlist
     */
    private List<String> getTransferAccountAndBankNameForSalesDepositDetail(String kaishuhohokbn,
        String title, String hurikomisaki, S07T001TD1501R30ListResultEntity entity,
        int breakPageOutputCount, String reportType, String prntpaykbn) {

        List<String> list = new ArrayList<>();
        if (breakPageOutputCount == 0) {
            list.addAll(getTransferAccountAndBankName(kaishuhohokbn, title, hurikomisaki,
                reportType, prntpaykbn));
        } else {
            list.add("");
            list.add("");
        }

        return list;
    }

    /**
     * 格納用お振込先タイトル、格納用銀行名称のlistを返却する
     * 
     * @param kaishuhohokbn 回収方法区分
     * @param title お振込先タイトル
     * @param hurikomisaki 振込先
     * @param reportType 帳票区分
     * @param prntpaykbn お振込先印刷区分
     * @return 格納用お振込先タイトル、格納用銀行名称のlist
     */
    private List<String> getTransferAccountAndBankName(String kaishuhohokbn, String title,
        String hurikomisaki, String reportType, String prntpaykbn) {
        List<String> list = new ArrayList<>();

        // 帳票区分とお振込先印刷区分をチェックして、お振込先情報をリストに追加する
        // 帳票区分が「請求書」の場合
        if ("1".equals(reportType)) {
            // お振込先情報がある場合
            if (!hurikomisaki.isEmpty()) {
                list.add(title);
                list.add(hurikomisaki);
            } else {
                // お振込先情報がない場合
                list.add("");
                list.add("");
            }
        } else {
            // 帳票区分が「ご利用明細書」の場合
            if (CommonConstants.FLG_ON.equals(prntpaykbn) && !hurikomisaki.isEmpty()) {
                list.add(title);
                list.add(hurikomisaki);
            } else {
                // 上記の条件を満たさない場合
                list.add("");
                list.add("");
            }
        }
        return list;
    }

    /**
     * 売上入金明細,売上入金合計の格納用お支払い方法お支払期限2の設定値を返却する
     * 
     * @param entity 請求書出力データEntity
     * @param kaishuhohokbn 回収方法区分
     * @param sksmkinyukknnmkana 請求先金融機関名称カナ
     * @param sksmkinyukknkzkindnm 請求先金融機関口座種別名称
     * @param sksmksikknnm 決済機関名称
     * @param sksmkinyukknkzno 請求先金融機関口座番号
     * @param sksmkinyukknshitennmkana 請求先金融機関支店名称カナ
     * @param breakPageOutputCount 改ページ単位出力件数
     * @return 格納用お支払い方法お支払期限2
     */
    private String getPaymentMethodAndPayDeadline2ForSalesDepositDetail(
        S07T001TD1501R30ListResultEntity entity, String kaishuhohokbn, String sksmkinyukknnmkana,
        String sksmkinyukknkzkindnm, String sksmksikknnm, String sksmkinyukknkzno,
        String sksmkinyukknshitennmkana, int breakPageOutputCount) {
        if (breakPageOutputCount == 0) {
            return d1501r00Service.getPaymentMethodAndPayDeadline2(kaishuhohokbn,
                sksmkinyukknnmkana, sksmkinyukknkzkindnm, sksmksikknnm, sksmkinyukknkzno,
                sksmkinyukknshitennmkana);
        } else {
            return "";
        }
    }


    /**
     * 売上入金明細,売上入金合計の格納用お支払い方法お支払期限1の設定値を返却する
     * 
     * @param reportType 帳票区分
     * @param kaishuhohokbn 回収方法区分
     * @param kaishuhohokbnnm 回収方法区分名
     * @param shrytidydmdd 支払予定年月日
     * @param creditPostingdd クレジット計上日
     * @param entity 請求書出力データEntity
     * @param acikeiyuksikbn ACI経由決済区分
     * @param breakPageOutputCount 改ページ単位出力件数
     * @return 格納用お支払い方法お支払期限1
     */
    private String getPaymentMethodAndPayDeadline1ForSalesDepositDetail(String reportType,
        String kaishuhohokbn, String kaishuhohokbnnm, String shrytidydmdd, String creditPostingdd,
        S07T001TD1501R30ListResultEntity entity, String acikeiyuksikbn, int breakPageOutputCount) {
        if (breakPageOutputCount == 0) {
            return d1501r00Service.getPaymentMethodAndPayDeadline1(reportType, kaishuhohokbn,
                kaishuhohokbnnm, shrytidydmdd, creditPostingdd, acikeiyuksikbn);
        } else {
            return "";
        }
    }

    /**
     * 請求書CSVデータの振替予定金額を返却する
     * 
     * @param entity 請求書出力データEntity
     * @return 振替予定金額
     */
    private String getScheduledTransferAmount(String invoiceFlag,
        S07T001TD1501R30ListResultEntity entity, int breakPageOutputCount) {
        if (breakPageOutputCount == 0) {
            return getScheduledTransferAmountForGoriyoumeisai(invoiceFlag, entity);
        }
        return "";
    }

    /**
     * 売上合計の振替予定金額を返却する
     * 
     * @param reportType 帳票区分
     * @param entity 請求書出力データEntity
     * @return 振替予定金額
     */
    private String getScheduledTransferAmountForTotalSales(String reportType, String invoiceFlag,
        S07T001TD1501R30ListResultEntity entity) {
        if (D1501R00Constants.TYOHYO_KBN_GORIYOMEISAI_HIKIOTOSHI_YOTEI_ARI.equals(reportType)
            || D1501R00Constants.TYOHYO_KBN_GORIYOMEISAI_HIKIOTOSHI_YOTEI_NASHI
                .equals(reportType)) {
            return getScheduledTransferAmountForGoriyoumeisai(invoiceFlag, entity);
        }
        return "";
    }

    /**
     * ご利用明細書の振替予定金額を返却する
     * 
     * @param invoiceFlag インボイス施行フラグ
     * @param entity 請求書出力データEntity
     * @return 振替予定金額
     */
    private String getScheduledTransferAmountForGoriyoumeisai(String invoiceFlag,
        S07T001TD1501R30ListResultEntity entity) {
        if (hasScheduledTransferAmount(invoiceFlag, entity.getSkykbn(),
            entity.getMeisaiprntkbn())) {
            String scheduledTransferAmount = entity.getScheduledTransferAmountForGoriyoumeisai();
            return "".equals(scheduledTransferAmount) ? scheduledTransferAmount
                : d1501r00Service.amountFormatter(scheduledTransferAmount);
        }
        return "";
    }

    /**
     * ご利用明細書の振替予定金額を持っているか調べる
     * 
     * @param invoiceFlag インボイス施行フラグ
     * @param skyKbn 請求区分
     * @param meisaiPrintKbn 明細印刷区分
     * @return 振替予定金額を返却場合trueそれ以外はfalseを返却
     */
    private Boolean hasScheduledTransferAmount(String invoiceFlag, String skyKbn,
        String meisaiPrintKbn) {

        // 請求区分が1：一般 または
        // 請求区分が2：親子 かつ 明細印刷区分が1：親子卸の場合は出力対象とする。
        if (CommonConstants.SKYKBN_1.equals(skyKbn)) {
            return true;
        }
        if (CommonConstants.SKYKBN_2.equals(skyKbn)
            && CommonConstants.MEISAIPRNTKBN_ISSHO.equals(meisaiPrintKbn)) {
            return true;
        }
        return false;
    }

    /**
     * 請求書CSVデータの今回ご請求額を返却する
     * 
     * @param entity 請求書出力データEntity
     * @return 今回ご請求額
     */
    private String getBillingAmount(S07T001TD1501R30ListResultEntity entity) {
        return CommonConstants.SKYHOHOKBN_ZANDAKA.equals(entity.getSkyhohokbn())
            ? entity.getKurikoshikin()
            : entity.getKonkaigoriyokin();
    }

    /**
     * 請求書CSVデータの前回ご請求額を返却する
     * 
     * @param entity 請求書出力データEntity
     * @return 前回ご請求額
     */
    private String getLastAmount(S07T001TD1501R30ListResultEntity entity) {
        String s = CommonConstants.SKYHOHOKBN_ZANDAKA.equals(entity.getSkyhohokbn())
            ? entity.getZnkkurikoshikin()
            : "*********";
        s = "*********".equals(s) ? s : d1501r00Service.amountFormatter(s);
        return s;
    }

    /**
     * 請求書CSVデータ、変数退避（ヘッダー項目）の消費税項目名を返却する
     * 
     * @param entity 請求書出力データEntity
     * @return 消費税項目名
     */
    private String getTaxName(S07T001TD1501R30ListResultEntity entity) {
        return CommonConstants.KBN_TAXKEISAN_EX.equals(entity.getTaxkeisankbn()) ? "消費税額" : "内消費税額";
    }

    /**
     * 請求書CSVデータのご入金額を返却する
     * 
     * @param entity 請求書出力データEntity
     * @return ご入金額
     */
    private String getDepositAmount(S07T001TD1501R30ListResultEntity entity) {
        String s = CommonConstants.SKYHOHOKBN_ZANDAKA.equals(entity.getSkyhohokbn())
            ? entity.getKonkainyknkin()
            : "*********";
        s = "*********".equals(s) ? s : d1501r00Service.amountFormatter(s);
        return s;
    }


    /**
     * 請求書CSVデータの今回ご利用額を返却する
     * 
     * @param entity 請求書出力データEntity
     * @return 今回ご利用金額
     */
    private String getAmount(S07T001TD1501R30ListResultEntity entity) {
        if (CommonConstants.KBN_TAXKEISAN_EX.equals(entity.getTaxkeisankbn())) {
            return entity.getKonkaitaxnukiurikin();
        } else {
            return entity.getKonkaitaxkomiurikin();
        }
    }

    /**
     * 帳票区分から帳票名を取得する
     * 
     * @param reportType 帳票区分
     * @return 帳票名
     */
    private String getReportName(String reportType) {
        String result = "";
        switch (reportType) {
            case D1501R00Constants.TYOHYO_KBN_SKYSHO:
                result = "請求書";
                break;
            case D1501R00Constants.TYOHYO_KBN_GORIYOMEISAI_HIKIOTOSHI_YOTEI_ARI:
                result = "ご利用明細書";
                break;
            case D1501R00Constants.TYOHYO_KBN_GORIYOMEISAI_HIKIOTOSHI_YOTEI_NASHI:
                result = "ご利用明細書";
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * 帳票区分からプログラムIDを取得する
     * 
     * @param reportType 帳票区分
     * @return プログラムID
     */
    private String getProgramId(String reportType) {
        String result = "";
        switch (reportType) {
            case D1501R00Constants.TYOHYO_KBN_SKYSHO:
                result = D1501R00Constants.PROGRAM_ID_SKYSHO;
                break;
            case D1501R00Constants.TYOHYO_KBN_GORIYOMEISAI_HIKIOTOSHI_YOTEI_ARI:
                result = D1501R00Constants.PROGRAM_ID_GORIYOMEISAI_HIKIOTOSHI_ARI;
                break;
            case D1501R00Constants.TYOHYO_KBN_GORIYOMEISAI_HIKIOTOSHI_YOTEI_NASHI:
                result = D1501R00Constants.PROGRAM_ID_GORIYOMEISAI_HIKIOTOSHI_NASHI;
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * 売上合計のお繰越金額を返却する
     * 
     * @param entity 請求書出力データEntity
     * @return お繰越金額
     */
    private String getTotalSalesOkurikoshikin(S07T001TD1501R30ListResultEntity entity) {
        return d1501r00Service.amountFormatter(getOkurikoshikin(entity));
    }

    /**
     * 売上入金明細のお繰越金額を返却する
     * 
     * @param entity 請求書出力データEntity
     * @return お繰越金額
     */
    private String getSalesDepositDetailOkurikoshikin(S07T001TD1501R30ListResultEntity entity,
        int breakPageOutputCount) {
        if (breakPageOutputCount == 0) {
            return d1501r00Service.amountFormatter(getOkurikoshikin(entity));
        } else {
            return "";
        }
    }

    /**
     * お繰越金を返却する
     * 
     * @param entity 請求書出力データEntity
     * @return お繰越金額
     */
    private String getOkurikoshikin(S07T001TD1501R30ListResultEntity entity) {
        if (CommonConstants.SKYHOHOKBN_ZANDAKA.equals(entity.getSkyhohokbn())) {
            return d1501r00Service.getDifference(entity.getZnkkurikoshikin(),
                entity.getKonkainyknkin());
        }
        return "*********";
    }

    /**
     * 売上入金最終行、売上入金合計のお繰越金を取得する
     * 
     * @param okurikoshikin お繰越金
     * @param breaktokuisakicd 改ページ用得意先コード
     * @param breakPageOutputCount 改ページ単位出力件数
     * @param entity 請求書出力データ抽出用結果Entity
     * @return お繰越金
     */
    private String getUrinyukinOkurikoshikin(String okurikoshikin, String breaktokuisakicd,
        int breakPageOutputCount, S07T001TD1501R30ListResultEntity entity) {
        if (!breaktokuisakicd.equals(entity.getBreaktokuisakicd()) || breakPageOutputCount == 0) {
            return ListCreatorUtil.wrapQuote(d1501r00Service.amountFormatter(okurikoshikin));
        }
        return "";
    }

    /**
     * お支払期日超過額の印字判定を行う。
     * 
     * @param skyhohokbn 請求方法区分
     * @param tairyuoverprntkbn 滞留超過額の印刷区分
     * @return true：印字する、false：印字しない
     */
    private boolean isTairyuPrntKbn(String skyhohokbn, String tairyuoverprntkbn) {

        // 請求方法区分「1：残高請求」 かつ 画面.滞留超過額の印刷区分「1:する」場合のみ印字する
        if (CommonConstants.SKYHOHOKBN_ZANDAKA.equals(skyhohokbn)) {
            if (CommonConstants.FLG_ON.equals(tairyuoverprntkbn)) {
                return true;
            }
        }

        // 上記以外はfalse（印字しない）
        return false;
    }
}
