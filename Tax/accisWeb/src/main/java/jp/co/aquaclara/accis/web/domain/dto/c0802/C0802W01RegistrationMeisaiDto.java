package jp.co.aquaclara.accis.web.domain.dto.c0802;

import java.math.BigDecimal;
import java.util.List;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntity;
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
public class C0802W01RegistrationMeisaiDto {

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
    // 単位
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
    // 納品数量
    private BigDecimal nohinsuryo;

    // 商品マスタビュー
    private S02V001TEntity s02v001TEntity;

    // 受注関係Entity
    // 製品水大分類コード
    private String relshnmctgrycd;
    // サーバー大分類コード
    private String relsrvlctgrycd;
    // サーバー中分類コード
    private String relsrvmctgrycd;
    // レギュラーの商品コードリスト
    private List<String> relrgralist;
    // スリムの商品コードリスト
    private List<String> relsrmlist;
    // 得意先区分
    private String reltokuisakikbn;
    // 受注区分
    private String relacptkbn;
    // 受注区分詳細
    private String relacptkbnshs;
    // 計上区分
    private String relkeijokbn;
    // 商品大分類コード
    private String rellctgrycd;
    // 商品中分類コード
    private String relmctgrycd;
    // 商品コード
    private String relshncd;

}
