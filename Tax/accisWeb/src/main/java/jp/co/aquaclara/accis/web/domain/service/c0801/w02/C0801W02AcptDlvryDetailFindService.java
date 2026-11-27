package jp.co.aquaclara.accis.web.domain.service.c0801.w02;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M044TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M048TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02V001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T002TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M044TC0802W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M048TgetInstalledServerDetailListCondition;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02V001TC0802W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S04T002TC0805W01Entity;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02AcptDlvryDetailDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02AcptDlvryDetailDto.DETAIL_TYPE;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02AcptDlvryDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02AcptRelationDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02SettingDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02SettingOptionDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02ShnDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02ShopCalculateOptionDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02ShopMasterDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02SysMngDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02TokuisakiDlvryDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02TokuisakiMasterDto;
import jp.co.aquaclara.accis.web.domain.service.c0800.UtilityService;
import jp.co.aquaclara.accis.web.domain.service.x9901.StringUtil;

/**
 * C0801W02_受注登録（受注登録）_受注配達明細検索サービス.<br>
 */
@Service
public class C0801W02AcptDlvryDetailFindService {

    private static final String ZERO = BigDecimal.ZERO.stripTrailingZeros().toPlainString();

    /** ユーティリティ */
    @Autowired
    private UtilityService util;

    /** 受注配達検索サービス */
    @Autowired
    private C0801W02AcptDlvryFindService finder;

    /** 受注配達明細価格サービス */
    @Autowired
    private C0801W02AcptDlvryDetailPriceService priceService;

    /** 得意先サービス */
    @Autowired
    private C0801W02TokuisakiService tokuisakiService;

    /** 商品サービス. */
    @Autowired
    private C0801W02ShnService shnService;

    /** システム管理サービス */
    @Autowired
    private C0801W02SysMngService sysMngService;

    /** 受注配達明細テーブルDAO */
    @Autowired
    private S04T002TDao s04t002tDao;

    /** お気に入り商品マスタDAO */
    @Autowired
    private S02M044TDao s02m044tDao;

    /** 商品マスタビューDAO */
    @Autowired
    private S02V001TDao s02v001tDao;

    /** 得意先設置サーバ管理マスタDAO */
    @Autowired
    private S02M048TDao s02m048tDao;

    /** 明細をマージする際のフィルタ */
    public static final Predicate<C0801W02AcptDlvryDetailDto> DETAIL_MERGE_FILTER =
        (dto) -> !StringUtil.isEmpty(dto.getAcptsuryo()) && !"0".equals(dto.getAcptsuryo());

    /** 明細に商品行を追加する際のフィルタ */
    public static final Predicate<C0801W02AcptDlvryDetailDto> INSERT_ROW_FILTER =
        (dto) -> dto.getType() == C0801W02AcptDlvryDetailDto.DETAIL_TYPE.SHN
            || (!StringUtil.isEmpty(dto.getAcptsuryo()) && !"0".equals(dto.getAcptsuryo()));

    /** 明細に行を複写した際のフィルタ */
    public static final Predicate<C0801W02AcptDlvryDetailDto> APPEND_ROW_FILTER =
        (dto) -> dto.getType() == C0801W02AcptDlvryDetailDto.DETAIL_TYPE.COPY
            || (!StringUtil.isEmpty(dto.getAcptsuryo()) && !"0".equals(dto.getAcptsuryo()));

    /** 画面表示用の受注配達明細DTOリストにするためのフィルタ */
    public static final Predicate<C0801W02AcptDlvryDetailDto> ONLY_DISPLAY_ROW_FILTER =
        dto -> CommonConstants.FLG_ON.equals(dto.getNyuryokumeisaiflg())
            && StringUtil.isEmpty(dto.getHaifubutsucd());

    /**
     * 受注伝票の再計算を行う
     * 
     * @param settingDto 受注登録設定DTO
     */
    public void reCalculate(C0801W02SettingDto settingDto) {
        C0801W02AcptDlvryDto acptDlvryDto = settingDto.getAcptDlvryDto();
        List<C0801W02AcptDlvryDetailDto> detailList = settingDto.getDetailList();
        // 再計算時は計算区分対象日を受注日にする
        C0801W02ShopCalculateOptionDto calcOptionDto = createShopCalculateOptionDto(
            settingDto.getShopInfo(), acptDlvryDto.getDlvryytidd(), acptDlvryDto.getAcptdd());
        C0801W02AcptRelationDto relationDto = finder.createAcptRelationDto(acptDlvryDto);
        // 明細の単価再設定および金額計算
        detailList.stream().forEach((detailDto) -> {
            // 計算オプションを明細に設定
            detailDto.setCalculateOptionDto(calcOptionDto);
            // 受注関係DTOを迷彩に設定
            detailDto.setAcptRelation(relationDto.copyWith(detailDto));
            // 軽減税率施行日前は税区分を軽減から課税に変更
            if (CommonConstants.KBN_TAX_KEIGEN.equals(detailDto.getTaxkbn())
                && !util.isLater(acptDlvryDto.getDlvryytidd(),
                    C0801W02ShopCalculateOptionDto.REDUCED_TAX_RATE_STRDD)) {
                detailDto.setTaxkbn(CommonConstants.KBN_TAX_KAZEI);
            }
            // 受注単価再設定
            resetAcptunt(acptDlvryDto, detailDto);
            // 明細の受注金額再計算
            priceService.calculateAcptkin(acptDlvryDto.getDlvryytidd(), detailDto);
        });
        // 受注伝票の金額再計算
        priceService.calculateOrderSlip(acptDlvryDto, detailList, calcOptionDto);
        return;
    }

