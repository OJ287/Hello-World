package jp.co.aquaclara.accis.web.domain.dto.c1002;

import java.math.BigDecimal;
import java.util.List;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.JuchuRelationVo;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.TokuisakiTokubetsuUntVo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 売上入力 明細DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class C1002W01UriageMeisaiDto {
    /**
     * AP基本コード
     */
    private String apkihoncd;
    
    /**
     * ショップコード
     */
    private String shopcd;
    
    /**
     * 売上伝票
     */
    private String uridnpyno;
    
    /**
     * 枝番
     */
    private Short edano;
    
    /**
     * 売上伝票明細番号
     */
    private int uridnpymeisaino;
    
    /**
     * 売上伝票明細枝番号
     */
    private short uridnpymeisaiedano;

    /**
     * 明細区分
     */
    private String meisaikbn;

    /**
     * 入力明細フラグ
     */
    private String inptmeisaiflg;

    /**
     * 消費税調整フラグ
     */
    private String zeicyoseiflg;

    /**
     * 大分類コード
     */
    private String lctgrycd;

    /**
     * 中分類コード
     */
    private String mctgrycd;

    /**
     * 小分類コード
     */
    private String sctgrycd;

    /**
     * 商品コード
     */
    private String shncd;
    
    /**
     * 商品名称略称
     */
    private String shnnmryakusho;
    
    /** 
     * 標準単価
     */
    public BigDecimal hyojununt;
    
    /**
     * 色
     */
    private String iro;
    
    /**
     * サイズ名
     */
    private String size;
    
    /**
     * 型式
     */
    private String katasiki;
    
    /**
     * 計上区分
     */
    private String keijokbn;
    
    /**
     * 計上区分名称
     */
    private String keijokbnnm;

    /**
     * 価格区分
     */
    private String pricekbn;
    
    /**
     * 数量
     */
    private BigDecimal suryo;
    
    /**
     * 売上取扱単位名称
     */
    private String uritrtskitani;
    
    /**
     * 売上入数
     */
    private Integer uriirisu;
    
    /**
     * 売上単位名称
     */
    private String uritani;
    
    /**
     * 総数
     */
    private BigDecimal sokazu;
    
    /**
     * 単価
     */
    private BigDecimal unt;
    
    /**
     * 初期表示価格
     */
    private BigDecimal shokidsplyprice;
    
    /**
     * 金額
     */
    private Long kin;
    
    /** 
     * 単価消費税金額
     */
    public Long untshohitaxkin;
    
    /**
     * 売上消費税金額
     */
    private Long shohitaxkinn1;
    
    /**
     * 仕入数量
     */
    private BigDecimal siiresuryo;
    
    /**
     * 仕入取扱単位名称
     */
    private String siiretrtskitani;
    
    /**
     * 仕入入数
     */
    private Integer siireirisu;
    
    /**
     * 仕入単位
     */
    private String siiretani;
    
    /**
     * 仕入総数
     */
    private BigDecimal siiresokazu;
    
    /**
     * 仕入単価
     */
    private BigDecimal siireunt;
    
    /**
     * 仕入金額
     */
    private Long siirekin;
    
    /** 
     * 仕入単価消費税金額
     */
    public Long siireuntshohitaxkin;
    
    /**
     * 仕入消費税金額
     */
    private Long shohitaxkinn2;
    
    /** 
     * 在庫更新済フラグ
     */
    public String zaikokshnsumiflg;

    /**
     * 標準価格切替日
     */
    private String hyojunpricekirikaedd;
    
    /**
     * 現標準価格
     */
    private BigDecimal genzhyojunprice;

    /**
     * 新標準価格
     */
    private BigDecimal shinhyojunprice;

    /**
     * 現持ち帰り価格
     */
    private BigDecimal genzmochikaeriprice;

    /**
     * 新持ち帰り価格
     */
    private BigDecimal shinmochikaeriprice;
    
    /** 
     * 入力明細フラグ
     */
    public String nyuryokumeisaiflg;
    
    /** 
     * 税区分
     */
    public String taxkbn;
    
    /** 
     * 単品明細フラグ
     */
    public String tanpinmeisaiflg;

    /** 
     * セット商品コード
     */
    public String stshncd;
    
    /**
     * 在庫管理対象フラグ
     */
    private String zaikokanritrgtflg;
    
    /**
     * 消費税金額
     */
    private Long shohitaxkin;
    
    /**
     * 受注区分・受注区分詳細・得意先区分・商品大分類コード・商品コード・計上区分の関係クラス
     */
    private JuchuRelationVo relationVo;
    
    /**
     * 商品マスタビュー情報
     */
    private S02V001TEntity shohinModel;
    
    /**
     * セット区分
     */
    private String stkbn;
    
    /**
     * 販促開始日
     */
    private String hansokustrtdd;

    /**
     * 販促終了日
     */
    private String hansokushrydd;

    /**
     * 販促価格
     */
    private BigDecimal hansokuprice;

    /**
     * 販促持ち帰り価格
     */
    private BigDecimal hansokumochikaeriprice;
    
    /**
     * 販促優先区分
     */
    public String hansokuyusenkbn;
    
    /**
     * 得意先価格開始日
     */
    private String ttpricestrtdd;

    /**
     * 得意先価格終了日
     */
    private String ttpriceshrydd;

    /**
     * 得意先価格
     */
    private BigDecimal ttprice;

    /**
     * 得意先持ち帰り価格
     */
    private BigDecimal ttmochikaeriprice;
    
    /**
     * ショップ価格切替日
     */
    private String shoppricekirikaedd;

    /**
     * 現ショップ価格
     */
    private BigDecimal genzshopprice;

    /**
     * 現ショップ持ち帰り価格
     */
    private BigDecimal genzshopmochikaeriprice;

    /**
     * 新ショップ価格
     */
    private BigDecimal shinshopprice;

    /**
     * 新ショップ持ち帰り価格
     */
    private BigDecimal shinshopmochikaeriprice;
    
    /**
     * 商品税区分
     */
    private String shntaxkbn;
    
    /**
     * 得意先別特別単価リスト
     */
    private List<TokuisakiTokubetsuUntVo> tokubetsuUntList;
    
    /**
     * 明細備考
     */
    private String meisaibiko;
}
