package jp.co.aquaclara.accis.web.mobile.domain.service.f0501;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M013TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M017TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M046TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S06T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S10T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.Z1808B01Dao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.Z1808B01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S06T001TF0501W35MishimeEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S06T001TF0501W35MishimeSkyEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TF0501W35Entity;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.domain.dto.f0501.F0501W35SearchConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.f0501.F0501W35SearchResultDto;
import jp.co.aquaclara.accis.web.domain.dto.f0501.F0501W35SkyinfoDto;

/**
 * 得意先情報確認（得意先情報確認画面）（請求情報）
 * 
 * @author accisdev008
 */
@Service
public class F0501W35Service {

    @Autowired
    private S02M013TDao s02m013tdao;

    @Autowired
    private S02M017TDao s02m017tdao;

    @Autowired
    private S02M046TDao s02m046tdao;

    @Autowired
    private S06T001TDao s06t001tdao;

    @Autowired
    private S07T001TDao s07t001tdao;

    @Autowired
    private S10T001TDao s10t001tdao;

    @Autowired
    private Z1808B01Dao z1808b01dao;

    /**
     * 請求情報画面に表示する情報を検索する。
     * 
     * @param dto
     * @return
     */
    public F0501W35SearchResultDto search(F0501W35SearchConditionDto dto) {

        // Controllerへ返すインスタンスの生成
        F0501W35SearchResultDto result = new F0501W35SearchResultDto();

        // 得意先マスタの検索
        S02M013TEntityKey s02m013tkey = new S02M013TEntityKey();
        s02m013tkey.setApkihoncd(dto.getApkihoncd());
        s02m013tkey.setShopcd(dto.getShopcd());
        s02m013tkey.setTokuisakicd(dto.getTokuisakicd());
        S02M013TEntity resulttokuisaki = s02m013tdao.selectByPrimaryKey(s02m013tkey);

        // 請求先マスタの検索
        S02M017TEntityKey s02m017tkey = new S02M017TEntityKey();
        s02m017tkey.setApkihoncd(dto.getApkihoncd());
        s02m017tkey.setShopcd(dto.getShopcd());
        s02m017tkey.setSkysakicd(resulttokuisaki.getSkysakicd());
        S02M017TEntity resultskysaki = s02m017tdao.selectByPrimaryKey(s02m017tkey);

        // ショップマスタの検索
        S02M046TEntityKey s02m046tkey = new S02M046TEntityKey();
        s02m046tkey.setApkihoncd(dto.getApkihoncd());
        s02m046tkey.setShopcd(dto.getShopcd());
        S02M046TEntity resultshop = s02m046tdao.selectByPrimaryKey(s02m046tkey);

        // 請求情報（締め済）エリアの整形
        List<F0501W35SkyinfoDto> znskyinfodtolist =
            createZnskyinfo(dto, resulttokuisaki, resultskysaki);

        // 請求情報（未締め）エリアの整形
        F0501W35SkyinfoDto skyinfodto =
            createSkyinfo(dto, resulttokuisaki, resultskysaki, resultshop);

        result.setSkyinfodto(skyinfodto);
        result.setZnskyinfodto(znskyinfodtolist);

        return result;

    }

    /**
     * 請求情報（締め済）エリア情報
     * 
     * @param dto
     * @param resultokuisaki
     * @param resultskysaki
     * @return List<F0501W35SkyinfoDto>
     */
    private List<F0501W35SkyinfoDto> createZnskyinfo(F0501W35SearchConditionDto dto,
        S02M013TEntity resultokuisaki, S02M017TEntity resultskysaki) {

        // 過去請求情報を取得
        List<S07T001TF0501W35Entity> resultznskyinfo = s07t001tdao.selectByF0501W35Znskyinfo(
            dto.getApkihoncd(), dto.getShopcd(), resultokuisaki.getSkysakicd());

        List<F0501W35SkyinfoDto> znskyinfodtolist = new ArrayList<>();

        // 過去請求情報の件数分、以下の処理を繰り返し行う
        for (S07T001TF0501W35Entity e : resultznskyinfo) {

            F0501W35SkyinfoDto znskyinfodto = new F0501W35SkyinfoDto();

            // 現／新項目（税計算区分売上）を取得
            String taxkeisankbn = ItemUtil.getItem(e.getGenztaxkeisankbnuri(),
                e.getShintaxkeisankbnuri(), e.getTaxkeisankbnurikirikaedd(), e.getKonkaishimedd());

            // 請求先コード
            znskyinfodto.setSkysakicd(e.getSkysakicd());
            // 請求締日
            znskyinfodto.setSkyshimedd(e.getKonkaishimedd());
            // 前回請求額
            znskyinfodto.setZnkskykin(String.valueOf(e.getZnkkurikoshikin()));
            // 入金額
            znskyinfodto.setNyknkin(String.valueOf(e.getKonkainyknkin()));
            // 売上額
            znskyinfodto.setUrikin(uriagegaku(resultskysaki, e, znskyinfodto, taxkeisankbn));
            // 消費税
            znskyinfodto.setShohitax(String.valueOf(e.getKonkaishohitaxkin()));
            // 請求額
            znskyinfodto.setSkykin(String.valueOf(e.getKurikoshikin()));
            // 請求処理実施区分
            znskyinfodto.setSkyprcsjissikbn(e.getSkyprcsjissikbn());
            znskyinfodtolist.add(znskyinfodto);
        }
        return znskyinfodtolist;
    }

