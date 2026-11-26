package jp.co.aquaclara.accis.web.domain.service.d1501;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M046TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R30Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R34List1Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R34List1ResultEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501W00AciKeiyuConditionEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501W00KaishuHohoConditionEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501W31Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501W31SearchConditionEntity;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.web.domain.dto.d1501.D1501R30PrintDto;
import jp.co.aquaclara.accis.web.domain.dto.d1501.D1501R30TargetMeisaiDto;
import jp.co.aquaclara.accis.web.domain.dto.d1501.D1501W31SearchConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.d1501.D1501W31SearchResultDto;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import jp.co.aquaclara.accis.web.exception.dto.FormField;

/**
 * 請求関連処理（請求書）-Service.
 * 
 * @author accisdev047
 *
 */
@Service
public class D1501W31Service {
    /**
     * 請求関連処理ユーティリティサービスクラス
     */
    @Autowired
    private D1501W00Service d1501w00service;

    @Autowired
    private S07T001TDao dao;

    /**
     * ショップマスタDao
     */
    @Autowired
    private S02M046TDao s02m046tDao;

    /**
     * 請求書検索を実行
     * 
     * @param conditionDto 請求書検索条件DTO
     * @return 請求書検索結果
     */
    public List<D1501W31SearchResultDto> search(D1501W31SearchConditionDto conditionDto) {
        // 回収方法選択リスト取得
        List<S07T001TD1501W00KaishuHohoConditionEntity> kaishuHohoList =
            S07T001TD1501W00KaishuHohoConditionEntity.create(conditionDto.getKaishuhohokbn1(),
                conditionDto.getKaishuhohokbn2(), conditionDto.getKaishuhohokbn3(),
                conditionDto.getKaishuhohokbn4(), conditionDto.getKaishuhohokbn5(),
                conditionDto.getKaishuhohokbn6(), conditionDto.getKaishuhohokbn7(),
                conditionDto.getKaishuhohokbn8(), conditionDto.getKaishuhohokbn9(),
                conditionDto.getKaishuhohokbn10(), conditionDto.getKaishuhohokbn11(),
                conditionDto.getKaishuhohocd2(), conditionDto.getKaishuhohocd5());

        // ACI本部NA区分リスト取得
        List<S07T001TD1501W00AciKeiyuConditionEntity> acikeiyuflgList =
            S07T001TD1501W00AciKeiyuConditionEntity.createCondition(conditionDto.getAcikeiyuflg1(),
                conditionDto.getAcikeiyuflg2());

        // 検索条件を生成する
        S07T001TD1501W31SearchConditionEntity entity =
            createSearchEntity(conditionDto, kaishuHohoList, acikeiyuflgList);
        // 検索実行
        List<S07T001TD1501W31Entity> searchResultList = dao.selectForD1501W31List(entity);
        // 検索結果が0件の場合エラー
        if (searchResultList.isEmpty()) {
            throw BusinessExceptionFactory.dataNotFound(MessageID.W083);
        }
        // APIコントローラ返却用DTO作成
        List<D1501W31SearchResultDto> resultDto = createSearchResultDto(searchResultList);
        return resultDto;
    }

    /**
     * 検索結果のEntityから検索結果DTOを作成する
     * 
     * @param searchResultList 請求書検索結果Entityリスト
     * @return 請求書検索結果一覧DTOリスト
     */
    private List<D1501W31SearchResultDto> createSearchResultDto(
        List<S07T001TD1501W31Entity> searchResultList) {
        List<D1501W31SearchResultDto> resultDto = new ArrayList<>();

        for (S07T001TD1501W31Entity entity : searchResultList) {
            D1501W31SearchResultDto dto = new D1501W31SearchResultDto();
            dto.setApkihoncd(entity.getApkihoncd());
            dto.setShopcd(entity.getShopcd());
            dto.setSkysakicd(entity.getSkysakicd());
            dto.setSkysakimei(entity.getSkysakinm());
            dto.setJigyoshocd(entity.getJigyoshocd());
            dto.setShimeddmei(DateTimeUtil.toSlashedDate(entity.getShimedd()));
            dto.setKaishuhohomei(entity.getKaishuhohokbnnm());
            dto.setTairyukbnmei(entity.getTairyukbnnm());
            dto.setPrcsjotai(entity.getSyoristatus());
            dto.setPrcsjotaimei(d1501w00service.getPrcsjotaime(entity.getSyoristatus()));
            resultDto.add(dto);
        }
        return resultDto;
    }

