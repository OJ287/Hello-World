package jp.co.aquaclara.accis.web.domain.service.c0815;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.thymeleaf.util.ListUtils;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.constants.SystemConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S01T002TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M003TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M006TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M013TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M046TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M048TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02V001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T002TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S06T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T004TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.Z1808B01Dao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S01T002TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S01T002TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M003TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M003TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M006TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M006TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M048TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M048TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S06T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S06T001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T004TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T004TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T004TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.Z1808B01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S04T001TC0815W11Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S04T002TC0815W11Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S04T002TC0815W21Entity;
import jp.co.aquaclara.accis.common.exception.BusinessException;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.common.util.business.BusinessUtil;
import jp.co.aquaclara.accis.web.config.AppSystemProperties;
import jp.co.aquaclara.accis.web.controller.c0815.bean.C0815B03JobStateBean;
import jp.co.aquaclara.accis.web.controller.c0815.session.C0815W00Session;
import jp.co.aquaclara.accis.web.controller.commons.DbManagementSession;
import jp.co.aquaclara.accis.web.controller.commons.UserSessionForm;
import jp.co.aquaclara.accis.web.controller.z9999.bean.Z9999W01JobStateBean;
import jp.co.aquaclara.accis.web.domain.dto.c0815.C0815B03UpdateDto;
import jp.co.aquaclara.accis.web.domain.dto.c0815.C0815R11PdfOutputDto;
import jp.co.aquaclara.accis.web.domain.dto.c0815.C0815R12CsvOutputDto;
import jp.co.aquaclara.accis.web.domain.dto.c0815.C0815W11AcptDlvryMeisaiDto;
import jp.co.aquaclara.accis.web.domain.dto.c0815.C0815W11DlvryJissekiDto;
import jp.co.aquaclara.accis.web.domain.dto.c0815.C0815W11DlvryRcrdCaptureWorkDto;
import jp.co.aquaclara.accis.web.domain.dto.c0815.C0815W11SearchConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.c0815.C0815W11SearchResultDto;
import jp.co.aquaclara.accis.web.domain.dto.c0815.C0815W11UpdateDto;
import jp.co.aquaclara.accis.web.domain.service.c0800.UtilityService;
import jp.co.aquaclara.accis.web.domain.service.common.S3AccessService;
import jp.co.aquaclara.accis.web.domain.service.common.async.AsyncManagementService;
import jp.co.aquaclara.accis.web.domain.service.common.async.AsyncManagementServiceModel;
import jp.co.aquaclara.accis.web.domain.service.master.S02M006TService;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import jp.co.aquaclara.accis.web.exception.dto.FormField;
import lombok.extern.slf4j.Slf4j;

/**
 * 配達実績登録（一括更新）Service
 * 
 * @author accisdev006
 *
 */
@Service
@Slf4j
public class C0815W11Service {

    /** 担当者マスタDAO */
    @Autowired
    private S02M003TDao s02m003tDao;

    /** システム管理マスタDAO */
    @Autowired
    private S02M006TDao s02m006tDao;

    /** 得意先マスタDAO */
    @Autowired
    private S02M013TDao s02m013tDao;

    /** ショップマスタDAO */
    @Autowired
    private S02M046TDao s02m046tDao;

    /** 得意先設置サーバ管理マスタDAO */
    @Autowired
    private S02M048TDao s02m048tDao;

    /** 受注配達テーブルDAO */
    @Autowired
    private S04T001TDao s04t001tDao;

    /** 受注配達明細テーブルDAO */
    @Autowired
    private S04T002TDao s04t002tDao;

    /** 売上テーブルDAO */
    @Autowired
    private S06T001TDao s06t001tDao;

    /** 請求残高テーブルDAO */
    @Autowired
    private S07T001TDao s07t001tDao;

    /** 請求残高処理管理テーブルDAO */
    @Autowired
    private S07T004TDao s07t004tDao;

    /** 消費税計算バッチDAO */
    @Autowired
    private Z1808B01Dao z1808b01Dao;

    /** 処理状態管理テーブルDAO */
    @Autowired
    private S01T002TDao s01t002tDao;

    /** 商品マスタビューDAO */
    @Autowired
    private S02V001TDao s02v001tDao;

    /** システム管理マスタService */
    @Autowired
    private S02M006TService s02m006tService;

    /** S3アクセスService */
    @Autowired
    private S3AccessService s3AccessService;

    /** 非同期処理管理(受付)Service */
    @Autowired
    private AsyncManagementService<C0815B03UpdateDto> c0815b03ManagementService;

    /**
     * 非同期処理管理サービス（更新）
     */
    @Autowired
    private C0815B03Service c0815b03Service;

    /** ユーティリティサービス */
    @Autowired
    private UtilityService util;

    /** 配達実績登録セッション情報 */
    @Autowired
    private C0815W00Session c0815w00Session;

    /** AP基盤プロパティ値取得Object */
    @Autowired
    private AppSystemProperties appSystemProperties;

    /** 明細1ページ当たりの行数 */
    private static final int PAGEABLE_SIZE = 30;

    /**
     * 受注配達明細リスト設定
     * 
     * @param keyDto C0815W21KeyDto
     */
    public List<C0815W11AcptDlvryMeisaiDto> getAcptDlvryMeisaiList(C0815W11UpdateDto updateDto) {
        S04T002TC0815W21Entity s04t002tC0815W21Entity = new S04T002TC0815W21Entity();
        s04t002tC0815W21Entity.setKakakugetbasedd(updateDto.getDlvrydd());
        s04t002tC0815W21Entity.setApkihoncd(updateDto.getApkihoncd());
        s04t002tC0815W21Entity.setShopcd(updateDto.getShopcd());
        s04t002tC0815W21Entity.setAcptdnpyno(updateDto.getAcptdnpyno());
        s04t002tC0815W21Entity.setEdano(Short.valueOf(updateDto.getAcptdnpyedano()));

        List<S04T002TC0815W21Entity> acptmeisaiList =
            s04t002tDao.selectByC0815W21AcptmeisaiList(s04t002tC0815W21Entity);

        List<C0815W11AcptDlvryMeisaiDto> retList = new ArrayList<>();
        acptmeisaiList.forEach(entity -> {
            C0815W11AcptDlvryMeisaiDto meisaiDto = new C0815W11AcptDlvryMeisaiDto();
            String suryo;
            String hanbaiprice;
            String kin;
            String shohitaxkin;
            if (CommonConstants.DNPYSTS_11.equals(updateDto.getDnpysts())) {
                suryo = entity.getAcptsuryo();
                hanbaiprice = entity.getAcptunt();
                kin = entity.getAcptkin();
                shohitaxkin = entity.getAcptshohitaxkin();
            } else {
                suryo = entity.getNohinsuryo();
                hanbaiprice = entity.getNohinunt();
                kin = entity.getNohinkin();
                shohitaxkin = entity.getNohinshohitaxkin();
            }
            String haifuflg =
                !StringUtils.isEmpty(entity.getHaifubutsucd()) ? CommonConstants.FLG_ON
                    : CommonConstants.FLG_OFF; // 配布物フラグ
            meisaiDto.setAcptdnpymeisaino(
                Objects.toString(entity.getAcptdnpymeisaino(), SystemConstants.WORD_BLANK)); // 受注伝票明細番号
            meisaiDto.setAcptdnpymeisaiedano(
                Objects.toString(entity.getAcptdnpymeisaiedano(), SystemConstants.WORD_BLANK)); // 受注伝票明細枝番号
            meisaiDto.setShncd(entity.getShncd()); // 商品コード
            meisaiDto.setHaifuflg(haifuflg); // 配布物フラグ
            meisaiDto.setHaifubutsucd(entity.getHaifubutsucd()); // 配布物コード
            meisaiDto.setHaifubutsuapkihoncd(entity.getHaifubutsuapkihoncd()); // 配布物AP基本コード
            meisaiDto.setHaifubutsushopcd(entity.getHaifubutsushopcd()); // 配布物ショップコード
            meisaiDto.setKatasiki(entity.getKatasiki()); // 型式
            meisaiDto.setKeijokbn(entity.getKeijokbn()); // 計上区分
            meisaiDto.setKeijokbn2(entity.getKeijokbn()); // 計上区分2
            meisaiDto.setNohinsuryo(entity.getNohinsuryo()); // 納品数量
            meisaiDto.setNohinunt(entity.getNohinunt()); // 納品単価
            meisaiDto.setTaxkbn(entity.getTaxkbn()); // 税区分
            meisaiDto.setTanpinmeisaiflg(entity.getTanpinmeisaiflg()); // 単品明細フラグ
            meisaiDto.setMeisaibiko(entity.getMeisaibiko()); // 明細備考
            meisaiDto.setPricekbn(entity.getPricekbn()); // 価格区分
            meisaiDto.setHyojunpricekirikaedd(entity.getHyojunpricekirikaedd()); // 標準単価
            meisaiDto.setGenzhyojunprice(entity.getGenzhyojunprice()); // 現在標準価格
            meisaiDto.setShinhyojunprice(entity.getShinhyojunprice()); // 新標準価格
            meisaiDto.setHansokustrtdd(entity.getHansokustrtdd()); // 販促開始日
            meisaiDto.setHansokushrydd(entity.getHansokushrydd()); // 販促終了日
            meisaiDto.setHansokumochikaeriprice(entity.getHansokumochikaeriprice()); // 販促持ち帰り価格
            meisaiDto.setIro(entity.getIro()); // 色
            meisaiDto.setSz(entity.getSz()); // サイズ
            meisaiDto.setKeijokbnnm(entity.getKeijokbnnm()); // 計上区分名称
            meisaiDto.setSokazu(entity.getSokazu()); // 総数
            meisaiDto.setShnnm(entity.getShnnmryakusho()); // 商品名称略称
            meisaiDto.setHyojunprice(entity.getHyojunprice()); // 標準価格
            meisaiDto.setSuryo(suryo);
            meisaiDto.setKin(kin);
            meisaiDto.setHanbaiprice(hanbaiprice);
            meisaiDto.setShohitaxkin(shohitaxkin);
            meisaiDto.setShncd(entity.getShncd());
            meisaiDto.setKeijokbnnm(entity.getKeijokbnnm());
            meisaiDto.setNohinunt(entity.getNohinunt());
            meisaiDto.setMeisaibiko(entity.getMeisaibiko());
            meisaiDto.setLctgrycd(entity.getLctgrycd());
            meisaiDto.setMctgrycd(entity.getMctgrycd());
            meisaiDto.setShnnm(entity.getShnnmryakusho());
            meisaiDto.setIro(entity.getIro());
            meisaiDto.setSz(entity.getSz());
            meisaiDto.setIrisu(entity.getUriirisu());
            meisaiDto.setIrisutani(entity.getUritani());
            meisaiDto.setSokazu(entity.getSokazu());
            meisaiDto.setKatasiki(entity.getKatasiki());
            meisaiDto.setTani(entity.getUritrtskitani());
            meisaiDto.setHyojunprice(entity.getHyojunprice());
            meisaiDto.setHyojunpricekirikaedd(entity.getHyojunpricekirikaedd());
            meisaiDto.setGenzhyojunprice(entity.getGenzhyojunprice());
            meisaiDto.setShinhyojunprice(entity.getShinhyojunprice());
            meisaiDto.setGenzhyojunmochikaeriprice(entity.getGenzmochikaeriprice());
            meisaiDto.setShinhyojunmochikaeriprice(entity.getShinmochikaeriprice());
            meisaiDto.setHansokustrtdd(entity.getHansokustrtdd());
            meisaiDto.setHansokushrydd(entity.getHansokushrydd());
            meisaiDto.setHansokumochikaeriprice(entity.getHansokumochikaeriprice());
            meisaiDto.setTanpinmeisaiflg(entity.getTanpinmeisaiflg());
            meisaiDto.setPricekbn(entity.getPricekbn());
            meisaiDto.setMeisaikbn(entity.getMeisaikbn()); // 明細区分
            meisaiDto.setNyuryokumeisaiflg(entity.getNyuryokumeisaiflg()); // 入力明細フラグ
            meisaiDto.setShntaxkbn(entity.getShntaxkbn()); // 商品税区分

            // ショップ切替日、現ショップ価格、新ショップ価格、現ショップ持ち帰り価格、新ショップ持ち帰り価格
            if (CommonConstants.KBN_PRICE_HYOJUN.equals(entity.getPricekbn())) {
                meisaiDto.setShoppricekirikaedd(entity.getHyojunpricekirikaedd());
                meisaiDto.setGenzshopprice(
                    Objects.toString(entity.getGenzhyojunprice(), BigDecimal.ZERO.toString()));
                meisaiDto.setShinshopprice(
                    Objects.toString(entity.getShinhyojunprice(), BigDecimal.ZERO.toString()));
                meisaiDto.setGenzshopmochikaeriprice(
                    Objects.toString(entity.getGenzmochikaeriprice(), BigDecimal.ZERO.toString()));
                meisaiDto.setShinshopmochikaeriprice(
                    Objects.toString(entity.getShinmochikaeriprice(), BigDecimal.ZERO.toString()));
            } else {
                meisaiDto.setShoppricekirikaedd(entity.getOpnpricekirikaedd());
                meisaiDto.setGenzshopprice(
                    Objects.toString(entity.getGenzopnprice(), BigDecimal.ZERO.toString()));
                meisaiDto.setShinshopprice(
                    Objects.toString(entity.getShinopnprice(), BigDecimal.ZERO.toString()));
                meisaiDto.setGenzshopmochikaeriprice(Objects
                    .toString(entity.getGenzopnmochikaeriprice(), BigDecimal.ZERO.toString()));
                meisaiDto.setShinshopmochikaeriprice(Objects
                    .toString(entity.getShinopnmochikaeriprice(), BigDecimal.ZERO.toString()));
            }

            // 販促開始日、販促終了日、販促配達価格、販促持ち帰り価格
            if (!CommonConstants.KBN_SAKUSEIMOTO_JUCHUYOYAKU.equals(entity.getDatsksmotokbn())
                && CommonConstants.FLG_ON.equals(entity.getHansokukbn())) {
                meisaiDto.setHansokustrtdd(entity.getHansokustrtdd());
                meisaiDto.setHansokushrydd(entity.getHansokushrydd());
                meisaiDto.setHansokuprice(
                    Objects.toString(entity.getHansokudlvryprice(), BigDecimal.ZERO.toString()));
                meisaiDto.setHansokumochikaeriprice(Objects
                    .toString(entity.getHansokumochikaeriprice(), BigDecimal.ZERO.toString()));
            }

            // 得意先価格開始日、得意先価格終了日、得意先価格、得意先持ち帰り価格
            if (CommonConstants.FLG_ON.equals(entity.getRknkindtokubetsuuntflg())) {
                if (CommonConstants.FLG_ON.equals(entity.getStkbn())) {
                    meisaiDto.setTtpricestrtdd(entity.getSet_tekiyostrtdd());
                    meisaiDto.setTtpriceshrydd(entity.getSet_tekiyoshrydd());
                    meisaiDto.setTtprice(
                        Objects.toString(entity.getSet_dlvryprice(), BigDecimal.ZERO.toString()));
                    meisaiDto.setTtmochikaeriprice(Objects.toString(entity.getSet_mochikaeriprice(),
                        BigDecimal.ZERO.toString()));
                } else {
                    meisaiDto.setTtpricestrtdd(entity.getTekiyostrtdd());
                    meisaiDto.setTtpriceshrydd(entity.getTekiyoshrydd());
                    meisaiDto.setTtprice(
                        Objects.toString(entity.getDlvryprice(), BigDecimal.ZERO.toString()));
                    meisaiDto.setTtmochikaeriprice(
                        Objects.toString(entity.getMochikaeriprice(), BigDecimal.ZERO.toString()));
                }
            }

            retList.add(meisaiDto);
        });
        return retList;

    }

    /**
     * 検索処理
     * 
     * @param dto C0815W11SearchConditionDto
     * @return List<C0815W11SearchResultDto>
     */
    public List<C0815W11SearchResultDto> search(C0815W11SearchConditionDto dto) {
        S04T001TC0815W11Entity e = createSearchEntity(dto);

        List<S04T001TC0815W11Entity> entityList = s04t001tDao.selectByAcptDlvryList(e);

        return createResultList(entityList, dto.getUnyodd());
    }

    /**
     * 更新処理
     * 
     * @param dto C0815W11UpdateDto
     * @param dbManagementSession DbManagementSession
     * @throws IOException
     * @throws Exception
     */
    @Transactional
    public C0815B03JobStateBean update(C0815W11UpdateDto dto,
        DbManagementSession dbManagementSession, UserSessionForm userSessionForm)
        throws IOException, Exception {

        // 相関チェックを実施(トランザクションあり)
        checkParam(dto.getDlvryjissekiDtoList(), false);

        // CSVデータ作成
        List<C0815W11DlvryRcrdCaptureWorkDto> workDtoList = createWorkDtoList(dto);

        // CSVファイル保存先
        String filepathStr =
            s02m006tService.searchForMoji(CommonConstants.APCD_000, CommonConstants.SYS_SHOPCD_000,
                CommonConstants.SYS_SETTEINO_230, CommonConstants.SETTEICD_230_2);
        Path filepath = FileSystems.getDefault().getPath(filepathStr);
        // CSVファイル名設定
        String timestamp =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String filename =
            String.format("AccisSystemUpload%s%s%s%s.csv", dbManagementSession.getApkihoncd(),
                dbManagementSession.getShopcd(), dbManagementSession.getTnshacd(), timestamp);
        // CSVファイル作成
        String path = outputCsvFile(workDtoList, filepath, filename);
        // CSVファイルをS3にアップロード
        s3AccessService.fileUpload(path, filepathStr, filename);

        // 請求締対象となる受注データ（受注時代引フラグまたは単体伝票締フラグがONの受注）を取得
        Optional<S04T001TEntity> optSkyshimeAcptdnpyEntity = dto.getDlvryjissekiDtoList().stream()
            .filter(jissekiDto -> util.isFlagged(jissekiDto.getKshntrgtflg()))
            .filter(jissekiDto -> util.isFlagged(jissekiDto.getAcptthdaibikiflg())
                || util.isFlagged(jissekiDto.getSingldnpyflg()))
            .findFirst().map(jissekiDto -> selectS04T001TEntity(jissekiDto));
        // 請求締対象となる受注が更新対象配達実績リストに含まれている場合
        S07T004TEntity s07t004tEntity = optSkyshimeAcptdnpyEntity
            // 請求締処理管理テーブルのチェック及び更新
            .map(acptdnpyEntity -> {
                // 請求締処理管理テーブルエンティティを取得し、
                Optional<S07T004TEntity> optSkyshimeMngEntity =
                    selectS07T004TEntity(acptdnpyEntity);
                // エンティティが取得できた場合は請求締処理管理テーブルをチェック
                optSkyshimeMngEntity.ifPresent(shimeEntity -> checkSkyshimesyori(shimeEntity));
                // 請求締処理管理テーブル更新用のEntityを取得（テーブルに対象データがない場合は新規Entityを生成）
                S07T004TEntity shimeMngEntity = optSkyshimeMngEntity
                    .orElse(createInsertS07T004TEntity(dto, dbManagementSession));
                // 請求締め処理管理テーブルを「1：処理開始」に更新
                fillPropertiesForUpdate(shimeMngEntity, dbManagementSession,
                    CommonConstants.PRCSJOTAI_1);
                int result = s07t004tDao.updateByExample(shimeMngEntity,
                    createUpdateS07T004TEntityExample(dto, true));
                // 更新対象が存在しない場合は新規登録
                if (optSkyshimeMngEntity.isEmpty() && result == 0) {
                    s07t004tDao.insert(shimeMngEntity);
                }
                return shimeMngEntity;
            })
            // 請求締処理管理テーブルの内容を取得（請求締対象となる受注が含まれない場合はnull）
            .orElse(null);

        // モバイル実績取込バッチを実行
        Z9999W01JobStateBean jobstatebean = exeMobileJissekiTorikomi(dto, dbManagementSession,
            filename, userSessionForm, s07t004tEntity);

        C0815B03JobStateBean c0815b03JobStateBean = new C0815B03JobStateBean();
        c0815b03JobStateBean.setApkihoncd(dto.getApkihoncd());
        c0815b03JobStateBean.setShopcd(dto.getShopcd());
        c0815b03JobStateBean.setReceiptId(jobstatebean.getReceiptId());

        return c0815b03JobStateBean;
    }

