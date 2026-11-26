package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import lombok.Data;

@Data
public class S07T001TZ1503W01Entity  implements Serializable {
    
    /**
     * 請求日
     */
    private String skydd;
    
    /**
     * 請求書番号
     */
    private String skyshono;
    
    /**
     * 得意先コード
     */
    private String tokuisakicd;
    
    
    /**
     * 得意先名
     */
    private String tokuisakinmn1;
    
    /**
     * 当月売上額
     */
    private String totsukiurikin;
    
    /**
     * 未消し残額
     */
    private String keshikomizd;
    
    /**
     * 支払い予定日
     */
    private String shrytidydmdd;
    
    

}