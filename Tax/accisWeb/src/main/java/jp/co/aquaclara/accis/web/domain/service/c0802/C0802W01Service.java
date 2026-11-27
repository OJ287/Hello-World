package jp.co.aquaclara.accis.web.domain.service.c0802;

import static jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02AcptRelationDto.checkPattern;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M006TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M007TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M008TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M013TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M017TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M044TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M046TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M066TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02V001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T002TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.Z1804B01Dao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M006TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M006TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M006TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M007TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M007TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.Z1804B01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M008TC0805W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M013TC0802W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M044TC0802W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M066TC0805W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02V001TC0802W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S04T001TC0802W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S04T002TC0805W01Entity;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.domain.dto.c0801.C0801W02AcptRelationDto;
import jp.co.aquaclara.accis.web.domain.dto.c0802.C0802W01RegistrationConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.c0802.C0802W01RegistrationMeisaiDto;
import jp.co.aquaclara.accis.web.domain.dto.c0802.C0802W01SearchConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.c0802.C0802W01SearchResultDto;
import jp.co.aquaclara.accis.web.domain.dto.c0802.C0802W01SearchResultMeisaiDto;
import jp.co.aquaclara.accis.web.domain.dto.c0805.C0805W01AcptRelationConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.common.LabelValueDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S02M006TDto;
import jp.co.aquaclara.accis.web.domain.service.business.TaxService;
import jp.co.aquaclara.accis.web.domain.service.c0801.w02.C0801W02ShnService;
import jp.co.aquaclara.accis.web.domain.service.master.S02M006TService;
import jp.co.aquaclara.accis.web.domain.service.x9901.StringUtil;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import jp.co.aquaclara.accis.web.exception.dto.FormField;

/**
 * C0802W01_受注明細予約登録サービスクラス.
 * 
 * @author accisdev041
 *
 */
@Service
public class C0802W01Service {

    @Autowired
    private S02M006TService s02m006tService;

    @Autowired
    private final MessageSource msg = null;

    /** 得意先マスタDAO **/
    @Autowired
    private S02M006TDao s02m006tDao;

    /** 基本商品マスタDAO **/
    @Autowired
    private S02M007TDao s02m007tDao;

    /** 展開マスタDAO **/
    @Autowired
    private S02M008TDao s02m008tDao;

    /** 得意先マスタDAO **/
    @Autowired
    private S02M013TDao s02m013tDao;

    /** 請求先マスタDAO **/
    @Autowired
    private S02M017TDao s02m017tDao;

    /** お気に入り商品マスタDAO **/
    @Autowired
    private S02M044TDao s02m044tDao;

    /** ショップマスタDAO **/
    @Autowired
    private S02M046TDao s02m046tDao;

    /** 得意先別セット商品単価マスタDAO **/
    @Autowired
    private S02M066TDao s02m066tDao;

    /** 商品マスタビューDAO **/
    @Autowired
    private S02V001TDao s02v001tDao;

    /** 受注配達テーブルDAO **/
    @Autowired
    private S04T001TDao s04t001tDao;

    /** 受注配達明細テーブルDAO **/
    @Autowired
    private S04T002TDao s04t002tDao;

    /** 採番DAO **/
    @Autowired
    private Z1804B01Dao z1804b01Dao;

    /** 商品明細消費税・金額計算Service **/
    @Autowired
    private TaxService taxService;

    /** 商品サービス. */
    @Autowired
    private C0801W02ShnService shnService;

    /**
     * 計上区分リスト設定
     * 
     * @param tokuisakikubun
     * @return List<LabelValueDto>
     */
    public List<LabelValueDto> getKeijyokubunList(String tokuisakikubun) {

        S02M006TDto dto = new S02M006TDto();
        dto.setApkihoncd(CommonConstants.APCD_000);
        dto.setShopcd(CommonConstants.SYS_SHOPCD_000);
        dto.setSetteino(CommonConstants.SYS_SETTEINO_009);
        dto.setRcrdkbn(CommonConstants.RECORD_KBN_M);
        dto.setYukokbn(CommonConstants.YUKOKBN_1);

        List<S02M006TDto> result = s02m006tService.search(dto, null, null);

        // 得意先区分に応じて不要な値を除外する
        switch (tokuisakikubun) {
            case "1": // 顧客
            case "5": // NA受託
            case "6": // 配達受託
                result.remove(8); // サブレンタルを除外
                result.remove(7); // 経費を除外
                result.remove(1); // キャンペーンを除外
                break;
            case "2": // ショップ
                result.remove(7); // 経費を除外
                result.remove(6); // 交換を除外
                result.remove(1); // キャンペーンを除外
                break;
            case "3": // 他AP
                result.remove(9); // レンタルを除外
                result.remove(8); // サブレンタルを除外
                result.remove(7); // 経費を除外
                result.remove(6); // 交換を除外
                result.remove(5); // メンテを除外
                result.remove(4); // 試飲を除外
                result.remove(3); // プロモーションを除外
                result.remove(1); // キャンペーンを除外
                break;
            case "4": // ACI本部
                result.remove(9); // レンタルを除外
                result.remove(8); // サブレンタルを除外
                result.remove(7); // 経費を除外
                result.remove(6); // 交換を除外
                result.remove(5); // メンテを除外
                result.remove(4); // 試飲を除外
                result.remove(3); // プロモーションを除外
                result.remove(1); // キャンペーンを除外
                break;
            case "9": // 経費
                result.remove(9); // レンタルを除外
                result.remove(8); // サブレンタルを除外
                result.remove(6); // 交換を除外
                result.remove(5); // メンテを除外
                result.remove(4); // 試飲を除外
                result.remove(3); // プロモーションを除外
                result.remove(2); // サービスを除外
                result.remove(1); // キャンペーンを除外
                result.remove(0); // 売上を除外
                break;
        }

        List<LabelValueDto> resultDto = new ArrayList<>();
        for (S02M006TDto d : result) {
            LabelValueDto retDto = new LabelValueDto(d.getSetteicd(), d.getMojisetteiatai());
            resultDto.add(retDto);
        }
        return resultDto;
    }

    /**
     * 画面表示用データ設定
     * 
     * @param conditionDto 条件
     * @return C0802W01SearchResultDto
     */
    public C0802W01SearchResultDto getInitDisplay(C0802W01SearchConditionDto conditionDto) {

        C0802W01SearchResultDto resultDto = new C0802W01SearchResultDto();

        // 受注伝票データ取得
        S04T001TC0802W01Entity s04t001tEntityIn = new S04T001TC0802W01Entity();
        s04t001tEntityIn.setApkihoncd(conditionDto.getApkihoncd());
        s04t001tEntityIn.setShopcd(conditionDto.getShopcd());
        s04t001tEntityIn.setTokuisakicd(conditionDto.getTokuisakicd());
        s04t001tEntityIn.setYoyakuflg(conditionDto.isYoyakuflg());
        if (!conditionDto.isYoyakuflg()) {
            // 未配達受注データ確認・修正からの遷移（伝票指定）時
            s04t001tEntityIn.setAcptdnpyno(conditionDto.getAcptdnpyno());
            s04t001tEntityIn.setEdano(Short.parseShort(conditionDto.getEdano()));
        }
        s04t001tEntityIn.setUnyodate(conditionDto.getUnyodate());
        // 伝票指定がない場合は伝票ステータスが「01：受注予約」のデータを取得する
        List<S04T001TC0802W01Entity> s04t001tEntityOutList =
            s04t001tDao.selectByC0802W01getJuchuDenpyo(s04t001tEntityIn);

        if (s04t001tEntityOutList.size() == 1) {
            // 受注予約登録時点では配達予定日をNULLで設定しているため、当処理にて運用日付を設定する。
            s04t001tEntityOutList.get(0).setDlvryytidd(conditionDto.getUnyodate());
            // 処理区分
            resultDto.setShorikbn(C0802W01SearchResultDto.PROCESS_UPDATE);
            // 受注伝票番号
            resultDto.setAcptdnpyno(s04t001tEntityOutList.get(0).getAcptdnpyno());
            // 枝番号
            resultDto.setEdano(String.valueOf(s04t001tEntityOutList.get(0).getEdano()));

            // 受注予約明細データ取得
            List<C0802W01SearchResultMeisaiDto> searchResultMeisaiDtoList =
                getJuchuMeisai(conditionDto, s04t001tEntityOutList.get(0));
            resultDto.setSearchResultMeisaiDtoList(searchResultMeisaiDtoList);

            // 印刷メモ１～３設定（更新の場合は、受注配達テーブルの印刷メモ１～３を設定）
            // 印刷メモ1
            resultDto.setPrntmemon1(s04t001tEntityOutList.get(0).getPrntmemon1());
            // 印刷メモ2
            resultDto.setPrntmemon2(s04t001tEntityOutList.get(0).getPrntmemon2());
            // 印刷メモ3
            resultDto.setPrntmemon3(s04t001tEntityOutList.get(0).getPrntmemon3());

        } else {
            // 処理区分
            resultDto.setShorikbn(C0802W01SearchResultDto.PROCESS_NEW);
            // 受注伝票番号
            resultDto.setAcptdnpyno("");
            // 枝番号
            resultDto.setEdano("");
        }

        // 得意先配達データ取得
        S02M013TC0802W01Entity s02m013tEntityIn = new S02M013TC0802W01Entity();
        s02m013tEntityIn.setApkihoncd(conditionDto.getApkihoncd());
        s02m013tEntityIn.setShopcd(conditionDto.getShopcd());
        s02m013tEntityIn.setTokuisakicd(conditionDto.getTokuisakicd());
        if (s04t001tEntityOutList.size() == 0
            || Strings.isEmpty(s04t001tEntityOutList.get(0).getDlvryytidd())) {
            s02m013tEntityIn.setUnyodate(conditionDto.getUnyodate());
        } else {
            s02m013tEntityIn.setUnyodate(s04t001tEntityOutList.get(0).getDlvryytidd());
        }
        List<S02M013TC0802W01Entity> s02m013tEntityOutList =
            s02m013tDao.selectByC0802W01getTokuisakiDlvryyData(s02m013tEntityIn);

        resultDto.setTokuisakidata("0");
        if (s02m013tEntityOutList.size() > 0) {
            resultDto.setTokuisakidata("1");
            S02M013TC0802W01Entity tokuisakiDlvry = s02m013tEntityOutList.get(0);
            // 配達拠点コード
            resultDto.setDlvrykyotencd(tokuisakiDlvry.getDlvrykankatsukyotencd());
            // 配達拠点名称
            resultDto.setDlvrykyotennm(tokuisakiDlvry.getDlvrykyotennm());
            // 配達エリアコード
            resultDto.setDlvryareacd(tokuisakiDlvry.getDlvryareacd());
            // 配達エリア名称
            resultDto.setDlvryareanm(tokuisakiDlvry.getDlvryareanm());
            // 配達管轄AP基本コード
            resultDto.setDlvrykankatsuapkihoncd(tokuisakiDlvry.getDlvrykankatsuapkihoncd());
            // 配達管轄ショップコード
            resultDto.setDlvrykankatsushopcd(tokuisakiDlvry.getDlvrykankatsushopcd());
            // 配達会社コード
            resultDto.setDlvrycmpnycd(tokuisakiDlvry.getDlvrycmpnycd());
            // 配達会社名称
            String dlvrycmpnynm = tokuisakiDlvry.getDlvrycmpnynm();
            if (CommonConstants.KBN_DLVRY_JISHA.equals(tokuisakiDlvry.getDlvrykbn())
                || CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(tokuisakiDlvry.getDlvrykbn())) {
                dlvrycmpnynm = s02m006tDao.findConfig(CommonConstants.SYS_SETTEINO_029,
                    tokuisakiDlvry.getDlvrykbn());
            }
            resultDto.setDlvrycmpnynm(dlvrycmpnynm);
            // 配達パターン
            resultDto.setDlvrypatern(tokuisakiDlvry.getDlvrypatern());
            // 配達担当者コード
            resultDto.setDlvrytnshacd(tokuisakiDlvry.getDlvrytnshacd());
            // 配達担当者名
            resultDto.setDlvrytnshamei(tokuisakiDlvry.getDlvrytnshamei());
            // データ作成元区分名称
            resultDto.setDatsksmotokbnmei(s02m006tDao.findConfig(CommonConstants.SYS_SETTEINO_093,
                CommonConstants.KBN_SAKUSEIMOTO_JUCHUYOYAKU));
            resultDto.setFzaiokikanoflg(tokuisakiDlvry.getFzaiokikanoflg());
            // 納品先情報１
            resultDto.setNohinsakijohonmei1(tokuisakiDlvry.getNohinsakijohonmei1());
            // 納品先情報２
            resultDto.setNohinsakijohonmei2(tokuisakiDlvry.getNohinsakijohonmei2());
            // 得意先区分
            resultDto.setTokuisakikbn(tokuisakiDlvry.getTokuisakikbn());
            // 得意先状態
            resultDto.setTokuisakijotai(tokuisakiDlvry.getTokuisakijotai());
            // 印刷メモ１～３設定（新規の場合は、得意先マスタの印刷メモ１～３を設定）
            if (C0802W01SearchResultDto.PROCESS_NEW.equals(resultDto.getShorikbn())) {
                // 印刷メモ1
                resultDto.setPrntmemon1(tokuisakiDlvry.getPrntmemon1());
                // 印刷メモ2
                resultDto.setPrntmemon2(tokuisakiDlvry.getPrntmemon2());
                // 印刷メモ3
                resultDto.setPrntmemon3(tokuisakiDlvry.getPrntmemon3());
            }
            // 配達順
            resultDto.setDlvryjun(tokuisakiDlvry.getDlvryjun());
            // 受注受付経路区分
            resultDto.setAcptrcptkeirokbn(CommonConstants.SETTEICD_093_03);
            // ショップマスタ検索：単独TBL検索
            S02M046TEntityExample shopCondition = new S02M046TEntityExample();
            shopCondition.createCriteria().andApkihoncdEqualTo(conditionDto.getApkihoncd())
                .andShopcdEqualTo(conditionDto.getShopcd());
            List<S02M046TEntity> s02m046tOutList = s02m046tDao.selectByExample(shopCondition);
            // 事業所管理区分
            resultDto.setJigyoshokanrikbn(s02m046tOutList.get(0).getJigyoshokanri());
            // 請求先コード
            resultDto.setSkysakicd(tokuisakiDlvry.getSkysakicd());
            // 受託配達顧客区分
            resultDto.setJutakudlvrycstkbn(tokuisakiDlvry.getJutakudlvrycstkbn());
            // 委託相手ＡＰ基本コード
            resultDto.setItakuaiteapkihoncd(tokuisakiDlvry.getItakuaiteapkihoncd());
            // 委託相手ショップコード
            resultDto.setItakuaiteshopcd(tokuisakiDlvry.getItakuaiteshopcd());
            // 配達区分
            resultDto.setDlvrykbn(tokuisakiDlvry.getDlvrykbn());
        }
        // 価格情報
        resultDto.setPriceinfo(getPriceinfo(conditionDto));
        return resultDto;
    }

