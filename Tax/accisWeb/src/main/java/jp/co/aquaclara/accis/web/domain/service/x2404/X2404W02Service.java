package jp.co.aquaclara.accis.web.domain.service.x2404;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M006TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M007TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M008TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M013TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M017TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M046TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02V001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S04T002TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M007TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M007TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M008TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M013TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M046TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02V001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S04T002TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M008TX2404W02Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.business.S04T001TEntityDaoEx;
import jp.co.aquaclara.accis.common.domain.repository.vo.business.FileUpdloadVO;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.business.BusinessUtil;
import jp.co.aquaclara.accis.web.controller.commons.DbManagementSession;
import jp.co.aquaclara.accis.web.controller.commons.UserSessionForm;
import jp.co.aquaclara.accis.web.controller.x9901.form.X9901W03M1Form;
import jp.co.aquaclara.accis.web.domain.dto.x2404.X2404W02CategoryDto;
import jp.co.aquaclara.accis.web.domain.dto.x2404.X2404W02RegistrationConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.x2404.X2404W02SummaryDto;
import jp.co.aquaclara.accis.web.domain.service.business.SaibanService;
import jp.co.aquaclara.accis.web.domain.service.x9901.X9901W00Constants;
import jp.co.aquaclara.accis.web.domain.service.x9901.X9901W02Service;

/**
 * X2404W01_ボトルクーポン情報取込サービスクラス
 * 
 *
 */
@Service
public class X2404W02Service implements X9901W02Service {

    Logger logger = LoggerFactory.getLogger(X2404W02Service.class);

    @Autowired
    private X2404W02Repository X2404w02Repository;

    @Autowired
    private S02M006TDao s02m006tDao;

    @Autowired
    private S02M007TDao s02m007tDao;

    @Autowired
    private S02M008TDao s02m008tDao;

    @Autowired
    private S02M013TDao s02m013tDao;

    @Autowired
    private S02M017TDao s02m017tDao;

    @Autowired
    private S02M046TDao s02m046tDao;

    @Autowired
    private S02V001TDao s02v001tDao;

    @Autowired
    private S04T001TDao s04t001tDao;

    @Autowired
    private S04T002TDao s04t002tDao;

    @Autowired
    private SaibanService saibanService;

    public X2404W02Service() {
        super();
    }

    @Transactional
    public ModelMap register(ArrayList<HashMap<String, String>> csvList, FileUpdloadVO fileVo,
        UserSessionForm userSessionForm, DbManagementSession dbManagementSession, String apkihoncd,
        String shopcd) {

        ModelMap resultMap = new ModelMap();
        ArrayList<X9901W03M1Form> errorList = new ArrayList<X9901W03M1Form>();

        int fileKetasuu = 0;
        int fileSeijoprcs = 0;

        // 商品の分類コード共通設定取得
        X2404W02CategoryDto categoryDto = new X2404W02CategoryDto();
        settingCategory(categoryDto);

        for (int i = 0; i < csvList.size(); i++) {
            HashMap<String, String> myMap = csvList.get(i);
            X2404W02RegistrationConditionDto conditionDto = new X2404W02RegistrationConditionDto();

            // 受注配達テーブル取得 または 新規作成
            findOrCreateHeader(conditionDto, myMap, dbManagementSession);
            conditionDto.setCategory(categoryDto);

            // 受注明細作成：セット商品展開
            createMeisai(conditionDto, myMap, dbManagementSession);

            // 受注配達テーブル登録・更新
            if (conditionDto.getShorikbn().equals(X2404W02RegistrationConditionDto.PROCESS_NEW)) {
                X2404w02Repository.insertZyutyuHaitatuTb(conditionDto.getHeader());
            } else {
                X2404w02Repository.updateZyutyuHaitatuTb(conditionDto.getHeader(), dbManagementSession);
            }
            // 受注配達明細テーブル登録処理
            X2404w02Repository.insertZyutyuHaitatuMeisaiTb(conditionDto.getMeisaiList());
            fileSeijoprcs++;
            fileKetasuu++;
        }
        resultMap.addAttribute(X9901W00Constants.ERRORLIST, errorList);
        resultMap.addAttribute(X9901W00Constants.PRCSCNT, String.valueOf(fileKetasuu));
        resultMap.addAttribute(X9901W00Constants.SEIJOPRCS, String.valueOf(fileSeijoprcs));
        resultMap.addAttribute(X9901W00Constants.ERRCNT,
            String.valueOf(fileKetasuu - fileSeijoprcs));

        return resultMap;
    }