    /**
     * 明細単価再設定
     * 
     * @param acptDlvryDto 受注配達DTO
     * @param detailDto 受注配達明細DTO
     */
    protected void resetAcptunt(C0801W02AcptDlvryDto acptDlvryDto,
        C0801W02AcptDlvryDetailDto detailDto) {
        C0801W02AcptRelationDto relationDto = detailDto.getAcptRelation();
        String dlvrykbn = acptDlvryDto.getDlvrykbn();
        // 計上区分が無償の場合（売上、キャンペーン、経費以外）は単価を0円に設定
        if (relationDto.isMusho()) {
            detailDto.setAcptunt(BigDecimal.ZERO.stripTrailingZeros().toPlainString());
            return;
        }
        // 計上区分がキャンペーンかつ販促区分がOFFの場合は単価を0円に設定
        if (relationDto.isCampaign() && !util.isFlagged(detailDto.getHansokukbn())) {
            detailDto.setAcptunt(BigDecimal.ZERO.stripTrailingZeros().toPlainString());
            return;
        }
        // 受注単価の再設定は顧客、NA受託、配達受託の得意先のみ
        if (!acptDlvryDto.isAllowedResetAcptunt()) {
            return;
        }
        // 価格区分が標準価格の場合のみ再設定
        if (!CommonConstants.KBN_PRICE_HYOJUN.equals(detailDto.getPricekbn())) {
            return;
        }
        // 計上区分が「売上」または「キャンペーン」の場合のみ再設定
        if (!relationDto.isYuryo()) {
            return;
        }
        // 受注単価が未設定または0の場合のみ再設定
        if (!util.isZeroString(detailDto.getAcptunt())) {
            return;
        }
        // ショップ価格情報、販促価格情報、得意先特別単価情報の対象日を引数で指定された日付に変更
        detailDto.getShopShnPriceDto().setTargetDate(acptDlvryDto.getDlvryytidd());
        detailDto.getTokubetsuShnPriceDto().setTargetDate(acptDlvryDto.getDlvryytidd());
        detailDto.getHansokuShnPriceDto().setTargetDate(acptDlvryDto.getDlvryytidd());
        // 販促単価を取得
        Optional<String> campaignUnt =
            detailDto.getHansokuShnPriceDto(true).map(dto -> dto.getPriceByDlvrykbn(dlvrykbn));
        // 特別単価を取得
        Optional<String> tokuisakiUnt =
            detailDto.getTokubetsuShnPriceDto(true).map(dto -> dto.getPriceByDlvrykbn(dlvrykbn));
        // 計上区分が売上かつ特別単価が取得できた場合は設定
        if (relationDto.isUriage() && tokuisakiUnt.isPresent()) {
            detailDto.setAcptunt(tokuisakiUnt.get());
            return;
        }
        // 計上区分がキャンペーンの場合
        if (relationDto.isCampaign()) {
            // 販促単価が取得できていればそれを、そうでない場合は0円を設定
            detailDto.setAcptunt(
                campaignUnt.orElse(BigDecimal.ZERO.stripTrailingZeros().toPlainString()));
            return;
        }
        // 販促単価、特別単価のいずれも取得できない場合
        if (!campaignUnt.isPresent() && !tokuisakiUnt.isPresent()) {
            // ショップ価格（実質は標準価格）を適用
            detailDto.setAcptunt(detailDto.getShopShnPriceDto().getPriceByDlvrykbn(dlvrykbn));
        }
    }

    /**
     * 受注配達明細DTOを取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param acptdnpyno 受注伝票番号
     * @param edano 枝番号
     * @param acptdnpymeisaino 受注伝票明細番号
     * @param acptdnpymeisaiedano 受注伝票明細枝番号
     * @param isLockRow 行ロックを取得するか
     * @return 受注配達明細DTO
     */
    public C0801W02AcptDlvryDetailDto getDetailDto(String apkihoncd, String shopcd,
        String acptdnpyno, String edano, String acptdnpymeisaino, String acptdnpymeisaiedano,
        boolean isLockRow) {
        // 受注配達明細テーブルを主キーで検索する
        S04T002TEntityKey key = new S04T002TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setAcptdnpyno(acptdnpyno);
        key.setEdano(Short.parseShort(edano));
        key.setAcptdnpymeisaino(Integer.parseInt(acptdnpymeisaino));
        key.setAcptdnpymeisaiedano(Short.parseShort(acptdnpymeisaiedano));
        Optional<S04T002TEntity> entityOptional =
            Optional.ofNullable(isLockRow ? s04t002tDao.selectByPrimaryKeyForLock(key)
                : s04t002tDao.selectByPrimaryKey(key));
        // EntityをDTOに変換して返す
        return entityOptional.map(e -> C0801W02AcptDlvryDetailDto.create(e))
            // データが見つからない場合は無効データを返す
            .orElseGet(() -> C0801W02AcptDlvryDetailDto.builder()
                // AP基本コード
                .apkihoncd(apkihoncd)
                // ショップコード
                .shopcd(shopcd)
                // 受注伝票番号
                .acptdnpyno(acptdnpyno)
                // 枝番号
                .edano(edano)
                // 受注伝票明細番号
                .acptdnpymeisaino(acptdnpymeisaino)
                // 受注伝票明細枝番号
                .acptdnpymeisaiedano(acptdnpymeisaiedano)
                // 有効区分（0：無効）
                .yukokbn(CommonConstants.YUKOKBN_0).build());
    }

    /**
     * 受注配達明細DTOをマージする.<br>
     * <br>
     * マージする際に元となる受注配達明細リストから数量が未設定または0の明細を除外する
     * 
     * @param base 元となる受注配達明細DTOリスト
     * @param another 追加する受注配達明細DTOリスト
     * @return マージされた受注配達明細DTOリスト
     */
    public List<C0801W02AcptDlvryDetailDto> mergeList(List<C0801W02AcptDlvryDetailDto> base,
        List<C0801W02AcptDlvryDetailDto> another) {
        return util.mergeList(base, Optional.ofNullable(DETAIL_MERGE_FILTER), another,
            Optional.empty(), Optional.empty());
    }

    /**
     * 指定された受注配達明細DTOリストから数量が未設定または0の明細を除去して返す
     * 
     * @param origin 元となる受注配達明細DTOリスト
     * @return 受注配達明細DTOリスト
     */
    public List<C0801W02AcptDlvryDetailDto> omitList(List<C0801W02AcptDlvryDetailDto> origin) {
        return origin.stream().filter(DETAIL_MERGE_FILTER).collect(util.toList());
    }

    /**
     * 基準となる受注配達明細DTOリストに別の受注配達明細DTOリストを挿入する.
     * 
     * @param base 基準となる受注配達明細DTOリスト
     * @param another 挿入する受注配達明細DTOリスト
     * @param idx 挿入する位置
     * @return 受注配達明細DTOリスト
     */
    public List<C0801W02AcptDlvryDetailDto> insertDtoToList(List<C0801W02AcptDlvryDetailDto> base,
        List<C0801W02AcptDlvryDetailDto> another, int idx) {

        // 結果格納用のリスト
        List<C0801W02AcptDlvryDetailDto> result = new LinkedList<>();
        result.addAll(base);
        // 挿入位置が負数または元のリストの要素数を超える場合は最後尾に追加
        idx = idx < 0 || idx >= base.size() ? base.size() : idx;
        result.addAll(idx, another);

        // 結果リストにフィルタを適用して返す
        return result.stream().filter(INSERT_ROW_FILTER).collect(util.toList());
    }

