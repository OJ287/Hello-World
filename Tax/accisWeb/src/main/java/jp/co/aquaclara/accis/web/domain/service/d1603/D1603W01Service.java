package jp.co.aquaclara.accis.web.domain.service.d1603;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import jp.co.aquaclara.accis.common.constants.CommonConstants;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M006TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M017TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S02M025TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S07T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S09T001TDao;
import jp.co.aquaclara.accis.common.domain.repository.dao.S09T002TDao;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M006TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M006TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S02M017TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.S07T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S09T001TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S09T001TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S09T002TEntity;
import jp.co.aquaclara.accis.common.domain.repository.entity.S09T002TEntityExample;
import jp.co.aquaclara.accis.common.domain.repository.entity.S09T002TEntityKey;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S02M025TD1603W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S07T001TD1603W01Entity;
import jp.co.aquaclara.accis.common.domain.repository.entity.bp.S09T001TD1603W01Entity;
import jp.co.aquaclara.accis.common.util.DateTimeUtil;
import jp.co.aquaclara.accis.common.util.ItemUtil;
import jp.co.aquaclara.accis.web.domain.dto.d1603.D1603W01ExecDetailDto;
import jp.co.aquaclara.accis.web.domain.dto.d1603.D1603W01ExecDto;
import jp.co.aquaclara.accis.web.domain.dto.d1603.D1603W01SearchConditionDto;
import jp.co.aquaclara.accis.web.domain.dto.d1603.D1603W01SearchResultDto;
import jp.co.aquaclara.accis.web.domain.dto.d1603.D1603W01UpdateInfoDto;
import jp.co.aquaclara.accis.web.domain.service.master.S02M006TService;
import jp.co.aquaclara.accis.web.enumerate.MessageID;
import jp.co.aquaclara.accis.web.exception.BusinessExceptionFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * D1603w01_口座振替抽出処理(検索画面)サービスクラス
 * 
 * @author accisdev037
 *
 */
@Service
@Slf4j
public class D1603W01Service {

    // 決済機関マスタDao
    @Autowired
    private S02M025TDao s02m025tDao;

    // 決済機関別決済履歴テーブルDAO
    @Autowired
    private S09T002TDao s09t002tDao;

    // 決済履歴テーブルDAO
    @Autowired
    private S09T001TDao s09t001tDao;

    // 請求残高テーブルDAO
    @Autowired
    private S07T001TDao s07t001tDao;

    // 請求先マスタテーブルDAO
    @Autowired
    private S02M017TDao s02m017tdao;

    // システム管理マスタDAO
    @Autowired
    private S02M006TDao s02M006TDao;

    // システム管理マスタサービス
    @Autowired
    private S02M006TService s02m006tService;

    // チェックボックスON
    private final String CHK_ON = "1";
    // チェックボックスOFF
    private final String CHK_OFF = "0";

    /**
     * 口座振替抽出検索処理
     * 
     * @param dto 口座振替抽出検索条件DTO
     * @return List<D1604W01SearchResultDto> 口座振替抽出処理検索結果DTOリスト
     */
    public List<D1603W01SearchResultDto> search(D1603W01SearchConditionDto dto) {

        // 検索条件を生成する
        S02M025TD1603W01Entity e = createSearchEntity(dto);

        return createResultList(dto, s02m025tDao.selectByD1603W01(e));

    }


    /**
     * 検索結果をControllerへ返却する形式へ変換する。
     * 
     * @param dto 口座振替抽出検索条件DTO
     * @param searchResultList DB取得果リスト
     * @return List<D1604W01SearchResultDto> 口座振替確定処理検索結果DTOリスト
     */
    private List<D1603W01SearchResultDto> createResultList(D1603W01SearchConditionDto dto,
        List<S02M025TD1603W01Entity> searchResultList) {

        // 検索結果が0件の場合
        if (searchResultList.size() == 0) {
            throw BusinessExceptionFactory.dataNotFound(MessageID.W083);
        }

        // 運用日付取得
        String uDate = s02m006tService.searchForMoji(dto.getApkihoncd(), dto.getShopcd(),
            CommonConstants.SYS_SETTEINO_038, CommonConstants.SETTEICD_038_1);

        List<D1603W01SearchResultDto> result = new ArrayList<>();

        for (S02M025TD1603W01Entity e : searchResultList) {
            D1603W01SearchResultDto d = new D1603W01SearchResultDto();
            d.setKsikkncdmei(e.getKsikkncd()); // 決済機関コード明細
            d.setKsikknnmmei(e.getKsikknnm()); // 決済機関名称明細
            d.setShimeddmei(StringUtils.isEmpty(e.getShimedd())
                ? DateTimeUtil.getLastDate(dto.getShimedydm(), e.getShimeddn1())
                : DateTimeUtil.getLastDate(dto.getShimedydm(), e.getShimedd())); // 締日明細
            d.setDydmddmei(getFurikaeyoteiDydm(dto.getShimedydm(), e.getFurikaedydmdd(),
                e.getShimeddn1(), e.getFurikaeddn1())); // 振替予定年月明細
            d.setFurikaeddmei(StringUtils.isEmpty(e.getFurikaedydmdd())
                ? DateTimeUtil.getLastDate(d.getDydmddmei(), e.getFurikaeddn1())
                : DateTimeUtil.getLastDate(d.getDydmddmei(), e.getFurikaedydmdd().substring(6, 8))); // 振替日
            d.setFurikaeddmei1(e.getFurikaeddn1()); // 振替日１
            d.setFurikaeiraihohomei(e.getFurikaeiraihoho()); // 振替依頼方法明細
            // 決済方法区分=2(クレジット)の場合、非表示
            if (!CommonConstants.KSIHOHOKBN_2.equals(e.getKsihohokbn())
                && !CommonConstants.KSIHOHOKBN_2.equals(e.getKsihohokbn1())) {
                d.setFurikaeiraihohonmmei(e.getFurikaeiraihohonm()); // 振替依頼方法名称明細
            }
            d.setKsihohokbnmei(e.getKsihohokbn()); // 決済方法区分明細
            d.setJotaimei(e.getJotai()); // 状態明細
            d.setJotainmmei(StringUtils.isEmpty(e.getJotai()) ? "未処理" : e.getJotainm()); // 状態名明細
            d.setSentakumeiShow(getSentakumei(e.getJotai(), dto.getShimedydm(), d.getShimeddmei(),
                d.getDydmddmei(), d.getFurikaeddmei(), uDate)); // 選択表示明細
            d.setSentakumei(getSentakumei(e.getJotai(), d.isSentakumeiShow())); // 選択明細

            result.add(d);
        }
        return result;
    }

