package jp.co.aquaclara.accis.common.domain.repository.vo.business;

import java.math.BigDecimal;
import java.util.List;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntity;
import jp.co.aquaclara.accis.common.util.business.BusinessUtil;


/**
 * 受注配達明細のValueObjectです．
 * 
 * @author Isac)Katayama
 * @version 1.0.0 2007-05-06
 */
public class JuchuMeisaiVO extends S04T002TEntity {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * オブジェクトのキー
     */
    public static final String OBJECT_KEY = "S01JuchuMeisaiVO";

    /**
     * コンストラクタ
     */
    public JuchuMeisaiVO() {}

    /**
     * このオブジェクトの内容を文字列で取得する。
     * 
     * @return オブジェクトの内容
     */
    public String toString() {
        StringBuffer str = new StringBuffer();

        str.append("apkihoncd=").append("").append("\n");

        return super.toString() + str.toString();
    }

    /**
     * このオブジェクトの内容を文字列で取得する。
     * 
     * @return オブジェクトの内容
     */
    public static JuchuMeisaiVO createHaifubutsuVo() {
        JuchuMeisaiVO vo = new JuchuMeisaiVO();

        vo.setAcptdnpymeisaiedano((short) 0);
        vo.setYusyokbn(CommonConstants.KBN_YUSHO_NONE);
        vo.setMeisaikbn(CommonConstants.KBN_MEISAI_HAIHUBUTSU);
        vo.setNyuryokumeisaiflg(CommonConstants.FLG_ON);
        vo.setTanpinmeisaiflg(CommonConstants.FLG_ON);

        return vo;
    }

    /**
     * 本数計算対象フラグ
     * 
     * @return boolean 結果
     */
    public boolean isCountable() {
        return CommonConstants.FLG_ON.equals(this.getTanpinmeisaiflg())
            && CommonConstants.KBN_MEISAI_SHOHIN.equals(this.getMeisaikbn());
    }

    /**
     * 更新用プロパティセット
     * 
     * @param relation 受注関係VO
     * @param shohinModel 商品マスタビューモデル
     */
    public void setUpdateProperties(JuchuRelationVo relation, S02V001TEntity shohinModel) {

        // プロパティ値のセット
        if (shohinModel != null) {
            this.setLctgrycd(shohinModel.getLctgrycd());
            this.setMctgrycd(shohinModel.getMctgrycd());
            this.setSctgrycd(shohinModel.getSctgrycd());
            this.setTaxkbn(shohinModel.getTaxkbn());
        }

        // 受注関係VO生成
        this.setRelationVo(relation.createHalfClone(this));

        // 商品マスタビュー情報セット
        this.setShohinModel(shohinModel);
    }

    /**
     * 商品税区分取得（計上区分の判断有り）
     * 
     * @param keijokbn 計上区分
     * @param shntaxkbn 商品税区分
     * @return String 結果
     */
    public static String getShnTaxKbnByKeijoKbn(String keijokbn, String shntaxkbn) {
        return CommonConstants.KBN_KEIJO_KEIHI.equals(keijokbn) ? CommonConstants.KBN_TAX_HIKAZEI
            : shntaxkbn;
    }

    /**
     * 明細Formのインデックス
     */
    private int formIndex;
    /**
     * 受注計上区分関係VO
     */
    private JuchuRelationVo relationVo;

    /**
     * 商品マスタビューモデル
     */
    private S02V001TEntity shohinModel;

    /**
     * 商品ＡＰ基本コード(shnapkihoncd)
     */
    public String shnapkihoncd;

    /**
     * 商品名称略称(SHNNMRYAKUSHO)
     */
    private String shnnmryakusho;

    /**
     * 色(IRO)
     */
    private String iro;

    /**
     * サイズ(SZ)
     */
    private String sz;

    /**
     * 売上取扱単位(URITRTSKITANI)
     */
    private String uritrtskitani;

    /**
     * 売上入数(URIIRISU)
     */
    private String uriirisu;

    /**
     * 売上単位(URITANI)
     */
    private String uritani;

    /**
     * 総数(SOKAZU)
     */
    private String sokazu;

    /**
     * 配布物名称(haifubutsunm)
     */
    private String haifubutsunm;

    /**
     * 価格区分(PRICEKBN)
     */
    private String pricekbn;

    /**
     * 標準価格切替日(HYOJUNPRICEKIRIKAEDD)
     */
    private String hyojunpricekirikaedd;

    /**
     * 現標準価格(GENZHYOJUNPRICE)
     */
    private String genzhyojunprice;

    /**
     * 現標準持ち帰り価格(GENZHYOJUNMOCHIKAERIPRICE)
     */
    private String genzhyojunmochikaeriprice;

    /**
     * 新標準価格(SHINHYOJUNPRICE)
     */
    private String shinhyojunprice;

    /**
     * 新標準持ち帰り価格(SHINHYOJUNMOCHIKAERIPRICE)
     */
    private String shinhyojunmochikaeriprice;

    /**
     * ショップ価格切替日(SHOPPRICEKIRIKAEDD)
     */
    private String shoppricekirikaedd;

    /**
     * 現ショップ価格(GENZSHOPPRICE)
     */
    private String genzshopprice;

    /**
     * 現ショップ持ち帰り価格(GENZSHOPMOCHIKAERIPRICE)
     */
    private String genzshopmochikaeriprice;

    /**
     * 新ショップ価格(SHINSHOPPRICE)
     */
    private String shinshopprice;

    /**
     * 新ショップ持ち帰り価格(SHINSHOPMOCHIKAERIPRICE)
     */
    private String shinshopmochikaeriprice;

    // ■:1 Base)Arai 2014-02-20 計上区分のキャンペーン初期表示切替対応(要望)
    /**
     * 販促区分(hansokukbn)
     */
    public String hansokukbn;

    // ■:2 Base)Arai 2014-02-20 計上区分のキャンペーン初期表示切替対応(要望)
    /**
     * 販促開始日(HANSOKUSTRTDD)
     */
    private String hansokustrtdd;

