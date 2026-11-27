package jp.co.aquaclara.accis.web.domain.service.h2101;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M003TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M006TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M041TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M043TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M046TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02V001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S11T003TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S11T004TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S11T005TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S13T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S13T002TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S13T003TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.Z1804B01Dao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M003TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M003TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M006TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M041TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M041TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M043TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S11T003TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S11T004TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S11T005TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S11T005TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S11T005TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S13T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S13T001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S13T001TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S13T002TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S13T002TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S13T002TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S13T003TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.Z1804B01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S11T003TH2101W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S11T005TH2101W01Entity;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.domain.dto.h2101.DATATYPE;
import jp.co.aquaclara.accis.web.domain.dto.h2101.H2101W01SearchConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.h2101.H2101W01SearchResultDto;
import jp.co.aquaclara.accis.web.domain.dto.h2101.H2101W01SearchResultMeisaiDto;
import jp.co.aquaclara.accis.web.domain.dto.h2101.H2101W01UpdateDto;
import jp.co.aquaclara.accis.web.domain.dto.h2101.H2101W01UpdateMeisaiDto;
import jp.co.aquaclara.accis.web.domain.dto.h2101.PRCSKBN;
import jp.co.aquaclara.accis.web.domain.service.c0800.UtilityService;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import jp.co.aquaclara.accis.web.exception.dto.FormField;
import lombok.extern.slf4j.Slf4j;

/**
 * H2101w01_入出庫入力Service
 * 
 * @author accisdev003
 *
 */
@Service
@Slf4j
public class H2101W01Service {
    /** String ⇔ LocalDate変換用（yyyyMM） */
    private static final DateTimeFormatter FORMATTER_YYYYMM = DateTimeFormatter.ofPattern("yyyyMM");

    /**
     * システム管理マスタDao
     */
    @Autowired
    private S02M006TDao s02m006tDao;
    /**
     * 担当者マスタDao
     */
    @Autowired
    private S02M003TDao s02m003tDao;
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
     * 商品マスタビューDao
     */
    @Autowired
    private S02V001TDao s02v001tDao;
    /**
     * 入出庫実績テーブルDao
     */
    @Autowired
    private S11T003TDao s11t003tDao;
    /**
     * 入出庫実績明細テーブルDao
     */
    @Autowired
    private S11T004TDao s11t004tDao;
    /**
     * 入出庫仕入制御テーブルDao
     */
    @Autowired
    private S11T005TDao s11t005tDao;
    /**
     * 現在庫テーブルDao
     */
    @Autowired
    private S13T001TDao s13t001tDao;
    /**
     * 在庫テーブルDao
     */
    @Autowired
    private S13T002TDao s13t002tDao;
    /**
     * 在庫入出庫明細テーブルDao
     */
    @Autowired
    private S13T003TDao s13t003tDao;
    /**
     * 採番部品Dao
     */
    @Autowired
    private Z1804B01Dao z1804b01Dao;

    @Autowired
    private S02M043TDao s02M043TDao;

    @Autowired
    private UtilityService util;

    /**
     * 入出庫入力検索処理
     * 
     * @param dto H2101W01SearchConditionDto
     * @return H2101W01SearchResultDto
     */
    public H2101W01SearchResultDto search(H2101W01SearchConditionDto dto) {

        // 相関チェック
        checkParam(dto);

        // 入出庫仕入制御テーブル検索条件の生成
        S11T005TH2101W01Entity siireEntity = createCustomEntity(dto);

        // 入出庫伝票情報の取得
        S11T005TH2101W01Entity dnpyEntity = s11t005tDao.selectByH2101W01(siireEntity);

        // 入出庫実績テーブル検索条件の生成
        S11T003TH2101W01Entity jissekiEntity = createCustomMeisaiEntity(dto, dnpyEntity);

        // 入出庫明細情報の取得とDto変換
        List<H2101W01SearchResultMeisaiDto> meisaiDtoList =
            createResultMeisaiList(s11t003tDao.selectByH2101W01(jissekiEntity));

        return createResultDto(dnpyEntity, meisaiDtoList);
    }

    /**
     * 検索処理相関チェック
     * 
     * @param dto H2101W01SearchConditionDto
     */
    private void checkParam(H2101W01SearchConditionDto dto) {
        // 処理区分「2:取消」、かつ入出庫伝票番号設定値ありの場合、入出庫仕入制御テーブル相関チェックを実施
        if (PRCSKBN.CANCEL == PRCSKBN.of(dto.getPrcskbn())
            && !StringUtils.isEmpty(dto.getNyushukkodnpyno())) {
            s11t005tDbCheck(dto);
        }
    }

    /**
     * 入出庫仕入制御テーブル相関チェック
     * 
     * @param dto H2101W01SearchConditionDto
     */
    private void s11t005tDbCheck(H2101W01SearchConditionDto dto) {
        // 検索条件設定
        S11T005TEntityKey key = new S11T005TEntityKey();
        key.setApkihoncd(dto.getApkihoncd());
        key.setShopcd(dto.getShopcd());
        key.setDnpyno(dto.getNyushukkodnpyno());

        // 検索処理実施
        S11T005TEntity e = s11t005tDao.selectByPrimaryKey(key);

        // 検索結果が存在しない場合、エラー
        if (ObjectUtils.isEmpty(e)) {
            throw BusinessExceptionFactory.validationFailed(MessageID.W083);

            // 取消伝票番号に設定値がある(取消済データ)の場合、エラー
        } else if (!StringUtils.isEmpty(e.getTrksdnpyno())) {
            throw BusinessExceptionFactory.validationFailed(MessageID.W124, "取消済データ");
        }
    }

    /**
     * 入出庫仕入制御テーブル検索条件を生成
     * 
     * @param dto H2101W01SearchConditionDto
     * @return e S11T005TH2101W01Entity
     */
    private S11T005TH2101W01Entity createCustomEntity(H2101W01SearchConditionDto dto) {
        // 検索条件をCustomEntityに展開する
        S11T005TH2101W01Entity e = new S11T005TH2101W01Entity();

        // AP基本コード
        e.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        e.setShopcd(dto.getShopcd());
        // 入出庫伝票番号
        e.setDnpyno(dto.getNyushukkodnpyno());

        return e;
    }

    /**
     * 入出庫実績テーブル検索条件を生成
     * 
     * @param dto H2101W01SearchConditionDto
     * @param resultEntity S11T005TH2101W01Entity
     * @return e S11T003TH2101W01Entity
     */
    private S11T003TH2101W01Entity createCustomMeisaiEntity(H2101W01SearchConditionDto dto,
        S11T005TH2101W01Entity resultEntity) {

        // 検索条件をCustomEntityに展開する
        S11T003TH2101W01Entity e = new S11T003TH2101W01Entity();

        // AP基本コード
        e.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        e.setShopcd(dto.getShopcd());
        // 入出庫伝票番号
        e.setNyushukkodnpyno(dto.getNyushukkodnpyno());
        // 伝票区分
        e.setDnpykbn(resultEntity.getDnpykbn());
        // 明細最大件数
        e.setRownum(dto.getLimit());

        return e;
    }

    /**
     * 検索結果をControllerへ返却する形式へ変換
     * 
     * @param searchResultList List<S11T003TH2101W01Entity>
     * @return result List<H2101W01SearchResultMeisaiDto>
     */
    private List<H2101W01SearchResultMeisaiDto> createResultMeisaiList(
        List<S11T003TH2101W01Entity> searchResultList) {

        // 検索結果が0件の場合
        if (searchResultList.size() == 0) {
            throw BusinessExceptionFactory.dataNotFound(MessageID.W083);
        }

        return util.convertList(searchResultList, e -> {
            H2101W01SearchResultMeisaiDto d = new H2101W01SearchResultMeisaiDto();
            // 商品コード
            d.setShncd(e.getShncd());
            // 商品略称名
            d.setShnnm(e.getShnnmryakusho());
            // 色
            d.setIro(e.getIro());
            // サイズ
            d.setSznm(e.getSz());
            // 型式
            d.setKatasikimei(e.getKatasiki());
            // 数量
            d.setSuryo(e.getSuryo());
            // 数量単位
            d.setSiiretrtskitaninm(e.getSiiretrtskitani());
            // 入数
            d.setIrisu(e.getSiireirisu());
            // 入数単位
            d.setSiiretaninm(e.getSiiretani());
            // 総数
            d.setSokazu(e.getSiiresokazu());
            // 単価
            d.setUnt(e.getUnt());
            // 金額
            d.setKin(e.getKin());
            // 備考
            d.setBiko(e.getMeisaibiko());
            // 更新システム日付
            d.setKshnsysmymd(e.getKshnsysmymd());
            // 更新システム時間
            d.setKshnsystmjikan(e.getKshnsystmjikan());
            // 入出庫伝票明細番号
            d.setNyushukkodnpymeisaino(e.getNyushukkodnpymeisaino());
            // 大分類コード
            d.setLctgrycd(e.getLctgrycd());
            // 中分類コード
            d.setMctgrycd(e.getMctgrycd());
            // 小分類コード
            d.setSctgrycd(e.getSctgrycd());
            // 明細備考
            d.setMeisaibiko(e.getMeisaibiko());
            // 商品AP基本コード
            d.setShnapkihoncd(e.getShnapkihoncd());
            // 価格区分
            d.setPricekbn(e.getPricekbn());
            // 標準価格切替日
            d.setHyojunpricekirikaedd(e.getHyojunpricekirikaedd());
            // 現標準価格
            d.setGenzhyojunprice(e.getGenzhyojunprice());
            // 新標準価格
            d.setShinhyojunprice(e.getShinhyojunprice());
            // 現持ち帰り価格
            d.setGenzmochikaeriprice(e.getGenzmochikaeriprice());
            // 新持ち帰り価格
            d.setShinmochikaeriprice(e.getShinmochikaeriprice());
            // オープン価格切替日
            d.setOpnpricekirikaedd(e.getOpnpricekirikaedd());
            // 現オープン価格
            d.setGenzopnprice(e.getGenzopnprice());
            // 新オープン価格
            d.setShinopnprice(e.getShinopnprice());
            // 現オープン持ち帰り価格
            d.setGenzopnmochikaeriprice(e.getGenzopnmochikaeriprice());
            // 新オープン持ち帰り価格
            d.setShinopnmochikaeriprice(e.getShinopnmochikaeriprice());
            // 販促区分
            d.setHansokukbn(e.getHansokukbn());
            // 販促開始日
            d.setHansokustrtdd(e.getHansokustrtdd());
            // 販促終了日
            d.setHansokushrydd(e.getHansokushrydd());
            // 販促配達価格
            d.setHansokudlvryprice(e.getHansokudlvryprice());
            // 販促持ち帰り価格
            d.setHansokumochikaeriprice(e.getHansokumochikaeriprice());
            // 在庫単価
            d.setZaikount(e.getZaikount());
            // 売上取扱単位名称
            d.setUritrtskitani(e.getUritrtskitani());
            // 売上入数
            d.setUriirisu(e.getUriirisu());
            // 売上単位名称
            d.setUritani(e.getUritani());
            // 在庫管理対象フラグ
            d.setZaikokanritrgtflgshmv(e.getZaikokanritrgtflgshmv());
            // 在庫管理区分
            d.setZaikokanrikbnshopm(e.getZaikokanrikbnshopm());
            // 仕入取扱単位名称
            d.setSiiretrtskitani(e.getSiiretrtskitani());
            // 仕入入数
            d.setSiireirisu(e.getSiireirisu());
            // 仕入単位名称
            d.setSiiretani(e.getSiiretani());
            // 税設定区分
            d.setTaxsetteikbn(e.getTaxsetteikbn());
            // 税区分
            d.setTaxkbn(e.getTaxkbn());
            // 在庫アラーム数量
            d.setZaikoaramusuryo(e.getZaikoaramusuryo());
            // 取扱区分
            d.setTrtskikbn(e.getTrtskikbn());
            // 仕入総数
            d.setSiiresokazu(e.getSiiresokazu());
            // 売上総数
            d.setUrisokazu(e.getUrisokazu());
            return d;
        });
    }

