package jp.co.aquaclara.accis.web.domain.service.z1802;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jp.co.aquaclara.accis.common.domain.repository.dao.S01T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M002TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M003TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M006TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M013TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M029TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M046TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M091TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.CtiEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.CtiTanEntity2;
import jp.co.aquaclara.accis.common.domain.repository.entity.CtiTokEntity2;
import jp.co.aquaclara.accis.common.domain.repository.entity.LoginUserEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.LoginUserEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S01T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S01T001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M002TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M002TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M003TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M003TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M006TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M006TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M029TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M029TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M091TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M091TEntityExample;
import jp.co.aquaclara.accis.web.controller.commons.LoginSession;
import jp.co.aquaclara.accis.web.controller.commons.TaxInfo;
import jp.co.aquaclara.accis.web.controller.commons.UserSessionForm;
import jp.co.aquaclara.accis.web.controller.z1802.LoginUserInfo;

@Service
@Transactional
public class Z1802W01Service {

    @Autowired
    HttpSession httpSession;
    @Autowired
    private LoginSession loginSession;
    @Autowired
    private UserSessionForm userSession;

    @Autowired
    private S01T001TDao s01t001tDao;
    @Autowired
    private S02M001TDao s02m001tDao;
    @Autowired
    private S02M002TDao s02m002tDao;
    @Autowired
    private S02M003TDao s02m003tDao;
    @Autowired
    private S02M006TDao s02m006tDao;
    @Autowired
    private S02M013TDao s02m013tDao;
    @Autowired
    private S02M029TDao s02m029tDao;
    @Autowired
    private S02M046TDao s02m046tDao;
    @Autowired
    private S02M091TDao s02m091tDao;

    /** 同一セッションログインチェック */
    public boolean checkSessionLogin(String sessionId) {
        S01T001TEntityExample sessionexample = new S01T001TEntityExample();
        sessionexample.createCriteria().andSessionidEqualTo(sessionId);
        List<S01T001TEntity> sessionlist = getList(sessionexample);
        if (sessionlist != null && sessionlist.size() > 0) {
            return true;
        }
        return false;
    }

    /** ログイン情報リスト取得 */
    public List<LoginUserInfo> getLoginUserInfo(String loginId, short edano) {
        List<LoginUserInfo> list = new ArrayList<LoginUserInfo>();
        LoginUserEntityKey key = new LoginUserEntityKey();
        key.setLoginid(loginId);
        key.setEdano(edano);
        List<LoginUserEntity> userlist = getLoginUser(key);
        for (LoginUserEntity user : userlist) {
            LoginUserInfo userinfo = new LoginUserInfo();
            userinfo.setAkshanikbn(user.getAkshanikbn());
            userinfo.setApkihoncd(user.getApkihoncd());
            userinfo.setApnm(user.getApnm());
            userinfo.setBushocd(user.getBushocd());
            userinfo.setBushokind(user.getBushokind());
            userinfo.setBushonm(user.getBushonm());
            userinfo.setBushoshopshitenkbn(user.getBushoshopshitenkbn());
            userinfo.setJigyoshokanri(user.getJigyoshokanri());
            userinfo.setJigyoshonm(user.getJigyoshonm());
            userinfo.setKanshakbn(user.getKanshakbn());
            userinfo.setKihonkbn(user.getKihonkbn());
            userinfo.setLoginflg(user.getLoginflg());
            userinfo.setLoginid(user.getLoginid());
            userinfo.setMobileriyoumu(user.getMobileriyoumu());
            userinfo.setAcptcenterno(user.getAcptcenterno());
            userinfo.setTnshamenuctgrycd(user.getTnshamenuctgrycd());
            userinfo.setPsw(user.getPsw());
            userinfo.setShopcd(user.getShopcd());
            userinfo.setShopkbn(user.getShopkbn());
            userinfo.setShopnm(user.getShopnm());
            userinfo.setShopshitenkbn(user.getShopshitenkbn());
            userinfo.setShozokujigyoshocd(user.getShozokujigyoshocd());
            userinfo.setTnshacd(user.getTnshacd());
            userinfo.setTnshamei(user.getTnshamei());
            userinfo.setTnshasts(user.getTnshasts());
            userinfo.setEdano(user.getEdano());
            list.add(userinfo);
        }
        return list;
    }

