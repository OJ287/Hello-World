package jp.co.aquaclara.accis.web.domain.service.b1501;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M017TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S06T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M017TZ0502W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S06T001TB1501W21Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TB1501W21Entity;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.web.domain.dto.b1501.B1501W21M1SearchMeisaiDto;
import jp.co.aquaclara.accis.web.domain.dto.b1501.B1501W21M2SearchMeisaiDto;
import jp.co.aquaclara.accis.web.domain.dto.b1501.B1501W21SearchConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.b1501.B1501W21SearchSkysakiDto;
import jp.co.aquaclara.accis.web.domain.dto.c0800.ComparableValueCurrentOrNew;
import jp.co.aquaclara.accis.web.domain.dto.c0800.HasConvertableNumericProperty;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M017TDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M046TDto;
import jp.co.aquaclara.accis.web.domain.dto.z0502.Z0502W01SkysakiDto;
import jp.co.aquaclara.accis.web.domain.service.c0800.UtilityService;
import jp.co.aquaclara.accis.web.domain.service.master.S02M017TService;
import jp.co.aquaclara.accis.web.domain.service.master.S02M046TService;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * B1501W21_請求入金照会(詳細)サービスクラス
 */
@Service
@Slf4j
public class B1501W21Service implements HasConvertableNumericProperty, ComparableValueCurrentOrNew {

    /**
     * 請求先マスタDao
     */
    @Autowired
    private S02M017TDao s02m017tDao;
    /**
     * 請求先マスタ-Partsサービスクラス
     */
    @Autowired
    private S02M017TService s02m017tService;
    /**
     * ショップマスタ-Partsサービスクラス
     */
    @Autowired
    private S02M046TService s02m046tService;

    /** ユーティリティサービス */
    @Autowired
    private UtilityService util;

    /**
     * 売上テーブルDao
     */
    @Autowired
    private S06T001TDao s06t001tDao;
    /**
     * 請求残高テーブルDao
     */
    @Autowired
    private S07T001TDao s07t001tDao;

    /**
     * 請求入金照会(詳細)明細エリア検索処理
     * 
     * @param dto 請求入金照会(詳細)検索条件Dto
     * @return 請求入金照会(詳細)検索結果(明細エリア)Dtoリスト
     */
    public List<B1501W21M1SearchMeisaiDto> searchMeisai1(B1501W21SearchConditionDto dto) {
        // 検索用Entityを生成
        S07T001TB1501W21Entity meisaiSearchEntity = createCustomM1Entity(dto);
        // 明細エリア情報(1行目)を取得
        List<B1501W21M1SearchMeisaiDto> meisaiDtoList = new ArrayList<>();
        S07T001TB1501W21Entity meisaiResultEntity =
            s07t001tDao.selectByB1501W21Meisai1(meisaiSearchEntity);
        // 明細データが取得出来た場合のみ返却リストに設定
        if (!ObjectUtils.isEmpty(meisaiResultEntity)) {
            meisaiDtoList.add(createResultMeisai(meisaiResultEntity));
        }
        // 1件目のデータが取得出来ていた場合のみ、明細エリア情報(2行目以降)を取得し設定
        if (!ObjectUtils.isEmpty(meisaiDtoList) && meisaiDtoList.size() > 0) {
            meisaiDtoList.addAll(
                createResultMeisaiList(s07t001tDao.selectByB1501W21Meisai2(meisaiSearchEntity)));
        }
        return meisaiDtoList;
    }

    /**
     * 請求残高テーブル検索用Entity生成
     * 
     * @param dto 請求残高テーブル検索条件Dto
     * @return 請求残高テーブル検索条件Dto
     */
    private S07T001TB1501W21Entity createCustomM1Entity(B1501W21SearchConditionDto dto) {
        S07T001TB1501W21Entity entity = new S07T001TB1501W21Entity();

        // AP基本コード
        entity.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        entity.setShopcd(dto.getShopcd());
        // 締年月
        entity.setShimedydm(dto.getDydmdd().substring(0, 6));
        // 締日
        entity.setShimedd(dto.getDydmdd().substring(6));
        // 請求先コード
        entity.setSkysakicd(dto.getSkysakicd());
        // 回収方法区分
        entity.setKaishuhohokbn(dto.getKaishuhohokbn());
        // 連番
        if (!StringUtils.isEmpty(dto.getRenban())) {
            entity.setRenban(dto.getRenban());
        }

        return entity;
    }

