package jp.co.aquaclara.accis.web.domain.service.b0505;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M006TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M009TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M013TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M016TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M043TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M046TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M048TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02V001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S06T003TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.Z1804B01Dao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M006TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M006TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M009TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M009TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M016TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M016TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M016TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M043TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M043TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M048TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M048TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S06T003TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S06T003TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.Z1804B01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M016TB0505W02Entity;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.domain.dto.b0505.B0505W02CalcTaxDto;
import jp.co.aquaclara.accis.web.domain.dto.b0505.B0505W02DisplayDto;
import jp.co.aquaclara.accis.web.domain.dto.b0505.B0505W02KeyDto;
import jp.co.aquaclara.accis.web.domain.dto.b0505.B0505W02ShnDisplayDto;
import jp.co.aquaclara.accis.web.domain.dto.b0505.B0505W02ShnDisplayForSubWindowDto;
import jp.co.aquaclara.accis.web.domain.dto.b0505.B0505W02UpdateDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M003TDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M013TDto;
import jp.co.aquaclara.accis.web.domain.service.business.TaxService;
import jp.co.aquaclara.accis.web.domain.service.master.S02M003TService;
import jp.co.aquaclara.accis.web.domain.service.master.S02M006TService;
import jp.co.aquaclara.accis.web.domain.service.master.S02M013TService;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import jp.co.aquaclara.accis.web.exception.dto.FormField;

/**
 * 定例売上マスタ(登録画面)Service
 * 
 * @author accisdev010
 *
 */
@Service
public class B0505W02Service {

    @Autowired
    private S02M006TDao s02m006tDao;

    @Autowired
    private S02M013TDao s02m013tDao;

    @Autowired
    private S02M016TDao s02m016tDao;

    @Autowired
    private S02M046TDao s02m046tDao;

    @Autowired
    private S02M048TDao s02m048tDao;

    @Autowired
    private S02V001TDao s02v001tDao;

    @Autowired
    private S06T003TDao s06t003tDao;

    @Autowired
    private Z1804B01Dao z1804b01Dao;

    @Autowired
    private S02M009TDao s02M009TDao;

    @Autowired
    private S02M043TDao s02M043TDao;

    @Autowired
    private S02M003TService s02m003tService;

    @Autowired
    private S02M006TService s02m006tService;

    @Autowired
    private S02M013TService s02m013tService;

    @Autowired
    private TaxService taxService;

    // 単価の項目にて利用
    private final String TANKA_0 = "0";

    // 数量及び消費税の項目にて利用
    private final String SUCHI_1 = "1";

    private final String DATE_FORMAT = "yyyyMMdd";

    /**
     * 定例売上マスタ更新初期値取得処理
     * 
     * @param dto 検索キー
     * @param unyodate 運用日付
     * @return B0505W02SearchResultDto
     */
    public B0505W02DisplayDto init(B0505W02KeyDto dto, String unyodate) {

        // keyエンティティの生成
        S02M016TEntityKey key = new S02M016TEntityKey();
        key.setApkihoncd(dto.getApkihoncd());
        key.setShopcd(dto.getShopcd());
        key.setTeireihasseino(dto.getTeireihasseino());

        // SELECT実行
        S02M016TB0505W02Entity entitiy = s02m016tDao.selectByB0505W02(key);

        // 結果セットをdtoに詰めて返却
        return createSelectResultDto(entitiy, unyodate);
    }

    /**
     * 定例売上マスタ登録処理
     * 
     * @param dto 登録値格納Dto
     * @return 新規採番した定例発生番号
     */
    @Transactional
    public String insert(B0505W02UpdateDto dto) {

        String teireihasseino = "";

        // 初回金額、初回消費税金額、金額、消費税金額の再計算及びdtoへの設定を実施
        String kaishiDate = dto.getShokaikeijodydm() + "/" + dto.getKeijodd();
        String jikaiDate = dto.getJikaikeijodydm() + "/" + dto.getKeijodd();
        String taxRateShokai = getTaxRate(dto.getApkihoncd(), dto.getShopcd(), kaishiDate);
        String taxRate = getTaxRate(dto.getApkihoncd(), dto.getShopcd(), jikaiDate);

        // 初回単価の値が"0"かどうかによって設定値を変更
        if (dto.getShokaiunt().equals(TANKA_0)) {
            // 初回単価の値が"0"の場合は初回金額は変更せず、初回消費税金額の再計算及び再設定のみ実施
            dto.setShokaishohitaxkin(culcNum(SUCHI_1, dto.getShokaikin(), taxRateShokai));
        } else {
            dto.setShokaikin(culcNum(dto.getSuryo(), dto.getShokaiunt(), SUCHI_1));
            dto.setShokaishohitaxkin(culcNum(dto.getSuryo(), dto.getShokaiunt(), taxRateShokai));
        }

        // 単価の値が"0"かどうかによって設定値を変更
        if (dto.getHyojunprice().equals(TANKA_0)) {
            // 単価の値が"0"の場合は金額は変更せず、消費税金額の再計算及び再設定のみ実施
            dto.setShohitaxkin(culcNum(SUCHI_1, dto.getKin(), taxRate));
        } else {
            dto.setKin(culcNum(dto.getSuryo(), dto.getHyojunprice(), SUCHI_1));
            dto.setShohitaxkin(culcNum(dto.getSuryo(), dto.getHyojunprice(), taxRate));
        }

        // 相関チェック実施
        checkParam(dto);

        try {
            // 定例発生番号の採番
            Z1804B01Entity entity = new Z1804B01Entity();
            entity.setType(Integer.parseInt(CommonConstants.NEWNOTEIREI, 10)); // 採番種別（定例発生番号採番)
            entity.setCdflg(0);
            entity.setCount(1);
            entity.setApkihoncd(dto.getApkihoncd()); // ＡＰ基本コード
            entity.setShopcd(dto.getShopcd()); // ショップコード

            z1804b01Dao.getNewNo(entity);
            dto.setTeireihasseino(entity.getResultno());
            teireihasseino = entity.getResultno();

            // システム日時を設定
            dto.setSystemDate(DateTimeUtil.changeFormatToSystemDate(LocalDateTime.now()));
            dto.setSystemTime(DateTimeUtil.changeFormatToSystemTime(LocalDateTime.now()));

            // INSERT実行
            s02m016tDao.insertSelective(createInsertEntity(dto));

        } catch (DuplicateKeyException e) {
            // 一意制約違反の場合、エラー
            throw BusinessExceptionFactory.validationFailed(MessageID.W089);
        } catch (RuntimeException e) {
            throw BusinessExceptionFactory.validationFailed(MessageID.E002);
        }

        return teireihasseino;
    }