    /**
     * 振替予定年月取得処理
     *
     * @param shimedydm 画面.振替年月
     * @param furikaedydmdd 決済機関別決済履歴.振替年月日
     * @param shimeddn1 決済機関マスタ.締日１
     * @param furikaeddn1 決済機関マスタ.振替日１
     * @return 振替予定年月
     */
    private String getFurikaeyoteiDydm(String shimedydm, String furikaedydmdd, String shimeddn1,
        String furikaeddn1) {

        String ret = "";

        // 決済機関別決済履歴テーブルにデータが存在しない場合
        if (StringUtils.isEmpty(furikaedydmdd)) {
            // 締日1≧振替日1のときは、振替年月の次月、締日1＜振替日1のときは、振替年月
            ret = shimeddn1.compareTo(furikaeddn1) >= 0 ? DateTimeUtil.calcYM(shimedydm, 1)
                : shimedydm;

            // 決済機関別決済履歴テーブルにデータが存在する場合
        } else {
            // 振替年月日の年月を返却
            ret = furikaedydmdd.substring(0, 6);
        }

        return ret;
    }

    /**
     * 口座振替確定実行処理
     *
     * @param dto 口座振替確定処理実行DTO
     */
    @Transactional
    public void exec(D1603W01ExecDto dto) {

        try {
            // 口座振替抽出の更新データを取得
            List<D1603W01UpdateInfoDto> updateDataList = getUpdateInfo(dto);

            // 金融機関情報リストの要素数分処理を行う。
            for (D1603W01UpdateInfoDto updateData : updateDataList) {
                String furikaedydmdd = "";
                // 振替年月の再設定
                if (Integer.parseInt(updateData.getFurikaedd()) <= Integer
                    .parseInt(updateData.getShimedd())) {
                    // 振替年月日を翌月で設定する
                    furikaedydmdd = DateTimeUtil
                        .addMonth(updateData.getShimedydm() + updateData.getFurikaedd(), 1);
                    updateData.setFurikaedydmdd(furikaedydmdd);
                } else {
                    furikaedydmdd = updateData.getShimedydm() + updateData.getFurikaedd();
                    updateData.setFurikaedydmdd(furikaedydmdd);
                }

                // 決済履歴テーブルから処理対象となるレコードを削除
                s09t001tDao.deleteByExample(createS09T001TDeleteEntityExample(updateData));

                // 決済履歴更新リストの要素数分処理を行う。
                for (S09T001TEntity extractTargetData : updateData.getUpdateInfoList()) {
                    // 決済履歴テーブル.振替年月の再設定
                    extractTargetData.setFurikaedydmdd(furikaedydmdd);
                    // デフォルト値の設定
                    extractTargetData = setDefaultParam(extractTargetData);

                    // 決済履歴テーブルを検索
                    // ─────────────────────────────────────────
                    // ACCIS2023-7209 決済履歴テーブルの検索条件から締日を除外
                    // 締日を条件に含めると、決済機関変更により締日が変わった場合に
                    // 同一締年月に複数の履歴が登録されてしまう事象が発生する。
                    // 決済は月次単位で一意とする業務ルールに基づき、締日を除外して検索する。
                    // ─────────────────────────────────────────
                    S09T001TEntityExample existingHistoriesCondition = new S09T001TEntityExample();
                    existingHistoriesCondition.createCriteria()
                        // AP基本コード
                        .andApkihoncdEqualTo(extractTargetData.getApkihoncd())
                        // ショップコード
                        .andShopcdEqualTo(extractTargetData.getShopcd())
                        // 請求先コード
                        .andSkysakicdEqualTo(extractTargetData.getSkysakicd())
                        // 締年月
                        .andShimedydmEqualTo(extractTargetData.getShimedydm());
                    List<S09T001TEntity> existingHistories =
                        s09t001tDao.selectByExample(existingHistoriesCondition);
                    // 取得した決済履歴データごとに抽出済データの削除と決済機関別決済履歴テーブルの更新を実行
                    existingHistories.stream()
                        // 状態が1（抽出済）のデータのみにフィルタ
                        .filter(h -> CommonConstants.JOTAI_1.equals(h.getJotai()))
                        // フィルタしたデータについて、決済機関別決済履歴の更新と既存データの削除を実行
                        .forEach(history -> {
                            // ACCIS2023-7209 減算対象とする決済機関は抽出済の決済履歴データに設定されているものに変更
                            S09T002TEntity s09t002tEntity = s09t002tDao
                                .selectByPrimaryKey(createS09T002TSerchEntityKey(history));
                            // 決済機関別決済履歴の更新内容の設定
                            S09T002TEntity s09t002tUpdateEntity =
                                setUpdateEntity(s09t002tEntity, history.getKsikin(), dto);
                            // 決済機関別決済履歴テーブルの更新（減算処理）
                            // ACCIS2023-7209 減算対象とする決済機関は抽出済の決済履歴データに設定されているものに変更
                            s09t002tDao.updateByExampleSelective(s09t002tUpdateEntity,
                                createS09T002TUpdateEntityExample(history));
                            // 決済履歴テーブルのレコード削除
                            s09t001tDao.deleteByPrimaryKey(history);
                        });
                    // 請求先の決済履歴が存在しないまたは登録済の決済履歴の状態が
                    // 1（抽出済）以外のデータが存在しない場合は新しい決済履歴を登録する
                    boolean shouldRegister = existingHistories.stream()
                        .noneMatch(h -> !CommonConstants.JOTAI_1.equals(h.getJotai()));
                    if (shouldRegister) {
                        // 決済履歴テーブルに登録
                        s09t001tDao.insert(extractTargetData);
                    } else {
                        log.debug("状態が1（抽出済）以外の決済履歴が存在しているため、決済履歴登録をスキップ({}-{}-{})",
                            extractTargetData.getApkihoncd(), extractTargetData.getShopcd(),
                            extractTargetData.getSkysakicd());
                    }
                }

                // 決済機関別決済履歴テーブルを削除
                s09t002tDao.deleteByExample(createS09T002TDeleteEntityExample(updateData));
                // 依頼合計件数、依頼合計金額の取得
                S09T001TD1603W01Entity sumData =
                    s09t001tDao.selectByD1603W01(createCntCondS09T001TEntity(updateData));
                // 決済機関別決済履歴Entity設定
                S09T002TEntity s09t002tInsertEntity = setInsertEntity(updateData, sumData, dto);
                // 決済機関別決済履歴登録
                s09t002tDao.insert(s09t002tInsertEntity);

                // 請求残高テーブル更新Entity設定
                S07T001TEntity s07t001tUpdateEntity = setUpdateS07T001TEntity(updateData, dto);
                // 請求残高テーブル更新
                s07t001tDao.updateByD1603W01(s07t001tUpdateEntity);
            }
        } catch (RuntimeException e) {
            // ACCIS2023-7209 例外発生時に例外情報が握りつぶされていたので修正
            throw BusinessExceptionFactory.validationFailed(e, MessageID.E002);
        }
    }

