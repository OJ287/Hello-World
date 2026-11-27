package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M016TEntityKey;
import lombok.Data;

/**
 * Database Table Remarks: 定例売上仕入マスタ
 *
 * This class corresponds to the database table S02M016T
 */
@Data
public class S02M016TB0501W02Entity extends S02M016TEntityKey implements Serializable {

    /**
     * Database Column Remarks: 得意先コード
     *
     */
    private String tokuisakicd;

    /**
     * Database Column Remarks: 計上日
     *
     */
    private String keijodd;

    /**
     * Database Column Remarks: 初回計上年月
     *
     */
    private String shokaikeijodydm;

    /**
     * Database Column Remarks: 次回計上年月
     *
     */
    private String jikaikeijodydm;

    /**
     * Database Column Remarks: 終了年月
     *
     */
    private String shrydydm;

    /**
     * Database Column Remarks: 前回発生計上日
     *
     */
    private String znkhasseikeijodd;

    /**
     * Database Column Remarks: 実績担当者コード
     *
     */
    private String jissekitnshacd;

    /**
     * Database Column Remarks: 実績担当者名
     *
     */
    private String jissekitnshamei;

    /**
     * Database Column Remarks: 数量
     *
     */
    private String suryo;

    /**
     * Database Column Remarks: 単価
     *
     */
    private String hyojunprice;

    /**
     * Database Column Remarks: 合計金額
     *
     */
    private String kin;

    /**
     * Database Column Remarks: 消費税
     *
     */
    private String shohitaxkin;

    /**
     * Database Column Remarks: 初回単価
     *
     */
    private String shokaiunt;

    /**
     * Database Column Remarks: 初回金額
     *
     */
    private String shokaikin;

    /**
     * Database Column Remarks: 初回消費税額
     *
     */
    private String shokaishohitaxkin;

    /**
     * Database Column Remarks: 明細備考
     *
     */
    private String meisaibiko;

    /**
     * Database Column Remarks: 売上設定
     *
     */
    private String rtlrknshukeikbn;

    /**
     * Database Column Remarks: 発生間隔計上パターン
     *
     */
    private String hasseikankakukeijoptn;

    /**
     * Database Column Remarks: 売上発生有無フラグ
     *
     */
    private String dnpyhasseiarinashiflg;

    /**
     * Database Column Remarks: 有効区分
     *
     */
    private String yukokbn;

    /**
     * Database Column Remarks: 登録システム日付
     *
     */
    private String inptsystmymd;

    /**
     * Database Column Remarks: 登録システム時間
     *
     */
    private String inptsystmjikan;

    /**
     * Database Column Remarks: 登録者ＡＰ基本コード
     *
     */
    private String toshaapkihoncd;

    /**
     * Database Column Remarks: 登録者ショップコード
     *
     */
    private String toshashopcd;

    /**
     * Database Column Remarks: 登録者コード
     *
     */
    private String toshacd;

    /**
     * Database Column Remarks: 登録者名
     *
     */
    private String toshamei;

    /**
     * Database Column Remarks: 更新システム日付
     *
     */
    private String kshnsystmymd;

    /**
     * Database Column Remarks: 更新システム時間
     *
     */
    private String kshnsystmjikan;

    /**
     * Database Column Remarks: 更新者ＡＰ基本コード
     *
     */
    private String koshaapkihoncd;

    /**
     * Database Column Remarks: 更新者ショップコード
     *
     */
    private String koshashopcd;

    /**
     * Database Column Remarks: 更新者コード
     *
     */
    private String koshacd;

    /**
     * Database Column Remarks: 更新者名
     *
     */
    private String koshamei;

    /**
     * Database Column Remarks: 現標準価格
     *
     */
    private String genzhyojunprice;

    /**
     * Database Column Remarks: 新標準価格
     *
     */
    private String shinhyojunprice;

    /**
     * Database Column Remarks: 標準価格切替日
     *
     */
    private String hyojunpricekirikaedd;

    /**
     * Database Column Remarks: 商品コード
     *
     */
    private String shncd;

    /**
     * Database Column Remarks: 定例設定区分
     *
     */
    private String tteireikbn;

    /**
     * Database Column Remarks: 税設定区分
     *
     */
    private String taxsetteikbn;

