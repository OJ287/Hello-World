package jp.co.aquaclara.accis.web.domain.dto.c0501;

import jp.co.aquaclara.accis.web.domain.dto.common.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 請求先情報DTO.
 * 
 * @author accisdev041
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class C0501W02SkysakiInfoDto extends BaseDto {

    // 照会請求締日明細
    private String skyshimeddmei;
    // 前回請求金額明細/
    private String znkskykinmei;
    // 入金金額明細
    private String nyknkinmei;
    // 売上金額明細
    private String urikinmei;
    // 内消費税金額明細
    private String uchishohitaxkinmei;
    // ８％税込売上額計明細
    private String n8persiirekinmei;
    // １０％税込売上額計明細
    private String n10persiirekinmei;
    // 請求金額明細
    private String skykinmei;

}
