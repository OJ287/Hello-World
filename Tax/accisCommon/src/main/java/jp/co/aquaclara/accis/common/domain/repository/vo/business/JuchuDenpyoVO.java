package jp.co.aquaclara.accis.common.domain.repository.vo.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntity;

/**
 * 受注配達伝票のValueObjectです．
 * 
 * @author Isac)Katayama
 * @version 1.0.0 2007-05-06
 */
public class JuchuDenpyoVO extends S04T001TEntity {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * オブジェクトのキー
     */
    public static final String OBJECT_KEY = "S01JuchuDenpyoVO";

    /**
     * コンストラクタ
     */
    public JuchuDenpyoVO() {}

    /**
     * このオブジェクトの内容を文字列で取得する。
     * 
     * @return オブジェクトの内容
     */
    public String toString() {
        StringBuffer str = new StringBuffer();

        return super.toString() + str.toString();
    }

    /**
     * 有効伝票か判定する。
     * 
     * @return boolean 結果
     */
    public boolean canUpdate() {
        return (this.getDlvryjissekidd() == null || this.getDlvryjissekidd().length() == 0)
            && CommonConstants.FLG_ON.equals(this.getYukokbn())
            && !CommonConstants.DNPYSTS_09.equals(this.getDnpysts());
    }

    /**
     * 予約か判定する。
     * 
     * @return boolean 結果
     */
    public boolean isYoyaku() {
        return CommonConstants.KBN_SAKUSEIMOTO_JUCHUYOYAKU.equals(this.getDatsksmotokbn());
    }

    /**
     * 委託配達か判定する。
     * 
     * @return boolean 結果
     */
    public boolean getDlvryOutsource() {
        boolean result = false;

        if (this.getApkihoncd() != null && this.getShopcd() != null) {
            result = !this.getApkihoncd().equals(this.getDlvrykankatsuapkihoncd())
                || !this.getShopcd().equals(this.getDlvrykankatsushopcd());
        }

        return result;
    }

    /**
     * 受注金額・受注消費税金額を算出する。
     */
    public void calcAcptkin() {
        long acptkin = 0;
        long acptshohikin = 0;

        if (this.meisaiList != null) {
            for (int i = 0; i < this.meisaiList.size(); i++) {
                JuchuMeisaiVO meiVo = this.meisaiList.get(i);
                if (CommonConstants.FLG_ON.equals(meiVo.getNyuryokumeisaiflg())) {
                    if (meiVo.getAcptkin() != null) {
                        acptkin += new BigDecimal(meiVo.getAcptkin()).longValue();
                    }
                    if (meiVo.getAcptshohitaxkin() != null) {
                        acptshohikin += new BigDecimal(meiVo.getAcptshohitaxkin()).longValue();
                    }
                }
            }
        }

        this.setAcptkin(acptkin);
        this.setAcptshohitaxkin(acptshohikin);
    }

    /**
     * インボイス実施後 : 受注金額・受注消費税金額を算出する。
     */
    public void calcAcptkinAfterInvoice() {

        long acptkin = this.getAcptippannukikin() + this.getAcptkeigennukikin()
            + this.getAcpthikazeiuriikin() + this.getAcptfukazeiuriikin();

        long acptshohikin = this.getAcptippantaxkin() + this.getAcptkeigentaxkin();

        this.setAcptkin(acptkin);
        this.setAcptshohitaxkin(acptshohikin);
    }

    /**
     * 受注伝票番号をセットする（受注明細含む）。
     * 
     * @param newNumber 受注伝票番号
     */
    public void setAcptdnpynoAtOnce(String newNumber) {
        this.setAcptdnpyno(newNumber);
        if (this.meisaiList != null && this.meisaiList.size() != 0) {
            for (int i = 0; i < this.getMeisaiList().size(); i++) {
                JuchuMeisaiVO meiVo = this.getMeisaiList().get(i);
                meiVo.setAcptdnpyno(newNumber);
            }
        }
    }


    /**
     * 在庫管理区分をセットする。
     * 
     * @param shopModel ショップマスタモデル
     */
    public void setZaikokanrikbnFromShopModel(S02M046TEntity shopModel) {
        if (shopModel != null && !CommonConstants.FLG_OFF.equals(shopModel.getZaikokanrikbn())) {
            this.setZaikokanrikbn(CommonConstants.FLG_ON);
        } else {
            this.setZaikokanrikbn(CommonConstants.FLG_OFF);
        }
    }

