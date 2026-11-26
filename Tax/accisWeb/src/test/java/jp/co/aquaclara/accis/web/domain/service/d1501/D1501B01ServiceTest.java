package jp.co.aquaclara.accis.web.domain.service.d1501;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M006TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M013TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M046TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M050TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S06T002TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T004TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T005TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T006TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07W003TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07W006TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S10T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M006TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M006TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M050TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M050TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M050TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S06T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S06T001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S06T002TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S06T002TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T004TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T004TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T004TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T005TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T005TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T005TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T006TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T006TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07W003TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07W003TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07W006TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07W006TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S10T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S10T001TEntityExample;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.web.controller.commons.DbManagementSession;
import jp.co.aquaclara.accis.web.domain.dto.d1501.D1501W11SearchConditionDto;
import lombok.extern.slf4j.Slf4j;

/**
 * 請求締処理実行サービステストクラス
 * 
 * @author accisdev044
 *
 */
@Slf4j
@SpringBootTest
class D1501B01ServiceTest extends D1501B01Service {

    /** プログラムID */
    private static final String PROGRAM_ID = "D1502B01";
    /** AP基本コード（テストデータ用） */
    private static final String APKIHONCD_TEST = "XX1";

    /** システム管理マスタDao */
    @Autowired
    private S02M006TDao s02M006TDao;
    /** 得意先マスタDao */
    @Autowired
    private S02M013TDao s02M013TDao;
    /** ショップマスタDao */
    @Autowired
    private S02M046TDao s02M046TDao;
    /** 得意先実績テーブルDao */
    @Autowired
    private S02M050TDao s02M050TDao;
    /** 売上明細テーブルDao */
    @Autowired
    private S06T002TDao s06T002TDao;
    /** 請求締ワークDao */
    @Autowired
    private S07W003TDao s07W003TDao;
    /** 請求締処理管理テーブルDao */
    @Autowired
    private S07T004TDao s07T004TDao;
    /** 請求締処理詳細管理テーブルDao */
    @Autowired
    private S07T005TDao s07T005TDao;
    /** 請求明細関連テーブルDao */
    @Autowired
    private S07T006TDao s07T006TDao;
    /** 請求締用対象得意先ワークDao */
    @Autowired
    private S07W006TDao s07W006TDao;
    /** 入金明細テーブルDao */
    @Autowired
    private S10T001TDao s10T001TDao;

    /**
     * 滞留更新処理（正常系テスト）
     */
    @Test
    @Rollback
    void testExecuteTairyuUpdate() {
        // テスト値リスト（0:締日、1:過去伝票締年月、2:滞留区分、3:繰越金額、4:締年月、5:請求区分）
        List<List<String>> valuesList = new ArrayList<List<String>>();
        valuesList.add(List.of("10", "209911", "0", "1000", "210001", "1"));
        valuesList.add(List.of("30", "209911", "0", "1000", "210001", "1"));
        valuesList.add(List.of("31", "209912", "0", "1000", "210001", "1"));
        valuesList.add(List.of("10", "209911", "0", "3000", "210001", "1"));
        valuesList.add(List.of("30", "209911", "0", "3000", "210001", "1"));
        valuesList.add(List.of("31", "209912", "0", "3000", "210001", "1"));
        valuesList.add(List.of("10", "209911", "0", "4000", "210001", "1"));
        valuesList.add(List.of("30", "209911", "0", "4000", "210001", "1"));
        valuesList.add(List.of("31", "209912", "0", "4000", "210001", "1"));
        valuesList.add(List.of("10", "209911", "1", "-1000", "210001", "1"));
        valuesList.add(List.of("30", "209911", "1", "-1000", "210001", "1"));
        valuesList.add(List.of("31", "209912", "1", "-1000", "210001", "1"));
        valuesList.add(List.of("10", "210002", "0", "1000", "210004", "1"));
        valuesList.add(List.of("30", "210003", "0", "1000", "210004", "1"));
        valuesList.add(List.of("10", "210002", "0", "3000", "210004", "1"));
        valuesList.add(List.of("30", "210003", "0", "3000", "210004", "1"));
        valuesList.add(List.of("10", "210002", "0", "4000", "210004", "1"));
        valuesList.add(List.of("30", "210003", "0", "4000", "210004", "1"));
        valuesList.add(List.of("10", "210002", "1", "-1000", "210004", "1"));
        valuesList.add(List.of("30", "210003", "1", "-1000", "210004", "1"));
        valuesList.add(List.of("10", "209911", "0", "1000", "210001", "2"));
        valuesList.add(List.of("10", "209911", "0", "3000", "210001", "2"));
        valuesList.add(List.of("10", "209911", "0", "4000", "210001", "2"));
        valuesList.add(List.of("10", "209911", "1", "-1000", "210001", "2"));

        // テスト結果値リスト（0:滞留区分、1:滞留金額、2:滞留発生経過月、3:滞留発生回数）
        List<List<String>> resultsList = new ArrayList<List<String>>();
        resultsList.add(List.of("1", "1000", "2", "1"));
        resultsList.add(List.of("1", "1000", "1", "1"));
        resultsList.add(List.of("1", "1000", "1", "1"));
        resultsList.add(List.of("2", "3000", "2", "1"));
        resultsList.add(List.of("2", "3000", "1", "1"));
        resultsList.add(List.of("2", "3000", "1", "1"));
        resultsList.add(List.of("3", "4000", "2", "1"));
        resultsList.add(List.of("3", "4000", "1", "1"));
        resultsList.add(List.of("3", "4000", "1", "1"));
        resultsList.add(List.of("0", "0", "0", "0"));
        resultsList.add(List.of("0", "0", "0", "0"));
        resultsList.add(List.of("0", "0", "0", "0"));
        resultsList.add(List.of("1", "1000", "2", "1"));
        resultsList.add(List.of("1", "1000", "2", "1"));
        resultsList.add(List.of("2", "3000", "2", "1"));
        resultsList.add(List.of("2", "3000", "2", "1"));
        resultsList.add(List.of("3", "4000", "2", "1"));
        resultsList.add(List.of("3", "4000", "2", "1"));
        resultsList.add(List.of("0", "0", "0", "0"));
        resultsList.add(List.of("0", "0", "0", "0"));
        resultsList.add(List.of("1", "1000", "2", "1"));
        resultsList.add(List.of("2", "3000", "2", "1"));
        resultsList.add(List.of("3", "4000", "2", "1"));
        resultsList.add(List.of("0", "0", "0", "0"));

        // テストデータ削除
        this.clearTestData();
        // 運用日付データ生成
        this.insertS02M006T("1", "21000131");

        for (int i = 0; i < valuesList.size(); i++) {
            final List<String> values = valuesList.get(i);
            final List<String> results = resultsList.get(i);
            final String fixYearMonth = values.get(4);
            final String shimedd = values.get(0);
            final String shimedydm = values.get(1);
            final String beforeShimedydm = String.valueOf(Long.parseLong(shimedydm) - 1L);
            final String tairyukbn = values.get(2);
            final String kurikoshikin = values.get(3);
            final String skykbn = values.get(5);
            final D1501W11SearchConditionDto dto = getExecuteTestDto("1", shimedd, fixYearMonth);
            final DbManagementSession db = dto.getDbManagementSession();

            // データ生成
            insertS02M017T("1", "1", tairyukbn, skykbn);
            // 滞留基準日
            insertS07T001T(shimedd, "1", null, "1000", null, shimedydm, "0", "1", kurikoshikin);
            // 滞留基準日までの1か月分
            insertS07T001T("01", "1", null, "1000", null, shimedydm, "1", "2", kurikoshikin);
            // 滞留基準日までの2か月
            insertS07T001T("01", "1", null, "1000", null, beforeShimedydm, "1", "3", kurikoshikin);
            // 締年月
            insertS07T001T(shimedd, "1", null, "1000", null, fixYearMonth, "1", "4", kurikoshikin);
            // 締年月（子の得意先分）
            if (skykbn.equals("2")) {
                insertS07T001T(shimedd, "1", null, "1000", null, fixYearMonth, "1", "5",
                    kurikoshikin, "2");
            }
            insertS07W003T(db.getSystemDate(), db.getSystemTime(), "1");

            // 滞留更新処理実行
            log.debug("■■■■■■■■■■ " + values.toString() + " ■■■■■■■■■■");
            this.executeTairyuUpdate(dto, dto.getSkyshimedate().replace("/", ""));

            // 請求先マスタ更新値検証
            S02M017TEntityExample example = new S02M017TEntityExample();
            example.createCriteria().andApkihoncdEqualTo(APKIHONCD_TEST);
            S02M017TEntity entity = this.s02M017TDao.selectByExample(example).get(0);
            assertEquals(entity.getTairyukbn(), results.get(0));
            assertEquals(entity.getTairyukin(), Long.parseLong(results.get(1)));
            assertEquals(entity.getTairyuhasseikeikadm(), results.get(2));
            assertEquals(entity.getTairyuhasseikaisu(), Short.parseShort(results.get(3)));
        }
    }

