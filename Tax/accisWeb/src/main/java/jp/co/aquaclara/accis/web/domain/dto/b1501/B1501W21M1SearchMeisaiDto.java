package jp.co.aquaclara.accis.web.domain.dto.b1501;

import jp.co.aquaclara.accis.web.domain.dto.common.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * B1501W21_請求入金照会(詳細)検索結果(明細エリア)DTO.
 * 
 * @author accisdev003
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class B1501W21M1SearchMeisaiDto extends BaseDto {

    // 年月日
    private String dydmdd;
    // 得意先状態
    private String tokuisakijotai;
    // 得意先状態名
    private String tokuisakijotainm;
    // 状態ステータス
    private String jotaists;
    // 状態ステータス名
    private String jotaistsnm;
    // 請求先コード
    private String skysakicd;
    // 請求先名称
    private String skysakinm;
    // 得意先コード
    private String tokuisakicd;
    // 得意先名称
    private String tokuisakinm;
    // 事業所コード
    private String jigyoshocd;
    // 事業所名称
    private String jigyoshonm;
    // 前回請求金額
    private String znkskykin;
    // 入金金額
    private String nyukinkin;
    // 今回利用金額
    private String konkairiyokin;
    // 消費税金額
    private String shohitaxkin;
    // 税込ご利用額計
    private String taxkomiskyzkin;
    // 8%税込ご利用額計
    private String n8perzeikomiriyogaku;
    // 10%税込ご利用額計
    private String n10perzeikomiriyogakumei;
    // 今回ご請求金額
    private String konkaiskykinmei;
    // 選択
    private String sentaku;
    // 請求区分
    private String skykbn;
}