    /**
     * 請求残高テーブル検索結果(明細エリア)Dto生成(1件目)
     * 
     * @param entity 請求入金照会(詳細)明細エリアデータ
     * @return 請求入金照会(詳細)検索結果(明細エリア)DTO
     */
    private B1501W21M1SearchMeisaiDto createResultMeisai(S07T001TB1501W21Entity entity) {
        B1501W21M1SearchMeisaiDto resultDto = new B1501W21M1SearchMeisaiDto();

        // 請求先コード
        resultDto.setSkysakicd(entity.getSkysakicd());
        // 請求区分
        resultDto.setSkykbn(entity.getSkykbn());
        // 請求先名称
        resultDto.setSkysakinm(entity.getSkysakinmn1());
        // 前回請求金額
        resultDto.setZnkskykin(entity.getZnkkurikoshikin());
        // 入金金額
        resultDto.setNyukinkin(entity.getKonkainyknkin());
        // 今回利用金額
        resultDto.setKonkairiyokin(entity.getKonkaitaxnukiurikin());
        // 消費税金額
        resultDto.setShohitaxkin(entity.getKonkaishohitaxkin());
        // 税込ご利用額計
        resultDto
            .setTaxkomiskyzkin(Integer.toString(Integer.parseInt(entity.getKonkaitaxnukiurikin())
                + Integer.parseInt(entity.getKonkaishohitaxkin())));
        // 8%税込ご利用額計
        resultDto.setN8perzeikomiriyogaku(entity.getN8perzeikomiriyogaku());
        // 10%税込ご利用額計
        resultDto.setN10perzeikomiriyogakumei(entity.getN10perzeikomiriyogaku());
        // 今回ご請求金額
        resultDto.setKonkaiskykinmei(entity.getKurikoshikin());
        // 選択
        resultDto.setSentaku(CommonConstants.FLG_OFF);


        return resultDto;
    }

    /**
     * 請求残高テーブル検索結果(明細エリア)Dto生成(2件目以降)
     * 
     * @param entityList 請求入金照会(詳細)明細エリアデータリスト
     * @return 請求入金照会(詳細)検索結果(明細エリア)DTOリスト
     */
    private List<B1501W21M1SearchMeisaiDto> createResultMeisaiList(
        List<S07T001TB1501W21Entity> entityList) {
        List<B1501W21M1SearchMeisaiDto> resultDtoList = new ArrayList<>();

        if (!ObjectUtils.isEmpty(entityList)) {
            for (S07T001TB1501W21Entity entity : entityList) {
                B1501W21M1SearchMeisaiDto resultDto = new B1501W21M1SearchMeisaiDto();
                // 請求先コード(得意先コード)
                resultDto.setSkysakicd(entity.getTokuisakicd());
                // 請求先名称(得意先名称)
                resultDto.setSkysakinm(entity.getTokuisakinm1());
                // 事業所コード
                resultDto.setJigyoshocd(entity.getJigyoshocd());
                // 事業所名称
                resultDto.setJigyoshonm(entity.getJigyoshonm());
                // 得意先状態名
                resultDto.setTokuisakijotainm(entity.getTokuisakijotainm());
                // 状態ステータス名
                resultDto.setJotaistsnm(entity.getJotaistsnm());
                // 請求区分
                resultDto.setSkykbn(entity.getSkykbn());
                // 前回請求金額
                resultDto.setZnkskykin(entity.getZnkkurikoshikin());
                // 入金金額
                resultDto.setNyukinkin(entity.getKonkainyknkin());
                // 今回利用金額
                resultDto.setKonkairiyokin(entity.getKonkaitaxnukiurikin());
                // 消費税金額
                resultDto.setShohitaxkin(entity.getKonkaishohitaxkin());
                // 税込ご利用額計
                resultDto.setTaxkomiskyzkin(
                    Integer.toString(Integer.parseInt(entity.getKonkaitaxnukiurikin())
                        + Integer.parseInt(entity.getKonkaishohitaxkin())));
                // 8%税込ご利用額計
                resultDto.setN8perzeikomiriyogaku(entity.getN8perzeikomiriyogaku());
                // 10%税込ご利用額計
                resultDto.setN10perzeikomiriyogakumei(entity.getN10perzeikomiriyogaku());
                // 今回ご請求金額
                resultDto.setKonkaiskykinmei(entity.getKurikoshikin());
                // 選択
                resultDto.setSentaku(CommonConstants.FLG_ON);

                resultDtoList.add(resultDto);
            }
        }

        return resultDtoList;
    }

