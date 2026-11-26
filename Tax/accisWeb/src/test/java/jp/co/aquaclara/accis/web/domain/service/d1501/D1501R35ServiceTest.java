package jp.co.aquaclara.accis.web.domain.service.d1501;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R35ListResultEntity;

@SpringBootTest
class D1501R35ServiceTest extends D1501R35Service {


    /**
     * entity 明細印刷区分:2（親子一緒） 請求先コード!＝得意先コード
     */
    @Test
    void test19() {
        S07T001TD1501R35ListResultEntity e = new S07T001TD1501R35ListResultEntity();
        List<S07T001TD1501R35ListResultEntity> list = new ArrayList<>();
        e.setMeisaiprntkbn("2");
        e.setTokuisakicd("1");
        e.setSkysakicd("1");
        list.add(e);
        ArrayList<List<String>> csvData = printDataEdit(list, "20170630", "002-036-002036.bmp");
        // csvデータの件数確認
        assertEquals(1, csvData.size());
        assertEquals(20, csvData.get(0).size());
        assertEquals("D1501R35", csvData.get(0).get(0));
        assertEquals("20170630", csvData.get(0).get(1));
        assertEquals("", csvData.get(0).get(2));
        assertEquals("\"002-036-002036.bmp\"", csvData.get(0).get(3));
        assertEquals("", csvData.get(0).get(4));
        assertEquals("", csvData.get(0).get(5));
        assertEquals("", csvData.get(0).get(6));
        assertEquals("", csvData.get(0).get(7));
        assertEquals("", csvData.get(0).get(8));
        assertEquals("", csvData.get(0).get(9));
        assertEquals("", csvData.get(0).get(10));
        assertEquals("1", csvData.get(0).get(11));
        assertEquals("", csvData.get(0).get(12));
        assertEquals("", csvData.get(0).get(13));
        assertEquals("", csvData.get(0).get(14));
        assertEquals("", csvData.get(0).get(15));
        assertEquals("", csvData.get(0).get(16));
        assertEquals("", csvData.get(0).get(17));
        assertEquals("", csvData.get(0).get(18));
        assertEquals("", csvData.get(0).get(19));
    }

    /**
     * entity 明細印刷区分:2（親子一緒） 請求先コード＝得意先コード
     */
    @Test
    void test18() {
        S07T001TD1501R35ListResultEntity e = new S07T001TD1501R35ListResultEntity();
        List<S07T001TD1501R35ListResultEntity> list = new ArrayList<>();
        e.setMeisaiprntkbn("2");
        e.setTokuisakicd("1");
        e.setSkysakicd("1");
        list.add(e);
        ArrayList<List<String>> csvData = printDataEdit(list, "20170630", "002-036-002036.bmp");
        // csvデータの件数確認
        assertEquals(1, csvData.size());
        assertEquals(20, csvData.get(0).size());
        assertEquals("D1501R35", csvData.get(0).get(0));
        assertEquals("20170630", csvData.get(0).get(1));
        assertEquals("", csvData.get(0).get(2));
        assertEquals("\"002-036-002036.bmp\"", csvData.get(0).get(3));
        assertEquals("", csvData.get(0).get(4));
        assertEquals("", csvData.get(0).get(5));
        assertEquals("", csvData.get(0).get(6));
        assertEquals("", csvData.get(0).get(7));
        assertEquals("", csvData.get(0).get(8));
        assertEquals("", csvData.get(0).get(9));
        assertEquals("", csvData.get(0).get(10));
        assertEquals("1", csvData.get(0).get(11));
        assertEquals("", csvData.get(0).get(12));
        assertEquals("", csvData.get(0).get(13));
        assertEquals("", csvData.get(0).get(14));
        assertEquals("", csvData.get(0).get(15));
        assertEquals("", csvData.get(0).get(16));
        assertEquals("", csvData.get(0).get(17));
        assertEquals("", csvData.get(0).get(18));
        assertEquals("", csvData.get(0).get(19));
    }

    /**
     * entity 明細印刷区分:1（親子一緒）
     */
    @Test
    void test17() {
        S07T001TD1501R35ListResultEntity e = new S07T001TD1501R35ListResultEntity();
        List<S07T001TD1501R35ListResultEntity> list = new ArrayList<>();
        e.setMeisaiprntkbn("1");
        list.add(e);
        ArrayList<List<String>> csvData = printDataEdit(list, "20170630", "002-036-002036.bmp");
        // csvデータの件数確認
        assertEquals(1, csvData.size());
        assertEquals(20, csvData.get(0).size());
        assertEquals("D1501R35", csvData.get(0).get(0));
        assertEquals("20170630", csvData.get(0).get(1));
        assertEquals("", csvData.get(0).get(2));
        assertEquals("\"002-036-002036.bmp\"", csvData.get(0).get(3));
        assertEquals("", csvData.get(0).get(4));
        assertEquals("", csvData.get(0).get(5));
        assertEquals("", csvData.get(0).get(6));
        assertEquals("", csvData.get(0).get(7));
        assertEquals("", csvData.get(0).get(8));
        assertEquals("", csvData.get(0).get(9));
        assertEquals("", csvData.get(0).get(10));
        assertEquals("", csvData.get(0).get(11));
        assertEquals("", csvData.get(0).get(12));
        assertEquals("", csvData.get(0).get(13));
        assertEquals("", csvData.get(0).get(14));
        assertEquals("", csvData.get(0).get(15));
        assertEquals("", csvData.get(0).get(16));
        assertEquals("", csvData.get(0).get(17));
        assertEquals("", csvData.get(0).get(18));
        assertEquals("", csvData.get(0).get(19));
    }

    /**
     * entity１件 得意先連絡先番号区分4：4以外
     */
    @Test
    void test16() {
        S07T001TD1501R35ListResultEntity e = new S07T001TD1501R35ListResultEntity();
        List<S07T001TD1501R35ListResultEntity> list = new ArrayList<>();
        e.setApkihoncd("002");
        e.setSkyshono("000001");
        e.setShopcd("036");
        e.setKurikoshikin("1000");
        e.setKonkaitaxnukiurikin("2000");
        e.setKonkaishohitaxkin("200");
        e.setSkysakicd("000002");
        e.setTokuisakicd("000002");
        e.setShukindydmdd("20170730");
        e.setSkykbn("1");
        // 請求方法区分
        e.setSkyhohokbn("2");
        // 明細印刷区分
        e.setMeisaiprntkbn("2");
        e.setSksyubinno("請求先郵便番号");
        e.setSksadrsn1("請求先住所１");
        e.setSksadrsn2("請求先住所２");
        e.setSksadrsn3("請求先住所３");
        e.setSksrenrakusakinokbnn1("4");
        e.setSksrenrakusakinon1("08011111111");
        e.setSksrenrakusakinokbnn2("4");
        e.setSksrenrakusakinon2("08022222222");
        e.setSksrenrakusakinokbnn3("2");
        e.setSksrenrakusakinon3("08033333333");
        e.setSksrenrakusakinokbnn4("1");
        e.setSksrenrakusakinon4("08044444444");
        e.setSksskysakinmn1("請求先名称１");
        e.setSksskysakinmn2("請求先名称２");
        e.setSksskysakikeishokbn("1");
        e.setSksskysakikeishokbnnm("請求先敬称");
        e.setTksyubinno("得意先郵便番号");
        e.setTksadrsn1("得意先住所１");
        e.setTksadrsn2("得意先住所２");
        e.setTksadrsn3("得意先住所３");
        e.setTksrenrakusakinokbnn1("4");
        e.setTksrenrakusakinon1("09011111111");
        e.setTksrenrakusakinokbnn2("4");
        e.setTksrenrakusakinon2("09022222222");
        e.setTksrenrakusakinokbnn3("4");
        e.setTksrenrakusakinon3("09033333333");
        e.setTksrenrakusakinokbnn4("1");
        e.setTksrenrakusakinon4("09044444444");
        e.setTkstokuisakinmn1("得意先名称１");
        e.setTkstokuisakinmn2("得意先名称２");
        e.setTkstokuisakikeishokbn("1");
        e.setTkstokuisakikeishokbnnm("得意先敬称");
        e.setTkstokuisakikbn("1");
        e.setTnshamei("担当者名");
        e.setShopnm("店名");
        e.setSshdshhitoimage("イメージ");
        e.setKaishuhohokbn("1");
        e.setRyosyukin("");
        list.add(e);
        ArrayList<List<String>> csvData = printDataEdit(list, "20170630", "002-036-002036.bmp");
        // csvデータの件数確認
        assertEquals(1, csvData.size());
        assertEquals(20, csvData.get(0).size());
        assertEquals("D1501R35", csvData.get(0).get(0));
        assertEquals("20170630", csvData.get(0).get(1));
        assertEquals("\"000001\"", csvData.get(0).get(2));
        assertEquals("\"002-036-002036.bmp\"", csvData.get(0).get(3));
        assertEquals("\"得意先郵便番号\"", csvData.get(0).get(4));
        assertEquals("\"得意先住所１\"", csvData.get(0).get(5));
        assertEquals("\"得意先住所２\"", csvData.get(0).get(6));
        assertEquals("\"得意先住所３\"", csvData.get(0).get(7));
        assertEquals("\"09044444444\"", csvData.get(0).get(8));
        assertEquals("002", csvData.get(0).get(9));
        assertEquals("036", csvData.get(0).get(10));
        assertEquals("000002", csvData.get(0).get(11));
        assertEquals("\"得意先名称１\"", csvData.get(0).get(12));
        assertEquals("\"得意先名称２\"", csvData.get(0).get(13));
        assertEquals("得意先敬称", csvData.get(0).get(14));
        assertEquals("\"取扱店：店名\"", csvData.get(0).get(15));
        assertEquals("\"担当者名\"", csvData.get(0).get(16));
        assertEquals("2200", csvData.get(0).get(17));
        assertEquals("", csvData.get(0).get(18));
        assertEquals("20170730", csvData.get(0).get(19));
    }

