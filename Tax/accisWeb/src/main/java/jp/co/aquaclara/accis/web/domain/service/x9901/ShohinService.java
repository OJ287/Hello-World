package jp.co.aquaclara.accis.web.domain.service.x9901;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02V001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.business.ShohinDaoRepository;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M008TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.business.S02M008TEntityEx;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.JuchuDenpyoVO;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.JuchuMeisaiVO;

/**
 * 商品関連マスタ業務ロジックを記述するクラス。
 * 
 * @author Isac)Katayama
 * @version 1.0.0 2007-05-06
 */
@Service
public class ShohinService {

    @Autowired
    private S02V001TDao s02v001tDao;

    @Autowired
    private ShohinDaoRepository shohinDaoResository;

    /**
     * コンストラクタ
     * 
     * @param tran トランザクション
     */
    // public S02ShohinService(Transaction tran) { // 2017/05/10 ユン UPD
    public ShohinService() {
        super();
    }

    /**
     * S02ShohinDaoを取得します。
     * 
     * @return S02ShohinDao
     * @throws SystemException システム例外
     */
    private ShohinDaoRepository getShohinDao() {
        return shohinDaoResository;
    }

    /**
     * 本部商品かどうかを判定します。
     * 
     * @param shncd 商品コード
     * @return boolean 結果
     */
    public static boolean isHonbuShohin(String shncd) {
        return !StringUtil.isEmpty(shncd) && 0 < shncd.length()
            && CommonConstants.KBN_SHNZOKUSEI_HONBU.equals(shncd.substring(0, 1));
    }

    /**
     * 商品マスタビューモデルを取得します．
     * 
     * @param apKihonCd AP基本コード
     * @param shopCd ショップコード
     * @param shncd 商品コード
     * @return S01JuchuDenpyoVO 結果
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public S02V001TEntity getShohinModel(String apKihonCd, String shopCd, String shncd) {
        S02V001TEntityExample example = new S02V001TEntityExample();
        example.createCriteria().andApkihoncdEqualTo(apKihonCd).andShopcdEqualTo(shopCd)
            .andShncdEqualTo(shncd);
        List<S02V001TEntity> list = s02v001tDao.selectByExample(example);
        if (list.size() == 0) {
            return null;
        }
        S02V001TEntity shohinModel = list.get(0);

        if (shohinModel != null && CommonConstants.FLG_OFF.equals(shohinModel.getTaxsetteikbn())) {
            shohinModel.setTaxkbn(null);// 課税として税計算する
        }
        return shohinModel;
    }

    /**
     * セット商品リストを取得します．
     * 
     * @param apKihoncd AP基本コード
     * @param shopcd ショップコード
     * @param stshncd セット商品コード
     * @return List 結果
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public List<S02M008TEntityEx> getSetShohinList(String apKihoncd, String shopcd,
        String stshncd) {

        // 展開マスタKey生成
        S02V001TEntity model = new S02V001TEntity();
        model.setApkihoncd(apKihoncd);
        model.setShopcd(shopcd);
        model.setShncd(stshncd);

        // 検索実行
        return shohinDaoResository.getSetShohinList(model);
    }

    // 2020/11/18 価格改定対応 ISAC)金谷 START
    /**
     * 得意先別セット商品単価情報取得
     * 
     * @param childModel 展開マスタモデル
     * @param denVo 受注配達伝票モデル
     * @param untKijunbi 単価基準日
     * @return 得意先別セット商品単価情報
     * @throws ApplicationException
     * @throws SystemException
     */
    public JuchuMeisaiVO getSetShohinTankaList(S02M008TEntity childModel, JuchuDenpyoVO denVo,
        String untKijunbi) {

        // 検索実行
        return getShohinDao().getSetShohinTankaList(childModel, denVo, untKijunbi);
    }
    // 2020/11/18 価格改定対応 ISAC)金谷 END
}