    /**
     * 販促終了日(HANSOKUSHRYDD)
     */
    private String hansokushrydd;

    /**
     * 販促価格(HANSOKUPRICE)
     */
    private String hansokuprice;

    /**
     * 販促持ち帰り価格(HANSOKUMOCHIKAERIPRICE)
     */
    private String hansokumochikaeriprice;
    // ■:1 Base)Arai 2014-01-23
    /**
     * 販促優先区分(hansokuyusenkbn)
     */
    public String hansokuyusenkbn;
    // ■:2 Base)Arai 2014-01-23

    /**
     * 得意先価格開始日(TTPRICESTRTDD)
     */
    private String ttpricestrtdd;

    /**
     * 得意先価格終了日(TTPRICESHRYDD)
     */
    private String ttpriceshrydd;

    /**
     * 得意先価格(TTPRICE)
     */
    private String ttprice;

    /**
     * 得意先持ち帰り価格(TTMOCHIKAERIPRICE)
     */
    private String ttmochikaeriprice;

    /**
     * 初期表示価格(SHOKIDSPLYPRICE)
     */
    private String shokidsplyprice;
    /**
     * フォーカスセットフラグ(setFocusFg)
     */
    public boolean focusFg;
    /**
     * 必須フラグ(requiredFg)
     */
    public boolean requiredFg;


    /**
     * 在庫単価(ZAIKOUNT)
     */
    private String zaikount;
    /**
     * 商品税区分(shntaxkbn)
     */
    private String shntaxkbn;
    /**
     * 計上区分名称(keijokbnnm)
     */
    private String keijokbnnm;
    /**
     * 販促名称(hansokunm)
     */
    private String hansokunm;

    // ■:1 Isac)hikoso 2007-05-31
    /**
     * 商品名称(shnnm)
     */
    public String shnnm;

    /**
     * 標準価格(hyojunprice)
     */
    public String hyojunprice;
    // ■:2 Isac)hikoso 2007-05-31


    // ■:1 tjs)nakano 2009-01-20
    /**
     * 登録者名(toshamei)
     */
    public String toshamei;

    /**
     * 更新者名(koshamei)
     */
    public String koshamei;
    // ■:1 tjs)nakano 2009-01-20


    /* 2017-06-29 IBIS)Hiraishi add begin */
    /**
     * 委託相手AP基本コード
     */
    public String itakuapkihoncd;

    /**
     * 委託相手ショップコード
     */
    public String itakushopcd;

    /**
     * 委託相手受注伝票番号
     */
    public String itakuacptdnpyno;

    /**
     * 委託相手枝番号
     */
    public String itakuedano;

    /**
     * 委託相手受注伝票明細番号
     */
    public String itakuacptdnpymeino;

    /**
     * 委託相手受注伝票明細枝番号
     */
    public String itakuacptdnpymeiedano;

    /**
     * 配布物AP基本コード
     */
    public String haifubutsuapkihoncd;

    /**
     * 配布物ショップコード
     */
    public String haifubutsushopcd;

    /**
     * 残回数
     */
    public String zankaisuu;

    /**
     * 総回数
     */
    public String soukaisuu;

    /**
     * 委託会社受注単価
     */
    public String itakucmpnyacptunt;

    /**
     * 委託会社受注単価消費税金額
     */
    public String itakucmpnyacptuntshohitaxkin;

    /**
     * 委託会社受注消費税金額
     */
    public String itakucmpnyacptshohitaxkin;

    /* 2017-06-29 IBIS)Hiraishi add end */

    // 2020/11/18 価格改定対応 ISAC)金谷 START
    /**
     * 得意先別セット商品マスタ.適用開始日(SET_TEKIYOSTRTDD)
     */
    private String setTekiyostrtdd;

    /**
     * 得意先別セット商品マスタ.適用終了日(SET_TEKIYOSHRYDD)
     */
    private String setTekiyoshrydd;

    /**
     * 得意先別セット商品マスタ.配達価格(SET_DLVRYPRICE)
     */
    private String setDlvryprice;

    /**
     * 得意先別セット商品マスタ.持ち帰り価格(SET_MOCHIKAERIPRICE)
     */
    private String setMochikaeriprice;
    // 2020/11/18 価格改定対応 ISAC)金谷 END

    /**
     * 在庫アラーム数量を取得します。
     * 
     * @return 在庫アラーム数量
     */
    public String getZaikoaramusuryo() {
        return this.shohinModel == null ? "0" : String.valueOf(shohinModel.getZaikoaramusuryo());
    }

    /**
     * 取扱区分を取得します。
     * 
     * @return 取扱区分
     */
    public String getTrtskikbn() {
        return this.shohinModel == null ? "" : this.shohinModel.getTrtskikbn();
    }

    /**
     * 在庫管理対象フラグを取得します。
     * 
     * @return 在庫管理対象フラグ
     */
    public String getZaikokanritrgtflg() {
        return this.shohinModel == null ? "" : this.shohinModel.getZaikokanritrgtflg();
    }

    /**
     * relationVoを取得する。
     * 
     * @return relationVo
     */
    public JuchuRelationVo getRelationVo() {
        return relationVo;
    }

    /**
     * relationVoを設定する。
     * 
     * @param relationVo 設定する relationVo
     */
    public void setRelationVo(JuchuRelationVo relationVo) {
        this.relationVo = relationVo;
    }

    /**
     * shohinModelを取得する。
     * 
     * @return shohinModel
     */
    public S02V001TEntity getShohinModel() {
        return shohinModel;
    }

    /**
     * shohinModelを設定する。
     * 
     * @param shohinModel 設定する shohinModel
     */
    public void setShohinModel(S02V001TEntity shohinModel) {
        this.shohinModel = shohinModel;
    }

    /**
     * 商品ＡＰ基本コードを設定します。
     * 
     * @param shnapkihoncd 商品ＡＰ基本コード
     */
    public void setShnapkihoncd(String shnapkihoncd) {
        this.shnapkihoncd = shnapkihoncd;
    }

