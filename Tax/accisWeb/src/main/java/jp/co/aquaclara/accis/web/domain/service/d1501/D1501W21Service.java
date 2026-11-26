package jp.co.aquaclara.accis.web.domain.service.d1501;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.SystemConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R21Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501W00AciKeiyuConditionEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501W00KaishuHohoConditionEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501W21Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501W21SearchConditionEntity;
import jp.co.aquaclara.accis.web.domain.dto.d1501.D1501R21PrintDto;
import jp.co.aquaclara.accis.web.domain.dto.d1501.D1501R21TargetMeisaiDto;
import jp.co.aquaclara.accis.web.domain.dto.d1501.D1501W21SearchConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.d1501.D1501W21SearchResultDto;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;

/**
 * 請求関連処理（請求一覧表）-サービスクラス
 * 
 * @author accisdev044
 *
 */
@Service
public class D1501W21Service {

    /**
     * 請求関連処理ユーティリティサービス
     */
    @Autowired
    private D1501W00Service d1501w00Service;

    /**
     * 請求残高テーブルDAO
     */
    @Autowired
    private S07T001TDao s07T001TDao;

    /**
     * 請求一覧表対象結果取得
     *
     * @param conditionDto 請求一覧表検索条件DTO
     * @return 請求一覧表検索結果一覧DTOリスト
     */
    public List<D1501W21SearchResultDto> search(D1501W21SearchConditionDto conditionDto) {
        // 回収方法選択リスト取得
        List<S07T001TD1501W00KaishuHohoConditionEntity> kaishuhohoList =
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

        // 請求一覧表検索条件Entity生成
        S07T001TD1501W21SearchConditionEntity entity =
            createSearchEntity(conditionDto, kaishuhohoList, acikeiyuflgList);

        // 請求一覧表検索データ取得
        List<S07T001TD1501W21Entity> searchResultList = s07T001TDao.selectForD1501W21List(entity);
        // 検索データ存在なしの場合エラー
        if (searchResultList.isEmpty()) {
            throw BusinessExceptionFactory.dataNotFound(MessageID.W083);
        }

        // 請求一覧表検索結果一覧DTO生成
        List<D1501W21SearchResultDto> resultDto =
            createSearchResultDto(searchResultList, conditionDto.getSkyshimedate());
        return resultDto;
    }

    /**
     * 請求一覧表検索結果一覧DTO生成
     *
     * @param searchResultList 請求一覧表検索結果Entity
     * @param shimedd 締日
     * @return 請求一覧表検索結果一覧DTO
     */
    private List<D1501W21SearchResultDto> createSearchResultDto(
        List<S07T001TD1501W21Entity> searchResultList, String shimedd) {
        List<D1501W21SearchResultDto> result = new ArrayList<>();
        for (S07T001TD1501W21Entity entity : searchResultList) {
            D1501W21SearchResultDto dto = new D1501W21SearchResultDto();
            dto.setApkihoncd(entity.getApkihoncd());
            dto.setShopcd(entity.getShopcd());
            dto.setSkysakicd(entity.getSkysakicd());
            dto.setSkysakinmn1(entity.getSkysakinmn1());
            dto.setJigyoshocd(entity.getJigyoshocd());
            dto.setKonkaishimedd(entity.getKonkaishimedd());
            dto.setKaishuhohokbnnm(entity.getKaishuhohokbnnm());
            dto.setTairyukbnnm(entity.getTairyukbnnm());
            dto.setStatus(entity.getStatus());
            dto.setPrcsjotai(d1501w00Service.getPrcsjotaime(entity.getStatus()));
            result.add(dto);
        }
        return result;
    }