    /**
     * 共通のカテゴリー設定を取得
     * 
     * @param categoryDto X2404W02CategoryDto
     */
    private void settingCategory(X2404W02CategoryDto categoryDto) {

        // 「製品水」の商品大分類コード
        categoryDto.setShnmctgrycd(
                getSystemMasterValue(CommonConstants.SYS_SETTEINO_096, CommonConstants.SETTEICD_096_1));

        // 「サーバー関連用」の商品大分類コード
        categoryDto.setSrvlctgrycd(
            getSystemMasterValue(CommonConstants.SYS_SETTEINO_096, CommonConstants.SETTEICD_096_4));

        // 「サーバー関連本体」の商品大分類コード
        categoryDto.setSrvmctgrycd(
            getSystemMasterValue(CommonConstants.SYS_SETTEINO_134, CommonConstants.SETTEICD_134_41));

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
        categoryDto.setRgralist(rgralist);

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
        categoryDto.setSrmlist(srmlist);
    }

    /**
     * 受注配達ヘッダ取得 or 新規作成
     * 
     * @param conditionDto X2404W02RegistrationConditionDto
     * @param myMap CSVのリストの１行
     * @param dbManagementSession
     */
    private void findOrCreateHeader(X2404W02RegistrationConditionDto conditionDto,
            HashMap<String, String> myMap, DbManagementSession dbSession) {

        // エンティティのインスタンス生成
        S04T001TEntityExample example = new S04T001TEntityExample();
        // クライテリア（SQL条件）の生成
        example.createCriteria()
            // AP基本コード
            .andApkihoncdEqualTo(myMap.get("apkihoncd").toString())
            // ショップコード
            .andShopcdEqualTo(myMap.get("shopcd").toString())
            // 得意先コード
            .andTokuisakicdEqualTo(myMap.get("tokuisakicd").toString())
            // 伝票ステータス
            .andDnpystsEqualTo("01")
            // 受注日 >= 使用日
            .andAcptddGreaterThanOrEqualTo(myMap.get("siyoudd").toString())
            // 有効区分
            .andYukokbnEqualTo("1");
        // 使用日が受注日で一番近い順に並べる
        example.setOrderByClause("ACPTDD ASC");

        // 該当する受注配達テーブルのレコードを取得
        List<S04T001TEntity> resultEntityList = s04t001tDao.selectByExample(example);

        if (!resultEntityList.isEmpty()) {
            conditionDto.setHeader(resultEntityList.get(0));
            conditionDto.setShorikbn(X2404W02RegistrationConditionDto.PROCESS_UPDATE);
            return;
        }

        // 新規作成
        S04T001TEntity header = createAcptDnpyEntity(myMap, dbSession);

        conditionDto.setHeader(header);
        conditionDto.setShorikbn(X2404W02RegistrationConditionDto.PROCESS_NEW);
    }

