package jp.co.aquaclara.accis.web.domain.service.c1005;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.test.annotation.Rollback;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M006TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M013TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M050TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S10T004TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S10W002TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M003TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M003TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M006TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M006TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M050TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M050TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S10T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S10T001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S10T003TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S10T003TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S10T004TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S10T004TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S10W001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S10W001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S10W002TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S10W002TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S10W003TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S10W001TD1608W03IkkatsuKeshikomiEntity;
import jp.co.aquaclara.accis.common.exception.BusinessException;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.controller.commons.DbManagementSession;
import jp.co.aquaclara.accis.web.domain.dto.c1005.C1005W01HeaderDto;
import jp.co.aquaclara.accis.web.domain.dto.c1005.C1005W01MeisaiDto;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.AccisException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 入金入力（入金登録）サービステストクラス
 * 
 * @author accisdev044
 *
 */
@Slf4j
@SpringBootTest
class C1005W01ServiceTest extends C1005W01Service {

    /** AP基本コード */
    private static final String APKIHONCD = "O01";

    /** システム管理マスタDao */
    @Autowired
    private S02M006TDao s02m006tDao;
    /** 得意先マスタDao */
    @Autowired
    private S02M013TDao s02m013tDao;
    /** 得意先実績テーブル */
    @Autowired
    private S02M050TDao s02m050tDao;
    /** 入金消込履歴テーブルDao */
    @Autowired
    private S10T004TDao s10t004tDao;
    /** 入金取込得意先ワークDao */
    @Autowired
    private S10W002TDao s10w002tDao;

    /** メッセージ取得Object */
    @Autowired
    private MessageSourceAccessor msg;

    /**
     * 消込請求残高チェックテスト
     */
    @Test
    @Rollback
    void testClearingSeikyuBalanceCheck() {
        List<AccisException> messageList = new ArrayList<AccisException>();
        messageList.add(new AccisException(MessageID.W255, "消込先の請求締めが解除されているため取消"));
        messageList.add(new AccisException(MessageID.W255, "消込先の請求締めが解除されているため取消"));
        messageList.add(new AccisException(MessageID.W194, ""));
        messageList.add(new AccisException(MessageID.W194, ""));
        messageList.add(new AccisException(MessageID.W194, ""));

        C1005W01HeaderDto dto = this.createDefaultC1005W01HeaderDto("1", null, null);

        // テストパターンリスト設定（入金種別、入金消込履歴消込金額、得意先コード、請求先コード、消込残高、請求書番号）
        List<List<String>> testList = new ArrayList<List<String>>();
        testList.add(Arrays.asList("0", "0", "XXXXXX", null, "0", "1"));
        testList.add(Arrays.asList("0", "0", "XXXXXX", null, "0", "1"));
        testList.add(Arrays.asList(null, "1000", "000000", null, "0", "1"));
        testList.add(Arrays.asList(null, "-1000", "000000", null, "0", "1"));
        testList.add(Arrays.asList("31", "1000", "000000", null, "0", "1"));
        testList.add(Arrays.asList(null, "1000", "000000", dto.getSkysakicd(), "-5000", "1"));
        testList.add(Arrays.asList(null, "-1000", "000000", dto.getSkysakicd(), "5000", "1"));
        testList.add(Arrays.asList("31", "1000", "000000", dto.getSkysakicd(), "-5000", "1"));
        testList.add(Arrays.asList(null, "0", "000000", dto.getSkysakicd(), "0", null));

        int index = 0;
        for (List<String> test : testList) {
            try {
                this.deleteS07T001T();
                this.deleteS10T001T();
                this.deleteS10T004T();

                if (index > 0) {
                    final String nyknkind = test.get(0);
                    final Long nkkrkeshikomikin = Long.valueOf(test.get(1));
                    final String tokuisakicd = test.get(2);
                    final String skysakicd = test.get(3);
                    final Long keshikomizd = Long.valueOf(test.get(4));
                    final String skyshono = test.get(5);

                    this.createS07T001T(1, tokuisakicd, keshikomizd, skysakicd);
                    this.createS10T001T(dto.getDnpyno(), false, true, nyknkind, null);
                    this.createS10T004T(nkkrkeshikomikin, skyshono);
                }

                this.clearingSeikyuBalanceCheck(dto);
                assertTrue(index > 4);
            } catch (AccisException e) {
                MessageID id = messageList.get(index).getMessageId();
                Object[] params = {messageList.get(index).getMessage()};
                assertEquals(e.getMessageId(), id);
                assertEquals(e.getMessage(), msg.getMessage(id.toString(), params));
            }
            index++;
        }
    }

    /**
     * 入金伝票チェックテスト
     * 
     * @throws ParseException 型変換例外
     */
    @Test
    @Rollback
    void testNyukinVoucherCheck() throws ParseException {
        // テストパターンリスト生成（過去日数データ生成有無フラグ、入金日加算日数、正常結果フラグ）
        List<List<String>> testItemList = new ArrayList<List<String>>();
        testItemList.add(Arrays.asList("0", "1", "0"));
        testItemList.add(Arrays.asList("0", "0", "1"));
        testItemList.add(Arrays.asList("1", "61", "0"));
        testItemList.add(Arrays.asList("1", "60", "1"));

        for (List<String> testItems : testItemList) {
            Calendar tmp = Calendar.getInstance();
            tmp.setTime(new Date());
            tmp.add(Calendar.DATE, -Integer.valueOf(testItems.get(1)));
            String nykndd = new SimpleDateFormat("yyyyMMdd").format(tmp.getTime());

            tmp.setTime(new Date());
            tmp.add(Calendar.DATE, testItems.get(0).equals("1") ? -60 : 0);
            String beforeDate = testItems.get(2).equals("1") ? ""
                : new SimpleDateFormat("yyyy/MM/dd").format(tmp.getTime());

            try {
                this.deleteData();
                this.createS10T001T("X", false, true, null, null);
                this.createS10T003T("X", nykndd);
                this.createUnyoDate();
                this.createBeforeDays(testItems.get(0).equals("1"));;

                C1005W01HeaderDto dto = new C1005W01HeaderDto();
                dto.setApkihoncd(APKIHONCD);
                dto.setShopcd("000");
                dto.setDnpyno("X");
                dto.setSkysakicd("000000");

                this.nyukinVoucherCheck(dto);
            } catch (BusinessException e) {
                Object[] params = {"入金日が" + beforeDate + "より過去の伝票", "取消"};
                assertEquals(e.getMessage(),
                    this.msg.getMessage(MessageID.W171.toString(), params));
            }
        }
    }

    /**
     * （入金登録時）相関チェック（処理前）テスト
     * 
     * @throws ParseException 型変換例外
     */
    @Test
    @Rollback
    void testEntryCorrelationBeforeCheck() throws ParseException {
        // テストパターンリスト生成（過去日数データ生成有無フラグ、未来日数データ生成有無フラグ、入金日加算日数、正常結果フラグ）
        List<List<String>> testItemList = new ArrayList<List<String>>();
        testItemList.add(Arrays.asList("0", "0", "-1", "0"));
        testItemList.add(Arrays.asList("0", "0", "1", "0"));
        testItemList.add(Arrays.asList("0", "0", "0", "1"));
        testItemList.add(Arrays.asList("1", "1", "-61", "0"));
        testItemList.add(Arrays.asList("1", "1", "61", "0"));
        testItemList.add(Arrays.asList("1", "1", "-60", "1"));
        testItemList.add(Arrays.asList("1", "1", "60", "1"));

        this.deleteData();
        this.createS02M003T();
        for (List<String> testItems : testItemList) {
            Calendar tmp = Calendar.getInstance();
            tmp.setTime(new Date());
            tmp.add(Calendar.DATE, Integer.valueOf(testItems.get(2)));
            String nykndd = new SimpleDateFormat("yyyyMMdd").format(tmp.getTime());

            tmp.setTime(new Date());
            tmp.add(Calendar.DATE, testItems.get(0).equals("1") ? -60 : 0);
            String beforeDate = testItems.get(3).equals("1") ? ""
                : new SimpleDateFormat("yyyy/MM/dd").format(tmp.getTime());
            tmp.setTime(new Date());
            tmp.add(Calendar.DATE, testItems.get(1).equals("1") ? 60 : 0);
            String afterDate = testItems.get(3).equals("1") ? ""
                : new SimpleDateFormat("yyyy/MM/dd").format(tmp.getTime());

            try {
                this.deleteS02M006T();
                this.createUnyoDate();
                this.createBeforeDays(testItems.get(0).equals("1"));
                this.createAfterDays(testItems.get(1).equals("1"));

                C1005W01HeaderDto dto = new C1005W01HeaderDto();
                dto.setPrcskbn("3");
                dto.setApkihoncd(APKIHONCD);
                dto.setShopcd("000");
                dto.setUrichoseikin(1L);
                dto.setNykndd(nykndd);
                dto.setKaishutnshacd("000000");

                this.entryCorrelationBeforeCheck(dto);
            } catch (BusinessException e) {
                Object[] params = {"入金日", beforeDate, afterDate};
                assertEquals(e.getMessage(),
                    this.msg.getMessage(MessageID.W115.toString(), params));
            }
        }
    }

    /**
     * （入金登録時）相関チェック（処理後）テスト（異常系）
     * 
     * @throws ParseException 型変換例外
     */
    @Test
    void testEntryCorrelationAfterCheckAbnomal() throws ParseException {

        // テストパターン（処理区分、合計入金額、明細有無フラグ、今回消込残高）
        List<List<String>> testList = new ArrayList<List<String>>();
        testList.add(new ArrayList<String>(Arrays.asList("1", "0", "0", "0")));
        testList.add(new ArrayList<String>(Arrays.asList("1", "0", "1", "-1")));
        testList.add(new ArrayList<String>(Arrays.asList("1", "-1", "0", "-1")));
        testList.add(new ArrayList<String>(Arrays.asList("3", "0", "0", "0")));

        int index = 0;
        for (List<String> test : testList) {
            try {
                C1005W01HeaderDto dto = new C1005W01HeaderDto();
                dto.setPrcskbn(test.get(0));
                dto.setNyknsmkin(Long.parseLong(test.get(1)));
                dto.setKonkaikeshizd(Long.parseLong(test.get(3)));

                List<C1005W01MeisaiDto> meisaiDtoList = new ArrayList<C1005W01MeisaiDto>();
                if (test.get(2).equals("1")) {
                    meisaiDtoList.add(new C1005W01MeisaiDto());
                }

                this.entryCorrelationAfterCheck(dto, meisaiDtoList);
                fail("Test Failed");
            } catch (BusinessException e) {
                if (index == 1 || index == 2) {
                    assertEquals(e.getMessage(), "過去の入金額の合計より大きいマイナス入金は入力できません。");
                } else {
                    assertEquals(e.getMessage(), "未消込残高を持つ請求残高が存在しませんでした。");
                }
            }
            index++;
        }
    }

    /**
     * 合計金額取得テスト
     */
    @Test
    void testGetSumKingaku() {

        // テストパターン（No、処理区分、合計消込可能額、今回消込金額、消込済金額、取消消込金額、消込残高）
        List<List<String>> testList = new ArrayList<List<String>>();
        // 処理区分：入金
        testList.add(new ArrayList<String>(Arrays.asList("1", "1", "1000", "10", "1", "0", "10")));
        testList.add(new ArrayList<String>(Arrays.asList("2", "1", "100", "100", "1", "0", "190")));
        testList.add(new ArrayList<String>(Arrays.asList("3", "1", "-1000", "-1", "1", "0", "0")));
        testList.add(new ArrayList<String>(Arrays.asList("4", "1", "-10", "-10", "100", "0", "0")));
        // 処理区分：調整
        testList.add(new ArrayList<String>(Arrays.asList("5", "3", "1000", "10", "1", "0", "10")));
        testList.add(new ArrayList<String>(Arrays.asList("6", "3", "100", "100", "1", "0", "190")));
        testList.add(new ArrayList<String>(Arrays.asList("7", "3", "-1000", "-1", "1", "0", "0")));
        testList.add(new ArrayList<String>(Arrays.asList("8", "3", "-10", "-10", "100", "0", "0")));
        // 処理区分：取消
        testList.add(new ArrayList<String>(Arrays.asList("9", "2", "10", "-10", "100", "10", "0")));
        testList.add(new ArrayList<String>(Arrays.asList("10", "2", "10", "-1", "1", "1000", "0")));
        testList.add(new ArrayList<String>(Arrays.asList("11", "2", "1", "10", "1", "-10", "100")));
        testList.add(new ArrayList<String>(Arrays.asList("12", "2", "1", "10", "1", "-100", "10")));

        for (List<String> test : testList) {
            // 入金入力ヘッダ情報Dto生成
            C1005W01HeaderDto dto = new C1005W01HeaderDto();
            dto.setPrcskbn(test.get(1));
            dto.setKshkmkanokinsm(Long.parseLong(test.get(2)));

            // 消込対象明細Dto生成
            List<C1005W01MeisaiDto> meisaiDtoList = new ArrayList<C1005W01MeisaiDto>();
            for (int i = 0; i < 5; i++) {
                C1005W01MeisaiDto meisaiDto = new C1005W01MeisaiDto();
                meisaiDto.setTouurikin(10L);
                meisaiDto.setKeshikomizumikin(Long.parseLong(test.get(4)));
                meisaiDto.setKeshikomizd(Long.parseLong(test.get(6)));
                meisaiDto.setUpdkeshikomikin(Long.parseLong(test.get(5)));
                meisaiDto.setMikeshizankin(40L);
                meisaiDto.setKonkaikeshikomikin(Long.parseLong("0"));
                meisaiDtoList.add(meisaiDto);
            }

            // 合計金額取得処理
            log.debug("■■■■■■■■■■ " + test.toString() + " ■■■■■■■■■■");
            meisaiDtoList = this.getSumKingaku(dto, meisaiDtoList);

            // 消込対象明細検証（消込、今回消込金額）
            Long keshikomizumikinsum = 0L;
            Long keshikomikinsum = 0L;
            for (C1005W01MeisaiDto meisai : meisaiDtoList) {
                Long konkaikeshikomikin = Long.parseLong(test.get(3));
                if (test.get(0).equals("2") || test.get(0).equals("4") || test.get(0).equals("6")
                    || test.get(0).equals("8")) {
                    konkaikeshikomikin -= keshikomikinsum;
                }

                assertEquals(meisai.getKeshikomi(), konkaikeshikomikin == 0L ? null : "1");
                assertEquals(meisai.getKonkaikeshikomikin(), konkaikeshikomikin);

                keshikomizumikinsum += meisai.getKeshikomizumikin();
                keshikomikinsum += meisai.getKonkaikeshikomikin();
            }

            // 合計金額検証（当月売上額、消込済金額、今回消込金額、未消し残額、今回消込残高）
            assertEquals(dto.getTouurikinsm(), 50L);
            assertEquals(dto.getKeshizumikinsm(), keshikomizumikinsum);
            assertEquals(dto.getKonkaikeshikinsm(), keshikomikinsum);
            assertEquals(dto.getMikeshizankinsm(),
                dto.getTouurikinsm() - dto.getKeshizumikinsm() - dto.getKonkaikeshikinsm());
            assertEquals(dto.getKonkaikeshizd(),
                dto.getKshkmkanokinsm() - dto.getKonkaikeshikinsm());
        }
    }

    /**
     * 一括入金消込処理テスト（異常系）
     * 
     * @throws Exception システム例外
     */
    @Test
    void testBulkNyukinClearingAbnomal() throws Exception {
        // テストパターン
        // （AP基本コード、ショップコード、担当者AP基本コード、担当者ショップコード、担当者コード、IPアドレス、伝票番号From、伝票番号To、メッセージ、処理区分）
        List<List<String>> testList = new ArrayList<List<String>>();
        // 処理区分：入金
        testList.add(new ArrayList<String>(Arrays.asList(null, "000", "000", "000", "000000",
            "0.0.0.1", "000000", "000000", "★★★ パラメータ.AP基本コードが設定されていません。 ★★★", "1")));
        testList.add(new ArrayList<String>(Arrays.asList("000", null, "000", "000", "000000",
            "0.0.0.1", "000000", "000000", "★★★ パラメータ.ショップコードが設定されていません。 ★★★", "1")));
        testList.add(new ArrayList<String>(Arrays.asList("000", "000", null, "000", "000000",
            "0.0.0.1", "000000", "000000", "★★★ パラメータ.ログインユーザ情報(ＡＰ基本コード)が設定されていません。 ★★★", "1")));
        testList.add(new ArrayList<String>(Arrays.asList("000", "000", "000", null, "000000",
            "0.0.0.1", "000000", "000000", "★★★ パラメータ.ログインユーザ情報(ショップコード)が設定されていません。 ★★★", "1")));
        testList.add(new ArrayList<String>(Arrays.asList("000", "000", "000", "000", null,
            "0.0.0.1", "000000", "000000", "★★★ パラメータ.担当者コードが設定されていません。 ★★★", "1")));
        testList.add(new ArrayList<String>(Arrays.asList("000", "000", "000", "000", "000000", null,
            "000000", "000000", "★★★ パラメータ.ＩＰアドレスが設定されていません。 ★★★", "1")));
        testList.add(new ArrayList<String>(Arrays.asList("000", "000", "000", "000", "000000",
            "0.0.0.1", null, "000000", "★★★ パラメータ.採番伝票番号ＦＲＯＭが設定されていません。 ★★★", "1")));

        // 処理区分：調整
        testList.add(new ArrayList<String>(Arrays.asList(null, "000", "000", "000", "000000",
            "0.0.0.1", "000000", "000000", "★★★ パラメータ.AP基本コードが設定されていません。 ★★★", "3")));
        testList.add(new ArrayList<String>(Arrays.asList("000", null, "000", "000", "000000",
            "0.0.0.1", "000000", "000000", "★★★ パラメータ.ショップコードが設定されていません。 ★★★", "3")));
        testList.add(new ArrayList<String>(Arrays.asList("000", "000", null, "000", "000000",
            "0.0.0.1", "000000", "000000", "★★★ パラメータ.ログインユーザ情報(ＡＰ基本コード)が設定されていません。 ★★★", "3")));
        testList.add(new ArrayList<String>(Arrays.asList("000", "000", "000", null, "000000",
            "0.0.0.1", "000000", "000000", "★★★ パラメータ.ログインユーザ情報(ショップコード)が設定されていません。 ★★★", "3")));
        testList.add(new ArrayList<String>(Arrays.asList("000", "000", "000", "000", null,
            "0.0.0.1", "000000", "000000", "★★★ パラメータ.担当者コードが設定されていません。 ★★★", "3")));
        testList.add(new ArrayList<String>(Arrays.asList("000", "000", "000", "000", "000000", null,
            "000000", "000000", "★★★ パラメータ.ＩＰアドレスが設定されていません。 ★★★", "3")));
        testList.add(new ArrayList<String>(Arrays.asList("000", "000", "000", "000", "000000",
            "0.0.0.1", null, "000000", "★★★ パラメータ.採番伝票番号ＦＲＯＭが設定されていません。 ★★★", "3")));

        // 処理区分：取消
        testList.add(new ArrayList<String>(Arrays.asList(null, "000", "000", "000", "000000",
            "0.0.0.1", "000000", "000000", "★★★ パラメータ.AP基本コードが設定されていません。 ★★★", "2")));
        testList.add(new ArrayList<String>(Arrays.asList("000", null, "000", "000", "000000",
            "0.0.0.1", "000000", "000000", "★★★ パラメータ.ショップコードが設定されていません。 ★★★", "2")));
        testList.add(new ArrayList<String>(Arrays.asList("000", "000", null, "000", "000000",
            "0.0.0.1", "000000", "000000", "★★★ パラメータ.ログインユーザ情報(ＡＰ基本コード)が設定されていません。 ★★★", "2")));
        testList.add(new ArrayList<String>(Arrays.asList("000", "000", "000", null, "000000",
            "0.0.0.1", "000000", "000000", "★★★ パラメータ.ログインユーザ情報(ショップコード)が設定されていません。 ★★★", "2")));
        testList.add(new ArrayList<String>(Arrays.asList("000", "000", "000", "000", null,
            "0.0.0.1", "000000", "000000", "★★★ パラメータ.担当者コードが設定されていません。 ★★★", "2")));
        testList.add(new ArrayList<String>(Arrays.asList("000", "000", "000", "000", "000000", null,
            "000000", "000000", "★★★ パラメータ.ＩＰアドレスが設定されていません。 ★★★", "2")));
        testList.add(new ArrayList<String>(Arrays.asList("000", "000", "000", "000", "000000",
            "0.0.0.1", null, "000000", "★★★ パラメータ.採番伝票番号ＦＲＯＭが設定されていません。 ★★★", "2")));
        testList.add(new ArrayList<String>(Arrays.asList("000", "000", "000", "000", "000000",
            "0.0.0.1", "000000", null, "★★★ パラメータ.採番伝票番号ＴＯが設定されていません。 ★★★", "2")));
        testList.add(new ArrayList<String>(Arrays.asList("000", "000", "000", "000", "000000",
            "0.0.0.1", "000000", "000000", "★★★ パラメータ.請求書出力有無区分が正しくありません。 ★★★", "2")));

        // システム例外発生
        testList.add(new ArrayList<String>(Arrays.asList("X00", "000", "000", "000", "000000",
            "0.0.0.1", "000000", "000000", "System Error", "1")));
        testList.add(new ArrayList<String>(Arrays.asList("X00", "000", "000", "000", "000000",
            "0.0.0.1", "000000", "000000", "System Error", "3")));
        testList.add(new ArrayList<String>(Arrays.asList("X00", "000", "000", "000", "000000",
            "0.0.0.1", "000000", "000000", "System Error", "2")));
        // 請求書出力なし：0、1、null（パラメータチェック後の後続処理でエラーになること）
        testList.add(new ArrayList<String>(Arrays.asList("000", "000", "000", "000", "000000",
            "0.0.0.1", "000000", "000000", "System Error", "2")));
        testList.add(new ArrayList<String>(Arrays.asList("000", "000", "000", "000", "000000",
            "0.0.0.1", "000000", "000000", "System Error", "2")));
        testList.add(new ArrayList<String>(Arrays.asList("000", "000", "000", "000", "000000",
            "0.0.0.1", "000000", "000000", "System Error", "2")));

        int cancelIndex = 0;
        for (List<String> test : testList) {
            if (test.get(9).equals("2")) {
                cancelIndex++;
            }

            try {
                C1005W01HeaderDto dto = new C1005W01HeaderDto();
                dto.setApkihoncd(test.get(0));
                dto.setShopcd(test.get(1));
                dto.setDnpyno(test.get(7));
                switch (cancelIndex) {
                    case 9:
                        dto.setSkyshootptkbn("2");
                        break;
                    case 10:
                        dto.setSkyshootptkbn("0");
                        break;
                    case 12:
                        dto.setSkyshootptkbn(null);
                        break;
                    default:
                        dto.setSkyshootptkbn("1");
                        break;
                }
                dto.setPrcskbn(test.get(9));

                DbManagementSession db = new DbManagementSession();
                db.setApkihoncd(test.get(2));
                db.setShopcd(test.get(3));
                db.setTnshacd(test.get(4));
                db.setIpAddress(test.get(5));

                this.bulkNyukinClearing(dto, test.get(6), db);
                fail("TEST Failed");
            } catch (BusinessException e) {
                assertEquals(e.getMessage(), test.get(8));
            } catch (Exception e) {
                assertEquals(test.get(8), "System Error");
            }
        }
    }

