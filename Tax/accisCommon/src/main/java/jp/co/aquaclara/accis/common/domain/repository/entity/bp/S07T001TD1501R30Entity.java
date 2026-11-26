package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntityKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 請求書Entity.
 * 
 * @author accisdev047
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class S07T001TD1501R30Entity extends S07T001TEntityKey implements Serializable {

    private static final long serialVersionUID = 1L;
    /** 請求締日 */
    private String skyshimeday;
    /** 請求締年月日 */
    private String skyshimedate;
    /** 対象データ */
    private String trgtdat;
    /** 請求金額ゼロ円の請求（明細あり） */
    private String skykinn0ensky1;
    /** 請求金額ゼロ円の請求（明細なし） */
    private String skykinn0ensky2;
    /** 請求金額ゼロ円の請求（過入金） */
    private String skykinn0ensky3;
    /** 印刷種類（請求書） */
    private String skyshoprntkbn;
    /** 印刷種類（控え） */
    private String hikaeprntkbn;
    /** 印刷種類（領収書） */
    private String ryoshushoprntkbn;
    /** 出力順 */
    private String otptjun;
    /** ログイン担当者AP基本コード */
    private String loginUserapkihoncd;
    /** ログイン担当者ショップコード */
    private String loginUsershopcd;
    /** ログイン担当者コード */
    private String loginUsertnshacd;
    /** ログイン担当者事業者コード */
    private String loginUserjigyoshocd;
    /** 請求先コード */
    private String skysakicd;
    /** 締年月 */
    private String shimedydm;
    /** 締日 */
    private String shimedd;

    /**
     * toString
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", skyshimeday=").append(skyshimeday);
        sb.append(", skyshimedate=").append(skyshimedate);
        sb.append(", trgtdat=").append(trgtdat);
        sb.append(", skykinn0ensky1=").append(skykinn0ensky1);
        sb.append(", skykinn0ensky2=").append(skykinn0ensky2);
        sb.append(", skykinn0ensky3=").append(skykinn0ensky3);
        sb.append(", skyshoprntkbn=").append(skyshoprntkbn);
        sb.append(", hikaeprntkbn=").append(hikaeprntkbn);
        sb.append(", ryoshushoprntkbn=").append(ryoshushoprntkbn);
        sb.append(", otptjun=").append(otptjun);
        sb.append(", loginUserapkihoncd=").append(loginUserapkihoncd);
        sb.append(", loginUsershopcd=").append(loginUsershopcd);
        sb.append(", loginUsertnshacd=").append(loginUsertnshacd);
        sb.append(", loginUserjigyoshocd=").append(loginUserjigyoshocd);
        sb.append(", skysakicd=").append(skysakicd);
        sb.append(", shimedydm=").append(shimedydm);
        sb.append(", shimedd=").append(shimedd);
        sb.append("]");
        return sb.toString();
    }
}
