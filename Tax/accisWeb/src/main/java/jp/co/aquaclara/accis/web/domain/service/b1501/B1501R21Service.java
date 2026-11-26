package jp.co.aquaclara.accis.web.domain.service.b1501;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.thymeleaf.util.ListUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S06T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S06T002TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.Z1808B01Dao;
import jp.co.aquaclara.accis.common.domain.repository.entity.Z1808B01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S06T001TB1501R21Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S06T002TB1501R21Entity;
import jp.co.aquaclara.accis.common.exception.BusinessException;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.common.util.ListCreatorUtil;
import jp.co.aquaclara.accis.web.domain.dto.b1501.B1501W21PrintConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.b1501.B1501W21PrintMeisaiDto;
import jp.co.aquaclara.accis.web.domain.service.common.OnlineBatchBaseService;
import jp.co.aquaclara.accis.web.domain.service.common.OnlineBatchCommonModel;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;

/**
 * 請求入金明細生成サービス
 * 
 * @author accisdev003
 */
@Service
public class B1501R21Service extends OnlineBatchBaseService<B1501W21PrintConditionDto> {

    /**
     * 売上テーブルDao
     */
    @Autowired
    private S06T001TDao s06t001tDao;
    /**
     * 売上明細テーブルDao
     */
    @Autowired
    private S06T002TDao s06t002tDao;
    /**
     * 消費税金額計算バッチDao
     */
    @Autowired
    private Z1808B01Dao z1808b01Dao;
    /**
     * 処理ID
     */
    private final String processId = "B1501B01";
    /**
     * 印刷用プログラムID
     */
    private final String PRNT_PROCESS_ID = "b1501r21";
    /**
     * 空文字列
     */
    private final String BLANK = "";
    /**
     * 開始括弧
     */
    private final String PARENTHESIS_START = "(";
    /**
     * 終了括弧
     */
    private final String PARENTHESIS_END = ")";
    /**
     * パーセント
     */
    private final String PERCENT = "%";
    /**
     * 数値：0
     */
    private final String STR_ZERO = "0";

    /**
     * 処理ID取得
     */
    @Override
    public String getProcessId() {
        return this.processId;
    }

    /**
     * 非同期処理前のチェック処理
     * 
     * @param dto B1501R21PrintConditionDto
     */
    public void processBeforeCheck(B1501W21PrintConditionDto dto) {
        // 検索結果件数チェック
        List<S06T001TB1501R21Entity> resultList =
            s06t001tDao.selectByB1501R21(createCustomEntity(dto));
        // 検索結果が0件の場合
        if (ObjectUtils.isEmpty(resultList)) {
            throw BusinessExceptionFactory.dataNotFound(MessageID.W083);
        }
    }

    /**
     * 請求入金明細生成
     * 
     * @param paramModel 請求入金照会(詳細)印刷処理用DTO
     * @param commonInfo OnlineBatchCommonModel
     */
    @Override
    protected void process(B1501W21PrintConditionDto paramModel, OnlineBatchCommonModel commonInfo)
        throws BusinessException, Exception {
        // DBデータ取得
        List<S06T001TB1501R21Entity> resultList =
            s06t001tDao.selectByB1501R21(createCustomEntity(paramModel));
        // 検索結果が0件の場合(事前チェック後～本検索処理の間にデータ削除された場合のみエラー)
        if (ObjectUtils.isEmpty(resultList)) {
            throw BusinessExceptionFactory.dataNotFound(MessageID.W083);
        }
        // 検索結果の設定
        List<List<String>> dataList = new ArrayList<List<String>>();
        dataList.addAll(createCsvDataList(paramModel, resultList));
        // 帳票(PDFファイル)を出力
        super.createReportFile(dataList, commonInfo.getFileNamne(), null);
    }

