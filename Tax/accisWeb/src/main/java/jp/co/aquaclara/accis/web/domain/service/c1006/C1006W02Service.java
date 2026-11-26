package jp.co.aquaclara.accis.web.domain.service.c1006;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M017TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TC1006W02ConditionEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TC1006W02ResultEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501W00KaishuHohoConditionEntity;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.web.controller.c1006.form.C1006W02Form;
import jp.co.aquaclara.accis.web.domain.dto.c1006.C1006W01SearchConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.c1006.C1006W02SearchResultDto;
import jp.co.aquaclara.accis.web.domain.dto.c1006.C1006W02SkysakiMstDto;

/**
 * C1006W02 入金消込履歴照会サービスクラス
 * 
 * @author accisdev043
 *
 */

@Service
public class C1006W02Service {

    @Autowired
    S02M017TDao s02M017TDao;

    @Autowired
    S07T001TDao s07T001TDao;

    /**
     * 請求先マスタ情報取得
     * 
     * @param dto 入金管理検索条件Dto
     * @return c1006W02SkysakiMstDto 請求先マスタ取得Dto
     */
    public C1006W02SkysakiMstDto getSkysakimstdata(C1006W01SearchConditionDto dto) {
        C1006W02SkysakiMstDto c1006W02SkysakiMstDto = new C1006W02SkysakiMstDto();
        // 請求先マスタEntityKeyを設定する
        S02M017TEntityKey entitykey = new S02M017TEntityKey();
        entitykey.setApkihoncd(dto.getApkihoncd());
        entitykey.setShopcd(dto.getShopcd());
        entitykey.setSkysakicd(dto.getSkysakicd());
        // S02M017T_請求先マスタDaoを呼び出す
        S02M017TEntity entity = s02M017TDao.selectByPrimaryKey(entitykey);
        // 請求先マスタ取得Dtoを設定する
        if (entity.getKairekin() == null) {
            c1006W02SkysakiMstDto.setKairekin(0);
        } else {
            c1006W02SkysakiMstDto.setKairekin(entity.getKairekin());
        }
        if (entity.getUrichoseikin() == null) {
            c1006W02SkysakiMstDto.setUrichoseikin(0);
        } else {
            c1006W02SkysakiMstDto.setUrichoseikin(entity.getUrichoseikin());
        }
        return c1006W02SkysakiMstDto;
    }

    /**
     * 入金消込履歴一覧取得
     * 
     * @param dto 入金管理検索条件Dto
     * @return resultdaoList 入金消込履歴一覧情報Dtoリスト
     */
    public List<C1006W02SearchResultDto> getNykinKeshikomiRireki(C1006W01SearchConditionDto dto) {
        // 入金消込履歴検索条件Entityの各項目へ値を設定する
        S07T001TC1006W02ConditionEntity entity = setEntity(dto);
        // S07T001T_請求残高テーブルDaoを呼び出す
        List<S07T001TC1006W02ResultEntity> resultList = s07T001TDao.selectForC1006W02List(entity);
        // 上記resultListのデータ全件に対して以下を行う
        // resultListを基にList<入金消込履歴一覧情報Dto>に値を設定する
        List<C1006W02SearchResultDto> resultdaoList = new ArrayList<>();
        for (S07T001TC1006W02ResultEntity result : resultList) {
            C1006W02SearchResultDto resultdto = new C1006W02SearchResultDto();
            resultdto.setShimeymd(result.getShimeymd());
            resultdto.setSkyshono(result.getSkyshono());
            resultdto.setTokuisakicd(result.getTokuisakicd());
            resultdto.setTokuisakinmn1(result.getTokuisakinmn1());
            resultdto.setUriage(result.getUriage());
            resultdto.setKeshikomizd(result.getKeshikomizd());
            resultdto.setNykndd(result.getNykndd());
            resultdto.setKshkmdd(result.getKshkmdd());
            resultdto.setNykinkind(result.getNykinkind());
            resultdto.setKskinkind(result.getKskinkind());
            resultdto.setKeshikomikin(result.getKeshikomikin());
            resultdto.setNykndnpyno(result.getNykndnpyno());
            resultdto.setSkyprcsjissikbn(result.getSkyprcsjissikbn());

            resultdaoList.add(resultdto);
        }
        // List<入金消込履歴一覧情報Dto>を返却する
        return resultdaoList;
    }