    /**
     * 受注配達明細DTOリストの指定された位置に受注配達明細DTOを追加する.
     * 
     * @param base 受注配達明細DTOリスト
     * @param detailDto 受注配達明細DTO
     * @param idx 追加する位置
     * @return 受注配達明細リスト
     */
    public List<C0801W02AcptDlvryDetailDto> appendDtoToList(List<C0801W02AcptDlvryDetailDto> base,
        C0801W02AcptDlvryDetailDto detailDto, int idx) {
        // 結果格納用のリスト
        List<C0801W02AcptDlvryDetailDto> result = new LinkedList<>();
        result.addAll(base);
        // 追加位置が負数または元のリストの要素数を超える場合は最後尾に追加
        // それ以外ならば指定された行の次の行に追加するのでインクリメント
        idx = idx < 0 || idx >= base.size() ? base.size() : ++idx;
        result.add(idx, detailDto);
        // 結果リストにフィルタを適用して返す
        return result.stream().filter(APPEND_ROW_FILTER).collect(util.toList());
    }

    /**
     * 指定された受注配達明細DTOのコピーを取得する.
     * 
     * @param base 受注配達明細DTO
     * @return 受注配達明細DTO
     */
    public C0801W02AcptDlvryDetailDto copyDetail(C0801W02AcptDlvryDetailDto base) {
        return base.toBuilder()
            // タイプ
            .type(DETAIL_TYPE.COPY).build();
    }

    /**
     * 受注配達明細データを取得する.
     * 
     * @param settingDto 受注登録設定DTO
     * @return 受注配達明細データリスト
     */
    public List<C0801W02AcptDlvryDetailDto> getAcptDlvryDetailList(C0801W02SettingDto settingDto) {
        return getAcptDlvryDetailList(settingDto.getAcptDlvryDto(), settingDto.getShopInfo(),
            settingDto.getOption());
    }

    /**
     * 受注配達明細DTOリストを取得する
     * 
     * @param acptDlvryDto 受注配達DTO
     * @param shopDto ショップマスタDTO
     * @param option 受注登録設定オプション
     * @return 受注配達明細DTOリスト
     */
    public List<C0801W02AcptDlvryDetailDto> getAcptDlvryDetailList(
        C0801W02AcptDlvryDto acptDlvryDto, C0801W02ShopMasterDto shopDto,
        C0801W02SettingOptionDto option) {
        // 受注単価再設定を行うかどうかを示すフラグを設定
        // 処理区分が新規かつ更新処理ではない場合は再設定
        boolean isSetPrice =
            C0801W02SettingDto.PRCSKBN.INSERT == option.getPrcskbn() && !option.isRegist();
        // ショップ計算オプションを生成
        C0801W02ShopCalculateOptionDto calcOptionDto =
            createShopCalculateOptionDto(shopDto, acptDlvryDto.getBaseDate());
        // 受注関係情報を生成
        C0801W02AcptRelationDto relationDto = finder.createAcptRelationDto(acptDlvryDto);
        return util.convertList(
            // データベースから受注配達明細リストを取得
            loadAcptDlvryDetailList(acptDlvryDto, !option.isRegist()),
            // 項目編集
            detailDto -> {
                // 明細単価再編集
                detailDto = reEditUnt(isSetPrice, acptDlvryDto.getDlvrykbn(),
                    acptDlvryDto.getBaseDate(), detailDto, calcOptionDto, relationDto);
                // 商品・入力行のみ
                if (util.isFlagged(detailDto.getNyuryokumeisaiflg())
                    && CommonConstants.KBN_MEISAI_SHOHIN.equals(detailDto.getMeisaikbn())) {
                    // 特別単価リストを設定
                    detailDto.setTokubetsuUntList(
                        tokuisakiService.getTokubetuUnt(acptDlvryDto.getApkihoncd(),
                            acptDlvryDto.getShopcd(), acptDlvryDto.getTokuisakicd(),
                            acptDlvryDto.getDlvryytidd(), detailDto.getShncd(),
                            detailDto.getRknkindtokubetsuuntflg(), detailDto.getTanpinmeisaiflg()));
                }
                return detailDto;
            });
    }

    /**
     * 前回受注配達明細データを取得する.
     * 
     * @param settingDto 受注登録設定DTO
     * @return 受注配達明細データリスト
     */
    public List<C0801W02AcptDlvryDetailDto> getPreviousAcptDlvryDetailList(
        C0801W02SettingDto settingDto) {
        // 受注配達データに対して前回受注配達データの伝票番号と枝番号を設定
        C0801W02AcptDlvryDto acptDlvryDto = settingDto.getAcptDlvryDto().toBuilder()
            // 受注伝票番号
            .acptdnpyno(settingDto.getPreviousAcptDlvryDto().getAcptdnpyno())
            // 枝番号
            .edano(settingDto.getPreviousAcptDlvryDto().getEdano())
            // クローンを生成する（※受注登録設定DTO内のデータを更新するとセッションも変わってしまうため）
            .build();
        // ショップ計算オプションを生成
        C0801W02ShopCalculateOptionDto calcOptionDto =
            createShopCalculateOptionDto(settingDto.getShopInfo(), acptDlvryDto.getDlvryytidd());
        // 受注関係情報を生成
        C0801W02AcptRelationDto relationDto = finder.createAcptRelationDto(acptDlvryDto);
        return util.convertList(
            // データベースから受注配達明細リストを取得
            loadAcptDlvryDetailList(acptDlvryDto, !settingDto.getOption().isRegist())
                // データ作成元区分が06（定例売上）の明細は除外する
                .stream().filter(detailDto -> !Objects.equals("06", detailDto.getDatsksmotokbn())),
            // 項目編集
            detailDto -> {
                // 納品数量を受注数量に設定
                detailDto.setAcptsuryo(detailDto.getNohinsuryo());
                // 納品関連項目をクリア
                detailDto.setNohinsuryo(null);
                detailDto.setNohinkin(null);
                detailDto.setNohinunt(null);
                detailDto.setNohinuntshohitaxkin(null);
                detailDto.setNohinshohitaxkin(null);
                // デフォルトプロパティの設定
                detailDto = setDefaultPropertiesToBlankDetail(detailDto, acptDlvryDto,
                    settingDto.getTokuisakiDlvryDto(), calcOptionDto, relationDto);
                // 前回情報から委託相手関連項目はクリアする
                detailDto.setItakuaiteapkihoncd(null);
                detailDto.setItakuaiteshopcd(null);
                detailDto.setItakuaiteacptdnpyno(null);
                detailDto.setItakuaiteedano(null);
                detailDto.setItakuaiteacptdnpymeisaino(null);
                detailDto.setItakuaiteacptdnpymeisaiedano(null);
                return detailDto;
            });
    }