    /**
     * 受注配達テーブル新規作成
     *
     * @param myMap CSVのリストの１行
     * @param dbManagementSession 
     * @return S04T001TEntity 新規登録用Entity
     */
    public S04T001TEntity createAcptDnpyEntity(HashMap<String, String> myMap,
            DbManagementSession dbManagementSession) {
        // S04T001Tの初期値を含んだレコードを生成
        S04T001TEntity entity = S04T001TEntityDaoEx.createDefaultModel();

        // CSVの項目を変数に設定
        String apkihoncd = myMap.get("apkihoncd").toString();
        String shopcd = myMap.get("shopcd").toString();
        String tokuisakicd = myMap.get("tokuisakicd").toString();
        String siyoudd = myMap.get("siyoudd").toString();

        // 得意先マスタ取得
        S02M013TEntity m013tEntity = getTokuisakiData(apkihoncd, shopcd, tokuisakicd);
        // 請求先マスタ取得
        S02M017TEntity m017tentity = getSkysakiData(apkihoncd, shopcd, m013tEntity);
        // ショップ情報取得
        S02M046TEntity m046tEntity = getShopData(apkihoncd, shopcd);

        // 回収方法の取得
        String kaishuhoho = m017tentity == null ? "" : m017tentity.getKaishuhohokbn();
        //税計算区分
        String currentTaxkeisankbn = getCurrentTaxkeisankbnuri(m046tEntity, siyoudd);

        // 受注配達テーブルのEntity作成
        entity.setApkihoncd(apkihoncd); // AP基本コード
        entity.setShopcd(shopcd); // ショップコード
        entity.setAcptdnpyno(saibanService.getSlipNoJuchu(apkihoncd, shopcd)); // 受注伝票番号
        entity.setEdano(Short.valueOf("0")); // 枝番号
        entity.setDlvrytrgtflg(CommonConstants.FLG_ON); // 配達対象フラグ
        entity.setDnpykbn(getDnpykbn(m013tEntity.getTokuisakikbn())); // 伝票区分
        entity.setTorihikikbn(CommonConstants.KBN_ACPTTORIHIKI_JUCHUYOYAKU); // 取引区分
        entity.setAcptkbn(CommonConstants.KBN_ACPT_SHOHIN); // 受注区分
        entity.setAcptkbnshs(CommonConstants.KBN_ACPTSYS_NONE); // 受注区分詳細
        entity.setTokuisakicd(tokuisakicd); // 得意先コード
        entity.setAcptdd(siyoudd); // 受注日
        entity.setAcpttnshaapkihoncd(dbManagementSession.getApkihoncd()); // 受注担当者AP基本コード
        entity.setAcpttnshashopcd(dbManagementSession.getShopcd()); // 受注担当者ショップコード
        entity.setAcpttnshacd(dbManagementSession.getTnshacd()); // 受注担当者コード
        entity.setFzaiokikanoflg(m013tEntity.getFzaiokikanoflg()); // 不在置き可能フラグ
        entity.setFzaiokijissekiflg("0"); // 不在置き実績フラグ
        entity.setDlvrytnshacd(m013tEntity.getDlvrytnshacd()); // 配達担当者コード
        entity.setDlvrykankatsuapkihoncd(m013tEntity.getDlvrykankatsuapkihoncd()); // 配達管轄AP基本コード
        entity.setDlvrykankatsushopcd(m013tEntity.getDlvrykankatsushopcd()); // 配達管轄ショップコード
        entity.setDlvrykyotencd(m013tEntity.getDlvrykankatsukyotencd()); // 配達拠点コード
        entity.setDlvryareacd(m013tEntity.getDlvryareacd()); // 配達エリアコード
        entity.setDlvrykbn(m013tEntity.getDlvrykbn()); // 配達区分
        entity.setDlvrycmpnycd(m013tEntity.getDlvrycmpnycd()); // 配達会社コード
        entity.setPrntmemon1(m013tEntity.getPrntmemon1()); // 印刷メモ1
        entity.setPrntmemon2(m013tEntity.getPrntmemon2()); // 印刷メモ2
        entity.setPrntmemon3(m013tEntity.getPrntmemon3()); // 印刷メモ3
        entity.setDlvryjun(m013tEntity.getDlvryjun()); // 配達順
        entity.setNyknhyoprnttrgtflg(S04T001TEntityDaoEx.getNyknhyoprnttrgtflg(kaishuhoho)); // 入会票印刷対象フラグ
        entity.setSingldnpyflg(CommonConstants.FLG_OFF); // 単体伝票締フラグ
        entity.setDnpysts(CommonConstants.DNPYSTS_01); // 伝票ステータス
        entity.setAcptsts(CommonConstants.DNPYSTS_01); // 受注ステータス
        entity.setDlvrysts(CommonConstants.DNPYSTS_00); // 配達ステータス
        entity.setDatsksmotokbn(CommonConstants.KBN_SAKUSEIMOTO_BOTTLECUPON); // データ作成元区分
        entity.setAcpthikiatesakiapkihoncd(apkihoncd); // 受注引当先AP基本コード
        entity.setAcpthikiatesakishopcd(shopcd); // 受注引当先ショップコード
        entity.setAcpthikiatesakikyotencd(m013tEntity.getDlvrykankatsukyotencd()); // 受注引当先拠点コード
        entity.setDlvryytirgrayusyohns(Short.valueOf("0")); // 配達予定レギュラー有償本数
        entity.setDlvryytirgramushohns(Short.valueOf("0")); // 配達予定レギュラー無償本数
        entity.setDlvryytisrmyusyohns(Short.valueOf("0")); // 配達予定スリム有償本数
        entity.setDlvryytisrmmushohns(Short.valueOf("0")); // 配達予定スリム無償本数
        entity.setDlvryytisonotayusyohns(Short.valueOf("0")); // 配達予定その他有償本数
        entity.setDlvryytisonotamushohns(Short.valueOf("0")); // 配達予定その他無償本数
        entity.setDlvryytiseihinmizuurikin(Long.valueOf("0")); // 配達予定製品水売上金額
        entity.setDlvryytisonotaurikin(Long.valueOf("0")); // 配達予定その他売上金額
        entity.setAcptkin(Long.valueOf("0")); // 受注金額
        entity.setAcptshohitaxkin(Long.valueOf("0")); // 受注消費税金額
        entity.setAcptippannukikin(Long.valueOf("0")); // 受注一般税抜金額
        entity.setAcptippantaxkin(Long.valueOf("0")); // 受注一般消費税金額
        entity.setAcptkeigennukikin(Long.valueOf("0")); // 受注軽減税抜金額
        entity.setAcptkeigentaxkin(Long.valueOf("0")); // 受注軽減消費税金額
        entity.setAcpthikazeiuriikin(Long.valueOf("0")); // 受注非課税金額
        entity.setAcptfukazeiuriikin(Long.valueOf("0")); // 受注不課税売上金額
        entity.setNohinkin(Long.valueOf("0")); // 納品金額
        entity.setNohinshohitaxkin(Long.valueOf("0")); // 納品消費税金額
        entity.setNohinippannukikin(Long.valueOf("0")); // 納品一般税抜金額
        entity.setNohinippantaxkin(Long.valueOf("0")); // 納品一般消費税金額
        entity.setNohinkeigennukikin(Long.valueOf("0")); // 納品軽減税抜金額
        entity.setNohinkeigentaxkin(Long.valueOf("0")); // 納品軽減消費税金額
        entity.setNohinhikazeiuriikin(Long.valueOf("0")); // 納品非課税金額
        entity.setNohinfukazeiuriikin(Long.valueOf("0")); // 納品不課税売上金額
        // 得意先が卸対象の場合は税計算区分に外税をセットする
        entity.setTaxkeisankbn(checkOroshi(m013tEntity.getTokuisakikbn()) ? 
                CommonConstants.KBN_TAXKEISAN_EX: currentTaxkeisankbn); // 税計算区分
        entity.setAcptrcptkeirokbn(CommonConstants.SETTEICD_093_03); // 受注受付経路区分
        entity.setToshaapkihoncd(dbManagementSession.getApkihoncd()); // 登録者AP基本コード
        entity.setToshashopcd(dbManagementSession.getShopcd()); // 登録者ショップコード
        entity.setToshacd(dbManagementSession.getTnshacd()); // 登録者コード
        entity.setInptsystmymd(DateTimeUtil.changeFormatToSystemDate(LocalDateTime.now())); // 登録システム日付
        entity.setInptsystmjikan(DateTimeUtil.changeFormatToSystemTime(LocalDateTime.now())); // 登録システム時間
        entity.setInptunyoymd(dbManagementSession.getUnyoDate()); // 登録運用日付
        entity.setInptipadrse(dbManagementSession.getIpAddress()); // 登録IPアドレス
        entity.setInptpgmid(dbManagementSession.getProgramId());  // 登録プログラムID
        entity.setKoshaapkihoncd(dbManagementSession.getApkihoncd()); // 更新者AP基本コード
        entity.setKoshashopcd(dbManagementSession.getShopcd()); // 更新者ショップコード
        entity.setKoshacd(dbManagementSession.getTnshacd()); // 更新者コード
        entity.setKshnsystmymd(DateTimeUtil.changeFormatToSystemDate(LocalDateTime.now())); // 更新者システム日付
        entity.setKshnsystmjikan(DateTimeUtil.changeFormatToSystemTime(LocalDateTime.now())); // 更新者システム時間
        entity.setKshnunyoymd(dbManagementSession.getUnyoDate()); // 更新者運用日付
        entity.setKshnipadrse(dbManagementSession.getIpAddress()); // 更新者IPアドレス
        entity.setKshnpgmid(dbManagementSession.getProgramId()); // 更新プログラムID

        return entity;
    }

