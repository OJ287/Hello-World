package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import lombok.Data;

/**
 * 入金消込履歴一覧情報Entity
 * 
 * @author accisdev043
 */
@Data
public class S07T001TC1006W02ResultEntity implements Serializable {

    /** 請求締日 */
    private String shimeymd;
    /** 請求書No */
    private String skyshono;
    /** 得意先コード */
    private String tokuisakicd;
    /** 得意先名称 */
    private String tokuisakinmn1;
    /** 税込売上金額 */
    private long uriage;
    /** 消込残額 */
    private long keshikomizd;
    /** 入金日 */
    private String nykndd;
    /** 消込日 */
    private String kshkmdd;
    /** 入金種別 */
    private String nykinkind;
    /** 消込入金種別 */
    private String kskinkind;
    /** 消込金額 */
    private int keshikomikin;
    /** 入金伝票No */
    private String nykndnpyno;
    /** 請求処理実施区分 */
    private String skyprcsjissikbn;

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * toString
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", shimeymd=").append(shimeymd);
        sb.append(", skyshono=").append(skyshono);
        sb.append(", tokuisakicd=").append(tokuisakicd);
        sb.append(", tokuisakinmn1=").append(tokuisakinmn1);
        sb.append(", uriage=").append(uriage);
        sb.append(", keshikomizd=").append(keshikomizd);
        sb.append(", nykndd=").append(nykndd);
        sb.append(", kshkmdd=").append(kshkmdd);
        sb.append(", nykinkind=").append(nykinkind);
        sb.append(", kskinkind=").append(kskinkind);
        sb.append(", keshikomikin=").append(keshikomikin);
        sb.append(", nykndnpyno=").append(nykndnpyno);
        sb.append(", skyprcsjissikbn=").append(skyprcsjissikbn);
        sb.append("]");
        return sb.toString();
    }
}