    /**
     * 請求締処理（正常系テスト）
     */
    @Test
    @Rollback
    void testExecuteSkyShimeNormal() {
        // テスト条件リスト定義
        // 0:回収方法区分、1:税計算区分、2:インボイス施行日、3:税計算区分売上切替日、4:締処理区分、5:締日、6:状態ステータス、
        // 7:繰越金額、8:消込金額、9:データ作成元区分、10:消込残高、11:請求先管理区分、12:売上日、13:税区分
        List<List<String>> testList = new ArrayList<List<String>>();
        testList.add(Arrays.asList("1", "1", "20200101", "20200101", "1", "01", null, null, "01",
            "0", "1", null, "1"));// 現金、内税、インボイス施行後、締
        testList.add(Arrays.asList("1", "X", "20200101", "21010101", "1", "01", null, null, "01",
            "0", "1", null, "1"));// 現金、外税、インボイス施行後、締
        testList.add(Arrays.asList("7", "1", "20200101", "20200101", "1", "01", null, null, "01",
            "0", "1", null, "1"));// 代引き、内税、インボイス施行後、締
        testList.add(Arrays.asList("2", "2", "20200101", "20200101", "1", "01", null, null, "01",
            "0", "1", null, "1"));// クレジット、外税、インボイス施行後、締
        testList.add(Arrays.asList("2", "1", "20200101", "21010101", "1", "01", null, null, "01",
            "0", "1", null, "1"));// クレジット、内税、インボイス施行後、締
        testList.add(Arrays.asList("2", "2", "22000101", "20200101", "1", "01", "02", null, "01",
            "0", "1", null, "1"));// クレジット、外税、インボイス施行前、締
        testList.add(Arrays.asList("2", "1", "22000101", "22010101", "1", "01", "02", null, "01",
            "0", "1", null, "1"));// クレジット、内税、インボイス施行前、締
        testList.add(Arrays.asList("1", "1", "20200101", "20200101", "2", "31", null, "0", "01",
            "0", "1", null, "1"));// 現金、内税、インボイス施行後、再締
        testList.add(Arrays.asList("1", "1", "20200101", "20200101", "2", "31", null, "0", "23",
            "0", "1", null, "1"));// 現金、内税、インボイス施行後、再締
        testList.add(Arrays.asList("1", "1", "22000101", "20200101", "1", "01", "02", null, "01",
            "0", "1", null, "1"));// 現金、内税、インボイス施行前、締
        testList.add(Arrays.asList("7", "1", "20200101", "20200101", "1", "01", null, null, "XX",
            "0", "1", null, "1"));// 代引き、内税、インボイス施行後、締、売上・入金伝票無し
        testList.add(Arrays.asList("1", "1", "20200101", "20200101", "1", "01", null, null, "01",
            "-2", "1", null, "1"));// 現金、内税、インボイス施行後、締、消込残高マイナス
        testList.add(Arrays.asList("1", "1", "20200101", "21010101", "1", "01", null, null, "01",
            "0", "2", null, "1"));// 現金、内税、インボイス施行後、締、卸
        testList.add(Arrays.asList("1", "1", "22000101", "22010101", "1", "01", "02", null, "01",
            "0", "2", null, "1"));// 現金、内税、インボイス施行前、締、卸
        testList.add(Arrays.asList("1", "1", "20200101", "20200101", "1", "01", null, null, "01",
            "0", "1", "21000201", "1"));// 現金、内税、インボイス施行後、締、未来売上日
        testList.add(Arrays.asList("1", "1", "20200101", "20200101", "1", "01", null, null, "01",
            "0", "1", null, "2"));// 現金、内税、インボイス施行後、締、非課税
        testList.add(Arrays.asList("1", "1", "20200101", "20200101", "1", "01", null, null, "01",
            "0", "1", null, "3"));// 現金、内税、インボイス施行後、締、不課税
        testList.add(Arrays.asList("1", "1", "20200101", "20200101", "1", "01", null, null, "01",
            "0", "1", null, "4"));// 現金、内税、インボイス施行後、締、軽減税率

        // パラメータDto生成
        D1501W11SearchConditionDto param = getExecuteTestDto("1");
        // テスト実行
        for (List<String> test : testList) {
            // 検証用項目値設定
            final String kaishuhoho = test.get(0);
            final String taxkeisankbn = test.get(1);
            final String invoiceDate = test.get(2);
            final String taxkeisankbnurikirikaedd = test.get(3);
            final String fixProcessType = test.get(4);
            final String skysakikanrikbn = test.get(10);
            final String fixDay = test.get(5);
            final String systemDate = param.getDbManagementSession().getSystemDate();
            final String systemTime = param.getDbManagementSession().getSystemTime();
            Long keshikomizdTmp = Long.parseLong(test.get(9));
            keshikomizdTmp = keshikomizdTmp < 0 ? keshikomizdTmp + 1 : keshikomizdTmp;
            final String uridd = test.get(11);
            final String taxkbn = test.get(12);
            final boolean isInvoice = !(kaishuhoho.equals("7") || invoiceDate.equals("22000101"));
            final boolean isFixCancel = fixProcessType.equals("2");
            final boolean isUriNyukinNotExists = test.get(8).equals("XX");
            final boolean isNormalTax = taxkbn.equals("1");
            final boolean isLowerTax = taxkbn.equals("4");
            final boolean isNotTax = !isNormalTax && !isLowerTax;
            final Long tax = isNormalTax ? 123L : isLowerTax ? 98L : 0L;

            // テストデータ生成
            createTestData();
            insertS02M046T(invoiceDate, test.get(3), taxkeisankbn.equals("X") ? "1" : taxkeisankbn,
                taxkeisankbn.equals("X") ? "2"
                    : taxkeisankbnurikirikaedd.equals("20200101") ? "2" : taxkeisankbn); // ショップマスタ
            insertS02M013T(test.get(6)); // 得意先マスタ
            insertS02M017T(kaishuhoho, skysakikanrikbn); // 請求先マスタ
            insertS06T001T(test.get(8), uridd); // 売上テーブル
            insertS06T002T("0", taxkbn); // 売上明細テーブル
            insertS07T004T(param.getPrcskbn(), "1"); // 請求締処理管理テーブル
            insertS07W003T(systemDate, systemTime, fixProcessType); // 請求締ワーク
            // 請求残高テーブル
            insertS07T001T(fixDay, kaishuhoho, test.get(7), keshikomizdTmp.toString());
            insertS07T001T(fixDay, kaishuhoho, test.get(7), keshikomizdTmp.toString(), null,
                "210001", "1", "00002", "2000", "1", "1"); // 同一締日で複数データ生成

            // 売上伝票・入金伝票データ削除
            if (isUriNyukinNotExists) {
                this.deleteS06T001T();
                this.deleteS10T001T();
            }

            // 請求締処理
            log.debug("■■■■■■■■■■ " + test.toString() + " ■■■■■■■■■■");
            this.executeSkyShime(param, param.getSkyshimedate().replaceAll("/", ""));

            // 売上日が未来日の場合、請求明細関連テーブルデータが生成されないことの検証
            if (uridd != null) {
                S07T006TEntityExample example = new S07T006TEntityExample();
                example.createCriteria().andApkihoncdEqualTo(APKIHONCD_TEST)
                    .andDnpynoEqualTo("X0000");
                List<S07T006TEntity> entitys = this.s07T006TDao.selectByExample(example);
                assertEquals(entitys.size(), 0);
                continue;
            }

            // 売上テーブルデータ検証
            // -----------------------------------------------------------------
            S06T001TEntityExample s06T001TExmp = new S06T001TEntityExample();
            s06T001TExmp.createCriteria().andApkihoncdEqualTo(param.getApkihoncd());
            s06T001TExmp.setOrderByClause("URIDNPYNO DESC");
            List<S06T001TEntity> s06T001TEntityList = s06T001TDao.selectByExample(s06T001TExmp);
            assertEquals(s06T001TEntityList.size(),
                isUriNyukinNotExists ? 0 : (!isInvoice || isNotTax ? 1 : 2));
            int indexS06T001T = 1;
            for (S06T001TEntity entity : s06T001TEntityList) {
                assertEquals(entity.getShopcd(), param.getShopcd());
                assertNotNull(entity.getUridnpyno());
                assertNotNull(entity.getEdano());
                if (indexS06T001T == 2) {
                    assertNull(entity.getAcptdnpyno());
                    assertNull(entity.getAcptdnpyedano());
                    assertEquals(entity.getTorihikikbn(), "1");
                    assertEquals(entity.getTokuisakicd(), "1");
                    assertNull(entity.getSiiresakicd());
                    assertNull(entity.getNyukamotocd());
                    assertEquals(entity.getUridd(), "21000131");
                    assertEquals(entity.getAcptdd(), "00000000");
                    assertEquals(entity.getUrikin(), 0L);
                    assertEquals(entity.getShohitaxkin(), tax);
                    assertEquals(entity.getSankoushohitaxkin(), 0L);
                    assertNull(entity.getAcpttnshacd());
                    assertNull(entity.getDlvrytnshacd());
                    assertNull(entity.getDlvrykankatsuapkihoncd());
                    assertNull(entity.getDlvrykankatsushopcd());
                    assertNull(entity.getDlvrykyotencd());
                    assertNull(entity.getDlvrykbn());
                    assertNull(entity.getDlvrycmpnycd());
                    assertEquals(entity.getShukkamotokbn(), "2");
                    assertNull(entity.getEigyotnshacd());
                    assertNull(entity.getJissekitnshacd());
                    assertEquals(entity.getSkysakicd(), "1");
                    assertEquals(entity.getSkyshimedydm(), "210001");
                    assertEquals(entity.getSkyshimedd(), "31");
                    assertEquals(entity.getSkyprcsjissidd(), "20200101");
                    assertNull(entity.getGetsujishimedydm());
                    assertNull(entity.getGetsujishimedd());
                    assertEquals(entity.getGetsujiprcsjissidd(), "00000000");
                    assertEquals(entity.getUrishimeprcsdd(), "20200101");
                    assertNull(entity.getNebikishimedydm());
                    assertNull(entity.getNebikishimedd());
                    assertEquals(entity.getNebikiprcsjissidd(), "00000000");
                    assertNull(entity.getDnpybiko());
                    assertNull(entity.getDnpyno());
                    assertEquals(entity.getSkyshootptarinashikbn(), "0");
                    assertEquals(entity.getDatsksmotokbn(), "23");
                    assertEquals(entity.getTaxkeisankbn(), "2");
                    assertNull(entity.getSiirednpyno());
                    assertNull(entity.getNykndnpyno());
                    assertNull(entity.getNyknkin());
                    assertNull(entity.getNyknkind());
                    assertEquals(entity.getYukokbn(), "1");
                    assertEquals(entity.getToshaapkihoncd(), APKIHONCD_TEST);
                    assertEquals(entity.getToshashopcd(), "000");
                    assertEquals(entity.getToshacd(), "00001");
                    assertEquals(entity.getInptsystmymd(), systemDate);
                    assertNotNull(entity.getInptsystmjikan());
                    assertEquals(entity.getInptunyoymd(), "20200101");
                    assertEquals(entity.getInptipadrse(), "0.0.0.1");
                    assertEquals(entity.getInptpgmid(), PROGRAM_ID);
                } else {
                    assertEquals(entity.getSkysakicd(), "1");
                    assertEquals(entity.getSkyshimedydm(), "210001");
                    assertEquals(entity.getSkyshimedd(), "31");
                    assertEquals(entity.getSkyprcsjissidd(), "20200101");
                }
                assertEquals(entity.getSkyprcsjissikbn(), (short) 1);
                assertEquals(entity.getKoshaapkihoncd(), APKIHONCD_TEST);
                assertEquals(entity.getKoshashopcd(), "000");
                assertEquals(entity.getKoshacd(), "00001");
                assertEquals(entity.getKshnsystmymd(), systemDate);
                assertNotNull(entity.getKshnsystmjikan());
                assertEquals(entity.getKshnunyoymd(), "20200101");
                assertEquals(entity.getKshnipadrse(), "0.0.0.1");
                assertEquals(entity.getKshnpgmid(), PROGRAM_ID);
                indexS06T001T++;
            }

            // 売上明細テーブルデータ検証
            // -----------------------------------------------------------------
            if (isInvoice && !isNotTax) {
                S06T002TEntityExample s06T002TExmp = new S06T002TEntityExample();
                s06T002TExmp.createCriteria().andApkihoncdEqualTo(param.getApkihoncd())
                    .andUridnpynoNotEqualTo("X0000");
                s06T002TExmp.setOrderByClause("URIDNPYMEISAINO");
                List<S06T002TEntity> s06T002TEntityList = s06T002TDao.selectByExample(s06T002TExmp);
                assertEquals(s06T002TEntityList.size(), 1);
                S06T002TEntity s06T002TEntity = s06T002TEntityList.get(0);
                final int uridnpymeisaino = s06T002TEntity.getShncd().equals("099901") ? 1 : 2;
                assertEquals(s06T002TEntity.getShopcd(), param.getShopcd());
                assertNotNull(s06T002TEntity.getUridnpyno());
                assertEquals(s06T002TEntity.getEdano(), (short) 0);
                assertEquals(s06T002TEntity.getUridnpymeisaino(), uridnpymeisaino);
                assertEquals(s06T002TEntity.getUridnpymeisaiedano(), (short) 1);
                assertEquals(s06T002TEntity.getLctgrycd(), "99");
                assertEquals(s06T002TEntity.getMctgrycd(), "001");
                assertEquals(s06T002TEntity.getSctgrycd(), "001");
                assertEquals(s06T002TEntity.getShncd(), uridnpymeisaino == 1 ? "099901" : "099902");
                assertEquals(s06T002TEntity.getKeijokbn(), "01");
                assertEquals(s06T002TEntity.getSuryo(), BigDecimal.ONE);
                assertEquals(s06T002TEntity.getHyojununt(), BigDecimal.ZERO);
                assertEquals(s06T002TEntity.getUnt(), BigDecimal.ZERO);
                assertEquals(s06T002TEntity.getKin(), 0L);
                assertEquals(s06T002TEntity.getUntshohitaxkin(), 0L);
                assertEquals(s06T002TEntity.getShohitaxkin(), tax);
                assertEquals(s06T002TEntity.getSankoushohitaxkin(), 0L);
                assertEquals(s06T002TEntity.getTaxkbn(), taxkbn);
                assertEquals(s06T002TEntity.getMeisaikbn(), "0");
                assertEquals(s06T002TEntity.getNyuryokumeisaiflg(), "1");
                assertEquals(s06T002TEntity.getTanpinmeisaiflg(), "1");
                assertNull(s06T002TEntity.getMeisaibiko());
                assertNull(s06T002TEntity.getStshncd());
                assertNull(s06T002TEntity.getKatasiki());
                assertEquals(s06T002TEntity.getSiiresuryo(), BigDecimal.ZERO);
                assertEquals(s06T002TEntity.getSiirekin(), 0L);
                assertEquals(s06T002TEntity.getSiireunt(), BigDecimal.ZERO);
                assertEquals(s06T002TEntity.getSiireshohitaxkin(), 0L);
                assertEquals(s06T002TEntity.getZaikokshnsumiflg(), "1");
                assertEquals(s06T002TEntity.getTokuisakikbn(), "A");
                assertEquals(s06T002TEntity.getJutakudlvrycstkbn(), "B");
                assertEquals(s06T002TEntity.getRoyaltyshimeymd(), "00000000");
                assertEquals(s06T002TEntity.getYukokbn(), "1");
                assertEquals(s06T002TEntity.getToshaapkihoncd(), APKIHONCD_TEST);
                assertEquals(s06T002TEntity.getToshashopcd(), "000");
                assertEquals(s06T002TEntity.getToshacd(), "00001");
                assertEquals(s06T002TEntity.getInptsystmymd(), systemDate);
                assertNotNull(s06T002TEntity.getInptsystmjikan());
                assertEquals(s06T002TEntity.getInptunyoymd(), "20200101");
                assertEquals(s06T002TEntity.getInptipadrse(), "0.0.0.1");
                assertEquals(s06T002TEntity.getInptpgmid(), PROGRAM_ID);
                assertEquals(s06T002TEntity.getKoshaapkihoncd(), APKIHONCD_TEST);
                assertEquals(s06T002TEntity.getKoshashopcd(), "000");
                assertEquals(s06T002TEntity.getKoshacd(), "00001");
                assertEquals(s06T002TEntity.getKshnsystmymd(), systemDate);
                assertNotNull(s06T002TEntity.getKshnsystmjikan());
                assertEquals(s06T002TEntity.getKshnunyoymd(), "20200101");
                assertEquals(s06T002TEntity.getKshnipadrse(), "0.0.0.1");
                assertEquals(s06T002TEntity.getKshnpgmid(), PROGRAM_ID);
            }

            // 請求締処理詳細管理テーブルデータ検証
            // -----------------------------------------------------------------
            S07T005TEntityExample s07T005TExmp = new S07T005TEntityExample();
            s07T005TExmp.createCriteria().andApkihoncdEqualTo(param.getApkihoncd());
            for (S07T005TEntity entity : s07T005TDao.selectByExample(s07T005TExmp)) {
                if (entity.getPrcsid().equals("D1502010") || entity.getPrcsid().equals("D1502020")
                    || entity.getPrcsid().equals("D1502040")
                    || entity.getPrcsid().equals("D1502060")
                    || entity.getPrcsid().equals("D1502100")) {
                    assertNotNull(entity.getPrcsstrtdd());
                    assertNotNull(entity.getPrcsstrtjikan());
                    assertNotNull(entity.getPrcsshrydd());
                    assertNotNull(entity.getPrcsstrtjikan());
                    assertNull(entity.getRecovstrtdd());
                    assertNull(entity.getRecovstrtjikan());
                    assertNull(entity.getRecovshrydd());
                    assertNull(entity.getRecovshryjikan());
                    assertEquals(entity.getPrcsjotai(), "0");
                    assertNull(entity.getMsg());
                    assertEquals(entity.getToshaapkihoncd(), APKIHONCD_TEST);
                    assertEquals(entity.getToshashopcd(), "000");
                    assertEquals(entity.getToshacd(), "00001");
                    assertEquals(entity.getInptsystmymd(), systemDate);
                    assertNotNull(entity.getInptsystmjikan());
                    assertEquals(entity.getInptunyoymd(), "20200101");
                    assertEquals(entity.getInptipadrse(), "0.0.0.1");
                    assertEquals(entity.getInptpgmid(), PROGRAM_ID);
                    assertEquals(entity.getKoshaapkihoncd(), APKIHONCD_TEST);
                    assertEquals(entity.getKoshashopcd(), "000");
                    assertEquals(entity.getKoshacd(), "00001");
                    assertEquals(entity.getKshnsystmymd(), systemDate);
                    assertNotNull(entity.getKshnsystmjikan());
                    assertEquals(entity.getKshnunyoymd(), "20200101");
                    assertEquals(entity.getKshnipadrse(), "0.0.0.1");
                    assertEquals(entity.getKshnpgmid(), PROGRAM_ID);
                }
            }

            // 請求明細関連テーブルデータ検証
            // -----------------------------------------------------------------
            S07T006TEntityExample s07T006TExmp = new S07T006TEntityExample();
            s07T006TExmp.createCriteria().andApkihoncdEqualTo(param.getApkihoncd());
            s07T006TExmp.setOrderByClause("URINYKNKBN DESC");
            List<S07T006TEntity> s07T006TEntityList = s07T006TDao.selectByExample(s07T006TExmp);
            assertEquals(s07T006TEntityList.size(),
                isUriNyukinNotExists ? 0 : (!isInvoice || isNotTax ? 2 : 3));
            int indexS07T006T = 1;
            for (S07T006TEntity entity : s07T006TEntityList) {
                assertEquals(entity.getShopcd(), param.getShopcd());
                assertNotNull(entity.getSkyshono());
                assertEquals(entity.getSkyshonoedano(), 0);
                assertNotNull(entity.getDnpyno());
                assertEquals(entity.getNykndnpynoedano(), "0");
                assertEquals(entity.getUrinyknkbn(), indexS07T006T == 1 ? "1" : "0");
                assertEquals(entity.getSkysakicd(), "1");
                assertEquals(entity.getTokuisakicd(), "1");
                assertEquals(entity.getYukokbn(), "1");
                assertEquals(entity.getToshaapkihoncd(), APKIHONCD_TEST);
                assertEquals(entity.getToshashopcd(), "000");
                assertEquals(entity.getToshacd(), "00001");
                assertEquals(entity.getInptsystmymd(), systemDate);
                assertNotNull(entity.getInptsystmjikan());
                assertEquals(entity.getInptunyoymd(), "20200101");
                assertEquals(entity.getInptipadrse(), "0.0.0.1");
                assertEquals(entity.getInptpgmid(), PROGRAM_ID);
                assertEquals(entity.getKoshaapkihoncd(), APKIHONCD_TEST);
                assertEquals(entity.getKoshashopcd(), "000");
                assertEquals(entity.getKoshacd(), "00001");
                assertEquals(entity.getKshnsystmymd(), systemDate);
                assertNotNull(entity.getKshnsystmjikan());
                assertEquals(entity.getKshnunyoymd(), "20200101");
                assertEquals(entity.getKshnipadrse(), "0.0.0.1");
                assertEquals(entity.getKshnpgmid(), PROGRAM_ID);
                indexS07T006T++;
            }

            // 請求締処理管理テーブルデータ検証
            // -----------------------------------------------------------------
            S07T004TEntityKey s07T004TEntityKey = new S07T004TEntityKey();
            s07T004TEntityKey.setApkihoncd(param.getApkihoncd());
            s07T004TEntityKey.setShopcd(param.getShopcd());
            S07T004TEntity s07T004TEntity = s07T004TDao.selectByPrimaryKey(s07T004TEntityKey);
            assertNotNull(s07T004TEntity.getPrcsstrtdd());
            assertNotNull(s07T004TEntity.getPrcsstrtjikan());
            assertNotNull(s07T004TEntity.getPrcsshrydd());
            assertNotNull(s07T004TEntity.getPrcsstrtjikan());
            assertNull(s07T004TEntity.getRecovstrtdd());
            assertNull(s07T004TEntity.getRecovstrtjikan());
            assertNull(s07T004TEntity.getRecovshrydd());
            assertNull(s07T004TEntity.getRecovshryjikan());
            assertEquals(s07T004TEntity.getPrcsjotai(), "0");
            assertEquals(s07T004TEntity.getKoshaapkihoncd(), APKIHONCD_TEST);
            assertEquals(s07T004TEntity.getKoshashopcd(), "000");
            assertEquals(s07T004TEntity.getKoshacd(), "00001");
            assertEquals(s07T004TEntity.getKshnsystmymd(), systemDate);
            assertNotNull(s07T004TEntity.getKshnsystmjikan());
            assertEquals(s07T004TEntity.getKshnunyoymd(), "20200101");
            assertEquals(s07T004TEntity.getKshnipadrse(), "0.0.0.1");
            assertEquals(s07T004TEntity.getKshnpgmid(), PROGRAM_ID);

            // 得意先実績テーブルデータ検証
            // -----------------------------------------------------------------
            S02M050TEntityKey s02M050TEntityKey = new S02M050TEntityKey();
            s02M050TEntityKey.setApkihoncd(param.getApkihoncd());
            s02M050TEntityKey.setShopcd(param.getShopcd());
            s02M050TEntityKey.setTokuisakicd("1");
            S02M050TEntity s02M050TEntity = s02M050TDao.selectByPrimaryKey(s02M050TEntityKey);
            if (!isInvoice) {
                assertEquals(s02M050TEntity.getGenzaizd(), 0);
                assertNull(s02M050TEntity.getKoshaapkihoncd());
                assertNull(s02M050TEntity.getKoshashopcd());
                assertNull(s02M050TEntity.getKoshacd());
                assertNull(s02M050TEntity.getKshnsystmymd());
                assertNull(s02M050TEntity.getKshnsystmjikan());
                assertNull(s02M050TEntity.getKshnunyoymd());
                assertNull(s02M050TEntity.getKshnipadrse());
                assertNull(s02M050TEntity.getKshnpgmid());
            } else if (!isNotTax) {
                assertNotNull(s02M050TEntity.getGenzaizd());
                assertEquals(s02M050TEntity.getKoshaapkihoncd(), APKIHONCD_TEST);
                assertEquals(s02M050TEntity.getKoshashopcd(), "000");
                assertEquals(s02M050TEntity.getKoshacd(), "00001");
                assertEquals(s02M050TEntity.getKshnsystmymd(), systemDate);
                assertNotNull(s02M050TEntity.getKshnsystmjikan());
                assertEquals(s02M050TEntity.getKshnunyoymd(), "20200101");
                assertEquals(s02M050TEntity.getKshnipadrse(), "0.0.0.1");
                assertEquals(s02M050TEntity.getKshnpgmid(), PROGRAM_ID);
            }

            // 請求締用対象得意先ワークデータ検証
            // -----------------------------------------------------------------
            S07W006TEntityExample s07W006TExmp = new S07W006TEntityExample();
            s07W006TExmp.createCriteria().andApkihoncdEqualTo(param.getApkihoncd());
            S07W006TEntity entity = s07W006TDao.selectByExample(s07W006TExmp).get(0);
            assertEquals(entity.getShopcd(), param.getShopcd());
            assertEquals(entity.getSkysakicd(), "1");
            assertEquals(entity.getTokuisakicd(), "1");
            assertEquals(entity.getZnkshimedd(), isFixCancel ? null : "210001" + fixDay);
            assertEquals(entity.getShimesumiflg(), "0");
            assertEquals(entity.getKurikoshikin(), isFixCancel ? 0 : 2000L);
            assertEquals(entity.getShohitaxkin(), isFixCancel ? 123L : (!isInvoice ? 0L : tax));
            assertEquals(entity.getToshaapkihoncd(), APKIHONCD_TEST);
            assertEquals(entity.getToshashopcd(), "000");
            assertEquals(entity.getToshacd(), "00001");
            assertEquals(entity.getInptsystmymd(), systemDate);
            assertNotNull(entity.getInptsystmjikan());
            assertEquals(entity.getInptunyoymd(), "20200101");
            assertEquals(entity.getInptipadrse(), "0.0.0.1");
            assertEquals(entity.getInptpgmid(), PROGRAM_ID);
            assertEquals(entity.getKoshaapkihoncd(), APKIHONCD_TEST);
            assertEquals(entity.getKoshashopcd(), "000");
            assertEquals(entity.getKoshacd(), "00001");
            assertEquals(entity.getKshnsystmymd(), systemDate);
            assertNotNull(entity.getKshnsystmjikan());
            assertEquals(entity.getKshnunyoymd(), "20200101");
            assertEquals(entity.getKshnipadrse(), "0.0.0.1");
            assertEquals(entity.getKshnpgmid(), PROGRAM_ID);

            // 請求残高テーブルデータ検証
            // -----------------------------------------------------------------
            S07T001TEntityExample exmp = new S07T001TEntityExample();
            exmp.createCriteria().andApkihoncdEqualTo(APKIHONCD_TEST).andShimeddEqualTo("31");
            S07T001TEntity s07T001TEntity = s07T001TDao.selectByExample(exmp).get(0);
            assertEquals(s07T001TEntity.getShopcd(), param.getShopcd());
            assertEquals(s07T001TEntity.getSkysakicd(), "1");
            assertEquals(s07T001TEntity.getTokuisakicd(), "1");
            assertEquals(s07T001TEntity.getShimedydm(), "210001");
            assertEquals(s07T001TEntity.getRenban(), isFixCancel ? 2 : 0);
            assertEquals(s07T001TEntity.getSkyprcsjissikbn(), (short) 1);
            assertEquals(s07T001TEntity.getShimeprcsjissidd(), "20200101");
            assertEquals(s07T001TEntity.getZnkshimedd(), isFixCancel ? null : "210001" + fixDay);
            assertEquals(s07T001TEntity.getKonkaishimedd(), "21000131");
            assertEquals(s07T001TEntity.getZnkkurikoshikin(), isFixCancel ? 0 : 2000L);
            assertEquals(s07T001TEntity.getKonkaitaxnukiurikin(),
                isUriNyukinNotExists ? 0L : 1230L);
            assertEquals(s07T001TEntity.getKonkaishohitaxkin(), !isInvoice ? 0L : tax);
            assertEquals(s07T001TEntity.getKonkainyknkin(), isUriNyukinNotExists ? 0L : 3300L);

            Long kurikoshikin =
                s07T001TEntity.getZnkkurikoshikin() + s07T001TEntity.getKonkaitaxnukiurikin()
                    + s07T001TEntity.getKonkaishohitaxkin() - s07T001TEntity.getKonkainyknkin();
            assertEquals(s07T001TEntity.getKurikoshikin(), kurikoshikin);
            assertEquals(s07T001TEntity.getKeshikomikin(), 0);

            int keshikomizd = (int) (isUriNyukinNotExists ? 0 : (!isInvoice ? 1230 : 1230 + tax));
            assertEquals(s07T001TEntity.getKeshikomizd(), isNotTax ? 1230 : keshikomizd);

            Long urikinSum = isUriNyukinNotExists ? 0L : 1230L;
            Long shohitaxkinSum = !isInvoice ? 0L : tax;
            assertEquals(s07T001TEntity.getN8perzeinukiurikin(), !isLowerTax ? 0L : urikinSum);
            assertEquals(s07T001TEntity.getN8pershohitaxkin(), !isLowerTax ? 0L : shohitaxkinSum);
            assertEquals(s07T001TEntity.getN10perzeinukiurikin(), !isNormalTax ? 0L : urikinSum);
            assertEquals(s07T001TEntity.getN10pershohitaxkin(), !isNormalTax ? 0L : shohitaxkinSum);
            assertEquals(s07T001TEntity.getTaxkeisankbn(),
                skysakikanrikbn.equals("2") ? "2" : taxkeisankbn.equals("X") ? "2" : taxkeisankbn);
            assertEquals(s07T001TEntity.getSkyicrnhyprntdd(), "00000000");
            assertEquals(s07T001TEntity.getSkyshoprntdd(), "00000000");
            assertNotNull(s07T001TEntity.getSkyshono());
            assertEquals(s07T001TEntity.getSkyshonoedano(), (short) 0);
            assertEquals(s07T001TEntity.getRiyomeisaishoprntdd(), "00000000");
            assertNotNull(s07T001TEntity.getRiyomeisaishono());
            assertEquals(s07T001TEntity.getTokusokujoprntdd(), "00000000");
            assertNotNull(s07T001TEntity.getTokusokujono());
            assertEquals(s07T001TEntity.getCsvdatchusyutsudd(), "00000000");
            assertEquals(s07T001TEntity.getRgrayuryo(), 0);
            assertEquals(s07T001TEntity.getRgramuryo(), 0);
            assertEquals(s07T001TEntity.getSrmyuryo(), 0);
            assertEquals(s07T001TEntity.getSrmmuryo(), 0);
            assertEquals(s07T001TEntity.getSonotayuryo(), 0);
            assertEquals(s07T001TEntity.getSonotamuryo(), 0);
            assertEquals(s07T001TEntity.getSeihinmizuuri(), 0);
            assertEquals(s07T001TEntity.getRtlrkn(), 0);
            assertEquals(s07T001TEntity.getSonotaurikin(), isUriNyukinNotExists ? 0L : 1230L);
            assertEquals(s07T001TEntity.getAqcrgrakynpnsuryo(), 0);
            assertEquals(s07T001TEntity.getAqcrgrasvsuryo(), 0);
            assertEquals(s07T001TEntity.getAqcrgrapuromoshonsuryo(), 0);
            assertEquals(s07T001TEntity.getAqcrgrasiinsuryo(), 0);
            assertEquals(s07T001TEntity.getAqcrgramntesuryo(), 0);
            assertEquals(s07T001TEntity.getAqcrgrakokansuryo(), 0);
            assertEquals(s07T001TEntity.getAqcrgraurisuryo(), 0);
            assertEquals(s07T001TEntity.getAqcsrmkynpnsuryo(), 0);
            assertEquals(s07T001TEntity.getAqcsrmsvsuryo(), 0);
            assertEquals(s07T001TEntity.getAqcsrmpuromoshonsuryo(), 0);
            assertEquals(s07T001TEntity.getAqcsrmsiinsuryo(), 0);
            assertEquals(s07T001TEntity.getAqcsrmmntesuryo(), 0);
            assertEquals(s07T001TEntity.getAqcsrmkokansuryo(), 0);
            assertEquals(s07T001TEntity.getAqcsrmurisuryo(), 0);
            assertEquals(s07T001TEntity.getEtcshnmzkynpnsuryo(), 0);
            assertEquals(s07T001TEntity.getEtcshnmzsvsuryo(), 0);
            assertEquals(s07T001TEntity.getEtcshnmzpuromoshonsuryo(), 0);
            assertEquals(s07T001TEntity.getEtcshnmzsiinsuryo(), 0);
            assertEquals(s07T001TEntity.getEtcshnmzmntesuryo(), 0);
            assertEquals(s07T001TEntity.getEtcshnmzkokansuryo(), 0);
            assertEquals(s07T001TEntity.getEtcshnmzurisuryo(), 0);
            assertEquals(s07T001TEntity.getShnmzrgrakin(), 0);
            assertEquals(s07T001TEntity.getShnmzsrmkin(), 0);
            assertEquals(s07T001TEntity.getShnmzsonotakin(), 0);
            assertEquals(s07T001TEntity.getRntlrwsvkin(), 0);
            assertEquals(s07T001TEntity.getRntlrhkshnkin(), 0);
            assertEquals(s07T001TEntity.getSntshnwsvkin(), 0);
            assertEquals(s07T001TEntity.getSntshnhkshnkin(), isUriNyukinNotExists ? 0L : 1230L);
            assertEquals(s07T001TEntity.getSntshnknphnkin(), 0);
            assertEquals(s07T001TEntity.getKaishuhohokbn(), kaishuhoho);
            assertEquals(s07T001TEntity.getShrytidydmdd(), "21000131");
            assertEquals(s07T001TEntity.getKsikkncd(), "0001");
            assertEquals(s07T001TEntity.getYukokbn(), "1");
            assertEquals(s07T001TEntity.getToshaapkihoncd(), APKIHONCD_TEST);
            assertEquals(s07T001TEntity.getToshashopcd(), "000");
            assertEquals(s07T001TEntity.getToshacd(), "00001");
            assertEquals(s07T001TEntity.getInptsystmymd(), systemDate);
            assertNotNull(s07T001TEntity.getInptsystmjikan());
            assertEquals(s07T001TEntity.getInptunyoymd(), "20200101");
            assertEquals(s07T001TEntity.getInptipadrse(), "0.0.0.1");
            assertEquals(s07T001TEntity.getInptpgmid(), PROGRAM_ID);
            assertEquals(s07T001TEntity.getKoshaapkihoncd(), APKIHONCD_TEST);
            assertEquals(s07T001TEntity.getKoshashopcd(), "000");
            assertEquals(s07T001TEntity.getKoshacd(), "00001");
            assertEquals(s07T001TEntity.getKshnsystmymd(), systemDate);
            assertNotNull(s07T001TEntity.getKshnsystmjikan());
            assertEquals(s07T001TEntity.getKshnunyoymd(), "20200101");
            assertEquals(s07T001TEntity.getKshnipadrse(), "0.0.0.1");
            assertEquals(s07T001TEntity.getKshnpgmid(), PROGRAM_ID);

            // 入金明細テーブルデータ検証
            // -----------------------------------------------------------------
            if (!isUriNyukinNotExists) {
                S10T001TEntityExample s10T001TExmp = new S10T001TEntityExample();
                s10T001TExmp.createCriteria().andApkihoncdEqualTo(param.getApkihoncd());
                S10T001TEntity s10T001TEntity = s10T001TDao.selectByExample(s10T001TExmp).get(0);
                assertEquals(s10T001TEntity.getSkyshimedydm(), "210001");
                assertEquals(s10T001TEntity.getSkyshimedd(), "31");
                assertEquals(s10T001TEntity.getSkyprcsjissidd(), "20200101");
                assertEquals(s10T001TEntity.getSkyprcsjissikbn(), (short) 1);
                assertEquals(s10T001TEntity.getKoshaapkihoncd(), APKIHONCD_TEST);
                assertEquals(s10T001TEntity.getKoshashopcd(), "000");
                assertEquals(s10T001TEntity.getKoshacd(), "00001");
                assertEquals(s10T001TEntity.getKshnsystmymd(), systemDate);
                assertNotNull(s10T001TEntity.getKshnsystmjikan());
                assertEquals(s10T001TEntity.getKshnunyoymd(), "20200101");
                assertEquals(s10T001TEntity.getKshnipadrse(), "0.0.0.1");
                assertEquals(s10T001TEntity.getKshnpgmid(), PROGRAM_ID);
            }

            // 請求先マスタテーブルデータ検証
            // -----------------------------------------------------------------
            S02M017TEntityExample s02M017TExmp = new S02M017TEntityExample();
            s02M017TExmp.createCriteria().andApkihoncdEqualTo(param.getApkihoncd());
            S02M017TEntity s07M017TEntity = s02M017TDao.selectByExample(s02M017TExmp).get(0);
            if (keshikomizdTmp < 0) {
                assertEquals(s07M017TEntity.getUrichoseikin(), Long.parseLong(test.get(9)));
            } else {
                assertEquals(s07M017TEntity.getUrichoseikin(), Long.parseLong("0"));
            }
            assertEquals(s07M017TEntity.getKoshaapkihoncd(), APKIHONCD_TEST);
            assertEquals(s07M017TEntity.getKoshashopcd(), "000");
            assertEquals(s07M017TEntity.getKoshacd(), "00001");
            assertEquals(s07M017TEntity.getKshnsystmymd(), systemDate);
            assertNotNull(s07M017TEntity.getKshnsystmjikan());
            assertEquals(s07M017TEntity.getKshnunyoymd(), "20200101");
            assertEquals(s07M017TEntity.getKshnipadrse(), "0.0.0.1");
            assertEquals(s07M017TEntity.getKshnpgmid(), PROGRAM_ID);
        }
    }