    /**
     * セット商品展開
     * 
     * @param conditionDto
     * @param myMap CSVのリストの１行
     * @param duplicateCheckList
     * @param dbManagementSession
     */
    private void createMeisai(X2404W02RegistrationConditionDto conditionDto,
            HashMap<String, String> myMap, DbManagementSession dbManagementSession) {

        S04T001TEntity header = conditionDto.getHeader();
        String shncd = myMap.get("shncd");
        String couponCd = myMap.get("cuponcd");
        X2404W02SummaryDto summaryDto = new X2404W02SummaryDto();

        List<S04T002TEntity> meisaiList = new ArrayList<>();

        // 商品マスタビューからレコード取得
        S02V001TEntityExample S02V001TExample = new S02V001TEntityExample();
        S02V001TExample.createCriteria().andApkihoncdEqualTo(header.getApkihoncd())
            .andShopcdEqualTo(header.getShopcd()).andShncdEqualTo(shncd);
        S02V001TEntity s02v001tEntity = s02v001tDao.selectByExample(S02V001TExample).get(0);

        // 商品マスタビューEntity.税設定区分が「0:しない」の場合、商品マスタビューEntity.税区分は「null」（課税）として設定
        if ("0".equals(s02v001tEntity.getTaxsetteikbn())) {
            s02v001tEntity.setTaxkbn(null);
        }

        // --- 親明細
        S04T002TEntity meisai = new S04T002TEntity();
        meisai.setApkihoncd(header.getApkihoncd()); // AP基本コード
        meisai.setShopcd(header.getShopcd()); // ショップコード
        meisai.setAcptdnpyno(header.getAcptdnpyno()); // 受注伝票番号
        meisai.setEdano(header.getEdano()); // 枝番号
        meisai.setAcptdnpymeisaino(getNextAcptDnpyMeisaiNo(header)); // 受注伝票明細番号
        meisai.setAcptdnpymeisaiedano((short)0); // 受注伝票明細枝番号
        meisai.setLctgrycd(s02v001tEntity.getLctgrycd()); // 大分類コード
        meisai.setMctgrycd(s02v001tEntity.getMctgrycd()); // 中分類コード
        meisai.setSctgrycd(s02v001tEntity.getSctgrycd()); // 小分類コード
        meisai.setShncd(shncd); // 商品コード
        meisai.setKeijokbn(CommonConstants.KBN_KEIJO_URIAGE); // 計上区分
        meisai.setAcptsuryo(BigDecimal.ONE); // 受注数量
        meisai.setHyojununt(getCurrentPriceValue(
                s02v001tEntity.getGenzhyojunprice(), s02v001tEntity.getShinhyojunprice(),
                s02v001tEntity.getHyojunpricekirikaedd() , header.getAcptdd())); // 標準単価
        meisai.setHyojununt(BigDecimal.ZERO); // 標準単価
        meisai.setAcptunt(BigDecimal.ZERO); // 受注単価
        meisai.setAcptkin(Long.valueOf("0")); // 受注金額
        meisai.setAcptuntshohitaxkin(Long.valueOf("0"));
        meisai.setAcptshohitaxkin(Long.valueOf("0")); // 受注消費税金額
        meisai.setNohinsuryo(BigDecimal.ZERO); // 納品数量
        meisai.setNohinunt(BigDecimal.ZERO); // 納品単価
        meisai.setNohinkin(Long.valueOf("0")); // 納品金額
        meisai.setNohinuntshohitaxkin(Long.valueOf("0")); // 納品単価消費税金額
        meisai.setNohinshohitaxkin(Long.valueOf("0")); // 納品消費税金額
        meisai.setSoukaisu((short)1); // 総回数
        meisai.setZankaisu((short)1); // 残回数
        meisai.setTaxkbn(s02v001tEntity.getTaxkbn()); // 税区分
        meisai.setYusyokbn("1"); // 有償区分
        meisai.setMeisaikbn("0"); // 明細区分
        meisai.setNyuryokumeisaiflg(CommonConstants.FLG_ON); // 入力明細フラグ
        meisai.setTanpinmeisaiflg(CommonConstants.FLG_OFF); // 単品明細フラグ
        meisai.setStshncd(shncd); // セット商品コード
        meisai.setMeisaibiko(couponCd); // 明細備考
        meisai.setDatsksmotokbn(CommonConstants.KBN_SAKUSEIMOTO_BOTTLECUPON); // データ作成元区分
        meisai.setYukokbn(CommonConstants.YUKOKBN_1); // 有効区分
        meisai.setToshaapkihoncd(dbManagementSession.getApkihoncd()); // 登録者AP基本コード
        meisai.setToshashopcd(dbManagementSession.getShopcd()); // 登録者ショップコード
        meisai.setToshacd(dbManagementSession.getTnshacd()); // 登録者コード
        meisai.setInptsystmymd(DateTimeUtil.changeFormatToSystemDate(LocalDateTime.now())); // 登録システム日付
        meisai.setInptsystmjikan(DateTimeUtil.changeFormatToSystemTime(LocalDateTime.now())); // 登録システム時間
        meisai.setInptunyoymd(dbManagementSession.getUnyoDate()); // 登録運用日付
        meisai.setInptipadrse(dbManagementSession.getIpAddress()); // 登録IPアドレス
        meisai.setInptpgmid(dbManagementSession.getProgramId());  // 登録プログラムID
        meisai.setKoshaapkihoncd(dbManagementSession.getApkihoncd()); // 更新者AP基本コード
        meisai.setKoshashopcd(dbManagementSession.getShopcd()); // 更新者ショップコード
        meisai.setKoshacd(dbManagementSession.getTnshacd()); // 更新者コード
        meisai.setKshnsystmymd(DateTimeUtil.changeFormatToSystemDate(LocalDateTime.now())); // 更新者システム日付
        meisai.setKshnsystmjikan(DateTimeUtil.changeFormatToSystemTime(LocalDateTime.now())); // 更新者システム時間
        meisai.setKshnunyoymd(dbManagementSession.getUnyoDate()); // 更新者運用日付
        meisai.setKshnipadrse(dbManagementSession.getIpAddress()); // 更新者IPアドレス
        meisai.setKshnpgmid(dbManagementSession.getProgramId()); // 更新プログラムID

        meisaiList.add(meisai);

        // 展開処理
        // セット商品リスト取得
        S02M008TX2404W02Entity s02m008tx2404w02EntityIn = new S02M008TX2404W02Entity();
        s02m008tx2404w02EntityIn.setApkihoncd(header.getApkihoncd());
        s02m008tx2404w02EntityIn.setShopcd(header.getShopcd());
        s02m008tx2404w02EntityIn.setStshncd(shncd);

        // --- 構成品取得
        List<S02M008TX2404W02Entity> koseiList =
                s02m008tDao.selectByX2404W02(s02m008tx2404w02EntityIn);

        short edano = 1;

        for (S02M008TEntity kosei : koseiList) {
            // 商品マスタビューからレコード取得
            S02V001TEntityExample S02V001TChildExample = new S02V001TEntityExample();
            S02V001TChildExample.createCriteria().andApkihoncdEqualTo(header.getApkihoncd())
                .andShopcdEqualTo(header.getShopcd()).andShncdEqualTo(kosei.getKoseishncd());
            S02V001TEntity s02v001tChildEntity = s02v001tDao.selectByExample(S02V001TChildExample).get(0);

            // 子明細Entity
            S04T002TEntity child = copyChildEntity(meisai);
            child.setAcptdnpymeisaiedano(edano++); // 受注伝票明細枝番号
            child.setShncd(s02v001tChildEntity.getShncd()); // 商品コード
            child.setLctgrycd(s02v001tChildEntity.getLctgrycd()); // 大分類コード
            child.setMctgrycd(s02v001tChildEntity.getMctgrycd()); // 中分類コード
            child.setSctgrycd(s02v001tChildEntity.getSctgrycd()); // 小分類コード
            child.setHyojununt(getCurrentPriceValue(
                    s02v001tChildEntity.getGenzhyojunprice(), s02v001tChildEntity.getShinhyojunprice(),
                    s02v001tChildEntity.getHyojunpricekirikaedd() , header.getAcptdd())); // 標準単価
            child.setAcptunt(kosei.getDlvryprice()); // 受注単価
            child.setAcptkin(kosei.getDlvryprice().longValueExact()); // 受注金額
            child.setTaxkbn(s02v001tChildEntity.getTaxkbn()); // 税区分
            child.setTanpinmeisaiflg(CommonConstants.FLG_ON); // 入力明細フラグ
            child.setNyuryokumeisaiflg(CommonConstants.FLG_OFF); // 単品明細フラグ

            // 本数・金額の集計
            aggregateForHeader(summaryDto, child, conditionDto.getCategory());

            meisaiList.add(child);

        }

        // 集計結果をHeaderに反映
        applySummaryToHeader(conditionDto.getHeader(), summaryDto);

        conditionDto.setMeisaiList(meisaiList);
    }