    /**
     * entity１件 得意先連絡先番号区分3：4以外
     */
    @Test
    void test15() {
        S07T001TD1501R35ListResultEntity e = new S07T001TD1501R35ListResultEntity();
        List<S07T001TD1501R35ListResultEntity> list = new ArrayList<>();
        e.setApkihoncd("002");
        e.setSkyshono("000001");
        e.setShopcd("036");
        e.setKurikoshikin("1000");
        e.setKonkaitaxnukiurikin("2000");
        e.setKonkaishohitaxkin("200");
        e.setSkysakicd("000002");
        e.setTokuisakicd("000002");
        e.setShukindydmdd("20170730");
        e.setSkykbn("1");
        // 請求方法区分
        e.setSkyhohokbn("2");
        // 明細印刷区分
        e.setMeisaiprntkbn("2");
        e.setSksyubinno("請求先郵便番号");
        e.setSksadrsn1("請求先住所１");
        e.setSksadrsn2("請求先住所２");
        e.setSksadrsn3("請求先住所３");
        e.setSksrenrakusakinokbnn1("4");
        e.setSksrenrakusakinon1("08011111111");
        e.setSksrenrakusakinokbnn2("4");
        e.setSksrenrakusakinon2("08022222222");
        e.setSksrenrakusakinokbnn3("2");
        e.setSksrenrakusakinon3("08033333333");
        e.setSksrenrakusakinokbnn4("1");
        e.setSksrenrakusakinon4("08044444444");
        e.setSksskysakinmn1("請求先名称１");
        e.setSksskysakinmn2("請求先名称２");
        e.setSksskysakikeishokbn("1");
        e.setSksskysakikeishokbnnm("請求先敬称");
        e.setTksyubinno("得意先郵便番号");
        e.setTksadrsn1("得意先住所１");
        e.setTksadrsn2("得意先住所２");
        e.setTksadrsn3("得意先住所３");
        e.setTksrenrakusakinokbnn1("4");
        e.setTksrenrakusakinon1("09011111111");
        e.setTksrenrakusakinokbnn2("4");
        e.setTksrenrakusakinon2("09022222222");
        e.setTksrenrakusakinokbnn3("3");
        e.setTksrenrakusakinon3("09033333333");
        e.setTksrenrakusakinokbnn4("1");
        e.setTksrenrakusakinon4("09044444444");
        e.setTkstokuisakinmn1("得意先名称１");
        e.setTkstokuisakinmn2("得意先名称２");
        e.setTkstokuisakikeishokbn("1");
        e.setTkstokuisakikeishokbnnm("得意先敬称");
        e.setTkstokuisakikbn("1");
        e.setTnshamei("担当者名");
        e.setShopnm("店名");
        e.setSshdshhitoimage("イメージ");
        e.setKaishuhohokbn("1");
        e.setRyosyukin("");
        list.add(e);
        ArrayList<List<String>> csvData = printDataEdit(list, "20170630", "002-036-002036.bmp");
        // csvデータの件数確認
        assertEquals(1, csvData.size());
        assertEquals(20, csvData.get(0).size());
        assertEquals("D1501R35", csvData.get(0).get(0));
        assertEquals("20170630", csvData.get(0).get(1));
        assertEquals("\"000001\"", csvData.get(0).get(2));
        assertEquals("\"002-036-002036.bmp\"", csvData.get(0).get(3));
        assertEquals("\"得意先郵便番号\"", csvData.get(0).get(4));
        assertEquals("\"得意先住所１\"", csvData.get(0).get(5));
        assertEquals("\"得意先住所２\"", csvData.get(0).get(6));
        assertEquals("\"得意先住所３\"", csvData.get(0).get(7));
        assertEquals("\"09033333333\"", csvData.get(0).get(8));
        assertEquals("002", csvData.get(0).get(9));
        assertEquals("036", csvData.get(0).get(10));
        assertEquals("000002", csvData.get(0).get(11));
        assertEquals("\"得意先名称１\"", csvData.get(0).get(12));
        assertEquals("\"得意先名称２\"", csvData.get(0).get(13));
        assertEquals("得意先敬称", csvData.get(0).get(14));
        assertEquals("\"取扱店：店名\"", csvData.get(0).get(15));
        assertEquals("\"担当者名\"", csvData.get(0).get(16));
        assertEquals("2200", csvData.get(0).get(17));
        assertEquals("", csvData.get(0).get(18));
        assertEquals("20170730", csvData.get(0).get(19));
    }

    /**
     * entity１件 得意先連絡先番号区分2：4以外
     */
    @Test
    void test14() {
        S07T001TD1501R35ListResultEntity e = new S07T001TD1501R35ListResultEntity();
        List<S07T001TD1501R35ListResultEntity> list = new ArrayList<>();
        e.setApkihoncd("002");
        e.setSkyshono("000001");
        e.setShopcd("036");
        e.setKurikoshikin("1000");
        e.setKonkaitaxnukiurikin("2000");
        e.setKonkaishohitaxkin("200");
        e.setSkysakicd("000002");
        e.setTokuisakicd("000002");
        e.setShukindydmdd("20170730");
        e.setSkykbn("1");
        // 請求方法区分
        e.setSkyhohokbn("2");
        // 明細印刷区分
        e.setMeisaiprntkbn("2");
        e.setSksyubinno("請求先郵便番号");
        e.setSksadrsn1("請求先住所１");
        e.setSksadrsn2("請求先住所２");
        e.setSksadrsn3("請求先住所３");
        e.setSksrenrakusakinokbnn1("4");
        e.setSksrenrakusakinon1("08011111111");
        e.setSksrenrakusakinokbnn2("3");
        e.setSksrenrakusakinon2("08022222222");
        e.setSksrenrakusakinokbnn3("2");
        e.setSksrenrakusakinon3("08033333333");
        e.setSksrenrakusakinokbnn4("1");
        e.setSksrenrakusakinon4("08044444444");
        e.setSksskysakinmn1("請求先名称１");
        e.setSksskysakinmn2("請求先名称２");
        e.setSksskysakikeishokbn("1");
        e.setSksskysakikeishokbnnm("請求先敬称");
        e.setTksyubinno("得意先郵便番号");
        e.setTksadrsn1("得意先住所１");
        e.setTksadrsn2("得意先住所２");
        e.setTksadrsn3("得意先住所３");
        e.setTksrenrakusakinokbnn1("4");
        e.setTksrenrakusakinon1("09011111111");
        e.setTksrenrakusakinokbnn2("2");
        e.setTksrenrakusakinon2("09022222222");
        e.setTksrenrakusakinokbnn3("1");
        e.setTksrenrakusakinon3("09033333333");
        e.setTksrenrakusakinokbnn4("2");
        e.setTksrenrakusakinon4("09044444444");
        e.setTkstokuisakinmn1("得意先名称１");
        e.setTkstokuisakinmn2("得意先名称２");
        e.setTkstokuisakikeishokbn("1");
        e.setTkstokuisakikeishokbnnm("得意先敬称");
        e.setTkstokuisakikbn("1");
        e.setTnshamei("担当者名");
        e.setShopnm("店名");
        e.setSshdshhitoimage("イメージ");
        e.setKaishuhohokbn("1");
        e.setRyosyukin("");
        list.add(e);
        ArrayList<List<String>> csvData = printDataEdit(list, "20170630", "002-036-002036.bmp");
        // csvデータの件数確認
        assertEquals(1, csvData.size());
        assertEquals(20, csvData.get(0).size());
        assertEquals("D1501R35", csvData.get(0).get(0));
        assertEquals("20170630", csvData.get(0).get(1));
        assertEquals("\"000001\"", csvData.get(0).get(2));
        assertEquals("\"002-036-002036.bmp\"", csvData.get(0).get(3));
        assertEquals("\"得意先郵便番号\"", csvData.get(0).get(4));
        assertEquals("\"得意先住所１\"", csvData.get(0).get(5));
        assertEquals("\"得意先住所２\"", csvData.get(0).get(6));
        assertEquals("\"得意先住所３\"", csvData.get(0).get(7));
        assertEquals("\"09022222222\"", csvData.get(0).get(8));
        assertEquals("002", csvData.get(0).get(9));
        assertEquals("036", csvData.get(0).get(10));
        assertEquals("000002", csvData.get(0).get(11));
        assertEquals("\"得意先名称１\"", csvData.get(0).get(12));
        assertEquals("\"得意先名称２\"", csvData.get(0).get(13));
        assertEquals("得意先敬称", csvData.get(0).get(14));
        assertEquals("\"取扱店：店名\"", csvData.get(0).get(15));
        assertEquals("\"担当者名\"", csvData.get(0).get(16));
        assertEquals("2200", csvData.get(0).get(17));
        assertEquals("", csvData.get(0).get(18));
        assertEquals("20170730", csvData.get(0).get(19));
    }

    /**
     * entity１件 得意先連絡先番号区分1：4以外
     */
    @Test
    void test13() {
        S07T001TD1501R35ListResultEntity e = new S07T001TD1501R35ListResultEntity();
        List<S07T001TD1501R35ListResultEntity> list = new ArrayList<>();
        e.setApkihoncd("002");
        e.setSkyshono("000001");
        e.setShopcd("036");
        e.setKurikoshikin("1000");
        e.setKonkaitaxnukiurikin("2000");
        e.setKonkaishohitaxkin("200");
        e.setSkysakicd("000002");
        e.setTokuisakicd("000002");
        e.setShukindydmdd("20170730");
        e.setSkykbn("1");
        // 請求方法区分
        e.setSkyhohokbn("2");
        // 明細印刷区分
        e.setMeisaiprntkbn("2");
        e.setSksyubinno("請求先郵便番号");
        e.setSksadrsn1("請求先住所１");
        e.setSksadrsn2("請求先住所２");
        e.setSksadrsn3("請求先住所３");
        e.setSksrenrakusakinokbnn1("4");
        e.setSksrenrakusakinon1("08011111111");
        e.setSksrenrakusakinokbnn2("3");
        e.setSksrenrakusakinon2("08022222222");
        e.setSksrenrakusakinokbnn3("2");
        e.setSksrenrakusakinon3("08033333333");
        e.setSksrenrakusakinokbnn4("1");
        e.setSksrenrakusakinon4("08044444444");
        e.setSksskysakinmn1("請求先名称１");
        e.setSksskysakinmn2("請求先名称２");
        e.setSksskysakikeishokbn("1");
        e.setSksskysakikeishokbnnm("請求先敬称");
        e.setTksyubinno("得意先郵便番号");
        e.setTksadrsn1("得意先住所１");
        e.setTksadrsn2("得意先住所２");
        e.setTksadrsn3("得意先住所３");
        e.setTksrenrakusakinokbnn1("1");
        e.setTksrenrakusakinon1("09011111111");
        e.setTksrenrakusakinokbnn2("2");
        e.setTksrenrakusakinon2("09022222222");
        e.setTksrenrakusakinokbnn3("3");
        e.setTksrenrakusakinon3("09033333333");
        e.setTksrenrakusakinokbnn4("1");
        e.setTksrenrakusakinon4("09044444444");
        e.setTkstokuisakinmn1("得意先名称１");
        e.setTkstokuisakinmn2("得意先名称２");
        e.setTkstokuisakikeishokbn("1");
        e.setTkstokuisakikeishokbnnm("得意先敬称");
        e.setTkstokuisakikbn("1");
        e.setTnshamei("担当者名");
        e.setShopnm("店名");
        e.setSshdshhitoimage("イメージ");
        e.setKaishuhohokbn("1");
        e.setRyosyukin("");
        list.add(e);
        ArrayList<List<String>> csvData = printDataEdit(list, "20170630", "002-036-002036.bmp");
        // csvデータの件数確認
        assertEquals(1, csvData.size());
        assertEquals(20, csvData.get(0).size());
        assertEquals("D1501R35", csvData.get(0).get(0));
        assertEquals("20170630", csvData.get(0).get(1));
        assertEquals("\"000001\"", csvData.get(0).get(2));
        assertEquals("\"002-036-002036.bmp\"", csvData.get(0).get(3));
        assertEquals("\"得意先郵便番号\"", csvData.get(0).get(4));
        assertEquals("\"得意先住所１\"", csvData.get(0).get(5));
        assertEquals("\"得意先住所２\"", csvData.get(0).get(6));
        assertEquals("\"得意先住所３\"", csvData.get(0).get(7));
        assertEquals("\"09011111111\"", csvData.get(0).get(8));
        assertEquals("002", csvData.get(0).get(9));
        assertEquals("036", csvData.get(0).get(10));
        assertEquals("000002", csvData.get(0).get(11));
        assertEquals("\"得意先名称１\"", csvData.get(0).get(12));
        assertEquals("\"得意先名称２\"", csvData.get(0).get(13));
        assertEquals("得意先敬称", csvData.get(0).get(14));
        assertEquals("\"取扱店：店名\"", csvData.get(0).get(15));
        assertEquals("\"担当者名\"", csvData.get(0).get(16));
        assertEquals("2200", csvData.get(0).get(17));
        assertEquals("", csvData.get(0).get(18));
        assertEquals("20170730", csvData.get(0).get(19));
    }