    /**
     * 請求入金照会(詳細)検索結果一覧エリア検索処理
     * 
     * @param dto 請求入金照会(詳細)検索条件Dto
     * @return 請求入金照会(詳細)検索結果(検索結果一覧エリア)Dtoリスト
     */
    public List<B1501W21M2SearchMeisaiDto> searchMeisai2(B1501W21SearchConditionDto dto) {
        // 請求先マスタから請求先管理区分取得
        // 請求先管理区分が「2:卸」の場合、税計算区分に「2:外税」設定
        // 請求先管理区分が「2:卸」以外の場合、ショップマスタの税計算系項目を利用して税計算区分設定
        setDbParam(dto);
        log.debug("検索条件 -----------------------------");
        log.debug("\t対象とする得意先");
        dto.getTokuisakicdlist().forEach(tokuisakicd -> log.debug("\t\t{}-{}-{}",
            dto.getApkihoncd(), dto.getShopcd(), tokuisakicd));
        log.debug("\t回収方法区分 : {}", dto.getKaishuhohokbn());
        log.debug("\t運用日付 : {}", dto.getUnyodate());
        log.debug("\tインボイス施行日 : {}", dto.getInvoicshikoymd());
        log.debug("\t税計算区分 : {}", dto.getTaxkeisankbn());
        log.debug("\t年月日 : {}", dto.getDydmdd());
        log.debug("\t年月日フラグ : {}", dto.getDydmddflag());
        log.debug("\t連番 : {}", dto.getRenban());
        log.debug("\t売上入金取消チェック : {}", dto.getUriagenyukinchk());
        log.debug("-----------------------------");
        // 検索用Entity生成
        S06T001TB1501W21Entity meisaiSearchEntity = createCustomM2Entity(dto);
        // 検索処理実施と返却用DTO生成
        List<B1501W21M2SearchMeisaiDto> meisaiDtoList = createResultMeisai2List(
            s06t001tDao.selectByB1501W21(meisaiSearchEntity), meisaiSearchEntity);
        log.debug("検索結果DTOリスト : {}", meisaiDtoList);
        return meisaiDtoList;
    }

    /**
     * 請求先マスタ、ショップマスタから取得した値をDtoに設定
     * 
     * @param dto 請求入金照会(詳細)検索条件Dto
     */
    private void setDbParam(B1501W21SearchConditionDto dto) {
        // 請求先マスタのデータ取得
        S02M017TDto condition = new S02M017TDto();
        condition.setApkihoncd(dto.getApkihoncd());
        condition.setShopcd(dto.getShopcd());
        condition.setSkysakicd(dto.getSkysakicd());
        S02M017TDto skysakiDto = s02m017tService.searchPrimary(condition);
        // ショップマスタのデータ取得
        S02M046TDto shopDto =
            s02m046tService.getByPrimaryKey(dto.getApkihoncd(), dto.getShopcd(), false);
        // 請求先管理区分が「2:卸」の場合は「2:外税」、それ以外の場合は税計算区分売上切替日に沿った税計算区分売上を設定
        if (!ObjectUtils.isEmpty(skysakiDto)
            && CommonConstants.SKYSAKIKANRIKBN_2.equals(skysakiDto.getSkysakikanrikbn())) {
            dto.setTaxkeisankbn(CommonConstants.KBN_TAXKEISAN_EX);
        } else {
            dto.setTaxkeisankbn(
                getCurrentValue(shopDto.getGenztaxkeisankbnuri(), shopDto.getShintaxkeisankbnuri(),
                    shopDto.getTaxkeisankbnurikirikaedd(), dto.getUnyodate()));
        }
        // 請求締日
        dto.setShimedd(skysakiDto.getSkyshimedd());
        // 税端数計算区分
        dto.setTaxhasukeisankbn(shopDto.getTaxhasukeisankbn());
        // インボイス施行日
        dto.setInvoicshikoymd(shopDto.getInvoicshikoymd());
    }