    /**
     * 請求締処理（異常系テスト）
     */
    @Test
    @Rollback
    void testExecuteSkyShimeAbnormal() {
        // 処理状態リスト定義
        List<String> prcsjotaiList = new ArrayList<String>();
        prcsjotaiList.add("0"); // 「終了／待機」
        prcsjotaiList.add("1"); // 「処理開始」
        prcsjotaiList.add("2"); // 「実行中」
        prcsjotaiList.add("6"); // 「リカバリ実行待」
        prcsjotaiList.add("6"); // 「リカバリ実行待」
        prcsjotaiList.add("7"); // 「リカバリ実行中」
        prcsjotaiList.add("9"); // 「エラー」
        prcsjotaiList.add(""); // データなし
        prcsjotaiList.add("1"); // 「処理開始」
        prcsjotaiList.add("1"); // 「処理開始」
        prcsjotaiList.add("1"); // 「処理開始」

        // テスト結果メッセージ定義
        List<String> messageList = new ArrayList<String>();
        messageList.add("実行可能な状態ではないため、処理を続行できません。");
        messageList.add("実行可能な状態ではないか、他の締め処理実行中のため、処理を続行できません。");
        messageList.add("既に実行中のため、処理を続行できません。");
        messageList.add("リカバリ実行の開始ポイントが見つかりません。");
        messageList.add("リカバリ実行の開始ポイントが複数見つかりました。");
        messageList.add("現在リカバリ実行中のため、処理を続行できません。");
        messageList.add("エラーで終了しています。エラーの原因を解消しリカバリ実行を行うか、データを戻して再度実行してください。");
        messageList.add("請求締処理の管理情報が存在しないため、処理を続行できません。");
        messageList.add("インボイスに関連する売上が訂正されています。システム部に連絡してください。");
        messageList.add("消込済のため締解除はできません。");
        messageList.add("請求締め処理でエラーが発生しました。");

        // パラメータDto生成
        D1501W11SearchConditionDto param = getExecuteTestDto("1");
        // テストデータ生成
        createTestData();
        // テスト実行
        int index = 0;
        for (String prcsjotai : prcsjotaiList) {
            try {
                // 検証用項目値設定
                String prcskbn = index == 1 ? "2" : param.getPrcskbn();
                String systemDate = param.getDbManagementSession().getSystemDate();
                String systemTime = param.getDbManagementSession().getSystemTime();

                // テストデータ削除
                deleteS07W006T(); // 請求締用対象得意先ワーク
                // テストデータ生成
                switch (index) {
                    case 4: // エラーメッセージ：リカバリ実行の開始ポイントが複数見つかりました。
                        insertS07T005T("1", "6"); // 請求締処理詳細管理テーブル
                        insertS07T005T("2", "6"); // 請求締処理詳細管理テーブル
                        insertS07T004T(prcskbn, prcsjotai); // 請求締処理管理テーブル
                        break;

                    case 0: // エラーメッセージ：実行可能な状態ではないため、処理を続行できません。
                    case 1: // エラーメッセージ：実行可能な状態ではないか、他の締め処理実行中ため、処理を続行できません。
                    case 2: // エラーメッセージ：既に実行中のため、処理を続行できません。
                    case 3: // エラーメッセージ：リカバリ実行の開始ポイントが見つかりません。
                    case 5: // エラーメッセージ：現在リカバリ実行中のため、処理を続行できません。
                    case 6: // エラーメッセージ：エラーで終了しています。エラーの原因を解消しリカバリ実行を行うか、データを戻して再度実行してください。
                        insertS07T004T(prcskbn, prcsjotai); // 請求締処理管理テーブル
                        break;

                    case 7: // エラーメッセージ：請求締処理の管理情報が存在しないため、処理を続行できません。
                        deleteS07T004T(); // 請求締処理管理テーブル
                        break;

                    case 10: // エラーメッセージ：インボイスに関連する売上が訂正されています。システム部に連絡してください。
                    case 8:
                        insertS07T004T(prcskbn, prcsjotai); // 請求締処理管理テーブル
                        insertS07W003T(systemDate, systemTime, "1"); // 請求締ワーク
                        if (index == 10) {
                            insertS02M017T("7"); // 請求先マスタ
                        }
                        break;

                    case 9: // エラーメッセージ：消込済のため締解除はできません。
                        insertS07T004T(prcskbn, prcsjotai); // 請求締処理管理テーブル
                        insertS07W003T(systemDate, systemTime, "2"); // 請求締ワーク
                        break;

                    default:
                        break;
                }

                // 請求締処理
                this.executeSkyShime(param, param.getSkyshimedate().replaceAll("/", ""));

            } catch (Exception e) {
                log.debug("■■■ エラーメッセージ ■■■:" + e.getMessage());
                if (index > 7) {
                    // 例外メッセージと比較
                    assertTrue(e.getMessage().contains("請求締め処理でエラーが発生しました。"));

                    // 請求締処理管理テーブルデータと比較
                    S07T004TEntityKey s07T004TKey = new S07T004TEntityKey();
                    s07T004TKey.setApkihoncd(APKIHONCD_TEST);
                    s07T004TKey.setShopcd("000");
                    S07T004TEntity s07T004TEntity = s07T004TDao.selectByPrimaryKey(s07T004TKey);
                    log.debug("☆☆☆ 想定メッセージ ☆☆☆:" + messageList.get(index));
                    log.debug("★★★ 結果メッセージ ★★★:" + s07T004TEntity.getMsg());
                    assertTrue(s07T004TEntity.getMsg().contains("請求締め処理でエラーが発生しました。"));
                    assertEquals(s07T004TEntity.getPrcsjotai(), "9");

                    // 請求締処理詳細管理テーブルデータと比較
                    S07T005TEntityKey s07T005TKey = new S07T005TEntityKey();
                    s07T005TKey.setApkihoncd(APKIHONCD_TEST);
                    s07T005TKey.setShopcd("000");
                    s07T005TKey.setPrcsid(index == 8 ? "D1502040" : "D1502100");
                    S07T005TEntity s07T005TEntity = s07T005TDao.selectByPrimaryKey(s07T005TKey);
                    log.debug("☆☆☆ 想定メッセージ ☆☆☆:" + messageList.get(index));
                    log.debug("★★★ 結果メッセージ ★★★:" + s07T005TEntity.getMsg());

                    if (index == 10) {
                        assertNull(s07T005TEntity.getMsg());
                    } else {
                        assertTrue(s07T005TEntity.getMsg().contains(messageList.get(index)));
                        assertEquals(s07T005TEntity.getPrcsjotai(), "9");
                    }
                } else {
                    // 例外メッセージと比較
                    log.debug("☆☆☆ 想定メッセージ ☆☆☆:" + messageList.get(index));
                    log.debug("★★★ 結果メッセージ ★★★:" + e.getMessage());
                    assertTrue(e.getMessage().contains(messageList.get(index)));
                }
            }
            index++;
        }
    }