    /**
     * 検索条件設定処理
     * 
     * @param dto 請求入金照会(詳細)印刷処理用DTO
     * @return 売上入金データ検索用Entity
     */
    private S06T001TB1501R21Entity createCustomEntity(B1501W21PrintConditionDto dto) {
        S06T001TB1501R21Entity entity = new S06T001TB1501R21Entity();
        entity.setDydmddflag(dto.getDydmddflag()); // 年月日フラグ
        entity.setApkihoncd(dto.getApkihoncd()); // AP基本コード
        entity.setShopcd(dto.getShopcd()); // ショップコード
        entity.setSkysakicd(dto.getSkysakicd()); // 請求先コード
        entity.setDd(dto.getShimedd()); // 請求締日
        entity.setInsatflag(dto.getInsatflag()); // 印刷対象フラグ
        entity.setUriagenyukinsentaku(dto.getUriagenyukincnclsentaku()); // 売上入金取消選択
        entity.setShrsaito(dto.getShrsaito()); // 支払サイト
        entity.setRenban(dto.getRenban()); // 連番

        // 明細データの設定
        setMeisaiData(dto.getMeisaidata(), entity);

        return entity;
    }

    /**
     * 検索用明細データ設定処理
     * 
     * @param meisaiDtoList 請求入金照会(詳細)印刷処理用明細DTO
     * @param entity 売上入金データ検索用Entity
     */
    private void setMeisaiData(List<B1501W21PrintMeisaiDto> meisaiDtoList,
        S06T001TB1501R21Entity entity) {
        // 明細データが存在する場合のみ請求締年月日、得意先コードを設定
        if (!ListUtils.isEmpty(meisaiDtoList)) {
            entity.setDydm(meisaiDtoList.get(0).getDydmmei().substring(0, 6)); // 請求締年月
            entity.setDd(meisaiDtoList.get(0).getDydmmei().substring(6)); // 請求締日

            // 得意先コードリスト設定
            List<String> tokuisakicdList = new ArrayList<>();
            // 明細データの件数分繰り返し
            for (B1501W21PrintMeisaiDto meisaiDto : meisaiDtoList) {
                // entity設定用変数に得意先コードを追加
                tokuisakicdList.add(meisaiDto.getTokuisakicdmei());
            }
            entity.setTokuisakicdlist(tokuisakicdList); // 得意先コードリスト
        }
    }