    /**
     * 処理状態管理テーブル:処理状態チェック
     * 
     * @param dbManagementSession
     * @param isSkyshimednpyStr
     * @param apkihoncd
     * @param shopcd
     * @param prcsshrydd
     * @param prcssshryjikan
     * @param recovshrydd
     * @param recovshryjikan
     * @param receiptId
     * @throws Exception
     */
    public void checkPrcsjotai(String receiptId) throws Exception {

        int tryCount = 1;
        // リトライする最大回数
        final int retryMaxCount = appSystemProperties.getAsyncRetryMaxCount().intValue();
        log.debug("処理状態管理テーブル:処理状態チェック [開始]");
        while (true) {
            // 一定時間待機する。ミリ秒
            waitRetry();

            // 処理管理テーブルから処理状態を取得
            S01T002TEntityKey entitykey = new S01T002TEntityKey();
            entitykey.setRcptid(receiptId);
            S01T002TEntity entity = new S01T002TEntity();
            entity = s01t002tDao.selectByPrimaryKey(entitykey);
            String prcsjotai = entity.getPrcsjotai();

            // 正常終了時
            if (SystemConstants.STATUS_ASYNC_FINISH.equals(prcsjotai)) {
                log.debug("処理状態管理テーブル:処理状態チェック [正常終了] 処理状態 : {} / チェック回数 : ", prcsjotai, tryCount);
                break;
            }

            // トライ回数上限時または異常終了時
            if (tryCount > retryMaxCount || SystemConstants.STATUS_ASYNC_ERROR.equals(prcsjotai)
                || SystemConstants.STATUS_ASYNC_WARNING_FINISH.equals(prcsjotai)
                || SystemConstants.STATUS_ASYNC_LIST_ZERO.equals(prcsjotai)) {
                log.debug("処理状態管理テーブル:処理状態チェック [異常終了] 処理状態 : {} / チェック回数 : ", prcsjotai, tryCount);
                throw new Exception("S01T002T.PRCSJOTAI:" + prcsjotai);
            }

            // トライ回数をカウント
            tryCount++;
        }
    }