    /**
     * 売上テーブル検索用Entity生成
     * 
     * @param dto 請求入金照会(詳細)検索条件DTO
     * @return 売上テーブル検索条件DTO
     */
    private S06T001TB1501W21Entity createCustomM2Entity(B1501W21SearchConditionDto dto) {
        S06T001TB1501W21Entity entity = new S06T001TB1501W21Entity();
        // AP基本コード
        entity.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        entity.setShopcd(dto.getShopcd());
        // 締年月
        entity.setShimedydm(dto.getDydmdd().substring(0, 6));
        // 締日
        entity.setShimedd(dto.getDydmdd().substring(6));
        // 回収方法区分
        entity.setKaishuhohokbn(dto.getKaishuhohokbn());
        // 請求先コード
        entity.setSkysakicd(dto.getSkysakicd());
        // 得意先コードリスト
        entity.setTokuisakicdlist(dto.getTokuisakicdlist());
        // 年月日フラグ
        entity.setDydmddflag(dto.getDydmddflag());
        // 売上入金取消チェック
        entity.setUriagenyukinchk(dto.getUriagenyukinchk());
        // インボイス施行日
        entity.setInvoicshikoymd(dto.getInvoicshikoymd());
        // 請求先マスタ.請求締日
        entity.setSkyshimedd(dto.getShimedd());
        // 税端数計算区分
        entity.setTaxhasukeisankbn(dto.getTaxhasukeisankbn());
        // 初期表示フラグ
        entity.setShokihyojiflag(dto.getShokihyojiflag());
        // 運用日付
        entity.setUnyodate(dto.getUnyodate());
        // 明細最大出力件数
        entity.setRownum(dto.getLimit());
        // 税計算区分
        entity.setTaxkeisankbn(dto.getTaxkeisankbn());
        // 連番
        entity.setRenban(dto.getRenban());
        return entity;
    }

