package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 請求関連処理(請求書)-請求書検索検索Entity
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class S07T001TD1501W31SearchConditionEntity extends S07T001TD1501W00SearchConditionEntity
    implements Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    /**
     * Database Column Remarks: 抽出締日
     *
     */
    private String extractshimedd;

    /**
     * Database Column Remarks: 対象データ
     *
     */
    private String trgtdat;

    /**
     * Database Column Remarks: 請求金額ゼロ円の請求（明細あり）
     *
     */
    private String skykinn0ensky1;

    /**
     * Database Column Remarks: 請求金額ゼロ円の請求（明細なし）
     *
     */
    private String skykinn0ensky2;

    /**
     * Database Column Remarks: 請求金額ゼロ円の請求（過入金）
     *
     */
    private String skykinn0ensky3;

    /**
     * Database Column Remarks: 集金日
     *
     */
    private String shukindd;

    /**
     * Database Column Remarks: 印刷種類（請求書）
     *
     */
    private String skyshoprntkbn;

    /**
     * Database Column Remarks: 印刷種類（控え）
     *
     */
    private String hikaeprntkbn;

    /**
     * Database Column Remarks: 印刷種類（領収書）
     *
     */
    private String ryoshushoprntkbn;

    /**
     * Database Column Remarks: 出力順
     *
     */
    private String otptjun;

    /**
     * Database Column Remarks: 請求書発送委託（対象のみ）
     *
     */
    private String skyshohassouitaku1;

    /**
     * Database Column Remarks: 請求書発送委託（対象を除く）
     *
     */
    private String skyshohassouitaku2;

    /**
     * Database Column Remarks: 営業担当者コード
     *
     */
    private String eigyotnshacd;

    /**
     * Database Column Remarks: 回収担当者コード
     *
     */
    private String kaishutnshacd;

    /**
     * Database Column Remarks: カナ名称
     *
     */
    private String skysakinmkana;

    /**
     * Database Column Remarks: 得意先有無（有）
     *
     */
    private String ttumu1;

    /**
     * Database Column Remarks: 得意先有無（無）
     *
     */
    private String ttumu2;

    /**
     * Database Column Remarks: 請求先管理区部
     *
     */
    private String skysakikanrikbn;

    /**
     * Database Column Remarks: 請求区分（親子）
     *
     */
    private String skykbn1;

    /**
     * Database Column Remarks: 請求区分（一般）
     *
     */
    private String skykbn2;

    /**
     * Database Column Remarks: 早出し
     *
     */
    private String hayadasiflg;

    /**
     * Database Column Remarks: 回収方法コード
     *
     */
    private List<String> kaishuhohocd;

    /**
     * Database Column Remarks: 請求先送付区分(送付する)
     *
     */
    private String skymeisaisofukbn1;

    /**
     * Database Column Remarks: 請求先送付区分（送付しない）
     *
     */
    private String skymeisaisofukbn2;

    /**
     * toString
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", extractshimedd=").append(extractshimedd);
        sb.append(", trgtdat=").append(trgtdat);
        sb.append(", skykinn0ensky1=").append(skykinn0ensky1);
        sb.append(", skykinn0ensky2=").append(skykinn0ensky2);
        sb.append(", skykinn0ensky3=").append(skykinn0ensky3);
        sb.append(", shukindd=").append(shukindd);
        sb.append(", skyshoprntkbn=").append(skyshoprntkbn);
        sb.append(", hikaeprntkbn=").append(hikaeprntkbn);
        sb.append(", ryoshushoprntkbn=").append(ryoshushoprntkbn);
        sb.append(", otptjun=").append(otptjun);
        sb.append(", skyshohassouitaku1=").append(skyshohassouitaku1);
        sb.append(", skyshohassouitaku2=").append(skyshohassouitaku2);
        sb.append(", eigyotnshacd=").append(eigyotnshacd);
        sb.append(", kaishutnshacd=").append(kaishutnshacd);
        sb.append(", skysakinmkana=").append(skysakinmkana);
        sb.append(", ttumu1=").append(ttumu1);
        sb.append(", ttumu2=").append(ttumu2);
        sb.append(", skysakikanrikbn=").append(skysakikanrikbn);
        sb.append(", skykbn1=").append(skykbn1);
        sb.append(", skykbn2=").append(skykbn2);
        sb.append(", hayadasiflg=").append(hayadasiflg);
        sb.append(", kaishuhohocd=").append(kaishuhohocd);
        sb.append(", skymeisaisofukbn1=").append(skymeisaisofukbn1);
        sb.append(", skymeisaisofukbn2=").append(skymeisaisofukbn2);
        sb.append("]");
        return sb.toString();
    }
}
