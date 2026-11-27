package jp.co.aquaclara.accis.web.domain.dto.c0815;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * C0815W11_配達実績登録（一括更新）受注配達明細DTO.
 * 
 * @author accisdev006
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class C0815W11AcptDlvryMeisaiDto implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** 行追加フラグ */
    private String addgyoflg;
    /** 計上区分 */
    private String keijokbn;
    /** 数量明細 */
    private String suryo;
    /** 単位明細 */
    private String tani;
    /** 入数明細 */
    private String irisu;
    /** 入数単位明細 */
    private String irisutani;
    /** 販売価格明細 */
    private String hanbaiprice;
    /** 金額明細 */
    private String kin;
    /** 明細備考番号 */
    private String meisaibikono;
    /** 単価消費税金額 */
    private String untshohitaxkin;
    /** 消費税金額 */
    private String shohitaxkin;
    /** 商品税区分 */
    private String shntaxkbn;
    /** 標準価格切替日 */
    private String hyojunpricekirikaedd;
    /** 現標準価格 */
    private String genzhyojunprice;
    /** 新標準価格 */
    private String shinhyojunprice;
    /** 現標準持ち帰り価格 */
    private String genzhyojunmochikaeriprice;
    /** 新標準持ち帰り価格 */
    private String shinhyojunmochikaeriprice;
    /** ショップ価格切替日 */
    private String shoppricekirikaedd;
    /** 現ショップ価格 */
    private String genzshopprice;
    /** 新ショップ価格 */
    private String shinshopprice;
    /** 現ショップ持ち帰り価格 */
    private String genzshopmochikaeriprice;
    /** 新ショップ持ち帰り価格 */
    private String shinshopmochikaeriprice;
    /** 販促開始日 */
    private String hansokustrtdd;
    /** 販促終了日 */
    private String hansokushrydd;
    /** 販促価格 */
    private String hansokuprice;
    /** 販促持ち帰り価格 */
    private String hansokumochikaeriprice;
    /** 得意先価格開始日 */
    private String ttpricestrtdd;
    /** 得意先価格終了日 */
    private String ttpriceshrydd;
    /** 得意先価格 */
    private String ttprice;
    /** 得意先持ち帰り価格 */
    private String ttmochikaeriprice;
    /** 初期表示価格 */
    private String shokidsplyprice;
    /** 計上区分２ */
    private String keijokbn2;
    /** 価格区分 */
    private String pricekbn;
    /** 単品明細フラグ */
    private String tanpinmeisaiflg;
    /** 特別単価Json */
    private String tokubetsuUntJson;
    /** セット区分 */
    private String stkbn;
    /** 受注伝票明細枝番号 */
    private String acptdnpymeisaiedano;
    /** AP基本コード */
    private String apkihoncd;
    /** ショップコード */
    private String shopcd;
    /** 受注伝票番号 */
    private String acptdnpyno;
    /** 枝番号 */
    private Short edano;
    /** 受注伝票明細番号 */
    private String acptdnpymeisaino;
    /** 委託相手AP基本コード */
    private String itakuaiteapkihoncd;
    /** 委託相手ショップコード */
    private String itakuaiteshopcd;
    /** 委託相手受注伝票番号 */
    private String itakuaiteacptdnpyno;
    /** 委託相手枝番号 */
    private String itakuaiteedano;
    /** 委託相手受注伝票明細番号 */
    private String itakuaiteacptdnpymeisaino;
    /** 委託相手受注伝票明細枝番号 */
    private String itakuaiteacptdnpymeisaiedano;
    /** 大分類コード */
    private String lctgrycd;
    /** 中分類コード */
    private String mctgrycd;
    /** 小分類コード */
    private String sctgrycd;
    /** 商品コード */
    private String shncd;
    /** 配布物フラグ */
    private String haifuflg;
    /** 配布物AP基本コード */
    private String haifubutsuapkihoncd;
    /** 配布物ショップコード */
    private String haifubutsushopcd;
    /** 配布物コード */
    private String haifubutsucd;
    /** 配布物名称 */
    private String haifubutsunm;
    /** 型式 */
    private String katasiki;
    /** 受注数量 */
    private String acptsuryo;
    /** 標準単価 */
    private String hyojununt;
    /** 受注単価 */
    private String acptunt;
    /** 受注金額 */
    private String acptkin;
    /** 受注単価消費税 */
    private String acptuntshohitaxkin;
    /** 受注消費税 */
    private String acptshohitaxkin;
    /** 委託会社受注単価 */
    private String itakucmpnyacptunt;
    /** 委託会社受注単価消費税金額 */
    private String itakucmpnyacptuntshohitaxkin;
    /** 委託会社消費税金額 */
    private String itakucmpnyacptshohitaxkin;
    /** 納品数量 */
    private String nohinsuryo;
    /** 納品単価 */
    private String nohinunt;
    /** 納品金額 */
    private String nohinkin;
    /** 納品単価消費税金額 */
    private String nohinuntshohitaxkin;
    /** 納品消費税金額 */
    private String nohinshohitaxkin;
    /** 残回収 */
    private String zankaisu;
    /** 総回数 */
    private String soukaisu;
    /** 税区分 */
    private String taxkbn;
    /** 有償区分 */
    private String yusyokbn;
    /** 明細区分 */
    private String meisaikbn;
    /** 入力明細区分 */
    private String nyuryokumeisaiflg;
    /** セット商品コード */
    private String stshncd;
    /** 明細備考 */
    private String meisaibiko;
    /** データ作成元区分 */
    private String datsksmotokbn;
    /** 有効区分 */
    private String yukokbn;
    /** 更新システム日付 */
    private String kshnsystmymd;
    /** 更新システム時間 */
    private String kshnsystmjikan;
    /** 商品AP基本コード */
    private String shnapkihoncd;
    /** 商品名称略称 */
    private String shnnmryakusho;
    /** 現在持ち帰り価格 */
    private String genzmochikaeriprice;
    /** 新持ち帰り価格 */
    private String shinmochikaeriprice;
    /** オープン価格切替日 */
    private String opnpricekirikaedd;
    /** 現在オープン価格 */
    private String genzopnprice;
    /** 新オープン価格 */
    private String shinopnprice;
    /** 現在オープン持ち帰り価格 */
    private String genzopnmochikaeriprice;
    /** 新オープン持ち帰り価格 */
    private String shinopnmochikaeriprice;
    /** 販促区分 */
    private String hansokukbn;
    /** 販促配達価格 */
    private String hansokudlvryprice;
    /** 販促優先区分 */
    private String hansokuyusenkbn;
    /** 在庫単価 */
    private String zaikount;
    /** 色 */
    private String iro;
    /** サイズ */
    private String sz;
    /** 売上取扱単位 */
    private String uritrtskitani;
    /** 売上入数 */
    private String uriirisu;
    /** 売上単位 */
    private String uritani;
    /** 税設定区分 */
    private String taxsetteikbn;
    /** 在庫アラーム数量 */
    private String zaikoaramusuryo;
    /** 取り扱い区分 */
    private String trtskikbn;
    /** 特別単価区分 */
    private String tokubetsuuntkbn;
    /** 適用開始日 */
    private String tekiyostrtdd;
    /** 適用終了日 */
    private String tekiyoshrydd;
    /** 配達価格 */
    private String dlvryprice;
    /** 持ち帰り価格 */
    private String mochikaeriprice;
    /** 計上区分名称 */
    private String keijokbnnm;
    /** 総数 */
    private String sokazu;
    /** 商品名称略称 */
    private String shnnm;
    /** 標準価格 */
    private String hyojunprice;
    /** 売上伝票 */
    private String uridnpyno;
    /** 売上伝票番号 */
    private String uridnpyedano;
    /** 売上伝票明細番号 */
    private String uridnpymeisaino;
    /** 売上伝票明細枝番号 */
    private String uridnpymeisaiedano;
    /** 登録者AP基本コード */
    private String toshaapkihoncd;
    /** 登録者ショップコード */
    private String toshashopcd;
    /** 登録者コード */
    private String toshacd;
    /** 登録システム日付 */
    private String inptsystmymd;
    /** 登録システム時間 */
    private String inptsystmjikan;
    /** 更新者AP基本コード */
    private String koshaapkihoncd;
    /** 更新者ショップコード */
    private String koshashopcd;
    /** 更新者コード */
    private String koshacd;
    /** 登録者名 */
    private String toshamei;
    /** 更新者名 */
    private String koshamei;
    /** セット適用開始日 */
    private String set_tekiyostrtdd;
    /** セット適用終了日 */
    private String set_tekiyoshrydd;
    /** セット配達価格 */
    private String set_dlvryprice;
    /** セット持ち帰り価格 */
    private String set_mochikaeriprice;
    /** 販促名称 */
    private String hansokunm;
    /** 料金種別特別単価フラグ */
    private String rknkindtokubetsuuntflg;
    /** チェック時用行番号 */
    private String meisaiposition;

    /**
     * 伝票ステータスに応じた数量を取得する
     * 
     * @param dnpysts 伝票ステータス
     * @return 伝票ステータスが「配達・売上済」の場合は納品数量、それ以外は受注数量
     */
    public String getSuryo(String dnpysts) {
        return CommonConstants.DNPYSTS_12.equals(dnpysts) ? getNohinsuryo() : getAcptsuryo();
    }

    /**
     * 伝票ステータスに応じた単価を取得する
     * 
     * @param dnpysts 伝票ステータス
     * @return 伝票ステータスが「配達・売上済」の場合は納品単価、それ以外は受注単価
     */
    public String getUnt(String dnpysts) {
        return CommonConstants.DNPYSTS_12.equals(dnpysts) ? getNohinunt() : getAcptunt();
    }
}
