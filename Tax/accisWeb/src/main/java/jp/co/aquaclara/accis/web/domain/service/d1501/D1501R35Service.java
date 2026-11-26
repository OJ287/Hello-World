package jp.co.aquaclara.accis.web.domain.service.d1501;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R35ListResultEntity;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.common.util.ListCreatorUtil;
import jp.co.aquaclara.accis.web.domain.service.master.S02M006TService;

/**
 * 帳票出力 入金票兼領収書-Service.
 * 
 * @author accisdev047
 *
 */
@Service
public class D1501R35Service {
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
     * 入金票兼領収書中間データを作成する
     * 
     * @param s07t001td1501r35ResultEntityList S07T001TD1501R35ListResultEntityのList
     * @param systemDate システム時間
     * @param sshdshhitoImage 差出人イメージ
     * @return 入金票兼領収書中間データ
     */
    public ArrayList<List<String>> printDataEdit(
        List<S07T001TD1501R35ListResultEntity> s07t001td1501r35ResultEntityList, String systemDate,
        String sshdshhitoImage) {
        // 入金票兼領収書CSV
        ArrayList<List<String>> csvDataList = new ArrayList<List<String>>();
        // データ加工
        List<S07T001TD1501R35ListResultEntity> processedDataList =
            dataProcessing(s07t001td1501r35ResultEntityList);
        // 印紙基準金額を取得
        String stampStandardAmount = getStampStandardAmount();
        // 入金票兼領収書CSVに設定
        for (S07T001TD1501R35ListResultEntity entity : processedDataList) {
            csvDataList
                .add(createCsvData(entity, systemDate, stampStandardAmount, sshdshhitoImage));
        }
        return csvDataList;
    }