    /**
     * 請求一覧表検索条件Entity生成
     *
     * @param dto 請求一覧表検索条件DTO
     * @param kaishuhohoList 請求残高テーブル回収方法検索条件エンティティのリスト
     * @param acikeiyuflgList ACI本部NA区分検索条件Entiryリスト
     * @return 請求一覧表検索条件Entity
     */
    private S07T001TD1501W21SearchConditionEntity createSearchEntity(D1501W21SearchConditionDto dto,
        List<S07T001TD1501W00KaishuHohoConditionEntity> kaishuhohoList,
        List<S07T001TD1501W00AciKeiyuConditionEntity> acikeiyuflgList) {
        S07T001TD1501W21SearchConditionEntity entity = new S07T001TD1501W21SearchConditionEntity();
        // AP基本コード
        entity.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        entity.setShopcd(dto.getShopcd());
        // 事業所コード
        entity.setJigyoshocd(dto.getJigyoshocd());
        // 締年月
        entity.setShimedydm(dto.getShimedd1().replace("/", SystemConstants.WORD_BLANK));
        // 締日
        entity.setShimedd(dto.getShimedd3());
        // 抽出締日
        entity.setExtractshimedd(dto.getSkyshimeday());
        // 請求先コード
        if (!StringUtils.isEmpty(dto.getSkysakicd())) {
            entity.setSkysakicd(dto.getSkysakicd());
        }
        // 営業担当者コード
        if (!StringUtils.isEmpty(dto.getEigyotnshacd())) {
            entity.setEigyotnshacd(dto.getEigyotnshacd());
        }
        // 回収担当者コード
        if (!StringUtils.isEmpty(dto.getKaishutnshacd())) {
            entity.setKaishutnshacd(dto.getKaishutnshacd());
        }
        // カナ名称
        if (!StringUtils.isEmpty(dto.getSkysakinmkana())) {
            entity.setSkysakinmkana(dto.getSkysakinmkana());
        }
        // 得意先有無（有）
        if (!StringUtils.isEmpty(dto.getTtumu1())) {
            entity.setTtumu1(dto.getTtumu1());
        }
        // 得意先有無（無）
        if (!StringUtils.isEmpty(dto.getTtumu2())) {
            entity.setTtumu2(dto.getTtumu2());
        }
        // 請求先管理区分
        if (!StringUtils.isEmpty(dto.getSkysakikanrikbn())) {
            entity.setSkysakikanrikbn(dto.getSkysakikanrikbn());
        }
        // 請求区分（親子）
        if (!StringUtils.isEmpty(dto.getSkykbn1())) {
            entity.setSkykbn1(dto.getSkykbn1());
        }
        // 請求区分（一般）
        if (!StringUtils.isEmpty(dto.getSkykbn2())) {
            entity.setSkykbn2(dto.getSkykbn2());
        }
        // 早出し
        if (!StringUtils.isEmpty(dto.getHayadasiflg())) {
            entity.setHayadasiflg(dto.getHayadasiflg());
        }
        // 請求明細送付区分１
        if (!StringUtils.isEmpty(dto.getSkymeisaisofukbn1())) {
            entity.setSkymeisaisofukbn1(dto.getSkymeisaisofukbn1());
        }
        // 請求明細送付区分2
        if (!StringUtils.isEmpty(dto.getSkymeisaisofukbn2())) {
            entity.setSkymeisaisofukbn2(dto.getSkymeisaisofukbn2());
        }
        // 明細最大件数
        entity.setMeisaimaxline(dto.getMeisaimaxline());
        // 回収方法選択リスト
        entity.setKaishuHohoList(kaishuhohoList);

        // ACI本部NA区分リスト
        entity.setAcikeiyuflgList(acikeiyuflgList);

        return entity;
    }

    /**
     * 検索実行時の相関チェック
     * 
     * @param conditionDto 請求一覧表検索条件DTO
     */
    public void correlationCheck(D1501W21SearchConditionDto conditionDto) {
        // 得意先有無選択チェック
        if (d1501w00Service.isTtumuEmpty(conditionDto.getTtumu1(), conditionDto.getTtumu2())) {
            throw BusinessExceptionFactory.validationFailed(d1501w00Service.getTtumuFormFieldList(),
                MessageID.W091, "得意先有無");
        }

        // 請求区分選択チェック
        if (d1501w00Service.isSkykbnEmpty(conditionDto.getSkykbn1(), conditionDto.getSkykbn2())) {
            throw BusinessExceptionFactory
                .validationFailed(d1501w00Service.getSkykbnFormFieldList(), MessageID.W091, "請求区分");
        }

        // 回収方法選択チェック
        if (d1501w00Service.isKaishuhohokbnEmpty(conditionDto.getKaishuhohokbn1(),
            conditionDto.getKaishuhohokbn2(), conditionDto.getKaishuhohokbn3(),
            conditionDto.getKaishuhohokbn4(), conditionDto.getKaishuhohokbn5(),
            conditionDto.getKaishuhohokbn6(), conditionDto.getKaishuhohokbn7(),
            conditionDto.getKaishuhohokbn8(), conditionDto.getKaishuhohokbn9(),
            conditionDto.getKaishuhohokbn10(), conditionDto.getKaishuhohokbn11())) {
            throw BusinessExceptionFactory.validationFailed(
                d1501w00Service.getKaishuhohokbnFormFieldList(), MessageID.W091, "回収方法");
        }
    }

