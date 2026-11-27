package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntityKey;
import lombok.Data;

/**
 * Database Table Remarks: 受注配達明細
 *
 * This class corresponds to the database table S04T002T
 */
@Data
public class S04T002TC0815W11Entity extends S04T002TEntityKey implements Serializable {

    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * Database Column Remarks: 価格（単価）取得基準日
     *
     */
    private String priceacquisitionkeydate;

    /**
     * Database Column Remarks: 受注日
     *
     */
    private String acptdd;

    /**
     * Database Column Remarks: AP基本コード
     *
     */
    private String apkihoncd;

    /**
     * Database Column Remarks: ショップコード
     *
     */
    private String shopcd;

    /**
     * Database Column Remarks: 受注伝票番号
     *
     */
    private String acptdnpyno;

    /**
     * Database Column Remarks: 委託相手AP基本コード
     *
     */
    private String itakuaiteapkihoncd;

    /**
     * Database Column Remarks: 委託相手ショップコード
     *
     */
    private String itakuaiteshopcd;

    /**
     * Database Column Remarks: 委託相手受注伝票番号
     *
     */
    private String itakuaiteacptdnpyno;

    /**
     * Database Column Remarks: 委託相手枝番号
     *
     */
    private String itakuaiteedano;

    /**
     * Database Column Remarks: 委託相手受注伝票明細番号
     *
     */
    private String itakuaiteacptdnpymeisaino;

    /**
     * Database Column Remarks: 委託相手受注伝票明細枝番号
     *
     */
    private String itakuaiteacptdnpymeisaiedano;

    /**
     * Database Column Remarks: 大分類コード
     *
     */
    private String lctgrycd;

    /**
     * Database Column Remarks: 中分類コード
     *
     */
    private String mctgrycd;

    /**
     * Database Column Remarks: 小分類コード
     *
     */
    private String sctgrycd;

    /**
     * Database Column Remarks: 商品コード
     *
     */
    private String shncd;

    /**
     * Database Column Remarks: 配布物AP基本コード
     *
     */
    private String haifubutsuapkihoncd;

    /**
     * Database Column Remarks: 配布物ショップコード
     *
     */
    private String haifubutsushopcd;

    /**
     * Database Column Remarks: 配布物コード
     *
     */
    private String haifubutsucd;

    /**
     * Database Column Remarks: 型式
     *
     */
    private String katasiki;

    /**
     * Database Column Remarks: 計上区分
     *
     */
    private String keijokbn;

    /**
     * Database Column Remarks: 受注数量
     *
     */
    private String acptsuryo;

    /**
     * Database Column Remarks: 標準単価
     *
     */
    private String hyojununt;

    /**
     * Database Column Remarks: 受注単価
     *
     */
    private String acptunt;

    /**
     * Database Column Remarks: 受注金額
     *
     */
    private String acptkin;

    /**
     * Database Column Remarks: 受注単価消費税
     *
     */
    private String acptuntshohitaxkin;

    /**
     * Database Column Remarks: 受注消費税
     *
     */
    private String acptshohitaxkin;

    /**
     * Database Column Remarks: 委託会社受注単価
     *
     */
    private String itakucmpnyacptunt;

    /**
     * Database Column Remarks: 委託会社受注単価消費税金額
     *
     */
    private String itakucmpnyacptuntshohitaxkin;

    /**
     * Database Column Remarks: 委託会社消費税金額
     *
     */
    private String itakucmpnyacptshohitaxkin;

    /**
     * Database Column Remarks: 納品数量
     *
     */
    private String nohinsuryo;

    /**
     * Database Column Remarks: 納品単価
     *
     */
    private String nohinunt;

    /**
     * Database Column Remarks: 納品金額
     *
     */
    private String nohinkin;

    /**
     * Database Column Remarks: 納品単価消費税金額
     *
     */
    private String nohinuntshohitaxkin;

    /**
     * Database Column Remarks: 納品消費税金額
     *
     */
    private String nohinshohitaxkin;

    /**
     * Database Column Remarks: 残回収
     *
     */
    private String zankaisu;

    /**
     * Database Column Remarks: 総回数
     *
     */
    private String soukaisu;

    /**
     * Database Column Remarks: 税区分
     *
     */
    private String taxkbn;

    /**
     * Database Column Remarks: 有償区分
     *
     */
    private String yusyokbn;

    /**
     * Database Column Remarks: 明細区分
     *
     */
    private String meisaikbn;

    /**
     * Database Column Remarks: 入力明細区分
     *
     */
    private String nyuryokumeisaiflg;

    /**
     * Database Column Remarks: 単品明細フラグ
     *
     */
    private String tanpinmeisaiflg;

    /**
     * Database Column Remarks: セット商品コード
     *
     */
    private String stshncd;

    /**
     * Database Column Remarks: 明細備考
     *
     */
    private String meisaibiko;

    /**
     * Database Column Remarks: データ作成元区分
     *
     */
    private String datsksmotokbn;

    /**
     * Database Column Remarks: 有効区分
     *
     */
    private String yukokbn;

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
     * Database Column Remarks: 商品AP基本コード
     *
     */
    private String shnapkihoncd;

    /**
     * Database Column Remarks: 商品名称略称
     *
     */
    private String shnnmryakusho;