    /**
     * 指定された商品の受注配達明細リストを取得する.
     * 
     * @param settingDto 受注登録設定DTO
     * @param shncd 商品コード
     * @return 受注配達明細DTOリスト
     */
    public List<C0801W02AcptDlvryDetailDto> getShnDetailList(C0801W02SettingDto settingDto,
        String shncd) {

        // 受注配達データを取得
        C0801W02AcptDlvryDto acptDlvryDto = settingDto.getAcptDlvryDto();
        // ショップ計算オプションを生成
        C0801W02ShopCalculateOptionDto calcOptionDto =
            createShopCalculateOptionDto(settingDto.getShopInfo(), acptDlvryDto.getDlvryytidd());
        // 受注関係情報を生成
        if (!acptDlvryDto.isLoaded()) {
            acptDlvryDto.setTokuisakikbn(settingDto.getTokuisakiInfo().getTokuisakikbn());
        }
        C0801W02AcptRelationDto relationDto = finder.createAcptRelationDto(acptDlvryDto);
        return util.convertList(
            // データベースから商品の受注配達明細リストを取得
            loadShnDetailList(acptDlvryDto, shncd),
            // デフォルトプロパティの設定
            detailDto -> setDefaultPropertiesToBlankDetail(detailDto, acptDlvryDto,
                settingDto.getTokuisakiDlvryDto(), calcOptionDto, relationDto));
    }

    /**
     * 設置サーバの受注配達明細を取得する.
     * 
     * @param settingDto 受注登録設定DTO
     * @return 受注配達明細DTOリスト
     */
    public List<C0801W02AcptDlvryDetailDto> getInstalledServerDetailList(
        C0801W02SettingDto settingDto) {

        // 受注配達データを取得
        C0801W02AcptDlvryDto acptDlvryDto = settingDto.getAcptDlvryDto();

        // ショップ計算オプションを生成
        C0801W02ShopCalculateOptionDto calcOptionDto =
            createShopCalculateOptionDto(settingDto.getShopInfo(), acptDlvryDto.getDlvryytidd());

        // 受注関係情報を生成
        if (!acptDlvryDto.isLoaded()) {
            acptDlvryDto.setTokuisakikbn(settingDto.getTokuisakiInfo().getTokuisakikbn());
        }
        C0801W02AcptRelationDto relationDto = finder.createAcptRelationDto(acptDlvryDto);

        // 得意先設置サーバ管理マスタ等から必要な情報を取得しDTOのリストを生成
        return loadInstalledServerDetailList(acptDlvryDto, settingDto.getTokuisakiInfo()).stream()
            .map(detailDto -> {
                // 親伝票の内容を反映
                detailDto.setApkihoncd(acptDlvryDto.getApkihoncd());
                detailDto.setShopcd(acptDlvryDto.getShopcd());
                detailDto.setAcptdnpyno(acptDlvryDto.getAcptdnpyno());
                detailDto.setEdano(acptDlvryDto.getEdano());
                // 明細単価再編集
                detailDto = reEditUnt(true, acptDlvryDto.getDlvrykbn(),
                    acptDlvryDto.getDlvryytidd(), detailDto, calcOptionDto, relationDto);
                // 特別単価設定
                detailDto.setTokubetsuUntList(
                    tokuisakiService.getTokubetuUnt(acptDlvryDto.getApkihoncd(),
                        acptDlvryDto.getShopcd(), acptDlvryDto.getTokuisakicd(),
                        acptDlvryDto.getDlvryytidd(), detailDto.getShncd(),
                        detailDto.getRknkindtokubetsuuntflg(), detailDto.getTanpinmeisaiflg()));
                return detailDto;
            })
            // 取得したサーバ情報件数が上限を超えている場合は、超過分を切り捨てる
            .limit(999)
            // Stream→List
            .collect(util.toList());
    }

    /**
     * お気に入り商品の受注配達明細を取得する.
     * 
     * @param settingDto 受注登録設定DTO
     * @return 受注配達明細DTOリスト
     */
    public List<C0801W02AcptDlvryDetailDto> getFavoriteShnDetailList(
        C0801W02SettingDto settingDto) {

        // 受注配達データを取得
        C0801W02AcptDlvryDto acptDlvryDto = settingDto.getAcptDlvryDto();
        // ショップ計算オプションを生成
        C0801W02ShopCalculateOptionDto calcOptonDto =
            createShopCalculateOptionDto(settingDto.getShopInfo(), acptDlvryDto.getDlvryytidd());
        // 受注関係情報を生成
        if (!acptDlvryDto.isLoaded()) {
            acptDlvryDto.setTokuisakikbn(settingDto.getTokuisakiInfo().getTokuisakikbn());
        }
        C0801W02AcptRelationDto relationDto = finder.createAcptRelationDto(acptDlvryDto);
        // 検索を実行し、デフォルトプロパティを設定してから返却する
        return util.convertList(
            // データベースからお気に入り商品の受注配達明細リストを取得
            loadFavoriteShnDetailList(acptDlvryDto),
            // デフォルトプロパティの設定
            detailDto -> setDefaultPropertiesToBlankDetail(detailDto, acptDlvryDto,
                settingDto.getTokuisakiDlvryDto(), calcOptonDto, relationDto));
    }

    /**
     * 受注配達明細の単価再編集.
     * 
     * @param isSetPrice 受注単価の再設定を行うかどうか
     * @param dlvrykbn 配達区分
     * @param dlvryytidd 配達予定日
     * @param detailDto 受注配達明細データ
     * @param calcOptionDto ショップ計算オプション
     * @param relationDto 受注関係情報
     * @return 受注配達明細DTO
     */
    public C0801W02AcptDlvryDetailDto reEditUnt(boolean isSetPrice, String dlvrykbn,
        String dlvryytidd, C0801W02AcptDlvryDetailDto detailDto,
        C0801W02ShopCalculateOptionDto calcOptionDto, C0801W02AcptRelationDto relationDto) {

        // 受注明細データに受注関係情報（一部項目は明細の内容を反映）を設定
        detailDto.setAcptRelation(relationDto.copyWith(detailDto));
        // 受注明細データにショップ計算オプションを設定
        detailDto.setCalculateOptionDto(calcOptionDto);

        // 対象は入力・商品行のみ
        if (util.isFlagged(detailDto.getNyuryokumeisaiflg())
            && CommonConstants.KBN_MEISAI_SHOHIN.equals(detailDto.getMeisaikbn())) {
            // 標準単価設定
            detailDto.getHyojunShnPriceDto().setTargetDate(dlvryytidd);
            detailDto.setHyojununt(detailDto.getHyojunShnPriceDto().getPriceByDlvrykbn(dlvrykbn));
            // 受注単価を設定するかにより分岐
            if (isSetPrice) {
                // 受注単価設定
                setCurrentAcptUnt(dlvrykbn, dlvryytidd, detailDto);
            } else if (DETAIL_TYPE.LOADED == detailDto.getType()) {
                // 配達予定日時点の受注単価を取得
                String acptunt = getAcptUntForSpecifiedDate(dlvrykbn, dlvryytidd, detailDto);
                // 明細の受注単価と取得した受注単価を比較し、
                // 以前の受注登録で受注単価を手入力により変更しているかをフラグとして設定
                detailDto.setUntChangedBefore(!Objects.equals(acptunt, detailDto.getAcptunt()));
            }
            // 初期表示価格設定
            detailDto.setShokidsplyprice(detailDto.getAcptunt());
        }
        return detailDto;
    }