    /**
     * 商品ＡＰ基本コードを取得します。
     * 
     * @return 商品ＡＰ基本コード
     */
    public String getShnapkihoncd() {
        return this.shnapkihoncd;
    }

    /**
     * 商品名称略称(shnnmryakusho)を設定する。
     * 
     * @param shnnmryakusho 商品名称略称
     */
    public void setShnnmryakusho(String shnnmryakusho) {
        this.shnnmryakusho = shnnmryakusho;
    }

    /**
     * 商品名称略称(shnnmryakusho)を取得する。
     * 
     * @return 商品名称略称(shnnmryakusho)
     */
    public String getShnnmryakusho() {
        return this.shnnmryakusho;
    }

    /**
     * 色(iro)を設定する。
     * 
     * @param iro 色
     */
    public void setIro(String iro) {
        this.iro = iro;
    }

    /**
     * 色(iro)を取得する。
     * 
     * @return 色(iro)
     */
    public String getIro() {
        return this.iro;
    }

    /**
     * サイズ(sz)を設定する。
     * 
     * @param sz サイズ
     */
    public void setSz(String sz) {
        this.sz = sz;
    }

    /**
     * サイズ(sz)を取得する。
     * 
     * @return サイズ(sz)
     */
    public String getSz() {
        return this.sz;
    }

    /**
     * 売上取扱単位(uritrtskitani)を設定する。
     * 
     * @param uritrtskitani 売上取扱単位
     */
    public void setUritrtskitani(String uritrtskitani) {
        this.uritrtskitani = uritrtskitani;
    }

    /**
     * 売上取扱単位(uritrtskitani)を取得する。
     * 
     * @return 売上取扱単位(uritrtskitani)
     */
    public String getUritrtskitani() {
        return this.uritrtskitani;
    }

    /**
     * 売上入数(uriirisu)を設定する。
     * 
     * @param uriirisu 売上入数
     */
    public void setUriirisu(String uriirisu) {
        this.uriirisu = uriirisu;
    }

    /**
     * 売上入数(uriirisu)を取得する。
     * 
     * @return 売上入数(uriirisu)
     */
    public String getUriirisu() {
        return this.uriirisu;
    }

    /**
     * 売上単位(uritani)を設定する。
     * 
     * @param uritani 売上単位
     */
    public void setUritani(String uritani) {
        this.uritani = uritani;
    }

    /**
     * 売上単位(uritani)を取得する。
     * 
     * @return 売上単位(uritani)
     */
    public String getUritani() {
        return this.uritani;
    }

    /**
     * 総数(sokazu)を設定する。
     * 
     * @param sokazu 総数
     */
    public void setSokazu(String sokazu) {
        this.sokazu = sokazu;
    }

    /**
     * 総数(sokazu)を取得する。
     * 
     * @return 総数(sokazu)
     */
    public String getSokazu() {
        return this.sokazu;
    }

    /**
     * 配布物名称を設定します。
     * 
     * @param haifubutsunm 配布物名称
     */
    public void setHaifubutsunm(String haifubutsunm) {
        this.haifubutsunm = haifubutsunm;
    }

    /**
     * 配布物名称を取得します。
     * 
     * @return 配布物名称
     */
    public String getHaifubutsunm() {
        return this.haifubutsunm;
    }

    /**
     * 価格区分(pricekbn)を設定する。
     * 
     * @param pricekbn 価格区分
     */
    public void setPricekbn(String pricekbn) {
        this.pricekbn = pricekbn;
    }

    /**
     * 価格区分(pricekbn)を取得する。
     * 
     * @return 価格区分(pricekbn)
     */
    public String getPricekbn() {
        return this.pricekbn;
    }

    /**
     * 標準価格切替日(hyojunpricekirikaedd)を設定する。
     * 
     * @param hyojunpricekirikaedd 標準価格切替日
     */
    public void setHyojunpricekirikaedd(String hyojunpricekirikaedd) {
        this.hyojunpricekirikaedd = hyojunpricekirikaedd;
    }

    /**
     * 標準価格切替日(hyojunpricekirikaedd)を取得する。
     * 
     * @return 標準価格切替日(hyojunpricekirikaedd)
     */
    public String getHyojunpricekirikaedd() {
        return this.hyojunpricekirikaedd;
    }

    /**
     * 現標準価格(genzhyojunprice)を設定する。
     * 
     * @param genzhyojunprice 現標準価格
     */
    public void setGenzhyojunprice(String genzhyojunprice) {
        this.genzhyojunprice = genzhyojunprice;
    }

    /**
     * 現標準価格(genzhyojunprice)を取得する。
     * 
     * @return 現標準価格(genzhyojunprice)
     */
    public String getGenzhyojunprice() {
        return this.genzhyojunprice;
    }

    /**
     * 現標準持ち帰り価格(genzhyojunmochikaeriprice)を設定する。
     * 
     * @param genzhyojunmochikaeriprice 現標準持ち帰り価格
     */
    public void setGenzhyojunmochikaeriprice(String genzhyojunmochikaeriprice) {
        this.genzhyojunmochikaeriprice = genzhyojunmochikaeriprice;
    }

    /**
     * 現標準持ち帰り価格(genzhyojunmochikaeriprice)を取得する。
     * 
     * @return 現標準持ち帰り価格(genzhyojunmochikaeriprice)
     */
    public String getGenzhyojunmochikaeriprice() {
        return this.genzhyojunmochikaeriprice;
    }

    /**
     * 新標準価格(shinhyojunprice)を設定する。
     * 
     * @param shinhyojunprice 新標準価格
     */
    public void setShinhyojunprice(String shinhyojunprice) {
        this.shinhyojunprice = shinhyojunprice;
    }

    /**
     * 新標準価格(shinhyojunprice)を取得する。
     * 
     * @return 新標準価格(shinhyojunprice)
     */
    public String getShinhyojunprice() {
        return this.shinhyojunprice;
    }

    /**
     * 新標準持ち帰り価格(shinhyojunmochikaeriprice)を設定する。
     * 
     * @param shinhyojunmochikaeriprice 新標準持ち帰り価格
     */
    public void setShinhyojunmochikaeriprice(String shinhyojunmochikaeriprice) {
        this.shinhyojunmochikaeriprice = shinhyojunmochikaeriprice;
    }

