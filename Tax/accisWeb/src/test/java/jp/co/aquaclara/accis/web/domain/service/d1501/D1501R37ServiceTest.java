package jp.co.aquaclara.accis.web.domain.service.d1501;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R40ListResultEntity;

@SpringBootTest
class D1501R37ServiceTest extends D1501R37Service {
    /**
     * 請求方法区分:１，２以外 帳票区分：請求書以外 決済金:null
     */
    @Test
    void test07() {
        S07T001TD1501R40ListResultEntity e = new S07T001TD1501R40ListResultEntity();
        List<S07T001TD1501R40ListResultEntity> list = new ArrayList<>();
        e.setYubinno("111-1111");
        e.setSshdshhitoimage("差出人イメージ");
        e.setAdrsn1("請求先住所1");
        e.setAdrsn2("請求先住所2");
        e.setAdrsn3("請求先住所3");
        e.setApkihoncd("AP基本コード");
        e.setShopcd("ショップコード");
        e.setSkysakicd("000001");
        e.setSkysakinmn1("請求先名称1");
        e.setSkysakinmn2("請求先名称2");
        e.setSkysakikeishonm("敬称");
        e.setTaxkeisankbn("1");
        e.setSkyhohokbn("");
        e.setZnkkurikoshikinsum("10");
        e.setKonkainyknkinsum("2");
        e.setKonkaishohitaxkinsum("3");
        e.setKonkaitaxnukiurikinsum("19");
        e.setKurikoshikinsum("4");
        e.setSkyshono("請求書番号");
        e.setTokuisakicd("得意先コード");
        e.setTokuisakinmn1("得意先名称");
        e.setZnkkurikoshikin("5");
        e.setKonkainyknkin("6");
        e.setKonkaitaxnukiurikin("7");
        e.setKonkaishohitaxkin("8");
        e.setKurikoshikin("10");
        e.setKsikin("100");
        list.add(e);
        ArrayList<List<String>> csvData = printDataEdit(list, "201706", "30",
            D1501R00Constants.TYOHYO_KBN_GORIYOMEISAI_HIKIOTOSHI_YOTEI_ARI, "20170630", "image");
        // プログラムID
        assertEquals("\"D1501R37\"", csvData.get(0).get(0));
        // 発行日
        assertEquals("\"20170630\"", csvData.get(0).get(1));
        // 締日
        assertEquals("\"20170630\"", csvData.get(0).get(2));
        // 請求先郵便番号
        assertEquals("\"111-1111\"", csvData.get(0).get(3));
        // ロゴ
        assertEquals("\"image\"", csvData.get(0).get(4));
        // 請求先住所1
        assertEquals("\"請求先住所1\"", csvData.get(0).get(5));
        // 請求先住所2
        assertEquals("\"請求先住所2\"", csvData.get(0).get(6));
        // 請求先住所3
        assertEquals("\"請求先住所3\"", csvData.get(0).get(7));
        // AP基本コード
        assertEquals("\"AP基本コード\"", csvData.get(0).get(8));
        // ショップコード
        assertEquals("\"ショップコード\"", csvData.get(0).get(9));
        // 請求先コード
        assertEquals("\"000001\"", csvData.get(0).get(10));
        // 請求先名称1
        assertEquals("\"請求先名称1\"", csvData.get(0).get(11));
        // 請求先名称2
        assertEquals("\"請求先名称2\"", csvData.get(0).get(12));
        // 敬称
        assertEquals("\"敬称\"", csvData.get(0).get(13));
        // 帳票タイトル
        assertEquals("\"お支払明細書\"", csvData.get(0).get(14));
        // 消費税項目タイトル
        assertEquals("\"内消費税額\"", csvData.get(0).get(15));
        // 前回ご請求額格納
        assertEquals("\"*********\"", csvData.get(0).get(16));
        // ご入金額格納
        assertEquals("\"*********\"", csvData.get(0).get(17));
        // お繰越金額格納
        assertEquals("\"*********\"", csvData.get(0).get(18));
        // 今回ご利用額格納
        assertEquals("\"22\"", csvData.get(0).get(19));
        // 内消費税格納
        assertEquals("\"3\"", csvData.get(0).get(20));
        // 今回ご請求額格納
        assertEquals("\"22\"", csvData.get(0).get(21));
        // 請求書番号
        assertEquals("\"請求書番号\"", csvData.get(0).get(22));
        // 得意先コード
        assertEquals("\"得意先コード\"", csvData.get(0).get(23));
        // 得意先名称
        assertEquals("\"得意先名称\"", csvData.get(0).get(24));
        // 前回ご請求額明細
        assertEquals("\"*********\"", csvData.get(0).get(25));
        // ご入金額明細
        assertEquals("\"*********\"", csvData.get(0).get(26));
        // お繰越金額明細
        assertEquals("\"*********\"", csvData.get(0).get(27));
        // 今回ご利用明細
        assertEquals("\"15\"", csvData.get(0).get(28));
        // 内消費税明細
        assertEquals("\"8\"", csvData.get(0).get(29));
        // 今回ご請求額明細
        assertEquals("\"15\"", csvData.get(0).get(30));
        // 振替予定金額名称
        assertEquals("\"振替予定金額\"", csvData.get(0).get(31));
        // 振替予定金額
        assertEquals("\"100\"", csvData.get(0).get(32));
        assertEquals(33, csvData.get(0).size());
    }

