package jp.co.aquaclara.accis.web.controller.b0501.bean;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import jp.co.aquaclara.accis.common.constants.ValidationConstants;
import jp.co.aquaclara.accis.web.annotation.ByteSize;
import jp.co.aquaclara.accis.web.annotation.ValidationMessage;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 得意先マスタ(登録画面)-サーバーレンタル料情報Beanクラス.
 */
@Data
@AllArgsConstructor
public class B0501W02RentalBean implements Serializable {

    public B0501W02RentalBean() {
        super();
    }

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    // 定例売上設定
    /** 複数行用メッセージ */
    private String hukusugyoyomsg;
    /** 定例発生番号 */
    private String teireihasseino;
    /** 定例設定区分 */
    private String teireisetteikbn;
    /** 計上日 */
    @Pattern(regexp = ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS)
    @ValidationMessage(validation = Pattern.class, messageId = MessageID.W013,
        args = {"計上日", "0～9のみ"})
    @Min(1)
    @ValidationMessage(validation = Min.class, messageId = MessageID.W115,
        args = {"計上日", "1", "31"})
    @Max(31)
    @ValidationMessage(validation = Max.class, messageId = MessageID.W115,
        args = {"計上日", "1", "31"})
    private String keijodd;
    /** 開始年月 */
    @Pattern(regexp = ValidationConstants.REGEXP_YYYY_MM_SLASH)
    @ValidationMessage(messageId = MessageID.W016, args = {"開始年月"})
    private String strtdydm;
    /** 次回計上年月 */
    @Pattern(regexp = ValidationConstants.REGEXP_YYYY_MM_SLASH)
    @ValidationMessage(messageId = MessageID.W016, args = {"次回計上年月"})
    private String jikaikeijodydm;
    /** 終了年月 */
    @Pattern(regexp = ValidationConstants.REGEXP_YYYY_MM_SLASH)
    @ValidationMessage(messageId = MessageID.W016, args = {"終了年月"})
    private String shrydydm;
    /** 前回発生計上日 */
    private String znkhasseikeijodd;
    /** 実績担当者コード */
    @Pattern(regexp = ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS)
    @ValidationMessage(validation = Pattern.class, messageId = MessageID.W013,
        args = {"実績担当者コード", "0～9のみ"})
    private String jissekitnshacd;
    /** 実績担当者名 */
    private String tnshamei;
    /** 商品名 */
    private String shnmei;
    /** 次回計上日 */
    private String jikaikeijodd;
    /** 初回計上日 */
    private String shokaikeijodd;
    /** 商品税区分 */
    private String shntaxkbn;
    /** 売上計上区分 */
    private String urikeijokbnnm;
    /** 数量 */
    private String suryo;
    /** 標準価格 */
    private String hyojunprice;
    /** 単価 */
    private String unt;
    /** 合計金額 */
    private String smkin;
    /** 消費税 */
    private String shohitax;
    /** 初回単価 */
    private String shokaiunt;
    /** 初回金額 */
    private String smkin1;
    /** 消費税1 */
    private String shohitax1;
    /** 明細備考 */
    @ByteSize(size = 40)
    @ValidationMessage(validation = ByteSize.class, messageId = MessageID.W009)
    private String meisaibiko;
    /** 売上設定 */
    private String urisettei;
    /** 発生間隔計上パターン */
    private String hasseikankakukeijoptn;
    /** 売上発生有無フラグ */
    private String urihasseiarinashiflg;
    /** 有効区分 */
    private String yukokbn1;
    /** 登録年月日 */
    private String inptsystmymd1;
    /** 登録時間 */
    private String inptsystmjikan1;
    /** 登録者AP基本コード */
    private String toshaapkihoncd1;
    /** 登録者ショップコード */
    private String toshashopcd1;
    /** 登録者コード */
    private String toshacd1;
    /** 登録者名称 */
    private String toshamei2;
    /** 更新年月日 */
    private String kshnsystmymd1;
    /** 更新時間 */
    private String kshnsystmjikan1;
    /** 更新者AP基本コード */
    private String koshaapkihoncd1;
    /** 更新者ショップコード */
    private String koshashopcd1;
    /** 更新者コード */
    private String koshacd1;
    /** 更新者名称 */
    private String koshamei1;
    /** 価格情報 */
    private String priceinfo;
    /** 消費税情報 */
    private List<String> jsshohitaxvo;
}