    /**
     * 請求締解除処理（正常系テスト）
     */
    @Test
    @Rollback
    void testExecuteSkyKaijoNormal() {
        // テスト条件リスト定義（売上入金区分、データ作成元区分、消込残高（請求残高テーブルに2レコード設定したい場合4つ目の要素を設定する)）
        List<List<String>> testList = new ArrayList<List<String>>();
        testList.add(new ArrayList<String>(Arrays.asList("0", "23", "0"))); // 売上、請求消費税算出
        testList.add(new ArrayList<String>(Arrays.asList("0", "01", "0"))); // 売上、受注登録
        testList.add(new ArrayList<String>(Arrays.asList("1", "01", "0"))); // 入金、受注登録
        testList.add(new ArrayList<String>(Arrays.asList("0", "23", "-10000", "1"))); // 売上、請求消費税算出

        // パラメータDto生成
        D1501W11SearchConditionDto param = getExecuteTestDto("2");
        // テスト実行
        for (List<String> test : testList) {
            final String salesNyukinType = test.get(0);
            final String datsksmotokbn = test.get(1);
            final String systemDate = param.getDbManagementSession().getSystemDate();
            final String systemTime = param.getDbManagementSession().getSystemTime();
            final boolean isCalcTax = datsksmotokbn.equals("23");

            // テストデータ生成
            createTestData();
            insertS06T001T(datsksmotokbn); // 売上テーブル
            // 請求残高テーブル
            if (test.size() == 4) {
                insertS07T001T("31", "1", "0", test.get(2), test.get(3));
            } else {
                insertS07T001T("31", "1", "0", test.get(2));
            }
            insertS07T001T("31", "1", "0", "0", "1", "209901", "1", "000002");
            insertS07W003T(systemDate, systemTime, param.getPrcskbn()); // 請求締ワーク
            insertS07T004T(param.getPrcskbn(), "1"); // 請求締処理管理テーブル
            insertS07T006T(salesNyukinType); // 請求明細関連テーブル
            insertS10T001T(salesNyukinType.equals("1") ? "X0000" : null); // 入金明細テーブル

            // 請求締処理
            log.debug("■■■■■■■■■■ " + test.toString() + " ■■■■■■■■■■");
            this.executeSkyShime(param, param.getSkyshimedate().replaceAll("/", ""));

            // 請求締処理管理テーブルデータ検証
            // -----------------------------------------------------------------
            S07T004TEntityKey s07T004TEntityKey = new S07T004TEntityKey();
            s07T004TEntityKey.setApkihoncd(param.getApkihoncd());
            s07T004TEntityKey.setShopcd(param.getShopcd());
            S07T004TEntity s07T004TEntity = s07T004TDao.selectByPrimaryKey(s07T004TEntityKey);
            assertNotNull(s07T004TEntity.getPrcsstrtdd());
            assertNotNull(s07T004TEntity.getPrcsstrtjikan());
            assertNotNull(s07T004TEntity.getPrcsshrydd());
            assertNotNull(s07T004TEntity.getPrcsstrtjikan());
            assertNull(s07T004TEntity.getRecovstrtdd());
            assertNull(s07T004TEntity.getRecovstrtjikan());
            assertNull(s07T004TEntity.getRecovshrydd());
            assertNull(s07T004TEntity.getRecovshryjikan());
            assertEquals(s07T004TEntity.getPrcsjotai(), "0");
            assertEquals(s07T004TEntity.getKoshaapkihoncd(), APKIHONCD_TEST);
            assertEquals(s07T004TEntity.getKoshashopcd(), "000");
            assertEquals(s07T004TEntity.getKoshacd(), "00001");
            assertEquals(s07T004TEntity.getKshnsystmymd(), systemDate);
            assertNotNull(s07T004TEntity.getKshnsystmjikan());
            assertEquals(s07T004TEntity.getKshnunyoymd(), "20200101");
            assertEquals(s07T004TEntity.getKshnipadrse(), "0.0.0.1");
            assertEquals(s07T004TEntity.getKshnpgmid(), PROGRAM_ID);

            // 得意先実績テーブルデータ検証
            // -----------------------------------------------------------------
            S02M050TEntityKey s02M050TEntityKey = new S02M050TEntityKey();
            s02M050TEntityKey.setApkihoncd(param.getApkihoncd());
            s02M050TEntityKey.setShopcd(param.getShopcd());
            s02M050TEntityKey.setTokuisakicd("1");
            S02M050TEntity s02M050TEntity = s02M050TDao.selectByPrimaryKey(s02M050TEntityKey);
            assertEquals(s02M050TEntity.getGenzaizd(), isCalcTax ? -1000 : 0);
            if (isCalcTax) {
                assertEquals(s02M050TEntity.getKoshaapkihoncd(), APKIHONCD_TEST);
                assertEquals(s02M050TEntity.getKoshashopcd(), "000");
                assertEquals(s02M050TEntity.getKoshacd(), "00001");
                assertEquals(s02M050TEntity.getKshnsystmymd(), systemDate);
                assertNotNull(s02M050TEntity.getKshnsystmjikan());
                assertEquals(s02M050TEntity.getKshnunyoymd(), "20200101");
                assertEquals(s02M050TEntity.getKshnipadrse(), "0.0.0.1");
                assertEquals(s02M050TEntity.getKshnpgmid(), PROGRAM_ID);
            }

            // 請求締用対象得意先ワークデータ検証
            // -----------------------------------------------------------------
            S07W006TEntityExample s07W006TExmp = new S07W006TEntityExample();
            s07W006TExmp.createCriteria().andApkihoncdEqualTo(param.getApkihoncd());
            S07W006TEntity s07W006TEntity = s07W006TDao.selectByExample(s07W006TExmp).get(0);
            assertEquals(s07W006TEntity.getShopcd(), param.getShopcd());
            assertEquals(s07W006TEntity.getSkysakicd(), "1");
            assertEquals(s07W006TEntity.getTokuisakicd(), "1");
            assertEquals(s07W006TEntity.getZnkshimedd(), "20990131");
            assertNull(s07W006TEntity.getShimesumiflg());
            assertEquals(s07W006TEntity.getKurikoshikin(), 1000L);
            assertEquals(s07W006TEntity.getShohitaxkin(), isCalcTax ? -1000L : 0);
            assertEquals(s07W006TEntity.getToshaapkihoncd(), APKIHONCD_TEST);
            assertEquals(s07W006TEntity.getToshashopcd(), "000");
            assertEquals(s07W006TEntity.getToshacd(), "00001");
            assertNotNull(s07W006TEntity.getInptsystmymd());
            assertNotNull(s07W006TEntity.getInptsystmjikan());
            assertEquals(s07W006TEntity.getInptunyoymd(), "20200101");
            assertEquals(s07W006TEntity.getInptipadrse(), "0.0.0.1");
            assertEquals(s07W006TEntity.getInptpgmid(), PROGRAM_ID);
            assertEquals(s07W006TEntity.getKoshaapkihoncd(), APKIHONCD_TEST);
            assertEquals(s07W006TEntity.getKoshashopcd(), "000");
            assertEquals(s07W006TEntity.getKoshacd(), "00001");
            assertEquals(s07W006TEntity.getKshnsystmymd(), systemDate);
            assertNotNull(s07W006TEntity.getKshnsystmjikan());
            assertEquals(s07W006TEntity.getKshnunyoymd(), "20200101");
            assertEquals(s07W006TEntity.getKshnipadrse(), "0.0.0.1");
            assertEquals(s07W006TEntity.getKshnpgmid(), PROGRAM_ID);

            // 請求残高テーブルデータ検証
            // -----------------------------------------------------------------
            S07T001TEntityExample s07T001TExmp = new S07T001TEntityExample();
            s07T001TExmp.createCriteria().andApkihoncdEqualTo(param.getApkihoncd());
            S07T001TEntity s07T001TEntity = s07T001TDao.selectByExample(s07T001TExmp).get(0);
            assertEquals(s07T001TEntity.getYukokbn(), "0");
            assertEquals(s07T001TEntity.getKoshaapkihoncd(), APKIHONCD_TEST);
            assertEquals(s07T001TEntity.getKoshashopcd(), "000");
            assertEquals(s07T001TEntity.getKoshacd(), "00001");
            assertEquals(s07T001TEntity.getKshnsystmymd(), systemDate);
            assertNotNull(s07T001TEntity.getKshnsystmjikan());
            assertEquals(s07T001TEntity.getKshnunyoymd(), "20200101");
            assertEquals(s07T001TEntity.getKshnipadrse(), "0.0.0.1");
            assertEquals(s07T001TEntity.getKshnpgmid(), PROGRAM_ID);

            String newUridnpyno = "";
            if (salesNyukinType.equals("1")) {
                // 入金明細テーブルデータ検証
                // -----------------------------------------------------------------
                S10T001TEntityExample s10T001TExmp = new S10T001TEntityExample();
                s10T001TExmp.createCriteria().andApkihoncdEqualTo(param.getApkihoncd());
                S10T001TEntity s10T001TEntity = s10T001TDao.selectByExample(s10T001TExmp).get(0);
                assertNull(s10T001TEntity.getSkyshimedydm());
                assertNull(s10T001TEntity.getSkyshimedd());
                assertEquals(s10T001TEntity.getSkyprcsjissidd(), "00000000");
                assertEquals(s10T001TEntity.getSkyprcsjissikbn(), (short) 0);
                assertEquals(s10T001TEntity.getKoshaapkihoncd(), APKIHONCD_TEST);
                assertEquals(s10T001TEntity.getKoshashopcd(), "000");
                assertEquals(s10T001TEntity.getKoshacd(), "00001");
                assertEquals(s10T001TEntity.getKshnsystmymd(), systemDate);
                assertNotNull(s10T001TEntity.getKshnsystmjikan());
                assertEquals(s10T001TEntity.getKshnunyoymd(), "20200101");
                assertEquals(s10T001TEntity.getKshnipadrse(), "0.0.0.1");
                assertEquals(s10T001TEntity.getKshnpgmid(), PROGRAM_ID);
            } else {
                // 売上テーブルデータ検証
                // -----------------------------------------------------------------
                S06T001TEntityExample s06T001TExmp = new S06T001TEntityExample();
                s06T001TExmp.createCriteria().andApkihoncdEqualTo(param.getApkihoncd());
                s06T001TExmp.setOrderByClause("URIDNPYNO DESC");
                List<S06T001TEntity> s06T001TEntityList = s06T001TDao.selectByExample(s06T001TExmp);
                assertEquals(s06T001TEntityList.size(), isCalcTax ? 2 : 1);
                int indexS06T001T = 1;
                for (S06T001TEntity entity : s06T001TEntityList) {
                    if (indexS06T001T == 2) {
                        newUridnpyno = entity.getUridnpyno();
                        assertEquals(entity.getShopcd(), param.getShopcd());
                        assertNotNull(entity.getUridnpyno());
                        assertNotNull(entity.getEdano());
                        assertEquals(entity.getAcptdnpyno(), "Z0000");
                        assertEquals(entity.getAcptdnpyedano(), Short.parseShort("1"));
                        assertEquals(entity.getTorihikikbn(), "2");
                        assertEquals(entity.getTokuisakicd(), "1");
                        assertEquals(entity.getSiiresakicd(), "A");
                        assertEquals(entity.getNyukamotocd(), "B");
                        assertEquals(entity.getUridd(), "20200131");
                        assertEquals(entity.getAcptdd(), "20210101");
                        assertEquals(entity.getUrikin(), 1230L);
                        assertEquals(entity.getShohitaxkin(), -123L);
                        assertEquals(entity.getSankoushohitaxkin(), 1240L);
                        assertEquals(entity.getAcpttnshacd(), "C");
                        assertEquals(entity.getDlvrytnshacd(), "D");
                        assertEquals(entity.getDlvrykankatsuapkihoncd(), "E");
                        assertEquals(entity.getDlvrykankatsushopcd(), "F");
                        assertEquals(entity.getDlvrykyotencd(), "G");
                        assertEquals(entity.getDlvrykbn(), "3");
                        assertEquals(entity.getDlvrycmpnycd(), "H");
                        assertEquals(entity.getShukkamotokbn(), "4");
                        assertEquals(entity.getEigyotnshacd(), "I");
                        assertEquals(entity.getJissekitnshacd(), "J");
                        assertEquals(entity.getSkysakicd(), "1");
                        assertEquals(entity.getSkyshimedydm(), "210002");
                        assertEquals(entity.getSkyshimedd(), "10");
                        assertEquals(entity.getSkyprcsjissidd(), "00000000");
                        assertNull(entity.getGetsujishimedydm());
                        assertNull(entity.getGetsujishimedd());
                        assertEquals(entity.getGetsujiprcsjissidd(), "00000000");
                        assertEquals(entity.getUrishimeprcsdd(), "20200131");
                        assertEquals(entity.getNebikishimedydm(), "209912");
                        assertEquals(entity.getNebikishimedd(), "11");
                        assertEquals(entity.getNebikiprcsjissidd(), "20991201");
                        assertEquals(entity.getDnpybiko(), "XXXXX");
                        assertEquals(entity.getDnpyno(), "X0000");
                        assertEquals(entity.getSkyshootptarinashikbn(), "0");
                        assertEquals(entity.getDatsksmotokbn(), test.get(1));
                        assertEquals(entity.getTaxkeisankbn(), "1");
                        assertEquals(entity.getSiirednpyno(), "B000");
                        assertEquals(entity.getNykndnpyno(), "C000");
                        assertEquals(entity.getNyknkin(), Long.parseLong("1250"));
                        assertEquals(entity.getNyknkind(), "1");
                        assertEquals(entity.getYukokbn(), "1");
                        assertEquals(entity.getToshaapkihoncd(), APKIHONCD_TEST);
                        assertEquals(entity.getToshashopcd(), "000");
                        assertEquals(entity.getToshacd(), "00001");
                        assertEquals(entity.getInptsystmymd(), systemDate);
                        assertNotNull(entity.getInptsystmjikan());
                        assertEquals(entity.getInptunyoymd(), "20200101");
                        assertEquals(entity.getInptipadrse(), "0.0.0.1");
                        assertEquals(entity.getInptpgmid(), PROGRAM_ID);
                        assertEquals(entity.getSkyprcsjissikbn(), (short) 1);
                    } else if (isCalcTax) {
                        assertNotNull(entity.getDnpyno());
                        assertEquals(entity.getSkyprcsjissikbn(), (short) 1);
                    } else {
                        assertNull(entity.getSkyshimedydm());
                        assertNull(entity.getSkyshimedd());
                        assertEquals(entity.getSkyprcsjissidd(), "00000000");
                        assertEquals(entity.getSkyprcsjissikbn(), (short) 0);
                    }
                    assertEquals(entity.getKoshaapkihoncd(), APKIHONCD_TEST);
                    assertEquals(entity.getKoshashopcd(), "000");
                    assertEquals(entity.getKoshacd(), "00001");
                    assertEquals(entity.getKshnsystmymd(), systemDate);
                    assertNotNull(entity.getKshnsystmjikan());
                    assertEquals(entity.getKshnunyoymd(), "20200101");
                    assertEquals(entity.getKshnipadrse(), "0.0.0.1");
                    assertEquals(entity.getKshnpgmid(), PROGRAM_ID);
                    indexS06T001T++;
                }

                // 売上明細テーブルデータ検証
                // -----------------------------------------------------------------
                if (isCalcTax) {
                    S06T002TEntityExample exmp = new S06T002TEntityExample();
                    exmp.createCriteria().andApkihoncdEqualTo(param.getApkihoncd());
                    exmp.setOrderByClause("URIDNPYNO DESC");
                    List<S06T002TEntity> s06T002TEntityList = s06T002TDao.selectByExample(exmp);
                    assertEquals(s06T001TEntityList.size(), 2);
                    int indexS06T002T = 1;
                    for (S06T002TEntity entity : s06T002TEntityList) {
                        if (indexS06T002T == 2) {
                            assertEquals(entity.getShopcd(), param.getShopcd());
                            assertEquals(entity.getUridnpyno(), newUridnpyno);
                            assertEquals(entity.getEdano(), (short) 0);
                            assertEquals(entity.getUridnpymeisaino(), 1);
                            assertEquals(entity.getUridnpymeisaiedano(), (short) 0);
                            assertEquals(entity.getLctgrycd(), "99");
                            assertEquals(entity.getMctgrycd(), "001");
                            assertEquals(entity.getSctgrycd(), "001");
                            assertEquals(entity.getShncd(), "000000");
                            assertEquals(entity.getKeijokbn(), "01");
                            assertEquals(entity.getSuryo(), BigDecimal.valueOf(-2L));
                            assertEquals(entity.getHyojununt(), BigDecimal.valueOf(2000L));
                            assertEquals(entity.getUnt(), BigDecimal.valueOf(2001L));
                            assertEquals(entity.getKin(), 1230L);
                            assertEquals(entity.getUntshohitaxkin(), 123L);
                            assertEquals(entity.getShohitaxkin(), -1000L);
                            assertEquals(entity.getSankoushohitaxkin(), 124L);
                            assertEquals(entity.getTaxkbn(), "1");
                            assertEquals(entity.getMeisaikbn(), "0");
                            assertEquals(entity.getNyuryokumeisaiflg(), "1");
                            assertEquals(entity.getTanpinmeisaiflg(), "1");
                            assertEquals(entity.getStshncd(), "100000");
                            assertEquals(entity.getKatasiki(), "1");
                            assertEquals(entity.getSiiresuryo(), BigDecimal.valueOf(3L));
                            assertEquals(entity.getSiirekin(), 0L);
                            assertEquals(entity.getSiireunt(), BigDecimal.valueOf(2002L));
                            assertEquals(entity.getSiireshohitaxkin(), 120L);
                            assertEquals(entity.getZaikokshnsumiflg(), "1");
                            assertEquals(entity.getTokuisakikbn(), "A");
                            assertEquals(entity.getJutakudlvrycstkbn(), "B");
                            assertEquals(entity.getRoyaltyshimeymd(), "00000000");
                            assertEquals(entity.getYukokbn(), "1");
                            assertEquals(entity.getToshaapkihoncd(), APKIHONCD_TEST);
                            assertEquals(entity.getToshashopcd(), "000");
                            assertEquals(entity.getToshacd(), "00001");
                            assertEquals(entity.getInptsystmymd(), systemDate);
                            assertNotNull(entity.getInptsystmjikan());
                            assertEquals(entity.getInptunyoymd(), "20200101");
                            assertEquals(entity.getInptipadrse(), "0.0.0.1");
                            assertEquals(entity.getInptpgmid(), PROGRAM_ID);
                        }
                        assertEquals(entity.getMeisaibiko(), "締解除取消");
                        assertEquals(entity.getKoshaapkihoncd(), APKIHONCD_TEST);
                        assertEquals(entity.getKoshashopcd(), "000");
                        assertEquals(entity.getKoshacd(), "00001");
                        assertEquals(entity.getKshnsystmymd(), systemDate);
                        assertNotNull(entity.getKshnsystmjikan());
                        assertEquals(entity.getKshnunyoymd(), "20200101");
                        assertEquals(entity.getKshnipadrse(), "0.0.0.1");
                        assertEquals(entity.getKshnpgmid(), PROGRAM_ID);
                        indexS06T002T++;
                    }
                }
            }
            // 請求先マスタテーブルデータ検証
            // -----------------------------------------------------------------
            S02M017TEntityExample s02M017TExmp = new S02M017TEntityExample();
            s02M017TExmp.createCriteria().andApkihoncdEqualTo(param.getApkihoncd());
            S02M017TEntity s07M017TEntity = s02M017TDao.selectByExample(s02M017TExmp).get(0);
            if (Long.parseLong(test.get(2)) < 0) {
                assertEquals(s07M017TEntity.getUrichoseikin(), Long.parseLong(test.get(2)));

            } else {
                assertEquals(s07M017TEntity.getUrichoseikin(), Long.parseLong("0"));
            }
            assertEquals(s07M017TEntity.getKoshaapkihoncd(), APKIHONCD_TEST);
            assertEquals(s07M017TEntity.getKoshashopcd(), "000");
            assertEquals(s07M017TEntity.getKoshacd(), "00001");
            assertEquals(s07M017TEntity.getKshnsystmymd(), systemDate);
            assertNotNull(s07M017TEntity.getKshnsystmjikan());
            assertEquals(s07M017TEntity.getKshnunyoymd(), "20200101");
            assertEquals(s07M017TEntity.getKshnipadrse(), "0.0.0.1");
            assertEquals(s07M017TEntity.getKshnpgmid(), PROGRAM_ID);
        }
    }

