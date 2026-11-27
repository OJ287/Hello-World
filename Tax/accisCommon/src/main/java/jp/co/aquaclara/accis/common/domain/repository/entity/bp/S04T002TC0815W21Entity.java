package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import java.util.List;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntityKey;
import lombok.Builder;
import lombok.Data;

/**
 * Database Table Remarks: 受注配達明細
 *
 * This class corresponds to the database table S04T002T
 */
@Data
public class S04T002TC0815W21Entity extends S04T002TEntityKey implements Serializable {

    /**
     * Database Column Remarks: 価格取得基準日
     *
     */
    private String kakakugetbasedd;

    /**
     * Database Column Remarks: AP基本コード
     *
     */
    private String apkihoncd;

    /**
     * Database Column Remarks: ショップコード
     *
     */
    private String shopcd;

    /**
     * Database Column Remarks: 受注伝票番号
     *
     */
    private String acptdnpyno;

    /**
     * Database Column Remarks: 委託相手AP基本コード
     *
     */
    private String itakuaiteapkihoncd;

    /**
     * Database Column Remarks: 委託相手ショップコード
     *
     */
    private String itakuaiteshopcd;

    /**
     * Database Column Remarks: 委託相手受注伝票番号
     *
     */
    private String itakuaiteacptdnpyno;

    /**
     * Database Column Remarks: 委託相手枝番号
     *
     */
    private String itakuaiteedano;

    /**
     * Database Column Remarks: 委託相手受注伝票明細番号
     *
     */
    private String itakuaiteacptdnpymeisaino;

    /**
     * Database Column Remarks: 委託相手受注伝票明細枝番号
     *
     */
    private String itakuaiteacptdnpymeisaiedano;

    /**
     * Database Column Remarks: 大分類コード
     *
     */
    private String lctgrycd;

    /**
     * Database Column Remarks: 中分類コード
     *
     */
    private String mctgrycd;

    /**
     * Database Column Remarks: 小分類コード
     *
     */
    private String sctgrycd;

    /**
     * Database Column Remarks: 商品コード
     *
     */
    private String shncd;

    /**
     * Database Column Remarks: 配布物AP基本コード
     *
     */
    private String haifubutsuapkihoncd;

    /**
     * Database Column Remarks: 配布物ショップコード
     *
     */
    private String haifubutsushopcd;

    /**
     * Database Column Remarks: 配布物コード
     *
     */
    private String haifubutsucd;

    /**
     * Database Column Remarks: 型式
     *
     */
    private String katasiki;

    /**
     * Database Column Remarks: 計上区分
     *
     */
    private String keijokbn;

    /**
     * Database Column Remarks: 受注数量
     *
     */
    private String acptsuryo;

    /**
     * Database Column Remarks: 標準単価
     *
     */
    private String hyojununt;

    /**
     * Database Column Remarks: 受注単価
     *
     */
    private String acptunt;

    /**
     * Database Column Remarks: 受注金額
     *
     */
    private String acptkin;

    /**
     * Database Column Remarks: 受注単価消費税
     *
     */
    private String acptuntshohitaxkin;

    /**
     * Database Column Remarks: 受注消費税
     *
     */
    private String acptshohitaxkin;

    /**
     * Database Column Remarks: 委託会社受注単価
     *
     */
    private String itakucmpnyacptunt;

    /**
     * Database Column Remarks: 委託会社受注単価消費税金額
     *
     */
    private String itakucmpnyacptuntshohitaxkin;

    /**
     * Database Column Remarks: 委託会社消費税金額
     *
     */
    private String itakucmpnyacptshohitaxkin;

    /**
     * Database Column Remarks: 納品数量
     *
     */
    private String nohinsuryo;

    /**
     * Database Column Remarks: 納品単価
     *
     */
    private String nohinunt;

    /**
     * Database Column Remarks: 納品金額
     *
     */
    private String nohinkin;