    /**
     * 受注伝票明細番号取得処理
     *
     * @param S04T002TEntity
     * @param duplicateCheckList 重複確認用
     * @return 検索した受注伝票明細番号の最大値＋１の値
     */
    private int getNextAcptDnpyMeisaiNo(S04T001TEntity entity) {

        // エンティティのインスタンス生成
        S04T002TEntityExample example = new S04T002TEntityExample();
        // クライテリア（SQL条件）の生成
        example.createCriteria()
            .andApkihoncdEqualTo(entity.getApkihoncd()) // AP基本コード
            .andShopcdEqualTo(entity.getShopcd()) // ショップコード
            .andAcptdnpynoEqualTo(entity.getAcptdnpyno()) // 受注伝票番号
            .andEdanoEqualTo(entity.getEdano()); // 枝番号

        List<S04T002TEntity> result = s04t002tDao.selectByExample(example);

        // 受注伝票明細番号の最大値を取得
        int currentMax = result.stream()
            .mapToInt(S04T002TEntity::getAcptdnpymeisaino)
            .max()
            .orElse(0);

        return currentMax + 1;
    }

    /**
     * 展開行の受注配達明細テーブルのコピーを作成
     *
     * @param e 展開元の受注配達明細Entity
     */
    private S04T002TEntity copyChildEntity(S04T002TEntity e) {
        S04T002TEntity en = new S04T002TEntity();

        en.setApkihoncd(e.getApkihoncd()); // AP基本コード
        en.setShopcd(e.getShopcd()); // ショップコード
        en.setAcptdnpyno(e.getAcptdnpyno()); // 受注伝票番号
        en.setEdano(e.getEdano()); // 枝番号
        en.setAcptdnpymeisaino(e.getAcptdnpymeisaino()); // 受注伝票明細番号
        en.setKeijokbn(e.getKeijokbn()); // 計上区分
        en.setAcptsuryo(e.getAcptsuryo()); // 受注数量
        en.setAcptuntshohitaxkin(e.getAcptuntshohitaxkin()); // 受注単価消費税金額
        en.setAcptshohitaxkin(e.getAcptshohitaxkin()); // 受注消費税金額
        en.setNohinsuryo(e.getNohinsuryo()); // 納品数量
        en.setNohinunt(e.getNohinunt()); // 納品単価
        en.setNohinkin(e.getNohinkin()); // 納品金額
        en.setNohinuntshohitaxkin(e.getNohinuntshohitaxkin()); // 納品単価消費税金額
        en.setNohinshohitaxkin(e.getNohinshohitaxkin()); // 納品消費税金額
        en.setSoukaisu(e.getSoukaisu()); // 総回数
        en.setZankaisu(e.getZankaisu()); // 残回数
        en.setYusyokbn(e.getYusyokbn()); // 有償区分
        en.setMeisaikbn(e.getMeisaikbn()); // 明細区分
        en.setStshncd(e.getStshncd()); // セット商品コード
        en.setMeisaibiko(e.getMeisaibiko()); // 明細備考
        en.setDatsksmotokbn(e.getDatsksmotokbn()); // データ作成元区分
        en.setYukokbn(e.getYukokbn()); // 有効区分
        en.setToshaapkihoncd(e.getToshaapkihoncd()); // 登録者AP基本コード
        en.setToshashopcd(e.getToshashopcd()); // 登録車ショップコード
        en.setToshacd(e.getToshacd()); // 登録車コード
        en.setInptsystmymd(e.getInptsystmymd()); // 登録システム日付
        en.setInptsystmjikan(e.getInptsystmjikan()); // 登録システム時間
        en.setInptunyoymd(e.getInptunyoymd()); // 登録運用日付
        en.setInptipadrse(e.getInptipadrse()); // 登録IPアドレス
        en.setInptpgmid(e.getInptpgmid());  // 登録プログラムID
        en.setKoshaapkihoncd(e.getKoshaapkihoncd()); // 更新者AP基本コード
        en.setKoshashopcd(e.getKoshashopcd()); // 更新者ショップコード
        en.setKoshacd(e.getKoshacd()); // 更新者コード
        en.setKshnsystmymd(e.getKshnsystmymd()); // 更新者システム日付
        en.setKshnsystmjikan(e.getKshnsystmjikan()); // 更新者システム時間
        en.setKshnunyoymd(e.getKshnunyoymd()); // 更新者運用日付
        en.setKshnipadrse(e.getKshnipadrse()); // 更新者IPアドレス
        en.setKshnpgmid(e.getKshnpgmid()); // 更新プログラムID

        return en;
    }

