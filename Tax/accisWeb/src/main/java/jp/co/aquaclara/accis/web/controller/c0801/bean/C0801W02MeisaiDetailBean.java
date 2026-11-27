package jp.co.aquaclara.accis.web.controller.c0801.bean;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.web.controller.c0801.bean.price.C0801W02SwitchableShnPriceBean;
import jp.co.aquaclara.accis.web.controller.c0801.bean.price.C0802W01RangeShnPriceBean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 受注明細情報Beanクラス(一覧表示部分).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class C0801W02MeisaiDetailBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 「商品コード」テキストボックスリードオンリー */
    private String shncd;
    /** 「商品名称略称」ラベル */
    private String shnnmryakusho;
    /** 「色」ラベル */
    private String iro;
    /** 「サイズ」ラベル */
    private String sz;
    /** 「型式」ドロップダウンリスト */
    private String katasiki;
    /** 「計上区分」ドロップダウンリスト */
    private String keijokbn;
    /** 「受注数量」一行テキストボックス（セパレート日付以外） */
    private String acptsuryo;
    /** 「売上取扱単位」ラベル */
    private String uritrtskitani;
    /** 「売上入数」テキストボックスリードオンリー */
    private String uriirisu;
    /** 「売上単位」ラベル */
    private String uritani;
    /** 「総数」テキストボックスリードオンリー */
    private String sokazu;
    /** 「標準単価」テキストボックスリードオンリー */
    private String hyojununt;
    /** 「受注単価」一行テキストボックス（セパレート日付以外） */
    private String acptunt;
    /** 「金額」テキストボックスリードオンリー */
    private String kin;
    /** 「明細備考No」 */
    private String meisaibikono;
    /** 「明細備考」一行テキストボックス（セパレート日付以外） */
    private String meisaibiko;
    /** 「大分類コード」隠しフィールド */
    private String lctgrycd;
    /** 「中分類コード」隠しフィールド */
    private String mctgrycd;
    /** 「小分類コード」隠しフィールド */
    private String sctgrycd;
    /** 「受注伝票明細番号」隠しフィールド */
    private String acptdnpymeisaino;
    /** 「受注伝票明細枝番号」隠しフィールド */
    private String acptdnpymeisaiedano;
    /** 「データ作成元区分」隠しフィールド */
    private String datsksmotokbn;

    /** 受注予約明細かどうかを返す */
    public boolean isReserved() {
        return CommonConstants.KBN_SAKUSEIMOTO_JUCHUYOYAKU.equals(datsksmotokbn);
    }

    /** 「受注単価消費税金額」隠しフィールド */
    private String acptuntshohitaxkin;
    /** 「受注消費税金額」隠しフィールド */
    private String acptshohitaxkin;
    /** 「初期表示価格」隠しフィールド */
    private String shokidsplyprice;
    /** 「在庫単価」隠しフィールド */
    private String zaikount;
    /** 「商品税区分」隠しフィールド */
    private String shntaxkbn;
    /** 「単品明細フラグ」隠しフィールド */
    private String tanpinmeisaiflg;

    /** お気に入り商品かを示すフラグ */
    private boolean favorite;

    /** クライアントでの金額計算に必要な情報 */

    /** 標準価格情報 */
    private C0801W02SwitchableShnPriceBean hyojunpriceBean;

    /** オープン価格情報 */
    private C0801W02SwitchableShnPriceBean openpriceBean;

    /** 販促価格情報 */
    private C0802W01RangeShnPriceBean hansokupriceBean;

    /** 得意先別特別商品価格情報 */
    private C0802W01RangeShnPriceBean tokubetsupriceBean;

    /** 特別価格情報リスト */
    @Builder.Default
    private List<C0802W01RangeShnPriceBean> tokubetsupriceBeanList = new LinkedList<>();

    /** 料金種別特別単価フラグ */
    private String rknkindtokubetsuuntflg;

    /** 販促優先区分 */
    private String hansokuyusenkbn;

    /** 販促区分 */
    private String hansokukbn;

    /** 新価格区分 */
    private String shinpricekbn;

    /** 現価格区分 */
    private String genzpricekbn;

    /** 現計算区分 */
    private String genzkeisankbn;

    /** 新計算区分 */
    private String shinkeisankbn;

    /** 税計算区分切替日 */
    private String taxkeisankbnurikirikaedd;

    /** 消費税切替日 */
    private String shohitaxkirikaedd;

    /** 税区分 */
    private String taxkbn;

    /** 税端数計算区分 */
    private String taxhasukeisankbn;

    /** 売上金額端数計算区分 */
    private String urikinhasukeisankbn;

    /** 計算方法 */
    private String keisanhoho;

    /** 販売価格の活性非活性を示す */
    private boolean disabledAcptUnt;

    /** 明細行番号 */
    private int rowIndex;

    /** 過去に手入力で受注単価を変更したことがあるか */
    private boolean untChangedBefore;
}