    /**
     * CSVデータ設定処理
     * 
     * @param dto 請求入金照会(詳細)印刷処理用DTO
     * @param entityList 売上入金データEntityリスト
     * @return CSVデータリスト
     */
    private List<List<String>> createCsvDataList(B1501W21PrintConditionDto dto,
        List<S06T001TB1501R21Entity> entityList) {
        // 返却用リスト
        List<List<String>> resultList = new ArrayList<List<String>>();

        // 消費税データ生成用Map
        Map<String, Integer> n8perMap = new HashMap<String, Integer>();
        Map<String, Integer> n10perMap = new HashMap<String, Integer>();

        // 売上入金データEntityの明細単位処理
        for (S06T001TB1501R21Entity entity : entityList) {
            // 税区分が「5:子商品明細」の場合、子商品データを取得して設定
            if (CommonConstants.KBN_TAX_KOMEISAI.equals(entity.getTaxkbn())) {
                setChildData(entity);
            }
            // CSVデータ設定用リスト
            List<String> columns = new ArrayList<String>();
            // 画面表示商品データリスト(年月日,事業所コード,事業所名称,前月残高,当月入金額,当月売上額,消費税額,請求金額)
            List<String> meisaiDataList = createMeisaiData(dto.getMeisaidata(), entity);

            columns.add(ListCreatorUtil.wrapQuote(PRNT_PROCESS_ID)); // プログラムID
            columns.add(ListCreatorUtil.wrapQuote(dto.getApkihoncd())); // AP基本コード
            columns.add(ListCreatorUtil.wrapQuote(dto.getShopcd())); // ショップコード
            columns.add(ListCreatorUtil.wrapQuote(dto.getShopnm())); // ショップ名称
            columns.add(ListCreatorUtil
                .wrapQuote(!ListUtils.isEmpty(meisaiDataList) ? meisaiDataList.get(1) : BLANK)); // 事業所コード
            columns.add(ListCreatorUtil
                .wrapQuote(!ListUtils.isEmpty(meisaiDataList) ? meisaiDataList.get(2) : BLANK)); // 事業所名称
            columns.add(ListCreatorUtil.wrapQuote(dto.getTnshacd())); // 印刷指示担当者コード
            columns.add(ListCreatorUtil.wrapQuote(dto.getTnshamei())); // 印刷指示担当者名
            columns.add(ListCreatorUtil.wrapQuote(dto.getApkihoncd())); // AP基本コード2
            columns.add(ListCreatorUtil.wrapQuote(dto.getShopcd())); // ショップコード2
            columns.add(ListCreatorUtil.wrapQuote(dto.getSkysakicd())); // 請求先コード
            // 請求区分名称加工処理
            String skykbnnm = !StringUtils.isEmpty(dto.getSkykbnnm())
                ? PARENTHESIS_START.concat(dto.getSkykbnnm().concat(PARENTHESIS_END))
                : null;
            columns.add(ListCreatorUtil.wrapQuote(skykbnnm)); // 請求先区分名称
            columns.add(ListCreatorUtil.wrapQuote(dto.getSkysakinmkana())); // 請求先カナ名称
            columns.add(ListCreatorUtil.wrapQuote(dto.getSkysakinmn())); // 請求先名称1
            columns.add(ListCreatorUtil.wrapQuote(dto.getShimedd())); // 締日1
            columns.add(ListCreatorUtil.wrapQuote(entity.getShrsaito())); // 支払サイト
            columns.add(ListCreatorUtil.wrapQuote(dto.getShrdd())); // 支払日
            columns.add(ListCreatorUtil.wrapQuote(dto.getKaishuhohokbnnm())); // 回収方法区分名称
            columns.add(ListCreatorUtil.wrapQuote(
                !StringUtils.isEmpty(dto.getGenzaizandaka()) ? dto.getGenzaizandaka() : STR_ZERO)); // 現在残高
            columns.add(ListCreatorUtil.wrapQuote(entity.getTairyukbn())); // 滞留区分名称
            columns.add(ListCreatorUtil.wrapQuote(dto.getTairyukin())); // 滞留金額
            columns.add(ListCreatorUtil.wrapQuote(dto.getSkysakiyubinno().substring(0, 3) + "-"
                + dto.getSkysakiyubinno().substring(3, 7))); // 請求先郵便番号
            columns.add(ListCreatorUtil.wrapQuote(dto.getAdrs1())); // 請求先住所1
            columns.add(ListCreatorUtil.wrapQuote(dto.getAdrs2())); // 請求先住所2
            columns.add(ListCreatorUtil.wrapQuote(dto.getAdrs3())); // 請求先住所3
            columns.add(ListCreatorUtil.wrapQuote(dto.getRenrakusakinon())); // 連絡先番号1
            columns.add(ListCreatorUtil.wrapQuote(dto.getGaiburenkeicd())); // 外部連携コード
            columns.add(ListCreatorUtil.wrapQuote(dto.getEigyotnshamei())); // 営業担当者名
            columns.add(ListCreatorUtil
                .wrapQuote(!ListUtils.isEmpty(meisaiDataList) ? meisaiDataList.get(0) : BLANK)); // 請求年月日
            columns.add(ListCreatorUtil
                .wrapQuote(!ListUtils.isEmpty(meisaiDataList) ? meisaiDataList.get(3) : BLANK)); // 前月残高
            columns.add(ListCreatorUtil
                .wrapQuote(!ListUtils.isEmpty(meisaiDataList) ? meisaiDataList.get(4) : BLANK)); // 当月入金金額
            columns.add(ListCreatorUtil
                .wrapQuote(!ListUtils.isEmpty(meisaiDataList) ? meisaiDataList.get(5) : BLANK)); // 当月売上金額
            columns.add(ListCreatorUtil
                .wrapQuote(!ListUtils.isEmpty(meisaiDataList) ? meisaiDataList.get(6) : BLANK)); // 消費税金額
            columns.add(ListCreatorUtil
                .wrapQuote(!ListUtils.isEmpty(meisaiDataList) ? meisaiDataList.get(8) : BLANK)); // 税込ご利用額計
            columns.add(ListCreatorUtil
                .wrapQuote(!ListUtils.isEmpty(meisaiDataList) ? meisaiDataList.get(7) : BLANK)); // 請求金額
            columns.add(ListCreatorUtil.wrapQuote(entity.getYmd())); // 日付
            columns.add(ListCreatorUtil.wrapQuote(entity.getPyno())); // 伝票番号
            columns.add(ListCreatorUtil.wrapQuote(entity.getShncd())); // 商品コード
            // 商品名称加工処理
            String shnnm = ItemUtil.getItem(entity.getGenzshnnmryakusho(),
                entity.getShinshnnmryakusho(), entity.getShnnmkirikaedd(), entity.getYmd());
            columns.add(ListCreatorUtil.wrapQuote(
                StringUtils.isEmpty(entity.getNyknkin()) ? shnnm : entity.getNyukinkindnm())); // 商品名称
            columns.add(ListCreatorUtil.wrapQuote(entity.getIro())); // 色
            columns.add(ListCreatorUtil.wrapQuote(entity.getSznm())); // サイズ
            columns.add(ListCreatorUtil.wrapQuote(entity.getKatasiki())); // 型式
            columns.add(ListCreatorUtil.wrapQuote(entity.getKeijokbnnm())); // 計上区分
            columns.add(ListCreatorUtil.wrapQuote(entity.getSuryo())); // 数量
            columns.add(ListCreatorUtil.wrapQuote(entity.getUritrtskitaninm())); // 単位
            columns.add(ListCreatorUtil.wrapQuote(entity.getUnt())); // 単価
            columns.add(ListCreatorUtil.wrapQuote(calcUrikin(dto, entity))); // 売上金額
            // 税率加工処理
            String shohitax =
                !StringUtils.isEmpty(entity.getShohitax()) ? entity.getShohitax().concat(PERCENT)
                    : null;
            columns.add(ListCreatorUtil.wrapQuote(shohitax)); // 税率
            columns.add(ListCreatorUtil
                .wrapQuote(!StringUtils.isEmpty(entity.getShohitaxkin()) ? entity.getShohitaxkin()
                    : STR_ZERO)); // 消費税金額
            columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 税込売上金額
            columns.add(ListCreatorUtil.wrapQuote(
                !StringUtils.isEmpty(entity.getNyknkin()) ? entity.getNyknkin() : STR_ZERO)); // 入金金額
            columns.add(ListCreatorUtil.wrapQuote(entity.getBiko())); // 明細備考
            columns.add(ListCreatorUtil.wrapQuote(entity.getTokuisakicd())); // 得意先コード
            columns.add(ListCreatorUtil.wrapQuote(entity.getTokuisakinmn1())); // 得意先名称
            columns.add(ListCreatorUtil
                .wrapQuote(calcPercentUriKin(dto.getTaxkeisankbn(), entity.getSkysakikanrikbn(),
                    entity.getTaxkbn(), entity.getN8kin(), entity.getN8shohitaxkin()))); // 8%売上金額
            columns.add(ListCreatorUtil.wrapQuote(
                !StringUtils.isEmpty(entity.getN8shohitaxkin()) ? entity.getN8shohitaxkin() : "0")); // 8%消費税金額（空またはNULLの場合"0"を設定）
            columns.add(ListCreatorUtil
                .wrapQuote(calcPercentUriKin(dto.getTaxkeisankbn(), entity.getSkysakikanrikbn(),
                    entity.getTaxkbn(), entity.getN10kin(), entity.getN10shohitaxkin()))); // 10%売上金額
            columns.add(ListCreatorUtil.wrapQuote(
                !StringUtils.isEmpty(entity.getN10shohitaxkin()) ? entity.getN10shohitaxkin()
                    : "0")); // 10%消費税金額（空またはNULLの場合"0"を設定）
            columns.add(ListCreatorUtil.wrapQuote(dto.getTaxkeisankbn())); // 税計算区分

            resultList.add(columns);

            // 税率が「8」の場合
            if (CommonConstants.TAX_8.equals(entity.getShohitax())) {
                // 8%用Mapに設定済の得意先コードの場合
                if (n8perMap.containsKey(entity.getTokuisakicd())) {
                    // 設定済の金額に処理中データの金額を加算
                    n8perMap.replace(entity.getTokuisakicd(),
                        n8perMap.get(entity.getTokuisakicd()) + Integer.parseInt(entity.getKin()));
                    // 8%用Mapに未設定の得意先コードの場合
                } else {
                    // 8%用Mapに処理中データの得意先コード,金額を設定
                    n8perMap.put(entity.getTokuisakicd(), Integer.parseInt(entity.getKin()));
                }
                // 税率が「10」の場合
            } else if (CommonConstants.TAX_10.equals(entity.getShohitax())) {
                // 10%用Mapに設定済の得意先コードの場合
                if (n10perMap.containsKey(entity.getTokuisakicd())) {
                    // 設定済の金額に処理中データの金額を加算
                    n10perMap.replace(entity.getTokuisakicd(),
                        n10perMap.get(entity.getTokuisakicd()) + Integer.parseInt(entity.getKin()));
                    // 10%用Mapに未設定の得意先コードの場合
                } else {
                    // 10%用Mapに処理中データの得意先コード,金額を設定
                    n10perMap.put(entity.getTokuisakicd(), Integer.parseInt(entity.getKin()));
                }
            }
        }

        // 年月日フラグが「1:現在」、かつインボイス施行日<=運用年月+締日の場合
        if (CommonConstants.FLG_ON.equals(dto.getDydmddflag()) && DateTimeUtil
            .isLater(dto.getInvoicshikoymd(), dto.getUnyodydm().concat(dto.getShimedd()))) {
            // 返却用リストに8%消費税データを追加
            resultList.add(
                createShohitaxLine(dto.getTaxhasukeisankbn(), CommonConstants.TAX_8, n8perMap));
            // 返却用リストに10%消費税データを追加
            resultList.add(
                createShohitaxLine(dto.getTaxhasukeisankbn(), CommonConstants.TAX_10, n10perMap));
        }
        return resultList;
    }