    /**
     * entity１件 請求先連絡先番号区分4：4以外
     */
    @Test
    void test12() {
        S07T001TD1501R35ListResultEntity e = new S07T001TD1501R35ListResultEntity();
        List<S07T001TD1501R35ListResultEntity> list = new ArrayList<>();
        e.setApkihoncd("002");
        e.setSkyshono("000001");
        e.setShopcd("036");
        e.setKurikoshikin("1000");
        e.setKonkaitaxnukiurikin("2000");
        e.setKonkaishohitaxkin("200");
        e.setSkysakicd("000002");
        e.setTokuisakicd("000003");
        e.setShukindydmdd("20170730");
        e.setSkykbn("1");
        // 請求方法区分
        e.setSkyhohokbn("1");
        // 明細印刷区分
        e.setMeisaiprntkbn("1");
        e.setSksyubinno("請求先郵便番号");
        e.setSksadrsn1("請求先住所１");
        e.setSksadrsn2("請求先住所２");
        e.setSksadrsn3("請求先住所３");
        e.setSksrenrakusakinokbnn1("4");
        e.setSksrenrakusakinon1("08011111111");
        e.setSksrenrakusakinokbnn2("4");
        e.setSksrenrakusakinon2("08022222222");
        e.setSksrenrakusakinokbnn3("4");
        e.setSksrenrakusakinon3("08033333333");
        e.setSksrenrakusakinokbnn4("1");
        e.setSksrenrakusakinon4("08044444444");
        e.setSksskysakinmn1("請求先名称１");
        e.setSksskysakinmn2("請求先名称２");
        e.setSksskysakikeishokbn("1");
        e.setSksskysakikeishokbnnm("請求先敬称");
        e.setTksyubinno("得意先郵便番号");
        e.setTksadrsn1("得意先住所１");
        e.setTksadrsn2("得意先住所２");
        e.setTksadrsn3("得意先住所３");
        e.setTksrenrakusakinokbnn1("1");
        e.setTksrenrakusakinon1("09011111111");
        e.setTksrenrakusakinokbnn2("2");
        e.setTksrenrakusakinon2("09022222222");
        e.setTksrenrakusakinokbnn3("4");
        e.setTksrenrakusakinon3("09033333333");
        e.setTksrenrakusakinokbnn4("4");
        e.setTksrenrakusakinon4("09044444444");
        e.setTkstokuisakinmn1("得意先名称１");
        e.setTkstokuisakinmn2("得意先名称２");
        e.setTkstokuisakikeishokbn("1");
        e.setTkstokuisakikeishokbnnm("得意先敬称");
        e.setTkstokuisakikbn("1");
        e.setTnshamei("担当者名");
        e.setShopnm("店名");
        e.setSshdshhitoimage("イメージ");
        e.setKaishuhohokbn("1");
        e.setRyosyukin("");
        list.add(e);
        ArrayList<List<String>> csvData = printDataEdit(list, "20170630", "002-036-002036.bmp");
        // csvデータの件数確認
        assertEquals(1, csvData.size());
        assertEquals(20, csvData.get(0).size());
        assertEquals("D1501R35", csvData.get(0).get(0));
        assertEquals("20170630", csvData.get(0).get(1));
        assertEquals("\"000001\"", csvData.get(0).get(2));
        assertEquals("\"002-036-002036.bmp\"", csvData.get(0).get(3));
        assertEquals("\"請求先郵便番号\"", csvData.get(0).get(4));
        assertEquals("\"請求先住所１\"", csvData.get(0).get(5));
        assertEquals("\"請求先住所２\"", csvData.get(0).get(6));
        assertEquals("\"請求先住所３\"", csvData.get(0).get(7));
        assertEquals("\"08044444444\"", csvData.get(0).get(8));
        assertEquals("002", csvData.get(0).get(9));
        assertEquals("036", csvData.get(0).get(10));
        assertEquals("000002", csvData.get(0).get(11));
        assertEquals("\"請求先名称１\"", csvData.get(0).get(12));
        assertEquals("\"請求先名称２\"", csvData.get(0).get(13));
        assertEquals("請求先敬称", csvData.get(0).get(14));
        assertEquals("", csvData.get(0).get(15));
        assertEquals("\"担当者名\"", csvData.get(0).get(16));
        assertEquals("1000", csvData.get(0).get(17));
        assertEquals("", csvData.get(0).get(18));
        assertEquals("20170730", csvData.get(0).get(19));
    }


    /**
     * entity１件 請求先連絡先番号区分3：4以外
     */
    @Test
    void test11() {
        S07T001TD1501R35ListResultEntity e = new S07T001TD1501R35ListResultEntity();
        List<S07T001TD1501R35ListResultEntity> list = new ArrayList<>();
        e.setApkihoncd("002");
        e.setSkyshono("000001");
        e.setShopcd("036");
        e.setKurikoshikin("1000");
        e.setKonkaitaxnukiurikin("2000");
        e.setKonkaishohitaxkin("200");
        e.setSkysakicd("000002");
        e.setTokuisakicd("000003");
        e.setShukindydmdd("20170730");
        e.setSkykbn("1");
        // 請求方法区分
        e.setSkyhohokbn("1");
        // 明細印刷区分
        e.setMeisaiprntkbn("1");
        e.setSksyubinno("請求先郵便番号");
        e.setSksadrsn1("請求先住所１");
        e.setSksadrsn2("請求先住所２");
        e.setSksadrsn3("請求先住所３");
        e.setSksrenrakusakinokbnn1("4");
        e.setSksrenrakusakinon1("08011111111");
        e.setSksrenrakusakinokbnn2("4");
        e.setSksrenrakusakinon2("08022222222");
        e.setSksrenrakusakinokbnn3("2");
        e.setSksrenrakusakinon3("08033333333");
        e.setSksrenrakusakinokbnn4("1");
        e.setSksrenrakusakinon4("08044444444");
        e.setSksskysakinmn1("請求先名称１");
        e.setSksskysakinmn2("請求先名称２");
        e.setSksskysakikeishokbn("1");
        e.setSksskysakikeishokbnnm("請求先敬称");
        e.setTksyubinno("得意先郵便番号");
        e.setTksadrsn1("得意先住所１");
        e.setTksadrsn2("得意先住所２");
        e.setTksadrsn3("得意先住所３");
        e.setTksrenrakusakinokbnn1("1");
        e.setTksrenrakusakinon1("09011111111");
        e.setTksrenrakusakinokbnn2("2");
        e.setTksrenrakusakinon2("09022222222");
        e.setTksrenrakusakinokbnn3("4");
        e.setTksrenrakusakinon3("09033333333");
        e.setTksrenrakusakinokbnn4("4");
        e.setTksrenrakusakinon4("09044444444");
        e.setTkstokuisakinmn1("得意先名称１");
        e.setTkstokuisakinmn2("得意先名称２");
        e.setTkstokuisakikeishokbn("1");
        e.setTkstokuisakikeishokbnnm("得意先敬称");
        e.setTkstokuisakikbn("1");
        e.setTnshamei("担当者名");
        e.setShopnm("店名");
        e.setSshdshhitoimage("イメージ");
        e.setKaishuhohokbn("1");
        e.setRyosyukin("");
        list.add(e);
        ArrayList<List<String>> csvData = printDataEdit(list, "20170630", "002-036-002036.bmp");
        // csvデータの件数確認
        assertEquals(1, csvData.size());
        assertEquals(20, csvData.get(0).size());
        assertEquals("D1501R35", csvData.get(0).get(0));
        assertEquals("20170630", csvData.get(0).get(1));
        assertEquals("\"000001\"", csvData.get(0).get(2));
        assertEquals("\"002-036-002036.bmp\"", csvData.get(0).get(3));
        assertEquals("\"請求先郵便番号\"", csvData.get(0).get(4));
        assertEquals("\"請求先住所１\"", csvData.get(0).get(5));
        assertEquals("\"請求先住所２\"", csvData.get(0).get(6));
        assertEquals("\"請求先住所３\"", csvData.get(0).get(7));
        assertEquals("\"08033333333\"", csvData.get(0).get(8));
        assertEquals("002", csvData.get(0).get(9));
        assertEquals("036", csvData.get(0).get(10));
        assertEquals("000002", csvData.get(0).get(11));
        assertEquals("\"請求先名称１\"", csvData.get(0).get(12));
        assertEquals("\"請求先名称２\"", csvData.get(0).get(13));
        assertEquals("請求先敬称", csvData.get(0).get(14));
        assertEquals("", csvData.get(0).get(15));
        assertEquals("\"担当者名\"", csvData.get(0).get(16));
        assertEquals("1000", csvData.get(0).get(17));
        assertEquals("", csvData.get(0).get(18));
        assertEquals("20170730", csvData.get(0).get(19));
    }

    /**
     * entity１件 請求先連絡先番号区分2：4以外
     */
    @Test
    void test10() {
        S07T001TD1501R35ListResultEntity e = new S07T001TD1501R35ListResultEntity();
        List<S07T001TD1501R35ListResultEntity> list = new ArrayList<>();
        e.setApkihoncd("002");
        e.setSkyshono("000001");
        e.setShopcd("036");
        e.setKurikoshikin("1000");
        e.setKonkaitaxnukiurikin("2000");
        e.setKonkaishohitaxkin("200");
        e.setSkysakicd("000002");
        e.setTokuisakicd("000003");
        e.setShukindydmdd("20170730");
        e.setSkykbn("1");
        // 請求方法区分
        e.setSkyhohokbn("1");
        // 明細印刷区分
        e.setMeisaiprntkbn("1");
        e.setSksyubinno("請求先郵便番号");
        e.setSksadrsn1("請求先住所１");
        e.setSksadrsn2("請求先住所２");
        e.setSksadrsn3("請求先住所３");
        e.setSksrenrakusakinokbnn1("4");
        e.setSksrenrakusakinon1("08011111111");
        e.setSksrenrakusakinokbnn2("3");
        e.setSksrenrakusakinon2("08022222222");
        e.setSksrenrakusakinokbnn3("2");
        e.setSksrenrakusakinon3("08033333333");
        e.setSksrenrakusakinokbnn4("1");
        e.setSksrenrakusakinon4("08044444444");
        e.setSksskysakinmn1("請求先名称１");
        e.setSksskysakinmn2("請求先名称２");
        e.setSksskysakikeishokbn("1");
        e.setSksskysakikeishokbnnm("請求先敬称");
        e.setTksyubinno("得意先郵便番号");
        e.setTksadrsn1("得意先住所１");
        e.setTksadrsn2("得意先住所２");
        e.setTksadrsn3("得意先住所３");
        e.setTksrenrakusakinokbnn1("1");
        e.setTksrenrakusakinon1("09011111111");
        e.setTksrenrakusakinokbnn2("2");
        e.setTksrenrakusakinon2("09022222222");
        e.setTksrenrakusakinokbnn3("4");
        e.setTksrenrakusakinon3("09033333333");
        e.setTksrenrakusakinokbnn4("4");
        e.setTksrenrakusakinon4("09044444444");
        e.setTkstokuisakinmn1("得意先名称１");
        e.setTkstokuisakinmn2("得意先名称２");
        e.setTkstokuisakikeishokbn("1");
        e.setTkstokuisakikeishokbnnm("得意先敬称");
        e.setTkstokuisakikbn("1");
        e.setTnshamei("担当者名");
        e.setShopnm("店名");
        e.setSshdshhitoimage("イメージ");
        e.setKaishuhohokbn("1");
        e.setRyosyukin("");
        list.add(e);
        ArrayList<List<String>> csvData = printDataEdit(list, "20170630", "002-036-002036.bmp");
        // csvデータの件数確認
        assertEquals(1, csvData.size());
        assertEquals(20, csvData.get(0).size());
        assertEquals("D1501R35", csvData.get(0).get(0));
        assertEquals("20170630", csvData.get(0).get(1));
        assertEquals("\"000001\"", csvData.get(0).get(2));
        assertEquals("\"002-036-002036.bmp\"", csvData.get(0).get(3));
        assertEquals("\"請求先郵便番号\"", csvData.get(0).get(4));
        assertEquals("\"請求先住所１\"", csvData.get(0).get(5));
        assertEquals("\"請求先住所２\"", csvData.get(0).get(6));
        assertEquals("\"請求先住所３\"", csvData.get(0).get(7));
        assertEquals("\"08022222222\"", csvData.get(0).get(8));
        assertEquals("002", csvData.get(0).get(9));
        assertEquals("036", csvData.get(0).get(10));
        assertEquals("000002", csvData.get(0).get(11));
        assertEquals("\"請求先名称１\"", csvData.get(0).get(12));
        assertEquals("\"請求先名称２\"", csvData.get(0).get(13));
        assertEquals("請求先敬称", csvData.get(0).get(14));
        assertEquals("", csvData.get(0).get(15));
        assertEquals("\"担当者名\"", csvData.get(0).get(16));
        assertEquals("1000", csvData.get(0).get(17));
        assertEquals("", csvData.get(0).get(18));
        assertEquals("20170730", csvData.get(0).get(19));
    }

