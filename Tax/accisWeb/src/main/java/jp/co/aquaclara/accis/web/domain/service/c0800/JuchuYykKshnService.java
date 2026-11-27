package jp.co.aquaclara.accis.web.domain.service.c0800;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M006TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M007TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M009TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M013TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M046TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02V001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T002TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M007TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M007TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M009TC0800W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M013TC0800W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S04T001TC0800C01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S04T002TC0800C01Entity;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.domain.dto.c0800.JuchuRelationDto;
import jp.co.aquaclara.accis.web.domain.dto.c0800.JuchuSumDto;
import jp.co.aquaclara.accis.web.domain.dto.c0800.RegistrationInfoDto;
import jp.co.aquaclara.accis.web.domain.service.business.SaibanService;
import jp.co.aquaclara.accis.web.domain.service.business.TaxService;
import jp.co.aquaclara.accis.web.domain.service.x9901.StringUtil;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * 受注予約更新Service
 * 
 * @author accisdev041
 *
 */
@Service
@Slf4j
public class JuchuYykKshnService {

    /** システム管理マスタDAO **/
    @Autowired
    private S02M006TDao s02m006tDao;

    /** 基本商品マスタDAO **/
    @Autowired
    private S02M007TDao s02m007tDao;

    /** 得意先別商品別単価マスタDAO **/
    @Autowired
    private S02M009TDao s02m009tDao;

    /** 得意先マスタDAO **/
    @Autowired
    private S02M013TDao s02m013tDao;

    /** ショップマスタDAO **/
    @Autowired
    private S02M046TDao s02m046tDao;

    /** ショップマスタDAO **/
    @Autowired
    private S02V001TDao s02v001tDao;

    /** 受注配達テーブルDAO **/
    @Autowired
    private S04T001TDao s04t001tDao;

    /** 受注配達明細テーブルDAO **/
    @Autowired
    private S04T002TDao s04t002tDao;

    /** 採番サービス */
    @Autowired
    private SaibanService numberingService;

    /** 商品明細消費税・金額計算Service **/
    @Autowired
    private TaxService taxService;

    /**
     * 受注予約情報の更新を行います。
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param acptdnpyno 受注伝票番号
     * @param edano 枝番号
     * @param meisaiNoList 明細番号リスト
     * @param delCount 削除回数
     * @param unyodate 運用日付
     * @param dlvryjissekiFlg 配達実績フラグ（配達実績:true, 受注:false)
     * @param regInfo 登録情報Entity
     */
    public void juchuYykKshn(String apkihoncd, String shopcd, String acptdnpyno, String edano,
        List<String> meisaiNoList, short delCount, String unyodate, boolean dlvryjissekiFlg,
        RegistrationInfoDto regInfo) {

        log.debug("受注予約更新部品の処理開始");
        log.debug("\tAP基本コード ： {}", apkihoncd);
        log.debug("\tショップコード ： {}", shopcd);
        log.debug("\t受注伝票番号 ： {}", acptdnpyno);
        log.debug("\t枝番号 ： {}", edano);
        log.debug("\t受注予約明細番号リスト ： {}", meisaiNoList);
        log.debug("\t削除回数 ： {}", delCount);
        log.debug("\t運用日付 ： {}", unyodate);
        log.debug("\t配達実績フラグ ： {}", dlvryjissekiFlg);
        log.debug("\t登録情報Entity ： {}", regInfo);

        List<S04T002TC0800C01Entity> detailEntityList = new ArrayList<>();

        // 受注予約情報を取得
        S04T001TC0800C01Entity juchudenpyovo1 = getJuchuDenpyoMeisai(false, false, apkihoncd,
            shopcd, acptdnpyno, edano, unyodate, dlvryjissekiFlg);

        // データ作成元区分が"受注予約"の場合、配達予定日を設定
        if (CommonConstants.KBN_SAKUSEIMOTO_JUCHUYOYAKU.equals(juchudenpyovo1.getDatsksmotokbn())) {
            juchudenpyovo1.setDlvryytidd(unyodate); // 配達予定日
        }

        log.debug("DBから取得し直した残回数算出前の受注予約データ ： {}", juchudenpyovo1);

        S04T001TC0800C01Entity juchudenpyovo2 = juchudenpyovo1;

        // 新規作成受注予約情報を作成
        // 明細番号リストがNULLで無い場合
        if (meisaiNoList != null) {

            for (S04T002TC0800C01Entity detailEntity : juchudenpyovo1.getS04T002tEntityList()) {

                // 受注伝票VO①．受注明細番号＝パラメータ．明細番号リストの場合かつ受注伝票VO①．残回数が2以上の受注明細が存在する場合
                String acptdnpymeisaino = String.valueOf(detailEntity.getAcptdnpymeisaino());
                log.debug("DBから取得した受注配達明細番号 : {}", acptdnpymeisaino);
                for (int j = 0; j < meisaiNoList.size(); j++) {
                    if (acptdnpymeisaino.equals(meisaiNoList.get(j))) {
                        log.debug("DBから取得した受注配達明細の残回数 ： {}", detailEntity.getZankaisu());
                        if (detailEntity.getZankaisu() != null) {
                            if (delCount > 0) {
                                detailEntity
                                    .setZankaisu((short) (detailEntity.getZankaisu() - delCount));
                            } else {
                                detailEntity.setZankaisu((short) (detailEntity.getZankaisu() - 1));
                            }
                        } else {
                            detailEntity.setZankaisu((short) 0);
                        }
                        break;
                    }
                }
                log.debug("残回数（更新後） ： {}", detailEntity.getZankaisu());
                if (detailEntity.getZankaisu() != null && detailEntity.getZankaisu() >= 1) {
                    log.debug("残回数が1以上なので明細リストに追加");
                    // 明細リストに格納
                    detailEntityList.add(detailEntity);
                }
            }

            juchudenpyovo2.setS04T002tEntityList(detailEntityList);

        }

        // 受注予約情報を登録します。
        log.debug("更新対象とする受注配達明細リスト : {}", detailEntityList);
        detailEntityList.stream().forEach(m -> {
            log.debug("\t明細のデータ作成元区分 : {}", m.getDatsksmotokbn());
        });
        if (detailEntityList.size() >= 1) {

            // 受注伝票番号を採番する。
            String newAcptdnpyno = numberingService.getSlipNoJuchu(apkihoncd, shopcd);
            juchudenpyovo2.setAcptdnpyno(newAcptdnpyno);

            JuchuSumDto sumDto = new JuchuSumDto();
            sumDto.setRycnt(0);
            sumDto.setRmcnt(0);
            sumDto.setSycnt(0);
            sumDto.setSmcnt(0);
            sumDto.setOycnt(0);
            sumDto.setOmcnt(0);
            sumDto.setWmn(0);
            sumDto.setOmn(0);
            sumDto.setSvcnt(0);
            sumDto.setServerIncFg(false);

            JuchuRelationDto relation = getJuchuRelation(juchudenpyovo2);

            for (int i = detailEntityList.size() - 1; 0 <= i; i--) {
                S04T002TC0800C01Entity detailEntity = detailEntityList.get(i);
                if (CommonConstants.KBN_MEISAI_SHOHIN.equals(detailEntity.getMeisaikbn())) {

                    // 受注関係VO生成
                    setRelationDto(detailEntity, createHalfClone(relation, detailEntity));

                    // 本数、金額、サーバー交換台数の集計（セット商品は対象外）
                    if (CommonConstants.FLG_ON.equals(detailEntity.getTanpinmeisaiflg())
                        && CommonConstants.KBN_MEISAI_SHOHIN.equals(detailEntity.getMeisaikbn())) {
                        addAll(sumDto, detailEntity);
                    }
                }
            }
            juchudenpyovo2.setRycnt(sumDto.getRycnt());
            juchudenpyovo2.setRmcnt(sumDto.getRmcnt());
            juchudenpyovo2.setSycnt(sumDto.getSycnt());
            juchudenpyovo2.setSmcnt(sumDto.getSmcnt());
            juchudenpyovo2.setOycnt(sumDto.getOycnt());
            juchudenpyovo2.setOmcnt(sumDto.getOmcnt());
            juchudenpyovo2.setWmn(sumDto.getWmn());
            juchudenpyovo2.setOmn(sumDto.getOmn());
            juchudenpyovo2.setSvcnt(sumDto.getSvcnt());
            juchudenpyovo2.setServerIncFg(sumDto.isServerIncFg());

            // 受注消費税・金額計算処理
            calcTaxValue(juchudenpyovo2);

            log.debug("この内容で受注配達テーブルと受注配達明細テーブルを更新 : {}", juchudenpyovo2);
            log.debug("\t伝票のデータ作成元区分 : {}", juchudenpyovo2.getDatsksmotokbn());

            // 受注配達テーブル登録処理
            insertJuchuDenpyo(juchudenpyovo2, false, unyodate, regInfo);
            // 受注配達明細テーブル登録処理
            insertJuchuMeisai(juchudenpyovo2, unyodate, regInfo);
        }
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
     * @param unyodate 運用日付
     * @param dlvryjissekiFlg 配達実績フラグ（配達実績:true, 受注:false)
     * @return S04T001TC0800C01Entity 受注データEntity
     */
    private S04T001TC0800C01Entity getJuchuDenpyoMeisai(boolean priceSetFg, boolean removeShimeFg,
        String apkihoncd, String shopcd, String acptdnpyno, String edano, String unyodate,
        boolean dlvryjissekiFlg) {

        // ショップマスタ検索：単独TBL検索
        S02M046TEntityExample S02M046TExample = new S02M046TEntityExample();
        jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityExample.Criteria S02M046TCriteria =
            S02M046TExample.createCriteria();
        S02M046TCriteria.andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd);
        List<S02M046TEntity> s02m046tOutList = s02m046tDao.selectByExample(S02M046TExample);

