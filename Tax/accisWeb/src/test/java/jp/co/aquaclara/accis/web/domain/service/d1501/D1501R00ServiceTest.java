package jp.co.aquaclara.accis.web.domain.service.d1501;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class D1501R00ServiceTest extends D1501R00Service {

    /**
     * getSumの単体試験用メソッド
     * 
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @Test
    void getSumTest01() throws NoSuchMethodException, SecurityException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException {
        String sum = getSum(new ArrayList<String>(Arrays.asList("1", "2", "3")));
        assertEquals("6", sum);
        sum = getSum(new ArrayList<String>(Arrays.asList("1", "-2")));
        assertEquals("-1", sum);
        sum = getSum(new ArrayList<String>(Arrays.asList("-10", "11")));
        assertEquals("1", sum);
        sum = getSum(new ArrayList<String>(Arrays.asList("-10", "-11")));
        assertEquals("-21", sum);
        sum = getSum(new ArrayList<String>(Arrays.asList("a")));
        assertEquals("", sum);
    }

    /**
     * getDifferenceの単体試験用メソッド
     * 
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @Test
    void getDifferenceTest01() throws NoSuchMethodException, SecurityException,
        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String diff = getDifference("100", "50");
        assertEquals("50", diff);
        diff = getDifference("-10", "10");
        assertEquals("-20", diff);
        diff = getDifference("-10", "-10");
        assertEquals("0", diff);
        diff = getDifference("a", "10");
        assertEquals("", diff);
        diff = getDifference("10", "a");
        assertEquals("", diff);
    }

    /**
     * getInvoiceEnforcementFlgの単体試験用メソッド
     * 
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @Test
    void getInvoiceEnforcementFlgTest01() throws NoSuchMethodException, SecurityException,
        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // 締年月+請求締日 == インボイス施行日付
        assertEquals("1", getInvoiceEnforcementFlg("202010", "10", "20201010"));
        // 締年月+請求締日 > インボイス施行日付
        assertEquals("1", getInvoiceEnforcementFlg("202010", "31", "20201010"));
        // 締年月+請求締日 < インボイス施行日付
        assertEquals("0", getInvoiceEnforcementFlg("202010", "05", "20201010"));
    }

    /**
     * getInvoiceEnforcementFlgの単体試験用メソッド
     * 
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @Test
    void selectS02M046Test01() throws NoSuchMethodException, SecurityException,
        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        assertEquals(false, selectS02M046T("002", "036", "1").isEmpty());
    }

    /**
     * selectS02M091の単体試験用メソッド
     * 
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @Test
    void selectS02M091Test01() throws NoSuchMethodException, SecurityException,
        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        assertEquals(new BigDecimal("8"),
            selectS02M091T("20140401", "20190930", "1").get(0).getShohitax());
        assertEquals(new BigDecimal("8"),
            selectS02M091T("20140401", "20140401", "1").get(0).getShohitax());
        assertEquals(new BigDecimal("8"),
            selectS02M091T("20190930", "20190930", "1").get(0).getShohitax());
    }

    /**
     * selectS02M017の単体試験用メソッド
     * 
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @Test
    void selectS02M017Test01() throws NoSuchMethodException, SecurityException,
        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        assertEquals("002", selectS02M017T("002", "036", "000027").getApkihoncd());
    }

    /**
     * createHikaeの単体試験用メソッド
     * 
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @Test
    void createHikaeTest01() throws NoSuchMethodException, SecurityException,
        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        ArrayList<List<String>> lists = new ArrayList<List<String>>();
        List<String> list = new ArrayList<String>();
        list.add("");
        list.add("");
        list.add("");
        lists.add(list);
        lists.add(list);
        ArrayList<List<String>> result = createHikae(lists, 1);
        assertEquals("（控）", result.get(0).get(1));
        assertEquals("（控）", result.get(1).get(1));
    }

    /**
     * amountFormatterの単体試験用メソッド
     * 
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @Test
    void amountFormatterTest01() throws NoSuchMethodException, SecurityException,
        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        assertEquals("100000000000000", amountFormatter("100000000000000"));
        assertEquals("1,000,000,000", amountFormatter("1000000000"));
        assertEquals("1,000", amountFormatter("1000"));
        assertEquals("100", amountFormatter("100"));
        assertEquals("", amountFormatter(""));
    }

    /**
     * getCustomerNameの単体試験用メソッド
     * 
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @Test
    void getCustomerNamelTest01() throws NoSuchMethodException, SecurityException,
        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // 以外
        String tokubetsuuntkbn = "";
        String tokubetsuuntkbnnm = "テスト";
        String tokuisakinmn1 = "得意先名称";
        assertEquals("得意先名称様ご利用明細", getCustomerName("", "", tokubetsuuntkbn, "", tokubetsuuntkbnnm,
            tokuisakinmn1, "20210630"));
        // 1. 請求書出力データEntity.特別単価区分 ≠ ”” ＆
        // 請求書出力データEntity.特別単価区分 ≧ 4（子育てアクアプラン） ＆
        // 請求書出力データEntity.料金プラン継続更新日（※） ≠ ”” ＆
        // 請求書出力データEntity.料金プラン期間 ≠ ”” の場合
        // 請求書出力データEntity.料金プラン期間 ＞ 1でない
        tokubetsuuntkbn = "4";
        String rknplnkeizokukshndd = "20210630";
        String rknplnsetteidd = "20210530";
        String rknplnkikan = "1";
        assertEquals("得意先名称様ご利用明細", getCustomerName(rknplnkeizokukshndd, rknplnsetteidd,
            tokubetsuuntkbn, rknplnkikan, tokubetsuuntkbnnm, tokuisakinmn1, "20210630"));
        // 1. 請求書出力データEntity.特別単価区分 ≠ ”” ＆
        // 請求書出力データEntity.特別単価区分 ≧ 4（子育てアクアプラン） ＆
        // 請求書出力データEntity.料金プラン継続更新日（※） ≠ ”” ＆
        // 請求書出力データEntity.料金プラン期間 ≠ ”” の場合
        // 請求書出力データEntity.料金プラン期間 ＞ 1
        // 変数.締日の年月 ≧ 請求書出力データEntity.料金プラン継続更新日（※）の年月 ＆
        // 終了日付の年月② = 変数.締日の年月 の場合
        tokubetsuuntkbn = "4";
        rknplnkeizokukshndd = "20210630";
        rknplnsetteidd = "20210530";
        rknplnkikan = "2";
        assertEquals("得意先名称様ご利用明細        テスト適用中　2021年06月～2021年07月",
            getCustomerName(rknplnkeizokukshndd, rknplnsetteidd, tokubetsuuntkbn, rknplnkikan,
                tokubetsuuntkbnnm, tokuisakinmn1, "20210730"));
        // 1. 請求書出力データEntity.特別単価区分 ≠ ”” ＆
        // 請求書出力データEntity.特別単価区分 ≧ 4（子育てアクアプラン） ＆
        // 請求書出力データEntity.料金プラン継続更新日（※） ≠ ”” ＆
        // 請求書出力データEntity.料金プラン期間 ≠ ”” の場合
        // 請求書出力データEntity.料金プラン期間 ＞ 1
        // 変数.締日の年月 ≧ 請求書出力データEntity.料金プラン継続更新日（※）の年月 ＆
        // 終了日付の年月② > 変数.締日の年月 の場合
        tokubetsuuntkbn = "4";
        rknplnkeizokukshndd = "20210630";
        rknplnsetteidd = "20210530";
        rknplnkikan = "3";
        assertEquals("得意先名称様ご利用明細        テスト適用中　2021年06月～2021年08月",
            getCustomerName(rknplnkeizokukshndd, rknplnsetteidd, tokubetsuuntkbn, rknplnkikan,
                tokubetsuuntkbnnm, tokuisakinmn1, "20210630"));
        // 1. 請求書出力データEntity.特別単価区分 ≠ ”” ＆
        // 請求書出力データEntity.特別単価区分 ≧ 4（子育てアクアプラン） ＆
        // 請求書出力データEntity.料金プラン継続更新日（※） ≠ ”” ＆
        // 請求書出力データEntity.料金プラン期間 ≠ ”” の場合
        // 請求書出力データEntity.料金プラン期間 ＞ 1
        // 変数.締日の年月 ≧ 請求書出力データEntity.料金プラン継続更新日（※）の年月 ＆
        // 終了日付の年月② > 変数.締日の年月 の場合
        // 更新継続日空
        tokubetsuuntkbn = "4";
        rknplnkeizokukshndd = "";
        rknplnsetteidd = "20210530";
        rknplnkikan = "3";
        assertEquals("得意先名称様ご利用明細        テスト適用中　2021年05月～2021年07月",
            getCustomerName(rknplnkeizokukshndd, rknplnsetteidd, tokubetsuuntkbn, rknplnkikan,
                tokubetsuuntkbnnm, tokuisakinmn1, "20210630"));
        // 1. 請求書出力データEntity.特別単価区分 ≠ ”” ＆
        // 請求書出力データEntity.特別単価区分 ≧ 4（子育てアクアプラン） ＆
        // 請求書出力データEntity.料金プラン継続更新日（※） ≠ ”” ＆
        // 請求書出力データEntity.料金プラン期間 ≠ ”” の場合
        // 請求書出力データEntity.料金プラン期間 ＞ 1
        // 変数.締日の年月 < 請求書出力データEntity.料金プラン継続更新日（※）の年月 ＆
        tokubetsuuntkbn = "4";
        rknplnkeizokukshndd = "20210630";
        rknplnsetteidd = "20210530";
        rknplnkikan = "2";
        assertEquals("得意先名称様ご利用明細", getCustomerName(rknplnkeizokukshndd, rknplnsetteidd,
            tokubetsuuntkbn, rknplnkikan, tokubetsuuntkbnnm, tokuisakinmn1, "20210430"));
        // 1. 請求書出力データEntity.特別単価区分 ≠ ”” ＆
        // 請求書出力データEntity.特別単価区分 ≧ 4（子育てアクアプラン） ＆
        // 請求書出力データEntity.料金プラン継続更新日（※） ≠ ”” ＆
        // 請求書出力データEntity.料金プラン期間 ≠ ”” の場合
        // 請求書出力データEntity.料金プラン期間 ＞ 1
        // 変数.締日の年月 < 請求書出力データEntity.料金プラン継続更新日（※）の年月 ＆
        // 終了日付の年月② < 変数.締日の年月 の場合
        tokubetsuuntkbn = "4";
        rknplnkeizokukshndd = "20210430";
        rknplnsetteidd = "20210530";
        rknplnkikan = "2";
        assertEquals("得意先名称様ご利用明細", getCustomerName(rknplnkeizokukshndd, rknplnsetteidd,
            tokubetsuuntkbn, rknplnkikan, tokubetsuuntkbnnm, tokuisakinmn1, "20220530"));
    }

    /**
     * getTaxTypeの単体試験用メソッド
     * 
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @Test
    void getTaxTypeDetailTest01() throws NoSuchMethodException, SecurityException,
        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // 1. 請求書出力データEntity.単品明細フラグ ＝ 1（単品明細行） ＆請求書出力データEntity.入力明細フラグ ≠ 0（セット商品自動展開行）
        // ＆請求書出力データEntity.税区分 ＝ 4（軽減税率） の場合
        assertEquals("※", getTaxTypeDetail("1", "2", "4", ""));
        // 2. 請求書出力データEntity.入力明細フラグ ＝ 1（入力行） ＆請求書出力データEntity.税区分 ＝ 4（軽減税率） の場合
        assertEquals("※", getTaxTypeDetail("1", "1", "4", ""));
        // 3. 請求書出力データEntity.入力明細フラグ ＝ 0（セット商品自動展開行） ＆請求書出力データEntity.構成品の親商品税区分 ＝ 5（子明細商品）
        // ＆請求書出力データEntity.税区分 ＝ 4（軽減税率） の場合
        assertEquals("※", getTaxTypeDetail("1", "0", "4", "5"));
        // それ以外
        assertEquals("", getTaxTypeDetail("0", "0", "0", "5"));
    }

    /**
     * getItemNameDetailForSalesDepositDetailの単体試験用メソッド
     * 
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @Test
    void getItemNameDetailTest01() throws NoSuchMethodException, SecurityException,
        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // 請求書出力データEntity.単品明細フラグ ＝ 1（単品明細行） ＆請求書出力データEntity.セット商品コード ≠ ”” の場合
        assertEquals("　　（商品名称）", getItemNameDetail("1", "セット商品コード", "商品名称"));
        // 請求書出力データEntity.単品明細フラグ !＝ 1（単品明細行） ＆請求書出力データEntity.セット商品コード ≠ ”” の場合
        assertEquals("商品名称", getItemNameDetail("0", "セット商品コード", "商品名称"));
        // 請求書出力データEntity.単品明細フラグ ＝ 1（単品明細行） ＆請求書出力データEntity.セット商品コード = ”” の場合
        assertEquals("商品名称", getItemNameDetail("1", "", "商品名称"));
    }

    /**
     * getPaymentMethodAndPayDeadline2の単体試験用メソッド
     * 
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @Test
    void getPaymentMethodAndPayDeadline2Test01() throws NoSuchMethodException, SecurityException,
        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // 回収方法区分：クレジットカード
        assertEquals("決済機関名称", getPaymentMethodAndPayDeadline2("2", "", "", "決済機関名称", "", ""));
        // 回収方法区分：他
        assertEquals("", getPaymentMethodAndPayDeadline2("1", "", "", "", "", ""));
        // 回収方法区分：口座振替 口座番号なし
        assertEquals("金融機関:請求先金融機関名称カナ123456  支店名:請求先金融機関支店名称カナ1234  請求先金融機関口座種別名称 ",
            getPaymentMethodAndPayDeadline2("5", "請求先金融機関名称カナ1234567890", "請求先金融機関口座種別名称", "決済機関名称",
                "", "請求先金融機関支店名称カナ1234567890"));
        // 回収方法区分：口座振替 口座番号3桁
        assertEquals("金融機関:請求先金融機関名称カナ123456  支店名:請求先金融機関支店名称カナ1234  請求先金融機関口座種別名称 ***",
            getPaymentMethodAndPayDeadline2("5", "請求先金融機関名称カナ1234567890", "請求先金融機関口座種別名称", "決済機関名称",
                "123", "請求先金融機関支店名称カナ1234567890"));
        // 回収方法区分：口座振替 口座番号4桁
        assertEquals("金融機関:請求先金融機関名称カナ123456  支店名:請求先金融機関支店名称カナ1234  請求先金融機関口座種別名称 ****",
            getPaymentMethodAndPayDeadline2("5", "請求先金融機関名称カナ1234567890", "請求先金融機関口座種別名称", "決済機関名称",
                "1234", "請求先金融機関支店名称カナ1234567890"));
        // 回収方法区分：口座振替 口座番号5桁
        assertEquals("金融機関:請求先金融機関名称カナ123456  支店名:請求先金融機関支店名称カナ1234  請求先金融機関口座種別名称 1****",
            getPaymentMethodAndPayDeadline2("5", "請求先金融機関名称カナ1234567890", "請求先金融機関口座種別名称", "決済機関名称",
                "12345", "請求先金融機関支店名称カナ1234567890"));
        // 回収方法区分：口座振替 口座番号7桁
        assertEquals("金融機関:請求先金融機関名称カナ123456  支店名:請求先金融機関支店名称カナ1234  請求先金融機関口座種別名称 123****",
            getPaymentMethodAndPayDeadline2("5", "請求先金融機関名称カナ1234567890", "請求先金融機関口座種別名称", "決済機関名称",
                "1234567", "請求先金融機関支店名称カナ1234567890"));
        // 回収方法区分：口座振替 口座番号8桁
        assertEquals("金融機関:請求先金融機関名称カナ123456  支店名:請求先金融機関支店名称カナ1234  請求先金融機関口座種別名称 123****",
            getPaymentMethodAndPayDeadline2("5", "請求先金融機関名称カナ1234567890", "請求先金融機関口座種別名称", "決済機関名称",
                "12345678", "請求先金融機関支店名称カナ1234567890"));
        // 回収方法区分：口座振替 口座番号8桁
        assertEquals("金融機関:                   支店名:                   請求先金融機関口座種別名称 123****",
            getPaymentMethodAndPayDeadline2("5", "", "請求先金融機関口座種別名称", "決済機関名称", "12345678", ""));
        // 回収方法区分：口座振替 口座番号8桁
        assertEquals("金融機関:123                支店名:123                請求先金融機関口座種別名称 123****",
            getPaymentMethodAndPayDeadline2("5", "123", "請求先金融機関口座種別名称", "決済機関名称", "12345678",
                "123"));
    }

    /**
     * getPaymentMethodAndPayDeadline1の単体試験用メソッド
     * 
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @Test
    void getPaymentMethodAndPayDeadline1Test01() throws NoSuchMethodException, SecurityException,
        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // 回収方法区分：クレジットカード、帳票区分：請求書
        assertEquals("お支払方法：クレジット", getPaymentMethodAndPayDeadline1("1", "2", "クレジット", "", "", ""));
        // 回収方法区分：クレジットカード、帳票区分：ご利用明細引き落としあり ACI経由決済区分：クレジット自動
        assertEquals("お支払方法：クレジット  クレジットカード会社 明細計上日：2022年06月30日",
            getPaymentMethodAndPayDeadline1("2", "2", "クレジット", "20220601", "30", "3"));
        // 回収方法区分：クレジットカード、帳票区分：ご利用明細引き落としあり ACI経由決済区分：クレジット手動
        assertEquals("お支払方法：クレジット  クレジットカード会社 明細計上日：2022年06月30日",
            getPaymentMethodAndPayDeadline1("2", "2", "クレジット", "20220601", "30", "4"));
        // 回収方法区分：クレジットカード、帳票区分：ご利用明細引き落としあり ACI経由決済区分：クレジット自動,クレジット手動以外
        assertEquals("お支払方法：クレジット",
            getPaymentMethodAndPayDeadline1("2", "2", "クレジット", "20220601", "30", "0"));
        // 回収方法区分：クレジットカード、帳票区分：ご利用明細引き落としなし ACI経由決済区分：クレジット自動
        assertEquals("お支払方法：クレジット  クレジットカード会社 明細計上日：2022年06月30日",
            getPaymentMethodAndPayDeadline1("3", "2", "クレジット", "20220601", "30", "3"));
        // 回収方法区分：クレジットカード、帳票区分：ご利用明細引き落としなし ACI経由決済区分：クレジット手動
        assertEquals("お支払方法：クレジット  クレジットカード会社 明細計上日：2022年06月30日",
            getPaymentMethodAndPayDeadline1("3", "2", "クレジット", "20220601", "30", "4"));
        // 回収方法区分：クレジットカード、帳票区分：ご利用明細引き落としなし ACI経由決済区分：クレジット自動,クレジット手動以外
        assertEquals("お支払方法：クレジット",
            getPaymentMethodAndPayDeadline1("3", "2", "クレジット", "20220601", "30", "0"));
        // 回収方法区分：口座振替
        assertEquals("お支払方法：口座振替  お引落日：2022年06月01日",
            getPaymentMethodAndPayDeadline1("1", "5", "口座振替", "20220601", "30", ""));
        // 回収方法区分：口座振替
        assertEquals("お支払方法：テスト  お支払期日：2022年06月01日",
            getPaymentMethodAndPayDeadline1("1", "1", "テスト", "20220601", "30", ""));
    }
}