    /**
     * 新標準持ち帰り価格(shinhyojunmochikaeriprice)を取得する。
     * 
     * @return 新標準持ち帰り価格(shinhyojunmochikaeriprice)
     */
    public String getShinhyojunmochikaeriprice() {
        return this.shinhyojunmochikaeriprice;
    }

    /**
     * ショップ価格切替日(shoppricekirikaedd)を設定する。
     * 
     * @param shoppricekirikaedd ショップ価格切替日
     */
    public void setShoppricekirikaedd(String shoppricekirikaedd) {
        this.shoppricekirikaedd = shoppricekirikaedd;
    }

    /**
     * ショップ価格切替日(shoppricekirikaedd)を取得する。
     * 
     * @return ショップ価格切替日(shoppricekirikaedd)
     */
    public String getShoppricekirikaedd() {
        return this.shoppricekirikaedd;
    }

    /**
     * 現ショップ価格(genzshopprice)を設定する。
     * 
     * @param genzshopprice 現ショップ価格
     */
    public void setGenzshopprice(String genzshopprice) {
        this.genzshopprice = genzshopprice;
    }

    /**
     * 現ショップ価格(genzshopprice)を取得する。
     * 
     * @return 現ショップ価格(genzshopprice)
     */
    public String getGenzshopprice() {
        return this.genzshopprice;
    }

    /**
     * 現ショップ持ち帰り価格(genzshopmochikaeriprice)を設定する。
     * 
     * @param genzshopmochikaeriprice 現ショップ持ち帰り価格
     */
    public void setGenzshopmochikaeriprice(String genzshopmochikaeriprice) {
        this.genzshopmochikaeriprice = genzshopmochikaeriprice;
    }

    /**
     * 現ショップ持ち帰り価格(genzshopmochikaeriprice)を取得する。
     * 
     * @return 現ショップ持ち帰り価格(genzshopmochikaeriprice)
     */
    public String getGenzshopmochikaeriprice() {
        return this.genzshopmochikaeriprice;
    }

    /**
     * 新ショップ価格(shinshopprice)を設定する。
     * 
     * @param shinshopprice 新ショップ価格
     */
    public void setShinshopprice(String shinshopprice) {
        this.shinshopprice = shinshopprice;
    }

    /**
     * 新ショップ価格(shinshopprice)を取得する。
     * 
     * @return 新ショップ価格(shinshopprice)
     */
    public String getShinshopprice() {
        return this.shinshopprice;
    }

    /**
     * 新ショップ持ち帰り価格(shinshopmochikaeriprice)を設定する。
     * 
     * @param shinshopmochikaeriprice 新ショップ持ち帰り価格
     */
    public void setShinshopmochikaeriprice(String shinshopmochikaeriprice) {
        this.shinshopmochikaeriprice = shinshopmochikaeriprice;
    }

    /**
     * 新ショップ持ち帰り価格(shinshopmochikaeriprice)を取得する。
     * 
     * @return 新ショップ持ち帰り価格(shinshopmochikaeriprice)
     */
    public String getShinshopmochikaeriprice() {
        return this.shinshopmochikaeriprice;
    }

    // ■:1 Base)Arai 2014-02-20 計上区分のキャンペーン初期表示切替対応(要望)
    /**
     * 販促区分を設定します。
     * 
     * @param hansokukbn 販促区分
     */
    public void setHansokukbn(String hansokukbn) {
        this.hansokukbn = hansokukbn;
    }

    /**
     * 販促区分を取得します。
     * 
     * @return 販促区分
     */
    public String getHansokukbn() {
        return this.hansokukbn;
    }

    // ■:2 Base)Arai 2014-02-20 計上区分のキャンペーン初期表示切替対応(要望)
    /**
     * 販促開始日(hansokustrtdd)を設定する。
     * 
     * @param hansokustrtdd 販促開始日
     */
    public void setHansokustrtdd(String hansokustrtdd) {
        this.hansokustrtdd = hansokustrtdd;
    }

    /**
     * 販促開始日(hansokustrtdd)を取得する。
     * 
     * @return 販促開始日(hansokustrtdd)
     */
    public String getHansokustrtdd() {
        return this.hansokustrtdd;
    }

    /**
     * 販促終了日(hansokushrydd)を設定する。
     * 
     * @param hansokushrydd 販促終了日
     */
    public void setHansokushrydd(String hansokushrydd) {
        this.hansokushrydd = hansokushrydd;
    }

    /**
     * 販促終了日(hansokushrydd)を取得する。
     * 
     * @return 販促終了日(hansokushrydd)
     */
    public String getHansokushrydd() {
        return this.hansokushrydd;
    }

    /**
     * 販促価格(hansokuprice)を設定する。
     * 
     * @param hansokuprice 販促価格
     */
    public void setHansokuprice(String hansokuprice) {
        this.hansokuprice = hansokuprice;
    }

    /**
     * 販促価格(hansokuprice)を取得する。
     * 
     * @return 販促価格(hansokuprice)
     */
    public String getHansokuprice() {
        return this.hansokuprice;
    }

    /**
     * 販促持ち帰り価格(hansokumochikaeriprice)を設定する。
     * 
     * @param hansokumochikaeriprice 販促持ち帰り価格
     */
    public void setHansokumochikaeriprice(String hansokumochikaeriprice) {
        this.hansokumochikaeriprice = hansokumochikaeriprice;
    }

    /**
     * 販促持ち帰り価格(hansokumochikaeriprice)を取得する。
     * 
     * @return 販促持ち帰り価格(hansokumochikaeriprice)
     */
    public String getHansokumochikaeriprice() {
        return this.hansokumochikaeriprice;
    }

    // ■:1 Base)Arai 2014-01-23
    /**
     * 販促優先区分を設定します。
     * 
     * @param hansokuyusenkbn 販促優先区分
     */
    public void setHansokuyusenkbn(String hansokuyusenkbn) {
        this.hansokuyusenkbn = hansokuyusenkbn;
    }