        // 既存伝票・明細取得
        S04T001TC0800C01Entity acptdnpyEntity =
            getJuchuDenpyo(apkihoncd, shopcd, acptdnpyno, edano, unyodate, false);
        List<S04T002TC0800C01Entity> detailEntityList =
            getJuchuMeisai(priceSetFg, unyodate, acptdnpyEntity, dlvryjissekiFlg);

        // 在庫管理区分セット
        acptdnpyEntity.setZaikokanrikbn(
            CommonConstants.FLG_OFF.equals(s02m046tOutList.get(0).getZaikokanrikbn())
                ? CommonConstants.FLG_OFF
                : CommonConstants.FLG_ON);
        // 税計算区分セット
        acptdnpyEntity
            .setTaxkeisankbn(getCurrentValue(s02m046tOutList.get(0).getGenztaxkeisankbnuri(),
                s02m046tOutList.get(0).getShintaxkeisankbnuri(),
                s02m046tOutList.get(0).getTaxkeisankbnurikirikaedd(), unyodate));

        // 得意先情報取得
        S02M013TC0800W01Entity s02M013tIn = new S02M013TC0800W01Entity();
        s02M013tIn.setApkihoncd(apkihoncd);
        s02M013tIn.setShopcd(shopcd);
        s02M013tIn.setTokuisakicd(acptdnpyEntity.getTokuisakicd());
        s02M013tIn.setUnyodate(unyodate);
        List<S02M013TC0800W01Entity> s02M013tOutList = s02m013tDao.selectByC0800W01(s02M013tIn);

        acptdnpyEntity.setSkysakicd(s02M013tOutList.get(0).getSkysakicd());
        if (StringUtil.isEmpty(acptdnpyEntity.getDlvryareacd())) {
            acptdnpyEntity.setDlvryareacd(s02M013tOutList.get(0).getDlvryareacd());
            acptdnpyEntity.setDlvryareanm(s02M013tOutList.get(0).getDlvryareanm());
            acptdnpyEntity.setDlvryptnnm(s02M013tOutList.get(0).getDlvrypatern());
        }
        if (StringUtil.isEmpty(acptdnpyEntity.getDlvrykibojikankbn())) {
            acptdnpyEntity.setDlvrykibojikan(s02M013tOutList.get(0).getAcptjikanshiteijikoku());
            acptdnpyEntity
                .setDlvrykibojikanshry(s02M013tOutList.get(0).getAcptjikanshiteijikokushry());
            acptdnpyEntity.setDlvrykibojikankbn(s02M013tOutList.get(0).getAcptjikanshiteikbn());
        }
        if (StringUtil.isEmpty(acptdnpyEntity.getDlvryjun())) {
            acptdnpyEntity.setDlvryjun(s02M013tOutList.get(0).getDlvryjun());
        }

        // 明細プロパティセット
        JuchuRelationDto relation = getJuchuRelation(acptdnpyEntity);
        JuchuSumDto sumDto = new JuchuSumDto();
        sumDto.setRycnt(0);
        sumDto.setRmcnt(0);
        sumDto.setSycnt(0);
        sumDto.setSmcnt(0);
        sumDto.setOycnt(0);
        sumDto.setOmcnt(0);
        sumDto.setWmn(0);
        sumDto.setOmn(0);
        sumDto.setSvcnt(0);
        sumDto.setServerIncFg(false);
        for (S04T002TC0800C01Entity detailEntity : detailEntityList) {
            if (CommonConstants.KBN_MEISAI_SHOHIN.equals(detailEntity.getMeisaikbn())) {

                // 受注関係VO生成
                setRelationDto(detailEntity, createHalfClone(relation, detailEntity));

                // 商品マスタビュー情報取得
                S02V001TEntityExample shnCondition = new S02V001TEntityExample();
                shnCondition.createCriteria().andApkihoncdEqualTo(acptdnpyEntity.getApkihoncd())
                    .andShopcdEqualTo(acptdnpyEntity.getShopcd())
                    .andShncdEqualTo(detailEntity.getShncd());
                List<S02V001TEntity> S02V001TOutList = s02v001tDao.selectByExample(shnCondition);

                // プロパティ値のセット
                setUpdateProperties(detailEntity, relation, S02V001TOutList);

                if (CommonConstants.NYURYOKUMEISAIFLG_0
                    .equals(detailEntity.getNyuryokumeisaiflg())) {

                    // 商品マスタビュー情報取得
                    S02V001TEntityExample S02V001TExampleOya = new S02V001TEntityExample();
                    jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntityExample.Criteria S02V001TCriteriaOya =
                        S02V001TExampleOya.createCriteria();
                    S02V001TCriteriaOya.andApkihoncdEqualTo(detailEntity.getApkihoncd())
                        .andShopcdEqualTo(detailEntity.getShopcd())
                        .andShncdEqualTo(detailEntity.getStshncd());
                    List<S02V001TEntity> S02V001TOyaOutList =
                        s02v001tDao.selectByExample(S02V001TExampleOya);

                    detailEntity.setTaxkbn(S02V001TOyaOutList.get(0).getTaxkbn());
                }

                // 本数、金額、サーバー交換台数の集計（セット商品は対象外）
                if (CommonConstants.FLG_ON.equals(detailEntity.getTanpinmeisaiflg())
                    && CommonConstants.KBN_MEISAI_SHOHIN.equals(detailEntity.getMeisaikbn())) {
                    addAll(sumDto, detailEntity);
                }
            }
        }
        acptdnpyEntity.setRycnt(sumDto.getRycnt());
        acptdnpyEntity.setRmcnt(sumDto.getRmcnt());
        acptdnpyEntity.setSycnt(sumDto.getSycnt());
        acptdnpyEntity.setSmcnt(sumDto.getSmcnt());
        acptdnpyEntity.setOycnt(sumDto.getOycnt());
        acptdnpyEntity.setOmcnt(sumDto.getOmcnt());
        acptdnpyEntity.setWmn(sumDto.getWmn());
        acptdnpyEntity.setOmn(sumDto.getOmn());
        acptdnpyEntity.setSvcnt(sumDto.getSvcnt());
        acptdnpyEntity.setServerIncFg(sumDto.isServerIncFg());
        acptdnpyEntity.setS04T002tEntityList(detailEntityList);

