package jp.co.aquaclara.accis.web.domain.service.b1601;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M013TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M017TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S09T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S09T002TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S09T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S09T001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S09T002TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S09T002TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S09T002TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M013TB1601W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M017TB1601W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TB1601W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S09T001TB1601W01Entity;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.web.domain.dto.b1601.B1601W01BeforeCheckConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.b1601.B1601W01SearchConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.b1601.B1601W01SearchHistoryResultDto;
import jp.co.aquaclara.accis.web.domain.dto.b1601.B1601W01SearchMethodResultDto;
import jp.co.aquaclara.accis.web.domain.dto.b1601.B1601W01UpdateDto;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;

/**
 * 決済状況履歴照会Service
 * 
 * @author accisdev010
 *
 */
@Service
public class B1601W01Service {

    @Autowired
    private S02M013TDao s02m013tDao;

    @Autowired
    private S02M017TDao s02m017tDao;

    @Autowired
    private S07T001TDao s07t001tDao;

    @Autowired
    private S09T001TDao s09t001tDao;

    @Autowired
    private S09T002TDao s09t002tDao;

    // 決済結果（画面表示用文字列）
    private final String KSIKEKKA_JISSEKIZUMI = "実績済";

    // データ件数（0件）
    private final String NODATA = "0";

    /**
     * 請求先マスタ検索処理
     * 
     * @param dto 検索条件
     * @return B1601W01SearchMethodResultDto
     */
    public B1601W01SearchMethodResultDto searchMethod(B1601W01SearchConditionDto dto) {

        // 検索条件を生成する
        S02M017TB1601W01Entity entity = createMethodCustomEntity(dto);

        // 請求先マスタからのデータ取得及び結果の返却
        return createMethodResultDto(s02m017tDao.selectByB1601W01(entity));
    }

    /**
     * 請求先マスタ検索条件を生成する。
     * 
     * @param dto 検索条件
     * @return S02M017TB1601W01Entity
     */
    private S02M017TB1601W01Entity createMethodCustomEntity(B1601W01SearchConditionDto dto) {
        // 検索条件をCustomEntityに展開する
        S02M017TB1601W01Entity entity = new S02M017TB1601W01Entity();

        // AP基本コード
        entity.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        entity.setShopcd(dto.getShopcd());
        // 請求先コード
        entity.setSkysakicd(dto.getSkysakicd());

        return entity;
    }

    /**
     * 請求先マスタ検索結果をControllerへ返却する形式へ変換する。
     * 
     * @param entity S02M017TB1601W01Entity
     * @return B1601W01SearchMethodResultDto
     */
    private B1601W01SearchMethodResultDto createMethodResultDto(S02M017TB1601W01Entity entity) {

        if (ObjectUtils.isEmpty(entity)) {
            return null;
        }

        B1601W01SearchMethodResultDto dto = new B1601W01SearchMethodResultDto();

        dto.setKsihoho1(entity.getKsihohokbnnm()); // 決済方法1
        dto.setKsikkncd(entity.getKsikkncd()); // 決済機関コード
        dto.setKsikknnm(entity.getKsikknnm()); // 決済機関名称
        dto.setBnkcd1(entity.getKinyukkncd()); // 銀行コード1
        dto.setShitencd1(entity.getKinyukknshitencd()); // 支店コード
        dto.setBnknm1(entity.getKinyukknnmkana()); // 銀行名称1
        dto.setShitennm1(entity.getKinyukknshitennmkana()); // 支店名称1
        dto.setKzkind1(entity.getKinyukknkzkindnm()); // 口座種別1
        dto.setKzno1(changeToFuseji(entity.getKinyukknkzno())); // 口座番号1
        dto.setZengincstcd1(entity.getZengincstcd()); // 全銀顧客コード1
        dto.setCrdtcrdno1(changeToFuseji(entity.getCrdtcrdno())); // クレジットカード番号1
        dto.setCrdtcrdyukokgn1(entity.getCrdtcrdyukokgn()); // クレジットカード有効期限1
        dto.setMeigihitomei1(CommonConstants.KSIHOHOKBN_1.equals(entity.getKsihohokbn())
            ? entity.getKinyukknkzmeigikana()
            : entity.getCrdtcrdmeigi()); // 名義人名称1

        return dto;
    }