    /**
     * ご利用明細CSVの設定値リストを返却する
     * 
     * @param entity S07T001TD1501R40ResultEntity
     * @param systemDate システム時間
     * @param stampStandardAmount 印紙基準金額
     * @param sshdshhitoImage 差出人イメージ
     * @return 中間データの設定値リスト
     */
    private List<String> createCsvData(S07T001TD1501R35ListResultEntity entity, String systemDate,
        String stampStandardAmount, String sshdshhitoImage) {

        List<String> csvData = new ArrayList<String>();
        // 印紙に設定する値を取得
        String stamp = getStamp(entity.getRyosyukin(), stampStandardAmount);
        // プログラムID
        csvData.add(D1501R00Constants.PROGRAM_ID_NYUUKIN_RYOUSYUSYO);
        // 発行日
        csvData.add(systemDate);
        // 請求書番号
        csvData.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getSkyshono())));
        // 差出人イメージ
        csvData.add(ListCreatorUtil.wrapQuote(sshdshhitoImage));
        // 郵便番号～取扱店名までを設定
        csvData.addAll(createCsvDataFor5thTo16thColumnsList(entity));
        // 領収担当者
        csvData.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(entity.getTnshamei())));
        // 領収金額
        csvData.add(ItemUtil.nullToString(entity.getRyosyukin()));
        // 印紙
        csvData.add(ListCreatorUtil.wrapQuote(stamp));
        // 集金予定年月日
        csvData.add(ItemUtil.nullToString(entity.getShukindydmdd()));
        return csvData;
    }

    /**
     * 領収金額と印紙基準金額を比較し印紙に設定する値を返却する
     * 
     * @param ryosyukin 領収金額
     * @param stampStandardAmount 印紙基準金額
     * @return 領収金額が印紙基準金額以上の場合に"印紙"を返却 領収金額が印紙基準金額以下の場合、比較に失敗した場合は空を返却
     */
    private String getStamp(String ryosyukin, String stampStandardAmount) {
        String s = "";
        try {
            s = Integer.parseInt(stampStandardAmount) <= Integer.parseInt(ryosyukin) ? "印紙" : "";
        } catch (Exception e) {
            s = "";
        }
        return s;
    }

    /**
     * 得意先連絡先番号区分を順にチェックし得意先電話番号を取得する
     * 
     * @param entity S07T001TD1501R35ListResultEntity
     * @return FAX番号以外の電話番号
     */
    private String getTksPhoneNumber(S07T001TD1501R35ListResultEntity entity) {
        if (!CommonConstants.RENRAKUSAKI_FAX.equals(entity.getTksrenrakusakinokbnn1())) {
            return entity.getTksrenrakusakinon1();
        } else if (!CommonConstants.RENRAKUSAKI_FAX.equals(entity.getTksrenrakusakinokbnn2())) {
            return entity.getTksrenrakusakinon2();
        } else if (!CommonConstants.RENRAKUSAKI_FAX.equals(entity.getTksrenrakusakinokbnn3())) {
            return entity.getTksrenrakusakinon3();
        } else if (!CommonConstants.RENRAKUSAKI_FAX.equals(entity.getTksrenrakusakinokbnn4())) {
            return entity.getTksrenrakusakinon4();
        }
        return "";
    }

    /**
     * 請求先連絡先番号区分を順にチェックし請求先電話番号を取得する
     * 
     * @param entity S07T001TD1501R35ListResultEntity
     * @return FAX番号以外の電話番号
     */
    private String getSksPhoneNumber(S07T001TD1501R35ListResultEntity entity) {
        if (!CommonConstants.RENRAKUSAKI_FAX.equals(entity.getSksrenrakusakinokbnn1())) {
            return entity.getSksrenrakusakinon1();
        } else if (!CommonConstants.RENRAKUSAKI_FAX.equals(entity.getSksrenrakusakinokbnn2())) {
            return entity.getSksrenrakusakinon2();
        } else if (!CommonConstants.RENRAKUSAKI_FAX.equals(entity.getSksrenrakusakinokbnn3())) {
            return entity.getSksrenrakusakinon3();
        } else if (!CommonConstants.RENRAKUSAKI_FAX.equals(entity.getSksrenrakusakinokbnn4())) {
            return entity.getSksrenrakusakinon4();
        }
        return "";
    }

    /**
     * 入金票兼領収書Csvの５から１６列目の設定値のリストを返却する
     * 
     * @param entity S07T001TD1501R35ListResultEntity
     * @return 入金票兼領収書Csvの５から１６列目の設定値のリスト
     */
    private List<String> createCsvDataFor5thTo16thColumnsList(
        S07T001TD1501R35ListResultEntity entity) {
        List<String> csvData = new ArrayList<String>();
        String phoneNumber = "";
        String shopName = "";

        if (CommonConstants.MEISAIPRNTKBN_ISSHO.equals(entity.getMeisaiprntkbn())) {
            // 明細印刷区分='1'
            // 請求先電話番号を取得
            phoneNumber = getSksPhoneNumber(entity);
            // 入金票兼領収書Csvの５から１６列目の設定値のリストを作成
            csvData.addAll(getCsvDataFor5thTo16thColumnsList(entity.getSksyubinno(),
                entity.getSksadrsn1(), entity.getSksadrsn2(), entity.getSksadrsn3(), phoneNumber,
                entity.getApkihoncd(), entity.getShopcd(), entity.getSkysakicd(),
                entity.getSksskysakinmn1(), entity.getSksskysakinmn2(),
                entity.getSksskysakikeishokbnnm(), shopName));
        } else if (CommonConstants.MEISAIPRNTKBN_KOBETSU.equals(entity.getMeisaiprntkbn())
            && StringUtils.equals(entity.getSkysakicd(), entity.getTokuisakicd())) {
            // 明細印刷区分='2' and 請求先コード = 得意先コード
            // 得意先電話番号を取得
            phoneNumber = getTksPhoneNumber(entity);
            // 取扱店名を取得
            shopName = getDealerName(entity);
            // 入金票兼領収書Csvの５から１６列目の設定値のリストを作成
            csvData.addAll(getCsvDataFor5thTo16thColumnsList(entity.getTksyubinno(),
                entity.getTksadrsn1(), entity.getTksadrsn2(), entity.getTksadrsn3(), phoneNumber,
                entity.getApkihoncd(), entity.getShopcd(), entity.getSkysakicd(),
                entity.getTkstokuisakinmn1(), entity.getTkstokuisakinmn2(),
                entity.getTkstokuisakikeishokbnnm(), shopName));
        } else if (CommonConstants.MEISAIPRNTKBN_KOBETSU.equals(entity.getMeisaiprntkbn())
            && !StringUtils.equals(entity.getSkysakicd(), entity.getTokuisakicd())) {
            // 明細印刷区分='2' and 請求先コード <> 得意先コード
            // 請求先電話番号を取得
            phoneNumber = getSksPhoneNumber(entity);
            // 取扱店名を取得
            shopName = getDealerName(entity);
            // 入金票兼領収書Csvの５から１６列目の設定値のリストを作成
            csvData.addAll(getCsvDataFor5thTo16thColumnsList(entity.getSksyubinno(),
                entity.getSksadrsn1(), entity.getSksadrsn2(), entity.getSksadrsn3(), phoneNumber,
                entity.getApkihoncd(), entity.getShopcd(), entity.getSkysakicd(),
                entity.getTkstokuisakinmn1(), entity.getTkstokuisakinmn2(),
                entity.getTkstokuisakikeishokbnnm(), shopName));
        }
        return csvData;
    }

    /**
     * 取扱店名を取得する
     * 
     * @param entity S07T001TD1501R35ListResultEntity
     * @return 得意先区分が１（顧客）の場合に"取扱店:{ショップ名}"それ以外は空を返却
     */
    private String getDealerName(S07T001TD1501R35ListResultEntity entity) {
        return CommonConstants.KBN_TOKUISAKI_KOKUAKU.equals(entity.getTkstokuisakikbn())
            ? "取扱店：" + ItemUtil.nullToString(entity.getShopnm())
            : "";
    }

    /**
     * 引数の値をリストにセットし返却する
     * 
     * @param postCode 請求先郵便番号/得意先郵便番号
     * @param address1 請求先住所1/得意先住所1
     * @param address2 請求先住所2/得意先住所2
     * @param address3 請求先住所3/得意先住所3
     * @param phoneNumber 電話番号
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param skysakicd 請求先コード
     * @param name1 請求先名称1/得意先名称1
     * @param name2 請求先名称2/得意先名称2
     * @param honorificTitle 請求先敬称/得意先敬称
     * @param shop 取扱店
     * @return 入金票兼領収書Csvの５から１６列目の設定値のリスト
     */
    private List<String> getCsvDataFor5thTo16thColumnsList(String postCode, String address1,
        String address2, String address3, String phoneNumber, String apkihoncd, String shopcd,
        String skysakicd, String name1, String name2, String honorificTitle, String shop) {
        List<String> csvData = new ArrayList<String>();
        csvData.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(postCode)));
        csvData.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(address1)));
        csvData.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(address2)));
        csvData.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(address3)));
        csvData.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(phoneNumber)));
        csvData.add(ItemUtil.nullToString(apkihoncd));
        csvData.add(ItemUtil.nullToString(shopcd));
        csvData.add(ItemUtil.nullToString(skysakicd));
        csvData.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(name1)));
        csvData.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(name2)));
        csvData.add(ItemUtil.nullToString(honorificTitle));
        csvData.add(ListCreatorUtil.wrapQuote(ItemUtil.nullToString(shop)));
        return csvData;
    }

    /**
     * 入金表兼領収書のリストを作成するためのS07T001TD1501R35ListResultEntityのデータ加工処理を行う
     * 
     * @param entityList S07T001TD1501R35ListResultEntityのリスト
     * @return 加工データリスト
     */
    private List<S07T001TD1501R35ListResultEntity> dataProcessing(
        List<S07T001TD1501R35ListResultEntity> entityList) {

        S07T001TD1501R35ListResultEntity processedData = null;
        List<S07T001TD1501R35ListResultEntity> processedDataList =
            new ArrayList<S07T001TD1501R35ListResultEntity>();
        String wkSkyshono = "";
        String wkSkysakicd = "";
        String wkTokuisakicd = "";
        String wkRyousyukin = "0";

        for (S07T001TD1501R35ListResultEntity entity : entityList) {

            // ブレイク条件項目退避
            String brkSkyshono = entity.getSkyshono();
            String brkSkysakicd = entity.getSkysakicd();
            String brkTokuisakicd = entity.getTokuisakicd();
            String brkSkyhohokbn = entity.getSkyhohokbn();

            // ブレイク判断
            boolean breakCheck = isBreak(wkSkyshono, wkSkysakicd, wkTokuisakicd, brkSkyshono,
                brkSkysakicd, brkTokuisakicd, entity.getMeisaiprntkbn());

            if (breakCheck) {
                if (processedData != null) {
                    // 領収金額をセット
                    processedData.setRyosyukin(wkRyousyukin);
                    // 加工データEntityListに追加
                    processedDataList.add(processedData);
                    // 集計用領収金額をクリア
                    wkRyousyukin = "0";
                }
            }

            if (CommonConstants.SKYHOHOKBN_ZANDAKA.equals(brkSkyhohokbn)) {
                // 繰越金を加算
                wkRyousyukin = d1501r00Service.getSum(
                    new ArrayList<String>(Arrays.asList(wkRyousyukin, entity.getKurikoshikin())));
            } else {
                // 今回消費税抜売上金額を加算
                wkRyousyukin = d1501r00Service.getSum(new ArrayList<String>(
                    Arrays.asList(wkRyousyukin, entity.getKonkaitaxnukiurikin())));
                // 今回消費税金額を加算
                wkRyousyukin = d1501r00Service.getSum(new ArrayList<String>(
                    Arrays.asList(wkRyousyukin, entity.getKonkaishohitaxkin())));
            }

            // 前回結果の退避
            processedData = entity;
            wkSkyshono = entity.getSkyshono();
            wkSkysakicd = entity.getSkysakicd();
            wkTokuisakicd = entity.getTokuisakicd();

        }
        // 返却するリストにセット
        if (processedData != null) {
            processedData.setRyosyukin(wkRyousyukin);
            processedDataList.add(processedData);
        }
        return processedDataList;
    }

    /**
     * 領収金額の処理を行うためのブレイク判定結果を返却する
     * 
     * @param wkSkyshono wk請求書番号
     * @param wkSkysakicd wk請求先コード
     * @param wkTokuisakicd wk得意先コード
     * @param brkSkyshono brk請求書番号
     * @param brkSkysakicd brk請求先コード
     * @param brkTokuisakicd brk得意先コード
     * @param meisaiPrintKbn 明細印刷区分
     * @return true:ブレイクする false:ブレイクしない
     */
    private boolean isBreak(String wkSkyshono, String wkSkysakicd, String wkTokuisakicd,
        String brkSkyshono, String brkSkysakicd, String brkTokuisakicd, String meisaiPrintKbn) {
        // wk請求書番号≠"" && brk請求書番号≠wk請求書番号
        if (!org.springframework.util.StringUtils.isEmpty(wkSkyshono)
            && !StringUtils.equals(brkSkyshono, wkSkyshono)) {
            return true;
        }
        // wk請求先コード≠"" && brk請求先コード≠wk請求先コード
        if (!org.springframework.util.StringUtils.isEmpty(wkSkysakicd)
            && !StringUtils.equals(brkSkysakicd, wkSkysakicd)) {
            return true;
        }
        // wk得意先コード≠"" && brk得意先コード≠wk得意先コード && 入金票兼領収書出力データEntity.明細印刷区分 == '2'（別々）
        if (!org.springframework.util.StringUtils.isEmpty(wkTokuisakicd)
            && !StringUtils.equals(brkTokuisakicd, wkTokuisakicd)
            && CommonConstants.MEISAIPRNTKBN_KOBETSU.equals(meisaiPrintKbn)) {
            return true;
        }
        return false;
    }

    /**
     * 印紙基準金額の値をシステム管理マスタより取得する
     * 
     * @return 印紙基準金額
     */
    public String getStampStandardAmount() {
        return s02m006tService.searchForMoji("000", "000", "904", "1");
    }
}