    /**
     * 請求方法区分:１，２以外 帳票区分：請求書以外 決済金:null
     */
    @Test
    void test06() {
        S07T001TD1501R40ListResultEntity e = new S07T001TD1501R40ListResultEntity();
        List<S07T001TD1501R40ListResultEntity> list = new ArrayList<>();
        e.setYubinno("111-1111");
        e.setSshdshhitoimage("差出人イメージ");
        e.setAdrsn1("請求先住所1");
        e.setAdrsn2("請求先住所2");
        e.setAdrsn3("請求先住所3");
        e.setApkihoncd("AP基本コード");
        e.setShopcd("ショップコード");
        e.setSkysakicd("000001");
        e.setSkysakinmn1("請求先名称1");
        e.setSkysakinmn2("請求先名称2");
        e.setSkysakikeishonm("敬称");
        e.setTaxkeisankbn("1");
        e.setSkyhohokbn("");
        e.setZnkkurikoshikinsum("10");
        e.setKonkainyknkinsum("2");
        e.setKonkaishohitaxkinsum("3");
        e.setKonkaitaxnukiurikinsum("19");
        e.setKurikoshikinsum("4");
        e.setSkyshono("請求書番号");
        e.setTokuisakicd("得意先コード");
        e.setTokuisakinmn1("得意先名称");
        e.setZnkkurikoshikin("5");
        e.setKonkainyknkin("6");
        e.setKonkaitaxnukiurikin("7");
        e.setKonkaishohitaxkin("8");
        e.setKurikoshikin("10");
        e.setKsikin(null);
        list.add(e);
        ArrayList<List<String>> csvData = printDataEdit(list, "201706", "30",
            D1501R00Constants.TYOHYO_KBN_GORIYOMEISAI_HIKIOTOSHI_YOTEI_ARI, "20170630", "image");
        // プログラムID
        assertEquals("\"D1501R37\"", csvData.get(0).get(0));
        // 発行日
        assertEquals("\"20170630\"", csvData.get(0).get(1));
        // 締日
        assertEquals("\"20170630\"", csvData.get(0).get(2));
        // 請求先郵便番号
        assertEquals("\"111-1111\"", csvData.get(0).get(3));
        // ロゴ
        assertEquals("\"image\"", csvData.get(0).get(4));
        // 請求先住所1
        assertEquals("\"請求先住所1\"", csvData.get(0).get(5));
        // 請求先住所2
        assertEquals("\"請求先住所2\"", csvData.get(0).get(6));
        // 請求先住所3
        assertEquals("\"請求先住所3\"", csvData.get(0).get(7));
        // AP基本コード
        assertEquals("\"AP基本コード\"", csvData.get(0).get(8));
        // ショップコード
        assertEquals("\"ショップコード\"", csvData.get(0).get(9));
        // 請求先コード
        assertEquals("\"000001\"", csvData.get(0).get(10));
        // 請求先名称1
        assertEquals("\"請求先名称1\"", csvData.get(0).get(11));
        // 請求先名称2
        assertEquals("\"請求先名称2\"", csvData.get(0).get(12));
        // 敬称
        assertEquals("\"敬称\"", csvData.get(0).get(13));
        // 帳票タイトル
        assertEquals("\"お支払明細書\"", csvData.get(0).get(14));
        // 消費税項目タイトル
        assertEquals("\"内消費税額\"", csvData.get(0).get(15));
        // 前回ご請求額格納
        assertEquals("\"*********\"", csvData.get(0).get(16));
        // ご入金額格納
        assertEquals("\"*********\"", csvData.get(0).get(17));
        // お繰越金額格納
        assertEquals("\"*********\"", csvData.get(0).get(18));
        // 今回ご利用額格納
        assertEquals("\"22\"", csvData.get(0).get(19));
        // 内消費税格納
        assertEquals("\"3\"", csvData.get(0).get(20));
        // 今回ご請求額格納
        assertEquals("\"22\"", csvData.get(0).get(21));
        // 請求書番号
        assertEquals("\"請求書番号\"", csvData.get(0).get(22));
        // 得意先コード
        assertEquals("\"得意先コード\"", csvData.get(0).get(23));
        // 得意先名称
        assertEquals("\"得意先名称\"", csvData.get(0).get(24));
        // 前回ご請求額明細
        assertEquals("\"*********\"", csvData.get(0).get(25));
        // ご入金額明細
        assertEquals("\"*********\"", csvData.get(0).get(26));
        // お繰越金額明細
        assertEquals("\"*********\"", csvData.get(0).get(27));
        // 今回ご利用明細
        assertEquals("\"15\"", csvData.get(0).get(28));
        // 内消費税明細
        assertEquals("\"8\"", csvData.get(0).get(29));
        // 今回ご請求額明細
        assertEquals("\"15\"", csvData.get(0).get(30));
        // 振替予定金額名称
        assertEquals("\"振替予定金額\"", csvData.get(0).get(31));
        // 振替予定金額
        assertEquals("", csvData.get(0).get(32));
        assertEquals(33, csvData.get(0).size());
    }