    /**
     * 売上テーブル検索結果(検索結果一覧エリア)Dto生成
     * 
     * @param entityList 請求入金照会(詳細)検索結果一覧エリアデータリスト
     * @param searchEntity 売上入金データ検索用Entity
     * @return 請求入金照会(詳細)検索結果(検索結果一覧エリア)DTOリスト
     */
    private List<B1501W21M2SearchMeisaiDto> createResultMeisai2List(
        List<S06T001TB1501W21Entity> entityList, S06T001TB1501W21Entity searchEntity) {

        // 検索結果が0件の場合
        if (entityList.size() == 0) {
            throw BusinessExceptionFactory.dataNotFound(MessageID.W083);
        }

        // 消費税（仮）データを作成する必要があるかを判定（代引以外かつ現在表示かつ運用年月＋締日がインボイス施行日以降の場合に作成する）
        final boolean isNeedReferenceTaxData = taxLineCheck(searchEntity);
        // 内税か
        final boolean isIncludeTax =
            CommonConstants.KBN_TAXKEISAN_IN.equals(searchEntity.getTaxkeisankbn());

        // 検索結果EntityをDTOに変換する
        List<B1501W21M2SearchMeisaiDto> dtoList = entityList.stream().map(entity -> {
            B1501W21M2SearchMeisaiDto resultDto = new B1501W21M2SearchMeisaiDto();
            String shohitax = getString(
                // 参考消費税金額が0円の場合は消費税金額
                BigDecimal.ZERO.equals(entity.getSankoushohitaxkin()) ? entity.getShohitaxkin()
                    // 消費税金額が0円の場合
                    : BigDecimal.ZERO.equals(entity.getShohitaxkin())
                        // 参考消費税金額
                        ? entity.getSankoushohitaxkin()
                        // いずれでもない場合は0円
                        : BigDecimal.ZERO);
            // 消費税の設定（明細の消費税とするか参考消費税として、消費税（仮）としてサマリして表示するか）
            if (Objects.equals(BigDecimal.ZERO.toPlainString(), shohitax)) {
                // 消費税も参考消費税も0円の場合、消費税の判定処理は必要ない
                resultDto.setSankoushohitaxkin(getString(BigDecimal.ZERO));
                resultDto.setShohitaxkin(getString(BigDecimal.ZERO));
            } else {
                if (CommonConstants.KAISHUHOHOKBN_7.equals(searchEntity.getKaishuhohokbn())
                    || !CommonConstants.FLG_ON.equals(searchEntity.getDydmddflag())) {
                    // 回収方法区分が代引または当月表示以外の場合
                    // __… 売上明細テーブル.消費税金額を明細の消費税として表示する
                    resultDto.setSankoushohitaxkin(getString(BigDecimal.ZERO));
                    resultDto.setShohitaxkin(getString(entity.getShohitaxkin()));
                } else {
                    // 消費税（仮）を表示する場合
                    // 売上日から請求締年月日（予定）を算出し、その請求締日とインボイス施行日を比較して決める
                    String scheduledFixDate =
                        util.getFixDate(entity.getYmd(), searchEntity.getSkyshimedd());
                    log.debug("伝票番号 : {} / 売上日 : {} / 請求締日 : {} → 請求締年月日（予定） : {}",
                        entity.getPyno(), entity.getYmd(), searchEntity.getSkyshimedd(),
                        scheduledFixDate);
                    if (util.isAfter(scheduledFixDate, searchEntity.getInvoicshikoymd())) {
                        // 請求締年月日（予定）がインボイス施行日より後 … 参考消費税として消費税（仮）にサマリ
                        resultDto.setSankoushohitaxkin(shohitax);
                        resultDto.setShohitaxkin(getString(BigDecimal.ZERO));
                    } else {
                        // 請求締年月日（予定）がインボイス施行日以前 … 明細の消費税として表示
                        resultDto.setSankoushohitaxkin(getString(BigDecimal.ZERO));
                        resultDto.setShohitaxkin(shohitax);
                    }
                }
            }
            // 売上日
            resultDto.setYmd(entity.getYmd());
            // 商品コード
            resultDto.setShncd(entity.getShncd());
            // 現商品名称略称
            resultDto.setGenzshnnmryakusho(entity.getGenzshnnmryakusho());
            // 新商品名称略称
            resultDto.setShinshnnmryakusho(entity.getShinshnnmryakusho());
            // 商品名称切替日
            resultDto.setShnnmkirikaedd(entity.getShnnmkirikaedd());
            // 色
            resultDto.setIro(entity.getIro());
            // サイズ名称
            resultDto.setSznm(entity.getSznm());
            // 型式
            resultDto.setKatasiki(entity.getKatasiki());
            // 計上区分名称
            resultDto.setKeijokbnnm(entity.getKeijokbnnm());
            // 数量
            resultDto.setSuryo(entity.getSuryo());
            // 売上取扱単位名称
            resultDto.setUritrtskitaninm(entity.getUritrtskitaninm());
            // 売上入数
            resultDto.setUriirisu(entity.getUriirisu());
            // 総数
            resultDto.setSokazu(entity.getSokazu());
            // 単価
            resultDto.setUnt(getString(entity.getUnt()));
            // 金額（消費税（仮）を作成しない場合かつ内税の場合は消費税金額を売上金額に加算する）
            if (!isNeedReferenceTaxData && isIncludeTax) {
                resultDto.setKin(
                    getString(entity.getKin().add(getDecimalValue(resultDto.getShohitaxkin()))));
            } else {
                resultDto.setKin(getString(entity.getKin()));
            }
            // 消費税レート
            resultDto.setTaxrate(entity.getTaxrate());
            // 入力明細フラグ
            resultDto.setNyuryokumeisaiflg(entity.getNyuryokumeisaiflg());
            // 入金金額
            resultDto.setNyknkin(getString(entity.getNyknkin()));
            // 明細備考
            resultDto.setBiko(entity.getBiko());
            // 売上ポイント
            resultDto.setUripoint(entity.getUripoint());
            // 特別ポイント
            resultDto.setTkbtpoint(entity.getTkbtpoint());
            // ポイント内訳
            resultDto.setPointutwk(entity.getPointutwk());
            // 合計ポイント
            if(StringUtils.isEmpty(entity.getUripoint())
                    && StringUtils.isEmpty(entity.getTkbtpoint())) {
                // 売上ポイントと特別ポイントが空の場合はnullを設定
                resultDto.setSmpoint(null);
            } else {
                resultDto.setSmpoint(entity.getSmpoint());
            }
            // ポイント連携日付
            resultDto.setPointrenkeidd(entity.getPointrenkeidd());
            // 売上伝票番号
            resultDto.setPyno(entity.getPyno());
            // 枝番号
            resultDto.setEdano(entity.getEdano());
            // 売上伝票明細番号
            resultDto.setMeisaino(entity.getMeisaino());
            // 得意先コード
            resultDto.setTokuisakicd(entity.getTokuisakicd());
            // 得意先名称1
            resultDto.setTokuisakinmn1(entity.getTokuisakinmn1());
            // 登録システム日付
            resultDto.setInptsystmymd(entity.getInptsystmymd());
            // 登録システム時間
            resultDto.setInptsystmjikan(entity.getInptsystmjikan());
            // 登録者AP基本コード
            resultDto.setToshaapkihoncd(entity.getToshaapkihoncd());
            // 登録者ショップコード
            resultDto.setToshashopcd(entity.getToshashopcd());
            // 登録者コード
            resultDto.setToshacd(entity.getToshacd());
            // 担当者名
            resultDto.setTnshamei(entity.getTnshamei());
            // 請求書出力有無区分
            resultDto.setSkyshootptarinashikbn(entity.getSkyshootptarinashikbn());
            // 入金種別名称
            resultDto.setNyukinkindnm(entity.getNyukinkindnm());
            return resultDto;
        }).collect(util.toList());

        // 入金金額のサマリ
        BigDecimal nyukinkinsm = dtoList.stream().map(B1501W21M2SearchMeisaiDto::getNyknkin)
            .map(BigDecimal::new).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

        // 軽減税率の明細リスト
        List<B1501W21M2SearchMeisaiDto> n8perDtoList = dtoList.stream()
            .filter(dto -> CommonConstants.TAX_8.equals(dto.getTaxrate())).collect(util.toList());
        // 軽減税率：金額のサマリ
        BigDecimal n8peruri = n8perDtoList.stream().map(B1501W21M2SearchMeisaiDto::getKin)
            .map(BigDecimal::new).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        // 軽減税率：消費税金額のサマリ
        BigDecimal n8pershohzei =
            n8perDtoList.stream().map(B1501W21M2SearchMeisaiDto::getShohitaxkin)
                .map(BigDecimal::new).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        // 軽減税率：参考消費税金額のサマリ
        BigDecimal n8persankoushohzei =
            n8perDtoList.stream().map(B1501W21M2SearchMeisaiDto::getSankoushohitaxkin)
                .map(BigDecimal::new).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        // 一般税率の明細リスト
        List<B1501W21M2SearchMeisaiDto> n10perDtoList = dtoList.stream()
            .filter(dto -> CommonConstants.TAX_10.equals(dto.getTaxrate())).collect(util.toList());
        // 一般税率：金額のサマリ
        BigDecimal n10peruri = n10perDtoList.stream().map(B1501W21M2SearchMeisaiDto::getKin)
            .map(BigDecimal::new).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        // 一般税率：消費税金額のサマリ
        BigDecimal n10pershohzei =
            n10perDtoList.stream().map(B1501W21M2SearchMeisaiDto::getShohitaxkin)
                .map(BigDecimal::new).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        // 一般税率：参考消費税金額のサマリ
        BigDecimal n10persankoushohzei =
            n10perDtoList.stream().map(B1501W21M2SearchMeisaiDto::getSankoushohitaxkin)
                .map(BigDecimal::new).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

        // 参考消費税に金額設定されている場合は消費税（仮）の明細行を追加する
        if (!BigDecimal.ZERO.equals(n8persankoushohzei)
            || !BigDecimal.ZERO.equals(n10persankoushohzei)) {
            // 8%消費税データ生成
            B1501W21M2SearchMeisaiDto tmpN8pershohitax =
                createShohitaxLine(CommonConstants.TAX_8, n8persankoushohzei);
            dtoList.add(tmpN8pershohitax);
            // 10%消費税データ生成
            B1501W21M2SearchMeisaiDto tmpN10pershohitax =
                createShohitaxLine(CommonConstants.TAX_10, n10persankoushohzei);
            dtoList.add(tmpN10pershohitax);
        }

        // 税率ごとのトータル消費税金額を算出
        BigDecimal totalN8pershohitax = n8pershohzei.add(n8persankoushohzei);
        BigDecimal totalN10pershohitax = n10pershohzei.add(n10persankoushohzei);

        // 内税の場合は消費税を売上金額に加算
        if (isIncludeTax) {
            n8peruri = n8peruri.add(totalN8pershohitax);
            n10peruri = n10peruri.add(totalN10pershohitax);
        }

        // 売上ポイント合計
        BigDecimal uripointSm = dtoList.stream().map(B1501W21M2SearchMeisaiDto::getUripoint)
                .map(v -> toBDSafe(v)).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

        // 特別ポイント合計
        BigDecimal tkbtpointSm = dtoList.stream().map(B1501W21M2SearchMeisaiDto::getTkbtpoint)
                .map(v -> toBDSafe(v)).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

        // ポイント合計
        BigDecimal pointsm = uripointSm.add(tkbtpointSm);

        // 明細の先頭データに合算データを設定
        log.debug("合算データ");
        log.debug("\t軽減売上金額合計 : {}", n8peruri);
        log.debug("\t軽減消費税金額合計 : {}", n8pershohzei);
        log.debug("\t軽減参考消費税金額合計 : {}", n8persankoushohzei);
        log.debug("\t一般売上金額合計 : {}", n10peruri);
        log.debug("\t一般消費税金額合計 : {}", n10pershohzei);
        log.debug("\t一般参考消費税金額合計 : {}", n10persankoushohzei);
        log.debug("\t売上金額合計 : {}", n8peruri.add(n10peruri));
        log.debug("\t消費税金額合計 : {}", totalN8pershohitax.add(totalN10pershohitax));
        log.debug("\t入金金額合計 : {}", nyukinkinsm);
        log.debug("\tポイント合計 : {}", pointsm);
        dtoList.get(0).setN8peruri(n8peruri);
        dtoList.get(0).setN8pershohzei(totalN8pershohitax);
        dtoList.get(0).setN10peruri(n10peruri);
        dtoList.get(0).setN10pershohzei(totalN10pershohitax);
        dtoList.get(0).setKinsm1(n8peruri.add(n10peruri));
        dtoList.get(0).setKinsm2(totalN8pershohitax.add(totalN10pershohitax));
        dtoList.get(0).setKinsm3(nyukinkinsm);
        dtoList.get(0).setPointsm(pointsm);

        return dtoList;
    }