        // 検索実行
        return acptdnpyEntity;

    }

    /**
     * 受注伝票情報を取得します．
     * 
     * @param apKihoncd AP基本コード
     * @param shopcd ショップコード
     * @param acptdnpyno 受注伝票番号
     * @param edano 枝番号
     * @param unyodate 運用日付
     * @param yoyakuflg 予約フラグ
     * @return S04T001TC0800C01Entity 受注データEntity
     */
    private S04T001TC0800C01Entity getJuchuDenpyo(String apKihoncd, String shopcd,
        String acptdnpyno, String edano, String unyodate, boolean yoyakuflg) {

        S04T001TC0800C01Entity s04T001TEntityIn = new S04T001TC0800C01Entity();

        s04T001TEntityIn.setApkihoncd(apKihoncd);
        s04T001TEntityIn.setShopcd(shopcd);
        s04T001TEntityIn.setAcptdnpyno(acptdnpyno);
        s04T001TEntityIn.setEdano(Short.valueOf(edano));
        s04T001TEntityIn.setUnyodate(unyodate);
        if (yoyakuflg) {
            s04T001TEntityIn.setYoyakuflag("true");
        } else {
            s04T001TEntityIn.setYoyakuflag("false");
        }

        S04T001TC0800C01Entity s04T001TEntityOut = s04t001tDao.selectByC0800C01(s04T001TEntityIn);

        // 検索実行
        return s04T001TEntityOut;

    }

    /**
     * 受注明細情報を取得します．
     * 
     * @param priceSetFg 単価セットフラグ
     * @param unyodate 運用日付
     * @param acptdnpyEntity 受注データEntity
     * @param dlvryjissekiFlg 配達実績フラグ
     * @return List 結果のリスト
     */
    private List<S04T002TC0800C01Entity> getJuchuMeisai(boolean priceSetFg, String unyodate,
        S04T001TC0800C01Entity acptdnpyEntity, boolean dlvryjissekiFlg) {

        S04T002TC0800C01Entity s04T002TEntityIn = new S04T002TC0800C01Entity();

        s04T002TEntityIn.setApkihoncd(acptdnpyEntity.getApkihoncd());
        s04T002TEntityIn.setShopcd(acptdnpyEntity.getShopcd());
        s04T002TEntityIn.setAcptdnpyno(acptdnpyEntity.getAcptdnpyno());
        s04T002TEntityIn.setEdano(Short.valueOf(acptdnpyEntity.getEdano()));
        if (dlvryjissekiFlg) {
            s04T002TEntityIn.setKakakugetbasedd(acptdnpyEntity.getDlvrydd());
        } else {
            s04T002TEntityIn.setKakakugetbasedd(acptdnpyEntity.getDlvryytidd());
        }

        List<S04T002TC0800C01Entity> s04T002TEntityOutList =
            s04t002tDao.selectByC0800C01(s04T002TEntityIn);

        // 単価の再設定を行う
        reEditUnt(s04T002TEntityOutList, priceSetFg, acptdnpyEntity);

        // 得意先マスタ検索：単独TBL検索
        S02M013TEntityExample tokuisakiCondition = new S02M013TEntityExample();
        tokuisakiCondition.createCriteria().andApkihoncdEqualTo(acptdnpyEntity.getApkihoncd())
            .andShopcdEqualTo(acptdnpyEntity.getShopcd())
            .andTokuisakicdEqualTo(acptdnpyEntity.getTokuisakicd());
        List<S02M013TEntity> s02m013tOutList = s02m013tDao.selectByExample(tokuisakiCondition);

        // 単価の再設定を行う
        for (S04T002TC0800C01Entity detailEntity : s04T002TEntityOutList) {

            // 商品・入力行のみ
            if (CommonConstants.FLG_ON.equals(detailEntity.getNyuryokumeisaiflg())
                && CommonConstants.KBN_MEISAI_SHOHIN.equals(detailEntity.getMeisaikbn())) {

                if (CommonConstants.FLG_ON
                    .equals(s02m013tOutList.get(0).getRknkindtokubetsuuntflg())) {

                    S02M009TC0800W01Entity s02M009tIn = new S02M009TC0800W01Entity();
                    s02M009tIn.setApkihoncd(acptdnpyEntity.getApkihoncd());
                    s02M009tIn.setShopcd(acptdnpyEntity.getShopcd());
                    s02M009tIn.setShncd(detailEntity.getShncd());
                    s02M009tIn.setTokuisakicd(acptdnpyEntity.getTokuisakicd());
                    if (CommonConstants.FLG_ON.equals(detailEntity.getTanpinmeisaiflg())) {
                        s02M009tIn.setTanpinmeisaiflg(CommonConstants.FLG_ON);
                    } else {
                        s02M009tIn.setTanpinmeisaiflg(CommonConstants.FLG_OFF);
                    }
                    List<S02M009TC0800W01Entity> s02M009tOutList =
                        s02m009tDao.selectByC0800C01(s02M009tIn);
                    detailEntity.setS02m009tList(s02M009tOutList);
                }
            }
        }

        return s04T002TEntityOutList;

    }

    /**
     * 明細の価格再編集
     * 
     * @param detailEntityList 受注明細データEntityリスト
     * @param priceSetFg 単価セットフラグ
     * @param acptdnpyEntity 受注データEntity
     * @return List 受注明細データEntityリスト
     */
    private List<S04T002TC0800C01Entity> reEditUnt(List<S04T002TC0800C01Entity> detailEntityList,
        boolean priceSetFg, S04T001TC0800C01Entity acptdnpyEntity) {

        JuchuRelationDto relationDto = getJuchuRelation(acptdnpyEntity);

        // 単価の再設定を行う
        for (S04T002TC0800C01Entity detailEntity : detailEntityList) {
            setRelationDto(detailEntity, createHalfClone(relationDto, detailEntity));

            // 商品・入力行のみ
            if (CommonConstants.FLG_ON.equals(detailEntity.getNyuryokumeisaiflg())
                && CommonConstants.KBN_MEISAI_SHOHIN.equals(detailEntity.getMeisaikbn())) {

                // 標準単価の設定
                setCurrentHyojununt(acptdnpyEntity, detailEntity);

            }
        }
        return detailEntityList;
    }

    /**
     * 受注関係DTOを生成します。
     * 
     * @param acptdnpyEntity 受注データEntity
     * @return 受注関係DTO
     */
    private JuchuRelationDto getJuchuRelation(S04T001TC0800C01Entity acptdnpyEntity) {

        // 基本商品マスタ検索：単独TBL検索：レギュラーボトルの商品コードを検索する。
        S02M007TEntityExample baseShnCondition = new S02M007TEntityExample();
        baseShnCondition.createCriteria().andApkihoncdEqualTo(CommonConstants.APCD_001)
            .andStkbnEqualTo(CommonConstants.STKBN_0)
            .andLctgrycdEqualTo(CommonConstants.LCTGRYCD_01)
            .andMctgrycdEqualTo(CommonConstants.MCTGRYCD_001)
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<S02M007TEntity> s02m007tOutList = s02m007tDao.selectByExample(baseShnCondition);

        // レギュラー商品リスト
        List<String> rgraCdList = new ArrayList<>();
        // スリム商品リスト
        List<String> srmCdList = new ArrayList<>();

        for (S02M007TEntity e : s02m007tOutList) {
            if (CommonConstants.SCTGRYCD_001.equals(e.getSctgrycd())) {
                rgraCdList.add(e.getShncd());
            } else if (CommonConstants.SCTGRYCD_002.equals(e.getSctgrycd())) {
                srmCdList.add(e.getShncd());
            }
        }

        // 受注関係DTO生成（伝票情報のみセット）
        return new JuchuRelationDto(
            getSystemMasterValue(CommonConstants.SYS_SETTEINO_096, CommonConstants.SETTEICD_096_1),
            getSystemMasterValue(CommonConstants.SYS_SETTEINO_096, CommonConstants.SETTEICD_096_4),
            getSystemMasterValue(CommonConstants.SYS_SETTEINO_134, CommonConstants.SETTEICD_134_41),
            rgraCdList, srmCdList, acptdnpyEntity.getTokuisakikbn(), acptdnpyEntity.getAcptkbn(),
            acptdnpyEntity.getAcptkbnshs(), "", "", "", "");
    }

    /**
     * 受注関係DTOを引数の受注明細情報の情報を加えて、複製します。
     * 
     * @param relationDto 受注関係DTO
     * @param detailEntity 受注明細データEntity
     * @return 受注関係DTO
     */
    private JuchuRelationDto createHalfClone(JuchuRelationDto relationDto,
        S04T002TC0800C01Entity detailEntity) {

        // 受注関係DTO生成
        return new JuchuRelationDto(relationDto.getWatercd(), relationDto.getServerlcd(),
            relationDto.getServermcd(), relationDto.getRegularcdList(), relationDto.getSlimcdList(),
            relationDto.getTokuisakikbn(), relationDto.getAcptkbn(), relationDto.getAcptkbnshs(),
            detailEntity.getLctgrycd(), detailEntity.getMctgrycd(), detailEntity.getShncd(),
            detailEntity.getKeijokbn());
    }

    /**
     * 受注関係DTOを明細へセット
     * 
     * @param detailEntity 受注明細データEntity
     * @param relationDto 受注関係DTO
     */
    private void setRelationDto(S04T002TC0800C01Entity detailEntity, JuchuRelationDto relationDto) {
        detailEntity.setWatercd(relationDto.getWatercd());
        detailEntity.setServerlcd(relationDto.getServerlcd());
        detailEntity.setServermcd(relationDto.getServermcd());
        detailEntity.setRegularcdList(relationDto.getRegularcdList());
        detailEntity.setSlimcdList(relationDto.getSlimcdList());
        detailEntity.setTokuisakikbn(relationDto.getTokuisakikbn());
        detailEntity.setAcptkbn(relationDto.getAcptkbn());
        detailEntity.setAcptkbnshs(relationDto.getAcptkbnshs());
        detailEntity.setLctgrycd(relationDto.getLctgrycd());
        detailEntity.setMctgrycd(relationDto.getMctgrycd());
        detailEntity.setShncd(relationDto.getShncd());
        detailEntity.setKeijokbn(relationDto.getKeijokbn());
    }

    /**
     * 現在の標準単価をセットします
     * 
     * @param acptdnpyEntity 受注伝票Entity
     * @param detailEntity 受注明細Entity
     */
    private void setCurrentHyojununt(S04T001TC0800C01Entity acptdnpyEntity,
        S04T002TC0800C01Entity detailEntity) {

        BigDecimal hyojununt = BigDecimal.ZERO;

        if (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(acptdnpyEntity.getDlvrykbn())) {
            hyojununt = getCurrentValue(new BigDecimal(detailEntity.getGenzmochikaeriprice()),
                new BigDecimal(detailEntity.getShinmochikaeriprice()),
                detailEntity.getHyojunpricekirikaedd(), acptdnpyEntity.getDlvryytidd());
        } else {
            hyojununt = getCurrentValue(new BigDecimal(detailEntity.getGenzhyojunprice()),
                new BigDecimal(detailEntity.getShinhyojunprice()),
                detailEntity.getHyojunpricekirikaedd(), acptdnpyEntity.getDlvryytidd());
        }

        detailEntity.setHyojununt(hyojununt);

    }


    /**
     * 切替日を判断して現／新項目を取得します。
     * 
     * @param oldValue 現項目
     * @param newVlue 新項目
     * @param switchDay 切替日
     * @param targetDay 対象日
     * @return 現または新項目
     */
    private <T> T getCurrentValue(T oldValue, T newVlue, String switchDay, String targetDay) {
        targetDay = getLastdayOfMonthYMD(targetDay);

        if (Strings.isEmpty(switchDay)) {
            return newVlue;
        } else if (Strings.isEmpty(targetDay)) {
            return oldValue;
        } else {
            if (0 <= targetDay.compareTo(switchDay)) {
                return newVlue;
            } else {
                return oldValue;
            }
        }
    }

    /**
     * 月末日を取得する(計上年月+計上日にすると、ありえない日付になるため、書き換え)
     * 
     * @param targetYMD
     * @return 月末日の日付(YYYYMMDD)
     */
    private String getLastdayOfMonthYMD(String targetYMD) {

        if (Strings.isEmpty(targetYMD) || targetYMD.length() != 8) {
            return targetYMD;
        }

        String rtnYmd = targetYMD;
        String strYYYY = targetYMD.substring(0, 4);
        String strYYYYMM = targetYMD.substring(0, 6);
        String strMMDD = targetYMD.substring(4, 8);
        int inYYYY = Integer.valueOf(strYYYY);
        boolean bNG = false;

        if ("0230".equals(strMMDD) || "0231".equals(strMMDD) || "0431".equals(strMMDD)
            || "0631".equals(strMMDD) || "0931".equals(strMMDD) || "1131".equals(strMMDD)) {
            bNG = true;
        }

        // 閏年ではない2月29日
        if (!(inYYYY % 4 == 0 && (inYYYY % 400 == 0 || inYYYY % 100 != 0))
            && "0229".equals(strMMDD)) {
            bNG = true;
        }

        if (bNG) {
            rtnYmd = getLastDayOfMonth(strYYYYMM);
        }

        return rtnYmd;
    }

    /**
     * 月末日を取得する
     * 
     * @param targetYM 対象日付の年月
     * @return 月末日の日付(YYYYMMDD)
     */
    private String getLastDayOfMonth(String targetYM) {
        Calendar cal = getDateObject(targetYM + "01");
        return targetYM
            + padFront(String.valueOf(cal.getActualMaximum(Calendar.DAY_OF_MONTH)), 2, '0');
    }

    /**
     * 文字列の先頭にpadを加えた新しいStringを返します。
     * 
     * @param s 詰め物をしたいString
     * @param length 生成したいStringの長さ
     * @param pad 詰めたいchar
     * @return 引数のStirngの先頭にpadを詰めてlengthの長さにした新しいString
     */
    private String padFront(String s, int length, char pad) {
        if (s == null) {
            s = "";
        }
        int padCount = length - s.length();
        if (padCount > 0) {
            StringBuffer b = new StringBuffer("");
            for (int i = 0; i < padCount; i++) {
                b.append(pad);
            }
            b.append(s);
            return b.toString();
        } else {
            return s;
        }
    }

    /**
     * 日付オブジェクトを取得する
     * 
     * @param targetDate 対象日付
     * @return 日付オブジェクト
     */
    private Calendar getDateObject(String targetDate) {
        Calendar cal = Calendar.getInstance();

        if (targetDate != null && targetDate.length() == 8) {
            cal.clear();
            cal.set(Integer.parseInt(targetDate.substring(0, 4)),
                Integer.parseInt(targetDate.substring(4, 6)) - 1,
                Integer.parseInt(targetDate.substring(6, 8)));
        }

        return cal;
    }

    /**
     * システム管理マスタ設定値取得
     * 
     * @param searchResultList
     * @return
     */
    private String getSystemMasterValue(String setteino, String setteicd) {

        return s02m006tDao.findConfig(setteino, setteicd);

    }

    /**
     * 更新用プロパティセット
     * 
     * @param detailEntity 受注明細データ
     * @param relationDto 受注関係
     * @param shnEntityList 商品マスタビューEntityリスト
     */
    private void setUpdateProperties(S04T002TC0800C01Entity detailEntity,
        JuchuRelationDto relationDto, List<S02V001TEntity> shnEntityList) {

        // プロパティ値のセット
        if (shnEntityList.size() > 0) {

            if (CommonConstants.FLG_OFF.equals(shnEntityList.get(0).getTaxsetteikbn())) {
                shnEntityList.get(0).setTaxkbn(null);// 課税として税計算する
            }
            detailEntity.setLctgrycd(shnEntityList.get(0).getLctgrycd());
            detailEntity.setMctgrycd(shnEntityList.get(0).getMctgrycd());
            detailEntity.setSctgrycd(shnEntityList.get(0).getSctgrycd());
            detailEntity.setTaxkbn(shnEntityList.get(0).getTaxkbn());
            // 商品マスタビュー情報セット
            detailEntity.setS02v001tEntity(shnEntityList.get(0));
        }

        // 受注関係VO生成
        setRelationDto(detailEntity, createHalfClone(relationDto, detailEntity));

    }



    /**
     * 統合集計処理。
     * 
     * @param detailEntity 受注明細データ
     */
    private void addAll(JuchuSumDto sumDto, S04T002TC0800C01Entity detailEntity) {

        // 本数集計処理
        String suryo = String.valueOf(detailEntity.getAcptsuryo());
        if (ItemUtil.isNumeric(suryo)) {
            if (isNewRegular(detailEntity)) {
                if (isYuryo(detailEntity.getKeijokbn())) {
                    sumDto.setRycnt(sumDto.getRycnt() + Long.parseLong(suryo));
                } else {
                    sumDto.setRmcnt(sumDto.getRmcnt() + Long.parseLong(suryo));
                }
            } else if (isNewSlim(detailEntity)) {
                if (isYuryo(detailEntity.getKeijokbn())) {
                    sumDto.setSycnt(sumDto.getSycnt() + Long.parseLong(suryo));
                } else {
                    sumDto.setSmcnt(sumDto.getSmcnt() + Long.parseLong(suryo));
                }
            } else if (isOther(detailEntity)) {
                if (isYuryo(detailEntity.getKeijokbn())) {
                    sumDto.setOycnt(sumDto.getOycnt() + Long.parseLong(suryo));
                } else {
                    sumDto.setOmcnt(sumDto.getOmcnt() + Long.parseLong(suryo));
                }
            }
        }

        // 金額集計処理
        String kingaku = String.valueOf(detailEntity.getAcptkin());

        if (ItemUtil.isNumeric(kingaku)) {
            if (isWater(detailEntity)) {
                sumDto.setWmn(sumDto.getWmn() + Long.parseLong(kingaku));
            } else {
                sumDto.setOmn(sumDto.getOmn() + Long.parseLong(kingaku));
            }
        }

        // サーバー集計処理
        suryo = String.valueOf(detailEntity.getAcptsuryo());

        if (ItemUtil.isNumeric(suryo)) {

            // 交換台数集計処理
            if (isServerChange(detailEntity)) {
                sumDto.setSvcnt(sumDto.getSvcnt() + Long.parseLong(suryo));
            }

            // 存在フラグ更新処理
            if (isServerBody(detailEntity, detailEntity.getLctgrycd(),
                detailEntity.getMctgrycd())) {
                sumDto.setServerIncFg(true);
            }
        }

    }

    /**
     * サーバー交換対象区分。
     * 
     * @param detailEntity 受注明細データ
     * @return boolean 結果
     */
    private boolean isServerChange(S04T002TC0800C01Entity detailEntity) {
        // 商品大分類コード＝ＳＶで、
        // 受注区分＝交換または受注区分＝メンテナンスかつ受注区分詳細＝交換のもの
        return (isServerBody(detailEntity, detailEntity.getLctgrycd(), detailEntity.getMctgrycd())
            && (CommonConstants.KBN_ACPT_KOKAN.equals(detailEntity.getAcptkbn())
                || (CommonConstants.KBN_ACPT_MAINT.equals(detailEntity.getAcptkbn())
                    && CommonConstants.KBN_ACPTSYS_KOKAN.equals(detailEntity.getAcptkbnshs()))));
    }

    /**
     * サーバー本体区分。
     * 
     * @param detailEntity 受注明細データ
     * @param lctgrycd 商品大分類コード
     * @param mctgrycd 商品中分類コード
     * @return boolean サーバーの場合はtrue、それ以外の場合はfalse
     */
    private boolean isServerBody(S04T002TC0800C01Entity detailEntity, String lctgrycd,
        String mctgrycd) {
        return (detailEntity.getServerlcd() != null && detailEntity.getServerlcd().equals(lctgrycd)
            && detailEntity.getServermcd() != null && detailEntity.getServermcd().equals(mctgrycd));
    }

    /**
     * レギュラー区分。 -- ７Ｌ対応
     * 
     * @param detailEntity 受注明細データ
     * @return boolean レギュラーの場合はtrue、それ以外の場合はfalse
     */
    private boolean isNewRegular(S04T002TC0800C01Entity detailEntity) {
        if (detailEntity.getRegularcdList() == null)
            return false;
        else
            return ((!detailEntity.getRegularcdList().isEmpty())
                && detailEntity.getRegularcdList().contains(detailEntity.getShncd()));
    }

    /**
     * スリム区分。 -- ７Ｌ対応
     * 
     * @param detailEntity 受注明細データ
     * @return boolean スリムの場合はtrue、それ以外の場合はfalse
     */
    private boolean isNewSlim(S04T002TC0800C01Entity detailEntity) {
        if (detailEntity.getSlimcdList() == null)
            return false;
        else
            return ((!detailEntity.getSlimcdList().isEmpty())
                && detailEntity.getSlimcdList().contains(detailEntity.getShncd()));
    }

    /**
     * その他製品水区分。
     * 
     * @param detailEntity 受注明細データ
     * @return boolean その他製品水の場合はtrue、それ以外の場合はfalse
     */
    private boolean isOther(S04T002TC0800C01Entity detailEntity) {
        return (isWater(detailEntity) && !isAquaclara(detailEntity));
    }

    /**
     * 製品水区分。
     * 
     * @param detailEntity 受注明細データ
     * @return boolean 製品水の場合はtrue、それ以外の場合はfalse
     */
    private boolean isWater(S04T002TC0800C01Entity detailEntity) {
        return (detailEntity.getWatercd() != null
            && detailEntity.getWatercd().equals(detailEntity.getLctgrycd()));
    }

    /**
     * アクアクララ区分。
     * 
     * @param detailEntity 受注明細データ
     * @return boolean アクアクララの場合はtrue、それ以外の場合はfalse
     */
    private boolean isAquaclara(S04T002TC0800C01Entity detailEntity) {
        return isNewRegular(detailEntity) || isNewSlim(detailEntity);
    }

    /**
     * 有料区分。
     * 
     * @param keijokbn 計上区分
     * @return boolean 有料の場合はtrue、無料の場合はfalse
     */
    private boolean isYuryo(String keijokbn) {
        return (CommonConstants.KBN_KEIJO_URIAGE.equals(keijokbn)
            || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(keijokbn));
    }

    /**
     * 受注消費税・金額計算処理
     * 
     * @param acptdnpyEntity 受注データ
     */
    private void calcTaxValue(S04T001TC0800C01Entity acptdnpyEntity) {

        // ショップマスタ検索：単独TBL検索
        S02M046TEntityExample shopCondition = new S02M046TEntityExample();
        shopCondition.createCriteria().andApkihoncdEqualTo(acptdnpyEntity.getApkihoncd())
            .andShopcdEqualTo(acptdnpyEntity.getShopcd());
        List<S02M046TEntity> s02m046tOutList = s02m046tDao.selectByExample(shopCondition);

        long acptkin = 0;
        long acptshohitaxkin = 0;
        long acptippannukikin = 0;
        long acptippantaxkin = 0;
        long acptkeigennukikin = 0;
        long acptkeigentaxkin = 0;
        long acpthikazeiuriikin = 0;
        long acptfukazeiuriikin = 0;

        for (S04T002TC0800C01Entity detailEntity : acptdnpyEntity.getS04T002tEntityList()) {

            if (CommonConstants.FLG_ON.equals(detailEntity.getNyuryokumeisaiflg())) {
                // 受注金額
                acptkin = acptkin + detailEntity.getAcptkin();
                // 受注消費税金額
                acptshohitaxkin = acptshohitaxkin + detailEntity.getAcptshohitaxkin();
                // 受注一般税抜金額
                if (CommonConstants.KBN_TAX_KAZEI.equals(detailEntity.getTaxkbn())) {
                    acptippannukikin = acptippannukikin + detailEntity.getAcptkin();
                }
                // 受注軽減税抜金額
                if (CommonConstants.KBN_TAX_KEIGEN.equals(detailEntity.getTaxkbn())) {
                    acptkeigennukikin = acptkeigennukikin + detailEntity.getAcptkin();
                }
                // 受注非課税金額
                if (CommonConstants.KBN_TAX_HIKAZEI.equals(detailEntity.getTaxkbn())) {
                    acpthikazeiuriikin = acpthikazeiuriikin + detailEntity.getAcptkin();
                }
                // 受注不課税売上金額
                if (CommonConstants.KBN_TAX_HUKAZEI.equals(detailEntity.getTaxkbn())) {
                    acptfukazeiuriikin = acptfukazeiuriikin + detailEntity.getAcptkin();
                }
            }
        }

        // 消費税金額計算を行う
        String ippanPriceList[] = taxService.getPriceList(String.valueOf(acptippannukikin), "1",
            CommonConstants.KBN_TAX_KAZEI, acptdnpyEntity.getTokuisakikbn(),
            acptdnpyEntity.getDlvryytidd(), s02m046tOutList.get(0));
        acptippantaxkin = Long.parseLong(ippanPriceList[1]);

        String keigenPriceList[] = taxService.getPriceList(String.valueOf(acptkeigennukikin), "1",
            CommonConstants.KBN_TAX_KEIGEN, acptdnpyEntity.getTokuisakikbn(),
            acptdnpyEntity.getDlvryytidd(), s02m046tOutList.get(0));
        acptkeigentaxkin = Long.parseLong(keigenPriceList[1]);

        // インボイス施行日判定
        if (acptdnpyEntity.getDlvryytidd()
            .compareTo(s02m046tOutList.get(0).getInvoicshikoymd()) >= 0) {
            acptkin =
                acptippannukikin + acptkeigennukikin + acpthikazeiuriikin + acptfukazeiuriikin;
            acptshohitaxkin = acptippantaxkin + acptkeigentaxkin;
        }

        // 受注伝票データEntityに税計算用内部変数値を設定する。
        acptdnpyEntity.setAcptkin(acptkin);
        acptdnpyEntity.setAcptshohitaxkin(acptshohitaxkin);
        acptdnpyEntity.setAcptippannukikin(acptippannukikin);
        acptdnpyEntity.setAcptippantaxkin(acptippantaxkin);
        acptdnpyEntity.setAcptkeigennukikin(acptkeigennukikin);
        acptdnpyEntity.setAcptkeigentaxkin(acptkeigentaxkin);
        acptdnpyEntity.setAcpthikazeiuriikin(acpthikazeiuriikin);
        acptdnpyEntity.setAcptfukazeiuriikin(acptfukazeiuriikin);

    }

    /**
     * 受注伝票を新規登録します．
     * 
     * @param acptdnpyEntity 受注データ
     * @param saibanFg 採番フラグ
     * @param unyodate 運用日付
     * @param regInfo 登録情報
     */
    public void insertJuchuDenpyo(S04T001TC0800C01Entity acptdnpyEntity, boolean saibanFg,
        String unyodate, RegistrationInfoDto regInfo) {

        try {
            // INSERT実行
            s04t001tDao
                .insertSelective(createInsertS04t001tEntity(acptdnpyEntity, unyodate, regInfo));
        } catch (DuplicateKeyException e) {
            // 一意制約違反の場合、エラー
            throw BusinessExceptionFactory.validationFailed(MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(MessageID.E002);
        }

    }

    /**
     * 受注配達テーブル登録時の値を設定する。
     * 
     * @param acptdnpyEntity 受注データ
     * @param unyodate 運用日付
     * @return S04T001TEntity
     */
    private S04T001TEntity createInsertS04t001tEntity(S04T001TC0800C01Entity acptdnpyEntity,
        String unyodate, RegistrationInfoDto regInfo) {

        S04T001TEntity entity = new S04T001TEntity();

        entity.setApkihoncd(acptdnpyEntity.getApkihoncd()); // AP基本コード
        entity.setShopcd(acptdnpyEntity.getShopcd()); // ショップコード
        entity.setAcptdnpyno(acptdnpyEntity.getAcptdnpyno()); // 受注伝票番号
        entity.setEdano((short) 0); // 枝番号
        entity.setItakuaiteapkihoncd(null); // 委託相手ＡＰ基本コード
        entity.setItakuaiteshopcd(null); // 委託相手ショップコード
        entity.setItakuaiteacptdnpyno(null); // 委託相手受注伝票番号
        entity.setItakuaiteedano(null); // 委託相手枝番号
        entity.setUridnpyno(null); // 売上伝票番号
        entity.setDlvrytrgtflg("1"); // 配達対象フラグ
        if (CommonConstants.KBN_TOKUISAKI_KEIHI.equals(acptdnpyEntity.getTokuisakikbn())) {
            entity.setDnpykbn(CommonConstants.KBN_KEIHI_SYUKKO);
        } else {
            entity.setDnpykbn(CommonConstants.KBN_URIAGE_SYUKKO);
        } // 伝票区分
        if (CommonConstants.KBN_TOKUISAKI_KEIHI.equals(acptdnpyEntity.getTokuisakikbn())) {
            entity.setTorihikikbn(CommonConstants.KBN_ACPTTORIHIKI_KEIHISHUKKO);
        } else if (CommonConstants.KBN_SAKUSEIMOTO_JUCHUYOYAKU
            .equals(acptdnpyEntity.getDatsksmotokbn())) {
            entity.setTorihikikbn(CommonConstants.KBN_ACPTTORIHIKI_JUCHUYOYAKU);
        } else {
            entity.setTorihikikbn(CommonConstants.KBN_ACPTTORIHIKI_JUCHU);
        } // 取引区分
        entity.setAcptkbn(acptdnpyEntity.getAcptkbn()); // 受注区分
        entity.setAcptkbnshs(acptdnpyEntity.getAcptkbnshs()); // 受注区分詳細
        entity.setTokuisakicd(acptdnpyEntity.getTokuisakicd()); // 得意先コード
        entity.setAcptdd(acptdnpyEntity.getAcptdd()); // 受注日
        entity.setAcpttnshaapkihoncd(acptdnpyEntity.getAcpttnshaapkihoncd()); // 受注担当者ＡＰ基本コード
        entity.setAcpttnshashopcd(acptdnpyEntity.getAcpttnshashopcd()); // 受注担当者ショップコード
        entity.setAcpttnshacd(acptdnpyEntity.getAcpttnshacd()); // 受注担当者コード
        if (CommonConstants.KBN_SAKUSEIMOTO_JUCHUYOYAKU.equals(acptdnpyEntity.getDatsksmotokbn())) {
            entity.setDlvryytidd(null);
        } else {
            entity.setDlvryytidd(acptdnpyEntity.getDlvryytidd());
        } // 配達予定日
        entity.setDlvryjissekidd(null); // 配達実績日
        entity.setDlvrydd(acptdnpyEntity.getDlvrydd()); // 配達日
        entity.setDlvrykibojikan(acptdnpyEntity.getDlvrykibojikan()); // 配達希望時間
        entity.setDlvrykibojikanshry(acptdnpyEntity.getDlvrykibojikanshry()); // 配達希望時間終了
        entity.setDlvrykibojikankbn(acptdnpyEntity.getDlvrykibojikankbn()); // 配達希望時間区分
        entity.setFzaiokikanoflg(acptdnpyEntity.getFzaiokikanoflg()); // 不在置き可能フラグ
        entity.setFzaiokijissekiflg(CommonConstants.FLG_OFF); // 不在置き実績フラグ
        entity.setDlvrytnshacd(acptdnpyEntity.getDlvrytnshacd()); // 配達担当者コード
        entity.setDlvrykankatsuapkihoncd(acptdnpyEntity.getDlvrykankatsuapkihoncd()); // 配達管轄ＡＰ基本コード
        entity.setDlvrykankatsushopcd(acptdnpyEntity.getDlvrykankatsushopcd()); // 配達管轄ショップコード
        entity.setDlvrykyotencd(acptdnpyEntity.getDlvrykyotencd()); // 配達拠点コード
        entity.setDlvryareacd(acptdnpyEntity.getDlvryareacd()); // 配達エリアコード
        entity.setDlvrykbn(acptdnpyEntity.getDlvrykbn()); // 配達区分
        entity.setDlvrycmpnycd(acptdnpyEntity.getDlvrycmpnycd()); // 配達会社コード
        if (acptdnpyEntity.getAzukaribtln1n2rittoruhns() == null) {
            entity.setAzukaribtln1n2rittoruhns(0);
        } else {
            entity.setAzukaribtln1n2rittoruhns(acptdnpyEntity.getAzukaribtln1n2rittoruhns());
        } // 預かりボトル１２Ｌ本数
        if (acptdnpyEntity.getAzukaribtln1n2rittoruhns() == null) {
            entity.setKybtln1n2rittoruhns((short) 0);
        } else {
            entity.setKybtln1n2rittoruhns(acptdnpyEntity.getKybtln1n2rittoruhns());
        } // 回収予定ボトル１２Ｌ本数
        if (acptdnpyEntity.getKzbtln1n2rittoruhns() == null) {
            entity.setKzbtln1n2rittoruhns((short) 0);
        } else {
            entity.setKzbtln1n2rittoruhns(acptdnpyEntity.getKzbtln1n2rittoruhns());
        } // 回収実績ボトル１２Ｌ本数
        if (acptdnpyEntity.getAzukaribtln8rittoruhns() == null) {
            entity.setAzukaribtln8rittoruhns(0);
        } else {
            entity.setAzukaribtln8rittoruhns(acptdnpyEntity.getAzukaribtln8rittoruhns());
        } // 預かりボトル８Ｌ本数
        if (acptdnpyEntity.getKybtln8rittoruhns() == null) {
            entity.setKybtln8rittoruhns((short) 0);
        } else {
            entity.setKybtln8rittoruhns(acptdnpyEntity.getKybtln8rittoruhns());
        } // 回収予定ボトル８Ｌ本数
        if (acptdnpyEntity.getKzbtln8rittoruhns() == null) {
            entity.setKzbtln8rittoruhns((short) 0);
        } else {
            entity.setKzbtln8rittoruhns(acptdnpyEntity.getKzbtln8rittoruhns());
        } // 回収実績ボトル８Ｌ本数
        entity.setPrntmemon1(acptdnpyEntity.getPrntmemon1()); // 印刷メモ１
        entity.setPrntmemon2(acptdnpyEntity.getPrntmemon2()); // 印刷メモ２
        entity.setPrntmemon3(acptdnpyEntity.getPrntmemon3()); // 印刷メモ３
        if (CommonConstants.DNPYSTS_11.equals(acptdnpyEntity.getDnpysts())) {
            entity.setAcptshimeprcsdd(CommonConstants.DEF_DATE_00000000); // 受注締担当者ＡＰ基本コード
        } else {
            entity.setAcptshimeprcsdd(null);
        }
        entity.setAcptshimetnshaapkihoncd(""); // 受注締担当者ＡＰ基本コード
        entity.setAcptshimetnshashopcd(""); // 受注締担当者ショップコード
        entity.setAcptshimeprcstnshacd(""); // 受注締処理担当者コード
        entity.setDlvryjissekiprcsdd(CommonConstants.DEF_DATE_00000000); // 配達実績処理日
        entity.setDlvryjissekimonoapkihoncd(""); // 配達実績者ＡＰ基本コード
        entity.setDlvryjissekimonoshopcd(""); // 配達実績者ショップコード
        entity.setDlvryjissekiprcstnshacd(""); // 配達実績処理担当者コード
        entity.setUrishimeprcsdd(CommonConstants.DEF_DATE_00000000); // 売上締処理日
        entity.setGetsujishimedydm(null); // 月次締年月
        entity.setGetsujishimedd(null); // 月次締日
        entity.setGetsujiprcsjissidd(CommonConstants.DEF_DATE_00000000); // 月次処理実施日
        entity.setDlvryjun(acptdnpyEntity.getDlvryjun()); // 配達順
        entity.setNoshoprnttrgtflg(CommonConstants.FLG_ON); // 納品書印刷対象フラグ
        entity.setNoshoprntsumiflg(CommonConstants.FLG_OFF); // 納品書印刷済フラグ
        if (CommonConstants.DNPYSTS_11.equals(acptdnpyEntity.getDnpysts())) {
            entity.setNoshoprntdd(null);
        } else {
            entity.setNoshoprntdd("");
        } // 納品書印刷日
        entity.setNoshohikaeprnttrgtflg(CommonConstants.FLG_ON); // 納品書控印刷対象フラグ
        entity.setNoshohikaeprntsumiflg(CommonConstants.FLG_OFF); // 納品書控印刷済フラグ
        if (CommonConstants.DNPYSTS_11.equals(acptdnpyEntity.getDnpysts())) {
            entity.setNoshohikaeprntdd(null);
        } else {
            entity.setNoshohikaeprntdd("");
        } // 納品書控印刷日
        if (CommonConstants.KAISHUHOHOKBN_7.equals(acptdnpyEntity.getKaishuhohokbn())) {
            entity.setNyknhyoprnttrgtflg(CommonConstants.FLG_ON);
        } else {
            entity.setNyknhyoprnttrgtflg(CommonConstants.FLG_OFF);
        } // 入金票印刷対象フラグ
        entity.setNyknhyoprntsumiflg(CommonConstants.FLG_OFF); // 入金票印刷済フラグ
        if (CommonConstants.DNPYSTS_11.equals(acptdnpyEntity.getDnpysts())) {
            entity.setNyknhyoprntdd(null);
        } else {
            entity.setNyknhyoprntdd("");
        } // 入金票印刷日
        if (CommonConstants.KBN_ACPT_SHOHIN.equals(acptdnpyEntity.getAcptkbn())) {
            entity.setWsvsijishoprnttrgtflg(CommonConstants.FLG_OFF);
        } else {
            entity.setWsvsijishoprnttrgtflg(CommonConstants.FLG_ON);
        } // サーバー指示書印刷対象フラグ
        entity.setWsvsijishoprntsumiflg(CommonConstants.FLG_OFF); // サーバー指示書印刷済フラグ
        if (CommonConstants.DNPYSTS_11.equals(acptdnpyEntity.getDnpysts())) {
            entity.setWsvsijishoprntdd(null);
        } else {
            entity.setWsvsijishoprntdd("");
        } // サーバー指示書印刷日
        entity.setSingldnpyflg("0"); // 単体伝票締めフラグ
        if (CommonConstants.DNPYSTS_11.equals(acptdnpyEntity.getDnpysts())) {
            entity.setDnpysts(CommonConstants.DNPYSTS_02);
        } else {
            entity.setDnpysts(acptdnpyEntity.getDnpysts());
        } // 伝票ステータス
        if (CommonConstants.DNPYSTS_11.equals(acptdnpyEntity.getDnpysts())) {
            entity.setAcptsts(CommonConstants.DNPYSTS_02);
        } else {
            entity.setAcptsts(acptdnpyEntity.getDnpysts());
        } // 受注ステータス
        entity.setDlvrysts(CommonConstants.KBN_DLVRYSTS_NONE); // 配達ステータス
        entity.setDatsksmotokbn(acptdnpyEntity.getDatsksmotokbn()); // データ作成元区分
        if (CommonConstants.KBN_SAKUSEIMOTO_JUCHUTORIKOMI
            .equals(acptdnpyEntity.getDatsksmotokbn())) {
            entity.setSksmotokanrikbn(acptdnpyEntity.getSksmotokanrikbn());
        } else {
            entity.setSksmotokanrikbn("");
        } // 作成元管理区分
        if (CommonConstants.KBN_SAKUSEIMOTO_JUCHUTORIKOMI
            .equals(acptdnpyEntity.getDatsksmotokbn())) {
            entity.setSksmotokanrino(acptdnpyEntity.getSksmotokanrino());
        } else {
            entity.setSksmotokanrino("");
        } // 作成元管理番号
        entity.setAcpthikiatesakiapkihoncd(acptdnpyEntity.getDlvrykankatsuapkihoncd()); // 受注引当先ＡＰ基本コード
        entity.setAcpthikiatesakishopcd(acptdnpyEntity.getDlvrykankatsushopcd()); // 受注引当先ショップコード
        entity.setAcpthikiatesakikyotencd(acptdnpyEntity.getDlvrykyotencd()); // 受注引当先拠点コード
        entity.setDlvryytirgrayusyohns((short) acptdnpyEntity.getRycnt()); // 配達予定レギュラー有償本数
        entity.setDlvryytirgramushohns((short) acptdnpyEntity.getRmcnt()); // 配達予定レギュラー無償本数
        entity.setDlvryytisrmyusyohns((short) acptdnpyEntity.getSycnt()); // 配達予定スリム有償本数
        entity.setDlvryytisrmmushohns((short) acptdnpyEntity.getSmcnt()); // 配達予定スリム無償本数
        entity.setDlvryytisonotayusyohns((short) acptdnpyEntity.getOycnt()); // 配達予定その他有償本数
        entity.setDlvryytisonotamushohns((short) acptdnpyEntity.getOmcnt()); // 配達予定その他無償本数
        if (CommonConstants.KBN_TOKUISAKI_KEIHI.equals(acptdnpyEntity.getTokuisakikbn())) {
            entity.setDlvryytiseihinmizuurikin((long) 0);
        } else {
            entity.setDlvryytiseihinmizuurikin(acptdnpyEntity.getWmn());
        } // 配達予定製品水売上金額
        if (CommonConstants.KBN_TOKUISAKI_KEIHI.equals(acptdnpyEntity.getTokuisakikbn())) {
            entity.setDlvryytisonotaurikin((long) 0);
        } else {
            entity.setDlvryytisonotaurikin(acptdnpyEntity.getOmn());
        } // 配達予定その他売上金額
        entity.setDlvryjissekirgrayusyohns((short) 0); // 配達実績レギュラー有償本数
        entity.setDlvryjissekirgramushohns((short) 0); // 配達実績レギュラー無償本数
        entity.setDlvryjissekisrmyusyohns((short) 0); // 配達実績スリム有償本数
        entity.setDlvryjissekisrmmushohns((short) 0); // 配達実績スリム無償本数
        entity.setDlvryjissekistyshns((short) 0); // 配達実績その他有償本数
        entity.setDlvryjissekistmshns((short) 0); // 配達実績その他無償本数
        entity.setDlvryjissekishsuakin(0); // 配達実績製品水売上金額
        entity.setDlvryjissekisonotaurikin(0); // 配達実績その他売上金額
        entity.setAcptkin(acptdnpyEntity.getAcptkin()); // 受注金額
        entity.setAcptshohitaxkin(acptdnpyEntity.getAcptshohitaxkin()); // 受注消費税金額
        entity.setAcptippannukikin(acptdnpyEntity.getAcptippannukikin()); // 受注一般税抜金額
        entity.setAcptippantaxkin(acptdnpyEntity.getAcptippantaxkin()); // 受注一般消費税金額
        entity.setAcptkeigennukikin(acptdnpyEntity.getAcptkeigennukikin()); // 受注軽減税抜金額
        entity.setAcptkeigentaxkin(acptdnpyEntity.getAcptkeigentaxkin()); // 受注軽減消費税金額
        entity.setAcpthikazeiuriikin(acptdnpyEntity.getAcpthikazeiuriikin()); // 受注非課税金額
        entity.setAcptfukazeiuriikin(acptdnpyEntity.getAcptfukazeiuriikin()); // 受注不課税売上金額
        entity.setNohinkin((long) 0); // 納品金額
        entity.setNohinshohitaxkin((long) 0); // 納品消費税金額
        entity.setNohinippannukikin((long) 0); // 納品一般税抜金額
        entity.setNohinippantaxkin((long) 0); // 納品一般消費税金額
        entity.setNohinkeigennukikin((long) 0); // 納品軽減税抜金額
        entity.setNohinkeigentaxkin((long) 0); // 納品軽減消費税金額
        entity.setNohinhikazeiuriikin((long) 0); // 納品非課税金額
        entity.setNohinfukazeiuriikin((long) 0); // 納品不課税売上金額
        entity.setKurerurittorustup((short) 0); // クレールＬ設置
        entity.setKurerurittoruhikiage((short) 0); // クレールＬ引上
        entity.setKureruesustup((short) 0); // クレールＳ設置
        entity.setKureruesuhikiage((short) 0); // クレールＳ引上
        entity.setSonotastup((short) 0); // その他設置
        entity.setSonotahikiage((short) 0); // その他引上
        if (CommonConstants.KBN_TOKUISAKI_SHOP.equals(acptdnpyEntity.getTokuisakikbn())
            || CommonConstants.KBN_TOKUISAKI_AP.equals(acptdnpyEntity.getTokuisakikbn())
            || CommonConstants.KBN_TOKUISAKI_ACI.equals(acptdnpyEntity.getTokuisakikbn())
            || CommonConstants.KBN_TOKUISAKI_NAJUTAKU.equals(acptdnpyEntity.getTokuisakikbn())
            || CommonConstants.KBN_TOKUISAKI_DLVRYJUTAKU.equals(acptdnpyEntity.getTokuisakikbn())) {
            entity.setTaxkeisankbn(CommonConstants.KBN_TAXKEISAN_EX);
        } else {
            entity.setTaxkeisankbn(CommonConstants.KBN_TAXKEISAN_IN);
        } // 税計算区分
        entity.setZnkdlvrydd(""); // 前回配達日
        entity.setZnkdlvryhns((short) 0); // 前回配達本数
        entity.setJikaidlvryytidd(""); // 次回配達予定日
        entity.setZnkshimedd(""); // 前回締日
        entity.setZnkkurikoshikin(null); // 前回繰越金額
        entity.setAcptthdaibikiflg(CommonConstants.FLG_OFF); // 受注時代引フラグ
        entity.setAcptrcptkeirokbn(acptdnpyEntity.getAcptrcptkeirokbn()); // 受注受付経路区分
        entity.setYukokbn(CommonConstants.YUKOKBN_1); // 有効区分
        entity.setToshaapkihoncd(regInfo.getLoginUserApkihoncd()); // 登録者ＡＰ基本コード
        entity.setToshashopcd(regInfo.getLoginUserShopcd()); // 登録者ショップコード
        entity.setToshacd(regInfo.getLoginUserTnshacd()); // 登録者コード
        entity.setInptsystmymd(regInfo.getSystemDate()); // 登録システム日付
        entity.setInptsystmjikan(regInfo.getSystemTime()); // 登録システム時間
        entity.setInptunyoymd(unyodate); // 登録運用日付
        entity.setInptipadrse(regInfo.getIpAddress()); // 登録ＩＰアドレス
        entity.setInptpgmid(regInfo.getProgramId()); // 登録プログラムＩＤ
        entity.setKoshaapkihoncd(regInfo.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        entity.setKoshashopcd(regInfo.getLoginUserShopcd()); // 更新者ショップコード
        entity.setKoshacd(regInfo.getLoginUserTnshacd()); // 更新者コード
        entity.setKshnsystmymd(regInfo.getSystemDate()); // 更新システム日付
        entity.setKshnsystmjikan(regInfo.getSystemTime()); // 更新システム時間
        entity.setKshnunyoymd(unyodate); // 更新運用日付
        entity.setKshnipadrse(regInfo.getIpAddress()); // 更新ＩＰアドレス
        entity.setKshnpgmid(regInfo.getProgramId()); // 更新プログラムＩＤ

        return entity;

    }

    /**
     * 受注明細を新規登録します．
     * 
     * @param acptdnpyEntity 受注データ
     * @param unyodate 運用日付
     * @param regInfo 登録情報
     */
    public void insertJuchuMeisai(S04T001TC0800C01Entity acptdnpyEntity, String unyodate,
        RegistrationInfoDto regInfo) {

        try {
            // DELETE実行
            s04t002tDao.deleteByExample(createDeleteConditionS04t002tEntity(acptdnpyEntity));
            int acptdnpymeisaino = 0;
            for (S04T002TC0800C01Entity detailEntity : acptdnpyEntity.getS04T002tEntityList()) {
                // INSERT実行
                if (acptdnpymeisaino == 0
                    || CommonConstants.FLG_ON.equals(detailEntity.getNyuryokumeisaiflg())) {
                    acptdnpymeisaino++;
                }
                detailEntity.setAcptdnpymeisaino(acptdnpymeisaino);
                s04t002tDao.insertSelective(
                    createInsertS04t002tEntity(acptdnpyEntity, detailEntity, unyodate, regInfo));
            }
        } catch (DuplicateKeyException e) {
            // 一意制約違反の場合、エラー
            throw BusinessExceptionFactory.validationFailed(MessageID.W089);
        } catch (RuntimeException re) {
            throw BusinessExceptionFactory.validationFailed(MessageID.E002);
        }
    }

    /**
     * 受注配達明細テーブル削除時の条件値を設定する。
     * 
     * @param acptdnpyEntity 受注データ
     * @return S04T002TEntityExample
     */
    private S04T002TEntityExample createDeleteConditionS04t002tEntity(
        S04T001TC0800C01Entity acptdnpyEntity) {

        S04T002TEntityExample entity = new S04T002TEntityExample();

        // クライテリア(SQL条件)の生成
        entity.createCriteria().andApkihoncdEqualTo(acptdnpyEntity.getApkihoncd())
            .andShopcdEqualTo(acptdnpyEntity.getShopcd())
            .andAcptdnpynoEqualTo(acptdnpyEntity.getAcptdnpyno())
            .andEdanoEqualTo(acptdnpyEntity.getEdano());

        return entity;
    }

    /**
     * 受注配達明細テーブル登録時の値を設定する。
     * 
     * @param acptdnpyEntity 受注データ
     * @param detailEntity 受注明細データ
     * @param unyodate 運用日付
     * @param regInfo 登録情報
     * @return S04T002TEntity
     */
    private S04T002TEntity createInsertS04t002tEntity(S04T001TC0800C01Entity acptdnpyEntity,
        S04T002TC0800C01Entity detailEntity, String unyodate, RegistrationInfoDto regInfo) {

        S04T002TEntity entity = new S04T002TEntity();

        entity.setApkihoncd(acptdnpyEntity.getApkihoncd()); // AP基本コード
        entity.setShopcd(acptdnpyEntity.getShopcd()); // ショップコード
        entity.setAcptdnpyno(acptdnpyEntity.getAcptdnpyno()); // 受注伝票番号
        entity.setEdano(acptdnpyEntity.getEdano()); // 枝番号
        entity.setAcptdnpymeisaino(detailEntity.getAcptdnpymeisaino()); // 受注伝票明細番号
        entity.setAcptdnpymeisaiedano(detailEntity.getAcptdnpymeisaiedano()); // 受注伝票明細枝番号
        entity.setItakuaiteapkihoncd(detailEntity.getItakuaiteapkihoncd()); // 委託相手ＡＰ基本コード
        entity.setItakuaiteshopcd(detailEntity.getItakuaiteshopcd()); // 委託相手ショップコード
        entity.setItakuaiteacptdnpyno(detailEntity.getItakuaiteacptdnpyno()); // 委託相手受注伝票番号
        entity.setItakuaiteedano(detailEntity.getItakuaiteedano()); // 委託相手枝番号
        entity.setItakuaiteacptdnpymeisaino(detailEntity.getItakuaiteacptdnpymeisaino()); // 委託相手受注伝票明細番号
        entity.setItakuaiteacptdnpymeisaiedano(detailEntity.getItakuaiteacptdnpymeisaiedano()); // 委託相手受注伝票明細枝番号
        if (!CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(detailEntity.getMeisaikbn())) {
            entity.setLctgrycd(detailEntity.getLctgrycd()); // 大分類コード
            entity.setMctgrycd(detailEntity.getMctgrycd()); // 中分類コード
            entity.setSctgrycd(detailEntity.getSctgrycd()); // 小分類コード
            entity.setShncd(detailEntity.getShncd()); // 商品コード
            entity.setHaifubutsuapkihoncd(detailEntity.getHaifubutsuapkihoncd()); // ＡＰ基本コード
            entity.setHaifubutsushopcd(detailEntity.getHaifubutsushopcd()); // 配布物ショップコード
            entity.setHaifubutsucd(detailEntity.getHaifubutsucd()); // 配布物コード
            entity.setKatasiki(detailEntity.getKatasiki()); // 型式
            entity.setKeijokbn(detailEntity.getKeijokbn()); // 計上区分
        } else {
            entity.setLctgrycd(null); // 大分類コード
            entity.setMctgrycd(null); // 中分類コード
            entity.setSctgrycd(null); // 小分類コード
            entity.setShncd(null); // 商品コード
            entity.setHaifubutsuapkihoncd(null); // ＡＰ基本コード
            entity.setHaifubutsushopcd(null); // 配布物ショップコード
            entity.setHaifubutsucd(null); // 配布物コード
            entity.setKatasiki(null); // 型式
            entity.setKeijokbn(null); // 計上区分
        }
        entity.setAcptsuryo(detailEntity.getAcptsuryo()); // 受注数量
        if (!CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(detailEntity.getMeisaikbn())) {
            entity.setHyojununt(detailEntity.getHyojununt()); // 標準単価
            entity.setAcptunt(detailEntity.getAcptunt()); // 受注単価
            entity.setAcptkin(detailEntity.getAcptkin()); // 受注金額
            entity.setAcptuntshohitaxkin(detailEntity.getAcptuntshohitaxkin()); // 受注単価消費税金額
            entity.setAcptshohitaxkin(detailEntity.getAcptshohitaxkin()); // 受注消費税金額
        } else {
            entity.setHyojununt(null); // 標準単価
            entity.setAcptunt(null); // 受注単価
            entity.setAcptkin(null); // 受注金額
            entity.setAcptuntshohitaxkin(null); // 受注単価消費税金額
            entity.setAcptshohitaxkin(null); // 受注消費税金額
        }
        entity.setItakucmpnyacptunt(detailEntity.getItakucmpnyacptunt()); // 委託会社受注単価
        entity.setItakucmpnyacptuntshohitaxkin(detailEntity.getItakucmpnyacptuntshohitaxkin()); // 委託会社受注単価消費税金額
        entity.setItakucmpnyacptshohitaxkin(detailEntity.getItakucmpnyacptshohitaxkin()); // 委託会社受注消費税金額
        if (CommonConstants.KBN_MEISAI_2.equals(detailEntity.getMeisaikbn())) {
            entity.setNohinsuryo(detailEntity.getNohinsuryo());
        } else if (detailEntity.getNohinsuryo() != null) {
            entity.setNohinsuryo(detailEntity.getNohinsuryo());
        } else {
            entity.setNohinsuryo(BigDecimal.ZERO);
        } // 納品数量
        if (CommonConstants.KBN_MEISAI_2.equals(detailEntity.getMeisaikbn())) {
            entity.setNohinunt(detailEntity.getNohinunt());
        } else if ((!CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(detailEntity.getMeisaikbn()))
            && (detailEntity.getNohinunt() != null)) {
            entity.setNohinunt(detailEntity.getNohinunt());
        } else {
            entity.setNohinunt(BigDecimal.ZERO);
        } // 納品単価
        if (CommonConstants.KBN_MEISAI_2.equals(detailEntity.getMeisaikbn())) {
            entity.setNohinkin(detailEntity.getNohinkin());
        } else if ((!CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(detailEntity.getMeisaikbn()))
            && (detailEntity.getNohinkin() != null)) {
            entity.setNohinkin(detailEntity.getNohinkin());
        } else {
            entity.setNohinkin((long) 0);
        } // 納品金額
        if (CommonConstants.KBN_MEISAI_2.equals(detailEntity.getMeisaikbn())) {
            entity.setNohinuntshohitaxkin(detailEntity.getNohinuntshohitaxkin());
        } else if ((!CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(detailEntity.getMeisaikbn()))
            && (detailEntity.getNohinuntshohitaxkin() != null)) {
            entity.setNohinuntshohitaxkin(detailEntity.getNohinuntshohitaxkin());
        } else {
            entity.setNohinuntshohitaxkin((long) 0);
        } // 納品単価消費税金額
        if (CommonConstants.KBN_MEISAI_2.equals(detailEntity.getMeisaikbn())) {
            entity.setNohinshohitaxkin(detailEntity.getNohinshohitaxkin());
        } else if ((!CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(detailEntity.getMeisaikbn()))
            && (detailEntity.getNohinshohitaxkin() != null)) {
            entity.setNohinshohitaxkin(detailEntity.getNohinshohitaxkin());
        } else {
            entity.setNohinshohitaxkin((long) 0);
        } // 納品消費税金額
        if (CommonConstants.KBN_SAKUSEIMOTO_JUCHUYOYAKU.equals(acptdnpyEntity.getDatsksmotokbn())) {
            entity.setSoukaisu(detailEntity.getSoukaisu()); // 総回数
            entity.setZankaisu(detailEntity.getZankaisu()); // 残回数
        } else {
            entity.setSoukaisu(null); // 総回数
            entity.setZankaisu(null); // 残回数
        }
        if (!CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(detailEntity.getMeisaikbn())) {
            entity.setTaxkbn(detailEntity.getTaxkbn());
        } else {
            entity.setTaxkbn(null);
        } // 税区分
        if (CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(detailEntity.getMeisaikbn())) {
            entity.setYusyokbn(CommonConstants.FLG_OFF);
        } else if (CommonConstants.KBN_MEISAI_3.equals(detailEntity.getMeisaikbn())) {
            entity.setYusyokbn(detailEntity.getYusyokbn());
        } else {
            if (CommonConstants.KBN_KEIJO_KEIHI.equals(detailEntity.getKeijokbn())) {
                entity.setYusyokbn(CommonConstants.KBN_YUSHO_KEIHI);
            } else if (CommonConstants.KBN_KEIJO_SUBRENTAL.equals(detailEntity.getKeijokbn())
                || CommonConstants.KBN_KEIJO_RENTAL.equals(detailEntity.getKeijokbn())) {
                entity.setYusyokbn(CommonConstants.KBN_YUSHO_RENTAL);
            } else if (CommonConstants.KBN_KEIJO_URIAGE.equals(detailEntity.getKeijokbn())
                || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(detailEntity.getKeijokbn())) {
                entity.setYusyokbn(CommonConstants.KBN_YUSHO_YUSHO);
            } else {
                entity.setYusyokbn(CommonConstants.KBN_YUSHO_MUSHO);
            }
        } // 有償区分
        if (CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(detailEntity.getMeisaikbn())) {
            entity.setMeisaikbn(CommonConstants.KBN_MEISAI_HAIHUBUTSU);
        } else if (CommonConstants.KBN_MEISAI_2.equals(detailEntity.getMeisaikbn())) {
            entity.setMeisaikbn(CommonConstants.KBN_MEISAI_2);
        } else if (CommonConstants.KBN_MEISAI_3.equals(detailEntity.getMeisaikbn())) {
            entity.setMeisaikbn(CommonConstants.KBN_MEISAI_3);
        } else {
            entity.setMeisaikbn(CommonConstants.KBN_MEISAI_SHOHIN);
        } // 明細区分
        if (CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(detailEntity.getMeisaikbn())) {
            entity.setNyuryokumeisaiflg(CommonConstants.FLG_ON); // 入力明細フラグ
            entity.setTanpinmeisaiflg(CommonConstants.FLG_ON); // 単品明細フラグ
        } else {
            entity.setNyuryokumeisaiflg(detailEntity.getNyuryokumeisaiflg()); // 入力明細フラグ
            entity.setTanpinmeisaiflg(detailEntity.getTanpinmeisaiflg()); // 単品明細フラグ
        }
        if (!CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(detailEntity.getMeisaikbn())) {
            entity.setStshncd(detailEntity.getStshncd()); // セット商品コード
            entity.setMeisaibiko(detailEntity.getMeisaibiko()); // 明細備考
        } else {
            entity.setStshncd(null); // セット商品コード
            entity.setMeisaibiko(null); // 明細備考
        }
        entity.setDatsksmotokbn(detailEntity.getDatsksmotokbn()); // データ作成元区分

        if (CommonConstants.KBN_MEISAI_2.equals(detailEntity.getMeisaikbn())) {
            entity.setUridnpyno(detailEntity.getUridnpyno());
        } else if (!CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(detailEntity.getMeisaikbn())
            && detailEntity.getNohinsuryo() != null
            && detailEntity.getNohinsuryo().compareTo(BigDecimal.ZERO) > 0) {
            entity.setUridnpyno(detailEntity.getUridnpyno()); // 売上伝票番号
            entity.setUridnpyedano(detailEntity.getUridnpyedano()); // 売上伝票枝番号
            entity.setUridnpymeisaino(detailEntity.getUridnpymeisaino()); // 売上伝票明細番号
            entity.setUridnpymeisaiedano(detailEntity.getUridnpymeisaiedano()); // 売上伝票明細枝番号
        }
        entity.setYukokbn(CommonConstants.YUKOKBN_1); // 有効区分
        entity.setToshaapkihoncd(regInfo.getLoginUserApkihoncd()); // 登録者ＡＰ基本コード
        entity.setToshashopcd(regInfo.getLoginUserShopcd()); // 登録者ショップコード
        entity.setToshacd(regInfo.getLoginUserTnshacd()); // 登録者コード
        entity.setInptsystmymd(regInfo.getSystemDate()); // 登録システム日付
        entity.setInptsystmjikan(regInfo.getSystemTime()); // 登録システム時間
        entity.setInptunyoymd(unyodate); // 登録運用日付
        entity.setInptipadrse(regInfo.getIpAddress()); // 登録ＩＰアドレス
        entity.setInptpgmid(regInfo.getProgramId()); // 登録プログラムＩＤ
        entity.setKoshaapkihoncd(regInfo.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        entity.setKoshashopcd(regInfo.getLoginUserShopcd()); // 更新者ショップコード
        entity.setKoshacd(regInfo.getLoginUserTnshacd()); // 更新者コード
        entity.setKshnsystmymd(regInfo.getSystemDate()); // 更新システム日付
        entity.setKshnsystmjikan(regInfo.getSystemTime()); // 更新システム時間
        entity.setKshnunyoymd(unyodate); // 更新運用日付
        entity.setKshnipadrse(regInfo.getIpAddress()); // 更新ＩＰアドレス
        entity.setKshnpgmid(regInfo.getProgramId()); // 更新プログラムＩＤ

        return entity;

    }

}