    /**
     * 請求方法区分:2 帳票区分：請求書以外 決済金:null
     */
    @Test
    void test05() {
        S07T001TD1501R40ListResultEntity e = new S07T001TD1501R40ListResultEntity();
        List<S07T001TD1501R40ListResultEntity> list = new ArrayList<>();
        e.setYubinno("111-1111");
        e.setSshdshhitoimage("差出人イメージ");
        e.setAdrsn1("請求先住所1");
        e.setAdrsn2("請求先住所2");
        e.setAdrsn3("請求先住所3");
        e.setApkihoncd("AP基本コード");
        e.setShopcd("ショップコード");
        e.setSkysakicd("000001");
        e.setSkysakinmn1("請求先名称1");
        e.setSkysakinmn2("請求先名称2");
        e.setSkysakikeishonm("敬称");
        e.setTaxkeisankbn("1");
        e.setSkyhohokbn("2");
        e.setZnkkurikoshikinsum("10");
        e.setKonkainyknkinsum("2");
        e.setKonkaishohitaxkinsum("3");
        e.setKonkaitaxnukiurikinsum("19");
        e.setKurikoshikinsum("4");
        e.setSkyshono("請求書番号");
        e.setTokuisakicd("得意先コード");
        e.setTokuisakinmn1("得意先名称");
        e.setZnkkurikoshikin("5");
        e.setKonkainyknkin("6");
        e.setKonkaitaxnukiurikin("7");
        e.setKonkaishohitaxkin("8");
        e.setKurikoshikin("10");
        e.setKsikin(null);
        list.add(e);
        ArrayList<List<String>> csvData = printDataEdit(list, "201706", "30",
            D1501R00Constants.TYOHYO_KBN_GORIYOMEISAI_HIKIOTOSHI_YOTEI_ARI, "20170630", "image");
        // プログラムID
        assertEquals("\"D1501R37\"", csvData.get(0).get(0));
        // 発行日
        assertEquals("\"20170630\"", csvData.get(0).get(1));
        // 締日
        assertEquals("\"20170630\"", csvData.get(0).get(2));
        // 請求先郵便番号
        assertEquals("\"111-1111\"", csvData.get(0).get(3));
        // ロゴ
        assertEquals("\"image\"", csvData.get(0).get(4));
        // 請求先住所1
        assertEquals("\"請求先住所1\"", csvData.get(0).get(5));
        // 請求先住所2
        assertEquals("\"請求先住所2\"", csvData.get(0).get(6));
        // 請求先住所3
        assertEquals("\"請求先住所3\"", csvData.get(0).get(7));
        // AP基本コード
        assertEquals("\"AP基本コード\"", csvData.get(0).get(8));
        // ショップコード
        assertEquals("\"ショップコード\"", csvData.get(0).get(9));
        // 請求先コード
        assertEquals("\"000001\"", csvData.get(0).get(10));
        // 請求先名称1
        assertEquals("\"請求先名称1\"", csvData.get(0).get(11));
        // 請求先名称2
        assertEquals("\"請求先名称2\"", csvData.get(0).get(12));
        // 敬称
        assertEquals("\"敬称\"", csvData.get(0).get(13));
        // 帳票タイトル
        assertEquals("\"お支払明細書\"", csvData.get(0).get(14));
        // 消費税項目タイトル
        assertEquals("\"内消費税額\"", csvData.get(0).get(15));
        // 前回ご請求額格納
        assertEquals("\"*********\"", csvData.get(0).get(16));
        // ご入金額格納
        assertEquals("\"*********\"", csvData.get(0).get(17));
        // お繰越金額格納
        assertEquals("\"*********\"", csvData.get(0).get(18));
        // 今回ご利用額格納
        assertEquals("\"22\"", csvData.get(0).get(19));
        // 内消費税格納
        assertEquals("\"3\"", csvData.get(0).get(20));
        // 今回ご請求額格納
        assertEquals("\"22\"", csvData.get(0).get(21));
        // 請求書番号
        assertEquals("\"請求書番号\"", csvData.get(0).get(22));
        // 得意先コード
        assertEquals("\"得意先コード\"", csvData.get(0).get(23));
        // 得意先名称
        assertEquals("\"得意先名称\"", csvData.get(0).get(24));
        // 前回ご請求額明細
        assertEquals("\"*********\"", csvData.get(0).get(25));
        // ご入金額明細
        assertEquals("\"*********\"", csvData.get(0).get(26));
        // お繰越金額明細
        assertEquals("\"*********\"", csvData.get(0).get(27));
        // 今回ご利用明細
        assertEquals("\"15\"", csvData.get(0).get(28));
        // 内消費税明細
        assertEquals("\"8\"", csvData.get(0).get(29));
        // 今回ご請求額明細
        assertEquals("\"15\"", csvData.get(0).get(30));
        // 振替予定金額名称
        assertEquals("\"振替予定金額\"", csvData.get(0).get(31));
        // 振替予定金額
        assertEquals("\"15\"", csvData.get(0).get(32));
        assertEquals(33, csvData.get(0).size());
    }

