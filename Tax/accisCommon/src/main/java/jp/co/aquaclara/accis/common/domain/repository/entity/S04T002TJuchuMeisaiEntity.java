package jp.co.aquaclara.accis.common.domain.repository.entity;

import java.io.Serializable;
import lombok.Data;

@Data
public class S04T002TJuchuMeisaiEntity extends S04T002TEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 商品ＡＰ基本コード(SHNAPKIHONCD)
     */
    private String shnapkihoncd;
    /**
     * 商品名称略称(SHNNMRYAKUSHO)
     */
    private String shnnmryakusho;
    /**
     * 価格区分(PRICEKBN)
     */
    private String pricekbn;
    /**
     * 標準価格切替日(HYOJUNPRICEKIRIKAEDD)
     */
    private String hyojunpricekirikaedd;
    /**
     * 現標準価格(GENZHYOJUNPRICE)
     */
    private String genzhyojunprice;
    /**
     * 新標準価格(SHINHYOJUNPRICE)
     */
    private String shinhyojunprice;
    /**
     * 現在持ち帰り価格(GENZMOCHIKAERIPRICE)
     */
    private String genzmochikaeriprice;
    /**
     * 新持ち帰り価格(SHINMOCHIKAERIPRICE)
     */
    private String shinmochikaeriprice;
    /**
     * オープン価格切替日(OPNPRICEKIRIKAEDD)
     */
    private String opnpricekirikaedd;
    /**
     * 現オープン価格(GENZOPNPRICE)
     */
    private String genzopnprice;
    /**
     * 新オープン価格(SHINOPNPRICE)
     */
    private String shinopnprice;
    /**
     * 現オープン持ち帰り価格(GENZOPNMOCHIKAERIPRICE)
     */
    private String genzopnmochikaeriprice;
    /**
     * 新オープン持ち帰り価格(SHINOPNMOCHIKAERIPRICE)
     */
    private String shinopnmochikaeriprice;
    /**
     * 販促区分(HANSOKUKBN)
     */
    private String hansokukbn;
    /**
     * 販促開始日(HANSOKUSTRTDD)
     */
    private String hansokustrtdd;
    /**
     * 販促終了日(HANSOKUSHRYDD)
     */
    private String hansokushrydd;
    /**
     * 販促配達価格(HANSOKUDLVRYPRICE)
     */
    private String hansokudlvryprice;
    /**
     * 販促持ち帰り価格(HANSOKUMOCHIKAERIPRICE)
     */
    private String hansokumochikaeriprice;
    /**
     * 販促優先区分(HANSOKUYUSENKBN)
     */
    private String hansokuyusenkbn;
    /**
     * 在庫単価(ZAIKOUNT)
     */
    private String zaikount;
    /**
     * 色(IRO)
     */
    private String iro;
    /**
     * サイズ(SZ)
     */
    private String sz;
    /**
     * 売上取扱単位(URITRTSKITANI)
     */
    private String uritrtskitani;
    /**
     * 売上入数(URIIRISU)
     */
    private String uriirisu;
    /**
     * 売上単位(URITANI)
     */
    private String uritani;
    /**
     * 税設定区分(TAXSETTEIKBN)
     */
    public String taxsetteikbn;
    /**
     * 商品税区分(SHNTAXKBN)
     */
    private String shntaxkbn;
    /**
     * 在庫アラーム数量(ZAIKOARAMUSURYO)
     */
    private String zaikoaramusuryo;
    /**
     * 取扱区分(TRTSKIKBN)
     */
    private String trtskikbn;
    /**
     * 特別単価区分(TOKUBETSUUNTKBN)
     */
    private String tokubetsuuntkbn;
    /**
     * 適用開始日(TEKIYOSTRTDD)
     */
    private String tekiyostrtdd;
    /**
     * 適用終了日(TEKIYOSHRYDD)
     */
    private String tekiyoshrydd;
    /**
     * 配達価格(DLVRYPRICE)
     */
    private String dlvryprice;
    /**
     * 持ち帰り価格(MOCHIKAERIPRICE)
     */
    private String mochikaeriprice;
    /**
     * 計上区分名称(KEIJOKBNNM)
     */
    private String keijokbnnm;
    /**
     * 配布物名称(HAIFUBUTSUNM)
     */
    private String haifubutsunm;
    /**
     * 総数(SOKAZU)
     */
    private String sokazu;
    /**
     * 商品名称(SHNNM)
     */
    public String shnnm;
    /**
     * 標準価格(HYOJUNPRICE)
     */
    public String hyojunprice;
    /**
     * 登録者名(TOSHAMEI)
     */
    private String toshamei;
    /**
     * 更新者名(KOSHAMEI)
     */
    private String koshamei;
    /**
     * セット区分(STKBN)
     */
    private String stkbn;
    /**
     * 得意先別セット商品マスタ.適用開始日(SET_TEKIYOSTRTDD)
     */
    private String setTekiyostrtdd;
    /**
     * 得意先別セット商品マスタ.適用終了日(SET_TEKIYOSHRYDD)
     */
    private String setTekiyoshrydd;
    /**
     * 得意先別セット商品マスタ.配達価格(SET_DLVRYPRICE)
     */
    private String setDlvryprice;
    /**
     * 得意先別セット商品マスタ.持ち帰り価格(SET_MOCHIKAERIPRICE)
     */
    private String setMochikaeriprice;
    /**
     * 料金種別特別単価フラグ(RKNKINDTOKUBETSUUNTFLG)
     */
    private String rknkindtokubetsuuntflg;
}