    /**
     * 販促優先区分を取得します。
     * 
     * @return 販促優先区分
     */
    public String getHansokuyusenkbn() {
        return this.hansokuyusenkbn;
    }
    // ■:2 Base)Arai 2014-01-23

    /**
     * 得意先価格開始日(ttpricestrtdd)を設定する。
     * 
     * @param ttpricestrtdd 得意先価格開始日
     */
    public void setTtpricestrtdd(String ttpricestrtdd) {
        this.ttpricestrtdd = ttpricestrtdd;
    }

    /**
     * 得意先価格開始日(ttpricestrtdd)を取得する。
     * 
     * @return 得意先価格開始日(ttpricestrtdd)
     */
    public String getTtpricestrtdd() {
        return this.ttpricestrtdd;
    }

    /**
     * 得意先価格終了日(ttpriceshrydd)を設定する。
     * 
     * @param ttpriceshrydd 得意先価格終了日
     */
    public void setTtpriceshrydd(String ttpriceshrydd) {
        this.ttpriceshrydd = ttpriceshrydd;
    }

    /**
     * 得意先価格終了日(ttpriceshrydd)を取得する。
     * 
     * @return 得意先価格終了日(ttpriceshrydd)
     */
    public String getTtpriceshrydd() {
        return this.ttpriceshrydd;
    }

    /**
     * 得意先価格(ttprice)を設定する。
     * 
     * @param ttprice 得意先価格
     */
    public void setTtprice(String ttprice) {
        this.ttprice = ttprice;
    }

    /**
     * 得意先価格(ttprice)を取得する。
     * 
     * @return 得意先価格(ttprice)
     */
    public String getTtprice() {
        return this.ttprice;
    }

    /**
     * 得意先持ち帰り価格(ttmochikaeriprice)を設定する。
     * 
     * @param ttmochikaeriprice 得意先持ち帰り価格
     */
    public void setTtmochikaeriprice(String ttmochikaeriprice) {
        this.ttmochikaeriprice = ttmochikaeriprice;
    }

    /**
     * 得意先持ち帰り価格(ttmochikaeriprice)を取得する。
     * 
     * @return 得意先持ち帰り価格(ttmochikaeriprice)
     */
    public String getTtmochikaeriprice() {
        return this.ttmochikaeriprice;
    }

    /**
     * 初期表示価格(shokidsplyprice)を設定する。
     * 
     * @param shokidsplyprice 初期表示価格
     */
    public void setShokidsplyprice(String shokidsplyprice) {
        this.shokidsplyprice = shokidsplyprice;
    }

    /**
     * 初期表示価格(shokidsplyprice)を取得する。
     * 
     * @return 初期表示価格(shokidsplyprice)
     */
    public String getShokidsplyprice() {
        return this.shokidsplyprice;
    }

    /**
     * 在庫単価(zaikount)を設定する。
     * 
     * @param zaikount 在庫単価
     */
    public void setZaikount(String zaikount) {
        this.zaikount = zaikount;
    }

    /**
     * 在庫単価(zaikount)を取得する。
     * 
     * @return 在庫単価(zaikount)
     */
    public String getZaikount() {
        return this.zaikount;
    }

    /**
     * shntaxkbnを取得する。
     * 
     * @return shntaxkbn
     */
    public String getShntaxkbn() {
        return shntaxkbn;
    }

    /**
     * shntaxkbnを設定する。
     * 
     * @param shntaxkbn 設定する shntaxkbn
     */
    public void setShntaxkbn(String shntaxkbn) {
        this.shntaxkbn = shntaxkbn;
    }


    /**
     * keijokbnnmを取得する。
     * 
     * @return keijokbnnm
     */
    public String getKeijokbnnm() {
        return keijokbnnm;
    }

    /**
     * keijokbnnmを設定する。
     * 
     * @param keijokbnnm 設定する keijokbnnm
     */
    public void setKeijokbnnm(String keijokbnnm) {
        this.keijokbnnm = keijokbnnm;
    }

    /**
     * formIndexを取得する。
     * 
     * @return formIndex
     */
    public int getFormIndex() {
        return formIndex;
    }

    /**
     * formIndexを設定する。
     * 
     * @param formIndex 設定する formIndex
     */
    public void setFormIndex(int formIndex) {
        this.formIndex = formIndex;
    }

    /**
     * focusFgを取得する。
     * 
     * @return focusFg
     */
    public boolean isFocusFg() {
        return focusFg;
    }

    /**
     * focusFgを設定する。
     * 
     * @param focusFg 設定する focusFg
     */
    public void setFocusFg(boolean focusFg) {
        this.focusFg = focusFg;
    }

    /**
     * requiredFgを取得する。
     * 
     * @return requiredFg
     */
    public boolean isRequiredFg() {
        return requiredFg;
    }

    /**
     * requiredFgを設定する。
     * 
     * @param requiredFg 設定する requiredFg
     */
    public void setRequiredFg(boolean requiredFg) {
        this.requiredFg = requiredFg;
    }

    /**
     * hansokunmを取得する。
     * 
     * @return hansokunm
     */
    public String getHansokunm() {
        return hansokunm;
    }

    /**
     * hansokunmを設定する。
     * 
     * @param hansokunm 設定する hansokunm
     */
    public void setHansokunm(String hansokunm) {
        this.hansokunm = hansokunm;
    }

    // ■:1 Isac)hikoso 2007-05-31
    /**
     * 商品名称を取得します。
     * 
     * @return 商品名称
     */
    public String getShnnm() {
        return shnnm;
    }

    /**
     * 商品名称を設定します。
     * 
     * @param shnnm 設定する
     */
    public void setShnnm(String shnnm) {
        this.shnnm = shnnm;
    }

    /**
     * 標準価格を取得します。
     * 
     * @return 標準価格
     */
    public String getHyojunprice() {
        return hyojunprice;
    }

    /**
     * 標準価格を取得します。
     * 
     * @param hyojunprice 設定する
     */
    public void setHyojunprice(String hyojunprice) {
        this.hyojunprice = hyojunprice;
    }
    // ■:2 Isac)hikoso 2007-05-31