    /**
     * 請求方法区分:1 帳票区分：請求書以外 決済金:null
     */
    @Test
    void test04() {
        S07T001TD1501R40ListResultEntity e = new S07T001TD1501R40ListResultEntity();
        List<S07T001TD1501R40ListResultEntity> list = new ArrayList<>();
        e.setYubinno("111-1111");
        e.setSshdshhitoimage("差出人イメージ");
        e.setAdrsn1("請求先住所1");
        e.setAdrsn2("請求先住所2");
        e.setAdrsn3("請求先住所3");
        e.setApkihoncd("AP基本コード");
        e.setShopcd("ショップコード");
        e.setSkysakicd("000001");
        e.setSkysakinmn1("請求先名称1");
        e.setSkysakinmn2("請求先名称2");
        e.setSkysakikeishonm("敬称");
        e.setTaxkeisankbn("1");
        e.setSkyhohokbn("1");
        e.setZnkkurikoshikinsum("10");
        e.setKonkainyknkinsum("2");
        e.setKonkaishohitaxkinsum("3");
        e.setKonkaitaxnukiurikinsum("19");
        e.setKurikoshikinsum("4");
        e.setSkyshono("請求書番号");
        e.setTokuisakicd("得意先コード");
        e.setTokuisakinmn1("得意先名称");
        e.setZnkkurikoshikin("5");
        e.setKonkainyknkin("6");
        e.setKonkaitaxnukiurikin("7");
        e.setKonkaishohitaxkin("8");
        e.setKurikoshikin("10");
        e.setKsikin(null);
        list.add(e);
        ArrayList<List<String>> csvData = printDataEdit(list, "201706", "30",
            D1501R00Constants.TYOHYO_KBN_GORIYOMEISAI_HIKIOTOSHI_YOTEI_ARI, "20170630", "image");
        // プログラムID
        assertEquals("\"D1501R37\"", csvData.get(0).get(0));
        // 発行日
        assertEquals("\"20170630\"", csvData.get(0).get(1));
        // 締日
        assertEquals("\"20170630\"", csvData.get(0).get(2));
        // 請求先郵便番号
        assertEquals("\"111-1111\"", csvData.get(0).get(3));
        // ロゴ
        assertEquals("\"image\"", csvData.get(0).get(4));
        // 請求先住所1
        assertEquals("\"請求先住所1\"", csvData.get(0).get(5));
        // 請求先住所2
        assertEquals("\"請求先住所2\"", csvData.get(0).get(6));
        // 請求先住所3
        assertEquals("\"請求先住所3\"", csvData.get(0).get(7));
        // AP基本コード
        assertEquals("\"AP基本コード\"", csvData.get(0).get(8));
        // ショップコード
        assertEquals("\"ショップコード\"", csvData.get(0).get(9));
        // 請求先コード
        assertEquals("\"000001\"", csvData.get(0).get(10));
        // 請求先名称1
        assertEquals("\"請求先名称1\"", csvData.get(0).get(11));
        // 請求先名称2
        assertEquals("\"請求先名称2\"", csvData.get(0).get(12));
        // 敬称
        assertEquals("\"敬称\"", csvData.get(0).get(13));
        // 帳票タイトル
        assertEquals("\"お支払明細書\"", csvData.get(0).get(14));
        // 消費税項目タイトル
        assertEquals("\"内消費税額\"", csvData.get(0).get(15));
        // 前回ご請求額格納
        assertEquals("\"10\"", csvData.get(0).get(16));
        // ご入金額格納
        assertEquals("\"2\"", csvData.get(0).get(17));
        // お繰越金額格納
        assertEquals("\"8\"", csvData.get(0).get(18));
        // 今回ご利用額格納
        assertEquals("\"22\"", csvData.get(0).get(19));
        // 内消費税格納
        assertEquals("\"3\"", csvData.get(0).get(20));
        // 今回ご請求額格納
        assertEquals("\"4\"", csvData.get(0).get(21));
        // 請求書番号
        assertEquals("\"請求書番号\"", csvData.get(0).get(22));
        // 得意先コード
        assertEquals("\"得意先コード\"", csvData.get(0).get(23));
        // 得意先名称
        assertEquals("\"得意先名称\"", csvData.get(0).get(24));
        // 前回ご請求額明細
        assertEquals("\"5\"", csvData.get(0).get(25));
        // ご入金額明細
        assertEquals("\"6\"", csvData.get(0).get(26));
        // お繰越金額明細
        assertEquals("\"-1\"", csvData.get(0).get(27));
        // 今回ご利用明細
        assertEquals("\"15\"", csvData.get(0).get(28));
        // 内消費税明細
        assertEquals("\"8\"", csvData.get(0).get(29));
        // 今回ご請求額明細
        assertEquals("\"10\"", csvData.get(0).get(30));
        // 振替予定金額名称
        assertEquals("\"振替予定金額\"", csvData.get(0).get(31));
        // 振替予定金額
        assertEquals("\"10\"", csvData.get(0).get(32));
        assertEquals(33, csvData.get(0).size());
    }