    /**
     * 請求締解除処理（異常系テスト）
     */
    @Test
    void testExecuteSkyKaijoAbnormal() {
        // 処理状態リスト定義
        List<String> prcsjotaiList = new ArrayList<String>();
        prcsjotaiList.add("0"); // 「終了／待機」
        prcsjotaiList.add("1"); // 「処理開始」
        prcsjotaiList.add("2"); // 「実行中」
        prcsjotaiList.add("6"); // 「リカバリ実行待」
        prcsjotaiList.add("6"); // 「リカバリ実行待」
        prcsjotaiList.add("7"); // 「リカバリ実行中」
        prcsjotaiList.add("9"); // 「エラー」
        prcsjotaiList.add(""); // データなし
        prcsjotaiList.add("1"); // 「処理開始」

        // テスト結果メッセージ定義
        List<String> messageList = new ArrayList<String>();
        messageList.add("実行可能な状態ではないため、処理を続行できません。");
        messageList.add("実行可能な状態ではないか、他の締め処理実行中ため、処理を続行できません。");
        messageList.add("既に実行中のため、処理を続行できません。");
        messageList.add("リカバリ実行の開始ポイントが見つかりません。");
        messageList.add("リカバリ実行の開始ポイントが複数見つかりました。");
        messageList.add("現在リカバリ実行中のため、処理を続行できません。");
        messageList.add("エラーで終了しています。エラーの原因を解消しリカバリ実行を行うか、データを戻して再度実行してください。");
        messageList.add("請求締処理の管理情報が存在しないため、処理を続行できません。");
        messageList.add("消込済のため締解除はできません。");

        // テストデータ生成
        createTestData();
        // テスト実行
        int index = 0;
        for (String prcsjotai : prcsjotaiList) {
            try {
                // パラメータDto生成
                D1501W11SearchConditionDto param = getExecuteTestDto("2");
                String systemDate = param.getDbManagementSession().getSystemDate();
                String systemTime = param.getDbManagementSession().getSystemTime();

                // テストデータ生成・削除
                switch (index) {
                    case 3: // エラーメッセージ：リカバリ実行の開始ポイントが見つかりません。
                        deleteS07T005T(null); // 請求締処理詳細管理テーブル
                        insertS07T004T("2", prcsjotai); // 請求締処理管理テーブル
                        break;

                    case 4: // エラーメッセージ：リカバリ実行の開始ポイントが複数見つかりました。
                        insertS07T005T("1", "6"); // 請求締処理詳細管理テーブル
                        insertS07T005T("2", "6"); // 請求締処理詳細管理テーブル
                        insertS07T004T("2", prcsjotai); // 請求締処理管理テーブル
                        break;

                    case 0: // エラーメッセージ：実行可能な状態ではないため、処理を続行できません。
                    case 1: // エラーメッセージ：実行可能な状態ではないか、他の締め処理実行中ため、処理を続行できません。
                    case 2: // エラーメッセージ：既に実行中のため、処理を続行できません。
                    case 5: // エラーメッセージ：現在リカバリ実行中のため、処理を続行できません。
                    case 6: // エラーメッセージ：エラーで終了しています。エラーの原因を解消しリカバリ実行を行うか、データを戻して再度実行してください。
                        insertS07T004T("2", prcsjotai); // 請求締処理管理テーブル
                        break;

                    case 7: // エラーメッセージ：請求締処理の管理情報が存在しないため、処理を続行できません。
                        deleteS07T004T(); // 請求締処理管理テーブル
                        break;

                    case 8: // エラーメッセージ：消込済のため締解除はできません。
                        insertS07T004T("2", prcsjotai); // 請求締処理管理テーブル
                        insertS07W003T(systemDate, systemTime, "2"); // 請求締ワーク
                        break;

                    default:
                        break;
                }

                // 請求締処理
                this.executeSkyShime(param, param.getSkyshimedate().replaceAll("/", ""));

            } catch (Exception e) {
                // 例外メッセージと比較
                log.debug("☆☆☆ 想定メッセージ ☆☆☆:" + messageList.get(index));
                log.debug("★★★ 結果メッセージ ★★★:" + e.getMessage());
                assertTrue(e.getMessage().contains(messageList.get(index)));

                // 請求締処理管理テーブルデータ値と比較
                if (index != 7) {
                    S07T004TEntityKey key = new S07T004TEntityKey();
                    key.setApkihoncd(APKIHONCD_TEST);
                    key.setShopcd("000");
                    S07T004TEntity entity = s07T004TDao.selectByPrimaryKey(key);
                    log.debug("☆☆☆ 想定メッセージ ☆☆☆:" + messageList.get(index));
                    log.debug("★★★ 結果メッセージ ★★★:" + entity.getMsg());
                    assertTrue(entity.getMsg().contains(messageList.get(index)));
                    assertEquals(entity.getPrcsjotai(), "9");
                }
            }
            index++;
        }
    }