    /**
     * 検索結果をControllerへ返却する形式へ変換
     * 
     * @param e S11T005TH2101W01Entity
     * @param meisaiDtoList List<H2101W01SearchResultMeisaiDto>
     * @return result H2101W01SearchResultDto
     */
    private H2101W01SearchResultDto createResultDto(S11T005TH2101W01Entity e,
        List<H2101W01SearchResultMeisaiDto> meisaiDtoList) {

        H2101W01SearchResultDto result = new H2101W01SearchResultDto(e.getDnpykbn(), // 伝票区分
            e.getDnpykbnnm(), // 伝票区分ラベル
            e.getDnpyno(), // 伝票番号
            e.getDnpybiko(), // 備考
            DateTimeUtil.toSlashedDate(e.getSiiredd()), // 仕入日
            e.getKin(), // 仕入金額
            e.getNyukamototorihikikbn(), // 入荷元取引区分
            e.getNyukamototorihikikbnnm(), // 入荷元取引区分名
            e.getNyukamotojissekitnshacd(), // 入荷元実績担当者コード
            e.getJissekitnshamei1(), // 入荷元実績担当者名
            e.getNyukamotocd(), // 入荷元倉庫コード
            e.getIdomotosokonm(), // 入荷元倉庫名
            e.getShukkasakitorihikikbn(), // 出荷先取引区分
            e.getShukkasakitorihikikbnnm(), // 出荷先取引区分名
            e.getShukkasakijissekitnshacd(), // 出荷先実績担当者コード
            e.getJissekitnshamei2(), // 出荷先実績担当者名
            e.getShukkasakicd(), // 出荷先倉庫コード
            e.getIdosakisokonm(), // 出荷先倉庫名
            meisaiDtoList // 明細Dtoリスト
        );
        return result;
    }

    /**
     * 追加行商品データ取得処理
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param shncd 商品コード
     * @param nyushukkodd 入出庫日
     * @param unyodd 運用日付
     * @return H2101W01SearchResultMeisaiDto
     */
    public H2101W01SearchResultMeisaiDto getMeisaiDtoForAddRow(String apkihoncd, String shopcd,
        String shncd, String nyushukkodd, String unyodd) {
        H2101W01SearchResultMeisaiDto result = new H2101W01SearchResultMeisaiDto();

        // 相関チェック
        checkCommonParam(apkihoncd, shopcd, nyushukkodd, unyodd);

        // key生成
        S02V001TEntityExample example = new S02V001TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd)
            .andShncdEqualTo(shncd);

        // 商品マスタビューの取得
        List<S02V001TEntity> entityList = s02v001tDao.selectByExample(example);

        if (!ObjectUtils.isEmpty(entityList) && entityList.size() > 0) {
            result = createMeisaiDtoForAddRow(entityList.get(0), nyushukkodd);
        }