    /**
     * 印刷実行時の相関チェックを実行
     * 
     * @param targetMeisaiDtoList 請求一覧表対象明細DTOのリスト
     */
    public void printCorrelationCheck(List<D1501R21TargetMeisaiDto> targetMeisaiDtoList) {
        // 請求先が1件以上選択されていることをチェック
        if (targetMeisaiDtoList.size() == 0) {
            throw BusinessExceptionFactory.validationFailed(MessageID.W091, "請求先");
        }
    }

    /**
     * 請求一覧表対象明細DTOを生成する
     * 
     * @param skysakicd 請求先コード
     * @return 請求一覧表対象明細DTO
     */
    public D1501R21TargetMeisaiDto createTargetMeisaiDto(String skysakicd) {
        D1501R21TargetMeisaiDto targetMeisai = new D1501R21TargetMeisaiDto();
        targetMeisai.setSkysakicd(skysakicd);
        return targetMeisai;
    }

    /**
     * 帳票情報存在チェック(取得件数0件の場合はエラーメッセージ出力)
     * 
     * @param printDto 請求一覧表印刷DTO
     * @param targetMeisaiDtoList 請求一覧表対象明細DTOのリスト
     */
    public void skyshoExistConfirm(D1501R21PrintDto printDto,
        List<D1501R21TargetMeisaiDto> targetMeisaiDtoList) {

        Boolean checkFlg = true;
        for (D1501R21TargetMeisaiDto targetMeisaiDto : targetMeisaiDtoList) {
            List<S07T001TD1501R21Entity> countResult =
                countSkyshoPrintTarget(printDto, targetMeisaiDto);
            if (countResult.get(0).getCnt() > 0) {
                continue;
            }
            checkFlg = false;

        }
        if (!checkFlg) {
            throw BusinessExceptionFactory.dataNotFound(MessageID.W083);
        }

    }

    /**
     * 請求一覧表印刷対象件数取得を実行し実行結果を返却
     * 
     * @param printDto 請求一覧表印刷DTO
     * @param targetMeisaiDto 請求一覧表対象明細DTO
     * @return 請求一覧表印刷対象件数
     */
    private List<S07T001TD1501R21Entity> countSkyshoPrintTarget(D1501R21PrintDto printDto,
        D1501R21TargetMeisaiDto targetMeisaiDto) {
        S07T001TD1501R21Entity entity = new S07T001TD1501R21Entity();
        entity = createSkyshoEntity(printDto, targetMeisaiDto);
        return s07T001TDao.selectForD1501R21List(entity, true);
    }

    /**
     * 請求一覧表Entityを生成
     * 
     * @param printDto 請求一覧表印刷DTO
     * @param targetMeisaiDto 請求一覧表対象明細DTO
     * @return 請求一覧表Entity
     */
    private S07T001TD1501R21Entity createSkyshoEntity(D1501R21PrintDto printDto,
        D1501R21TargetMeisaiDto targetMeisaiDto) {

        S07T001TD1501R21Entity entity = new S07T001TD1501R21Entity();
        // AP基本コード
        entity.setApkihoncd(printDto.getApkihoncd());
        // ショップコード
        entity.setShopcd(printDto.getShopcd());
        // 請求先コード（targetDtoから取得）
        entity.setSkysakicd(targetMeisaiDto.getSkysakicd());
        // 締年月
        entity.setShimedydm(printDto.getShimedydm());
        // 請求締日
        entity.setShimedd(printDto.getShimedd());
        // 請求締年月日
        entity.setSkydydmdd(printDto.getShimedydmdd());
        // ログイン担当者AP基本コード
        entity.setLoginUserapkihoncd(printDto.getLoginUserapkihoncd());
        // ログイン担当者ショップコード
        entity.setLoginUsershopcd(printDto.getLoginUsershopcd());
        // ログイン担当者ショップ名称
        entity.setLoginUsershopnm(printDto.getLoginUsershopnm());
        // ログイン担当者事業所コード
        entity.setLoginUserjigyoshocd(printDto.getLoginUserjigyoshocd());
        // ログイン担当者事業所名称
        entity.setLoginUserjigyoshonm(printDto.getLoginUserjigyoshonm());
        // ログイン担当者コード
        entity.setLoginUsertnshacd(printDto.getLoginUsertnshacd());
        // ログイン担当者名称
        entity.setLoginUsertnshamei(printDto.getLoginUsertnshamei());
        // 運用日付
        entity.setUnyoymd(printDto.getUnyoymd());

        return entity;
    }
}