    /**
     * 子明細データ設定処理
     * 
     * @param entity 売上入金データEntity
     */
    private void setChildData(S06T001TB1501R21Entity entity) {
        // 売上明細テーブル検索処理
        S06T002TB1501R21Entity resultEntity =
            s06t002tDao.selectByB1501R21(createS06T002TSearchEntity(entity));
        // 検索結果が存在する場合
        if (!ObjectUtils.isEmpty(resultEntity)) {
            entity.setN8kin(resultEntity.getN8kin()); // 8%売上金額
            entity.setN8shohitaxkin(resultEntity.getN8shohitaxkin()); // 8%消費税金額
            entity.setN10kin(resultEntity.getN10kin()); // 10%売上金額
            entity.setN10shohitaxkin(resultEntity.getN10shohitaxkin()); // 10%消費税金額
        }
    }

    /**
     * 売上明細テーブル検索用Entity生成処理
     * 
     * @param entity 売上入金データEntity
     * @return 売上明細テーブルデータ(子明細)
     */
    private S06T002TB1501R21Entity createS06T002TSearchEntity(S06T001TB1501R21Entity entity) {
        S06T002TB1501R21Entity searchEntity = new S06T002TB1501R21Entity();
        searchEntity.setApkihoncd(entity.getApkihoncd()); // AP基本コード
        searchEntity.setShopcd(entity.getShopcd()); // ショップコード
        searchEntity.setUridnpyno(entity.getUridnpyno()); // 売上伝票番号
        searchEntity.setEdano(entity.getEdano()); // 枝番号
        searchEntity.setUridnpymeisaino(Integer.parseInt(entity.getUridnpymeisaino())); // 売上伝票明細番号
        searchEntity.setUridate(entity.getYmd()); // 売上日

        return searchEntity;
    }