    /**
     * 口座振替抽出情報検索条件を生成する。
     * 
     * @param dto 口座振替抽出情報検索条件DTO
     * @return S02M025TD1603W01Entity 決済機関マスタエンティティ
     */
    private S02M025TD1603W01Entity createSearchEntity(D1603W01SearchConditionDto dto) {
        // 検索条件をCustomEntityに展開する
        S02M025TD1603W01Entity e = new S02M025TD1603W01Entity();

        // AP基本コード
        e.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        e.setShopcd(dto.getShopcd());
        // 締年月
        e.setShimedydm(dto.getShimedydm());

        return e;
    }

    /**
     * 口座振替更新対象情報検索条件を生成する。
     * 
     * @param dto 口座振替抽出情報DTO
     * @return S07T001TD1603W01Entity 口座振替更新情報Entity
     */
    private S07T001TD1603W01Entity createS07T001TD1603W01Entity(D1603W01ExecDto dto,
        D1603W01ExecDetailDto detailDto) {

        // 検索条件をEntityに展開する
        S07T001TD1603W01Entity entity = new S07T001TD1603W01Entity();

        // AP基本コード
        entity.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        entity.setShopcd(dto.getShopcd());
        // 決済機関コード
        entity.setKsikkncd(detailDto.getKsikkncdmei());
        // 締年月
        entity.setShimedydm(DateTimeUtil.toNoSlashedDateYYYYMM(dto.getShimedydm()));
        // 締日
        entity.setShimedd(detailDto.getShimeddmei());
        // 支払予定年月日
        // ACCIS2023-7145 存在しない年月日の場合は月末日を指定する必要があるが、31日以外のケースも考慮した実装に修正
        // 画面.検索結果一覧エリア.振替予定年月（スラッシュを除去）
        String scheduledTransferMonthAndYear =
            DateTimeUtil.toNoSlashedDateYYYYMM(detailDto.getDydmddmei());
        // 上記振替予定年月と検索時取得.決済機関マスタ.振替日１を元に振替予定日を取得（存在しない日付になる場合は月末日とする）
        String scheduledTransferDay =
            DateTimeUtil.getLastDate(scheduledTransferMonthAndYear, detailDto.getFurikaeddmei1());
        // 振替予定年月日として文字列を連結してEntityに設定
        entity.setShrytidydmdd(scheduledTransferMonthAndYear + scheduledTransferDay);
        return entity;
    }

    /**
     * 選択明細の値を返却する。
     *
     * @param jotai 状態
     * @param sentakumeiShow 選択明細表示
     * @return ON:1 OFF:0
     */
    private String getSentakumei(String jotai, boolean sentakumeiShow) {

        // 選択明細が非表示の場合、0を返却する。
        if (!sentakumeiShow) {
            return CHK_OFF;
        }

        // 状態が1の場合、0を返却する。
        if (CommonConstants.JOTAI_1.equals(jotai)) {
            return CHK_OFF;
        }

        return CHK_ON;
    }

    /**
     * 選択明細の表示/非表示を返却する。
     *
     * @param jotai 状態
     * @param furikaeDydm 振替年月
     * @param shimedd 締日
     * @param furikaeyoteiDydm 振替予定年月
     * @param furikaedd 振替日
     * @param uDate 運用日付
     * @return true:表示 false:非表示
     */
    private boolean getSentakumei(String jotai, String furikaeDydm, String shimedd,
        String furikaeyoteiDydm, String furikaedd, String uDate) {

        boolean ret = true;

        // 状態が2以上の場合、falseを返却
        if (CommonConstants.JOTAI_2.compareTo(ItemUtil.nullToValue(jotai, "0")) <= 0) {
            ret = false;

            // 当日処理日(運用日付)より(振替年月+締日)が小さい場合、falseを返却
        } else if ((furikaeDydm + shimedd).compareTo(uDate) < 0) {
            ret = false;

            // 当日処理日(運用日付)より振替予定年月+振替日が小さい場合、falseを返却
        } else if (((!StringUtils.isEmpty(furikaeyoteiDydm) ? furikaeyoteiDydm : furikaeDydm)
            + furikaedd).compareTo(uDate) < 0) {
            ret = false;
        }

        return ret;
    }