    /**
     * 決済履歴テーブル検索処理
     * 
     * @param dto 検索条件
     * @return List<B1601W01SearchHistoryResultDto>
     */
    public List<B1601W01SearchHistoryResultDto> searchHistory(B1601W01SearchConditionDto dto) {

        // 検索条件を生成する
        S09T001TB1601W01Entity entity = createHistoryCustomEntity(dto);

        // 決済履歴テーブルからのデータ取得及び結果の返却
        return createHistoryResultDtoList(s09t001tDao.selectByB1601W01(entity));
    }

    /**
     * 決済履歴テーブル検索条件を生成する。
     * 
     * @param dto 検索条件
     * @return S09T001TB1601W01Entity
     */
    private S09T001TB1601W01Entity createHistoryCustomEntity(B1601W01SearchConditionDto dto) {

        // 検索条件をCustomEntityに展開する
        S09T001TB1601W01Entity entity = new S09T001TB1601W01Entity();

        // AP基本コード
        entity.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        entity.setShopcd(dto.getShopcd());
        // 請求先コード
        entity.setSkysakicd(dto.getSkysakicd());

        return entity;
    }

    /**
     * 決済履歴テーブル検索結果をControllerへ返却する形式へ変換する。
     * 
     * @param list List<S09T001TB1601W01Entity>
     * @return List<B1601W01SearchHistoryResultDto>
     */
    private List<B1601W01SearchHistoryResultDto> createHistoryResultDtoList(
        List<S09T001TB1601W01Entity> list) {

        // 検索結果が空の場合
        if (ObjectUtils.isEmpty(list)) {
            throw BusinessExceptionFactory.dataNotFound(MessageID.W083);
        }

        List<B1601W01SearchHistoryResultDto> resultList = new ArrayList<>();

        for (S09T001TB1601W01Entity entity : list) {

            B1601W01SearchHistoryResultDto dto = new B1601W01SearchHistoryResultDto();

            dto.setShimedydmddmei(entity.getShimedydm() + "/" + entity.getShimedd()); // 締年月日
            dto.setKsihohokbnnmmei(entity.getKsihohokbnnm()); // 決済方法区分名称
            dto.setJotaimei(entity.getJotai()); // 状態
            dto.setJotainmmei(entity.getJotainm()); // 状態名称
            dto.setFurikaedydmddmei(DateTimeUtil.toSlashedDate(entity.getFurikaedydmdd())); // 振替年月日
            dto.setKsikekkamei(entity.getKsikekka()); // 決済結果
            dto.setKsikekkanmmei(changeKsikekkanm(entity.getKsikekka(), entity.getKsikekkanm())); // 決済結果名称
            dto.setKsikinmei(entity.getKsikin()); // 決済金額
            dto.setKsikkncdmei(entity.getKsikkncd()); // 決済機関コード
            dto.setKsikknnmmei(entity.getKsikknnm()); // 決済機関名称
            dto.setKinyukkncdmei(entity.getKinyukkncd()); // 金融機関コード
            dto.setKinyukknnmkanamei(entity.getKinyukknnmkana()); // 金融機関名称カナ
            dto.setKinyukknshitencdmei(entity.getKinyukknshitencd()); // 金融機関支店コード
            dto.setKinyukknshitennmkanamei(entity.getKinyukknshitennmkana()); // 金融機関支店名称カナ
            dto.setKzkindmei(entity.getKzkindnm()); // 口座種別
            dto.setKesshameigimei(entity.getKesshameigi()); // 決済者名義
            dto.setZengincstcdmei(entity.getZengincstcd()); // 全銀顧客コード
            dto.setCrdtcrdyukokgnmei(entity.getCrdtcrdyukokgn()); // クレジットカード有効期限
            dto.setSkyshimedydmddmei(entity.getSkyshimedydm() + "/" + entity.getSkyshimedd()); // 請求締年月日
            dto.setYukokbnmei(entity.getYukokbn()); // 有効区分
            dto.setToshaapkihoncdmei(entity.getToshaapkihoncd()); // 登録者ＡＰ基本コード
            dto.setToshashopcdmei(entity.getToshashopcd()); // 登録者ショップコード
            dto.setToshacdmei(entity.getToshacd()); // 登録者コード
            dto.setToshanmmei(entity.getToshanm()); // 登録者名
            dto.setInptsystmymdmei(DateTimeUtil.toSlashedDate(entity.getInptsystmymd())); // 登録システム日付
            dto.setInptsystmjikanmei(DateTimeUtil.toColonedDateHHMMSS(entity.getInptsystmjikan())); // 登録システム時間
            dto.setKoshaapkihoncdmei(entity.getKoshaapkihoncd()); // 更新者ＡＰ基本コード
            dto.setKoshashopcdmei(entity.getKoshashopcd()); // 更新者ショップコード
            dto.setKoshacdmei(entity.getKoshacd()); // 更新者コード
            dto.setKoshanmmei(entity.getKoshanm()); // 更新者名
            dto.setKshnsystmymdmei(DateTimeUtil.toSlashedDate(entity.getKshnsystmymd())); // 更新システム日付
            dto.setKshnsystmjikanmei(DateTimeUtil.toColonedDateHHMMSS(entity.getKshnsystmjikan())); // 更新システム時間
            dto.setKshnsystmymdmei1(DateTimeUtil.toSlashedDate(entity.getKshnsystmymd1())); // 更新システム日付1
            dto.setKshnsystmjikanmei1(
                DateTimeUtil.toColonedDateHHMMSS(entity.getKshnsystmjikan1())); // 更新システム時間1

            // 決済方法区分からDTOの口座番号及びクレジット番号の各プロパティに値を設定
            setKesshano(dto, entity.getKsihohokbn(), entity.getKesshano());

            resultList.add(dto);
        }
        return resultList;
    }