    /**
     * 画面表示得意先データ取得処理
     * 
     * @param meisaiDtoList 請求入金照会(詳細)印刷処理用明細DTO
     * @param entity 売上入金データEntity
     * @return 対象得意先データ(年月日,事業所コード,事業所名称,前月残高,当月入金額,当月売上額,消費税額,請求入金額)
     */
    private List<String> createMeisaiData(List<B1501W21PrintMeisaiDto> meisaiDtoList,
        S06T001TB1501R21Entity entity) {
        List<String> returnList = new ArrayList<String>();
        // 得意先データの件数分繰り返し
        for (B1501W21PrintMeisaiDto meisaiDto : meisaiDtoList) {
            returnList.add(meisaiDto.getDydmmei()); // 年月日
            returnList.add(!StringUtils.isEmpty(meisaiDto.getJigyoshocdmei())
                && !CommonConstants.JIGYOSHOCD_00.equals(meisaiDto.getJigyoshocdmei())
                    ? meisaiDto.getJigyoshocdmei()
                    : BLANK); // 事業所コード
            returnList.add(meisaiDto.getJigyoshonmmei()); // 事業所名称
            returnList.add(meisaiDto.getZngtzndkmei()); // 前月残高
            returnList.add(meisaiDto.getTogtnyknkinmei()); // 当月入金額
            returnList.add(meisaiDto.getTogturagkinmei()); // 当月売上額
            returnList.add(meisaiDto.getShohitaxkinmei()); // 消費税額
            returnList.add(meisaiDto.getSkykinmei()); // 請求金額
            returnList.add(String.valueOf(Integer.parseInt(meisaiDto.getTogturagkinmei())
                + Integer.parseInt(meisaiDto.getShohitaxkinmei()))); // 当月売上額+消費税額
            break;
        }
        return returnList;
    }