    /** 軽減税率情報リスト取得 */
    public List<TaxInfo> getTaxInfo() {
        List<TaxInfo> taxinfoList = new ArrayList<TaxInfo>();
        S02M091TEntityExample example3 = new S02M091TEntityExample();
        List<S02M091TEntity> taxlist = getList(example3);
        if (taxlist.size() > 0) {
            for (S02M091TEntity tax : taxlist) {
                TaxInfo taxinfo = new TaxInfo();
                java.math.BigDecimal dec = tax.getShohitax();
                taxinfo.setShohitax(dec.toString());
                taxinfo.setTaxkbn(tax.getTaxkbn());
                taxinfo.setTekiyostrtdd(tax.getTekiyostrtdd());
                taxinfo.setTekiyoshrydd(tax.getTekiyoshrydd());
                taxinfoList.add(taxinfo);
            }
        }
        return taxinfoList;
    }

    /** ログイン状況管理テーブルにレコード作成 */
    public void insertLoginStatus() {
        LocalDateTime nowDate = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formatNowDate = dtf.format(nowDate);
        String nowdate = formatNowDate.substring(0, 8);
        String nowtime = formatNowDate.substring(8);
        S01T001TEntity roginstatusrecord = new S01T001TEntity();
        roginstatusrecord.setApkihoncd(userSession.getApkihoncd());
        roginstatusrecord.setShopcd(userSession.getShopcd());
        roginstatusrecord.setTnshacd(userSession.getTnshacd());
        roginstatusrecord.setEdano(loginSession.getLoginKind());
        roginstatusrecord.setSessionid(httpSession.getId());
        roginstatusrecord.setToshaapkihoncd(userSession.getApkihoncd());
        roginstatusrecord.setToshashopcd(userSession.getShopcd());
        roginstatusrecord.setToshacd(userSession.getTnshacd());
        roginstatusrecord.setInptsystmymd(nowdate);
        roginstatusrecord.setInptsystmjikan(nowtime);
        roginstatusrecord.setInptunyoymd(loginSession.getUnyoDate());
        roginstatusrecord.setInptipadrse(loginSession.getIpAddress());
        roginstatusrecord.setInptpgmid("Z1802w01");
        roginstatusrecord.setKoshaapkihoncd(userSession.getApkihoncd());
        roginstatusrecord.setKoshashopcd(userSession.getShopcd());
        roginstatusrecord.setKoshacd(userSession.getTnshacd());
        roginstatusrecord.setKshnsystmymd(nowdate);
        roginstatusrecord.setKshnsystmjikan(nowtime);
        roginstatusrecord.setKshnunyoymd(loginSession.getUnyoDate());
        roginstatusrecord.setKshnipadrse(loginSession.getIpAddress());
        roginstatusrecord.setKshnpgmid("Z1802w01");
        insert(roginstatusrecord);
    }

    /** ログイン状況管理テーブルからレコード削除(AP基本コード,SHOPコード,担当者コード,枝番号) */
    public void deleteLoginStatus() {
        S01T001TEntityExample example = new S01T001TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(userSession.getApkihoncd())
            .andShopcdEqualTo(userSession.getShopcd()).andTnshacdEqualTo(userSession.getTnshacd())
            .andEdanoEqualTo(loginSession.getLoginKind());
        delete(example);
    }

    /** ログイン状況管理テーブルからレコード削除(セッションID) */
    public void deleteLoginStatusBySessionId() {
        S01T001TEntityExample example = new S01T001TEntityExample();
        example.createCriteria().andSessionidEqualTo(httpSession.getId());
        delete(example);
    }