    /**
     * 帳票出力情報作成処理
     * 
     * @param updateDto C0815W11UpdateDto
     * @return C0815R11PdfOutputDto
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public C0815R11PdfOutputDto createPdfOutputDto(C0815W11UpdateDto updateDto,
        UserSessionForm userSessionForm) {
        C0815R11PdfOutputDto returnDto = new C0815R11PdfOutputDto<>();
        List<C0815R11PdfOutputDto> pdfoutputDtoList = new ArrayList<>();

        // インボイス施行日を取得
        S02M046TEntityExample s02m046tExample = new S02M046TEntityExample();

        // ショップマスタ検索
        s02m046tExample.createCriteria().andApkihoncdEqualTo(updateDto.getApkihoncd())
            .andShopcdEqualTo(updateDto.getShopcd()).andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<S02M046TEntity> s02m046tEntity = s02m046tDao.selectByExample(s02m046tExample);

        // 選択（画面でチェック）した明細の件数分、以下の処理を行う。
        for (C0815W11DlvryJissekiDto dlvryjissekiDto : updateDto.getDlvryjissekiDtoList()) {
            // 受注伝票データ取得
            S04T001TC0815W11Entity s04t001tEntitykey = new S04T001TC0815W11Entity();
            s04t001tEntitykey.setApkihoncd(dlvryjissekiDto.getApkihoncd());
            s04t001tEntitykey.setShopcd(dlvryjissekiDto.getShopcd());
            s04t001tEntitykey.setTokuisakicd(dlvryjissekiDto.getTokuisakicd());
            s04t001tEntitykey.setUnyoymd(updateDto.getUnyoymd());
            s04t001tEntitykey.setAcptdnpyno(dlvryjissekiDto.getAcptdnpyno());
            s04t001tEntitykey.setEdano(Short.valueOf(dlvryjissekiDto.getAcptedano()));
            s04t001tEntitykey.setYoyakuflag("FALSE");

            // 受注配達テーブル検索（受注伝票データ取得）
            S04T001TC0815W11Entity acptdnpyEntity =
                s04t001tDao.selectByC0815W11Acptdnpydata(s04t001tEntitykey);

            List<C0815W11AcptDlvryMeisaiDto> acptdlvrymeisaiDtoList = new ArrayList<>();

            // 画面で明細データの修正を行っていない場合、DBから対象データを取得する。
            if (!(CommonConstants.FLG_ON.equals(dlvryjissekiDto.getShuseiarinashiflg())
                && StringUtils.isEmpty(dlvryjissekiDto.getMidlvrykbn()))) {

                // 受注配達明細テーブル検索
                List<S04T002TC0815W11Entity> acptmeisaiList = getDetailEntityList(dlvryjissekiDto);

                if (!ListUtils.isEmpty(acptmeisaiList)) {
                    Integer kinsum = 0;
                    Integer shohitaxsum = 0;
                    for (S04T002TC0815W11Entity entity : acptmeisaiList) {

                        // 処理対象の明細データより、入力明細フラグが"1：入力行"のデータのみ処理対象とする。（セット商品の２重計上を避ける）
                        if (CommonConstants.NYURYOKUMEISAIFLG_1
                            .equals(entity.getNyuryokumeisaiflg())) {
                            C0815W11AcptDlvryMeisaiDto dto = new C0815W11AcptDlvryMeisaiDto();
                            String suryo = SystemConstants.WORD_BLANK;
                            String hanbaiprice = SystemConstants.WORD_BLANK;
                            String kin = SystemConstants.WORD_BLANK;
                            String shohitaxkin = SystemConstants.WORD_BLANK;
                            if (CommonConstants.DNPYSTS_12.equals(acptdnpyEntity.getDnpysts())) {
                                suryo = entity.getNohinsuryo();
                                hanbaiprice = entity.getNohinunt();
                                kin = BusinessUtil.getString(entity.getNohinkin(),
                                    BigInteger.ZERO.toString());
                                shohitaxkin = BusinessUtil.getString(entity.getNohinshohitaxkin(),
                                    BigInteger.ZERO.toString());
                            } else {
                                suryo = entity.getAcptsuryo();
                                hanbaiprice = entity.getAcptunt();
                                kin = BusinessUtil.getString(entity.getAcptkin(),
                                    BigInteger.ZERO.toString());
                                shohitaxkin = BusinessUtil.getString(entity.getAcptshohitaxkin(),
                                    BigInteger.ZERO.toString());
                            }
                            dto.setAcptdnpymeisaino(
                                Objects.toString(entity.getAcptdnpymeisaino(), "")); // 受注伝票明細番号
                            dto.setAcptdnpymeisaiedano(
                                Objects.toString(entity.getAcptdnpymeisaiedano(), "")); // 受注伝票明細枝番号
                            dto.setLctgrycd(entity.getLctgrycd()); // 大分類
                            dto.setMctgrycd(entity.getMctgrycd()); // 中分類
                            dto.setShncd(entity.getShncd()); // 商品コード
                            dto.setShnnm(entity.getShnnmryakusho()); // 商品名称略称
                            dto.setKeijokbn(entity.getKeijokbn()); // 計上区分
                            dto.setKeijokbnnm(entity.getKeijokbnnm()); // 計上区分名称
                            dto.setTaxkbn(entity.getTaxkbn()); // 税区分
                            dto.setSuryo(suryo); // 数量
                            dto.setTani(entity.getUritrtskitani()); // 単位
                            dto.setHyojunprice(entity.getHyojunprice()); // 標準価格
                            dto.setHanbaiprice(hanbaiprice); // 販売価格
                            dto.setKin(kin); // 金額
                            kinsum += Integer.parseInt(kin); // 金額合計
                            dto.setShohitaxkin(shohitaxkin); // 消費税金額
                            shohitaxsum += Integer.parseInt(shohitaxkin); // 消費税金額合計
                            dto.setMeisaibiko(entity.getMeisaibiko()); // 明細備考
                            dto.setIro(entity.getIro()); // 色
                            dto.setSz(entity.getSz()); // サイズ
                            dto.setKatasiki(entity.getKatasiki()); // 型式
                            dto.setIrisu(entity.getUriirisu()); // 入数
                            dto.setIrisutani(entity.getUritani()); // 入数単位
                            dto.setSokazu(entity.getSokazu()); // 総数

                            acptdlvrymeisaiDtoList.add(dto);
                        }
                        // 金額合計を設定
                        if (CommonConstants.DNPYSTS_12.equals(acptdnpyEntity.getDnpysts())) {
                            acptdnpyEntity.setNohinkin(kinsum.toString());
                            acptdnpyEntity.setNohinshohitaxkin(shohitaxsum.toString());
                        } else {
                            acptdnpyEntity.setAcptkin(kinsum.toString());
                            acptdnpyEntity.setAcptshohitaxkin(shohitaxsum.toString());
                        }
                    }
                }
            } else {
                if (!ObjectUtils.isEmpty(dlvryjissekiDto)) {
                    acptdlvrymeisaiDtoList = dlvryjissekiDto.getAcptdlvrymeisaiList();
                    int kazeikinTotal = 0;
                    int keigenkinTotal = 0;
                    if (CommonConstants.FLG_ON.equals(dlvryjissekiDto.getAcptthdaibikiflg())
                        || CommonConstants.FLG_ON.equals(dlvryjissekiDto.getSingldnpyflg())) {
                        // 代引の場合
                        for (C0815W11AcptDlvryMeisaiDto acptDlvryMeisaiDto : dlvryjissekiDto
                            .getAcptdlvrymeisaiList()) {
                            if (CommonConstants.KBN_TAX_KAZEI
                                .equals(acptDlvryMeisaiDto.getShntaxkbn())) {
                                kazeikinTotal += Integer
                                    .valueOf(ItemUtil.removeComma(acptDlvryMeisaiDto.getKin()));
                            }
                            if (CommonConstants.KBN_TAX_KEIGEN
                                .equals(acptDlvryMeisaiDto.getShntaxkbn())) {
                                keigenkinTotal += Integer
                                    .valueOf(ItemUtil.removeComma(acptDlvryMeisaiDto.getKin()));
                            }
                        }

                        // 課税金額合計の消費税金額を計算する
                        int kazeitaxkin = calculateTax(dlvryjissekiDto.getApkihoncd(),
                            dlvryjissekiDto.getShopcd(), dlvryjissekiDto.getDlvrydd(),
                            String.valueOf(kazeikinTotal), CommonConstants.KBN_TAX_KAZEI);
                        // 軽減課税金額合計の消費税金額を計算する
                        int keigentaxkin = calculateTax(dlvryjissekiDto.getApkihoncd(),
                            dlvryjissekiDto.getShopcd(), dlvryjissekiDto.getDlvrydd(),
                            String.valueOf(keigenkinTotal), CommonConstants.KBN_TAX_KEIGEN);
                        // 受注伝票に金額および消費税金額を設定（伝票ステータスが配達・売上済の場合は納品～、それ以外は受注～に設定）
                        if (CommonConstants.DNPYSTS_12.equals(acptdnpyEntity.getDnpysts())) {
                            acptdnpyEntity
                                .setNohinkin(String.valueOf(kazeikinTotal + keigenkinTotal));
                            acptdnpyEntity
                                .setNohinshohitaxkin(String.valueOf(kazeitaxkin + keigentaxkin));
                        } else {
                            acptdnpyEntity
                                .setAcptkin(String.valueOf(kazeikinTotal + keigenkinTotal));
                            acptdnpyEntity
                                .setAcptshohitaxkin(String.valueOf(kazeitaxkin + keigentaxkin));
                        }
                    } else {
                        // 代引以外の場合
                        int nohinkin = 0;
                        int nohintaxkin = 0;
                        int acptkin = 0;
                        int acpttaxkin = 0;
                        for (C0815W11AcptDlvryMeisaiDto acptDlvryMeisaiDto : dlvryjissekiDto
                            .getAcptdlvrymeisaiList()) {
                            if (CommonConstants.DNPYSTS_12.equals(acptdnpyEntity.getDnpysts())) {
                                nohinkin += Integer
                                    .valueOf(ItemUtil.removeComma(acptDlvryMeisaiDto.getKin()));
                                nohintaxkin += Integer.valueOf(
                                    ItemUtil.removeComma(acptDlvryMeisaiDto.getShohitaxkin()));
                            } else {
                                acptkin += Integer
                                    .valueOf(ItemUtil.removeComma(acptDlvryMeisaiDto.getKin()));
                                acpttaxkin += Integer.valueOf(
                                    ItemUtil.removeComma(acptDlvryMeisaiDto.getShohitaxkin()));
                            }
                        }
                        if (CommonConstants.DNPYSTS_12.equals(acptdnpyEntity.getDnpysts())) {
                            acptdnpyEntity.setNohinkin(String.valueOf(nohinkin));
                            acptdnpyEntity.setNohinshohitaxkin(String.valueOf(nohintaxkin));
                        } else {
                            acptdnpyEntity.setAcptkin(String.valueOf(acptkin));
                            acptdnpyEntity.setAcptshohitaxkin(String.valueOf(acpttaxkin));
                        }
                    }
                }
            }

            String kinsum = "";
            String shohitaxsum = "";
            String mshimeurikin = "0";
            String mshimeurikintax = "0";
            String zenkaikurikosikin = "0";

            // 前回繰越金
            if (!StringUtils.isEmpty(acptdnpyEntity.getZnkkurikoshikin())) {
                zenkaikurikosikin = acptdnpyEntity.getZnkkurikoshikin();
            }
            // 金額、消費税金額を設定
            if (CommonConstants.DNPYSTS_12.equals(acptdnpyEntity.getDnpysts())) {
                kinsum = acptdnpyEntity.getNohinkin();
                shohitaxsum = acptdnpyEntity.getNohinshohitaxkin();
            } else {
                kinsum = acptdnpyEntity.getAcptkin();
                shohitaxsum = acptdnpyEntity.getAcptshohitaxkin();
            }
            // 未締売上金額、未締売上消費税金額を設定
            S06T001TEntityExample example = new S06T001TEntityExample();
            S06T001TEntityExample.Criteria criteria = example.createCriteria();
            criteria.andApkihoncdEqualTo(acptdnpyEntity.getApkihoncd());
            criteria.andShopcdEqualTo(acptdnpyEntity.getShopcd());
            criteria.andAcptdnpynoEqualTo(acptdnpyEntity.getAcptdnpyno());
            criteria.andUridnpynoNotEqualTo(acptdnpyEntity.getAcptdnpyno());
            criteria.andSkyshimedydmIsNull();
            criteria.andSkyshimeddIsNull();
            criteria.andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
            List<S06T001TEntity> s06t001tEntityList = s06t001tDao.selectByExample(example);
            if (!ListUtils.isEmpty(s06t001tEntityList)) {
                mshimeurikin = Objects.toString(s06t001tEntityList.get(0).getUrikin(), "");
                mshimeurikintax = Objects.toString(s06t001tEntityList.get(0).getShohitaxkin(), "");
            }
            if (!ListUtils.isEmpty(acptdlvrymeisaiDtoList)) {
                for (C0815W11AcptDlvryMeisaiDto acptdlvrymeisaiDto : acptdlvrymeisaiDtoList) {
                    C0815R11PdfOutputDto pdfoutputDto = new C0815R11PdfOutputDto<>();
                    // 伝票AP基本コード
                    pdfoutputDto.setDnpyapkihoncd(nullToBlank(dlvryjissekiDto.getApkihoncd()));
                    // 伝票ショップコード
                    pdfoutputDto.setDnpyshopcd(nullToBlank(dlvryjissekiDto.getShopcd()));
                    // AP基本コード
                    pdfoutputDto.setApkihoncd(nullToBlank(dlvryjissekiDto.getApkihoncd()));
                    // ショップコード
                    pdfoutputDto.setShopcd(nullToBlank(dlvryjissekiDto.getShopcd()));
                    // ショップ名称
                    pdfoutputDto.setShopnm(nullToBlank(updateDto.getShopnm()));
                    // 伝票番号
                    pdfoutputDto.setDnpynomei(nullToBlank(dlvryjissekiDto.getAcptdnpyno()));
                    // 得意先コード
                    pdfoutputDto.setTokuisakicdmei(nullToBlank(dlvryjissekiDto.getTokuisakicd()));
                    // 得意先名称
                    pdfoutputDto.setTokuisakinmmei(nullToBlank(dlvryjissekiDto.getTokuisakinmn()));
                    // 配達日
                    pdfoutputDto.setDlvryddmei(nullToBlank(dlvryjissekiDto.getDlvrydd()));
                    // 未配達区分
                    pdfoutputDto.setMidlvrykbn(nullToBlank(dlvryjissekiDto.getMidlvrykbnnm()));
                    // 次回配達予定日
                    pdfoutputDto.setHdnjikaidd(nullToBlank(dlvryjissekiDto.getJikaidlvryytidd()));
                    // 配達担当者コード
                    pdfoutputDto.setDlvrytnshacd(nullToBlank(dlvryjissekiDto.getDlvrytnshacd()));
                    // 配達担当者名称
                    pdfoutputDto.setDlvrytnshamei(nullToBlank(dlvryjissekiDto.getDlvrytnshamei()));
                    // 回収実績ボトルレギュラー
                    pdfoutputDto.setKaishun1n2rittorukaishu(
                        nullToBlank(dlvryjissekiDto.getKaishun1n2rittorukaishu()));
                    // 回収実績ボトルスリム
                    pdfoutputDto.setKaishun8rittorukaishu(
                        nullToBlank(dlvryjissekiDto.getKaishun7rittorukaishu()));
                    // 受注時代引きフラグ
                    pdfoutputDto
                        .setAcptthdaibikiflg(nullToBlank(dlvryjissekiDto.getAcptthdaibikiflg()));
                    // 担当者コード
                    pdfoutputDto.setTnshaCd(nullToBlank(userSessionForm.getTnshacd()));
                    // 担当者名称
                    pdfoutputDto.setTnshaMei(nullToBlank(userSessionForm.getTnshamei()));
                    // 配達拠点コード
                    pdfoutputDto.setDlvrykyotencd(nullToBlank(acptdnpyEntity.getDlvrykyotencd()));
                    // 配達拠点名称
                    pdfoutputDto.setDlvrykyotennm(nullToBlank(acptdnpyEntity.getDlvrykyotennm()));
                    // 配達会社名称
                    pdfoutputDto.setDlvrycmpnynm(nullToBlank(acptdnpyEntity.getDlvrycmpnynm()));
                    // 配達エリアコード
                    pdfoutputDto.setDlvryareacd(nullToBlank(acptdnpyEntity.getDlvryareanm()));
                    // 受注区分
                    pdfoutputDto.setAcptkbn(nullToBlank(acptdnpyEntity.getAcptkbn()));
                    // 預かりボトル１２Ｌ本数
                    pdfoutputDto.setAzukaribtln1n2rittoruhns(
                        nullToBlank(acptdnpyEntity.getAzukaribtln1n2rittoruhns()));
                    // 預かりボトル７Ｌ本数
                    pdfoutputDto.setAzukaribtln8rittoruhns(
                        nullToBlank(acptdnpyEntity.getAzukaribtln7rittoruhns()));
                    // 前回繰越金額
                    pdfoutputDto.setZenkaikurikosikin(zenkaikurikosikin);
                    // 金額合計
                    pdfoutputDto.setKinSum(nullToBlank(kinsum));
                    // 消費税合計
                    pdfoutputDto.setShohitaxkinSum(nullToBlank(shohitaxsum));
                    // 単体伝票締めフラグ
                    pdfoutputDto
                        .setTantaidnpyshimeFlg(nullToBlank(acptdnpyEntity.getSingldnpyflg()));
                    // インボイス施行フラグ
                    if (!StringUtils.isEmpty(dlvryjissekiDto.getDlvrydd())
                        && !StringUtils.isEmpty(s02m046tEntity.get(0).getInvoicshikoymd())
                        && DateTimeUtil.isLater(s02m046tEntity.get(0).getInvoicshikoymd(),
                            dlvryjissekiDto.getDlvrydd())) {
                        pdfoutputDto.setInvoiceFlg(CommonConstants.FLG_ON);
                    } else {
                        pdfoutputDto.setInvoiceFlg(CommonConstants.FLG_OFF);
                    }
                    // 商品大分類コード
                    pdfoutputDto.setLctgrycd(acptdlvrymeisaiDto.getLctgrycd());
                    // 商品中分類コード
                    pdfoutputDto.setMctgrycd(acptdlvrymeisaiDto.getMctgrycd());
                    // 商品コード
                    pdfoutputDto.setShncd(nullToBlank(acptdlvrymeisaiDto.getShncd()));
                    // 商品名称
                    pdfoutputDto.setShnnm(nullToBlank(acptdlvrymeisaiDto.getShnnm()));
                    // 計上区分
                    pdfoutputDto.setKeijokbn(nullToBlank(acptdlvrymeisaiDto.getKeijokbn()));
                    // 計上区分名称
                    pdfoutputDto.setKeijokbnnm(nullToBlank(acptdlvrymeisaiDto.getKeijokbnnm()));
                    // 数量
                    pdfoutputDto.setAcptsuryo(nullToBlank(acptdlvrymeisaiDto.getSuryo()));
                    // 数量単位
                    pdfoutputDto.setSuryotani(nullToBlank(acptdlvrymeisaiDto.getTani()));
                    // 標準価格
                    pdfoutputDto.setHyojunprice(nullToBlank(acptdlvrymeisaiDto.getHyojunprice()));
                    // 販売価格
                    pdfoutputDto
                        .setHanbaipricemei(nullToBlank(acptdlvrymeisaiDto.getHanbaiprice()));
                    // 金額
                    pdfoutputDto.setKin(nullToBlank(acptdlvrymeisaiDto.getKin()));
                    // 消費税金額
                    pdfoutputDto.setShohitaxkin(nullToBlank(acptdlvrymeisaiDto.getShohitaxkin()));
                    // 明細備考
                    pdfoutputDto.setMeisaibiko(nullToBlank(acptdlvrymeisaiDto.getMeisaibiko()));
                    // 未締売上金額
                    pdfoutputDto.setMshimeurikin(mshimeurikin);
                    // 未締売上消費税金額
                    pdfoutputDto.setMshimeurikintax(mshimeurikintax);

                    pdfoutputDtoList.add(pdfoutputDto);
                }
            }
        }
        returnDto.setPrintList(pdfoutputDtoList);

        return returnDto;

    }

    /**
     * CSV出力情報作成処理
     * 
     * @param updateDto C0815W11UpdateDto
     * @return C0815R12CsvOutputDto
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public C0815R12CsvOutputDto createCsvOutputDto(C0815W11UpdateDto updateDto) {
        C0815R12CsvOutputDto returnDto = new C0815R12CsvOutputDto<>();

        List<C0815R12CsvOutputDto> csvoutputDtoList = new ArrayList<>();
        if (!ListUtils.isEmpty(updateDto.getDlvryjissekiDtoList())) {
            for (C0815W11DlvryJissekiDto dlvryjissekiDto : updateDto.getDlvryjissekiDtoList()) {
                S04T001TC0815W11Entity s04t001tEntitykey = new S04T001TC0815W11Entity();
                s04t001tEntitykey.setApkihoncd(dlvryjissekiDto.getApkihoncd());
                s04t001tEntitykey.setShopcd(dlvryjissekiDto.getShopcd());
                s04t001tEntitykey.setTokuisakicd(dlvryjissekiDto.getTokuisakicd());
                s04t001tEntitykey.setUnyoymd(updateDto.getUnyoymd());
                s04t001tEntitykey.setAcptdnpyno(dlvryjissekiDto.getAcptdnpyno());
                s04t001tEntitykey.setEdano(Short.valueOf(dlvryjissekiDto.getAcptedano()));
                s04t001tEntitykey.setYoyakuflag("FALSE");

                // 受注配達テーブル検索（受注伝票データ取得）
                S04T001TC0815W11Entity acptdnpyEntity =
                    s04t001tDao.selectByC0815W11Acptdnpydata(s04t001tEntitykey);

                List<C0815W11AcptDlvryMeisaiDto> acptdlvrymeisaiDtoList = new ArrayList<>();

                // 納品先情報を取得
                S02M013TEntityKey tksmKey = new S02M013TEntityKey();
                tksmKey.setApkihoncd(dlvryjissekiDto.getApkihoncd());
                tksmKey.setShopcd(dlvryjissekiDto.getShopcd());
                tksmKey.setTokuisakicd(dlvryjissekiDto.getTokuisakicd());
                S02M013TEntity tksmEntity = s02m013tDao.selectByPrimaryKey(tksmKey);

                String nohinsakijoho1 =
                    !StringUtils.isEmpty(tksmEntity.getNohinsakijohon1())
                        ? s02m006tService.searchForMoji(CommonConstants.APCD_000,
                            CommonConstants.SYS_SHOPCD_000, CommonConstants.SYS_SETTEINO_056,
                            tksmEntity.getNohinsakijohon1())
                        : SystemConstants.WORD_BLANK;
                String nohinsakijoho2 =
                    !StringUtils.isEmpty(tksmEntity.getNohinsakijohon2())
                        ? s02m006tService.searchForMoji(CommonConstants.APCD_000,
                            CommonConstants.SYS_SHOPCD_000, CommonConstants.SYS_SETTEINO_057,
                            tksmEntity.getNohinsakijohon2())
                        : SystemConstants.WORD_BLANK;

                // 画面で明細データの修正を行っていない場合、DBから対象データを取得する。
                if (!(CommonConstants.FLG_ON.equals(dlvryjissekiDto.getShuseiarinashiflg())
                    && StringUtils.isEmpty(dlvryjissekiDto.getMidlvrykbn()))) {

                    // 受注配達明細テーブル検索
                    List<S04T002TC0815W11Entity> acptmeisaiList =
                        getDetailEntityList(dlvryjissekiDto);

                    for (S04T002TC0815W11Entity entity : acptmeisaiList) {

                        // 処理対象の明細データより、入力明細フラグが"1：入力行"のデータのみ処理対象とする。（セット商品の２重計上を避ける）
                        if (CommonConstants.NYURYOKUMEISAIFLG_1
                            .equals(entity.getNyuryokumeisaiflg())) {
                            C0815W11AcptDlvryMeisaiDto dto = new C0815W11AcptDlvryMeisaiDto();
                            String suryo = "";
                            String hanbaiprice = "";
                            String kin = "";
                            String shohitaxkin = "";
                            if (CommonConstants.DNPYSTS_12.equals(acptdnpyEntity.getDnpysts())) {
                                suryo = entity.getNohinsuryo();
                                hanbaiprice = entity.getNohinunt();
                                kin = entity.getNohinkin();
                                shohitaxkin = entity.getNohinshohitaxkin();
                            } else {
                                suryo = entity.getAcptsuryo();
                                hanbaiprice = entity.getAcptunt();
                                kin = entity.getAcptkin();
                                shohitaxkin = entity.getAcptshohitaxkin();
                            }

                            dto.setAcptdnpymeisaino(
                                Objects.toString(entity.getAcptdnpymeisaino(), "")); // 受注伝票明細番号
                            dto.setAcptdnpymeisaiedano(
                                Objects.toString(entity.getAcptdnpymeisaiedano(), "")); // 受注伝票明細枝番号
                            dto.setLctgrycd(entity.getLctgrycd()); // 大分類
                            dto.setMctgrycd(entity.getMctgrycd()); // 中分類
                            // 配布物明細でない場合、商品コード、商品名称を設定する。
                            if (StringUtils.isEmpty(entity.getHaifubutsucd())) {
                                dto.setShncd(entity.getShncd()); // 商品コード
                                dto.setShnnm(entity.getShnnmryakusho()); // 商品名称
                            } else {
                                dto.setShncd(entity.getHaifubutsucd()); // 配布物コード
                                dto.setShnnm(entity.getHaifubutsunm()); // 配布物名称
                            }
                            dto.setKeijokbn(entity.getKeijokbn()); // 計上区分
                            dto.setKeijokbnnm(entity.getKeijokbnnm()); // 計上区分名称
                            dto.setTaxkbn(entity.getTaxkbn()); // 税区分
                            dto.setSuryo(suryo); // 数量
                            dto.setTani(entity.getUritrtskitani()); // 単位
                            dto.setHyojunprice(entity.getHyojununt()); // 標準単価
                            dto.setHanbaiprice(hanbaiprice); // 販売価格
                            dto.setKin(kin); // 金額
                            dto.setShohitaxkin(shohitaxkin); // 消費税金額
                            dto.setMeisaibiko(entity.getMeisaibiko()); // 明細備考
                            dto.setIro(entity.getIro()); // 色
                            dto.setSz(entity.getSz()); // サイズ
                            dto.setKatasiki(entity.getKatasiki()); // 型式
                            dto.setIrisu(entity.getUriirisu()); // 入数
                            dto.setIrisutani(entity.getUritani()); // 入数単位
                            dto.setSokazu(entity.getSokazu()); // 総数
                            dto.setShntaxkbn(entity.getShntaxkbn()); // 商品税区分

                            acptdlvrymeisaiDtoList.add(dto);
                        }
                    }
                } else {
                    acptdlvrymeisaiDtoList = dlvryjissekiDto.getAcptdlvrymeisaiList();
                    int kazeikinTotal = 0;
                    int keigenkinTotal = 0;
                    if (CommonConstants.FLG_ON.equals(dlvryjissekiDto.getAcptthdaibikiflg())
                        || CommonConstants.FLG_ON.equals(dlvryjissekiDto.getSingldnpyflg())) {
                        for (C0815W11AcptDlvryMeisaiDto acptDlvryMeisaiDto : dlvryjissekiDto
                            .getAcptdlvrymeisaiList()) {
                            if (CommonConstants.KBN_TAX_KAZEI
                                .equals(acptDlvryMeisaiDto.getShntaxkbn())) {
                                kazeikinTotal += Integer.valueOf(acptDlvryMeisaiDto.getKin());
                            }
                            if (CommonConstants.KBN_TAX_KEIGEN
                                .equals(acptDlvryMeisaiDto.getShntaxkbn())) {
                                keigenkinTotal += Integer.valueOf(acptDlvryMeisaiDto.getKin());
                            }
                        }

                        // 課税金額合計の消費税金額を計算する
                        int kazeitaxkin = calculateTax(dlvryjissekiDto.getApkihoncd(),
                            dlvryjissekiDto.getShopcd(), dlvryjissekiDto.getDlvrydd(),
                            String.valueOf(kazeikinTotal), CommonConstants.KBN_TAX_KAZEI);
                        // 軽減課税金額合計の消費税金額を計算する
                        int keigentaxkin = calculateTax(dlvryjissekiDto.getApkihoncd(),
                            dlvryjissekiDto.getShopcd(), dlvryjissekiDto.getDlvrydd(),
                            String.valueOf(keigenkinTotal), CommonConstants.KBN_TAX_KEIGEN);

                        if (CommonConstants.DNPYSTS_12.equals(acptdnpyEntity.getDnpysts())) {
                            acptdnpyEntity
                                .setNohinkin(String.valueOf(kazeikinTotal + keigenkinTotal));
                            acptdnpyEntity
                                .setNohinshohitaxkin(String.valueOf(kazeitaxkin + keigentaxkin));
                        } else {
                            acptdnpyEntity
                                .setAcptkin(String.valueOf(kazeikinTotal + keigenkinTotal));
                            acptdnpyEntity
                                .setAcptshohitaxkin(String.valueOf(kazeitaxkin + keigentaxkin));
                        }
                    } else {
                        int nohinkin = 0;
                        int nohintaxkin = 0;
                        int acptkin = 0;
                        int acpttaxkin = 0;
                        for (C0815W11AcptDlvryMeisaiDto acptDlvryMeisaiDto : dlvryjissekiDto
                            .getAcptdlvrymeisaiList()) {
                            int kin =
                                Integer.valueOf(ItemUtil.removeComma(acptDlvryMeisaiDto.getKin()));
                            int taxkin = Integer
                                .valueOf(ItemUtil.removeComma(acptDlvryMeisaiDto.getShohitaxkin()));
                            if (CommonConstants.DNPYSTS_12.equals(acptdnpyEntity.getDnpysts())) {
                                nohinkin += kin;
                                nohintaxkin += taxkin;
                            } else {
                                acptkin += kin;
                                acpttaxkin += taxkin;
                            }
                        }
                        if (CommonConstants.DNPYSTS_12.equals(acptdnpyEntity.getDnpysts())) {
                            acptdnpyEntity.setNohinkin(String.valueOf(nohinkin));
                            acptdnpyEntity.setNohinshohitaxkin(String.valueOf(nohintaxkin));
                        } else {
                            acptdnpyEntity.setAcptkin(String.valueOf(acptkin));
                            acptdnpyEntity.setAcptshohitaxkin(String.valueOf(acpttaxkin));
                        }
                    }
                }

                for (C0815W11AcptDlvryMeisaiDto acptdlvrymeisaiDto : acptdlvrymeisaiDtoList) {
                    C0815R12CsvOutputDto csvoutputDto = new C0815R12CsvOutputDto<>();
                    // AP基本コード
                    csvoutputDto.setDnpyapkihoncd(nullToBlank(dlvryjissekiDto.getApkihoncd()));
                    // ショップコード
                    csvoutputDto.setDnpyshopcd(nullToBlank(dlvryjissekiDto.getShopcd()));
                    // 伝票番号
                    csvoutputDto.setDnpynomei(nullToBlank(dlvryjissekiDto.getAcptdnpyno()));
                    // 得意先コード
                    csvoutputDto.setTokuisakicdmei(nullToBlank(dlvryjissekiDto.getTokuisakicd()));
                    // 得意先名称
                    csvoutputDto.setTokuisakinmmei(nullToBlank(dlvryjissekiDto.getTokuisakinmn()));
                    // 配達日
                    csvoutputDto.setDlvryddmei(nullToBlank(dlvryjissekiDto.getDlvrydd()));
                    // 未配達区分
                    csvoutputDto.setMidlvrykbn(nullToBlank(dlvryjissekiDto.getMidlvrykbnnm()));
                    // 次回配達予定日
                    csvoutputDto.setHdnjikaidd(nullToBlank(dlvryjissekiDto.getJikaidlvryytidd()));
                    // 配達担当者コード
                    csvoutputDto.setDlvrytnshacd(nullToBlank(dlvryjissekiDto.getDlvrytnshacd()));
                    // 配達担当者名称
                    csvoutputDto.setDlvrytnshamei(nullToBlank(dlvryjissekiDto.getDlvrytnshamei()));
                    // 回収実績ボトルレギュラー
                    csvoutputDto.setKaishun1n2rittorukaishu(nullToBlank(
                        ItemUtil.removeComma(dlvryjissekiDto.getKaishun1n2rittorukaishu())));
                    // 回収実績ボトルスリム
                    csvoutputDto.setKaishun7rittorukaishu(nullToBlank(
                        ItemUtil.removeComma(dlvryjissekiDto.getKaishun7rittorukaishu())));
                    // 受注日
                    csvoutputDto.setAcptdd(nullToBlank(dlvryjissekiDto.getAcptdd()));
                    // 受注時代引きフラグ
                    csvoutputDto
                        .setAcptthdaibikiflg(nullToBlank(dlvryjissekiDto.getAcptthdaibikiflg()));
                    // 配達拠点コード
                    csvoutputDto.setDlvrykyotencd(nullToBlank(acptdnpyEntity.getDlvrykyotencd()));
                    // 配達会社名称
                    csvoutputDto.setDlvrycmpnynm(nullToBlank(acptdnpyEntity.getDlvrycmpnynm()));
                    // 配達エリアコード
                    csvoutputDto.setDlvryareacd(nullToBlank(acptdnpyEntity.getDlvryareacd()));
                    // 受注区分
                    csvoutputDto.setAcptkbn(nullToBlank(acptdnpyEntity.getAcptkbn()));
                    // 回収予定ボトルレギュラー
                    csvoutputDto.setKzbtln1n2rittoruhns(nullToBlank(
                        ItemUtil.removeComma(acptdnpyEntity.getKaishun1n2rittorukaishu())));
                    // 回収予定ボトルスリム
                    csvoutputDto.setKzbtln7rittoruhns(nullToBlank(
                        ItemUtil.removeComma(acptdnpyEntity.getKaishun7rittorukaishu())));
                    // 受注区分名称
                    csvoutputDto.setAcptkbnshs(nullToBlank(acptdnpyEntity.getAcptkbnnm()));
                    // 配達パターン
                    csvoutputDto.setDlvryptnnm(nullToBlank(acptdnpyEntity.getDlvryptn()));
                    // 配達予定日
                    csvoutputDto.setDlvryytidd(nullToBlank(acptdnpyEntity.getDlvryytidd()));
                    // 配達時刻
                    csvoutputDto.setDlvrykibojikan(nullToBlank(acptdnpyEntity.getDlvrykibojikan()));
                    // 納品先情報１
                    csvoutputDto
                        .setNohinsakijohon1(nullToBlank(acptdnpyEntity.getNohinsakijohon1()));
                    // 納品先情報２
                    csvoutputDto
                        .setNohinsakijohon2(nullToBlank(acptdnpyEntity.getNohinsakijohon2()));
                    // 配布物明細でない場合、商品コード、商品名称を設定する。
                    if (StringUtils.isEmpty(acptdlvrymeisaiDto.getHaifubutsucd())) {
                        // 商品コード
                        csvoutputDto.setShncd(nullToBlank(acptdlvrymeisaiDto.getShncd()));
                        // 商品名称
                        csvoutputDto.setShnnm(nullToBlank(acptdlvrymeisaiDto.getShnnm()));
                    } else {
                        // 配布物明細の場合、配布物コード、配布物名称を設定する。
                        // 配布物コード
                        csvoutputDto.setShncd(nullToBlank(acptdlvrymeisaiDto.getHaifubutsucd()));
                        // 配布物名称
                        csvoutputDto.setShnnm(nullToBlank(acptdlvrymeisaiDto.getHaifubutsunm()));
                    }
                    // 計上区分
                    csvoutputDto.setKeijokbnnm(nullToBlank(acptdlvrymeisaiDto.getKeijokbnnm()));
                    // 数量
                    // 配布物明細でない場合、明細Dtoに設定されている数量を設定する。
                    if (StringUtils.isEmpty(acptdlvrymeisaiDto.getHaifubutsucd())) {
                        csvoutputDto.setAcptsuryo(
                            nullToBlank(ItemUtil.removeComma(acptdlvrymeisaiDto.getSuryo())));
                    } else {
                        // 配布物明細の場合、"1"固定で設定する。
                        csvoutputDto.setAcptsuryo("1");
                    }
                    // 数量単位
                    csvoutputDto.setSuryotani(nullToBlank(acptdlvrymeisaiDto.getTani()));
                    // 標準価格
                    csvoutputDto.setHyojunprice(
                        nullToBlank(ItemUtil.removeComma(acptdlvrymeisaiDto.getHyojunprice())));
                    // 販売価格
                    csvoutputDto.setHanbaipricemei(
                        nullToBlank(ItemUtil.removeComma(acptdlvrymeisaiDto.getHanbaiprice())));
                    // 金額
                    csvoutputDto
                        .setKin(nullToBlank(ItemUtil.removeComma(acptdlvrymeisaiDto.getKin())));
                    // 明細備考
                    csvoutputDto.setMeisaibiko(nullToBlank(acptdlvrymeisaiDto.getMeisaibiko()));
                    // 色
                    csvoutputDto.setIronmmei(nullToBlank(acptdlvrymeisaiDto.getIro()));
                    // サイズ
                    csvoutputDto.setSznmmei(nullToBlank(acptdlvrymeisaiDto.getSz()));
                    // 型式
                    csvoutputDto.setKatasikimei(nullToBlank(acptdlvrymeisaiDto.getKatasiki()));
                    // 入数
                    csvoutputDto.setUriirisu(
                        nullToBlank(ItemUtil.removeComma(acptdlvrymeisaiDto.getIrisu())));
                    // 入数単位
                    csvoutputDto.setUritani(nullToBlank(acptdlvrymeisaiDto.getIrisutani()));
                    // 総数
                    csvoutputDto.setSokazu(
                        nullToBlank(ItemUtil.removeComma(acptdlvrymeisaiDto.getSokazu())));
                    // 納品先情報１
                    csvoutputDto.setNohinsakijohon1(nohinsakijoho1);
                    // 納品先情報２
                    csvoutputDto.setNohinsakijohon2(nohinsakijoho2);

                    csvoutputDtoList.add(csvoutputDto);
                }
            }
        }
        returnDto.setPrintList(csvoutputDtoList);
        return returnDto;
    }

    /**
     * 相関チェック（トランザクションあり）
     * 
     * @param dtoList List<C0815W11DlvryJissekiDto>
     */
    public void checkParam(List<C0815W11DlvryJissekiDto> dtoList, boolean isPrnt) {
        if (!ListUtils.isEmpty(dtoList)) {
            int index = 1;
            for (C0815W11DlvryJissekiDto meisaiDto : dtoList) {
                if (CommonConstants.FLG_ON.equals(meisaiDto.getKshntrgtflg())) {
                    int currentPage = (index - 1) / PAGEABLE_SIZE;
                    int inIndex = index - (currentPage * PAGEABLE_SIZE);
                    if (isPrnt) {
                        S04T001TEntityKey s04t001tEntityKey = new S04T001TEntityKey();
                        s04t001tEntityKey.setApkihoncd(meisaiDto.getApkihoncd());
                        s04t001tEntityKey.setShopcd(meisaiDto.getShopcd());
                        s04t001tEntityKey.setAcptdnpyno(meisaiDto.getAcptdnpyno());
                        s04t001tEntityKey.setEdano(Short.valueOf(meisaiDto.getAcptedano()));
                        S04T001TEntity e = s04t001tDao.selectByPrimaryKey(s04t001tEntityKey);
                        // 対象の伝票の伝票ステータスが「02：受注」の場合エラー
                        if (CommonConstants.DNPYSTS_02.equals(e.getDnpysts())) {
                            String field =
                                String.format("meisaiBeanList[%d].kshntrgtflg", inIndex - 1);
                            FormField formField = new FormField(field, "更新対象", currentPage);
                            throw BusinessExceptionFactory.validationFailed(formField,
                                MessageID.W090);
                        }
                    }
                    S02M013TEntity s02m013tEntity = selectS02M013TEntity(meisaiDto);
                    if (!ObjectUtils.isEmpty(s02m013tEntity)) {
                        // 委託相手コード存在チェック（委託）：委受託関係の伝票を更新する場合で委託側の委託相手情報が未設定の場合エラー
                        if ((CommonConstants.JUTAKUDLVRYCSTKBN_3
                            .equals(s02m013tEntity.getJutakudlvrycstkbn())
                            || CommonConstants.JUTAKUDLVRYCSTKBN_4
                                .equals(s02m013tEntity.getJutakudlvrycstkbn()))
                            && (StringUtils.isEmpty(s02m013tEntity.getItakuaiteapkihoncd())
                                || StringUtils.isEmpty(s02m013tEntity.getItakuaiteshopcd())
                                || StringUtils.isEmpty(s02m013tEntity.getItakuaitetokuisakicd()))) {
                            FormField formField = new FormField("", "");
                            throw BusinessExceptionFactory.validationFailed(formField,
                                MessageID.W236);
                        }
                        // 委託相手コード存在チェック（受託）：委受託関係の伝票を更新する場合で受託側の委託相手情報が未設定の場合エラー
                        if ((CommonConstants.JUTAKUDLVRYCSTKBN_5
                            .equals(s02m013tEntity.getJutakudlvrycstkbn())
                            || CommonConstants.JUTAKUDLVRYCSTKBN_6
                                .equals(s02m013tEntity.getJutakudlvrycstkbn()))
                            && (StringUtils.isEmpty(s02m013tEntity.getItakuaiteapkihoncd())
                                || StringUtils.isEmpty(s02m013tEntity.getItakuaiteshopcd())
                                || StringUtils.isEmpty(s02m013tEntity.getItakuaitetokuisakicd()))) {
                            FormField formField = new FormField("", "");
                            throw BusinessExceptionFactory.validationFailed(formField,
                                MessageID.W238);
                        }

                        // 委受託相手の得意先取得
                        List<S02M013TEntity> s02m013tItakuaiteEntity =
                            selectS02M013TEntityForItakuaite(s02m013tEntity);
                        // 得意先マスタ存在チェック（委託）：委受託関係の伝票を更新する場合で受託側の得意先マスタが存在しない場合エラー
                        if ((CommonConstants.JUTAKUDLVRYCSTKBN_3
                            .equals(s02m013tEntity.getJutakudlvrycstkbn())
                            || CommonConstants.JUTAKUDLVRYCSTKBN_4
                                .equals(s02m013tEntity.getJutakudlvrycstkbn()))
                            && ListUtils.isEmpty(s02m013tItakuaiteEntity)) {
                            FormField formField = new FormField("", "");
                            throw BusinessExceptionFactory.validationFailed(formField,
                                MessageID.W237);
                        }
                        // 得意先マスタ存在チェック（受託）：委受託関係の伝票を更新する場合で委託側の得意先マスタが存在しない場合エラー
                        if ((CommonConstants.JUTAKUDLVRYCSTKBN_5
                            .equals(s02m013tEntity.getJutakudlvrycstkbn())
                            || CommonConstants.JUTAKUDLVRYCSTKBN_6
                                .equals(s02m013tEntity.getJutakudlvrycstkbn()))
                            && ListUtils.isEmpty(s02m013tItakuaiteEntity)) {
                            FormField formField = new FormField("", "");
                            throw BusinessExceptionFactory.validationFailed(formField,
                                MessageID.W239);
                        }
                        if (!ListUtils.isEmpty(s02m013tItakuaiteEntity)) {
                            // 委託側相手得意先の紐づきチェック
                            // 委受託関係の伝票を更新する場合で委託側の得意先が受託側の得意先と紐付かない場合エラー
                            if ((CommonConstants.JUTAKUDLVRYCSTKBN_3
                                .equals(s02m013tEntity.getJutakudlvrycstkbn())
                                || CommonConstants.JUTAKUDLVRYCSTKBN_4
                                    .equals(s02m013tEntity.getJutakudlvrycstkbn()))
                                && (!s02m013tEntity.getApkihoncd()
                                    .equals(s02m013tItakuaiteEntity.get(0).getItakuaiteapkihoncd())
                                    || !s02m013tEntity.getShopcd()
                                        .equals(s02m013tItakuaiteEntity.get(0).getItakuaiteshopcd())
                                    || !s02m013tEntity.getTokuisakicd()
                                        .equals(s02m013tItakuaiteEntity.get(0)
                                            .getItakuaitetokuisakicd()))) {
                                FormField formField = new FormField("", "");
                                throw BusinessExceptionFactory.validationFailed(formField,
                                    MessageID.W240);
                            }

                            // 受託側相手得意先の紐づきチェック
                            // 委受託関係の伝票を更新する場合で受託側の得意先が委託側の得意先と紐付かない場合エラー
                            if ((CommonConstants.JUTAKUDLVRYCSTKBN_5
                                .equals(s02m013tEntity.getJutakudlvrycstkbn())
                                || CommonConstants.JUTAKUDLVRYCSTKBN_6
                                    .equals(s02m013tEntity.getJutakudlvrycstkbn()))
                                && (!s02m013tEntity.getApkihoncd()
                                    .equals(s02m013tItakuaiteEntity.get(0).getItakuaiteapkihoncd())
                                    || !s02m013tEntity.getShopcd()
                                        .equals(s02m013tItakuaiteEntity.get(0).getItakuaiteshopcd())
                                    || !s02m013tEntity.getTokuisakicd()
                                        .equals(s02m013tItakuaiteEntity.get(0)
                                            .getItakuaitetokuisakicd()))) {
                                FormField formField = new FormField("", "");
                                throw BusinessExceptionFactory.validationFailed(formField,
                                    MessageID.W240);
                            }
                        }
                    }

                    S04T001TEntity selectS04t001tEntity = selectS04T001TEntity(meisaiDto);
                    S04T001TEntity s04t001tEntity =
                        !ObjectUtils.isEmpty(selectS04t001tEntity) ? selectS04t001tEntity
                            : new S04T001TEntity();
                    // 更新時受注配達テーブル排他チェック：更新システム日付または更新システム時間が検索時と異なる場合、エラー
                    // （s04t001tEntityが空の場合、一覧検索後に更新された削除されたデータになるため、排他エラーになる）
                    if (!isPrnt && (!Objects.equals(meisaiDto.getKshnsystmymd(),
                        s04t001tEntity.getKshnsystmymd())
                        || !Objects.equals(meisaiDto.getKshnsystmjikan(),
                            s04t001tEntity.getKshnsystmjikan()))) {
                        c0815w00Session.getMeisaiBeanList()
                            .forEach(meisai -> meisai.setKshntrgtflg(""));
                        FormField formField = new FormField("", "更新時受注配達テーブル排他チェック");
                        throw BusinessExceptionFactory.validationFailed(formField, MessageID.W090);
                    }

                    // ACCIS2023-7154 対応
                    // 更新以外の場合はここでチェックする
                    if (isPrnt) {
                        // 配達日が商品の販売期間内かどうかをチェック
                        ensureSalesPeriodOfDetail(meisaiDto);
                    }

                    String dnpysts = s04t001tEntity.getDnpysts();
                    // 配達売上済の伝票のみを対象とする
                    if (!CommonConstants.DNPYSTS_12.equals(dnpysts)) {
                        return;
                    }

                    // 更新処理時のみチェックする（印刷、CSV出力処理はチェックしない）
                    if (!isPrnt) {
                        // 代引き実績済の伝票更新チェック：対象伝票が配達売上済で受注時代引フラグまたは単体伝票締めフラグがONの場合、エラー
                        if ((CommonConstants.FLG_ON.equals(s04t001tEntity.getAcptthdaibikiflg())
                            || CommonConstants.FLG_ON.equals(s04t001tEntity.getSingldnpyflg()))) {
                            FormField formField = new FormField("", "");
                            throw BusinessExceptionFactory.validationFailed(formField,
                                MessageID.W260, "受注伝票番号:" + meisaiDto.getAcptdnpyno()
                                    + "は、代引き実績済みのため更新できません。受注登録にて修正伝票を作成してください。");
                        }
                    }

                    // 売上情報と連携している伝票のみを対象とする
                    if (StringUtils.isEmpty(s04t001tEntity.getUridnpyno())) {
                        return;
                    }
                    S06T001TEntity s06t001tEntity = selectS06T001TEntity(meisaiDto, s04t001tEntity);
                    if (ObjectUtils.isEmpty(s06t001tEntity)) {
                        return;
                    }

                    // 配達売上済時チェック
                    String invovoiceShikoymd = getInvoiceDay(meisaiDto);
                    String dlvrydd = meisaiDto.getDlvrydd();
                    String oldDlvrydd = meisaiDto.getShokidspdlvrydd();
                    // 配達日を変更していてかつ、インボイス施行日以前
                    if (!Objects.equals(dlvrydd, oldDlvrydd)
                        && !StringUtils.isEmpty(invovoiceShikoymd)
                        && DateTimeUtil.isLater(dlvrydd, invovoiceShikoymd)) {
                        List<S07T001TEntity> s07t001tEntity = selectS07T001TEntity(meisaiDto,
                            s02m013tEntity, s04t001tEntity, s06t001tEntity);
                        // 入金消込済みチェック：インボイス施行日前かつ配達売上済で入金消込済み状態の伝票を更新する場合エラー
                        if (!ListUtils.isEmpty(s07t001tEntity)
                            && s07t001tEntity.get(0).getKeshikomikin() != 0) {
                            FormField formField = new FormField("", "");
                            throw BusinessExceptionFactory.validationFailed(formField,
                                MessageID.W230, meisaiDto.getAcptdnpyno(), "「入金消込済みデータ」");
                        }
                        // 請求済みチェック１：インボイス施行日前かつ配達売上済で請求締めが完了している
                        // （請求残高テーブル．最新の請求締年月日）≠売上テーブル．請求締年月日）状態の伝票を更新する場合エラー
                        String maxShimeymd = getMaxSkyshimeymd(meisaiDto, s02m013tEntity);
                        String skyshimeymd = new StringBuilder()
                            .append(BusinessUtil.getString(s06t001tEntity.getSkyshimedydm(),
                                SystemConstants.WORD_BLANK))
                            .append(BusinessUtil.getString(s06t001tEntity.getSkyshimedd(),
                                SystemConstants.WORD_BLANK))
                            .toString();
                        if ((!CommonConstants.DEF_DATE_00000000
                            .equals(s06t001tEntity.getSkyprcsjissidd())
                            && !CommonConstants.DAIBIKI.equals(s06t001tEntity.getSkyprcsjissidd()))
                            && ((StringUtils.isEmpty(maxShimeymd))
                                || !maxShimeymd.equals(skyshimeymd))) {
                            FormField formField = new FormField("", "");
                            throw BusinessExceptionFactory.validationFailed(formField,
                                MessageID.W230, meisaiDto.getAcptdnpyno(), "「請求済みデータ」");
                        }
                    }

                    // 配達日を変更していてかつ、インボイス施行日以後
                    if (!Objects.equals(dlvrydd, oldDlvrydd)
                        && !StringUtils.isEmpty(invovoiceShikoymd)
                        && DateTimeUtil.isLater(invovoiceShikoymd, dlvrydd)) {
                        // 請求締チェック：インボイス施行日後かつ配達売上済で請求締めが完了している（売上テーブル．請求締年月≠“000000”かつ、
                        // ≠NULL）状態の伝票を更新する場合エラー
                        List<S06T001TEntity> s06t001tEntityList =
                            selectS06T001TEntityList(meisaiDto);
                        boolean skyshimecheck = false;
                        for (S06T001TEntity inEntity : s06t001tEntityList) {

                            String skyshimeymd = new StringBuilder()
                                .append(Objects.toString(inEntity.getSkyshimedydm(), ""))
                                .append(Objects.toString(inEntity.getSkyshimedd(), "")).toString();

                            if (!StringUtils.isEmpty(skyshimeymd)
                                && !CommonConstants.DEF_DATE_00000000.equals(skyshimeymd)) {
                                skyshimecheck = true;
                                break;
                            }
                        }
                        if (skyshimecheck) {
                            FormField formField = new FormField("", "");
                            throw BusinessExceptionFactory.validationFailed(formField,
                                MessageID.W260, "請求締めがされている為、修正・取消はできません。締め解除を行った後に、再度修正をしてください。");
                        }
                    }
                }
            }
            index++;
        }
    }