    @Test
    void test03() {
        S07T001TD1501R40ListResultEntity e = new S07T001TD1501R40ListResultEntity();
        List<S07T001TD1501R40ListResultEntity> list = new ArrayList<>();
        e.setTaxkeisankbn("2");
        e.setSkyhohokbn("2");
        list.add(e);
        e = new S07T001TD1501R40ListResultEntity();
        e.setTaxkeisankbn("1");
        e.setSkyhohokbn("1");
        list.add(e);
        ArrayList<List<String>> csvData = printDataEdit(list, "201706", "30",
            D1501R00Constants.TYOHYO_KBN_SKYSHO, "20170630", "image");
        // プログラムID
        assertEquals("\"D1501R37\"", csvData.get(0).get(0));
        // 発行日
        assertEquals("\"20170630\"", csvData.get(0).get(1));
        // 締日
        assertEquals("\"20170630\"", csvData.get(0).get(2));
        // 請求先郵便番号
        assertEquals("", csvData.get(0).get(3));
        // ロゴ
        assertEquals("\"image\"", csvData.get(0).get(4));
        // 請求先住所1
        assertEquals("", csvData.get(0).get(5));
        // 請求先住所2
        assertEquals("", csvData.get(0).get(6));
        // 請求先住所3
        assertEquals("", csvData.get(0).get(7));
        // AP基本コード
        assertEquals("", csvData.get(0).get(8));
        // ショップコード
        assertEquals("", csvData.get(0).get(9));
        // 請求先コード
        assertEquals("", csvData.get(0).get(10));
        // 請求先名称1
        assertEquals("", csvData.get(0).get(11));
        // 請求先名称2
        assertEquals("", csvData.get(0).get(12));
        // 敬称
        assertEquals("", csvData.get(0).get(13));
        // 帳票タイトル
        assertEquals("\"お支払明細書\"", csvData.get(0).get(14));
        // 消費税項目タイトル
        assertEquals("\"消費税額\"", csvData.get(0).get(15));
        // 前回ご請求額格納
        assertEquals("\"*********\"", csvData.get(0).get(16));
        // ご入金額格納
        assertEquals("\"*********\"", csvData.get(0).get(17));
        // お繰越金額格納
        assertEquals("\"*********\"", csvData.get(0).get(18));
        // 今回ご利用額格納
        assertEquals("", csvData.get(0).get(19));
        // 内消費税格納
        assertEquals("", csvData.get(0).get(20));
        // 今回ご請求額格納
        assertEquals("", csvData.get(0).get(21));
        // 請求書番号
        assertEquals("", csvData.get(0).get(22));
        // 得意先コード
        assertEquals("", csvData.get(0).get(23));
        // 得意先名称
        assertEquals("", csvData.get(0).get(24));
        // 前回ご請求額明細
        assertEquals("\"*********\"", csvData.get(0).get(25));
        // ご入金額明細
        assertEquals("\"*********\"", csvData.get(0).get(26));
        // お繰越金額明細
        assertEquals("\"*********\"", csvData.get(0).get(27));
        // 今回ご利用明細
        assertEquals("", csvData.get(0).get(28));
        // 内消費税明細
        assertEquals("", csvData.get(0).get(29));
        // 今回ご請求額明細
        assertEquals("\"0\"", csvData.get(0).get(30));
        // 振替予定金額名称
        assertEquals("", csvData.get(0).get(31));
        // 振替予定金額
        assertEquals("", csvData.get(0).get(32));
        assertEquals(33, csvData.get(0).size());
        // プログラムID
        assertEquals("\"D1501R37\"", csvData.get(1).get(0));
        // 発行日
        assertEquals("\"20170630\"", csvData.get(1).get(1));
        // 締日
        assertEquals("\"20170630\"", csvData.get(1).get(2));
        // 請求先郵便番号
        assertEquals("", csvData.get(1).get(3));
        // ロゴ
        assertEquals("\"image\"", csvData.get(1).get(4));
        // 請求先住所1
        assertEquals("", csvData.get(1).get(5));
        // 請求先住所2
        assertEquals("", csvData.get(1).get(6));
        // 請求先住所3
        assertEquals("", csvData.get(1).get(7));
        // AP基本コード
        assertEquals("", csvData.get(1).get(8));
        // ショップコード
        assertEquals("", csvData.get(1).get(9));
        // 請求先コード
        assertEquals("", csvData.get(1).get(10));
        // 請求先名称1
        assertEquals("", csvData.get(1).get(11));
        // 請求先名称2
        assertEquals("", csvData.get(1).get(12));
        // 敬称
        assertEquals("", csvData.get(1).get(13));
        // 帳票タイトル
        assertEquals("\"お支払明細書\"", csvData.get(1).get(14));
        // 消費税項目タイトル
        assertEquals("\"内消費税額\"", csvData.get(1).get(15));
        // 前回ご請求額格納
        assertEquals("", csvData.get(1).get(16));
        // ご入金額格納
        assertEquals("", csvData.get(1).get(17));
        // お繰越金額格納
        assertEquals("", csvData.get(1).get(18));
        // 今回ご利用額格納
        assertEquals("", csvData.get(1).get(19));
        // 内消費税格納
        assertEquals("", csvData.get(1).get(20));
        // 今回ご請求額格納
        assertEquals("", csvData.get(1).get(21));
        // 請求書番号
        assertEquals("", csvData.get(1).get(22));
        // 得意先コード
        assertEquals("", csvData.get(1).get(23));
        // 得意先名称
        assertEquals("", csvData.get(1).get(24));
        // 前回ご請求額明細
        assertEquals("", csvData.get(1).get(25));
        // ご入金額明細
        assertEquals("", csvData.get(1).get(26));
        // お繰越金額明細
        assertEquals("", csvData.get(1).get(27));
        // 今回ご利用明細
        assertEquals("", csvData.get(1).get(28));
        // 内消費税明細
        assertEquals("", csvData.get(1).get(29));
        // 今回ご請求額明細
        assertEquals("", csvData.get(1).get(30));
        // 振替予定金額名称
        assertEquals("", csvData.get(1).get(31));
        // 振替予定金額
        assertEquals("", csvData.get(1).get(32));
        assertEquals(33, csvData.get(1).size());
    }