    /**
     * 商品の受注配達明細リスト取得.
     * 
     * @param acptDlvryDto 受注配達DTO
     * @param shncd 商品コード
     * @return 受注配達明細DTOリスト
     */
    protected List<C0801W02AcptDlvryDetailDto> loadShnDetailList(C0801W02AcptDlvryDto acptDlvryDto,
        String shncd) {
        // 検索条件
        S02V001TC0802W01Entity condition = new S02V001TC0802W01Entity();
        condition.setApkihoncd(acptDlvryDto.getApkihoncd());
        condition.setShopcd(acptDlvryDto.getShopcd());
        condition.setTokuisakicd(acptDlvryDto.getTokuisakicd());
        condition.setDlvryytidd(acptDlvryDto.getDlvryytidd());
        condition.setShncd(shncd);

        // 検索を実行し受注配達明細DTOに変換してリストを返す
        return util.convertList(s02v001tDao.selectByC0802W01(condition),
            e -> C0801W02AcptDlvryDetailDto.create(e, acptDlvryDto.getDlvryytidd()));
    }

    /**
     * サーバ関連の受注配達明細リスト取得.
     * 
     * @param acptDlvryDto 受注配達DTO
     * @param tokuisakiDto 得意先マスタDTO
     * @return 受注配達明細DTOリスト
     */
    protected List<C0801W02AcptDlvryDetailDto> loadInstalledServerDetailList(
        C0801W02AcptDlvryDto acptDlvryDto, C0801W02TokuisakiMasterDto tokuisakiDto) {

        // 得意先設置サーバ管理マスタの検索条件
        S02M048TgetInstalledServerDetailListCondition condition =
            S02M048TgetInstalledServerDetailListCondition.builder()
                // AP基本コード
                .apkihoncd(acptDlvryDto.getApkihoncd())
                // ショップコード
                .shopcd(acptDlvryDto.getShopcd())
                // 得意先コード
                .tokuisakicd(acptDlvryDto.getTokuisakicd())
                // 得意先が委受託かどうか
                .ijutaku(tokuisakiDto.isIjutaku())
                // 配達予定日
                .dlvryytidd(acptDlvryDto.getDlvryytidd())
                // 委託相手AP基本コード
                .itakuaiteapkihoncd(tokuisakiDto.getItakuaiteapkihoncd())
                // 委託相手ショップコード
                .itakuaiteshopcd(tokuisakiDto.getItakuaiteshopcd())
                // 委託相手得意先コード
                .itakuaitetokuisakicd(tokuisakiDto.getItakuaitetokuisakicd())
                // Entityを構築
                .build();
        // 検索実行
        return util.convertList(s02m048tDao.selectInstalledSereverDetailList(condition), e -> {
            C0801W02AcptDlvryDetailDto detailDto =
                C0801W02AcptDlvryDetailDto.create(e, acptDlvryDto.getDlvryytidd());
            // 計上区分（得意先区分により設定）
            detailDto.setKeijokbn(
                CommonConstants.KBN_TOKUISAKI_SHOP.equals(tokuisakiDto.getTokuisakikbn())
                    ? CommonConstants.KBN_KEIJO_SUBRENTAL
                    : CommonConstants.KBN_KEIJO_RENTAL);
            // 売上取扱単位（システム管理マスタから名称を取得して設定）
            detailDto.setUritrtskitani(sysMngService.getSettingValue(CommonConstants.APCD_000,
                CommonConstants.SYS_SHOPCD_000, CommonConstants.SYS_SETTEINO_013,
                detailDto.getUritrtskitani()));
            return detailDto;
        });
    }

    /**
     * お気に入り商品の受注配達明細リスト取得.
     * 
     * @param acptDlvryDto 受注配達DTO
     * @return 受注配達明細DTOリスト
     */
    protected List<C0801W02AcptDlvryDetailDto> loadFavoriteShnDetailList(
        C0801W02AcptDlvryDto acptDlvryDto) {

        // 検索条件編集
        S02M044TC0802W01Entity condition = new S02M044TC0802W01Entity();
        condition.setApkihoncd(acptDlvryDto.getApkihoncd());
        condition.setShopcd(acptDlvryDto.getShopcd());
        condition.setTokuisakicd(acptDlvryDto.getTokuisakicd());
        condition.setDlvryytidd(acptDlvryDto.getDlvryytidd());
        // 受注区分の値に1加えたのがタイプになる
        condition.setType(acptDlvryDto.getDecimalValue(acptDlvryDto.getAcptkbn())
            .add(BigDecimal.ONE).stripTrailingZeros().toPlainString());
        // 検索実行
        return util.convertList(s02m044tDao.selectByC0802W01(condition),
            e -> C0801W02AcptDlvryDetailDto.create(e, acptDlvryDto.getDlvryytidd()));
    }

    /**
     * 受注配達明細取得.
     * 
     * @param acptDlvryDto 受注配達DTO
     * @return 受注配達明細DTOリスト
     */
    protected List<C0801W02AcptDlvryDetailDto> loadAcptDlvryDetailList(
        C0801W02AcptDlvryDto acptDlvryDto, boolean onlyShnDetail) {

        // 検索条件を編集
        S04T002TC0805W01Entity searchCondition = new S04T002TC0805W01Entity();
        // 価格取得基準日（配達日または配達予定日）
        searchCondition.setKakakugetbasedd(acptDlvryDto.getBaseDate());
        // AP基本コード
        searchCondition.setApkihoncd(acptDlvryDto.getApkihoncd());
        // ショップコード
        searchCondition.setShopcd(acptDlvryDto.getShopcd());
        // 受注伝票番号
        searchCondition.setAcptdnpyno(acptDlvryDto.getAcptdnpyno());
        // 枝番号
        searchCondition.setEdano(Short.parseShort(acptDlvryDto.getEdano()));

        // 検索を実行し、受注配達明細データを生成、リストとして返す
        // データアクセス仕様:S04T002T_受注配達明細テーブル 1_受注配達伝票明細データ取得
        // 入力行以外、配布物を除外するかは引数で指定されたbooleanも条件に含める
        return s04t002tDao.selectByC0805W01getJuchuMeisai(searchCondition).stream()
            // 入力行のみ
            .filter(entity -> !onlyShnDetail || util.isFlagged(entity.getNyuryokumeisaiflg()))
            // 配布物は除外
            .filter(entity -> !onlyShnDetail || Objects.isNull(entity.getHaifubutsuapkihoncd()))
            // EntityからDTOに変換
            .map(e -> C0801W02AcptDlvryDetailDto.create(e, acptDlvryDto.getBaseDate()))
            // StreamからListに変換
            .collect(util.toList());
    }

