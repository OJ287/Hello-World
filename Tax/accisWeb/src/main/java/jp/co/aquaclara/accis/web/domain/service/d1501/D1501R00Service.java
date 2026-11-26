package jp.co.aquaclara.accis.web.domain.service.d1501;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M017TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M046TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M091TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M091TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M091TEntityExample;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.common.util.ListCreatorUtil;

/**
 * 帳票出力 共通クラス
 * 
 * @author accisdev047
 *
 */
@Service
public class D1501R00Service {
    /**
     * ショップマスタDao
     */
    @Autowired
    private S02M046TDao s02m046tDao;

    /**
     * 消費税率管理マスタDao
     */
    @Autowired
    private S02M091TDao s02m091tDao;
    /**
     * 請求先マスタDao
     */
    @Autowired
    private S02M017TDao s02m017tDao;

    /**
     * 引数の値を足し算して返却する 数値以外の文字列が引数に設定されている場合は空を返却する
     * 
     * @param list 足し算を行う文字列のリスト
     * @return 合計値の文字列 エラーの場合空文字列
     */
    public String getSum(List<String> list) {
        BigDecimal sum = new BigDecimal("0");
        String result = "";
        try {
            for (String value : list) {
                sum = sum.add(new BigDecimal(value));
            }
            result = sum.toString();
        } catch (Exception e) {
            result = "";
        }
        return result;
    }

    /**
     * 引数1 - 引数2の結果を返却する
     * 
     * @param value1 引く元の数値
     * @param value2 引く数値
     * @return 引いた結果の文字列 エラーの場合空文字列
     */
    public String getDifference(String value1, String value2) {
        String result = "";
        try {
            BigDecimal diff = new BigDecimal(value1).subtract(new BigDecimal(value2));
            result = diff.toString();
        } catch (Exception e) {
            result = "";
        }
        return result;
    }