    /**
     * 定例売上マスタ更新処理
     * 
     * @param dto 更新値格納Dto
     */
    @Transactional
    public void update(B0505W02UpdateDto dto) {

        // 初回金額、初回消費税金額、金額、消費税金額の再計算及びdtoへの設定を実施
        String kaishiDate = dto.getShokaikeijodydm() + "/" + dto.getKeijodd();
        String jikaiDate = dto.getJikaikeijodydm() + "/" + dto.getKeijodd();
        String taxRateShokai = getTaxRate(dto.getApkihoncd(), dto.getShopcd(), kaishiDate);
        String taxRate = getTaxRate(dto.getApkihoncd(), dto.getShopcd(), jikaiDate);

        // 初回単価の値が"0"かどうかによって設定値を変更
        if (dto.getShokaiunt().equals(TANKA_0)) {
            // 初回単価の値が"0"の場合は初回金額は変更せず、初回消費税金額の再計算及び再設定のみ実施
            dto.setShokaishohitaxkin(culcNum(SUCHI_1, dto.getShokaikin(), taxRateShokai));
        } else {
            dto.setShokaikin(culcNum(dto.getSuryo(), dto.getShokaiunt(), SUCHI_1));
            dto.setShokaishohitaxkin(culcNum(dto.getSuryo(), dto.getShokaiunt(), taxRateShokai));
        }

        // 単価の値が"0"かどうかによって設定値を変更
        if (dto.getHyojunprice().equals(TANKA_0)) {
            // 単価の値が"0"の場合は金額は変更せず、消費税金額の再計算及び再設定のみ実施
            dto.setShohitaxkin(culcNum(SUCHI_1, dto.getKin(), taxRate));
        } else {
            dto.setKin(culcNum(dto.getSuryo(), dto.getHyojunprice(), SUCHI_1));
            dto.setShohitaxkin(culcNum(dto.getSuryo(), dto.getHyojunprice(), taxRate));
        }

        // 相関チェック実施
        checkParam(dto);

        int result = 0;

        try {
            // システム日時を設定
            dto.setSystemDate(DateTimeUtil.changeFormatToSystemDate(LocalDateTime.now()));
            dto.setSystemTime(DateTimeUtil.changeFormatToSystemTime(LocalDateTime.now()));

            // UPDATE実行
            result = s02m016tDao.updateByExampleSelective(createUpdateSetEntity(dto),
                createUpdateConditionEntity(dto));

        } catch (RuntimeException e) {
            throw BusinessExceptionFactory.validationFailed(MessageID.E002);
        }

        if (result == 0) {
            // 更新件数が0(排他制御)の場合、エラー
            throw BusinessExceptionFactory.validationFailed(MessageID.W090);
        }
    }

    /**
     * 検索結果をControllerへ返却する形式へ変換する。
     * 
     * @param e S02M016TB0505W02Entity
     * @param unyodate 運用日付
     * @return resultDto
     */
    private B0505W02DisplayDto createSelectResultDto(S02M016TB0505W02Entity e, String unyodate) {

        B0505W02DisplayDto dto = new B0505W02DisplayDto(e.getApkihoncd(), // AP基本コード
            e.getShopcd(), // ショップコード
            e.getUrisiirekbn(), // 売上仕入区分
            e.getTeireihasseino(), // 定例発生番号
            ItemUtil.nullToString(e.getDnpykbn()), // 伝票区分
            ItemUtil.nullToString(e.getTorihikikbn()), // 取引区分
            ItemUtil.nullToString(e.getTokuisakicd()), // 得意先コード
            ItemUtil.nullToString(e.getTokuisakinm()), // 得意先名称
            ItemUtil.nullToString(e.getJissekitnshacd()), // 実績担当者コード
            ItemUtil.nullToString(e.getJissekitnshanm()), // 実績担当者名
            ItemUtil.nullToString(e.getKeijodd()), // 計上日
            ItemUtil.nullToString(DateTimeUtil.toSlashedDateYYYYMM(e.getShokaikeijodydm())), // 開始年月
            ItemUtil.nullToString(DateTimeUtil.toSlashedDateYYYYMM(e.getShrydydm())), // 終了年月
            ItemUtil.nullToString(DateTimeUtil.toSlashedDate(e.getZnkhasseikeijodd())), // 前回発生計上日
            ItemUtil.nullToString(DateTimeUtil.toSlashedDateYYYYMM(e.getJikaikeijodydm())), // 次回計上年月
            ItemUtil.nullToString(e.getHasseikankakukeijoptn()), // 発生間隔計上パターン
            ItemUtil.nullToString(e.getTeireikbn()), // 定例区分
            ItemUtil.nullToString(e.getDnpyhasseiarinashiflg()), // 売上発生
            ItemUtil.nullToString(e.getRtlrknshukeikbn()), // 売上設定
            ItemUtil.nullToString(e.getShncd()), // 商品コード
            ItemUtil.nullToString(ItemUtil.getItem(e.getGenzshnnm(), e.getShinshnnm(),
                e.getShnnmkirikaedd(), unyodate)), // 商品名称
            ItemUtil.nullToString(e.getIro()), // 色
            ItemUtil.nullToString(e.getSznm()), // サイズ名称
            ItemUtil.nullToString(e.getKatasiki()), // 型式
            ItemUtil.nullToString(e.getKeijokbn()), // 売上計上区分
            ItemUtil.nullToString(bigChangeStr(e.getSuryo())), // 数量
            ItemUtil.nullToString(e.getUritrtskitaninm()), // 取扱単位
            ItemUtil.nullToString(longChangeStr(e.getUriirisu())), // 入数
            ItemUtil.nullToString(e.getUritaninm()), // 入数単位
            ItemUtil.nullToString(calcSokazu(e.getSuryo(), e.getUriirisu())), // 総数
            ItemUtil.nullToString(ItemUtil.getItem(longChangeStr(e.getGenzhyojunprice()),
                longChangeStr(e.getShinhyojunprice()), e.getHyojunpricekirikaedd(), unyodate)), // 標準価格
            ItemUtil.nullToString(bigChangeStr(e.getHyojunprice())), // 単価
            ItemUtil.nullToString(longChangeStr(e.getKin())), // 金額
            ItemUtil.nullToString(longChangeStr(e.getShohitaxkin())), // 消費税金額
            ItemUtil.nullToString(e.getMeisaibiko()), // 明細備考
            ItemUtil.nullToString(bigChangeStr(e.getShokaiunt())), // 初回単価
            ItemUtil.nullToString(longChangeStr(e.getShokaikin())), // 初回金額
            ItemUtil.nullToString(longChangeStr(e.getShokaishohitaxkin())), // 初回消費税金額
            ItemUtil.nullToString(e.getYukokbn()), // 有効区分
            ItemUtil.nullToString(DateTimeUtil.toSlashedDate(e.getInptsystmymd())), // 登録システム日付
            ItemUtil.nullToString(DateTimeUtil.toColonedDateHHMMSS(e.getInptsystmjikan())), // 登録システム時間
            ItemUtil.nullToString(e.getToshaapkihoncd()), // 登録者ＡＰ基本コード
            ItemUtil.nullToString(e.getToshashopcd()), // 登録者ショップコード
            ItemUtil.nullToString(e.getToshacd()), // 登録者コード
            ItemUtil.nullToString(e.getToshanm()), // 登録者名
            ItemUtil.nullToString(DateTimeUtil.toSlashedDate(e.getKshnsystmymd())), // 更新システム日付
            ItemUtil.nullToString(DateTimeUtil.toColonedDateHHMMSS(e.getKshnsystmjikan())), // 更新システム時間
            ItemUtil.nullToString(e.getKoshaapkihoncd()), // 更新者ＡＰ基本コード
            ItemUtil.nullToString(e.getKoshashopcd()), // 更新者ショップコード
            ItemUtil.nullToString(e.getKoshacd()), // 更新者コード
            ItemUtil.nullToString(e.getKoshanm())); // 更新者名称

        return dto;
    }