    /**
     * 口座振替抽出の更新データを取得
     * 
     * @param dto 口座振替抽出処理実行DTO
     * @return List<D1603W01UpdateInfoDto> 金融機関情報リスト
     */
    private List<D1603W01UpdateInfoDto> getUpdateInfo(D1603W01ExecDto dto) {

        // 決済機関別決済履歴の更新内容
        List<D1603W01UpdateInfoDto> updateInfoList = new ArrayList<>();

        // 明細情報一覧の要素数分（明細情報）処理を行う。
        for (D1603W01ExecDetailDto detailDto : dto.getExecDetailDtoList()) {

            List<S09T001TEntity> s09t001tEntityList = new ArrayList<>();

            D1603W01UpdateInfoDto updateInfo = new D1603W01UpdateInfoDto();

            // 請求残高テーブルから口座振替の更新対象情報を取得する
            List<S07T001TD1603W01Entity> entryDataList =
                s07t001tDao.selectByD1603W01(createS07T001TD1603W01Entity(dto, detailDto));

            // 取得した更新対象情報の要素数分処理を行う。
            for (S07T001TD1603W01Entity entryData : entryDataList) {

                // 請求先マスタ情報を取得
                S02M017TEntity s02m017tRet =
                    s02m017tdao.selectByPrimaryKey(createS02M017EntityKey(dto, entryData));

                // 請求先情報を設定
                setSeikyusakiInfo(updateInfo, entryData.getSkysakicd(), s02m017tRet);

                // 請求先マスタ情報を設定
                S09T001TEntity s09t001tEntity =
                    setS09T001TEntity(dto, detailDto, entryData, s02m017tRet);

                s09t001tEntityList.add(s09t001tEntity);
            }

            // 決済履歴テーブルの更新内容を保存
            setUpdateInfo(updateInfo, dto, detailDto, s09t001tEntityList);

            // 金融機関情報リストに更新内容を保存
            updateInfoList.add(updateInfo);
        }

        return updateInfoList;

    }