    /**
     * ショップマスタをAP基本コード=apkihoncd、ショップコード=shopcd、有効区分=yukokbnでselectした結果を返却する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param yukokbn 有効区分
     * @return セレクト結果
     */
    public List<S02M046TEntity> selectS02M046T(String apkihoncd, String shopcd, String yukokbn) {
        // 検索条件の設定
        S02M046TEntityExample example = new S02M046TEntityExample();
        jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityExample.Criteria c =
            example.createCriteria();
        c.andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd).andYukokbnEqualTo(yukokbn);
        // ショップマスタの取得
        return s02m046tDao.selectByExample(example);
    }

    /**
     * インボイス施行フラグを返却する
     * 
     * @param shimedydm 締年月
     * @param skyshimeday 請求締日
     * @param invoicshikoymd インボイス施行日付
     * @return インボイス施行フラグ 0：インボイス施行前 1:インボイス施行後
     */
    public String getInvoiceEnforcementFlg(String shimedydm, String skyshimeday,
        String invoicshikoymd) {
        return DateTimeUtil.isLater(invoicshikoymd, shimedydm + skyshimeday)
            ? D1501R00Constants.INVOICE_ENFORCEMENT_FLAG_ENABLED
            : D1501R00Constants.INVOICE_ENFORCEMENT_FLAG_DISABLED;
    }

    /**
     * 消費税率管理マスタを適用開始日<=startDate、適用終了日>=endDate、税区分=taxkbnでselectした結果を返却する
     * 
     * @param startDate 適用開始日
     * @param endDate 適用終了日
     * @param taxkbn 税区分
     * @return セレクト結果
     */
    public List<S02M091TEntity> selectS02M091T(String startDate, String endDate, String taxkbn) {
        // 検索条件の設定
        S02M091TEntityExample example = new S02M091TEntityExample();
        jp.co.aquaclara.accis.common.domain.repository.entity.S02M091TEntityExample.Criteria c =
            example.createCriteria();
        c.andTaxkbnEqualTo(taxkbn).andTekiyostrtddLessThanOrEqualTo(startDate)
            .andTekiyoshryddGreaterThanOrEqualTo(endDate);
        // 消費税率管理マスタの取得
        return s02m091tDao.selectByExample(example);
    }

    /**
     * 請求先マスタをAP基本コード=apkihoncd、ショップコード=shopcd、請求先コード=skysakicdでselectした結果を返却する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param skysakicd 請求先コード
     * @return セレクト結果
     */
    public S02M017TEntity selectS02M017T(String apkihoncd, String shopcd, String skysakicd) {
        // 検索条件の設定
        S02M017TEntityKey key = new S02M017TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setSkysakicd(skysakicd);
        // 請求先マスタの取得
        return s02m017tDao.selectByPrimaryKey(key);
    }

    /**
     * 控えのリストを返却する
     * 
     * @param printDataList 帳票出力データリスト
     * @param index (控)を差し込むindex番号
     * @return 帳票（控え）List
     */
    public ArrayList<List<String>> createHikae(ArrayList<List<String>> printDataList, int index) {
        ArrayList<List<String>> hikaeList = new ArrayList<List<String>>();
        for (List<String> printData : printDataList) {
            List<String> tmp = new ArrayList<>();
            for (int i = 0; i < printData.size(); i++) {
                tmp.add(printData.get(i));
            }
            String hikaeTitle = createHikaeTitle(index, tmp.get(index));
            tmp.set(index, hikaeTitle);
            hikaeList.add(tmp);
        }
        return hikaeList;
    }

    /**
     * 控えの帳票名を返却する
     * 
     * @param index (控)を差し込むindex番号
     * @param originalName 控え元の帳票名
     * @return 帳票名
     */
    private String createHikaeTitle(int index, String originalName) {
        if (D1501R00Constants.TITLE_INDEX_SHIHARAI_MEISAI == index) {
            // 先頭と末尾に存在するダブルクォーテーションを取り除く
            String tmp = originalName.substring(1, originalName.length() - 1);
            return ListCreatorUtil.wrapQuote(tmp + "（控）");
        } else {
            return originalName + "（控）";
        }
    }

    /**
     * 金額のフォーマットを行う（３桁ごとに,を付ける）
     * 
     * @param s フォーマットを行う半角数字の文字列
     * @return フォーマットした値 桁数溢れ等で引数がint型に変換できない場合は引数をそのまま返却する
     */
    public String amountFormatter(String s) {
        NumberFormat formatter = NumberFormat.getNumberInstance();
        String result = "";
        try {
            result = formatter.format(Integer.parseInt(s));
        } catch (Exception e) {
            result = s;
        }
        return result;
    }

    /**
     * 得意先名称を返却する
     * 
     * @param rknplnkeizokukshndd 料金プラン継続更新日
     * @param rknplnsetteidd 料金プラン設定日
     * @param tokubetsuuntkbn 特別単価区分
     * @param rknplnkikan 料金プラン期間
     * @param tokubetsuuntkbnnm 特別単価区分名
     * @param tokuisakinmn1 得意先名称１
     * @param shimeDate 締日
     * @return 得意先名称
     */
    public String getCustomerName(String rknplnkeizokukshndd, String rknplnsetteidd,
        String tokubetsuuntkbn, String rknplnkikan, String tokubetsuuntkbnnm, String tokuisakinmn1,
        String shimeDate) {
        String tokuisakiInfo = "";
        // 得意先情報で使用する日付を取得
        String checkDate =
            StringUtils.isEmpty(rknplnkeizokukshndd) ? rknplnsetteidd : rknplnkeizokukshndd;

        // 特別単価区分 ≠ ”” ＆
        // 特別単価区分 ≧ 4（子育てアクアプラン） ＆
        // 請料金プラン継続更新日（※） ≠ ”” ＆
        // 料金プラン期間 ≠ ”” の場合
        if (!StringUtils.isEmpty(tokubetsuuntkbn)
            && Integer.parseInt(tokubetsuuntkbn) >= Integer
                .parseInt(CommonConstants.TOKUBETSUUNTKBN_BABYAQUA)
            && !StringUtils.isEmpty(checkDate) && !StringUtils.isEmpty(rknplnkikan)) {
            // プランスタート日取得
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            ParsePosition pos = new ParsePosition(0);
            Date planStartDate = formatter.parse(checkDate, pos);

            // 料金プラン期間 ＞ 1 の場合
            if (Integer.parseInt(rknplnkikan) > 1) {
                // プラン終了日取得
                Calendar cal = Calendar.getInstance();
                cal.setTime(planStartDate);
                cal.add(Calendar.MONTH, (Integer.parseInt(rknplnkikan) - 1));
                String planEndDate = formatter.format(cal.getTime());

                // 料金プラン継続更新日が未設定の場合、料金プラン設定日が締日の適用日付に含まれているか判定する
                if (StringUtils.isEmpty(rknplnkeizokukshndd)) {
                    // 変数.締日の年月 ≧ 料金プラン設定日の年月 ＆
                    // 終了日付の年月② ≧ 変数.締日の年月 の場合
                    if (Integer.parseInt(shimeDate.substring(0, 6)) >= Integer
                        .parseInt(checkDate.substring(0, 6))
                        && Integer.parseInt(planEndDate.substring(0, 6)) >= Integer
                            .parseInt(shimeDate.substring(0, 6))) {
                        tokuisakiInfo = getTokuisakiInfoPlannm(tokubetsuuntkbnnm, planEndDate);
                    } else {
                        tokuisakiInfo = "";
                    }
                }
                // 料金プラン継続更新日と料金プラン設定日が設定されている場合、締日が料金プラン継続更新日に含まれているか判定する
                if (!StringUtils.isEmpty(rknplnsetteidd)
                    && !StringUtils.isEmpty(rknplnkeizokukshndd)) {
                    // 変数.締日の年月 ≧ 料金プラン更新年月の年月 の場合
                    if (Integer.parseInt(planEndDate.substring(0, 6)) >= Integer
                        .parseInt(shimeDate.substring(0, 6))) {
                        tokuisakiInfo = getTokuisakiInfoPlannm(tokubetsuuntkbnnm, planEndDate);
                    }
                }
            } else {
                tokuisakiInfo = "";
            }
        } else {
            tokuisakiInfo = "";
        }
        String s = tokuisakinmn1 + "様ご利用明細" + tokuisakiInfo;
        return s;
    }

    /**
     * 
     * 得意先プラン名称の文字列整形
     * 
     * @param tokubetsuuntkbnnm 特別単価区分名称
     * @param planEndDate プラン終了日
     * @return プラン名称+次回更新年月
     */
    private String getTokuisakiInfoPlannm(String tokubetsuuntkbnnm, String planEndDate) {
        return "        " + tokubetsuuntkbnnm + "次回更新年月" + "　" + planEndDate.substring(0, 4) + "年"
            + planEndDate.substring(4, 6) + "月";
    }

    /**
     * 商品名称明細を返却する
     * 
     * @param tanpinMeisaiFlag 単品明細フラグ
     * @param stshnCd セット商品コード
     * @param shnName 商品名
     * @return 商品名称明細
     */
    public String getItemNameDetail(String tanpinMeisaiFlag, String stshnCd, String shnName) {
        String s = "";
        if (CommonConstants.TANPINMEISAIFLG_1.equals(tanpinMeisaiFlag)
            && !StringUtils.isEmpty(stshnCd)) {
            s = "　　" + "（" + ItemUtil.nullToString(shnName) + "）";
        } else {
            s = ItemUtil.nullToString(shnName);
        }
        return s;
    }


    /**
     * 税区分印明細を返却する
     * 
     * @param tanpinMeisaiFlag 単品明細フラグ
     * @param nyuryokuMeisaiFlag 入力明細フラグ
     * @param taxKbn 税区分
     * @param oyaStTaxKbn 構成品の親商品税区分
     * @return 税区分印明細
     */
    public String getTaxTypeDetail(String tanpinMeisaiFlag, String nyuryokuMeisaiFlag,
        String taxKbn, String oyaStTaxKbn) {

        if (CommonConstants.TANPINMEISAIFLG_1.equals(tanpinMeisaiFlag)
            && !CommonConstants.NYURYOKUMEISAIFLG_0.equals(nyuryokuMeisaiFlag)
            && CommonConstants.KBN_TAX_KEIGEN.equals(taxKbn)) {
            return "※";
        }

        if (CommonConstants.NYURYOKUMEISAIFLG_1.equals(nyuryokuMeisaiFlag)
            && CommonConstants.KBN_TAX_KEIGEN.equals(taxKbn)) {
            return "※";
        }

        if (CommonConstants.NYURYOKUMEISAIFLG_0.equals(nyuryokuMeisaiFlag)
            && CommonConstants.KBN_TAX_KOMEISAI.equals(oyaStTaxKbn)
            && CommonConstants.KBN_TAX_KEIGEN.equals(taxKbn)) {
            return "※";
        }
        return "";
    }

    /**
     * 格納用お支払い方法お支払期限2の設定値を返却する
     * 
     * @param kaishuhohokbn 回収方法区分
     * @param sksmkinyukknnmkana 請求先金融機関名称カナ
     * @param sksmkinyukknkzkindnm 請求先金融機関口座種別名称
     * @param sksmksikknnm 決済機関名称
     * @param sksmkinyukknkzno 請求先金融機関口座番号
     * @param sksmkinyukknshitennmkana 請求先金融機関支店名称カナ
     * @return 格納用お支払い方法お支払期限2
     */
    public String getPaymentMethodAndPayDeadline2(String kaishuhohokbn, String sksmkinyukknnmkana,
        String sksmkinyukknkzkindnm, String sksmksikknnm, String sksmkinyukknkzno,
        String sksmkinyukknshitennmkana) {
        switch (kaishuhohokbn) {
            case CommonConstants.KAISHUHOHOKBN_2:
                return sksmksikknnm;
            case CommonConstants.KAISHUHOHOKBN_5:
                return "金融機関:" + whitespacePadding(sksmkinyukknnmkana, 17).substring(0, 17) + "  "
                    + "支店名:" + whitespacePadding(sksmkinyukknshitennmkana, 17).substring(0, 17)
                    + "  " + sksmkinyukknkzkindnm + " " + getAccountNumber(sksmkinyukknkzno);
            default:
                return "";
        }
    }

    /**
     * 指定された文字数より文字列の桁が下回っている場合不足している文字数分を半角スペースで埋めて返却するそれ以外は文字列をそのまま返却する
     * 
     * @param target 対象文字列
     * @param num 半角スペース追加後の文字数
     * @return 空白追加後の対象文字列
     */
    private String whitespacePadding(String target, int num) {
        String res = target;
        if (num > target.length()) {
            for (int i = 0; i < (num - target.length()); i++) {
                res = res + " ";
            }
        }
        return res;
    }

    /**
     * 格納用お支払い方法お支払期限2に設定する口座番号を返却する
     * 
     * @param sksmkinyukknkzno 請求先金融機関口座番号
     * @return 格納用お支払い方法お支払期限2の口座番号
     */
    private String getAccountNumber(String sksmkinyukknkzno) {
        String accountNumber = "";
        if (!sksmkinyukknkzno.isEmpty()) {
            accountNumber = sksmkinyukknkzno;
            if (accountNumber.length() > 3) {
                if (accountNumber.length() > 7) {
                    accountNumber = sksmkinyukknkzno.substring(0, 7);
                }
                accountNumber = accountNumber.substring(0, accountNumber.length() - 4) + "****";
            } else {
                accountNumber = accountNumber.replaceAll("[0-9]", "*");
            }
        }
        return accountNumber;
    }

    /**
     * 格納用お支払い方法お支払期限1の設定値を返却する
     * 
     * @param reportType 帳票区分
     * @param kaishuhohokbn 回収方法区分
     * @param kaishuhohokbnnm 回収方法区分名称
     * @param shrytidydmdd 支払予定年月日
     * @param creditPostingdd クレジット計上日
     * @param acikeiyuksikbn ACI経由決済区分
     * @return 格納用お支払い方法お支払期限1
     */
    public String getPaymentMethodAndPayDeadline1(String reportType, String kaishuhohokbn,
        String kaishuhohokbnnm, String shrytidydmdd, String creditPostingdd,
        String acikeiyuksikbn) {
        // 回収方法区分：クレジットカードの場合
        if (CommonConstants.KAISHUHOHOKBN_2.equals(kaishuhohokbn)) {
            // 請求書の場合
            if (D1501R00Constants.TYOHYO_KBN_SKYSHO.equals(reportType)) {
                return "お支払方法：" + kaishuhohokbnnm;
                // ご利用明細の場合
            } else if (D1501R00Constants.TYOHYO_KBN_GORIYOMEISAI_HIKIOTOSHI_YOTEI_ARI
                .equals(reportType)
                || D1501R00Constants.TYOHYO_KBN_GORIYOMEISAI_HIKIOTOSHI_YOTEI_NASHI
                    .equals(reportType)) {
                // 請求書出力データEntity.ＡＣＩ経由決済区分 ＝ 3（クレジット自動） or
                // 請求書出力データEntity.ＡＣＩ経由決済区分 ＝ 4（クレジット手動） の場合
                if (CommonConstants.ACI_KEIYU_KSIKBN_3.equals(acikeiyuksikbn)
                    || CommonConstants.ACI_KEIYU_KSIKBN_4.equals(acikeiyuksikbn)) {
                    if (StringUtils.isEmpty(shrytidydmdd)) {
                        // 支払予定年月日が空の場合に文字列操作が行えないため用意
                        return "お支払方法：" + kaishuhohokbnnm + "  " + "クレジットカード会社" + " " + "明細計上日："
                            + "" + "年" + "" + "月" + creditPostingdd + "日";
                    } else {
                        return "お支払方法：" + kaishuhohokbnnm + "  " + "クレジットカード会社" + " " + "明細計上日："
                            + shrytidydmdd.substring(0, 4) + "年" + shrytidydmdd.substring(4, 6)
                            + "月" + creditPostingdd + "日";
                    }
                } else {
                    return "お支払方法：" + kaishuhohokbnnm;
                }
            }
            // 回収方法区分：口座振替の場合
        } else if (CommonConstants.KAISHUHOHOKBN_5.equals(kaishuhohokbn)) {
            if (StringUtils.isEmpty(shrytidydmdd)) {
                return "お支払方法：" + kaishuhohokbnnm + "  " + "お引落日：" + "" + "年" + "" + "月" + "" + "日";
            } else {
                return "お支払方法：" + kaishuhohokbnnm + "  " + "お引落日：" + shrytidydmdd.substring(0, 4)
                    + "年" + shrytidydmdd.substring(4, 6) + "月" + shrytidydmdd.substring(6, 8) + "日";
            }
        } else {
            if (StringUtils.isEmpty(shrytidydmdd)) {
                return "お支払方法：" + kaishuhohokbnnm + "  " + "お支払期日：" + "" + "年" + "" + "月" + ""
                    + "日";
            } else {
                return "お支払方法：" + kaishuhohokbnnm + "  " + "お支払期日：" + shrytidydmdd.substring(0, 4)
                    + "年" + shrytidydmdd.substring(4, 6) + "月" + shrytidydmdd.substring(6, 8) + "日";
            }
        }
        return "";
    }

    /**
     * 請求書／ご利用明細書の印字内容をListで返却する
     * 
     * @param reportType 帳票区分
     * @param printYearMonth 印字年月
     * @param kaishuhohokbn 回収方法区分
     * @return 印字内容List
     */
    public List<String> getPrintContents(String reportType, String printYearMonth,
        String kaishuhohokbn) {
        if (D1501R00Constants.TYOHYO_KBN_SKYSHO.equals(reportType)) {
            return getInvoicePrintContents(kaishuhohokbn, printYearMonth);
        } else if (D1501R00Constants.TYOHYO_KBN_GORIYOMEISAI_HIKIOTOSHI_YOTEI_ARI.equals(reportType)
            || D1501R00Constants.TYOHYO_KBN_GORIYOMEISAI_HIKIOTOSHI_YOTEI_NASHI.equals(reportType)
            || D1501R00Constants.TYOHYO_KBN_SKYSHO_CSV.equals(reportType)) {
            return getUsageDetailsPrintContents(kaishuhohokbn, printYearMonth);
        } else {
            throw new IllegalArgumentException("未定義の帳票区分が引数に指定されています");
        }
    }


    /**
     * ご利用明細書の印字内容をListで返却する
     * 
     * @param printYearMonth 印字年月
     * @param kaishuhohokbn 回収方法区分
     * @return 印字内容List
     */
    private List<String> getUsageDetailsPrintContents(String kaishuhohokbn, String printYearMonth) {
        List<String> list = new ArrayList<>();
        list.add(ListCreatorUtil.wrapQuote("いつもアクアクララをご利用いただきましてありがとうございます。"));
        switch (kaishuhohokbn) {
            case CommonConstants.KAISHUHOHOKBN_2:
                list.add(ListCreatorUtil.wrapQuote(printYearMonth + "のご利用額を以下の通りご案内申し上げます。"));
                list.add(ListCreatorUtil.wrapQuote("ご指定のクレジットカードにて精算させていただきます。"));
                list.add(ListCreatorUtil.wrapQuote("お引落日は、ご利用のクレジットカード会社様のご利用明細書にてご確認下さい。"));
                break;
            case CommonConstants.KAISHUHOHOKBN_5:
                list.add(ListCreatorUtil.wrapQuote(printYearMonth + "のご利用額とお引落日を以下の通りご案内申し上げます。"));
                list.add(ListCreatorUtil.wrapQuote("「お引落日」と「口座残高」のご確認をお願い申し上げます。"));
                list.add(ListCreatorUtil.wrapQuote("土日祝日等で銀行休業日の場合は、翌営業日がお引落日になります。"));
                break;
            default:
                list.add(ListCreatorUtil.wrapQuote(printYearMonth + "分のご利用明細をご案内申し上げます。"));
                list.add("");
                list.add("");
                break;
        }
        return list;
    }

    /**
     * 請求書の印字内容をListで返却する
     * 
     * @param printYearMonth 印字年月
     * @param kaishuhohokbn 回収方法区分
     * @return 印字内容List
     */
    private List<String> getInvoicePrintContents(String kaishuhohokbn, String printYearMonth) {
        List<String> list = new ArrayList<>();
        list.add(ListCreatorUtil.wrapQuote("いつもアクアクララをご利用いただきましてありがとうございます。"));
        switch (kaishuhohokbn) {
            case CommonConstants.KAISHUHOHOKBN_2:
                list.add(ListCreatorUtil.wrapQuote(printYearMonth + "のご利用額を以下の通りご請求申し上げます。"));
                list.add(ListCreatorUtil.wrapQuote("ご指定のクレジットカードにて精算させていただきます。"));
                list.add(ListCreatorUtil.wrapQuote("お引落日は、ご利用のクレジットカード会社様のご利用明細書にてご確認下さい。"));
                break;
            case CommonConstants.KAISHUHOHOKBN_5:
                list.add(ListCreatorUtil.wrapQuote(printYearMonth + "のご利用額とお引落日を以下の通りご請求申し上げます。"));
                list.add(ListCreatorUtil.wrapQuote("「お引落日」と「口座残高」のご確認をお願い申し上げます。"));
                list.add(ListCreatorUtil.wrapQuote("土日祝日等で銀行休業日の場合は、翌営業日がお引落日になります。"));
                break;
            default:
                list.add(ListCreatorUtil.wrapQuote(printYearMonth + "分のご利用額を以下の通りご請求申し上げます。"));
                list.add("");
                list.add("");
                break;
        }
        return list;
    }

    /**
     * 印字締年月を取得する
     * 
     * @param shimedydm yyyyMMの文字列
     * @return yyyy年MM月の文字列
     */
    public String getPrintYearMonth(String shimedydm) {

        // 締年月の「月」の値が10より小さい場合は、0を取り除く
        if (Integer.parseInt(shimedydm.substring(4, 6)) < 10) {
            return shimedydm.substring(0, 4) + "年" + shimedydm.substring(4, 6).replace("0", " ")
                + "月";
        } else {
            return shimedydm.substring(0, 4) + "年" + shimedydm.substring(4, 6) + "月";
        }
    }
}