    /**
     * 各種受注金額・受注消費税金額を算出する。
     * 
     * @param ippanTaxRate 一般税率("0.1"など)
     * @param keigenTaxRate 軽減税率("0.8"など)
     */
    public void calcEachAcptkin(PriceInfoVo priceInfoVo, String ippanTaxRate,
        String keigenTaxRate) {
        // 受注一般税抜金額
        long acptippannukikin = 0;
        // 受注一般消費税金額
        long acptippantaxkin = 0;
        // 受注軽減税抜金額
        long acptkeigennukikin = 0;
        // 受注軽減消費税金額
        long acptkeigentaxkin = 0;
        // 受注非課税金額
        long acpthikazeiuriikin = 0;
        // 受注不課税売上金額
        long acptfukazeiuriikin = 0;

        if (this.meisaiList != null) {
            for (int i = 0; i < this.meisaiList.size(); i++) {
                JuchuMeisaiVO meiVo = this.meisaiList.get(i);
                if (CommonConstants.FLG_ON.equals(meiVo.getNyuryokumeisaiflg())) {
                    // 配布物の場合は Taxkbn = null
                    if (meiVo.getTaxkbn() != null) {
                        switch (meiVo.getTaxkbn()) {
                            case CommonConstants.KBN_TAX_KAZEI:
                                if (meiVo.getAcptkin() != null) {
                                    acptippannukikin +=
                                        new BigDecimal(meiVo.getAcptkin()).longValue();
                                }
                                break;
                            case CommonConstants.KBN_TAX_KEIGEN:
                                if (meiVo.getAcptkin() != null) {
                                    acptkeigennukikin +=
                                        new BigDecimal(meiVo.getAcptkin()).longValue();
                                }
                                break;
                            case CommonConstants.KBN_TAX_HIKAZEI:
                                if (meiVo.getAcptkin() != null) {
                                    acpthikazeiuriikin +=
                                        new BigDecimal(meiVo.getAcptkin()).longValue();
                                }
                                break;
                            case CommonConstants.KBN_TAX_HUKAZEI:
                                if (meiVo.getAcptkin() != null) {
                                    acptfukazeiuriikin +=
                                        new BigDecimal(meiVo.getAcptkin()).longValue();
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }

        RoundingMode taxRounding = priceInfoVo.getTaxRoundingMode();
        acptippantaxkin = new BigDecimal(acptippannukikin).multiply(new BigDecimal(ippanTaxRate))
            .setScale(0, taxRounding).longValue();

        acptkeigentaxkin = new BigDecimal(acptkeigennukikin).multiply(new BigDecimal(keigenTaxRate))
            .setScale(0, taxRounding).longValue();

        this.setAcptippannukikin(acptippannukikin);
        this.setAcptippantaxkin(acptippantaxkin);
        this.setAcptkeigennukikin(acptkeigennukikin);
        this.setAcptkeigentaxkin(acptkeigentaxkin);
        this.setAcpthikazeiuriikin(acpthikazeiuriikin);
        this.setAcptfukazeiuriikin(acptfukazeiuriikin);
    }

    /**
     * 納品金額・納品税金額をセットする
     */
    public void setInvoiceNohinTaxParams() {
        // 納品一般税抜金額
        this.setNohinippannukikin(0L);
        // 納品一般消費税金額
        this.setNohinippantaxkin(0L);
        // 納品軽減税抜金額
        this.setNohinkeigennukikin(0L);
        // 納品軽減消費税金額
        this.setNohinkeigentaxkin(0L);
        // 納品非課税金額
        this.setNohinhikazeiuriikin(0L);
        // 納品不課税売上金額
        this.setNohinfukazeiuriikin(0L);
    }

    /**
     * 受注予約伝票ＶＯ
     */
    private JuchuDenpyoVO deletingDenVo;

    /**
     * 集計用VO
     */
    private JuchuSumVo sumvo;

    /**
     * 明細リスト
     */
    private List<JuchuMeisaiVO> meisaiList;

    /**
     * 伝票ステータス名称(dnpystsmei)
     */
    private String dnpystsmei;

    /**
     * 配達エリア名称(dlvryareanm)
     */
    private String dlvryareanm;

    /**
     * 配達パターン名称(dlvryptnnm)
     */
    private String dlvryptnnm;

    /**
     * 配達拠点名称(dlvrykyotennm)
     */
    private String dlvrykyotennm;

    /**
     * 配達会社名称(dlvrycmpnynm)
     */
    private String dlvrycmpnynm;

    /**
     * 受注担当者名(acpttnshamei)
     */
    private String acpttnshamei;

    /**
     * 配達担当者名(dlvrytnshamei)
     */
    private String dlvrytnshamei;

    /**
     * 納品先情報１(nohinsakijohon1)
     */
    private String nohinsakijohon1;

    /**
     * 納品先情報２(nohinsakijohon2)
     */
    private String nohinsakijohon2;

    /**
     * 納品先情報１(nohinsakijohon1)
     */
    private String nohinsakijohonCD1;

    /**
     * 納品先情報２(nohinsakijohon2)
     */
    private String nohinsakijohonCD2;

    /**
     * レンタル請求管理区分(rtlskykanrikbn)
     */
    private String rtlskykanrikbn;

    /**
     * サーバーレンタル料金(wsvrtlrkn)
     */
    private String wsvrtlrkn;

    // ■:1 IBIS)Shinoda 2017-06-29 add begin
    /**
     * 配布物AP基本コード１(haifubutsuapkihoncd1)
     */
    private String haifubutsuapkihoncd1;

    /**
     * 配布物ショップコード１(haifubutsushopcd1)
     */
    private String haifubutsushopcd1;
    // ■:1 IBIS)Shinoda 2017-06-29 add end

    /**
     * 配布物コード１(haifubutsucd1)
     */
    private String haifubutsucd1;

    /**
     * 配布物名称１(haifubutsunm1)
     */
    private String haifubutsunm1;

    // ■:1 IBIS)Shinoda 2017-06-29 add begin
    /**
     * 配布物AP基本コード２(haifubutsuapkihoncd2)
     */
    private String haifubutsuapkihoncd2;

    /**
     * 配布物ショップコード２(haifubutsushopcd2)
     */
    private String haifubutsushopcd2;
    // ■:1 IBIS)Shinoda 2017-06-29 add end

    /**
     * 配布物コード２(haifubutsucd2)
     */
    private String haifubutsucd2;

    /**
     * 配布物名称２(haifubutsunm2)
     */
    private String haifubutsunm2;

    // ■:1 IBIS)Shinoda 2017-06-29 add begin
    /**
     * 配布物AP基本コード３(haifubutsuapkihoncd3)
     */
    private String haifubutsuapkihoncd3;

    /**
     * 配布物ショップコード３(haifubutsushopcd3)
     */
    private String haifubutsushopcd3;

    /**
     * 配布物コード３(haifubutsucd3)
     */
    private String haifubutsucd3;

    /**
     * 配布物名称３(haifubutsunm3)
     */
    private String haifubutsunm3;

    /**
     * 配布物AP基本コード４(haifubutsuapkihoncd4)
     */
    private String haifubutsuapkihoncd4;

    /**
     * 配布物ショップコード４(haifubutsushopcd4)
     */
    private String haifubutsushopcd4;

    /**
     * 配布物コード４(haifubutsucd4)
     */
    private String haifubutsucd4;

    /**
     * 配布物名称４(haifubutsunm4)
     */
    private String haifubutsunm4;

    /**
     * 拠点ステータス(kyotensts)
     */
    private String kyotensts;

    /**
     * 受託配達顧客区分(jutakudlvrycstkbn)
     */
    private String jutakudlvrycstkbn;
    // ■:1 IBIS)Shinoda 2017-06-29 add end

    /**
     * 受注停止フラグ(ACPTTEISHIFLG)
     */
    private String acptteishiflg;

    /**
     * データ作成元区分(datsksmotokbnnm)
     */
    private String datsksmotokbnnm;

    /**
     * 得意先区分(tokuisakikbn)
     */
    private String tokuisakikbn;

    /**
     * 得意先状態(tokuisakijotai)
     */
    private String tokuisakijotai;

    /**
     * 状態ステータス(jotaists)
     */
    private String jotaists;

    /**
     * 受注区分名称(acptkbnnm)
     */
    private String acptkbnnm;

    /**
     * 受注区分詳細名称(acptkbnshsnm)
     */
    private String acptkbnshsnm;

    /**
     * 配達希望時間区分名称(dlvrykibojikankbnnm)
     */
    private String dlvrykibojikankbnnm;

    /**
     * 追加商品コード(ADDSHNCD)
     */
    private String addshncd;

    /**
     * 在庫管理区分(zaikokanrikbn)
     */
    private String zaikokanrikbn;

    /**
     * 請求先コード(skysakicd)
     */
    private String skysakicd;

    /**
     * 配達ＷＥＥＫ(dlvryshuu)
     */
    private String dlvryshuu;

    /**
     * 指定曜日(shiteiyobi)
     */
    private String shiteiyobi;

    /**
     * 得意先名称(tokuisakinmn)
     */
    public String tokuisakinmn;

    // ■:1 tjs)nakano 2009-03-17
    /**
     * 指定区分(shiteikbn)
     */
    private String shiteikbn;

    /**
     * 指定要素(shiteiyobi)
     */
    private String shiteiyouso;
    // ■:2 tjs)nakano 2009-03-17


    // /**
    // * 結果区分(resultKbnnm)
    // */
    // public String resultKbnnm;

    // ■:1 Isac)Shiba 2007-12-20
    /**
     * 配達順
     */
    private String dlvryJun;
    // ■:2 Isac)Shiba 2007-12-20


    // ■:1 tjs)nakano 2009-01-22
    /**
     * 配達予定時間終了
     */
    private String dlvrykibojikanshry;
    // ■:2 tjs)nakano 2009-01-22

    // ■:1 Isac）matsuo 2012-02-03
    /**
     * 受注受付経路区分(acptrcptkeirokbn)
     */
    public String acptrcptkeirokbn;
    // ■:2 Isac）matsuo 2012-02-03

    // ■:1 Isac）matsuo 2012-02-15
    /**
     * 回収方法区分(kaishuhohokbn)
     */
    public String kaishuhohokbn;
    // ■:2 Isac）matsuo 2012-02-15

    /**
     * sumvoを取得する。
     * 
     * @return sumvo
     */
    public JuchuSumVo getSumvo() {
        return sumvo;
    }

    /**
     * sumvoを設定する。
     * 
     * @param sumvo 設定する sumvo
     */
    public void setSumvo(JuchuSumVo sumvo) {
        this.sumvo = sumvo;
    }

    /**
     * meisaiListを取得する。
     * 
     * @return meisaiList
     */
    public List<JuchuMeisaiVO> getMeisaiList() {
        return meisaiList;
    }

    /**
     * meisaiListを設定する。
     * 
     * @param meisaiList 設定する meisaiList
     */
    public void setMeisaiList(List<JuchuMeisaiVO> meisaiList) {
        this.meisaiList = meisaiList;
    }

    /**
     * acpttnshameiを取得する。
     * 
     * @return acpttnshamei
     */
    public String getAcpttnshamei() {
        return acpttnshamei;
    }

    /**
     * acpttnshameiを設定する。
     * 
     * @param acpttnshamei 設定する acpttnshamei
     */
    public void setAcpttnshamei(String acpttnshamei) {
        this.acpttnshamei = acpttnshamei;
    }

    /**
     * dlvryareanmを取得する。
     * 
     * @return dlvryareanm
     */
    public String getDlvryareanm() {
        return dlvryareanm;
    }

    /**
     * dlvryareanmを設定する。
     * 
     * @param dlvryareanm 設定する dlvryareanm
     */
    public void setDlvryareanm(String dlvryareanm) {
        this.dlvryareanm = dlvryareanm;
    }

    /**
     * dlvrycmpnynmを取得する。
     * 
     * @return dlvrycmpnynm
     */
    public String getDlvrycmpnynm() {
        return dlvrycmpnynm;
    }

    /**
     * dlvrycmpnynmを設定する。
     * 
     * @param dlvrycmpnynm 設定する dlvrycmpnynm
     */
    public void setDlvrycmpnynm(String dlvrycmpnynm) {
        this.dlvrycmpnynm = dlvrycmpnynm;
    }

    /**
     * dlvrykyotennmを取得する。
     * 
     * @return dlvrykyotennm
     */
    public String getDlvrykyotennm() {
        return dlvrykyotennm;
    }

    /**
     * dlvrykyotennmを設定する。
     * 
     * @param dlvrykyotennm 設定する dlvrykyotennm
     */
    public void setDlvrykyotennm(String dlvrykyotennm) {
        this.dlvrykyotennm = dlvrykyotennm;
    }

    /**
     * dlvryptnnmを取得する。
     * 
     * @return dlvryptnnm
     */
    public String getDlvryptnnm() {
        return dlvryptnnm;
    }

    /**
     * dlvryptnnmを設定する。
     * 
     * @param dlvryptnnm 設定する dlvryptnnm
     */
    public void setDlvryptnnm(String dlvryptnnm) {
        this.dlvryptnnm = dlvryptnnm;
    }

    /**
     * dlvrytnshameiを取得する。
     * 
     * @return dlvrytnshamei
     */
    public String getDlvrytnshamei() {
        return dlvrytnshamei;
    }

    /**
     * dlvrytnshameiを設定する。
     * 
     * @param dlvrytnshamei 設定する dlvrytnshamei
     */
    public void setDlvrytnshamei(String dlvrytnshamei) {
        this.dlvrytnshamei = dlvrytnshamei;
    }

    /**
     * dnpystsmeiを取得する。
     * 
     * @return dnpystsmei
     */
    public String getDnpystsmei() {
        return dnpystsmei;
    }

    /**
     * dnpystsmeiを設定する。
     * 
     * @param dnpystsmei 設定する dnpystsmei
     */
    public void setDnpystsmei(String dnpystsmei) {
        this.dnpystsmei = dnpystsmei;
    }

    // ■:1 IBIS)Shinoda 2017-06-29 add begin
    /**
     * haifubutsuapkihoncd1を取得する。
     * 
     * @return haifubutsuapkihoncd1
     */
    public String getHaifubutsuapkihoncd1() {
        return haifubutsuapkihoncd1;
    }

    /**
     * haifubutsuapkihoncd1を設定する。
     * 
     * @param haifubutsuapkihoncd1 設定する haifubutsuapkihoncd1
     */
    public void setHaifubutsuapkihoncd1(String haifubutsuapkihoncd1) {
        this.haifubutsuapkihoncd1 = haifubutsuapkihoncd1;
    }

    /**
     * haifubutsushopcd1を取得する。
     * 
     * @return haifubutsushopcd1
     */
    public String getHaifubutsushopcd1() {
        return haifubutsushopcd1;
    }

    /**
     * haifubutsushopcd1を設定する。
     * 
     * @param haifubutsushopcd1 設定する haifubutsushopcd1
     */
    public void setHaifubutsushopcd1(String haifubutsushopcd1) {
        this.haifubutsushopcd1 = haifubutsushopcd1;
    }
    // ■:1 IBIS)Shinoda 2017-06-29 add end

    /**
     * haifubutsucd1を取得する。
     * 
     * @return haifubutsucd1
     */
    public String getHaifubutsucd1() {
        return haifubutsucd1;
    }

    /**
     * haifubutsucd1を設定する。
     * 
     * @param haifubutsucd1 設定する haifubutsucd1
     */
    public void setHaifubutsucd1(String haifubutsucd1) {
        this.haifubutsucd1 = haifubutsucd1;
    }

    // ■:1 IBIS)Shinoda 2017-06-29 add begin
    /**
     * haifubutsuapkihoncd2を取得する。
     * 
     * @return haifubutsuapkihoncd2
     */
    public String getHaifubutsuapkihoncd2() {
        return haifubutsuapkihoncd2;
    }

    /**
     * haifubutsuapkihoncd2を設定する。
     * 
     * @param haifubutsuapkihoncd2 設定する haifubutsuapkihoncd2
     */
    public void setHaifubutsuapkihoncd2(String haifubutsuapkihoncd2) {
        this.haifubutsuapkihoncd2 = haifubutsuapkihoncd2;
    }

    /**
     * haifubutsushopcd2を取得する。
     * 
     * @return haifubutsushopcd2
     */
    public String getHaifubutsushopcd2() {
        return haifubutsushopcd2;
    }

    /**
     * haifubutsushopcd2を設定する。
     * 
     * @param haifubutsushopcd2 設定する haifubutsushopcd2
     */
    public void setHaifubutsushopcd2(String haifubutsushopcd2) {
        this.haifubutsushopcd2 = haifubutsushopcd2;
    }
    // ■:1 IBIS)Shinoda 2017-06-29 add end

    /**
     * haifubutsucd2を取得する。
     * 
     * @return haifubutsucd2
     */
    public String getHaifubutsucd2() {
        return haifubutsucd2;
    }

    /**
     * haifubutsucd2を設定する。
     * 
     * @param haifubutsucd2 設定する haifubutsucd2
     */
    public void setHaifubutsucd2(String haifubutsucd2) {
        this.haifubutsucd2 = haifubutsucd2;
    }

    // ■:1 IBIS)Shinoda 2017-06-29 add begin
    /**
     * haifubutsuapkihoncd3を取得する。
     * 
     * @return haifubutsuapkihoncd3
     */
    public String getHaifubutsuapkihoncd3() {
        return haifubutsuapkihoncd3;
    }

    /**
     * haifubutsuapkihoncd3を設定する。
     * 
     * @param haifubutsuapkihoncd3 設定する haifubutsuapkihoncd3
     */
    public void setHaifubutsuapkihoncd3(String haifubutsuapkihoncd3) {
        this.haifubutsuapkihoncd3 = haifubutsuapkihoncd3;
    }

    /**
     * haifubutsushopcd3を取得する。
     * 
     * @return haifubutsushopcd3
     */
    public String getHaifubutsushopcd3() {
        return haifubutsushopcd3;
    }

    /**
     * haifubutsushopcd3を設定する。
     * 
     * @param haifubutsushopcd3 設定する haifubutsushopcd3
     */
    public void setHaifubutsushopcd3(String haifubutsushopcd3) {
        this.haifubutsushopcd3 = haifubutsushopcd3;
    }

    /**
     * haifubutsucd3を取得する。
     * 
     * @return haifubutsucd3
     */
    public String getHaifubutsucd3() {
        return haifubutsucd3;
    }

    /**
     * haifubutsucd3を設定する。
     * 
     * @param haifubutsucd3 設定する haifubutsucd3
     */
    public void setHaifubutsucd3(String haifubutsucd3) {
        this.haifubutsucd3 = haifubutsucd3;
    }

    /**
     * haifubutsuapkihoncd4を取得する。
     * 
     * @return haifubutsuapkihoncd4
     */
    public String getHaifubutsuapkihoncd4() {
        return haifubutsuapkihoncd4;
    }

    /**
     * haifubutsuapkihoncd4を設定する。
     * 
     * @param haifubutsuapkihoncd4 設定する haifubutsuapkihoncd4
     */
    public void setHaifubutsuapkihoncd4(String haifubutsuapkihoncd4) {
        this.haifubutsuapkihoncd4 = haifubutsuapkihoncd4;
    }

    /**
     * haifubutsushopcd4を取得する。
     * 
     * @return haifubutsushopcd4
     */
    public String getHaifubutsushopcd4() {
        return haifubutsushopcd4;
    }

    /**
     * haifubutsushopcd4を設定する。
     * 
     * @param haifubutsushopcd4 設定する haifubutsushopcd4
     */
    public void setHaifubutsushopcd4(String haifubutsushopcd4) {
        this.haifubutsushopcd4 = haifubutsushopcd4;
    }

    /**
     * haifubutsucd4を取得する。
     * 
     * @return haifubutsucd4
     */
    public String getHaifubutsucd4() {
        return haifubutsucd4;
    }

    /**
     * haifubutsucd4を設定する。
     * 
     * @param haifubutsucd4 設定する haifubutsucd4
     */
    public void setHaifubutsucd4(String haifubutsucd4) {
        this.haifubutsucd4 = haifubutsucd4;
    }
    // ■:1 IBIS)Shinoda 2017-06-29 add end

    /**
     * haifubutsunm1を取得する。
     * 
     * @return haifubutsunm1
     */
    public String getHaifubutsunm1() {
        return haifubutsunm1;
    }

    /**
     * haifubutsunm1を設定する。
     * 
     * @param haifubutsunm1 設定する haifubutsunm1
     */
    public void setHaifubutsunm1(String haifubutsunm1) {
        this.haifubutsunm1 = haifubutsunm1;
    }

    /**
     * haifubutsunm2を取得する。
     * 
     * @return haifubutsunm2
     */
    public String getHaifubutsunm2() {
        return haifubutsunm2;
    }

    /**
     * haifubutsunm2を設定する。
     * 
     * @param haifubutsunm2 設定する haifubutsunm2
     */
    public void setHaifubutsunm2(String haifubutsunm2) {
        this.haifubutsunm2 = haifubutsunm2;
    }

    // ■:1 IBIS)Shinoda 2017-06-29 add begin
    /**
     * haifubutsunm3を取得する。
     * 
     * @return haifubutsunm3
     */
    public String getHaifubutsunm3() {
        return haifubutsunm3;
    }

    /**
     * haifubutsunm3を設定する。
     * 
     * @param haifubutsunm3 設定する haifubutsunm3
     */
    public void setHaifubutsunm3(String haifubutsunm3) {
        this.haifubutsunm3 = haifubutsunm3;
    }

    /**
     * haifubutsunm4を取得する。
     * 
     * @return haifubutsunm4
     */
    public String getHaifubutsunm4() {
        return haifubutsunm4;
    }

    /**
     * haifubutsunm4を設定する。
     * 
     * @param haifubutsunm4 設定する haifubutsunm4
     */
    public void setHaifubutsunm4(String haifubutsunm4) {
        this.haifubutsunm4 = haifubutsunm4;
    }

    /**
     * kyotenstsを取得する。
     * 
     * @return kyotensts
     */
    public String getKyotensts() {
        return kyotensts;
    }

    /**
     * kyotenstsを設定する。
     * 
     * @param kyotensts 設定する kyotensts
     */
    public void setKyotensts(String kyotensts) {
        this.kyotensts = kyotensts;
    }

    /**
     * jutakudlvrycstkbnを取得する。
     * 
     * @return jutakudlvrycstkbn
     */
    public String getJutakudlvrycstkbn() {
        return jutakudlvrycstkbn;
    }

    /**
     * jutakudlvrycstkbnを設定する。
     * 
     * @param jutakudlvrycstkbn 設定する jutakudlvrycstkbn
     */
    public void setJutakudlvrycstkbn(String jutakudlvrycstkbn) {
        this.jutakudlvrycstkbn = jutakudlvrycstkbn;
    }
    // ■:1 IBIS)Shinoda 2017-06-29 add end

    /**
     * nohinsakijohon1を取得する。
     * 
     * @return nohinsakijohon1
     */
    public String getNohinsakijohon1() {
        return nohinsakijohon1;
    }

    /**
     * nohinsakijohon1を設定する。
     * 
     * @param nohinsakijohon1 設定する nohinsakijohon1
     */
    public void setNohinsakijohon1(String nohinsakijohon1) {
        this.nohinsakijohon1 = nohinsakijohon1;
    }

    /**
     * nohinsakijohon2を取得する。
     * 
     * @return nohinsakijohon2
     */
    public String getNohinsakijohon2() {
        return nohinsakijohon2;
    }

    /**
     * nohinsakijohon2を設定する。
     * 
     * @param nohinsakijohon2 設定する nohinsakijohon2
     */
    public void setNohinsakijohon2(String nohinsakijohon2) {
        this.nohinsakijohon2 = nohinsakijohon2;
    }

    /**
     * nohinsakijohonCD1を取得する。
     * 
     * @return nohinsakijohonCD1
     */
    public String getNohinsakijohonCD1() {
        return nohinsakijohonCD1;
    }

    /**
     * nohinsakijohonCD1を設定する。
     * 
     * @param nohinsakijohonCD1 設定する nohinsakijohonCD1
     */
    public void setNohinsakijohonCD1(String nohinsakijohonCD1) {
        this.nohinsakijohonCD1 = nohinsakijohonCD1;
    }

    /**
     * nohinsakijohonCD2を取得する。
     * 
     * @return nohinsakijohonCD2
     */
    public String getNohinsakijohonCD2() {
        return nohinsakijohonCD2;
    }

    /**
     * nohinsakijohonCD2を設定する。
     * 
     * @param nohinsakijohonCD2 設定する nohinsakijohonCD2
     */
    public void setNohinsakijohonCD2(String nohinsakijohonCD2) {
        this.nohinsakijohonCD2 = nohinsakijohonCD2;
    }

    /**
     * rtlskykanrikbnを取得する。
     * 
     * @return rtlskykanrikbn
     */
    public String getRtlskykanrikbn() {
        return rtlskykanrikbn;
    }

    /**
     * rtlskykanrikbnを設定する。
     * 
     * @param rtlskykanrikbn 設定する rtlskykanrikbn
     */
    public void setRtlskykanrikbn(String rtlskykanrikbn) {
        this.rtlskykanrikbn = rtlskykanrikbn;
    }

    /**
     * wsvrtlrknを取得する。
     * 
     * @return wsvrtlrkn
     */
    public String getWsvrtlrkn() {
        return wsvrtlrkn;
    }

    /**
     * wsvrtlrknを設定する。
     * 
     * @param wsvrtlrkn 設定する wsvrtlrkn
     */
    public void setWsvrtlrkn(String wsvrtlrkn) {
        this.wsvrtlrkn = wsvrtlrkn;
    }

    /**
     * acptteishiflgを取得する。
     * 
     * @return acptteishiflg
     */
    public String getAcptteishiflg() {
        return acptteishiflg;
    }

    /**
     * acptteishiflgを設定する。
     * 
     * @param acptteishiflg 設定する acptteishiflg
     */
    public void setAcptteishiflg(String acptteishiflg) {
        this.acptteishiflg = acptteishiflg;
    }

    /**
     * datsksmotokbnnmを取得する。
     * 
     * @return datsksmotokbnnm
     */
    public String getDatsksmotokbnnm() {
        return datsksmotokbnnm;
    }

    /**
     * datsksmotokbnnmを設定する。
     * 
     * @param datsksmotokbnnm 設定する datsksmotokbnnm
     */
    public void setDatsksmotokbnnm(String datsksmotokbnnm) {
        this.datsksmotokbnnm = datsksmotokbnnm;
    }

    /**
     * 得意先区分(tokuisakikbn)を設定する。
     * 
     * @param tokuisakikbn 得意先区分
     */
    public void setTokuisakikbn(String tokuisakikbn) {
        this.tokuisakikbn = tokuisakikbn;
    }

    /**
     * 得意先区分(tokuisakikbn)を取得する。
     * 
     * @return 得意先区分(tokuisakikbn)
     */
    public String getTokuisakikbn() {
        return this.tokuisakikbn;
    }

    /**
     * 得意先状態(tokuisakijotai)を設定する。
     * 
     * @param tokuisakijotai 得意先状態
     */
    public void setTokuisakijotai(String tokuisakijotai) {
        this.tokuisakijotai = tokuisakijotai;
    }

    /**
     * 得意先状態(tokuisakijotai)を取得する。
     * 
     * @return 得意先状態(tokuisakijotai)
     */
    public String getTokuisakijotai() {
        return this.tokuisakijotai;
    }


    /**
     * 状態ステータスを設定します。
     * 
     * @param jotaists 状態ステータス
     */
    public void setJotaists(String jotaists) {
        this.jotaists = jotaists;
    }

    /**
     * 状態ステータスを取得します。
     * 
     * @return 状態ステータス
     */
    public String getJotaists() {
        return this.jotaists;
    }

    /**
     * 受注区分名称を設定します。
     * 
     * @param acptkbnnm 受注区分名称
     */
    public void setAcptkbnnm(String acptkbnnm) {
        this.acptkbnnm = acptkbnnm;
    }

    /**
     * 受注区分名称を取得します。
     * 
     * @return 受注区分名称
     */
    public String getAcptkbnnm() {
        return this.acptkbnnm;
    }

    /**
     * 受注区分詳細名称を設定します。
     * 
     * @param acptkbnshsnm 受注区分詳細名称
     */
    public void setAcptkbnshsnm(String acptkbnshsnm) {
        this.acptkbnshsnm = acptkbnshsnm;
    }

    /**
     * 受注区分詳細名称を取得します。
     * 
     * @return 受注区分詳細名称
     */
    public String getAcptkbnshsnm() {
        return this.acptkbnshsnm;
    }

    /**
     * dlvrykibojikankbnnmを取得する。
     * 
     * @return dlvrykibojikankbnnm
     */
    public String getDlvrykibojikankbnnm() {
        return dlvrykibojikankbnnm;
    }

    /**
     * dlvrykibojikankbnnmを設定する。
     * 
     * @param dlvrykibojikankbnnm 設定する dlvrykibojikankbnnm
     */
    public void setDlvrykibojikankbnnm(String dlvrykibojikankbnnm) {
        this.dlvrykibojikankbnnm = dlvrykibojikankbnnm;
    }

    /**
     * addshncdを取得する。
     * 
     * @return addshncd
     */
    public String getAddshncd() {
        return addshncd;
    }

    /**
     * addshncdを設定する。
     * 
     * @param addshncd 設定する addshncd
     */
    public void setAddshncd(String addshncd) {
        this.addshncd = addshncd;
    }

    /**
     * 在庫管理区分を設定します。
     * 
     * @param zaikokanrikbn 在庫管理区分
     */
    public void setZaikokanrikbn(String zaikokanrikbn) {
        this.zaikokanrikbn = zaikokanrikbn;
    }

    /**
     * 在庫管理区分を取得します。
     * 
     * @return 在庫管理区分
     */
    public String getZaikokanrikbn() {
        return this.zaikokanrikbn;
    }

    /**
     * 請求先コードを設定します。
     * 
     * @param skysakicd 請求先コード
     */
    public void setSkysakicd(String skysakicd) {
        this.skysakicd = skysakicd;
    }

    /**
     * 請求先コードを取得します。
     * 
     * @return 請求先コード
     */
    public String getSkysakicd() {
        return this.skysakicd;
    }


    /**
     * 配達ＷＥＥＫを設定します。
     * 
     * @param dlvryshuu 配達ＷＥＥＫ
     */
    public void setDlvryshuu(String dlvryshuu) {
        this.dlvryshuu = dlvryshuu;
    }

    /**
     * 配達ＷＥＥＫを取得します。
     * 
     * @return 配達ＷＥＥＫ
     */
    public String getDlvryshuu() {
        return this.dlvryshuu;
    }

    /**
     * 指定曜日を設定します。
     * 
     * @param shiteiyobi 指定曜日
     */
    public void setShiteiyobi(String shiteiyobi) {
        this.shiteiyobi = shiteiyobi;
    }

    /**
     * 指定曜日を取得します。
     * 
     * @return 指定曜日
     */
    public String getShiteiyobi() {
        return this.shiteiyobi;
    }

    /**
     * 得意先名称を設定します。
     * 
     * @param tokuisakinmn 得意先名称
     */
    public void setTokuisakinmn(String tokuisakinmn) {
        this.tokuisakinmn = tokuisakinmn;
    }

    /**
     * 得意先名称を取得します。
     * 
     * @return 得意先名称
     */
    public String getTokuisakinmn() {
        return this.tokuisakinmn;
    }

    /**
     * deletingDenVoを取得する。
     * 
     * @return deletingDenVo
     */
    public JuchuDenpyoVO getDeletingDenVo() {
        return deletingDenVo;
    }

    /**
     * deletingDenVoを設定する。
     * 
     * @param deletingDenVo 設定する deletingDenVo
     */
    public void setDeletingDenVo(JuchuDenpyoVO deletingDenVo) {
        this.deletingDenVo = deletingDenVo;
    }

    // ■:1 Isac)Shiba 2007-12-20
    /**
     * dlvryjunを取得する。
     * 
     * @return dlvryjun
     */
    public String getDlvryJun() {
        return dlvryJun;
    }

    /**
     * dlvryjunを設定する。
     * 
     * @param dlvryjun 設定する dlvryjun
     */
    public void setDlvryJun(String dlvryJun) {
        this.dlvryJun = dlvryJun;
    }
    // ■:2 Isac)Shiba 2007-12-20

    // ■:1 tjs)nakano 2009-01-22
    /**
     * dlvrykibojikanshryを取得する。
     * 
     * @return dlvryjun
     */
    public String getDlvrykibojikanshry() {
        return dlvrykibojikanshry;
    }

    /**
     * dlvrykibojikanshryを設定する。
     * 
     * @param dlvryjun 設定する dlvryjun
     */
    public void setDlvrykibojikanshry(String dlvrykibojikanshry) {
        this.dlvrykibojikanshry = dlvrykibojikanshry;
    }
    // ■:2 tjs)nakano 2009-01-22

    // ■:1 tjs)nakano 2009-03-17
    public String getShiteikbn() {
        return shiteikbn;
    }

    public void setShiteikbn(String shiteikbn) {
        this.shiteikbn = shiteikbn;
    }

    public String getShiteiyouso() {
        return shiteiyouso;
    }

    public void setShiteiyouso(String shiteiyouso) {
        this.shiteiyouso = shiteiyouso;
    }
    // ■:2 tjs)nakano 2009-03-17

    // ■:1 Isac）matsuo 2012-02-03
    /**
     * 受注受付経路区分を設定します。
     * 
     * @param acptrcptkeirokbn 受注受付経路区分
     */
    public void setAcptrcptkeirokbn(String acptrcptkeirokbn) {
        this.acptrcptkeirokbn = acptrcptkeirokbn;
    }

    /**
     * 受注受付経路区分を取得します。
     * 
     * @return 受注受付経路区分
     */
    public String getAcptrcptkeirokbn() {
        return this.acptrcptkeirokbn;
    }
    // ■:2 Isac）matsuo 2012-02-03

    // ■:1 Isac）matsuo 2012-02-15
    /**
     * 回収方法区分を設定します。
     * 
     * @param kaishuhohokbn 回収方法区分
     */
    public void setKaishuhohokbn(String kaishuhohokbn) {
        this.kaishuhohokbn = kaishuhohokbn;
    }

    /**
     * 回収方法区分を取得します。
     * 
     * @return 回収方法区分
     */
    public String getKaishuhohokbn() {
        return this.kaishuhohokbn;
    }
    // ■:2 Isac）matsuo 2012-02-15

    /* 2018-01-13 IBIS)inoue add begin */
    /**
     * 配布物コード１委託相手AP基本コード(Haihubutu1itakuaiteapkihoncd)
     */
    protected String Haihubutu1itakuaiteapkihoncd;
    /**
     * 配布物コード２委託相手AP基本コード(Haihubutu2itakuaiteapkihoncd)
     */
    protected String Haihubutu2itakuaiteapkihoncd;
    /**
     * 配布物コード３委託相手AP基本コード(Haihubutu3itakuaiteapkihoncd)
     */
    protected String Haihubutu3itakuaiteapkihoncd;
    /**
     * 配布物コード４委託相手AP基本コード(Haihubutu4itakuaiteapkihoncd)
     */
    protected String Haihubutu4itakuaiteapkihoncd;
    /**
     * 配布物コード１委託相手ショップコード(Haihubutu1itakuaiteshopcd)
     */
    protected String Haihubutu1itakuaiteshopcd;
    /**
     * 配布物コード２委託相手ショップコード(Haihubutu2itakuaiteshopcd)
     */
    protected String Haihubutu2itakuaiteshopcd;
    /**
     * 配布物コード３委託相手ショップコード(Haihubutu3itakuaiteshopcd)
     */
    protected String Haihubutu3itakuaiteshopcd;
    /**
     * 配布物コード４委託相手ショップコード(Haihubutu4itakuaiteshopcd)
     */
    protected String Haihubutu4itakuaiteshopcd;
    /**
     * 配布物コード１委託相手受注伝票番号(Haihubutu1itakuaiteacptdnpyno)
     */
    protected String Haihubutu1itakuaiteacptdnpyno;
    /**
     * 配布物コード２委託相手受注伝票番号(Haihubutu2itakuaiteacptdnpyno)
     */
    protected String Haihubutu2itakuaiteacptdnpyno;
    /**
     * 配布物コード３委託相手受注伝票番号(Haihubutu3itakuaiteacptdnpyno)
     */
    protected String Haihubutu3itakuaiteacptdnpyno;
    /**
     * 配布物コード４委託相手受注伝票番号(Haihubutu4itakuaiteacptdnpyno)
     */
    protected String Haihubutu4itakuaiteacptdnpyno;
    /**
     * 配布物コード１委託相手受注伝票番号枝番(Haihubutu1itakuaiteedano)
     */
    protected String Haihubutu1itakuaiteedano;
    /**
     * 配布物コード２委託相手受注伝票番号枝番(Haihubutu2itakuaiteedano)
     */
    protected String Haihubutu2itakuaiteedano;
    /**
     * 配布物コード３委託相手受注伝票番号枝番(Haihubutu3itakuaiteedano)
     */
    protected String Haihubutu3itakuaiteedano;
    /**
     * 配布物コード４委託相手受注伝票番号枝番(Haihubutu4itakuaiteedano)
     */
    protected String Haihubutu4itakuaiteedano;
    /**
     * 配布物コード１委託相手受注伝票明細番号(Haihubutu1itakuaiteacptdnpymeisaino)
     */
    protected String Haihubutu1itakuaiteacptdnpymeisaino;
    /**
     * 配布物コード２委託相手受注伝票明細番号(Haihubutu2itakuaiteacptdnpymeisaino)
     */
    protected String Haihubutu2itakuaiteacptdnpymeisaino;
    /**
     * 配布物コード３委託相手受注伝票明細番号(Haihubutu3itakuaiteacptdnpymeisaino)
     */
    protected String Haihubutu3itakuaiteacptdnpymeisaino;
    /**
     * 配布物コード４委託相手受注伝票明細番号(Haihubutu4itakuaiteacptdnpymeisaino)
     */
    protected String Haihubutu4itakuaiteacptdnpymeisaino;
    /**
     * 配布物コード１委託相手受注伝票明細番号枝番(Haihubutu1itakuaiteacptdnpymeisaiedano)
     */
    protected String Haihubutu1itakuaiteacptdnpymeisaiedano;
    /**
     * 配布物コード２委託相手受注伝票明細番号枝番(Haihubutu2itakuaiteacptdnpymeisaiedano)
     */
    protected String Haihubutu2itakuaiteacptdnpymeisaiedano;
    /**
     * 配布物コード３委託相手受注伝票明細番号枝番(Haihubutu3itakuaiteacptdnpymeisaiedano)
     */
    protected String Haihubutu3itakuaiteacptdnpymeisaiedano;
    /**
     * 配布物コード４委託相手受注伝票明細番号枝番(Haihubutu4itakuaiteacptdnpymeisaiedano)
     */
    protected String Haihubutu4itakuaiteacptdnpymeisaiedano;

    /**
     * 配布物コード１委託相手AP基本コード(Haihubutu1itakuaiteapkihoncd)を取得する。
     * 
     * @return Haihubutu1itakuaiteapkihoncd
     */
    public String getHaihubutu1itakuaiteapkihoncd() {
        return Haihubutu1itakuaiteapkihoncd;
    }

    /**
     * 配布物コード１委託相手AP基本コード(Haihubutu1itakuaiteapkihoncd)を設定する。
     * 
     * @param Haihubutu1itakuaiteapkihoncd
     */
    public void setHaihubutu1itakuaiteapkihoncd(String Haihubutu1itakuaiteapkihoncd) {
        this.Haihubutu1itakuaiteapkihoncd = Haihubutu1itakuaiteapkihoncd;
    }

    /**
     * 配布物コード２委託相手AP基本コード(Haihubutu2itakuaiteapkihoncd)を取得する。
     * 
     * @return Haihubutu2itakuaiteapkihoncd
     */
    public String getHaihubutu2itakuaiteapkihoncd() {
        return Haihubutu2itakuaiteapkihoncd;
    }

    /**
     * 配布物コード２委託相手AP基本コード(Haihubutu2itakuaiteapkihoncd)を設定する。
     * 
     * @param Haihubutu2itakuaiteapkihoncd
     */
    public void setHaihubutu2itakuaiteapkihoncd(String Haihubutu2itakuaiteapkihoncd) {
        this.Haihubutu2itakuaiteapkihoncd = Haihubutu2itakuaiteapkihoncd;
    }

    /**
     * 配布物コード３委託相手AP基本コード(Haihubutu3itakuaiteapkihoncd)を取得する。
     * 
     * @return Haihubutu3itakuaiteapkihoncd
     */
    public String getHaihubutu3itakuaiteapkihoncd() {
        return Haihubutu3itakuaiteapkihoncd;
    }

    /**
     * 配布物コード３委託相手AP基本コード(Haihubutu3itakuaiteapkihoncd)を設定する。
     * 
     * @param Haihubutu3itakuaiteapkihoncd
     */
    public void setHaihubutu3itakuaiteapkihoncd(String Haihubutu3itakuaiteapkihoncd) {
        this.Haihubutu3itakuaiteapkihoncd = Haihubutu3itakuaiteapkihoncd;
    }

    /**
     * 配布物コード４委託相手AP基本コード(Haihubutu4itakuaiteapkihoncd)を取得する。
     * 
     * @return Haihubutu4itakuaiteapkihoncd
     */
    public String getHaihubutu4itakuaiteapkihoncd() {
        return Haihubutu4itakuaiteapkihoncd;
    }

    /**
     * 配布物コード４委託相手AP基本コード(Haihubutu4itakuaiteapkihoncd)を設定する。
     * 
     * @param Haihubutu4itakuaiteapkihoncd
     */
    public void setHaihubutu4itakuaiteapkihoncd(String Haihubutu4itakuaiteapkihoncd) {
        this.Haihubutu4itakuaiteapkihoncd = Haihubutu4itakuaiteapkihoncd;
    }

    /**
     * 配布物コード１委託相手ショップコード(Haihubutu1itakuaiteshopcd)を取得する。
     * 
     * @return Haihubutu1itakuaiteshopcd
     */
    public String getHaihubutu1itakuaiteshopcd() {
        return Haihubutu1itakuaiteshopcd;
    }

    /**
     * 配布物コード１委託相手ショップコード(Haihubutu1itakuaiteshopcd)を設定する。
     * 
     * @param Haihubutu1itakuaiteshopcd
     */
    public void setHaihubutu1itakuaiteshopcd(String Haihubutu1itakuaiteshopcd) {
        this.Haihubutu1itakuaiteshopcd = Haihubutu1itakuaiteshopcd;
    }

    /**
     * 配布物コード２委託相手ショップコード(Haihubutu2itakuaiteshopcd)を取得する。
     * 
     * @return Haihubutu2itakuaiteshopcd
     */
    public String getHaihubutu2itakuaiteshopcd() {
        return Haihubutu2itakuaiteshopcd;
    }

    /**
     * 配布物コード２委託相手ショップコード(Haihubutu2itakuaiteshopcd)を設定する。
     * 
     * @param Haihubutu2itakuaiteshopcd
     */
    public void setHaihubutu2itakuaiteshopcd(String Haihubutu2itakuaiteshopcd) {
        this.Haihubutu2itakuaiteshopcd = Haihubutu2itakuaiteshopcd;
    }

    /**
     * 配布物コード３委託相手ショップコード(Haihubutu3itakuaiteshopcd)を取得する。
     * 
     * @return Haihubutu3itakuaiteshopcd
     */
    public String getHaihubutu3itakuaiteshopcd() {
        return Haihubutu3itakuaiteshopcd;
    }

    /**
     * 配布物コード３委託相手ショップコード(Haihubutu3itakuaiteshopcd)を設定する。
     * 
     * @param Haihubutu3itakuaiteshopcd
     */
    public void setHaihubutu3itakuaiteshopcd(String Haihubutu3itakuaiteshopcd) {
        this.Haihubutu3itakuaiteshopcd = Haihubutu3itakuaiteshopcd;
    }

    /**
     * 配布物コード４委託相手ショップコード(Haihubutu4itakuaiteshopcd)を取得する。
     * 
     * @return Haihubutu4itakuaiteshopcd
     */
    public String getHaihubutu4itakuaiteshopcd() {
        return Haihubutu4itakuaiteshopcd;
    }

    /**
     * 配布物コード４委託相手ショップコード(Haihubutu4itakuaiteshopcd)を設定する。
     * 
     * @param Haihubutu4itakuaiteshopcd
     */
    public void setHaihubutu4itakuaiteshopcd(String Haihubutu4itakuaiteshopcd) {
        this.Haihubutu4itakuaiteshopcd = Haihubutu4itakuaiteshopcd;
    }

    /**
     * 配布物コード１委託相手受注伝票番号(Haihubutu1itakuaiteacptdnpyno)を取得する。
     * 
     * @return Haihubutu1itakuaiteacptdnpyno
     */
    public String getHaihubutu1itakuaiteacptdnpyno() {
        return Haihubutu1itakuaiteacptdnpyno;
    }

    /**
     * 配布物コード１委託相手受注伝票番号(Haihubutu1itakuaiteacptdnpyno)を設定する。
     * 
     * @param Haihubutu1itakuaiteacptdnpyno
     */
    public void setHaihubutu1itakuaiteacptdnpyno(String Haihubutu1itakuaiteacptdnpyno) {
        this.Haihubutu1itakuaiteacptdnpyno = Haihubutu1itakuaiteacptdnpyno;
    }

    /**
     * 配布物コード２委託相手受注伝票番号(Haihubutu2itakuaiteacptdnpyno)を取得する。
     * 
     * @return Haihubutu2itakuaiteacptdnpyno
     */
    public String getHaihubutu2itakuaiteacptdnpyno() {
        return Haihubutu2itakuaiteacptdnpyno;
    }

    /**
     * 配布物コード２委託相手受注伝票番号(Haihubutu2itakuaiteacptdnpyno)を設定する。
     * 
     * @param Haihubutu2itakuaiteacptdnpyno
     */
    public void setHaihubutu2itakuaiteacptdnpyno(String Haihubutu2itakuaiteacptdnpyno) {
        this.Haihubutu2itakuaiteacptdnpyno = Haihubutu2itakuaiteacptdnpyno;
    }

    /**
     * 配布物コード３委託相手受注伝票番号(Haihubutu3itakuaiteacptdnpyno)を取得する。
     * 
     * @return Haihubutu3itakuaiteacptdnpyno
     */
    public String getHaihubutu3itakuaiteacptdnpyno() {
        return Haihubutu3itakuaiteacptdnpyno;
    }

    /**
     * 配布物コード３委託相手受注伝票番号(Haihubutu3itakuaiteacptdnpyno)を設定する。
     * 
     * @param Haihubutu3itakuaiteacptdnpyno
     */
    public void setHaihubutu3itakuaiteacptdnpyno(String Haihubutu3itakuaiteacptdnpyno) {
        this.Haihubutu3itakuaiteacptdnpyno = Haihubutu3itakuaiteacptdnpyno;
    }

    /**
     * 配布物コード４委託相手受注伝票番号(Haihubutu4itakuaiteacptdnpyno)を取得する。
     * 
     * @return Haihubutu4itakuaiteacptdnpyno
     */
    public String getHaihubutu4itakuaiteacptdnpyno() {
        return Haihubutu4itakuaiteacptdnpyno;
    }

    /**
     * 配布物コード４委託相手受注伝票番号(Haihubutu4itakuaiteacptdnpyno)を設定する。
     * 
     * @param Haihubutu4itakuaiteacptdnpyno
     */
    public void setHaihubutu4itakuaiteacptdnpyno(String Haihubutu4itakuaiteacptdnpyno) {
        this.Haihubutu4itakuaiteacptdnpyno = Haihubutu4itakuaiteacptdnpyno;
    }

    /**
     * 配布物コード１委託相手受注伝票番号枝番(Haihubutu1itakuaiteedano)を取得する。
     * 
     * @return Haihubutu1itakuaiteedano
     */
    public String getHaihubutu1itakuaiteedano() {
        return Haihubutu1itakuaiteedano;
    }

    /**
     * 配布物コード１委託相手受注伝票番号枝番(Haihubutu1itakuaiteedano)を設定する。
     * 
     * @param Haihubutu1itakuaiteedano
     */
    public void setHaihubutu1itakuaiteedano(String Haihubutu1itakuaiteedano) {
        this.Haihubutu1itakuaiteedano = Haihubutu1itakuaiteedano;
    }

    /**
     * 配布物コード２委託相手受注伝票番号枝番(Haihubutu2itakuaiteedano)を取得する。
     * 
     * @return Haihubutu2itakuaiteedano
     */
    public String getHaihubutu2itakuaiteedano() {
        return Haihubutu2itakuaiteedano;
    }

    /**
     * 配布物コード２委託相手受注伝票番号枝番(Haihubutu2itakuaiteedano)を設定する。
     * 
     * @param Haihubutu2itakuaiteedano
     */
    public void setHaihubutu2itakuaiteedano(String Haihubutu2itakuaiteedano) {
        this.Haihubutu2itakuaiteedano = Haihubutu2itakuaiteedano;
    }

    /**
     * 配布物コード３委託相手受注伝票番号枝番(Haihubutu3itakuaiteedano)を取得する。
     * 
     * @return Haihubutu3itakuaiteedano
     */
    public String getHaihubutu3itakuaiteedano() {
        return Haihubutu3itakuaiteedano;
    }

    /**
     * 配布物コード３委託相手受注伝票番号枝番(Haihubutu3itakuaiteedano)を設定する。
     * 
     * @param Haihubutu3itakuaiteedano
     */
    public void setHaihubutu3itakuaiteedano(String Haihubutu3itakuaiteedano) {
        this.Haihubutu3itakuaiteedano = Haihubutu3itakuaiteedano;
    }

    /**
     * 配布物コード４委託相手受注伝票番号枝番(Haihubutu4itakuaiteedano)を取得する。
     * 
     * @return Haihubutu4itakuaiteedano
     */
    public String getHaihubutu4itakuaiteedano() {
        return Haihubutu4itakuaiteedano;
    }

    /**
     * 配布物コード４委託相手受注伝票番号枝番(Haihubutu4itakuaiteedano)を設定する。
     * 
     * @param Haihubutu4itakuaiteedano
     */
    public void setHaihubutu4itakuaiteedano(String Haihubutu4itakuaiteedano) {
        this.Haihubutu4itakuaiteedano = Haihubutu4itakuaiteedano;
    }

    /**
     * 配布物コード１委託相手受注伝票明細番号(Haihubutu1itakuaiteacptdnpymeisaino)を取得する。
     * 
     * @return Haihubutu1itakuaiteacptdnpymeisaino
     */
    public String getHaihubutu1itakuaiteacptdnpymeisaino() {
        return Haihubutu1itakuaiteacptdnpymeisaino;
    }

    /**
     * 配布物コード１委託相手受注伝票明細番号(Haihubutu1itakuaiteacptdnpymeisaino)を設定する。
     * 
     * @param Haihubutu1itakuaiteacptdnpymeisaino
     */
    public void setHaihubutu1itakuaiteacptdnpymeisaino(String Haihubutu1itakuaiteacptdnpymeisaino) {
        this.Haihubutu1itakuaiteacptdnpymeisaino = Haihubutu1itakuaiteacptdnpymeisaino;
    }

    /**
     * 配布物コード２委託相手受注伝票明細番号(Haihubutu2itakuaiteacptdnpymeisaino)を取得する。
     * 
     * @return Haihubutu2itakuaiteacptdnpymeisaino
     */
    public String getHaihubutu2itakuaiteacptdnpymeisaino() {
        return Haihubutu2itakuaiteacptdnpymeisaino;
    }

    /**
     * 配布物コード２委託相手受注伝票明細番号(Haihubutu2itakuaiteacptdnpymeisaino)を設定する。
     * 
     * @param Haihubutu2itakuaiteacptdnpymeisaino
     */
    public void setHaihubutu2itakuaiteacptdnpymeisaino(String Haihubutu2itakuaiteacptdnpymeisaino) {
        this.Haihubutu2itakuaiteacptdnpymeisaino = Haihubutu2itakuaiteacptdnpymeisaino;
    }

    /**
     * 配布物コード３委託相手受注伝票明細番号(Haihubutu3itakuaiteacptdnpymeisaino)を取得する。
     * 
     * @return Haihubutu3itakuaiteacptdnpymeisaino
     */
    public String getHaihubutu3itakuaiteacptdnpymeisaino() {
        return Haihubutu3itakuaiteacptdnpymeisaino;
    }

    /**
     * 配布物コード３委託相手受注伝票明細番号(Haihubutu3itakuaiteacptdnpymeisaino)を設定する。
     * 
     * @param Haihubutu3itakuaiteacptdnpymeisaino
     */
    public void setHaihubutu3itakuaiteacptdnpymeisaino(String Haihubutu3itakuaiteacptdnpymeisaino) {
        this.Haihubutu3itakuaiteacptdnpymeisaino = Haihubutu3itakuaiteacptdnpymeisaino;
    }

    /**
     * 配布物コード４委託相手受注伝票明細番号(Haihubutu4itakuaiteacptdnpymeisaino)を取得する。
     * 
     * @return Haihubutu4itakuaiteacptdnpymeisaino
     */
    public String getHaihubutu4itakuaiteacptdnpymeisaino() {
        return Haihubutu4itakuaiteacptdnpymeisaino;
    }

    /**
     * 配布物コード４委託相手受注伝票明細番号(Haihubutu4itakuaiteacptdnpymeisaino)を設定する。
     * 
     * @param Haihubutu4itakuaiteacptdnpymeisaino
     */
    public void setHaihubutu4itakuaiteacptdnpymeisaino(String Haihubutu4itakuaiteacptdnpymeisaino) {
        this.Haihubutu4itakuaiteacptdnpymeisaino = Haihubutu4itakuaiteacptdnpymeisaino;
    }

    /**
     * 配布物コード１委託相手受注伝票明細番号枝番(Haihubutu1itakuaiteacptdnpymeisaiedano)を取得する。
     * 
     * @return Haihubutu1itakuaiteacptdnpymeisaiedano
     */
    public String getHaihubutu1itakuaiteacptdnpymeisaiedano() {
        return Haihubutu1itakuaiteacptdnpymeisaiedano;
    }

    /**
     * 配布物コード１委託相手受注伝票明細番号枝番(Haihubutu1itakuaiteacptdnpymeisaiedano)を設定する。
     * 
     * @param Haihubutu1itakuaiteacptdnpymeisaiedano
     */
    public void setHaihubutu1itakuaiteacptdnpymeisaiedano(
        String Haihubutu1itakuaiteacptdnpymeisaiedano) {
        this.Haihubutu1itakuaiteacptdnpymeisaiedano = Haihubutu1itakuaiteacptdnpymeisaiedano;
    }

    /**
     * 配布物コード２委託相手受注伝票明細番号枝番(Haihubutu2itakuaiteacptdnpymeisaiedano)を取得する。
     * 
     * @return Haihubutu2itakuaiteacptdnpymeisaiedano
     */
    public String getHaihubutu2itakuaiteacptdnpymeisaiedano() {
        return Haihubutu2itakuaiteacptdnpymeisaiedano;
    }

    /**
     * 配布物コード２委託相手受注伝票明細番号枝番(Haihubutu2itakuaiteacptdnpymeisaiedano)を設定する。
     * 
     * @param Haihubutu2itakuaiteacptdnpymeisaiedano
     */
    public void setHaihubutu2itakuaiteacptdnpymeisaiedano(
        String Haihubutu2itakuaiteacptdnpymeisaiedano) {
        this.Haihubutu2itakuaiteacptdnpymeisaiedano = Haihubutu2itakuaiteacptdnpymeisaiedano;
    }

    /**
     * 配布物コード３委託相手受注伝票明細番号枝番(Haihubutu3itakuaiteacptdnpymeisaiedano)を取得する。
     * 
     * @return Haihubutu3itakuaiteacptdnpymeisaiedano
     */
    public String getHaihubutu3itakuaiteacptdnpymeisaiedano() {
        return Haihubutu3itakuaiteacptdnpymeisaiedano;
    }

    /**
     * 配布物コード３委託相手受注伝票明細番号枝番(Haihubutu3itakuaiteacptdnpymeisaiedano)を設定する。
     * 
     * @param Haihubutu3itakuaiteacptdnpymeisaiedano
     */
    public void setHaihubutu3itakuaiteacptdnpymeisaiedano(
        String Haihubutu3itakuaiteacptdnpymeisaiedano) {
        this.Haihubutu3itakuaiteacptdnpymeisaiedano = Haihubutu3itakuaiteacptdnpymeisaiedano;
    }

    /**
     * 配布物コード４委託相手受注伝票明細番号枝番(Haihubutu4itakuaiteacptdnpymeisaiedano)を取得する。
     * 
     * @return Haihubutu4itakuaiteacptdnpymeisaiedano
     */
    public String getHaihubutu4itakuaiteacptdnpymeisaiedano() {
        return Haihubutu4itakuaiteacptdnpymeisaiedano;
    }

    /**
     * 配布物コード４委託相手受注伝票明細番号枝番(Haihubutu4itakuaiteacptdnpymeisaiedano)を設定する。
     * 
     * @param Haihubutu4itakuaiteacptdnpymeisaiedano
     */
    public void setHaihubutu4itakuaiteacptdnpymeisaiedano(
        String Haihubutu4itakuaiteacptdnpymeisaiedano) {
        this.Haihubutu4itakuaiteacptdnpymeisaiedano = Haihubutu4itakuaiteacptdnpymeisaiedano;
    }
    /* 2018-01-13 IBIS)inoue add end */
}
