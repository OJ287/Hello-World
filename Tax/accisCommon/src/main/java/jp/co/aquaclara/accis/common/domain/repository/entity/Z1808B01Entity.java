package jp.co.aquaclara.accis.common.domain.repository.entity;

import java.io.Serializable;
import lombok.Data;

@Data
public class Z1808B01Entity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 単体計算フラグ (0:しない 1:する)(mode=IN)
     */
    private String simplefg;

    /**
     * 税設定区分(mode=IN)
     */
    private String taxsetteikbn;

    /**
     * 税区分(mode=IN)
     */
    private String taxkbn;

    /**
     * 税計算区分 (固定しない場合はNULL)(mode=IN)
     */
    private String taxkeisankbn;

    /**
     * 切り替え日(mode=IN)
     */
    private String kirikaebi;

    /**
     * AP基本コード(mode=IN)
     */
    private String apkihoncd;

    /**
     * ショップコード(mode=IN)
     */
    private String shopcd;

    /**
     * 仕入先コード(mode=IN)
     */
    private String siiresakicd;

    /**
     * 数量(mode=IN)
     */
    private String suryo;

    /**
     * 税抜金額（税抜単価）(mode=IN)
     */
    private String kin;

    /**
     * 計算対象値(mode=IN)
     */
    private Double value;

    /**
     * 端数計算区分(mode=IN)
     */
    private String hasukeisankbn;

    /**
     * 商品コード(mode=IN)
     */
    private String syohincd;

    /**
     * 配達区分(mode=IN)
     */
    private String dlvrykbn;

    /**
     * 計上区分(mode=IN)
     */
    private String keijokbn;

    /**
     * セット商品単価消費税(mode=OUT)
     */
    private int kidunttaxsum;

    /**
     * セット商品消費税(mode=OUT)
     */
    private int kidtaxsum;
}
