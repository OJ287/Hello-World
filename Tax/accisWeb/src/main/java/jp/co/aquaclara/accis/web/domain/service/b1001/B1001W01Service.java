package jp.co.aquaclara.accis.web.domain.service.b1001;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M013TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M017TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M046TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02V001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S06T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S09T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S06T001TB1001W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TB1001W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S09T001TB1001W01Entity;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.web.domain.dto.b1001.B1001W01SearchConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.b1001.B1001W01SearchResultDto;
import jp.co.aquaclara.accis.web.domain.dto.b1001.B1001W01SearchResultListDto;
import jp.co.aquaclara.accis.web.domain.dto.b1001.B1001W01SearchSeikyu1ConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.b1001.B1001W01SearchSeikyu1ResultDto;
import jp.co.aquaclara.accis.web.domain.dto.b1001.B1001W01SearchSeikyu2ConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.b1001.B1001W01SearchSeikyu2ResultDto;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import jp.co.aquaclara.accis.web.exception.dto.FormField;

/**
 * B1001W01_売掛元帳（詳細）サービスクラス
 * 
 * @author accisdev037
 *
 */
@Service
public class B1001W01Service {

    // 商品マスタビューDAO
    @Autowired
    private S02V001TDao s02v001tDao;

    // 得意先マスタDAO
    @Autowired
    private S02M013TDao s02m013tDao;

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

    // 決済履歴テーブルDAO
    @Autowired
    private S09T001TDao s09t001tDao;

    /**
     * 売掛元帳（詳細）請求先1検索処理
     * 
     * @param dto 売掛元帳（詳細）請求先情報1検索条件DTO
     * @return 初期表示1（売掛元帳（詳細））検索結果DTOリスト
     */
    public List<B1001W01SearchSeikyu1ResultDto> searchSeikyu1(
        B1001W01SearchSeikyu1ConditionDto dto) {

        // 消費税フラグの取得
        String taxFlg = getTaxFlg(dto);

        // 検索条件を生成する
        S07T001TB1001W01Entity e = createSearchSeikyu1Entity(dto);

        return createSeikyu1ResultList(s07t001tDao.selectByB1001W01(e), taxFlg);
    }

    /**
     * 売掛元帳（詳細）請求先2検索処理
     * 
     * @param dto 売掛元帳（詳細）請求先情報2検索条件DTO
     * @return 初期表示2（売掛元帳（詳細））検索結果DTOリスト
     */
    public List<B1001W01SearchSeikyu2ResultDto> searchSeikyu2(
        B1001W01SearchSeikyu2ConditionDto dto) {

        // 検索条件を生成する
        S09T001TB1001W01Entity e = createSearchSeikyu2Entity(dto);

        return createSeikyu2ResultList(s09t001tDao.selectByB1001W01(e));
    }

    /**
     * 売掛元帳（詳細）検索処理
     * 
     * @param dto 売掛元帳（詳細）検索条件DTO
     * @return 売掛元帳（詳細）検索結果DTO
     */
    public B1001W01SearchResultDto search(B1001W01SearchConditionDto dto) {

        // 商品コード存在チェック（商品コードの入力があった場合)
        if (!StringUtils.isEmpty(dto.getShncd())) {
            checkShnCdExist(dto);
        }

        // 検索条件を生成する
        S06T001TB1001W01Entity e = createSearchEntity(dto);

        return createResultList(s06t001tDao.selectByB1001W01(e));
    }

    /**
     * 消費税フラグを取得する
     * 
     * @param dto 売掛元帳（詳細）請求先情報1検索条件DTO
     * @return 消費税区分
     */
    private String getTaxFlg(B1001W01SearchSeikyu1ConditionDto dto) {

        // 請求先管理区分取得
        S02M017TEntity SkRet = s02m017tDao.selectByPrimaryKey(createSkySakiCond(dto));

        // 請求先管理区分が2(卸)の場合、消費税区分に2(外税)を返却
        if (!ObjectUtils.isEmpty(SkRet)
            && CommonConstants.SKYSAKIKANRIKBN_2.equals(SkRet.getSkysakikanrikbn())) {
            return CommonConstants.KBN_TAXKEISAN_EX;
        }

        // ショップマスタ情報取得
        S02M046TEntity ShopRet = s02m046tDao.selectByPrimaryKey(createShopCond(dto));

        if (!DateTimeUtil.isLater(ShopRet.getTaxkeisankbnurikirikaedd(), dto.getOpeDate())) {
            // 税計算区分売上切替日>運用日付の場合、現税計算区分売上を返却
            return ShopRet.getGenztaxkeisankbnuri();
        }

        // それ以外の場合、新税計算区分売上を返却
        return ShopRet.getShintaxkeisankbnuri();
    }

