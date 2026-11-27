package jp.co.aquaclara.accis.web.domain.service.x9901;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.constants.MessageConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M006TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M013TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T002TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04W001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.business.CommonDaoRepository;
import jp.co.aquaclara.accis.common.domain.repository.dao.business.JuchuDaoRepository;
import jp.co.aquaclara.accis.common.domain.repository.dao.business.TokuisakiDaoRepository;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M006TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M006TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M008TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M050TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04W001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.business.S02M008TEntityEx;
import jp.co.aquaclara.accis.common.domain.repository.entity.business.S02M033TEntityEx;
import jp.co.aquaclara.accis.common.domain.repository.entity.business.S02M046TEntityDaoEx;
import jp.co.aquaclara.accis.common.domain.repository.entity.business.S02V001TEntityDaoEx;
import jp.co.aquaclara.accis.common.domain.repository.entity.business.S04T001TEntityDaoEx;
import jp.co.aquaclara.accis.common.domain.repository.entity.business.S04T002TEntityDaoEx;
import jp.co.aquaclara.accis.common.domain.repository.entity.business.S04W001TEntityEx;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.FileShinkiJuchuVO;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.JuchuDenpyoVO;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.JuchuMeisaiVO;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.JuchuRelationVo;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.JuchuSumVo;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.JuchuTeikiVo;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.PriceInfoVo;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.TokuisakiRelationVo;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.TokuisakiTokubetsuUntVo;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.ZaikoKeyVo;
import jp.co.aquaclara.accis.common.exception.BusinessException;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.business.BusinessUtil;
import jp.co.aquaclara.accis.common.util.business.message.Message;
import jp.co.aquaclara.accis.common.util.business.message.MessageList;
import jp.co.aquaclara.accis.common.util.business.message.MessageService;
import jp.co.aquaclara.accis.web.controller.commons.DbManagementSession;
import jp.co.aquaclara.accis.web.domain.service.business.SaibanService;

/**
 * 受注サービス業務ロジックを記述するクラス。
 * 
 * @author Isac)Katayama
 * @version 1.0.0 2007-05-06
 */
@Service
public class JuchuService {

    /**
     * 新規受注明細行タイプ：前回受注伝票より
     */
    public static final int TYPE_ADDLINE_S04T002T = 1;
    /**
     * 新規受注明細行タイプ：システム管理マスタより
     */
    public static final int TYPE_ADDLINE_S02M006T = 2;
    /**
     * 新規受注明細行タイプ：お気に入り商品マスタより
     */
    public static final int TYPE_ADDLINE_S02M044T = 3;
    // ■:1 TJS)Aoi 2009-03-30
    // （一桁目をタイプ、二桁目をお気に入り商品マスタの表示区分とする）
    /**
     * 新規受注明細行タイプ：お気に入り商品マスタ（商品受注）より
     */
    public static final int TYPE_ADDLINE_S02M044T_SHNJUCHU = 32;
    /**
     * 新規受注明細行タイプ：お気に入り商品マスタ（設置）より
     */
    public static final int TYPE_ADDLINE_S02M044T_SETTI = 33;
    /**
     * 新規受注明細行タイプ：お気に入り商品マスタ（交換）より
     */
    public static final int TYPE_ADDLINE_S02M044T_KOUKAN = 34;
    /**
     * 新規受注明細行タイプ：お気に入り商品マスタ（引き上げ）より
     */
    public static final int TYPE_ADDLINE_S02M044T_HIKIAGE = 35;
    /**
     * 新規受注明細行タイプ：お気に入り商品マスタ（メンテナンス）より
     */
    public static final int TYPE_ADDLINE_S02M044T_MENTE = 36;
    // ■:2 TJS)Aoi 2009-03-30
    /**
     * 新規受注明細行タイプ：商品マスタビューより
     */
    public static final int TYPE_ADDLINE_S02V001T = 4;
    /**
     * 新規受注明細行タイプ：定期受注明細マスタより
     */
    public static final int TYPE_ADDLINE_S02M049T = 5;
    // ■:1 tjs)nakano 2009-03-06
    /**
     * 新規受注明細行タイプ：特定定期受注明細マスタより
     */
    public static final int TYPE_ADDLINE_S02M056T = 7;
    // ■:2 tjs)nakano 2009-03-06
    /**
     * 新規受注明細行タイプ：新規受注取込ワークより
     */
    public static final int TYPE_ADDLINE_S04W001T = 6;
    /**
     * 処理タイプ：新規追加
     */
    public static final int TYPE_UPATE_INSERT = 1;
    /**
     * 処理タイプ：予約受注新規追加
     */
    public static final int TYPE_UPATE_YOYAKUINSERT = 2;
    /**
     * 処理タイプ：更新
     */
    public static final int TYPE_UPATE_UPDATE = 3;
    /**
     * 処理タイプ：キャンセル
     */
    public static final int TYPE_UPATE_CANCEL = 4;
    /**
     * 処理タイプ：削除
     */
    public static final int TYPE_UPATE_DELETE = 5;

    /** QuiQproのログ出力クラスです。 */
    // private static final Logger LOGGER = LoggerFactory.getLogger(S01JuchuService.class);
    // ■:1 Base)Arai 2013-09-20
    // private static final Logger LOGGER_JUTYUYUKO =
    // LoggerFactory.getLogger("log.output.jutyuyuko.S01JuchuService");
    // ■:1 Base)Arai 2013-09-20

    @Autowired
    private S04W001TDao s04w001tDao;

    @Autowired
    private S02M006TDao s02m006tDao;

    @Autowired
    private S02M013TDao s02m013tDao;

    @Autowired
    private S04T002TDao s04t002tDao;

    @Autowired
    private S04T001TDao s04t001tDao;

    @Autowired
    private CommonDaoRepository commonDaoRepository;

    @Autowired
    JuchuDaoRepository juchuDaoRepository;

    @Autowired
    private TokuisakiDaoRepository tokuisakiDaoRepository;

    @Autowired
    CommonService commonService;

    @Autowired
    private ShohinService shohinService;

    @Autowired
    private ZaikoService zaikoService;

    @Autowired
    private TokuisakiService tokuisakiService;

    @Autowired
    private SaibanService saibanService;

    @Autowired
    private HaifubutsuService haifubutsuService;

    @Autowired
    private PriceService priceService;

    @Autowired
    FileUploadService fileUploadService;

    @Autowired
    private KouseihinService kouseihinService;

    // 2017-05-12 FSW V15移行対応 add start
    // private LanguageInfo lang;
    // 2017-05-12 FSW V15移行対応 add end

    /**
     * コンストラクタ
     * 
     * @param tran トランザクション
     */
    public JuchuService() {}


    public CommonDaoRepository getCommonDao() {
        return commonDaoRepository;
    }
    // ■:2 tjs)nakano 2009-02-10



    /**
     * S01JuchuDaoを取得します。
     * 
     * @return S01JuchuDao
     * @throws SystemException システム例外
     */
    public JuchuDaoRepository getJuchuDao() {
        return juchuDaoRepository;
    }

    /**
     * S04t001tEntityDaoを取得します。
     * 
     * @return S04t001tEntityDao
     * @throws SystemException システム例外
     */

    public S04T001TDao getS04t001tEntityDao() {
        return s04t001tDao;
    }

    /**
     * S04t002tEntityDaoを取得します。
     * 
     * @return S04t002tEntityDao
     * @throws SystemException システム例外
     */
    public S04T002TDao getS04t002tEntityDao() {
        return s04t002tDao;
    }

    /**
     * S04w001tEntityDaoを取得します。
     * 
     * @return S04w001tEntityDao
     * @throws SystemException システム例外
     */
    private S04W001TDao getS04w001tEntityDao() {
        return s04w001tDao;
    }

    // ■:1 Isac)Shiba 2020-12-16 価格改定対応
    /**
     * S01TokuisakiDaoを取得します。
     * 
     * @return S01JTokuisakiDao
     * @throws SystemException システム例外
     */
    public TokuisakiDaoRepository getTokuisakiDao() {
        return tokuisakiDaoRepository;
    }
    // ■:2 Isac)Shiba 2020-12-16 価格改定対応

    /**
     * 受注伝票情報を取得します．
     * 
     * @param apKihoncd AP基本コード
     * @param shopcd ショップコード
     * @param acptdnpyno 受注伝票番号
     * @param edano 枝番号
     * @return S01JuchuDenpyoVO 結果
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public JuchuDenpyoVO getJuchuDenpyo(String apKihoncd, String shopcd, String acptdnpyno,
        String edano) {

        try {
            // 受注配達VO生成
            JuchuDenpyoVO vo = new JuchuDenpyoVO();
            vo.setApkihoncd(apKihoncd);
            vo.setShopcd(shopcd);
            vo.setAcptdnpyno(acptdnpyno);
            vo.setEdano(Short.valueOf(edano));

            // 検索実行
            return getJuchuDao().getJuchuDenpyo(vo, false);

        } catch (Exception e) {
            // S01Util.debugLogOut(LOGGER, "受注伝票情報を取得できませんでした。");
            throw new BusinessException("受注伝票情報を取得できませんでした。", e);
        } finally {

        }
    }

    /**
     * 受注予約伝票情報を取得します．
     * 
     * @param apKihonCd AP基本コード
     * @param shopCd ショップコード
     * @param tokuisakicd 得意先コード
     * @param targetDate 取得対象日付
     * @return S01JuchuDenpyoVO 結果
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public JuchuDenpyoVO getJuchuDenpyoYoyaku(String apKihonCd, String shopCd, String tokuisakicd,
        String targetDate) {

        try {
            // 受注配達VO生成
            JuchuDenpyoVO vo = new JuchuDenpyoVO();
            vo.setApkihoncd(apKihonCd);
            vo.setShopcd(shopCd);
            vo.setTokuisakicd(tokuisakicd);
            vo.setDlvryytidd(targetDate);

            // 検索実行
            return getJuchuDao().getJuchuDenpyo(vo, true);

        } catch (Exception e) {
            // S01Util.debugLogOut(LOGGER, "前回受注伝票を取得できませんでした。");
            throw new BusinessException("前回受注伝票を取得できませんでした。", e);
        } finally {

        }
    }

    /**
     * 受注明細情報を取得します．
     * 
     * @param priceSetFg 単価セットフラグ
     * @param denVo 受注伝票ＶＯ
     * @return List 結果のリスト
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public List<JuchuMeisaiVO> getJuchuMeisai(boolean priceSetFg, JuchuDenpyoVO denVo) {

        try {
            // 検索実行
            // ■:1 Isac)Shiba 2021-01-20 価格改定対応
            List<JuchuMeisaiVO> meisaiList =
                getJuchuDao().getJuchuMeisai(denVo, denVo.getDlvryytidd());
            // ■:2 Isac)Shiba 2021-01-20 価格改定対応

            // 単価の再設定を行う
            reEditUnt(meisaiList, priceSetFg, denVo);
            // ■:2 Isac)Shiba 2021-02-21 価格改定対応

            // ■:1 Isac)Shiba 2020-12-16 価格改定対応
            S02M013TEntityKey key = new S02M013TEntityKey();
            key.setApkihoncd(denVo.getApkihoncd());
            key.setShopcd(denVo.getShopcd());
            key.setTokuisakicd(denVo.getTokuisakicd());
            S02M013TEntity s02m013 = s02m013tDao.selectByPrimaryKey(key);
            TokuisakiDaoRepository tokuisakiDao = this.getTokuisakiDao();
            // ■:2 Isac)Shiba 2020-12-16 価格改定対応

            // 単価の再設定を行う
            for (int i = 0; i < meisaiList.size(); i++) {
                JuchuMeisaiVO meiVo = (JuchuMeisaiVO) meisaiList.get(i);

                // 商品・入力行のみ
                if (CommonConstants.FLG_ON.equals(meiVo.getNyuryokumeisaiflg())
                    && CommonConstants.KBN_MEISAI_SHOHIN.equals(meiVo.getMeisaikbn())) {

                    // ■:1 Isac)Shiba 2020-12-16 価格改定対応（JavaScriptで日付変更時に特別単価を再設定できるように特別単価情報を取得する）
                    if (CommonConstants.FLG_ON.equals(s02m013.getRknkindtokubetsuuntflg())) {
                        List<TokuisakiTokubetsuUntVo> tokubetsuUntList;
                        if (CommonConstants.FLG_ON.equals(meiVo.getTanpinmeisaiflg())) {
                            tokubetsuUntList = tokuisakiDao.getShnTokubetsuUnt(denVo.getApkihoncd(),
                                denVo.getShopcd(), meiVo.getShncd(), denVo.getTokuisakicd());
                        } else {
                            tokubetsuUntList =
                                tokuisakiDao.getStShnTokubetsuUnt(denVo.getApkihoncd(),
                                    denVo.getShopcd(), meiVo.getShncd(), denVo.getTokuisakicd());
                        }
                        meiVo.setTokubetsuUntList(tokubetsuUntList);
                    }
                    // ■:2 Isac)Shiba 2020-12-16 価格改定対応
                }
            }

            return meisaiList;

        } catch (Exception e) {
            // S01Util.debugLogOut(LOGGER, "受注明細を取得できませんでした。");
            throw new BusinessException("受注明細を取得できませんでした。", e);
        } finally {

        }
    }


    // ■:1 Isac)Shiba 2021-02-22 価格改定対応
    /**
     * 明細の価格再編集 （※価格の基準日が配達予定日になり、受注予約から明細を取得したタイミングでは受注予定日が空欄のため 受注予定日決定後に呼び出せるよう別メソッド化）
     * 
     * @param meisaiList
     * @param priceSetFg
     * @param denVo
     * @return
     */
    public List<JuchuMeisaiVO> reEditUnt(List<JuchuMeisaiVO> meisaiList, boolean priceSetFg,
        JuchuDenpyoVO denVo) {
        // 価格情報VO取得
        PriceInfoVo priceVo = null;
        if (priceSetFg) {
            // S01PriceService priceService = new S01PriceService(tran);
            // ■:1 Base)Arai 2014-01-08 消費税基準日変更対応
            if (StringUtil.isEmpty(denVo.getDlvrydd())) {
                // 配達日がセットされていない場合は配達予定日をセット(主に受注～配達予定データ向け)
                priceVo = priceService.getPriceInfo(denVo.getApkihoncd(), denVo.getShopcd(),
                    denVo.getDlvryytidd());
            } else {
                // 配達日をセット(主に配達実績登録向け)
                priceVo = priceService.getPriceInfo(denVo.getApkihoncd(), denVo.getShopcd(),
                    denVo.getDlvrydd());
            }
            // ■:2 Base)Arai 2014-01-08 消費税基準日変更対応
        }

        // ■:1 Base)Arai 2013-12-05 計上区分のキャンペーン初期表示切替対応
        JuchuRelationVo relationVo = getJuchuRelation(denVo);
        // ■:2 Base)Arai 2013-12-05 計上区分のキャンペーン初期表示切替対応

        // 単価の再設定を行う
        for (int i = 0; i < meisaiList.size(); i++) {
            JuchuMeisaiVO meiVo = meisaiList.get(i);
            // ■:1 Base)Arai 2013-12-05 計上区分のキャンペーン初期表示切替対応
            meiVo.setRelationVo(relationVo.createHalfClone(meiVo));
            // ■:2 Base)Arai 2013-12-05 計上区分のキャンペーン初期表示切替対応

            // 商品・入力行のみ
            if (CommonConstants.FLG_ON.equals(meiVo.getNyuryokumeisaiflg())
                && CommonConstants.KBN_MEISAI_SHOHIN.equals(meiVo.getMeisaikbn())) {

                // 標準単価の設定
                setCurrentHyojununt(denVo, meiVo);

                // 受注予約の場合は受注単価の再設定を行う
                if (priceSetFg) {
                    setCurrentAcptunt(denVo, meiVo, priceVo);
                }
            }
        }
        return meisaiList;
    }

    /**
     * 受注伝票・明細情報を取得します．
     * 
     * @param priceSetFg 単価セットフラグ
     * @param removeShimeFg 締めデータ削除フラグ
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param acptdnpyno 受注伝票番号
     * @param edano 枝番号
     * @return S01JuchuDenpyoVO 結果
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public JuchuDenpyoVO getJuchuDenpyoMeisai(boolean priceSetFg, boolean removeShimeFg,
        String apkihoncd, String shopcd, String acptdnpyno, String edano) {

        try {
            // サービスクラス取得
            // S02Service s02Service = new S02Service();
            // S02ShohinService s02ShohinService = new S02ShohinService(tran);
            S02M046TEntity shopModel = commonService.getShopModel(apkihoncd, shopcd);

            // 運用日付取得
            String opeDate = commonService.getOperationDate(apkihoncd, shopcd);

            // 既存伝票・明細取得
            JuchuDenpyoVO denVo = getJuchuDenpyo(apkihoncd, shopcd, acptdnpyno, edano);
            List<JuchuMeisaiVO> meisaiList = getJuchuMeisai(priceSetFg, denVo);

            // 在庫管理区分セット
            denVo.setZaikokanrikbn(CommonConstants.FLG_OFF.equals(shopModel.getZaikokanrikbn())
                ? CommonConstants.FLG_OFF
                : CommonConstants.FLG_ON);
            // 税計算区分セット
            denVo
                .setTaxkeisankbn(S02M046TEntityDaoEx.getCurrentTaxkeisankbnuri(shopModel, opeDate));

            // 得意先情報取得
            JuchuDenpyoVO denVoTokuisaki =
                getTokuisakiDlvryInfo(apkihoncd, shopcd, denVo.getTokuisakicd(), opeDate);
            denVo.setSkysakicd(denVoTokuisaki.getSkysakicd());
            if (StringUtil.isEmpty(denVo.getDlvryareacd())) {
                denVo.setDlvryareacd(denVoTokuisaki.getDlvryareacd());
                denVo.setDlvryareanm(denVoTokuisaki.getDlvryareanm());
                denVo.setDlvryptnnm(denVoTokuisaki.getDlvryptnnm());
            }
            if (StringUtil.isEmpty(denVo.getDlvrykibojikankbn())) {
                denVo.setDlvrykibojikan(denVoTokuisaki.getDlvrykibojikan());
                // ■:1 TJS)takakuwa 2009-01-29
                denVo.setDlvrykibojikanshry(denVoTokuisaki.getDlvrykibojikanshry());
                // ■:2 TJS)takakuwa 2009-01-29
                denVo.setDlvrykibojikankbn(denVoTokuisaki.getDlvrykibojikankbn());
            }
            if (StringUtil.isEmpty(denVo.getDlvryjun())) {
                denVo.setDlvryjun(denVoTokuisaki.getDlvryjun());
            }

            // 明細プロパティセット
            JuchuSumVo sumVo = new JuchuSumVo();
            JuchuRelationVo relation = getJuchuRelation(denVo);
            for (int i = meisaiList.size() - 1; 0 <= i; i--) {
                JuchuMeisaiVO meiVo = (JuchuMeisaiVO) meisaiList.get(i);
                if (CommonConstants.KBN_MEISAI_SHOHIN.equals(meiVo.getMeisaikbn())) {

                    // 受注関係VO生成
                    meiVo.setRelationVo(relation.createHalfClone(meiVo));

                    // 商品マスタビュー情報取得
                    S02V001TEntity shohinModel = shohinService.getShohinModel(denVo.getApkihoncd(),
                        denVo.getShopcd(), meiVo.getShncd());

                    // プロパティ値のセット
                    meiVo.setUpdateProperties(relation, shohinModel);

                    // 軽減税率対応 ryu 2019-07-14 IT Start
                    if (("4".equals(meiVo.getTaxkbn()) || "5".equals(meiVo.getTaxkbn()))
                        && "20190930".compareTo(opeDate) >= 0) {
                        meiVo.setTaxkbn("1");
                    }


                    // 構成品で親商品の税区分が"5"以外の場合、親商品と同じ税区分を設定する
                    if (meiVo.getNyuryokumeisaiflg().equals("0")) {
                        S02V001TEntity shohinModelOya = shohinService.getShohinModel(
                            meiVo.getApkihoncd(), meiVo.getShopcd(), meiVo.getStshncd());
                        if (!shohinModelOya.getTaxkbn().equals(CommonConstants.KBN_TAX_KOMEISAI)) {
                            meiVo.setTaxkbn(shohinModelOya.getTaxkbn());
                        }
                    }
                    if ((meiVo.getTaxkbn().equals(CommonConstants.KBN_TAX_KEIGEN)
                        || meiVo.getTaxkbn().equals(CommonConstants.KBN_TAX_KOMEISAI))
                        && "20190930".compareTo(opeDate) >= 0) {
                        meiVo.setTaxkbn("1");
                    }



                    // 軽減税率対応 ryu 2019-07-14 IT End

                    // 本数、金額、サーバー交換台数の集計（セット商品は対象外）
                    if (meiVo.isCountable()) {
                        sumVo.addAll(meiVo, meiVo.getRelationVo());
                    }

                    // 受注締データを削除する場合
                } else if (removeShimeFg
                    && CommonConstants.KBN_MEISAI_DAIBIKI.equals(meiVo.getMeisaikbn())) {
                    meisaiList.remove(i);
                }
            }

            denVo.setSumvo(sumVo);
            denVo.setMeisaiList(meisaiList);

            // 検索実行
            return denVo;

        } catch (BusinessException e) {
            // S01Util.debugLogOut(LOGGER, "受注伝票・明細情報を取得できませんでした。");
            throw new BusinessException("受注伝票・明細情報を取得できませんでした。", e);
        } finally {

        }
    }

    /**
     * 得意先配達情報を取得します．
     * 
     * @param apKihonCd AP基本コード
     * @param shopCd ショップコード
     * @param tokuisakicd 得意先コード
     * @param targetDate 取得対象日付
     * @return S01JuchuDenpyoVO 結果
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public JuchuDenpyoVO getTokuisakiDlvryInfo(String apKihonCd, String shopCd, String tokuisakicd,
        String targetDate) {

        try {
            // 得意先マスタEntityKey取得
            S02M013TEntityKey entityKey = new S02M013TEntityKey();
            entityKey.setApkihoncd(apKihonCd);
            entityKey.setShopcd(shopCd);
            entityKey.setTokuisakicd(tokuisakicd);

            // 検索実行
            return getJuchuDao().getTokuisakiDlvryInfo(entityKey, targetDate);

        } catch (Exception e) {
            // S01Util.debugLogOut(LOGGER, "得意先配達情報を取得できませんでした。");
            throw new BusinessException("得意先配達情報を取得できませんでした。", e);
        } finally {

        }
    }

    /**
     * 受注登録キャンセル処理．
     * 
     * @param msgList メッセージリスト
     * @param type 処理タイプ
     * @param yoyakuFg 予約フラグ
     * @param confirmFg 確認フラグ
     * @param sourceVo 受注伝票VO
     * @param zanCount 減算残回数
     * @param DbManagementSession DB管理セッションクラス
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    /* 2018-05-22 IBIS)Shinoda upd start */
    public void processJuchuCancel(List<MessageList> msgList, int type, boolean yoyakuFg,
        boolean confirmFg, JuchuDenpyoVO sourceVo, ArrayList<Integer> yoyakuMeisaiNoList,
        int zanCount, DbManagementSession dbManagementSession) {
        /* 2018-05-22 IBIS)Shinoda upd end */
        /* 2018-05-22 IBIS)Shinoda add start */
        // x0801ではconfirmListは使われない
        // MessageList confirmList = null;
        // if (msgList != null) {
        // confirmList = (MessageList) msgList.get(0);
        // }
        /* 2018-05-22 IBIS)Shinoda add end */
        MessageList errorList = null;
        if (msgList != null && 1 < msgList.size()) {
            errorList = (MessageList) msgList.get(1);
        }

        // ロック処理
        // getS04t001tEntityDao().findByPrimaryKeyForLock(sourceVo);
        getJuchuDao().findByPrimaryKeyForLock(sourceVo);

        // 既存伝票・明細取得処理
        JuchuDenpyoVO denVo = getJuchuDenpyoMeisai(false, false, sourceVo.getApkihoncd(),
            sourceVo.getShopcd(), sourceVo.getAcptdnpyno(), sourceVo.getEdano().toString());

        if (!yoyakuFg) {
            // x0801ではyoyakuFgは常にTRUE
            // /* 2018-05-22 IBIS)Shinoda add start */
            // if (CommonConstants.KBN_TOKUISAKI_KOKUAKU.equals(sourceVo.getTokuisakikbn())
            // && (!StringUtil.isEmpty(denVo.getItakuaiteacptdnpyno())) // 2018-05-23
            // // BASE)Chokenkun add
            // // 障害対応No.A1163
            // && (CommonConstants.KBN_JUTAKUDLVTOKUI_NAKOKYAKU
            // .equals(sourceVo.getJutakudlvrycstkbn())
            // || CommonConstants.JUTAKUDLVRYCSTKBN_4
            // .equals(sourceVo.getJutakudlvrycstkbn()))) {
            //
            // JuchuDenpyoVO jutakuDenpyoVo = new JuchuDenpyoVO();
            // jutakuDenpyoVo = getJuchuDenpyoMeisai(false, false, denVo.getItakuaiteapkihoncd(),
            // denVo.getItakuaiteshopcd(), denVo.getItakuaiteacptdnpyno(),
            // denVo.getItakuaiteedano().toString());
            //
            // // UPD START 20180830 TJS)takio OM-00303
            // if (CommonConstants.DNPYSTS_11.equals(jutakuDenpyoVo.getDnpysts())
            // || CommonConstants.DNPYSTS_12.equals(jutakuDenpyoVo.getDnpysts())
            // || CommonConstants.DNPYSTS_13.equals(jutakuDenpyoVo.getDnpysts())
            // || CommonConstants.DNPYSTS_14.equals(jutakuDenpyoVo.getDnpysts())
            // || CommonConstants.DNPYSTS_19.equals(jutakuDenpyoVo.getDnpysts())) {
            // // UPD E N D 20180830 TJS)takio
            // if (confirmList != null) {
            // confirmList.add(Message.getErrorMessage(MessageConstants.MSG_W256,
            // new String[] {}, new String[] {}));
            // }
            // }
            // }
            // // 確認済の場合は警告をクリアする
            // if (confirmFg) {
            // if (confirmList != null) {
            // confirmList.clear();
            // }
            // }
            // /* 2018-05-22 IBIS)Shinoda add end */
            //
            // // 受注締データのキャンセル処理
            // deleteDaibikiMeisai(denVo);
            //
            // // 得意先マスタ、得意先実績テーブルの値を受注伝票VOにセットする
            // // ■:1 Base)Arai 2014-01-31 得意先実績の排他制御改善対応
            // TokuisakiRelationVo relation = tokuisakiService.getTokuisakiRelation(
            // denVo.getApkihoncd(), denVo.getShopcd(), denVo.getTokuisakicd(), true);
            // // ■:2 Base)Arai 2014-01-31 得意先実績の排他制御改善対応
            // if (relation.getTokuisakiModel() != null) {
            // denVo.setSkysakicd(relation.getTokuisakiModel().getSkysakicd());
            // denVo.setDlvryjun(relation.getTokuisakiModel().getDlvryjun());
            // }
            //
            // // ■:1 Base)chokenkun 2016-06-17 休眠区分バグ修正
            // // 得意先情報更新キャンセル処理
            // if (type == TYPE_UPATE_UPDATE) {
            // checkUpdateTokuisaki(msgList, false, false, relation, denVo, denVo.getMeisaiList(),
            // dbManagementSession);
            // } else {
            // checkUpdateTokuisaki(msgList, true, false, relation, denVo, denVo.getMeisaiList(),
            // dbManagementSession);
            // }
            // // ■:2 Base)chokenkun 2016-06-17 休眠区分バグ修正
            //
            // /* 2018-01-15 IBIS)Inoue upd start */
            // if (!"1".equals(sourceVo.getKyotensts())) {
            // // 在庫情報キャンセル処理
            // List<List<ZaikoKeyVo>> keySet =
            // createZaikoKeyList(true, false, denVo, denVo.getMeisaiList());
            // checkUpdateZaiko(msgList, zaikoService, keySet, dbManagementSession);
            // }
            // /* 2018-01-15 IBIS)Inoue upd start */
            // // 配布物取込キャンセル処理
            // updateHaifubutsu(true, haifubutsuService, denVo, denVo.getMeisaiList(),
            // dbManagementSession);
        }

        // 受注伝票キャンセル処理
        if (type != TYPE_UPATE_UPDATE) {
            denVo.setDlvrytrgtflg(CommonConstants.FLG_OFF);

            /* 2017-09-07 IBIS)Inoue upd start */
            /* 2017-06-29 IBIS)Shinoda add start */
            if (type == TYPE_UPATE_YOYAKUINSERT) {
                // 明細番号リストを作成
                if (yoyakuMeisaiNoList == null) {
                    yoyakuMeisaiNoList = new ArrayList<Integer>();
                    for (int i = 0; i < denVo.getMeisaiList().size(); i++) {
                        JuchuMeisaiVO vo = (JuchuMeisaiVO) denVo.getMeisaiList().get(i);
                        yoyakuMeisaiNoList.add(vo.getAcptdnpymeisaino());
                    }
                }
                if (!StringUtil.isEmpty(sourceVo.getAcptdnpyno())
                    && !StringUtil.isEmpty(sourceVo.getShopcd())
                    && !StringUtil.isEmpty(sourceVo.getAcptdnpyno())
                    && sourceVo.getEdano() != null) {
                    juchuYykKshn(sourceVo.getApkihoncd(), sourceVo.getShopcd(),
                        sourceVo.getAcptdnpyno(), sourceVo.getEdano().toString(),
                        yoyakuMeisaiNoList, zanCount, dbManagementSession);
                }
                type = TYPE_UPATE_DELETE;
            }
            /* 2017-06-29 IBIS)Shinoda add end */
            /* 2017-09-07 IBIS)Inoue upd end */

            if (!updateJuchuDenpyo(type, denVo, null) && errorList != null) {
                errorList.add(Message.getErrorMessage(MessageConstants.MSG_W090, null, null));
            }
        }
    }

