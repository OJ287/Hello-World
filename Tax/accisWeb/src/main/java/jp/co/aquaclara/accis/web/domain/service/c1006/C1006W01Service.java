package jp.co.aquaclara.accis.web.domain.service.c1006;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M003TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M017TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M003TKinoIdKengenEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M017TC1006W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TC1006W01ConditionEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501W00KaishuHohoConditionEntity;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.web.controller.commons.UserSessionForm;
import jp.co.aquaclara.accis.web.domain.dto.c1006.C1006W01SearchConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.c1006.C1006W01SearchResultDto;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import jp.co.aquaclara.accis.web.exception.dto.FormField;

/**
 * C1006W01 入金管理サービスクラス.
 * 
 * @author accisdev043
 *
 */
@Service
public class C1006W01Service {

    @Autowired
    S02M017TDao s02M017TDao;

    @Autowired
    S02M003TDao s02M003TDao;

    @Autowired
    S07T001TDao s07T001TDao;

    /**
     * 入金管理一覧情報取得
     * 
     * @param dto 入金管理検索条件dto
     * @return resultList 入金管理一覧情報dtoリスト
     * @throws SQLException 業務例外
     */
    public List<C1006W01SearchResultDto> getNykinData(C1006W01SearchConditionDto dto)
        throws SQLException {

        // 回収方法選択リスト取得
        List<S07T001TD1501W00KaishuHohoConditionEntity> kaishuHohoList =
            S07T001TD1501W00KaishuHohoConditionEntity.create(dto.getKaishuhohokbn1(),
                dto.getKaishuhohokbn2(), dto.getKaishuhohokbn3(), dto.getKaishuhohokbn4(),
                dto.getKaishuhohokbn5(), dto.getKaishuhohokbn6(), dto.getKaishuhohokbn7(),
                dto.getKaishuhohokbn8(), dto.getKaishuhohokbn9(), dto.getKaishuhohokbn10(),
                dto.getKaishuhohokbn11(), dto.getCrdtsentaku(), dto.getKzfurikaesentaku());

        // 入金管理一覧検索条件Entityの各項目へ値を設定する
        S07T001TC1006W01ConditionEntity entity = setEntity(dto, kaishuHohoList);
        // S02M017T_請求先マスタDaoを呼び出す
        List<S02M017TC1006W01Entity> s02M017TC1006W01EntityList =
            s02M017TDao.selectForC1006W01List(entity);
        // listが0件の場合、業務例外をthrowする
        if (s02M017TC1006W01EntityList.size() == 0) {
            throw BusinessExceptionFactory.dataNotFound(MessageID.W083);
        }
        // 上記のデータ全件に対して以下を行う
        List<C1006W01SearchResultDto> resultList = new ArrayList<>();
        for (S02M017TC1006W01Entity s02M017TC1006W01Entity : s02M017TC1006W01EntityList) {
            // 取得した値を基にList＜入金管理一覧情報Dto＞に値を設定する
            C1006W01SearchResultDto result = new C1006W01SearchResultDto();
            result.setSkysakicd(s02M017TC1006W01Entity.getSkysakicd());
            result.setJgycd(s02M017TC1006W01Entity.getJgycd());
            result.setSkysakinmn1(s02M017TC1006W01Entity.getSkysakinmn1());
            result.setUriage(s02M017TC1006W01Entity.getUriage());
            result.setKesikomi(s02M017TC1006W01Entity.getKesikomi());
            result.setMinyu(s02M017TC1006W01Entity.getMinyu());
            result.setTairyukin(s02M017TC1006W01Entity.getTairyukin());
            result.setTairyukbn(s02M017TC1006W01Entity.getTairyukbnnm());

            resultList.add(result);
        }

        return resultList;
    }

