package jp.co.aquaclara.accis.common.domain.repository.entity.business;

import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M008TEntity;
import jp.co.aquaclara.accis.common.util.business.BusinessUtil;

/**
 * エンティティ「S02M008T(展開マスタ)」のモデルです。
 * 
 * @author Fsol
 * @version 0.0.0 2007-04-09
 */
public class S02M008TEntityEx extends S02M008TEntity {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * 大分類コード(lctgrycd)
     */
    public String lctgrycd;
    /**
     * 中分類コード(mctgrycd)
     */
    public String mctgrycd;
    /**
     * 小分類コード(sctgrycd)
     */
    public String sctgrycd;
    /**
     * 標準価格切替日(hyojunpricekirikaedd)
     */
    public String hyojunpricekirikaedd;
    /**
     * 現標準配達価格(genzhyojundlvryprice)
     */
    public String genzhyojundlvryprice;
    /**
     * 新標準配達価格(shinhyojundlvryprice)
     */
    public String shinhyojundlvryprice;
    /**
     * 現標準持ち帰り価格(genzhyojunmochikaeriprice)
     */
    public String genzhyojunmochikaeriprice;
    /**
     * 新標準持ち帰り価格(shinhyojunmochikaeriprice)
     */
    public String shinhyojunmochikaeriprice;
    /**
     * 税区分(taxkbn)
     */
    public String taxkbn;

    /**
     * 標準価格を取得します（配達区分、対象日判断あり）。
     * 
     * @param dlvrykbn 配達区分
     * @param targetDate 対象日
     * @return 標準価格
     */
    public String getCurrenthyojunprice(String dlvrykbn, String targetDate) {

        if (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(dlvrykbn)) {
            return BusinessUtil.getCurrentValue(this.genzhyojunmochikaeriprice,
                this.shinhyojunmochikaeriprice, this.hyojunpricekirikaedd, targetDate);
        } else {
            return BusinessUtil.getCurrentValue(this.genzhyojundlvryprice,
                this.shinhyojundlvryprice, this.hyojunpricekirikaedd, targetDate);
        }
    }

    /**
     * 大分類コードを設定します。
     * 
     * @param lctgrycd 大分類コード
     */
    public void setLctgrycd(String lctgrycd) {
        this.lctgrycd = lctgrycd;
    }

    /**
     * 大分類コードを取得します。
     * 
     * @return 大分類コード
     */
    public String getLctgrycd() {
        return this.lctgrycd;
    }

    /**
     * 中分類コードを設定します。
     * 
     * @param mctgrycd 中分類コード
     */
    public void setMctgrycd(String mctgrycd) {
        this.mctgrycd = mctgrycd;
    }

    /**
     * 中分類コードを取得します。
     * 
     * @return 中分類コード
     */
    public String getMctgrycd() {
        return this.mctgrycd;
    }

    /**
     * 小分類コードを設定します。
     * 
     * @param sctgrycd 小分類コード
     */
    public void setSctgrycd(String sctgrycd) {
        this.sctgrycd = sctgrycd;
    }

    /**
     * 小分類コードを取得します。
     * 
     * @return 小分類コード
     */
    public String getSctgrycd() {
        return this.sctgrycd;
    }

    /**
     * 標準価格切替日を設定します。
     * 
     * @param hyojunpricekirikaedd 標準価格切替日
     */
    public void setHyojunpricekirikaedd(String hyojunpricekirikaedd) {
        this.hyojunpricekirikaedd = hyojunpricekirikaedd;
    }

    /**
     * 標準価格切替日を取得します。
     * 
     * @return 標準価格切替日
     */
    public String getHyojunpricekirikaedd() {
        return this.hyojunpricekirikaedd;
    }

    /**
     * 現標準配達価格を設定します。
     * 
     * @param genzhyojundlvryprice 現標準配達価格
     */
    public void setGenzhyojundlvryprice(String genzhyojundlvryprice) {
        this.genzhyojundlvryprice = genzhyojundlvryprice;
    }

    /**
     * 現標準配達価格を取得します。
     * 
     * @return 現標準配達価格
     */
    public String getGenzhyojundlvryprice() {
        return this.genzhyojundlvryprice;
    }

    /**
     * 新標準配達価格を設定します。
     * 
     * @param shinhyojundlvryprice 新標準配達価格
     */
    public void setShinhyojundlvryprice(String shinhyojundlvryprice) {
        this.shinhyojundlvryprice = shinhyojundlvryprice;
    }

    /**
     * 新標準配達価格を取得します。
     * 
     * @return 新標準配達価格
     */
    public String getShinhyojundlvryprice() {
        return this.shinhyojundlvryprice;
    }

    /**
     * 現標準持ち帰り価格を設定します。
     * 
     * @param genzhyojunmochikaeriprice 現標準持ち帰り価格
     */
    public void setGenzhyojunmochikaeriprice(String genzhyojunmochikaeriprice) {
        this.genzhyojunmochikaeriprice = genzhyojunmochikaeriprice;
    }

    /**
     * 現標準持ち帰り価格を取得します。
     * 
     * @return 現標準持ち帰り価格
     */
    public String getGenzhyojunmochikaeriprice() {
        return this.genzhyojunmochikaeriprice;
    }

    /**
     * 新標準持ち帰り価格を設定します。
     * 
     * @param shinhyojunmochikaeriprice 新標準持ち帰り価格
     */
    public void setShinhyojunmochikaeriprice(String shinhyojunmochikaeriprice) {
        this.shinhyojunmochikaeriprice = shinhyojunmochikaeriprice;
    }

    /**
     * 新標準持ち帰り価格を取得します。
     * 
     * @return 新標準持ち帰り価格
     */
    public String getShinhyojunmochikaeriprice() {
        return this.shinhyojunmochikaeriprice;
    }

    /**
     * 税区分を設定します。
     * 
     * @param taxkbn 税区分
     */
    public void setTaxkbn(String taxkbn) {
        this.taxkbn = taxkbn;
    }

    /**
     * 税区分を取得します。
     * 
     * @return 税区分
     */
    public String getTaxkbn() {
        return this.taxkbn;
    }

    /**
     * このオブジェクトの内容を文字列で取得する。
     * 
     * @return 内容
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(BusinessUtil.ENDLINE).append("lctgrycd(大分類コード)=").append(lctgrycd);
        buf.append(BusinessUtil.ENDLINE).append("mctgrycd(中分類コード)=").append(mctgrycd);
        buf.append(BusinessUtil.ENDLINE).append("sctgrycd(小分類コード)=").append(sctgrycd);
        buf.append(BusinessUtil.ENDLINE).append("hyojunpricekirikaedd(標準価格切替日)=")
            .append(hyojunpricekirikaedd);
        buf.append(BusinessUtil.ENDLINE).append("genzhyojundlvryprice(現標準配達価格)=")
            .append(genzhyojundlvryprice);
        buf.append(BusinessUtil.ENDLINE).append("shinhyojundlvryprice(新標準配達価格)=")
            .append(shinhyojundlvryprice);
        buf.append(BusinessUtil.ENDLINE).append("genzhyojunmochikaeriprice(現標準持ち帰り価格)=")
            .append(genzhyojunmochikaeriprice);
        buf.append(BusinessUtil.ENDLINE).append("shinhyojunmochikaeriprice(新標準持ち帰り価格)=")
            .append(shinhyojunmochikaeriprice);
        buf.append(BusinessUtil.ENDLINE).append("taxkbn(税区分)=").append(taxkbn);
        return super.toString() + buf.toString();
    }

}

