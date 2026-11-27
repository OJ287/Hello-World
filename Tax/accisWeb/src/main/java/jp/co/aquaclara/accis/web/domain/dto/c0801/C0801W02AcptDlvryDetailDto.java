package jp.co.aquaclara.accis.web.domain.dto.c0801;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M044TC0802W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M048TInstalledServerDetailEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02V001TC0802W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S04T002TC0805W01Entity;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.web.domain.dto.c0800.ComparableValueCurrentOrNew;
import jp.co.aquaclara.accis.web.domain.dto.c0800.HasConvertableNumericProperty;
import jp.co.aquaclara.accis.web.domain.dto.c0800.HasKeijokbn;
import jp.co.aquaclara.accis.web.domain.dto.c0801.shnprice.C0801W02RangeShnPriceDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.shnprice.C0801W02SwitchableShnPriceDto;
import jp.co.aquaclara.accis.web.domain.service.x9901.StringUtil;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

/**
 * 受注登録（受注登録）_受注配達明細DTO.
 */
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class C0801W02AcptDlvryDetailDto implements Serializable, ComparableValueCurrentOrNew,
    HasConvertableNumericProperty, HasKeijokbn {

    private static final long serialVersionUID = 1L;

    /** 明細の種別 */
    @Builder.Default
    private DETAIL_TYPE type = DETAIL_TYPE.LOADED;

    /** 過去に手入力で受注単価を変更したことがあるか */
    private boolean untChangedBefore;

    /** ＡＰ基本コード */
    @EqualsAndHashCode.Include
    private String apkihoncd;

    /** ショップコード */
    @EqualsAndHashCode.Include
    private String shopcd;

    /** 受注伝票番号 */
    @EqualsAndHashCode.Include
    private String acptdnpyno;

    /** 枝番号 */
    @EqualsAndHashCode.Include
    private String edano;

    /** 受注伝票明細番号 */
    @EqualsAndHashCode.Include
    private String acptdnpymeisaino;

    /** 受注伝票明細枝番号 */
    @EqualsAndHashCode.Include
    private String acptdnpymeisaiedano;

    /** 委託相手ＡＰ基本コード */
    private String itakuaiteapkihoncd;

    /** 委託相手ショップコード */
    private String itakuaiteshopcd;

    /** 委託相手受注伝票番号 */
    private String itakuaiteacptdnpyno;

    /** 委託相手枝番号 */
    private String itakuaiteedano;

    /** 委託相手受注伝票明細番号 */
    private String itakuaiteacptdnpymeisaino;

    /** 委託相手受注伝票明細枝番号 */
    private String itakuaiteacptdnpymeisaiedano;

    /** 大分類コード */
    private String lctgrycd;

    /** 中分類コード */
    private String mctgrycd;

    /** 小分類コード */
    private String sctgrycd;

    /**
     * 明細商品の分類コード（大中小を連結したもの）を取得する
     * 
     * @return 分類コード
     */
    public String getCategoryCode() {
        return String.format("%s%s%s", lctgrycd, mctgrycd, sctgrycd);
    }

    /** 商品コード */
    private String shncd;

    /** 配布物ＡＰ基本コード */
    private String haifubutsuapkihoncd;

    /** 配布物ショップコード */
    private String haifubutsushopcd;

    /** 配布物コード */
    private String haifubutsucd;

    /** 型式 */
    private String katasiki;

    /** 計上区分 */
    private String keijokbn;

    /** 受注数量 */
    private String acptsuryo;

    /** 標準単価 */
    private String hyojununt;

    /** 受注単価 */
    private String acptunt;

    /** 受注金額 */
    private String acptkin;

    /** 初期表示価格 */
    private String shokidsplyprice;

    /** 受注単価消費税金額 */
    private String acptuntshohitaxkin;

    /** 受注消費税金額 */
    private String acptshohitaxkin;

    /** 委託会社受注単価 */
    private String itakucmpnyacptunt;

    /** 委託会社受注単価消費税金額 */
    private String itakucmpnyacptuntshohitaxkin;

    /** 委託会社受注消費税金額 */
    private String itakucmpnyacptshohitaxkin;

    /** 納品数量 */
    private String nohinsuryo;

    /** 納品単価 */
    private String nohinunt;

    /** 納品金額 */
    private String nohinkin;

    /** 納品単価消費税金額 */
    private String nohinuntshohitaxkin;

    /** 納品消費税金額 */
    private String nohinshohitaxkin;

    /** 残回数 */
    private String zankaisu;

    /** 総回数 */
    private String soukaisu;

    /** 税区分 */
    private String taxkbn;

    /**
     * 税区分を取得する.<br>
     * <br>
     * 税設定区分がOFFのときはnullを返す.
     * 
     * @return 税区分
     */
    public String getTaxkbn() {
        return CommonConstants.FLG_ON.equals(taxsetteikbn) ? taxkbn : null;
    }

    /** 有償区分 */
    private String yusyokbn;

    /** 明細区分 */
    private String meisaikbn;

    /** 入力明細フラグ */
    private String nyuryokumeisaiflg;

    /** 単品明細フラグ */
    private String tanpinmeisaiflg;

    /** セット商品コード */
    private String stshncd;

    /** 明細備考 */
    private String meisaibiko;

    /** データ作成元区分 */
    private String datsksmotokbn;

    /** 有効区分 */
    private String yukokbn;

    /** 商品ＡＰ基本コード */
    private String shnapkihoncd;

    /** 商品名称略称 */
    private String shnnmryakusho;

    /** 販促優先区分 */
    private String hansokuyusenkbn;

    /** 販促区分 */
    private String hansokukbn;

    /** 販促名称 */
    private String hansokunm;

    /** 料金種別特別単価フラグ */
    private String rknkindtokubetsuuntflg;

    /** 特別単価区分 */
    private String tokubetsuuntkbn;

    /** セット区分 */
    private String stkbn;

    /** 在庫単価 */
    private String zaikount;

    /** 色 */
    private String iro;

    /** サイズ名称 */
    private String sz;

    /** 売上取扱単位名称 */
    private String uritrtskitani;

    /** 売上入数 */
    private String uriirisu;

    /** 売上単位名称 */
    private String uritani;

    /** 税設定区分 */
    private String taxsetteikbn;

    /** 商品税区分 */
    private String shntaxkbn;

    /**
     * 商品税区分を取得する.<br>
     * <br>
     * 税設定区分がOFFのときはnullを返す.
     * 
     * @return 商品税区分
     */
    public String getShntaxkbn() {
        return CommonConstants.FLG_ON.equals(taxsetteikbn) ? shntaxkbn : null;
    }

    /** 在庫アラーム数量 */
    private String zaikoaramusuryo;

    /** 取扱区分 */
    private String trtskikbn;

    /** 計上区分名称 */
    private String keijokbnnm;

    /** 商品名称 */
    private String shnnm;

    /** 総数 */
    private String sokazu;

    /** 売上伝票番号 */
    private String uridnpyno;

    /** 売上伝票枝番号 */
    private String uridnpyedano;

    /** 売上伝票明細番号 */
    private String uridnpymeisaino;

    /** 売上伝票明細枝番号 */
    private String uridnpymeisaiedano;

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

    /** 登録担当者名 */
    private String toshamei;

    /** 更新担当者名 */
    private String koshamei;

    /**
     * 価格区分を取得する.
     * 
     * @return 価格区分
     */
    public String getPricekbn() {
        // 標準価格切替日を元に現新いずれかの価格区分を取得
        return getCurrentValue(genzpricekbn, shinpricekbn, hyojunShnPriceDto.getKakakukirikaedd(),
            hyojunShnPriceDto.getTargetDate());
    }

    /** 現価格区分 */
    private String genzpricekbn;

    /** 新価格区分 */
    private String shinpricekbn;

    /** 標準価格情報 */
    private C0801W02SwitchableShnPriceDto hyojunShnPriceDto;

    /** オープン価格情報 */
    private C0801W02SwitchableShnPriceDto openShnPriceDto;

    /**
     * ショップ商品価格情報を取得する.
     * 
     * @return 価格区分に応じ標準価格情報またはオープン価格情報
     */
    public C0801W02SwitchableShnPriceDto getShopShnPriceDto() {
        return CommonConstants.KBN_PRICE_OPEN.equals(getPricekbn()) ? openShnPriceDto
            : hyojunShnPriceDto;
    }

    /** 販促商品価格情報 */
    private C0801W02RangeShnPriceDto hansokuShnPriceDto;

    /**
     * 販促商品価格情報を取得する.
     * 
     * @param checkkbn 適用条件を考慮して取得するかどうか
     * @return 販促商品価格情報
     */
    public Optional<C0801W02RangeShnPriceDto> getHansokuShnPriceDto(boolean checkkbn) {
        boolean isApplied =
            CommonConstants.FLG_ON.equals(hansokukbn) && hansokuShnPriceDto.isApplied();
        return checkkbn ? isApplied ? Optional.of(hansokuShnPriceDto) : Optional.empty()
            : Optional.of(getHansokuShnPriceDto());
    }

    /** 得意先別特別商品価格情報 */
    private C0801W02RangeShnPriceDto tokubetsuShnPriceDto;

    /**
     * 得意先別特別商品価格情報を取得する.
     * 
     * @param checkkbn 適用条件を考慮して取得するかどうか
     * @return 得意先別特別商品価格情報
     */
    public Optional<C0801W02RangeShnPriceDto> getTokubetsuShnPriceDto(boolean checkkbn) {
        boolean isApplied = CommonConstants.FLG_ON.equals(rknkindtokubetsuuntflg)
            && tokubetsuShnPriceDto.isApplied();
        return checkkbn ? isApplied ? Optional.of(tokubetsuShnPriceDto) : Optional.empty()
            : Optional.of(getTokubetsuShnPriceDto());
    }

    /** 得意先別特別商品価格情報リスト */
    @Builder.Default
    private List<C0801W02RangeShnPriceDto> tokubetsuUntList = Collections.emptyList();

    /** 受注関係DTO */
    private C0801W02AcptRelationDto acptRelation;

    /** ショップ計算オプションDTO */
    private C0801W02ShopCalculateOptionDto calculateOptionDto;

    /** 商品DTO（更新時に設定） ※lombokの@Dataが生成するSetterではなく、別に定義しているSetterで設定すること */
    @Setter(value = AccessLevel.PRIVATE)
    private C0801W02ShnDto shnDto;

    /**
     * 商品DTOを設定する
     * 
     * @param shnDto 商品DTO
     * @param baseDate 価格切替基準日
     */
    public void setShnDto(C0801W02ShnDto shnDto, String baseDate) {
        this.shnDto = shnDto;
        // 商品分類コードと税区分を商品DTOから設定
        setLctgrycd(shnDto.getLctgrycd());
        setMctgrycd(shnDto.getMctgrycd());
        setSctgrycd(shnDto.getSctgrycd());
        setTaxkbn(shnDto.getTaxkbn());
        // 標準商品価格情報
        setHyojunShnPriceDto(shnDto.getHyojunPriceDto(baseDate));
        // オープン商品価格情報
        setOpenShnPriceDto(shnDto.getOpenPriceDto(baseDate));
    }

    /** 明細行番号（表示時） */
    private int rowIndex;

    /**
     * 商品税区分取得（計上区分の判断有り）
     * 
     * @return String 商品税区分
     */
    public String getShnTaxKbnByKeijoKbn() {
        return CommonConstants.KBN_KEIJO_KEIHI.equals(keijokbn) ? CommonConstants.KBN_TAX_HIKAZEI
            : shntaxkbn;
    }

    /**
     * 受注予約明細であるかを返す.
     * 
     * @return データ作成元区分が「受注予約（03）」であればtrue
     */
    public boolean isReserved() {
        return CommonConstants.KBN_SAKUSEIMOTO_JUCHUYOYAKU.equals(datsksmotokbn);
    }

    /**
     * 本数計算対象フラグ
     * 
     * @return boolean 結果
     */
    public boolean isCountable() {
        return CommonConstants.FLG_ON.equals(tanpinmeisaiflg)
            && CommonConstants.KBN_MEISAI_SHOHIN.equals(meisaikbn);
    }

    /**
     * 商品マスタビューの内容を元に受注配達明細DTOを生成する
     * 
     * @param e 商品マスタビューEntity
     * @param baseDate 基準日
     * @return 受注配達明細DTO
     */
    public static C0801W02AcptDlvryDetailDto create(S02V001TC0802W01Entity e, String baseDate) {
        return C0801W02AcptDlvryDetailDto.builder()
            // タイプ
            .type(DETAIL_TYPE.SHN)
            // 大分類コード
            .lctgrycd(e.getLctgrycd())
            // 中分類コード
            .mctgrycd(e.getMctgrycd())
            // 小分類コード
            .sctgrycd(e.getSctgrycd())
            // 商品コード
            .shncd(e.getShncd())
            // 税区分
            .taxkbn(CommonConstants.FLG_ON.equals(e.getTaxsetteikbn()) ? e.getTaxkbn() : null)
            // 明細区分（「商品」固定）
            .meisaikbn(CommonConstants.KBN_MEISAI_SHOHIN)
            // 入力明細フラグ（「ON」固定）
            .nyuryokumeisaiflg(CommonConstants.FLG_ON)
            // 単品明細フラグ（セット区分が「ON」なら「OFF」、「OFF」なら「ON」を設定）
            .tanpinmeisaiflg(CommonConstants.FLG_ON.equals(e.getStkbn()) ? CommonConstants.FLG_OFF
                : CommonConstants.FLG_ON)
            // セット商品コード（セット区分が「ON」なら「商品コード」を設定）
            .stshncd(CommonConstants.FLG_ON.equals(e.getStkbn()) ? e.getShncd() : null)
            // 有効区分（「ON」固定）
            .yukokbn(CommonConstants.FLG_ON)
            // 商品ＡＰ基本コード
            .shnapkihoncd(e.getShnapkihoncd())
            // 商品名称略称
            .shnnmryakusho(e.getShnnmryakusho())
            // 現価格区分
            .genzpricekbn(e.getPricekbn())
            // 新価格区分
            .shinpricekbn(e.getPricekbn())
            // 販促区分
            .hansokukbn(e.getHansokukbn())
            // 販促名称
            .hansokunm(e.getHansokunm())
            // 販促優先区分
            .hansokuyusenkbn(e.getHansokuyusenkbn())
            // 在庫単価
            .zaikount(e.getZaikount())
            // 色
            .iro(e.getIro())
            // サイズ名称
            .sz(e.getSz())
            // 売上取扱単位名称
            .uritrtskitani(e.getUritrtskitani())
            // 売上入数
            .uriirisu(e.getUriirisu())
            // 売上単位名称
            .uritani(e.getUritani())
            // 税設定区分
            .taxsetteikbn(e.getTaxsetteikbn())
            // 商品税区分
            .shntaxkbn(CommonConstants.FLG_ON.equals(e.getTaxsetteikbn()) ? e.getTaxkbn() : null)
            // 在庫アラーム数量
            .zaikoaramusuryo(e.getZaikoaramusuryo())
            // 取扱区分
            .trtskikbn(e.getTrtskikbn())
            // 特別単価区分
            .tokubetsuuntkbn(e.getTokubetsuuntkbn())
            // 総数（「0」固定）
            .sokazu("0")
            // 商品名称
            .shnnm(e.getShnnm())
            // セット区分
            .stkbn(e.getStkbn())
            // 料金種別特別単価フラグ
            .rknkindtokubetsuuntflg(e.getRknkindtokubetsuuntflg())
            // 受注数量は0にする
            .acptsuryo("")
            // 標準商品価格情報
            .hyojunShnPriceDto(C0801W02SwitchableShnPriceDto.builder()
                // 商品コード
                .shncd(e.getShncd())
                // 対象日
                .targetDate(baseDate)
                // 価格切替日
                .kakakukirikaedd(e.getHyojunpricekirikaedd())
                // 現価格
                .genzprice(e.getGenzhyojunprice())
                // 新価格
                .shinprice(e.getShinhyojunprice())
                // 現持ち帰り価格
                .genzmochikaeriprice(e.getGenzmochikaeriprice())
                // 新持ち帰り価格
                .shinmochikaeriprice(e.getShinmochikaeriprice()).build())
            // オープン商品価格情報
            .openShnPriceDto(C0801W02SwitchableShnPriceDto.builder()
                // 商品コード
                .shncd(e.getShncd())
                // 対象日
                .targetDate(baseDate)
                // 価格切替日
                .kakakukirikaedd(e.getOpnpricekirikaedd())
                // 現価格
                .genzprice(e.getGenzopnprice())
                // 新価格
                .shinprice(e.getShinopnprice())
                // 現持ち帰り価格
                .genzmochikaeriprice(e.getGenzopnmochikaeriprice())
                // 新持ち帰り価格
                .shinmochikaeriprice(e.getShinopnmochikaeriprice()).build())
            // 販促商品価格情報
            .hansokuShnPriceDto(C0801W02RangeShnPriceDto.builder()
                // 商品コード
                .shncd(e.getShncd())
                // 対象日
                .targetDate(baseDate)
                // 適用開始日
                .strtdd(e.getHansokustrtdd())
                // 適用終了日
                .shrydd(e.getHansokushrydd())
                // 配達価格
                .dlvryprice(e.getHansokudlvryprice())
                // 持ち帰り価格
                .mochikaeriprice(e.getHansokumochikaeriprice()).build())
            // 得意先別特別商品価格情報
            .tokubetsuShnPriceDto(C0801W02RangeShnPriceDto.builder()
                // 商品コード
                .shncd(e.getShncd())
                // 対象日
                .targetDate(baseDate)
                // 適用開始日
                .strtdd(CommonConstants.FLG_OFF.equals(e.getStkbn()) ? e.getTekiyostrtdd()
                    : e.getSet_tekiyostrtdd())
                // 適用終了日
                .shrydd(CommonConstants.FLG_OFF.equals(e.getStkbn()) ? e.getTekiyoshrydd()
                    : e.getSet_tekiyoshrydd())
                // 配達価格
                .dlvryprice(CommonConstants.FLG_OFF.equals(e.getStkbn()) ? e.getDlvryprice()
                    : e.getSet_dlvryprice())
                // 持ち帰り価格
                .mochikaeriprice(
                    CommonConstants.FLG_OFF.equals(e.getStkbn()) ? e.getMochikaeriprice()
                        : e.getSet_mochikaeriprice())
                .build())
            // 構築
            .build();
    }

    /**
     * 設置サーバ明細Entityの内容を元に受注配達明細DTOを生成する
     * 
     * @param e 設置サーバ明細Entity
     * @param baseDate 基準日
     * @return 受注配達明細DTO
     */
    public static C0801W02AcptDlvryDetailDto create(S02M048TInstalledServerDetailEntity e,
        String baseDate) {
        C0801W02AcptDlvryDetailDto dto = C0801W02AcptDlvryDetailDto.builder()
            // タイプ
            .type(DETAIL_TYPE.SERVER)
            // 得意先設置サーバ管理マスタ.商品コード（またはフロントパネルコード）
            .shncd(e.getShncd())
            // 商品マスタビュー.色
            .iro(e.getIro())
            // 商品マスタビュー.サイズ
            .sz(e.getSznm())
            // 商品マスタビュー.売上取扱単位
            .uritrtskitani(e.getUritani())
            // 商品マスタビュー.売上入数
            .uriirisu(e.getUriirisu())
            // 商品マスタビュー.売上取扱単位名称
            .uritani(e.getUritaninm())
            // 商品マスタビュー.大分類コード
            .lctgrycd(e.getLctgrycd())
            // 商品マスタビュー.中分類コード
            .mctgrycd(e.getMctgrycd())
            // 商品マスタビュー.小分類コード
            .sctgrycd(e.getSctgrycd())
            // 商品マスタビュー.現価格区分
            .genzpricekbn(e.getGenzpricekbn())
            // 商品マスタビュー.新価格区分
            .shinpricekbn(e.getShinpricekbn())
            // 販促区分（クエリでは取得していないため、販促関連項目の設定有無で判定）
            .hansokukbn(StringUtil.isEmpty(e.getHansokustrtdd()) ? CommonConstants.FLG_OFF
                : CommonConstants.FLG_ON)
            // 得意先マスタ.料金種別特別単価フラグ
            .rknkindtokubetsuuntflg(e.getRknkindtokubetsuuntflg())
            // 商品マスタビュー.在庫単価
            .zaikount(e.getZaikount())
            // 商品マスタビュー.商品税区分
            .taxkbn(e.getTaxkbn())
            // 商品マスタビュー.商品ＡＰ基本コード
            .shnapkihoncd(e.getShnapkihoncd())
            // 単品明細フラグ
            .tanpinmeisaiflg(CommonConstants.FLG_ON)
            // 商品税区分
            .shntaxkbn(e.getTaxkbn())
            // 税設定区分（クエリ結果に含まれないので税区分の値で判定）
            .taxsetteikbn(StringUtil.isEmpty(e.getTaxkbn()) ? CommonConstants.FLG_OFF
                : CommonConstants.FLG_ON)
            // データ作成元区分
            .datsksmotokbn(CommonConstants.KBN_SAKUSEIMOTO_JUCHUTOROKU)
            // 受注配達明細枝番号
            .acptdnpymeisaiedano("0")
            // 受注単価
            .acptunt("0")
            // 受注数量
            .acptsuryo("")
            // 有効区分
            .yukokbn(CommonConstants.FLG_ON)
            // 明細区分
            .meisaikbn(CommonConstants.KBN_MEISAI_SHOHIN)
            // 入力明細フラグ
            .nyuryokumeisaiflg(CommonConstants.FLG_ON)
            // 総数
            .sokazu("0")
            // 標準商品価格情報
            .hyojunShnPriceDto(C0801W02SwitchableShnPriceDto.builder()
                // 商品コード
                .shncd(e.getShncd())
                // 対象日
                .targetDate(baseDate)
                // 価格切替日
                .kakakukirikaedd(DateTimeUtil.toNoSlashedDate(e.getHyojunpricekirikaedd()))
                // 現価格
                .genzprice(e.getGenzhyojunprice())
                // 新価格
                .shinprice(e.getShinhyojunprice())
                // 現持ち帰り価格
                .genzmochikaeriprice(e.getGenzmochikaeriprice())
                // 新持ち帰り価格
                .shinmochikaeriprice(e.getShinmochikaeriprice()).build())
            // オープン商品価格情報
            .openShnPriceDto(C0801W02SwitchableShnPriceDto.builder()
                // 商品コード
                .shncd(e.getShncd())
                // 対象日
                .targetDate(baseDate)
                // 価格切替日
                .kakakukirikaedd(DateTimeUtil.toNoSlashedDate(e.getShoppricekirikaedd()))
                // 現価格
                .genzprice(e.getGenzshopprice())
                // 新価格
                .shinprice(e.getShinshopprice())
                // 現持ち帰り価格
                .genzmochikaeriprice(e.getGenzshopmochikaeriprice())
                // 新持ち帰り価格
                .shinmochikaeriprice(e.getShinshopmochikaeriprice()).build())
            // 販促商品価格情報
            .hansokuShnPriceDto(C0801W02RangeShnPriceDto.builder()
                // 商品コード
                .shncd(e.getShncd())
                // 対象日
                .targetDate(baseDate)
                // 適用開始日
                .strtdd(DateTimeUtil.toNoSlashedDate(e.getHansokustrtdd()))
                // 適用終了日
                .shrydd(DateTimeUtil.toNoSlashedDate(e.getHansokushrydd()))
                // 配達価格
                .dlvryprice(e.getHansokudlvryprice())
                // 持ち帰り価格
                .mochikaeriprice(e.getHansokumochikaeriprice()).build())
            // 得意先別特別商品価格情報
            .tokubetsuShnPriceDto(C0801W02RangeShnPriceDto.builder()
                // 商品コード
                .shncd(e.getShncd())
                // 対象日
                .targetDate(baseDate)
                // 適用開始日
                .strtdd(DateTimeUtil.toNoSlashedDate(e.getTekiyostrtdd()))
                // 適用終了日
                .shrydd(DateTimeUtil.toNoSlashedDate(e.getTekiyoshrydd()))
                // 配達価格
                .dlvryprice(e.getDlvryprice())
                // 持ち帰り価格
                .mochikaeriprice(e.getMochikaeriprice()).build())
            .build();
        // 商品名称略称（現新商品名称略称と商品名称切替日が取得できていればそれを用いる）
        if (!StringUtil.isEmpty(e.getShnnmkirikaedd())
            && !StringUtil.isEmpty(e.getGenzshnnmryakusho())
            && !StringUtil.isEmpty(e.getShinshnnmryakusho())) {
            dto.setShnnmryakusho(
                dto.getCurrentValue(e.getGenzshnnmryakusho(), e.getShinshnnmryakusho(),
                    DateTimeUtil.toNoSlashedDate(e.getShnnmkirikaedd()), baseDate));
        }
        return dto;
    }

    /**
     * お気に入り商品受注配達明細Entityの内容を元に受注明細DTOを生成する.
     * 
     * @param e お気に入り商品受注配達明細Entity
     * @param baseDate 基準日
     * @return 受注明細DTO
     */
    public static C0801W02AcptDlvryDetailDto create(S02M044TC0802W01Entity e, String baseDate) {
        return C0801W02AcptDlvryDetailDto.builder()
            // タイプ
            .type(DETAIL_TYPE.FAVORITE)
            // 大分類コード
            .lctgrycd(e.getLctgrycd())
            // 中分類コード
            .mctgrycd(e.getMctgrycd())
            // 小分類コード
            .sctgrycd(e.getSctgrycd())
            // 商品コード
            .shncd(e.getShncd())
            // 計上区分
            .keijokbn(e.getKeijokbn())
            // 税区分
            .taxkbn(CommonConstants.FLG_ON.equals(e.getTaxsetteikbn()) ? e.getTaxkbn() : null)
            // 明細区分（「商品」固定）
            .meisaikbn(CommonConstants.KBN_MEISAI_SHOHIN)
            // 入力明細フラグ（「ON」固定）
            .nyuryokumeisaiflg(CommonConstants.FLG_ON)
            // 単品明細フラグ（セット区分が「ON」なら「OFF」、「OFF」なら「ON」を設定）
            .tanpinmeisaiflg(CommonConstants.FLG_ON.equals(e.getStkbn()) ? CommonConstants.FLG_OFF
                : CommonConstants.FLG_ON)
            // セット商品コード（セット区分が「ON」なら「商品コード」を設定）
            .stshncd(CommonConstants.FLG_ON.equals(e.getStkbn()) ? e.getShncd() : null)
            // 有効区分（「ON」固定）
            .yukokbn(CommonConstants.FLG_ON)
            // 商品ＡＰ基本コード
            .shnapkihoncd(e.getShnapkihoncd())
            // 商品名称略称
            .shnnmryakusho(e.getShnnmryakusho())
            // 現価格区分
            .genzpricekbn(e.getPricekbn())
            // 新価格区分
            .shinpricekbn(e.getPricekbn())
            // 販促区分
            .hansokukbn(e.getHansokukbn())
            // 販促名称
            .hansokunm(e.getHansokunm())
            // 販促優先区分
            .hansokuyusenkbn(e.getHansokuyusenkbn())
            // 在庫単価
            .zaikount(e.getZaikount())
            // 色
            .iro(e.getIro())
            // サイズ名称
            .sz(e.getSz())
            // 売上取扱単位名称
            .uritrtskitani(e.getUritrtskitani())
            // 売上入数
            .uriirisu(e.getUriirisu())
            // 売上単位名称
            .uritani(e.getUritani())
            // 税設定区分
            .taxsetteikbn(e.getTaxsetteikbn())
            // 商品税区分
            .shntaxkbn(CommonConstants.FLG_ON.equals(e.getTaxsetteikbn()) ? e.getTaxkbn() : null)
            // 在庫アラーム数量
            .zaikoaramusuryo(e.getZaikoaramusuryo())
            // 取扱区分
            .trtskikbn(e.getTrtskikbn())
            // 特別単価区分
            .tokubetsuuntkbn(e.getTokubetsuuntkbn())
            // 総数（「0」固定）
            .sokazu("0")
            // 商品名称
            .shnnm(e.getShnnm())
            // セット区分
            .stkbn(e.getStkbn())
            // 料金種別特別単価フラグ
            .rknkindtokubetsuuntflg(e.getRknkindtokubetsuuntflg())
            // 受注数量は0にする
            .acptsuryo("")
            // 標準商品価格情報
            .hyojunShnPriceDto(C0801W02SwitchableShnPriceDto.builder()
                // 商品コード
                .shncd(e.getShncd())
                // 対象日
                .targetDate(baseDate)
                // 価格切替日
                .kakakukirikaedd(e.getHyojunpricekirikaedd())
                // 現価格
                .genzprice(e.getGenzhyojunprice())
                // 新価格
                .shinprice(e.getShinhyojunprice())
                // 現持ち帰り価格
                .genzmochikaeriprice(e.getGenzmochikaeriprice())
                // 新持ち帰り価格
                .shinmochikaeriprice(e.getShinmochikaeriprice()).build())
            // オープン商品価格情報
            .openShnPriceDto(C0801W02SwitchableShnPriceDto.builder()
                // 商品コード
                .shncd(e.getShncd())
                // 対象日
                .targetDate(baseDate)
                // 価格切替日
                .kakakukirikaedd(e.getOpnpricekirikaedd())
                // 現価格
                .genzprice(e.getGenzopnprice())
                // 新価格
                .shinprice(e.getShinopnprice())
                // 現持ち帰り価格
                .genzmochikaeriprice(e.getGenzopnmochikaeriprice())
                // 新持ち帰り価格
                .shinmochikaeriprice(e.getShinopnmochikaeriprice()).build())
            // 販促商品価格情報
            .hansokuShnPriceDto(C0801W02RangeShnPriceDto.builder()
                // 商品コード
                .shncd(e.getShncd())
                // 対象日
                .targetDate(baseDate)
                // 適用開始日
                .strtdd(e.getHansokustrtdd())
                // 適用終了日
                .shrydd(e.getHansokushrydd())
                // 配達価格
                .dlvryprice(e.getHansokudlvryprice())
                // 持ち帰り価格
                .mochikaeriprice(e.getHansokumochikaeriprice()).build())
            // 得意先別特別商品価格情報
            .tokubetsuShnPriceDto(C0801W02RangeShnPriceDto.builder()
                // 商品コード
                .shncd(e.getShncd())
                // 対象日
                .targetDate(baseDate)
                // 適用開始日
                .strtdd(CommonConstants.FLG_OFF.equals(e.getStkbn()) ? e.getTekiyostrtdd()
                    : e.getSet_tekiyostrtdd())
                // 適用終了日
                .shrydd(CommonConstants.FLG_OFF.equals(e.getStkbn()) ? e.getTekiyoshrydd()
                    : e.getSet_tekiyoshrydd())
                // 配達価格
                .dlvryprice(CommonConstants.FLG_OFF.equals(e.getStkbn()) ? e.getDlvryprice()
                    : e.getSet_dlvryprice())
                // 持ち帰り価格
                .mochikaeriprice(
                    CommonConstants.FLG_OFF.equals(e.getStkbn()) ? e.getMochikaeriprice()
                        : e.getSet_mochikaeriprice())
                .build())

            .build();
    }

    /**
     * 受注配達明細Entityの内容から受注配達明細DTOを生成する
     * 
     * @param e 受注配達明細Entity
     * @return 受注配達明細DTO
     */
    public static C0801W02AcptDlvryDetailDto create(S04T002TEntity e) {
        return C0801W02AcptDlvryDetailDto.builder()
            // タイプ
            .type(DETAIL_TYPE.LOADED)
            // ＡＰ基本コード
            .apkihoncd(e.getApkihoncd())
            // ショップコード
            .shopcd(e.getShopcd())
            // 受注伝票番号
            .acptdnpyno(e.getAcptdnpyno())
            // 枝番号
            .edano(Objects.toString(e.getEdano(), null))
            // 受注伝票明細番号
            .acptdnpymeisaino(Objects.toString(e.getAcptdnpymeisaino(), null))
            // 受注伝票明細枝番号
            .acptdnpymeisaiedano(Objects.toString(e.getAcptdnpymeisaiedano(), null))
            // 委託相手ＡＰ基本コード
            .itakuaiteapkihoncd(e.getItakuaiteapkihoncd())
            // 委託相手ショップコード
            .itakuaiteshopcd(e.getItakuaiteshopcd())
            // 委託相手受注伝票番号
            .itakuaiteacptdnpyno(e.getItakuaiteacptdnpyno())
            // 委託相手枝番号
            .itakuaiteedano(Objects.toString(e.getItakuaiteedano(), null))
            // 委託相手受注伝票明細番号
            .itakuaiteacptdnpymeisaino(Objects.toString(e.getItakuaiteacptdnpymeisaino(), null))
            // 委託相手受注伝票明細枝番号
            .itakuaiteacptdnpymeisaiedano(
                Objects.toString(e.getItakuaiteacptdnpymeisaiedano(), null))
            // 大分類コード
            .lctgrycd(e.getLctgrycd())
            // 中分類コード
            .mctgrycd(e.getMctgrycd())
            // 小分類コード
            .sctgrycd(e.getSctgrycd())
            // 商品コード
            .shncd(e.getShncd())
            // 配布物ＡＰ基本コード
            .haifubutsuapkihoncd(e.getHaifubutsuapkihoncd())
            // 配布物ショップコード
            .haifubutsushopcd(e.getHaifubutsushopcd())
            // 配布物コード
            .haifubutsucd(e.getHaifubutsucd())
            // 型式
            .katasiki(e.getKatasiki())
            // 計上区分
            .keijokbn(e.getKeijokbn())
            // 受注数量
            .acptsuryo(Objects.toString(e.getAcptsuryo(), null))
            // 標準単価
            .hyojununt(Objects.toString(e.getHyojununt(), null))
            // 受注単価
            .acptunt(Objects.toString(e.getAcptunt(), null))
            // 受注金額
            .acptkin(Objects.toString(e.getAcptkin(), null))
            // 受注単価消費税金額
            .acptuntshohitaxkin(Objects.toString(e.getAcptuntshohitaxkin(), null))
            // 受注消費税金額
            .acptshohitaxkin(Objects.toString(e.getAcptshohitaxkin(), null))
            // 委託会社受注単価
            .itakucmpnyacptunt(Objects.toString(e.getItakucmpnyacptunt(), null))
            // 委託会社受注単価消費税金額
            .itakucmpnyacptuntshohitaxkin(
                Objects.toString(e.getItakucmpnyacptuntshohitaxkin(), null))
            // 委託会社受注消費税金額
            .itakucmpnyacptshohitaxkin(Objects.toString(e.getItakucmpnyacptshohitaxkin(), null))
            // 納品数量
            .nohinsuryo(Objects.toString(e.getNohinsuryo(), null))
            // 納品単価
            .nohinunt(Objects.toString(e.getNohinunt(), null))
            // 納品金額
            .nohinkin(Objects.toString(e.getNohinkin(), null))
            // 納品単価消費税金額
            .nohinuntshohitaxkin(Objects.toString(e.getNohinuntshohitaxkin(), null))
            // 納品消費税金額
            .nohinshohitaxkin(Objects.toString(e.getNohinshohitaxkin(), null))
            // 残回数
            .zankaisu(Objects.toString(e.getZankaisu(), null))
            // 総回数
            .soukaisu(Objects.toString(e.getSoukaisu(), null))
            // 税区分
            .taxkbn(e.getTaxkbn())
            // 有償区分
            .yusyokbn(e.getYusyokbn())
            // 明細区分
            .meisaikbn(e.getMeisaikbn())
            // 入力明細フラグ
            .nyuryokumeisaiflg(e.getNyuryokumeisaiflg())
            // 単品明細フラグ
            .tanpinmeisaiflg(e.getTanpinmeisaiflg())
            // セット商品コード
            .stshncd(e.getStshncd())
            // 明細備考
            .meisaibiko(e.getMeisaibiko())
            // データ作成元区分
            .datsksmotokbn(e.getDatsksmotokbn())
            // 有効区分
            .yukokbn(e.getYukokbn())
            // 売上伝票番号
            .uridnpyno(e.getUridnpyno())
            // 売上伝票枝番号
            .uridnpyedano(Objects.toString(e.getUridnpyedano(), null))
            // 売上伝票明細番号
            .uridnpymeisaino(Objects.toString(e.getUridnpymeisaino(), null))
            // 売上伝票明細枝番号
            .uridnpymeisaiedano(Objects.toString(e.getUridnpymeisaiedano(), null))
            // 登録者ＡＰ基本コード
            .toshaapkihoncd(e.getToshaapkihoncd())
            // 登録者ショップコード
            .toshashopcd(e.getToshashopcd())
            // 登録者コード
            .toshacd(e.getToshacd())
            // 登録システム日付
            .inptsystmymd(e.getInptsystmymd())
            // 登録システム時間
            .inptsystmjikan(e.getInptsystmjikan())
            // 更新者ＡＰ基本コード
            .koshaapkihoncd(e.getKoshaapkihoncd())
            // 更新者ショップコード
            .koshashopcd(e.getKoshashopcd())
            // 更新者コード
            .koshacd(e.getKoshacd())
            // 更新システム日付
            .kshnsystmymd(e.getKshnsystmymd())
            // 更新システム時間
            .kshnsystmjikan(e.getKshnsystmjikan()).build();
    }

    /**
     * 受注配達明細Entityの内容を元に受注明細DTOを生成する.
     * 
     * @param e 受注配達明細Entity
     * @param baseDate 基準日
     * @return 受注明細DTO
     */
    public static C0801W02AcptDlvryDetailDto create(S04T002TC0805W01Entity e, String baseDate) {
        return C0801W02AcptDlvryDetailDto.builder()
            // タイプ
            .type(DETAIL_TYPE.LOADED)
            // ＡＰ基本コード
            .apkihoncd(e.getApkihoncd())
            // ショップコード
            .shopcd(e.getShopcd())
            // 受注伝票番号
            .acptdnpyno(e.getAcptdnpyno())
            // 枝番号
            .edano(Objects.toString(e.getEdano(), null))
            // 受注伝票明細番号
            .acptdnpymeisaino(Objects.toString(e.getAcptdnpymeisaino(), null))
            // 受注伝票明細枝番号
            .acptdnpymeisaiedano(Objects.toString(e.getAcptdnpymeisaiedano(), null))
            // 委託相手ＡＰ基本コード
            .itakuaiteapkihoncd(e.getItakuaiteapkihoncd())
            // 委託相手ショップコード
            .itakuaiteshopcd(e.getItakuaiteshopcd())
            // 委託相手受注伝票番号
            .itakuaiteacptdnpyno(e.getItakuaiteacptdnpyno())
            // 委託相手枝番号
            .itakuaiteedano(Objects.toString(e.getItakuaiteedano(), null))
            // 委託相手受注伝票明細番号
            .itakuaiteacptdnpymeisaino(Objects.toString(e.getItakuaiteacptdnpymeisaino(), null))
            // 委託相手受注伝票明細枝番号
            .itakuaiteacptdnpymeisaiedano(
                Objects.toString(e.getItakuaiteacptdnpymeisaiedano(), null))
            // 大分類コード
            .lctgrycd(e.getLctgrycd())
            // 中分類コード
            .mctgrycd(e.getMctgrycd())
            // 小分類コード
            .sctgrycd(e.getSctgrycd())
            // 商品コード
            .shncd(e.getShncd())
            // 配布物ＡＰ基本コード
            .haifubutsuapkihoncd(e.getHaifubutsuapkihoncd())
            // 配布物ショップコード
            .haifubutsushopcd(e.getHaifubutsushopcd())
            // 配布物コード
            .haifubutsucd(e.getHaifubutsucd())
            // 型式
            .katasiki(e.getKatasiki())
            // 計上区分
            .keijokbn(e.getKeijokbn())
            // 受注数量
            .acptsuryo(Objects.toString(e.getAcptsuryo(), null))
            // 標準単価
            .hyojununt(Objects.toString(e.getHyojununt(), null))
            // 受注単価
            .acptunt(Objects.toString(e.getAcptunt(), null))
            // 受注金額
            .acptkin(Objects.toString(e.getAcptkin(), null))
            // 受注単価消費税金額
            .acptuntshohitaxkin(Objects.toString(e.getAcptuntshohitaxkin(), null))
            // 受注消費税金額
            .acptshohitaxkin(Objects.toString(e.getAcptshohitaxkin(), null))
            // 委託会社受注単価
            .itakucmpnyacptunt(Objects.toString(e.getItakucmpnyacptunt(), null))
            // 委託会社受注単価消費税金額
            .itakucmpnyacptuntshohitaxkin(
                Objects.toString(e.getItakucmpnyacptuntshohitaxkin(), null))
            // 委託会社受注消費税金額
            .itakucmpnyacptshohitaxkin(Objects.toString(e.getItakucmpnyacptshohitaxkin(), null))
            // 納品数量
            .nohinsuryo(Objects.toString(e.getNohinsuryo(), null))
            // 納品単価
            .nohinunt(Objects.toString(e.getNohinunt(), null))
            // 納品金額
            .nohinkin(Objects.toString(e.getNohinkin(), null))
            // 納品単価消費税金額
            .nohinuntshohitaxkin(Objects.toString(e.getNohinuntshohitaxkin(), null))
            // 納品消費税金額
            .nohinshohitaxkin(Objects.toString(e.getNohinshohitaxkin(), null))
            // 残回数
            .zankaisu(Objects.toString(e.getZankaisu(), null))
            // 総回数
            .soukaisu(Objects.toString(e.getSoukaisu(), null))
            // 税区分
            .taxkbn(e.getTaxkbn())
            // 有償区分
            .yusyokbn(e.getYusyokbn())
            // 明細区分
            .meisaikbn(e.getMeisaikbn())
            // 入力明細フラグ
            .nyuryokumeisaiflg(e.getNyuryokumeisaiflg())
            // 単品明細フラグ
            .tanpinmeisaiflg(e.getTanpinmeisaiflg())
            // セット商品コード
            .stshncd(e.getStshncd())
            // 明細備考
            .meisaibiko(e.getMeisaibiko())
            // データ作成元区分
            .datsksmotokbn(e.getDatsksmotokbn())
            // 有効区分
            .yukokbn(e.getYukokbn())
            // 更新システム日付
            .kshnsystmymd(e.getKshnsystmymd())
            // 更新システム時間
            .kshnsystmjikan(e.getKshnsystmjikan())
            // 商品ＡＰ基本コード
            .shnapkihoncd(e.getShnapkihoncd())
            // 商品名称略称
            .shnnmryakusho(e.getShnnmryakusho())
            // 現価格区分
            .genzpricekbn(e.getPricekbn())
            // 新価格区分
            .shinpricekbn(e.getPricekbn())
            // 販促区分
            .hansokukbn(e.getHansokukbn())
            // 販促優先区分
            .hansokuyusenkbn(e.getHansokuyusenkbn())
            // 在庫単価
            .zaikount(e.getZaikount())
            // 色
            .iro(e.getIro())
            // サイズ名称
            .sz(e.getSz())
            // 売上取扱単位名称
            .uritrtskitani(e.getUritrtskitani())
            // 売上入数
            .uriirisu(e.getUriirisu())
            // 売上単位名称
            .uritani(e.getUritani())
            // 税設定区分
            .taxsetteikbn(e.getTaxsetteikbn())
            // 商品税区分
            .shntaxkbn(e.getShntaxkbn())
            // 在庫アラーム数量
            .zaikoaramusuryo(e.getZaikoaramusuryo())
            // 取扱区分
            .trtskikbn(e.getTrtskikbn())
            // 特別単価区分
            .tokubetsuuntkbn(e.getTokubetsuuntkbn())
            // 計上区分名称
            .keijokbnnm(e.getKeijokbnnm())
            // 総数
            .sokazu(e.getSokazu())
            // 商品名称
            .shnnm(e.getShnnm())
            // 売上伝票番号
            .uridnpyno(e.getUridnpyno())
            // 売上伝票枝番号
            .uridnpyedano(Objects.toString(e.getUridnpyedano(), null))
            // 売上伝票明細番号
            .uridnpymeisaino(Objects.toString(e.getUridnpymeisaino(), null))
            // 売上伝票明細枝番号
            .uridnpymeisaiedano(Objects.toString(e.getUridnpymeisaiedano(), null))
            // 登録者ＡＰ基本コード
            .toshaapkihoncd(e.getToshaapkihoncd())
            // 登録者ショップコード
            .toshashopcd(e.getToshashopcd())
            // 登録者コード
            .toshacd(e.getToshacd())
            // 登録システム日付
            .inptsystmymd(e.getInptsystmymd())
            // 登録システム時間
            .inptsystmjikan(e.getInptsystmjikan())
            // 更新者ＡＰ基本コード
            .koshaapkihoncd(e.getKoshaapkihoncd())
            // 更新者ショップコード
            .koshashopcd(e.getKoshashopcd())
            // 更新者コード
            .koshacd(e.getKoshacd())
            // 登録担当者名
            .toshamei(e.getToshamei())
            // 更新担当者名
            .koshamei(e.getKoshamei())
            // セット区分
            .stkbn(e.getStkbn())
            // 料金種別特別単価フラグ
            .rknkindtokubetsuuntflg(e.getRknkindtokubetsuuntflg())
            // 標準商品価格情報
            .hyojunShnPriceDto(C0801W02SwitchableShnPriceDto.builder()
                // 商品コード
                .shncd(e.getShncd())
                // 対象日
                .targetDate(baseDate)
                // 価格切替日
                .kakakukirikaedd(e.getHyojunpricekirikaedd())
                // 現価格
                .genzprice(e.getGenzhyojunprice())
                // 新価格
                .shinprice(e.getShinhyojunprice())
                // 現持ち帰り価格
                .genzmochikaeriprice(e.getGenzmochikaeriprice())
                // 新持ち帰り価格
                .shinmochikaeriprice(e.getShinmochikaeriprice()).build())
            // オープン商品価格情報
            .openShnPriceDto(C0801W02SwitchableShnPriceDto.builder()
                // 商品コード
                .shncd(e.getShncd())
                // 対象日
                .targetDate(baseDate)
                // 価格切替日
                .kakakukirikaedd(e.getOpnpricekirikaedd())
                // 現価格
                .genzprice(e.getGenzopnprice())
                // 新価格
                .shinprice(e.getShinopnprice())
                // 現持ち帰り価格
                .genzmochikaeriprice(e.getGenzopnmochikaeriprice())
                // 新持ち帰り価格
                .shinmochikaeriprice(e.getShinopnmochikaeriprice()).build())
            // 販促商品価格情報
            .hansokuShnPriceDto(C0801W02RangeShnPriceDto.builder()
                // 商品コード
                .shncd(e.getShncd())
                // 対象日
                .targetDate(baseDate)
                // 適用開始日
                .strtdd(e.getHansokustrtdd())
                // 適用終了日
                .shrydd(e.getHansokushrydd())
                // 配達価格
                .dlvryprice(e.getHansokudlvryprice())
                // 持ち帰り価格
                .mochikaeriprice(e.getHansokumochikaeriprice()).build())
            // 得意先別特別商品価格情報
            .tokubetsuShnPriceDto(C0801W02RangeShnPriceDto.builder()
                // 商品コード
                .shncd(e.getShncd())
                // 対象日
                .targetDate(baseDate)
                // 適用開始日
                .strtdd(CommonConstants.FLG_ON.equals(e.getTanpinmeisaiflg()) ? e.getTekiyostrtdd()
                    : e.getSet_tekiyostrtdd())
                // 適用終了日
                .shrydd(CommonConstants.FLG_ON.equals(e.getTanpinmeisaiflg()) ? e.getTekiyoshrydd()
                    : e.getSet_tekiyoshrydd())
                // 配達価格
                .dlvryprice(
                    CommonConstants.FLG_ON.equals(e.getTanpinmeisaiflg()) ? e.getDlvryprice()
                        : e.getSet_dlvryprice())
                // 持ち帰り価格
                .mochikaeriprice(
                    CommonConstants.FLG_ON.equals(e.getTanpinmeisaiflg()) ? e.getMochikaeriprice()
                        : e.getSet_mochikaeriprice())
                .build())
            .build();
    }

    /**
     * 自身の内容を元に新規の受注配達明細テーブルEntityを生成する
     * 
     * @param context コンテキスト
     * @return 受注配達明細テーブルEntity
     */
    public S04T002TEntity createNewEntity(C0801W02ProcessingContextDto context) {
        S04T002TEntity entity = new S04T002TEntity();
        // 受注伝票明細枝番号
        entity.setAcptdnpymeisaiedano((short) 0);
        // 受注数量
        entity.setAcptsuryo(BigDecimal.ZERO);
        // 納品数量
        entity.setNohinsuryo(BigDecimal.ZERO);
        // 納品単価
        entity.setNohinunt(BigDecimal.ZERO);
        // 納品金額
        entity.setNohinkin(0L);
        // 納品単価消費税金額
        entity.setNohinuntshohitaxkin(0L);
        // 納品消費税金額
        entity.setNohinshohitaxkin(0L);
        // 有償区分
        entity.setYusyokbn(CommonConstants.KBN_YUSHO_NONE);
        // 明細区分
        entity.setMeisaikbn(CommonConstants.KBN_MEISAI_SHOHIN);
        // 入力明細フラグ
        entity.setNyuryokumeisaiflg(CommonConstants.FLG_ON);
        // 単品明細フラグ
        entity.setTanpinmeisaiflg(CommonConstants.FLG_ON);
        // データ作成元区分
        entity.setDatsksmotokbn(CommonConstants.KBN_SAKUSEIMOTO_JUCHUTOROKU);
        // 売上伝票番号
        entity.setUridnpyno("");
        // 売上伝票枝番号
        entity.setUridnpyedano(null);
        // 売上伝票明細番号
        entity.setUridnpymeisaino(null);
        // 売上伝票明細枝番号
        entity.setUridnpymeisaiedano(null);
        // 有効区分
        entity.setYukokbn(CommonConstants.FLG_ON);
        // タイムスタンプ等
        setContextForInsert(entity, context);
        return entity;
    }

    /**
     * 受注配達明細テーブルEntityに自身の内容を反映する
     * 
     * @param entity 受注配達明細テーブルEntity
     * @param context コンテキスト
     * @param acptDlvryDto 受注配達DTO
     */
    public void fillPropertiesToEntity(S04T002TEntity entity, C0801W02ProcessingContextDto context,
        C0801W02AcptDlvryDto acptDlvryDto) {
        // AP基本コード
        entity.setApkihoncd(acptDlvryDto.getApkihoncd());
        // ショップコード
        entity.setShopcd(acptDlvryDto.getShopcd());
        // 受注伝票番号
        entity.setAcptdnpyno(acptDlvryDto.getAcptdnpyno());
        // 枝番号
        entity.setEdano(getShortValue(acptDlvryDto.getEdano()));
        // 受注伝票明細番号
        entity.setAcptdnpymeisaino(getIntValue(acptdnpymeisaino));
        // 受注伝票明細枝番号
        entity.setAcptdnpymeisaiedano(getShortValue(acptdnpymeisaiedano));
        // データ作成元区分
        entity.setDatsksmotokbn(acptDlvryDto.getDatsksmotokbn());
        // 委託相手AP基本コード
        entity.setItakuaiteapkihoncd(itakuaiteapkihoncd);
        // 委託相手ショップコード
        entity.setItakuaiteshopcd(this.getItakuaiteshopcd());
        // 委託相手受注伝票番号
        entity.setItakuaiteacptdnpyno(this.getItakuaiteacptdnpyno());
        // 委託相手枝番号
        entity.setItakuaiteedano(getNullableShortValue(itakuaiteedano));
        // 委託相手受注伝票明細番号
        entity.setItakuaiteacptdnpymeisaino(getNullableIntValue(itakuaiteacptdnpymeisaino));
        // 委託相手受注伝票明細枝番号
        entity.setItakuaiteacptdnpymeisaiedano(getNullableShortValue(itakuaiteacptdnpymeisaiedano));
        // 他拠点の場合、委託相手受注伝票明細番号と委託相手受注伝票明細枝番号を設定する
        // ※本部受注データ連携バッチでは委託相手受注伝票番号と委託相手枝番号しか委託元のデータに反映しない
        // __本部受注データ連携バッチが委託先の明細を作るときも、委託元の明細番号を設定するだけ
        if (acptDlvryDto.isTakyoten()) {
            entity.setItakuaiteacptdnpymeisaino(entity.getAcptdnpymeisaino());
            entity.setItakuaiteacptdnpymeisaiedano(entity.getAcptdnpymeisaiedano());
        }
        // 委託相手受注単価
        entity.setItakucmpnyacptunt(getNullableDecimalValue(itakucmpnyacptunt));
        // 委託相手受注単価消費税金額
        entity.setItakucmpnyacptuntshohitaxkin(getNullableLongValue(itakucmpnyacptuntshohitaxkin));
        // 委託相手受注消費税金額
        entity.setItakucmpnyacptshohitaxkin(getNullableLongValue(itakucmpnyacptshohitaxkin));

        if (CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(meisaikbn)) {
            // 配布物の場合
            // 配布物AP基本コード
            entity.setHaifubutsuapkihoncd(haifubutsuapkihoncd);
            // 配布物ショップコード
            entity.setHaifubutsushopcd(haifubutsushopcd);
            // 配布物コード
            entity.setHaifubutsucd(haifubutsucd);
            // 受注数量
            entity.setAcptsuryo(getDecimalValue(acptsuryo));
            // 有償区分
            entity.setYusyokbn(CommonConstants.KBN_YUSHO_NONE);
            // 明細区分
            entity.setMeisaikbn(CommonConstants.KBN_MEISAI_HAIHUBUTSU);
            // 入力明細フラグ
            entity.setNyuryokumeisaiflg(CommonConstants.FLG_ON);
            // 単品明細フラグ
            entity.setTanpinmeisaiflg(CommonConstants.FLG_ON);
            if (!StringUtil.isEmpty(nohinsuryo)) {
                // 納品数量
                entity.setNohinsuryo(getNullableDecimalValue(nohinsuryo));
            }
        } else if (CommonConstants.KBN_MEISAI_DAIBIKI.equals(meisaikbn)) {
            // 代引の場合
            // 商品大分類コード
            entity.setLctgrycd(lctgrycd);
            // 商品中分類コード
            entity.setMctgrycd(mctgrycd);
            // 商品症分類コード
            entity.setSctgrycd(sctgrycd);
            // 商品コード
            entity.setShncd(shncd);
            // 型式
            entity.setKatasiki(katasiki);
            // 計上区分
            entity.setKeijokbn(keijokbn);
            // 受注数量
            entity.setAcptsuryo(getDecimalValue(acptsuryo));
            // 標準単価
            entity.setHyojununt(getDecimalValue(hyojununt));
            // 受注単価
            entity.setAcptunt(getDecimalValue(acptunt));
            // 受注金額
            entity.setAcptkin(getLongValue(acptkin));
            // 受注単価消費税金額
            entity.setAcptuntshohitaxkin(getLongValue(acptuntshohitaxkin));
            // 受注消費税金額
            entity.setAcptshohitaxkin(getLongValue(acptshohitaxkin));
            // 税区分
            entity.setTaxkbn(taxkbn);
            // 有償区分
            entity.setYusyokbn(acptRelation.getYushokbn());
            // 明細区分
            entity.setMeisaikbn(CommonConstants.KBN_MEISAI_DAIBIKI);
            // 入力明細フラグ
            entity.setNyuryokumeisaiflg(nyuryokumeisaiflg);
            // 単品明細フラグ
            entity.setTanpinmeisaiflg(tanpinmeisaiflg);
            // セット商品コード
            entity.setStshncd(stshncd);
            // 明細備考
            entity.setMeisaibiko(meisaibiko);
            // 納品数量
            entity.setNohinsuryo(getNullableDecimalValue(nohinsuryo));
            // 納品単価
            entity.setNohinunt(getNullableDecimalValue(nohinunt));
            // 納品金額
            entity.setNohinkin(getNullableLongValue(nohinkin));
            // 納品単価消費税金額
            entity.setNohinuntshohitaxkin(getNullableLongValue(nohinuntshohitaxkin));
            // 納品消費税金額
            entity.setNohinshohitaxkin(getNullableLongValue(nohinshohitaxkin));
            // 売上伝票番号
            entity.setUridnpyno(uridnpyno);
            // 売上伝票枝番号
            entity.setUridnpyedano(getNullableShortValue(uridnpyedano));
            // 売上伝票明細番号
            entity.setUridnpymeisaino(getNullableIntValue(uridnpymeisaino));
            // 売上伝票明細枝番号
            entity.setUridnpymeisaiedano(getNullableShortValue(uridnpymeisaiedano));
        } else {
            // 商品大分類コード
            entity.setLctgrycd(lctgrycd);
            // 商品中分類コード
            entity.setMctgrycd(mctgrycd);
            // 商品症分類コード
            entity.setSctgrycd(sctgrycd);
            // 商品コード
            entity.setShncd(shncd);
            // 型式
            entity.setKatasiki(katasiki);
            // 計上区分
            entity.setKeijokbn(keijokbn);
            // 受注数量
            entity.setAcptsuryo(getDecimalValue(acptsuryo));
            // 標準単価
            entity.setHyojununt(getDecimalValue(hyojununt));
            // 受注単価
            entity.setAcptunt(getDecimalValue(acptunt));
            // 税区分
            entity.setTaxkbn(taxkbn);
            if (CommonConstants.KBN_MEISAI_SHOHIZEI.equals(meisaikbn)) {
                // 有償区分
                entity.setYusyokbn(yusyokbn);
                // 明細区分
                entity.setMeisaikbn(meisaikbn);
            } else {
                // 有償区分
                entity.setYusyokbn(acptRelation.getYushokbn());
                // 明細区分
                entity.setMeisaikbn(CommonConstants.KBN_MEISAI_SHOHIN);
            }
            // 入力明細フラグ
            entity.setNyuryokumeisaiflg(nyuryokumeisaiflg);
            // 単品明細フラグ
            entity.setTanpinmeisaiflg(tanpinmeisaiflg);
            // セット商品コード
            entity.setStshncd(stshncd);
            // 明細備考
            entity.setMeisaibiko(meisaibiko);
            if (!StringUtil.isEmpty(nohinsuryo)) {
                // 納品数量
                entity.setNohinsuryo(getNullableDecimalValue(nohinsuryo));
                // 納品単価
                entity.setNohinunt(getNullableDecimalValue(nohinunt));
                // 納品金額
                entity.setNohinkin(getNullableLongValue(nohinkin));
                // 納品単価消費税金額
                entity.setNohinuntshohitaxkin(getNullableLongValue(nohinuntshohitaxkin));
                // 納品消費税金額
                entity.setNohinshohitaxkin(getNullableLongValue(nohinshohitaxkin));
                // 売上伝票番号
                entity.setUridnpyno(uridnpyno);
                // 売上伝票枝番号
                entity.setUridnpyedano(getNullableShortValue(uridnpyedano));
                // 売上伝票明細番号
                entity.setUridnpymeisaino(getNullableIntValue(uridnpymeisaino));
                // 売上伝票明細枝番号
                entity.setUridnpymeisaiedano(getNullableShortValue(uridnpymeisaiedano));
            }
            // 受注金額
            entity.setAcptkin(getLongValue(acptkin));
            // 受注単価消費税金額
            entity.setAcptuntshohitaxkin(getLongValue(acptuntshohitaxkin));
            // 受注消費税金額
            entity.setAcptshohitaxkin(getLongValue(acptshohitaxkin));
        }
        // タイムスタンプ等
        setContextForUpdate(entity, context);
    }

    /**
     * 登録時のタイムスタンプ等をコンテキストから設定する
     * 
     * @param entity 受注配達明細テーブルEntity
     * @param context コンテキスト
     */
    public void setContextForInsert(S04T002TEntity entity, C0801W02ProcessingContextDto context) {
        // 登録者AP基本コード
        entity.setToshaapkihoncd(context.getApkihoncd());
        // 登録者ショップコード
        entity.setToshashopcd(context.getShopcd());
        // 登録担当者コード
        entity.setToshacd(context.getTnshacd());
        // 登録システム日付
        entity.setInptsystmymd(context.getSystemDate());
        // 登録システム時刻
        entity.setInptsystmjikan(context.getSystemTime());
        // 登録運用日付
        entity.setInptunyoymd(context.getOperationDate());
        // 登録IPアドレス
        entity.setInptipadrse(context.getIpAddress());
        // 登録プログラムID
        entity.setInptpgmid(context.getProgramId());
    }

    /**
     * 更新時のタイムスタンプ等をコンテキストから設定する
     * 
     * @param entity 受注配達明細テーブルEntity
     * @param context コンテキスト
     */
    public void setContextForUpdate(S04T002TEntity entity, C0801W02ProcessingContextDto context) {
        // 更新者AP基本コード
        entity.setKoshaapkihoncd(context.getApkihoncd());
        // 更新者ショップコード
        entity.setKoshashopcd(context.getShopcd());
        // 更新担当者コード
        entity.setKoshacd(context.getTnshacd());
        // 更新システム日付
        entity.setKshnsystmymd(context.getSystemDate());
        // 更新システム時刻
        entity.setKshnsystmjikan(context.getSystemTime());
        // 更新運用日付
        entity.setKshnunyoymd(context.getOperationDate());
        // 更新IPアドレス
        entity.setKshnipadrse(context.getIpAddress());
        // 更新プログラムID
        entity.setKshnpgmid(context.getProgramId());
    }

    /**
     * 受注配達明細テーブルのプライマリキーを生成する
     * 
     * @return プライマリキー
     */
    public S04T002TEntityKey createEntityKey() {
        S04T002TEntityKey key = new S04T002TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setAcptdnpyno(acptdnpyno);
        key.setEdano(getShortValue(edano));
        key.setAcptdnpymeisaino(getIntValue(acptdnpymeisaino));
        key.setAcptdnpymeisaiedano(getShortValue(acptdnpymeisaiedano));
        return key;
    }

    /**
     * 受注配達明細DTOをソートするためのComparatorを取得する <br>
     * 
     * 以下の項目の昇順でソートするComparatorとなる
     * <ul>
     * <li>AP基本コード</li>
     * <li>ショップコード</li>
     * <li>受注伝票番号</li>
     * <li>枝番号</li>
     * <li>受注伝票明細番号</li>
     * <li>受注伝票明細枝番号</li>
     * </ul>
     * 
     * @return Comparator
     */
    public static Comparator<C0801W02AcptDlvryDetailDto> getOrderBy() {
        // 各項目ともnullは最後にする
        return
        // AP基本コード
        comparing(C0801W02AcptDlvryDetailDto::getApkihoncd, nullsLast(naturalOrder()))
            // ショップコード
            .thenComparing(C0801W02AcptDlvryDetailDto::getShopcd, nullsLast(naturalOrder()))
            // 受注伝票番号
            .thenComparing(C0801W02AcptDlvryDetailDto::getAcptdnpyno, nullsLast(naturalOrder()))
            // 枝番号（数値比較）
            .thenComparing(d -> d.getIntValue(d.getEdano()), nullsLast(naturalOrder()))
            // 受注伝票明細番号（数値比較）
            .thenComparing(d -> d.getIntValue(d.getAcptdnpymeisaino()), nullsLast(naturalOrder()))
            // 受注伝票明細枝番号（数値比較）
            .thenComparing(d -> d.getIntValue(d.getAcptdnpymeisaiedano()),
                nullsLast(naturalOrder()));
    }

    /**
     * 受注配達明細DTOの種別を示す列挙
     */
    public static enum DETAIL_TYPE {
        /** お気に入り商品 */
        FAVORITE,
        /** 設置サーバ */
        SERVER,
        /** 商品 */
        SHN,
        /** 複写明細 */
        COPY,
        /** セッション格納済 */
        SESSION,
        /** 変更あり */
        MODIFIED,
        /** DB反映済 */
        UPDATED,
        /** 登録済明細 */
        LOADED,
        /** 追加対象 */
        TO_ADD,
        /** 削除対象 */
        TO_REMOVE;
    }
}