    /**
     * 入金登録テスト（異常系）
     * 
     * @throws Exception システム例外
     */
    @Test
    @Rollback
    void testCreateNyukinAbnomal() throws Exception {
        // テストパターン
        // 0:処理区分、1:メッセージ、2:ショップコード、3:今回消込金額、4:請求残高テーブルデータ生成フラグ、5:プラスのみフラグ、6:入金種別、7:消込残高
        List<List<String>> testList = new ArrayList<List<String>>();
        testList.add(Arrays.asList("1", "★★★ 請求先コード：000000が見つかりません。 ★★★", "X00", "10", "1", "0",
            null, null));
        testList.add(Arrays.asList("3", "★★★ 請求先コード：000000が見つかりません。 ★★★", "X00", "10", "1", "0",
            null, null));
        testList.add(Arrays.asList("2", "★★★ 請求先コード：000000が見つかりません。 ★★★", "X00", "10", "1", "0",
            null, null));
        testList.add(
            Arrays.asList("1", "★★★ 請求残高：1への消込が行えません。 ★★★", null, "10", "1", "1", null, "-100"));
        testList
            .add(Arrays.asList("1", "★★★ 請求残高：1への消込が行えません。 ★★★", null, "10", "0", "1", null, null));
        testList.add(
            Arrays.asList("1", "★★★ 請求残高：1への消込戻しが行えません。 ★★★", null, "-10", "1", "0", null, null));
        testList.add(Arrays.asList("3", "★★★ 請求残高テーブルの消込残高が変更されています。請求書番号：1 ★★★", null, "10", "1",
            "0", null, null));
        testList.add(Arrays.asList("2", "★★★ この伝票で消込を行った残高が他の伝票で戻されています。 ★★★", "000", "10", "1",
            "0", "31", null));

        // DB管理セッション情報生成
        DbManagementSession db = this.createDefaultDbManagementSession();
        for (List<String> test : testList) {
            try {
                final String shopcd = test.get(2);
                final String keshikomikin = test.get(3);
                final boolean isCreateS07001T = test.get(4).equals("1");
                final boolean isMinusOnly = keshikomikin.contains("-");
                final boolean isPlusOnly = test.get(5).equals("1");
                final String nyknkind = test.get(6);
                final String keshikomizd = test.get(7);

                // データ削除
                deleteData();

                // 入金入力ヘッダ情報Dto生成
                final C1005W01HeaderDto dto =
                    this.createDefaultC1005W01HeaderDto(test.get(0), 0L, null);

                // 消込対象明細Dto生成
                List<C1005W01MeisaiDto> meisaiDtoList;
                meisaiDtoList = new ArrayList<C1005W01MeisaiDto>();
                C1005W01MeisaiDto meisaiDto = new C1005W01MeisaiDto();
                meisaiDto.setKeshikomi("1");
                meisaiDto.setSkyshono("1");
                meisaiDto.setTokuisakicd("000000");
                meisaiDto.setKonkaikeshikomikin(Long.parseLong(keshikomikin));
                meisaiDtoList.add(meisaiDto);

                // 入金ワーク登録（入金伝票番号取得）
                String dnpyno = this.createNyukinWork(dto, meisaiDtoList, db);
                dto.setDnpyno(dnpyno);

                // データ生成
                this.createData(shopcd, dnpyno, isCreateS07001T, isMinusOnly, isPlusOnly, nyknkind,
                    keshikomizd, keshikomikin);

                log.debug("■■■■■■■■■■ " + test.toString() + " ■■■■■■■■■■");

                // 一括入金消込処理実行
                this.bulkNyukinClearing(dto, dnpyno, db);
                fail("TEST Failed");
            } catch (BusinessException e) {
                assertEquals(e.getMessage(), test.get(1));
            }
        }
    }

    /**
     * 入金登録テスト（（通常入金／入金取消）正常系）
     * 
     * @throws Exception システム例外
     */
    @Test
    @Rollback
    void testCreateNyukinNomalBase() throws Exception {
        // マスタデータ生成
        this.createMasterData("4");
        // DB管理セッション情報生成
        DbManagementSession db = this.createDefaultDbManagementSession();
        // 消込対象明細Dto生成
        List<C1005W01MeisaiDto> meisaiDtoList = this.createDefaultC1005W01MeisaiDto(false, 2);
        // 請求残高テーブルデータ生成
        int index = 0;
        for (C1005W01MeisaiDto meisaiDto : meisaiDtoList) {
            meisaiDto.setSkyshono(String.valueOf(index));
            createS07T001T(index, meisaiDto.getTokuisakicd(), meisaiDto.getKeshikomizd(), null);
            index++;
        }

        // 通常入金実行
        // ----------------------------------------------------------------
        C1005W01HeaderDto dto = this.execCreateNyukin(db, "1", 3000L, null, meisaiDtoList);
        String dnpyno = dto.getDnpyno();
        // 請求残高テーブルデータ検証
        equalsS07T001T(dto, db, meisaiDtoList, false);
        // 入金管理テーブルデータ検証
        equalsS10T003T(dto, dnpyno, db, false, null, 0L, null, null);
        // 入金明細テーブルデータ検証
        equalsS10T001T(dto, dnpyno, db, meisaiDtoList, null, false, null, false, false, null);
        // 入金消込履歴テーブルデータ検証
        equalsS10T004T(dto, dnpyno, db, meisaiDtoList, "10", false, null, null, null,
            new String[] {null});
        // 入金取込得意先ワークデータ検証
        equalsS10W002T();
        // 得意先マスタデータ検証
        equalsS02M013T(db, meisaiDtoList.get(1).getTokuisakicd(), "05");
        // 得意先実績テーブルデータ検証
        equalsS02M050T(db, meisaiDtoList.get(0).getTokuisakicd(), -1000);
        equalsS02M050T(db, meisaiDtoList.get(1).getTokuisakicd(), -2000);
        // 入金取込ワークデータ検証
        equalsS10W001T(db, dnpyno);

        // 入金取消実行
        // ----------------------------------------------------------------
        dto = this.execCreateNyukin(db, "2", 0L, dnpyno, null);
        // 請求残高テーブルデータ検証
        equalsS07T001T(dto, db, meisaiDtoList, true);
        // 入金管理テーブルデータ検証
        equalsS10T003T(dto, dto.getDnpyno(), db, true, dnpyno, 0L, -3000L, null);
        equalsS10T003T(dto, dnpyno, db, false, dto.getDnpyno(), 0L, 3000L, null);
        // 入金明細テーブルデータ検証
        equalsS10T001T(dto, dto.getDnpyno(), db, meisaiDtoList, dnpyno, true,
            dto.getSkyshootptkbn(), false, false, null);
        equalsS10T001T(dto, dnpyno, db, meisaiDtoList, dto.getDnpyno(), false,
            dto.getSkyshootptkbn(), false, false, null);
        // 入金消込履歴テーブルデータ検証
        equalsS10T004T(dto, dto.getDnpyno(), db, meisaiDtoList, "12", true, null, null, null,
            dnpyno);
        // 入金取込ワークデータ検証
        equalsS10W001T(db, dto.getDnpyno());
    }

    /**
     * 入金登録テスト（（過入金／過入金入金）正常系）
     * 
     * @throws Exception システム例外
     */
    @Test
    @Rollback
    void testCreateNyukinNomalKanyukin() throws Exception {
        // マスタデータ生成
        this.createMasterData(null);
        // DB管理セッション情報生成
        DbManagementSession db = this.createDefaultDbManagementSession();
        // 消込対象明細Dto生成
        List<C1005W01MeisaiDto> meisaiDtoList = this.createDefaultC1005W01MeisaiDto(false, 2);
        // （初回）請求残高テーブルデータ生成
        int skyshono = 1;
        for (C1005W01MeisaiDto meisaiDto : meisaiDtoList) {
            meisaiDto.setSkyshono(String.valueOf(skyshono));
            createS07T001T(skyshono, meisaiDto.getTokuisakicd(), meisaiDto.getKeshikomizd(), null);
            skyshono++;
        }

        // 過入金実行
        // ----------------------------------------------------------------
        C1005W01HeaderDto dto = this.execCreateNyukin(db, "1", 4000L, null, meisaiDtoList);
        String dnpyno = dto.getDnpyno();
        // 請求残高テーブルデータ検証
        equalsS07T001T(dto, db, meisaiDtoList, false);
        // 入金管理テーブルデータ検証
        equalsS10T003T(dto, dnpyno, db, false, null, 1000L, null, null);
        // 入金明細テーブルデータ検証
        equalsS10T001T(dto, dnpyno, db, meisaiDtoList, null, false, null, true, false, null);
        // 入金消込履歴テーブルデータ検証
        equalsS10T004T(dto, dnpyno, db, meisaiDtoList, "10", false, null, null, null,
            new String[] {null});
        // 請求先マスタデータ検証
        equalsS02M017T(db, "000000", 1000L, 0L);

        // （次回）請求残高テーブルデータ生成
        int index = 0;
        for (C1005W01MeisaiDto meisaiDto : meisaiDtoList) {
            meisaiDto.setSkyshono(String.valueOf(skyshono));
            meisaiDto.setKonkaikeshikomikin(index == 0 ? meisaiDto.getKonkaikeshikomikin() : 0L);
            meisaiDto.setKeshikomizd(index == 0 ? meisaiDto.getKonkaikeshikomikin() : 0L);
            createS07T001T(skyshono, meisaiDto.getTokuisakicd(), (index == 0 ? 1000L : 0L), null);
            skyshono++;
            index++;
        }

        // 過入金入金実行
        // ----------------------------------------------------------------
        meisaiDtoList.remove(1);
        dto = this.execCreateNyukin(db, "1", 0L, dnpyno, meisaiDtoList);
        // 請求残高テーブルデータ検証
        equalsS07T001T(dto, db, meisaiDtoList, false);
        // 入金管理テーブルデータ検証
        equalsS10T003T(dto, dnpyno, db, false, null, 0L, 4000L, null);
        // 入金消込履歴テーブルデータ検証
        equalsS10T004T(dto, dnpyno, db, meisaiDtoList, "10", false, "30", 4, 3,
            new String[] {null});
        // 入金明細テーブルデータ検証
        meisaiDtoList = new ArrayList<C1005W01MeisaiDto>();
        C1005W01MeisaiDto meisaiDto1 = new C1005W01MeisaiDto();
        meisaiDto1.setTokuisakicd("000000");
        meisaiDto1.setKonkaikeshikomikin(1000L);
        meisaiDtoList.add(meisaiDto1);
        C1005W01MeisaiDto meisaiDto2 = new C1005W01MeisaiDto();
        meisaiDto2.setTokuisakicd("000001");
        meisaiDto2.setKonkaikeshikomikin(3000L);
        meisaiDto2.setKeshikomizumikin(2000L);
        meisaiDtoList.add(meisaiDto2);
        C1005W01MeisaiDto meisaiDto3 = new C1005W01MeisaiDto();
        meisaiDto3.setTokuisakicd("000001");
        meisaiDto3.setKonkaikeshikomikin(-1000L);
        meisaiDto3.setKeshikomizumikin(0L);
        meisaiDtoList.add(meisaiDto3);
        C1005W01MeisaiDto meisaiDto4 = new C1005W01MeisaiDto();
        meisaiDto4.setTokuisakicd("000000");
        meisaiDto4.setKonkaikeshikomikin(1000L);
        meisaiDtoList.add(meisaiDto4);
        equalsS10T001T(dto, dnpyno, db, meisaiDtoList, null, false, null, false, true, null);
    }

    /**
     * 入金登録テスト（（過入金入金取消）正常系）
     * 
     * @throws Exception システム例外
     */
    @Test
    @Rollback
    void testCreateNyukinNomalKanyukinCancel() throws Exception {
        // マスタデータ生成
        this.createMasterData(null);
        // DB管理セッション情報生成
        DbManagementSession db = this.createDefaultDbManagementSession();
        // 消込対象明細Dto生成
        List<C1005W01MeisaiDto> meisaiDtoList = this.createDefaultC1005W01MeisaiDto(false, 2);
        // （初回）請求残高テーブルデータ生成
        int skyshono = 1;
        for (C1005W01MeisaiDto meisaiDto : meisaiDtoList) {
            meisaiDto.setSkyshono(String.valueOf(skyshono));
            createS07T001T(skyshono, meisaiDto.getTokuisakicd(), meisaiDto.getKeshikomizd(), null);
            skyshono++;
        }

        // 過入金実行
        // ----------------------------------------------------------------
        C1005W01HeaderDto dto = this.execCreateNyukin(db, "1", 5000L, null, meisaiDtoList);
        String beforeDnpyno = dto.getDnpyno();
        // 入金管理テーブルデータ検証
        equalsS10T003T(dto, beforeDnpyno, db, false, null, 2000L, null, null);
        // 請求先マスタデータ検証
        equalsS02M017T(db, "000000", 2000L, 0L);

        // （次回）請求残高テーブルデータ生成
        int index = 0;
        for (C1005W01MeisaiDto meisaiDto : meisaiDtoList) {
            meisaiDto.setSkyshono(String.valueOf(skyshono));
            meisaiDto.setKonkaikeshikomikin(index == 0 ? meisaiDto.getKonkaikeshikomikin() : 0L);
            meisaiDto.setKeshikomizd(index == 0 ? meisaiDto.getKonkaikeshikomikin() : 0L);
            createS07T001T(skyshono, meisaiDto.getTokuisakicd(), (index == 0 ? 1000L : 0L), null);
            skyshono++;
            index++;
        }

        // 過入金入金実行
        // ----------------------------------------------------------------
        meisaiDtoList.remove(1);
        dto = this.execCreateNyukin(db, "1", 1000L, beforeDnpyno, meisaiDtoList);
        // 入金管理テーブルデータ検証（新）
        equalsS10T003T(dto, dto.getDnpyno(), db, false, null, 1000L, null, null);
        // 入金明細テーブルデータ検証（新）
        meisaiDtoList.get(0).setTokuisakicd("000001");
        equalsS10T001T(dto, dto.getDnpyno(), db, meisaiDtoList, null, false, null, true, false, 0L);
        // 入金管理テーブルデータ検証（旧）
        equalsS10T003T(dto, beforeDnpyno, db, false, null, 1000L, 5000L, null);
        // 入金明細テーブルデータ検証（旧）
        meisaiDtoList = new ArrayList<C1005W01MeisaiDto>();
        C1005W01MeisaiDto meisaiDto1 = new C1005W01MeisaiDto();
        meisaiDto1.setSkyshono("1");
        meisaiDto1.setTokuisakicd("000000");
        meisaiDto1.setKonkaikeshikomikin(1000L);
        meisaiDtoList.add(meisaiDto1);
        C1005W01MeisaiDto meisaiDto2 = new C1005W01MeisaiDto();
        meisaiDto2.setSkyshono("2");
        meisaiDto2.setTokuisakicd("000001");
        meisaiDto2.setKonkaikeshikomikin(4000L);
        meisaiDto2.setKeshikomizumikin(2000L);
        meisaiDto2.setUpdkeshikomikin(1000L);
        meisaiDtoList.add(meisaiDto2);
        C1005W01MeisaiDto meisaiDto3 = new C1005W01MeisaiDto();
        meisaiDto3.setTokuisakicd("000001");
        meisaiDto3.setKonkaikeshikomikin(-1000L);
        meisaiDto3.setKeshikomizumikin(0L);
        meisaiDtoList.add(meisaiDto3);
        C1005W01MeisaiDto meisaiDto4 = new C1005W01MeisaiDto();
        meisaiDto4.setSkyshono("3");
        meisaiDto4.setTokuisakicd("000000");
        meisaiDto4.setKonkaikeshikomikin(1000L);
        meisaiDtoList.add(meisaiDto4);
        equalsS10T001T(dto, beforeDnpyno, db, meisaiDtoList, null, false, null, false, true, null);

        // 入金取消実行
        // ----------------------------------------------------------------
        dto = this.execCreateNyukin(db, "2", 0L, beforeDnpyno, null);
        // 入金管理テーブルデータ検証
        equalsS10T003T(dto, dto.getDnpyno(), db, true, beforeDnpyno, -1000L, -5000L, "0");
        equalsS10T003T(dto, beforeDnpyno, db, false, dto.getDnpyno(), 0L, 5000L, null);
        // 入金明細テーブルデータ検証（旧）
        equalsS10T001T(dto, beforeDnpyno, db, meisaiDtoList, dto.getDnpyno(), false,
            dto.getSkyshootptkbn(), false, true, null);
        // 入金明細テーブルデータ検証（新）
        meisaiDtoList.get(1).setUpdkeshikomikin(-1000L);
        meisaiDtoList.get(2).setUpdkeshikomikin(1000L);
        meisaiDtoList.get(2).setTokuisakinm("X");
        meisaiDtoList.get(3).setTokuisakinm("X");
        equalsS10T001T(dto, dto.getDnpyno(), db, meisaiDtoList, beforeDnpyno, true,
            dto.getSkyshootptkbn(), false, true, null);
        // 入金消込履歴テーブルデータ検証
        meisaiDtoList.get(0).setTokuisakinm("1");
        meisaiDtoList.get(1).setTokuisakinm("2");
        meisaiDtoList.get(2).setTokuisakinm("3");
        meisaiDtoList.get(3).setTokuisakinm("4");
        meisaiDtoList.get(3).setSkydydmdd("30");
        meisaiDtoList.remove(2);
        equalsS10T004T(dto, dto.getDnpyno(), db, meisaiDtoList, "12", true, null, null, null,
            beforeDnpyno);
    }