    /**
     * 請求方法区分:1以外 税区分2
     */
    @Test
    void test02() {
        S07T001TD1501R40ListResultEntity e = new S07T001TD1501R40ListResultEntity();
        List<S07T001TD1501R40ListResultEntity> list = new ArrayList<>();
        e.setYubinno("111-1111");
        e.setSshdshhitoimage("差出人イメージ");
        e.setAdrsn1("請求先住所1");
        e.setAdrsn2("請求先住所2");
        e.setAdrsn3("請求先住所3");
        e.setApkihoncd("AP基本コード");
        e.setShopcd("ショップコード");
        e.setSkysakicd("000001");
        e.setSkysakinmn1("請求先名称1");
        e.setSkysakinmn2("請求先名称2");
        e.setSkysakikeishonm("敬称");
        e.setTaxkeisankbn("2");
        e.setSkyhohokbn("2");
        e.setZnkkurikoshikinsum("10");
        e.setKonkainyknkinsum("2");
        e.setKonkaishohitaxkinsum("3");
        e.setKonkaitaxnukiurikinsum("19");
        e.setKurikoshikinsum("4");
        e.setSkyshono("請求書番号");
        e.setTokuisakicd("得意先コード");
        e.setTokuisakinmn1("得意先名称");
        e.setZnkkurikoshikin("5");
        e.setKonkainyknkin("6");
        e.setKonkaitaxnukiurikin("7");
        e.setKonkaishohitaxkin("8");
        e.setKurikoshikin("10");
        list.add(e);
        ArrayList<List<String>> csvData = printDataEdit(list, "201706", "30",
            D1501R00Constants.TYOHYO_KBN_SKYSHO, "20170630", "image");
        // プログラムID
        assertEquals("\"D1501R37\"", csvData.get(0).get(0));
        // 発行日
        assertEquals("\"20170630\"", csvData.get(0).get(1));
        // 締日
        assertEquals("\"20170630\"", csvData.get(0).get(2));
        // 請求先郵便番号
        assertEquals("\"111-1111\"", csvData.get(0).get(3));
        // ロゴ
        assertEquals("\"image\"", csvData.get(0).get(4));
        // 請求先住所1
        assertEquals("\"請求先住所1\"", csvData.get(0).get(5));
        // 請求先住所2
        assertEquals("\"請求先住所2\"", csvData.get(0).get(6));
        // 請求先住所3
        assertEquals("\"請求先住所3\"", csvData.get(0).get(7));
        // AP基本コード
        assertEquals("\"AP基本コード\"", csvData.get(0).get(8));
        // ショップコード
        assertEquals("\"ショップコード\"", csvData.get(0).get(9));
        // 請求先コード
        assertEquals("\"000001\"", csvData.get(0).get(10));
        // 請求先名称1
        assertEquals("\"請求先名称1\"", csvData.get(0).get(11));
        // 請求先名称2
        assertEquals("\"請求先名称2\"", csvData.get(0).get(12));
        // 敬称
        assertEquals("\"敬称\"", csvData.get(0).get(13));
        // 帳票タイトル
        assertEquals("\"お支払明細書\"", csvData.get(0).get(14));
        // 消費税項目タイトル
        assertEquals("\"消費税額\"", csvData.get(0).get(15));
        // 前回ご請求額格納
        assertEquals("\"*********\"", csvData.get(0).get(16));
        // ご入金額格納
        assertEquals("\"*********\"", csvData.get(0).get(17));
        // お繰越金額格納
        assertEquals("\"*********\"", csvData.get(0).get(18));
        // 今回ご利用額格納
        assertEquals("\"19\"", csvData.get(0).get(19));
        // 内消費税格納
        assertEquals("\"3\"", csvData.get(0).get(20));
        // 今回ご請求額格納
        assertEquals("\"22\"", csvData.get(0).get(21));
        // 請求書番号
        assertEquals("\"請求書番号\"", csvData.get(0).get(22));
        // 得意先コード
        assertEquals("\"得意先コード\"", csvData.get(0).get(23));
        // 得意先名称
        assertEquals("\"得意先名称\"", csvData.get(0).get(24));
        // 前回ご請求額明細
        assertEquals("\"*********\"", csvData.get(0).get(25));
        // ご入金額明細
        assertEquals("\"*********\"", csvData.get(0).get(26));
        // お繰越金額明細
        assertEquals("\"*********\"", csvData.get(0).get(27));
        // 今回ご利用明細
        assertEquals("\"7\"", csvData.get(0).get(28));
        // 内消費税明細
        assertEquals("\"8\"", csvData.get(0).get(29));
        // 今回ご請求額明細
        assertEquals("\"15\"", csvData.get(0).get(30));
        // 振替予定金額名称
        assertEquals("", csvData.get(0).get(31));
        // 振替予定金額
        assertEquals("", csvData.get(0).get(32));
        assertEquals(33, csvData.get(0).size());
    }

