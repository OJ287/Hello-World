package jp.co.aquaclara.accis.web.domain.service.c1002;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M008TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M066TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S06T002TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M008TSetShohinEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M066TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M066TSetShohinTankaEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S06T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S06T002TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S06T002TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S06T002TC1002W01Entity;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.controller.c1002.bean.C1002W01BaseBean;
import jp.co.aquaclara.accis.web.controller.c1002.bean.C1002W01MeisaiBean;
import jp.co.aquaclara.accis.web.controller.c1002.form.C1002W01Form;
import jp.co.aquaclara.accis.web.controller.commons.DbManagementSession;
import jp.co.aquaclara.accis.web.domain.service.business.TaxService;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;

/**
 * 売上入力 売上明細テーブル関連Service
 */
@Service
public class C1002W01UriageMeisaiService {

    /**
     * 展開マスタDao
     */
    @Autowired
    private S02M008TDao s02m008mDao;

    /**
     * 得意先別セット商品単価マスタDao
     */
    @Autowired
    private S02M066TDao s02m066tDao;

    /**
     * 売上明細テーブルDao
     */
    @Autowired
    private S06T002TDao s06t002tDao;

    /**
     * 売上入力_共通Service
     */
    @Autowired
    private C1002W01CommonService commonService;

    /**
     * 売上入力_メインService
     */
    @Autowired
    private C1002W01Service mainService;

    /**
     * 商品明細消費税・金額計算サービスクラス
     */
    @Autowired
    private TaxService taxService;

