package jp.co.aquaclara.accis.web.domain.service.a0301;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S01M004TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M007TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M008TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M043TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M046TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M066TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M206TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02V200TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T002TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.Z1804B01Dao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S01M004TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M001TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M007TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M007TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M008TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M008TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M008TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M043TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M043TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityExample.Criteria;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M066TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M206TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V200TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V200TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V200TSetShnDetailEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V200TSetShnDetailListConditionEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.Z1804B01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M008TA0301W02MeisaiEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02V200TA0301W02Entity;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.controller.a0301.bean.A0301W02BaseBean;
import jp.co.aquaclara.accis.web.controller.a0301.bean.A0301W02BaseBean.MODE;
import jp.co.aquaclara.accis.web.controller.a0301.bean.A0301W02BaseTabBean;
import jp.co.aquaclara.accis.web.controller.a0301.bean.A0301W02SetMeisaiBean;
import jp.co.aquaclara.accis.web.controller.a0301.bean.A0301W02SetMeisaiBean.STATE;
import jp.co.aquaclara.accis.web.controller.a0301.bean.A0301W02ShopTabBean;
import jp.co.aquaclara.accis.web.controller.a0301.form.A0301W02Form;
import jp.co.aquaclara.accis.web.controller.commons.DbManagementSession;
import jp.co.aquaclara.accis.web.domain.dto.a0301.A0301W02DisplayDto;
import jp.co.aquaclara.accis.web.domain.service.master.S02M006TService;
import jp.co.aquaclara.accis.web.domain.service.x9901.StringUtil;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * ショップマスタ(登録画面)Services
 * 
 * @author accisdev009
 *
 */
@Service
@Slf4j
public class A0301W02Service {

    /**
     * 商品ポイントマスタビューDao
     */
    @Autowired
    private S02V200TDao s02V200tDao;

    /**
     * 基本商品マスタDao
     */
    @Autowired
    private S02M007TDao s02m007tDao;

    /**
     * 展開マスタDao
     */
    @Autowired
    private S02M008TDao s02m008tDao;

    /**
     * ショップマスタDao
     */
    @Autowired
    private S02M046TDao s02m046tDao;

    /**
     * ショップ商品マスタDao
     */
    @Autowired
    private S02M043TDao s02m043tDao;

    /**
     * ショップ商品ポイントマスタDao
     */
    @Autowired
    private S02M206TDao s02m206tDao;

    /**
     * 受注配達明細テーブルDao
     */
    @Autowired
    private S04T002TDao s04t002Dao;

    /**
     * 採番マスタDao
     */
    @Autowired
    private Z1804B01Dao z1804b01dao;

    /**
     * 得意先別セット商品単価マスタDAO
     */
    @Autowired
    private S02M066TDao s02m066tDao;

    /**
     * AP基本マスタDAO
     */
    @Autowired
    private S02M001TDao s02m001tDao;

    /**
     * コンディションマスタDAO
     */
    @Autowired
    private S01M004TDao s01m004tDao;

    /**
     * システム管理マスタサービスクラス
     */
    @Autowired
    private S02M006TService s02m006tService;

    /**
     * 指定された商品コードの商品が得意先別セット商品単価マスタに何件存在するかを返す
     * 
     * @param shncd 商品コード
     * @return 件数
     */
    public long getStSpecialUntCount(String shncd) {
        S02M066TEntityExample condition = new S02M066TEntityExample();
        condition.createCriteria().andStshncdEqualTo(shncd);
        return s02m066tDao.countByExample(condition);
    }

    /**
     * 指定された商品の商品区分を基本商品マスタより取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shncd 商品コード
     * @return 商品区分（取得できない場合はNULL）
     */
    public String getBaseShnkbn(String apkihoncd, String shncd) {
        S02M007TEntityKey key = new S02M007TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShncd(shncd);
        // PKで検索
        return Optional.ofNullable(s02m007tDao.selectByPrimaryKey(key))
            // 商品区分に変換
            .map(e -> e.getShnkbn())
            // 検索結果が得られない場合はnullを返す
            .orElse(null);
    }

    /**
     * 商品マスタ更新初期値取得処理
     * 
     * @param apkihoncd
     * @param shopcd
     * @param shncd
     * @param unyodate
     * @return
     */
    public A0301W02DisplayDto init(String apkihoncd, String shopcd, String shncd, String unyodate) {
        // 検索条件を作成
        S02V200TA0301W02Entity searchEntity = new S02V200TA0301W02Entity();
        searchEntity.setApkihoncd(apkihoncd);
        searchEntity.setShopcd(shopcd);
        searchEntity.setShncd(shncd);
        searchEntity.setUnyodate(unyodate);
        // 検索
        S02V200TA0301W02Entity resultEntity = s02V200tDao.selectByA0301W02(searchEntity);
        // 結果セットをdtoに詰めて返却
        return createSelectResultDto(resultEntity, unyodate);
    }

    /**
     * 登録処理
     * 
     * @param form
     * @param dbManagementSession
     * @return
     */
    @Transactional
    public String insert(A0301W02Form form, DbManagementSession dbManagementSession) {

        A0301W02BaseBean baseBean = form.getBaseBean();
        A0301W02BaseTabBean baseTabBean = form.getBaseTabBean();
        List<A0301W02SetMeisaiBean> detailList = form.getMeisaiBeanList();
        A0301W02ShopTabBean shopTabBean = form.getShopTabBean();

        try {
            // 商品コードを採番し、Beanに設定
            baseBean.setShncd(getNewShncd(baseBean, baseTabBean));
            // 基本商品マスタ登録処理
            s02m007tDao.insert(createNewEntity(baseBean, baseTabBean, dbManagementSession));
            // 価格区分名をコンディションマスタから取得してフォームに反映
            S01M004TEntityExample query = new S01M004TEntityExample();
            query.createCriteria().andConditionidEqualTo(CommonConstants.CONDITION_ID_CN028)
                .andSetteiataiEqualTo(baseTabBean.getGenzpricekbn());
            String pricekbnnm = s01m004tDao.selectByExample(query).stream().findFirst()
                .map(e -> e.getItemnm()).orElse("");
            baseBean.setPricekbnnm(pricekbnnm);
            // 展開マスタ登録
            // ACCIS2023-7141 展開マスタ登録時、本部商品の場合は各AP分のデータを登録するように処理を修正
            // 自AP以外で展開マスタの登録対象となるAPの情報を取得
            List<S02M001TEntity> apList =
                getTargetApList(baseBean.getApkihoncd(), baseTabBean.getLctgrycd());
            detailList.stream().forEachOrdered(detail -> {
                // 自AP分の展開マスタを登録
                S02M008TEntity compositionEntity =
                    createNewEntity(baseBean, detail, dbManagementSession);
                s02m008tDao.insert(compositionEntity);
                // 必要に応じて自AP以外の展開マスタを登録
                apList.forEach(apEntity -> {
                    // AP基本コード以外は自APのデータと同じ値で登録する
                    compositionEntity.setApkihoncd(apEntity.getApkihoncd());
                    s02m008tDao.insert(compositionEntity);
                });
            });
            // ショップ商品マスタ登録用Entityを生成
            S02M043TEntity shopShnEntity =
                createNewEntity(baseBean, baseTabBean, shopTabBean, dbManagementSession);
            // ショップ商品ポイントマスタ登録用Entityを生成
            S02M206TEntity shopShnPointEntity =
                createNewPointEntity(baseBean, baseTabBean, shopTabBean, dbManagementSession);

            // 登録対象ショップ情報取得
            S02M046TEntityExample condition = new S02M046TEntityExample();
            Criteria criteria =
                condition.createCriteria().andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
            if (CommonConstants.LCTGRYCD_90.equals(baseTabBean.getLctgrycd())) {
                // AP商品を登録する場合はAP配下のショップが対象
                criteria.andApkihoncdEqualTo(baseBean.getApkihoncd());
            }
            List<S02M046TEntity> shopList = s02m046tDao.selectByExample(condition);
            // ショップ商品マスタ登録
            shopList.stream().forEach(shop -> {
                shopShnEntity.setApkihoncd(shop.getApkihoncd());
                shopShnEntity.setShopcd(shop.getShopcd());
                shopShnEntity.setTaxsetteikbn(CommonConstants.FLG_ON);
                s02m043tDao.insert(shopShnEntity);
            });
            // ショップ商品ポイントマスタ登録
            shopList.stream().forEach(shop -> {
                shopShnPointEntity.setApkihoncd(shop.getApkihoncd());
                shopShnPointEntity.setShopcd(shop.getShopcd());
                s02m206tDao.insert(shopShnPointEntity);
            });
            return baseBean.getShncd();
        } catch (DuplicateKeyException e) {
            // 相関チェックNo45
            throw BusinessExceptionFactory.validationFailed(e, MessageID.W089);
        }
    }