    /**
     * 受注単価設定.
     * 
     * @param dlvrykbn 配達区分
     * @param dlvryytidd 配達予定日
     * @param detailDto 受注配達明細データ
     */
    public void setCurrentAcptUnt(String dlvrykbn, String dlvryytidd,
        C0801W02AcptDlvryDetailDto detailDto) {

        // 計上区分により設定する
        C0801W02AcptRelationDto relationDto = detailDto.getAcptRelation();

        // ショップ価格情報、販促価格情報、得意先特別単価情報の対象日を引数で指定された日付に変更
        detailDto.getShopShnPriceDto().setTargetDate(dlvryytidd);
        detailDto.getTokubetsuShnPriceDto().setTargetDate(dlvryytidd);
        detailDto.getHansokuShnPriceDto().setTargetDate(dlvryytidd);

        if (relationDto.isKeihiKeijo()) {
            // 計上区分＝「経費」の時はショップ商品マスタの在庫単価を受注単価に設定
            detailDto.setAcptunt(Objects.requireNonNullElse(detailDto.getZaikount(),
                BigDecimal.ZERO.stripTrailingZeros().stripTrailingZeros().toPlainString()));
        } else if (relationDto.isYuryo()) {
            // 計上区分＝「売上」または「キャンペーン」の時は得意先別商品別、販促単価の適用判定を行う
            // 販促単価、得意先別商品別単価を取得(対象外のときはempty)
            Optional<String> campaignUnt =
                detailDto.getHansokuShnPriceDto(true).map(dto -> dto.getPriceByDlvrykbn(dlvrykbn));
            Optional<String> tokuisakiUnt = detailDto.getTokubetsuShnPriceDto(true)
                .map(dto -> dto.getPriceByDlvrykbn(dlvrykbn));

            if (tokuisakiUnt.isPresent()) {
                // 得意先別商品別単価の適用期間内の場合は受注単価を得意先別商品別単価にする
                detailDto.setKeijokbn(CommonConstants.KBN_KEIJO_URIAGE);
                detailDto.setAcptunt(tokuisakiUnt.get());
            } else {
                // 計上区分がキャンペーン（お気に入りマスタからの登録）の場合は単価のみ設定
                if (relationDto.isCampaign()) {
                    detailDto.setAcptunt(
                        campaignUnt.orElse(BigDecimal.ZERO.stripTrailingZeros().toPlainString()));
                } else {
                    // 行挿入や行追加などの計上区分：売上の場合、販促設定適用範囲内ならキャンペーンに設定する
                    if (campaignUnt.isPresent()) {
                        // 販促期間内＋販促設定：対象内
                        if (util.isFlagged(detailDto.getHansokuyusenkbn())) {
                            // 販促優先区分＝"する"の場合は計上区分をキャンペーン、販促価格に設定
                            detailDto.setKeijokbn(CommonConstants.KBN_KEIJO_CAMPAIGN);
                            detailDto.setAcptunt(campaignUnt.get());
                        } else {
                            // 販促優先区分＝"しない"の場合、売上価格のみ設定
                            detailDto.setAcptunt(
                                detailDto.getShopShnPriceDto().getPriceByDlvrykbn(dlvrykbn));
                        }
                    } else {
                        // 販促期間外＋販促設定：対象外 売上価格を設定
                        detailDto.setAcptunt(
                            detailDto.getShopShnPriceDto().getPriceByDlvrykbn(dlvrykbn));
                    }
                }
            }

        } else {
            // 計上区分が上記以外の場合
            detailDto.setAcptunt(BigDecimal.ZERO.stripTrailingZeros().toPlainString());
        }

        // 受注金額計算
        priceService.calculateAcptkin(dlvryytidd, detailDto);

    }

    /**
     * 指定された日付（配達予定日）の受注単価を取得する
     * 
     * @param dlvrykbn 配達区分
     * @param dlvryytidd 配達予定日
     * @param detailDto 受注配達明細DTO
     * @return 受注単価
     */
    private String getAcptUntForSpecifiedDate(String dlvrykbn, String dlvryytidd,
        C0801W02AcptDlvryDetailDto detailDto) {
        // 計上区分により設定する
        C0801W02AcptRelationDto relationDto = detailDto.getAcptRelation();
        if (relationDto.isKeihiKeijo()) {
            // 経費の場合 … 在庫単価
            return Objects.requireNonNullElse(detailDto.getZaikount(),
                BigDecimal.ZERO.stripTrailingZeros().stripTrailingZeros().toPlainString());
        } else if (relationDto.isYuryo()) {
            // 計上区分＝「売上」または「キャンペーン」の時は得意先別商品別、販促単価の適用判定を行う
            Optional<String> campaignUnt =
                detailDto.getHansokuShnPriceDto(true).map(dto -> dto.getPriceByDlvrykbn(dlvrykbn));
            Optional<String> tokuisakiUnt = detailDto.getTokubetsuShnPriceDto(true)
                .map(dto -> dto.getPriceByDlvrykbn(dlvrykbn));
            if (tokuisakiUnt.isPresent()) {
                // 特別単価適用可能 … 特別単価
                return tokuisakiUnt.get();
            } else {
                // 特別単価適用対象外の場合
                if (relationDto.isCampaign()) {
                    // 計上区分がキャンペーン … 販促単価
                    return campaignUnt.orElse(BigDecimal.ZERO.stripTrailingZeros().toPlainString());
                } else {
                    // 計上区分が売上の場合
                    if (campaignUnt.isPresent()) {
                        // キャンペーン単価が適用可能な場合
                        if (util.isFlagged(detailDto.getHansokuyusenkbn())) {
                            // 販促優先区分がON … 販促単価
                            return campaignUnt.get();
                        }
                        // 販促優先区分がOFF … ショップ単価
                        return detailDto.getShopShnPriceDto().getPriceByDlvrykbn(dlvrykbn);
                    }
                    // キャンペーン単価が適用されない場合 … ショップ単価
                    return detailDto.getShopShnPriceDto().getPriceByDlvrykbn(dlvrykbn);
                }
            }
        }
        // 上記以外の計上区分の場合は0円
        return BigDecimal.ZERO.stripTrailingZeros().toPlainString();
    }

