package jp.co.aquaclara.accis.web.domain.service.d1501;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1501R21Entity;
import jp.co.aquaclara.accis.common.exception.BusinessException;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.common.util.ListCreatorUtil;
import jp.co.aquaclara.accis.web.domain.dto.d1501.D1501R21PrintDto;
import jp.co.aquaclara.accis.web.domain.dto.d1501.D1501R21TargetMeisaiDto;
import jp.co.aquaclara.accis.web.domain.dto.master.S01M004TDto;
import jp.co.aquaclara.accis.web.domain.service.common.OnlineBatchBaseService;
import jp.co.aquaclara.accis.web.domain.service.common.OnlineBatchCommonModel;
import jp.co.aquaclara.accis.web.domain.service.master.S01M004TService;

/**
 * 請求一覧表印刷OnlineBatchService
 * 
 * @author accisdev048
 *
 */
@Service
public class D1501R21Service extends OnlineBatchBaseService<D1501R21ParameterModel> {

    /**
     * 請求関連処理ユーティリティサービスクラス
     */
    @Autowired
    private S01M004TService s01M004TService;

    /**
     * 請求残高テーブルDAO
     */
    @Autowired
    private S07T001TDao s07T001Dao;

    /**
     * 処理ID
     */
    private static final String PROCESS_ID = "D1501B02";

    /**
     * 親子区分：1(親)
     */
    private static final String OYAKO_KBN_OYA = "1";

    /**
     * 親子区分：2(子)
     */
    private static final String OYAKO_KBN_KODOMO = "2";

    /**
     * 親子区分：3(一般)
     */
    private static final String OYAKO_KBN_IPPAN = "3";

    @Override
    public String getProcessId() {
        return PROCESS_ID;
    }

    /**
     * 請求一覧表印刷処理を実行
     * 
     * @param paramModel 請求一覧表出力ParameterModel
     * @param commonInfo 非同期処理の共通情報クラスModel
     */
    @Override
    @Transactional
    protected void process(D1501R21ParameterModel paramModel, OnlineBatchCommonModel commonInfo)
        throws BusinessException, Exception {
        Map<String, String> kaishuHouho = getKaishuHouho();
        List<List<String>> csvPrintData = new ArrayList<List<String>>();
        for (D1501R21TargetMeisaiDto meisaiDto : paramModel.getTargetMeisaiDtoList()) {
            List<S07T001TD1501R21Entity> seikyuList =
                getSeikyuList(paramModel.getPrintDto(), meisaiDto);
            if (seikyuList.size() == 0) {
                continue;
            }
            getSeikyuListPrintData(paramModel.getPrintDto(), seikyuList, kaishuHouho, csvPrintData);
            updateS07T001T(paramModel.getPrintDto(), meisaiDto.getSkysakicd());
        }
        super.createReportFile(csvPrintData, commonInfo.getFileNamne(), new ArrayList<>());
    }

    /**
     * 請求残高テーブルの更新
     * 
     * @param printDto 請求一覧表印刷DTO
     * @param skysakicd 請求先コード
     */
    private void updateS07T001T(D1501R21PrintDto printDto, String skysakicd) {
        S07T001TEntity entity = createS07T001TEntity(printDto, skysakicd);
        s07T001Dao.updateForD1501R20Printed(entity);
    }

    /**
     * 請求残高テーブルの更新Entity生成
     * 
     * @param printDto 請求一覧表印刷DTO.
     * @param skysakicd 請求先コード
     * @return 請求残高テーブルの更新Entity
     */
    private S07T001TEntity createS07T001TEntity(D1501R21PrintDto printDto, String skysakicd) {
        S07T001TEntity entity = new S07T001TEntity();
        // AP基本コード
        entity.setApkihoncd(printDto.getApkihoncd());
        // ショップコード
        entity.setShopcd(printDto.getShopcd());
        // 請求先コード
        entity.setSkysakicd(skysakicd);
        // 締年月
        entity.setShimedydm(printDto.getShimedydm());
        // 締日
        entity.setShimedd(printDto.getShimedd());
        // 請求一覧表印刷日
        entity.setSkyicrnhyprntdd(printDto.getUnyoymd());
        // 更新者AP基本コード
        entity.setKoshaapkihoncd(printDto.getLoginUserapkihoncd());
        // 更新者ショップコード
        entity.setKoshashopcd(printDto.getLoginUsershopcd());
        // 更新者コード
        entity.setKoshacd(printDto.getLoginUsertnshacd());
        // 更新システム日付
        entity.setKshnsystmymd(printDto.getSystemDate());
        // 更新システム時間
        entity.setKshnsystmjikan(printDto.getSystemTime());
        // 更新運用日付
        entity.setKshnunyoymd(printDto.getUnyoymd());
        // 更新IPアドレス
        entity.setKshnipadrse(printDto.getIpAddress());
        // 更新プログラムID
        entity.setKshnpgmid(D1501R00Constants.PROGRAM_ID_SEIKYUITIRAN);
        return entity;
    }

