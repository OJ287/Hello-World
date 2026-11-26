package jp.co.aquaclara.accis.web.domain.service.d1501;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R30Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501W00AciKeiyuConditionEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501W00KaishuHohoConditionEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501W41Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501W41SearchConditionEntity;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.web.domain.dto.d1501.D1501R40PrintDto;
import jp.co.aquaclara.accis.web.domain.dto.d1501.D1501R40TargetMeisaiDto;
import jp.co.aquaclara.accis.web.domain.dto.d1501.D1501W41SearchConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.d1501.D1501W41SearchResultDto;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import jp.co.aquaclara.accis.web.exception.dto.FormField;

/**
 * 請求関連処理（ご利用明細書）-Service.
 * 
 * @author accisdev047
 *
 */
@Service
public class D1501W41Service {
    /**
     * 請求関連処理ユーティリティサービスクラス
     */
    @Autowired
    private D1501W00Service d1501w00service;

    @Autowired
    private S07T001TDao dao;

    /**
     * ご利用明細書検索を実行
     * 
     * @param conditionDto ご利用明細書検索条件DTO.
     * @return ご利用明細書検索結果一覧DTOリスト
     */
    public List<D1501W41SearchResultDto> search(D1501W41SearchConditionDto conditionDto) {
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
        S07T001TD1501W41SearchConditionEntity entity =
            createSearchEntity(conditionDto, kaishuHohoList, acikeiyuflgList);
        // 検索実行
        List<S07T001TD1501W41Entity> searchResultList = dao.selectForD1501W41List(entity);
        // 検索結果が0件の場合エラー
        if (searchResultList.isEmpty()) {
            throw BusinessExceptionFactory.dataNotFound(MessageID.W083);
        }
        // APIコントローラ返却用DTO作成
        List<D1501W41SearchResultDto> resultDto = createSearchResultDto(searchResultList);
        return resultDto;
    }

    /**
     * 検索結果のEntityから検索結果DTOを作成する
     * 
     * @param searchResultList ご利用明細書検索結果Entity
     * @return ご利用明細書検索結果一覧DTOリスト
     */
    private List<D1501W41SearchResultDto> createSearchResultDto(
        List<S07T001TD1501W41Entity> searchResultList) {
        List<D1501W41SearchResultDto> resultDto = new ArrayList<>();

        for (S07T001TD1501W41Entity entity : searchResultList) {
            D1501W41SearchResultDto dto = new D1501W41SearchResultDto();
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
     * ご利用明細書検索用Entityを作成する
     * 
     * @param conditionDto ご利用明細書検索条件DTO.
     * @param kaishuHohoList 請求残高テーブル回収方法検索条件エンティティ
     * @param acikeiyuflgList ACI本部NA区分検索条件Entiryリスト
     * @return ご利用明細書検索用Entity
     */
    private S07T001TD1501W41SearchConditionEntity createSearchEntity(
        D1501W41SearchConditionDto conditionDto,
        List<S07T001TD1501W00KaishuHohoConditionEntity> kaishuHohoList,
        List<S07T001TD1501W00AciKeiyuConditionEntity> acikeiyuflgList) {

        S07T001TD1501W41SearchConditionEntity entity = new S07T001TD1501W41SearchConditionEntity();

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
        // 出力順
        entity.setOtptjun(conditionDto.getOtptjun());
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
     * 相関チェックを実行
     * 
     * @param conditionDto ご利用明細書検索条件DTO.
     */
    public void correlationCheck(D1501W41SearchConditionDto conditionDto) {
        // 締年月が範囲内であることをチェック
        if (!d1501w00service.isWithinDateRange(conditionDto.getShimedd1())) {
            throw BusinessExceptionFactory.validationFailed(
                new FormField("baseBean.shimedd1", "請求書", 0), MessageID.W033, "締年月",
                CommonConstants.YEARMONTH_MIN, CommonConstants.YEARMONTH_MAX);
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
     * ご利用明細書対象明細DTOを生成する
     * 
     * @param skysakicd 請求先コード
     * @return ご利用明細書対象明細DTO.
     */
    public D1501R40TargetMeisaiDto createTargetMeisaiDto(String skysakicd) {
        D1501R40TargetMeisaiDto targetMeisai = new D1501R40TargetMeisaiDto();
        targetMeisai.setSkysakicd(skysakicd);
        return targetMeisai;
    }

    /**
     * 印刷実行時の相関チェックを実行
     * 
     * @param targetMeisaiDtoList ご利用明細書対象明細DTOのリスト
     */
    public void printCorrelationCheck(List<D1501R40TargetMeisaiDto> targetMeisaiDtoList) {
        // 請求先が1件以上選択されていることをチェック
        if (targetMeisaiDtoList.size() == 0) {
            throw BusinessExceptionFactory.validationFailed(MessageID.W091, "請求先");
        }
    }

    /**
     * ご利用明細対象明細DTOの帳票情報存在チェックを実行し対象となるご利用明細書対象明細DTOリストを返却する
     * 
     * @param printDto ご利用明細印刷DTO.
     * @param targetMeisaiDtoList ご利用明細対象明細DTOのリスト
     * @return チェック後のご利用明細書対象明細DTOリスト
     */
    public List<D1501R40TargetMeisaiDto> meisaisyoExistConfirm(D1501R40PrintDto printDto,
        List<D1501R40TargetMeisaiDto> targetMeisaiDtoList) {
        List<D1501R40TargetMeisaiDto> afterCheckTargetMeisaiDtoList = new ArrayList<>();
        for (D1501R40TargetMeisaiDto targetMeisaiDto : targetMeisaiDtoList) {
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
     * ご利用明細書印刷対象件数取得を実行し実行結果を返却
     * 
     * @param printDto ご利用明細書印刷DTO
     * @param targetMeisaiDto ご利用明細書対象明細DTO
     * @return カウント結果
     */
    private long countSkyshoPrintTarget(D1501R40PrintDto printDto,
        D1501R40TargetMeisaiDto targetMeisaiDto) {
        S07T001TD1501R30Entity entity = new S07T001TD1501R30Entity();
        entity = createMeisaiEentity(printDto, targetMeisaiDto);
        return dao.selectForD1501R30Count(entity);
    }

    /**
     * ご利用明細書Entityを生成する
     * 
     * @param printDto ご利用明細書印刷DTO.
     * @param targetMeisaiDto ご利用明細書対象明細DTO.
     * @return 請求書Entity
     */
    private S07T001TD1501R30Entity createMeisaiEentity(D1501R40PrintDto printDto,
        D1501R40TargetMeisaiDto targetMeisaiDto) {
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
        // 印刷種類（控え）
        if (!StringUtils.isEmpty(printDto.getHikaeprntkbn())) {
            entity.setHikaeprntkbn(printDto.getHikaeprntkbn());
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
}