    /**
     * Database Column Remarks: 納品単価消費税金額
     *
     */
    private String nohinuntshohitaxkin;

    /**
     * Database Column Remarks: 納品消費税金額
     *
     */
    private String nohinshohitaxkin;

    /**
     * Database Column Remarks: 残回収
     *
     */
    private String zankaisu;

    /**
     * Database Column Remarks: 総回数
     *
     */
    private String soukaisu;

    /**
     * Database Column Remarks: 税区分
     *
     */
    private String taxkbn;

    /**
     * Database Column Remarks: 有償区分
     *
     */
    private String yusyokbn;

    /**
     * Database Column Remarks: 明細区分
     *
     */
    private String meisaikbn;

    /**
     * Database Column Remarks: 入力明細区分
     *
     */
    private String nyuryokumeisaiflg;

    /**
     * Database Column Remarks: 単品明細フラグ
     *
     */
    private String tanpinmeisaiflg;

    /**
     * Database Column Remarks: セット商品コード
     *
     */
    private String stshncd;

    /**
     * Database Column Remarks: 明細備考
     *
     */
    private String meisaibiko;

    /**
     * Database Column Remarks: データ作成元区分
     *
     */
    private String datsksmotokbn;

    /**
     * Database Column Remarks: 有効区分
     *
     */
    private String yukokbn;

    /**
     * Database Column Remarks: 更新システム日付
     *
     */
    private String kshnsystmymd;

    /**
     * Database Column Remarks: 更新システム時間
     *
     */
    private String kshnsystmjikan;

    /**
     * Database Column Remarks: 商品AP基本コード
     *
     */
    private String shnapkihoncd;

    /**
     * Database Column Remarks: 商品名称略称
     *
     */
    private String shnnmryakusho;

    /**
     * Database Column Remarks: 価格区分
     *
     */
    private String pricekbn;

    /**
     * Database Column Remarks: 標準単価
     *
     */
    private String hyojunpricekirikaedd;

    /**
     * Database Column Remarks: 現在標準価格
     *
     */
    private String genzhyojunprice;

    /**
     * Database Column Remarks: 新標準価格
     *
     */
    private String shinhyojunprice;

    /**
     * Database Column Remarks: 現在持ち帰り価格
     *
     */
    private String genzmochikaeriprice;

    /**
     * Database Column Remarks: 新持ち帰り価格
     *
     */
    private String shinmochikaeriprice;

    /**
     * Database Column Remarks: オープン価格切替日
     *
     */
    private String opnpricekirikaedd;

    /**
     * Database Column Remarks: 現在オープン価格
     *
     */
    private String genzopnprice;

    /**
     * Database Column Remarks: 新オープン価格
     *
     */
    private String shinopnprice;

    /**
     * Database Column Remarks: 現在オープン持ち帰り価格
     *
     */
    private String genzopnmochikaeriprice;

    /**
     * Database Column Remarks: 新オープン持ち帰り価格
     *
     */
    private String shinopnmochikaeriprice;

    /**
     * Database Column Remarks: 販促区分
     *
     */
    private String hansokukbn;

    /**
     * Database Column Remarks: 販促開始日
     *
     */
    private String hansokustrtdd;

    /**
     * Database Column Remarks: 販促終了日
     *
     */
    private String hansokushrydd;

    /**
     * Database Column Remarks: 販促配達価格
     *
     */
    private String hansokudlvryprice;

    /**
     * Database Column Remarks: 販促持ち帰り価格
     *
     */
    private String hansokumochikaeriprice;

    /**
     * Database Column Remarks: 販促優先区分
     *
     */
    private String hansokuyusenkbn;

    /**
     * Database Column Remarks: 在庫単価
     *
     */
    private String zaikount;

    /**
     * Database Column Remarks: 色
     *
     */
    private String iro;

    /**
     * Database Column Remarks: サイズ
     *
     */
    private String sz;