    /**
     * テストデータ生成
     */
    private void createTestData() {
        // テストデータ削除
        clearTestData();
        // テストデータ生成
        insertS02M006T("1", "20200101"); // システム管理マスタ
        insertS02M013T(null); // 得意先マスタ
        insertS02M017T("1"); // 請求先マスタ
        insertS02M046T("20200101", "20200101", "1", "1"); // ショップマスタ
        insertS02M050T(); // 得意先実績テーブル
        insertS06T001T(null, null); // 売上テーブル
        insertS06T002T(null, null); // 売上明細テーブル
        insertS07T001T(null, "1", null, "0"); // 請求残高テーブル
        insertS10T001T(null); // 入金明細テーブル
    }

    /**
     * システム管理マスタデータ生成（運用日付）
     * 
     * @param setteiCd 設定コード
     * @param unyoDate 運用日付
     */
    private void insertS02M006T(String setteiCd, String unyoDate) {
        // データ削除
        deleteS02M006T(setteiCd);
        // データ生成
        S02M006TEntity entity = new S02M006TEntity();
        entity.setApkihoncd(APKIHONCD_TEST);
        entity.setShopcd("000");
        entity.setSetteino("038");
        entity.setRcrdkbn("M");
        entity.setYukokbn("1");
        entity.setSetteicd(setteiCd);
        entity.setMojisetteiatai(unyoDate);
        s02M006TDao.insert(entity);
    }