    /**
     * 請求情報（未締め）エリア情報
     * 
     * @param dto
     * @param resultokuisaki
     * @param resultskysaki
     * @return F0501W35SkyinfoDto
     */
    private F0501W35SkyinfoDto createSkyinfo(F0501W35SearchConditionDto dto,
        S02M013TEntity resulttokuisaki, S02M017TEntity resultskysaki, S02M046TEntity resultshop) {

        int nyknkin = 0; // 入金金額
        int urikin = 0; // 売上金額
        int shohitax = 0; // 消費税
        int znkskykin = 0; // 前回請求金額
        int urikintax = 0; // 売上金額課税

        F0501W35SkyinfoDto skyinfodto = new F0501W35SkyinfoDto();

        // 入金未締め請求データ取得
        nyknkin = s10t001tdao.selectByF0501W35nyknmishimesky(dto.getApkihoncd(), dto.getShopcd(),
            resulttokuisaki.getSkysakicd());

        // 運用日付がインボイス施行日未満の場合、以下の処理を行う。
        if (dto.getUnyoymd().compareTo(resultshop.getInvoicshikoymd()) <= -1) {

            // 売上未締め請求データ取得
            S06T001TF0501W35MishimeSkyEntity entity = s06t001tdao.selectByF0501W01UriMishimeSky(
                dto.getApkihoncd(), dto.getShopcd(), resulttokuisaki.getSkysakicd());

            // 売上金額を設定
            if (entity != null && !StringUtils.isEmpty(entity.getUrikin())) {
                urikin = Integer.parseInt(entity.getUrikin());
            }
            // 消費税を設定
            if (entity != null && !StringUtils.isEmpty(entity.getTaxkin())) {
                shohitax = Integer.parseInt(entity.getTaxkin());
            }

        }

        // 締日最大値取得
        String shimedydm = s07t001tdao.selectByF0501W35shimedydm(dto.getApkihoncd(),
            dto.getShopcd(), resulttokuisaki.getSkysakicd());

        // 繰越金額取得
        if (shimedydm != null) {
            znkskykin = s07t001tdao.selectByF0501W35kurikoshikin(dto.getApkihoncd(),
                dto.getShopcd(), resulttokuisaki.getSkysakicd(), shimedydm.substring(0, 6),
                shimedydm.substring(6));
        }

        // 新旧税計算区分を取得
        String taxkeisankbn = ItemUtil.getItem(resultshop.getGenztaxkeisankbnuri(),
            resultshop.getShintaxkeisankbnuri(), resultshop.getTaxkeisankbnurikirikaedd(),
            dto.getUnyoymd());

        // 運用日がインボイス施行日以降の場合、課税と課税軽減の税を取得
        if (dto.getUnyoymd().compareTo(resultshop.getInvoicshikoymd()) >= 1) {

            // 売上未締めデータ取得
            List<S06T001TF0501W35MishimeEntity> entityMishime =
                s06t001tdao.selectByF0501W01UriMishime(dto.getApkihoncd(), dto.getShopcd(),
                    resulttokuisaki.getSkysakicd());

            // 売上未締めデータ取得結果の件数分、以下の処理を繰り返し行う
            for (S06T001TF0501W35MishimeEntity e : entityMishime) {

                // 税区分が"1:課税"または、"4:課税軽減"の場合は以下の処理を行う
                if (CommonConstants.KBN_TAX_KAZEI.equals(e.getTaxkbn())
                    || CommonConstants.KBN_TAX_KEIGEN.equals(e.getTaxkbn())) {

                    // 売上金額を取得
                    urikintax = Integer.parseInt(e.getKin());

                    // 変数.売上金額課税が0でない場合、消費税額計算（バッチ処理）を呼び出して消費税額を計算
                    if (urikintax != 0) {
                        int result = taxCalc(dto, e, urikintax);
                        // 変数.消費税に戻り値の消費税額を加算
                        shohitax += result;
                    }
                }
                // 変数.売上金額に金額を加算する。
                urikin += Integer.parseInt(e.getKin());
            }
        }

        // 請求先コード
        skyinfodto.setSkysakicd(resulttokuisaki.getSkysakicd());
        // 前回請求金
        skyinfodto.setZnkskykin(String.valueOf(znkskykin));
        // 入金額
        skyinfodto.setNyknkin(String.valueOf(nyknkin));
        // 売上額
        skyinfodto.setUrikin(uriagegakuMishime(resultskysaki, resultshop, skyinfodto, taxkeisankbn,
            dto, urikin, shohitax));
        // 消費税
        skyinfodto.setShohitax(String.valueOf(shohitax));
        // 請求額（変数.前回請求金額 - 変数.入金金額 + 変数.売上金額 + 変数.消費税）
        skyinfodto.setSkykin(String.valueOf(znkskykin - nyknkin + urikin + shohitax));

        return skyinfodto;
    }

