package jp.co.aquaclara.accis.web.domain.service.d1501;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jp.co.aquaclara.accis.common.constants.SystemConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M017TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S06T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M017TD1501W11TairyuUpdateEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S06T001TD1501W11CreateJidoNebikiEntity;
import jp.co.aquaclara.accis.common.exception.BusinessException;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.web.domain.dto.d1501.D1501W11SearchConditionDto;
import jp.co.aquaclara.accis.web.domain.service.common.OnlineBatchBaseService;
import jp.co.aquaclara.accis.web.domain.service.common.OnlineBatchCommonModel;

/**
 * 請求締処理実行サービスクラス
 */
@Service
public class D1501B01Service extends OnlineBatchBaseService<D1501W11SearchConditionDto> {

    /** 処理ID */
    private static final String PROCESS_ID = "D1501B01";

    /**
     * 処理ID取得
     * 
     * @return 処理ID
     */
    @Override
    public String getProcessId() {
        return PROCESS_ID;
    }

    /** 請求先マスタDao */
    @Autowired
    protected S02M017TDao s02M017TDao;
    /** 売上テーブルDao */
    @Autowired
    protected S06T001TDao s06T001TDao;
    /** 請求残高テーブルDao */
    @Autowired
    protected S07T001TDao s07T001TDao;

    /**
     * 請求締処理実行
     * 
     * @param paramModel 請求締検索条件Dto
     * @param commonInfo 非同期処理共有情報モデルクラス
     * @throws BusinessException 業務例外
     */
    @Override
    protected void process(D1501W11SearchConditionDto paramModel, OnlineBatchCommonModel commonInfo)
        throws BusinessException {
        // 請求締年月日取得
        String shimedydmdd = DateTimeUtil.toNoSlashedDate(paramModel.getSkyshimedate());

        // 月次自動値引処理
        executeCreateJidouNebiki(paramModel, shimedydmdd);

        // 請求締処理
        executeSkyShime(paramModel, shimedydmdd);

        // 滞留更新処理
        executeTairyuUpdate(paramModel, shimedydmdd);
    }

    /**
     * 月次自動値引処理
     * 
     * @param paramModel 請求締検索条件Dto
     * @param shimedydmdd 請求締年月日
     */
    protected void executeCreateJidouNebiki(D1501W11SearchConditionDto paramModel,
        String shimedydmdd) {
        S06T001TD1501W11CreateJidoNebikiEntity s06T001TEntity =
            new S06T001TD1501W11CreateJidoNebikiEntity();
        s06T001TEntity.setApkihoncd(paramModel.getApkihoncd());
        s06T001TEntity.setShopcd(paramModel.getShopcd());
        s06T001TEntity.setShimedydmdd(shimedydmdd);
        s06T001TEntity.setPrcsmode(paramModel.getPrcskbn());
        s06T001TEntity.setKoshaapkihoncd(paramModel.getDbManagementSession().getApkihoncd());
        s06T001TEntity.setKoshashopcd(paramModel.getDbManagementSession().getShopcd());
        s06T001TEntity.setKoshacd(paramModel.getDbManagementSession().getTnshacd());
        s06T001TEntity.setKshnunyoymd(paramModel.getDbManagementSession().getUnyoDate());
        s06T001TEntity.setKshnipadrse(paramModel.getDbManagementSession().getIpAddress());
        s06T001TEntity.setKshnpgmid(paramModel.getDbManagementSession().getProgramId());
        s06T001TEntity.setKshnsystmymd(paramModel.getDbManagementSession().getSystemDate());
        s06T001TEntity.setKshnsystmjikan(paramModel.getDbManagementSession().getSystemTime());
        s06T001TDao.executeCreateJidouNebiki(s06T001TEntity);
    }

    /**
     * 請求締処理
     * 
     * @param paramModel 請求締検索条件Dto
     * @param shimedydmdd 請求締年月日
     */
    protected void executeSkyShime(D1501W11SearchConditionDto paramModel, String shimedydmdd) {
        S07T001TEntity s07T001TEntity = new S07T001TEntity();
        s07T001TEntity.setApkihoncd(paramModel.getApkihoncd());
        s07T001TEntity.setShopcd(paramModel.getShopcd());
        s07T001TEntity.setKonkaishimedd(shimedydmdd);
        s07T001TEntity.setKoshaapkihoncd(paramModel.getDbManagementSession().getApkihoncd());
        s07T001TEntity.setKoshashopcd(paramModel.getDbManagementSession().getShopcd());
        s07T001TEntity.setKoshacd(paramModel.getDbManagementSession().getTnshacd());
        s07T001TEntity.setKshnunyoymd(paramModel.getDbManagementSession().getUnyoDate());
        s07T001TEntity.setKshnipadrse(paramModel.getDbManagementSession().getIpAddress());
        s07T001TEntity.setKshnpgmid(paramModel.getDbManagementSession().getProgramId());
        s07T001TEntity.setKshnsystmymd(paramModel.getDbManagementSession().getSystemDate());
        s07T001TEntity.setKshnsystmjikan(paramModel.getDbManagementSession().getSystemTime());
        if (D1501W11Service.Process.SEIKYU_FIX.equals(paramModel.getPrcskbn())) {
            s07T001TDao.executeSkyShime(s07T001TEntity); // 請求締処理
        } else if (D1501W11Service.Process.SEIKYU_FIX_CANCEL.equals(paramModel.getPrcskbn())) {
            s07T001TDao.executeSkyKaijo(s07T001TEntity); // 請求締解除処理
        }
    }

    /**
     * 滞留更新処理
     * 
     * @param paramModel 請求締検索条件Dto
     * @param shimedydmdd 請求締年月日
     */
    protected void executeTairyuUpdate(D1501W11SearchConditionDto paramModel, String shimedydmdd) {
        S02M017TD1501W11TairyuUpdateEntity s02M017TEntity =
            new S02M017TD1501W11TairyuUpdateEntity();
        s02M017TEntity.setApkihoncd(paramModel.getApkihoncd());
        s02M017TEntity.setShopcd(paramModel.getShopcd());
        s02M017TEntity.setShimedydm(paramModel.getShimedd1());
        s02M017TEntity.setShimedydmdd(shimedydmdd);
        s02M017TEntity.setPrcskbn("1");
        s02M017TEntity.setNyknkin(0L);
        s02M017TEntity.setSkysakicd(SystemConstants.WORD_BLANK);
        s02M017TEntity.setKoshaapkihoncd(paramModel.getDbManagementSession().getApkihoncd());
        s02M017TEntity.setKoshashopcd(paramModel.getDbManagementSession().getShopcd());
        s02M017TEntity.setKoshacd(paramModel.getDbManagementSession().getTnshacd());
        s02M017TEntity.setKshnunyoymd(paramModel.getDbManagementSession().getUnyoDate());
        s02M017TEntity.setKshnipadrse(paramModel.getDbManagementSession().getIpAddress());
        s02M017TEntity.setKshnpgmid(paramModel.getDbManagementSession().getProgramId());
        s02M017TEntity.setKshnsystmymd(paramModel.getDbManagementSession().getSystemDate());
        s02M017TEntity.setKshnsystmjikan(paramModel.getDbManagementSession().getSystemTime());
        s02M017TDao.executeTairyuUpdate(s02M017TEntity);
    }
}