    /**
     * 回収方法区分名称取得
     * 
     * @return 回収方法区分名称をHashMap(設定値, 項目名)で返却
     */
    private Map<String, String> getKaishuHouho() {
        Map<String, String> resultMap = new HashMap<String, String>();
        S01M004TDto conditionDto = createKaishuhohoDto();
        List<S01M004TDto> searchResultList = s01M004TService.search(conditionDto, null);
        searchResultList.forEach(searchResult -> {
            resultMap.put(searchResult.getSetteiatai(), searchResult.getItemnm());
        });
        return resultMap;
    }

    /**
     * 回収方法区分名称取得用のDTOを作成して返却する
     * 
     * @return コンディションマスタ－DTOクラス.
     */
    private S01M004TDto createKaishuhohoDto() {
        S01M004TDto dto = new S01M004TDto();
        dto.setConditionid(CommonConstants.CONDITION_ID_CN160);
        return dto;
    }

    /**
     * 請求一覧表出力データ抽出処理
     * 
     * @param printDto 請求一覧表印刷DTO
     * @param meisaiDto 請求一覧表対象明細DTO
     * @return 請求一覧表Entityのリスト
     */
    private List<S07T001TD1501R21Entity> getSeikyuList(D1501R21PrintDto printDto,
        D1501R21TargetMeisaiDto meisaiDto) {
        S07T001TD1501R21Entity entity = createS07T001TD1501R21Entity(printDto, meisaiDto);
        List<S07T001TD1501R21Entity> selectResultList =
            s07T001Dao.selectForD1501R21List(entity, false);
        return selectResultList;
    }

    /**
     * 請求一覧表印刷データ取得Entityを生成し返却する
     * 
     * @param printDto 請求一覧表印刷DTO
     * @param meisaiDto 請求一覧表対象明細DTO
     * @return 請求一覧表Entity
     */
    private S07T001TD1501R21Entity createS07T001TD1501R21Entity(D1501R21PrintDto printDto,
        D1501R21TargetMeisaiDto meisaiDto) {
        S07T001TD1501R21Entity entity = new S07T001TD1501R21Entity();
        // ＡＰ基本コード
        entity.setApkihoncd(printDto.getApkihoncd());
        // ショップコード;
        entity.setShopcd(printDto.getShopcd());
        // 請求先コード
        entity.setSkysakicd(meisaiDto.getSkysakicd());
        // 締年月
        entity.setShimedydm(printDto.getShimedydm());
        // 締日
        entity.setShimedd(printDto.getShimedd());
        // 請求締年月日
        entity.setSkydydmdd(printDto.getShimedydmdd());
        // 有効区分
        entity.setYukokbn(CommonConstants.YUKOKBN_1);
        return entity;
    }

    /**
     * 請求一覧表出力データ設定処理で使用する各金額の初期化を行う
     * 
     * @return HashMap(出力データ名, BigDecimal型の0)
     */
    private Map<String, BigDecimal> initOutputDate() {
        Map<String, BigDecimal> wkOutputDate = new HashMap<String, BigDecimal>();
        wkOutputDate.put("wk前回請求額", BigDecimal.ZERO);
        wkOutputDate.put("wk当月入金額", BigDecimal.ZERO);
        wkOutputDate.put("wk当月売上額", BigDecimal.ZERO);
        wkOutputDate.put("wk消費税額", BigDecimal.ZERO);
        wkOutputDate.put("wk繰越金額", BigDecimal.ZERO);
        wkOutputDate.put("wk現在残高", BigDecimal.ZERO);
        return wkOutputDate;
    }

    /**
     * 請求一覧表出力データ設定処理の金額計算を行う
     * 
     * @param originalValue 元の数値
     * @param addValue 加える数値
     * @return 元の数値と加える数値の和 加える数値をBigDecimal型に変換できない場合、元の数値を返却
     */
    private BigDecimal getSum(BigDecimal originalValue, String addValue) {
        BigDecimal sum = BigDecimal.ZERO;
        try {
            sum = originalValue.add(new BigDecimal(addValue));
        } catch (Exception e) {
            sum = originalValue;
        }
        return sum;
    }