    /**
     * 決済履歴テーブルの更新内容Entityを設定する。
     * 
     * @param dto 口座振替抽出処理実行DTO
     * @param detailDto 明細情報一覧DTO
     * @param entryData 口座振替更新情報Entity
     * @param s02m017tRet 請求先マスタ情報Entity
     * @return S09T001TEntity 決済履歴テーブルの更新内容Entity
     */
    private S09T001TEntity setS09T001TEntity(D1603W01ExecDto dto, D1603W01ExecDetailDto detailDto,
        S07T001TD1603W01Entity entryData, S02M017TEntity s02m017tRet) {

        S09T001TEntity entity = new S09T001TEntity();

        entity.setApkihoncd(dto.getApkihoncd());
        entity.setShopcd(dto.getShopcd());
        entity.setSkysakicd(entryData.getSkysakicd());
        entity.setShimedydm(DateTimeUtil.toNoSlashedDateYYYYMM(dto.getShimedydm()));
        entity.setShimedd(detailDto.getShimeddmei());
        entity.setKsihohokbn(detailDto.getKsihohokbnmei());
        entity.setJotai(CommonConstants.JOTAI_1);
        entity.setFurikaedydmdd(DateTimeUtil.toNoSlashedDateYYYYMM(detailDto.getDydmddmei())
            + detailDto.getFurikaeddmei());
        entity.setKsikekka(null);
        if (CommonConstants.SKYHOHOKBN_ZANDAKA.equals(s02m017tRet.getSkyhohokbn())) {
            // 口座振替更新対象情報.請求方法区分=1(残高請求)の場合
            if ("1".equals(getTransferMoneyControl(dto.getApkihoncd(), dto.getShopcd()))) {
                // 振替金額締後入金制御=1(する)の場合
                // 取得した口座振替更新対象情報.繰越金額_合計 ― 取得した口座振替更新対象情報.「入金金額＋値引金額＋手数料」
                entity.setKsikin(nullToLong0(entryData.getKurikoshikin_sum())
                    - nullToLong0(entryData.getKin3mei()));
            } else {
                // 取得した口座振替更新対象情報.繰越金額_合計
                entity.setKsikin(entryData.getKurikoshikin_sum());
            }
        } else if (CommonConstants.SKYHOHOKBN_TOUGETSU.equals(s02m017tRet.getSkyhohokbn())) {
            // 口座振替更新対象情報.請求方法区分=2(当月請求)の場合
            // 取得した口座振替更新対象情報.今回税抜売上金額_合計 + 取得した口座振替更新対象情報.今回消費税金額_合計
            entity.setKsikin(nullToLong0(entryData.getKonkaitaxnukiurikin_sum())
                + nullToLong0(entryData.getKonkaishohitaxkin_sum()));
        }
        entity.setKsikkncd(s02m017tRet.getKsikkncd());
        if (CommonConstants.KAISHUHOHOKBN_5.equals(entryData.getKaishuhohokbn())) {
            entity.setKinyukkncd(s02m017tRet.getKinyukkncd());
        }
        if (CommonConstants.KAISHUHOHOKBN_5.equals(entryData.getKaishuhohokbn())) {
            entity.setKinyukknshitencd(s02m017tRet.getKinyukknshitencd());
        }
        if (CommonConstants.KAISHUHOHOKBN_5.equals(entryData.getKaishuhohokbn())) {
            entity.setKinyukknshitennmkana(s02m017tRet.getKinyukknshitennmkana());
        }
        if (CommonConstants.KAISHUHOHOKBN_5.equals(entryData.getKaishuhohokbn())
            || CommonConstants.KAISHUHOHOKBN_2.equals(entryData.getKaishuhohokbn())) {
            entity.setKinyukknnmkana(s02m017tRet.getKinyukknnmkana());
        }
        if (CommonConstants.KAISHUHOHOKBN_5.equals(entryData.getKaishuhohokbn())) {
            entity.setKzkind(s02m017tRet.getKinyukknkzkind());
        }
        if (CommonConstants.KAISHUHOHOKBN_5.equals(entryData.getKaishuhohokbn())) {
            entity.setKesshano(s02m017tRet.getKinyukknkzno());
        } else if (CommonConstants.KAISHUHOHOKBN_2.equals(entryData.getKaishuhohokbn())) {
            entity.setKesshano(s02m017tRet.getCrdtcrdno());
        }
        if (CommonConstants.KAISHUHOHOKBN_5.equals(entryData.getKaishuhohokbn())) {
            entity.setKesshameigi(
                ItemUtil.substringBytes(s02m017tRet.getKinyukknkzmeigikana(), 30, "Windows-31J"));
        } else if (CommonConstants.KAISHUHOHOKBN_2.equals(entryData.getKaishuhohokbn())) {
            entity.setKesshameigi(
                ItemUtil.substringBytes(s02m017tRet.getCrdtcrdmeigi(), 30, "Windows-31J"));
        }
        if (CommonConstants.KAISHUHOHOKBN_5.equals(entryData.getKaishuhohokbn())) {
            entity.setZengincstcd(s02m017tRet.getZengincstcd());
        }
        if (CommonConstants.KAISHUHOHOKBN_2.equals(entryData.getKaishuhohokbn())) {
            entity.setCrdtcrdyukokgn(s02m017tRet.getCrdtcrdyukokgn());
        }
        entity.setSkyshimedydm(StringUtils.isEmpty(entryData.getSkyshimedydm()) ? ""
            : entryData.getSkyshimedydm().substring(0, 6));
        entity.setSkyshimedd(StringUtils.isEmpty(entryData.getSkyshimedydm()) ? ""
            : entryData.getSkyshimedydm().substring(6, 8));
        entity.setYukokbn(CommonConstants.YUKOKBN_1);
        entity.setToshaapkihoncd(dto.getLoginUserApkihoncd());
        entity.setToshashopcd(dto.getLoginUserShopcd());
        entity.setToshacd(dto.getLoginUserTnshacd());
        entity.setInptsystmymd(dto.getSystemDate());
        entity.setInptsystmjikan(dto.getSystemTime());
        entity.setInptunyoymd(dto.getUnyoDate());
        entity.setInptipadrse(dto.getIpAddress());
        entity.setInptpgmid(dto.getProgramId());
        entity.setKoshaapkihoncd(dto.getLoginUserApkihoncd());
        entity.setKoshashopcd(dto.getLoginUserShopcd());
        entity.setKoshacd(dto.getLoginUserTnshacd());
        entity.setKshnsystmymd(dto.getSystemDate());
        entity.setKshnsystmjikan(dto.getSystemTime());
        entity.setKshnunyoymd(dto.getUnyoDate());
        entity.setKshnipadrse(dto.getIpAddress());
        entity.setKshnpgmid(dto.getProgramId());

        return entity;
    }

    /**
     * 振替金額締後入金制御の取得
     * 
     * @param apkihoncd AP基本コード
     * @param shopcd ショップコード
     * @return 振替金額締後入金制御値(システム管理マスタの対象レコードなしの場合は"0"を返却)
     */
    private String getTransferMoneyControl(String apkihoncd, String shopcd) {
        // デフォルト値として"0"を設定
        String result = "0";
        // システム管理マスタから振替金額締後入金制御を取得
        S02M006TEntityKey entityKeyM = new S02M006TEntityKey();
        entityKeyM.setApkihoncd(apkihoncd);;
        entityKeyM.setShopcd(shopcd);
        entityKeyM.setSetteino(CommonConstants.SYS_SETTEINO_906);
        entityKeyM.setRcrdkbn(CommonConstants.RECORD_KBN_M);
        entityKeyM.setSetteicd(CommonConstants.SETTEICD_906_1);
        S02M006TEntity s02m006tEntityM = s02M006TDao.selectByPrimaryKey(entityKeyM);
        if (s02m006tEntityM != null) {
            if (!ObjectUtils.isEmpty(s02m006tEntityM.getSuchisetteiatai())) {
                // 振替金額締後入金制御として、システム管理マスタ.数値設定値を設定
                result = s02m006tEntityM.getSuchisetteiatai().toString();
            }
        }
        return result;
    }

    /**
     * 対象値がNULLの場合は0を返す.
     * 
     * @param target 対象値
     * @return 変換値
     */
    private Long nullToLong0(Long target) {
        if (target == null) {
            return 0L;
        }
        return target;
    }

    /**
     * 請求先マスタ情報テーブルの検索条件を設定
     * 
     * @param dto 口座振替抽出処理実行DTO
     * @param entryData 口座振替更新情報Entity
     * @return S02M017TEntityKey 請求先マスタ情報テーブルのEntityKey
     */
    private S02M017TEntityKey createS02M017EntityKey(D1603W01ExecDto dto,
        S07T001TD1603W01Entity entryData) {

        // 検索条件をEntityに展開する
        S02M017TEntityKey entityKey = new S02M017TEntityKey();

        // AP基本コード
        entityKey.setApkihoncd(dto.getApkihoncd());
        // ショップコード
        entityKey.setShopcd(dto.getShopcd());
        // 請求先コード
        entityKey.setSkysakicd(entryData.getSkysakicd());

        return entityKey;
    }