    /**
     * 得意先マスタデータ生成
     * 
     * @param jotaists 状態ステータス
     */
    private void insertS02M013T(String jotaists) {
        // データ削除
        deleteS02M013T();
        // データ生成
        S02M013TEntity entity = new S02M013TEntity();
        entity.setApkihoncd(APKIHONCD_TEST);
        entity.setShopcd("000");
        entity.setTokuisakicd("1");
        entity.setTokuisakijotai("1");
        entity.setJotaists(jotaists == null ? "07" : jotaists);
        entity.setTokuisakikbn("1");
        entity.setRiyowsvkbn("1");
        entity.setCdkindkbn("1");
        entity.setGyoshukbn("00");
        entity.setSkysakicd("1");
        entity.setJigyoshocd("00");
        entity.setTokuisakinmn1("得意先名称１");
        entity.setTokuisakikeishokbn("1");
        entity.setYubinno("1111111");
        entity.setAdrsn1("北海道");
        entity.setRenrakusakinon1("00000000000");
        entity.setClckensakuyotelnon1("00000000000");
        entity.setMerumagakbn("1");
        entity.setMoshikomircptkbn("1");
        entity.setTokubetsuuntkbn("1");
        entity.setRknkindtokubetsunebikiflg("1");
        entity.setRknkindtokubetsuuntflg("1");
        entity.setRknkindkihonflg("1");
        entity.setUrikeijotnshakbn("1");
        entity.setMgntrgtkbn("1");
        entity.setRtlskykanrikbn("1");
        entity.setDlvrykbn("1");
        entity.setFzaiokikanoflg("1");
        entity.setAcptrcptkbn("1");
        entity.setAcptteishiflg("0");
        entity.setHoshokinkbn("1");
        entity.setKeizokurnk("S");
        entity.setShohirnk("S");
        entity.setKyuminkbn("0");
        entity.setMtmkisvkbn("1");
        entity.setMuryobtlsvkbn("1");
        entity.setMyaqauntjohodsplyumuflg("1");
        entity.setMyaqaskyjohodsplyumuflg("1");
        entity.setBabyaqariyoflg("1");
        entity.setYukokbn("1");
        entity.setTokuisakikbn("A");
        entity.setJutakudlvrycstkbn("B");
        s02M013TDao.insert(entity);
    }

    /**
     * 請求先マスタデータ生成
     * 
     * @param params パラメータリスト（0:回収方法区分、1:請求先管理区分、2:滞留区分、3:請求区分）
     */
    private void insertS02M017T(String... params) {
        // データ削除
        deleteS02M017T();
        // データ生成
        S02M017TEntity entity = new S02M017TEntity();
        entity.setApkihoncd(APKIHONCD_TEST);
        entity.setShopcd("000");
        entity.setSkysakicd("1");
        entity.setJigyoshocd("00");
        entity.setSkyshohassouitakuumu("1");
        entity.setSkysakikanrikbn(params.length > 1 ? params[1] : "1");
        entity.setSkykbn(params.length > 3 ? params[3] : "1");
        entity.setSkysakinmn1("請求先名称１");
        entity.setSkysakikeishokbn("1");
        entity.setSkysakinmkana("ｾｲｷｭｳｻｷﾒｲｼｮｳｶﾅ");
        entity.setSkysakinmryakusho("請求先名称");
        entity.setYubinno("1111111");
        entity.setAdrsn1("北海道");
        entity.setRenrakusakinokbnn1("1");
        entity.setRenrakusakinon1("00000000000");
        entity.setEigyotnshacd("00001");
        entity.setKaishutnshacd("00001");
        entity.setSkyshimedd("31");
        entity.setShrsaito("1");
        entity.setShrdd("31");
        entity.setKaishuhohokbn(params[0]);
        entity.setSkyhohokbn("1");
        entity.setMeisaiprntkbn("1");
        entity.setKsikkntetsuzukiflg("1");
        entity.setKsikkncd("0001");
        entity.setTairyukeisanshrsaito("1");
        entity.setTairyukbn(params.length > 2 ? params[2] : "1");
        entity.setKairekin(Long.parseLong("1000"));
        entity.setUrichoseikin(Long.parseLong("1000"));
        entity.setSkymeisaisofukbn("1");
        entity.setYukokbn("1");
        s02M017TDao.insert(entity);
    }

    /**
     * ショップマスタデータ生成
     * 
     * @param invoicShikoYmd インボイス施行日
     * @param taxKeisanKbnUriKirikaeDd 税計算区分売上切替日
     * @param shinTaxKeisanKbnUri 新税計算区分売上
     * @param genzTaxKeisanKbnUri 現税計算区分売上
     */
    private void insertS02M046T(String invoicShikoYmd, String taxKeisanKbnUriKirikaeDd,
        String shinTaxKeisanKbnUri, String genzTaxKeisanKbnUri) {
        // データ削除
        deleteS02M046T();
        // データ生成
        S02M046TEntity entity = new S02M046TEntity();
        entity.setApkihoncd(APKIHONCD_TEST);
        entity.setShopcd("000");
        entity.setShopkbn("1");
        entity.setShopshitenkbn("1");
        entity.setJigyoshokanri("1");
        entity.setShopnm("テストショップ");
        entity.setShopnmryakusho("テスト");
        entity.setYubinno("0000000");
        entity.setAdrsn1("北海道");
        entity.setRenrakusakinokbnn1("1");
        entity.setRenrakusakinon1("00000000000");
        entity.setZaikokanrikbn("1");
        entity.setKnndotptarinashikbn("1");
        entity.setHyojungenkahasukeisankbn("2");
        entity.setTaxhasukeisankbn("2");
        entity.setSkyshotairyukmntdsplykbn("1");
        entity.setNoshoazyokikazudsplykbn("1");
        entity.setHonbuclcrenkeikbn("1");
        entity.setUrikinhasukeisankbn("2");
        entity.setShimedd("31");
        entity.setKessandm("12");
        entity.setKessanki("12");
        entity.setKessankikshnshimedd("00000000");
        entity.setSundacpteigyokbn("1");
        entity.setSunddlvryeigyokbn("1");
        entity.setSundstupeigyokbn("1");
        entity.setSundseisaneigyokbn("1");
        entity.setMondacpteigyokbn("1");
        entity.setMonddlvryeigyokbn("1");
        entity.setMondstupeigyokbn("1");
        entity.setMondseisaneigyokbn("1");
        entity.setTuedacpteigyokbn("1");
        entity.setTueddlvryeigyokbn("1");
        entity.setTuedstupeigyokbn("1");
        entity.setTuedseisaneigyokbn("1");
        entity.setWeddacpteigyokbn("1");
        entity.setWedddlvryeigyokbn("1");
        entity.setWeddstupeigyokbn("1");
        entity.setWeddseisaneigyokbn("1");
        entity.setThedacpteigyokbn("1");
        entity.setTheddlvryeigyokbn("1");
        entity.setThedstupeigyokbn("1");
        entity.setThedseisaneigyokbn("1");
        entity.setFridacpteigyokbn("1");
        entity.setFriddlvryeigyokbn("1");
        entity.setFridstupeigyokbn("1");
        entity.setFridseisaneigyokbn("1");
        entity.setSatdacpteigyokbn("1");
        entity.setSatddlvryeigyokbn("1");
        entity.setSatdstupeigyokbn("1");
        entity.setSatdseisaneigyokbn("1");
        entity.setSystmkanshamei("テスト管理者");
        entity.setSystmkanritantomail("xxxxxxx@xxxxxxx.co.jp");
        entity.setSystmkansharenrakukbnn1("1");
        entity.setSystmkansharenrakunon1("00000000000");
        entity.setShinshohitax(BigDecimal.valueOf(10));
        entity.setShohitaxkirikaedd("20171001");
        entity.setGenzshohitax(BigDecimal.valueOf(8));
        entity.setShintaxkeisankbnuri(shinTaxKeisanKbnUri);
        entity.setTaxkeisankbnurikirikaedd(taxKeisanKbnUriKirikaeDd);
        entity.setGenztaxkeisankbnuri(genzTaxKeisanKbnUri);
        entity.setHokaapdlvryitakukbn("1");
        entity.setJissekikanriflg("1");
        entity.setMtmkisvkbn("1");
        entity.setMuryobtlsvkbn("1");
        entity.setYukokbn("1");
        entity.setInvoicshikoymd(invoicShikoYmd);
        s02M046TDao.insert(entity);
    }

    /**
     * 得意先実績テーブルデータ生成
     */
    private void insertS02M050T() {
        // データ削除
        deleteS02M050T();
        // データ生成
        S02M050TEntity entity = new S02M050TEntity();
        entity.setApkihoncd(APKIHONCD_TEST);
        entity.setShopcd("000");
        entity.setTokuisakicd("1");
        entity.setGenzaizd(0);
        entity.setYukokbn("1");
        s02M050TDao.insert(entity);
    }

    /**
     * 売上テーブルデータ生成
     * 
     * @param values 値リスト（0:データ作成元区分、1:売上日）
     */
    private void insertS06T001T(String... values) {
        // データ削除
        deleteS06T001T();
        // データ生成
        S06T001TEntity entity = new S06T001TEntity();
        entity.setApkihoncd(APKIHONCD_TEST);
        entity.setShopcd("000");
        entity.setUridnpyno("X0000");
        entity.setEdano(Short.parseShort("0"));
        entity.setSkysakicd("1");
        entity.setTokuisakicd("1");
        entity.setSkyprcsjissikbn(Short.parseShort("1"));

        String uridd =
            values != null && values.length > 1 && values[1] != null ? values[1] : "20200131";
        entity.setUridd(uridd);
        entity.setUrishimeprcsdd("20200131");
        entity.setSkyprcsjissidd("00000000");
        entity.setYukokbn("1");
        entity.setDatsksmotokbn(values == null ? "01" : values[0]);
        entity.setTorihikikbn("1");
        entity.setAcptdnpyno("Z0000");
        entity.setAcptdnpyedano(Short.parseShort("1"));
        entity.setTorihikikbn("2");
        entity.setSiiresakicd("A");
        entity.setNyukamotocd("B");
        entity.setAcptdd("20210101");
        entity.setUrikin(Long.parseLong("1230"));
        entity.setShohitaxkin(Long.parseLong("123"));
        entity.setSankoushohitaxkin(Long.parseLong("1240"));
        entity.setAcpttnshacd("C");
        entity.setDlvrytnshacd("D");
        entity.setDlvrykankatsuapkihoncd("E");
        entity.setDlvrykankatsushopcd("F");
        entity.setDlvrykyotencd("G");
        entity.setDlvrykbn("3");
        entity.setDlvrycmpnycd("H");
        entity.setShukkamotokbn("4");
        entity.setEigyotnshacd("I");
        entity.setJissekitnshacd("J");
        entity.setSkyshimedydm("210002");
        entity.setSkyshimedd("10");
        entity.setNebikishimedydm("209912");
        entity.setNebikishimedd("11");
        entity.setNebikiprcsjissidd("20991201");
        entity.setDnpybiko("XXXXX");
        entity.setDnpyno("X0000");
        entity.setTaxkeisankbn("1");
        entity.setSiirednpyno("B000");
        entity.setNykndnpyno("C000");
        entity.setNyknkin(Long.parseLong("1250"));
        entity.setNyknkind("1");
        s06T001TDao.insert(entity);
    }