    // ■:1 tjs)nakano 2009-01-20

    /**
     * 更新者名を取得します。
     * 
     * @return 更新者名
     */
    public String getKoshamei() {
        return koshamei;
    }

    /**
     * 更新者名を取得します。
     * 
     * @param hyojunprice 設定する
     */
    public void setKoshamei(String koshamei) {
        this.koshamei = koshamei;
    }

    /**
     * 登録者名を取得します。
     * 
     * @return 更新者名
     */
    public String getToshamei() {
        return toshamei;
    }

    /**
     * 登録者名を取得します。
     * 
     * @param hyojunprice 設定する
     */
    public void setToshamei(String toshamei) {
        this.toshamei = toshamei;
    }

    // ■:2 tjs)nakano 2009-01-20

    /* 2017-06-29 IBIS)Hiraishi add begin */
    /**
     * 委託相手AP基本コードを取得します。
     * 
     * @return 委託相手AP基本コード
     */
    public String getItakuapkihoncd() {
        return itakuapkihoncd;
    }

    /**
     * 委託相手AP基本コードを取得します。
     * 
     * @param itakuapkihoncd 設定する
     */
    public void setItakuapkihoncd(String itakuapkihoncd) {
        this.itakuapkihoncd = itakuapkihoncd;
    }

    /**
     * 委託相手ショップコードを取得します。
     * 
     * @return 委託相手ショップコード
     */
    public String getItakushopcd() {
        return itakushopcd;
    }

    /**
     * 委託相手ショップコードを取得します。
     * 
     * @param itakuapkihoncd 設定する
     */
    public void setItakushopcd(String itakushopcd) {
        this.itakushopcd = itakushopcd;
    }

    /**
     * 委託相手受注伝票番号を取得します。
     * 
     * @return 委託相手受注伝票番号
     */
    public String getItakuacptdnpyno() {
        return itakuacptdnpyno;
    }

    /**
     * 委託相手受注伝票番号を取得します。
     * 
     * @param itakuacptdnpyno 設定する
     */
    public void setItakuacptdnpyno(String itakuacptdnpyno) {
        this.itakuacptdnpyno = itakuacptdnpyno;
    }

    /**
     * 委託相手枝番号を取得します。
     * 
     * @return 委託相手枝番号
     */
    public String getItakuedano() {
        return itakuedano;
    }

    /**
     * 委託相手枝番号を取得します。
     * 
     * @param itakuedano 設定する
     */
    public void setItakuedano(String itakuedano) {
        this.itakuedano = itakuedano;
    }

    /**
     * 委託相手受注伝票明細番号を取得します。
     * 
     * @return 委託相手受注伝票明細番号
     */
    public String getItakuacptdnpymeino() {
        return itakuacptdnpymeino;
    }

    /**
     * 委託相手受注伝票明細番号を取得します。
     * 
     * @param itakuacptdnpymeino 設定する
     */
    public void setItakuacptdnpymeino(String itakuacptdnpymeino) {
        this.itakuacptdnpymeino = itakuacptdnpymeino;
    }

    /**
     * 委託相手受注伝票明細枝番号を取得します。
     * 
     * @return 委託相手受注伝票明細枝番号
     */
    public String getItakuacptdnpymeiedano() {
        return itakuacptdnpymeiedano;
    }

    /**
     * 委託相手受注伝票明細枝番号を取得します。
     * 
     * @param itakuacptdnpymeiedano 設定する
     */
    public void setItakuacptdnpymeiedano(String itakuacptdnpymeiedano) {
        this.itakuacptdnpymeiedano = itakuacptdnpymeiedano;
    }


    /**
     * 配布物AP基本コードを取得します。
     * 
     * @return 配布物AP基本コード
     */
    public String getHaifubutsuapkihoncd() {
        return haifubutsuapkihoncd;
    }

    /**
     * 配布物AP基本コードを取得します。
     * 
     * @param haifubutsuapkihoncd 設定する
     */
    public void setHaifubutsuapkihoncd(String haifubutsuapkihoncd) {
        this.haifubutsuapkihoncd = haifubutsuapkihoncd;
    }

    /**
     * 配布物ショップコードを取得します。
     * 
     * @return 配布物ショップコード
     */
    public String getHaifubutsushopcd() {
        return haifubutsushopcd;
    }

    /**
     * 配布物ショップコードを取得します。
     * 
     * @param haifubutsushopcd 設定する
     */
    public void setHaifubutsushopcd(String haifubutsushopcd) {
        this.haifubutsushopcd = haifubutsushopcd;
    }

    /**
     * 残回数を取得します。
     * 
     * @return 残回数
     */
    public String getZankaisuu() {
        return zankaisuu;
    }

    /**
     * 残回数を取得します。
     * 
     * @param zankaisuu 設定する
     */
    public void setZankaisuu(String zankaisuu) {
        this.zankaisuu = zankaisuu;
    }

    /**
     * 総回数を取得します。
     * 
     * @return 総回数
     */
    public String getSoukaisuu() {
        return soukaisuu;
    }

    /**
     * 総回数を取得します。
     * 
     * @param soukaisuu 設定する
     */
    public void setSoukaisuu(String soukaisuu) {
        this.soukaisuu = soukaisuu;
    }

    /**
     * 委託会社受注単価を取得します。
     * 
     * @return 委託会社受注単価
     */
    public BigDecimal getItakucmpnyacptunt() {
        if (itakucmpnyacptunt == null) {
            return null;
        } else {
            return new BigDecimal(itakucmpnyacptunt);
        }
    }

    /**
     * 委託会社受注単価を取得します。
     * 
     * @param itakucmpnyacptunt 設定する
     */
    public void setItakucmpnyacptunt(String itakucmpnyacptunt) {
        this.itakucmpnyacptunt = itakucmpnyacptunt;
    }

