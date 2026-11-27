package jp.co.aquaclara.accis.web.domain.service.x9901;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M013TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M008TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.business.S02M008TEntityEx;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.JuchuMeisaiVO;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.JuchuRelationVo;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.PriceInfoVo;
import jp.co.aquaclara.accis.common.util.business.BusinessUtil;
import jp.co.aquaclara.accis.common.util.business.NumberUtils;


/**
 * 子明細商品の消費税計算用業務ロジックを記述するクラス。
 * 
 * @author Isac)
 * @version 1.0.0 2007-06-10
 */
@Service
public class KouseihinService {

    @Autowired
    private S02M013TDao s02m013tDao;

    @Autowired
    private CommonService commonService;

    @Autowired
    private ShohinService shohinService;

    @Autowired
    private PriceService priceService;

    /** QuiQproのログ出力クラスです。 */
    // private static final Logger LOGGER = (Logger) Logger.getLogger(S01KouseihinService.class);

    /**
     * 子明細商品の消費税を計算します．
     * 
     * @param apkihoncd APコード
     * @param shopcd ショップコード
     * @param syohinCd 商品コード
     * @param suryo 数量
     * @param tokuisakiCd 得意先コード
     * @param keijokbn 計上区分
     * @param targetDate 対象日
     * @return String[] 結果（受注単価消費税金額、受注消費税金額、税抜金額）
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public String[] getKouseihinTax(String apkihoncd, String shopcd, String syohinCd, String suryo,
        String tokuisakiCd, String keijokbn, String targetDate) {

        // DbTransaction tran = new DbTransaction();
        try {

            // サービスクラス取得
            // S02Service s02Service = new S02Service(tran);
            // ショップマスタモデル取得
            S02M046TEntity shopModel = commonService.getShopModel(apkihoncd, shopcd);

            S02M013TEntityKey tokuisakiKey = new S02M013TEntityKey();
            tokuisakiKey.setApkihoncd(apkihoncd);
            tokuisakiKey.setShopcd(shopcd);
            tokuisakiKey.setTokuisakicd(tokuisakiCd);

            S02M013TDao dao = s02m013tDao;
            S02M013TEntity tokuisakiModel = dao.selectByPrimaryKey(tokuisakiKey);

            // サービスクラス取得
            // ShohinService s02ShohinService = new ShohinService();

            // 構成品リスト取得
            List<S02M008TEntityEx> setList =
                shohinService.getSetShohinList(apkihoncd, shopcd, syohinCd);

            // セット商品の受注単価消費税金額
            long singlShohiTaxkinSetShohi = 0;
            // セット商品の受注消費税金額
            long shohitaxkinSetShohi = 0;
            // セット商品の税抜金額
            long shohikinSetShohi = 0;
            // セット商品の単価
            long tankaSetShohi = 0;

            PriceInfoVo priceInfoVo = new PriceInfoVo(shopModel, targetDate);

            // 構成品リスト生成
            for (int j = 0; j < setList.size(); j++) {

                S02M008TEntity childModel = (S02M008TEntity) setList.get(j);

                // 商品マスタビュー情報取得
                S02V001TEntity shohinModelKosei =
                    shohinService.getShohinModel(apkihoncd, shopcd, childModel.getKoseishncd());

                String tanka = BusinessUtil.getDlvryValue(tokuisakiModel.getDlvrykbn(),
                    childModel.getDlvryprice().toString(),
                    childModel.getMochikaeriprice().toString());
                if ("20190930".compareTo(targetDate) < 0) {
                    // 数量のセット
                    String acptsuryo = "";
                    if (NumberUtils.isNumber(suryo) && childModel.getSuryo() != null) {
                        acptsuryo =
                            (new BigDecimal(suryo).multiply(childModel.getSuryo()).toString());
                        /* 2019-08-23 Pkut)Yang 軽減税率対応 update begin */
                    } else {
                        acptsuryo = "0";
                        /* 2019-08-23 Pkut)Yang 軽減税率対応 update end */
                    }

                    String[] priceListAcpt = null;
                    BigDecimal singlShohitaxkin = new BigDecimal(0);
                    BigDecimal shohitaxkin = new BigDecimal(0);
                    BigDecimal shohikin = new BigDecimal(0);
                    // 親商品が有料の場合は構成商品の計上区分をセットし、金額を算出する
                    if (JuchuRelationVo.isYuryo(keijokbn)) {

                        priceListAcpt =
                            priceService.getPriceList(priceInfoVo, tanka, acptsuryo,
                                JuchuMeisaiVO.getShnTaxKbnByKeijoKbn(childModel.getKeijokbn(),
                                    shohinModelKosei.getTaxkbn()),
                                tokuisakiModel.getTokuisakikbn());
                        // 構成商品の単価消費税金額
                        singlShohitaxkin = new BigDecimal(priceListAcpt[0]);
                        // 構成商品の消費税金額
                        shohitaxkin = new BigDecimal(priceListAcpt[1]);
                        // 税抜金額
                        shohikin = new BigDecimal(priceListAcpt[2]);
                    }

                    // 構成商品数量
                    BigDecimal suryoBD = childModel.getSuryo();

                    // セット商品の受注単価消費税金額を算出する。
                    singlShohiTaxkinSetShohi += singlShohitaxkin.multiply(suryoBD).longValue();
                    // セット商品の受注消費税金額を算出する。
                    shohitaxkinSetShohi += shohitaxkin.longValue();
                    // セット商品の税抜金額を算出する。
                    shohikinSetShohi += shohikin.longValue();
                } else {
                    tankaSetShohi =
                        tankaSetShohi + Long.valueOf(tanka) * childModel.getSuryo().longValue();
                }
            }
            if (JuchuRelationVo.isYuryo(keijokbn) && "20190930".compareTo(targetDate) >= 0) {
                String[] priceListAcpt = null;
                priceListAcpt = priceService.getPriceList(priceInfoVo,
                    String.valueOf(tankaSetShohi), suryo,
                    JuchuMeisaiVO.getShnTaxKbnByKeijoKbn(keijokbn, CommonConstants.KBN_TAX_KAZEI),
                    tokuisakiModel.getTokuisakikbn());
                // 構成商品の単価消費税金額
                // セット商品の受注単価消費税金額を算出する。
                singlShohiTaxkinSetShohi = Long.valueOf(priceListAcpt[0]);
                // セット商品の受注消費税金額を算出する。
                shohitaxkinSetShohi = Long.valueOf(priceListAcpt[1]);
                // セット商品の税抜金額を算出する。
                shohikinSetShohi = Long.valueOf(priceListAcpt[2]);
            }
            return new String[] {String.valueOf(singlShohiTaxkinSetShohi),
                String.valueOf(shohitaxkinSetShohi), String.valueOf(shohikinSetShohi)};

        } catch (Exception e) {
            // S01Util.debugLogOut(LOGGER, e.getMessage());
            return new String[] {"0", "0", "0"};
        } finally {
            // トランザクションを閉じる。
            // try {
            // tran.end();
            // } catch (Exception e) {
            // S01Util.debugLogOut(LOGGER, e.getMessage());
            // }

        }

    }

}
