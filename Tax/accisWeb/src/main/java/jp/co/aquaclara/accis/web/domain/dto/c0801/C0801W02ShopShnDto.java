package jp.co.aquaclara.accis.web.domain.dto.c0801;

import java.io.Serializable;
import java.util.Objects;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M043TEntity;
import jp.co.aquaclara.accis.web.domain.dto.c0800.ComparableValueCurrentOrNew;
import jp.co.aquaclara.accis.web.domain.dto.c0800.HasConvertableNumericProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * C0801W02_受注登録（受注登録）_ショップ商品DTO
 */
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class C0801W02ShopShnDto implements Serializable, HasConvertableNumericProperty,
    ComparableValueCurrentOrNew {

    private static final long serialVersionUID = 1L;

    /** ＡＰ基本コード */
    @EqualsAndHashCode.Include
    private String apkihoncd;
    /** ショップコード */
    @EqualsAndHashCode.Include
    private String shopcd;
    /** 商品ＡＰ基本コード */
    @EqualsAndHashCode.Include
    private String shnapkihoncd;
    /** 商品コード */
    @EqualsAndHashCode.Include
    private String shncd;
    /** 販売開始日 */
    private String hanbaistrtdd;
    /** 販売終了日 */
    private String hanbaishrydd;
    /** 外部連携商品コード */
    private String gaiburenkeishncd;
    /** 本部販売管理商品コード */
    private String honbuhanbaikanrishncd;
    /** マージン対象区分 */
    private String mgntrgtkbn;
    /** 商品区分 */
    private String shnkbn;
    /** モバイル利用有無 */
    private String mobileriyoumu;
    /** 税設定区分 */
    private String taxsetteikbn;
    /** 税区分 */
    private String taxkbn;
    /** 仕入取扱単位 */
    private String siiretrtskitani;
    /** 仕入入数 */
    private String siireirisu;
    /** 仕入単位 */
    private String siiretani;
    /** 仕入単価 */
    private String siireunt;
    /** 発注ロット数 */
    private String hacchurottokazu;
    /** 売上取扱単位 */
    private String uritrtskitani;
    /** 売上入数 */
    private String uriirisu;
    /** 売上単位 */
    private String uritani;
    /** オープン価格切替日 */
    private String opnpricekirikaedd;
    /** 現オープン価格 */
    private String genzopnprice;
    /** 新オープン価格 */
    private String shinopnprice;
    /** 現オープン持ち帰り価格 */
    private String genzopnmochikaeriprice;
    /** 新オープン持ち帰り価格 */
    private String shinopnmochikaeriprice;
    /** 販促区分 */
    private String hansokukbn;
    /** 販促開始日 */
    private String hansokustrtdd;
    /** 販促終了日 */
    private String hansokushrydd;
    /** 販促名称 */
    private String hansokunm;
    /** 販促配達価格 */
    private String hansokudlvryprice;
    /** 販促持ち帰り価格 */
    private String hansokumochikaeriprice;
    /** 販促優先区分 */
    private String hansokuyusenkbn;
    /** 在庫管理対象フラグ */
    private String zaikokanritrgtflg;
    /** 原価計算区分 */
    private String genkakeisankbn;
    /** 標準原価 */
    private String hyojungenka;
    /** 在庫単価 */
    private String zaikount;
    /** 仕入単位変換区分 */
    private String siiretanihenkankbn;
    /** 在庫アラーム数量 */
    private String zaikoaramusuryo;
    /** 備考 */
    private String biko;
    /** 会員サイト連携区分 */
    private String kaiinsaitorenkeikbn;
    /** 有効区分 */
    private String yukokbn;
    /** 登録者ＡＰ基本コード */
    private String toshaapkihoncd;
    /** 登録者ショップコード */
    private String toshashopcd;
    /** 登録者コード */
    private String toshacd;
    /** 登録システム日付 */
    private String inptsystmymd;
    /** 登録システム時間 */
    private String inptsystmjikan;
    /** 登録運用日付 */
    private String inptunyoymd;
    /** 登録ＩＰアドレス */
    private String inptipadrse;
    /** 登録プログラムＩＤ */
    private String inptpgmid;
    /** 更新者ＡＰ基本コード */
    private String koshaapkihoncd;
    /** 更新者ショップコード */
    private String koshashopcd;
    /** 更新者コード */
    private String koshacd;
    /** 更新システム日付 */
    private String kshnsystmymd;
    /** 更新システム時間 */
    private String kshnsystmjikan;
    /** 更新運用日付 */
    private String kshnunyoymd;
    /** 更新ＩＰアドレス */
    private String kshnipadrse;
    /** 更新プログラムＩＤ */
    private String kshnpgmid;

    public static C0801W02ShopShnDto create(S02M043TEntity entity) {
        return C0801W02ShopShnDto.builder()
            // ＡＰ基本コード
            .apkihoncd(entity.getApkihoncd())
            // ショップコード
            .shopcd(entity.getShopcd())
            // 商品ＡＰ基本コード
            .shnapkihoncd(entity.getShnapkihoncd())
            // 商品コード
            .shncd(entity.getShncd())
            // 販売開始日
            .hanbaistrtdd(entity.getHanbaistrtdd())
            // 販売終了日
            .hanbaishrydd(entity.getHanbaishrydd())
            // 外部連携商品コード
            .gaiburenkeishncd(entity.getGaiburenkeishncd())
            // 本部販売管理商品コード
            .honbuhanbaikanrishncd(entity.getHonbuhanbaikanrishncd())
            // マージン対象区分
            .mgntrgtkbn(entity.getMgntrgtkbn())
            // 商品区分
            .shnkbn(entity.getShnkbn())
            // モバイル利用有無
            .mobileriyoumu(entity.getMobileriyoumu())
            // 税設定区分
            .taxsetteikbn(entity.getTaxsetteikbn())
            // 税区分
            .taxkbn(entity.getTaxkbn())
            // 仕入取扱単位
            .siiretrtskitani(entity.getSiiretrtskitani())
            // 仕入入数
            .siireirisu(Objects.toString(entity.getSiireirisu(), null))
            // 仕入単位
            .siiretani(entity.getSiiretani())
            // 仕入単価
            .siireunt(Objects.toString(entity.getSiireunt(), null))
            // 発注ロット数
            .hacchurottokazu(Objects.toString(entity.getHacchurottokazu(), null))
            // 売上取扱単位
            .uritrtskitani(entity.getUritrtskitani())
            // 売上入数
            .uriirisu(Objects.toString(entity.getUriirisu(), null))
            // 売上単位
            .uritani(entity.getUritani())
            // オープン価格切替日
            .opnpricekirikaedd(entity.getOpnpricekirikaedd())
            // 現オープン価格
            .genzopnprice(Objects.toString(entity.getGenzopnprice(), null))
            // 新オープン価格
            .shinopnprice(Objects.toString(entity.getShinopnprice(), null))
            // 現オープン持ち帰り価格
            .genzopnmochikaeriprice(Objects.toString(entity.getGenzopnmochikaeriprice(), null))
            // 新オープン持ち帰り価格
            .shinopnmochikaeriprice(Objects.toString(entity.getShinopnmochikaeriprice(), null))
            // 販促区分
            .hansokukbn(entity.getHansokukbn())
            // 販促開始日
            .hansokustrtdd(entity.getHansokustrtdd())
            // 販促終了日
            .hansokushrydd(entity.getHansokushrydd())
            // 販促名称
            .hansokunm(entity.getHansokunm())
            // 販促配達価格
            .hansokudlvryprice(Objects.toString(entity.getHansokudlvryprice(), null))
            // 販促持ち帰り価格
            .hansokumochikaeriprice(Objects.toString(entity.getHansokumochikaeriprice(), null))
            // 販促優先区分
            .hansokuyusenkbn(entity.getHansokuyusenkbn())
            // 在庫管理対象フラグ
            .zaikokanritrgtflg(entity.getZaikokanritrgtflg())
            // 原価計算区分
            .genkakeisankbn(entity.getGenkakeisankbn())
            // 標準原価
            .hyojungenka(Objects.toString(entity.getHyojungenka(), null))
            // 在庫単価
            .zaikount(Objects.toString(entity.getZaikount(), null))
            // 仕入単位変換区分
            .siiretanihenkankbn(entity.getSiiretanihenkankbn())
            // 在庫アラーム数量
            .zaikoaramusuryo(Objects.toString(entity.getZaikoaramusuryo(), null))
            // 備考
            .biko(entity.getBiko())
            // 会員サイト連携区分
            .kaiinsaitorenkeikbn(entity.getKaiinsaitorenkeikbn())
            // 有効区分
            .yukokbn(entity.getYukokbn())
            // 登録者ＡＰ基本コード
            .toshaapkihoncd(entity.getToshaapkihoncd())
            // 登録者ショップコード
            .toshashopcd(entity.getToshashopcd())
            // 登録者コード
            .toshacd(entity.getToshacd())
            // 登録システム日付
            .inptsystmymd(entity.getInptsystmymd())
            // 登録システム時間
            .inptsystmjikan(entity.getInptsystmjikan())
            // 登録運用日付
            .inptunyoymd(entity.getInptunyoymd())
            // 登録ＩＰアドレス
            .inptipadrse(entity.getInptipadrse())
            // 登録プログラムＩＤ
            .inptpgmid(entity.getInptpgmid())
            // 更新者ＡＰ基本コード
            .koshaapkihoncd(entity.getKoshaapkihoncd())
            // 更新者ショップコード
            .koshashopcd(entity.getKoshashopcd())
            // 更新者コード
            .koshacd(entity.getKoshacd())
            // 更新システム日付
            .kshnsystmymd(entity.getKshnsystmymd())
            // 更新システム時間
            .kshnsystmjikan(entity.getKshnsystmjikan())
            // 更新運用日付
            .kshnunyoymd(entity.getKshnunyoymd())
            // 更新ＩＰアドレス
            .kshnipadrse(entity.getKshnipadrse())
            // 更新プログラムＩＤ
            .kshnpgmid(entity.getKshnpgmid()).build();
    }
}