        return result;
    }

    /**
     * DB相関チェック(共通)
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param nyushukkodd 入出庫日
     * @param unyodd 運用日付
     */
    private void checkCommonParam(String apkihoncd, String shopcd, String nyushukkodd,
        String unyodd) {
        // システム管理マスタから過去日数と未来日数を取得
        long oldDays = getNissu(apkihoncd, shopcd, CommonConstants.SETTEICD_040_1);
        long futureDays = getNissu(apkihoncd, shopcd, CommonConstants.SETTEICD_040_2);
        // 運用日付を元にした許容過去日・未来日の生成
        LocalDate baseDate = util.getLocalDate(unyodd);
        LocalDate pastDate = baseDate.minusDays(oldDays);
        LocalDate futureDate = baseDate.plusDays(futureDays);
        // 入出庫日
        LocalDate nyushukkoDate = util.getLocalDate(nyushukkodd);
        // 入出庫日の許容過去日・未来日範囲チェック
        if (nyushukkoDate.isBefore(pastDate) || nyushukkoDate.isAfter(futureDate)) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("slipBean.siiredd", "入出庫日"), MessageID.W115, "入出庫日",
                String.format("%s日前", oldDays), String.format("%s日後", futureDays));
        }
    }

    /**
     * 未来日数過去日数を取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param setteicd 設定コード
     * @return 日数
     */
    private long getNissu(String apkihoncd, String shopcd, String setteicd) {
        // 検索条件作成
        S02M006TEntityKey key = new S02M006TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setSetteino(CommonConstants.SYS_SETTEINO_040);
        key.setRcrdkbn(CommonConstants.RECORD_KBN_M);
        key.setSetteicd(setteicd);

        // システム管理マスタから取得（取得できない場合は0を返す）
        return Optional.ofNullable(s02m006tDao.selectByPrimaryKey(key))
            .map(e -> e.getSuchisetteiatai()).map(d -> d.longValue()).orElse(0L);
    }

    /**
     * 商品マスタビュー検索結果をControllerへ返却する形式へ変換する
     * 
     * @param entity S02V001TEntity
     * @param nyushukkodd 入出庫日
     * @return resultDto H2101W01SearchResultMeisaiDto
     */
    private H2101W01SearchResultMeisaiDto createMeisaiDtoForAddRow(S02V001TEntity entity,
        String nyushukkodd) {
        // 商品名の設定
        String shnmei = ItemUtil.getItem(entity.getGenzshnnmryakusho(),
            entity.getShinshnnmryakusho(), entity.getShnnmkirikaedd(), nyushukkodd);

        H2101W01SearchResultMeisaiDto resultDto = new H2101W01SearchResultMeisaiDto(
            entity.getShncd(), // 商品コード
            shnmei, // 商品略称名
            entity.getIro(), // 色
            entity.getSznm(), // サイズ
            "", // 型式
            "", // 型式名称
            "", // 数量
            // entity.getUritaninm(), // 数量単位
            entity.getSiiretrtskitaninm(), // 数量単位
            entity.getSiireirisu() != null ? String.valueOf(entity.getSiireirisu()) : "", // 入数
            // entity.getUritaninm(), // 入数単位
            entity.getSiiretaninm(), // 入数単位
            "", // 総数
            entity.getSiireunt() != null ? entity.getSiireunt().toString() : "", // 単価
            "0", // 金額
            "", // 備考
            entity.getKshnsystmymd(), // 更新システム日付
            entity.getKshnsystmjikan(), // 更新システム時間
            "", // 入出庫伝票明細番号
            entity.getLctgrycd(), // 大分類コード
            entity.getMctgrycd(), // 中分類コード
            entity.getSctgrycd(), // 小分類コード
            "", // 明細備考
            entity.getShnapkihoncd(), // 商品AP基本コード
            "", // 価格区分
            entity.getHyojunpricekirikaedd() != null ? entity.getHyojunpricekirikaedd().toString()
                : "", // 標準価格切替日
            entity.getGenzhyojunprice() != null ? entity.getGenzhyojunprice().toString() : "", // 現標準価格
            entity.getShinhyojunprice() != null ? entity.getShinhyojunprice().toString() : "", // 新標準価格
            entity.getGenzmochikaeriprice() != null ? entity.getGenzmochikaeriprice().toString()
                : "", // 現持ち帰り価格
            entity.getShinmochikaeriprice() != null ? entity.getShinmochikaeriprice().toString()
                : "", // 新持ち帰り価格
            entity.getOpnpricekirikaedd(), // オープン価格切替日
            entity.getGenzopnprice() != null ? entity.getGenzopnprice().toString() : "", // 現オープン価格
            entity.getShinopnprice() != null ? entity.getShinopnprice().toString() : "", // 新オープン価格
            entity.getGenzopnmochikaeriprice() != null
                ? entity.getGenzopnmochikaeriprice().toString()
                : "", // 現オープン持ち帰り価格
            entity.getShinopnmochikaeriprice() != null
                ? entity.getShinopnmochikaeriprice().toString()
                : "", // 新オープン持ち帰り価格
            entity.getHansokukbn(), // 販促区分
            entity.getHansokustrtdd(), // 販促開始日
            entity.getHansokushrydd(), // 販促終了日
            entity.getHansokudlvryprice() != null ? entity.getHansokudlvryprice().toString() : "", // 販促配達価格
            entity.getHansokumochikaeriprice() != null
                ? entity.getHansokumochikaeriprice().toString()
                : "", // 販促持ち帰り価格
            entity.getZaikount() != null ? String.valueOf(entity.getZaikount()) : "", // 在庫単価
            entity.getUritrtskitaninm(), // 売上取扱単位名称
            entity.getUriirisu() != null ? String.valueOf(entity.getUriirisu()) : "", // 売上入数
            entity.getUritaninm(), // 売上単位名称
            entity.getZaikokanritrgtflg(), // 在庫管理対象フラグ
            "", // 在庫管理区分
            entity.getSiiretrtskitaninm(), // 仕入取扱単位名称
            entity.getSiireirisu() != null ? String.valueOf(entity.getSiireirisu()) : "", // 仕入入数
            entity.getSiiretaninm(), // 仕入単位名称
            entity.getTaxsetteikbn(), // 税設定区分
            entity.getTaxkbn(), // 税区分
            entity.getZaikoaramusuryo() != null ? String.valueOf(entity.getZaikoaramusuryo()) : "", // 在庫アラーム数量
            entity.getTrtskikbn(), // 取扱区分
            "", // 仕入総数
            "" // 売上総数
        );
        return resultDto;
    }

    /**
     * 更新処理
     * 
     * @param dto H2101W01UpdateDto
     * @param updateMeisaiDtoList List<H2101W01UpdateMeisaiDto>
     */
    @Transactional
    public void update(H2101W01UpdateDto dto, List<H2101W01UpdateMeisaiDto> updateMeisaiDtoList) {
        // DB相関チェック
        if (dto.isRegistProcess()) {
            checkCommonParam(dto.getApkihoncd(), dto.getShopcd(), dto.getNyushukkodd(),
                dto.getUnyoymd());
        }
        checkParamUpdate(dto);

        // 商品マスタビューから商品情報を取得
        getShnData(dto, updateMeisaiDtoList);

        // ショップマスタのデータ取得とDTO設定
        s02m046tSetData(dto);

        // 入出庫伝票採番処理
        dnpynoSaiban(dto);

        // 入出庫実績テーブル登録処理
        s11t003tEntry(dto);

        for (H2101W01UpdateMeisaiDto updateMeisaiDto : updateMeisaiDtoList) {
            // 入出庫実績明細テーブル登録処理
            s11t004tEntry(dto, updateMeisaiDto);

            // ショップマスタ.在庫管理区分「0:無し」以外、かつ商品マスタビュー.在庫管理対象フラグ「0:在庫管理しない」以外、
            // かつ入出庫日>運用日付以外の場合
            if (!CommonConstants.ZAIKOKANRIKBN_0.equals(dto.getZaikokanrikbn())
                && !CommonConstants.ZAIKOKANRITRGTFLG_0
                    .equals(updateMeisaiDto.getZaikokanritrgtflg())
                && dto.getUnyoymd().compareTo(dto.getNyushukkodd()) >= 0) {

                // 伝票区分「03：入庫」の場合、ショップ商品マスタ更新処理
                if (dto.isReceipt()) {
                    s02m043tUpdate(dto, updateMeisaiDto);
                }

                // 連番値定義
                short renban = 1;

                // 伝票区分「03：調整入庫」「04:入庫」「22：倉庫移動」の場合、入庫データの登録・更新処理
                if (dto.isAdjustReceipt() || dto.isReceipt() || dto.isMovement()) {
                    // 現在庫テーブル登録・更新処理(入庫データ）
                    s13t001tEntryUpdate(dto, updateMeisaiDto, DATATYPE.RECEIPT);

                    // 在庫テーブル登録・更新処理(入庫データ)
                    s13t002tEntryUpdate(dto, updateMeisaiDto, DATATYPE.RECEIPT);

                    // 在庫入出庫明細テーブル登録処理(入庫データ)
                    s13t003tEntry(dto, updateMeisaiDto, DATATYPE.RECEIPT, renban);

                    // 連番インクリメント
                    renban++;
                }

                // 伝票区分「13：調整出庫」「14:出庫」「22：倉庫移動」の場合、出庫データの登録・更新処理
                if (dto.isAdjustShipment() || dto.isShipment() || dto.isMovement()) {
                    // 現在庫テーブル登録・更新処理(出庫データ）
                    s13t001tEntryUpdate(dto, updateMeisaiDto, DATATYPE.SHIPMENT);

                    // 在庫テーブル登録・更新処理(出庫データ)
                    s13t002tEntryUpdate(dto, updateMeisaiDto, DATATYPE.SHIPMENT);

                    // 在庫入出庫明細テーブル登録処理(出庫データ)
                    s13t003tEntry(dto, updateMeisaiDto, DATATYPE.SHIPMENT, renban);
                }
            }
        }

        // 入出庫仕入制御テーブル登録処理
        s11t005tEntryUpdate(dto, updateMeisaiDtoList.size());
    }

    /**
     * 更新処理のDB相関チェック
     * 
     * @param dto H2101W01UpdateDto
     */
    private void checkParamUpdate(H2101W01UpdateDto dto) {

        // 処理区分「1：新規」「2:取消」のチェック
        if (dto.isRegistProcess() || dto.isCancelProcess()) {
            // 伝票区分「13:調整出庫」「14:出庫」「22:倉庫移動」のチェック
            if (dto.isAdjustShipment() || dto.isShipment() || dto.isMovement()) {
                // 移動元担当者のチェック
                if (!StringUtils.isEmpty(dto.getIdomotojissekitnshacd())) {
                    // 移動元担当者のデータ取得
                    S02M003TEntity s02m003tEntity = getTnshaEntity(dto.getApkihoncd(),
                        dto.getShopcd(), dto.getIdomotojissekitnshacd());
                    // 移動元担当者の存在チェック
                    if (ObjectUtils.isEmpty(s02m003tEntity)) {
                        throw BusinessExceptionFactory.validationFailed(
                            new FormField("moveBean.jissekitnshacd1", "移動元：実績担当者コード"),
                            MessageID.W084, "移動元：実績担当者コード");
                        // 移動元担当者の有効チェック
                    } else if (!util.isAvailable(s02m003tEntity.getYukokbn())) {
                        throw BusinessExceptionFactory.validationFailed(
                            new FormField("moveBean.jissekitnshacd1", "移動元：実績担当者コード"),
                            MessageID.W123, "移動元：実績担当者コード");
                    }
                }
                // 移動元倉庫のチェック
                if (!StringUtils.isEmpty(dto.getIdomotosokocd())) {
                    // 移動元倉庫のデータ取得
                    S02M041TEntity s02m041tEntity =
                        getSokoEntity(dto.getApkihoncd(), dto.getShopcd(), dto.getIdomotosokocd());
                    // 移動元倉庫の存在チェック
                    if (ObjectUtils.isEmpty(s02m041tEntity)) {
                        throw BusinessExceptionFactory.validationFailed(
                            new FormField("moveBean.nyukosakisokocd", "移動元：倉庫コード"), MessageID.W084,
                            "移動元：倉庫コード");
                    } else if (!util.isAvailable(s02m041tEntity.getYukokbn())) {
                        // 移動元倉庫の有効チェック
                        throw BusinessExceptionFactory.validationFailed(
                            new FormField("moveBean.nyukosakisokocd", "移動元：倉庫コード"), MessageID.W123,
                            "移動元：倉庫コード");
                    }
                }
            }
            // 伝票区分「03：調整入庫」「04:入庫」「22：倉庫移動」のチェック
            if (dto.isAdjustReceipt() || dto.isReceipt() || dto.isMovement()) {
                // 移動先担当者のチェック
                if (!StringUtils.isEmpty(dto.getIdosakijissekitnshacd())) {
                    // 移動先担当者のデータ取得
                    S02M003TEntity s02m003tEntity = getTnshaEntity(dto.getApkihoncd(),
                        dto.getShopcd(), dto.getIdosakijissekitnshacd());
                    // 移動先担当者の存在チェック
                    if (ObjectUtils.isEmpty(s02m003tEntity)) {
                        throw BusinessExceptionFactory.validationFailed(
                            new FormField("moveBean.jissekitnshacd2", "移動先：実績担当者コード"),
                            MessageID.W084, "移動先：実績担当者コード");
                        // 移動先担当者の有効チェック
                    } else if (!util.isAvailable(s02m003tEntity.getYukokbn())) {
                        throw BusinessExceptionFactory.validationFailed(
                            new FormField("moveBean.jissekitnshacd2", "移動先：実績担当者コード"),
                            MessageID.W123, "移動先：実績担当者コード");
                    }
                }
                // 移動先倉庫のチェック
                if (!StringUtils.isEmpty(dto.getIdosakisokocd())) {
                    // 移動先倉庫のデータ取得
                    S02M041TEntity s02m041tEntity =
                        getSokoEntity(dto.getApkihoncd(), dto.getShopcd(), dto.getIdosakisokocd());
                    // 移動先倉庫の存在チェック
                    if (ObjectUtils.isEmpty(s02m041tEntity)) {
                        throw BusinessExceptionFactory.validationFailed(
                            new FormField("moveBean.shukkosakisokocd", "移動先：倉庫コード"), MessageID.W084,
                            "移動先：倉庫コード");
                    } else if (!util.isAvailable(s02m041tEntity.getYukokbn())) {
                        // 移動先倉庫の有効チェック
                        throw BusinessExceptionFactory.validationFailed(
                            new FormField("moveBean.shukkosakisokocd", "移動先：倉庫コード"), MessageID.W123,
                            "移動先：倉庫コード");
                    }
                }
            }
        }
    }

    /**
     * 担当者マスタのPK検索
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tnshacd 担当者コード
     * @return entity S02M003TEntity
     */
    private S02M003TEntity getTnshaEntity(String apkihoncd, String shopcd, String tnshacd) {
        // 検索条件設定
        S02M003TEntityKey entityKey = new S02M003TEntityKey();
        entityKey.setApkihoncd(apkihoncd);
        entityKey.setShopcd(shopcd);
        entityKey.setTnshacd(tnshacd);

        // 担当者マスタPK検索結果を呼出元にリターン
        return s02m003tDao.selectByPrimaryKey(entityKey);
    }

    /**
     * 倉庫マスタPK検索
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param sokocd 倉庫コード
     * @return entity S02M041TEntity
     */
    private S02M041TEntity getSokoEntity(String apkihoncd, String shopcd, String sokocd) {
        // 検索条件設定
        S02M041TEntityKey entityKey = new S02M041TEntityKey();
        entityKey.setApkihoncd(apkihoncd);
        entityKey.setShopcd(shopcd);
        entityKey.setSokocd(sokocd);

        // 倉庫マスタPK検索結果を呼出元にリターン
        return s02m041tDao.selectByPrimaryKey(entityKey);
    }

    /**
     * 商品マスタビュー検索+取得値の明細Dto設定
     * 
     * @param dto H2101W01UpdateDto
     * @param meisaiDtoList H2101W01UpdateMeisaiDto
     */
    private void getShnData(H2101W01UpdateDto dto, List<H2101W01UpdateMeisaiDto> meisaiDtoList) {

        int index = 0;
        // 明細行単位で商品データを取得
        for (H2101W01UpdateMeisaiDto meisaiDto : meisaiDtoList) {
            // 商品マスタビューから商品情報を取得
            List<S02V001TEntity> s02v001tEntityList = s02v001tDao
                .selectByExample(s02v001tCreateEntityExample(dto, meisaiDto.getShncdmei()));

            // 商品情報をDTOに設定
            for (S02V001TEntity s02v001tEntity : s02v001tEntityList) {
                // 商品マスタビューのデータを元に相関チェック
                checkShnParam(dto, meisaiDto, s02v001tEntity, index);

                // 商品マスタビュー取得結果を明細Dtoに設定(1件目限定)
                // 大分類コード
                meisaiDto.setLctgrycd(s02v001tEntity.getLctgrycd());
                // 中分類コード
                meisaiDto.setMctgrycd(s02v001tEntity.getMctgrycd());
                // 小分類コード
                meisaiDto.setSctgrycd(s02v001tEntity.getSctgrycd());
                // 在庫管理対象フラグ
                meisaiDto.setZaikokanritrgtflg(s02v001tEntity.getZaikokanritrgtflg());
                break;
            }
            index++;
        }
    }

    /**
     * 商品マスタビューの検索条件設定
     * 
     * @param dto H2101W01UpdateDto
     * @param shncd 商品コード
     * @return entity S02V001TEntityExample
     */
    private S02V001TEntityExample s02v001tCreateEntityExample(H2101W01UpdateDto dto, String shncd) {
        // 商品マスタビューの検索条件設定
        S02V001TEntityExample entity = new S02V001TEntityExample();
        entity.createCriteria().andApkihoncdEqualTo(dto.getApkihoncd())
            .andShopcdEqualTo(dto.getShopcd()).andShncdEqualTo(shncd);
        return entity;
    }

    /**
     * 商品データ単位のバリデーションチェック
     * 
     * @param dto H2101W01UpdateDto
     * @param meisaiDto H2101W01UpdateMeisaiDto
     * @param s02v001tEntity S02V001TEntity
     * @param index 行番号
     */
    private void checkShnParam(H2101W01UpdateDto dto, H2101W01UpdateMeisaiDto meisaiDto,
        S02V001TEntity s02v001tEntity, int index) {
        // 処理区分「1:新規」「2:取消」のチェック
        if (dto.isRegistProcess() || dto.isCancelProcess()) {
            FormField shncdField =
                new FormField(String.format("meisaiBeanList[%d].shncdmei", index), "商品コード");
            // セット区分チェック
            if (!CommonConstants.STKBN_0.equals(s02v001tEntity.getStkbn())) {
                throw BusinessExceptionFactory.validationFailed(shncdField, MessageID.W112, "単独商品");
            }
            // 有効チェック
            if (!util.isAvailable(s02v001tEntity.getShopyukokbn())) {
                throw BusinessExceptionFactory.validationFailed(shncdField, MessageID.W123,
                    "商品コード");
            }
            // 販売期間商品チェック
            if (s02v001tEntity.getHanbaistrtdd().compareTo(dto.getNyushukkodd()) > 0
                || s02v001tEntity.getHanbaishrydd().compareTo(dto.getNyushukkodd()) < 0) {
                throw BusinessExceptionFactory.validationFailed(shncdField, MessageID.W130, "販売");
            }
            // レンタル商品チェック
            if (CommonConstants.LCTGRYCD_02.equals(s02v001tEntity.getLctgrycd())
                || CommonConstants.LCTGRYCD_03.equals(s02v001tEntity.getLctgrycd())) {
                throw BusinessExceptionFactory.validationFailed(shncdField, MessageID.W124,
                    "レンタル商品");
            }
            // 数量端数チェック
            if (CommonConstants.SURYOHASUKANRIKBN_0.equals(s02v001tEntity.getSuryohasukanrikbn())) {
                Pattern p = Pattern.compile("^(0|[-]?[1-9]\\d*)$");
                Matcher m = p.matcher(String.valueOf(meisaiDto.getSuryomei()));
                if (!m.matches()) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField(String.format("meisaiBeanList[%d].suryo2mei", index), "数量"),
                        MessageID.W120, "数量", "整数");
                }
            }
        }
    }

    /**
     * ショップマスタPK検索+取得値のDto設定
     * 
     * @param dto H2101W01UpdateDto
     */
    private void s02m046tSetData(H2101W01UpdateDto dto) {
        // 検索条件設定
        S02M046TEntityKey key = new S02M046TEntityKey();
        key.setApkihoncd(dto.getApkihoncd());
        key.setShopcd(dto.getShopcd());

        // ショップマスタPK検索
        S02M046TEntity entity = s02m046tDao.selectByPrimaryKey(key);

        // ショップマスタ検索結果をDtoに設定
        // 在庫管理区分
        dto.setZaikokanrikbn(entity.getZaikokanrikbn());
        // 締日
        dto.setShimedd(entity.getShimedd());
        // 在庫処理年月
        dto.setZaikoprcsdydm(entity.getZaikoprcsdydm());
        // 標準原価端数計算区分
        dto.setHyojungenkahasukeisankbn(entity.getHyojungenkahasukeisankbn());
    }

    /**
     * 入出庫伝票番号採番（採番プロシージャ呼出）
     * 
     * @param dto H2101W01UpdateDto
     */
    private void dnpynoSaiban(H2101W01UpdateDto dto) {
        // 検索条件設定
        Z1804B01Entity entity = new Z1804B01Entity();
        entity.setType(Integer.parseInt(CommonConstants.NEWSLIPNOSIIRE));
        entity.setApkihoncd(dto.getApkihoncd());
        entity.setShopcd(dto.getShopcd());
        entity.setCount(1);

        // プロシージャ実行
        z1804b01Dao.getNewNo(entity);

        // 採番結果をDtoに設定
        dto.setNewdnpyno(entity.getResultno());
    }

    /**
     * 入出庫実績テーブル登録処理
     * 
     * @param dto H2101W01UpdateDto
     */
    private void s11t003tEntry(H2101W01UpdateDto dto) {

        // 伝票区分「03：調整入庫」「04:入庫」「22：倉庫移動」の場合、入庫データを登録
        if (dto.isAdjustReceipt() || dto.isReceipt() || dto.isMovement()) {
            try {
                // 登録処理実施
                s11t003tDao.insert(s11t003tCreateEntryEntity(dto, DATATYPE.RECEIPT));
            } catch (DuplicateKeyException e) {
                // 一意制約違反の場合、エラー
                throw BusinessExceptionFactory.validationFailed(MessageID.W089);
            } catch (RuntimeException re) {
                throw BusinessExceptionFactory.validationFailed(MessageID.E002);
            }
        }

        // 伝票区分「13：調整出庫」「14:出庫」「22：倉庫移動」の場合、出庫データを登録
        if (dto.isAdjustShipment() || dto.isShipment() || dto.isMovement()) {
            try {
                // 登録処理実施
                s11t003tDao.insert(s11t003tCreateEntryEntity(dto, DATATYPE.SHIPMENT));
            } catch (DuplicateKeyException e) {
                // 一意制約違反の場合、エラー
                throw BusinessExceptionFactory.validationFailed(MessageID.W089);
            } catch (RuntimeException re) {
                throw BusinessExceptionFactory.validationFailed(MessageID.E002);
            }
        }
    }

    /**
     * 入出庫実績テーブル登録用データ設定
     * 
     * @param dto H2101W01UpdateDto
     * @param nyushukkoflg 入出庫フラグ(0：入庫データ/1：出庫データ)
     * @return entity S11T003TEntity
     */
    private S11T003TEntity s11t003tCreateEntryEntity(H2101W01UpdateDto dto, DATATYPE datatype) {
        S11T003TEntity entity = new S11T003TEntity();
        // AP基本コード
        entity.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        entity.setShopcd(dto.getShopcd());
        // 入出庫伝票番号
        entity.setNyushukkodnpyno(dto.getNewdnpyno());
        // 入出庫日
        entity.setNyushukkodd(dto.getNyushukkodd());
        // 金額
        // 処理区分「1:新規」の場合、合計金額を、処理区分「2:取消」の場合、合計金額をマイナス表記で設定
        int sign = dto.isRegistProcess() ? 1 : -1;
        entity.setKin(util.getLongValue(dto.getTotalkin()) * sign);
        // 伝票備考
        entity.setDnpybiko(dto.getDnpybiko());
        // データ作成元区分
        entity.setDatsksmotokbn(CommonConstants.KBN_SAKUSEIMOTO_SHIIRENYURYOKU);
        // 有効区分
        entity.setYukokbn(dto.getYukokbn());
        // 登録者AP基本コード
        entity.setToshaapkihoncd(dto.getTnshaapkihoncd());
        // 登録者ショップコード
        entity.setToshashopcd(dto.getTnshashopcd());
        // 登録者コード
        entity.setToshacd(dto.getTnshacd());
        // 登録システム日付
        entity.setInptsystmymd(dto.getSystmymd());
        // 登録システム時間
        entity.setInptsystmjikan(dto.getSystmjikan());
        // 登録運用日付
        entity.setInptunyoymd(dto.getUnyoymd());
        // 登録IPアドレス
        entity.setInptipadrse(dto.getIpadrse());
        // 登録プログラムID
        entity.setInptpgmid(dto.getProgramid());
        // 更新者AP基本コード
        entity.setKoshaapkihoncd(dto.getTnshaapkihoncd());
        // 更新者ショップコード
        entity.setKoshashopcd(dto.getTnshashopcd());
        // 更新者コード
        entity.setKoshacd(dto.getTnshacd());
        // 更新システム日付
        entity.setKshnsystmymd(dto.getSystmymd());
        // 更新システム時間
        entity.setKshnsystmjikan(dto.getSystmjikan());
        // 更新運用日付
        entity.setKshnunyoymd(dto.getUnyoymd());
        // 更新IPアドレス
        entity.setKshnipadrse(dto.getIpadrse());
        // 更新プログラムID
        entity.setKshnpgmid(dto.getProgramid());
        // 入庫データ特有の設定
        if (DATATYPE.RECEIPT == datatype) {
            // 入出庫区分
            entity.setNyushukkokbn(CommonConstants.NYUSHUKKOKBN_1);
            // 取引区分
            entity.setTorihikikbn(CommonConstants.KBN_TORIHIKI_NYUKO);
            // 入庫先倉庫コード
            // 伝票区分「22:倉庫移動」の場合、移動元倉庫コードを設定
            if (dto.isMovement()) {
                entity.setNyukosakisokocd(dto.getIdomotosokocd());
            }
            // 出庫先倉庫コード
            entity.setShukkosakisokocd(dto.getIdosakisokocd());
            // 実績担当者コード
            entity.setJissekitnshacd(dto.getIdosakijissekitnshacd());

            // 出庫データ特有の設定
        } else if (DATATYPE.SHIPMENT == datatype) {
            // 入出庫区分
            entity.setNyushukkokbn(CommonConstants.NYUSHUKKOKBN_2);
            // 取引区分
            entity.setTorihikikbn(CommonConstants.KBN_TORIHIKI_SYUKKO);
            // 入庫先倉庫コード
            entity.setNyukosakisokocd(dto.getIdomotosokocd());
            // 実績担当者コード
            entity.setJissekitnshacd(dto.getIdomotojissekitnshacd());
            // 出庫先倉庫コード
            // 伝票区分「22:倉庫移動」の場合、移動先倉庫コードを設定
            if (dto.isMovement()) {
                entity.setShukkosakisokocd(dto.getIdosakisokocd());
            }
        }

        return entity;
    }

    /**
     * 入出庫実績明細テーブル登録処理
     * 
     * @param dto H2101W01UpdateDto
     * @param meisaiDto H2101W01MeisaiDto
     */
    private void s11t004tEntry(H2101W01UpdateDto dto, H2101W01UpdateMeisaiDto meisaiDto) {

        // 伝票区分「03：調整入庫」「04:入庫」「22：倉庫移動」の場合、入庫データを登録
        if (dto.isAdjustReceipt() || dto.isReceipt() || dto.isMovement()) {
            try {
                // 入庫登録処理実施
                s11t004tDao.insert(s11t004tCreateEntryEntity(dto, meisaiDto, DATATYPE.RECEIPT));
            } catch (DuplicateKeyException e) {
                // 一意制約違反の場合、エラー
                throw BusinessExceptionFactory.validationFailed(MessageID.W089);
            } catch (RuntimeException re) {
                throw BusinessExceptionFactory.validationFailed(MessageID.E002);
            }
        }

        // 伝票区分「13：調整出庫」「14:出庫」「22：倉庫移動」の場合、出庫データを登録
        if (dto.isAdjustShipment() || dto.isShipment() || dto.isMovement()) {
            try {
                // 出庫登録処理実施
                s11t004tDao.insert(s11t004tCreateEntryEntity(dto, meisaiDto, DATATYPE.SHIPMENT));
            } catch (DuplicateKeyException e) {
                // 一意制約違反の場合、エラー
                throw BusinessExceptionFactory.validationFailed(MessageID.W089);
            } catch (RuntimeException re) {
                throw BusinessExceptionFactory.validationFailed(MessageID.E002);
            }
        }
    }

    /**
     * 入出庫実績明細テーブル登録用データ設定
     * 
     * @param dto H2101W01UpdateDto
     * @param meisaiDto H2101W01UpdateMeisaiDto
     * @param nyushukkoflg 入出庫フラグ(0：入庫データ/1：出庫データ)
     * @return entity S11T004TEntity
     */
    private S11T004TEntity s11t004tCreateEntryEntity(H2101W01UpdateDto dto,
        H2101W01UpdateMeisaiDto meisaiDto, DATATYPE datatype) {
        S11T004TEntity entity = new S11T004TEntity();
        // AP基本コード
        entity.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        entity.setShopcd(dto.getShopcd());
        // 入出庫伝票番号
        entity.setNyushukkodnpyno(dto.getNewdnpyno());
        // 入出庫伝票明細番号
        entity.setNyushukkodnpymeisaino(meisaiDto.getNyushukkodnpymeisaino());
        // 入出庫伝票明細枝番号
        entity.setNyushukkodnpymeisaiedano((short) 0);
        // 大分類コード
        entity.setLctgrycd(meisaiDto.getLctgrycd());
        // 中分類コード
        entity.setMctgrycd(meisaiDto.getMctgrycd());
        // 小分類コード
        entity.setSctgrycd(meisaiDto.getSctgrycd());
        // 商品コード
        entity.setShncd(meisaiDto.getShncdmei());
        // 単価
        entity.setUnt(meisaiDto.getUntmei());
        // 明細備考
        entity.setMeisaibiko(meisaiDto.getMeisaibikomei());
        // 型式
        entity.setKatasiki(meisaiDto.getKatasikimei());
        // 在庫更新済フラグ
        entity.setZaikokshnsumiflg(CommonConstants.FLG_ON);
        if (util.isAfter(dto.getNyushukkodd(), dto.getUnyoymd())) {
            // 画面.入出庫日>運用日付の場合は「0」を設定
            entity.setZaikokshnsumiflg(CommonConstants.FLG_OFF);
        }
        // 有効区分
        entity.setYukokbn(dto.getYukokbn());
        // 登録者AP基本コード
        entity.setToshaapkihoncd(dto.getTnshaapkihoncd());
        // 登録者ショップコード
        entity.setToshashopcd(dto.getTnshashopcd());
        // 登録者コード
        entity.setToshacd(dto.getTnshacd());
        // 登録システム日付
        entity.setInptsystmymd(dto.getSystmymd());
        // 登録システム時間
        entity.setInptsystmjikan(dto.getSystmjikan());
        // 登録運用日付
        entity.setInptunyoymd(dto.getUnyoymd());
        // 登録IPアドレス
        entity.setInptipadrse(dto.getIpadrse());
        // 登録プログラムID
        entity.setInptpgmid(dto.getProgramid());
        // 更新者AP基本コード
        entity.setKoshaapkihoncd(dto.getTnshaapkihoncd());
        // 更新者ショップコード
        entity.setKoshashopcd(dto.getTnshashopcd());
        // 更新者コード
        entity.setKoshacd(dto.getTnshacd());
        // 更新システム日付
        entity.setKshnsystmymd(dto.getSystmymd());
        // 更新システム時間
        entity.setKshnsystmjikan(dto.getSystmjikan());
        // 更新運用日付
        entity.setKshnunyoymd(dto.getUnyoymd());
        // 更新IPアドレス
        entity.setKshnipadrse(dto.getIpadrse());
        // 更新プログラムID
        entity.setKshnpgmid(dto.getProgramid());

        // 入庫データ特有の設定
        if (DATATYPE.RECEIPT == datatype) {
            // 入出庫区分
            entity.setNyushukkokbn(CommonConstants.NYUSHUKKOKBN_1);

            // 出庫データ特有の設定
        } else if (DATATYPE.SHIPMENT == datatype) {
            // 入出庫区分
            entity.setNyushukkokbn(CommonConstants.NYUSHUKKOKBN_2);
        }

        // 処理区分「1：新規」特有の設定
        if (dto.isRegistProcess()) {
            // 数量
            entity.setSuryo(meisaiDto.getSuryomei());
            // 金額
            entity.setKin(meisaiDto.getKinmei());

            // 処理区分「2:取消」特有の設定
        } else if (dto.isCancelProcess()) {
            // 数量
            entity.setSuryo(
                !ObjectUtils.isEmpty(meisaiDto.getSuryomei()) ? meisaiDto.getSuryomei().negate()
                    : null);
            // 金額
            entity.setKin(
                !ObjectUtils.isEmpty(meisaiDto.getKinmei()) ? meisaiDto.getKinmei() * -1 : null);
        }

        return entity;
    }

    /**
     * ショップ商品マスタ更新処理
     * 
     * @param dto H2101W01UpdateDto
     * @param meisaiDto H2101W01MeisaiDto
     */
    private void s02m043tUpdate(H2101W01UpdateDto dto, H2101W01UpdateMeisaiDto meisaiDto) {
        // 商品マスタビュー検索条件設定
        S02V001TEntityExample example = new S02V001TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(dto.getApkihoncd())
            .andShopcdEqualTo(dto.getShopcd()).andShncdEqualTo(meisaiDto.getShncdmei())
            .andShopyukokbnEqualTo(CommonConstants.YUKOKBN_1);

        // 商品マスタビュー取得
        // 設計書上は基本商品マスタ、ショップ商品マスタを抽出するが、商品マスタビューで一括取得するのと同じことである
        List<S02V001TEntity> results = s02v001tDao.selectByExample(example);

        // 原価計算区分＝2：移動平均単価の場合、在庫単価を更新する
        if (!ObjectUtils.isEmpty(results)
            && CommonConstants.GENKAKEISANKBN_2.equals(results.get(0).getGenkakeisankbn())) {
            // 現在庫テーブル検索条件設定
            S13T001TEntityKey entityKey = new S13T001TEntityKey();
            entityKey.setApkihoncd(dto.getApkihoncd());
            entityKey.setShopcd(dto.getShopcd());
            entityKey.setShncd(meisaiDto.getShncdmei());
            entityKey.setSokocd(dto.getIdosakisokocd());

            // 現在庫テーブル取得
            S13T001TEntity s13t001tEntity = s13t001tDao.selectByPrimaryKey(entityKey);

            BigDecimal zaikosuryo = ObjectUtils.isEmpty(s13t001tEntity) ? BigDecimal.ZERO
                : s13t001tEntity.getZaikosuryo(); // 現在庫テーブル.在庫数量

            // A : ショップ商品マスタ.在庫単価 × 現在庫テーブル.在庫数量
            // B : 明細.金額
            // C : 現在庫テーブル.在庫数量 + 明細.数量
            // 在庫単価 = (A + B) ÷ C
            BigDecimal sumA = results.get(0).getZaikount().multiply(zaikosuryo);
            BigDecimal sumB = new BigDecimal(
                ObjectUtils.isEmpty(meisaiDto.getKinmei()) ? 0L : meisaiDto.getKinmei());
            BigDecimal sumC = zaikosuryo.add(meisaiDto.getSuryomei());
            BigDecimal zaikount = sumC.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
                : (sumA.add(sumB)).divide(sumC,
                    ItemUtil.getRoundingMode(dto.getHyojungenkahasukeisankbn()));

            // ショップ商品マスタ更新
            s02M043TDao.updateByPrimaryKeySelective(
                s02m043tCreateEntryEntity(results.get(0), dto, zaikount));
        }
    }

    /**
     * ショップ商品マスタ登録用データ設定
     * 
     * @param srcEntity S02V001TEntity
     * @param dto H2101W01UpdateDto
     * @param zaikount 在庫単価
     * @return entity S02M043TEntity
     */
    private S02M043TEntity s02m043tCreateEntryEntity(S02V001TEntity srcEntity,
        H2101W01UpdateDto dto, BigDecimal zaikount) {

        S02M043TEntity entity = new S02M043TEntity();

        entity.setApkihoncd(srcEntity.getApkihoncd()); // ＡＰ基本コード
        entity.setShopcd(srcEntity.getShopcd()); // ショップコード
        // ACCIS2023-6536対応
        // 商品マスタビュー.商品ＡＰ基本コードを設定すれば問題ないはずだが、
        // 設計書に従って商品属性＝0本部の場合、001を設定する
        String shnapkihoncd = CommonConstants.KBN_SHNZOKUSEI_HONBU.equals(srcEntity.getShnzokusei())
            ? CommonConstants.APCD_001
            : srcEntity.getApkihoncd();
        entity.setShnapkihoncd(shnapkihoncd); // 商品ＡＰ基本コード
        entity.setShncd(srcEntity.getShncd());// 商品コード
        entity.setZaikount(zaikount); // 在庫単価
        entity.setKoshaapkihoncd(dto.getTnshaapkihoncd()); // 更新者ＡＰ基本コード
        entity.setKoshashopcd(dto.getTnshashopcd()); // 更新者ショップコード
        entity.setKoshacd(dto.getTnshacd()); // 更新者コード
        entity.setKshnsystmymd(dto.getSystmymd()); // 更新システム日付
        entity.setKshnsystmjikan(dto.getSystmjikan()); // 更新システム時間
        entity.setKshnunyoymd(dto.getUnyoymd()); // 更新運用日付
        entity.setKshnipadrse(dto.getIpadrse()); // 更新ＩＰアドレス
        entity.setKshnpgmid(dto.getProgramid()); // 更新プログラムＩＤ

        return entity;
    }

    /**
     * 現在庫テーブル登録・更新処理
     * 
     * @param dto H2101W01UpdateDto
     * @param meisaiDto H2101W01UpdateMeisaiDto
     * @param nyushukkoflg 入出庫フラグ(0：入庫データ/1：出庫データ)
     */
    private void s13t001tEntryUpdate(H2101W01UpdateDto dto, H2101W01UpdateMeisaiDto meisaiDto,
        DATATYPE datatype) {
        /* 現在庫テーブル情報を取得 */
        // 現在庫テーブル検索条件設定
        S13T001TEntityKey entityKey = new S13T001TEntityKey();
        entityKey.setApkihoncd(dto.getApkihoncd());
        entityKey.setShopcd(dto.getShopcd());
        entityKey.setShncd(meisaiDto.getShncdmei());

        // 入庫データ特有の検索条件設定
        if (DATATYPE.RECEIPT == datatype) {
            entityKey.setSokocd(dto.getIdosakisokocd());

            // 出庫データ特有の検索条件設定
        } else if (DATATYPE.SHIPMENT == datatype) {
            entityKey.setSokocd(dto.getIdomotosokocd());
        }

        // 現在庫テーブルPK検索
        S13T001TEntity s13t001tEntity = s13t001tDao.selectByPrimaryKey(entityKey);

        // 現在庫テーブルの登録情報がない場合、登録処理
        if (ObjectUtils.isEmpty(s13t001tEntity)) {
            try {
                // 登録処理
                s13t001tDao.insert(s13t001tCreateEntryEntity(dto, meisaiDto, datatype));
            } catch (DuplicateKeyException e) {
                // 一意制約違反の場合、エラー
                throw BusinessExceptionFactory.validationFailed(MessageID.W089);
            } catch (RuntimeException re) {
                throw BusinessExceptionFactory.validationFailed(MessageID.E002);
            }

            // 現在庫テーブルの登録情報がある場合、更新処理
        } else {
            // 更新件数
            int result = 0;
            try {
                // 更新処理
                result = s13t001tDao.updateByExampleSelective(
                    s13t001tCreateUpdateSetEntity(dto, meisaiDto, s13t001tEntity, datatype),
                    s13t001tCreateUpdateConditionEntity(s13t001tEntity));
            } catch (RuntimeException e) {
                throw BusinessExceptionFactory.validationFailed(MessageID.E002);
            }

            if (result == 0) {
                // 更新件数が0(排他制御)の場合、エラー
                throw BusinessExceptionFactory.validationFailed(MessageID.W090);
            }
        }
    }

    /**
     * 現在庫テーブル登録用データ設定
     * 
     * @param dto H2101W01UpdateDto
     * @param meisaiDto H2101W01UpdateMeisaiDto
     * @param nyushukkoflg 入出庫フラグ(0：入庫データ/1：出庫データ)
     * @return entity S13T001TEntity
     */
    private S13T001TEntity s13t001tCreateEntryEntity(H2101W01UpdateDto dto,
        H2101W01UpdateMeisaiDto meisaiDto, DATATYPE datatype) {
        S13T001TEntity entity = new S13T001TEntity();

        // AP基本コード
        entity.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        entity.setShopcd(dto.getShopcd());
        // 商品コード
        entity.setShncd(meisaiDto.getShncdmei());
        // 有効区分
        entity.setYukokbn(dto.getYukokbn());
        // 登録者AP基本コード
        entity.setToshaapkihoncd(dto.getTnshaapkihoncd());
        // 登録者ショップコード
        entity.setToshashopcd(dto.getTnshashopcd());
        // 登録者コード
        entity.setToshacd(dto.getTnshacd());
        // 登録システム日付
        entity.setInptsystmymd(dto.getSystmymd());
        // 登録システム時間
        entity.setInptsystmjikan(dto.getSystmjikan());
        // 登録運用日付
        entity.setInptunyoymd(dto.getUnyoymd());
        // 登録IPアドレス
        entity.setInptipadrse(dto.getIpadrse());
        // 登録プログラムID
        entity.setInptpgmid(dto.getProgramid());
        // 更新者AP基本コード
        entity.setKoshaapkihoncd(dto.getTnshaapkihoncd());
        // 更新者ショップコード
        entity.setKoshashopcd(dto.getTnshashopcd());
        // 更新者コード
        entity.setKoshacd(dto.getTnshacd());
        // 更新システム日付
        entity.setKshnsystmymd(dto.getSystmymd());
        // 更新システム時間
        entity.setKshnsystmjikan(dto.getSystmjikan());
        // 更新運用日付
        entity.setKshnunyoymd(dto.getUnyoymd());
        // 更新IPアドレス
        entity.setKshnipadrse(dto.getIpadrse());
        // 更新プログラムID
        entity.setKshnpgmid(dto.getProgramid());

        // 入庫データ特有の設定
        if (DATATYPE.RECEIPT == datatype) {
            // 倉庫コード
            entity.setSokocd(dto.getIdosakisokocd());
            // 在庫数量
            entity.setZaikosuryo(meisaiDto.getSuryomei());

            // 出庫データ特有の設定
        } else if (DATATYPE.SHIPMENT == datatype) {
            // 倉庫コード
            entity.setSokocd(dto.getIdomotosokocd());
            // 在庫数量
            entity.setZaikosuryo(meisaiDto.getSuryomei().negate());
        }

        return entity;
    }

    /**
     * 現在庫テーブル更新用データ設定
     * 
     * @param dto H2101W01UpdateDto
     * @param meisaiDto H2101W01UpdateMeisaiDto
     * @param resultEntity S13T001TEntity(検索結果)
     * @param nyushukkoflg 入出庫フラグ(0：入庫データ/1：出庫データ)
     * @return entity S13T001TEntity
     */
    private S13T001TEntity s13t001tCreateUpdateSetEntity(H2101W01UpdateDto dto,
        H2101W01UpdateMeisaiDto meisaiDto, S13T001TEntity resultEntity, DATATYPE datatype) {
        S13T001TEntity entity = new S13T001TEntity();

        // 更新内容設定
        // 処理区分「1:新規」+入庫レコード、もしくは処理区分「2:取消」+出庫レコードの場合
        if ((dto.isRegistProcess() && DATATYPE.RECEIPT == datatype)
            || dto.isCancelProcess() && DATATYPE.SHIPMENT == datatype) {
            // 在庫数量
            entity.setZaikosuryo(resultEntity.getZaikosuryo().add(meisaiDto.getSuryomei()));

            // 処理区分「1:新規」+出庫レコード、もしくは処理区分「2:取消」+入庫レコードの場合
        } else if ((dto.isRegistProcess() && DATATYPE.SHIPMENT == datatype)
            || dto.isCancelProcess() && DATATYPE.RECEIPT == datatype) {
            // 在庫数量
            entity.setZaikosuryo(resultEntity.getZaikosuryo()
                .add(meisaiDto.getSuryomei().multiply(BigDecimal.valueOf(-1))));
        }

        // 更新者AP基本コード
        entity.setKoshaapkihoncd(dto.getTnshaapkihoncd());
        // 更新者ショップコード
        entity.setKoshashopcd(dto.getTnshashopcd());
        // 更新者コード
        entity.setKoshacd(dto.getTnshacd());
        // 更新システム日付
        entity.setKshnsystmymd(dto.getSystmymd());
        // 更新システム時間
        entity.setKshnsystmjikan(dto.getSystmjikan());
        // 更新運用日付
        entity.setKshnunyoymd(dto.getUnyoymd());
        // 更新IPアドレス
        entity.setKshnipadrse(dto.getIpadrse());
        // 更新プログラムID
        entity.setKshnpgmid(dto.getProgramid());

        return entity;
    }

    /**
     * 現在庫テーブル更新条件設定
     * 
     * @param entity S13T001TEntity(検索結果)
     * @return entityExample S13T001TEntityExample
     */
    private S13T001TEntityExample s13t001tCreateUpdateConditionEntity(S13T001TEntity entity) {
        // 更新条件設定
        S13T001TEntityExample entityExample = new S13T001TEntityExample();
        entityExample.createCriteria().andApkihoncdEqualTo(entity.getApkihoncd())
            .andShopcdEqualTo(entity.getShopcd()).andShncdEqualTo(entity.getShncd())
            .andSokocdEqualTo(entity.getSokocd()).andKshnsystmymdEqualTo(entity.getKshnsystmymd())
            .andKshnsystmjikanEqualTo(entity.getKshnsystmjikan());
        // 呼出元に更新条件をリターン
        return entityExample;
    }

    /**
     * 在庫テーブル登録・更新処理
     * 
     * @param dto H2101W01UpdateDto
     * @param meisaiDto H2101W01UpdateMeisaiDto
     * @param nyushukkoflg 入出庫フラグ(0：入庫データ/1：出庫データ)
     */
    private void s13t002tEntryUpdate(H2101W01UpdateDto dto, H2101W01UpdateMeisaiDto meisaiDto,
        DATATYPE datatype) {
        /* 在庫テーブル情報を取得 */
        // 在庫テーブル検索条件設定
        S13T002TEntityKey entityKey = new S13T002TEntityKey();
        entityKey.setApkihoncd(dto.getApkihoncd());
        entityKey.setShopcd(dto.getShopcd());
        entityKey.setShncd(meisaiDto.getShncdmei());

        // 入庫データ特有の検索条件設定
        if (DATATYPE.RECEIPT == datatype) {
            entityKey.setSokocd(dto.getIdosakisokocd());

            // 出庫データ特有の検索条件設定
        } else if (DATATYPE.SHIPMENT == datatype) {
            entityKey.setSokocd(dto.getIdomotosokocd());
        }

        // 入出庫日の設定
        LocalDate nyushukkoDate = util.getLocalDate(dto.getNyushukkodd());
        // 入出庫日>締日の場合、入出庫年月を次月に変更
        String nyushukkodydm = nyushukkoDate.getDayOfMonth() > Integer.parseInt(dto.getShimedd())
            ? nyushukkoDate.plusMonths(1).format(FORMATTER_YYYYMM)
            : nyushukkoDate.format(FORMATTER_YYYYMM);
        log.debug("*** 入出庫年月 : {}", nyushukkodydm);

        // 入出庫年月<=在庫処理年月の場合、検索条件に在庫処理年月を設定
        if (nyushukkodydm.compareTo(dto.getZaikoprcsdydm()) <= 0) {
            entityKey.setShimedydm(dto.getZaikoprcsdydm());
            dto.setShimedydm(dto.getZaikoprcsdydm());

            // 上記以外の場合、検索条件に入出庫年月を設定
        } else {
            entityKey.setShimedydm(nyushukkodydm);
            dto.setShimedydm(nyushukkodydm);
        }

        // 在庫テーブルPK検索
        S13T002TEntity s13t002tEntity = s13t002tDao.selectByPrimaryKey(entityKey);

        // 在庫テーブルの登録情報がない場合、登録処理
        if (ObjectUtils.isEmpty(s13t002tEntity)) {
            try {
                // 登録処理
                s13t002tDao.insert(s13t002tCreateInsertEntity(dto, meisaiDto, datatype));
            } catch (DuplicateKeyException e) {
                // 一意制約違反の場合、エラー
                throw BusinessExceptionFactory.validationFailed(MessageID.W089);
            } catch (RuntimeException re) {
                throw BusinessExceptionFactory.validationFailed(MessageID.E002);
            }

            // 在庫テーブルの登録情報がある場合、更新処理
        } else {
            // 更新件数
            int result = 0;
            try {
                // 更新処理
                result = s13t002tDao.updateByExampleSelective(
                    s13t002tCreateUpdateSetEntity(dto, meisaiDto, s13t002tEntity, datatype),
                    s13t002tCreateUpdateConditionEntity(dto, s13t002tEntity, datatype));
            } catch (RuntimeException e) {
                throw BusinessExceptionFactory.validationFailed(MessageID.E002);
            }

            if (result == 0) {
                // 更新件数が0(排他制御)の場合、エラー
                throw BusinessExceptionFactory.validationFailed(MessageID.W090);
            }
        }
    }

    /**
     * 在庫テーブル登録用データ設定
     * 
     * @param dto H2101W01UpdateDto
     * @param meisaiDto H2101W01UpdateMeisaiDto
     * @param nyushukkoflg 入出庫フラグ(0：入庫データ/1：出庫データ)
     * @return entity S13T002TEntity
     */
    private S13T002TEntity s13t002tCreateInsertEntity(H2101W01UpdateDto dto,
        H2101W01UpdateMeisaiDto meisaiDto, DATATYPE datatype) {
        S13T002TEntity entity = new S13T002TEntity();

        // AP基本コード
        entity.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        entity.setShopcd(dto.getShopcd());
        // 商品コード
        entity.setShncd(meisaiDto.getShncdmei());
        // 締年月
        entity.setShimedydm(dto.getShimedydm());
        // 前月末在庫数
        entity.setMaedmmatsuzaikokazu(BigDecimal.ZERO);
        // 当月仕入数（必要に応じて後で上書き）
        entity.setTotsukisiirekazu(BigDecimal.ZERO);
        // 当月仕入金額（必要に応じて後で上書き）
        entity.setTotsukisiirekin(0L);
        // 当月製造数
        entity.setTotsukiseizokazu(BigDecimal.ZERO);
        // 当月調整入庫数（必要に応じて後で上書き）
        entity.setTotsukichoseinyukokazu(BigDecimal.ZERO);
        // 当月その他入庫数
        entity.setTotsukisonotanyukokazu(BigDecimal.ZERO);
        // 当月売上数（必要に応じて後で上書き）
        entity.setTotsukiurikazu(BigDecimal.ZERO);
        // 当月調整出庫数（必要に応じて後で上書き）
        entity.setTotsukichoseishukkokazu(BigDecimal.ZERO);
        // 当月経費出庫数
        entity.setTotsukikeihishukkokazu(BigDecimal.ZERO);
        // 当月その他出庫数
        entity.setTotsukisonotashukkokazu(BigDecimal.ZERO);
        // 当月棚卸数量
        entity.setTotsukitnorssuryo(null);
        // 当月過不足数量
        entity.setTotsukikfsksuryo(null);
        // 在庫単価
        entity.setZaikount(BigDecimal.ZERO);
        // 締フラグ
        entity.setShimeflg(CommonConstants.KBN_SHIMEFLG_SHIMEMAE);
        // 有効区分
        entity.setYukokbn(dto.getYukokbn());
        // 登録者AP基本コード
        entity.setToshaapkihoncd(dto.getTnshaapkihoncd());
        // 登録者ショップコード
        entity.setToshashopcd(dto.getTnshashopcd());
        // 登録者コード
        entity.setToshacd(dto.getTnshacd());
        // 登録システム日付
        entity.setInptsystmymd(dto.getSystmymd());
        // 登録システム時間
        entity.setInptsystmjikan(dto.getSystmjikan());
        // 登録運用日付
        entity.setInptunyoymd(dto.getUnyoymd());
        // 登録IPアドレス
        entity.setInptipadrse(dto.getIpadrse());
        // 登録プログラムID
        entity.setInptpgmid(dto.getProgramid());
        // 更新者AP基本コード
        entity.setKoshaapkihoncd(dto.getTnshaapkihoncd());
        // 更新者ショップコード
        entity.setKoshashopcd(dto.getTnshashopcd());
        // 更新者コード
        entity.setKoshacd(dto.getTnshacd());
        // 更新システム日付
        entity.setKshnsystmymd(dto.getSystmymd());
        // 更新システム時間
        entity.setKshnsystmjikan(dto.getSystmjikan());
        // 更新運用日付
        entity.setKshnunyoymd(dto.getUnyoymd());
        // 更新IPアドレス
        entity.setKshnipadrse(dto.getIpadrse());
        // 更新プログラムID
        entity.setKshnpgmid(dto.getProgramid());

        // 入庫データ特有の設定
        if (DATATYPE.RECEIPT == datatype) {
            // 倉庫コード（移動先）
            entity.setSokocd(dto.getIdosakisokocd());
            // 「04:入庫」「22:倉庫移動」の場合のみ
            if (dto.isReceipt() || dto.isMovement()) {
                // 当月仕入数
                entity.setTotsukisiirekazu(meisaiDto.getSuryomei());
            }
            // 「04:入庫」の場合のみ
            if (dto.isReceipt()) {
                // 当月仕入金額
                entity.setTotsukisiirekin(meisaiDto.getKinmei());
            }
            // 当月調整入庫数（「13:調整入庫」の場合のみ）
            if (dto.isAdjustReceipt()) {
                // 当月調整入庫数
                entity.setTotsukichoseinyukokazu(meisaiDto.getSuryomei());
            }
            // 当月末在庫数
            entity.setTotsukimatsuzaikokazu(meisaiDto.getSuryomei());

            // 出庫データ特有の設定
        } else if (DATATYPE.SHIPMENT == datatype) {
            // 倉庫コード（移動元）
            entity.setSokocd(dto.getIdomotosokocd());
            // 当月仕入数
            entity.setTotsukisiirekazu(BigDecimal.ZERO);
            // 当月仕入金額
            entity.setTotsukisiirekin(0L);
            // 当月調整入庫数
            entity.setTotsukichoseinyukokazu(BigDecimal.ZERO);
            // 当月売上数（「14:出庫」「22:倉庫移動」の場合のみ）
            if (dto.isShipment() || dto.isMovement()) {
                // 当月売上数
                entity.setTotsukiurikazu(meisaiDto.getSuryomei());
            }
            // 当月調整出庫数（「13:調整出庫」の場合のみ）
            if (dto.isAdjustShipment()) {
                // 当月調整出庫数
                entity.setTotsukichoseishukkokazu(meisaiDto.getSuryomei());
            }
            // 当月末在庫数
            entity.setTotsukimatsuzaikokazu(meisaiDto.getSuryomei().negate());
        }

        return entity;
    }

    /**
     * 在庫テーブル更新用データ設定
     * 
     * @param dto H2101W01UpdateDto
     * @param meisaiDto H2101W01UpdateMeisaiDto
     * @param resultEntity S13T002TEntity(検索結果)
     * @param nyushukkoflg 入出庫フラグ(0：入庫データ/1：出庫データ)
     * @return entity S13T002TEntity
     */
    private S13T002TEntity s13t002tCreateUpdateSetEntity(H2101W01UpdateDto dto,
        H2101W01UpdateMeisaiDto meisaiDto, S13T002TEntity resultEntity, DATATYPE datatype) {
        S13T002TEntity entity = new S13T002TEntity();

        // 当月製造数
        entity.setTotsukiseizokazu(resultEntity.getTotsukiseizokazu().add(BigDecimal.ZERO));
        // 当月その他入庫数
        entity.setTotsukisonotanyukokazu(
            resultEntity.getTotsukisonotanyukokazu().add(BigDecimal.ZERO));
        // 当月経費出庫数
        entity.setTotsukikeihishukkokazu(
            resultEntity.getTotsukikeihishukkokazu().add(BigDecimal.ZERO));
        // 当月その他出庫数
        entity.setTotsukisonotashukkokazu(
            resultEntity.getTotsukisonotashukkokazu().add(BigDecimal.ZERO));
        // 更新者AP基本コード
        entity.setKoshaapkihoncd(dto.getTnshaapkihoncd());
        // 更新者ショップコード
        entity.setKoshashopcd(dto.getTnshashopcd());
        // 更新者コード
        entity.setKoshacd(dto.getTnshacd());
        // 更新システム日付
        entity.setKshnsystmymd(dto.getSystmymd());
        // 更新システム時間
        entity.setKshnsystmjikan(dto.getSystmjikan());
        // 更新運用日付
        entity.setKshnunyoymd(dto.getUnyoymd());
        // 更新IPアドレス
        entity.setKshnipadrse(dto.getIpadrse());
        // 更新プログラムID
        entity.setKshnpgmid(dto.getProgramid());
        // 処理区分「1:新規」特有の設定
        if (dto.isRegistProcess()) {
            // 入庫データ特有の設定
            if (DATATYPE.RECEIPT == datatype) {
                // 「04:入庫」「22:倉庫移動」の場合のみ
                if (dto.isReceipt() || dto.isMovement()) {
                    // 当月仕入数
                    entity.setTotsukisiirekazu(
                        resultEntity.getTotsukisiirekazu().add(meisaiDto.getSuryomei()));
                }
                // 「04:入庫」の場合のみ
                if (dto.isReceipt()) {
                    // 当月仕入金額
                    entity.setTotsukisiirekin(
                        resultEntity.getTotsukisiirekin() + Optional.ofNullable(meisaiDto.getKinmei()).orElse(0L));
                }
                // 当月調整入庫数（「13:調整入庫」の場合のみ）
                if (dto.isAdjustReceipt()) {
                    // 当月調整入庫数
                    entity.setTotsukichoseinyukokazu(
                        resultEntity.getTotsukichoseinyukokazu().add(meisaiDto.getSuryomei()));
                }
                // 当月末在庫数
                entity.setTotsukimatsuzaikokazu(
                    resultEntity.getTotsukimatsuzaikokazu().add(meisaiDto.getSuryomei()));

                // 出庫データ特有の設定
            } else if (DATATYPE.SHIPMENT == datatype) {
                // 当月売上数（「14:出庫」「22:倉庫移動」の場合のみ）
                if (dto.isShipment() || dto.isMovement()) {
                    entity.setTotsukiurikazu(
                        resultEntity.getTotsukiurikazu().add(meisaiDto.getSuryomei()));
                }
                // 当月調整出庫数（「13:調整出庫」の場合のみ）
                if (dto.isAdjustShipment()) {
                    entity.setTotsukichoseishukkokazu(
                        resultEntity.getTotsukichoseishukkokazu().add(meisaiDto.getSuryomei()));
                }
                // 当月末在庫数
                entity.setTotsukimatsuzaikokazu(
                    resultEntity.getTotsukimatsuzaikokazu().add(meisaiDto.getSuryomei().negate()));
            }
            // 処理区分「2:取消」特有の設定
        } else if (dto.isCancelProcess()) {
            // 入庫データ特有の設定
            if (DATATYPE.RECEIPT == datatype) {
                // 「04:入庫」「22:倉庫移動」の場合のみ
                if (dto.isReceipt() || dto.isMovement()) {
                    // 当月仕入数
                    entity.setTotsukisiirekazu(
                        resultEntity.getTotsukisiirekazu().add(meisaiDto.getSuryomei().negate()));
                }
                // 「04:入庫」の場合のみ
                if (dto.isReceipt() || dto.isMovement()) {
                    // 当月仕入金額
                    entity.setTotsukisiirekin(
                        resultEntity.getTotsukisiirekin() + (Optional.ofNullable(meisaiDto.getKinmei()).orElse(0L) * -1));
                }
                // 当月調整入庫数（「13:調整入庫」の場合のみ）
                if (dto.isAdjustReceipt()) {
                    // 当月調整入庫数
                    entity.setTotsukichoseinyukokazu(resultEntity.getTotsukichoseinyukokazu()
                        .add(meisaiDto.getSuryomei().negate()));
                }
                // 当月末在庫数
                entity.setTotsukimatsuzaikokazu(
                    resultEntity.getTotsukimatsuzaikokazu().add(meisaiDto.getSuryomei().negate()));

                // 出庫データ特有の設定
            } else if (DATATYPE.SHIPMENT == datatype) {
                // 当月売上数（「14:出庫」「22:倉庫移動」の場合のみ）
                if (dto.isShipment() || dto.isMovement()) {
                    entity.setTotsukiurikazu(
                        resultEntity.getTotsukiurikazu().add(meisaiDto.getSuryomei().negate()));
                }
                // 当月調整出庫数（「13:調整出庫」の場合のみ）
                if (dto.isAdjustShipment()) {
                    entity.setTotsukichoseishukkokazu(resultEntity.getTotsukichoseishukkokazu()
                        .add(meisaiDto.getSuryomei().negate()));
                }
                // 当月末在庫数
                entity.setTotsukimatsuzaikokazu(
                    resultEntity.getTotsukimatsuzaikokazu().add(meisaiDto.getSuryomei()));
            }
        }

        return entity;
    }

    /**
     * 在庫テーブル更新条件設定
     * 
     * @param dto H2101W01UpdateDto
     * @param entity S13T002TEntity(検索結果)
     * @param nyushukkoflg 入出庫フラグ(0：入庫データ/1：出庫データ)
     * @return entityExample S13T002TEntityExample
     */
    private S13T002TEntityExample s13t002tCreateUpdateConditionEntity(H2101W01UpdateDto dto,
        S13T002TEntity entity, DATATYPE datatype) {

        // 更新条件設定
        S13T002TEntityExample entityExample = new S13T002TEntityExample();

        S13T002TEntityExample.Criteria criteria = entityExample.createCriteria()
            .andApkihoncdEqualTo(entity.getApkihoncd()).andShopcdEqualTo(entity.getShopcd())
            .andShncdEqualTo(entity.getShncd()).andShimedydmEqualTo(dto.getShimedydm())
            .andKshnsystmymdEqualTo(entity.getKshnsystmymd())
            .andKshnsystmjikanEqualTo(entity.getKshnsystmjikan());

        // 入庫データ特有の検索条件設定
        if (DATATYPE.RECEIPT == datatype) {
            criteria.andSokocdEqualTo(dto.getIdosakisokocd());

            // 出庫データ特有の検索条件設定
        } else if (DATATYPE.SHIPMENT == datatype) {
            criteria.andSokocdEqualTo(dto.getIdomotosokocd());
        }

        // 呼出元に更新条件をリターン
        return entityExample;
    }

    /**
     * 在庫入出庫明細テーブル登録処理
     * 
     * @param dto H2101W01UpdateDto
     * @param meisaiDto H2101W01UpdateMeisaiDto
     * @param nyushukkoflg 入出庫フラグ(0：入庫データ/1：出庫データ)
     * @param renban 連番
     */
    private void s13t003tEntry(H2101W01UpdateDto dto, H2101W01UpdateMeisaiDto meisaiDto,
        DATATYPE datatype, short renban) {
        try {
            // 登録処理
            s13t003tDao.insert(s13t003tCreateEntrySetEntity(dto, meisaiDto, datatype, renban));
        } catch (DuplicateKeyException e) {
            // 一意制約違反の場合、エラー
            throw BusinessExceptionFactory.validationFailed(MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(MessageID.E002);
        }
    }

    /**
     * 在庫入出庫明細テーブル登録用データ設定
     * 
     * @param dto H2101W01UpdateDto
     * @param meisaiDto H2101W01UpdateMeisaiDto
     * @param nyushukkoflg 入出庫フラグ(0：入庫データ/1：出庫データ)
     * @param renban 連番
     * @return entity S13T003TEntity
     */
    private S13T003TEntity s13t003tCreateEntrySetEntity(H2101W01UpdateDto dto,
        H2101W01UpdateMeisaiDto meisaiDto, DATATYPE datatype, short renban) {
        S13T003TEntity entity = new S13T003TEntity();

        // AP基本コード
        entity.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        entity.setShopcd(dto.getShopcd());
        // 商品コード
        entity.setShncd(meisaiDto.getShncdmei());
        // 締年月
        entity.setShimedydm(dto.getShimedydm());
        // 入出庫日
        entity.setNyushukkodd(dto.getNyushukkodd());
        // 伝票番号
        entity.setDnpyno(dto.getNewdnpyno());
        // 枝番号
        entity.setEdano((short) 0);
        // 伝票行番号
        entity.setDnpygyono(meisaiDto.getMeisaigyono());
        // 連番
        entity.setRenban(renban);
        // 伝票区分
        entity.setDnpykbn(dto.getDnpykbn());
        // データ作成元区分
        entity.setDatsksmotokbn(CommonConstants.SETTEICD_093_12);
        // 有効区分
        entity.setYukokbn(dto.getYukokbn());
        // 登録者AP基本コード
        entity.setToshaapkihoncd(dto.getTnshaapkihoncd());
        // 登録者ショップコード
        entity.setToshashopcd(dto.getTnshashopcd());
        // 登録者コード
        entity.setToshacd(dto.getTnshacd());
        // 登録システム日付
        entity.setInptsystmymd(dto.getSystmymd());
        // 登録システム時間
        entity.setInptsystmjikan(dto.getSystmjikan());
        // 登録運用日付
        entity.setInptunyoymd(dto.getUnyoymd());
        // 登録IPアドレス
        entity.setInptipadrse(dto.getIpadrse());
        // 登録プログラムID
        entity.setInptpgmid(dto.getProgramid());
        // 更新者AP基本コード
        entity.setKoshaapkihoncd(dto.getTnshaapkihoncd());
        // 更新者ショップコード
        entity.setKoshashopcd(dto.getTnshashopcd());
        // 更新者コード
        entity.setKoshacd(dto.getTnshacd());
        // 更新システム日付
        entity.setKshnsystmymd(dto.getSystmymd());
        // 更新システム時間
        entity.setKshnsystmjikan(dto.getSystmjikan());
        // 更新運用日付
        entity.setKshnunyoymd(dto.getUnyoymd());
        // 更新IPアドレス
        entity.setKshnipadrse(dto.getIpadrse());
        // 更新プログラムID
        entity.setKshnpgmid(dto.getProgramid());

        // 伝票区分「22:倉庫移動」特有の設定
        if (dto.isMovement()) {
            // 入出庫種別
            entity.setNyushukkokind(CommonConstants.SETTEICD_105_04);
            // 入出庫先AP基本コード
            entity.setNyushukkosakiapkihoncd(dto.getApkihoncd());
            // 入出庫先ショップコード
            entity.setNyushukkosakishopcd(dto.getShopcd());
            // 入庫データ特有の設定
            if (DATATYPE.RECEIPT == datatype) {
                // 入出庫先倉庫コード
                entity.setNyushukkosakisokocd(dto.getIdomotosokocd());

                // 出庫データ特有の設定
            } else if (DATATYPE.SHIPMENT == datatype) {
                // 入出庫先倉庫コード
                entity.setNyushukkosakisokocd(dto.getIdosakisokocd());
            }

            // 上記以外
        } else {
            // 入出庫種別
            entity.setNyushukkokind(CommonConstants.SETTEICD_105_00);
        }

        // 入庫データ特有の設定
        if (DATATYPE.RECEIPT == datatype) {
            // 倉庫コード
            entity.setSokocd(dto.getIdosakisokocd());
            // 入出庫区分
            entity.setNyushukkokbn(CommonConstants.NYUSHUKKOKBN_1);
            // 出庫数量
            entity.setShukkosuryo(BigDecimal.ZERO);

            // 処理区分「1:新規」特有の設定
            if (dto.isRegistProcess()) {
                // 入庫数量
                entity.setNyukosuryo(meisaiDto.getSuryomei());

                // 処理区分「2:取消」特有の設定
            } else if (dto.isCancelProcess()) {
                // 入庫数量
                entity.setNyukosuryo(meisaiDto.getSuryomei().negate());
            }

            // 出庫データ特有の設定
        } else if (DATATYPE.SHIPMENT == datatype) {
            // 倉庫コード
            entity.setSokocd(dto.getIdomotosokocd());
            // 入出庫区分
            entity.setNyushukkokbn(CommonConstants.NYUSHUKKOKBN_2);
            // 入庫数量
            entity.setNyukosuryo(BigDecimal.ZERO);

            // 処理区分「1:新規」特有の設定
            if (dto.isRegistProcess()) {
                // 出庫数量
                entity.setShukkosuryo(meisaiDto.getSuryomei());

                // 処理区分「2:取消」特有の設定
            } else if (dto.isCancelProcess()) {
                // 出庫数量
                entity.setShukkosuryo(meisaiDto.getSuryomei().negate());
            }
        }

        return entity;
    }

    /**
     * 入出庫仕入制御テーブル登録・更新処理
     * 
     * @param dto H2101W01UpdateDto
     * @param size 明細行の件数
     */
    private void s11t005tEntryUpdate(H2101W01UpdateDto dto, int size) {

        // 更新件数
        int result = 0;

        try {
            // 処理区分「2:取消」の場合、登録・更新処理を実施
            if (dto.isCancelProcess()) {
                // 検索条件設定
                S11T005TEntityKey entityKey = new S11T005TEntityKey();
                entityKey.setApkihoncd(dto.getApkihoncd());
                entityKey.setShopcd(dto.getShopcd());
                entityKey.setDnpyno(dto.getNyushukkodnpyno());
                // 検索処理実施
                S11T005TEntity s11t005tEntity = s11t005tDao.selectByPrimaryKey(entityKey);

                // 検索結果が存在しない場合、登録処理を実施
                if (ObjectUtils.isEmpty(s11t005tEntity)) {
                    // 登録処理
                    s11t005tDao.insert(s11t005tCreateEntrySetEntity(dto, size));

                    // 検索結果が存在する場合、更新処理を実施
                } else {
                    // 更新処理
                    result =
                        s11t005tDao.updateByExampleSelective(s11t005tCreateUpdateSetEntity(dto),
                            s11t005tCreateUpdateConditionEntity(s11t005tEntity));
                    if (result == 0) {
                        // 更新件数が0(排他制御)の場合、エラー
                        throw BusinessExceptionFactory.validationFailed(MessageID.W090);
                    }
                }
                // 上記以外の場合、登録処理を実施
            } else {
                // 登録処理
                s11t005tDao.insert(s11t005tCreateEntrySetEntity(dto, size));
            }
        } catch (DuplicateKeyException e) {
            // 一意制約違反の場合、エラー
            throw BusinessExceptionFactory.validationFailed(MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(MessageID.E002);
        }
    }

    /**
     * 入出庫仕入制御テーブル登録用データ設定
     * 
     * @param dto H2101W01UpdateDto
     * @param size 明細件数
     * @return entity S11T005TEntity
     */
    private S11T005TEntity s11t005tCreateEntrySetEntity(H2101W01UpdateDto dto, int size) {
        S11T005TEntity entity = new S11T005TEntity();
        // AP基本コード
        entity.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        entity.setShopcd(dto.getShopcd());
        // 伝票番号
        entity.setDnpyno(dto.getNewdnpyno());
        // 伝票区分
        entity.setDnpykbn(dto.getDnpykbn());
        // 仕入データ作成区分
        entity.setSiiredatskskbn(CommonConstants.KBN_DATASAKUSEIKBN_MISAKUSEI);
        // 明細件数
        entity.setMeisaicnt((short) size);
        // 仕入日
        entity.setSiiredd(dto.getNyushukkodd());
        // 伝票備考
        entity.setDnpybiko(dto.getDnpybiko());
        // 有効区分
        entity.setYukokbn(dto.getYukokbn());
        // 登録者AP基本コード
        entity.setToshaapkihoncd(dto.getTnshaapkihoncd());
        // 登録者ショップコード
        entity.setToshashopcd(dto.getTnshashopcd());
        // 登録者コード
        entity.setToshacd(dto.getTnshacd());
        // 登録システム日付
        entity.setInptsystmymd(dto.getSystmymd());
        // 登録システム時間
        entity.setInptsystmjikan(dto.getSystmjikan());
        // 登録運用日付
        entity.setInptunyoymd(dto.getUnyoymd());
        // 登録IPアドレス
        entity.setInptipadrse(dto.getIpadrse());
        // 登録プログラムID
        entity.setInptpgmid(dto.getProgramid());
        // 更新者AP基本コード
        entity.setKoshaapkihoncd(dto.getTnshaapkihoncd());
        // 更新者ショップコード
        entity.setKoshashopcd(dto.getTnshashopcd());
        // 更新者コード
        entity.setKoshacd(dto.getTnshacd());
        // 更新システム日付
        entity.setKshnsystmymd(dto.getSystmymd());
        // 更新システム時間
        entity.setKshnsystmjikan(dto.getSystmjikan());
        // 更新運用日付
        entity.setKshnunyoymd(dto.getUnyoymd());
        // 更新IPアドレス
        entity.setKshnipadrse(dto.getIpadrse());
        // 更新プログラムID
        entity.setKshnpgmid(dto.getProgramid());

        // 伝票区分「03:調整入庫」「04:入庫」特有の設定
        if (dto.isAdjustReceipt() || dto.isReceipt()) {
            // 出荷先取引区分
            entity.setShukkasakitorihikikbn(CommonConstants.KBN_TORIHIKI_NYUKO);
            // 入庫データ作成区分
            entity.setNyukodatskskbn(CommonConstants.KBN_DATASAKUSEIKBN_MISAKUSEI);
            // 出庫データ作成区分
            entity.setShukkodatskskbn(CommonConstants.KBN_DATASAKUSEIKBN_SAKUSEI);
            // 出荷先コード
            entity.setShukkasakicd(dto.getIdosakisokocd());
            // 出荷先実績担当者コード
            entity.setShukkasakijissekitnshacd(dto.getIdosakijissekitnshacd());

            // 伝票区分「13:調整出庫」「14:出庫」特有の設定
        } else if (dto.isAdjustShipment() || dto.isShipment()) {
            // 入荷元取引区分
            entity.setNyukamototorihikikbn(CommonConstants.KBN_TORIHIKI_SYUKKO);
            // 入庫データ作成区分
            entity.setNyukodatskskbn(CommonConstants.KBN_DATASAKUSEIKBN_SAKUSEI);
            // 出庫データ作成区分
            entity.setShukkodatskskbn(CommonConstants.KBN_DATASAKUSEIKBN_MISAKUSEI);
            // 入荷元コード
            entity.setNyukamotocd(dto.getIdomotosokocd());
            // 入荷元実績担当者コード
            entity.setNyukamotojissekitnshacd(dto.getIdomotojissekitnshacd());

            // 伝票区分「22:倉庫移動」特有の設定
        } else if (dto.isMovement()) {
            // 入荷元取引区分
            entity.setNyukamototorihikikbn(CommonConstants.KBN_TORIHIKI_SYUKKO);
            // 出荷先取引区分
            entity.setShukkasakitorihikikbn(CommonConstants.KBN_TORIHIKI_NYUKO);
            // 入庫データ作成区分
            entity.setNyukodatskskbn(CommonConstants.KBN_DATASAKUSEIKBN_MISAKUSEI);
            // 出庫データ作成区分
            entity.setShukkodatskskbn(CommonConstants.KBN_DATASAKUSEIKBN_SAKUSEI);
            // 入荷元コード
            entity.setNyukamotocd(dto.getIdomotosokocd());
            // 入荷元実績担当者コード
            entity.setNyukamotojissekitnshacd(dto.getIdomotojissekitnshacd());
            // 出荷先コード
            entity.setShukkasakicd(dto.getIdosakisokocd());
            // 出荷先実績担当者コード
            entity.setShukkasakijissekitnshacd(dto.getIdosakijissekitnshacd());
        }

        // 処理区分「1:新規」特有の設定
        if (dto.isRegistProcess()) {
            // 金額
            entity.setKin(util.getLongValue(dto.getTotalkin()));

            // 処理区分「2:取消」特有の設定
        } else if (dto.isCancelProcess()) {
            // 取消伝票番号
            entity.setTrksdnpyno(dto.getNyushukkodnpyno());
            // 金額
            entity.setKin(util.getLongValue(dto.getTotalkin()) * -1);
        }
        return entity;
    }

    /**
     * 入出庫仕入制御テーブル更新用データ設定
     * 
     * @param dto H2101W01UpdateDto
     * @return entity S11T005TEntity
     */
    private S11T005TEntity s11t005tCreateUpdateSetEntity(H2101W01UpdateDto dto) {
        S11T005TEntity entity = new S11T005TEntity();
        // 取消伝票番号
        entity.setTrksdnpyno(dto.getNewdnpyno());
        // 更新者AP基本コード
        entity.setKoshaapkihoncd(dto.getTnshaapkihoncd());
        // 更新者ショップコード
        entity.setKoshashopcd(dto.getTnshashopcd());
        // 更新者コード
        entity.setKoshacd(dto.getTnshacd());
        // 更新システム日付
        entity.setKshnsystmymd(dto.getSystmymd());
        // 更新システム時間
        entity.setKshnsystmjikan(dto.getSystmjikan());
        // 更新運用日付
        entity.setKshnunyoymd(dto.getUnyoymd());
        // 更新IPアドレス
        entity.setKshnipadrse(dto.getIpadrse());
        // 更新プログラムID
        entity.setKshnpgmid(dto.getProgramid());
        return entity;
    }

    /**
     * 入出庫仕入制御テーブル更新条件設定
     * 
     * @param entity S11T005TEntity(検索結果)
     * @return entityExample S11T005TEntityExample
     */
    private S11T005TEntityExample s11t005tCreateUpdateConditionEntity(S11T005TEntity entity) {
        // 更新条件設定
        S11T005TEntityExample entityExample = new S11T005TEntityExample();
        entityExample.createCriteria().andApkihoncdEqualTo(entity.getApkihoncd())
            .andShopcdEqualTo(entity.getShopcd()).andDnpynoEqualTo(entity.getDnpyno())
            .andKshnsystmymdEqualTo(entity.getKshnsystmymd())
            .andKshnsystmjikanEqualTo(entity.getKshnsystmjikan());
        // 呼出元に更新条件をリターン
        return entityExample;
    }

}