    /**
     * 売上明細を登録する
     * 
     * @param uriageMeisaiEntityList List<S06T002TEntity>
     */
    public void insertUriageMeisai(List<S06T002TEntity> uriageMeisaiEntityList) {
        for (S06T002TEntity uriageMeisaiEntity : uriageMeisaiEntityList) {
            try {
                s06t002tDao.insert(uriageMeisaiEntity);
            } catch (DuplicateKeyException e) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W089);
            } catch (RuntimeException re) {
                throw BusinessExceptionFactory.validationFailed(MessageID.E002);
            }
        }
    }

    /**
     * 売上明細リスト（新規用）を生成
     * 
     * @param form C1002W01Form
     * @param dbManagementSession DbManagementSession
     * @param uriageEntity S06T001TEntity
     * @param tokuisakiEntity S02M013TEntity
     * @param kaishuhohokbn 回収方法区分
     * @param invoiceEnforced インボイス施行済みフラグ
     * @return List<S06T002TEntity>
     */
    public List<S06T002TEntity> createBaseUriageMeisaiList(C1002W01Form form,
        DbManagementSession dbManagementSession, S06T001TEntity uriageEntity,
        S02M013TEntity tokuisakiEntity, String kaishuhohokbn, boolean invoiceEnforced) {

        // 得意先区分
        String tokuisakikbn = tokuisakiEntity.getTokuisakikbn();

        // 受託配達顧客区分
        String jutakudlvrycstkbn = tokuisakiEntity.getJutakudlvrycstkbn();

        List<S06T002TEntity> uriagemeisaiEntityList = new ArrayList<>();

        int i = 1;
        for (C1002W01MeisaiBean meisaiBean : form.getMeisaiBeanList()) {

            String shncd = meisaiBean.getShncdmei();
            if(shncd.equals(CommonConstants.SHNCD_099990)) {
                // 入力された予定ポイントをBaseBeanに保持する
                form.getBaseBean().setPointsu(meisaiBean.getPointsu());
                continue;
            }

            S06T002TEntity uriageMeisaiEntity = new S06T002TEntity();
            uriageMeisaiEntity.setApkihoncd(uriageEntity.getApkihoncd());
            uriageMeisaiEntity.setShopcd(uriageEntity.getShopcd());
            uriageMeisaiEntity.setUridnpyno(uriageEntity.getUridnpyno());
            uriageMeisaiEntity.setEdano(Short.parseShort("0"));
            uriageMeisaiEntity.setUridnpymeisaino(i);
            uriageMeisaiEntity.setUridnpymeisaiedano(Short.parseShort("0"));
            uriageMeisaiEntity.setLctgrycd(meisaiBean.getLctgrycd());
            uriageMeisaiEntity.setMctgrycd(meisaiBean.getMctgrycd());
            uriageMeisaiEntity.setSctgrycd(meisaiBean.getSctgrycd());
            uriageMeisaiEntity.setShncd(meisaiBean.getShncdmei());
            uriageMeisaiEntity.setKeijokbn(meisaiBean.getKeijokbnmei());
            uriageMeisaiEntity
                .setSuryo(new BigDecimal(ItemUtil.removeComma(meisaiBean.getSuryo1mei())));
            uriageMeisaiEntity.setHyojununt(new BigDecimal(meisaiBean.getHyojununt()));
            uriageMeisaiEntity
                .setUnt(new BigDecimal(ItemUtil.removeComma(meisaiBean.getUnt1mei())));
            uriageMeisaiEntity
                .setKin(Long.parseLong(ItemUtil.removeComma(meisaiBean.getKin1mei())));
            uriageMeisaiEntity.setUntshohitaxkin(Long.parseLong(meisaiBean.getUntshohitaxkin()));

            // 代引きの場合
            if (CommonConstants.KBN_KAISHUHOHO_DAIBIKI.equals(kaishuhohokbn)) {
                // 代引きの場合
                uriageMeisaiEntity.setShohitaxkin(Long.parseLong("0"));
                uriageMeisaiEntity.setSankoushohitaxkin(
                    Long.parseLong(ItemUtil.removeComma(meisaiBean.getShohitaxkinn1mei())));

            } else if (invoiceEnforced) {
                // インボイス施行済みの場合
                uriageMeisaiEntity.setShohitaxkin(Long.parseLong("0"));
                uriageMeisaiEntity.setSankoushohitaxkin(
                    Long.parseLong(ItemUtil.removeComma(meisaiBean.getShohitaxkinn1mei())));

            } else {
                // インボイス施行前の場合
                uriageMeisaiEntity.setShohitaxkin(
                    Long.parseLong(ItemUtil.removeComma(meisaiBean.getShohitaxkinn1mei())));
                uriageMeisaiEntity.setSankoushohitaxkin(Long.parseLong("0"));
            }

            uriageMeisaiEntity.setTaxkbn(meisaiBean.getTaxkbn());
            uriageMeisaiEntity.setMeisaikbn(CommonConstants.KBN_MEISAI_SHOHIN);
            uriageMeisaiEntity.setNyuryokumeisaiflg(meisaiBean.getNyuryokumeisaiflg());
            uriageMeisaiEntity.setTanpinmeisaiflg(meisaiBean.getTanpinmeisaiflg());
            uriageMeisaiEntity.setMeisaibiko(meisaiBean.getMeisaibikomei());
            uriageMeisaiEntity.setStshncd(meisaiBean.getStshncd());
            uriageMeisaiEntity.setKatasiki(meisaiBean.getKatasikimei());
            uriageMeisaiEntity
                .setSiiresuryo(new BigDecimal(ItemUtil.removeComma(meisaiBean.getSuryo2mei())));
            uriageMeisaiEntity
                .setSiirekin(Long.parseLong(ItemUtil.removeComma(meisaiBean.getKin2mei())));
            uriageMeisaiEntity
                .setSiireunt(new BigDecimal(ItemUtil.removeComma(meisaiBean.getUnt2mei())));
            uriageMeisaiEntity.setSiireshohitaxkin(
                Long.parseLong(ItemUtil.removeComma(meisaiBean.getShohitaxkinn2mei())));

            uriageMeisaiEntity.setNyuryokumeisaiflg(CommonConstants.FLG_ON);
            uriageMeisaiEntity.setZaikokshnsumiflg(meisaiBean.getZaikokshnsumiflg());
            uriageMeisaiEntity.setTokuisakikbn(tokuisakikbn);
            uriageMeisaiEntity.setJutakudlvrycstkbn(jutakudlvrycstkbn);
            uriageMeisaiEntity.setRoyaltyshimeymd(CommonConstants.DEF_DATE_00000000);
            uriageMeisaiEntity.setYukokbn(CommonConstants.YUKOKBN_1);

            // 登録者情報
            uriageMeisaiEntity.setToshaapkihoncd(dbManagementSession.getApkihoncd());
            uriageMeisaiEntity.setToshashopcd(dbManagementSession.getShopcd());
            uriageMeisaiEntity.setToshacd(dbManagementSession.getTnshacd());
            uriageMeisaiEntity.setInptsystmymd(dbManagementSession.getSystemDate());
            uriageMeisaiEntity.setInptsystmjikan(dbManagementSession.getSystemTime());
            uriageMeisaiEntity.setInptunyoymd(dbManagementSession.getUnyoDate());
            uriageMeisaiEntity.setInptipadrse(dbManagementSession.getIpAddress());
            uriageMeisaiEntity.setInptpgmid(dbManagementSession.getProgramId());

            // 更新者情報
            uriageMeisaiEntity.setKoshaapkihoncd(dbManagementSession.getApkihoncd());
            uriageMeisaiEntity.setKoshashopcd(dbManagementSession.getShopcd());
            uriageMeisaiEntity.setKoshacd(dbManagementSession.getTnshacd());
            uriageMeisaiEntity.setKshnsystmymd(dbManagementSession.getSystemDate());
            uriageMeisaiEntity.setKshnsystmjikan(dbManagementSession.getSystemTime());
            uriageMeisaiEntity.setKshnunyoymd(dbManagementSession.getUnyoDate());
            uriageMeisaiEntity.setKshnipadrse(dbManagementSession.getIpAddress());
            uriageMeisaiEntity.setKshnpgmid(dbManagementSession.getProgramId());

            uriagemeisaiEntityList.add(uriageMeisaiEntity);

            i++;
        }

        return uriagemeisaiEntityList;
    }

    /**
     * 売上明細リスト（取消用）を生成
     * 
     * @param form C1002W01Form
     * @param dbManagementSession DbManagementSession
     * @param uriageEntity S06T001TEntity
     * @param kaishuhohokbn 回収方法区分
     * @param invoiceEnforced インボイス施行済みフラグ
     * @return List<S06T002TEntity>
     */
    public List<S06T002TEntity> createCancelUriageMeisaiList(C1002W01Form form,
        DbManagementSession dbManagementSession, S06T001TEntity uriageEntity, String kaishuhohokbn,
        boolean invoiceEnforced) {

        C1002W01BaseBean baseBean = form.getBaseBean();
        String apkihoncd = baseBean.getApkihoncd();
        String shopcd = baseBean.getShopcd();
        String uridnpyno = form.getBaseBean().getUridnpyno();

        // 売上明細リストを取得
        S06T002TEntityExample example = new S06T002TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd)
            .andUridnpynoEqualTo(uridnpyno);
        example.setOrderByClause("URIDNPYMEISAINO, URIDNPYMEISAIEDANO ASC");

        List<S06T002TEntity> uriagemeisaiEntityList = s06t002tDao.selectByExample(example);

        // 取消対象となる売上明細を元に赤伝票用の売上明細を作成する（取消内容を反映する）
        uriagemeisaiEntityList.forEach(meisaiEntity -> {
            // 売上伝票番号
            meisaiEntity.setUridnpyno(uriageEntity.getUridnpyno());
            // 消費税レコードの場合
            String shncd = meisaiEntity.getShncd();;
            if (CommonConstants.SHNCD_099901.equals(shncd)
                || CommonConstants.SHNCD_099902.equals(shncd)) {
                meisaiEntity.setShohitaxkin(meisaiEntity.getShohitaxkin() * -1);
            } else {
                // 数量
                meisaiEntity.setSuryo(meisaiEntity.getSuryo().multiply(new BigDecimal(-1)));

                // 金額
                meisaiEntity.setKin(meisaiEntity.getKin() * -1);

                // 消費税金額
                meisaiEntity.setShohitaxkin(meisaiEntity.getShohitaxkin() * -1);

                // 参考消費税（NULLのデータが存在するため）
                Long sankoshohitaxkin = Objects.isNull(meisaiEntity.getSankoushohitaxkin()) ? null
                    : meisaiEntity.getSankoushohitaxkin() * -1;
                meisaiEntity.setSankoushohitaxkin(sankoshohitaxkin);

                // 仕入数量
                meisaiEntity
                    .setSiiresuryo(meisaiEntity.getSiiresuryo().multiply(new BigDecimal(-1)));

                // 仕入金額
                meisaiEntity.setSiirekin(meisaiEntity.getSiirekin() * -1);

                // 仕入消費税金額
                meisaiEntity.setSiireshohitaxkin(meisaiEntity.getSiireshohitaxkin() * -1);

            }

            // ロイヤリティ締め年月日 ACCIS2023-6679 要望対応
            // ACCIS2023-6702 代引得意先の消費税明細に対してもロイヤリティ締め年月日には「00000000」を設定
            meisaiEntity.setRoyaltyshimeymd(CommonConstants.DEF_DATE_00000000);
            // 有効区分
            meisaiEntity.setYukokbn(CommonConstants.YUKOKBN_1);

            // 登録者情報
            meisaiEntity.setToshaapkihoncd(dbManagementSession.getApkihoncd());
            meisaiEntity.setToshashopcd(dbManagementSession.getShopcd());
            meisaiEntity.setToshacd(dbManagementSession.getTnshacd());
            meisaiEntity.setInptsystmymd(dbManagementSession.getSystemDate());
            meisaiEntity.setInptsystmjikan(dbManagementSession.getSystemTime());
            meisaiEntity.setInptunyoymd(dbManagementSession.getUnyoDate());
            meisaiEntity.setInptipadrse(dbManagementSession.getIpAddress());
            meisaiEntity.setInptpgmid(dbManagementSession.getProgramId());

            // 更新者情報
            meisaiEntity.setKoshaapkihoncd(dbManagementSession.getApkihoncd());
            meisaiEntity.setKoshashopcd(dbManagementSession.getShopcd());
            meisaiEntity.setKoshacd(dbManagementSession.getTnshacd());
            meisaiEntity.setKshnsystmymd(dbManagementSession.getSystemDate());
            meisaiEntity.setKshnsystmjikan(dbManagementSession.getSystemTime());
            meisaiEntity.setKshnunyoymd(dbManagementSession.getUnyoDate());
            meisaiEntity.setKshnipadrse(dbManagementSession.getIpAddress());
            meisaiEntity.setKshnpgmid(dbManagementSession.getProgramId());
        });

        return uriagemeisaiEntityList;
    }

    /**
     * 売上明細のセット商品展開を行う
     * 
     * <p>
     * ACCIS2023-7117 引数に料金種別特別単価フラグを追加
     * </p>
     * 
     * @param form C1002W01Form
     * @param uriageEntity S06T001TEntity
     * @param uriagemeisaiEntityList List<S06T002TEntity>
     * @param unyodate 運用日付
     * @param invoiceEnforced インボイス施行済み判定
     * @param kaishuhohokbn 回収方法区分
     * @param maxDisplayCount 明細表示最大行数
     * @param rknkindtokubetsuuntflg 料金種別特別単価フラグ
     * @return
     */
    public List<S06T002TEntity> expandUriageMeisai(C1002W01Form form, S06T001TEntity uriageEntity,
        List<S06T002TEntity> uriagemeisaiEntityList, String unyodate, boolean invoiceEnforced,
        String kaishuhohokbn, int maxDisplayCount, String rknkindtokubetsuuntflg) {

        String apkihoncd = uriageEntity.getApkihoncd();
        String shopcd = uriageEntity.getShopcd();
        String tokuisakicd = uriageEntity.getTokuisakicd();
        String uridd = uriageEntity.getUridd();
        String prcskbn = form.getBaseBean().getPrcskbn();
        String dnpykbn = form.getSlipBean().getDnpykbn();
        String dlvryhoho = form.getSlipBean().getDlvryhoho();

        // 税調整商品コード
        String zeichoseishncd = commonService.getZeichoseihncd();

        // ショップマスタ検索
        S02M046TEntity shopEntity = commonService.searchShop(apkihoncd, shopcd);

        // 下記項目に対する初期値設定判定
        // ・売上明細.在庫更新済フラグ
        boolean result1 = false;
        if (Long.valueOf(uridd).longValue() > Long.valueOf(unyodate).longValue()) {
            result1 = true;
        }

        // 明細展開後のリスト
        List<S06T002TEntity> newUriagemeisaiEntityList = new ArrayList<>();

        // セット商品の構成商品を追加するリスト
        List<S06T002TEntity> subList = new ArrayList<>();

        // 売上明細を1行ずつループ
        for (S06T002TEntity uriageMeisaiEntity : uriagemeisaiEntityList) {

            // 商品マスタを検索し、検索結果が空の場合はスキップ
            S02V001TEntity shohinEntity =
                commonService.searchShohin(apkihoncd, shopcd, uriageMeisaiEntity.getShncd());
            if (ObjectUtils.isEmpty(shohinEntity)) {
                continue;
            }

            // 在庫更新済フラグ
            if (result1) {
                uriageMeisaiEntity.setZaikokshnsumiflg(CommonConstants.FLG_OFF);
            } else {
                uriageMeisaiEntity.setZaikokshnsumiflg(CommonConstants.FLG_ON);
            }

            // 単品明細フラグが 1（単品商品）の場合はスキップ
            if (CommonConstants.FLG_ON.equals(uriageMeisaiEntity.getTanpinmeisaiflg())) {
                newUriagemeisaiEntityList.add(uriageMeisaiEntity);
                continue;
            }

            // 処理区分が「取消」の場合
            if (CommonConstants.KBN_PRCSKBN1_TORIKESI.equals(prcskbn)) {

                // 売上明細（セット商品構成商品）を取得する
                List<S06T002TC1002W01Entity> koseiUriagemeisaiEntityList =
                    mainService.searchUriageMeisai(apkihoncd, shopcd,
                        uriageMeisaiEntity.getUridnpymeisaino().toString(), maxDisplayCount);

                // 売上明細（構成商品）を1行ずつループ
                for (S06T002TC1002W01Entity koseiUriageMeisaiEntity : koseiUriagemeisaiEntityList) {

                    // 売上伝票明細枝番号が 0 の場合はスキップする
                    if (koseiUriageMeisaiEntity.getUridnpymeisaiedano() == 0) {
                        continue;
                    }

                    // 在庫更新済フラグ
                    if (result1) {
                        koseiUriageMeisaiEntity.setZaikokshnsumiflg(CommonConstants.FLG_OFF);
                    } else {
                        koseiUriageMeisaiEntity.setZaikokshnsumiflg(CommonConstants.FLG_ON);
                    }

                    // 構成商品用のサブリストに追加
                    subList.add(convertUriageMeisaiEntity(koseiUriageMeisaiEntity));
                }

                newUriagemeisaiEntityList.add(uriageMeisaiEntity);

                continue;
            }

            // 以降、処理区分が「新規」の場合の処理

            BigDecimal sumTaxkin = new BigDecimal(0);
            long stuntshohitaxkin = 0;
            long stshohitaxkin = 0;

            // セット商品コード
            uriageMeisaiEntity.setStshncd(uriageMeisaiEntity.getShncd());

            // 税区分
            if ((CommonConstants.KBN_TAX_KOMEISAI.equals(uriageMeisaiEntity.getTaxkbn()))
                && "20190930".compareTo(uriageEntity.getUridd()) >= 0) {
                uriageMeisaiEntity.setTaxkbn(CommonConstants.KBN_TAX_KAZEI);
            }

            newUriagemeisaiEntityList.add(uriageMeisaiEntity);

            // セット商品構成商品一検索
            List<S02M008TSetShohinEntity> setShohinEntityList =
                s02m008mDao.getSetShohinList(apkihoncd, shopcd, uriageMeisaiEntity.getShncd());

            // セット商品構成商品を1行ずつループ
            int uridnpymeisaiedano = 1;
            for (S02M008TSetShohinEntity koseiShohinEntity : setShohinEntityList) {

                // 親明細をベースとする
                S06T002TEntity koseiUriageMeisaiEntity = copyEntity(uriageMeisaiEntity);
                koseiUriageMeisaiEntity.setUridnpymeisaiedano((short) uridnpymeisaiedano);
                koseiUriageMeisaiEntity.setShncd(koseiShohinEntity.getKoseishncd());
                koseiUriageMeisaiEntity.setKatasiki("");
                koseiUriageMeisaiEntity
                    .setSuryo(uriageMeisaiEntity.getSuryo().multiply(koseiShohinEntity.getSuryo()));

                koseiUriageMeisaiEntity.setSiiresuryo(koseiUriageMeisaiEntity.getSuryo());

                S02V001TEntity koseiShohin = commonService.searchShohin(apkihoncd, shopcd,
                    koseiShohinEntity.getKoseishncd());
                // 商品分類コードは構成商品の値を設定する
                koseiUriageMeisaiEntity.setLctgrycd(koseiShohin.getLctgrycd());
                koseiUriageMeisaiEntity.setMctgrycd(koseiShohin.getMctgrycd());
                koseiUriageMeisaiEntity.setSctgrycd(koseiShohin.getSctgrycd());
                koseiUriageMeisaiEntity
                    .setHyojununt(commonService.getCurrentPrice(koseiShohin, dlvryhoho, uridd));

                boolean untZeroFlg = true;

                String keijokbn = koseiUriageMeisaiEntity.getKeijokbn();
                if (CommonConstants.KBN_KEIJO_URIAGE.equals(keijokbn)
                    || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(keijokbn)) {
                    // 親が有償の場合、構成商品の計上区分を使用
                    koseiUriageMeisaiEntity.setKeijokbn(koseiShohinEntity.getKeijokbn());
                }
                if (!(CommonConstants.KBN_KEIJO_URIAGE.equals(keijokbn)
                    || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(keijokbn)
                    || CommonConstants.KBN_KEIJO_KEIHI.equals(keijokbn))) {
                    koseiUriageMeisaiEntity.setUnt(BigDecimal.ZERO);
                } else {
                    untZeroFlg = false;
                }

                koseiUriageMeisaiEntity.setNyuryokumeisaiflg(CommonConstants.FLG_OFF);
                koseiUriageMeisaiEntity.setTanpinmeisaiflg(CommonConstants.FLG_ON);

                // 単価取得
                BigDecimal unt = (CommonConstants.KBN_DLVRY_HOHO_HAITATSU.equals(dlvryhoho))
                    ? koseiShohinEntity.getDlvryprice()
                    : koseiShohinEntity.getMochikaeriprice();

                // ACCIS2023-7117 料金種別特別単価フラグの値が1（ON）の場合のみ特別単価の取得や設定を行う
                if (CommonConstants.FLG_ON.equals(rknkindtokubetsuuntflg)) {
                    S02M066TEntityKey key = new S02M066TEntityKey();
                    key.setApkihoncd(apkihoncd);
                    key.setShopcd(shopcd);
                    key.setTokuisakicd(tokuisakicd);
                    key.setStshncd(koseiShohinEntity.getStshncd());

                    // 得意先別セット商品単価を取得する。
                    List<S02M066TSetShohinTankaEntity> stShohinTankaEntityList = s02m066tDao
                        .getSetShohinTankaList(key, uridd, koseiShohinEntity.getRenban());

                    // 得意先別セット商品単価が取得出来た場合、
                    // 単価に設定する
                    if (stShohinTankaEntityList.size() > 0) {
                        S02M066TSetShohinTankaEntity stShohinTankaEntity =
                            stShohinTankaEntityList.get(0);

                        BigDecimal tokuisakisetunt =
                            (CommonConstants.KBN_DLVRY_HOHO_HAITATSU.equals(dlvryhoho))
                                ? stShohinTankaEntity.getDlvryprice()
                                : stShohinTankaEntity.getMochikaeriprice();

                        if (tokuisakisetunt != null) {
                            unt = tokuisakisetunt;
                        }
                    }
                }

                if (!untZeroFlg) {
                    koseiUriageMeisaiEntity.setUnt(unt);
                }

                if (CommonConstants.KBN_TAX_KOMEISAI.equals(uriageMeisaiEntity.getTaxkbn())) {
                    koseiUriageMeisaiEntity.setTaxkbn(koseiShohinEntity.getTaxkbn());
                } else {
                    koseiUriageMeisaiEntity.setTaxkbn(uriageMeisaiEntity.getTaxkbn());
                }

                // 価格情報を取得
                String[] priceList = taxService.getPriceList(unt.toString(),
                    koseiUriageMeisaiEntity.getSuryo().toString(),
                    koseiUriageMeisaiEntity.getTaxkbn(), koseiUriageMeisaiEntity.getTokuisakikbn(),
                    uridd, shopEntity);

                if (CommonConstants.FLG_ON.equals(koseiShohinEntity.getTaxsetteikbn())
                    && (CommonConstants.KBN_TAX_HIKAZEI.equals(koseiUriageMeisaiEntity.getTaxkbn())
                        || CommonConstants.KBN_TAX_HUKAZEI
                            .equals(koseiUriageMeisaiEntity.getTaxkbn()))) {
                    priceList[0] = "0";
                    priceList[1] = "0";
                }

                if (!(CommonConstants.KBN_KEIJO_URIAGE.equals(keijokbn)
                    || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(keijokbn)
                    || CommonConstants.KBN_KEIJO_KEIHI.equals(keijokbn))) {
                    koseiUriageMeisaiEntity.setShohitaxkin(Long.parseLong("0"));
                    koseiUriageMeisaiEntity.setKin(Long.parseLong("0"));
                } else {
                    if (CommonConstants.KBN_KEIHI_KONYU.equals(dnpykbn)
                        || CommonConstants.KBN_KEIHI_NOMI.equals(dnpykbn)
                        || CommonConstants.KBN_KEIHI_SYUKKO.equals(dnpykbn)) {
                        koseiUriageMeisaiEntity.setUntshohitaxkin(Long.parseLong("0"));
                        koseiUriageMeisaiEntity.setShohitaxkin(Long.parseLong("0"));
                    } else {
                        koseiUriageMeisaiEntity.setUntshohitaxkin(Long.parseLong(priceList[0]));
                        koseiUriageMeisaiEntity.setShohitaxkin(Long.parseLong(priceList[1]));
                        koseiUriageMeisaiEntity.setKin(Long.parseLong(priceList[2]));
                    }
                }

                // インボイス施行済み、または代引きの場合
                // 単価消費税金額、消費税金額、参考消費税金額を0に設定し、
                // 税金の再計算処理をスキップする
                if (invoiceEnforced
                    || CommonConstants.KBN_KAISHUHOHO_DAIBIKI.equals(kaishuhohokbn)) {
                    koseiUriageMeisaiEntity.setUntshohitaxkin(Long.parseLong("0"));
                    koseiUriageMeisaiEntity.setShohitaxkin(Long.parseLong("0"));
                    koseiUriageMeisaiEntity.setSankoushohitaxkin(Long.parseLong("0"));

                    subList.add(koseiUriageMeisaiEntity);
                    uridnpymeisaiedano++;

                    continue;
                }

                subList.add(koseiUriageMeisaiEntity);

                BigDecimal taxKingaku = new BigDecimal(koseiUriageMeisaiEntity.getShohitaxkin());
                sumTaxkin = sumTaxkin.add(taxKingaku);

                // セット商品の税区分が5：子明細の場合はセット商品の税金を再計算する
                if (CommonConstants.KBN_TAX_KOMEISAI.equals(uriageMeisaiEntity.getTaxkbn())) {

                    // 構成商品_売上数量
                    BigDecimal suryo = koseiUriageMeisaiEntity.getSuryo();

                    // 構成商品_売上単価消費税金額
                    BigDecimal untshohitaxkin =
                        new BigDecimal(koseiUriageMeisaiEntity.getUntshohitaxkin());

                    // 構成商品_売上消費税金額
                    BigDecimal shohiTaxkin = new BigDecimal(uriageMeisaiEntity.getShohitaxkin());

                    // セット商品の売上単価消費税金額を算出する
                    stuntshohitaxkin += untshohitaxkin.multiply(suryo).longValue();

                    // セット商品の売上消費税金額を算出する。
                    stshohitaxkin += shohiTaxkin.longValue();

                    // 子明細の場合
                    if (setShohinEntityList.size() == uridnpymeisaiedano) {
                        // セット商品の売上単価消費税金額を設定
                        koseiUriageMeisaiEntity.setUntshohitaxkin(stuntshohitaxkin);
                        // セット商品の売上消費税金額を設定
                        koseiUriageMeisaiEntity.setShohitaxkin(stshohitaxkin);
                    }
                }

                uridnpymeisaiedano++;
            }

            // インボイス施行済み、または代引きの場合
            // 消費税調整は行わない
            if (invoiceEnforced || CommonConstants.KBN_KAISHUHOHO_DAIBIKI.equals(kaishuhohokbn)) {
                continue;
            }

            // 構成商品が取得できなかった場合
            // 消費税調整は行わない
            if ((setShohinEntityList.size() == 0)) {
                continue;
            }

            // 計上区分が「売上」「キャンペーン」「経費」のいずれかでもない場合
            // 消費税調整は行わない
            if (!(CommonConstants.KBN_KEIJO_URIAGE.equals(uriageMeisaiEntity.getKeijokbn())
                || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(uriageMeisaiEntity.getKeijokbn())
                || CommonConstants.KBN_KEIJO_KEIHI.equals(uriageMeisaiEntity.getKeijokbn()))) {
                continue;
            }

            // 消費税金額に差異がない場合
            // 消費税調整は行わない
            BigDecimal taxkin = new BigDecimal(uriageMeisaiEntity.getShohitaxkin());
            if (taxkin.compareTo(sumTaxkin) == 0) {
                continue;
            }

            BigDecimal addTaxkin = taxkin.subtract(sumTaxkin);
            S06T002TEntity lastKouseiUriageMeisaiEntity = subList.get(subList.size() - 1);
            S06T002TEntity zeiChouseiMeisaiEntity = new S06T002TEntity();

            // 商品マスタ検索（税調整）
            S02V001TEntity zeiChouseiShohinEntity =
                commonService.searchShohin(apkihoncd, shopcd, zeichoseishncd);

            // ベースを親の明細とする
            zeiChouseiMeisaiEntity = copyEntity(uriageMeisaiEntity);

            zeiChouseiMeisaiEntity.setUridnpymeisaiedano(
                (short) (lastKouseiUriageMeisaiEntity.getUridnpymeisaiedano() + 1));

            zeiChouseiMeisaiEntity.setLctgrycd(zeiChouseiShohinEntity.getLctgrycd());
            zeiChouseiMeisaiEntity.setMctgrycd(zeiChouseiShohinEntity.getMctgrycd());
            zeiChouseiMeisaiEntity.setSctgrycd(zeiChouseiShohinEntity.getSctgrycd());

            zeiChouseiMeisaiEntity.setStshncd(zeiChouseiMeisaiEntity.getShncd());
            zeiChouseiMeisaiEntity.setShncd(zeichoseishncd);

            zeiChouseiMeisaiEntity.setKatasiki(null);
            zeiChouseiMeisaiEntity.setSuryo(BigDecimal.ONE);

            // 標準単価のセット
            BigDecimal zeiChouseiHyojununt = BigDecimal.ZERO;
            if (CommonConstants.KBN_DLVRY_HOHO_HAITATSU.equals(dlvryhoho)) {
                // 配達方法が「配達」の場合
                // 標準価格を判定して設定
                zeiChouseiHyojununt = new BigDecimal(
                    ItemUtil.getItem(zeiChouseiShohinEntity.getGenzhyojunprice().toString(),
                        zeiChouseiShohinEntity.getShinhyojunprice().toString(),
                        zeiChouseiShohinEntity.getHyojunpricekirikaedd(), uridd));
            } else {
                // 配達方法が「持ち帰り」の場合
                // 持ち帰り価格を判定して設定
                zeiChouseiHyojununt = new BigDecimal(
                    ItemUtil.getItem(zeiChouseiShohinEntity.getGenzmochikaeriprice().toString(),
                        zeiChouseiShohinEntity.getShinmochikaeriprice().toString(),
                        zeiChouseiShohinEntity.getHyojunpricekirikaedd(), uridd));
            }
            zeiChouseiMeisaiEntity.setHyojununt(zeiChouseiHyojununt);

            zeiChouseiMeisaiEntity.setMeisaikbn(CommonConstants.KBN_MEISAI_SHOHIZEI);
            zeiChouseiMeisaiEntity.setNyuryokumeisaiflg(CommonConstants.FLG_OFF);
            zeiChouseiMeisaiEntity.setTanpinmeisaiflg(CommonConstants.FLG_ON);

            zeiChouseiMeisaiEntity.setUnt(BigDecimal.ZERO);
            zeiChouseiMeisaiEntity.setKin(Long.parseLong("0"));
            zeiChouseiMeisaiEntity.setUntshohitaxkin(Long.parseLong("0"));
            zeiChouseiMeisaiEntity.setShohitaxkin(addTaxkin.longValue());

            subList.add(zeiChouseiMeisaiEntity);
        }

        newUriagemeisaiEntityList.addAll(subList);

        return newUriagemeisaiEntityList;
    }

    /**
     * 消費税情報登録（売上明細テーブル）
     * 
     * @param s06t001tEntity S06T001TEntity
     * @param meisaiEntityList List<S06T002TEntity>
     * @param s02m013tEntity S02M013TEntity
     * @return int 課税料金合計と軽減税料金合計の和
     */
    public int insertTax(S06T001TEntity s06t001tEntity, List<S06T002TEntity> meisaiEntityList,
        S02M013TEntity s02m013tEntity, DbManagementSession dbManagementSession) {

        // 課税料金合計
        long kazeiKinSum1 = 0;

        // 軽減税料金合計
        long keigenKinSum1 = 0;

        // 課税料金合計（消費税計算後）
        int kazeiKinSum2 = 0;

        // 軽減税料金合計（消費税計算後）
        int keigenKinSum2 = 0;

        for (S06T002TEntity meisaiEntity : meisaiEntityList) {
            String taxkbn = meisaiEntity.getTaxkbn();

            // 入力行のみ処理する
            if (!CommonConstants.FLG_ON.equals(meisaiEntity.getNyuryokumeisaiflg())) {
                continue;
            }

            // 税区分が「課税」の場合
            if (CommonConstants.KBN_TAX_KAZEI.equals(taxkbn)) {
                kazeiKinSum1 += meisaiEntity.getKin();
                continue;
            }

            // 税区分が「課税軽減（8%）の場合」
            if (CommonConstants.KBN_TAX_KEIGEN.equals(taxkbn)) {
                keigenKinSum1 += meisaiEntity.getKin();
                continue;
            }
        }

        // 税端数計算区分
        S02M046TEntity s02m046tEntity =
            commonService.searchShop(s06t001tEntity.getApkihoncd(), s06t001tEntity.getShopcd());
        String taxhasukeisankbn = s02m046tEntity.getTaxhasukeisankbn();

        int nextUridnpymeisaino = getMaxUridnpymeisaino(meisaiEntityList) + 1;

        // 課税料金合計が 0 ではない場合
        if (kazeiKinSum1 != 0) {
            BigDecimal shohiTaxRate = commonService.getShohiTaxRate(CommonConstants.KBN_TAX_KAZEI,
                s06t001tEntity.getUridd());

            // 課税料金合計の消費税計算
            kazeiKinSum2 = commonService.callRoundValue(taxhasukeisankbn, shohiTaxRate.toString(),
                (int) kazeiKinSum1);

            // 売上明細テーブルに明細（課税）を追加
            insertTaxMeisai(s06t001tEntity, s02m013tEntity, dbManagementSession, kazeiKinSum2,
                nextUridnpymeisaino);

            nextUridnpymeisaino++;
        }

        // 軽減税料金合計が 0 ではない場合
        if (keigenKinSum1 != 0) {
            BigDecimal keigenShohiTaxRate = commonService
                .getShohiTaxRate(CommonConstants.KBN_TAX_KEIGEN, s06t001tEntity.getUridd());

            // 軽減税料金合計の消費税計算
            keigenKinSum2 = commonService.callRoundValue(taxhasukeisankbn,
                keigenShohiTaxRate.toString(), (int) keigenKinSum1);

            // 売上明細テーブルに明細（軽減税）を追加
            insertKeigenTaxMeisai(s06t001tEntity, s02m013tEntity, dbManagementSession,
                keigenKinSum2, nextUridnpymeisaino);
        }

        // 課税料金合計と軽減税料金合計の和を返却する
        return kazeiKinSum2 + keigenKinSum2;
    }

    /**
     * 消費税情報を売上明細テーブルに登録する
     * 
     * @param s06t001tEntity S06T001TEntity
     * @param s02m013tEntity S02M013TEntity
     * @param dbManagementSession DbManagementSession
     * @param kazeiKinSum 課税料金合計
     * @param uridnpymeisaino 売上伝票明細番号
     */
    private void insertTaxMeisai(S06T001TEntity s06t001tEntity, S02M013TEntity s02m013tEntity,
        DbManagementSession dbManagementSession, int kazeiKinSum, int uridnpymeisaino) {
        S06T002TEntity taxMeisaiEntity = new S06T002TEntity();
        taxMeisaiEntity.setApkihoncd(ItemUtil.nullToString(s06t001tEntity.getApkihoncd()));
        taxMeisaiEntity.setShopcd(ItemUtil.nullToString(s06t001tEntity.getShopcd()));
        taxMeisaiEntity.setUridnpyno(ItemUtil.nullToString(s06t001tEntity.getUridnpyno()));
        taxMeisaiEntity.setEdano(Short.parseShort("0"));
        taxMeisaiEntity.setUridnpymeisaino(uridnpymeisaino);
        taxMeisaiEntity.setUridnpymeisaiedano(Short.parseShort("0"));
        taxMeisaiEntity.setLctgrycd(CommonConstants.LCTGRYCD_99);
        taxMeisaiEntity.setMctgrycd(CommonConstants.MCTGRYCD_001);
        taxMeisaiEntity.setSctgrycd(CommonConstants.SCTGRYCD_001);
        taxMeisaiEntity.setShncd(CommonConstants.SHNCD_099901);
        taxMeisaiEntity.setKeijokbn(CommonConstants.KBN_KEIJO_URIAGE);
        taxMeisaiEntity.setSuryo(new BigDecimal(1));
        taxMeisaiEntity.setHyojununt(new BigDecimal(0));
        taxMeisaiEntity.setUnt(new BigDecimal(0));
        taxMeisaiEntity.setKin(Long.parseLong("0"));
        taxMeisaiEntity.setUntshohitaxkin(Long.parseLong("0"));
        taxMeisaiEntity.setShohitaxkin((long) kazeiKinSum);
        taxMeisaiEntity.setSankoushohitaxkin(Long.parseLong("0"));
        taxMeisaiEntity.setTaxkbn(CommonConstants.KBN_TAX_KAZEI);
        taxMeisaiEntity.setMeisaikbn(CommonConstants.KBN_MEISAI_SHOHIN);
        taxMeisaiEntity.setNyuryokumeisaiflg(CommonConstants.FLG_ON);
        taxMeisaiEntity.setTanpinmeisaiflg(CommonConstants.FLG_ON);
        taxMeisaiEntity.setMeisaibiko("");
        taxMeisaiEntity.setStshncd("");
        taxMeisaiEntity.setKatasiki("");
        taxMeisaiEntity.setSiiresuryo(new BigDecimal(0));
        taxMeisaiEntity.setSiirekin(Long.parseLong("0"));
        taxMeisaiEntity.setSiireunt(new BigDecimal(0));
        taxMeisaiEntity.setSiireshohitaxkin(Long.parseLong("0"));
        taxMeisaiEntity.setZaikokshnsumiflg("");
        taxMeisaiEntity.setTokuisakikbn(s02m013tEntity.getTokuisakikbn());
        taxMeisaiEntity.setJutakudlvrycstkbn(s02m013tEntity.getJutakudlvrycstkbn());
        taxMeisaiEntity.setRoyaltyshimeymd(CommonConstants.DEF_DATE_00000000);
        taxMeisaiEntity.setYukokbn(CommonConstants.YUKOKBN_1);
        taxMeisaiEntity
            .setToshaapkihoncd(ItemUtil.nullToString(dbManagementSession.getApkihoncd()));
        taxMeisaiEntity.setToshashopcd(ItemUtil.nullToString(dbManagementSession.getShopcd()));
        taxMeisaiEntity.setToshacd(ItemUtil.nullToString(dbManagementSession.getTnshacd()));
        taxMeisaiEntity.setInptsystmymd(ItemUtil.nullToString(dbManagementSession.getSystemDate()));
        taxMeisaiEntity
            .setInptsystmjikan(ItemUtil.nullToString(dbManagementSession.getSystemTime()));
        taxMeisaiEntity.setInptunyoymd(ItemUtil.nullToString(dbManagementSession.getUnyoDate()));
        taxMeisaiEntity.setInptipadrse(ItemUtil.nullToString(dbManagementSession.getIpAddress()));
        taxMeisaiEntity.setInptpgmid(ItemUtil.nullToString(dbManagementSession.getProgramId()));
        taxMeisaiEntity
            .setKoshaapkihoncd(ItemUtil.nullToString(dbManagementSession.getApkihoncd()));
        taxMeisaiEntity.setKoshashopcd(ItemUtil.nullToString(dbManagementSession.getShopcd()));
        taxMeisaiEntity.setKoshacd(ItemUtil.nullToString(dbManagementSession.getTnshacd()));
        taxMeisaiEntity.setKshnsystmymd(ItemUtil.nullToString(dbManagementSession.getSystemDate()));
        taxMeisaiEntity
            .setKshnsystmjikan(ItemUtil.nullToString(dbManagementSession.getSystemTime()));
        taxMeisaiEntity.setKshnunyoymd(ItemUtil.nullToString(dbManagementSession.getUnyoDate()));
        taxMeisaiEntity.setKshnipadrse(ItemUtil.nullToString(dbManagementSession.getIpAddress()));
        taxMeisaiEntity.setKshnpgmid(ItemUtil.nullToString(dbManagementSession.getProgramId()));

        try {
            s06t002tDao.insert(taxMeisaiEntity);
        } catch (DuplicateKeyException e) {
            throw BusinessExceptionFactory.validationFailed(MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(MessageID.E002);
        }
    }

    /**
     * 消費税情報（軽減税率（8%））を売上明細テーブルに登録する
     * 
     * @param s06t001tEntity S06T001TEntity
     * @param s02m013tEntity S02M013TEntity
     * @param dbManagementSession DbManagementSession
     * @param keigenKinSum 軽減税料金合計
     * @param uridnpymeisaino 売上伝票明細番号
     */
    private void insertKeigenTaxMeisai(S06T001TEntity s06t001tEntity, S02M013TEntity s02m013tEntity,
        DbManagementSession dbManagementSession, int keigenKinSum, int uridnpymeisaino) {
        S06T002TEntity taxMeisaiEntity = new S06T002TEntity();
        taxMeisaiEntity.setApkihoncd(s06t001tEntity.getApkihoncd());
        taxMeisaiEntity.setShopcd(s06t001tEntity.getShopcd());
        taxMeisaiEntity.setUridnpyno(s06t001tEntity.getUridnpyno());
        taxMeisaiEntity.setEdano(Short.parseShort("0"));
        taxMeisaiEntity.setUridnpymeisaino(uridnpymeisaino);
        taxMeisaiEntity.setUridnpymeisaiedano(Short.parseShort("0"));
        taxMeisaiEntity.setLctgrycd(CommonConstants.LCTGRYCD_99);
        taxMeisaiEntity.setMctgrycd(CommonConstants.MCTGRYCD_001);
        taxMeisaiEntity.setSctgrycd(CommonConstants.SCTGRYCD_002);
        taxMeisaiEntity.setShncd(CommonConstants.SHNCD_099902);
        taxMeisaiEntity.setKeijokbn(CommonConstants.KBN_KEIJO_URIAGE);
        taxMeisaiEntity.setSuryo(new BigDecimal(1));
        taxMeisaiEntity.setHyojununt(new BigDecimal(0));
        taxMeisaiEntity.setUnt(new BigDecimal(0));
        taxMeisaiEntity.setKin(Long.parseLong("0"));
        taxMeisaiEntity.setUntshohitaxkin(Long.parseLong("0"));
        taxMeisaiEntity.setShohitaxkin((long) keigenKinSum);
        taxMeisaiEntity.setSankoushohitaxkin(Long.parseLong("0"));
        taxMeisaiEntity.setTaxkbn(CommonConstants.KBN_TAX_KEIGEN);
        taxMeisaiEntity.setMeisaikbn(CommonConstants.KBN_MEISAI_SHOHIN);
        taxMeisaiEntity.setNyuryokumeisaiflg(CommonConstants.FLG_ON);
        taxMeisaiEntity.setTanpinmeisaiflg(CommonConstants.FLG_ON);
        taxMeisaiEntity.setMeisaibiko("");
        taxMeisaiEntity.setStshncd("");
        taxMeisaiEntity.setKatasiki("");
        taxMeisaiEntity.setSiiresuryo(new BigDecimal(0));
        taxMeisaiEntity.setSiirekin(Long.parseLong("0"));
        taxMeisaiEntity.setSiireunt(new BigDecimal(0));
        taxMeisaiEntity.setSiireshohitaxkin(Long.parseLong("0"));
        taxMeisaiEntity.setZaikokshnsumiflg("");
        taxMeisaiEntity.setTokuisakikbn(s02m013tEntity.getTokuisakikbn());
        taxMeisaiEntity.setJutakudlvrycstkbn(s02m013tEntity.getJutakudlvrycstkbn());
        taxMeisaiEntity.setRoyaltyshimeymd(CommonConstants.DEF_DATE_00000000);
        taxMeisaiEntity.setYukokbn(CommonConstants.YUKOKBN_1);
        taxMeisaiEntity.setToshaapkihoncd((dbManagementSession.getApkihoncd()));
        taxMeisaiEntity.setToshashopcd((dbManagementSession.getShopcd()));
        taxMeisaiEntity.setToshacd((dbManagementSession.getTnshacd()));
        taxMeisaiEntity.setInptsystmymd((dbManagementSession.getSystemDate()));
        taxMeisaiEntity.setInptsystmjikan((dbManagementSession.getSystemTime()));
        taxMeisaiEntity.setInptunyoymd((dbManagementSession.getUnyoDate()));
        taxMeisaiEntity.setInptipadrse((dbManagementSession.getIpAddress()));
        taxMeisaiEntity.setInptpgmid((dbManagementSession.getProgramId()));
        taxMeisaiEntity.setKoshaapkihoncd((dbManagementSession.getApkihoncd()));
        taxMeisaiEntity.setKoshashopcd((dbManagementSession.getShopcd()));
        taxMeisaiEntity.setKoshacd((dbManagementSession.getTnshacd()));
        taxMeisaiEntity.setKshnsystmymd((dbManagementSession.getSystemDate()));
        taxMeisaiEntity.setKshnsystmjikan((dbManagementSession.getSystemTime()));
        taxMeisaiEntity.setKshnunyoymd((dbManagementSession.getUnyoDate()));
        taxMeisaiEntity.setKshnipadrse((dbManagementSession.getIpAddress()));
        taxMeisaiEntity.setKshnpgmid((dbManagementSession.getProgramId()));

        try {
            s06t002tDao.insert(taxMeisaiEntity);
        } catch (DuplicateKeyException e) {
            throw BusinessExceptionFactory.validationFailed(MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(MessageID.E002);
        }
    }

    /**
     * 売上明細Entityを変換する
     * 
     * @param S06T002TC1002W01Entity
     * @return S06T002TEntity
     */
    private S06T002TEntity convertUriageMeisaiEntity(S06T002TC1002W01Entity entity) {
        S06T002TEntity uriageMeisaiEntity = new S06T002TEntity();
        uriageMeisaiEntity.setApkihoncd(entity.getApkihoncd());
        uriageMeisaiEntity.setShopcd(entity.getShopcd());
        uriageMeisaiEntity.setUridnpyno(entity.getUridnpyno());
        uriageMeisaiEntity.setEdano(entity.getEdano());
        uriageMeisaiEntity.setUridnpymeisaino(entity.getUridnpymeisaino());
        uriageMeisaiEntity.setUridnpymeisaiedano(entity.getUridnpymeisaiedano());
        uriageMeisaiEntity.setLctgrycd(entity.getLctgrycd());
        uriageMeisaiEntity.setMctgrycd(entity.getMctgrycd());
        uriageMeisaiEntity.setSctgrycd(entity.getSctgrycd());
        uriageMeisaiEntity.setShncd(entity.getShncd());
        uriageMeisaiEntity.setKeijokbn(entity.getKeijokbn());
        uriageMeisaiEntity.setSuryo(entity.getSuryo());
        uriageMeisaiEntity.setHyojununt(entity.getHyojununt());
        uriageMeisaiEntity.setUnt(entity.getUnt());
        uriageMeisaiEntity.setKin(entity.getKin());
        uriageMeisaiEntity.setUntshohitaxkin(entity.getUntshohitaxkin());
        uriageMeisaiEntity.setShohitaxkin(entity.getShohitaxkin());
        uriageMeisaiEntity.setSankoushohitaxkin(entity.getSankoushohitaxkin());
        uriageMeisaiEntity.setTaxkbn(entity.getTaxkbn());
        uriageMeisaiEntity.setMeisaikbn(entity.getMeisaikbn());
        uriageMeisaiEntity.setNyuryokumeisaiflg(entity.getNyuryokumeisaiflg());
        uriageMeisaiEntity.setTanpinmeisaiflg(entity.getTanpinmeisaiflg());
        uriageMeisaiEntity.setMeisaibiko(entity.getMeisaibiko());
        uriageMeisaiEntity.setStshncd(entity.getStshncd());
        uriageMeisaiEntity.setKatasiki(entity.getKatasiki());
        uriageMeisaiEntity.setSiiresuryo(entity.getSiiresuryo());
        uriageMeisaiEntity.setSiirekin(entity.getSiirekin());
        uriageMeisaiEntity.setSiireunt(entity.getSiireunt());
        uriageMeisaiEntity.setSiireshohitaxkin(entity.getSiireshohitaxkin());
        uriageMeisaiEntity.setZaikokshnsumiflg(entity.getZaikokshnsumiflg());
        uriageMeisaiEntity.setTokuisakikbn(entity.getTokuisakikbn());
        uriageMeisaiEntity.setJutakudlvrycstkbn(entity.getJutakudlvrycstkbn());
        uriageMeisaiEntity.setRoyaltyshimeymd(entity.getRoyaltyshimeymd());
        uriageMeisaiEntity.setYukokbn(entity.getYukokbn());
        uriageMeisaiEntity.setToshaapkihoncd(entity.getToshaapkihoncd());
        uriageMeisaiEntity.setToshashopcd(entity.getToshashopcd());
        uriageMeisaiEntity.setToshacd(entity.getToshacd());
        uriageMeisaiEntity.setInptsystmymd(entity.getInptsystmymd());
        uriageMeisaiEntity.setInptsystmjikan(entity.getInptsystmjikan());
        uriageMeisaiEntity.setInptunyoymd(entity.getInptunyoymd());
        uriageMeisaiEntity.setInptipadrse(entity.getInptipadrse());
        uriageMeisaiEntity.setInptpgmid(entity.getInptpgmid());
        uriageMeisaiEntity.setKoshaapkihoncd(entity.getKoshaapkihoncd());
        uriageMeisaiEntity.setKoshashopcd(entity.getKoshashopcd());
        uriageMeisaiEntity.setKoshacd(entity.getKoshacd());
        uriageMeisaiEntity.setKshnsystmymd(entity.getKshnsystmymd());
        uriageMeisaiEntity.setKshnsystmjikan(entity.getKshnsystmjikan());
        uriageMeisaiEntity.setKshnunyoymd(entity.getKshnunyoymd());
        uriageMeisaiEntity.setKshnipadrse(entity.getKshnipadrse());
        uriageMeisaiEntity.setKshnpgmid(entity.getKshnpgmid());

        return uriageMeisaiEntity;
    }

    /**
     * 売上明細Entityをコピーする
     * 
     * @param S06T002TEntity
     * @return S06T002TEntity
     */
    private S06T002TEntity copyEntity(S06T002TEntity entity) {

        S06T002TEntity newEntity = new S06T002TEntity();
        newEntity.setApkihoncd(entity.getApkihoncd());
        newEntity.setShopcd(entity.getShopcd());
        newEntity.setUridnpyno(entity.getUridnpyno());
        newEntity.setEdano(entity.getEdano());
        newEntity.setUridnpymeisaino(entity.getUridnpymeisaino());
        newEntity.setUridnpymeisaiedano(entity.getUridnpymeisaiedano());
        newEntity.setLctgrycd(entity.getLctgrycd());
        newEntity.setMctgrycd(entity.getMctgrycd());
        newEntity.setSctgrycd(entity.getSctgrycd());
        newEntity.setShncd(entity.getShncd());
        newEntity.setKeijokbn(entity.getKeijokbn());
        newEntity.setSuryo(entity.getSuryo());
        newEntity.setHyojununt(entity.getHyojununt());
        newEntity.setUnt(entity.getUnt());
        newEntity.setKin(entity.getKin());
        newEntity.setUntshohitaxkin(entity.getUntshohitaxkin());
        newEntity.setShohitaxkin(entity.getShohitaxkin());
        newEntity.setSankoushohitaxkin(entity.getSankoushohitaxkin());
        newEntity.setTaxkbn(entity.getTaxkbn());
        newEntity.setMeisaikbn(entity.getMeisaikbn());
        newEntity.setNyuryokumeisaiflg(entity.getNyuryokumeisaiflg());
        newEntity.setTanpinmeisaiflg(entity.getTanpinmeisaiflg());
        newEntity.setMeisaibiko(entity.getMeisaibiko());
        newEntity.setStshncd(entity.getStshncd());
        newEntity.setKatasiki(entity.getKatasiki());
        newEntity.setSiiresuryo(entity.getSiiresuryo());
        newEntity.setSiirekin(entity.getSiirekin());
        newEntity.setSiireunt(entity.getSiireunt());
        newEntity.setSiireshohitaxkin(entity.getSiireshohitaxkin());
        newEntity.setZaikokshnsumiflg(entity.getZaikokshnsumiflg());
        newEntity.setTokuisakikbn(entity.getTokuisakikbn());
        newEntity.setJutakudlvrycstkbn(entity.getJutakudlvrycstkbn());
        newEntity.setRoyaltyshimeymd(entity.getRoyaltyshimeymd());
        newEntity.setYukokbn(entity.getYukokbn());
        newEntity.setToshaapkihoncd(entity.getToshaapkihoncd());
        newEntity.setToshashopcd(entity.getToshashopcd());
        newEntity.setToshacd(entity.getToshacd());
        newEntity.setInptsystmymd(entity.getInptsystmymd());
        newEntity.setInptsystmjikan(entity.getInptsystmjikan());
        newEntity.setInptunyoymd(entity.getInptunyoymd());
        newEntity.setInptipadrse(entity.getInptipadrse());
        newEntity.setInptpgmid(entity.getInptpgmid());
        newEntity.setKoshaapkihoncd(entity.getKoshaapkihoncd());
        newEntity.setKoshashopcd(entity.getKoshashopcd());
        newEntity.setKoshacd(entity.getKoshacd());
        newEntity.setKshnsystmymd(entity.getKshnsystmymd());
        newEntity.setKshnsystmjikan(entity.getKshnsystmjikan());
        newEntity.setKshnunyoymd(entity.getKshnunyoymd());
        newEntity.setKshnipadrse(entity.getKshnipadrse());
        newEntity.setKshnpgmid(entity.getKshnpgmid());

        return newEntity;
    }

    /**
     * 売上伝票明細番号の最大値を取得する
     * 
     * @param List<S06T002TEntity> meisaiEntityList
     * @return int 売上伝票明細番号の最大値
     */
    private int getMaxUridnpymeisaino(List<S06T002TEntity> meisaiEntityList) {
        int max = 0;
        for (S06T002TEntity meisaiEntity : meisaiEntityList) {
            if (meisaiEntity.getUridnpymeisaino() > max) {
                max = meisaiEntity.getUridnpymeisaino();
            }
        }

        return max;
    }
}
