package jp.co.aquaclara.accis.web.domain.service.c1002;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.constants.ValidationConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M202TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M203TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02V001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S06T002TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S20T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S20T002TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S20T004TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S20W002TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S20W003TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.Z2401B01Dao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M003TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M041TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M050TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M202TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M203TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S06T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S06T002TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T004TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S20T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S20T002TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S20T004TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S20W002TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S20W002TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S20W003TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S20W003TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S20W003TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.Z2401B01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02V001TC1002W01UriageMeisaiBlankRowEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S06T001TC1002W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S06T002TC1002W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S10T004TC1002W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.business.S02V001TEntityDaoEx;
import jp.co.aquaclara.accis.common.exception.BusinessException;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.controller.c1002.bean.C1002W01BaseBean;
import jp.co.aquaclara.accis.web.controller.c1002.bean.C1002W01CountBean;
import jp.co.aquaclara.accis.web.controller.c1002.bean.C1002W01MeisaiBean;
import jp.co.aquaclara.accis.web.controller.c1002.bean.C1002W01SlipBean;
import jp.co.aquaclara.accis.web.controller.c1002.bean.C1002W01TransportBean;
import jp.co.aquaclara.accis.web.controller.c1002.form.C1002W01Form;
import jp.co.aquaclara.accis.web.controller.commons.DbManagementSession;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02ShnDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02TokuisakiMasterDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.shnprice.C0801W02RangeShnPriceDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.shnprice.C0801W02SwitchableShnPriceDto;
import jp.co.aquaclara.accis.web.domain.dto.c1002.C1002W01CalcTaxDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M006TDto;
import jp.co.aquaclara.accis.web.domain.service.business.SaibanService;
import jp.co.aquaclara.accis.web.domain.service.business.TaxService;
import jp.co.aquaclara.accis.web.domain.service.c0800.UtilityService;
import jp.co.aquaclara.accis.web.domain.service.c0801.w02.C0801W02ShnService;
import jp.co.aquaclara.accis.web.domain.service.c0801.w02.C0801W02TokuisakiService;
import jp.co.aquaclara.accis.web.domain.service.master.S02M006TService;
import jp.co.aquaclara.accis.web.domain.service.x9901.StringUtil;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import jp.co.aquaclara.accis.web.exception.dto.FormField;
import lombok.extern.slf4j.Slf4j;

/**
 * 売上入力 Service
 */
@Service
@Slf4j
public class C1002W01Service {

    /** String ⇔ LocalDate変換用（yyyyMMdd） */
    private static final DateTimeFormatter FORMATTER_YYYYMMDD =
        DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 売上入力_共通Service
     */
    @Autowired
    private C1002W01CommonService commonService;

    /** ユーティリティ */
    @Autowired
    private UtilityService util;

    /**
     * 売上入力_売上テーブル関連Service
     */
    @Autowired
    private C1002W01UriageService uriageService;

    /**
     * 売上入力_売上明細テーブル関連Service
     */
    @Autowired
    private C1002W01UriageMeisaiService uriageMeisaiService;

    /**
     * 売上入力_得意先マスタ関連Service
     */
    @Autowired
    private C1002W01TokuisakiService tokuisakiService;

    /**
     * 売上入力_得意先実績テーブル関連Service
     */
    @Autowired
    private C1002W01TokuisakiJissekiService tokuisakiJissekiService;

    /**
     * 売上入力_入出庫実績テーブル関連Service
     */
    @Autowired
    private C1002W01NyushukkoJissekiService nyushukkoJissekiService;

    /**
     * 売上入力_入出庫実績明細テーブル関連Service
     */
    @Autowired
    private C1002W01NyushukkoJissekiMeisaiService nyushukkoJissekiMeisaiService;

    /**
     * 売上入力_現在庫テーブル関連Service
     */
    @Autowired
    private C1002W01GenZaikoService genZaikoService;

    /**
     * 売上入力_在庫テーブル関連Service
     */
    @Autowired
    private C1002W01ZaikoService zaikoService;

    /**
     * 売上入力_在庫入出庫明細テーブル関連Service
     */
    @Autowired
    private C1002W01ZaikoNyuShukkoMeisaiService zaikoNyushukkoMeisaiService;

    /**
     * システム管理マスタService
     */
    @Autowired
    private S02M006TService s02m006tService;

    /**
     * 商品明細消費税・金額計算Service
     */
    @Autowired
    private TaxService taxService;

    /**
     * 採番Service
     */
    @Autowired
    private SaibanService saibanService;

    /**
     * 商品マスタビューDao
     */
    @Autowired
    private S02V001TDao s02v001tDao;

    /**
     * 売上明細テーブルDao
     */
    @Autowired
    private S06T002TDao s06t002tDao;

    /**
     * 請求残高テーブルDao
     */
    @Autowired
    private S07T001TDao s07t001tDao;

    /**
     * ポイント情報部品引数ワークテーブルDao
     */
    @Autowired
    private S20W002TDao s20w002tDao;

    /**
     * ポイント情報部品処理結果ワークテーブルDao
     */
    @Autowired
    private S20W003TDao s20w003tDao;

    /**
     * ポイント情報部品バッチDAO
     */
    @Autowired
    private Z2401B01Dao z2401b01Dao;

    /**
     * 予定発生ポイントテーブルDAO
     */
    @Autowired
    private S20T001TDao s20t001tDao;

    /**
     * 予定発生ポイント明細テーブルDAO
     */
    @Autowired
    private S20T002TDao s20t002tDao;

    /**
     * 予定発生ポイント明細計算詳細テーブルDAO
     */
    @Autowired
    private S20T004TDao s20t004tDao;

    /**
     * ショップポイントマスタDAO
     */
    @Autowired
    private S02M202TDao s02m202tDao;

    /**
     * 得意先ポイントマスタDAO
     */
    @Autowired
    private S02M203TDao s02m203tDao;

    /** 得意先サービス（価格情報取得用） */
    @Autowired
    private C0801W02TokuisakiService customerService;
    /** 商品サービス（価格情報取得用） */
    @Autowired
    private C0801W02ShnService shnService;