    /**
     * 請求一覧表出力データ設定処理の親子区分設定に設定する値を返却する
     * 
     * @param skyKbn 請求一覧表Entity.請求区分
     * @return 請求区分2の場合2(子) 請求区分2以外の場合3(一般)を返却
     */
    private String getOyakoKbn(String skyKbn) {
        return CommonConstants.SKYKBN_2.equals(skyKbn) ? OYAKO_KBN_KODOMO : OYAKO_KBN_IPPAN;
    }


    /**
     * 請求一覧表出力データ設定処理
     * 
     * @param printDto 請求一覧表印刷DTO
     * @param seikyuListEntity 請求一覧表Entity
     * @param kaishuHouhoMap 回収方法区分名称
     * @param csvList CSVリスト
     */
    private void getSeikyuListPrintData(D1501R21PrintDto printDto,
        List<S07T001TD1501R21Entity> seikyuListEntity, Map<String, String> kaishuHouhoMap,
        List<List<String>> csvList) {
        List<Map<String, String>> chukanCsvList = new ArrayList<Map<String, String>>();
        Map<String, BigDecimal> wkShutsuryoku = initOutputDate();
        Map<String, String> wkOyacsv = null;

        if (CommonConstants.SKYKBN_2.equals(seikyuListEntity.get(0).getSkykbn())) {
            wkOyacsv = new HashMap<String, String>();
            wkOyacsv.put("ショップ名称", ItemUtil.nullToString(seikyuListEntity.get(0).getShopnm()));
            wkOyacsv.put("事業所コード", ItemUtil.nullToString(seikyuListEntity.get(0).getJigyoshocd()));
            wkOyacsv.put("事業所名称", ItemUtil.nullToString(seikyuListEntity.get(0).getJigyoshonm()));
            wkOyacsv.put("営業担当者名",
                getTnshamei(printDto.getEigyotnshacd(), seikyuListEntity.get(0).getTnshamei1()));
            wkOyacsv.put("回収担当者名",
                getTnshamei(printDto.getKaishutnshacd(), seikyuListEntity.get(0).getTnshamei2()));
            wkOyacsv.put("消費税項目名称", ItemUtil.nullToString(seikyuListEntity.get(0).getSyouhikin()));
            wkOyacsv.put("請求先コード", ItemUtil.nullToString(seikyuListEntity.get(0).getSkysakicd()));
            wkOyacsv.put("請求先名", ItemUtil.nullToString(seikyuListEntity.get(0).getSkysakinmn1()));
            wkOyacsv.put("回収方法", ItemUtil.nullToString(seikyuListEntity.get(0).getKaishuhohokbn()));
            wkOyacsv.put("決済機関", ItemUtil.nullToString(seikyuListEntity.get(0).getKsikkncd()));
            wkOyacsv.put("滞留区分", ItemUtil.nullToString(seikyuListEntity.get(0).getTairyukbn()));
            wkOyacsv.put("滞留区分名称", ItemUtil.nullToString(seikyuListEntity.get(0).getTairyukbnnm()));
            wkOyacsv.put("滞留金額", seikyuListEntity.get(0).getTairyukin());
            wkOyacsv.put("親子区分判定", OYAKO_KBN_OYA);
        }

        for (S07T001TD1501R21Entity seikyuList : seikyuListEntity) {
            Map<String, String> wkcsv = new HashMap<String, String>();
            wkcsv.put("ショップ名称", ItemUtil.nullToString(seikyuList.getShopnm()));
            wkcsv.put("事業所コード", ItemUtil.nullToString(seikyuList.getJigyoshocd()));
            wkcsv.put("事業所名称", ItemUtil.nullToString(seikyuList.getJigyoshonm()));
            wkcsv.put("営業担当者名", getTnshamei(printDto.getEigyotnshacd(), seikyuList.getTnshamei1()));
            wkcsv.put("回収担当者名",
                getTnshamei(printDto.getKaishutnshacd(), seikyuList.getTnshamei2()));
            wkcsv.put("消費税項目名称", ItemUtil.nullToString(seikyuList.getSyouhikin()));
            wkcsv.put("請求先コード", ItemUtil.nullToString(seikyuList.getSkysakicd()));
            wkcsv.put("得意先状態", ItemUtil.nullToString(seikyuList.getTokuisakijotai()));
            wkcsv.put("得意先コード", ItemUtil.nullToString(seikyuList.getTokuisakicd()));
            wkcsv.put("請求先名", ItemUtil.nullToString(seikyuList.getSkysakinmn1()));
            wkcsv.put("得意先名", ItemUtil.nullToString(seikyuList.getTokuisakinmn1()));
            wkcsv.put("前回繰越金額", ItemUtil.nullToString(seikyuList.getZnkkurikoshikin()));
            wkcsv.put("今回入金金額", ItemUtil.nullToString(seikyuList.getKonkainyknkin()));
            wkcsv.put("当月売上額", ItemUtil.nullToString(seikyuList.getKin()));
            wkcsv.put("今回消費税金額", ItemUtil.nullToString(seikyuList.getKonkaishohitaxkin()));
            wkcsv.put("繰越金額", ItemUtil.nullToString(seikyuList.getKurikoshikin()));
            wkcsv.put("回収方法", ItemUtil.nullToString(seikyuList.getKaishuhohokbn()));
            wkcsv.put("決済機関", ItemUtil.nullToString(seikyuList.getKsikkncd()));
            wkcsv.put("滞留区分", ItemUtil.nullToString(seikyuList.getTairyukbn()));
            wkcsv.put("滞留区分名称", ItemUtil.nullToString(seikyuList.getTairyukbnnm()));
            wkcsv.put("滞留金額", seikyuList.getTairyukin());
            wkcsv.put("現在残高", seikyuList.getGenzaizd());
            wkcsv.put("親子区分判定", getOyakoKbn(seikyuList.getSkykbn()));
            chukanCsvList.add(wkcsv);

            if (wkOyacsv != null) {
                wkShutsuryoku.put("wk前回請求額", getSum(wkShutsuryoku.get("wk前回請求額"),
                    seikyuList.getZnkkurikoshikin().toString()));
                wkShutsuryoku.put("wk当月入金額",
                    getSum(wkShutsuryoku.get("wk当月入金額"), seikyuList.getKonkainyknkin()));
                wkShutsuryoku.put("wk当月売上額",
                    getSum(wkShutsuryoku.get("wk当月売上額"), seikyuList.getKin()));
                wkShutsuryoku.put("wk消費税額",
                    getSum(wkShutsuryoku.get("wk消費税額"), seikyuList.getKonkaishohitaxkin()));
                wkShutsuryoku.put("wk繰越金額",
                    getSum(wkShutsuryoku.get("wk繰越金額"), seikyuList.getKurikoshikin()));
                wkShutsuryoku.put("wk現在残高",
                    getSum(wkShutsuryoku.get("wk現在残高"), seikyuList.getGenzaizd()));
            }
        }

        if (wkOyacsv != null) {
            wkOyacsv.put("前回繰越金額", wkShutsuryoku.get("wk前回請求額").toString());
            wkOyacsv.put("今回入金金額", wkShutsuryoku.get("wk当月入金額").toString());
            wkOyacsv.put("当月売上額", wkShutsuryoku.get("wk当月売上額").toString());
            wkOyacsv.put("今回消費税金額", wkShutsuryoku.get("wk消費税額").toString());
            wkOyacsv.put("繰越金額", wkShutsuryoku.get("wk繰越金額").toString());
            wkOyacsv.put("現在残高", wkShutsuryoku.get("wk現在残高").toString());
            chukanCsvList.add(0, wkOyacsv);
        }
        for (Map<String, String> chukanCsv : chukanCsvList) {
            List<String> csvData = makeCsvDataList(printDto, chukanCsv, kaishuHouhoMap);
            csvList.add(csvData);
        }
    }

