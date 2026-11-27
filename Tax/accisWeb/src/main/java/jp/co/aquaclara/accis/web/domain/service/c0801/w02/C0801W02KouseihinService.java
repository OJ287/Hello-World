package jp.co.aquaclara.accis.web.domain.service.c0801.w02;

import static jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02ShopCalculateOptionDto.REDUCED_TAX_RATE_STRDD;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M008TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02V001TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M008TSetShohinEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntityKey;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02AcptDlvryDetailDto;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02ShopCalculateOptionDto;
import jp.co.aquaclara.accis.web.domain.service.c0800.UtilityService;

/**
 * C0801W02_受注登録（受注登録）_構成品サービス.
 */
@Service
public class C0801W02KouseihinService {

    /** 受注配達明細価格サービス */
    @Autowired
    private C0801W02AcptDlvryDetailPriceService priceService;

    /** 展開マスタDAO */
    @Autowired
    private S02M008TDao s02m008tDao;

    /** 商品マスタビューDAO */
    @Autowired
    private S02V001TDao s02v001tDao;

    /** ユーティリティ */
    @Autowired
    private UtilityService util;

    /**
     * 子明細商品の消費税を計算する.<br>
     * <br>
     * 受注配達明細データの以下の項目に計算結果を設定する
     * <ul>
     * <li>受注単価消費税金額</li>
     * <li>受注消費税金額</li>
     * <li>受注金額</li>
     * </ul>
     * 
     * @param targetDate 対象日（軽減税率施行日、インボイス施行日判定用）
     * @param dlvrykbn 配達区分
     * @param detailDto 受注配達明細DTO
     * @param calcOptionDto ショップ計算オプション
     */
    public void calculate(String targetDate, String dlvrykbn, C0801W02AcptDlvryDetailDto detailDto,
        C0801W02ShopCalculateOptionDto calcOptionDto) {

        // 展開マスタからセット商品構成情報を取得する
        List<S02M008TSetShohinEntity> setShnList = s02m008tDao.getSetShohinList(
            detailDto.getApkihoncd(), detailDto.getShopcd(), detailDto.getShncd());

        // セット商品の数量
        BigDecimal suryoSetShn = detailDto.getDecimalValue(detailDto.getAcptsuryo());

        // セット商品の単価
        BigDecimal untSetShn = BigDecimal.ZERO;

        // 最終的な計算結果を格納する変数を初期化
        BigDecimal setShnUntShohiTax = BigDecimal.ZERO;
        BigDecimal setShnShohiTax = BigDecimal.ZERO;
        BigDecimal setShnAcptkin = BigDecimal.ZERO;

        // 取得した商品ごとに繰り返し
        for (S02M008TSetShohinEntity setChildShn : setShnList) {

            // 商品マスタビューから構成商品の情報を取得
            S02V001TEntity childShnInfo =
                getShnInfo(detailDto.getApkihoncd(), detailDto.getShopcd(), detailDto.getShncd());

            // 構成商品の単価を取得
            BigDecimal untChild = CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(dlvrykbn)
                ? setChildShn.getMochikaeriprice()
                : setChildShn.getDlvryprice();
            // 軽減税率対応
            if (util.isLater(targetDate, REDUCED_TAX_RATE_STRDD)) {
                // 軽減税率施行日以降の場合
                // 受注数量の算出（セット商品の数量×構成商品の数量）
                BigDecimal acptSuryo = suryoSetShn.multiply(setChildShn.getSuryo());
                // 構成商品の商品税区分を取得（計上区分を考慮）
                String childShnKeijoKbn =
                    CommonConstants.KBN_KEIJO_KEIHI.equals(setChildShn.getKeijokbn())
                        ? CommonConstants.KBN_TAX_HIKAZEI
                        : childShnInfo.getTaxkbn();
                BigDecimal childUntShohiTax = BigDecimal.ZERO;
                BigDecimal childShohiTax = BigDecimal.ZERO;
                BigDecimal childAcptkin = BigDecimal.ZERO;
                // 親商品が有料の場合は構成商品の計上区分をセットし、金額を算出する
                if (detailDto.getAcptRelation().isYuryo()) {
                    BigDecimal[] priceList = priceService.getPriceList(untChild, acptSuryo,
                        childShnKeijoKbn, targetDate, detailDto);
                    childUntShohiTax = priceList[0];
                    childShohiTax = priceList[1];
                    childAcptkin = priceList[2];
                }
                // セット商品の受注単価消費税金額を算出する。
                setShnUntShohiTax =
                    setShnUntShohiTax.add(childUntShohiTax.multiply(setChildShn.getSuryo()));
                // //セット商品の受注消費税金額を算出する。
                setShnShohiTax = setShnShohiTax.add(childShohiTax);
                // //セット商品の税抜金額を算出する。
                setShnAcptkin = setShnAcptkin.add(childAcptkin);
            } else {
                // 軽減税率施行日以前の場合、構成商品の単価×数量をセット商品の単価に加算していく
                // 算出結果は後で使用する
                untSetShn = untSetShn.add(untChild.multiply(setChildShn.getSuryo()));
            }
        }
        // 軽減税率施行前かつ親商品が有料の場合はセット商品の単価と数量を元に計算を行う
        if (!util.isLater(targetDate, REDUCED_TAX_RATE_STRDD)
            && detailDto.getAcptRelation().isYuryo()) {
            // 現行では計上区分を考慮して商品税区分を指定しているが、
            // 課税固定なので直接商品税区分を指定する
            BigDecimal[] priceList = priceService.getPriceList(untSetShn, suryoSetShn,
                detailDto.getShntaxkbn(), targetDate, detailDto);
            setShnUntShohiTax = priceList[0];
            setShnShohiTax = priceList[1];
            setShnAcptkin = priceList[2];
        }
        // 計算結果を受注配達明細データに設定
        detailDto.setAcptuntshohitaxkin(setShnUntShohiTax.toString());
        detailDto.setAcptshohitaxkin(setShnShohiTax.toString());
        detailDto.setAcptkin(setShnAcptkin.toString());
    }

    /**
     * 商品マスタビューから商品情報を取得する.
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param shncd 商品コード
     * @return 商品情報
     */
    protected S02V001TEntity getShnInfo(String apkihoncd, String shopcd, String shncd) {
        S02V001TEntityKey key = new S02V001TEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setShncd(shncd);
        S02V001TEntity entity = s02v001tDao.selectByPrimaryKey(key);
        // 商品の税設定区分がOFFの場合は、課税として税計算する（税区分をnullにする）
        if (!util.isFlagged(entity.getTaxsetteikbn())) {
            entity.setTaxkbn(null);
        }
        return entity;
    }
}