    /**
     * 金融機関情報リストに請求先情報を設定する。
     * 
     * @param updateInfo 決済機関別決済履歴の更新内容Dto
     * @param seikyusakicd 請求先コード
     * @param s02m017tRet 請求先マスタ情報Entity
     * @return D1603W01UpdateInfoDto 決済機関別決済履歴の更新内容Dto
     */
    private D1603W01UpdateInfoDto setSeikyusakiInfo(D1603W01UpdateInfoDto updateInfo,
        String seikyusakicd, S02M017TEntity s02m017tRet) {

        // 請求先コード
        updateInfo.setSkysakicd(seikyusakicd);
        // 決済機関コード
        updateInfo.setKsikkncd(s02m017tRet.getKsikkncd());
        // 金融機関コード
        updateInfo.setKinyukkncd(s02m017tRet.getKinyukkncd());
        // 金融機関支店コード
        updateInfo.setKinyukknshitencd(s02m017tRet.getKinyukknshitencd());
        // 金融機関支店名称カナ
        updateInfo.setKinyukknshitennmkana(s02m017tRet.getKinyukknshitennmkana());
        // 金融機関名称カナ
        updateInfo.setKinyukknnmkana(s02m017tRet.getKinyukknnmkana());
        // 口座種別
        updateInfo.setKzkind(s02m017tRet.getKinyukknkzkind());
        // 決済者番号
        updateInfo.setKesshano(s02m017tRet.getKinyukknkzno());
        // 決済者名義
        updateInfo.setKesshameigi(s02m017tRet.getKinyukknkzmeigi());

        return updateInfo;

    }

    /**
     * 金融機関情報リストに決済履歴テーブルの更新内容を設定する。
     * 
     * @param updateInfo 決済機関別決済履歴の更新内容Dto
     * @param dto 口座振替抽出処理実行DTO
     * @param detailDto 明細情報一覧DTO
     * @param s09t001tEntityList 決済履歴テーブルの更新内容
     * @return D1603W01UpdateInfoDto 決済機関別決済履歴の更新内容Dto
     */
    private D1603W01UpdateInfoDto setUpdateInfo(D1603W01UpdateInfoDto updateInfo,
        D1603W01ExecDto dto, D1603W01ExecDetailDto detailDto,
        List<S09T001TEntity> s09t001tEntityList) {

        // AP基本コード
        updateInfo.setApkihonCd(dto.getApkihoncd());
        // ショップコード
        updateInfo.setShopCd(dto.getShopcd());
        // 締年月
        updateInfo.setShimedydm(DateTimeUtil.toNoSlashedDateYYYYMM(dto.getShimedydm()));
        // 締日
        updateInfo.setShimedd(detailDto.getShimeddmei());
        // 決済機関コード
        updateInfo.setKsikkncd(detailDto.getKsikkncdmei());
        // 振替予定年月
        updateInfo.setDydmdd(detailDto.getDydmddmei());
        // 振替日
        updateInfo.setFurikaedd(detailDto.getFurikaeddmei());
        // 振替日１
        updateInfo.setFurikaedd1(detailDto.getFurikaeddmei1());
        // 振替依頼方法
        updateInfo.setFurikaeiraihoho(detailDto.getFurikaeiraihohomei());
        // 決済方法
        updateInfo.setKsihoho(detailDto.getKsihohokbnmei());
        // 状態
        updateInfo.setJotai(detailDto.getJotaimei());
        // 決済履歴テーブル更新リスト
        updateInfo.setUpdateInfoList(s09t001tEntityList);

        return updateInfo;

    }

    /**
     * 決済履歴テーブルの削除条件を設定する
     * 
     * @param updateInfo 決済機関別決済履歴の更新内容Dto
     * @return S09T001TEntityExample 決済履歴テーブルの削除条件EntityExample
     */
    private S09T001TEntityExample createS09T001TDeleteEntityExample(
        D1603W01UpdateInfoDto updateData) {

        // クライテリア(SQL条件)の生成
        S09T001TEntityExample entity = new S09T001TEntityExample();

        entity.createCriteria().andApkihoncdEqualTo(updateData.getApkihonCd())
            .andShopcdEqualTo(updateData.getShopCd()).andKsikkncdEqualTo(updateData.getKsikkncd())
            .andShimedydmEqualTo(updateData.getShimedydm())
            .andShimeddEqualTo(updateData.getShimedd());

        return entity;
    }

    /**
     * 決済機関別決済履歴テーブルの削除条件を設定する
     * 
     * @param updateData 決済機関別決済履歴の更新内容Dto
     * @return S09T002TEntityExample 決済機関別決済履歴テーブルの削除条件EntityExample
     */
    private S09T002TEntityExample createS09T002TDeleteEntityExample(
        D1603W01UpdateInfoDto updateData) {

        // クライテリア(SQL条件)の生成
        S09T002TEntityExample entity = new S09T002TEntityExample();

        entity.createCriteria().andApkihoncdEqualTo(updateData.getApkihonCd())
            .andShopcdEqualTo(updateData.getShopCd()).andKsikkncdEqualTo(updateData.getKsikkncd())
            .andShimedydmEqualTo(updateData.getShimedydm())
            .andShimeddEqualTo(updateData.getShimedd());

        return entity;
    }