    /**
     * 消費税データチェック
     * 
     * @param entity 検索結果データ
     * @return true/false
     */
    private boolean taxLineCheck(S06T001TB1501W21Entity entity) {
        log.debug("消費税データチェック");
        log.debug("\t回収方法区分 : {}", entity.getKaishuhohokbn());
        log.debug("\t年月日フラグ : {}", entity.getDydmddflag());
        log.debug("\tインボイス施行日 : {}", entity.getInvoicshikoymd());
        log.debug("\t運用日付 : {}", entity.getUnyodate());
        log.debug("\t請求締日 : {}", entity.getSkyshimedd());
        // 回収方法区分が「7:代引」以外、かつ年月日フラグが「1:ON(現在)」、かつインボイス施行日<=(運用年月+締日)の場合、true
        // つまり消費税（仮）のデータを生成する
        if (!CommonConstants.KAISHUHOHOKBN_7.equals(entity.getKaishuhohokbn())
            && CommonConstants.FLG_ON.equals(entity.getDydmddflag())
            && !StringUtils.isEmpty(entity.getInvoicshikoymd())
            && DateTimeUtil.isLater(entity.getInvoicshikoymd(),
                entity.getUnyodate().substring(0, 6) + entity.getSkyshimedd())) {
            log.debug("消費税データチェック結果 : true");
            return true;
        }
        log.debug("消費税データチェック結果 : false");
        return false;
    }

