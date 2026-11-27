package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import lombok.Data;

@Data
public class S06T002TZ1001W01Entity implements Serializable {
    
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
     * 枝番号
     */
    private String edano;
    
    /**
     * 売上伝票明細番号
     */
    private String uridnpymeisaino;
    
    /**
     * 売上伝票明細枝番号
     */
    private String uridnpymeisaiedano;
    
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
     * 計上区分
     */
    private String keijokbn;
    
    /**
     * 数量
     */
    private String suryo;
    
    /**
     * 標準単価
     */
    private String hyojununt;
    
    /**
     * 単価
     */
    private String unt;
    
    /**
     * 金額
     */
    private String kin;
    
    /**
     * 単価消費税金額
     */
    private String untshohitaxkin;
    
    /**
     * 消費税金額
     */
    private String shohitaxkin;
    
    /**
     * 税区分
     */
    private String taxkbn;
    
    /**
     * 明細区分
     */
    private String meisaikbn;
    
    /**
     * 入力明細フラグ
     */
    private String nyuryokumeisaiflg;
    
    /**
     * 単品明細フラグ
     */
    private String tanpinmeisaiflg;
    
    /**
     * 明細備考
     */
    private String meisaibiko;
    
    /**
     * セット商品コード
     */
    private String stshncd;
    
    /**
     * 型式
     */
    private String katasiki;
    
    /**
     * 仕入数量
     */
    private String siiresuryo;
    
    /**
     * 仕入金額
     */
    private String siirekin;
    
    /**
     * 仕入単価
     */
    private String siireunt;
    
    /**
     * 仕入消費税金額
     */
    private String siireshohitaxkin;
    
    /**
     * 在庫更新済みフラグ
     */
    private String zaikokshnsumiflg;
    
    /**
     * 更新プログラムID
     */
    private String kshnpgmid;
    
    /**
     * 有効区分
     */
    private String yukokbn;
    
    /**
     * 登録者AP基本コード
     */
    private String toshaapkihoncd;
    
    /**
     * 登録者ショップコード
     */
    private String toshashopcd;
    
    /**
     * 登録者コード
     */
    private String toshacd;
    
    /**
     * 登録システム日付
     */
    private String inptsystmymd;
    
    /**
     * 登録システム時間
     */
    private String inptsystmjikan;
    
    /**
     * 登録運用日付
     */
    private String inptunyoymd;
    
    /**
     * 登録IPアドレス
     */
    private String inptipadrse;
    
    /**
     * 登録プログラムID
     */
    private String inptpgmid;
    
    /**
     * 更新者AP基本コード
     */
    private String koshaapkihoncd;
    
    /**
     * 更新者ショップコード
     */
    private String koshashopcd;
    
    /**
     * 更新者コード
     */
    private String koshacd;
    
    /**
     * 更新者システム日付
     */
    private String kshnsystmymd;
    
    /**
     * 更新者システム時間
     */
    private String kshnsystmjikan;
    
    /**
     * 更新者運用日付
     */
    private String kshnunyoymd;
    
    /**
     * 更新者IPアドレス
     */
    private String kshnipadrse;
    
    /**
     * 登録者名
     */
    private String toshamei;
    
    /**
     * 更新者名
     */
    private String koshamei;
    
    /**
     * 商品AP基本コード
     */
    private String shnapkihoncd;
    
    /**
     * 商品名称略称
     */
    private String shnnmryakusho;
    
    /**
     * 価格区分
     */
    private String pricekbn;
    
    /**
     * 標準価格切り替え日
     */
    private String hyojunpricekirikaedd;
    
    /**
     * 現標準価格
     */
    private String genzhyojunprice;
    
    /**
     * 新標準価格
     */
    private String shinhyojunprice;
    
    /**
     * 現お持ち帰り価格
     */
    private String genzmochikaeriprice;
    
    /**
     * 新持ち帰り価格
     */
    private String shinmochikaeriprice;
    
    /**
     * オープン価格切り替え日
     */
    private String opnpricekirikaedd;
    
    /**
     * 現オープン価格
     */
    private String genzopnprice;
    
    /**
     * 新オープン価格
     */
    private String shinopnprice;
    
    /**
     * 現オープン持ち帰り価格
     */
    private String genzopnmochikaeriprice;
    
    /**
     * 新オープン持ち帰り価格
     */
    private String shinopnmochikaeriprice;
    
    /**
     * 販促区分
     */
    private String hansokukbn;
    
    /**
     * 販促開始日
     */
    private String hansokustrtdd;
    
    /**
     * 販促終了日
     */
    private String hansokushrydd;
    
    /**
     * 販促配達価格
     */
    private String hansokudlvryprice;
    
    /**
     * 販促持ち帰り価格
     */
    private String hansokumochikaeriprice;
    
    /**
     * 販促優先区分
     */
    private String hansokuyusenkbn;
    
    /**
     * 在庫
     */
    private String zaikount;
    
    /**
     * 色
     */
    private String iro;
    
    /**
     * サイズ名
     */
    private String sz;
    
    /**
     * 売上取扱単位名称
     */
    private String uritrtskitani;
    
    /**
     * 売上入数
     */
    private String uriirisu;
    
    /**
     * 売上単位名称
     */
    private String uritani;
    
    /**
     * 仕入取扱単位名称
     */
    private String siiretrtskitani;
    
    /**
     * 仕入入数
     */
    private String siireirisu;
    
    /**
     * 仕入単位
     */
    private String siiretani;
    
    /**
     * 在庫管理対象フラグ
     */
    private String zaikokanritrgtflgshmv;
    
    /**
     * 在庫管理区分
     */
    private String zaikokanrikbnshopm;
    
    /**
     * 税設定区分
     */
    private String taxsetteikbn;
    
    /**
     * 在庫アラーム数量
     */
    private String zaikoaramusuryo;
    
    /**
     * 取扱区分
     */
    private String trtskikbn;
    
    /**
     * 適用開始日
     */
    private String tekiyostrtdd;
    
    /**
     * 適用終了日
     */
    private String tekiyoshrydd;
    
    /**
     * 配達価格
     */
    private String dlvryprice;
    
    /**
     * 持ち帰り価格
     */
    private String mochikaeriprice;
    
    /**
     * 総数
     */
    private String sokazu;
    
    /**
     * 仕入総数
     */
    private String siiresokazu;
    
    /**
     * 計上区分名称
     */
    private String keijokbnnm;
    
    
    /**
     */
    private static final long serialVersionUID = 1L;

}
