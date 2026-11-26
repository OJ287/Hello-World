package jp.co.aquaclara.accis.web.domain.service.z1503;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M017TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S10T003TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S10T004TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TZ1503W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S10T003TZ1503W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S10T004TZ1503W01Entity;


/**
 * 未入金分照会Service
 */
@Service
public class Z1503W01Service {

    @Autowired
    private S02M017TDao s02m017tDao;

    @Autowired
    private S07T001TDao s07T001tDao;

    @Autowired
    private S10T003TDao s10T003tDao;

    @Autowired
    private S10T004TDao s10T004tDao;

    /**
     * 請求先マスタ検索処理
     * 
     * @param entity
     * @return
     */
    public S02M017TEntity searchKessai(String apkihoncd, String shopcd, String skysakicd) {

        S02M017TEntityKey entity = new S02M017TEntityKey();
        entity.setApkihoncd(apkihoncd);
        entity.setShopcd(shopcd);
        entity.setSkysakicd(skysakicd);
        // SELECT実行
        S02M017TEntity result = s02m017tDao.selectByPrimaryKey(entity);

        return result;
    }

    /**
     * 残高一覧取得
     */
    public List<S07T001TZ1503W01Entity> searchZandaka(String apkihoncd, String shopcd,
        String skysakicd) {

        // SELECT実行
        List<S07T001TZ1503W01Entity> result =
            s07T001tDao.selectByZ1503W01(apkihoncd, shopcd, skysakicd);

        // 結果セットをdtoに詰めて返却
        return result;
    }

    /**
     * 入金伝票情報取得
     */
    public S10T003TZ1503W01Entity searchNyukinDnpyo(String apkihoncd, String shopcd,
        String skysakicd) {

        // SELECT実行
        S10T003TZ1503W01Entity result = s10T003tDao.selectByZ1503W01(apkihoncd, shopcd, skysakicd);

        // 結果セットをdtoに詰めて返却
        return result;
    }

    /**
     * 入金一覧情報取得
     */
    public List<S10T004TZ1503W01Entity> searchNyukinIchiran(String apkihoncd, String shopcd,
        String nykndnpyono) {

        // SELECT実行
        List<S10T004TZ1503W01Entity> result =
            s10T004tDao.selectByZ1503W01(apkihoncd, shopcd, nykndnpyono);

        // 結果セットをdtoに詰めて返却
        return result;
    }
}