    /**
     * 商品リスト取得
     * 
     * @param conditionDto 条件
     * @return C0802W01SearchResultDto
     */
    public C0802W01SearchResultDto getRenkeiShnList(C0802W01SearchConditionDto conditionDto) {

        C0802W01SearchResultDto resultDto = new C0802W01SearchResultDto();

        // 商品データ取得（商品コード検索）
        S02V001TC0802W01Entity s02V001tEntityIn = new S02V001TC0802W01Entity();
        s02V001tEntityIn.setApkihoncd(conditionDto.getApkihoncd());
        s02V001tEntityIn.setShopcd(conditionDto.getShopcd());
        s02V001tEntityIn.setTokuisakicd(conditionDto.getTokuisakicd());
        s02V001tEntityIn.setDlvryytidd(conditionDto.getUnyodate());
        s02V001tEntityIn.setShncd(conditionDto.getRenkeishncd());

        List<S02V001TC0802W01Entity> s02V001tEntityOutList =
            s02v001tDao.selectByC0802W01(s02V001tEntityIn);

        List<C0802W01SearchResultMeisaiDto> meisaiDtoList = new ArrayList<>();
        Integer gyono = 0;
        for (S02V001TC0802W01Entity entity : s02V001tEntityOutList) {
            gyono++;
            C0802W01SearchResultMeisaiDto meisaiDto = new C0802W01SearchResultMeisaiDto();
            // 商品コード
            meisaiDto.setShncd(entity.getShncd());
            // 商品名称略称
            meisaiDto.setShnnmryakusho(entity.getShnnmryakusho());
            // 色
            meisaiDto.setIro(entity.getIro());
            // サイズ
            meisaiDto.setSz(entity.getSz());
            // 型式
            meisaiDto.setKatasiki(entity.getKatasiki());
            // 計上区分
            String keijokbn = getKeijokbn(conditionDto, entity.getLctgrycd(), entity.getMctgrycd());
            meisaiDto.setKeijokbn(keijokbn);
            // 売上取扱単位
            meisaiDto.setUritrtskitani(entity.getUritrtskitani());
            // 単位
            meisaiDto.setUritani(entity.getUritani());
            // 入数
            meisaiDto.setUriirisu(entity.getUriirisu());
            // 標準価格
            if (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(conditionDto.getDlvrykbn())) {
                meisaiDto.setHyojununt(getCurrentValue(entity.getGenzmochikaeriprice(),
                    entity.getShinmochikaeriprice(), entity.getHyojunpricekirikaedd(),
                    conditionDto.getUnyodate()));
            } else {
                meisaiDto.setHyojununt(
                    getCurrentValue(entity.getGenzhyojunprice(), entity.getShinhyojunprice(),
                        entity.getHyojunpricekirikaedd(), conditionDto.getUnyodate()));
            }

            // 商品マスタビュー(単独TBL検索）
            S02V001TEntityExample S02V001TExample = new S02V001TEntityExample();
            S02V001TExample.createCriteria().andApkihoncdEqualTo(conditionDto.getApkihoncd())
                .andShopcdEqualTo(conditionDto.getShopcd()).andShncdEqualTo(entity.getShncd());
            List<S02V001TEntity> S02V001TOutList = s02v001tDao.selectByExample(S02V001TExample);

            // 販売価格
            String acptunt = "";
            if (CommonConstants.KBN_KEIJO_KEIHI.equals(keijokbn)) {
                meisaiDto.setAcptunt(entity.getZaikount());
            } else if (CommonConstants.KBN_KEIJO_URIAGE.equals(keijokbn)) {
                meisaiDto.setAcptunt(entity.getZaikount());
                if (CommonConstants.FLG_ON.equals(entity.getRknkindtokubetsuuntflg())) {
                    // 得意先価格のセット
                    if (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(conditionDto.getDlvrykbn())) {
                        if (!Strings.isEmpty(entity.getMochikaeriprice())
                            && !Strings.isEmpty(entity.getTekiyostrtdd())
                            && !Strings.isEmpty(entity.getTekiyostrtdd())) {
                            if (conditionDto.getUnyodate().compareTo(entity.getTekiyostrtdd()) >= 0
                                && entity.getTekiyostrtdd()
                                    .compareTo(entity.getTekiyostrtdd()) >= 0) {
                                acptunt = entity.getMochikaeriprice();
                            }
                        }
                    } else {
                        if (!Strings.isEmpty(entity.getDlvryprice())
                            && !Strings.isEmpty(entity.getTekiyostrtdd())
                            && !Strings.isEmpty(entity.getTekiyostrtdd())) {
                            if (conditionDto.getUnyodate().compareTo(entity.getTekiyostrtdd()) >= 0
                                && entity.getTekiyostrtdd()
                                    .compareTo(entity.getTekiyostrtdd()) >= 0) {
                                acptunt = entity.getDlvryprice();
                            }
                        }
                    }

                    if (Strings.isEmpty(acptunt)) {
                        // セット価格のセット
                        if (CommonConstants.KBN_DLVRY_MOCHIKAERI
                            .equals(conditionDto.getDlvrykbn())) {
                            if (!Strings.isEmpty(entity.getSet_mochikaeriprice())
                                && !Strings.isEmpty(entity.getSet_tekiyostrtdd())
                                && !Strings.isEmpty(entity.getSet_tekiyostrtdd())) {
                                if (conditionDto.getUnyodate()
                                    .compareTo(entity.getSet_tekiyostrtdd()) >= 0
                                    && entity.getSet_tekiyostrtdd()
                                        .compareTo(entity.getSet_tekiyostrtdd()) >= 0) {
                                    acptunt = entity.getSet_mochikaeriprice();
                                }
                            }
                        } else {
                            if (!Strings.isEmpty(entity.getSet_dlvryprice())
                                && !Strings.isEmpty(entity.getSet_tekiyostrtdd())
                                && !Strings.isEmpty(entity.getSet_tekiyostrtdd())) {
                                if (conditionDto.getUnyodate()
                                    .compareTo(entity.getSet_tekiyostrtdd()) >= 0
                                    && entity.getSet_tekiyostrtdd()
                                        .compareTo(entity.getSet_tekiyostrtdd()) >= 0) {
                                    acptunt = entity.getSet_dlvryprice();
                                }
                            }
                        }
                    }
                }

                // ショップ価格のセット
                if (Strings.isEmpty(acptunt)) {
                    if (CommonConstants.KBN_PRICE_HYOJUN.equals(entity.getPricekbn())) {
                        if (CommonConstants.KBN_DLVRY_MOCHIKAERI
                            .equals(conditionDto.getDlvrykbn())) {
                            acptunt = getCurrentValue(
                                String.valueOf(S02V001TOutList.get(0).getGenzmochikaeriprice()),
                                String.valueOf(S02V001TOutList.get(0).getShinmochikaeriprice()),
                                S02V001TOutList.get(0).getHyojunpricekirikaedd(),
                                conditionDto.getUnyodate());
                        } else {
                            acptunt = getCurrentValue(
                                String.valueOf(S02V001TOutList.get(0).getGenzhyojunprice()),
                                String.valueOf(S02V001TOutList.get(0).getShinhyojunprice()),
                                S02V001TOutList.get(0).getHyojunpricekirikaedd(),
                                conditionDto.getUnyodate());
                        }
                    } else {
                        if (CommonConstants.KBN_DLVRY_MOCHIKAERI
                            .equals(conditionDto.getDlvrykbn())) {
                            acptunt = getCurrentValue(
                                String.valueOf(S02V001TOutList.get(0).getGenzopnmochikaeriprice()),
                                String.valueOf(S02V001TOutList.get(0).getShinopnmochikaeriprice()),
                                S02V001TOutList.get(0).getOpnpricekirikaedd(),
                                conditionDto.getUnyodate());
                        } else {
                            acptunt = getCurrentValue(
                                String.valueOf(S02V001TOutList.get(0).getGenzopnprice()),
                                String.valueOf(S02V001TOutList.get(0).getShinopnprice()),
                                S02V001TOutList.get(0).getOpnpricekirikaedd(),
                                conditionDto.getUnyodate());
                        }
                    }
                }
                meisaiDto.setAcptunt(acptunt);
            } else {
                meisaiDto.setAcptunt("0");
            }

            if ("null".equals(meisaiDto.getAcptunt())) {
                meisaiDto.setAcptunt("0");
            }

            // 金額
            meisaiDto.setAcptkin((long) 0);
            // 明細備考
            meisaiDto.setMeisaibiko(entity.getMeisaibiko());
            // 大分類コード
            meisaiDto.setLctgrycd(entity.getLctgrycd());
            // 中分類コード
            meisaiDto.setMctgrycd(entity.getMctgrycd());
            // 小分類コード
            meisaiDto.setSctgrycd(entity.getSctgrycd());
            // 行番号（明細）
            meisaiDto.setGyono(gyono);
            // 更新処理フラグ
            meisaiDto.setUpdateflg("0");
            // 受注伝票明細番号
            meisaiDto.setAcptdnpymeisaino(0);
            // 受注伝票明細枝番号
            meisaiDto.setAcptdnpymeisaiedano((short) 0);
            // 税区分
            meisaiDto.setTaxkbn(entity.getTaxkbn());
            // 明細区分
            meisaiDto.setMeisaikbn(CommonConstants.KBN_MEISAI_SHOHIN);
            // 入力明細フラグ
            meisaiDto.setNyuryokumeisaiflg(CommonConstants.FLG_ON);
            // 単品明細フラグ
            if (CommonConstants.FLG_ON.equals(entity.getStkbn())) {
                meisaiDto.setTanpinmeisaiflg(CommonConstants.FLG_OFF);
                // セット商品コード
                meisaiDto.setStshncd(entity.getShncd());
            } else {
                meisaiDto.setTanpinmeisaiflg(CommonConstants.FLG_ON);
            }
            // 価格区分
            meisaiDto.setPricekbn(entity.getPricekbn());
            // 得意先区分
            meisaiDto.setTokuisakikbn(conditionDto.getTokuisakikbn());

            // 標準価格切替日、現標準価格、新標準価格、現標準持ち帰り価格、新標準持ち帰り価格
            meisaiDto.setHyojunpricekirikaedd(entity.getHyojunpricekirikaedd());
            meisaiDto.setGenzhyojunprice(entity.getGenzhyojunprice());
            meisaiDto.setShinhyojunprice(entity.getShinhyojunprice());
            meisaiDto.setGenzmochikaeriprice(entity.getGenzmochikaeriprice());
            meisaiDto.setShinmochikaeriprice(entity.getShinmochikaeriprice());

            // ショップ切替日、現ショップ価格、新ショップ価格、現ショップ持ち帰り価格、新ショップ持ち帰り価格
            if (CommonConstants.KBN_PRICE_HYOJUN.equals(entity.getPricekbn())) {
                meisaiDto.setShoppricekirikaedd(entity.getHyojunpricekirikaedd());
                meisaiDto.setGenzshopprice(entity.getGenzhyojunprice());
                meisaiDto.setShinshopprice(entity.getShinhyojunprice());
                meisaiDto.setGenzshopmochikaeriprice(entity.getGenzmochikaeriprice());
                meisaiDto.setShinshopmochikaeriprice(entity.getShinmochikaeriprice());
            } else {
                meisaiDto.setShoppricekirikaedd(entity.getOpnpricekirikaedd());
                meisaiDto.setGenzshopprice(entity.getGenzopnprice());
                meisaiDto.setShinshopprice(entity.getShinopnprice());
                meisaiDto.setGenzshopmochikaeriprice(entity.getGenzopnmochikaeriprice());
                meisaiDto.setShinshopmochikaeriprice(entity.getShinopnmochikaeriprice());
            }

            // 販促開始日、販促終了日、販促配達価格、販促持ち帰り価格
            if (!CommonConstants.KBN_SAKUSEIMOTO_JUCHUYOYAKU.equals(entity.getDatsksmotokbn())
                && (CommonConstants.FLG_ON.equals(entity.getHansokukbn()))) {
                meisaiDto.setHansokustrtdd(entity.getHansokustrtdd());
                meisaiDto.setHansokushrydd(entity.getHansokushrydd());
                meisaiDto.setHansokudlvryprice(entity.getHansokudlvryprice());
                meisaiDto.setHansokumochikaeriprice(entity.getHansokumochikaeriprice());
            }

            // 得意先価格開始日、得意先価格終了日、得意先価格、得意先持ち帰り価格
            if (CommonConstants.FLG_ON.equals(entity.getRknkindtokubetsuuntflg())) {
                if (CommonConstants.FLG_ON.equals(entity.getStkbn())) {
                    meisaiDto.setTtpricestrtdd(entity.getSet_tekiyostrtdd());
                    meisaiDto.setTtpriceshrydd(entity.getSet_tekiyoshrydd());
                    meisaiDto.setTtprice(entity.getSet_dlvryprice());
                    meisaiDto.setTtmochikaeriprice(entity.getSet_mochikaeriprice());
                } else {
                    meisaiDto.setTtpricestrtdd(entity.getTekiyostrtdd());
                    meisaiDto.setTtpriceshrydd(entity.getTekiyoshrydd());
                    meisaiDto.setTtprice(entity.getDlvryprice());
                    meisaiDto.setTtmochikaeriprice(entity.getMochikaeriprice());
                }
            }

            if (Strings.isEmpty(meisaiDto.getTtpricestrtdd())) {
                meisaiDto.setTtprice(meisaiDto.getAcptunt());
                meisaiDto.setTtmochikaeriprice(meisaiDto.getAcptunt());
            }

            meisaiDtoList.add(meisaiDto);
        }

        resultDto.setSearchResultMeisaiDtoList(meisaiDtoList);

        return resultDto;
    }

    /**
     * 計上区分取得
     * 
     * @param conditionDto 条件
     * @param lctgrycd 大分類コード
     * @param mctgrycd 中分類コード
     * @return C0802W01SearchResultDto
     */
    private String getKeijokbn(C0802W01SearchConditionDto conditionDto, String lctgrycd,
        String mctgrycd) {

        // 「サーバー関連用」の商品大分類コード
        String keijokbn = "";
        String tokuisakikbn = conditionDto.getTokuisakikbn();
        String srvlctgrycd =
            getSystemMasterValue(CommonConstants.SYS_SETTEINO_096, CommonConstants.SETTEICD_096_4);
        // 「サーバー関連本体」の商品大分類コード
        String srvmctgrycd =
            getSystemMasterValue(CommonConstants.SYS_SETTEINO_134, CommonConstants.SETTEICD_134_41);

        keijokbn = CommonConstants.KBN_KEIJO_URIAGE;
        if (CommonConstants.KBN_TOKUISAKI_KEIHI.equals(tokuisakikbn)) {
            keijokbn = CommonConstants.KBN_KEIJO_KEIHI;
        }

        if (srvlctgrycd.equals(lctgrycd) && srvmctgrycd.equals(mctgrycd)) {
            if (CommonConstants.KBN_TOKUISAKI_SHOP.equals(tokuisakikbn)) {
                keijokbn = CommonConstants.KBN_KEIJO_SUBRENTAL;
            } else {
                keijokbn = CommonConstants.KBN_KEIJO_URIAGE;
            }
        }

        // 取得した明細の計上区分が利用可能か判定する
        if (!checkKeijokbn(tokuisakikbn, keijokbn)) {
            // 利用不可ならデフォルトの計上区分を返却
            return getDefaultKeijokbn(tokuisakikbn, keijokbn, lctgrycd, mctgrycd);
        }

        return keijokbn;
    }

    /**
     * お気に入り商品表示
     * 
     * @param conditionDto 条件
     * @return C0802W01SearchResultDto
     */
    public C0802W01SearchResultDto getOkiniList(C0802W01SearchConditionDto conditionDto) {

        C0802W01SearchResultDto resultDto = new C0802W01SearchResultDto();

        // 受注伝票明細リスト取得
        S02M044TC0802W01Entity s02M044tEntityIn = new S02M044TC0802W01Entity();
        s02M044tEntityIn.setApkihoncd(conditionDto.getApkihoncd());
        s02M044tEntityIn.setShopcd(conditionDto.getShopcd());
        s02M044tEntityIn.setTokuisakicd(conditionDto.getTokuisakicd());
        s02M044tEntityIn.setDlvryytidd(conditionDto.getUnyodate());
        s02M044tEntityIn.setType(CommonConstants.DSPLYKBN_2);

        List<S02M044TC0802W01Entity> s02M044tEntityOutList =
            s02m044tDao.selectByC0802W01(s02M044tEntityIn);

        List<C0802W01SearchResultMeisaiDto> meisaiDtoList = new ArrayList<>();
        Integer gyono = 0;
        for (S02M044TC0802W01Entity entity : s02M044tEntityOutList) {
            gyono++;
            C0802W01SearchResultMeisaiDto meisaiDto = new C0802W01SearchResultMeisaiDto();
            // 商品コード
            meisaiDto.setShncd(entity.getShncd());
            // 商品名称略称
            meisaiDto.setShnnmryakusho(entity.getShnnmryakusho());
            // 色
            meisaiDto.setIro(entity.getIro());
            // サイズ
            meisaiDto.setSz(entity.getSz());
            // 型式
            meisaiDto.setKatasiki(entity.getKatasiki());
            // 計上区分
            String keijokbn = entity.getKeijokbn();
            // 取得した明細の計上区分が利用可能か判定する
            if (!checkKeijokbn(conditionDto.getTokuisakikbn(), keijokbn)) {
                // 利用不可ならデフォルトの計上区分を返却
                keijokbn = getDefaultKeijokbn(conditionDto.getTokuisakikbn(), keijokbn,
                    entity.getLctgrycd(), entity.getMctgrycd());
            }
            meisaiDto.setKeijokbn(keijokbn);
            // 売上取扱単位
            meisaiDto.setUritrtskitani(entity.getUritrtskitani());
            // 単位
            meisaiDto.setUritani(entity.getUritani());
            // 入数
            meisaiDto.setUriirisu(entity.getUriirisu());
            // 標準価格
            if (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(conditionDto.getDlvrykbn())) {
                meisaiDto.setHyojununt(getCurrentValue(entity.getGenzmochikaeriprice(),
                    entity.getShinmochikaeriprice(), entity.getHyojunpricekirikaedd(),
                    conditionDto.getUnyodate()));
            } else {
                meisaiDto.setHyojununt(
                    getCurrentValue(entity.getGenzhyojunprice(), entity.getShinhyojunprice(),
                        entity.getHyojunpricekirikaedd(), conditionDto.getUnyodate()));
            }

            // 商品マスタビュー(単独TBL検索）
            S02V001TEntityExample S02V001TExample = new S02V001TEntityExample();
            S02V001TExample.createCriteria().andApkihoncdEqualTo(conditionDto.getApkihoncd())
                .andShopcdEqualTo(conditionDto.getShopcd()).andShncdEqualTo(entity.getShncd());
            List<S02V001TEntity> S02V001TOutList = s02v001tDao.selectByExample(S02V001TExample);

            // 販売価格
            String acptunt = "";
            if (CommonConstants.KBN_KEIJO_KEIHI.equals(keijokbn)) {
                meisaiDto.setAcptunt(entity.getZaikount());
            } else if (CommonConstants.KBN_KEIJO_URIAGE.equals(keijokbn)) {
                meisaiDto.setAcptunt(entity.getZaikount());

                if (CommonConstants.FLG_ON.equals(entity.getRknkindtokubetsuuntflg())) {

                    // 得意先価格のセット
                    if (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(conditionDto.getDlvrykbn())) {
                        if (!Strings.isEmpty(entity.getMochikaeriprice())
                            && !Strings.isEmpty(entity.getTekiyostrtdd())
                            && !Strings.isEmpty(entity.getTekiyostrtdd())) {
                            if (conditionDto.getUnyodate().compareTo(entity.getTekiyostrtdd()) >= 0
                                && entity.getTekiyostrtdd()
                                    .compareTo(entity.getTekiyostrtdd()) >= 0) {
                                acptunt = entity.getMochikaeriprice();
                            }
                        }
                    } else {
                        if (!Strings.isEmpty(entity.getDlvryprice())
                            && !Strings.isEmpty(entity.getTekiyostrtdd())
                            && !Strings.isEmpty(entity.getTekiyostrtdd())) {
                            if (conditionDto.getUnyodate().compareTo(entity.getTekiyostrtdd()) >= 0
                                && entity.getTekiyostrtdd()
                                    .compareTo(entity.getTekiyostrtdd()) >= 0) {
                                acptunt = entity.getDlvryprice();
                            }
                        }
                    }

                    if (Strings.isEmpty(acptunt)) {
                        // セット価格のセット
                        if (CommonConstants.KBN_DLVRY_MOCHIKAERI
                            .equals(conditionDto.getDlvrykbn())) {
                            if (!Strings.isEmpty(entity.getSet_mochikaeriprice())
                                && !Strings.isEmpty(entity.getSet_tekiyostrtdd())
                                && !Strings.isEmpty(entity.getSet_tekiyostrtdd())) {
                                if (conditionDto.getUnyodate()
                                    .compareTo(entity.getSet_tekiyostrtdd()) >= 0
                                    && entity.getSet_tekiyostrtdd()
                                        .compareTo(entity.getSet_tekiyostrtdd()) >= 0) {
                                    acptunt = entity.getSet_mochikaeriprice();
                                }
                            }
                        } else {
                            if (!Strings.isEmpty(entity.getSet_dlvryprice())
                                && !Strings.isEmpty(entity.getSet_tekiyostrtdd())
                                && !Strings.isEmpty(entity.getSet_tekiyostrtdd())) {
                                if (conditionDto.getUnyodate()
                                    .compareTo(entity.getSet_tekiyostrtdd()) >= 0
                                    && entity.getSet_tekiyostrtdd()
                                        .compareTo(entity.getSet_tekiyostrtdd()) >= 0) {
                                    acptunt = entity.getSet_dlvryprice();
                                }
                            }
                        }
                    }
                }

                // ショップ価格のセット
                if (Strings.isEmpty(acptunt)) {
                    if (CommonConstants.KBN_PRICE_HYOJUN.equals(entity.getPricekbn())) {
                        if (CommonConstants.KBN_DLVRY_MOCHIKAERI
                            .equals(conditionDto.getDlvrykbn())) {
                            acptunt = getCurrentValue(
                                String.valueOf(S02V001TOutList.get(0).getGenzmochikaeriprice()),
                                String.valueOf(S02V001TOutList.get(0).getShinmochikaeriprice()),
                                S02V001TOutList.get(0).getHyojunpricekirikaedd(),
                                conditionDto.getUnyodate());
                        } else {
                            acptunt = getCurrentValue(
                                String.valueOf(S02V001TOutList.get(0).getGenzhyojunprice()),
                                String.valueOf(S02V001TOutList.get(0).getShinhyojunprice()),
                                S02V001TOutList.get(0).getHyojunpricekirikaedd(),
                                conditionDto.getUnyodate());
                        }
                    } else {
                        if (CommonConstants.KBN_DLVRY_MOCHIKAERI
                            .equals(conditionDto.getDlvrykbn())) {
                            acptunt = getCurrentValue(
                                String.valueOf(S02V001TOutList.get(0).getGenzopnmochikaeriprice()),
                                String.valueOf(S02V001TOutList.get(0).getShinopnmochikaeriprice()),
                                S02V001TOutList.get(0).getOpnpricekirikaedd(),
                                conditionDto.getUnyodate());
                        } else {
                            acptunt = getCurrentValue(
                                String.valueOf(S02V001TOutList.get(0).getGenzopnprice()),
                                String.valueOf(S02V001TOutList.get(0).getShinopnprice()),
                                S02V001TOutList.get(0).getOpnpricekirikaedd(),
                                conditionDto.getUnyodate());
                        }
                    }
                }
                meisaiDto.setAcptunt(acptunt);
            } else {
                meisaiDto.setAcptunt("0");
            }
            if ("null".equals(meisaiDto.getAcptunt())) {
                meisaiDto.setAcptunt("0");
            }
            // 金額
            meisaiDto.setAcptkin((long) 0);
            // 明細備考
            meisaiDto.setMeisaibiko(entity.getMeisaibiko());
            // 大分類コード
            meisaiDto.setLctgrycd(entity.getLctgrycd());
            // 中分類コード
            meisaiDto.setMctgrycd(entity.getMctgrycd());
            // 小分類コード
            meisaiDto.setSctgrycd(entity.getSctgrycd());
            // 行番号（明細）
            meisaiDto.setGyono(gyono);
            // 更新処理フラグ
            meisaiDto.setUpdateflg("0");
            // 受注伝票明細番号
            meisaiDto.setAcptdnpymeisaino(0);
            // 受注伝票明細枝番号
            meisaiDto.setAcptdnpymeisaiedano((short) 0);
            // 税区分
            meisaiDto.setTaxkbn(entity.getTaxkbn());
            // 明細区分
            meisaiDto.setMeisaikbn(CommonConstants.KBN_MEISAI_SHOHIN);
            // 入力明細フラグ
            meisaiDto.setNyuryokumeisaiflg(CommonConstants.FLG_ON);
            // 単品明細フラグ
            if (CommonConstants.FLG_ON.equals(entity.getStkbn())) {
                meisaiDto.setTanpinmeisaiflg(CommonConstants.FLG_OFF);
                // セット商品コード
                meisaiDto.setStshncd(entity.getShncd());
            } else {
                meisaiDto.setTanpinmeisaiflg(CommonConstants.FLG_ON);
            }
            // 価格区分
            meisaiDto.setPricekbn(entity.getPricekbn());
            // 得意先区分
            meisaiDto.setTokuisakikbn(conditionDto.getTokuisakikbn());

            // 標準価格切替日、現標準価格、新標準価格、現標準持ち帰り価格、新標準持ち帰り価格
            meisaiDto.setHyojunpricekirikaedd(entity.getHyojunpricekirikaedd());
            meisaiDto.setGenzhyojunprice(entity.getGenzhyojunprice());
            meisaiDto.setShinhyojunprice(entity.getShinhyojunprice());
            meisaiDto.setGenzmochikaeriprice(entity.getGenzmochikaeriprice());
            meisaiDto.setShinmochikaeriprice(entity.getShinmochikaeriprice());

            // ショップ切替日、現ショップ価格、新ショップ価格、現ショップ持ち帰り価格、新ショップ持ち帰り価格
            if (CommonConstants.KBN_PRICE_HYOJUN.equals(entity.getPricekbn())) {
                meisaiDto.setShoppricekirikaedd(entity.getHyojunpricekirikaedd());
                meisaiDto.setGenzshopprice(entity.getGenzhyojunprice());
                meisaiDto.setShinshopprice(entity.getShinhyojunprice());
                meisaiDto.setGenzshopmochikaeriprice(entity.getGenzmochikaeriprice());
                meisaiDto.setShinshopmochikaeriprice(entity.getShinmochikaeriprice());
            } else {
                meisaiDto.setShoppricekirikaedd(entity.getOpnpricekirikaedd());
                meisaiDto.setGenzshopprice(entity.getGenzopnprice());
                meisaiDto.setShinshopprice(entity.getShinopnprice());
                meisaiDto.setGenzshopmochikaeriprice(entity.getGenzopnmochikaeriprice());
                meisaiDto.setShinshopmochikaeriprice(entity.getShinopnmochikaeriprice());
            }

            // 販促開始日、販促終了日、販促配達価格、販促持ち帰り価格
            if (!CommonConstants.KBN_SAKUSEIMOTO_JUCHUYOYAKU.equals(entity.getDatsksmotokbn())
                && (CommonConstants.FLG_ON.equals(entity.getHansokukbn()))) {
                meisaiDto.setHansokustrtdd(entity.getHansokustrtdd());
                meisaiDto.setHansokushrydd(entity.getHansokushrydd());
                meisaiDto.setHansokudlvryprice(entity.getHansokudlvryprice());
                meisaiDto.setHansokumochikaeriprice(entity.getHansokumochikaeriprice());
            }

            // 得意先価格開始日、得意先価格終了日、得意先価格、得意先持ち帰り価格
            if (CommonConstants.FLG_ON.equals(entity.getRknkindtokubetsuuntflg())) {
                if (CommonConstants.FLG_ON.equals(entity.getStkbn())) {
                    meisaiDto.setTtpricestrtdd(entity.getSet_tekiyostrtdd());
                    meisaiDto.setTtpriceshrydd(entity.getSet_tekiyoshrydd());
                    meisaiDto.setTtprice(entity.getSet_dlvryprice());
                    meisaiDto.setTtmochikaeriprice(entity.getSet_mochikaeriprice());
                } else {
                    meisaiDto.setTtpricestrtdd(entity.getTekiyostrtdd());
                    meisaiDto.setTtpriceshrydd(entity.getTekiyoshrydd());
                    meisaiDto.setTtprice(entity.getDlvryprice());
                    meisaiDto.setTtmochikaeriprice(entity.getMochikaeriprice());
                }
            }

            if (Strings.isEmpty(meisaiDto.getTtpricestrtdd())) {
                meisaiDto.setTtprice(meisaiDto.getAcptunt());
                meisaiDto.setTtmochikaeriprice(meisaiDto.getAcptunt());
            }

            meisaiDtoList.add(meisaiDto);
        }

        resultDto.setSearchResultMeisaiDtoList(meisaiDtoList);

        return resultDto;
    }

