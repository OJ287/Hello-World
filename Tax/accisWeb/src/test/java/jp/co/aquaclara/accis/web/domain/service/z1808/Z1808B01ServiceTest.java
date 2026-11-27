package jp.co.aquaclara.accis.web.domain.service.z1808;

import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import jp.co.aquaclara.accis.common.domain.repository.dao.Z1808B01Dao;
import jp.co.aquaclara.accis.common.domain.repository.entity.Z1808B01Entity;

@SpringBootTest
public class Z1808B01ServiceTest {

    @Autowired
    private Z1808B01Dao z1808b01Dao;

    @Test
    void test() {
        syohizeiKeisan();
        roundValue();
        setSyohizeiKeisan();
    }

    void setSyohizeiKeisan() {
        try {
            Z1808B01Entity z1808b01Entity = new Z1808B01Entity();
            /* ファンクション名 ：セット商品税計算 */
            /* 引数 */
            /* ：iTaxsetteikbn 税設定区分 */
            /* ：iTaxKeisanKbn 税計算区分 (固定しない場合はNULL) */
            /* ：iKirikaebi 切り替え日 */
            /* ：iApKihoncd AP基本コード */
            /* ：iShopcd ショップコード */
            /* ：iSuryo 数量 */
            /* ：iSyohinCd 商品コード */
            /* ：iDlvrykbn 配達区分 */
            /* ：iKeijokbn 計上区分 */
            /* return ：oKidUntTaxSum セット商品単価消費税 */
            /* ：oKidTaxSum セット商品消費税 */
            z1808b01Entity.setTaxsetteikbn("1");
            z1808b01Entity.setTaxkeisankbn(null);
            z1808b01Entity.setKirikaebi("20000830");
            z1808b01Entity.setApkihoncd("020");
            z1808b01Entity.setShopcd("001");
            z1808b01Entity.setSuryo("1");
            z1808b01Entity.setSyohincd("000601");
            z1808b01Entity.setDlvrykbn(null);
            z1808b01Entity.setKeijokbn("01");

            z1808b01Dao.setSyohizeiKeisan(z1808b01Entity);

            System.out.println("セット商品単価消費税額:" + z1808b01Entity.getKidunttaxsum());
            System.out.println("セット商品消費税額:" + z1808b01Entity.getKidtaxsum());

        } catch (RuntimeException e) {
            System.out.println(
                "【消費税計算処理】******************************************************************* \n");
            System.out.println("エラー内容：" + e.getMessage() + "\n");
            System.out
                .println("******************************************************************* \n");
            fail("exception", e);
        }
    }

    void roundValue() {
        try {
            Z1808B01Entity z1808b01Entity = new Z1808B01Entity();
            z1808b01Entity.setHasukeisankbn("3");
            z1808b01Entity.setValue(25700.11111);

            int result = z1808b01Dao.roundValue(z1808b01Entity);

            System.out.println("端数計算結果(切り上げ):" + result);

            z1808b01Entity.setHasukeisankbn("2");
            z1808b01Entity.setValue(25700.77777);
            result = z1808b01Dao.roundValue(z1808b01Entity);
            System.out.println("端数計算結果(切り捨て):" + result);

            z1808b01Entity.setHasukeisankbn(null);
            z1808b01Entity.setValue(25700.44444);
            result = z1808b01Dao.roundValue(z1808b01Entity);
            System.out.println("端数計算結果(四捨五入):" + result);

            z1808b01Entity.setHasukeisankbn(null);
            z1808b01Entity.setValue(25700.55555);
            result = z1808b01Dao.roundValue(z1808b01Entity);
            System.out.println("端数計算結果(四捨五入):" + result);

        } catch (RuntimeException e) {
            System.out.println(
                "【消費税計算処理】******************************************************************* \n");
            System.out.println("エラー内容：" + e.getMessage() + "\n");
            System.out
                .println("******************************************************************* \n");
            fail("exception", e);
        }
    }

    void syohizeiKeisan() {
        try {
            Z1808B01Entity z1808b01Entity = new Z1808B01Entity();
            /* 使用方法 ：①税抜金額（税抜単価）より消費税額を算出する場合、 */
            /* ：iSimpleFgに1を、iKinに値をセットする */
            /* ：税抜単価、数量から消費税金額を算出する場合、 */
            /* ：iSimpleFgに0を、iKin・iSuryoに値をセットする */
            /* ：②税計算区分（内税・外税）を固定で決定したい場合、 */
            /* ：引数の税計算区分に値をセット（そうでない場合はNULL） */
            /* ：ただし、単体計算時は常に外税計算となる */
            /* ：③仕入時計算の場合はiSiiresakicdに値をセットする */
            /* ：（そうでない場合は'000000'をセット） */
            /* 引数 ：iSimpleFg 単体計算フラグ (0:しない 1:する) */
            /* ：iTaxsetteikbn 税設定区分 */
            /* ：iTaxkbn 税区分 */
            /* ：iTaxKeisanKbn 税計算区分 (固定しない場合はNULL) */
            /* ：iKirikaebi 切り替え日 */
            /* ：iApKihoncd AP基本コード */
            /* ：iShopcd ショップコード */
            /* ：iSiiresakicd 仕入先コード */
            /* ：iSuryo 数量 */
            /* ：iKin 税抜金額（税抜単価） */
            /* return ：returnShohitaxk 消費税額 */
            z1808b01Entity.setSimplefg("1");
            z1808b01Entity.setTaxsetteikbn("1");
            z1808b01Entity.setTaxkbn("1");
            z1808b01Entity.setTaxkeisankbn(null);
            z1808b01Entity.setKirikaebi("20000830");
            z1808b01Entity.setApkihoncd("020");
            z1808b01Entity.setShopcd("001");
            z1808b01Entity.setSiiresakicd(null);
            z1808b01Entity.setSuryo(null);
            z1808b01Entity.setKin("25700");

            int result = z1808b01Dao.syohizeiKeisan(z1808b01Entity);

            System.out.println("消費税額(5%):" + result);

            z1808b01Entity.setSimplefg("1");
            z1808b01Entity.setTaxsetteikbn("1");
            z1808b01Entity.setTaxkbn("1");
            z1808b01Entity.setTaxkeisankbn(null);
            z1808b01Entity.setKirikaebi("20150830");
            z1808b01Entity.setApkihoncd("020");
            z1808b01Entity.setShopcd("001");
            z1808b01Entity.setSiiresakicd(null);
            z1808b01Entity.setSuryo(null);
            z1808b01Entity.setKin("25700");

            result = z1808b01Dao.syohizeiKeisan(z1808b01Entity);
            System.out.println("消費税額(8%):" + result);

            z1808b01Entity.setSimplefg("1");
            z1808b01Entity.setTaxsetteikbn("1");
            z1808b01Entity.setTaxkbn("1");
            z1808b01Entity.setTaxkeisankbn(null);
            z1808b01Entity.setKirikaebi("20220830");
            z1808b01Entity.setApkihoncd("020");
            z1808b01Entity.setShopcd("001");
            z1808b01Entity.setSiiresakicd(null);
            z1808b01Entity.setSuryo(null);
            z1808b01Entity.setKin("25700");

            result = z1808b01Dao.syohizeiKeisan(z1808b01Entity);
            System.out.println("消費税額(10%):" + result);

        } catch (RuntimeException e) {
            System.out.println(
                "【消費税計算処理】******************************************************************* \n");
            System.out.println("エラー内容：" + e.getMessage() + "\n");
            System.out
                .println("******************************************************************* \n");
            fail("exception", e);
        }
    }

}