    /**
     * 商品検索結果をAPIControllerへ返却する形式へ変換する。
     * 
     * @param e S02V001TEntity
     * @return resultDto
     */
    private B0505W02ShnDisplayDto createShnSelectResultDto(S02V001TEntity e) {

        B0505W02ShnDisplayDto dto = new B0505W02ShnDisplayDto(ItemUtil.nullToString(e.getStkbn()), // セット区分
            ItemUtil.nullToString(e.getGenzshnnm()), // 現商品名称
            ItemUtil.nullToString(e.getShinshnnm()), // 新商品名称
            ItemUtil.nullToString(e.getShnnmkirikaedd()), // 商品名称切替日
            ItemUtil.nullToString(e.getIro()), // 色
            ItemUtil.nullToString(e.getSznm()), // サイズ名称
            ItemUtil.nullToString(e.getUritrtskitaninm()), // 売上取扱単位名称
            !ObjectUtils.isEmpty(e.getUriirisu()) ? e.getUriirisu().toString() : "", // 売上入数
            ItemUtil.nullToString(e.getUritaninm()), // 売上単位名称
            ItemUtil.nullToString(bigChangeStr(e.getGenzhyojunprice())), // 現標準価格
            ItemUtil.nullToString(bigChangeStr(e.getShinhyojunprice())), // 新標準価格
            ItemUtil.nullToString(e.getHyojunpricekirikaedd()), // 標準価格切替日
            ItemUtil.nullToString(e.getLctgrycd()), // 大分類コード
            ItemUtil.nullToString(e.getMctgrycd()), // 中分類コード
            ItemUtil.nullToString(e.getTrtskikbn()), // 取扱区分
            ItemUtil.nullToString(e.getShopyukokbn()), // 有効区分(ショップ有効区分)
            e.getShnapkihoncd()); // 商品AP基本コード
        return dto;
    }

    /**
     * 商品検索結果をAPIControllerへ返却する形式へ変換する。(子画面にて商品選択)
     * 
     * @param e S02V001TEntity
     * @param tokuisakicd 得意先コード
     * @param unyoDate 運用日付
     * @return B0505W02ShnDisplayForSubWindowDto
     */
    private B0505W02ShnDisplayForSubWindowDto createShnSelectResultDtoForSubWindow(S02V001TEntity e,
        String tokuisakicd, String unyoDate) {

        // 得意先別商品別単価マスタから配達価格を取得
        S02M009TEntityExample s02M009TExample = new S02M009TEntityExample();
        s02M009TExample.createCriteria().andApkihoncdEqualTo(e.getApkihoncd())
            .andShopcdEqualTo(e.getShopcd()).andShncdEqualTo(e.getShncd())
            .andTokuisakicdEqualTo(tokuisakicd).andTekiyostrtddLessThanOrEqualTo(unyoDate)
            .andTekiyoshryddGreaterThanOrEqualTo(unyoDate);
        List<S02M009TEntity> s02M009TList = s02M009TDao.selectByExample(s02M009TExample);
        BigDecimal dlvryprice = null;
        if (s02M009TList.size() > 0) {
            dlvryprice = s02M009TList.get(0).getDlvryprice();
        }

        B0505W02ShnDisplayForSubWindowDto dto =
            new B0505W02ShnDisplayForSubWindowDto(ItemUtil.nullToString(e.getStkbn()), // セット区分
                ItemUtil.nullToString(e.getGenzshnnm()), // 現商品名称
                ItemUtil.nullToString(e.getShinshnnm()), // 新商品名称
                ItemUtil.nullToString(e.getShnnmkirikaedd()), // 商品名称切替日
                ItemUtil.nullToString(e.getIro()), // 色
                ItemUtil.nullToString(e.getSznm()), // サイズ名称
                ItemUtil.nullToString(e.getUritrtskitaninm()), // 売上取扱単位名称
                !ObjectUtils.isEmpty(e.getUriirisu()) ? e.getUriirisu().toString() : "", // 売上入数
                ItemUtil.nullToString(e.getUritaninm()), // 売上単位名称
                ItemUtil.nullToString(bigChangeStr(e.getShinhyojunprice())), // 新標準価格
                CommonConstants.KBN_PRICE_HYOJUN.equals(e.getShinpricekbn())
                    ? ItemUtil.nullToString(bigChangeStr(e.getShinhyojunprice()))
                    : ItemUtil.nullToString(bigChangeStr(e.getShinopnprice())), // 単価（価格区分が標準の場合は新標準価格、オープン価格の場合は新オープン価格）
                ItemUtil.nullToString(bigChangeStr(dlvryprice)), // 配達価格
                ItemUtil.nullToString(e.getHyojunpricekirikaedd()), // 標準価格切替日
                ItemUtil.nullToString(e.getLctgrycd()), // 大分類コード
                ItemUtil.nullToString(e.getMctgrycd()), // 中分類コード
                ItemUtil.nullToString(e.getTrtskikbn()), // 取扱区分
                ItemUtil.nullToString(e.getShopyukokbn())); // 有効区分(ショップ有効区分)
        return dto;
    }

    /**
     * BigDecimal型の値を文字列に変換する.
     * 
     * @param val 確認対象
     * @return String 変換した文字列
     */
    private String bigChangeStr(BigDecimal val) {
        // 確認対象がnullではない場合、String型に変換して返却する
        if (!StringUtils.isEmpty(val)) {
            return String.valueOf(val);
        }
        return null;
    }

    /**
     * Long型の値を文字列に変換する
     * 
     * @param val
     * @return String変換値
     */
    private String longChangeStr(Long val) {
        // valueがnull以外の場合Stringに変換して返却する
        if (!StringUtils.isEmpty(val)) {
            return String.valueOf(val);
        }
        return null;
    }

    /**
     * 登録時の値を設定する。
     * 
     * @param dto 登録更新用DTO
     * @return S02M016TEntity
     */
    private S02M016TEntity createInsertEntity(B0505W02UpdateDto dto) {

        S02M016TEntity entity = new S02M016TEntity();

        entity.setApkihoncd(dto.getApkihoncd()); // ＡＰ基本コード
        entity.setShopcd(dto.getShopcd()); // ショップコード
        entity.setUrisiirekbn(dto.getUrisiirekbn()); // 売上仕入区分
        entity.setTeireihasseino(dto.getTeireihasseino()); // 定例発生番号
        entity.setDnpykbn(dto.getDnpykbn()); // 伝票区分
        entity.setHasseikankakukeijoptn(dto.getHasseikankakukeijoptn()); // 発生間隔計上パターン
        entity.setTorihikikbn(dto.getTorihikikbn()); // 取引区分
        entity.setShncd(dto.getShncd()); // 商品コード
        entity.setTokuisakicd(dto.getTokuisakicd1()); // 得意先コード
        entity.setSuryo(new BigDecimal(dto.getSuryo())); // 数量
        entity.setKeijokbn(dto.getKeijokbn()); // 計上区分
        entity.setHyojunprice(new BigDecimal(dto.getHyojunprice())); // 標準価格
        entity.setShokaiunt(new BigDecimal(dto.getShokaiunt())); // 初回単価
        entity.setShokaikin(Long.valueOf(dto.getShokaikin())); // 初回金額
        entity.setKin(Long.valueOf(dto.getKin())); // 金額
        entity.setKeijodd(dto.getKeijodd()); // 計上日
        entity.setShokaikeijodydm(dto.getShokaikeijodydm()); // 初回計上年月
        entity.setZnkhasseikeijodd(dto.getZnkhasseikeijodd()); // 前回発生計上日
        entity.setJikaikeijodydm(dto.getJikaikeijodydm()); // 次回計上年月
        entity.setShrydydm(dto.getShrydydm()); // 終了年月
        entity.setKatasiki(dto.getKatasiki()); // 型式
        entity.setDnpyhasseiarinashiflg(dto.getDnpyhasseiarinashiflg()); // 伝票発生有無フラグ
        entity.setTeireikbn(dto.getTeireikbn()); // 定例区分
        entity.setRtlrknshukeikbn(dto.getRtlrknshukeikbn()); // レンタル料金集計区分
        entity.setNoshootptkbn(dto.getNoshootptkbn()); // 納品書出力区分
        entity.setAcpttorikomisumiflg(dto.getAcpttorikomisumiflg()); // 受注取込済フラグ
        entity.setMeisaibiko(dto.getMeisaibiko()); // 明細備考
        entity.setJissekitnshacd(dto.getJissekitnshacd1()); // 実績担当者コード
        entity.setYukokbn(dto.getYukokbn()); // 有効区分
        entity.setToshaapkihoncd(dto.getLoginUserApkihoncd()); // 登録者ＡＰ基本コード
        entity.setToshashopcd(dto.getLoginUserShopcd()); // 登録者ショップコード
        entity.setToshacd(dto.getLoginUserTnshacd()); // 登録者コード
        entity.setInptsystmymd(dto.getSystemDate()); // 登録システム日付
        entity.setInptsystmjikan(dto.getSystemTime()); // 登録システム時間
        entity.setInptunyoymd(dto.getUnyoDate()); // 登録運用日付
        entity.setInptipadrse(dto.getIpAddress()); // 登録ＩＰアドレス
        entity.setInptpgmid(dto.getProgramId()); // 登録プログラムＩＤ
        entity.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        entity.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        entity.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        entity.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        entity.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        entity.setKshnunyoymd(dto.getUnyoDate()); // 更新運用日付
        entity.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        entity.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        // 初回消費税金額、消費税金額の設定
        calcShohitaxkin(dto, entity);

        return entity;
    }