    /**
     * 画面表示用データ設定
     * 
     * @param conditionDto 条件
     * @param s04t001tEntity
     * @return List<C0802W01SearchResultMeisaiDto>
     */

    // 受注予約明細データ取得
    private List<C0802W01SearchResultMeisaiDto> getJuchuMeisai(
        C0802W01SearchConditionDto conditionDto, S04T001TC0802W01Entity s04t001tEntity) {

        S04T002TC0805W01Entity s04t002tc0805w01entityIn = new S04T002TC0805W01Entity();
        s04t002tc0805w01entityIn.setApkihoncd(conditionDto.getApkihoncd());
        s04t002tc0805w01entityIn.setShopcd(conditionDto.getShopcd());
        s04t002tc0805w01entityIn.setAcptdnpyno(s04t001tEntity.getAcptdnpyno());
        s04t002tc0805w01entityIn.setEdano(s04t001tEntity.getEdano());
        if (Strings.isEmpty(s04t001tEntity.getAcptdd())) {
            s04t002tc0805w01entityIn.setKakakugetbasedd(s04t001tEntity.getDlvryytidd());
        } else {
            s04t002tc0805w01entityIn.setKakakugetbasedd(s04t001tEntity.getAcptdd());
        }
        List<S04T002TC0805W01Entity> s04t002tEntityOutList =
            s04t002tDao.selectByC0805W01getJuchuMeisai(s04t002tc0805w01entityIn);

        for (int i = s04t002tEntityOutList.size() - 1; 0 <= i; i--) {
            if (CommonConstants.NYURYOKUMEISAIFLG_0
                .equals(s04t002tEntityOutList.get(i).getNyuryokumeisaiflg())) {
                s04t002tEntityOutList.remove(i);
            }
        }

        List<C0802W01SearchResultMeisaiDto> meisaiDtoList = new ArrayList<>();
        Integer gyono = 0;
        for (S04T002TC0805W01Entity entity : s04t002tEntityOutList) {
            gyono++;
            C0802W01SearchResultMeisaiDto meisaiDto = new C0802W01SearchResultMeisaiDto();
            // 商品コード
            meisaiDto.setShncd(entity.getShncd());
            // 商品名称略称
            meisaiDto.setShnnmryakusho(entity.getShnnmryakusho());
            // 色
            meisaiDto.setIro(entity.getIro());
            // サイズ
            meisaiDto.setSz(entity.getSz());
            // 型式
            meisaiDto.setKatasiki(entity.getKatasiki());
            // 計上区分
            meisaiDto.setKeijokbn(entity.getKeijokbn());
            // 数量
            meisaiDto.setAcptsuryo(entity.getAcptsuryo());
            // 売上取扱単位
            meisaiDto.setUritrtskitani(entity.getUritrtskitani());
            // 売上入数単位
            meisaiDto.setUritani(entity.getUritani());
            // 入数
            meisaiDto.setUriirisu(entity.getUriirisu());
            // 総数
            meisaiDto.setSokazu(entity.getSokazu());
            // 標準価格
            if (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(s04t001tEntity.getDlvrykbn())) {
                meisaiDto.setHyojununt(getCurrentValue(entity.getGenzmochikaeriprice(),
                    entity.getShinmochikaeriprice(), entity.getHyojunpricekirikaedd(),
                    s04t001tEntity.getUnyodate()));
            } else {
                meisaiDto.setHyojununt(
                    getCurrentValue(entity.getGenzhyojunprice(), entity.getShinhyojunprice(),
                        entity.getHyojunpricekirikaedd(), s04t001tEntity.getUnyodate()));
            }

            // 販売価格
            meisaiDto.setAcptunt(String.valueOf(entity.getAcptunt()));
            // 金額
            meisaiDto.setAcptkin(entity.getAcptkin());
            // 総回数
            meisaiDto.setSoukaisu(entity.getSoukaisu());
            // 残回数
            meisaiDto.setZankaisu(entity.getZankaisu());
            // 明細備考
            meisaiDto.setMeisaibiko(entity.getMeisaibiko());
            // 大分類コード
            meisaiDto.setLctgrycd(entity.getLctgrycd());
            // 中分類コード
            meisaiDto.setMctgrycd(entity.getMctgrycd());
            // 小分類コード
            meisaiDto.setSctgrycd(entity.getSctgrycd());
            // 行番号（明細）
            meisaiDto.setGyono(gyono);
            // 更新処理フラグ
            meisaiDto.setUpdateflg("1");
            // 受注伝票明細番号
            meisaiDto.setAcptdnpymeisaino(entity.getAcptdnpymeisaino());
            // 受注伝票明細枝番号
            meisaiDto.setAcptdnpymeisaiedano(entity.getAcptdnpymeisaiedano());
            // 税区分
            meisaiDto.setTaxkbn(entity.getTaxkbn());
            // 有償区分
            meisaiDto.setYusyokbn(entity.getYusyokbn());
            // 明細区分
            meisaiDto.setMeisaikbn(entity.getMeisaikbn());
            // 入力明細フラグ
            meisaiDto.setNyuryokumeisaiflg(entity.getNyuryokumeisaiflg());
            // 単品明細フラグ
            meisaiDto.setTanpinmeisaiflg(entity.getTanpinmeisaiflg());
            // セット商品コード
            meisaiDto.setStshncd(entity.getStshncd());
            // 受注単価消費税金額
            meisaiDto.setAcptuntshohitaxkin(entity.getAcptuntshohitaxkin());
            // 受注消費税金額
            meisaiDto.setAcptshohitaxkin(entity.getAcptshohitaxkin());
            // 価格区分
            meisaiDto.setPricekbn(entity.getPricekbn());
            // 得意先区分
            meisaiDto.setTokuisakikbn(conditionDto.getTokuisakikbn());

            // 標準価格切替日、現標準価格、新標準価格、現標準持ち帰り価格、新標準持ち帰り価格
            meisaiDto.setHyojunpricekirikaedd(entity.getHyojunpricekirikaedd());
            meisaiDto.setGenzhyojunprice(entity.getGenzhyojunprice());
            meisaiDto.setShinhyojunprice(entity.getShinhyojunprice());
            meisaiDto.setGenzmochikaeriprice(entity.getGenzmochikaeriprice());
            meisaiDto.setShinmochikaeriprice(entity.getShinmochikaeriprice());

            // ショップ切替日、現ショップ価格、新ショップ価格、現ショップ持ち帰り価格、新ショップ持ち帰り価格
            if (CommonConstants.KBN_PRICE_HYOJUN.equals(entity.getPricekbn())) {
                meisaiDto.setShoppricekirikaedd(entity.getHyojunpricekirikaedd());
                meisaiDto.setGenzshopprice(entity.getGenzhyojunprice());
                meisaiDto.setShinshopprice(entity.getShinhyojunprice());
                meisaiDto.setGenzshopmochikaeriprice(entity.getGenzmochikaeriprice());
                meisaiDto.setShinshopmochikaeriprice(entity.getShinmochikaeriprice());
            } else {
                meisaiDto.setShoppricekirikaedd(entity.getOpnpricekirikaedd());
                meisaiDto.setGenzshopprice(entity.getGenzopnprice());
                meisaiDto.setShinshopprice(entity.getShinopnprice());
                meisaiDto.setGenzshopmochikaeriprice(entity.getGenzopnmochikaeriprice());
                meisaiDto.setShinshopmochikaeriprice(entity.getShinopnmochikaeriprice());
            }

            // 販促開始日、販促終了日、販促配達価格、販促持ち帰り価格
            if (!CommonConstants.KBN_SAKUSEIMOTO_JUCHUYOYAKU.equals(entity.getDatsksmotokbn())
                && (CommonConstants.FLG_ON.equals(entity.getHansokukbn()))) {
                meisaiDto.setHansokustrtdd(entity.getHansokustrtdd());
                meisaiDto.setHansokushrydd(entity.getHansokushrydd());
                meisaiDto.setHansokudlvryprice(entity.getHansokudlvryprice());
                meisaiDto.setHansokumochikaeriprice(entity.getHansokumochikaeriprice());
            }

            // 得意先価格開始日、得意先価格終了日、得意先価格、得意先持ち帰り価格
            if (CommonConstants.FLG_ON.equals(entity.getRknkindtokubetsuuntflg())) {
                if (CommonConstants.FLG_ON.equals(entity.getStkbn())) {
                    meisaiDto.setTtpricestrtdd(entity.getSet_tekiyostrtdd());
                    meisaiDto.setTtpriceshrydd(entity.getSet_tekiyoshrydd());
                    meisaiDto.setTtprice(entity.getSet_dlvryprice());
                    meisaiDto.setTtmochikaeriprice(entity.getSet_mochikaeriprice());
                } else {
                    meisaiDto.setTtpricestrtdd(entity.getTekiyostrtdd());
                    meisaiDto.setTtpriceshrydd(entity.getTekiyoshrydd());
                    meisaiDto.setTtprice(entity.getDlvryprice());
                    meisaiDto.setTtmochikaeriprice(entity.getMochikaeriprice());
                }
            }

            meisaiDtoList.add(meisaiDto);
        }

        return meisaiDtoList;

    }

    /**
     * 事前チェック
     * 
     * @param conditionDto 登録データ
     * @return メッセージ
     */
    public List<String> checkParamPre(C0802W01RegistrationConditionDto conditionDto) {

        List<String> messages = new LinkedList<>();
        // 請求先マスタから有効な請求先を取得
        Optional<S02M017TEntity> skysaki = getSkysaki(conditionDto.getApkihoncd(),
            conditionDto.getShopcd(), conditionDto.getSkysakicd());

        if (!Objects.isNull(conditionDto.getRegistrationMeisaiDtoList())) {
            for (C0802W01RegistrationMeisaiDto meisaiDto : conditionDto
                .getRegistrationMeisaiDtoList()) {
                // 相関チェックNo7
                if (CommonConstants.KBN_TOKUISAKIJOTAI_KARIKAIIN
                    .equals(conditionDto.getTokuisakijotai())) {
                    // 得意先状態が「仮会員」
                    if (CommonConstants.KBN_KEIJO_URIAGE.equals(meisaiDto.getKeijokbn())
                        || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(meisaiDto.getKeijokbn())) {
                        // 計上区分が有料（売上またはキャンペーン）
                        if (!Strings.isEmpty(meisaiDto.getAcptunt())
                            && !Objects.equals("0", meisaiDto.getAcptunt())) {
                            // 受注単価が0ではない
                            if (skysaki.isPresent()) {
                                // 有効な請求先が存在している場合はワーニング
                                Object[] args = {meisaiDto.getShncd()};
                                messages.add(
                                    msg.getMessage(MessageID.W141.toString(), args, Locale.JAPAN));
                            }
                        }

                    }
                }
            }
        }
        return messages;
    }