    /**
     * 売上金額計算処理
     * 
     * @param dto 請求入金照会(詳細)印刷処理用DTO
     * @param entity 売上入金データEntity
     * @return 計算後の売上金額
     */
    private String calcUrikin(B1501W21PrintConditionDto dto, S06T001TB1501R21Entity entity) {
        Integer returnKin = 0;

        // 請求先管理区分が「2:卸」の場合
        if (CommonConstants.SKYSAKIKANRIKBN_2.equals(entity.getSkysakikanrikbn())) {
            // 税計算区分が「1:内税」の場合
            if (CommonConstants.KBN_TAXKEISAN_IN.equals(dto.getTaxkeisankbn())) {
                // 金額が設定値あり
                if (!StringUtils.isEmpty(entity.getKin())) {
                    // 消費税金額が設定値あり
                    if (!StringUtils.isEmpty(entity.getShohitaxkin())) {
                        // 売上金額=金額+消費税金額
                        returnKin = Integer.parseInt(entity.getKin())
                            + Integer.parseInt(entity.getShohitaxkin());
                        // 消費税金額が未設定
                    } else {
                        // 売上金額=金額
                        returnKin = Integer.parseInt(entity.getKin());
                    }
                    // 金額が未設定、かつ消費税金額が設定値あり
                } else if (!StringUtils.isEmpty(entity.getShohitaxkin())) {
                    // 売上金額 = 消費税金額
                    returnKin = Integer.parseInt(entity.getShohitaxkin());
                }
            } else {
                // 金額が設定値あり
                if (!StringUtils.isEmpty(entity.getKin())) {
                    returnKin = Integer.parseInt(entity.getKin());
                }
            }
            // 請求先管理区分が「2：卸」以外、かつ税計算区分が「1：内税」の場合
        } else if (CommonConstants.KBN_TAXKEISAN_IN.equals(dto.getTaxkeisankbn())) {
            // 金額が設定値あり
            if (!StringUtils.isEmpty(entity.getKin())) {
                // 売上金額=金額
                returnKin = Integer.parseInt(entity.getKin());
                // 金額が未設定、かつ消費税金額が設定値あり
            } else if (!StringUtils.isEmpty(entity.getShohitaxkin())) {
                // 売上金額=消費税金額
                returnKin = Integer.parseInt(entity.getShohitaxkin());
            }
            // 請求先管理区分が「2：卸」以外、かつ税計算区分が「1：内税」以外、かつ金額が設定値ありの場合
        } else if (!StringUtils.isEmpty(entity.getKin())) {
            // 売上金額=金額
            returnKin = Integer.parseInt(entity.getKin());
        }
        return returnKin.toString();
    }