    /**
     * 得意先情報を取得します。
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public S02M013TEntity tokuisakiJoho(String apkihoncd, String shopcd, String tokuisakicd) {

        // 得意先情報取得
        S02M013TEntityKey entityKey = new S02M013TEntityKey();
        entityKey.setApkihoncd(apkihoncd);
        entityKey.setShopcd(shopcd);
        entityKey.setTokuisakicd(tokuisakicd);
        S02M013TEntity tokuisakiModel = s02m013tDao.selectByPrimaryKey(entityKey);

        if (tokuisakiModel != null) {
            if (CommonConstants.FLG_OFF.equals(tokuisakiModel.getYukokbn())) {
                return null;
            }
        }

        return tokuisakiModel;
    }
    /* 2017-06-29 IBIS)Shinoda add end */

    /**
     * 新規受注取込行データをチェックします。
     * 
     * @param fileVO ファイルVO
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public void checkShinkiCsv(FileShinkiJuchuVO fileVO) {

        // サービスクラス取得
        // S01Service s01Service = new S01Service();
        // S01FileUploadService fileService = new S01FileUploadService();

        // 受注関連クラス取得
        JuchuRelationVo juchuRelation = getJuchuRelation(new JuchuDenpyoVO());

        // 運用日付取得
        String opedd = commonService.getOperationDate(fileVO.getApkihoncd(), fileVO.getShopcd());

        // 得意先モデル
        S02M013TEntity tokuiModel = null;

        List<String[]> rows = fileVO.getRowList();
        for (int i = 1; i <= rows.size(); i++) {
            String[] row = (String[]) rows.get(i - 1);
            int counter = 0;
            boolean apValid = true;
            boolean shopValid = true;
            boolean acptApValid = true;
            boolean acptShopValid = true;

            // 項目数チェック
            if (!fileUploadService.chkLineSize(fileVO, i, row, fileVO.getColumnCount())) {
                continue;
            }

            // ＡＰ基本コード
            apValid = fileUploadService.checkBasics(fileVO, i, counter, row[counter]);
            if (apValid) {
                apValid = fileUploadService.chkMasterApkihon(fileVO, i, fileVO.getColName(counter),
                    row[counter]) != null;

                // 画面のＡＰ基本コードと違う場合はエラー
                if (apValid && !row[counter].equals(fileVO.getApkihoncd())) {
                    fileVO.addError(i,
                        new Message(MessageConstants.MSG_W124, new String[] {"他ＡＰ"}));
                    apValid = false;
                }
            }

            // ショップコード
            counter++;
            shopValid = fileUploadService.checkBasics(fileVO, i, counter, row[counter]);
            if (apValid && shopValid) {
                shopValid = fileUploadService.chkMasterShop(fileVO, i, fileVO.getColName(counter),
                    row[0], row[counter]) != null;

                // 画面のショップコードと違う場合はエラー
                if (shopValid && !row[counter].equals(fileVO.getShopcd())) {
                    fileVO.addError(i,
                        new Message(MessageConstants.MSG_W124, new String[] {"他ショップ"}));
                    shopValid = false;
                }
            }

            if (!apValid || !shopValid) {
                continue;
            }

            // 得意先コード
            counter++;
            if (fileUploadService.checkBasics(fileVO, i, counter, row[counter]) && apValid
                && shopValid) {
                tokuiModel = fileUploadService.chkMasterTokuisaki(fileVO, i,
                    fileVO.getColName(counter), row[0], row[1], row[counter]);

                // 得意先状態が本会員以外の場合はエラー
                if (!new TokuisakiRelationVo(tokuiModel, null).isHonkaiin()) {
                    fileVO.addError(i,
                        new Message(MessageConstants.MSG_W132, new String[] {"本会員以外", "新規受注"}));
                }
            }

            // 受注日
            counter++;
            fileUploadService.checkBasics(fileVO, i, counter, row[counter]);

            // 受注担当者ＡＰ基本コード
            counter++;
            acptApValid = fileUploadService.checkBasics(fileVO, i, counter, row[counter]);
            if (acptApValid) {
                acptApValid = fileUploadService.chkMasterApkihon(fileVO, i,
                    fileVO.getColName(counter), row[counter]) != null;
            }

            // 受注担当者ショップコード
            counter++;
            acptShopValid = fileUploadService.checkBasics(fileVO, i, counter, row[counter]);
            if (acptApValid && acptShopValid) {
                acptShopValid = fileUploadService.chkMasterShop(fileVO, i,
                    fileVO.getColName(counter), row[counter - 1], row[counter]) != null;
            }

            // 受注担当者コード
            counter++;
            if (fileUploadService.checkBasics(fileVO, i, counter, row[counter]) && acptApValid
                && acptShopValid) {
                fileUploadService.chkMasterTnsha(fileVO, i, fileVO.getColName(counter),
                    row[counter - 2], row[counter - 1], row[counter]);
            }

            // 配達予定日
            counter++;
            if (fileUploadService.checkBasics(fileVO, i, counter, row[counter])) {

                // 配達予定日が運用日付より過去の場合はエラー
                if (opedd != null && 0 < opedd.compareTo(row[counter])) {
                    fileVO.addError(i,
                        new Message(MessageConstants.MSG_W124, new String[] {"過去の配達予定日"}));

                    // 配達予定日が受注日より過去の場合はエラー
                } else if (0 < row[3].compareTo(row[counter])) {
                    fileVO.addError(i,
                        new Message(MessageConstants.MSG_W093, new String[] {"受注日", "配達予定日"}));
                }
            }

            // 配達希望時間・配達希望時間区分
            int dlvryKiboTimeCnt = ++counter;
            int dlvryKiboTimeKbnCnt = ++counter;
            if (fileUploadService.checkBasics(fileVO, i, dlvryKiboTimeKbnCnt,
                row[dlvryKiboTimeKbnCnt])
                && fileUploadService.checkBasics(fileVO, i, dlvryKiboTimeCnt,
                    row[dlvryKiboTimeCnt])) {

                // システム管理マスタ存在チェック
                if (fileUploadService.chkMasterSystem(fileVO, i,
                    fileVO.getColName(dlvryKiboTimeKbnCnt), CommonConstants.APCD_000,
                    CommonConstants.SYS_SHOPCD_000, CommonConstants.SYS_SETTEINO_012,
                    row[dlvryKiboTimeKbnCnt]) == null) {

                } else {

                    // 配達希望時間区分＝「頃・以降・迄」の場合は配達希望時間が必須
                    if (CommonConstants.KBN_DLVRYKIBO_KORO.equals(row[dlvryKiboTimeKbnCnt])
                        || CommonConstants.KBN_DLVRYKIBO_IKOU.equals(row[dlvryKiboTimeKbnCnt])
                        || CommonConstants.KBN_DLVRYKIBO_MADE.equals(row[dlvryKiboTimeKbnCnt])) {

                        if (StringUtil.isEmpty(row[dlvryKiboTimeCnt])) {
                            fileVO.addError(i,
                                new Message(MessageConstants.MSG_W113, new String[] {"配達希望時間"}));
                        }

                        // 配達希望時間区分≠「頃・以降・迄」の場合は配達希望時間が入力不可
                    } else {

                        if (!StringUtil.isEmpty(row[dlvryKiboTimeCnt])) {
                            fileVO.addError(i,
                                new Message(MessageConstants.MSG_W124, new String[] {"配達希望時間"}));
                        }
                    }
                }
            }

            // 回収予定１２Ｌ
            counter++;
            fileUploadService.checkBasics(fileVO, i, counter, row[counter]);

            // 回収予定８Ｌ
            counter++;
            fileUploadService.checkBasics(fileVO, i, counter, row[counter]);

            // 不在置き可能フラグ
            counter++;
            if (fileUploadService.checkBasics(fileVO, i, counter, row[counter])) {

                // 0,1以外はエラー
                if (!CommonConstants.FLG_ON.equals(row[counter])
                    && !CommonConstants.FLG_OFF.equals(row[counter])) {
                    fileVO.addError(i,
                        new Message(MessageConstants.MSG_W120, new String[] {"不在置き可能フラグ", "0か1"}));
                }
            }

            // 印刷メモ
            counter++;
            fileUploadService.checkBasics(fileVO, i, counter, row[counter]);

            // 作成元管理区分
            counter++;
            fileUploadService.checkBasics(fileVO, i, counter, row[counter]);

            // 作成元管理番号
            counter++;
            fileUploadService.checkBasics(fileVO, i, counter, row[counter]);

            // 明細データ
            for (int j = 0; j < fileVO.getMeisaiCount(); j++) {
                boolean shnValid = true;
                S02V001TEntity shohinModel = null;

                // 商品コード
                counter++;
                shnValid = fileUploadService.checkBasics(fileVO, i, counter, row[counter], j == 0);

                // 商品コードが未入力で他の項目が入力されている場合はエラー
                if (StringUtil.isEmpty(row[counter]) && (!StringUtil.isEmpty(row[counter + 1])
                    || !StringUtil.isEmpty(row[counter + 2])
                    || !StringUtil.isEmpty(row[counter + 3])
                    || !StringUtil.isEmpty(row[counter + 4]))) {
                    fileVO.addError(i,
                        fileUploadService.getBlankErrMsg(fileVO.getColName(counter)));

                } else if (shnValid && !StringUtil.isEmpty(row[counter]) && apValid && shopValid) {
                    shohinModel = fileUploadService.chkMasterShohin(fileVO, i,
                        fileVO.getColName(counter), row[0], row[1], row[counter], row[3]);

                    if (shohinModel == null) {
                        shnValid = false;

                        // 基本商品マスタ．取扱区分＝仕入のみの場合はエラー
                    } else if (CommonConstants.KBN_TORIATSUKAI_SHIIRE
                        .equals(shohinModel.getTrtskikbn())) {
                        fileVO.addError(i,
                            new Message(MessageConstants.MSG_W124, new String[] {"仕入のみの商品"}));

                        // 大分類コード＝サーバー関連、中分類＝サーバー本体の場合はエラーとする
                    } else if (juchuRelation.isServerBody(shohinModel.getLctgrycd(),
                        shohinModel.getMctgrycd())) {
                        fileVO.addError(i,
                            new Message(MessageConstants.MSG_W124, new String[] {"サーバー"}));

                        // 経費の時はセット商品の場合はエラー
                    } else if (JuchuRelationVo.isKeihi(row[counter + 2])
                        && CommonConstants.FLG_ON.equals(shohinModel.getStkbn())) {
                        fileVO.addError(i,
                            new Message(MessageConstants.MSG_W124, new String[] {"セット商品"}));
                    }

                    // ■:1 Base)chokenkun 2017-10-02 計上区分キャンペーンでセット商品登録不可チェック 2017-10-22 次期ＡＣＣＩＳに移行
                    // キャンペーン時はセット商品の場合はエラー
                    else if (JuchuRelationVo.isCampaign(row[counter + 2])
                        && CommonConstants.FLG_ON.equals(shohinModel.getStkbn())) {
                        fileVO.addError(i, new Message(MessageConstants.MSG_W124,
                            new String[] {"計上区分がキャンペーンの場合はセット商品"}));
                    }
                    // ■:2 Base)chokenkun 2017-10-02 計上区分キャンペーンでセット商品登録不可チェック 2017-10-22 次期ＡＣＣＩＳに移行
                }

                // 計上区分、受注数量、受注単価は一行目または、商品コードが入力されている場合は必須とする
                boolean followFg = (j == 0) || !StringUtil.isEmpty(row[counter]);

                // 型式
                counter++;
                if (fileUploadService.checkBasics(fileVO, i, counter, row[counter])
                    && !StringUtil.isEmpty(row[counter]) && apValid && shnValid) {
                    fileUploadService.chkMasterKatashiki(fileVO, i, fileVO.getColName(counter),
                        row[0], row[counter - 1], row[counter]);
                }

                // 計上区分
                counter++;
                if (fileUploadService.checkBasics(fileVO, i, counter, row[counter], followFg)
                    && !StringUtil.isEmpty(row[counter])) {

                    if (fileUploadService.chkMasterSystem(fileVO, i, fileVO.getColName(counter),
                        CommonConstants.APCD_000, CommonConstants.SYS_SHOPCD_000,
                        CommonConstants.SYS_SETTEINO_009, row[counter]) == null) {

                        // 計上区分関係チェック
                        // ■:1 Base)chokenkun 2017-04-28 新規受注取込計上区分関係チェック対応 2017-10-22 次期ＡＣＣＩＳに移行
                    } else if (tokuiModel != null && shohinModel != null
                        && !juchuRelation.checkKeijokbnShinkiTorikomi(tokuiModel.getTokuisakikbn(),
                            row[counter], shohinModel.getLctgrycd())) {
                        // ■:2 Base)chokenkun 2017-04-28 新規受注取込計上区分関係チェック対応 2017-10-22 次期ＡＣＣＩＳに移行
                        fileVO.addError(i, new Message(MessageConstants.MSG_W123,
                            new String[] {fileVO.getColName(counter)}));

                        // 請求先が存在しない場合は有料の場合はエラー
                    } else if (tokuiModel != null && StringUtil.isEmpty(tokuiModel.getSkysakicd())
                        && JuchuRelationVo.isYuryo(row[counter])) {
                        fileVO.addError(i, new Message(MessageConstants.MSG_W140, new String[] {}));
                    }
                }

                // 受注数量
                counter++;
                if (fileUploadService.checkBasics(fileVO, i, counter, row[counter], followFg)) {

                    // 商品マスタの数量端数管理区分＝なしの時、数量に小数点が含まれてる場合はエラー
                    if (!S02V001TEntityDaoEx.isValidSuryo(shohinModel, row[counter])) {
                        fileVO.addError(i,
                            new Message(MessageConstants.MSG_W120, new String[] {"数量", "整数"}));
                    }
                }

                // 受注単価
                counter++;
                if (fileUploadService.checkBasics(fileVO, i, counter, row[counter], followFg)) {

                    // 計上区分が売上、キャンペーン、経費以外の時、単価が０以外はエラー
                    if (!StringUtil.isEmpty(row[counter])
                        && !CommonConstants.KBN_KEIJO_URIAGE.equals(row[counter - 2])
                        && !CommonConstants.KBN_KEIJO_CAMPAIGN.equals(row[counter - 2])
                        && !CommonConstants.KBN_KEIJO_KEIHI.equals(row[counter - 2])
                        && !BusinessUtil.isEqualZero(row[counter])) {
                        fileVO.addError(i, new Message(MessageConstants.MSG_W132,
                            new String[] {"無料の計上区分", "単価入力"}));
                    }
                }

                // 受注金額
                BigDecimal acptkin =
                    new BigDecimal(BusinessUtil.calcMultiple(row[counter - 1], row[counter]));
                if (!fileUploadService.chkColByte(
                    acptkin.setScale(0, RoundingMode.CEILING).toString(),
                    FileShinkiJuchuVO.AMOUNT_ACPTKIN)) {
                    fileVO.addError(i, new Message(MessageConstants.MSG_W135,
                        new String[] {"受注金額" + String.valueOf(j + 1)}));
                }

                // 明細備考
                counter++;
                fileUploadService.checkBasics(fileVO, i, counter, row[counter]);
            }

            /* 2017-06-29 IBIS)Shinoda add begin */
            // 得意先情報取得
            tokuiModel = tokuisakiJoho(row[0], row[1], row[2]);
            boolean itakuValid = true;
            boolean jutakuValid = true;