    /**
     * 委託会社受注単価消費税金額を取得します。
     * 
     * @return 委託会社受注単価消費税金額
     */
    public Long getItakucmpnyacptuntshohitaxkin() {
        if (itakucmpnyacptuntshohitaxkin == null) {
            return null;
        } else {
            return Long.valueOf(itakucmpnyacptuntshohitaxkin);
        }
    }

    /**
     * 委託会社受注単価消費税金額を取得します。
     * 
     * @param itakucmpnyacptuntshohitaxkin 設定する
     */
    public void setItakucmpnyacptuntshohitaxkin(String itakucmpnyacptuntshohitaxkin) {
        this.itakucmpnyacptuntshohitaxkin = itakucmpnyacptuntshohitaxkin;
    }

    /**
     * 委託会社受注消費税金額を取得します。
     * 
     * @return 委託会社受注消費税金額
     */
    public Long getItakucmpnyacptshohitaxkin() {
        if (itakucmpnyacptshohitaxkin == null) {
            return null;
        } else {
            return Long.valueOf(itakucmpnyacptshohitaxkin);
        }
    }

    /**
     * 委託会社受注消費税金額を取得します。
     * 
     * @param itakucmpnyacptshohitaxkin 設定する
     */
    public void setItakucmpnyacptshohitaxkin(String itakucmpnyacptshohitaxkin) {
        this.itakucmpnyacptshohitaxkin = itakucmpnyacptshohitaxkin;
    }

    /* 2017-06-29 IBIS)Hiraishi add end */

    // 2020/11/18 価格改定対応 ISAC)金谷 START
    /**
     * 得意先別セット商品マスタ.適用開始日を取得します。
     * 
     * @return setTekiyostrtdd
     */
    public String getSetTekiyostrtdd() {
        return setTekiyostrtdd;
    }

    /**
     * 得意先別セット商品マスタ.適用開始日を設定します。
     * 
     * @param setTekiyostrtdd セットする setTekiyostrtdd
     */
    public void setSetTekiyostrtdd(String setTekiyostrtdd) {
        this.setTekiyostrtdd = setTekiyostrtdd;
    }

    /**
     * 得意先別セット商品マスタ.適用終了日を取得します。
     * 
     * @return setTekiyoshrydd
     */
    public String getSetTekiyoshrydd() {
        return setTekiyoshrydd;
    }

    /**
     * 得意先別セット商品マスタ.適用終了日を設定します。
     * 
     * @param setTekiyoshrydd セットする setTekiyoshrydd
     */
    public void setSetTekiyoshrydd(String setTekiyoshrydd) {
        this.setTekiyoshrydd = setTekiyoshrydd;
    }

    /**
     * 得意先別セット商品マスタ.配達価格を取得します。
     * 
     * @return setDlvryprice
     */
    public String getSetDlvryprice() {
        return setDlvryprice;
    }

    /**
     * 得意先別セット商品マスタ.配達価格を設定します。
     * 
     * @param setDlvryprice セットする setDlvryprice
     */
    public void setSetDlvryprice(String setDlvryprice) {
        this.setDlvryprice = setDlvryprice;
    }

    /**
     * 得意先別セット商品マスタ.持ち帰り価格を取得します。
     * 
     * @return setMochikaeriprice
     */
    public String getSetMochikaeriprice() {
        return setMochikaeriprice;
    }

    /**
     * 得意先別セット商品マスタ.持ち帰り価格を設定します。
     * 
     * @param setMochikaeriprice セットする setMochikaeriprice
     */
    public void setSetMochikaeriprice(String setMochikaeriprice) {
        this.setMochikaeriprice = setMochikaeriprice;
    }
    // 2020/11/18 価格改定対応 ISAC)金谷 END


    // ■:1 Isac)Shiba 2020-12-16 価格改定対応
    /**
     * 現在の標準単価をセットします ※リファクタリングにてS01JuchuServiceから移動
     * 
     * @param dlvryKbn 配達区分
     * @param dlvryYtiDd 配達予定日
     */
    public void setCurrentHyojununt(String dlvryKbn, String dlvryYtiDd) {
        if (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(dlvryKbn)) {
            this.setHyojununt(new BigDecimal(BusinessUtil.getCurrentValue(
                this.getGenzhyojunmochikaeriprice(), this.getShinhyojunmochikaeriprice(),
                this.getHyojunpricekirikaedd(), dlvryYtiDd)));
        } else {
            this.setHyojununt(new BigDecimal(BusinessUtil.getCurrentValue(this.getGenzhyojunprice(),
                this.getShinhyojunprice(), this.getHyojunpricekirikaedd(), dlvryYtiDd)));
        }
    }