    /**
     * Database Column Remarks: 売上取扱単位
     *
     */
    private String uritrtskitani;

    /**
     * Database Column Remarks: 売上入数
     *
     */
    private String uriirisu;

    /**
     * Database Column Remarks: 売上単位
     *
     */
    private String uritani;

    /**
     * Database Column Remarks: 税設定区分
     *
     */
    private String taxsetteikbn;

    /**
     * Database Column Remarks: 商品税区分
     *
     */
    private String shntaxkbn;

    /**
     * Database Column Remarks: 在庫アラーム数量
     *
     */
    private String zaikoaramusuryo;

    /**
     * Database Column Remarks: 取り扱い区分
     *
     */
    private String trtskikbn;

    /**
     * Database Column Remarks: 特別単価区分
     *
     */
    private String tokubetsuuntkbn;

    /**
     * Database Column Remarks: 適用開始日
     *
     */
    private String tekiyostrtdd;

    /**
     * Database Column Remarks: 適用終了日
     *
     */
    private String tekiyoshrydd;

    /**
     * Database Column Remarks: 配達価格
     *
     */
    private String dlvryprice;

    /**
     * Database Column Remarks: 持ち帰り価格
     *
     */
    private String mochikaeriprice;

    /**
     * Database Column Remarks: 計上区分名称
     *
     */
    private String keijokbnnm;

    /**
     * Database Column Remarks: 配布物名称
     *
     */
    private String haifubutsunm;

    /**
     * Database Column Remarks: 総数
     *
     */
    private String sokazu;

    /**
     * Database Column Remarks: 商品名称略称
     *
     */
    private String shnnm;

    /**
     * Database Column Remarks: 標準価格
     *
     */
    private String hyojunprice;

    /**
     * Database Column Remarks: 売上伝票
     *
     */
    private String uridnpyno;

    /**
     * Database Column Remarks: 売上伝票番号
     *
     */
    private String uridnpyedano;

    /**
     * Database Column Remarks: 売上伝票明細番号
     *
     */
    private String uridnpymeisaino;

    /**
     * Database Column Remarks: 売上伝票明細枝番号
     *
     */
    private String uridnpymeisaiedano;

    /**
     * Database Column Remarks: 登録者AP基本コード
     *
     */
    private String toshaapkihoncd;

    /**
     * Database Column Remarks: 登録者ショップコード
     *
     */
    private String toshashopcd;

    /**
     * Database Column Remarks: 登録者コード
     *
     */
    private String toshacd;

    /**
     * Database Column Remarks: 登録システム日付
     *
     */
    private String inptsystmymd;

    /**
     * Database Column Remarks: 登録システム時間
     *
     */
    private String inptsystmjikan;

    /**
     * Database Column Remarks: 更新者AP基本コード
     *
     */
    private String koshaapkihoncd;

    /**
     * Database Column Remarks: 更新者ショップコード
     *
     */
    private String koshashopcd;

    /**
     * Database Column Remarks: 更新者コード
     *
     */
    private String koshacd;

    /**
     * Database Column Remarks: 登録者名
     *
     */
    private String toshamei;

    /**
     * Database Column Remarks: 更新者名
     *
     */
    private String koshamei;

    /**
     * Database Column Remarks: セット区分
     *
     */
    private String stkbn;

    /**
     * Database Column Remarks: セット適用開始日
     *
     */
    private String set_tekiyostrtdd;

    /**
     * Database Column Remarks: セット適用終了日
     *
     */
    private String set_tekiyoshrydd;

    /**
     * Database Column Remarks: セット配達価格
     *
     */
    private String set_dlvryprice;

    /**
     * Database Column Remarks: セット持ち帰り価格
     *
     */
    private String set_mochikaeriprice;

    /**
     * Database Column Remarks: 料金種別特別単価フラグ
     *
     */
    private String rknkindtokubetsuuntflg;