    /**
     * ショップ情報の内容を元にショップ計算オプションを生成する.
     * 
     * @param shopDto ショップ情報
     * @param taxrateTargetDate 消費税率対象日
     * @param taxkeisankbnTargetDate 計算方法対象日
     * @return ショップ計算オプション
     */
    public C0801W02ShopCalculateOptionDto createShopCalculateOptionDto(
        C0801W02ShopMasterDto shopDto, String taxrateTargetDate, String taxkeisankbnTargetDate) {
        return C0801W02ShopCalculateOptionDto.builder()
            // インボイス施行日
            .invoicshikoymd(shopDto.getInvoicshikoymd())
            // 現消費税率
            .genzshohitaxrate(shopDto.getGenzshohitax())
            // 新消費税率
            .shinshohitaxrate(shopDto.getShinshohitax())
            // 消費税切替日
            .shohitaxkirikaedd(shopDto.getShohitaxkirikaedd())
            // 現計算区分
            .genzkeisankbn(shopDto.getGenztaxkeisankbnuri())
            // 新計算区分
            .shinkeisankbn(shopDto.getShintaxkeisankbnuri())
            // 税計算区分切替日
            .taxkeisankbnurikirikaedd(shopDto.getTaxkeisankbnurikirikaedd())
            // 税区分
            .taxkbn(CommonConstants.KBN_TAX_KAZEI)
            // 税端数計算区分
            .taxhasukeisankbn(shopDto.getTaxhasukeisankbn())
            // 売上金額端数計算区分
            .urikinhasukeisankbn(shopDto.getUrikinhasukeisankbn())
            // 計算方法
            .keisanhoho(CommonConstants.KBN_KEISANHOHO_MEISAI)
            // 対象日
            .targetDate(taxrateTargetDate)
            // 計算区分対象日
            .taxkeisankbnTargetDate(taxkeisankbnTargetDate)
            // DTOを構築
            .build();
    }

    /**
     * 調整消費税の受注配達明細DTOを生成する
     * 
     * @param acptDlvryDto 受注配達DTO
     * @param detailDto セット商品の受注配達明細DTO
     * @param bundleList 展開された構成商品の受注配達明細DTOリスト
     * @param diff 消費税金額の差分
     * @return 調整消費税の受注配達明細DTO
     */
    public C0801W02AcptDlvryDetailDto createadjustedTaxDetail(C0801W02AcptDlvryDto acptDlvryDto,
        C0801W02AcptDlvryDetailDto detailDto, List<C0801W02AcptDlvryDetailDto> bundleList,
        BigDecimal diff) {
        // 調整消費税の商品コードをシステム管理マスタから取得する
        String adjustedTaxShncd = sysMngService
            .getSettingList(CommonConstants.APCD_000, CommonConstants.SYS_SHOPCD_000,
                CommonConstants.SYS_SETTEINO_166, CommonConstants.KBN_RECORD_MEISAI, false, false)
            .stream().findFirst().map(C0801W02SysMngDto::getSetteiatai).get();
        // 構成商品の受注配達明細DTOリストの最後の要素を取得
        C0801W02AcptDlvryDetailDto lastBundleDetail = bundleList.get(bundleList.size() - 1);
        // 調整消費税の商品情報を取得
        C0801W02ShnDto adjustedTaxShnDto = shnService.getShnInfo(acptDlvryDto.getApkihoncd(),
            acptDlvryDto.getShopcd(), adjustedTaxShncd);
        // 親を元に調整消費税明細生成
        C0801W02AcptDlvryDetailDto adjustedTaxDetail = detailDto.toBuilder()
            // 受注伝票明細枝番号
            .acptdnpymeisaiedano(util.getString(
                lastBundleDetail.getIntValue(lastBundleDetail.getAcptdnpymeisaiedano()) + 1))
            // 商品コード
            .shncd(adjustedTaxShncd)
            // 型式
            .katasiki(null)
            // セット商品コード
            .stshncd(detailDto.getShncd())
            // 入力明細フラグ
            .nyuryokumeisaiflg(CommonConstants.FLG_OFF)
            // 単品明細フラグ
            .tanpinmeisaiflg(CommonConstants.FLG_ON)
            // 明細区分
            .meisaikbn(CommonConstants.KBN_MEISAI_SHOHIZEI)
            // 有償区分
            .yusyokbn(CommonConstants.KBN_YUSHO_NONE)
            // 受注数量
            .acptsuryo(BigDecimal.ONE.stripTrailingZeros().toPlainString())
            // 納品数量
            .nohinsuryo(ZERO).build();
        // 委託相手受注伝票明細番号
        if (!StringUtil.isEmpty(detailDto.getItakuaiteacptdnpymeisaino())) {
            adjustedTaxDetail.setItakuaiteacptdnpymeisaiedano(util.getString(
                lastBundleDetail.getIntValue(lastBundleDetail.getItakuaiteacptdnpymeisaiedano())
                    + 1));
        }
        // 商品情報を設定（税区分はセット商品の税区分にする）
        adjustedTaxShnDto.setTaxkbn(detailDto.getTaxkbn());
        // 調整消費税の商品情報を受注配達明細DTOに設定
        adjustedTaxDetail.setShnDto(adjustedTaxShnDto, acptDlvryDto.getDlvryytidd());
        // 標準単価
        adjustedTaxDetail.setHyojununt(adjustedTaxDetail.getHyojunShnPriceDto()
            .getPriceByDlvrykbn(acptDlvryDto.getDlvrykbn()));
        // 受注関係DTO
        adjustedTaxDetail.setAcptRelation(detailDto.getAcptRelation().copyWith(adjustedTaxDetail));
        // セット商品が有料の場合
        if (detailDto.getAcptRelation().isYuryo()) {
            // 受注金額関連
            adjustedTaxDetail.setAcptunt(ZERO);
            adjustedTaxDetail.setAcptuntshohitaxkin(ZERO);
            adjustedTaxDetail.setAcptshohitaxkin(diff.stripTrailingZeros().toPlainString());
            adjustedTaxDetail.setAcptkin(ZERO);
            // 納品金額関連
            adjustedTaxDetail.setNohinunt(ZERO);
            adjustedTaxDetail.setNohinuntshohitaxkin(ZERO);
            adjustedTaxDetail.setNohinshohitaxkin(ZERO);
            adjustedTaxDetail.setNohinkin(ZERO);
            // 計上区分
            adjustedTaxDetail.setKeijokbn(detailDto.getKeijokbn());
        }
        return adjustedTaxDetail;
    }