    /**
     * 決済機関別決済履歴テーブルの更新内容Entityを設定する。
     * 
     * @param ksikin 依頼金額
     * @param dto 明細情報一覧DTO
     * @return updateEntity 決済機関別決済履歴テーブルの更新内容Entity
     */
    private S09T002TEntity setUpdateEntity(S09T002TEntity entity, Long ksikin,
        D1603W01ExecDto dto) {

        S09T002TEntity updateEntity = new S09T002TEntity();

        // 依頼件数
        updateEntity.setIraismcnt(entity.getIraismcnt() - 1);
        // 依頼金額
        updateEntity.setIraismkin(entity.getIraismkin() - ksikin);
        // 更新者ＡＰ基本コード
        updateEntity.setKoshaapkihoncd(dto.getLoginUserApkihoncd());
        // 更新者ショップコード
        updateEntity.setKoshashopcd(dto.getLoginUserShopcd());
        // 更新者コード
        updateEntity.setKoshacd(dto.getLoginUserTnshacd());
        // 更新システム日付
        updateEntity.setKshnsystmymd(dto.getSystemDate());
        // 更新システム時間
        updateEntity.setKshnsystmjikan(dto.getSystemTime());
        // 更新運用日付
        updateEntity.setKshnunyoymd(dto.getUnyoDate());
        // 更新ＩＰアドレス
        updateEntity.setKshnipadrse(dto.getIpAddress());
        // 更新プログラムＩＤ
        updateEntity.setKshnpgmid(dto.getProgramId());

        return updateEntity;
    }

    /**
     * 決済機関別決済履歴テーブルの更新条件Entityを設定する。
     * 
     * <p>
     * ACCIS2023-7209 減算対象とする決済機関は抽出済の決済履歴データに設定されているものに変更
     * </p>
     * 
     * @param updateData 決済機関別決済履歴の更新内容Dto
     * @return updateEntity 決済機関別決済履歴テーブルの更新条件Entity
     */
    private S09T002TEntityExample createS09T002TUpdateEntityExample(S09T001TEntity updateData) {

        // クライテリア(SQL条件)の生成
        S09T002TEntityExample updateEntityExample = new S09T002TEntityExample();
        updateEntityExample.createCriteria() // 決済機関別決済履歴データに対する減算の更新条件
            .andApkihoncdEqualTo(updateData.getApkihoncd()) // AP基本コード
            .andShopcdEqualTo(updateData.getShopcd()) // ショップコード
            .andKsikkncdEqualTo(updateData.getKsikkncd()) // 決済機関コード
            .andShimedydmEqualTo(updateData.getShimedydm()) // 締年月
            .andShimeddEqualTo(updateData.getShimedd()); // 締日

        return updateEntityExample;
    }

    /**
     * 抽出済決済履歴データを元に決済機関別決済履歴テーブルの主キーEntityを生成する
     * 
     * <p>
     * ACCIS2023-7209 減算対象とする決済機関は抽出済の決済履歴データに設定されているものに変更
     * </p>
     * 
     * @param draftedPaymentHistoryForTransfer 抽出済の決済履歴データ
     * @return 決済機関別決済履歴テーブル検索条件となる主キーEntity
     */
    private S09T002TEntityKey createS09T002TSerchEntityKey(
        S09T001TEntity draftedPaymentHistoryForTransfer) {

        S09T002TEntityKey key = new S09T002TEntityKey();
        // AP基本コード
        key.setApkihoncd(draftedPaymentHistoryForTransfer.getApkihoncd());
        // ショップコード
        key.setShopcd(draftedPaymentHistoryForTransfer.getShopcd());
        // 決済機関コード
        key.setKsikkncd(draftedPaymentHistoryForTransfer.getKsikkncd());
        // 締年月
        key.setShimedydm(draftedPaymentHistoryForTransfer.getShimedydm());
        // 締日
        key.setShimedd(draftedPaymentHistoryForTransfer.getShimedd());
        return key;
    }

    /**
     * 依頼合計件数、依頼合計金額の取得条件を設定する。
     * 
     * @param dto 口座振替確定処理実行DTO
     * @param detailDto 口座振替確定処理明細DTO
     * @return S09T001TEntityExample 決済履歴テーブル条件エンティティ
     */
    private S09T001TEntityExample createCntCondS09T001TEntity(D1603W01UpdateInfoDto updateData) {

        S09T001TEntityExample entity = new S09T001TEntityExample();

        // クライテリア(SQL条件)の生成
        entity.createCriteria().andApkihoncdEqualTo(updateData.getApkihonCd())
            .andShopcdEqualTo(updateData.getShopCd()).andKsikkncdEqualTo(updateData.getKsikkncd())
            .andShimedydmEqualTo(updateData.getShimedydm())
            .andShimeddEqualTo(updateData.getShimedd()).andKsikinGreaterThan(0L);

        return entity;
    }

    /**
     * 決済履歴テーブルの登録内容デフォルト値を設定する
     * 
     * @param entity 決済履歴テーブルの登録内容Entity
     * @return S09T001TEntity 決済履歴テーブルの登録内容（デフォルト値）
     */
    private S09T001TEntity setDefaultParam(S09T001TEntity entity) {

        if (StringUtils.isEmpty(entity.getKinyukkncd())) {
            entity.setKinyukkncd("0000");
        }
        if (StringUtils.isEmpty(entity.getKinyukknshitencd())) {
            entity.setKinyukknshitencd("000");
        }
        if (StringUtils.isEmpty(entity.getKinyukknshitennmkana())) {
            entity.setKinyukknshitennmkana(" ");
        }
        if (StringUtils.isEmpty(entity.getKinyukknnmkana())) {
            entity.setKinyukknnmkana(" ");
        }
        if (StringUtils.isEmpty(entity.getKzkind())) {
            entity.setKzkind("1");
        }
        if (StringUtils.isEmpty(entity.getKesshano())) {
            entity.setKesshano(" ");
        }
        if (StringUtils.isEmpty(entity.getKesshameigi())) {
            entity.setKesshameigi("*ﾏｽﾀﾐﾄｳﾛｸ*");
        }
        if (StringUtils.isEmpty(entity.getZengincstcd())) {
            entity.setZengincstcd(" ");
        }

        return entity;
    }