    /**
     * 更新処理
     * 
     * @param form
     * @param updateBaseTabFlg
     * @param updateSetTabFlg
     * @param dbManagementSession
     */
    @Transactional
    public void update(A0301W02Form form, boolean updateBaseTabFlg, boolean updateSetTabFlg,
        DbManagementSession dbManagementSession) {

        A0301W02BaseBean baseBean = form.getBaseBean();
        A0301W02BaseTabBean baseTabBean = form.getBaseTabBean();
        List<A0301W02SetMeisaiBean> detailList = form.getMeisaiBeanList();
        A0301W02ShopTabBean shopTabBean = form.getShopTabBean();

        // 基本情報タブ更新
        if (updateBaseTabFlg) {
            // 基本商品マスタの更新対象レコードを取得（行ロック）
            S02M007TEntity baseShnEntity = new S02M007TEntity();
            baseShnEntity.setApkihoncd(baseBean.getApkihoncd());
            baseShnEntity.setShncd(baseBean.getShncd());
            baseShnEntity = s02m007tDao.selectByPrimaryKeyForLock(baseShnEntity);
            // 相関チェックNo46
            if (!Objects.equals(baseShnEntity.getKshnsystmymd(),
                DateTimeUtil.toNoSlashedDate(baseTabBean.getKihonshnkshnsystmymd()))
                || !Objects.equals(baseShnEntity.getKshnsystmjikan(),
                    DateTimeUtil.toNoColonDateHHMMSS(baseTabBean.getKihonshnkshnsystmjikan()))) {
                // 相関チェックNo46
                throw BusinessExceptionFactory.validationFailed(MessageID.W090);
            }
            // 画面入力内容をEntityに反映する
            baseShnEntity = fillToEntity(baseShnEntity, baseBean, baseTabBean, dbManagementSession);

            // 基本商品マスタを更新
            int s02m007tResultCount = s02m007tDao.updateByPrimaryKey(baseShnEntity);
            // 基本商品マスタを更新、更新件数が両方0件の場合は例外とする
            if (s02m007tResultCount == 0) {
                // 相関チェックNo46
                throw BusinessExceptionFactory.validationFailed(MessageID.W090);
            }
        }
        // セット商品情報タブ更新（一度削除してから登録する）
        if (updateSetTabFlg) {
            // 更新/削除対象行を取得しロック
            S02M008TEntityExample condition = new S02M008TEntityExample();
            condition.createCriteria()
                // AP基本コード
                .andApkihoncdEqualTo(baseBean.getApkihoncd())
                // セット商品コード
                .andStshncdEqualTo(baseBean.getShncd());
            List<S02M008TEntity> entitiesFromTable = s02m008tDao.selectByExampleForLock(condition);

            // 相関チェックNo46
            if (form.getInitialMeisaiBeanList().size() != entitiesFromTable.size()) {
                // 初期表示で取得した明細件数とテーブルの明細件数が一致しない場合はエラー
                log.error("件数不一致 : {} ⇔ {}", form.getInitialMeisaiBeanList().size(),
                    entitiesFromTable.size());
                throw BusinessExceptionFactory.validationFailed(MessageID.W090);
            }
            // 初期表示した明細リストとテーブル内のデータの比較
            form.getInitialMeisaiBeanList().stream().forEachOrdered(initialDetail -> {
                Optional<S02M008TEntity> entity = entitiesFromTable.stream()
                    .filter(e -> e.getApkihoncd().equals(initialDetail.getApkihoncd())
                        && e.getStshncd().equals(initialDetail.getStshncd())
                        && e.getRenban().equals(Short.parseShort(initialDetail.getRenban())))
                    .findFirst();
                if (entity.isEmpty()) {
                    // テーブルに行が存在しない場合はエラー
                    throw BusinessExceptionFactory.validationFailed(MessageID.W090);
                }
                if (!Objects.equals(entity.get().getKshnsystmymd(), initialDetail.getKshnsystmymd())
                    || !Objects.equals(entity.get().getKshnsystmjikan(),
                        initialDetail.getKshnsystmjikan())) {
                    // タイムスタンプが一致しない場合はエラー
                    throw BusinessExceptionFactory.validationFailed(MessageID.W090);
                }
            });
            // データ削除処理（先に削除しないと後続処理でキー重複の可能性がある）
            form.getInitialMeisaiBeanList().stream()
                .filter(detail -> STATE.REMOVED == detail.getState()).forEachOrdered(detail -> {
                    S02M008TEntityKey key = new S02M008TEntityKey();
                    key.setApkihoncd(detail.getApkihoncd());
                    key.setStshncd(detail.getStshncd());
                    key.setRenban(Short.parseShort(detail.getRenban()));
                    if (s02m008tDao.deleteByPrimaryKey(key) == 0) {
                        // 相関チェックNo46
                        throw BusinessExceptionFactory.validationFailed(MessageID.W090);
                    }
                });
            // データ登録/更新処理
            try {
                detailList.stream().forEach(detail -> {
                    STATE state = detail.getState();
                    int count = 0;
                    switch (state) {
                        case ADDED:
                            // レコード登録
                            count = s02m008tDao
                                .insert(createNewEntity(baseBean, detail, dbManagementSession));
                            if (count == 0) {
                                // 相関チェックNo46
                                throw BusinessExceptionFactory.validationFailed(MessageID.W090);
                            }
                            break;
                        case MODIFIED:
                            // レコード更新
                            S02M008TEntity entity = entitiesFromTable.stream()
                                .filter(e -> e.getApkihoncd().equals(detail.getApkihoncd())
                                    && e.getStshncd().equals(detail.getStshncd())
                                    && e.getRenban().equals(Short.parseShort(detail.getRenban())))
                                .findFirst().get();
                            entity = fillToEntity(entity, baseBean, detail, dbManagementSession);
                            count = s02m008tDao.updateByPrimaryKey(entity);
                            if (count == 0) {
                                // 相関チェックNo46
                                throw BusinessExceptionFactory.validationFailed(MessageID.W090);
                            }
                            break;
                        case REMOVED:
                        case LOADED:
                        default:
                            // ロードされた状態の場合は何もしない（それ以外のデータは処理済または存在しない）
                            break;
                    }
                });
            } catch (DuplicateKeyException e) {
                // 相関チェックNo45
                throw BusinessExceptionFactory.validationFailed(e, MessageID.W089);
            }
            // ACCIS2023-7141 本部商品の場合は各AP分のデータを一度削除した上で、更新後の展開マスタの内容を複製し登録する
            // 対象となるAPの情報を取得
            List<S02M001TEntity> apList =
                getTargetApList(baseBean.getApkihoncd(), baseTabBean.getLctgrycd());
            // 対象となるAPが存在する場合のみ、展開マスタの複写を行う
            if (apList.size() != 0) {
                // 登録済の展開マスタの情報を取得
                S02M008TEntityExample compositionCondition = new S02M008TEntityExample();
                compositionCondition.createCriteria().andApkihoncdEqualTo(baseBean.getApkihoncd())
                    .andStshncdEqualTo(baseBean.getShncd());
                compositionCondition.setOrderByClause("RENBAN");
                List<S02M008TEntity> compositionList =
                    s02m008tDao.selectByExample(compositionCondition);
                // 対象となるAP事に処理を行う
                apList.forEach(apEntity -> {
                    // 対象APに登録されている商品の展開マスタを一度削除する
                    compositionCondition.clear();
                    compositionCondition.createCriteria()
                        .andApkihoncdEqualTo(apEntity.getApkihoncd())
                        .andStshncdEqualTo(baseBean.getShncd());
                    s02m008tDao.deleteByExample(compositionCondition);
                    // 登録されている自APの展開マスタの情報を元に対象AP分のデータを登録する
                    compositionList.forEach(compositionEntity -> {
                        // AP基本コードのみを変更し、データを登録する
                        compositionEntity.setApkihoncd(apEntity.getApkihoncd());
                        s02m008tDao.insert(compositionEntity);
                    });
                });
            }
        }
        // ショップ情報タブ更新
        S02M043TEntity shopShnEntity = new S02M043TEntity();
        shopShnEntity.setApkihoncd(baseBean.getApkihoncd());
        shopShnEntity.setShopcd(baseBean.getShopcd());
        shopShnEntity.setShnapkihoncd(shopTabBean.getShnapkihoncd());
        shopShnEntity.setShncd(baseBean.getShncd());
        shopShnEntity = s02m043tDao.selectByPrimaryKeyForLock(shopShnEntity);
        // 相関チェックNo46
        if (!Objects.equals(shopShnEntity.getKshnsystmymd(),
            DateTimeUtil.toNoSlashedDate(shopTabBean.getShopkshnsystmymd()))
            || !Objects.equals(shopShnEntity.getKshnsystmjikan(),
                DateTimeUtil.toNoColonDateHHMMSS(shopTabBean.getShopkshnsystmjikan()))) {
            // 相関チェックNo46
            throw BusinessExceptionFactory.validationFailed(MessageID.W090);
        }
        S02M206TEntity shopShnPointEntity = new S02M206TEntity();
        shopShnPointEntity.setApkihoncd(baseBean.getApkihoncd());
        shopShnPointEntity.setShopcd(baseBean.getShopcd());
        shopShnPointEntity.setShnapkihoncd(shopTabBean.getShnapkihoncd());
        shopShnPointEntity.setShncd(baseBean.getShncd());
        shopShnPointEntity = s02m206tDao.selectByPrimaryKey(shopShnPointEntity);

        // 画面入力内容をEntityに反映する
        shopShnEntity =
            fillToEntity(shopShnEntity, baseBean, baseTabBean, shopTabBean, dbManagementSession);
        shopShnPointEntity =
            fillToEntity(shopShnPointEntity, baseBean, baseTabBean, shopTabBean, dbManagementSession);

        // ショップ商品マスタを更新
        int s02m043tResultCount = s02m043tDao.updateByPrimaryKey(shopShnEntity);
        // ショップ商品ポイントマスタを更新
        int s02m206tResultCount = s02m206tDao.updateByPrimaryKey(shopShnPointEntity);
        // 更新件数が0件の場合は例外とする
        if (s02m043tResultCount == 0 && s02m206tResultCount == 0) {
            // 相関チェックNo46
            throw BusinessExceptionFactory.validationFailed(MessageID.W090);
        }
    }