    /**
     * 売上伝票チェック
     * 
     * @param uriageDenpyoEntity 売上伝票情報
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     */
    public void checkUriageDenpyo(S06T001TC1002W01Entity uriageDenpyoEntity, String apkihoncd,
        String shopcd, String tokuisakicd) {

        // 売上伝票が取得できない場合はエラー
        if (ObjectUtils.isEmpty(uriageDenpyoEntity)) {
            List<FormField> fields = List.of(new FormField("baseBean.uridnpyno"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W083);
        }

        // データ作成元区分が「生産情報入力」の場合はエラー
        if (CommonConstants.KBN_SAKUSEIMOTO_SEISANJOHONYURYOKU
            .equals(uriageDenpyoEntity.getDatsksmotokbn())) {
            List<FormField> fields = List.of(new FormField("baseBean.uridnpyno"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W171,
                "生産情報入力で登録された売上データ", "売上入力では取消");
        }

        // 得意先確定時に入力した得意先コードと売上伝票の得意先コードが異なる場合はエラー
        if (!(tokuisakicd.equals(uriageDenpyoEntity.getTokuisakicd()))) {
            List<FormField> fields = List.of(new FormField("baseBean.uridnpyno"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W120, "得意先コード",
                "売上伝票データと同一得意先コード");
        }

        // 売上伝票の「伝票番号」が空以外の場合はエラー
        if (!(StringUtils.isEmpty(uriageDenpyoEntity.getDnpyno()))) {
            List<FormField> fields = List.of(new FormField("baseBean.uridnpyno"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W124, "取消済データ");
        }

        // 売上伝票の「受注伝票番号」が空以外の場合はエラー
        if (!(StringUtils.isEmpty(uriageDenpyoEntity.getAcptdnpyno()))) {
            List<FormField> fields = List.of(new FormField("baseBean.uridnpyno"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W171,
                "配達実績と連携しているデータ", "取消");
        }

        // 得意先情報、請求先情報を取得
        S02M013TEntity tokuisakiEntity = tokuisakiService.search(apkihoncd, shopcd, tokuisakicd);
        String skysakicd = tokuisakiEntity.getSkysakicd();
        S02M017TEntity seikyusakiEntity = commonService.searchSkysaki(apkihoncd, shopcd, skysakicd);

        // 請求残高テーブルを検索する
        S07T001TEntity seikyuZandakaEntity = null;
        S07T001TEntityExample S07T001TExample = new S07T001TEntityExample();
        S07T001TEntityExample.Criteria S07T001TCriteria = S07T001TExample.createCriteria();
        S07T001TCriteria.andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd)
            .andSkysakicdEqualTo(skysakicd).andTokuisakicdEqualTo(tokuisakicd);

        if (CommonConstants.DAIBIKI.equals(uriageDenpyoEntity.getSkyprcsjissidd())) {
            S07T001TCriteria.andShimedydmEqualTo(uriageDenpyoEntity.getUridd().substring(0, 6))
                .andShimeddEqualTo(uriageDenpyoEntity.getUridd().substring(6))
                .andSkyprcsjissikbnEqualTo(
                    Short.parseShort(CommonConstants.SKYPRCSJISSIKBN_DAIBIKI));
        } else {
            if (StringUtils.isEmpty(uriageDenpyoEntity.getSkyshimedydm())) {
                S07T001TCriteria.andShimedydmIsNull();
            } else {
                S07T001TCriteria.andShimedydmEqualTo(uriageDenpyoEntity.getSkyshimedydm());
            }

            if (StringUtils.isEmpty(uriageDenpyoEntity.getSkyshimedd())) {
                S07T001TCriteria.andShimeddIsNull();
            } else {
                S07T001TCriteria.andShimeddEqualTo(uriageDenpyoEntity.getSkyshimedd());
            }

            S07T001TCriteria.andSkyprcsjissikbnEqualTo(
                Short.parseShort(CommonConstants.SKYPRCSJISSIKBN_TSUKIJIME));
        }

        S07T001TExample.setOrderByClause("RENBAN DESC");

        List<S07T001TEntity> seikyuZandakaEntityList = s07t001tDao.selectByExample(S07T001TExample);
        if (seikyuZandakaEntityList.size() > 0) {
            seikyuZandakaEntity = seikyuZandakaEntityList.get(0);
        }

        // 消込金額が「空」「0」いずれでもない場合、代引のチェックを実施
        if (Objects.nonNull(seikyuZandakaEntity)) {
            Long keshikomikin = seikyuZandakaEntity.getKeshikomikin();
            if (!(StringUtils.isEmpty(keshikomikin) || keshikomikin == 0)) {
                checkDaibiki(apkihoncd, shopcd, tokuisakicd, uriageDenpyoEntity, seikyusakiEntity,
                    seikyuZandakaEntity);
            }
        }

        // 請求処理実施日を取得
        String skyprcsjissidd = uriageDenpyoEntity.getSkyprcsjissidd();

        // 「請求処理実施日が "DAIBIKI" または "00000000"」以外の場合、締日のチェックを実施
        if (!(CommonConstants.DAIBIKI.equals(skyprcsjissidd)
            || CommonConstants.DEF_DATE_00000000.equals(skyprcsjissidd))) {
            // 直近の請求締で対象となった売上を取り消そうとした場合、下記メソッドで例外となる
            // ※直近の請求残高（月締）については、締解除可能なので解除してから取消すようにメッセージを出す
            checkShimeymd(uriageDenpyoEntity, apkihoncd, shopcd, skysakicd, tokuisakicd);
            // 既に請求済の売上は取消不可（請求処理実施日に日付が入っている場合はエラーにする）
            // ※過去の請求残高は締解除出来ないため、取消すのではなく相殺するためのマイナス売上を作成する必要がある
            throw BusinessExceptionFactory.validationFailed(
                List.of(new FormField("baseBean.uridnpyno")), MessageID.W171, "請求済データ", "取消");
        }

        // 「請求処理実施日が "DAIBIKI" または "00000000"」かつ「売上締処理日が "00000000"」の場合はエラー
        String urishimeprcsdd = uriageDenpyoEntity.getUrishimeprcsdd();
        if (CommonConstants.DAIBIKI.equals(skyprcsjissidd)
            || CommonConstants.DEF_DATE_00000000.equals(skyprcsjissidd)) {
            if (CommonConstants.DEF_DATE_00000000.equals(urishimeprcsdd)) {
                List<FormField> fields = List.of(new FormField("baseBean.uridnpyno"));
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W171, "配達中の定例データ",
                    "取消");
            }
        }

        // 入金金額が空でない、かつ0以外の場合はエラー
        if (!StringUtils.isEmpty(uriageDenpyoEntity.getNyknkin())
            && uriageDenpyoEntity.getNyknkin() != 0) {
            List<FormField> fields = List.of(new FormField("baseBean.uridnpyno"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W171, "売上同時入金の売上伝票",
                "取消");
        }
    }

    /**
     * 売上明細検索
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param uridenpyno 伝票番号
     * @param rownum 取得行数
     * @return
     */
    public List<S06T002TC1002W01Entity> searchUriageMeisai(String apkihoncd, String shopcd,
        String uridenpyno, int rownum) {

        S06T002TC1002W01Entity entity = new S06T002TC1002W01Entity();

        entity.setApkihoncd(apkihoncd);
        entity.setShopcd(shopcd);
        entity.setUridnpyno(uridenpyno);
        entity.setRownum(rownum);

        List<S06T002TC1002W01Entity> resultList = s06t002tDao.selectByC1002W01(entity);

        return resultList;
    }

    /**
     * 締日チェック
     * 
     * @param entity 売上伝票情報
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param skysakicd 請求先コード
     * @param tokuisakicd 得意先コード
     */
    private void checkShimeymd(S06T001TC1002W01Entity entity, String apkihoncd, String shopcd,
        String skysakicd, String tokuisakicd) {

        // 締年月日の最大値を取得
        String shimeymd =
            s07t001tDao.selectMaxShimeddByC1002W01(apkihoncd, shopcd, skysakicd, tokuisakicd);

        if (!StringUtils.isEmpty(shimeymd)) {

            String yyyymm = shimeymd.substring(0, 6);
            String dd = shimeymd.substring(6);

            // 売上伝票の締日と請求残高テーブルの最大締年月日が等しい場合はエラー
            if (yyyymm.equals(entity.getSkyshimedydm()) && dd.equals(entity.getSkyshimedd())) {
                List<FormField> fields = List.of(new FormField("baseBean.uridnpyno"));
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W260,
                    "請求締めがされている為、修正・取消はできません。締め解除を行った後に、再度修正をしてください。");
            }
        }
    }

    /**
     * 代引きチェック
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @param uriageDenpyoEntity 売上伝票情報
     * @param skysakiEntity 請求先マスタレコード
     * @param skyzdEntity 請求残高テーブルレコード
     */
    private void checkDaibiki(String apkihoncd, String shopcd, String tokuisakicd,
        S06T001TC1002W01Entity uriageDenpyoEntity, S02M017TEntity skysakiEntity,
        S07T001TEntity skyzdEntity) {

        // 『売上伝票番号の一桁目が"U"かつ回収方法区分が7（代引き）』を満たさない場合はエラー
        if (!("U".equals(uriageDenpyoEntity.getUridnpyno().substring(0, 1))
            && CommonConstants.KAISHUHOHOKBN_7.equals(skysakiEntity.getKaishuhohokbn()))) {
            List<FormField> fields = List.of(new FormField("baseBean.uridnpyno"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W171, "入金消し込み済みデータ",
                "取消");
        }

        BigDecimal urkin = new BigDecimal(uriageDenpyoEntity.getUrikin()).negate();
        BigDecimal shohitaxkin = new BigDecimal(uriageDenpyoEntity.getShohitaxkin()).negate();
        BigDecimal keshikomizd = new BigDecimal(skyzdEntity.getKeshikomizd());

        urkin = urkin.add(shohitaxkin).abs();

        // 請求残高消込済金額情報を取得する
        // 自身の売上入金で自身が属する請求残高の消込を行った額と現時点の請求残高の消込/未消額と
        // 集計レコードもあればそれの消込/未消額を取得する。（なければnull)
        S10T004TC1002W01Entity nyknkshkmEntity =
            commonService.searchNyknkshkm(apkihoncd, shopcd, uriageDenpyoEntity.getUridnpyno(),
                skyzdEntity.getSkysakicd(), tokuisakicd, skyzdEntity.getSkyshono());

        // 請求残高消込済金額情報を取得できない場合
        // 自身の売上が属する請求残高に対しての売上入金消込を行っていない場合
        if (ObjectUtils.isEmpty(nyknkshkmEntity)) {

            // 消込残高 < 消費税込金額の場合
            // =自身が属する請求残高の未消残高に自身の売上額分が存在しなければエラー
            if (keshikomizd.abs().longValue() < urkin.abs().longValue()) {
                List<FormField> fields = List.of(new FormField("baseBean.uridnpyno"));
                throw BusinessExceptionFactory.validationFailed(fields, MessageID.W171,
                    "入金消し込み済みデータ", "取消");
            }

            return;
        }

        // 以降、マップを取得できた（自身の売上が属する請求残高に対しての売上入金消込を行っている）場合の処理

        // 入金が持つ消込金
        BigDecimal nkesikmkin = BigDecimal.valueOf(nyknkshkmEntity.getNrkeshikomikin()).abs();

        // 請求残高が持つ消込金
        BigDecimal skesikmkin = BigDecimal.valueOf(nyknkshkmEntity.getS1keshikomikin()).abs();

        // 請求残高が持つ残高
        BigDecimal szandaka = BigDecimal.valueOf(nyknkshkmEntity.getS1keshikomizd()).abs();

        // 代引きサブチェック呼び出し
        checkDaibikiSub(urkin, nkesikmkin, skesikmkin, szandaka);

        // 代引請求書番号がオールゼロ以外（＝既に代引集計レコードが存在する）の場合
        if (!("000000000000".equals(nyknkshkmEntity.getDaibikiskyshono()))) {

            skesikmkin = BigDecimal.valueOf(nyknkshkmEntity.getS2keshikomikin()).abs();
            szandaka = BigDecimal.valueOf(nyknkshkmEntity.getS2keshikomizd()).abs();

            // 再度サブチェック呼び出し
            checkDaibikiSub(urkin, nkesikmkin, skesikmkin, szandaka);
        }
    }

    /**
     * 代引きチェック（サブ）
     * 
     * @param urkin 売上金額
     * @param nkesikmkin 入金消込金額
     * @param skesikmkin 請求消込金額
     * @param szandaka 請求残高
     */
    private void checkDaibikiSub(BigDecimal urkin, BigDecimal nkesikmkin, BigDecimal skesikmkin,
        BigDecimal szandaka) {

        // 消込金を全額戻せない
        // ⇒ 自身の売上入金を戻しても自身が属する請求残高の未消残高に自身の売上額分が存在しない場合はエラー
        if (skesikmkin.longValue() < nkesikmkin.longValue()) {
            List<FormField> fields = List.of(new FormField("baseBean.uridnpyno"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W171, "入金消し込み済みデータ",
                "取消");
        }

        // 消込を戻したあとの残高に売上額分がなければエラー
        if (nkesikmkin.add(szandaka).longValue() < urkin.longValue()) {
            List<FormField> fields = List.of(new FormField("baseBean.uridnpyno"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W171, "入金消し込み済みデータ",
                "取消");
        }
    }

    /**
     * 行追加・挿入イベントの共通処理
     * 
     * @param form C1002W01Form
     * @return
     */
    public C1002W01MeisaiBean getBlankRow(C1002W01Form form) {
        C1002W01BaseBean baseBean = form.getBaseBean();
        C1002W01SlipBean slipBean = form.getSlipBean();

        String apkihoncd = baseBean.getApkihoncd();
        String shopcd = baseBean.getShopcd();
        String tokuisakicd = form.getKttcd();
        String shncd = baseBean.getShncdadd();
        String uridd = DateTimeUtil.toNoSlashedDate(slipBean.getUridd());

        // 売上明細伝票新規追加行情報を取得する
        List<S02V001TC1002W01UriageMeisaiBlankRowEntity> blankMeisaiBeanList = s02v001tDao
            .selectUriageMeisaiBlankRowsByC1002W01(apkihoncd, shopcd, tokuisakicd, shncd, uridd);

        // 有効な商品の場合のみ明細行を追加・挿入する（旧システム踏襲）
        if (blankMeisaiBeanList.size() == 0) {
            return null;
        }

        S02V001TC1002W01UriageMeisaiBlankRowEntity blankRowEntity = blankMeisaiBeanList.get(0);

        // 調整用商品コード取得
        String chouseiShncd = commonService.getZeichoseihncd();

        String tokuisakikbn = baseBean.getTokuisakikbn();
        String dlvryhoho = slipBean.getDlvryhoho();

        C1002W01MeisaiBean meisaiBean = new C1002W01MeisaiBean();

        meisaiBean.setApkihoncd(apkihoncd);
        meisaiBean.setShopcd(shopcd);
        meisaiBean.setShncdmei(blankRowEntity.getShncd());
        meisaiBean.setShnnmryakushomei(blankRowEntity.getShnnmryakusho());
        meisaiBean.setIromei(blankRowEntity.getIro());
        meisaiBean.setSzmei(blankRowEntity.getSz());
        meisaiBean.setTrtskitani1mei(blankRowEntity.getUritrtskitaninm());
        meisaiBean.setTrtskitani2mei(blankRowEntity.getSiiretrtskitaninm());

        String irisu1mei = (StringUtils.isEmpty(blankRowEntity.getUriirisu())) ? ""
            : blankRowEntity.getUriirisu().toString();
        meisaiBean.setIrisu1mei(irisu1mei);

        meisaiBean.setIrisutani1mei(blankRowEntity.getUritaninm());

        String irisu2mei = (StringUtils.isEmpty(blankRowEntity.getSiireirisu())) ? ""
            : blankRowEntity.getSiireirisu().toString();
        meisaiBean.setIrisu2mei(irisu2mei);

        meisaiBean.setIrisutani2mei(blankRowEntity.getSiiretaninm());

        meisaiBean.setNyuryokumeisaiflg(CommonConstants.FLG_ON);
        if (chouseiShncd.equals(blankRowEntity.getShncd())) {
            meisaiBean.setZeicyoseiflg(CommonConstants.FLG_ON);
        } else {
            meisaiBean.setZeicyoseiflg(CommonConstants.FLG_OFF);
        }

        String tanpinmeisaiflg =
            (CommonConstants.STKBN_0.equals(blankRowEntity.getStkbn())) ? CommonConstants.FLG_ON
                : CommonConstants.FLG_OFF;
        meisaiBean.setTanpinmeisaiflg(tanpinmeisaiflg);

        meisaiBean.setTaxkbn(blankRowEntity.getTaxkbn());
        meisaiBean.setStkbn(blankRowEntity.getStkbn());
        meisaiBean.setPricekbn(blankRowEntity.getPricekbn());

        meisaiBean.setLctgrycd(blankRowEntity.getLctgrycd());
        meisaiBean.setMctgrycd(blankRowEntity.getMctgrycd());
        meisaiBean.setSctgrycd(blankRowEntity.getSctgrycd());

        // 計上区分の絞り込み用に得意先区分を設定する
        meisaiBean.setTokuisakikbn(tokuisakikbn);

        // デフォルトの計上区分を取得
        // ※後続の売上単価設定処理にて、計上区分を「キャンペーン」に設定するケースあり
        String keijokbn = getDefaultKeijokbn(tokuisakikbn, CommonConstants.KBN_ACPT_SHOHIN,
            blankRowEntity.getLctgrycd(), blankRowEntity.getMctgrycd());
        meisaiBean.setKeijokbnmei(keijokbn);

        // 標準単価を設定
        if (CommonConstants.KBN_DLVRY_HOHO_MOCHIKAERI.equals(dlvryhoho)) {
            meisaiBean.setHyojununt(blankRowEntity.getMochikaeriprice().toString());
        } else {
            meisaiBean.setHyojununt(blankRowEntity.getHyojunprice().toString());
        }

        // 各種単価情報取得処理
        meisaiBean = fillPriceInfo(meisaiBean, tokuisakicd, uridd);

        // サブ処理1
        meisaiBean = getBlankRowSub1(meisaiBean, blankRowEntity, dlvryhoho, uridd);

        // サブ処理2
        S02M046TEntity shopEntity = commonService.searchShop(apkihoncd, shopcd);
        meisaiBean = getBlankRowSub2(meisaiBean, slipBean.getDnpykbn(), blankRowEntity.getTaxkbn(),
            tokuisakikbn, uridd, shopEntity);

        // ※明細備考は新規追加セット取得SQLで取得しておらず、初期値は必ず空になる（旧システム踏襲）
        if (CommonConstants.KBN_KEIJO_CAMPAIGN.equals(meisaiBean.getKeijokbnmei())
            && StringUtil.isEmpty(blankRowEntity.getMeisaibiko())) {

            // 販促価格を取得
            BigDecimal hansokuunt = getUnt(blankRowEntity.getHansokustrtdd(),
                blankRowEntity.getHansokushrydd(), blankRowEntity.getHansokudlvryprice(),
                blankRowEntity.getHansokumochikaeriprice(), dlvryhoho, uridd);

            // 販促優先区分が "する"、かつ販促単価が null でない場合
            // 明細備考に販促名称を設定する
            if (CommonConstants.FLG_ON.equals(blankRowEntity.getHansokuyusenkbn())
                && (hansokuunt != null)) {
                meisaiBean.setMeisaibikomei(blankRowEntity.getHansokunm());
            }
        }
        // ポイントフラグが有効な場合は予定ポイントの初期値は0を設定する。無効の場合はブランク
        if (CommonConstants.FLG_ON.equals(baseBean.getPointflg())) {
            meisaiBean.setPointsu("0");
        }

        return meisaiBean;
    }

    /**
     * getBlankRow() のサブ処理1
     * 
     * @param meisaiBean C1002W01MeisaiBean
     * @param blankRowEntity S02V001TC1002W01UriageMeisaiBlankRowEntity
     * @param dlvryhoho 配達方法
     * @param uridd 売上日
     * @return
     */
    private C1002W01MeisaiBean getBlankRowSub1(C1002W01MeisaiBean meisaiBean,
        S02V001TC1002W01UriageMeisaiBlankRowEntity blankRowEntity, String dlvryhoho, String uridd) {

        // 計上区分が「経費」の場合、売上単価をショップ商品マスタの在庫単価に設定して終了
        if (CommonConstants.KBN_KEIJO_KEIHI.equals(meisaiBean.getKeijokbnmei())) {
            meisaiBean.setUnt1mei(blankRowEntity.getZaikount().toString());
            return meisaiBean;
        }

        // 計上区分が「売上」でない場合、売上単価に "0" を設定して終了
        if (!(CommonConstants.KBN_KEIJO_URIAGE.equals(meisaiBean.getKeijokbnmei()))) {
            meisaiBean.setUnt1mei("0");
            return meisaiBean;
        }

        // 得意先単価および得意先セット単価を取得する
        BigDecimal tokuisakiunt = null;
        if (CommonConstants.FLG_ON.equals(blankRowEntity.getRknkindtokubetsuuntflg())) {
            if (CommonConstants.STKBN_1.equals(blankRowEntity.getStkbn())) {
                // 得意先セット単価を取得
                tokuisakiunt = getUnt(blankRowEntity.getSttekiyostrtdd(),
                    blankRowEntity.getSttekiyoshrydd(), blankRowEntity.getStdlvryprice(),
                    blankRowEntity.getStmochikaeriprice(), dlvryhoho, uridd);
            } else {
                // 得意先単価を取得
                tokuisakiunt = getUnt(blankRowEntity.getTstmtekiyostrtdd(),
                    blankRowEntity.getTstmtekiyoshrydd(), blankRowEntity.getTstmdlvryprice(),
                    blankRowEntity.getTstmmochikaeriprice(), dlvryhoho, uridd);
            }
        }

        // 得意先単価および得意先セット単価が null でない場合、売上単価に設定して終了
        if (tokuisakiunt != null) {
            meisaiBean.setUnt1mei(tokuisakiunt.toString());
            return meisaiBean;
        }

        // 販促単価を取得する
        BigDecimal hansokuunt = getUnt(blankRowEntity.getHansokustrtdd(),
            blankRowEntity.getHansokushrydd(), blankRowEntity.getHansokudlvryprice(),
            blankRowEntity.getHansokumochikaeriprice(), dlvryhoho, uridd);

        // 計上区分が「キャンペーン」の場合
        if (CommonConstants.KBN_KEIJO_CAMPAIGN.equals(meisaiBean.getKeijokbnmei())) {
            // 販促単価が null でない場合、売上単価に販促単価を設定して終了
            if (hansokuunt != null) {
                meisaiBean.setUnt1mei(hansokuunt.toString());
            } else {
                // 販促単価が null の場合、売上単価に 0 を設定して終了
                meisaiBean.setUnt1mei("0");
            }
            return meisaiBean;
        }

        // 販促単価が null でない
        // かつ販促優先区分が「する」の場合
        if ((!StringUtils.isEmpty(hansokuunt))
            && (CommonConstants.FLG_ON.equals(blankRowEntity.getHansokuyusenkbn()))) {
            // 計上区分に「キャンペーン」、売上単価に販促単価をそれぞれ設定して終了
            meisaiBean.setKeijokbnmei(CommonConstants.KBN_KEIJO_CAMPAIGN);
            meisaiBean.setUnt1mei(hansokuunt.toString());
            return meisaiBean;
        }

        // 価格区分による単価設定
        if (Objects.equals(CommonConstants.KBN_PRICE_HYOJUN, blankRowEntity.getPricekbn())) {
            // 標準価格
            // 配達方法が「持ち帰り」の場合、売上単価に持ち帰り価格を設定して終了
            if (CommonConstants.KBN_DLVRY_HOHO_MOCHIKAERI.equals(dlvryhoho)) {
                meisaiBean.setUnt1mei(util.getString(blankRowEntity.getMochikaeriprice()));
            } else {
                // それ以外の場合は配達価格を設定する
                meisaiBean.setUnt1mei(util.getString(blankRowEntity.getHyojunprice()));
            }
        } else {
            // オープン価格
            // 配達方法が「持ち帰り」の場合、売上単価に持ち帰り価格を設定して終了
            if (CommonConstants.KBN_DLVRY_HOHO_MOCHIKAERI.equals(dlvryhoho)) {
                meisaiBean.setUnt1mei(util.getString(blankRowEntity.getOpnmochikaeriprice()));
            } else {
                // それ以外の場合は配達価格を設定する
                meisaiBean.setUnt1mei(util.getString(blankRowEntity.getOpnprice()));
            }
        }

        return meisaiBean;
    }

    /**
     * getBlankRow() のサブ処理2
     * 
     * @param meisaiBean C1002W01MeisaiBean
     * @param dnpykbn 伝票区分
     * @param taxkbn 税区分
     * @param tokuisakikbn 得意先区分
     * @param uridd 売上日
     * @param shopEntity S02M046TEntity
     * @return
     */
    private C1002W01MeisaiBean getBlankRowSub2(C1002W01MeisaiBean meisaiBean, String dnpykbn,
        String taxkbn, String tokuisakikbn, String uridd, S02M046TEntity shopEntity) {

        // 初期表示価格の設定
        meisaiBean.setShokidsplyprice(meisaiBean.getUnt1mei());

        // 消費税、金額の算出（売上）
        String[] uriagePriceList = taxService.getPriceList(meisaiBean.getUnt1mei(),
            meisaiBean.getSuryo1mei(), taxkbn, tokuisakikbn, uridd, shopEntity);

        if (CommonConstants.KBN_KEIHI_KONYU.equals(dnpykbn)
            || CommonConstants.KBN_KEIHI_NOMI.equals(dnpykbn)
            || CommonConstants.KBN_KEIHI_SYUKKO.equals(dnpykbn)) {
            meisaiBean.setUntshohitaxkin("0");
            meisaiBean.setShohitaxkin("0");
        } else {
            meisaiBean.setUntshohitaxkin(uriagePriceList[0]);
            meisaiBean.setShohitaxkin(uriagePriceList[1]);
        }

        meisaiBean.setKin1mei(uriagePriceList[2]);

        // 消費税、金額の算出（仕入）
        String[] siirePriceList = taxService.getPriceList(meisaiBean.getUnt2mei(),
            meisaiBean.getSuryo2mei(), taxkbn, tokuisakikbn, uridd, shopEntity);

        meisaiBean.setSiireuntshohitaxkin(siirePriceList[0]);

        return meisaiBean;
    }

    /**
     * 計上区分のデフォルト値取得処理
     * 
     * @param tokuisakikbn 得意先区分
     * @param acptkbn 受注区分
     * @param lctgrycd 大分類コード
     * @param mctgrycd 中分類コード
     * @return
     */
    private String getDefaultKeijokbn(String tokuisakikbn, String acptkbn, String lctgrycd,
        String mctgrycd) {

        // 得意先区分が「経費」の場合は「経費」を返す
        if (CommonConstants.KBN_TOKUISAKI_KEIHI.equals(tokuisakikbn)) {
            return CommonConstants.KBN_KEIJO_KEIHI;
        }

        // 「サーバー関連用」の大分類コードを取得
        String serverlcd =
            s02m006tService.searchForMoji(CommonConstants.APCD_000, CommonConstants.SYS_SHOPCD_000,
                CommonConstants.SYS_SETTEINO_096, CommonConstants.SETTEICD_096_4);

        // 「サーバー関連用」の大分類コードが取得できない
        // または、大分類コードが「サーバー関連用」の大分類コードと一致しない場合は「売上」を返す
        if (StringUtil.isEmpty(serverlcd) || !(serverlcd.equals(lctgrycd))) {
            return CommonConstants.KBN_KEIJO_URIAGE;
        }

        // 「サーバー関連本体」の中分類コードを取得
        String servermcd =
            s02m006tService.searchForMoji(CommonConstants.APCD_000, CommonConstants.SYS_SHOPCD_000,
                CommonConstants.SYS_SETTEINO_134, CommonConstants.SETTEICD_134_41);

        // 「サーバー関連本体」の中分類コードが取得できない
        // または、中分類コードが「サーバー関連本体」の中分類コードと一致しない場合は「売上」を返す
        if (StringUtil.isEmpty(servermcd) || !(servermcd.equals(mctgrycd))) {
            return CommonConstants.KBN_KEIJO_URIAGE;
        }

        // 受注区分が「商品受注」でない場合は「レンタル」を返す
        if (!(CommonConstants.KBN_ACPT_SHOHIN.equals(acptkbn))) {
            return CommonConstants.KBN_KEIJO_RENTAL;
        }

        // 得意先区分が「ショップ」の場合は「サブレンタル」を返す
        if (CommonConstants.KBN_TOKUISAKI_SHOP.equals(tokuisakikbn)) {
            return CommonConstants.KBN_KEIJO_SUBRENTAL;
        }

        // いずれの条件にも当てはまらない場合、「売上」を返す
        return CommonConstants.KBN_KEIJO_URIAGE;
    }

    /**
     * 配達／持ち帰りを判定して単価を返す処理
     * 
     * @param tekiyostrtdd 適用開始日
     * @param tekiyoshrydd 適用終了日
     * @param dlvryprice 配達価格
     * @param mochikaeriprice 持ち帰り価格
     * @param dlvryhoho 配達方法
     * @param uridd 売上日
     * @return
     */
    private BigDecimal getUnt(String tekiyostrtdd, String tekiyoshrydd, BigDecimal dlvryprice,
        BigDecimal mochikaeriprice, String dlvryhoho, String uridd) {

        if (!(DateTimeUtil.checkDatePeriod(uridd, tekiyostrtdd, tekiyoshrydd))) {
            return null;
        }

        if (CommonConstants.KBN_DLVRY_HOHO_MOCHIKAERI.equals(dlvryhoho)) {
            return mochikaeriprice;
        }

        return dlvryprice;
    }

    /**
     * 消費税金額計算処理
     * 
     * @param calcTaxDto 消費税金額計算用DTO
     * @return 消費税金額
     */
    public String calcTax(C1002W01CalcTaxDto calcTaxDto) {

        String taxkin = BigDecimal.ZERO.stripTrailingZeros().toPlainString(); // 消費税金額

        // 商品コード、数量、単価のいずれかが空の場合は0を返して終了
        if (StringUtils.isEmpty(calcTaxDto.getShncd()) || StringUtils.isEmpty(calcTaxDto.getSuryo())
            || StringUtils.isEmpty(calcTaxDto.getUnt())) {
            return taxkin;
        }

        // 調整用商品コードを取得
        String cyoseishncd = commonService.getZeichoseihncd();

        // 調整用商品コードと商品コードが一致する場合は0を返して終了
        if (calcTaxDto.getShncd().equals(cyoseishncd)) {
            return taxkin;
        }

        // 得意先マスタ取得
        S02M013TEntity tokuisakiEntity = tokuisakiService.search(calcTaxDto.getApkihoncd(),
            calcTaxDto.getShopcd(), calcTaxDto.getTokuisakicd());

        // 得意先が存在しない場合は0を返して終了
        if (Objects.isNull(tokuisakiEntity)) {
            return taxkin;
        }

        // ショップマスタ取得
        S02M046TEntity shopEntity =
            commonService.searchShop(calcTaxDto.getApkihoncd(), calcTaxDto.getShopcd());

        // 商品マスタビュー取得
        S02V001TEntity shnEntity = commonService.searchShohin(calcTaxDto.getApkihoncd(),
            calcTaxDto.getShopcd(), calcTaxDto.getShncd());

        // 税区分取得
        String taxkbn = CommonConstants.KBN_KEIJO_KEIHI.equals(calcTaxDto.getKeijokbn())
            ? CommonConstants.KBN_TAX_HIKAZEI
            : util.isFlagged(shnEntity.getTaxsetteikbn()) ? shnEntity.getTaxkbn() : null;

        // 消費税の計算
        String[] kinPriceList = taxService.getPriceList(calcTaxDto.getUnt(), calcTaxDto.getSuryo(),
            taxkbn, tokuisakiEntity.getTokuisakikbn(), calcTaxDto.getUridd(), shopEntity);

        taxkin = kinPriceList[1];
        return taxkin;
    }

    /**
     * 売上伝票のチェック
     * 
     * @param form C1002W01Form
     */
    public void uridnpynoCheck(C1002W01Form form) {
        C1002W01BaseBean baseBean = form.getBaseBean();

        // 処理区分が「取消」の場合のみチェックを実施
        if (CommonConstants.KBN_PRCSKBN1_SHINKI.equals(baseBean.getPrcskbn())) {
            return;
        }

        // 売上テーブル検索
        S06T001TEntity entity = uriageService.search(baseBean.getApkihoncd(), baseBean.getShopcd(),
            baseBean.getUridnpyno());

        if (ObjectUtils.isEmpty(entity)) {
            throw BusinessExceptionFactory.validationFailed(MessageID.E002);
        }

        // 取得した売上伝票の得意先コードと、画面から入力した得意先コードが一致しない場合はエラー
        if (!(entity.getTokuisakicd().equals(form.getKttcd()))) {
            List<FormField> fields = List.of(new FormField("slipBean.uridd"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W120, "得意先コード",
                "売上伝票データと同一得意先コード");
        }
    }

    /**
     * 売上日のチェック
     * 
     * @param form C1002W01Form
     * @param unyodate String
     */
    public void uriddCheck(C1002W01Form form, String unyodate) {
        C1002W01BaseBean baseBean = form.getBaseBean();

        // 処理区分が「新規」の場合のみチェックを実施
        if (CommonConstants.KBN_PRCSKBN1_TORIKESI.equals(baseBean.getPrcskbn())) {
            return;
        }

        String uridd = form.getSlipBean().getUridd();

        // 売上日が空の場合はエラー
        if (StringUtils.isEmpty(uridd)) {
            List<FormField> fields = List.of(new FormField("slipBean.uridd"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W011, "売上日");
        }

        // 売上日が日付形式（YYYY/MM/DD）でない場合はエラー
        if (!uridd.matches(ValidationConstants.REGEXP_YYYY_MM_DD_SLASH)) {
            List<FormField> fields = List.of(new FormField("slipBean.uridd"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W016, "売上日");
        }

        // 売上日が 1900/01/01 ～ 9999/12/31 の範囲外の場合はエラー
        if (!DateTimeUtil.isDateBetween(uridd, CommonConstants.YEARMONTH_MIN,
            CommonConstants.YEARMONTH_MAX)) {
            List<FormField> fields = List.of(new FormField("slipBean.uridd"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W033, "売上日",
                CommonConstants.DATE_MIN, CommonConstants.DATE_MAX);
        }

        // 過去日数を取得
        int from = s02m006tService.searchForSuchi(baseBean.getApkihoncd(), baseBean.getShopcd(),
            CommonConstants.SYS_SETTEINO_039, CommonConstants.SETTEICD_039_1);

        // 未来日数を取得
        int to = s02m006tService.searchForSuchi(baseBean.getApkihoncd(), baseBean.getShopcd(),
            CommonConstants.SYS_SETTEINO_039, CommonConstants.SETTEICD_039_2);

        // 運用日付を元にした許容過去日・未来日の生成
        LocalDate baseDate = LocalDate.parse(unyodate, FORMATTER_YYYYMMDD);
        LocalDate pastDate = baseDate.minusDays(from);
        LocalDate futureDate = baseDate.plusDays(to);
        // 売上日
        LocalDate salesDate =
            LocalDate.parse(DateTimeUtil.toNoSlashedDate(uridd), FORMATTER_YYYYMMDD);
        // 売上日が許容過去日・未来日の範囲外の場合はエラー
        if (salesDate.isBefore(pastDate) || salesDate.isAfter(futureDate)) {
            throw BusinessExceptionFactory.validationFailed(new FormField("slipBean.uridd", "売上日"),
                MessageID.W115, "売上日", String.format("%s日前", from), String.format("%s日先", to));
        }
    }

    /**
     * 入荷元担当者コードのチェック
     * 
     * @param form C1002W01Form
     */
    public void jissekitnshacd1Check(C1002W01Form form) {
        C1002W01BaseBean baseBean = form.getBaseBean();

        // 処理区分が「新規」の場合のみチェックを実施
        if (CommonConstants.KBN_PRCSKBN1_TORIKESI.equals(baseBean.getPrcskbn())) {
            return;
        }

        C1002W01TransportBean transportBean = form.getTransportBean();
        String jissekitnshacd1 = transportBean.getJissekitnshacd1();

        // 入荷元担当者コードが空でない場合のみチェックを実施
        if (StringUtils.isEmpty(jissekitnshacd1)) {
            return;
        }

        // 入荷元担当者コードが数値以外の場合はエラー
        if (!jissekitnshacd1.matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS)) {
            List<FormField> fields = List.of(new FormField("transportBean.jissekitnshacd1"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W013, "実績担当者コード（入荷元）",
                "0～9のみ");
        }

        // 担当者マスタ検索
        S02M003TEntity entity = commonService.searchTnsha(baseBean.getApkihoncd(),
            baseBean.getShopcd(), jissekitnshacd1);

        // 検索結果が存在しない場合はエラー
        if (ObjectUtils.isEmpty(entity)) {
            List<FormField> fields = List.of(new FormField("transportBean.jissekitnshacd1"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W084,
                "実績担当者コード（入荷元）");
        }

        // 有効でない場合はエラー
        if (!(CommonConstants.YUKOKBN_1.equals(entity.getYukokbn()))) {
            List<FormField> fields = List.of(new FormField("transportBean.jissekitnshacd1"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W123,
                "実績担当者コード（入荷元）");
        }
    }

    /**
     * 倉庫コードのチェック
     * 
     * @param form C1002W01Form
     */
    public void sokocdCheck(C1002W01Form form) {
        C1002W01BaseBean baseBean = form.getBaseBean();

        // 処理区分が「新規」の場合のみチェックを実施
        if (CommonConstants.KBN_PRCSKBN1_TORIKESI.equals(baseBean.getPrcskbn())) {
            return;
        }

        // 伝票区分が「売上出庫」「経費出庫」の場合のみチェックを実施
        String dnpykbn = form.getSlipBean().getDnpykbn();
        if (!(CommonConstants.KBN_URIAGE_SYUKKO.equals(dnpykbn)
            || CommonConstants.KBN_KEIHI_SYUKKO.equals(dnpykbn))) {
            return;
        }

        C1002W01TransportBean transportBean = form.getTransportBean();
        String sokocd = transportBean.getSokocd();

        // 倉庫コードが空の場合はエラー
        if (StringUtils.isEmpty(sokocd)) {
            List<FormField> fields = List.of(new FormField("transportBean.sokocd"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W011, "倉庫コード");
        }

        // 倉庫コードが数値以外の場合はエラー
        if (!sokocd.matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS)) {
            List<FormField> fields = List.of(new FormField("transportBean.sokocd"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W013, "倉庫コード",
                "0～9のみ");
        }

        // 倉庫マスタ検索
        S02M041TEntity entity =
            commonService.searchSoko(baseBean.getApkihoncd(), baseBean.getShopcd(), sokocd);

        // 検索結果が存在しない場合はエラー
        if (ObjectUtils.isEmpty(entity)) {
            List<FormField> fields = List.of(new FormField("transportBean.sokocd"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W084, "倉庫コード");
        }

        // 有効でない場合はエラー
        if (!(CommonConstants.YUKOKBN_1.equals(entity.getYukokbn()))) {
            List<FormField> fields = List.of(new FormField("transportBean.sokocd"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W123, "倉庫コード");
        }
    }

    /**
     * 出荷先担当者コードのチェック
     * 
     * @param form C1002W01Form
     */
    public void jissekitnshacd2Check(C1002W01Form form) {
        C1002W01BaseBean baseBean = form.getBaseBean();

        // 処理区分が「新規」の場合のみチェックを実施
        if (CommonConstants.KBN_PRCSKBN1_TORIKESI.equals(baseBean.getPrcskbn())) {
            return;
        }

        C1002W01TransportBean transportBean = form.getTransportBean();
        String jissekitnshacd2 = transportBean.getJissekitnshacd2();

        // 出荷先担当者コードが空でない場合のみチェックを実施
        if (StringUtils.isEmpty(jissekitnshacd2)) {
            return;
        }

        // 出荷先担当者コードが数値以外の場合はエラー
        if (!jissekitnshacd2.matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS)) {
            List<FormField> fields = List.of(new FormField("transportBean.jissekitnshacd2"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W013, "実績担当者コード（出荷先）",
                "0～9のみ");
        }

        // 担当者マスタ検索
        S02M003TEntity entity = commonService.searchTnsha(baseBean.getApkihoncd(),
            baseBean.getShopcd(), jissekitnshacd2);

        // 検索結果が存在しない場合はエラー
        if (ObjectUtils.isEmpty(entity)) {
            List<FormField> fields = List.of(new FormField("transportBean.jissekitnshacd2"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W084,
                "実績担当者コード（出荷先）");
        }

        // 有効でない場合はエラー
        if (!(CommonConstants.YUKOKBN_1.equals(entity.getYukokbn()))) {
            List<FormField> fields = List.of(new FormField("transportBean.jissekitnshacd2"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W123,
                "実績担当者コード（出荷先）");
        }
    }

    /**
     * 営業担当者コードのチェック
     * 
     * @param form C1002W01Form
     */
    public void eigyotnshacdCheck(C1002W01Form form) {
        C1002W01BaseBean baseBean = form.getBaseBean();

        // 処理区分が「新規」の場合のみチェックを実施
        if (CommonConstants.KBN_PRCSKBN1_TORIKESI.equals(baseBean.getPrcskbn())) {
            return;
        }

        C1002W01TransportBean transportBean = form.getTransportBean();
        String eigyotnshacd = transportBean.getEigyotnshacd();

        // 営業担当者コードが空でない場合のみチェックを実施
        if (StringUtils.isEmpty(eigyotnshacd)) {
            return;
        }

        // 営業担当者コードが数値以外の場合はエラー
        if (!eigyotnshacd.matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS)) {
            List<FormField> fields = List.of(new FormField("transportBean.eigyotnshacd"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W013, "営業担当者コード",
                "0～9のみ");
        }

        // 担当者マスタ検索
        S02M003TEntity entity =
            commonService.searchTnsha(baseBean.getApkihoncd(), baseBean.getShopcd(), eigyotnshacd);

        // 検索結果が存在しない場合はエラー
        if (ObjectUtils.isEmpty(entity)) {
            List<FormField> fields = List.of(new FormField("transportBean.eigyotnshacd"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W084, "営業担当者コード");
        }

        // 有効でない場合はエラー
        if (!(CommonConstants.YUKOKBN_1.equals(entity.getYukokbn()))) {
            List<FormField> fields = List.of(new FormField("transportBean.eigyotnshacd"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W123, "営業担当者コード");
        }

        // 営業担当者でない場合はエラー
        if (!(CommonConstants.FLG_ON.equals(entity.getEigyotnshakbn()))) {
            List<FormField> fields = List.of(new FormField("transportBean.eigyotnshacd"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W112, "営業担当者コード");
        }
    }

    /**
     * 配達担当者コードのチェック
     * 
     * @param form C1002W01Form
     */
    public void dlvrytnshacdCheck(C1002W01Form form) {
        C1002W01BaseBean baseBean = form.getBaseBean();

        // 処理区分が「新規」の場合のみチェックを実施
        if (CommonConstants.KBN_PRCSKBN1_TORIKESI.equals(baseBean.getPrcskbn())) {
            return;
        }

        C1002W01TransportBean transportBean = form.getTransportBean();
        String dlvrytnshacd = transportBean.getDlvrytnshacd();

        // 配達担当者コードが空でない場合のみチェックを実施
        if (StringUtils.isEmpty(dlvrytnshacd)) {
            return;
        }

        // 配達担当者コードが数値以外の場合はエラー
        if (!dlvrytnshacd.matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS)) {
            List<FormField> fields = List.of(new FormField("transportBean.dlvrytnshacd"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W013, "配達担当者コード",
                "0～9のみ");
        }

        // 担当者マスタ検索
        S02M003TEntity entity =
            commonService.searchTnsha(baseBean.getApkihoncd(), baseBean.getShopcd(), dlvrytnshacd);

        // 検索結果が存在しない場合はエラー
        if (ObjectUtils.isEmpty(entity)) {
            List<FormField> fields = List.of(new FormField("transportBean.dlvrytnshacd"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W084, "配達担当者コード");
        }

        // 有効でない場合はエラー
        if (!(CommonConstants.YUKOKBN_1.equals(entity.getYukokbn()))) {
            List<FormField> fields = List.of(new FormField("transportBean.dlvrytnshacd"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W123, "配達担当者コード");
        }

        // 配達担当者でない場合はエラー
        if (!(CommonConstants.FLG_ON.equals(entity.getDlvrytnshakbn()))) {
            List<FormField> fields = List.of(new FormField("transportBean.dlvrytnshacd"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W112, "配達担当者コード");
        }
    }

    /**
     * 伝票備考のチェック
     * 
     * @param 伝票備考 String
     */
    public void bikoCheck(String biko) {
        if (StringUtils.isEmpty(biko)) {
            return;
        }

        // 備考の桁数（バイト数）チェック
        if (!ItemUtil.isByteSizeValid(biko, 100)) {
            List<FormField> fields = List.of(new FormField("slipBean.biko"));
            throw BusinessExceptionFactory.validationFailed(fields, MessageID.W009);
        }
    }

    /**
     * 与信限度金額チェック
     * 
     * @param form C1002W01Form
     */
    public void yoshinCheck(C1002W01Form form) {

        C1002W01BaseBean baseBean = form.getBaseBean();

        String apkihoncd = baseBean.getApkihoncd();
        String shopcd = baseBean.getShopcd();
        String tokuisakicd = form.getKttcd();

        // 得意先マスタ検索
        S02M013TEntity entity1 = tokuisakiService.search(apkihoncd, shopcd, tokuisakicd);

        if (ObjectUtils.isEmpty(entity1)) {
            return;
        }

        // 得意先実績テーブル検索
        S02M050TEntity entity2 = tokuisakiJissekiService.search(apkihoncd, shopcd, tokuisakicd);

        if (ObjectUtils.isEmpty(entity2)) {
            return;
        }

        // ACCIS2023-7193 得意先マスタ.与信限度額がNULL（未設定）の場合は与信限度額チェック不要
        // …与信限度額がNULLの場合は0の場合と同じとして、与信限度額チェックを行わないようにする
        long yoshingendokin = Optional.ofNullable(entity1.getYoshingendokin()).orElse(0L);
        if (yoshingendokin == 0L) {
            return;
        }

        // ACCIS2023-7193 得意先実績テーブルの現在残高および計上前受注金額も項目的にNULLを許容しているので与信限度額と同様に対応
        long genzaizd = Optional.ofNullable(entity2.getGenzaizd()).orElse(0);
        long keijomaeacptkin = Optional.ofNullable(entity2.getKeijomaeacptkin()).orElse(0L);
        long kin = Long.parseLong(form.getCountBean().getUrikin1hidden());

        // 現在残高と計上前受注金額と今回受注金額の和が与信限度額を超えている場合はエラー
        if (yoshingendokin < (genzaizd + keijomaeacptkin + kin)) {
            throw BusinessExceptionFactory.validationFailed(MessageID.W150, "与信限度額");
        }
    }

    /**
     * 更新前明細チェック
     * 
     * @param form C1002W01Form
     */
    public void checkMeisaiForUpdate(C1002W01Form form) {

        C1002W01BaseBean baseBean = form.getBaseBean();
        C1002W01SlipBean slipBean = form.getSlipBean();
        List<C1002W01MeisaiBean> meisaiBeanList = form.getMeisaiBeanList();

        // 明細行が0件の場合はエラー（「新規」の場合のみ）
        // 「取消」の場合、明細行0件チェックは更新チェックの先頭で実施（旧システム踏襲）
        if (ObjectUtils.isEmpty(meisaiBeanList) || meisaiBeanList.size() == 0) {
            if (CommonConstants.KBN_PRCSKBN1_SHINKI.equals(baseBean.getPrcskbn())) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W104);
            }
        }

        String tokuisakikbn = baseBean.getTokuisakikbn();
        String prcskbn = baseBean.getPrcskbn();
        String dnpykbn = slipBean.getDnpykbn();
        String uridd = DateTimeUtil.toNoSlashedDate(slipBean.getUridd());

        // 売上数量をチェックするかどうかのフラグ
        boolean suryo1CheckFlg = (dnpykbn.equals(CommonConstants.KBN_KEIHI_KONYU)
            || dnpykbn.equals(CommonConstants.KBN_URIAGE_NOMI)
            || dnpykbn.equals(CommonConstants.KBN_KEIHI_NOMI)
            || dnpykbn.equals(CommonConstants.KBN_URIAGE_SYUKKO)
            || dnpykbn.equals(CommonConstants.KBN_KEIHI_SYUKKO)
            || dnpykbn.equals(CommonConstants.KBN_SIIRE_URIAGE)) ? true : false;

        // 仕入数量をチェックするかどうかのフラグ
        boolean suryo2CheckFlg = (dnpykbn.equals(CommonConstants.KBN_URIAGE_SYUKKO)
            || dnpykbn.equals(CommonConstants.KBN_KEIHI_SYUKKO)
            || dnpykbn.equals(CommonConstants.KBN_SIIRE_URIAGE)) ? true : false;

        // 大分類コード（サーバー）
        String serverLctgrycd = commonService.getServerLctgrycd();

        // 中分類コード（サーバー）
        String serverMctgrycd = commonService.getServerMctgrycd();

        // 大分類コード（製品水）
        String waterLctgrycd = commonService.getWaterLctgrycd();

        // 明細行を1件ずつ処理する
        int index = 0;
        for (C1002W01MeisaiBean meisaiBean : meisaiBeanList) {

            // 入力行でない場合はスキップする
            if (CommonConstants.FLG_OFF.equals(meisaiBean.getNyuryokumeisaiflg())) {
                index++;
                continue;
            }

            // 処理区分が「新規」の場合
            if (CommonConstants.KBN_PRCSKBN1_SHINKI.equals(prcskbn)) {

                // 売上数量が空または "0" の場合はエラー
                String suryo1 = meisaiBean.getSuryo1mei();
                if (suryo1CheckFlg && (StringUtils.isEmpty(suryo1) || "0".equals(suryo1))) {
                    List<FormField> fields =
                        List.of(new FormField("meisaiBeanList[" + index + "].suryo1mei"));
                    throw BusinessExceptionFactory.validationFailed(fields, MessageID.W113, "上段数量");
                }

                // 売上数量に数字、マイナス、小数点以外が含まれる場合はエラー
                if (!StringUtils.isEmpty(suryo1) && !ItemUtil.removeComma(suryo1)
                    .matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS_DECIMAL)) {
                    List<FormField> fields =
                        List.of(new FormField("meisaiBeanList[" + index + "].suryo1mei"));
                    throw BusinessExceptionFactory.validationFailed(fields, MessageID.W039,
                        index + 1, "売上数量", "数字とマイナス記号と小数点");
                }

                // 仕入数量が空または "0" の場合はエラー
                String suryo2 = meisaiBean.getSuryo2mei();
                if (suryo2CheckFlg && (StringUtils.isEmpty(suryo2) || "0".equals(suryo2))) {
                    List<FormField> fields =
                        List.of(new FormField("meisaiBeanList[" + index + "].suryo2mei"));
                    throw BusinessExceptionFactory.validationFailed(fields, MessageID.W113, "下段数量");
                }

                // 仕入数量に数字、マイナス、小数点以外が含まれる場合はエラー
                if (!StringUtils.isEmpty(suryo2) && !ItemUtil.removeComma(suryo2)
                    .matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS_DECIMAL)) {
                    List<FormField> fields =
                        List.of(new FormField("meisaiBeanList[" + index + "].suryo2mei"));
                    throw BusinessExceptionFactory.validationFailed(fields, MessageID.W039,
                        index + 1, "仕入数量", "数字とマイナス記号と小数点");
                }

                // 売上単価に数字、マイナス、小数点以外が含まれる場合はエラー
                String unt1 = meisaiBean.getUnt1mei();
                if (!ItemUtil.removeComma(unt1)
                    .matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS_DECIMAL)) {
                    List<FormField> fields =
                        List.of(new FormField("meisaiBeanList[" + index + "].unt1mei"));
                    throw BusinessExceptionFactory.validationFailed(fields, MessageID.W039,
                        index + 1, "売上単価", "数字とマイナス記号と小数点");
                }

                // 売上金額に数字、マイナス、小数点以外が含まれる場合はエラー
                String kin1 = meisaiBean.getKin1mei();
                if (!ItemUtil.removeComma(kin1)
                    .matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS_DECIMAL)) {
                    List<FormField> fields =
                        List.of(new FormField("meisaiBeanList[" + index + "].kin1mei"));
                    throw BusinessExceptionFactory.validationFailed(fields, MessageID.W039,
                        index + 1, "売上金額", "数字とマイナス記号と小数点");
                }

                // 売上金額が 8 桁を超える場合はエラー
                if (!StringUtils.isEmpty(kin1) && ItemUtil.removeComma(kin1).length() > 8) {
                    List<FormField> fields =
                        List.of(new FormField("meisaiBeanList[" + index + "].kin1mei"));
                    throw BusinessExceptionFactory.validationFailed(fields, MessageID.W039,
                        index + 1, "売上金額", "8桁以下");
                }

                // 売上消費税金額が 8 桁を超える場合はエラー
                String shohitaxkinn1 = meisaiBean.getShohitaxkinn1mei();
                if (!StringUtils.isEmpty(shohitaxkinn1)
                    && ItemUtil.removeComma(shohitaxkinn1).length() > 8) {
                    List<FormField> fields =
                        List.of(new FormField("meisaiBeanList[" + index + "].shohitaxkinn1mei"));
                    throw BusinessExceptionFactory.validationFailed(fields, MessageID.W039,
                        index + 1, "売上消費税金額", "8桁以下");
                }

                // 区分関係チェックを実施
                String keijokbn = meisaiBean.getKeijokbnmei();
                if (!checkRelations(tokuisakikbn, meisaiBean.getLctgrycd(),
                    meisaiBean.getMctgrycd(), serverLctgrycd, serverMctgrycd, waterLctgrycd,
                    keijokbn)) {
                    List<FormField> fields =
                        List.of(new FormField("meisaiBeanList[" + index + "].keijokbnmei"));
                    throw BusinessExceptionFactory.validationFailed(fields, MessageID.W123, "計上区分");
                }

                // 売上計上区分が「キャンペーン」かつセット商品の場合はエラー
                String stkbn = meisaiBean.getStkbn();
                if (CommonConstants.KBN_KEIJO_CAMPAIGN.equals(keijokbn)
                    && CommonConstants.FLG_ON.equals(stkbn)) {
                    List<FormField> fields =
                        List.of(new FormField("meisaiBeanList[" + index + "].shncdmei"));
                    throw BusinessExceptionFactory.validationFailed(fields, MessageID.W124,
                        "計上区分がキャンペーンの場合はセット商品");
                }

                // 商品マスタ検索
                S02V001TEntity entity = commonService.searchShohin(baseBean.getApkihoncd(),
                    baseBean.getShopcd(), meisaiBean.getShncdmei());

                // 商品の有効性チェックを実施し、無効の場合はエラー
                if (!S02V001TEntityDaoEx.isValidShohin(entity, uridd)) {
                    throw BusinessExceptionFactory.validationFailed(MessageID.W123, "商品コード");
                }

                // 売上数量の小数点有効性チェックを実施し、無効の場合はエラー
                if (!S02V001TEntityDaoEx.isValidSuryo(entity, meisaiBean.getSuryo1mei())) {
                    List<FormField> fields =
                        List.of(new FormField("meisaiBeanList[" + index + "].suryo1mei"));
                    throw BusinessExceptionFactory.validationFailed(fields, MessageID.W120, "上段数量",
                        "整数");
                }

                // 仕入数量の小数点有効性チェックを実施し、無効の場合はエラー
                if (!S02V001TEntityDaoEx.isValidSuryo(entity, meisaiBean.getSuryo2mei())) {
                    List<FormField> fields =
                        List.of(new FormField("meisaiBeanList[" + index + "].suryo2mei"));
                    throw BusinessExceptionFactory.validationFailed(fields, MessageID.W120, "下段数量",
                        "整数");
                }

                // 伝票区分が「経費購入」「仕入売上」のいずれかの場合
                if (dnpykbn.equals(CommonConstants.KBN_KEIHI_KONYU)
                    || dnpykbn.equals(CommonConstants.KBN_SIIRE_URIAGE)) {

                    // 取扱区分が「売上仕入両方」でない場合エラー
                    if (!CommonConstants.KBN_TORIATSUKAI_URIAGESHIIRE
                        .equals(entity.getTrtskikbn())) {
                        throw BusinessExceptionFactory.validationFailed(MessageID.W124,
                            "商品コードの取扱区分：売上仕入以外");
                    }

                } else {
                    // 取扱区分が「仕入のみ」の場合エラー
                    if (CommonConstants.KBN_TORIATSUKAI_SHIIRE.equals(entity.getTrtskikbn())) {
                        throw BusinessExceptionFactory.validationFailed(MessageID.W124,
                            "商品コードの取扱区分：仕入のみ");
                    }
                }

                // 明細備考の桁数（バイト数）チェック
                String meisaibiko = meisaiBean.getMeisaibikomei();
                if (!StringUtils.isEmpty(meisaibiko)) {
                    if (!ItemUtil.isByteSizeValid(meisaibiko, 40)) {
                        List<FormField> fields =
                            List.of(new FormField("meisaiBeanList[" + index + "].meisaibikomei"));
                        throw BusinessExceptionFactory.validationFailed(fields, MessageID.W009);
                    }
                }
            }

            // 消費税調整の場合のチェック → 相関チェック不要と設計書に記載があるため削除

            index++;
        }
    }

    /**
     * 区分関係チェック
     * 
     * @param tokuisakikbn 得意先区分
     * @param lctgrycd 大分類コード
     * @param mctgrycd 中分類コード
     * @param serverLctgrycd 大分類コード（サーバー）
     * @param serverMctgrycd 中分類コード（サーバー）
     * @param waterLctgrycd 中分類コード（製品水）
     * @param keijokbn 売上計上区分
     * @return
     */
    private boolean checkRelations(String tokuisakikbn, String lctgrycd, String mctgrycd,
        String serverLctgrycd, String serverMctgrycd, String waterLctgrycd, String keijokbn) {

        switch (tokuisakikbn) {
            // 得意先区分が「顧客」「NA受託」「配達受託」いずれかの場合
            case CommonConstants.KBN_TOKUISAKI_KOKUAKU:
            case CommonConstants.KBN_TOKUISAKI_NAJUTAKU:
            case CommonConstants.KBN_TOKUISAKI_DLVRYJUTAKU:
                // 大分類コード、中分類コードがいずれも「サーバー」の場合
                if (serverLctgrycd.equals(lctgrycd) && serverMctgrycd.equals(mctgrycd)) {
                    return (CommonConstants.KBN_KEIJO_URIAGE.equals(keijokbn)
                        || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(keijokbn)) ? true : false;
                }

                // 大分類コードが「製品水」の場合
                if (waterLctgrycd.equals(lctgrycd)) {
                    return (CommonConstants.KBN_KEIJO_URIAGE.equals(keijokbn)
                        || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(keijokbn)
                        || CommonConstants.KBN_KEIJO_SERVICE.equals(keijokbn)
                        || CommonConstants.KBN_KEIJO_PROMO.equals(keijokbn)
                        || CommonConstants.KBN_KEIJO_SHIIN.equals(keijokbn)) ? true : false;
                }

                // 大分類コードが「製品水」以外の場合
                return (CommonConstants.KBN_KEIJO_URIAGE.equals(keijokbn)
                    || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(keijokbn)
                    || CommonConstants.KBN_KEIJO_SERVICE.equals(keijokbn)
                    || CommonConstants.KBN_KEIJO_PROMO.equals(keijokbn)
                    || CommonConstants.KBN_KEIJO_RENTAL.equals(keijokbn)) ? true : false;

            // 得意先区分が「ショップ」の場合
            case CommonConstants.KBN_TOKUISAKI_SHOP:
                // 大分類コード、中分類コードがいずれも「サーバー」の場合
                if (serverLctgrycd.equals(lctgrycd) && serverMctgrycd.equals(mctgrycd)) {
                    return (CommonConstants.KBN_KEIJO_URIAGE.equals(keijokbn)
                        || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(keijokbn)
                        || CommonConstants.KBN_KEIJO_SUBRENTAL.equals(keijokbn)) ? true : false;
                }

                // 大分類コードが「製品水」の場合
                if (waterLctgrycd.equals(lctgrycd)) {
                    return (CommonConstants.KBN_KEIJO_URIAGE.equals(keijokbn)
                        || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(keijokbn)
                        || CommonConstants.KBN_KEIJO_SERVICE.equals(keijokbn)
                        || CommonConstants.KBN_KEIJO_PROMO.equals(keijokbn)
                        || CommonConstants.KBN_KEIJO_SHIIN.equals(keijokbn)
                        || CommonConstants.KBN_KEIJO_MAINT.equals(keijokbn)) ? true : false;
                }

                // 大分類コードが「製品水」以外の場合
                return (CommonConstants.KBN_KEIJO_URIAGE.equals(keijokbn)
                    || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(keijokbn)
                    || CommonConstants.KBN_KEIJO_SERVICE.equals(keijokbn)
                    || CommonConstants.KBN_KEIJO_PROMO.equals(keijokbn)
                    || CommonConstants.KBN_KEIJO_SUBRENTAL.equals(keijokbn)
                    || CommonConstants.KBN_KEIJO_RENTAL.equals(keijokbn)) ? true : false;

            // 得意先区分が「AP」「ACI」いずれかの場合
            case CommonConstants.KBN_TOKUISAKI_AP:
            case CommonConstants.KBN_TOKUISAKI_ACI:
                // 大分類コード、中分類コードがいずれも「サーバー」の場合
                if (serverLctgrycd.equals(lctgrycd) && serverMctgrycd.equals(mctgrycd)) {
                    return (CommonConstants.KBN_KEIJO_URIAGE.equals(keijokbn)
                        || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(keijokbn)) ? true : false;
                }

                // 大分類コード、中分類コードいずれかが「サーバー」でない場合
                return (CommonConstants.KBN_KEIJO_URIAGE.equals(keijokbn)
                    || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(keijokbn)
                    || CommonConstants.KBN_KEIJO_SERVICE.equals(keijokbn)) ? true : false;

            // 得意先区分が「経費」の場合
            case CommonConstants.KBN_TOKUISAKI_KEIHI:
                return (CommonConstants.KBN_KEIJO_KEIHI.equals(keijokbn)) ? true : false;

            default:
                return true;
        }
    }

    /**
     * 請求締処理状態チェック
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     */
    public void checkSeikyuShimeJotai(String apkihoncd, String shopcd) {
        S07T004TEntity entity = commonService.searchSeikyuShimeShoriKanri(apkihoncd, shopcd);

        if (ObjectUtils.isEmpty(entity)) {
            return;
        }

        if (CommonConstants.PRCSJOTAI_0.equals(entity.getPrcsjotai())) {
            return;
        }

        throw BusinessExceptionFactory.validationFailed(MessageID.W260, "処理中です。しばらくお待ちください");
    }

    /**
     * ポイントのチェック
     * 
     * @param form C1002W01Form
     */
    public void checkGamenPoint(C1002W01Form form) {

        // 商品コード:099990の件数
        int gamenPointCnt = 0;

        // 画面発生ポイントのデータ
        C1002W01MeisaiBean gamenPointBean = null;

        // 明細の中に商品コード:099990が１件以上ある場合に以下のチェックを実施
        for (C1002W01MeisaiBean meisaiBean :form.getMeisaiBeanList()) {
            if (CommonConstants.SHNCD_099990.equals(meisaiBean.getShncdmei())) {
                gamenPointCnt++;
                gamenPointBean = meisaiBean;

                // ２件以上ある場合はエラー終了
                if (gamenPointCnt >= 2) {
                    throw BusinessExceptionFactory.validationFailed(MessageID.W260,
                            "商品コード099990は複数指定できません");
                }
            }
        }

        if (gamenPointCnt >= 1) {
            String apkihoncd = form.getBaseBean().getApkihoncd();
            String shopcd = form.getBaseBean().getShopcd();
            String tokuisakicd = form.getKttcd();

            // ショップポイントマスタのポイント制度有無チェック
            S02M202TEntity shopPointEntity = commonService.searchShopPoint(apkihoncd, shopcd);
            // ポイント制度参加有無が「0:なし」の場合は処理終了
            if (CommonConstants.POINTSYSTEMJOINKBN_0.equals(shopPointEntity.getPointsystemjoinkbn())) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W260,
                        "ポイント制度に参加していないためポイントの付与は行えません");
            }

            // 得意先ポイントマスタのポイント
            S02M203TEntity tokuisakiPointEntity = commonService.searchTokuisakiPoint(apkihoncd, shopcd, tokuisakicd);
            // ポイント管理有無またはマイアクア会員登録有無のいずれかが、「0:なし」の場合は処理終了
            if (CommonConstants.POINTKANRIUMU_0.equals(tokuisakiPointEntity.getPointkanriumu())
                    || CommonConstants.MYAQUAKAIININPTUMU_0.equals(tokuisakiPointEntity.getMyaquakaiininptumu())) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W260,
                        "得意先がポイント付与対象ではないためポイントの付与は行えません");
            }

            // 予定ポイントが空または "0" の場合はエラー
            String yoteiPoint = gamenPointBean.getPointsu();
            if (StringUtils.isEmpty(yoteiPoint) || "0".equals(yoteiPoint)) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W113, "予定ポイント");
            }

            // 予定ポイントに数字、マイナス、小数点以外が含まれる場合はエラー
            if (!StringUtils.isEmpty(yoteiPoint) && !util.removeComma(yoteiPoint)
                .matches(ValidationConstants.REGEXP_HALF_WIDTH_NUMBERS_DECIMAL)) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W039,
                    1, "予定ポイント", "数字とマイナス記号と小数点");
            }

            int pointsu = Integer.parseInt(util.removeComma(gamenPointBean.getPointsu()));

            // ポイント上限数の取得
            Integer pointZyougen = getZyougenPointsu(CommonConstants.APCD_000, CommonConstants.SYS_SHOPCD_000,
                    "952");

            // 値が空の場合はデフォルト値5000を設定
            if (pointZyougen == null) {
                pointZyougen = 5000;
            }

            // 入力された予定ポイントがポイント数上限以上ならエラー
            if (pointsu > pointZyougen) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W023, "予定ポイント", pointZyougen);
            }
        }
    }

    /**
     * 登録・更新処理
     * 
     * @param form C1002W01Form
     * @param dbManagementSession DbManagementSession
     * @param maxDisplayCount 最大明細行数
     * @return uridnpyno 売上伝票番号
     */
    @Transactional
    public String execUpdateProcess(C1002W01Form form, DbManagementSession dbManagementSession,
        int maxDisplayCount) {

        C1002W01BaseBean baseBean = form.getBaseBean();
        C1002W01SlipBean slipBean = form.getSlipBean();
        C1002W01TransportBean transportBean = form.getTransportBean();

        String prcskbn = baseBean.getPrcskbn();

        String dnpykbn = "";
        if (CommonConstants.KBN_PRCSKBN1_SHINKI.equals(prcskbn)) {
            dnpykbn = slipBean.getDnpykbn();
        }

        // 得意先マスタ検索
        S02M013TEntity s02m013tEntity =
            tokuisakiService.search(baseBean.getApkihoncd(), baseBean.getShopcd(), form.getKttcd());

        // 請求先マスタ検索
        // 回収方法区分を取得
        S02M017TEntity seikyusakiEntity = commonService.searchSkysaki(baseBean.getApkihoncd(),
            baseBean.getShopcd(), s02m013tEntity.getSkysakicd());
        String kaishuhohokbn = seikyusakiEntity.getKaishuhohokbn();

        // インボイス施行済み判定結果を取得
        S02M046TEntity shopEntity =
            commonService.searchShop(baseBean.getApkihoncd(), baseBean.getShopcd());
        String uridd = StringUtils.isEmpty(form.getSlipBean().getUridd()) ? null
            : DateTimeUtil.toNoSlashedDate(form.getSlipBean().getUridd());
        boolean invoiceEnforced =
            commonService.isInvoiceEnforced(shopEntity.getInvoicshikoymd(), uridd);

        // 売上Entityを生成
        S06T001TEntity uriageEntity =
            createS06t001tEntity(form, dbManagementSession, kaishuhohokbn, invoiceEnforced);

        // 売上伝票を登録（新規：黒伝票、取消：赤伝票）
        uriageService.insert(uriageEntity);

        List<S06T002TEntity> uriageMeisaiList = new ArrayList<>();

        // ベースとなる売上明細リストを生成
        List<S06T002TEntity> baseUriageMeisaiList = new ArrayList<>();
        if (CommonConstants.KBN_PRCSKBN1_SHINKI.equals(prcskbn)) {
            baseUriageMeisaiList = uriageMeisaiService.createBaseUriageMeisaiList(form,
                dbManagementSession, uriageEntity, s02m013tEntity, kaishuhohokbn, invoiceEnforced);

            // 明細展開処理を実行
            // ACCIS2023-7117 引数追加対応（得意先マスタ.料金種別特別単価フラグ）
            uriageMeisaiList = uriageMeisaiService.expandUriageMeisai(form, uriageEntity,
                baseUriageMeisaiList, dbManagementSession.getUnyoDate(), invoiceEnforced,
                kaishuhohokbn, maxDisplayCount, s02m013tEntity.getRknkindtokubetsuuntflg());

        } else {
            uriageMeisaiList = uriageMeisaiService.createCancelUriageMeisaiList(form,
                dbManagementSession, uriageEntity, kaishuhohokbn, invoiceEnforced);
        }

        // 売上明細を登録
        uriageMeisaiService.insertUriageMeisai(uriageMeisaiList);

        // 回収方法区分が「代引」かつ処理区分が「新規」の場合は消費税情報登録を行う
        if ((CommonConstants.KBN_KAISHUHOHO_DAIBIKI.equals(kaishuhohokbn))
            && (CommonConstants.KBN_PRCSKBN1_SHINKI.equals(prcskbn))) {

            // 消費税情報登録（売上明細テーブル）
            // 課税料金合計と軽減税率料金合計の和を取得
            int taxkinSum = uriageMeisaiService.insertTax(uriageEntity, uriageMeisaiList,
                s02m013tEntity, dbManagementSession);

            uriageService.updateTax(uriageEntity, taxkinSum, dbManagementSession);
        }

        // 現在残高更新（得意先実績テーブル）
        tokuisakiJissekiService.updateGenzaiZandaka(uriageEntity, dbManagementSession,
            kaishuhohokbn);

        // 処理区分が「取消」の場合
        if (CommonConstants.KBN_PRCSKBN1_TORIKESI.equals(prcskbn)) {
            // 取消対象となる伝票の売上伝票番号
            String kuroUridnpyno = form.getBaseBean().getUridnpyno();

            // 黒伝票の更新（売上テーブル）
            uriageService.updateKuroDenpyo(uriageEntity, kuroUridnpyno, dbManagementSession);
        }

        if (CommonConstants.KBN_URIAGE_SYUKKO.equals(dnpykbn)
            || CommonConstants.KBN_KEIHI_SYUKKO.equals(dnpykbn)
            || !StringUtils.isEmpty(transportBean.getSokocd())) {

            if (CommonConstants.KBN_PRCSKBN1_TORIKESI.equals(prcskbn)) {
                // キャンセル処理（入出庫実績テーブル）
                nyushukkoJissekiService.cancel(uriageEntity,
                    Long.parseLong(form.getCountBean().getSiirekin2hidden()), dbManagementSession);

                // キャンセル処理（入出庫実績明細テーブル）
                nyushukkoJissekiMeisaiService.cancel(uriageEntity, dbManagementSession);
            } else {
                // 登録処理（入出庫実績テーブル）
                String nyukamotojissekitnshacd = transportBean.getJissekitnshacd1();
                nyushukkoJissekiService.insert(uriageEntity,
                    Long.parseLong(form.getCountBean().getSiirekin2hidden()),
                    nyukamotojissekitnshacd, dbManagementSession);

                // 登録処理（入出庫実績明細テーブル）
                nyushukkoJissekiMeisaiService.insert(uriageEntity, uriageMeisaiList,
                    dbManagementSession);
            }

            // 売上明細リストの要素分、現在庫テーブル、在庫テーブル、在庫入出庫明細テーブルの登録を行う
            execUpdateZaikoNyuShukko(dnpykbn, uriageEntity, uriageMeisaiList, dbManagementSession);
        }

        // 得意先マスタ更新
        tokuisakiService.update(uriageEntity, dbManagementSession);

        // 売上日
        String uriDate = uriageEntity.getUridd();
        // 処理種別
        String prockind = CommonConstants.Z2401B01_PROC_BULK;
        // ポイントフラグ
        boolean pointFlg = CommonConstants.FLG_ON.equals(form.getBaseBean().getPointflg());
        // ポイントフラグが有効の場合のみ実行
        if (pointFlg) {
            // ポイント情報部品引数ワークテーブルへ登録を行う（管理番号採番用）
            insertPointWork(dbManagementSession, uriageMeisaiList, null, true);
            // 管理番号採番
            String kanrino = runNumberingAndGetKanrino(dbManagementSession, uriageEntity,
                s02m013tEntity.getTokuisakicd(), uriDate, form.getBaseBean().getApkihoncd(),
                form.getBaseBean().getShopcd());
            // ポイント情報部品引数ワークテーブルへ登録を行う（一括計算用）
            insertPointWork(dbManagementSession, uriageMeisaiList, form.getBaseBean().getPointsu(),
                false);
            // 共通部品呼び出し
            callPoint(dbManagementSession, uriageEntity, s02m013tEntity.getTokuisakicd(), uriDate,
                prockind, form.getBaseBean().getApkihoncd(), form.getBaseBean().getShopcd());
            // 共通部品実行結果を各予定ポイントテーブルに登録
            insertPointResults(dbManagementSession, uriageEntity, uriDate, kanrino);
        }
        return uriageEntity.getUridnpyno();
    }

    /**
     * 在庫関連テーブルの登録・更新を行う
     * 
     * @param uriageEntity S06T001TEntity
     * @param meisaiEntityList List<S06T002TEntity>
     * @param dbManagementSession dbManagementSession
     */
    public void execUpdateZaikoNyuShukko(String dnpykbn, S06T001TEntity uriageEntity,
        List<S06T002TEntity> meisaiEntityList, DbManagementSession dbManagementSession) {

        // 在庫関連テーブルを登録・更新するかどうかのフラグ
        boolean updateFlg = true;

        // ショップマスタ検索
        S02M046TEntity s02m046tEntity =
            commonService.searchShop(uriageEntity.getApkihoncd(), uriageEntity.getShopcd());

        // ショップマスタが取得できない場合
        if (ObjectUtils.isEmpty(s02m046tEntity)) {
            updateFlg = false;
        }

        // ショップマスタの在庫管理区分が「無し」の場合
        if (CommonConstants.ZAIKOKANRIKBN_0.equals(s02m046tEntity.getZaikokanrikbn())) {
            updateFlg = false;
        }

        // 運用日付 > 入出庫締日の場合
        if (Long.parseLong(uriageEntity.getUridd()) > Long
            .parseLong(dbManagementSession.getUnyoDate())) {
            updateFlg = false;
        }

        // 在庫関連テーブルを登録・更新しない場合
        if (!updateFlg) {
            return;
        }

        // 在庫締年月日算出
        String shimedydm = commonService.getShimedydm(uriageEntity.getUridd(),
            s02m046tEntity.getZaikoprcsdydm(), s02m046tEntity.getShimedd());

        // 在庫関連テーブルを登録・更新する場合
        for (S06T002TEntity meisaiEntity : meisaiEntityList) {
            // 商品マスタ検索
            S02V001TEntity s02v001tEntity = commonService.searchShohin(meisaiEntity.getApkihoncd(),
                meisaiEntity.getShopcd(), meisaiEntity.getShncd());

            // 商品マスタ検索結果が空の場合
            if (ObjectUtils.isEmpty(s02v001tEntity)) {
                continue;
            }

            // 商品マスタの在庫管理対象フラグが「在庫管理しない」の場合
            if (CommonConstants.ZAIKOKANRITRGTFLG_0.equals(s02v001tEntity.getZaikokanritrgtflg())) {
                continue;
            }

            // 登録・更新（現在庫テーブル）
            genZaikoService.upsert(uriageEntity, meisaiEntity, dbManagementSession);

            // 伝票区分
            if (StringUtils.isEmpty(dnpykbn)) {
                if (uriageEntity.getTorihikikbn().equals(CommonConstants.KBN_TORIHIKI_URIAGE)) {
                    dnpykbn = CommonConstants.KBN_URIAGE_SYUKKO;
                } else {
                    dnpykbn = CommonConstants.KBN_KEIHI_SYUKKO;
                }
            }

            // 登録・更新（在庫テーブル）
            zaikoService.upsert(dnpykbn, uriageEntity, meisaiEntity, shimedydm,
                dbManagementSession);

            // 登録（在庫入出庫明細テーブル）
            zaikoNyushukkoMeisaiService.insert(dnpykbn, uriageEntity, meisaiEntity, shimedydm,
                dbManagementSession);
        }

    }

    /**
     * 売上テーブル登録Entity生成
     * 
     * @param form C1002W01Form
     * @param dbManagementSession DbManagementSession
     * @param kaishuhohokbn 回収方法区分
     * @param invoiceEnforced インボイス施行済み判定
     */
    private S06T001TEntity createS06t001tEntity(C1002W01Form form,
        DbManagementSession dbManagementSession, String kaishuhohokbn, boolean invoiceEnforced) {

        // 各種Bean
        C1002W01BaseBean baseBean = form.getBaseBean();
        C1002W01SlipBean slipBean = form.getSlipBean();
        C1002W01TransportBean transportBean = form.getTransportBean();
        C1002W01CountBean countBean = form.getCountBean();

        String apkihoncd = baseBean.getApkihoncd();
        String shopcd = baseBean.getShopcd();
        String tokuisakicd = form.getKttcd();
        String prcskbn = baseBean.getPrcskbn();

        // 得意先マスタ検索
        S02M013TEntity s02m013tEntity = tokuisakiService.search(apkihoncd, shopcd, tokuisakicd);

        // ショップマスタ検索
        S02M046TEntity s02m046tEntity = commonService.searchShop(apkihoncd, shopcd);

        // 売上テーブルEntity
        S06T001TEntity entity = new S06T001TEntity();

        // AP基本コード
        entity.setApkihoncd(ItemUtil.nullToString(apkihoncd));

        // ショップコード
        entity.setShopcd(ItemUtil.nullToString(shopcd));

        // 売上伝票番号
        String uridnpyno = saibanService.getSlipNoUriage(apkihoncd, shopcd);
        entity.setUridnpyno(ItemUtil.nullToString(uridnpyno));

        // 枝番号
        entity.setEdano(Short.parseShort("0"));

        // 受注伝票番号
        entity.setAcptdnpyno("");

        // 受注伝票枝番号
        entity.setAcptdnpyedano(null);

        // 取引区分
        String dnpykbn = slipBean.getDnpykbn();
        if (CommonConstants.KBN_URIAGE_NOMI.equals(dnpykbn)
            || CommonConstants.KBN_URIAGE_SYUKKO.equals(dnpykbn)
            || CommonConstants.KBN_SIIRE_URIAGE.equals(dnpykbn)) {
            entity.setTorihikikbn(CommonConstants.KBN_TORIHIKI_URIAGE);
        } else if (CommonConstants.KBN_KEIHI_KONYU.equals(dnpykbn)
            || CommonConstants.KBN_KEIHI_NOMI.equals(dnpykbn)
            || CommonConstants.KBN_KEIHI_SYUKKO.equals(dnpykbn)) {
            entity.setTorihikikbn(CommonConstants.KBN_TORIHIKI_KEIHI);
        } else {
            entity.setTorihikikbn("");
        }

        // 得意先コード
        entity.setTokuisakicd(ItemUtil.nullToString(tokuisakicd));

        // 仕入先コード
        entity.setSiiresakicd("");

        // 入荷元コード
        entity.setNyukamotocd(ItemUtil.nullToString(transportBean.getSokocd()));

        // 売上日
        String uridd = StringUtils.isEmpty(slipBean.getUridd()) ? null
            : DateTimeUtil.toNoSlashedDate(slipBean.getUridd());
        entity.setUridd(ItemUtil.nullToString(uridd));

        // 受注日
        entity.setAcptdd("");

        // 取消の場合は売上金額、消費税金額、参考消費税金額をマイナスにする
        int sign = (CommonConstants.KBN_PRCSKBN1_SHINKI.equals(prcskbn)) ? 1 : -1;

        // 売上金額
        entity.setUrikin(Long.parseLong(countBean.getUrikin2hidden()) * sign);

        // 消費税金額
        // 参考消費税金額
        if (CommonConstants.KBN_PRCSKBN1_SHINKI.equals(prcskbn)) {

            if (CommonConstants.KBN_KAISHUHOHO_DAIBIKI.equals(kaishuhohokbn)) {
                // 代引きの場合（売上テーブル.消費税金額については、このあとupdateTaxで設定するのでここでは0円でよい）
                entity.setShohitaxkin(Long.parseLong("0"));
                entity
                    .setSankoushohitaxkin(Long.parseLong(countBean.getShohitaxkin1hidden()) * sign);
            } else if (invoiceEnforced) {
                // インボイス施行済みの場合
                entity.setShohitaxkin(Long.parseLong("0"));
                entity
                    .setSankoushohitaxkin(Long.parseLong(countBean.getShohitaxkin1hidden()) * sign);
            } else {
                // インボイス施行前の場合
                entity.setShohitaxkin(Long.parseLong(countBean.getShohitaxkin1hidden()) * sign);
                entity.setSankoushohitaxkin(Long.parseLong("0"));
            }

        } else {
            // ACCIS2023-6712 代引の売上取消時、消費税金額が0円になってしまう不具合の対応（消費税明細は画面に表示されないため）
            // →取消対象の売上データを取得し、その消費税および参考消費税の符号を反転させた値を設定する
            S06T001TEntity base = uriageService.search(apkihoncd, shopcd,
                ItemUtil.nullToString(baseBean.getUridnpyno()));
            entity.setShohitaxkin(base.getShohitaxkin() * sign);
            entity.setSankoushohitaxkin(Objects.isNull(base.getSankoushohitaxkin()) ? null
                : base.getSankoushohitaxkin() * sign);
        }

        // 受注担当者コード
        entity.setAcpttnshacd(ItemUtil.nullToString(baseBean.getAcpttnshacd()));

        // 配達担当者コード
        entity.setDlvrytnshacd(ItemUtil.nullToString(transportBean.getDlvrytnshacd()));

        // 配達管轄AP基本コード
        entity
            .setDlvrykankatsuapkihoncd(ItemUtil.nullToString(baseBean.getDlvrykankatsuapkihoncd()));

        // 配達管轄ショップコード
        entity.setDlvrykankatsushopcd(ItemUtil.nullToString(baseBean.getDlvrykankatsushopcd()));

        // 配達拠点コード
        entity.setDlvrykyotencd(ItemUtil.nullToString(baseBean.getDlvrykyotencd()));

        // 配達区分
        // ※売上テーブルの配達区分は 1（配達）もしくは 2（持ち帰り）のいずれかのみ
        String dlvryhoho = slipBean.getDlvryhoho();
        if (CommonConstants.KBN_DLVRY_HOHO_HAITATSU.equals(dlvryhoho)) {
            entity.setDlvrykbn("1");
        } else if (CommonConstants.KBN_DLVRY_HOHO_MOCHIKAERI.equals(dlvryhoho)) {
            entity.setDlvrykbn("2");
        } else {
            entity.setDlvrykbn("");
        }

        // 配達会社コード
        entity.setDlvrycmpnycd(ItemUtil.nullToString(baseBean.getDlvrycmpnycd()));

        // 出荷元区分
        entity.setShukkamotokbn("");

        // 営業担当者コード
        entity.setEigyotnshacd(ItemUtil.nullToString(transportBean.getEigyotnshacd()));

        // 実績担当者コード
        if (StringUtil.isEmpty(transportBean.getJissekitnshacd2())) {
            if (CommonConstants.URIKEIJOTNSHAKBN_2.equals(s02m013tEntity.getUrikeijotnshakbn())) {
                entity.setJissekitnshacd(ItemUtil.nullToString(s02m013tEntity.getEigyotnshacd()));

            } else {
                entity.setJissekitnshacd(ItemUtil.nullToString(s02m013tEntity.getDlvrytnshacd()));
            }

        } else {
            entity.setJissekitnshacd(ItemUtil.nullToString(transportBean.getJissekitnshacd2()));
        }

        // 請求先コード
        entity.setSkysakicd(ItemUtil.nullToString(s02m013tEntity.getSkysakicd()));

        // 請求締年月
        entity.setSkyshimedydm("");

        // 請求締日
        entity.setSkyshimedd("");

        // 請求処理実施日
        // 代引きの場合も "00000000" を設定するよう変更（インボイス対応）
        entity.setSkyprcsjissidd(CommonConstants.DEF_DATE_00000000);

        // 月次締年月
        entity.setGetsujishimedydm("");

        // 月次締日
        entity.setGetsujishimedd("");

        // 月次処理実施日
        entity.setGetsujiprcsjissidd(CommonConstants.DEF_DATE_00000000);

        // 売上締処理日
        entity.setUrishimeprcsdd(ItemUtil.nullToString(uridd));

        // 値引締年月
        entity.setNebikishimedydm("");

        // 値引締日
        entity.setNebikishimedd("");

        // 値引処理実施日
        entity.setNebikiprcsjissidd(CommonConstants.DEF_DATE_00000000);

        // 伝票備考
        entity.setDnpybiko(ItemUtil.nullToString(slipBean.getBiko()));

        // 伝票番号
        if (CommonConstants.KBN_PRCSKBN1_SHINKI.equals(prcskbn)) {
            entity.setDnpyno("");
        } else {
            entity.setDnpyno(ItemUtil.nullToString(baseBean.getUridnpyno()));
        }

        // 請求書出力有無区分
        if (CommonConstants.KBN_PRCSKBN1_TORIKESI.equals(prcskbn)
            && CommonConstants.FLG_ON.equals(slipBean.getSkyshootptarinashikbn())) {
            entity.setSkyshootptarinashikbn(CommonConstants.FLG_ON);
        } else {
            entity.setSkyshootptarinashikbn(CommonConstants.FLG_OFF);
        }

        // 請求処理実施区分
        entity.setSkyprcsjissikbn(Short.parseShort("0"));

        // データ作成元区分
        entity.setDatsksmotokbn(CommonConstants.KBN_SAKUSEIMOTO_URIAGENYURYOKU);

        // 税計算区分
        String taxkeisankbn = ItemUtil.getItem(s02m046tEntity.getGenztaxkeisankbnuri(),
            s02m046tEntity.getShintaxkeisankbnuri(), s02m046tEntity.getTaxkeisankbnurikirikaedd(),
            dbManagementSession.getUnyoDate());
        entity.setTaxkeisankbn(ItemUtil.nullToString(taxkeisankbn));

        // 仕入伝票番号
        entity.setSiirednpyno(uridnpyno);

        // 入金伝票番号
        entity.setNykndnpyno("");

        // 入金金額
        entity.setNyknkin(null);

        // 入金種別
        entity.setNyknkind("");

        // 有効区分
        entity.setYukokbn(CommonConstants.YUKOKBN_1);

        // 登録者情報
        entity.setToshaapkihoncd(ItemUtil.nullToString(dbManagementSession.getApkihoncd()));
        entity.setToshashopcd(ItemUtil.nullToString(dbManagementSession.getShopcd()));
        entity.setToshacd(ItemUtil.nullToString(dbManagementSession.getTnshacd()));
        entity.setInptsystmymd(ItemUtil.nullToString(dbManagementSession.getSystemDate()));
        entity.setInptsystmjikan(ItemUtil.nullToString(dbManagementSession.getSystemTime()));
        entity.setInptunyoymd(ItemUtil.nullToString(dbManagementSession.getUnyoDate()));
        entity.setInptipadrse(ItemUtil.nullToString(dbManagementSession.getIpAddress()));
        entity.setInptpgmid(ItemUtil.nullToString(dbManagementSession.getProgramId()));

        // 更新者情報
        entity.setKoshaapkihoncd(ItemUtil.nullToString(dbManagementSession.getApkihoncd()));
        entity.setKoshashopcd(ItemUtil.nullToString(dbManagementSession.getShopcd()));
        entity.setKoshacd(ItemUtil.nullToString(dbManagementSession.getTnshacd()));
        entity.setKshnsystmymd(ItemUtil.nullToString(dbManagementSession.getSystemDate()));
        entity.setKshnsystmjikan(ItemUtil.nullToString(dbManagementSession.getSystemTime()));
        entity.setKshnunyoymd(ItemUtil.nullToString(dbManagementSession.getUnyoDate()));
        entity.setKshnipadrse(ItemUtil.nullToString(dbManagementSession.getIpAddress()));
        entity.setKshnpgmid(ItemUtil.nullToString(dbManagementSession.getProgramId()));

        return entity;
    }

    /**
     * 明細Beanに各種価格情報を設定します。<br>
     * <br>
     * <ul>
     * <li>標準価格</li>
     * <li>ショップ価格（価格区分により標準またはオープン）</li>
     * <li>販促価格（販促区分がONの場合のみ）</li>
     * <li>得意先別特別価格（料金種別特別単価フラグがONの場合のみ）</li>
     * <li>得意先別特別価格リスト（料金種別特別単価フラグがONの場合に全期間分、JSON形式文字列で設定）</li>
     * </ul>
     * 
     * @param detail 明細Bean
     * @param tokuisakicd 得意先コード
     * @param uridd 売上日
     * @return 明細Bean
     */
    private C1002W01MeisaiBean fillPriceInfo(C1002W01MeisaiBean detail, String tokuisakicd,
        String uridd) {
        String apkihoncd = detail.getApkihoncd();
        String shopcd = detail.getShopcd();
        String shncd = detail.getShncdmei();
        // 得意先情報取得
        C0801W02TokuisakiMasterDto customer =
            customerService.getTokuisaki(apkihoncd, shopcd, tokuisakicd);
        // 商品情報取得
        C0801W02ShnDto shn = shnService.getShnInfo(apkihoncd, shopcd, shncd);
        // 標準価格情報取得
        C0801W02SwitchableShnPriceDto hyojunPriceDto = shn.getHyojunPriceDto(uridd);
        // オープン価格情報取得
        C0801W02SwitchableShnPriceDto openPriceDto = shn.getOpenPriceDto(uridd);
        // 販促価格情報取得
        C0801W02RangeShnPriceDto campaignPriceDto = shn.getCampaignPriceDto(uridd);
        // 得意先別特別単価情報リスト取得
        List<C0801W02RangeShnPriceDto> specialPriceDtoList =
            customerService.getTokubetuUnt(apkihoncd, shopcd, tokuisakicd, uridd, shncd,
                customer.getRknkindtokubetsuuntflg(), detail.getTanpinmeisaiflg());
        // 各種価格DTOから明細Beanの各価格関連項目への設定
        // 標準価格
        detail.setHyojunpricekirikaedd(hyojunPriceDto.getKakakukirikaedd());
        detail.setGenzhyojunprice(hyojunPriceDto.getGenzprice());
        detail.setGenzhyojunmochikaeriprice(hyojunPriceDto.getGenzmochikaeriprice());
        detail.setShinhyojunprice(hyojunPriceDto.getShinprice());
        detail.setShinhyojunmochikaeriprice(hyojunPriceDto.getShinmochikaeriprice());
        // ショップ価格（標準またはオープン、価格区分に応じて設定）
        C0801W02SwitchableShnPriceDto shopPriceDto =
            Objects.equals(CommonConstants.KBN_PRICE_HYOJUN, detail.getPricekbn()) ? hyojunPriceDto
                : openPriceDto;
        detail.setShoppricekirikaedd(shopPriceDto.getKakakukirikaedd());
        detail.setGenzshopprice(shopPriceDto.getGenzprice());
        detail.setGenzshopmochikaeriprice(shopPriceDto.getGenzmochikaeriprice());
        detail.setShinshopprice(shopPriceDto.getShinprice());
        detail.setShinshopmochikaeriprice(shopPriceDto.getShinmochikaeriprice());
        // 販促価格（販促区分による）
        detail.setHansokukbn(shn.getHansokukbn());
        if (util.isFlagged(shn.getHansokukbn())) {
            detail.setHansokustrtdd(campaignPriceDto.getStrtdd());
            detail.setHansokushrydd(campaignPriceDto.getShrydd());
            detail.setHansokuprice(campaignPriceDto.getDlvrypriceForce());
            detail.setHansokumochikaeriprice(campaignPriceDto.getMochikaeripriceForce());
        }
        // 得意先別特別単価（料金種別特別単価フラグによる）
        detail.setRknkindtokubetsuuntflg(customer.getRknkindtokubetsuuntflg());
        if (util.isFlagged(customer.getRknkindtokubetsuuntflg())) {
            Optional<C0801W02RangeShnPriceDto> specialPriceDto = specialPriceDtoList.stream()
                // 売上日が適用開始日より以降（適用開始日を含む）
                .filter(d -> util.isLater(uridd, d.getStrtdd()))
                // 終了日が売上日より以降（売上日を含む）
                .filter(d -> util.isLater(d.getShrydd(), uridd))
                // 要素を取得
                .findFirst();
            // 適用する特別価格がある場合は明細に設定
            specialPriceDto.ifPresent(priceDto -> {
                detail.setTokubetsustrtdd(priceDto.getStrtdd());
                detail.setTokubetsushrydd(priceDto.getShrydd());
                detail.setTokubetsuprice(priceDto.getDlvrypriceForce());
                detail.setTokubetsumochikaeriprice(priceDto.getMochikaeripriceForce());
            });
            // 全期間の特別価格情報を明細Beanに設定
            detail.setTokubetsuPriceList(specialPriceDtoList);
        }
        return detail;
    }

    /**
     * 売上入力画面における、明細ごとの売上金額の合計値と、売上金額テーブルに保持されている合計金額が一致するかを検証します。<br>
     * 不一致の場合は、業務例外（ValidationFailed）をスローし、整合性エラーとして扱います。<br>
     * <br>
     * チェック内容：<br>
     * <ul>
     * <li>明細の売上金額（文字列でカンマ区切りされた値）を数値化し、全明細分を合算</li>
     * <li>売上テーブル側の金額（文字列）を数値化</li>
     * <li>両者が完全一致（BigDecimal#equals を使用）するかを検証</li>
     * </ul>
     * ※数値の桁数（スケール）も一致していないとエラーとなります（例：100.0 ≠ 100.00）<br>
     * <br>
     * このチェックは、売上整合性エラーの原因特定するために追加しました。
     *
     * @param form 売上入力フォーム（明細および合計金額情報を含む）
     * @throws BusinessException 売上金額に不一致がある場合（MessageID.W260）
     */
    public void checkIfSalesTotalMatchesDetail(C1002W01Form form) {

        // １．画面に入力された明細毎のの合計値をすべて合計した値を出す
        BigDecimal totalDetailSale = form.getMeisaiBeanList().stream()
            .map(C1002W01MeisaiBean::getKin1mei).map(ItemUtil::removeComma).map(BigDecimal::new)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 集計金額をコンソールで確認する
        log.debug("明細の集計金額＝{}", totalDetailSale);

        // ２．String型である売上テーブルの合計をBigDecimalに変換し型一致をさせる
        BigDecimal tableTotalSale = new BigDecimal(form.getCountBean().getUrikin2hidden());

        // 売上テーブルの合計をコンソールで確認する
        log.debug("売上テーブルの合計金額＝{}", tableTotalSale);

        // ３．画面の明細の合計と売上テーブルの合計が一致しない場合はエラーを発生させる
        // ３．チェックは完全一致でTrueとしており、100.0と100.00の場合もエラーが出る
        if (!(totalDetailSale.equals(tableTotalSale))) {
            throw BusinessExceptionFactory.validationFailed(MessageID.W260,
                "売上金額の整合性が取れていません。システム部にご連絡ください。");
        }
    }

    /**
     * ポイント情報部品引数ワークテーブルへの登録処理
     * 
     * @param sess DbManagementSession
     * @param meisaiList List<S06T002TEntity>
     */
    @Transactional
    protected void insertPointWork(DbManagementSession sess, List<S06T002TEntity> meisaiList,
        String yoteiPoint, boolean isSaiban) {

        // ポイント情報部品引数ワークテーブル登録前のクリーン処理
        S20W002TEntityExample example = new S20W002TEntityExample();
        S20W002TEntityExample.Criteria criteria = example.createCriteria();
        criteria.andTnshaapkihoncdEqualTo(sess.getApkihoncd())
            .andTnshashopcdEqualTo(sess.getShopcd()).andTnshacdEqualTo(sess.getTnshacd());

        s20w002tDao.deleteByExample(example);

        // ポイント情報部品処理結果ワークテーブルのクリーン処理
        S20W003TEntityExample resultExample = new S20W003TEntityExample();
        S20W003TEntityExample.Criteria resultCriteria = resultExample.createCriteria();
        resultCriteria.andTnshaapkihoncdEqualTo(sess.getApkihoncd())
            .andTnshashopcdEqualTo(sess.getShopcd()).andTnshacdEqualTo(sess.getTnshacd());

        s20w003tDao.deleteByExample(resultExample);

        int gyobangou = 1;

        for (S06T002TEntity m : meisaiList) {
            // 展開行の場合はスキップする
            if (CommonConstants.FLG_OFF.equals(m.getNyuryokumeisaiflg())) {
                continue;
            }
            // 引数ワーク登録用
            S20W002TEntity entity = new S20W002TEntity();
            entity.setApkihoncd(m.getApkihoncd());
            entity.setShopcd(m.getShopcd());
            entity.setTnshaapkihoncd(sess.getApkihoncd());
            entity.setTnshashopcd(sess.getShopcd());
            entity.setTnshacd(sess.getTnshacd());
            entity.setIpadrse(sess.getIpAddress());
            entity.setPrcsid(sess.getProgramId());
            entity.setGyobangou(String.valueOf(gyobangou++));
            entity.setDnpymeisaino(m.getUridnpymeisaino());
            entity.setDnpymeisaiedano(m.getUridnpymeisaiedano());
            entity.setShncd(m.getShncd());
            entity.setKeijokbn(m.getKeijokbn());
            entity.setShnunt(m.getUnt());
            entity.setSuryo(m.getSuryo());
            entity.setMeisaibiko(m.getMeisaibiko());
            entity.setYukokbn(CommonConstants.YUKOKBN_1);

            // ACCIS2023-7433 横並び対応（登録・更新関連の共通項目への値設定漏れ対応）
            entity.setToshaapkihoncd(sess.getApkihoncd());
            entity.setToshashopcd(sess.getShopcd());
            entity.setToshacd(sess.getTnshacd());
            entity.setInptsystmymd(sess.getSystemDate());
            entity.setInptsystmjikan(sess.getSystemTime());
            entity.setInptunyoymd(sess.getUnyoDate());
            entity.setInptipadrse(sess.getIpAddress());
            entity.setInptpgmid(sess.getProgramId());
            entity.setKoshaapkihoncd(sess.getApkihoncd());
            entity.setKoshashopcd(sess.getShopcd());
            entity.setKoshacd(sess.getTnshacd());
            entity.setKshnsystmymd(sess.getSystemDate());
            entity.setKshnsystmjikan(sess.getSystemTime());
            entity.setKshnunyoymd(sess.getUnyoDate());
            entity.setKshnipadrse(sess.getIpAddress());
            entity.setKshnpgmid(sess.getProgramId());

            s20w002tDao.insertSelective(entity);

            if (isSaiban) {
                // 管理番号採番の場合、1件だけ登録後に処理を終了する
                break;
            }
        }
        String insertPoint = ItemUtil.removeComma(yoteiPoint).trim();
        // 予定ポイントがnullでない、かつ1以上の場合、画面から発生した予定ポイントの明細行を登録する
        if (!StringUtils.isEmpty(insertPoint) && Integer.parseInt(insertPoint) > 0) {
            s20w002tDao.insertSelective(
                createPointRow(sess, meisaiList.get(0), gyobangou, Integer.parseInt(insertPoint)));
        }

    }

    /**
     * ポイント情報部品バッチ実行
     * 
     * @param sess DbManagementSession
     * @param uriageEntity S06T001TEntity
     * @param tokuisakicd
     * @param uriDate
     * @param prockind
     */
    private void callPoint(DbManagementSession sess, S06T001TEntity headerOrTemp,
        String tokuisakicd, String uriDate, String prockind, String apkihoncd, String shopcd) {

        // 共通部品実行パラメータを設定
        Z2401B01Entity z2401b01Entity = new Z2401B01Entity();
        z2401b01Entity.setApKihoncd(apkihoncd);
        z2401b01Entity.setShopcd(shopcd);
        z2401b01Entity.setUserApKihoncd(sess.getApkihoncd());
        z2401b01Entity.setUserShopcd(sess.getShopcd());
        z2401b01Entity.setUserTnshacd(sess.getTnshacd());
        z2401b01Entity.setIpadrse(sess.getIpAddress());
        z2401b01Entity.setPrcsid(sess.getProgramId());
        z2401b01Entity.setProckind(prockind);
        z2401b01Entity.setTokuisakicd(tokuisakicd);
        z2401b01Entity.setDnpykind(CommonConstants.KBN_SAKUSEIMOTO_URIAGENYURYOKU);
        z2401b01Entity.setChumondd(uriDate);
        z2401b01Entity.setDlvryytidd(null);
        z2401b01Entity.setDlvryKibojikankbn(null);
        z2401b01Entity.setDlvrykbn(headerOrTemp.getDlvrykbn());
        z2401b01Entity.setAcptrcptKeirokbn(null);
        // 共通部品実行
        z2401b01Dao.callPointInfo(z2401b01Entity);

    }

    /**
     * ポイント情報部品バッチ実行結果を各ポイントテーブルに登録する
     * 
     * @param sess DbManagementSession
     * @param uriageEntity S06T001TEntity
     * @param uriDate
     */
    private void insertPointResults(DbManagementSession sess, S06T001TEntity uriageEntity,
        String uriDate, String kanrino) {

        // 共通部品実行結果を取得
        List<S20W003TEntity> rows = getPointResult(sess, CommonConstants.Z2401B01_PROC_BULK);
        if (rows == null || rows.isEmpty()) {
            // 共通部品の実行結果が0件の場合は登録しない
            return;
        }

        // 取得結果のチェック
        if (checkPointRow(rows)) {
            // ポイント処理対象のレコードがある場合
            // 予定発生ポイントテーブル登録
            insertS20T001TPoint(sess, uriageEntity, kanrino);

            int renban = 1;
            for (S20W003TEntity e : rows) {
                if (e.getSyorikekka() == 0) {
                    // 予定発生ポイント明細テーブル登録
                    insertS20T002TPoint(sess, kanrino, renban, e, uriDate);
                    // 予定発生ポイント明細計算詳細テーブル登録
                    insertS20T004TPoint(sess, kanrino, renban, e);
                    renban++;
                }
            }
        }

    }
    /**
     * ポイント情報部品結果ワークテーブルの処理結果よりポイント生成対象が存在するか確認
     * 
     * @param rows List<S20W003TEntity>
     * @return boolean
     */
    private boolean checkPointRow(List<S20W003TEntity> rows) {
        boolean isPointEligible = false;
        for (S20W003TEntity row : rows) {
            if (row.getSyorikekka() == 0) {
                isPointEligible = true;
            } else if (row.getSyorikekka() == -1) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W260,
                        "ポイント生成時にエラーが発生しました。システム管理者に連絡してください。");
            }
        }
        return isPointEligible;
    }

    /**
     * ポイント情報部品結果ワークテーブルから共通部品の実行結果を取得
     * 
     * @param sess DbManagementSession
     * @param procKind 処理種別
     * @return List<S20W003TEntity>
     */
    protected List<S20W003TEntity> getPointResult(DbManagementSession sess, String procKind) {

        List<S20W003TEntity> result = new ArrayList<>();
        // 処理種別が個別計算の場合
        if (CommonConstants.Z2401B01_PROC_INDIVIDUAL.equals(procKind)) {
            S20W003TEntity row = selectWorkResult(sess, 1);
            if (row != null) {
                result.add(row);
            }
            return result;
        }

        // 処理種別が一括計算の場合
        int MAX_ROWS = 10000;

        for (int i = 1; i <= MAX_ROWS; i++) {
            S20W003TEntity row = selectWorkResult(sess, i);
            if (row == null) {
                // 連番が途切れたら終了
                break;
            }
            result.add(row);
        }
        return result;
    }

    /**
     * ポイント情報部品結果ワークテーブルから採番された管理番号を取得
     * 
     * @param sess DbManagementSession
     * @param uriDate
     * @return set
     */
    protected String getSaibanKanrino(DbManagementSession sess, String procKind) {

        List<S20W003TEntity> rows = getPointResult(sess, procKind);

        if (rows == null || rows.isEmpty())
            return null;
        LinkedHashSet<String> set = new LinkedHashSet<>();
        for (S20W003TEntity e : rows) {
            String v = e.getSaibankanrino();
            if (v != null && !v.isEmpty())
                set.add(v);
        }
        return set.isEmpty() ? null : set.iterator().next();
    }

    /**
     * 予定発生ポイントテーブル登録処理
     * 
     * @param sess DbManagementSession
     * @param uriageEntity S06T001TEntity
     * @param kanrino
     */
    private void insertS20T001TPoint(DbManagementSession sess, S06T001TEntity uriageEntity,
        String kanrino) {

        S20T001TEntity entity = new S20T001TEntity();

        entity.setApkihoncd(uriageEntity.getApkihoncd());
        entity.setShopcd(uriageEntity.getShopcd());
        entity.setKanrino(kanrino);
        entity.setUridnpyno(uriageEntity.getUridnpyno());
        entity.setUriedano(uriageEntity.getEdano());
        entity.setTokuisakicd(uriageEntity.getTokuisakicd());
        entity.setYukokbn(CommonConstants.YUKOKBN_1);
        entity.setToshaapkihoncd(sess.getApkihoncd());
        entity.setToshashopcd(sess.getShopcd());
        entity.setToshacd(sess.getTnshacd());
        entity.setInptsystmymd(sess.getSystemDate());
        entity.setInptsystmjikan(sess.getSystemTime());
        entity.setInptunyoymd(sess.getUnyoDate());
        entity.setInptipadrse(sess.getIpAddress());
        entity.setInptpgmid(sess.getProgramId());
        entity.setKoshaapkihoncd(sess.getApkihoncd());
        entity.setKoshashopcd(sess.getShopcd());
        entity.setKoshacd(sess.getTnshacd());
        entity.setKshnsystmymd(sess.getSystemDate());
        entity.setKshnsystmjikan(sess.getSystemTime());
        entity.setKshnunyoymd(sess.getUnyoDate());
        entity.setKshnipadrse(sess.getIpAddress());
        entity.setKshnpgmid(sess.getProgramId());

        try {
            s20t001tDao.insertSelective(entity);
        } catch (DuplicateKeyException r) {
            throw BusinessExceptionFactory.validationFailed(r, MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.E002);
        }
    }

    /**
     * 予定発生ポイント明細テーブル登録処理
     * 
     * @param sess DbManagementSession
     * @param kanrino
     * @param renban
     * @param e S20W003TEntity
     * @param uriDate
     */
    private void insertS20T002TPoint(DbManagementSession sess, String kanrino, int renban,
        S20W003TEntity e, String uriDate) {

        S20T002TEntity entity = new S20T002TEntity();

        entity.setApkihoncd(e.getApkihoncd());
        entity.setShopcd(e.getShopcd());
        entity.setKanrino(kanrino);
        entity.setRenban(renban);
        entity.setUridnpymeisaino(e.getDnpymeisaino());
        entity.setUridnpymeisaiedano(e.getDnpymeisaiedano());
        entity.setPointgensifutanapkihoncd(e.getPointgensifutanapkihoncd());
        entity.setPointgensifutanshopcd(e.getPointgensifutanshopcd());
        entity.setPointhasseiapkihoncd(e.getPointhasseiapkihoncd());
        entity.setPointhasseishopcd(e.getPointhasseishopcd());
        entity.setPointkbn(e.getPointkbn());
        entity.setPointkind(e.getPointkind());
        entity.setTekiyostrtdd(e.getTekiyostrtdd());
        entity.setTekiyoshrydd(e.getTekiyoshrydd());
        BigDecimal goukei = Optional.ofNullable(e.getGoukeipoint()).orElse(BigDecimal.ZERO);
        entity.setPointsu(goukei.intValue());
        entity.setMeisaibiko(e.getMeisaibiko());
        entity.setPointytidd(uriDate);
        entity.setYukokbn(CommonConstants.YUKOKBN_1);
        entity.setToshaapkihoncd(sess.getApkihoncd());
        entity.setToshashopcd(sess.getShopcd());
        entity.setToshacd(sess.getTnshacd());
        entity.setInptsystmymd(sess.getSystemDate());
        entity.setInptsystmjikan(sess.getSystemTime());
        entity.setInptunyoymd(sess.getUnyoDate());
        entity.setInptipadrse(sess.getIpAddress());
        entity.setInptpgmid(sess.getProgramId());
        entity.setKoshaapkihoncd(sess.getApkihoncd());
        entity.setKoshashopcd(sess.getShopcd());
        entity.setKoshacd(sess.getTnshacd());
        entity.setKshnsystmymd(sess.getSystemDate());
        entity.setKshnsystmjikan(sess.getSystemTime());
        entity.setKshnunyoymd(sess.getUnyoDate());
        entity.setKshnipadrse(sess.getIpAddress());
        entity.setKshnpgmid(sess.getProgramId());

        try {
            s20t002tDao.insertSelective(entity);
        } catch (DuplicateKeyException r) {
            throw BusinessExceptionFactory.validationFailed(r, MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.E002);
        }
    }

    /**
     * 予定発生ポイント明細計算詳細テーブル登録処理
     * 
     * @param sess DbManagementSession
     * @param kanrino
     * @param renban
     * @param e S20W003TEntity
     * @param uriDate
     */
    private void insertS20T004TPoint(DbManagementSession sess, String kanrino, int renban,
        S20W003TEntity e) {

        S20T004TEntity entity = new S20T004TEntity();

        entity.setApkihoncd(e.getApkihoncd());
        entity.setShopcd(e.getShopcd());
        entity.setKanrino(kanrino);
        entity.setRenban(renban);
        entity.setUridnpymeisaino(e.getDnpymeisaino());
        entity.setUridnpymeisaiedano(e.getDnpymeisaiedano());
        entity.setPointhasseikbn(e.getPointhasseikbn());
        BigDecimal tanka = Optional.ofNullable(e.getSuuryouataripoint()).orElse(BigDecimal.ZERO);
        BigDecimal suryo = Optional.ofNullable(e.getSuryo()).orElse(BigDecimal.ZERO);
        BigDecimal goukei = Optional.ofNullable(e.getGoukeipoint()).orElse(BigDecimal.ZERO);
        entity.setPointtanka(tanka);
        entity.setSuryo(suryo);
        entity.setKeisanpointsu(goukei.intValue());
        entity.setPointrnkcd(e.getPointrnkcd());
        entity.setPointrnk(e.getPointrnk());
        entity.setPointbairitu(e.getPointbairitu());
        entity.setYukokbn(CommonConstants.YUKOKBN_1);
        entity.setToshaapkihoncd(sess.getApkihoncd());
        entity.setToshashopcd(sess.getShopcd());
        entity.setToshacd(sess.getTnshacd());
        entity.setInptsystmymd(sess.getSystemDate());
        entity.setInptsystmjikan(sess.getSystemTime());
        entity.setInptunyoymd(sess.getUnyoDate());
        entity.setInptipadrse(sess.getIpAddress());
        entity.setInptpgmid(sess.getProgramId());

        // ACCIS2023-7433 更新関連の項目への値設定漏れを修正
        entity.setKoshaapkihoncd(sess.getApkihoncd());
        entity.setKoshashopcd(sess.getShopcd());
        entity.setKoshacd(sess.getTnshacd());
        entity.setKshnsystmymd(sess.getSystemDate());
        entity.setKshnsystmjikan(sess.getSystemTime());
        entity.setKshnunyoymd(sess.getUnyoDate());
        entity.setKshnipadrse(sess.getIpAddress());
        entity.setKshnpgmid(sess.getProgramId());

        try {
            s20t004tDao.insertSelective(entity);
        } catch (DuplicateKeyException r) {
            throw BusinessExceptionFactory.validationFailed(r, MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.E002);
        }
    }

    /**
     * 数量が変更された場合に共通部品を実行し、計算した予定ポイントを返却する
     * 
     * @param sess DbManagementSession
     * @param uriDate
     * @param tokuisakicd
     * @param dlvryhoho
     * @param bean C1002W01MeisaiBean
     * @return resultPoint
     */
    public int recalcPoint(DbManagementSession sess, String uriDate, C1002W01Form form,
        String dlvryhoho, C1002W01MeisaiBean bean) {

        // ポイント情報部品引数ワークテーブルへ登録
        S06T002TEntity m = new S06T002TEntity();
        m.setApkihoncd(bean.getApkihoncd());
        m.setShopcd(bean.getShopcd());
        m.setUridnpymeisaino(0);
        m.setUridnpymeisaiedano((short) 0);
        m.setShncd(bean.getShncdmei());
        m.setKeijokbn(bean.getKeijokbnmei());
        m.setUnt(
            new BigDecimal(ItemUtil.removeComma(form.getMeisaiBeanList().get(0).getUnt1mei())));
        m.setSuryo(
            new BigDecimal(ItemUtil.removeComma(form.getMeisaiBeanList().get(0).getSuryo1mei())));
        m.setMeisaibiko(bean.getMeisaibikomei());

        insertPointWork(sess, List.of(m), null, false);

        // 共通部品を実行
        S06T001TEntity temp = createTempHeaderPoint(bean, dlvryhoho);

        callPoint(sess, temp, form.getKttcd(), uriDate, CommonConstants.Z2401B01_PROC_INDIVIDUAL,
            form.getMeisaiBeanList().get(0).getApkihoncd(),
            form.getMeisaiBeanList().get(0).getShopcd());

        // ポイント情報部品処理結果ワークテーブルから実行結果を取得
        List<S20W003TEntity> rows = getPointResult(sess, CommonConstants.Z2401B01_PROC_INDIVIDUAL);
        Optional<S20W003TEntity> resultPoint =
            rows.stream().filter(r -> bean.getShncdmei().equals(r.getShncd())).findFirst();

        return resultPoint
            .map(e -> Optional.ofNullable(e.getGoukeipoint()).orElse(BigDecimal.ZERO).intValue())
            .orElse(0);
    }

    /**
     * ショップポイントマスタ、得意先ポイントマスタのポイント制度参加有無区分、ポイント管理有無、 両方が「参加・有効」ならtrueを返す
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @return ポイント有効フラグ
     */
    public boolean getPointFlg(String apkihoncd, String shopcd, String tokuisakicd) {

        // ショップポイントマスタ取得
        S02M202TEntity shopKey = new S02M202TEntity();
        shopKey.setApkihoncd(apkihoncd);
        shopKey.setShopcd(shopcd);
        S02M202TEntity shop = s02m202tDao.selectByPrimaryKey(shopKey);

        // 得意先ポイントマスタ取得
        S02M203TEntity custKey = new S02M203TEntity();
        custKey.setApkihoncd(apkihoncd);
        custKey.setShopcd(shopcd);
        custKey.setTokuisakicd(tokuisakicd);
        S02M203TEntity cust = s02m203tDao.selectByPrimaryKey(custKey);

        // どちらかのテーブルから取得できなければfalseを返す
        if (shop == null || cust == null)
            return false;
        // ショップのポイント制度参加が「なし」の場合はfalse
        if (!CommonConstants.FLG_ON.equals(shop.getPointsystemjoinkbn())) {
            return false;
        }
        // 得意先のポイント管理が「なし」の場合はfalse
        if (!CommonConstants.FLG_ON.equals(cust.getPointkanriumu())) {
            return false;
        }
        // 得意先のマイアクア利用許諾が「なし」の場合はfalse
        if (!CommonConstants.FLG_ON.equals(cust.getMyaquakaiininptumu())) {
            return false;
        }

        return true;

    }

    /**
     * 共通部品のための仮の売上ヘッダ（S06T001TEntity）を生成して返却する
     * 
     * @param bean C1002W01MeisaiBean
     * @param dlvryhoho
     * @return temp S06T001TEntity
     */
    public S06T001TEntity createTempHeaderPoint(C1002W01MeisaiBean bean, String dlvryhoho) {

        S06T001TEntity temp = new S06T001TEntity();
        temp.setApkihoncd(bean.getApkihoncd());
        temp.setShopcd(bean.getShopcd());
        if (CommonConstants.KBN_DLVRY_HOHO_HAITATSU.equals(dlvryhoho)) {
            temp.setDlvrykbn("1");
        } else if (CommonConstants.KBN_DLVRY_HOHO_MOCHIKAERI.equals(dlvryhoho)) {
            temp.setDlvrykbn("2");
        } else {
            temp.setDlvrykbn("");
        }
        return temp;
    }

    /**
     * 共通部品を実行し、採番された管理番号を返す
     * 
     * @param sess
     * @param tempHeader S06T001TEntity
     * @param tokuisakicd
     * @param uriDate
     * @param apkihoncd
     * @param shopcd
     * @return 管理番号
     */
    public String runNumberingAndGetKanrino(DbManagementSession sess, S06T001TEntity tempHeader,
        String tokuisakicd, String uriDate, String apkihoncd, String shopcd) {

        callPoint(sess, tempHeader, tokuisakicd, uriDate,
            CommonConstants.Z2401B01_PROC_NUMBERINGKANRINO, apkihoncd, shopcd);
        return getSaibanKanrino(sess, CommonConstants.Z2401B01_PROC_NUMBERINGKANRINO);
    }

    /**
     * S20W003T_ポイント情報部品処理結果ワークから共通部品実行結果を取得する
     * 
     * @param sess DbManagementSession
     * @param gyobangou
     * @return S20W003T検索結果
     */
    private S20W003TEntity selectWorkResult(DbManagementSession sess, int gyobangou) {

        S20W003TEntityKey key = new S20W003TEntityKey();
        key.setTnshaapkihoncd(sess.getApkihoncd());
        key.setTnshashopcd(sess.getShopcd());
        key.setTnshacd(sess.getTnshacd());
        key.setPrcsid(sess.getProgramId());
        key.setGyobangou(String.valueOf(gyobangou));

        return s20w003tDao.selectByPrimaryKey(key);
    }

    /**
     * 画面から発生したポイント用の明細行のentityを作成する
     * 
     * @param sess
     * @param e
     * @param gyobangou
     * @param yoteiPoint
     * @return entity
     */
    public S20W002TEntity createPointRow(DbManagementSession sess, S06T002TEntity e, int gyobangou,
        int yoteiPoint) {

        // 引数ワーク登録用
        S20W002TEntity entity = new S20W002TEntity();
        entity.setApkihoncd(e.getApkihoncd());
        entity.setShopcd(e.getShopcd());
        entity.setTnshaapkihoncd(sess.getApkihoncd());
        entity.setTnshashopcd(sess.getShopcd());
        entity.setTnshacd(sess.getTnshacd());
        entity.setPrcsid(sess.getProgramId());
        entity.setIpadrse(sess.getIpAddress());
        entity.setGyobangou(String.valueOf(gyobangou));
        entity.setDnpymeisaino(e.getUridnpymeisaino());
        entity.setDnpymeisaiedano(e.getUridnpymeisaiedano());
        entity.setShncd(null);
        entity.setKeijokbn(CommonConstants.KBN_KEIJO_SERVICE);
        entity.setShnunt(null);
        entity.setGamenpoint(yoteiPoint);
        entity.setSuryo(new BigDecimal(1));
        entity.setMeisaibiko(CommonConstants.GAMEN_HASSEIPOINT);
        entity.setYukokbn(CommonConstants.YUKOKBN_1);

        // ACCIS2023-7433 横並び対応（登録・更新関連の共通項目への値設定漏れ対応）
        entity.setToshaapkihoncd(sess.getApkihoncd());
        entity.setToshashopcd(sess.getShopcd());
        entity.setToshacd(sess.getTnshacd());
        entity.setInptsystmymd(sess.getSystemDate());
        entity.setInptsystmjikan(sess.getSystemTime());
        entity.setInptunyoymd(sess.getUnyoDate());
        entity.setInptipadrse(sess.getIpAddress());
        entity.setInptpgmid(sess.getProgramId());
        entity.setKoshaapkihoncd(sess.getApkihoncd());
        entity.setKoshashopcd(sess.getShopcd());
        entity.setKoshacd(sess.getTnshacd());
        entity.setKshnsystmymd(sess.getSystemDate());
        entity.setKshnsystmjikan(sess.getSystemTime());
        entity.setKshnunyoymd(sess.getUnyoDate());
        entity.setKshnipadrse(sess.getIpAddress());
        entity.setKshnpgmid(sess.getProgramId());

        return entity;
    }

    /**
     * 画面からのポイント登録処理
     * 
     * @param form C1002W01Form
     * @param sess DbManagementSession
     */
    @Transactional
    public void execInsertPointProcess(C1002W01Form form, DbManagementSession sess) {

        C1002W01BaseBean baseBean = form.getBaseBean();
        String uriDate = DateTimeUtil.toNoSlashedDate(form.getSlipBean().getUridd());
        String yoteiPoint = ItemUtil.removeComma(form.getMeisaiBeanList().get(0).getPointsu());
        String meisaibiko = form.getMeisaiBeanList().get(0).getMeisaibikomei();

        // 明細備考
        if (StringUtils.isEmpty(meisaibiko)) {
            // 明細備考の文言取得
            meisaibiko = s02m006tService.searchForMoji(CommonConstants.APCD_000, CommonConstants.SYS_SHOPCD_000,
                        "948", "16");
        }

        // 管理番号採番用_ポイント情報部品引数ワーク登録
        insertPointWork(baseBean, sess);

        // 管理番号採番
        String kanrino = runNumberingAndGetKanrino(sess, new S06T001TEntity(),
            form.getKttcd(), null, baseBean.getApkihoncd(), baseBean.getShopcd());

        // 予定発生ポイントテーブル（S20T001T）登録
        insertYoteiPointTable(form, sess, kanrino);

        // 予定発生ポイント明細テーブル（S20T002T）登録
        insertYoteiPointMeisaiTable(form, sess, kanrino, uriDate, yoteiPoint, meisaibiko);

        // 予定発生ポイント明細計算詳細テーブル(S20T004T)登録
        insertYoteiPointMeisaiKeisanTable(form, sess, kanrino, yoteiPoint);

        return;
    }

    /**
     * 画面発生ポイント用_ポイント情報部品引数ワークへ登録する。
     * 
     * @param baseBean
     * @param sess DbManagementSession
     */
    public void insertPointWork(C1002W01BaseBean baseBean, DbManagementSession sess) {

        // ポイント情報部品引数ワークテーブルへ登録
        S20W002TEntity entity = new S20W002TEntity();
        entity.setApkihoncd(baseBean.getApkihoncd());
        entity.setShopcd(baseBean.getShopcd());
        entity.setTnshaapkihoncd(sess.getApkihoncd());
        entity.setTnshashopcd(sess.getShopcd());
        entity.setTnshacd(sess.getTnshacd());
        entity.setPrcsid(sess.getProgramId());
        entity.setIpadrse(sess.getIpAddress());
        entity.setGyobangou(String.valueOf('1'));
        entity.setYukokbn(CommonConstants.YUKOKBN_1);
        entity.setToshaapkihoncd(sess.getApkihoncd());
        entity.setToshashopcd(sess.getShopcd());
        entity.setToshacd(sess.getTnshacd());
        entity.setInptsystmymd(sess.getSystemDate());
        entity.setInptsystmjikan(sess.getSystemTime());
        entity.setInptunyoymd(sess.getUnyoDate());
        entity.setInptipadrse(sess.getIpAddress());
        entity.setInptpgmid(sess.getProgramId());
        entity.setKoshaapkihoncd(sess.getApkihoncd());
        entity.setKoshashopcd(sess.getShopcd());
        entity.setKoshacd(sess.getTnshacd());
        entity.setKshnsystmymd(sess.getSystemDate());
        entity.setKshnsystmjikan(sess.getSystemTime());
        entity.setKshnunyoymd(sess.getUnyoDate());
        entity.setKshnipadrse(sess.getIpAddress());
        entity.setKshnpgmid(sess.getProgramId());

        try {
            // ポイント情報部品引数ワークへ登録
            s20w002tDao.insertSelective(entity);
        } catch (DuplicateKeyException e) {
            throw BusinessExceptionFactory.validationFailed(e, MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.E002);
        }
    }

    /**
     * 画面発生ポイント用_予定発生ポイントテーブル登録
     * 
     * @param form C1002W01Form
     * @param sess DbManagementSession
     * @param kanrino 管理番号
     */
    public void insertYoteiPointTable(C1002W01Form form, DbManagementSession sess, String kanrino) {
        C1002W01BaseBean baseBean = form.getBaseBean();

        // 予定発生ポイントテーブル（S20T001T）登録
        S20T001TEntity entity = new S20T001TEntity();
        entity.setApkihoncd(baseBean.getApkihoncd());
        entity.setShopcd(baseBean.getShopcd());
        entity.setKanrino(kanrino);
        entity.setAcptdnpyno(null);
        entity.setAcptedano(null);
        entity.setUridnpyno(null);
        entity.setUriedano(null);
        entity.setTokuisakicd(form.getKttcd());
        entity.setYukokbn(CommonConstants.YUKOKBN_1);
        entity.setToshaapkihoncd(sess.getApkihoncd());
        entity.setToshashopcd(sess.getShopcd());
        entity.setToshacd(sess.getTnshacd());
        entity.setInptsystmymd(sess.getSystemDate());
        entity.setInptsystmjikan(sess.getSystemTime());
        entity.setInptunyoymd(sess.getUnyoDate());
        entity.setInptipadrse(sess.getIpAddress());
        entity.setInptpgmid(sess.getProgramId());
        entity.setKoshaapkihoncd(sess.getApkihoncd());
        entity.setKoshashopcd(sess.getShopcd());
        entity.setKoshacd(sess.getTnshacd());
        entity.setKshnsystmymd(sess.getSystemDate());
        entity.setKshnsystmjikan(sess.getSystemTime());
        entity.setKshnunyoymd(sess.getUnyoDate());
        entity.setKshnipadrse(sess.getIpAddress());
        entity.setKshnpgmid(sess.getProgramId());

        try {
            // 予定発生ポイントテーブル登録
            s20t001tDao.insertSelective(entity);
        } catch (DuplicateKeyException e) {
            throw BusinessExceptionFactory.validationFailed(e, MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.E002);
        }
    }

    /**
     * 画面発生ポイント用_予定発生ポイント明細テーブル登録
     * 
     * @param form C1002W01Form
     * @param sess DbManagementSession
     * @param kanrino 管理番号
     * @param uridate 売上日
     * @param yoteiPoint 予定ポイント
     * @param meisaibiko 明細備考
     */
    public void insertYoteiPointMeisaiTable(C1002W01Form form, DbManagementSession sess, String kanrino
            , String uriDate, String yoteiPoint, String meisaibiko) {
        C1002W01BaseBean baseBean = form.getBaseBean();

        // 予定発生ポイント明細テーブル（S20T002T）登録
        S20T002TEntity entity = new S20T002TEntity();
        entity.setApkihoncd(baseBean.getApkihoncd());
        entity.setShopcd(baseBean.getShopcd());
        entity.setKanrino(kanrino);
        entity.setRenban(1);
        entity.setAcptdnpymeisaino(null);
        entity.setAcptdnpymeisaiedano(null);
        entity.setUridnpymeisaino(null);
        entity.setUridnpymeisaiedano(null);
        entity.setPointgensifutanapkihoncd(baseBean.getApkihoncd());
        entity.setPointgensifutanshopcd(baseBean.getShopcd());
        entity.setPointhasseiapkihoncd(null);
        // ACCIS2023-7579 ポイント原資負担元ショップコードが正しく設定されない不具合（編集項目誤りのバグ）の修正
        // ポイント原資負担元ショップコードに正しく設定した値を上書きしていたので、ポイント発生元ショップコードに値を設定するように修正
        entity.setPointhasseishopcd(null);
        entity.setPointkind(Short.parseShort(CommonConstants.POINTCD_ZUIJI_GAMEN));
        entity.setTokubetsufuyoplncd(null);
        entity.setTokubetsustartgessu(null);
        entity.setTokubetsuendgessu(null);
        entity.setTekiyostrtdd(null);
        entity.setTekiyoshrydd(null);
        entity.setPointkbn((short)2);
        entity.setPointsu(Integer.parseInt(yoteiPoint));
        entity.setMeisaibiko(meisaibiko);
        entity.setPointytidd(uriDate);
        entity.setPointhasseidd(null);
        entity.setPointmyaquarenkeidd(null);
        entity.setYukokbn(CommonConstants.YUKOKBN_1);
        entity.setToshaapkihoncd(sess.getApkihoncd());
        entity.setToshashopcd(sess.getShopcd());
        entity.setToshacd(sess.getTnshacd());
        entity.setInptsystmymd(sess.getSystemDate());
        entity.setInptsystmjikan(sess.getSystemTime());
        entity.setInptunyoymd(sess.getUnyoDate());
        entity.setInptipadrse(sess.getIpAddress());
        entity.setInptpgmid(sess.getProgramId());
        entity.setKoshaapkihoncd(sess.getApkihoncd());
        entity.setKoshashopcd(sess.getShopcd());
        entity.setKoshacd(sess.getTnshacd());
        entity.setKshnsystmymd(sess.getSystemDate());
        entity.setKshnsystmjikan(sess.getSystemTime());
        entity.setKshnunyoymd(sess.getUnyoDate());
        entity.setKshnipadrse(sess.getIpAddress());
        entity.setKshnpgmid(sess.getProgramId());

        try {
            // 予定発生ポイント明細テーブル登録
            s20t002tDao.insertSelective(entity);
        } catch (DuplicateKeyException e) {
            throw BusinessExceptionFactory.validationFailed(e, MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.E002);
       }
    }

    /**
     * 画面発生ポイント用_予定発生ポイント明細計算詳細テーブル登録
     * 
     * @param form C1002W01Form
     * @param sess DbManagementSession
     * @param kanrino 管理番号
     * @param uridate 売上日
     * @param yoteiPoint 予定ポイント
     * @param meisaibiko 明細備考
     */
    public void insertYoteiPointMeisaiKeisanTable(C1002W01Form form, DbManagementSession sess, String kanrino
            , String yoteiPoint) {
        C1002W01BaseBean baseBean = form.getBaseBean();

        // 予定発生ポイント明細計算詳細テーブル（S20T004T）登録
        S20T004TEntity entity = new S20T004TEntity();
        entity.setApkihoncd(baseBean.getApkihoncd());
        entity.setShopcd(baseBean.getShopcd());
        entity.setKanrino(kanrino);
        entity.setRenban(1);
        entity.setAcptdnpymeisaino(null);
        entity.setAcptdnpymeisaiedano(null);
        entity.setUridnpymeisaino(null);
        entity.setUridnpymeisaiedano(null);
        entity.setPointhasseikbn("3");
        entity.setPointtanka(new BigDecimal(yoteiPoint));
        entity.setSuryo(new BigDecimal(1));
        entity.setPointrnkcd(null);
        entity.setPointrnk(null);
        entity.setPointbairitu(null);
        entity.setKeisanpointsu(Integer.parseInt(yoteiPoint));
        entity.setYukokbn(CommonConstants.YUKOKBN_1);
        entity.setToshaapkihoncd(sess.getApkihoncd());
        entity.setToshashopcd(sess.getShopcd());
        entity.setToshacd(sess.getTnshacd());
        entity.setInptsystmymd(sess.getSystemDate());
        entity.setInptsystmjikan(sess.getSystemTime());
        entity.setInptunyoymd(sess.getUnyoDate());
        entity.setInptipadrse(sess.getIpAddress());
        entity.setInptpgmid(sess.getProgramId());
        entity.setKoshaapkihoncd(sess.getApkihoncd());
        entity.setKoshashopcd(sess.getShopcd());
        entity.setKoshacd(sess.getTnshacd());
        entity.setKshnsystmymd(sess.getSystemDate());
        entity.setKshnsystmjikan(sess.getSystemTime());
        entity.setKshnunyoymd(sess.getUnyoDate());
        entity.setKshnipadrse(sess.getIpAddress());
        entity.setKshnpgmid(sess.getProgramId());

        try {
         // 予定発生ポイント明細計算詳細テーブル登録
            s20t004tDao.insertSelective(entity);
        } catch (DuplicateKeyException e) {
            throw BusinessExceptionFactory.validationFailed(e, MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.E002);
        }
    }

    /**
     * 割り当て可能なポイント数をシステム管理マスタから取得する
     * 
     * @param dto
     * @return 支払期限日数
     */
    private Integer getZyougenPointsu(String apkihoncd, String shopcd, String setteino) {
        S02M006TDto s02M006TDto = new S02M006TDto();
        s02M006TDto.setApkihoncd(apkihoncd);
        s02M006TDto.setShopcd(shopcd);
        s02M006TDto.setSetteino("952");
        Integer result = s02m006tService.searchForSuchiOrNull(s02M006TDto);
        return result;
    }

}