    /**
     * 請求先マスタ検索条件を生成する。
     * 
     * @param dto 売掛元帳（詳細）請求先情報1検索条件DTO
     * @return 請求先マスタEntityKey
     */
    private S02M017TEntityKey createSkySakiCond(B1001W01SearchSeikyu1ConditionDto dto) {

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
     * @param dto 売掛元帳（詳細）請求先情報1検索条件DTO
     * @return ショップマスタEntityKey
     */
    private S02M046TEntityKey createShopCond(B1001W01SearchSeikyu1ConditionDto dto) {

        S02M046TEntityKey e = new S02M046TEntityKey();

        // キーentityの生成
        e.setApkihoncd(dto.getApkihoncd()); // AP基本コード
        e.setShopcd(dto.getShopcd()); // ショップコード

        return e;
    }

    /**
     * 初期表示1（売掛元帳（詳細））検索条件を生成する。
     * 
     * @param dto 売掛元帳（詳細）請求先情報1検索条件DTO
     * @return 初期表示1（売掛元帳（詳細））取得用entity
     */
    private S07T001TB1001W01Entity createSearchSeikyu1Entity(
        B1001W01SearchSeikyu1ConditionDto dto) {

        // 検索条件をCustomEntityに展開する
        S07T001TB1001W01Entity e = new S07T001TB1001W01Entity();

        // AP基本コード
        e.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        e.setShopcd(dto.getShopcd());
        // 締年月
        e.setShimedydm(dto.getShimedydm());

        // 請求先コードが未設定の場合
        if (StringUtils.isEmpty(dto.getSkysakicd())) {
            // 得意先コードを設定
            e.setTokuisakicd(dto.getTokuisakicd());
            return e;
        }

        // 親子関係のチェック(得意先マスタの件数取得)
        long cnt = s02m013tDao.countByExample(createTkiSakiCond(dto));

        // 得意先/請求先コード
        // 得意先マスタ取得件数が2件以上の場合請求先コード、それ以外の場合得意先コード
        if (cnt >= 2l) {
            e.setSkysakicd(dto.getSkysakicd());
        } else {
            e.setTokuisakicd(dto.getTokuisakicd());
        }

        return e;
    }

    /**
     * 得意先マスタ検索条件を生成する。
     * 
     * @param dto 売掛元帳（詳細）請求先情報1検索条件DTO
     * @return 得意先マスタExampleEntity
     */
    private S02M013TEntityExample createTkiSakiCond(B1001W01SearchSeikyu1ConditionDto dto) {

        S02M013TEntityExample e = new S02M013TEntityExample();

        // クライテリア(SQL条件)の生成
        e.createCriteria().andApkihoncdEqualTo(dto.getApkihoncd()).andShopcdEqualTo(dto.getShopcd())
            .andSkysakicdEqualTo(dto.getSkysakicd()).andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

        return e;
    }

    /**
     * 請求先1検索結果をControllerへ返却する形式へ変換する。
     * 
     * @param searchResultList 初期表示1（売掛元帳（詳細））entityリスト
     * @param taxFlg 消費税フラグ
     * @return 初期表示1（売掛元帳（詳細））検索結果DTOリスト
     */
    private List<B1001W01SearchSeikyu1ResultDto> createSeikyu1ResultList(
        List<S07T001TB1001W01Entity> searchResultList, String taxFlg) {

        List<B1001W01SearchSeikyu1ResultDto> result = new ArrayList<>();

        for (S07T001TB1001W01Entity e : searchResultList) {
            B1001W01SearchSeikyu1ResultDto d = new B1001W01SearchSeikyu1ResultDto();
            d.setShimedydm(e.getShimedydm()); // 締年月
            d.setShimedd(e.getShimedd()); // 締日
            d.setSkysakicd(e.getSkysakicd()); // 請求先コード
            d.setSkysakinmn1(e.getSkysakinmn1()); // 請求先名称１
            d.setZnkkurikoshikin(ObjectUtils.isEmpty(e.getZnkkurikoshikin()) ? null
                : String.valueOf(e.getZnkkurikoshikin())); // 前回繰越金額
            d.setKonkainyknkin(ObjectUtils.isEmpty(e.getKonkainyknkin()) ? null
                : String.valueOf(e.getKonkainyknkin())); // 今回入金金額
            d.setKonkaitaxnukiurikin(ObjectUtils.isEmpty(e.getKonkaitaxnukiurikin()) ? null
                : String.valueOf(e.getKonkaitaxnukiurikin())); // 今回税抜売上金額
            d.setKonkaishohitaxkin(ObjectUtils.isEmpty(e.getKonkaishohitaxkin()) ? null
                : String.valueOf(e.getKonkaishohitaxkin())); // 今回消費税金額
            d.setAddsum(ObjectUtils.isEmpty(e.getAddsum()) ? null : String.valueOf(e.getAddsum())); // 今回税抜売上金額+今回消費税金額
            d.setKurikoshikin(ObjectUtils.isEmpty(e.getKurikoshikin()) ? null
                : String.valueOf(e.getKurikoshikin())); // 繰越金額
            d.setSkyprcsjissikbn(ObjectUtils.isEmpty(e.getSkyprcsjissikbn()) ? null
                : String.valueOf(e.getSkyprcsjissikbn())); // 請求処理実施区分
            d.setTaxflg(taxFlg); // 消費税フラグ

            result.add(d);
        }
        return result;
    }

    /**
     * 初期表示2（売掛元帳（詳細））検索条件を生成する。
     * 
     * @param dto 売掛元帳（詳細）請求先情報2検索条件DTO
     * @return 初期表示2（売掛元帳（詳細））取得用entity
     */
    private S09T001TB1001W01Entity createSearchSeikyu2Entity(
        B1001W01SearchSeikyu2ConditionDto dto) {

        // 検索条件をCustomEntityに展開する
        S09T001TB1001W01Entity e = new S09T001TB1001W01Entity();

        // AP基本コード
        e.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        e.setShopcd(dto.getShopcd());
        // 得意先コード
        e.setTokuisakicd(dto.getTokuisakicd());
        // 締年月
        e.setShimedydm(dto.getShimedydm());

        return e;
    }

    /**
     * 請求先2検索結果をControllerへ返却する形式へ変換する。
     * 
     * @param searchResultList 初期表示2（売掛元帳（詳細））entityリスト
     * @return 初期表示2（売掛元帳（詳細））検索結果DTOリスト
     */
    private List<B1001W01SearchSeikyu2ResultDto> createSeikyu2ResultList(
        List<S09T001TB1001W01Entity> searchResultList) {

        List<B1001W01SearchSeikyu2ResultDto> result = new ArrayList<>();

        for (S09T001TB1001W01Entity e : searchResultList) {
            B1001W01SearchSeikyu2ResultDto d = new B1001W01SearchSeikyu2ResultDto();
            d.setShimedydm(e.getShimedydm()); // 締年月
            d.setShimedd(e.getShimedd()); // 締日
            d.setJotai(e.getJotai()); // 状態
            d.setJotainm(e.getJotainm()); // 状態名称
            d.setFurikaedydmdd(e.getFurikaedydmdd()); // 振替年月日
            d.setKsikin(ObjectUtils.isEmpty(e.getKsikin()) ? null : String.valueOf(e.getKsikin())); // 決済金額
            d.setKsikekka(e.getKsikekka()); // 決済結果
            d.setKsikekkaval(e.getMojisetteiatai()); // 決済結果コード値
            d.setKsikknnm(e.getKsikknnm()); // 決済機関名称

            result.add(d);
        }
        return result;
    }

    /**
     * 商品コード存在チェックを実行する
     * 
     * @param dto 売掛元帳（詳細）検索条件DTO
     */
    private void checkShnCdExist(B1001W01SearchConditionDto dto) {

        // 商品マスタビューの件数取得
        long cnt = s02v001tDao.countByExample(createShnViewCond(dto));

        // 商品コードが存在しない場合、エラー
        if (cnt == 0l) {
            FormField field = new FormField("baseBean.shncd", "商品コード");
            throw BusinessExceptionFactory.validationFailed(field, MessageID.W084, "商品コード");
        }
    }

    /**
     * 商品コード存在チェック検索条件を生成する。
     * 
     * @param dto 売掛元帳（詳細）検索条件DTO
     * @return 商品マスタビューExampleEntity
     */
    private S02V001TEntityExample createShnViewCond(B1001W01SearchConditionDto dto) {

        S02V001TEntityExample e = new S02V001TEntityExample();

        // クライテリア(SQL条件)の生成
        e.createCriteria().andApkihoncdEqualTo(dto.getApkihoncd()).andShopcdEqualTo(dto.getShopcd())
            .andShncdEqualTo(dto.getShncd());

        return e;
    }

    /**
     * 検索（売掛元帳（詳細））検索条件を生成する。
     * 
     * @param dto 売掛元帳（詳細）検索条件DTO
     * @return 売掛元帳（詳細）取得用entity
     */
    public S06T001TB1001W01Entity createSearchEntity(B1001W01SearchConditionDto dto) {
        // 検索条件をCustomEntityに展開する
        S06T001TB1001W01Entity e = new S06T001TB1001W01Entity();

        // AP基本コード
        e.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        e.setShopcd(dto.getShopcd());
        // 得意先コード
        e.setTokuisakicd(dto.getTokuisakicd());
        // 締年月
        e.setGetsujishimedydm(dto.getGetsujishimedydm());
        // 日付開始
        e.setStnykndd(dto.getStnykndd());
        // 日付終了
        e.setEdnykndd(dto.getEdnykndd());
        // 大分類コード
        e.setLctgrycd(dto.getLctgrycd());
        // 中分類コード
        e.setMctgrycd(dto.getMctgrycd());
        // 小分類コード
        e.setSctgrycd(dto.getSctgrycd());
        // 商品コード
        e.setShncd(dto.getShncd());
        // 計上区分
        e.setKeijokbn(dto.getKeijokbn());
        // 入金種別
        e.setNyknkind(dto.getNyknkind());
        // 売上入金取消選択
        e.setSkyshootptarinashikbn(dto.getSkyshootptarinashikbn());
        // 売上入金区分
        e.setUrinyknkbn(dto.getUrinyknkbn());
        // 明細最大件数
        e.setRownum(dto.getLimit());

        return e;
    }

    /**
     * 検索結果をControllerへ返却する形式へ変換する。
     * 
     * @param searchResultList 売掛元帳（詳細）取得用entityリスト
     * @return 売掛元帳（詳細）検索結果DTO
     */
    private B1001W01SearchResultDto createResultList(
        List<S06T001TB1001W01Entity> searchResultList) {

        // 検索結果が0件の場合
        if (searchResultList.size() == 0) {
            throw BusinessExceptionFactory.dataNotFound(MessageID.W084, "未締分データ");
        }

        List<B1001W01SearchResultListDto> result = new ArrayList<>();

        BigDecimal uriKinTotal = BigDecimal.ZERO; // 計上金額合計
        BigDecimal shohiTaxKinTotal = BigDecimal.ZERO; // 消費税金額合計
        BigDecimal nyknKinTotal = BigDecimal.ZERO; // 入金金額合計
        BigDecimal uriPointTotal = BigDecimal.ZERO; // 売上ポイント合計
        BigDecimal tkbtPointTotal = BigDecimal.ZERO; // 特別ポイント合計
        BigDecimal smPointTotal = BigDecimal.ZERO; // 合計ポイント合計

        for (S06T001TB1001W01Entity e : searchResultList) {
            B1001W01SearchResultListDto d = new B1001W01SearchResultListDto();
            d.setYmd(e.getYmd()); // 売上日
            d.setShncd(e.getShncd()); // 商品コード
            d.setGenzshnnmryakusho(e.getGenzshnnmryakusho()); // 現商品名称略称
            d.setShinshnnmryakusho(e.getShinshnnmryakusho()); // 新商品名称略称
            d.setIro(e.getIro()); // 色
            d.setSznm(e.getSznm()); // サイズ名称
            d.setKatasiki(e.getKatasiki()); // 型式
            d.setKeijokbn(e.getMojisetteiatai()); // 計上区分
            d.setSuryo(ObjectUtils.isEmpty(e.getSuryo()) ? null : String.valueOf(e.getSuryo())); // 数量
            d.setUritrtskitaninm(e.getUritrtskitaninm()); // 売上取扱単位名称
            d.setUriirisu(
                ObjectUtils.isEmpty(e.getUriirisu()) ? null : String.valueOf(e.getUriirisu())); // 売上入数
            d.setSokazu(ObjectUtils.isEmpty(e.getSokazu()) ? null : String.valueOf(e.getSokazu())); // 総数
            d.setUnt(ObjectUtils.isEmpty(e.getUnt()) ? null : String.valueOf(e.getUnt())); // 単価
            d.setKin(ObjectUtils.isEmpty(e.getKin()) ? null : String.valueOf(e.getKin())); // 金額
            d.setShohitaxkin(ObjectUtils.isEmpty(e.getShohitaxkin()) ? null
                : String.valueOf(e.getShohitaxkin())); // 消費税金額
            d.setSankoushohitaxkin(ObjectUtils.isEmpty(e.getSankoushohitaxkin()) ? null
                : String.valueOf(e.getSankoushohitaxkin())); // 参考消費税金額
            d.setSankoutaxkomiurikin(ObjectUtils.isEmpty(e.getSankoutaxkomiurikin()) ? null
                : String.valueOf(e.getSankoutaxkomiurikin())); // 参考税込売上金額
            d.setNyknkin(
                ObjectUtils.isEmpty(e.getNyknkin()) ? null : String.valueOf(e.getNyknkin())); // 入金金額
            d.setNebikikin(
                ObjectUtils.isEmpty(e.getNebikikin()) ? null : String.valueOf(e.getNebikikin())); // 値引き金額
            d.setTesuryo(
                ObjectUtils.isEmpty(e.getTesuryo()) ? null : String.valueOf(e.getTesuryo())); // 手数料
            d.setBiko(e.getBiko()); // 明細備考
            d.setDnpyno(e.getPyno()); // 売上伝票番号
            d.setEdano(ObjectUtils.isEmpty(e.getEdano()) ? null : String.valueOf(e.getEdano())); // 枝番号(取得しているが未使用)
            d.setMeisaino(
                ObjectUtils.isEmpty(e.getMeisaino()) ? null : String.valueOf(e.getMeisaino())); // 売上伝票明細番号(取得しているが未使用)
            d.setLctgrycd(e.getLctgrycd()); // 大分類コード
            d.setMctgrycd(e.getMctgrycd()); // 中分類コード
            d.setSctgrycd(e.getSctgrycd()); // 小分類コード
            d.setLctgrynm(e.getLctgrynm()); // 大分類名称
            d.setSkyshimedydm(e.getSkyshimedydm()); // 請求締年月
            d.setSkyshimedd(e.getSkyshimedd()); // 請求締日
            d.setGetsujishimedydm(e.getGetsujishimedydm()); // 月次締年月
            d.setGetsujishimedd(e.getGetsujishimedd()); // 月次締日
            d.setShnnmkirikaedd(e.getShnnmkirikaedd()); // 商品名称切替日
            d.setInptsystmymd(e.getInptsystmymd()); // 登録システム日付
            d.setInptsystmjikan(e.getInptsystmjikan()); // 登録システム時間
            d.setToshaapkihoncd(e.getToshaapkihoncd()); // 登録者ＡＰ基本コード
            d.setToshashopcd(e.getToshashopcd()); // 登録者ショップコード
            d.setToshacd(e.getToshacd()); // 登録者コード
            d.setTnshamei(e.getTnshamei()); // 担当者名
            d.setNyukinkind(e.getMojisetteiatai2()); // 入金種別
            d.setSkyshootptarinashikbn(e.getSkyshootptarinashikbn()); // 請求書出力有無区分
            d.setSkysakicd(e.getSkysakicd()); // 請求先コード
            d.setNyknkinCalc(getNyknkin(e)); // 入金金額(画面表示、合計金額計算用)
            d.setUripoint(ObjectUtils.isEmpty(e.getUripoint()) ? null : String.valueOf(e.getUripoint())); // 売上ポイント
            d.setTkbtpoint(ObjectUtils.isEmpty(e.getTkbtpoint()) ? null : String.valueOf(e.getTkbtpoint())); // 特別ポイント
            d.setPointutwk(e.getPointutwk()); // ポイント内訳
            d.setSmpoint(ObjectUtils.isEmpty(e.getSmpoint()) ? null : String.valueOf(e.getSmpoint())); // 合計ポイント
            d.setPointrenkeidd(e.getPointrenkeidd()); // ポイント連携日付

            // 計上金額合計算出
            uriKinTotal = uriKinTotal.add(
                ObjectUtils.isEmpty(e.getKin()) ? BigDecimal.ZERO : BigDecimal.valueOf(e.getKin()));
            // 消費税金額合計算出
            shohiTaxKinTotal =
                shohiTaxKinTotal.add(ObjectUtils.isEmpty(e.getShohitaxkin()) ? BigDecimal.ZERO
                    : BigDecimal.valueOf(e.getShohitaxkin()));
            // 入金金額合計算出
            nyknKinTotal =
                nyknKinTotal.add(StringUtils.isEmpty(d.getNyknkinCalc()) ? BigDecimal.ZERO
                    : new BigDecimal(d.getNyknkinCalc()));
            // 売上ポイント合計算出
            uriPointTotal = uriPointTotal.add(
                    ObjectUtils.isEmpty(e.getUripoint()) ? BigDecimal.ZERO : BigDecimal.valueOf(e.getUripoint()));
            // 特別ポイント合計算出
            tkbtPointTotal = tkbtPointTotal.add(
                    ObjectUtils.isEmpty(e.getTkbtpoint()) ? BigDecimal.ZERO : BigDecimal.valueOf(e.getTkbtpoint()));
            // 合計ポイント合計算出
            smPointTotal = smPointTotal.add(
                    ObjectUtils.isEmpty(e.getSmpoint()) ? BigDecimal.ZERO : BigDecimal.valueOf(e.getSmpoint()));

            result.add(d);
        }

        B1001W01SearchResultDto res = new B1001W01SearchResultDto();

        // 売掛元帳（詳細）検索結果DTOリストの先頭行に各合計値を設定する
        res.setUrikinkei(String.valueOf(uriKinTotal)); // 計上金額合計
        res.setShohitaxkinkei(String.valueOf(shohiTaxKinTotal)); // 消費税金額合計
        res.setNyknkinkei(String.valueOf(nyknKinTotal)); // 入金金額合計
        res.setUripoiintsm(String.valueOf(uriPointTotal)); // 売上ポイント合計
        res.setTkbtpointsm(String.valueOf(tkbtPointTotal)); // 特別ポイント合計
        res.setSmpointsm(String.valueOf(smPointTotal)); // 合計ポイント合計

        res.setResultList(result);

        return res;
    }

    /**
     * 入金金額を取得する。
     * 
     * @param e 売掛元帳（詳細）取得用entity
     * @return 入金金額
     */
    private String getNyknkin(S06T001TB1001W01Entity e) {

        if (ObjectUtils.isEmpty(e.getNyknkin())) {
            // 入金金額が空の場合
            if (ObjectUtils.isEmpty(e.getNebikikin())) {
                // 値引金額が空の場合
                if (ObjectUtils.isEmpty(e.getTesuryo())) {
                    // 手数料が空の場合
                    // nullを返却
                    return null;
                } else {
                    // 手数料が空以外の場合
                    // 手数料を返却
                    return String.valueOf(e.getTesuryo());
                }
            } else {
                // 値引金額が空以外の場合
                if (ObjectUtils.isEmpty(e.getTesuryo())) {
                    // 手数料が空の場合
                    // 値引金額を返却
                    return String.valueOf(e.getNebikikin());
                } else {
                    // 手数料が空以外の場合
                    // 値引金額+手数料を返却
                    return String.valueOf(e.getNebikikin() + e.getTesuryo());
                }
            }
        } else {
            // 入金金額が空以外の場合
            if (ObjectUtils.isEmpty(e.getNebikikin())) {
                // 値引金額が空の場合
                if (ObjectUtils.isEmpty(e.getTesuryo())) {
                    // 手数料が空の場合
                    // 入金金額を返却
                    return String.valueOf(e.getNyknkin());
                } else {
                    // 手数料が空以外の場合
                    // 入金金額+手数料を返却
                    return String.valueOf(e.getNyknkin() + e.getTesuryo());
                }
            } else {
                // 値引金額が空以外の場合
                if (ObjectUtils.isEmpty(e.getTesuryo())) {
                    // 手数料が空の場合
                    // 入金金額+値引金額を返却
                    return String.valueOf(e.getNyknkin() + e.getNebikikin());
                } else {
                    // 手数料が空以外の場合
                    // 入金金額+値引金額+手数料を返却
                    return String.valueOf(e.getNyknkin() + e.getNebikikin() + e.getTesuryo());
                }
            }
        }
    }
}