    /**
     * 請求書検索用Entityを作成する
     * 
     * @param conditionDto 請求書検索条件DTO
     * @param kaishuHohoList 請求残高テーブル回収方法検索条件エンティティのリスト
     * @param acikeiyuflgList ACI本部NA区分検索条件Entiryリスト
     * @return 請求書検索用Entity
     */
    private S07T001TD1501W31SearchConditionEntity createSearchEntity(
        D1501W31SearchConditionDto conditionDto,
        List<S07T001TD1501W00KaishuHohoConditionEntity> kaishuHohoList,
        List<S07T001TD1501W00AciKeiyuConditionEntity> acikeiyuflgList) {

        S07T001TD1501W31SearchConditionEntity entity = new S07T001TD1501W31SearchConditionEntity();
        // AP基本コード
        entity.setApkihoncd(conditionDto.getApkihoncd());
        // ショップコード
        entity.setShopcd(conditionDto.getShopcd());
        // 事業所コード
        if (!StringUtils.isEmpty(conditionDto.getJigyoshocd())) {
            entity.setJigyoshocd(conditionDto.getJigyoshocd());
        }
        // 締年月
        entity.setShimedydm(conditionDto.getShimedd1());
        // 締日
        entity.setShimedd(conditionDto.getShimedd3());
        // 抽出締日
        entity.setExtractshimedd(conditionDto.getSkyshimeday());
        // 対象データ
        entity.setTrgtdat(conditionDto.getTrgtdat());
        // 請求金額ゼロ円の請求（明細あり）
        if (!StringUtils.isEmpty(conditionDto.getSkykinn0ensky1())) {
            entity.setSkykinn0ensky1(conditionDto.getSkykinn0ensky1());
        }
        // 請求金額ゼロ円の請求（明細なし）
        if (!StringUtils.isEmpty(conditionDto.getSkykinn0ensky2())) {
            entity.setSkykinn0ensky2(conditionDto.getSkykinn0ensky2());
        }
        // 請求金額ゼロ円の請求（過入金）
        if (!StringUtils.isEmpty(conditionDto.getSkykinn0ensky3())) {
            entity.setSkykinn0ensky3(conditionDto.getSkykinn0ensky3());
        }
        // 集金日
        if (!StringUtils.isEmpty(conditionDto.getShukindd())) {
            entity.setShukindd(conditionDto.getShukindd());
        }
        // 印刷種類（請求書）
        if (!StringUtils.isEmpty(conditionDto.getSkyshoprntkbn())) {
            entity.setSkyshoprntkbn(conditionDto.getSkyshoprntkbn());
        }
        // 印刷種類（控え）
        if (!StringUtils.isEmpty(conditionDto.getHikaeprntkbn())) {
            entity.setHikaeprntkbn(conditionDto.getHikaeprntkbn());
        }
        // 印刷種類（領収書）
        if (!StringUtils.isEmpty(conditionDto.getRyoshushoprntkbn())) {
            entity.setRyoshushoprntkbn(conditionDto.getRyoshushoprntkbn());
        }
        // 出力順
        entity.setOtptjun(conditionDto.getOtptjun());
        // 請求書発送委託（対象のみ）
        if (!StringUtils.isEmpty(conditionDto.getSkyshohassouitaku1())) {
            entity.setSkyshohassouitaku1(conditionDto.getSkyshohassouitaku1());
        }
        // 請求書発送委託（対象を除く）
        if (!StringUtils.isEmpty(conditionDto.getSkyshohassouitaku2())) {
            entity.setSkyshohassouitaku2(conditionDto.getSkyshohassouitaku2());
        }
        // 営業担当者コード
        if (!StringUtils.isEmpty(conditionDto.getEigyotnshacd())) {
            entity.setEigyotnshacd(conditionDto.getEigyotnshacd());
        }
        // 回収担当者コード
        if (!StringUtils.isEmpty(conditionDto.getKaishutnshacd())) {
            entity.setKaishutnshacd(conditionDto.getKaishutnshacd());
        }
        // 請求先コード
        if (!StringUtils.isEmpty(conditionDto.getSkysakicd())) {
            entity.setSkysakicd(conditionDto.getSkysakicd());
        }
        // カナ名称
        if (!StringUtils.isEmpty(conditionDto.getSkysakinmkana())) {
            entity.setSkysakinmkana(conditionDto.getSkysakinmkana());
        }
        // 得意先有無（有）
        if (!StringUtils.isEmpty(conditionDto.getTtumu1())) {
            entity.setTtumu1(conditionDto.getTtumu1());
        }
        // 得意先有無（無）
        if (!StringUtils.isEmpty(conditionDto.getTtumu2())) {
            entity.setTtumu2(conditionDto.getTtumu2());
        }
        // 請求先管理区分
        entity.setSkysakikanrikbn(conditionDto.getSkysakikanrikbn());
        // 請求区分(親子)
        if (!StringUtils.isEmpty(conditionDto.getSkykbn1())) {
            entity.setSkykbn1(conditionDto.getSkykbn1());
        }
        // 請求区分（一般）
        if (!StringUtils.isEmpty(conditionDto.getSkykbn2())) {
            entity.setSkykbn2(conditionDto.getSkykbn2());
        }
        // 早出し
        if (!StringUtils.isEmpty(conditionDto.getHayadasiflg())) {
            entity.setHayadasiflg(conditionDto.getHayadasiflg());
        }
        // 回収方法リスト
        entity.setKaishuHohoList(kaishuHohoList);
        // 請求明細送付区分(送付する)
        if (!StringUtils.isEmpty(conditionDto.getSkymeisaisofukbn1())) {
            entity.setSkymeisaisofukbn1(conditionDto.getSkymeisaisofukbn1());
        }
        // 請求明細送付区分（送付しない）
        if (!StringUtils.isEmpty(conditionDto.getSkymeisaisofukbn2())) {
            entity.setSkymeisaisofukbn2(conditionDto.getSkymeisaisofukbn2());
        }
        // 明細最大件数
        entity.setMeisaimaxline(conditionDto.getMeisaimaxline());

        // ACI本部NA区分リスト
        entity.setAcikeiyuflgList(acikeiyuflgList);

        return entity;
    }

