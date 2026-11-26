package jp.co.aquaclara.accis.web.domain.service.b1501;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S01M004TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M007TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M017TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M046TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S06T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S10T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S01M004TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S01M004TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M007TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M007TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S10T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S06T001TB1501W11Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TB1501W11Entity;
import jp.co.aquaclara.accis.web.domain.dto.b1501.B1501W11SearchConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.b1501.B1501W11SearchResultCurrDto;
import jp.co.aquaclara.accis.web.domain.dto.b1501.B1501W11SearchResultDto;
import jp.co.aquaclara.accis.web.domain.dto.b1501.B1501W11SearchResultListDto;

/**
 * B1001W01_売掛元帳（詳細）サービスクラス
 * 
 * @author accisdev037
 *
 */
@Service
public class B1501W11Service {

    // 基本商品マスタDAO
    @Autowired
    private S02M007TDao s02m007tDao;

    // 請求先マスタDAO
    @Autowired
    private S02M017TDao s02m017tDao;

    // ショップマスタDAO
    @Autowired
    private S02M046TDao s02m046tDao;

    // 売上テーブルDAO
    @Autowired
    private S06T001TDao s06t001tDao;

    // 請求残高テーブルDAO
    @Autowired
    private S07T001TDao s07t001tDao;

    // 入金明細テーブルDAO
    @Autowired
    private S10T001TDao s10t001tDao;

    /** コンディションマスタDAO */
    @Autowired
    private S01M004TDao s01m004tDao;

    private static final String ZERO = BigDecimal.ZERO.stripTrailingZeros().toPlainString();

    /**
     * 請求入金照会検索処理
     * 
     * @param dto 請求入金照会検索条件DTO
     * @return 請求入金照会検索結果DTO
     */
    public B1501W11SearchResultDto search(B1501W11SearchConditionDto dto) {

        // 各種マスタ情報取得
        B1501W11SearchResultDto ret = getMasterInf(dto);

        // 明細行取得
        S07T001TB1501W11Entity cond = createSearchEntity(dto);
        ret.setDetailList(createResultList(s07t001tDao.selectByB1501W11(cond)));

        // 現在行取得
        S06T001TB1501W11Entity curCond = createCurrentSearchEntity(dto);
        Optional<S06T001TB1501W11Entity> curEntity =
            Optional.ofNullable(s06t001tDao.selectByB1501W11(curCond));
        ret.setCurrentInfo(createCurrentResult(curEntity.orElse(createBlankCurEntity(dto))));
        // 現在行情報取得(当月入金、前月残高)
        getCurrentInf(dto, ret.getCurrentInfo());

        return ret;
    }

    /**
     * 各種マスタ情報取得
     * 
     * @param dto 請求入金照会検索条件DTO
     * @return 請求入金照会検索結果DTO
     */
    private B1501W11SearchResultDto getMasterInf(B1501W11SearchConditionDto dto) {

        // 請求先管理区分取得
        S02M017TEntity skRet = s02m017tDao.selectByPrimaryKey(createSkySakiCond(dto));

        // 税計算区分設定用データ取得
        S02M046TEntity shopRet = s02m046tDao.selectByPrimaryKey(createShopCond(dto));

        B1501W11SearchResultDto d = new B1501W11SearchResultDto();

        d.setSkysakikanrikbn(skRet.getSkysakikanrikbn()); // 請求先管理区分
        d.setTaxkeisankbnurikirikaedd(shopRet.getTaxkeisankbnurikirikaedd()); // 税計算区分売上切替日
        d.setGenztaxkeisankbnuri(shopRet.getGenztaxkeisankbnuri()); // 現税計算区分売上
        d.setShintaxkeisankbnuri(shopRet.getShintaxkeisankbnuri()); // 新税計算区分売上
        d.setInvoicshikoymd(shopRet.getInvoicshikoymd()); // インボイス施行日
        d.setShimedd(shopRet.getShimedd()); // 締日

        return d;
    }

    /**
     * 請求先マスタ検索条件を生成する。
     * 
     * @param dto 請求入金照会検索条件DTO
     * @return 請求先マスタEntityKey
     */
    private S02M017TEntityKey createSkySakiCond(B1501W11SearchConditionDto dto) {

        S02M017TEntityKey e = new S02M017TEntityKey();

        // キーentityの生成
        e.setApkihoncd(dto.getApkihoncd()); // AP基本コード
        e.setShopcd(dto.getShopcd()); // ショップコード
        e.setSkysakicd(dto.getSkysakicd()); // 請求先コード

        return e;
    }