    /**
     * 対象文字列の文字数分の伏字（アスタリスク）を文字列にして返す。
     * 
     * @param str 対象文字列
     * @return String 伏字
     */
    private String changeToFuseji(String str) {

        // 対象文字列が空ではない場合に処理を実施
        if (!StringUtils.isEmpty(str)) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < str.length(); i++) {
                sb.append("*");
            }
            return sb.toString();
        }
        // 対象文字列が空の場合は空文字を返却
        return "";
    }

    /**
     * 決済方法区分からDTOの口座番号及びクレジット番号の各プロパティに値を設定する。
     * 
     * @param dto B1601W01SearchHistoryResultDto
     * @param ksihohokbn 決済方法区分
     * @param kesshano 決済者番号（口座番号 or クレジット番号）
     */
    private void setKesshano(B1601W01SearchHistoryResultDto dto, String ksihohokbn,
        String kesshano) {

        // 決済方法区分と決済者番号が空ではない場合に処理を実施
        if (!StringUtils.isEmpty(ksihohokbn) && !StringUtils.isEmpty(kesshano)) {

            // 決済方法区分が1（口座振替）の場合の処理
            if (ksihohokbn.equals(CommonConstants.KSIHOHOKBN_1)) {
                dto.setKesshano1mei(changeToFuseji(kesshano));
                dto.setKesshano2mei("");

                // 決済方法区分が1（口座振替）以外の場合の処理
            } else {
                dto.setKesshano1mei("");
                dto.setKesshano2mei(changeToFuseji(kesshano));
            }
        }
    }

    /**
     * 決済結果によって決済結果名称を変更する。
     * 
     * @param ksikekka 決済結果
     * @param ksikekkanm DBから取得した決済結果名称(システム管理マスタから取得した文字設定値)
     * @return String 決済結果名称
     */
    private String changeKsikekkanm(String ksikekka, String ksikekkanm) {

        // 決済結果が空ではない場合に処理を実施
        if (!StringUtils.isEmpty(ksikekka)) {
            // 決済結果が0（振替済）の場合の処理
            if (ksikekka.equals(CommonConstants.KSIKEKKA_0)) {
                return KSIKEKKA_JISSEKIZUMI;
            } else {
                // 決済結果が0（振替済）以外は、DBから取得した決済結果名称をそのまま返す
                return ksikekkanm;
            }
        }
        // 決済結果が空の場合は空文字を返す
        return "";
    }

    /**
     * 相関チェックを実行する(更新前)
     * 
     * @param dto B1601W01BeforeCheckConditionDto
     * @param messageList エラーメッセージ格納用リスト
     * 
     */
    public void checkParamPre(B1601W01BeforeCheckConditionDto dto, List<String> messageList) {

        // 得意先マスタ検索（請求先コードに紐づく得意先の存在確認）
        S02M013TB1601W01Entity tokuisakiResult =
            s02m013tDao.selectByB1601W01(createS02M013TCustomEntity(dto));

        // 請求先マスタ検索（請求先情報の取得）
        List<S02M017TEntity> seikyusakiList =
            s02m017tDao.selectByExample(createS02M017TEntityExample(dto));

        // 請求残高テーブル検索（繰越金額（合計）及び今回消費税金額（合計）の取得）
        S07T001TB1601W01Entity seikyuzandakaResult =
            s07t001tDao.selectByB1601W01(createS07T001TCustomEntity(dto));

        // 決済金額（画面側での入力値）と合計金額を比較
        int compareResult = compareKsikin(dto.getKsikin(), seikyusakiList.get(0).getSkyhohokbn(),
            seikyuzandakaResult.getKurikoshikingoukei(),
            seikyuzandakaResult.getKonkaishohitaxkingoukei());

        // 画面で指定された請求先に紐付く得意先が存在しない場合
        if (NODATA.equals(tokuisakiResult.getCount())) {
            // 合計金額より決済金額（画面側での入力値）の方が大きい場合、エラー
            if (compareResult > 0) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W124, "請求金額より大きな金額");
            }
            // 画面で指定された請求先に紐付く得意先が存在する場合
        } else {
            // 合計金額より決済金額（画面側での入力値）の方が大きい場合、警告文をセットする
            if (compareResult > 0) {
                BusinessExceptionFactory.setMessageList(messageList, MessageID.W260,
                    "請求金額より大きな金額が入力されています。よろしいですか？");
            }
        }
    }

    /**
     * 得意先マスタ検索条件を生成する。
     * 
     * @param dto 検索条件
     * @return S02M013TB1601W01Entity
     */
    private S02M013TB1601W01Entity createS02M013TCustomEntity(B1601W01BeforeCheckConditionDto dto) {
        // 検索条件をCustomEntityに展開する
        S02M013TB1601W01Entity entity = new S02M013TB1601W01Entity();

        // AP基本コード
        entity.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        entity.setShopcd(dto.getShopcd());
        // 請求先コード
        entity.setSkysakicd(dto.getSkysakicd());

        return entity;
    }

    /**
     * 請求先マスタのEntityExampleを生成する。
     * 
     * @param dto B1601W01BeforeCheckConditionDto
     * @return S02M017TEntityExample
     */
    private S02M017TEntityExample createS02M017TEntityExample(B1601W01BeforeCheckConditionDto dto) {

        S02M017TEntityExample entity = new S02M017TEntityExample();

        // クライテリア(SQL条件)の生成
        jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntityExample.Criteria criteria =
            entity.createCriteria();
        criteria.andApkihoncdEqualTo(dto.getApkihoncd()).andShopcdEqualTo(dto.getShopcd())
            .andSkysakicdEqualTo(dto.getSkysakicd());

        return entity;
    }

    /**
     * 請求残高テーブル検索条件を生成する。
     * 
     * @param dto 検索条件
     * @return S07T001TB1601W01Entity
     */
    private S07T001TB1601W01Entity createS07T001TCustomEntity(B1601W01BeforeCheckConditionDto dto) {
        // 検索条件をCustomEntityに展開する
        S07T001TB1601W01Entity entity = new S07T001TB1601W01Entity();

        // AP基本コード
        entity.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        entity.setShopcd(dto.getShopcd());
        // 請求締年月
        entity.setSkyshimedydm(dto.getSkyshimedydm());
        // 請求締日
        entity.setSkyshimedd(dto.getSkyshimedd());
        // 請求先コード
        entity.setSkysakicd(dto.getSkysakicd());

        return entity;
    }

    /**
     * 決済金額と合計金額の大きさを比較する
     * 
     * @param ksikin 決済金額
     * @param skyhohokbn 請求方法区分
     * @param kurikoshikingoukei 繰越金額（合計）
     * @param konkaishohitaxkinkomigoukei 今回消費税込金額（合計）
     * @return int 比較結果
     */
    private int compareKsikin(String ksikin, String skyhohokbn, String kurikoshikingoukei,
        String konkaishohitaxkinkomigoukei) {

        // 決済金額（画面側での入力値）
        BigDecimal tmpKsikin = new BigDecimal(ksikin);

        // 請求方法区分が"1"(残高請求)の場合
        if (CommonConstants.SKYHOHOKBN_ZANDAKA.equals(skyhohokbn)) {
            // 繰越金額（合計）との比較結果を返却
            return tmpKsikin.compareTo(new BigDecimal(kurikoshikingoukei));

            // 請求方法区分が"1"(残高請求)以外の場合
        } else {
            // 今回消費税込金額（合計）との比較結果を返却
            return tmpKsikin.compareTo(new BigDecimal(konkaishohitaxkinkomigoukei));
        }
    }

    /**
     * 決済状況履歴照会更新処理
     * 
     * @param dto B1601W01UpdateDto
     * 
     */
    @Transactional
    public void update(B1601W01UpdateDto dto) {

        // 相関チェック実施
        checkUpdateParam(dto);

        // 決済履歴テーブルの更新処理を行なう
        doUpdateS09T001T(dto);

        // 決済機関別決済履歴テーブル更新の為の依頼合計のデータを決済履歴テーブルから取得してDTOに設定する
        setIraismData(dto);

        // 決済機関別決済履歴テーブルの更新処理を行なう
        doUpdateS09T002T(dto);
    }

    /**
     * 相関チェックを実行する。
     * 
     * @param dto B1601W01UpdateDto
     * 
     */
    private void checkUpdateParam(B1601W01UpdateDto dto) {

        // 決済機関別決済履歴テーブル検索
        S09T002TEntity result = s09t002tDao.selectByPrimaryKey(createS09T002TEntityKey(dto));

        // 決済機関別決済履歴テーブルの検索結果が空ではない場合チェックする
        if (!ObjectUtils.isEmpty(result)) {
            // 状態が1(抽出済)以外の場合はエラー
            if (!CommonConstants.JOTAI_1.equals(result.getJotai())) {
                throw BusinessExceptionFactory.validationFailed(MessageID.W259);
            }
        }
    }

    /**
     * 決済機関別決済履歴テーブルのEntityKeyを生成する。
     * 
     * @param dto B1601W01UpdateDto
     * @return S09T002TEntityKey
     */
    private S09T002TEntityKey createS09T002TEntityKey(B1601W01UpdateDto dto) {

        S09T002TEntityKey entityKey = new S09T002TEntityKey();
        entityKey.setApkihoncd(dto.getApkihoncd());
        entityKey.setShopcd(dto.getShopcd());
        entityKey.setKsikkncd(dto.getFurikaekkncd());
        entityKey.setShimedydm(dto.getShimedydm());
        entityKey.setShimedd(dto.getShimedd());

        return entityKey;
    }

    /**
     * 決済履歴テーブル更新処理
     * 
     * @param dto B1601W01UpdateDto
     * 
     */
    private void doUpdateS09T001T(B1601W01UpdateDto dto) {

        // 更新件数
        int result = 0;

        // 更新処理の実行
        try {
            // システム日時を設定
            dto.setSystemDate(DateTimeUtil.changeFormatToSystemDate(LocalDateTime.now()));
            dto.setSystemTime(DateTimeUtil.changeFormatToSystemTime(LocalDateTime.now()));

            // UPDATE実行
            result = s09t001tDao.updateByExampleSelective(createUpdateSetS09T001TEntity(dto),
                createUpdateConditionS09T001TEntity(dto));

        } catch (RuntimeException e) {
            // 実行時例外処理
            throw BusinessExceptionFactory.validationFailed(MessageID.E002);
        }

        // 更新件数が0(排他制御)の場合、エラー
        if (result == 0) {
            throw BusinessExceptionFactory.validationFailed(MessageID.W090);
        }
    }

    /**
     * 決済履歴テーブル更新時の値を設定する。
     * 
     * @param dto B1601W01UpdateDto
     * @return S09T001TEntity
     */
    private S09T001TEntity createUpdateSetS09T001TEntity(B1601W01UpdateDto dto) {

        // Entityの生成
        S09T001TEntity entity = new S09T001TEntity();

        entity.setKsikin(Long.valueOf(dto.getKsikin())); // 決済金額
        entity.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        entity.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        entity.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        entity.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        entity.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        entity.setKshnunyoymd(dto.getUnyoDate()); // 更新運用日付
        entity.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        entity.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return entity;
    }

    /**
     * 決済履歴テーブル更新時の条件を設定する。
     * 
     * @param dto B1601W01UpdateDto
     * @return S09T001TEntityExample
     * 
     */
    private S09T001TEntityExample createUpdateConditionS09T001TEntity(B1601W01UpdateDto dto) {

        // EntityExampleの生成
        S09T001TEntityExample entity = new S09T001TEntityExample();

        // クライテリア(SQL条件)の生成
        jp.co.aquaclara.accis.common.domain.repository.entity.S09T001TEntityExample.Criteria criteria =
            entity.createCriteria();
        criteria.andApkihoncdEqualTo(dto.getApkihoncd()).andShopcdEqualTo(dto.getShopcd())
            .andSkysakicdEqualTo(dto.getSkysakicd()).andShimedydmEqualTo(dto.getShimedydm())
            .andShimeddEqualTo(dto.getShimedd()).andKshnsystmymdEqualTo(dto.getKshnsystmymd())
            .andKshnsystmjikanEqualTo(dto.getKshnsystmjikan());

        return entity;
    }

    /**
     * 決済機関別決済履歴テーブル更新の為のデータを決済履歴テーブルから取得してDTOに設定する。
     * 
     * @param dto B1601W01UpdateDto
     * 
     */
    private void setIraismData(B1601W01UpdateDto dto) {

        // 決済履歴テーブルを検索
        List<S09T001TEntity> resultList =
            s09t001tDao.selectByExample(createS09T001TEntityExample(dto));

        // 依頼合計金額
        long iraismkin = 0;

        // 検索結果が空でない場合
        if (!ObjectUtils.isEmpty(resultList) && resultList.size() > 0) {
            // リスト内の決済金額を合算する
            for (S09T001TEntity entity : resultList) {
                iraismkin += entity.getKsikin();
            }

            // 依頼合計件数(決済履歴テーブルでの検索件数)の設定
            dto.setIraismcnt(resultList.size());
            // 依頼合計金額(決済履歴テーブルでの決済金額の合計値)の設定
            dto.setIraismkin(iraismkin);

            // 検索結果にデータが存在しない場合はエラーとして処理する
        } else {
            throw BusinessExceptionFactory.dataNotFound(MessageID.W083);
        }
    }

    /**
     * 決済機関別決済履歴テーブル更新時の決済履歴テーブルからの追加データ取得の為の条件を設定する。
     * 
     * @param dto B1601W01UpdateDto
     * @return S09T001TEntityExample
     * 
     */
    private S09T001TEntityExample createS09T001TEntityExample(B1601W01UpdateDto dto) {

        // EntityExampleの生成
        S09T001TEntityExample entity = new S09T001TEntityExample();

        // クライテリア(SQL条件)の生成
        jp.co.aquaclara.accis.common.domain.repository.entity.S09T001TEntityExample.Criteria criteria =
            entity.createCriteria();
        criteria.andApkihoncdEqualTo(dto.getApkihoncd()).andShopcdEqualTo(dto.getShopcd())
            .andKsikkncdEqualTo(dto.getFurikaekkncd()).andShimedydmEqualTo(dto.getShimedydm())
            .andShimeddEqualTo(dto.getShimedd());

        return entity;
    }


    /**
     * 決済機関別決済履歴テーブル更新処理
     * 
     * @param dto B1601W01UpdateDto
     * 
     */
    private void doUpdateS09T002T(B1601W01UpdateDto dto) {

        // 更新件数
        int result = 0;

        // 更新処理の実行
        try {
            // UPDATE実行
            result = s09t002tDao.updateByExampleSelective(createUpdateSetS09T002TEntity(dto),
                createUpdateConditionS09T002TEntity(dto));

        } catch (RuntimeException e) {
            // 実行時例外処理
            throw BusinessExceptionFactory.validationFailed(MessageID.E002);
        }

        // 更新件数が0(排他制御)の場合、エラー
        if (result == 0) {
            throw BusinessExceptionFactory.validationFailed(MessageID.W090);
        }
    }

    /**
     * 決済機関別決済履歴テーブル更新時の値を設定する。
     * 
     * @param dto B1601W01UpdateDto
     * @return S09T002TEntity
     */
    private S09T002TEntity createUpdateSetS09T002TEntity(B1601W01UpdateDto dto) {

        // Entityの生成
        S09T002TEntity entity = new S09T002TEntity();

        entity.setIraismcnt(dto.getIraismcnt()); // 依頼合計件数
        entity.setIraismkin(dto.getIraismkin()); // 依頼合計金額
        entity.setKoshaapkihoncd(dto.getLoginUserApkihoncd()); // 更新者ＡＰ基本コード
        entity.setKoshashopcd(dto.getLoginUserShopcd()); // 更新者ショップコード
        entity.setKoshacd(dto.getLoginUserTnshacd()); // 更新者コード
        entity.setKshnsystmymd(dto.getSystemDate()); // 更新システム日付
        entity.setKshnsystmjikan(dto.getSystemTime()); // 更新システム時間
        entity.setKshnunyoymd(dto.getUnyoDate()); // 更新運用日付
        entity.setKshnipadrse(dto.getIpAddress()); // 更新ＩＰアドレス
        entity.setKshnpgmid(dto.getProgramId()); // 更新プログラムＩＤ

        return entity;
    }

    /**
     * 決済機関別決済履歴テーブル更新時の条件を設定する。
     * 
     * @param dto B1601W01UpdateDto
     * @return S09T002TEntityExample
     * 
     */
    private S09T002TEntityExample createUpdateConditionS09T002TEntity(B1601W01UpdateDto dto) {

        // EntityExampleの生成
        S09T002TEntityExample entity = new S09T002TEntityExample();

        // クライテリア(SQL条件)の生成
        jp.co.aquaclara.accis.common.domain.repository.entity.S09T002TEntityExample.Criteria criteria =
            entity.createCriteria();
        criteria.andApkihoncdEqualTo(dto.getApkihoncd()).andShopcdEqualTo(dto.getShopcd())
            .andKsikkncdEqualTo(dto.getFurikaekkncd()).andShimedydmEqualTo(dto.getShimedydm())
            .andShimeddEqualTo(dto.getShimedd()).andKshnsystmymdEqualTo(dto.getKshnsystmymd1())
            .andKshnsystmjikanEqualTo(dto.getKshnsystmjikan1());

        return entity;
    }
}