    /**
     * 更新
     * 
     * @param conditionDto 登録データ
     */
    @Transactional
    public void registration(C0802W01RegistrationConditionDto conditionDto) {

        // 相関チェック
        // 請求先マスタから有効な請求先を取得
        Optional<S02M017TEntity> skysaki = getSkysaki(conditionDto.getApkihoncd(),
            conditionDto.getShopcd(), conditionDto.getSkysakicd());
        if (!Objects.isNull(conditionDto.getRegistrationMeisaiDtoList())) {
            for (C0802W01RegistrationMeisaiDto meisaiDto : conditionDto
                .getRegistrationMeisaiDtoList()) {
                // 相関チェックNo6
                if (CommonConstants.KBN_TOKUISAKIJOTAI_KARIKAIIN
                    .equals(conditionDto.getTokuisakijotai())) {
                    // 得意先状態が「仮会員」
                    if (CommonConstants.KBN_KEIJO_URIAGE.equals(meisaiDto.getKeijokbn())
                        || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(meisaiDto.getKeijokbn())) {
                        // 計上区分が有料（売上またはキャンペーン）
                        if (skysaki.isEmpty()) {
                            // 有効な請求先が存在しない場合はエラー
                            throw BusinessExceptionFactory.validationFailed(MessageID.W140);
                        }
                    }
                }
            }
        }
        // 相関チェックNo8
        if ((CommonConstants.KBN_JUTAKUDLVTOKUI_NAJUTAKU.equals(conditionDto.getTokuisakikbn())
            && CommonConstants.JUTAKUDLVRYCSTKBN_5.equals(conditionDto.getJutakudlvrycstkbn()))
            || (CommonConstants.KBN_JUTAKUDLVTOKUI_DLVRYJUTAKU
                .equals(conditionDto.getTokuisakikbn())
                && CommonConstants.JUTAKUDLVRYCSTKBN_6
                    .equals(conditionDto.getJutakudlvrycstkbn()))) {
            throw BusinessExceptionFactory.validationFailed(MessageID.W255, "受託会社で受注予約の登録");

        }

        if (!Objects.isNull(conditionDto.getRegistrationMeisaiDtoList())) {
            int rowNum = 0;

            // 受注データを元に受注関係DTOを生成
            C0801W02AcptRelationDto relation = createRelation(conditionDto);

            for (C0802W01RegistrationMeisaiDto meisaiDto : conditionDto
                .getRegistrationMeisaiDtoList()) {
                rowNum++;

                // 商品行、入力行、数量が入力された行以外はスキップする
                if (!CommonConstants.KBN_MEISAI_SHOHIN.equals(meisaiDto.getMeisaikbn())
                    || !CommonConstants.FLG_ON.equals(meisaiDto.getNyuryokumeisaiflg())
                    || StringUtils.isEmpty(meisaiDto.getAcptsuryo())
                    || BigDecimal.ZERO.equals(meisaiDto.getAcptsuryo())) {
                    continue;
                }

                // 相関チェックNo3
                if (meisaiDto.getSoukaisu() != null && meisaiDto.getZankaisu() != null) {
                    if (meisaiDto.getSoukaisu() < meisaiDto.getZankaisu()) {
                        List<FormField> formFieldList = new ArrayList<>();
                        FormField formField = new FormField(
                            String.format("detailBeanList[%d].soukaisumei", rowNum - 1), "総回数");
                        formFieldList.add(formField);
                        formField = new FormField(
                            String.format("detailBeanList[%d].zankaisumei", rowNum - 1), "残回数");
                        formFieldList.add(formField);

                        throw BusinessExceptionFactory.validationFailed(formFieldList,
                            MessageID.W254);
                    }
                }

                // チェック対象の受注配達明細の内容を反映した受注関係DTOを生成
                C0801W02AcptRelationDto detailRelation = relation.toBuilder()
                    .lctgrycd(meisaiDto.getLctgrycd()).mctgrycd(meisaiDto.getMctgrycd())
                    .shncd(meisaiDto.getShncd()).keijokbn(meisaiDto.getKeijokbn())
                    .yukokbn(CommonConstants.YUKOKBN_1).build();
                // 相関チェックNo6
                if (!checkRelations(detailRelation)) {
                    throw BusinessExceptionFactory.validationFailed(
                        new FormField(String.format("detailBeanList[%d].keijokbn", rowNum - 1),
                            "計上区分"),
                        MessageID.W123, "計上区分");
                }

                // 商品マスタビュー(単独TBL検索）
                S02V001TEntityExample s02v001tEntityExample = new S02V001TEntityExample();
                s02v001tEntityExample.createCriteria()
                    .andApkihoncdEqualTo(conditionDto.getApkihoncd())
                    .andShopcdEqualTo(conditionDto.getShopcd())
                    .andShncdEqualTo(meisaiDto.getShncd());
                List<S02V001TEntity> s02v001tOutList =
                    s02v001tDao.selectByExample(s02v001tEntityExample);

                // 相関チェックNo9
                if (!CommonConstants.YUKOKBN_1.equals(s02v001tOutList.get(0).getShopyukokbn())) {
                    throw BusinessExceptionFactory.validationFailed(MessageID.W123, "商品コード");

                }

                // 相関チェックNo10
                if (CommonConstants.KBN_TORIATSUKAI_SHIIRE
                    .equals(s02v001tOutList.get(0).getTrtskikbn())) {
                    throw BusinessExceptionFactory.validationFailed(MessageID.W124, "仕入のみの商品");
                }

                // 相関チェックNo11
                if (!(conditionDto.getUnyodate()
                    .compareTo(s02v001tOutList.get(0).getHanbaistrtdd()) >= 0
                    && s02v001tOutList.get(0).getHanbaishrydd()
                        .compareTo(conditionDto.getUnyodate()) >= 0)) {
                    throw BusinessExceptionFactory.validationFailed(MessageID.W130, "販売");
                }

                if (!Strings.isEmpty(conditionDto.getDlvrykankatsuapkihoncd())
                    && !Strings.isEmpty(conditionDto.getDlvrykankatsushopcd())) {

                    if (!(conditionDto.getApkihoncd()
                        .equals(conditionDto.getDlvrykankatsuapkihoncd())
                        && conditionDto.getShopcd()
                            .equals(conditionDto.getDlvrykankatsushopcd()))) {

                        // 相関チェックNo12
                        if (!ObjectUtils.isEmpty(s02v001tOutList)
                            && !CommonConstants.KBN_SHNZOKUSEI_HONBU
                                .equals(s02v001tOutList.get(0).getShnzokusei())) {
                            throw BusinessExceptionFactory.validationFailed(MessageID.W132, "委託配達",
                                "AP商品の受注");
                        } else {
                            // 受託側の商品マスタビュー情報取得
                            S02V001TEntityExample jtkS02V001TExample = new S02V001TEntityExample();
                            jtkS02V001TExample.createCriteria()
                                .andApkihoncdEqualTo(conditionDto.getDlvrykankatsuapkihoncd())
                                .andShopcdEqualTo(conditionDto.getDlvrykankatsushopcd())
                                .andShncdEqualTo(meisaiDto.getShncd());
                            List<S02V001TEntity> jtkS02v001tOutList =
                                s02v001tDao.selectByExample(jtkS02V001TExample);

                            // 相関チェックNo13
                            if (ObjectUtils.isEmpty(jtkS02v001tOutList)
                                || !CommonConstants.YUKOKBN_1
                                    .equals(jtkS02v001tOutList.get(0).getShopyukokbn())) {
                                throw BusinessExceptionFactory.validationFailed(MessageID.W132,
                                    "委託配達", "受託側で取り込んでいない商品の受注");
                            }

                            // 相関チェックNo14
                            if (!(conditionDto.getUnyodate()
                                .compareTo(jtkS02v001tOutList.get(0).getHanbaistrtdd()) >= 0
                                && jtkS02v001tOutList.get(0).getHanbaishrydd()
                                    .compareTo(conditionDto.getUnyodate()) >= 0)) {
                                throw BusinessExceptionFactory.validationFailed(MessageID.W132,
                                    "受託配達", "受託側で販売期間外の商品の受注");
                            }
                        }
                    }
                }

                // 相関チェックNo15
                if (CommonConstants.KBN_TOKUISAKI_KEIHI.equals(conditionDto.getTokuisakikbn())
                    && CommonConstants.TANPINMEISAIFLG_0.equals(meisaiDto.getTanpinmeisaiflg())) {
                    throw BusinessExceptionFactory.validationFailed(MessageID.W124, "セット商品");
                }
            }

            // ショップマスタ検索：単独TBL検索
            S02M046TEntityExample S02M046TExample = new S02M046TEntityExample();
            S02M046TExample.createCriteria().andApkihoncdEqualTo(conditionDto.getApkihoncd())
                .andShopcdEqualTo(conditionDto.getShopcd());
            List<S02M046TEntity> s02m046tOutList = s02m046tDao.selectByExample(S02M046TExample);

            // 税計算区分取得
            String taxkeisankbn = getCurrentValue(s02m046tOutList.get(0).getGenztaxkeisankbnuri(),
                s02m046tOutList.get(0).getShintaxkeisankbnuri(),
                s02m046tOutList.get(0).getTaxkeisankbnurikirikaedd(), conditionDto.getUnyodate());
            if (CommonConstants.KBN_TOKUISAKI_SHOP.equals(conditionDto.getTokuisakikbn())
                || CommonConstants.KBN_TOKUISAKI_AP.equals(conditionDto.getTokuisakikbn())
                || CommonConstants.KBN_TOKUISAKI_ACI.equals(conditionDto.getTokuisakikbn())
                || CommonConstants.KBN_TOKUISAKI_NAJUTAKU.equals(conditionDto.getTokuisakikbn())
                || CommonConstants.KBN_TOKUISAKI_DLVRYJUTAKU
                    .equals(conditionDto.getTokuisakikbn())) {
                taxkeisankbn = CommonConstants.KBN_TAXKEISAN_EX;
            }
            conditionDto.setTaxkeisankbn(taxkeisankbn);

            // 受注金額・消費税の集計
            long acptkin = 0;
            long acptshohitaxkin = 0;
            long acptippannukikin = 0;
            long acptippantaxkin = 0;
            long acptkeigennukikin = 0;
            long acptkeigentaxkin = 0;
            long acpthikazeiuriikin = 0;
            long acptfukazeiuriikin = 0;

            boolean invoicshikoflg = false;
            List<C0802W01RegistrationMeisaiDto> deploymentMeisaiDtoList = new ArrayList<>();

            if (!C0802W01RegistrationConditionDto.PROCESS_DELETE
                .equals(conditionDto.getShorikbn())) {
                if (!Objects.isNull(conditionDto.getRegistrationMeisaiDtoList())) {

                    // 数量0データの削除
                    for (int i =
                        conditionDto.getRegistrationMeisaiDtoList().size() - 1; 0 <= i; i--) {
                        if (Objects.isNull(
                            conditionDto.getRegistrationMeisaiDtoList().get(i).getAcptsuryo())
                            || BigDecimal.ZERO.equals(conditionDto.getRegistrationMeisaiDtoList()
                                .get(i).getAcptsuryo())) {
                            conditionDto.getRegistrationMeisaiDtoList().remove(i);
                        }
                    }

                    // 受注伝票明細番号のフリ直し
                    Integer newAcptdnpymeisaino = 0;
                    for (int j = 0; j < conditionDto.getRegistrationMeisaiDtoList().size(); j++) {
                        newAcptdnpymeisaino++;
                        conditionDto.getRegistrationMeisaiDtoList().get(j)
                            .setAcptdnpymeisaino(newAcptdnpymeisaino);
                    }

                    // 消費税計算（明細）
                    for (C0802W01RegistrationMeisaiDto meisaiDto : conditionDto
                        .getRegistrationMeisaiDtoList()) {
                        String priceList[] =
                            taxService.getPriceList(String.valueOf(meisaiDto.getAcptunt()),
                                String.valueOf(meisaiDto.getAcptsuryo()), meisaiDto.getTaxkbn(),
                                conditionDto.getTokuisakikbn(), conditionDto.getUnyodate(),
                                s02m046tOutList.get(0));
                        // 受注単価消費税金額
                        meisaiDto.setAcptuntshohitaxkin(Long.parseLong(priceList[0]));
                        // 受注消費税金額
                        meisaiDto.setAcptshohitaxkin(Long.parseLong(priceList[1]));
                    }

                    // 明細リスト展開処理
                    deploymentMeisaiDtoList = deploymentSetShohin(conditionDto,
                        conditionDto.getRegistrationMeisaiDtoList(), s02m046tOutList.get(0));

                    // 消費税計算
                    for (C0802W01RegistrationMeisaiDto meisaiDto : deploymentMeisaiDtoList) {

                        if (CommonConstants.FLG_ON.equals(meisaiDto.getNyuryokumeisaiflg())) {

                            // 受注金額
                            acptkin = acptkin + meisaiDto.getAcptkin();
                            // 受注消費税金額
                            acptshohitaxkin = acptshohitaxkin + meisaiDto.getAcptshohitaxkin();
                            // 受注一般税抜金額
                            if (CommonConstants.KBN_TAX_KAZEI.equals(meisaiDto.getTaxkbn())) {
                                acptippannukikin = acptippannukikin + meisaiDto.getAcptkin();
                            }
                            // 受注軽減税抜金額
                            if (CommonConstants.KBN_TAX_KEIGEN.equals(meisaiDto.getTaxkbn())) {
                                acptkeigennukikin = acptkeigennukikin + meisaiDto.getAcptkin();
                            }
                            // 受注非課税金額
                            if (CommonConstants.KBN_TAX_HIKAZEI.equals(meisaiDto.getTaxkbn())) {
                                acpthikazeiuriikin = acpthikazeiuriikin + meisaiDto.getAcptkin();
                            }
                            // 受注不課税売上金額
                            if (CommonConstants.KBN_TAX_HUKAZEI.equals(meisaiDto.getTaxkbn())) {
                                acptfukazeiuriikin = acptfukazeiuriikin + meisaiDto.getAcptkin();
                            }
                        }
                    }

                    // 消費税金額計算を行う
                    String ippanPriceList[] = taxService.getPriceList(
                        String.valueOf(acptippannukikin), "1", "1", conditionDto.getTokuisakikbn(),
                        conditionDto.getUnyodate(), s02m046tOutList.get(0));
                    acptippantaxkin = Long.parseLong(ippanPriceList[1]);

                    String keigenPriceList[] = taxService.getPriceList(
                        String.valueOf(acptkeigennukikin), "1", "4", conditionDto.getTokuisakikbn(),
                        conditionDto.getUnyodate(), s02m046tOutList.get(0));
                    acptkeigentaxkin = Long.parseLong(keigenPriceList[1]);

                    // インボイス施行日判定
                    if (conditionDto.getUnyodate()
                        .compareTo(s02m046tOutList.get(0).getInvoicshikoymd()) >= 0) {
                        acptkin = acptippannukikin + acptkeigennukikin + acpthikazeiuriikin
                            + acptfukazeiuriikin;
                        acptshohitaxkin = acptippantaxkin + acptkeigentaxkin;
                        invoicshikoflg = true;
                    }
                }

                conditionDto.setAcptkin(acptkin);
                conditionDto.setAcptshohitaxkin(acptshohitaxkin);
                conditionDto.setAcptippannukikin(acptippannukikin);
                conditionDto.setAcptippantaxkin(acptippantaxkin);
                conditionDto.setAcptkeigennukikin(acptkeigennukikin);
                conditionDto.setAcptkeigentaxkin(acptkeigentaxkin);
                conditionDto.setAcpthikazeiuriikin(acpthikazeiuriikin);
                conditionDto.setAcptfukazeiuriikin(acptfukazeiuriikin);
            }

            if (C0802W01RegistrationConditionDto.PROCESS_NEW.equals(conditionDto.getShorikbn())) {

                // ACCIS2023-6950
                // 処理モードが新規の場合、登録済の受注予約データの存在チェックを行う
                S04T001TEntityExample condition = new S04T001TEntityExample();
                condition.createCriteria() // 検索条件
                    .andApkihoncdEqualTo(conditionDto.getApkihoncd()) // AP基本コード
                    .andShopcdEqualTo(conditionDto.getShopcd()) // ショップコード
                    .andTokuisakicdEqualTo(conditionDto.getTokuisakicd()) // 得意先コード
                    .andDnpystsEqualTo(CommonConstants.DNPYSTS_01) // 伝票ステータス：01（受注予約）
                    .andYukokbnEqualTo(CommonConstants.YUKOKBN_1); // 有効区分：1（有効）
                List<S04T001TEntity> existsReservedList = s04t001tDao.selectByExample(condition);
                if (existsReservedList.size() != 0) {
                    // 1つの得意先に対して有効な受注予約データは1件までとするため、データが存在した場合はエラー
                    throw BusinessExceptionFactory.validationFailed(MessageID.W260,
                        "登録済の受注予約があります、もう一度得意先を確定し表示内容を更新してください。");
                }
                // ***** 新規 *****
                // 伝票番号採番
                Z1804B01Entity z1804bEntity = new Z1804B01Entity();
                z1804bEntity.setType(50);
                z1804bEntity.setApkihoncd(conditionDto.getApkihoncd());
                z1804bEntity.setShopcd(conditionDto.getShopcd());
                z1804bEntity.setCount(1);
                z1804b01Dao.getNewNo(z1804bEntity);
                conditionDto.setAcptdnpyno(z1804bEntity.getResultno());

                // 受注配達登録処理
                try {
                    // INSERT実行
                    s04t001tDao
                        .insertSelective(createInsertS04t001tEntity(conditionDto, skysaki.get()));
                } catch (DuplicateKeyException e) {
                    // 一意制約違反の場合、エラー
                    throw BusinessExceptionFactory.validationFailed(MessageID.W089);
                } catch (RuntimeException re) {
                    throw BusinessExceptionFactory.validationFailed(MessageID.E002);
                }

                // 受注配達明細登録処理
                if (!Objects.isNull(deploymentMeisaiDtoList)) {
                    for (C0802W01RegistrationMeisaiDto meisaiDto : deploymentMeisaiDtoList) {
                        try {
                            // INSERT実行
                            s04t002tDao.insertSelective(createInsertS04t002tEntity(conditionDto,
                                meisaiDto, invoicshikoflg));
                        } catch (DuplicateKeyException e) {
                            // 一意制約違反の場合、エラー
                            throw BusinessExceptionFactory.validationFailed(MessageID.W089);
                        } catch (RuntimeException re) {
                            throw BusinessExceptionFactory.validationFailed(MessageID.E002);
                        }
                    }
                }
            } else if (C0802W01RegistrationConditionDto.PROCESS_UPDATE
                .equals(conditionDto.getShorikbn())) {
                // ***** 修正 *****
                int result = 0;
                // 受注配達更新処理
                try {
                    // UPDATE実行
                    result =
                        s04t001tDao.updateByExample(createUpdateSetS04t001tEntity(conditionDto),
                            createUpdateConditionS04t001tEntity(conditionDto));
                } catch (RuntimeException e) {
                    throw BusinessExceptionFactory.validationFailed(MessageID.E002);
                }

                if (result == 0) {
                    // 更新件数が0(排他制御)の場合、エラー
                    throw BusinessExceptionFactory.validationFailed(MessageID.W090);
                }

                // 受注配達明細更新処理
                // 登録済レコード削除
                try {
                    // DELETE実行
                    s04t002tDao.deleteByExample(createUpdateConditionS04t002tEntity(conditionDto));
                } catch (RuntimeException e) {
                    throw BusinessExceptionFactory.validationFailed(MessageID.E002);
                }

                result = 0;
                if (!Objects.isNull(deploymentMeisaiDtoList)) {
                    for (C0802W01RegistrationMeisaiDto meisaiDto : deploymentMeisaiDtoList) {
                        try {
                            // INSERT実行
                            s04t002tDao.insertSelective(createInsertS04t002tEntity(conditionDto,
                                meisaiDto, invoicshikoflg));
                        } catch (DuplicateKeyException e) {
                            // 一意制約違反の場合、エラー
                            throw BusinessExceptionFactory.validationFailed(MessageID.W089);
                        } catch (RuntimeException re) {
                            throw BusinessExceptionFactory.validationFailed(MessageID.E002);
                        }
                    }
                }

            } else if (C0802W01RegistrationConditionDto.PROCESS_DELETE
                .equals(conditionDto.getShorikbn())) {
                // ***** 削除 *****
                // 受注配達更新処理
                int result = 0;
                try {
                    // UPDATE実行
                    result = s04t001tDao.updateByExampleSelective(
                        createDeleteSetS04t001tEntity(conditionDto),
                        createUpdateConditionS04t001tEntity(conditionDto));
                } catch (RuntimeException e) {
                    throw BusinessExceptionFactory.validationFailed(MessageID.E002);
                }

                if (result == 0) {
                    // 更新件数が0(排他制御)の場合、エラー
                    throw BusinessExceptionFactory.validationFailed(MessageID.W090);
                }

            }
        }

    }

    /**
     * 受注伝票の内容を元に受注関係DTOを生成する
     * 
     * @param conditionDto 受注予約登録DTO
     * @return 受注関係DTO
     */
    private C0801W02AcptRelationDto createRelation(C0802W01RegistrationConditionDto conditionDto) {
        // 水の小分類コードや得意先状態、状態ステータスは計上区分のチェック処理で必要ないため割愛
        return C0801W02AcptRelationDto.builder()
            // 商品大分類コード：製品水
            .watercd(shnService.getWatercd())
            // 商品大分類コード：サーバ関連用
            .serverlcd(shnService.getServerLcd())
            // 商品中分類コード：サーバ関連本体
            .servermcd(shnService.getServerMcd())
            // 得意先区分
            .tokuisakikbn(conditionDto.getTokuisakikbn())
            // 受託配達顧客区分
            .jutakudlvrycstkbn(conditionDto.getJutakudlvrycstkbn())
            // 受注区分
            .acptkbn(CommonConstants.KBN_ACPT_SHOHIN)
            // 受注区分詳細
            .acptkbnshs(CommonConstants.KBN_ACPTSYS_NONE)
            // 有効区分
            .yukokbn(CommonConstants.YUKOKBN_1).build();
    }