    /**
     * 指定された受注データ内の明細商品の販売期間のチェックを行う
     * 
     * @param acptdnpyDto 受注データ
     */
    private void ensureSalesPeriodOfDetail(C0815W11DlvryJissekiDto acptdnpyDto) {
        log.debug("★★★ ensureSalesPeriodOfDetailが呼ばれた");
        // 受注配達明細テーブルから必要な情報を取得
        S04T001TEntity acptdnpyEntity = selectS04T001TEntity(acptdnpyDto);
        log.debug("委託相手受注伝票番号 : {}", acptdnpyEntity.getItakuaiteacptdnpyno());
        acptdnpyDto.getAcptdlvrymeisaiList().stream()
            // 商品行のみにフィルタ
            .map(detailDto -> {
                log.debug("明細番号 : {}", detailDto.getAcptdnpymeisaino());
                log.debug("明細区分 : {}", detailDto.getMeisaikbn());
                return detailDto;
            })
            .filter(detailDto -> CommonConstants.KBN_MEISAI_SHOHIN.equals(detailDto.getMeisaikbn()))
            .forEach(detailDto -> {
                log.debug("商品コード : {}", detailDto.getShncd());
                // 商品マスタビューから商品情報を取得
                S02V001TEntityExample s02v001tExample = new S02V001TEntityExample();
                s02v001tExample.createCriteria().andApkihoncdEqualTo(acptdnpyEntity.getApkihoncd())
                    .andShopcdEqualTo(acptdnpyEntity.getShopcd())
                    .andShncdEqualTo(detailDto.getShncd())
                    .andKihonshnyukokbnEqualTo(CommonConstants.YUKOKBN_1);
                S02V001TEntity productEntity = s02v001tDao.selectByExample(s02v001tExample).stream()
                    .findFirst().orElseGet(S02V001TEntity::new);
                // 販売期間をチェック
                ensureSalePeriodValid(acptdnpyEntity.getAcptdnpyno(), acptdnpyDto.getDlvrydd(),
                    productEntity, detailDto);
                // 委託相手の受注がある場合
                if (!StringUtils.isEmpty(acptdnpyEntity.getItakuaiteacptdnpyno())) {
                    // 委託相手の商品情報を取得
                    s02v001tExample.clear();
                    s02v001tExample.createCriteria()
                        .andApkihoncdEqualTo(acptdnpyEntity.getItakuaiteapkihoncd())
                        .andShopcdEqualTo(acptdnpyEntity.getItakuaiteshopcd())
                        .andShncdEqualTo(detailDto.getShncd())
                        .andKihonshnyukokbnEqualTo(CommonConstants.YUKOKBN_1);
                    productEntity = s02v001tDao.selectByExample(s02v001tExample).stream()
                        .findFirst().orElseGet(S02V001TEntity::new);
                    // 販売期間をチェック
                    ensureConsigneeProductSalePeriodValid(acptdnpyEntity.getAcptdnpyno(),
                        acptdnpyDto.getDlvrydd(), productEntity, detailDto);
                }
            });
    }

    /**
     * 配達日が商品の販売期間内かどうかをチェックする.
     * 
     * @param acptdnpyno 受注伝票番号
     * @param dlvrydd 配達日
     * @param index 画面上での行番号
     * @param productEntity 商品情報
     * @param detailDto 明細情報
     */
    private void ensureSalePeriodValid(String acptdnpyno, String dlvrydd,
        S02V001TEntity productEntity, C0815W11AcptDlvryMeisaiDto detailDto) {
        validateSalePeriod(dlvrydd, productEntity, detailDto.getSuryo(),
            String.format("販売期間外です。（受注伝票番号:%s）", acptdnpyno));
    }

    /**
     * 配達日が委託相手の商品の販売期間内かどうかをチェックする.
     * 
     * @param acptdnpyno 受注伝票番号
     * @param dlvrydd 配達日
     * @param productEntity 商品情報
     * @param detailDto 明細情報
     */
    private void ensureConsigneeProductSalePeriodValid(String acptdnpyno, String dlvrydd,
        S02V001TEntity productEntity, C0815W11AcptDlvryMeisaiDto detailDto) {
        validateSalePeriod(dlvrydd, productEntity, detailDto.getSuryo(),
            String.format("委託相手の商品が販売期間外です。（受注伝票番号:%s）", acptdnpyno));
    }

    /**
     * 配達日が商品の販売期間内かどうかをチェックする.
     * 
     * @param dlvrydd 配達日
     * @param productEntity 商品情報
     * @param detailQuantity 明細数量
     * @param messageArg メッセージの可変要素
     */
    private void validateSalePeriod(String dlvrydd, S02V001TEntity productEntity,
        String detailQuantity, String messageArg) {
        log.debug("*** ACCIS2023-7154 対応 : 販売期間チェック ***");
        log.debug("配達日 : {}", dlvrydd);
        log.debug("商品コード : {}", productEntity.getShncd());
        log.debug("販売開始日 : {}", productEntity.getHanbaistrtdd());
        log.debug("販売終了日 : {}", productEntity.getHanbaishrydd());
        log.debug("明細数量 : {}", detailQuantity);
        log.debug("メッセージパラメータ : {}", messageArg);
        BigDecimal quantity = util.getDecimalValue(detailQuantity);
        // 明細の数量が0の場合はチェック不要
        if (BigDecimal.ZERO.equals(quantity)) {
            return;
        }
        // 配達日が商品の販売期間に含まれているかをチェック
        if (!util.isDateBetween(dlvrydd, productEntity.getHanbaistrtdd(),
            productEntity.getHanbaishrydd())) {
            // 販売期間街の場合はエラー
            FormField field = new FormField("", "配達日");
            throw BusinessExceptionFactory.validationFailed(field, MessageID.W260, messageArg);
        }
    }

    /**
     * 請求締処理管理テーブルチェック
     * 
     * @param entity S07T004TEntity
     */
    private void checkSkyshimesyori(S07T004TEntity entity) {
        // 処理状態が「0:終了／待機」以外の場合
        if (!ObjectUtils.isEmpty(entity)
            && !CommonConstants.PRCSJOTAI_0.equals(entity.getPrcsjotai())) {
            // 処理モードが"3"（単体）の場合
            if (CommonConstants.KBN_PRCSKBN1_TANTAI.equals(entity.getPrcsmode())) {
                // 処理状態が"1"（処理開始）または"2"（処理中）の場合
                if (CommonConstants.PRCSJOTAI_1.equals(entity.getPrcsjotai())
                    || CommonConstants.PRCSJOTAI_RUNNING.equals(entity.getPrcsjotai())) {
                    FormField formField = new FormField("", "");
                    throw BusinessExceptionFactory.validationFailed(formField, MessageID.W260,
                        "処理中です。しばらくお待ちください。");
                    // 処理状態が"6"（リカバリ実行待）の場合
                } else if (CommonConstants.PRCSJOTAI_6.equals(entity.getPrcsjotai())) {
                    FormField formField = new FormField("", "");
                    throw BusinessExceptionFactory.validationFailed(formField, MessageID.W260,
                        "現在リカバリ実行待ちです。");
                    // 処理状態が"7"（リカバリ実行中）の場合
                } else if (CommonConstants.PRCSJOTAI_7.equals(entity.getPrcsjotai())) {
                    FormField formField = new FormField("", "");
                    throw BusinessExceptionFactory.validationFailed(formField, MessageID.W260,
                        "現在リカバリ実行中のため、処理を続行できません。");
                    // 処理状態が"9"（エラー）の場合
                } else if (CommonConstants.PRCSJOTAI_9.equals(entity.getPrcsjotai())) {
                    FormField formField = new FormField("", "");
                    throw BusinessExceptionFactory.validationFailed(formField, MessageID.W260,
                        "エラーで終了しています。エラーの原因を解消しリカバリ実行を行うか、データを戻して再度実行してください。");
                }
                // 処理モードが"3"（単体）以外の場合
            } else {
                FormField formField = new FormField("", "");
                throw BusinessExceptionFactory.validationFailed(formField, MessageID.W260,
                    "実行可能な状態ではないか、他の締め処理実行中のため、処理を続行できません。");
            }
        }
    }