    /**
     * 更新時の値を設定する。
     * 
     * @param dto 登録更新用DTO
     * @return S02M016TEntity
     */
    private S02M016TEntity createUpdateSetEntity(B0505W02UpdateDto dto) {

        S02M016TEntity entity = new S02M016TEntity();

        entity.setApkihoncd(dto.getApkihoncd()); // ＡＰ基本コード
        entity.setShopcd(dto.getShopcd()); // ショップコード
        entity.setUrisiirekbn(dto.getUrisiirekbn()); // 売上仕入区分
        entity.setTeireihasseino(dto.getTeireihasseino()); // 定例発生番号
        entity.setDnpykbn(dto.getDnpykbn()); // 伝票区分
        entity.setHasseikankakukeijoptn(dto.getHasseikankakukeijoptn()); // 発生間隔計上パターン
        entity.setTorihikikbn(dto.getTorihikikbn()); // 取引区分
        entity.setShncd(dto.getShncd()); // 商品コード
        entity.setTokuisakicd(dto.getTokuisakicd1()); // 得意先コード
        entity.setSuryo(new BigDecimal(dto.getSuryo())); // 数量
        entity.setKeijokbn(dto.getKeijokbn()); // 計上区分
        entity.setHyojunprice(new BigDecimal(dto.getHyojunprice())); // 標準価格
        entity.setShokaiunt(new BigDecimal(dto.getShokaiunt())); // 初回単価
        entity.setShokaikin(Long.valueOf(dto.getShokaikin())); // 初回金額
        entity.setKin(Long.valueOf(dto.getKin())); // 金額
        entity.setKeijodd(dto.getKeijodd()); // 計上日
        entity.setShokaikeijodydm(dto.getShokaikeijodydm()); // 初回計上年月
        entity.setZnkhasseikeijodd(dto.getZnkhasseikeijodd()); // 前回発生計上日
        entity.setJikaikeijodydm(dto.getJikaikeijodydm()); // 次回計上年月
        entity.setShrydydm(dto.getShrydydm()); // 終了年月
        entity.setKatasiki(dto.getKatasiki()); // 型式
        entity.setDnpyhasseiarinashiflg(dto.getDnpyhasseiarinashiflg()); // 伝票発生有無フラグ
        entity.setTeireikbn(dto.getTeireikbn()); // 定例区分
        entity.setRtlrknshukeikbn(dto.getRtlrknshukeikbn()); // レンタル料金集計区分
        entity.setNoshootptkbn(dto.getNoshootptkbn()); // 納品書出力区分
        entity.setAcpttorikomisumiflg(dto.getAcpttorikomisumiflg()); // 受注取込済フラグ
        entity.setMeisaibiko(dto.getMeisaibiko()); // 明細備考
        entity.setJissekitnshacd(dto.getJissekitnshacd1()); // 実績担当者コード
        entity.setYukokbn(dto.getYukokbn()); // 有効区分
        entity.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        entity.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        entity.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        entity.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        entity.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        entity.setKshnunyoymd(dto.getUnyoDate()); // 更新運用日付
        entity.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        entity.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        // 初回消費税金額、消費税金額の設定
        calcShohitaxkin(dto, entity);

        return entity;
    }

    /**
     * 更新時の条件値を設定する。
     * 
     * @param dto 登録更新用DTO
     * @return S02M016TEntityExample
     */
    private S02M016TEntityExample createUpdateConditionEntity(B0505W02UpdateDto dto) {

        S02M016TEntityExample entity = new S02M016TEntityExample();

        // クライテリア(SQL条件)の設定
        entity.createCriteria().andApkihoncdEqualTo(dto.getApkihoncd())
            .andShopcdEqualTo(dto.getShopcd()).andUrisiirekbnEqualTo(dto.getUrisiirekbn())
            .andTeireihasseinoEqualTo(dto.getTeireihasseino())
            .andKshnsystmymdEqualTo(dto.getKshnsystmymd())
            .andKshnsystmjikanEqualTo(dto.getKshnsystmjikan());

        return entity;
    }

