package jp.co.aquaclara.accis.common.domain.repository.dao.business;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.C0804B01Dao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M013TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M043TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02V001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T002TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04W001TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.C0804B01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TTokuisakiDlvryInfoEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M043TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M043TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M050TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04001TTeikiZaikoEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04001TTeikiZaikoEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TDenpyoTokuisakiEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TJuchuDenpyoEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TJuchuMeisaiEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04W001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04W001TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04w001tShinkiWorkEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.business.S04W001TEntityEx;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.FileShinkiJuchuVO;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.JuchuDenpyoVO;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.JuchuMeisaiVO;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.JuchuRelationVo;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.JuchuTeikiVo;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.ZaikoKeyVo;
import jp.co.aquaclara.accis.common.exception.BusinessException;
import jp.co.aquaclara.accis.common.exception.RecordUpdatedException;



/**
 * データアクセスクラスです。 QuiQpro共通クラスのBaseDAOを継承します。
 * 
 * @author Isac)Katayama
 * @version 1.0.0 2007-05-06
 */
@Repository
public class JuchuDaoRepository {

    @Autowired
    private S02M013TDao s02m013tDao;

    @Autowired
    private S02M043TDao s02m043tDao;

    @Autowired
    private S04T001TDao s04t001tDao;

    @Autowired
    private S04T002TDao s04t002tDao;

    @Autowired
    private S04W001TDao s04w001tDao;

    @Autowired
    private S02V001TDao s02v001tDao;

    @Autowired
    private C0804B01Dao c0804b01Dao;

    /**
     * コンストラクタ
     * 
     * @param con コネクション
     */
    public JuchuDaoRepository() {
        super();
    }