    /**
     * ショップマスタ検索条件を生成する。
     * 
     * @param dto 請求入金照会検索条件DTO
     * @return ショップマスタEntityKey
     */
    private S02M046TEntityKey createShopCond(B1501W11SearchConditionDto dto) {

        S02M046TEntityKey e = new S02M046TEntityKey();

        // キーentityの生成
        e.setApkihoncd(dto.getApkihoncd()); // AP基本コード
        e.setShopcd(dto.getShopcd()); // ショップコード

        return e;
    }

    /**
     * 当月入金額（請求入金照会)取得条件を生成する。
     * 
     * @param dto 当月入金額（請求入金照会)取得条件DTO
     * @return 当月入金額（請求入金照会)取得用entity
     */
    private S10T001TEntity creatPaymentCond(B1501W11SearchConditionDto dto) {
        // 検索条件をEntityに展開する
        S10T001TEntity e = new S10T001TEntity();

        // AP基本コード
        e.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        e.setShopcd(dto.getShopcd());
        // 請求先コード
        e.setSkysakicd(dto.getSkysakicd());

        return e;
    }

    /**
     * 前月残高（請求入金照会)検索条件を生成する。
     * 
     * @param dto 請求入金照会検索条件DTO
     * @return 前月残高（請求入金照会）取得用entity
     */
    private S07T001TEntity createBalanceCond(B1501W11SearchConditionDto dto) {
        // 検索条件をCustomEntityに展開する
        S07T001TEntity e = new S07T001TEntity();

        // AP基本コード
        e.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        e.setShopcd(dto.getShopcd());
        // 請求先コード
        e.setSkysakicd(dto.getSkysakicd());

        return e;
    }

    /**
     * 明細行データ（請求入金照会)検索条件を生成する。
     * 
     * @param dto 請求入金照会検索条件DTO
     * @return 明細行データ（請求入金照会）取得用entity
     */
    private S07T001TB1501W11Entity createSearchEntity(B1501W11SearchConditionDto dto) {
        // 検索条件をCustomEntityに展開する
        S07T001TB1501W11Entity e = new S07T001TB1501W11Entity();

        // AP基本コード
        e.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        e.setShopcd(dto.getShopcd());
        // 締年月
        e.setShimedydm(dto.getDydm());
        // 請求先コード
        e.setSkysakicd(dto.getSkysakicd());

        return e;
    }

    /**
     * 検索結果（明細行)をControllerへ返却する形式へ変換する。
     * 
     * @param searchResultList 明細行データ(請求入金照会)entityリスト
     * @return 請求入金照会検索結果(明細行)DTOリスト
     */
    private List<B1501W11SearchResultListDto> createResultList(
        List<S07T001TB1501W11Entity> searchResultList) {

        List<B1501W11SearchResultListDto> result = new ArrayList<>();

        for (S07T001TB1501W11Entity e : searchResultList) {
            B1501W11SearchResultListDto d = new B1501W11SearchResultListDto();

            d.setSkysakicd(e.getSkysakicd()); // 請求先コード
            d.setShimedydm(e.getShimedydm()); // 締年月
            d.setShimedd(e.getShimedd()); // 締日
            d.setKaishuhohokbn(e.getKaishuhohokbn()); // 回収方法区分
            d.setKaishuhohonm(e.getKaishuhohonm()); // 回収方法名称
            // NULL判定不要。無条件で文字列型にキャストする。
            // (SQLでNVLを使用し0に置換しているのでnullの項目値はあり得ない)
            d.setRgrayuryo(String.valueOf(e.getRgrayuryo())); // レギュラー有料
            d.setRgramuryo(String.valueOf(e.getRgramuryo())); // レギュラー無料
            d.setSrmyuryo(String.valueOf(e.getSrmyuryo())); // スリム有料
            d.setSrmmuryo(String.valueOf(e.getSrmmuryo())); // スリム無料
            d.setSonotayuryo(String.valueOf(e.getSonotayuryo())); // その他有料
            d.setSonotamuryo(String.valueOf(e.getSonotamuryo())); // その他無料
            d.setSeihinmizuuri(String.valueOf(e.getSeihinmizuuri())); // 製品水売上
            d.setRtlrkn(String.valueOf(e.getRtlrkn())); // レンタル料金
            d.setSonotaurikin(String.valueOf(e.getSonotaurikin())); // その他売上金額
            d.setKonkaitaxnukiurikin(String.valueOf(e.getKonkaitaxnukiurikin())); // 今回税抜売上金額
            d.setKonkaishohitaxkin(String.valueOf(e.getKonkaishohitaxkin())); // 今回消費税金額
            d.setTaxkomiurikeikin(String.valueOf(e.getTaxkomiurikeikin())); // 税込売上金額
            d.setZnkkurikoshikin(String.valueOf(e.getZnkkurikoshikin())); // 前回繰越金額
            d.setKonkainyknkin(String.valueOf(e.getKonkainyknkin())); // 今回入金金額
            d.setKurikoshikin(String.valueOf(e.getKurikoshikin())); // 繰越金額
            d.setRenban(String.valueOf(e.getRenban())); // 連番

            result.add(d);
        }

        return result;
    }