            if (tokuiModel != null) {

                if (CommonConstants.KBN_TOKUISAKI_KOKUAKU.equals(tokuiModel.getTokuisakikbn())
                    && (CommonConstants.KBN_JUTAKUDLVTOKUI_NAKOKYAKU
                        .equals(tokuiModel.getJutakudlvrycstkbn())
                        || CommonConstants.JUTAKUDLVRYCSTKBN_4
                            .equals(tokuiModel.getJutakudlvrycstkbn()))) {

                    // 委託AP／ショップ独自商品チェック
                    for (int j = 16; j <= 46; j = j + 6) {
                        if (!StringUtil.isEmpty(row[j])) {
                            if (getJuchuDao().dokujiShnChk(row[0], row[1], row[j])) {
                                fileVO.addError(i,
                                    new Message(MessageConstants.MSG_W233, new String[] {""}));
                            }
                        }
                    }

                    // 委託得意先コード存在チェック
                    if (StringUtil.isEmpty(tokuiModel.getItakuaiteapkihoncd())
                        || StringUtil.isEmpty(tokuiModel.getItakuaiteshopcd())
                        || StringUtil.isEmpty(tokuiModel.getItakuaitetokuisakicd())) {
                        itakuValid = false;
                        fileVO.addError(i,
                            new Message(MessageConstants.MSG_W236, new String[] {""}));
                    }

                    // 委託ショップ商品マスタ存在チェック
                    if (itakuValid) {
                        for (int j = 16; j <= 46; j = j + 6) {
                            if (!StringUtil.isEmpty(row[j])) {
                                if (getJuchuDao().ShnSnziChk(tokuiModel.getItakuaiteapkihoncd(),
                                    tokuiModel.getItakuaiteshopcd(), row[j])) {
                                    fileVO.addError(i,
                                        new Message(MessageConstants.MSG_W234, new String[] {""}));
                                }
                            }
                        }
                    }
                }

                if ((CommonConstants.KBN_TOKUISAKI_NAJUTAKU.equals(tokuiModel.getTokuisakikbn())
                    || CommonConstants.KBN_TOKUISAKI_DLVRYJUTAKU
                        .equals(tokuiModel.getTokuisakikbn()))
                    && (CommonConstants.KBN_JUTAKUDLVTOKUI_NAJUTAKU
                        .equals(tokuiModel.getJutakudlvrycstkbn())
                        || CommonConstants.JUTAKUDLVRYCSTKBN_6
                            .equals(tokuiModel.getJutakudlvrycstkbn()))) {

                    // 受託AP／ショップ独自商品チェック
                    for (int j = 16; j <= 46; j = j + 6) {
                        if (!StringUtil.isEmpty(row[j])) {
                            if (getJuchuDao().dokujiShnChk(row[0], row[1], row[j])) {
                                fileVO.addError(i,
                                    new Message(MessageConstants.MSG_W233, new String[] {""}));
                            }
                        }
                    }

                    // 受託得意先コード存在チェック
                    if (StringUtil.isEmpty(tokuiModel.getItakuaiteapkihoncd())
                        || StringUtil.isEmpty(tokuiModel.getItakuaiteshopcd())
                        || StringUtil.isEmpty(tokuiModel.getItakuaitetokuisakicd())) {
                        jutakuValid = false;
                        fileVO.addError(i,
                            new Message(MessageConstants.MSG_W238, new String[] {""}));
                    }

                    // 受託ショップ商品マスタ存在チェック
                    if (jutakuValid) {
                        for (int j = 16; j <= 46; j = j + 6) {
                            if (!StringUtil.isEmpty(row[j])) {
                                if (getJuchuDao().ShnSnziChk(tokuiModel.getItakuaiteapkihoncd(),
                                    tokuiModel.getItakuaiteshopcd(), row[j])) {
                                    fileVO.addError(i,
                                        new Message(MessageConstants.MSG_W235, new String[] {""}));
                                }
                            }
                        }
                    }
                }

                if (itakuValid
                    && CommonConstants.KBN_TOKUISAKI_KOKUAKU.equals(tokuiModel.getTokuisakikbn())
                    && (CommonConstants.KBN_JUTAKUDLVTOKUI_NAKOKYAKU
                        .equals(tokuiModel.getJutakudlvrycstkbn())
                        || CommonConstants.JUTAKUDLVRYCSTKBN_4
                            .equals(tokuiModel.getJutakudlvrycstkbn()))) {

                    // 委託得意先コード存在チェック
                    if (StringUtil.isEmpty(tokuiModel.getItakuaiteapkihoncd())
                        || StringUtil.isEmpty(tokuiModel.getItakuaiteshopcd())
                        || StringUtil.isEmpty(tokuiModel.getItakuaitetokuisakicd())) {
                        itakuValid = false;
                        fileVO.addError(i,
                            new Message(MessageConstants.MSG_W236, new String[] {""}));
                    }


                    // 委託得意先マスタ存在チェック
                    if (itakuValid) {
                        S02M013TEntity tokuisakiModel2 = tokuisakiJoho(
                            tokuiModel.getItakuaiteapkihoncd(), tokuiModel.getItakuaiteshopcd(),
                            tokuiModel.getItakuaitetokuisakicd());
                        if (tokuisakiModel2 == null) {
                            itakuValid = false;
                            fileVO.addError(i, new Message(MessageConstants.MSG_W084,
                                new String[] {"委託元が得意先マスタ"}));
                        }
                    }
                }

                if (jutakuValid
                    && (CommonConstants.KBN_TOKUISAKI_NAJUTAKU.equals(tokuiModel.getTokuisakikbn())
                        || CommonConstants.KBN_TOKUISAKI_DLVRYJUTAKU
                            .equals(tokuiModel.getTokuisakikbn()))
                    && (CommonConstants.KBN_JUTAKUDLVTOKUI_NAJUTAKU
                        .equals(tokuiModel.getJutakudlvrycstkbn())
                        || CommonConstants.JUTAKUDLVRYCSTKBN_6
                            .equals(tokuiModel.getJutakudlvrycstkbn()))) {

                    // 受託得意先コード存在チェック
                    if (StringUtil.isEmpty(tokuiModel.getItakuaiteapkihoncd())
                        || StringUtil.isEmpty(tokuiModel.getItakuaiteshopcd())
                        || StringUtil.isEmpty(tokuiModel.getItakuaitetokuisakicd())) {
                        jutakuValid = false;
                        fileVO.addError(i,
                            new Message(MessageConstants.MSG_W238, new String[] {""}));
                    }

                    // 受託得意先マスタ存在チェック
                    if (jutakuValid) {
                        S02M013TEntity tokuisakiModel2 = tokuisakiJoho(
                            tokuiModel.getItakuaiteapkihoncd(), tokuiModel.getItakuaiteshopcd(),
                            tokuiModel.getItakuaitetokuisakicd());
                        if (tokuisakiModel2 == null) {
                            jutakuValid = false;
                            fileVO.addError(i, new Message(MessageConstants.MSG_W084,
                                new String[] {"委託先が得意先マスタに"}));
                        }
                    }
                }

                if (itakuValid
                    && CommonConstants.KBN_TOKUISAKI_KOKUAKU.equals(tokuiModel.getTokuisakikbn())
                    && (CommonConstants.KBN_JUTAKUDLVTOKUI_NAKOKYAKU
                        .equals(tokuiModel.getJutakudlvrycstkbn())
                        || CommonConstants.JUTAKUDLVRYCSTKBN_4
                            .equals(tokuiModel.getJutakudlvrycstkbn()))) {

                    // 委託相手コード連携チェック（委託）
                    String apkihoncd = "";
                    String shopcd = "";
                    String tokuisakicd = "";
                    if (tokuiModel != null) {
                        apkihoncd = tokuiModel.getItakuaiteapkihoncd();
                        shopcd = tokuiModel.getItakuaiteshopcd();
                        tokuisakicd = tokuiModel.getItakuaitetokuisakicd();
                    }

                    S02M013TEntity tokuisakiModel3 = tokuisakiJoho(apkihoncd, shopcd, tokuisakicd);

                    String itakuapkihoncd = "";
                    String itakushopcd = "";
                    String itakutokuisakicd = "";
                    if (tokuisakiModel3 != null) {
                        itakuapkihoncd = tokuisakiModel3.getItakuaiteapkihoncd();
                        itakushopcd = tokuisakiModel3.getItakuaiteshopcd();
                        itakutokuisakicd = tokuisakiModel3.getItakuaitetokuisakicd();
                    }

                    if (!(row[0].equals(itakuapkihoncd) && row[1].equals(itakushopcd)
                        && row[2].equals(itakutokuisakicd))) {
                        fileVO.addError(i,
                            new Message(MessageConstants.MSG_W240, new String[] {""}));
                    }
                }

                if (jutakuValid
                    && (CommonConstants.KBN_TOKUISAKI_NAJUTAKU.equals(tokuiModel.getTokuisakikbn())
                        || CommonConstants.KBN_TOKUISAKI_DLVRYJUTAKU
                            .equals(tokuiModel.getTokuisakikbn()))
                    && (CommonConstants.KBN_JUTAKUDLVTOKUI_NAJUTAKU
                        .equals(tokuiModel.getJutakudlvrycstkbn())
                        || CommonConstants.JUTAKUDLVRYCSTKBN_6
                            .equals(tokuiModel.getJutakudlvrycstkbn()))) {

                    // 委託相手コード連携チェック（受託）
                    String apkihoncd = "";
                    String shopcd = "";
                    String tokuisakicd = "";
                    if (tokuiModel != null) {
                        apkihoncd = tokuiModel.getItakuaiteapkihoncd();
                        shopcd = tokuiModel.getItakuaiteshopcd();
                        tokuisakicd = tokuiModel.getItakuaitetokuisakicd();
                    }

                    S02M013TEntity tokuisakiModel3 = tokuisakiJoho(apkihoncd, shopcd, tokuisakicd);

                    String itakuapkihoncd = "";
                    String itakushopcd = "";
                    String itakutokuisakicd = "";
                    if (tokuisakiModel3 != null) {
                        itakuapkihoncd = tokuisakiModel3.getItakuaiteapkihoncd();
                        itakushopcd = tokuisakiModel3.getItakuaiteshopcd();
                        itakutokuisakicd = tokuisakiModel3.getItakuaitetokuisakicd();
                    }

                    if (!(row[0].equals(itakuapkihoncd) && row[1].equals(itakushopcd)
                        && row[2].equals(itakutokuisakicd))) {
                        fileVO.addError(i,
                            new Message(MessageConstants.MSG_W240, new String[] {""}));
                    }
                }
            }
            /* 2017-06-29 IBIS)Shinoda add end */
        }
    }

    /**
     * 新規受注取込処理．
     * 
     * @param msgList メッセージリスト
     * @param fileVO ファイルVO
     * @param DbManagementSession DB管理セッションクラス
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    @Transactional
    public void processShinkiJuchu(MessageList msgList, FileShinkiJuchuVO fileVO,
        DbManagementSession dbManagementSession) {

        // サービスクラス取得
        // S02Service s02Service = new S02Service(tran);
        // S01ZaikoService zaikoService = S01ZaikoService.getInstance(tran);
        // S01TokuisakiService tokuisakiService = new S01TokuisakiService(tran);

        // ショップマスタモデル取得
        S02M046TEntity shopModel =
            commonService.getShopModel(fileVO.getApkihoncd(), fileVO.getShopcd());

        //// ////
        //// 受注伝票・明細作成処理 ////
        //// ////

        // CSVデータを新規受注ワークに取り込む
        insertShinkiWork(fileVO, dbManagementSession);

        // 新規受注ワーク情報より受注伝票・明細更新
        insertJuchuFromShinkiWork(fileVO, shopModel, dbManagementSession);

        //// ////
        //// 得意先情報更新処理 ////
        //// ////

        // 得意先情報取得用VO作成
        List<S02M050TEntity> tokuisakiList =
            getJuchuDao().getDenpyoTokuisakiSummary(fileVO, shopModel);

        // 得意先単位に処理を実行する
        for (int i = 0; i < tokuisakiList.size(); i++) {
            S02M050TEntity updateModel = tokuisakiList.get(i);

            // 得意先関係VOの取得
            // ■:1 Base)Arai 2014-01-31 得意先実績の排他制御改善対応
            TokuisakiRelationVo tokuiRelation =
                tokuisakiService.getTokuisakiRelation(updateModel.getApkihoncd(),
                    updateModel.getShopcd(), updateModel.getTokuisakicd(), true);
            // ■:2 Base)Arai 2014-01-31 得意先実績の排他制御改善対応

            // 得意先実績テーブルの更新
            if (tokuiRelation.getJissekiModel() != null) {
                S02M050TEntity currentJissekiModel = tokuiRelation.getJissekiModel();

                // 実績テーブルの最終受注日が今回の最終受注日より古い時はセットする
                if (!(!StringUtil.isEmpty(updateModel.getSaishuacptdd())
                    && !StringUtil.isEmpty(currentJissekiModel.getSaishuacptdd()) && 0 < updateModel
                        .getSaishuacptdd().compareTo(currentJissekiModel.getSaishuacptdd()))) {
                    updateModel.setSaishuacptdd("");
                }
                int jissekiCount = tokuisakiService.updateTokuisakiJisseki(false,
                    currentJissekiModel, updateModel, dbManagementSession);

                if (jissekiCount == 0) {
                    msgList.add(new Message(MessageConstants.MSG_W135, new String[] {"計上前金額"}));
                }
            }

            // 得意先マスタの更新（休眠区分）
            if (tokuiRelation.isKyumin()) {
                S02M013TEntity updateTokuiModel = new S02M013TEntity();
                updateTokuiModel.setKyuminkbn(CommonConstants.FLG_OFF);
                tokuisakiService.updateTokuisaki(false, tokuiRelation.getTokuisakiModel(),
                    updateTokuiModel, dbManagementSession);
            }
        }

        //// ////
        //// 在庫・アラーム処理 ////
        //// ////

        // 集計キーオブジェクト生成
        JuchuTeikiVo teikiVo = new JuchuTeikiVo();
        // teikiVo.writeProperties(fileVO);
        BeanUtils.copyProperties(fileVO, teikiVo);

        teikiVo.setRelationVo(getJuchuRelation(new JuchuDenpyoVO()));

        // 更新後の受注伝票・明細を集計して取得
        List<List<ZaikoKeyVo>> keySet = new ArrayList<List<ZaikoKeyVo>>();
        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 0:
                    teikiVo.setZaikotype(ZaikoKeyVo.TYPE_S04T005T);
                    break;
                case 1:
                    teikiVo.setZaikotype(ZaikoKeyVo.TYPE_S04T006T);
                    break;
                case 2:
                    teikiVo.setZaikotype(ZaikoKeyVo.TYPE_S04T007T);
                    break;
                case 3:
                    teikiVo.setZaikotype(ZaikoKeyVo.TYPE_S13T004T);
                    break;
                default:
                    break;
            }
            // 在庫引当テーブルの場合、ショップマスタの在庫管理区分＝無しの場合は更新しない
            if (!(ZaikoKeyVo.TYPE_S13T004T == teikiVo.getZaikotype() && shopModel != null
                && CommonConstants.FLG_OFF.equals(shopModel.getZaikokanrikbn()))) {
                /* 2017-11-20 ibis)inoue update begin */
                keySet.add(getJuchuDao().getTeikiZaikoList2(teikiVo));
                /* 2017-11-20 ibis)inoue update end */
            }
        }

        // ロック＆更新実行
        for (int i = 0; i < keySet.size(); i++) {
            zaikoService.lockTable(null, keySet.get(i), dbManagementSession);
        }
        checkUpdateZaiko(null, zaikoService, keySet, dbManagementSession);

        //// ////
        //// 更新結果取得処理 ////
        //// ////
        fileVO.setResultList(getJuchuDao().getShinkiWork(fileVO, true));
    }

    /**
     * 新規受注データをワークに取り込みます．
     * 
     * @param fileVO ファイルＶＯ
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public void insertShinkiWork(FileShinkiJuchuVO fileVO,
        DbManagementSession dbManagementSession) {
        List<S04W001TEntity> insertList = new ArrayList<S04W001TEntity>();

        // 新規受注取込ワーク新規挿入用オブジェクト生成
        List<String[]> rows = fileVO.getRowList();
        for (int i = 0; i < rows.size(); i++) {
            S04W001TEntity model = new S04W001TEntity();
            int counter = 0;

            String[] values = (String[]) rows.get(i);
            model.setLoginmonoapkihoncd(fileVO.getLoginmonoapkihoncd());
            model.setLoginmonoshopcd(fileVO.getLoginmonoshopcd());
            model.setLogintantomonocd(fileVO.getLogintantomonocd());
            model.setGyonol(i + 1);
            model.setApkihoncd(values[counter++]);
            model.setShopcd(values[counter++]);
            model.setTokuisakicd(values[counter++]);
            model.setAcptdd(values[counter++]);
            model.setAcpttnshaapkihoncd(values[counter++]);
            model.setAcpttnshashopcd(values[counter++]);
            model.setAcpttnshacd(values[counter++]);
            model.setDlvryytidd(values[counter++]);
            model.setDlvrykibojikan(values[counter++]);
            model.setDlvrykibojikankbn(values[counter++]);
            String kybtl12l = values[counter++];
            if (kybtl12l == null || kybtl12l.isEmpty()) {
                model.setKybtln1n2rittoruhns((short) 0);
            } else {
                model.setKybtln1n2rittoruhns(Short.valueOf(kybtl12l));
            }
            String kybtl8l = values[counter++];
            if (kybtl8l == null || kybtl8l.isEmpty()) {
                model.setKybtln8rittoruhns((short) 0);
            } else {
                model.setKybtln8rittoruhns(Short.valueOf(kybtl8l));
            }
            model.setFzaiokikanoflg(values[counter++]);
            model.setPrntmemo(values[counter++]);
            model.setSksmotokanrikbn(values[counter++]);
            model.setSksmotokanrino(values[counter++]);
            model.setShncdn1(values[counter++]);
            model.setKatasikin1(values[counter++]);
            model.setKeijokbnn1(values[counter++]);
            if (!StringUtil.isEmpty(values[counter])) {
                model.setAcptsuryon1(new BigDecimal(values[counter]));
            }
            counter++;
            if (!StringUtil.isEmpty(values[counter])) {
                model.setAcptuntn1(new BigDecimal(values[counter]));
            }
            counter++;
            model.setMeisaibikon1(values[counter++]);
            model.setShncdn2(values[counter++]);
            model.setKatasikin2(values[counter++]);
            model.setKeijokbnn2(values[counter++]);
            if (!StringUtil.isEmpty(values[counter])) {
                model.setAcptsuryon2(new BigDecimal(values[counter]));
            }
            counter++;
            if (!StringUtil.isEmpty(values[counter])) {
                model.setAcptuntn2(new BigDecimal(values[counter]));
            }
            counter++;
            model.setMeisaibikon2(values[counter++]);
            model.setShncdn3(values[counter++]);
            model.setKatasikin3(values[counter++]);
            model.setKeijokbnn3(values[counter++]);
            if (!StringUtil.isEmpty(values[counter])) {
                model.setAcptsuryon3(new BigDecimal(values[counter]));
            }
            counter++;
            if (!StringUtil.isEmpty(values[counter])) {
                model.setAcptuntn3(new BigDecimal(values[counter]));
            }
            counter++;
            model.setMeisaibikon3(values[counter++]);
            model.setShncdn4(values[counter++]);
            model.setKatasikin4(values[counter++]);
            model.setKeijokbnn4(values[counter++]);
            if (!StringUtil.isEmpty(values[counter])) {
                model.setAcptsuryon4(new BigDecimal(values[counter]));
            }
            counter++;
            if (!StringUtil.isEmpty(values[counter])) {
                model.setAcptuntn4(new BigDecimal(values[counter]));
            }
            counter++;
            model.setMeisaibikon4(values[counter++]);
            model.setShncdn5(values[counter++]);
            model.setKatasikin5(values[counter++]);
            model.setKeijokbnn5(values[counter++]);
            if (!StringUtil.isEmpty(values[counter])) {
                model.setAcptsuryon5(new BigDecimal(values[counter]));
            }
            counter++;
            if (!StringUtil.isEmpty(values[counter])) {
                model.setAcptuntn5(new BigDecimal(values[counter]));
            }
            counter++;
            model.setMeisaibikon5(values[counter++]);
            model.setShncdn6(values[counter++]);
            model.setKatasikin6(values[counter++]);
            model.setKeijokbnn6(values[counter++]);
            if (!StringUtil.isEmpty(values[counter])) {
                model.setAcptsuryon6(new BigDecimal(values[counter]));
            }
            counter++;
            if (!StringUtil.isEmpty(values[counter])) {
                model.setAcptuntn6(new BigDecimal(values[counter]));
            }
            counter++;
            model.setMeisaibikon6(values[counter++]);
            model.setToshacd(dbManagementSession.getTnshacd()); // 登録者コード
            model.setToshaapkihoncd(dbManagementSession.getApkihoncd()); // 登録者ＡＰ基本コード
            model.setToshashopcd(dbManagementSession.getShopcd()); /// 登録者ショップコード
            model.setInptsystmymd(DateTimeUtil.changeFormatToSystemDate(LocalDateTime.now())); // 登録システム日付
            model.setInptsystmjikan(DateTimeUtil.changeFormatToSystemTime(LocalDateTime.now())); // 登録システム時間
            model.setInptunyoymd(dbManagementSession.getUnyoDate()); // 登録運用日付
            model.setInptipadrse(dbManagementSession.getIpAddress()); // 登録ＩＰアドレス
            model.setInptpgmid(dbManagementSession.getProgramId()); // 登録プログラムＩＤ
            model.setKoshacd(dbManagementSession.getTnshacd()); // 更新者コード
            model.setKoshaapkihoncd(dbManagementSession.getApkihoncd()); // 更新者ＡＰ基本コード
            model.setKoshashopcd(dbManagementSession.getShopcd()); // 更新者ショップコード
            model.setKshnsystmymd(DateTimeUtil.changeFormatToSystemDate(LocalDateTime.now())); // 更新システム日付
            model.setKshnsystmjikan(DateTimeUtil.changeFormatToSystemTime(LocalDateTime.now())); // 更新システム時間
            model.setKshnunyoymd(dbManagementSession.getUnyoDate()); // 更新運用日付
            model.setKshnipadrse(dbManagementSession.getIpAddress()); // 更新ＩＰアドレス
            model.setKshnpgmid(dbManagementSession.getProgramId()); // 更新プログラムＩＤ
            insertList.add(model);
        }

        // 新規受注ワーク一括削除
        getJuchuDao().deleteShinkiWork(fileVO);

        // 新規受注ワーク挿入
        for (S04W001TEntity entity : insertList) {
            getS04w001tEntityDao().insert(entity);
        }

    }

    /**
     * 新規受注取込ワークの更新処理を行います。
     * 
     * @param model 新規受注取込ワークのmodel
     * @param dbManagementSession
     * @param kekka 新規受注取込ワークの結果
     * @throws SystemException システム例外
     */
    public void updateShinkiWork(S04W001TEntity model, DbManagementSession dbManagementSession,
        String kekka) {
        S04W001TEntity entity = new S04W001TEntity();
        entity.setLoginmonoapkihoncd(model.getLoginmonoapkihoncd());
        entity.setLoginmonoshopcd(model.getLoginmonoshopcd());
        entity.setLogintantomonocd(model.getLogintantomonocd());
        entity.setGyonol(model.getGyonol());
        if (kekka != null) {
            entity.setKekka(kekka);
        } else {
            entity.setAcptdnpyno(model.getAcptdnpyno());
        }
        entity.setKoshaapkihoncd(dbManagementSession.getApkihoncd());
        entity.setKoshashopcd(dbManagementSession.getShopcd());
        entity.setKoshacd(dbManagementSession.getTnshacd());
        entity.setKshnsystmymd(DateTimeUtil.changeFormatToSystemDate(LocalDateTime.now()));
        entity.setKshnsystmjikan(DateTimeUtil.changeFormatToSystemTime(LocalDateTime.now()));
        entity.setKshnunyoymd(dbManagementSession.getUnyoDate());
        entity.setKshnipadrse(dbManagementSession.getIpAddress());
        entity.setKshnpgmid(dbManagementSession.getProgramId());

        s04w001tDao.updateByPrimaryKeySelective(entity);
    }

    /**
     * 新規受注取込ワークより受注伝票を作成します．
     * 
     * @param fileVO ファイルＶＯ
     * @param shopModel ショップマスタモデル
     * @param DbManagementSession DB管理セッションクラス
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public void insertJuchuFromShinkiWork(FileShinkiJuchuVO fileVO, S02M046TEntity shopModel,
        DbManagementSession dbManagementSession) {
        TokuisakiRelationVo tokuiRelation = null;
        List<JuchuDenpyoVO> result = new ArrayList<JuchuDenpyoVO>();
        String tokuisakicd = null;
        boolean isFirst = false;
        boolean isSuspend = false;
        /* 2017-11-20 ibis)inoue add begin */
        boolean isLast = false;
        int yoyakuCount = 0;
        /* 2017-11-20 ibis)inoue add end */

        // サービスクラス取得
        // S01SaibanService saibanService = new S01SaibanService(tran);
        // S01HaifubutsuService haifubutsuService = new S01HaifubutsuService(tran);
        // S01TokuisakiService tokuisakiService = new S01TokuisakiService(tran);
        // S01PriceService priceService = new S01PriceService(tran);

        // // DAOクラス取得
        // S04W001TDao workDao = getS04w001tEntityDao();

        // 新規受注ワークより受注伝票・明細を生成する
        List<S04W001TEntityEx> shinkiList = getJuchuDao().getShinkiWork(fileVO, false);
        for (int i = 0; i < shinkiList.size(); i++) {
            S04W001TEntity model = shinkiList.get(i);
            List<JuchuMeisaiVO> yoyakuMeisaiList = null;

            // 得意先単位の初回時処理
            if (tokuisakicd == null || !tokuisakicd.equals(model.getTokuisakicd())) {
                tokuisakicd = model.getTokuisakicd();
                isFirst = true;
                isSuspend = false;
                /* 2017-11-20 ibis)inoue add begin */
                isLast = false;
                yoyakuCount = 0;
                /* 2017-11-20 ibis)inoue add end */

                // 得意先関係VOの取得
                tokuiRelation = tokuisakiService.getTokuisakiRelation(model.getApkihoncd(),
                    model.getShopcd(), model.getTokuisakicd(), false);

                // 受注停止チェック
                if (CommonConstants.FLG_ON
                    .equals(tokuiRelation.getTokuisakiModel().getAcptteishiflg())) {
                    isSuspend = true;
                }
            }

            /* 2017-11-20 ibis)inoue add begin */
            // 得意先単位の最終伝票フラグ取得
            if (i != shinkiList.size() - 1) {
                S04W001TEntity modelNext = shinkiList.get(i + 1);
                if (!tokuisakicd.equals(modelNext.getTokuisakicd())) {
                    isLast = true;
                }
            } else if (i == shinkiList.size() - 1) {
                isLast = true;
            }
            /* 2017-11-20 ibis)inoue add end */

            // 新規受注取込ワーク、得意先情報より受注伝票作成
            JuchuDenpyoVO denVo = createDenVoFromShinkiWork(model, shopModel);
            result.add(denVo);

            // 価格情報VO取得
            // ■:1 Base)Arai 2014-01-08 消費税基準日変更対応
            PriceInfoVo priceVo = priceService.getPriceInfo(fileVO.getApkihoncd(),
                fileVO.getShopcd(), denVo.getDlvryytidd());
            // ■:2 Base)Arai 2014-01-08 消費税基準日変更対応

            /* 2017-11-20 ibis)inoue add begin */
            // 受注予約明細取込処理
            JuchuDenpyoVO yoyakuDenVo = getJuchuDenpyoYoyaku(denVo.getApkihoncd(),
                denVo.getShopcd(), denVo.getTokuisakicd(), denVo.getDlvryytidd());
            if (yoyakuDenVo != null) {
                yoyakuDenVo.setDlvryytidd(denVo.getDlvryytidd());
                yoyakuMeisaiList = getJuchuMeisai(true, yoyakuDenVo);

                // セット構成商品は一旦削除する
                for (int j = yoyakuMeisaiList.size() - 1; 0 <= j; j--) {
                    JuchuMeisaiVO meiVo = (JuchuMeisaiVO) yoyakuMeisaiList.get(j);
                    if (CommonConstants.FLG_OFF.equals(meiVo.getNyuryokumeisaiflg())) {
                        yoyakuMeisaiList.remove(j);
                    }
                }

                // 予約残回数 >= 得意先ごとの伝票数の場合のみ受注予約情報を繰り越す
                for (int k = yoyakuMeisaiList.size() - 1; 0 <= k; k--) {
                    JuchuMeisaiVO meiVo = (JuchuMeisaiVO) yoyakuMeisaiList.get(k);
                    if (meiVo.getZankaisu() != null) {
                        if (meiVo.getZankaisu() < yoyakuCount + 1) {
                            yoyakuMeisaiList.remove(k);
                        }
                    }
                }
                // ADD START 20181219 TJS)takio OM-00330
                denVo.setPrntmemon1(yoyakuDenVo.getPrntmemon1());
                denVo.setPrntmemon2(yoyakuDenVo.getPrntmemon2());
                // ADD E N D 20181219 TJS)takio
                denVo.setDeletingDenVo(yoyakuDenVo);
                yoyakuCount++;
            }
            /* 2017-11-20 ibis)inoue add end */

            // 受注停止チェック（エラーの場合は同一得意先に紐づく全ての伝票登録を行わない）
            if (isSuspend) {
                updateShinkiWork(model, dbManagementSession,
                    FileShinkiJuchuVO.ERROR_SHINKI_SUSPEND);
                continue;

                // 作成元管理区分、作成元管理番号の重複チェック（エラーの場合は伝票登録を行わない）
            } else if (getJuchuDao().getDuplicationCount(denVo) != 0) {
                updateShinkiWork(model, dbManagementSession,
                    FileShinkiJuchuVO.ERROR_SHINKI_CONFLICT);
                continue;

                // 得意先単位の初回のみ受注予約取込、配布物取込処理を行う
            } else if (isFirst) {

                // 配布物取込処理
                List<S02M033TEntityEx> haifuList = haifubutsuService.getTokuisakiHaifubutsu(
                    denVo.getApkihoncd(), denVo.getShopcd(), denVo.getTokuisakicd(), null,
                    denVo.getDlvryytidd(), CommonConstants.KBN_HAIFUSTS_MINOHIN);
                for (int j = 0; j < haifuList.size(); j++) {
                    if (j < haifuList.size()) {
                        S02M033TEntityEx haifu = haifuList.get(j);
                        if (j == 0) {
                            // ■:1 IBIS)Shinoda 2017-06-29 add begin
                            denVo.setHaifubutsuapkihoncd1(haifu.getApkihoncd());
                            denVo.setHaifubutsushopcd1(haifu.getShopcd());
                            // ■:1 IBIS)Shinoda 2017-06-29 add end
                            denVo.setHaifubutsucd1(haifu.getHaifubutsucd());
                            denVo.setHaifubutsunm1(haifu.getHaifubutsunm());
                        } else if (j == 1) {
                            // ■:1 IBIS)Shinoda 2017-06-29 add begin
                            denVo.setHaifubutsuapkihoncd2(haifu.getApkihoncd());
                            denVo.setHaifubutsushopcd2(haifu.getShopcd());
                            // ■:1 IBIS)Shinoda 2017-06-29 add end
                            denVo.setHaifubutsucd2(haifu.getHaifubutsucd());
                            denVo.setHaifubutsunm2(haifu.getHaifubutsunm());
                            // ■:1 IBIS)Shinoda 2017-06-29 add begin
                        } else if (j == 2) {
                            denVo.setHaifubutsuapkihoncd3(haifu.getApkihoncd());
                            denVo.setHaifubutsushopcd3(haifu.getShopcd());
                            denVo.setHaifubutsucd3(haifu.getHaifubutsucd());
                            denVo.setHaifubutsunm3(haifu.getHaifubutsunm());
                        } else if (j == 3) {
                            denVo.setHaifubutsuapkihoncd4(haifu.getApkihoncd());
                            denVo.setHaifubutsushopcd4(haifu.getShopcd());
                            denVo.setHaifubutsucd4(haifu.getHaifubutsucd());
                            denVo.setHaifubutsunm4(haifu.getHaifubutsunm());
                            // ■:1 IBIS)Shinoda 2017-06-29 add end
                        }
                    }
                }
            }

            // 受注明細情報作成
            List<JuchuMeisaiVO> meisaiList =
                createMeiVoFromShinkiWork(denVo, model, priceVo, fileVO);
            if (yoyakuMeisaiList != null) {
                yoyakuMeisaiList.addAll(meisaiList);
                meisaiList = yoyakuMeisaiList;
            }
            // ■:1 Base)Arai 2014-01-08 消費税基準日変更対応
            // ■:1 Isac)Shiba 2021-01-20 価格改定対応
            PriceInfoVo priceInfoVo =
                new PriceInfoVo(shopModel, denVo.getDlvryytidd(), denVo.getAcptdd());
            denVo
                .setMeisaiList(expandJuchuMeisai(denVo, meisaiList, priceInfoVo, shopModel, false));

            // インボイス対応 : 受注金額・税をセットする
            String ippanTaxRate = priceService.getShohiTaxString(CommonConstants.KBN_TAX_KAZEI,
                denVo.getDlvryytidd());
            String keigenTaxRate = priceService.getShohiTaxString(CommonConstants.KBN_TAX_KEIGEN,
                denVo.getDlvryytidd());
            denVo.calcEachAcptkin(priceInfoVo, ippanTaxRate, keigenTaxRate);

            // インボイス対応 : 納品金額・納品税金額をセットする
            denVo.setInvoiceNohinTaxParams();

            // インボイス対応 : 単体伝票締めフラグ
            denVo.setSingldnpyflg("0");

            // ■:2 Isac)Shiba 2021-01-20 価格改定対応
            // ■:2 Base)Arai 2014-01-08 消費税基準日変更対応
            // インボイス対応
            if (shopModel.getInvoicshikoymd().compareTo(denVo.getDlvryytidd()) <= 0) {
                // インボイス実施後
                denVo.calcAcptkinAfterInvoice();
            } else {
                // インボイス実施前
                denVo.calcAcptkin();
            }

            // 与信限度額のチェック（エラーの場合も伝票登録を行う）
            if (!TokuisakiService.checkYoshin(tokuiRelation.getTokuisakiModel(),
                tokuiRelation.getJissekiModel(), denVo.getAcptkin().toString())) {
                updateShinkiWork(model, dbManagementSession, FileShinkiJuchuVO.ERROR_SHINKI_CREDIT);
            }

            // ■:1 Isac)Shiba 2012-03-28
            // 受注受付経路区分の編集
            denVo.setAcptrcptkeirokbn(getAcptrcptkeirokbn(denVo.getSksmotokanrikbn()));
            // ■:2 Isac)Shiba 2012-03-28

            // 採番処理
            denVo.setAcptdnpynoAtOnce(
                saibanService.getSlipNoJuchu(denVo.getApkihoncd(), denVo.getShopcd()));

            // 受注伝票・明細更新処理
            insertJuchuDenpyo(denVo, false, dbManagementSession);
            /* 2019-05-21 Pkut)Yang 軽減税率対応 add begin */
            for (int k = 0; k < denVo.getMeisaiList().size(); k++) {
                JuchuMeisaiVO meiVo = (JuchuMeisaiVO) denVo.getMeisaiList().get(k);
                if (("4".equals(meiVo.getTaxkbn()) || "5".equals(meiVo.getTaxkbn()))
                    && "20190930".compareTo(denVo.getDlvryytidd()) >= 0) {
                    meiVo.setTaxkbn("1");
                }
            }
            /* 2019-05-21 Pkut)Yang 軽減税率対応 add end */
            insertJuchuMeisai(denVo, denVo.getMeisaiList(), dbManagementSession);

            /* 2018-05-28 BASE)Chokenkun add begin 障害対応OM-0005 */
            // ワークテーブルに受注伝票番号を更新する
            model.setAcptdnpyno(denVo.getAcptdnpyno());
            updateShinkiWork(model, dbManagementSession, null);
            /* 2018-05-28 BASE)Chokenkun add end 障害対応OM-0005 */

            /* 2017-11-20 ibis)inoue update begin */
            // 受注予約キャンセル処理（得意先単位）
            if (isLast && yoyakuCount > 0) {
                // if (denVo.getDeletingDenVo() != null) {
                /* 2017-11-20 ibis)inoue update end */
                /* 2018-05-22 IBIS)Shinoda upd start */
                processJuchuCancel(null, TYPE_UPATE_YOYAKUINSERT, true, true,
                    denVo.getDeletingDenVo(), null, yoyakuCount, dbManagementSession);
                /* 2018-05-22 IBIS)Shinoda upd end */
            }

            // 配布物更新処理（得意先単位の初回のみ）
            // ■:1 IBIS)Shinoda 2017-06-29 upd begin
            if (!StringUtil.isEmpty(denVo.getHaifubutsucd1())
                || !StringUtil.isEmpty(denVo.getHaifubutsucd2())
                || !StringUtil.isEmpty(denVo.getHaifubutsucd3())
                || !StringUtil.isEmpty(denVo.getHaifubutsucd4())) {
                // ■:1 IBIS)Shinoda 2017-06-29 upd end
                updateHaifubutsu(false, haifubutsuService, denVo, denVo.getMeisaiList(),
                    dbManagementSession);
            }

            /* 2017-09-21 ibis)inoue add begin */
            if ("1".equals(tokuiRelation.getTokuisakiModel().getTokuisakikbn())) {
                if (CommonConstants.KBN_JUTAKUDLVTOKUI_NAKOKYAKU
                    .equals(tokuiRelation.getTokuisakiModel().getJutakudlvrycstkbn())
                    || CommonConstants.JUTAKUDLVRYCSTKBN_4
                        .equals(tokuiRelation.getTokuisakiModel().getJutakudlvrycstkbn())) {
                    // 「本部受注データ連携（バッチ）」に委託会社の受注配達情報を渡し、受託会社の受注配達情報を作成
                    getJuchuDao().honbuJuchuDtRenkei(denVo.getApkihoncd(), denVo.getShopcd(),
                        denVo.getAcptdnpyno(), "0", CommonConstants.HONBURENKEI_SHINKI);
                }
            }
            /* 2017-09-21 ibis)inoue add end */

            // 初回・最終受注伝票番号採取
            if (StringUtil.isEmpty(fileVO.getAcptdennostart())) {
                fileVO.setAcptdennostart(denVo.getAcptdnpyno());
            }
            fileVO.setAcptdennoend(denVo.getAcptdnpyno());

            isFirst = false;
        }
    }

    //
    /**
     * 受注明細リストのセット商品を展開します。
     * 
     * @param denVo 受注伝票VO
     * @param meisaiList 受注明細VOリスト
     * @param priceInfoVo 価格情報VO
     * @return List 受注明細VOリスト
     * @throws ApplicationException アプリケーション例外時
     * @throws SystemException システム例外時
     */
    // 2021/01/20 価格改定対応 ISAC)Shiba START
    public List<JuchuMeisaiVO> expandJuchuMeisai(JuchuDenpyoVO denVo,
        List<JuchuMeisaiVO> meisaiList, PriceInfoVo priceInfoVo, S02M046TEntity shopModel,
        boolean useItaku) {
        // 2021/01/20 価格改定対応 ISAC)Shiba END
        JuchuSumVo sumVo = new JuchuSumVo();
        ArrayList<JuchuMeisaiVO> subList = new ArrayList<JuchuMeisaiVO>();

        // サービスクラス取得
        // S02ShohinService s02ShohinService = new S02ShohinService(tran);
        /* 2017-12-13 Base)Chokenkun add begin 障害対応(No.IT0028) */
        // S02Service s02Service = new S02Service(tran);
        /* 2017-12-13 Base)Chokenkun add end 障害対応(No.IT0028) */

        // 受注関係VO生成（伝票情報のみセット）
        JuchuRelationVo relation = getJuchuRelation(denVo);
        // ■:1 Base)Arai 2014-01-24 セット商品消費税補正対応
        String zeiChoseiShncd = null;
        // ■:2 Base)Arai 2014-01-24 セット商品消費税補正対応
        // ■:1 Base)Arai 2014-02-05 セット商品消費税補正対応
        PriceInfoVo juchuPriceInfoVo = null;
        // 2021/01/20 価格改定対応 ISAC)Shiba START
        if (useItaku) {
            // 2021/01/20 価格改定対応 ISAC)Shiba END
            juchuPriceInfoVo = new PriceInfoVo(shopModel, denVo.getDlvryytidd(), denVo.getAcptdd());
        }
        // ■:2 Base)Arai 2014-02-05 セット商品消費税補正対応
        /* 2017-12-13 Base)Chokenkun add begin 障害対応(No.IT0028) */
        PriceInfoVo aitePriceInfoVo = null;
        S02M046TEntity aiteShop =
            commonService.getShopModel(denVo.getItakuaiteapkihoncd(), denVo.getItakuaiteshopcd());
        // 2021/01/20 価格改定対応 ISAC)Shiba START
        if (useItaku && aiteShop != null) {
            // 2021/01/20 価格改定対応 ISAC)Shiba END
            aitePriceInfoVo = new PriceInfoVo(aiteShop, denVo.getDlvryytidd(), denVo.getAcptdd());
        }
        /* 2017-12-13 Base)Chokenkun add end 障害対応(No.IT0028) */

        // 2020/12/24 価格改定対応 ISAC)Shiba START
        // 委受託の場合相手得意先を取得するために相手受注伝票を参照しておく
        JuchuDenpyoVO aiteDenVo = null;
        if (!StringUtil.isEmpty(denVo.getItakuaiteacptdnpyno())) {
            S04T001TDao aiteJuchuDao = this.getS04t001tEntityDao();
            S04T001TEntityKey aiteJuchuKey = new S04T001TEntityKey();
            aiteJuchuKey.setApkihoncd(denVo.getItakuaiteapkihoncd());
            aiteJuchuKey.setShopcd(denVo.getItakuaiteshopcd());
            aiteJuchuKey.setAcptdnpyno(denVo.getItakuaiteacptdnpyno());
            aiteJuchuKey.setEdano(denVo.getItakuaiteedano());
            S04T001TEntity aiteJuchuModel =
                (S04T001TEntity) aiteJuchuDao.selectByPrimaryKey(aiteJuchuKey);
            aiteDenVo = new JuchuDenpyoVO();
            aiteDenVo.setApkihoncd(aiteJuchuModel.getApkihoncd());
            aiteDenVo.setShopcd(aiteJuchuModel.getShopcd());
            aiteDenVo.setTokuisakicd(aiteJuchuModel.getTokuisakicd());
            aiteDenVo.setDlvryytidd(denVo.getDlvryytidd());
            aiteDenVo.setDlvrykbn(aiteJuchuModel.getDlvrykbn());
        }
        // 2020/12/24 価格改定対応 ISAC)Shiba END

        // 明細行単位に処理を行う
        for (int i = 0; i < meisaiList.size(); i++) {
            JuchuMeisaiVO meiVo = (JuchuMeisaiVO) meisaiList.get(i);
            // 軽減税率対応 ryu 2019-05-21 Start
            // セット商品の受注単価消費税金額
            long acptuntshohiTaxkinSetShohi = 0;
            // セット商品の受注消費税金額
            long aacptShohitaxkinSetShohi = 0;
            // セット商品の委託会社受注単価消費税金額
            long itakucmpnyAcptuntshohiTaxkinSetShohi = 0;
            // セット商品の委託会社受注消費税金額
            long itakucmpnyAcptshohiTaxkinSetShohi = 0;
            // セット商品の納品単価消費税金額
            long nohinuntShohitaxkinSetShohi = 0;
            // セット商品の納品消費税金額
            long nohinShohitaxkinSetShohi = 0;

            // 軽減税率対応 ryu 2019-05-21 End

            // 商品マスタビュー情報取得
            S02V001TEntity shohinModel = shohinService.getShohinModel(denVo.getApkihoncd(),
                denVo.getShopcd(), meiVo.getShncd());

            // プロパティ値のセット
            meiVo.setUpdateProperties(relation, shohinModel);
            meiVo.setAcptdnpymeisaino(i + 1);
            meiVo.setNyuryokumeisaiflg(CommonConstants.FLG_ON);
            // ■:1 Isac)hikoso 2007-06-26
            if (denVo.getNohinkin() == null) {
                meiVo.setMeisaikbn(CommonConstants.KBN_MEISAI_SHOHIN);
            }
            // ■:2 Isac)hikoso 2007-06-26
            // 本数、金額、サーバー交換台数の集計（セット商品は対象外）
            if (meiVo.isCountable()) {
                sumVo.addAll(meiVo, meiVo.getRelationVo());
            }

            // 入力行がセット商品の場合は展開する
            if (CommonConstants.FLG_OFF.equals(meiVo.getTanpinmeisaiflg())) {
                meiVo.setStshncd(meiVo.getShncd());
                // ■:1 Base)Arai 2014-01-24 セット商品消費税補正対応
                BigDecimal sumAcptTaxKin = new BigDecimal(0);
                BigDecimal sumNohinTaxKin = new BigDecimal(0);
                /* 2017-12-13 Base)Chokenkun add begin 障害対応(No.IT0028) */
                BigDecimal sumAiteTaxKin = new BigDecimal(0);
                /* 2017-12-13 Base)Chokenkun add end 障害対応(No.IT0028) */
                // 消費税調整の商品コードをシステム管理マスタから取得する
                if (zeiChoseiShncd == null) {
                    List<S02M006TEntity> S02m006tEntityModelList =
                        getCommonDao().getSystemKanriMeisaiMoji(CommonConstants.APCD_000,
                            CommonConstants.SYS_SHOPCD_000, CommonConstants.SYS_SETTEINO_166);
                    for (Iterator<S02M006TEntity> it = S02m006tEntityModelList.iterator(); it
                        .hasNext();) {
                        S02M006TEntity model = (S02M006TEntity) it.next();
                        zeiChoseiShncd = model.getMojisetteiatai();
                        break;
                    }
                }

                // 受注か配達実績かを判断
                boolean isHaitatsuJisseki = false;
                // ■:2 Base)Arai 2014-01-24 セット商品消費税補正対応

                // 構成品リスト取得
                List<S02M008TEntityEx> setList = shohinService
                    .getSetShohinList(meiVo.getApkihoncd(), meiVo.getShopcd(), meiVo.getShncd());
                /* 2017-12-13 Base)Chokenkun add begin 障害対応(No.IT0028) */
                // 委託相手構成品リスト取得
                List<S02M008TEntityEx> aiteList = null;
                if (meiVo.getItakuaiteacptdnpymeisaino() != null) {
                    aiteList = shohinService.getSetShohinList(meiVo.getItakuaiteapkihoncd(),
                        meiVo.getItakuaiteshopcd(), meiVo.getShncd());
                }
                /* 2017-12-13 Base)Chokenkun add end 障害対応(No.IT0028) */
                // 軽減税率対応 ryu 2019-05-21 Start
                String juchuOyaTaxkbn = meiVo.getTaxkbn();
                String nohinOyaTaxkbn = meiVo.getTaxkbn();
                if (("5".equals(meiVo.getTaxkbn()))
                    && "20190930".compareTo(denVo.getDlvryytidd()) >= 0) {
                    juchuOyaTaxkbn = "1";
                }
                if (("5".equals(meiVo.getTaxkbn()))
                    && "20190930".compareTo(priceInfoVo.getTargetDate()) >= 0) {
                    nohinOyaTaxkbn = "1";
                }
                // 軽減税率対応 ryu 2019-05-21 End
                // 構成品リスト生成
                for (int j = 0; j < setList.size(); j++) {
                    S02M008TEntity childModel = (S02M008TEntity) setList.get(j);
                    JuchuMeisaiVO childVo = new JuchuMeisaiVO();

                    // 商品マスタビュー情報取得
                    S02V001TEntity shohinModelKosei = shohinService.getShohinModel(
                        denVo.getApkihoncd(), denVo.getShopcd(), childModel.getKoseishncd());

                    /* 2017-12-13 Base)Chokenkun add begin 障害対応(No.IT0028) */
                    S02M008TEntity aiteChild = null;
                    S02V001TEntity aiteModelKosei = null;
                    if (aiteList != null)
                        aiteChild = (S02M008TEntity) aiteList.get(j);
                    // 相手商品マスタビュー情報取得
                    if (aiteChild != null)
                        aiteModelKosei = shohinService.getShohinModel(denVo.getItakuaiteapkihoncd(),
                            denVo.getItakuaiteshopcd(), aiteChild.getKoseishncd());
                    /* 2017-12-13 Base)Chokenkun add end 障害対応(No.IT0028) */

                    // プロパティ値のセット
                    // meiVo.readProperties(childVo);
                    BeanUtils.copyProperties(meiVo, childVo);

                    childVo.setAcptdnpymeisaiedano((short) (j + 1));
                    /* 2017-12-13 Base)Chokenkun add begin 障害対応(No.IT0028) */
                    if (meiVo.getItakuaiteacptdnpymeisaino() != null) {
                        childVo.setItakuaiteacptdnpymeisaiedano((short) (j + 1)); // 委託相手明細枝番号
                    }
                    /* 2017-12-13 Base)Chokenkun add end 障害対応(No.IT0028) */
                    childVo.setShncd(childModel.getKoseishncd());
                    childVo.setKatasiki(null);
                    childVo.setStshncd(meiVo.getShncd());
                    childVo.setNyuryokumeisaiflg(CommonConstants.FLG_OFF);
                    childVo.setTanpinmeisaiflg(CommonConstants.FLG_ON);
                    childVo.setUpdateProperties(relation, shohinModelKosei);
                    // ■:1 Base)Arai 2014-02-05 消費税対応
                    // 親の消費税区分を引き継ぐ
                    // ■:2 Base)Arai 2014-02-05 消費税対応

                    // 軽減税率対応 ryu 2019-05-21 Start
                    // セット商品の税区分が「1：課税」または「4：課税軽減」の場合、構成商品をセット商品の税区分に設定する。
                    String juchuChildTaxkbn = childVo.getTaxkbn();
                    String nohinChildTaxkbn = childVo.getTaxkbn();
                    if (!CommonConstants.KBN_TAX_KOMEISAI.equals(juchuOyaTaxkbn)) {
                        juchuChildTaxkbn = juchuOyaTaxkbn;
                    } else {
                        juchuChildTaxkbn = shohinModelKosei.getTaxkbn();
                    }
                    if (!CommonConstants.KBN_TAX_KOMEISAI.equals(nohinOyaTaxkbn)) {
                        nohinChildTaxkbn = nohinOyaTaxkbn;
                    } else {
                        nohinChildTaxkbn = shohinModelKosei.getTaxkbn();
                    }
                    // 軽減税率対応 ryu 2019-05-21 End

                    // 受注数量のセット
                    if (meiVo.getAcptsuryo() != null && childModel.getSuryo() != null) {
                        childVo.setAcptsuryo(new BigDecimal(
                            meiVo.getAcptsuryo().multiply(childModel.getSuryo()).toString()));
                    } else if (childModel.getSuryo() != null) {
                        childVo.setAcptsuryo(childModel.getSuryo());
                    }
                    // 納品数量のセット
                    if (meiVo.getNohinsuryo() != null && childModel.getSuryo() != null) {
                        childVo.setNohinsuryo(new BigDecimal(
                            meiVo.getNohinsuryo().multiply(childModel.getSuryo()).toString()));
                    } else if (meiVo.getNohinsuryo() == null) {
                        childVo.setNohinsuryo(new BigDecimal("0"));
                    }

                    // 標準単価のセット
                    // 2020/11/20 価格改定対応 ISAC)金谷 START
                    childVo.setHyojununt(new BigDecimal(S02V001TEntityDaoEx.getCurrentHyojunprice(
                        shohinModelKosei, denVo.getDlvrykbn(),
                        isHaitatsuJisseki ? denVo.getDlvrydd() : denVo.getDlvryytidd())));
                    // 2020/11/20 価格改定対応 ISAC)金谷 END

                    // 親商品が有料の場合は構成商品の計上区分をセットし、金額を算出する
                    if (JuchuRelationVo.isYuryo(meiVo.getKeijokbn())) {

                        // 2020/11/18 価格改定対応 ISAC)金谷 START
                        // 得意先別セット商品別単価の適用期間内の場合は受注単価を得意先別セット商品別単価にする
                        JuchuMeisaiVO setVo = null;
                        String tokuisakisetunt = null;
                        setVo = shohinService.getSetShohinTankaList(childModel, denVo,
                            denVo.getDlvryytidd());
                        tokuisakisetunt =
                            setVo.getTokuisakiSetTanka(denVo.getDlvrykbn(), denVo.getDlvryytidd());
                        if (tokuisakisetunt != null) {
                            childVo.setAcptunt(new BigDecimal(tokuisakisetunt));

                        } else {
                            // 2020/11/18 価格改定対応 ISAC)金谷 END

                            childVo.setAcptunt(new BigDecimal(BusinessUtil.getDlvryValue(
                                denVo.getDlvrykbn(), childModel.getDlvryprice().toString(),
                                childModel.getMochikaeriprice().toString())));

                            // 2020/11/18 価格改定対応 ISAC)金谷 START
                        }
                        // 2020/11/18 価格改定対応 ISAC)金谷 END

                        // 2020/12/24 価格改定対応 ISAC)Shiba START
                        if (childVo.getNohinsuryo().compareTo(BigDecimal.ZERO) == 0) {
                            childVo.setNohinunt(new BigDecimal("0"));
                        } else {
                            setVo = shohinService.getSetShohinTankaList(childModel, denVo,
                                denVo.getDlvrydd());
                            tokuisakisetunt =
                                setVo.getTokuisakiSetTanka(denVo.getDlvrykbn(), denVo.getDlvrydd());
                            if (tokuisakisetunt != null) {
                                childVo.setNohinunt(new BigDecimal(tokuisakisetunt));
                            } else {
                                childVo.setNohinunt(new BigDecimal(BusinessUtil.getDlvryValue(
                                    denVo.getDlvrykbn(), childModel.getDlvryprice().toString(),
                                    childModel.getMochikaeriprice().toString())));
                            }
                        }
                        // 2020/12/24 価格改定対応 ISAC)Shiba END
                        childVo.setKeijokbn(childModel.getKeijokbn());

                        // 2021/01/20 価格改定対応 ISAC)Shiba START
                        if (useItaku) {
                            // 2021/01/20 価格改定対応 ISAC)Shiba END
                            /* 2017-12-13 Base)Chokenkun add begin 障害対応(No.IT0028) */
                            if (aiteChild != null) {
                                childVo.setItakucmpnyacptunt(BusinessUtil.getDlvryValue(
                                    denVo.getDlvrykbn(), aiteChild.getDlvryprice().toString(),
                                    aiteChild.getMochikaeriprice().toString()));
                            }
                            /* 2017-12-13 Base)Chokenkun add end 障害対応(No.IT0028) */
                            // 2020/12/24 価格改定対応 ISAC)Shiba START
                            if (aiteChild != null && aiteDenVo != null) {
                                // 委託元の得意先別セット商品別単価の適用期間内の場合は得意先別セット商品単価を使用
                                JuchuMeisaiVO aiteSetVo = shohinService.getSetShohinTankaList(
                                    childModel, aiteDenVo, aiteDenVo.getDlvryytidd());
                                String aiteTokuisakiSetUnt =
                                    getTokuisakiSetTanka(aiteDenVo, aiteSetVo);
                                if (aiteTokuisakiSetUnt != null) {
                                    childVo.setItakucmpnyacptunt(aiteTokuisakiSetUnt);
                                }
                            }
                            // 2021/01/20 価格改定対応 ISAC)Shiba START
                        }
                        // 2021/01/20 価格改定対応 ISAC)Shiba END
                        // 2020/12/24 価格改定対応 ISAC)Shiba End

                        // 価格情報算出
                        if (priceInfoVo != null) {
                            // 2020/12/24 価格改定対応 ISAC)Shiba START
                            String tanka = childVo.getAcptunt().toString();
                            // 2020/12/24 価格改定対応 ISAC)Shiba END


                            String[] priceListAcpt = null;
                            if (juchuPriceInfoVo == null) {
                                // 軽減税率対応 ryu 2019-05-21 Start
                                priceListAcpt =
                                    priceService
                                        .getPriceList(priceInfoVo, tanka,
                                            childVo.getAcptsuryo().toString(),
                                            JuchuMeisaiVO.getShnTaxKbnByKeijoKbn(
                                                childVo.getKeijokbn(), juchuChildTaxkbn),
                                            denVo.getTokuisakikbn());
                                // 軽減税率対応 ryu 2019-05-21 End
                                childVo.setAcptuntshohitaxkin(Long.valueOf(priceListAcpt[0]));
                                // インボイス対応 : インボイス施行日以降、セット商品の子明細は0円固定
                                if (shopModel.getInvoicshikoymd()
                                    .compareTo(denVo.getDlvryytidd()) <= 0) {
                                    // インボイス実施後
                                    childVo.setAcptshohitaxkin(0L);
                                } else {
                                    // インボイス実施前
                                    childVo.setAcptshohitaxkin(Long.valueOf(priceListAcpt[1]));
                                }
                                childVo.setAcptkin(Long.valueOf(priceListAcpt[2]));
                            } else {
                                // 軽減税率対応 ryu 2019-05-21 Start
                                // 配達実績登録の場合は受注金額は配達予定日で計算する
                                priceListAcpt =
                                    priceService
                                        .getPriceList(juchuPriceInfoVo, tanka,
                                            childVo.getAcptsuryo().toString(),
                                            JuchuMeisaiVO.getShnTaxKbnByKeijoKbn(
                                                childVo.getKeijokbn(), juchuChildTaxkbn),
                                            denVo.getTokuisakikbn());
                                // 軽減税率対応 ryu 2019-05-21 End
                                childVo.setAcptuntshohitaxkin(Long.valueOf(priceListAcpt[0]));
                                // インボイス対応 : インボイス施行日以降、セット商品の子明細は0円固定
                                if (shopModel.getInvoicshikoymd()
                                    .compareTo(denVo.getDlvryytidd()) <= 0) {
                                    // インボイス実施後
                                    childVo.setAcptshohitaxkin(0L);
                                } else {
                                    // インボイス実施前
                                    childVo.setAcptshohitaxkin(Long.valueOf(priceListAcpt[1]));
                                }
                                childVo.setAcptkin(Long.valueOf(priceListAcpt[2]));

                                /* 2017-12-13 Base)Chokenkun add begin 障害対応(No.IT0028) */
                                if (aiteChild != null && aiteModelKosei != null
                                    && aitePriceInfoVo != null) {
                                    // 2020/11/18 価格改定対応 ISAC)Shiba START
                                    String aiteTanka = childVo.getItakucmpnyacptunt().toString();
                                    // 2020/11/18 価格改定対応 ISAC)Shiba END

                                    // 軽減税率対応 ryu 2019-05-21 Start
                                    String[] priceListAite =
                                        priceService.getPriceList(aitePriceInfoVo, aiteTanka,
                                            childVo.getAcptsuryo().toString(),
                                            JuchuMeisaiVO.getShnTaxKbnByKeijoKbn(
                                                childVo.getKeijokbn(), juchuChildTaxkbn),
                                            CommonConstants.KBN_TOKUISAKI_KOKUAKU);
                                    // 軽減税率対応 ryu 2019-05-21 End
                                    childVo.setItakucmpnyacptuntshohitaxkin(priceListAite[0]);
                                    childVo.setItakucmpnyacptshohitaxkin(priceListAite[1]);
                                }
                                /* 2017-12-13 Base)Chokenkun add end 障害対応(No.IT0028) */
                            }
                            // 軽減税率対応 ryu 2019-05-21 Start
                            // 2020/12/24 価格改定対応 ISAC)Shiba START
                            tanka = childVo.getNohinunt().toString();
                            // 2020/12/24 価格改定対応 ISAC)Shiba END
                            String[] priceListNohin = priceService.getPriceList(priceInfoVo, tanka,
                                (childVo.getNohinsuryo().compareTo(BigDecimal.ZERO) == 0) ? ""
                                    : childVo.getNohinsuryo().toString(),
                                nohinChildTaxkbn, denVo.getTokuisakikbn());
                            // 軽減税率対応 ryu 2019-05-21 End

                            childVo.setNohinuntshohitaxkin(Long.valueOf(priceListNohin[0]));
                            // インボイス対応 : インボイス施行日以降、セット商品の子明細は0円固定
                            if (shopModel.getInvoicshikoymd()
                                .compareTo(denVo.getDlvryytidd()) <= 0) {
                                // インボイス実施後
                                childVo.setNohinshohitaxkin(0L);
                            } else {
                                // インボイス実施前
                                childVo.setNohinshohitaxkin(Long.valueOf(priceListNohin[1]));
                            }
                            childVo.setNohinkin(Long.valueOf(priceListNohin[2]));

                            // ■:1 Base)Arai 2014-02-13 消費税対応
                            // 軽減税率対応 ryu 2019-05-21 Start
                            if (CommonConstants.FLG_ON.equals(shohinModel.getTaxsetteikbn())
                                && (CommonConstants.KBN_TAX_HIKAZEI.equals(childVo.getTaxkbn())
                                    || CommonConstants.KBN_TAX_HUKAZEI
                                        .equals(childVo.getTaxkbn()))) {

                                // 税設定するで税区分が課税以外は0円をセット
                                // 軽減税率対応 ryu 2019-05-21 End
                                // ■:2 Base)Arai 2014-02-13 消費税対応
                                childVo.setAcptuntshohitaxkin(Long.valueOf("0"));
                                childVo.setAcptshohitaxkin(Long.valueOf("0"));
                                childVo.setNohinuntshohitaxkin(Long.valueOf("0"));
                                childVo.setNohinshohitaxkin(Long.valueOf("0"));
                            }
                            // ■:2 Base)Arai 2014-02-05 消費税対応

                        }

                        // 親商品の計上区分＝無料の場合は計上区分を親商品のものにして金額を０にする
                    } else {
                        childVo.setKeijokbn(meiVo.getKeijokbn());
                        childVo.setAcptunt(new BigDecimal("0"));
                        childVo.setAcptuntshohitaxkin(Long.valueOf("0"));
                        childVo.setAcptshohitaxkin(Long.valueOf("0"));
                        childVo.setAcptkin(Long.valueOf("0"));
                        childVo.setNohinunt(new BigDecimal("0"));
                        childVo.setNohinuntshohitaxkin(Long.valueOf("0"));
                        childVo.setNohinshohitaxkin(Long.valueOf("0"));
                        childVo.setNohinkin(Long.valueOf("0"));
                    }

                    // 軽減税率対応 ryu 2019-05-21 Start

                    // セット商品の税区分が5：子明細の場合はセット商品の税金を再計算する
                    if (CommonConstants.KBN_TAX_KOMEISAI.equals(juchuOyaTaxkbn)
                        || CommonConstants.KBN_TAX_KOMEISAI.equals(nohinOyaTaxkbn)) {
                        // 構成商品数量
                        BigDecimal suryo = childModel.getSuryo();

                        // 委託会社の場合
                        if (juchuPriceInfoVo != null && aiteChild != null && aiteModelKosei != null
                            && aitePriceInfoVo != null) {

                            // 構成商品の委託会社受注単価消費税金額
                            BigDecimal itakucmpnyAcptuntshohitaxkin =

                                new BigDecimal(childVo.getItakucmpnyacptuntshohitaxkin());
                            // 構成商品の委託会社受注消費税金額
                            BigDecimal itakucmpnyAcptshohitaxkin =
                                new BigDecimal(childVo.getItakucmpnyacptshohitaxkin());

                            // セット商品の委託会社受注単価消費税金額を算出する。
                            itakucmpnyAcptuntshohiTaxkinSetShohi +=
                                itakucmpnyAcptuntshohitaxkin.multiply(suryo).longValue();
                            // セット商品の委託会社受注消費税金額を算出する。
                            itakucmpnyAcptshohiTaxkinSetShohi +=
                                itakucmpnyAcptshohitaxkin.longValue();

                        }

                        // 構成商品の受注単価消費税金額
                        BigDecimal acptuntShohitaxkin =
                            new BigDecimal(childVo.getAcptuntshohitaxkin());
                        // 構成商品の受注消費税金額
                        BigDecimal acptShohitaxkin = new BigDecimal(childVo.getAcptshohitaxkin());

                        // セット商品の受注単価消費税金額を算出する。
                        acptuntshohiTaxkinSetShohi +=
                            acptuntShohitaxkin.multiply(suryo).longValue();
                        // セット商品の受注消費税金額を算出する。
                        aacptShohitaxkinSetShohi += acptShohitaxkin.longValue();

                        // 構成商品の納品単価消費税金額
                        BigDecimal nohinuntShohitaxkin =
                            new BigDecimal(childVo.getNohinuntshohitaxkin());
                        // 構成商品の納品消費税金額
                        BigDecimal nohinShohitaxkin = new BigDecimal(childVo.getNohinshohitaxkin());

                        // セット商品の納品単価消費税金額を算出する。
                        nohinuntShohitaxkinSetShohi +=
                            nohinuntShohitaxkin.multiply(suryo).longValue();
                        // セット商品の納品消費税金額を算出する。
                        nohinShohitaxkinSetShohi += nohinShohitaxkin.longValue();

                        // 子明細の場合はセット商品の税金をセット
                        if (setList.size() == j + 1) {

                            // 委託会社の場合
                            if (juchuPriceInfoVo != null && aiteChild != null
                                && aiteModelKosei != null && aitePriceInfoVo != null
                                && CommonConstants.KBN_TAX_KOMEISAI.equals(juchuOyaTaxkbn)) {
                                // セット商品の委託会社受注単価消費税金額をセットする。
                                meiVo.setItakucmpnyacptuntshohitaxkin(
                                    String.valueOf(itakucmpnyAcptuntshohiTaxkinSetShohi));
                                // セット商品の委託会社受注消費税金額をセットする。
                                meiVo.setItakucmpnyacptshohitaxkin(
                                    String.valueOf(itakucmpnyAcptshohiTaxkinSetShohi));
                            }
                            if (CommonConstants.KBN_TAX_KOMEISAI.equals(juchuOyaTaxkbn)) {
                                // セット商品の受注単価消費税金額をセットする。
                                meiVo.setAcptuntshohitaxkin(acptuntshohiTaxkinSetShohi);
                                // セット商品の受注消費税金額をセットする。
                                meiVo.setAcptshohitaxkin(aacptShohitaxkinSetShohi);
                            }
                            if (CommonConstants.KBN_TAX_KOMEISAI.equals(nohinOyaTaxkbn)) {
                                // セット商品の納品単価消費税金額をセットする。
                                meiVo.setNohinuntshohitaxkin(nohinuntShohitaxkinSetShohi);
                                // セット商品の納品消費税金額をセットする。
                                meiVo.setNohinshohitaxkin(nohinShohitaxkinSetShohi);
                            }
                        }

                    }

                    if (("5".equals(meiVo.getTaxkbn()))
                        && "20190930".compareTo(priceInfoVo.getTargetDate()) >= 0) {
                        meiVo.setTaxkbn("1");
                    }
                    // セット商品の税区分が「1：課税」または「4：課税軽減」の場合、構成商品をセット商品の税区分に設定する。
                    if (!CommonConstants.KBN_TAX_KOMEISAI.equals(meiVo.getTaxkbn())) {
                        childVo.setTaxkbn(meiVo.getTaxkbn());
                    } else {
                        childVo.setTaxkbn(shohinModelKosei.getTaxkbn());
                    }
                    // 軽減税率対応 ryu 2019-05-21 End


                    // ■:1 Base)Arai 2014-01-24 セット商品消費税補正対応
                    if (isHaitatsuJisseki) {
                        // 配達実績の場合は納品金額消費税
                        BigDecimal taxKingaku = new BigDecimal(childVo.getNohinshohitaxkin());
                        sumNohinTaxKin = sumNohinTaxKin.add(taxKingaku);

                        // 2021/01/20 価格改定対応 ISAC)Shiba START
                        // 2021/01/20 価格改定対応 ISAC)Shiba END
                    }
                    // 2021/01/20 価格改定対応 ISAC)Shiba START
                    if (useItaku) {
                        BigDecimal aiteTax =
                            (childVo.getItakucmpnyacptshohitaxkin() == null) ? new BigDecimal(0)
                                : new BigDecimal(childVo.getItakucmpnyacptshohitaxkin());
                        sumAiteTaxKin = sumAiteTaxKin.add(aiteTax);
                    }
                    // 2021/01/20 価格改定対応 ISAC)Shiba END

                    // 受注系～配達予定の場合は受注金額消費税
                    BigDecimal taxKingaku = new BigDecimal(childVo.getAcptshohitaxkin());
                    sumAcptTaxKin = sumAcptTaxKin.add(taxKingaku);
                    // ■:2 Base)Arai 2014-01-24 セット商品消費税補正対応

                    // 本数、金額、サーバー交換台数の集計
                    sumVo.addAll(childVo, childVo.getRelationVo());

                    subList.add(childVo);
                }
                // ■:1 Base)Arai 2014-07-03
                // 構成商品が取得出来た場合に調整処理を行う
                if (setList.size() != 0) {
                    // ■:2 Base)Arai 2014-07-03
                    // ■:1 Base)Arai 2014-01-24 セット商品消費税補正対応
                    // 親商品の消費税額を取得
                    BigDecimal oyaAcptTaxKin =
                        new BigDecimal((meiVo.getAcptshohitaxkin() == null) ? "0"
                            : meiVo.getAcptshohitaxkin().toString());
                    BigDecimal oyaNohinTaxKin =
                        new BigDecimal((meiVo.getNohinshohitaxkin() == null) ? "0"
                            : meiVo.getNohinshohitaxkin().toString());

                    if (!isHaitatsuJisseki) {
                        // 受注系～配達予定
                        // 親商品の消費税額と展開商品の消費税額集計を比較
                        int taxCompare = oyaAcptTaxKin.compareTo(sumAcptTaxKin);
                        // 結果に差異がある場合は消費税調整商品を追加する
                        // インボイス対応 : インボイス施行日以降、セット商品の場合は消費税調整レコードは登録しない
                        if (taxCompare != 0
                            && shopModel.getInvoicshikoymd().compareTo(denVo.getDlvryytidd()) > 0) {
                            BigDecimal addAcptTaxKin = (new BigDecimal(meiVo.getAcptshohitaxkin()))
                                .subtract(sumAcptTaxKin);
                            BigDecimal addNohinTaxKin = new BigDecimal(0);
                            // 2021/01/20 価格改定対応 ISAC)Shiba START
                            /* 2017-12-13 Base)Chokenkun update begin 障害対応(No.IT0028) */
                            BigDecimal addAiteTaxKin =
                                (meiVo.getItakucmpnyacptshohitaxkin() == null) ? null
                                    : (new BigDecimal(meiVo.getItakucmpnyacptshohitaxkin()))
                                        .subtract(sumAiteTaxKin);
                            addShohizeiChoseiMeisai(denVo, relation, meiVo, isHaitatsuJisseki,
                                subList, zeiChoseiShncd, addAcptTaxKin, addNohinTaxKin,
                                addAiteTaxKin);
                            /* 2017-12-13 Base)Chokenkun update end 障害対応(No.IT0028) */
                            // 2021/01/20 価格改定対応 ISAC)Shiba END
                        }
                    } else {
                        // 配達実績
                        int taxCompare1 = oyaAcptTaxKin.compareTo(sumAcptTaxKin);
                        int taxCompare2 = oyaNohinTaxKin.compareTo(sumNohinTaxKin);
                        // 受注消費税か納品消費税のどちらか差異がある場合に消費税調整商品を追加する
                        // インボイス対応 : インボイス施行日以降、セット商品の場合は消費税調整レコードは登録しない
                        if ((taxCompare1 != 0 || taxCompare2 != 0)
                            && shopModel.getInvoicshikoymd().compareTo(denVo.getDlvryytidd()) > 0) {
                            BigDecimal addAcptTaxKin = (new BigDecimal(meiVo.getAcptshohitaxkin()))
                                .subtract(sumAcptTaxKin);
                            BigDecimal addNohinTaxKin =
                                (new BigDecimal(meiVo.getNohinshohitaxkin()))
                                    .subtract(sumNohinTaxKin);
                            /* 2017-12-13 Base)Chokenkun update begin 障害対応(No.IT0028) */
                            BigDecimal addAiteTaxKin =
                                (meiVo.getItakucmpnyacptshohitaxkin() == null) ? null
                                    : (new BigDecimal(meiVo.getItakucmpnyacptshohitaxkin()))
                                        .subtract(sumAiteTaxKin);
                            addShohizeiChoseiMeisai(denVo, relation, meiVo, isHaitatsuJisseki,
                                subList, zeiChoseiShncd, addAcptTaxKin, addNohinTaxKin,
                                addAiteTaxKin);
                            /* 2017-12-13 Base)Chokenkun update end 障害対応(No.IT0028) */
                        }
                    }
                    // ■:2 Base)Arai 2014-01-24 セット商品消費税補正対応
                    // ■:1 Base)Arai 2014-07-03
                }
                // ■:2 Base)Arai 2014-07-03
            }

        }

        // 配布物を明細リストに追加
        int haihuCount = meisaiList.size() + 1;
        // ■:1 IBIS)Shinoda 2017-06-29 upd begin
        for (int i = 0; i < 4; i++) {
            String haifu = "";
            String haifuApcd = "";
            String haifuShopcd = "";
            String haifuItakuaiteapkihoncd = "";
            String haifuItakuaiteshopcd = "";
            String haifuItakuaiteacptdnpyno = "";
            String haifuItakuaiteedano = "";
            String haifuItakuaiteacptdnpymeisaino = "";
            String haifuItakuaiteacptdnpymeisaiedano = "";
            if (i == 0) {
                haifu = denVo.getHaifubutsucd1();
                haifuApcd = denVo.getHaifubutsuapkihoncd1();
                haifuShopcd = denVo.getHaifubutsushopcd1();
                /* 2018-01-13 ibis)inoue add begin */
                haifuItakuaiteapkihoncd = denVo.getHaihubutu1itakuaiteapkihoncd();
                haifuItakuaiteshopcd = denVo.getHaihubutu1itakuaiteshopcd();
                haifuItakuaiteacptdnpyno = denVo.getHaihubutu1itakuaiteacptdnpyno();
                haifuItakuaiteedano = denVo.getHaihubutu1itakuaiteedano();
                haifuItakuaiteacptdnpymeisaino = denVo.getHaihubutu1itakuaiteacptdnpymeisaino();
                haifuItakuaiteacptdnpymeisaiedano =
                    denVo.getHaihubutu1itakuaiteacptdnpymeisaiedano();
                /* 2018-01-13 ibis)inoue add end */
            } else if (i == 1) {
                haifu = denVo.getHaifubutsucd2();
                haifuApcd = denVo.getHaifubutsuapkihoncd2();
                haifuShopcd = denVo.getHaifubutsushopcd2();
                /* 2018-01-13 ibis)inoue add begin */
                haifuItakuaiteapkihoncd = denVo.getHaihubutu2itakuaiteapkihoncd();
                haifuItakuaiteshopcd = denVo.getHaihubutu2itakuaiteshopcd();
                haifuItakuaiteacptdnpyno = denVo.getHaihubutu2itakuaiteacptdnpyno();
                haifuItakuaiteedano = denVo.getHaihubutu2itakuaiteedano();
                haifuItakuaiteacptdnpymeisaino = denVo.getHaihubutu2itakuaiteacptdnpymeisaino();
                haifuItakuaiteacptdnpymeisaiedano =
                    denVo.getHaihubutu2itakuaiteacptdnpymeisaiedano();
                /* 2018-01-13 ibis)inoue add end */
            } else if (i == 2) {
                haifu = denVo.getHaifubutsucd3();
                haifuApcd = denVo.getHaifubutsuapkihoncd3();
                haifuShopcd = denVo.getHaifubutsushopcd3();
                /* 2018-01-13 ibis)inoue add begin */
                haifuItakuaiteapkihoncd = denVo.getHaihubutu3itakuaiteapkihoncd();
                haifuItakuaiteshopcd = denVo.getHaihubutu3itakuaiteshopcd();
                haifuItakuaiteacptdnpyno = denVo.getHaihubutu3itakuaiteacptdnpyno();
                haifuItakuaiteedano = denVo.getHaihubutu3itakuaiteedano();
                haifuItakuaiteacptdnpymeisaino = denVo.getHaihubutu3itakuaiteacptdnpymeisaino();
                haifuItakuaiteacptdnpymeisaiedano =
                    denVo.getHaihubutu3itakuaiteacptdnpymeisaiedano();
                /* 2018-01-13 ibis)inoue add end */
            } else if (i == 3) {
                haifu = denVo.getHaifubutsucd4();
                haifuApcd = denVo.getHaifubutsuapkihoncd4();
                haifuShopcd = denVo.getHaifubutsushopcd4();
                /* 2018-01-13 ibis)inoue add begin */
                haifuItakuaiteapkihoncd = denVo.getHaihubutu4itakuaiteapkihoncd();
                haifuItakuaiteshopcd = denVo.getHaihubutu4itakuaiteshopcd();
                haifuItakuaiteacptdnpyno = denVo.getHaihubutu4itakuaiteacptdnpyno();
                haifuItakuaiteedano = denVo.getHaihubutu4itakuaiteedano();
                haifuItakuaiteacptdnpymeisaino = denVo.getHaihubutu4itakuaiteacptdnpymeisaino();
                haifuItakuaiteacptdnpymeisaiedano =
                    denVo.getHaihubutu4itakuaiteacptdnpymeisaiedano();
                /* 2018-01-13 ibis)inoue add end */
            }
            // ■:2 IBIS)Shinoda 2017-06-29 upd end
            if (haifu != null && haifu.length() != 0) {
                JuchuMeisaiVO vo = JuchuMeisaiVO.createHaifubutsuVo();
                // ■:1 Base)Arai 2014-02-05 セット商品消費税補正対応
                vo.setApkihoncd(denVo.getApkihoncd());
                vo.setShopcd(denVo.getShopcd());
                vo.setAcptdnpyno(denVo.getAcptdnpyno());
                vo.setEdano(denVo.getEdano());
                // ■:2 Base)Arai 2014-02-05 セット商品消費税補正対応
                vo.setAcptdnpymeisaino(haihuCount);
                vo.setDatsksmotokbn(denVo.getDatsksmotokbn());
                // ■:1 IBIS)Shinoda 2017-06-29 add start
                vo.setHaifubutsuapkihoncd(haifuApcd);
                vo.setHaifubutsushopcd(haifuShopcd);
                // ■:2 IBIS)Shinoda 2017-06-29 add end
                vo.setHaifubutsucd(haifu);
                vo.setAcptsuryo(new BigDecimal("1"));
                /* 2018-01-13 ibis)inoue add begin */
                vo.setItakuaiteapkihoncd(haifuItakuaiteapkihoncd);
                vo.setItakuaiteshopcd(haifuItakuaiteshopcd);
                vo.setItakuaiteacptdnpyno(haifuItakuaiteacptdnpyno);
                if (haifuItakuaiteedano != null) {
                    vo.setItakuaiteedano(Short.valueOf(haifuItakuaiteedano));
                }
                if (haifuItakuaiteacptdnpymeisaino != null) {
                    vo.setItakuaiteacptdnpymeisaino(
                        Integer.valueOf(haifuItakuaiteacptdnpymeisaino));
                }
                if (haifuItakuaiteacptdnpymeisaiedano != null) {
                    vo.setItakuaiteacptdnpymeisaiedano(
                        Short.valueOf(haifuItakuaiteacptdnpymeisaiedano));
                }
                /* 2018-01-13 ibis)inoue add end */
                meisaiList.add(vo);
                haihuCount++;
            }
        }

        // セット構成商品を明細リストに追加
        meisaiList.addAll(subList);

        // 伝票VOに集計VOをセットする
        denVo.setSumvo(sumVo);

        // ■:1 Base)Arai 2014-02-05 消費税調整対応
        // 受注配達明細テーブルのプライマリーキー順にソートする
        juchuMeisaiComparator(meisaiList);
        // ■:2 Base)Arai 2014-02-05 消費税調整対応
        return meisaiList;
    }

    /**
     * 受注在庫更新処理を行います．
     * 
     * @param msgList メッセージリスト
     * @param zaikoService 在庫サービス
     * @param keySet 在庫キーリスト
     * @param DbManagementSession DB管理セッションクラス
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public void checkUpdateZaiko(List<MessageList> msgList, ZaikoService zaikoService,
        List<List<ZaikoKeyVo>> keySet, DbManagementSession dbManagementSession) {
        MessageList confirmList = msgList == null ? null : (MessageList) msgList.get(0);

        if (keySet == null || keySet.size() == 0)
            return;

        // 在庫キーVO生成
        for (int i = 0; i < keySet.size(); i++) {
            List<ZaikoKeyVo> keys = keySet.get(i);

            // ■:1 Base)Arai 2013-10-31 在庫ロック対応
            // ロック処理（必要か検討）
            zaikoService.lockTable(null, keys, dbManagementSession);
            // ■:2 Base)Arai 2013-10-31 在庫ロック対応

            // チェック＆更新処理
            zaikoService.processCheckUpdate(keys, dbManagementSession);

            // エラーメッセージ追加
            if (0 < keys.size() && confirmList != null) {
                for (int j = 0; j < keys.size(); j++) {
                    ZaikoKeyVo vo = (ZaikoKeyVo) keys.get(j);
                    if (!vo.isCheckFg() || vo.isCheckResult())
                        continue;

                    switch (vo.getType()) {
                        case ZaikoKeyVo.TYPE_S04T005T:
                            confirmList.add(Message.getErrorMessage(MessageConstants.MSG_W150,
                                new String[] {"商品コード「" + vo.getFourthkey() + "」が配達拠点受注アラーム数"},
                                new String[] {"acptsuryo"},
                                Integer.parseInt(vo.getOriginalIndex()) - 1));
                            break;
                        case ZaikoKeyVo.TYPE_S04T006T:
                            confirmList.add(Message.getErrorMessage(MessageConstants.MSG_W150,
                                new String[] {"担当者受注アラーム数"}, new String[] {"dlvrytnshacd"}));
                            break;
                        case ZaikoKeyVo.TYPE_S04T007T:
                            confirmList.add(Message.getErrorMessage(MessageConstants.MSG_W150,
                                new String[] {"配達エリア受注アラーム数"}, null));
                            break;
                        case ZaikoKeyVo.TYPE_S13T004T:
                            confirmList.add(Message.getErrorMessage(MessageConstants.MSG_W150,
                                new String[] {"商品コード「" + vo.getFourthkey() + "」が在庫引当可能数"},
                                new String[] {"acptsuryo"},
                                Integer.parseInt(vo.getOriginalIndex()) - 1));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    /**
     * 配布物受注取込処理を行います（受注明細に配布物を展開していること）．
     * 
     * @param cancelFg キャンセルフラグ
     * @param haifuService 配布物サービス
     * @param denVo 受注伝票VO
     * @param meisaiList 受注明細VOリスト
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public void updateHaifubutsu(boolean cancelFg, HaifubutsuService haifuService,
        JuchuDenpyoVO denVo, List<JuchuMeisaiVO> meisaiList,
        DbManagementSession dbManagementSession) {

        if (meisaiList == null || meisaiList.size() == 0)
            return;

        // 配布物取込更新処理
        for (int i = 0; i < meisaiList.size(); i++) {
            JuchuMeisaiVO meiVo = meisaiList.get(i);

            if (CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(meiVo.getMeisaikbn())) {

                // 配布対象ステータス更新
                haifuService.processJuchuTorikomi(cancelFg, denVo, meiVo, dbManagementSession);
            }
        }
    }

    /**
     * 受注伝票を新規登録します．
     * 
     * @param denVo 受注伝票VO
     * @param saibanFg 採番フラグ
     * @return result 結果
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public boolean insertJuchuDenpyo(JuchuDenpyoVO denVo, boolean saibanFg,
        DbManagementSession dbManagementSession) {
        boolean result = false;

        try {
            // DAO取得
            S04T001TDao dao = getS04t001tEntityDao();

            // 登録用Model生成
            S04T001TEntity entityModel = S04T001TEntityDaoEx.createDefaultModel();

            // 受注伝票VOよりModelにプロパティをセットする
            setpropDenpyoVoToModel(denVo, entityModel);
            denVo.setEdano(Short.valueOf("0"));
            entityModel.setEdano(Short.valueOf("0"));

            if (saibanFg) {
                // 採番処理実行
                // S01SaibanService saibanService = new S01SaibanService();
                String newNumber =
                    saibanService.getSlipNoJuchu(denVo.getApkihoncd(), denVo.getShopcd());

                // 採番した受注伝票番号セット
                denVo.setAcptdnpyno(newNumber);
                entityModel.setAcptdnpyno(newNumber);
            }
            entityModel.setToshacd(dbManagementSession.getTnshacd()); // 登録者コード
            entityModel.setToshaapkihoncd(dbManagementSession.getApkihoncd()); // 登録者ＡＰ基本コード
            entityModel.setToshashopcd(dbManagementSession.getShopcd()); /// 登録者ショップコード
            entityModel.setInptsystmymd(DateTimeUtil.changeFormatToSystemDate(LocalDateTime.now())); // 登録システム日付
            entityModel
                .setInptsystmjikan(DateTimeUtil.changeFormatToSystemTime(LocalDateTime.now())); // 登録システム時間
            entityModel.setInptunyoymd(dbManagementSession.getUnyoDate()); // 登録運用日付
            entityModel.setInptipadrse(dbManagementSession.getIpAddress()); // 登録ＩＰアドレス
            entityModel.setInptpgmid(dbManagementSession.getProgramId()); // 登録プログラムＩＤ
            entityModel.setKoshacd(dbManagementSession.getTnshacd()); // 更新者コード
            entityModel.setKoshaapkihoncd(dbManagementSession.getApkihoncd()); // 更新者ＡＰ基本コード
            entityModel.setKoshashopcd(dbManagementSession.getShopcd()); // 更新者ショップコード
            entityModel.setKshnsystmymd(DateTimeUtil.changeFormatToSystemDate(LocalDateTime.now())); // 更新システム日付
            entityModel
                .setKshnsystmjikan(DateTimeUtil.changeFormatToSystemTime(LocalDateTime.now())); // 更新システム時間
            entityModel.setKshnunyoymd(dbManagementSession.getUnyoDate()); // 更新運用日付
            entityModel.setKshnipadrse(dbManagementSession.getIpAddress()); // 更新ＩＰアドレス
            entityModel.setKshnpgmid(dbManagementSession.getProgramId()); // 更新プログラムＩＤ

            // 登録処理
            // UPD >> 20170801 TJS)takio
            result = (dao.insert(entityModel) == 1);
            // UPD << 20170801 TJS)takio

            // ■:1 Base)Arai 2013-09-20
            // if (LOGGER_JUTYUYUKO.isDebugEnabled()) {
            // LOGGER_JUTYUYUKO.debug(entityModel.toStringFlatLog());
            // }
            // ■:2 Base)Arai 2013-09-20

        } catch (Exception e) {
            // S01Util.debugLogOut(LOGGER, "受注伝票の登録処理に失敗しました。");
            throw new BusinessException("受注伝票の登録処理に失敗しました。", e);
        } finally {

        }

        return result;
    }

    /**
     * 受注伝票を更新します．
     * 
     * @param type タイプ（通常更新・キャンセル・削除）
     * @param denVo 受注伝票VO
     * @param entityModel 受注伝票Model
     * @return result 結果
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public boolean updateJuchuDenpyo(int type, JuchuDenpyoVO denVo, S04T001TEntity entityModel) {
        boolean result = false;

        try {
            // DAO取得
            S04T001TDao dao = getS04t001tEntityDao();

            if (entityModel == null) {
                // 排他処理実行
                entityModel = juchuDaoRepository.findByPrimaryKeyForLock(denVo);
            }

            // 行が存在しない場合は排他エラーにする
            if (entityModel == null) {
                throw new BusinessException("排他エラー");

            } else {

                // キャンセル時は伝票ステータスをキャンセルにする
                if (type == TYPE_UPATE_CANCEL) {
                    entityModel.setDnpysts(CommonConstants.DNPYSTS_09);
                    entityModel.setAcptsts(CommonConstants.DNPYSTS_09);
                    entityModel.setDlvrysts(CommonConstants.DNPYSTS_00);

                    // 削除時は有効区分を無効にする
                } else if (type == TYPE_UPATE_DELETE) {
                    entityModel.setYukokbn(CommonConstants.FLG_OFF);

                    // 通常更新時はプロパティをModelにセットする
                } else {
                    setpropDenpyoVoToModel(denVo, entityModel);
                }

                // 更新処理
                // UPD >> 20170801 TJS)takio
                result = (dao.updateByPrimaryKey(entityModel) == 1);
                // UPD << 20170801 TJS)takio

                // 更新日付・時間を再取得する
                S04T001TEntityKey key = new S04T001TEntityKey();
                key.setApkihoncd(entityModel.getApkihoncd());
                key.setShopcd(entityModel.getShopcd());
                key.setAcptdnpyno(entityModel.getAcptdnpyno());
                key.setEdano(entityModel.getEdano());
                S04T001TEntity updatedModel = dao.selectByPrimaryKey(key);
                denVo.setKshnsystmymd(updatedModel.getKshnsystmymd());
                denVo.setKshnsystmjikan(updatedModel.getKshnsystmjikan());
            }
            // ■:1 Base)Arai 2013-09-20
            // if (LOGGER_JUTYUYUKO.isDebugEnabled()) {
            // LOGGER_JUTYUYUKO.debug(entityModel.toStringFlatLog());
            // }
            // ■:2 Base)Arai 2013-09-20

        } catch (Exception e) {
            // S01Util.debugLogOut(LOGGER, "受注伝票の登録処理に失敗しました。");
            throw new BusinessException("受注伝票の登録処理に失敗しました。");
        } finally {

        }

        return result;
    }

    /**
     * 受注明細を新規登録します．
     * 
     * @param denVo 受注伝票VO
     * @param meisaiList 明細VOリスト
     * @return result 結果
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public boolean insertJuchuMeisai(JuchuDenpyoVO denVo, List<JuchuMeisaiVO> meisaiList,
        DbManagementSession dbManagementSession) {
        ArrayList<S04T002TEntity> insertList = new ArrayList<S04T002TEntity>();
        boolean result = false;

        try {
            // DAO取得
            S04T002TDao dao = getS04t002tEntityDao();

            // 明細Modelを生成する
            for (int i = 0; i < meisaiList.size(); i++) {
                JuchuMeisaiVO meiVo = (JuchuMeisaiVO) meisaiList.get(i);
                S04T002TEntity meiModel = S04T002TEntityDaoEx.createDefaultModel();

                // プロパティをセットする
                setpropMeisaiVoToModel(denVo, meiVo, meiModel, dbManagementSession);

                insertList.add(meiModel);
            }

            // 現在の明細を一旦削除する
            getJuchuDao().deleteMeisai(denVo);

            // 登録処理
            /* 2017-08-08 IBIS)shinoda upd start */

            int count = 0;
            for (S04T002TEntity entity : insertList) {
                count += dao.insert(entity);
            }
            result = (count == meisaiList.size());

            /* 2017-08-08 IBIS)shinoda upd end */
            // ■:1 Base)Arai 2013-09-20
            // if (LOGGER_JUTYUYUKO.isDebugEnabled()) {
            // for (Iterator it = meisaiList.iterator(); it.hasNext();) {
            // S04t002tEntityModel meiModel = (S04t002tEntityModel) it.next();
            // LOGGER_JUTYUYUKO.debug(meiModel.toStringFlatLog());
            // }
            // }
            // ■:2 Base)Arai 2013-09-20

        } catch (Exception e) {
            // S01Util.debugLogOut(LOGGER, "受注明細の登録処理に失敗しました。");
            throw new BusinessException("受注明細の登録処理に失敗しました。");
        } finally {

        }

        return result;
    }

    /**
     * 受注関係VOを生成します。
     * 
     * @param denVo 受注伝票VO
     * @return List 受注明細VOリスト
     * @throws ApplicationException アプリケーション例外時
     * @throws SystemException システム例外時
     */
    public JuchuRelationVo getJuchuRelation(JuchuDenpyoVO denVo) {

        // サービスクラス取得
        // S02Service s02Service = new S02Service();

        // 受注関係VO生成（伝票情報のみセット）
        return new JuchuRelationVo(getSystemSeihinsuicd(commonService),
            getSystemServerkanrencd(commonService), getSystemServerhontaicd(commonService),
            // ■:1 Base)chokenkun 2016-03-11 ７Ｌボトル対応
            /*
             * S01JuchuRelationVo.getSystemRegularcd(s02Service),
             * S01JuchuRelationVo.getSystemSlimcd(s02Service),
             */
            getSystemRegularcdList(commonService), getSystemSlimcdList(commonService),
            // ■:2 Base)chokenkun 2016-03-11 ７Ｌボトル対応
            denVo.getTokuisakikbn(), denVo.getAcptkbn(), denVo.getAcptkbnshs());
    }


    /**
     * 受注伝票VOより受注伝票モデルにプロパティをセットします
     * 
     * @param vo 受注伝票VO
     * @param model 受注伝票Model
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    private void setpropDenpyoVoToModel(JuchuDenpyoVO vo, S04T001TEntity model) {

        // サービスクラス取得
        // S01TokuisakiService tokuisakiService = new S01TokuisakiService(tran);

        // 得意先マスタモデル取得
        S02M013TEntity tokuisaki = tokuisakiService.getTokuisakiModel(vo.getApkihoncd(),
            vo.getShopcd(), vo.getTokuisakicd(), false);
        String skysakicd = tokuisaki == null ? "" : tokuisaki.getSkysakicd();

        // 請求先マスタモデル取得
        S02M017TEntity seikyusaki = tokuisakiService.getSeikyusakiModel(vo.getApkihoncd(),
            vo.getShopcd(), skysakicd, false);
        String kaishuhoho = seikyusaki == null ? "" : seikyusaki.getKaishuhohokbn();

        // 集計クラス取得
        JuchuSumVo sum = vo.getSumvo();

        // プロパティセット
        model.setApkihoncd(vo.getApkihoncd());
        model.setShopcd(vo.getShopcd());
        model.setAcptdnpyno(vo.getAcptdnpyno());
        model.setEdano(vo.getEdano());
        model.setDlvrytrgtflg(CommonConstants.FLG_ON);
        model.setDnpykbn(JuchuRelationVo.getDnpykbn(vo.getTokuisakikbn()));
        model.setTorihikikbn(
            JuchuRelationVo.getTorihikikbn(vo.getTokuisakikbn(), vo.getDatsksmotokbn()));
        model.setTokuisakicd(vo.getTokuisakicd());
        model.setAcpttnshaapkihoncd(vo.getAcpttnshaapkihoncd());
        model.setAcpttnshashopcd(vo.getAcpttnshashopcd());
        model.setAcpttnshacd(vo.getAcpttnshacd());
        model.setAcptkbn(vo.getAcptkbn());
        model.setAcptkbnshs(vo.getAcptkbnshs());
        model.setAcptdd(vo.getAcptdd());
        model.setDlvryytidd(vo.isYoyaku() ? "" : vo.getDlvryytidd());
        model.setDlvrydd(model.getDlvryytidd());
        model.setDlvrykibojikan(vo.getDlvrykibojikan());
        // ■:1 tjs)nakano 2009-02-12
        model.setDlvrykibojikanshry(vo.getDlvrykibojikanshry());
        // ■:2 tjs)nakano 2009-02-12
        model.setDlvrykibojikankbn(vo.getDlvrykibojikankbn());
        model.setFzaiokikanoflg(vo.getFzaiokikanoflg());
        model.setDlvrytnshacd(vo.getDlvrytnshacd());
        model.setDlvrykankatsuapkihoncd(vo.getDlvrykankatsuapkihoncd());
        model.setDlvrykankatsushopcd(vo.getDlvrykankatsushopcd());
        model.setDlvrykyotencd(vo.getDlvrykyotencd());
        model.setDlvryareacd(vo.getDlvryareacd());
        model.setDlvrykbn(vo.getDlvrykbn());
        model.setDlvrycmpnycd(vo.getDlvrycmpnycd());
        model.setAzukaribtln1n2rittoruhns(
            (vo.getAzukaribtln1n2rittoruhns() == null) ? 0 : vo.getAzukaribtln1n2rittoruhns());
        model.setKybtln1n2rittoruhns(
            (vo.getKybtln1n2rittoruhns() == null) ? 0 : vo.getKybtln1n2rittoruhns());
        model.setKzbtln1n2rittoruhns(
            (vo.getKzbtln1n2rittoruhns() == null) ? 0 : vo.getKzbtln1n2rittoruhns());
        model.setAzukaribtln8rittoruhns(
            (vo.getAzukaribtln8rittoruhns() == null) ? 0 : vo.getAzukaribtln8rittoruhns());
        model.setKybtln8rittoruhns(
            (vo.getKybtln8rittoruhns() == null) ? 0 : vo.getKybtln8rittoruhns());
        model.setKzbtln8rittoruhns(
            (vo.getKzbtln8rittoruhns() == null) ? 0 : vo.getKzbtln8rittoruhns());
        model.setPrntmemon1(vo.getPrntmemon1());
        model.setPrntmemon2(vo.getPrntmemon2());
        model.setPrntmemon3(vo.getPrntmemon3());
        if (CommonConstants.DNPYSTS_11.equals(vo.getDnpysts())) {
            model.setAcptshimeprcsdd(CommonConstants.DEF_DATE_00000000);
            model.setAcptshimeprcstnshacd("");
            model.setAcptthdaibikiflg(CommonConstants.FLG_OFF);
            model.setNoshoprntsumiflg(CommonConstants.FLG_OFF);
            model.setNoshohikaeprntsumiflg(CommonConstants.FLG_OFF);
            model.setNyknhyoprntsumiflg(CommonConstants.FLG_OFF);
            model.setWsvsijishoprntsumiflg(CommonConstants.FLG_OFF);
            // ■:1 base)arai 2011-12-07 印刷日付を初期化する
            model.setNoshoprntdd(null);
            model.setNoshohikaeprntdd(null);
            model.setNyknhyoprntdd(null);
            model.setWsvsijishoprntdd(null);
            // ■:2 base)arai 2011-12-07
        }
        model.setDlvryjun(vo.getDlvryjun());
        model.setNyknhyoprnttrgtflg(S04T001TEntityDaoEx.getNyknhyoprnttrgtflg(kaishuhoho));
        model.setAcptthdaibikiflg(S04T001TEntityDaoEx.getAcptthdaibikiflg(kaishuhoho));
        model.setWsvsijishoprnttrgtflg(
            S04T001TEntityDaoEx.getWsvsijishoprnttrgtflg(vo.getAcptkbn()));
        if (CommonConstants.DNPYSTS_11.equals(vo.getDnpysts())) {
            model.setDnpysts(CommonConstants.DNPYSTS_02);
            model.setAcptsts(CommonConstants.DNPYSTS_02);
            model.setDlvrysts(CommonConstants.DNPYSTS_00);
        } else {
            model.setDnpysts(vo.getDnpysts());
            model.setAcptsts(vo.getDnpysts());
            model.setDlvrysts(CommonConstants.DNPYSTS_00);
        }
        model.setDatsksmotokbn(vo.getDatsksmotokbn());
        model.setAcpthikiatesakiapkihoncd(vo.getDlvrykankatsuapkihoncd());
        model.setAcpthikiatesakishopcd(vo.getDlvrykankatsushopcd());
        model.setAcpthikiatesakikyotencd(vo.getDlvrykyotencd());
        model.setDlvryytirgrayusyohns(
            Short.valueOf(sum == null ? "0" : sum.getCntForUpdate(sum.getRycnt())));
        model.setDlvryytirgramushohns(
            Short.valueOf(sum == null ? "0" : sum.getCntForUpdate(sum.getRmcnt())));
        model.setDlvryytisrmyusyohns(
            Short.valueOf(sum == null ? "0" : sum.getCntForUpdate(sum.getSycnt())));
        model.setDlvryytisrmmushohns(
            Short.valueOf(sum == null ? "0" : sum.getCntForUpdate(sum.getSmcnt())));
        model.setDlvryytisonotayusyohns(
            Short.valueOf(sum == null ? "0" : sum.getCntForUpdate(sum.getOycnt())));
        model.setDlvryytisonotamushohns(
            Short.valueOf(sum == null ? "0" : sum.getCntForUpdate(sum.getOmcnt())));
        // 得意先が卸対象の場合は税計算区分に外税をセットする
        model.setTaxkeisankbn(
            TokuisakiRelationVo.checkOroshi(vo.getTokuisakikbn()) ? CommonConstants.KBN_TAXKEISAN_EX
                : vo.getTaxkeisankbn());

        if (CommonConstants.KBN_TOKUISAKI_KEIHI
            .equals(tokuisaki == null ? "" : tokuisaki.getTokuisakikbn())) {
            model.setDlvryytiseihinmizuurikin(Long.valueOf("0"));
            model.setDlvryytisonotaurikin(Long.valueOf("0"));
        } else {
            model.setDlvryytiseihinmizuurikin(sum == null ? Long.valueOf("0")
                : Long.valueOf(sum.getMoneyForUpdate(sum.getWmn())));
            model.setDlvryytisonotaurikin(sum == null ? Long.valueOf("0")
                : Long.valueOf(sum.getMoneyForUpdate(sum.getOmn())));
        }
        model.setAcptkin(vo.getAcptkin());
        model.setAcptshohitaxkin(vo.getAcptshohitaxkin());

        // 新規受注取込時
        if (CommonConstants.KBN_SAKUSEIMOTO_JUCHUTORIKOMI.equals(vo.getDatsksmotokbn())) {
            model.setSksmotokanrikbn(vo.getSksmotokanrikbn());
            model.setSksmotokanrino(vo.getSksmotokanrino());
        }
        // ■:1 Isac）matsuo 2012-02-02
        model.setAcptrcptkeirokbn(vo.getAcptrcptkeirokbn());
        // ■:2 Isac）matsuo 2012-02-02
        // インボイス対応
        model.setAcptippannukikin(vo.getAcptippannukikin());
        model.setAcptippantaxkin(vo.getAcptippantaxkin());
        model.setAcptkeigennukikin(vo.getAcptkeigennukikin());
        model.setAcptkeigentaxkin(vo.getAcptkeigentaxkin());
        model.setAcpthikazeiuriikin(vo.getAcpthikazeiuriikin());
        model.setAcptfukazeiuriikin(vo.getAcptfukazeiuriikin());
        model.setNohinippannukikin(vo.getNohinippannukikin());
        model.setNohinippantaxkin(vo.getNohinippantaxkin());
        model.setNohinkeigennukikin(vo.getNohinkeigennukikin());
        model.setNohinkeigentaxkin(vo.getNohinkeigentaxkin());
        model.setNohinhikazeiuriikin(vo.getNohinhikazeiuriikin());
        model.setNohinfukazeiuriikin(vo.getNohinfukazeiuriikin());
        model.setSingldnpyflg(vo.getSingldnpyflg());
    }

    /**
     * 受注明細VOより受注明細モデルにプロパティをセットします
     * 
     * @param denVo 受注伝票VO
     * @param meiVo 受注明細VO
     * @param model 受注明細Model
     */
    private void setpropMeisaiVoToModel(JuchuDenpyoVO denVo, JuchuMeisaiVO meiVo,
        S04T002TEntity model, DbManagementSession dbManagementSession) {

        // プロパティセット
        model.setApkihoncd(denVo.getApkihoncd());
        model.setShopcd(denVo.getShopcd());
        model.setAcptdnpyno(denVo.getAcptdnpyno());
        model.setEdano(denVo.getEdano());
        model.setAcptdnpymeisaino(meiVo.getAcptdnpymeisaino());
        model.setAcptdnpymeisaiedano(meiVo.getAcptdnpymeisaiedano());
        model.setDatsksmotokbn(meiVo.getDatsksmotokbn());
        /* 2017-09-21 Base)Chokenkun add begin 次期ＡＣＣＩＳ */
        model.setItakuaiteapkihoncd(meiVo.getItakuaiteapkihoncd());
        model.setItakuaiteshopcd(meiVo.getItakuaiteshopcd());
        model.setItakuaiteacptdnpyno(meiVo.getItakuaiteacptdnpyno());
        model.setItakuaiteedano(meiVo.getItakuaiteedano());
        model.setItakuaiteacptdnpymeisaino(meiVo.getItakuaiteacptdnpymeisaino());
        model.setItakuaiteacptdnpymeisaiedano(meiVo.getItakuaiteacptdnpymeisaiedano());
        model.setItakucmpnyacptunt(meiVo.getItakucmpnyacptunt());
        model.setItakucmpnyacptuntshohitaxkin(meiVo.getItakucmpnyacptuntshohitaxkin());
        model.setItakucmpnyacptshohitaxkin(meiVo.getItakucmpnyacptshohitaxkin());
        /* 2017-09-21 Base)Chokenkun add end 次期ＡＣＣＩＳ */

        model.setToshacd(dbManagementSession.getTnshacd()); // 登録者コード
        model.setToshaapkihoncd(dbManagementSession.getApkihoncd()); // 登録者ＡＰ基本コード
        model.setToshashopcd(dbManagementSession.getShopcd()); /// 登録者ショップコード
        model.setInptsystmymd(DateTimeUtil.changeFormatToSystemDate(LocalDateTime.now())); // 登録システム日付
        model.setInptsystmjikan(DateTimeUtil.changeFormatToSystemTime(LocalDateTime.now())); // 登録システム時間
        model.setInptunyoymd(dbManagementSession.getUnyoDate()); // 登録運用日付
        model.setInptipadrse(dbManagementSession.getIpAddress()); // 登録ＩＰアドレス
        model.setInptpgmid(dbManagementSession.getProgramId()); // 登録プログラムＩＤ
        model.setKoshacd(dbManagementSession.getTnshacd()); // 更新者コード
        model.setKoshaapkihoncd(dbManagementSession.getApkihoncd()); // 更新者ＡＰ基本コード
        model.setKoshashopcd(dbManagementSession.getShopcd()); // 更新者ショップコード
        model.setKshnsystmymd(DateTimeUtil.changeFormatToSystemDate(LocalDateTime.now())); // 更新システム日付
        model.setKshnsystmjikan(DateTimeUtil.changeFormatToSystemTime(LocalDateTime.now())); // 更新システム時間
        model.setKshnunyoymd(dbManagementSession.getUnyoDate()); // 更新運用日付
        model.setKshnipadrse(dbManagementSession.getIpAddress()); // 更新ＩＰアドレス
        model.setKshnpgmid(dbManagementSession.getProgramId()); // 更新プログラムＩＤ

        // 配布物の場合
        if (CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(meiVo.getMeisaikbn())) {
            /* 2017-06-29 IBIS)Shinoda add start */
            model.setHaifubutsuapkihoncd(meiVo.getHaifubutsuapkihoncd());
            model.setHaifubutsushopcd(meiVo.getHaifubutsushopcd());
            /* 2017-06-29 IBIS)Shinoda add end */
            model.setHaifubutsucd(meiVo.getHaifubutsucd());
            model.setAcptsuryo(meiVo.getAcptsuryo());
            /* 2017-12-05 IBIS)inoue update start */
            if (CommonConstants.KBN_SAKUSEIMOTO_JUCHUYOYAKU.equals(denVo.getDatsksmotokbn())) {
                /* 2017-08-07 IBIS)Shinoda add start */
                model.setSoukaisu(meiVo.getSoukaisu());
                model.setZankaisu(meiVo.getZankaisu());
                /* 2017-08-07 IBIS)Shinoda add end */
            }
            /* 2017-12-05 IBIS)inoue update end */
            model.setYusyokbn(CommonConstants.KBN_YUSHO_NONE);
            model.setMeisaikbn(CommonConstants.KBN_MEISAI_HAIHUBUTSU);
            model.setNyuryokumeisaiflg(CommonConstants.FLG_ON);
            model.setTanpinmeisaiflg(CommonConstants.FLG_ON);
            // ■:1 Isac)hikoso 2007-08-07
            // 実績登録を行う場合、配布物の納品数量をセット
            if (meiVo.getNohinsuryo() != null) {
                model.setNohinsuryo(meiVo.getNohinsuryo());
            }
            // ■:2 Isac)hikoso 2007-08-07

            // ■:1 Isac)hikoso 2007-06-26
            // 代引の場合
        } else if (CommonConstants.KBN_MEISAI_DAIBIKI.equals(meiVo.getMeisaikbn())) {
            model.setLctgrycd(meiVo.getLctgrycd());
            model.setMctgrycd(meiVo.getMctgrycd());
            model.setSctgrycd(meiVo.getSctgrycd());
            model.setShncd(meiVo.getShncd());
            model.setKatasiki(meiVo.getKatasiki());
            model.setKeijokbn(meiVo.getKeijokbn());
            model.setAcptsuryo(meiVo.getAcptsuryo());
            /* 2017-12-05 IBIS)inoue update start */
            if (CommonConstants.KBN_SAKUSEIMOTO_JUCHUYOYAKU.equals(denVo.getDatsksmotokbn())) {
                /* 2017-08-07 IBIS)Shinoda add start */
                model.setSoukaisu(meiVo.getSoukaisu());
                model.setZankaisu(meiVo.getZankaisu());
                /* 2017-08-07 IBIS)Shinoda add end */
            }
            /* 2017-12-05 IBIS)inoue update end */
            model.setHyojununt(meiVo.getHyojununt());
            model.setAcptunt(meiVo.getAcptunt());
            // ■:1 Isac)Shiba 2008-05-24
            model.setAcptkin(meiVo.getAcptkin());
            model.setAcptuntshohitaxkin(meiVo.getAcptuntshohitaxkin());
            model.setAcptshohitaxkin(meiVo.getAcptshohitaxkin());
            // ■:2 Isac)Shiba 2008-05-24
            model.setTaxkbn(meiVo.getTaxkbn());
            model.setYusyokbn(
                JuchuRelationVo.getYushokbn(meiVo.getKeijokbn(), meiVo.getAcptunt().toString()));
            model.setMeisaikbn(CommonConstants.KBN_MEISAI_DAIBIKI);
            model.setNyuryokumeisaiflg(meiVo.getNyuryokumeisaiflg());
            model.setTanpinmeisaiflg(meiVo.getTanpinmeisaiflg());
            model.setStshncd(meiVo.getStshncd());
            model.setMeisaibiko(meiVo.getMeisaibiko());
            model.setNohinsuryo(meiVo.getNohinsuryo());
            model.setNohinunt(meiVo.getNohinunt());
            model.setNohinkin(meiVo.getNohinkin());
            model.setNohinuntshohitaxkin(meiVo.getNohinuntshohitaxkin());
            model.setNohinshohitaxkin(meiVo.getNohinshohitaxkin());
            model.setUridnpyno(meiVo.getUridnpyno());
            model.setUridnpyedano(meiVo.getUridnpyedano());
            model.setUridnpymeisaino(meiVo.getUridnpymeisaino());
            model.setUridnpymeisaiedano(meiVo.getUridnpymeisaiedano());
            // ■:2 Isac)hikoso 2007-06-26
            // 商品の場合
        } else {
            model.setLctgrycd(meiVo.getLctgrycd());
            model.setMctgrycd(meiVo.getMctgrycd());
            model.setSctgrycd(meiVo.getSctgrycd());
            model.setShncd(meiVo.getShncd());
            model.setKatasiki(meiVo.getKatasiki());
            model.setKeijokbn(meiVo.getKeijokbn());
            model.setAcptsuryo(meiVo.getAcptsuryo());
            /* 2017-12-05 IBIS)inoue update start */
            if (CommonConstants.KBN_SAKUSEIMOTO_JUCHUYOYAKU.equals(denVo.getDatsksmotokbn())) {
                /* 2017-08-07 IBIS)Shinoda add start */
                model.setSoukaisu(meiVo.getSoukaisu());
                model.setZankaisu(meiVo.getZankaisu());
                /* 2017-08-07 IBIS)Shinoda add end */
            }
            /* 2017-12-05 IBIS)inoue update end */
            model.setHyojununt(meiVo.getHyojununt());
            model.setAcptunt(meiVo.getAcptunt());
            model.setTaxkbn(meiVo.getTaxkbn());
            // ■:1 Base)Arai 2014-01-24 セット商品消費税補正対応
            // model.setYusyokbn(S01JuchuRelationVo.getYushokbn(meiVo.getKeijokbn(),
            // meiVo.getAcptunt()));
            // model.setMeisaikbn(CommonConstants.KBN_MEISAI_SHOHIN);
            if (CommonConstants.KBN_MEISAI_SHOHIZEI.equals(meiVo.getMeisaikbn())) {
                model.setYusyokbn(meiVo.getYusyokbn());
                model.setMeisaikbn(meiVo.getMeisaikbn());
            } else {
                model.setYusyokbn(JuchuRelationVo.getYushokbn(meiVo.getKeijokbn(),
                    meiVo.getAcptunt().toString()));
                model.setMeisaikbn(CommonConstants.KBN_MEISAI_SHOHIN);
            }
            // ■:2 Base)Arai 2014-01-24 セット商品消費税補正対応
            model.setNyuryokumeisaiflg(meiVo.getNyuryokumeisaiflg());
            model.setTanpinmeisaiflg(meiVo.getTanpinmeisaiflg());
            model.setStshncd(meiVo.getStshncd());
            model.setMeisaibiko(meiVo.getMeisaibiko());
            // ■:1 Isac)hikoso 2007-06-14
            if (meiVo.getNohinsuryo() != null) {
                model.setNohinsuryo(meiVo.getNohinsuryo());
                model.setNohinunt(meiVo.getNohinunt());
                model.setNohinkin(meiVo.getNohinkin());
                model.setNohinuntshohitaxkin(meiVo.getNohinuntshohitaxkin());
                model.setNohinshohitaxkin(meiVo.getNohinshohitaxkin());
                model.setUridnpyno(meiVo.getUridnpyno());
                model.setUridnpyedano(meiVo.getUridnpyedano());
                model.setUridnpymeisaino(meiVo.getUridnpymeisaino());
                model.setUridnpymeisaiedano(meiVo.getUridnpymeisaiedano());
            }
            // ■:2 Isac)hikoso 2007-06-14

            model.setAcptkin(meiVo.getAcptkin());
            model.setAcptuntshohitaxkin(meiVo.getAcptuntshohitaxkin());
            model.setAcptshohitaxkin(meiVo.getAcptshohitaxkin());
        }
    }

    /**
     * 新規受注取込ワークモデルより受注伝票モデルにプロパティをセットします
     * 
     * @param model 受注伝票Model
     * @param shopModel ショップマスタモデル
     * @return S01JuchuDenpyoVO 受注伝票VO
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    private JuchuDenpyoVO createDenVoFromShinkiWork(S04W001TEntity model,
        S02M046TEntity shopModel) {

        // 得意先情報より受注伝票作成
        JuchuDenpyoVO vo = getTokuisakiDlvryInfo(model.getApkihoncd(), model.getShopcd(),
            model.getTokuisakicd(), model.getAcptdd());

        // 更新用プロパティセット
        vo.setEdano(Short.valueOf("0"));
        vo.setAcptkbn(CommonConstants.KBN_ACPT_SHOHIN);
        vo.setAcptkbnshs(CommonConstants.KBN_ACPTSYS_NONE);
        vo.setAcptdd(model.getAcptdd());
        vo.setAcpttnshaapkihoncd(model.getAcpttnshaapkihoncd());
        vo.setAcpttnshashopcd(model.getAcpttnshashopcd());
        vo.setAcpttnshacd(model.getAcpttnshacd());
        vo.setDlvryytidd(model.getDlvryytidd());
        vo.setDlvrykibojikan(model.getDlvrykibojikan());
        // ADD >> 20180424 TJS)takio A1052対応
        vo.setDlvrykibojikanshry("");
        // ADD << 20180424 TJS)takio
        vo.setDlvrykibojikankbn(model.getDlvrykibojikankbn());
        vo.setKybtln1n2rittoruhns(model.getKybtln1n2rittoruhns());
        vo.setKybtln8rittoruhns(model.getKybtln8rittoruhns());
        vo.setFzaiokikanoflg(model.getFzaiokikanoflg());
        vo.setPrntmemon3(model.getPrntmemo());
        vo.setDnpysts(CommonConstants.DNPYSTS_02);
        vo.setDatsksmotokbn(CommonConstants.KBN_SAKUSEIMOTO_JUCHUTORIKOMI);
        vo.setSksmotokanrikbn(model.getSksmotokanrikbn());
        vo.setSksmotokanrino(model.getSksmotokanrino());
        vo.setZaikokanrikbnFromShopModel(shopModel);

        // 得意先が卸対象の場合は税計算区分に外税をセットする
        vo.setTaxkeisankbn(
            TokuisakiRelationVo.checkOroshi(vo.getTokuisakikbn()) ? CommonConstants.KBN_TAXKEISAN_EX
                : S02M046TEntityDaoEx.getCurrentTaxkeisankbnuri(shopModel, vo.getAcptdd()));

        return vo;
    }

    // ■:1 Isac）matsuo 2012-02-03
    /**
     * システム管理マスタより受注受付経路区分を取得します。
     * 
     * @return 受注受付経路区分
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public String getAcptrcptkeirokbn(String sksmotoKanriKbn) {

        S02M006TEntityKey entityKey = new S02M006TEntityKey();
        entityKey.setApkihoncd("000");
        entityKey.setShopcd("000");
        entityKey.setSetteino("146");
        entityKey.setRcrdkbn("M");
        entityKey.setSetteicd(sksmotoKanriKbn);;

        S02M006TDao dao = s02m006tDao;
        S02M006TEntity model = dao.selectByPrimaryKey(entityKey);
        if (model == null) {
            return "99";
        } else if (StringUtil.isEmpty(model.getMojisetteiatai())) {
            return "99";
        } else {
            return model.getMojisetteiatai();
        }
    }

    // ■:2 Isac）matsuo 2012-02-03

    /**
     * 新規受注取込ワークモデルより受注明細モデルにプロパティをセットします
     * 
     * @param denVo 受注伝票VO
     * @param model 受注伝票Model
     * @param priceVo 価格情報ＶＯ
     * @param fileVO ファイルＶＯ
     * @return List 受注明細リスト
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    private List<JuchuMeisaiVO> createMeiVoFromShinkiWork(JuchuDenpyoVO denVo, S04W001TEntity model,
        PriceInfoVo priceVo, FileShinkiJuchuVO fileVO) {
        List<JuchuMeisaiVO> meisaiList = new ArrayList<JuchuMeisaiVO>();

        // 商品サービス取得
        // ShohinService shohinService = new ShohinService();

        for (int i = 0; i < fileVO.getMeisaiCount(); i++) {
            JuchuMeisaiVO meiVo = new JuchuMeisaiVO();
            String shncd = null;
            switch (i) {
                case 0:
                    shncd = model.getShncdn1();
                    break;
                case 1:
                    shncd = model.getShncdn2();
                    break;
                case 2:
                    shncd = model.getShncdn3();
                    break;
                case 3:
                    shncd = model.getShncdn4();
                    break;
                case 4:
                    shncd = model.getShncdn5();
                    break;
                case 5:
                    shncd = model.getShncdn6();
                    break;
            }

            if (!StringUtil.isEmpty(shncd)) {

                // 商品マスタビュー情報取得
                S02V001TEntity shohinModel =
                    shohinService.getShohinModel(model.getApkihoncd(), model.getShopcd(), shncd);
                meiVo.setApkihoncd(model.getApkihoncd());
                meiVo.setShopcd(model.getShopcd());
                meiVo.setEdano(Short.valueOf("0"));
                meiVo.setAcptdnpymeisaiedano(Short.valueOf("0"));
                meiVo.setLctgrycd(shohinModel.getLctgrycd());
                meiVo.setMctgrycd(shohinModel.getMctgrycd());
                meiVo.setSctgrycd(shohinModel.getSctgrycd());
                meiVo.setDatsksmotokbn(denVo.getDatsksmotokbn());
                meiVo.setTaxkbn(shohinModel.getTaxkbn());
                meiVo.setMeisaikbn(CommonConstants.KBN_MEISAI_SHOHIN);
                meiVo.setNyuryokumeisaiflg(CommonConstants.FLG_ON);
                meiVo.setTanpinmeisaiflg(
                    CommonConstants.FLG_ON.equals(shohinModel.getStkbn()) ? CommonConstants.FLG_OFF
                        : CommonConstants.FLG_ON);

                // 標準単価設定用にプロパティをセットする
                meiVo.setHyojunpricekirikaedd(shohinModel.getHyojunpricekirikaedd());
                meiVo.setGenzhyojunprice(shohinModel.getGenzhyojunprice().toString());
                meiVo.setShinhyojunprice(shohinModel.getShinhyojunprice().toString());

                // 2020/11/26 価格改定対応 ISAC)金谷 START
                // 現標準持ち帰り価格に現オープン持ち帰り価格をセットしていたため、現持ち帰り価格をセットするように修正
                meiVo.setGenzhyojunmochikaeriprice(shohinModel.getGenzmochikaeriprice().toString());
                // 2020/11/26 価格改定対応 ISAC)金谷 END

                meiVo.setShinhyojunmochikaeriprice(shohinModel.getShinmochikaeriprice().toString());

                switch (i) {
                    case 0:
                        meiVo.setShncd(model.getShncdn1());
                        meiVo.setKatasiki(model.getKatasikin1());
                        meiVo.setKeijokbn(model.getKeijokbnn1());
                        meiVo.setAcptsuryo(model.getAcptsuryon1());
                        meiVo.setAcptunt(model.getAcptuntn1());
                        meiVo.setMeisaibiko(model.getMeisaibikon1());
                        break;
                    case 1:
                        meiVo.setShncd(model.getShncdn2());
                        meiVo.setKatasiki(model.getKatasikin2());
                        meiVo.setKeijokbn(model.getKeijokbnn2());
                        meiVo.setAcptsuryo(model.getAcptsuryon2());
                        meiVo.setAcptunt(model.getAcptuntn2());
                        meiVo.setMeisaibiko(model.getMeisaibikon2());
                        break;
                    case 2:
                        meiVo.setShncd(model.getShncdn3());
                        meiVo.setKatasiki(model.getKatasikin3());
                        meiVo.setKeijokbn(model.getKeijokbnn3());
                        meiVo.setAcptsuryo(model.getAcptsuryon3());
                        meiVo.setAcptunt(model.getAcptuntn3());
                        meiVo.setMeisaibiko(model.getMeisaibikon3());
                        break;
                    case 3:
                        meiVo.setShncd(model.getShncdn4());
                        meiVo.setKatasiki(model.getKatasikin4());
                        meiVo.setKeijokbn(model.getKeijokbnn4());
                        meiVo.setAcptsuryo(model.getAcptsuryon4());
                        meiVo.setAcptunt(model.getAcptuntn4());
                        meiVo.setMeisaibiko(model.getMeisaibikon4());
                        break;
                    case 4:
                        meiVo.setShncd(model.getShncdn5());
                        meiVo.setKatasiki(model.getKatasikin5());
                        meiVo.setKeijokbn(model.getKeijokbnn5());
                        meiVo.setAcptsuryo(model.getAcptsuryon5());
                        meiVo.setAcptunt(model.getAcptuntn5());
                        meiVo.setMeisaibiko(model.getMeisaibikon5());
                        break;
                    case 5:
                        meiVo.setShncd(model.getShncdn6());
                        meiVo.setKatasiki(model.getKatasikin6());
                        meiVo.setKeijokbn(model.getKeijokbnn6());
                        meiVo.setAcptsuryo(model.getAcptsuryon6());
                        meiVo.setAcptunt(model.getAcptuntn6());
                        meiVo.setMeisaibiko(model.getMeisaibikon6());
                        break;
                }

                // 標準単価の設定
                setCurrentHyojununt(denVo, meiVo);

                // 消費税、金額の設定
                /* 2019-07-15 Pkut)Yang 軽減税率対応 add begin */
                if (("4".equals(meiVo.getTaxkbn()) || "5".equals(meiVo.getTaxkbn()))
                    && "20190930".compareTo(denVo.getDlvryytidd()) >= 0) {
                    meiVo.setTaxkbn("1");
                }
                String[] priceList = null;
                if (CommonConstants.KBN_TAX_KOMEISAI.equals(meiVo.getTaxkbn())) {
                    // KouseihinService s01KouseihinService = new KouseihinService();
                    priceList = kouseihinService.getKouseihinTax(denVo.getApkihoncd(),
                        denVo.getShopcd(), meiVo.getShncd(), String.valueOf(meiVo.getAcptsuryo()),
                        denVo.getTokuisakicd(), meiVo.getKeijokbn(), denVo.getDlvryytidd());
                } else {
                    priceList =
                        priceService
                            .getPriceList(priceVo, meiVo.getAcptunt().toString(),
                                meiVo.getAcptsuryo().toString(), JuchuMeisaiVO
                                    .getShnTaxKbnByKeijoKbn(meiVo.getKeijokbn(), meiVo.getTaxkbn()),
                                denVo.getTokuisakikbn());
                }
                /* 2019-07-15 Pkut)Yang 軽減税率対応 add end */
                // ■2: Fsol) wangjing 2011-02-10 End
                meiVo.setAcptuntshohitaxkin(Long.valueOf(priceList[0]));
                meiVo.setAcptshohitaxkin(Long.valueOf(priceList[1]));
                meiVo.setAcptkin(Long.valueOf(priceList[2]));

                meisaiList.add(meiVo);
            }
        }

        return meisaiList;
    }

    /**
     * 現在の標準単価をセットします
     * 
     * @param denVo 受注伝票VO
     * @param meiVo 受注明細VO
     */
    public void setCurrentHyojununt(JuchuDenpyoVO denVo, JuchuMeisaiVO meiVo) {
        // ■:1 Isac)Shiba 2020-12-16 価格改定対応にてリファクタリング
        // 2020/11/19 価格改定対応 ISAC)金谷 START
        // S01JuchuMeisaiVOに移動（リファクタリング）
        meiVo.setCurrentHyojununt(denVo.getDlvrykbn(), denVo.getDlvryytidd());
        // 2020/11/19 価格改定対応 ISAC)金谷 END
        // ■:2 Isac)Shiba 2020-12-16 価格改定対応にてリファクタリング
    }

    /**
     * 現在の受注単価をセットします
     * 
     * @param denVo 受注伝票VO
     * @param meiVo 受注明細VO
     * @param priceVo 価格情報VO
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public void setCurrentAcptunt(JuchuDenpyoVO denVo, JuchuMeisaiVO meiVo, PriceInfoVo priceVo) {
        // S01JuchuMeisaiVOに移動（リファクタリング）
        meiVo.setCurrentAcptunt(denVo.getDlvrykbn(), denVo.getDlvryytidd());
        // ■:2 Isac)Shiba 2020-12-16 価格改定対応にてリファクタリング

        // 初期表示価格の設定
        meiVo.setShokidsplyprice(meiVo.getAcptunt().toString());

        // 消費税、金額の算出
        /* 2019-07-15 Pkut)Yang 軽減税率対応 add begin */
        String[] priceList = null;
        String targetDD = "";
        if (StringUtil.isEmpty(denVo.getDlvrydd())) {
            // 配達日がセットされていない場合は配達予定日をセット(主に受注～配達予定データ向け)
            targetDD = denVo.getDlvryytidd();
        } else {
            // 配達日をセット(主に配達実績登録向け)
            targetDD = denVo.getDlvrydd();
        }

        if (CommonConstants.KBN_TAX_KOMEISAI.equals(meiVo.getShntaxkbn())) {
            // KouseihinService s01KouseihinService = new KouseihinService();
            priceList = kouseihinService.getKouseihinTax(denVo.getApkihoncd(), denVo.getShopcd(),
                meiVo.getShncd(), String.valueOf(meiVo.getAcptsuryo()), denVo.getTokuisakicd(),
                meiVo.getKeijokbn(), targetDD);
        } else {
            priceList = priceService.getPriceList(priceVo, meiVo.getAcptunt().toString(),
                meiVo.getAcptsuryo().toString(),
                JuchuMeisaiVO.getShnTaxKbnByKeijoKbn(meiVo.getKeijokbn(), meiVo.getShntaxkbn()),
                denVo.getTokuisakikbn());
        }
        /* 2019-07-15 Pkut)Yang 軽減税率対応 add end */
        meiVo.setAcptuntshohitaxkin(Long.valueOf(priceList[0]));
        meiVo.setAcptshohitaxkin(Long.valueOf(priceList[1]));
        meiVo.setAcptkin(Long.valueOf(priceList[2]));
    }

    /**
     * 得意先別セット商品別単価を取得します。 （適用期間外の場合はnullになります）
     * 
     * @param denVo 受注伝票VO
     * @param meiVo 受注明細VO
     * @return String 結果
     */
    public String getTokuisakiSetTanka(JuchuDenpyoVO denVo, JuchuMeisaiVO meiVo) {
        // S01JuchuMeisaiVOに移動（リファクタリング）
        return meiVo.getTokuisakiSetTanka(denVo.getDlvrykbn(), denVo.getDlvryytidd());
    }
    // 2020/11/18 価格改定対応 ISAC)金谷 END

    /**
     * セット消費の親商品消費税額と展開行の消費税額合計が一致しない場合に 消費税調整として明細を追加する。
     */
    /* 2017-12-13 Base)Chokenkun update begin 障害対応(No.IT0028) */
    private void addShohizeiChoseiMeisai(JuchuDenpyoVO denVo, JuchuRelationVo relation,
        JuchuMeisaiVO meiVo, boolean isHaitatsuJisseki, List<JuchuMeisaiVO> subList,
        String zeiChoseiShncd, BigDecimal addAcptTaxKin, BigDecimal addNohinTaxKin,
        BigDecimal addAiteTaxKin) {
        /* 2017-12-13 Base)Chokenkun update end 障害対応(No.IT0028) */
        JuchuMeisaiVO lastChildVo = (JuchuMeisaiVO) subList.get(subList.size() - 1);
        JuchuMeisaiVO addChildVo = new JuchuMeisaiVO();

        // 商品マスタビュー情報取得
        S02V001TEntity shohinModelKosei = shohinService.getShohinModel(lastChildVo.getApkihoncd(),
            lastChildVo.getShopcd(), zeiChoseiShncd);
        if (shohinModelKosei == null) {
            // エラー処理 ショップ商品に登録されていない
            String s = MessageService.getMessage(MessageConstants.MSG_W084,
                new String[] {"ショップ商品マスタに商品コード：" + zeiChoseiShncd});
            throw new BusinessException(s);
        }

        // プロパティ値のセット
        // meiVo.readProperties(addChildVo);
        BeanUtils.copyProperties(meiVo, addChildVo);

        addChildVo.setAcptdnpymeisaiedano((short) (lastChildVo.getAcptdnpymeisaiedano() + 1));
        /* 2017-12-13 Base)Chokenkun update begin 障害対応(No.IT0028) */
        if (meiVo.getItakuaiteacptdnpymeisaino() != null) {
            addChildVo.setItakuaiteacptdnpymeisaiedano(
                (short) (lastChildVo.getItakuaiteacptdnpymeisaiedano() + 1));
        }
        /* 2017-12-13 Base)Chokenkun update end 障害対応(No.IT0028) */
        addChildVo.setShncd(zeiChoseiShncd);
        addChildVo.setKatasiki(null);
        addChildVo.setStshncd(meiVo.getShncd());
        addChildVo.setNyuryokumeisaiflg(CommonConstants.FLG_OFF);
        addChildVo.setTanpinmeisaiflg(CommonConstants.FLG_ON);
        // 軽減税率対応 消費税調整 2019-05-21 ryu Start
        shohinModelKosei.setTaxkbn(meiVo.getTaxkbn());
        // 軽減税率対応 消費税調整 2019-05-21 ryu End
        addChildVo.setUpdateProperties(relation, shohinModelKosei);
        addChildVo.setMeisaikbn(CommonConstants.KBN_MEISAI_SHOHIZEI);
        addChildVo.setYusyokbn(CommonConstants.KBN_YUSHO_NONE);

        // 受注数量/納品数量のセット
        if (isHaitatsuJisseki) {
            // 配達実績
            if (meiVo.getAcptsuryo() == null
                || meiVo.getAcptsuryo().compareTo(BigDecimal.ZERO) == 0) {
                addChildVo.setAcptsuryo(new BigDecimal("0"));
            } else {
                addChildVo.setAcptsuryo(new BigDecimal("1"));
            }
            addChildVo.setNohinsuryo(new BigDecimal("1"));
        } else {
            // 受注～配達予定
            addChildVo.setAcptsuryo(new BigDecimal("1"));
            addChildVo.setNohinsuryo(new BigDecimal("0"));
        }

        // 標準単価のセット
        addChildVo.setHyojununt(new BigDecimal(S02V001TEntityDaoEx
            .getCurrentHyojunprice(shohinModelKosei, denVo.getDlvrykbn(), denVo.getAcptdd())));

        // 親商品が有料の場合は構成商品の計上区分をセットし、金額を算出する
        if (JuchuRelationVo.isYuryo(meiVo.getKeijokbn())) {

            if (isHaitatsuJisseki) {
                // 配達実績
                addChildVo.setAcptunt(new BigDecimal("0"));
                addChildVo.setAcptkin(Long.valueOf("0"));
                addChildVo.setAcptuntshohitaxkin(Long.valueOf("0"));
                if (addChildVo.getAcptsuryo().compareTo(BigDecimal.ZERO) == 0) {
                    addChildVo.setAcptshohitaxkin(Long.valueOf("0"));
                } else {
                    addChildVo.setAcptshohitaxkin(addAcptTaxKin.longValue());
                }
                /* 2017-12-13 Base)Chokenkun update begin 障害対応(No.IT0028) */
                if (meiVo.getItakuaiteacptdnpymeisaino() != null) {
                    addChildVo.setItakucmpnyacptunt("0");
                    addChildVo.setItakucmpnyacptuntshohitaxkin("0");
                    if (addChildVo.getAcptsuryo().compareTo(BigDecimal.ZERO) == 0) {
                        addChildVo.setItakucmpnyacptshohitaxkin("0");
                    } else {
                        addChildVo.setItakucmpnyacptshohitaxkin(
                            addAiteTaxKin == null ? "0" : addAiteTaxKin.toString());
                    }
                }
                /* 2017-12-13 Base)Chokenkun update end 障害対応(No.IT0028) */
                addChildVo.setNohinunt(new BigDecimal("0"));
                addChildVo.setNohinkin(Long.valueOf("0"));
                addChildVo.setNohinuntshohitaxkin(Long.valueOf("0"));
                addChildVo.setNohinshohitaxkin(addNohinTaxKin.longValue());
            } else {
                // 受注～配達予定
                addChildVo.setAcptunt(new BigDecimal("0"));
                addChildVo.setAcptkin(Long.valueOf("0"));
                addChildVo.setAcptuntshohitaxkin(Long.valueOf("0"));
                addChildVo.setAcptshohitaxkin(addAcptTaxKin.longValue());
                addChildVo.setNohinunt(new BigDecimal("0"));
                addChildVo.setNohinkin(Long.valueOf("0"));
                addChildVo.setNohinuntshohitaxkin(Long.valueOf("0"));
                addChildVo.setNohinshohitaxkin(Long.valueOf("0"));

                // 2021/01/20 価格改定対応 ISAC)Shiba START
                addChildVo
                    .setItakucmpnyacptunt(addAiteTaxKin == null ? null : addAiteTaxKin.toString());
                addChildVo.setItakucmpnyacptuntshohitaxkin(
                    addAiteTaxKin == null ? null : addAiteTaxKin.toString());
                if (addAiteTaxKin == null) {
                    addChildVo.setItakucmpnyacptshohitaxkin((String) null);
                } else if (addChildVo.getAcptsuryo().compareTo(BigDecimal.ZERO) == 0) {
                    addChildVo.setItakucmpnyacptshohitaxkin("0");
                } else {
                    addChildVo.setItakucmpnyacptshohitaxkin(addAiteTaxKin.toString());
                }
                // 2021/01/20 価格改定対応 ISAC)Shiba END
            }
            addChildVo.setKeijokbn(meiVo.getKeijokbn());

            subList.add(addChildVo);
        }
    }

    // ■:2 Base)Arai 2014-01-24 セット商品消費税補正対応
    // ■:1 Base)Arai 2014-02-05 消費税調整対応
    /**
     * 受注配達明細テーブルのプライマリーキー順にソートする
     */
    public void juchuMeisaiComparator(List<JuchuMeisaiVO> meisaiList) {
        Collections.sort(meisaiList, new S01JuchuServiceExpandJuchuMeisaiComparator());
    }

    /**
     * 受注配達明細テーブルのプライマリーキー順にソートする
     * 
     * @author base
     * @version 0.0.0 2014-02-05
     */
    private class S01JuchuServiceExpandJuchuMeisaiComparator implements Comparator<JuchuMeisaiVO> {

        public int compare(JuchuMeisaiVO obj1, JuchuMeisaiVO obj2) {
            JuchuMeisaiVO vo1 = (JuchuMeisaiVO) obj1;
            JuchuMeisaiVO vo2 = (JuchuMeisaiVO) obj2;
            // AP比較(文字比較)
            if (!vo1.getApkihoncd().equals(vo2.getApkihoncd())) {
                return vo1.getApkihoncd().compareTo(vo2.getApkihoncd());
            }
            // SHOP比較(文字比較)
            if (!vo1.getShopcd().equals(vo2.getShopcd())) {
                return vo1.getShopcd().compareTo(vo2.getShopcd());
            }
            // 伝票番号比較(文字比較)
            if (!StringUtil.isEmpty(vo1.getAcptdnpyno())
                && !StringUtil.isEmpty(vo2.getAcptdnpyno())) {
                if (!vo1.getAcptdnpyno().equals(vo2.getAcptdnpyno())) {
                    return vo1.getAcptdnpyno().compareTo(vo2.getAcptdnpyno());
                }
            }
            // 枝番比較(数値比較)
            if (vo1.getEdano() != null && vo2.getEdano() != null) {
                Integer edano1 = Integer.valueOf(vo1.getEdano());
                Integer edano2 = Integer.valueOf(vo2.getEdano());
                if (edano1.intValue() != edano2.intValue()) {
                    return edano1.compareTo(edano2);
                }
            }
            // 明細番号比較(数値比較)
            Integer edamei1 = Integer.valueOf(vo1.getAcptdnpymeisaino());
            Integer edamei2 = Integer.valueOf(vo2.getAcptdnpymeisaino());
            if (edamei1.intValue() != edamei2.intValue()) {
                return edamei1.compareTo(edamei2);
            }
            // 明細枝番比較(数値比較)
            Integer edameino1 = Integer.valueOf(vo1.getAcptdnpymeisaiedano());
            Integer edameino2 = Integer.valueOf(vo2.getAcptdnpymeisaiedano());
            return edameino1.compareTo(edameino2);
        }

    }
    // ■:2 Base)Arai 2014-02-05 消費税調整対応

    /* 2017-06-27 IBIS)Hiraishi add begin */
    /**
     * 受注予約情報の更新を行います。
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param acptdnpyno 受注伝票番号
     * @param edano 枝番号
     * @param meisaiNoList 明細番号リスト
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public void juchuYykKshn(String apkihoncd, String shopcd, String acptdnpyno, String edano,
        List<Integer> meisaiNoList, int delCount, DbManagementSession dbManagementSession) {

        JuchuDenpyoVO juchudenpyovo1 = new JuchuDenpyoVO();

        List<JuchuMeisaiVO> meisaiList = new ArrayList<JuchuMeisaiVO>();

        // 受注予約情報を取得
        /* 2017-11-15 ibis)Shinoda update start */
        juchudenpyovo1 = getJuchuDenpyoMeisai(false, false, apkihoncd, shopcd, acptdnpyno, edano);
        /* 2017-11-15 ibis)Shinoda update end */

        JuchuDenpyoVO juchudenpyovo2 = juchudenpyovo1;

        // 新規作成受注予約情報を作成
        // 明細番号リストがNULLで無い場合
        if (meisaiNoList != null) {

            for (int i = 0; i < juchudenpyovo1.getMeisaiList().size(); i++) {
                JuchuMeisaiVO meiVo = (JuchuMeisaiVO) juchudenpyovo1.getMeisaiList().get(i);

                // 受注伝票VO①．受注明細番号＝パラメータ．明細番号リストの場合かつ受注伝票VO①．残回数が2以上の受注明細が存在する場合
                Integer acptdnpymeisaino = meiVo.getAcptdnpymeisaino();

                for (int j = 0; j < meisaiNoList.size(); j++) {
                    if (acptdnpymeisaino.equals(meisaiNoList.get(j))) {

                        if (meiVo.getZankaisu() != null) {
                            if (delCount > 0) {
                                meiVo.setZankaisu((short) (meiVo.getZankaisu() - delCount));
                            } else {
                                meiVo.setZankaisu((short) (meiVo.getZankaisu() - 1));
                            }
                        } else {
                            meiVo.setZankaisu(Short.valueOf("0"));
                        }

                        break;


                    }
                }
                if (meiVo.getZankaisu() >= 1) {
                    // 明細リストに格納
                    meisaiList.add(meiVo);
                }
            }

            juchudenpyovo2.setMeisaiList(meisaiList);


        }

        // 受注予約情報を登録します。
        if (meisaiList.size() >= 1) {

            // 受注伝票番号を採番する。
            // S01SaibanService saibanService = new S01SaibanService(tran);
            juchudenpyovo2.setAcptdnpyno(saibanService.getSlipNoJuchu(apkihoncd, shopcd));

            /* 2018-04-27 ibis)Shinoda add start */
            // 本数、金額の集計
            JuchuSumVo sumVo = new JuchuSumVo();
            JuchuRelationVo relation = getJuchuRelation(juchudenpyovo2);

            for (int i = meisaiList.size() - 1; 0 <= i; i--) {
                JuchuMeisaiVO meiVo = (JuchuMeisaiVO) meisaiList.get(i);
                if (CommonConstants.KBN_MEISAI_SHOHIN.equals(meiVo.getMeisaikbn())) {

                    // 受注関係VO生成
                    meiVo.setRelationVo(relation.createHalfClone(meiVo));

                    // 本数、金額、サーバー交換台数の集計（セット商品は対象外）
                    if (meiVo.isCountable()) {
                        sumVo.addAll(meiVo, meiVo.getRelationVo());
                    }
                }
            }
            juchudenpyovo2.setSumvo(sumVo);
            /* 2018-04-27 ibis)Shinoda add end */
            // インボイス対応 : 単体伝票締めフラグ
            juchudenpyovo2.setSingldnpyflg("0");

            insertJuchuDenpyo(juchudenpyovo2, false, dbManagementSession);

            insertJuchuMeisai(juchudenpyovo2, juchudenpyovo2.getMeisaiList(), dbManagementSession);
        }
    }

    /* 2017-06-27 IBIS)Hiraishi add end */

    // copied from S01JuchuRelationVo
    /**
     * アクアクララ「レギュラー」の商品コードを取得します。 - List
     * 
     * @param s02Service s02サービスクラス
     * @return ArrayList 「レギュラー」の商品コード - List
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public static ArrayList<String> getSystemRegularcdList(CommonService s02Service) {
        return s02Service.getShncd(CommonConstants.SCTGRYCD_001);
    }

    /**
     * アクアクララ「スリム」の商品コードを取得します。 - List
     * 
     * @param s02Service s02サービスクラス
     * @return ArrayList 「スリム」の商品コード - List
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public static ArrayList<String> getSystemSlimcdList(CommonService s02Service) {
        return s02Service.getShncd(CommonConstants.SCTGRYCD_002);
    }
    // ■:2 Base)chokenkun 2016-03-11 ７Ｌボトル対応

    /**
     * 「製品水」の商品大分類コードを取得します。
     * 
     * @param s02Service s02サービスクラス
     * @return String 「製品水」の商品大分類コード
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public static String getSystemSeihinsuicd(CommonService s02Service) {
        return s02Service.getSetteiatai(CommonConstants.APCD_000, CommonConstants.SYS_SHOPCD_000,
            CommonConstants.SYS_SETTEINO_096, CommonConstants.SETTEICD_096_1);
    }

    /**
     * 「サーバー関連用」の商品大分類コードを取得します。
     * 
     * @param s02Service s02サービスクラス
     * @return String 「サーバー関連用」の商品大分類コード
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public static String getSystemServerkanrencd(CommonService s02Service) {
        return s02Service.getSetteiatai(CommonConstants.APCD_000, CommonConstants.SYS_SHOPCD_000,
            CommonConstants.SYS_SETTEINO_096, CommonConstants.SETTEICD_096_4);
    }

    /**
     * 「サーバー関連本体」の商品中分類コードを取得します。
     * 
     * @param s02Service s02サービスクラス
     * @return String 「サーバー関連本体」の商品中分類コード
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public static String getSystemServerhontaicd(CommonService s02Service) {
        return s02Service.getSetteiatai(CommonConstants.APCD_000, CommonConstants.SYS_SHOPCD_000,
            CommonConstants.SYS_SETTEINO_134, CommonConstants.SETTEICD_134_41);
    }

    // ■:1 Base)chokenkun 2016-04-05 ７Lボトル預かり対応

    // ■:2 Base)chokenkun 2016-04-05 ７Lボトル預かり対応

    /**
     * 「アクアクレールＬ」の商品コードを取得します。
     * 
     * @param s02Service s02サービスクラス
     * @return String[] 「アクアクレールＬ」の商品コード
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public static String[] getSystemAquaclairL(CommonService s02Service) {
        return s02Service.getSystemSetteiNoList(CommonConstants.SYS_SETTEINO_123);
    }

    /**
     * 「アクアクレールＳ」の商品コードを取得します。
     * 
     * @param s02Service s02サービスクラス
     * @return String[] 「アクアクレールＳ」の商品コード
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public static String[] getSystemAquaclairS(CommonService s02Service) {
        return s02Service.getSystemSetteiNoList(CommonConstants.SYS_SETTEINO_124);
    }

    /**
     * 「メンテナンス料金用商品分類コード」の商品分類コードを取得します。
     * 
     * @param s02Service s02サービスクラス
     * @return String[] 「メンテナンス料金用商品分類コード」の商品分類コード
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public static String[] getSystemMaintCtgrycd(CommonService s02Service) {
        return s02Service.getSystemSetteiNoList(CommonConstants.SYS_SETTEINO_135);
    }

}
