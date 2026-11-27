package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntityKey;
import lombok.Data;

/**
 * Database Table Remarks: 配布物情報データ
 *
 * This class corresponds to the database table S04T001T
 */
@Data
public class S04T001TC0810R00SysMstEntity extends S04T001TEntityKey implements Serializable {

    // AP基本コード
    private String apkihoncd;
    // ショップコード
    private String shopcd;
    // 受注伝票番号
    private String acptdnpyno;
    // 枝番号
    private Short edano;
    // 税区分_子明細商品の構成品
    private String taxkbn_5;
    // セット商品明細印字フラグ_印字する
    private String stshnmeisaiinjiflg;
    // 入力明細フラグ_入力行
    private String nyuryokumeisaiflg;
    // システム管理マスタ用AP基本コード
    private String sys_apkihoncd;
    // システム管理マスタ用ショップコード
    private String sys_shopcd;
    // システム管理マスタ用レコード区分
    private String sys_recordkbn;
    // 配達希望時間区分取得用設定番号
    private String setteino_012;
    // 回収方法区分取得用設定番号
    private String setteino_051;
    // 納品先情報1取得用設定番号
    private String setteino_056;
    // 納品先情報2取得用設定番号
    private String setteino_057;
    // 得意先状態取得用設定番号
    private String setteino_014;

    // 配達希望時間名称
    private String dlvrykibojikannm;
    // 納品先情報1
    private String nohinsakijohon1;
    // 納品先情報2
    private String nohinsakijohon2;
    // 回収方法
    private String kaishuhoho;
    // 得意先状態
    private String tokuisakijotai;
    // 受注伝票番号明細
    private String acptdnpynomei;
    // 枝番号明細
    private String edanomei;

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
        sb.append(", dlvrykibojikannm=").append(dlvrykibojikannm);
        sb.append(", nohinsakijohon1=").append(nohinsakijohon1);
        sb.append(", nohinsakijohon2=").append(nohinsakijohon2);
        sb.append(", kaishuhoho=").append(kaishuhoho);
        sb.append(", tokuisakijotai=").append(tokuisakijotai);
        sb.append(", acptdnpynomei=").append(acptdnpynomei);
        sb.append(", edanomei=").append(edanomei);
        sb.append("]");
        return sb.toString();
    }
}