    /**
     * 消費税データ生成処理
     * 
     * @param taxrate 税率
     * @param sankoushohitaxkin 参考消費税金額
     * @return 請求入金照会(詳細)検索結果(検索結果一覧エリア)DTO
     */
    private B1501W21M2SearchMeisaiDto createShohitaxLine(String taxrate,
        BigDecimal sankoushohitaxkin) {

        // 消費税名称の出力判定処理
        String shinshnmeiryakusyo = null;
        if (CommonConstants.TAX_8.equals(taxrate)) {
            // 税率8%時の表示内容設定
            shinshnmeiryakusyo = "軽減消費税（仮）";
        } else {
            // 税率10%時の表示内容設定
            shinshnmeiryakusyo = "消費税（仮）";
        }

        return new B1501W21M2SearchMeisaiDto("", // 売上日
            "", // 商品コード
            "", // 現商品名称略称
            shinshnmeiryakusyo, // 新商品名称略称
            "", // 商品名称切替日
            "", // 色
            "", // サイズ名称
            "", // 型式
            "", // 計上区分名称
            "", // 数量
            "", // 売上取扱単位名称
            "", // 売上入数
            "", // 総数
            "0", // 単価
            "0", // 金額
            "", // 消費税レート
            "", // 入力明細フラグ
            getString(sankoushohitaxkin), // 消費税額
            "0", // 参考消費税金額
            "0", // 入金金額
            "", // 明細備考
            "", // 売上ポイント
            "", // 特別ポイント
            "", // ポイント内訳
            "", // 合計ポイント
            "", // ポイント連携日付
            "", // 売上伝票番号
            "", // 枝番号
            "", // 売上伝票明細番号
            "", // 得意先コード
            "", // 得意先名称1
            "", // 登録システム日付
            "", // 登録システム時間
            "", // 登録者AP基本コード
            "", // 登録者ショップコード
            "", // 登録者コード
            "", // 担当者名
            "", // 請求書出力有無区分
            "", // 入金種別名称
            CommonConstants.FLG_ON, // 消費税フラグ
            null, // 8%の売上金額合計
            null, // 8%の消費税額合計
            null, // 10%の売上金額合計
            null, // 10%の消費税額合計
            null, // 金額合計1
            null, // 金額合計2
            null, // 金額合計3
            null // ポイント合計
        );
    }