    /**
     * 現在行データ（請求入金照会)検索条件を生成する。
     * 
     * @param dto 請求入金照会検索条件DTO
     * @return 現在行データ（請求入金照会）取得用entity
     */
    private S06T001TB1501W11Entity createCurrentSearchEntity(B1501W11SearchConditionDto dto) {
        // 検索条件をCustomEntityに展開する
        S06T001TB1501W11Entity e = new S06T001TB1501W11Entity();

        // AP基本コード
        e.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        e.setShopcd(dto.getShopcd());
        // 請求先コード
        e.setSkysakicd(dto.getSkysakicd());
        // レギュラー商品コード
        e.setRegshncdlist(createShncdList(CommonConstants.SCTGRYCD_001));
        // スリム商品コード
        e.setSlmshncdlist(createShncdList(CommonConstants.SCTGRYCD_002));

        return e;
    }

    /**
     * 商品コードリストを作成する。
     *
     * @param sctgrycd 小分類コード
     * @return 商品コードリスト
     */
    private List<String> createShncdList(String sctgrycd) {

        List<S02M007TEntity> result = s02m007tDao.selectByExample(createShnCdEntity(sctgrycd));

        // 商品コードリストの生成
        List<String> list = new ArrayList<>();
        for (S02M007TEntity e : result) {
            list.add(e.getShncd());
        }
        return list;
    }

    /**
     * 商品コードリスト検索条件を生成する。
     * 
     * @param sctgrycd 小分類コード
     * @return 商品コード検索用entity
     */
    private S02M007TEntityExample createShnCdEntity(String sctgrycd) {

        S02M007TEntityExample entity = new S02M007TEntityExample();

        // クライテリア(SQL条件)の生成
        entity.createCriteria().andApkihoncdEqualTo(CommonConstants.APCD_001)
            .andStkbnEqualTo(CommonConstants.STKBN_0)
            .andLctgrycdEqualTo(CommonConstants.LCTGRYCD_01)
            .andMctgrycdEqualTo(CommonConstants.MCTGRYCD_001)
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1).andSctgrycdEqualTo(sctgrycd);

