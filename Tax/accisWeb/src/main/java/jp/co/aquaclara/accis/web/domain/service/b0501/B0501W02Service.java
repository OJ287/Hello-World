package jp.co.aquaclara.accis.web.domain.service.b0501;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M003TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M006TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M009TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M013TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M014TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M015TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M016TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M017TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M018TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M019TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M020TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M027TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M031TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M041TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M046TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M048TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M050TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M052TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M059TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M066TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M067TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M081TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M083TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M084TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M085TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M202TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M203TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M204TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02V001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S03T003TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S03T018TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S03T022TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S03T023TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T002TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S06T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S06T003TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S10T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S20T003TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M001TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M003TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M003TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M006TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M006TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M006TEntityExample.Criteria;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M009TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M009TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M014TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M014TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M015TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M015TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M016TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M016TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M016TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M018TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M018TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M019TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M019TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M020TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M020TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M027TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M027TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M031TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M031TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M041TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M041TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M048TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M048TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M050TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M050TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M052TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M052TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M059TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M059TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M066TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M066TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M067TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M067TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M081TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M081TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M083TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M083TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M084TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M084TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M085TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M085TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M202TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M202TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M203TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M203TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M204TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M204TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S03T003TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S03T003TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S03T018TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S03T018TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S03T022TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S06T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S06T001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S06T003TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S06T003TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S10T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S10T001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S20T003TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S20T003TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M003TKinoIdKengenEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M013TB0501W02BlackCheckEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M013TB0501W02Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M016TB0501W02Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M016TB0501W02SummaryEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M084TB0501W02Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M085TB0501W02Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M203TB0501W02Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02V001TB0501W02Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S04T001TC0805W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S04T002TC0805W01Entity;
import jp.co.aquaclara.accis.common.util.ConvertUtil;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.controller.commons.UserSessionForm;
import jp.co.aquaclara.accis.web.domain.dto.b0501.B0501W02AddressDto;
import jp.co.aquaclara.accis.web.domain.dto.b0501.B0501W02DisplayDto;
import jp.co.aquaclara.accis.web.domain.dto.b0501.B0501W02ShnDto;
import jp.co.aquaclara.accis.web.domain.dto.b0501.B0501W02UpdateDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M006TDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M015TDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M017TDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M018TDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M200TDto;
import jp.co.aquaclara.accis.web.domain.service.business.CustomerBarCodeService;
import jp.co.aquaclara.accis.web.domain.service.business.SaibanService;
import jp.co.aquaclara.accis.web.domain.service.business.TaxService;
import jp.co.aquaclara.accis.web.domain.service.master.S02M006TService;
import jp.co.aquaclara.accis.web.domain.service.master.S02M200TService;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import jp.co.aquaclara.accis.web.exception.dto.FormField;
import lombok.extern.slf4j.Slf4j;

/**
 * 得意先マスタ(登録画面)Service
 * 
 * @author accisdev021
 *
 */
@Service
@Slf4j
public class B0501W02Service {

    @Autowired
    private S02M001TDao s02m001tDao;

    @Autowired
    private S02M003TDao s02m003tDao;

    @Autowired
    private S02M006TDao s02m006tDao;

    @Autowired
    private S02M009TDao s02m009tDao;

    @Autowired
    private S02M013TDao s02m013tDao;

    @Autowired
    private S02M014TDao s02m014tDao;

    @Autowired
    private S02M015TDao s02m015tDao;

    @Autowired
    private S02M016TDao s02m016tDao;

    @Autowired
    private S02M017TDao s02m017tDao;

    @Autowired
    private S02M018TDao s02m018tDao;

    @Autowired
    private S02M019TDao s02m019tDao;

    @Autowired
    private S02M020TDao s02m020tDao;

    @Autowired
    private S02M027TDao s02m027tDao;

    @Autowired
    private S02M031TDao s02m031tDao;

    @Autowired
    private S02M041TDao s02m041tDao;

    @Autowired
    private S02M046TDao s02m046tDao;

    @Autowired
    private S02M048TDao s02m048tDao;

    @Autowired
    private S02M050TDao s02m050tDao;

    @Autowired
    private S02M052TDao s02m052tDao;

    @Autowired
    private S02M059TDao s02m059tDao;

    @Autowired
    private S02M066TDao s02m066tDao;

    @Autowired
    private S02M067TDao s02m067tDao;

    @Autowired
    private S02M081TDao s02m081tDao;

    @Autowired
    private S02M083TDao s02m083tDao;

    @Autowired
    private S02M084TDao s02m084tDao;

    @Autowired
    private S02M085TDao s02m085tDao;

    @Autowired
    private S02M202TDao s02m202tDao;

    @Autowired
    private S02M203TDao s02m203tDao;

    @Autowired
    private S02M204TDao s02m204tDao;

    @Autowired
    private S02V001TDao s02v001tDao;

    @Autowired
    private S03T003TDao s03t003tDao;

    @Autowired
    private S03T018TDao s03t018tDao;

    @Autowired
    private S03T022TDao s03t022tDao;

    @Autowired
    private S03T023TDao s03t023tDao;

    @Autowired
    private S04T001TDao s04t001tDao;

    @Autowired
    private S04T002TDao s04t002tDao;

    @Autowired
    private S06T001TDao s06t001tDao;

    @Autowired
    private S06T003TDao s06t003tDao;

    @Autowired
    private S10T001TDao s10t001tDao;

    @Autowired
    private S20T003TDao s20t003tDao;

    @Autowired
    private S02M006TService s02m006tService;

    @Autowired
    private S02M200TService s02m200tService;

    @Autowired
    private SaibanService saibanService;

    @Autowired
    private TaxService taxService;

    @Autowired
    private CustomerBarCodeService customerBarCodeService;

    @Autowired
    private UserSessionForm userSession;

    // 機能ID
    private final String KINOID_B0505W00 = "B0505W00"; // 定例売上マスタ
    private final String KINOID_B0504W00 = "B0504W00"; // 得意先設置サーバーマスタ
    private final String KINOID_B0503W00 = "B0503W00"; // 請求先マスタ修正・照会
    private final String KINOID_Z0507W00 = "Z0507W00"; // 会員マスタ照会
    private final String KINOID_B0502W00 = "B0502W00"; // 違約金情報
    private final String KINOID_B0801W00 = "B0801W00"; // 定期受注登録・照会

    // アクセス可否
    private final String ACCESS_ON = "1"; // アクセス可

    // 請求設定区分ラジオボタンの選択値
    private final String SKYSETTEIKBN_1 = "1"; // コード指定
    private final String SKYSETTEIKBN_2 = "2"; // 自動生成

    // 会員コード区分ラジオボタンの選択値
    private final String KAIINCDKBN_0 = "0"; // 指定なし
    private final String KAIINCDKBN_1 = "1"; // コード指定
    private final String KAIINCDKBN_2 = "2"; // 自動生成

    // 売上設定ラジオボタンの選択値
    private final String URISETTEI_0 = "0"; // 定例売上マスタ
    private final String URISETTEI_1 = "1"; // 得意先設置サーバーマスタ

    private final Short SHORT_ZERO = Short.valueOf("0");
    private final long LONG_ZERO = Long.valueOf(0);

    private final String END_DATE = "31";

    private static final String EMPTY = "";

    /**
     * 初期値取得処理(新規登録時)
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param unyodd 運用日付
     * @return B0501W02DisplayDto
     */
    public B0501W02DisplayDto init(String apkihoncd, String shopcd, String unyodd) {
        // ショップマスタの取得
        S02M046TEntity shopEntity = getShopMst(apkihoncd, shopcd);

        // ショップポイントマスタの取得
        S02M202TEntity shopPointEntity = getShopPointMst(apkihoncd, shopcd);

        // 結果セットをdtoに詰めて返却
        return createDisplayDto(shopEntity, shopPointEntity, unyodd);
    }

    /**
     * 初期値取得処理(更新時)
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @param unyodd 運用日付
     * @return B0501W02DisplayDto
     */
    public B0501W02DisplayDto init(String apkihoncd, String shopcd, String tokuisakicd,
        String unyodd) {
        // ==============
        // 得意先マスタの取得
        // ==============
        S02M013TB0501W02Entity tokuisakiEntity = s02m013tDao
            .selectByB0501W02(createTokuisakiEntityKey(apkihoncd, shopcd, tokuisakicd, unyodd));

        // ==============
        // 得意先予約マスタの取得
        // ==============
        S02M084TB0501W02Entity tokuisakiYoyakuEntity = new S02M084TB0501W02Entity();
        S02M084TB0501W02Entity tokuisakiYoyakuEntityKey =
            createTokuisakiYoyakuEntityKey(apkihoncd, shopcd, tokuisakicd, unyodd);
        List<S02M084TB0501W02Entity> tokuisakiYoyakuEntityList =
            s02m084tDao.selectByB0501W02(tokuisakiYoyakuEntityKey);

        if (!ObjectUtils.isEmpty(tokuisakiYoyakuEntityList)
            && tokuisakiYoyakuEntityList.size() > 0) {
            tokuisakiYoyakuEntity = tokuisakiYoyakuEntityList.get(0);
        } else {
            // 得意先予約マスタに存在しない場合、得意先マスタから取得する
            tokuisakiYoyakuEntity = s02m013tDao.selectYoyakuByB0501W02(tokuisakiYoyakuEntityKey);
            if (ObjectUtils.isEmpty(tokuisakiYoyakuEntity)) {
                tokuisakiYoyakuEntity = new S02M084TB0501W02Entity();
            }
        }

        // ==============
        // 請求先予約マスタの取得
        // ==============
        S02M085TB0501W02Entity skysakiYoyakuEntity = new S02M085TB0501W02Entity();
        if (!StringUtils.isEmpty(tokuisakiEntity.getSkysakicd())) {
            S02M085TB0501W02Entity skysakiYoyakuEntityKey =
                createSkysakiYoyakuEntityKey(apkihoncd, shopcd, tokuisakiEntity.getSkysakicd());
            List<S02M085TB0501W02Entity> skysakiYoyakuEntityList =
                s02m085tDao.selectByB0501W02(skysakiYoyakuEntityKey);

            if (!ObjectUtils.isEmpty(skysakiYoyakuEntityList)
                && skysakiYoyakuEntityList.size() > 0) {
                skysakiYoyakuEntity = skysakiYoyakuEntityList.get(0);
            } else {
                // 請求先予約マスタに存在しない場合、請求先マスタから取得する
                skysakiYoyakuEntity = s02m017tDao.selectByB0501W02(skysakiYoyakuEntityKey);
                if (ObjectUtils.isEmpty(skysakiYoyakuEntity)) {
                    skysakiYoyakuEntity = new S02M085TB0501W02Entity();
                }
            }
        }

        // ==============
        // 定例売上仕入マスタの取得
        // ==============
        List<S02M016TB0501W02Entity> teireiUriageEntityList = s02m016tDao
            .selectByB0501W02(createTeireiUriageShireEntityKey(apkihoncd, shopcd, tokuisakicd));

        // ==============
        // ショップマスタ取得
        // ==============
        S02M046TEntity shopEntity = getShopMst(apkihoncd, shopcd);

        // ==============
        // 得意先ポイントマスタ取得
        // ==============
        S02M203TB0501W02Entity tokuisakiPointEntity = s02m203tDao.selectByB0501W02(
            createTokuisakiPointEntityKey(apkihoncd, shopcd, tokuisakicd, unyodd));

        // 結果セットをdtoに詰めて返却
        return createDisplayDto(tokuisakiEntity, teireiUriageEntityList, tokuisakiYoyakuEntity,
            skysakiYoyakuEntity, shopEntity, unyodd, tokuisakiPointEntity);
    }

    /**
     * 定例売上仕入マスタキーEntityを生成する
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @return S02M016TB0501W02Entity
     */
    private S02M016TB0501W02Entity createTeireiUriageShireEntityKey(String apkihoncd, String shopcd,
        String tokuisakicd) {

        S02M016TB0501W02Entity entity = new S02M016TB0501W02Entity();

        entity.setApkihoncd(apkihoncd); // ＡＰ基本コード
        entity.setShopcd(shopcd); // ショップコード
        entity.setTokuisakicd(tokuisakicd); // 得意先コード

        return entity;
    }

    /**
     * 各種機能のアクセス可否を設定する
     * 
     * @param displayDto 得意先情報DTO
     */
    private void setAccessKahi(B0501W02DisplayDto displayDto) {

        List<String> kinoids = Arrays.asList(KINOID_B0505W00, KINOID_B0504W00, KINOID_B0503W00,
            KINOID_Z0507W00, KINOID_B0502W00, KINOID_B0801W00);

        // 検索条件作成
        S02M003TKinoIdKengenEntity condition = new S02M003TKinoIdKengenEntity();
        condition.setApkihoncd(userSession.getApkihoncd());
        condition.setShopcd(userSession.getShopcd());
        condition.setTnshacd(userSession.getTnshacd());
        condition.setKinoids(kinoids);

        // 担当者マスタから取得
        List<S02M003TKinoIdKengenEntity> kengenList =
            s02m003tDao.selectForKinoIdKengenByList(condition);

        for (S02M003TKinoIdKengenEntity e : kengenList) {
            if (KINOID_B0505W00.equals(e.getKinoid())) {
                displayDto.setAccesskahiteireiuriage(ACCESS_ON); // 定例売上マスタアクセス可否
            } else if (KINOID_B0504W00.equals(e.getKinoid())) {
                displayDto.setAccesskahitokuisakisettisv(ACCESS_ON); // 得意先設置サーバーアクセス可否
            } else if (KINOID_B0503W00.equals(e.getKinoid())) {
                displayDto.setAccesskahiskysaki(ACCESS_ON); // 請求先マスタ修正・照会アクセス可否
            } else if (KINOID_Z0507W00.equals(e.getKinoid())) {
                displayDto.setAccesskahikain(ACCESS_ON); // 会員マスタ照会アクセス可否
            } else if (KINOID_B0502W00.equals(e.getKinoid())) {
                displayDto.setAccesskahiiyakukin(ACCESS_ON); // 違約金情報アクセス可否
            } else if (KINOID_B0801W00.equals(e.getKinoid())) {
                displayDto.setAccesskahiteikijuchu(ACCESS_ON);// 定期受注登録・照会アクセス可否
            }
        }
    }

    /**
     * 会員作成有無区分を取得する
     * 
     * @return 会員作成有無区分
     */
    private String getKainsakuseiumukbn() {

        String ret = "";

        // 検索条件作成
        S02M006TDto conditionDto = new S02M006TDto();
        conditionDto.setApkihoncd(CommonConstants.APCD_000);
        conditionDto.setShopcd(CommonConstants.SYS_SHOPCD_000);
        conditionDto.setSetteino(CommonConstants.SYS_SETTEINO_225);

        // システム管理マスタから取得
        List<S02M006TDto> dtoList = s02m006tService.search(conditionDto, null, null);
        if (!ObjectUtils.isEmpty(dtoList) && dtoList.size() > 0) {
            ret = dtoList.get(0).getMojisetteiatai();
        }

        return ret;
    }

    /**
     * サーバーレンタル料金を取得する
     * 
     * @param e 得意先マスタカスタムエンティティ
     * @param unyodd 運用日付
     * @return サーバーレンタル料金
     */
    public String getSvrtlrkn(S02M013TB0501W02Entity e, String unyodd) {

        String ret = "0";

        // 定例売上マスタ集計サーバーレンタル料金情報を取得する
        S02M016TB0501W02SummaryEntity sum1 = s02m016tDao.selectSummaryByB0501W02(
            createTeireiUriageShireEntityKey(e, unyodd, CommonConstants.RTLRKNSHUKEIKBN_0));

        if (!ObjectUtils.isEmpty(sum1) && sum1.getSummary() > 0) {
            ret = String.valueOf(sum1.getSummary());

            // 0件の場合
        } else {
            // 得意先設置サーバーマスタ集計サーバーレンタル料金情報を取得する
            S02M016TB0501W02SummaryEntity sum2 = s02m016tDao.selectSummaryByB0501W02(
                createTeireiUriageShireEntityKey(e, unyodd, CommonConstants.RTLRKNSHUKEIKBN_1));

            if (!ObjectUtils.isEmpty(sum2) && sum2.getSummary() > 0) {
                ret = String.valueOf(sum2.getSummary());
            }
        }

        return ret;
    }

    /**
     * お預けボトル商品名称を取得する
     * 
     * @param setteicd 設定コード
     * @return 預かりボトル名称
     */
    private String getBottleName(String setteicd) {

        // 検索条件作成
        S02M006TDto conditionDto = new S02M006TDto();
        conditionDto.setApkihoncd(CommonConstants.APCD_000);
        conditionDto.setShopcd(CommonConstants.SYS_SHOPCD_000);
        conditionDto.setSetteino(CommonConstants.SYS_SETTEINO_114);
        conditionDto.setSetteicd(setteicd);

        // システム管理マスタから取得
        return s02m006tService.search(conditionDto);
    }

    /**
     * 違約金管理設定フラグを取得する
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @return 違約金管理設定フラグ
     */
    private String getIyakukinkanriflg(String apkihoncd, String shopcd) {

        String ret = "";

        // エンティティのインスタンス生成
        S02M006TEntityExample entityExample = new S02M006TEntityExample();

        // クライテリア(SQL条件)の生成
        Criteria criteria = entityExample.createCriteria();
        criteria.andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd)
            .andSetteinoEqualTo(CommonConstants.SYS_SETTEINO_172)
            .andSetteicdEqualTo(CommonConstants.SETTEICD_172_1)
            .andRcrdkbnEqualTo(CommonConstants.RECORD_KBN_M);

        // SELECT実行
        List<S02M006TEntity> entities = s02m006tDao.selectByExample(entityExample);

        if (ObjectUtils.isEmpty(entities)
            || StringUtils.isEmpty(entities.get(0).getSuchisetteiatai())) {
            S02M006TDto conditionDto = new S02M006TDto();
            conditionDto.setApkihoncd(CommonConstants.APCD_000);
            conditionDto.setShopcd(CommonConstants.SYS_SHOPCD_000);
            conditionDto.setSetteino(CommonConstants.SYS_SETTEINO_172);
            conditionDto.setSetteicd(CommonConstants.SETTEICD_172_1);

            ret = String.valueOf(s02m006tService.searchForSuchi(conditionDto));
        } else {
            ret = String.valueOf(entities.get(0).getSuchisetteiatai());
        }

        return ret;
    }

    /**
     * 売上計上区分名を取得する
     * 
     * @return 売上計上区分名
     */
    private String getKeijokbnnm() {

        // 検索条件作成
        S02M006TDto conditionDto = new S02M006TDto();
        conditionDto.setApkihoncd(CommonConstants.APCD_000);
        conditionDto.setShopcd(CommonConstants.SYS_SHOPCD_000);
        conditionDto.setSetteino(CommonConstants.SYS_SETTEINO_009);
        conditionDto.setSetteicd(CommonConstants.KBN_KEIJO_URIAGE);

        // システム管理マスタから取得
        return s02m006tService.search(conditionDto);
    }

    /**
     * ポイントランク（現）を取得する
     * 
     * @param unyodd 運用日付
     * @return ポイントランクコード
     */
    private S02M200TDto getPointRnkGen(String unyodd) {

        // ポイントランクマスタから取得
        S02M200TDto dto = s02m200tService.search(unyodd);

        return dto;
    }

    /**
     * 得意先マスタキーEntityを生成する（更新モード初期表示検索用）
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @param unyodd 運用日付
     * @return S02M013TB0501W02Entity
     */
    private S02M013TB0501W02Entity createTokuisakiEntityKey(String apkihoncd, String shopcd,
        String tokuisakicd, String unyodd) {

        S02M013TB0501W02Entity entity = new S02M013TB0501W02Entity();

        entity.setApkihoncd(apkihoncd); // ＡＰ基本コード
        entity.setShopcd(shopcd); // ショップコード
        entity.setTokuisakicd(tokuisakicd); // 得意先コード
        entity.setUnyodd(unyodd); // 運用日付

        return entity;
    }

    /**
     * 得意先予約マスタキーEntityを生成する（更新モード初期表示検索用）
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @param unyodd 運用日付
     * @return S02M084TB0501W02Entity
     */
    private S02M084TB0501W02Entity createTokuisakiYoyakuEntityKey(String apkihoncd, String shopcd,
        String tokuisakicd, String unyodd) {

        S02M084TB0501W02Entity entity = new S02M084TB0501W02Entity();

        entity.setApkihoncd(apkihoncd); // ＡＰ基本コード
        entity.setShopcd(shopcd); // ショップコード
        entity.setTokuisakicd(tokuisakicd); // 得意先コード
        entity.setUnyodd(unyodd); // 運用日付

        return entity;
    }

    /**
     * 請求先予約マスタキーEntityを生成する（更新モード初期表示検索用）
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param skysakicd 請求先コード
     * @return S02M085TB0501W02Entity
     */
    private S02M085TB0501W02Entity createSkysakiYoyakuEntityKey(String apkihoncd, String shopcd,
        String skysakicd) {

        S02M085TB0501W02Entity entity = new S02M085TB0501W02Entity();

        entity.setApkihoncd(apkihoncd); // ＡＰ基本コード
        entity.setShopcd(shopcd); // ショップコード
        entity.setSkysakicd(skysakicd); // 請求先コード

        return entity;
    }

    /**
     * 定例売上仕入マスタキーEntityを生成する（サーバーレンタル料金取得用）
     * 
     * @param e 得意先マスタカスタムエンティティ
     * @param unyodd 運用日付
     * @param rtlrknshukeikbn レンタル料金集計区分
     * @return S02M016TB0501W02SummaryEntity
     */
    private S02M016TB0501W02SummaryEntity createTeireiUriageShireEntityKey(S02M013TB0501W02Entity e,
        String unyodd, String rtlrknshukeikbn) {

        S02M016TB0501W02SummaryEntity entity = new S02M016TB0501W02SummaryEntity();

        entity.setApkihoncd(e.getApkihoncd()); // ＡＰ基本コード
        entity.setShopcd(e.getShopcd()); // ショップコード
        entity.setTokuisakicd(e.getTokuisakicd()); // 得意先コード
        entity.setTeireikbn(CommonConstants.TEIREIKBN_1); // 定例区分
        entity.setUrisiirekbn(CommonConstants.URISIIREKBN_1);// 売上仕入区分
        entity.setRtlrknshukeikbn(rtlrknshukeikbn);// レンタル料金集計区分
        entity.setDnpyhasseiarinashiflg(CommonConstants.DNPYHASSEIARINASHIFLG_1);// 伝票発生有無
        entity.setYukokbn(CommonConstants.YUKOKBN_1); // 有効区分
        entity.setYm(unyodd.substring(0, 6)); // 年月

        return entity;
    }

    /**
     * 得意先ポイントマスタキーEntityを生成する（更新モード初期表示検索用）
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @param unyodd 運用日付
     * @return S02M203TB0501W02Entity
     */
    private S02M203TB0501W02Entity createTokuisakiPointEntityKey(String apkihoncd, String shopcd,
        String tokuisakicd, String unyodd) {

        S02M203TB0501W02Entity entity = new S02M203TB0501W02Entity();

        entity.setApkihoncd(apkihoncd); // ＡＰ基本コード
        entity.setShopcd(shopcd); // ショップコード
        entity.setTokuisakicd(tokuisakicd); // 得意先コード
        entity.setUnyodd(unyodd); // 運用日付

        return entity;
    }

    /**
     * エンティティをControllerへ返却する形式へ変換する。(新規登録時)
     *
     * @param e ショップエンティティ
     * @param ep ショップポイントエンティティ
     * @param unyodd 運用日付
     * @return B0501W02TokuisakiDisplayDto
     */
    private B0501W02DisplayDto createDisplayDto(S02M046TEntity e, S02M202TEntity ep,
        String unyodd) {

        B0501W02DisplayDto dto = new B0501W02DisplayDto();
        S02M200TDto rnkDto = getPointRnkGen(unyodd); // ポイントランクコード取得（初期値用）

        /* 得意先情報 */
        dto.setApkihoncd(e.getApkihoncd()); // ＡＰ基本コード
        dto.setShopcd(e.getShopcd()); // ショップコード
        dto.setShopkbn(e.getShopkbn()); // ショップ区分
        dto.setJigyoshokanri(e.getJigyoshokanri()); // 事業所管理
        dto.setMerumagakbn(CommonConstants.MERUMAGAKBN_0);// メルマガ区分
        dto.setMoshikomircptkbn(CommonConstants.MOSHIKOMIRCPTKBN_0);// 申込受付区分
        dto.setUrikeijotnshakbn(CommonConstants.URIKEIJOTNSHAKBN_0); // 売上計上担当者区分
        dto.setMgntrgtkbn(CommonConstants.TOKUISAKIMGNTRGTKBN_0); // マージン対象区分
        dto.setTokubetsuuntkbn(CommonConstants.TOKUBETSUUNTKBN_0); // 特別単価区分
        dto.setDlvrykbn(CommonConstants.KBN_DLVRY_JISHA); // 配達区分
        dto.setFzaiokikanoflg(CommonConstants.FZAIOKIKANOFLG_0); // 不在置き可能フラグ
        dto.setKinotptnashiflg(CommonConstants.KINOTPTNASHIFLG_0); // 金額出力無フラグ
        dto.setHoshokinkbn(CommonConstants.HOSHOKINKBN_0); // 保証金区分
        dto.setYoshingendokin("0"); // 与信限度金額
        dto.setYukokbn(CommonConstants.YUKOKBN_1); // 有効区分
        dto.setKyuminkanri(CommonConstants.KYUMINKANRI_1); // 休眠管理
        dto.setMtmkisvkbn(e.getMtmkisvkbn()); // まとめ買いサービス区分
        dto.setMtmkiyuryohns(
            ObjectUtils.isEmpty(e.getMtmkiyuryohns()) ? "" : String.valueOf(e.getMtmkiyuryohns())); // まとめ買い有料本数
        dto.setMtmkimuryohns(
            ObjectUtils.isEmpty(e.getMtmkimuryohns()) ? "" : String.valueOf(e.getMtmkimuryohns())); // まとめ買い無料本数
        dto.setMuryobtlsvkbn(e.getMuryobtlsvkbn()); // 無料ボトルサービス区分
        dto.setMyaqauntjohodsplyumuflg(CommonConstants.MYAQAUNTJOHODSPLYUMUFLG_1); // マイアクア単価情報表示有無フラグ
        dto.setMyaqaskyjohodsplyumuflg(CommonConstants.MYAQASKYJOHODSPLYUMUFLG_1); // マイアクア請求情報表示有無フラグ
        dto.setRegularbottlenm(getBottleName(CommonConstants.SETTEICD_114_1)); // 預かりボトル名称レギュラー
        dto.setSlimbottlenm(getBottleName(CommonConstants.SETTEICD_114_2)); // 預かりボトル名称スリム
        dto.setIyakukinkanriflg(getIyakukinkanriflg(e.getApkihoncd(), e.getShopcd())); // 違約金管理フラグ
        dto.setRknkindtokubetsuuntflg(CommonConstants.RKNKINDTOKUBETSUUNTFLG_0); // 料金種別特別単価フラグ
        dto.setRknkindtokubetsunebikiflg(CommonConstants.RKNKINDTOKUBETSUNEBIKIFLG_0); // 料金種別特別値引フラグ
        dto.setSeibetsukbn(CommonConstants.SEIBETSUKBN_2); // 性別区分
        dto.setHaiguumuflg(CommonConstants.HAIGUUMUFLG_2); // 配偶有無フラグ
        dto.setSonotadokyoninumuflg(CommonConstants.SONOTADOKYONINUMUFLG_2); // その他同居人有無フラグ
        dto.setTashakirikae(CommonConstants.TASHAKIRIKAE_0); // 他社切替
        dto.setCollabokbn(CommonConstants.COLLABOKBN_0);// コラボ区分
        dto.setKainsakuseiumukbn(getKainsakuseiumukbn()); // 会員作成有無区分
        dto.setPointkbn(ep.getPointsystemjoinkbn()); // ポイント付与管理
        dto.setMyaquakaiininptumu(CommonConstants.MYAQUAKAIININPTUMU_0); // マイアクア会員登録
        dto.setMtmkipointkbn(ep.getMtmkipointkbn()); // まとめ買いポイント区分
        dto.setMtmkisuryo(
            ObjectUtils.isEmpty(ep.getMtmkisuryo()) ? "" : String.valueOf(ep.getMtmkisuryo())); // まとめ買い数量
        dto.setMtmkipointsu(
            ObjectUtils.isEmpty(ep.getMtmkipointsu()) ? "" : String.valueOf(ep.getMtmkipointsu())); // まとめ買いポイント数
        dto.setNyukaitokutenumu(ep.getNyukaitokutenumu()); // 入会特典区分
        dto.setNyukaitokutenpointsu(ObjectUtils.isEmpty(ep.getNyukaitokutenpointsu()) ? ""
            : String.valueOf(ep.getNyukaitokutenpointsu())); // 入会特典ポイント数
        dto.setPointrnkcdgen(rnkDto.getPointrnkcd()); // 適用ステージ（現）
        dto.setPointrnkgen(rnkDto.getPointrnk() + " " + rnkDto.getPointbairitu()
            + CommonConstants.POINTBAIRITUSUFFIX); // ポイントランクコード（現）

        // アクセス可否設定
        setAccessKahi(dto);

        /* 定例売上情報 */
        dto.setShrydydm(CommonConstants.YEARMONTH_MAX); // 終了年月
        dto.setRtlrknshukeikbn(CommonConstants.RTLRKNSHUKEIKBN_0); // 売上設定
        dto.setHasseikankakukeijoptn(CommonConstants.HASSEIKANKAKUKEIJOPTN_1); // 発生間隔計上パターン
        dto.setDnpyhasseiarinashiflg(CommonConstants.DNPYHASSEIARINASHIFLG_1); // 売上発生有無フラグ
        dto.setYukokbn1(CommonConstants.YUKOKBN_1); // 有効区分
        dto.setTteireikbn(e.getTtmasutateireisetteikbn()); // 定例設定区分
        dto.setKeijokbn(CommonConstants.KBN_KEIJO_URIAGE);// 計上区分
        dto.setKeijokbnnm(getKeijokbnnm()); // 計上区分名
        dto.setPriceinfo(getPriceinfo(e)); // 価格情報

        /* 得意先予約情報 */
        dto.setDlvrykbn1(CommonConstants.KBN_DLVRY_JISHA); // 配達区分

        return dto;
    }

    /**
     * エンティティをControllerへ返却する形式へ変換する。(更新時)
     *
     * @param to 得意先カスタムエンティティ
     * @param tuList 定例売上仕入カスタムエンティティリスト
     * @param ty 得意先予約カスタムエンティティ
     * @param sy 請求先予約カスタムエンティティ
     * @param sh ショップエンティティ
     * @param unyodd 運用日付
     * @param top 得意先ポイントカスタムエンティティ
     * @return B0501W02DisplayDto
     */
    private B0501W02DisplayDto createDisplayDto(S02M013TB0501W02Entity to,
        List<S02M016TB0501W02Entity> tuList, S02M084TB0501W02Entity ty, S02M085TB0501W02Entity sy,
        S02M046TEntity sh, String unyodd, S02M203TB0501W02Entity top) {

        B0501W02DisplayDto dto = new B0501W02DisplayDto();

        /* 得意先情報 */
        dto.setApkihoncd(to.getApkihoncd()); // ＡＰ基本コード
        dto.setShopcd(to.getShopcd()); // ショップコード
        dto.setShopkbn(sh.getShopkbn()); // ショップ区分
        dto.setJigyoshokanri(sh.getJigyoshokanri()); // 事業所管理
        dto.setTokuisakicd(to.getTokuisakicd()); // 得意先コード
        dto.setTokuisakijotai(to.getTokuisakijotai()); // 得意先状態
        dto.setTokuisakijotainm(to.getTokuisakijotainm()); // 得意先状態名称
        dto.setJotaists(to.getJotaists()); // 状態ステータス
        dto.setJotaistsnm(to.getJotaistsnm()); // 状態ステータス名称
        dto.setTokuisakikbn(to.getTokuisakikbn()); // 得意先区分
        dto.setJutakudlvrycstkbn(to.getJutakudlvrycstkbn()); // 受託配達顧客区分
        dto.setCdkindkbn(to.getCdkindkbn()); // コード種別区分
        dto.setRiyowsvkbn(to.getRiyowsvkbn()); // 利用サーバー区分
        dto.setGyoshukbn(to.getGyoshukbn()); // 業種区分
        dto.setSkysakicd(to.getSkysakicd()); // 請求先コード
        dto.setTokuisakishopcd(to.getTokuisakishopcd()); // 得意先ショップコード
        dto.setTokuisakishopnm(to.getTokuisakishopnm()); // 得意先ショップ名称
        dto.setJigyoshocd(to.getJigyoshocd()); // 事業所コード
        dto.setTokuisakinmn1(to.getTokuisakinmn1()); // 得意先名称１
        dto.setTokuisakinmn2(to.getTokuisakinmn2()); // 得意先名称２
        dto.setTokuisakikeishokbn(to.getTokuisakikeishokbn()); // 得意先敬称区分
        dto.setTokuisakinmkanan1(to.getTokuisakinmkanan1()); // 得意先名称カナ１
        dto.setTokuisakinmkanan2(to.getTokuisakinmkanan2()); // 得意先名称カナ２
        dto.setYubinno(to.getYubinno()); // 郵便番号
        dto.setAdrsn1(to.getAdrsn1()); // 住所１
        dto.setAdrsn2(to.getAdrsn2()); // 住所２
        dto.setAdrsn3(to.getAdrsn3()); // 住所３
        dto.setRenrakusakinokbnn1(to.getRenrakusakinokbnn1()); // 連絡先番号区分１
        dto.setRenrakusakinon1(to.getRenrakusakinon1()); // 連絡先番号１
        dto.setRenrakusakinokbnn2(to.getRenrakusakinokbnn2()); // 連絡先番号区分２
        dto.setRenrakusakinon2(to.getRenrakusakinon2()); // 連絡先番号２
        dto.setRenrakusakinokbnn3(to.getRenrakusakinokbnn3()); // 連絡先番号区分３
        dto.setRenrakusakinon3(to.getRenrakusakinon3()); // 連絡先番号３
        dto.setRenrakusakinokbnn4(to.getRenrakusakinokbnn4()); // 連絡先番号区分４
        dto.setRenrakusakinon4(to.getRenrakusakinon4()); // 連絡先番号４
        dto.setClckensakuyotelnon1(to.getClckensakuyotelnon1()); // コールセンター検索用電話番号１
        dto.setClckensakuyotelnon2(to.getClckensakuyotelnon2()); // コールセンター検索用電話番号２
        dto.setClckensakuyotelnon3(to.getClckensakuyotelnon3()); // コールセンター検索用電話番号３
        dto.setClckensakuyotelnon4(to.getClckensakuyotelnon4()); // コールセンター検索用電話番号４
        dto.setMailadrse(to.getMailadrse()); // メールアドレス
        dto.setMerumagakbn(to.getMerumagakbn()); // メルマガ区分
        dto.setKazokukoseikazudansei(to.getKazokukoseikazudansei()); // 利用者数
        dto.setKazokukoseikazujosei(to.getKazokukoseikazujosei()); // 内子供数
        dto.setKazokukoseikazukodomo(to.getKazokukoseikazukodomo()); // 家族構成数子供
        dto.setNyushatanjodd(to.getNyushatanjodd()); // 入会者誕生日
        dto.setMoshikomircptdd(to.getMoshikomircptdd()); // 申込受付日
        dto.setNyukaimokoshono(to.getNyukaimokoshono()); // 入会申込書番号
        dto.setMoshikomicncldd(to.getMoshikomicncldd()); // 申込キャンセル日
        dto.setMoshikomitoshacd(to.getMoshikomitoshacd()); // 申込登録者コード
        dto.setMoshikomitoshamei(to.getMoshikomitoshamei()); // 申込登録者名
        dto.setNyukaidd(to.getNyukaidd()); // 入会日
        dto.setMoshikomircptkbn(to.getMoshikomircptkbn()); // 申込受付区分
        dto.setKaiinmokoshono(to.getKaiinmokoshono()); // 会員申込書番号
        dto.setNyukaiddtoshacd(to.getNyukaiddtoshacd()); // 入会日登録者コード
        dto.setNyukaiddtoshamei(to.getNyukaiddtoshamei()); // 入会日登録者名
        dto.setTaikaidd(to.getTaikaidd()); // 退会日
        dto.setTaikairiyukbn(to.getTaikairiyukbn()); // 退会理由区分
        dto.setTaikairiyushs(to.getTaikairiyushs()); // 退会理由詳細
        dto.setTaikaiddtoshacd(to.getTaikaiddtoshacd()); // 退会日登録者コード
        dto.setTaikaiddtoshamei(to.getTaikaiddtoshamei()); // 退会日登録者名
        dto.setEigyoareacd(to.getEigyoareacd()); // 営業エリアコード
        dto.setEigyoareanm(to.getEigyoareanm()); // 営業エリア名称
        dto.setTokubetsuuntkbn(to.getTokubetsuuntkbn()); // 特別単価区分
        dto.setEigyotnshacd(to.getEigyotnshacd()); // 営業担当者コード
        dto.setEigyotnshamei(to.getEigyotnshamei()); // 営業担当者名
        dto.setUrikeijotnshakbn(to.getUrikeijotnshakbn()); // 売上計上担当者区分
        dto.setKakutokukbn(to.getKakutokukbn()); // 獲得区分
        dto.setRcptkbn(to.getRcptkbn()); // 受付区分
        dto.setBaitaikbn(to.getBaitaikbn()); // 媒体区分
        dto.setKakutokutnshacd(to.getKakutokutnshacd()); // 獲得担当者コード
        dto.setKakutokutnshamei(to.getKakutokutnshamei()); // 獲得担当者名
        dto.setHonbuejntcd(to.getHonbuejntcd()); // 本部エージェントコード
        dto.setHonbuejntmei(to.getHonbuejntmei()); // 本部エージェント名
        dto.setKakutokuejntcd(to.getKakutokuejntcd()); // 獲得エージェントコード
        dto.setKakutokuejntmei(to.getKakutokuejntmei()); // 獲得エージェント名
        dto.setShanaitnshacd(to.getShanaitnshacd()); // 社内担当者コード
        dto.setShanaitnshamei(to.getShanaitnshamei()); // 社内担当者名
        dto.setShoukaitokuisakicd(to.getShoukaitokuisakicd()); // 紹介得意先コード
        dto.setShoukaitokuisakinm(to.getShoukaitokuisakinm()); // 紹介得意先名
        dto.setHansokushscd(to.getHansokushscd()); // 販促詳細コード
        dto.setHansokushsnm(to.getHansokushsnm()); // 販促詳細名称
        dto.setKakutokushsmemo(to.getKakutokushsmemo()); // 獲得詳細メモ
        dto.setMgntrgtkbn(to.getMgntrgtkbn()); // マージン対象区分
        dto.setWanshottocd(to.getWanshottocd()); // ワンショットコード
        dto.setWanshottonm(to.getWanshottonm()); // ワンショット名称
        dto.setBonasumgncnt(to.getBonasumgncnt()); // ボーナスマージン件数
        dto.setBonasumgnkin(to.getBonasumgnkin()); // ボーナスマージン金額
        dto.setBtlmgntrgtdkikan(to.getBtlmgntrgtdkikan()); // ボトルマージン対象期間
        dto.setMgnyoshncdn1(to.getMgnyoshncdn1()); // マージン用商品コード１
        dto.setMgnyoshnnm1(to.getMgnyoshnnm1()); // マージン用商品名称１
        dto.setMgnyoshncdn2(to.getMgnyoshncdn2()); // マージン用商品コード２
        dto.setMgnyoshnnm2(to.getMgnyoshnnm2()); // マージン用商品名称２
        dto.setMgnyoshncdn3(to.getMgnyoshncdn3()); // マージン用商品コード３
        dto.setMgnyoshnnm3(to.getMgnyoshnnm3()); // マージン用商品名称３
        dto.setMgnyoshncdn4(to.getMgnyoshncdn4()); // マージン用商品コード４
        dto.setMgnyoshnnm4(to.getMgnyoshnnm4()); // マージン用商品名称４
        dto.setMgncdn1(to.getMgncdn1()); // マージンコード１
        dto.setMgnnm1(to.getMgnnm1()); // マージン名称１
        dto.setMgncdn2(to.getMgncdn2()); // マージンコード２
        dto.setMgnnm2(to.getMgnnm2()); // マージン名称２
        dto.setMgncdn3(to.getMgncdn3()); // マージンコード３
        dto.setMgnnm3(to.getMgnnm3()); // マージン名称３
        dto.setMgncdn4(to.getMgncdn4()); // マージンコード４
        dto.setMgnnm4(to.getMgnnm4()); // マージン名称４
        dto.setJidonebikicdn1(to.getJidonebikicdn1()); // 自動値引コード１
        dto.setJidonebikinm1(to.getJidonebikinm1()); // 自動値引名称１
        dto.setGekkanhanbaihns1(to.getGekkanhanbaihns1()); // 月間販売本数１
        dto.setJidonebikikind1(to.getJidonebikikind1()); // 自動値引種別１
        dto.setJidonebikikindnm1(to.getJidonebikikindnm1()); // 自動値引種別名称１
        dto.setNebikibtlhns1(to.getNebikibtlhns1()); // 値引ボトル本数１
        dto.setNebikikin1(to.getNebikikin1()); // 値引金額１
        dto.setJidonebishncd1(to.getJidonebishncd1()); // 自動値引商品コード１
        dto.setJidonebishnnm1(to.getJidonebishnnm1()); // 自動値引商品名称１
        dto.setJidonebikicdn2(to.getJidonebikicdn2()); // 自動値引コード２
        dto.setJidonebikinm2(to.getJidonebikinm2()); // 自動値引名称２
        dto.setGekkanhanbaihns2(to.getGekkanhanbaihns2()); // 月間販売本数２
        dto.setJidonebikikind2(to.getJidonebikikind2()); // 自動値引種別２
        dto.setJidonebikikindnm2(to.getJidonebikikindnm2()); // 自動値引種別名称２
        dto.setNebikibtlhns2(to.getNebikibtlhns2()); // 値引ボトル本数２
        dto.setNebikikin2(to.getNebikikin2()); // 値引金額２
        dto.setJidonebishncd2(to.getJidonebishncd2()); // 自動値引商品コード２
        dto.setJidonebishnnm2(to.getJidonebishnnm2()); // 自動値引商品名称２
        dto.setJidonebikicdn3(to.getJidonebikicdn3()); // 自動値引コード３
        dto.setJidonebikinm3(to.getJidonebikinm3()); // 自動値引名称３
        dto.setGekkanhanbaihns3(to.getGekkanhanbaihns3()); // 月間販売本数３
        dto.setJidonebikikind3(to.getJidonebikikind3()); // 自動値引種別３
        dto.setJidonebikikindnm3(to.getJidonebikikindnm3()); // 自動値引種別名称３
        dto.setNebikibtlhns3(to.getNebikibtlhns3()); // 値引ボトル本数３
        dto.setNebikikin3(to.getNebikikin3()); // 値引金額３
        dto.setJidonebishncd3(to.getJidonebishncd3()); // 自動値引商品コード３
        dto.setJidonebishnnm3(to.getJidonebishnnm3()); // 自動値引商品名称３
        dto.setPointkbn(top.getPointkanriumu()); // ポイント管理有無
        dto.setMyaquakaiininptumu(top.getMyaquakaiininptumu()); // マイアクア会員登録
        dto.setMtmkipointkbn(top.getMtmkipointkbn()); // まとめ買いポイント区分
        dto.setMtmkisuryo(top.getMtmkisuryo()); // まとめ買い数量
        dto.setMtmkipointsu(top.getMtmkipointsu()); // まとめ買いポイント数
        dto.setNyukaitokutenumu(top.getNyukaitokutenumu()); // 入会特典区分
        dto.setNyukaitokutenpointsu(top.getNyukaitokutenpointsu()); // 入会特典ポイント数
        // ポイントランク強制変更実施済のデータは画面に表示しない
        if (StringUtils.isEmpty(top.getHenkojissidd())) {
            dto.setPointrnkshin(top.getHenkoatopointrnkcd()); // ステージ修正（新）
            dto.setTekiyoudd(top.getHenkokibodd()); // ステージ切替日
        }
        dto.setPointrnkgen(
            top.getPointrnk() + " " + top.getPointbairitu() + CommonConstants.POINTBAIRITUSUFFIX); // 適用ステージ（現）
        dto.setPointrnkcdgen(top.getPointrnkcd()); // ポイントランクコード（現）
        dto.setRtlskykanrikbn(to.getRtlskykanrikbn()); // レンタル請求管理区分
        dto.setSaburtlsokocd(to.getSaburtlsokocd()); // サブレンタル倉庫コード
        dto.setSaburtlsokonm(to.getSaburtlsokonm()); // サブレンタル倉庫名称
        dto.setShokaiwsvstupdd(to.getShokaiwsvstupdd()); // 初回サーバー設置日
        dto.setStupddtoshaapkihoncd(to.getStupddtoshaapkihoncd()); // 設置日登録者ＡＰ基本コード
        dto.setStupddtoshashopcd(to.getStupddtoshashopcd()); // 設置日登録者ショップコード
        dto.setStupddtoshacd(to.getStupddtoshacd()); // 設置日登録者コード
        dto.setStupddtoshamei(to.getStupddtoshamei()); // 設置日登録者名
        dto.setWsvhikiagedd(to.getWsvhikiagedd()); // サーバー引上日
        dto.setHikiageddtoshaapkihoncd(to.getHikiageddtoshaapkihoncd()); // 引上日登録者ＡＰ基本コード
        dto.setHikiageddtoshashopcd(to.getHikiageddtoshashopcd()); // 引上日登録者ショップコード
        dto.setHikiageddtoshacd(to.getHikiageddtoshacd()); // 引上日登録者コード
        dto.setHikiageddtoshamei(to.getHikiageddtoshamei()); // 引上日登録者名
        dto.setDlvrykankatsuapkihoncd(to.getDlvrykankatsuapkihoncd()); // 配達管轄ＡＰ基本コード
        dto.setDlvrykankatsuapkihonnm(to.getDlvrykankatsuapkihonnm()); // 配達管轄ＡＰ基本名称
        dto.setDlvrykankatsushopcd(to.getDlvrykankatsushopcd()); // 配達管轄ショップコード
        dto.setDlvrykankatsushopnm(to.getDlvrykankatsushopnm()); // 配達管轄ショップ名称
        dto.setDlvrykankatsukyotencd(to.getDlvrykankatsukyotencd()); // 配達管轄拠点コード
        dto.setDlvrykankatsukyotennm(to.getDlvrykankatsukyotennm()); // 配達管轄拠点名称
        dto.setDlvrycmpnycd(to.getDlvrycmpnycd()); // 配達会社コード
        dto.setDlvrycmpnynm(to.getDlvrycmpnynm()); // 配達会社名称
        dto.setDlvrytnshacd(to.getDlvrytnshacd()); // 配達担当者コード
        dto.setDlvrytnshamei(to.getDlvrytnshamei()); // 配達担当者名
        dto.setDlvryjun(to.getDlvryjun()); // 配達順
        dto.setDlvryareacd(to.getDlvryareacd()); // 配達エリアコード
        dto.setDlvryareanm(to.getDlvryareanm()); // 配達エリア名称
        dto.setDlvrykbn(to.getDlvrykbn()); // 配達区分
        dto.setFzaiokikanoflg(to.getFzaiokikanoflg()); // 不在置き可能フラグ
        dto.setNohinsakijohon1(to.getNohinsakijohon1()); // 納品先情報１
        dto.setNohinsakijohon2(to.getNohinsakijohon2()); // 納品先情報２
        dto.setChizuno(to.getChizuno()); // 地図番号
        dto.setGaiburenkeidlvrycd(to.getGaiburenkeidlvrycd()); // 外部連携配達コード
        dto.setAcptrcptkbn(to.getAcptrcptkbn()); // 受注受付区分
        dto.setAcptrcptkbnnm(to.getAcptrcptkbnnm()); // 受注受付区分名称
        dto.setNestlehachusakikbn(to.getNestlehachusakikbn()); // ネスレ発注先区分
        dto.setAcptteishiflg(to.getAcptteishiflg()); // 受注停止フラグ
        dto.setAcptjikanshiteijikoku(to.getAcptjikanshiteijikoku()); // 受注時間指定時刻
        dto.setAcptjikanshiteijikokushry(to.getAcptjikanshiteijikokushry()); // 受注時間指定時刻終了
        dto.setAcptjikanshiteikbn(to.getAcptjikanshiteikbn()); // 受注時間指定区分
        dto.setChuitokuisakikbnaci(to.getChuitokuisakikbnaci()); // 注意得意先区分ＡＣＩ
        dto.setChuinaiyoaci(to.getChuinaiyoaci()); // 注意内容ＡＣＩ
        dto.setChuitokuisakikbnap(to.getChuitokuisakikbnap()); // 注意得意先区分ＡＰ
        dto.setChuinaiyoap(to.getChuinaiyoap()); // 注意内容ＡＰ
        dto.setRenrakujikokokai(to.getRenrakujikokokai()); // 連絡事項公開
        dto.setTokkijikohikokai(to.getTokkijikohikokai()); // 特記事項非公開
        dto.setPrntmemon1(to.getPrntmemon1()); // 印刷メモ１
        dto.setPrntmemon2(to.getPrntmemon2()); // 印刷メモ２
        dto.setPrntmemon3(to.getPrntmemon3()); // 印刷メモ３
        dto.setKinotptnashiflg(
            StringUtils.isEmpty(to.getKinotptnashiflg()) ? CommonConstants.KINOTPTNASHIFLG_0
                : to.getKinotptnashiflg()); // 金額出力無フラグ
        dto.setHoshokinkbn(to.getHoshokinkbn()); // 保証金区分
        dto.setHoshokinazukaridd(to.getHoshokinazukaridd()); // 保証金預かり日
        dto.setHoshokinazukarikin(to.getHoshokinazukarikin()); // 保証金預かり金額
        dto.setAzushono(to.getAzushono()); // 預かり書番号
        dto.setHoshokinhenkyakudd(to.getHoshokinhenkyakudd()); // 保証金返却日
        dto.setHoshokinhenkyakukin(to.getHoshokinhenkyakukin()); // 保証金返却金額
        dto.setKeizokurnk(to.getKeizokurnk()); // 継続ランク
        dto.setShohirnk(to.getShohirnk()); // 消費ランク
        dto.setApbetsutokuisakirnk(to.getApbetsutokuisakirnk()); // ＡＰ別得意先ランク
        dto.setKyuminkbn(to.getKyuminkbn()); // 休眠区分
        dto.setKyuminkbnnm(to.getKyuminkbnnm()); // 休眠区分名称
        dto.setYoshingendokin(to.getYoshingendokin()); // 与信限度金額
        dto.setTokuisakigaibuskbtscd(to.getTokuisakigaibuskbtscd()); // 得意先外部識別コード
        dto.setTokuisakigaibuskbtscd2(to.getTokuisakigaibuskbtscd2()); // 得意先外部識別コード2
        dto.setTokuisakigaibuskbtscd3(to.getTokuisakigaibuskbtscd3()); // 得意先外部識別コード3
        dto.setTokuisakishukeicd(to.getTokuisakishukeicd()); // 得意先集計コード
        dto.setTokuisakishoshakazu(
            getShoshakazu(to.getApkihoncd(), to.getShopcd(), to.getTokuisakicd())); // 得意先紹介者数
        dto.setYukokbn(to.getYukokbn()); // 有効区分
        dto.setToshaapkihoncd(to.getToshaapkihoncd()); // 登録者ＡＰ基本コード
        dto.setToshashopcd(to.getToshashopcd()); // 登録者ショップコード
        dto.setToshacd(to.getToshacd()); // 登録者コード
        dto.setToshamei(to.getToshamei()); // 登録者名
        dto.setInptsystmymd(to.getInptsystmymd()); // 登録システム日付
        dto.setInptsystmjikan(to.getInptsystmjikan()); // 登録システム時間
        dto.setInptunyoymd(to.getInptunyoymd()); // 登録運用日付
        dto.setInptipadrse(to.getInptipadrse()); // 登録ＩＰアドレス
        dto.setInptpgmid(to.getInptpgmid()); // 登録プログラムＩＤ
        dto.setKoshacd(to.getKoshacd()); // 更新者コード
        dto.setKoshamei(to.getKoshamei()); // 更新者名
        dto.setKoshaapkihoncd(to.getKoshaapkihoncd()); // 更新者ＡＰ基本コード
        dto.setKoshashopcd(to.getKoshashopcd()); // 更新者ショップコード
        dto.setKshnsystmymd(to.getKshnsystmymd()); // 更新システム日付
        dto.setKshnsystmjikan(to.getKshnsystmjikan()); // 更新システム時間
        dto.setKshnunyoymd(to.getKshnunyoymd()); // 更新運用日付
        dto.setKshnipadrse(to.getKshnipadrse()); // 更新ＩＰアドレス
        dto.setKshnpgmid(to.getKshnpgmid()); // 更新プログラムＩＤ
        dto.setHonbuejntapkihoncd(to.getHonbuejntapkihoncd()); // 本部エージェントＡＰ基本コード
        dto.setHonbuejntshopcd(to.getHonbuejntshopcd()); // 本部エージェントショップコード
        dto.setMoshikomitoshaapkihoncd(to.getMoshikomitoshaapkihoncd()); // 申込登録者ＡＰ基本コード
        dto.setMoshikomitoshashopcd(to.getMoshikomitoshashopcd()); // 申込登録者ショップコード
        dto.setNyukaiddtoshaapkihoncd(to.getNyukaiddtoshaapkihoncd()); // 入会日登録者ＡＰ基本コード
        dto.setNyukaiddtoshashopcd(to.getNyukaiddtoshashopcd()); // 入会日登録者ショップコード
        dto.setTaikaiddtoshaapkihoncd(to.getTaikaiddtoshaapkihoncd()); // 退会日登録者ＡＰ基本コード
        dto.setTaikaiddtoshashopcd(to.getTaikaiddtoshashopcd()); // 退会日登録者ショップコード
        dto.setKyuminkanri(to.getKyuminkanri()); // 休眠管理
        dto.setKinmusakin1(to.getKinmusakin1()); // 勤務先１
        dto.setKinmusakin2(to.getKinmusakin2()); // 勤務先２
        dto.setKinmusakiyubinno(to.getKinmusakiyubinno()); // 勤務先郵便番号
        dto.setKinmusakiadrsn1(to.getKinmusakiadrsn1()); // 勤務先住所１
        dto.setKinmusakiadrsn2(to.getKinmusakiadrsn2()); // 勤務先住所２
        dto.setKinmusakiadrsn3(to.getKinmusakiadrsn3()); // 勤務先住所３
        dto.setKinmusakitelno(to.getKinmusakitelno()); // 勤務先電話番号
        dto.setKinmusakifaxno(to.getKinmusakifaxno()); // 勤務先ＦＡＸ番号
        dto.setRenrakusakimemon1(to.getRenrakusakimemon1()); // 連絡先メモ１
        dto.setRenrakusakimemon2(to.getRenrakusakimemon2()); // 連絡先メモ２
        dto.setRenrakusakimemon3(to.getRenrakusakimemon3()); // 連絡先メモ３
        dto.setRenrakusakimemon4(to.getRenrakusakimemon4()); // 連絡先メモ４
        dto.setStartup(to.getStartup()); // スタートアップ
        dto.setKodomomein1(to.getKodomomein1()); // 子供名１
        dto.setKodomotanjoddn1(to.getKodomotanjoddn1()); // 子供誕生日１
        dto.setKodomomein2(to.getKodomomein2()); // 子供名２
        dto.setKodomotanjoddn2(to.getKodomotanjoddn2()); // 子供誕生日２
        dto.setKodomomein3(to.getKodomomein3()); // 子供名３
        dto.setKodomotanjoddn3(to.getKodomotanjoddn3()); // 子供誕生日３
        dto.setNebikistrtdydmn1(to.getNebikistrtdydmn1()); // 値引開始年月１
        dto.setNebikishrydydmn1(to.getNebikishrydydmn1()); // 値引終了年月１
        dto.setNebikistrtdydmn2(to.getNebikistrtdydmn2()); // 値引開始年月２
        dto.setNebikishrydydmn2(to.getNebikishrydydmn2()); // 値引終了年月２
        dto.setNebikistrtdydmn3(to.getNebikistrtdydmn3()); // 値引開始年月３
        dto.setNebikishrydydmn3(to.getNebikishrydydmn3()); // 値引終了年月３
        dto.setMtmkisvkbn(to.getMtmkisvkbn()); // まとめ買いサービス区分
        dto.setMtmkiyuryohns(to.getMtmkiyuryohns()); // まとめ買い有料本数
        dto.setMtmkimuryohns(to.getMtmkimuryohns()); // まとめ買い無料本数
        dto.setMuryobtlsvkbn(to.getMuryobtlsvkbn()); // 無料ボトルサービス区分
        dto.setMyaqauntjohodsplyumuflg(to.getMyaqauntjohodsplyumuflg()); // マイアクア単価情報表示有無フラグ
        dto.setMyaqaskyjohodsplyumuflg(to.getMyaqaskyjohodsplyumuflg()); // マイアクア請求情報表示有無フラグ
        dto.setRknplnsetteidd(to.getRknplnsetteidd()); // 料金プラン設定日
        dto.setAcptdd(to.getAcptdd()); // 受注日
        dto.setYusyohns(to.getYusyohns()); // 有償本数
        dto.setMushohns(to.getMushohns()); // 無償本数
        dto.setAzukaribtln1n2rittoruhns(to.getAzukaribtln1n2rittoruhns()); // 預かりボトル１２Ｌ本数
        dto.setAzukaribtln8rittoruhns(to.getAzukaribtln8rittoruhns()); // 預かりボトル８Ｌ本数
        dto.setRegularbottlenm(getBottleName(CommonConstants.SETTEICD_114_1)); // 預かりボトル名称レギュラー
        dto.setSlimbottlenm(getBottleName(CommonConstants.SETTEICD_114_2)); // 預かりボトル名称スリム;
        dto.setSaishuteikiacptdlvryytidd(to.getSaishuteikiacptdlvryytidd()); // 最終定期受注配達予定日
        dto.setGenzaizd(to.getGenzaizd()); // 現在残高
        dto.setInvoicshikoymd(to.getInvoicshikoymd()); // インボイス施行日
        dto.setKeijomaeacptkin(to.getKeijomaeacptkin()); // 計上前受注金額
        dto.setZnkdlvryacptdd(to.getZnkdlvryacptdd()); // 前回配達受注日
        dto.setZnkdlvrydd(to.getZnkdlvrydd()); // 前回配達日
        dto.setZnkdlvryrgrayusyohns(to.getZnkdlvryrgrayusyohns()); // 前回配達レギュラー有償本数
        dto.setZnkdlvryrgramushohns(to.getZnkdlvryrgramushohns()); // 前回配達レギュラー無償本数
        dto.setZnkdlvrysrmyusyohns(to.getZnkdlvrysrmyusyohns()); // 前回配達スリム有償本数
        dto.setZnkdlvrysrmmushohns(to.getZnkdlvrysrmmushohns()); // 前回配達スリム無償本数
        dto.setZnkdlvrysonotayusyohns(to.getZnkdlvrysonotayusyohns()); // 前回配達その他有償本数
        dto.setZnkdlvrysonotamushohns(to.getZnkdlvrysonotamushohns()); // 前回配達その他無償本数
        dto.setZnkdlvryseihinmizuurikin(to.getZnkdlvryseihinmizuurikin()); // 前回配達製品水売上金額
        dto.setZnkdlvrysonotaurikin(to.getZnkdlvrysonotaurikin()); // 前回配達その他売上金額
        dto.setZzkaidlvryacptdd(to.getZzkaidlvryacptdd()); // 前々回配達受注日
        dto.setZzkhtdd(to.getZzkhtdd()); // 前々回配達日
        dto.setZzkhtrgrayusyohns(to.getZzkhtrgrayusyohns()); // 前々回配達レギュラー有償本数
        dto.setZzkhtrgramushohns(to.getZzkhtrgramushohns()); // 前々回配達レギュラー無償本数
        dto.setZzkhtsrmyusyohns(to.getZzkhtsrmyusyohns()); // 前々回配達スリム有償本数
        dto.setZzkhtsrmmushohns(to.getZzkhtsrmmushohns()); // 前々回配達スリム無償本数
        dto.setZzkhtsonotayusyohns(to.getZzkhtsonotayusyohns()); // 前々回配達その他有償本数
        dto.setZzkhtsonotamushohns(to.getZzkhtsonotamushohns()); // 前々回配達その他無償本数
        dto.setZzkhtseihinmizuurikin(to.getZzkhtseihinmizuurikin()); // 前々回配達製品水売上金額
        dto.setZzkhtsonotaurikin(to.getZzkhtsonotaurikin()); // 前々回配達その他売上金額
        dto.setZzzkhtacptdd(to.getZzzkhtacptdd()); // 前々前回配達受注日
        dto.setZzzkhtdd(to.getZzzkhtdd()); // 前々前回配達日
        dto.setZzzkhtrgrayusyohns(to.getZzzkhtrgrayusyohns()); // 前々前回配達レギュラー有償本数
        dto.setZzzkhtrgramushohns(to.getZzzkhtrgramushohns()); // 前々前回配達レギュラー無償本数
        dto.setZzzkhtsrmyusyohns(to.getZzzkhtsrmyusyohns()); // 前々前回配達スリム有償本数
        dto.setZzzkhtsrmmushohns(to.getZzzkhtsrmmushohns()); // 前々前回配達スリム無償本数
        dto.setZzzkhtsonotayusyohns(to.getZzzkhtsonotayusyohns()); // 前々前回配達その他有償本数
        dto.setZzzkhtsonotamushohns(to.getZzzkhtsonotamushohns()); // 前々前回配達その他無償本数
        dto.setZzzkhtseihinmizuurikin(to.getZzzkhtseihinmizuurikin()); // 前々前回配達製品水売上金額
        dto.setZzzkhtsonotaurikin(to.getZzzkhtsonotaurikin()); // 前々前回配達その他売上金額
        dto.setRknplnkeizokukshndd(to.getRknplnkeizokukshndd()); // 料金プラン継続更新日
        dto.setIyakukinkanriflg(to.getIyakukinkanriflg()); // 違約金管理フラグ
        dto.setHonshokaino(to.getHonshokaino()); // 紹介番号
        dto.setToritugitenkaiinid(to.getToritugitenkaiinid()); // 取次店会員ID
        dto.setKakutokuhonbukynpncd(to.getKakutokuhonbukynpncd()); // 獲得本部キャンペーンコード
        dto.setKakutokuhonbukynpnnm(to.getKakutokuhonbukynpnnm()); // 獲得本部キャンペーン名称
        dto.setIyakukinkin(to.getIyakukinkin()); // 違約金金額
        dto.setHaneimotottcd(to.getHaneimotottcd()); // 反映元得意先コード
        dto.setHaneimototokuisakinm(to.getHaneimototokuisakinm()); // 反映元得意先名称１
        dto.setFttbnyukaiflg(to.getFttbnyukaiflg()); // 再入会フラグ
        dto.setGyoshushskbn(to.getGyoshushskbn()); // 業種詳細区分
        dto.setItakuaiteapkihoncd(to.getItakuaiteapkihoncd()); // 委託相手ＡＰ基本コード
        dto.setItakuaiteshopcd(to.getItakuaiteshopcd()); // 委託相手ショップコード
        dto.setItakuaitetokuisakicd(to.getItakuaitetokuisakicd()); // 委託相手得意先コード
        dto.setItakuaitetokuisakinm(to.getItakuaitetokuisakinm()); // 委託相手得意先名称１
        dto.setKaiincd(to.getKaiincd()); // 会員コード
        dto.setChihoupubdanticdn1(to.getChihoupubdanticdn1()); // 地方公共団体コード１
        dto.setChihoupubdanticdn2(to.getChihoupubdanticdn2()); // 地方公共団体コード２
        dto.setKinmusakichihoupubdanticdn1(to.getKinmusakichihoupubdanticdn1()); // 勤務先地方公共団体コード１
        dto.setKinmusakichihoupubdanticdn2(to.getKinmusakichihoupubdanticdn2()); // 勤務先地方公共団体コード２
        dto.setRknkindkihonflg(to.getRknkindkihonflg()); // 料金種別基本フラグ
        dto.setRknkindtokubetsuuntflg(to.getRknkindtokubetsuuntflg()); // 料金種別特別単価フラグ
        dto.setRknkindtokubetsunebikiflg(to.getRknkindtokubetsunebikiflg()); // 料金種別特別値引フラグ
        dto.setPlncd(to.getPlncd()); // プランコード
        dto.setPlncd2(to.getPlncd2()); // プランコード２
        dto.setRknplnsetteidd2(to.getRknplnsetteidd2()); // 料金プラン設定日２
        dto.setRknplnkeizokukshndd2(to.getRknplnkeizokukshndd2()); // 料金プラン継続更新日２
        dto.setPlncd3(to.getPlncd3()); // プランコード３
        dto.setRknplnsetteidd3(to.getRknplnsetteidd3()); // 料金プラン設定日３
        dto.setRknplnkeizokukshndd3(to.getRknplnkeizokukshndd3()); // 料金プラン継続更新日３
        dto.setJishasvcd(to.getJishasvcd()); // 自社サービス
        dto.setHonbukynpnkeihinkbn(to.getHonbukynpnkeihinkbn()); // 本部キャンペーン景品区分
        dto.setKakutokukynpncd(to.getKakutokukynpncd()); // 獲得キャンペーンコード
        dto.setKakutokukynpnnm(to.getKakutokukynpnnm()); // キャンペーン名称
        dto.setKynpnkeihinkbn(to.getKynpnkeihinkbn()); // キャンペーン景品区分
        dto.setShokaitokutenmemo(to.getShokaitokutenmemo()); // 初回特典メモ
        dto.setNyukaiinptdd(to.getNyukaiinptdd()); // 入会登録日
        dto.setRiyomokutekikbn(to.getRiyomokutekikbn()); // 利用目的区分
        dto.setNyushadydm(to.getNyushadydm()); // 入社年月
        dto.setSeibetsukbn(StringUtils.isEmpty(to.getSeibetsukbn()) ? CommonConstants.SEIBETSUKBN_2
            : to.getSeibetsukbn()); // 性別区分
        dto.setSykugyokbn(to.getSykugyokbn()); // 職業区分
        dto.setHaiguumuflg(StringUtils.isEmpty(to.getHaiguumuflg()) ? CommonConstants.HAIGUUMUFLG_2
            : to.getHaiguumuflg()); // 配偶有無フラグ
        dto.setSonotadokyoninumuflg(StringUtils.isEmpty(to.getSonotadokyoninumuflg())
            ? CommonConstants.SONOTADOKYONINUMUFLG_2
            : to.getSonotadokyoninumuflg()); // その他同居人有無フラグ
        dto.setShihasyazokukbn(to.getShihasyazokukbn()); // 支払者続柄区分
        dto.setKojinsyoumeikindkbn(to.getKojinsyoumeikindkbn()); // 個人証明種別区分
        dto.setKojinsyoumeino(to.getKojinsyoumeino()); // 個人証明番号
        dto.setSetaijohoinptdd(to.getSetaijohoinptdd()); // 世帯情報登録日
        dto.setSetainushi(to.getSetainushi()); // 世帯主
        dto.setJugyoinkibkbn(to.getJugyoinkibkbn()); // 従業員規模区分
        dto.setKojudykazukbn(to.getKojudykazukbn()); // 居住年数区分
        dto.setPetjohokbn(to.getPetjohokbn()); // ペット情報区分
        dto.setSetaisyunyu(to.getSetaisyunyu()); // 世帯収入
        dto.setDoitsusekeninzukbn(to.getDoitsusekeninzukbn()); // 同一生計人数区分
        dto.setGaikokujinninzu(to.getGaikokujinninzu()); // 外国人人数
        dto.setGaikokujinbiko(to.getGaikokujinbiko()); // 外国人備考
        dto.setPlnkgndydm(to.getPlnkgndydm()); // プラン期限年月
        dto.setTashakirikae(
            StringUtils.isEmpty(to.getTashakirikae()) ? CommonConstants.TASHAKIRIKAE_0
                : to.getTashakirikae()); // 他社切替
        dto.setCollabokbn(to.getCollabokbn()); // コラボ区分
        dto.setSkysakinmn1(to.getSkysakinmn1()); // 請求先名称１
        dto.setOyatokuisakicd(to.getOyatokuisakicd()); // 親得意先コード
        dto.setKainsakuseiumukbn(getKainsakuseiumukbn()); // 会員作成有無区分
        dto.setSvrtlrkn(getSvrtlrkn(to, unyodd)); // サーバーレンタル料金
        // アクセス可否設定
        setAccessKahi(dto);

        S02M016TB0501W02Entity tu =
            ObjectUtils.isEmpty(tuList) ? new S02M016TB0501W02Entity() : tuList.get(0);
        /* 定例売上情報 */
        dto.setUrisiirekbn(tu.getUrisiirekbn()); // 売上仕入区分
        dto.setTeireihasseino(tu.getTeireihasseino()); // 定例発生番号
        dto.setTeireicount(tuList.size()); // 定例売上仕入登録件数
        dto.setKeijodd(tu.getKeijodd()); // 計上日
        dto.setShokaikeijodydm(tu.getShokaikeijodydm()); // 初回計上年月
        dto.setJikaikeijodydm(tu.getJikaikeijodydm()); // 次回計上年月
        dto.setShrydydm(StringUtils.isEmpty(tu.getShrydydm()) ? CommonConstants.YEARMONTH_MAX
            : tu.getShrydydm()); // 終了年月
        dto.setZnkhasseikeijodd(tu.getZnkhasseikeijodd()); // 前回発生計上日
        dto.setJissekitnshacd(tu.getJissekitnshacd()); // 実績担当者コード
        dto.setJissekitnshamei(tu.getJissekitnshamei()); // 実績担当者名
        dto.setSuryo(tu.getSuryo()); // 数量
        dto.setHyojunprice(ItemUtil.getItem(tu.getGenzhyojunprice(), tu.getShinhyojunprice(),
            tu.getHyojunpricekirikaedd(), unyodd)); // 標準価格
        dto.setUnt(tu.getHyojunprice()); // 単価
        dto.setKin(tu.getKin()); // 合計金額
        dto.setShohitaxkin(tu.getShohitaxkin()); // 消費税
        dto.setShokaiunt(tu.getShokaiunt()); // 初回単価
        dto.setShokaikin(tu.getShokaikin()); // 初回金額
        dto.setShokaishohitaxkin(tu.getShokaishohitaxkin()); // 初回消費税額
        dto.setMeisaibiko(tu.getMeisaibiko()); // 明細備考
        dto.setRtlrknshukeikbn(
            StringUtils.isEmpty(tu.getRtlrknshukeikbn()) ? CommonConstants.RTLRKNSHUKEIKBN_0
                : tu.getRtlrknshukeikbn()); // 売上設定
        dto.setHasseikankakukeijoptn(StringUtils.isEmpty(tu.getHasseikankakukeijoptn())
            ? CommonConstants.HASSEIKANKAKUKEIJOPTN_1
            : tu.getHasseikankakukeijoptn()); // 発生間隔計上パターン
        dto.setDnpyhasseiarinashiflg(StringUtils.isEmpty(tu.getDnpyhasseiarinashiflg())
            ? CommonConstants.DNPYHASSEIARINASHIFLG_1
            : tu.getDnpyhasseiarinashiflg()); // 売上発生有無フラグ
        dto.setYukokbn1(
            StringUtils.isEmpty(tu.getYukokbn()) ? CommonConstants.YUKOKBN_1 : tu.getYukokbn()); // 有効区分
        dto.setInptsystmymd1(tu.getInptsystmymd()); // 登録システム日付
        dto.setInptsystmjikan1(tu.getInptsystmjikan()); // 登録システム時間
        dto.setToshaapkihoncd1(tu.getToshaapkihoncd()); // 登録者ＡＰ基本コード
        dto.setToshashopcd1(tu.getToshashopcd()); // 登録者ショップコード
        dto.setToshacd1(tu.getToshacd()); // 登録者コード
        dto.setToshamei1(tu.getToshamei()); // 登録者名
        dto.setKshnsystmymd1(tu.getKshnsystmymd()); // 更新システム日付
        dto.setKshnsystmjikan1(tu.getKshnsystmjikan()); // 更新システム時間
        dto.setKoshaapkihoncd1(tu.getKoshaapkihoncd()); // 更新者ＡＰ基本コード
        dto.setKoshashopcd1(tu.getKoshashopcd()); // 更新者ショップコード
        dto.setKoshacd1(tu.getKoshacd()); // 更新者コード
        dto.setKoshamei1(tu.getKoshamei()); // 更新者名
        dto.setGenzhyojunprice(tu.getGenzhyojunprice()); // 現標準価格
        dto.setShinhyojunprice(tu.getShinhyojunprice()); // 新標準価格
        dto.setHyojunpricekirikaedd(tu.getHyojunpricekirikaedd()); // 標準価格切替日
        dto.setShncd(tu.getShncd()); // 商品コード
        dto.setTteireikbn(""); // 定例設定区分
        dto.setTaxsetteikbn(tu.getTaxsetteikbn()); // 税設定区分
        dto.setTaxkbn(tu.getTaxkbn()); // 税区分
        dto.setKeijokbn(StringUtils.isEmpty(tu.getKeijokbn()) ? CommonConstants.KBN_KEIJO_URIAGE
            : tu.getKeijokbn()); // 計上区分
        dto.setKeijokbnnm(
            StringUtils.isEmpty(tu.getKeijokbn()) ? getKeijokbnnm() : tu.getKeijokbnnm()); // 計上区分名
        dto.setPriceinfo(getPriceinfo(sh)); // 価格情報

        /* 得意先予約情報 */
        dto.setYoyakuinptdd(ty.getYoyakuinptdd()); // 予約登録日
        dto.setTtmasutachgdd(ty.getTtmasutachgdd()); // 得意先マスタ変更日
        dto.setInptsystmymd2(ty.getInptsystmymd()); // 登録システム日付
        dto.setInptsystmjikan2(ty.getInptsystmjikan()); // 登録システム時間
        dto.setToshaapkihoncd2(ty.getToshaapkihoncd()); // 登録者ＡＰ基本コード
        dto.setToshashopcd2(ty.getToshashopcd()); // 登録者ショップコード
        dto.setToshacd2(ty.getToshacd()); // 登録者コード
        dto.setToshamei2(ty.getToshamei()); // 登録者名称
        dto.setKshnsystmymd2(ty.getKshnsystmymd()); // 更新システム日付
        dto.setKshnsystmjikan2(ty.getKshnsystmjikan()); // 更新システム時間
        dto.setKoshaapkihoncd2(ty.getKoshaapkihoncd()); // 更新者ＡＰ基本コード
        dto.setKoshashopcd2(ty.getKoshashopcd()); // 更新者ショップコード
        dto.setKoshacd2(ty.getKoshacd()); // 更新者コード
        dto.setKoshamei2(ty.getKoshamei()); // 更新者名称
        dto.setTokuisakinmn11(ty.getTokuisakinmn1()); // 得意先名称１
        dto.setTokuisakinmn21(ty.getTokuisakinmn2()); // 得意先名称２
        dto.setTokuisakikeishokbn1(ty.getTokuisakikeishokbn()); // 得意先敬称区分
        dto.setTokuisakinmkanan11(ty.getTokuisakinmkanan1()); // 得意先名称カナ１
        dto.setTokuisakinmkanan21(ty.getTokuisakinmkanan2()); // 得意先名称カナ２
        dto.setYubinno1(ty.getYubinno()); // 郵便番号
        dto.setChihoupubdanticdn11(ty.getChihoupubdanticdn1()); // 地方公共団体コード１
        dto.setChihoupubdanticdn21(ty.getChihoupubdanticdn2()); // 地方公共団体コード２
        dto.setAdrsn11(ty.getAdrsn1()); // 住所１
        dto.setAdrsn21(ty.getAdrsn2()); // 住所２
        dto.setAdrsn31(ty.getAdrsn3()); // 住所３
        dto.setRenrakusakinokbnn11(ty.getRenrakusakinokbnn1()); // 連絡先番号区分１
        dto.setRenrakusakinon11(ty.getRenrakusakinon1()); // 連絡先番号１
        dto.setRenrakusakimemon11(ty.getRenrakusakimemon1()); // 連絡先メモ１
        dto.setRenrakusakinokbnn21(ty.getRenrakusakinokbnn2()); // 連絡先番号区分２
        dto.setRenrakusakinon21(ty.getRenrakusakinon2()); // 連絡先番号２
        dto.setRenrakusakimemon21(ty.getRenrakusakimemon2()); // 連絡先メモ２
        dto.setRenrakusakinokbnn31(ty.getRenrakusakinokbnn3()); // 連絡先番号区分３
        dto.setRenrakusakinon31(ty.getRenrakusakinon3()); // 連絡先番号３
        dto.setRenrakusakimemon31(ty.getRenrakusakimemon3()); // 連絡先メモ３
        dto.setRenrakusakinokbnn41(ty.getRenrakusakinokbnn4()); // 連絡先番号区分４
        dto.setRenrakusakinon41(ty.getRenrakusakinon4()); // 連絡先番号４
        dto.setRenrakusakimemon41(ty.getRenrakusakimemon4()); // 連絡先メモ４
        dto.setJigyoshocd1(ty.getJigyoshocd()); // 事業所コード
        dto.setEigyoareacd1(ty.getEigyoareacd()); // 営業エリアコード
        dto.setEigyoareanm1(ty.getEigyoareanm()); // 営業エリア名称
        dto.setEigyotnshacd1(ty.getEigyotnshacd()); // 営業担当者コード
        dto.setEigyotnshanm1(ty.getEigyotnshanm()); // 営業担当者名称
        dto.setDlvrykankatsuapkihoncd1(ty.getDlvrykankatsuapkihoncd()); // 配達管轄ＡＰ基本コード
        dto.setDlvrykankatsushopcd1(ty.getDlvrykankatsushopcd()); // 配達管轄ショップコード
        dto.setApnm(ty.getApnm()); // ＡＰ名称
        dto.setShopnm(ty.getShopnm()); // ショップ名称
        dto.setDlvrykankatsukyotencd1(ty.getDlvrykankatsukyotencd()); // 配達管轄拠点コード
        dto.setDlvrykyotennm(ty.getDlvrykyotennm()); // 配達拠点名称
        dto.setDlvryareacd1(ty.getDlvryareacd()); // 配達エリアコード
        dto.setDlvryareanm1(ty.getDlvryareanm()); // 配達エリア名称
        dto.setDlvrykbn1(ty.getDlvrykbn()); // 配達区分
        dto.setDlvryjun1(ty.getDlvryjun()); // 配達順
        dto.setDlvrycmpnycd1(ty.getDlvrycmpnycd()); // 配達会社コード
        dto.setDlvrycmpnynm1(ty.getDlvrycmpnynm()); // 配達会社名称
        dto.setDlvrytnshacd1(ty.getDlvrytnshacd()); // 配達担当者コード
        dto.setDlvrytnshamei1(ty.getDlvrytnshamei()); // 配達担当者名称
        dto.setPrntmemon11(ty.getPrntmemon1()); // 印刷メモ１
        dto.setPrntmemon21(ty.getPrntmemon2()); // 印刷メモ２
        dto.setPrntmemon31(ty.getPrntmemon3()); // 印刷メモ３
        dto.setNohinsakijohon11(ty.getNohinsakijohon1()); // 納品先情報１
        dto.setNohinsakijohon21(ty.getNohinsakijohon2()); // 納品先情報２
        dto.setChizuno1(ty.getChizuno()); // 地図番号
        dto.setAcptjikanshiteikbn1(ty.getAcptjikanshiteikbn()); // 受注時間指定区分
        dto.setAcptjikanshiteijikoku1(ty.getAcptjikanshiteijikoku()); // 受注時間指定時刻
        dto.setAcptjikanshiteijikokushry1(ty.getAcptjikanshiteijikokushry()); // 受注時間指定時刻終了

        /* 請求先予約情報 */
        dto.setSkysakicd1(sy.getSkysakicd()); // 請求先コード
        dto.setYoyakuinptdd1(sy.getYoyakuinptdd()); // 予約登録日
        dto.setSksmasutachgdd(sy.getSksmasutachgdd()); // 請求先マスタ変更日
        dto.setInptsystmymd3(sy.getInptsystmymd()); // 登録システム日付
        dto.setInptsystmjikan3(sy.getInptsystmjikan()); // 登録システム時間
        dto.setToshaapkihoncd3(sy.getToshaapkihoncd()); // 登録者ＡＰ基本コード
        dto.setToshashopcd3(sy.getToshashopcd()); // 登録者ショップコード
        dto.setToshacd3(sy.getToshacd()); // 登録者コード
        dto.setToshamei3(sy.getToshamei()); // 登録者名
        dto.setKshnsystmymd3(sy.getKshnsystmymd()); // 更新システム日付
        dto.setKshnsystmjikan3(sy.getKshnsystmjikan()); // 更新システム時間
        dto.setKoshaapkihoncd3(sy.getKoshaapkihoncd()); // 更新者ＡＰ基本コード
        dto.setKoshashopcd3(sy.getKoshashopcd()); // 更新者ショップコード
        dto.setKoshacd3(sy.getKoshacd()); // 更新者コード
        dto.setKoshamei3(sy.getKoshamei()); // 更新者名
        dto.setSkysakinmn11(sy.getSkysakinmn1()); // 請求先名称１
        dto.setSkysakinmn21(sy.getSkysakinmn2()); // 請求先名称２
        dto.setSkysakikeishokbn(sy.getSkysakikeishokbn()); // 請求先敬称区分
        dto.setSkysakinmkana(sy.getSkysakinmkana()); // 請求先名称カナ
        dto.setSkysakinmryakusho(sy.getSkysakinmryakusho()); // 請求先名称略称
        dto.setYubinno2(sy.getYubinno()); // 郵便番号
        dto.setAdrsn12(sy.getAdrsn1()); // 住所１
        dto.setAdrsn22(sy.getAdrsn2()); // 住所２
        dto.setAdrsn32(sy.getAdrsn3()); // 住所３
        dto.setRenrakusakinokbnn12(sy.getRenrakusakinokbnn1()); // 連絡先番号区分１
        dto.setRenrakusakinon12(sy.getRenrakusakinon1()); // 連絡先番号１
        dto.setRenrakusakinokbnn22(sy.getRenrakusakinokbnn2()); // 連絡先番号区分２
        dto.setRenrakusakinon22(sy.getRenrakusakinon2()); // 連絡先番号２
        dto.setRenrakusakinokbnn32(sy.getRenrakusakinokbnn3()); // 連絡先番号区分３
        dto.setRenrakusakinon32(sy.getRenrakusakinon3()); // 連絡先番号３
        dto.setRenrakusakinokbnn42(sy.getRenrakusakinokbnn4()); // 連絡先番号区分４
        dto.setRenrakusakinon42(sy.getRenrakusakinon4()); // 連絡先番号４
        dto.setEigyotnshacd2(sy.getEigyotnshacd()); // 営業担当者コード
        dto.setEigyotnshamei2(sy.getEigyotnshamei()); // 営業担当者名
        dto.setKaishutnshacd(sy.getKaishutnshacd()); // 回収担当者コード
        dto.setKaishutnshamei(sy.getKaishutnshamei()); // 回収担当者名
        dto.setJigyoshocd2(sy.getJigyoshocd()); // 事業所コード

        return dto;
    }

    /**
     * 商品プルダウンの内容を取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param unyodd 運用日付
     * @return 商品DTOリスト
     */
    public List<B0501W02ShnDto> getShnList(String apkihoncd, String shopcd, String unyodd) {

        List<B0501W02ShnDto> resultList = new LinkedList<B0501W02ShnDto>();

        // 検索条件設定
        S02V001TB0501W02Entity key = new S02V001TB0501W02Entity();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setUnyodd(unyodd);

        // 商品マスタビュー取得
        List<S02V001TB0501W02Entity> entities = s02v001tDao.selectByB0501W02(key);

        // DTOに格納
        entities.forEach(e -> resultList.add(createShnDto(e)));

        return resultList;
    }

    /**
     * 商品マスタビューエンティティをControllerへ返却する形式へ変換する。
     *
     * @param e 商品マスタビューカスタムエンティティ
     * @return B0501W02ShnDto
     */
    private B0501W02ShnDto createShnDto(S02V001TB0501W02Entity e) {

        B0501W02ShnDto d = new B0501W02ShnDto(e.getShncd(), // 商品コード
            e.getShnnmryakusho(), // 商品名称略称
            e.getHyojunprice(), // 標準価格
            e.getTaxsetteikbn(), // 税設定区分
            e.getTaxkbn() // 税区分
        );

        return d;
    }

    /**
     * 住所情報を取得する
     * 
     * @param yubinno 郵便番号
     * @return 住所情報DTOリスト
     */
    public List<B0501W02AddressDto> getAddressList(String yubinno) {

        List<B0501W02AddressDto> resultList = new LinkedList<B0501W02AddressDto>();

        // 郵便番号管理マスタの取得
        List<S02M083TEntity> entityList = getYubinnoKanriMst(yubinno);

        // DTOに格納
        entityList.forEach(e -> resultList.add(createAddressDto(e)));

        return resultList;
    }

    /**
     * 郵便番号管理マスタエンティティをControllerへ返却する形式へ変換する。
     *
     * @param e 郵便番号管理マスタエンティティ
     * @return B0501W02AddressDto
     */
    private B0501W02AddressDto createAddressDto(S02M083TEntity e) {

        B0501W02AddressDto d = new B0501W02AddressDto(e.getChihoupubdanticdn1(), // 地方公共団体コード１
            e.getChihoupubdanticdn2(), // 地方公共団体コード２
            e.getYubinno(), // 郵便番号
            e.getTdoufknmei(), // 都道府県名
            e.getShikutyosonmei(), // 市区町村名
            e.getTyoikimei(), // 町域名
            e.getYukokbn() // 有効区分
        );

        return d;
    }

    /**
     * 登録処理
     * 
     * @param dto 登録値格納Dto
     */
    @Transactional
    public void insert(B0501W02UpdateDto dto) {

        // 相関チェック実施
        checkParam(dto);

        // システム日時を設定
        dto.setSystemDate(DateTimeUtil.changeFormatToSystemDate(LocalDateTime.now()));
        dto.setSystemTime(DateTimeUtil.changeFormatToSystemTime(LocalDateTime.now()));

        // 得意先コードの採番
        String tokuisakicd = saibanService.getNoTokuisaki(dto.getApkihoncd(), dto.getShopcd());
        dto.setTokuisakicd(tokuisakicd);

        // 会員コード設定区分が「2:自動生成」の場合、会員コードを採番する
        if (KAIINCDKBN_2.equals(dto.getKaiincdkbn())) {
            String[] kaiincd = saibanService.getSaibanNo(28, 1, 1, null, null, null);
            dto.setKaiincd(kaiincd[0]);
        }

        // 得意先マスタ登録
        insertTokuisaki(dto);

        // 得意先実績登録
        insertTokuisakiJisseki(dto);

        // 画面.会員コード区分が「1:コード指定」/「2:自動生成」の場合
        // ※会員コード区分が非活性の場合NULLになるので、会員コードの設定有無で判断する
        if (KAIINCDKBN_1.equals(dto.getKaiincdkbn()) || KAIINCDKBN_2.equals(dto.getKaiincdkbn())
            || !StringUtils.isEmpty(dto.getKaiincd())) {
            // 会員関連テーブル登録・更新
            updateKaiinInfo(dto);
        }

        // 親子反映機能が「1:チェックON」の場合
        if (!StringUtils.isEmpty(dto.getOyakohaneikinoflg())) {
            // 得意先別商品別単価マスタ登録
            insertShnTankaMst(dto);

            // 得意先別セット商品単価マスタ登録
            insertSetShnTankaMst(dto);
        }

        // キャンペーン実績管理テーブル登録(本部)
        updateCampaignJissekiKanri(dto, true);

        // キャンペーン実績管理テーブル登録(自社)
        updateCampaignJissekiKanri(dto, false);

        // 定例売上仕入情報、設定する＝1(チェックON)の場合
        if (!StringUtils.isEmpty(dto.getTeireisetteikbn())) {
            // 定例売上仕入マスタ登録処理
            updateTeireiUriage(dto);
        }

        // 得意先区分が「1：顧客」、かつ、違約金情報.管理しないがチェックOFF(管理する)の場合
        if (CommonConstants.KBN_TOKUISAKI_KOKUAKU.equals(dto.getTokuisakikbn())
            && StringUtils.isEmpty(dto.getIyakukinkanritrgtgai())) {
            // 得意先違約金管理テーブルの登録処理
            insertIyakukinKanri(dto);
        }

        // 請求先マスタ登録・更新処理
        updateSkysakiMst(dto);

        // 会員履歴作成
        createKaiinRireki(dto);

        // 得意先ポイントマスタ登録
        insertTokuisakiPoint(dto);

        // 得意先ポイントランクマスタ新規登録
        insertTokuisakiPointRank(dto);

        // ステージ切替日とステージ修正（新）の入力がある場合
        if (!StringUtils.isEmpty(dto.getTekiyoudd())
            && !StringUtils.isEmpty(dto.getPointrnkcdshin())) {

            // ポイントランク強制変更予約テーブル登録
            insertPointKyoseiHenkouYoyaku(dto);
        }
    }

    /**
     * 更新処理
     * 
     * @param dto 更新値格納Dto
     */
    @Transactional
    public void update(B0501W02UpdateDto dto) {

        // 相関チェック実施
        checkParam(dto);

        // システム日時を設定
        dto.setSystemDate(DateTimeUtil.changeFormatToSystemDate(LocalDateTime.now()));
        dto.setSystemTime(DateTimeUtil.changeFormatToSystemTime(LocalDateTime.now()));

        // 請求先マスタ悲観ロック
        lockSkysaki(dto);

        S02M050TEntity jissekiEntity = new S02M050TEntity();
        // 「画面.12L預かりボトル本数」「画面.7L預かりボトル本数」どちらか設定値ありの場合
        if (!StringUtils.isEmpty(dto.getAzukaribtln1n2rittoruhns())
            || !StringUtils.isEmpty(dto.getAzukaribtln8rittoruhns())) {
            // 得意先実績テーブル悲観ロック
            jissekiEntity = lockTokuisakiJisseki(dto);
        }

        // 違約金管理情報.管理しないが「チェックON(管理しない)」の場合
        if (!StringUtils.isEmpty(dto.getIyakukinkanritrgtgai())) {
            // 得意先違約金管理情報無効化
            updateIyakukinKanri(dto);
        }

        // 会員コード設定区分が「2:自動生成」の場合、会員コードを採番する
        if (KAIINCDKBN_2.equals(dto.getKaiincdkbn())) {
            String[] kaiincd = saibanService.getSaibanNo(28, 1, 1, null, null, null);
            dto.setKaiincd(kaiincd[0]);
        }

        // 得意先マスタ更新
        updateTokuisaki(dto);

        // 得意先実績テーブルが取得できている場合
        if (!ObjectUtils.isEmpty(jissekiEntity)) {
            // 得意先実績テーブル更新
            updateTokuisakiJisseki(dto);
        }

        // 画面.会員コード区分が「1:コード指定」/「2:自動生成」の場合
        // ※会員コード区分が非活性の場合NULLになるので、会員コードの設定有無で判断する
        if (KAIINCDKBN_1.equals(dto.getKaiincdkbn()) || KAIINCDKBN_2.equals(dto.getKaiincdkbn())
            || !StringUtils.isEmpty(dto.getKaiincd())) {
            // 会員関連テーブル登録・更新
            updateKaiinInfo(dto);
        }

        // 親子反映機能が「1:チェックON」の場合
        if (!StringUtils.isEmpty(dto.getOyakohaneikinoflg())) {
            // 得意先別商品別単価マスタ登録
            insertShnTankaMst(dto);

            // 得意先別セット商品単価マスタ登録
            insertSetShnTankaMst(dto);
        }

        // キャンペーン実績管理テーブル登録(本部)
        updateCampaignJissekiKanri(dto, true);

        // キャンペーン実績管理テーブル登録(自社)
        updateCampaignJissekiKanri(dto, false);

        // 得意先予約マスタ登録・更新
        updateTokuisakiYoyaku(dto);

        // 請求先予約マスタ登録・更新
        updateSkysakiYoyaku(dto);

        // 請求先マスタ登録・更新処理
        updateSkysakiMst(dto);

        // 定例売上仕入情報、設定する＝1(チェックON)の場合
        if (!StringUtils.isEmpty(dto.getTeireisetteikbn())) {
            // 定例売上仕入マスタ登録処理
            updateTeireiUriage(dto);
        }

        // 得意先区分が「1：顧客」、かつ、違約金情報.管理しないがチェックOFF(管理する)の場合
        if (CommonConstants.KBN_TOKUISAKI_KOKUAKU.equals(dto.getTokuisakikbn())
            && StringUtils.isEmpty(dto.getIyakukinkanritrgtgai())) {
            // 得意先違約金管理テーブルの登録処理
            insertIyakukinKanri(dto);
        }

        // 会員履歴作成
        createKaiinRireki(dto);

        // 得意先ポイントマスタ登録・更新処理
        updateTokuisakiPoint(dto);

        // ポイントランク強制変更予約テーブルを登録・更新
        upsertPointKyoseiHenkou(dto);
    }

    /**
     * 請求先マスタ悲観ロック処理
     * 
     * @param dto 更新用DTO
     */
    private void lockSkysaki(B0501W02UpdateDto dto) {
        try {
            // 検索条件の設定
            S02M017TEntityKey key = new S02M017TEntityKey();
            key.setApkihoncd(dto.getApkihoncd());
            key.setShopcd(dto.getShopcd());

            // 請求先コード設定なしの場合
            if (StringUtils.isEmpty(dto.getSkysakicd())) {
                // 初期表示時.請求先コード設定ありの場合
                if (!StringUtils.isEmpty(dto.getSkysakicdbefore())) {
                    key.setSkysakicd(dto.getSkysakicdbefore());
                    s02m017tDao.selectForUpdateNowaitByPrimaryKey(key);
                }

                // 請求先コード設定ありの場合
            } else {
                key.setSkysakicd(dto.getSkysakicd());
                s02m017tDao.selectForUpdateNowaitByPrimaryKey(key);
            }
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.W090);
        }
    }

    /**
     * 得意先実績テーブル悲観ロック処理
     * 
     * @param dto 更新用DTO
     * @return S02M050TEntity
     */
    private S02M050TEntity lockTokuisakiJisseki(B0501W02UpdateDto dto) {
        try {
            // 検索条件の設定
            S02M050TEntityKey key = new S02M050TEntityKey();
            key.setApkihoncd(dto.getApkihoncd());
            key.setShopcd(dto.getShopcd());
            key.setTokuisakicd(dto.getTokuisakicd());

            return s02m050tDao.selectForUpdateNowaitByPrimaryKey(key);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.W090);
        }
    }

    /**
     * 得意先マスタ登録処理
     * 
     * @param dto 登録値格納Dto
     */
    private void insertTokuisaki(B0501W02UpdateDto dto) {
        try {
            s02m013tDao.insertSelective(createTokuisakiUpdateEntity(dto, null));

        } catch (DuplicateKeyException e) {
            // 一意制約違反の場合、エラー
            throw BusinessExceptionFactory.validationFailed(e, MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.E002);
        }
    }

    /**
     * 得意先マスタ更新処理
     * 
     * @param dto 登録値格納Dto
     */
    private void updateTokuisaki(B0501W02UpdateDto dto) {

        boolean lockFlg = false;

        try {
            // 検索条件の設定
            S02M013TEntityKey key = new S02M013TEntityKey();
            key.setApkihoncd(dto.getApkihoncd());
            key.setShopcd(dto.getShopcd());
            key.setTokuisakicd(dto.getTokuisakicd());

            // 得意先マスタを悲観ロックで取得
            S02M013TEntity entity = s02m013tDao.selectForUpdateNowaitByPrimaryKey(key);

            // 更新システム日付、更新システム時間が画面表示時点と一致しない場合は、エラー
            if (ObjectUtils.isEmpty(entity)
                || !entity.getKshnsystmymd().equals(dto.getKshnsystmymd())
                || !entity.getKshnsystmjikan().equals(dto.getKshnsystmjikan())) {
                lockFlg = true;
            }

            // UPDATE実行
            if (!lockFlg) {
                s02m013tDao.updateByPrimaryKey(createTokuisakiUpdateEntity(dto, entity));
            }
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.E002);
        }

        if (lockFlg) {
            throw BusinessExceptionFactory.validationFailed(MessageID.W090);
        }
    }

    /**
     * 得意先実績テーブル登録処理
     * 
     * @param dto 登録値格納Dto
     */
    private void insertTokuisakiJisseki(B0501W02UpdateDto dto) {
        try {
            // INSERT実行
            s02m050tDao.insertSelective(createTokuisakiJissekiInsertEntity(dto));

        } catch (DuplicateKeyException e) {
            // 一意制約違反の場合、エラー
            throw BusinessExceptionFactory.validationFailed(e, MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.E002);
        }
    }

    /**
     * 得意先実績テーブル更新処理
     * 
     * @param dto 登録値格納Dto
     */
    private void updateTokuisakiJisseki(B0501W02UpdateDto dto) {
        try {
            // UPDATE実行
            s02m050tDao.updateByPrimaryKeySelective(createTokuisakiJissekiUpdateEntity(dto));
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.E002);
        }
    }

    /**
     * 会員関連テーブル更新処理
     * 
     * @param dto 更新値格納Dto
     */
    private void updateKaiinInfo(B0501W02UpdateDto dto) {

        long shozaiCnt = 0; // 所在管理件数

        try {
            // 画面.入会日が入力された場合
            if (!StringUtils.isEmpty(dto.getNyukaidd())) {
                // ==============
                // 会員所在管理テーブル登録・更新処理
                // ==============
                S03T022TEntity updateEntity = createShozaiKanriUpdateEntity(dto);
                // 旧所在クリア
                s03t022tDao.updateShozaiClearByB0501W02(updateEntity);

                // 所在管理件数取得
                shozaiCnt = s03t022tDao.countByB0501W02(updateEntity);

                // 所在管理区分設定
                updateEntity.setSzikanrikbn(
                    shozaiCnt > 0 ? CommonConstants.SZIKANRIKBN_0 : CommonConstants.SZIKANRIKBN_1);

                // 会員所在管理更新
                int updateCnt = s03t022tDao.updateByB0501W02(updateEntity);

                // 更新件数が0件の場合
                if (updateCnt == 0) {
                    // 新規連番取得
                    String renban = s03t022tDao.getRenbanByB0501W02(updateEntity);
                    updateEntity.setRenban(Short.parseShort(renban));

                    // 会員所在管理テーブル登録
                    s03t022tDao.insertSelective(updateEntity);
                }
            }

            // ==============
            // 会員マスタ登録・更新処理
            // ==============
            // 検索条件の設定
            S02M081TEntityKey key = new S02M081TEntityKey();
            key.setKaiincd(dto.getKaiincd());

            // 会員マスタの取得
            S02M081TEntity entity = s02m081tDao.selectByPrimaryKeyForLock(key);

            // 会員マスタにデータが存在しない場合
            if (ObjectUtils.isEmpty(entity)) {
                // 会員マスタ登録
                s02m081tDao.insertSelective(createKaiinMstUpdateEntity(dto, null));

                // 会員マスタにデータが存在する場合
            } else {
                // 画面.入会日に設定値あり かつ 所在管理件数が0件の場合
                if (!StringUtils.isEmpty(dto.getNyukaidd()) && shozaiCnt == 0) {
                    // 会員マスタ更新
                    s02m081tDao.updateByPrimaryKey(createKaiinMstUpdateEntity(dto, entity));
                }
            }
        } catch (DuplicateKeyException e) {
            // 一意制約違反の場合、エラー
            throw BusinessExceptionFactory.validationFailed(e, MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.E002);
        }
    }

    /**
     * 得意先ポイントマスタ登録処理
     * 
     * @param dto 登録値格納Dto
     */
    private void insertTokuisakiPoint(B0501W02UpdateDto dto) {
        try {
            s02m203tDao.insertSelective(createTokuisakiPointUpdateEntity(dto, null));
        } catch (DuplicateKeyException e) {
            // 一意制約違反の場合、エラー
            throw BusinessExceptionFactory.validationFailed(e, MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.E002);
        }
    }

    /**
     * 得意先ポイントマスタ更新処理
     * 
     * @param dto 登録値格納Dto
     */
    private void updateTokuisakiPoint(B0501W02UpdateDto dto) {
        try {
            // 検索条件の設定
            S02M203TEntityKey key = new S02M203TEntityKey();
            key.setApkihoncd(dto.getApkihoncd());
            key.setShopcd(dto.getShopcd());
            key.setTokuisakicd(dto.getTokuisakicd());

            // 得意先ポイントマスタを悲観ロックで取得
            S02M203TEntity entity = s02m203tDao.selectForUpdateNowaitByPrimaryKey(key);

            // 得意先ポイントマスタにデータが存在しない場合
            if (ObjectUtils.isEmpty(entity)) {
                // 得意先ポイントマスタを登録
                s02m203tDao.insertSelective(createTokuisakiPointUpdateEntity(dto, null));
            } else {
                // 得意先ポイントマスタを更新
                s02m203tDao.updateByPrimaryKey(createTokuisakiPointUpdateEntity(dto, entity));
            }

        } catch (DuplicateKeyException e) {
            // 一意制約違反の場合、エラー
            throw BusinessExceptionFactory.validationFailed(e, MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.E002);
        }
    }

    /**
     * 得意先ポイントランクマスタ登録処理
     * 
     * @param dto 登録値格納Dto
     */
    private void insertTokuisakiPointRank(B0501W02UpdateDto dto) {
        try {
            s02m204tDao.insertSelective(createTokuisakiPointRankUpdateEntity(dto));
        } catch (DuplicateKeyException e) {
            // 一意制約違反の場合、エラー
            throw BusinessExceptionFactory.validationFailed(e, MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.E002);
        }
    }

    /**
     * ポイントランク強制変更予約テーブル登録・更新処理
     * 
     * @param dto 更新用DTO
     */
    private void upsertPointKyoseiHenkou(B0501W02UpdateDto dto) {
        // 入力チェック
        boolean existPointRnkCd = !StringUtils.isEmpty(dto.getPointrnkcdshin());
        boolean existTekiyoudd = !StringUtils.isEmpty(dto.getTekiyoudd());

        // キー生成
        S20T003TEntityKey e = new S20T003TEntityKey();
        e.setApkihoncd(dto.getApkihoncd());
        e.setShopcd(dto.getShopcd());
        e.setTokuisakicd(dto.getTokuisakicd());

        // ポイントランク強制変更予約テーブルの既存データ取得
        S20T003TEntity s20t003tEntity = s20t003tDao.selectByPrimaryKey(e);
        // 既存レコードの存在有無 （存在する：true、存在しない:false）
        boolean exists = !ObjectUtils.isEmpty(s20t003tEntity);
        // 既存レコードが有効無効チェック （有効:true、無効:false）
        boolean isEnabled = exists && CommonConstants.YUKOKBN_1.equals(s20t003tEntity.getYukokbn());

        // ステージ修正（新）またはステージ切替日に入力あり
        if (existPointRnkCd || existTekiyoudd) {
            if (!exists) {
                // 既存レコードなし 新規登録
                insertPointKyoseiHenkouYoyaku(dto);
            } else {
                // 既存レコードあり 更新
                updatePointKyoseiHenkouYoyaku(dto, s20t003tEntity, false);
            }
        } else {
            if (!exists) {
                // レコードがない場合は特に何もしない
                return;
            }
            if (isEnabled) {
                // 有効レコードがある場合は無効化
                updatePointKyoseiHenkouYoyaku(dto, s20t003tEntity, true);
            }
        }
    }

    /**
     * ポイントランク強制変更予約テーブル登録処理
     * 
     * @param dto 登録値格納Dto
     */
    private void insertPointKyoseiHenkouYoyaku(B0501W02UpdateDto dto) {
        try {
            s20t003tDao
                .insertSelective(createPointKyoseiHenkouYoyakuUpdateEntity(dto, null, false));
        } catch (DuplicateKeyException e) {
            // 一意制約違反の場合、エラー
            throw BusinessExceptionFactory.validationFailed(e, MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.E002);
        }
    }

    /**
     * ポイントランク強制変更予約テーブル更新処理
     * 
     * @param dto 登録値格納Dto
     * @param entity 既存データ格納Entity
     * @param isMukou 無効フラグ
     */
    private void updatePointKyoseiHenkouYoyaku(B0501W02UpdateDto dto, S20T003TEntity entity,
        boolean isMukou) {
        try {
            s20t003tDao.updateByPrimaryKey(
                createPointKyoseiHenkouYoyakuUpdateEntity(dto, entity, isMukou));
        } catch (DuplicateKeyException e) {
            // 一意制約違反の場合、エラー
            throw BusinessExceptionFactory.validationFailed(e, MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.E002);
        }
    }

    /**
     * 会員所在管理テーブル更新用Entityを生成する
     * 
     * @param dto 更新値格納Dto
     * @return S03T022TEntity
     */
    private S03T022TEntity createShozaiKanriUpdateEntity(B0501W02UpdateDto dto) {

        S03T022TEntity e = new S03T022TEntity();

        e.setApkihoncd(dto.getApkihoncd()); // ＡＰ基本コード
        e.setShopcd(dto.getShopcd()); // ショップコード
        e.setTokuisakicd(dto.getTokuisakicd()); // 得意先コード
        e.setKaiincd(dto.getKaiincd()); // 会員コード
        e.setNyukaidd(dto.getNyukaidd()); // 入会日
        e.setTaikaidd(dto.getTaikaidd()); // 退会日
        e.setRenrakusakinon1(dto.getRenrakusakinon1()); // 連絡先番号１
        e.setRenrakusakinon2(dto.getRenrakusakinon2()); // 連絡先番号２
        e.setRenrakusakinon3(dto.getRenrakusakinon3()); // 連絡先番号３
        e.setRenrakusakinon4(dto.getRenrakusakinon4()); // 連絡先番号４
        e.setN1n2rittorubtlsuryo(0); // １２Ｌボトル数量
        e.setN8rittorubtlsuryo(0); // ８Ｌボトル数量
        e.setN7rittorubtlsuryo(0); // ７Ｌボトル数量
        e.setVanadiumn8rittorubtlsuryo(0); // バナジウム８Ｌボトル数量
        e.setSonotaseihinmizusuryo(0); // その他製品水数量
        e.setYbi1btlsuryo(0); // 予備１ボトル数量
        e.setYbi2btlsuryo(0); // 予備２ボトル数量
        e.setYbi3btlsuryo(0); // 予備３ボトル数量
        e.setSeihinmizuuriagedaka(LONG_ZERO); // 製品水売上高
        e.setTeireiuriagedaka(LONG_ZERO); // 定例売上高
        e.setSonotauriagedaka(LONG_ZERO); // その他売上高
        e.setSmuriagedaka(LONG_ZERO); // 合計売上高
        e.setTtbetsukaiindkikan(BigDecimal.ZERO); // 得意先別会員期間
        e.setYukokbn(CommonConstants.YUKOKBN_1); // 有効区分
        e.setToshaapkihoncd(dto.getLoginUserApkihoncd()); // 登録者ＡＰ基本コード
        e.setToshashopcd(dto.getLoginUserShopcd()); // 登録者ショップコード
        e.setToshacd(dto.getLoginUserTnshacd()); // 登録者コード
        e.setInptsystmymd(dto.getSystemDate()); // 登録システム日付
        e.setInptsystmjikan(dto.getSystemTime()); // 登録システム時間
        e.setInptunyoymd(dto.getUnyoDate()); // 登録運用日付
        e.setInptipadrse(dto.getIpAddress()); // 登録ＩＰアドレス
        e.setInptpgmid(dto.getProgramId()); // 登録プログラムＩＤ
        e.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        e.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        e.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        e.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        e.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        e.setKshnunyoymd(dto.getUnyoDate()); // 更新運用日付
        e.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        e.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return e;
    }

    /**
     * 会員マスタ更新用Entityを生成する
     * 
     * @param dto 登録更新用DTO
     * @param src 会員マスタエンティティ(新規登録時はnull)
     * @return S02M081TEntity
     */
    private S02M081TEntity createKaiinMstUpdateEntity(B0501W02UpdateDto dto, S02M081TEntity src) {

        boolean insertFlg = ObjectUtils.isEmpty(src) ? true : false;
        S02M081TEntity e = insertFlg ? new S02M081TEntity() : src;

        e.setKaiincd(dto.getKaiincd()); // 会員コード
        e.setKaiinnm1(dto.getTokuisakinmn1()); // 会員名称１
        e.setKaiinnmn2(dto.getTokuisakinmn2()); // 会員名称２
        e.setYubinno(dto.getYubinno()); // 郵便番号
        e.setChihoupubdanticdn1(dto.getChihoupubdanticdn1()); // 地方公共団体コード１
        e.setChihoupubdanticdn2(dto.getChihoupubdanticdn2()); // 地方公共団体コード２
        e.setAdrsn1(dto.getAdrsn1()); // 住所１
        e.setAdrsn2(dto.getAdrsn2()); // 住所２
        e.setAdrsn3(dto.getAdrsn3()); // 住所３
        e.setRenrakusakinokbnn1(dto.getRenrakusakinokbnn1()); // 連絡先番号区分１
        e.setRenrakusakinon1(dto.getRenrakusakinon1()); // 連絡先番号１
        e.setRenrakusakimemon1(dto.getRenrakusakimemon1()); // 連絡先メモ１
        e.setRenrakusakinokbnn2(dto.getRenrakusakinokbnn2()); // 連絡先番号区分２
        e.setRenrakusakinon2(dto.getRenrakusakinon2()); // 連絡先番号２
        e.setRenrakusakimemon2(dto.getRenrakusakimemon2()); // 連絡先メモ２
        e.setRenrakusakinokbnn3(dto.getRenrakusakinokbnn3()); // 連絡先番号区分３
        e.setRenrakusakinon3(dto.getRenrakusakinon3()); // 連絡先番号３
        e.setRenrakusakimemon3(dto.getRenrakusakimemon3()); // 連絡先メモ３
        e.setRenrakusakinokbnn4(dto.getRenrakusakinokbnn4()); // 連絡先番号区分４
        e.setRenrakusakinon4(dto.getRenrakusakinon4()); // 連絡先番号４
        e.setRenrakusakimemon4(dto.getRenrakusakimemon4()); // 連絡先メモ４
        e.setMailadrse(dto.getMailadrse()); // メールアドレス
        e.setSetainushi(dto.getSetainushi()); // 世帯主
        e.setKazokukoseikazudansei(!StringUtils.isEmpty(dto.getKazokukoseikazudansei())
            ? Short.parseShort(dto.getKazokukoseikazudansei())
            : null); // 利用者数
        e.setKazokukoseikazujosei(!StringUtils.isEmpty(dto.getKazokukoseikazujosei())
            ? Short.parseShort(dto.getKazokukoseikazujosei())
            : null); // 内子供数
        e.setKojudykazukbn(dto.getKojudykazukbn()); // 居住年数区分
        e.setPetjohokbn(dto.getPetjohokbn()); // ペット情報区分
        e.setSetaisyunyu(
            !StringUtils.isEmpty(dto.getSetaisyunyu()) ? Integer.parseInt(dto.getSetaisyunyu())
                : null); // 世帯収入
        e.setDoitsusekeninzukbn(dto.getDoitsusekeninzukbn()); // 同一生計人数区分
        e.setGaikokujinninzu(!StringUtils.isEmpty(dto.getGaikokujinninzu())
            ? Short.parseShort(dto.getGaikokujinninzu())
            : null); // 外国人人数
        e.setJugyoinkibkbn(dto.getJugyoinkibkbn()); // 従業員規模区分
        e.setRiyomokutekikbn(dto.getRiyomokutekikbn()); // 利用目的区分
        e.setSeibetsukbn(dto.getSeibetsukbn()); // 性別区分
        e.setNyushatanjodd(dto.getNyushatanjodd()); // 入会者誕生日
        e.setSykugyokbn(dto.getSykugyokbn()); // 職業区分
        e.setNyushadydm(dto.getNyushadydm()); // 入社年月
        e.setShihasyazokukbn(dto.getShihasyazokukbn()); // 支払者続柄区分
        e.setHoshokinkbn(dto.getHoshokinkbn()); // 保証金区分
        e.setCdkindkbn(dto.getCdkindkbn()); // コード種別区分
        e.setGyoshukbn(dto.getGyoshukbn()); // 業種区分
        e.setGyoshushskbn(dto.getGyoshushskbn()); // 業種詳細区分

        // 新規登録時
        if (insertFlg) {
            e.setMoshikomircptdd(dto.getMoshikomircptdd()); // 申込受付日
            e.setShokaiwsvstupdd(dto.getShokaiwsvstupdd()); // 初回サーバー設置日
            e.setNyukaidd(dto.getNyukaidd()); // 入会日
        }

        e.setFttbnyukaidd(!StringUtils.isEmpty(dto.getFttbnyukaiflg()) ? dto.getNyukaidd() : null); // 再入会日
        e.setTaikaidd(dto.getTaikaidd()); // 退会日
        e.setTaikairiyukbn(dto.getTaikairiyukbn()); // 退会理由区分

        // 新規登録時
        if (insertFlg) {
            e.setRknkindkihonflg(
                CommonConstants.PLNCD_000.equals(dto.getPlncd()) ? CommonConstants.RKNKINDKIHONFLG_1
                    : CommonConstants.RKNKINDKIHONFLG_0); // 料金種別基本フラグ
            e.setRknkindtokubetsuuntflg(dto.getRknkindtokubetsuuntflg()); // 料金種別特別単価フラグ
            e.setRknkindtokubetsunebikiflg(dto.getRknkindtokubetsunebikiflg()); // 料金種別特別値引フラグ
            e.setPlncd(dto.getPlncd()); // プランコード
        }

        e.setSaishinrknkindkihonflg(
            CommonConstants.PLNCD_000.equals(dto.getPlncd()) ? CommonConstants.RKNKINDKIHONFLG_1
                : CommonConstants.RKNKINDKIHONFLG_0); // 最新料金種別基本フラグ
        e.setSaishinrknkindtokubetsuuntflg(dto.getRknkindtokubetsuuntflg()); // 最新料金種別特別単価フラグ
        e.setSaishinrknkindtokubetsunbkflg(dto.getRknkindtokubetsunebikiflg()); // 最新料金種別特別値引フラグ
        e.setSaishinplncd(dto.getPlncd()); // 最新プランコード
        e.setSaishinplncd2(null); // 最新プランコード２
        e.setSaishinplncd3(null); // 最新プランコード３
        e.setKojinsyoumeikindkbn(dto.getKojinsyoumeikindkbn()); // 個人証明種別区分
        e.setKojinsyoumeino(dto.getKojinsyoumeino()); // 個人証明番号

        // 新規登録時
        if (insertFlg) {
            e.setKeizokurnk(CommonConstants.KEIZOKURNK_E); // 継続ランク
            e.setShohirnk(CommonConstants.SHOHIRNK_D); // 消費ランク
            e.setRcptkbn(dto.getRcptkbn()); // 受付区分
            e.setKakutokukbn(dto.getKakutokukbn()); // 獲得区分
        }

        e.setSaishinrcptkbn(dto.getRcptkbn()); // 最新受付区分
        e.setSaishinkakutokukbn(dto.getKakutokukbn()); // 最新獲得区分
        e.setTokuisakishoshakazu(Short.parseShort(dto.getTokuisakishoshakazu())); // 得意先紹介者数

        // 新規登録時
        if (insertFlg) {
            e.setSksapkihoncd(dto.getApkihoncd()); // 請求先ＡＰ基本コード
            e.setSksshopcd(dto.getShopcd()); // 請求先ショップコード
            e.setSkysakicd(getSkysakicd(dto)); // 請求先コード
            e.setTtapkihoncd(dto.getApkihoncd()); // 得意先ＡＰ基本コード
            e.setTokuisakishopcd(dto.getShopcd()); // 得意先ショップコード
            e.setTokuisakicd(dto.getTokuisakicd()); // 得意先コード
            e.setN1n2rittorubtlsuryo(0); // １２Ｌボトル数量
            e.setN8rittorubtlsuryo(0); // ８Ｌボトル数量
            e.setN7rittorubtlsuryo(0); // ７Ｌボトル数量
            e.setVanadiumn8rittorubtl(0); // バナジウム８Ｌボトル
            e.setSonotaseihinmizusuryo(0); // その他製品水数量
            e.setYbi1btl(0); // 予備１ボトル
            e.setYbi2btl(0); // 予備２ボトル
            e.setYbi3btl(0); // 予備３ボトル
            e.setSeihinmizuuriagedaka(LONG_ZERO); // 製品水売上高
            e.setTeireiuriagedaka(LONG_ZERO); // 定例売上高
            e.setSonotauriagedaka(LONG_ZERO); // その他売上高
            e.setSmuriagedaka(LONG_ZERO); // 合計売上高
            e.setToshaapkihoncd(dto.getLoginUserApkihoncd()); // 登録者ＡＰ基本コード
            e.setToshashopcd(dto.getLoginUserShopcd()); // 登録者ショップコード
            e.setToshacd(dto.getLoginUserTnshacd()); // 登録者コード
            e.setInptsystmymd(dto.getSystemDate()); // 登録システム日付
            e.setInptsystmjikan(dto.getSystemTime()); // 登録システム時間
            e.setInptunyoymd(dto.getUnyoDate()); // 登録運用日付
            e.setInptipadrse(dto.getIpAddress()); // 登録ＩＰアドレス
            e.setInptpgmid(dto.getProgramId()); // 登録プログラムＩＤ

            // 更新時
        } else {
            if ((StringUtils.isEmpty(e.getSksapkihoncd()) || StringUtils.isEmpty(e.getSksshopcd())
                || StringUtils.isEmpty(e.getSkysakicd()))
                && (!ItemUtil.nullToString(e.getSksapkihoncd()).equals(dto.getApkihoncd())
                    || !ItemUtil.nullToString(e.getSksshopcd()).equals(dto.getShopcd())
                    || !ItemUtil.nullToString(e.getSkysakicd()).equals(dto.getSkysakicd()))) {
                e.setZnksksapkihoncd(e.getSksapkihoncd()); // 前回請求先ＡＰ基本コード
                e.setZnksksshopcd(e.getSksshopcd()); // 前回請求先ショップコード
                e.setZnkskscd(e.getSkysakicd()); // 前回請求先コード
                e.setSksapkihoncd(dto.getApkihoncd()); // 請求先ＡＰ基本コード
                e.setSksshopcd(dto.getShopcd()); // 請求先ショップコード
                e.setSkysakicd(dto.getSkysakicd()); // 請求先コード
            }

            if ((StringUtils.isEmpty(e.getTtapkihoncd())
                || StringUtils.isEmpty(e.getTokuisakishopcd())
                || StringUtils.isEmpty(e.getTokuisakicd()))
                && (!ItemUtil.nullToString(e.getTtapkihoncd()).equals(dto.getApkihoncd())
                    || !ItemUtil.nullToString(e.getTokuisakishopcd()).equals(dto.getShopcd())
                    || !ItemUtil.nullToString(e.getTokuisakicd()).equals(dto.getTokuisakicd()))) {
                e.setZnkttapkihoncd(e.getTtapkihoncd()); // 前回得意先ＡＰ基本コード
                e.setZnkttshopcd(e.getTokuisakishopcd()); // 前回得意先ショップコード
                e.setZnkttcd(e.getTokuisakicd()); // 前回得意先コード
                e.setTtapkihoncd(dto.getApkihoncd()); // 得意先ＡＰ基本コード
                e.setTokuisakishopcd(dto.getShopcd()); // 得意先ショップコード
                e.setTokuisakicd(dto.getTokuisakicd()); // 得意先コード
            }
        }

        e.setYukokbn(CommonConstants.YUKOKBN_1); // 有効区分
        e.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        e.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        e.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        e.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        e.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        e.setKshnunyoymd(dto.getUnyoDate()); // 更新運用日付
        e.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        e.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return e;
    }

    /**
     * 得意先別商品別単価マスタ登録処理
     * 
     * @param dto 更新値格納Dto
     */
    private void insertShnTankaMst(B0501W02UpdateDto dto) {
        try {
            // 削除条件の設定
            S02M009TEntityExample deleteExample = new S02M009TEntityExample();
            deleteExample.createCriteria().andApkihoncdEqualTo(dto.getApkihoncd())
                .andShopcdEqualTo(dto.getShopcd()).andTokuisakicdEqualTo(dto.getTokuisakicd());

            // 得意先別商品別単価マスタを削除する
            s02m009tDao.deleteByExample(deleteExample);

            // 検索条件の設定
            S02M009TEntityExample s02m009tExample = new S02M009TEntityExample();
            s02m009tExample.createCriteria().andApkihoncdEqualTo(dto.getApkihoncd())
                .andShopcdEqualTo(dto.getShopcd()).andTokuisakicdEqualTo(dto.getOyatokuisakicd())
                .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

            // 得意先別商品別単価マスタ取得
            List<S02M009TEntity> entityList = s02m009tDao.selectByExample(s02m009tExample);

            // 得意先別商品別単価マスタ登録
            entityList
                .forEach(e -> s02m009tDao.insertSelective(createShnTankaMstInsertEntity(dto, e)));

        } catch (DuplicateKeyException e) {
            // 一意制約違反の場合、エラー
            throw BusinessExceptionFactory.validationFailed(e, MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.E002);
        }
    }

    /**
     * 得意先別商品別単価マスタ登録用Entityを生成する
     * 
     * @param dto 登録更新用DTO
     * @param src 登録元得意先別商品別単価マスタエンティティ
     * @return S02M009TEntity
     */
    private S02M009TEntity createShnTankaMstInsertEntity(B0501W02UpdateDto dto,
        S02M009TEntity src) {

        S02M009TEntity e = new S02M009TEntity();

        e.setApkihoncd(dto.getApkihoncd()); // ＡＰ基本コード
        e.setShopcd(dto.getShopcd()); // ショップコード
        e.setShncd(src.getShncd()); // 商品コード
        e.setTokuisakicd(dto.getTokuisakicd()); // 得意先コード
        e.setTekiyostrtdd(src.getTekiyostrtdd()); // 適用開始日
        e.setTekiyoshrydd(src.getTekiyoshrydd()); // 適用終了日
        e.setDlvryprice(src.getDlvryprice()); // 配達価格
        e.setMochikaeriprice(src.getMochikaeriprice()); // 持ち帰り価格
        e.setYukokbn(CommonConstants.YUKOKBN_1); // 有効区分
        e.setToshaapkihoncd(dto.getLoginUserApkihoncd()); // 登録者ＡＰ基本コード
        e.setToshashopcd(dto.getLoginUserShopcd()); // 登録者ショップコード
        e.setToshacd(dto.getLoginUserTnshacd()); // 登録者コード
        e.setInptsystmymd(dto.getSystemDate()); // 登録システム日付
        e.setInptsystmjikan(dto.getSystemTime()); // 登録システム時間
        e.setInptunyoymd(dto.getUnyoDate()); // 登録運用日付
        e.setInptipadrse(dto.getIpAddress()); // 登録ＩＰアドレス
        e.setInptpgmid(dto.getProgramId()); // 登録プログラムＩＤ
        e.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        e.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        e.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        e.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        e.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        e.setKshnunyoymd(dto.getUnyoDate()); // 更新運用日付
        e.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        e.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return e;
    }

    /**
     * 得意先別セット商品単価マスタ登録処理
     * 
     * @param dto 更新値格納Dto
     */
    private void insertSetShnTankaMst(B0501W02UpdateDto dto) {
        try {
            // 削除条件の設定
            S02M066TEntityExample deleteExampleSet = new S02M066TEntityExample();
            deleteExampleSet.createCriteria().andApkihoncdEqualTo(dto.getApkihoncd())
                .andShopcdEqualTo(dto.getShopcd()).andTokuisakicdEqualTo(dto.getTokuisakicd());

            // 得意先別セット商品単価マスタを削除する
            s02m066tDao.deleteByExample(deleteExampleSet);

            // 削除条件の設定
            S02M067TEntityExample deleteExampleTenkai = new S02M067TEntityExample();
            deleteExampleTenkai.createCriteria().andApkihoncdEqualTo(dto.getApkihoncd())
                .andShopcdEqualTo(dto.getShopcd()).andTokuisakicdEqualTo(dto.getTokuisakicd());

            // 得意先セット商品単価展開マスタを削除する
            s02m067tDao.deleteByExample(deleteExampleTenkai);

            // 検索条件の設定
            S02M066TEntityExample s02m066tExample = new S02M066TEntityExample();
            s02m066tExample.createCriteria().andApkihoncdEqualTo(dto.getApkihoncd())
                .andShopcdEqualTo(dto.getShopcd()).andTokuisakicdEqualTo(dto.getOyatokuisakicd())
                .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

            // 得意先別セット商品単価マスタ取得
            List<S02M066TEntity> entityListSet = s02m066tDao.selectByExample(s02m066tExample);

            // 得意先別セット商品別単価マスタ登録
            entityListSet.forEach(
                e -> s02m066tDao.insertSelective(createSetShnTankaMstInsertEntity(dto, e)));

            // 検索条件の設定
            S02M067TEntityExample s02m067tExample = new S02M067TEntityExample();
            s02m067tExample.createCriteria().andApkihoncdEqualTo(dto.getApkihoncd())
                .andShopcdEqualTo(dto.getShopcd()).andTokuisakicdEqualTo(dto.getOyatokuisakicd())
                .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

            // 得意先セット商品単価展開マスタ取得
            List<S02M067TEntity> entityListTenkai = s02m067tDao.selectByExample(s02m067tExample);

            // 得意先セット商品単価展開マスタ登録
            entityListTenkai.forEach(
                e -> s02m067tDao.insertSelective(createSetShnTenkaiMstInsertEntity(dto, e)));

        } catch (DuplicateKeyException e) {
            // 一意制約違反の場合、エラー
            throw BusinessExceptionFactory.validationFailed(e, MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.E002);
        }
    }

    /**
     * 得意先別セット商品単価マスタ登録用Entityを生成する
     * 
     * @param dto 登録更新用DTO
     * @param src 登録元得意先別セット商品単価マスタエンティティ
     * @return S02M066TEntity
     */
    private S02M066TEntity createSetShnTankaMstInsertEntity(B0501W02UpdateDto dto,
        S02M066TEntity src) {

        S02M066TEntity e = new S02M066TEntity();

        e.setApkihoncd(dto.getApkihoncd()); // ＡＰ基本コード
        e.setShopcd(dto.getShopcd()); // ショップコード
        e.setStshncd(src.getStshncd()); // セット商品コード
        e.setTokuisakicd(dto.getTokuisakicd()); // 得意先コード
        e.setTekiyostrtdd(src.getTekiyostrtdd()); // 適用開始日
        e.setTekiyoshrydd(src.getTekiyoshrydd()); // 適用終了日
        e.setDlvryprice(src.getDlvryprice()); // 配達価格
        e.setMochikaeriprice(src.getMochikaeriprice()); // 持ち帰り価格
        e.setYukokbn(CommonConstants.YUKOKBN_1); // 有効区分
        e.setToshaapkihoncd(dto.getLoginUserApkihoncd()); // 登録者ＡＰ基本コード
        e.setToshashopcd(dto.getLoginUserShopcd()); // 登録者ショップコード
        e.setToshacd(dto.getLoginUserTnshacd()); // 登録者コード
        e.setInptsystmymd(dto.getSystemDate()); // 登録システム日付
        e.setInptsystmjikan(dto.getSystemTime()); // 登録システム時間
        e.setInptunyoymd(dto.getUnyoDate()); // 登録運用日付
        e.setInptipadrse(dto.getIpAddress()); // 登録ＩＰアドレス
        e.setInptpgmid(dto.getProgramId()); // 登録プログラムＩＤ
        e.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        e.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        e.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        e.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        e.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        e.setKshnunyoymd(dto.getUnyoDate()); // 更新運用日付
        e.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        e.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return e;
    }

    /**
     * 得意先セット商品単価展開マスタ登録用Entityを生成する
     * 
     * @param dto 登録更新用DTO
     * @param src 登録元得意先セット商品単価展開マスタエンティティ
     * @return S02M067TEntity
     */
    private S02M067TEntity createSetShnTenkaiMstInsertEntity(B0501W02UpdateDto dto,
        S02M067TEntity src) {

        S02M067TEntity e = new S02M067TEntity();

        e.setApkihoncd(dto.getApkihoncd()); // ＡＰ基本コード
        e.setShopcd(dto.getShopcd()); // ショップコード
        e.setStshncd(src.getStshncd()); // セット商品コード
        e.setTokuisakicd(dto.getTokuisakicd()); // 得意先コード
        e.setTekiyostrtdd(src.getTekiyostrtdd()); // 適用開始日
        e.setRenban(src.getRenban()); // 連番
        e.setShncd(src.getShncd()); // 商品コード
        e.setDlvryprice(src.getDlvryprice()); // 配達価格
        e.setMochikaeriprice(src.getMochikaeriprice()); // 持ち帰り価格
        e.setYukokbn(CommonConstants.YUKOKBN_1); // 有効区分
        e.setToshaapkihoncd(dto.getLoginUserApkihoncd()); // 登録者ＡＰ基本コード
        e.setToshashopcd(dto.getLoginUserShopcd()); // 登録者ショップコード
        e.setToshacd(dto.getLoginUserTnshacd()); // 登録者コード
        e.setInptsystmymd(dto.getSystemDate()); // 登録システム日付
        e.setInptsystmjikan(dto.getSystemTime()); // 登録システム時間
        e.setInptunyoymd(dto.getUnyoDate()); // 登録運用日付
        e.setInptipadrse(dto.getIpAddress()); // 登録ＩＰアドレス
        e.setInptpgmid(dto.getProgramId()); // 登録プログラムＩＤ
        e.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        e.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        e.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        e.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        e.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        e.setKshnunyoymd(dto.getUnyoDate()); // 更新運用日付
        e.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        e.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return e;
    }

    /**
     * キャンペーン実績管理テーブル登録処理
     * 
     * @param dto 更新値格納Dto
     * @param honbuFlg true: 本部 / false:自社
     */
    private void updateCampaignJissekiKanri(B0501W02UpdateDto dto, boolean honbuFlg) {
        try {
            // 画面.獲得本部キャンペーンコード または 獲得キャンペーンコードが未設定の場合
            if (StringUtils
                .isEmpty(honbuFlg ? dto.getKakutokuhonbukynpncd() : dto.getKakutokukynpncd())) {
                // キャンペーン実績管理テーブルを削除する
                s03t018tDao.deleteByExample(createCampaignJissekiKanriExample(dto, honbuFlg));

                // 画面.獲得本部キャンペーンコード または 獲得キャンペーンコードが設定ありの場合
            } else {
                S03T018TEntityExample example = createCampaignJissekiKanriExample(dto, honbuFlg);
                example.setOrderByClause("KANRINO DESC");

                List<S03T018TEntity> entityList = s03t018tDao.selectByExample(example);

                // 管理番号が取得出来た場合
                if (!ObjectUtils.isEmpty(entityList) && entityList.size() > 0) {
                    // キャンペーン実績管理テーブルを更新する
                    s03t018tDao.updateByPrimaryKey(
                        createCampaignJissekiKanriUpdateEntity(dto, entityList.get(0), honbuFlg));

                    // 管理番号が取得出来なかった場合
                } else {
                    // キャンペーン実績管理テーブルを登録する
                    s03t018tDao.insertSelective(
                        createCampaignJissekiKanriUpdateEntity(dto, null, honbuFlg));
                }
            }
        } catch (DuplicateKeyException e) {
            // 一意制約違反の場合、エラー
            throw BusinessExceptionFactory.validationFailed(e, MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.E002);
        }
    }

    /**
     * キャンペーン実績管理テーブルの検索条件値を設定する。
     * 
     * @param dto 更新用DTO
     * @param honbuFlg true: 本部 / false:自社
     * @return S03T018TEntityExample
     */
    private S03T018TEntityExample createCampaignJissekiKanriExample(B0501W02UpdateDto dto,
        boolean honbuFlg) {

        S03T018TEntityExample example = new S03T018TEntityExample();
        example.createCriteria().andTorikomimotoapkihoncdEqualTo(dto.getApkihoncd())
            .andTorikomimotoshopcdEqualTo(dto.getShopcd())
            .andKynpnapkihoncdEqualTo(honbuFlg ? CommonConstants.APCD_001 : dto.getApkihoncd())
            .andKynpnshopcdEqualTo(honbuFlg ? CommonConstants.SYS_SHOPCD_000 : dto.getShopcd())
            .andApkihoncdEqualTo(dto.getApkihoncd()).andShopcdEqualTo(dto.getShopcd())
            .andTokuisakicdEqualTo(dto.getTokuisakicd())
            .andKynpnkakutokukbnEqualTo(CommonConstants.KYNPNKAKUTOKUKBN_1)
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

        return example;
    }

    /**
     * キャンペーン実績管理テーブル登録用Entityを生成する
     * 
     * @param dto 更新用DTO
     * @param src キャンペーン実績管理エンティティ(新規登録時はnull)
     * @param honbuFlg true: 本部 / false:自社
     * @return S03T018TEntity
     */
    private S03T018TEntity createCampaignJissekiKanriUpdateEntity(B0501W02UpdateDto dto,
        S03T018TEntity src, boolean honbuFlg) {

        boolean insertFlg = ObjectUtils.isEmpty(src) ? true : false;
        S03T018TEntity e = insertFlg ? new S03T018TEntity() : src;

        // 新規登録時
        if (insertFlg) {
            e.setTorikomimotoapkihoncd(dto.getApkihoncd()); // 取込元ＡＰ基本コード
            e.setTorikomimotoshopcd(dto.getShopcd()); // 取込元ショップコード

            // 管理番号を採番する
            String[] kanrino =
                saibanService.getSaibanNo(34, 0, 1, dto.getApkihoncd(), dto.getShopcd(), null);
            e.setKanrino(kanrino[0]); // 管理番号
        }

        e.setKynpnapkihoncd(honbuFlg ? CommonConstants.APCD_001 : dto.getApkihoncd()); // キャンペーンＡＰ基本コード
        e.setKynpnshopcd(honbuFlg ? CommonConstants.SYS_SHOPCD_000 : dto.getShopcd()); // キャンペーンショップコード
        e.setKynpncd(honbuFlg ? dto.getKakutokuhonbukynpncd() : dto.getKakutokukynpncd()); // キャンペーンコード
        e.setKynpnkakutokukbn(CommonConstants.KYNPNKAKUTOKUKBN_1); // キャンペーン獲得区分
        e.setJotaikbn(CommonConstants.JOTAIKBN_0); // 状態区分
        e.setObotosendd(dto.getMoshikomircptdd()); // 応募当選日
        e.setApkihoncd(dto.getApkihoncd()); // ＡＰ基本コード
        e.setShopcd(dto.getShopcd()); // ショップコード
        e.setTokuisakicd(dto.getTokuisakicd()); // 得意先コード
        e.setKeihincd(honbuFlg ? dto.getHonbukynpnkeihinkbn() : dto.getKynpnkeihinkbn()); // 景品コード
        e.setShoukaino(dto.getHonshokaino()); // 紹介番号
        e.setAiteapkihoncd(null); // 相手ＡＰ基本コード
        e.setAiteshopcd(null); // 相手ショップコード
        e.setAitettcd(null); // 相手得意先コード
        e.setBiko(null); // 備考
        e.setYukokbn(CommonConstants.YUKOKBN_1); // 有効区分

        // 新規登録時
        if (insertFlg) {
            e.setToshaapkihoncd(dto.getLoginUserApkihoncd()); // 登録者ＡＰ基本コード
            e.setToshashopcd(dto.getLoginUserShopcd()); // 登録者ショップコード
            e.setToshacd(dto.getLoginUserTnshacd()); // 登録者コード
            e.setInptsystmymd(dto.getSystemDate()); // 登録システム日付
            e.setInptsystmjikan(dto.getSystemTime()); // 登録システム時間
            e.setInptunyoymd(dto.getUnyoDate()); // 登録運用日付
            e.setInptipadrse(dto.getIpAddress()); // 登録ＩＰアドレス
            e.setInptpgmid(dto.getProgramId()); // 登録プログラムＩＤ
        }

        e.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        e.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        e.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        e.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        e.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        e.setKshnunyoymd(dto.getUnyoDate()); // 更新運用日付
        e.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        e.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return e;
    }

    /**
     * 得意先予約マスタ登録・更新処理
     * 
     * @param dto 更新値格納Dto
     */
    private void updateTokuisakiYoyaku(B0501W02UpdateDto dto) {

        // 得意先マスタ変更が「0：チェックOFF」、かつ得意先予約登録日(hidden)が未設定の場合、処理しない
        if (StringUtils.isEmpty(dto.getTokuisakimasutahnkflg())
            && StringUtils.isEmpty(dto.getYoyakuinptdd1())) {
            return;
        }

        try {
            // 検索条件の設定
            S02M084TEntityKey key = new S02M084TEntityKey();
            key.setApkihoncd(dto.getApkihoncd());
            key.setShopcd(dto.getShopcd());
            key.setTokuisakicd(dto.getTokuisakicd());
            key.setYoyakuinptdd(
                !StringUtils.isEmpty(dto.getYoyakuinptdd1()) ? dto.getYoyakuinptdd1()
                    : dto.getSystemDate());

            // 得意先予約マスタを取得する
            S02M084TEntity eintity = s02m084tDao.selectByPrimaryKey(key);

            // 抽出結果なしの場合
            if (ObjectUtils.isEmpty(eintity)) {
                // 得意先予約マスタを登録する
                s02m084tDao.insertSelective(createTokuisakiYoyakuEntity(dto, null));

                // 得意先マスタ変更が「0：チェックOFF」、かつ得意先予約登録日(hidden)が設定ありの場合
            } else if (StringUtils.isEmpty(dto.getTokuisakimasutahnkflg())
                && !StringUtils.isEmpty(dto.getYoyakuinptdd1())) {
                // 得意先予約マスタを論理削除する
                s02m084tDao.updateByPrimaryKeySelective(createTokuisakiYoyakuDeleteEntity(dto));

                // 上記以外の場合
            } else {
                // 得意先予約マスタを更新する
                s02m084tDao.updateByPrimaryKey(createTokuisakiYoyakuEntity(dto, eintity));
            }
        } catch (DuplicateKeyException e) {
            // 一意制約違反の場合、エラー
            throw BusinessExceptionFactory.validationFailed(e, MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.E002);
        }
    }

    /**
     * 得意先予約マスタ登録・更新用Entityを生成する
     * 
     * @param dto 登録更新用DTO
     * @param src 得意先予約マスタエンティティ(新規登録時はnull)
     * @return S02M084TEntity
     */
    private S02M084TEntity createTokuisakiYoyakuEntity(B0501W02UpdateDto dto, S02M084TEntity src) {

        boolean insertFlg = ObjectUtils.isEmpty(src) ? true : false;
        S02M084TEntity e = insertFlg ? new S02M084TEntity() : src;

        // 新規登録時
        if (insertFlg) {
            e.setApkihoncd(dto.getApkihoncd()); // ＡＰ基本コード
            e.setShopcd(dto.getShopcd()); // ショップコード
            e.setTokuisakicd(dto.getTokuisakicd()); // 得意先コード
            e.setYoyakuinptdd(dto.getSystemDate()); // 予約登録日
        }

        e.setTtmasutachgdd(dto.getTtmasutachgdd()); // 得意先マスタ変更日
        e.setJigyoshocd(dto.getJigyoshocd1()); // 事業所コード
        e.setTokuisakinmn1(dto.getTokuisakinmn11()); // 得意先名称１
        e.setTokuisakinmn2(dto.getTokuisakinmn21()); // 得意先名称２
        e.setTokuisakikeishokbn(dto.getTokuisakikeishokbn1()); // 得意先敬称区分
        e.setTokuisakinmkanan1(dto.getTokuisakinmkanan11()); // 得意先名称カナ１
        e.setTokuisakinmkanan2(dto.getTokuisakinmkanan21()); // 得意先名称カナ２
        e.setYubinno(dto.getYubinno1()); // 郵便番号
        e.setChihoupubdanticdn1(dto.getTokuisakichihoupubdanticdn1()); // 地方公共団体コード１
        e.setChihoupubdanticdn2(dto.getTokuisakichihoupubdanticdn2()); // 地方公共団体コード２
        e.setAdrsn1(dto.getAdrsn11()); // 住所１
        e.setAdrsn2(dto.getAdrsn21()); // 住所２
        e.setAdrsn3(dto.getAdrsn31()); // 住所３
        e.setRenrakusakinokbnn1(dto.getRenrakusakinokbnn11()); // 連絡先番号区分１
        e.setRenrakusakinon1(dto.getRenrakusakinon11()); // 連絡先番号１
        e.setRenrakusakimemon1(dto.getRenrakusakimemon11()); // 連絡先メモ１
        e.setRenrakusakinokbnn2(dto.getRenrakusakinokbnn21()); // 連絡先番号区分２
        e.setRenrakusakinon2(dto.getRenrakusakinon21()); // 連絡先番号２
        e.setRenrakusakimemon2(dto.getRenrakusakimemon21()); // 連絡先メモ２
        e.setRenrakusakinokbnn3(dto.getRenrakusakinokbnn31()); // 連絡先番号区分３
        e.setRenrakusakinon3(dto.getRenrakusakinon31()); // 連絡先番号３
        e.setRenrakusakimemon3(dto.getRenrakusakimemon31()); // 連絡先メモ３
        e.setRenrakusakinokbnn4(dto.getRenrakusakinokbnn41()); // 連絡先番号区分４
        e.setRenrakusakinon4(dto.getRenrakusakinon41()); // 連絡先番号４
        e.setRenrakusakimemon4(dto.getRenrakusakimemon41()); // 連絡先メモ４
        e.setEigyoareacd(dto.getEigyoareacd1()); // 営業エリアコード
        e.setEigyotnshacd(dto.getEigyotnshacd1()); // 営業担当者コード
        e.setDlvrykankatsuapkihoncd(dto.getDlvrykankatsuapkihoncd1()); // 配達管轄ＡＰ基本コード
        e.setDlvrykankatsushopcd(dto.getDlvrykankatsushopcd1()); // 配達管轄ショップコード
        e.setDlvrykankatsukyotencd(dto.getDlvrykankatsukyotencd1()); // 配達管轄拠点コード
        e.setDlvrycmpnycd(dto.getDlvrycmpnycd1()); // 配達会社コード
        e.setDlvryjun(dto.getDlvryjun1()); // 配達順
        e.setDlvryareacd(dto.getDlvryareacd1()); // 配達エリアコード
        e.setDlvrykbn(dto.getDlvrykbn1()); // 配達区分
        e.setDlvrytnshacd(dto.getDlvrytnshacd1()); // 配達担当者コード
        e.setNohinsakijohon1(dto.getNohinsakijohon11()); // 納品先情報１
        e.setNohinsakijohon2(dto.getNohinsakijohon21()); // 納品先情報２
        e.setChizuno(dto.getChizuno1()); // 地図番号
        e.setAcptjikanshiteijikoku(dto.getAcptjikanshiteijikoku1()); // 受注時間指定時刻
        e.setAcptjikanshiteijikokushry(dto.getAcptjikanshiteijikokushry1()); // 受注時間指定時刻終了
        e.setAcptjikanshiteikbn(dto.getAcptjikanshiteikbn1()); // 受注時間指定区分
        e.setPrntmemon1(dto.getPrntmemon11()); // 印刷メモ１
        e.setPrntmemon2(dto.getPrntmemon21()); // 印刷メモ２
        e.setPrntmemon3(dto.getPrntmemon31()); // 印刷メモ３
        e.setPrcssumiflg(CommonConstants.PRCSSUMIFLG_0); // 処理済フラグ
        e.setYukokbn(CommonConstants.YUKOKBN_1); // 有効区分

        // 新規登録時
        if (insertFlg) {
            e.setToshaapkihoncd(dto.getLoginUserApkihoncd()); // 登録者ＡＰ基本コード
            e.setToshashopcd(dto.getLoginUserShopcd()); // 登録者ショップコード
            e.setToshacd(dto.getLoginUserTnshacd()); // 登録者コード
            e.setInptsystmymd(dto.getSystemDate()); // 登録システム日付
            e.setInptsystmjikan(dto.getSystemTime()); // 登録システム時間
            e.setInptunyoymd(dto.getUnyoDate()); // 登録運用日付
            e.setInptipadrse(dto.getIpAddress()); // 登録ＩＰアドレス
            e.setInptpgmid(dto.getProgramId()); // 登録プログラムＩＤ
        }

        e.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        e.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        e.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        e.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        e.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        e.setKshnunyoymd(dto.getUnyoDate()); // 更新運用日付
        e.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        e.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return e;
    }

    /**
     * 得意先予約マスタ論理削除用Entityを生成する
     * 
     * @param dto 登録更新用DTO
     * @return S02M084TEntity
     */
    private S02M084TEntity createTokuisakiYoyakuDeleteEntity(B0501W02UpdateDto dto) {

        S02M084TEntity e = new S02M084TEntity();

        e.setApkihoncd(dto.getApkihoncd()); // ＡＰ基本コード
        e.setShopcd(dto.getShopcd()); // ショップコード
        e.setTokuisakicd(dto.getTokuisakicd()); // 得意先コード
        e.setYoyakuinptdd(dto.getYoyakuinptdd1()); // 予約登録日
        e.setYukokbn(CommonConstants.YUKOKBN_0); // 有効区分
        e.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        e.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        e.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        e.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        e.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        e.setKshnunyoymd(dto.getUnyoDate()); // 更新運用日付
        e.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        e.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return e;
    }

    /**
     * 請求先予約マスタ登録・更新処理
     * 
     * @param dto 更新値格納Dto
     */
    private void updateSkysakiYoyaku(B0501W02UpdateDto dto) {

        // 請求先マスタ変更が「0：チェックOFF」、かつ請求先予約登録日(hidden)が未設定の場合、処理しない
        if (StringUtils.isEmpty(dto.getSeikyusakimsthnkflg())
            && StringUtils.isEmpty(dto.getYoyakuinptdd2())) {
            return;
        }

        try {
            // 検索条件の設定
            S02M085TEntityKey key = new S02M085TEntityKey();
            key.setApkihoncd(dto.getApkihoncd());
            key.setShopcd(dto.getShopcd());
            key.setSkysakicd(dto.getSkysakicd());
            key.setYoyakuinptdd(
                !StringUtils.isEmpty(dto.getYoyakuinptdd2()) ? dto.getYoyakuinptdd2()
                    : dto.getSystemDate());

            // 請求先予約マスタを取得する
            S02M085TEntity eintity = s02m085tDao.selectByPrimaryKey(key);

            // 抽出結果なしの場合
            if (ObjectUtils.isEmpty(eintity)) {
                // 請求先予約マスタを登録する
                s02m085tDao.insertSelective(createSkysakiYoyakuEntity(dto, null));

                // 請求先マスタ変更が「0：チェックOFF」、かつ請求先予約登録日(hidden)が設定ありの場合
            } else if (StringUtils.isEmpty(dto.getSeikyusakimsthnkflg())
                && !StringUtils.isEmpty(dto.getYoyakuinptdd2())) {
                // 請求先予約マスタを論理削除する
                s02m085tDao.updateByPrimaryKeySelective(createSkysakiYoyakuDeleteEntity(dto));

                // 上記以外の場合
            } else {
                // 請求先予約マスタを更新する
                s02m085tDao.updateByPrimaryKey(createSkysakiYoyakuEntity(dto, eintity));
            }
        } catch (DuplicateKeyException e) {
            // 一意制約違反の場合、エラー
            throw BusinessExceptionFactory.validationFailed(e, MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.E002);
        }
    }

    /**
     * 請求先予約マスタ登録・更新用Entityを生成する
     * 
     * @param dto 登録更新用DTO
     * @param src 請求先予約マスタエンティティ(新規登録時はnull)
     * @return S02M085TEntity
     */
    private S02M085TEntity createSkysakiYoyakuEntity(B0501W02UpdateDto dto, S02M085TEntity src) {

        boolean insertFlg = ObjectUtils.isEmpty(src) ? true : false;
        S02M085TEntity e = insertFlg ? new S02M085TEntity() : src;

        // 新規登録時
        if (insertFlg) {
            e.setApkihoncd(dto.getApkihoncd()); // ＡＰ基本コード
            e.setShopcd(dto.getShopcd()); // ショップコード
            e.setSkysakicd(dto.getSkysakicd()); // 請求先コード
            e.setYoyakuinptdd(dto.getSystemDate()); // 予約登録日
        }

        e.setSksmasutachgdd(dto.getSksmasutachgdd()); // 請求先マスタ変更日
        e.setJigyoshocd(dto.getJigyoshocd2()); // 事業所コード
        e.setSkysakinmn1(dto.getSkysakinmn1()); // 請求先名称１
        e.setSkysakinmn2(dto.getSkysakinmn2()); // 請求先名称２
        e.setSkysakikeishokbn(dto.getSkysakikeishokbn()); // 請求先敬称区分
        e.setSkysakinmkana(dto.getSkysakinmkana()); // 請求先名称カナ
        e.setSkysakinmryakusho(dto.getSkysakinmryakusho()); // 請求先名称略称
        e.setYubinno(dto.getYubinno2()); // 郵便番号
        e.setAdrsn1(dto.getAdrsn12()); // 住所１
        e.setAdrsn2(dto.getAdrsn22()); // 住所２
        e.setAdrsn3(dto.getAdrsn32()); // 住所３
        e.setRenrakusakinokbnn1(dto.getRenrakusakinokbnn12()); // 連絡先番号区分１
        e.setRenrakusakinon1(dto.getRenrakusakinon12()); // 連絡先番号１
        e.setRenrakusakinokbnn2(dto.getRenrakusakinokbnn22()); // 連絡先番号区分２
        e.setRenrakusakinon2(dto.getRenrakusakinon22()); // 連絡先番号２
        e.setRenrakusakinokbnn3(dto.getRenrakusakinokbnn32()); // 連絡先番号区分３
        e.setRenrakusakinon3(dto.getRenrakusakinon32()); // 連絡先番号３
        e.setRenrakusakinokbnn4(dto.getRenrakusakinokbnn42()); // 連絡先番号区分４
        e.setRenrakusakinon4(dto.getRenrakusakinon42()); // 連絡先番号４
        e.setEigyotnshacd(dto.getEigyotnshacd2()); // 営業担当者コード
        e.setKaishutnshacd(dto.getKaishutnshacd()); // 回収担当者コード

        // 郵便番号バーコード取得
        String address =
            dto.getAdrsn12() + (StringUtils.isEmpty(dto.getAdrsn22()) ? "" : dto.getAdrsn22())
                + (StringUtils.isEmpty(dto.getAdrsn32()) ? "" : dto.getAdrsn32());
        String yubinnobakodo =
            customerBarCodeService.makeBarCodeCharacter(dto.getYubinno2(), address);

        e.setYubinnobakodo(yubinnobakodo); // 郵便番号バーコード
        e.setPrcssumiflg(CommonConstants.PRCSSUMIFLG_0); // 処理済フラグ
        e.setYukokbn(CommonConstants.YUKOKBN_1); // 有効区分

        // 新規登録時
        if (insertFlg) {
            e.setToshaapkihoncd(dto.getLoginUserApkihoncd()); // 登録者ＡＰ基本コード
            e.setToshashopcd(dto.getLoginUserShopcd()); // 登録者ショップコード
            e.setToshacd(dto.getLoginUserTnshacd()); // 登録者コード
            e.setInptsystmymd(dto.getSystemDate()); // 登録システム日付
            e.setInptsystmjikan(dto.getSystemTime()); // 登録システム時間
            e.setInptunyoymd(dto.getUnyoDate()); // 登録運用日付
            e.setInptipadrse(dto.getIpAddress()); // 登録ＩＰアドレス
            e.setInptpgmid(dto.getProgramId()); // 登録プログラムＩＤ
        }

        e.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        e.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        e.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        e.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        e.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        e.setKshnunyoymd(dto.getUnyoDate()); // 更新運用日付
        e.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        e.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return e;
    }

    /**
     * 請求先予約マスタ論理削除用Entityを生成する
     * 
     * @param dto 登録更新用DTO
     * @return S02M085TEntity
     */
    private S02M085TEntity createSkysakiYoyakuDeleteEntity(B0501W02UpdateDto dto) {

        S02M085TEntity e = new S02M085TEntity();

        e.setApkihoncd(dto.getApkihoncd()); // ＡＰ基本コード
        e.setShopcd(dto.getShopcd()); // ショップコード
        e.setSkysakicd(dto.getSkysakicd()); // 請求先コード
        e.setYoyakuinptdd(dto.getYoyakuinptdd2()); // 予約登録日
        e.setYukokbn(CommonConstants.YUKOKBN_0); // 有効区分
        e.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        e.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        e.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        e.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        e.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        e.setKshnunyoymd(dto.getUnyoDate()); // 更新運用日付
        e.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        e.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return e;
    }

    /**
     * 定例売上仕入マスタ登録・更新処理
     * 
     * @param dto 更新値格納Dto
     */
    private void updateTeireiUriage(B0501W02UpdateDto dto) {
        try {
            // 定例発生番号(hidden)に値がない場合
            if (StringUtils.isEmpty(dto.getTeireihasseino())) {
                // 定例発生番号を採番する
                String teireihasseino =
                    saibanService.getNoTeirei(dto.getApkihoncd(), dto.getShopcd());
                dto.setTeireihasseino(teireihasseino); // 定例発生番号

                // 定例売上仕入マスタを登録する
                s02m016tDao.insertSelective(createTeireiUriageEntity(dto, null));

                // 定例発生番号(hidden)に値がある場合
            } else {
                // 検索条件の設定
                S02M016TEntityKey key = new S02M016TEntityKey();
                key.setApkihoncd(dto.getApkihoncd());
                key.setShopcd(dto.getShopcd());
                key.setUrisiirekbn(CommonConstants.URISIIREKBN_1);
                key.setTeireihasseino(dto.getTeireihasseino());

                S02M016TEntity entity = s02m016tDao.selectByPrimaryKey(key);

                // 定例売上仕入マスタを更新する
                s02m016tDao.updateByPrimaryKey(createTeireiUriageEntity(dto, entity));
            }
        } catch (DuplicateKeyException e) {
            // 一意制約違反の場合、エラー
            throw BusinessExceptionFactory.validationFailed(e, MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.E002);
        }
    }

    /**
     * 定例売上仕入マスタ登録用Entityを生成する
     * 
     * @param dto 登録更新用DTO
     * @param src 定例売上仕入マスタエンティティ(新規登録時はnull)
     * @return S02M016TEntity
     */
    private S02M016TEntity createTeireiUriageEntity(B0501W02UpdateDto dto, S02M016TEntity src) {

        boolean insertFlg = ObjectUtils.isEmpty(src) ? true : false;
        S02M016TEntity e = insertFlg ? new S02M016TEntity() : src;

        // 新規登録時
        if (insertFlg) {
            e.setApkihoncd(dto.getApkihoncd()); // ＡＰ基本コード
            e.setShopcd(dto.getShopcd()); // ショップコード
            e.setUrisiirekbn(CommonConstants.TEIREIKBN_1); // 売上仕入区分
            e.setTeireihasseino(dto.getTeireihasseino()); // 定例発生番号
        }

        e.setDnpykbn(CommonConstants.KBN_TOKUISAKI_KEIHI.equals(dto.getTokuisakikbn())
            ? CommonConstants.KBN_KEIHI_NOMI
            : CommonConstants.KBN_URIAGE_NOMI); // 伝票区分
        e.setHasseikankakukeijoptn(dto.getHasseikankakukeijoptn()); // 発生間隔計上パターン
        e.setTorihikikbn(CommonConstants.KBN_TOKUISAKI_KEIHI.equals(dto.getTokuisakikbn())
            ? CommonConstants.KBN_TORIHIKI_KEIHI
            : CommonConstants.KBN_TORIHIKI_URIAGE); // 取引区分
        e.setShncd(dto.getShncd()); // 商品コード
        e.setTokuisakicd(dto.getTokuisakicd()); // 得意先コード
        e.setNyukamotocd(null); // 入荷元コード
        e.setSuryo(!StringUtils.isEmpty(dto.getSuryo()) ? new BigDecimal(dto.getSuryo()) : null); // 数量
        e.setKeijokbn(CommonConstants.KBN_TOKUISAKI_KEIHI.equals(dto.getTokuisakikbn())
            ? CommonConstants.KBN_KEIJO_KEIHI
            : CommonConstants.KBN_KEIJO_URIAGE); // 計上区分
        e.setHyojunprice(
            !StringUtils.isEmpty(dto.getHyojunprice()) ? new BigDecimal(dto.getHyojunprice())
                : null); // 標準価格
        e.setShokaiunt(
            !StringUtils.isEmpty(dto.getShokaiunt()) ? new BigDecimal(dto.getShokaiunt()) : null); // 初回単価
        e.setShokaikin(
            !StringUtils.isEmpty(dto.getShokaiunt()) && !StringUtils.isEmpty(dto.getSuryo())
                ? new BigDecimal(dto.getShokaiunt()).longValue()
                    * new BigDecimal(dto.getSuryo()).longValue()
                : null); // 初回金額

        long shohitaxkin = 0; // 消費税金額
        long shokaishohitaxkin = 0; // 初回消費税金額

        // 商品コード、数量、単価が入力ありの場合
        if (!StringUtils.isEmpty(dto.getShncd()) && !StringUtils.isEmpty(dto.getSuryo())
            && !StringUtils.isEmpty(dto.getHyojunprice())) {
            // 調整用商品コードを取得
            String cyoseishncd = s02m006tService.searchForMoji(CommonConstants.APCD_000,
                CommonConstants.SYS_SHOPCD_000, CommonConstants.SYS_SETTEINO_139,
                CommonConstants.SETTEICD_139_1);

            // 調整用商品コード ≠ 商品コードの場合
            if (!dto.getShncd().equals(cyoseishncd)) {
                S02M046TEntity s02m046tEntity = getShopMst(dto.getApkihoncd(), dto.getShopcd());

                // 商品マスタビューの取得
                S02V001TEntityKey key = new S02V001TEntityKey();
                key.setApkihoncd(dto.getApkihoncd());
                key.setShopcd(dto.getShopcd());
                key.setShncd(dto.getShncd());
                S02V001TEntity shmv = s02v001tDao.selectByPrimaryKey(key);

                // 税区分の設定
                String taxkbn = CommonConstants.KBN_KEIJO_KEIHI.equals(e.getKeijokbn())
                    ? CommonConstants.KBN_TAX_HIKAZEI
                    : ItemUtil.toBoolean(shmv.getTaxsetteikbn()) ? shmv.getTaxkbn() : null;

                // 消費税の計算
                String[] kinPriceList = taxService.getPriceList(dto.getHyojunprice(),
                    dto.getSuryo(), taxkbn, dto.getTokuisakikbn(),
                    dto.getJikaikeijodydm() + dto.getKeijodd(), s02m046tEntity);

                // 初回消費税の計算
                String[] shokaikinPriceList = taxService.getPriceList(dto.getShokaiunt(),
                    dto.getSuryo(), taxkbn, dto.getTokuisakikbn(),
                    dto.getShokaikeijodydm() + dto.getKeijodd(), s02m046tEntity);

                shohitaxkin = Long.parseLong(kinPriceList[1]);
                shokaishohitaxkin = Long.parseLong(shokaikinPriceList[1]);
            }
        }

        e.setShokaishohitaxkin(shokaishohitaxkin); // 初回消費税金額
        e.setKin(!StringUtils.isEmpty(dto.getHyojunprice()) && !StringUtils.isEmpty(dto.getSuryo())
            ? new BigDecimal(dto.getHyojunprice()).longValue()
                * new BigDecimal(dto.getSuryo()).longValue()
            : null); // 金額
        e.setShohitaxkin(shohitaxkin); // 消費税金額
        e.setKeijodd(dto.getKeijodd()); // 計上日
        e.setShokaikeijodydm(dto.getShokaikeijodydm()); // 初回計上年月
        e.setZnkhasseikeijodd(dto.getZnkhasseikeijodd()); // 前回発生計上日
        e.setJikaikeijodydm(dto.getJikaikeijodydm()); // 次回計上年月
        e.setShrydydm(dto.getShrydydm()); // 終了年月
        e.setKatasiki(null); // 型式
        e.setDnpyhasseiarinashiflg(dto.getDnpyhasseiarinashiflg()); // 伝票発生有無フラグ
        e.setTeireikbn(CommonConstants.TEIREIKBN_1); // 定例区分
        e.setRtlrknshukeikbn(dto.getRtlrknshukeikbn()); // レンタル料金集計区分
        e.setNoshootptkbn(CommonConstants.NOSHOOTPTKBN_0); // 納品書出力区分
        e.setAcpttorikomisumiflg(CommonConstants.ACPTTORIKOMISUMIFLG_0); // 受注取込済フラグ
        e.setMeisaibiko(dto.getMeisaibiko()); // 明細備考
        e.setJissekitnshacd(dto.getJissekitnshacd()); // 実績担当者コード
        e.setYukokbn(dto.getYukokbn1()); // 有効区分

        // 新規登録時
        if (insertFlg) {
            e.setToshaapkihoncd(dto.getLoginUserApkihoncd()); // 登録者ＡＰ基本コード
            e.setToshashopcd(dto.getLoginUserShopcd()); // 登録者ショップコード
            e.setToshacd(dto.getLoginUserTnshacd()); // 登録者コード
            e.setInptsystmymd(dto.getSystemDate()); // 登録システム日付
            e.setInptsystmjikan(dto.getSystemTime()); // 登録システム時間
            e.setInptunyoymd(dto.getUnyoDate()); // 登録運用日付
            e.setInptipadrse(dto.getIpAddress()); // 登録ＩＰアドレス
            e.setInptpgmid(dto.getProgramId()); // 登録プログラムＩＤ
        }

        e.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        e.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        e.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        e.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        e.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        e.setKshnunyoymd(dto.getUnyoDate()); // 更新運用日付
        e.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        e.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return e;
    }

    /**
     * 得意先違約金管理テーブル登録処理
     * 
     * @param dto 更新値格納Dto
     */
    private void insertIyakukinKanri(B0501W02UpdateDto dto) {
        try {
            // 初期表示.入会日が入力なし、入会日が入力あり、かつプラン＝000(基本プラン)の場合
            if (StringUtils.isEmpty(dto.getNyukaiddbefore())
                && !StringUtils.isEmpty(dto.getNyukaidd())
                && CommonConstants.PLNCD_000.equals(dto.getPlncd())) {
                // 検索条件の設定
                S03T003TEntityExample s03t003tExample = new S03T003TEntityExample();
                s03t003tExample.createCriteria().andApkihoncdEqualTo(dto.getApkihoncd())
                    .andShopcdEqualTo(dto.getShopcd()).andTokuisakicdEqualTo(dto.getTokuisakicd())
                    .andRknplncdEqualTo(CommonConstants.SETTEICD_153_01)
                    .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

                // 得意先違約金管理テーブルの件数取得
                long cnt = s03t003tDao.countByExample(s03t003tExample);

                // 抽出結果＝0件(基本プラン違約金情報未設定)の場合
                if (cnt == 0) {
                    // 得意先違約金管理テーブルを登録する
                    s03t003tDao.insertSelective(
                        createIyakukinKanriInsertEntity(dto, CommonConstants.SETTEICD_153_01,
                            dto.getNyukaidd().substring(0, 6), CommonConstants.SETTEICD_153_01));
                }

                // プラン＝000(基本プラン)以外かつ、
                // (プラン初回設定日＝入力値ありかつ、違約金(プラン初回設定日)＝チェックONの場合) または
                // (プラン継続更新日＝入力値ありかつ、違約金(プラン継続更新日)＝チェックONの場合)
            } else if (!CommonConstants.PLNCD_000.equals(dto.getPlncd())
                && (!StringUtils.isEmpty(dto.getRknplnsetteidd())
                    && !StringUtils.isEmpty(dto.getPlnshokaiiyakukinflg()))
                || (!StringUtils.isEmpty(dto.getRknplnkeizokukshndd())
                    && !StringUtils.isEmpty(dto.getPlnkshniyakukinflg()))) {
                // 特別単価料金プラン対応の取得
                String rknplncd = s02m006tService.searchForMoji(CommonConstants.APCD_000,
                    CommonConstants.SYS_SHOPCD_000, CommonConstants.SYS_SETTEINO_154,
                    dto.getPlncd());

                // 特別単価料金プラン対応が取得できた場合
                if (!StringUtils.isEmpty(rknplncd)) {
                    // プラン初回設定日＝入力値ありかつ、違約金(プラン初回設定日)＝チェックONの場合
                    if (!StringUtils.isEmpty(dto.getRknplnsetteidd())
                        && !StringUtils.isEmpty(dto.getPlnshokaiiyakukinflg())) {
                        // 得意先違約金管理テーブルを登録する
                        s03t003tDao.insertSelective(createIyakukinKanriInsertEntity(dto, rknplncd,
                            dto.getRknplnsetteidd().substring(0, 6), rknplncd));
                    }

                    // プラン継続更新日＝入力値ありかつ、違約金(プラン継続更新日)＝チェックONの場合
                    if (!StringUtils.isEmpty(dto.getRknplnkeizokukshndd())
                        && !StringUtils.isEmpty(dto.getPlnkshniyakukinflg())) {
                        // 得意先違約金管理テーブルを登録する
                        s03t003tDao.insertSelective(createIyakukinKanriInsertEntity(dto, rknplncd,
                            dto.getRknplnkeizokukshndd().substring(0, 6), rknplncd));
                    }
                }
            }
        } catch (DuplicateKeyException e) {
            // 一意制約違反の場合、エラー
            throw BusinessExceptionFactory.validationFailed(e, MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.E002);
        }
    }

    /**
     * 得意先違約金管理テーブル登録用Entityを生成する
     * 
     * @param dto 登録更新用DTO
     * @param src 得意先違約金管理エンティティ(新規登録時はnull)
     * @return S03T003TEntity
     */
    private S03T003TEntity createIyakukinKanriInsertEntity(B0501W02UpdateDto dto, String rknplncd,
        String yyyymm, String setteicd) {

        S03T003TEntity e = new S03T003TEntity();

        // 検索条件の設定
        S03T003TEntityExample s03t003tExample = new S03T003TEntityExample();
        s03t003tExample.createCriteria().andApkihoncdEqualTo(dto.getApkihoncd())
            .andShopcdEqualTo(dto.getShopcd()).andTokuisakicdEqualTo(dto.getTokuisakicd());
        s03t003tExample.setOrderByClause("IYAKUKINKANRINO DESC");

        // 違約金管理番号最大値の取得
        List<S03T003TEntity> entityList = s03t003tDao.selectByExample(s03t003tExample);

        int iyakukinkanrino = 1; // 違約金管理番号
        if (!ObjectUtils.isEmpty(entityList) && entityList.size() > 0) {
            iyakukinkanrino = entityList.get(0).getIyakukinkanrino() + 1;
        }

        // エンティティのインスタンス生成
        S02M006TEntityExample entityExample = new S02M006TEntityExample();

        // クライテリア(SQL条件)の生成
        Criteria criteria = entityExample.createCriteria();
        criteria.andApkihoncdEqualTo(CommonConstants.APCD_000)
            .andShopcdEqualTo(CommonConstants.SYS_SHOPCD_000)
            .andSetteinoEqualTo(CommonConstants.SYS_SETTEINO_155)
            .andRcrdkbnEqualTo(CommonConstants.RECORD_KBN_M)
            .andOyasetteinoEqualTo(CommonConstants.SYS_SETTEINO_153)
            .andOyasetteicdEqualTo(setteicd);

        // SELECT実行
        List<S02M006TEntity> entities = s02m006tDao.selectByExample(entityExample);

        // 違約金適用期間の取得
        String iyakukintekiyodkikan = "";
        if (!ObjectUtils.isEmpty(entities)
            && !ObjectUtils.isEmpty(entities.get(0).getSuchisetteiatai())) {
            iyakukintekiyodkikan = entities.get(0).getSuchisetteiatai().toString();
        }

        // エンティティのインスタンス生成
        entityExample = new S02M006TEntityExample();

        // クライテリア(SQL条件)の生成
        criteria = entityExample.createCriteria();
        criteria.andApkihoncdEqualTo(CommonConstants.APCD_000)
            .andShopcdEqualTo(CommonConstants.SYS_SHOPCD_000)
            .andSetteinoEqualTo(CommonConstants.SYS_SETTEINO_156)
            .andRcrdkbnEqualTo(CommonConstants.RECORD_KBN_M)
            .andOyasetteinoEqualTo(CommonConstants.SYS_SETTEINO_153)
            .andOyasetteicdEqualTo(setteicd);

        // SELECT実行
        entities = s02m006tDao.selectByExample(entityExample);

        // 違約金金額の取得
        String iyakukinkin = "";
        if (!ObjectUtils.isEmpty(entities)
            && !ObjectUtils.isEmpty(entities.get(0).getSuchisetteiatai())) {
            iyakukinkin = entities.get(0).getSuchisetteiatai().toString();
        }

        // 違約金適用終了年月を算出する
        String iyakukintekiyoshrydydm = !StringUtils.isEmpty(iyakukintekiyodkikan)
            ? DateTimeUtil.calcYM(yyyymm, Integer.parseInt(iyakukintekiyodkikan) - 1)
            : null;

        e.setApkihoncd(dto.getApkihoncd()); // ＡＰ基本コード
        e.setShopcd(dto.getShopcd()); // ショップコード
        e.setTokuisakicd(dto.getTokuisakicd()); // 得意先コード
        e.setIyakukinkanrino(iyakukinkanrino); // 違約金管理番号
        e.setIyakukinkind(CommonConstants.IYAKUKINKIND_RKNPLN); // 違約金種別
        e.setRknplncd(rknplncd); // 料金プランコード
        e.setWsvkishucd(null); // サーバー機種コード
        e.setIyakukintekiyostrtdydm(yyyymm); // 違約金適用開始年月
        e.setIyakukintekiyoshrydydm(iyakukintekiyoshrydydm); // 違約金適用終了年月
        e.setIyakukintekiyodkikan(
            !StringUtils.isEmpty(iyakukintekiyodkikan) ? Short.parseShort(iyakukintekiyodkikan)
                : null); // 違約金適用期間
        e.setIyakukinkin(!StringUtils.isEmpty(iyakukinkin) ? Long.parseLong(iyakukinkin) : null); // 違約金金額
        e.setYukokbn(CommonConstants.YUKOKBN_1); // 有効区分
        e.setToshaapkihoncd(dto.getLoginUserApkihoncd()); // 登録者ＡＰ基本コード
        e.setToshashopcd(dto.getLoginUserShopcd()); // 登録者ショップコード
        e.setToshacd(dto.getLoginUserTnshacd()); // 登録者コード
        e.setInptsystmymd(dto.getSystemDate()); // 登録システム日付
        e.setInptsystmjikan(dto.getSystemTime()); // 登録システム時間
        e.setInptunyoymd(dto.getUnyoDate()); // 登録運用日付
        e.setInptipadrse(dto.getIpAddress()); // 登録ＩＰアドレス
        e.setInptpgmid(dto.getProgramId()); // 登録プログラムＩＤ
        e.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        e.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        e.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        e.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        e.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        e.setKshnunyoymd(dto.getUnyoDate()); // 更新運用日付
        e.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        e.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return e;
    }

    /**
     * 得意先違約金管理テーブル更新処理
     * 
     * @param dto 更新値格納Dto
     */
    private void updateIyakukinKanri(B0501W02UpdateDto dto) {
        try {
            // 得意先を取得
            S02M013TEntity tokuisaki =
                getTokuisakiMst(dto.getApkihoncd(), dto.getShopcd(), dto.getTokuisakicd());

            // 違約金管理フラグ = 0(管理しない)以外の場合
            if (!ObjectUtils.isEmpty(tokuisaki)
                && !CommonConstants.IYAKUKINKANRIFLG_0.equals(tokuisaki.getIyakukinkanriflg())) {
                // 更新条件の設定
                S03T003TEntityExample example = new S03T003TEntityExample();
                example.createCriteria().andApkihoncdEqualTo(dto.getApkihoncd())
                    .andShopcdEqualTo(dto.getShopcd()).andTokuisakicdEqualTo(dto.getTokuisakicd());

                // 得意先違約金管理テーブル更新
                s03t003tDao.updateByExampleSelective(createIyakukinKanriUpdateEntity(dto), example);
            }
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.E002);
        }
    }

    /**
     * 得意先違約金管理テーブル更新用Entityを生成する
     * 
     * @param dto 登録更新用DTO
     * @return S03T003TEntity
     */
    private S03T003TEntity createIyakukinKanriUpdateEntity(B0501W02UpdateDto dto) {

        S03T003TEntity e = new S03T003TEntity();

        e.setYukokbn(CommonConstants.YUKOKBN_0); // 有効区分
        e.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        e.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        e.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        e.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        e.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        e.setKshnunyoymd(dto.getUnyoDate()); // 更新運用日付
        e.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        e.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return e;
    }

    /**
     * 請求先マスタ登録・更新処理
     * 
     * @param dto 更新値格納Dto
     */
    private void updateSkysakiMst(B0501W02UpdateDto dto) {
        try {
            // 画面.請求先コードに入力値がない場合
            if (StringUtils.isEmpty(dto.getSkysakicd())) {
                // 請求設定区分が「2：自動生成」の場合
                if (SKYSETTEIKBN_2.equals(dto.getSkysetteikbn())) {
                    // 請求先マスタを登録する
                    s02m017tDao.insertSelective(createSkysakiInsertEntity(dto));
                }

                // 再入会が「1：チェックON」、かつ反映元得意先コードが設定値ありの場合
                if (!StringUtils.isEmpty(dto.getFttbnyukaiflg())
                    && !StringUtils.isEmpty(dto.getHaneimotottcd())) {
                    // 反映元得意先を取得
                    S02M013TEntity motoTokuisaki = getTokuisakiMst(dto.getApkihoncd(),
                        dto.getShopcd(), dto.getHaneimotottcd());

                    // 有効区分＝1(有効)の場合かつ、請求先コード設定値ありの場合
                    if (!ObjectUtils.isEmpty(motoTokuisaki)
                        && CommonConstants.YUKOKBN_1.equals(motoTokuisaki.getYukokbn())
                        && !StringUtils.isEmpty(motoTokuisaki.getSkysakicd())) {
                        // 反映元請求先を取得
                        S02M017TEntity motoSkysaki = getSkysakiMst(motoTokuisaki.getApkihoncd(),
                            motoTokuisaki.getShopcd(), motoTokuisaki.getSkysakicd());

                        // 有効区分＝1(有効)の場合
                        if (!ObjectUtils.isEmpty(motoSkysaki)
                            && CommonConstants.YUKOKBN_1.equals(motoSkysaki.getYukokbn())) {
                            // 更新対象の請求先が存在する場合
                            if (isExistSkysaki(dto.getApkihoncd(), dto.getShopcd(),
                                dto.getTokuisakicd())) {
                                // 請求先マスタ再入会更新
                                s02m017tDao.updateByPrimaryKeySelective(
                                    createSkysakiUpdateEntityForSainyukai(dto, motoSkysaki));
                            }
                        }
                    }
                }

                // 初期表示.画面.請求先コード＝入力値ありの場合
                if (!StringUtils.isEmpty(dto.getSkysakicdbefore())) {
                    // 初期表示時の請求先マスタの請求区分を更新する
                    updateSkysakikbn(dto, dto.getSkysakicdbefore());
                }

                // 画面.請求先コードに入力値がある場合
            } else {
                // 請求先マスタの請求区分を更新する
                updateSkysakikbn(dto, dto.getSkysakicd());

                // 初期表示.画面.請求先コード＝入力値あり かつ請求先コードが変更されている場合
                if (!StringUtils.isEmpty(dto.getSkysakicdbefore())
                    && !dto.getSkysakicd().equals(dto.getSkysakicdbefore())) {
                    // 初期表示時の請求先マスタの請求区分を更新する
                    updateSkysakikbn(dto, dto.getSkysakicdbefore());
                }

                // 選択ボタンからの遷移の場合
                if (!StringUtils.isEmpty(dto.getTokuisakicdbefore())) {
                    // 更新対象の請求先が存在する場合
                    if (isExistSkysaki(dto.getApkihoncd(), dto.getShopcd(), dto.getSkysakicd())) {
                        // 請求先マスタ請求先管理区分更新
                        s02m017tDao.updateByPrimaryKeySelective(
                            createSkysakiUpdateEntityForSkysakikanrikbn(dto));
                    }
                }
            }
        } catch (DuplicateKeyException e) {
            // 一意制約違反の場合、エラー
            throw BusinessExceptionFactory.validationFailed(e, MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.E002);
        }
    }

    /**
     * 請求先マスタ存在チェック
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param skysakicd 請求先コード
     * @return true: 存在する / false: 存在しない
     */
    private boolean isExistSkysaki(String apkihoncd, String shopcd, String skysakicd) {
        // 検索条件の設定
        S02M017TEntityExample s02m017tExample = new S02M017TEntityExample();
        s02m017tExample.createCriteria().andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd)
            .andSkysakicdEqualTo(skysakicd);

        // 更新対象の請求先の存在チェック
        return s02m017tDao.countByExample(s02m017tExample) > 0 ? true : false;
    }

    /**
     * 請求先マスタ請求先区分更新処理
     * 
     * @param dto 更新値格納Dto
     * @param skysakicd 請求先コード
     */
    private void updateSkysakikbn(B0501W02UpdateDto dto, String skysakicd) {
        try {
            // 更新対象の請求先を取得
            S02M017TEntity skysaki = getSkysakiMst(dto.getApkihoncd(), dto.getShopcd(), skysakicd);

            if (!ObjectUtils.isEmpty(skysaki)) {
                // 得意先マスタの件数を取得する
                S02M013TEntityExample s02m013tExample = new S02M013TEntityExample();
                s02m013tExample.createCriteria().andApkihoncdEqualTo(dto.getApkihoncd())
                    .andShopcdEqualTo(dto.getShopcd()).andSkysakicdEqualTo(skysakicd);

                long cnt = s02m013tDao.countByExample(s02m013tExample);
                if (cnt > 0) {
                    // 請求先マスタ.請求区分＝2(親子)かつ 得意先マスタが1件の場合
                    if (CommonConstants.SKYKBN_2.equals(skysaki.getSkykbn()) && cnt == 1) {
                        // 請求先マスタの請求区分を1(一般)に更新
                        s02m017tDao.updateByPrimaryKeySelective(createSkysakiUpdateEntityForSkykbn(
                            dto, skysakicd, CommonConstants.SKYKBN_1));

                        // 請求先マスタ.請求区分＝1(一般)かつ 得意先マスタが2件以上の場合
                    } else if (CommonConstants.SKYKBN_1.equals(skysaki.getSkykbn()) && cnt > 1) {
                        // 請求先マスタの請求区分を2(親子)に更新
                        s02m017tDao.updateByPrimaryKeySelective(createSkysakiUpdateEntityForSkykbn(
                            dto, skysakicd, CommonConstants.SKYKBN_2));
                    }
                }
            }
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(re, MessageID.E002);
        }
    }

    /**
     * 請求先マスタ登録用Entityを生成する
     * 
     * @param dto 登録更新用DTO
     * @return S02M017TEntity
     */
    private S02M017TEntity createSkysakiInsertEntity(B0501W02UpdateDto dto) {

        S02M017TEntity e = new S02M017TEntity();

        e.setApkihoncd(dto.getApkihoncd()); // ＡＰ基本コード
        e.setShopcd(dto.getShopcd()); // ショップコード
        e.setSkysakicd(dto.getTokuisakicd()); // 請求先コード
        e.setJigyoshocd(dto.getJigyoshocd()); // 事業所コード
        e.setSkyshohassouitakuumu(CommonConstants.SKYSHOHASSOUITAKUUMU_0); // 請求書発送委託有無
        e.setSkysakikanrikbn(dto.getSkysakikanrikbn()); // 請求先管理区分
        e.setSkykbn(CommonConstants.SKYKBN_1); // 請求区分
        e.setSkysakinmn1(dto.getTokuisakinmn1()); // 請求先名称１
        e.setSkysakinmn2(dto.getTokuisakinmn2()); // 請求先名称２
        e.setSkysakikeishokbn(dto.getTokuisakikeishokbn()); // 請求先敬称区分
        e.setSkysakinmkana(dto.getTokuisakinmkanan1()); // 請求先名称カナ
        e.setSkysakinmryakusho(ItemUtil.substringBytes(dto.getTokuisakinmn1(), 30, "Windows-31J"));// 請求先名称略称
        e.setYubinno(dto.getYubinno()); // 郵便番号
        e.setAdrsn1(dto.getAdrsn1()); // 住所１
        e.setAdrsn2(dto.getAdrsn2()); // 住所２
        e.setAdrsn3(dto.getAdrsn3()); // 住所３
        e.setRenrakusakinokbnn1(dto.getRenrakusakinokbnn1()); // 連絡先番号区分１
        e.setRenrakusakinon1(dto.getRenrakusakinon1()); // 連絡先番号１
        e.setRenrakusakinokbnn2(dto.getRenrakusakinokbnn2()); // 連絡先番号区分２
        e.setRenrakusakinon2(dto.getRenrakusakinon2()); // 連絡先番号２
        e.setRenrakusakinokbnn3(dto.getRenrakusakinokbnn3()); // 連絡先番号区分３
        e.setRenrakusakinon3(dto.getRenrakusakinon3()); // 連絡先番号３
        e.setRenrakusakinokbnn4(dto.getRenrakusakinokbnn4()); // 連絡先番号区分４
        e.setRenrakusakinon4(dto.getRenrakusakinon4()); // 連絡先番号４
        e.setMailadrse(dto.getMailadrse()); // メールアドレス
        e.setEigyotnshacd(dto.getEigyotnshacd()); // 営業担当者コード
        e.setKaishutnshacd(dto.getEigyotnshacd()); // 回収担当者コード
        e.setSkyshimedd(END_DATE); // 請求締日
        e.setShrsaito(CommonConstants.SHRSAITO_2); // 支払サイト
        e.setShrdd(END_DATE); // 支払日

        String kaishuhohokbn = CommonConstants.KBN_TOKUISAKI_KEIHI.equals(dto.getTokuisakikbn())
            ? CommonConstants.KAISHUHOHOKBN_9
            : CommonConstants.JUTAKUDLVRYCSTKBN_2.equals(dto.getJutakudlvrycstkbn())
                ? CommonConstants.KAISHUHOHOKBN_8
                : CommonConstants.KAISHUHOHOKBN_3;

        e.setKaishuhohokbn(kaishuhohokbn); // 回収方法区分
        e.setSkyhohokbn(CommonConstants.SKYHOHOKBN_ZANDAKA); // 請求方法区分
        e.setMeisaiprntkbn(CommonConstants.MEISAIPRNTKBN_ISSHO); // 明細印刷区分
        e.setKsikkntetsuzukiflg(CommonConstants.KSIKKNTETSUZUKIFLG_0); // 決済機関手続中フラグ
        e.setCrdtcrdyukokbn(CommonConstants.YUKOKBN_1); // クレジットカード有効区分
        e.setTairyukeisanshrsaito(CommonConstants.TAIRYUKEISANSHRSAITO_1); // 滞留計算支払サイト
        e.setTairyukbn(CommonConstants.TAIRYUKBN_0); // 滞留区分
        e.setKairekin(LONG_ZERO); // 過入金額
        e.setUrichoseikin(LONG_ZERO); // 売上調整金額

        // 郵便番号バーコード取得
        String address =
            dto.getAdrsn1() + (StringUtils.isEmpty(dto.getAdrsn2()) ? "" : dto.getAdrsn2())
                + (StringUtils.isEmpty(dto.getAdrsn3()) ? "" : dto.getAdrsn3());
        String yubinnobakodo =
            customerBarCodeService.makeBarCodeCharacter(dto.getYubinno(), address);

        e.setYubinnobakodo(yubinnobakodo); // 郵便番号バーコード
        e.setHayadasiflg(CommonConstants.HAYADASIFLG_0); // 早出しフラグ
        e.setSkymeisaisofukbn(CommonConstants.SKYMEISAISOFUKBN_1); // 請求明細送付区分
        e.setAcikeiyuflg(CommonConstants.ACIKEIYUFLG_0); // ACI経由請求フラグ
        e.setOyatokuisakicd(dto.getTokuisakicd()); // 親得意先コード
        e.setYukokbn(CommonConstants.YUKOKBN_1); // 有効区分
        e.setToshaapkihoncd(dto.getLoginUserApkihoncd()); // 登録者ＡＰ基本コード
        e.setToshashopcd(dto.getLoginUserShopcd()); // 登録者ショップコード
        e.setToshacd(dto.getLoginUserTnshacd()); // 登録者コード
        e.setInptsystmymd(dto.getSystemDate()); // 登録システム日付
        e.setInptsystmjikan(dto.getSystemTime()); // 登録システム時間
        e.setInptunyoymd(dto.getUnyoDate()); // 登録運用日付
        e.setInptipadrse(dto.getIpAddress()); // 登録ＩＰアドレス
        e.setInptpgmid(dto.getProgramId()); // 登録プログラムＩＤ
        e.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        e.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        e.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        e.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        e.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        e.setKshnunyoymd(dto.getUnyoDate()); // 更新運用日付
        e.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        e.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return e;
    }

    /**
     * 請求先マスタ再入会更新用Entityを生成する
     * 
     * @param dto 登録更新用DTO
     * @param src 反映元請求先マスタエンティティ
     * @return S02M017TEntity
     */
    private S02M017TEntity createSkysakiUpdateEntityForSainyukai(B0501W02UpdateDto dto,
        S02M017TEntity src) {

        S02M017TEntity e = new S02M017TEntity();

        e.setApkihoncd(dto.getApkihoncd()); // ＡＰ基本コード
        e.setShopcd(dto.getShopcd()); // ショップコード
        e.setSkysakicd(dto.getTokuisakicd()); // 請求先コード
        e.setGaiburenkeicd(src.getGaiburenkeicd()); // 外部連携コード
        e.setSkyshohassouitakuumu(src.getSkyshohassouitakuumu()); // 請求書発送委託有無
        e.setSkybiko(src.getSkybiko()); // 請求備考
        e.setPrntbiko(src.getPrntbiko()); // 印刷備考
        e.setShukindd(src.getShukindd()); // 集金日
        e.setKsikkntetsuzukiflg(src.getKsikkntetsuzukiflg()); // 決済機関手続中フラグ
        e.setKsikknttszkjrhbi(src.getKsikknttszkjrhbi()); // 決済機関手続受領日
        e.setKsikkncd(src.getKsikkncd()); // 決済機関コード
        e.setItakusakirenkeicd(src.getItakusakirenkeicd()); // 委託先連携コード
        e.setKinyukkncd(src.getKinyukkncd()); // 金融機関コード
        e.setKinyukknshitencd(src.getKinyukknshitencd()); // 金融機関支店コード
        e.setKinyukknkzkind(src.getKinyukknkzkind()); // 金融機関口座種別
        e.setKinyukknkzno(src.getKinyukknkzno()); // 金融機関口座番号
        e.setKinyukknnm(src.getKinyukknnm()); // 金融機関名称
        e.setKinyukknshitennm(src.getKinyukknshitennm()); // 金融機関支店名称
        e.setKinyukknkzmeigi(src.getKinyukknkzmeigi()); // 金融機関口座名義
        e.setKinyukknnmkana(src.getKinyukknnmkana()); // 金融機関名称カナ
        e.setKinyukknshitennmkana(src.getKinyukknshitennmkana()); // 金融機関支店名称カナ
        e.setKinyukknkzmeigikana(src.getKinyukknkzmeigikana()); // 金融機関口座名義カナ
        e.setCrdtcrdno(src.getCrdtcrdno()); // クレジット会員ID
        e.setCrdtcrdmeigi(src.getCrdtcrdmeigi()); // クレジット会員備考
        e.setCrdtcrdyukokgn(src.getCrdtcrdyukokgn()); // 登録年月
        e.setCrdtcrdyukokbn(src.getCrdtcrdyukokbn()); // クレジットカード有効区分
        e.setZengincstcd(src.getZengincstcd()); // 全銀顧客コード
        e.setTairyukbn(src.getTairyukbn()); // 滞留区分
        e.setTairyuhasseikeikadm(src.getTairyuhasseikeikadm()); // 滞留発生経過月
        e.setTairyukin(src.getTairyukin()); // 滞留金額
        e.setTairyuhasseikaisu(src.getTairyuhasseikaisu()); // 滞留発生回数
        e.setKairekin(src.getKairekin()); // 過入金額
        e.setUrichoseikin(src.getUrichoseikin()); // 売上調整金額
        e.setHayadasiflg(src.getHayadasiflg()); // 早出しフラグ
        e.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        e.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        e.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        e.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        e.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        e.setKshnunyoymd(dto.getUnyoDate()); // 更新運用日付
        e.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        e.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return e;
    }

    /**
     * 請求先マスタ請求区分更新用Entityを生成する
     * 
     * @param dto 登録更新用DTO
     * @param skysakicd 請求先コード
     * @param skykbn 請求区分
     * @return S02M017TEntity
     */
    private S02M017TEntity createSkysakiUpdateEntityForSkykbn(B0501W02UpdateDto dto,
        String skysakicd, String skykbn) {

        S02M017TEntity e = new S02M017TEntity();

        e.setApkihoncd(dto.getApkihoncd()); // ＡＰ基本コード
        e.setShopcd(dto.getShopcd()); // ショップコード
        e.setSkysakicd(skysakicd); // 請求先コード
        e.setSkykbn(skykbn); // 請求区分
        e.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        e.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        e.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        e.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        e.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        e.setKshnunyoymd(dto.getUnyoDate()); // 更新運用日付
        e.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        e.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return e;
    }

    /**
     * 請求先マスタ請求先管理区分更新用Entityを生成する
     * 
     * @param dto 登録更新用DTO
     * @return S02M017TEntity
     */
    private S02M017TEntity createSkysakiUpdateEntityForSkysakikanrikbn(B0501W02UpdateDto dto) {

        S02M017TEntity e = new S02M017TEntity();

        e.setApkihoncd(dto.getApkihoncd()); // ＡＰ基本コード
        e.setShopcd(dto.getShopcd()); // ショップコード
        e.setSkysakicd(dto.getTokuisakicd()); // 請求先コード
        e.setSkysakikanrikbn(dto.getSkysakikanrikbn()); // 請求先管理区分
        e.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        e.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        e.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        e.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        e.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        e.setKshnunyoymd(dto.getUnyoDate()); // 更新運用日付
        e.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        e.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return e;
    }

    /**
     * 得意先マスタエンティティの登録/更新時のvalue値を設定する。
     * 
     * @param dto 登録更新用DTO
     * @param src 得意先マスタエンティティ(新規登録時はnull)
     * @return S02M013TEntity
     */
    private S02M013TEntity createTokuisakiUpdateEntity(B0501W02UpdateDto dto, S02M013TEntity src) {

        boolean insertFlg = ObjectUtils.isEmpty(src) ? true : false;
        S02M013TEntity e = insertFlg ? new S02M013TEntity() : src;

        // 新規登録時
        if (insertFlg) {
            e.setApkihoncd(dto.getApkihoncd()); // ＡＰ基本コード
            e.setShopcd(dto.getShopcd()); // ショップコード
            e.setTokuisakicd(dto.getTokuisakicd()); // 得意先コード
        }

        // 再入会フラグが「1：チェックON」の場合
        if (!StringUtils.isEmpty(dto.getFttbnyukaiflg())) {
            e.setHaneimotottcd(dto.getHaneimotottcd()); // 反映元得意先コード
        }

        e.setFttbnyukaiflg(
            !StringUtils.isEmpty(dto.getFttbnyukaiflg()) ? CommonConstants.FTTBNYUKAIFLG_1
                : CommonConstants.FTTBNYUKAIFLG_0); // 再入会フラグ
        e.setKaiincd(dto.getKaiincd()); // 会員コード

        // 得意先状態、状態ステータスを設定する
        setTokuisakiJotai(dto, e);
        e.setTokuisakikbn(dto.getTokuisakikbn()); // 得意先区分
        e.setJutakudlvrycstkbn(dto.getJutakudlvrycstkbn()); // 受託配達顧客区分
        e.setRiyowsvkbn(dto.getRiyowsvkbn()); // 利用サーバー区分
        e.setCdkindkbn(dto.getCdkindkbn()); // コード種別区分
        e.setGyoshukbn(dto.getGyoshukbn()); // 業種区分
        e.setGyoshushskbn(dto.getGyoshushskbn()); // 業種詳細区分
        e.setSkysakicd(getSkysakicd(dto)); // 請求先コード
        e.setTokuisakishopcd(dto.getTokuisakishopcd()); // 得意先ショップコード
        e.setItakuaiteapkihoncd(dto.getItakuaiteapkihoncd()); // 委託相手ＡＰ基本コード
        e.setItakuaiteshopcd(dto.getItakuaiteshopcd()); // 委託相手ショップコード
        e.setItakuaitetokuisakicd(dto.getItakuaitetokuisakicd()); // 委託相手得意先コード
        e.setJigyoshocd(dto.getJigyoshocd()); // 事業所コード
        e.setTokuisakinmn1(dto.getTokuisakinmn1()); // 得意先名称１
        e.setTokuisakinmn2(dto.getTokuisakinmn2()); // 得意先名称２
        e.setTokuisakikeishokbn(dto.getTokuisakikeishokbn()); // 得意先敬称区分
        e.setTokuisakinmkanan1(dto.getTokuisakinmkanan1()); // 得意先名称カナ１
        e.setTokuisakinmkanan2(dto.getTokuisakinmkanan2()); // 得意先名称カナ２
        e.setYubinno(dto.getYubinno()); // 郵便番号
        e.setChihoupubdanticdn1(dto.getChihoupubdanticdn1()); // 地方公共団体コード１
        e.setChihoupubdanticdn2(dto.getChihoupubdanticdn2()); // 地方公共団体コード２
        e.setAdrsn1(dto.getAdrsn1()); // 住所１
        e.setAdrsn2(dto.getAdrsn2()); // 住所２
        e.setAdrsn3(dto.getAdrsn3()); // 住所３
        e.setRenrakusakinokbnn1(dto.getRenrakusakinokbnn1()); // 連絡先番号区分１
        e.setRenrakusakinon1(dto.getRenrakusakinon1()); // 連絡先番号１
        e.setRenrakusakimemon1(dto.getRenrakusakimemon1()); // 連絡先メモ１
        e.setRenrakusakinokbnn2(dto.getRenrakusakinokbnn2()); // 連絡先番号区分２
        e.setRenrakusakinon2(dto.getRenrakusakinon2()); // 連絡先番号２
        e.setRenrakusakimemon2(dto.getRenrakusakimemon2()); // 連絡先メモ２
        e.setRenrakusakinokbnn3(dto.getRenrakusakinokbnn3()); // 連絡先番号区分３
        e.setRenrakusakinon3(dto.getRenrakusakinon3()); // 連絡先番号３
        e.setRenrakusakimemon3(dto.getRenrakusakimemon3()); // 連絡先メモ３
        e.setRenrakusakinokbnn4(dto.getRenrakusakinokbnn4()); // 連絡先番号区分４
        e.setRenrakusakinon4(dto.getRenrakusakinon4()); // 連絡先番号４
        e.setRenrakusakimemon4(dto.getRenrakusakimemon4()); // 連絡先メモ４
        e.setClckensakuyotelnon1(!StringUtils.isEmpty(dto.getRenrakusakinon1())
            ? dto.getRenrakusakinon1().replaceAll("-", "")
            : null); // コールセンター検索用電話番号１
        e.setClckensakuyotelnon2(!StringUtils.isEmpty(dto.getRenrakusakinon2())
            ? dto.getRenrakusakinon2().replaceAll("-", "")
            : null); // コールセンター検索用電話番号２
        e.setClckensakuyotelnon3(!StringUtils.isEmpty(dto.getRenrakusakinon3())
            ? dto.getRenrakusakinon3().replaceAll("-", "")
            : null); // コールセンター検索用電話番号３
        e.setClckensakuyotelnon4(!StringUtils.isEmpty(dto.getRenrakusakinon4())
            ? dto.getRenrakusakinon4().replaceAll("-", "")
            : null); // コールセンター検索用電話番号４
        e.setMailadrse(dto.getMailadrse()); // メールアドレス
        e.setMerumagakbn(dto.getMerumagakbn()); // メルマガ区分
        e.setNyukaiinptdd(dto.getNyukaiinptdd()); // 入会登録日
        e.setRiyomokutekikbn(dto.getRiyomokutekikbn()); // 利用目的区分
        e.setNyushadydm(dto.getNyushadydm()); // 入社年月
        e.setSeibetsukbn(dto.getSeibetsukbn()); // 性別区分
        e.setSykugyokbn(dto.getSykugyokbn()); // 職業区分
        e.setHaiguumuflg(dto.getHaiguumuflg()); // 配偶有無フラグ
        e.setSonotadokyoninumuflg(dto.getSonotadokyoninumuflg()); // その他同居人有無フラグ
        e.setShihasyazokukbn(dto.getShihasyazokukbn()); // 支払者続柄区分
        e.setKojinsyoumeikindkbn(dto.getKojinsyoumeikindkbn()); // 個人証明種別区分
        e.setKojinsyoumeino(dto.getKojinsyoumeino()); // 個人証明番号
        e.setKazokukoseikazudansei(!StringUtils.isEmpty(dto.getKazokukoseikazudansei())
            ? Short.parseShort(dto.getKazokukoseikazudansei())
            : null); // 利用者数
        e.setKazokukoseikazujosei(!StringUtils.isEmpty(dto.getKazokukoseikazujosei())
            ? Short.parseShort(dto.getKazokukoseikazujosei())
            : null); // 内子供数
        e.setSetaijohoinptdd(dto.getSetaijohoinptdd()); // 世帯情報登録日
        e.setSetainushi(dto.getSetainushi()); // 世帯主
        e.setJugyoinkibkbn(dto.getJugyoinkibkbn()); // 従業員規模区分
        e.setKojudykazukbn(dto.getKojudykazukbn()); // 居住年数区分
        e.setPetjohokbn(dto.getPetjohokbn()); // ペット情報区分
        e.setSetaisyunyu(
            !StringUtils.isEmpty(dto.getSetaisyunyu()) ? Integer.parseInt(dto.getSetaisyunyu())
                : null); // 世帯収入
        e.setDoitsusekeninzukbn(dto.getDoitsusekeninzukbn()); // 同一生計人数区分
        e.setGaikokujinninzu(!StringUtils.isEmpty(dto.getGaikokujinninzu())
            ? Short.parseShort(dto.getGaikokujinninzu())
            : null); // 外国人人数
        e.setGaikokujinbiko(dto.getGaikokujinbiko()); // 外国人備考
        e.setNyushatanjodd(dto.getNyushatanjodd()); // 入会者誕生日
        e.setKodomomein1(dto.getKodomomein1()); // 子供名１
        e.setKodomotanjoddn1(dto.getKodomotanjoddn1()); // 子供誕生日１
        e.setKodomomein2(dto.getKodomomein2()); // 子供名２
        e.setKodomotanjoddn2(dto.getKodomotanjoddn2()); // 子供誕生日２
        e.setKodomomein3(dto.getKodomomein3()); // 子供名３
        e.setKodomotanjoddn3(dto.getKodomotanjoddn3()); // 子供誕生日３
        e.setMoshikomircptdd(dto.getMoshikomircptdd()); // 申込受付日
        e.setNyukaimokoshono(dto.getNyukaimokoshono()); // 入会申込書番号
        e.setMoshikomicncldd(dto.getMoshikomicncldd()); // 申込キャンセル日

        // 申込受付日が入力された場合
        if (!StringUtils.isEmpty(dto.getMoshikomircptdd())) {
            // 申込受付日が変更された場合
            if (!dto.getMoshikomircptdd().equals(dto.getMoshikomircptddbefore())) {
                e.setMoshikomitoshaapkihoncd(dto.getLoginUserApkihoncd()); // 申込登録者ＡＰ基本コード
                e.setMoshikomitoshashopcd(dto.getLoginUserShopcd()); // 申込登録者ショップコード
                e.setMoshikomitoshacd(dto.getLoginUserTnshacd()); // 申込登録者コード
            }
            // 申込受付日が未入力の場合
        } else {
            e.setMoshikomitoshaapkihoncd(null); // 申込登録者ＡＰ基本コード
            e.setMoshikomitoshashopcd(null); // 申込登録者ショップコード
            e.setMoshikomitoshacd(null); // 申込登録者コード
        }

        e.setNyukaidd(dto.getNyukaidd()); // 入会日
        e.setMoshikomircptkbn(dto.getMoshikomircptkbn()); // 申込受付区分
        e.setKaiinmokoshono(dto.getKaiinmokoshono()); // 会員申込書番号

        // 入会日が入力された場合
        if (!StringUtils.isEmpty(dto.getNyukaidd())) {
            // 入会日が変更された場合
            if (!dto.getNyukaidd().equals(dto.getNyukaiddbefore())) {
                e.setNyukaiddtoshaapkihoncd(dto.getLoginUserApkihoncd()); // 入会日登録者ＡＰ基本コード
                e.setNyukaiddtoshashopcd(dto.getLoginUserShopcd()); // 入会日登録者ショップコード
                e.setNyukaiddtoshacd(dto.getLoginUserTnshacd()); // 入会日登録者コード
            }
            // 入会日が未入力の場合
        } else {
            e.setNyukaiddtoshaapkihoncd(null); // 入会日登録者ＡＰ基本コード
            e.setNyukaiddtoshashopcd(null); // 入会日登録者ショップコード
            e.setNyukaiddtoshacd(null); // 入会日登録者コード
        }

        e.setTaikaidd(dto.getTaikaidd()); // 退会日
        e.setTaikairiyukbn(dto.getTaikairiyukbn()); // 退会理由区分
        e.setTaikairiyushs(dto.getTaikairiyushs()); // 退会理由詳細

        // 退会日が入力された場合
        if (!StringUtils.isEmpty(dto.getTaikaidd())) {
            // 退会日が変更された場合
            if (!dto.getTaikaidd().equals(dto.getTaikaiddbefore())) {
                e.setTaikaiddtoshaapkihoncd(dto.getLoginUserApkihoncd()); // 退会日登録者ＡＰ基本コード
                e.setTaikaiddtoshashopcd(dto.getLoginUserShopcd()); // 退会日登録者ショップコード
                e.setTaikaiddtoshacd(dto.getLoginUserTnshacd()); // 退会日登録者コード
            }
            // 退会日が未入力の場合
        } else {
            e.setTaikaiddtoshaapkihoncd(null); // 退会日登録者ＡＰ基本コード
            e.setTaikaiddtoshashopcd(null); // 退会日登録者ショップコード
            e.setTaikaiddtoshacd(null); // 退会日登録者コード
        }

        e.setEigyoareacd(dto.getEigyoareacd()); // 営業エリアコード

        // 特別単価区分を設定する
        setTokubetsuTankaKbn(dto, e);

        e.setRknkindtokubetsunebikiflg(dto.getRknkindtokubetsunebikiflg()); // 料金種別特別値引フラグ
        e.setRknkindtokubetsuuntflg(dto.getRknkindtokubetsuuntflg()); // 料金種別特別単価フラグ
        e.setRknkindkihonflg(
            CommonConstants.PLNCD_000.equals(dto.getPlncd()) ? CommonConstants.RKNKINDKIHONFLG_1
                : CommonConstants.RKNKINDKIHONFLG_0); // 料金種別基本フラグ
        e.setPlncd(dto.getPlncd()); // プランコード
        e.setRknplnsetteidd(dto.getRknplnsetteidd()); // 料金プラン設定日
        e.setRknplnkeizokukshndd(dto.getRknplnkeizokukshndd()); // 料金プラン継続更新日
        e.setPlnkgndydm(dto.getPlnkgndydm()); // プラン期限年月
        e.setJishasvcd(dto.getJishasvcd()); // 自社サービスコード
        e.setIyakukinkanriflg(dto.getIyakukinkanriflg()); // 違約金管理フラグ
        e.setEigyotnshacd(dto.getEigyotnshacd()); // 営業担当者コード
        e.setUrikeijotnshakbn(dto.getUrikeijotnshakbn()); // 売上計上担当者区分
        e.setKakutokukbn(dto.getKakutokukbn()); // 獲得区分
        e.setRcptkbn(dto.getRcptkbn()); // 受付区分
        e.setBaitaikbn(dto.getBaitaikbn()); // 媒体区分
        e.setKakutokutnshacd(dto.getKakutokutnshacd()); // 獲得担当者コード

        // 新規登録時
        if (insertFlg) {
            // 基本区分が「1：ACI」、かつ画面.本部エージェントコードが設定値ありの場合
            if (CommonConstants.KBN_KIHON_ACI.equals(userSession.getApkbn())
                && !StringUtils.isEmpty(dto.getHonbuejntcd())) {
                e.setHonbuejntapkihoncd(dto.getLoginUserApkihoncd()); // 本部エージェントＡＰ基本コード
                e.setHonbuejntshopcd(dto.getLoginUserShopcd()); // 本部エージェントショップコード
            }
            // 更新時
        } else {
            // 基本区分が「1：ACI」以外の場合
            if (!CommonConstants.KBN_KIHON_ACI.equals(userSession.getApkbn())) {
                e.setHonbuejntapkihoncd(dto.getHonbuejntapkihoncd()); // 本部エージェントＡＰ基本コード
                e.setHonbuejntshopcd(dto.getHonbuejntshopcd()); // 本部エージェントショップコード
            } else {
                // 画面.本部エージェントコードが設定値ありの場合
                if (!StringUtils.isEmpty(dto.getHonbuejntcd())) {
                    e.setHonbuejntapkihoncd(dto.getLoginUserApkihoncd()); // 本部エージェントＡＰ基本コード
                    e.setHonbuejntshopcd(dto.getLoginUserShopcd()); // 本部エージェントショップコード
                    // 画面.本部エージェントコードが設定値なしの場合は空文字を設定
                } else {
                    e.setHonbuejntapkihoncd(EMPTY); // 本部エージェントＡＰ基本コード
                    e.setHonbuejntshopcd(EMPTY); // 本部エージェントショップコード
                }
            }
        }

        e.setHonbuejntcd(dto.getHonbuejntcd()); // 本部エージェントコード
        e.setKakutokuejntcd(dto.getKakutokuejntcd()); // 獲得エージェントコード
        e.setShoukaitokuisakicd(dto.getShoukaitokuisakicd()); // 紹介得意先コード
        e.setHansokushscd(dto.getHansokushscd()); // 販促詳細コード
        e.setKakutokushsmemo(dto.getKakutokushsmemo()); // 獲得詳細メモ
        e.setToritugitenkaiinid(dto.getToritugitenkaiinid()); // 取次店会員ＩＤ
        e.setKakutokuhonbukynpncd(dto.getKakutokuhonbukynpncd()); // 獲得本部キャンペーンコード
        e.setHonbukynpnkeihinkbn(dto.getHonbukynpnkeihinkbn()); // 本部キャンペーン景品区分
        e.setKakutokukynpncd(dto.getKakutokukynpncd()); // 獲得キャンペーンコード
        e.setKynpnkeihinkbn(dto.getKynpnkeihinkbn()); // キャンペーン景品区分
        e.setShokaitokutenmemo(dto.getShokaitokutenmemo()); // 初回特典メモ
        e.setMgntrgtkbn(dto.getMgntrgtkbn()); // マージン対象区分

        // 画面.マージン対象区分が「0：設定なし」以外の場合
        if (!CommonConstants.KBN_MARGINTARGET_NONE.equals(dto.getMgntrgtkbn())) {
            e.setWanshottocd(dto.getWanshottocd()); // ワンショットコード
            e.setBtlmgntrgtdkikan(dto.getBtlmgntrgtdkikan()); // ボトルマージン対象期間
            e.setMgnyoshncdn1(dto.getMgnyoshncdn1()); // マージン用商品コード１
            e.setMgnyoshncdn2(dto.getMgnyoshncdn2()); // マージン用商品コード２
            e.setMgnyoshncdn3(dto.getMgnyoshncdn3()); // マージン用商品コード３
            e.setMgnyoshncdn4(dto.getMgnyoshncdn4()); // マージン用商品コード４
            e.setMgncdn1(dto.getMgncdn1()); // マージンコード１
            e.setMgncdn2(dto.getMgncdn2()); // マージンコード２
            e.setMgncdn3(dto.getMgncdn3()); // マージンコード３
            e.setMgncdn4(dto.getMgncdn4()); // マージンコード４
        } else {
            e.setWanshottocd(null); // ワンショットコード
            e.setBtlmgntrgtdkikan(null); // ボトルマージン対象期間
            e.setMgnyoshncdn1(null); // マージン用商品コード１
            e.setMgnyoshncdn2(null); // マージン用商品コード２
            e.setMgnyoshncdn3(null); // マージン用商品コード３
            e.setMgnyoshncdn4(null); // マージン用商品コード４
            e.setMgncdn1(null); // マージンコード１
            e.setMgncdn2(null); // マージンコード２
            e.setMgncdn3(null); // マージンコード３
            e.setMgncdn4(null); // マージンコード４
        }

        e.setJidonebikicdn1(dto.getJidonebikicdn1()); // 自動値引コード１
        e.setNebikistrtdydmn1(dto.getNebikistrtdydmn1()); // 値引開始年月１
        e.setNebikishrydydmn1(dto.getNebikishrydydmn1()); // 値引終了年月１
        e.setJidonebikicdn2(dto.getJidonebikicdn2()); // 自動値引コード２
        e.setNebikistrtdydmn2(dto.getNebikistrtdydmn2()); // 値引開始年月２
        e.setNebikishrydydmn2(dto.getNebikishrydydmn2()); // 値引終了年月２
        e.setJidonebikicdn3(dto.getJidonebikicdn3()); // 自動値引コード３
        e.setNebikistrtdydmn3(dto.getNebikistrtdydmn3()); // 値引開始年月３
        e.setNebikishrydydmn3(dto.getNebikishrydydmn3()); // 値引終了年月３
        e.setRtlskykanrikbn(dto.getRtlskykanrikbn()); // レンタル請求管理区分
        e.setSaburtlsokocd(dto.getSaburtlsokocd()); // サブレンタル倉庫コード
        e.setShokaiwsvstupdd(dto.getShokaiwsvstupdd()); // 初回サーバー設置日
        e.setStupddtoshaapkihoncd(dto.getStupddtoshaapkihoncd()); // 設置日登録者ＡＰ基本コード
        e.setStupddtoshashopcd(dto.getStupddtoshashopcd()); // 設置日登録者ショップコード
        e.setStupddtoshacd(dto.getStupddtoshacd()); // 設置日登録者コード
        e.setWsvhikiagedd(dto.getWsvhikiagedd()); // サーバー引上日
        e.setHikiageddtoshaapkihoncd(dto.getHikiageddtoshaapkihoncd()); // 引上日登録者ＡＰ基本コード
        e.setHikiageddtoshashopcd(dto.getHikiageddtoshashopcd()); // 引上日登録者ショップコード
        e.setHikiageddtoshacd(dto.getHikiageddtoshacd()); // 引上日登録者コード
        e.setDlvrykankatsuapkihoncd(dto.getDlvrykankatsuapkihoncd()); // 配達管轄ＡＰ基本コード
        e.setDlvrykankatsushopcd(dto.getDlvrykankatsushopcd()); // 配達管轄ショップコード
        e.setDlvrykankatsukyotencd(dto.getDlvrykankatsukyotencd()); // 配達管轄拠点コード
        e.setDlvrycmpnycd(dto.getDlvrycmpnycd()); // 配達会社コード
        e.setDlvrytnshacd(dto.getDlvrytnshacd()); // 配達担当者コード
        e.setDlvryjun(dto.getDlvryjun()); // 配達順
        e.setDlvryareacd(dto.getDlvryareacd()); // 配達エリアコード
        e.setDlvrykbn(dto.getDlvrykbn()); // 配達区分
        e.setFzaiokikanoflg(dto.getFzaiokikanoflg()); // 不在置き可能フラグ
        e.setNohinsakijohon1(dto.getNohinsakijohon1()); // 納品先情報１
        e.setNohinsakijohon2(dto.getNohinsakijohon2()); // 納品先情報２
        e.setChizuno(dto.getChizuno()); // 地図番号
        e.setGaiburenkeidlvrycd(dto.getGaiburenkeidlvrycd()); // 外部連携配達コード

        // 新規登録時
        if (insertFlg) {
            e.setAcptrcptkbn(CommonConstants.KBN_ACPTRCPT_TSUJO); // 受注受付区分
        }

        e.setAcptteishiflg(
            StringUtils.isEmpty(dto.getAcptteishiflg()) ? CommonConstants.ACPTTEISHIFLG_0
                : dto.getAcptteishiflg()); // 受注停止フラグ
        e.setAcptjikanshiteijikoku(dto.getAcptjikanshiteijikoku()); // 受注時間指定時刻
        e.setAcptjikanshiteijikokushry(dto.getAcptjikanshiteijikokushry()); // 受注時間指定時刻終了
        e.setAcptjikanshiteikbn(dto.getAcptjikanshiteikbn()); // 受注時間指定区分
        e.setNestlehachusakikbn(dto.getNestlehachusakikbn()); // ネスレ発注先区分
        e.setChuitokuisakikbnaci(dto.getChuitokuisakikbnaci()); // 注意得意先区分ＡＣＩ
        e.setChuinaiyoaci(dto.getChuinaiyoaci()); // 注意内容ＡＣＩ
        e.setChuitokuisakikbnap(dto.getChuitokuisakikbnap()); // 注意得意先区分ＡＰ
        e.setChuinaiyoap(dto.getChuinaiyoap()); // 注意内容ＡＰ
        e.setRenrakujikokokai(dto.getRenrakujikokokai()); // 連絡事項公開
        e.setTokkijikohikokai(dto.getTokkijikohikokai()); // 特記事項非公開
        e.setPrntmemon1(dto.getPrntmemon1()); // 印刷メモ１
        e.setPrntmemon2(dto.getPrntmemon2()); // 印刷メモ２
        e.setPrntmemon3(dto.getPrntmemon3()); // 印刷メモ３
        e.setKinotptnashiflg(dto.getKinotptnashiflg()); // 金額出力無フラグ（納品金額印字 ）
        e.setHoshokinkbn(dto.getHoshokinkbn()); // 保証金区分
        e.setHoshokinazukaridd(dto.getHoshokinazukaridd()); // 保証金預かり日
        e.setHoshokinazukarikin(!StringUtils.isEmpty(dto.getHoshokinazukarikin())
            ? Long.parseLong(dto.getHoshokinazukarikin())
            : null); // 保証金預かり金額
        e.setAzushono(dto.getAzushono()); // 預かり書番号
        e.setHoshokinhenkyakudd(dto.getHoshokinhenkyakudd()); // 保証金返却日
        e.setHoshokinhenkyakukin(!StringUtils.isEmpty(dto.getHoshokinhenkyakukin())
            ? Long.parseLong(dto.getHoshokinhenkyakukin())
            : null); // 保証金返却金額

        // 新規登録時
        if (insertFlg) {
            e.setKeizokurnk(CommonConstants.KEIZOKURNK_C); // 継続ランク
            e.setShohirnk(CommonConstants.SHOHIRNK_C); // 消費ランク
        }

        e.setApbetsutokuisakirnk(dto.getApbetsutokuisakirnk()); // ＡＰ別得意先ランク

        // 新規登録時
        if (insertFlg) {
            e.setKyuminkbn(CommonConstants.KYUMINKBN_0); // 休眠区分
        }

        e.setKyuminkanri(dto.getKyuminkanri()); // 休眠管理
        e.setKinmusakin1(dto.getKinmusakin1()); // 勤務先１
        e.setKinmusakin2(dto.getKinmusakin2()); // 勤務先２
        e.setKinmusakiyubinno(dto.getKinmusakiyubinno()); // 勤務先郵便番号
        e.setKinmusakichihoupubdanticdn1(dto.getKinmusakichihoupubdanticdn1()); // 勤務先地方公共団体コード１
        e.setKinmusakichihoupubdanticdn2(dto.getKinmusakichihoupubdanticdn2()); // 勤務先地方公共団体コード２
        e.setKinmusakiadrsn1(dto.getKinmusakiadrsn1()); // 勤務先住所１
        e.setKinmusakiadrsn2(dto.getKinmusakiadrsn2()); // 勤務先住所２
        e.setKinmusakiadrsn3(dto.getKinmusakiadrsn3()); // 勤務先住所３
        e.setKinmusakitelno(dto.getKinmusakitelno()); // 勤務先電話番号
        e.setKinmusakifaxno(dto.getKinmusakifaxno()); // 勤務先ＦＡＸ番号
        e.setYoshingendokin(
            !StringUtils.isEmpty(dto.getYoshingendokin()) ? Long.parseLong(dto.getYoshingendokin())
                : null); // 与信限度金額
        e.setTokuisakigaibuskbtscd(dto.getTokuisakigaibuskbtscd()); // 得意先外部識別コード
        e.setTokuisakigaibuskbtscd2(dto.getTokuisakigaibuskbtscd2()); // 得意先外部識別コード2
        e.setTokuisakigaibuskbtscd3(dto.getTokuisakigaibuskbtscd3()); // 得意先外部識別コード3
        e.setTokuisakishukeicd(dto.getTokuisakishukeicd()); // 得意先集計コード

        // 得意先紹介者数の取得
        dto.setTokuisakishoshakazu(
            getShoshakazu(dto.getApkihoncd(), dto.getShopcd(), dto.getTokuisakicd()));

        e.setTokuisakishoshakazu(Short.parseShort(dto.getTokuisakishoshakazu())); // 得意先紹介者数
        e.setStartup(
            StringUtils.isEmpty(dto.getStartup()) ? CommonConstants.STARTUP_0 : dto.getStartup()); // スタートアップ
        e.setMtmkisvkbn(dto.getMtmkisvkbn()); // まとめ買いサービス区分

        // 新規登録時
        if (insertFlg) {
            e.setMtmkiyuryohns(!StringUtils.isEmpty(dto.getMtmkiyuryohns())
                ? Short.parseShort(dto.getMtmkiyuryohns())
                : null);// まとめ買い有料本数
            e.setMtmkimuryohns(!StringUtils.isEmpty(dto.getMtmkimuryohns())
                ? Short.parseShort(dto.getMtmkimuryohns())
                : null); // まとめ買い無料本数

            // 更新時
        } else {
            // 画面.まとめ買いサービス区分が「0：なし」の場合
            if (CommonConstants.MTMKISVKBN_0.equals(dto.getMtmkisvkbn())) {
                e.setMtmkiyuryohns(null);// まとめ買い有料本数
                e.setMtmkimuryohns(null); // まとめ買い無料本数
            } else {
                e.setMtmkiyuryohns(!StringUtils.isEmpty(dto.getMtmkiyuryohns())
                    ? Short.parseShort(dto.getMtmkiyuryohns())
                    : null);// まとめ買い有料本数
                e.setMtmkimuryohns(!StringUtils.isEmpty(dto.getMtmkimuryohns())
                    ? Short.parseShort(dto.getMtmkimuryohns())
                    : null); // まとめ買い無料本数
            }
        }

        e.setMuryobtlsvkbn(dto.getMuryobtlsvkbn()); // 無料ボトルサービス区分
        e.setMyaqauntjohodsplyumuflg(dto.getMyaqauntjohodsplyumuflg()); // マイアクア単価情報表示有無フラグ
        e.setMyaqaskyjohodsplyumuflg(dto.getMyaqaskyjohodsplyumuflg()); // マイアクア請求情報表示有無フラグ

        // 新規登録時
        if (insertFlg) {
            e.setBabyaqariyoflg(CommonConstants.BABYAQARIYOFLG_0); // ベビアクア利用フラグ
        }

        e.setTashakirikae(dto.getTashakirikae()); // 他社切替
        e.setCollabokbn(dto.getCollabokbn()); // コラボ区分
        e.setYukokbn(dto.getYukokbn()); // 有効区分

        // 新規登録時
        if (insertFlg) {
            e.setToshaapkihoncd(dto.getLoginUserApkihoncd()); // 登録者ＡＰ基本コード
            e.setToshashopcd(dto.getLoginUserShopcd()); // 登録者ショップコード
            e.setToshacd(dto.getLoginUserTnshacd()); // 登録者コード
            e.setInptsystmymd(dto.getSystemDate()); // 登録システム日付
            e.setInptsystmjikan(dto.getSystemTime()); // 登録システム時間
            e.setInptunyoymd(dto.getUnyoDate()); // 登録運用日付
            e.setInptipadrse(dto.getIpAddress()); // 登録ＩＰアドレス
            e.setInptpgmid(dto.getProgramId()); // 登録プログラムＩＤ
        }

        e.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        e.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        e.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        e.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        e.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        e.setKshnunyoymd(dto.getUnyoDate()); // 更新運用日付
        e.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        e.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return e;
    }

    /**
     * 得意先ポイントマスタエンティティの登録/更新時のvalue値を設定する。
     * 
     * @param dto 登録更新用DTO
     * @param src 得意先ポイントマスタエンティティ(新規登録時はnull)
     * @return S02M203TEntity
     */
    private S02M203TEntity createTokuisakiPointUpdateEntity(B0501W02UpdateDto dto,
        S02M203TEntity src) {

        boolean insertFlg = ObjectUtils.isEmpty(src);
        S02M203TEntity e = insertFlg ? new S02M203TEntity() : src;

        // 新規登録時
        if (insertFlg) {
            e.setApkihoncd(dto.getApkihoncd()); // ＡＰ基本コード
            e.setShopcd(dto.getShopcd()); // ショップコード
            e.setTokuisakicd(dto.getTokuisakicd()); // 得意先コード
        }
        e.setPointkanriumu(dto.getPointkbn()); // ポイント管理有無
        e.setMyaquapointriyokyodaku(dto.getMyaquakaiininptumu()); // マイアクアポイント利用許諾-マイアクア会員登録と同じ値を設定
        e.setMyaquakaiininptumu(dto.getMyaquakaiininptumu()); // マイアクア会員登録
        e.setMtmkipointkbn(dto.getMtmkipointkbn()); // まとめ買いポイント区分

        // 画面.まとめ買いポイント区分が「0：なし」の場合
        if (CommonConstants.MTMKIPOINTKBN_0.equals(dto.getMtmkipointkbn())) {
            e.setMtmkisuryo(null); // まとめ買い数量
            e.setMtmkipointsu(null); // まとめ買いポイント数
        } else {
            e.setMtmkisuryo(
                !StringUtils.isEmpty(dto.getMtmkisuryo()) ? Integer.parseInt(dto.getMtmkisuryo())
                    : null); // まとめ買い数量
            e.setMtmkipointsu(!StringUtils.isEmpty(dto.getMtmkipointsu())
                ? Integer.parseInt(dto.getMtmkipointsu())
                : null); // まとめ買いポイント数
        }

        e.setNyukaitokutenumu(dto.getNyukaitokutenumu()); // 入会特典有無

        // 画面.入会特典有無が「0：なし」の場合
        if (CommonConstants.NYUKAITOKUTENUMU_0.equals(dto.getNyukaitokutenumu())) {
            e.setNyukaitokutenpointsu(null); // 入会特典ポイント数
        } else {
            e.setNyukaitokutenpointsu(!StringUtils.isEmpty(dto.getNyukaitokutenpointsu())
                ? Integer.parseInt(dto.getNyukaitokutenpointsu())
                : null); // 入会特典ポイント数
        }

        e.setYukokbn(dto.getYukokbn()); // 有効区分

        // 新規登録時
        if (insertFlg) {
            e.setToshaapkihoncd(dto.getLoginUserApkihoncd()); // 登録者ＡＰ基本コード
            e.setToshashopcd(dto.getLoginUserShopcd()); // 登録者ショップコード
            e.setToshacd(dto.getLoginUserTnshacd()); // 登録者コード
            e.setInptsystmymd(dto.getSystemDate()); // 登録システム日付
            e.setInptsystmjikan(dto.getSystemTime()); // 登録システム時間
            e.setInptunyoymd(dto.getUnyoDate()); // 登録運用日付
            e.setInptipadrse(dto.getIpAddress()); // 登録ＩＰアドレス
            e.setInptpgmid(dto.getProgramId()); // 登録プログラムＩＤ
        }
        e.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        e.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        e.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        e.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        e.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        e.setKshnunyoymd(dto.getUnyoDate()); // 更新運用日付
        e.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        e.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return e;
    }

    /**
     * 得意先ポイントランクマスタエンティティの登録時のvalue値を設定する。
     * 
     * @param dto 登録更新用DTO
     * @return S02M203TEntity
     */
    private S02M204TEntity createTokuisakiPointRankUpdateEntity(B0501W02UpdateDto dto) {

        S02M204TEntity e = new S02M204TEntity();

        e.setApkihoncd(dto.getApkihoncd()); // ＡＰ基本コード
        e.setShopcd(dto.getShopcd()); // ショップコード
        e.setTokuisakicd(dto.getTokuisakicd()); // 得意先コード
        e.setTekiyoudd(dto.getUnyoDate()); // 適用年月日：運用日付
        e.setPointrnkcd(dto.getPointrnkcdgen()); // ポイントランクコード（現）：初期値

        e.setYukokbn(dto.getYukokbn()); // 有効区分
        e.setToshaapkihoncd(dto.getLoginUserApkihoncd()); // 登録者ＡＰ基本コード
        e.setToshashopcd(dto.getLoginUserShopcd()); // 登録者ショップコード
        e.setToshacd(dto.getLoginUserTnshacd()); // 登録者コード
        e.setInptsystmymd(dto.getSystemDate()); // 登録システム日付
        e.setInptsystmjikan(dto.getSystemTime()); // 登録システム時間
        e.setInptunyoymd(dto.getUnyoDate()); // 登録運用日付
        e.setInptipadrse(dto.getIpAddress()); // 登録ＩＰアドレス
        e.setInptpgmid(dto.getProgramId()); // 登録プログラムＩＤ
        e.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        e.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        e.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        e.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        e.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        e.setKshnunyoymd(dto.getUnyoDate()); // 更新運用日付
        e.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        e.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return e;
    }

    /**
     * ポイントランク強制変更予約テーブルエンティティの登録/更新時のvalue値を設定する。
     * 
     * @param dto 登録更新用DTO
     * @param src 既存データEntity
     * @param isMukou 無効化フラグ
     * @return S02M203TEntity
     */
    private S20T003TEntity createPointKyoseiHenkouYoyakuUpdateEntity(B0501W02UpdateDto dto,
        S20T003TEntity src, boolean isMukou) {

        boolean insertFlg = ObjectUtils.isEmpty(src);
        S20T003TEntity e = insertFlg ? new S20T003TEntity() : src;

        // 新規登録時
        if (insertFlg) {
            e.setApkihoncd(dto.getApkihoncd()); // ＡＰ基本コード
            e.setShopcd(dto.getShopcd()); // ショップコード
            e.setTokuisakicd(dto.getTokuisakicd()); // 得意先コード
        }
        e.setHenkomaepointrnkcd(dto.getPointrnkcdgen()); // 変更前ポイントランクコード
        if (isMukou) {
            e.setHenkoatopointrnkcd(null); // 変更後ポイントランクコード
            e.setHenkokibodd(CommonConstants.PKVALUENOTSELECT_STRING); // 変更指定日: NotNullのため空白
        } else {
            e.setHenkoatopointrnkcd(dto.getPointrnkcdshin()); // 変更後ポイントランクコード
            e.setHenkokibodd(dto.getTekiyoudd()); // 変更指定日
        }
        e.setHenkojissidd(null); // 変更実施日

        if (isMukou) {
            e.setYukokbn(CommonConstants.YUKOKBN_0); // 有効区分 無効
        } else {
            e.setYukokbn(dto.getYukokbn()); // 有効区分
        }
        // 新規登録時
        if (insertFlg) {
            e.setToshaapkihoncd(dto.getLoginUserApkihoncd()); // 登録者ＡＰ基本コード
            e.setToshashopcd(dto.getLoginUserShopcd()); // 登録者ショップコード
            e.setToshacd(dto.getLoginUserTnshacd()); // 登録者コード
            e.setInptsystmymd(dto.getSystemDate()); // 登録システム日付
            e.setInptsystmjikan(dto.getSystemTime()); // 登録システム時間
            e.setInptunyoymd(dto.getUnyoDate()); // 登録運用日付
            e.setInptipadrse(dto.getIpAddress()); // 登録ＩＰアドレス
            e.setInptpgmid(dto.getProgramId()); // 登録プログラムＩＤ
        }
        e.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        e.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        e.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        e.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        e.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        e.setKshnunyoymd(dto.getUnyoDate()); // 更新運用日付
        e.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        e.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return e;
    }

    /**
     * 得意先実績エンティティの登録時のvalue値を設定する。
     * 
     * @param dto 登録更新用DTO
     * @return S02M050TEntity
     */
    private S02M050TEntity createTokuisakiJissekiInsertEntity(B0501W02UpdateDto dto) {

        S02M050TEntity e = new S02M050TEntity();

        e.setApkihoncd(dto.getApkihoncd()); // ＡＰ基本コード
        e.setShopcd(dto.getShopcd()); // ショップコード
        e.setTokuisakicd(dto.getTokuisakicd()); // 得意先コード
        e.setGenzaizd(0); // 現在残高
        e.setKeijomaeacptkin(LONG_ZERO); // 計上前受注金額
        e.setZnkdlvryrgrayusyohns(SHORT_ZERO); // 前回配達レギュラー有償本数
        e.setZnkdlvryrgramushohns(SHORT_ZERO); // 前回配達レギュラー無償本数
        e.setZnkdlvrysrmyusyohns(SHORT_ZERO); // 前回配達スリム有償本数
        e.setZnkdlvrysrmmushohns(SHORT_ZERO); // 前回配達スリム無償本数
        e.setZnkdlvrysonotayusyohns(SHORT_ZERO); // 前回配達その他有償本数
        e.setZnkdlvrysonotamushohns(SHORT_ZERO); // 前回配達その他無償本数
        e.setZnkdlvryseihinmizuurikin(LONG_ZERO); // 前回配達製品水売上金額
        e.setZnkdlvrysonotaurikin(LONG_ZERO); // 前回配達その他売上金額
        e.setZzkhtrgrayusyohns(SHORT_ZERO); // 前々回配達レギュラー有償本数
        e.setZzkhtrgramushohns(SHORT_ZERO); // 前々回配達レギュラー無償本数
        e.setZzkhtsrmyusyohns(SHORT_ZERO); // 前々回配達スリム有償本数
        e.setZzkhtsrmmushohns(SHORT_ZERO); // 前々回配達スリム無償本数
        e.setZzkhtsonotayusyohns(SHORT_ZERO); // 前々回配達その他有償本数
        e.setZzkhtsonotamushohns(SHORT_ZERO); // 前々回配達その他無償本数
        e.setZzkhtseihinmizuurikin(LONG_ZERO); // 前々回配達製品水売上金額
        e.setZzkhtsonotaurikin(LONG_ZERO); // 前々回配達その他売上金額
        e.setZzzkhtrgrayusyohns(SHORT_ZERO); // 前々前回配達レギュラー有償本数
        e.setZzzkhtrgramushohns(SHORT_ZERO); // 前々前回配達レギュラー無償本数
        e.setZzzkhtsrmyusyohns(SHORT_ZERO); // 前々前回配達スリム有償本数
        e.setZzzkhtsrmmushohns(SHORT_ZERO); // 前々前回配達スリム無償本数
        e.setZzzkhtsonotayusyohns(SHORT_ZERO); // 前々前回配達その他有償本数
        e.setZzzkhtsonotamushohns(SHORT_ZERO); // 前々前回配達その他無償本数
        e.setZzzkhtseihinmizuurikin(LONG_ZERO); // 前々前回配達製品水売上金額
        e.setZzzkhtsonotaurikin(LONG_ZERO); // 前々前回配達その他売上金額
        e.setAzukaribtln1n2rittoruhns(!StringUtils.isEmpty(dto.getAzukaribtln1n2rittoruhns())
            ? Integer.parseInt(dto.getAzukaribtln1n2rittoruhns())
            : 0); // 預かりボトル１２Ｌ本数
        e.setAzukaribtln8rittoruhns(!StringUtils.isEmpty(dto.getAzukaribtln8rittoruhns())
            ? Integer.parseInt(dto.getAzukaribtln8rittoruhns())
            : 0); // 預かりボトル８Ｌ本数
        e.setYukokbn(dto.getYukokbn()); // 有効区分
        e.setToshaapkihoncd(dto.getLoginUserApkihoncd()); // 登録者ＡＰ基本コード
        e.setToshashopcd(dto.getLoginUserShopcd()); // 登録者ショップコード
        e.setToshacd(dto.getLoginUserTnshacd()); // 登録者コード
        e.setInptsystmymd(dto.getSystemDate()); // 登録システム日付
        e.setInptsystmjikan(dto.getSystemTime()); // 登録システム時間
        e.setInptunyoymd(dto.getUnyoDate()); // 登録運用日付
        e.setInptipadrse(dto.getIpAddress()); // 登録ＩＰアドレス
        e.setInptpgmid(dto.getProgramId()); // 登録プログラムＩＤ
        e.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        e.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        e.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        e.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        e.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        e.setKshnunyoymd(dto.getUnyoDate()); // 更新運用日付
        e.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        e.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return e;
    }

    /**
     * 請求先コードの設定値を取得する。
     * 
     * @param dto 登録更新用DTO
     * @return 請求先コード
     */
    private String getSkysakicd(B0501W02UpdateDto dto) {
        String ret = null;
        // 画面.請求設定区分が「2：自動生成」、かつ、画面.請求先コードが未設定の場合
        if (SKYSETTEIKBN_2.equals(dto.getSkysetteikbn())
            && StringUtils.isEmpty(dto.getSkysakicd())) {
            ret = dto.getTokuisakicd();

            // 画面.請求設定区分が「2：自動生成」以外、かつ、画面.請求先コードが設定値ありの場合
        } else if (!SKYSETTEIKBN_2.equals(dto.getSkysetteikbn())
            && !StringUtils.isEmpty(dto.getSkysakicd())) {
            ret = dto.getSkysakicd();
        }

        return ret;
    }

    /**
     * 得意先実績エンティティの更新時のvalue値を設定する。
     * 
     * @param dto 登録更新用DTO
     * @return S02M050TEntity
     */
    private S02M050TEntity createTokuisakiJissekiUpdateEntity(B0501W02UpdateDto dto) {

        S02M050TEntity e = new S02M050TEntity();

        e.setApkihoncd(dto.getApkihoncd()); // ＡＰ基本コード
        e.setShopcd(dto.getShopcd()); // ショップコード
        e.setTokuisakicd(dto.getTokuisakicd()); // 得意先コード
        e.setAzukaribtln1n2rittoruhns(!StringUtils.isEmpty(dto.getAzukaribtln1n2rittoruhns())
            ? Integer.parseInt(dto.getAzukaribtln1n2rittoruhns())
            : 0); // 預かりボトル１２Ｌ本数
        e.setAzukaribtln8rittoruhns(!StringUtils.isEmpty(dto.getAzukaribtln8rittoruhns())
            ? Integer.parseInt(dto.getAzukaribtln8rittoruhns())
            : 0); // 預かりボトル８Ｌ本数
        e.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        e.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        e.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        e.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        e.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        e.setKshnunyoymd(dto.getUnyoDate()); // 更新運用日付
        e.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        e.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return e;
    }

    /**
     * 得意先エンティティに得意先状態、状態ステータスを設定する
     * 
     * @param dto 更新用DTO
     * @param entity 得意先エンティティ
     */
    private void setTokuisakiJotai(B0501W02UpdateDto dto, S02M013TEntity entity) {

        // 各種条件で得意先状態、状態ステータスを設定するが、
        // 後勝ちとなる。（上から下へ優先度が高くなる）

        // 得意先状態：申込受付／状態ステータス：設置前
        String tokuisakijotai = CommonConstants.KBN_TOKUISAKIJOTAI_MOSHIKOMI;
        String jotaists = CommonConstants.KBN_JOTAISTAT_1;

        // 申込キャンセル日が設定されている場合
        if (!StringUtils.isEmpty(dto.getMoshikomicncldd())) {
            // 得意先状態：設置前キャンセル／状態ステータス：設置前キャンセル
            tokuisakijotai = CommonConstants.KBN_TOKUISAKIJOTAI_SETCANCEL;
            jotaists = CommonConstants.KBN_JOTAISTAT_2;
        }

        // 初回サーバー設置日が設定されている場合
        if (!StringUtils.isEmpty(dto.getShokaiwsvstupdd())) {
            // 得意先状態：仮会員／状態ステータス：試飲中
            tokuisakijotai = CommonConstants.KBN_TOKUISAKIJOTAI_KARIKAIIN;
            jotaists = CommonConstants.KBN_JOTAISTAT_3;
        }

        // 得意先区分=1(顧客) or 2(ショップ) or 5(NA受託) or 6(配達受託)の場合
        if (CommonConstants.KBN_TOKUISAKI_KOKUAKU.equals(dto.getTokuisakikbn())
            || CommonConstants.KBN_TOKUISAKI_SHOP.equals(dto.getTokuisakikbn())
            || CommonConstants.KBN_TOKUISAKI_NAJUTAKU.equals(dto.getTokuisakikbn())
            || CommonConstants.KBN_TOKUISAKI_DLVRYJUTAKU.equals(dto.getTokuisakikbn())) {
            // 初回サーバー設置日、退会受付日が設定されている場合
            if (!StringUtils.isEmpty(dto.getShokaiwsvstupdd())
                && !StringUtils.isEmpty(dto.getTaikaidd())) {
                // 得意先状態：仮会員退会／状態ステータス：引上処理中
                tokuisakijotai = CommonConstants.KBN_TOKUISAKIJOTAI_KARIKAIYAKU;
                jotaists = CommonConstants.KBN_JOTAISTAT_4;
            }

            // 初回サーバー設置日、退会受付日、サーバー引上日が設定されている場合
            if (!StringUtils.isEmpty(dto.getShokaiwsvstupdd())
                && !StringUtils.isEmpty(dto.getTaikaidd())
                && !StringUtils.isEmpty(dto.getWsvhikiagedd())) {
                // 現在残高≠0の場合
                if (!"0".equals(dto.getGenzaizd())) {
                    // 得意先状態：仮会員退会／状態ステータス：退会（未払い有り）
                    tokuisakijotai = CommonConstants.KBN_TOKUISAKIJOTAI_KARIKAIYAKU;
                    jotaists = CommonConstants.KBN_JOTAISTAT_5;

                    // 現在残高＝0の場合
                } else {
                    // 得意先状態：仮会員退会／状態ステータス：退会済
                    tokuisakijotai = CommonConstants.KBN_TOKUISAKIJOTAI_KARIKAIYAKU;
                    jotaists = CommonConstants.KBN_JOTAISTAT_6;
                }

            }

            // 初回サーバー設置日、入会日が設定されている場合
            if (!StringUtils.isEmpty(dto.getShokaiwsvstupdd())
                && !StringUtils.isEmpty(dto.getNyukaidd())) {
                // 得意先状態：本会員／状態ステータス：継続中
                tokuisakijotai = CommonConstants.KBN_TOKUISAKIJOTAI_HONKAIIN;
                jotaists = CommonConstants.KBN_JOTAISTAT_7;
            }

            // 初回サーバー設置日、入会日、退会受付日が設定されている場合
            if (!StringUtils.isEmpty(dto.getShokaiwsvstupdd())
                && !StringUtils.isEmpty(dto.getNyukaidd())
                && !StringUtils.isEmpty(dto.getTaikaidd())) {
                // 得意先状態：本会員／状態ステータス：引上処理中
                tokuisakijotai = CommonConstants.KBN_TOKUISAKIJOTAI_HONKAIIN;
                jotaists = CommonConstants.KBN_JOTAISTAT_8;
            }

            // 初回サーバー設置日、入会日、退会受付日、サーバー引上日が設定されている場合
            if (!StringUtils.isEmpty(dto.getShokaiwsvstupdd())
                && !StringUtils.isEmpty(dto.getNyukaidd())
                && !StringUtils.isEmpty(dto.getTaikaidd())
                && !StringUtils.isEmpty(dto.getWsvhikiagedd())) {
                // 現在残高≠0の場合
                if (!"0".equals(dto.getGenzaizd())) {
                    // 得意先状態：退会／状態ステータス：退会（未払い有り）
                    tokuisakijotai = CommonConstants.KBN_TOKUISAKIJOTAI_TAIKAI;
                    jotaists = CommonConstants.KBN_JOTAISTAT_9;

                    // 現在残高＝0の場合
                } else {
                    // 得意先状態：退会／状態ステータス：退会済
                    tokuisakijotai = CommonConstants.KBN_TOKUISAKIJOTAI_TAIKAI;
                    jotaists = CommonConstants.KBN_JOTAISTAT_10;
                }
            }

            // 得意先区分が上記以外の場合
        } else {
            // 退会受付日が設定されている場合
            if (!StringUtils.isEmpty(dto.getTaikaidd())) {
                // 得意先状態：仮会員退会／状態ステータス：引上処理中
                tokuisakijotai = CommonConstants.KBN_TOKUISAKIJOTAI_KARIKAIYAKU;
                jotaists = CommonConstants.KBN_JOTAISTAT_4;
                // 現在残高≠0の場合
                if (!"0".equals(dto.getGenzaizd())) {
                    // 得意先状態：仮会員退会／状態ステータス：退会（未払い有り）
                    tokuisakijotai = CommonConstants.KBN_TOKUISAKIJOTAI_KARIKAIYAKU;
                    jotaists = CommonConstants.KBN_JOTAISTAT_5;

                    // 現在残高＝0の場合
                } else {
                    // 得意先状態：仮会員退会／状態ステータス：退会済
                    tokuisakijotai = CommonConstants.KBN_TOKUISAKIJOTAI_KARIKAIYAKU;
                    jotaists = CommonConstants.KBN_JOTAISTAT_6;
                }
            }

            // 入会日が設定されている場合
            if (!StringUtils.isEmpty(dto.getNyukaidd())) {
                // 得意先状態：本会員／状態ステータス：継続中
                tokuisakijotai = CommonConstants.KBN_TOKUISAKIJOTAI_HONKAIIN;
                jotaists = CommonConstants.KBN_JOTAISTAT_7;
            }

            // 入会日、退会受付日が設定されている場合
            if (!StringUtils.isEmpty(dto.getNyukaidd())
                && !StringUtils.isEmpty(dto.getTaikaidd())) {
                // 得意先状態：退会／状態ステータス：引上処理中
                tokuisakijotai = CommonConstants.KBN_TOKUISAKIJOTAI_TAIKAI;
                jotaists = CommonConstants.KBN_JOTAISTAT_8;
                // 現在残高≠0の場合
                if (!"0".equals(dto.getGenzaizd())) {
                    // 得意先状態：退会／状態ステータス：退会（未払い有り）
                    tokuisakijotai = CommonConstants.KBN_TOKUISAKIJOTAI_TAIKAI;
                    jotaists = CommonConstants.KBN_JOTAISTAT_9;

                    // 現在残高＝0の場合
                } else {
                    // 得意先状態：退会／状態ステータス：退会済
                    tokuisakijotai = CommonConstants.KBN_TOKUISAKIJOTAI_TAIKAI;
                    jotaists = CommonConstants.KBN_JOTAISTAT_10;
                }
            }
        }

        entity.setTokuisakijotai(tokuisakijotai);
        entity.setJotaists(jotaists);
    }

    /**
     * 得意先エンティティに特別単価区分を設定する
     * 
     * @param dto 更新用DTO
     * @param entity 得意先エンティティ
     */
    private void setTokubetsuTankaKbn(B0501W02UpdateDto dto, S02M013TEntity entity) {

        // 画面.プラン＝000：基本プランの場合
        if (CommonConstants.PLNCD_000.equals(dto.getPlncd())) {
            // 画面.特別値引＝0：なしの場合
            if (CommonConstants.RKNKINDTOKUBETSUNEBIKIFLG_0
                .equals(dto.getRknkindtokubetsunebikiflg())) {
                // 画面.特別単価区分＝0：なしの場合
                if (CommonConstants.RKNKINDTOKUBETSUUNTFLG_0
                    .equals(dto.getRknkindtokubetsuuntflg())) {
                    // 「0：基本」を設定する
                    entity.setTokubetsuuntkbn(CommonConstants.TOKUBETSUUNTKBN_0);

                    // 画面.特別単価区分＝1：ありの場合
                } else {
                    // 「1：特別単価」を設定する
                    entity.setTokubetsuuntkbn(CommonConstants.TOKUBETSUUNTKBN_1);
                }

                // 画面.特別値引＝1：ありの場合
            } else {
                // 画面.特別単価区分＝0：なしの場合
                if (CommonConstants.RKNKINDTOKUBETSUUNTFLG_0
                    .equals(dto.getRknkindtokubetsuuntflg())) {
                    // 「2：特別値引」を設定する
                    entity.setTokubetsuuntkbn(CommonConstants.TOKUBETSUUNTKBN_2);

                    // 画面.特別単価区分＝1：ありの場合
                } else {
                    // 「3：特別単価 + 特別値引」を設定する
                    entity.setTokubetsuuntkbn(CommonConstants.TOKUBETSUUNTKBN_3);
                }
            }

            // 画面.プラン＝001：子育てｱｸｱﾌﾟﾗﾝの場合
        } else if (CommonConstants.PLNCD_001.equals(dto.getPlncd())) {
            // 画面.特別単価区分＝0：なしの場合
            if (CommonConstants.RKNKINDTOKUBETSUUNTFLG_0.equals(dto.getRknkindtokubetsuuntflg())) {
                // 「4：子育てｱｸｱﾌﾟﾗﾝ」を設定する
                entity.setTokubetsuuntkbn(CommonConstants.TOKUBETSUUNTKBN_BABYAQUA);

                // 画面.特別単価区分＝1：ありの場合
            } else {
                // 「5：子育てｱｸｱ特別ﾌﾟﾗﾝ」を設定する
                entity.setTokubetsuuntkbn(CommonConstants.TOKUBETSUUNTKBN_5);
            }

            // 画面.プラン＝004：2年割プランの場合
        } else {
            // 画面.特別単価区分＝0：なしの場合
            if (CommonConstants.RKNKINDTOKUBETSUUNTFLG_0.equals(dto.getRknkindtokubetsuuntflg())) {
                // 「6：2年割プラン」を設定する
                entity.setTokubetsuuntkbn(CommonConstants.TOKUBETSUUNTKBN_6);

                // 画面.特別単価区分＝1：ありの場合
            } else {
                // 「7：2年割特別プラン」を設定する
                entity.setTokubetsuuntkbn(CommonConstants.TOKUBETSUUNTKBN_7);
            }
        }
    }

    /**
     * 事前チェックを実行する
     * 
     * @param dto 更新用DTO
     * @param messageList エラーメッセージ格納用リスト
     */
    public void checkParamPre(B0501W02UpdateDto dto, List<String> messageList) {

        // 新規登録かつ、画面.得意先区分＝1(顧客)の場合
        if (StringUtils.isEmpty(dto.getTokuisakicd())
            && CommonConstants.KBN_TOKUISAKI_KOKUAKU.equals(dto.getTokuisakikbn())
            && !StringUtils.isEmpty(dto.getTokuisakinmkanan1())
            && !StringUtils.isEmpty(dto.getAdrsn1())
            && (!StringUtils.isEmpty(dto.getRenrakusakinon1())
                || !StringUtils.isEmpty(dto.getRenrakusakinon2())
                || !StringUtils.isEmpty(dto.getRenrakusakinon3())
                || !StringUtils.isEmpty(dto.getRenrakusakinon4()))) {

            // ==============
            // ブラック顧客チェック
            // ==============
            // 得意先名称カナ（半角）１の値から空白を除去する
            String kana = dto.getTokuisakinmkanan1().replaceAll("[\\s]", "");
            // 住所１の値から空白を除去し、全角ひらがなを全角カタカナへ変換する
            String adres =
                ConvertUtil.zenHiraganaToZenKatakana(dto.getAdrsn1().replaceAll("[　\\s]", ""));

            // 検索条件の設定
            S02M013TEntity key = new S02M013TEntity();
            key.setTokuisakinmkanan1(kana);
            key.setAdrsn1(adres);
            key.setRenrakusakinon1(!StringUtils.isEmpty(dto.getRenrakusakinon1())
                ? dto.getRenrakusakinon1().replaceAll("-", "")
                : null);
            key.setRenrakusakinon2(!StringUtils.isEmpty(dto.getRenrakusakinon2())
                ? dto.getRenrakusakinon2().replaceAll("-", "")
                : null);
            key.setRenrakusakinon3(!StringUtils.isEmpty(dto.getRenrakusakinon3())
                ? dto.getRenrakusakinon3().replaceAll("-", "")
                : null);
            key.setRenrakusakinon4(!StringUtils.isEmpty(dto.getRenrakusakinon4())
                ? dto.getRenrakusakinon4().replaceAll("-", "")
                : null);

            // 住所、電話番号が一致する件数を取得
            S02M013TB0501W02BlackCheckEntity result =
                s02m013tDao.selectByB0501W02ForBlackCheck(key);

            if (!ObjectUtils.isEmpty(result)) {
                // 住所が一致する件数>0、電話番号が一致する件数>0の場合
                if (result.getAdrsum() > 0 && result.getTelsum() > 0) {
                    BusinessExceptionFactory.setMessageList(messageList, MessageID.W271,
                        "住所|連絡先番号");

                    // 住所が一致する件数>0の場合
                } else if (result.getAdrsum() > 0) {
                    BusinessExceptionFactory.setMessageList(messageList, MessageID.W271, "住所");

                    // 電話番号が一致する件数>0の場合
                } else if (result.getTelsum() > 0) {
                    BusinessExceptionFactory.setMessageList(messageList, MessageID.W271, "連絡先番号");
                }
            }
        }

        // 更新レコードの場合
        if (!StringUtils.isEmpty(dto.getTokuisakicd())) {
            // ==============
            // 親得意先チェック
            // ==============
            // 得意先マスタの取得
            S02M013TEntity entity =
                getTokuisakiMst(dto.getApkihoncd(), dto.getShopcd(), dto.getTokuisakicd());

            if (!ObjectUtils.isEmpty(entity)) {
                // 請求先コード＝未設定の場合
                if (StringUtils.isEmpty(entity.getSkysakicd())) {
                    // チェックOK

                    // 画面.有効区分＝1(有効) かつ 請求先コード＝画面.請求先コードの場合
                } else if (CommonConstants.YUKOKBN_1.equals(dto.getYukokbn())
                    && entity.getSkysakicd().equals(dto.getSkysakicd())) {
                    // チェックOK

                    // 上記以外の場合
                } else {
                    // 請求先マスタの取得
                    S02M017TEntity skysakiEntity =
                        getSkysakiMst(dto.getApkihoncd(), dto.getShopcd(), entity.getSkysakicd());

                    // 親得意先コード＝画面.得意先コードの場合、確認メッセージ
                    if (!ObjectUtils.isEmpty(skysakiEntity)
                        && !StringUtils.isEmpty(skysakiEntity.getOyatokuisakicd())
                        && skysakiEntity.getOyatokuisakicd().equals(dto.getTokuisakicd())) {
                        BusinessExceptionFactory.setMessageList(messageList, MessageID.W272);
                    }
                }
            }

            // ==============
            // 商品単価置き換えチェック
            // ==============
            // 親子反映機能=チェックONの場合
            if (!StringUtils.isEmpty(dto.getOyakohaneikinoflg())
                && !StringUtils.isEmpty(dto.getOyatokuisakicd())) {
                // 検索条件の設定
                S02M009TEntityExample s02m009tExample = new S02M009TEntityExample();
                s02m009tExample.createCriteria().andApkihoncdEqualTo(dto.getApkihoncd())
                    .andShopcdEqualTo(dto.getShopcd())
                    .andTokuisakicdEqualTo(dto.getOyatokuisakicd())
                    .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

                // 得意先別商品別単価マスタの件数取得
                long cnt = s02m009tDao.countByExample(s02m009tExample);

                // 得意先別商品別単価マスタが存在する場合
                if (cnt > 0) {
                    BusinessExceptionFactory.setMessageList(messageList, MessageID.W273);
                }
            }
        }
    }

    /**
     * 相関チェックを実行する
     * 
     * @param dto 更新用DTO
     */
    private void checkParam(B0501W02UpdateDto dto) {

        // ==============
        // 反映元得意先関連チェック
        // ==============
        // 反映元得意先コードが入力されている場合
        if (!StringUtils.isEmpty(dto.getHaneimotottcd())) {
            // 得意先マスタの取得
            S02M013TEntity entity =
                getTokuisakiMst(dto.getApkihoncd(), dto.getShopcd(), dto.getHaneimotottcd());

            // 得意先マスタに存在しないコード値はエラー
            if (ObjectUtils.isEmpty(entity)) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.haneimototokuisakicd", "反映元得意先コード"), MessageID.W084,
                    "反映元得意先コード");
            }

            // 得意先マスタ.有効区分=1(有効)以外の場合、エラー
            if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.haneimototokuisakicd", "反映元得意先コード"), MessageID.W123,
                    "反映元得意先コード");
            }
        }

        // ==============
        // 請求先マスタ関連チェック
        // ==============
        // 請求先管理区分の設定
        dto.setSkysakikanrikbn(getSkysakikanrikbn(dto.getTokuisakikbn()));

        // 請求先コードが入力されている場合
        if (!StringUtils.isEmpty(dto.getSkysakicd())) {
            // 請求先マスタの取得
            S02M017TEntity entity =
                getSkysakiMst(dto.getApkihoncd(), dto.getShopcd(), dto.getSkysakicd());

            // 請求先マスタに紐づくデータが存在しない場合、エラー
            if (ObjectUtils.isEmpty(entity)) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.skysakicd", "請求先コード"), MessageID.W084, "請求先コード");
            }

            // 請求先マスタ.有効区分=1(有効)以外の場合、エラー
            if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.skysakicd", "請求先コード"), MessageID.W123, "請求先コード");
            }

            // 得意先区分=9(経費)の場合
            boolean errflg = false;
            if (CommonConstants.KBN_TOKUISAKI_KEIHI.equals(dto.getTokuisakikbn())) {
                // 請求先マスタ.請求先管理区分≠設定済の請求先管理区分の場合
                if (!dto.getSkysakikanrikbn().equals(entity.getSkysakikanrikbn())) {
                    errflg = true;
                }
            } else {
                // 請求先マスタ.請求先管理区分≠1(請求先) /2(卸)の場合
                if (!CommonConstants.SKYSAKIKANRIKBN_1.equals(entity.getSkysakikanrikbn())
                    && !CommonConstants.SKYSAKIKANRIKBN_2.equals(entity.getSkysakikanrikbn())) {
                    errflg = true;
                }
            }
            if (errflg) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.skysakicd", "請求先コード"), MessageID.W124,
                    "得意先区分と請求先管理区分の相違がある請求先");
            }

            // 親子反映機能=チェックONの場合
            if (!StringUtils.isEmpty(dto.getOyakohaneikinoflg())
                && CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
                // 請求先マスタ．親得意先コード が NULLまたは空白の場合、エラー
                if (StringUtils.isEmpty(entity.getOyatokuisakicd())) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("baseBean.skysakicd", "請求先コード"), MessageID.W176, "親得意先コード");
                }

                // 検索条件の設定
                S02M009TEntityExample s02m009tExample = new S02M009TEntityExample();
                s02m009tExample.createCriteria().andApkihoncdEqualTo(dto.getApkihoncd())
                    .andShopcdEqualTo(dto.getShopcd())
                    .andTokuisakicdEqualTo(entity.getOyatokuisakicd())
                    .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

                // 得意先別商品別単価マスタの件数取得
                long cnt = s02m009tDao.countByExample(s02m009tExample);

                if (cnt == 0) {
                    // 検索条件の設定
                    S02M066TEntityExample s02m066tExample = new S02M066TEntityExample();
                    s02m066tExample.createCriteria().andApkihoncdEqualTo(dto.getApkihoncd())
                        .andShopcdEqualTo(dto.getShopcd())
                        .andTokuisakicdEqualTo(entity.getOyatokuisakicd())
                        .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

                    // 得意先別セット商品単価マスタの件数取得
                    cnt = s02m066tDao.countByExample(s02m066tExample);

                    // 得意先別商品別単価マスタ、得意先別セット商品単価マスタに紐づく情報がない場合、エラー
                    if (cnt == 0) {
                        throw BusinessExceptionFactory.validationFailed(
                            new FormField("baseBean.skysakicd", "請求先コード"), MessageID.W176,
                            "親得意先の商品単価");
                    }
                }

                // DTOに親得意先コードをセット
                dto.setOyatokuisakicd(entity.getOyatokuisakicd());
            }

            // 異なる得意先区分チェック
            // 検索条件の設定
            S02M013TEntity key1 = new S02M013TEntity();
            key1.setApkihoncd(dto.getApkihoncd());
            key1.setShopcd(dto.getShopcd());
            key1.setSkysakicd(dto.getSkysakicd());
            key1.setTokuisakicd(dto.getTokuisakicd());
            key1.setTokuisakikbn(dto.getTokuisakikbn());
            key1.setJutakudlvrycstkbn(dto.getJutakudlvrycstkbn());
            key1.setYukokbn(CommonConstants.YUKOKBN_1);

            // 異なる得意先区分チェックSQL実行
            long cnt = s02m013tDao.countByB0501W02ForTokuisakikbnCheck(key1);

            // 異なる得意先区分が設定された得意先に紐づく請求先コードが設定されている場合、エラー
            if (cnt > 0) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.skysakicd", "請求先コード"), MessageID.W124,
                    "異なる得意先区分が設定された得意先に紐づく請求先コード");
            }

            // 代引親子チェック
            // 回収方法区分が「代引」で、画面.請求設定区分=1(コード指定)を選択している場合
            if (CommonConstants.KAISHUHOHOKBN_7.equals(entity.getKaishuhohokbn())
                && SKYSETTEIKBN_1.equals(dto.getSkysetteikbn())) {

                // ACCIS2023-7197 請求先が代引きの場合に行う、親子判定方法を修正
                // 得意先コードと請求先コードが異なっていても親子とは限らない
                // 請求先に紐づいている得意先の件数により親子であるかの判定を行う
                // …得意先コードと請求先コードの値比較は行わない

                // 得意先マスタから請求先に紐づく得意先が存在しているか判定するため件数を取得
                // customer
                S02M013TEntityExample customerCondition = new S02M013TEntityExample();
                S02M013TEntityExample.Criteria customerCriteria =
                    customerCondition.createCriteria();
                customerCriteria
                    // AP基本コード
                    .andApkihoncdEqualTo(dto.getApkihoncd())
                    // ショップコード
                    .andShopcdEqualTo(dto.getShopcd())
                    // 請求先コード
                    .andSkysakicdEqualTo(dto.getSkysakicd());
                // 更新の場合、自身の得意先マスタレコードは除く
                if (!StringUtils.isEmpty(dto.getTokuisakicd())) {
                    // 得意先コード
                    customerCriteria.andTokuisakicdNotEqualTo(dto.getTokuisakicd());
                }
                // 紐付けようとしている請求先に別の得意先が紐づいている場合は親子請求とする
                if (s02m013tDao.countByExample(customerCondition) > 0) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("baseBean.skysakicd", "請求先コード"), MessageID.W121, "請求先コード",
                        "回収方法区分が「代引」以外の請求先");
                }
            }

            // 請求先の未締め売上チェック１～２
            if (!StringUtils.isEmpty(dto.getSkysakicdbefore())
                && !dto.getSkysakicd().equals(dto.getSkysakicdbefore())) {

                // 変更前-請求先マスタの取得
                S02M017TEntity entityBefore =
                    getSkysakiMst(dto.getApkihoncd(), dto.getShopcd(), dto.getSkysakicdbefore());

                // 変更前後-請求先マスタの回収方法区分を取得
                String kaishuhohokbnBefore = entityBefore.getKaishuhohokbn(); // 変更前-請求先に対する回収方法区分
                String kaishuhohokbn = entity.getKaishuhohokbn(); // 変更後-請求先に対する回収方法区分

                // 得意先コードに対する未締め売上げが存在するか確認
                S06T001TEntity s06T001TEntity = new S06T001TEntity();
                s06T001TEntity.setApkihoncd(dto.getApkihoncd());
                s06T001TEntity.setShopcd(dto.getShopcd());
                s06T001TEntity.setTokuisakicd(dto.getTokuisakicd());
                s06T001TEntity.setYukokbn(CommonConstants.YUKOKBN_1);
                List<S06T001TEntity> s06t001tList = s06t001tDao.selectByB0501W02(s06T001TEntity);

                if (s06t001tList != null && s06t001tList.size() > 0) {

                    // 請求先の未締め売上チェック１
                    if (!CommonConstants.KAISHUHOHOKBN_7.equals(kaishuhohokbnBefore)
                        && CommonConstants.KAISHUHOHOKBN_7.equals(kaishuhohokbn)) {
                        throw BusinessExceptionFactory.validationFailed(
                            new FormField("baseBean.skysakicd", "請求先コード"), MessageID.W171,
                            "未締めの売上がある状態で", "回収方法区分が「代引」の請求先に変更することは");
                    }

                    // 請求先の未締め売上チェック２
                    if (CommonConstants.KAISHUHOHOKBN_7.equals(kaishuhohokbnBefore)
                        && !CommonConstants.KAISHUHOHOKBN_7.equals(kaishuhohokbn)) {
                        throw BusinessExceptionFactory.validationFailed(
                            new FormField("baseBean.skysakicd", "請求先コード"), MessageID.W171,
                            "未締めの売上がある状態で", "回収方法区分が「代引」以外の請求先に変更することは");
                    }
                }
            }

            // 請求先コードが未入力の場合
        } else {
            // 画面.請求設定区分=2(自動生成)を選択している場合)
            if (SKYSETTEIKBN_2.equals(dto.getSkysetteikbn())
                && !StringUtils.isEmpty(dto.getTokuisakicd())) {
                // 請求先マスタに紐づくデータが存在する場合、エラー
                if (isExistSkysaki(dto.getApkihoncd(), dto.getShopcd(), dto.getTokuisakicd())) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("baseBean.skysakicd", "請求先コード"), MessageID.W133,
                        "請求先情報が自動生成");
                }
            }
        }

        // ==============
        // 得意先ショップコード関連チェック
        // ==============
        // 得意先区分＝2(ショップ)または、(得意先区分＝1(顧客)かつ顧客区分＝2(ショップ顧客)）の場合
        if (CommonConstants.KBN_TOKUISAKI_SHOP.equals(dto.getTokuisakikbn())
            || (CommonConstants.KBN_TOKUISAKI_KOKUAKU.equals(dto.getTokuisakikbn())
                && CommonConstants.JUTAKUDLVRYCSTKBN_2.equals(dto.getJutakudlvrycstkbn()))
                && !StringUtils.isEmpty(dto.getTokuisakishopcd())) {

            // ショップマスタの取得
            S02M046TEntity entity = getShopMst(dto.getApkihoncd(), dto.getTokuisakishopcd());

            // 得意先ショップコードがショップマスタに存在しない場合、エラー
            if (ObjectUtils.isEmpty(entity)) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.tokuisakishopcd", "得意先ショップコード"), MessageID.W084,
                    "得意先ショップコード");
            }

            // ショップマスタ.ショップ区分＝１(ショップ)以外の場合、エラー
            if (!CommonConstants.SHOP_KBN_SHOP.equals(entity.getShopkbn())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.tokuisakishopcd", "得意先ショップコード"), MessageID.W124,
                    "ショップ区分がショップ以外の場合");
            }

            // ショップマスタ.有効区分＝１(有効)以外の場合、エラー
            if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.tokuisakishopcd", "得意先ショップコード"), MessageID.W123,
                    "得意先ショップコード");
            }
        }

        // ==============
        // 委託相手得意先コード関連チェック
        // ==============
        if (!StringUtils.isEmpty(dto.getItakuaitetokuisakicd())) {
            // 得意先マスタの取得
            S02M013TEntity entity = getTokuisakiMst(dto.getItakuaiteapkihoncd(),
                dto.getItakuaiteshopcd(), dto.getItakuaitetokuisakicd());

            // 得意先マスタに存在しないコード値はエラー
            if (ObjectUtils.isEmpty(entity)) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.itakuaitetokuisakicd", "委託相手得意先コード"), MessageID.W084,
                    "委託相手得意先コード");
            }

            // 得意先マスタ.有効区分=1(有効)以外の場合、エラー
            if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.itakuaitetokuisakicd", "委託相手得意先コード"), MessageID.W123,
                    "委託相手得意先コード");
            }

            // 委託相手コードの顧客区分が不整合の場合、エラー
            if ((CommonConstants.JUTAKUDLVRYCSTKBN_3.equals(dto.getJutakudlvrycstkbn())
                && !CommonConstants.JUTAKUDLVRYCSTKBN_5.equals(entity.getJutakudlvrycstkbn()))
                || (CommonConstants.JUTAKUDLVRYCSTKBN_4.equals(dto.getJutakudlvrycstkbn())
                    && !CommonConstants.JUTAKUDLVRYCSTKBN_6.equals(entity.getJutakudlvrycstkbn()))
                || (CommonConstants.JUTAKUDLVRYCSTKBN_5.equals(dto.getJutakudlvrycstkbn())
                    && !CommonConstants.JUTAKUDLVRYCSTKBN_3.equals(entity.getJutakudlvrycstkbn()))
                || (CommonConstants.JUTAKUDLVRYCSTKBN_6.equals(dto.getJutakudlvrycstkbn())
                    && !CommonConstants.JUTAKUDLVRYCSTKBN_4
                        .equals(entity.getJutakudlvrycstkbn()))) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.itakuaitetokuisakicd", "委託相手得意先コード"), MessageID.W149,
                    "委託相手コードの顧客区分", "対象");
            }

            // 得意先マスタ.受託配達顧客区分=3(NA顧客)／4(委託顧客)／5(NA受託)／6(配達受託)で、
            // 委託相手に画面の得意先と異なる得意先が既に登録されていた場合、エラー
            if (!StringUtils.isEmpty(dto.getTokuisakicd())
                && !StringUtils.isEmpty(entity.getItakuaiteapkihoncd())
                && !StringUtils.isEmpty(entity.getItakuaiteshopcd())
                && !StringUtils.isEmpty(entity.getItakuaitetokuisakicd())
                && (CommonConstants.JUTAKUDLVRYCSTKBN_3.equals(entity.getJutakudlvrycstkbn())
                    || CommonConstants.JUTAKUDLVRYCSTKBN_4.equals(entity.getJutakudlvrycstkbn())
                    || CommonConstants.JUTAKUDLVRYCSTKBN_5.equals(entity.getJutakudlvrycstkbn())
                    || CommonConstants.JUTAKUDLVRYCSTKBN_6.equals(entity.getJutakudlvrycstkbn()))
                && (!dto.getApkihoncd().equals(entity.getItakuaiteapkihoncd())
                    || !dto.getShopcd().equals(entity.getItakuaiteshopcd())
                    || !dto.getTokuisakicd().equals(entity.getItakuaitetokuisakicd()))) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.itakuaitetokuisakicd", "委託相手得意先コード"), MessageID.W124,
                    "他の得意先を相手として登録された委託相手");
            }
        }

        // ==============
        // 会員コード関連チェック
        // ==============
        String kainsakuseiumukbn = getKainsakuseiumukbn();
        // 会員コードが未入力の場合
        if (StringUtils.isEmpty(dto.getKaiincd())) {
            // 入会日が入力され、顧客区分＝0(顧客以外)／5（ＮＡ受託）／6（配達受託）以外で、
            // 会員コード区分＝0（指定なし）、エラー
            if (!StringUtils.isEmpty(dto.getNyukaidd()) && KAIINCDKBN_0.equals(dto.getKaiincdkbn())
                && !CommonConstants.JUTAKUDLVRYCSTKBN_0.equals(dto.getJutakudlvrycstkbn())
                && !CommonConstants.JUTAKUDLVRYCSTKBN_5.equals(dto.getJutakudlvrycstkbn())
                && !CommonConstants.JUTAKUDLVRYCSTKBN_6.equals(dto.getJutakudlvrycstkbn())
                && !CommonConstants.JUTAKUDLVRYCSTKBN_8.equals(dto.getJutakudlvrycstkbn())
                && !CommonConstants.JUTAKUDLVRYCSTKBN_9.equals(dto.getJutakudlvrycstkbn())
                && !StringUtils.isEmpty(kainsakuseiumukbn)) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.kaiincd", "会員コード"), MessageID.W113, "会員コード");
            }
            // 会員コードが入力された場合
        } else {
            // 検索条件の設定
            S02M081TEntityKey key = new S02M081TEntityKey();
            key.setKaiincd(dto.getKaiincd());

            // 会員マスタの取得
            S02M081TEntity entity = s02m081tDao.selectByPrimaryKey(key);

            // 会員マスタに存在しないコード値はエラー
            if (ObjectUtils.isEmpty(entity)) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.kaiincd", "会員コード"), MessageID.W084, "会員コード");
            }

            // 会員マスタ.有効区分=1(有効)以外の場合、エラー
            if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.kaiincd", "会員コード"), MessageID.W123, "会員コード");
            }
        }

        // 会員作成有無区分が存在せず、会員コードに入力値がある、または、会員コード区分が"2"（自動生成）の場合、エラー
        if (StringUtils.isEmpty(kainsakuseiumukbn)) {
            if (!StringUtils.isEmpty(dto.getKaiincd())
                || KAIINCDKBN_2.equals(dto.getKaiincdkbn())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.kaiincd", "会員コード"), MessageID.W124, "会員作成無しの場合、会員コード");
            }
        }

        // ==============
        // 郵便番号関連チェック
        // ==============
        if (!StringUtils.isEmpty(dto.getYubinno())) {
            S02M083TEntity entity = checkYubinno(dto.getYubinno(), dto.getChihoupubdanticdn1(),
                dto.getChihoupubdanticdn2(), "baseBean.yubinno", "郵便番号");
            // 地方公共団体コードを設定する
            dto.setChihoupubdanticdn1(entity.getChihoupubdanticdn1());
            dto.setChihoupubdanticdn2(entity.getChihoupubdanticdn2());
        }

        // ==============
        // 営業エリア関連チェック
        // ==============
        if (!StringUtils.isEmpty(dto.getEigyoareacd())) {
            checkEigyoArea(dto.getApkihoncd(), dto.getShopcd(), dto.getEigyoareacd(),
                "deliveryOrderBean.eigyoareacd", "営業エリアコード");
        }

        // ==============
        // 営業担当者関連チェック
        // ==============
        if (!StringUtils.isEmpty(dto.getEigyotnshacd())) {
            checkEigyoTnsha(dto.getApkihoncd(), dto.getShopcd(), dto.getEigyotnshacd(),
                "deliveryOrderBean.eigyotnshacd", "営業担当者コード");
        }

        // ==============
        // 配達担当者関連チェック
        // ==============
        if (!StringUtils.isEmpty(dto.getDlvrytnshacd())) {
            checkDlvryTnsha(dto.getApkihoncd(), dto.getShopcd(), dto.getDlvrytnshacd(),
                dto.getDlvrykbn(), dto.getDlvrycmpnycd(), "deliveryOrderBean.dlvrytnshacd",
                "配達担当者コード");
        }

        // ==============
        // 設置日登録者関連チェック
        // ==============
        if (!StringUtils.isEmpty(dto.getStupddtoshacd())) {
            // 担当者マスタの取得
            S02M003TEntity entity = getTnshaMst(dto.getStupddtoshaapkihoncd(),
                dto.getStupddtoshashopcd(), dto.getStupddtoshacd());

            // 担当者マスタに存在しないコード値はエラー
            if (ObjectUtils.isEmpty(entity)) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.stupddtoshacd", "設置日登録者コード"), MessageID.W084,
                    "設置日登録者コード");
            }
        }

        // ==============
        // 引上登録者関連チェック
        // ==============
        if (!StringUtils.isEmpty(dto.getHikiageddtoshacd())) {
            // 担当者マスタの取得
            S02M003TEntity entity = getTnshaMst(dto.getHikiageddtoshaapkihoncd(),
                dto.getHikiageddtoshashopcd(), dto.getHikiageddtoshacd());

            // 担当者マスタに存在しないコード値はエラー
            if (ObjectUtils.isEmpty(entity)) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.hikiagetoshacd", "引上登録者コード"), MessageID.W084,
                    "引上登録者コード");
            }
        }

        // ==============
        // 本部エージェントコード関連チェック
        // ==============
        if (!StringUtils.isEmpty(dto.getHonbuejntcd())
            && CommonConstants.KBN_KIHON_ACI.equals(userSession.getApkbn())) {
            // エージェントマスタの取得
            S02M014TEntity entity = getEjentMst(
                StringUtils.isEmpty(dto.getHonbuejntapkihoncd()) ? userSession.getApkihoncd()
                    : dto.getHonbuejntapkihoncd(),
                StringUtils.isEmpty(dto.getHonbuejntapkihoncd()) ? userSession.getShopcd()
                    : dto.getHonbuejntshopcd(),
                dto.getHonbuejntcd());

            // エージェントマスタに存在しないコード値はエラー
            if (ObjectUtils.isEmpty(entity)) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("deliveryOrderBean.honbuejntcd", "本部エージェントコード"), MessageID.W084,
                    "本部エージェントコード");
            }

            // エージェントマスタ.有効区分=1(有効)以外の場合、エラー
            if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("deliveryOrderBean.honbuejntcd", "本部エージェントコード"), MessageID.W123,
                    "本部エージェントコード");
            }
        }

        // ==============
        // 獲得エージェントコード関連チェック
        // ==============
        if (!StringUtils.isEmpty(dto.getKakutokuejntcd())) {
            // エージェントマスタの取得
            S02M014TEntity entity =
                getEjentMst(dto.getApkihoncd(), dto.getShopcd(), dto.getKakutokuejntcd());

            // エージェントマスタに存在しないコード値はエラー
            if (ObjectUtils.isEmpty(entity)) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("deliveryOrderBean.kakutokuejntcd", "獲得エージェントコード"),
                    MessageID.W084, "獲得エージェントコード");
            }

            // エージェントマスタ.有効区分=1(有効)以外の場合、エラー
            if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("deliveryOrderBean.kakutokuejntcd", "獲得エージェントコード"),
                    MessageID.W123, "獲得エージェントコード");
            }

            // マージン対象区分=0(設定なし)以外の場合、エージェントマスタ.マージン対象区分と不一致の場合、エラー
            if (!CommonConstants.TOKUISAKIMGNTRGTKBN_0.equals(dto.getMgntrgtkbn())
                && !dto.getMgntrgtkbn().equals(entity.getMgntrgtkbn())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("deliveryOrderBean.kakutokuejntcd", "獲得エージェントコード"),
                    MessageID.W106, "エージェント情報の対象区分と相違がある区分");
            }

            // マージン対象区分=2(ボトル)または3(併用)の場合
            if (CommonConstants.TOKUISAKIMGNTRGTKBN_2.equals(dto.getMgntrgtkbn())
                || CommonConstants.TOKUISAKIMGNTRGTKBN_3.equals(dto.getMgntrgtkbn())) {
                // 商品コード１が獲得エージェントコードに登録されているマージン用商品コードと不一致の場合、エラー
                if (!StringUtils.isEmpty(dto.getMgnyoshncdn1())
                    && !dto.getMgnyoshncdn1().equals(entity.getMgnyoshncdn1())
                    && !dto.getMgnyoshncdn1().equals(entity.getMgnyoshncdn2())
                    && !dto.getMgnyoshncdn1().equals(entity.getMgnyoshncdn3())
                    && !dto.getMgnyoshncdn1().equals(entity.getMgnyoshncdn4())) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("saleBean.mgnyoshncdn1", "マージン用商品コード１"), MessageID.W106,
                        "エージェント情報の商品と相違がある商品コード");
                }

                // 商品コード２が獲得エージェントコードに登録されているマージン用商品コードと不一致の場合、エラー
                if (!StringUtils.isEmpty(dto.getMgnyoshncdn2())
                    && !dto.getMgnyoshncdn2().equals(entity.getMgnyoshncdn1())
                    && !dto.getMgnyoshncdn2().equals(entity.getMgnyoshncdn2())
                    && !dto.getMgnyoshncdn2().equals(entity.getMgnyoshncdn3())
                    && !dto.getMgnyoshncdn2().equals(entity.getMgnyoshncdn4())) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("saleBean.mgnyoshncdn2", "マージン用商品コード２"), MessageID.W106,
                        "エージェント情報の商品と相違がある商品コード");
                }

                // 商品コード３が獲得エージェントコードに登録されているマージン用商品コードと不一致の場合、エラー
                if (!StringUtils.isEmpty(dto.getMgnyoshncdn3())
                    && !dto.getMgnyoshncdn3().equals(entity.getMgnyoshncdn1())
                    && !dto.getMgnyoshncdn3().equals(entity.getMgnyoshncdn2())
                    && !dto.getMgnyoshncdn3().equals(entity.getMgnyoshncdn3())
                    && !dto.getMgnyoshncdn3().equals(entity.getMgnyoshncdn4())) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("saleBean.mgnyoshncdn3", "マージン用商品コード３"), MessageID.W106,
                        "エージェント情報の商品と相違がある商品コード");
                }

                // 商品コード４が獲得エージェントコードに登録されているマージン用商品コードと不一致の場合、エラー
                if (!StringUtils.isEmpty(dto.getMgnyoshncdn4())
                    && !dto.getMgnyoshncdn4().equals(entity.getMgnyoshncdn1())
                    && !dto.getMgnyoshncdn4().equals(entity.getMgnyoshncdn2())
                    && !dto.getMgnyoshncdn4().equals(entity.getMgnyoshncdn3())
                    && !dto.getMgnyoshncdn4().equals(entity.getMgnyoshncdn4())) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("saleBean.mgnyoshncdn4", "マージン用商品コード４"), MessageID.W106,
                        "エージェント情報の商品と相違がある商品コード");
                }
            }

            // 本部エージェントコード＝入力あり、
            // 獲得エージェントコードがエージェントマスタ.社内担当区分＝ 1(社内担当者)の場合、エラー
            if (!StringUtils.isEmpty(dto.getHonbuejntcd())
                && CommonConstants.SHANAITANTOKBN_1.equals(entity.getShanaitantokbn())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("deliveryOrderBean.kakutokuejntcd", "獲得エージェントコード"),
                    MessageID.W106, "獲得エージェントに社内担当者");
            }
        }

        // ==============
        // 獲得担当者関連チェック
        // ==============
        if (!StringUtils.isEmpty(dto.getKakutokutnshacd())) {
            checkTnsha(dto.getApkihoncd(), dto.getShopcd(), dto.getKakutokutnshacd(),
                "deliveryOrderBean.kakutokutnshacd", "獲得担当者コード");
        }

        // ==============
        // 獲得本部キャンペーンコード関連チェック
        // ==============
        if (!StringUtils.isEmpty(dto.getKakutokuhonbukynpncd())) {
            // キャンペーンマスタの取得
            S02M059TEntity entity = getCampaignMst(CommonConstants.APCD_001,
                CommonConstants.SYS_SHOPCD_000, dto.getKakutokuhonbukynpncd());

            // キャンペーンマスタに存在しないコード値はエラー
            if (ObjectUtils.isEmpty(entity)) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("deliveryOrderBean.kakutokuhonbukynpncd", "獲得本部キャンペーンコード"),
                    MessageID.W177, "該当する獲得本部キャンペーンコード");
            }

            // キャンペーンマスタ.有効区分=1(有効)以外の場合、エラー
            if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("deliveryOrderBean.kakutokuhonbukynpncd", "獲得本部キャンペーンコード"),
                    MessageID.W177, "該当する獲得本部キャンペーンコード");
            }

            // キャンペーンマスタ.キャンペーン区分＝3(CS)の場合、エラー
            if (CommonConstants.KYNPNKBN_3.equals(entity.getKynpnkbn())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("deliveryOrderBean.kakutokuhonbukynpncd", "獲得本部キャンペーンコード"),
                    MessageID.W177, "該当する獲得本部キャンペーンコード");
            }
        }

        // ==============
        // 獲得キャンペーンコード関連チェック
        // ==============
        if (!StringUtils.isEmpty(dto.getKakutokukynpncd())) {
            // キャンペーンマスタの取得
            S02M059TEntity entity =
                getCampaignMst(dto.getApkihoncd(), dto.getShopcd(), dto.getKakutokukynpncd());

            // キャンペーンマスタに存在しないコード値はエラー
            if (ObjectUtils.isEmpty(entity)) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("deliveryOrderBean.kakutokukynpncd", "獲得キャンペーンコード"),
                    MessageID.W084, "獲得キャンペーンコード");
            }

            // キャンペーンマスタ.有効区分=1(有効)以外の場合、エラー
            if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("deliveryOrderBean.kakutokukynpncd", "獲得キャンペーンコード"),
                    MessageID.W123, "獲得キャンペーンコード");
            }

            // キャンペーンマスタ.キャンペーン区分＝3(CS)の場合、エラー
            if (CommonConstants.KYNPNKBN_3.equals(entity.getKynpnkbn())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("deliveryOrderBean.kakutokukynpncd", "獲得キャンペーンコード"),
                    MessageID.W084, "獲得キャンペーンコード");
            }
        }

        // ==============
        // 獲得詳細コード関連チェック
        // ==============
        if (!StringUtils.isEmpty(dto.getHansokushscd())) {
            // 検索条件の設定
            S02M027TEntityKey key = new S02M027TEntityKey();
            key.setApkihoncd(dto.getApkihoncd());
            key.setShopcd(dto.getShopcd());
            key.setHansokushscd(dto.getHansokushscd());

            // 得意先獲得販促詳細マスタの取得
            S02M027TEntity entity = s02m027tDao.selectByPrimaryKey(key);

            // 得意先獲得販促詳細マスタに存在しないコード値はエラー
            if (ObjectUtils.isEmpty(entity)) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("deliveryOrderBean.hansokushscd", "獲得詳細コード"), MessageID.W084,
                    "獲得詳細コード");
            }

            // 得意先獲得販促詳細マスタ.有効区分=1(有効)以外の場合、エラー
            if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("deliveryOrderBean.hansokushscd", "獲得詳細コード"), MessageID.W123,
                    "獲得詳細コード");
            }
        }

        // ==============
        // 紹介元得意先関連チェック
        // ==============
        if (!StringUtils.isEmpty(dto.getShoukaitokuisakicd())) {
            // 得意先マスタの取得
            S02M013TEntity entity =
                getTokuisakiMst(dto.getApkihoncd(), dto.getShopcd(), dto.getShoukaitokuisakicd());

            // 得意先マスタに存在しないコード値はエラー
            if (ObjectUtils.isEmpty(entity)) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("deliveryOrderBean.shoukaitokuisakicd", "紹介元得意先コード"),
                    MessageID.W084, "紹介元得意先コード");
            }

            // 得意先マスタ.有効区分=1(有効)以外の場合、エラー
            if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("deliveryOrderBean.shoukaitokuisakicd", "紹介元得意先コード"),
                    MessageID.W123, "紹介元得意先コード");
            }
        }

        // ==============
        // ワンショットコード関連チェック
        // ==============
        // マージン対象区分＝0(設定なし)または1(ワンショット)または3(併用)を選択し、
        // ワンショットコードに入力がある場合
        if ((CommonConstants.TOKUISAKIMGNTRGTKBN_0.equals(dto.getMgntrgtkbn())
            || CommonConstants.TOKUISAKIMGNTRGTKBN_1.equals(dto.getMgntrgtkbn())
            || CommonConstants.TOKUISAKIMGNTRGTKBN_3.equals(dto.getMgntrgtkbn()))
            && !StringUtils.isEmpty(dto.getWanshottocd())) {
            // マージンマスタの取得
            S02M015TEntity entity = getMgnMst(dto.getApkihoncd(), dto.getShopcd(),
                dto.getWanshottocd(), CommonConstants.KBN_MARGINTARGET_ONESHOT);

            // マージンマスタに存在しないコード値はエラー
            if (ObjectUtils.isEmpty(entity)) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("saleBean.wanshottocd", "ワンショットコード"), MessageID.W084,
                    "ワンショットコード");
            }

            // マージンマスタ.有効区分=1(有効)以外の場合、エラー
            if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("saleBean.wanshottocd", "ワンショットコード"), MessageID.W123,
                    "ワンショットコード");
            }
        }

        // マージン用商品コード１関連チェック
        if (!StringUtils.isEmpty(dto.getMgnyoshncdn1())) {
            checkMgnyoShnCd(dto.getApkihoncd(), dto.getShopcd(), dto.getMgnyoshncdn1(),
                "saleBean.mgnyoshncdn1", "マージン用商品コード１");
        }

        // マージン用商品コード２関連チェック
        if (!StringUtils.isEmpty(dto.getMgnyoshncdn2())) {
            checkMgnyoShnCd(dto.getApkihoncd(), dto.getShopcd(), dto.getMgnyoshncdn2(),
                "saleBean.mgnyoshncdn2", "マージン用商品コード２");
        }

        // マージン用商品コード３関連チェック
        if (!StringUtils.isEmpty(dto.getMgnyoshncdn3())) {
            checkMgnyoShnCd(dto.getApkihoncd(), dto.getShopcd(), dto.getMgnyoshncdn3(),
                "saleBean.mgnyoshncdn3", "マージン用商品コード３");
        }

        // マージン用商品コード４関連チェック
        if (!StringUtils.isEmpty(dto.getMgnyoshncdn4())) {
            checkMgnyoShnCd(dto.getApkihoncd(), dto.getShopcd(), dto.getMgnyoshncdn4(),
                "saleBean.mgnyoshncdn4", "マージン用商品コード４");
        }

        // マージン対象区分＝0(設定なし)またはマージン対象区分＝2(ボトル)または3(併用)を選択した場合
        if ((CommonConstants.TOKUISAKIMGNTRGTKBN_0.equals(dto.getMgntrgtkbn())
            || CommonConstants.TOKUISAKIMGNTRGTKBN_2.equals(dto.getMgntrgtkbn())
            || CommonConstants.TOKUISAKIMGNTRGTKBN_3.equals(dto.getMgntrgtkbn()))) {
            // マージンコード１関連チェック
            if (!StringUtils.isEmpty(dto.getMgncdn1())) {
                checkMgnCd(dto.getApkihoncd(), dto.getShopcd(), dto.getMgncdn1(),
                    "saleBean.mgncdn1", "マージンコード１");
            }

            // マージンコード２関連チェック
            if (!StringUtils.isEmpty(dto.getMgncdn2())) {
                checkMgnCd(dto.getApkihoncd(), dto.getShopcd(), dto.getMgncdn2(),
                    "saleBean.mgncdn2", "マージンコード２");
            }

            // マージンコード３関連チェック
            if (!StringUtils.isEmpty(dto.getMgncdn3())) {
                checkMgnCd(dto.getApkihoncd(), dto.getShopcd(), dto.getMgncdn3(),
                    "saleBean.mgncdn3", "マージンコード３");
            }

            // マージンコード４関連チェック
            if (!StringUtils.isEmpty(dto.getMgncdn4())) {
                checkMgnCd(dto.getApkihoncd(), dto.getShopcd(), dto.getMgncdn4(),
                    "saleBean.mgncdn4", "マージンコード4");
            }
        }

        // ==============
        // サブレンタル倉庫コード関連チェック
        // ==============
        // （得意先区分＝1(顧客)かつ 顧客区分＝2(ショップ顧客)）を選択または得意先区分＝ 2(ショップ)を選択した場合
        if ((CommonConstants.KBN_TOKUISAKI_KOKUAKU.equals(dto.getTokuisakikbn())
            && CommonConstants.JUTAKUDLVRYCSTKBN_2.equals(dto.getJutakudlvrycstkbn()))
            || CommonConstants.KBN_TOKUISAKI_SHOP.equals(dto.getTokuisakikbn())) {
            // 検索条件の設定
            S02M041TEntityKey key = new S02M041TEntityKey();
            key.setApkihoncd(dto.getApkihoncd());
            key.setShopcd(dto.getShopcd());
            key.setSokocd(dto.getSaburtlsokocd());

            // 倉庫マスタの取得
            S02M041TEntity entity = s02m041tDao.selectByPrimaryKey(key);

            // 倉庫マスタに存在しないコード値はエラー
            if (ObjectUtils.isEmpty(entity)) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.saburtlsokocd", "サブレンタル倉庫コード"), MessageID.W084,
                    "サブレンタル倉庫コード");
            }

            // 倉庫マスタ.有効区分=1(有効)以外の場合、エラー
            if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.saburtlsokocd", "サブレンタル倉庫コード"), MessageID.W123,
                    "サブレンタル倉庫コード");
            }

            // サブレンタル倉庫区分以外の場合、エラー
            if (!CommonConstants.SOKOKBN_SUBRET.equals(entity.getSokokbn())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.saburtlsokocd", "サブレンタル倉庫コード"), MessageID.W120,
                    "サブレンタル倉庫コード", "倉庫区分がサブレンタルの倉庫");
            }
        }

        // ==============
        // 配達拠点コード関連チェック
        // ==============
        if (!StringUtils.isEmpty(dto.getDlvrykankatsukyotencd())) {
            List<FormField> fields = new ArrayList<>();
            addField(fields, "deliveryOrderBean.dlvrykankatsukyotencd", "配達管轄拠点コード");
            addField(fields, "deliveryOrderBean.dlvrykankatsuapkihoncd", "配達管轄AP基本コード");
            addField(fields, "deliveryOrderBean.dlvrykankatsushopcd", "配達管轄ショップコード");
            checkDlvryKyoten(dto.getApkihoncd(), dto.getShopcd(), dto.getDlvrykankatsuapkihoncd(),
                dto.getDlvrykankatsushopcd(), dto.getDlvrykankatsukyotencd(), dto.getTokuisakikbn(),
                dto.getJutakudlvrycstkbn(), fields);
        }

        // ==============
        // 配達エリアコード関連チェック
        // ==============
        if (!StringUtils.isEmpty(dto.getDlvryareacd())) {
            checkDlvryArea(dto.getDlvrykankatsuapkihoncd(), dto.getDlvrykankatsushopcd(),
                dto.getDlvrykankatsukyotencd(), dto.getDlvryareacd(), dto.getUnyoDate(),
                "deliveryOrderBean.dlvryareacd", "配達エリアコード");
        }

        // ==============
        // 配達会社コード関連チェック
        // ==============
        if (!StringUtils.isEmpty(dto.getDlvrycmpnycd())) {
            checkDlvryCmpny(dto.getApkihoncd(), dto.getShopcd(), dto.getDlvrycmpnycd(),
                "deliveryOrderBean.dlvrycmpnycd", "配達会社コード");
        }

        // 自動値引コード１関連チェック
        if (!StringUtils.isEmpty(dto.getJidonebikicdn1())) {
            checkJidonebikiCd(dto.getApkihoncd(), dto.getShopcd(), dto.getJidonebikicdn1(),
                "saleBean.jidonebikicd1", "自動値引きコード１");
        }

        // 自動値引コード２関連チェック
        if (!StringUtils.isEmpty(dto.getJidonebikicdn2())) {
            checkJidonebikiCd(dto.getApkihoncd(), dto.getShopcd(), dto.getJidonebikicdn2(),
                "saleBean.jidonebikicd2", "自動値引きコード２");
        }

        // 自動値引コード３関連チェック
        if (!StringUtils.isEmpty(dto.getJidonebikicdn3())) {
            checkJidonebikiCd(dto.getApkihoncd(), dto.getShopcd(), dto.getJidonebikicdn3(),
                "saleBean.jidonebikicd3", "自動値引きコード３");
        }

        // 有効区分＝0(無効)を選択した場合
        if (CommonConstants.YUKOKBN_0.equals(dto.getYukokbn())) {
            // ==============
            // 得意先設置サーバー関連チェック
            // ==============
            // 状態ステータス＝02(設置前キャンセル)/06(退会済み)/10(退会済み)の場合
            if ((CommonConstants.KBN_JOTAISTAT_2.equals(dto.getJotaists())
                || CommonConstants.KBN_JOTAISTAT_6.equals(dto.getJotaists())
                || CommonConstants.KBN_JOTAISTAT_10.equals(dto.getJotaists()))) {

                // 検索条件の設定
                S02M048TEntityExample example = new S02M048TEntityExample();
                example.createCriteria().andApkihoncdEqualTo(dto.getApkihoncd())
                    .andShopcdEqualTo(dto.getShopcd()).andTokuisakicdEqualTo(dto.getTokuisakicd());

                // 得意先設置サーバー管理マスタの取得
                List<S02M048TEntity> results = s02m048tDao.selectByExample(example);

                if (!ObjectUtils.isEmpty(results) && results.size() > 0) {
                    for (S02M048TEntity e : results) {
                        // 引上済でなサーバが1件でも存在した場合、エラー
                        if (StringUtils.isEmpty(e.getHikiagedd())) {
                            throw BusinessExceptionFactory.validationFailed(
                                new FormField("baseBean.yukokbn", "有効区分"), MessageID.W166);
                        }
                    }
                }

                // 状態ステータス≠02(設置前キャンセル)/06(退会済み)/10(退会済み)の場合、エラー
            } else {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.yukokbn", "有効区分"), MessageID.W132,
                    "状態ステータスが設置前キャンセル、退会済（仮）、退会済（本）以外", "無効に");
            }

            // ==============
            // 未締め売上チェック
            // ==============
            // 検索条件の設定
            S06T001TEntityExample example1 = new S06T001TEntityExample();
            example1.createCriteria().andApkihoncdEqualTo(dto.getApkihoncd())
                .andShopcdEqualTo(dto.getShopcd()).andTokuisakicdEqualTo(dto.getTokuisakicd())
                .andGetsujishimeddIsNull().andGetsujiprcsjissiddEqualTo("00000000")
                .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

            // 売上テーブルの取得
            List<S06T001TEntity> results1 = s06t001tDao.selectByExample(example1);

            // 未締め売上が存在する場合、エラー
            if (!ObjectUtils.isEmpty(results1) && results1.size() > 0) {
                throw BusinessExceptionFactory
                    .validationFailed(new FormField("baseBean.yukokbn", "有効区分"), MessageID.W189);
            }

            // ==============
            // 未締め入金チェック
            // ==============
            // 検索条件の設定
            S10T001TEntityExample example2 = new S10T001TEntityExample();
            example2.createCriteria().andApkihoncdEqualTo(dto.getApkihoncd())
                .andShopcdEqualTo(dto.getShopcd()).andTokuisakicdEqualTo(dto.getTokuisakicd())
                .andGetsujishimeddIsNull().andGetsujiprcsjissiddEqualTo("00000000")
                .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

            // 入金明細テーブルの取得
            List<S10T001TEntity> results2 = s10t001tDao.selectByExample(example2);

            // 未締め入金が存在する場合、エラー
            if (!ObjectUtils.isEmpty(results2) && results2.size() > 0) {
                throw BusinessExceptionFactory
                    .validationFailed(new FormField("baseBean.yukokbn", "有効区分"), MessageID.W190);
            }
        }

        // ==============
        // 引上日関連チェック
        // ==============
        if (!StringUtils.isEmpty(dto.getWsvhikiagedd())) {
            // 得意先実績テーブルの取得
            S02M050TEntity entity =
                getTokuisakiJisseki(dto.getApkihoncd(), dto.getShopcd(), dto.getTokuisakicd());

            // 得意先実績テーブル.前回配達日＞引上日の場合、エラー
            if (!ObjectUtils.isEmpty(entity) && !StringUtils.isEmpty(entity.getZnkdlvrydd())
                && entity.getZnkdlvrydd().compareTo(dto.getWsvhikiagedd()) > 0) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.wsvhikiagedd", "引上日"), MessageID.W124,
                    "引上日に前回配達日より過去の日付");
            }
        }

        // ==============
        // プラン初回設定日関連チェック
        // ==============
        // 検索条件の設定
        S02M006TEntityExample entityExample = new S02M006TEntityExample();
        Criteria criteria = entityExample.createCriteria();
        criteria.andApkihoncdEqualTo(dto.getApkihoncd()).andShopcdEqualTo(dto.getShopcd())
            .andSetteinoEqualTo(CommonConstants.SYS_SETTEINO_158)
            .andSetteicdEqualTo(CommonConstants.SETTEICD_158_1)
            .andRcrdkbnEqualTo(CommonConstants.RECORD_KBN_M);

        // 最大未来日を取得する
        List<S02M006TEntity> entities = s02m006tDao.selectByExample(entityExample);

        long nissu = 0;
        String maxMiraibi = "";
        if (!ObjectUtils.isEmpty(entities) && entities.size() > 0) {
            nissu = entities.get(0).getSuchisetteiatai().longValue();
            // 運用日付に数値設定値(未来日数)を足した日付を最大未来日とする
            maxMiraibi = DateTimeUtil.calcYMD(dto.getUnyoDate(), nissu);
        }

        if (!StringUtils.isEmpty(dto.getRknplnsetteidd())) {
            // プラン初回設定日＞最大未来日の場合、エラー
            if (!StringUtils.isEmpty(maxMiraibi)
                && dto.getRknplnsetteidd().compareTo(maxMiraibi) > 0) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.rknplnsetteidd", "プラン初回設定日"), MessageID.W120,
                    "プラン初回設定日", String.valueOf(nissu) + "日以内の日付");
            }
        }

        // ==============
        // プラン継続更新日関連チェック
        // ==============
        if (!StringUtils.isEmpty(dto.getRknplnkeizokukshndd())) {
            // プラン継続更新日＞最大未来日の場合、エラー
            if (!StringUtils.isEmpty(maxMiraibi)
                && dto.getRknplnkeizokukshndd().compareTo(maxMiraibi) > 0) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.rknplnkeizokukshndd", "プラン継続更新日"), MessageID.W120,
                    "プラン継続更新日", String.valueOf(nissu) + "日以内の日付");
            }
        }

        // ==============
        // 引上日以降経過日数チェック
        // ==============
        if (!StringUtils.isEmpty(dto.getTokuisakicd())) {
            checkHikiageKeikaNissu(dto);
        }

        // ==============
        // 得意先外部識別コード関連チェック
        // ==============
        // 得意先外部識別コード①の重複チェック
        if (!StringUtils.isEmpty(dto.getTokuisakigaibuskbtscd())) {
            checkTokuisakiGaibuskbtsCd(dto.getApkihoncd(), dto.getShopcd(), dto.getTokuisakicd(),
                dto.getTokuisakigaibuskbtscd(), "saleBean.tokuisakigaibuskbtscd1", "得意先外部識別コード①");
        }

        // 得意先外部識別コード②の重複チェック
        if (!StringUtils.isEmpty(dto.getTokuisakigaibuskbtscd2())) {
            checkTokuisakiGaibuskbtsCd(dto.getApkihoncd(), dto.getShopcd(), dto.getTokuisakicd(),
                dto.getTokuisakigaibuskbtscd2(), "saleBean.tokuisakigaibuskbtscd2", "得意先外部識別コード②");
        }

        // 得意先外部識別コード③の重複チェック
        if (!StringUtils.isEmpty(dto.getTokuisakigaibuskbtscd3())) {
            checkTokuisakiGaibuskbtsCd(dto.getApkihoncd(), dto.getShopcd(), dto.getTokuisakicd(),
                dto.getTokuisakigaibuskbtscd3(), "saleBean.tokuisakigaibuskbtscd3", "得意先外部識別コード③");
        }

        // ==============
        // 委託顧客予約情報チェック
        // ==============
        // 更新の場合かつ顧客区分＝4(委託顧客)を選択した場合
        if (!StringUtils.isEmpty(dto.getTokuisakicd())
            && CommonConstants.JUTAKUDLVRYCSTKBN_4.equals(dto.getJutakudlvrycstkbn())) {
            // 検索条件の設定
            S04T001TC0805W01Entity jhKey = new S04T001TC0805W01Entity();
            jhKey.setApkihoncd(dto.getApkihoncd());
            jhKey.setShopcd(dto.getShopcd());
            jhKey.setTokuisakicd(dto.getTokuisakicd());
            jhKey.setUnyodate(dto.getUnyoDate());

            // 受注予約伝票情報の取得
            S04T001TC0805W01Entity jhEntity = s04t001tDao.selectByC0805W01getAcptdnpydata(jhKey);

            // 受注予約伝票情報が存在する場合
            if (!ObjectUtils.isEmpty(jhEntity)) {
                // 検索条件の設定
                S04T002TC0805W01Entity jhmKey = new S04T002TC0805W01Entity();
                jhmKey.setApkihoncd(jhEntity.getApkihoncd());
                jhmKey.setShopcd(jhEntity.getShopcd());
                jhmKey.setAcptdnpyno(jhEntity.getAcptdnpyno());
                jhmKey.setEdano(jhEntity.getEdano());
                jhmKey.setKakakugetbasedd(jhEntity.getDlvryytidd());

                List<S04T002TC0805W01Entity> jhmEntityList =
                    s04t002tDao.selectByC0805W01getJuchuMeisai(jhmKey);

                // 受注予約伝票情報が存在する場合
                if (!ObjectUtils.isEmpty(jhmEntityList) && jhmEntityList.size() > 0) {
                    for (S04T002TC0805W01Entity e : jhmEntityList) {
                        // 検索条件の設定
                        S02V001TEntityExample example = new S02V001TEntityExample();
                        example.createCriteria().andApkihoncdEqualTo(dto.getApkihoncd())
                            .andShopcdEqualTo(dto.getShopcd()).andShncdEqualTo(e.getShncd())
                            .andShnzokuseiEqualTo(CommonConstants.KBN_SHNZOKUSEI_AP);

                        // 商品マスタビューの取得
                        List<S02V001TEntity> shmvList = s02v001tDao.selectByExample(example);

                        // 商品が存在する場合、エラー
                        if (!ObjectUtils.isEmpty(shmvList) && shmvList.size() > 0) {
                            throw BusinessExceptionFactory.validationFailed(
                                new FormField("baseBean.jutakudlvrycstkbn", "顧客区分"), MessageID.W105,
                                "受注予約情報にAP／ショップの商品");
                        }
                    }
                }
            }
        }

        // 営業付帯情報
        // ==============
        // 勤務先郵便番号関連チェック
        // ==============
        if (!StringUtils.isEmpty(dto.getKinmusakiyubinno())) {
            S02M083TEntity entity = checkYubinno(dto.getKinmusakiyubinno(),
                dto.getKinmusakichihoupubdanticdn1(), dto.getKinmusakichihoupubdanticdn2(),
                "additionalBean.kinmusakiyubinno", "勤務先郵便番号");
            // 地方公共団体コードを設定する
            dto.setKinmusakichihoupubdanticdn1(entity.getChihoupubdanticdn1());
            dto.setKinmusakichihoupubdanticdn2(entity.getChihoupubdanticdn2());
        }

        // 定例売上仕入情報
        // 設定する＝1(チェックON)の場合
        if (!StringUtils.isEmpty(dto.getTeireisetteikbn())) {
            // ==============
            // 実績担当者関連チェック
            // ==============
            if (!StringUtils.isEmpty(dto.getJissekitnshacd())) {
                // 担当者マスタの取得
                S02M003TEntity entity =
                    getTnshaMst(dto.getApkihoncd(), dto.getShopcd(), dto.getJissekitnshacd());

                // 担当者マスタに存在しないコード値はエラー
                if (ObjectUtils.isEmpty(entity)) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("rentalBean.jissekitnshacd", "実績担当者コード"), MessageID.W177,
                        "該当する実績担当者コード");
                }

                // 担当者マスタ.有効区分=1(有効)以外の場合、エラー
                if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("rentalBean.jissekitnshacd", "実績担当者コード"), MessageID.W123,
                        "実績担当者コード");
                }
            }

            // 有効区分=1(有効)の場合
            if (CommonConstants.YUKOKBN_1.equals(dto.getYukokbn1())
                && !StringUtils.isEmpty(dto.getTokuisakicd())) {
                // ==============
                // 売上設定チェック
                // ==============
                // 検索条件の設定
                S02M016TEntityExample entityExample1 = new S02M016TEntityExample();
                jp.co.aquaclara.accis.common.domain.repository.entity.S02M016TEntityExample.Criteria criteria1 =
                    entityExample1.createCriteria();
                String rtlrknshukeikbn =
                    URISETTEI_0.equals(dto.getUrisettei()) ? CommonConstants.RTLRKNSHUKEIKBN_1
                        : CommonConstants.RTLRKNSHUKEIKBN_0;

                criteria1.andApkihoncdEqualTo(dto.getApkihoncd()).andShopcdEqualTo(dto.getShopcd())
                    .andTokuisakicdEqualTo(dto.getTokuisakicd())
                    .andTeireikbnEqualTo(CommonConstants.TEIREIKBN_1)
                    .andUrisiirekbnEqualTo(CommonConstants.URISIIREKBN_1)
                    .andRtlrknshukeikbnEqualTo(rtlrknshukeikbn)
                    .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

                if (!StringUtils.isEmpty(dto.getTeireihasseino())) {
                    criteria1.andTeireihasseinoNotEqualTo(dto.getTeireihasseino());
                }

                // 定例売上仕入マスタの件数取得
                long cnt1 = s02m016tDao.countByExample(entityExample1);

                // 異なる売上設定のデータが登録されていた場合、エラー
                if (cnt1 > 0) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("rentalBean.yukokbn1", "有効区分"), MessageID.W133,
                        "異なる売上設定のデータが登録");
                }

                // 売上設定＝1(得意先設置サーバーマスタ)の場合
                if (URISETTEI_1.equals(dto.getUrisettei())) {
                    // 検索条件の設定
                    S02M016TEntityExample entityExample2 = new S02M016TEntityExample();
                    jp.co.aquaclara.accis.common.domain.repository.entity.S02M016TEntityExample.Criteria criteria2 =
                        entityExample2.createCriteria();

                    criteria2.andApkihoncdEqualTo(dto.getApkihoncd())
                        .andShopcdEqualTo(dto.getShopcd())
                        .andTokuisakicdEqualTo(dto.getTokuisakicd())
                        .andUrisiirekbnEqualTo(CommonConstants.URISIIREKBN_1)
                        .andRtlrknshukeikbnEqualTo(CommonConstants.RTLRKNSHUKEIKBN_1)
                        .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

                    if (!StringUtils.isEmpty(dto.getTeireihasseino())) {
                        criteria2.andTeireihasseinoNotEqualTo(dto.getTeireihasseino());
                    }

                    // 定例売上仕入マスタの件数取得
                    long cnt2 = s02m016tDao.countByExample(entityExample2);

                    // 既にデータが登録されていた場合、エラー
                    if (cnt2 > 0) {
                        throw BusinessExceptionFactory.validationFailed(
                            new FormField("rentalBean.yukokbn1", "有効区分"), MessageID.W133,
                            "売上設定：得意先設置サーバーのデータが登録");
                    }
                }
            }

            // ==============
            // 定例売上仕入処理中チェック
            // ==============
            // 検索条件の設定
            S06T003TEntityKey key = new S06T003TEntityKey();
            key.setApkihoncd(dto.getApkihoncd());
            key.setShopcd(dto.getShopcd());

            // 定例売上仕入状態テーブルを取得する
            S06T003TEntity entity = s06t003tDao.selectForUpdateNowaitByPrimaryKey(key);

            // 定例売上仕入状態テーブル.処理状態＝1(処理中) の場合、エラー
            if (!ObjectUtils.isEmpty(entity)
                && CommonConstants.PRCSJOTAI_1.equals(entity.getPrcsjotai())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("rentalBean.teireisetteikbn", "設定する"), MessageID.W171,
                    "定例売上仕入処理を実施中", "定例売上仕入マスタの更新は");
            }

            // ==============
            // 明細備考入力チェック
            // ==============
            if (!StringUtils.isEmpty(dto.getMeisaibiko())) {
                // 検索条件の設定
                S02M006TEntityExample entityExample1 = new S02M006TEntityExample();
                Criteria criteria1 = entityExample1.createCriteria();
                criteria1.andApkihoncdEqualTo(dto.getApkihoncd()).andShopcdEqualTo(dto.getShopcd())
                    .andSetteinoEqualTo(CommonConstants.SYS_SETTEINO_917)
                    .andSetteicdEqualTo(CommonConstants.SETTEICD_917_1)
                    .andRcrdkbnEqualTo(CommonConstants.RECORD_KBN_M);

                // 明細備考を取得する
                List<S02M006TEntity> entities1 = s02m006tDao.selectByExample(entityExample1);

                if (!ObjectUtils.isEmpty(entities1) && entities1.size() > 0
                    && !ObjectUtils.isEmpty(entities1.get(0).getSuchisetteiatai())) {
                    int ret = entities1.get(0).getSuchisetteiatai().intValue();
                    // 明細備考が17桁以上入力ありの場合、エラー
                    if (ret == 1 && !ItemUtil.isByteSizeValid(dto.getMeisaibiko(), 16)) {
                        throw BusinessExceptionFactory.validationFailed(
                            new FormField("rentalBean.meisaibiko", "明細備考"), MessageID.W120,
                            "明細備考の文字数", "16桁以下");
                    }
                }
            }
        }

        // 得意先マスタ変更＝1(チェックON)の場合
        if (!StringUtils.isEmpty(dto.getTokuisakimasutahnkflg())) {
            // ==============
            // 得意先郵便番号関連チェック
            // ==============
            if (!StringUtils.isEmpty(dto.getYubinno1())) {
                S02M083TEntity entity = checkYubinno(dto.getYubinno1(),
                    dto.getTokuisakichihoupubdanticdn1(), dto.getTokuisakichihoupubdanticdn2(),
                    "reservationBean.yubinno1", "得意先郵便番号");
                // 地方公共団体コードを設定する
                dto.setTokuisakichihoupubdanticdn1(entity.getChihoupubdanticdn1());
                dto.setTokuisakichihoupubdanticdn2(entity.getChihoupubdanticdn2());
            }

            // ==============
            // 営業エリア関連チェック
            // ==============
            if (!StringUtils.isEmpty(dto.getEigyoareacd1())) {
                checkEigyoArea(dto.getApkihoncd(), dto.getShopcd(), dto.getEigyoareacd1(),
                    "reservationBean.eigyoareacd1", "変更得意先営業エリアコード");
            }

            // ==============
            // 営業担当者関連チェック
            // ==============
            if (!StringUtils.isEmpty(dto.getEigyotnshacd1())) {
                checkEigyoTnsha(dto.getApkihoncd(), dto.getShopcd(), dto.getEigyotnshacd1(),
                    "reservationBean.eigyotnshacd1", "変更得意先営業担当者コード");
            }

            // ==============
            // 配達担当者関連チェック
            // ==============
            if (!StringUtils.isEmpty(dto.getDlvrytnshacd1())) {
                checkDlvryTnsha(dto.getApkihoncd(), dto.getShopcd(), dto.getDlvrytnshacd1(),
                    dto.getDlvrykbn1(), dto.getDlvrycmpnycd1(), "reservationBean.dlvrytnshacd1",
                    "変更得意先配達担当者コード");
            }

            // ==============
            // 配達拠点ＡＰ基本コード関連チェック
            // ==============
            if (!StringUtils.isEmpty(dto.getDlvrykankatsuapkihoncd1())) {
                // 検索条件の設定
                S02M001TEntityKey key = new S02M001TEntityKey();
                key.setApkihoncd(dto.getDlvrykankatsuapkihoncd1());

                // ＡＰ基本マスタの取得
                S02M001TEntity entity = s02m001tDao.selectByPrimaryKey(key);

                // ＡＰ基本マスタに存在しないコード値はエラー
                if (ObjectUtils.isEmpty(entity)) {
                    throw BusinessExceptionFactory
                        .validationFailed(new FormField("reservationBean.dlvrykankatsuapkihoncd1",
                            "変更得意先配達管轄ＡＰ基本コード"), MessageID.W084, "変更得意先配達管轄ＡＰ基本コード");
                }

                // ＡＰ基本マスタ.有効区分=1(有効)以外の場合、エラー
                if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
                    throw BusinessExceptionFactory
                        .validationFailed(new FormField("reservationBean.dlvrykankatsuapkihoncd1",
                            "変更得意先配達管轄ＡＰ基本コード"), MessageID.W123, "変更得意先配達管轄ＡＰ基本コード");
                }
            }

            // ==============
            // 配達拠点ショップコード関連チェック
            // ==============
            if (!StringUtils.isEmpty(dto.getDlvrykankatsushopcd1())) {
                // ショップマスタの取得
                S02M046TEntity entity =
                    getShopMst(dto.getDlvrykankatsuapkihoncd1(), dto.getDlvrykankatsushopcd1());

                // ショップマスタに存在しないコード値はエラー
                if (ObjectUtils.isEmpty(entity)) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("reservationBean.dlvrykankatsushopcd1", "変更得意先配達管轄ショップコード"),
                        MessageID.W084, "変更得意先配達管轄ショップコード");
                }

                // ショップマスタ.有効区分=1(有効)以外の場合、エラー
                if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("reservationBean.dlvrykankatsushopcd1", "変更得意先配達管轄ショップコード"),
                        MessageID.W123, "変更得意先配達管轄ショップコード");
                }
            }

            // ==============
            // 配達拠点コード関連チェック
            // ==============
            if (!StringUtils.isEmpty(dto.getDlvrykankatsukyotencd1())) {
                List<FormField> fields = new ArrayList<>();
                addField(fields, "reservationBean.dlvrykankatsukyotencd1", "変更得意先配達管轄拠点コード");
                addField(fields, "reservationBean.dlvrykankatsuapkihoncd1", "変更得意先配達管轄ＡＰ基本コード");
                addField(fields, "reservationBean.dlvrykankatsushopcd1", "変更得意先配達管轄ショップコード");
                checkDlvryKyoten(dto.getApkihoncd(), dto.getShopcd(),
                    dto.getDlvrykankatsuapkihoncd1(), dto.getDlvrykankatsushopcd1(),
                    dto.getDlvrykankatsukyotencd1(), dto.getTokuisakikbn(),
                    dto.getJutakudlvrycstkbn(), fields);
            }

            // ==============
            // 配達エリアコード関連チェック
            // ==============
            if (!StringUtils.isEmpty(dto.getDlvryareacd1())) {
                checkDlvryArea(dto.getDlvrykankatsuapkihoncd1(), dto.getDlvrykankatsushopcd1(),
                    dto.getDlvrykankatsukyotencd1(), dto.getDlvryareacd1(), dto.getUnyoDate(),
                    "reservationBean.dlvryareacd1", "変更得意先配達エリアコード");
            }

            // ==============
            // 配達会社コード関連チェック
            // ==============
            if (!StringUtils.isEmpty(dto.getDlvrycmpnycd1())) {
                checkDlvryCmpny(dto.getApkihoncd(), dto.getShopcd(), dto.getDlvrycmpnycd1(),
                    "reservationBean.dlvrycmpnycd1", "変更得意先配達会社コード");
            }
        }

        // 請求先マスタ変更＝1(チェックON)の場合
        if (!StringUtils.isEmpty(dto.getSeikyusakimsthnkflg())) {
            // ==============
            // 営業担当者コード関連チェック
            // ==============
            checkTnsha(dto.getApkihoncd(), dto.getShopcd(), dto.getEigyotnshacd2(),
                "reservationBean.eigyotnshacd2", "変更請求先営業担当者コード");

            // ==============
            // 回収担当者コード関連チェック
            // ==============
            checkTnsha(dto.getApkihoncd(), dto.getShopcd(), dto.getKaishutnshacd(),
                "reservationBean.kaishutnshacd", "変更請求先回収担当者コード");
        }

        // ==============
        // 「変更前-請求先」の親子チェック
        // ==============
        // 請求先コードの入力値が変更された場合
        if (!StringUtils.isEmpty(dto.getSkysakicdbefore())
            && !dto.getSkysakicdbefore().equals(dto.getSkysakicd())) {

            // 変更前の請求先コードにて、請求先マスタを取得
            S02M017TEntity s02M017TEntityCondition = new S02M017TEntity();
            s02M017TEntityCondition.setApkihoncd(dto.getApkihoncd());
            s02M017TEntityCondition.setShopcd(dto.getShopcd());
            s02M017TEntityCondition.setSkysakicd(dto.getSkysakicdbefore());
            S02M017TEntity s02M017TEntity = s02m017tDao.selectByPrimaryKey(s02M017TEntityCondition);

            // 変更前の請求先コードについて、請求区分が"親子(2)"かつ親得意先コードが画面.得意先コードと同じ場合は、エラーとする
            if (!ObjectUtils.isEmpty(s02M017TEntity)) {
                String skykbn = s02M017TEntity.getSkykbn(); // 変更前の請求先コード.請求区分
                String oyatokuisakicd = s02M017TEntity.getOyatokuisakicd(); // 変更前の請求先コード.親得意先コード
                if (CommonConstants.SKYKBN_2.equals(skykbn) && !StringUtils.isEmpty(oyatokuisakicd)
                    && oyatokuisakicd.equals(dto.getTokuisakicd())) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("baseBean.skysakicd", "請求先コード"), MessageID.W216, "親得意先コード",
                        "請求先コードは変更");
                }
            }
        }
    }

    /**
     * FormFieldリストに要素を追加する
     * 
     * @param list FormFieldリスト
     * @param name フィールド名
     * @param label ラベル
     * @return
     */
    private void addField(List<FormField> list, String name, String label) {
        list.add(new FormField(name, label));
    }

    /**
     * 郵便番号関連チェック
     * 
     * @param yubinno 郵便番号
     * @param chihoupubdanticdn1 地方公共団体コード1
     * @param chihoupubdanticdn2 地方公共団体コード2
     * @param field フィールド
     * @param fieldName フィールド名
     * @return
     */
    private S02M083TEntity checkYubinno(String yubinno, String chihoupubdanticdn1,
        String chihoupubdanticdn2, String field, String fieldName) {

        S02M083TEntity ret = new S02M083TEntity();

        // 郵便番号管理マスタの取得
        List<S02M083TEntity> entityList = getYubinnoKanriMst(yubinno);

        if (!ObjectUtils.isEmpty(entityList)) {
            // 抽出結果が1件の場合、地方公共団体コードを設定する
            if (entityList.size() == 1) {
                ret = entityList.get(0);

                // 抽出結果が2件以上の場合
            } else if (entityList.size() > 1) {
                // 地方公共団体コードが一致するレコードが存在するかチェックする
                boolean isExist = false;
                for (S02M083TEntity e : entityList) {
                    if (e.getChihoupubdanticdn1().equals(chihoupubdanticdn1)
                        && e.getChihoupubdanticdn2().equals(chihoupubdanticdn2)) {
                        ret = e;
                        isExist = true;
                        break;
                    }
                }
                // 存在しない場合、エラー
                if (!isExist) {
                    throw BusinessExceptionFactory.validationFailed(new FormField(field, fieldName),
                        MessageID.W258, fieldName);
                }
            }
        }

        return ret;
    }

    /**
     * 引上日以降経過日数チェック
     * 
     * @param dto 更新用DTO
     * @return
     */
    private void checkHikiageKeikaNissu(B0501W02UpdateDto dto) {
        // 更新前得意先マスタ取得
        S02M013TEntity entity =
            getTokuisakiMst(dto.getApkihoncd(), dto.getShopcd(), dto.getTokuisakicd());

        if (!ObjectUtils.isEmpty(entity) && !StringUtils.isEmpty(entity.getWsvhikiagedd())) {
            // 検索条件作成
            S02M006TDto conditionDto = new S02M006TDto();
            conditionDto.setApkihoncd(CommonConstants.APCD_000);
            conditionDto.setShopcd(CommonConstants.SYS_SHOPCD_000);
            conditionDto.setSetteino(CommonConstants.SYS_SETTEINO_226);

            // システム管理マスタから引上日以降経過日数を取得
            List<S02M006TDto> dtoList = s02m006tService.search(conditionDto, null, null);
            if (!ObjectUtils.isEmpty(dtoList) && dtoList.size() > 0
                && !StringUtils.isEmpty(dtoList.get(0).getMojisetteiatai())) {
                String keikaNissu = dtoList.get(0).getMojisetteiatai();

                // サーバー引上日に文字設定値(引上日以降経過日数)を足した日付を日付変更最大可能日とする
                String henkoKanobi =
                    DateTimeUtil.calcYMD(entity.getWsvhikiagedd(), Long.parseLong(keikaNissu));

                // 運用日付＞日付変更最大可能日の場合
                if (dto.getUnyoDate().compareTo(henkoKanobi) > 0) {
                    List<FormField> fields = new ArrayList<>();

                    // 画面.引上げ日≠得意先マスタ.サーバー引上日の場合
                    if (!ItemUtil.nullToString(dto.getWsvhikiagedd())
                        .equals(ItemUtil.nullToString(entity.getWsvhikiagedd()))) {
                        addField(fields, "baseBean.wsvhikiagedd", "引上日");
                    }
                    // 画面.申込受付日≠得意先マスタ.申込受付日の場合
                    if (!dto.getMoshikomircptdd().equals(entity.getMoshikomircptdd())) {
                        addField(fields, "baseBean.moshikomircptdd", "申込受付日");
                    }
                    // 画面.入会日≠得意先マスタ.入会日の場合
                    if (!ItemUtil.nullToString(dto.getNyukaidd())
                        .equals(ItemUtil.nullToString(entity.getNyukaidd()))) {
                        addField(fields, "baseBean.nyukaidd", "入会日");
                    }
                    // 画面.退会受付日≠得意先マスタ.退会日の場合
                    if (!ItemUtil.nullToString(dto.getTaikaidd())
                        .equals(ItemUtil.nullToString(entity.getTaikaidd()))) {
                        addField(fields, "baseBean.taikaidd", "退会受付日");
                    }
                    // 画面.初回設置日≠得意先マスタ.初回サーバー設置日の場合
                    if (!ItemUtil.nullToString(dto.getShokaiwsvstupdd())
                        .equals(ItemUtil.nullToString(entity.getShokaiwsvstupdd()))) {
                        addField(fields, "baseBean.shokaiwsvstupdd", "初回設置日");
                    }

                    // 画面初期表示値から値が変更されていた場合、エラー
                    if (fields.size() > 0) {
                        // エラーメッセージ生成
                        StringBuffer sb = new StringBuffer();
                        sb.append("引上日から" + keikaNissu + "日経過しているため、");
                        for (int i = 0; i < fields.size(); i++) {
                            sb.append("「" + fields.get(i).getLabel() + "」、");
                        }
                        throw BusinessExceptionFactory.validationFailed(fields, MessageID.W171,
                            sb.substring(0, sb.length() - 1), "変更");
                    }
                }
            }
        }
    }

    /**
     * 担当者関連チェック
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tnshacd 担当者コード
     * @param name フィールド名
     * @param label フィールドラベル
     * @return
     */
    private void checkTnsha(String apkihoncd, String shopcd, String tnshacd, String name,
        String label) {
        // 担当者マスタの取得
        S02M003TEntity entity = getTnshaMst(apkihoncd, shopcd, tnshacd);

        // 担当者マスタに存在しないコード値はエラー
        if (ObjectUtils.isEmpty(entity)) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W084, label);
        }

        // 担当者マスタ.有効区分=1(有効)以外の場合、エラー
        if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W123, label);
        }
    }

    /**
     * 配達会社関連チェック
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param dlvrycmpnycd 配達会社コード
     * @param name フィールド名
     * @param label フィールドラベル
     * @return
     */
    private void checkDlvryCmpny(String apkihoncd, String shopcd, String dlvrycmpnycd, String name,
        String label) {
        // 配達会社マスタの取得
        S02M019TEntity entity = getDlvryCmpnyMst(apkihoncd, shopcd, dlvrycmpnycd);

        // 配達会社マスタに存在しないコード値はエラー
        if (ObjectUtils.isEmpty(entity)) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W084, label);
        }

        // 配達会社マスタ.有効区分=1(有効)以外の場合、エラー
        if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W123, label);
        }
    }

    /**
     * 配達エリア関連チェック
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param dlvrykyotencd 配達拠点コード
     * @param dlvryareacd 配達エリアコード
     * @param unyodd 運用日付
     * @param name フィールド名
     * @param label フィールドラベル
     * @return
     */
    private void checkDlvryArea(String apkihoncd, String shopcd, String dlvrykyotencd,
        String dlvryareacd, String unyodd, String name, String label) {
        // 配達エリアマスタの取得
        S02M018TEntity entity =
            getDlvryAreaMst(apkihoncd, shopcd, dlvrykyotencd, dlvryareacd, unyodd);

        // 配達エリアマスタに存在しないコード値はエラー
        if (ObjectUtils.isEmpty(entity)) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W084, label);
        }

        // 配達エリアマスタ.有効区分=1(有効)以外の場合、エラー
        if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W123, label);
        }
    }

    /**
     * 配達拠点関連チェック
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param dlvryapkihoncd 配達管轄AP基本コード
     * @param dlvryshopcd 配達管轄ショップコード
     * @param dlvrykyotencd 配達拠点コード
     * @param tokuisakikbn 得意先区分
     * @param jutakudlvrycstkbn 顧客区分
     * @param fields フィールド
     * @return
     */
    private void checkDlvryKyoten(String apkihoncd, String shopcd, String dlvryapkihoncd,
        String dlvryshopcd, String dlvrykyotencd, String tokuisakikbn, String jutakudlvrycstkbn,
        List<FormField> fields) {
        // 配達拠点マスタの取得
        S02M031TEntity entity = getDlvryKyotenMst(dlvryapkihoncd, dlvryshopcd, dlvrykyotencd);

        FormField field = fields.get(0);

        // 配達拠点マスタに存在しないコード値はエラー
        if (ObjectUtils.isEmpty(entity)) {
            throw BusinessExceptionFactory.validationFailed(field, MessageID.W084,
                field.getLabel());
        }

        // 配達拠点マスタ.有効区分=1(有効)以外の場合、エラー
        if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
            throw BusinessExceptionFactory.validationFailed(field, MessageID.W123,
                field.getLabel());
        }

        // 配達拠点に自AP－SHOP以外を選択した場合
        if (!(apkihoncd.equals(dlvryapkihoncd) && shopcd.equals(dlvryshopcd))) {
            // 配達拠点マスタ.受託区分＝0(受託無)の場合、エラー
            if (CommonConstants.JUTAKUKBN_0.equals(entity.getJutakukbn())) {
                throw BusinessExceptionFactory.validationFailed(field, MessageID.W149,
                    "「" + field.getLabel() + "」", "受託可能な配達拠点");
            }

            // ※以下チェックはAPIControllerに移動 （設計書上は本来ここで実施する）
            // // （得意先区分＝1(顧客)かつ 顧客区分＝2(ショップ顧客)）を選択
            // // または得意先区分＝ 2(ショップ)を選択した場合、エラー
            // if ((CommonConstants.KBN_TOKUISAKI_KOKUAKU.equals(tokuisakikbn)
            // && CommonConstants.JUTAKUDLVRYCSTKBN_2.equals(jutakudlvrycstkbn))
            // || CommonConstants.KBN_TOKUISAKI_SHOP.equals(tokuisakikbn)) {
            // throw BusinessExceptionFactory.validationFailed(field, MessageID.W124, "委託配達先");
            // }
        }
    }

    /**
     * 配達担当者関連チェック
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param dlvrytnshacd 配達担当者コード
     * @param dlvrykbn 配達区分
     * @param dlvrycmpnycd 配達会社コード
     * @param name フィールド名
     * @param label フィールドラベル
     * @return
     */
    private void checkDlvryTnsha(String apkihoncd, String shopcd, String dlvrytnshacd,
        String dlvrykbn, String dlvrycmpnycd, String name, String label) {
        // 担当者マスタの取得
        S02M003TEntity entity = getTnshaMst(apkihoncd, shopcd, dlvrytnshacd);

        // 担当者マスタに存在しないコード値はエラー
        if (ObjectUtils.isEmpty(entity)) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W084, label);
        }

        // 担当者マスタ.有効区分=1(有効)以外の場合、エラー
        if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W123, label);
        }

        // 担当者マスタ．配達担当者区分＝1(配達担当者である)以外の場合、エラー
        if (!CommonConstants.DLVRYTNSHAKBN_1.equals(entity.getDlvrytnshakbn())) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W112, label);
        }

        // 配達区分＝1（自社）を選択し、担当者マスタ．所属配達会社コードが設定されている場合、エラー
        if (CommonConstants.KBN_DLVRY_JISHA.equals(dlvrykbn)
            && !StringUtils.isEmpty(entity.getShozokudlvrycmpnycd())) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W120, "配達区分「自社」を選択した場合、配達担当者コード", "所属配達会社が未設定の担当者");
        }

        // 担当者マスタ．所属配達会社コードと画面.配達会社コードが値がある場合
        if (!StringUtils.isEmpty(dlvrycmpnycd)
            && !StringUtils.isEmpty(entity.getShozokudlvrycmpnycd())) {
            // 配達区分=2（他社便）を選択し、
            // 担当者マスタ．所属配達会社コードと画面.配達会社コードが不一致の場合、エラー
            if (CommonConstants.KBN_DLVRY_TASHA.equals(dlvrykbn)
                && !dlvrycmpnycd.equals(entity.getShozokudlvrycmpnycd())) {
                throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                    MessageID.W120, "配達区分「他社便」を選択した場合、配達担当者コード", "所属配達会社と同一の配達会社");
            }
        }
    }

    /**
     * 営業担当者関連チェック
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param eigyotnshacd 営業担当者コード
     * @param name フィールド名
     * @param label フィールドラベル
     * @return
     */
    private void checkEigyoTnsha(String apkihoncd, String shopcd, String eigyotnshacd, String name,
        String label) {

        // 担当者マスタの取得
        S02M003TEntity entity = getTnshaMst(apkihoncd, shopcd, eigyotnshacd);

        // 担当者マスタに存在しないコード値はエラー
        if (ObjectUtils.isEmpty(entity)) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W084, label);
        }

        // 担当者マスタ.有効区分=1(有効)以外の場合、エラー
        if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W123, label);
        }

        // 担当者マスタ．営業担当者区分 ＝ 1(営業担当者である)以外の場合、エラー
        if (!CommonConstants.EIGYOTNSHAKBN_1.equals(entity.getEigyotnshakbn())) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W112, label);
        }
    }

    /**
     * 営業エリア関連チェック
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param eigyoareacd 営業エリアコード
     * @param name フィールド名
     * @param label フィールドラベル
     * @return
     */
    private void checkEigyoArea(String apkihoncd, String shopcd, String eigyoareacd, String name,
        String label) {

        // 営業エリアマスタの取得
        S02M020TEntity entity = getEigyoAreaMst(apkihoncd, shopcd, eigyoareacd);

        // 営業エリアマスタに存在しないコード値はエラー
        if (ObjectUtils.isEmpty(entity)) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W084, label);
        }

        // 営業エリアマスタ.有効区分=1(有効)以外の場合、エラー
        if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W123, label);
        }
    }

    /**
     * 得意先外部識別コード関連チェック
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @param tokuisakigaibuskbtscd 得意先外部識別コード
     * @param name フィールド名
     * @param label フィールドラベル
     * @return
     */
    private void checkTokuisakiGaibuskbtsCd(String apkihoncd, String shopcd, String tokuisakicd,
        String tokuisakigaibuskbtscd, String name, String label) {
        // 検索条件の設定
        S02M013TEntity key = new S02M013TEntity();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setTokuisakicd(tokuisakicd);
        key.setTokuisakigaibuskbtscd(tokuisakigaibuskbtscd);

        // 得意先外部識別コードの重複チェックSQL実行
        long cnt = s02m013tDao.countByB0501W02ForGaibuskbtsCdCheck(key);

        // 得意先外部識別コードが他の得意先に既に設定されている場合、エラー
        if (cnt > 0) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W103, label);
        }
    }

    /**
     * マージン用商品コード関連チェック
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param shncd マージン用商品コード
     * @param name フィールド名
     * @param label フィールドラベル
     * @return
     */
    private void checkMgnyoShnCd(String apkihoncd, String shopcd, String shncd, String name,
        String label) {
        // 検索条件の設定
        S02V001TEntityExample example = new S02V001TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd)
            .andShncdEqualTo(shncd).andLctgrycdEqualTo(CommonConstants.LCTGRYCD_01)
            .andMgntrgtkbnEqualTo(CommonConstants.SHNMGNTRGTKBN_1);

        // 商品マスタビューの取得
        List<S02V001TEntity> entityList = s02v001tDao.selectByExample(example);

        // 商品マスタビューに存在しないコード値はエラー
        if (ObjectUtils.isEmpty(entityList)) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W084, label);
        }

        // 商品マスタビュー.ショップ有効区分＝１(有効)以外の場合、エラー
        if (!CommonConstants.YUKOKBN_1.equals(entityList.get(0).getShopyukokbn())) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W123, label);
        }
    }

    /**
     * マージンコード関連チェック
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param mgncd マージンコード
     * @param name フィールド名
     * @param label フィールドラベル
     * @return
     */
    private void checkMgnCd(String apkihoncd, String shopcd, String mgncd, String name,
        String label) {
        // マージンマスタの取得
        S02M015TEntity entity =
            getMgnMst(apkihoncd, shopcd, mgncd, CommonConstants.KBN_MARGINTARGET_BOTTLE);

        // マージンマスタに存在しないコード値はエラー
        if (ObjectUtils.isEmpty(entity)) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W084, label);
        }

        // マージンマスタ.有効区分=1(有効)以外の場合、エラー
        if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W123, label);
        }
    }

    /**
     * 自動値引コード関連チェック
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param jidonebikicd 自動値引コード
     * @param name フィールド名
     * @param label フィールドラベル
     * @return
     */
    private void checkJidonebikiCd(String apkihoncd, String shopcd, String jidonebikicd,
        String name, String label) {
        // 自動値引マスタの取得
        S02M052TEntity entity = getJidonebikiMst(apkihoncd, shopcd, jidonebikicd);

        // 自動値引マスタに存在しないコード値はエラー
        if (ObjectUtils.isEmpty(entity)) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W084, label);
        }

        // 自動値引マスタ.有効区分=1(有効)以外の場合、エラー
        if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
            throw BusinessExceptionFactory.validationFailed(new FormField(name, label),
                MessageID.W123, label);
        }
    }

    /**
     * 得意先マスタを取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @return 得意先マスタエンティティ
     */
    private S02M013TEntity getTokuisakiMst(String apkihoncd, String shopcd, String tokuisakicd) {
        // 検索条件の設定
        S02M013TEntityKey key = new S02M013TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setTokuisakicd(tokuisakicd);

        // 得意先マスタの取得
        return s02m013tDao.selectByPrimaryKey(key);
    }

    /**
     * 得意先実績を取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @return 得意先実績エンティティ
     */
    private S02M050TEntity getTokuisakiJisseki(String apkihoncd, String shopcd,
        String tokuisakicd) {
        // 検索条件の設定
        S02M050TEntity key = new S02M050TEntity();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setTokuisakicd(tokuisakicd);

        // 得意先実績の取得
        return s02m050tDao.selectByPrimaryKey(key);
    }

    /**
     * 請求先マスタを取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param skysakicd 請求先コード
     * @return 請求先マスタエンティティ
     */
    private S02M017TEntity getSkysakiMst(String apkihoncd, String shopcd, String skysakicd) {
        // 検索条件の設定
        S02M017TEntityKey key = new S02M017TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setSkysakicd(skysakicd);

        // 請求先マスタの取得
        return s02m017tDao.selectByPrimaryKey(key);
    }

    /**
     * 請求先マスタをAP基本コード/ショップコード/請求先コードをキーに検索.
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param skysakicd 請求先コード
     * @return S02M017TDto
     */
    public S02M017TDto searchSkysakiByPrimaryKey(String apkihoncd, String shopcd,
        String skysakicd) {
        // 請求先マスタの取得
        S02M017TEntity entity = getSkysakiMst(apkihoncd, shopcd, skysakicd);

        if (ObjectUtils.isEmpty(entity)) {
            return null;
        }

        // SELECTの結果が取得できた場合、dtoに取得結果を設定
        S02M017TDto dto = new S02M017TDto();

        dto.setSkysakinmn1(entity.getSkysakinmn1()); // 請求先名称
        dto.setOyatokuisakicd(entity.getOyatokuisakicd()); // 親得意先コード
        dto.setYukokbn(entity.getYukokbn()); // 有効区分

        return dto;
    }

    /**
     * 営業エリアマスタを取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param eigyoareacd 営業エリアコード
     * @return 営業エリアマスタエンティティ
     */
    private S02M020TEntity getEigyoAreaMst(String apkihoncd, String shopcd, String eigyoareacd) {
        // 検索条件の設定
        S02M020TEntityKey key = new S02M020TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setEigyoareacd(eigyoareacd);

        // 営業エリアマスタの取得
        return s02m020tDao.selectByPrimaryKey(key);
    }

    /**
     * 担当者マスタを取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tnshacd 担当者コード
     * @return 担当者マスタエンティティ
     */
    private S02M003TEntity getTnshaMst(String apkihoncd, String shopcd, String tnshacd) {
        // 検索条件の設定
        S02M003TEntityKey key = new S02M003TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setTnshacd(tnshacd);

        // 担当者マスタの取得
        return s02m003tDao.selectByPrimaryKey(key);
    }

    /**
     * エージェントマスタを取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param ejntcd エージェントコード
     * @return エージェントマスタエンティティ
     */
    private S02M014TEntity getEjentMst(String apkihoncd, String shopcd, String ejntcd) {
        // 検索条件の設定
        S02M014TEntityKey key = new S02M014TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setEjntcd(ejntcd);

        // エージェントマスタの取得
        return s02m014tDao.selectByPrimaryKey(key);
    }

    /**
     * キャンペーンマスタを取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param kynpncd キャンペーンコード
     * @return キャンペーンマスタエンティティ
     */
    private S02M059TEntity getCampaignMst(String apkihoncd, String shopcd, String kynpncd) {
        // 検索条件の設定
        S02M059TEntityKey key = new S02M059TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setKynpncd(kynpncd);

        // キャンペーンマスタの取得
        return s02m059tDao.selectByPrimaryKey(key);
    }

    /**
     * マージンマスタを取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param mgncd マージンコード
     * @param mgntrgtkbn マージン対象区分
     * @return マージンマスタエンティティ
     */
    private S02M015TEntity getMgnMst(String apkihoncd, String shopcd, String mgncd,
        String mgntrgtkbn) {
        // 検索条件の設定
        S02M015TEntityKey key = new S02M015TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setMgncd(mgncd);
        key.setMgntrgtkbn(mgntrgtkbn);

        // マージンマスタの取得
        return s02m015tDao.selectByPrimaryKey(key);
    }

    /**
     * マージンマスタをAP基本コード/ショップコード/マージンコード/マージン対象区分をキーに検索.
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param mgncd マージンコード
     * @param mgntrgtkbn マージン対象区分
     * @return S02M015TDto
     */
    public S02M015TDto searchMgnByPrimaryKey(String apkihoncd, String shopcd, String mgncd,
        String mgntrgtkbn) {
        // マージンマスタの取得
        S02M015TEntity entity = getMgnMst(apkihoncd, shopcd, mgncd, mgntrgtkbn);

        if (ObjectUtils.isEmpty(entity)) {
            return null;
        }

        // SELECTの結果が取得できた場合、dtoに取得結果を設定
        S02M015TDto dto = new S02M015TDto();

        dto.setMgncd(entity.getMgncd()); // マージンコード
        dto.setMgnnm(entity.getMgnnm()); // マージン名称
        dto.setYukokbn(entity.getYukokbn()); // 有効区分

        return dto;
    }

    /**
     * ショップマスタを取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @return ショップマスタエンティティ
     */
    private S02M046TEntity getShopMst(String apkihoncd, String shopcd) {
        // 検索条件の設定
        S02M046TEntityKey key = new S02M046TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);

        // ショップマスタの取得
        return s02m046tDao.selectByPrimaryKey(key);
    }

    /**
     * ショップポイントマスタを取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @return ショップポイントマスタエンティティ
     */
    private S02M202TEntity getShopPointMst(String apkihoncd, String shopcd) {
        // 検索条件の設定
        S02M202TEntityKey key = new S02M202TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);

        // ショップポイントマスタの取得
        return s02m202tDao.selectByPrimaryKey(key);
    }

    /**
     * 配達拠点マスタを取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param dlvrykyotencd 配達拠点コード
     * @return 配達拠点マスタエンティティ
     */
    private S02M031TEntity getDlvryKyotenMst(String apkihoncd, String shopcd,
        String dlvrykyotencd) {
        // 検索条件の設定
        S02M031TEntityKey key = new S02M031TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setDlvrykyotencd(dlvrykyotencd);

        // 配達拠点マスタの取得
        return s02m031tDao.selectByPrimaryKey(key);
    }

    /**
     * 配達エリアマスタを取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param dlvrykyotencd 配達拠点コード
     * @param dlvryareacd 配達エリアコード
     * @param unyodd 運用日付
     * @return 配達エリアマスタエンティティ
     */
    private S02M018TEntity getDlvryAreaMst(String apkihoncd, String shopcd, String dlvrykyotencd,
        String dlvryareacd, String unyodd) {

        // 検索条件の設定
        S02M018TEntityExample example = new S02M018TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd)
            .andDlvrykyotencdEqualTo(dlvrykyotencd).andDlvryareacdEqualTo(dlvryareacd)
            .andTekiyostrtddLessThanOrEqualTo(unyodd).andTekiyoshryddGreaterThanOrEqualTo(unyodd);

        // 配達エリアマスタの取得
        List<S02M018TEntity> results = s02m018tDao.selectByExample(example);

        if (ObjectUtils.isEmpty(results) || results.size() == 0) {
            return null;
        }

        return results.get(0);
    }

    /**
     * 配達エリアマスタを運用日付で取得.
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param dlvrykyotencd 配達拠点コード
     * @param dlvryareacd 配達エリアコード
     * @param unyodd 運用日付
     * @return S02M018TDto
     */
    public S02M018TDto searchDlvryAreaByUnyodd(String apkihoncd, String shopcd,
        String dlvrykyotencd, String dlvryareacd, String unyodd) {
        // 配達エリアマスタの取得
        S02M018TEntity entity =
            getDlvryAreaMst(apkihoncd, shopcd, dlvrykyotencd, dlvryareacd, unyodd);

        if (ObjectUtils.isEmpty(entity)) {
            return null;
        }

        // SELECTの結果が取得できた場合、dtoに取得結果を設定
        S02M018TDto dto = new S02M018TDto();

        dto.setDlvryareacd(entity.getDlvryareacd()); // 配達エリアコード
        dto.setDlvryareanm(entity.getDlvryareanm()); // 配達エリア名称
        dto.setYukokbn(entity.getYukokbn()); // 有効区分

        return dto;
    }

    /**
     * 配達会社マスタを取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param dlvrycmpnycd 配達会社コード
     * @return 配達会社マスタエンティティ
     */
    private S02M019TEntity getDlvryCmpnyMst(String apkihoncd, String shopcd, String dlvrycmpnycd) {
        // 検索条件の設定
        S02M019TEntityKey key = new S02M019TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setDlvrycmpnycd(dlvrycmpnycd);

        // 配達会社マスタの取得
        return s02m019tDao.selectByPrimaryKey(key);
    }

    /**
     * 自動値引マスタを取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param jidonebikicd 自動値引コード
     * @return 自動値引マスタエンティティ
     */
    private S02M052TEntity getJidonebikiMst(String apkihoncd, String shopcd, String jidonebikicd) {
        // 検索条件の設定
        S02M052TEntityKey key = new S02M052TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setJidonebikicd(jidonebikicd);

        // 自動値引マスタの取得
        return s02m052tDao.selectByPrimaryKey(key);
    }

    /**
     * 郵便番号管理マスタを取得する
     * 
     * @param yubinno 郵便番号
     * @return 郵便番号管理マスタエンティティリスト
     */
    private List<S02M083TEntity> getYubinnoKanriMst(String yubinno) {
        // 検索条件の設定
        S02M083TEntityExample example = new S02M083TEntityExample();
        example.createCriteria().andYubinnoEqualTo(yubinno);

        // 郵便番号管理マスタの取得
        return s02m083tDao.selectByExample(example);
    }

    /**
     * 請求先管理区分を取得する
     * 
     * @param tokuisakikbn 得意先区分
     * @return 請求先管理区分
     */
    private String getSkysakikanrikbn(String tokuisakikbn) {
        // 得意先区分=1(顧客) or 5(NA受託) or 6(配達受託)の場合
        if (CommonConstants.KBN_TOKUISAKI_KOKUAKU.equals(tokuisakikbn)
            || CommonConstants.KBN_TOKUISAKI_NAJUTAKU.equals(tokuisakikbn)
            || CommonConstants.KBN_TOKUISAKI_DLVRYJUTAKU.equals(tokuisakikbn)) {
            // 1(請求先)を返却
            return CommonConstants.SKYSAKIKANRIKBN_1;

            // 得意先区分=9(経費)の場合
        } else if (CommonConstants.KBN_TOKUISAKI_KEIHI.equals(tokuisakikbn)) {
            // 9(経費)を返却
            return CommonConstants.SKYSAKIKANRIKBN_9;

            // 上記以外
        } else {
            // 2(卸)を返却
            return CommonConstants.SKYSAKIKANRIKBN_2;
        }
    }

    /**
     * 得意先違約金集計値を取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @param unyodd 運用日付
     * @return 得意先違約金集計値
     */
    public long getIyakukinSummary(String apkihoncd, String shopcd, String tokuisakicd,
        String unyodd) {
        // 検索条件指定
        S03T003TEntity key = new S03T003TEntity();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setTokuisakicd(tokuisakicd);
        key.setIyakukintekiyostrtdydm(unyodd);
        key.setIyakukintekiyoshrydydm(unyodd);

        // 得意先違約金データ取得
        return s03t003tDao.selectByB0501W02ForSummary(key);
    }

    /**
     * 価格情報取得
     * 
     * @param entity ショップマスタエンティティ
     * @return 価格情報
     */
    private String getPriceinfo(S02M046TEntity entity) {
        StringBuffer buf = new StringBuffer();
        buf.append("");
        buf.append("{");
        buf.append("type:1");
        buf.append(",shohitaxkirikaedd:").append(entity.getShohitaxkirikaedd()).append("");
        buf.append(",shinshohitax:").append(entity.getShinshohitax()).append("");
        buf.append(",genzshohitax:").append(entity.getGenzshohitax()).append("");
        buf.append(",taxkeisankbnurikirikaedd:").append(entity.getTaxkeisankbnurikirikaedd())
            .append("");
        buf.append(",shintaxkeisankbnuri:").append(entity.getShintaxkeisankbnuri()).append("");
        buf.append(",genztaxkeisankbnuri:").append(entity.getGenztaxkeisankbnuri()).append("");
        buf.append(",taxhasukeisankbn:").append(entity.getTaxhasukeisankbn()).append("");
        buf.append(",keisanhoho:1");
        buf.append(",taxkbn:1");
        buf.append(",urikinhasukeisankbn:").append(entity.getUrikinhasukeisankbn()).append("");
        buf.append("}");

        return buf.toString();
    }

    /**
     * 反映元得意先取得処理
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @param unyodd 運用日付
     * @return B0501W02DisplayDto
     */
    public B0501W02DisplayDto getHaneimotTokuisaki(String apkihoncd, String shopcd,
        String tokuisakicd, String unyodd) {
        // 得意先マスタの取得
        S02M013TB0501W02Entity entity = s02m013tDao
            .selectByB0501W02(createTokuisakiEntityKey(apkihoncd, shopcd, tokuisakicd, unyodd));

        // 得意先マスタに存在しないコード値はエラー
        if (ObjectUtils.isEmpty(entity)) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.haneimototokuisakicd", "反映元得意先コード"), MessageID.W084,
                "反映元得意先コード");
        }

        // 有効区分 ≠ 1:有効の場合、エラー
        if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.haneimototokuisakicd", "反映元得意先コード"), MessageID.W123,
                "反映元得意先コード");
        }

        // 結果セットをdtoに詰めて返却
        return createDisplayDto(entity, new ArrayList<S02M016TB0501W02Entity>(),
            new S02M084TB0501W02Entity(), new S02M085TB0501W02Entity(), new S02M046TEntity(),
            unyodd, new S02M203TB0501W02Entity());
    }

    /**
     * 反映元委託相手取得処理
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @param jutakudlvrycstkbn 顧客区分
     * @param unyodd 運用日付
     * @return B0501W02DisplayDto
     */
    public B0501W02DisplayDto getHaneimotoItakuaite(String apkihoncd, String shopcd,
        String tokuisakicd, String jutakudlvrycstkbn, String unyodd) {
        // 得意先マスタの取得
        S02M013TB0501W02Entity entity = s02m013tDao
            .selectByB0501W02(createTokuisakiEntityKey(apkihoncd, shopcd, tokuisakicd, unyodd));

        // 得意先マスタに存在しないコード値はエラー
        if (ObjectUtils.isEmpty(entity)) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.itakuaitetokuisakicd", "委託相手得意先コード"), MessageID.W084,
                "反映元得意先コード");
        }

        // 有効区分 ≠ 1:有効の場合、エラー
        if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.itakuaitetokuisakicd", "委託相手得意先コード"), MessageID.W123,
                "委託相手得意先コード");
        }

        // 得意先マスタ.受託配達顧客区分=3(NA顧客)/ 4(委託顧客)以外の場合、エラー
        if (!CommonConstants.JUTAKUDLVRYCSTKBN_3.equals(entity.getJutakudlvrycstkbn())
            && !CommonConstants.JUTAKUDLVRYCSTKBN_4.equals(entity.getJutakudlvrycstkbn())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.jutakudlvrycstkbn", "顧客区分"), MessageID.W149, "委託相手コードの顧客区分",
                "対象");
        }

        // 画面.顧客区分 = 5（NA受託）、得意先マスタ．受託配達顧客区分 ≠ 3（NA顧客）の場合、エラー
        if (CommonConstants.JUTAKUDLVRYCSTKBN_5.equals(jutakudlvrycstkbn)
            && !CommonConstants.JUTAKUDLVRYCSTKBN_3.equals(entity.getJutakudlvrycstkbn())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.jutakudlvrycstkbn", "顧客区分"), MessageID.W149, "委託相手コードの顧客区分",
                "対象");
        }

        // 画面.顧客区分が = 6（配達受託）、得意先マスタ．受託配達顧客区分 ≠ 4（委託顧客）の場合、エラー
        if (CommonConstants.JUTAKUDLVRYCSTKBN_6.equals(jutakudlvrycstkbn)
            && !CommonConstants.JUTAKUDLVRYCSTKBN_4.equals(entity.getJutakudlvrycstkbn())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.jutakudlvrycstkbn", "顧客区分"), MessageID.W149, "委託相手コードの顧客区分",
                "対象");
        }

        // 結果セットをdtoに詰めて返却
        return createDisplayDto(entity, new ArrayList<S02M016TB0501W02Entity>(),
            new S02M084TB0501W02Entity(), new S02M085TB0501W02Entity(), new S02M046TEntity(),
            unyodd, new S02M203TB0501W02Entity());
    }

    /**
     * 委託相手チェック処理.
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @return
     */
    public void checkItakuaite(String apkihoncd, String shopcd) {

        // 検索条件の設定
        S02M001TEntityKey key = new S02M001TEntityKey();
        key.setApkihoncd(apkihoncd);

        // ＡＰ基本マスタの取得
        S02M001TEntity apEntity = s02m001tDao.selectByPrimaryKey(key);

        // ＡＰ基本マスタに存在しないコード値はエラー
        if (ObjectUtils.isEmpty(apEntity)) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.itakuaiteapkihoncd", "委託相手AP基本コード"), MessageID.W084,
                "委託相手AP基本コード");
        }

        // ＡＰ基本マスタ.有効区分=1(有効)以外の場合、エラー
        if (!CommonConstants.YUKOKBN_1.equals(apEntity.getYukokbn())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.itakuaiteapkihoncd", "委託相手AP基本コード"), MessageID.W123,
                "委託相手AP基本コード");
        }

        // ショップマスタの取得
        S02M046TEntity shopEntity = getShopMst(apkihoncd, shopcd);

        // ショップマスタに存在しないコード値はエラー
        if (ObjectUtils.isEmpty(shopEntity)) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.itakuaiteshopcd", "委託相手ショップコード"), MessageID.W084,
                "委託相手ショップコード");
        }

        // ショップマスタ.有効区分=1(有効)以外の場合、エラー
        if (!CommonConstants.YUKOKBN_1.equals(shopEntity.getYukokbn())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.itakuaiteshopcd", "委託相手ショップコード"), MessageID.W123,
                "委託相手ショップコード");
        }
    }

    /**
     * 会員チェック処理.
     * 
     * @param kaiincd 会員コード
     * @return
     */
    public void checkKaiin(String kaiincd) {

        // 検索条件の設定
        S02M081TEntityKey key = new S02M081TEntityKey();
        key.setKaiincd(kaiincd);

        // 会員マスタの取得
        S02M081TEntity entity = s02m081tDao.selectByPrimaryKey(key);

        // 会員マスタにデータが存在しない場合
        if (ObjectUtils.isEmpty(entity)) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.kaiincd", "会員コード"), MessageID.W084, "会員コード");
        }

        // 会員マスタ.有効区分=1(有効)以外の場合、エラー
        if (!CommonConstants.YUKOKBN_1.equals(entity.getYukokbn())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.kaiincd", "会員コード"), MessageID.W123, "会員コード");
        }
    }

    /**
     * 紹介者数取得
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @return 紹介者数
     */
    private String getShoshakazu(String apkihoncd, String shopcd, String tokuisakicd) {
        // 検索条件の設定
        S02M013TEntityExample s02m013tExample = new S02M013TEntityExample();
        s02m013tExample.createCriteria().andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd)
            .andShoukaitokuisakicdEqualTo(tokuisakicd);

        // 得意先マスタの件数を取得する
        long cnt = s02m013tDao.countByExample(s02m013tExample);

        return String.valueOf(cnt);
    }

    /**
     * 会員履歴作成処理
     * 
     * @param dto 更新用DTO
     */
    private void createKaiinRireki(B0501W02UpdateDto dto) {
        try {
            // 会員履歴テーブルの作成
            s03t023tDao.createKaiinRireki(dto.getApkihoncd(), // ＡＰ基本コード
                dto.getShopcd(), // ショップコード
                dto.getTokuisakicd(), // 得意先コード
                dto.getShoukaitokuisakicdbefore(), // 変更前紹介得意先コード
                getSkysakicd(dto), // 請求先コード
                dto.getLoginUserApkihoncd(), // 担当者ＡＰ基本コード
                dto.getLoginUserShopcd(), // 担当者ショップコード
                dto.getLoginUserTnshacd(), // 担当者コード
                dto.getIpAddress(), // ＩＰアドレス
                dto.getProgramId() // プログラムＩＤ
            );
        } catch (RuntimeException e) {
            // エラーの場合スルーする
            // throw BusinessExceptionFactory.validationFailed(MessageID.E002);
        }
    }

    /**
     * 得意先ポイントランクマスタ ステージ切替日重複チェック処理
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @param tekiyoudd 切替日
     * @param dto 更新用DTO
     */
    public void checkTokuPointRankDuplicate(String apkihoncd, String shopcd, String tokuisakicd,
        String tekiyoudd) {

        // 得意先ポイントランクマスタ検索条件の設定
        S02M204TEntityKey key = new S02M204TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setTokuisakicd(tokuisakicd);
        key.setTekiyoudd(tekiyoudd);

        // 得意先ポイントランクマスタの取得
        S02M204TEntity entity = s02m204tDao.selectByPrimaryKey(key);

        // 得意先ポイントランクマスタにデータが存在する場合
        if (!ObjectUtils.isEmpty(entity)) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("saleBean.tekiyoudd", "ステージ切替日"), MessageID.W103, "ステージ切替日");
        }
    }

    /**
     * ステージ切替日のポイントランク年次再評価の更新月日チェック
     * 
     * @param tekiyoudd 切替日
     * @return 預かりボトル名称
     */
    public void checkPointRankUpdateDD(String tekiyoudd) {

        // 検索条件作成
        S02M006TDto conditionDto = new S02M006TDto();
        conditionDto.setApkihoncd(CommonConstants.APCD_001);
        conditionDto.setShopcd(CommonConstants.SYS_SHOPCD_000);
        conditionDto.setSetteino(CommonConstants.SYS_SETTEINO_949);
        conditionDto.setSetteicd(CommonConstants.SETTEICD_949_1);

        // システム管理マスタから取得
        String pointRankDD = s02m006tService.search(conditionDto);

        // ステージ切替日の月日を取得
        String tekiyouddMMDD = tekiyoudd.substring(tekiyoudd.length() - 4);
        // ステージ切替日の月日がポイントランク年次再評価日と一致する場合はエラー
        if (tekiyouddMMDD.equals(pointRankDD)) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("saleBean.tekiyoudd", "ステージ切替日"), MessageID.W159, "ステージ切替日",
                "ポイントステージ年次再評価日");
        }
    }
}