    /**
     * 売上明細テーブルデータ生成
     * 
     * @param values 項目値（0:消費税額、1:税区分）
     */
    private void insertS06T002T(String... values) {
        // データ削除
        deleteS06T002T();
        // データ生成
        S06T002TEntity entity = new S06T002TEntity();
        entity.setApkihoncd(APKIHONCD_TEST);
        entity.setShopcd("000");
        entity.setUridnpyno("X0000");
        entity.setEdano(Short.parseShort("0"));
        entity.setUridnpymeisaino(1);
        entity.setUridnpymeisaiedano(Short.parseShort("0"));
        entity.setShncd("000000");
        entity.setShohitaxkin(Long.parseLong(values[0] != null ? values[0] : "1000"));
        entity.setTanpinmeisaiflg("1");
        entity.setYukokbn("1");
        entity.setLctgrycd("99");
        entity.setMctgrycd("001");
        entity.setSctgrycd("001");
        entity.setKeijokbn("01");
        entity.setSuryo(BigDecimal.valueOf(Long.parseLong("2")));
        entity.setHyojununt(BigDecimal.valueOf(Long.parseLong("2000")));
        entity.setUnt(BigDecimal.valueOf(Long.parseLong("2001")));
        entity.setKin(Long.parseLong("1230"));
        entity.setUntshohitaxkin(Long.parseLong("123"));
        entity.setSankoushohitaxkin(Long.parseLong("124"));

        String taxkbn = values.length > 1 && values[1] != null ? values[1] : "1";
        entity.setTaxkbn(taxkbn);
        entity.setMeisaikbn("0");
        entity.setNyuryokumeisaiflg("1");
        entity.setStshncd("100000");
        entity.setKatasiki("1");
        entity.setSiiresuryo(BigDecimal.valueOf(Long.parseLong("3")));
        entity.setSiirekin(Long.parseLong("0"));
        entity.setSiireunt(BigDecimal.valueOf(Long.parseLong("2002")));
        entity.setSiireshohitaxkin(Long.parseLong("120"));
        entity.setZaikokshnsumiflg("1");
        entity.setTokuisakikbn("A");
        entity.setJutakudlvrycstkbn("B");
        entity.setRoyaltyshimeymd("20110101");
        s06T002TDao.insert(entity);
    }

    /**
     * 請求残高テーブルデータ生成
     * 
     * @param shimedd 締日
     * @param kaishuhohokbn 回収方法区分
     * @param keshikomikin 消込金額
     * @param params パラメータリスト（0:消込残高、1:締日変更判定値、2:締年月、3:削除判定（1:未削除）、4:請求書番号、5:繰越金額、6:得意先コード、7:連番）
     */
    private void insertS07T001T(String shimedd, String kaishuhohokbn, String keshikomikin,
        String... params) {
        // データ削除
        boolean isNotDelete = params.length > 3 && params[3].equals("1");
        if (!isNotDelete) {
            deleteS07T001T();
        }

        // データ生成
        S07T001TEntity entity = new S07T001TEntity();
        entity.setApkihoncd(APKIHONCD_TEST);
        entity.setShopcd("000");
        entity.setSkysakicd("1");
        entity.setTokuisakicd(params.length > 6 ? params[6] : "1");
        entity.setShimedydm(params.length > 2 ? params[2] : "210001");
        entity.setShimedd(shimedd == null ? "31" : shimedd);
        entity.setRenban(params.length > 7 ? Integer.parseInt(params[7]) : 0);
        entity.setSkyprcsjissikbn(Short.parseShort("1"));
        entity.setSkyshono(params.length > 4 ? params[4] : "00001");
        entity.setSkyshonoedano(Short.parseShort("0"));
        entity.setKonkaishimedd(entity.getShimedydm() + entity.getShimedd());
        entity.setKurikoshikin(params.length > 5 ? Long.parseLong(params[5]) : 1000L);
        entity.setKonkaitaxnukiurikin(1000L);
        entity.setKonkaishohitaxkin(100L);
        entity.setKeshikomikin(Long.parseLong(keshikomikin == null ? "1000" : keshikomikin));
        entity.setKonkaishohitaxkin(Long.parseLong("100"));
        entity.setYukokbn("1");
        entity.setKaishuhohokbn(kaishuhohokbn);
        entity.setKeshikomizd(Integer.parseInt(params[0]));

        String lastDay = DateTimeUtil.getLastDate(entity.getShimedydm(), entity.getShimedd());
        if (lastDay.compareTo(entity.getShimedd()) == -1) {
            entity.setShimedd(lastDay);
            entity.setKonkaishimedd(entity.getShimedydm() + entity.getShimedd());
        }

        s07T001TDao.insert(entity);
        if (params.length == 2) {
            entity.setShimedydm("210002");
            entity.setSkyshono("00002");
            entity.setKonkaishimedd("210002" + entity.getShimedd());
            s07T001TDao.insert(entity);
        }
    }

    /**
     * 請求締ワークデータ生成
     * 
     * @param iraidd 依頼日
     * @param iraijikan 依頼時間
     * @param shimeprcskbn 締処理区分
     */
    private void insertS07W003T(String iraidd, String iraijikan, String shimeprcskbn) {
        // データ削除
        deleteS07W003T();
        // データ生成
        S07W003TEntity entity = new S07W003TEntity();
        entity.setApkihoncd(APKIHONCD_TEST);
        entity.setShopcd("000");
        entity.setIraidd(iraidd);
        entity.setIraijikan(iraijikan);
        entity.setSkysakicd("1");
        entity.setShimeprcskbn(shimeprcskbn);
        s07W003TDao.insert(entity);
    }

    /**
     * 請求締処理管理テーブルデータ生成
     * 
     * @param prcsmode 処理モード
     * @param prcsjotai 処理状態
     */
    private void insertS07T004T(String prcsmode, String prcsjotai) {
        // データ削除
        deleteS07T004T();
        // データ生成
        S07T004TEntity entity = new S07T004TEntity();
        entity.setApkihoncd(APKIHONCD_TEST);
        entity.setShopcd("000");
        entity.setPrcsmode(prcsmode);
        entity.setShimedydm("210001");
        entity.setShimedd("31");
        entity.setKonkaishimedd("21000131");
        entity.setIraidd("21000131");
        entity.setIraijikan("101010");
        entity.setPrcsjotai(prcsjotai);
        s07T004TDao.insert(entity);
    }

    /**
     * 請求締処理詳細管理テーブルデータ生成
     * 
     * @param prcsid 処理ID
     * @param prcsjotai 処理状態
     */
    private void insertS07T005T(String prcsid, String prcsjotai) {
        // データ削除
        deleteS07T005T(prcsid);
        // データ生成
        S07T005TEntity entity = new S07T005TEntity();
        entity.setApkihoncd(APKIHONCD_TEST);
        entity.setShopcd("000");
        entity.setPrcsid(prcsid);
        entity.setPrcsjotai(prcsjotai);
        s07T005TDao.insert(entity);
    }

    /**
     * 請求明細関連テーブルデータ生成
     * 
     * @param urinyknkbn 売上入金区分
     */
    private void insertS07T006T(String urinyknkbn) {
        // データ削除
        deleteS07T006T();
        // データ生成
        S07T006TEntity entity = new S07T006TEntity();
        entity.setApkihoncd(APKIHONCD_TEST);
        entity.setShopcd("000");
        entity.setSkyshono("00001");
        entity.setSkyshonoedano(0);
        entity.setDnpyno("X0000");
        entity.setNykndnpynoedano("0");;
        entity.setSkysakicd("1");
        entity.setTokuisakicd("1");
        entity.setUrinyknkbn(urinyknkbn);
        entity.setYukokbn("1");
        s07T006TDao.insert(entity);
    }

    /**
     * 入金明細テーブルデータ生成
     * 
     * @param dnpyno 伝票番号
     */
    private void insertS10T001T(String dnpyno) {
        // データ削除
        deleteS10T001T();
        // データ生成
        S10T001TEntity entity = new S10T001TEntity();
        entity.setApkihoncd(APKIHONCD_TEST);
        entity.setShopcd("000");
        entity.setDnpyno(dnpyno == null ? "A0000" : dnpyno);
        entity.setNykndnpynoedano(0);
        entity.setSkysakicd("1");
        entity.setTokuisakicd("1");
        entity.setNyknkind("1");
        entity.setNykndd("20200101");
        entity.setUrishimeprcsdd("20200101");
        entity.setSkyprcsjissidd("00000000");
        entity.setYukokbn("1");
        entity.setNebikikin(Long.parseLong("1000"));
        entity.setNyknkin(Long.parseLong("2000"));
        entity.setTesuryo(300);
        entity.setSkyprcsjissikbn(Short.parseShort("1"));
        s10T001TDao.insert(entity);
    }

    /**
     * テストデータ削除
     */
    private void clearTestData() {
        deleteS07W006T(); // 請求締用対象得意先ワーク
        deleteS07T006T(); // 請求明細関連テーブル
        deleteS07T005T(null); // 請求締処理詳細管理テーブル
        deleteS07T004T();// 請求締処理管理テーブル
        deleteS07W003T();// 請求締ワークテスト
        deleteS07T001T();// 請求残高テーブル
        deleteS06T002T();// 売上明細テーブル
        deleteS06T001T();// 売上テーブル
        deleteS02M046T();// ショップマスタ
        deleteS02M050T();// 得意先実績テーブル
        deleteS02M017T();// 請求先マスタ
        deleteS02M013T();// 得意先マスタ
        deleteS02M006T("1");// 運用日付テストデータ削除
        deleteS10T001T();// 入金明細テーブル
    }

    /**
     * システム管理マスタデータ削除
     * 
     * @param setteiCd 設定コード
     */
    private void deleteS02M006T(String setteiCd) {
        S02M006TEntityExample example = new S02M006TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD_TEST).andSetteicdEqualTo(setteiCd);
        s02M006TDao.deleteByExample(example);
    }

    /**
     * 得意先マスタデータ削除
     */
    private void deleteS02M013T() {
        S02M013TEntityExample example = new S02M013TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD_TEST);
        s02M013TDao.deleteByExample(example);
    }

    /**
     * 請求先マスタデータ削除
     */
    private void deleteS02M017T() {
        S02M017TEntityExample example = new S02M017TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD_TEST);
        s02M017TDao.deleteByExample(example);
    }

    /**
     * ショップマスタデータ削除
     */
    private void deleteS02M046T() {
        S02M046TEntityExample example = new S02M046TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD_TEST);
        s02M046TDao.deleteByExample(example);
    }

    /**
     * 得意先実績テーブルデータ削除
     */
    private void deleteS02M050T() {
        S02M050TEntityExample example = new S02M050TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD_TEST);
        s02M050TDao.deleteByExample(example);
    }

    /**
     * 売上テーブルデータ削除
     */
    private void deleteS06T001T() {
        S06T001TEntityExample example = new S06T001TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD_TEST);
        s06T001TDao.deleteByExample(example);
    }

    /**
     * 売上明細テーブルデータ削除
     */
    private void deleteS06T002T() {
        S06T002TEntityExample example = new S06T002TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD_TEST);
        s06T002TDao.deleteByExample(example);
    }

    /**
     * 請求残高テーブルデータ削除
     */
    private void deleteS07T001T() {
        S07T001TEntityExample example = new S07T001TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD_TEST);
        s07T001TDao.deleteByExample(example);
    }

    /**
     * 請求締ワークデータ削除
     */
    private void deleteS07W003T() {
        S07W003TEntityExample example = new S07W003TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD_TEST);
        s07W003TDao.deleteByExample(example);
    }

    /**
     * 請求締処理管理テーブルデータ削除
     */
    private void deleteS07T004T() {
        S07T004TEntityExample example = new S07T004TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD_TEST);
        s07T004TDao.deleteByExample(example);
    }

    /**
     * 請求締処理詳細管理テーブルデータ削除
     * 
     * @param prcsid 処理ID
     */
    private void deleteS07T005T(String prcsid) {
        S07T005TEntityExample example = new S07T005TEntityExample();
        if (prcsid == null) {
            example.createCriteria().andApkihoncdEqualTo(APKIHONCD_TEST);
        } else {
            example.createCriteria().andApkihoncdEqualTo(APKIHONCD_TEST).andPrcsidEqualTo(prcsid);
        }
        s07T005TDao.deleteByExample(example);
    }

    /**
     * 請求明細関連テーブルデータ削除
     */
    private void deleteS07T006T() {
        S07T006TEntityExample example = new S07T006TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD_TEST);
        s07T006TDao.deleteByExample(example);
    }

    /**
     * 入金明細テーブルデータ削除
     */
    private void deleteS10T001T() {
        S10T001TEntityExample example = new S10T001TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD_TEST);
        s10T001TDao.deleteByExample(example);
    }

    /**
     * 請求締用対象得意先ワークデータ削除
     */
    private void deleteS07W006T() {
        S07W006TEntityExample example = new S07W006TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD_TEST);
        s07W006TDao.deleteByExample(example);
    }

    /**
     * 請求締検索Dto生成
     * 
     * @param params パラメータリスト（0:処理区分、1:締日）
     * @return 請求締検索Dto
     */
    private D1501W11SearchConditionDto getExecuteTestDto(String... params) {
        String systemDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String systemTime = new SimpleDateFormat("HHmmss").format(new Date());

        DbManagementSession dbManagementSession = new DbManagementSession();
        dbManagementSession.setApkihoncd(APKIHONCD_TEST);
        dbManagementSession.setShopcd("000");
        dbManagementSession.setTnshacd("00001");
        dbManagementSession.setUnyoDate("21000131");
        dbManagementSession.setIpAddress("0.0.0.1");
        dbManagementSession.setProgramId("D1501W11");
        dbManagementSession.setSystemDate(systemDate);
        dbManagementSession.setSystemTime(systemTime);

        String shimedydm = params.length > 2 ? params[2] : "210001";
        String shimedd = params.length > 1 ? params[1] : "31";
        D1501W11SearchConditionDto result = new D1501W11SearchConditionDto();
        result.setPrcskbn(params[0]);
        result.setApkihoncd(APKIHONCD_TEST);
        result.setShopcd("000");
        result.setShimedd1(shimedydm);
        result.setShimedd3(shimedd);
        result.setDbManagementSession(dbManagementSession);
        result.setSkyshimedydmdd();

        return result;
    }

}