    /**
     * 検索実行時の相関チェックを実行
     * 
     * @param conditionDto 請求書検索条件DTO
     */
    public void correlationCheck(D1501W31SearchConditionDto conditionDto) {
        // 締年月が範囲内であることをチェック
        if (!d1501w00service.isWithinDateRange(conditionDto.getShimedd1())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.shimedd1", "請求書", 0), MessageID.W033, "締年月",
                CommonConstants.YEARMONTH_MIN, CommonConstants.YEARMONTH_MAX);
        }
        // 印刷種類のチェックボックスが選択されていることをチェック
        if (d1501w00service.isPrintkbnEmpty(conditionDto.getSkyshoprntkbn(),
            conditionDto.getHikaeprntkbn(), conditionDto.getRyoshushoprntkbn())) {
            throw BusinessExceptionFactory.validationFailed(
                d1501w00service.getPrintkbnFormFieldList(), MessageID.W091, "印刷種類");
        }
        // 請求書発送委託のチェックボックスが選択されていることをチェック
        if (d1501w00service.isSkyshohassouitakuEmpty(conditionDto.getSkyshohassouitaku1(),
            conditionDto.getSkyshohassouitaku2())) {
            throw BusinessExceptionFactory.validationFailed(
                d1501w00service.getSkyshohassouitakuFormFieldList(), MessageID.W091, "請求書発送委託");
        }
        // 得意先有無のチェックボックスが選択されていることをチェック
        if (d1501w00service.isTtumuEmpty(conditionDto.getTtumu1(), conditionDto.getTtumu2())) {
            throw BusinessExceptionFactory.validationFailed(d1501w00service.getTtumuFormFieldList(),
                MessageID.W091, "得意先有無");
        }
        // 請求区分のチェックボックスが選択されていることをチェック
        if (d1501w00service.isSkykbnEmpty(conditionDto.getSkykbn1(), conditionDto.getSkykbn2())) {
            throw BusinessExceptionFactory
                .validationFailed(d1501w00service.getSkykbnFormFieldList(), MessageID.W091, "請求区分");
        }
        // 回収方法のチェックボックスが選択されていることをチェック
        if (d1501w00service.isKaishuhohokbnEmpty(conditionDto.getKaishuhohokbn1(),
            conditionDto.getKaishuhohokbn2(), conditionDto.getKaishuhohokbn3(),
            conditionDto.getKaishuhohokbn4(), conditionDto.getKaishuhohokbn5(),
            conditionDto.getKaishuhohokbn6(), conditionDto.getKaishuhohokbn7(),
            conditionDto.getKaishuhohokbn8(), conditionDto.getKaishuhohokbn9(),
            conditionDto.getKaishuhohokbn10(), conditionDto.getKaishuhohokbn11())) {
            throw BusinessExceptionFactory.validationFailed(
                d1501w00service.getKaishuhohokbnFormFieldList(), MessageID.W091, "回収方法");
        }
    }

    /**
     * 請求書対象明細DTOの帳票情報存在チェックを実行し対象となる請求書対象明細DTOリストを返却する
     * 
     * @param printDto 請求書印刷DTO
     * @param targetMeisaiDtoList 請求書対象明細DTOのList
     * @return チェック後の請求書対象明細DTOリスト
     */
    public List<D1501R30TargetMeisaiDto> skyshoExistConfirm(D1501R30PrintDto printDto,
        List<D1501R30TargetMeisaiDto> targetMeisaiDtoList) {
        List<D1501R30TargetMeisaiDto> afterCheckTargetMeisaiDtoList = new ArrayList<>();
        for (D1501R30TargetMeisaiDto targetMeisaiDto : targetMeisaiDtoList) {
            long countResult = countSkyshoPrintTarget(printDto, targetMeisaiDto);
            if (countResult > 0) {
                // 帳票情報が存在する場合返却するDTOに請求先コードを詰めなおす
                afterCheckTargetMeisaiDtoList
                    .add(createTargetMeisaiDto(targetMeisaiDto.getSkysakicd()));
            }
        }
        // 存在チェック結果が0件の場合エラー
        if (afterCheckTargetMeisaiDtoList.isEmpty()) {
            throw BusinessExceptionFactory.dataNotFound(MessageID.W083);
        }
        return afterCheckTargetMeisaiDtoList;
    }

    /**
     * 請求書CSV（コンビニ）情報存在チェック
     * 
     * @param printDto 請求書印刷DTO
     * @param targetMeisaiDtoList 請求書対象明細DTOリスト
     * @return チェック後の請求書対象明細DTOリスト
     */
    public List<D1501R30TargetMeisaiDto> csvcvsExistConfirm(D1501R30PrintDto printDto,
        List<D1501R30TargetMeisaiDto> targetMeisaiDtoList) {
        List<D1501R30TargetMeisaiDto> afterCheckTargetMeisaiDtoList = new ArrayList<>();
        for (D1501R30TargetMeisaiDto targetMeisaiDto : targetMeisaiDtoList) {
            List<S07T001TD1501R34List1ResultEntity> selectResult =
                countCsvcvsPrintTarget(printDto, targetMeisaiDto);
            if (selectResult.size() > 0) {
                // 帳票情報が存在する場合返却するDTOに請求先コードを詰めなおす
                afterCheckTargetMeisaiDtoList
                    .add(createTargetMeisaiDto(targetMeisaiDto.getSkysakicd()));
            }
        }
        // 存在チェック結果が0件の場合エラー
        if (afterCheckTargetMeisaiDtoList.isEmpty()) {
            throw BusinessExceptionFactory.dataNotFound(MessageID.W083);
        }
        return afterCheckTargetMeisaiDtoList;
    }

    /**
     * 請求書印刷対象件数取得を実行し実行結果を返却
     * 
     * @param printDto 請求書印刷DTO
     * @param targetMeisaiDto 請求書対象明細DTO
     * @return カウント結果
     */
    private long countSkyshoPrintTarget(D1501R30PrintDto printDto,
        D1501R30TargetMeisaiDto targetMeisaiDto) {
        S07T001TD1501R30Entity entity = new S07T001TD1501R30Entity();
        entity = createSkyshoEentity(printDto, targetMeisaiDto);
        return dao.selectForD1501R30Count(entity);
    }

    /**
     * 請求書コンビニデータ取得1を実行し実行結果を返却
     * 
     * @param printDto 請求書印刷DTO
     * @param targetMeisaiDto 請求書対象明細DTO
     * @return セレクト結果
     */
    private List<S07T001TD1501R34List1ResultEntity> countCsvcvsPrintTarget(
        D1501R30PrintDto printDto, D1501R30TargetMeisaiDto targetMeisaiDto) {
        S07T001TD1501R34List1Entity entity = new S07T001TD1501R34List1Entity();
        entity = createCsvcvsEentity(printDto, targetMeisaiDto);
        return dao.selectForD1501R34List1(entity);
    }

    /**
     * 請求書CSVコンビニデータ取得１Entityを生成する
     * 
     * @param printDto 請求書印刷DTO
     * @param targetMeisaiDto 請求書対象明細DTO
     * @return 請求書Entity
     */
    private S07T001TD1501R34List1Entity createCsvcvsEentity(D1501R30PrintDto printDto,
        D1501R30TargetMeisaiDto targetMeisaiDto) {
        S07T001TD1501R34List1Entity entity = new S07T001TD1501R34List1Entity();
        // AP基本コード
        entity.setApkihoncd(printDto.getApkihoncd());
        // ショップコード
        entity.setShopcd(printDto.getShopcd());
        // 請求先コード
        entity.setSkysakicd(targetMeisaiDto.getSkysakicd());
        // 締年月
        entity.setShimedydm(printDto.getShimedydm());
        // 締日
        entity.setShimedd(printDto.getSkyshimeday());
        return entity;
    }

    /**
     * 請求書Entityを生成する
     * 
     * @param printDto 請求書印刷DTO
     * @param targetMeisaiDto 請求書対象明細DTO
     * @return 請求書Entity
     */
    private S07T001TD1501R30Entity createSkyshoEentity(D1501R30PrintDto printDto,
        D1501R30TargetMeisaiDto targetMeisaiDto) {
        S07T001TD1501R30Entity entity = new S07T001TD1501R30Entity();
        // AP基本コード
        entity.setApkihoncd(printDto.getApkihoncd());
        // ショップコード
        entity.setShopcd(printDto.getShopcd());
        // 請求先コード
        entity.setSkysakicd(targetMeisaiDto.getSkysakicd());
        // 締年月
        entity.setShimedydm(printDto.getShimedydm());
        // 請求締日
        entity.setSkyshimeday(printDto.getSkyshimeday());
        // 請求締年月日
        entity.setSkyshimedate(printDto.getSkyshimedate());
        // 対象データ
        entity.setTrgtdat(printDto.getTrgtdat());
        // 請求金額ゼロ円の請求（明細あり）
        if (!StringUtils.isEmpty(printDto.getSkykinn0ensky1())) {
            entity.setSkykinn0ensky1(printDto.getSkykinn0ensky1());
        }
        // 請求金額ゼロ円の請求（明細なし）
        if (!StringUtils.isEmpty(printDto.getSkykinn0ensky2())) {
            entity.setSkykinn0ensky2(printDto.getSkykinn0ensky2());
        }
        // 請求金額ゼロ円の請求（過入金）
        if (!StringUtils.isEmpty(printDto.getSkykinn0ensky3())) {
            entity.setSkykinn0ensky3(printDto.getSkykinn0ensky3());
        }
        // 印刷種類（請求書）
        if (!StringUtils.isEmpty(printDto.getSkyshoprntkbn())) {
            entity.setSkyshoprntkbn(printDto.getSkyshoprntkbn());
        }
        // 印刷種類（控え）
        if (!StringUtils.isEmpty(printDto.getHikaeprntkbn())) {
            entity.setHikaeprntkbn(printDto.getHikaeprntkbn());
        }
        // 印刷種類（領収書）
        if (!StringUtils.isEmpty(printDto.getRyoshushoprntkbn())) {
            entity.setRyoshushoprntkbn(printDto.getRyoshushoprntkbn());
        }
        // 出力順
        entity.setOtptjun(printDto.getOtptjun());
        // ログイン担当者AP基本コード
        entity.setLoginUserapkihoncd(printDto.getLoginUserapkihoncd());
        // ログイン担当者ショップコード
        entity.setLoginUsershopcd(printDto.getLoginUsershopcd());
        // ログイン担当者事業者コード
        entity.setLoginUserjigyoshocd(printDto.getLoginUserjigyoshocd());
        // ログイン担当者コード
        entity.setLoginUsertnshacd(printDto.getLoginUsertnshacd());
        return entity;
    }

    /**
     * 印刷実行時の相関チェックを実行
     * 
     * @param conditionDto 請求書検索条件DTO
     * @param targetMeisaiDtoList 請求書対象明細DTOリスト
     */
    public void printCorrelationCheck(D1501W31SearchConditionDto conditionDto,
        List<D1501R30TargetMeisaiDto> targetMeisaiDtoList) {
        // 印刷種類のチェックボックスが選択されていることをチェック
        if (d1501w00service.isPrintkbnEmpty(conditionDto.getSkyshoprntkbn(),
            conditionDto.getHikaeprntkbn(), conditionDto.getRyoshushoprntkbn())) {
            throw BusinessExceptionFactory.validationFailed(
                d1501w00service.getPrintkbnFormFieldList(), MessageID.W091, "印刷種類");
        }
        // 請求先が1件以上選択されていることをチェック
        if (targetMeisaiDtoList.size() == 0) {
            throw BusinessExceptionFactory.validationFailed(MessageID.W091, "請求先");
        }
    }

    /**
     * CSV、CSVコンビニボタン押下時の相関チェックを実行
     * 
     * @param targetMeisaiDtoList 請求書対象明細DTOリスト
     */
    public void csvCorrelationCheck(List<D1501R30TargetMeisaiDto> targetMeisaiDtoList) {
        // 請求先が1件以上選択されていることをチェック
        if (targetMeisaiDtoList.size() == 0) {
            throw BusinessExceptionFactory.validationFailed(MessageID.W091, "請求先");
        }
    }

    /**
     * 請求書対象明細DTOを生成する
     * 
     * @param skysakicd 請求先コード
     * @return 請求書対象明細DTO
     */
    public D1501R30TargetMeisaiDto createTargetMeisaiDto(String skysakicd) {
        D1501R30TargetMeisaiDto targetMeisai = new D1501R30TargetMeisaiDto();
        targetMeisai.setSkysakicd(skysakicd);
        return targetMeisai;
    }

    /**
     * 検索フォームの初期値を請求書検索条件DTOに設定して返却する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @return 請求書検索条件DTO
     */
    public D1501W31SearchConditionDto iniSearchConditionDto(String apkihoncd, String shopcd) {
        // ショップマスタを取得
        S02M046TEntity shopMasterRecord = getShopMasterRecord(apkihoncd, shopcd);
        return new D1501W31SearchConditionDto(shopMasterRecord);
    }

    /**
     * ショップマスタ検索を行う
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @return セレクト結果
     */
    private S02M046TEntity getShopMasterRecord(String apkihoncd, String shopcd) {
        // 検索条件の設定
        S02M046TEntityKey s02m046tEntityKey = new S02M046TEntityKey();
        s02m046tEntityKey.setApkihoncd(apkihoncd);
        s02m046tEntityKey.setShopcd(shopcd);
        // ショップマスタの取得
        return s02m046tDao.selectByPrimaryKey(s02m046tEntityKey);
    }
}