        return entity;
    }

    /**
     * 検索結果（現在行)をControllerへ返却する形式へ変換する。
     * 
     * @param searchResult 現在行データ(請求入金照会)entity
     * @return 請求入金照会検索結果(現在行)DTOリスト
     */
    private B1501W11SearchResultCurrDto createCurrentResult(S06T001TB1501W11Entity e) {
        B1501W11SearchResultCurrDto d = new B1501W11SearchResultCurrDto();
        d.setKaishuhohokbn(e.getKaishuhohokbn()); // 回収方法区分
        d.setKaishuhohonm(e.getKaishuhohonm()); // 回収方法名称
        // NULL判定不要。無条件で文字列型にキャストする。
        // (SQLでNVLを使用し0に置換しているのでnullの項目値はあり得ない)
        d.setRgrayuryo(String.valueOf(e.getRgrayuryo())); // レギュラー有料
        d.setRgramuryo(String.valueOf(e.getRgramuryo())); // レギュラー無料
        d.setSrmyuryo(String.valueOf(e.getSrmyuryo())); // スリム有料
        d.setSrmmuryo(String.valueOf(e.getSrmmuryo())); // スリム無料
        d.setSonotayuryo(String.valueOf(e.getSonotayuryo())); // その他有料
        d.setSonotamuryo(String.valueOf(e.getSonotamuryo())); // その他無料
        d.setSeihinmizuuri(String.valueOf(e.getSeihinmizuuri())); // 製品水売上
        d.setRtlrkn(String.valueOf(e.getRtlrkn())); // レンタル料金
        d.setSonotaurikin(String.valueOf(e.getSonotaurikin())); // その他売上金額
        d.setKonkaitaxnukiurikin(String.valueOf(e.getKonkaitaxnukiurikin())); // 当月売上計
        d.setShohitaxkin(String.valueOf(e.getKonkaishohitaxkin())); // 消費税額
        d.setTaxkomiurikeikin(String.valueOf(e.getTaxkomiurikeikin())); // 当月税込売上計
        return d;
    }

    /**
     * 空の現在行Entityを生成する
     * 
     * @param conditionDto 検索条件DTO
     * @return 現在行Entity
     */
    private S06T001TB1501W11Entity createBlankCurEntity(B1501W11SearchConditionDto conditionDto) {
        S06T001TB1501W11Entity entity = new S06T001TB1501W11Entity();
        // 請求先マスタから請求先の回収方法区分を取得
        S02M017TEntityKey skysakiKey = new S02M017TEntityKey();
        skysakiKey.setApkihoncd(conditionDto.getApkihoncd());
        skysakiKey.setShopcd(conditionDto.getShopcd());
        skysakiKey.setSkysakicd(conditionDto.getSkysakicd());
        S02M017TEntity skysakiEntity = s02m017tDao.selectByPrimaryKey(skysakiKey);
        entity.setKaishuhohokbn(skysakiEntity.getKaishuhohokbn());
        // コンディションマスタから回収方法区分の名称を取得する(回収方法区分は文字列のため、検索条件は"文字設定値"にて行う)
        S01M004TEntityExample conditionEntity = new S01M004TEntityExample();
        conditionEntity.createCriteria().andConditionidEqualTo(CommonConstants.CONDITION_ID_CN160)
            .andSetteiataiEqualTo(skysakiEntity.getKaishuhohokbn());
        List<S01M004TEntity> conditionEntityList = s01m004tDao.selectByExample(conditionEntity);
        String kaishuhohokbnnm = "";
        if (!ObjectUtils.isEmpty(conditionEntityList)) {
            kaishuhohokbnnm = conditionEntityList.get(0).getItemnm();
        }
        entity.setKaishuhohonm(kaishuhohokbnnm);
        // 数値項目は0で初期化する
        entity.setRgrayuryo(BigDecimal.ZERO);
        entity.setRgramuryo(BigDecimal.ZERO);
        entity.setSrmyuryo(BigDecimal.ZERO);
        entity.setSrmmuryo(BigDecimal.ZERO);
        entity.setSonotayuryo(BigDecimal.ZERO);
        entity.setSonotamuryo(BigDecimal.ZERO);
        entity.setSeihinmizuuri(BigDecimal.ZERO);
        entity.setRtlrkn(BigDecimal.ZERO);
        entity.setSonotaurikin(BigDecimal.ZERO);
        entity.setKonkaitaxnukiurikin(BigDecimal.ZERO);
        entity.setKonkaishohitaxkin(BigDecimal.ZERO);
        entity.setTaxkomiurikeikin(BigDecimal.ZERO);
        return entity;
    }

    /**
     * 現在行入金、残高情報取得
     * 
     * @param dto 請求入金照会検索条件DTO
     * @param retDto 現在行検索結果DTO
     */
    private void getCurrentInf(B1501W11SearchConditionDto dto, B1501W11SearchResultCurrDto retDto) {

        // 当月入金（現在行)データ取得
        Long payAmt = s10t001tDao.selectByB1501W11(creatPaymentCond(dto));

        // 前月残高（現在行)データ取得
        Long preblc = s07t001tDao.selectByB1501W11Current(createBalanceCond(dto));

        retDto.setNyknkin(ObjectUtils.isEmpty(payAmt) ? ZERO : String.valueOf(payAmt)); // 当月入金
        retDto.setKurikoshikin(ObjectUtils.isEmpty(preblc) ? ZERO : String.valueOf(preblc)); // 前月残高
    }
}