    /**
     * 決済機関別決済履歴テーブルの更新内容を設定する。
     * 
     * @param updateData 決済機関別決済履歴の更新内容Dto
     * @param sumData 依頼合計件数、依頼合計金額
     * @param dto 明細情報一覧DTO
     * @return S09T002TEntity 決済機関別決済履歴テーブル登録内容エンティティ
     */
    private S09T002TEntity setInsertEntity(D1603W01UpdateInfoDto updateData,
        S09T001TD1603W01Entity sumData, D1603W01ExecDto dto) {

        // 決済機関別決済履歴Entity設定
        S09T002TEntity insertEntity = new S09T002TEntity();

        // ＡＰ基本コード
        insertEntity.setApkihoncd(updateData.getApkihonCd());
        // ショップコード
        insertEntity.setShopcd(updateData.getShopCd());
        // 決済機関コード
        insertEntity.setKsikkncd(updateData.getKsikkncd());
        // 締年月
        insertEntity.setShimedydm(updateData.getShimedydm());
        // 締日
        insertEntity.setShimedd(updateData.getShimedd());
        // 決済方法区分
        insertEntity.setKsihohokbn(updateData.getKsihoho());
        // 状態
        insertEntity.setJotai(CommonConstants.JOTAI_1);
        // 振替日
        insertEntity.setFurikaedd(updateData.getFurikaedd());
        // 振替年月日
        insertEntity.setFurikaedydmdd(updateData.getFurikaedydmdd());
        // 振替依頼方法
        insertEntity.setFurikaeiraihoho(updateData.getFurikaeiraihoho());
        // 依頼合計件数
        insertEntity.setIraismcnt(sumData.getCnt());
        // 依頼合計金額
        insertEntity.setIraismkin(sumData.getKsikin());
        // 有効区分
        insertEntity.setYukokbn(CommonConstants.YUKOKBN_1);
        // 登録者ＡＰ基本コード
        insertEntity.setToshaapkihoncd(dto.getLoginUserApkihoncd());
        // 登録者ショップコード
        insertEntity.setToshashopcd(dto.getLoginUserShopcd());
        // 登録者コード
        insertEntity.setToshacd(dto.getLoginUserTnshacd());
        // 登録システム日付
        insertEntity.setInptsystmymd(dto.getSystemDate());
        // 登録システム時間
        insertEntity.setInptsystmjikan(dto.getSystemTime());
        // 登録運用日付
        insertEntity.setInptunyoymd(dto.getUnyoDate());
        // 登録ＩＰアドレス
        insertEntity.setInptipadrse(dto.getIpAddress());
        // 登録プログラムＩＤ
        insertEntity.setInptpgmid(dto.getProgramId());
        // 更新者ＡＰ基本コード
        insertEntity.setKoshaapkihoncd(dto.getLoginUserApkihoncd());
        // 更新者ショップコード
        insertEntity.setKoshashopcd(dto.getLoginUserShopcd());
        // 更新者コード
        insertEntity.setKoshacd(dto.getLoginUserTnshacd());
        // 更新システム日付
        insertEntity.setKshnsystmymd(dto.getSystemDate());
        // 更新システム時間
        insertEntity.setKshnsystmjikan(dto.getSystemTime());
        // 更新運用日付
        insertEntity.setKshnunyoymd(dto.getUnyoDate());
        // 更新ＩＰアドレス
        insertEntity.setKshnipadrse(dto.getIpAddress());
        // 更新プログラムＩＤ
        insertEntity.setKshnpgmid(dto.getProgramId());

        return insertEntity;
    }

    /**
     * 請求残高テーブルの更新内容を設定する。
     * 
     * @param updateData 決済機関別決済履歴の更新内容Dto
     * @param dto 明細情報一覧DTO
     * @return S07T001TEntity 請求残高テーブル更新内容エンティティ
     */
    private S07T001TEntity setUpdateS07T001TEntity(D1603W01UpdateInfoDto updateData,
        D1603W01ExecDto dto) {

        S07T001TEntity updateEntity = new S07T001TEntity();

        // ＡＰ基本コード
        updateEntity.setApkihoncd(updateData.getApkihonCd());
        // ショップコード
        updateEntity.setShopcd(updateData.getShopCd());
        // 決済機関コード
        updateEntity.setKsikkncd(updateData.getKsikkncd());
        // 決済締年月日
        updateEntity.setKsishimedydmdd(dto.getShimedydm()
            + DateTimeUtil.getLastDate(dto.getShimedydm(), updateData.getShimedd()));
        // 締年月
        updateEntity.setShimedydm(dto.getShimedydm());
        // 締日
        updateEntity.setShimedd(updateData.getShimedd());
        // 支払予定年月日
        String dydmdd = DateTimeUtil.toNoSlashedDateYYYYMM(updateData.getDydmdd());
        updateEntity
            .setShrytidydmdd(dydmdd + DateTimeUtil.getLastDate(dydmdd, updateData.getFurikaedd1()));
        // 更新者ＡＰ基本コード
        updateEntity.setKoshaapkihoncd(dto.getLoginUserApkihoncd());
        // 更新者ショップコード
        updateEntity.setKoshashopcd(dto.getLoginUserShopcd());
        // 更新者コード
        updateEntity.setKoshacd(dto.getLoginUserTnshacd());
        // 更新システム日付
        updateEntity.setKshnsystmymd(dto.getSystemDate());
        // 更新システム時間
        updateEntity.setKshnsystmjikan(dto.getSystemTime());
        // 更新運用日付
        updateEntity.setKshnunyoymd(dto.getUnyoDate());
        // 更新ＩＰアドレス
        updateEntity.setKshnipadrse(dto.getIpAddress());
        // 更新プログラムＩＤ
        updateEntity.setKshnpgmid(dto.getProgramId());

        return updateEntity;
    }
}
