package jp.co.aquaclara.accis.web.domain.service.c1002;

import java.math.BigDecimal;
import java.util.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M003TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M006TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M017TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M041TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M046TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M066TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M091TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M202TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M203TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02V001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T004TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S10T004TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.Z1808B01Dao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M003TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M003TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M006TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M006TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M041TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M041TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M066TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M066TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M091TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M202TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M202TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M203TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M203TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T004TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T004TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.Z1808B01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S10T004TC1002W01Entity;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M046TDto;
import jp.co.aquaclara.accis.web.domain.service.master.S02M006TService;
import jp.co.aquaclara.accis.web.domain.service.master.S02M046TService;

/**
 * 売上入力_共通Service
 */
@Service
public class C1002W01CommonService {

    /**
     * システム管理マスタService
     */
    @Autowired
    private S02M006TService s02m006tService;

    /**
     * ショップマスタService
     */
    @Autowired
    private S02M046TService s02m046tService;

    /**
     * 担当者マスタDao
     */
    @Autowired
    private S02M003TDao s02m003tDao;

    /**
     * システム管理マスタDao
     */
    @Autowired
    private S02M006TDao s02m006tDao;

    /**
     * 請求先マスタDao
     */
    @Autowired
    private S02M017TDao s02m017tDao;

    /**
     * 倉庫マスタDao
     */
    @Autowired
    private S02M041TDao s02m041tDao;

    /**
     * ショップマスタDao
     */
    @Autowired
    private S02M046TDao s02m046tDao;

    /**
     * 得意先別セット商品単価マスタDao
     */
    @Autowired
    private S02M066TDao s02m066tDao;

    /**
     * 消費税率管理マスタDao
     */
    @Autowired
    private S02M091TDao s02m091tDao;

    /**
     * ショップポイントマスタDao
     */
    @Autowired
    private S02M202TDao s02m202tDao;

    /**
     * 得意先ポイントマスタDao
     */
    @Autowired
    private S02M203TDao s02m203tDao;

    /**
     * 商品マスタビューDao
     */
    @Autowired
    private S02V001TDao s02v001tDao;

    /**
     * 請求残高テーブルDao
     */
    @Autowired
    private S07T001TDao s07t001tDao;

    /**
     * 請求締処理管理テーブルDao
     */
    @Autowired
    private S07T004TDao s07t004tDao;

    /**
     * 入金消込履歴テーブルDao
     */
    @Autowired
    private S10T004TDao s10t004tDao;

    /**
     * 消費税金額計算バッチDao
     */
    @Autowired
    private Z1808B01Dao z1808b01Dao;

    /**
     * 担当者マスタ検索
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tnshacd 担当者コード
     * @return entity 担当者マスタレコード
     */
    public S02M003TEntity searchTnsha(String apkihoncd, String shopcd, String tnshacd) {

        S02M003TEntityKey key = new S02M003TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setTnshacd(tnshacd);

        return s02m003tDao.selectByPrimaryKey(key);
    }

    /**
     * 担当者名称検索
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tnshacd 担当者コード
     * @return 担当者名称
     */
    public String getTnshamei(String apkihoncd, String shopcd, String tnshacd) {

        S02M003TEntityKey key = new S02M003TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setTnshacd(tnshacd);

        S02M003TEntity entity = s02m003tDao.selectByPrimaryKey(key);

        return !ObjectUtils.isEmpty(entity) ? entity.getTnshamei() : "";
    }

    /**
     * 請求先マスタ検索
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param skysakicd 請求先コード
     * @return entity 請求先マスタレコード
     */
    public S02M017TEntity searchSkysaki(String apkihoncd, String shopcd, String skysakicd) {

        S02M017TEntityKey key = new S02M017TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setSkysakicd(skysakicd);

        return s02m017tDao.selectByPrimaryKey(key);
    }