    /**
     * 請求一覧表出力行データ生成処理
     * 
     * @param printDto 請求一覧表印刷DTO
     * @param chukanCsv 中間CSVマップ
     * @param kaishuHouho 回収方法区分
     * @return 出力データ
     */
    private List<String> makeCsvDataList(D1501R21PrintDto printDto, Map<String, String> chukanCsv,
        Map<String, String> kaishuHouho) {
        List<String> csvdata = new ArrayList<>();
        csvdata.add(ListCreatorUtil.wrapQuote(D1501R00Constants.PROGRAM_ID_SEIKYUITIRAN));
        csvdata.add(ListCreatorUtil.wrapQuote(printDto.getApkihoncd()));
        csvdata.add(ListCreatorUtil.wrapQuote(printDto.getShopcd()));
        csvdata.add(ListCreatorUtil.wrapQuote(chukanCsv.get("ショップ名称")));
        csvdata.add(ListCreatorUtil.wrapQuote(chukanCsv.get("事業所コード")));
        csvdata.add(ListCreatorUtil.wrapQuote(chukanCsv.get("事業所名称")));
        csvdata.add(ListCreatorUtil.wrapQuote(printDto.getLoginUsertnshacd()));
        csvdata.add(ListCreatorUtil.wrapQuote(printDto.getLoginUsertnshamei()));
        csvdata.add(ListCreatorUtil.wrapQuote(chukanCsv.get("営業担当者名")));
        csvdata.add(ListCreatorUtil.wrapQuote(chukanCsv.get("回収担当者名")));
        String kaishuhouhoName = getKaishuhouhoName(kaishuHouho, printDto.getKaishuhoho());
        csvdata.add(ListCreatorUtil.wrapQuote(kaishuhouhoName));
        csvdata.add(ListCreatorUtil.wrapQuote(printDto.getShimedydmdd()));
        csvdata.add(ListCreatorUtil.wrapQuote(chukanCsv.get("消費税項目名称")));
        csvdata.add(ListCreatorUtil.wrapQuote(chukanCsv.get("請求先コード")));
        csvdata.add(ListCreatorUtil.wrapQuote(chukanCsv.get("得意先状態")));
        csvdata.add(ListCreatorUtil.wrapQuote(chukanCsv.get("得意先コード")));
        String skysakiName = getSkysakiName(chukanCsv);
        csvdata.add(ListCreatorUtil.wrapQuote(skysakiName));
        csvdata.add(chukanCsv.get("前回繰越金額"));
        csvdata.add(chukanCsv.get("今回入金金額"));
        csvdata.add(chukanCsv.get("当月売上額"));
        csvdata.add(chukanCsv.get("今回消費税金額"));
        csvdata.add(chukanCsv.get("繰越金額"));
        csvdata.add(ListCreatorUtil.wrapQuote(kaishuHouho.get(chukanCsv.get("回収方法"))));
        csvdata.add(ListCreatorUtil.wrapQuote(chukanCsv.get("決済機関")));
        csvdata.add(ListCreatorUtil.wrapQuote(chukanCsv.get("滞留区分名称")));
        csvdata.add(getKingakuValue(chukanCsv.get("滞留金額")));
        csvdata.add("");
        csvdata.add(ListCreatorUtil.wrapQuote(chukanCsv.get("親子区分判定")));
        return csvdata;

    }