    /**
     * 入金入力画面使用権限取得
     * 
     * @param userSessionForm ログインユーザー情報
     * @return true/false 入金入力画面使用可能フラグ
     * @throws SQLException 業務例外
     */
    public boolean getNykinAuthority(UserSessionForm userSessionForm) throws SQLException {
        // 担当者機能ID使用権限取得Entityの各項目へ値を設定する
        S02M003TKinoIdKengenEntity entity = new S02M003TKinoIdKengenEntity();
        entity.setApkihoncd(userSessionForm.getApkihoncd());
        entity.setShopcd(userSessionForm.getShopcd());
        entity.setTnshacd(userSessionForm.getTnshacd());
        entity.setKinoid("C1005W00");

        // S02M003T_担当者マスタDaoを呼び出し、機能IDを取得する
        String kinoid = s02M003TDao.selectForKinoIdKengen2(entity);
        // 機能IDの値をもとに戻り値を設定する
        if (kinoid == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 入金管理CSV出力件数取得
     * 
     * @param dto 入金管理検索条件Dto
     * @return csvcnt 入金管理CSV出力件数
     * @throws SQLException 業務例外
     */
    public int getnykinkanricsvcnt(C1006W01SearchConditionDto dto) throws SQLException {

        // 回収方法選択リスト取得
        List<S07T001TD1501W00KaishuHohoConditionEntity> kaishuHohoList =
            S07T001TD1501W00KaishuHohoConditionEntity.create(dto.getKaishuhohokbn1(),
                dto.getKaishuhohokbn2(), dto.getKaishuhohokbn3(), dto.getKaishuhohokbn4(),
                dto.getKaishuhohokbn5(), dto.getKaishuhohokbn6(), dto.getKaishuhohokbn7(),
                dto.getKaishuhohokbn8(), dto.getKaishuhohokbn9(), dto.getKaishuhohokbn10(),
                dto.getKaishuhohokbn11(), dto.getCrdtsentaku(), dto.getKzfurikaesentaku());

        // 入金管理一覧検索条件Entityの各項目へ値を設定する
        S07T001TC1006W01ConditionEntity entity = setEntity(dto, kaishuHohoList);
        // S07T001T_請求残高テーブルDaoを呼び出す
        int csvcnt = s07T001TDao.selectForC1006R01Count(entity);

        return csvcnt;
    }



    /**
     * 入金管理一覧検索条件Entityの各項目へ値を設定する
     * 
     * @param dto 入金管理検索条件Dto
     * @param kaishuHohoList 請求残高テーブル回収方法検索条件エンティティ
     * @return entity 入金管理一覧検索条件Entity
     */
    public S07T001TC1006W01ConditionEntity setEntity(C1006W01SearchConditionDto dto,
        List<S07T001TD1501W00KaishuHohoConditionEntity> kaishuHohoList) {
        S07T001TC1006W01ConditionEntity entity = new S07T001TC1006W01ConditionEntity();
        entity.setApkihoncd(dto.getApkihoncd());
        entity.setShopcd(dto.getShopcd());
        entity.setJigyoshosentaku(dto.getJigyoshosentaku());
        entity.setSkysakicd(dto.getSkysakicd());
        entity.setGaiburenkeicd(dto.getGaiburenkeicd());
        entity.setSkysakinmn(dto.getSkysakinmn());
        entity.setSkysakinmkana(dto.getSkysakinmkana());
        // 回収方法リスト
        entity.setKaishuHohoList(kaishuHohoList);
        entity.setKzmeigikana(dto.getKzmeigikana());
        entity.setKzfurikaerenkeicd(dto.getKzfurikaerenkeicd());
        entity.setNyknjotai(dto.getNyknjotai());
        entity.setTairyukbnlist(new ArrayList<String>());
        if ("1".equals(dto.getTairyukbn1())) {
            entity.getTairyukbnlist().add("1");
        }
        if ("1".equals(dto.getTairyukbn2())) {
            entity.getTairyukbnlist().add("2");
        }
        if ("1".equals(dto.getTairyukbn3())) {
            entity.getTairyukbnlist().add("3");
        }
        if ("1".equals(dto.getTairyukbn4())) {
            entity.getTairyukbnlist().add("0");
        }
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
        entity.setMeisaisaidaicnt(dto.getMeisaisaidaicnt());
        // 請求残高が無い入金を含めるフラグ
        entity.setIncludenobalanceflg(dto.getIncludenobalanceflg());
        return entity;
    }

    /**
     * 相関チェック
     * 
     * @param dto 入金管理検索条件Dto
     */
    public void check(C1006W01SearchConditionDto dto) {

        // 回収方法(請求時)を1つも選択していない場合はエラーとする
        if (StringUtils.isEmpty(dto.getKaishuhohokbn1())
            && StringUtils.isEmpty(dto.getKaishuhohokbn2())
            && StringUtils.isEmpty(dto.getCrdtsentaku())
            && StringUtils.isEmpty(dto.getKaishuhohokbn3())
            && StringUtils.isEmpty(dto.getKaishuhohokbn4())
            && StringUtils.isEmpty(dto.getKaishuhohokbn5())
            && StringUtils.isEmpty(dto.getKzfurikaesentaku())
            && StringUtils.isEmpty(dto.getKaishuhohokbn6())
            && StringUtils.isEmpty(dto.getKaishuhohokbn7())
            && StringUtils.isEmpty(dto.getKaishuhohokbn8())
            && StringUtils.isEmpty(dto.getKaishuhohokbn9())
            && StringUtils.isEmpty(dto.getKaishuhohokbn10())
            && StringUtils.isEmpty(dto.getKaishuhohokbn11())) {
            throw BusinessExceptionFactory.validationFailed(getKaishuhohoFieldList(),
                MessageID.W091, "回収方法(請求時)");
        }

        // 滞留区分を1つも選択していない場合はエラーとする
        if (StringUtils.isEmpty(dto.getTairyukbn1()) && StringUtils.isEmpty(dto.getTairyukbn2())
            && StringUtils.isEmpty(dto.getTairyukbn3())
            && StringUtils.isEmpty(dto.getTairyukbn4())) {
            throw BusinessExceptionFactory.validationFailed(getTairyukbnFieldList(), MessageID.W091,
                "滞留区分");
        }

        if ("1".equals(dto.getNyknjotai())) {
            // 入金種別(消込)を1つも選択していない場合はエラーとする
            if (StringUtils.isEmpty(dto.getNyknkindkbn1())
                && StringUtils.isEmpty(dto.getNyknkindkbn2())
                && StringUtils.isEmpty(dto.getNyknkindkbn3())
                && StringUtils.isEmpty(dto.getNyknkindkbn4())
                && StringUtils.isEmpty(dto.getNyknkindkbn5())
                && StringUtils.isEmpty(dto.getNyknkindkbn6())
                && StringUtils.isEmpty(dto.getNyknkindkbn7())
                && StringUtils.isEmpty(dto.getNyknkindkbn8())
                && StringUtils.isEmpty(dto.getNyknkindkbn9())
                && StringUtils.isEmpty(dto.getNyknkindkbn10())
                && StringUtils.isEmpty(dto.getNyknkindkbn11())
                && StringUtils.isEmpty(dto.getNyknkindkbn12())
                && StringUtils.isEmpty(dto.getNyknkindkbn13())
                && StringUtils.isEmpty(dto.getNyknkindkbn14())) {
                throw BusinessExceptionFactory.validationFailed(getNyukintypeFieldList(),
                    MessageID.W091, "入金種別(消込)");
            }

            // 「入金日開始」が1900/01/01～9999/12/31の範囲内チェック
            if (!StringUtils.isEmpty(dto.getNyknddstrt())) {
                if (!DateTimeUtil.isDateBetween(dto.getNyknddstrt(), CommonConstants.DATE_MIN,
                    CommonConstants.DATE_MAX)) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("baseBean.nyknddstrt", "入金日開始", 0), MessageID.W033, "入金日開始",
                        CommonConstants.DATE_MIN, CommonConstants.DATE_MAX);
                }
            }
            // 「入金日終了」が1900/01/01～9999/12/31の範囲内チェック
            if (!StringUtils.isEmpty(dto.getNyknddshry())) {
                if (!DateTimeUtil.isDateBetween(dto.getNyknddshry(), CommonConstants.DATE_MIN,
                    CommonConstants.DATE_MAX)) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField("baseBean.nyknddshry", "入金日終了", 0), MessageID.W033, "入金日終了",
                        CommonConstants.DATE_MIN, CommonConstants.DATE_MAX);
                }
            }
            // 入金日開始＞入金日終了の場合はエラーとする
            if (!StringUtils.isEmpty(dto.getNyknddstrt())
                && !StringUtils.isEmpty(dto.getNyknddshry())) {
                // 入金日フォームフィールドリスト取得
                List<FormField> nyukinddFieldList = new ArrayList<FormField>();
                nyukinddFieldList.add(new FormField("baseBean.nyknddstrt", "入金日開始", 0));
                nyukinddFieldList.add(new FormField("baseBean.nyknddshry", "入金日終了", 0));
                if (!DateTimeUtil.isLater(dto.getNyknddstrt(), dto.getNyknddshry())) {
                    throw BusinessExceptionFactory.validationFailed(nyukinddFieldList,
                        MessageID.W093, "入金日開始", "入金日終了");
                }
            }
        }
        // 「請求年月日開始」が1900/01/01～9999/12/31の範囲内チェック
        if (!StringUtils.isEmpty(dto.getSkydydmddstrt())) {
            if (!DateTimeUtil.isDateBetween(dto.getSkydydmddstrt(), CommonConstants.DATE_MIN,
                CommonConstants.DATE_MAX)) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.skydydmddstrt", "請求年月日開始", 0), MessageID.W033,
                    "請求年月日開始", CommonConstants.DATE_MIN, CommonConstants.DATE_MAX);
            }
        }
        // 「請求年月日終了」が1900/01/01～9999/12/31の範囲内チェック
        if (!StringUtils.isEmpty(dto.getSkydydmddshry())) {
            if (!DateTimeUtil.isDateBetween(dto.getSkydydmddshry(), CommonConstants.DATE_MIN,
                CommonConstants.DATE_MAX)) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.skydydmddshry", "請求年月日終了", 0), MessageID.W033,
                    "請求年月日終了", CommonConstants.DATE_MIN, CommonConstants.DATE_MAX);
            }
        }
        // 請求年月日開始＞請求年月日終了の場合はエラーとする
        if (!StringUtils.isEmpty(dto.getSkydydmddstrt())
            && !StringUtils.isEmpty(dto.getSkydydmddshry())) {
            // 請求年月日フォームフィールドリスト取得
            List<FormField> skydydmddFieldList = new ArrayList<FormField>();
            skydydmddFieldList.add(new FormField("baseBean.skydydmddstrt", "請求年月日開始", 0));
            skydydmddFieldList.add(new FormField("baseBean.skydydmddshry", "請求年月日終了", 0));
            if (!DateTimeUtil.isLater(dto.getSkydydmddstrt(), dto.getSkydydmddshry())) {
                throw BusinessExceptionFactory.validationFailed(skydydmddFieldList, MessageID.W093,
                    "請求年月日開始", "請求年月日終了");
            }
        }
        // 「入金予定日開始」が1900/01/01～9999/12/31の範囲内チェック
        if (!StringUtils.isEmpty(dto.getNyknytiddstrt())) {
            if (!DateTimeUtil.isDateBetween(dto.getNyknytiddstrt(), CommonConstants.DATE_MIN,
                CommonConstants.DATE_MAX)) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.nyknytiddstrt", "入金予定日開始", 0), MessageID.W033,
                    "入金予定日開始", CommonConstants.DATE_MIN, CommonConstants.DATE_MAX);
            }
        }
        // 「入金予定日終了」が1900/01/01～9999/12/31の範囲内チェック
        if (!StringUtils.isEmpty(dto.getNyknytiddshry())) {
            if (!DateTimeUtil.isDateBetween(dto.getNyknytiddshry(), CommonConstants.DATE_MIN,
                CommonConstants.DATE_MAX)) {
                throw BusinessExceptionFactory.validationFailed(
                    new FormField("baseBean.nyknytiddshry", "入金予定日終了", 0), MessageID.W033,
                    "入金予定日終了", CommonConstants.DATE_MIN, CommonConstants.DATE_MAX);
            }
        }
        // 入金予定日開始＞入金予定日終了の場合はエラーとする
        if (!StringUtils.isEmpty(dto.getNyknytiddstrt())
            && !StringUtils.isEmpty(dto.getNyknytiddshry())) {
            // 入金予定日フォームフィールドリスト取得
            List<FormField> nyukinnyoteiFieldList = new ArrayList<FormField>();
            nyukinnyoteiFieldList.add(new FormField("baseBean.nyknytiddstrt", "入金予定日開始", 0));
            nyukinnyoteiFieldList.add(new FormField("baseBean.nyknytiddshry", "入金予定日終了", 0));
            if (!DateTimeUtil.isLater(dto.getNyknytiddstrt(), dto.getNyknytiddshry())) {
                throw BusinessExceptionFactory.validationFailed(nyukinnyoteiFieldList,
                    MessageID.W093, "入金予定日開始", "入金予定日終了");
            }
        }
    }

    /**
     * 回収方法（請求時）フォームフィールドリスト取得
     * 
     * @return 回収方法（請求時）フォームフィールドリスト
     */
    private List<FormField> getKaishuhohoFieldList() {
        List<FormField> kaishuhohoFieldList = new ArrayList<FormField>();
        kaishuhohoFieldList.add(new FormField("baseBean.kaishuhohokbn1", "現金一括", 0));
        kaishuhohoFieldList.add(new FormField("baseBean.kaishuhohokbn2", "クレジット", 0));
        kaishuhohoFieldList.add(new FormField("baseBean.crdtsentaku", "クレジット選択", 0));
        kaishuhohoFieldList.add(new FormField("baseBean.kaishuhohokbn3", "銀行振込", 0));
        kaishuhohoFieldList.add(new FormField("baseBean.kaishuhohokbn4", "郵便振込", 0));
        kaishuhohoFieldList.add(new FormField("baseBean.kaishuhohokbn5", "口座振替", 0));
        kaishuhohoFieldList.add(new FormField("baseBean.kzfurikaesentaku", "口座振替選択", 0));
        kaishuhohoFieldList.add(new FormField("baseBean.kaishuhohokbn6", "コンビニ", 0));
        kaishuhohoFieldList.add(new FormField("baseBean.kaishuhohokbn7", "代引き", 0));
        kaishuhohoFieldList.add(new FormField("baseBean.kaishuhohokbn8", "その他", 0));
        kaishuhohoFieldList.add(new FormField("baseBean.kaishuhohokbn10", "その他２", 0));
        kaishuhohoFieldList.add(new FormField("baseBean.kaishuhohokbn11", "その他３", 0));
        kaishuhohoFieldList.add(new FormField("baseBean.kaishuhohokbn9", "請求無し", 0));

        return kaishuhohoFieldList;
    }

    /**
     * 滞留区分フォームフィールドリスト取得
     * 
     * @return 滞留区分フォームフィールドリスト
     */
    private List<FormField> getTairyukbnFieldList() {
        List<FormField> tairyukbnFieldList = new ArrayList<FormField>();
        tairyukbnFieldList.add(new FormField("baseBean.tairyukbn1", "1ケ月", 0));
        tairyukbnFieldList.add(new FormField("baseBean.tairyukbn2", "2ケ月", 0));
        tairyukbnFieldList.add(new FormField("baseBean.tairyukbn3", "長期滞留", 0));
        tairyukbnFieldList.add(new FormField("baseBean.tairyukbn4", "滞留無し", 0));

        return tairyukbnFieldList;
    }

    /**
     * 入金種別(消込)フォームフィールドリスト取得
     * 
     * @return 入金種別(消込)フォームフィールドリスト
     */
    private List<FormField> getNyukintypeFieldList() {
        List<FormField> nyukintypeFieldList = new ArrayList<FormField>();
        nyukintypeFieldList.add(new FormField("baseBean.nyknkindkbn1", "現金", 0));
        nyukintypeFieldList.add(new FormField("baseBean.nyknkindkbn2", "カード入金", 0));
        nyukintypeFieldList.add(new FormField("baseBean.nyknkindkbn3", "銀行振込", 0));
        nyukintypeFieldList.add(new FormField("baseBean.nyknkindkbn4", "郵便振込", 0));
        nyukintypeFieldList.add(new FormField("baseBean.nyknkindkbn5", "口座振替", 0));
        nyukintypeFieldList.add(new FormField("baseBean.nyknkindkbn6", "コンビニ", 0));
        nyukintypeFieldList.add(new FormField("baseBean.nyknkindkbn7", "手形", 0));
        nyukintypeFieldList.add(new FormField("baseBean.nyknkindkbn8", "チケット", 0));
        nyukintypeFieldList.add(new FormField("baseBean.nyknkindkbn9", "相殺", 0));
        nyukintypeFieldList.add(new FormField("baseBean.nyknkindkbn10", "手数料", 0));
        nyukintypeFieldList.add(new FormField("baseBean.nyknkindkbn11", "値引", 0));
        nyukintypeFieldList.add(new FormField("baseBean.nyknkindkbn12", "過入金", 0));
        nyukintypeFieldList.add(new FormField("baseBean.nyknkindkbn13", "売上調整額", 0));
        nyukintypeFieldList.add(new FormField("baseBean.nyknkindkbn14", "その他", 0));
        return nyukintypeFieldList;
    }
}