    /**
     * 受注配達テーブル集計反映
     *
     * @param summary 集計結果保管用Dto
     * @param child 展開した予定発生ポイント明細Entity
     * @param category カテゴリーDto
     */
    private void applySummaryToHeader(S04T001TEntity header, X2404W02SummaryDto s) {

        // レギュラー有償本数
        header.setDlvryytirgrayusyohns(
                (short)(header.getDlvryytirgrayusyohns() + s.getRgrayusyohns().intValue()));
        // レギュラー無償本数
        header.setDlvryytirgramushohns(
                (short)(header.getDlvryytirgramushohns() + s.getRgramushohns().intValue()));
        // スリム有償本数
        header.setDlvryytisrmyusyohns(
                (short)(header.getDlvryytisrmyusyohns() + s.getSrmyusyohns().intValue()));
        // スリム無償本数
        header.setDlvryytisrmmushohns(
                (short)(header.getDlvryytisrmmushohns() + s.getSrmmushohns().intValue()));
        // その他有償本数
        header.setDlvryytisonotayusyohns(
                (short)(header.getDlvryytisonotayusyohns() + s.getSonotayusyohns().intValue()));
        // その他無償本数
        header.setDlvryytisonotamushohns(
                (short)(header.getDlvryytisonotamushohns() + s.getSonotamushohns().intValue()));
        // 製品水売上金額
        header.setDlvryytiseihinmizuurikin(
                header.getDlvryytiseihinmizuurikin() + s.getSeihinmizuurikin().longValueExact());
        // その他売上金額
        header.setDlvryytisonotaurikin(
                header.getDlvryytisonotaurikin() + s.getSonotaurikin().longValueExact());
    }