    /**
     * 受注配達テーブル登録時の値を設定する。
     * 
     * @param conditionDto 登録データ
     * @param s02m017tEntity 請求先Entity
     * @return S04T001TEntity
     */
    private S04T001TEntity createInsertS04t001tEntity(C0802W01RegistrationConditionDto conditionDto,
        S02M017TEntity s02m017tEntity) {

        S04T001TEntity s04t001tEntity = new S04T001TEntity();

        s04t001tEntity.setApkihoncd(conditionDto.getApkihoncd()); // AP基本コード
        s04t001tEntity.setShopcd(conditionDto.getShopcd()); // ショップコード
        s04t001tEntity.setAcptdnpyno(conditionDto.getAcptdnpyno()); // 受注伝票番号
        s04t001tEntity.setEdano((short) 0); // 枝番号
        s04t001tEntity.setItakuaiteapkihoncd(null); // 委託相手ＡＰ基本コード
        s04t001tEntity.setItakuaiteshopcd(null); // 委託相手ショップコード
        s04t001tEntity.setItakuaiteacptdnpyno(null); // 委託相手受注伝票番号
        s04t001tEntity.setItakuaiteedano(null); // 委託相手枝番号
        s04t001tEntity.setDlvrytrgtflg("1"); // 配達対象フラグ
        if (CommonConstants.KBN_TOKUISAKI_KEIHI.equals(conditionDto.getTokuisakikbn())) {
            s04t001tEntity.setDnpykbn(CommonConstants.KBN_KEIHI_SYUKKO);
        } else {
            s04t001tEntity.setDnpykbn(CommonConstants.KBN_URIAGE_SYUKKO);
        } // 伝票区分
        s04t001tEntity.setTorihikikbn(CommonConstants.KBN_ACPTTORIHIKI_JUCHUYOYAKU); // 取引区分
        s04t001tEntity.setAcptkbn(CommonConstants.KBN_ACPT_SHOHIN); // 受注区分
        s04t001tEntity.setAcptkbnshs(CommonConstants.KBN_ACPTSYS_NONE); // 受注区分詳細
        s04t001tEntity.setTokuisakicd(conditionDto.getTokuisakicd()); // 得意先コード
        s04t001tEntity.setAcptdd(conditionDto.getUnyodate()); // 受注日
        s04t001tEntity.setAcpttnshaapkihoncd(conditionDto.getAcpttnshaapkihoncd()); // 受注担当者ＡＰ基本コード
        s04t001tEntity.setAcpttnshashopcd(conditionDto.getAcpttnshashopcd()); // 受注担当者ショップコード
        s04t001tEntity.setAcpttnshacd(conditionDto.getAcpttnshacd()); // 受注担当者コード
        s04t001tEntity.setDlvryytidd(null); // 配達予定日
        s04t001tEntity.setDlvryjissekidd(null); // 配達実績日
        s04t001tEntity.setDlvrydd(null); // 配達日
        s04t001tEntity.setDlvrykibojikan(null); // 配達希望時間
        s04t001tEntity.setDlvrykibojikanshry(null); // 配達希望時間終了
        s04t001tEntity.setDlvrykibojikankbn(null); // 配達希望時間区分
        s04t001tEntity.setFzaiokikanoflg(conditionDto.getFzaiokikanoflg()); // 不在置き可能フラグ
        s04t001tEntity.setFzaiokijissekiflg("0"); // 不在置き実績フラグ
        s04t001tEntity.setDlvrytnshacd(conditionDto.getDlvrytnshacd()); // 配達担当者コード
        s04t001tEntity.setDlvrykankatsuapkihoncd(conditionDto.getDlvrykankatsuapkihoncd()); // 配達管轄ＡＰ基本コード
        s04t001tEntity.setDlvrykankatsushopcd(conditionDto.getDlvrykankatsushopcd()); // 配達管轄ショップコード
        s04t001tEntity.setDlvrykyotencd(conditionDto.getDlvrykyotencd()); // 配達拠点コード
        s04t001tEntity.setDlvryareacd(conditionDto.getDlvryareacd()); // 配達エリアコード
        s04t001tEntity.setDlvrykbn(conditionDto.getDlvrykbn()); // 配達区分
        s04t001tEntity.setDlvrycmpnycd(conditionDto.getDlvrycmpnycd()); // 配達会社コード
        s04t001tEntity.setAzukaribtln1n2rittoruhns(null); // 預かりボトル１２Ｌ本数
        s04t001tEntity.setKybtln1n2rittoruhns(null); // 回収予定ボトル１２Ｌ本数
        s04t001tEntity.setKzbtln1n2rittoruhns(null); // 回収実績ボトル１２Ｌ本数
        s04t001tEntity.setAzukaribtln8rittoruhns(null); // 預かりボトル８Ｌ本数
        s04t001tEntity.setKybtln8rittoruhns(null); // 回収予定ボトル８Ｌ本数
        s04t001tEntity.setKzbtln8rittoruhns(null); // 回収実績ボトル８Ｌ本数
        s04t001tEntity.setPrntmemon1(conditionDto.getPrntmemon1()); // 印刷メモ１
        s04t001tEntity.setPrntmemon2(conditionDto.getPrntmemon2()); // 印刷メモ２
        s04t001tEntity.setPrntmemon3(conditionDto.getPrntmemon3()); // 印刷メモ３
        s04t001tEntity.setAcptshimeprcsdd(CommonConstants.DEF_DATE_00000000); // 受注締担当者ＡＰ基本コード
        s04t001tEntity.setAcptshimetnshaapkihoncd(null); // 受注締担当者ＡＰ基本コード
        s04t001tEntity.setAcptshimetnshashopcd(null); // 受注締担当者ショップコード
        s04t001tEntity.setAcptshimeprcstnshacd(null); // 受注締処理担当者コード
        s04t001tEntity.setDlvryjissekiprcsdd(CommonConstants.DEF_DATE_00000000); // 配達実績処理日
        s04t001tEntity.setDlvryjissekimonoapkihoncd(null); // 配達実績者ＡＰ基本コード
        s04t001tEntity.setDlvryjissekimonoshopcd(null); // 配達実績者ショップコード
        s04t001tEntity.setDlvryjissekiprcstnshacd(null); // 配達実績処理担当者コード
        s04t001tEntity.setUrishimeprcsdd(CommonConstants.DEF_DATE_00000000); // 売上締処理日
        s04t001tEntity.setGetsujishimedydm(null); // 月次締年月
        s04t001tEntity.setGetsujishimedd(null); // 月次締日
        s04t001tEntity.setGetsujiprcsjissidd(null); // 月次処理実施日
        s04t001tEntity.setDlvryjun(conditionDto.getDlvryjun()); // 配達順
        s04t001tEntity.setNoshoprnttrgtflg(CommonConstants.FLG_ON); // 納品書印刷対象フラグ
        s04t001tEntity.setNoshoprntsumiflg(CommonConstants.FLG_OFF); // 納品書印刷済フラグ
        s04t001tEntity.setNoshoprntdd(null); // 納品書印刷日
        s04t001tEntity.setNoshohikaeprnttrgtflg(CommonConstants.FLG_ON); // 納品書控印刷対象フラグ
        s04t001tEntity.setNoshohikaeprntsumiflg(CommonConstants.FLG_OFF); // 納品書控印刷済フラグ
        s04t001tEntity.setNoshohikaeprntdd(null); // 納品書控印刷日
        if (CommonConstants.KAISHUHOHOKBN_7.equals(s02m017tEntity.getKaishuhohokbn())) {
            s04t001tEntity.setNyknhyoprnttrgtflg(CommonConstants.FLG_ON);
        } else {
            s04t001tEntity.setNyknhyoprnttrgtflg(CommonConstants.FLG_OFF);
        } // 入金票印刷対象フラグ
        s04t001tEntity.setNyknhyoprntsumiflg(CommonConstants.FLG_OFF); // 入金票印刷済フラグ
        s04t001tEntity.setNyknhyoprntdd(null); // 入金票印刷日
        s04t001tEntity.setWsvsijishoprnttrgtflg(CommonConstants.FLG_OFF); // サーバー指示書印刷対象フラグ
        s04t001tEntity.setWsvsijishoprntsumiflg(CommonConstants.FLG_OFF); // サーバー指示書印刷済フラグ
        s04t001tEntity.setWsvsijishoprntdd(null); // サーバー指示書印刷日
        s04t001tEntity.setSingldnpyflg("0"); // 単体伝票締めフラグ
        s04t001tEntity.setDnpysts(CommonConstants.DNPYSTS_01); // 伝票ステータス
        s04t001tEntity.setAcptsts(CommonConstants.DNPYSTS_01); // 受注ステータス
        s04t001tEntity.setDlvrysts(CommonConstants.KBN_DLVRYSTS_NONE); // 配達ステータス
        s04t001tEntity.setDatsksmotokbn(CommonConstants.KBN_SAKUSEIMOTO_JUCHUYOYAKU); // データ作成元区分
        s04t001tEntity.setSksmotokanrikbn(null); // 作成元管理区分
        s04t001tEntity.setSksmotokanrino(null); // 作成元管理番号
        s04t001tEntity.setAcpthikiatesakiapkihoncd(conditionDto.getApkihoncd()); // 受注引当先ＡＰ基本コード
        s04t001tEntity.setAcpthikiatesakishopcd(conditionDto.getShopcd()); // 受注引当先ショップコード
        s04t001tEntity.setAcpthikiatesakikyotencd(conditionDto.getDlvrykyotencd()); // 受注引当先拠点コード
        s04t001tEntity.setDlvryytirgrayusyohns((short) conditionDto.getDlvryytirgrayusyohns()); // 配達予定レギュラー有償本数
        s04t001tEntity.setDlvryytirgramushohns((short) conditionDto.getDlvryytirgramushohns()); // 配達予定レギュラー無償本数
        s04t001tEntity.setDlvryytisrmyusyohns((short) conditionDto.getDlvryytisrmyusyohns()); // 配達予定スリム有償本数
        s04t001tEntity.setDlvryytisrmmushohns((short) conditionDto.getDlvryytisrmmushohns()); // 配達予定スリム無償本数
        s04t001tEntity.setDlvryytisonotayusyohns((short) conditionDto.getDlvryytisonotayusyohns()); // 配達予定その他有償本数
        s04t001tEntity.setDlvryytisonotamushohns((short) conditionDto.getDlvryytisonotamushohns()); // 配達予定その他無償本数
        s04t001tEntity.setDlvryytiseihinmizuurikin(conditionDto.getDlvryytiseihinmizuurikin()); // 配達予定製品水売上金額
        s04t001tEntity.setDlvryytisonotaurikin(conditionDto.getDlvryytisonotaurikin()); // 配達予定その他売上金額
        s04t001tEntity.setDlvryjissekirgrayusyohns((short) 0); // 配達実績レギュラー有償本数
        s04t001tEntity.setDlvryjissekirgramushohns((short) 0); // 配達実績レギュラー無償本数
        s04t001tEntity.setDlvryjissekisrmyusyohns((short) 0); // 配達実績スリム有償本数
        s04t001tEntity.setDlvryjissekisrmmushohns((short) 0); // 配達実績スリム無償本数
        s04t001tEntity.setDlvryjissekistyshns((short) 0); // 配達実績その他有償本数
        s04t001tEntity.setDlvryjissekistmshns((short) 0); // 配達実績その他無償本数
        s04t001tEntity.setDlvryjissekishsuakin(0); // 配達実績製品水売上金額
        s04t001tEntity.setDlvryjissekisonotaurikin(0); // 配達実績その他売上金額
        s04t001tEntity.setAcptkin(conditionDto.getAcptkin()); // 受注金額
        s04t001tEntity.setAcptshohitaxkin(conditionDto.getAcptshohitaxkin()); // 受注消費税金額
        s04t001tEntity.setAcptippannukikin(conditionDto.getAcptippannukikin()); // 受注一般税抜金額
        s04t001tEntity.setAcptippantaxkin(conditionDto.getAcptippantaxkin()); // 受注一般消費税金額
        s04t001tEntity.setAcptkeigennukikin(conditionDto.getAcptkeigennukikin()); // 受注軽減税抜金額
        s04t001tEntity.setAcptkeigentaxkin(conditionDto.getAcptkeigentaxkin()); // 受注軽減消費税金額
        s04t001tEntity.setAcpthikazeiuriikin(conditionDto.getAcpthikazeiuriikin()); // 受注非課税金額
        s04t001tEntity.setAcptfukazeiuriikin(conditionDto.getAcptfukazeiuriikin()); // 受注不課税売上金額
        s04t001tEntity.setNohinkin((long) 0); // 納品金額
        s04t001tEntity.setNohinshohitaxkin((long) 0); // 納品消費税金額
        s04t001tEntity.setNohinippannukikin((long) 0); // 納品一般税抜金額
        s04t001tEntity.setNohinippantaxkin((long) 0); // 納品一般消費税金額
        s04t001tEntity.setNohinkeigennukikin((long) 0); // 納品軽減税抜金額
        s04t001tEntity.setNohinkeigentaxkin((long) 0); // 納品軽減消費税金額
        s04t001tEntity.setNohinhikazeiuriikin((long) 0); // 納品非課税金額
        s04t001tEntity.setNohinfukazeiuriikin((long) 0); // 納品不課税売上金額
        s04t001tEntity.setKurerurittorustup((short) 0); // クレールＬ設置
        s04t001tEntity.setKurerurittoruhikiage((short) 0); // クレールＬ引上
        s04t001tEntity.setKureruesustup((short) 0); // クレールＳ設置
        s04t001tEntity.setKureruesuhikiage((short) 0); // クレールＳ引上
        s04t001tEntity.setSonotastup((short) 0); // その他設置
        s04t001tEntity.setSonotahikiage((short) 0); // その他引上
        s04t001tEntity.setTaxkeisankbn(conditionDto.getTaxkeisankbn()); // 税計算区分
        s04t001tEntity.setZnkdlvrydd(null); // 前回配達日
        s04t001tEntity.setZnkdlvryhns((short) 0); // 前回配達本数
        s04t001tEntity.setJikaidlvryytidd(null); // 次回配達予定日
        s04t001tEntity.setZnkshimedd(null); // 前回締日
        s04t001tEntity.setZnkkurikoshikin(null); // 前回繰越金額
        s04t001tEntity.setAcptthdaibikiflg(CommonConstants.FLG_OFF); // 受注時代引フラグ
        s04t001tEntity.setAcptrcptkeirokbn(conditionDto.getAcptrcptkeirokbn()); // 受注受付経路区分
        s04t001tEntity.setYukokbn(CommonConstants.YUKOKBN_1); // 有効区分
        s04t001tEntity.setToshaapkihoncd(conditionDto.getLoginUserApkihoncd()); // 登録者ＡＰ基本コード
        s04t001tEntity.setToshashopcd(conditionDto.getLoginUserShopcd()); // 登録者ショップコード
        s04t001tEntity.setToshacd(conditionDto.getLoginUserTnshacd()); // 登録者コード
        s04t001tEntity.setInptsystmymd(conditionDto.getSystemDate()); // 登録システム日付
        s04t001tEntity.setInptsystmjikan(conditionDto.getSystemTime()); // 登録システム時間
        s04t001tEntity.setInptunyoymd(conditionDto.getUnyodate()); // 登録運用日付
        s04t001tEntity.setInptipadrse(conditionDto.getIpAddress()); // 登録ＩＰアドレス
        s04t001tEntity.setInptpgmid(conditionDto.getProgramId()); // 登録プログラムＩＤ
        s04t001tEntity.setKoshaapkihoncd(conditionDto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        s04t001tEntity.setKoshashopcd(conditionDto.getLoginUserShopcd()); // 更新者ショップコード
        s04t001tEntity.setKoshacd(conditionDto.getLoginUserTnshacd()); // 更新者コード
        s04t001tEntity.setKshnsystmymd(conditionDto.getSystemDate()); // 更新システム日付
        s04t001tEntity.setKshnsystmjikan(conditionDto.getSystemTime()); // 更新システム時間
        s04t001tEntity.setKshnunyoymd(conditionDto.getUnyodate()); // 更新運用日付
        s04t001tEntity.setKshnipadrse(conditionDto.getIpAddress()); // 更新ＩＰアドレス
        s04t001tEntity.setKshnpgmid(conditionDto.getProgramId()); // 更新プログラムＩＤ

        return s04t001tEntity;

    }

    /**
     * 受注配達明細テーブル登録時の値を設定する。
     * 
     * @param conditionDto 登録データ
     * @param meisaiDto 明細登録データ
     * @param invoicshikoflg インボイス施行日フラグ
     * @return S04T002TEntity
     */
    private S04T002TEntity createInsertS04t002tEntity(C0802W01RegistrationConditionDto conditionDto,
        C0802W01RegistrationMeisaiDto meisaiDto, boolean invoicshikoflg) {

        S04T002TEntity entity = new S04T002TEntity();

        entity.setApkihoncd(conditionDto.getApkihoncd()); // AP基本コード
        entity.setShopcd(conditionDto.getShopcd()); // ショップコード
        entity.setAcptdnpyno(conditionDto.getAcptdnpyno()); // 受注伝票番号
        entity.setEdano((short) 0); // 枝番号
        entity.setAcptdnpymeisaino(meisaiDto.getAcptdnpymeisaino()); // 受注伝票明細番号
        entity.setAcptdnpymeisaiedano(meisaiDto.getAcptdnpymeisaiedano()); // 受注伝票明細枝番号
        entity.setItakuaiteapkihoncd(null); // 委託相手ＡＰ基本コード
        entity.setItakuaiteshopcd(null); // 委託相手ショップコード
        entity.setItakuaiteacptdnpyno(null); // 委託相手受注伝票番号
        entity.setItakuaiteedano(null); // 委託相手枝番号
        entity.setItakuaiteacptdnpymeisaino(null); // 委託相手受注伝票明細番号
        entity.setItakuaiteacptdnpymeisaiedano(null); // 委託相手受注伝票明細枝番号
        entity.setLctgrycd(meisaiDto.getLctgrycd()); // 大分類コード
        entity.setMctgrycd(meisaiDto.getMctgrycd()); // 中分類コード
        entity.setSctgrycd(meisaiDto.getSctgrycd()); // 小分類コード
        entity.setShncd(meisaiDto.getShncd()); // 商品コード
        entity.setHaifubutsuapkihoncd(null); // 配布物ＡＰ基本コード
        entity.setHaifubutsushopcd(null); // 配布物ショップコード
        entity.setHaifubutsucd(null); // 配布物コード
        entity.setKatasiki(meisaiDto.getKatasiki()); // 型式
        entity.setKeijokbn(meisaiDto.getKeijokbn()); // 計上区分
        entity.setAcptsuryo(meisaiDto.getAcptsuryo()); // 受注数量
        entity.setHyojununt(new BigDecimal(meisaiDto.getHyojununt())); // 標準単価
        entity.setAcptunt(new BigDecimal(meisaiDto.getAcptunt())); // 受注単価
        entity.setAcptkin(meisaiDto.getAcptkin()); // 受注金額
        entity.setAcptuntshohitaxkin(meisaiDto.getAcptuntshohitaxkin()); // 受注単価消費税金額
        entity.setAcptshohitaxkin(meisaiDto.getAcptshohitaxkin()); // 受注消費税金額
        entity.setItakucmpnyacptunt(null); // 委託会社受注単価
        entity.setItakucmpnyacptuntshohitaxkin(null); // 委託会社受注単価消費税金額
        entity.setItakucmpnyacptshohitaxkin(null); // 委託会社受注消費税金額
        entity.setNohinsuryo(BigDecimal.ZERO); // 納品数量
        entity.setNohinunt(BigDecimal.ZERO); // 納品単価
        entity.setNohinkin((long) 0); // 納品金額
        entity.setNohinuntshohitaxkin((long) 0); // 納品単価消費税金額
        entity.setNohinshohitaxkin((long) 0); // 納品消費税金額
        entity.setSoukaisu(meisaiDto.getSoukaisu()); // 総回数
        entity.setZankaisu(meisaiDto.getZankaisu()); // 残回数
        entity.setTaxkbn(meisaiDto.getTaxkbn());
        if (CommonConstants.KBN_MEISAI_3.equals(meisaiDto.getMeisaikbn())) {
            entity.setYusyokbn(meisaiDto.getYusyokbn());
        } else if (!CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(meisaiDto.getMeisaikbn())) {
            if (CommonConstants.KBN_KEIJO_KEIHI.equals(meisaiDto.getKeijokbn())) {
                entity.setYusyokbn(CommonConstants.KBN_YUSHO_KEIHI);
            } else if (CommonConstants.KBN_KEIJO_SUBRENTAL.equals(meisaiDto.getKeijokbn())
                || CommonConstants.KBN_KEIJO_RENTAL.equals(meisaiDto.getKeijokbn())) {
                entity.setYusyokbn(CommonConstants.KBN_YUSHO_RENTAL);
            } else if (CommonConstants.KBN_KEIJO_URIAGE.equals(meisaiDto.getKeijokbn())
                || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(meisaiDto.getKeijokbn())) {
                entity.setYusyokbn(CommonConstants.KBN_YUSHO_YUSHO);
            } else {
                entity.setYusyokbn(CommonConstants.KBN_YUSHO_MUSHO);
            }
        } // 有償区分
        if (CommonConstants.KBN_MEISAI_2.equals(meisaiDto.getMeisaikbn())) {
            entity.setMeisaikbn(CommonConstants.KBN_MEISAI_2);
        } else if (CommonConstants.KBN_MEISAI_3.equals(meisaiDto.getMeisaikbn())) {
            entity.setMeisaikbn(CommonConstants.KBN_MEISAI_3);
        } else if (!CommonConstants.KBN_MEISAI_HAIHUBUTSU.equals(meisaiDto.getMeisaikbn())
            && !CommonConstants.KBN_MEISAI_2.equals(meisaiDto.getMeisaikbn())) {
            entity.setMeisaikbn(CommonConstants.KBN_MEISAI_SHOHIN);
        } // 明細区分
        entity.setNyuryokumeisaiflg(meisaiDto.getNyuryokumeisaiflg()); // 入力明細フラグ
        entity.setTanpinmeisaiflg(meisaiDto.getTanpinmeisaiflg()); // 単品明細フラグ
        entity.setStshncd(meisaiDto.getStshncd()); // セット商品コード
        entity.setMeisaibiko(ItemUtil.nullToString(meisaiDto.getMeisaibiko())); // 明細備考
        entity.setDatsksmotokbn(CommonConstants.KBN_SAKUSEIMOTO_JUCHUYOYAKU); // データ作成元区分
        entity.setUridnpyno(null); // 売上伝票番号
        entity.setUridnpyedano(null); // 売上伝票枝番号
        entity.setUridnpymeisaino(null); // 売上伝票明細番号
        entity.setUridnpymeisaiedano(null); // 売上伝票明細枝番号
        entity.setYukokbn(CommonConstants.YUKOKBN_1); // 有効区分
        entity.setToshaapkihoncd(conditionDto.getLoginUserApkihoncd()); // 登録者ＡＰ基本コード
        entity.setToshashopcd(conditionDto.getLoginUserShopcd()); // 登録者ショップコード
        entity.setToshacd(conditionDto.getLoginUserTnshacd()); // 登録者コード
        entity.setInptsystmymd(conditionDto.getSystemDate()); // 登録システム日付
        entity.setInptsystmjikan(conditionDto.getSystemTime()); // 登録システム時間
        entity.setInptunyoymd(conditionDto.getUnyodate()); // 登録運用日付
        entity.setInptipadrse(conditionDto.getIpAddress()); // 登録ＩＰアドレス
        entity.setInptpgmid(conditionDto.getProgramId()); // 登録プログラムＩＤ
        entity.setKoshaapkihoncd(conditionDto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        entity.setKoshashopcd(conditionDto.getLoginUserShopcd()); // 更新者ショップコード
        entity.setKoshacd(conditionDto.getLoginUserTnshacd()); // 更新者コード
        entity.setKshnsystmymd(conditionDto.getSystemDate()); // 更新システム日付
        entity.setKshnsystmjikan(conditionDto.getSystemTime()); // 更新システム時間
        entity.setKshnunyoymd(conditionDto.getUnyodate()); // 更新運用日付
        entity.setKshnipadrse(conditionDto.getIpAddress()); // 更新ＩＰアドレス
        entity.setKshnpgmid(conditionDto.getProgramId()); // 更新プログラムＩＤ

        return entity;

    }

    /**
     * 更新時のset値を設定する。
     * 
     * @param conditionDto 登録更新用DTO
     * @return S04T001TEntity
     */
    private S04T001TEntity createUpdateSetS04t001tEntity(
        C0802W01RegistrationConditionDto conditionDto) {

        S04T001TEntityKey key = new S04T001TEntityKey();
        key.setApkihoncd(conditionDto.getApkihoncd());
        key.setShopcd(conditionDto.getShopcd());
        key.setAcptdnpyno(conditionDto.getAcptdnpyno());
        key.setEdano(Short.parseShort(conditionDto.getEdano()));
        S04T001TEntity s04t001tEntity = s04t001tDao.selectByPrimaryKeyForLock(key);

        s04t001tEntity.setAcptdd(conditionDto.getUnyodate()); // 受注日
        s04t001tEntity.setAcpttnshaapkihoncd(conditionDto.getAcpttnshaapkihoncd()); // 受注担当者ＡＰ基本コード
        s04t001tEntity.setAcpttnshashopcd(conditionDto.getAcpttnshashopcd()); // 受注担当者ショップコード
        s04t001tEntity.setAcpttnshacd(conditionDto.getAcpttnshacd()); // 受注担当者コード
        s04t001tEntity.setFzaiokikanoflg(conditionDto.getFzaiokikanoflg()); // 不在置き可能フラグ
        s04t001tEntity.setDlvrytnshacd(conditionDto.getDlvrytnshacd()); // 配達担当者コード
        s04t001tEntity.setDlvrykankatsuapkihoncd(conditionDto.getDlvrykankatsuapkihoncd()); // 配達管轄ＡＰ基本コード
        s04t001tEntity.setDlvrykankatsushopcd(conditionDto.getDlvrykankatsushopcd()); // 配達管轄ショップコード
        s04t001tEntity.setDlvrykyotencd(conditionDto.getDlvrykyotencd()); // 配達拠点コード
        s04t001tEntity.setDlvryareacd(conditionDto.getDlvryareacd()); // 配達エリアコード
        s04t001tEntity.setDlvrykbn(conditionDto.getDlvrykbn()); // 配達区分
        s04t001tEntity.setDlvrycmpnycd(conditionDto.getDlvrycmpnycd()); // 配達会社コード
        s04t001tEntity.setPrntmemon1(conditionDto.getPrntmemon1()); // 印刷メモ１
        s04t001tEntity.setPrntmemon1(conditionDto.getPrntmemon1()); // 印刷メモ２
        s04t001tEntity.setPrntmemon1(conditionDto.getPrntmemon1()); // 印刷メモ３
        s04t001tEntity.setDlvryjun(conditionDto.getDlvryjun()); // 配達順
        s04t001tEntity.setAcpthikiatesakiapkihoncd(conditionDto.getApkihoncd()); // 受注引当先ＡＰ基本コード
        s04t001tEntity.setAcpthikiatesakishopcd(conditionDto.getShopcd()); // 受注引当先ショップコード
        s04t001tEntity.setAcpthikiatesakikyotencd(conditionDto.getDlvrykyotencd()); // 受注引当先拠点コード
        s04t001tEntity.setDlvryytirgrayusyohns((short) conditionDto.getDlvryytirgrayusyohns()); // 配達予定レギュラー有償本数
        s04t001tEntity.setDlvryytirgramushohns((short) conditionDto.getDlvryytirgramushohns()); // 配達予定レギュラー無償本数
        s04t001tEntity.setDlvryytisrmyusyohns((short) conditionDto.getDlvryytisrmyusyohns()); // 配達予定スリム有償本数
        s04t001tEntity.setDlvryytisrmmushohns((short) conditionDto.getDlvryytisrmmushohns()); // 配達予定スリム無償本数
        s04t001tEntity.setDlvryytisonotayusyohns((short) conditionDto.getDlvryytisonotayusyohns()); // 配達予定その他有償本数
        s04t001tEntity.setDlvryytisonotamushohns((short) conditionDto.getDlvryytisonotamushohns()); // 配達予定その他無償本数
        s04t001tEntity.setDlvryytiseihinmizuurikin(conditionDto.getDlvryytiseihinmizuurikin()); // 配達予定製品水売上金額
        s04t001tEntity.setDlvryytisonotaurikin(conditionDto.getDlvryytisonotaurikin()); // 配達予定その他売上金額
        s04t001tEntity.setAcptkin(conditionDto.getAcptkin()); // 受注金額
        s04t001tEntity.setAcptshohitaxkin(conditionDto.getAcptshohitaxkin()); // 受注消費税金額
        s04t001tEntity.setAcptippannukikin(conditionDto.getAcptippannukikin()); // 受注一般税抜金額
        s04t001tEntity.setAcptippantaxkin(conditionDto.getAcptippantaxkin()); // 受注一般消費税金額
        s04t001tEntity.setAcptkeigennukikin(conditionDto.getAcptkeigennukikin()); // 受注軽減税抜金額
        s04t001tEntity.setAcptkeigentaxkin(conditionDto.getAcptkeigentaxkin()); // 受注軽減消費税金額
        s04t001tEntity.setAcpthikazeiuriikin(conditionDto.getAcpthikazeiuriikin()); // 受注非課税金額
        s04t001tEntity.setAcptfukazeiuriikin(conditionDto.getAcptfukazeiuriikin()); // 受注不課税売上金額
        s04t001tEntity.setNohinkin((long) 0); // 納品金額
        s04t001tEntity.setNohinshohitaxkin((long) 0); // 納品消費税金額
        s04t001tEntity.setNohinippannukikin((long) 0); // 納品一般税抜金額
        s04t001tEntity.setNohinippantaxkin((long) 0); // 納品一般消費税金額
        s04t001tEntity.setNohinkeigennukikin((long) 0); // 納品軽減税抜金額
        s04t001tEntity.setNohinkeigentaxkin((long) 0); // 納品軽減消費税金額
        s04t001tEntity.setNohinhikazeiuriikin((long) 0); // 納品非課税金額
        s04t001tEntity.setNohinfukazeiuriikin((long) 0); // 納品不課税売上金額
        s04t001tEntity.setKurerurittorustup((short) 0); // クレールＬ設置
        s04t001tEntity.setKurerurittoruhikiage((short) 0); // クレールＬ引上
        s04t001tEntity.setKureruesustup((short) 0); // クレールＳ設置
        s04t001tEntity.setKureruesuhikiage((short) 0); // クレールＳ引上
        s04t001tEntity.setSonotastup((short) 0); // その他設置
        s04t001tEntity.setSonotahikiage((short) 0); // その他引上
        s04t001tEntity.setTaxkeisankbn(conditionDto.getTaxkeisankbn()); // 税計算区分
        s04t001tEntity.setAcptrcptkeirokbn(conditionDto.getAcptrcptkeirokbn()); // 受注受付経路区分
        s04t001tEntity.setKoshaapkihoncd(conditionDto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        s04t001tEntity.setKoshashopcd(conditionDto.getLoginUserShopcd()); // 更新者ショップコード
        s04t001tEntity.setKoshacd(conditionDto.getLoginUserTnshacd()); // 更新者コード
        s04t001tEntity.setKshnsystmymd(conditionDto.getSystemDate()); // 更新システム日付
        s04t001tEntity.setKshnsystmjikan(conditionDto.getSystemTime()); // 更新システム時間
        s04t001tEntity.setKshnunyoymd(conditionDto.getUnyodate()); // 更新運用日付
        s04t001tEntity.setKshnipadrse(conditionDto.getIpAddress()); // 更新ＩＰアドレス
        s04t001tEntity.setKshnpgmid(conditionDto.getProgramId()); // 更新プログラムＩＤ

        return s04t001tEntity;
    }

    /**
     * 更新時の条件値を設定する。
     * 
     * @param conditionDto 登録更新用DTO
     * @return S04T001TEntityExample
     */
    private S04T001TEntityExample createUpdateConditionS04t001tEntity(
        C0802W01RegistrationConditionDto conditionDto) {

        // クライテリア(SQL条件)の生成
        S04T001TEntityExample entity = new S04T001TEntityExample();
        entity.createCriteria().andApkihoncdEqualTo(conditionDto.getApkihoncd())
            .andShopcdEqualTo(conditionDto.getShopcd())
            .andAcptdnpynoEqualTo(conditionDto.getAcptdnpyno())
            .andEdanoEqualTo(Short.parseShort(conditionDto.getEdano()));

        return entity;
    }

    /**
     * 更新時の条件値を設定する。
     * 
     * @param conditionDto 登録更新用DTO
     * @return S04T002TEntityExample
     */
    private S04T002TEntityExample createUpdateConditionS04t002tEntity(
        C0802W01RegistrationConditionDto conditionDto) {

        // クライテリア(SQL条件)の生成
        S04T002TEntityExample entity = new S04T002TEntityExample();
        entity.createCriteria().andApkihoncdEqualTo(conditionDto.getApkihoncd())
            .andShopcdEqualTo(conditionDto.getShopcd())
            .andAcptdnpynoEqualTo(conditionDto.getAcptdnpyno())
            .andEdanoEqualTo(Short.parseShort(conditionDto.getEdano()));

        return entity;
    }

    /**
     * 更新時のset値を設定する。
     * 
     * @param conditionDto 登録更新用DTO
     * @return S04T001TEntity
     */
    private S04T001TEntity createDeleteSetS04t001tEntity(
        C0802W01RegistrationConditionDto conditionDto) {

        S04T001TEntity s04t001tEntity = new S04T001TEntity();

        s04t001tEntity.setYukokbn(CommonConstants.YUKOKBN_0); // 有効区分

        return s04t001tEntity;
    }

    /**
     * 切替日を判断して現／新項目を取得
     * 
     * @param oldValue 現項目
     * @param newVlue 新項目
     * @param switchDay 切替日
     * @param targetDay 対象日
     * @return
     */
    private String getCurrentValue(String oldValue, String newVlue, String switchDay,
        String targetDay) {

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
     * 得意先区分、商品分類に応じた計上区分の妥当性チェック
     * 
     * @param relation 受注関係DTO
     * @return 判定結果
     */
    private boolean checkRelations(C0801W02AcptRelationDto relation) {

        // ※受注予約登録では計上区分にキャンペーンが入力されることはない
        // ※受注予約登録では受注区分は商品受注しか登録されることがない

        String pattern = C0801W02AcptRelationDto.PT_UCSPTMKRSK;
        if (relation.isKokyaku() || relation.isItakusaki()) {
            // 顧客または委託先（受託）の場合
            if (relation.isServerBody(relation.getLctgrycd(), relation.getMctgrycd())) {
                // 商品大・中分類コード＝ＳＶ … 売上のみ
                pattern = C0801W02AcptRelationDto.PT_UC00000000;
            } else if (relation.isWater()) {
                // 商品大分類コード＝水 … 売上、サービス、プロモ、試飲のみ
                pattern = C0801W02AcptRelationDto.PT_UCSPT00000;
            } else {
                // 商品大分類コード≠ＳＶ、水 … 売上、サービス、プロモ、レンタルのみ
                pattern = C0801W02AcptRelationDto.PT_UCSP000R00;
            }
        } else if (relation.isShop()) {
            // ショップの場合
            if (relation.isServerBody(relation.getLctgrycd(), relation.getMctgrycd())) {
                // 商品大・中分類コード＝ＳＶ … 売上、サブレンタルのみ
                pattern = C0801W02AcptRelationDto.PT_UC000000S0;
            } else if (relation.isWater()) {
                // 商品大分類コード＝水 … 売上、サービス、プロモ、試飲、メンテのみ
                pattern = C0801W02AcptRelationDto.PT_UCSPTM0000;
            } else {
                // 商品大分類コード≠ＳＶ、水 … 売上、サービス、プロモ、サブレンタル、レンタルのみ
                pattern = C0801W02AcptRelationDto.PT_UCSP000RS0;
            }
        } else if (relation.isAp() || relation.isAci()) {
            // 他APまたはACI本部の場合
            if (relation.isServerBody(relation.getLctgrycd(), relation.getMctgrycd())) {
                // 商品大・中分類コード＝ＳＶ … 売上のみ
                pattern = C0801W02AcptRelationDto.PT_UC00000000;
            } else {
                // 商品大分類コード≠ＳＶ … 売上・サービスのみ
                pattern = C0801W02AcptRelationDto.PT_UCS0000000;
            }
        } else if (relation.isKeihiKeijo()) {
            // 計上区分が経費の場合のみ
            pattern = C0801W02AcptRelationDto.PT_000000000K;
        }
        return checkPattern(relation.getKeijokbn(), pattern);
    }

    /**
     * 受注明細リスト展開処理
     * 
     * @param conditionDto 得意先配達情報データEntity
     * @param meisaiDtoListt 受注明細データEntity
     * @param s02m046tEntity ショップマスタEntity
     * @return List<S04T002TC0805W01Entity>
     */
    private List<C0802W01RegistrationMeisaiDto> deploymentSetShohin(
        C0802W01RegistrationConditionDto conditionDto,
        List<C0802W01RegistrationMeisaiDto> meisaiDtoList, S02M046TEntity s02m046tEntity) {

        List<C0802W01RegistrationMeisaiDto> retList = meisaiDtoList;
        List<C0802W01RegistrationMeisaiDto> retTmpList = new ArrayList<>();

        // 「製品水」の商品大分類コード
        String shnmctgrycd =
            getSystemMasterValue(CommonConstants.SYS_SETTEINO_096, CommonConstants.SETTEICD_096_1);
        // 「サーバー関連用」の商品大分類コード
        String srvlctgrycd =
            getSystemMasterValue(CommonConstants.SYS_SETTEINO_096, CommonConstants.SETTEICD_096_4);
        // 「サーバー関連本体」の商品大分類コード
        String srvmctgrycd =
            getSystemMasterValue(CommonConstants.SYS_SETTEINO_134, CommonConstants.SETTEICD_134_41);
        // 「レギュラー」の商品コードリスト
        S02M007TEntityExample S02M007TExample = new S02M007TEntityExample();
        S02M007TExample.createCriteria().andApkihoncdEqualTo(CommonConstants.APCD_001)
            .andStkbnEqualTo(CommonConstants.STKBN_0)
            .andLctgrycdEqualTo(CommonConstants.LCTGRYCD_01)
            .andMctgrycdEqualTo(CommonConstants.MCTGRYCD_001)
            .andSctgrycdEqualTo(CommonConstants.SCTGRYCD_001)
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<S02M007TEntity> s02m007tRgOutList = s02m007tDao.selectByExample(S02M007TExample);
        List<String> rgralist = new ArrayList<>();
        for (S02M007TEntity s02m007tEntity : s02m007tRgOutList) {
            rgralist.add(s02m007tEntity.getShncd());
        }

        // 「スリム」の商品コードリスト
        S02M007TExample = new S02M007TEntityExample();
        S02M007TExample.createCriteria().andApkihoncdEqualTo(CommonConstants.APCD_001)
            .andStkbnEqualTo(CommonConstants.STKBN_0)
            .andLctgrycdEqualTo(CommonConstants.LCTGRYCD_01)
            .andMctgrycdEqualTo(CommonConstants.MCTGRYCD_001)
            .andSctgrycdEqualTo(CommonConstants.SCTGRYCD_002)
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<S02M007TEntity> s02m007tSlOutList = s02m007tDao.selectByExample(S02M007TExample);
        List<String> srmlist = new ArrayList<>();
        for (S02M007TEntity s02m007tEntity : s02m007tSlOutList) {
            srmlist.add(s02m007tEntity.getShncd());
        }

        // 受注関係Entityの設定
        C0805W01AcptRelationConditionDto acptrelationdto = new C0805W01AcptRelationConditionDto();
        acptrelationdto.setShnmctgrycd(shnmctgrycd);
        acptrelationdto.setSrvlctgrycd(srvlctgrycd);
        acptrelationdto.setSrvmctgrycd(srvmctgrycd);
        acptrelationdto.setRgralist(rgralist);
        acptrelationdto.setSrmlist(srmlist);
        acptrelationdto.setTokuisakikbn(conditionDto.getTokuisakikbn());
        acptrelationdto.setAcptkbn("1");
        acptrelationdto.setAcptkbnshs("00");

        // セット商品展開処理
        BigDecimal rgrayusyohns = BigDecimal.ZERO;
        BigDecimal rgramushohns = BigDecimal.ZERO;
        BigDecimal srmyusyohns = BigDecimal.ZERO;
        BigDecimal srmmushohns = BigDecimal.ZERO;
        BigDecimal sonotayusyohns = BigDecimal.ZERO;
        BigDecimal sonotamushohns = BigDecimal.ZERO;
        BigDecimal seihinmizuurikin = BigDecimal.ZERO;
        BigDecimal sonotaurikin = BigDecimal.ZERO;
        BigDecimal srvchngesuu = BigDecimal.ZERO;

        for (C0802W01RegistrationMeisaiDto meisaiDto : meisaiDtoList) {

            long saveAcptshohitaxkin = 0;
            if (meisaiDto.getAcptshohitaxkin() != 0) {
                saveAcptshohitaxkin = meisaiDto.getAcptshohitaxkin();
            }
            // 商品マスタビュー(単独TBL検索）
            S02V001TEntityExample S02V001TExample = new S02V001TEntityExample();
            S02V001TExample.createCriteria().andApkihoncdEqualTo(conditionDto.getApkihoncd())
                .andShopcdEqualTo(conditionDto.getShopcd()).andShncdEqualTo(meisaiDto.getShncd());
            List<S02V001TEntity> S02V001TOutList = s02v001tDao.selectByExample(S02V001TExample);

            // 商品マスタビューEntity.税設定区分が「0:しない」の場合、商品マスタビューEntity.税区分は「null」（課税）として設定
            if ("0".equals(S02V001TOutList.get(0).getTaxsetteikbn())) {
                S02V001TOutList.get(0).setTaxkbn(null);
            }

            // 受注明細データEntityの設定
            meisaiDto.setLctgrycd(S02V001TOutList.get(0).getLctgrycd());
            meisaiDto.setMctgrycd(S02V001TOutList.get(0).getMctgrycd());
            meisaiDto.setSctgrycd(S02V001TOutList.get(0).getSctgrycd());
            meisaiDto.setTaxkbn(S02V001TOutList.get(0).getTaxkbn());
            // 受注関係Entityをコピーし、商品の情報を追加
            meisaiDto.setRelshnmctgrycd(acptrelationdto.getShnmctgrycd());
            meisaiDto.setRelsrvlctgrycd(acptrelationdto.getSrvlctgrycd());
            meisaiDto.setRelsrvmctgrycd(acptrelationdto.getSrvmctgrycd());
            meisaiDto.setRelkeijokbn(meisaiDto.getKeijokbn());
            meisaiDto.setRelrgralist(acptrelationdto.getRgralist());
            meisaiDto.setRelsrmlist(acptrelationdto.getSrmlist());
            meisaiDto.setReltokuisakikbn(acptrelationdto.getTokuisakikbn());
            meisaiDto.setRelacptkbn(acptrelationdto.getAcptkbn());
            meisaiDto.setRelacptkbnshs(acptrelationdto.getAcptkbnshs());
            meisaiDto.setRellctgrycd(meisaiDto.getLctgrycd());
            meisaiDto.setRelmctgrycd(meisaiDto.getMctgrycd());
            meisaiDto.setRelshncd(meisaiDto.getShncd());
            // 受注明細データEntityに商品情報を設定
            meisaiDto.setS02v001TEntity(S02V001TOutList.get(0));
            meisaiDto.setNyuryokumeisaiflg(CommonConstants.FLG_ON);
            meisaiDto.setMeisaikbn(CommonConstants.KBN_MEISAI_SHOHIN);

            // 本数、金額、サーバー交換台数の集計（セット商品は対象外）
            if (CommonConstants.TANPINMEISAIFLG_1.equals(meisaiDto.getTanpinmeisaiflg())
                && CommonConstants.KBN_MEISAI_SHOHIN.equals(meisaiDto.getMeisaikbn())) {

                // 受注伝票集計Entity
                // レギュラー有償本数
                if (meisaiDto.getRelrgralist().indexOf(meisaiDto.getShncd()) >= 0
                    && (CommonConstants.KBN_KEIJO_URIAGE.equals(meisaiDto.getKeijokbn())
                        || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(meisaiDto.getKeijokbn()))) {
                    rgrayusyohns = rgrayusyohns.add(meisaiDto.getAcptsuryo());
                }
                // レギュラー無償本数
                if (meisaiDto.getRelrgralist().indexOf(meisaiDto.getShncd()) >= 0
                    && !(CommonConstants.KBN_KEIJO_URIAGE.equals(meisaiDto.getKeijokbn())
                        || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(meisaiDto.getKeijokbn()))) {
                    rgramushohns = rgramushohns.add(meisaiDto.getAcptsuryo());
                }
                // スリム有償本数
                if (meisaiDto.getRelsrmlist().indexOf(meisaiDto.getShncd()) >= 0
                    && (CommonConstants.KBN_KEIJO_URIAGE.equals(meisaiDto.getKeijokbn())
                        || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(meisaiDto.getKeijokbn()))) {
                    srmyusyohns = srmyusyohns.add(meisaiDto.getAcptsuryo());
                }
                // スリム無償本数
                if (meisaiDto.getRelsrmlist().indexOf(meisaiDto.getShncd()) >= 0
                    && !(CommonConstants.KBN_KEIJO_URIAGE.equals(meisaiDto.getKeijokbn())
                        || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(meisaiDto.getKeijokbn()))) {
                    srmmushohns = srmmushohns.add(meisaiDto.getAcptsuryo());
                }
                // その他有償本数
                if (meisaiDto.getRelshnmctgrycd().equals(meisaiDto.getLctgrycd())
                    && meisaiDto.getRelrgralist().indexOf(meisaiDto.getShncd()) < 0
                    && meisaiDto.getRelsrmlist().indexOf(meisaiDto.getShncd()) < 0
                    && (CommonConstants.KBN_KEIJO_URIAGE.equals(meisaiDto.getKeijokbn())
                        || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(meisaiDto.getKeijokbn()))) {
                    sonotayusyohns = sonotayusyohns.add(meisaiDto.getAcptsuryo());
                }
                // その他無償本数
                if (meisaiDto.getRelshnmctgrycd().equals(meisaiDto.getLctgrycd())
                    && meisaiDto.getRelrgralist().indexOf(meisaiDto.getShncd()) < 0
                    && meisaiDto.getRelsrmlist().indexOf(meisaiDto.getShncd()) < 0
                    && !(CommonConstants.KBN_KEIJO_URIAGE.equals(meisaiDto.getKeijokbn())
                        || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(meisaiDto.getKeijokbn()))) {
                    sonotamushohns = sonotamushohns.add(meisaiDto.getAcptsuryo());
                }
                // 製品水売上金
                if (meisaiDto.getRelshnmctgrycd().equals(meisaiDto.getLctgrycd())) {
                    seihinmizuurikin = seihinmizuurikin.add(new BigDecimal(meisaiDto.getAcptkin()));
                }
                // その他売上金
                if (!meisaiDto.getRelshnmctgrycd().equals(meisaiDto.getLctgrycd())) {
                    sonotaurikin = sonotaurikin.add(new BigDecimal(meisaiDto.getAcptkin()));
                }
                // サーバー交換台数
                if (meisaiDto.getRelsrvlctgrycd().equals(meisaiDto.getLctgrycd())
                    && meisaiDto.getRelsrvmctgrycd().equals(meisaiDto.getMctgrycd())
                    && (CommonConstants.KBN_ACPT_KOKAN.equals(meisaiDto.getRelacptkbn())
                        || CommonConstants.KBN_ACPT_MAINT.equals(meisaiDto.getRelacptkbn()))
                    && CommonConstants.KBN_ACPTSYS_KOKAN.equals(meisaiDto.getRelacptkbnshs())) {
                    srvchngesuu = srvchngesuu.add(meisaiDto.getAcptsuryo());
                }
            }

            if (CommonConstants.TANPINMEISAIFLG_0.equals(meisaiDto.getTanpinmeisaiflg())) {

                // セット商品の展開
                meisaiDto.setStshncd(meisaiDto.getShncd());

                // セット商品リスト取得
                S02M008TC0805W01Entity s02m008tc0805w01EntityIn = new S02M008TC0805W01Entity();
                s02m008tc0805w01EntityIn.setApkihoncd(conditionDto.getApkihoncd());
                s02m008tc0805w01EntityIn.setShopcd(conditionDto.getShopcd());
                s02m008tc0805w01EntityIn.setStshncd(meisaiDto.getShncd());

                List<S02M008TC0805W01Entity> s02m008tc0805w01EntityOutList =
                    s02m008tDao.selectByC0805W01(s02m008tc0805w01EntityIn);

                // 受注消費税金額
                long reAcptshohitaxkin = 0;
                // 子明細Entity
                C0802W01RegistrationMeisaiDto childEntity = new C0802W01RegistrationMeisaiDto();
                // 構成品の明細情報を生成
                for (int m = 0; m < s02m008tc0805w01EntityOutList.size(); m++) {

                    // 商品マスタビュー(単独TBL検索）
                    S02V001TEntityExample koseiS02V001TExample = new S02V001TEntityExample();
                    koseiS02V001TExample.createCriteria()
                        .andApkihoncdEqualTo(conditionDto.getApkihoncd())
                        .andShopcdEqualTo(conditionDto.getShopcd())
                        .andShncdEqualTo(s02m008tc0805w01EntityOutList.get(m).getKoseishncd());
                    List<S02V001TEntity> koseiS02V001TOutList =
                        s02v001tDao.selectByExample(koseiS02V001TExample);

                    // 受注明細データEntityを子明細データEntityにコピー
                    childEntity = copyChildEntity(meisaiDto);
                    // 受注伝票明細枝番号を設定
                    childEntity.setAcptdnpymeisaiedano((short) (m + 1));
                    // 商品コード
                    childEntity.setShncd(s02m008tc0805w01EntityOutList.get(m).getKoseishncd());
                    // 型式
                    childEntity.setKatasiki(null);
                    // セット商品コード
                    childEntity.setStshncd(meisaiDto.getShncd());
                    // 入力明細フラグ
                    childEntity.setNyuryokumeisaiflg(CommonConstants.FLG_OFF);
                    // 単品明細フラグ
                    childEntity.setTanpinmeisaiflg(CommonConstants.FLG_ON);
                    // 大分類コード
                    childEntity.setLctgrycd(koseiS02V001TOutList.get(0).getLctgrycd());
                    // 中分類コード
                    childEntity.setMctgrycd(koseiS02V001TOutList.get(0).getMctgrycd());
                    // 小分類コード
                    childEntity.setSctgrycd(koseiS02V001TOutList.get(0).getSctgrycd());
                    // 税区分
                    childEntity.setTaxkbn(koseiS02V001TOutList.get(0).getTaxkbn());

                    // 製品水大分類コード
                    childEntity.setRelshnmctgrycd(acptrelationdto.getShnmctgrycd());
                    // サーバー大分類コード
                    childEntity.setRelsrvlctgrycd(acptrelationdto.getSrvlctgrycd());
                    // サーバー中分類コード
                    childEntity.setRelsrvmctgrycd(acptrelationdto.getSrvmctgrycd());
                    // レギュラーの商品コードリスト
                    childEntity.setRelrgralist(acptrelationdto.getRgralist());
                    // スリムの商品コードリスト
                    childEntity.setRelsrmlist(acptrelationdto.getSrmlist());
                    // 得意先区分
                    childEntity.setReltokuisakikbn(acptrelationdto.getTokuisakikbn());
                    // 受注区分
                    childEntity.setRelacptkbn(acptrelationdto.getAcptkbn());
                    // 受注区分詳細
                    childEntity.setRelacptkbnshs(acptrelationdto.getAcptkbnshs());
                    // 計上区分
                    childEntity.setRelkeijokbn(acptrelationdto.getKeijokbn());
                    // 商品大分類コード
                    childEntity.setRellctgrycd(childEntity.getLctgrycd());
                    // 商品中分類コード
                    childEntity.setRelmctgrycd(childEntity.getMctgrycd());
                    // 商品コード
                    childEntity.setRelshncd(childEntity.getShncd());
                    // 商品情報
                    childEntity.setS02v001TEntity(koseiS02V001TOutList.get(0));
                    // 受注子商品税区分、納品子商品税区分の設定
                    String acptkoshnkbn = koseiS02V001TOutList.get(0).getTaxkbn();
                    // 受注数量
                    childEntity.setAcptsuryo(meisaiDto.getAcptsuryo()
                        .multiply(s02m008tc0805w01EntityOutList.get(m).getSuryo()));
                    // 納品数量
                    childEntity.setNohinsuryo(meisaiDto.getAcptsuryo()
                        .multiply(s02m008tc0805w01EntityOutList.get(m).getSuryo()));
                    // 標準価格
                    if (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(conditionDto.getDlvrykbn())) {
                        childEntity.setHyojununt(String.valueOf(getCurrentPriceValue(
                            koseiS02V001TOutList.get(0).getGenzmochikaeriprice(),
                            koseiS02V001TOutList.get(0).getShinmochikaeriprice(),
                            koseiS02V001TOutList.get(0).getHyojunpricekirikaedd(),
                            conditionDto.getUnyodate())));

                    } else {
                        childEntity.setHyojununt(String.valueOf(
                            getCurrentPriceValue(koseiS02V001TOutList.get(0).getGenzhyojunprice(),
                                koseiS02V001TOutList.get(0).getShinhyojunprice(),
                                koseiS02V001TOutList.get(0).getHyojunpricekirikaedd(),
                                conditionDto.getUnyodate())));
                    }

                    // 子明細データEntityの金額系項目の設定 d-5-8-1
                    if (CommonConstants.KBN_KEIJO_URIAGE.equals(meisaiDto.getKeijokbn())
                        || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(meisaiDto.getKeijokbn())) {
                        // 得意先別セット商品取得
                        S02M066TC0805W01Entity s02m066tc0805w01EntityIn =
                            new S02M066TC0805W01Entity();
                        s02m066tc0805w01EntityIn.setRenban(
                            String.valueOf(s02m008tc0805w01EntityOutList.get(m).getRenban()));
                        s02m066tc0805w01EntityIn.setApkihoncd(conditionDto.getApkihoncd());
                        s02m066tc0805w01EntityIn.setShopcd(conditionDto.getShopcd());
                        s02m066tc0805w01EntityIn
                            .setStshncd(s02m008tc0805w01EntityOutList.get(m).getStshncd());
                        s02m066tc0805w01EntityIn.setTokuisakicd(conditionDto.getTokuisakicd());
                        s02m066tc0805w01EntityIn.setTankabasedd(conditionDto.getUnyodate());

                        List<S02M066TC0805W01Entity> s02m066tc0805w01EntityOutList =
                            s02m066tDao.selectByC0805W01(s02m066tc0805w01EntityIn);

                        // 受注単価取得
                        BigDecimal juchutanka = BigDecimal.ZERO;
                        if (s02m066tc0805w01EntityOutList.size() > 0
                            && conditionDto.getUnyodate().compareTo(
                                s02m066tc0805w01EntityOutList.get(0).getTekiyostrtdd()) >= 0
                            && s02m066tc0805w01EntityOutList.get(0).getTekiyoshrydd()
                                .compareTo(conditionDto.getUnyodate()) >= 0) {
                            if (CommonConstants.KBN_DLVRY_MOCHIKAERI
                                .equals(conditionDto.getDlvrykbn())) {
                                juchutanka =
                                    s02m066tc0805w01EntityOutList.get(0).getMochikaeriprice();
                            } else {
                                juchutanka = s02m066tc0805w01EntityOutList.get(0).getDlvryprice();
                            }
                        } else {
                            if (CommonConstants.KBN_DLVRY_MOCHIKAERI
                                .equals(conditionDto.getDlvrykbn())) {
                                juchutanka =
                                    s02m008tc0805w01EntityOutList.get(m).getMochikaeriprice();
                            } else {
                                juchutanka = s02m008tc0805w01EntityOutList.get(m).getDlvryprice();
                            }
                        }

                        // 受注単価消費税金額、受注消費税金額、受注金額の取得
                        String priceList[] = taxService.getPriceList(String.valueOf(juchutanka),
                            String.valueOf(childEntity.getAcptsuryo()), acptkoshnkbn,
                            conditionDto.getTokuisakikbn(), conditionDto.getUnyodate(),
                            s02m046tEntity);

                        // 子商品の計上区分、および金額系項目を設定
                        // 計上区分
                        childEntity.setKeijokbn(s02m008tc0805w01EntityOutList.get(m).getKeijokbn());
                        // 受注単価
                        childEntity.setAcptunt(String.valueOf(juchutanka));
                        // 受注単価消費税金額
                        childEntity.setAcptuntshohitaxkin(Long.parseLong(priceList[0]));
                        if ("0".equals(S02V001TOutList.get(0).getTaxsetteikbn())
                            && (CommonConstants.KBN_TAX_HIKAZEI.equals(childEntity.getTaxkbn())
                                || CommonConstants.KBN_TAX_HUKAZEI
                                    .equals(childEntity.getTaxkbn()))) {
                            childEntity.setAcptuntshohitaxkin(0);
                        }
                        // 受注消費税金額
                        childEntity.setAcptshohitaxkin(Long.parseLong(priceList[1]));
                        if ("0".equals(S02V001TOutList.get(0).getTaxsetteikbn())
                            && (CommonConstants.KBN_TAX_HIKAZEI.equals(childEntity.getTaxkbn())
                                || CommonConstants.KBN_TAX_HUKAZEI
                                    .equals(childEntity.getTaxkbn()))) {
                            childEntity.setAcptshohitaxkin(0);
                        }
                        if (conditionDto.getUnyodate()
                            .compareTo(s02m046tEntity.getInvoicshikoymd()) >= 0) {
                            childEntity.setAcptshohitaxkin(0);
                        }
                        // 受注金額
                        childEntity.setAcptkin(Long.parseLong(priceList[2]));

                    } else {
                        // 計上区分
                        childEntity.setKeijokbn(meisaiDto.getKeijokbn());
                        // 受注単価
                        childEntity.setAcptunt(String.valueOf(BigDecimal.ZERO));
                        // 受注単価消費税金額
                        childEntity.setAcptuntshohitaxkin(0);
                        // 受注消費税金額
                        childEntity.setAcptshohitaxkin(0);
                        // 受注金額
                        childEntity.setAcptkin((long) 0);
                    }

                    // 受注消費税金額
                    reAcptshohitaxkin = reAcptshohitaxkin + childEntity.getAcptshohitaxkin();
                    childEntity.setTaxkbn(koseiS02V001TOutList.get(0).getTaxkbn());

                    // 本数、金額、サーバー交換台数の集計
                    if (CommonConstants.TANPINMEISAIFLG_0.equals(meisaiDto.getTanpinmeisaiflg())
                        && CommonConstants.KBN_MEISAI_SHOHIN.equals(meisaiDto.getMeisaikbn())) {

                        // 受注伝票集計Entity
                        // レギュラー有償本数
                        if (childEntity.getRelrgralist().indexOf(childEntity.getShncd()) >= 0
                            && (CommonConstants.KBN_KEIJO_URIAGE.equals(childEntity.getKeijokbn())
                                || CommonConstants.KBN_KEIJO_CAMPAIGN
                                    .equals(childEntity.getKeijokbn()))) {
                            rgrayusyohns = rgrayusyohns.add(childEntity.getAcptsuryo());
                        }
                        // レギュラー無償本数
                        if (childEntity.getRelrgralist().indexOf(childEntity.getShncd()) >= 0
                            && !(CommonConstants.KBN_KEIJO_URIAGE.equals(childEntity.getKeijokbn())
                                || CommonConstants.KBN_KEIJO_CAMPAIGN
                                    .equals(childEntity.getKeijokbn()))) {
                            rgramushohns = rgramushohns.add(childEntity.getAcptsuryo());
                        }
                        // スリム有償本数
                        if (childEntity.getRelsrmlist().indexOf(childEntity.getShncd()) >= 0
                            && (CommonConstants.KBN_KEIJO_URIAGE.equals(childEntity.getKeijokbn())
                                || CommonConstants.KBN_KEIJO_CAMPAIGN
                                    .equals(childEntity.getKeijokbn()))) {
                            srmyusyohns = srmyusyohns.add(childEntity.getAcptsuryo());
                        }
                        // スリム無償本数
                        if (childEntity.getRelsrmlist().indexOf(childEntity.getShncd()) >= 0
                            && !(CommonConstants.KBN_KEIJO_URIAGE.equals(childEntity.getKeijokbn())
                                || CommonConstants.KBN_KEIJO_CAMPAIGN
                                    .equals(childEntity.getKeijokbn()))) {
                            srmmushohns = srmmushohns.add(childEntity.getAcptsuryo());
                        }
                        // その他有償本数
                        if (childEntity.getRelshnmctgrycd().equals(childEntity.getLctgrycd())
                            && childEntity.getRelrgralist().indexOf(childEntity.getShncd()) < 0
                            && childEntity.getRelsrmlist().indexOf(childEntity.getShncd()) < 0
                            && (CommonConstants.KBN_KEIJO_URIAGE.equals(childEntity.getKeijokbn())
                                || CommonConstants.KBN_KEIJO_CAMPAIGN
                                    .equals(childEntity.getKeijokbn()))) {
                            sonotayusyohns = sonotayusyohns.add(childEntity.getAcptsuryo());
                        }
                        // その他無償本数
                        if (childEntity.getRelshnmctgrycd().equals(childEntity.getLctgrycd())
                            && childEntity.getRelrgralist().indexOf(childEntity.getShncd()) < 0
                            && childEntity.getRelsrmlist().indexOf(childEntity.getShncd()) < 0
                            && !(CommonConstants.KBN_KEIJO_URIAGE.equals(childEntity.getKeijokbn())
                                || CommonConstants.KBN_KEIJO_CAMPAIGN
                                    .equals(childEntity.getKeijokbn()))) {
                            sonotamushohns = sonotamushohns.add(childEntity.getAcptsuryo());
                        }
                        // 製品水売上金
                        if (childEntity.getRelshnmctgrycd().equals(childEntity.getLctgrycd())) {
                            seihinmizuurikin =
                                seihinmizuurikin.add(new BigDecimal(childEntity.getAcptkin()));
                        }
                        // その他売上金
                        if (!childEntity.getRelshnmctgrycd().equals(childEntity.getLctgrycd())) {
                            sonotaurikin =
                                sonotaurikin.add(new BigDecimal(childEntity.getAcptkin()));
                        }
                        // サーバー交換台数
                        if (childEntity.getRelsrvlctgrycd().equals(childEntity.getLctgrycd())
                            && childEntity.getRelsrvmctgrycd().equals(childEntity.getMctgrycd())
                            && (CommonConstants.KBN_ACPT_KOKAN.equals(childEntity.getRelacptkbn())
                                || CommonConstants.KBN_ACPT_MAINT
                                    .equals(childEntity.getRelacptkbn()))
                            && CommonConstants.KBN_ACPTSYS_KOKAN
                                .equals(childEntity.getRelacptkbnshs())) {
                            srvchngesuu = srvchngesuu.add(childEntity.getAcptsuryo());

                        }
                    }
                    // 構成商品（子明細データEntity）を構成商品リスト（List<子明細データEntity>）に追加する
                    retTmpList.add(childEntity);

                } // 構成品 d-5

                if (s02m046tEntity.getInvoicshikoymd().compareTo(conditionDto.getUnyodate()) > 0) {
                    // 調整消費税金額
                    long ajustAcptshohitaxkin = saveAcptshohitaxkin - reAcptshohitaxkin;

                    if (ajustAcptshohitaxkin != 0) {
                        // セット商品消費税調整明細生成
                        C0802W01RegistrationMeisaiDto ajustAcptshohitaxkinEntity =
                            reateAjustAcptshohitaxData(conditionDto, meisaiDto, childEntity,
                                ajustAcptshohitaxkin);

                        retTmpList.add(ajustAcptshohitaxkinEntity);
                    }
                }
            }
        } // セット商品展開(d)

        conditionDto.setDlvryytirgrayusyohns(Integer.parseInt(String.valueOf(rgrayusyohns)));
        conditionDto.setDlvryytirgramushohns(Integer.parseInt(String.valueOf(rgramushohns)));
        conditionDto.setDlvryytisrmyusyohns(Integer.parseInt(String.valueOf(srmyusyohns)));
        conditionDto.setDlvryytisrmmushohns(Integer.parseInt(String.valueOf(srmmushohns)));
        conditionDto.setDlvryytisonotayusyohns(Integer.parseInt(String.valueOf(sonotayusyohns)));
        conditionDto.setDlvryytisonotamushohns(Integer.parseInt(String.valueOf(sonotamushohns)));
        conditionDto
            .setDlvryytiseihinmizuurikin(Integer.parseInt(String.valueOf(seihinmizuurikin)));
        conditionDto.setDlvryytisonotaurikin(Integer.parseInt(String.valueOf(sonotaurikin)));


        retList.addAll(retTmpList);

        // List<受注明細データEntity>を以下の順番（ 受注配達明細テーブルのプライマリーキー順）にソート
        List<C0802W01RegistrationMeisaiDto> retSortList = retList.stream()
            .sorted(Comparator.comparing(C0802W01RegistrationMeisaiDto::getAcptdnpymeisaino)
                .thenComparing(C0802W01RegistrationMeisaiDto::getAcptdnpymeisaiedano))
            .collect(Collectors.toList());

        return retSortList;

    }

    /**
     * システム管理マスタ設定値取得
     * 
     * @param setteino
     * @param setteicd
     * @return String
     */
    private String getSystemMasterValue(String setteino, String setteicd) {

        return s02m006tDao.findConfig(setteino, setteicd);

    }

    /**
     * エンティティコピー
     * 
     * @param entity
     * @return C0802W01RegistrationMeisaiDto
     */
    private C0802W01RegistrationMeisaiDto copyChildEntity(C0802W01RegistrationMeisaiDto entity) {
        C0802W01RegistrationMeisaiDto childEntity = new C0802W01RegistrationMeisaiDto();

        // KEY
        childEntity.setAcptdnpymeisaino(entity.getAcptdnpymeisaino());
        childEntity.setAcptdnpymeisaiedano(entity.getAcptdnpymeisaiedano());
        // TABLE
        childEntity.setLctgrycd(entity.getLctgrycd());
        childEntity.setMctgrycd(entity.getMctgrycd());
        childEntity.setSctgrycd(entity.getSctgrycd());
        childEntity.setShncd(entity.getShncd());
        childEntity.setKatasiki(entity.getKatasiki());
        childEntity.setKeijokbn(entity.getKeijokbn());
        childEntity.setAcptsuryo(entity.getAcptsuryo());
        childEntity.setHyojununt(entity.getHyojununt());
        childEntity.setAcptunt(entity.getAcptunt());
        childEntity.setAcptkin(entity.getAcptkin());
        childEntity.setAcptuntshohitaxkin(entity.getAcptuntshohitaxkin());
        childEntity.setAcptshohitaxkin(entity.getAcptshohitaxkin());
        childEntity.setSoukaisu(entity.getSoukaisu());
        childEntity.setZankaisu(entity.getZankaisu());
        childEntity.setTaxkbn(entity.getTaxkbn());
        childEntity.setYusyokbn(entity.getYusyokbn());
        childEntity.setMeisaikbn(entity.getMeisaikbn());
        childEntity.setNyuryokumeisaiflg(entity.getNyuryokumeisaiflg());
        childEntity.setTanpinmeisaiflg(entity.getTanpinmeisaiflg());
        childEntity.setStshncd(entity.getStshncd());
        childEntity.setMeisaibiko(entity.getMeisaibiko());
        // CUSTOM
        childEntity.setShnnmryakusho(entity.getShnnmryakusho());
        childEntity.setIro(entity.getIro());
        childEntity.setSz(entity.getSz());
        childEntity.setUriirisu(entity.getUriirisu());
        childEntity.setUritani(entity.getUritani());
        childEntity.setSokazu(entity.getSokazu());
        childEntity.setRelshnmctgrycd(entity.getRelshnmctgrycd());
        childEntity.setRelsrvlctgrycd(entity.getRelsrvlctgrycd());
        childEntity.setRelsrvmctgrycd(entity.getRelsrvmctgrycd());
        childEntity.setReltokuisakikbn(entity.getReltokuisakikbn());
        childEntity.setRelacptkbn(entity.getRelacptkbn());
        childEntity.setRelacptkbnshs(entity.getRelacptkbnshs());
        childEntity.setRelkeijokbn(entity.getRelkeijokbn());

        return childEntity;
    }

    /**
     * セット商品消費税調整明細生成
     * 
     * @param conditionDto 得意先配達情報データEntity
     * @param meisaiDto 受注明細データEntity
     * @param childEntity 子明細データEntity
     * @param ajustAcptshohitaxkin 調整消費税金額
     * @return C0802W01RegistrationMeisaiDto
     */
    private C0802W01RegistrationMeisaiDto reateAjustAcptshohitaxData(
        C0802W01RegistrationConditionDto conditionDto, C0802W01RegistrationMeisaiDto meisaiDto,
        C0802W01RegistrationMeisaiDto childEntity, long ajustAcptshohitaxkin) {

        // システム管理マスタ検索：単独TBL検索
        S02M006TEntityExample S02M006TExample = new S02M006TEntityExample();
        S02M006TExample.createCriteria().andApkihoncdEqualTo(CommonConstants.APCD_000)
            .andShopcdEqualTo(CommonConstants.SYS_SHOPCD_000)
            .andSetteinoEqualTo(CommonConstants.SYS_SETTEINO_166)
            .andRcrdkbnEqualTo(CommonConstants.KBN_RECORD_MEISAI)
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1);
        List<S02M006TEntity> S02M006tOutList = s02m006tDao.selectByExample(S02M006TExample);

        // 調整消費税明細に設定するの大分類コード、中分類コード、小分類コード、税区分を取得
        // 商品マスタビュー(単独TBL検索）
        S02V001TEntityExample S02V001TExample = new S02V001TEntityExample();
        S02V001TExample.createCriteria().andApkihoncdEqualTo(conditionDto.getApkihoncd())
            .andShopcdEqualTo(conditionDto.getShopcd()).andShncdEqualTo(meisaiDto.getShncd());
        List<S02V001TEntity> S02V001TOutList = s02v001tDao.selectByExample(S02V001TExample);

        // 標準単価取得
        BigDecimal hyojununt = BigDecimal.ZERO;
        if (CommonConstants.KBN_DLVRY_MOCHIKAERI.equals(conditionDto.getDlvrykbn())) {
            hyojununt = getCurrentPriceValue(S02V001TOutList.get(0).getGenzmochikaeriprice(),
                S02V001TOutList.get(0).getShinmochikaeriprice(),
                S02V001TOutList.get(0).getHyojunpricekirikaedd(), conditionDto.getUnyodate());

        } else {
            hyojununt = getCurrentPriceValue(S02V001TOutList.get(0).getGenzhyojunprice(),
                S02V001TOutList.get(0).getShinhyojunprice(),
                S02V001TOutList.get(0).getHyojunpricekirikaedd(), conditionDto.getUnyodate());
        }

        C0802W01RegistrationMeisaiDto ajustAcptshohitaxkinEntity = copyChildEntity(meisaiDto);

        // 受注伝票明細枝番号
        ajustAcptshohitaxkinEntity
            .setAcptdnpymeisaiedano((short) (childEntity.getAcptdnpymeisaiedano() + ((short) 1)));
        // 商品コード
        ajustAcptshohitaxkinEntity.setShncd(S02M006tOutList.get(0).getMojisetteiatai());
        // 型式
        ajustAcptshohitaxkinEntity.setKatasiki(null);
        // セット商品コード
        ajustAcptshohitaxkinEntity.setStshncd(meisaiDto.getShncd());
        // 入力明細フラグ
        ajustAcptshohitaxkinEntity.setNyuryokumeisaiflg(CommonConstants.NYURYOKUMEISAIFLG_0);
        // 単品明細フラグ
        ajustAcptshohitaxkinEntity.setTanpinmeisaiflg(CommonConstants.TANPINMEISAIFLG_1);
        // 大分類コード
        ajustAcptshohitaxkinEntity.setLctgrycd(S02V001TOutList.get(0).getLctgrycd());
        // 中分類コード
        ajustAcptshohitaxkinEntity.setMctgrycd(S02V001TOutList.get(0).getMctgrycd());
        // 小分類コード
        ajustAcptshohitaxkinEntity.setSctgrycd(S02V001TOutList.get(0).getSctgrycd());
        // 税区分
        ajustAcptshohitaxkinEntity.setTaxkbn(S02V001TOutList.get(0).getTaxkbn());
        // 明細区分
        ajustAcptshohitaxkinEntity.setMeisaikbn(CommonConstants.KBN_MEISAI_SHOHIZEI);
        // 有償区分
        ajustAcptshohitaxkinEntity.setYusyokbn(CommonConstants.KBN_YUSHO_NONE);
        // 受注数量
        ajustAcptshohitaxkinEntity.setAcptsuryo(BigDecimal.ZERO);
        // 納品数量
        ajustAcptshohitaxkinEntity.setNohinsuryo(BigDecimal.ZERO);
        // 標準単価
        ajustAcptshohitaxkinEntity.setHyojununt(String.valueOf(hyojununt));
        // 受注単価
        ajustAcptshohitaxkinEntity.setAcptunt(String.valueOf(BigDecimal.ZERO));
        // 受注金額
        ajustAcptshohitaxkinEntity.setAcptkin((long) 0);
        // 受注単価消費税金額
        ajustAcptshohitaxkinEntity.setAcptuntshohitaxkin(0);
        // 受注消費税金額
        ajustAcptshohitaxkinEntity.setAcptshohitaxkin(ajustAcptshohitaxkin);

        return ajustAcptshohitaxkinEntity;

    }

    /**
     * 切替日を判断して現／新項目を取得
     * 
     * @param oldValue 現項目
     * @param newVlue 新項目
     * @param switchDay 切替日
     * @param targetDay 対象日
     * @return
     */
    private BigDecimal getCurrentPriceValue(BigDecimal oldValue, BigDecimal newVlue,
        String switchDay, String targetDay) {

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
     * 価格情報設定
     * 
     * @param conditionDto C0802W01SearchConditionDto
     */
    private String getPriceinfo(C0802W01SearchConditionDto conditionDto) {
        S02M046TEntity keyEntity = new S02M046TEntity();
        keyEntity.setApkihoncd(conditionDto.getApkihoncd());
        keyEntity.setShopcd(conditionDto.getShopcd());

        S02M046TEntity result = s02m046tDao.selectByPrimaryKey(keyEntity);
        StringBuffer buf = new StringBuffer();
        buf.append("");
        if (result != null) {
            buf.append("{");
            buf.append("type:1");
            buf.append(",shohitaxkirikaedd:").append(result.getShohitaxkirikaedd()).append("");
            buf.append(",shinshohitax:").append(result.getShinshohitax()).append("");
            buf.append(",genzshohitax:").append(result.getGenzshohitax()).append("");
            buf.append(",taxkeisankbnurikirikaedd:").append(result.getTaxkeisankbnurikirikaedd())
                .append("");
            buf.append(",shintaxkeisankbnuri:").append(result.getShintaxkeisankbnuri()).append("");
            buf.append(",genztaxkeisankbnuri:").append(result.getGenztaxkeisankbnuri()).append("");
            buf.append(",taxhasukeisankbn:").append(result.getTaxhasukeisankbn()).append("");
            buf.append(",keisanhoho:1");
            buf.append(",taxkbn:1");
            buf.append(",urikinhasukeisankbn:").append(result.getUrikinhasukeisankbn()).append("");
            buf.append("}");
        }

        return buf.toString();
    }

    /**
     * 請求先マスタから有効な請求先を取得する
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param skysakicd 請求先コード
     * @return 有効な請求先
     */
    private Optional<S02M017TEntity> getSkysaki(String apkihoncd, String shopcd, String skysakicd) {
        // 請求先が未設定の場合
        if (StringUtils.isEmpty(skysakicd)) {
            return Optional.empty();
        }
        S02M017TEntityExample condition = new S02M017TEntityExample();
        condition.createCriteria() // 検索条件を設定
            .andApkihoncdEqualTo(apkihoncd) // AP基本コード
            .andShopcdEqualTo(shopcd) // ショップコード
            .andSkysakicdEqualTo(skysakicd) // 請求先コード
            .andYukokbnEqualTo(CommonConstants.YUKOKBN_1); // 有効区分が有効
        // 検索結果の先頭を返す
        return s02m017tDao.selectByExample(condition).stream().findFirst();
    }

    /**
     * 利用可能な計上区分であるかを判定する.
     * 
     * @param tokuisakikbn 得意先区分
     * @param keijokbn 計上区分
     * @return 利用可能な場合、true
     */
    private boolean checkKeijokbn(String tokuisakikbn, String keijokbn) {
        // 得意先区分または計上区分が空の場合はfalse
        if (StringUtil.isEmpty(tokuisakikbn) || StringUtil.isEmpty(keijokbn)) {
            return false;
        }
        List<String> keijokbnList = new ArrayList<>();
        for (LabelValueDto keijokbnDto : getKeijyokubunList(tokuisakikbn)) {
            keijokbnList.add(keijokbnDto.getKey());
        }
        if (keijokbnList.contains(keijokbn)) {
            return true;
        }
        return false;
    }

    /**
     * デフォルトの計上区分を取得する.
     * 
     * @param tokuisakikbn 得意先区分
     * @param keijokbn 計上区分
     * @param lctgrycd 大分類コード
     * @param mctgrycd 中分類コード
     * @return デフォルトの計上区分
     */
    private String getDefaultKeijokbn(String tokuisakikbn, String keijokbn, String lctgrycd,
        String mctgrycd) {

        // サーバー本体の大分類を取得
        String serverlcd = getSystemServerkanrencd();
        // サーバー本体の中分類を取得
        String servermcd = getSystemServerhontaicd();

        if (CommonConstants.KBN_TOKUISAKI_KEIHI.equals(tokuisakikbn)) {
            // 得意先区分＝経費の場合は「経費」
            return CommonConstants.KBN_KEIJO_KEIHI;
        } else if (!StringUtil.isEmpty(serverlcd) && serverlcd.equals(lctgrycd)
            && !StringUtil.isEmpty(servermcd) && servermcd.equals(mctgrycd)) {
            // 受注区分＝商品受注のみ
            // 得意先区分＝ショップの場合は「サブレンタル」
            if (CommonConstants.KBN_TOKUISAKI_SHOP.equals(tokuisakikbn)) {
                return CommonConstants.KBN_KEIJO_SUBRENTAL;
            } else {
                // それ以外の場合は「売上」
                return CommonConstants.KBN_KEIJO_URIAGE;
            }
        } else {
            // 大分類コード≠サーバーの場合は「売上」
            return CommonConstants.KBN_KEIJO_URIAGE;
        }
    }

    /**
     * 「サーバー関連用」の商品大分類コードを取得.
     * 
     * @return String 「サーバー関連用」の商品大分類コード
     */
    private String getSystemServerkanrencd() {
        return getSetteiatai(CommonConstants.APCD_000, CommonConstants.SYS_SHOPCD_000,
            CommonConstants.SYS_SETTEINO_096, CommonConstants.CD_SETTEI_SERVERKNREN);
    }

    /**
     * 「サーバー関連本体」の商品中分類コードを取得.
     * 
     * @return String 「サーバー関連本体」の商品中分類コード
     */
    private String getSystemServerhontaicd() {
        return getSetteiatai(CommonConstants.APCD_000, CommonConstants.SYS_SHOPCD_000,
            CommonConstants.SYS_SETTEINO_134, CommonConstants.CD_SETTEI_SERVERHONTAI);
    }

    /**
     * システム管理マスタ検索（設定値取得）
     * 
     * @param apkihoncd ＡＰ基本コード
     * @param shopcd ショップコード
     * @param setteino 設定番号
     * @param setteicd 設定コード
     * @return String
     */
    private String getSetteiatai(String apkihoncd, String shopcd, String setteino,
        String setteicd) {

        // システム管理マスタ検索
        S02M006TEntityKey entityKey = new S02M006TEntityKey();
        entityKey.setApkihoncd(apkihoncd);
        entityKey.setShopcd(shopcd);
        entityKey.setSetteino(setteino);
        entityKey.setRcrdkbn(CommonConstants.RECORD_KBN_M);
        entityKey.setSetteicd(setteicd);
        S02M006TEntity entity = s02m006tDao.selectByPrimaryKey(entityKey);

        if (entity == null) {
            return null;
        } else {
            if (CommonConstants.KBN_SETTEISHIYO_SUCHI.equals(entity.getSetteisiyokbn())) {
                return entity.getSuchisetteiatai().toString();
            } else {
                return entity.getMojisetteiatai();
            }
        }
    }
}