    /**
     * entity１件 請求先連絡先番号区分１：4以外
     */
    @Test
    void test09() {
        S07T001TD1501R35ListResultEntity e = new S07T001TD1501R35ListResultEntity();
        List<S07T001TD1501R35ListResultEntity> list = new ArrayList<>();
        e.setApkihoncd("002");
        e.setSkyshono("000001");
        e.setShopcd("036");
        e.setKurikoshikin("1000");
        e.setKonkaitaxnukiurikin("2000");
        e.setKonkaishohitaxkin("200");
        e.setSkysakicd("000002");
        e.setTokuisakicd("000003");
        e.setShukindydmdd("20170730");
        e.setSkykbn("1");
        // 請求方法区分
        e.setSkyhohokbn("1");
        // 明細印刷区分
        e.setMeisaiprntkbn("1");
        e.setSksyubinno("請求先郵便番号");
        e.setSksadrsn1("請求先住所１");
        e.setSksadrsn2("請求先住所２");
        e.setSksadrsn3("請求先住所３");
        e.setSksrenrakusakinokbnn1("1");
        e.setSksrenrakusakinon1("08011111111");
        e.setSksrenrakusakinokbnn2("3");
        e.setSksrenrakusakinon2("08022222222");
        e.setSksrenrakusakinokbnn3("2");
        e.setSksrenrakusakinon3("08033333333");
        e.setSksrenrakusakinokbnn4("1");
        e.setSksrenrakusakinon4("08044444444");
        e.setSksskysakinmn1("請求先名称１");
        e.setSksskysakinmn2("請求先名称２");
        e.setSksskysakikeishokbn("1");
        e.setSksskysakikeishokbnnm("請求先敬称");
        e.setTksyubinno("得意先郵便番号");
        e.setTksadrsn1("得意先住所１");
        e.setTksadrsn2("得意先住所２");
        e.setTksadrsn3("得意先住所３");
        e.setTksrenrakusakinokbnn1("1");
        e.setTksrenrakusakinon1("09011111111");
        e.setTksrenrakusakinokbnn2("2");
        e.setTksrenrakusakinon2("09022222222");
        e.setTksrenrakusakinokbnn3("4");
        e.setTksrenrakusakinon3("09033333333");
        e.setTksrenrakusakinokbnn4("4");
        e.setTksrenrakusakinon4("09044444444");
        e.setTkstokuisakinmn1("得意先名称１");
        e.setTkstokuisakinmn2("得意先名称２");
        e.setTkstokuisakikeishokbn("1");
        e.setTkstokuisakikeishokbnnm("得意先敬称");
        e.setTkstokuisakikbn("1");
        e.setTnshamei("担当者名");
        e.setShopnm("店名");
        e.setSshdshhitoimage("イメージ");
        e.setKaishuhohokbn("1");
        e.setRyosyukin("");
        list.add(e);
        ArrayList<List<String>> csvData = printDataEdit(list, "20170630", "002-036-002036.bmp");
        // csvデータの件数確認
        assertEquals(1, csvData.size());
        assertEquals(20, csvData.get(0).size());
        assertEquals("D1501R35", csvData.get(0).get(0));
        assertEquals("20170630", csvData.get(0).get(1));
        assertEquals("\"000001\"", csvData.get(0).get(2));
        assertEquals("\"002-036-002036.bmp\"", csvData.get(0).get(3));
        assertEquals("\"請求先郵便番号\"", csvData.get(0).get(4));
        assertEquals("\"請求先住所１\"", csvData.get(0).get(5));
        assertEquals("\"請求先住所２\"", csvData.get(0).get(6));
        assertEquals("\"請求先住所３\"", csvData.get(0).get(7));
        assertEquals("\"08011111111\"", csvData.get(0).get(8));
        assertEquals("002", csvData.get(0).get(9));
        assertEquals("036", csvData.get(0).get(10));
        assertEquals("000002", csvData.get(0).get(11));
        assertEquals("\"請求先名称１\"", csvData.get(0).get(12));
        assertEquals("\"請求先名称２\"", csvData.get(0).get(13));
        assertEquals("請求先敬称", csvData.get(0).get(14));
        assertEquals("", csvData.get(0).get(15));
        assertEquals("\"担当者名\"", csvData.get(0).get(16));
        assertEquals("1000", csvData.get(0).get(17));
        assertEquals("", csvData.get(0).get(18));
        assertEquals("20170730", csvData.get(0).get(19));
    }

    /**
     * entity2件 印紙 領収金額 49999 50000
     */
    @Test
    void test08() {
        S07T001TD1501R35ListResultEntity e = new S07T001TD1501R35ListResultEntity();
        List<S07T001TD1501R35ListResultEntity> list = new ArrayList<>();
        e.setApkihoncd("002");
        e.setSkyshono("000001");
        e.setShopcd("036");
        e.setKurikoshikin("49999");
        e.setKonkaitaxnukiurikin("2000");
        e.setKonkaishohitaxkin("200");
        e.setSkysakicd("000002");
        e.setTokuisakicd("000003");
        e.setShukindydmdd("20170730");
        e.setSkykbn("1");
        // 請求方法区分
        e.setSkyhohokbn("1");
        // 明細印刷区分
        e.setMeisaiprntkbn("1");
        e.setSksyubinno("請求先郵便番号");
        e.setSksadrsn1("請求先住所１");
        e.setSksadrsn2("請求先住所２");
        e.setSksadrsn3("請求先住所３");
        e.setSksrenrakusakinokbnn1("4");
        e.setSksrenrakusakinon1("08011111111");
        e.setSksrenrakusakinokbnn2("3");
        e.setSksrenrakusakinon2("08022222222");
        e.setSksrenrakusakinokbnn3("2");
        e.setSksrenrakusakinon3("08033333333");
        e.setSksrenrakusakinokbnn4("1");
        e.setSksrenrakusakinon4("08044444444");
        e.setSksskysakinmn1("請求先名称１");
        e.setSksskysakinmn2("請求先名称２");
        e.setSksskysakikeishokbn("1");
        e.setSksskysakikeishokbnnm("請求先敬称");
        e.setTksyubinno("得意先郵便番号");
        e.setTksadrsn1("得意先住所１");
        e.setTksadrsn2("得意先住所２");
        e.setTksadrsn3("得意先住所３");
        e.setTksrenrakusakinokbnn1("1");
        e.setTksrenrakusakinon1("09011111111");
        e.setTksrenrakusakinokbnn2("2");
        e.setTksrenrakusakinon2("09022222222");
        e.setTksrenrakusakinokbnn3("4");
        e.setTksrenrakusakinon3("09033333333");
        e.setTksrenrakusakinokbnn4("4");
        e.setTksrenrakusakinon4("09044444444");
        e.setTkstokuisakinmn1("得意先名称１");
        e.setTkstokuisakinmn2("得意先名称２");
        e.setTkstokuisakikeishokbn("1");
        e.setTkstokuisakikeishokbnnm("得意先敬称");
        e.setTkstokuisakikbn("1");
        e.setTnshamei("担当者名");
        e.setShopnm("店名");
        e.setSshdshhitoimage("イメージ");
        e.setKaishuhohokbn("1");
        e.setRyosyukin("");
        list.add(e);
        e = new S07T001TD1501R35ListResultEntity();
        e.setApkihoncd("002");
        e.setSkyshono("000002");
        e.setShopcd("036");
        e.setKurikoshikin("50000");
        e.setKonkaitaxnukiurikin("2000");
        e.setKonkaishohitaxkin("200");
        e.setSkysakicd("000002");
        e.setTokuisakicd("000003");
        e.setShukindydmdd("20170730");
        e.setSkykbn("1");
        // 請求方法区分
        e.setSkyhohokbn("1");
        // 明細印刷区分
        e.setMeisaiprntkbn("1");
        e.setSksyubinno("請求先郵便番号");
        e.setSksadrsn1("請求先住所１");
        e.setSksadrsn2("請求先住所２");
        e.setSksadrsn3("請求先住所３");
        e.setSksrenrakusakinokbnn1("4");
        e.setSksrenrakusakinon1("08011111111");
        e.setSksrenrakusakinokbnn2("3");
        e.setSksrenrakusakinon2("08022222222");
        e.setSksrenrakusakinokbnn3("2");
        e.setSksrenrakusakinon3("08033333333");
        e.setSksrenrakusakinokbnn4("1");
        e.setSksrenrakusakinon4("08044444444");
        e.setSksskysakinmn1("請求先名称１");
        e.setSksskysakinmn2("請求先名称２");
        e.setSksskysakikeishokbn("1");
        e.setSksskysakikeishokbnnm("請求先敬称");
        e.setTksyubinno("得意先郵便番号");
        e.setTksadrsn1("得意先住所１");
        e.setTksadrsn2("得意先住所２");
        e.setTksadrsn3("得意先住所３");
        e.setTksrenrakusakinokbnn1("1");
        e.setTksrenrakusakinon1("09011111111");
        e.setTksrenrakusakinokbnn2("2");
        e.setTksrenrakusakinon2("09022222222");
        e.setTksrenrakusakinokbnn3("4");
        e.setTksrenrakusakinon3("09033333333");
        e.setTksrenrakusakinokbnn4("4");
        e.setTksrenrakusakinon4("09044444444");
        e.setTkstokuisakinmn1("得意先名称１");
        e.setTkstokuisakinmn2("得意先名称２");
        e.setTkstokuisakikeishokbn("1");
        e.setTkstokuisakikeishokbnnm("得意先敬称");
        e.setTkstokuisakikbn("1");
        e.setTnshamei("担当者名");
        e.setShopnm("店名");
        e.setSshdshhitoimage("イメージ");
        e.setKaishuhohokbn("1");
        e.setRyosyukin("");
        list.add(e);
        ArrayList<List<String>> csvData = printDataEdit(list, "20170630", "002-036-002036.bmp");
        // csvデータの件数確認
        assertEquals(2, csvData.size());
        assertEquals(20, csvData.get(0).size());
        assertEquals("D1501R35", csvData.get(0).get(0));
        assertEquals("20170630", csvData.get(0).get(1));
        assertEquals("\"000001\"", csvData.get(0).get(2));
        assertEquals("\"002-036-002036.bmp\"", csvData.get(0).get(3));
        assertEquals("\"請求先郵便番号\"", csvData.get(0).get(4));
        assertEquals("\"請求先住所１\"", csvData.get(0).get(5));
        assertEquals("\"請求先住所２\"", csvData.get(0).get(6));
        assertEquals("\"請求先住所３\"", csvData.get(0).get(7));
        assertEquals("\"08022222222\"", csvData.get(0).get(8));
        assertEquals("002", csvData.get(0).get(9));
        assertEquals("036", csvData.get(0).get(10));
        assertEquals("000002", csvData.get(0).get(11));
        assertEquals("\"請求先名称１\"", csvData.get(0).get(12));
        assertEquals("\"請求先名称２\"", csvData.get(0).get(13));
        assertEquals("請求先敬称", csvData.get(0).get(14));
        assertEquals("", csvData.get(0).get(15));
        assertEquals("\"担当者名\"", csvData.get(0).get(16));
        assertEquals("49999", csvData.get(0).get(17));
        assertEquals("", csvData.get(0).get(18));
        assertEquals("20170730", csvData.get(0).get(19));
        assertEquals(20, csvData.get(1).size());
        assertEquals("D1501R35", csvData.get(1).get(0));
        assertEquals("20170630", csvData.get(1).get(1));
        assertEquals("\"000002\"", csvData.get(1).get(2));
        assertEquals("\"002-036-002036.bmp\"", csvData.get(1).get(3));
        assertEquals("\"請求先郵便番号\"", csvData.get(1).get(4));
        assertEquals("\"請求先住所１\"", csvData.get(1).get(5));
        assertEquals("\"請求先住所２\"", csvData.get(1).get(6));
        assertEquals("\"請求先住所３\"", csvData.get(1).get(7));
        assertEquals("\"08022222222\"", csvData.get(1).get(8));
        assertEquals("002", csvData.get(1).get(9));
        assertEquals("036", csvData.get(1).get(10));
        assertEquals("000002", csvData.get(1).get(11));
        assertEquals("\"請求先名称１\"", csvData.get(1).get(12));
        assertEquals("\"請求先名称２\"", csvData.get(1).get(13));
        assertEquals("請求先敬称", csvData.get(1).get(14));
        assertEquals("", csvData.get(1).get(15));
        assertEquals("\"担当者名\"", csvData.get(1).get(16));
        assertEquals("50000", csvData.get(1).get(17));
        assertEquals("\"印紙\"", csvData.get(1).get(18));
        assertEquals("20170730", csvData.get(1).get(19));
    }

