package jp.co.aquaclara.accis.common.domain.repository.entity.bp;

import java.io.Serializable;
import java.util.List;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntity;
import lombok.Data;

/**
 * Database Table Remarks: 受注配達明細
 *
 * This class corresponds to the database table S04T002T
 */
@Data
public class S04T002TC0800C01Entity extends S04T002TEntity implements Serializable {
    // 価格（単価）取得基準日
    private String kakakugetbasedd;
    // 商品ＡＰ基本コード
    private String shnapkihoncd;
    // 商品名称略称
    private String shnnmryakusho;
    // 価格区分
    private String pricekbn;
    // 標準価格切替日
    private String hyojunpricekirikaedd;
    // 現標準価格
    private String genzhyojunprice;
    // 新標準価格
    private String shinhyojunprice;
    // 現持ち帰り価格
    private String genzmochikaeriprice;
    // 新持ち帰り価格
    private String shinmochikaeriprice;
    // オープン価格切替日
    private String opnpricekirikaedd;
    // 現オープン価格
    private String genzopnprice;
    // 新オープン価格
    private String shinopnprice;
    // 現オープン持ち帰り価格
    private String genzopnmochikaeriprice;
    // 新オープン持ち帰り価格
    private String shinopnmochikaeriprice;
    // 販促区分
    private String hansokukbn;
    // 販促開始日
    private String hansokustrtdd;
    // 販促終了日
    private String hansokushrydd;
    // 販促配達価格
    private String hansokudlvryprice;
    // 販促持ち帰り価格
    private String hansokumochikaeriprice;
    // 販促優先区分
    private String hansokuyusenkbn;
    // 在庫単価
    private String zaikount;
    // 色
    private String iro;
    // サイズ名称
    private String sz;
    // 売上取扱単位名称
    private String uritrtskitani;
    // 売上入数
    private String uriirisu;
    // 売上単位名称
    private String uritani;
    // 税設定区分
    private String taxsetteikbn;
    // 商品税区分
    private String shntaxkbn;
    // 在庫アラーム数量
    private String zaikoaramusuryo;
    // 取扱区分
    private String trtskikbn;
    // 特別単価区分
    private String tokubetsuuntkbn;
    // 適用開始日
    private String tekiyostrtdd;
    // 適用終了日
    private String tekiyoshrydd;
    // 配達価格
    private String dlvryprice;
    // 持ち帰り価格
    private String mochikaeriprice;
    // 計上区分名
    private String keijokbnnm;
    // 数
    private String sokazu;
    // 商品名称
    private String shnnm;
    // 標準価格
    private String hyojunprice;
    // 登録者ＡＰ基本コード
    private String toshaapkihoncd;
    // 登録者ショップコード
    private String toshashopcd;
    // 登録者コード
    private String toshacd;
    // 登録システム日付
    private String inptsystmymd;
    // 登録システム時間
    private String inptsystmjikan;
    // 更新者ＡＰ基本コード
    private String koshaapkihoncd;
    // 更新者ショップコー
    private String koshashopcd;
    // 更新者コード
    private String koshacd;
    // 更新システム日付
    private String kshnsystmymd;
    // 更新システム時間
    private String kshnsystmjikan;
    // 登録担当者名
    private String toshamei;
    // 更新担当者名
    private String koshamei;
    // セット区分
    private String stkbn;
    // 適用開始日
    private String set_tekiyostrtdd;
    // 適用終了日
    private String set_tekiyoshrydd;
    // 配達価格
    private String set_dlvryprice;
    // 持ち帰り価格
    private String set_mochikaeriprice;
    // 料金種別特別単価フラグ
    private String rknkindtokubetsuuntflg;

    // 受注関係DTO
    // 製品水コード
    private String watercd;
    // サーバー関連コード
    private String serverlcd;
    // サーバー本体コード
    private String servermcd;
    // レギュラーコードリスト
    private List<String> regularcdList;
    // スリムコードリスト
    private List<String> slimcdList;
    // 得意先区分
    private String tokuisakikbn;
    // 受注区分
    private String acptkbn;
    // 受注区分詳細
    private String acptkbnshs;
    // 大分類コード
    private String lctgrycd;
    // 中分類コード
    private String mctgrycd;
    // 商品コード
    private String shncd;
    // 計上区分
    private String keijokbn;

    // 得意先別セット商品単価リスト
    private List<S02M009TC0800W01Entity> s02m009tList;
    // 商品マスタビュー
    private S02V001TEntity s02v001tEntity;

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
        sb.append(", kakakugetbasedd=").append(kakakugetbasedd);
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
        sb.append(", sokazu=").append(sokazu);
        sb.append(", shnnm=").append(shnnm);
        sb.append(", hyojunprice=").append(hyojunprice);
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
        sb.append(", watercd=").append(watercd);
        sb.append(", serverlcd=").append(serverlcd);
        sb.append(", servermcd=").append(servermcd);
        sb.append(", regularcdList=").append(regularcdList);
        sb.append(", slimcdList=").append(slimcdList);
        sb.append(", tokuisakikbn=").append(tokuisakikbn);
        sb.append(", acptkbn=").append(acptkbn);
        sb.append(", acptkbnshs=").append(acptkbnshs);
        sb.append(", lctgrycd=").append(lctgrycd);
        sb.append(", mctgrycd=").append(mctgrycd);
        sb.append(", shncd=").append(shncd);
        sb.append(", keijokbn=").append(keijokbn);
        sb.append(", s02m009tList=").append(s02m009tList);
        sb.append(", s02v001tEntity=").append(s02v001tEntity);
        sb.append("]");
        return sb.toString();
    }

}