    /**
     * 入金消込履歴検索条件Entityの各項目へ値を設定する
     * 
     * @param dto 入金管理検索条件Dto
     * @return entity 入金消込履歴情報検索条件Entity
     */
    private S07T001TC1006W02ConditionEntity setEntity(C1006W01SearchConditionDto dto) {
        // 回収方法選択リスト取得
        List<S07T001TD1501W00KaishuHohoConditionEntity> kaishuHohoList =
            S07T001TD1501W00KaishuHohoConditionEntity.create(dto.getKaishuhohokbn1(),
                dto.getKaishuhohokbn2(), dto.getKaishuhohokbn3(), dto.getKaishuhohokbn4(),
                dto.getKaishuhohokbn5(), dto.getKaishuhohokbn6(), dto.getKaishuhohokbn7(),
                dto.getKaishuhohokbn8(), dto.getKaishuhohokbn9(), dto.getKaishuhohokbn10(),
                dto.getKaishuhohokbn11(), dto.getCrdtsentaku(), dto.getKzfurikaesentaku());

        S07T001TC1006W02ConditionEntity entity = new S07T001TC1006W02ConditionEntity();
        entity.setApkihoncd(dto.getApkihoncd());
        entity.setShopcd(dto.getShopcd());
        entity.setSkysakicd(dto.getSkysakicd());
        entity.setKaishuHohoList(kaishuHohoList);
        entity.setNyknjotai(dto.getNyknjotai());
        entity.setSkydydmddstrt(dto.getSkydydmddstrt());
        entity.setSkydydmddshry(dto.getSkydydmddshry());
        entity.setNyknddstrt(dto.getNyknddstrt());
        entity.setNyknddshry(dto.getNyknddshry());
        entity.setNyknytiddstrt(dto.getNyknytiddstrt());
        entity.setNyknytiddshry(dto.getNyknytiddshry());
        entity.setNyknkindkbnlist(new ArrayList<String>());
        if ("1".equals(dto.getNyknkindkbn1())) {
            entity.getNyknkindkbnlist().add("1");
        }
        if ("1".equals(dto.getNyknkindkbn2())) {
            entity.getNyknkindkbnlist().add("2");
        }
        if ("1".equals(dto.getNyknkindkbn3())) {
            entity.getNyknkindkbnlist().add("3");
        }
        if ("1".equals(dto.getNyknkindkbn4())) {
            entity.getNyknkindkbnlist().add("4");
        }
        if ("1".equals(dto.getNyknkindkbn5())) {
            entity.getNyknkindkbnlist().add("5");
        }
        if ("1".equals(dto.getNyknkindkbn6())) {
            entity.getNyknkindkbnlist().add("6");
        }
        if ("1".equals(dto.getNyknkindkbn7())) {
            entity.getNyknkindkbnlist().add("7");
        }
        if ("1".equals(dto.getNyknkindkbn8())) {
            entity.getNyknkindkbnlist().add("8");
        }
        if ("1".equals(dto.getNyknkindkbn9())) {
            entity.getNyknkindkbnlist().add("9");
        }
        if ("1".equals(dto.getNyknkindkbn10())) {
            entity.getNyknkindkbnlist().add("20");
        }
        if ("1".equals(dto.getNyknkindkbn11())) {
            entity.getNyknkindkbnlist().add("21");
        }
        if ("1".equals(dto.getNyknkindkbn12())) {
            entity.getNyknkindkbnlist().add("30");
        }
        if ("1".equals(dto.getNyknkindkbn13())) {
            entity.getNyknkindkbnlist().add("31");
        }
        if ("1".equals(dto.getNyknkindkbn14())) {
            entity.getNyknkindkbnlist().add("10");
        }

        return entity;
    }

