package jp.co.aquaclara.accis.web.controller.c0501.bean;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 得意先情報確認(得意先情報確認)-請求情報一覧Bean
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class C0501W02Meisai2Bean implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** 照会請求締日明細 */
    private String skyshimeddmei;
    
    /**　前回請求金額明細 */
    private String znkskykinmei;
    
    /** 入金金額明細 */
    private String nyknkinmei;
    
    /** 売上金額明細 */
    private String urikinmei;
    
    /** 内消費税金額明細 */
    private String uchishohitaxkinmei;
    
    /** ８％税込売上額計明細 */
    private String n8persiirekinmei;
    
    /** １０％税込売上額計明細 */
    private String n10persiirekinmei;
    
    /** 請求金額明細 */
    private String skykinmei;
    
    /** 請求処理実施区分 */
    private String skyprcsjissikbnmei;
}