    /**
     * 請求方法区分:1 税区分1
     */
    @Test
    void test01() {
        S07T001TD1501R40ListResultEntity e = new S07T001TD1501R40ListResultEntity();
        List<S07T001TD1501R40ListResultEntity> list = new ArrayList<>();
        e.setYubinno("111-1111");
        e.setSshdshhitoimage("差出人イメージ");
        e.setAdrsn1("請求先住所1");
        e.setAdrsn2("請求先住所2");
        e.setAdrsn3("請求先住所3");
        e.setApkihoncd("AP基本コード");
        e.setShopcd("ショップコード");
        e.setSkysakicd("000001");
        e.setSkysakinmn1("請求先名称1");
        e.setSkysakinmn2("請求先名称2");
        e.setSkysakikeishonm("敬称");
        e.setTaxkeisankbn("1");
        e.setSkyhohokbn("1");
        e.setZnkkurikoshikinsum("10");
        e.setKonkainyknkinsum("2");
        e.setKonkaishohitaxkinsum("3");
        e.setKonkaitaxnukiurikinsum("19");
        e.setKurikoshikinsum("4");
        e.setSkyshono("請求書番号");
        e.setTokuisakicd("得意先コード");
        e.setTokuisakinmn1("得意先名称");
        e.setZnkkurikoshikin("5");
        e.setKonkainyknkin("6");
        e.setKonkaitaxnukiurikin("7");
        e.setKonkaishohitaxkin("8");
        e.setKurikoshikin("10");
        list.add(e);
        ArrayList<List<String>> csvData = printDataEdit(list, "201706", "30",
            D1501R00Constants.TYOHYO_KBN_SKYSHO, "20170630", "image");
        // プログラムID
        assertEquals("\"D1501R37\"", csvData.get(0).get(0));
        // 発行日
        assertEquals("\"20170630\"", csvData.get(0).get(1));
        // 締日
        assertEquals("\"20170630\"", csvData.get(0).get(2));
        // 請求先郵便番号
        assertEquals("\"111-1111\"", csvData.get(0).get(3));
        // ロゴ
        assertEquals("\"image\"", csvData.get(0).get(4));
        // 請求先住所1
        assertEquals("\"請求先住所1\"", csvData.get(0).get(5));
        // 請求先住所2
        assertEquals("\"請求先住所2\"", csvData.get(0).get(6));
        // 請求先住所3
        assertEquals("\"請求先住所3\"", csvData.get(0).get(7));
        // AP基本コード
        assertEquals("\"AP基本コード\"", csvData.get(0).get(8));
        // ショップコード
        assertEquals("\"ショップコード\"", csvData.get(0).get(9));
        // 請求先コード
        assertEquals("\"000001\"", csvData.get(0).get(10));
        // 請求先名称1
        assertEquals("\"請求先名称1\"", csvData.get(0).get(11));
        // 請求先名称2
        assertEquals("\"請求先名称2\"", csvData.get(0).get(12));
        // 敬称
        assertEquals("\"敬称\"", csvData.get(0).get(13));
        // 帳票タイトル
        assertEquals("\"お支払明細書\"", csvData.get(0).get(14));
        // 消費税項目タイトル
        assertEquals("\"内消費税額\"", csvData.get(0).get(15));
        // 前回ご請求額格納
        assertEquals("\"10\"", csvData.get(0).get(16));
        // ご入金額格納
        assertEquals("\"2\"", csvData.get(0).get(17));
        // お繰越金額格納
        assertEquals("\"8\"", csvData.get(0).get(18));
        // 今回ご利用額格納
        assertEquals("\"22\"", csvData.get(0).get(19));
        // 内消費税格納
        assertEquals("\"3\"", csvData.get(0).get(20));
        // 今回ご請求額格納
        assertEquals("\"4\"", csvData.get(0).get(21));
        // 請求書番号
        assertEquals("\"請求書番号\"", csvData.get(0).get(22));
        // 得意先コード
        assertEquals("\"得意先コード\"", csvData.get(0).get(23));
        // 得意先名称
        assertEquals("\"得意先名称\"", csvData.get(0).get(24));
        // 前回ご請求額明細
        assertEquals("\"5\"", csvData.get(0).get(25));
        // ご入金額明細
        assertEquals("\"6\"", csvData.get(0).get(26));
        // お繰越金額明細
        assertEquals("\"-1\"", csvData.get(0).get(27));
        // 今回ご利用明細
        assertEquals("\"15\"", csvData.get(0).get(28));
        // 内消費税明細
        assertEquals("\"8\"", csvData.get(0).get(29));
        // 今回ご請求額明細
        assertEquals("\"10\"", csvData.get(0).get(30));
        // 振替予定金額名称
        assertEquals("", csvData.get(0).get(31));
        // 振替予定金額
        assertEquals("", csvData.get(0).get(32));
        assertEquals(33, csvData.get(0).size());
    }

}