    /**
     * ショップ情報の内容を元にショップ計算オプションを生成する.
     * 
     * @param shopDto ショップ情報
     * @param targetDate 対象日
     * @return ショップ計算オプション
     */
    protected C0801W02ShopCalculateOptionDto createShopCalculateOptionDto(
        C0801W02ShopMasterDto shopDto, String targetDate) {
        return C0801W02ShopCalculateOptionDto.builder()
            // インボイス施行日
            .invoicshikoymd(shopDto.getInvoicshikoymd())
            // 現消費税率
            .genzshohitaxrate(shopDto.getGenzshohitax())
            // 新消費税率
            .shinshohitaxrate(shopDto.getShinshohitax())
            // 消費税切替日
            .shohitaxkirikaedd(shopDto.getShohitaxkirikaedd())
            // 現計算区分
            .genzkeisankbn(shopDto.getGenztaxkeisankbnuri())
            // 新計算区分
            .shinkeisankbn(shopDto.getShintaxkeisankbnuri())
            // 税計算区分切替日
            .taxkeisankbnurikirikaedd(shopDto.getTaxkeisankbnurikirikaedd())
            // 税区分
            .taxkbn(CommonConstants.KBN_TAX_KAZEI)
            // 税端数計算区分
            .taxhasukeisankbn(shopDto.getTaxhasukeisankbn())
            // 売上金額端数計算区分
            .urikinhasukeisankbn(shopDto.getUrikinhasukeisankbn())
            // 計算方法
            .keisanhoho(CommonConstants.KBN_KEISANHOHO_MEISAI)
            // 対象日
            .targetDate(targetDate)
            // 計算区分対象日
            .taxkeisankbnTargetDate(targetDate)
            // DTOを構築
            .build();
    }

    /**
     * 新規追加明細の計上区分を取得する.
     *
     * @param detailDto 受注配達明細DTO
     * @param tokuisakikbn 得意先区分
     * @param acptkbn 受注区分
     * @return 計上区分
     */
    protected String getNewDetailKeijokbn(C0801W02AcptDlvryDetailDto detailDto, String tokuisakikbn,
        String acptkbn) {

        // サーバ大分類、サーバ中分類を取得
        String serverlcd = shnService.getServerLcd();
        String servermcd = shnService.getServerMcd();

        if (CommonConstants.KBN_TOKUISAKI_KEIHI.equals(tokuisakikbn)) {
            // 得意先区分が「経費」ならば、計上区分は「経費」
            return CommonConstants.KBN_KEIJO_KEIHI;
        } else {
            // 取得した明細の形状区分が利用可能か判定する
            return C0801W02AcptRelationDto.checkKeijokbn(tokuisakikbn, detailDto.getKeijokbn(),
                false)
                    // 利用可能ならそのまま
                    ? detailDto.getKeijokbn()
                    // 利用不可ならデフォルトの計上区分
                    : C0801W02AcptRelationDto.getDefaultKeijokbn(tokuisakikbn,
                        detailDto.getKeijokbn(), acptkbn, detailDto.getLctgrycd(),
                        detailDto.getMctgrycd(), serverlcd, servermcd);
        }
    }

    /**
     * 新規受注配達明細データに対してデフォルトのプロパティを設定する.
     * 
     * @param detailDto 受注配達明細DTO
     * @param acptDlvryDto 受注配達DTO
     * @param tokuisakiDlvryDto 得意先配達DTO
     * @param calcOptionDto ショップ計算オプションDTO
     * @param relationDto 受注関係DTO
     * @return 受注配達明細DTO
     */
    protected C0801W02AcptDlvryDetailDto setDefaultPropertiesToBlankDetail(
        C0801W02AcptDlvryDetailDto detailDto, C0801W02AcptDlvryDto acptDlvryDto,
        C0801W02TokuisakiDlvryDto tokuisakiDlvryDto, C0801W02ShopCalculateOptionDto calcOptionDto,
        C0801W02AcptRelationDto relationDto) {

        // 受注配達データの内容を明細に設定
        detailDto.setApkihoncd(acptDlvryDto.getApkihoncd());
        detailDto.setShopcd(acptDlvryDto.getShopcd());
        detailDto.setAcptdnpyno(acptDlvryDto.getAcptdnpyno());
        detailDto.setEdano(acptDlvryDto.getEdano());
        detailDto.setAcptdnpymeisaiedano(BigDecimal.ZERO.stripTrailingZeros().toPlainString());
        detailDto.setDatsksmotokbn(acptDlvryDto.getDatsksmotokbn());

        // 新規でデータベースに受注配達データがない場合は得意先配達データから
        String tokuisakikbn =
            StringUtil.isEmpty(acptDlvryDto.getTokuisakikbn()) ? tokuisakiDlvryDto.getTokuisakikbn()
                : acptDlvryDto.getTokuisakikbn();
        // 計上区分設定
        detailDto
            .setKeijokbn(getNewDetailKeijokbn(detailDto, tokuisakikbn, acptDlvryDto.getAcptkbn()));

        // 明細単価再編集
        detailDto = reEditUnt(true, acptDlvryDto.getDlvrykbn(), acptDlvryDto.getDlvryytidd(),
            detailDto, calcOptionDto, relationDto);

        // 新規でデータベースに受注配達データがない場合は得意先配達データから
        String dlvrykbn =
            StringUtil.isEmpty(acptDlvryDto.getDlvrykbn()) ? tokuisakiDlvryDto.getDlvrykbn()
                : acptDlvryDto.getDlvrykbn();
        // 明細備考設定
        if (detailDto.getAcptRelation().isCampaign()
            && StringUtil.isEmpty(detailDto.getMeisaibiko())) {
            Optional<String> campaignUnt =
                detailDto.getHansokuShnPriceDto(true).map(dto -> dto.getPriceByDlvrykbn(dlvrykbn));
            // 販促優先区分＝"する"で販促価格がNULL以外(販促設定:対象＋適用期間内)の場合
            if (util.isFlagged(detailDto.getHansokuyusenkbn()) && campaignUnt.isPresent()) {
                // 販促名称を明細備考に設定
                detailDto.setMeisaibiko(detailDto.getHansokunm());
            }
        }

        // 商品・入力行のみ
        if (util.isFlagged(detailDto.getNyuryokumeisaiflg())
            && CommonConstants.KBN_MEISAI_SHOHIN.equals(detailDto.getMeisaikbn())) {
            // 特別単価リスト設定
            detailDto.setTokubetsuUntList(tokuisakiService.getTokubetuUnt(
                acptDlvryDto.getApkihoncd(), acptDlvryDto.getShopcd(),
                acptDlvryDto.getTokuisakicd(), acptDlvryDto.getDlvryytidd(), detailDto.getShncd(),
                detailDto.getRknkindtokubetsuuntflg(), detailDto.getTanpinmeisaiflg()));
        }

        return detailDto;
    }

}
