package jp.co.aquaclara.accis.web.domain.dto.c0802;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * C0802W01_受注明細予約登録検索結果DTO.
 * 
 * @author accisdev041
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class C0802W01SearchResultMeisaiDto {

    // 商品コード
    private String shncd;
    // 商品名称略称
    private String shnnmryakusho;
    // 色
    private String iro;
    // サイズ
    private String sz;
    // 型式
    private String katasiki;
    // 計上区分
    private String keijokbn;
    // 数量
    private BigDecimal acptsuryo;
    // 売上取扱単位
    private String uritrtskitani;
    // 売上入数単位
    private String uritani;
    // 入数
    private String uriirisu;
    // 総数
    private String sokazu;
    // 標準価格
    private String hyojununt;
    // 販売価格
    private String acptunt;
    // 金額
    private Long acptkin;
    // 総回数
    private Short soukaisu;
    // 残回数
    private Short zankaisu;
    // 明細備考
    private String meisaibiko;
    // 大分類コード
    private String lctgrycd;
    // 中分類コード
    private String mctgrycd;
    // 小分類コード
    private String sctgrycd;
    // 行番号（明細）
    private Integer gyono;
    // 更新処理フラグ
    private String updateflg;
    // 受注伝票明細番号
    private Integer acptdnpymeisaino;
    // 受注伝票明細枝番号
    private Short acptdnpymeisaiedano;
    // 税区分
    private String taxkbn;
    // 有償区分
    private String yusyokbn;
    // 明細区分
    private String meisaikbn;
    // 入力明細フラグ
    private String nyuryokumeisaiflg;
    // 単品明細フラグ
    private String tanpinmeisaiflg;
    // セット商品コード
    private String stshncd;
    // 受注単価消費税金額
    private long acptuntshohitaxkin;
    // 受注消費税金額
    private long acptshohitaxkin;
    // 価格区分
    private String pricekbn;
    // 標準価格切替日
    private String hyojunpricekirikaedd;
    // 現標準価格
    private String genzhyojunprice;
    // 新標準価格
    private String shinhyojunprice;
    // 現標準持ち帰り価格
    private String genzmochikaeriprice;
    // 新標準持ち帰り価格
    private String shinmochikaeriprice;
    // ショップ価格切替日
    private String shoppricekirikaedd;
    // 現ショップ価格
    private String genzshopprice;
    // 新ショップ価格
    private String shinshopprice;
    // 現ショップ持ち帰り価格
    private String genzshopmochikaeriprice;
    // 新ショップ持ち帰り価格
    private String shinshopmochikaeriprice;
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
    // 得意先区分
    private String tokuisakikbn;
    // 得意先価格開始日
    private String ttpricestrtdd;
    // 得意先価格終了日
    private String ttpriceshrydd;
    // 得意先価格
    private String ttprice;
    // 得意先持ち帰り価格
    private String ttmochikaeriprice;
}