    /**
     * 受注配達テーブル用集計ロジック
     *
     * @param summary 集計結果保管用Dto
     * @param child 展開した予定発生ポイント明細Entity
     * @param category カテゴリーDto
     */
    private void aggregateForHeader(X2404W02SummaryDto summary, S04T002TEntity child,
            X2404W02CategoryDto category) {
        if (child.getAcptsuryo() == null) {
            return;
        }

        // 単品明細フラグがOFFのものは集計対象から除外
        if (CommonConstants.FLG_OFF.equals(child.getTanpinmeisaiflg())) {
            return;
        }

        // 受注数量
        BigDecimal qty = child.getAcptsuryo();
        String shncd = child.getShncd();
        String keijokbn = child.getKeijokbn();
;
        // 有償判定
        boolean isYusho = CommonConstants.KBN_KEIJO_URIAGE.equals(keijokbn)
                       || CommonConstants.KBN_KEIJO_CAMPAIGN.equals(keijokbn);
        // 製品水判定
        boolean isWater = category.getShnmctgrycd().equals(child.getLctgrycd());

        // 製品水の集計
        if (isWater) {
         // レギュラー
            if (category.getRgralist().contains(shncd)) {
                if (isYusho) {
                    // レギュラー有償本数
                    summary.setRgrayusyohns(summary.getRgrayusyohns().add(qty));
                } else {
                    // レギュラー無償本数
                    summary.setRgramushohns(summary.getRgramushohns().add(qty));
                }
            }

            // スリム
            else if (category.getSrmlist().contains(shncd)) {
                if (isYusho) {
                    // スリム有償本数
                    summary.setSrmyusyohns(summary.getSrmyusyohns().add(qty));
                } else {
                    // スリム無償本数
                    summary.setSrmmushohns(summary.getSrmmushohns().add(qty));
                }
            }

            // その他
            else {
                if (isYusho) {
                    // その他有償本数
                    summary.setSonotayusyohns(summary.getSonotayusyohns().add(qty));
                } else {
                    // その他無償本数
                    summary.setSonotamushohns(summary.getSonotamushohns().add(qty));
                }
            }
        }

        // 金額の集計
        if (isYusho && child.getAcptkin() != null) {
            // 製品水売上金額
            if (isWater) {
                summary.setSeihinmizuurikin(summary.getSeihinmizuurikin().add(
                        BigDecimal.valueOf(child.getAcptkin())));
            // その他売上金額
            } else {
                summary.setSonotaurikin(summary.getSonotaurikin().add(
                        BigDecimal.valueOf(child.getAcptkin())));
            }
        }
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
     * 得意先情報を取得
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @return S02M013TEntity 得意先情報
     */
    private S02M013TEntity getTokuisakiData(String apkihoncd, String shopcd, String tokuisakicd) {
        // パラメータセット
        S02M013TEntityKey entityKey = new S02M013TEntityKey();
        entityKey.setApkihoncd(apkihoncd);
        entityKey.setShopcd(shopcd);
        entityKey.setTokuisakicd(tokuisakicd);

        // 得意先情報を取得
        return s02m013tDao.selectByPrimaryKey(entityKey);
    }

    /**
     * 請求先情報を取得
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param skysakicd 請求先コード
     * @return S02M017TEntity 請求先情報
     */
    private S02M017TEntity getSkysakiData(String apkihoncd, String shopcd, S02M013TEntity m013tEntity) {

        // 請求先コード取得
        String skysakicd = m013tEntity.getSkysakicd();
        // 請求先コードない場合は得意先コードを使用
        if (StringUtils.isEmpty(skysakicd)) {
            skysakicd = m013tEntity.getTokuisakicd();
        }

        // パラメータセット
        S02M017TEntityKey entityKey = new S02M017TEntityKey();
        entityKey.setApkihoncd(apkihoncd);
        entityKey.setShopcd(shopcd);
        entityKey.setSkysakicd(skysakicd);

        // 得意先情報を取得
        return s02m017tDao.selectByPrimaryKey(entityKey);
    }

    /**
     * ショップ情報を取得
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @param tokuisakicd 得意先コード
     * @return S02M013TEntity 得意先情報
     */
    private S02M046TEntity getShopData(String apkihoncd, String shopcd){
        // パラメータセット
        S02M046TEntityKey entityKey = new S02M046TEntityKey();
        entityKey.setApkihoncd(apkihoncd);
        entityKey.setShopcd(shopcd);

        // 得意先情報を取得
        return s02m046tDao.selectByPrimaryKey(entityKey);
    }

    /**
     * 伝票区分を返します。
     * 
     * @param tokuisakikbn 得意先区分
     * @return 伝票区分
     */
    private static String getDnpykbn(String tokuisakikbn) {

        if (CommonConstants.KBN_TOKUISAKI_KEIHI.equals(tokuisakikbn)) {
            return CommonConstants.KBN_KEIHI_SYUKKO;

        } else {
            return CommonConstants.KBN_URIAGE_SYUKKO;
        }
    }

    /**
     * 卸区分判定。
     *
     * @param tokuisakikbn 得意先区分
     * @return boolean 受注可能の場合はtrue、それ以外の場合はfalse
     */
    private static boolean checkOroshi(String tokuisakikbn) {
        return CommonConstants.KBN_TOKUISAKI_SHOP.equals(tokuisakikbn)
            || CommonConstants.KBN_TOKUISAKI_AP.equals(tokuisakikbn)
            || CommonConstants.KBN_TOKUISAKI_ACI.equals(tokuisakikbn)
            || CommonConstants.KBN_TOKUISAKI_NAJUTAKU.equals(tokuisakikbn)
            || CommonConstants.KBN_TOKUISAKI_DLVRYJUTAKU.equals(tokuisakikbn)
        ;
    }

    /**
     * 切替日を判断して現／新税計算区分を取得します。
     * 
     * @param shopEntity ショップマスタEntity
     * @param targetDate 対象日
     * @return 標準価格
     */
    private String getCurrentTaxkeisankbnuri(S02M046TEntity shopEntity, String targetDate) {

        return BusinessUtil.getCurrentValue(shopEntity.getGenztaxkeisankbnuri(),
                shopEntity.getShintaxkeisankbnuri(), shopEntity.getTaxkeisankbnurikirikaedd(), targetDate)
            .toString();
    }

    /**
     * エラーメッセージをセット
     *
     * @param errorList エラーリスト
     * @param errorParam 動的メッセージの配列
     * @throws ApplicationException アプリケーション例外
     */

    public void set03M1Form(ArrayList<X9901W03M1Form> errorList, int fileKetasuu, String item2) {

        X9901W03M1Form m1Form = new X9901W03M1Form();
        String[] items = new String[2];
        items[0] = String.valueOf(fileKetasuu + 1).toString();
        items[1] = item2;
        m1Form.setItems(items);
        errorList.add(m1Form);

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

}