    /**
     * Database Column Remarks: 特別単価リスト
     *
     */
    private String tokubetsuUntJson;

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
        sb.append(", apkihoncd=").append(apkihoncd);
        sb.append(", shopcd=").append(shopcd);
        sb.append(", acptdnpyno=").append(acptdnpyno);
        sb.append(", itakuaiteapkihoncd=").append(itakuaiteapkihoncd);
        sb.append(", itakuaiteshopcd=").append(itakuaiteshopcd);
        sb.append(", itakuaiteacptdnpyno=").append(itakuaiteacptdnpyno);
        sb.append(", itakuaiteedano=").append(itakuaiteedano);
        sb.append(", itakuaiteacptdnpymeisaino=").append(itakuaiteacptdnpymeisaino);
        sb.append(", itakuaiteacptdnpymeisaiedano=").append(itakuaiteacptdnpymeisaiedano);
        sb.append(", lctgrycd=").append(lctgrycd);
        sb.append(", mctgrycd=").append(mctgrycd);
        sb.append(", sctgrycd=").append(sctgrycd);
        sb.append(", shncd=").append(shncd);
        sb.append(", haifubutsuapkihoncd=").append(haifubutsuapkihoncd);
        sb.append(", haifubutsushopcd=").append(haifubutsushopcd);
        sb.append(", haifubutsucd=").append(haifubutsucd);
        sb.append(", katasiki=").append(katasiki);
        sb.append(", keijokbn=").append(keijokbn);
        sb.append(", acptsuryo=").append(acptsuryo);
        sb.append(", hyojununt=").append(hyojununt);
        sb.append(", acptunt=").append(acptunt);
        sb.append(", acptkin=").append(acptkin);
        sb.append(", acptuntshohitaxkin=").append(acptuntshohitaxkin);
        sb.append(", acptshohitaxkin=").append(acptshohitaxkin);
        sb.append(", itakucmpnyacptunt=").append(itakucmpnyacptunt);
        sb.append(", itakucmpnyacptuntshohitaxkin=").append(itakucmpnyacptuntshohitaxkin);
        sb.append(", itakucmpnyacptshohitaxkin=").append(itakucmpnyacptshohitaxkin);
        sb.append(", nohinsuryo=").append(nohinsuryo);
        sb.append(", nohinunt=").append(nohinunt);
        sb.append(", nohinkin=").append(nohinkin);
        sb.append(", nohinuntshohitaxkin=").append(nohinuntshohitaxkin);
        sb.append(", nohinshohitaxkin=").append(nohinshohitaxkin);
        sb.append(", zankaisu=").append(zankaisu);
        sb.append(", soukaisu=").append(soukaisu);
        sb.append(", taxkbn=").append(taxkbn);
        sb.append(", yusyokbn=").append(yusyokbn);
        sb.append(", meisaikbn=").append(meisaikbn);
        sb.append(", nyuryokumeisaiflg=").append(nyuryokumeisaiflg);
        sb.append(", tanpinmeisaiflg=").append(tanpinmeisaiflg);
        sb.append(", stshncd=").append(stshncd);
        sb.append(", meisaibiko=").append(meisaibiko);
        sb.append(", datsksmotokbn=").append(datsksmotokbn);
        sb.append(", yukokbn=").append(yukokbn);
        sb.append(", kshnsystmymd=").append(kshnsystmymd);
        sb.append(", kshnsystmjikan=").append(kshnsystmjikan);
        sb.append(", shnapkihoncd=").append(shnapkihoncd);
        sb.append(", shnnmryakusho=").append(shnnmryakusho);
        sb.append(", pricekbn=").append(pricekbn);
        sb.append(", hyojunpricekirikaedd=").append(hyojunpricekirikaedd);
        sb.append(", genzhyojunprice=").append(genzhyojunprice);
        sb.append(", shinhyojunprice=").append(shinhyojunprice);
        sb.append(", genzmochikaeriprice=").append(genzmochikaeriprice);
        sb.append(", shinmochikaeriprice=").append(shinmochikaeriprice);
        sb.append(", opnpricekirikaedd=").append(opnpricekirikaedd);
        sb.append(", genzopnprice=").append(genzopnprice);
        sb.append(", shinopnprice=").append(shinopnprice);
        sb.append(", genzopnmochikaeriprice=").append(genzopnmochikaeriprice);
        sb.append(", shinopnmochikaeriprice=").append(shinopnmochikaeriprice);
        sb.append(", hansokukbn=").append(hansokukbn);
        sb.append(", hansokustrtdd=").append(hansokustrtdd);
        sb.append(", hansokushrydd=").append(hansokushrydd);
        sb.append(", hansokudlvryprice=").append(hansokudlvryprice);
        sb.append(", hansokumochikaeriprice=").append(hansokumochikaeriprice);
        sb.append(", hansokuyusenkbn=").append(hansokuyusenkbn);
        sb.append(", zaikount=").append(zaikount);
        sb.append(", iro=").append(iro);
        sb.append(", sz=").append(sz);
        sb.append(", uritrtskitani=").append(uritrtskitani);
        sb.append(", uriirisu=").append(uriirisu);
        sb.append(", uritani=").append(uritani);
        sb.append(", taxsetteikbn=").append(taxsetteikbn);
        sb.append(", shntaxkbn=").append(shntaxkbn);
        sb.append(", zaikoaramusuryo=").append(zaikoaramusuryo);
        sb.append(", trtskikbn=").append(trtskikbn);
        sb.append(", tokubetsuuntkbn=").append(tokubetsuuntkbn);
        sb.append(", tekiyostrtdd=").append(tekiyostrtdd);
        sb.append(", tekiyoshrydd=").append(tekiyoshrydd);
        sb.append(", dlvryprice=").append(dlvryprice);
        sb.append(", mochikaeriprice=").append(mochikaeriprice);
        sb.append(", keijokbnnm=").append(keijokbnnm);
        sb.append(", haifubutsunm=").append(haifubutsunm);
        sb.append(", sokazu=").append(sokazu);
        sb.append(", shnnm=").append(shnnm);
        sb.append(", hyojunprice=").append(hyojunprice);
        sb.append(", uridnpyno=").append(uridnpyno);
        sb.append(", uridnpyedano=").append(uridnpyedano);
        sb.append(", uridnpymeisaino=").append(uridnpymeisaino);
        sb.append(", uridnpymeisaiedano=").append(uridnpymeisaiedano);
        sb.append(", toshaapkihoncd=").append(toshaapkihoncd);
        sb.append(", toshashopcd=").append(toshashopcd);
        sb.append(", toshacd=").append(toshacd);
        sb.append(", inptsystmymd=").append(inptsystmymd);
        sb.append(", inptsystmjikan=").append(inptsystmjikan);
        sb.append(", koshaapkihoncd=").append(koshaapkihoncd);
        sb.append(", koshashopcd=").append(koshashopcd);
        sb.append(", koshacd=").append(koshacd);
        sb.append(", kshnsystmymd=").append(kshnsystmymd);
        sb.append(", kshnsystmjikan=").append(kshnsystmjikan);
        sb.append(", toshamei=").append(toshamei);
        sb.append(", koshamei=").append(koshamei);
        sb.append(", stkbn=").append(stkbn);
        sb.append(", set_tekiyostrtdd=").append(set_tekiyostrtdd);
        sb.append(", set_tekiyoshrydd=").append(set_tekiyoshrydd);
        sb.append(", set_dlvryprice=").append(set_dlvryprice);
        sb.append(", set_mochikaeriprice=").append(set_mochikaeriprice);
        sb.append(", rknkindtokubetsuuntflg=").append(rknkindtokubetsuuntflg);
        sb.append("]");
        return sb.toString();
    }

}