    /**
     * 入金登録テスト（（入金後マイナス入金）正常系）
     * 
     * @throws Exception システム例外
     */
    @Test
    @Rollback
    void testCreateNyukinNomalNyukinAfterMinusNyukin() throws Exception {
        // マスタデータ生成
        this.createMasterData(null);
        // DB管理セッション情報生成
        DbManagementSession db = this.createDefaultDbManagementSession();
        // 消込対象明細Dto生成
        List<C1005W01MeisaiDto> meisaiDtoList = this.createDefaultC1005W01MeisaiDto(false, 2);
        // （初回）請求残高テーブルデータ生成
        int skyshono = 1;
        for (C1005W01MeisaiDto meisaiDto : meisaiDtoList) {
            meisaiDto.setSkyshono(String.valueOf(skyshono));
            createS07T001T(skyshono, meisaiDto.getTokuisakicd(), meisaiDto.getKeshikomizd(), null);
            skyshono++;
        }

        // 入金実行
        // ----------------------------------------------------------------
        C1005W01HeaderDto dto;
        dto = this.execCreateNyukin(db, "1", 3000L, null, meisaiDtoList);

        // （次回）請求残高テーブルデータ生成
        int index = 0;
        for (C1005W01MeisaiDto meisaiDto : meisaiDtoList) {
            meisaiDto.setSkyshono(String.valueOf(skyshono));
            meisaiDto.setKonkaikeshikomikin(index == 0 ? meisaiDto.getKonkaikeshikomikin() : 0L);
            meisaiDto.setKeshikomizd(index == 0 ? meisaiDto.getKonkaikeshikomikin() : 0L);
            createS07T001T(skyshono, meisaiDto.getTokuisakicd(), (index == 0 ? 1000L : 0L), null);
            skyshono++;
            index++;
        }

        // マイナス入金実行
        // ----------------------------------------------------------------
        meisaiDtoList = this.createDefaultC1005W01MeisaiDto(true, 2);
        meisaiDtoList.get(0).setSkyshono("1");
        meisaiDtoList.get(1).setSkyshono("2");
        dto = this.execCreateNyukin(db, "1", -3000L, null, meisaiDtoList);
        // 請求残高テーブルデータ検証
        meisaiDtoList = new ArrayList<C1005W01MeisaiDto>();
        C1005W01MeisaiDto meisaiDto1 = new C1005W01MeisaiDto();
        meisaiDto1.setSkyshono("1");
        meisaiDto1.setTokuisakicd("000000");
        meisaiDto1.setKonkaikeshikomikin(0L);
        meisaiDto1.setKeshikomizd(1000L);
        meisaiDtoList.add(meisaiDto1);
        C1005W01MeisaiDto meisaiDto2 = new C1005W01MeisaiDto();
        meisaiDto2.setSkyshono("2");
        meisaiDto2.setTokuisakicd("000001");
        meisaiDto2.setKonkaikeshikomikin(0L);
        meisaiDto2.setKeshikomizd(2000L);
        meisaiDtoList.add(meisaiDto2);
        equalsS07T001T(dto, db, meisaiDtoList, false);
        // 入金管理テーブルデータ検証
        equalsS10T003T(dto, dto.getDnpyno(), db, false, null, 0L, null, null);
        // 入金明細テーブルデータ検証
        meisaiDtoList.get(0).setKonkaikeshikomikin(-1000L);
        meisaiDtoList.get(1).setKonkaikeshikomikin(-2000L);
        equalsS10T001T(dto, dto.getDnpyno(), db, meisaiDtoList, null, false, null, false, false,
            null);
        // 入金消込履歴テーブルデータ検証
        equalsS10T004T(dto, dto.getDnpyno(), db, meisaiDtoList, "10", false, null, null, null,
            new String[] {null});
    }