    /**
     * entity3件 請求方法区分：１（残高請求）以外 明細印刷区分:2（親子別々） 得意先コード!=得意先コード 請求先コード!=得意先コード 得意先区分:1(顧客)
     */
    @Test
    void test07() {
        S07T001TD1501R35ListResultEntity e = new S07T001TD1501R35ListResultEntity();
        List<S07T001TD1501R35ListResultEntity> list = new ArrayList<>();
        e.setApkihoncd("002");
        e.setSkyshono("000001");
        e.setShopcd("036");
        e.setKurikoshikin("1000");
        e.setKonkaitaxnukiurikin("2000");
        e.setKonkaishohitaxkin("200");
        e.setSkysakicd("000002");
        e.setTokuisakicd("000002");
        e.setShukindydmdd("20170730");
        e.setSkykbn("1");
        // 請求方法区分
        e.setSkyhohokbn("2");
        // 明細印刷区分
        e.setMeisaiprntkbn("2");
        e.setSksyubinno("請求先郵便番号");
        e.setSksadrsn1("請求先住所１");
        e.setSksadrsn2("請求先住所２");
        e.setSksadrsn3("請求先住所３");
        e.setSksrenrakusakinokbnn1("4");
        e.setSksrenrakusakinon1("08011111111");
        e.setSksrenrakusakinokbnn2("3");
        e.setSksrenrakusakinon2("08022222222");
        e.setSksrenrakusakinokbnn3("2");
        e.setSksrenrakusakinon3("08033333333");
        e.setSksrenrakusakinokbnn4("1");
        e.setSksrenrakusakinon4("08044444444");
        e.setSksskysakinmn1("請求先名称１");
        e.setSksskysakinmn2("請求先名称２");
        e.setSksskysakikeishokbn("1");
        e.setSksskysakikeishokbnnm("請求先敬称");
        e.setTksyubinno("得意先郵便番号");
        e.setTksadrsn1("得意先住所１");
        e.setTksadrsn2("得意先住所２");
        e.setTksadrsn3("得意先住所３");
        e.setTksrenrakusakinokbnn1("1");
        e.setTksrenrakusakinon1("09011111111");
        e.setTksrenrakusakinokbnn2("2");
        e.setTksrenrakusakinon2("09022222222");
        e.setTksrenrakusakinokbnn3("4");
        e.setTksrenrakusakinon3("09033333333");
        e.setTksrenrakusakinokbnn4("4");
        e.setTksrenrakusakinon4("09044444444");
        e.setTkstokuisakinmn1("得意先名称１");
        e.setTkstokuisakinmn2("得意先名称２");
        e.setTkstokuisakikeishokbn("1");
        e.setTkstokuisakikeishokbnnm("得意先敬称");
        // 得意先区分
        e.setTkstokuisakikbn("1");
        e.setTnshamei("担当者名");
        e.setShopnm("店名");
        e.setSshdshhitoimage("イメージ");
        e.setKaishuhohokbn("1");
        e.setRyosyukin("");
        list.add(e);
        list.add(e);
        e = new S07T001TD1501R35ListResultEntity();
        e.setApkihoncd("002");
        e.setSkyshono("000001");
        e.setShopcd("036");
        e.setKurikoshikin("1000");
        e.setKonkaitaxnukiurikin("2000");
        e.setKonkaishohitaxkin("200");
        e.setSkysakicd("000002");
        e.setTokuisakicd("000003");
        e.setShukindydmdd("20170730");
        e.setSkykbn("1");
        // 請求方法区分
        e.setSkyhohokbn("2");
        // 明細印刷区分
        e.setMeisaiprntkbn("2");
        e.setSksyubinno("請求先郵便番号");
        e.setSksadrsn1("請求先住所１");
        e.setSksadrsn2("請求先住所２");
        e.setSksadrsn3("請求先住所３");
        e.setSksrenrakusakinokbnn1("4");
        e.setSksrenrakusakinon1("08011111111");
        e.setSksrenrakusakinokbnn2("3");
        e.setSksrenrakusakinon2("08022222222");
        e.setSksrenrakusakinokbnn3("2");
        e.setSksrenrakusakinon3("08033333333");
        e.setSksrenrakusakinokbnn4("1");
        e.setSksrenrakusakinon4("08044444444");
        e.setSksskysakinmn1("請求先名称１");
        e.setSksskysakinmn2("請求先名称２");
        e.setSksskysakikeishokbn("1");
        e.setSksskysakikeishokbnnm("請求先敬称");
        e.setTksyubinno("2得意先郵便番号");
        e.setTksadrsn1("2得意先住所１");
        e.setTksadrsn2("2得意先住所２");
        e.setTksadrsn3("2得意先住所３");
        e.setTksrenrakusakinokbnn1("1");
        e.setTksrenrakusakinon1("09011111111");
        e.setTksrenrakusakinokbnn2("2");
        e.setTksrenrakusakinon2("09022222222");
        e.setTksrenrakusakinokbnn3("4");
        e.setTksrenrakusakinon3("09033333333");
        e.setTksrenrakusakinokbnn4("4");
        e.setTksrenrakusakinon4("09044444444");
        e.setTkstokuisakinmn1("2得意先名称１");
        e.setTkstokuisakinmn2("2得意先名称２");
        e.setTkstokuisakikeishokbn("1");
        e.setTkstokuisakikeishokbnnm("2得意先敬称");
        // 得意先区分
        e.setTkstokuisakikbn("1");
        e.setTnshamei("2担当者名");
        e.setShopnm("2店名");
        e.setSshdshhitoimage("イメージ");
        e.setKaishuhohokbn("1");
        e.setRyosyukin("");
        list.add(e);
        ArrayList<List<String>> csvData = printDataEdit(list, "20170630", "002-036-002036.bmp");
        // csvデータの件数確認
        assertEquals(2, csvData.size());
        // １件目
        assertEquals(20, csvData.get(0).size());
        assertEquals("D1501R35", csvData.get(0).get(0));
        assertEquals("20170630", csvData.get(0).get(1));
        assertEquals("\"000001\"", csvData.get(0).get(2));
        assertEquals("\"002-036-002036.bmp\"", csvData.get(0).get(3));
        assertEquals("\"得意先郵便番号\"", csvData.get(0).get(4));
        assertEquals("\"得意先住所１\"", csvData.get(0).get(5));
        assertEquals("\"得意先住所２\"", csvData.get(0).get(6));
        assertEquals("\"得意先住所３\"", csvData.get(0).get(7));
        assertEquals("\"09011111111\"", csvData.get(0).get(8));
        assertEquals("002", csvData.get(0).get(9));
        assertEquals("036", csvData.get(0).get(10));
        assertEquals("000002", csvData.get(0).get(11));
        assertEquals("\"得意先名称１\"", csvData.get(0).get(12));
        assertEquals("\"得意先名称２\"", csvData.get(0).get(13));
        assertEquals("得意先敬称", csvData.get(0).get(14));
        assertEquals("\"取扱店：店名\"", csvData.get(0).get(15));
        assertEquals("\"担当者名\"", csvData.get(0).get(16));
        assertEquals("4400", csvData.get(0).get(17));
        assertEquals("", csvData.get(0).get(18));
        assertEquals("20170730", csvData.get(0).get(19));
        // 2件目
        assertEquals(20, csvData.get(1).size());
        assertEquals("D1501R35", csvData.get(1).get(0));
        assertEquals("20170630", csvData.get(1).get(1));
        assertEquals("\"000001\"", csvData.get(1).get(2));
        assertEquals("\"002-036-002036.bmp\"", csvData.get(1).get(3));
        assertEquals("\"請求先郵便番号\"", csvData.get(1).get(4));
        assertEquals("\"請求先住所１\"", csvData.get(1).get(5));
        assertEquals("\"請求先住所２\"", csvData.get(1).get(6));
        assertEquals("\"請求先住所３\"", csvData.get(1).get(7));
        assertEquals("\"08022222222\"", csvData.get(1).get(8));
        assertEquals("002", csvData.get(1).get(9));
        assertEquals("036", csvData.get(1).get(10));
        assertEquals("000002", csvData.get(1).get(11));
        assertEquals("\"2得意先名称１\"", csvData.get(1).get(12));
        assertEquals("\"2得意先名称２\"", csvData.get(1).get(13));
        assertEquals("2得意先敬称", csvData.get(1).get(14));
        assertEquals("\"取扱店：2店名\"", csvData.get(1).get(15));
        assertEquals("\"2担当者名\"", csvData.get(1).get(16));
        assertEquals("2200", csvData.get(1).get(17));
        assertEquals("", csvData.get(1).get(18));
        assertEquals("20170730", csvData.get(1).get(19));
    }