    /**
     * Database Column Remarks: 税区分
     *
     */
    private String taxkbn;

    /**
     * Database Column Remarks: 計上区分
     *
     */
    private String keijokbn;

    /**
     * Database Column Remarks: 計上区分名
     *
     */
    private String keijokbnnm;

    /**
     * 検索条件: 定数_有効区分_1（有効）
     */
    private String constyukokbn1 = CommonConstants.YUKOKBN_1;

    /**
     * 検索条件: 定数_AP基本コード_000
     */
    private String constapkihoncd000 = CommonConstants.APCD_000;

    /**
     * 検索条件: 定数_ショップコード_000
     */
    private String constshopcd000 = CommonConstants.SYS_SHOPCD_000;

    /**
     * 検索条件: 定数_レコード区分_M（明細）
     */
    private String constrecordkbnm = CommonConstants.RECORD_KBN_M;

    /**
     * 検索条件: 定数_設定番号_009（計上区分）
     */
    private String constsetteino009 = CommonConstants.SYS_SETTEINO_009;

    /**
     * 検索条件: 定数_売上仕入区分_1（売上仕入区分）
     */
    private String consturisiirekbn1 = CommonConstants.URISIIREKBN_1;

    /**
     * 検索条件: 定数_定例区分_1（サーバーレンタル）
     */
    private String constteireikbn1 = CommonConstants.TEIREIKBN_1;

    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", tokuisakicd=").append(tokuisakicd);
        sb.append(", keijodd=").append(keijodd);
        sb.append(", shokaikeijodydm=").append(shokaikeijodydm);
        sb.append(", jikaikeijodydm=").append(jikaikeijodydm);
        sb.append(", shrydydm=").append(shrydydm);
        sb.append(", znkhasseikeijodd=").append(znkhasseikeijodd);
        sb.append(", jissekitnshacd=").append(jissekitnshacd);
        sb.append(", jissekitnshamei=").append(jissekitnshamei);
        sb.append(", suryo=").append(suryo);
        sb.append(", hyojunprice=").append(hyojunprice);
        sb.append(", kin=").append(kin);
        sb.append(", shohitaxkin=").append(shohitaxkin);
        sb.append(", shokaiunt=").append(shokaiunt);
        sb.append(", shokaikin=").append(shokaikin);
        sb.append(", shokaishohitaxkin=").append(shokaishohitaxkin);
        sb.append(", meisaibiko=").append(meisaibiko);
        sb.append(", rtlrknshukeikbn=").append(rtlrknshukeikbn);
        sb.append(", hasseikankakukeijoptn=").append(hasseikankakukeijoptn);
        sb.append(", dnpyhasseiarinashiflg=").append(dnpyhasseiarinashiflg);
        sb.append(", yukokbn=").append(yukokbn);
        sb.append(", inptsystmymd=").append(inptsystmymd);
        sb.append(", inptsystmjikan=").append(inptsystmjikan);
        sb.append(", toshaapkihoncd=").append(toshaapkihoncd);
        sb.append(", toshashopcd=").append(toshashopcd);
        sb.append(", toshacd=").append(toshacd);
        sb.append(", toshamei=").append(toshamei);
        sb.append(", kshnsystmymd=").append(kshnsystmymd);
        sb.append(", kshnsystmjikan=").append(kshnsystmjikan);
        sb.append(", koshaapkihoncd=").append(koshaapkihoncd);
        sb.append(", koshashopcd=").append(koshashopcd);
        sb.append(", koshacd=").append(koshacd);
        sb.append(", koshamei   =").append(koshamei);
        sb.append(", genzhyojunprice=").append(genzhyojunprice);
        sb.append(", shinhyojunprice=").append(shinhyojunprice);
        sb.append(", hyojunpricekirikaedd=").append(hyojunpricekirikaedd);
        sb.append(", shncd=").append(shncd);
        sb.append(", tteireikbn=").append(tteireikbn);
        sb.append(", taxsetteikbn=").append(taxsetteikbn);
        sb.append(", taxkbn=").append(taxkbn);
        sb.append(", keijokbn=").append(keijokbn);
        sb.append(", keijokbnnm=").append(keijokbnnm);
        sb.append("]");
        return sb.toString();
    }
}
