package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntityKey;
import lombok.Data;

/**
 * Database Table Remarks: 請求残高テーブル
 *
 * This class corresponds to the database table S07T001T
 */
@Data
public class S07T001TB1501W21Entity extends S07T001TEntityKey implements Serializable {

    /**
     * Database Column Remarks: 得意先コード
     */
    private String tokuisakicd;
    /**
     * Database Column Remarks: 得意先名称
     */
    private String tokuisakinm1;
    /**
     * Database Column Remarks: 事業所コード
     */
    private String jigyoshocd;
    /**
     * Database Column Remarks: 事業所名称
     */
    private String jigyoshonm;
    /**
     * Database Column Remarks: 得意先状態名
     */
    private String tokuisakijotainm;
    /**
     * Database Column Remarks: 状態ステータス名
     */
    private String jotaistsnm;
    /**
     * Database Column Remarks: 有効区分
     */
    private String yukokbn;
    /**
     * Database Column Remarks: 請求先コード
     */
    private String skysakicd;
    /**
     * Database Column Remarks: 請求区分
     */
    private String skykbn;
    /**
     * Database Column Remarks: 請求先名称
     */
    private String skysakinmn1;
    /**
     * Database Column Remarks: 前回繰越金額
     */
    private String znkkurikoshikin;
    /**
     * Database Column Remarks: 今回入金金額
     */
    private String konkainyknkin;
    /**
     * Database Column Remarks: 今回税抜売上金額
     */
    private String konkaitaxnukiurikin;
    /**
     * Database Column Remarks: 今回消費税金額
     */
    private String konkaishohitaxkin;
    /**
     * Database Column Remarks: 今回税抜売上金額
     */
    private String taxkomiurikeikin;
    /**
     * Database Column Remarks: 税込請求残高金額
     */
    private String taxkomiskyzdkin;
    /**
     * Database Column Remarks: 8%税込利用額計
     */
    private String n8perzeikomiriyogaku;
    /**
     * Database Column Remarks: 10%税込利用額計
     */
    private String n10perzeikomiriyogaku;
    /**
     * Database Column Remarks: 繰越金額
     */
    private String kurikoshikin;
    /**
     * 検索条件： 請求締年月
     */
    private String shimedydm;
    /**
     * 検索条件： 請求締日
     */
    private String shimedd;
    /**
     * 検索条件： 回収方法区分
     */
    private String kaishuhohokbn;
    /**
     * 検索条件： 連番
     */
    private String renban;

    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", tokuisakicd=").append(tokuisakicd);
        sb.append(", tokuisakinm1=").append(tokuisakinm1);
        sb.append(", jigyoshocd=").append(jigyoshocd);
        sb.append(", jigyoshonm=").append(jigyoshonm);
        sb.append(", tokuisakijotainm=").append(tokuisakijotainm);
        sb.append(", jotaistsnm=").append(jotaistsnm);
        sb.append(", yukokbn=").append(yukokbn);
        sb.append(", skysakicd=").append(skysakicd);
        sb.append(", skykbn=").append(skykbn);
        sb.append(", skysakinmn1=").append(skysakinmn1);
        sb.append(", znkkurikoshikin=").append(znkkurikoshikin);
        sb.append(", konkainyknkin=").append(konkainyknkin);
        sb.append(", konkaitaxnukiurikin=").append(konkaitaxnukiurikin);
        sb.append(", konkaishohitaxkin=").append(konkaishohitaxkin);
        sb.append(", taxkomiurikeikin=").append(taxkomiurikeikin);
        sb.append(", taxkomiskyzdkin=").append(taxkomiskyzdkin);
        sb.append(", n8perzeikomiriyogaku=").append(n8perzeikomiriyogaku);
        sb.append(", n10perzeikomiriyogaku=").append(n10perzeikomiriyogaku);
        sb.append(", kurikoshikin=").append(kurikoshikin);
        sb.append("]");
        return sb.toString();
    }
}