    /**
     * 売上額（締め済）を設定
     * 
     * @param resultskysaki
     * @param e
     * @param znskyinfodto
     * @param taxkeisankbn
     * @return String
     */
    private String uriagegaku(S02M017TEntity resultskysaki, S07T001TF0501W35Entity e,
        F0501W35SkyinfoDto znskyinfodto, String taxkeisankbn) {

        String uriagegaku = null;

        // 請求先管理区分が"2：卸"の場合、売上額に今回税抜き売上金額を設定する。
        if (CommonConstants.SKYSAKIKANRIKBN_2.equals(resultskysaki.getSkysakikanrikbn())) {
            // 今回税抜売上金額
            uriagegaku = String.valueOf(e.getKonkaitaxnukiurikin());
        } else {
            // 税計算区分売上が"1：内税"の場合、売上額に売上金額を設定する。
            if (CommonConstants.KBN_TAXKEISAN_IN.equals(taxkeisankbn)) {
                // 売上金額
                uriagegaku = String.valueOf(e.getUriagegaku());
            } else {
                // 今回税抜売上金額
                uriagegaku = String.valueOf(e.getKonkaitaxnukiurikin());
            }
        }
        return uriagegaku;
    }

    /**
     * 売上額（未締め）を設定
     * 
     * @param resultskysaki
     * @param resultshop
     * @param skyinfodto
     * @param taxkeisankbn
     * @param dto
     * @param urikin
     * @param shohitax
     * @return String
     */
    private String uriagegakuMishime(S02M017TEntity resultskysaki, S02M046TEntity resultshop,
        F0501W35SkyinfoDto skyinfodto, String taxkeisankbn, F0501W35SearchConditionDto dto,
        int urikin, int shohitax) {

        String uriagegakumishime = null;

        // 運用日付がインボイス施行日未満の場合、以下の処理を行う。
        if (dto.getUnyoymd().compareTo(resultshop.getInvoicshikoymd()) <= -1) {
            if (CommonConstants.SKYSAKIKANRIKBN_2.equals(resultskysaki.getSkysakikanrikbn())) {
                // 売上額を設定（売上金額）
                uriagegakumishime = String.valueOf(urikin);
            } else {
                if (CommonConstants.KBN_TAXKEISAN_IN.equals(taxkeisankbn)) {
                    // 売上額を設定（売上金額 + 消費税の合計）
                    uriagegakumishime = String.valueOf(urikin + shohitax);
                } else {
                    // 売上額を設定（売上金額）
                    uriagegakumishime = String.valueOf(urikin);
                }
            }
        } else {
            // 売上額を設定（売上金額）
            uriagegakumishime = String.valueOf(urikin);
        }
        return uriagegakumishime;
    }

    /**
     * 消費税額計算（バッチ処理呼び出し）
     * 
     * @param dto
     * @param e
     * @param urikintax
     * @return
     */
    private int taxCalc(F0501W35SearchConditionDto dto, S06T001TF0501W35MishimeEntity e,
        int urikintax) {

        Z1808B01Entity z1808b01Entity = new Z1808B01Entity();
        // 単体計算フラグ
        z1808b01Entity.setSimplefg(CommonConstants.FLG_ON);
        // 税設定区分
        z1808b01Entity.setTaxsetteikbn(CommonConstants.FLG_ON);
        // 税区分
        z1808b01Entity.setTaxkbn(CommonConstants.KBN_TAX_KAZEI);
        // 税計算区分
        z1808b01Entity.setTaxkeisankbn(null);
        // 切替日
        z1808b01Entity.setKirikaebi(e.getUridd());
        // AP基本コード
        z1808b01Entity.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        z1808b01Entity.setShopcd(dto.getShopcd());
        // 仕入れ先コード（未設定）
        z1808b01Entity.setSiiresakicd(null);
        // 数量（未設定）
        z1808b01Entity.setSuryo(null);
        // 税抜き金額（税抜き単価）
        z1808b01Entity.setKin(String.valueOf(urikintax));

        // 消費税計算（バッチ処理）呼び出し
        int result = z1808b01dao.syohizeiKeisan(z1808b01Entity);

        return result;
    }

}