    /**
     * 金額値取得
     * 
     * @param kingaku 対象金額
     * @return 金額値（null又は空値の場合は"0"を返却する）
     */
    private String getKingakuValue(String kingaku) {
        return StringUtils.isEmpty(kingaku) ? "0" : kingaku;
    }

    /**
     * 回収方法名称を取得する
     * 
     * @param kaishuHouho 回収方法HashMap
     * @param kaishuhohokbnList 回収方法HashMapのキー
     * @return 回収方法名称
     */
    private String getKaishuhouhoName(Map<String, String> kaishuHouho,
        List<String> kaishuhohokbnList) {
        List<String> kaishuhouhoNameList = new ArrayList<String>();
        for (String kaishuhohokbn : kaishuhohokbnList) {
            kaishuhouhoNameList.add(kaishuHouho.get(kaishuhohokbn));
        }
        return String.join("/", kaishuhouhoNameList);
    }

    /**
     * 請求先名を取得する
     * 
     * @param chukanCsvList 中間CSV作成用のデータリスト
     * @return 親子区分判定が子（2）の場合、得意先 子以外の場合は請求先を返却する
     */
    private String getSkysakiName(Map<String, String> chukanCsvList) {
        if (OYAKO_KBN_KODOMO.equals(chukanCsvList.get("親子区分判定"))) {
            return chukanCsvList.get("得意先名");
        } else {
            return chukanCsvList.get("請求先名");
        }
    }

    /**
     * 担当者名称を取得する
     * 
     * @param tnshacd 担当者コード
     * @param tnshamei 担当者名称
     * @return 担当者コードがnullの場合は空文字、そうでない場合引数の担当者名称を返す
     */
    private String getTnshamei(String tnshacd, String tnshamei) {
        return tnshacd == null ? "" : ItemUtil.nullToString(tnshamei);
    }
}