    /**
     * 入金管理検索条件Dtoを設定する
     * 
     * @param form 画面表示情報
     * @return dto 入金管理検索条件Dto
     */
    public C1006W01SearchConditionDto setSearchConditionDto(C1006W02Form form) {
        C1006W01SearchConditionDto dto = new C1006W01SearchConditionDto();
        // ＡＰ基本コード
        dto.setApkihoncd(form.getBaseBean().getApkihoncd());
        // ショップコード
        dto.setShopcd(form.getBaseBean().getShopcd());
        // 請求先コード
        dto.setSkysakicd(form.getBaseBean().getKsscd());
        // 回収方法（現金一括）
        dto.setKaishuhohokbn1(form.getBaseBean().getKaishuhohokbn1());
        // 回収方法（クレジット）
        dto.setKaishuhohokbn2(form.getBaseBean().getKaishuhohokbn2());
        // クレジット決済機関
        dto.setCrdtsentaku(form.getBaseBean().getCrdtsentaku());
        // 回収方法（銀行振込）
        dto.setKaishuhohokbn3(form.getBaseBean().getKaishuhohokbn3());
        // 回収方法（郵便振込）
        dto.setKaishuhohokbn4(form.getBaseBean().getKaishuhohokbn4());
        // 回収方法（口座振替）
        dto.setKaishuhohokbn5(form.getBaseBean().getKaishuhohokbn5());
        // 口座振替決済機関
        dto.setKzfurikaesentaku(form.getBaseBean().getKzfurikaesentaku());
        // 回収方法（コンビニ）
        dto.setKaishuhohokbn6(form.getBaseBean().getKaishuhohokbn6());
        // 回収方法（代引き）
        dto.setKaishuhohokbn7(form.getBaseBean().getKaishuhohokbn7());
        // 回収方法（その他）
        dto.setKaishuhohokbn8(form.getBaseBean().getKaishuhohokbn8());
        // 回収方法（請求無し）
        dto.setKaishuhohokbn9(form.getBaseBean().getKaishuhohokbn9());
        // 回収方法（その他２）
        dto.setKaishuhohokbn10(form.getBaseBean().getKaishuhohokbn10());
        // 回収方法（その他３）
        dto.setKaishuhohokbn11(form.getBaseBean().getKaishuhohokbn11());
        // 入金状態
        dto.setNyknjotai(form.getBaseBean().getNyknjotai());
        // 請求年月日開始
        dto.setSkydydmddstrt(DateTimeUtil.toNoSlashedDate(form.getBaseBean().getSkydydmddstrt()));
        // 請求年月日終了
        dto.setSkydydmddshry(DateTimeUtil.toNoSlashedDate(form.getBaseBean().getSkydydmddshry()));
        // 入金日開始
        dto.setNyknddstrt(DateTimeUtil.toNoSlashedDate(form.getBaseBean().getNyknddstrt()));
        // 入金日終了
        dto.setNyknddshry(DateTimeUtil.toNoSlashedDate(form.getBaseBean().getNyknddshry()));
        // 入金予定日開始
        dto.setNyknytiddstrt(DateTimeUtil.toNoSlashedDate(form.getBaseBean().getNyknytiddstrt()));
        // 入金予定日終了
        dto.setNyknytiddshry(DateTimeUtil.toNoSlashedDate(form.getBaseBean().getNyknytiddshry()));
        // 入金種別（現金）
        dto.setNyknkindkbn1(form.getBaseBean().getNyknkindkbn1());
        // 入金種別（カード入金）
        dto.setNyknkindkbn2(form.getBaseBean().getNyknkindkbn2());
        // 入金種別（銀行振込）
        dto.setNyknkindkbn3(form.getBaseBean().getNyknkindkbn3());
        // 入金種別（郵便振込）
        dto.setNyknkindkbn4(form.getBaseBean().getNyknkindkbn4());
        // 入金種別（口座振替）
        dto.setNyknkindkbn5(form.getBaseBean().getNyknkindkbn5());
        // 入金種別（コンビニ）
        dto.setNyknkindkbn6(form.getBaseBean().getNyknkindkbn6());
        // 入金種別（手形）
        dto.setNyknkindkbn7(form.getBaseBean().getNyknkindkbn7());
        // 入金種別（チケット）
        dto.setNyknkindkbn8(form.getBaseBean().getNyknkindkbn8());
        // 入金種別（相殺）
        dto.setNyknkindkbn9(form.getBaseBean().getNyknkindkbn9());
        // 入金種別（手数料）
        dto.setNyknkindkbn10(form.getBaseBean().getNyknkindkbn10());
        // 入金種別（値引）
        dto.setNyknkindkbn11(form.getBaseBean().getNyknkindkbn11());
        // 入金種別（過入金）
        dto.setNyknkindkbn12(form.getBaseBean().getNyknkindkbn12());
        // 入金種別（売上調整額）
        dto.setNyknkindkbn13(form.getBaseBean().getNyknkindkbn13());
        // 入金種別（その他）
        dto.setNyknkindkbn14(form.getBaseBean().getNyknkindkbn14());

        return dto;
    }
}