    /**
     * entity2件 請求方法区分：１（残高請求）以外 明細印刷区分:2（親子別々） 請求先コード＝得意先コード 得意先区分:1(顧客)以外
     */
    @Test
    void test06() {
        S07T001TD1501R35ListResultEntity e = new S07T001TD1501R35ListResultEntity();
        List<S07T001TD1501R35ListResultEntity> list = new ArrayList<>();
        e.setApkihoncd("002");
        e.setSkyshono("000001");
        e.setShopcd("036");
        e.setKurikoshikin("1000");
        e.setKonkaitaxnukiurikin("2000");
        e.setKonkaishohitaxkin("200");
        e.setSkysakicd("000002");
        e.setTokuisakicd("000002");
        e.setShukindydmdd("20170730");
        e.setSkykbn("1");
        // 請求方法区分
        e.setSkyhohokbn("2");
        // 明細印刷区分
        e.setMeisaiprntkbn("2");
        e.setSksyubinno("請求先郵便番号");
        e.setSksadrsn1("請求先住所１");
        e.setSksadrsn2("請求先住所２");
        e.setSksadrsn3("請求先住所３");
        e.setSksrenrakusakinokbnn1("4");
        e.setSksrenrakusakinon1("08011111111");
        e.setSksrenrakusakinokbnn2("3");
        e.setSksrenrakusakinon2("08022222222");
        e.setSksrenrakusakinokbnn3("2");
        e.setSksrenrakusakinon3("08033333333");
        e.setSksrenrakusakinokbnn4("1");
        e.setSksrenrakusakinon4("08044444444");
        e.setSksskysakinmn1("請求先名称１");
        e.setSksskysakinmn2("請求先名称２");
        e.setSksskysakikeishokbn("1");
        e.setSksskysakikeishokbnnm("請求先敬称");
        e.setTksyubinno("得意先郵便番号");
        e.setTksadrsn1("得意先住所１");
        e.setTksadrsn2("得意先住所２");
        e.setTksadrsn3("得意先住所３");
        e.setTksrenrakusakinokbnn1("1");
        e.setTksrenrakusakinon1("09011111111");
        e.setTksrenrakusakinokbnn2("2");
        e.setTksrenrakusakinon2("09022222222");
        e.setTksrenrakusakinokbnn3("4");
        e.setTksrenrakusakinon3("09033333333");
        e.setTksrenrakusakinokbnn4("4");
        e.setTksrenrakusakinon4("09044444444");
        e.setTkstokuisakinmn1("得意先名称１");
        e.setTkstokuisakinmn2("得意先名称２");
        e.setTkstokuisakikeishokbn("1");
        e.setTkstokuisakikeishokbnnm("得意先敬称");
        // 得意先区分
        e.setTkstokuisakikbn("2");
        e.setTnshamei("担当者名");
        e.setShopnm("店名");
        e.setSshdshhitoimage("イメージ");
        e.setKaishuhohokbn("1");
        e.setRyosyukin("");
        list.add(e);
        list.add(e);
        ArrayList<List<String>> csvData = printDataEdit(list, "20170630", "002-036-002036.bmp");
        // csvデータの件数確認
        assertEquals(1, csvData.size());
        assertEquals(20, csvData.get(0).size());
        assertEquals("D1501R35", csvData.get(0).get(0));
        assertEquals("20170630", csvData.get(0).get(1));
        assertEquals("\"000001\"", csvData.get(0).get(2));
        assertEquals("\"002-036-002036.bmp\"", csvData.get(0).get(3));
        assertEquals("\"得意先郵便番号\"", csvData.get(0).get(4));
        assertEquals("\"得意先住所１\"", csvData.get(0).get(5));
        assertEquals("\"得意先住所２\"", csvData.get(0).get(6));
        assertEquals("\"得意先住所３\"", csvData.get(0).get(7));
        assertEquals("\"09011111111\"", csvData.get(0).get(8));
        assertEquals("002", csvData.get(0).get(9));
        assertEquals("036", csvData.get(0).get(10));
        assertEquals("000002", csvData.get(0).get(11));
        assertEquals("\"得意先名称１\"", csvData.get(0).get(12));
        assertEquals("\"得意先名称２\"", csvData.get(0).get(13));
        assertEquals("得意先敬称", csvData.get(0).get(14));
        assertEquals("", csvData.get(0).get(15));
        assertEquals("\"担当者名\"", csvData.get(0).get(16));
        assertEquals("4400", csvData.get(0).get(17));
        assertEquals("", csvData.get(0).get(18));
        assertEquals("20170730", csvData.get(0).get(19));
    }

    /**
     * entity１件 請求方法区分：１（残高請求）以外 明細印刷区分:2（親子別々） 請求先コード＝得意先コード 得意先区分:1(顧客)
     */
    @Test
    void test05() {
        S07T001TD1501R35ListResultEntity e = new S07T001TD1501R35ListResultEntity();
        List<S07T001TD1501R35ListResultEntity> list = new ArrayList<>();
        e.setApkihoncd("002");
        e.setSkyshono("000001");
        e.setShopcd("036");
        e.setKurikoshikin("1000");
        e.setKonkaitaxnukiurikin("2000");
        e.setKonkaishohitaxkin("200");
        e.setSkysakicd("000002");
        e.setTokuisakicd("000002");
        e.setShukindydmdd("20170730");
        e.setSkykbn("1");
        // 請求方法区分
        e.setSkyhohokbn("2");
        // 明細印刷区分
        e.setMeisaiprntkbn("2");
        e.setSksyubinno("請求先郵便番号");
        e.setSksadrsn1("請求先住所１");
        e.setSksadrsn2("請求先住所２");
        e.setSksadrsn3("請求先住所３");
        e.setSksrenrakusakinokbnn1("4");
        e.setSksrenrakusakinon1("08011111111");
        e.setSksrenrakusakinokbnn2("3");
        e.setSksrenrakusakinon2("08022222222");
        e.setSksrenrakusakinokbnn3("2");
        e.setSksrenrakusakinon3("08033333333");
        e.setSksrenrakusakinokbnn4("1");
        e.setSksrenrakusakinon4("08044444444");
        e.setSksskysakinmn1("請求先名称１");
        e.setSksskysakinmn2("請求先名称２");
        e.setSksskysakikeishokbn("1");
        e.setSksskysakikeishokbnnm("請求先敬称");
        e.setTksyubinno("得意先郵便番号");
        e.setTksadrsn1("得意先住所１");
        e.setTksadrsn2("得意先住所２");
        e.setTksadrsn3("得意先住所３");
        e.setTksrenrakusakinokbnn1("1");
        e.setTksrenrakusakinon1("09011111111");
        e.setTksrenrakusakinokbnn2("2");
        e.setTksrenrakusakinon2("09022222222");
        e.setTksrenrakusakinokbnn3("4");
        e.setTksrenrakusakinon3("09033333333");
        e.setTksrenrakusakinokbnn4("4");
        e.setTksrenrakusakinon4("09044444444");
        e.setTkstokuisakinmn1("得意先名称１");
        e.setTkstokuisakinmn2("得意先名称２");
        e.setTkstokuisakikeishokbn("1");
        e.setTkstokuisakikeishokbnnm("得意先敬称");
        e.setTkstokuisakikbn("1");
        e.setTnshamei("担当者名");
        e.setShopnm("店名");
        e.setSshdshhitoimage("イメージ");
        e.setKaishuhohokbn("1");
        e.setRyosyukin("");
        list.add(e);
        ArrayList<List<String>> csvData = printDataEdit(list, "20170630", "002-036-002036.bmp");
        // csvデータの件数確認
        assertEquals(1, csvData.size());
        assertEquals(20, csvData.get(0).size());
        assertEquals("D1501R35", csvData.get(0).get(0));
        assertEquals("20170630", csvData.get(0).get(1));
        assertEquals("\"000001\"", csvData.get(0).get(2));
        assertEquals("\"002-036-002036.bmp\"", csvData.get(0).get(3));
        assertEquals("\"得意先郵便番号\"", csvData.get(0).get(4));
        assertEquals("\"得意先住所１\"", csvData.get(0).get(5));
        assertEquals("\"得意先住所２\"", csvData.get(0).get(6));
        assertEquals("\"得意先住所３\"", csvData.get(0).get(7));
        assertEquals("\"09011111111\"", csvData.get(0).get(8));
        assertEquals("002", csvData.get(0).get(9));
        assertEquals("036", csvData.get(0).get(10));
        assertEquals("000002", csvData.get(0).get(11));
        assertEquals("\"得意先名称１\"", csvData.get(0).get(12));
        assertEquals("\"得意先名称２\"", csvData.get(0).get(13));
        assertEquals("得意先敬称", csvData.get(0).get(14));
        assertEquals("\"取扱店：店名\"", csvData.get(0).get(15));
        assertEquals("\"担当者名\"", csvData.get(0).get(16));
        assertEquals("2200", csvData.get(0).get(17));
        assertEquals("", csvData.get(0).get(18));
        assertEquals("20170730", csvData.get(0).get(19));
    }

    /**
     * entity3件 請求方法区分：１（残高請求） 明細区分:1（親子一緒） 請求書コードが異なる
     */
    @Test
    void test04() {
        S07T001TD1501R35ListResultEntity e = new S07T001TD1501R35ListResultEntity();
        List<S07T001TD1501R35ListResultEntity> list = new ArrayList<>();
        e.setApkihoncd("002");
        e.setSkyshono("000001");
        e.setShopcd("036");
        e.setKurikoshikin("1000");
        e.setKonkaitaxnukiurikin("2000");
        e.setKonkaishohitaxkin("200");
        e.setSkysakicd("000002");
        e.setTokuisakicd("000003");
        e.setShukindydmdd("20170730");
        e.setSkykbn("1");
        // 請求方法区分
        e.setSkyhohokbn("1");
        // 明細印刷区分
        e.setMeisaiprntkbn("1");
        e.setSksyubinno("請求先郵便番号");
        e.setSksadrsn1("請求先住所１");
        e.setSksadrsn2("請求先住所２");
        e.setSksadrsn3("請求先住所３");
        e.setSksrenrakusakinokbnn1("4");
        e.setSksrenrakusakinon1("08011111111");
        e.setSksrenrakusakinokbnn2("3");
        e.setSksrenrakusakinon2("08022222222");
        e.setSksrenrakusakinokbnn3("2");
        e.setSksrenrakusakinon3("08033333333");
        e.setSksrenrakusakinokbnn4("1");
        e.setSksrenrakusakinon4("08044444444");
        e.setSksskysakinmn1("請求先名称１");
        e.setSksskysakinmn2("請求先名称２");
        e.setSksskysakikeishokbn("1");
        e.setSksskysakikeishokbnnm("請求先敬称");
        e.setTksyubinno("得意先郵便番号");
        e.setTksadrsn1("得意先住所１");
        e.setTksadrsn2("得意先住所２");
        e.setTksadrsn3("得意先住所３");
        e.setTksrenrakusakinokbnn1("1");
        e.setTksrenrakusakinon1("09011111111");
        e.setTksrenrakusakinokbnn2("2");
        e.setTksrenrakusakinon2("09022222222");
        e.setTksrenrakusakinokbnn3("4");
        e.setTksrenrakusakinon3("09033333333");
        e.setTksrenrakusakinokbnn4("4");
        e.setTksrenrakusakinon4("09044444444");
        e.setTkstokuisakinmn1("得意先名称１");
        e.setTkstokuisakinmn2("得意先名称２");
        e.setTkstokuisakikeishokbn("1");
        e.setTkstokuisakikeishokbnnm("得意先敬称");
        e.setTkstokuisakikbn("1");
        e.setTnshamei("担当者名");
        e.setShopnm("店名");
        e.setSshdshhitoimage("イメージ");
        e.setKaishuhohokbn("1");
        e.setRyosyukin("");
        list.add(e);
        list.add(e);
        e = new S07T001TD1501R35ListResultEntity();
        e.setApkihoncd("002");
        e.setSkyshono("000001");
        e.setShopcd("036");
        e.setKurikoshikin("1000");
        e.setKonkaitaxnukiurikin("2000");
        e.setKonkaishohitaxkin("200");
        e.setSkysakicd("000003");
        e.setTokuisakicd("000003");
        e.setShukindydmdd("20170730");
        e.setSkykbn("1");
        // 請求方法区分
        e.setSkyhohokbn("1");
        // 明細印刷区分
        e.setMeisaiprntkbn("1");
        e.setSksyubinno("2請求先郵便番号");
        e.setSksadrsn1("2請求先住所１");
        e.setSksadrsn2("2請求先住所２");
        e.setSksadrsn3("2請求先住所３");
        e.setSksrenrakusakinokbnn1("4");
        e.setSksrenrakusakinon1("08011111111");
        e.setSksrenrakusakinokbnn2("3");
        e.setSksrenrakusakinon2("08022222222");
        e.setSksrenrakusakinokbnn3("2");
        e.setSksrenrakusakinon3("08033333333");
        e.setSksrenrakusakinokbnn4("1");
        e.setSksrenrakusakinon4("08044444444");
        e.setSksskysakinmn1("2請求先名称１");
        e.setSksskysakinmn2("2請求先名称２");
        e.setSksskysakikeishokbn("1");
        e.setSksskysakikeishokbnnm("2請求先敬称");
        e.setTksyubinno("得意先郵便番号");
        e.setTksadrsn1("得意先住所１");
        e.setTksadrsn2("得意先住所２");
        e.setTksadrsn3("得意先住所３");
        e.setTksrenrakusakinokbnn1("1");
        e.setTksrenrakusakinon1("09011111111");
        e.setTksrenrakusakinokbnn2("2");
        e.setTksrenrakusakinon2("09022222222");
        e.setTksrenrakusakinokbnn3("4");
        e.setTksrenrakusakinon3("09033333333");
        e.setTksrenrakusakinokbnn4("4");
        e.setTksrenrakusakinon4("09044444444");
        e.setTkstokuisakinmn1("得意先名称１");
        e.setTkstokuisakinmn2("得意先名称２");
        e.setTkstokuisakikeishokbn("1");
        e.setTkstokuisakikeishokbnnm("得意先敬称");
        e.setTkstokuisakikbn("1");
        e.setTnshamei("2担当者名");
        e.setShopnm("店名");
        e.setSshdshhitoimage("イメージ");
        e.setKaishuhohokbn("1");
        e.setRyosyukin("");
        list.add(e);
        ArrayList<List<String>> csvData = printDataEdit(list, "20170630", "002-036-002036.bmp");
        // csvデータの件数確認
        assertEquals(2, csvData.size());
        // １件目
        assertEquals(20, csvData.get(0).size());
        assertEquals("D1501R35", csvData.get(0).get(0));
        assertEquals("20170630", csvData.get(0).get(1));
        assertEquals("\"000001\"", csvData.get(0).get(2));
        assertEquals("\"002-036-002036.bmp\"", csvData.get(0).get(3));
        assertEquals("\"請求先郵便番号\"", csvData.get(0).get(4));
        assertEquals("\"請求先住所１\"", csvData.get(0).get(5));
        assertEquals("\"請求先住所２\"", csvData.get(0).get(6));
        assertEquals("\"請求先住所３\"", csvData.get(0).get(7));
        assertEquals("\"08022222222\"", csvData.get(0).get(8));
        assertEquals("002", csvData.get(0).get(9));
        assertEquals("036", csvData.get(0).get(10));
        assertEquals("000002", csvData.get(0).get(11));
        assertEquals("\"請求先名称１\"", csvData.get(0).get(12));
        assertEquals("\"請求先名称２\"", csvData.get(0).get(13));
        assertEquals("請求先敬称", csvData.get(0).get(14));
        assertEquals("", csvData.get(0).get(15));
        assertEquals("\"担当者名\"", csvData.get(0).get(16));
        assertEquals("2000", csvData.get(0).get(17));
        assertEquals("", csvData.get(0).get(18));
        assertEquals("20170730", csvData.get(0).get(19));
        assertEquals(20, csvData.get(1).size());
        // 2件目
        assertEquals("D1501R35", csvData.get(1).get(0));
        assertEquals("20170630", csvData.get(1).get(1));
        assertEquals("\"000001\"", csvData.get(1).get(2));
        assertEquals("\"002-036-002036.bmp\"", csvData.get(1).get(3));
        assertEquals("\"2請求先郵便番号\"", csvData.get(1).get(4));
        assertEquals("\"2請求先住所１\"", csvData.get(1).get(5));
        assertEquals("\"2請求先住所２\"", csvData.get(1).get(6));
        assertEquals("\"2請求先住所３\"", csvData.get(1).get(7));
        assertEquals("\"08022222222\"", csvData.get(1).get(8));
        assertEquals("002", csvData.get(1).get(9));
        assertEquals("036", csvData.get(1).get(10));
        assertEquals("000003", csvData.get(1).get(11));
        assertEquals("\"2請求先名称１\"", csvData.get(1).get(12));
        assertEquals("\"2請求先名称２\"", csvData.get(1).get(13));
        assertEquals("2請求先敬称", csvData.get(1).get(14));
        assertEquals("", csvData.get(1).get(15));
        assertEquals("\"2担当者名\"", csvData.get(1).get(16));
        assertEquals("1000", csvData.get(1).get(17));
        assertEquals("", csvData.get(1).get(18));
        assertEquals("20170730", csvData.get(1).get(19));
    }