    /**
     * 更新前チェックを実施する
     * 
     * @param dtoList List<C0815W11DlvryJissekiDto>
     */
    public void checkParamBeforeUpdate(List<C0815W11DlvryJissekiDto> dtoList,
        List<String> messageList) {
        if (!ListUtils.isEmpty(dtoList)) {
            for (C0815W11DlvryJissekiDto meisaiDto : dtoList) {
                if (CommonConstants.FLG_ON.equals(meisaiDto.getKshntrgtflg())) {

                    // ACCIS2023-7154 対応
                    // 配達日が商品の販売期間内かどうかをチェック
                    ensureSalesPeriodOfDetail(meisaiDto);

                    // インボイス施行日を取得
                    String invoiceDay = getInvoiceDay(meisaiDto);
                    S02M013TEntity s02m013tEntity = selectS02M013TEntity(meisaiDto);
                    S04T001TEntity s04t001tEntity = selectS04T001TEntity(meisaiDto);
                    // 配達日がインボイス施行日より前の場合
                    if (!util.isLater(meisaiDto.getDlvrydd(), invoiceDay)) {
                        // 伝票ステータスが「12:配達売上済」の場合
                        if (!ObjectUtils.isEmpty(s04t001tEntity)
                            && CommonConstants.DNPYSTS_12.equals(s04t001tEntity.getDnpysts())) {

                            // インボイス施行日前かつ配達売上済で消込が完了している（請求残高テーブル．消込金額＝０
                            // かつ、売上テーブル．請求処理実施日≠"00000000"）状態の伝票を更新する場合ワーニング
                            if (!StringUtils.isEmpty(s04t001tEntity.getUridnpyno()) && !Objects
                                .equals(meisaiDto.getDlvrydd(), meisaiDto.getShokidspdlvrydd())) {
                                S06T001TEntity s06t001tEntity =
                                    selectS06T001TEntity(meisaiDto, s04t001tEntity);
                                List<S07T001TEntity> s07t001tEntity = selectS07T001TEntity(
                                    meisaiDto, s02m013tEntity, s04t001tEntity, s06t001tEntity);
                                if (!ListUtils.isEmpty(s07t001tEntity)
                                    && s07t001tEntity.get(0).getKeshikomikin() == 0
                                    && (!CommonConstants.DEF_DATE_00000000
                                        .equals(s06t001tEntity.getSkyprcsjissidd())
                                        && !CommonConstants.DAIBIKI
                                            .equals(s06t001tEntity.getSkyprcsjissidd()))) {
                                    BusinessExceptionFactory.setMessageList(messageList,
                                        MessageID.W231);
                                }
                            }

                            // 配達売上済で委受託関係の伝票を更新する場合ワーニング
                            if (CommonConstants.JUTAKUDLVRYCSTKBN_5
                                .equals(s02m013tEntity.getJutakudlvrycstkbn())
                                || CommonConstants.JUTAKUDLVRYCSTKBN_6
                                    .equals(s02m013tEntity.getJutakudlvrycstkbn())) {
                                BusinessExceptionFactory.setMessageList(messageList,
                                    MessageID.W232);
                            }

                            // 配達売上済で商品にサーバー本体が存在する伝票を更新する場合ワーニング
                            S02M046TEntity s02m046tEntity = selectS02M046TEntity(meisaiDto);
                            String serverknrenLctgry = s02m006tService.searchForMoji(
                                CommonConstants.APCD_000, CommonConstants.SYS_SHOPCD_000,
                                CommonConstants.SYS_SETTEINO_096, CommonConstants.SETTEICD_096_4);
                            String serverknrenMctgry = s02m006tService.searchForMoji(
                                CommonConstants.APCD_000, CommonConstants.SYS_SHOPCD_000,
                                CommonConstants.SYS_SETTEINO_134, CommonConstants.SETTEICD_134_41);

                            if (CommonConstants.FLG_ON
                                .equals(s02m046tEntity.getTtstupwsvrenkeikbn())
                                && !CommonConstants.JUTAKUDLVRYCSTKBN_6
                                    .equals(s02m013tEntity.getJutakudlvrycstkbn())) {
                                if (!ListUtils.isEmpty(meisaiDto.getAcptdlvrymeisaiList())) {
                                    for (C0815W11AcptDlvryMeisaiDto acptdlvrymeisaiDto : meisaiDto
                                        .getAcptdlvrymeisaiList()) {

                                        S04T002TEntity s04t002tEntity =
                                            selectS04T002TEntity(meisaiDto, acptdlvrymeisaiDto);

                                        if (!ObjectUtils.isEmpty(s04t002tEntity)
                                            && CommonConstants.KBN_MEISAI_SHOHIN
                                                .equals(s04t002tEntity.getMeisaikbn())
                                            && CommonConstants.FLG_ON
                                                .equals(s04t002tEntity.getTanpinmeisaiflg())
                                            && serverknrenLctgry
                                                .equals(s04t002tEntity.getLctgrycd())
                                            && serverknrenMctgry
                                                .equals(s04t002tEntity.getMctgrycd())) {
                                            BusinessExceptionFactory.setMessageList(messageList,
                                                MessageID.W260,
                                                "伝票が変更されました。「得意先設置サーバー管理マスタ」の内容を確認、修正してください。<br>&nbsp;&nbsp;伝票番号："
                                                    + meisaiDto.getAcptdnpyno() + "　得意先コード："
                                                    + meisaiDto.getTokuisakicd());
                                        }
                                    }
                                }

                                // 配達売上済で初回設置の伝票を取り消す（明細の数量すべて０）場合ワーニング
                                if (CommonConstants.KBN_ACPT_SETTI
                                    .equals(s04t001tEntity.getAcptkbn())
                                    && CommonConstants.KBN_ACPTSYS_SHOKAISETTI
                                        .equals(s04t001tEntity.getAcptkbnshs())
                                    && CommonConstants.FLG_ON.equals(s02m013tEntity.getStartup())
                                    && CommonConstants.FLG_ON
                                        .equals(s02m013tEntity.getIyakukinkanriflg())) {

                                    boolean alarmIyakukin = true;
                                    for (C0815W11AcptDlvryMeisaiDto acptdlvrymeisaiDto : meisaiDto
                                        .getAcptdlvrymeisaiList()) {
                                        // 納品数量
                                        if (!"0".equals(acptdlvrymeisaiDto.getNohinsuryo())) {
                                            alarmIyakukin = false;
                                            break;
                                        }
                                    }
                                    if (alarmIyakukin) {
                                        BusinessExceptionFactory.setMessageList(messageList,
                                            MessageID.W260,
                                            "伝票が変更されました。「得意先別違約金管理マスタ」の内容を確認、修正してください。<br>&nbsp;&nbsp;伝票番号："
                                                + meisaiDto.getAcptdnpyno() + "　得意先コード："
                                                + meisaiDto.getTokuisakicd());
                                    }
                                }

                                // アラーム表示（得意先マスタ）
                                boolean alarmTKS = false;

                                // 設置
                                // 配達売上済で初回設置の伝票を更新する場合ワーニング
                                if (CommonConstants.KBN_ACPT_SETTI
                                    .equals(s04t001tEntity.getAcptkbn())
                                    && CommonConstants.KBN_ACPTSYS_SHOKAISETTI
                                        .equals(s04t001tEntity.getAcptkbnshs())
                                    && CommonConstants.FLG_ON.equals(s02m013tEntity.getStartup())) {
                                    alarmTKS = true;
                                }

                                // 引上
                                // 配達売上済で引上の伝票を更新する場合ワーニング
                                if (CommonConstants.KBN_ACPT_HIKIAGE
                                    .equals(s04t001tEntity.getAcptkbn()) // 受注区分：引上
                                    && (CommonConstants.KBN_ACPTSYS_SHIINHIKIAGE
                                        .equals(s04t001tEntity.getAcptkbnshs()) // 受注詳細区分：試飲引上／退会引上
                                        || CommonConstants.KBN_ACPTSYS_TAIKAIHIKIAGE
                                            .equals(s04t001tEntity.getAcptkbnshs()))) {
                                    alarmTKS = true;
                                }

                                // 配達関連
                                // 配達担当者実績更新区分が1：担当者or2：担当者・配達順のショップが配達売上済で持ち帰り以外の伝票を更新する場合ワーニング
                                if (!CommonConstants.KBN_DLVRY_MOCHIKAERI
                                    .equals(s04t001tEntity.getDlvrykbn()) // 配達区分≠持ち帰り
                                    && ("1".equals(s02m046tEntity.getDlvrytnshajissekikshnkbn()) // 配達担当者実績更新区分："1"／"2"
                                        || "2".equals(
                                            s02m046tEntity.getDlvrytnshajissekikshnkbn()))) {
                                    alarmTKS = true;
                                }

                                // 配達順
                                // 配達担当者実績更新区分が2：担当者・配達順のショップが配達売上済の伝票を更新する場合ワーニング
                                if ("2".equals(s02m046tEntity.getDlvrytnshajissekikshnkbn())) {
                                    alarmTKS = true;
                                }

                                // アラーム表示（得意先マスタ）
                                if (alarmTKS) {
                                    BusinessExceptionFactory.setMessageList(messageList,
                                        MessageID.W260,
                                        "伝票が変更されました。「得意先マスタ」の内容を確認、修正してください。<br>&nbsp;&nbsp;伝票番号："
                                            + meisaiDto.getAcptdnpyno() + " 得意先コード："
                                            + meisaiDto.getTokuisakicd());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 相関チェック（配達担当者一括変更時）
     * 
     * @param dto C0815W11SearchConditionDto
     */
    public void checkParamDlvrytnshaikkatsu(C0815W11SearchConditionDto dto) {
        S02M003TEntityExample example = new S02M003TEntityExample();
        S02M003TEntityExample.Criteria criteria = example.createCriteria();
        criteria.andApkihoncdEqualTo(dto.getApkihoncd());
        criteria.andShopcdEqualTo(dto.getShopcd());
        criteria.andTnshacdEqualTo(dto.getDlvrytnshacd());
        List<S02M003TEntity> results = s02m003tDao.selectByExample(example);

        // 担当者マスタが存在しない場合エラー
        if (ListUtils.isEmpty(results)) {
            FormField formField = new FormField("baseBean.dlvrytnshacd2", "配達担当者コード");
            throw BusinessExceptionFactory.validationFailed(formField, MessageID.W084, "配達担当者コード");
        }

        S02M003TEntity tnshaEntity = results.get(0);
        // 有効区分＝1(有効)でない場合はエラー
        if (!CommonConstants.YUKOKBN_1.equals(tnshaEntity.getYukokbn())) {
            FormField formField = new FormField("baseBean.dlvrytnshacd2", "配達担当者コード");
            throw BusinessExceptionFactory.validationFailed(formField, MessageID.W123, "配達担当者コード");
        }

        // 配達担当者区分＝1(配達担当者である)でない場合エラー
        if (!CommonConstants.DLVRYTNSHAKBN_YES.equals(tnshaEntity.getDlvrytnshakbn())) {
            FormField formField = new FormField("baseBean.dlvrytnshacd2", "配達担当者コード");
            throw BusinessExceptionFactory.validationFailed(formField, MessageID.W124, "配達担当者以外");
        }
    }

    /**
     * 検索処理結果を生成する
     * 
     * @param entity List<S04T001TC0815W11Entity>
     * @return List<C0815W11SearchResultDto>
     */
    private List<C0815W11SearchResultDto> createResultList(List<S04T001TC0815W11Entity> entity,
        String unyoymd) {
        List<C0815W11SearchResultDto> retList = new ArrayList<>();

        // 検索結果が0件の場合
        if (ListUtils.isEmpty(entity)) {
            throw BusinessExceptionFactory.dataNotFound(MessageID.W083);
        }

        for (S04T001TC0815W11Entity e : entity) {
            C0815W11SearchResultDto dto = new C0815W11SearchResultDto();
            String dlvrytnshacd = e.getDlvrytnshacd();
            if (StringUtils.isEmpty(dlvrytnshacd)
                && CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(e.getDlvrykbn())) {
                dlvrytnshacd = CommonConstants.CD_DLVRYCMP_MOCHIKAERI;
            }
            String jikaidlvryytidd = SystemConstants.WORD_BLANK;
            String kaishun1n2rittorukaishu =
                CommonConstants.DNPYSTS_11.equals(e.getDnpysts()) ? e.getKybtln1n2rittoruhns()
                    : e.getKzbtln1n2rittoruhns();
            String kaishun7rittorukaishu =
                CommonConstants.DNPYSTS_11.equals(e.getDnpysts()) ? e.getKybtln7rittoruhns()
                    : e.getKzbtln7rittoruhns();
            if (CommonConstants.DNPYSTS_13.equals(e.getDnpysts())
                || CommonConstants.DNPYSTS_14.equals(e.getDnpysts())
                || CommonConstants.DNPYSTS_19.equals(e.getDnpysts())) {
                jikaidlvryytidd = DateTimeUtil.calcYMD(unyoymd, 1);
                kaishun1n2rittorukaishu = SystemConstants.WORD_BLANK;
                kaishun7rittorukaishu = SystemConstants.WORD_BLANK;
            }
            dto.setApkihoncdmei(e.getApkihoncd()); // AP基本コード
            dto.setShopcdmei(e.getShopcd()); // ショップコード
            dto.setDnpynomei(e.getDnpyno()); // 受注伝票番号
            dto.setDlvryddmei(e.getDlvrydd()); // 配達日
            dto.setTokuisakicdmei(e.getTokuisakicd()); // 得意先コード
            dto.setJigyoshocdmei(e.getJigyoshocd()); // 事業所コード
            dto.setTokuisakinmmei(e.getTokuisakinm()); // 得意先名称１
            dto.setFzaiokijissekiflgmei(getFlgValue(e.getFzaiokijissekiflg())); // 不在置き実績フラグ
            dto.setFzaiokikanoflgmei(getFlgValue(e.getFzaiokikanoflg())); // 不在置き可能フラグ
            dto.setDlvryytidd(e.getDlvryytidd()); // 配達予定日
            dto.setJikaidlvryytiddmei(jikaidlvryytidd); // 次回配達予定日
            dto.setKaishun1n2rittorukaishu(kaishun1n2rittorukaishu); // 回収予定ボトル１２Ｌ本数
            dto.setKaishun7rittorukaishu(kaishun7rittorukaishu); // 回収予定ボトル７Ｌ本数
            dto.setAcptkbnmei(e.getAcptkbn()); // 受注区分
            dto.setAcptkbnnmmei(e.getAcptkbnnm()); // 受注区分名称
            dto.setEdano(Objects.toString(e.getEdano(), SystemConstants.WORD_BLANK)); // 枝番号
            dto.setTokuisakicdmei(e.getTokuisakicd()); // 得意先コード
            dto.setApkihoncdmei(e.getApkihoncd()); // AP基本コード
            dto.setShopcdmei(e.getShopcd()); // ショップコード
            dto.setAcptkbnshsmei(e.getAcptkbnshs()); // 受注区分詳細
            dto.setAcptddmei(e.getAcptdd()); // 受注日
            dto.setSkysakicdmei(e.getSkysakicd()); // 請求先コード
            dto.setMidlvrykbn(e.getMidlvrykbn()); // 未配達区分（伝票ステータス）
            dto.setMidlvrykbnnm(e.getMidlvrykbnnm()); // 未配達区分名称
            dto.setDlvrytnshameimei(dlvrytnshacd); // 配達担当者コード
            dto.setDlvrytnshanmmei(e.getDlvrytnshamei()); // 配達担当者名称
            dto.setDlvrykankatsuapkihoncd(e.getDlvrykankatsuapkihoncd()); // 配達管轄AP基本コード
            dto.setDlvrykankatsushopcd(e.getDlvrykankatsushopcd()); // 配達管轄ショップコード
            dto.setDlvrykyotencd(e.getDlvrykyotencd()); // 配達拠点コード
            dto.setAcptthdaibikiflg(e.getAcptthdaibikiflg()); // 受注時代引フラグ
            dto.setSingldnpyflg(e.getSingldnpyflg()); // 単体締伝票フラグ
            dto.setDlvrykbn(e.getDlvrykbn()); // 配達区分
            dto.setDnpysts(e.getDnpysts()); // 伝票ステータス
            dto.setKshnsystmymd(e.getKshnsystmymd()); // 更新システム日付
            dto.setKshnsystmjikan(e.getKshnsystmjikan()); // 更新システム時間

            dto.setDlvrykbn(e.getDlvrykbn()); // 配達区分
            dto.setDlvrycmpnycd(e.getDlvrycmpnycd()); // 配達会社コード

            retList.add(dto);
        }

        return retList;
    }

    /**
     * 受注配達明細リストの指定された税区分に関する金額を集計する
     * 
     * @param source 受注配達明細リスト
     * @param dnpysts 伝票ステータス
     * @param taxkbn 集計対象とする明細の税区分
     * @return 集計結果
     */
    private int summaryDetailList(List<C0815W11AcptDlvryMeisaiDto> source, String dnpysts,
        String taxkbn) {
        return source.stream()
            // 税区分：課税
            .filter(detailDto -> Objects.equals(taxkbn, detailDto.getTaxkbn()))
            // 単価と数量を乗算して金額を算出
            .map(detailDto -> {
                BigDecimal unt = util.getDecimalValue(detailDto.getUnt(dnpysts));
                BigDecimal suryo = util.getDecimalValue(detailDto.getSuryo(dnpysts));
                return unt.multiply(suryo).setScale(0, RoundingMode.HALF_UP);
            })
            // サマリ
            .reduce(BigDecimal::add)
            // 整数型に変換
            .map(BigDecimal::intValue)
            // リストが空の場合は0を返す
            .orElse(0);
    }

    /**
     * ワークDTOリストを生成する
     * 
     * @param updateDto 更新用DTO
     * @return ワークDTOリスト
     */
    private List<C0815W11DlvryRcrdCaptureWorkDto> createWorkDtoList(C0815W11UpdateDto updateDto) {
        // DTOに配達実績DTOリストが設定されていない場合は何もせずに空のリストを返す
        if (ObjectUtils.isEmpty(updateDto.getDlvryjissekiDtoList())) {
            return List.of();
        }
        // サーバー関連大分類コード、サーバー本体中分類コード、フロントパネル中分類コードを取得
        final String svrLctgry =
            s02m006tService.searchForMoji(CommonConstants.APCD_000, CommonConstants.SYS_SHOPCD_000,
                CommonConstants.SYS_SETTEINO_096, CommonConstants.SETTEICD_096_4);
        final String svrbdyMctgry =
            s02m006tService.searchForMoji(CommonConstants.APCD_000, CommonConstants.SYS_SHOPCD_000,
                CommonConstants.SYS_SETTEINO_134, CommonConstants.SETTEICD_134_41);
        final String fpnlMctgry =
            s02m006tService.searchForMoji(CommonConstants.APCD_000, CommonConstants.SYS_SHOPCD_000,
                CommonConstants.SYS_SETTEINO_134, CommonConstants.SETTEICD_0134_44);
        // ショップマスタから得意先設置サーバ連携区分を取得
        final boolean isLinkageShop =
            getShopIsLinkagedWsvr(updateDto.getApkihoncd(), updateDto.getShopcd());
        // 更新対象フラグがONの配達実績DTOリスト
        final List<C0815W11DlvryJissekiDto> targetJissekiList = updateDto.getDlvryjissekiDtoList()
            .stream().filter(d -> util.isFlagged(d.getKshntrgtflg())).collect(util.toList());

        List<C0815W11DlvryRcrdCaptureWorkDto> result = targetJissekiList.stream()
            // 配達実績DTOを元にCSVデータのリストに変換
            .map(dlvryjissekiDto -> {

                // 受注配達明細リストを取得
                List<C0815W11AcptDlvryMeisaiDto> detailDtoList =
                    dlvryjissekiDto.getAcptdlvrymeisaiList();
                // 修正有無フラグがONではなく、未配達区分が空の場合
                if (!util.isFlagged(dlvryjissekiDto.getShuseiarinashiflg())
                    && StringUtils.isEmpty(dlvryjissekiDto.getMidlvrykbn())) {
                    // 受注配達明細をデータベースから取得する
                    detailDtoList =
                        createAcptDlvryMeisaiDtoList(getDetailEntityList(dlvryjissekiDto));
                }

                // 入力行のみの受注配達明細リストを取得
                List<C0815W11AcptDlvryMeisaiDto> inptDtoList = detailDtoList.stream()
                    // 入力明細フラグがONの明細に絞り込み
                    .filter(detailDto -> util.isFlagged(detailDto.getNyuryokumeisaiflg()))
                    .collect(util.toList());
                // 商品入力行に絞り込みした受注配達明細リストを取得
                List<C0815W11AcptDlvryMeisaiDto> inptShnDtoList = inptDtoList.stream()
                    // 明細区分が商品
                    .filter(detailDto -> CommonConstants.KBN_MEISAI_SHOHIN
                        .equals(detailDto.getMeisaikbn()))
                    .collect(util.toList());

                // CSVデータリストを生成する(対象は入力行のみ）
                List<C0815W11DlvryRcrdCaptureWorkDto> workDtoList = inptDtoList.stream()
                    .map(detailDto -> buildWorkDto(dlvryjissekiDto, detailDto,
                        dlvryjissekiDto.getDnpysts(), svrLctgry, svrbdyMctgry, fpnlMctgry))
                    .collect(util.toList());

                // ショップが得意先設置サーバを連携する場合
                if (isLinkageShop) {
                    workDtoList.stream()
                        // 設置対象となるサーバ本体またはフロントパネルおよび引上対象となるサーバ本体に絞り込み
                        .filter(workDto -> workDto.isStupSvr() || workDto.isStupFpnl()
                            || workDto.isHikiageSvr())
                        // 得意先設置サーバ管理マスタに反映するべき数量を算出し設定
                        .forEach(workDto -> {
                            // 得意先設置サーバ管理マスタへ反映するサーバ本体、フロントパネルの数量を算出
                            // ※伝票ステータスが「12：配達・売上済」の場合は変更分のみ反映するため、数量の差分が必要
                            BigDecimal suryo = util.getDecimalValue(workDto.getNohinsuryo());
                            // 配達・売上済の数量を取得（伝票ステータスが条件を満たさない場合0が返る）
                            BigDecimal currentSuryo =
                                getDeliveredDnpySuryo(workDto, dlvryjissekiDto.getDnpysts());
                            // 設置対象となるサーバ本体またはフロントパネルの場合
                            if (workDto.isStupSvr() || workDto.isStupFpnl()) {
                                // 変更前の納品数量が負数の場合は0として、差分数量を算出
                                workDto.setToreflectsuryo(util.getString(suryo.subtract(
                                    currentSuryo.signum() == -1 ? BigDecimal.ZERO : currentSuryo)));
                            }
                            // 得意先設置サーバ管理マスタに反映する引上対象サーバ台数を算出
                            if (workDto.isHikiageSvr()) {
                                // 変更前の納品数量が正数の場合は0として、絶対値の差分数量を算出
                                workDto.setToreflectsuryo(util.getString(suryo.abs()
                                    .subtract(currentSuryo.signum() == 1 ? BigDecimal.ZERO
                                        : currentSuryo.abs())));
                            }
                            // 設置内容（設置対象のサーバー本体の明細に対してのみ設定する）
                            workDto.setStupnaiyo(workDto.isStupSvr() ? workDto.getAcptkbnshs()
                                : SystemConstants.WORD_BLANK);
                        });
                    // 自動設定するフロントパネルコードを取得
                    String fpnlShncd = getStupFrontpanelcd(workDtoList);
                    // 設置対象となるサーバ本体のワークデータに対してフロントパネルコードを自動設定
                    workDtoList.stream().filter(C0815W11DlvryRcrdCaptureWorkDto::isStupSvr)
                        .forEach(workDto -> workDto.setFrontpanelcd(fpnlShncd));
                    // 引上対象となるサーバ本体のワークDTOに対して引上内容等を設定する
                    fillHikiageProperties(dlvryjissekiDto, workDtoList);
                }

                // 課税の消費税ワークデータを追加
                workDtoList.add(createKazeiTaxCsvDto(dlvryjissekiDto, inptShnDtoList,
                    dlvryjissekiDto.getDnpysts()));
                // 軽減の消費税ワークデータを追加
                workDtoList.add(createKeigenTaxCsvDto(dlvryjissekiDto, inptShnDtoList,
                    dlvryjissekiDto.getDnpysts()));
                log.debug("{}", workDtoList);
                // 伝票1つに対するワークデータリストを返却
                return workDtoList;
            })
            // Stream<List<E>>をStream<E>に平坦化
            .flatMap(list -> list.stream())
            // Listを生成
            .collect(util.toList());
        return result;
    }

    /**
     * ワークDTOに対して、引上内容の設定を行う
     * 
     * @param dlvryjissekiDto 配達実績DTO
     * @param hikiageWorkDtoList ワークDTOリスト
     */
    private void fillHikiageProperties(C0815W11DlvryJissekiDto dlvryjissekiDto,
        List<C0815W11DlvryRcrdCaptureWorkDto> workDtoList) {
        // ワークDTOから引上対象サーバ本体のデータを取得する
        List<C0815W11DlvryRcrdCaptureWorkDto> hikiageWorkDtoList = workDtoList.stream()
            .filter(C0815W11DlvryRcrdCaptureWorkDto::isHikiageSvr).collect(util.toList());
        // 得意先設置サーバ管理マスタから設置済かつ未引上のデータを取得する
        S02M048TEntityExample ex = new S02M048TEntityExample();
        ex.createCriteria()
            // AP基本コード
            .andApkihoncdEqualTo(dlvryjissekiDto.getApkihoncd())
            // ショップコード
            .andShopcdEqualTo(dlvryjissekiDto.getShopcd())
            // 得意先コード
            .andTokuisakicdEqualTo(dlvryjissekiDto.getTokuisakicd())
            // 設置日（NULL以外…設置済）
            .andStupddIsNotNull()
            // 引上日（NULL…未引上）
            .andHikiageddIsNull()
            // 有効区分（有効）
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<S02M048TEntity> stupedWsvrList = s02m048tDao.selectByExample(ex);
        log.debug("設置済未引上の得意先設置サーバ管理マスタの件数 : {}", stupedWsvrList.size());
        // 引上数量（差分を加味した数量）を取得してサマリ
        int hikiageCount = hikiageWorkDtoList.stream()
            .mapToInt(workDto -> util.getIntValue(workDto.getToreflectsuryo())).sum();
        log.debug("受注に含まれる引上対象サーバの数量 : {}", hikiageCount);
        // 処理対象ワークDTOリストの初期化
        List<C0815W11DlvryRcrdCaptureWorkDto> targetList = List.of();
        if (hikiageCount == stupedWsvrList.size()) {
            // 設置済かつ未引上のサーバ台数と引上対象サーバ本体の数量が一致する場合
            // 全ての引上対象サーバ本体の明細に引上内容を設定する
            targetList = hikiageWorkDtoList;
        } else if (hikiageCount == 1) {
            // 上記以外の場合で引上対象のサーバ台数が1台の時
            C0815W11DlvryRcrdCaptureWorkDto hikiageWsvr =
                hikiageWorkDtoList.stream().findFirst().get();
            log.debug("受注に含まれる引上対象サーバ本体が1台（商品コード：{}）", hikiageWsvr.getShncd());
            long sameShnSvrCount = stupedWsvrList.stream()
                .filter(wsvrEntity -> Objects.equals(hikiageWsvr.getShncd(), wsvrEntity.getShncd()))
                .count();
            log.debug("設置済未引上のサーバで商品コードが同じものの件数（商品コード：{}） : {}", hikiageWsvr.getShncd(),
                sameShnSvrCount);
            // 設置済で未引上のサーバの中に同じ商品コードのサーバが1台だけなら引上内容を設定する
            if (sameShnSvrCount == 1) {
                targetList = hikiageWorkDtoList;
            }
        }
        // 処理対象とするワークDTOに対して引上内容とサブステータスを設定する
        targetList.stream().forEach(workDto -> {
            // 引上内容を設定
            workDto.setHikiagenaiyo(workDto.getAcptkbnshs());
            // サブステータスを設定
            // メンテナンスの場合は「083：メンテナンス」を、それ以外は「081：回収」を設定
            workDto.setSabusts(workDto.isMaintenance() ? "083" : "081");
        });
    }

    /**
     * 配達・売上済の受注について受注配達明細テーブルから変更前の納品数量を取得する
     * 
     * @param workDto ワークDTO
     * @param dnpysts 伝票ステータス
     * @return 納品数量（伝票ステータスが12：配達売上済以外またはテーブルにデータがない場合はBigDecimal.ZERO）
     */
    private BigDecimal getDeliveredDnpySuryo(C0815W11DlvryRcrdCaptureWorkDto workDto,
        String dnpysts) {
        // 伝票ステータスが12：配達・売上済ではない場合
        if (!CommonConstants.DNPYSTS_12.equals(dnpysts)) {
            // 現在の数量は0として処理する
            return BigDecimal.ZERO;
        }
        // 受注配達明細テーブルから現在の納品数量を取得する
        S04T002TEntityKey key = new S04T002TEntityKey();
        key.setApkihoncd(workDto.getApkihoncd());
        key.setShopcd(workDto.getShopcd());
        key.setAcptdnpyno(workDto.getAccisdnpyno());
        key.setEdano(util.getShortValue(workDto.getAccisdnpyedano()));
        key.setAcptdnpymeisaino(util.getIntValue(workDto.getAcptdnpymeisaino()));
        key.setAcptdnpymeisaiedano(util.getShortValue(workDto.getAcptdnpymeisaiedano()));
        BigDecimal currentSuryo = Optional.ofNullable(s04t002tDao.selectByPrimaryKey(key))
            .map(entity -> entity.getNohinsuryo()).orElse(BigDecimal.ZERO);
        return currentSuryo;
    }

    /**
     * 設置対象となるサーバー本体に紐づけ可能なフロントパネルの商品コードを取得する
     * 
     * @param workDtoList 商品入力行の受注配達明細DTOリスト
     * @return フロントパネルの商品コード
     */
    private String getStupFrontpanelcd(List<C0815W11DlvryRcrdCaptureWorkDto> workDtoList) {
        // 設置対象となるサーバー本体の明細リストを取得
        List<C0815W11DlvryRcrdCaptureWorkDto> stupSrvDtoList = workDtoList.stream()
            .filter(C0815W11DlvryRcrdCaptureWorkDto::isStupSvr).collect(util.toList());
        // 設置対象となるフロントパネルの明細リストを取得
        List<C0815W11DlvryRcrdCaptureWorkDto> stupPnlDtoList = workDtoList.stream()
            .filter(C0815W11DlvryRcrdCaptureWorkDto::isStupFpnl).collect(util.toList());

        // 設置対象のサーバー本体納品数をサマリ
        long srvCount = stupSrvDtoList.stream()
            .mapToLong(detailDto -> util.getLongValue(detailDto.getToreflectsuryo())).sum();
        // 設置対象サーバー本体の種類が1種類かどうか
        boolean isOnlyOneSvrType =
            util.distinct(stupSrvDtoList.stream().map(C0815W11DlvryRcrdCaptureWorkDto::getShncd))
                .count() == 1L;
        // 設置対象フロントパネルの納品数をカウント
        long pnlCount = stupPnlDtoList.stream()
            .mapToLong(detailDto -> util.getLongValue(detailDto.getToreflectsuryo())).sum();
        // 設置対象フロントパネルの種類が1種類かどうか
        boolean isOnlyOnePnlType =
            util.distinct(stupPnlDtoList.stream().map(C0815W11DlvryRcrdCaptureWorkDto::getShncd))
                .count() == 1L;

        // 下記の条件を満たす場合、フロントパネルの商品コードを取得
        // ・設置対象のサーバー商品コードと設置対象のフロントパネル商品コードの納品数合計が同一
        // ・設置対象のサーバー商品コードの種類または設置対象のフロントパネル商品コードの種類の何れかが1
        return srvCount == pnlCount && (isOnlyOneSvrType || isOnlyOnePnlType)
            ? stupPnlDtoList.stream()
                // フロントパネルの商品コードを取得（複数の明細がある場合は最初に見つけた商品コード）
                .findFirst().map(C0815W11DlvryRcrdCaptureWorkDto::getShncd)
                // フロントパネルの明細が含まれていない場合、空文字
                .orElse(SystemConstants.WORD_BLANK)
            // 条件を満たしてない場合、空文字
            : SystemConstants.WORD_BLANK;
    }

    /**
     * 課税消費税のワークDTOを生成する
     * 
     * @param dlvryjissekiDto 配達実績DTO
     * @param inptShnDtoList 商品入力行受注配達明細リスト
     * @param dnpysts 伝票ステータス
     * @return ワークDTO
     */
    private C0815W11DlvryRcrdCaptureWorkDto createKazeiTaxCsvDto(
        C0815W11DlvryJissekiDto dlvryjissekiDto, List<C0815W11AcptDlvryMeisaiDto> inptShnDtoList,
        String dnpysts) {
        return createTaxWorkDto(dlvryjissekiDto, inptShnDtoList, dnpysts,
            CommonConstants.KBN_TAX_KAZEI, "099901");
    }

    /**
     * 軽減消費税のワークDTOを生成する
     * 
     * @param dlvryjissekiDto 配達実績DTO
     * @param inptShnDtoList 商品入力行受注配達明細リスト
     * @param dnpysts 伝票ステータス
     * @return ワークDTO
     */
    private C0815W11DlvryRcrdCaptureWorkDto createKeigenTaxCsvDto(
        C0815W11DlvryJissekiDto dlvryjissekiDto, List<C0815W11AcptDlvryMeisaiDto> inptShnDtoList,
        String dnpysts) {
        return createTaxWorkDto(dlvryjissekiDto, inptShnDtoList, dnpysts,
            CommonConstants.KBN_TAX_KEIGEN, "099902");
    }

    /**
     * 消費税ワークDTOを生成する
     * 
     * @param dlvryjissekiDto 配達実績DTO
     * @param inptShnDtoList 商品入力行の受注配達明細リスト
     * @param dnpysts 伝票ステータス
     * @param taxkbn 税区分
     * @param taxShncd 消費税の商品コード
     * @return ワークDTO
     */
    private C0815W11DlvryRcrdCaptureWorkDto createTaxWorkDto(
        C0815W11DlvryJissekiDto dlvryjissekiDto, List<C0815W11AcptDlvryMeisaiDto> inptShnDtoList,
        String dnpysts, String taxkbn, String taxShncd) {
        // 商品入力行の金額をサマリ
        int summary = summaryDetailList(inptShnDtoList, dnpysts, taxkbn);
        // 消費税計算バッチにより消費税額を計算する
        int shohitax = calculateTax(dlvryjissekiDto.getApkihoncd(), dlvryjissekiDto.getShopcd(),
            dlvryjissekiDto.getDlvrydd(), util.getString(summary), taxkbn);
        // 消費税ワークDTOを生成して返却
        return buildTaxWorkDto(dlvryjissekiDto, shohitax, taxShncd);
    }

    /**
     * 消費税計算バッチにより消費税金額を計算する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param targetDate 対象日
     * @param kin 計算対象とする金額
     * @param taxkbn 税区分
     * @return 消費税金額
     */
    private int calculateTax(String apkihoncd, String shopcd, String targetDate, String kin,
        String taxkbn) {
        Z1808B01Entity param = new Z1808B01Entity();
        param.setSimplefg(CommonConstants.FLG_ON);
        param.setTaxsetteikbn(CommonConstants.FLG_ON);
        param.setApkihoncd(apkihoncd);
        param.setShopcd(shopcd);
        param.setKirikaebi(targetDate);
        param.setTaxkbn(taxkbn);
        param.setKin(kin);
        return z1808b01Dao.syohizeiKeisan(param);
    }

    /**
     * ワークDTOを構築する
     * 
     * @param dlvryjissekiDto 配達実績DTO
     * @param detailDto 受注配達明細DTO
     * @param dnpysts 伝票ステータス
     * @param svrLctgrycd サーバー関連大分類コード
     * @param svrbdyMctgrycd サーバー本体中分類コード
     * @param fpnlMctgrycd フロントパネル中分類コード
     * @return ワークDTO
     */
    private C0815W11DlvryRcrdCaptureWorkDto buildWorkDto(C0815W11DlvryJissekiDto dlvryjissekiDto,
        C0815W11AcptDlvryMeisaiDto detailDto, String dnpysts, String svrLctgrycd,
        String svrbdyMctgrycd, String fpnlMctgrycd) {
        C0815W11DlvryRcrdCaptureWorkDto workDto = C0815W11DlvryRcrdCaptureWorkDto.builder()
            // AP基本コード
            .apkihoncd(dlvryjissekiDto.getApkihoncd())
            // ショップコード
            .shopcd(dlvryjissekiDto.getShopcd())
            // ACCIS伝票番号
            .accisdnpyno(dlvryjissekiDto.getAcptdnpyno())
            // ACCIS伝票番号枝番
            .accisdnpyedano(dlvryjissekiDto.getAcptedano())
            // 受注区分
            .acptkbn(dlvryjissekiDto.getAcptkbn())
            // 受注区分詳細
            .acptkbnshs(dlvryjissekiDto.getAcptkbnshs())
            // 得意先コード
            .tokuisakicd(dlvryjissekiDto.getTokuisakicd())
            // 配達実績日
            .dlvryjissekidd(dlvryjissekiDto.getDlvrydd())
            // 配達区分
            .dlvrykbn(dlvryjissekiDto.getDlvrykbn())
            // 配達会社コード
            .dlvrycmpnycd(dlvryjissekiDto.getDlvrycmpnycd())
            // 配達担当者コード
            .dlvrytnshacd(dlvryjissekiDto.getDlvrytnshacd())
            // 請求先コード
            .skysakicd(dlvryjissekiDto.getSkysakicd())
            // 回収実績ボトルレギュラー本数
            .kzbtlrgrahns(
                ItemUtil.removeComma(nullToBlank(dlvryjissekiDto.getKaishun1n2rittorukaishu())))
            // 回収実績ボトルスリム本数
            .kzbtlslmhns(
                ItemUtil.removeComma(nullToBlank(dlvryjissekiDto.getKaishun7rittorukaishu())))
            // 配達ステータス
            .dlvrysts(dlvryjissekiDto.getDlvrysts())
            // 次回配達予定日
            .jikaidlvryytidd(dlvryjissekiDto.getJikaidlvryytidd())
            // 不在置き実績フラグ(空の場合はOFFにする）
            .fzaiokijissekiflg(getFlgValue(dlvryjissekiDto.getFzaiokijissekiflg()))
            // 受注伝票明細番号
            .acptdnpymeisaino(detailDto.getAcptdnpymeisaino())
            // 受注伝票明細枝番号
            .acptdnpymeisaiedano(detailDto.getAcptdnpymeisaiedano())
            // 商品コード
            .shncd(detailDto.getShncd())
            // 配布物AP基本コード
            .haifubutsuapkihoncd(detailDto.getHaifubutsuapkihoncd())
            // 配布物ショップコード
            .haifubutsushopcd(detailDto.getHaifubutsushopcd())
            // 配布物コード
            .haifubutsucd(detailDto.getHaifubutsucd())
            // 計上区分
            .keijokbn(detailDto.getKeijokbn())
            // 納品数量
            .nohinsuryo(detailDto.getSuryo(dnpysts))
            // 納品単価
            .nohinunt(detailDto.getUnt(dnpysts))
            // 入力明細フラグ
            .nyuryokumeisaiflg(CommonConstants.FLG_ON)
            // 明細備考
            .meisaibiko(detailDto.getMeisaibiko())
            // 型式
            .katasiki(detailDto.getKatasiki())
            // カップフォルダ有無
            .cupholderumu("0")
            // ロック付フォーセット
            .locktfoset("00")
            // サーバー関連大分類コード
            .svrLctgrycd(svrLctgrycd)
            // サーバー本体中分類コード
            .svrbdyMctgrycd(svrbdyMctgrycd)
            // フロントパネル中分類コード
            .fpnlMctgrycd(fpnlMctgrycd)
            // 大分類コード
            .lctgrycd(detailDto.getLctgrycd())
            // 中分類コード
            .mctgrycd(detailDto.getMctgrycd())
            // DTOを構築
            .build();
        // 明細備考に半角カンマ（CSV項目の値として用いることが出来ない文字）を含む場合、別の文字に置換する
        // 配達実績登録にて出力するCSVファイルは引用符で値を囲えないための措置（PL/SQL側で読み込む際に復元する）
        if (!StringUtils.isEmpty(workDto.getMeisaibiko())) {
            // 半角カンマを「$$」に置換する
            String note = workDto.getMeisaibiko().replaceAll(",", Matcher.quoteReplacement("$$"));
            log.debug("明細備考 : {}", workDto.getMeisaibiko());
            log.debug("明細備考（置換後） : {}", note);
            workDto.setMeisaibiko(note);
        }
        return workDto;
    }

    /**
     * 消費税のワークDTOを生成する
     * 
     * @param dlvryjissekiDto 配達実績DTO
     * @param shohitaxkin 消費税金額
     * @param taxShncd 商品コード
     * @return 消費税ワークDTO
     */
    private C0815W11DlvryRcrdCaptureWorkDto buildTaxWorkDto(C0815W11DlvryJissekiDto dlvryjissekiDto,
        int shohitaxkin, String taxShncd) {
        return C0815W11DlvryRcrdCaptureWorkDto.builder()
            // AP基本コード
            .apkihoncd(dlvryjissekiDto.getApkihoncd())
            // ショップコード
            .shopcd(dlvryjissekiDto.getShopcd())
            // ACCIS伝票番号
            .accisdnpyno(dlvryjissekiDto.getAcptdnpyno())
            // ACCIS伝票番号枝番
            .accisdnpyedano(dlvryjissekiDto.getAcptedano())
            // 受注区分
            .acptkbn(dlvryjissekiDto.getAcptkbn())
            // 受注区分詳細
            .acptkbnshs(dlvryjissekiDto.getAcptkbnshs())
            // 得意先コード
            .tokuisakicd(dlvryjissekiDto.getTokuisakicd())
            // 配達実績日
            .dlvryjissekidd(dlvryjissekiDto.getDlvrydd())
            // 配達区分
            .dlvrykbn(dlvryjissekiDto.getDlvrykbn())
            // 配達会社コード
            .dlvrycmpnycd(dlvryjissekiDto.getDlvrycmpnycd())
            // 配達担当者コード
            .dlvrytnshacd(dlvryjissekiDto.getDlvrytnshacd())
            // 請求先コード
            .skysakicd(dlvryjissekiDto.getSkysakicd())
            // 回収実績ボトルレギュラー本数
            .kzbtlrgrahns(
                ItemUtil.removeComma(nullToBlank(dlvryjissekiDto.getKaishun1n2rittorukaishu())))
            // 回収実績ボトルスリム本数
            .kzbtlslmhns(
                ItemUtil.removeComma(nullToBlank(dlvryjissekiDto.getKaishun7rittorukaishu())))
            // 配達ステータス
            .dlvrysts(dlvryjissekiDto.getDlvrysts())
            // 次回配達予定日
            .jikaidlvryytidd(dlvryjissekiDto.getJikaidlvryytidd())
            // 不在置き実績フラグ(空の場合はOFFにする）
            .fzaiokijissekiflg(getFlgValue(dlvryjissekiDto.getFzaiokijissekiflg()))
            // 商品コード
            .shncd(taxShncd)
            // 消費税金額
            .shohitaxkin(util.getString(shohitaxkin))
            // DTOを構築
            .build();
    }

    /**
     * 受注配達明細Entityリストを取得する
     * 
     * @param dlvryjissekiDto 配達実績DTO
     * @return 受注配達明細Entityリスト
     */
    private List<S04T002TC0815W11Entity> getDetailEntityList(
        C0815W11DlvryJissekiDto dlvryjissekiDto) {
        S04T002TC0815W11Entity entityKey = new S04T002TC0815W11Entity();
        entityKey.setApkihoncd(dlvryjissekiDto.getApkihoncd());
        entityKey.setShopcd(dlvryjissekiDto.getShopcd());
        entityKey.setAcptdnpyno(dlvryjissekiDto.getAcptdnpyno());
        entityKey.setEdano(Short.valueOf(dlvryjissekiDto.getAcptedano()));
        entityKey.setPriceacquisitionkeydate(dlvryjissekiDto.getDlvrydd());
        entityKey.setAcptdd(dlvryjissekiDto.getAcptdd());
        return s04t002tDao.selectByC0815W11AcptmeisaiList(entityKey);
    }

    /**
     * C0815W11受注配達明細リストを作成する
     * 
     * @param acptdlvrymeisaiEntityList List<S04T002TC0815W11Entity>
     * @return List<C0815W11AcptDlvryMeisaiDto>
     */
    private List<C0815W11AcptDlvryMeisaiDto> createAcptDlvryMeisaiDtoList(
        List<S04T002TC0815W11Entity> acptdlvrymeisaiEntityList) {
        List<C0815W11AcptDlvryMeisaiDto> acptdlvrymeisaiDtoList = new ArrayList<>();
        if (!ListUtils.isEmpty(acptdlvrymeisaiEntityList)) {
            for (S04T002TC0815W11Entity meisaiEntity : acptdlvrymeisaiEntityList) {
                C0815W11AcptDlvryMeisaiDto acptdlvrymeisaiDto = new C0815W11AcptDlvryMeisaiDto();
                String haifuflg =
                    !StringUtils.isEmpty(meisaiEntity.getHaifubutsucd()) ? CommonConstants.FLG_ON
                        : CommonConstants.FLG_OFF; // 配布物フラグ
                acptdlvrymeisaiDto.setAcptdnpymeisaino(Objects
                    .toString(meisaiEntity.getAcptdnpymeisaino(), SystemConstants.WORD_BLANK)); // 受注伝票明細番号
                acptdlvrymeisaiDto.setAcptdnpymeisaiedano(Objects
                    .toString(meisaiEntity.getAcptdnpymeisaiedano(), SystemConstants.WORD_BLANK)); // 受注明細枝番号
                acptdlvrymeisaiDto.setEdano(meisaiEntity.getEdano()); // 枝番号
                acptdlvrymeisaiDto.setShncd(meisaiEntity.getShncd()); // 商品コード
                acptdlvrymeisaiDto.setHaifuflg(haifuflg); // 配布物フラグ
                acptdlvrymeisaiDto.setHaifubutsuapkihoncd(meisaiEntity.getHaifubutsuapkihoncd()); // 配布物AP基本コード
                acptdlvrymeisaiDto.setHaifubutsushopcd(meisaiEntity.getHaifubutsushopcd()); // 配布物ショップコード
                acptdlvrymeisaiDto.setHaifubutsucd(meisaiEntity.getHaifubutsucd()); // 配布物コード
                acptdlvrymeisaiDto.setKeijokbn(meisaiEntity.getKeijokbn()); // 計上区分
                acptdlvrymeisaiDto.setNohinunt(meisaiEntity.getNohinunt()); // 納品単価
                acptdlvrymeisaiDto.setNohinsuryo(meisaiEntity.getNohinsuryo()); // 納品数量
                acptdlvrymeisaiDto.setAcptunt(meisaiEntity.getAcptunt()); // 受注単価
                acptdlvrymeisaiDto.setAcptsuryo(meisaiEntity.getAcptsuryo()); // 受注数量
                acptdlvrymeisaiDto.setMeisaibiko(meisaiEntity.getMeisaibiko()); // 明細備考
                acptdlvrymeisaiDto.setLctgrycd(meisaiEntity.getLctgrycd()); // 大分類
                acptdlvrymeisaiDto.setMctgrycd(meisaiEntity.getMctgrycd()); // 中分類
                acptdlvrymeisaiDto.setShnnm(meisaiEntity.getShnnm()); // 商品名称
                acptdlvrymeisaiDto.setKeijokbnnm(meisaiEntity.getKeijokbnnm()); // 計上区分名称
                acptdlvrymeisaiDto.setKin(meisaiEntity.getNohinkin()); // 納品金額
                acptdlvrymeisaiDto.setIro(meisaiEntity.getIro()); // 色
                acptdlvrymeisaiDto.setSz(meisaiEntity.getSz()); // サイズ
                acptdlvrymeisaiDto.setIrisu(meisaiEntity.getUriirisu()); // 入数
                acptdlvrymeisaiDto.setIrisutani(meisaiEntity.getUritani()); // 入数単位
                acptdlvrymeisaiDto.setSokazu(meisaiEntity.getSokazu()); // 総数
                acptdlvrymeisaiDto.setKatasiki(meisaiEntity.getKatasiki()); // 型式
                acptdlvrymeisaiDto.setTaxkbn(meisaiEntity.getTaxkbn()); // 税区分
                acptdlvrymeisaiDto.setTani(meisaiEntity.getUritrtskitani()); // 数量単位
                acptdlvrymeisaiDto.setHyojunprice(meisaiEntity.getHyojununt()); // 標準単価
                acptdlvrymeisaiDto.setMeisaikbn(meisaiEntity.getMeisaikbn()); // 明細区分
                acptdlvrymeisaiDto.setNyuryokumeisaiflg(meisaiEntity.getNyuryokumeisaiflg()); // 入力明細フラグ

                // ショップ切替日、現ショップ価格、新ショップ価格、現ショップ持ち帰り価格、新ショップ持ち帰り価格
                if (CommonConstants.KBN_PRICE_HYOJUN.equals(meisaiEntity.getPricekbn())) {
                    acptdlvrymeisaiDto
                        .setShoppricekirikaedd(meisaiEntity.getHyojunpricekirikaedd());
                    acptdlvrymeisaiDto.setGenzshopprice(Objects
                        .toString(meisaiEntity.getGenzhyojunprice(), BigDecimal.ZERO.toString()));
                    acptdlvrymeisaiDto.setShinshopprice(Objects
                        .toString(meisaiEntity.getShinhyojunprice(), BigDecimal.ZERO.toString()));
                    acptdlvrymeisaiDto.setGenzshopmochikaeriprice(Objects.toString(
                        meisaiEntity.getGenzmochikaeriprice(), BigDecimal.ZERO.toString()));
                    acptdlvrymeisaiDto.setShinshopmochikaeriprice(Objects.toString(
                        meisaiEntity.getShinmochikaeriprice(), BigDecimal.ZERO.toString()));
                } else {
                    acptdlvrymeisaiDto.setShoppricekirikaedd(meisaiEntity.getOpnpricekirikaedd());
                    acptdlvrymeisaiDto.setGenzshopprice(Objects
                        .toString(meisaiEntity.getGenzopnprice(), BigDecimal.ZERO.toString()));
                    acptdlvrymeisaiDto.setShinshopprice(Objects
                        .toString(meisaiEntity.getShinopnprice(), BigDecimal.ZERO.toString()));
                    acptdlvrymeisaiDto.setGenzshopmochikaeriprice(Objects.toString(
                        meisaiEntity.getGenzopnmochikaeriprice(), BigDecimal.ZERO.toString()));
                    acptdlvrymeisaiDto.setShinshopmochikaeriprice(Objects.toString(
                        meisaiEntity.getShinopnmochikaeriprice(), BigDecimal.ZERO.toString()));
                }

                // 販促開始日、販促終了日、販促配達価格、販促持ち帰り価格
                if (!CommonConstants.KBN_SAKUSEIMOTO_JUCHUYOYAKU
                    .equals(meisaiEntity.getDatsksmotokbn())
                    && CommonConstants.FLG_ON.equals(meisaiEntity.getHansokukbn())) {
                    acptdlvrymeisaiDto.setHansokustrtdd(meisaiEntity.getHansokustrtdd());
                    acptdlvrymeisaiDto.setHansokushrydd(meisaiEntity.getHansokushrydd());
                    acptdlvrymeisaiDto.setHansokuprice(Objects
                        .toString(meisaiEntity.getHansokudlvryprice(), BigDecimal.ZERO.toString()));
                    acptdlvrymeisaiDto.setHansokumochikaeriprice(Objects.toString(
                        meisaiEntity.getHansokumochikaeriprice(), BigDecimal.ZERO.toString()));
                    acptdlvrymeisaiDto.setHansokuyusenkbn(meisaiEntity.getHansokuyusenkbn());
                }

                // 得意先価格開始日、得意先価格終了日、得意先価格、得意先持ち帰り価格
                if (CommonConstants.FLG_ON.equals(meisaiEntity.getRknkindtokubetsuuntflg())) {
                    if (CommonConstants.FLG_ON.equals(meisaiEntity.getStkbn())) {
                        acptdlvrymeisaiDto.setTtpricestrtdd(meisaiEntity.getSet_tekiyostrtdd());
                        acptdlvrymeisaiDto.setTtpriceshrydd(meisaiEntity.getSet_tekiyoshrydd());
                        acptdlvrymeisaiDto.setTtprice(Objects.toString(
                            meisaiEntity.getSet_dlvryprice(), BigDecimal.ZERO.toString()));
                        acptdlvrymeisaiDto.setTtmochikaeriprice(Objects.toString(
                            meisaiEntity.getSet_mochikaeriprice(), BigDecimal.ZERO.toString()));
                    } else {
                        acptdlvrymeisaiDto.setTtpricestrtdd(meisaiEntity.getTekiyostrtdd());
                        acptdlvrymeisaiDto.setTtpriceshrydd(meisaiEntity.getTekiyoshrydd());
                        acptdlvrymeisaiDto.setTtprice(Objects.toString(meisaiEntity.getDlvryprice(),
                            BigDecimal.ZERO.toString()));
                        acptdlvrymeisaiDto.setTtmochikaeriprice(Objects.toString(
                            meisaiEntity.getMochikaeriprice(), BigDecimal.ZERO.toString()));
                    }
                }
                // 商品税区分
                if (CommonConstants.FLG_ON.equals(meisaiEntity.getTaxsetteikbn())) {
                    acptdlvrymeisaiDto.setShntaxkbn(meisaiEntity.getShntaxkbn());
                }
                // セット得意先価格開始日、セット得意先価格終了日、セット得意先価格、セット得意先持ち帰り価格
                if (CommonConstants.FLG_ON.equals(meisaiEntity.getRknkindtokubetsuuntflg())) {
                    acptdlvrymeisaiDto.setSet_tekiyostrtdd(meisaiEntity.getSet_tekiyostrtdd());
                    acptdlvrymeisaiDto.setSet_tekiyoshrydd(meisaiEntity.getSet_tekiyoshrydd());
                    acptdlvrymeisaiDto.setSet_dlvryprice(Objects
                        .toString(meisaiEntity.getSet_dlvryprice(), BigDecimal.ZERO.toString()));
                    acptdlvrymeisaiDto.setSet_mochikaeriprice(Objects.toString(
                        meisaiEntity.getSet_mochikaeriprice(), BigDecimal.ZERO.toString()));
                }
                acptdlvrymeisaiDtoList.add(acptdlvrymeisaiDto);
            }
        }
        return acptdlvrymeisaiDtoList;
    }

    /**
     * モバイル配達実績取込ワークCSVを出力する
     * 
     * @param workDtoList ワークDTOリスト
     * @param filePath CSVファイル保存先パス
     * @param fileName ファイル名
     * @return CSVファイル絶対パス文字列
     */
    private String outputCsvFile(List<C0815W11DlvryRcrdCaptureWorkDto> workDtoList, Path filePath,
        String fileName) {
        Path cp = filePath.resolve(fileName);
        File csvFile = cp.toFile();
        // ファイルが既に存在する場合は、削除する。
        if (csvFile.exists() && csvFile.isFile()) {
            // 既存のファイルを削除する。
            if (!csvFile.delete()) {
                // 削除できなかった場合、業務例外発生
                throw new BusinessException("該当ファイルは、他の業務にて使用中です。");
            }
        }
        CsvMapper mapper = new CsvMapper();
        // LocalDateTimeを含む場合に必要なモジュール登録
        mapper.registerModule(new JavaTimeModule());
        // 値に引用符をつけない
        mapper.configure(CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS, false);
        // CSVマッパーからCSVスキーマを取得
        CsvSchema schema = mapper
            // CSVデータを保持するDTOを指定
            .schemaFor(C0815W11DlvryRcrdCaptureWorkDto.class)
            // NULL値は空文字で出力する
            .withNullValue("")
            // 改行文字はCRLF
            .withLineSeparator("\r\n");

        try (BufferedWriter writer = Files.newBufferedWriter(cp, Charset.forName("MS932"))) {
            // CSVマッパーから行ライターを取得
            SequenceWriter sequenceWriter = mapper
                // CSVデータを保持するDTOを指定
                .writerFor(C0815W11DlvryRcrdCaptureWorkDto.class)
                // CSVスキーマを指定
                .with(schema)
                // BufferedWriterを指定
                .writeValues(writer);
            for (C0815W11DlvryRcrdCaptureWorkDto workDto : workDtoList) {
                log.debug("{}", mapper.writerFor(C0815W11DlvryRcrdCaptureWorkDto.class).with(schema)
                    .writeValueAsString(workDto));
                // ファイルに1行ずつ書き込み
                sequenceWriter.write(workDto);
            }
        } catch (IOException e) {
            throw BusinessExceptionFactory.systemError(e);
        }
        // 作成したCSVファイルの絶対パスを返却
        return cp.toAbsolutePath().toString();
    }

    /**
     * モバイル実績取込バッチを実行する
     * 
     * @param dto
     * @param dbManagementSession
     * @return
     * @throws InterruptedException
     */
    private Z9999W01JobStateBean exeMobileJissekiTorikomi(C0815W11UpdateDto dto,
        DbManagementSession dbManagementSession, String filename, UserSessionForm userSessionForm,
        S07T004TEntity s07t004tEntity) {

        // 処理用の独自パラメータ設定
        C0815B03UpdateDto pdfOutputDto =
            createC0815B03UpdateDto(dto, dbManagementSession, filename, s07t004tEntity);

        // DB管理セッション情報プログラムID設定（※本サンプルのプログラムIDはテーブル項目の桁数を超過してしまうため、桁数に合わせて調整）
        dbManagementSession.setProgramId("C0815B03");

        // 非同期処理の受付用パラメータModel設定
        AsyncManagementServiceModel<C0815B03UpdateDto> asyncManagementServiceModel =
            new AsyncManagementServiceModel<C0815B03UpdateDto>(pdfOutputDto, userSessionForm,
                dbManagementSession, c0815b03Service);

        // 非同期業務処理実行＆受付ID取得
        Z9999W01JobStateBean jobStateBean =
            c0815b03ManagementService.entryAsyncService(asyncManagementServiceModel);
        return jobStateBean;
    }

    /**
     * 検索結果Entityを生成する
     * 
     * @param dto C0815W11SearchConditionDto
     * @return S04T001TC0815W11Entity
     */
    private S04T001TC0815W11Entity createSearchEntity(C0815W11SearchConditionDto dto) {
        S04T001TC0815W11Entity e = new S04T001TC0815W11Entity();

        if (!ObjectUtils.isEmpty(dto)) {
            e.setApkihoncd(dto.getApkihoncd()); // ＡＰ基本コード
            e.setShopcd(dto.getShopcd()); // ショップコード
            e.setDlvrykyotennm(dto.getDlvrykyotennm()); // 配達拠点
            e.setDlvrykbn1(dto.getDlvrykbn1()); // 自社便
            e.setDlvrykbn2(dto.getDlvrykbn2()); // 他社便
            e.setDlvrycmpnynm(dto.getDlvrycmpnynm()); // 配達会社名称
            e.setDlvrykbn3(dto.getDlvrykbn3()); // 持ち帰り
            e.setDnpysts(dto.getDnpysts()); // 伝票ステータス
            e.setDlvrytnshamei(dto.getDlvrytnshamei()); // 配達担当者
            e.setDlvrytnshacd(dto.getDlvrytnshacd()); // 配達担当者コード
            e.setDlvryareanm(dto.getDlvryareanm()); // 配達エリア
            e.setDlvrydd(dto.getDlvrydd()); // 配達日
            e.setDlvryddkbn(dto.getDlvryddkbn()); // 配達日指定区分
            e.setDnpynostr(dto.getDnpynostr()); // 伝票番号開始
            e.setDnpynoend(dto.getDnpynoend()); // 伝票番号終了
            e.setApkihoncd2(dto.getApkihoncd2()); // ＡＰ基本コード２
            e.setShopcd2(dto.getShopcd2()); // ショップコード２
            e.setTokuisakicd(dto.getTokuisakicd()); // 得意先コード
            e.setOutputseigyo(getOutputseigyo(dto)); // 出力制御
            e.setOrderd(dto.getOrderd()); // 出力順
            e.setSkyshimedd(dto.getSkyshimedd()); // 請求締日
            e.setApkihoncd3(dto.getApkihoncd3()); // ＡＰ基本コード３
            e.setShopcd3(dto.getShopcd3()); // ショップコード３
            e.setSkysakicd(dto.getSkysakicd()); // 請求先コード
            e.setSysapkihoncd(CommonConstants.APCD_000); // システム管理マスタ用AP基本コード
            e.setSysshopcd(CommonConstants.SYS_SHOPCD_000); // システム管理マスタ用ショップコード
            e.setAcptsetteino(CommonConstants.SYS_SETTEINO_052); // 受注区分取得用設定番号
            e.setSysrcrdkbn(CommonConstants.RECORD_KBN_M); // システム管理マスタ用レコード区分
            e.setYukokbn(CommonConstants.YUKOKBN_1); // 有効区分
            e.setMaxrow(getMaxrow(dto)); // 最大件数
        }

        return e;
    }

    /**
     * 請求締処理管理テーブル登録用Entityを生成する
     * 
     * @param dto C0815W11UpdateDto
     * @param dbManagementSession DbManagementSession
     * @return S07T004TEntity
     */
    private S07T004TEntity createInsertS07T004TEntity(C0815W11UpdateDto dto,
        DbManagementSession dbManagementSession) {
        S07T004TEntity e = new S07T004TEntity();
        e.setApkihoncd(dto.getApkihoncd()); // AP基本コード
        e.setShopcd(dto.getShopcd()); // ショップコード
        e.setToshaapkihoncd(dbManagementSession.getApkihoncd()); // 登録者AP基本コード
        e.setToshashopcd(dbManagementSession.getShopcd()); // 登録者ショップコード
        e.setToshacd(dbManagementSession.getTnshacd()); // 登録者コード
        e.setInptsystmymd(dbManagementSession.getSystemDate()); // 登録システム日付
        e.setInptsystmjikan(dbManagementSession.getSystemTime()); // 登録システム時間
        e.setInptunyoymd(dbManagementSession.getUnyoDate()); // 登録運用日付
        e.setInptipadrse(dbManagementSession.getIpAddress()); // 登録IPアドレス
        e.setInptpgmid(dbManagementSession.getProgramId()); // 登録プログラムID
        return e;
    }

    /**
     * 請求締処理管理テーブルEntityに更新内容を反映する
     * 
     * @param e 請求締処理管理テーブルEntity
     * @param dbManagementSession DB管理セッション情報
     * @param prcsjotai 処理状態
     */
    private void fillPropertiesForUpdate(S07T004TEntity e, DbManagementSession dbManagementSession,
        String prcsjotai) {
        e.setPrcsmode(CommonConstants.KBN_PRCSKBN1_SHINKI); // 処理モード
        e.setShimedydm(dbManagementSession.getSystemDate().substring(0, 6)); // 締年月
        e.setShimedd(dbManagementSession.getSystemDate().substring(6, 8)); // 締日
        e.setKonkaishimedd(dbManagementSession.getSystemDate()); // 今回締日
        e.setIraidd(dbManagementSession.getSystemDate()); // 依頼日
        e.setIraijikan(dbManagementSession.getSystemTime()); // 依頼時間
        e.setPrcsjotai(prcsjotai); // 処理状態
        e.setKoshaapkihoncd(dbManagementSession.getApkihoncd()); // 更新者AP基本コード
        e.setKoshashopcd(dbManagementSession.getShopcd()); // 更新者ショップコード
        e.setKoshacd(dbManagementSession.getTnshacd()); // 更新者コード
        e.setKshnsystmymd(dbManagementSession.getSystemDate()); // 更新システム日付
        e.setKshnsystmjikan(dbManagementSession.getSystemTime()); // 更新システム時間
        e.setKshnunyoymd(dbManagementSession.getUnyoDate()); // 更新運用日付
        e.setKshnipadrse(dbManagementSession.getIpAddress()); // 更新IPアドレス
        e.setKshnpgmid(dbManagementSession.getProgramId()); // 更新プログラムID
    }

    /**
     * モバイル実績取込バッチ用パラメータの作成
     * 
     * @param dbManagementSession
     * @param filename
     * @return C0815B03UpdateDto
     */
    private C0815B03UpdateDto createC0815B03UpdateDto(C0815W11UpdateDto dto,
        DbManagementSession dbManagementSession, String filename, S07T004TEntity s07t004tEntity) {
        C0815B03UpdateDto c0815b03UpdateDto = new C0815B03UpdateDto();
        if (!ObjectUtils.isEmpty(dto) && !ObjectUtils.isEmpty(filename)
            && !ObjectUtils.isEmpty(dbManagementSession)) {
            c0815b03UpdateDto.setTnshaapkihoncd(dbManagementSession.getApkihoncd());
            c0815b03UpdateDto.setTnshashopcd(dbManagementSession.getShopcd());
            c0815b03UpdateDto.setTnshacd(dbManagementSession.getTnshacd());
            c0815b03UpdateDto.setIpadrse(dbManagementSession.getIpAddress());
            c0815b03UpdateDto.setFilename(filename);
            c0815b03UpdateDto.setWebmobilekbn("0");
            c0815b03UpdateDto.setS07t004tEntity(s07t004tEntity);
            c0815b03UpdateDto.setUpdateDto(dto);
            c0815b03UpdateDto.setDbManagementSession(dbManagementSession);
        }

        return c0815b03UpdateDto;
    }

    /**
     * 請求締処理管理テーブル更新用Entityを生成する
     * 
     * @param dto C0815W11UpdateDto
     * @param isStandbyStateflg
     * @return S07T004TEntityExample
     */
    private S07T004TEntityExample createUpdateS07T004TEntityExample(C0815W11UpdateDto dto,
        boolean isStandbyStateflg) {
        S07T004TEntityExample example = new S07T004TEntityExample();

        if (isStandbyStateflg) {
            S07T004TEntityExample.Criteria criteria = example.createCriteria();
            criteria.andApkihoncdEqualTo(dto.getApkihoncd());
            criteria.andShopcdEqualTo(dto.getShopcd());
            criteria.andPrcsjotaiEqualTo(CommonConstants.PRCSJOTAI_0);
        } else {
            S07T004TEntityExample.Criteria criteria = example.createCriteria();
            List<String> prcsjotaiList =
                Arrays.asList(CommonConstants.PRCSJOTAI_1, CommonConstants.PRCSJOTAI_RUNNING);
            criteria.andApkihoncdEqualTo(dto.getApkihoncd());
            criteria.andShopcdEqualTo(dto.getShopcd());
            criteria.andPrcsjotaiIn(prcsjotaiList);
        }

        return example;
    }

    /**
     * インボイス施行日を取得する
     * 
     * @param dto 配達実績DTO
     * @return インボイス施行日
     */
    private String getInvoiceDay(C0815W11DlvryJissekiDto dto) {
        return getInvoiceDay(dto.getApkihoncd(), dto.getShopcd());
    }

    /**
     * ショップマスタからインボイス施行日を取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @return インボイス施行日（取得できない場合は空文字）
     */
    private String getInvoiceDay(String apkihoncd, String shopcd) {
        return getShopMasterEntity(apkihoncd, shopcd).map(S02M046TEntity::getInvoicshikoymd)
            .orElse(SystemConstants.WORD_BLANK);
    }

    /**
     * 得意先設置サーバを連携ショップかどうかを取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @return 連携する場合true
     */
    private boolean getShopIsLinkagedWsvr(String apkihoncd, String shopcd) {
        return getShopMasterEntity(apkihoncd, shopcd)
            // 得意先設置サーバ連携区分
            .map(S02M046TEntity::getTtstupwsvrenkeikbn)
            // ONかどうかを判定してbooleanで返す
            .map(kbn -> util.isFlagged(kbn))
            // ショップマスタからデータが取れないときはfalse
            .orElse(false);
    }

    /**
     * ショップマスタからショップ情報を取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @return ショップ情報（有効なデータが存在しない場合はempty）
     */
    private Optional<S02M046TEntity> getShopMasterEntity(String apkihoncd, String shopcd) {
        S02M046TEntityExample example = new S02M046TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd)
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        return s02m046tDao.selectByExample(example).stream().findFirst();
    }

    /**
     * 得意先マスタを検索
     * 
     * @param dto C0815W11DlvryJissekiDto
     * @return S02M013TEntity
     */
    private S02M013TEntity selectS02M013TEntity(C0815W11DlvryJissekiDto dto) {
        S02M013TEntityExample example = new S02M013TEntityExample();
        S02M013TEntityExample.Criteria criteria = example.createCriteria();
        criteria.andApkihoncdEqualTo(dto.getApkihoncd());
        criteria.andShopcdEqualTo(dto.getShopcd());
        criteria.andTokuisakicdEqualTo(dto.getTokuisakicd());
        criteria.andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

        S02M013TEntity ret = null;
        List<S02M013TEntity> list = s02m013tDao.selectByExample(example);
        if (!ListUtils.isEmpty(list)) {
            ret = list.get(0);
        }
        return ret;
    }

    /**
     * 得意先マスタを検索（委託相手）
     * 
     * @param entity S02M013TEntity
     * @return List<S02M013TEntity>
     */
    private List<S02M013TEntity> selectS02M013TEntityForItakuaite(S02M013TEntity entity) {
        S02M013TEntityExample example = new S02M013TEntityExample();
        List<S02M013TEntity> retList = null;
        S02M013TEntityExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(entity.getItakuaiteapkihoncd())) {
            criteria.andApkihoncdEqualTo(entity.getItakuaiteapkihoncd());
        } else {
            criteria.andApkihoncdIsNull();
        }
        if (!StringUtils.isEmpty(entity.getItakuaiteshopcd())) {
            criteria.andShopcdEqualTo(entity.getItakuaiteshopcd());
        } else {
            criteria.andShopcdIsNull();
        }
        if (!StringUtils.isEmpty(entity.getItakuaitetokuisakicd())) {
            criteria.andTokuisakicdEqualTo(entity.getItakuaitetokuisakicd());
        } else {
            criteria.andTokuisakicdIsNull();
        }
        criteria.andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

        retList = s02m013tDao.selectByExample(example);

        return retList;
    }

    /**
     * 受注配達テーブルを検索
     * 
     * @param dto C0815W11DlvryJissekiDto
     * @return S04T001TEntity
     */
    private S04T001TEntity selectS04T001TEntity(C0815W11DlvryJissekiDto dto) {
        S04T001TEntityExample example = new S04T001TEntityExample();
        S04T001TEntityExample.Criteria criteria = example.createCriteria();
        criteria.andApkihoncdEqualTo(dto.getApkihoncd());
        criteria.andShopcdEqualTo(dto.getShopcd());
        criteria.andAcptdnpynoEqualTo(dto.getAcptdnpyno());
        criteria.andEdanoEqualTo(Short.valueOf(dto.getAcptedano()));
        criteria.andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

        S04T001TEntity ret = null;
        List<S04T001TEntity> list = s04t001tDao.selectByExample(example);
        if (!ListUtils.isEmpty(list)) {
            ret = list.get(0);
        }
        return ret;
    }

    /**
     * 売上テーブルを検索
     * 
     * @param dto C0815W11DlvryJissekiDto
     * @param entity S04T001TEntity
     * @return S06T001TEntity
     */
    private S06T001TEntity selectS06T001TEntity(C0815W11DlvryJissekiDto dto,
        S04T001TEntity entity) {
        S06T001TEntityExample example = new S06T001TEntityExample();
        S06T001TEntityExample.Criteria criteria = example.createCriteria();
        criteria.andApkihoncdEqualTo(dto.getApkihoncd());
        criteria.andShopcdEqualTo(dto.getShopcd());
        criteria.andUridnpynoEqualTo(
            !StringUtils.isEmpty(entity.getUridnpyno()) ? entity.getUridnpyno() : "");
        criteria.andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

        S06T001TEntity ret = null;
        List<S06T001TEntity> list = s06t001tDao.selectByExample(example);
        if (!ListUtils.isEmpty(list)) {
            ret = list.get(0);
        }
        return ret;
    }

    /**
     * 請求残高テーブルを検索
     * 
     * @param dto C0815W11DlvryJissekiDto
     * @param s02m013tEntity s02m013tEntity
     * @param s04t001Entity s04t001Entity
     * @param s06t001tEntity s06t001tEntity
     * @return S07T001TEntity
     */
    private List<S07T001TEntity> selectS07T001TEntity(C0815W11DlvryJissekiDto dto,
        S02M013TEntity s02m013tEntity, S04T001TEntity s04t001Entity,
        S06T001TEntity s06t001tEntity) {
        S07T001TEntityExample example = new S07T001TEntityExample();
        List<S07T001TEntity> ret = new ArrayList<>();
        if (CommonConstants.FLG_ON.equals(dto.getAcptthdaibikiflg())) {
            if (!ObjectUtils.isEmpty(s02m013tEntity) && !ObjectUtils.isEmpty(s04t001Entity)) {
                S07T001TEntityExample.Criteria criteria = example.createCriteria();
                criteria.andApkihoncdEqualTo(dto.getApkihoncd());
                criteria.andShopcdEqualTo(dto.getShopcd());
                if (!StringUtils.isEmpty(s02m013tEntity.getSkysakicd())) {
                    criteria.andSkysakicdEqualTo(s02m013tEntity.getSkysakicd());
                } else {
                    criteria.andSkysakicdIsNull();
                }
                criteria.andTokuisakicdEqualTo(dto.getTokuisakicd());
                if (!StringUtils.isEmpty(s04t001Entity.getDlvrydd())) {
                    criteria.andShimedydmEqualTo(s04t001Entity.getDlvrydd().substring(0, 4));
                    criteria.andShimeddEqualTo(s04t001Entity.getDlvrydd().substring(4, 6));
                } else {
                    criteria.andShimedydmIsNull();
                    criteria.andShimeddIsNull();
                }
                criteria.andSkyprcsjissikbnEqualTo(
                    Short.valueOf(CommonConstants.SKYPRCSJISSIKBN_DAIBIKI));
                criteria.andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
                ret = s07t001tDao.selectByExample(example);
            }
        } else {
            S07T001TEntityExample.Criteria criteria = example.createCriteria();
            criteria.andApkihoncdEqualTo(dto.getApkihoncd());
            criteria.andShopcdEqualTo(dto.getShopcd());
            if (!StringUtils.isEmpty(s02m013tEntity.getSkysakicd())) {
                criteria.andSkysakicdEqualTo(s02m013tEntity.getSkysakicd());
            } else {
                criteria.andSkysakicdIsNull();
            }
            criteria.andTokuisakicdEqualTo(dto.getTokuisakicd());
            if (!StringUtils.isEmpty(s06t001tEntity.getSkyshimedydm())) {
                criteria.andShimedydmEqualTo(s06t001tEntity.getSkyshimedydm());
            } else {
                criteria.andShimedydmIsNull();
            }
            if (!StringUtils.isEmpty(s06t001tEntity.getSkyshimedd())) {
                criteria.andShimeddEqualTo(s06t001tEntity.getSkyshimedd());
            } else {
                criteria.andShimeddIsNull();
            }
            criteria.andSkyprcsjissikbnEqualTo(
                Short.valueOf(CommonConstants.SKYPRCSJISSIKBN_TSUKIJIME));
            criteria.andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
            ret = s07t001tDao.selectByExample(example);
        }

        return ret;
    }

    /**
     * ショップマスタを検索
     * 
     * @param dto C0815W11DlvryJissekiDto
     * @return S02M046TEntity
     */
    private S02M046TEntity selectS02M046TEntity(C0815W11DlvryJissekiDto dto) {
        S02M046TEntityExample example = new S02M046TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(dto.getApkihoncd())
            .andShopcdEqualTo(dto.getShopcd()).andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

        S02M046TEntity ret = null;
        List<S02M046TEntity> list = s02m046tDao.selectByExample(example);
        if (!ListUtils.isEmpty(list)) {
            ret = list.get(0);
        }
        return ret;
    }

    /**
     * 受注配達明細テーブルを検索
     * 
     * @param dlvryjissekiDto C0815W11DlvryJissekiDto
     * @param acptdlvrymeisaiDto C0815W11AcptDlvryMeisaiDto
     * @return S04T002TEntity
     */
    private S04T002TEntity selectS04T002TEntity(C0815W11DlvryJissekiDto dlvryjissekiDto,
        C0815W11AcptDlvryMeisaiDto acptdlvrymeisaiDto) {
        S04T002TEntityExample s04t002tExample = new S04T002TEntityExample();
        s04t002tExample.createCriteria().andApkihoncdEqualTo(dlvryjissekiDto.getApkihoncd())
            .andShopcdEqualTo(dlvryjissekiDto.getShopcd())
            .andAcptdnpynoEqualTo(dlvryjissekiDto.getAcptdnpyno())
            .andEdanoEqualTo(Short.valueOf(dlvryjissekiDto.getAcptedano()))
            .andAcptdnpymeisainoEqualTo(Integer.valueOf(acptdlvrymeisaiDto.getAcptdnpymeisaino()))
            .andAcptdnpymeisaiedanoEqualTo(
                Short.valueOf(acptdlvrymeisaiDto.getAcptdnpymeisaiedano()))
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

        S04T002TEntity ret = null;
        List<S04T002TEntity> list = s04t002tDao.selectByExample(s04t002tExample);
        if (!ListUtils.isEmpty(list)) {
            ret = list.get(0);
        }
        return ret;
    }

    /**
     * 売上テーブルリストを検索
     * 
     * @param dlvryjissekiDto C0815W11DlvryJissekiDto
     * @return List<S06T001TEntity>
     */
    private List<S06T001TEntity> selectS06T001TEntityList(C0815W11DlvryJissekiDto dlvryjissekiDto) {
        S06T001TEntityExample s06t001tExample = new S06T001TEntityExample();
        s06t001tExample.createCriteria().andApkihoncdEqualTo(dlvryjissekiDto.getApkihoncd())
            .andShopcdEqualTo(dlvryjissekiDto.getShopcd())
            .andAcptdnpynoEqualTo(dlvryjissekiDto.getAcptdnpyno())
            .andAcptdnpyedanoEqualTo(Short.valueOf(dlvryjissekiDto.getAcptedano()))
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        return s06t001tDao.selectByExample(s06t001tExample);
    }

    /**
     * 請求締処理管理テーブルを検索
     * 
     * @param s04t001tEntity 受注配達明細テーブルEntity
     * @return 請求締処理管理テーブルEntity
     */
    private Optional<S07T004TEntity> selectS07T004TEntity(S04T001TEntity s04t001tEntity) {
        S07T004TEntityKey key = new S07T004TEntityKey();
        key.setApkihoncd(s04t001tEntity.getApkihoncd());
        key.setShopcd(s04t001tEntity.getShopcd());
        return Optional.ofNullable(s07t004tDao.selectByPrimaryKey(key));
    }

    /**
     * 最大締年月日を検索
     * 
     * @param dto C0815W11DlvryJissekiDto
     * @param s02m013tEntity S02M013TEntity
     * @return String
     */
    private String getMaxSkyshimeymd(C0815W11DlvryJissekiDto dto, S02M013TEntity s02m013tEntity) {
        S07T001TEntityExample example = new S07T001TEntityExample();
        S07T001TEntityExample.Criteria criteria = example.createCriteria();
        criteria.andApkihoncdEqualTo(dto.getApkihoncd());
        criteria.andShopcdEqualTo(dto.getShopcd());
        if (!StringUtils.isEmpty(s02m013tEntity.getSkysakicd())) {
            criteria.andSkysakicdEqualTo(s02m013tEntity.getSkysakicd());
        } else {
            criteria.andSkysakicdIsNull();
        }
        criteria.andTokuisakicdEqualTo(dto.getTokuisakicd());
        criteria.andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        String maxSkyshimeymd = SystemConstants.WORD_BLANK;
        for (S07T001TEntity entity : s07t001tDao.selectByExample(example)) {
            String skyshimeymd = Objects.toString(new StringBuilder()
                .append(BusinessUtil.getString(entity.getShimedydm(), SystemConstants.WORD_BLANK))
                .append(BusinessUtil.getString(entity.getShimedd(), SystemConstants.WORD_BLANK)));
            if (DateTimeUtil.isLater(maxSkyshimeymd, skyshimeymd)) {
                maxSkyshimeymd = skyshimeymd;
            }
        }

        return maxSkyshimeymd;
    }

    /**
     * 最大明細件数を検索
     * 
     * @param dto C0815W11SearchConditionDto
     * @return String
     */
    private String getMaxrow(C0815W11SearchConditionDto dto) {
        S02M006TEntityExample example = new S02M006TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(CommonConstants.APCD_000)
            .andShopcdEqualTo(CommonConstants.SYS_SHOPCD_000)
            .andSetteinoEqualTo(CommonConstants.SYS_SETTEINO_035)
            .andRcrdkbnEqualTo(CommonConstants.RECORD_KBN_M)
            .andSetteicdEqualTo(CommonConstants.SETTEICD_035_1)
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

        String ret = null;
        List<S02M006TEntity> list = s02m006tDao.selectByExample(example);
        if (!ListUtils.isEmpty(list)) {
            ret = list.get(0).getMojisetteiatai();
        }
        return ret;
    }

    /**
     * 出力制御を検索
     * 
     * @param dto C0815W11SearchConditionDto
     * @return String
     */
    private String getOutputseigyo(C0815W11SearchConditionDto dto) {
        S02M006TEntityExample example = new S02M006TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(dto.getApkihoncd())
            .andShopcdEqualTo(dto.getShopcd()).andSetteinoEqualTo(CommonConstants.SYS_SETTEINO_140)
            .andSetteicdEqualTo(CommonConstants.SETTEICD_140_1)
            .andRcrdkbnEqualTo(CommonConstants.RECORD_KBN_M)
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);

        String ret = null;
        List<S02M006TEntity> list = s02m006tDao.selectByExample(example);
        if (!ListUtils.isEmpty(list)) {
            ret = list.get(0).getMojisetteiatai();
        }
        return ret;
    }

    /**
     * nullを空文字に変換する
     * 
     * @param str
     * @return String
     */
    private String nullToBlank(String str) {
        if (str == null) {
            str = "";
        }
        return str;
    }

    /*
     * チェックボックスの値を取得
     */
    private String getFlgValue(String flg) {
        String ret = CommonConstants.FLG_OFF;
        if (!StringUtils.isEmpty(flg) && CommonConstants.FLG_ON.equals(flg)) {
            ret = CommonConstants.FLG_ON;
        }

        return ret;
    }

    /**
     * リトライ間隔の間に待機させる処理
     * 
     * @throws InterruptedException
     */
    private final synchronized void waitRetry() throws InterruptedException {

        try {
            // 設定時間分待機する
            long interval = appSystemProperties.getAsyncRetryInterval().longValue();
            wait(interval);
        } catch (InterruptedException ie) {
            throw ie;
        }
    }
}