    /**
     * 現在の受注単価をセットします ※リファクタリングにてS01JuchuServiceから移動
     *
     * @param dlvryKbn 配達区分
     * @param dlvryYtiDd 配達予定日
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public void setCurrentAcptunt(String dlvryKbn, String dlvryYtiDd) {

        // 計上区分＝「経費」の時は受注単価をショップ商品マスタの在庫単価にする
        if (CommonConstants.KBN_KEIJO_KEIHI.equals(this.getKeijokbn())) {
            this.setAcptunt(new BigDecimal(BusinessUtil.getString(this.getZaikount(), "0")));

            // 計上区分＝「売上」「キャンペーン」の時は得意先別商品別、販促単価の適用判定を行う
        } else if (CommonConstants.KBN_KEIJO_URIAGE.equals(this.getKeijokbn())
            || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(this.getKeijokbn())) {
            String hansokuunt = getCampaignTanka(dlvryKbn, dlvryYtiDd);
            String tokuisakiunt = getTokuisakiTanka(dlvryKbn, dlvryYtiDd);
            String tokuisakisetunt = getTokuisakiSetTanka(dlvryKbn, dlvryYtiDd);

            // 得意先別商品別単価の適用期間内の場合は受注単価を得意先別商品別単価にする
            if (tokuisakiunt != null) {
                this.setKeijokbn(CommonConstants.KBN_KEIJO_URIAGE);
                this.setAcptunt(new BigDecimal(tokuisakiunt));

                // 得意先別セット商品別単価の適用期間内の場合は受注単価を得意先別セット商品別単価にする
            } else if (tokuisakisetunt != null) {
                this.setKeijokbn(CommonConstants.KBN_KEIJO_URIAGE);
                this.setAcptunt(new BigDecimal(tokuisakisetunt));

            } else {
                // 計上区分がキャンペーン（お気に入りマスタからの登録）の場合は単価のみ設定
                if (CommonConstants.KBN_KEIJO_CAMPAIGN.equals(this.getKeijokbn())) {
                    // 販促期間内は販促価格をセット
                    if (hansokuunt != null) {
                        this.setAcptunt(new BigDecimal(hansokuunt));
                    }
                    // 販促期間外は0円をセット
                    else {
                        this.setAcptunt(new BigDecimal("0"));
                    }
                }
                // 行挿入や行追加などの計上区分：売上の場合、販促設定適用範囲内ならキャンペーンに設定する
                else {
                    // 販促期間内＋販促設定：対象内
                    if (hansokuunt != null) {
                        // 販促優先区分＝"する"の場合は計上区分をキャンペーン、販促価格に設定
                        if (CommonConstants.FLG_ON.equals(this.getHansokuyusenkbn())) {
                            this.setKeijokbn(CommonConstants.KBN_KEIJO_CAMPAIGN);
                            this.setAcptunt(new BigDecimal(hansokuunt));
                        }
                        // 販促優先区分＝"しない"の場合、売上価格のみ設定
                        else {
                            // 配達区分＝「持ち帰り」の場合
                            if (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(dlvryKbn)) {
                                this.setAcptunt(new BigDecimal(
                                    BusinessUtil.getCurrentValue(this.getGenzshopmochikaeriprice(),
                                        this.getShinshopmochikaeriprice(),
                                        this.getShoppricekirikaedd(), dlvryYtiDd)));

                                // 配達区分＝「配達」の場合
                            } else {
                                this.setAcptunt(new BigDecimal(BusinessUtil.getCurrentValue(
                                    this.getGenzshopprice(), this.getShinshopprice(),
                                    this.getShoppricekirikaedd(), dlvryYtiDd)));
                            }
                        }
                    }
                    // 販促期間外＋販促設定：対象外 売上価格を設定
                    else {
                        // 配達区分＝「持ち帰り」の場合
                        if (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(dlvryKbn)) {
                            this.setAcptunt(new BigDecimal(
                                BusinessUtil.getCurrentValue(this.getGenzshopmochikaeriprice(),
                                    this.getShinshopmochikaeriprice(), this.getShoppricekirikaedd(),
                                    dlvryYtiDd)));

                            // 配達区分＝「配達」の場合
                        } else {
                            this.setAcptunt(new BigDecimal(BusinessUtil.getCurrentValue(
                                this.getGenzshopprice(), this.getShinshopprice(),
                                this.getShoppricekirikaedd(), dlvryYtiDd)));
                        }
                    }
                }
            }

            // 計上区分＝上記以外の時は０にする
        } else {
            this.setAcptunt(new BigDecimal("0"));
        }
    }

    /**
     * キャンペーン単価を取得します。 （キャンペーン対象外の場合はnullになります） ※リファクタリングにてS01JuchuServiceから移動
     *
     * @param dlvryKbn 配達区分
     * @param dlvryYtiDd 配達予定日
     * @return String 結果
     */
    public String getCampaignTanka(String dlvryKbn, String dlvryYtiDd) {

        // 販促設定：対象外の場合はキャンペーン対象外とする
        if (CommonConstants.FLG_OFF.equals(this.getHansokukbn())) {
            return null;
        }

        // 適用期間の判定
        if (BusinessUtil.checkDatePeriod(dlvryYtiDd, this.getHansokustrtdd(),
            this.getHansokushrydd())) {
            return (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(dlvryKbn))
                ? this.getHansokumochikaeriprice()
                : this.getHansokuprice();

        } else {
            return null;
        }
    }

    /**
     * 得意先別商品別単価を取得します。 （適用期間外の場合はnullになります） ※リファクタリングにてS01JuchuServiceから移動
     *
     * @param dlvryKbn 配達区分
     * @param dlvryYtiDd 配達予定日
     * @return String 結果
     */
    public String getTokuisakiTanka(String dlvryKbn, String dlvryYtiDd) {

        // 適用期間の判定
        if (BusinessUtil.checkDatePeriod(dlvryYtiDd, this.getTtpricestrtdd(),
            this.getTtpriceshrydd())) {
            return (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(dlvryKbn))
                ? this.getTtmochikaeriprice()
                : this.getTtprice();

        } else {
            return null;
        }
    }

    /**
     * 得意先別セット商品別単価を取得します。 （適用期間外の場合はnullになります） ※リファクタリングにてS01JuchuServiceから移動
     *
     * @param dlvryKbn 配達区分
     * @param dlvryYtiDd 配達予定日
     * @return String 結果
     */
    public String getTokuisakiSetTanka(String dlvryKbn, String dlvryYtiDd) {

        // 適用期間の判定
        if (BusinessUtil.checkDatePeriod(dlvryYtiDd, this.getSetTekiyostrtdd(),
            this.getSetTekiyoshrydd())) {
            return (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(dlvryKbn))
                ? this.getSetMochikaeriprice()
                : this.getSetDlvryprice();

        } else {
            return null;
        }
    }


    //
    // JavaScriptで日付変更時に特別単価を再設定できるように特別単価情報を取得する
    /**
     * 得意先別特別単価リスト
     */
    private List<TokuisakiTokubetsuUntVo> tokubetsuUntList;

    /**
     * 得意先別特別単価リストを取得します。
     *
     * @return
     */
    public List<TokuisakiTokubetsuUntVo> getTokubetsuUntList() {
        return tokubetsuUntList;
    }

    /**
     * 得意先別特別単価リストを設定します。
     *
     * @param tokubetsuUntList
     */
    public void setTokubetsuUntList(List<TokuisakiTokubetsuUntVo> tokubetsuUntList) {
        this.tokubetsuUntList = tokubetsuUntList;
    }

}
