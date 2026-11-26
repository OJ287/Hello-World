package jp.co.aquaclara.accis.web.domain.dto.b1501;

import java.math.BigDecimal;
import jp.co.aquaclara.accis.web.domain.dto.common.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * B1501W21_請求入金照会(詳細)検索結果(検索結果一覧エリア)DTO.
 * 
 * @author accisdev003
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class B1501W21M2SearchMeisaiDto extends BaseDto {

    // 売上日
    private String ymd;
    // 商品コード
    private String shncd;
    // 現商品名称略称
    private String genzshnnmryakusho;
    // 新商品名称略称
    private String shinshnnmryakusho;
    // 商品名称切替日
    private String shnnmkirikaedd;
    // 色
    private String iro;
    // サイズ名称
    private String sznm;
    // 型式
    private String katasiki;
    // 計上区分名称
    private String keijokbnnm;
    // 数量
    private String suryo;
    // 売上取扱単位名称
    private String uritrtskitaninm;
    // 売上入数
    private String uriirisu;
    // 総数
    private String sokazu;
    // 単価
    private String unt;
    // 金額
    private String kin;
    // 消費税レート
    private String taxrate;
    // 入力明細フラグ
    private String nyuryokumeisaiflg;
    // 消費税金額
    private String shohitaxkin;
    // 参考消費税金額
    private String sankoushohitaxkin;
    // 入金金額
    private String nyknkin;
    // 明細備考
    private String biko;
    // 売上ポイント
    private String uripoint;
    // 特別ポイント
    private String tkbtpoint;
    // ポイント内訳
    private String pointutwk;
    // 合計ポイント
    private String smpoint;
    // ポイント連携日付
    private String pointrenkeidd;
    // 売上伝票番号
    private String pyno;
    // 枝番号
    private String edano;
    // 売上伝票明細番号
    private String meisaino;
    // 得意先コード
    private String tokuisakicd;
    // 得意先名称1
    private String tokuisakinmn1;
    // 登録システム日付
    private String inptsystmymd;
    // 登録システム時間
    private String inptsystmjikan;
    // 登録者AP基本コード
    private String toshaapkihoncd;
    // 登録者ショップコード
    private String toshashopcd;
    // 登録者コード
    private String toshacd;
    // 担当者名
    private String tnshamei;
    // 請求書出力有無区分
    private String skyshootptarinashikbn;
    // 入金種別名称
    private String nyukinkindnm;
    // 消費税フラグ
    private String taxflag;
    // 8%の売上金額合計
    private BigDecimal n8peruri;
    // 8%の消費税額合計
    private BigDecimal n8pershohzei;
    // 10%の売上金額合計
    private BigDecimal n10peruri;
    // 10%の消費税額合計
    private BigDecimal n10pershohzei;
    // 金額合計1
    private BigDecimal kinsm1;
    // 金額合計2
    private BigDecimal kinsm2;
    // 金額合計3
    private BigDecimal kinsm3;
    // ポイント合計
    private BigDecimal pointsm;
}