    /**
     * 入金登録テスト（（マイナス過入金を入金で相殺）正常系）
     * 
     * @throws Exception システム例外
     */
    @Test
    @Rollback
    void testCreateNyukinNomalMinusKanyukinSosai() throws Exception {
        // マスタデータ生成
        this.createMasterData(null);
        // DB管理セッション情報生成
        final DbManagementSession db = this.createDefaultDbManagementSession();
        // 消込対象明細Dto生成
        List<C1005W01MeisaiDto> meisaiDtoList = this.createDefaultC1005W01MeisaiDto(false, 2);
        meisaiDtoList.get(0).setSkyshono("3");
        meisaiDtoList.get(1).setSkyshono("2");

        // 請求残高テーブルデータ生成
        createS07T001T(1, "000000", 1000L, null);
        createS07T001T(2, "000001", 2000L, null);
        createS07T001T(3, "000000", 1000L, null);
        createS07T001T(4, "000001", 0L, null);

        // 入金実行（1回目）
        // ----------------------------------------------------------------
        C1005W01HeaderDto dto = this.execCreateNyukin(db, "1", 3000L, null, meisaiDtoList);
        final String beforeDnpyno = dto.getDnpyno();
        // 請求残高テーブルデータ検証
        equalsS07T001T(dto, db, meisaiDtoList, false);
        // 入金管理テーブルデータ検証
        equalsS10T003T(dto, dto.getDnpyno(), db, false, null, 0L, null, null);
        // 入金明細テーブルデータ検証
        List<C1005W01MeisaiDto> tmpList = new ArrayList<C1005W01MeisaiDto>();
        tmpList.add(meisaiDtoList.get(1));
        tmpList.add(meisaiDtoList.get(0));
        equalsS10T001T(dto, dto.getDnpyno(), db, tmpList, null, false, null, false, false, null);
        // 入金消込履歴テーブルデータ検証
        equalsS10T004T(dto, dto.getDnpyno(), db, tmpList, "10", false, null, null, null,
            new String[] {null});

        // 入金実行（2回目）
        // ----------------------------------------------------------------
        meisaiDtoList.remove(1);
        meisaiDtoList.get(0).setSkyshono("1");
        dto = this.execCreateNyukin(db, "1", 1000L, null, meisaiDtoList);
        // 請求残高テーブルデータ検証
        equalsS07T001T(dto, db, meisaiDtoList, false);
        // 入金管理テーブルデータ検証
        equalsS10T003T(dto, dto.getDnpyno(), db, false, null, 0L, null, null);
        // 入金明細テーブルデータ検証
        equalsS10T001T(dto, dto.getDnpyno(), db, meisaiDtoList, null, false, null, false, false,
            null);
        // 入金消込履歴テーブルデータ検証
        equalsS10T004T(dto, dto.getDnpyno(), db, meisaiDtoList, "10", false, null, null, null,
            new String[] {null});

        // 入金実行（3回目）
        // ----------------------------------------------------------------
        meisaiDtoList.get(0).setSkyshono("3");
        meisaiDtoList.get(0).setKonkaikeshikomikin(-1000L);
        meisaiDtoList.get(0).setKeshikomizd(0L);
        dto = this.execCreateNyukin(db, "1", -1000L, null, meisaiDtoList);
        // 入金管理テーブルデータ検証
        equalsS10T003T(dto, dto.getDnpyno(), db, false, null, 0L, null, null);
        // 入金明細テーブルデータ検証
        equalsS10T001T(dto, dto.getDnpyno(), db, meisaiDtoList, null, false, null, false, false,
            null);
        // 入金消込履歴テーブルデータ検証
        equalsS10T004T(dto, dto.getDnpyno(), db, meisaiDtoList, "10", false, null, null, null,
            new String[] {null});
        // 請求残高テーブルデータ検証
        meisaiDtoList.get(0).setKonkaikeshikomikin(1000L);
        equalsS07T001T(dto, db, meisaiDtoList, true);

        // 入金取消実行
        // ----------------------------------------------------------------
        try {
            dto = this.execCreateNyukin(db, "2", 0L, beforeDnpyno, null);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "★★★ この伝票で消込を行った残高が他の伝票で戻されています。 ★★★");
        }
        // ＜後続処理を実行可能なテストパターンでなくなったためコメントアウト＞
        // 今後、本パターンテストが必要となった際にデータの見直しなどを行い、再実行できるようにコメントアウトとしています。
        // final String cancelDnpyno = dto.getDnpyno();
        // // 入金管理テーブルデータ検証
        // equalsS10T003T(dto, dto.getDnpyno(), db, true, beforeDnpyno, -1000L, -3000L);
        // equalsS10T003T(dto, beforeDnpyno, db, false, dto.getDnpyno(), 0L, 3000L);
        // // 請求残高テーブルデータ検証
        // meisaiDtoList = this.createDefaultC1005W01MeisaiDto(false, 2);
        // meisaiDtoList.get(0).setSkyshono("3");
        // meisaiDtoList.get(1).setSkyshono("2");
        // equalsS07T001T(dto, db, meisaiDtoList, true);
        // // 入金明細テーブルデータ検証（旧）
        // tmpList = new ArrayList<C1005W01MeisaiDto>();
        // tmpList.add(meisaiDtoList.get(1));
        // tmpList.add(meisaiDtoList.get(0));
        // equalsS10T001T(dto, beforeDnpyno, db, tmpList, dto.getDnpyno(), false,
        // dto.getSkyshootptkbn(), false, true, null);
        // // 入金明細テーブルデータ検証（新）
        // tmpList.get(1).setKeshikomizumikin(0L);
        // equalsS10T001T(dto, dto.getDnpyno(), db, tmpList, beforeDnpyno, true,
        // dto.getSkyshootptkbn(), false, false, null);
        // // 入金消込履歴テーブルデータ検証
        // tmpList.remove(1);
        // equalsS10T004T(dto, dto.getDnpyno(), db, tmpList, "12", true, null, null, null,
        // beforeDnpyno);
        //
        // // 入金実行（4回目）
        // // ----------------------------------------------------------------
        // meisaiDtoList.remove(0);
        // dto = this.execCreateNyukin(db, "1", 3000L, null, meisaiDtoList);
        // // 請求残高テーブルデータ検証
        // meisaiDtoList = this.createDefaultC1005W01MeisaiDto(false, 2);
        // meisaiDtoList.get(0).setSkyshono("3");
        // meisaiDtoList.get(1).setSkyshono("2");
        // tmpList = new ArrayList<C1005W01MeisaiDto>();
        // tmpList.add(meisaiDtoList.get(1));
        // equalsS07T001T(dto, db, tmpList, false);
        // // 入金管理テーブルデータ検証
        // equalsS10T003T(dto, cancelDnpyno, db, true, beforeDnpyno, 0L, null);
        // equalsS10T003T(dto, dto.getDnpyno(), db, false, null, 0L, null);
        // // 入金明細テーブルデータ検証（取消）
        // tmpList = new ArrayList<C1005W01MeisaiDto>();
        // tmpList.add(meisaiDtoList.get(1));
        // tmpList.add(meisaiDtoList.get(0));
        // tmpList.get(1).setKeshikomizumikin(0L);
        // C1005W01MeisaiDto meisaiDto3 = new C1005W01MeisaiDto();
        // meisaiDto3.setTokuisakicd("000000");
        // meisaiDto3.setKonkaikeshikomikin(0L);
        // meisaiDto3.setKeshikomizumikin(1000L);
        // meisaiDto3.setTouurikin(0L);
        // tmpList.add(meisaiDto3);
        // equalsS10T001T(dto, cancelDnpyno, db, tmpList, beforeDnpyno, true, null, false, true,
        // null);
        // // 入金明細テーブルデータ検証（新）
        // meisaiDtoList.get(0).setKeshikomizumikin(1000L);
        // meisaiDtoList.get(0).setSkydydmdd("1"); // 請求書出力区分とする
        // equalsS10T001T(dto, dto.getDnpyno(), db, meisaiDtoList, null, false, null, false, false,
        // null);
        // // 入金消込履歴テーブルデータ検証（新）
        // meisaiDtoList.get(0).setSkyshono(null);
        // meisaiDtoList.get(0).setSkydydmdd("30");
        // equalsS10T004T(dto, dto.getDnpyno(), db, meisaiDtoList, "10", false, null, null, null,
        // null);
        // // 入金消込履歴テーブルデータ検証（旧）
        // meisaiDtoList.remove(1);
        // equalsS10T004T(dto, cancelDnpyno, db, meisaiDtoList, "10", true, null, 3, 2,
        // beforeDnpyno);
    }

    /**
     * 入金登録テスト（（過入金をマイナス入金で相殺／過入金をマイナス入金で相殺後取消）正常系）
     * 
     * @throws Exception システム例外
     */
    @Test
    @Rollback
    void testCreateNyukinNomalKanyukinMinusNyukinSosai() throws Exception {
        // マスタデータ生成
        this.createMasterData(null);
        // DB管理セッション情報生成
        final DbManagementSession db = this.createDefaultDbManagementSession();
        // 消込対象明細Dto生成
        List<C1005W01MeisaiDto> meisaiDtoList = this.createDefaultC1005W01MeisaiDto(false, 2);
        meisaiDtoList.get(0).setSkyshono("1");
        meisaiDtoList.get(1).setSkyshono("2");

        // 請求残高テーブルデータ生成
        createS07T001T(1, "000000", 1000L, null);
        createS07T001T(2, "000001", 2000L, null);

        // 過入金実行
        // ----------------------------------------------------------------
        C1005W01HeaderDto dto = this.execCreateNyukin(db, "1", 5000L, null, meisaiDtoList);
        final String beforeDnpyno = dto.getDnpyno();
        // 請求残高テーブルデータ検証
        equalsS07T001T(dto, db, meisaiDtoList, false);
        // 入金管理テーブルデータ検証
        equalsS10T003T(dto, dto.getDnpyno(), db, false, null, 2000L, null, null);
        // 入金明細テーブルデータ検証
        meisaiDtoList.get(1).setKonkaikeshikomikin(4000L);
        meisaiDtoList.get(1).setKeshikomizumikin(2000L);
        equalsS10T001T(dto, dto.getDnpyno(), db, meisaiDtoList, null, false, null, false, false,
            null);
        // 入金消込履歴テーブルデータ検証
        equalsS10T004T(dto, dto.getDnpyno(), db, meisaiDtoList, "10", false, null, null, null,
            new String[] {null});

        // 請求残高テーブルデータ生成
        createS07T001T(3, "000000", 1000L, null);
        createS07T001T(4, "000001", 0L, null);

        // マイナス入金実行
        // ----------------------------------------------------------------
        meisaiDtoList.remove(0);
        meisaiDtoList.get(0).setKonkaikeshikomikin(-1000L);
        dto = this.execCreateNyukin(db, "1", -3000L, null, meisaiDtoList);
        final String minusDnpyno = dto.getDnpyno();
        // 請求残高テーブルデータ検証
        meisaiDtoList.get(0).setKonkaikeshikomikin(1000L);
        meisaiDtoList.get(0).setKeshikomizd(2000L);
        meisaiDtoList.get(0).setKeshikomizumikin(null);
        equalsS07T001T(dto, db, meisaiDtoList, false);
        // 入金管理テーブルデータ検証
        equalsS10T003T(dto, dto.getDnpyno(), db, false, null, 0L, null, null);
        equalsS10T003T(dto, beforeDnpyno, db, false, null, 0L, 5000L, null);
        // 入金明細テーブルデータ検証（旧）
        List<C1005W01MeisaiDto> tmpList = this.createDefaultC1005W01MeisaiDto(false, 2);
        tmpList.add(meisaiDtoList.get(0));
        tmpList.get(1).setKonkaikeshikomikin(4000L);
        tmpList.get(1).setKeshikomizumikin(2000L);
        tmpList.get(2).setKonkaikeshikomikin(0L);
        tmpList.get(2).setKeshikomizumikin(2000L);
        equalsS10T001T(dto, beforeDnpyno, db, tmpList, null, false, null, false, true, null);
        // 入金明細テーブルデータ検証（新）
        tmpList.remove(2);
        tmpList.get(0).setSkydydmdd("1");
        tmpList.get(0).setTokuisakicd("000001");
        tmpList.get(0).setKonkaikeshikomikin(-2000L);
        tmpList.get(0).setKeshikomizumikin(-2000L);
        tmpList.get(1).setKonkaikeshikomikin(-1000L);
        tmpList.get(1).setKeshikomizumikin(-1000L);
        equalsS10T001T(dto, dto.getDnpyno(), db, tmpList, null, false, null, false, false, null);
        // 入金消込履歴テーブルデータ検証（新）
        tmpList.get(0).setSkyshono(null);
        tmpList.get(0).setSkydydmdd("30");
        tmpList.get(1).setSkyshono("2");
        equalsS10T004T(dto, dto.getDnpyno(), db, List.of(tmpList.get(0)), "10", false, null, 1, 1,
            null, "0");
        equalsS10T004T(dto, dto.getDnpyno(), db, List.of(tmpList.get(1)), "10", false, null, 2, 2,
            null, "1");
        // 入金消込履歴テーブルデータ検証（旧）
        tmpList.remove(1);
        tmpList.get(0).setKeshikomizumikin(2000L);
        equalsS10T004T(dto, beforeDnpyno, db, tmpList, "10", false, null, 3, 3, null, "0");

        // 入金取消実行
        // ----------------------------------------------------------------
        dto = this.execCreateNyukin(db, "2", 0L, minusDnpyno, null);
        final String cancelDnpyno = dto.getDnpyno();
        // 請求残高テーブルデータ検証
        tmpList = this.createDefaultC1005W01MeisaiDto(false, 2);
        tmpList.remove(0);
        tmpList.get(0).setSkyshono("2");
        equalsS07T001T(dto, db, tmpList, false);
        // 入金管理テーブルデータ検証
        equalsS10T003T(dto, dto.getDnpyno(), db, false, minusDnpyno, 2000L, 3000L, null);
        equalsS10T003T(dto, minusDnpyno, db, true, dto.getDnpyno(), 0L, -3000L, null);
        // 入金明細テーブルデータ検証（新）
        tmpList = new ArrayList<C1005W01MeisaiDto>();
        C1005W01MeisaiDto tmp1 = new C1005W01MeisaiDto();
        tmp1.setTokuisakicd("000001");
        tmp1.setKonkaikeshikomikin(-2000L);
        tmp1.setKeshikomizumikin(0L);
        tmpList.add(tmp1);
        C1005W01MeisaiDto tmp2 = new C1005W01MeisaiDto();
        tmp2.setTokuisakicd("000001");
        tmp2.setKonkaikeshikomikin(-1000L);
        tmp2.setSkyshono("2");
        tmpList.add(tmp2);
        equalsS10T001T(dto, dto.getDnpyno(), db, tmpList, minusDnpyno, true, null, false, false,
            null);
        // 入金明細テーブルデータ検証（旧）
        tmpList.get(0).setKeshikomizumikin(null);
        equalsS10T001T(dto, minusDnpyno, db, tmpList, dto.getDnpyno(), false,
            dto.getSkyshootptkbn(), false, false, null);
        // 入金消込履歴テーブルデータ検証（新）
        tmpList.remove(0);
        equalsS10T004T(dto, dto.getDnpyno(), db, tmpList, "12", true, null, 2, 1, minusDnpyno);

        // 過入金入金実行
        // ----------------------------------------------------------------
        meisaiDtoList = this.createDefaultC1005W01MeisaiDto(false, 2);
        meisaiDtoList.get(0).setSkyshono("3");
        meisaiDtoList.remove(1);
        dto = this.execCreateNyukin(db, "1", 0L, cancelDnpyno, meisaiDtoList);
        // 請求残高テーブルデータ検証
        equalsS07T001T(dto, db, meisaiDtoList, false);
        // 入金管理テーブルデータ検証
        equalsS10T003T(dto, cancelDnpyno, db, false, minusDnpyno, 1000L, 3000L, null);
        // 入金消込履歴テーブルデータ検証
        equalsS10T004T(dto, cancelDnpyno, db, meisaiDtoList, "10", false, "30", 4, 2, minusDnpyno);
        // 入金明細テーブルデータ検証
        meisaiDtoList = new ArrayList<C1005W01MeisaiDto>();
        C1005W01MeisaiDto meisaiDto1 = new C1005W01MeisaiDto();
        meisaiDto1.setTokuisakicd("000001");
        meisaiDto1.setKonkaikeshikomikin(2000L);
        meisaiDto1.setKeshikomizumikin(0L);
        meisaiDto1.setUpdkeshikomikin(1000L);
        meisaiDtoList.add(meisaiDto1);
        C1005W01MeisaiDto meisaiDto2 = new C1005W01MeisaiDto();
        meisaiDto2.setTokuisakicd("000001");
        meisaiDto2.setKonkaikeshikomikin(1000L);
        meisaiDtoList.add(meisaiDto2);
        C1005W01MeisaiDto meisaiDto3 = new C1005W01MeisaiDto();
        meisaiDto3.setTokuisakicd("000001");
        meisaiDto3.setKonkaikeshikomikin(-1000L);
        meisaiDto3.setKeshikomizumikin(0L);
        meisaiDtoList.add(meisaiDto3);
        C1005W01MeisaiDto meisaiDto4 = new C1005W01MeisaiDto();
        meisaiDto4.setTokuisakicd("000000");
        meisaiDto4.setKonkaikeshikomikin(1000L);
        meisaiDtoList.add(meisaiDto4);
        equalsS10T001T(dto, cancelDnpyno, db, meisaiDtoList, minusDnpyno, false,
            dto.getSkyshootptkbn(), false, true, null);
    }

    /**
     * 入金登録テスト（（過入金をマイナス入金で相殺後元を取消）正常系）
     * 
     * @throws Exception システム例外
     */
    @Test
    @Rollback
    void testCreateNyukinNomalKanyukinMinusNyukinSosaiMotoCancel() throws Exception {
        // マスタデータ生成
        this.createMasterData(null);
        // DB管理セッション情報生成
        final DbManagementSession db = this.createDefaultDbManagementSession();
        // 消込対象明細Dto生成
        List<C1005W01MeisaiDto> dtoList = new ArrayList<C1005W01MeisaiDto>();
        dtoList.add(this.createMeisaiDto(0L, 3888L, 3888L, -1L, -1L, 1L));
        dtoList.add(this.createMeisaiDto(1L, 6480L, 6480L, -1L, -1L, 2L));

        // 請求残高テーブルデータ生成（1回目）
        // **************************************
        createS07T001T(1, "000000", 3888L, null);
        createS07T001T(2, "000001", 6480L, null);

        // 過入金実行
        // ----------------------------------------------------------------
        C1005W01HeaderDto dto = this.execCreateNyukin(db, "1", 15000L, null, dtoList);
        final String dnpyno1 = dto.getDnpyno();
        // 請求残高テーブルデータ検証
        equalsS07T001T(dto, db, dtoList, false);
        // 入金管理テーブルデータ検証
        equalsS10T003T(dto, dnpyno1, db, false, null, 4632L, null, null);
        // 入金明細テーブルデータ検証
        dtoList.get(1).setKonkaikeshikomikin(11112L);
        dtoList.get(1).setKeshikomizumikin(6480L);
        equalsS10T001T(dto, dnpyno1, db, dtoList, null, false, null, false, false, null);
        // 入金消込履歴テーブルデータ検証
        equalsS10T004T(dto, dnpyno1, db, dtoList, "10", false, null, null, null,
            new String[] {null});

        // 請求残高テーブルデータ生成（2回目）
        // **************************************
        createS07T001T(3, "000000", 3888L, null);
        createS07T001T(4, "000001", 0L, null);

        // マイナス入金実行
        // ----------------------------------------------------------------
        dtoList = List.of(createMeisaiDto(0L, 1632L, 1632L, -1L, -1L, 3L));
        dto = this.execCreateNyukin(db, "1", -3000L, null, dtoList);
        final String dnpyno2 = dto.getDnpyno();
        // 請求残高テーブルデータ検証
        dtoList.get(0).setKeshikomizd(3888L);
        equalsS07T001T(dto, db, dtoList, false);

        // <<<<< 1番目データ検証 >>>>>
        // 入金管理テーブル
        equalsS10T003T(dto, dnpyno1, db, false, null, 0L, 15000L, null);
        // 入金明細テーブル
        dtoList = new ArrayList<C1005W01MeisaiDto>();
        dtoList.add(createMeisaiDto(0L, 3888L, 3888L, -1L, -1L, 1L));
        dtoList.add(createMeisaiDto(1L, 11112L, 6480L, -1L, -1L, 2L, 6480L));
        dtoList.add(createMeisaiDto(1L, 0L, 3000L, -1L, -1L, -1L, 3000L));
        dtoList.add(createMeisaiDto(1L, -1632L, 0L, -1L, -1L, -1L, 0L));
        dtoList.add(createMeisaiDto(0L, 1632L, 1632L, -1L, -1L, 3L, 1632L));
        equalsS10T001T(dto, dnpyno1, db, dtoList, null, false, null, false, true, null);
        // 入金消込履歴テーブル
        dtoList.get(2).setSkydydmdd("30");
        dtoList.get(4).setSkydydmdd("30");
        equalsS10T004T(dto, dnpyno1, db, List.of(dtoList.get(2)), "10", false, null, 3, 3, null,
            "0");
        equalsS10T004T(dto, dnpyno1, db, List.of(dtoList.get(4)), "10", false, null, 5, 4, null,
            "1");

        // <<<<< 2番目データ検証 >>>>>
        // 入金管理テーブル
        equalsS10T003T(dto, dnpyno2, db, false, null, 0L, null, null);
        // 入金明細テーブル
        dtoList = List.of(createMeisaiDto(1L, -3000L, -3000L, 1L));
        equalsS10T001T(dto, dto.getDnpyno(), db, dtoList, null, false, null, false, false, null);
        // 入金消込履歴テーブル
        dtoList.get(0).setSkydydmdd("30");
        equalsS10T004T(dto, dto.getDnpyno(), db, dtoList, "10", false, null, 1, 1, null, "0");

        // 入金取消実行
        // ----------------------------------------------------------------
        dto = this.execCreateNyukin(db, "2", 0L, dnpyno1, null);
        final String dnpyno3 = dto.getDnpyno();
        // 請求残高テーブルデータ検証
        dtoList = new ArrayList<C1005W01MeisaiDto>();
        dtoList.add(createMeisaiDto(0L, 0L, 3888L, -1L, -1L, 1L));
        dtoList.add(createMeisaiDto(1L, 0L, 6480L, -1L, -1L, 2L));
        equalsS07T001T(dto, db, dtoList, false);

        // <<<<< 1番目データ検証 >>>>>
        // 入金管理テーブル
        equalsS10T003T(dto, dnpyno1, db, true, dnpyno3, 0L, 15000L, null);
        // 入金明細テーブル
        dtoList = new ArrayList<C1005W01MeisaiDto>();
        dtoList.add(createMeisaiDto(0L, 3888L, 3888L, -1L, -1L, 1L));
        dtoList.add(createMeisaiDto(1L, 11112L, 6480L, -1L, -1L, 2L, 6480L));
        dtoList.add(createMeisaiDto(1L, 0L, 3000L, -1L, -1L, -1L, 3000L));
        dtoList.add(createMeisaiDto(1L, -1632L, 0L, -1L, -1L, -1L, 0L));
        dtoList.add(createMeisaiDto(0L, 1632L, 1632L, -1L, -1L, 3L, 1632L));
        equalsS10T001T(dto, dnpyno1, db, dtoList, dnpyno3, false, "1", false, true, null);

        // <<<<< 3番目データ検証 >>>>>
        // 入金管理テーブル
        equalsS10T003T(dto, dnpyno3, db, false, dnpyno1, -3000L, -15000L, "1");
        // 入金明細テーブル
        dtoList = new ArrayList<C1005W01MeisaiDto>();
        dtoList.add(createMeisaiDto(0L, 3888L, 0L, -1L, -1L, 1L));
        dtoList.add(createMeisaiDto(1L, 11112L, 0L, -1L, -1L, 2L, 6480L));
        dtoList.add(createMeisaiDto(1L, -3000L, 0L, -1L, -2L, -1L, 0L));
        dtoList.add(createMeisaiDto(1L, -1632L, 0L, -1L, -2L, -1L, 0L));
        dtoList.add(createMeisaiDto(0L, 1632L, 0L, -1L, -2L, 3L, 1632L));
        dtoList.add(createMeisaiDto(1L, 3000L, 0L, -1L, -2L, -1L, 0L));
        dtoList.get(5).setUpdkeshikomikin(0L);
        equalsS10T001T(dto, dnpyno3, db, dtoList, dnpyno1, true, null, false, true, null);
        // 入金消込履歴テーブル
        dtoList.get(4).setSkydydmdd("30");
        equalsS10T004T(dto, dnpyno3, db, List.of(dtoList.get(0)), "12", true, null, 1, 1, dnpyno1);
        equalsS10T004T(dto, dnpyno3, db, List.of(dtoList.get(1)), "12", true, null, 2, 2, dnpyno1);
        equalsS10T004T(dto, dnpyno3, db, List.of(dtoList.get(4)), "12", true, null, 5, 3, dnpyno1);
    }

    /**
     * 入金登録テスト（（プラス過入金・マイナス過入金発生）正常系）
     * 
     * @throws Exception システム例外
     */
    @Test
    @Rollback
    void testCreateNyukinNomalPlusMinusKanyukin() throws Exception {
        // マスタデータ生成
        this.createMasterData(null);
        // DB管理セッション情報生成
        final DbManagementSession db = this.createDefaultDbManagementSession();
        // 消込対象明細Dto生成
        List<C1005W01MeisaiDto> meisaiDtoList = this.createDefaultC1005W01MeisaiDto(false, 2);
        meisaiDtoList.get(0).setSkyshono("3");
        meisaiDtoList.get(0).setKonkaikeshikomikin(1520L);
        meisaiDtoList.get(0).setKeshikomizd(1520L);
        meisaiDtoList.get(1).setSkyshono("2");
        meisaiDtoList.get(1).setKonkaikeshikomikin(6480L);
        meisaiDtoList.get(1).setKeshikomizd(6480L);

        // 請求残高テーブルデータ生成
        createS07T001T(1, "000000", 3888L, null);
        createS07T001T(2, "000001", 6480L, null);
        createS07T001T(3, "000000", 3888L, null);
        createS07T001T(4, "000001", 0L, null);

        // 一部入金実行（1回目）
        // ----------------------------------------------------------------
        C1005W01HeaderDto dto = this.execCreateNyukin(db, "1", 8000L, null, meisaiDtoList);
        // final String beforeDnpyno = dto.getDnpyno();
        // 請求残高テーブルデータ検証
        meisaiDtoList.get(0).setKeshikomizumikin(1520L);
        meisaiDtoList.get(0).setMikeshizankin(2368L);
        equalsS07T001T(dto, db, meisaiDtoList, false);
        // 入金管理テーブルデータ検証
        equalsS10T003T(dto, dto.getDnpyno(), db, false, null, 0L, null, null);
        // 入金明細テーブルデータ検証
        List<C1005W01MeisaiDto> tmpList = new ArrayList<C1005W01MeisaiDto>();
        tmpList.add(meisaiDtoList.get(1));
        tmpList.add(meisaiDtoList.get(0));
        tmpList.get(1).setKonkaikeshikomikin(1520L);
        equalsS10T001T(dto, dto.getDnpyno(), db, tmpList, null, false, null, false, false, null);
        // 入金消込履歴テーブルデータ検証
        equalsS10T004T(dto, dto.getDnpyno(), db, tmpList, "10", false, null, null, null,
            new String[] {null});

        // 一部入金実行（2回目）
        // ----------------------------------------------------------------
        meisaiDtoList = this.createDefaultC1005W01MeisaiDto(false, 2);
        meisaiDtoList.get(0).setSkyshono("1");
        meisaiDtoList.get(0).setKonkaikeshikomikin(632L);
        meisaiDtoList.get(0).setKeshikomizd(632L);
        meisaiDtoList.get(1).setSkyshono("3");
        meisaiDtoList.get(1).setTokuisakicd("000000");
        meisaiDtoList.get(1).setKonkaikeshikomikin(2368L);
        meisaiDtoList.get(1).setKeshikomizd(2368L);
        dto = this.execCreateNyukin(db, "1", 3000L, null, meisaiDtoList);
        // 入金管理テーブルデータ検証
        equalsS10T003T(dto, dto.getDnpyno(), db, false, null, 0L, null, null);
        // 入金明細テーブルデータ検証
        equalsS10T001T(dto, dto.getDnpyno(), db, meisaiDtoList, null, false, null, false, false,
            null);
        // 入金消込履歴テーブルデータ検証
        equalsS10T004T(dto, dto.getDnpyno(), db, meisaiDtoList, "10", false, null, null, null,
            new String[] {null});
        // 請求残高テーブルデータ検証
        meisaiDtoList.get(0).setKeshikomizumikin(632L);
        meisaiDtoList.get(0).setMikeshizankin(3256L);
        meisaiDtoList.get(1).setKeshikomizumikin(3888L);
        meisaiDtoList.get(1).setMikeshizankin(0L);
        equalsS07T001T(dto, db, meisaiDtoList, false);

        // 過入金実行
        // ----------------------------------------------------------------
        meisaiDtoList = this.createDefaultC1005W01MeisaiDto(false, 1);
        meisaiDtoList.get(0).setSkyshono("1");
        meisaiDtoList.get(0).setKonkaikeshikomikin(3256L);
        meisaiDtoList.get(0).setKeshikomizd(3256L);
        dto = this.execCreateNyukin(db, "1", 5000L, null, meisaiDtoList);
        final String kanyukinDnpyno = dto.getDnpyno();
        // 入金管理テーブルデータ検証
        equalsS10T003T(dto, dto.getDnpyno(), db, false, null, 1744L, null, null);
        // 入金明細テーブルデータ検証
        tmpList = this.createDefaultC1005W01MeisaiDto(false, 2);
        tmpList.get(0).setKonkaikeshikomikin(3256L);
        tmpList.get(0).setKeshikomizd(3256L);
        tmpList.get(1).setKonkaikeshikomikin(1744L);
        tmpList.get(1).setKeshikomizd(1744L);
        tmpList.get(1).setKeshikomizumikin(0L);
        equalsS10T001T(dto, dto.getDnpyno(), db, tmpList, null, false, null, false, false, null);
        // 入金消込履歴テーブルデータ検証
        equalsS10T004T(dto, dto.getDnpyno(), db, meisaiDtoList, "10", false, null, null, null,
            new String[] {null});
        // 請求残高テーブルデータ検証
        meisaiDtoList.get(0).setKonkaikeshikomikin(3888L);
        meisaiDtoList.get(0).setKeshikomizd(3888L);
        equalsS07T001T(dto, db, meisaiDtoList, false);

        // マイナス入金実行
        // ----------------------------------------------------------------
        meisaiDtoList = this.createDefaultC1005W01MeisaiDto(true, 2);
        meisaiDtoList.get(0).setSkyshono("3");
        meisaiDtoList.get(0).setKonkaikeshikomikin(-3888L);
        meisaiDtoList.get(0).setKeshikomizd(-3888L);
        meisaiDtoList.get(1).setSkyshono("2");
        meisaiDtoList.get(1).setKonkaikeshikomikin(-2368L);
        meisaiDtoList.get(1).setKeshikomizd(-2368L);
        dto = this.execCreateNyukin(db, "1", -8000L, null, meisaiDtoList);
        final String minusDnpyno = dto.getDnpyno();
        // 入金管理テーブルデータ検証
        equalsS10T003T(dto, dto.getDnpyno(), db, false, null, 0L, null, null);
        equalsS10T003T(dto, kanyukinDnpyno, db, false, null, 0L, 5000L, null);
        // 入金明細テーブルデータ検証（過入金明細）
        tmpList = this.createDefaultC1005W01MeisaiDto(false, 3);
        tmpList.get(0).setKonkaikeshikomikin(3256L);
        tmpList.get(0).setKeshikomizd(3256L);
        tmpList.get(1).setKonkaikeshikomikin(1744L);
        tmpList.get(1).setKeshikomizd(1744L);
        tmpList.get(1).setKeshikomizumikin(0L);
        tmpList.get(2).setKonkaikeshikomikin(0L);
        tmpList.get(2).setKeshikomizd(0L);
        tmpList.get(2).setKeshikomizumikin(1744L);
        equalsS10T001T(dto, kanyukinDnpyno, db, tmpList, null, false, null, false, true, null);
        // 入金消込履歴テーブルデータ検証（過入金明細）
        tmpList.remove(0);
        tmpList.remove(0);
        equalsS10T004T(dto, kanyukinDnpyno, db, tmpList, "10", false, "30", 3, 2, null, "0");
        // 入金明細テーブルデータ検証（新）
        List<C1005W01MeisaiDto> tmpSubList = this.createDefaultC1005W01MeisaiDto(true, 1);
        tmpSubList.get(0).setKonkaikeshikomikin(-1744L);
        tmpSubList.get(0).setKeshikomizd(-1744L);
        tmpSubList.get(0).setSkydydmdd("1");
        tmpSubList.get(0).setTokuisakicd("000001");
        tmpList = new ArrayList<C1005W01MeisaiDto>();
        tmpList.add(tmpSubList.get(0));
        tmpList.add(meisaiDtoList.get(0));
        tmpList.add(meisaiDtoList.get(1));
        equalsS10T001T(dto, dto.getDnpyno(), db, tmpList, null, false, null, false, false, null);
        // 入金消込履歴テーブルデータ検証（新）
        tmpList.get(0).setSkydydmdd("30");
        equalsS10T004T(dto, dto.getDnpyno(), db, List.of(tmpList.get(0)), "10", false, null, 1, 1,
            null, "0");
        equalsS10T004T(dto, dto.getDnpyno(), db, List.of(tmpList.get(1)), "10", false, null, 2, 2,
            null, "1");
        equalsS10T004T(dto, dto.getDnpyno(), db, List.of(tmpList.get(2)), "10", false, null, 3, 3,
            null, "1");
        // 請求残高テーブルデータ検証
        meisaiDtoList.get(0).setKeshikomizumikin(0L);
        meisaiDtoList.get(0).setMikeshizankin(3888L);
        meisaiDtoList.get(1).setKeshikomizumikin(4112L);
        meisaiDtoList.get(1).setMikeshizankin(2368L);
        equalsS07T001T(dto, db, meisaiDtoList, false);

        // 入金実行
        // ----------------------------------------------------------------
        meisaiDtoList = this.createDefaultC1005W01MeisaiDto(false, 1);
        meisaiDtoList.get(0).setSkyshono("2");
        meisaiDtoList.get(0).setTokuisakicd("000001");
        meisaiDtoList.get(0).setKonkaikeshikomikin(2368L);
        meisaiDtoList.get(0).setKeshikomizd(2368L);
        dto = this.execCreateNyukin(db, "1", 2368L, null, meisaiDtoList);
        // final String nyukinDnpyno = dto.getDnpyno();
        // 入金管理テーブルデータ検証
        equalsS10T003T(dto, dto.getDnpyno(), db, false, null, 0L, null, null);
        // 入金明細テーブルデータ検証
        equalsS10T001T(dto, dto.getDnpyno(), db, meisaiDtoList, null, false, null, false, false,
            null);
        // 入金消込履歴テーブルデータ検証
        equalsS10T004T(dto, dto.getDnpyno(), db, meisaiDtoList, "10", false, null, null, null,
            new String[] {null});
        // 請求残高テーブルデータ検証
        meisaiDtoList.get(0).setKonkaikeshikomikin(6480L);
        meisaiDtoList.get(0).setKeshikomizd(6480L);
        equalsS07T001T(dto, db, meisaiDtoList, false);

        // 入金取消実行（１回目）
        // ----------------------------------------------------------------
        try {
            dto = this.execCreateNyukin(db, "2", 0L, minusDnpyno, null);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "★★★ この伝票で消込を行った残高が他の伝票で戻されています。 ★★★");
        }
        // ＜後続処理を実行可能なテストパターンでなくなったためコメントアウト＞
        // 今後、本パターンテストが必要となった際にデータの見直しなどを行い、再実行できるようにコメントアウトとしています。
        // final String cancel1Dnpyno = dto.getDnpyno();
        // // 入金管理テーブルデータ検証
        // equalsS10T003T(dto, minusDnpyno, db, true, dto.getDnpyno(), 0L, -8000L);
        // equalsS10T003T(dto, dto.getDnpyno(), db, false, minusDnpyno, 4112L, 8000L);
        // // 入金明細テーブルデータ検証（マイナス入金明細）
        // tmpList = this.createDefaultC1005W01MeisaiDto(true, 3);
        // tmpList.get(0).setTokuisakicd("000001");
        // tmpList.get(0).setKonkaikeshikomikin(-1744L);
        // tmpList.get(0).setKeshikomizd(-1744L);
        // tmpList.get(1).setTokuisakicd("000000");
        // tmpList.get(1).setSkyshono("3");
        // tmpList.get(1).setKonkaikeshikomikin(-3888L);
        // tmpList.get(1).setKeshikomizd(-3888L);
        // tmpList.get(2).setKonkaikeshikomikin(-2368L);
        // tmpList.get(2).setKeshikomizd(-2368L);
        // equalsS10T001T(dto, minusDnpyno, db, tmpList, dto.getDnpyno(), false,
        // dto.getSkyshootptkbn(), false, false, null);
        // // 入金明細テーブルデータ検証（新）
        // tmpList = this.createDefaultC1005W01MeisaiDto(false, 3);
        // tmpList.get(0).setTokuisakicd("000001");
        // tmpList.get(0).setKonkaikeshikomikin(1744L);
        // tmpList.get(0).setKeshikomizd(1744L);
        // tmpList.get(0).setKeshikomizumikin(0L);
        // tmpList.get(1).setTokuisakicd("000000");
        // tmpList.get(1).setKonkaikeshikomikin(3888L);
        // tmpList.get(1).setKeshikomizd(3888L);
        // tmpList.get(1).setSkyshono("3");
        // tmpList.get(2).setKonkaikeshikomikin(2368L);
        // tmpList.get(2).setKeshikomizd(2368L);
        // tmpList.get(2).setKeshikomizumikin(0L);
        // equalsS10T001T(dto, dto.getDnpyno(), db, tmpList, minusDnpyno, false,
        // dto.getSkyshootptkbn(), false, false, null);
        // // 入金消込履歴テーブルデータ検証
        // tmpList.remove(2);
        // tmpList.remove(0);
        // equalsS10T004T(dto, dto.getDnpyno(), db, tmpList, "12", false, null, 2, 1, minusDnpyno);
        // // 請求残高テーブルデータ検証
        // tmpList = this.createDefaultC1005W01MeisaiDto(false, 1);
        // tmpList.get(0).setSkyshono("3");
        // tmpList.get(0).setKonkaikeshikomikin(3888L);
        // tmpList.get(0).setKeshikomizd(3888L);
        // equalsS07T001T(dto, db, tmpList, false);
        //
        // // 入金取消実行（2回目）
        // // ----------------------------------------------------------------
        // dto = this.execCreateNyukin(db, "2", 0L, nyukinDnpyno, null);
        // // 入金管理テーブルデータ検証
        // equalsS10T003T(dto, nyukinDnpyno, db, true, dto.getDnpyno(), 0L, 2368L);
        // equalsS10T003T(dto, dto.getDnpyno(), db, false, nyukinDnpyno, 0L, -2368L);
        // // 入金明細テーブルデータ検証（入金明細）
        // meisaiDtoList = this.createDefaultC1005W01MeisaiDto(false, 1);
        // meisaiDtoList.get(0).setSkyshono("2");
        // meisaiDtoList.get(0).setTokuisakicd("000001");
        // meisaiDtoList.get(0).setKonkaikeshikomikin(2368L);
        // meisaiDtoList.get(0).setKeshikomizd(2368L);
        // equalsS10T001T(dto, nyukinDnpyno, db, meisaiDtoList, dto.getDnpyno(), false,
        // dto.getSkyshootptkbn(), false, false, null);
        // // 入金明細テーブルデータ検証（新）
        // equalsS10T001T(dto, dto.getDnpyno(), db, meisaiDtoList, nyukinDnpyno, true,
        // dto.getSkyshootptkbn(), false, false, null);
        // // 入金消込履歴テーブルデータ検証
        // equalsS10T004T(dto, dto.getDnpyno(), db, meisaiDtoList, "12", true, null, null, null,
        // nyukinDnpyno);
        // // 請求残高テーブルデータ検証
        // meisaiDtoList.get(0).setKeshikomizumikin(4112L);
        // meisaiDtoList.get(0).setMikeshizankin(2368L);
        // equalsS07T001T(dto, db, meisaiDtoList, false);
        //
        // // 入金取消実行（3回目）
        // // ----------------------------------------------------------------
        // dto = this.execCreateNyukin(db, "2", 0L, beforeDnpyno, null);
        // final String cancel3Dnpyno = dto.getDnpyno();
        // // 入金管理テーブルデータ検証
        // equalsS10T003T(dto, beforeDnpyno, db, true, dto.getDnpyno(), 0L, 8000L);
        // equalsS10T003T(dto, dto.getDnpyno(), db, false, beforeDnpyno, -2368L, -8000L);
        // // 入金明細テーブルデータ検証（１回目一部入金明細）
        // tmpList = this.createDefaultC1005W01MeisaiDto(true, 2);
        // tmpList.get(0).setKonkaikeshikomikin(6480L);
        // tmpList.get(0).setKeshikomizd(6480L);
        // tmpList.get(0).setSkyshono("2");
        // tmpList.get(0).setTokuisakicd("000001");
        // tmpList.get(1).setKonkaikeshikomikin(1520L);
        // tmpList.get(1).setKeshikomizd(1520L);
        // tmpList.get(1).setSkyshono("3");
        // tmpList.get(1).setTokuisakicd("000000");
        // equalsS10T001T(dto, beforeDnpyno, db, tmpList, dto.getDnpyno(), false,
        // dto.getSkyshootptkbn(), false, false, null);
        // // 入金明細テーブルデータ検証（新）
        // tmpList.get(0).setKeshikomizumikin(4112L);
        // equalsS10T001T(dto, dto.getDnpyno(), db, tmpList, beforeDnpyno, true,
        // dto.getSkyshootptkbn(), false, false, null);
        // // 入金消込履歴テーブルデータ検証
        // equalsS10T004T(dto, dto.getDnpyno(), db, tmpList, "12", true, null, null, null,
        // beforeDnpyno);
        // // 請求残高テーブルデータ検証
        // tmpList.get(0).setKeshikomizumikin(0L);
        // tmpList.get(0).setMikeshizankin(6480L);
        // tmpList.get(1).setKeshikomizumikin(2368L);
        // tmpList.get(1).setMikeshizankin(1520L);
        // equalsS07T001T(dto, db, tmpList, false);
        //
        // // 入金実行
        // // ----------------------------------------------------------------
        // meisaiDtoList = this.createDefaultC1005W01MeisaiDto(false, 2);
        // meisaiDtoList.get(0).setSkyshono("3");
        // meisaiDtoList.get(0).setKonkaikeshikomikin(1520L);
        // meisaiDtoList.get(0).setKeshikomizd(1520L);
        // meisaiDtoList.get(1).setSkyshono("2");
        // meisaiDtoList.get(1).setKonkaikeshikomikin(6480L);
        // meisaiDtoList.get(1).setKeshikomizd(6480L);
        // dto = this.execCreateNyukin(db, "1", 6256L, null, meisaiDtoList);
        // // 入金管理テーブルデータ検証
        // equalsS10T003T(dto, dto.getDnpyno(), db, false, null, 0L, null);
        // equalsS10T003T(dto, cancel1Dnpyno, db, false, minusDnpyno, 0L, 8000L);
        // equalsS10T003T(dto, cancel3Dnpyno, db, false, beforeDnpyno, 0L, -8000L);
        // // 入金明細テーブルデータ検証（新）
        // tmpList = new ArrayList<C1005W01MeisaiDto>();
        // tmpList.add(meisaiDtoList.get(1));
        // tmpList.add(meisaiDtoList.get(0));
        // tmpList.get(0).setKonkaikeshikomikin(4736L);
        // tmpList.get(0).setKeshikomizd(4736L);
        // equalsS10T001T(dto, dto.getDnpyno(), db, tmpList, null, false, null, false, false, null);
        // // 入金消込履歴テーブルデータ検証（新）
        // equalsS10T004T(dto, dto.getDnpyno(), db, tmpList, "10", false, null, null, null, null);
        // // 入金明細テーブルデータ検証（入金取消1回目明細）
        // tmpList = this.createDefaultC1005W01MeisaiDto(false, 6);
        // tmpList.get(0).setTokuisakicd("000001");
        // tmpList.get(0).setKonkaikeshikomikin(1744L);
        // tmpList.get(0).setKeshikomizd(1744L);
        // tmpList.get(0).setKeshikomizumikin(0L);
        // tmpList.get(1).setTokuisakicd("000000");
        // tmpList.get(1).setSkyshono("3");
        // tmpList.get(1).setKonkaikeshikomikin(3888L);
        // tmpList.get(1).setKeshikomizd(3888L);
        // tmpList.get(2).setKonkaikeshikomikin(2368L);
        // tmpList.get(2).setKeshikomizd(2368L);
        // tmpList.get(2).setKeshikomizumikin(0L);
        // tmpList.get(2).setTokuisakinm("0");
        // tmpList.get(3).setKonkaikeshikomikin(0L);
        // tmpList.get(3).setKeshikomizd(0L);
        // tmpList.get(3).setKeshikomizumikin(1744L);
        // tmpList.get(4).setKonkaikeshikomikin(0L);
        // tmpList.get(4).setKeshikomizd(0L);
        // tmpList.get(4).setKeshikomizumikin(624L);
        // tmpList.get(5).setKonkaikeshikomikin(0L);
        // tmpList.get(5).setKeshikomizd(0L);
        // tmpList.get(5).setKeshikomizumikin(1744L);
        // tmpList.get(5).setSkyshono("2");
        // equalsS10T001T(dto, cancel1Dnpyno, db, tmpList, minusDnpyno, false,
        // dto.getSkyshootptkbn(),
        // false, true, null);
        // // 入金消込履歴テーブルデータ検証（入金取消1回目明細）
        // equalsS10T004T(dto, cancel1Dnpyno, db, Arrays.asList(tmpList.get(3)), "10", false, "30",
        // 4,
        // 2, minusDnpyno);
        // equalsS10T004T(dto, cancel1Dnpyno, db, Arrays.asList(tmpList.get(4)), "10", false, "30",
        // 5,
        // 3, minusDnpyno);
        // // 入金明細テーブルデータ検証（入金取消3回目明細）
        // tmpList = this.createDefaultC1005W01MeisaiDto(false, 4);
        // tmpList.get(0).setTokuisakicd("000001");
        // tmpList.get(0).setSkyshono("2");
        // tmpList.get(0).setKonkaikeshikomikin(-6480L);
        // tmpList.get(0).setKeshikomizd(-6480L);
        // tmpList.get(0).setKeshikomizumikin(-4112L);
        // tmpList.get(1).setTokuisakicd("000000");
        // tmpList.get(1).setSkyshono("3");
        // tmpList.get(1).setKonkaikeshikomikin(-1520L);
        // tmpList.get(1).setKeshikomizd(-1520L);
        // tmpList.get(2).setKonkaikeshikomikin(0L);
        // tmpList.get(2).setKeshikomizd(0L);
        // tmpList.get(2).setKeshikomizumikin(-1744L);
        // tmpList.get(3).setKonkaikeshikomikin(0L);
        // tmpList.get(3).setKeshikomizd(0L);
        // tmpList.get(3).setKeshikomizumikin(-624L);
        // equalsS10T001T(dto, cancel3Dnpyno, db, tmpList, beforeDnpyno, false,
        // dto.getSkyshootptkbn(),
        // false, true, null);
        // // 入金消込履歴テーブルデータ検証（入金取消3回目明細）
        // equalsS10T004T(dto, cancel3Dnpyno, db, Arrays.asList(tmpList.get(0)), "12", false, null,
        // 1,
        // 1, beforeDnpyno);
        // equalsS10T004T(dto, cancel3Dnpyno, db, Arrays.asList(tmpList.get(1)), "12", false, null,
        // 2,
        // 2, beforeDnpyno);
        // equalsS10T004T(dto, cancel3Dnpyno, db, Arrays.asList(tmpList.get(2)), "10", false, "30",
        // 3,
        // 3, beforeDnpyno);
        // equalsS10T004T(dto, cancel3Dnpyno, db, Arrays.asList(tmpList.get(3)), "10", false, "30",
        // 4,
        // 4, beforeDnpyno);
        // // 請求残高テーブルデータ検証
        // tmpList = this.createDefaultC1005W01MeisaiDto(false, 2);
        // tmpList.get(0).setKonkaikeshikomikin(3888L);
        // tmpList.get(0).setKeshikomizd(3888L);
        // tmpList.get(0).setSkyshono("3");
        // tmpList.get(1).setKonkaikeshikomikin(6480L);
        // tmpList.get(1).setKeshikomizd(6480L);
        // tmpList.get(1).setSkyshono("2");
        // equalsS07T001T(dto, db, tmpList, false);
    }

    /**
     * 入金登録テスト（（売上調整（同一得意先））正常系）
     * 
     * @throws Exception システム例外
     */
    @Test
    @Rollback
    void testCreateNyukinNomalSalesAdjustSameTokuisaki() throws Exception {
        // マスタデータ生成
        this.createMasterData(null);
        // DB管理セッション情報生成
        final DbManagementSession db = this.createDefaultDbManagementSession();
        // 消込対象明細Dto生成
        List<C1005W01MeisaiDto> meisaiDtoList = this.createDefaultC1005W01MeisaiDto(false, 2);
        meisaiDtoList.get(0).setSkyshono("1");
        meisaiDtoList.get(0).setKonkaikeshikomikin(3888L);
        meisaiDtoList.get(0).setKeshikomizd(3888L);
        meisaiDtoList.get(1).setSkyshono("2");
        meisaiDtoList.get(1).setKonkaikeshikomikin(5184L);
        meisaiDtoList.get(1).setKeshikomizd(5184L);

        // 請求残高テーブルデータ生成（初回）
        createS07T001T(1, "000000", 3888L, null);
        createS07T001T(2, "000001", 6480L, null);

        // 一部入金実行（1回目）
        // ----------------------------------------------------------------
        C1005W01HeaderDto dto = this.execCreateNyukin(db, "1", 9072L, null, meisaiDtoList);
        // 請求残高テーブルデータ検証
        meisaiDtoList.get(1).setMikeshizankin(1296L);
        equalsS07T001T(dto, db, meisaiDtoList, false);
        // 入金管理テーブルデータ検証
        equalsS10T003T(dto, dto.getDnpyno(), db, false, null, 0L, null, null);
        // 入金明細テーブルデータ検証
        equalsS10T001T(dto, dto.getDnpyno(), db, meisaiDtoList, null, false, null, false, false,
            null);
        // 入金消込履歴テーブルデータ検証
        equalsS10T004T(dto, dto.getDnpyno(), db, meisaiDtoList, "10", false, null, null, null,
            new String[] {null});

        // 請求残高テーブルデータ生成（次回）
        createS07T001T(3, "000000", 0L, null);
        createS07T001T(4, "000001", -1296L, null);

        // 調整実行
        // ----------------------------------------------------------------
        meisaiDtoList.remove(0);
        meisaiDtoList.get(0).setKonkaikeshikomikin(1296L);
        meisaiDtoList.get(0).setKeshikomizd(1296L);
        dto = this.execCreateNyukin(db, "3", 0L, null, meisaiDtoList);
        // 入金管理テーブルデータ検証
        dto.setUrichoseikin(1296L);
        equalsS10T003T(dto, dto.getDnpyno(), db, false, null, 0L, null, null);
        // 請求先マスタデータ検証
        equalsS02M017T(db, "000000", 0L, 1296L);
        // 入金明細テーブルデータ検証
        meisaiDtoList.get(0).setKonkaikeshikomikin(0L);
        meisaiDtoList.get(0).setKeshikomizd(0L);
        equalsS10T001T(dto, dto.getDnpyno(), db, meisaiDtoList, null, false, "1", false, false,
            null);
        // 入金消込履歴テーブルデータ検証
        meisaiDtoList.get(0).setKeshikomizumikin(1296L);
        equalsS10T004T(dto, dto.getDnpyno(), db, meisaiDtoList, "11", false, "31", 1, 1,
            new String[] {null});
        meisaiDtoList.get(0).setSkyshono("4");
        equalsS10T004T(dto, dto.getDnpyno(), db, meisaiDtoList, "11", true, "31", 1, 2,
            new String[] {null});
        // 請求残高テーブルデータ検証
        meisaiDtoList = this.createDefaultC1005W01MeisaiDto(false, 2);
        meisaiDtoList.get(0).setSkyshono("2");
        meisaiDtoList.get(0).setTokuisakicd("000001");
        meisaiDtoList.get(0).setKonkaikeshikomikin(6480L);
        meisaiDtoList.get(0).setKeshikomizd(6480L);
        meisaiDtoList.get(1).setSkyshono("4");
        meisaiDtoList.get(1).setKonkaikeshikomikin(-1296L);
        meisaiDtoList.get(1).setKeshikomizd(-1296L);
        equalsS07T001T(dto, db, meisaiDtoList, false);
        // 入金取込ワークデータ検証
        equalsS10W001T(db, dto.getDnpyno());
        // 入金取込得意先ワークデータ検証
        equalsS10W002T();
    }

    /**
     * 入金登録テスト（（売上調整（別得意先）／売上調整（別得意先）後取消）正常系）
     * 
     * @throws Exception システム例外
     */
    @Test
    @Rollback
    void testCreateNyukinNomalSalesAdjustOtherTokuisaki() throws Exception {
        // マスタデータ生成
        this.createMasterData(null);
        // DB管理セッション情報生成
        final DbManagementSession db = this.createDefaultDbManagementSession();
        // 消込対象明細Dto生成
        List<C1005W01MeisaiDto> meisaiDtoList = this.createDefaultC1005W01MeisaiDto(false, 2);
        meisaiDtoList.get(0).setSkyshono("1");
        meisaiDtoList.get(0).setKonkaikeshikomikin(3888L);
        meisaiDtoList.get(0).setKeshikomizd(3888L);
        meisaiDtoList.get(1).setSkyshono("2");
        meisaiDtoList.get(1).setKonkaikeshikomikin(5184L);
        meisaiDtoList.get(1).setKeshikomizd(5184L);

        // 請求残高テーブルデータ生成（初回）
        createS07T001T(1, "000000", 3888L, null);
        createS07T001T(2, "000001", 6480L, null);

        // 一部入金実行（1回目）
        // ----------------------------------------------------------------
        C1005W01HeaderDto dto = this.execCreateNyukin(db, "1", 9072L, null, meisaiDtoList);
        // 請求残高テーブルデータ検証
        meisaiDtoList.get(1).setMikeshizankin(1296L);
        equalsS07T001T(dto, db, meisaiDtoList, false);
        // 入金管理テーブルデータ検証
        equalsS10T003T(dto, dto.getDnpyno(), db, false, null, 0L, null, null);
        // 入金明細テーブルデータ検証
        equalsS10T001T(dto, dto.getDnpyno(), db, meisaiDtoList, null, false, null, false, false,
            null);
        // 入金消込履歴テーブルデータ検証
        equalsS10T004T(dto, dto.getDnpyno(), db, meisaiDtoList, "10", false, null, null, null,
            new String[] {null});

        // 請求残高テーブルデータ生成（次回）
        createS07T001T(3, "000000", -1296L, null);
        createS07T001T(4, "000001", -0L, null);

        // 調整実行
        // ----------------------------------------------------------------
        meisaiDtoList.remove(0);
        meisaiDtoList.get(0).setKonkaikeshikomikin(1296L);
        meisaiDtoList.get(0).setKeshikomizd(1296L);
        dto = this.execCreateNyukin(db, "3", 0L, null, meisaiDtoList);
        final String adjustDnpyno = dto.getDnpyno();
        // 入金管理テーブルデータ検証
        dto.setUrichoseikin(1296L);
        equalsS10T003T(dto, dto.getDnpyno(), db, false, null, 0L, null, null);
        // 入金明細テーブルデータ検証
        meisaiDtoList = this.createDefaultC1005W01MeisaiDto(false, 2);
        meisaiDtoList.get(0).setKonkaikeshikomikin(1296L);
        meisaiDtoList.get(0).setKeshikomizd(1296L);
        meisaiDtoList.get(0).setSkyshono("2");
        meisaiDtoList.get(0).setTokuisakicd("000001");
        meisaiDtoList.get(1).setKonkaikeshikomikin(-1296L);
        meisaiDtoList.get(1).setKeshikomizd(-1296L);
        meisaiDtoList.get(1).setSkyshono("3");
        meisaiDtoList.get(1).setTokuisakicd("000000");
        equalsS10T001T(dto, dto.getDnpyno(), db, meisaiDtoList, null, false, "1", false, false,
            null);
        // 入金消込履歴テーブルデータ検証
        equalsS10T004T(dto, dto.getDnpyno(), db, meisaiDtoList, "11", false, "31", null, null,
            new String[] {null});
        // 請求残高テーブルデータ検証
        meisaiDtoList = this.createDefaultC1005W01MeisaiDto(false, 2);
        meisaiDtoList.get(0).setSkyshono("3");
        meisaiDtoList.get(0).setKonkaikeshikomikin(-1296L);
        meisaiDtoList.get(0).setKeshikomizd(-1296L);
        meisaiDtoList.get(1).setSkyshono("2");
        meisaiDtoList.get(1).setKonkaikeshikomikin(6480L);
        meisaiDtoList.get(1).setKeshikomizd(6480L);
        equalsS07T001T(dto, db, meisaiDtoList, false);

        // 入金取消実行
        // ----------------------------------------------------------------
        dto = this.execCreateNyukin(db, "2", 0L, adjustDnpyno, null);
        // 入金管理テーブルデータ検証
        dto.setNyknkind("31");
        dto.setUrichoseikin(-1296L);
        equalsS10T003T(dto, dto.getDnpyno(), db, false, adjustDnpyno, 0L, 0L, null);
        equalsS10T003T(dto, adjustDnpyno, db, true, dto.getDnpyno(), 0L, 0L, null);
        // 入金明細テーブルデータ検証（マイナス入金明細）
        meisaiDtoList = this.createDefaultC1005W01MeisaiDto(false, 2);
        meisaiDtoList.get(0).setKonkaikeshikomikin(1296L);
        meisaiDtoList.get(0).setKeshikomizd(1296L);
        meisaiDtoList.get(0).setSkyshono("2");
        meisaiDtoList.get(0).setTokuisakicd("000001");
        meisaiDtoList.get(1).setKonkaikeshikomikin(-1296L);
        meisaiDtoList.get(1).setKeshikomizd(-1296L);
        meisaiDtoList.get(1).setSkyshono("3");
        meisaiDtoList.get(1).setTokuisakicd("000000");
        equalsS10T001T(dto, adjustDnpyno, db, meisaiDtoList, dto.getDnpyno(), false,
            dto.getSkyshootptkbn(), false, false, null);
        // 入金明細テーブルデータ検証（新）
        equalsS10T001T(dto, dto.getDnpyno(), db, meisaiDtoList, adjustDnpyno, true,
            dto.getSkyshootptkbn(), false, false, null);
        // 入金消込履歴テーブルデータ検証
        equalsS10T004T(dto, dto.getDnpyno(), db, meisaiDtoList, "12", true, null, null, null,
            adjustDnpyno);
        // 請求残高テーブルデータ検証
        meisaiDtoList.get(0).setKeshikomizumikin(5184L);
        meisaiDtoList.get(0).setMikeshizankin(1296L);
        equalsS07T001T(dto, db, meisaiDtoList, true);
    }

    /**
     * 一括入金消込データモデル
     * 
     * @author accisdev044
     *
     */
    @Data
    private class BulkNyukinClearingDataModel {
        /**
         * 伝票番号
         */
        private String dnpyno;

        /**
         * 入金入力ヘッダ情報Dto
         */
        private C1005W01HeaderDto dto;

        /**
         * 消込対象明細Dtoリスト
         */
        private List<C1005W01MeisaiDto> meisaiDtoList;
    }

    /**
     * 一括入金消込テスト（正常系）
     */
    @Test
    @Rollback
    void testBulkNyukinClearing() {
        // データ削除
        deleteData();
        // 運用日付データ生成
        createUnyoDate();
        // DB管理セッション情報生成
        final DbManagementSession db = this.createDefaultDbManagementSession();

        // ワークデータ生成
        List<BulkNyukinClearingDataModel> modelList = new ArrayList<BulkNyukinClearingDataModel>();
        int index = 0;
        int skyshono = 1;
        List<String> dnpynoList = new ArrayList<String>();
        for (Long i = -5L; i <= 5L; i++) {
            if (i == 0L) {
                continue;
            }
            // 入金入力ヘッダ情報Dto生成
            C1005W01HeaderDto dto = this.createDefaultC1005W01HeaderDto("1", 1000L * i, null);
            dto.setKaishutnshanm("01");
            // 消込対象明細Dto生成
            List<C1005W01MeisaiDto> meisaiDtoList = this.createDefaultC1005W01MeisaiDto(i < 0, 2);
            for (C1005W01MeisaiDto meisaiDto : meisaiDtoList) {
                meisaiDto.setTokuisakicd(
                    String.valueOf(index) + meisaiDto.getTokuisakicd().substring(1, 6));
                meisaiDto.setSkyshono(String.valueOf(skyshono));
                // 得意先マスタデータ生成
                createS02M013T(meisaiDto.getTokuisakicd(), null);
                // 得意先実績テーブルデータ生成
                createS02M050T(meisaiDto.getTokuisakicd());
                // 請求残高テーブルデータ生成
                createS07T001T(skyshono, meisaiDto.getTokuisakicd(),
                    meisaiDto.getKonkaikeshikomikin(), meisaiDtoList.get(0).getTokuisakicd());
                skyshono++;
            }
            dto.setSkysakicd(meisaiDtoList.get(0).getTokuisakicd());
            // 請求先マスタデータ生成
            createS02M017T(null, dto.getSkysakicd());
            // 入金取込ワーク、入金消込ワーク生成
            dnpynoList.add(this.createNyukinWork(dto, meisaiDtoList, db));
            // 入金取込ワーク更新
            S10W001TEntity s10W001TEntity = this.getS10W001TEntity(dnpynoList.get(index));
            s10W001TEntity.setDatsksmotokbn("01");
            S10W001TEntityExample s10W001TEntityExample = new S10W001TEntityExample();
            s10W001TEntityExample.createCriteria()
                .andApkihoncdEqualTo(s10W001TEntity.getApkihoncd())
                .andShopcdEqualTo(s10W001TEntity.getShopcd())
                .andDnpynoEqualTo(s10W001TEntity.getDnpyno());
            this.s10w001tDao.updateByExample(s10W001TEntity, s10W001TEntityExample);
            // 一括入金消込データモデル追加
            BulkNyukinClearingDataModel model = new BulkNyukinClearingDataModel();
            model.setDnpyno(dnpynoList.get(index));;
            model.setDto(dto);
            model.setMeisaiDtoList(meisaiDtoList);
            modelList.add(model);
            index++;
        }

        // パラメータ生成
        S10W001TD1608W03IkkatsuKeshikomiEntity entity =
            new S10W001TD1608W03IkkatsuKeshikomiEntity();
        entity.setApkihoncd(APKIHONCD);
        entity.setShopcd("000");
        entity.setKoshaapkihoncd(db.getApkihoncd());
        entity.setKoshashopcd(db.getShopcd());
        entity.setKoshacd(db.getTnshacd());
        entity.setKshnipadrse(db.getIpAddress());
        entity.setDatsksmotokbn("01");
        entity.setNyknhyostarchi(dnpynoList.get(0));
        entity.setNyknhyoendchi(dnpynoList.get(dnpynoList.size() - 1));

        // 一括入金消込処理実行
        this.s10w001tDao.executeIkatsuNyukin(entity);

        // データ検証
        index = 1;
        for (BulkNyukinClearingDataModel model : modelList) {
            log.debug("■■■■■ " + index + " ■■■■■");
            Long kairekin = model.dto.getNyknkin();
            int indexTmp = 0;
            for (C1005W01MeisaiDto meisaiDto : model.meisaiDtoList) {
                if (kairekin == 0L) {
                    model.meisaiDtoList.remove(indexTmp);
                    break;
                }
                // 消込金額、消込残高設定
                Long keshikomikin = meisaiDto.getKonkaikeshikomikin();
                if (model.dto.getNyknkin() > 0) {
                    meisaiDto
                        .setKeshikomizumikin(kairekin >= keshikomikin ? keshikomikin : kairekin);
                    meisaiDto
                        .setMikeshizankin(kairekin >= keshikomikin ? 0L : keshikomikin - kairekin);
                    meisaiDto.setKonkaikeshikomikin(meisaiDto.getKeshikomizumikin());
                } else {
                    meisaiDto.setKeshikomizumikin(Math.abs(kairekin) >= Math.abs(keshikomikin) ? 0L
                        : kairekin - keshikomikin);
                    meisaiDto.setMikeshizankin(
                        Math.abs(kairekin) >= Math.abs(keshikomikin) ? Math.abs(keshikomikin) * 2
                            : Math.abs(keshikomikin) + Math.abs(kairekin));
                    meisaiDto.setKonkaikeshikomikin(indexTmp == 0 ? keshikomikin : kairekin);
                }
                // 過入金額設定
                kairekin -= keshikomikin;
                indexTmp++;
            }
            kairekin = (kairekin < 0 && model.dto.getNyknkin() > 0) ? 0L : kairekin;
            kairekin = (kairekin > 0 && model.dto.getNyknkin() < 0) ? 0L : kairekin;
            if (kairekin > 0) {
                model.getMeisaiDtoList().get(1).setKonkaikeshikomikin(
                    model.getMeisaiDtoList().get(1).getKonkaikeshikomikin() + kairekin);
            }

            // 請求残高テーブルデータ検証
            equalsS07T001T(model.getDto(), db, model.getMeisaiDtoList(), false);
            // 入金管理テーブルデータ検証
            equalsS10T003T(model.getDto(), model.getDnpyno(), db, false, null, kairekin, null,
                null);

            // 入金明細テーブルデータ検証
            if (model.dto.getNyknkin() < 0) {
                model.meisaiDtoList.get(0)
                    .setKeshikomizumikin(model.meisaiDtoList.get(0).getKonkaikeshikomikin());
                if (model.meisaiDtoList.size() > 1) {
                    model.meisaiDtoList.get(1).setKeshikomizumikin(
                        model.meisaiDtoList.get(1).getKonkaikeshikomikin() - kairekin);
                }
            }
            equalsS10T001T(model.getDto(), model.getDnpyno(), db, model.getMeisaiDtoList(), null,
                false, null, false, false, null);
            // 入金消込履歴テーブルデータ検証
            equalsS10T004T(model.getDto(), model.getDnpyno(), db, model.getMeisaiDtoList(), "20",
                false, null, null, null, new String[] {null});
            // 入金取込ワークデータ検証
            equalsS10W001T(db, model.getDnpyno());
            index++;
        }
    }

    /**
     * 入金登録実行
     * 
     * @param db DB管理セッション情報
     * @param prcskbn 処理区分
     * @param nyknkin 入金金額
     * @param dnpyno 伝票番号
     * @return 入金入力ヘッダ情報Dto
     * @throws Exception システム例外
     */
    private C1005W01HeaderDto execCreateNyukin(DbManagementSession db, String prcskbn, Long nyknkin,
        String dnpyno, List<C1005W01MeisaiDto> meisaiDtoList) throws Exception {
        // 入金入力ヘッダ情報Dto設定
        C1005W01HeaderDto dto = this.createDefaultC1005W01HeaderDto(prcskbn, nyknkin, dnpyno);
        // 入金登録処理
        dto.setDnpyno(this.createNyukin(dto, meisaiDtoList, db));
        return dto;
    }

    /**
     * 得意先マスタデータ検証
     * 
     * @param db DB管理セッション情報
     * @param tokuisakicd 得意先コード
     * @param jotaists 状態ステータス
     */
    private void equalsS02M013T(DbManagementSession db, String tokuisakicd, String jotaists) {
        S02M013TEntity entity = getS02M013TEntity(tokuisakicd);
        assertEquals(entity.getJotaists(), jotaists);
        assertEquals(entity.getKoshaapkihoncd(), db.getApkihoncd());
        assertEquals(entity.getKoshashopcd(), db.getShopcd());
        assertEquals(entity.getKoshacd(), db.getTnshacd());
        assertEquals(entity.getKshnsystmymd(), db.getSystemDate());
        assertNotNull(entity.getKshnsystmjikan(), db.getSystemTime());
        assertEquals(entity.getKshnunyoymd(), db.getUnyoDate());
        assertEquals(entity.getKshnipadrse(), db.getIpAddress());
        assertEquals(entity.getKshnpgmid(), "C1009B01");
    }

    /**
     * 請求先マスタデータ検証
     * 
     * @param db DB管理セッション情報
     * @param skysakicd 請求先コード
     * @param kairekin 過入金額
     * @param urichoseikin 売上調整金額
     */
    private void equalsS02M017T(DbManagementSession db, String skysakicd, Long kairekin,
        Long urichoseikin) {
        S02M017TEntity entity = getS02M017TEntity(skysakicd);
        assertEquals(entity.getKairekin(), kairekin);
        assertEquals(entity.getUrichoseikin(), urichoseikin);
        assertEquals(entity.getKoshaapkihoncd(), db.getApkihoncd());
        assertEquals(entity.getKoshashopcd(), db.getShopcd());
        assertEquals(entity.getKoshacd(), db.getTnshacd());
        assertEquals(entity.getKshnsystmymd(), db.getSystemDate());
        assertNotNull(entity.getKshnsystmjikan(), db.getSystemTime());
        assertEquals(entity.getKshnunyoymd(), db.getUnyoDate());
        assertEquals(entity.getKshnipadrse(), db.getIpAddress());
        assertEquals(entity.getKshnpgmid(), "C1009B01");
    }

    /**
     * 得意先実績テーブルデータ検証
     * 
     * @param db DB管理セッション情報
     * @param tokuisakicd 得意先コード
     * @param nyknkin 入金金額
     */
    private void equalsS02M050T(DbManagementSession db, String tokuisakicd, Integer nyknkin) {
        S02M050TEntity entity = getS02M050TEntity(tokuisakicd);
        assertEquals(entity.getGenzaizd(), nyknkin);
        assertEquals(entity.getKoshaapkihoncd(), db.getApkihoncd());
        assertEquals(entity.getKoshashopcd(), db.getShopcd());
        assertEquals(entity.getKoshacd(), db.getTnshacd());
        assertEquals(entity.getKshnsystmymd(), db.getSystemDate());
        assertNotNull(entity.getKshnsystmjikan(), db.getSystemTime());
        assertEquals(entity.getKshnunyoymd(), db.getUnyoDate());
        assertEquals(entity.getKshnipadrse(), db.getIpAddress());
        assertEquals(entity.getKshnpgmid(), "C1009B01");
    }

    /**
     * 請求残高テーブルデータ検証
     * 
     * @param dto 入金入力ヘッダ情報Dto
     * @param db DB管理セッション情報
     * @param meisaiDtoList 消込対象明細Dtoリスト
     * @param isNyukinCancel 入金取消フラグ
     */
    private void equalsS07T001T(C1005W01HeaderDto dto, DbManagementSession db,
        List<C1005W01MeisaiDto> meisaiDtoList, boolean isNyukinCancel) {
        for (C1005W01MeisaiDto meisaiDto : meisaiDtoList) {
            S07T001TEntity entity = getS07T001TEntityList(meisaiDto.getSkyshono()).get(0);

            Long keshikomikin = !isNyukinCancel ? meisaiDto.getKonkaikeshikomikin()
                : (meisaiDto.getKonkaikeshikomikin() - meisaiDto.getKonkaikeshikomikin());
            if (meisaiDto.getKeshikomizumikin() != null) {
                keshikomikin = meisaiDto.getKeshikomizumikin();
            }
            assertEquals(entity.getKeshikomikin(), Integer.parseInt(keshikomikin.toString()));

            Long keshikomizd =
                !isNyukinCancel ? (meisaiDto.getKeshikomizd() - meisaiDto.getKonkaikeshikomikin())
                    : meisaiDto.getKonkaikeshikomikin();
            if (meisaiDto.getMikeshizankin() != null) {
                keshikomizd = meisaiDto.getMikeshizankin();
            }
            assertEquals(entity.getKeshikomizd(), Integer.parseInt(keshikomizd.toString()));
            assertEquals(entity.getKoshaapkihoncd(), db.getApkihoncd());
            assertEquals(entity.getKoshashopcd(), db.getShopcd());
            assertEquals(entity.getKoshacd(), db.getTnshacd());
            assertEquals(entity.getKshnsystmymd(), db.getSystemDate());
            assertNotNull(entity.getKshnsystmjikan(), db.getSystemTime());
            assertEquals(entity.getKshnunyoymd(), db.getUnyoDate());
            assertEquals(entity.getKshnipadrse(), db.getIpAddress());
            assertEquals(entity.getKshnpgmid(), "C1009B01");
        }
    }

    /**
     * 入金明細テーブルデータ検証
     * 
     * @param dto 入金入力ヘッダ情報Dto
     * @param dnpyno 伝票番号
     * @param db DB管理セッション情報
     * @param meisaiDtoList 消込対象明細Dtoリスト
     * @param trksdnpyno 取消伝票番号
     * @param isNyukinCancel 入金取消フラグ
     * @param skyshootptkbn 請求書出力有無区分
     * @param isKanyukin 過入金フラグ
     * @param isSosai 相殺フラグ
     * @param keshikomikin 消込金額
     */
    private void equalsS10T001T(C1005W01HeaderDto dto, String dnpyno, DbManagementSession db,
        List<C1005W01MeisaiDto> meisaiDtoList, String trksdnpyno, boolean isNyukinCancel,
        String skyshootptkbn, boolean isKanyukin, boolean isSosai, Long keshikomikin) {
        List<S10T001TEntity> entityList = getS10T001TEntityList(dnpyno);
        assertEquals(entityList.size(), meisaiDtoList.size());
        int index = 0;
        for (S10T001TEntity entity : entityList) {
            assertEquals(entity.getApkihoncd(), dto.getApkihoncd());
            assertEquals(entity.getShopcd(), dto.getShopcd());
            assertEquals(entity.getDnpyno(), dnpyno);
            assertEquals(entity.getNykndnpynoedano(), index + 1);

            String kairekinkbn = (isSosai && index > 1) ? "1" : "0";
            if (meisaiDtoList.get(index).getTokuisakinm() != null
                && !meisaiDtoList.get(index).getTokuisakinm().equals("X")) {
                kairekinkbn = meisaiDtoList.get(index).getTokuisakinm();
            }
            assertEquals(entity.getKairekinkbn(), kairekinkbn);
            assertEquals(entity.getSkysakicd(), dto.getSkysakicd());
            assertEquals(entity.getTokuisakicd(), meisaiDtoList.get(index).getTokuisakicd());
            assertEquals(entity.getNykndd(), dto.getNykndd());
            assertEquals(entity.getNyknkind(), dto.getNyknkind());

            Long nyknkin = meisaiDtoList.get(index).getKonkaikeshikomikin();
            if (isKanyukin && index != 0) {
                Long nyknkinsum = 0L;
                for (C1005W01MeisaiDto meisaiDto : meisaiDtoList) {
                    nyknkinsum += meisaiDto.getKonkaikeshikomikin();
                }
                nyknkin += dto.getNyknkin() - nyknkinsum;
            } else if (isNyukinCancel) {
                nyknkin *= -1;
            }
            assertEquals(entity.getNyknkin(), nyknkin);
            assertEquals(entity.getNebikikin(), 0L);
            assertEquals(entity.getTesuryo(), 0);

            String biko = dto.getMeisaibiko();
            if (meisaiDtoList.get(index).getTokuisakinm() == null) {
                if (isSosai && index > 1 && !isNyukinCancel) {
                    biko = null;
                } else if (meisaiDtoList.get(index).getTouurikin() != null) {
                    biko = null;
                }
            } else if (meisaiDtoList.get(index).getTokuisakinm().equals("X")) {
                biko = null;
            }
            assertEquals(entity.getBiko(), biko);

            Long keshikomikinTmp = meisaiDtoList.get(index).getKonkaikeshikomikin();
            if (meisaiDtoList.get(index).getKeshikomizumikin() != null) {
                keshikomikinTmp = meisaiDtoList.get(index).getKeshikomizumikin();
            }
            if (isNyukinCancel) {
                keshikomikinTmp *= -1;
            }
            assertEquals(entity.getKeshikomikin(),
                keshikomikin == null ? keshikomikinTmp : keshikomikin);

            Long sosaikin = 0L;
            if (meisaiDtoList.get(index).getUpdkeshikomikin() != null) {
                sosaikin = meisaiDtoList.get(index).getUpdkeshikomikin();
            } else if (isSosai) {
                sosaikin = entity.getNyknkin() - entity.getKeshikomikin();
            }
            assertEquals(entity.getSosaikin(), sosaikin);
            assertEquals(entity.getUrishimeprcsdd(), db.getUnyoDate());
            assertEquals(entity.getSkyprcsjissidd(), "00000000");
            assertEquals(entity.getSkyshimedydm(), null);
            assertEquals(entity.getSkyshimedd(), null);
            assertEquals(entity.getSkyprcsjissikbn(), Short.parseShort("0"));
            assertEquals(entity.getGetsujiprcsjissidd(), "00000000");
            assertEquals(entity.getGetsujishimedydm(), null);
            assertEquals(entity.getGetsujishimedd(), null);
            assertEquals(entity.getDatsksmotokbn(),
                dto.getKaishutnshanm() == null ? "07" : dto.getKaishutnshanm());

            String skyshootptarinashikbn = skyshootptkbn == null ? "0" : skyshootptkbn;
            if (meisaiDtoList.get(index).getSkydydmdd() != null) {
                skyshootptarinashikbn = meisaiDtoList.get(index).getSkydydmdd();
            } else if ((isSosai && index > 1) || isNyukinCancel) {
                skyshootptarinashikbn = dto.getSkyshootptkbn();
            }

            assertEquals(entity.getSkyshootptarinashikbn(), skyshootptarinashikbn);
            assertEquals(entity.getKaishutnshacd(), "000000");
            assertEquals(entity.getTrksdnpyno(), trksdnpyno);
            assertEquals(entity.getYukokbn(), "1");
            assertEquals(entity.getToshaapkihoncd(), db.getApkihoncd());
            assertEquals(entity.getToshashopcd(), db.getShopcd());
            assertEquals(entity.getToshacd(), db.getTnshacd());
            assertEquals(entity.getInptsystmymd(), db.getSystemDate());
            assertNotNull(entity.getInptsystmjikan(), db.getSystemTime());
            assertEquals(entity.getInptunyoymd(), db.getUnyoDate());
            assertEquals(entity.getInptipadrse(), db.getIpAddress());
            assertEquals(entity.getInptpgmid(), "C1009B01");
            assertEquals(entity.getKoshaapkihoncd(), db.getApkihoncd());
            assertEquals(entity.getKoshashopcd(), db.getShopcd());
            assertEquals(entity.getKoshacd(), db.getTnshacd());
            assertEquals(entity.getKshnsystmymd(), db.getSystemDate());
            assertNotNull(entity.getKshnsystmjikan(), db.getSystemTime());
            assertEquals(entity.getKshnunyoymd(), db.getUnyoDate());
            assertEquals(entity.getKshnipadrse(), db.getIpAddress());
            assertEquals(entity.getKshnpgmid(), "C1009B01");
            index++;
        }
    }

    /**
     * 入金管理テーブルデータ検証
     * 
     * @param dto 入金入力ヘッダ情報Dto
     * @param dnpyno 伝票番号
     * @param db DB管理セッション情報
     * @param isNyukinCancel 入金取消フラグ
     * @param trksdnpyno 取消伝票番号
     * @param kairekin 過入金額
     * @param nyknkin 入金金額
     */
    private void equalsS10T003T(C1005W01HeaderDto dto, String dnpyno, DbManagementSession db,
        boolean isNyukinCancel, String trksdnpyno, Long kairekin, Long nyknkin,
        String kairekinkanriflg) {
        List<S10T003TEntity> entityList = getS10T003TEntityList(dnpyno);
        assertEquals(entityList.size(), 1);
        assertEquals(entityList.get(0).getApkihoncd(), dto.getApkihoncd());
        assertEquals(entityList.get(0).getShopcd(), dto.getShopcd());
        assertEquals(entityList.get(0).getNykndnpyno(), dnpyno);
        assertEquals(entityList.get(0).getSkysakicd(), dto.getSkysakicd());
        assertEquals(entityList.get(0).getNykndd(), dto.getNykndd());
        assertEquals(entityList.get(0).getNyknkind(), dto.getNyknkind());

        Long nyknkinTmp = !isNyukinCancel ? dto.getNyknkin() : dto.getNyknkin() * -1;
        assertEquals(entityList.get(0).getNyknkin(), nyknkin == null ? nyknkinTmp : nyknkin);
        assertEquals(entityList.get(0).getNebikikin(),
            !isNyukinCancel ? dto.getNebikikin() : dto.getNebikikin() * -1);
        assertEquals(entityList.get(0).getTesuryo(),
            !isNyukinCancel ? dto.getTesuryo() : dto.getTesuryo() * -1);
        assertEquals(entityList.get(0).getKairekin(), kairekin);
        assertEquals(entityList.get(0).getUrichoseikin(),
            !isNyukinCancel ? dto.getUrichoseikin() : dto.getUrichoseikin() * -1);
        assertEquals(entityList.get(0).getTrksdnpyno(), trksdnpyno);

        String kairekinkanriflgTmp =
            kairekinkanriflg != null ? kairekinkanriflg : kairekin.equals(0L) ? "0" : "1";
        assertEquals(entityList.get(0).getKairekinkanriflg(), kairekinkanriflgTmp);
        assertEquals(entityList.get(0).getYukokbn(), "1");
        assertEquals(entityList.get(0).getToshaapkihoncd(), db.getApkihoncd());
        assertEquals(entityList.get(0).getToshashopcd(), db.getShopcd());
        assertEquals(entityList.get(0).getToshacd(), db.getTnshacd());
        assertEquals(entityList.get(0).getInptsystmymd(), db.getSystemDate());
        assertNotNull(entityList.get(0).getInptsystmjikan(), db.getSystemTime());
        assertEquals(entityList.get(0).getInptunyoymd(), db.getUnyoDate());
        assertEquals(entityList.get(0).getInptipadrse(), db.getIpAddress());
        assertEquals(entityList.get(0).getInptpgmid(), "C1009B01");
        assertEquals(entityList.get(0).getKoshaapkihoncd(), db.getApkihoncd());
        assertEquals(entityList.get(0).getKoshashopcd(), db.getShopcd());
        assertEquals(entityList.get(0).getKoshacd(), db.getTnshacd());
        assertEquals(entityList.get(0).getKshnsystmymd(), db.getSystemDate());
        assertNotNull(entityList.get(0).getKshnsystmjikan(), db.getSystemTime());
        assertEquals(entityList.get(0).getKshnunyoymd(), db.getUnyoDate());
        assertEquals(entityList.get(0).getKshnipadrse(), db.getIpAddress());
        assertEquals(entityList.get(0).getKshnpgmid(), "C1009B01");
    }

    /**
     * 入金消込履歴テーブルデータ検証
     * 
     * @param dto 入金入力ヘッダ情報Dto
     * @param dnpyno 伝票番号
     * @param db DB管理セッション情報
     * @param meisaiDtoList 消込対象明細Dtoリスト
     * @param nyknkshkmprcskbn 入金消込処理区分
     * @param isNyukinCancel 入金取消フラグ
     * @param kshkmnyknkind 消込入金種別
     * @param edano 枝番号
     * @param seq SEQ
     * @param params パラメータリスト（0:取消伝票番号、1:請求処理実施区分）
     */
    private void equalsS10T004T(C1005W01HeaderDto dto, String dnpyno, DbManagementSession db,
        List<C1005W01MeisaiDto> meisaiDtoList, String nyknkshkmprcskbn, boolean isNyukinCancel,
        String kshkmnyknkind, Integer edano, Integer seq, String... params) {
        List<S10T004TEntity> entityList = getS10T004TEntityList(dnpyno, edano, seq);
        assertEquals(entityList.size(), meisaiDtoList.size());
        int index = 0;
        for (S10T004TEntity entity : entityList) {
            assertEquals(entity.getApkihoncd(), dto.getApkihoncd());
            assertEquals(entity.getShopcd(), dto.getShopcd());
            assertEquals(entity.getNykndnpyno(), dnpyno);

            Integer nykndnpynoedano = index + 1;
            if (edano != null) {
                nykndnpynoedano = edano;
            } else if (meisaiDtoList.get(index).getTokuisakinm() != null) {
                nykndnpynoedano = Integer.parseInt(meisaiDtoList.get(index).getTokuisakinm());
            }
            assertEquals(entity.getNykndnpynoedano(), nykndnpynoedano);
            assertEquals(entity.getSeq(), seq != null ? seq : index + 1);
            assertEquals(entity.getNyknkshkmprcskbn(), nyknkshkmprcskbn);
            assertEquals(entity.getSkysakicd(), dto.getSkysakicd());
            assertEquals(entity.getTokuisakicd(), meisaiDtoList.get(index).getTokuisakicd());
            assertEquals(entity.getNykndd(), dto.getNykndd());
            assertEquals(entity.getKshkmdd(), db.getUnyoDate());
            assertEquals(entity.getNyknkind(), dto.getNyknkind());

            String kshkmnyknkindTmp = kshkmnyknkind == null ? dto.getNyknkind() : kshkmnyknkind;
            if (meisaiDtoList.get(index).getSkydydmdd() != null) {
                kshkmnyknkindTmp = meisaiDtoList.get(index).getSkydydmdd();
            }
            assertEquals(entity.getKshkmnyknkind(), kshkmnyknkindTmp);

            Long keshikomikin = meisaiDtoList.get(index).getKonkaikeshikomikin();
            if (meisaiDtoList.get(index).getKeshikomizumikin() != null) {
                keshikomikin = meisaiDtoList.get(index).getKeshikomizumikin();
            }
            if (isNyukinCancel) {
                keshikomikin *= -1;
            }
            assertEquals(entity.getKeshikomikin(), keshikomikin);
            assertEquals(entity.getShokaiskyshono(), meisaiDtoList.get(index).getSkyshono());
            assertEquals(entity.getDaibikiskyshono(), "000000000000");
            String skyprcsjissikbn = params.length > 1 ? params[1] : "1";
            assertEquals(entity.getSkyprcsjissikbn(), Short.parseShort(skyprcsjissikbn));
            String trksdnpynotmp = params[0] != null ? params[0] : null;
            assertEquals(entity.getTrksdnpyno(), trksdnpynotmp);
            assertEquals(entity.getDsplykbn(), "1");
            assertEquals(entity.getYukokbn(), "1");
            assertEquals(entity.getToshaapkihoncd(), db.getApkihoncd());
            assertEquals(entity.getToshashopcd(), db.getShopcd());
            assertEquals(entity.getToshacd(), db.getTnshacd());
            assertEquals(entity.getInptsystmymd(), db.getSystemDate());
            assertNotNull(entity.getInptsystmjikan(), db.getSystemTime());
            assertEquals(entity.getInptunyoymd(), db.getUnyoDate());
            assertEquals(entity.getInptipadrse(), db.getIpAddress());
            assertEquals(entity.getInptpgmid(), "C1009B01");
            assertEquals(entity.getKoshaapkihoncd(), db.getApkihoncd());
            assertEquals(entity.getKoshashopcd(), db.getShopcd());
            assertEquals(entity.getKoshacd(), db.getTnshacd());
            assertEquals(entity.getKshnsystmymd(), db.getSystemDate());
            assertNotNull(entity.getKshnsystmjikan(), db.getSystemTime());
            assertEquals(entity.getKshnunyoymd(), db.getUnyoDate());
            assertEquals(entity.getKshnipadrse(), db.getIpAddress());
            assertEquals(entity.getKshnpgmid(), "C1009B01");
            index++;
        }
    }

    /**
     * 入金取込ワークデータ検証
     * 
     * @param db DB管理セッション情報
     * @param dnpyno 伝票番号
     */
    private void equalsS10W001T(DbManagementSession db, String dnpyno) {
        S10W001TEntity entity = getS10W001TEntity(dnpyno);
        assertEquals(entity.getNykndatsksflg(), "1");
        assertEquals(entity.getKoshaapkihoncd(), db.getApkihoncd());
        assertEquals(entity.getKoshashopcd(), db.getShopcd());
        assertEquals(entity.getKoshacd(), db.getTnshacd());
        assertEquals(entity.getKshnsystmymd(), db.getSystemDate());
        assertNotNull(entity.getKshnsystmjikan(), db.getSystemTime());
        assertEquals(entity.getKshnunyoymd(), db.getUnyoDate());
        assertEquals(entity.getKshnipadrse(), db.getIpAddress());
        assertEquals(entity.getKshnpgmid(), "C1009B01");
    }

    /**
     * 入金取込得意先ワークデータ検証
     */
    private void equalsS10W002T() {
        List<S10W002TEntity> entityList = getS10W002TEntity();
        assertEquals(entityList.size(), 0);
    }

    /**
     * 得意先マスタデータ取得
     * 
     * @param tokuisakicd 得意先コード
     * @return 得意先マスタデータ
     */
    private S02M013TEntity getS02M013TEntity(String tokuisakicd) {
        S02M013TEntityExample example = new S02M013TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD).andTokuisakicdEqualTo(tokuisakicd);
        return s02m013tDao.selectByExample(example).get(0);
    }

    /**
     * 請求先マスタデータ取得
     * 
     * @param skysakicd 請求先コード
     * @return 請求先マスタデータ
     */
    private S02M017TEntity getS02M017TEntity(String skysakicd) {
        S02M017TEntityExample example = new S02M017TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD).andSkysakicdEqualTo(skysakicd);
        return s02m017tDao.selectByExample(example).get(0);
    }

    /**
     * 得意先実績テーブルデータ取得
     * 
     * @param tokuisakicd 得意先コード
     * @return 得意先実績テーブルデータ
     */
    private S02M050TEntity getS02M050TEntity(String tokuisakicd) {
        S02M050TEntityExample example = new S02M050TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD).andTokuisakicdEqualTo(tokuisakicd);
        return s02m050tDao.selectByExample(example).get(0);
    }

    /**
     * 請求残高テーブルデータリスト取得
     * 
     * @param skyshono 請求書番号
     * @return 請求残高テーブルデータリスト
     */
    private List<S07T001TEntity> getS07T001TEntityList(String skyshono) {
        S07T001TEntityExample example = new S07T001TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD).andSkyshonoEqualTo(skyshono);
        return s07t001tDao.selectByExample(example);
    }

    /**
     * 入金明細テーブルデータリスト取得
     * 
     * @param dnpyno 伝票番号
     * @return 入金明細テーブルデータリスト
     */
    private List<S10T001TEntity> getS10T001TEntityList(String dnpyno) {
        S10T001TEntityExample example = new S10T001TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD).andDnpynoEqualTo(dnpyno);
        example.setOrderByClause("NYKNDNPYNOEDANO");
        return s10t001tDao.selectByExample(example);
    }

    /**
     * 入金管理テーブルデータリスト取得
     * 
     * @param dnpyno 伝票番号
     * @return 入金管理テーブルデータリスト
     */
    private List<S10T003TEntity> getS10T003TEntityList(String dnpyno) {
        S10T003TEntityExample example = new S10T003TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD).andNykndnpynoEqualTo(dnpyno);
        return s10t003tDao.selectByExample(example);
    }

    /**
     * 入金消込履歴テーブルデータリスト取得
     * 
     * @param dnpyno 伝票番号
     * @param edano 枝番号
     * @param seq SEQ
     * @return 入金消込履歴テーブルデータリスト
     */
    private List<S10T004TEntity> getS10T004TEntityList(String dnpyno, Integer edano, Integer seq) {
        S10T004TEntityExample example = new S10T004TEntityExample();
        if (edano != null) {
            if (seq != null) {
                example.createCriteria().andApkihoncdEqualTo(APKIHONCD).andNykndnpynoEqualTo(dnpyno)
                    .andNykndnpynoedanoEqualTo(edano).andSeqEqualTo(seq);
            } else {
                example.createCriteria().andApkihoncdEqualTo(APKIHONCD).andNykndnpynoEqualTo(dnpyno)
                    .andNykndnpynoedanoEqualTo(edano);
            }
        } else {
            example.createCriteria().andApkihoncdEqualTo(APKIHONCD).andNykndnpynoEqualTo(dnpyno);
        }
        example.setOrderByClause("NYKNDNPYNOEDANO");
        return s10t004tDao.selectByExample(example);
    }

    /**
     * 入金取込ワークデータ取得
     * 
     * @param dnpyno 伝票番号
     * @return 入金取込ワークデータ
     */
    private S10W001TEntity getS10W001TEntity(String dnpyno) {
        S10W001TEntityExample example = new S10W001TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD).andDnpynoEqualTo(dnpyno);
        return s10w001tDao.selectByExample(example).get(0);
    }

    /**
     * 入金取込得意先ワークデータ取得
     * 
     * @return 入金取込得意先ワークデータリスト
     */
    private List<S10W002TEntity> getS10W002TEntity() {
        S10W002TEntityExample example = new S10W002TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD);
        return s10w002tDao.selectByExample(example);
    }

    /**
     * データ生成
     * 
     * @param shopcd ショップコード
     * @param nykndnpyno 入金伝票番号
     * @param isCreateS07001T 請求残高テーブルデータ生成フラグ
     * @param isMinusOnly マイナスのみフラグ
     * @param isPlusOnly プラスのみフラグ
     * @param params パラメータリスト（0:入金種別、1:消込残高）
     */
    private void createData(String shopcd, String nykndnpyno, boolean isCreateS07001T,
        boolean isMinusOnly, boolean isPlusOnly, String... params) {
        // 運用日付データ生成
        createUnyoDate();
        // 得意先マスタデータ生成
        createS02M013T("000000", null);
        createS02M013T("000001", null);
        // 請求先マスタデータ生成
        createS02M017T(shopcd, null);
        // 得意先実績テーブルデータ生成
        createS02M050T(null);
        // 請求残高テーブルデータ生成
        if (isCreateS07001T) {
            Long keshikomizd = params[1] != null ? Long.parseLong(params[1]) : null;
            createS07T001T(1, null, keshikomizd, null);
        }
        // 入金明細テーブル生成
        createS10T001T(nykndnpyno, isMinusOnly, isPlusOnly, params[0], nykndnpyno);
        // 入金管理テーブル生成
        createS10T003T(nykndnpyno, null, params[2]);
        // 入金消込履歴テーブル生成
        createS10T004T(1000L, "1");
    }

    /**
     * マスタデータ生成
     * 
     * @param tokuisakijotai 得意先状態
     */
    private void createMasterData(String tokuisakijotai) {
        // データ削除
        deleteData();
        // 運用日付データ生成
        createUnyoDate();
        // 請求先マスタデータ生成
        createS02M017T(null, null);
        // 得意先マスタデータ生成
        createS02M013T("000000", null);
        createS02M013T("000001", tokuisakijotai == null ? "5" : null);
        // 得意先実績テーブルデータ生成
        createS02M050T("000000");
        createS02M050T("000001");
    }

    /**
     * 運用日付データ生成
     */
    private void createUnyoDate() {
        S02M006TEntity entity = new S02M006TEntity();
        entity.setApkihoncd(APKIHONCD);
        entity.setShopcd("000");
        entity.setSetteino("038");
        entity.setRcrdkbn("M");
        entity.setSetteicd("1");
        entity.setMojisetteiatai(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        entity.setYukokbn("1");
        s02m006tDao.insert(entity);
    }

    /**
     * 過去日数データ生成
     * 
     * @param isCreate データ生成有無
     */
    private void createBeforeDays(boolean isCreate) {
        if (isCreate) {
            S02M006TEntity entity = new S02M006TEntity();
            entity.setApkihoncd(APKIHONCD);
            entity.setShopcd("000");
            entity.setSetteino("041");
            entity.setRcrdkbn("M");
            entity.setSetteicd("1");
            entity.setSuchisetteiatai(BigDecimal.valueOf(60));
            entity.setYukokbn("1");
            s02m006tDao.insert(entity);
        }
    }

    /**
     * 未来日数データ生成
     * 
     * @param isCreate データ生成有無
     */
    private void createAfterDays(boolean isCreate) {
        if (isCreate) {
            S02M006TEntity entity = new S02M006TEntity();
            entity.setApkihoncd(APKIHONCD);
            entity.setShopcd("000");
            entity.setSetteino("041");
            entity.setRcrdkbn("M");
            entity.setSetteicd("2");
            entity.setSuchisetteiatai(BigDecimal.valueOf(60));
            entity.setYukokbn("1");
            s02m006tDao.insert(entity);
        }
    }

    /**
     * 担当者マスタデータ生成
     */
    private void createS02M003T() {
        S02M003TEntity entity = new S02M003TEntity();
        entity.setApkihoncd(APKIHONCD);
        entity.setShopcd("000");
        entity.setTnshacd("000000");
        entity.setTnshamei("テスト担当者");
        entity.setTnshameikana("テストタントウシャ");
        entity.setShozokujigyoshocd("00");
        entity.setKanshakbn("0");
        entity.setAkshanikbn("1");
        entity.setMobileriyoumu("1");
        entity.setMailadrse("1@test.com");
        entity.setRenrakusakinokbnn1("1"); // 連絡先番号区分１（1:代表電話 2:自宅電話 3:携帯 4:FAX 5:勤め先 6:直通 7:その他）
        entity.setRenrakusakinon1("00000000000"); // 連絡先番号１
        entity.setTnshasts("0");
        entity.setLoginid("1");
        entity.setPsw("1");
        entity.setEigyotnshakbn("1");
        entity.setJimutnshakbn("1");
        entity.setDlvrytnshakbn("1");
        entity.setAramusetteiataimond(1);
        entity.setAramusetteiataitued(1);
        entity.setAramusetteiataiwedd(1);
        entity.setAramusetteiataithed(1);
        entity.setAramusetteiataifrid(1);
        entity.setAramusetteiataisatd(1);
        entity.setAramusetteiataisund(1);
        entity.setTnshamenuctgrycd("0");
        entity.setYukokbn("1");
        s02m003tDao.insert(entity);
    }

    /**
     * 得意先マスタデータ生成
     * 
     * @param tokuisakicd 得意先コード
     */
    private void createS02M013T(String tokuisakicd, String tokuisakijotai) {
        S02M013TEntity entity = new S02M013TEntity();
        entity.setApkihoncd(APKIHONCD);
        entity.setShopcd("000");
        entity.setSkysakicd("000000");
        entity.setJigyoshocd("00"); // 事業所コード
        entity.setYubinno("0000000"); // 郵便番号
        entity.setAdrsn1("東京都"); // 住所１
        entity.setRenrakusakinokbnn1("1"); // 連絡先番号区分１（1:代表電話 2:自宅電話 3:携帯 4:FAX 5:勤め先 6:直通 7:その他）
        entity.setRenrakusakinon1("00000000000"); // 連絡先番号１
        entity.setEigyotnshacd("000000"); // 営業担当者コード
        entity.setYukokbn("1");
        entity.setTokuisakicd(tokuisakicd); // 得意先コード
        entity.setTokuisakijotai(tokuisakijotai == null ? "4" : tokuisakijotai);
        entity.setTokuisakikbn("1"); // 得意先区分（1:顧客 2:ショップ 3:他AP 4:ACI本部 5:NA受託 6:配達受託 9:経費）
        entity.setRiyowsvkbn("0"); // 利用サーバー区分（0:アクアクララ 1:POU 2:両方 3:その他）
        entity.setCdkindkbn("1"); // コード種別区分（0:調査中 1:一般会員 2:法人会員 4:ﾌﾟﾛﾓｰｼｮﾝ会員 5:短期利用会員 9:移管データ）
        entity.setGyoshukbn("1"); // 業種区分
        entity.setTokuisakinmn1("テスト"); // 得意先名称１
        entity.setTokuisakikeishokbn("1"); // 得意先敬称区分（1:様 2:殿 3:御中）
        entity.setClckensakuyotelnon1("00000000000"); // コールセンター検索用電話番号１（画面上表示されない（ハイフンなし））
        entity.setMerumagakbn("0"); // メルマガ区分（0:発行しない 1:発行する）
        entity.setMoshikomircptkbn("0"); // 申込受付区分（0:なし 1:あり）
        entity.setTokubetsuuntkbn("0");
        entity.setRknkindtokubetsunebikiflg("0"); // 料金種別特別値引フラグ（0:なし 1:あり）
        entity.setRknkindtokubetsuuntflg("0"); // 料金種別特別単価フラグ（0:なし 1:あり）
        entity.setRknkindkihonflg("0"); // 料金種別基本フラグ（0:なし 1:あり）
        entity.setUrikeijotnshakbn("0"); // 売上計上担当者区分（0:設定なし 1:宅配担当者 2:営業担当者）
        entity.setMgntrgtkbn("0"); // マージン対象区分（0:なし 1:ワンショット 2:ボトル 3:併用）
        entity.setRtlskykanrikbn("0"); // レンタル請求管理区分（レンタル請求の管理区分 0:設定なし 1:レンタル 2:買取）
        entity.setDlvrykbn("1"); // 配達区分（1:自社、2:他社便、3:持ち帰り）
        entity.setFzaiokikanoflg("0"); // 不在置き可能フラグ（0:不可 1:可能）
        entity.setAcptrcptkbn("1"); // 受注受付区分（1:通常受注 2:定期受注）
        entity.setAcptteishiflg("0"); // 受注停止フラグ（0:受注可能 1:受注停止）
        entity.setHoshokinkbn("0"); // 保証金区分（0:なし 1:あり）
        entity.setKeizokurnk("S"); // 継続ランク（S:5年以上 A:3～5年 B:1～3年 C:１年未満 D:滞留）
        entity.setShohirnk("S"); // 消費ランク（S:10本以上 A:5～10本 B:3～5本 C:3本未満 D:滞留）
        entity.setKyuminkbn("0"); // 休眠区分（0:通常 1:４５日以上、納品実績がない）
        entity.setMtmkisvkbn("0"); // まとめ買いサービス区分（0:なし、1:あり）
        entity.setMuryobtlsvkbn("0"); // 無料ボトルサービス区分（0:なし、1:あり）
        entity.setMyaqauntjohodsplyumuflg("0"); // マイアクア単価情報表示有無フラグ（0:表示しない 1:表示する）
        entity.setMyaqaskyjohodsplyumuflg("0"); // マイアクア請求情報表示有無フラグ（0:表示しない 1:表示する）
        entity.setBabyaqariyoflg("0"); // ベビアクア利用フラグ（0:利用経験なし 1:利用経験あり）
        entity.setJotaists(tokuisakicd.equals("000000") ? "05" : "06");
        s02m013tDao.insert(entity);
    }

    /**
     * 請求先マスタデータ生成
     * 
     * @param shopcd ショップコード
     * @param skysakicd 請求先コード
     */
    private void createS02M017T(String shopcd, String skysakicd) {
        S02M017TEntity entity = new S02M017TEntity();
        entity.setApkihoncd(APKIHONCD);
        entity.setShopcd(shopcd == null ? "000" : shopcd);
        entity.setSkysakicd(skysakicd == null ? "000000" : skysakicd);
        entity.setJigyoshocd("00"); // 事業所コード
        entity.setSkyshohassouitakuumu("0"); // 請求書発送委託有無（0:なし 1:あり）
        entity.setSkysakikanrikbn("1"); // 請求先管理区分（1:請求先 2:卸 9:経費）
        entity.setSkykbn("1"); // 請求区分（1:一般 2:親子）
        entity.setSkysakinmn1("テスト"); // 請求先名称１
        entity.setSkysakikeishokbn("1"); // 請求先敬称区分（1:様 2:殿 3:御中）
        entity.setSkysakinmkana("テスト"); // 請求先名称カナ
        entity.setSkysakinmryakusho("テスト"); // 請求先名称略称
        entity.setYubinno("0000000"); // 郵便番号
        entity.setAdrsn1("東京都"); // 住所１
        entity.setRenrakusakinokbnn1("1"); // 連絡先番号区分１（1:代表電話 2:自宅電話 3:携帯 4:FAX 5:勤め先 6:直通 7:その他）
        entity.setRenrakusakinon1("00000000000"); // 連絡先番号１
        entity.setEigyotnshacd("000000"); // 営業担当者コード
        entity.setKaishutnshacd("000000"); // 回収担当者コード
        entity.setSkyshimedd("31"); // 請求締日
        entity.setShrsaito("1"); // 支払サイト（1:当月 2:翌月 3:翌々月 4:3ヶ月後 5:前払い）
        entity.setShrdd("31"); // 支払日（末締めは31）
        entity.setKaishuhohokbn("1");
        entity.setSkyhohokbn("1"); // 請求方法区分（1:残高請求 2:当月請求）
        entity.setMeisaiprntkbn("1"); // 明細印刷区分（1:親子一緒 2:別々）
        entity.setKsikkntetsuzukiflg("0"); // 決済機関手続中フラグ（0:フラグなし 1:手続き中）
        entity.setTairyukeisanshrsaito("1"); // 滞留計算支払サイト（1:１ヵ月 2:２ヵ月 3:３ヵ月 4:６ヵ月 5:１２ヵ月）
        entity.setTairyukbn("0"); // 滞留区分（0:滞留無 1:1ヶ月 2:2ヶ月 3:長期滞留）
        entity.setSkymeisaisofukbn("0"); // 請求明細送付区分（0:送付しない 1:送付する）
        entity.setYukokbn("1");
        entity.setKairekin(0L);
        entity.setUrichoseikin(0L);
        s02m017tDao.insert(entity);
    }

    /**
     * 得意先実績テーブルデータ生成
     * 
     * @param tokuisakicd 得意先コード
     */
    private void createS02M050T(String tokuisakicd) {
        S02M050TEntity entity = new S02M050TEntity();
        entity.setApkihoncd(APKIHONCD);
        entity.setShopcd("000");
        entity.setTokuisakicd(tokuisakicd == null ? "000000" : tokuisakicd); // 得意先コード
        entity.setGenzaizd(0);
        entity.setYukokbn("1");
        s02m050tDao.insert(entity);
    }

    /**
     * 請求残高テーブルデータ生成
     * 
     * @param index インデックス
     * @param tokuisakicd 得意先コード
     * @param keshikomizd 消込残高
     * @param skysakicd 請求先コード
     */
    private void createS07T001T(Integer index, String tokuisakicd, Long keshikomizd,
        String skysakicd) {
        S07T001TEntity entity = new S07T001TEntity();
        entity.setApkihoncd(APKIHONCD);
        entity.setShopcd("000");
        entity.setSkysakicd(skysakicd == null ? "000000" : skysakicd);
        entity.setTokuisakicd(tokuisakicd == null ? "000000" : tokuisakicd); // 得意先コード
        entity.setShimedydm(new SimpleDateFormat("yyyyMM").format(new Date())); // 締年月
        entity.setShimedd("31"); // 締日
        entity.setRenban(index); // 連番 （ AP、ショップ、請求先、得意先、締年月、締日毎の連番 ）
        entity.setSkyprcsjissikbn(Short.valueOf("1")); // 請求処理実施区分 （1:月締請求処理 2:代引時請求処理）
        entity.setSkyshono(String.valueOf(index)); // 請求書番号
        entity.setSkyshonoedano(Short.valueOf("0")); // 請求書番号枝番
        entity.setKeshikomikin((skysakicd != null && keshikomizd < 0) ? keshikomizd * -1 : 0L);

        Long keshikomizdTmp = keshikomizd == null ? 0 : keshikomizd;
        if (keshikomizdTmp < 0 && skysakicd != null) {
            keshikomizdTmp *= -1;
        }
        entity.setKeshikomizd(Integer.parseInt(keshikomizdTmp.toString()));
        entity.setKaishuhohokbn("1");
        entity.setYukokbn("1");
        s07t001tDao.insert(entity);
    }

    /**
     * 入金明細テーブルデータ生成
     * 
     * @param dnpyno 伝票番号
     * @param isMinusOnly マイナスのみフラグ
     * @param isPlusOnly プラスのみフラグ
     * @param nyknkind 入金種別
     * @param cancelDnpyno 取消伝票番号
     */
    private void createS10T001T(String dnpyno, boolean isMinusOnly, boolean isPlusOnly,
        String nyknkind, String cancelDnpyno) {
        int index = 0;
        for (int i = (isPlusOnly ? 1 : -5); i <= (isMinusOnly ? 0 : 5); i++) {
            S10T001TEntity entity = new S10T001TEntity();
            entity.setApkihoncd(APKIHONCD);
            entity.setShopcd("000");
            entity.setDnpyno(dnpyno);
            entity.setNykndnpynoedano(index);
            entity.setSkysakicd("000000");
            entity.setTokuisakicd("000000");
            entity.setNykndd(new SimpleDateFormat("yyyyMMdd").format(new Date())); // 入金日
            entity.setNyknkind(nyknkind == null ? "1" : nyknkind);
            entity.setNyknkin(Long.parseLong(String.valueOf(i))); // 入金金額
            entity.setNebikikin(0L); // 値引金額
            entity.setTesuryo(0); // 手数料
            entity.setKeshikomikin(0L);
            entity.setSosaikin(0L);
            entity.setDatsksmotokbn("07");
            entity.setSkyshootptarinashikbn("1");
            entity.setTrksdnpyno(cancelDnpyno);
            entity.setYukokbn("1");
            s10t001tDao.insert(entity);
            index++;
        }
    }

    /**
     * 入金管理テーブルデータ生成
     * 
     * @param nykndnpyno 入金伝票番号
     * @param params パラメータリスト（0:入金日、1:入金金額）
     */
    private void createS10T003T(String nykndnpyno, String... params) {
        S10T003TEntity entity = new S10T003TEntity();
        entity.setApkihoncd(APKIHONCD);
        entity.setShopcd("000");
        entity.setNykndnpyno(nykndnpyno);
        entity.setSkysakicd("000000");
        entity.setNykndd(
            params[0] == null ? new SimpleDateFormat("yyyyMMdd").format(new Date()) : params[0]);
        entity.setNyknkind("1");
        entity.setNyknkin(params.length > 1 ? Long.parseLong(params[1]) : 0L); // 入金金額
        entity.setNebikikin(0L); // 値引金額
        entity.setTesuryo(0); // 手数料
        entity.setKairekin(0L); // 過入金額
        entity.setUrichoseikin(0L); // 売上調整金額
        entity.setKairekinkanriflg("1"); // 過入金額管理フラグ（0：過入金なし 1：過入金あり ）
        entity.setYukokbn("1");
        s10t003tDao.insert(entity);
    }

    /**
     * 入金消込履歴テーブルデータ生成
     * 
     * @param keshikomikin 消込金額
     * @param skyshono 請求書番号
     */
    private void createS10T004T(Long keshikomikin, String skyshono) {
        S10T001TEntityExample example = new S10T001TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD);
        List<S10T001TEntity> entityList = s10t001tDao.selectByExample(example);

        int index = 0;
        for (S10T001TEntity e : entityList) {
            S10T004TEntity entity = new S10T004TEntity();
            entity.setApkihoncd(e.getApkihoncd());
            entity.setShopcd(e.getShopcd());
            entity.setNykndnpyno(e.getDnpyno());
            entity.setNykndnpynoedano(e.getNykndnpynoedano());
            entity.setSeq(index);
            entity.setNyknkshkmprcskbn("10");
            entity.setSkysakicd(e.getSkysakicd());
            entity.setTokuisakicd(e.getTokuisakicd());
            entity.setNyknkind("1");
            entity.setKshkmnyknkind("1");
            entity.setNykndd("20200101");
            entity.setKshkmdd("20200101");
            entity.setShokaiskyshono(skyshono);
            entity.setKeshikomikin(keshikomikin);
            entity.setDsplykbn("1");
            entity.setYukokbn("1");
            s10t004tDao.insert(entity);
            index++;
        }
    }

    /**
     * データ削除
     */
    private void deleteData() {
        deleteS02M003T(); // 担当者マスタデータ削除
        deleteS02M006T(); // システム管理マスタデータ削除
        deleteS02M013T(); // 得意先マスタデータ削除
        deleteS02M017T(); // 請求先マスタデータ削除
        deleteS02M050T(); // 得意先実績テーブルデータ削除
        deleteS07T001T(); // 請求残高テーブルデータ削除
        deleteS10T001T(); // 入金明細テーブルデータ削除
        deleteS10T003T(); // 入金管理テーブルデータ削除
        deleteS10T004T(); // 入金消込履歴テーブルデータ削除
        deleteS10W001T(); // 入金取込ワークデータ削除
        deleteS10W002T(); // 入金取込得意先ワークデータ削除
        deleteS10W003T(); // 入金消込ワークデータ削除
    }

    /**
     * システム管理マスタデータ削除
     */
    private void deleteS02M006T() {
        S02M006TEntityExample example = new S02M006TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD);
        s02m006tDao.deleteByExample(example);
    }

    /**
     * 担当者マスタデータ削除
     */
    private void deleteS02M003T() {
        S02M003TEntityExample example = new S02M003TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD);
        s02m003tDao.deleteByExample(example);
    }

    /**
     * 得意先マスタデータ削除
     */
    private void deleteS02M013T() {
        S02M013TEntityExample example = new S02M013TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD);
        s02m013tDao.deleteByExample(example);
    }

    /**
     * 請求先マスタデータ削除
     */
    private void deleteS02M017T() {
        S02M017TEntityExample example = new S02M017TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD);
        s02m017tDao.deleteByExample(example);
    }

    /**
     * 得意先実績テーブルデータ削除
     */
    private void deleteS02M050T() {
        S02M050TEntityExample example = new S02M050TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD);
        s02m050tDao.deleteByExample(example);
    }

    /**
     * 請求残高テーブルデータ削除
     */
    private void deleteS07T001T() {
        S07T001TEntityExample example = new S07T001TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD);
        s07t001tDao.deleteByExample(example);
    }

    /**
     * 入金管理テーブルデータ削除
     */
    private void deleteS10T001T() {
        S10T001TEntityExample example = new S10T001TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD);
        s10t001tDao.deleteByExample(example);
    }

    /**
     * 入金管理テーブルデータ削除
     */
    private void deleteS10T003T() {
        S10T003TEntityExample example = new S10T003TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD);
        s10t003tDao.deleteByExample(example);
    }

    /**
     * 入金消込履歴テーブルデータ削除
     */
    private void deleteS10T004T() {
        S10T004TEntityExample example = new S10T004TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD);
        s10t004tDao.deleteByExample(example);
    }

    /**
     * 入金取込ワークデータ削除
     */
    private void deleteS10W001T() {
        S10W001TEntityExample example = new S10W001TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD);
        s10w001tDao.deleteByExample(example);
    }

    /**
     * 入金取込得意先ワークデータ削除
     */
    private void deleteS10W002T() {
        S10W002TEntityExample example = new S10W002TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD);
        s10w002tDao.deleteByExample(example);
    }

    /**
     * 入金消込ワークデータ削除
     */
    private void deleteS10W003T() {
        S10W003TEntityExample example = new S10W003TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(APKIHONCD);
        s10w003tDao.deleteByExample(example);
    }

    /**
     * DB管理セッション情報初期値生成
     * 
     * @return DB管理セッション情報
     */
    private DbManagementSession createDefaultDbManagementSession() {
        DbManagementSession db = new DbManagementSession();
        db.setApkihoncd("AAA");
        db.setShopcd("999");
        db.setTnshacd("000000");
        db.setUnyoDate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        db.setSystemDate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        db.setSystemTime(new SimpleDateFormat("HHmmss").format(new Date()));
        db.setIpAddress("0.0.0.1");
        db.setProgramId("C1005W01");
        return db;
    }

    /**
     * 入金入力ヘッダ情報Dto初期化処理
     * 
     * @param prcskbn 処理区分
     * @param nyknkin 入金金額
     * @param dnpyno 伝票番号
     * @return 入金入力ヘッダ情報Dto
     */
    private C1005W01HeaderDto createDefaultC1005W01HeaderDto(String prcskbn, Long nyknkin,
        String dnpyno) {
        C1005W01HeaderDto dto = new C1005W01HeaderDto();
        dto.setApkihoncd(APKIHONCD);
        dto.setShopcd("000");
        dto.setDnpyno(dnpyno == null ? "R9999999999" : dnpyno);
        dto.setSkysakicd("000000");
        dto.setNyknkind(prcskbn.equals("3") ? "31" : "1");
        dto.setNykndd(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        dto.setNyknkin(nyknkin);
        dto.setNebikikin(0L);
        dto.setTesuryo(0);
        dto.setUrichoseikin(0L);
        dto.setMeisaibiko("ABC");
        dto.setKaishutnshacd("000000");
        dto.setSkyshootptkbn("1");
        dto.setPrcskbn(prcskbn);
        return dto;
    }

    /**
     * 消込対象明細Dtoリスト初期化処理
     * 
     * @param isMinus マイナス金額フラグ
     * @param listSize リスト件数
     * @return 消込対象明細Dtoリスト
     */
    private List<C1005W01MeisaiDto> createDefaultC1005W01MeisaiDto(boolean isMinus, int listSize) {
        List<C1005W01MeisaiDto> meisaiDtoList = new ArrayList<C1005W01MeisaiDto>();
        for (int i = 0; i < listSize; i++) {
            C1005W01MeisaiDto meisaiDto = new C1005W01MeisaiDto();
            meisaiDto.setTokuisakicd("00000" + (i == 0 ? "0" : "1"));
            meisaiDto.setKonkaikeshikomikin(1000L * (i + 1) * (isMinus ? -1 : 1));
            meisaiDto.setKeshikomizd(meisaiDto.getKonkaikeshikomikin());
            meisaiDto.setKeshikomi("1");
            meisaiDtoList.add(meisaiDto);
        }

        return meisaiDtoList;
    }

    /**
     * 消込対象明細Dto生成
     * 
     * @param valueList 値リスト（0: 得意先コード、1:今回消込金額、2:消込残高、3:請求締年月日、4:得意先名称、5:請求書番号、6:消込済金額）
     * @return 消込対象明細Dto
     */
    private C1005W01MeisaiDto createMeisaiDto(Long... valueList) {
        C1005W01MeisaiDto m = new C1005W01MeisaiDto();
        m.setKeshikomi("1");
        int index = 0;
        for (Long value : valueList) {
            String tmp = !value.equals(-1L) ? String.valueOf(value) : null;
            switch (index) {
                case 0:
                    m.setTokuisakicd(ItemUtil.paddingFront(tmp, 6, "0"));
                    break;
                case 1:
                    m.setKonkaikeshikomikin(Long.valueOf(tmp));
                    break;
                case 2:
                    m.setKeshikomizd(Long.valueOf(tmp));
                    break;
                case 3:
                    m.setSkydydmdd(tmp);
                    break;
                case 4:
                    m.setTokuisakinm(Objects.toString(tmp, "").equals("-2") ? "X" : tmp);
                    break;
                case 5:
                    m.setSkyshono(tmp);
                    break;
                case 6:
                    m.setKeshikomizumikin(Long.valueOf(tmp));
                    break;
                default:
                    break;
            }
            index++;
        }
        return m;
    }
}