    /**
     * 相関チェックを実行する(登録更新共通)
     * 
     * @param dto 登録更新用DTO
     */
    private void checkParam(B0505W02UpdateDto dto) {

        // ハイライト処理で利用する変数の宣言
        String field = null;
        String fieldName = null;

        String apkihoncd = dto.getApkihoncd();
        String shopcd = dto.getShopcd();
        String tokuisakicd = dto.getTokuisakicd1();
        String dnpykbn = dto.getDnpykbn();

        // 伝票区分＝「売上のみ」「経費のみ」の場合
        if (CommonConstants.KBN_URIAGE_NOMI.equals(dnpykbn)
            || CommonConstants.KBN_KEIHI_NOMI.equals(dnpykbn)) {

            // 得意先の取得
            S02M013TDto s02M013TCondDto = new S02M013TDto();
            s02M013TCondDto.setApkihoncd(apkihoncd);
            s02M013TCondDto.setShopcd(shopcd);
            s02M013TCondDto.setTokuisakicd(tokuisakicd);
            S02M013TDto tokuisakicdDto = s02m013tService.searchPrimary(s02M013TCondDto);

            // ハイライト処理する項目の設定
            field = "keijosakiBean.tokuisakicd1";
            fieldName = "得意先コード";

            if (ObjectUtils.isEmpty(tokuisakicdDto)) {
                // 結果が空の場合、エラー
                throw BusinessExceptionFactory.validationFailed(new FormField(field, fieldName),
                    MessageID.W084, "得意先コード");
            } else {
                // 有効区分が"1"(有効)以外の場合、エラー
                if (!CommonConstants.YUKOKBN_1.equals(tokuisakicdDto.getYukokbn())) {
                    throw BusinessExceptionFactory.validationFailed(new FormField(field, fieldName),
                        MessageID.W123, "得意先コード");
                }

                // 伝票区分＝「売上のみ」、かつ得意先マスタ.得意先区分＝9(経費)の場合はエラー
                if (CommonConstants.KBN_URIAGE_NOMI.equals(dnpykbn)
                    && CommonConstants.KBN_TOKUISAKI_KEIHI
                        .equals(tokuisakicdDto.getTokuisakikbn())) {
                    throw BusinessExceptionFactory.validationFailed(new FormField(field, fieldName),
                        MessageID.W117, "伝票区分＝「売上のみ」", "得意先区分が経費以外の得意先コード");
                }

                // 伝票区分＝「経費のみ」、かつ得意先マスタ.得意先区分≠9(経費)の場合はエラー
                if (CommonConstants.KBN_KEIHI_NOMI.equals(dnpykbn)
                    && !CommonConstants.KBN_TOKUISAKI_KEIHI
                        .equals(tokuisakicdDto.getTokuisakikbn())) {
                    throw BusinessExceptionFactory.validationFailed(new FormField(field, fieldName),
                        MessageID.W117, "伝票区分＝「経費のみ」", "得意先区分が経費の得意先コード");
                }

                // ログイン者のアクセス範囲区分が事業所の場合、ログイン者の事業所と得意先コードの事業所が異なる場合、エラー
                if (CommonConstants.AKSHANIKBN_JIGYOSHO.equals(dto.getUserSessionAkshanikbn())
                    && !dto.getUserSessionJigyoshocd().equals(tokuisakicdDto.getJigyoshocd())) {
                    throw BusinessExceptionFactory.validationFailed(MessageID.W126, "ログイン者の事業所",
                        "得意先コードの事業所");
                }
            }

            // 実績担当者コード（計上先）が空ではない場合チェックする
            if (!StringUtils.isEmpty(dto.getJissekitnshacd1())) {

                // 実績担当者コード（計上先）の取得
                S02M003TDto s02M003TCondDto = new S02M003TDto();
                s02M003TCondDto.setApkihoncd(apkihoncd);
                s02M003TCondDto.setShopcd(shopcd);
                s02M003TCondDto.setTnshacd(dto.getJissekitnshacd1());
                S02M003TDto jissekitnshaDto = s02m003tService.searchPrimary(s02M003TCondDto);

                // ハイライト処理する項目の設定
                field = "keijosakiBean.jissekitnshacd1";
                fieldName = "実績担当者コード";

                if (ObjectUtils.isEmpty(jissekitnshaDto)) {
                    // 結果が空の場合、エラー
                    throw BusinessExceptionFactory.validationFailed(new FormField(field, fieldName),
                        MessageID.W084, "実績担当者コード（計上先）");
                } else {
                    if (!CommonConstants.YUKOKBN_1.equals(jissekitnshaDto.getYukokbn())) {
                        // 有効区分が"1"(有効)以外の場合、エラー
                        throw BusinessExceptionFactory.validationFailed(
                            new FormField(field, fieldName), MessageID.W123, "実績担当者コード（計上先）");
                    }
                }
            }

        } // 伝票区分＝「売上のみ」「経費のみ」の場合 END


        // VIEWデータ取得
        B0505W02ShnDisplayDto shnData = getShnData(apkihoncd, shopcd, dto.getShncd());

        if (ObjectUtils.isEmpty(shnData)) {
            // 商品コードが商品マスタビューに存在しない場合はエラー
            throw BusinessExceptionFactory.validationFailed(
                new FormField("meisaiBean.shncd", "商品コード"), MessageID.W084, "商品コード");
        } else {
            // ショップ商品マスタデータ取得
            S02M043TEntityExample example = new S02M043TEntityExample();
            example.createCriteria().andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd)
                .andShnapkihoncdEqualTo(shnData.getShnapkihoncd()).andShncdEqualTo(dto.getShncd());
            List<S02M043TEntity> results = s02M043TDao.selectByExample(example);

            // 有効区分＝1(有効)ではない場合はエラー
            if (!ObjectUtils.isEmpty(results)
                && !CommonConstants.YUKOKBN_1.equals(results.get(0).getYukokbn())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("meisaiBean.shncd", "商品コード"), MessageID.W123, "商品コード");
            }
        }

        // 複数回利用する変数を宣言
        S02M006TEntity s02m006tData1 = null;
        S02M006TEntity s02m006tData2 = null;

        // 定例区分（サーバーレンタル）の場合
        if (CommonConstants.TEIREIKBN_1.equals(dto.getTeireikbn())) {
            s02m006tData1 = getSkmt(CommonConstants.APCD_000, CommonConstants.SYS_SHOPCD_000,
                CommonConstants.SYS_SETTEINO_096, CommonConstants.RECORD_KBN_M,
                CommonConstants.SETTEICD_096_3);

            // 商品大分類がＳＶ用商品大分類以外はエラー
            if (!ObjectUtils.isEmpty(s02m006tData1)
                && !s02m006tData1.getMojisetteiatai().equals(shnData.getLctgrycd())) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("meisaiBean.shncd", "商品コード"), MessageID.W134, "定例区分はサーバーレンタル",
                    "商品コードはサーバーレンタル以外");
            }
        }

        // 定例区分（商品レンタル）の場合
        if (CommonConstants.TEIREIKBN_2.equals(dto.getTeireikbn())) {
            s02m006tData1 = getSkmt(CommonConstants.APCD_000, CommonConstants.SYS_SHOPCD_000,
                CommonConstants.SYS_SETTEINO_096, CommonConstants.RECORD_KBN_M,
                CommonConstants.SETTEICD_096_2);

            // 商品大分類がレンタル用商品大分類以外、かつ商品大分類がAP商品(90)で商品中分類がレンタル商品中分類(099)以外はエラー
            if (!ObjectUtils.isEmpty(s02m006tData1)
                && !s02m006tData1.getMojisetteiatai().equals(shnData.getLctgrycd())) {
                if (CommonConstants.LCTGRYCD_90.equals(shnData.getLctgrycd())) {
                    s02m006tData2 = getSkmt(CommonConstants.APCD_000,
                        CommonConstants.SYS_SHOPCD_000, CommonConstants.SYS_SETTEINO_918,
                        CommonConstants.RECORD_KBN_M, CommonConstants.SETTEICD_918_1);

                    if (!ObjectUtils.isEmpty(s02m006tData2)
                        && !s02m006tData2.getMojisetteiatai().equals(shnData.getMctgrycd())) {
                        throw BusinessExceptionFactory.validationFailed(
                            new FormField("meisaiBean.shncd", "商品コード"), MessageID.W134,
                            "定例区分は商品レンタル", "商品コードは商品レンタル以外");
                    }
                } else {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("meisaiBean.shncd", "商品コード"), MessageID.W134, "定例区分は商品レンタル",
                        "商品コードは商品レンタル以外");
                }
            }
        }

        // 定例区分（通常取引）か定例区分（アクアクララ光）の場合
        if (CommonConstants.TEIREIKBN_3.equals(dto.getTeireikbn())
            || CommonConstants.TEIREIKBN_4.equals(dto.getTeireikbn())) {

            s02m006tData1 = getSkmt(CommonConstants.APCD_000, CommonConstants.SYS_SHOPCD_000,
                CommonConstants.SYS_SETTEINO_096, CommonConstants.RECORD_KBN_M,
                CommonConstants.SETTEICD_096_8);

            s02m006tData2 = getSkmt(CommonConstants.APCD_000, CommonConstants.SYS_SHOPCD_000,
                CommonConstants.SYS_SETTEINO_918, CommonConstants.RECORD_KBN_M,
                CommonConstants.SETTEICD_918_2);

            // 定例区分（通常取引）の場合
            if (CommonConstants.TEIREIKBN_3.equals(dto.getTeireikbn())) {
                // 商品大分類がコラボ・サービスの場合、商品中分類がアクアクララ光はエラー
                if (!ObjectUtils.isEmpty(s02m006tData1)
                    && s02m006tData1.getMojisetteiatai().equals(shnData.getLctgrycd())) {
                    if (!ObjectUtils.isEmpty(s02m006tData2)
                        && s02m006tData2.getMojisetteiatai().equals(shnData.getMctgrycd())) {
                        throw BusinessExceptionFactory.validationFailed(
                            new FormField("meisaiBean.shncd", "商品コード"), MessageID.W134, "定例区分は通常取引",
                            "商品コードはアクアクララ光");
                    }
                }
            }

            // 定例区分（アクアクララ光）の場合
            if (CommonConstants.TEIREIKBN_4.equals(dto.getTeireikbn())) {
                // 商品大分類がコラボ・サービスの場合、商品中分類がアクアクララ光以外はエラー
                if (!ObjectUtils.isEmpty(s02m006tData1)
                    && s02m006tData1.getMojisetteiatai().equals(shnData.getLctgrycd())) {
                    if (!ObjectUtils.isEmpty(s02m006tData2)
                        && !s02m006tData2.getMojisetteiatai().equals(shnData.getMctgrycd())) {
                        throw BusinessExceptionFactory.validationFailed(
                            new FormField("meisaiBean.shncd", "商品コード"), MessageID.W134,
                            "定例区分はアクアクララ光", "商品コードはアクアクララ光以外");
                    }
                } else {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("meisaiBean.shncd", "商品コード"), MessageID.W134, "定例区分はアクアクララ光",
                        "商品コードはアクアクララ光以外");
                }
            }
        }

        // 伝票区分が「売上のみ」か「経費のみ」の場合、商品コードの取扱区分が「仕入のみ」はエラー
        if ((CommonConstants.KBN_URIAGE_NOMI.equals(dnpykbn)
            || CommonConstants.KBN_KEIHI_NOMI.equals(dnpykbn))
            && (CommonConstants.KBN_TORIATSUKAI_SHIIRE.equals(shnData.getTrtskikbn()))) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("meisaiBean.shncd", "商品コード"), MessageID.W134, "伝票区分＝売上のみまたは経費のみ",
                "商品コードに仕入のみ");
        }

        // 定例区分＝「サーバーレンタル」、かつ有効区分＝１(有効)の場合
        if (dto.getTeireikbn().equals(CommonConstants.TEIREIKBN_1)
            && dto.getYukokbn().equals(CommonConstants.YUKOKBN_1)) {

            String rtlrknshukeikbn = "";

            if (dto.getRtlrknshukeikbn().equals(CommonConstants.RTLRKNSHUKEIKBN_0)) {
                rtlrknshukeikbn = CommonConstants.RTLRKNSHUKEIKBN_1;
            } else {
                rtlrknshukeikbn = CommonConstants.RTLRKNSHUKEIKBN_0;
            }

            // 「定例区分：指定あり」で定例売上仕入れマスタを検索
            S02M016TEntity s02m016tKizonData = getTusmKizonData(apkihoncd, shopcd, tokuisakicd,
                CommonConstants.TEIREIKBN_1, CommonConstants.URISIIREKBN_1, rtlrknshukeikbn,
                CommonConstants.YUKOKBN_1, dto.getTeireihasseino());

            // 定例区分＝「サーバーレンタル」で既に定例売上仕入マスタに異なる売上設定情報が存在する場合はエラー
            if (!ObjectUtils.isEmpty(s02m016tKizonData)) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W133, "異なる売上設定のデータが登録");
            }

            // 定例区分＝「サーバーレンタル」・売上設定＝「得意先設置サーバーマスタ」で
            // 定例売上仕入マスタに 得意先設置サーバーマスタの売上設定情報が存在する場合はエラー
            if (dto.getRtlrknshukeikbn().equals(CommonConstants.RTLRKNSHUKEIKBN_1)) {

                // 「定例区分：指定なし」で定例売上仕入れマスタを検索
                s02m016tKizonData = getTusmKizonDataForTokuisaki(apkihoncd, shopcd, tokuisakicd,
                    CommonConstants.URISIIREKBN_1, CommonConstants.RTLRKNSHUKEIKBN_1,
                    CommonConstants.YUKOKBN_1, dto.getTeireihasseino());

                if (!ObjectUtils.isEmpty(s02m016tKizonData)) {
                    throw BusinessExceptionFactory.validationFailed(MessageID.W133,
                        "売上設定：得意先設置サーバーデータが登録");
                }
            }
        }

        // 明細備考が空ではない場合チェックする
        if (!StringUtils.isEmpty(dto.getMeisaibiko())) {

            // システム管理マスタを検索
            S02M006TEntity s02m006tData =
                getSkmt(apkihoncd, shopcd, CommonConstants.SYS_SETTEINO_917,
                    CommonConstants.RECORD_KBN_M, CommonConstants.SETTEICD_917_1);

            // システム管理マスタに明細備考の桁数制御の設定が存在する場合で、入力されている文字数（バイト）が16文字を超過している場合、エラー
            if (!ObjectUtils.isEmpty(s02m006tData)
                && !ItemUtil.isByteSizeValid(dto.getMeisaibiko(), 16)) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("meisaiBean.meisaibiko", "明細備考"), MessageID.W120, "備考の文字数",
                    "16桁以下");
            }
        }

        // レンタル料金集計区分：得意先設置サーバーマスタ（"1"）、定例区分：サーバーレンタル（"1"）、売上仕入区分：売上（"1"）、
        // 伝票発生フラグ：発生（"1"）、有効区分：有効（"1"）が選択されている場合チェックする
        if (dto.getRtlrknshukeikbn().equals(CommonConstants.RTLRKNSHUKEIKBN_1)
            && dto.getTeireikbn().equals(CommonConstants.TEIREIKBN_1)
            && dto.getUrisiirekbn().equals(CommonConstants.URISIIREKBN_1)
            && dto.getDnpyhasseiarinashiflg().equals(CommonConstants.DNPYHASSEIARINASHIFLG_1)
            && dto.getYukokbn().equals(CommonConstants.YUKOKBN_1)) {

            List<S02M048TEntity> s02M048TList = getS02M048TList(apkihoncd, shopcd, tokuisakicd);
            if (!ObjectUtils.isEmpty(s02M048TList)) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W171,
                    "得意先設置サーバー管理マスタにレンタル開始日が設定されていない情報がある場合", "更新");
            }
        }

        // 定例売上仕入状態テーブルが処理中かを確認
        String prcsjotai = getPrcsjotai(apkihoncd, shopcd);
        if (!StringUtils.isEmpty(prcsjotai) && prcsjotai.equals(CommonConstants.PRCSJOTAI_1)) {
            throw BusinessExceptionFactory.validationFailed(MessageID.W171, "定例売上仕入処理を実施中",
                "定例売上仕入マスタの更新は");
        }

    }

    /**
     * 消費税率取得処理
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param taisyouDate 消費税率確認基準日
     * @return String 消費税率
     */
    private String getTaxRate(String apkihoncd, String shopcd, String taisyouDate) {

        S02M046TEntityExample example = new S02M046TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd)
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

        // ショップマスタからのデータ取得
        List<S02M046TEntity> results = s02m046tDao.selectByExample(example);

        if (results == null || results.size() == 0) {
            return null;
        }

        String tmpDate = DateTimeUtil.toSlashedDate(results.get(0).getShohitaxkirikaedd());

        String taxRate = DateTimeUtil.isLater(tmpDate, taisyouDate)
            ? (results.get(0).getShinshohitax().multiply(new BigDecimal("0.01"))).toString()
            : (results.get(0).getGenzshohitax().multiply(new BigDecimal("0.01"))).toString();

        return taxRate;
    }

    /**
     * 商品詳細情報取得処理
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param shncd 商品コード
     * @return List<String> 商品詳細情報
     */
    public B0505W02ShnDisplayDto getShnData(String apkihoncd, String shopcd, String shncd) {

        S02V001TEntityKey s02v001tEntityKey = new S02V001TEntityKey();
        s02v001tEntityKey.setApkihoncd(apkihoncd);
        s02v001tEntityKey.setShopcd(shopcd);
        s02v001tEntityKey.setShncd(shncd);

        // 商品マスタビューの取得
        S02V001TEntity result = s02v001tDao.selectByPrimaryKey(s02v001tEntityKey);

        if (ObjectUtils.isEmpty(result)) {
            return null;
        } else {
            return createShnSelectResultDto(result);
        }
    }

    /**
     * 商品詳細情報取得処理(子画面にて商品選択)
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param shncd 商品コード
     * @param tokuisakicd 得意先コード
     * @param unyoDate 運用日付
     * @return B0505W02ShnDisplayForSubWindowDto 商品詳細情報DTO
     */
    public B0505W02ShnDisplayForSubWindowDto getShnDataForSubWindow(String apkihoncd, String shopcd,
        String shncd, String tokuisakicd, String unyoDate) {

        S02V001TEntityKey s02v001tEntityKey = new S02V001TEntityKey();
        s02v001tEntityKey.setApkihoncd(apkihoncd);
        s02v001tEntityKey.setShopcd(shopcd);
        s02v001tEntityKey.setShncd(shncd);

        // 商品マスタビューの取得
        S02V001TEntity result = s02v001tDao.selectByPrimaryKey(s02v001tEntityKey);

        if (ObjectUtils.isEmpty(result)) {
            return null;
        } else if (CommonConstants.STKBN_1.equals(result.getStkbn())) {
            // セット商品の選択はエラー
            throw BusinessExceptionFactory.validationFailed(
                new FormField("meisaiBean.shncd", "商品コード"), MessageID.W171, "セット商品", "選択");
        } else {
            return createShnSelectResultDtoForSubWindow(result, tokuisakicd, unyoDate);
        }
    }

    /**
     * 定例売上仕入マスタ既存データ取得処理
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @param teireikbn 定例区分
     * @param urisiirekbn 売上仕入区分
     * @param rtlrknshukeikbn レンタル集計区分（売上設定）
     * @param yukokbn 有効区分
     * @param teireihasseino 定例発生番号
     * @return S02M016TEntity 定例売上仕入マスタエンティティ
     */
    private S02M016TEntity getTusmKizonData(String apkihoncd, String shopcd, String tokuisakicd,
        String teireikbn, String urisiirekbn, String rtlrknshukeikbn, String yukokbn,
        String teireihasseino) {

        S02M016TEntityExample example = new S02M016TEntityExample();

        // クライテリア(SQL条件)の設定（定例発生番号が存在する場合は、定例発生番号を条件に含める）
        if (!StringUtils.isEmpty(teireihasseino)) {
            example.createCriteria().andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd)
                .andTokuisakicdEqualTo(tokuisakicd).andTeireikbnEqualTo(teireikbn)
                .andUrisiirekbnEqualTo(urisiirekbn).andRtlrknshukeikbnEqualTo(rtlrknshukeikbn)
                .andYukokbnEqualTo(yukokbn).andTeireihasseinoNotEqualTo(teireihasseino);
        } else {
            example.createCriteria().andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd)
                .andTokuisakicdEqualTo(tokuisakicd).andTeireikbnEqualTo(teireikbn)
                .andUrisiirekbnEqualTo(urisiirekbn).andRtlrknshukeikbnEqualTo(rtlrknshukeikbn)
                .andYukokbnEqualTo(yukokbn);
        }

        // 定例売上仕入マスタからのデータ取得
        List<S02M016TEntity> results = s02m016tDao.selectByExample(example);

        if (results == null || results.size() == 0) {
            return null;
        } else {
            return results.get(0);
        }
    }

    /**
     * 定例売上仕入マスタ既存データ取得処理(売上設定＝「得意先設置サーバーマスタ」)
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @param urisiirekbn 売上仕入区分
     * @param rtlrknshukeikbn レンタル集計区分（売上設定）
     * @param yukokbn 有効区分
     * @param teireihasseino 定例発生番号
     * @return S02M016TEntity 定例売上仕入マスタエンティティ
     */
    private S02M016TEntity getTusmKizonDataForTokuisaki(String apkihoncd, String shopcd,
        String tokuisakicd, String urisiirekbn, String rtlrknshukeikbn, String yukokbn,
        String teireihasseino) {

        S02M016TEntityExample example = new S02M016TEntityExample();

        // クライテリア(SQL条件)の設定（定例発生番号が存在する場合は、定例発生番号を条件に含める）
        if (!StringUtils.isEmpty(teireihasseino)) {
            example.createCriteria().andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd)
                .andTokuisakicdEqualTo(tokuisakicd).andUrisiirekbnEqualTo(urisiirekbn)
                .andRtlrknshukeikbnEqualTo(rtlrknshukeikbn).andYukokbnEqualTo(yukokbn)
                .andTeireihasseinoNotEqualTo(teireihasseino);
        } else {
            example.createCriteria().andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd)
                .andTokuisakicdEqualTo(tokuisakicd).andUrisiirekbnEqualTo(urisiirekbn)
                .andRtlrknshukeikbnEqualTo(rtlrknshukeikbn).andYukokbnEqualTo(yukokbn);
        }

        // 定例売上仕入マスタからのデータ取得
        List<S02M016TEntity> results = s02m016tDao.selectByExample(example);

        if (results == null || results.size() == 0) {
            return null;
        } else {
            return results.get(0);
        }
    }

    /**
     * システム管理マスタ取得処理
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param setteino 設定番号
     * @param rcrdkbn レコード区分
     * @param setteicd 設定コード
     * @return システム管理マスタエンティティ
     */
    private S02M006TEntity getSkmt(String apkihoncd, String shopcd, String setteino, String rcrdkbn,
        String setteicd) {

        S02M006TEntityKey s02m006tEntityKey = new S02M006TEntityKey();
        s02m006tEntityKey.setApkihoncd(apkihoncd);
        s02m006tEntityKey.setShopcd(shopcd);
        s02m006tEntityKey.setSetteino(setteino);
        s02m006tEntityKey.setRcrdkbn(rcrdkbn);
        s02m006tEntityKey.setSetteicd(setteicd);

        // システム管理マスタからのデータ取得
        return s02m006tDao.selectByPrimaryKey(s02m006tEntityKey);
    }

    /**
     * 処理状態取得処理
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @return 処理状態
     */
    private String getPrcsjotai(String apkihoncd, String shopcd) {

        S06T003TEntityKey s06t003tEntityKey = new S06T003TEntityKey();
        s06t003tEntityKey.setApkihoncd(apkihoncd);
        s06t003tEntityKey.setShopcd(shopcd);

        // 定例売上仕入状態テーブルからのデータ取得
        S06T003TEntity result = s06t003tDao.selectByPrimaryKey(s06t003tEntityKey);

        if (ObjectUtils.isEmpty(result)) {
            return null;
        } else {
            return result.getPrcsjotai();
        }
    }

    /**
     * 得意先設置サーバー管理マスタ有効区分取得処理
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @return 得意先設置サーバー管理マスタエンティティ
     */
    private List<S02M048TEntity> getS02M048TList(String apkihoncd, String shopcd,
        String tokuisakicd) {

        S02M048TEntityExample example = new S02M048TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd)
            .andTokuisakicdEqualTo(tokuisakicd).andKeijokbnEqualTo(CommonConstants.KBN_KEIJO_RENTAL)
            .andRtlstrtddIsNull().andHikiageddIsNull().andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

        // 得意先設置サーバー管理マスタの取得
        return s02m048tDao.selectByExample(example);
    }

    /**
     * 総数算出処理
     * 
     * @param suryo 数量
     * @param irisu 入数
     * @return String 総数
     */
    private String calcSokazu(BigDecimal suryo, Long irisu) {
        // 引数（売上入数）がnullでない場合、総数算出処理を行う。
        if (!StringUtils.isEmpty(irisu)) {
            return bigChangeStr(suryo.multiply(BigDecimal.valueOf(irisu)));
        } else {
            // 引数（売上入数）がnullの場合、総数算出処理は行わずに"0"を返す。
            return "0";
        }
    }

    /**
     * 各種金額算出処理
     * 
     * @param suryo 数量
     * @param kin 金額
     * @param tax 消費税
     * @return 算出金額
     */
    private String culcNum(String suryo, String kin, String tax) {
        BigDecimal bigSuryo = new BigDecimal(suryo);
        BigDecimal bigKin = new BigDecimal(kin);
        BigDecimal bigTax = new BigDecimal(tax);

        return (bigSuryo.multiply(bigKin).multiply(bigTax)).setScale(0, RoundingMode.DOWN)
            .toString();
    }

    /**
     * 消費税金額計算処理(登録／更新時)
     * 
     * @param dto 登録更新用DTO
     * @param entity 定例売上仕入マスタENTITY
     */
    private void calcShohitaxkin(B0505W02UpdateDto dto, S02M016TEntity entity) {
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

                // ショップマスタの取得
                S02M046TEntityKey s02m046tEntityKey = new S02M046TEntityKey();
                s02m046tEntityKey.setApkihoncd(dto.getApkihoncd());
                s02m046tEntityKey.setShopcd(dto.getShopcd());
                S02M046TEntity s02m046tEntity = s02m046tDao.selectByPrimaryKey(s02m046tEntityKey);

                // 商品マスタビューの取得
                S02V001TEntityKey s02v001tEntityKey = new S02V001TEntityKey();
                s02v001tEntityKey.setApkihoncd(dto.getApkihoncd());
                s02v001tEntityKey.setShopcd(dto.getShopcd());
                s02v001tEntityKey.setShncd(dto.getShncd());
                S02V001TEntity s02v001tEntity = s02v001tDao.selectByPrimaryKey(s02v001tEntityKey);

                // 得意先マスタの取得
                S02M013TEntityKey s02m013tEntityKey = new S02M013TEntityKey();
                s02m013tEntityKey.setApkihoncd(dto.getApkihoncd());
                s02m013tEntityKey.setShopcd(dto.getShopcd());
                s02m013tEntityKey.setTokuisakicd(dto.getTokuisakicd1());
                S02M013TEntity s02m013tEntity = s02m013tDao.selectByPrimaryKey(s02m013tEntityKey);

                // 税区分の設定
                String taxkbn = CommonConstants.KBN_KEIJO_KEIHI.equals(dto.getKeijokbn())
                    ? CommonConstants.KBN_TAX_HIKAZEI
                    : ItemUtil.toBoolean(s02v001tEntity.getTaxsetteikbn())
                        ? s02v001tEntity.getTaxkbn()
                        : null;

                // 消費税の計算
                String[] kinPriceList = taxService.getPriceList(dto.getHyojunprice(),
                    dto.getSuryo(), taxkbn, s02m013tEntity.getTokuisakikbn(),
                    dto.getJikaikeijodydm() + dto.getKeijodd(), s02m046tEntity);

                // 初回消費税の計算
                String[] shokaiKinPriceList = taxService.getPriceList(dto.getShokaiunt(),
                    dto.getSuryo(), taxkbn, s02m013tEntity.getTokuisakikbn(),
                    dto.getShokaikeijodydm() + dto.getKeijodd(), s02m046tEntity);

                shohitaxkin = Long.parseLong(kinPriceList[1]);
                shokaishohitaxkin = Long.parseLong(shokaiKinPriceList[1]);
            }
        }
        entity.setShokaishohitaxkin(shokaishohitaxkin); // 初回消費税金額
        entity.setShohitaxkin(shohitaxkin); // 消費税金額
    }



    /**
     * 消費税金額計算処理(画面描画用)
     * 
     * @param dto 消費税金額計算用DTO
     * @return B0505W02CalcTaxDto(消費税項目を計算し直して、詰め直したDTO)
     */
    public B0505W02CalcTaxDto calcTax(B0505W02CalcTaxDto dto) {
        return calcTaxkin(dto);
    }

    /**
     * 消費税金額計算処理
     * 
     * @param calcTaxDto 消費税金額計算用DTO
     * @return B0505W02CalcTaxDto(消費税項目を計算し直して、詰め直したDTO)
     */
    private B0505W02CalcTaxDto calcTaxkin(B0505W02CalcTaxDto calcTaxDto) {
        String taxkin = "0"; // 消費税金額

        // 商品コード、数量、単価が入力ありの場合
        if (!StringUtils.isEmpty(calcTaxDto.getShncd())
            && !StringUtils.isEmpty(calcTaxDto.getSuryo())
            && !StringUtils.isEmpty(calcTaxDto.getTnk())) {
            // 調整用商品コードを取得
            String cyoseishncd = s02m006tService.searchForMoji(CommonConstants.APCD_000,
                CommonConstants.SYS_SHOPCD_000, CommonConstants.SYS_SETTEINO_139,
                CommonConstants.SETTEICD_139_1);

            // 調整用商品コード ≠ 商品コードの場合
            if (!calcTaxDto.getShncd().equals(cyoseishncd)) {

                // ショップマスタの取得
                S02M046TEntityKey s02m046tEntityKey = new S02M046TEntityKey();
                s02m046tEntityKey.setApkihoncd(calcTaxDto.getApkihoncd());
                s02m046tEntityKey.setShopcd(calcTaxDto.getShopcd());
                S02M046TEntity s02m046tEntity = s02m046tDao.selectByPrimaryKey(s02m046tEntityKey);

                // 商品マスタビューの取得
                S02V001TEntityKey s02v001tEntityKey = new S02V001TEntityKey();
                s02v001tEntityKey.setApkihoncd(calcTaxDto.getApkihoncd());
                s02v001tEntityKey.setShopcd(calcTaxDto.getShopcd());
                s02v001tEntityKey.setShncd(calcTaxDto.getShncd());
                S02V001TEntity s02v001tEntity = s02v001tDao.selectByPrimaryKey(s02v001tEntityKey);

                // 得意先マスタの取得
                S02M013TEntityKey s02m013tEntityKey = new S02M013TEntityKey();
                s02m013tEntityKey.setApkihoncd(calcTaxDto.getApkihoncd());
                s02m013tEntityKey.setShopcd(calcTaxDto.getShopcd());
                s02m013tEntityKey.setTokuisakicd(calcTaxDto.getTokuisakicd1());
                S02M013TEntity s02m013tEntity = s02m013tDao.selectByPrimaryKey(s02m013tEntityKey);
                if (ObjectUtils.isEmpty(s02m013tEntity)) {
                    calcTaxDto.setTaxkin("0"); // 得意先が存在しない場合は、消費税金額「０」
                    return calcTaxDto;
                }

                // 税区分の設定
                String taxkbn = CommonConstants.KBN_KEIJO_KEIHI.equals(calcTaxDto.getKeijokbn())
                    ? CommonConstants.KBN_TAX_HIKAZEI
                    : ItemUtil.toBoolean(s02v001tEntity.getTaxsetteikbn())
                        ? s02v001tEntity.getTaxkbn()
                        : null;

                String keijodd = calcTaxDto.getKeijodd();
                if (keijodd.length() == 1) {
                    keijodd = "0" + keijodd;
                }
                String keijoymd = calcTaxDto.getKeijodydm() + keijodd;
                // 計上日が「31」の場合のみ、年月に対する月末日を再取得（画面入力として「末日は31を指定」のため）
                if ("31".equals(keijodd)) {
                    // LocalDateの月末日を取得
                    LocalDate targetDate =
                        LocalDate.parse(keijoymd, DateTimeFormatter.ofPattern(DATE_FORMAT));
                    LocalDate localdate = targetDate.with(TemporalAdjusters.lastDayOfMonth());
                    keijoymd = localdate.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
                }

                // 消費税の計算
                String[] kinPriceList =
                    taxService.getPriceList(calcTaxDto.getTnk(), calcTaxDto.getSuryo(), taxkbn,
                        s02m013tEntity.getTokuisakikbn(), keijoymd, s02m046tEntity);

                taxkin = kinPriceList[1];
            }
        }
        calcTaxDto.setTaxkin(taxkin);; // 消費税金額

        return calcTaxDto;
    }

}