    /**
     * 請求先共通ヘッダ同等データ取得処理
     * 
     * @param dto 請求先共通ヘッダ検索条件DTO
     * @return 請求入金照会(詳細)請求先情報DTO
     */
    public B1501W21SearchSkysakiDto searchSkysaki(Z0502W01SkysakiDto dto) {
        S02M017TZ0502W01Entity example = new S02M017TZ0502W01Entity();
        example.setApkihoncd(dto.getApkihoncd()); // AP基本コード
        example.setShopcd(dto.getShopcd()); // ショップコード
        example.setSkysakicd(dto.getSkysakicd()); // 請求先コード
        example.setUnyodate(dto.getUnyodate()); // 運用日付
        example.setYukokbn(CommonConstants.YUKOKBN_1); // 有効区分

        // ショップマスタ検索
        S02M046TDto s02m046tDto =
            s02m046tService.getByPrimaryKey(dto.getApkihoncd(), dto.getShopcd(), false);

        // 請求先マスタ検索と返却データ生成
        return createResultSkysakiDto(s02m017tDao.selectByZ0502W01(example), s02m046tDto,
            dto.getUnyodate());
    }

    /**
     * 請求先共通ヘッダ同等データ生成処理
     * 
     * @param resultEntity 請求先マスタ検索結果
     * @param s02m046tDto ショップマスタ検索結果
     * @param unyodate 運用日付
     * @return 請求入金照会(詳細)請求先情報DTO
     */
    private B1501W21SearchSkysakiDto createResultSkysakiDto(S02M017TZ0502W01Entity resultEntity,
        S02M046TDto s02m046tDto, String unyodate) {
        return new B1501W21SearchSkysakiDto(resultEntity.getSkysakikanrikbn(), // 請求先管理区分
            resultEntity.getSkysakinmn1(), // 請求先名称
            resultEntity.getSkykbnnm(), // 請求区分名称
            resultEntity.getSkyshimedd(), // 請求締日
            resultEntity.getShrsaito(), // 支払サイト
            // インボイス施行日が未設定もしくはインボイス施行日>運用日付の場合は現在残高、それ以外の場合は空設定
            StringUtils.isEmpty(s02m046tDto.getInvoicshikoymd())
                || !DateTimeUtil.isLater(s02m046tDto.getInvoicshikoymd(), unyodate)
                    ? resultEntity.getGenzaizandaka()
                    : "", // 現在残高
            resultEntity.getTairyukin(), // 滞留金額
            resultEntity.getYubinno(), // 郵便番号
            resultEntity.getAdrsn1(), // 住所１
            resultEntity.getAdrsn2(), // 住所２
            resultEntity.getAdrsn3(), // 住所３
            resultEntity.getGaiburenkeicd(), // 外部連携コード
            resultEntity.getTnshamei(), // 営業担当者名
            resultEntity.getSkysakinmkana(), // 請求先カナ名称
            resultEntity.getKaishuhohokbnnm(), // 回収方法区分名称
            resultEntity.getRenrakusakinon1(), // 連絡先番号１
            resultEntity.getShrdd(), // 支払日
            s02m046tDto.getGenztaxkeisankbnuri(), // 現税計算区分売上
            s02m046tDto.getShintaxkeisankbnuri(), // 新税計算区分売上
            s02m046tDto.getTaxkeisankbnurikirikaedd(), // 税計算区分売上切替日
            s02m046tDto.getTaxhasukeisankbn(), // 税端数計算区分
            s02m046tDto.getInvoicshikoymd() // インボイス施行日
        );
    }

    // ポイント合計用 Null考慮
    private BigDecimal toBDSafe(String v) {
        // Null の場合に「0」で返す
        if(StringUtils.isEmpty(v)) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(v);
    }
}