    /**
     * 税率単位の売上金額算出処理
     * 
     * @param taxkeisankbn 税計算区分
     * @param skysakikanrikbn 請求先管理区分
     * @param taxkbn 税区分
     * @param urikin 売上金額
     * @param shohitaxkin 消費税金額
     * @return 計算後の売上金額
     */
    private String calcPercentUriKin(String taxkeisankbn, String skysakikanrikbn, String taxkbn,
        String urikin, String shohitaxkin) {
        Integer returnKin = 0;

        // 税区分が「5:子商品明細」の場合
        if (CommonConstants.KBN_TAX_KOMEISAI.equals(taxkbn)) {
            // 請求先管理区分が「2：卸」の場合
            if (!CommonConstants.SKYSAKIKANRIKBN_2.equals(skysakikanrikbn)) {
                // 税計算区分が「1：内税」の場合
                if (CommonConstants.KBN_TAXKEISAN_IN.equals(taxkeisankbn)) {
                    // 売上金額が設定値あり
                    if (!StringUtils.isEmpty(urikin)) {
                        // 消費税金額が設定値あり
                        if (!StringUtils.isEmpty(shohitaxkin)) {
                            // 売上金額=売上金額+消費税金額
                            returnKin = Integer.parseInt(urikin) + Integer.parseInt(shohitaxkin);
                            // 消費税金額が未設定
                        } else {
                            // 売上金額=売上金額
                            returnKin = Integer.parseInt(urikin);
                        }
                        // 売上金額が未設定、かつ消費税金額が設定値あり
                    } else if (!StringUtils.isEmpty(shohitaxkin)) {
                        // 売上金額=消費税金額
                        returnKin = Integer.parseInt(shohitaxkin);
                    }
                    // 税計算区分が「1：内税」以外、かつ売上金額が設定値あり
                } else if (!StringUtils.isEmpty(urikin)) {
                    // 売上金額=売上金額
                    returnKin = Integer.parseInt(urikin);
                }
                // 請求先管理区分が「2：卸」以外、かつ売上金額が設定値あり
            } else if (!StringUtils.isEmpty(urikin)) {
                // 売上金額=売上金額
                returnKin = Integer.parseInt(urikin);
            }
            // 税区分が「5：子商品明細」以外、かつ請求先管理区分が「2：卸」の場合
        } else if (!CommonConstants.SKYSAKIKANRIKBN_2.equals(skysakikanrikbn)) {
            // 税計算区分が「1：内税」の場合
            if (CommonConstants.KBN_TAXKEISAN_IN.equals(taxkeisankbn)) {
                // 売上金額が設定値あり
                if (!StringUtils.isEmpty(urikin)) {
                    // 消費税金額が設定値あり
                    if (!StringUtils.isEmpty(shohitaxkin)) {
                        // 売上金額=売上金額+消費税金額
                        returnKin = Integer.parseInt(urikin) + Integer.parseInt(shohitaxkin);
                        // 消費税金額が未設定
                    } else {
                        // 売上金額=売上金額
                        returnKin = Integer.parseInt(urikin);
                    }
                    // 売上金額が未設定、かつ消費税金額が設定値あり
                } else if (!StringUtils.isEmpty(shohitaxkin)) {
                    // 売上金額=消費是金額
                    returnKin = Integer.parseInt(shohitaxkin);
                }
                // 税計算区分が「1：内税」以外、かつ売上金額が設定値ありの場合
            } else if (!StringUtils.isEmpty(urikin)) {
                // 売上金額=売上金額
                returnKin = Integer.parseInt(urikin);
            }
            // 税区分が「5：子商品明細」以外、かつ請求先管理区分が「2：卸」以外、かつ売上金額が設定値ありの場合
        } else if (!StringUtils.isEmpty(urikin)) {
            // 売上金額=売上金額
            returnKin = Integer.parseInt(urikin);
        }

        return returnKin.toString();
    }

