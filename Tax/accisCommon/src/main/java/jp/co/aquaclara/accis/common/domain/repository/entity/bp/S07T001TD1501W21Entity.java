package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntityKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 請求関連処理（請求一覧表）-請求一覧表検索結果Entity
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class S07T001TD1501W21Entity extends S07T001TEntityKey implements Serializable {

    /**
     * 請求先コード
     */
    private String skysakicd;
    /**
     * 請求先名称
     */
    private String skysakinmn1;
    /**
     * 事業所コード
     */
    private String jigyoshocd;
    /**
     * 今回締日
     */
    private String konkaishimedd;
    /**
     * 回収方法
     */
    private String kaishuhohokbn;
    /**
     * 回収方法名称
     */
    private String kaishuhohokbnnm;
    /**
     * 滞留区分
     */
    private String tairyukbn;
    /**
     * 滞留区分名称
     */
    private String tairyukbnnm;
    /**
     * 処理状態
     */
    private String status;
    /**
     * 営業担当者コード
     */
    private String eigyotnshacd;

    /**
     * Database Column Remarks: 回収担当者コード
     *
     */
    private String kaishutnshacd;
    /**
     * 請求先管理区分
     */
    private String skysakikanrikbn;
    /**
     * カナ名称
     */
    private String skysakinmkana;
    /**
     * 得意先有(あり）
     */
    private String ttumu1;
    /**
     * 得意先有(なし）
     */
    private String ttumu2;
    /**
     * 請求区分（親子）
     */
    private String skykbn1;
    /**
     * 請求区分（一般）
     */
    private String skykbn2;
    /**
     * 早出し
     */
    private String hatadasiflg;
    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * 文字列変換
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", skysakicd=").append(skysakicd);
        sb.append(", skysakinmn1=").append(skysakinmn1);
        sb.append(", jigyoshocd=").append(jigyoshocd);
        sb.append(", konkaishimedd=").append(konkaishimedd);
        sb.append(", kaishuhohokbn=").append(kaishuhohokbn);
        sb.append(", kaishuhohokbnnm=").append(kaishuhohokbnnm);
        sb.append(", tairyukbn=").append(tairyukbn);
        sb.append(", tairyukbnnm=").append(tairyukbnnm);
        sb.append(", status=").append(status);
        sb.append(", eigyotnshacd=").append(eigyotnshacd);
        sb.append(", kaishutnshacd=").append(kaishutnshacd);
        sb.append(", skysakikanrikbn=").append(skysakikanrikbn);
        sb.append(", skysakinmkana=").append(skysakinmkana);
        sb.append(", ttumu1=").append(ttumu1);
        sb.append(", ttumu2=").append(ttumu2);
        sb.append(", skykbn1=").append(skykbn1);
        sb.append(", skykbn2=").append(skykbn2);
        sb.append(", hatadasiflg=").append(hatadasiflg);
        sb.append("]");
        return sb.toString();
    }
}
