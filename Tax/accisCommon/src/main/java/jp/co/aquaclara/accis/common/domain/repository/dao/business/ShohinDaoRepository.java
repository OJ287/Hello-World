package jp.co.aquaclara.accis.common.domain.repository.dao.business;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M008TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M066TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M008TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M008TSetShohinEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M066TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M066TSetShohinTankaEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.business.S02M008TEntityEx;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.JuchuDenpyoVO;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.JuchuMeisaiVO;

/**
 * データアクセスクラス（商品関連）です。 QuiQpro共通クラスのBaseDAOを継承します。
 * 
 * @author Isac)片山
 * @version 1.0.0 2007-05-06
 */
@Repository
public class ShohinDaoRepository {

    @Autowired
    private S02M008TDao s02m008tDao;

    @Autowired
    private S02M066TDao s02m066tDao;

    /**
     * コンストラクタ
     * 
     * @param con コネクション
     */
    public ShohinDaoRepository() {
        super();
    }

    /**
     * セット商品リストを取得します．
     * 
     * @param key 展開マスタのキー
     * @return List 結果
     * @throws ApplicationException アプリケーション例外
     * @throws SystemException システム例外
     */
    public List<S02M008TEntityEx> getSetShohinList(S02V001TEntity key) {
        List<S02M008TEntityEx> resultList = new ArrayList<S02M008TEntityEx>();
        // セット商品リストを取得
        List<S02M008TSetShohinEntity> shohinEntityList =
            this.s02m008tDao.getSetShohinList(key.getApkihoncd(), key.getShopcd(), key.getShncd());
        // セット商品情報のセット
        for (S02M008TSetShohinEntity rs : shohinEntityList) {
            S02M008TEntityEx model = new S02M008TEntityEx();
            model.setApkihoncd(rs.getApkihoncd());
            model.setStshncd(rs.getStshncd());
            model.setRenban(rs.getRenban());
            model.setKoseishnapkihoncd(rs.getKoseishnapkihoncd());
            model.setKoseishncd(rs.getKoseishncd());
            model.setKeijokbn(rs.getKeijokbn());
            model.setSuryo(rs.getSuryo());
            model.setDlvryprice(rs.getDlvryprice());
            model.setMochikaeriprice(rs.getMochikaeriprice());
            model.setLctgrycd(rs.getLctgrycd());
            model.setMctgrycd(rs.getMctgrycd());
            model.setSctgrycd(rs.getSctgrycd());
            model.setHyojunpricekirikaedd(rs.getHyojunpricekirikaedd());
            model.setGenzhyojundlvryprice(rs.getGenzhyojunprice().toString());
            model.setShinhyojundlvryprice(rs.getShinhyojunprice().toString());
            model.setGenzhyojunmochikaeriprice(rs.getGenzmochikaeriprice().toString());
            model.setShinhyojunmochikaeriprice(rs.getShinmochikaeriprice().toString());
            if (rs.getTaxsetteikbn().equals("1")) {
                model.setTaxkbn(rs.getTaxkbn());
            } else {
                model.setTaxkbn("");
            }
            resultList.add(model);
        }
        return resultList;
    }

    /**
     * 得意先別セット商品単価情報取得
     * 
     * @param childModel 展開マスタモデル
     * @param denVo 受注配伝票モデル
     * @param untKijunbi 単価基準日
     * @return 得意先別セット商品単価情報
     * @throws ApplicationException
     * @throws SystemException
     */
    public JuchuMeisaiVO getSetShohinTankaList(S02M008TEntity childModel, JuchuDenpyoVO denVo,
        String untKijunbi) {
        List<JuchuMeisaiVO> resultList = new ArrayList<JuchuMeisaiVO>();
        // パラメータセット
        S02M066TEntityKey key = new S02M066TEntityKey();
        key.setApkihoncd(denVo.getApkihoncd());
        key.setShopcd(denVo.getShopcd());
        key.setStshncd(childModel.getStshncd());
        key.setTokuisakicd(denVo.getTokuisakicd());
        // 得意先別セット商品単価情報取得
        List<S02M066TSetShohinTankaEntity> shohinTankaList =
            this.s02m066tDao.getSetShohinTankaList(key, untKijunbi, childModel.getRenban());
        // 取得した値をセット
        JuchuMeisaiVO model = new JuchuMeisaiVO();
        for (S02M066TSetShohinTankaEntity rs : shohinTankaList) {
            if (rs.getRknkindtokubetsuuntflg().equals("1")) {
                model.setSetTekiyostrtdd(rs.getTekiyostrtdd());
                model.setSetTekiyoshrydd(rs.getTekiyoshrydd());
                model.setSetDlvryprice(rs.getDlvryprice().toString());
                model.setSetMochikaeriprice(rs.getMochikaeriprice().toString());
            }
        }
        resultList.add(model);
        return resultList.get(0);
    }
}