    /**
     * 受注伝票データを取得します． ①受注伝票番号・枝番号指定で取得する場合は、引数のvoに受注伝票番号・枝番号をセットして下さい
     * ②予約データを取得する場合は、引数のyoyakuFgをtrueにして下さい （削除）配達エリアの情報が必要な場合は引数のvoに配達予定日をセットしてください
     * （削除）③前回伝票（配達実績有）を取得する場合は、引数のvoに得意先コード・伝票ステータス（＝配達売上済）をセットして下さい
     * （削除）④前回伝票（最終受注日分）を取得する場合は、引数のvoに得意先コードをセットして下さい
     * 
     * @param vo 受注配達VO
     * @param yoyakuFg 予約フラグ
     * @return S01JuchuDenpyoVO 結果
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public JuchuDenpyoVO getJuchuDenpyo(JuchuDenpyoVO vo, boolean yoyakuFg) {
        JuchuDenpyoVO result = null;
        // パラメータをセット
        S04T001TEntity s04t001tEntity = new S04T001TEntity();
        s04t001tEntity.setApkihoncd(vo.getApkihoncd());
        s04t001tEntity.setShopcd(vo.getShopcd());
        s04t001tEntity.setAcptdnpyno(vo.getAcptdnpyno());
        s04t001tEntity.setEdano(vo.getEdano());
        s04t001tEntity.setDlvryytidd(vo.getDlvryytidd());
        s04t001tEntity.setTokuisakicd(vo.getTokuisakicd());

        // 受注伝票データの取得
        S04T001TJuchuDenpyoEntity resultEntity =
            this.s04t001tDao.getJuchuDenpyo(yoyakuFg, s04t001tEntity);

        if (resultEntity != null) {
            result = new JuchuDenpyoVO();
            result.setApkihoncd(resultEntity.getApkihoncd());
            result.setShopcd(resultEntity.getShopcd());
            result.setAcptdnpyno(resultEntity.getAcptdnpyno());
            result.setEdano(resultEntity.getEdano());
            result.setDnpykbn(resultEntity.getDnpykbn());
            result.setAcptkbn(resultEntity.getAcptkbn());
            result.setAcptkbnshs(resultEntity.getAcptkbnshs());
            result.setTokuisakicd(resultEntity.getTokuisakicd());
            result.setAcptdd(resultEntity.getAcptdd());
            result.setAcpttnshaapkihoncd(resultEntity.getAcpttnshaapkihoncd());
            result.setAcpttnshashopcd(resultEntity.getAcpttnshashopcd());
            result.setAcpttnshacd(resultEntity.getAcpttnshacd());
            result.setDlvryytidd(resultEntity.getDlvryytidd());
            result.setDlvryjissekidd(resultEntity.getDlvryjissekidd());
            result.setDlvrykibojikan(resultEntity.getDlvrykibojikan());
            result.setDlvrykibojikanshry(resultEntity.getDlvrykibojikanshry());
            result.setDlvrykibojikankbn(resultEntity.getDlvrykibojikankbn());
            result.setFzaiokikanoflg(resultEntity.getFzaiokikanoflg());
            result.setFzaiokijissekiflg(resultEntity.getFzaiokijissekiflg());
            result.setDlvrytnshacd(resultEntity.getDlvrytnshacd());
            result.setDlvrykankatsuapkihoncd(resultEntity.getDlvrykankatsuapkihoncd());
            result.setDlvrykankatsushopcd(resultEntity.getDlvrykankatsushopcd());
            result.setDlvrykyotencd(resultEntity.getDlvrykyotencd());
            result.setDlvryareacd(resultEntity.getDlvryareacd());
            result.setDlvrykbn(resultEntity.getDlvrykbn());
            result.setDlvrycmpnycd(resultEntity.getDlvrycmpnycd());
            result.setAzukaribtln1n2rittoruhns(resultEntity.getAzukaribtln1n2rittoruhns());
            result.setKybtln1n2rittoruhns(resultEntity.getKybtln1n2rittoruhns());
            result.setKzbtln1n2rittoruhns(resultEntity.getKzbtln1n2rittoruhns());
            result.setAzukaribtln8rittoruhns(resultEntity.getAzukaribtln8rittoruhns());
            result.setKybtln8rittoruhns(resultEntity.getKybtln8rittoruhns());
            result.setKzbtln8rittoruhns(resultEntity.getKzbtln8rittoruhns());
            result.setPrntmemon1(resultEntity.getPrntmemon1());
            result.setPrntmemon2(resultEntity.getPrntmemon2());
            result.setPrntmemon3(resultEntity.getPrntmemon3());
            result.setDlvryjissekiprcsdd(resultEntity.getDlvryjissekiprcsdd());
            result.setDnpysts(resultEntity.getDnpysts());
            result.setAcptsts(resultEntity.getAcptsts());
            result.setDatsksmotokbn(resultEntity.getDatsksmotokbn());
            result.setDlvryjissekirgrayusyohns(resultEntity.getDlvryjissekirgrayusyohns());
            result.setDlvryjissekirgramushohns(resultEntity.getDlvryjissekirgramushohns());
            result.setDlvryjissekisrmyusyohns(resultEntity.getDlvryjissekisrmyusyohns());
            result.setDlvryjissekisrmmushohns(resultEntity.getDlvryjissekisrmmushohns());
            result.setDlvryjissekistyshns(resultEntity.getDlvryjissekistyshns());
            result.setDlvryjissekistmshns(resultEntity.getDlvryjissekistmshns());
            result.setDlvryjissekishsuakin(resultEntity.getDlvryjissekishsuakin());
            result.setDlvryjissekisonotaurikin(resultEntity.getDlvryjissekisonotaurikin());
            result.setAcptkin(resultEntity.getAcptkin());
            result.setAcptshohitaxkin(resultEntity.getAcptshohitaxkin());
            result.setNohinkin(resultEntity.getNohinkin());
            result.setNohinshohitaxkin(resultEntity.getNohinshohitaxkin());
            result.setYukokbn(resultEntity.getYukokbn());
            result.setKshnsystmymd(resultEntity.getKshnsystmymd());
            result.setKshnsystmjikan(resultEntity.getKshnsystmjikan());
            result.setAcptrcptkeirokbn(resultEntity.getAcptrcptkeirokbn());
            result.setKaishuhohokbn(resultEntity.getKaishuhohokbn());
            result.setAcptthdaibikiflg(resultEntity.getAcptthdaibikiflg());
            result.setItakuaiteapkihoncd(resultEntity.getItakuaiteapkihoncd());
            result.setItakuaiteshopcd(resultEntity.getItakuaiteshopcd());
            result.setItakuaiteacptdnpyno(resultEntity.getItakuaiteacptdnpyno());
            result.setItakuaiteedano(resultEntity.getItakuaiteedano());
            result.setUridnpyno(resultEntity.getUridnpyno());
            result.setSkysakicd(resultEntity.getSkysakicd());
            result.setJotaists(resultEntity.getJotaists());
            result.setAcptteishiflg(resultEntity.getAcptteishiflg());
            result.setTokuisakikbn(resultEntity.getTokuisakikbn());
            result.setDlvrykyotennm(resultEntity.getDlvrykyotennm());
            result.setDlvryareanm(resultEntity.getDlvryareanm());
            result.setDlvryptnnm(resultEntity.getDlvryptnnm());
            result.setDlvrycmpnynm(resultEntity.getDlvrycmpnynm());
            result.setAcpttnshamei(resultEntity.getAcpttnshamei());
            result.setDlvrytnshamei(resultEntity.getDlvrytnshamei());
            result.setDnpystsmei(resultEntity.getDnpystsmei());
            result.setDatsksmotokbnnm(resultEntity.getDatsksmotokbnnm());
            result.setAcptkbnnm(resultEntity.getAcptkbnnm());
            result.setAcptkbnshsnm(resultEntity.getAcptkbnshsnm());
            result.setNohinsakijohon1(resultEntity.getNohinsakijohon1());
            result.setNohinsakijohon2(resultEntity.getNohinsakijohon2());
            result.setDlvrykibojikankbnnm(resultEntity.getDlvrykibojikankbnnm());
        }

        return result;
    }

    /**
     * 受注伝票明細リストを取得します．
     * 
     * @param vo 受注配達VO
     * @param kakakuKijunbi 価格（単価）取得基準日
     * @return resultList 結果のリスト
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    // ■:1 Isac)Shiba 2021-01-20 価格改定対応
    public List<JuchuMeisaiVO> getJuchuMeisai(JuchuDenpyoVO vo, String kakakuKijunbi) {
        List<JuchuMeisaiVO> resultList = new ArrayList<JuchuMeisaiVO>();
        // パラメータをセット
        S04T002TEntityKey key = new S04T002TEntityKey();
        key.setApkihoncd(vo.getApkihoncd());
        key.setShopcd(vo.getShopcd());
        key.setAcptdnpyno(vo.getAcptdnpyno());
        key.setEdano(vo.getEdano());

        // 受注伝票明細リストを取得
        List<S04T002TJuchuMeisaiEntity> result =
            this.s04t002tDao.getJuchuMeisai(key, vo.getAcptdd(), kakakuKijunbi);
        // 明細リストの生成
        for (S04T002TJuchuMeisaiEntity rs : result) {
            JuchuMeisaiVO jmVo = new JuchuMeisaiVO();
            jmVo.setApkihoncd(rs.getApkihoncd());
            jmVo.setShopcd(rs.getShopcd());
            jmVo.setAcptdnpyno(rs.getAcptdnpyno());
            jmVo.setEdano(rs.getEdano());
            jmVo.setAcptdnpymeisaino(rs.getAcptdnpymeisaino());
            jmVo.setAcptdnpymeisaiedano(rs.getAcptdnpymeisaiedano());
            jmVo.setItakuaiteapkihoncd(rs.getItakuaiteapkihoncd());
            jmVo.setItakuaiteshopcd(rs.getItakuaiteshopcd());
            jmVo.setItakuaiteacptdnpyno(rs.getItakuaiteacptdnpyno());
            jmVo.setItakuaiteedano(rs.getItakuaiteedano());
            jmVo.setItakuaiteacptdnpymeisaino(rs.getItakuaiteacptdnpymeisaino());
            jmVo.setItakuaiteacptdnpymeisaiedano(rs.getItakuaiteacptdnpymeisaiedano());
            jmVo.setLctgrycd(rs.getLctgrycd());
            jmVo.setMctgrycd(rs.getMctgrycd());
            jmVo.setSctgrycd(rs.getSctgrycd());
            jmVo.setShncd(rs.getShncd());
            jmVo.setHaifubutsuapkihoncd(rs.getHaifubutsuapkihoncd());
            jmVo.setHaifubutsushopcd(rs.getHaifubutsushopcd());
            jmVo.setHaifubutsucd(rs.getHaifubutsucd());
            jmVo.setKatasiki(rs.getKatasiki());
            jmVo.setKeijokbn(rs.getKeijokbn());
            jmVo.setAcptsuryo(rs.getAcptsuryo());
            jmVo.setHyojununt(rs.getHyojununt());
            jmVo.setAcptunt(rs.getAcptunt());
            jmVo.setAcptkin(rs.getAcptkin());
            jmVo.setItakucmpnyacptunt(rs.getItakucmpnyacptunt());
            jmVo.setItakucmpnyacptuntshohitaxkin(rs.getItakucmpnyacptuntshohitaxkin());
            jmVo.setItakucmpnyacptshohitaxkin(rs.getItakucmpnyacptshohitaxkin());
            jmVo.setNohinsuryo(rs.getNohinsuryo());
            jmVo.setNohinunt(rs.getNohinunt());
            jmVo.setNohinkin(rs.getNohinkin());
            jmVo.setNohinuntshohitaxkin(rs.getNohinuntshohitaxkin());
            jmVo.setNohinshohitaxkin(rs.getNohinshohitaxkin());
            jmVo.setTaxkbn(rs.getTaxkbn());
            jmVo.setYusyokbn(rs.getYusyokbn());
            jmVo.setMeisaikbn(rs.getMeisaikbn());
            jmVo.setNyuryokumeisaiflg(rs.getNyuryokumeisaiflg());
            jmVo.setTanpinmeisaiflg(rs.getTanpinmeisaiflg());
            jmVo.setStshncd(rs.getStshncd());
            jmVo.setMeisaibiko(rs.getMeisaibiko());
            jmVo.setDatsksmotokbn(rs.getDatsksmotokbn());
            jmVo.setYukokbn(rs.getYukokbn());
            jmVo.setKshnsystmymd(rs.getKshnsystmymd());
            jmVo.setKshnsystmjikan(rs.getKshnsystmjikan());
            jmVo.setShnapkihoncd(rs.getShnapkihoncd());
            jmVo.setShnnmryakusho(rs.getShnnmryakusho());
            jmVo.setPricekbn(rs.getPricekbn());
            jmVo.setHyojunpricekirikaedd(rs.getHyojunpricekirikaedd());
            jmVo.setGenzhyojunprice(rs.getGenzhyojunprice());
            jmVo.setShinhyojunprice(rs.getShinhyojunprice());
            jmVo.setGenzhyojunmochikaeriprice(rs.getGenzmochikaeriprice());
            jmVo.setShinhyojunmochikaeriprice(rs.getShinmochikaeriprice());
            jmVo.setZankaisu(rs.getZankaisu());
            jmVo.setSoukaisu(rs.getSoukaisu());

            if (CommonConstants.KBN_PRICE_HYOJUN.equals(rs.getPricekbn())) {
                jmVo.setShoppricekirikaedd(rs.getHyojunpricekirikaedd());
                jmVo.setGenzshopprice(rs.getGenzhyojunprice());
                jmVo.setShinshopprice(rs.getShinhyojunprice());
                jmVo.setGenzshopmochikaeriprice(rs.getGenzmochikaeriprice());
                jmVo.setShinshopmochikaeriprice(rs.getShinmochikaeriprice());
            } else {
                jmVo.setShoppricekirikaedd(rs.getOpnpricekirikaedd());
                jmVo.setGenzshopprice(rs.getGenzopnprice());
                jmVo.setShinshopprice(rs.getShinopnprice());
                jmVo.setGenzshopmochikaeriprice(rs.getGenzopnmochikaeriprice());
                jmVo.setShinshopmochikaeriprice(rs.getShinopnmochikaeriprice());
            }

            if (!vo.isYoyaku() && CommonConstants.FLG_ON.equals(rs.getHansokukbn())) {
                jmVo.setHansokustrtdd(rs.getHansokustrtdd());
                jmVo.setHansokushrydd(rs.getHansokushrydd());
                jmVo.setHansokuprice(rs.getHansokudlvryprice());
                jmVo.setHansokumochikaeriprice(rs.getHansokumochikaeriprice());
                jmVo.setHansokuyusenkbn(rs.getHansokuyusenkbn());
            }
            jmVo.setHansokukbn(rs.getHansokukbn());
            if (CommonConstants.FLG_ON.equals(rs.getRknkindtokubetsuuntflg())) {
                if (CommonConstants.FLG_ON.equals(rs.getStkbn())) {
                    jmVo.setTtpricestrtdd(rs.getSetTekiyostrtdd());
                    jmVo.setTtpriceshrydd(rs.getSetTekiyoshrydd());
                    jmVo.setTtprice(rs.getSetDlvryprice());
                    jmVo.setTtmochikaeriprice(rs.getSetMochikaeriprice());
                } else {
                    jmVo.setTtpricestrtdd(rs.getTekiyostrtdd());
                    jmVo.setTtpriceshrydd(rs.getTekiyoshrydd());
                    jmVo.setTtprice(rs.getDlvryprice());
                    jmVo.setTtmochikaeriprice(rs.getMochikaeriprice());
                }
            }
            jmVo.setAcptuntshohitaxkin(rs.getAcptuntshohitaxkin());
            jmVo.setAcptshohitaxkin(rs.getAcptshohitaxkin());
            jmVo.setShokidsplyprice(rs.getAcptunt().toString());
            jmVo.setZaikount(rs.getZaikount());
            jmVo.setIro(rs.getIro());
            jmVo.setUritrtskitani(rs.getUritrtskitani());
            jmVo.setUriirisu(rs.getUriirisu());
            jmVo.setUritani(rs.getUritani());
            jmVo.setHaifubutsunm(rs.getHaifubutsunm());
            jmVo.setKeijokbnnm(rs.getKeijokbnnm());
            jmVo.setSz(rs.getSz());
            jmVo.setSokazu(rs.getSokazu());
            if (CommonConstants.FLG_ON.equals(rs.getTaxsetteikbn())) {
                jmVo.setShntaxkbn(rs.getShntaxkbn());
            }
            jmVo.setShnnm(rs.getShnnm());
            jmVo.setHyojunprice(rs.getHyojunprice());
            jmVo.setUridnpyno(rs.getUridnpyno());
            jmVo.setUridnpyedano(rs.getUridnpyedano());
            jmVo.setUridnpymeisaino(rs.getUridnpymeisaino());
            jmVo.setUridnpymeisaiedano(rs.getUridnpymeisaiedano());
            jmVo.setToshaapkihoncd(rs.getToshaapkihoncd());
            jmVo.setToshashopcd(rs.getToshashopcd());
            jmVo.setToshacd(rs.getToshacd());
            jmVo.setInptsystmymd(rs.getInptsystmymd());
            jmVo.setInptsystmjikan(rs.getInptsystmjikan());
            jmVo.setToshamei(rs.getToshamei());
            jmVo.setKoshaapkihoncd(rs.getKoshaapkihoncd());
            jmVo.setKoshashopcd(rs.getKoshashopcd());
            jmVo.setKoshacd(rs.getKoshacd());
            jmVo.setKshnsystmymd(rs.getKshnsystmymd());
            jmVo.setKshnsystmjikan(rs.getKshnsystmjikan());
            jmVo.setKoshamei(rs.getKoshamei());
            if (CommonConstants.FLG_ON.equals(rs.getRknkindtokubetsuuntflg())) {
                jmVo.setSetTekiyostrtdd(rs.getSetTekiyostrtdd());
                jmVo.setSetTekiyoshrydd(rs.getSetTekiyoshrydd());
                jmVo.setSetDlvryprice(rs.getSetDlvryprice());
                jmVo.setSetMochikaeriprice(rs.getSetMochikaeriprice());
            }
            resultList.add(jmVo);
        }

        return resultList;
    }


    /**
     * 得意先配達情報を取得します．
     * 
     * @param key 得意先マスタモデルのKey
     * @param targetDate 取得対象日付
     * @return S01JuchuDenpyoVO 結果
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public JuchuDenpyoVO getTokuisakiDlvryInfo(S02M013TEntityKey key, String targetDate) {
        JuchuDenpyoVO vo = new JuchuDenpyoVO();
        // パラメータセット
        S02M013TEntityKey entity = new S02M013TEntityKey();
        entity.setApkihoncd(key.getApkihoncd());
        entity.setShopcd(key.getShopcd());
        entity.setTokuisakicd(key.getTokuisakicd());
        // 得意先配達情報を取得
        S02M013TTokuisakiDlvryInfoEntity result =
            this.s02m013tDao.getTokuisakiDlvryInfo(targetDate, entity);

        if (result != null) {
            vo.setApkihoncd(result.getApkihoncd());
            vo.setShopcd(result.getShopcd());
            vo.setTokuisakicd(result.getTokuisakicd());
            vo.setTokuisakinmn(result.getTokuisakinmn1());
            vo.setTokuisakijotai(result.getTokuisakijotai());
            vo.setJotaists(result.getJotaists());
            vo.setTokuisakikbn(result.getTokuisakikbn());
            vo.setAcptteishiflg(result.getAcptteishiflg());
            vo.setDlvrykankatsuapkihoncd(result.getDlvrykankatsuapkihoncd());
            vo.setDlvrykankatsushopcd(result.getDlvrykankatsushopcd());
            vo.setDlvrykyotencd(result.getDlvrykankatsukyotencd());
            vo.setDlvryareacd(result.getDlvryareacd());
            vo.setDlvrykbn(result.getDlvrykbn());
            vo.setDlvrycmpnycd(result.getDlvrycmpnycd());
            vo.setDlvrytnshacd(result.getDlvrytnshacd());
            vo.setDlvryJun(result.getDlvryjun());
            vo.setFzaiokikanoflg(result.getFzaiokikanoflg());
            vo.setNohinsakijohon1(result.getNohinsakijohon1());
            vo.setNohinsakijohon2(result.getNohinsakijohon2());
            vo.setRtlskykanrikbn(result.getRtlskykanrikbn());
            vo.setAzukaribtln1n2rittoruhns(Integer.valueOf(result.getAzukaribtln1n2rittoruhns()));
            vo.setAzukaribtln8rittoruhns(Integer.valueOf(result.getAzukaribtln8rittoruhns()));
            vo.setDlvrykyotennm(result.getDlvrykyotennm());
            vo.setDlvryareanm(result.getDlvryareanm());
            vo.setDlvryptnnm(result.getDlvrypatern());
            vo.setDlvrytnshamei(result.getDlvrytnshamei());
            vo.setNohinsakijohon1(result.getNohinsakijohonmei1());
            vo.setNohinsakijohon2(result.getNohinsakijohonmei2());
            vo.setDlvrykibojikan(result.getAcptjikanshiteijikoku());
            vo.setDlvrykibojikanshry(result.getAcptjikanshiteijikokushry());
            vo.setDlvrykibojikankbn(result.getAcptjikanshiteikbn());
            vo.setPrntmemon1(result.getPrntmemon1());
            vo.setPrntmemon2(result.getPrntmemon2());
            vo.setPrntmemon3(result.getPrntmemon3());
            vo.setSkysakicd(result.getSkysakicd());
        }

        return vo;
    }

    /**
     * 明細情報を受注伝票単位で削除します．
     * 
     * @param vo 受注配達VO
     * @return int 更新件数
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public int deleteMeisai(JuchuDenpyoVO vo) {
        int result = 0;
        // パラメータセット
        S04T002TEntityKey entityKey = new S04T002TEntityKey();
        entityKey.setApkihoncd(vo.getApkihoncd());
        entityKey.setShopcd(vo.getShopcd());
        entityKey.setAcptdnpyno(vo.getAcptdnpyno());
        entityKey.setEdano(vo.getEdano());
        // 明細情報を受注伝票単位で削除
        result = this.s04t002tDao.deleteMeisai(entityKey);
        return result;
    }

    /**
     * 新規受注取込ワークを担当者単位で削除します．
     * 
     * @param vo 新規受注VO
     * @return int 更新件数
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public int deleteShinkiWork(FileShinkiJuchuVO vo) {
        S04W001TEntityExample example = new S04W001TEntityExample();
        example.createCriteria().andLoginmonoapkihoncdEqualTo(vo.getLoginmonoapkihoncd())
            .andLoginmonoshopcdEqualTo(vo.getLoginmonoshopcd())
            .andLogintantomonocdEqualTo(vo.getLogintantomonocd());
        return s04w001tDao.deleteByExample(example);
    }

    /**
     * 新規受注取込ワークリストを取得します．
     * 
     * @param vo 新規受注VO
     * @param resultFg 結果取得フラグ
     * @return resultList 結果のリスト
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public List<S04W001TEntityEx> getShinkiWork(FileShinkiJuchuVO vo, boolean resultFg) {
        List<S04W001TEntityEx> resultList = new ArrayList<S04W001TEntityEx>();
        // パラメータセット
        S04W001TEntityKey entityKey = new S04W001TEntityKey();
        entityKey.setLoginmonoapkihoncd(vo.getLoginmonoapkihoncd());
        entityKey.setLoginmonoshopcd(vo.getLoginmonoshopcd());
        entityKey.setLogintantomonocd(vo.getLogintantomonocd());
        // 新規受注取込ワークリストを取得
        List<S04w001tShinkiWorkEntity> workList =
            this.s04w001tDao.getShinkiWork(entityKey, resultFg);
        // 取得した値をList<S04w001tEntityModelEx>にセット
        for (S04w001tShinkiWorkEntity rs : workList) {
            S04W001TEntityEx model = new S04W001TEntityEx();
            model.setLoginmonoapkihoncd(rs.getLoginmonoapkihoncd());
            model.setLoginmonoshopcd(rs.getLoginmonoshopcd());
            model.setLogintantomonocd(rs.getLogintantomonocd());
            model.setGyonol(rs.getGyonol());
            model.setApkihoncd(rs.getApkihoncd());
            model.setShopcd(rs.getShopcd());
            model.setTokuisakicd(rs.getTokuisakicd());
            model.setAcptdd(rs.getAcptdd());
            model.setAcpttnshaapkihoncd(rs.getAcpttnshaapkihoncd());
            model.setAcpttnshashopcd(rs.getAcpttnshashopcd());
            model.setAcpttnshacd(rs.getAcpttnshacd());
            model.setDlvryytidd(rs.getDlvryytidd());
            model.setDlvrykibojikan(rs.getDlvrykibojikan());
            model.setDlvrykibojikankbn(rs.getDlvrykibojikankbn());
            model.setKybtln1n2rittoruhns(rs.getKybtln1n2rittoruhns());
            model.setKybtln8rittoruhns(rs.getKybtln8rittoruhns());
            model.setFzaiokikanoflg(rs.getFzaiokikanoflg());
            model.setPrntmemo(rs.getPrntmemo());
            model.setSksmotokanrikbn(rs.getSksmotokanrikbn());
            model.setSksmotokanrino(rs.getSksmotokanrino());
            model.setShncdn1(rs.getShncdn1());
            model.setKatasikin1(rs.getKatasikin1());
            model.setKeijokbnn1(rs.getKeijokbnn1());
            model.setAcptsuryon1(rs.getAcptsuryon1());
            model.setAcptuntn1(rs.getAcptuntn1());
            model.setMeisaibikon1(rs.getMeisaibikon1());
            model.setShncdn2(rs.getShncdn2());
            model.setKatasikin2(rs.getKatasikin2());
            model.setKeijokbnn2(rs.getKeijokbnn2());
            model.setAcptsuryon2(rs.getAcptsuryon2());
            model.setAcptuntn2(rs.getAcptuntn2());
            model.setMeisaibikon2(rs.getMeisaibikon2());
            model.setShncdn3(rs.getShncdn3());
            model.setKatasikin3(rs.getKatasikin3());
            model.setKeijokbnn3(rs.getKeijokbnn3());
            model.setAcptsuryon3(rs.getAcptsuryon3());
            model.setAcptuntn3(rs.getAcptuntn3());
            model.setMeisaibikon3(rs.getMeisaibikon3());
            model.setShncdn4(rs.getShncdn4());
            model.setKatasikin4(rs.getKatasikin4());
            model.setKeijokbnn4(rs.getKeijokbnn4());
            model.setAcptsuryon4(rs.getAcptsuryon4());
            model.setAcptuntn4(rs.getAcptuntn4());
            model.setMeisaibikon4(rs.getMeisaibikon4());
            model.setShncdn5(rs.getShncdn5());
            model.setKatasikin5(rs.getKatasikin5());
            model.setKeijokbnn5(rs.getKeijokbnn5());
            model.setAcptsuryon5(rs.getAcptsuryon5());
            model.setAcptuntn5(rs.getAcptuntn5());
            model.setMeisaibikon5(rs.getMeisaibikon5());
            model.setShncdn6(rs.getShncdn6());
            model.setKatasikin6(rs.getKatasikin6());
            model.setKeijokbnn6(rs.getKeijokbnn6());
            model.setAcptsuryon6(rs.getAcptsuryon6());
            model.setAcptuntn6(rs.getAcptuntn6());
            model.setMeisaibikon6(rs.getMeisaibikon6());
            model.setKekka(rs.getKekka());
            model.setTokuisakinmn1(rs.getTokuisakinmn1());
            resultList.add(model);
        }
        return resultList;
    }

    /**
     * 新規受注取込時の重複行数を取得します．
     * 
     * @param vo 受注伝票VO
     * @return int 結果
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public int getDuplicationCount(JuchuDenpyoVO vo) {
        // パラメータセット
        S04T001TEntity entity = new S04T001TEntity();
        entity.setApkihoncd(vo.getApkihoncd());
        entity.setShopcd(vo.getShopcd());
        entity.setSksmotokanrikbn(vo.getSksmotokanrikbn());
        entity.setSksmotokanrino(vo.getSksmotokanrino());
        // 新規受注取込時の重複行数を取得
        int result = Math.toIntExact(this.s04t001tDao.getDuplicationCount(entity));
        return result;
    }

    /**
     * 指定された受注番号内の受注金額合計、最終受注日を取得します．
     * 
     * @param vo 新規受注VO
     * @return List 結果
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public List<S02M050TEntity> getDenpyoTokuisakiSummary(FileShinkiJuchuVO vo,
        S02M046TEntity shopModel) {
        List<S02M050TEntity> result = new ArrayList<S02M050TEntity>();
        // パラメータセット
        S04T001TEntityKey key = new S04T001TEntityKey();
        key.setApkihoncd(vo.getApkihoncd());
        key.setShopcd(vo.getShopcd());
        // 指定された受注番号内の受注金額合計、最終受注日を取得
        List<S04T001TDenpyoTokuisakiEntity> resultList = this.s04t001tDao.getDenpyoTokuisakiSummary(
            key, vo.getAcptdennostart(), vo.getAcptdennoend(), vo.getLoginmonoapkihoncd(),
            vo.getLoginmonoshopcd(), vo.getLogintantomonocd(), shopModel.getInvoicshikoymd());
        // 取得した値をセット
        for (S04T001TDenpyoTokuisakiEntity rs : resultList) {
            S02M050TEntity model = new S02M050TEntity();
            model.setApkihoncd(rs.getApkihoncd());
            model.setShopcd(rs.getShopcd());
            model.setTokuisakicd(rs.getTokuisakicd());
            model.setSaishuacptdd(rs.getSaishuacptdd());
            model.setKeijomaeacptkin(rs.getAcptkin() + rs.getAcptshohitaxkin());
            result.add(model);
        }
        return result;
    }

    // ■:1 Base)chokenkun 2017-01-06 キャンセル時最終受注日設定戻す
    public String getSaishuacptdd(JuchuDenpyoVO denVo) {
        // パラメータセット
        S04T001TEntity entity = new S04T001TEntity();
        entity.setApkihoncd(denVo.getApkihoncd());
        entity.setShopcd(denVo.getShopcd());
        entity.setTokuisakicd(denVo.getTokuisakicd());
        entity.setAcptdnpyno(denVo.getAcptdnpyno());
        // 最終受注日を検索
        String result = String.valueOf(this.s04t001tDao.getSaishuacptdd(entity));
        return result;
    }

    // ■:1 IBIS)Shinoda 2017-06-29 add begin
    /**
     * AP独自商品か判断をします。
     * 
     * @param String apkihoncd
     * @param String shopcd
     * @param String shncd
     * @return boolean AP独自商品であればtrue
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public boolean dokujiShnChk(String apkihoncd, String shopcd, String shncd) {
        S02V001TEntityExample example = new S02V001TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd)
            .andShncdEqualTo(shncd).andShnzokuseiEqualTo("1");
        List<S02V001TEntity> list = s02v001tDao.selectByExample(example);
        if (list.size() != 0) {
            S02V001TEntity model = list.get(0);
            if (model != null) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 委託会社に商品が存在するか判断をします。
     * 
     * @param String apkihoncd
     * @param String shopcd
     * @param String shncd
     * @return boolean 商品が存在しなければtrue
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public boolean ShnSnziChk(String apkihoncd, String shopcd, String shncd) {
        S02M043TEntityExample example = new S02M043TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd)
            .andShncdEqualTo(shncd).andYukokbnEqualTo("1");
        List<S02M043TEntity> list = s02m043tDao.selectByExample(example);
        if (list.size() == 0) {
            return true;
        }
        S02M043TEntity model = list.get(0);
        if (model != null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 受注情報を登録後、該当得意先が委託顧客、配達顧客の場合に起動され、受託顧客の受注情報の作成を行う。
     * 
     * @param String apkihoncd
     * @param String shopcd
     * @param String acptdnpyno
     * @param String edaNo
     * @param String shoriMode
     * @return boolean バッチ実行結果
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public boolean honbuJuchuDtRenkei(String apkihoncd, String shopcd, String acptdnpyno,
        String edaNo, String shoriMode) {
        C0804B01Entity entity = new C0804B01Entity();
        entity.setApkihoncd(apkihoncd);
        entity.setShopcd(shopcd);
        entity.setAcptdnpyno(acptdnpyno);
        entity.setEdano(Integer.valueOf(edaNo));
        entity.setMode(shoriMode);
        c0804b01Dao.honbuJuchuDatRenkei(entity);
        return true;
    }

    /* 2017-11-20 ibis)inoue add begin */
    /**
     * 新規受注取込在庫・アラーム更新用サマリリストを取得します．
     * 
     * @param vo 受注配達VO
     * @return resultList 結果のリスト
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public List<ZaikoKeyVo> getTeikiZaikoList2(JuchuTeikiVo vo) {
        // パラメータセットおよび取得
        S04001TTeikiZaikoEntityKey key = new S04001TTeikiZaikoEntityKey();
        key.setApkihoncd(vo.getApkihoncd());
        key.setShopcd(vo.getShopcd());
        key.setAcptdennostart(vo.getAcptdennostart());
        key.setAcptdennoend(vo.getAcptdennoend());
        key.setLoginmonoapkihoncd(vo.getLoginmonoapkihoncd());
        key.setLoginmonoshopcd(vo.getLoginmonoshopcd());
        key.setLogintantomonocd(vo.getLogintantomonocd());
        JuchuRelationVo relationVo = vo.getRelationVo();
        List<String> regularcdlist = relationVo.getRegularcdList();
        List<String> slimcdlist = relationVo.getSlimcdList();
        // 結果値収納用オブジェクト初期化
        List<S04001TTeikiZaikoEntity> teikiZaikoList = new ArrayList<S04001TTeikiZaikoEntity>();

        switch (vo.getZaikotype()) {
            case ZaikoKeyVo.TYPE_S04T005T:
                teikiZaikoList =
                    this.s04t001tDao.getTeikiZaikoListSqlS04T005T2(key, regularcdlist, slimcdlist);
                break;
            case ZaikoKeyVo.TYPE_S04T006T:
                teikiZaikoList = this.s04t001tDao.getTeikiZaikoListSqlS04T006T2(key);
                break;
            case ZaikoKeyVo.TYPE_S04T007T:
                teikiZaikoList = this.s04t001tDao.getTeikiZaikoListSqlS04T007T2(key);
                break;
            case ZaikoKeyVo.TYPE_S13T004T:
                teikiZaikoList = this.s04t001tDao.getTeikiZaikoListSqlS13T004T2(key);
                break;
        }
        // 結果設定
        List<ZaikoKeyVo> resultList = new ArrayList<ZaikoKeyVo>();
        for (S04001TTeikiZaikoEntity rs : teikiZaikoList) {
            ZaikoKeyVo keyVo = new ZaikoKeyVo(vo.getZaikotype(), false, false,
                rs.getDlvrykankatsuapkihoncd(), rs.getDlvrykankatsushopcd(),
                (vo.getZaikotype() == ZaikoKeyVo.TYPE_S04T006T) ? "00" : rs.getDlvrykyotencd(),
                ZaikoKeyVo.TYPE_S13T004T == vo.getZaikotype() ? "" : rs.getDlvryytidd(),
                rs.getFourthkey(), "0", rs.getAcptsuryo().toString());
            resultList.add(keyVo);
        }
        return resultList;
    }

    public S04T001TEntity findByPrimaryKeyForLock(JuchuDenpyoVO vo) {
        if (vo.getKshnsystmymd() == null || vo.getKshnsystmjikan() == null) {
            throw new BusinessException("");
        }
        S04T001TEntity entity = s04t001tDao.selectByPrimaryKeyForLock(vo);

        if (entity.getKshnsystmymd().equals(vo.getKshnsystmymd())
            && entity.getKshnsystmjikan().equals(vo.getKshnsystmjikan())) {
            return entity;
        } else {
            throw new RecordUpdatedException(); // 既に更新されている
        }
    }
}