    /**
     * 倉庫マスタ検索
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param sokocd 倉庫コード
     * @return entity 倉庫マスタレコード
     */
    public S02M041TEntity searchSoko(String apkihoncd, String shopcd, String sokocd) {

        S02M041TEntityKey key = new S02M041TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setSokocd(sokocd);

        return s02m041tDao.selectByPrimaryKey(key);
    }

    /**
     * ショップマスタ検索
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @return entity ショップマスタレコード
     */
    public S02M046TEntity searchShop(String apkihoncd, String shopcd) {

        S02M046TEntityKey key = new S02M046TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);

        return s02m046tDao.selectByPrimaryKey(key);
    }

    /**
     * 得意先別セット商品単価マスタ検索
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param stshncd セット商品コード
     * @param tokuisakicd 得意先コード
     * @param tekiyostrtdd 適用開始日
     * @return entity 得意先別セット商品単価マスタレコード
     */
    public S02M066TEntity searchTokuisakibetsuSetShohinTanka(String apkihoncd, String shopcd,
        String stshncd, String tokuisakicd, String tekiyostrtdd) {

        S02M066TEntityKey key = new S02M066TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setStshncd(stshncd);
        key.setTokuisakicd(tokuisakicd);
        key.setTekiyostrtdd(tekiyostrtdd);

        return s02m066tDao.selectByPrimaryKey(key);
    }

    /**
     * 商品マスタビュー検索
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param shncd 商品コード
     * @return entity 商品マスタビューレコード
     */
    public S02V001TEntity searchShohin(String apkihoncd, String shopcd, String shncd) {

        S02V001TEntityKey key = new S02V001TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setShncd(shncd);

        return s02v001tDao.selectByPrimaryKey(key);
    }

    /**
     * 請求残高テーブル検索
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param skysakicd 請求書番号
     * @param skyshonoedano 請求書番号枝番
     * @return 請求残高テーブルレコード
     */
    public S07T001TEntity searchSeikyuZandaka(String apkihoncd, String shopcd, String skyshono,
        String skyshonoedano) {

        S07T001TEntityKey key = new S07T001TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setSkyshono(skyshono);
        key.setSkyshonoedano(Short.parseShort(skyshonoedano));

        return s07t001tDao.selectByPrimaryKey(key);
    }

    /**
     * 請求締処理管理テーブル検索
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @return 請求締処理管理テーブルレコード
     */
    public S07T004TEntity searchSeikyuShimeShoriKanri(String apkihoncd, String shopcd) {

        S07T004TEntityKey key = new S07T004TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);

        return s07t004tDao.selectByPrimaryKey(key);
    }

    /**
     * 商品コード（セット商品展開時の税調整商品）を取得する
     * 
     * @return 商品コード（税調整商品）
     */
    public String getZeichoseihncd() {
        return s02m006tService.searchForMoji(CommonConstants.APCD_000,
            CommonConstants.SYS_SHOPCD_000, CommonConstants.SYS_SETTEINO_166,
            CommonConstants.SETTEICD_166_0);
    }

    /**
     * 大分類コード（サーバー）取得処理
     * 
     * @return 大分類コード（サーバー）
     */
    public String getServerLctgrycd() {

        // システム管理マスタ検索
        S02M006TEntityKey key = new S02M006TEntityKey();
        key.setApkihoncd(CommonConstants.APCD_000);
        key.setShopcd(CommonConstants.SYS_SHOPCD_000);
        key.setSetteino(CommonConstants.SYS_SETTEINO_096);
        key.setSetteicd(CommonConstants.SETTEICD_096_4);
        key.setRcrdkbn(CommonConstants.KBN_RECORD_MEISAI);

        S02M006TEntity entity = s02m006tDao.selectByPrimaryKey(key);

        return entity.getMojisetteiatai();
    }

    /**
     * 中分類コード（サーバー）取得処理
     * 
     * @return 中分類コード（サーバー）
     */
    public String getServerMctgrycd() {

        // システム管理マスタ検索
        S02M006TEntityKey key = new S02M006TEntityKey();
        key.setApkihoncd(CommonConstants.APCD_000);
        key.setShopcd(CommonConstants.SYS_SHOPCD_000);
        key.setSetteino(CommonConstants.SYS_SETTEINO_134);
        key.setSetteicd(CommonConstants.SETTEICD_134_41);
        key.setRcrdkbn(CommonConstants.KBN_RECORD_MEISAI);

        S02M006TEntity entity = s02m006tDao.selectByPrimaryKey(key);

        return entity.getMojisetteiatai();
    }

    /**
     * 大分類コード（製品水）取得処理
     * 
     * @return 大分類コード（製品水）
     */
    public String getWaterLctgrycd() {

        // システム管理マスタ検索
        S02M006TEntityKey key = new S02M006TEntityKey();
        key.setApkihoncd(CommonConstants.APCD_000);
        key.setShopcd(CommonConstants.SYS_SHOPCD_000);
        key.setSetteino(CommonConstants.SYS_SETTEINO_096);
        key.setSetteicd(CommonConstants.SETTEICD_096_1);
        key.setRcrdkbn(CommonConstants.KBN_RECORD_MEISAI);

        S02M006TEntity entity = s02m006tDao.selectByPrimaryKey(key);

        return entity.getMojisetteiatai();
    }

    /**
     * 請求残高消込済金額取得
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param uridnpyno 売上伝票番号
     * @param skysakicd 請求先コード
     * @param tokuisakicd 得意先コード
     * @param skyshono 請求書番号
     * @return 請求残高消込済金額情報
     */
    public S10T004TC1002W01Entity searchNyknkshkm(String apkihoncd, String shopcd, String uridnpyno,
        String skysakicd, String tokuisakicd, String skyshono) {

        return s10t004tDao.selectByC1002W01(apkihoncd, shopcd, uridnpyno, skysakicd, tokuisakicd,
            skyshono);
    }

    /**
     * インボイス施行日と対象日を比較
     * 
     * @param invoicshikoymd String
     * @param targetDate String
     * @return boolean
     */
    public boolean isInvoiceEnforced(String invoicshikoymd, String targetDate) {

        if (StringUtils.isEmpty(invoicshikoymd) || StringUtils.isEmpty(targetDate)) {
            return false;
        }

        // インボイス施行日 <= 売上日の場合 true
        return DateTimeUtil.isLater(invoicshikoymd, targetDate);
    }

    /**
     * 消費税率取得
     * 
     * @param taxkbn 税区分
     * @param targetDate 対象日
     * @return 消費税率（小数）
     */
    public BigDecimal getShohiTaxRate(String taxkbn, String targetDate) {
        if (StringUtils.isEmpty(taxkbn)) {
            return BigDecimal.ZERO;
        }

        // 消費税率管理マスタ検索
        S02M091TEntityExample condition = new S02M091TEntityExample();
        condition.createCriteria()
            // 税区分
            .andTaxkbnEqualTo(taxkbn)
            // 適用開始日
            .andTekiyostrtddLessThanOrEqualTo(targetDate)
            // 適用終了日
            .andTekiyoshryddGreaterThanOrEqualTo(targetDate)
            // 有効区分
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

        // 取得できない場合は0を返す
        return s02m091tDao.selectByExample(condition).stream().findFirst()
            .map(entity -> entity.getShohitax()).orElse(BigDecimal.ZERO).scaleByPowerOfTen(-2);
    }

    /**
     * 売上金額端数計算区分取得する
     * 
     * @param apkihoncd
     * @param shopcd
     * @return 売上金額端数計算区分
     */
    public String searchUrikinhasukeisankbn(String apkihoncd, String shopcd) {

        S02M046TDto resultDto = s02m046tService.getByPrimaryKey(apkihoncd, shopcd, true);

        return resultDto.getUrikinhasukeisankbn();
    }


    /**
     * 消費税端数計算処理
     * 
     * @param taxhasukeisankbn 税端数計算区分
     * @param shohitax 税率
     * @param kin 金額
     * @return 端数計算後の消費税金額
     */
    public Integer callRoundValue(String taxhasukeisankbn, String shohitax, int kin) {
        Z1808B01Entity entity = new Z1808B01Entity();
        // 計算対象値(合計金額 * 税率)
        entity.setValue(kin * (Double.parseDouble(shohitax)));
        // 端数計算区分
        entity.setHasukeisankbn(taxhasukeisankbn);

        // 消費税金額計算バッチの端数計算処理呼出
        return z1808b01Dao.roundValue(entity);
    }

    /**
     * 在庫締年月算出
     * 
     * @param nyushukkodd 入出庫日
     * @param zaikoprcsdydm 在庫処理年月
     * @param shopShimedd 締日（ショップマスタ）
     * @return shimedydm 在庫締年月日
     */
    public String getShimedydm(String nyushukkodd, String zaikoprcsdydm, String shopShimedd) {
        if (nyushukkodd == null || (nyushukkodd.length() != 8)) {
            return null;
        }
        if (zaikoprcsdydm == null || zaikoprcsdydm.length() != 6) {
            return null;
        }
        if (shopShimedd == null || shopShimedd.length() != 2) {
            return null;
        }

        // 締年月判定
        String nyushukkoYm = nyushukkodd;
        if (nyushukkodd.length() > 6) {
            nyushukkoYm = nyushukkodd.substring(0, 6);
        }

        if (Integer.parseInt(nyushukkodd.substring(6, 8)) > Integer.parseInt(shopShimedd)) {
            Calendar cal = DateTimeUtil.getDateObject(nyushukkodd);
            cal.add(Calendar.MONTH, 1);
            nyushukkoYm = DateTimeUtil.getDateString(cal).substring(0, 6);
        }

        long ym1 = Long.parseLong(nyushukkoYm);
        long ym2 = Long.parseLong(zaikoprcsdydm);

        if (ym1 <= ym2) {
            return String.valueOf(ym2);
        }

        return String.valueOf(ym1);
    }

    /**
     * 配達方法、売上日から現在価格を取得する
     * 
     * @param shohinEntity S02V001TEntity
     * @param dlvryhoho 配達方法
     * @param uridd 売上日
     * @return BigDecimal 現在価格
     */
    public BigDecimal getCurrentPrice(S02V001TEntity shohinEntity, String dlvryhoho, String uridd) {

        // 配達方法が「配達」の場合
        // 標準価格を判定して設定
        if (CommonConstants.KBN_DLVRY_HOHO_HAITATSU.equals(dlvryhoho)) {

            return new BigDecimal(ItemUtil.getItem(shohinEntity.getGenzhyojunprice().toString(),
                shohinEntity.getShinhyojunprice().toString(),
                shohinEntity.getHyojunpricekirikaedd(), uridd));
        }

        // 配達方法が「持ち帰り」の場合
        // 持ち帰り価格を判定して設定
        return new BigDecimal(ItemUtil.getItem(shohinEntity.getGenzmochikaeriprice().toString(),
            shohinEntity.getShinmochikaeriprice().toString(),
            shohinEntity.getHyojunpricekirikaedd(), uridd));
    }

    /**
     * ショップポイントマスタ検索
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @return entity ショップマスタレコード
     */
    public S02M202TEntity searchShopPoint(String apkihoncd, String shopcd) {

        S02M202TEntityKey key = new S02M202TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);

        return s02m202tDao.selectByPrimaryKey(key);
    }

    /**
     * 得意先ポイントマスタ検索
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @return entity ショップマスタレコード
     */
    public S02M203TEntity searchTokuisakiPoint(String apkihoncd, String shopcd, String tokuisakicd) {

        S02M203TEntityKey key = new S02M203TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setTokuisakicd(tokuisakicd);

        return s02m203tDao.selectByPrimaryKey(key);
    }

}