    /** ショップマスタのSHOPコードの存在チェック */
    public boolean checkShopMaster(String apkihoncd, String shopcd) {
        S02M046TEntityExample example = new S02M046TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd);
        List<S02M046TEntity> shoplist = getList(example);
        if (shoplist != null && shoplist.size() > 0) {
            return true;
        }
        return false;
    }

    /** システム管理マスタ設定値取得(AP基本コード,SHOPコード) */
    public String getSystemValue(String apkihoncd, String shopcd, String setteino,
        String setteicd) {
        S02M006TEntityExample example1 = new S02M006TEntityExample();
        example1.createCriteria().andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd)
            .andRcrdkbnEqualTo("M").andSetteinoEqualTo(setteino).andSetteicdEqualTo(setteicd);
        List<S02M006TEntity> systemlist1 = getList(example1);
        if (systemlist1.size() > 0) {
            S02M006TEntity systeminfo = systemlist1.get(0);
            return systeminfo.getMojisetteiatai();
        }
        return null;
    }

    /** システム管理マスタ設定値（数値)取得(AP基本コード,SHOPコード) */
    public Integer getSystemIntegerValue(String apkihoncd, String shopcd, String setteino,
        String setteicd) {
        S02M006TEntityExample example1 = new S02M006TEntityExample();
        example1.createCriteria().andApkihoncdEqualTo(apkihoncd).andShopcdEqualTo(shopcd)
            .andRcrdkbnEqualTo("M").andSetteinoEqualTo(setteino).andSetteicdEqualTo(setteicd);
        List<S02M006TEntity> systemlist1 = getList(example1);
        if (systemlist1.size() > 0) {
            S02M006TEntity systeminfo = systemlist1.get(0);
            return Integer.valueOf(systeminfo.getSuchisetteiatai().intValue());
        }
        return null;
    }

    public List<LoginUserEntity> getLoginUser(LoginUserEntityKey key) {
        List<LoginUserEntity> list = s02m003tDao.selectUserInfoByloginid(key);
        return list;
    }

    public List<CtiTanEntity2> getTanMaster(String apkihoncd, String shopcd, String lastdd,
        String lasttime) {
        CtiEntityKey key = new CtiEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setLastdd(lastdd);
        key.setLasttime(lasttime);
        List<CtiTanEntity2> list = s02m003tDao.selectMasterForCtiCsv2(key);
        return list;
    }

    public List<CtiTokEntity2> getTokMaster(String apkihoncd, String shopcd, String lastdd,
        String lasttime) {
        CtiEntityKey key = new CtiEntityKey();
        key.setApkihoncd(apkihoncd);
        key.setShopcd(shopcd);
        key.setLastdd(lastdd);
        key.setLasttime(lasttime);
        List<CtiTokEntity2> list = s02m013tDao.selectMasterForCtiCsv2(key);
        return list;
    }

    public void insert(S01T001TEntity record) {
        s01t001tDao.insert(record);
    }

    public void delete(S01T001TEntityExample example) {
        s01t001tDao.deleteByExample(example);
    }

    public List<S01T001TEntity> getList(S01T001TEntityExample example) {
        List<S01T001TEntity> list = s01t001tDao.selectByExample(example);
        return list;
    }

    public List<S02M001TEntity> getList(S02M001TEntityExample example) {
        List<S02M001TEntity> list = s02m001tDao.selectByExample(example);
        return list;
    }

    public List<S02M002TEntity> getList(S02M002TEntityExample example) {
        List<S02M002TEntity> list = s02m002tDao.selectByExample(example);
        return list;
    }

    public List<S02M003TEntity> getList(S02M003TEntityExample example) {
        List<S02M003TEntity> list = s02m003tDao.selectByExample(example);
        return list;
    }

    public List<S02M006TEntity> getList(S02M006TEntityExample example) {
        List<S02M006TEntity> list = s02m006tDao.selectByExample(example);
        return list;
    }

    public List<S02M029TEntity> getList(S02M029TEntityExample example) {
        List<S02M029TEntity> list = s02m029tDao.selectByExample(example);
        return list;
    }

    public List<S02M046TEntity> getList(S02M046TEntityExample example) {
        List<S02M046TEntity> list = s02m046tDao.selectByExample(example);
        return list;
    }

    public List<S02M091TEntity> getList(S02M091TEntityExample example) {
        List<S02M091TEntity> list = s02m091tDao.selectByExample(example);
        return list;
    }
}