    /**
     * entity3件 請求方法区分：１（残高請求） 明細印刷区分:1（親子一緒） 請求書番号が異なる
     */
    @Test
    void test03() {
        S07T001TD1501R35ListResultEntity e = new S07T001TD1501R35ListResultEntity();
        List<S07T001TD1501R35ListResultEntity> list = new ArrayList<>();
        e.setApkihoncd("002");
        e.setSkyshono("000001");
        e.setShopcd("036");
        e.setKurikoshikin("1000");
        e.setKonkaitaxnukiurikin("2000");
        e.setKonkaishohitaxkin("200");
        e.setSkysakicd("000002");
        e.setTokuisakicd("000003");
        e.setShukindydmdd("20170730");
        e.setSkykbn("1");
        // 請求方法区分
        e.setSkyhohokbn("1");
        // 明細印刷区分
        e.setMeisaiprntkbn("1");
        e.setSksyubinno("請求先郵便番号");
        e.setSksadrsn1("請求先住所１");
        e.setSksadrsn2("請求先住所２");
        e.setSksadrsn3("請求先住所３");
        e.setSksrenrakusakinokbnn1("4");
        e.setSksrenrakusakinon1("08011111111");
        e.setSksrenrakusakinokbnn2("3");
        e.setSksrenrakusakinon2("08022222222");
        e.setSksrenrakusakinokbnn3("2");
        e.setSksrenrakusakinon3("08033333333");
        e.setSksrenrakusakinokbnn4("1");
        e.setSksrenrakusakinon4("08044444444");
        e.setSksskysakinmn1("請求先名称１");
        e.setSksskysakinmn2("請求先名称２");
        e.setSksskysakikeishokbn("1");
        e.setSksskysakikeishokbnnm("請求先敬称");
        e.setTksyubinno("得意先郵便番号");
        e.setTksadrsn1("得意先住所１");
        e.setTksadrsn2("得意先住所２");
        e.setTksadrsn3("得意先住所３");
        e.setTksrenrakusakinokbnn1("1");
        e.setTksrenrakusakinon1("09011111111");
        e.setTksrenrakusakinokbnn2("2");
        e.setTksrenrakusakinon2("09022222222");
        e.setTksrenrakusakinokbnn3("4");
        e.setTksrenrakusakinon3("09033333333");
        e.setTksrenrakusakinokbnn4("4");
        e.setTksrenrakusakinon4("09044444444");
        e.setTkstokuisakinmn1("得意先名称１");
        e.setTkstokuisakinmn2("得意先名称２");
        e.setTkstokuisakikeishokbn("1");
        e.setTkstokuisakikeishokbnnm("得意先敬称");
        e.setTkstokuisakikbn("1");
        e.setTnshamei("担当者名");
        e.setShopnm("店名");
        e.setSshdshhitoimage("イメージ");
        e.setKaishuhohokbn("1");
        e.setRyosyukin("");
        list.add(e);
        list.add(e);
        e = new S07T001TD1501R35ListResultEntity();
        e.setApkihoncd("002");
        e.setSkyshono("000002");
        e.setShopcd("036");
        e.setKurikoshikin("1000");
        e.setKonkaitaxnukiurikin("2000");
        e.setKonkaishohitaxkin("200");
        e.setSkysakicd("000002");
        e.setTokuisakicd("000003");
        e.setShukindydmdd("20170730");
        e.setSkykbn("1");
        // 請求方法区分
        e.setSkyhohokbn("1");
        // 明細印刷区分
        e.setMeisaiprntkbn("1");
        e.setSksyubinno("2請求先郵便番号");
        e.setSksadrsn1("2請求先住所１");
        e.setSksadrsn2("2請求先住所２");
        e.setSksadrsn3("2請求先住所３");
        e.setSksrenrakusakinokbnn1("4");
        e.setSksrenrakusakinon1("08011111111");
        e.setSksrenrakusakinokbnn2("3");
        e.setSksrenrakusakinon2("08022222222");
        e.setSksrenrakusakinokbnn3("2");
        e.setSksrenrakusakinon3("08033333333");
        e.setSksrenrakusakinokbnn4("1");
        e.setSksrenrakusakinon4("08044444444");
        e.setSksskysakinmn1("2請求先名称１");
        e.setSksskysakinmn2("2請求先名称２");
        e.setSksskysakikeishokbn("1");
        e.setSksskysakikeishokbnnm("2請求先敬称");
        e.setTksyubinno("得意先郵便番号");
        e.setTksadrsn1("得意先住所１");
        e.setTksadrsn2("得意先住所２");
        e.setTksadrsn3("得意先住所３");
        e.setTksrenrakusakinokbnn1("1");
        e.setTksrenrakusakinon1("09011111111");
        e.setTksrenrakusakinokbnn2("2");
        e.setTksrenrakusakinon2("09022222222");
        e.setTksrenrakusakinokbnn3("4");
        e.setTksrenrakusakinon3("09033333333");
        e.setTksrenrakusakinokbnn4("4");
        e.setTksrenrakusakinon4("09044444444");
        e.setTkstokuisakinmn1("得意先名称１");
        e.setTkstokuisakinmn2("得意先名称２");
        e.setTkstokuisakikeishokbn("1");
        e.setTkstokuisakikeishokbnnm("得意先敬称");
        e.setTkstokuisakikbn("1");
        e.setTnshamei("2担当者名");
        e.setShopnm("店名");
        e.setSshdshhitoimage("イメージ");
        e.setKaishuhohokbn("1");
        e.setRyosyukin("");
        list.add(e);
        ArrayList<List<String>> csvData = printDataEdit(list, "20170630", "002-036-002036.bmp");
        // csvデータの件数確認
        assertEquals(2, csvData.size());
        // １件目
        assertEquals(20, csvData.get(0).size());
        assertEquals("D1501R35", csvData.get(0).get(0));
        assertEquals("20170630", csvData.get(0).get(1));
        assertEquals("\"000001\"", csvData.get(0).get(2));
        assertEquals("\"002-036-002036.bmp\"", csvData.get(0).get(3));
        assertEquals("\"請求先郵便番号\"", csvData.get(0).get(4));
        assertEquals("\"請求先住所１\"", csvData.get(0).get(5));
        assertEquals("\"請求先住所２\"", csvData.get(0).get(6));
        assertEquals("\"請求先住所３\"", csvData.get(0).get(7));
        assertEquals("\"08022222222\"", csvData.get(0).get(8));
        assertEquals("002", csvData.get(0).get(9));
        assertEquals("036", csvData.get(0).get(10));
        assertEquals("000002", csvData.get(0).get(11));
        assertEquals("\"請求先名称１\"", csvData.get(0).get(12));
        assertEquals("\"請求先名称２\"", csvData.get(0).get(13));
        assertEquals("請求先敬称", csvData.get(0).get(14));
        assertEquals("", csvData.get(0).get(15));
        assertEquals("\"担当者名\"", csvData.get(0).get(16));
        assertEquals("2000", csvData.get(0).get(17));
        assertEquals("", csvData.get(0).get(18));
        assertEquals("20170730", csvData.get(0).get(19));
        assertEquals(20, csvData.get(1).size());
        // 2件目
        assertEquals("D1501R35", csvData.get(1).get(0));
        assertEquals("20170630", csvData.get(1).get(1));
        assertEquals("\"000002\"", csvData.get(1).get(2));
        assertEquals("\"002-036-002036.bmp\"", csvData.get(1).get(3));
        assertEquals("\"2請求先郵便番号\"", csvData.get(1).get(4));
        assertEquals("\"2請求先住所１\"", csvData.get(1).get(5));
        assertEquals("\"2請求先住所２\"", csvData.get(1).get(6));
        assertEquals("\"2請求先住所３\"", csvData.get(1).get(7));
        assertEquals("\"08022222222\"", csvData.get(1).get(8));
        assertEquals("002", csvData.get(1).get(9));
        assertEquals("036", csvData.get(1).get(10));
        assertEquals("000002", csvData.get(1).get(11));
        assertEquals("\"2請求先名称１\"", csvData.get(1).get(12));
        assertEquals("\"2請求先名称２\"", csvData.get(1).get(13));
        assertEquals("2請求先敬称", csvData.get(1).get(14));
        assertEquals("", csvData.get(1).get(15));
        assertEquals("\"2担当者名\"", csvData.get(1).get(16));
        assertEquals("1000", csvData.get(1).get(17));
        assertEquals("", csvData.get(1).get(18));
        assertEquals("20170730", csvData.get(1).get(19));
    }