    /**
     * Database Column Remarks: 価格区分
     *
     */
    private String pricekbn;

    /**
     * Database Column Remarks: 標準単価
     *
     */
    private String hyojunpricekirikaedd;

    /**
     * Database Column Remarks: 現在標準価格
     *
     */
    private String genzhyojunprice;

    /**
     * Database Column Remarks: 新標準価格
     *
     */
    private String shinhyojunprice;

    /**
     * Database Column Remarks: 現在持ち帰り価格
     *
     */
    private String genzmochikaeriprice;

    /**
     * Database Column Remarks: 新持ち帰り価格
     *
     */
    private String shinmochikaeriprice;

    /**
     * Database Column Remarks: オープン価格切替日
     *
     */
    private String opnpricekirikaedd;

    /**
     * Database Column Remarks: 現在オープン価格
     *
     */
    private String genzopnprice;

    /**
     * Database Column Remarks: 新オープン価格
     *
     */
    private String shinopnprice;

    /**
     * Database Column Remarks: 現在オープン持ち帰り価格
     *
     */
    private String genzopnmochikaeriprice;

    /**
     * Database Column Remarks: 新オープン持ち帰り価格
     *
     */
    private String shinopnmochikaeriprice;

    /**
     * Database Column Remarks: 販促区分
     *
     */
    private String hansokukbn;

    /**
     * Database Column Remarks: 販促開始日
     *
     */
    private String hansokustrtdd;

    /**
     * Database Column Remarks: 販促終了日
     *
     */
    private String hansokushrydd;

    /**
     * Database Column Remarks: 販促配達価格
     *
     */
    private String hansokudlvryprice;

    /**
     * Database Column Remarks: 販促持ち帰り価格
     *
     */
    private String hansokumochikaeriprice;

    /**
     * Database Column Remarks: 販促優先区分
     *
     */
    private String hansokuyusenkbn;

    /**
     * Database Column Remarks: 在庫単価
     *
     */
    private String zaikount;

    /**
     * Database Column Remarks: 色
     *
     */
    private String iro;

    /**
     * Database Column Remarks: サイズ
     *
     */
    private String sz;

    /**
     * Database Column Remarks: 売上取扱単位
     *
     */
    private String uritrtskitani;

    /**
     * Database Column Remarks: 売上入数
     *
     */
    private String uriirisu;

    /**
     * Database Column Remarks: 売上単位
     *
     */
    private String uritani;

    /**
     * Database Column Remarks: 税設定区分
     *
     */
    private String taxsetteikbn;

    /**
     * Database Column Remarks: 商品税区分
     *
     */
    private String shntaxkbn;

    /**
     * Database Column Remarks: 在庫アラーム数量
     *
     */
    private String zaikoaramusuryo;

    /**
     * Database Column Remarks: 取り扱い区分
     *
     */
    private String trtskikbn;

    /**
     * Database Column Remarks: 特別単価区分
     *
     */
    private String tokubetsuuntkbn;

    /**
     * Database Column Remarks: 適用開始日
     *
     */
    private String tekiyostrtdd;

    /**
     * Database Column Remarks: 適用終了日
     *
     */
    private String tekiyoshrydd;

    /**
     * Database Column Remarks: 配達価格
     *
     */
    private String dlvryprice;

    /**
     * Database Column Remarks: 持ち帰り価格
     *
     */
    private String mochikaeriprice;

    /**
     * Database Column Remarks: 計上区分名称
     *
     */
    private String keijokbnnm;

    /**
     * Database Column Remarks: 配布物名称
     *
     */
    private String haifubutsunm;

    /**
     * Database Column Remarks: 総数
     *
     */
    private String sokazu;

    /**
     * Database Column Remarks: 商品名称略称
     *
     */
    private String shnnm;

    /**
     * Database Column Remarks: 標準価格
     *
     */
    private String hyojunprice;

    /**
     * Database Column Remarks: 売上伝票
     *
     */
    private String uridnpyno;

    /**
     * Database Column Remarks: 売上伝票番号
     *
     */
    private String uridnpyedano;

    /**
     * Database Column Remarks: 売上伝票明細番号
     *
     */
    private String uridnpymeisaino;

    /**
     * Database Column Remarks: 売上伝票明細枝番号
     *
     */
    private String uridnpymeisaiedano;

    /**
     * Database Column Remarks: 登録者AP基本コード
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
     * Database Column Remarks: 更新者AP基本コード
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
     * Database Column Remarks: 登録者名
     *
     */
    private String toshamei;

    /**
     * Database Column Remarks: 更新者名
     *
     */
    private String koshamei;

    /**
     * Database Column Remarks: セット区分
     *
     */
    private String stkbn;

    /**
     * Database Column Remarks: セット適用開始日
     *
     */
    private String set_tekiyostrtdd;

    /**
     * Database Column Remarks: セット適用終了日
     *
     */
    private String set_tekiyoshrydd;

    /**
     * Database Column Remarks: セット配達価格
     *
     */
    private String set_dlvryprice;

    /**
     * Database Column Remarks: セット持ち帰り価格
     *
     */
    private String set_mochikaeriprice;

    /**
     * Database Column Remarks: 料金種別特別単価フラグ
     *
     */
    private String rknkindtokubetsuuntflg;

}
