package jp.co.aquaclara.accis.web.domain.dto.master;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ショップ商品マスタ－Dtoクラス.
 */
@Data
@AllArgsConstructor
public class S02M043TDto implements Serializable {

    public S02M043TDto() {
        super();
    }

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** ＡＰ基本コード */
    private String apkihoncd;
    /** ショップコード */
    private String shopcd;
    /** 商品ＡＰ基本コード */
    private String shnapkihoncd;
    /** 商品コード */
    private String shncd;
    /** 販売開始日 */
    private String hanbaistrtdd;
    /** 販売終了日 */
    private String hanbaishrydd;
    /** 外部連携商品コード */
    private String gaiburenkeishncd;
    /** 本部販売管理商品コード */
    private String honbuhanbaikanrishncd;
    /** マージン対象区分 */
    private String mgntrgtkbn;
    /** 商品区分 */
    private String shnkbn;
    /** モバイル利用有無 */
    private String mobileriyoumu;
    /** 税設定区分 */
    private String taxsetteikbn;
    /** 税区分 */
    private String taxkbn;
    /** 仕入取扱単位 */
    private String siiretrtskitani;
    /** 仕入入数 */
    private String siireirisu;
    /** 仕入単位 */
    private String siiretani;
    /** 仕入単価 */
    private String siireunt;
    /** 発注ロット数 */
    private String hacchurottokazu;
    /** 売上取扱単位 */
    private String uritrtskitani;
    /** 売上入数 */
    private String uriirisu;
    /** 売上単位 */
    private String uritani;
    /** オープン価格切替日 */
    private String opnpricekirikaedd;
    /** 現オープン価格 */
    private String genzopnprice;
    /** 新オープン価格 */
    private String shinopnprice;
    /** 現オープン持ち帰り価格 */
    private String genzopnmochikaeriprice;
    /** 新オープン持ち帰り価格 */
    private String shinopnmochikaeriprice;
    /** 販促区分 */
    private String hansokukbn;
    /** 販促開始日 */
    private String hansokustrtdd;
    /** 販促終了日 */
    private String hansokushrydd;
    /** 販促名称 */
    private String hansokunm;
    /** 販促配達価格 */
    private String hansokudlvryprice;
    /** 販促持ち帰り価格 */
    private String hansokumochikaeriprice;
    /** 販促優先区分 */
    private String hansokuyusenkbn;
    /** 在庫管理対象フラグ */
    private String zaikokanritrgtflg;
    /** 原価計算区分 */
    private String genkakeisankbn;
    /** 標準原価 */
    private String hyojungenka;
    /** 在庫単価 */
    private String zaikount;
    /** 仕入単位変換区分 */
    private String siiretanihenkankbn;
    /** 在庫アラーム数量 */
    private String zaikoaramusuryo;
    /** 備考 */
    private String biko;
    /** 会員サイト連携区分 */
    private String kaiinsaitorenkeikbn;
    /** 有効区分 */
    private String yukokbn;
    /** 登録者ＡＰ基本コード */
    private String toshaapkihoncd;
    /** 登録者ショップコード */
    private String toshashopcd;
    /** 登録者コード */
    private String toshacd;
    /** 登録システム日付 */
    private String inptsystmymd;
    /** 登録システム時間 */
    private String inptsystmjikan;
    /** 登録運用日付 */
    private String inptunyoymd;
    /** 登録ＩＰアドレス */
    private String inptipadrse;
    /** 登録プログラムＩＤ */
    private String inptpgmid;
    /** 更新者ＡＰ基本コード */
    private String koshaapkihoncd;
    /** 更新者ショップコード */
    private String koshashopcd;
    /** 更新者コード */
    private String koshacd;
    /** 更新システム日付 */
    private String kshnsystmymd;
    /** 更新システム時間 */
    private String kshnsystmjikan;
    /** 更新運用日付 */
    private String kshnunyoymd;
    /** 更新ＩＰアドレス */
    private String kshnipadrse;
    /** 更新プログラムＩＤ */
    private String kshnpgmid;
    /** 現商品名称 */
    private String genzshnnm;

}
