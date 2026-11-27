package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntityKey;
import lombok.Data;

/**
 * Database Table Remarks: サーバー情報データ取得
 *
 * This class corresponds to the database table S04T001T
 */
@Data
public class S04T001TC0810R00SvInfoEntity extends S04T001TEntityKey implements Serializable {

    // AP基本コード
    private String apkihoncd;
    // ショップコード
    private String shopcd;
    // 受注伝票番号
    private String acptdnpyno;
    // 枝番号
    private Short edano;
    // 受託配達顧客区分_NA受託
    private String jutakudlvrycstkbn_5;
    // 受託配達顧客区分_配達受託
    private String jutakudlvrycstkbn_6;
    // 得意先区分_NA受託
    private String tokuisakikbn_5;
    // 得意先区分_配達受託
    private String tokuisakikbn_6;
    // 税区分_子明細商品の構成品
    private String taxkbn_5;
    // 有効区分
    private String yukokbn;
    // セット商品明細印字フラグ_印字する
    private String stshnmeisaiinjiflg;
    // 入力明細フラグ_入力行
    private String nyuryokumeisaiflg;

    // ウォーターサーバー台数
    private Integer wsvdaisu;
    // 委託相手ウォーターサーバー台数
    private Integer itakuaitewsvdaisu;
    // 次回メンテナンス予定年月
    private String jikaimntytidydm;
    // 委託相手次回メンテナンス予定年月
    private String itakuaitejikaimntytidydm;
    // シリアル番号
    private String srlno;
    // 委託相手シリアル番号
    private String itakuaitesrlno;
    // 受注伝票番号明細
    private String acptdnpynomei;
    // 枝番号明細
    private String edanomei;
    // 得意先区分
    private String tokuisakikbn;
    // 受託配達顧客区分
    private String jutakudlvrycstkbn;

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
        sb.append(", wsvdaisu=").append(wsvdaisu);
        sb.append(", itakuaitewsvdaisu=").append(itakuaitewsvdaisu);
        sb.append(", jikaimntytidydm=").append(jikaimntytidydm);
        sb.append(", itakuaitejikaimntytidydm=").append(itakuaitejikaimntytidydm);
        sb.append(", srlno=").append(srlno);
        sb.append(", itakuaitesrlno=").append(itakuaitesrlno);
        sb.append(", acptdnpynomei=").append(acptdnpynomei);
        sb.append(", edanomei=").append(edanomei);
        sb.append(", tokuisakikbn=").append(tokuisakikbn);
        sb.append(", jutakudlvrycstkbn=").append(jutakudlvrycstkbn);
        sb.append("]");
        return sb.toString();
    }
}
