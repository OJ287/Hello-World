package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M048TEntityKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 設置サーバ明細Entity.<br>
 * <br>
 * 「データアクセス設計書 S02M048T_得意先設置サーバー管理マスタ_サーバー関連表示情報取得」
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class S02M048TInstalledServerDetailEntity extends S02M048TEntityKey implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 得意先設置サーバ管理マスタ.商品コード */
    private String shncd;
    /** 商品マスタビュー.現商品名称略称 */
    private String genzshnnmryakusho;
    /** 商品マスタビュー.新商品名称略称 */
    private String shinshnnmryakusho;
    /** 商品マスタビュー.商品名称切替日 */
    private String shnnmkirikaedd;
    /** 商品マスタビュー.色 */
    private String iro;
    /** 商品マスタビュー.サイズ */
    private String sznm;
    /** 商品マスタビュー.売上取扱単位 */
    private String uritani;
    /** 商品マスタビュー.売上入数 */
    private String uriirisu;
    /** 商品マスタビュー.売上取扱単位 */
    private String uritaninm;
    /** 商品マスタビュー.新標準価格 */
    private String shinhyojunprice;
    /** 商品マスタビュー.現標準価格 */
    private String genzhyojunprice;
    /** 商品マスタビュー.大分類コード */
    private String lctgrycd;
    /** 商品マスタビュー.中分類コード */
    private String mctgrycd;
    /** 商品マスタビュー.小分類コード */
    private String sctgrycd;
    /** 商品マスタビュー.現価格区分 */
    private String genzpricekbn;
    /** 商品マスタビュー.新価格区分 */
    private String shinpricekbn;
    /** 商品マスタビュー.標準価格切替日 */
    private String hyojunpricekirikaedd;
    /** 商品マスタビュー.現標準持ち帰り価格 */
    private String genzmochikaeriprice;
    /** 商品マスタビュー.新標準持ち帰り価格 */
    private String shinmochikaeriprice;
    /** 商品マスタビュー.ショップ価格切替日 */
    private String shoppricekirikaedd;
    /** 商品マスタビュー.現ショップ価格 */
    private String genzshopprice;
    /** 商品マスタビュー.現ショップ持ち帰り価格 */
    private String genzshopmochikaeriprice;
    /** 商品マスタビュー.新ショップ価格 */
    private String shinshopprice;
    /** 商品マスタビュー.新ショップ持ち帰り価格 */
    private String shinshopmochikaeriprice;
    /** 商品マスタビュー.販促開始日 */
    private String hansokustrtdd;
    /** 商品マスタビュー.販促終了日 */
    private String hansokushrydd;
    /** 商品マスタビュー.販促配達価格 */
    private String hansokudlvryprice;
    /** 商品マスタビュー.販促持ち帰り価格 */
    private String hansokumochikaeriprice;
    /** 得意先別商品別単価マスタ.得意先価格開始日 */
    private String tekiyostrtdd;
    /** 得意先別商品別単価マスタ.得意先価格開始日 */
    private String tekiyoshrydd;
    /** 得意先別商品別単価マスタ.得意先価格 */
    private String dlvryprice;
    /** 得意先別商品別単価マスタ.得意先持ち帰り価格 */
    private String mochikaeriprice;
    /** 商品マスタビュー.在庫単価 */
    private String zaikount;
    /** 商品マスタビュー.商品税区分 */
    private String taxkbn;
    /** 商品マスタビュー.商品ＡＰ基本コード */
    private String shnapkihoncd;
    /** 得意先マスタ.料金種別特別単価フラグ */
    private String rknkindtokubetsuuntflg;

}