    /**
     * entity2件 請求方法区分：１（残高請求） 明細印刷区分:1（親子一緒） 請求書番号１つ
     */
    @Test
    void test02() {
        S07T001TD1501R35ListResultEntity e = new S07T001TD1501R35ListResultEntity();
        List<S07T001TD1501R35ListResultEntity> list = new ArrayList<>();
        e.setApkihoncd("002");
        e.setSkyshono("000001");
        e.setShopcd("036");
        e.setKurikoshikin("1000");
        e.setKonkaitaxnukiurikin("2000");
        e.setKonkaishohitaxkin("200");
        e.setSkysakicd("000002");
        e.setTokuisakicd("000003");
        e.setShukindydmdd("20170730");
        e.setSkykbn("1");
        // 請求方法区分
        e.setSkyhohokbn("1");
        // 明細印刷区分
        e.setMeisaiprntkbn("1");
        e.setSksyubinno("請求先郵便番号");
        e.setSksadrsn1("請求先住所１");
        e.setSksadrsn2("請求先住所２");
        e.setSksadrsn3("請求先住所３");
        e.setSksrenrakusakinokbnn1("4");
        e.setSksrenrakusakinon1("08011111111");
        e.setSksrenrakusakinokbnn2("3");
        e.setSksrenrakusakinon2("08022222222");
        e.setSksrenrakusakinokbnn3("2");
        e.setSksrenrakusakinon3("08033333333");
        e.setSksrenrakusakinokbnn4("1");
        e.setSksrenrakusakinon4("08044444444");
        e.setSksskysakinmn1("請求先名称１");
        e.setSksskysakinmn2("請求先名称２");
        e.setSksskysakikeishokbn("1");
        e.setSksskysakikeishokbnnm("請求先敬称");
        e.setTksyubinno("得意先郵便番号");
        e.setTksadrsn1("得意先住所１");
        e.setTksadrsn2("得意先住所２");
        e.setTksadrsn3("得意先住所３");
        e.setTksrenrakusakinokbnn1("1");
        e.setTksrenrakusakinon1("09011111111");
        e.setTksrenrakusakinokbnn2("2");
        e.setTksrenrakusakinon2("09022222222");
        e.setTksrenrakusakinokbnn3("4");
        e.setTksrenrakusakinon3("09033333333");
        e.setTksrenrakusakinokbnn4("4");
        e.setTksrenrakusakinon4("09044444444");
        e.setTkstokuisakinmn1("得意先名称１");
        e.setTkstokuisakinmn2("得意先名称２");
        e.setTkstokuisakikeishokbn("1");
        e.setTkstokuisakikeishokbnnm("得意先敬称");
        e.setTkstokuisakikbn("1");
        e.setTnshamei("担当者名");
        e.setShopnm("店名");
        e.setSshdshhitoimage("イメージ");
        e.setKaishuhohokbn("1");
        e.setRyosyukin("");
        list.add(e);
        e = new S07T001TD1501R35ListResultEntity();
        e.setApkihoncd("002");
        e.setSkyshono("000001");
        e.setShopcd("036");
        e.setKurikoshikin("1000");
        e.setKonkaitaxnukiurikin("2000");
        e.setKonkaishohitaxkin("200");
        e.setSkysakicd("000002");
        e.setTokuisakicd("000003");
        e.setShukindydmdd("20170730");
        e.setSkykbn("1");
        // 請求方法区分
        e.setSkyhohokbn("1");
        // 明細印刷区分
        e.setMeisaiprntkbn("1");
        e.setSksyubinno("請求先郵便番号");
        e.setSksadrsn1("請求先住所１");
        e.setSksadrsn2("請求先住所２");
        e.setSksadrsn3("請求先住所３");
        e.setSksrenrakusakinokbnn1("4");
        e.setSksrenrakusakinon1("08011111111");
        e.setSksrenrakusakinokbnn2("3");
        e.setSksrenrakusakinon2("08022222222");
        e.setSksrenrakusakinokbnn3("2");
        e.setSksrenrakusakinon3("08033333333");
        e.setSksrenrakusakinokbnn4("1");
        e.setSksrenrakusakinon4("08044444444");
        e.setSksskysakinmn1("請求先名称１");
        e.setSksskysakinmn2("請求先名称２");
        e.setSksskysakikeishokbn("1");
        e.setSksskysakikeishokbnnm("請求先敬称");
        e.setTksyubinno("得意先郵便番号");
        e.setTksadrsn1("得意先住所１");
        e.setTksadrsn2("得意先住所２");
        e.setTksadrsn3("得意先住所３");
        e.setTksrenrakusakinokbnn1("1");
        e.setTksrenrakusakinon1("09011111111");
        e.setTksrenrakusakinokbnn2("2");
        e.setTksrenrakusakinon2("09022222222");
        e.setTksrenrakusakinokbnn3("4");
        e.setTksrenrakusakinon3("09033333333");
        e.setTksrenrakusakinokbnn4("4");
        e.setTksrenrakusakinon4("09044444444");
        e.setTkstokuisakinmn1("得意先名称１");
        e.setTkstokuisakinmn2("得意先名称２");
        e.setTkstokuisakikeishokbn("1");
        e.setTkstokuisakikeishokbnnm("得意先敬称");
        e.setTkstokuisakikbn("1");
        e.setTnshamei("担当者名");
        e.setShopnm("店名");
        e.setSshdshhitoimage("イメージ");
        e.setKaishuhohokbn("1");
        e.setRyosyukin("");
        list.add(e);
        ArrayList<List<String>> csvData = printDataEdit(list, "20170630", "002-036-002036.bmp");
        // csvデータの件数確認
        assertEquals(1, csvData.size());
        assertEquals(20, csvData.get(0).size());
        assertEquals("D1501R35", csvData.get(0).get(0));
        assertEquals("20170630", csvData.get(0).get(1));
        assertEquals("\"000001\"", csvData.get(0).get(2));
        assertEquals("\"002-036-002036.bmp\"", csvData.get(0).get(3));
        assertEquals("\"請求先郵便番号\"", csvData.get(0).get(4));
        assertEquals("\"請求先住所１\"", csvData.get(0).get(5));
        assertEquals("\"請求先住所２\"", csvData.get(0).get(6));
        assertEquals("\"請求先住所３\"", csvData.get(0).get(7));
        assertEquals("\"08022222222\"", csvData.get(0).get(8));
        assertEquals("002", csvData.get(0).get(9));
        assertEquals("036", csvData.get(0).get(10));
        assertEquals("000002", csvData.get(0).get(11));
        assertEquals("\"請求先名称１\"", csvData.get(0).get(12));
        assertEquals("\"請求先名称２\"", csvData.get(0).get(13));
        assertEquals("請求先敬称", csvData.get(0).get(14));
        assertEquals("", csvData.get(0).get(15));
        assertEquals("\"担当者名\"", csvData.get(0).get(16));
        assertEquals("2000", csvData.get(0).get(17));
        assertEquals("", csvData.get(0).get(18));
        assertEquals("20170730", csvData.get(0).get(19));
    }

    /**
     * entity１件 請求方法区分：１（残高請求） 明細印刷区分:1（親子一緒）
     */
    @Test
    void test01() {
        S07T001TD1501R35ListResultEntity e = new S07T001TD1501R35ListResultEntity();
        List<S07T001TD1501R35ListResultEntity> list = new ArrayList<>();
        e.setApkihoncd("002");
        e.setSkyshono("000001");
        e.setShopcd("036");
        e.setKurikoshikin("1000");
        e.setKonkaitaxnukiurikin("2000");
        e.setKonkaishohitaxkin("200");
        e.setSkysakicd("000002");
        e.setTokuisakicd("000003");
        e.setShukindydmdd("20170730");
        e.setSkykbn("1");
        // 請求方法区分
        e.setSkyhohokbn("1");
        // 明細印刷区分
        e.setMeisaiprntkbn("1");
        e.setSksyubinno("請求先郵便番号");
        e.setSksadrsn1("請求先住所１");
        e.setSksadrsn2("請求先住所２");
        e.setSksadrsn3("請求先住所３");
        e.setSksrenrakusakinokbnn1("4");
        e.setSksrenrakusakinon1("08011111111");
        e.setSksrenrakusakinokbnn2("3");
        e.setSksrenrakusakinon2("08022222222");
        e.setSksrenrakusakinokbnn3("2");
        e.setSksrenrakusakinon3("08033333333");
        e.setSksrenrakusakinokbnn4("1");
        e.setSksrenrakusakinon4("08044444444");
        e.setSksskysakinmn1("請求先名称１");
        e.setSksskysakinmn2("請求先名称２");
        e.setSksskysakikeishokbn("1");
        e.setSksskysakikeishokbnnm("請求先敬称");
        e.setTksyubinno("得意先郵便番号");
        e.setTksadrsn1("得意先住所１");
        e.setTksadrsn2("得意先住所２");
        e.setTksadrsn3("得意先住所３");
        e.setTksrenrakusakinokbnn1("1");
        e.setTksrenrakusakinon1("09011111111");
        e.setTksrenrakusakinokbnn2("2");
        e.setTksrenrakusakinon2("09022222222");
        e.setTksrenrakusakinokbnn3("4");
        e.setTksrenrakusakinon3("09033333333");
        e.setTksrenrakusakinokbnn4("4");
        e.setTksrenrakusakinon4("09044444444");
        e.setTkstokuisakinmn1("得意先名称１");
        e.setTkstokuisakinmn2("得意先名称２");
        e.setTkstokuisakikeishokbn("1");
        e.setTkstokuisakikeishokbnnm("得意先敬称");
        e.setTkstokuisakikbn("1");
        e.setTnshamei("担当者名");
        e.setShopnm("店名");
        e.setSshdshhitoimage("イメージ");
        e.setKaishuhohokbn("1");
        e.setRyosyukin("");
        list.add(e);
        ArrayList<List<String>> csvData = printDataEdit(list, "20170630", "002-036-002036.bmp");
        // csvデータの件数確認
        assertEquals(1, csvData.size());
        assertEquals(20, csvData.get(0).size());
        assertEquals("D1501R35", csvData.get(0).get(0));
        assertEquals("20170630", csvData.get(0).get(1));
        assertEquals("\"000001\"", csvData.get(0).get(2));
        assertEquals("\"002-036-002036.bmp\"", csvData.get(0).get(3));
        assertEquals("\"請求先郵便番号\"", csvData.get(0).get(4));
        assertEquals("\"請求先住所１\"", csvData.get(0).get(5));
        assertEquals("\"請求先住所２\"", csvData.get(0).get(6));
        assertEquals("\"請求先住所３\"", csvData.get(0).get(7));
        assertEquals("\"08022222222\"", csvData.get(0).get(8));
        assertEquals("002", csvData.get(0).get(9));
        assertEquals("036", csvData.get(0).get(10));
        assertEquals("000002", csvData.get(0).get(11));
        assertEquals("\"請求先名称１\"", csvData.get(0).get(12));
        assertEquals("\"請求先名称２\"", csvData.get(0).get(13));
        assertEquals("請求先敬称", csvData.get(0).get(14));
        assertEquals("", csvData.get(0).get(15));
        assertEquals("\"担当者名\"", csvData.get(0).get(16));
        assertEquals("1000", csvData.get(0).get(17));
        assertEquals("", csvData.get(0).get(18));
        assertEquals("20170730", csvData.get(0).get(19));
    }
}