    /**
     * 消費税データ生成処理
     * 
     * @param taxhasukeisankbn 税端数計算区分
     * @param taxrate 税率
     * @param kinMap 金額Map(得意先コード, 売上合計金額)
     * @return CSV出力用データ(消費税行)
     */
    private List<String> createShohitaxLine(String taxhasukeisankbn, String taxrate,
        Map<String, Integer> kinMap) {
        List<String> columns = new ArrayList<String>();

        // 消費税名称の出力判定処理
        String shinshnmeiryakusyo = null;
        if (CommonConstants.TAX_8.equals(taxrate)) {
            // 税率8%時の表示内容設定
            shinshnmeiryakusyo = "軽減消費税（仮）";
        } else {
            // 税率10%時の表示内容設定
            shinshnmeiryakusyo = "消費税（仮）";
        }

        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // プログラムID
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // AP基本コード
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // ショップコード
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // ショップ名称
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 事業所コード
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 事業所名称
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 印刷指示担当者コード
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 印刷指示担当者名
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // AP基本コード2
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // ショップコード2
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 請求先コード
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 請求先区分名称
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 請求先カナ名称
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 請求先名称1
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 締日1
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 支払サイト
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 支払日
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 回収方法区分名称
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 現在残高
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 滞留区分名称
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 滞留金額
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 請求先郵便番号
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 請求先住所1
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 請求先住所2
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 請求先住所3
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 連絡先番号1
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 外部連携コード
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 営業担当者名
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 請求年月日
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 前月残高
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 当月入金金額
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 当月売上金額
        columns.add(ListCreatorUtil
            .wrapQuote(String.valueOf(calcShohitax(taxhasukeisankbn, taxrate, kinMap)))); // 消費税金額
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 税込ご利用額計
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 請求金額
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 日付
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 伝票番号
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 商品コード
        columns.add(ListCreatorUtil.wrapQuote(shinshnmeiryakusyo)); // 商品名称
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 色
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // サイズ
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 型式
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 計上区分
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 数量
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 単位
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 単価
        columns.add(ListCreatorUtil.wrapQuote(STR_ZERO)); // 売上金額
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 税率
        columns.add(ListCreatorUtil.wrapQuote(STR_ZERO)); // 消費税金額
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 税込売上金額
        columns.add(ListCreatorUtil.wrapQuote(STR_ZERO)); // 入金金額
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 明細備考
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 得意先コード
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 得意先名称
        columns.add(ListCreatorUtil.wrapQuote(STR_ZERO)); // 8%売上金額
        columns.add(ListCreatorUtil.wrapQuote(STR_ZERO)); // 8%消費税金額
        columns.add(ListCreatorUtil.wrapQuote(STR_ZERO)); // 10%売上金額
        columns.add(ListCreatorUtil.wrapQuote(STR_ZERO)); // 10%消費税金額
        columns.add(ListCreatorUtil.wrapQuote(BLANK)); // 税計算区分

        return columns;
    }

    /**
     * 消費税計算処理
     * 
     * @param taxhasukeisankbn 税端数計算区分
     * @param taxrate 税率
     * @param kinMap 金額Map(得意先コード, 売上合計金額)
     * @return 消費税金額
     */
    private Integer calcShohitax(String taxhasukeisankbn, String taxrate,
        Map<String, Integer> kinMap) {
        Integer totalshohitaxkin = 0;

        // Mapのデータ数分繰り返し
        for (Integer kin : kinMap.values()) {

            // 消費税金額の合算
            totalshohitaxkin = totalshohitaxkin + callRoundValue(taxhasukeisankbn, taxrate, kin);
        }

        return totalshohitaxkin;
    }

    /**
     * 消費税端数計算処理
     * 
     * @param taxhasukeisankbn 税端数計算区分
     * @param taxrate 税率
     * @param totalkin 合計金額
     * @return 端数計算後の消費税金額
     */
    private Integer callRoundValue(String taxhasukeisankbn, String taxrate, Integer totalkin) {
        Z1808B01Entity entity = new Z1808B01Entity();
        // 計算対象値(合計金額 * (税率 / 100))
        entity.setValue(totalkin * (Double.parseDouble(taxrate) / 100));
        // 端数計算区分
        entity.setHasukeisankbn(taxhasukeisankbn);

        // 消費税金額計算バッチの端数計算処理呼出
        return z1808b01Dao.roundValue(entity);
    }
}