    /**
     * 明細一覧を検索する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param stshncd セット商品コード
     * @param shnapkihoncd 商品AP基本コード
     * @param unyodate 運用日付
     * @return セット商品の明細Beanリスト
     */
    public List<A0301W02SetMeisaiBean> getSetMeisaiBeanList(String apkihoncd, String shopcd,
        String stshncd, String shnapkihoncd, String unyodate) {

        Integer limit =
            s02m006tService.searchForSuchi(CommonConstants.APCD_000, CommonConstants.SYS_SHOPCD_000,
                CommonConstants.SYS_SETTEINO_035, CommonConstants.SETTEICD_035_1);

        S02M008TA0301W02MeisaiEntity searchEntity = new S02M008TA0301W02MeisaiEntity();
        searchEntity.setApkihoncd(apkihoncd);
        searchEntity.setShopcd(shopcd);
        searchEntity.setStshncd(stshncd);
        searchEntity.setShnapkihoncd(shnapkihoncd);
        searchEntity.setUnyodate(unyodate);
        searchEntity.setRownum(limit);

        return s02m008tDao.selectMeisaiByA0301W02(searchEntity).stream()
            .map(entity -> A0301W02SetMeisaiBean.builder()
                // AP基本コード
                .apkihoncd(entity.getApkihoncd())
                // ショップコード
                .shopcd(entity.getShopcd())
                // セット商品コード
                .stshncd(entity.getStshncd())
                // 連番
                .renban(entity.getRenban())
                // 構成商品AP基本コード
                .koseishnapkihoncd(entity.getKoseishnapkihoncd())
                // 構成商品コード
                .koseishncd(entity.getKoseishncd())
                // 商品コード
                .shncd2(entity.getShncd())
                // 商品名称
                .shnnm3(entity.getShnnm())
                // 計上区分
                .keijokbn1(entity.getKeijokbn())
                // 価格区分
                .pricekbn2(entity.getPricekbn())
                // 価格区分名
                .pricekbnnm2(entity.getPricekbnnm())
                // 配達標準価格
                .dlvryhyojunprice(ItemUtil.editComma(entity.getDlvryhyojunprice()))
                // 持ち帰り標準価格
                .mchikerihyojunprice(ItemUtil.editComma(entity.getMochikaerihyojunprice()))
                // 配達販売価格
                .dlvryhanbaiprice(ItemUtil.editComma(entity.getDlvryhanbaiprice()))
                // 持ち帰り販売価格
                .mchikerihanbaiprice(ItemUtil.editComma(entity.getMochikaerihanbaiprice()))
                // 数量
                .suryo(ItemUtil.editComma(entity.getSuryo()))
                // 数量端数管理区分
                .suryohasukanrikbn(entity.getSuryohasukanrikbn())
                // 売上入数
                .irisu1(entity.getUriirisu())
                // 入数単位
                .irisutani(entity.getUritaninm())
                // 有効区分
                .yukokbn(entity.getYukokbn())
                // 更新システム日付
                .kshnsystmymd(entity.getKshnsystmymd())
                // 更新システム時間
                .kshnsystmjikan(entity.getKshnsystmjikan()).build())
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * 受注伝票件数を取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param shncd 商品コード
     * @return 受注伝票件数
     */
    public Long countJuchuDenpyo(String apkihoncd, String shopcd, String shncd) {
        S04T002TEntity entity = new S04T002TEntity();
        entity.setApkihoncd(apkihoncd);
        entity.setShopcd(shopcd);
        entity.setShncd(shncd);

        return s04t002Dao.countByA0301W02(entity);
    }

    /**
     * 本部商品税区分を取得する
     * 
     * @param shncd 商品コード
     * @return 商品区分
     */
    public String getHnbShnTaxKbn(String shncd) {
        S02M043TEntityExample entityExample = new S02M043TEntityExample();
        entityExample.createCriteria().andApkihoncdEqualTo(CommonConstants.APCD_001)
            .andShopcdEqualTo(CommonConstants.SYS_SHOPCD_000).andShncdEqualTo(shncd)
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

        List<S02M043TEntity> entityList = s02m043tDao.selectByExample(entityExample);

        if (entityList.size() == 0) {
            return null;
        }

        return entityList.get(0).getTaxkbn();
    }

    /**
     * セット商品行追加用情報取得
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param shncd 商品コード
     * @param unyodate 運用日付
     * @return セット商品行リスト
     */
    public List<S02V200TSetShnDetailEntity> selectSetShn(String apkihoncd, String shopcd,
        String shncd, String unyodate) {
        // 検索条件
        S02V200TSetShnDetailListConditionEntity condition =
            S02V200TSetShnDetailListConditionEntity.builder()
                // AP基本コード
                .apkihoncd(apkihoncd)
                // ショップコード
                .shopcd(shopcd)
                // 商品コード
                .shncd(shncd)
                // 運用日
                .operationDate(unyodate)
                // 検索条件を構築
                .build();
        return s02V200tDao.selectSetShnDetailList(condition);
    }

    /**
     * AP/ショップのアクセス範囲区分を取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @return アクセス範囲区分
     */
    public String getApShopAkshanikbn(String apkihoncd, String shopcd) {
        S02M001TEntityKey apkey = new S02M001TEntityKey();
        apkey.setApkihoncd(apkihoncd);
        S02M001TEntity ap = s02m001tDao.selectByPrimaryKey(apkey);
        if (CommonConstants.KBN_KIHON_ACI.equals(ap.getKihonkbn())) {
            return CommonConstants.AKSHANIKBN_ACI;
        }
        S02M046TEntityKey shopkey = new S02M046TEntityKey();
        shopkey.setApkihoncd(apkihoncd);
        shopkey.setShopcd(shopcd);
        S02M046TEntity shop = s02m046tDao.selectByPrimaryKey(shopkey);
        if (CommonConstants.SHOP_KBN_SHOP.equals(shop.getShopkbn())) {
            return CommonConstants.AKSHANIKBN_SHOP;
        }
        return CommonConstants.AKSHANIKBN_AP;
    }

    /**
     * 新規登録時の商品コードを採番する
     * 
     * @param baseBean 基本Bean
     * @param baseTabBean 基本情報タブBean
     * @return 商品コード
     */
    private String getNewShncd(A0301W02BaseBean baseBean, A0301W02BaseTabBean baseTabBean) {
        /* 基本商品マスタ登録 */
        String saibanType = null;
        String apkihoncd = null;
        Z1804B01Entity saibanEntity = new Z1804B01Entity();
        if (CommonConstants.LCTGRYCD_90.equals(baseTabBean.getLctgrycd())) {
            // 採番種別：AP商品コード
            saibanType = CommonConstants.NEWNOAPSHOHIN;
            apkihoncd = baseBean.getApkihoncd();
        } else {
            // 採番種別：本部商品コード
            saibanType = CommonConstants.NEWNOHONBUSHOHIN;
            apkihoncd = CommonConstants.APCD_001;
        }
        // 商品コード採番
        saibanEntity.setType(Integer.parseInt(saibanType));
        saibanEntity.setApkihoncd(apkihoncd);
        z1804b01dao.getNewNo(saibanEntity);
        String shncd = saibanEntity.getResultno();
        log.debug("採番された商品コード : {}", shncd);
        return shncd;
    }

    /**
     * 基本商品マスタ登録用Entityを生成する
     * 
     * @param baseBean 基本Bean
     * @param baseTabBean 基本情報タブBean
     * @param dbManagementSession DB管理セッション情報
     * @return 基本商品マスタEntity
     */
    private S02M007TEntity createNewEntity(A0301W02BaseBean baseBean,
        A0301W02BaseTabBean baseTabBean, DbManagementSession dbManagementSession) {
        S02M007TEntity entity = new S02M007TEntity();
        if (CommonConstants.LCTGRYCD_90.equals(baseTabBean.getLctgrycd())) {
            // 大分類コードがAP商品の場合
            // AP基本コード
            entity.setApkihoncd(baseBean.getApkihoncd());
            // Beanの商品属性をAP商品にする
            baseBean.setShnzokusei(CommonConstants.KBN_SHNZOKUSEI_AP);
        } else {
            // 大分類コードがAP商品以外の場合
            // AP基本コード
            entity.setApkihoncd(CommonConstants.APCD_001);
            // Beanの商品属性を本部商品にする
            baseBean.setShnzokusei(CommonConstants.KBN_SHNZOKUSEI_HONBU);
        }
        // 商品コード
        entity.setShncd(baseBean.getShncd());
        // 登録者AP基本コード
        entity.setToshaapkihoncd(dbManagementSession.getApkihoncd());
        // 登録者ショップコード
        entity.setToshashopcd(dbManagementSession.getShopcd());
        // 登録者コード
        entity.setToshacd(dbManagementSession.getTnshacd());
        // 登録システム日付
        entity.setInptsystmymd(dbManagementSession.getSystemDate());
        // 登録システム時間
        entity.setInptsystmjikan(dbManagementSession.getSystemTime());
        // 登録運用日付
        entity.setInptunyoymd(dbManagementSession.getUnyoDate());
        // 登録IPアドレス
        entity.setInptipadrse(dbManagementSession.getIpAddress());
        // 登録プログラムID
        entity.setInptpgmid(dbManagementSession.getProgramId());
        // その他項目を反映
        entity = fillToEntity(entity, baseBean, baseTabBean, dbManagementSession);
        return entity;
    }

    /**
     * 展開マスタ登録用Entityを生成する
     * 
     * @param baseBean 基本Bean
     * @param detail セット商品明細Bean
     * @param dbManagementSession DB管理セッション
     * @return 展開マスタEntity
     */
    private S02M008TEntity createNewEntity(A0301W02BaseBean baseBean, A0301W02SetMeisaiBean detail,
        DbManagementSession dbManagementSession) {
        S02M008TEntity entity = new S02M008TEntity();
        // AP基本コード
        entity.setApkihoncd(detail.getApkihoncd());
        // セット商品コード
        entity.setStshncd(baseBean.getShncd());
        // 連番
        entity.setRenban(Short.parseShort(detail.getRenban()));
        // 登録者AP基本コード
        entity.setToshaapkihoncd(dbManagementSession.getApkihoncd());
        // 登録者ショップコード
        entity.setToshashopcd(dbManagementSession.getShopcd());
        // 登録者コード
        entity.setToshacd(dbManagementSession.getTnshacd());
        // 登録システム日付
        entity.setInptsystmymd(dbManagementSession.getSystemDate());
        // 登録システム時間
        entity.setInptsystmjikan(dbManagementSession.getSystemTime());
        // 登録運用日付
        entity.setInptunyoymd(dbManagementSession.getUnyoDate());
        // 登録IPアドレス
        entity.setInptipadrse(dbManagementSession.getIpAddress());
        // 登録プログラムID
        entity.setInptpgmid(dbManagementSession.getProgramId());
        entity = fillToEntity(entity, baseBean, detail, dbManagementSession);
        return entity;
    }

    private S02M008TEntity fillToEntity(S02M008TEntity entity, A0301W02BaseBean baseBean,
        A0301W02SetMeisaiBean detail, DbManagementSession dbManagementSession) {
        // 構成商品AP基本コード
        entity.setKoseishnapkihoncd(detail.getKoseishnapkihoncd());
        // 構成商品コード
        entity.setKoseishncd(detail.getKoseishncd());
        // 計上区分
        entity.setKeijokbn(detail.getKeijokbn1());
        // 数量
        entity.setSuryo(new BigDecimal(ItemUtil.removeComma(detail.getSuryo())));
        // 配達価格
        entity.setDlvryprice(new BigDecimal(ItemUtil.removeComma(detail.getDlvryhanbaiprice())));
        // 持ち帰り価格
        entity.setMochikaeriprice(
            new BigDecimal(ItemUtil.removeComma(detail.getMchikerihanbaiprice())));
        // 有効区分
        entity.setYukokbn(CommonConstants.YUKOKBN_1);
        // 更新者AP基本コード
        entity.setKoshaapkihoncd(dbManagementSession.getApkihoncd());
        // 更新者ショップコード
        entity.setKoshashopcd(dbManagementSession.getShopcd());
        // 更新者コード
        entity.setKoshacd(dbManagementSession.getTnshacd());
        // 更新システム日付
        entity.setKshnsystmymd(dbManagementSession.getSystemDate());
        // 更新システム時間
        entity.setKshnsystmjikan(dbManagementSession.getSystemTime());
        // 更新運用日付
        entity.setKshnunyoymd(dbManagementSession.getUnyoDate());
        // 更新IPアドレス
        entity.setKshnipadrse(dbManagementSession.getIpAddress());
        // 更新プログラムID
        entity.setKshnpgmid(dbManagementSession.getProgramId());
        log.debug("展開マスタEntity : {}", entity);
        return entity;
    }

    /**
     * ショップ商品マスタ登録用Entityを生成する（AP基本コードとショップコードは設定しない）
     * 
     * @param baseBean 基本Bean
     * @param baseTabBean 基本情報タブBean
     * @param shopTabBean ショップ情報Bean
     * @param dbManagementSession DB管理セッション情報
     * @return ショップ商品マスタ
     */
    private S02M043TEntity createNewEntity(A0301W02BaseBean baseBean,
        A0301W02BaseTabBean baseTabBean, A0301W02ShopTabBean shopTabBean,
        DbManagementSession dbManagementSession) {
        S02M043TEntity entity = new S02M043TEntity();
        // 商品AP基本コード
        if (CommonConstants.LCTGRYCD_90.equals(baseTabBean.getLctgrycd())) {
            entity.setShnapkihoncd(baseBean.getApkihoncd());
        } else {
            entity.setShnapkihoncd(CommonConstants.APCD_001);
        }
        // 商品コード
        entity.setShncd(baseBean.getShncd());
        // 登録者AP基本コード
        entity.setToshaapkihoncd(dbManagementSession.getApkihoncd());
        // 登録者ショップコード
        entity.setToshashopcd(dbManagementSession.getShopcd());
        // 登録者コード
        entity.setToshacd(dbManagementSession.getTnshacd());
        // 登録システム日付
        entity.setInptsystmymd(dbManagementSession.getSystemDate());
        // 登録システム時間
        entity.setInptsystmjikan(dbManagementSession.getSystemTime());
        // 登録運用日付
        entity.setInptunyoymd(dbManagementSession.getUnyoDate());
        // 登録IPアドレス
        entity.setInptipadrse(dbManagementSession.getIpAddress());
        // 登録プログラムID
        entity.setInptpgmid(dbManagementSession.getProgramId());
        // その他項目を反映
        entity = fillToEntity(entity, baseBean, baseTabBean, shopTabBean, dbManagementSession);
        return entity;
    }

    /**
     * ショップ商品マスタ登録用Entityを生成する（AP基本コードとショップコードは設定しない）
     * 
     * @param baseBean 基本Bean
     * @param baseTabBean 基本情報タブBean
     * @param shopTabBean ショップ情報Bean
     * @param dbManagementSession DB管理セッション情報
     * @return ショップ商品マスタ
     */
    private S02M206TEntity createNewPointEntity(A0301W02BaseBean baseBean,
        A0301W02BaseTabBean baseTabBean, A0301W02ShopTabBean shopTabBean,
        DbManagementSession dbManagementSession) {
        S02M206TEntity entity = new S02M206TEntity();
        // 商品AP基本コード
        if (CommonConstants.LCTGRYCD_90.equals(baseTabBean.getLctgrycd())) {
            entity.setShnapkihoncd(baseBean.getApkihoncd());
        } else {
            entity.setShnapkihoncd(CommonConstants.APCD_001);
        }
        // 商品コード
        entity.setShncd(baseBean.getShncd());
        // 登録者AP基本コード
        entity.setToshaapkihoncd(dbManagementSession.getApkihoncd());
        // 登録者ショップコード
        entity.setToshashopcd(dbManagementSession.getShopcd());
        // 登録者コード
        entity.setToshacd(dbManagementSession.getTnshacd());
        // 登録システム日付
        entity.setInptsystmymd(dbManagementSession.getSystemDate());
        // 登録システム時間
        entity.setInptsystmjikan(dbManagementSession.getSystemTime());
        // 登録運用日付
        entity.setInptunyoymd(dbManagementSession.getUnyoDate());
        // 登録IPアドレス
        entity.setInptipadrse(dbManagementSession.getIpAddress());
        // 登録プログラムID
        entity.setInptpgmid(dbManagementSession.getProgramId());
        // その他項目を反映
        entity = fillToEntity(entity, baseBean, baseTabBean, shopTabBean, dbManagementSession);
        return entity;
    }

    /**
     * Entityに更新内容を反映する
     * 
     * @param entity 基本商品マスタEntity
     * @param baseBean 基本Bean
     * @param baseTabBean 基本情報タブBean
     * @param dbManagementSession DB管理セッション情報
     * @return 基本商品マスタEntity
     */
    private S02M007TEntity fillToEntity(S02M007TEntity entity, A0301W02BaseBean baseBean,
        A0301W02BaseTabBean baseTabBean, DbManagementSession dbManagementSession) {
        // 主キーと登録時のタイムスタンプ以外を反映する
        // セット区分
        entity.setStkbn(baseBean.getStkbn());
        // 商品属性
        entity.setShnzokusei(baseBean.getShnzokusei());
        // 大分類コード
        entity.setLctgrycd(baseTabBean.getLctgrycd());
        // 中分類コード
        entity.setMctgrycd(baseTabBean.getMctgrycd());
        // 小分類コード
        entity.setSctgrycd(baseTabBean.getSctgrycd());
        // 現商品名称
        entity.setGenzshnnm(baseTabBean.getGenzshnnm());
        // 現商品名称カナ
        entity.setGenzshnnmkana(baseTabBean.getGenzshnnmkana());
        // 現商品名称略称
        entity.setGenzshnnmryakusho(baseTabBean.getGenzshnnmryakusho());
        // 現標準配達価格
        entity.setGenzhyojundlvryprice(
            new BigDecimal(ItemUtil.removeComma(baseTabBean.getGenzhyojundlvryprice())));
        // 現標準持ち帰り価格
        entity.setGenzhyojunmochikaeriprice(
            new BigDecimal(ItemUtil.removeComma(baseTabBean.getGenzhyojunmochikaeriprice())));
        // 現価格区分
        entity.setGenzpricekbn(baseTabBean.getGenzpricekbn());
        if (MODE.INSERT.equals(baseBean.getMode())) {
            // 標準価格切替日（運用日付を設定）
            entity.setHyojunpricekirikaedd(dbManagementSession.getUnyoDate());
            // 商品名称切替日（運用日付を設定）
            entity.setShnnmkirikaedd(dbManagementSession.getUnyoDate());
            // 新商品名称（現を設定）
            entity.setShinshnnm(entity.getGenzshnnm());
            // 新商品名称カナ（現を設定）
            entity.setShinshnnmkana(entity.getGenzshnnmkana());
            // 新商品名称略称（現を設定）
            entity.setShinshnnmryakusho(entity.getGenzshnnmryakusho());
            // 新標準配達価格（現を設定）
            entity.setShinhyojundlvryprice(entity.getGenzhyojundlvryprice());
            // 新標準持ち帰り価格（現を設定）
            entity.setShinhyojunmochikaeriprice(entity.getGenzhyojunmochikaeriprice());
            // 新価格区分（現を設定）
            entity.setShinpricekbn(entity.getGenzpricekbn());
        } else if (MODE.UPDATE.equals(baseBean.getMode())) {
            if (CommonConstants.FLG_ON.equals(baseBean.getStkbn())) {
                // セット区分がセット商品の場合、標準価格切替日は変更しない
                entity.setHyojunpricekirikaedd(entity.getHyojunpricekirikaedd());
            } else {
                // 標準価格切替日
                entity.setHyojunpricekirikaedd(
                    DateTimeUtil.toNoSlashedDate(baseTabBean.getHyojunpricekirikaedd()));
            }
            // 商品名称切替日
            entity.setShnnmkirikaedd(DateTimeUtil.toNoSlashedDate(baseTabBean.getShnnmkirikaedd()));
            // 新商品名称
            entity.setShinshnnm(baseTabBean.getShinshnnm());
            // 新商品名称カナ
            entity.setShinshnnmkana(baseTabBean.getShinshnnmkana());
            // 新商品名称略称
            entity.setShinshnnmryakusho(baseTabBean.getShinshnnmryakusho());
            // 新標準配達価格
            entity.setShinhyojundlvryprice(
                new BigDecimal(ItemUtil.removeComma(baseTabBean.getShinhyojundlvryprice())));
            // 新標準持ち帰り価格
            entity.setShinhyojunmochikaeriprice(
                new BigDecimal(ItemUtil.removeComma(baseTabBean.getShinhyojunmochikaeriprice())));
            // 新価格区分
            entity.setShinpricekbn(baseTabBean.getShinpricekbn());
        }
        // サイズ
        entity.setSz(baseTabBean.getSz());
        // 色
        entity.setIro(baseTabBean.getIro());
        // 取扱区分
        entity.setTrtskikbn(baseTabBean.getTrtskikbn());
        // 数量端数管理区分
        entity.setSuryohasukanrikbn(baseTabBean.getSuryohasukanrikbn());
        // 備考
        entity.setBiko(baseTabBean.getBiko1());
        // 商品区分
        entity.setShnkbn(baseTabBean.getShnkbn2());
        // 有効区分
        entity.setYukokbn(baseTabBean.getKihonshnyukokbn());
        // 更新者AP基本コード
        entity.setKoshaapkihoncd(dbManagementSession.getApkihoncd());
        // 更新者ショップコード
        entity.setKoshashopcd(dbManagementSession.getShopcd());
        // 更新者コード
        entity.setKoshacd(dbManagementSession.getTnshacd());
        // 更新システム日付
        entity.setKshnsystmymd(dbManagementSession.getSystemDate());
        // 更新システム時間
        entity.setKshnsystmjikan(dbManagementSession.getSystemTime());
        // 更新運用日付
        entity.setKshnunyoymd(dbManagementSession.getUnyoDate());
        // 更新IPアドレス
        entity.setKshnipadrse(dbManagementSession.getIpAddress());
        // 更新プログラムID
        entity.setKshnpgmid(dbManagementSession.getProgramId());
        log.debug("基本商品マスタEntity : {}", entity);
        return entity;
    }

    /**
     * Entityに更新内容を反映する
     * 
     * @param entity ショップ商品マスタEntity
     * @param baseBean 基本Bean
     * @param baseTabBean 基本情報タブBean
     * @param shopTabBean ショップ情報タブBean
     * @param dbManagementSession DB管理セッション情報
     * @return ショップ商品マスタEntity
     */
    private S02M043TEntity fillToEntity(S02M043TEntity entity, A0301W02BaseBean baseBean,
        A0301W02BaseTabBean baseTabBean, A0301W02ShopTabBean shopTabBean,
        DbManagementSession dbManagementSession) {
        // 販売開始日
        entity.setHanbaistrtdd(DateTimeUtil.toNoSlashedDate(shopTabBean.getHanbaidkikanstr()));
        // 販売終了日
        entity.setHanbaishrydd(DateTimeUtil.toNoSlashedDate(shopTabBean.getHanbaidkikanend()));
        // 外部連携商品コード
        entity.setGaiburenkeishncd(shopTabBean.getGaiburenkeishncd());
        // 本部販売管理商品コード
        entity.setHonbuhanbaikanrishncd(shopTabBean.getHonbuhanbaikanrishncd());
        // マージン対象区分
        entity.setMgntrgtkbn(shopTabBean.getMgntrgtkbn());
        // 商品区分
        entity.setShnkbn(shopTabBean.getShnkbn());
        // モバイル利用有無
        entity.setMobileriyoumu(shopTabBean.getMobileriyoumu());
        // 税設定区分
        entity.setTaxsetteikbn(shopTabBean.getTaxsetteikbn());
        // 税区分
        entity.setTaxkbn(shopTabBean.getTaxkbn());

        // 仕入取扱単位
        entity.setSiiretrtskitani(shopTabBean.getSiiretrtskitani());
        // 仕入入数
        if (!StringUtil.isEmpty(shopTabBean.getSiireirisu())) {
            entity
                .setSiireirisu(Integer.parseInt(ItemUtil.removeComma(shopTabBean.getSiireirisu())));
        }
        // 仕入単位
        entity.setSiiretani(shopTabBean.getIrisutani1());
        // 仕入単価
        if (!StringUtil.isEmpty(shopTabBean.getSiireunt())) {
            entity.setSiireunt(new BigDecimal(ItemUtil.removeComma(shopTabBean.getSiireunt())));
        }
        // 発注ロット数
        if (!StringUtil.isEmpty(shopTabBean.getHacchurottokazu())) {
            entity.setHacchurottokazu(
                Short.parseShort(ItemUtil.removeComma(shopTabBean.getHacchurottokazu())));
        }

        // 売上取扱単位
        entity.setUritrtskitani(shopTabBean.getUritrtskitani());
        // 売上入数
        if (!StringUtil.isEmpty(shopTabBean.getUriirisu())) {
            entity.setUriirisu(Integer.parseInt(ItemUtil.removeComma(shopTabBean.getUriirisu())));
        }
        // 売上単位
        entity.setUritani(shopTabBean.getIrisutani2());

        // 現オープン配達価格
        entity.setGenzopnprice(getDecimalNullable(shopTabBean.getGendlvryprice2()));
        // 現オープン持ち帰り価格
        entity.setGenzopnmochikaeriprice(getDecimalNullable(shopTabBean.getGenzmochikaeriprice()));

        if (MODE.INSERT.equals(baseBean.getMode())) {
            // オープン価格切替日（価格区分がオープン価格ならば運用日付を設定）
            if (CommonConstants.KBN_PRICE_OPEN.equals(baseTabBean.getGenzpricekbn())) {
                entity.setOpnpricekirikaedd(dbManagementSession.getUnyoDate());
            }
            // 新オープン配達価格（現を設定）
            entity.setShinopnprice(entity.getGenzopnprice());
            // 新オープン持ち帰り価格（現を設定）
            entity.setShinopnmochikaeriprice(entity.getGenzopnmochikaeriprice());
        } else if (MODE.UPDATE.equals(baseBean.getMode())) {
            // オープン価格切替日
            entity.setOpnpricekirikaedd(
                DateTimeUtil.toNoSlashedDate(shopTabBean.getOpnpricekirikaedd()));
            // 新オープン配達価格
            entity.setShinopnprice(getDecimalNullable(shopTabBean.getShindlvryprice1()));
            // 新オープン持ち帰り価格
            entity.setShinopnmochikaeriprice(
                getDecimalNullable(shopTabBean.getShinmochikaeriprice2()));
        }

        // 販促区分
        entity.setHansokukbn(shopTabBean.getHansokusettei());
        // 販促開始日
        entity.setHansokustrtdd(DateTimeUtil.toNoSlashedDate(shopTabBean.getTekiyodkikanstr()));
        // 販促終了日
        entity.setHansokushrydd(DateTimeUtil.toNoSlashedDate(shopTabBean.getTekiyodkikanend()));
        // 販促名称
        entity.setHansokunm(shopTabBean.getHansokunm());
        // 販促配達価格
        if (!StringUtil.isEmpty(shopTabBean.getDlvryprice())) {
            entity.setHansokudlvryprice(
                new BigDecimal(ItemUtil.removeComma(shopTabBean.getDlvryprice())));
        }
        // 販促持ち帰り価格
        if (!StringUtil.isEmpty(shopTabBean.getOmchkrprice())) {
            entity.setHansokumochikaeriprice(
                new BigDecimal(ItemUtil.removeComma(shopTabBean.getOmchkrprice())));
        }
        // 販促優先区分
        entity.setHansokuyusenkbn(shopTabBean.getHansokuyusenkbn());

        // 在庫管理対象フラグ
        entity.setZaikokanritrgtflg(shopTabBean.getZaikokanrikbn());
        // 原価計算区分
        entity.setGenkakeisankbn(shopTabBean.getGenkakeisankbn());
        // 在庫単価
        entity.setZaikount(new BigDecimal(ItemUtil.removeComma(shopTabBean.getZaikount1())));
        // 標準原価（※在庫単価の値を設定）
        entity.setHyojungenka(entity.getZaikount());
        // 仕入単位変換区分
        entity.setSiiretanihenkankbn(shopTabBean.getSiiretanihenkankbn());
        // 在庫アラーム数量
        entity.setZaikoaramusuryo(
            Integer.parseInt(ItemUtil.removeComma(shopTabBean.getZaikoaramusuryo())));
        // 会員サイト連携区分
        entity.setKaiinsaitorenkeikbn(CommonConstants.KAIINSAITORENKEIKBN_0);
        // 備考
        entity.setBiko(shopTabBean.getBiko2());
        // 有効区分
        entity.setYukokbn(shopTabBean.getYukokbn());

        // 更新者AP基本コード
        entity.setKoshaapkihoncd(dbManagementSession.getApkihoncd());
        // 更新者ショップコード
        entity.setKoshashopcd(dbManagementSession.getShopcd());
        // 更新者コード
        entity.setKoshacd(dbManagementSession.getTnshacd());
        // 更新システム日付
        entity.setKshnsystmymd(dbManagementSession.getSystemDate());
        // 更新システム時間
        entity.setKshnsystmjikan(dbManagementSession.getSystemTime());
        // 更新運用日付
        entity.setKshnunyoymd(dbManagementSession.getUnyoDate());
        // 更新IPアドレス
        entity.setKshnipadrse(dbManagementSession.getIpAddress());
        // 更新プログラムID
        entity.setKshnpgmid(dbManagementSession.getProgramId());
        log.debug("ショップ商品マスタ : {}", entity);
        return entity;
    }

    /**
     * Entityに更新内容を反映する
     * 
     * @param entity ショップ商品マスタEntity
     * @param baseBean 基本Bean
     * @param baseTabBean 基本情報タブBean
     * @param shopTabBean ショップ情報タブBean
     * @param dbManagementSession DB管理セッション情報
     * @return ショップ商品マスタEntity
     */
    private S02M206TEntity fillToEntity(S02M206TEntity entity, A0301W02BaseBean baseBean,
        A0301W02BaseTabBean baseTabBean, A0301W02ShopTabBean shopTabBean,
        DbManagementSession dbManagementSession) {
        if (MODE.INSERT.equals(baseBean.getMode())) {
            // 現商品ポイント発生区分
            entity.setGenshnpointhasseikbn(shopTabBean.getGenshnpointhasseikbn());
            // 現商品ポイント数
            if (entity.getGenshnpointhasseikbn().equals(CommonConstants.SHNPOINTHASSEIKBN_VAL_2)) {
                // 2:還元率
                entity.setGenshnpointsu(getDecimalNullable(shopTabBean.getGenshnpointsu1()));
            } else if (entity.getGenshnpointhasseikbn().equals(CommonConstants.SHNPOINTHASSEIKBN_VAL_3)) {
                // 3:固定
                entity.setGenshnpointsu(getDecimalNullable(shopTabBean.getGenshnpointsu2()));
            } else {
                // 0:発生しない/1:グループ共通設定
                entity.setGenshnpointsu(null);
            }
            // 商品ポイント切替日（運用日付を設定）
            entity.setShnpointkirikaedd(dbManagementSession.getUnyoDate());
            // 新商品ポイント発生区分（現を設定）
            entity.setShinshnpointhasseikbn(entity.getGenshnpointhasseikbn());
            // 新商品ポイント数（現を設定）
            entity.setShinshnpointsu(entity.getGenshnpointsu());
            // 現まとめ買いポイント区分
            entity.setGenmtmkipointkbn(shopTabBean.getGenmtmkipointkbn());
            // 現まとめ買い数量
            entity.setGenmtmkisuryo(getIntegerNullable(shopTabBean.getGenmtmkisuryo()));
            // 現まとめ買いポイント数
            entity.setGenmtmkipointsu(getIntegerNullable(shopTabBean.getGenmtmkipointsu()));
            // 現まとめ買い段階的発生の数量１
            entity.setGenmtmkidankaisuryo1(getIntegerNullable(shopTabBean.getGenmtmkidankaisuryo1()));
            // 現まとめ買い段階的発生のポイント数１
            entity.setGenmtmkidankaipointsu1(getIntegerNullable(shopTabBean.getGenmtmkidankaipointsu1()));
            // 現まとめ買い段階的発生の数量２
            entity.setGenmtmkidankaisuryo2(getIntegerNullable(shopTabBean.getGenmtmkidankaisuryo2()));
            // 現まとめ買い段階的発生のポイント数２
            entity.setGenmtmkidankaipointsu2(getIntegerNullable(shopTabBean.getGenmtmkidankaipointsu2()));
            // 現まとめ買い段階的発生の数量３
            entity.setGenmtmkidankaisuryo3(getIntegerNullable(shopTabBean.getGenmtmkidankaisuryo3()));
            // 現まとめ買い段階的発生のポイント数３
            entity.setGenmtmkidankaipointsu3(getIntegerNullable(shopTabBean.getGenmtmkidankaipointsu3()));
            // 現まとめ買い段階的発生の数量４
            entity.setGenmtmkidankaisuryo4(getIntegerNullable(shopTabBean.getGenmtmkidankaisuryo4()));
            // 現まとめ買い段階的発生のポイント数４
            entity.setGenmtmkidankaipointsu4(getIntegerNullable(shopTabBean.getGenmtmkidankaipointsu4()));
            // 現まとめ買い段階的発生の数量５
            entity.setGenmtmkidankaisuryo5(getIntegerNullable(shopTabBean.getGenmtmkidankaisuryo5()));
            // 現まとめ買い段階的発生のポイント数５
            entity.setGenmtmkidankaipointsu5(getIntegerNullable(shopTabBean.getGenmtmkidankaipointsu5()));
            // まとめ買いポイント発生切替日（運用日付を設定）
            entity.setMtmkipointkirikaedd(dbManagementSession.getUnyoDate());
            // 新まとめ買いポイント区分（現を設定）
            entity.setShinmtmkipointkbn(entity.getGenmtmkipointkbn());
            // 新まとめ買い数量（現を設定）
            entity.setShinmtmkisuryo(getIntegerNullable(shopTabBean.getGenmtmkisuryo()));
            // 新まとめ買いポイント数（現を設定）
            entity.setShinmtmkipointsu(getIntegerNullable(shopTabBean.getGenmtmkipointsu()));
            // 新まとめ買い段階的発生の数量１（現を設定）
            entity.setShinmtmkidankaisuryo1(getIntegerNullable(shopTabBean.getGenmtmkidankaisuryo1()));
            // 新まとめ買い段階的発生のポイント数１（現を設定）
            entity.setShinmtmkidankaipointsu1(getIntegerNullable(shopTabBean.getGenmtmkidankaipointsu1()));
            // 新まとめ買い段階的発生の数量２（現を設定）
            entity.setShinmtmkidankaisuryo2(getIntegerNullable(shopTabBean.getGenmtmkidankaisuryo2()));
            // 新まとめ買い段階的発生のポイント数２（現を設定）
            entity.setShinmtmkidankaipointsu2(getIntegerNullable(shopTabBean.getGenmtmkidankaipointsu2()));
            // 新まとめ買い段階的発生の数量３（現を設定）
            entity.setShinmtmkidankaisuryo3(getIntegerNullable(shopTabBean.getGenmtmkidankaisuryo3()));
            // 新まとめ買い段階的発生のポイント数３（現を設定）
            entity.setShinmtmkidankaipointsu3(getIntegerNullable(shopTabBean.getGenmtmkidankaipointsu3()));
            // 新まとめ買い段階的発生の数量４（現を設定）
            entity.setShinmtmkidankaisuryo4(getIntegerNullable(shopTabBean.getGenmtmkidankaisuryo4()));
            // 新まとめ買い段階的発生のポイント数４（現を設定）
            entity.setShinmtmkidankaipointsu4(getIntegerNullable(shopTabBean.getGenmtmkidankaipointsu4()));
            // 新まとめ買い段階的発生の数量５（現を設定）
            entity.setShinmtmkidankaisuryo5(getIntegerNullable(shopTabBean.getGenmtmkidankaisuryo5()));
            // 新まとめ買い段階的発生のポイント数５（現を設定）
            entity.setShinmtmkidankaipointsu5(getIntegerNullable(shopTabBean.getGenmtmkidankaipointsu5()));
        // 更新モード
        } else if (MODE.UPDATE.equals(baseBean.getMode())) {
            // 現ポイント発生区分
            entity.setGenshnpointhasseikbn(shopTabBean.getGenshnpointhasseikbn());
            // 現商品ポイント数
            if (entity.getGenshnpointhasseikbn().equals(CommonConstants.SHNPOINTHASSEIKBN_VAL_2)) {
                // 2:還元率の場合 還元率値を設定
                entity.setGenshnpointsu(getDecimalNullable(shopTabBean.getGenshnpointsu1()));
            } else if (entity.getGenshnpointhasseikbn().equals(CommonConstants.SHNPOINTHASSEIKBN_VAL_3)) {
                // 3:固定の場合 固定値を設定
                entity.setGenshnpointsu(getDecimalNullable(shopTabBean.getGenshnpointsu2()));
            } else {
                // 0:発生しない/1:グループ共通設定
                entity.setGenshnpointsu(null);
            }
            // ポイント発生切替日
            entity.setShnpointkirikaedd(
                DateTimeUtil.toNoSlashedDate(shopTabBean.getShnpointkirikaedd()));
            // 新商品ポイント発生区分
            entity.setShinshnpointhasseikbn(shopTabBean.getShinshnpointhasseikbn());
            // 新商品ポイント数
            if (shopTabBean.getShinshnpointhasseikbn().equals(CommonConstants.SHNPOINTHASSEIKBN_VAL_2)) {
                // 2:還元率
                entity.setShinshnpointsu(getDecimalNullable(shopTabBean.getShinshnpointsu1()));
            } else if (shopTabBean.getShinshnpointhasseikbn().equals(CommonConstants.SHNPOINTHASSEIKBN_VAL_3)) {
                // 3:固定
                entity.setShinshnpointsu(getDecimalNullable(shopTabBean.getShinshnpointsu2()));
            } else {
                // 0:発生しない/1:グループ共通設定
                entity.setShinshnpointsu(null);
            }
            // 現まとめ買いポイント区分
            entity.setGenmtmkipointkbn(shopTabBean.getGenmtmkipointkbn());
            // 現まとめ買い数量
            entity.setGenmtmkisuryo(getIntegerNullable(shopTabBean.getGenmtmkisuryo()));
            // 現まとめ買いポイント数
            entity.setGenmtmkipointsu(getIntegerNullable(shopTabBean.getGenmtmkipointsu()));
            // 現まとめ買い段階的発生の数量１
            entity.setGenmtmkidankaisuryo1(getIntegerNullable(shopTabBean.getGenmtmkidankaisuryo1()));
            // 現まとめ買い段階的発生のポイント数１
            entity.setGenmtmkidankaipointsu1(getIntegerNullable(shopTabBean.getGenmtmkidankaipointsu1()));
            // 現まとめ買い段階的発生の数量２
            entity.setGenmtmkidankaisuryo2(getIntegerNullable(shopTabBean.getGenmtmkidankaisuryo2()));
            // 現まとめ買い段階的発生のポイント数２
            entity.setGenmtmkidankaipointsu2(getIntegerNullable(shopTabBean.getGenmtmkidankaipointsu2()));
            // 現まとめ買い段階的発生の数量３
            entity.setGenmtmkidankaisuryo3(getIntegerNullable(shopTabBean.getGenmtmkidankaisuryo3()));
            // 現まとめ買い段階的発生のポイント数３
            entity.setGenmtmkidankaipointsu3(getIntegerNullable(shopTabBean.getGenmtmkidankaipointsu3()));
            // 現まとめ買い段階的発生の数量４
            entity.setGenmtmkidankaisuryo4(getIntegerNullable(shopTabBean.getGenmtmkidankaisuryo4()));
            // 現まとめ買い段階的発生のポイント数４
            entity.setGenmtmkidankaipointsu4(getIntegerNullable(shopTabBean.getGenmtmkidankaipointsu4()));
            // 現まとめ買い段階的発生の数量５
            entity.setGenmtmkidankaisuryo5(getIntegerNullable(shopTabBean.getGenmtmkidankaisuryo5()));
            // 現まとめ買い段階的発生のポイント数５
            entity.setGenmtmkidankaipointsu5(getIntegerNullable(shopTabBean.getGenmtmkidankaipointsu5()));
            // まとめ買いポイント発生切替日
            entity.setMtmkipointkirikaedd(
                        DateTimeUtil.toNoSlashedDate(shopTabBean.getMtmkipointkirikaedd()));
            // 新まとめ買いポイント区分
            entity.setShinmtmkipointkbn(shopTabBean.getShinmtmkipointkbn());
            // 新まとめ買い数量
            entity.setShinmtmkisuryo(getIntegerNullable(shopTabBean.getShinmtmkisuryo()));
            // 新まとめ買いポイント数
            entity.setShinmtmkipointsu(getIntegerNullable(shopTabBean.getShinmtmkipointsu()));
            // 新まとめ買い段階的発生の数量１
            entity.setShinmtmkidankaisuryo1(getIntegerNullable(shopTabBean.getShinmtmkidankaisuryo1()));
            // 新まとめ買い段階的発生のポイント数１
            entity.setShinmtmkidankaipointsu1(getIntegerNullable(shopTabBean.getShinmtmkidankaipointsu1()));
            // 新まとめ買い段階的発生の数量２
            entity.setShinmtmkidankaisuryo2(getIntegerNullable(shopTabBean.getShinmtmkidankaisuryo2()));
            // 新まとめ買い段階的発生のポイント数２
            entity.setShinmtmkidankaipointsu2(getIntegerNullable(shopTabBean.getShinmtmkidankaipointsu2()));
            // 新まとめ買い段階的発生の数量３
            entity.setShinmtmkidankaisuryo3(getIntegerNullable(shopTabBean.getShinmtmkidankaisuryo3()));
            // 新まとめ買い段階的発生のポイント数３
            entity.setShinmtmkidankaipointsu3(getIntegerNullable(shopTabBean.getShinmtmkidankaipointsu3()));
            // 新まとめ買い段階的発生の数量４
            entity.setShinmtmkidankaisuryo4(getIntegerNullable(shopTabBean.getShinmtmkidankaisuryo4()));
            // 新まとめ買い段階的発生のポイント数４
            entity.setShinmtmkidankaipointsu4(getIntegerNullable(shopTabBean.getShinmtmkidankaipointsu4()));
            // 新まとめ買い段階的発生の数量５
            entity.setShinmtmkidankaisuryo5(getIntegerNullable(shopTabBean.getShinmtmkidankaisuryo5()));
            // 新まとめ買い段階的発生のポイント数５
            entity.setShinmtmkidankaipointsu5(getIntegerNullable(shopTabBean.getShinmtmkidankaipointsu5()));
        }

        // 有効区分
        entity.setYukokbn(shopTabBean.getYukokbn());
        // 更新者AP基本コード
        entity.setKoshaapkihoncd(dbManagementSession.getApkihoncd());
        // 更新者ショップコード
        entity.setKoshashopcd(dbManagementSession.getShopcd());
        // 更新者コード
        entity.setKoshacd(dbManagementSession.getTnshacd());
        // 更新システム日付
        entity.setKshnsystmymd(dbManagementSession.getSystemDate());
        // 更新システム時間
        entity.setKshnsystmjikan(dbManagementSession.getSystemTime());
        // 更新運用日付
        entity.setKshnunyoymd(dbManagementSession.getUnyoDate());
        // 更新IPアドレス
        entity.setKshnipadrse(dbManagementSession.getIpAddress());
        // 更新プログラムID
        entity.setKshnpgmid(dbManagementSession.getProgramId());
        log.debug("ショップ商品ポイントマスタ : {}", entity);
        return entity;
    }

    /**
     * BigDecimalの値を取得する（元の値がnullまたは空文字の場合はnull）
     * 
     * @param value 値
     * @return BigDecimal
     */
    private BigDecimal getDecimalNullable(String value) {
        if (StringUtil.isEmpty(value)) {
            return null;
        }
        return new BigDecimal(ItemUtil.removeComma(value));
    }

    /**
     * Integerの値を取得する（元の値がnullまたは空文字の場合はnull）
     * 
     * @param value 値
     * @return BigDecimal
     */
    private Integer getIntegerNullable(String value) {
        if (StringUtil.isEmpty(value)) {
            return null;
        }
        return Integer.parseInt(ItemUtil.removeComma(value));
    }

    /**
     * 検索結果をControllerへ返却する形式へ変換する
     * 
     * @param e 検索結果
     * @param unyodate 運用j日付
     * @return 画面表示用DTO
     */
    private A0301W02DisplayDto createSelectResultDto(S02V200TA0301W02Entity e, String unyodate) {

        A0301W02DisplayDto dto = new A0301W02DisplayDto();

        dto.setApkihoncd(e.getApkihoncd());
        dto.setShopcd(e.getShopcd());
        dto.setStkbn(e.getStkbn());
        dto.setShncd(e.getShncd());
        dto.setShnapkihoncd(e.getShnapkihoncd());
        dto.setShnnm(e.getShnnm());
        dto.setTrtskikbnnm(e.getTrtskikbnnm());
        dto.setShnzokusei(e.getShnzokusei());
        dto.setShnzokuseinm(e.getShnzokuseinm());
        dto.setPricekbn(e.getPricekbn());
        dto.setPricekbnnm(e.getPricekbnnm());
        dto.setLctgrycd(e.getLctgrycd());
        dto.setMctgrycd(e.getMctgrycd());
        dto.setSctgrycd(e.getSctgrycd());
        dto.setLctgrynm(e.getLctgrynm());
        dto.setMctgrynm(e.getMctgrynm());
        dto.setSctgrynm(e.getSctgrynm());
        dto.setShinshnnm(e.getShinshnnm());
        dto.setShinshnnmkana(e.getShinshnnmkana());
        dto.setShinshnnmryakusho(e.getShinshnnmryakusho());

        String shnnmkirikaedd = (StringUtils.isEmpty(e.getShnnmkirikaedd())) ? ""
            : DateTimeUtil.toSlashedDate(e.getShnnmkirikaedd());
        dto.setShnnmkirikaedd(shnnmkirikaedd);

        dto.setGenzshnnm(e.getGenzshnnm());
        dto.setGenzshnnmkana(e.getGenzshnnmkana());
        dto.setGenzshnnmryakusho(e.getGenzshnnmryakusho());
        dto.setShinhyojunprice(e.getShinhyojunprice());
        dto.setShinmochikaeriprice(e.getShinmochikaeriprice());
        dto.setShinpricekbn(e.getShinpricekbn());

        String hyojunpricekirikaedd = (StringUtils.isEmpty(e.getHyojunpricekirikaedd())) ? ""
            : DateTimeUtil.toSlashedDate(e.getHyojunpricekirikaedd());
        dto.setHyojunpricekirikaedd(hyojunpricekirikaedd);

        dto.setGenzhyojunprice(e.getGenzhyojunprice());
        dto.setGenzmochikaeriprice(e.getGenzmochikaeriprice());
        dto.setGenzpricekbn(e.getGenzpricekbn());

        String shnpointkirikaedd = (StringUtils.isEmpty(e.getShnpointkirikaedd())) ? ""
                : DateTimeUtil.toSlashedDate(e.getShnpointkirikaedd());
        dto.setShnpointkirikaedd(shnpointkirikaedd);

        dto.setGenshnpointhasseikbn(e.getGenshnpointhasseikbn());
        switch(e.getGenshnpointhasseikbn()) {
            case "0": // 発生しない
            case "1": // グループ共通設定
                break;
            case "2": // 現還元率値
                dto.setGenshnpointsu1(e.getGenshnpointsu());
                break;
            case "3": // 現固定値
                dto.setGenshnpointsu2(e.getGenshnpointsu());
                break;
        }
        dto.setShinshnpointhasseikbn(e.getShinshnpointhasseikbn());
        switch(e.getShinshnpointhasseikbn()) {
            case "0": // 発生しない
            case "1": // グループ共通設定
                break;
            case "2": // 新還元率値
                dto.setShinshnpointsu1(e.getShinshnpointsu());
                break;
            case "3": // 新固定値
                dto.setShinshnpointsu2(e.getShinshnpointsu());
                break;
        }

        dto.setSz(e.getSz());
        dto.setIro(e.getIro());
        dto.setTrtskikbn(e.getTrtskikbn());
        dto.setSuryohasukanrikbn(e.getSuryohasukanrikbn());
        dto.setShnkbn(e.getShnkbn());
        dto.setBiko1(e.getKihonshnbiko());
        dto.setKihonshnyukokbn(e.getKihonshnyukokbn());
        dto.setHanbaistrtdd(e.getHanbaistrtdd());
        dto.setHanbaishrydd(e.getHanbaishrydd());
        dto.setGaiburenkeishncd(e.getGaiburenkeishncd());
        dto.setHonbuhanbaikanrishncd(e.getHonbuhanbaikanrishncd());
        dto.setMgntrgtkbn(e.getMgntrgtkbn());
        dto.setMobileriyoumu(e.getMobileriyoumu());
        dto.setTaxsetteikbn(e.getTaxsetteikbn());
        dto.setTaxkbn(e.getTaxkbn());
        dto.setSiiretrtskitani(e.getSiiretrtskitani());
        dto.setSiireirisu(e.getSiireirisu());
        dto.setSiiretani(e.getSiiretani());
        dto.setSiiretaninm(e.getSiiretaninm());
        dto.setSiireunt(e.getSiireunt());
        dto.setHacchurottokazu(e.getHacchurottokazu());
        dto.setUritrtskitani(e.getUritrtskitani());
        dto.setUriirisu(e.getUriirisu());
        dto.setUritani(e.getUritani());
        dto.setShinopnprice(e.getShinopnprice());
        dto.setShinopnmochikaeriprice(e.getShinopnmochikaeriprice());

        String opnpricekirikaedd = (StringUtils.isEmpty(e.getOpnpricekirikaedd())) ? ""
            : DateTimeUtil.toSlashedDate(e.getOpnpricekirikaedd());
        dto.setOpnpricekirikaedd(opnpricekirikaedd);

        dto.setGenzopnprice(e.getGenzopnprice());
        dto.setGenzopnmochikaeriprice(e.getGenzopnmochikaeriprice());

        String mtmkipointkirikaedd = (StringUtils.isEmpty(e.getMtmkipointkirikaedd())) ? ""
                : DateTimeUtil.toSlashedDate(e.getMtmkipointkirikaedd());
        dto.setMtmkipointkirikaedd(mtmkipointkirikaedd);

        dto.setGenmtmkipointkbn(e.getGenmtmkipointkbn());
        dto.setGenmtmkisuryo(e.getGenmtmkisuryo());
        dto.setGenmtmkipointsu(e.getGenmtmkipointsu());
        dto.setGenmtmkidankaisuryo1(e.getGenmtmkidankaisuryo1());
        dto.setGenmtmkidankaipointsu1(e.getGenmtmkidankaipointsu1());
        dto.setGenmtmkidankaisuryo2(e.getGenmtmkidankaisuryo2());
        dto.setGenmtmkidankaipointsu2(e.getGenmtmkidankaipointsu2());
        dto.setGenmtmkidankaisuryo3(e.getGenmtmkidankaisuryo3());
        dto.setGenmtmkidankaipointsu3(e.getGenmtmkidankaipointsu3());
        dto.setGenmtmkidankaisuryo4(e.getGenmtmkidankaisuryo4());
        dto.setGenmtmkidankaipointsu4(e.getGenmtmkidankaipointsu4());
        dto.setGenmtmkidankaisuryo5(e.getGenmtmkidankaisuryo5());
        dto.setGenmtmkidankaipointsu5(e.getGenmtmkidankaipointsu5());
        dto.setShinmtmkipointkbn(e.getShinmtmkipointkbn());
        dto.setShinmtmkisuryo(e.getShinmtmkisuryo());
        dto.setShinmtmkipointsu(e.getShinmtmkipointsu());
        dto.setShinmtmkidankaisuryo1(e.getShinmtmkidankaisuryo1());
        dto.setShinmtmkidankaipointsu1(e.getShinmtmkidankaipointsu1());
        dto.setShinmtmkidankaisuryo2(e.getShinmtmkidankaisuryo2());
        dto.setShinmtmkidankaipointsu2(e.getShinmtmkidankaipointsu2());
        dto.setShinmtmkidankaisuryo3(e.getShinmtmkidankaisuryo3());
        dto.setShinmtmkidankaipointsu3(e.getShinmtmkidankaipointsu3());
        dto.setShinmtmkidankaisuryo4(e.getShinmtmkidankaisuryo4());
        dto.setShinmtmkidankaipointsu4(e.getShinmtmkidankaipointsu4());
        dto.setShinmtmkidankaisuryo5(e.getShinmtmkidankaisuryo5());
        dto.setShinmtmkidankaipointsu5(e.getShinmtmkidankaipointsu5());
        
        dto.setHansokudlvryprice(e.getHansokudlvryprice());
        dto.setHansokumochikaeriprice(e.getHansokumochikaeriprice());
        dto.setHansokuyusenkbn(e.getHansokuyusenkbn());

        if (!StringUtils.isEmpty(e.getOpnpricekirikaedd())) {
            if (!DateTimeUtil.isLater(e.getOpnpricekirikaedd(), unyodate)) {
                dto.setHyojundlvryprice(e.getGenzhyojunprice());
                dto.setHyojunmochikaeriprice(e.getGenzmochikaeriprice());
            } else {
                dto.setHyojundlvryprice(e.getShinhyojunprice());
                dto.setHyojunmochikaeriprice(e.getShinmochikaeriprice());
            }
        }

        dto.setHansokukbn(e.getHansokukbn());

        String hansokustrtdd = (StringUtils.isEmpty(e.getHansokustrtdd())) ? ""
            : DateTimeUtil.toSlashedDate(e.getHansokustrtdd());
        dto.setHansokustrtdd(hansokustrtdd);

        String hansokushrydd = (StringUtils.isEmpty(e.getHansokushrydd())) ? ""
            : DateTimeUtil.toSlashedDate(e.getHansokushrydd());
        dto.setHansokushrydd(hansokushrydd);

        dto.setHansokunm(e.getHansokunm());
        dto.setZaikokanritrgtflg(e.getZaikokanritrgtflg());
        dto.setGenkakeisankbn(e.getGenkakeisankbn());
        dto.setZaikount(e.getZaikount());
        dto.setSiiretanihenkankbn(e.getSiiretanihenkankbn());
        dto.setZaikoaramusuryo(e.getZaikoaramusuryo());
        dto.setShopbiko(e.getShopbiko());
        dto.setShopyukokbn(e.getShopyukokbn());

        String kihonshninptsystmymd = (StringUtils.isEmpty(e.getKihonshninptsystmymd())) ? ""
            : DateTimeUtil.toSlashedDate(e.getKihonshninptsystmymd());
        dto.setKihonshninptsystmymd(kihonshninptsystmymd);

        String kihonshninptsystmjikan = (StringUtils.isEmpty(e.getKihonshninptsystmjikan())) ? ""
            : DateTimeUtil.toColonedDateHHMMSS(e.getKihonshninptsystmjikan());
        dto.setKihonshninptsystmjikan(kihonshninptsystmjikan);

        dto.setKihonshntoshaapkihoncd(e.getKihonshntoshaapkihoncd());
        dto.setKihonshntoshashopcd(e.getKihonshntoshashopcd());
        dto.setKihonshntoshacd(e.getKihonshntoshacd());
        dto.setKihonshntoshanm(e.getKihonshntoshanm());

        String kihonshnkshnsystmymd = (StringUtils.isEmpty(e.getKihonshnkshnsystmymd())) ? ""
            : DateTimeUtil.toSlashedDate(e.getKihonshnkshnsystmymd());
        dto.setKihonshnkshnsystmymd(kihonshnkshnsystmymd);

        String kihonshnkshnsystmjikan = (StringUtils.isEmpty(e.getKihonshnkshnsystmjikan())) ? ""
            : DateTimeUtil.toColonedDateHHMMSS(e.getKihonshnkshnsystmjikan());
        dto.setKihonshnkshnsystmjikan(kihonshnkshnsystmjikan);

        dto.setKihonshnkoshaapkihoncd(e.getKihonshnkoshaapkihoncd());
        dto.setKihonshnkoshashopcd(e.getKihonshnkoshashopcd());
        dto.setKihonshnkoshacd(e.getKihonshnkoshacd());
        dto.setKihonshnkoshanm(e.getKihonshnkoshanm());

        String shopinptsystmymd = (StringUtils.isEmpty(e.getShopinptsystmymd())) ? ""
            : DateTimeUtil.toSlashedDate(e.getShopinptsystmymd());
        dto.setShopinptsystmymd(shopinptsystmymd);

        String shopinptsystmjikan = (StringUtils.isEmpty(e.getShopinptsystmjikan())) ? ""
            : DateTimeUtil.toColonedDateHHMMSS(e.getShopinptsystmjikan());
        dto.setShopinptsystmjikan(shopinptsystmjikan);

        dto.setShoptoshaapkihoncd(e.getShoptoshaapkihoncd());
        dto.setShoptoshashopcd(e.getShoptoshashopcd());
        dto.setShoptoshacd(e.getShoptoshacd());
        dto.setShoptoshanm(e.getShoptoshanm());

        String shopkshnsystmymd = (StringUtils.isEmpty(e.getShopkshnsystmymd())) ? ""
            : DateTimeUtil.toSlashedDate(e.getShopkshnsystmymd());
        dto.setShopkshnsystmymd(shopkshnsystmymd);

        String shopkshnsystmjikan = (StringUtils.isEmpty(e.getShopkshnsystmjikan())) ? ""
            : DateTimeUtil.toColonedDateHHMMSS(e.getShopkshnsystmjikan());
        dto.setShopkshnsystmjikan(shopkshnsystmjikan);

        dto.setShopkoshaapkihoncd(e.getShopkoshaapkihoncd());
        dto.setShopkoshashopcd(e.getShopkoshashopcd());
        dto.setShopkoshacd(e.getShopkoshacd());
        dto.setShopkoshanm(e.getShopkoshanm());

        return dto;
    }

    /**
     * 指定された商品を構成商品に含む有効なセット商品の有無を返す
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param shncd 商品コード
     * @param shnapkihoncd 商品AP基本コード
     * @return 有効なセット商品が存在していた場合はtrue
     */
    public boolean isExistsAvailableStshn(String apkihoncd, String shopcd, String shncd,
        String shnapkihoncd) {

        // 展開マスタから指定された商品が構成商品となっているセット商品の情報を取得する
        S02M008TEntityExample compositionCondition = new S02M008TEntityExample();
        compositionCondition.createCriteria()
            // 構成商品AP基本コード
            .andKoseishnapkihoncdEqualTo(shnapkihoncd)
            // 構成商品コード
            .andKoseishncdEqualTo(shncd)
            // 有効区分
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<S02M008TEntity> compositionEntities =
            s02m008tDao.selectByExample(compositionCondition);
        if (compositionEntities.isEmpty()) {
            // 展開マスタに指定された商品の情報が存在しない
            return false;
        }
        // セット商品の情報をマスタから取得する
        for (S02M008TEntity bundleShnEntity : compositionEntities) {
            S02V200TEntityExample shnCondition = new S02V200TEntityExample();
            shnCondition.createCriteria()
                // AP基本コード
                .andApkihoncdEqualTo(apkihoncd)
                // ショップコード
                .andShopcdEqualTo(shopcd)
                // 商品コード
                .andShncdEqualTo(bundleShnEntity.getStshncd());
            List<S02V200TEntity> stShnEntities = s02V200tDao.selectByExample(shnCondition);
            if (stShnEntities.stream().anyMatch(
                shnEntity -> CommonConstants.YUKOKBN_1.equals(shnEntity.getShopyukokbn()))) {
                // 有効なセット商品が含まれている場合は存在するとする
                return true;
            }
        }
        // 条件を満たさない場合は存在しないものとする
        return false;
    }

    /**
     * 展開マスタの更新対象となるその他APのリストを取得する
     * 
     * @param apkihoncd 商品のAP基本コード
     * @param lctgry 商品の大分類コード
     * @return AP基本マスタのリスト
     */
    private List<S02M001TEntity> getTargetApList(String apkihoncd, String lctgry) {
        if (CommonConstants.LCTGRYCD_90.equals(lctgry)) {
            // AP商品の場合はその他APは対象外のためリストは空で返す
            return List.of();
        }
        S02M001TEntityExample apCondition = new S02M001TEntityExample();
        // 自AP以外で有効なAPの情報を取得
        apCondition.createCriteria().andApkihoncdNotEqualTo(apkihoncd)
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        return s02m001tDao.selectByExample(apCondition);
    }
}
