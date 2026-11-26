package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntityKey;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 請求書出力データ抽出用結果Entity.
 * 
 * @author accisdev047
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class S07T001TD1501R30ListResultEntity extends S07T001TEntityKey implements Serializable {

    /**
     * 消費税レコードフラグ:1(消費税)
     */
    private static final String TAX_FLAG_TAX = "1";

    /**
     * 帳票種別:ご利用明細引落なし
     */
    private static final String GORIYOMEISAI_HIKIOTOSHI_NASHI = "3";
    /**
     * 帳票種別:ご利用明細引落あり
     */
    private static final String GORIYOMEISAI_HIKIOTOSHI_ARI = "2";

    /**
     * serialVersionUID
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Database Column Remarks:売上入金区分
     *
     */
    private String urinykkbn;
    /**
     * Database Column Remarks:伝票番号
     *
     */
    private String uridnpyno;
    /**
     * Database Column Remarks:得意先コード
     *
     */
    private String tokuisakicd;
    /**
     * Database Column Remarks:売上日
     *
     */
    private String uridd;
    /**
     * Database Column Remarks:数量
     *
     */
    private String suryo;
    /**
     * Database Column Remarks:単位
     *
     */
    private String tani;
    /**
     * Database Column Remarks:単価
     *
     */
    private String unt;
    /**
     * Database Column Remarks:金額
     *
     */
    private String kin;
    /**
     * Database Column Remarks:単価消費税金額
     *
     */
    private String untshohitaxkin;
    /**
     * Database Column Remarks:消費税金額
     *
     */
    private String shohitaxkin;
    /**
     * Database Column Remarks:商品コード
     *
     */
    private String shncd;
    /**
     * Database Column Remarks:商品名
     *
     */
    private String shnnm;
    /**
     * Database Column Remarks:単品明細フラグ
     *
     */
    private String tanpinmeisaiflg;
    /**
     * Database Column Remarks:明細備考
     *
     */
    private String meisaibiko;
    /**
     * Database Column Remarks:セット商品コード
     *
     */
    private String stshncd;
    /**
     * Database Column Remarks:税区分
     *
     */
    private String taxkbn;
    /**
     * Database Column Remarks:構成品の親商品コード
     *
     */
    private String oyastshncd;
    /**
     * Database Column Remarks:構成品の親商品税区分
     *
     */
    private String oyasttaxkbn;
    /**
     * Database Column Remarks:入力明細ﾌﾗｸﾞ
     *
     */
    private String nyuryokumeisaiflg;
    /**
     * Database Column Remarks:請求先コード
     *
     */
    private String skysakicd;
    /**
     * Database Column Remarks:回収方法区分
     *
     */
    private String kaishuhohokbn;
    /**
     * Database Column Remarks:回収方法区分名
     *
     */
    private String kaishuhohokbnnm;
    /**
     * Database Column Remarks:支払予定年月日
     *
     */
    private String shrytidydmdd;
    /**
     * Database Column Remarks:今回ご利用金額
     *
     */
    private String konkaigoriyokin;
    /**
     * Database Column Remarks:前回繰越金額
     *
     */
    private String znkkurikoshikin;
    /**
     * Database Column Remarks:今回税抜売上金額
     *
     */
    private String konkaitaxnukiurikin;
    /**
     * Database Column Remarks:今回消費税金額
     *
     */
    private String konkaishohitaxkin;
    /**
     * Database Column Remarks:今回入金金額
     *
     */
    private String konkainyknkin;
    /**
     * Database Column Remarks:繰越金額
     *
     */
    private String kurikoshikin;
    /**
     * Database Column Remarks:請求区分
     *
     */
    private String skykbn;
    /**
     * Database Column Remarks:郵便番号
     *
     */
    private String yubinno;
    /**
     * Database Column Remarks:住所１
     *
     */
    private String adrsn1;
    /**
     * Database Column Remarks:住所２
     *
     */
    private String adrsn2;
    /**
     * Database Column Remarks:住所３
     *
     */
    private String adrsn3;
    /**
     * Database Column Remarks:請求先名称１
     *
     */
    private String skysakinmn1;
    /**
     * Database Column Remarks:請求先名称２
     *
     */
    private String skysakinmn2;
    /**
     * Database Column Remarks:請求敬称区分
     *
     */
    private String skysakikeishokbn;
    /**
     * Database Column Remarks:請求先敬称区分名称
     *
     */
    private String skysakikeishokbnnm;
    /**
     * Database Column Remarks:印刷備考
     *
     */
    private String prntbiko;
    /**
     * Database Column Remarks:請求先金融機関名称カナ
     *
     */
    private String sksmkinyukknnmkana;
    /**
     * Database Column Remarks:請求先金融機関支店名称カナ
     *
     */
    private String sksmkinyukknshitennmkana;
    /**
     * Database Column Remarks:請求先金融機関口座種別
     *
     */
    private String sksmkinyukknkzkind;
    /**
     * Database Column Remarks:請求先金融機関口座種別名称
     *
     */
    private String sksmkinyukknkzkindnm;
    /**
     * Database Column Remarks:請求先金融機関口座番号
     *
     */
    private String sksmkinyukknkzno;
    /**
     * Database Column Remarks:決済機関名称
     *
     */
    private String sksmksikknnm;
    /**
     * Database Column Remarks:明細印刷区分
     *
     */
    private String meisaiprntkbn;
    /**
     * Database Column Remarks:改ページ用得意先コード
     *
     */
    private String breaktokuisakicd;
    /**
     * Database Column Remarks:郵便番号バーコード
     *
     */
    private String yubinnobakodo;
    /**
     * Database Column Remarks:請求方法区分
     *
     */
    private String skyhohokbn;
    /**
     * Database Column Remarks:金融機関口座種別
     *
     */
    private String kinyukknkzkind;
    /**
     * Database Column Remarks:金融機関口座種別名称
     *
     */
    private String kinyukknkzkindnm;
    /**
     * Database Column Remarks:金融機関口座番号
     *
     */
    private String kinyukknkzno;
    /**
     * Database Column Remarks:金融機関名称
     *
     */
    private String kinyukknnm;
    /**
     * Database Column Remarks:金融機関支店名称
     *
     */
    private String kinyukknshitennm;
    /**
     * Database Column Remarks:金融機関口座名義
     *
     */
    private String kinyukknkzmeigi;
    /**
     * Database Column Remarks:金融機関２口座種別
     *
     */
    private String kinyukknn2kzkind;
    /**
     * Database Column Remarks:金融機関２口座種別名称
     *
     */
    private String kinyukkn2kzkindnm;
    /**
     * Database Column Remarks:金融機関２口座番号
     *
     */
    private String kinyukknn2kzno;
    /**
     * Database Column Remarks:金融機関２名称
     *
     */
    private String kinyukknn2nm;
    /**
     * Database Column Remarks:金融機関２支店名称
     *
     */
    private String kinyukknn2shitennm;
    /**
     * Database Column Remarks:金融機関２口座名義
     *
     */
    private String kinyukknn2kzmeigi;
    /**
     * Database Column Remarks:差出人イメージ
     *
     */
    private String sshdshhitoimage;
    /**
     * Database Column Remarks:伝票お知らせ内容１
     *
     */
    private String dnpyoshirasenaiyon1;
    /**
     * Database Column Remarks:伝票お知らせ内容２
     *
     */
    private String dnpyoshirasenaiyon2;
    /**
     * Database Column Remarks:伝票お知らせ内容３
     *
     */
    private String dnpyoshirasenaiyon3;
    /**
     * Database Column Remarks:伝票お知らせ内容４
     *
     */
    private String dnpyoshirasenaiyon4;
    /**
     * Database Column Remarks:伝票お知らせ内容５
     *
     */
    private String dnpyoshirasenaiyon5;
    /**
     * Database Column Remarks:伝票お知らせ内容６
     *
     */
    private String dnpyoshirasenaiyon6;
    /**
     * Database Column Remarks:税計算区分
     *
     */
    private String taxkeisankbn;
    /**
     * Database Column Remarks:滞留金額
     *
     */
    private String tairyukin;
    /**
     * Database Column Remarks:滞留名（滞留金額、請求方法区分）
     *
     */
    private String tairyukinmei;
    /**
     * Database Column Remarks:得意先名称１
     *
     */
    private String tokuisakinmn1;
    /**
     * Database Column Remarks:請求書滞留コメント表示区分、文字設定値
     *
     */
    private String osirase2;
    /**
     * Database Column Remarks:決済金額
     *
     */
    private String ksikin;
    /**
     * Database Column Remarks:料金プラン設定日
     *
     */
    private String rknplnsetteidd;
    /**
     * Database Column Remarks:料金プラン継続更新日
     *
     */
    private String rknplnkeizokukshndd;
    /**
     * Database Column Remarks:特別単価区分
     *
     */
    private String tokubetsuuntkbn;
    /**
     * Database Column Remarks:文字設定値（特別単価区分）
     *
     */
    private String tokubetsuuntkbnnm;
    /**
     * Database Column Remarks:数値設定値（料金プラン期間）
     *
     */
    private String rknplnkikan;
    /**
     * Database Column Remarks:プランコード
     *
     */
    private String plncd;
    /**
     * Database Column Remarks:決済期間（ACI経由決済区分
     *
     */
    private String acikeiyuksikbn;
    /**
     * Database Column Remarks:請求残高（税計算区分
     *
     */
    private String taxkeisankbn1;
    /**
     * Database Column Remarks:売上（消費税レコードフラグ（消費税REC判定
     *
     */
    private String taxrecflg;
    /**
     * Database Column Remarks:利用明細番号
     *
     */
    private String riyomeisaishono;
    /**
     * Database Column Remarks:ポイント制度参加有無
     *
     */
    private String pointsystemjoinkbn;
    /**
     * Database Column Remarks:ポイント管理有無
     *
     */
    private String pointkanriumu;
    /**
     * Database Column Remarks:獲得予定ポイント
     *
     */
    private String kakutokuyoteipoint;
    /**
     * Database Column Remarks:ポイントステージ名称
     *
     */
    private String pointrnkmei;

    /**
     * 今回消費税込売上額を取得する
     * 
     * @return 今回消費税金額と今回消費税抜売上額を足した値
     */
    public String getKonkaitaxkomiurikin() {
        BigDecimal taxNukiKin =
            new BigDecimal(ItemUtil.nullToValue(this.getKonkaitaxnukiurikin(), "0"));
        BigDecimal taxKin = new BigDecimal(ItemUtil.nullToValue(this.getKonkaishohitaxkin(), "0"));
        BigDecimal total = taxKin.add(taxNukiKin);
        return total.toString();
    }

    /**
     * ご利用明細書の振替予定金額を返却する
     * 
     * @return 振替予定金額
     */
    public String getScheduledTransferAmountForGoriyoumeisai() {
        if (StringUtils.isEmpty(this.getKsikin())) {
            if (CommonConstants.SKYHOHOKBN_ZANDAKA.equals(this.getSkyhohokbn())) {
                return this.getKurikoshikin();
            }
            if (CommonConstants.SKYHOHOKBN_TOUGETSU.equals(this.getSkyhohokbn())) {
                return this.getKonkaitaxkomiurikin();
            }
            return "";
        } else {
            return this.getKsikin();
        }
    }

    /**
     * 売上入金明細の設定をするか確認する
     * 
     * @return 設定する場合true設定しない場合false
     */
    public boolean shouldSetUriageNyukinMeisai() {
        return this.getUridnpyno() != null && !TAX_FLAG_TAX.equals(this.getTaxrecflg());
    }

    /**
     * 得意先名称を取得する
     * 
     * @param shimeDate 締日
     * @return 得意先名称
     */
    public String getTokuisakiName(String shimeDate) {
        String tokuisakiInfo = getTokuisakiInfo(shimeDate);
        String tokuisakiName = this.getTokuisakinmn1() + "様ご利用明細" + tokuisakiInfo;
        return tokuisakiName;
    }

    /**
     * 得意先情報を取得する
     * 
     * @param shimeDate 締日
     * @return 得意先情報
     */
    private String getTokuisakiInfo(String shimeDate) {
        // 得意先のプラン表示判定を行う。（全ての条件がtrueの場合、プラン名を設定する。）
        if (hasTokubetsuUnt() && isPlanMoreThan1Month() && isPlanApplied(shimeDate)) {
            String planStartDate = getPlanStartDate();
            String planEndDate = getPlanEndDate(planStartDate);
            return "        " + this.getTokubetsuuntkbnnm() + "次回更新年月" + "　"
                + planEndDate.substring(0, 4) + "年" + planEndDate.substring(4, 6) + "月";
        }
        return "";
    }

    /**
     * 特別単価の得意先か調べる
     * 
     * @return 特別単価の得意先：true 特別単価のではない得意先：false
     */
    private boolean hasTokubetsuUnt() {
        if (StringUtils.isEmpty(this.getTokubetsuuntkbn())) {
            return false;
        }
        if (!hasPlan()) {
            return false;
        }
        if (StringUtils.isEmpty(getPlanStartDate())) {
            return false;
        }
        if (StringUtils.isEmpty(this.getRknplnkikan())) {
            return false;
        }
        return true;
    }

    /**
     * 特別単価区分がプランであるか調べる
     * 
     * @return 子育てアクア、子育てアクア特別プラン、２年割プラン、２年割特別プラン：true 左記以外の特別単価区分：false
     */
    private boolean hasPlan() {
        int tokubetsuuntkbn = Integer.parseInt(this.getTokubetsuuntkbn());
        int tokubetsuuntkbnBabyaqua = Integer.parseInt(CommonConstants.TOKUBETSUUNTKBN_BABYAQUA);
        return tokubetsuuntkbn >= tokubetsuuntkbnBabyaqua;
    }

    /**
     * 料金プラン期間が１か月より多いか調べる
     * 
     * @return １か月より大きい：true １か月未満：false
     */
    private boolean isPlanMoreThan1Month() {
        int rknplnkikan = Integer.parseInt(this.getRknplnkikan());
        return rknplnkikan > 1;
    }

    /**
     * プラン終了日を取得する
     * 
     * @param startDate プラン開始日
     * @return プラン終了日
     */
    private String getPlanEndDate(String startDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        ParsePosition pos = new ParsePosition(0);
        Date planStartDate = formatter.parse(startDate, pos);
        Calendar cal = Calendar.getInstance();
        cal.setTime(planStartDate);
        cal.add(Calendar.MONTH, (Integer.parseInt(this.getRknplnkikan()) - 1));
        return formatter.format(cal.getTime());
    }

    /**
     * プラン開始日を取得する
     * 
     * @return プラン開始日
     */
    private String getPlanStartDate() {
        return StringUtils.isEmpty(this.getRknplnkeizokukshndd()) ? this.getRknplnsetteidd()
            : this.getRknplnkeizokukshndd();
    }

    /**
     * 締日がプラン適用日付に含まれるか調べる
     * 
     * @param shimeDate 締日
     * @return 締日がプラン適用日付に含まれる：true 締日がプラン適用日付に含まれない:false
     */
    private boolean isPlanApplied(String shimeDate) {
        String planStartDate = getPlanStartDate();
        String planEndDate = getPlanEndDate(planStartDate);

        // 料金プラン継続更新日が未設定の場合、料金プラン設定日が締日の適用日付に含まれているか判定する
        if (StringUtils.isEmpty(this.getRknplnkeizokukshndd())) {
            return DateTimeUtil.isLater(planStartDate.substring(0, 6), shimeDate.substring(0, 6))
                && DateTimeUtil.isLater(shimeDate.substring(0, 6), planEndDate.substring(0, 6));
        }
        // 料金プラン継続更新日と料金プラン設定日が設定されている場合、締日が料金プラン継続更新日に含まれているか判定する
        if (!StringUtils.isEmpty(this.getRknplnsetteidd())) {
            return DateTimeUtil.isLater(shimeDate.substring(0, 6), planEndDate.substring(0, 6));
        }

        // 上記以外の判定の場合は適用日付外としてfalseを返す。
        return false;
    }

    /**
     * 振替予定金額を取得する
     * 
     * @param invoiceFlag インボイス施行フラグ
     * @param reportType 帳票種別
     * @return 振替予定金額
     */
    public String getHurikaeYoteikin(String invoiceFlag, String reportType) {
        if (isHurikaeyoteikinRequired(invoiceFlag, reportType)) {
            if (StringUtils.isEmpty(this.getKsikin())) {
                if (CommonConstants.SKYKBN_1.equals(this.getSkyhohokbn())) {
                    return this.getKurikoshikin();
                }
                if (CommonConstants.SKYKBN_2.equals(this.getSkyhohokbn())) {
                    return getKonkaitaxkomiurikin();
                }
            } else {
                return this.getKsikin();
            }
        }
        return "";
    }

    /**
     * 振替予定金額が必要か調べる
     * 
     * @param invoiceFlag インボイス施行フラグ
     * @param reportType 帳票種別
     * @return true:振替予定金額を取得する false:振替予定金額を取得しない
     */
    private boolean isHurikaeyoteikinRequired(String invoiceFlag, String reportType) {
        if (!isGoriyouMeisai(reportType)) {
            return false;
        }

        // 請求区分が1：一般 または
        // 請求区分が2：親子 かつ 明細印刷区分が1：親子卸の場合は出力対象とする。
        if (CommonConstants.SKYKBN_1.equals(this.getSkykbn())) {
            return true;
        }
        if (CommonConstants.SKYKBN_2.equals(this.getSkykbn())
            && CommonConstants.MEISAIPRNTKBN_ISSHO.equals(this.getMeisaiprntkbn())) {
            return true;
        }
        return false;
    }

    /**
     * 帳票がご利用明細が調べる
     * 
     * @param reportType 帳票種別
     * @return true:ご利用明細 false:請求書
     */
    private boolean isGoriyouMeisai(String reportType) {
        if (GORIYOMEISAI_HIKIOTOSHI_ARI.equals(reportType)
            || GORIYOMEISAI_HIKIOTOSHI_NASHI.equals(reportType)) {
            return true;
        }
        return false;
    }

    /**
     * 今回ご利用額を取得する
     * 
     * @return 税計算区分が外税：今回税抜売上金額 税計算区分が外税以外：今回税込売上金額を返却
     */
    public String getKonkaiGoriyouGaku() {
        if (CommonConstants.KBN_TAXKEISAN_EX.equals(this.getTaxkeisankbn())) {
            return this.getKonkaitaxnukiurikin();
        }
        return getKonkaitaxkomiurikin();
    }

    /**
     * 請求書番号にハイフンを追加して取得する
     * 
     * @param reportType 帳票種別
     * @return 帳票種別請求書：請求書番号 帳票種別ご利用明細書：ご利用明細書番号
     */
    public String getSkyshonoToFormat(String reportType) {
        if (isGoriyouMeisai(reportType)) {
            return noToFormat(this.getRiyomeisaishono());
        }
        return noToFormat(this.getSkyshono());
    }

    /**
     * 請求書番号かご利用明細書番号をフォーマットする
     * 
     * @param no 請求書番号またはご利用明細書番号
     * @return 引数の値を"上10桁-下2桁"に変換して返却
     */
    private String noToFormat(String no) {
        if (StringUtils.isEmpty(no)) {
            return "";
        }
        int noSize = no.length();
        String formatNo = no.substring(0, 10) + "-" + no.substring(noSize - 2);
        return formatNo;
    }

    /**
     * toString
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", urinykkbn=").append(urinykkbn);
        sb.append(", uridnpyno=").append(uridnpyno);
        sb.append(", tokuisakicd=").append(tokuisakicd);
        sb.append(", uridd=").append(uridd);
        sb.append(", suryo=").append(suryo);
        sb.append(", tani=").append(tani);
        sb.append(", unt=").append(unt);
        sb.append(", kin=").append(kin);
        sb.append(", untshohitaxkin=").append(untshohitaxkin);
        sb.append(", shohitaxkin=").append(shohitaxkin);
        sb.append(", shncd=").append(shncd);
        sb.append(", shnnm=").append(shnnm);
        sb.append(", tanpinmeisaiflg=").append(tanpinmeisaiflg);
        sb.append(", meisaibiko=").append(meisaibiko);
        sb.append(", stshncd=").append(stshncd);
        sb.append(", taxkbn=").append(taxkbn);
        sb.append(", oyastshncd=").append(oyastshncd);
        sb.append(", oyasttaxkbn=").append(oyasttaxkbn);
        sb.append(", nyuryokumeisaiflg=").append(nyuryokumeisaiflg);
        sb.append(", skysakicd=").append(skysakicd);
        sb.append(", kaishuhohokbn=").append(kaishuhohokbn);
        sb.append(", kaishuhohokbnnm=").append(kaishuhohokbnnm);
        sb.append(", shrytidydmdd=").append(shrytidydmdd);
        sb.append(", konkaigoriyokin=").append(konkaigoriyokin);
        sb.append(", znkkurikoshikin=").append(znkkurikoshikin);
        sb.append(", konkaitaxnukiurikin=").append(konkaitaxnukiurikin);
        sb.append(", konkaishohitaxkin=").append(konkaishohitaxkin);
        sb.append(", konkainyknkin=").append(konkainyknkin);
        sb.append(", kurikoshikin=").append(kurikoshikin);
        sb.append(", skykbn=").append(skykbn);
        sb.append(", yubinno=").append(yubinno);
        sb.append(", adrsn1=").append(adrsn1);
        sb.append(", adrsn2=").append(adrsn2);
        sb.append(", adrsn3=").append(adrsn3);
        sb.append(", skysakinmn1=").append(skysakinmn1);
        sb.append(", skysakinmn2=").append(skysakinmn2);
        sb.append(", skysakikeishokbn=").append(skysakikeishokbn);
        sb.append(", skysakikeishokbnnm=").append(skysakikeishokbnnm);
        sb.append(", prntbiko=").append(prntbiko);
        sb.append(", sksmkinyukknnmkana=").append(sksmkinyukknnmkana);
        sb.append(", sksmkinyukknshitennmkana=").append(sksmkinyukknshitennmkana);
        sb.append(", sksmkinyukknkzkind=").append(sksmkinyukknkzkind);
        sb.append(", sksmkinyukknkzkindnm=").append(sksmkinyukknkzkindnm);
        sb.append(", sksmkinyukknkzno=").append(sksmkinyukknkzno);
        sb.append(", sksmksikknnm=").append(sksmksikknnm);
        sb.append(", meisaiprntkbn=").append(meisaiprntkbn);
        sb.append(", breaktokuisakicd=").append(breaktokuisakicd);
        sb.append(", yubinnobakodo=").append(yubinnobakodo);
        sb.append(", skyhohokbn=").append(skyhohokbn);
        sb.append(", kinyukknkzkind=").append(kinyukknkzkind);
        sb.append(", kinyukknkzkindnm=").append(kinyukknkzkindnm);
        sb.append(", kinyukknkzno=").append(kinyukknkzno);
        sb.append(", kinyukknnm=").append(kinyukknnm);
        sb.append(", kinyukknshitennm=").append(kinyukknshitennm);
        sb.append(", kinyukknkzmeigi=").append(kinyukknkzmeigi);
        sb.append(", kinyukknn2kzkind=").append(kinyukknn2kzkind);
        sb.append(", kinyukkn2kzkindnm=").append(kinyukkn2kzkindnm);
        sb.append(", kinyukknn2kzno=").append(kinyukknn2kzno);
        sb.append(", kinyukknn2nm=").append(kinyukknn2nm);
        sb.append(", kinyukknn2shitennm=").append(kinyukknn2shitennm);
        sb.append(", kinyukknn2kzmeigi=").append(kinyukknn2kzmeigi);
        sb.append(", sshdshhitoimage=").append(sshdshhitoimage);
        sb.append(", dnpyoshirasenaiyon1=").append(dnpyoshirasenaiyon1);
        sb.append(", dnpyoshirasenaiyon2=").append(dnpyoshirasenaiyon2);
        sb.append(", dnpyoshirasenaiyon3=").append(dnpyoshirasenaiyon3);
        sb.append(", dnpyoshirasenaiyon4=").append(dnpyoshirasenaiyon4);
        sb.append(", dnpyoshirasenaiyon5=").append(dnpyoshirasenaiyon5);
        sb.append(", dnpyoshirasenaiyon6=").append(dnpyoshirasenaiyon6);
        sb.append(", taxkeisankbn=").append(taxkeisankbn);
        sb.append(", tairyukin=").append(tairyukin);
        sb.append(", tairyukinmei=").append(tairyukinmei);
        sb.append(", tokuisakinmn1=").append(tokuisakinmn1);
        sb.append(", osirase2=").append(osirase2);
        sb.append(", ksikin=").append(ksikin);
        sb.append(", rknplnsetteidd=").append(rknplnsetteidd);
        sb.append(", rknplnkeizokukshndd=").append(rknplnkeizokukshndd);
        sb.append(", tokubetsuuntkbn=").append(tokubetsuuntkbn);
        sb.append(", tokubetsuuntkbnnm=").append(tokubetsuuntkbnnm);
        sb.append(", rknplnkikan=").append(rknplnkikan);
        sb.append(", plncd=").append(plncd);
        sb.append(", acikeiyuksikbn=").append(acikeiyuksikbn);
        sb.append(", taxkeisankbn1=").append(taxkeisankbn1);
        sb.append(", taxrecflg=").append(taxrecflg);
        sb.append(", riyomeisaishono=").append(riyomeisaishono);
        sb.append(", pointsystemjoinkbn=").append(pointsystemjoinkbn);
        sb.append(", pointkanriumu=").append(pointkanriumu);
        sb.append(", kakutokuyoteipoint=").append(kakutokuyoteipoint);
        sb.append(", pointrnkmei=").append(pointrnkmei);
        sb.append("]");
        return sb.toString();
    }
}
