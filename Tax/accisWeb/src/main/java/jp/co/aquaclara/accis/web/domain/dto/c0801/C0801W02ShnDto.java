package jp.co.aquaclara.accis.web.domain.dto.c0801;

import java.io.Serializable;
import java.util.Objects;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntity;
import jp.co.aquaclara.accis.web.domain.dto.c0800.ComparableValueCurrentOrNew;
import jp.co.aquaclara.accis.web.domain.dto.c0800.HasConvertableNumericProperty;
import jp.co.aquaclara.accis.web.domain.dto.c0801.shnprice.C0801W02RangeShnPriceDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.shnprice.C0801W02SwitchableShnPriceDto;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 受注登録（受注登録）_商品DTO
 */
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class C0801W02ShnDto
    implements Serializable, HasConvertableNumericProperty, ComparableValueCurrentOrNew {

    private static final long serialVersionUID = 1L;

    /** ＡＰ基本コード */
    @EqualsAndHashCode.Include
    private String apkihoncd;
    /** ショップコード */
    @EqualsAndHashCode.Include
    private String shopcd;
    /** 商品コード */
    @EqualsAndHashCode.Include
    private String shncd;
    /** 商品属性 */
    private String shnzokusei;
    /** セット区分 */
    private String stkbn;
    /** 大分類コード */
    private String lctgrycd;
    /** 中分類コード */
    private String mctgrycd;
    /** 小分類コード */
    private String sctgrycd;
    /** 大分類名称 */
    private String lctgrynm;
    /** 中分類名称 */
    private String mctgrynm;
    /** 小分類名称 */
    private String sctgrynm;
    /** 商品名称切替日 */
    private String shnnmkirikaedd;
    /** 現商品名称 */
    private String genzshnnm;
    /** 新商品名称 */
    private String shinshnnm;
    /** 現商品名称カナ */
    private String genzshnnmkana;
    /** 新商品名称カナ */
    private String shinshnnmkana;
    /** 現商品名称略称 */
    private String genzshnnmryakusho;
    /** 新商品名称略称 */
    private String shinshnnmryakusho;
    /** ショップ価格切替日 */
    private String shoppricekirikaedd;
    /** 現ショップ価格 */
    private String genzshopprice;
    /** 新ショップ価格 */
    private String shinshopprice;
    /** 現ショップ持ち帰り価格 */
    private String genzshopmochikaeriprice;
    /** 新ショップ持ち帰り価格 */
    private String shinshopmochikaeriprice;
    /** 色 */
    private String iro;
    /** サイズ */
    private String sz;
    /** サイズ名称 */
    private String sznm;
    /** 取扱区分 */
    private String trtskikbn;
    /** 数量端数管理区分 */
    private String suryohasukanrikbn;
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
    /** 税設定区分 */
    private String taxsetteikbn;
    /** 税区分 */
    private String taxkbn;
    /** 仕入取扱単位 */
    private String siiretrtskitani;
    /** 仕入取扱単位名称 */
    private String siiretrtskitaninm;
    /** 仕入入数 */
    private String siireirisu;
    /** 仕入単位 */
    private String siiretani;
    /** 仕入単位名称 */
    private String siiretaninm;
    /** 仕入単価 */
    private String siireunt;
    /** 発注ロット数 */
    private String hacchurottokazu;
    /** 売上取扱単位 */
    private String uritrtskitani;
    /** 売上取扱単位名称 */
    private String uritrtskitaninm;
    /** 売上入数 */
    private String uriirisu;
    /** 売上単位 */
    private String uritani;
    /** 売上単位名称 */
    private String uritaninm;
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
    /** 商品ＡＰ基本コード */
    private String shnapkihoncd;
    /** 標準価格切替日 */
    private String hyojunpricekirikaedd;
    /** 現標準価格 */
    private String genzhyojunprice;
    /** 新標準価格 */
    private String shinhyojunprice;
    /** 現持ち帰り価格 */
    private String genzmochikaeriprice;
    /** 新持ち帰り価格 */
    private String shinmochikaeriprice;
    /** 現価格区分 */
    private String genzpricekbn;
    /** 新価格区分 */
    private String shinpricekbn;
    /** 基本商品備考 */
    private String kihonshnbiko;
    /** 基本商品有効区分 */
    private String kihonshnyukokbn;
    /** 基本商品登録者ＡＰ基本コード */
    private String kihonshntoshaapkihoncd;
    /** 基本商品登録者ショップコード */
    private String kihonshntoshashopcd;
    /** 基本商品登録者コード */
    private String kihonshntoshacd;
    /** 基本商品登録システム日付 */
    private String kihonshninptsystmymd;
    /** 基本商品登録システム時間 */
    private String kihonshninptsystmjikan;
    /** 基本商品更新者ＡＰ基本コード */
    private String kihonshnkoshaapkihoncd;
    /** 基本商品更新者ショップコード */
    private String kihonshnkoshashopcd;
    /** 基本商品更新者コード */
    private String kihonshnkoshacd;
    /** 基本商品更新システム日付 */
    private String kihonshnkshnsystmymd;
    /** 基本商品更新システム時間 */
    private String kihonshnkshnsystmjikan;
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
    /** ショップ備考 */
    private String shopbiko;
    /** ショップ有効区分 */
    private String shopyukokbn;
    /** ショップ登録者ＡＰ基本コード */
    private String shoptoshaapkihoncd;
    /** ショップ登録者ショップコード */
    private String shoptoshashopcd;
    /** ショップ登録者コード */
    private String shoptoshacd;
    /** ショップ登録システム日付 */
    private String shopinptsystmymd;
    /** ショップ登録システム時間 */
    private String shopinptsystmjikan;
    /** ショップ更新者ＡＰ基本コード */
    private String shopkoshaapkihoncd;
    /** ショップ更新者ショップコード */
    private String shopkoshashopcd;
    /** ショップ更新者コード */
    private String shopkoshacd;
    /** ショップ更新システム日付 */
    private String shopkshnsystmymd;
    /** ショップ更新システム時間 */
    private String shopkshnsystmjikan;
    /** 会員サイト連携区分 */
    private String kaiinsaitorenkeikbn;
    /** モバイル利用有無 */
    private String mobileriyoumu;
    /** 連番（セット商品の構成商品の場合のみ設定、それ以外は0） */
    private short seq;
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

    /**
     * 標準価格DTOを生成する
     * 
     * @param baseDate 価格切替基準日
     * @return 標準価格DTO
     */
    public C0801W02SwitchableShnPriceDto getHyojunPriceDto(String baseDate) {
        return C0801W02SwitchableShnPriceDto.builder()
            // 商品コード
            .shncd(shncd)
            // 対象日
            .targetDate(baseDate)
            // 価格切替日
            .kakakukirikaedd(hyojunpricekirikaedd)
            // 現価格
            .genzprice(genzhyojunprice)
            // 新価格
            .shinprice(shinhyojunprice)
            // 現持ち帰り価格
            .genzmochikaeriprice(genzmochikaeriprice)
            // 新持ち帰り価格
            .shinmochikaeriprice(shinmochikaeriprice).build();
    }

    /**
     * オープン価格DTOを生成する
     * 
     * @param baseDate 価格切替基準日
     * @return オープン価格DTO
     */
    public C0801W02SwitchableShnPriceDto getOpenPriceDto(String baseDate) {
        return C0801W02SwitchableShnPriceDto.builder()
            // 商品コード
            .shncd(shncd)
            // 対象日
            .targetDate(baseDate)
            // 価格切替日
            .kakakukirikaedd(opnpricekirikaedd)
            // 現価格
            .genzprice(genzopnprice)
            // 新価格
            .shinprice(shinopnprice)
            // 現持ち帰り価格
            .genzmochikaeriprice(genzopnmochikaeriprice)
            // 新持ち帰り価格
            .shinmochikaeriprice(shinopnmochikaeriprice).build();
    }

    /**
     * 販促価格DTOを生成する
     * 
     * @param baseDate 基準日
     * @return 販促価格DTO
     */
    public C0801W02RangeShnPriceDto getCampaignPriceDto(String baseDate) {
        return C0801W02RangeShnPriceDto.builder()
            // 商品コード
            .shncd(shncd)
            // 対象日
            .targetDate(baseDate)
            // 適用開始日
            .strtdd(hansokustrtdd)
            // 適用終了日
            .shrydd(hansokushrydd)
            // 配達価格
            .dlvryprice(hansokudlvryprice)
            // 持ち帰り価格
            .mochikaeriprice(hansokumochikaeriprice).build();
    }

    /**
     * 商品マスタビューEntityの内容を元に商品DTOのインスタンスを生成する
     * 
     * @param entity 商品マスタビューEntity
     * @return 商品DTO
     */
    public static C0801W02ShnDto create(S02V001TEntity entity) {
        return C0801W02ShnDto.builder()
            // ＡＰ基本コード
            .apkihoncd(entity.getApkihoncd())
            // ショップコード
            .shopcd(entity.getShopcd())
            // 商品コード
            .shncd(entity.getShncd())
            // 商品属性
            .shnzokusei(entity.getShnzokusei())
            // セット区分
            .stkbn(entity.getStkbn())
            // 大分類コード
            .lctgrycd(entity.getLctgrycd())
            // 中分類コード
            .mctgrycd(entity.getMctgrycd())
            // 小分類コード
            .sctgrycd(entity.getSctgrycd())
            // 大分類名称
            .lctgrynm(entity.getLctgrynm())
            // 中分類名称
            .mctgrynm(entity.getMctgrynm())
            // 小分類名称
            .sctgrynm(entity.getSctgrynm())
            // 商品名称切替日
            .shnnmkirikaedd(entity.getShnnmkirikaedd())
            // 現商品名称
            .genzshnnm(entity.getGenzshnnm())
            // 新商品名称
            .shinshnnm(entity.getShinshnnm())
            // 現商品名称カナ
            .genzshnnmkana(entity.getGenzshnnmkana())
            // 新商品名称カナ
            .shinshnnmkana(entity.getShinshnnmkana())
            // 現商品名称略称
            .genzshnnmryakusho(entity.getGenzshnnmryakusho())
            // 新商品名称略称
            .shinshnnmryakusho(entity.getShinshnnmryakusho())
            // ショップ価格切替日
            .shoppricekirikaedd(entity.getShoppricekirikaedd())
            // 現ショップ価格
            .genzshopprice(Objects.toString(entity.getGenzshopprice(), null))
            // 新ショップ価格
            .shinshopprice(Objects.toString(entity.getShinshopprice(), null))
            // 現ショップ持ち帰り価格
            .genzshopmochikaeriprice(Objects.toString(entity.getGenzshopmochikaeriprice(), null))
            // 新ショップ持ち帰り価格
            .shinshopmochikaeriprice(Objects.toString(entity.getShinshopmochikaeriprice(), null))
            // 色
            .iro(entity.getIro())
            // サイズ
            .sz(entity.getSz())
            // サイズ名称
            .sznm(entity.getSznm())
            // 取扱区分
            .trtskikbn(entity.getTrtskikbn())
            // 数量端数管理区分
            .suryohasukanrikbn(entity.getSuryohasukanrikbn())
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
            // 税設定区分
            .taxsetteikbn(entity.getTaxsetteikbn())
            // 税区分
            .taxkbn(entity.getTaxkbn())
            // 仕入取扱単位
            .siiretrtskitani(entity.getSiiretrtskitani())
            // 仕入取扱単位名称
            .siiretrtskitaninm(entity.getSiiretrtskitaninm())
            // 仕入入数
            .siireirisu(Objects.toString(entity.getSiireirisu(), null))
            // 仕入単位
            .siiretani(entity.getSiiretani())
            // 仕入単位名称
            .siiretaninm(entity.getSiiretaninm())
            // 仕入単価
            .siireunt(Objects.toString(entity.getSiireunt(), null))
            // 発注ロット数
            .hacchurottokazu(Objects.toString(entity.getHacchurottokazu(), null))
            // 売上取扱単位
            .uritrtskitani(entity.getUritrtskitani())
            // 売上取扱単位名称
            .uritrtskitaninm(entity.getUritrtskitaninm())
            // 売上入数
            .uriirisu(Objects.toString(entity.getUriirisu(), null))
            // 売上単位
            .uritani(entity.getUritani())
            // 売上単位名称
            .uritaninm(entity.getUritaninm())
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
            // 商品ＡＰ基本コード
            .shnapkihoncd(entity.getShnapkihoncd())
            // 標準価格切替日
            .hyojunpricekirikaedd(entity.getHyojunpricekirikaedd())
            // 現標準価格
            .genzhyojunprice(Objects.toString(entity.getGenzhyojunprice(), null))
            // 新標準価格
            .shinhyojunprice(Objects.toString(entity.getShinhyojunprice(), null))
            // 現持ち帰り価格
            .genzmochikaeriprice(Objects.toString(entity.getGenzmochikaeriprice(), null))
            // 新持ち帰り価格
            .shinmochikaeriprice(Objects.toString(entity.getShinmochikaeriprice(), null))
            // 現価格区分
            .genzpricekbn(entity.getGenzpricekbn())
            // 新価格区分
            .shinpricekbn(entity.getShinpricekbn())
            // 基本商品備考
            .kihonshnbiko(entity.getKihonshnbiko())
            // 基本商品有効区分
            .kihonshnyukokbn(entity.getKihonshnyukokbn())
            // 基本商品登録者ＡＰ基本コード
            .kihonshntoshaapkihoncd(entity.getKihonshntoshaapkihoncd())
            // 基本商品登録者ショップコード
            .kihonshntoshashopcd(entity.getKihonshntoshashopcd())
            // 基本商品登録者コード
            .kihonshntoshacd(entity.getKihonshntoshacd())
            // 基本商品登録システム日付
            .kihonshninptsystmymd(entity.getKihonshninptsystmymd())
            // 基本商品登録システム時間
            .kihonshninptsystmjikan(entity.getKihonshninptsystmjikan())
            // 基本商品更新者ＡＰ基本コード
            .kihonshnkoshaapkihoncd(entity.getKihonshnkoshaapkihoncd())
            // 基本商品更新者ショップコード
            .kihonshnkoshashopcd(entity.getKihonshnkoshashopcd())
            // 基本商品更新者コード
            .kihonshnkoshacd(entity.getKihonshnkoshacd())
            // 基本商品更新システム日付
            .kihonshnkshnsystmymd(entity.getKihonshnkshnsystmymd())
            // 基本商品更新システム時間
            .kihonshnkshnsystmjikan(entity.getKihonshnkshnsystmjikan())
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
            // ショップ備考
            .shopbiko(entity.getShopbiko())
            // ショップ有効区分
            .shopyukokbn(entity.getShopyukokbn())
            // ショップ登録者ＡＰ基本コード
            .shoptoshaapkihoncd(entity.getShoptoshaapkihoncd())
            // ショップ登録者ショップコード
            .shoptoshashopcd(entity.getShoptoshashopcd())
            // ショップ登録者コード
            .shoptoshacd(entity.getShoptoshacd())
            // ショップ登録システム日付
            .shopinptsystmymd(entity.getShopinptsystmymd())
            // ショップ登録システム時間
            .shopinptsystmjikan(entity.getShopinptsystmjikan())
            // ショップ更新者ＡＰ基本コード
            .shopkoshaapkihoncd(entity.getShopkoshaapkihoncd())
            // ショップ更新者ショップコード
            .shopkoshashopcd(entity.getShopkoshashopcd())
            // ショップ更新者コード
            .shopkoshacd(entity.getShopkoshacd())
            // ショップ更新システム日付
            .shopkshnsystmymd(entity.getShopkshnsystmymd())
            // ショップ更新システム時間
            .shopkshnsystmjikan(entity.getShopkshnsystmjikan())
            // 会員サイト連携区分
            .kaiinsaitorenkeikbn(entity.getKaiinsaitorenkeikbn())
            // モバイル利用有無
            .mobileriyoumu(entity.getMobileriyoumu())
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
            .kshnpgmid(entity.getKshnpgmid())
            // DTOをビルド
            .build();
    }
}
