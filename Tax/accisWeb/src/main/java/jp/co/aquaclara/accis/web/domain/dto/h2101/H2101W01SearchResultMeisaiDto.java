package jp.co.aquaclara.accis.web.domain.dto.h2101;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * H2101w01_入出庫入力検索結果明細DTO.
 * 
 * @author accisdev021
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class H2101W01SearchResultMeisaiDto {

    // 商品コード
    private String shncd;
    // 商品略称名
    private String shnnm;
    // 色
    private String iro;
    // サイズ
    private String sznm;
    // 型式
    private String katasiki;
    // 型式名称
    private String katasikimei;
    // 数量
    private String suryo;
    // 数量単位
    private String siiretrtskitaninm;
    // 入数
    private String irisu;
    // 入数単位
    private String siiretaninm;
    // 総数
    private String sokazu;
    // 単価
    private String unt;
    // 金額
    private String kin;
    // 備考
    private String biko;
    // 更新システム日付
    private String kshnsysmymd;
    // 更新システム時間
    private String kshnsystmjikan;
    // 入出庫伝票明細番号
    private String nyushukkodnpymeisaino;
    // 大分類コード
    private String lctgrycd;
    // 中分類コード
    private String mctgrycd;
    // 小分類コード
    private String sctgrycd;
    // 明細備考
    private String meisaibiko;
    // 商品AP基本コード
    private String shnapkihoncd;
    // 価格区分
    private String pricekbn;
    // 標準価格切替日
    private String hyojunpricekirikaedd;
    // 現標準価格
    private String genzhyojunprice;
    // 新標準価格
    private String shinhyojunprice;
    // 現持ち帰り価格
    private String genzmochikaeriprice;
    // 新持ち帰り価格
    private String shinmochikaeriprice;
    // オープン価格切替日
    private String opnpricekirikaedd;
    // 現オープン価格
    private String genzopnprice;
    // 新オープン価格
    private String shinopnprice;
    // 現オープン持ち帰り価格
    private String genzopnmochikaeriprice;
    // 新オープン持ち帰り価格
    private String shinopnmochikaeriprice;
    // 販促区分
    private String hansokukbn;
    // 販促開始日
    private String hansokustrtdd;
    // 販促終了日
    private String hansokushrydd;
    // 販促配達価格
    private String hansokudlvryprice;
    // 販促持ち帰り価格
    private String hansokumochikaeriprice;
    // 在庫単価
    private String zaikount;
    // 売上取扱単位名称
    private String uritrtskitani;
    // 売上入数
    private String uriirisu;
    // 売上単位名称
    private String uritani;
    // 在庫管理対象フラグ
    private String zaikokanritrgtflgshmv;
    // 在庫管理区分
    private String zaikokanrikbnshopm;
    // 仕入取扱単位名称
    private String siiretrtskitani;
    // 仕入入数
    private String siireirisu;
    // 仕入単位名称
    private String siiretani;
    // 税設定区分
    private String taxsetteikbn;
    // 税区分
    private String taxkbn;
    // 在庫アラーム数量
    private String zaikoaramusuryo;
    // 取扱区分
    private String trtskikbn;
    // 仕入総数
    private String siiresokazu;
    // 売上総数
    private String urisokazu;
}
